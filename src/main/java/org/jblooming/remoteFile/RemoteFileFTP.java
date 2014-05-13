package org.jblooming.remoteFile;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.jblooming.PlatformRuntimeException;
import org.jblooming.tracer.Tracer;
import org.jblooming.waf.UploadHelper;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class RemoteFileFTP extends RemoteFile {

  private FTPClient ftpClient = null;
  private FTPFile[] ftpFileList = null;
  private String relativePath = null;

  private FTPClient getClient() {
    if (ftpClient == null)
      connect();
    return ftpClient;
  }


  public RemoteFileFTP(Document document) {
    super(document);
  }

  public String getName() {
    getClient();
    return document.getContent() + "/" + relativePath;
  }

  public String getParent() {
    if (relativePath != null && relativePath.length() > 0)
      return document.getContent() + "/" + relativePath.substring(0, relativePath.lastIndexOf("/"));
    return null;
  }

  public RemoteFile getParentFile() {
    RemoteFileFTP parent = null;
    parent = new RemoteFileFTP(document);
    if (relativePath != null && relativePath.length() > 0)
      parent.relativePath = relativePath.substring(0, relativePath.lastIndexOf("/"));
    return parent;
  }

  public String getRelativePath() {
    getClient();
    return relativePath;
  }

  public boolean canWrite() {
    return true;  //Todo change body of implemented methods use File | Settings | File Templates.
  }

  public boolean exists() {
    getClient();
    return ftpFileList.length > 0;
  }

  public boolean isDirectory() {
    getClient();
    return ftpFileList[0].isDirectory();
  }

  public long lastModified() {
    getClient();
    return ftpFileList[0].getTimestamp().getTimeInMillis();
  }

  public long length() {
    getClient();
    return ftpFileList[0].getSize();
  }

  public boolean delete() {
    boolean ret = false;
    try {
      if (isDirectory())
        getClient().removeDirectory(ftpFileList[0].getName());
      else
        getClient().deleteFile(ftpFileList[0].getName());
      ret = true;
    } catch (IOException e) {
      Tracer.platformLogger.error("Cannot delete " + ftpFileList[0].getName(), e);
    }
    return ret;

  }

  public List<String> list() {
    try {
      return Arrays.asList(getClient().listNames(relativePath));
    } catch (IOException e) {
      Tracer.platformLogger.error("Cannot get listNames for " + relativePath, e);
      return null;
    }
  }

  public List<RemoteFile> listFiles() {
    try {
      FTPFile[] fls = new FTPFile[0];
      fls = getClient().listFiles();
      List<RemoteFile> rf = new ArrayList<RemoteFile>();
      for (int i = 0; i < fls.length; i++) {
        FTPFile fl = fls[i];
        RemoteFileFTP rfs = new RemoteFileFTP(document);
        rfs.setTarget(relativePath + "/" + fl.getName());
        rf.add(rfs);
      }
      return rf;

    } catch (IOException e) {
      Tracer.platformLogger.error("Cannot get listFiles for " + relativePath, e);
    }
    return null;
  }

  public boolean mkdir() {
    boolean ret = false;
    try {
      getClient().makeDirectory(relativePath);
      ret = true;
    } catch (IOException e) {
      Tracer.platformLogger.error("Cannot create directory " + relativePath, e);
    }
    return ret;
  }

  public boolean mkdirs() {
    return false;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public boolean renameTo(RemoteFile dest) {
    return false;  //To change body of implemented methods use File | Settings | File Templates.
  }


  public boolean connect() {
    boolean ret = false;
    if (document.getConnectionHost() != null) {
      ftpClient = new FTPClient();
      try {
        ftpClient.connect(document.getConnectionHost());
        ftpClient.user(document.getConnectionUser());
        ftpClient.pass(document.getConnectionPwd());
        ftpClient.setFileTransferMode(FTPClient.BINARY_FILE_TYPE);
        relativePath = "/";
        setTarget(relativePath);
        ret = true;
      } catch (IOException e) {
        Tracer.platformLogger.error("Cannot connect", e);
      }
    }
    return ret;
  }

  public boolean disconnect() {
    try {
      getClient().disconnect();
      return true;
    } catch (IOException e) {
      Tracer.platformLogger.error("Cannot disconnect", e);
      return false;
    }
  }

  public boolean setTarget(String path) {
    boolean ret = false;
    try {
      ftpFileList = getClient().listFiles("");
      relativePath = path;
      ret = true;
    } catch (IOException e) {
      throw new PlatformRuntimeException(e);
    }
    return ret;
  }

  public File downloadCopy() throws IOException {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public boolean canMakeDir() {
    return true;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public boolean canZipFiles() {
    return true;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public boolean canDeleteFiles() {
    return true;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public boolean canUploadFiles() {
    return true;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public InputStream getRemoteInputStream() throws IOException {
    return null; //To change body of implemented methods use File | Settings | File Templates.
  }

  public void upload(UploadHelper uploader) throws IOException {
    //To change body of implemented methods use File | Settings | File Templates.
  }
}
