package org.jblooming.waf;

import org.jblooming.http.multipartfilter.MultipartFormRequestEncodingFilter;
import org.jblooming.operator.Operator;
import org.jblooming.system.SystemConstants;
import org.jblooming.utilities.HttpUtilities;
import org.jblooming.utilities.StringUtilities;
import org.jblooming.utilities.file.FileUtilities;
import org.jblooming.waf.constants.Fields;
import org.jblooming.waf.settings.ApplicationState;
import org.jblooming.waf.settings.I18n;
import org.jblooming.waf.view.PageState;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.Calendar;

public class UploadHelper {

  public String originalFileName;
  public String mimetype;

  public String temporaryFileName;
  public File temporaryFile;

  public String code;
  public String repositoryPath;
  public String repositoryFileName;


  private UploadHelper() {
  }

  public static UploadHelper saveInRepository(int documentId,
                                              String optionalVersionLabel,
                                              String optionalRepositorySubFolder,
                                              PageState view) throws IOException {

    return saveInRepository(Fields.FILE_TO_UPLOAD, documentId, optionalVersionLabel, optionalRepositorySubFolder, view);
  }

  public static UploadHelper saveInRepository(String formFileFieldName,
                                              int documentId,
                                              String optionalVersionLabel,
                                              String optionalRepositorySubFolder,
                                              PageState pageState) throws IOException {

    String repUrl = ApplicationState.getApplicationSetting(SystemConstants.FLD_REPOSITORY_URL);
    return saveInFolder(formFileFieldName, documentId, optionalVersionLabel, repUrl, optionalRepositorySubFolder, pageState);
  }

  public static UploadHelper saveInFolder(String formFileFieldName,
                                          int documentId,
                                          String optionalVersionLabel,
                                          String folderLocation,
                                          String optionalRepositorySubFolder,
                                          PageState pageState) throws IOException {

    return saveInFolder(formFileFieldName, documentId, optionalVersionLabel, folderLocation, optionalRepositorySubFolder, null, pageState);
  }

  public static UploadHelper saveInFolder(String formFileFieldName,
                                          int documentId,
                                          String optionalVersionLabel,
                                          String folderLocation,
                                          String optionalRepositorySubFolder,
                                          String nameToGiveToFileInRepository,
                                          PageState pageState) throws IOException {

    return saveInFolder(formFileFieldName, documentId, optionalVersionLabel, folderLocation, optionalRepositorySubFolder, nameToGiveToFileInRepository, true, pageState);
  }

  public static UploadHelper saveInFolder(String formFileFieldName,
                                          int documentId,
                                          String optionalVersionLabel,
                                          String folderLocation,
                                          String optionalRepositorySubFolder,
                                          String nameToGiveToFileInRepository,
                                          boolean useDocCodes,
                                          PageState pageState) throws IOException {
    return saveInFolder(formFileFieldName, documentId, optionalVersionLabel, folderLocation, optionalRepositorySubFolder, nameToGiveToFileInRepository, useDocCodes, false, pageState);
  }

  // teoros, january 2008 :: added 'boolean deleteSpacesInFileName' :: in tinyMCE uploaded images with spaces in fileName do not work properly   
  public static UploadHelper saveInFolder(String formFileFieldName,
                                          int documentId,
                                          String optionalVersionLabel,
                                          String folderLocation,
                                          String optionalRepositorySubFolder,
                                          String nameToGiveToFileInRepository,
                                          boolean useDocCodes,
                                          boolean deleteSpacesInFileName,
                                          PageState pageState) throws IOException {

    if (optionalVersionLabel == null)
      optionalVersionLabel = "";

    if (optionalRepositorySubFolder == null)
      optionalRepositorySubFolder = "";

    UploadHelper auh = UploadHelper.getInstance(formFileFieldName, pageState);

    if (auh != null && auh.temporaryFileName != null) {
      auh.temporaryFile = new File(auh.temporaryFileName);

      if (auh.temporaryFileName != null && auh.temporaryFileName.length() > 0 && auh.temporaryFile.exists()) {

        String lastPartFileName = null;
        if (auh.originalFileName.lastIndexOf(".") > -1)
          lastPartFileName =
                  auh.originalFileName.substring(Math.max(auh.originalFileName.lastIndexOf("\\"), auh.originalFileName.lastIndexOf("/")) + 1,
                          auh.originalFileName.lastIndexOf("."));
        else
          lastPartFileName = auh.originalFileName;

        if (deleteSpacesInFileName)
          lastPartFileName = StringUtilities.replaceAllNoRegex(lastPartFileName, " ", "_");

        String code = FileUtilities.padd(documentId + "", 6, "0");
        if (!useDocCodes)
          code = "";
        auh.code = code;

        if (nameToGiveToFileInRepository == null) {
          auh.repositoryFileName =
                  (code != "" ? code + '.' : "") +
                          lastPartFileName +
                          ((optionalVersionLabel != null && optionalVersionLabel.trim().length() > 0) ? '.' + StringUtilities.stripToLegal(optionalVersionLabel) : "") +
                          FileUtilities.getFileExt(auh.originalFileName);
        } else
          auh.repositoryFileName = (code != "" ? code + '.' : "") + nameToGiveToFileInRepository;

        String folderName = folderLocation + (folderLocation.endsWith("/") ? "/" : "") + StringUtilities.stripToLegal(optionalRepositorySubFolder);
        // robik 17/09/2007 to make more robust when uploading. Remove check from code where possible
        new File(folderName).mkdirs();

        auh.repositoryPath = folderName + '/' + auh.repositoryFileName;


        FileInputStream fis = new FileInputStream(auh.temporaryFile);
        FileOutputStream fos = new FileOutputStream(auh.repositoryPath);

        byte[] byteCount = new byte[1024]; // la dimensione puo essere "aggiustata" per esigenze specifiche

        int read;
        for (; ;) {
          read = fis.read(byteCount);
          if (read < 0)
            break;
          fos.write(byteCount, 0, read);
        }
        fos.flush();
        fis.close();
        fos.close();
      }
    }
    return auh;
  }

  public static UploadHelper getInstance(String formFileFieldName, PageState pageState) {
    UploadHelper auh = null;
    String originalFileName = pageState.getEntry(formFileFieldName).stringValueNullIfEmpty();
    if (originalFileName != null) {
      // aprile 2007 teoros:: persistentFile serialized name could be longer than 255  ==> data truncation
      // see also:: Uploader.save
      if (originalFileName.trim().length() > 50) {
        String extension = FileUtilities.getFileExt(originalFileName);
        originalFileName = originalFileName.substring(0, 50) + extension;
      }
      auh = new UploadHelper();

      auh.originalFileName = originalFileName;

      auh.mimetype = pageState.getEntry(formFileFieldName + MultipartFormRequestEncodingFilter.CONTENT_TYPE).stringValueNullIfEmpty();

      auh.temporaryFileName = pageState.getEntry(formFileFieldName + MultipartFormRequestEncodingFilter.TEMPORARY_FILENAME).stringValueNullIfEmpty();

      if (auh.temporaryFileName != null)
        auh.temporaryFile = new File(auh.temporaryFileName);

    }
    return auh;
  }

  public static UploadHelper getInstance(String originalFileName, String temporaryFileName, String mimetype, InputStream in) {
    UploadHelper auh = null;
    if (originalFileName != null) {
      if (originalFileName.trim().length() > 50) {
        String extension = FileUtilities.getFileExt(originalFileName);
        originalFileName = originalFileName.substring(0, 50) + extension;
      }
      auh = new UploadHelper();

      auh.originalFileName = originalFileName;
      auh.mimetype = mimetype;
      auh.temporaryFileName = temporaryFileName;

      if (auh.temporaryFileName != null)
        auh.temporaryFile = new File(auh.temporaryFileName);

      if (auh.temporaryFileName != null) {
        byte[] buffer = new byte[1024];
        int len;
        OutputStream out = null;
        try {
          out = new BufferedOutputStream(new FileOutputStream(auh.temporaryFileName));
          while ((len = in.read(buffer)) >= 0)
            out.write(buffer, 0, len);
          in.close();
          out.close();
        } catch (IOException e) {
        }
      }
    }
    return auh;
  }

  public static boolean fileSizeAllowed(String formFieldName, int maxSize, PageState pageState) {
    boolean goOn = true;
    UploadHelper auh = UploadHelper.getInstance(formFieldName, pageState);
    if (auh != null && auh.temporaryFileName != null) {
      File temp = new File(auh.temporaryFileName);
      if (temp.exists()) {
        long fileSize = temp.length();
        if (maxSize > 0 && fileSize > 0 && fileSize > maxSize)
          goOn = false;
      }
    }
    return goOn;
  }

  public static void logUpload(String context, String objectName, String actionOnFile, HttpServletRequest request, PageState pageState) throws IOException {

    String appName = pageState.getApplication().getName().toUpperCase();
    String path = ApplicationState.getApplicationSetting("UPLOADED_FILES_LOG_PATH");
    String log = HttpUtilities.getFileSystemRootPathForRequest(request) + File.separator + "WEB-INF" + File.separator + "log" + File.separator + appName + "_uploadHistory.log";
    if (path != null && path.trim().length() > 0)
      log = path + File.separator + appName + "_uploadHistory.log";

    Operator logged = null;
    String opName = I18n.get("UNCATCHED");
    String opUserName = I18n.get("UNCATCHED");
    logged = pageState.getLoggedOperator();
    opName = logged.getFullname();
    opUserName = logged.getLoginName();

    Calendar cal = Calendar.getInstance();
    int day = cal.get(Calendar.DAY_OF_MONTH);
    int month = cal.get(Calendar.MONTH) + 1;
    int year = cal.get(Calendar.YEAR);
    int hour = 0, minute = 0;
    String strYear = "", strMonth = "", strDay = "", strHour = "";
    hour = cal.get(Calendar.HOUR_OF_DAY);
    minute = cal.get(Calendar.MINUTE);
    if (hour < 10)
      strHour = "0" + Integer.toString(hour);
    else
      strHour = Integer.toString(hour);
    strHour += ":";
    if (minute < 10)
      strHour += "0" + Integer.toString(minute);
    else
      strHour += Integer.toString(minute);
    if (day < 10)
      strDay = "0" + Integer.toString(day);
    else
      strDay = Integer.toString(day);
    if (month < 10)
      strMonth = "0" + Integer.toString(month);
    else
      strMonth = Integer.toString(month);
    strYear = Integer.toString(year);
    String date = strDay + "/" + strMonth + "/" + strYear + " - " + strHour;

    StringBuffer hist = new StringBuffer();
    hist.append("----------------------------------------------------------------------------------------------------------------\r\n" +
            date + "\n" +
            "REMOTE_ADDR (IP)" + ": " + request.getRemoteAddr() + "\r\n" +
            "REMOTE_HOST" + ": " + request.getRemoteHost() + "\r\n" +
            I18n.get("OPERATOR_LOGGED") + ": " + opName + "\r\n" +
            I18n.get("USERNAME") + ": " + opUserName + "\r\n\n" +
            context + " - " + objectName + "\r\n" +
            actionOnFile + "\r\n\n");

    File file = new File(log);
    file.createNewFile();
    FileUtilities.appendToFile(log, hist.toString());
  }

}