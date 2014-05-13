package org.jblooming.waf;

import org.jblooming.utilities.HttpUtilities;
import org.jblooming.waf.view.PageSeed;
import org.jblooming.waf.view.PageState;
import org.jblooming.waf.constants.OperatorConstants;
import org.jblooming.waf.settings.PlatformConfiguration;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class AccessControlFilter implements Filter {

  public static Set<String> freeFolders = new HashSet();
  public static Set<String> freeFiles = new HashSet();
  public static Set<String> freePatterns = new HashSet();
  public static Set<String> securedSubFolders = new HashSet();
  public static Set<String> servletPath = new HashSet();
  public static String LOGIN_PAGE_PATH_FROM_ROOT;


  public void init(FilterConfig config) throws ServletException {
    freeFolders.add("settings");
  }

  public void destroy() {
  }

  public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {

    HttpServletRequest request = (HttpServletRequest) req;
    HttpServletResponse response = (HttpServletResponse) res;
    if (checkAccess(request, response))
      chain.doFilter(request, response);
  }

  protected boolean checkAccess(HttpServletRequest request, HttpServletResponse response) throws IOException {

    boolean ret = true;
    SessionState sessionState = SessionState.getSessionState(request);
    PageState pageState = sessionState.getPageState(request, response);

    // here starts the access control implementation
    // this implementation checks .jsp only

    boolean isReservedUrl = isReservedUrl(request);
    request.setAttribute("isReservedUrl", isReservedUrl);

    //no access to global properties
    if (request.getRequestURI().toUpperCase().endsWith(PlatformConfiguration.globalSettingsFileName.toUpperCase()))
      return false;

    if (request.getRequestURI().toUpperCase().endsWith(".PROPERTIES"))
      return false;

    if (!sessionState.isOperatorLogged() && isReservedUrl) {
      PageSeed login = new PageSeed(LOGIN_PAGE_PATH_FROM_ROOT);
      if (pageState.isLoginRequiring() && !pageState.getHref().equals(login.getHref()) && request.getAttribute("TOMCAT_NO_REDIR") == null) {
        sessionState.setLoginPendingUrl(pageState);
        String loginPage = request.getContextPath() + LOGIN_PAGE_PATH_FROM_ROOT;
        String ln = request.getParameter(OperatorConstants.FLD_LOGIN_NAME);
        String psw = request.getParameter(OperatorConstants.FLD_PWD);
        //hack to save login url
        if (ln != null && ln.trim().length() > 0)
          loginPage = loginPage + "?" + OperatorConstants.FLD_LOGIN_NAME + "=" + ln + "&" + OperatorConstants.FLD_PWD + "=" + psw;
        response.sendRedirect(response.encodeURL(loginPage));
        return false;

      }
    } else {

    }
    return ret;
  }

  /**
   * Determines whether a page requires login to access.
   * By default, all pages are secured; furthermore if a file in a freeFolder actually is in a subFolder reserved, its reserved.
   *
   * @param request
   */
  public static boolean isReservedUrl(HttpServletRequest request) {

    boolean isReserved = true;

    if (!request.getRequestURI().toUpperCase().endsWith(".JSP") &&
            !request.getRequestURI().toUpperCase().endsWith(FrontControllerFilter.page.toUpperCase()) &&
            request.getRequestURI().indexOf(".") > -1) {
      isReserved = false;

    } else {

      String rootPath = HttpUtilities.getFileSystemRootPathForRequest(request);
      String key = HttpUtilities.getCanonicalFileSystemPathOfPartFromURI(request);

      boolean isSurelyReserved = false;

      if (securedSubFolders != null && securedSubFolders.size() > 0) {
        for (Iterator iterator = securedSubFolders.iterator(); iterator.hasNext();) {
          String s = (String) iterator.next();
          if (key.toLowerCase().startsWith((rootPath + s).toLowerCase())) {
            isSurelyReserved = true;
            break;
          }
        }
      }

      if (!isSurelyReserved) {
        if (freeFolders != null && freeFolders.size() > 0) {
          for (Iterator iterator = freeFolders.iterator(); iterator.hasNext();) {
            String s = (String) iterator.next();
            if (key.toLowerCase().startsWith((rootPath + s).toLowerCase())) {
              isReserved = false;
              break;
            }
          }
        }
        if (isReserved && freeFiles != null && freeFiles.size() > 0) {
          for (Iterator iterator = freeFiles.iterator(); iterator.hasNext();) {
            String s = (String) iterator.next();
            if (key.equalsIgnoreCase(rootPath + s)) {
              isReserved = false;
              break;
            }
          }
        }
        if (isReserved && freePatterns != null && freePatterns.size() > 0) {
          for (Iterator iterator = freePatterns.iterator(); iterator.hasNext();) {
            String s = (String) iterator.next();
            if (key.toLowerCase().indexOf(s.toLowerCase()) > -1) {
              isReserved = false;
              break;
            }
          }
        }
      }
    }
    return isReserved;
  }
}