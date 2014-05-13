package org.jblooming.ontology;

import org.jblooming.waf.settings.ApplicationState;
import org.jblooming.waf.view.PageSeed;
import org.jblooming.waf.view.PageState;
import org.jblooming.waf.constants.Fields;
import org.jblooming.system.SystemConstants;
import org.jblooming.utilities.StringUtilities;
import org.jblooming.utilities.HttpUtilities;
import org.jblooming.utilities.file.FileUtilities;
import org.jblooming.persistence.PersistenceHome;
import org.jblooming.persistence.hibernate.PersistenceContext;
import org.jblooming.persistence.exceptions.FindByPrimaryKeyException;
import org.jblooming.persistence.exceptions.StoreException;
import org.jblooming.PlatformRuntimeException;

import java.io.*;

import net.sf.json.JSONObject;

/**
 * @author Pietro Polsinelli ppolsinelli@open-lab.com
 */
public class PersistentFile implements Serializable {

  public static final String TYPE_DB = "DB";
  public static final String TYPE_FILESTORAGE = "FR";           // this is relative to the REPOSITORY_URL parameter 
  public static final String TYPE_FILESTORAGE_ABSOLUTE = "FS";  // this is absolute e.g. c:\tmp
  public static final String TYPE_WEBAPP_FILESTORAGE = "WF";    // relative to root

  private int UID;
  private String type = TYPE_DB;
  private String originalFileName;

  /**
   * this is the complete path to the file, file inclusive
   */
  private String fileLocation;

  /**
   * this is the complete path to the file, file exclusive; not persisted
   */
  public String fileDir;

  public static final String PERSISTENTFILE_ID = "PERSISTENTFILE_ID";

  // not persisted is used only to get a un-persisted reference
  private BinaryLargeObject blob=null;

  /**
   * @deprecated use constructor with type
   */
  public PersistentFile(int UID, String originalFileName) {
    this.setOriginalFileName(originalFileName);
    this.setUID(UID);
  }

  public PersistentFile(int UID, String originalFileName, String type) {
    this.setOriginalFileName(originalFileName);
    this.setUID(UID);
    this.type = type;
  }


  public int getUID() {
    return UID;
  }

  public void setUID(int UID) {
    this.UID = UID;
  }


  public boolean equals(Object o) {
    return this.compareTo(o) == 0;
  }

  public int hashCode() {
    return serialize().hashCode();
  }

  public int compareTo(Object o) {
    if (this == o)
      return 0;
    if (o == null)
      return -1;
    else {
      return serialize().compareTo(((PersistentFile) o).serialize());
    }
  }

  public String getType() {
    return type;
  }

  /**
   * @deprecated use constructor
   */
  public void setType(String type) {
    this.type = type;
  }

  public String getOriginalFileName() {
    return originalFileName;
  }

  public void setOriginalFileName(String originalFileName) {
    this.originalFileName = originalFileName;
  }

  /**
   * @return the file location relative to PersistentFile.type complete with file name
   */
  public String getFileLocation() {
    return fileLocation;
  }

  public void setFileLocation(String fileLocation) {
    this.fileLocation = fileLocation;
  }

  public String serialize() {
    String ser = type + "_" + UID + "_" + originalFileName;
    if (TYPE_FILESTORAGE_ABSOLUTE.equals(type) || TYPE_FILESTORAGE.equals(type) || TYPE_WEBAPP_FILESTORAGE.equals(type))
      ser = ser + "+++" + fileLocation;
    return ser;
  }

  public String getName() {
    String name = "";
    if (TYPE_DB.equals(type)) {
      name = originalFileName;
    } else {
      String fileLocNorm = StringUtilities.replaceAllNoRegex(fileLocation, "\\", "/");
      name = fileLocNorm.substring(Math.max(0, fileLocNorm.lastIndexOf("/") + 1));
    }
    return name;
  }

  /*
  public void makedirs(){
   new File( ApplicationState.getApplicationSetting(SystemConstants.FLD_REPOSITORY_URL) + File.separator + "photos").mkdirs();
  }
  */

  public static PersistentFile deserialize(String serObj) {
    PersistentFile pf = null;
    if (serObj != null) {
      int first_ = serObj.indexOf("_");
      if (first_ > 0) {
        String type = serObj.substring(0, first_);

        int second_ = serObj.indexOf("_", first_ + 1);
        int third = serObj.length();
        if (TYPE_FILESTORAGE.equals(type) || TYPE_WEBAPP_FILESTORAGE.equals(type) || TYPE_FILESTORAGE_ABSOLUTE.equals(type))
          third = serObj.indexOf("+++");
        if (second_ > 0) {
          int UID = Integer.parseInt(serObj.substring(first_ + 1, second_));
          String originalFileName = serObj.substring(second_ + 1, third);
          pf = new PersistentFile(UID, originalFileName);
          pf.type = type;
        }
        if (TYPE_FILESTORAGE.equals(type) || TYPE_WEBAPP_FILESTORAGE.equals(type) || TYPE_FILESTORAGE_ABSOLUTE.equals(type)) {
          String fileLoc = serObj.substring(serObj.indexOf("+++") + 3);
          pf.fileLocation = fileLoc;
          File file = new File(fileLoc);
          pf.fileDir = file.getParent();
        }
      }
    }
    return pf;
  }


  public PageSeed getPageSeed() {
    PageSeed ps = null;
    if (!PersistentFile.TYPE_WEBAPP_FILESTORAGE.equals(getType())) {
      ps = new PageSeed(ApplicationState.contextPath + "/commons/layout/partUploaderView.jsp");
      ps.addClientEntry(Fields.FILE_TO_UPLOAD, serialize());
    } else {
      ps = new PageSeed(ApplicationState.contextPath + "/" + StringUtilities.replaceAllNoRegex(getFileLocation(), "\\", "/"));
    }
    return ps;
  }


  public BinaryLargeObject getBlob(PersistenceContext pc) throws FindByPrimaryKeyException {
    if (TYPE_DB.equals(getType())){
      if (blob==null){
        blob= (BinaryLargeObject) PersistenceHome.findByPrimaryKey(BinaryLargeObject.class, getUID(), pc);
      }
      return blob;
    } else{
      throw new PlatformRuntimeException("Blob object can be used in case of TYPE_DB only.");
    }
  }

  public InputStream getInputStream() throws FileNotFoundException, FindByPrimaryKeyException {
    // if you are using multiple connection with BLOB use in both DB you may be in trouble
    return getInputStream(PersistenceContext.getDefaultPersistenceContext());
  }

  public InputStream getInputStream(PersistenceContext pc) throws FileNotFoundException, FindByPrimaryKeyException {

    InputStream inputStream = null;
    if (PersistentFile.TYPE_DB.equals(getType())) {
      if (pc == null)
        throw new PlatformRuntimeException("Invalid use of getInputStream(null) with DB type PersistentFile");
      BinaryLargeObject blo = getBlob(pc);
      try {
        inputStream = blo.getInputStream();
      } catch (Throwable e) {
        throw new PlatformRuntimeException(e);
      }
    } else if (PersistentFile.TYPE_FILESTORAGE.equals(getType())) {
      String repositoryUrl = ApplicationState.getApplicationSetting(SystemConstants.FLD_REPOSITORY_URL);
      String fileLocation = getFileLocation();
      String separator = (repositoryUrl.endsWith(File.separator) || repositoryUrl.endsWith("/") ||
              fileLocation.startsWith(File.separator) || fileLocation.startsWith("/"))
              ? "" : File.separator;
      //String url = StringUtilities.replaceAllNoRegex(repositoryUrl + separator + fileLocation, "/", File.separator);
      String url = repositoryUrl + separator + fileLocation;
      inputStream = new FileInputStream(url);

    } else if (PersistentFile.TYPE_FILESTORAGE_ABSOLUTE.equals(getType())) {
      inputStream = new FileInputStream(getFileLocation());
    } else if (PersistentFile.TYPE_WEBAPP_FILESTORAGE.equals(getType())) {
      inputStream = new FileInputStream(ApplicationState.webAppFileSystemRootPath + File.separator + getFileLocation());
    } else {
      throw new PlatformRuntimeException("Unsupported Persistent File Type: " + getType());
    }
    return inputStream;
  }



  /**
   *
   * @param persistentFileType determine where the final file will be placed. In case of DB type a BinaryLargeObject will be created and stored. Remember to assign referral ID and CLASS and store the blob again.
   * @param inputStream
   * @param originalFileName
   * @param relativePathFileName instruct the method on where to place the real file
   *              file is created starting from the REPOSITORY_URL in case of TYPE_FILESTORAGE. e.g.: [REPOSITORY_URL] + / + "myfoder1/myfolder2/myfile.ext".
   *              file is created starting from web-app root in case of TYPE_WEBAPP_FILESTORAGE. e.g.: [REPOSITORY_URL] + / + "myfoder1/myfolder2/myfile.ext".
   *              file is created where specified (as-is) in case of TYPE_FILESTORAGE_ABSOLUTE e.g.: "c:\myfolder1\myfolder2\myfile.ext" or "/usr/myfoder1/myfolder2/myfile.ext"
   *              the parameter is ignored in case of DB type.
   * @param pc is used only in case of DB type. Use the PersistenceContext.get(MyObject.Class) where myObject.setPersistentFile()
   * @return  a PersistentFile created correctly basing on type, relative path etc..
   * The final real path where the file will be created will [SystemConstants.FLD_REPOSITORY_URL] + / + relativePath
   */
  public static PersistentFile createPersistentFileFromStream(String persistentFileType, InputStream inputStream, String originalFileName, String relativePathFileName, PersistenceContext pc) throws IOException {

    PersistentFile pf = new PersistentFile(0,originalFileName,persistentFileType);
    pf.setFileLocation(relativePathFileName);

    if (PersistentFile.TYPE_DB.equals(persistentFileType)) {
      if (pc == null)
        throw new PlatformRuntimeException("Invalid use of createPersistentFileFromStream with DB type when pc is null");

      // create BLOB
      BinaryLargeObject blo = new BinaryLargeObject() ;
      blo.feed(inputStream);
      try {
        blo.store(pc);
      } catch (StoreException e) {
        throw new PlatformRuntimeException(e);
      }
      pf.blob=blo;
      pf.setUID(blo.getIntId());

    } else if (PersistentFile.TYPE_FILESTORAGE.equals(persistentFileType)) {
      String repositoryUrl = ApplicationState.getApplicationSetting(SystemConstants.FLD_REPOSITORY_URL);
      String separator = (repositoryUrl.endsWith("\\") || repositoryUrl.endsWith("/") ||
              relativePathFileName.startsWith("\\") || relativePathFileName.startsWith("/"))
              ? "" : File.separator;
      String destination = repositoryUrl + separator + relativePathFileName;
      File destFile=new File(destination);
      File parent=destFile.getParentFile();
      parent.mkdirs();
      FileOutputStream fout = new FileOutputStream(destFile);
      FileUtilities.copy(inputStream,fout);
      fout.close();


    } else if (PersistentFile.TYPE_FILESTORAGE_ABSOLUTE.equals(persistentFileType)) {
      FileOutputStream fout = new FileOutputStream(relativePathFileName);
      FileUtilities.copy(inputStream,fout);
      fout.close();

    } else if (PersistentFile.TYPE_WEBAPP_FILESTORAGE.equals(persistentFileType)) {
      // be carefull the separator for a webapp is always /
      String repositoryUrl =ApplicationState.webAppFileSystemRootPath;
      String separator = (repositoryUrl.endsWith("\\") || repositoryUrl.endsWith("/") ||
              relativePathFileName.startsWith("\\") || relativePathFileName.startsWith("/"))
              ? "" : File.separator;
      String destination = repositoryUrl + separator + relativePathFileName;
      File destFile=new File(destination);
      File parent=destFile.getParentFile();
      parent.mkdirs();
      FileOutputStream fout = new FileOutputStream(destFile);
      FileUtilities.copy(inputStream,fout);
      fout.close();

    } else {
      throw new PlatformRuntimeException("Unsupported Persistent File Type: " + persistentFileType);
    }
    return pf;
  }


  public JSONObject jsonify() {
    JSONObject ret = new JSONObject();
    ret.element("uid", serialize());
    ret.element("type", getType());
    ret.element("name", getOriginalFileName());
    ret.element("img",HttpUtilities.getContentType(getName()).replace('/', '_'));
    return ret;
  }
}
