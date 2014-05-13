package org.jblooming.waf;

import org.jblooming.PlatformExceptionCarrier;
import org.jblooming.PlatformRuntimeException;
import org.jblooming.operator.Operator;
import org.jblooming.persistence.PersistenceHome;
import org.jblooming.persistence.ThreadLocalPersistenceContextCarrier;
import org.jblooming.persistence.hibernate.PersistenceContext;
import org.jblooming.security.InvalidTokenException;
import org.jblooming.tracer.Tracer;
import org.jblooming.utilities.HttpUtilities;
import org.jblooming.utilities.JSP;
import org.jblooming.waf.api.APIFilter;
import org.jblooming.waf.constants.Commands;
import org.jblooming.waf.constants.Fields;
import org.jblooming.waf.constants.SettingsConstants;
import org.jblooming.waf.exceptions.ActionException;
import org.jblooming.waf.settings.ApplicationState;
import org.jblooming.waf.settings.PlatformConfiguration;
import org.jblooming.waf.view.PageSeed;
import org.jblooming.waf.view.PageState;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class FrontControllerFilter implements Filter {

  public static Set<String> ignoredPatterns = new HashSet();


  public static String ERROR_PAGE_PATH_FROM_ROOT = "/commons/administration/error.jsp";
  public static final String page = ".page";

  public void init(FilterConfig config) throws ServletException {

    //linux no x-windows compatibility attempt
    System.setProperty("java.awt.headless", "true");
  }

  public void destroy() {
  }

  public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {


    HttpServletRequest request = (HttpServletRequest) req;
    HttpServletResponse response = (HttpServletResponse) res;
    String charset = "utf-8";
    request.setCharacterEncoding(charset);
    // in case of use with tomcat 4.1
    // response.setCharacterEncoding non supported, it must be replaced with response.setContentType
    response.setContentType("charset=\"utf-8\"");
    response.setCharacterEncoding(charset);

    //response.addHeader("Expires", "Sat, 20 Sep 2000 01 :01 :01 GMT");
    response.addHeader("Expires", "Sat, 23 Sep 2000 01:01:01 GMT");
    response.addHeader("Cache-Control", "no-cache");

    final String resource = request.getServletPath().toLowerCase();
    //PersistenceContext pc = null;

    boolean matchesIgnoredPatterns = false;
    for (String ip : ignoredPatterns) {
      if (resource.toLowerCase().endsWith(ip.toLowerCase())) {
        matchesIgnoredPatterns = true;
        break;
      }
    }

    boolean matchesServletPath = false;
    for (String ip : AccessControlFilter.servletPath) {
      if (resource.toLowerCase().startsWith(ip.toLowerCase())) {
        matchesServletPath = true;
        break;
      }
    }

    if (!matchesIgnoredPatterns && (
            resource.equalsIgnoreCase("") ||
                    resource.equalsIgnoreCase("/") ||
                    resource.toUpperCase().endsWith(".JSP") ||
                    resource.toUpperCase().endsWith(page.toUpperCase()) ||
                    APIFilter.isApiResource(resource) ||
                    matchesServletPath)) {


      if (ApplicationState.platformConfiguration != null && ApplicationState.platformConfiguration.development) {
        request.setAttribute("time", System.currentTimeMillis());
      }

      String pageStateName = "pageState name unknown";
      String pageStateNameHolder[] = new String[]{pageStateName};

      try {

        //this is necessary for instruct "non thread-local" code for default hib factory
        PersistenceContext.switchToFirst();

        boolean doChain = true;

        if (!JSP.ex(ApplicationState.serverURL)) {
          ApplicationState.serverURL = HttpUtilities.serverURL(request); // this uses PUBLIC_SERVER_NAME if exists
          ApplicationState.contextPath = request.getContextPath();
        }

        Map map = ApplicationState.getConfiguredUrls();
        if (map.size() == 0) {
          PageSeed vc = new PageSeed(ApplicationState.contextPath + AccessControlFilter.LOGIN_PAGE_PATH_FROM_ROOT);
          map.put(SettingsConstants.ROOT_LOGIN, vc);
          vc.setLoginRequiring(false);

          vc = new PageSeed(ApplicationState.contextPath + "/command.jsp");
          map.put(SettingsConstants.ROOT_COMMAND, vc);
        }

        //loggable, audit trail and subscribe enaction
        SessionState sessionState = SessionState.getSessionState(request);

        if (sessionState != null && sessionState.getOpid()!=-1) {
          //Operator loggedOperator = pageState.getLoggedOperator();
          Operator loggedOperator = (Operator) PersistenceHome.findByPrimaryKey(PlatformConfiguration.defaultOperatorSubclass, sessionState.getOpid());
          sessionState.setLoggedOperator(loggedOperator);
          PersistenceContext.threadLocalPersistenceContextCarrier.get().setOperator(loggedOperator);
        }

        if (request.getRequestURI().toUpperCase().endsWith(".JSP")
                || request.getRequestURI().toUpperCase().endsWith(page.toUpperCase())
                || APIFilter.isApiResource(resource)) {

          doChain = buildPageState(request, response, pageStateNameHolder);

          //used for example in Flowork to inject session
          if (ApplicationState.platformConfiguration.defaultApplication != null)
            ApplicationState.platformConfiguration.defaultApplication.configBeforePerform(request);

        }

        if (doChain) {
          chain.doFilter(req, response);
        }

        //28Apr2008
        //must rollback in case of action exception otherwise dirty gets saved by reachability
        PageState pageState = PageState.getCurrentPageState();
        if (pageState.validEntries())
          pageState.saveEntriesInDefaults();

        ThreadLocalPersistenceContextCarrier carrier = PersistenceContext.threadLocalPersistenceContextCarrier.get();
        if (carrier != null) {
          Collection<PersistenceContext> contexts = carrier.persistenceContextMap.values();

        for (PersistenceContext pc : contexts) {
            if (pageState.validEntries()) {
            pc.commitAndClose();

            } else
            pc.rollbackAndClose();
          }
        }

      } catch (Throwable throwable) {

        try {
          ThreadLocalPersistenceContextCarrier carrier = PersistenceContext.threadLocalPersistenceContextCarrier.get();
          if (carrier != null) {
            Collection<PersistenceContext> contexts = carrier.persistenceContextMap.values();
          for (PersistenceContext pc : contexts) {
            pc.rollbackAndClose();
          }
          }
          if (throwable instanceof ServletException && ((ServletException) throwable).getRootCause() != null) {
            Throwable thr = ((ServletException) throwable).getRootCause();
            throwInformedException(request, thr);
          } else
            throwInformedException(request, throwable);

        } catch (PlatformRuntimeException toBePrinted) {

          //if (!ApplicationState.platformConfiguration.development || toBePrinted.getCause() instanceof InvalidTokenException || (toBePrinted.getCause() != null && toBePrinted.getCause().getCause() instanceof InvalidTokenException)) {
          if (toBePrinted.getCause() instanceof InvalidTokenException || (toBePrinted.getCause() != null && toBePrinted.getCause().getCause() instanceof InvalidTokenException)) {
            try {
              SessionState.getSessionState(request).getAttributes().put("__ERROR__","INVALID_RESUBMIT");
            } catch (Throwable e) {
            }
            response.sendRedirect("/index.jsp");

          /*} else if (!ApplicationState.platformConfiguration.development) {
            Tracer.platformLogger.error(throwable);
            response.getWriter().print("<html><body>';\">--></select></textarea></script></table></table>" +
                    "Redirecting to error page...<iframe style=\"display:none\" src=\"" + ApplicationState.serverURL + ERROR_PAGE_PATH_FROM_ROOT + "\"></iframe>" +
                    "");*/
          } else {
            Tracer.platformLogger.error(throwable);
            throw toBePrinted;
        }
        }
      } finally {
        /**
         *  BE CAREFUL: you MUST remove by hand becouse ThreadLocal is local in a request but pooled by the application server
         *  it can be reused acronn requests --> if it is not clean could be a disaster.
         */
        PersistenceContext.threadLocalPersistenceContextCarrier.remove();
      }

    } else
      chain.doFilter(request, res);
  }

  private void throwInformedException(HttpServletRequest request, Throwable throwable) {

    PlatformExceptionCarrier exceptionCarrier = new PlatformExceptionCarrier();
    exceptionCarrier.exception = throwable;
    exceptionCarrier.requestURI = request.getRequestURI();
    exceptionCarrier.queryString = request.getQueryString();
    exceptionCarrier.command = request.getParameter(Commands.COMMAND);
    exceptionCarrier.objectID = request.getParameter(Fields.OBJECT_ID);
    request.getSession().setAttribute("PLAT_EXCEPTION", exceptionCarrier);


    throw new PlatformRuntimeException(
            "RequestURL: " + request.getRequestURL() + "\n" +
                    (JSP.ex(request.getQueryString()) ? "QueryString: " + request.getQueryString() + "\n" : "") +
                    (JSP.ex(request.getParameter(Commands.COMMAND)) ? "COMMAND: " + request.getParameter(Commands.COMMAND) + "\n" : "") +
                    (JSP.ex(request.getParameter(Fields.OBJECT_ID)) ? "OBJECT_ID: " + request.getParameter(Fields.OBJECT_ID) + "\n" : "") +

                    throwable.getMessage(), throwable);
  }

  protected boolean buildPageState(HttpServletRequest request, HttpServletResponse response, String[] pageStateNameHolder)
          throws ServletException, IOException {

    SessionState sessionState = SessionState.getSessionState(request);
    PageState pageState = sessionState.getPageState(request, response);
    /**
     *   This is a workaround for the immutability of Strings: this page name is returned in case of error.
     */
    pageStateNameHolder[0] = pageState.getName();

    if (Commands.BACK_TO.equals(pageState.getCommand())) {
      try {
        return sessionState.pageHistory.goBackTo(request, response, pageState);
      } catch (ParseException e) {
        throw new PlatformRuntimeException(e);
      } catch (ActionException e) {
        throw new PlatformRuntimeException(e);
      }
    } else if (Commands.BACK.equals(pageState.getCommand())) {
      return sessionState.pageHistory.goBack(request, response);

    } else {
      return true;
    }
  }
}
