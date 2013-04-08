<%@ page import="com.QA.Question, org.jblooming.oql.OqlQuery, org.jblooming.waf.view.PageState, java.util.List" %><%
  PageState pageState = PageState.getCurrentPageState();
  OqlQuery oqlQuery = new OqlQuery("from " + Question.class.getName() + " as brick where brick.deleted=false and brick.template=true and brick.contentRating is null ");
  oqlQuery.getQuery().setMaxResults(1);
  List<Question> br = oqlQuery.list();
%><%="OK"%>