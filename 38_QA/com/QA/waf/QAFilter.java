package com.QA.waf;

import com.QA.Question;
import org.jblooming.tracer.Tracer;
import org.jblooming.utilities.JSP;
import org.jblooming.waf.settings.ApplicationState;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class QAFilter implements Filter {

  public void init(FilterConfig filterConfig) throws ServletException {
    if (ApplicationState.platformConfiguration.development) {
      Tracer.platformLogger.info("-- Init QAFilter --");
    }
  }

  public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws ServletException, IOException {
    // cast to HttpServletRequest
    HttpServletRequest hRequest = (HttpServletRequest) servletRequest;
    HttpServletResponse hResponse = (HttpServletResponse) servletResponse;

    // Get Uri of request
    String uri = hRequest.getRequestURI();
    RequestDispatcher rq = null;
    String[] params = uri.split("/");
    if ("/".equalsIgnoreCase(uri) || "/index.jsp".equalsIgnoreCase(uri)) {

      rq = hRequest.getRequestDispatcher("/applications/QA/talk/index.jsp");
      rq.forward(hRequest, hResponse);
      filterChain.doFilter(servletRequest, servletResponse);

    } else if (params != null && params.length > 0 && (JSP.ex(uri) && !uri.endsWith(".jsp") && !uri.endsWith(".ico") && !uri.endsWith(".htm") && !uri.endsWith(".html") && !uri.startsWith("/applications") && !uri.startsWith("/commons"))) {

      String firstParam = params.length > 1 ? params[1] : "";
      String secondParam = params.length > 2 ? params[2] : "";
      String thirdParam = null;
      String fourthParam = null;
      if (params.length > 4) {
        thirdParam = params[3];
        fourthParam = params[4];
      }

      // SEE MANIFEST check if URI starts with right prefix to be forwarded
      if (JSP.ex(firstParam)) {


        //case osqa:
        //http://jquery.pupunzi.com/questions/1711/some-browser-problems
        if (JSP.ex(firstParam) && JSP.ex(secondParam) && "question".equalsIgnoreCase(firstParam)) {
          rq = hRequest.getRequestDispatcher("/applications/QA/talk/question.jsp?OBJID=" + secondParam);

        } else if (JSP.ex(firstParam) && JSP.ex(secondParam) && "questions".equalsIgnoreCase(firstParam)) {
          try {
            String id = Question.loadByExternalId(secondParam).getId() + "";
            rq = hRequest.getRequestDispatcher("/applications/QA/talk/question.jsp?OBJID=" + id);
          } catch (Throwable e) {
            Tracer.platformLogger.error(e);
            filterChain.doFilter(servletRequest, servletResponse);
            return;
          }

        } else if (JSP.ex(firstParam) && JSP.ex(secondParam) && "user".equalsIgnoreCase(firstParam)) {
          rq = hRequest.getRequestDispatcher("/applications/QA/user/profile.jsp?LNAME=" + secondParam);

        } else if (uri.toLowerCase().indexOf("/feed/") > -1 || uri.toLowerCase().endsWith("/feed")) {
          rq = hRequest.getRequestDispatcher("/applications/QA/site/rssGenerator.jsp");
        }

        rq.forward(hRequest, hResponse);

      } else
        filterChain.doFilter(servletRequest, servletResponse);

    } else
      filterChain.doFilter(servletRequest, servletResponse);
  }

  public void destroy() {
    if (ApplicationState.platformConfiguration.development) {
      Tracer.platformLogger.info("-- destroy QAFilter--");
    }
  }

}