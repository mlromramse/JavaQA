package org.jblooming.remoteFile;

import org.jblooming.PlatformRuntimeException;
import org.jblooming.operator.Operator;
import org.jblooming.waf.UploadHelper;
import org.jblooming.utilities.HttpUtilities;
import org.jblooming.utilities.file.FileUtilities;

import java.util.List;
import java.util.ArrayList;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import net.sf.json.JSONObject;

public abstract class RemoteFile {

  public Document document;

  public RemoteFile(Document document) {
    this.document = document;
  }

  public abstract String getName();

  public abstract String getParent();

  public abstract RemoteFile getParentFile();

  public abstract String getRelativePath();

  public abstract boolean canWrite();

  public abstract boolean exists();

  public abstract boolean isDirectory();

  public abstract long lastModified();

  public abstract long length();

  public abstract boolean delete();

  public abstract List<String> list();

  public abstract List<RemoteFile> listFiles();

  public abstract boolean mkdir();

  public abstract boolean mkdirs();

  public abstract boolean renameTo(RemoteFile dest);

  public abstract boolean connect();

  public abstract boolean disconnect();

  public abstract boolean setTarget(String path);

  public abstract File downloadCopy() throws IOException;


  public abstract boolean canMakeDir();

  public abstract boolean canZipFiles();

  public abstract boolean canDeleteFiles();

  public abstract boolean canUploadFiles();

  public abstract InputStream getRemoteInputStream() throws IOException;

  public abstract void upload(UploadHelper uploader) throws IOException;

  public boolean upload(UploadHelper uploadHelper, boolean overrideIfExist) throws IOException {
    boolean exist = this.exists();
  if(!overrideIfExist && exist){
    // nothing
    return false;
  }else{
     this.upload(uploadHelper);
    return true;
  }
  }

  public String getPathFromDocument() {
    if (!getRelativePath().startsWith(document.getContent()))
      throw new PlatformRuntimeException("You damned hacker: path must start with " + document.getContent() + " but is " + getRelativePath());
    return getRelativePath().substring(document.getContent().length());
  }

  public boolean isRoot() {
    String content = document.getContent();
    return getRelativePath().equals(content);
  }

  public static RemoteFile getInstance(Document document){
    RemoteFile rf = null;
    if (Document.ConnectionType.FS.equals(document.getConnType())) {
      rf = new RemoteFileSystem(document);
    } else if (Document.ConnectionType.FTP.equals(document.getConnType())) {
      try {
        rf = (RemoteFile) Class.forName("org.jblooming.remoteFile.RemoteFileFTP").getConstructor(Document.class).newInstance(document);
      }
      catch (Throwable e) {
        //nothing dramatic: simply absent from classpath
      }
    } else if (Document.ConnectionType.SERVICE.equals(document.getConnType())) {
      rf = new RemoteFileService(document);
    } else if (Document.ConnectionType.SERVICEGROUP.equals(document.getConnType())) {
      try {
        rf = (RemoteFile) Class.forName("org.jblooming.remoteFile.RemoteFileService").getConstructor(Document.class).newInstance(document);
      }
      catch (Throwable e) {
        //nothing dramatic: simply absent from classpath
      }
    } else if (Document.ConnectionType.SVN.equals(document.getConnType()) || Document.ConnectionType.SVN_Http.equals(document.getConnType()) || Document.ConnectionType.SVN_Https.equals(document.getConnType())) {
      try {
        rf = (RemoteFile) Class.forName("org.jblooming.remoteFile.RemoteFileSvn").getConstructor(Document.class).newInstance(document);
      } catch (Throwable e) {
        //nothing dramatic: simply absent from classpath
      }
    }else if (Document.ConnectionType.DROPBOX.equals(document.getConnType())) {
      try {
        rf = (RemoteFile) Class.forName("org.jblooming.remoteFile.RemoteFileDropBox").getConstructor(Document.class).newInstance(document);
      } catch (Throwable e) {
        //nothing dramatic: simply absent from classpath
      }
    }
//    if (rf != null)
//      rf.setLoggedOperator(loggedOperator);
    return rf;
  }


  public String getImageName() {
    String img = null;
    if (isDirectory())
      img = "directory";
    else
      img = HttpUtilities.getContentType(getName()).replace('/', '_');

    return img;
  }

  public List<RemoteFile> expandFileList() {
    return expandFileList(this);
  }

  private List<RemoteFile> expandFileList(RemoteFile toExpand) {

    List<RemoteFile> v = new ArrayList<RemoteFile>();
    if (toExpand.isDirectory()) {
      List<RemoteFile> content = toExpand.listFiles();
      v.addAll(content);
      for (RemoteFile rf : content) {
        v.addAll(expandFileList(rf));
      }
    }
    return v;
  }

  public JSONObject jsonify() {
    JSONObject ret = new JSONObject();
    boolean isDir = isDirectory();

    String name = getName();
    ret.element("fsId", document.getId()); //file storage id
    ret.element("name", name);
    ret.element("isDirectory", isDir);
    ret.element("path", getPathFromDocument()!=null?getPathFromDocument().replace(File.separatorChar, '/'):null);
    //ret.element("parent", getParent());
    ret.element("img", getImageName());
    if (!isDir){
      long bytes = length();
      ret.element("length", bytes);
      //ret.element("lastModified", lastModified());
    }
    return ret;
  }


}
