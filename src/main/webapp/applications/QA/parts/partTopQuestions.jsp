<%@ page import="com.QA.waf.QuestionDrawer, com.QA.Question, java.io.Serializable, java.util.List, java.util.ArrayList, org.jblooming.agenda.CompanyCalendar, org.jblooming.oql.OqlQuery, com.QA.rank.Hit, org.jblooming.waf.settings.I18n" %>
<%@page pageEncoding="UTF-8" %>
<div class="contentBox hotQuestion">
    <h3><span><%=I18n.g("QUESTIONS_HOT")%></span></h3>

    <div class="wrap">
    <%
      for (Question q : Question.getHotQuestions(5,false)) {

        new QuestionDrawer(q).toHtmlSimple(pageContext);

      }%></div>
</div>
