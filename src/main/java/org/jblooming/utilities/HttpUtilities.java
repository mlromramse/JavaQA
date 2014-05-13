package org.jblooming.utilities;

import org.jblooming.PlatformRuntimeException;
import org.jblooming.utilities.file.FileUtilities;
import org.jblooming.tracer.Tracer;
import org.jblooming.system.SystemConstants;
import org.jblooming.waf.settings.ApplicationState;
import org.apache.commons.validator.EmailValidator;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.httpclient.methods.GetMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;
import javax.servlet.ServletContext;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.BufferedReader;
import java.util.Enumeration;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.regex.PatternSyntaxException;
import java.net.URL;
import java.net.MalformedURLException;

import net.sf.json.JSON;
import net.sf.json.JSONObject;


public class HttpUtilities {

  public static String getRequestBody(HttpServletRequest request) {
    String body = "";
    try {
      StringBuilder sb = new StringBuilder();
      String line = null;

      BufferedReader reader = request.getReader();
      while ((line = reader.readLine()) != null) {
        sb.append(line + "\n");
      }
      reader.close();

      body = sb.toString();
    } catch(Exception e) {
      Tracer.platformLogger.error(e);
    }
    return body;
  }

  public static String getCompleteFileSystemPath(String webPath, ServletContext pc) {
    return StringUtilities.replaceAllNoRegex(StringUtilities.replaceAllNoRegex(pc.getRealPath(webPath), "/", File.separator), "\\", File.separator);
  }

  /**
   * //issue_WW2_1
   *
   * @param request
   * @return the result is in lower case. This is the file on which this method is called
   */
  public static String getCanonicalFileSystemPathOfPartFromURI(HttpServletRequest request) {
    String key = realURI(request);
    key = request.getSession(true).getServletContext().getRealPath(key);
    try {
      File file = new File(key);
      key = file.getCanonicalPath();
      if (file.isDirectory() && !key.endsWith(File.separator))
        key = key + File.separator;
    } catch (IOException e) {
      throw new PlatformRuntimeException(e);
    }
    return key.toLowerCase();
  }

  /**
   * @param request
   * @return This is the file of request.getRequestURI()
   */
  public static String getCanonicalFileSystemPathFromURI(HttpServletRequest request) {
    String key = request.getRequestURI().substring(request.getContextPath().length());
    key = request.getSession(true).getServletContext().getRealPath(key);
    try {
      key = new File(key).getCanonicalPath();
    } catch (IOException e) {
      throw new PlatformRuntimeException(e);
    }
    return key;
  }

  /**
   * //issue_WW2_1
   *
   * @param request
   * @return the result is in lower case
   */
  public static String getFileSystemRootPathForRequest(HttpServletRequest request) {
    String key = request.getSession(true).getServletContext().getRealPath("/");
    try {
      key = new File(key).getCanonicalPath();
    } catch (IOException e) {
      throw new PlatformRuntimeException(e);
    }
    return key+File.separator;
  }


  public static String getFileNameFromUri(HttpServletRequest request) {
    String uri = request.getRequestURI();
    return uri.substring(uri.lastIndexOf("/") + 1, uri.length());
  }

  public static String getContentType(String fileExtension) {
    fileExtension = fileExtension.toLowerCase().trim().replaceAll("\\.", "");

// images
    if (fileExtension.endsWith("jpg") || fileExtension.endsWith("jpeg") || fileExtension.endsWith("jpe"))
      return "image/jpeg";
    else if (fileExtension.endsWith("gif"))
      return "image/gif";
    else if (fileExtension.endsWith("png"))
      return "image/png";
    else if (fileExtension.endsWith("bmp"))
      return "image/bmp";
    else if (fileExtension.endsWith("tiff") || fileExtension.endsWith("tif"))
      return "image/tiff";
    else if (fileExtension.endsWith("svg"))
      return "image/svg+xml";
    else if (fileExtension.endsWith("dwg"))
      return "image/x-dwg";
    else if (fileExtension.endsWith("dxf"))
      return "image/x-dxf";

// graphics
    else if (fileExtension.endsWith("ai"))
      return "application/illustrator";
    else if (fileExtension.endsWith("psd"))
      return "application/photoshop";
    else if (fileExtension.endsWith("dwg"))
      return "application/acad";
    else if (fileExtension.endsWith("dxf"))
      return "application/dxf";
    else if (fileExtension.endsWith("fla"))
      return "application/octet-stream";

// multimedia
    else if (fileExtension.endsWith("mp3") || fileExtension.endsWith("mp4"))
      return "audio/mpeg";
    else if (fileExtension.endsWith("avi"))
      return "video/x-msvideo";
    else if (fileExtension.endsWith("mov") || fileExtension.endsWith("qt"))
      return "video/quicktime";
    else if (fileExtension.endsWith("mpg") || fileExtension.endsWith("mpeg") || fileExtension.endsWith("mpe"))
      return "video/mpeg";
    else if (fileExtension.endsWith("ra") || fileExtension.endsWith("ram"))
      return "application/x-pn-realaudio ";
    else if (fileExtension.endsWith("rm") || fileExtension.endsWith("rpm"))
      return "application/x-pn-realaudio-plugin";
    else if (fileExtension.endsWith("mid") || fileExtension.endsWith("midi"))
      return "audio/x-midi";
    else if (fileExtension.endsWith("wmv") )
      return "video/x-ms-wmv";
    else if (fileExtension.endsWith("swf"))
      return "application/x-shockwave-flash";

// office
    else if (fileExtension.endsWith("pdf") )
      return "application/pdf";

    else if (fileExtension.endsWith("ps") )
      return "application/postscript";

    else if (fileExtension.endsWith("rtf"))
      return "text/richtext";
    else if (fileExtension.endsWith("xl") || fileExtension.endsWith("xls") || fileExtension.endsWith("xlv") || fileExtension.endsWith("xla")
            || fileExtension.endsWith("xlb") || fileExtension.endsWith("xlt") || fileExtension.endsWith("xlm") || fileExtension.endsWith("xlk")
            || fileExtension.endsWith("xlsx") || fileExtension.endsWith("xltx") || fileExtension.endsWith("xlsEmb"))
      return "application/excel";
    else if (fileExtension.endsWith("doc") || fileExtension.endsWith("dot") || fileExtension.endsWith("docx") || fileExtension.endsWith("dotx") || fileExtension.endsWith("docEmb"))
      return "application/msword";
    else if (fileExtension.endsWith("mdb"))
      return "application/msaccess";
    else if (fileExtension.endsWith("ppt") || fileExtension.endsWith("pptx"))
      return "application/mspowerpoint";
    else if (fileExtension.endsWith("mpx"))
      return "application/vnd.ms-project";// return "application/msproject";

// apps
    else if (fileExtension.endsWith("zip"))
      return "application/zip";
    else if (fileExtension.endsWith("fh10") || fileExtension.endsWith("fh9") || fileExtension.endsWith("fh11"))
      return "image/x-freehand";

    else if (fileExtension.endsWith("exe") || fileExtension.endsWith("dll"))
      return "application/octet-stream";

// html xml
    else if (fileExtension.endsWith("htm") || fileExtension.endsWith("html") || fileExtension.endsWith("shtml"))
      return "text/html";
    else if (fileExtension.endsWith("xml"))
      return "text/xml";
    else if (fileExtension.endsWith("as"))
      return "text/xml";

// plain text
    else if (fileExtension.endsWith("txt"))
      return "text/plain";

    else
      return "unknown";

  }

  public static String realURI(HttpServletRequest request) {
    String realUri = null;
    final String includeURI = ((String) request.getAttribute("javax.servlet.include.request_uri"));
    if (includeURI != null) {
      realUri = includeURI.substring(request.getContextPath().length());
    } else
      realUri = request.getRequestURI().substring(request.getContextPath().length());

    return realUri;
  }

  public static String serverURL(HttpServletRequest request) {

    String psn = ApplicationState.getApplicationSetting(SystemConstants.PUBLIC_SERVER_NAME);
    if (!JSP.ex(psn))
      psn = request.getServerName();

    int serverPort = request.getServerPort();
    
    String psp = ApplicationState.getApplicationSetting(SystemConstants.PUBLIC_SERVER_PORT);
    if (JSP.ex(psp))
      try {
        serverPort = Integer.parseInt(psp);
      } catch (NumberFormatException e) {
        Tracer.platformLogger.error("PUBLIC_SERVER_PORT global config is not a number: "+psp);
      }

    String confProt = ApplicationState.getApplicationSetting(SystemConstants.HTTP_PROTOCOL);

    if (JSP.ex(confProt)) {
      if (confProt.indexOf(":")==-1)
        confProt = confProt+":";
      if (confProt.indexOf("//")==-1)
        confProt = confProt+"//";
    } else
      confProt = request.getRequestURL().substring(0, request.getRequestURL().indexOf("//") + 2);

    return confProt + psn + (serverPort != 80 ? ":" + serverPort : "") + request.getContextPath();
  }

  /**
   * @param email
   * @return true if the param indicates a valid email address
   */
  public static boolean checkValidEmail(String email) {
    return EmailValidator.getInstance().isValid(email);
  }

  /**
   * Set response to JSON and print the json element. REMEMBER TO call return after this method when used in jsp in order dirty the response
   *
   * @param json
   * @param response
   * @throws IOException
   */
  public static void jsonifyResponse(JSON json, HttpServletResponse response) throws IOException {
    response.setHeader("Cache-Control", "no-cache");
    response.setContentType("application/json; charset=utf-8");
    response.getWriter().write(json.toString());
  }


  public static String getPageContent(String url, String charset) throws IOException {
    HttpClient client = new HttpClient();

    client.getParams().setParameter(HttpMethodParams.USER_AGENT,
            "Mozilla/5.0 (Windows; U; Windows NT 6.1; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");

    HttpMethod method = new GetMethod(url);
    client.executeMethod(method);
    InputStream bodyAsStream = method.getResponseBodyAsStream();
    StringBuffer sb = new StringBuffer();
    String ret = FileUtilities.readInputStream(bodyAsStream, sb, charset);

    bodyAsStream.close();
    method.releaseConnection();

    return ret;
  }

  public static String getPageContent(String url) throws IOException {
    return getPageContent(url, "UTF-8");
  }

  public static String getDomainFromUrl(String url) {
    String domain = "";
    try {
      if (!url.startsWith("http://") && !url.startsWith("https://"))
        url = "http://" + url;

      Pattern regex = Pattern.compile("://(.[^/]+)");
      Matcher regexMatcher = regex.matcher(url);
      if (regexMatcher.find()) {
        domain = regexMatcher.group(1);
      }
    } catch (PatternSyntaxException ex) {
    }

    return domain;
  }


  public static String getShortenedUrl(String urlToBeShortened) throws IOException {
    String shortUrl = urlToBeShortened;
    try {

      HttpClient httpclient = new HttpClient();
      HttpMethod method = new GetMethod("http://api.bit.ly/shorten");
      method.setQueryString(
              new NameValuePair[]{
                      new NameValuePair("longUrl", urlToBeShortened),
                      new NameValuePair("version", "2.0.1"),
                      new NameValuePair("login", "licorize"),
                      new NameValuePair("apiKey", "R_bcd206f35f9f7510bd8a39cdb0416cf8"),
                      new NameValuePair("format", "json"),
                      new NameValuePair("history", "1")
              }
      );
      httpclient.executeMethod(method);
      JSONObject ob = JSONObject.fromObject(method.getResponseBodyAsString());
      JSONObject v = (JSONObject) ((JSONObject) ob.get("results")).get(urlToBeShortened);
      shortUrl = v.get("shortUrl") + "";
    } catch (Throwable e) {
      Tracer.platformLogger.error("Error shortening url:" + urlToBeShortened);
    }
    return shortUrl;
  }
}
