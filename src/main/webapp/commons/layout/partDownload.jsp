<%@ page import=" org.jblooming.operator.Operator,
                  org.jblooming.persistence.PersistenceHome,
                  org.jblooming.remoteFile.Document,
                  org.jblooming.remoteFile.RemoteFile,
                  org.jblooming.utilities.HttpUtilities,
                  org.jblooming.utilities.StringUtilities,
                  org.jblooming.utilities.file.FileUtilities, org.jblooming.waf.view.PageState,
                  javax.mail.internet.MimeUtility, java.io.BufferedInputStream, java.io.InputStream,java.net.URLEncoder"%><%

  response.resetBuffer();
  //response.setCharacterEncoding("ISO-8859-1");
  PageState pageState = PageState.getCurrentPageState();
  Operator logged = pageState.getLoggedOperator();


  Document document = (Document) PersistenceHome.findByPrimaryKey(Document.class, pageState.mainObjectId);
  String path = pageState.getEntry("PATH").stringValueNullIfEmpty();

  RemoteFile rfs = RemoteFile.getInstance(document);
 // if (document.hasPermissionFor(logged, TeamworkPermissions.fileStorage_explorer_canRead) && rfs.exists()) {
    rfs.setTarget(path);

    response.setContentType(HttpUtilities.getContentType(rfs.getName()));

    String filename = rfs.getName();
    String filenameEncoded = pageState.sessionState.isFirefox() ? MimeUtility.encodeText(filename, "UTF8", "B") : URLEncoder.encode(filename, "UTF8");
    filenameEncoded = StringUtilities.replaceAllNoRegex(StringUtilities.replaceAllNoRegex(filenameEncoded, "+", "_"), " ", "_");
    response.setHeader("content-disposition", "attachment; filename=" + filenameEncoded); //StringUtilities.normalize(rfs.getName())

    InputStream remoteInputStream = rfs.getRemoteInputStream();
    BufferedInputStream fr = new BufferedInputStream(remoteInputStream);
    //int b;
    //while ((b = fr.read()) != -1)
    //  response.getWriter().write(b);

    // write data to stream and close it
    FileUtilities.writeStream(fr, response.getOutputStream());

    fr.close();



%>