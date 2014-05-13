<%@ page contentType="text/html; charset=utf-8" pageEncoding="UTF-8"%><%@ page import="org.jblooming.operator.Operator, org.jblooming.waf.settings.ApplicationState, org.jblooming.waf.view.PageState,
 java.util.List, org.jblooming.waf.view.PageSeed, com.QA.QAOperator" %><%

  PageState pageState = PageState.getCurrentPageState();
  PageSeed redirTo = pageState.pageFromRoot("talk/index.jsp");
  redirTo.disableCache=false;
  response.sendRedirect(redirTo.toLinkToHref());

%>