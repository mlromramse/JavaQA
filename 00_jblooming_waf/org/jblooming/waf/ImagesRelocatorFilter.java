package org.jblooming.waf;

import org.jblooming.utilities.file.FileUtilities;
import org.jblooming.utilities.HttpUtilities;
import org.jblooming.utilities.StringUtilities;
import org.jblooming.tracer.Tracer;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

/**
 * ImagesRelocatorFilter (c) 2005 - Open Lab - www.open-lab.com
 */
public class ImagesRelocatorFilter implements Filter {

  public static String newPhysicalPath = null;
  public static String oldVirtualPath = null;
  public static String targetFolder = "uploaded_img";


  public void init(FilterConfig filterConfig) throws ServletException {
  }

  public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws ServletException, IOException {

    if (newPhysicalPath!=null && oldVirtualPath!=null) {
      HttpServletRequest request = (HttpServletRequest) servletRequest;
      HttpServletResponse response = (HttpServletResponse) servletResponse;
      String uri = request.getRequestURI();

      String extension = FileUtilities.getFileExt(uri);
      boolean resourceIsImage = FileUtilities.isImageByFileExt(extension);
      boolean resourceIsExternal = uri.indexOf(StringUtilities.replaceAllNoRegex(oldVirtualPath, File.separator, "/"))>-1;

      if(resourceIsImage && resourceIsExternal)
        drawImage(uri, extension, response);
      else
        filterChain.doFilter(servletRequest, servletResponse);

    } else {
      filterChain.doFilter(servletRequest, servletResponse);
    }
  }

  public void destroy() {
  }

  private void drawImage (String uri, String extension, HttpServletResponse response) throws FileNotFoundException {
    response.setContentType(HttpUtilities.getContentType(extension));
    int start = uri.lastIndexOf("/")+1;
    String imageName = uri.substring(start, uri.length());
    // as newPhysicalPath is static it is initialized only at runtime thus it is NOT build according to application settings
    String imagePath = newPhysicalPath + (newPhysicalPath.endsWith(File.separator) ? "" : File.separator) + imageName;
    InputStream inputStream = new FileInputStream(imagePath);
    try {
      FileUtilities.writeStream(inputStream, response.getOutputStream());
      inputStream.close();
    } catch (IOException e) {
      Tracer.platformLogger.error("ImagesRelocatorFilter:: unable to draw resource " + imagePath);
    }

  }

}