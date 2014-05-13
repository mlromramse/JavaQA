package org.jblooming.remoteFile;

import org.jblooming.waf.view.PageSeed;
import org.jblooming.waf.view.PageState;
import org.jblooming.waf.settings.ApplicationState;
import org.jblooming.waf.constants.Fields;
import org.jblooming.waf.constants.Commands;
import org.jblooming.persistence.PersistenceHome;
import org.jblooming.persistence.exceptions.FindByPrimaryKeyException;
import org.jblooming.persistence.exceptions.PersistenceException;
import org.jblooming.tracer.Tracer;
import org.jblooming.PlatformRuntimeException;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.InputStream;
import java.io.IOException;

public class BasicDocumentBricks {
  public static Document mainObject;

  public BasicDocumentBricks(Document basicDocument) {
    mainObject = basicDocument;
  }

  //
  private static String getStringFromRemoteInputStream(InputStream input) throws IOException {
    StringBuffer str = new StringBuffer();
    int c;
    //getClient();
    //InputStream input = remoteFile.getRemoteInputStream();
    while (((c = input.read()) != -1)) {
      str.append((char) c);
    }
    input.close();
    return str.toString();

  }

  public static PageSeed getExplorerPageSeed(HttpServletRequest request, PageState pageState, RemoteFile remoteFile, Document document, String classname) {
    mainObject = document;
    PageSeed downOrExplore = null;

    if (remoteFile.isDirectory()) {
      PageSeed ps = pageState.thisPage(request);
      ps.addClientEntry(pageState.getEntry("ROOTPATH"));
      ps.mainObjectId = mainObject.getId();
      ps.setPopup(pageState.isPopup());
      ps.addClientEntry("PATH", remoteFile.getPathFromDocument());
      downOrExplore = ps;
    } else {
      if (Document.ConnectionType.SERVICE.equals(document.getConnType())) {
        try {
          String psst = getStringFromRemoteInputStream(remoteFile.getRemoteInputStream());
          if (psst != null && !psst.trim().equals("")) {
            downOrExplore = new PageSeed(psst);
          } else {
            Tracer.platformLogger.error(" Cannot get URL ");

          }
        } catch (IOException e) {
          Tracer.platformLogger.error(" Cannot get URL ", e);
        }
      } else {
        PageSeed down = pageState.pageFromCommonsRoot("layout/partDownload.jsp");
        down.mainObjectId = mainObject.getId();
        down.setCommand("DOWNLOAD");
        down.setPopup(true);
        down.addClientEntry("PATH", remoteFile.getPathFromDocument());
        downOrExplore = down;
      }
    }

    return downOrExplore;

  }

  public static PageSeed getPageSeedForContent(String docContent, String partExplorer, PageState pageState) throws PersistenceException {// add - graziella 3/10/08
    return getPageSeedForContent(docContent, partExplorer, pageState, false);
  }

  public static PageSeed getPageSeedForContent(String docContent, String partExplorer, PageState pageState, boolean openMainDir) throws FindByPrimaryKeyException {

    PageSeed downOrExplore = null;
    if (docContent == null)
      docContent = partExplorer;

    if (docContent.startsWith("RF")) {
      String string = docContent.substring(2);
      String[] valori = string.split(":");
      if (valori != null && valori.length > 0) {
        String id = valori[0];
        FileStorage docFS = (FileStorage) PersistenceHome.findByPrimaryKey(FileStorage.class, id);
        if (docFS != null) {
          String relativePath = valori[1];
          RemoteFile rf = null;
          try {
            rf = RemoteFile.getInstance(docFS);
            rf.setTarget(relativePath); //docFS.getContent() +
            // controllo per aprire la directory che contiene il file
            if (!rf.isDirectory() && openMainDir) {
              int lastIndex = relativePath.lastIndexOf("/");
              if (lastIndex != -1) {
                relativePath = relativePath.substring(0, lastIndex + 1);
                rf.setTarget(relativePath);
              }
            }
            if (!rf.exists())
              return null; // add - graziella 04/09/08
          } catch (Exception e) {
            throw new PlatformRuntimeException(e);
          }

          if (rf != null) {
            if (!rf.isDirectory()) {
              downOrExplore = new PageSeed(ApplicationState.contextPath + "/commons/layout/partDownload.jsp");
              downOrExplore.mainObjectId = docFS.getId();
              downOrExplore.setCommand("DOWNLOAD");
              downOrExplore.setPopup(true);
              downOrExplore.addClientEntry("CLASS", FileStorage.class.getName());
              downOrExplore.addClientEntry("PATH", relativePath);
            } else {
              downOrExplore = new PageSeed(partExplorer);
              downOrExplore.mainObjectId = docFS.getId();
              downOrExplore.setPopup(true);
              downOrExplore.addClientEntry("PATH", relativePath);
              downOrExplore.addClientEntry("ROOTPATH", relativePath);
            }
          }
        }
      }
    } else { // standard url string e.g.: www.twproject.com
      downOrExplore = new PageSeed(docContent);
    }
    return downOrExplore;
  }


  public PageSeed getContentSeed(PageState pageState) throws FindByPrimaryKeyException {

    PageSeed downOrExplore = null;
    String docContent = mainObject.getContent();
    RemoteFile rf = null;
    if (Document.IS_URL == mainObject.getType()) {

      if (docContent != null && !"".equals(docContent.trim())) {
        if (docContent.startsWith("RF")) {
          String string = docContent.substring(2);
          String[] valori = string.split(":");
          if (valori != null && valori.length > 0) {
            String id = valori[0];
            FileStorage docFS = (FileStorage) PersistenceHome.findByPrimaryKey(FileStorage.class, id);
            if (docFS != null) {
              String relativePath = valori[1];
              if (Document.ConnectionType.SERVICE.equals(docFS.getConnType())) {
                try {
                  rf = RemoteFile.getInstance(docFS);
                  rf.setTarget(docFS.getContent() + relativePath);
                } catch (Exception e) {
                  Tracer.platformLogger.error(" Cannot get URL ", e);
                }

              }
              if ((!new File(docFS.getContent() + relativePath).isDirectory()) ||
                      (Document.ConnectionType.SERVICE.equals(docFS.getConnType()) && rf != null && !rf.isDirectory())) {
                if (Document.ConnectionType.SERVICE.equals(docFS.getConnType())) {
                  try {
                    String psst = getStringFromRemoteInputStream(rf.getRemoteInputStream());
                    if (psst != null && !psst.trim().equals("")) {
                      downOrExplore = new PageSeed(psst);
                    } else {
                      Tracer.platformLogger.error(" Cannot get URL ");

                    }

                  } catch (Exception e) {
                    Tracer.platformLogger.error(" Cannot get URL ", e);
                  }


                } else {
                  downOrExplore = new PageSeed(ApplicationState.contextPath + "/commons/document/download.jsp");
                  downOrExplore.mainObjectId = docFS.getId();
                  downOrExplore.setCommand("DOWNLOAD");
                  downOrExplore.setPopup(true);
                  downOrExplore.addClientEntry("PATH", relativePath);
                }
              } else {
                downOrExplore = new PageSeed(ApplicationState.contextPath + "/commons/document/explorer.jsp");
                downOrExplore.mainObjectId = docFS.getId();
                downOrExplore.setPopup(true);
                downOrExplore.addClientEntry("PATH", relativePath);
                downOrExplore.addClientEntry("ROOTPATH", relativePath);
              }
            }
          }
        } else {
          downOrExplore = new PageSeed(docContent);
        }
      }
    } else if (BasicDocument.IS_UPLOAD == mainObject.getType()) {

      if (mainObject.getFile() != null) {
        String uplUID = mainObject.getFile().serialize();
        downOrExplore = new PageSeed(ApplicationState.contextPath + "/commons/layout/partUploaderView.jsp");
        downOrExplore.setCommand(Commands.FILE_VIEW);
        downOrExplore.addClientEntry(Fields.FILE_TO_UPLOAD, uplUID);
        downOrExplore.addClientEntry("TREATASATTACH", Fields.TRUE);
      }


    } else if (BasicDocument.IS_CONTENT == mainObject.getType()) {
      downOrExplore = new PageSeed(ApplicationState.contextPath + "/commons/document/documentContentViewer.jsp");
      downOrExplore.mainObjectId = mainObject.getId();
      downOrExplore.setCommand(Commands.EDIT);
    }
    return downOrExplore;
  }


}
