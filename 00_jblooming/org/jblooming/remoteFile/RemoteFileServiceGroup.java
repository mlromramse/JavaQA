package org.jblooming.remoteFile;

import org.jblooming.operator.Operator;
import org.jblooming.tracer.Tracer;
import org.jblooming.agenda.CompanyCalendar;
import org.jblooming.utilities.DateUtilities;
import org.jblooming.utilities.HashTable;
import org.jblooming.PlatformRuntimeException;
import org.jblooming.waf.UploadHelper;

import java.net.URLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.net.MalformedURLException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.text.ParseException;

public class RemoteFileServiceGroup extends RemoteFileService {
    private static final String HOSTSEPARATOR = ";";

    List<RemoteFileService> remoteFileService = new ArrayList<RemoteFileService>();
    private String relativePath = null;
    // private boolean directory = true;
    private long lastModified = 0;
    private long length = 0;
    private boolean  directory =true ;

    public RemoteFileServiceGroup(Document document) {
        super(document);
    }


    private List<RemoteFileService> getClient() {
        if ((this.relativePath == null || this.relativePath.trim().length() <= 0))
            setTarget(document.getContent());
        else if (!relativePath.startsWith(document.getContent()))
            setTarget(document.getContent() + this.relativePath);
        if (remoteFileService.size() < 1)
            connect();
        return remoteFileService;
    }

    public boolean exists() {
        return (this.document.getConnectionHost() != null && this.document.getConnectionHost().trim().length() > 0);
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
        boolean ret = false;
        String dir = "";
        //URL urlToOpen;

        if (exists()) {
            String connectionHost = document.getConnectionHost();
            String connectionPwd = document.getConnectionPwd();
            String connectionUser = document.getConnectionUser();
            String content = document.getContent();
            String code = document.getCode();
            String[] connectionHosts = connectionHost.split(HOSTSEPARATOR);
            String[] codes = code.split(HOSTSEPARATOR);
            String[] connectionPwds = (connectionPwd != null) ? connectionPwd.split(HOSTSEPARATOR) : null;
            String[] connectionUsers = (connectionUser != null) ? connectionUser.split(HOSTSEPARATOR) : null;
            String[] contents = (content != null) ? content.split(HOSTSEPARATOR) : null;
            String cont = null;
            String conUser = null;
            String conPwd = null;
            String cod = null;
            for (int i = 0; i < connectionHosts.length; i++) {
                String conHost = connectionHosts[i];
                conPwd = (connectionPwd != null && connectionPwds.length > i) ? connectionPwds[i] : conPwd;
                conUser = (connectionUser != null && connectionUsers.length > i) ? connectionUsers[i] : conUser;
                cont = (content != null && contents.length > i) ? contents[i] : cont;
                cod = (code != null && codes.length > i) ? codes[i] : cod;
                if (conHost != null && conHost.trim().length() > 0) {
                    FileStorage doc = new FileStorage();
                    doc.setConnType(document.getConnType());
                    doc.setContent(document.getContent());
                    doc.setName(document.getName());
                    doc.setCode(cod);
                    doc.setConnectionHost(conHost);
                    doc.setConnectionPwd(conPwd);
                    doc.setConnectionUser(conUser);
                    // doc.setContent(getName());
                    RemoteFileService rfs = new RemoteFileService(doc);
                    //rfs.setLoggedOperator(this.getLoggedOperator());
                    rfs.setTarget(this.relativePath);
                    remoteFileService.add(rfs);
                }

            }
        }
        return ret;
    }

    public class RemoteFileDescComp implements Comparator {

        public int compare(Object first, Object second) {
            String o1 = ((RemoteFileService) first).getRelativePath();
            String o2 = ((RemoteFileService) second).getRelativePath();
            return (o1.compareToIgnoreCase(o2));
        }
    }

    private List<RemoteFile> addToSet(List<RemoteFile> lst) {
        List<RemoteFile> lstR= new ArrayList<RemoteFile>();
        Map<String, RemoteFile>arg = new HashTable<String, RemoteFile>();
        for (int i = 0; i < lst.size(); i++) {
            RemoteFile remoteFile = lst.get(i);
            String key = remoteFile.getRelativePath();
            if (remoteFile.isDirectory()){
                if (!arg.containsKey(key)){
                    arg.put(key, remoteFile);
                    lstR.add(remoteFile);
                }
            }else {
                    remoteFile.document.setConnType(Document.ConnectionType.SERVICE);
                    lstR.add(remoteFile);
                        arg.put(key, remoteFile);
                }

        }
         Collections.sort(lstR,new RemoteFileDescComp());
        return lstR;
    }

    public List<RemoteFile> listFiles() {
        List<RemoteFile> rf = new ArrayList<RemoteFile>();

        getClient();
        if (remoteFileService.size() > 0) {
            for (int i = 0; i < remoteFileService.size(); i++) {
                RemoteFileService remoteFile = remoteFileService.get(i);
                //remoteFile.setLoggedOperator( this.getLoggedOperator());

                //remoteFile.document.setConnectionHost(this.document.getConnectionHost());
                rf.addAll(remoteFile.listFiles());
            }
        }

        return addToSet(rf);
    }


    public boolean setTarget(String path) {
        relativePath = path;
        //getClient();
        return true;
    }


    public String getParent() {
        //getClient();
        if (relativePath != null && relativePath.length() > 0) {
            if (!relativePath.startsWith(document.getContent()))
                return document.getContent() + relativePath.substring(0, relativePath.lastIndexOf(File.separator));
            else
                return relativePath.substring(0, relativePath.lastIndexOf(File.separator));
        }
        return null;
    }

    public boolean isRoot() {
       // getClient();
        return relativePath.equals(document.getContent());
    }

    public boolean isDirectory() {
        //getClient();
        //int pos = relativePath.lastIndexOf(".");
        // return (pos<0);
           return this.directory;
    }



    public void setDirectory(boolean directory) {
        this.directory = directory;
    }

    public String getRelativePath() {
        //getClient();
        return relativePath;
    }


    public RemoteFile getParentFile() {
        RemoteFileServiceGroup parent = null;
        //getClient();
        parent = new RemoteFileServiceGroup(document);
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
        return null;  //To change body of implemented methods use File | Settings | File Templates.
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
            throw new PlatformRuntimeException("Silly developers try to download directories ");
        //throw new PlatformRuntimeException("Silly developers try to download directories " + getClient().getPath());
        File tmp = File.createTempFile(this.getName(), "");
      tmp.deleteOnExit();

        //FileUtilities.mycopy(getClient(),tmp);
        return tmp;
    }

    public boolean canMakeDir() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean canZipFiles() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean canDeleteFiles() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean canUploadFiles() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public InputStream getRemoteInputStream() throws IOException {
        getClient();
        return null;   //To change body of implemented methods use File | Settings | File Templates.
    }

    public void upload(UploadHelper uploader) throws IOException {
        //To change body of implemented methods use File | Settings | File Templates.
    }


    public boolean canWrite() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }


}
