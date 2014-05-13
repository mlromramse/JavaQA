package org.jblooming.remoteFile.businessLogic;

import org.jblooming.http.ZipServe;
import org.jblooming.persistence.PersistenceHome;
import org.jblooming.persistence.exceptions.FindByPrimaryKeyException;
import org.jblooming.persistence.exceptions.PersistenceException;
import org.jblooming.remoteFile.Document;
import org.jblooming.remoteFile.RemoteFile;
import org.jblooming.tracer.Tracer;
import org.jblooming.utilities.HttpUtilities;
import org.jblooming.utilities.JSP;
import org.jblooming.waf.UploadHelper;
import org.jblooming.waf.constants.FieldErrorConstants;
import org.jblooming.waf.constants.Fields;
import org.jblooming.waf.settings.I18n;
import org.jblooming.waf.view.PageState;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;


/**
 * @author Pietro Polsinelli ppolsinelli@open-lab.com
 * @author Roberto Bicchierai rbicchierai@open-lab.com
 */
public class ExplorerAction {

  public void cmdZip(HttpServletRequest request, HttpServletResponse response, PageState pageState) throws PersistenceException, IOException {

    Document document = (Document) PersistenceHome.findByPrimaryKey(Document.class, pageState.mainObjectId);
    response.setContentType("application/zip");

    RemoteFile rfs = RemoteFile.getInstance(document);
    String path = JSP.w(pageState.getEntry("PATH").stringValueNullIfEmpty());
    rfs.setTarget(path);

    //get selected files
    Set<String> selFiles = pageState.getClientEntries().getEntriesStartingWithStripped("FILE_", Fields.TRUE).keySet();
    String zipName = document.getName();
    if (selFiles.size() == 1)
      zipName = selFiles.iterator().next();

    response.setHeader("Content-Disposition", "attachment; filename=\"" + zipName + ".zip\"");
    ZipOutputStream zipout = new ZipOutputStream(response.getOutputStream());
    zipout.setComment("File Storage Service");

    for (String fileName : selFiles) {

      rfs.setTarget(path + File.separator + fileName);
      if (rfs.isDirectory()) {
        List<RemoteFile> lrf = rfs.expandFileList();
        for (RemoteFile foundRF : lrf) {
          if (!foundRF.isDirectory())
            zipRemoteFile(foundRF, path, zipout);
        }
      } else
        zipRemoteFile(rfs, path, zipout);
    }

    try {
      zipout.finish();
      //response.getWriter().flush();
    } catch (java.util.zip.ZipException e) {
      Tracer.platformLogger.error(e);
    }

  }

  public void cmdDelete(PageState pageState) throws PersistenceException, IOException {

    Document document = (Document) PersistenceHome.findByPrimaryKey(Document.class, pageState.mainObjectId);

    RemoteFile rfs = RemoteFile.getInstance(document);
    String path = JSP.w(pageState.getEntry("PATH").stringValueNullIfEmpty());
    rfs.setTarget(path);

    //get selected files
    Set<String> selFiles = pageState.getClientEntries().getEntriesStartingWithStripped("FILE_", Fields.TRUE).keySet();

    for (String fileName : selFiles) {
      rfs.setTarget(path + File.separator + fileName);
      rfs.delete();
    }

  }


  private void zipRemoteFile(RemoteFile foundRF, String currentPath, ZipOutputStream zipout) throws IOException {
    String fileDir = foundRF.getPathFromDocument().substring(currentPath.length() + File.separator.length());
    ZipEntry zipEntry = new ZipEntry(fileDir);
    zipout.putNextEntry(zipEntry);
    InputStream remoteInputStream = foundRF.getRemoteInputStream();
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

  public void mkdir(PageState pageState) throws FindByPrimaryKeyException {
    Document document = (Document) PersistenceHome.findByPrimaryKey(Document.class, pageState.mainObjectId);
    try {
      String dirName = pageState.getEntryAndSetRequired("DIR_NAME").stringValueNullIfEmpty();
      if (dirName != null) {
        RemoteFile rfs = RemoteFile.getInstance(document);
        String path = JSP.w(pageState.getEntry("PATH").stringValueNullIfEmpty());
        if (dirName.indexOf("/") != -1) {
          pageState.getEntry("DIR_NAME").errorCode = I18n.get("PATH_NOT_ALLOWED");
        }
        /*else{
         rfs.setTarget(path + File.separator + dirName);
         rfs.mkdirs();
        }*/
        rfs.setTarget(path + File.separator + dirName);
        rfs.mkdirs();
      }
    } catch (Exception e) {
      pageState.getEntry("DIR_NAME").errorCode = e.getMessage();
    }
  }

  public void upload(PageState pageState, HttpServletRequest request ) throws PersistenceException, IOException {

    Document document = (Document) PersistenceHome.findByPrimaryKey(Document.class, pageState.mainObjectId);
    UploadHelper uh = UploadHelper.getInstance("UPLOAD_HERE", pageState);
    String path = pageState.getEntry("PATH").stringValueNullIfEmpty();
    String fileName = pageState.getEntryAndSetRequired("UPLOAD_HERE").stringValueNullIfEmpty();
    if (fileName != null) {
      boolean unzip = pageState.getEntry("CHECK_UNZIP").checkFieldValue();
      if (HttpUtilities.getContentType(fileName).equals("application/zip") && unzip)
        unzipFile(uh.temporaryFileName, pageState, document, request);
      else {
        RemoteFile rfs = RemoteFile.getInstance(document);
        rfs.setTarget((path != null ? path + File.separator : "") + uh.originalFileName);
        //rfs.setTarget(path + uh.originalFileName);
        try {
          boolean overwrite = pageState.getEntry("CHECK_OVERWRITE").checkFieldValue();
          boolean isUpload = rfs.upload(uh, overwrite);
          if(!isUpload)
          pageState.getEntry("UPLOAD_HERE").errorCode = FieldErrorConstants.ERR_NAME_USED;
        }
        catch (Throwable e) {
          pageState.getEntry("UPLOAD_HERE").errorCode = FieldErrorConstants.ERR_NAME_USED;
          Tracer.platformLogger.error(e);
        }
      }
    } else {
      pageState.getEntry("UPLOAD_HERE").errorCode = FieldErrorConstants.ERR_FIELD_CANT_BE_EMPTY;
    }

  }

  private void unzipFile(String temporaryZipFileName, PageState pageState, Document document, HttpServletRequest request) throws PersistenceException {
    Enumeration entries;
    ZipFile zipFile;
    boolean overwrite = pageState.getEntry("CHECK_OVERWRITE").checkFieldValue();
    try {
      zipFile = new ZipFile(temporaryZipFileName);
      if((!dirsExist( pageState, zipFile.entries()) && !overwrite) || overwrite){
        entries = zipFile.entries();

      while (entries.hasMoreElements()) {
        ZipEntry entry = (ZipEntry) entries.nextElement();
        if (entry.isDirectory()) {
          mkdir(pageState, entry.getName());
          continue;
        }
        String path = pageState.getEntry("PATH").stringValueNullIfEmpty();
        File tempFile = File.createTempFile("formTW_TEMP_FILE", ".tmp", ZipServe.getTempFolder(request));
        tempFile.deleteOnExit();
        String temporaryFileName = tempFile.getName();
        UploadHelper uh = UploadHelper.getInstance(entry.getName(), temporaryFileName, null, zipFile.getInputStream(entry)); 
        RemoteFile rfs = RemoteFile.getInstance(document);
        rfs.setTarget((path != null ? path + File.separator : "") + entry.getName());

        try {
          rfs.upload(uh);
        }
        catch (Exception e) {
          pageState.getEntry("UPLOAD_HERE").errorCode = FieldErrorConstants.ERR_NAME_USED;
        }
      }
      }else{
        pageState.getEntry("UPLOAD_HERE").errorCode = FieldErrorConstants.ERR_NAME_USED;
      }
      zipFile.close();

    } catch (IOException e) {
      Tracer.platformLogger.error("Unhandled exception:",e);
    }

  }

  private void mkdir(PageState pageState, String dirName) throws FindByPrimaryKeyException {

    Document document = (Document) PersistenceHome.findByPrimaryKey(Document.class, pageState.mainObjectId);
    try {
      if (dirName != null) {
        RemoteFile remoteFile = RemoteFile.getInstance(document);
        String path = JSP.w(pageState.getEntry("PATH").stringValueNullIfEmpty());
        remoteFile.setTarget(path + File.separator + dirName);
        remoteFile.mkdirs();
      }
    } catch (Exception e) {

    }
  }

  private boolean dirsExist(PageState pageState, Enumeration<? extends ZipEntry> entries) throws PersistenceException, IOException {

    Document document = (Document) PersistenceHome.findByPrimaryKey(Document.class, pageState.mainObjectId);
    String path = pageState.getEntry("PATH").stringValueNullIfEmpty();
    RemoteFile rfs = RemoteFile.getInstance(document);
    rfs.setTarget(path);
    List<RemoteFile> files = rfs.listFiles();
    try {
      while (entries.hasMoreElements()) {
        ZipEntry entry = entries.nextElement();
        String pathToCheck1 = document.getContent() + path + "/" + entry.getName();
        String pathToCheck2 = document.getContent() + path + "/" +
                (entry.getName().indexOf("/") != -1 ? entry.getName().substring(0, entry.getName().indexOf("/")) : entry.getName());
        for (RemoteFile file : files) {
          String relPath = file.getRelativePath().replaceAll("\\\\", "/");
           if (relPath.equals(pathToCheck1.replaceAll("\\\\", "/")) || relPath.equals(pathToCheck2.replaceAll("\\\\", "/")))
            return true;
        }
      }
    } catch (Exception e) {
      return false;
    }
    return false;
  }

}
