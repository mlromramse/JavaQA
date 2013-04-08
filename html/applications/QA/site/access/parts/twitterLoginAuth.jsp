<%@ page import="com.QA.QAOperator, com.QA.waf.QALoginAction, org.jblooming.waf.settings.ApplicationState, org.jblooming.waf.view.PageSeed, org.jblooming.waf.view.PageState" %><%

  PageState pageState = PageState.getCurrentPageState();
  try {
    QALoginAction.enrollWithTwitter(pageState, request, response);
    PageSeed redirTo = QALoginAction.magicalRedir((QAOperator) pageState.getLoggedOperator(), pageState);
    response.sendRedirect(redirTo.toLinkToHref());

  } catch (Throwable e) {
    e.printStackTrace();
    //user click on deny - redirect to enroll again
    response.sendRedirect(ApplicationState.contextPath + "/applications/QA/site/access/login.jsp");
  }


%>