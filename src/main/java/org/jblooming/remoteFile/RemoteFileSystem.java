package org.jblooming.remoteFile;

import org.jblooming.PlatformRuntimeException;
import org.jblooming.utilities.file.FileUtilities;
import org.jblooming.waf.UploadHelper;

import java.io.*;
import java.util.*;

/**
 * @author Pietro Polsinelli ppolsinelli@open-lab.com
 * @author Roberto Bicchierai rbicchierai@open-lab.com
 */
public class RemoteFileSystem extends RemoteFile {

  private File file;

  public RemoteFileSystem(Document document) {
    super(document);
  }

  private File getClient() {
    if (file == null)
      connect();
    return file;
  }

  public String getName() {
    return getClient().getName();
  }

  public String getParent() {
    return getClient().getParent();
  }

  public RemoteFile getParentFile() {
    RemoteFileSystem parent = new RemoteFileSystem(document);
    parent.file = getClient().getParentFile();
    //parent.setLoggedOperator(getLoggedOperator());
    return parent;
  }

  public boolean isRoot() {
    return super.isRoot() || getClient().getParent() == null;
  }


  public String getRelativePath() {
    return getClient().getPath();
  }

  public boolean canWrite() {
    return getClient().canWrite();

  }

  public boolean exists() {
    return getClient().exists();
  }

  public boolean isDirectory() {
    return getClient().isDirectory();
  }

  public long lastModified() {
    return getClient().lastModified();
  }

  public long length() {
    return getClient().length();
  }

  public boolean delete() {
    //return getClient().delete();
    File client = getClient();
    if (client.isDirectory())
      FileUtilities.tryHardToDeleteDir(client);
    else
      FileUtilities.tryHardToDeleteFile(client);
    return true;
  }

  public List<String> list() {
    return Arrays.asList(getClient().list());
  }

  public List<RemoteFile> listFiles() {
    File[] fls = getClient().listFiles();
    List<File> files = new ArrayList();
    Collections.addAll(files, fls);
    Collections.sort(files, new FileNameComparator());
    List<RemoteFile> rf = new ArrayList<RemoteFile>();
    for (File fl : files) {
      RemoteFileSystem rfs = new RemoteFileSystem(document);
      //rfs.setLoggedOperator(getLoggedOperator());
      rfs.file = fl;
      rf.add(rfs);
    }
    return rf;
  }

  public boolean mkdir() {
    return getClient().mkdir();
  }

  public boolean mkdirs() {
    return getClient().mkdirs();
  }

  public boolean renameTo(RemoteFile dest) {
    return getClient().renameTo(((RemoteFileSystem) dest).getClient());
  }

  public boolean connect() {
    if (document.getContent() != null) {
      file = new File(document.getContent());
      return file.exists();
    } else {
      file = new File("");
      return false;
    }
  }

  public boolean disconnect() {
    file = null;
    return true;
  }

  public boolean setTarget(String path) {
    file = new File(document.getContent() + File.separator + path);
    try {
      if (file.getCanonicalPath().toLowerCase().startsWith(document.getContent().toLowerCase()))
        return true;
      else {
        file = new File(document.getContent());
        return false;
      }
    } catch (IOException e) {
      return false;
    }
  }

  public File downloadCopy() throws IOException {
    if (getClient().isDirectory())
      throw new PlatformRuntimeException("Silly developers try to download directories " + getClient().getPath());
    File tmp = File.createTempFile(getClient().getName(), "");
    tmp.deleteOnExit();
    FileUtilities.mycopy(getClient(), tmp);
    return tmp;
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
    return new FileInputStream(getClient());
  }

  public void upload(UploadHelper uploader) throws IOException {
    if (uploader != null && uploader.temporaryFile != null) {
      FileUtilities.copy(new FileInputStream(uploader.temporaryFile), new FileOutputStream(getClient()));
    } else {
      throw new IOException("Invalid uploadHelper.");
    }
  }

  public static class FileNameComparator implements Comparator {

    public int compare(Object f1, Object f2) {
      return ((File) f1).getName().compareToIgnoreCase(((File) f2).getName());
    }
  }

}
