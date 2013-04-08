<%@ page
        import="com.QA.QAOperator, com.QA.waf.QALoginAction, org.jblooming.waf.settings.ApplicationState, org.jblooming.waf.view.PageSeed, org.jblooming.waf.view.PageState" %><%

  PageState pageState = PageState.getCurrentPageState();

  try {
    String accessToken = QALoginAction.enrollWithFacebook(pageState, request, response);
    QAOperator logged = (QAOperator) pageState.getLoggedOperator();
    logged.getOptions().put("FBACCESSTOKEN", accessToken);
    logged.store();
    PageSeed redirTo = QALoginAction.magicalRedir(logged, pageState);
    response.sendRedirect(redirTo.toLinkToHref());
  } catch (Throwable e) {                                                                                                    //user click on deny - redirect to enroll again
    e.printStackTrace();
    response.sendRedirect(ApplicationState.contextPath + "/applications/QA/site/access/login.jsp");
  }
%>