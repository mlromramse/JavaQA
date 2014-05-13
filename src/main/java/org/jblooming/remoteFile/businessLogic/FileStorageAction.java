package org.jblooming.remoteFile.businessLogic;

import org.jblooming.ontology.businessLogic.DeleteHelper;
import org.jblooming.operator.Operator;
import org.jblooming.persistence.PersistenceHome;
import org.jblooming.persistence.exceptions.PersistenceException;
import org.jblooming.remoteFile.Document;
import org.jblooming.remoteFile.FileStorage;
import org.jblooming.security.PlatformPermissions;
import org.jblooming.security.SecurityException;
import org.jblooming.system.SystemConstants;
import org.jblooming.tracer.Tracer;
import org.jblooming.utilities.DateUtilities;
import org.jblooming.utilities.StringUtilities;
import org.jblooming.waf.ActionSupport;
import org.jblooming.waf.exceptions.ActionException;
import org.jblooming.waf.settings.ApplicationState;
import org.jblooming.waf.settings.I18n;
import org.jblooming.waf.view.PageState;

import java.util.List;

public class FileStorageAction extends ActionSupport {

  Operator logged;

  public FileStorageAction(PageState pageState) {
    super(pageState);
    this.logged = pageState.getLoggedOperator();
  }
  
  public void cmdAdd() throws SecurityException {

    logged.testPermission(PlatformPermissions.fileStorage_canCreate);

    FileStorage mainObject = new FileStorage();
    mainObject.setIdAsNew();
    pageState.setMainObject(mainObject);
    //mainObject.setArea(logged.getMyPersonFromPersistence().getArea());//todo area

    mainObject.setOwner(logged);

     //todo pageState.addClientEntry("AREA", mainObject.getArea().getId());

  }

  public void cmdEdit() throws PersistenceException, org.jblooming.security.SecurityException {

    FileStorage document = (FileStorage) PersistenceHome.findByPrimaryKey(FileStorage.class, pageState.getMainObjectId());

    document.testPermission(logged,PlatformPermissions.fileStorage_canRead);

    pageState.setMainObject(document);
    make(document);

  }

  public void cmdSave() throws PersistenceException, SecurityException {

    FileStorage document;
    if (PersistenceHome.NEW_EMPTY_ID.equals(pageState.mainObjectId)) {
      document = new FileStorage();
      document.setIdAsNew();
    } else
      document = (FileStorage) PersistenceHome.findByPrimaryKey(FileStorage.class, pageState.getMainObjectId());

    //todo ActionUtilities.setIdentifiable(pageState.getEntryAndSetRequired("AREA"),document,"area");

    document.testPermission(logged,PlatformPermissions.fileStorage_canWrite);


    pageState.setMainObject(document);
    document.setCode(pageState.getEntry("DOCUMENT_CODE").stringValueNullIfEmpty());
    try {
      String name = pageState.getEntryAndSetRequired("DOCUMENT_NAME").stringValue();
      document.setName(name);
    } catch (ActionException e) {
    }
    try {
      String urlToContent = pageState.getEntryAndSetRequired("DOCUMENT_URL_TO_CONTENT").stringValue();
      document.setType(Document.IS_FILE_STORAGE);
      //verify that it is allowed URL
      boolean allowed = false;
      if (urlToContent.indexOf("..") == -1) {
        //todo da vedere il caso reservableService
        String spa = ApplicationState.getApplicationSetting(SystemConstants.STORAGE_PATH_ALLOWED);
        if (spa == null) {
          Tracer.platformLogger.warn("STORAGE_PATH_ALLOWED value not found in global settings - global.properties");
        } else {
          List<String> allowedPaths = StringUtilities.splitToList(spa, ",");
          for (String s : allowedPaths) {
            //windows case insnsitivity and \ usage
            if (System.getProperty("os.name").toUpperCase().indexOf("WINDOWS") > -1) {
              if (urlToContent.toLowerCase().startsWith(s.toLowerCase())) {
                allowed = true;
                break;
              }
            } else if (urlToContent.startsWith(s)) {
              allowed = true;
              break;
            }
          }
        }
      }
      if (allowed)
        document.setContent(urlToContent);
      else
        pageState.getEntry("DOCUMENT_URL_TO_CONTENT").errorCode = I18n.get("PATH_NOT_ALLOWED");
    } catch (ActionException e) {

    }
    document.setType(Document.IS_FILE_STORAGE);
    document.setConnectionHost(pageState.getEntry("connectionHost").stringValueNullIfEmpty());
    document.setConnectionNotes(pageState.getEntry("connectionNotes").stringValueNullIfEmpty());
    document.setConnectionUser(pageState.getEntry("connectionUser").stringValueNullIfEmpty());
    document.setConnectionPwd(pageState.getEntry("connectionPwd").stringValueNullIfEmpty());
    String ct = pageState.getEntry("connectionType").stringValueNullIfEmpty();
    document.setConnType(Document.ConnectionType.valueOf(ct));
    document.setSumma(pageState.getEntry("SUMMA").stringValueNullIfEmpty());


    if (pageState.validEntries()) {
      if (PersistenceHome.NEW_EMPTY_ID.equals(pageState.mainObjectId)) {
        document.setOwner(pageState.getLoggedOperator());
      }
      document.store();

     
    }
  }


  public void cmdDelete() throws org.jblooming.security.SecurityException, PersistenceException {

    FileStorage delenda = (FileStorage) PersistenceHome.findByPrimaryKey(FileStorage.class, pageState.getMainObjectId());
     delenda.testPermission(logged,PlatformPermissions.fileStorage_canWrite);
    DeleteHelper.cmdDelete(delenda, pageState);
  }




  public void make(FileStorage document) throws PersistenceException {
    pageState.addClientEntry("DOCUMENT_CODE", document.getCode());
    pageState.addClientEntry("DOCUMENT_NAME", document.getName());
    pageState.addClientEntry("DOCUMENT_AUTHORED", DateUtilities.dateToString(document.getAuthored()));
    pageState.addClientEntry("DOCUMENT_AUTHOR", document.getAuthor());
    pageState.addClientEntry("DOCUMENT_AREA", (document.getArea() != null ? document.getArea().getId() : ""));
    pageState.addClientEntry("DOCUMENT_KEYWORDS", document.getKeywords());
    pageState.addClientEntry("DOCUMENT_VERSION", document.getVersion());
    pageState.addClientEntry("DOCUMENT_VERSION_LABEL", document.getVersionLabel());
    pageState.addClientEntry("DOCUMENT_TYPE", document.getType());

   //todo pageState.addClientEntry("AREA", document.getArea().getId());

    if (document.getType() == Document.IS_UPLOAD && document.getFile() != null) {
      pageState.addClientEntry("DOCUMENT_UPLOAD", document.getFile().serialize());
    } else if (document.getType() == Document.IS_URL) {
      pageState.addClientEntry("DOCUMENT_URL_TO_CONTENT", document.getContent());
    } else if (document.getType() == Document.IS_CONTENT) {
      pageState.addClientEntry("SUMMA", document.getContent());
    } else if (document.getType() == Document.IS_FILE_STORAGE) {
      pageState.addClientEntry("DOCUMENT_URL_TO_CONTENT", document.getContent());
      pageState.addClientEntry("connectionType", document.getConnType());
      pageState.addClientEntry("connectionHost", document.getConnectionHost());
      pageState.addClientEntry("connectionUser", document.getConnectionUser());
      pageState.addClientEntry("connectionPwd", document.getConnectionPwd());
      pageState.addClientEntry("connectionNotes", document.getConnectionNotes());
    }
    if (document.getType() != Document.IS_CONTENT)
      pageState.addClientEntry("SUMMA", document.getSumma());

  }


}
