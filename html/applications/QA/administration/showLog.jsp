<%@ page import="org.jblooming.utilities.HttpUtilities,
                 org.jblooming.utilities.JSP,
                 org.jblooming.waf.view.PageState,
                 java.io.*,
                 java.util.zip.ZipEntry, java.util.zip.ZipOutputStream, org.jblooming.waf.settings.PlatformConfiguration" %><%!

  public void zipRemoteFile(String log, File file, ZipOutputStream zipout) throws IOException {


    ZipEntry zipEntry = new ZipEntry(log);
    zipout.putNextEntry(zipEntry);
    InputStream remoteInputStream = new FileInputStream(file);
    BufferedInputStream fr = new BufferedInputStream(remoteInputStream);
    int b;
    byte[] buf = new byte[1024];
    int len;
    while ((len = fr.read(buf)) > 0) {
      zipout.write(buf, 0, len);
    }
    fr.close();
    zipout.closeEntry();
    remoteInputStream.close();

  }
%><%


  PageState pageState = PageState.getCurrentPageState();

  pageState.getLoggedOperator().testIsAdministrator();

  String log = pageState.getEntry("LOG").stringValueNullIfEmpty();



    String command = pageState.getCommand();
    if ("zip".equals(command)) {

/*
________________________________________________________________________________________________________________________________________________________________________


zip

________________________________________________________________________________________________________________________________________________________________________

*/
      response.setContentType("application/zip");
      response.setHeader("Content-Disposition", "attachment; filename=\"" + log + ".zip\"");

      //get selected files
      ZipOutputStream zipout = new ZipOutputStream(response.getOutputStream());
      zipout.setComment("Teamwork FileStorage Service");
      zipRemoteFile(log, new File(log), zipout);

      try {
        zipout.finish();
        response.getWriter().flush();
      } catch (java.util.zip.ZipException e) {
      }
/*
________________________________________________________________________________________________________________________________________________________________________


show

________________________________________________________________________________________________________________________________________________________________________

*/
    } else {

      response.resetBuffer();
      response.setCharacterEncoding("ISO-8859-1");
      %><html><big><%=log%>:</big><br><script>window.focus();</script><pre><%
      //response.setContentType(HttpUtilities.getContentType(pllog.getName()));
      //response.setHeader("content-disposition", "attachment; filename=" + pllog.getName());
      InputStream remoteInputStream = new FileInputStream(new File(log));
      BufferedInputStream fr = new BufferedInputStream(remoteInputStream);
      int b;
      while ((b = fr.read()) != -1) response.getWriter().write(b);
      fr.close();
      out.write("</pre></html>");
  }

%>