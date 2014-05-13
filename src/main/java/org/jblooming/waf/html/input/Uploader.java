package org.jblooming.waf.html.input;

import org.jblooming.ApplicationException;
import org.jblooming.PlatformRuntimeException;
import org.jblooming.tracer.Tracer;
import org.jblooming.system.SystemConstants;
import org.jblooming.ontology.BinaryLargeObject;
import org.jblooming.ontology.Identifiable;
import org.jblooming.ontology.PersistentFile;
import org.jblooming.ontology.IdentifiableSupport;
import org.jblooming.persistence.PersistenceHome;
import org.jblooming.persistence.exceptions.FindByPrimaryKeyException;
import org.jblooming.persistence.exceptions.PersistenceException;
import org.jblooming.uidgen.CounterHome;
import org.jblooming.utilities.HttpUtilities;
import org.jblooming.utilities.StringUtilities;
import org.jblooming.utilities.file.FileUtilities;
import org.jblooming.waf.UploadHelper;
import org.jblooming.waf.exceptions.ActionException;
import org.jblooming.waf.settings.ApplicationState;
import org.jblooming.waf.constants.Fields;
import org.jblooming.waf.html.core.JspHelper;
import org.jblooming.waf.html.layout.Skin;
import org.jblooming.waf.html.state.Form;
import org.jblooming.waf.view.ClientEntry;
import org.jblooming.waf.view.PageState;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;
import javax.mail.internet.MimeUtility;
import java.io.*;
import java.net.URLEncoder;


public class Uploader extends JspHelper {

  public Skin skin;
  public String fieldName;
  public boolean disabled = false;
  public int size;
  public String label;
  public String className="formElements";
  public String classLabelName;
  public String separator;
  public String jsScript;
  public boolean required = false;
  public String toolTip;
  public boolean doFeedBackError = true;
  public Form form;

  /**
   * key control
   */
  public String actionListened;
  public int keyToHandle;
  public String launchedJsOnActionListened;
  public boolean translateError = true;
  public boolean readOnly = false;
  public boolean treatAsAttachment = true;


  public Uploader(String fieldName, PageState pageState) {
    this.id = fieldName + "_upl";
    this.urlToInclude = "/commons/layout/partUploader.jsp";
    this.fieldName = fieldName;
    this.skin = pageState.getSkin();
    this.required = pageState.getEntry(fieldName).required;
  }

  @Deprecated
  public Uploader(String fieldName, Form form, PageState pageState) {
    this.id = fieldName + "_upl";
    this.urlToInclude = "/commons/layout/partUploader.jsp";
    this.fieldName = fieldName;
    this.skin = pageState.getSkin();
    this.form = form;
    this.required = pageState.getEntry(fieldName).required;
  }

  // of great elegance
  public static String getHiddenFieldName(String ceName) {
    return "sp_fi_br_" + ceName + "_upl";
  }

  public String getDiscriminator() {
    return SmartCombo.class.getName();
  }

  public void toHtml(PageContext pageContext) {
    pageContext.getRequest().setAttribute(ACTION, "");
    super.toHtml(pageContext);
  }

  public static void displayFile(PageState pageState, HttpServletResponse response) throws IOException, FindByPrimaryKeyException {
    displayFile(true, pageState, response);
  }

  public static void displayFile(boolean treatAsAttachment, PageState pageState, HttpServletResponse response) throws IOException, FindByPrimaryKeyException {

    String pfSer = pageState.getEntry(Fields.FILE_TO_UPLOAD).stringValueNullIfEmpty();

    if (pfSer != null) {
      PersistentFile pf = PersistentFile.deserialize(pfSer);

      response.setContentType(HttpUtilities.getContentType(pf.getOriginalFileName()));
      String filename = pf.getOriginalFileName();

      InputStream inputStream = pf.getInputStream();
      // this fantastic trick fixes the problem of non US filenames
      String filenameEncoded = pageState.sessionState.isFirefox() ? MimeUtility.encodeText(filename, "UTF8", "B") : URLEncoder.encode(filename, "UTF8");
      filenameEncoded = StringUtilities.replaceAllNoRegex(StringUtilities.replaceAllNoRegex(filenameEncoded, "+", "_"), " ", "_");

      // sets header with original file name
      if (treatAsAttachment) {
        response.setHeader("content-disposition", "attachment; filename=" + filenameEncoded);
      } else
        response.setHeader("content-disposition", "inline; filename=" + filenameEncoded);

      // write data to stream and close it
      FileUtilities.writeStream(inputStream, response.getOutputStream());
      inputStream.close();

    } else {
      throw new PlatformRuntimeException("Unsupported PersistentFile type.");
    }
  }

  public static PersistentFile save(String formFieldName, PageState pageState) throws PersistenceException, ApplicationException, ActionException {
    return save(formFieldName, PersistentFile.TYPE_FILESTORAGE, pageState);
  }

  public static PersistentFile save(String formFieldName, String type, PageState pageState) throws PersistenceException, ApplicationException, ActionException {
    PersistentFile persistentFile = new PersistentFile(0, null);
    persistentFile.setType(type);
    persistentFile.fileDir = "";//ApplicationState.getApplicationSetting(SystemConstants.FLD_REPOSITORY_URL);
    return save(null, persistentFile, formFieldName, pageState);
  }

  public static PersistentFile save(Identifiable mainObject, PersistentFile persistentFile, String formFieldName, PageState pageState) throws PersistenceException, ApplicationException, ActionException {
    return save(mainObject, persistentFile, formFieldName, null, pageState);
  }

  public static PersistentFile save(Identifiable mainObject, PersistentFile persistentFile, String formFieldName, String nameToGiveToFileInRepository, PageState pageState) throws PersistenceException, ApplicationException, ActionException {

    if (!pageState.multipart)
      Tracer.platformLogger.warn("Seems attempted upload on a non-multipart form:"+pageState.href);

    final ClientEntry entry = pageState.getEntry(formFieldName);
    String value = entry.stringValue();
    if (value != null && value.trim().length() <= 0) {
      value = null;
    // aprile 2007 teoros:: il nome serializzato del persistent file rischia di superare 255 caratteri ==> data truncation
    // vedi anche:: UploadHelper.getInstance()
    } else if (value!=null && value.trim().length()>50) {
      String extension = FileUtilities.getFileExt(value);
      value = value.substring(0, 50) + extension;
    }

    boolean fileSelected = value != null;
    boolean uploadEntryMissing = entry.name == null;  //only the first time is shown the page, after we have always the entry.name... thanks to filter
    boolean alreadyPersisted = (persistentFile != null && persistentFile.getUID() != 0);

    //creating a file
    boolean creatingAFile = fileSelected && !alreadyPersisted;
    //updating a file
    boolean updatingAFile = fileSelected && alreadyPersisted;
    //leave it as it is
    boolean leaveItAlone = uploadEntryMissing && alreadyPersisted;
    //removing a file
    boolean removeIt = !uploadEntryMissing && alreadyPersisted && !fileSelected;

    //DO NOT TOUCH THIS WITHOUT ASKING PIETRO
    if (!fileSelected && !alreadyPersisted) {
      persistentFile = null;
      return null;
    }

    //DO NOT TOUCH THIS WITHOUT ASKING PIETRO
    if (leaveItAlone) {
      pageState.addClientEntry(formFieldName, persistentFile.serialize());
      return persistentFile;
    }

    boolean isBlob = persistentFile == null || PersistentFile.TYPE_DB.equals(persistentFile.getType());
    boolean isWebApp = persistentFile != null && PersistentFile.TYPE_WEBAPP_FILESTORAGE.equals(persistentFile.getType());

    if (persistentFile != null) {

      if (creatingAFile || updatingAFile) {

        if (isBlob) {
          UploadHelper uh = UploadHelper.getInstance(entry.name, pageState);

          if (uh != null && uh.originalFileName != null && uh.originalFileName.trim().length() > 0) {

            BinaryLargeObject blo = null;
            if (updatingAFile) {
              blo = (BinaryLargeObject) PersistenceHome.findByPrimaryKey(BinaryLargeObject.class, persistentFile.getUID());
              //we are forced to create always a new one, as at least in Oracle the update of multiple blobs in the same trans seems broken.
              if (blo!=null)
                blo.remove();
            }

            blo = new BinaryLargeObject();

            try {
              blo.feed(new FileInputStream(uh.temporaryFile));
              blo.setReferral(mainObject);
              blo.setOriginalFileName(uh.originalFileName);
              blo.store();

              persistentFile = new PersistentFile(Integer.parseInt(blo.getId().toString()), uh.originalFileName);
              entry.setValue(persistentFile.serialize());

            } catch (FileNotFoundException e) {
              throw new ApplicationException(e);
            }
          }

        } else {
          
          if (updatingAFile) {
            String path = "";
            if (PersistentFile.TYPE_WEBAPP_FILESTORAGE.equals(persistentFile.getType())) {
              String location = persistentFile.getFileLocation();
              location = StringUtilities.replaceAllNoRegex(location, "/", File.separator);
              location = StringUtilities.replaceAllNoRegex(location, "\\", File.separator);
              path = ApplicationState.webAppFileSystemRootPath + location;
            } else if (PersistentFile.TYPE_FILESTORAGE.equals(persistentFile.getType()))
              path = ApplicationState.getApplicationSetting(SystemConstants.FLD_REPOSITORY_URL) + persistentFile.getFileLocation();
            else
              path = persistentFile.getFileLocation();

            File delendo = new File(path);
            FileUtilities.tryHardToDeleteFile(delendo);
          }

          try {
            persistentFile.setUID(CounterHome.next(PersistentFile.PERSISTENTFILE_ID));
            String folderLocation = null;

            //this is the relative path
            String fileDir = persistentFile.fileDir != null ? persistentFile.fileDir : "";

            String type = persistentFile.getType();
            //this is the complete path
            if (isWebApp){
              fileDir = StringUtilities.replaceAllNoRegex(fileDir, "/", File.separator);
              fileDir = StringUtilities.replaceAllNoRegex(fileDir, "\\", File.separator);
              folderLocation = ApplicationState.webAppFileSystemRootPath + File.separator + fileDir;
            } else if (PersistentFile.TYPE_FILESTORAGE.equals(persistentFile.getType()))
              folderLocation = ApplicationState.getApplicationSetting(SystemConstants.FLD_REPOSITORY_URL) + (fileDir.trim().length() > 0 ? File.separator + fileDir : "");
            else
              folderLocation = fileDir;

            UploadHelper uh = UploadHelper.saveInFolder(formFieldName, persistentFile.getUID(), null, folderLocation, null, nameToGiveToFileInRepository, pageState);
            persistentFile.setType(type);
            if (isWebApp)
              persistentFile.setFileLocation(fileDir + "/" + uh.repositoryFileName);//File.separator
            else if (PersistentFile.TYPE_FILESTORAGE.equals(persistentFile.getType()))
              persistentFile.setFileLocation(fileDir + File.separator + uh.repositoryFileName);
            else
              persistentFile.setFileLocation(uh.repositoryPath);

            if (uh.originalFileName != null) {
              persistentFile.setOriginalFileName(value);
              entry.setValue(nameToGiveToFileInRepository);
            }

          } catch (IOException e) {
            throw new ApplicationException(e);
          }
        }
      }
    }

    if (removeIt) {
      if (persistentFile != null) {
        if (isBlob)
          ((IdentifiableSupport) PersistenceHome.findByPrimaryKey(BinaryLargeObject.class, persistentFile.getUID())).remove();
        else {
          File delendo = new File(persistentFile.getFileLocation());
          FileUtilities.tryHardToDeleteFile(delendo);
        }
        persistentFile = null;
      }

    } else {
      if (persistentFile != null) {
        int index = persistentFile.serialize().length();
        for (int i = 0; i < 3; i++) {
          index = persistentFile.serialize().lastIndexOf(".", index);
        }
        pageState.addClientEntry(formFieldName, persistentFile.serialize());
      }
    }

    return persistentFile;
  }

}