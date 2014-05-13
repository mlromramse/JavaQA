<%@ page import="org.jblooming.waf.ScreenArea,org.jblooming.waf.settings.I18n, org.jblooming.waf.view.PageSeed, org.jblooming.waf.view.PageState, com.QA.waf.QAScreenApp" %><%@page pageEncoding="UTF-8" %><%

  PageState pageState = PageState.getCurrentPageState();
  PageSeed home = pageState.pageFromRoot("site/index.jsp");

  if (!pageState.screenRunning) {
    pageState.screenRunning = true;
    final ScreenArea body = new ScreenArea(request);
    new QAScreenApp(body).register(pageState);
    pageState.getHeaderFooter().toolTip = I18n.g("QA_APP_NAME")+" - "+ I18n.g("QA_THANKS");
    pageState.getHeaderFooter().meta = "<meta name=\"title\" content=\""+I18n.g("QA_APP_NAME")+": thanks\">\n" +
        "<meta name=\"description\" content=\"\">\n" +
        "<meta name=\"keywords\" content=\"\">";
    pageState.perform(request, response);
    pageState.toHtml(pageContext);

  } else {

  %>
  <div id="wrapper">

  <h1 id="logotype" class="logotype" onclick="getHome()">
    Your<br><span class="logoChange">Change</span>Manifesto
    <div id="claim">facilitating change</div>
    <img src="/applications/QA/images/sandwichMan.png" alt="logo">
  </h1>


  <jsp:include page="../parts/siteMenu.jsp"/>

  <div>
      <h1>Thanks</h1>

      <p><%=I18n.g("QA_APP_NAME")%> is developed and supplied using several contributed libraries.<br>In this page we want to thank the
        developers / producers of such libraries. </p><br><br>



      <h2>JavaScript</h2><br>

      <p>We thank the following JavaScript developers:<br>
        jQuery team <a href="http://jquery.com/" target="_blank">http://jquery.com/</a> <br>
        <br>
      </p>

      <h2>Java APIs and services</h2><br>

      <p>We thank the following Java APIs and services suppliers:<br><br>
        Oracle / Sun for <a href="http://jquery.com/" target="_blank">Java</a> (and <a href="http://www.mysql.com/" target="_blank">MySQL</a>)<br>
        JetBrains <a href="http://www.jetbrains.com/idea/" target="_blank">for Intellij</a><br>
        <a href="https://www.hibernate.org/" target="_blank">Hibernate</a> for persistence.<br>
        <a href="http://www.apache.org/" target="_blank">Apache</a> for Tomcat, Lucene, logging and components.<br>
        OAuth: <a href="http://code.google.com/p/oauth-signpost/" target="_blank">Oauth-signpost</a>, <a href="http://github.com/fernandezpablo85/scribe-java" target="_blank">Scribe-java</a>.<br>
      </p>

    </div>
  </div>

<%
  }
%>


