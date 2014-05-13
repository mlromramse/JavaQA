package org.jblooming.remoteFile;

import org.jblooming.waf.UploadHelper;
import org.jblooming.tracer.Tracer;
import org.jblooming.PlatformRuntimeException;
import org.jblooming.operator.Operator;
import org.jblooming.utilities.DateUtilities;
import org.jblooming.utilities.StringUtilities;
import org.jblooming.utilities.file.FileUtilities;
import org.jblooming.agenda.CompanyCalendar;

import java.util.List;
import java.util.ArrayList;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.net.URL;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.text.ParseException;

public class RemoteFileService extends RemoteFile {
  private URLConnection hpCon;//= urlToOpen.openConnection();
  //            InputStream input = hpCon.getInputStream();
  private URL urlToOpen;
  private static final String DIRSEPARATOR = "d";
  private static final String FILESEPARATOR = "f";//todo
  private static final String USERSEPARATOR = "u";//todo
  private static final String DATEWGEIHTSEPARATOR = "*";//todo
  private static final String SERVSEPARATOR = "!";//todo
  private static final String FILELINKSEPARATOR = "|";//todo

  private String relativePath = null;
  // private boolean directory = true;
  private long lastModified = 0;
  private long length = 0;
  private String linkForFile;
  private boolean isdirectory = true;

  private String remoteHostRoot;

  public RemoteFileService(Document document) {
    super(document);
  }


  private URL getClient() {
    if ((this.relativePath == null || this.relativePath.trim().length() <= 0))
      setTarget(document.getContent());
    else if (!relativePath.startsWith(document.getContent()))
      setTarget(document.getContent() + this.relativePath);
    if (urlToOpen == null)
      connect();
    return urlToOpen;
  }

  public boolean exists() {
    return (this.document.getConnectionHost() != null && this.document.getConnectionHost().trim().length() > 0);  //todo
  }

  public String getName() {
    //getClient();
    String name = "";
    //if (!relativePath.equals(document.getContent()))
    name = relativePath.substring(relativePath.lastIndexOf(File.separator) + 1);
    //else
    name = name.substring(name.lastIndexOf("/") + 1);

    return name;
  }

  public boolean connect() {
    throw new PlatformRuntimeException("REMETE FILE SERVICE NO LONGER SUPPORTED");
   /*
    boolean ret = false;
    String dir = "";
    if (document.getConnectionHost() != null) {
      dir = (relativePath != null && !relativePath.trim().equals("\\") && !relativePath.trim().equals("/")) ? relativePath : "";
      try {
        // if(isDirectory())
        Operator usr = this.getLoggedOperator();
        int posu = usr != null ? (usr.getLoginName().toUpperCase().trim().indexOf("EU\\")) : -1;
        String usrname = usr != null ? (posu >= 0 ? usr.getLoginName().substring(3) : usr.getLoginName()) : "";
        urlToOpen = new URL(document.getConnectionHost() + "?" + DIRSEPARATOR + "=" + URLEncoder.encode(dir) + "&" + USERSEPARATOR + "=" + usrname);
        String[] parts=document.getConnectionHost().split("/");
        remoteHostRoot= parts[0]+"//"+parts[2];
        // else
        // urlToOpen = new URL(document.getConnectionHost() + "?" + FILESEPARATOR + "=" + dir);
      } catch (MalformedURLException e) {
        Tracer.platformLogger.error(" Malformed URL ", e);
      }
      try {
        hpCon = urlToOpen.openConnection();
        ret = true;
      } catch (IOException e) {
        Tracer.platformLogger.error(" Cannot connect ", e);
      }
    }
    return ret;*/
  }


  public List<RemoteFile> listFiles() {
    StringBuffer str = new StringBuffer();
    int c;
    try {
      getClient();
      InputStream input = hpCon.getInputStream();
      while (((c = input.read()) != -1)) {
        str.append((char) c);
      }
      input.close();
      String[] ar = str.toString().split(SERVSEPARATOR);

      List<RemoteFile> rf = new ArrayList<RemoteFile>();
      for (int i = 0; i < ar.length; i++) {

        if (ar[i] != null && ar[i].trim().length() > 0) {
          Document doc = document;
          String fileindex = ar[i].substring(0, DIRSEPARATOR.length());
          int pos = ar[i].indexOf(DATEWGEIHTSEPARATOR);
          String filename1 = ar[i];
          if (pos > 0)
            filename1 = ar[i].substring(0, ar[i].indexOf(DATEWGEIHTSEPARATOR));
          String filename = filename1.substring(DIRSEPARATOR.length());

          String datewg = ar[i].substring(ar[i].indexOf(DATEWGEIHTSEPARATOR) + 1);
          RemoteFileService rfs = new RemoteFileService(doc);
          rfs.urlToOpen=this.urlToOpen;
          //rfs.setLoggedOperator(this.getLoggedOperator());

          if (!relativePath.startsWith(document.getContent()))
            rfs.setTarget(document.getContent() + (relativePath != null ? relativePath : "") + File.separator + filename);
          else
            rfs.setTarget((relativePath != null ? relativePath : "") + File.separator + filename);
          CompanyCalendar cc = new CompanyCalendar();
          if (fileindex.equals(DIRSEPARATOR)) {
            rfs.setDirectory(true);
            if (datewg.trim().length() > 0) {
              try {
                cc.setTime(DateUtilities.dateFromString(datewg));
                rfs.setlastModified(cc.getTimeInMillis());
              } catch (ParseException e) {
                Tracer.platformLogger.error("Cannot get date file for " + relativePath, e);
              }
            }
          } else {// caso file
            rfs.setDirectory(false);
            if (datewg.trim().length() > 0) {
              pos = datewg.indexOf(DATEWGEIHTSEPARATOR);
              String dat = datewg;
              if (pos >= 0) {
                if (pos > 0) {
                  dat = datewg.substring(0, pos);
                  datewg = datewg.substring(pos + 1);
                  try {
                    cc.setTime(DateUtilities.dateFromString(dat));
                    rfs.setlastModified(cc.getTimeInMillis());

                  } catch (ParseException e) {
                    Tracer.platformLogger.error("Cannot get date file for " + relativePath, e);
                  }
                  pos = datewg.indexOf(DATEWGEIHTSEPARATOR);
//                  if (pos > 0) {
//                    String wg = datewg.substring(0, pos);
                  if (datewg.trim().length() > 0) {
//                    if (wg.trim().length() > 0) {
                    long length = Long.parseLong(datewg);
                    rfs.setlength(length);
                  }
                  //}
                  /*String link = datewg.substring(pos + 1);
                  if (link.endsWith(SERVSEPARATOR))
                    link = link.substring(1, link.length() - 1);*/
                  rfs.setLinkForFile(remoteHostRoot+ StringUtilities.replaceAllNoRegex(rfs.getRelativePath(),File.separator,"/"));
                }

              } else {
                rfs.setLinkForFile(str.toString());
              }
            }
          }
          rf.add(rfs);
        }
      }
      return rf;

    } catch (IOException e) {
      Tracer.platformLogger.error("Cannot get listFiles for " + relativePath, e);
    }
    return null;
  }


  public boolean setTarget(String path) {
    relativePath = path;
   // getClient();
    return true;
  }


  public String getParent() {
   // getClient();
    if (relativePath != null && relativePath.length() > 0) {
      if (!relativePath.startsWith(document.getContent()))
        return document.getContent() + relativePath.substring(0, relativePath.lastIndexOf(File.separator));
      else
        return relativePath.substring(0, relativePath.lastIndexOf(File.separator));
    }
    return null;
  }

  public boolean isRoot() {
    //getClient();
    return relativePath.equals(document.getContent());
  }
//
//  public void setDirectory(boolean directory) {
//    this.directory = directory;
//  }

  public boolean isDirectory() {
   // getClient();
    // int pos=relativePath.lastIndexOf(".");
    // return (pos<0);  //todo change body of implemented methods use File | Settings | File Templates.
    return this.isdirectory;
  }

  public void setDirectory(boolean isdirectory) {
    this.isdirectory = isdirectory;
  }

  public String getRelativePath() {
    //getClient();
    return relativePath;
  }


  public RemoteFile getParentFile() {
    RemoteFileService parent = null;
    //getClient();
    parent = new RemoteFileService(document);
    if (relativePath != null && relativePath.length() > 0)
      if (!relativePath.startsWith(document.getContent()))
        parent.relativePath = document.getContent() + relativePath.substring(0, relativePath.lastIndexOf(File.separator));
      else {
        // int pos=relativePath.indexOf(File.separator);
        int pos = relativePath.lastIndexOf(File.separator);
        if (pos >= 0)
          parent.relativePath = relativePath.substring(0, relativePath.lastIndexOf(File.separator));
        else
          parent.relativePath = relativePath.substring(0, relativePath.lastIndexOf("/"));


      }

    //parent.relativePath = relativePath.substring(0, relativePath.lastIndexOf(File.separator));
    return parent;
  }

  public void setlastModified(long lastModified) {
    this.lastModified = lastModified;
  }

  public long lastModified() {
    return lastModified;
  }

  public void setlength(long length) {
    this.length = length;
  }

  public long length() {
    return length;
  }

  public boolean delete() {
    return false;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public List<String> list() {
    this.getClient();
    List lst = new ArrayList();
    lst.add(this.getFileLink());
    return lst;  //To change body of implemented methods use File | Settings | File Templates.
  }


  public boolean mkdir() {
    return false;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public boolean mkdirs() {
    return false;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public boolean renameTo(RemoteFile dest) {
    return false;  //To change body of implemented methods use File | Settings | File Templates.
  }


  public boolean disconnect() {
    return true;
  }


  public File downloadCopy() throws IOException {
    this.getClient();
    if (this.isDirectory())
      throw new PlatformRuntimeException("Silly developers try to download directories " + getClient().getPath());
    File tmp = File.createTempFile(this.getName(), "");
    tmp.deleteOnExit();
    //FileUtilities.mycopy(getClient(),tmp);
    return tmp;
  }


  public boolean canMakeDir() {
    return false;
  }

  public boolean canZipFiles() {
    return false;
  }

  public boolean canDeleteFiles() {
    return false;
  }

  public boolean canUploadFiles() {
    return false;
  }

  public InputStream getRemoteInputStream() throws IOException {
    getClient();
    this.getFileLink();
    return hpCon.getInputStream();
  }

  public void upload(UploadHelper uploader) throws IOException {

  }


  public boolean canWrite() {
    return false;
  }


  public String getFileLink() {
    return linkForFile;
  }

  public void setLinkForFile(String linkForFile) {
    this.linkForFile = linkForFile;
  }
}
