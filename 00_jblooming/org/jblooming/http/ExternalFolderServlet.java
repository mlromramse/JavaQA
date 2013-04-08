package org.jblooming.http;

import org.jblooming.utilities.file.FileUtilities;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class ExternalFolderServlet extends HttpServlet {
  File fsroot;
  File httproot;
  String httpRootPrefix;
  boolean processJsp;
  boolean allowCache;

  public void init(ServletConfig servletConfig) throws ServletException {
    try {
      httpRootPrefix = servletConfig.getInitParameter("http-work-folder");
      if (httpRootPrefix == null || httpRootPrefix.trim().length() == 0)
        httpRootPrefix = "work";
      httpRootPrefix = '/' + httpRootPrefix.trim();
      String fsRootName = servletConfig.getInitParameter("external-folders-root");
      if (fsRootName == null)
        throw new ServletException("You must specify a filesystem folder ( 'external-folders-root' parameter ) ");
      fsroot = new File(fsRootName).getCanonicalFile();
      httproot = new File(servletConfig.getServletContext().getRealPath(httpRootPrefix)).getCanonicalFile();
      if ("true".equalsIgnoreCase(servletConfig.getInitParameter("create-roots"))) {
        fsroot.mkdirs();
        httproot.mkdirs();
      }
      processJsp = "true".equalsIgnoreCase(servletConfig.getInitParameter("compile-jsp"));
      if (processJsp) {
        ServletContext servletContext = servletConfig.getServletContext();
        int major = servletContext.getMajorVersion();
        int minor = servletContext.getMinorVersion();
        if (major < 2 || (major == 2 && minor < 3)) {
          throw new ServletException(getClass().getName() + " needs servlets 2.3 or newer, current vesion is " + major + '.' + minor);
        }
      }
      allowCache = "true".equalsIgnoreCase(servletConfig.getInitParameter("allow-cache"));
      if (!fsroot.exists() ||
              !fsroot.isDirectory() ||
              !httproot.exists() ||
              !httproot.isDirectory())
        throw new ServletException("Cannot find repository");
    } catch (IOException e) {
      throw new ServletException(e);
    }
  }

  protected void doPost(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {
    perform(httpServletRequest, httpServletResponse);
  }

  protected void doGet(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {
    perform(httpServletRequest, httpServletResponse);
  }

  private void perform(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {
    String resource;
    boolean resourceIsIncluded = false;
    resource = (String) httpServletRequest.getAttribute("javax.servlet.include.request_uri");
    if (resource == null)
      resource = (String) httpServletRequest.getAttribute("javax.servlet.forward.request_uri");
    else
      resourceIsIncluded = true;
    if (resource == null)
      resource = httpServletRequest.getRequestURI();
    else
      resourceIsIncluded = true;

    resource = java.net.URLDecoder.decode(resource);
    String fsresource = resource.replace('/', File.separatorChar);
    String httpresource = httpRootPrefix + resource;
    File fsfile = new File(fsroot, fsresource);
    if (!fsfile.exists() || !fsfile.isFile()) {
      httpServletResponse.sendError(404, "Department " + resource + " not found on this server");
      return;
    }
    if (processJsp && resource.toUpperCase().endsWith(".JSP")) {
      File httpfile = new File(httproot, fsresource);
      manageDuplication(fsfile, httpfile);
      RequestDispatcher requestDispatcher = httpServletRequest.getRequestDispatcher(httpresource);
      if (resourceIsIncluded)
        requestDispatcher.include(httpServletRequest, httpServletResponse);
      else
        requestDispatcher.forward(httpServletRequest, httpServletResponse);
    } else {
      ZipServe.serve(fsfile, httpServletRequest, httpServletResponse, allowCache, resourceIsIncluded);
    }
  }

  private void manageDuplication(File fsfile, File httpfile) throws IOException {
    if (isDuplicationNeeded(httpfile, fsfile)) {
      duplicate(httpfile, fsfile);
    }
  }

  private synchronized void duplicate(File httpfile, File fsfile) throws IOException {
    if (isDuplicationNeeded(httpfile, fsfile)) {
      httpfile.getParentFile().mkdirs();
      FileInputStream source = new FileInputStream(fsfile);
      FileOutputStream dest = new FileOutputStream(httpfile);
      FileUtilities.copy(source, dest);
    }
  }

  private boolean isDuplicationNeeded(File httpfile, File fsfile) {
    boolean duplicate = !httpfile.exists();
    if (!duplicate) {
      long fsdate = fsfile.lastModified();
      long httpdate = httpfile.lastModified();
      duplicate = fsdate >= httpdate;
    }
    return duplicate;
  }
}
