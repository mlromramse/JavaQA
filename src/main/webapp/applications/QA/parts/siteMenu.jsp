<%@ page import="com.QA.QAOperator,org.jblooming.waf.SessionState,org.jblooming.waf.constants.Commands,org.jblooming.waf.html.layout.Skin,org.jblooming.waf.settings.I18n,org.jblooming.waf.view.PageSeed, org.jblooming.waf.view.PageState" %><%@ page buffer="16kb" %><%

  PageState pageState = PageState.getCurrentPageState();
  SessionState sessionState = pageState.sessionState;
  Skin skin = sessionState.getSkin();
  QAOperator logged = (QAOperator) pageState.getLoggedOperator();


  PageSeed homePs;
  if (logged == null)
    homePs = pageState.pageFromRoot("index.jsp");
  else
    homePs = pageState.pageFromRoot("talk/index.jsp");

  PageSeed login = pageState.pageFromRoot("site/access/login.jsp");
//  PageSeed enroll = pageState.pageFromRoot("site/access/enrollMail.jsp");
  //enroll.command = "ENROLL";
  PageSeed logout = pageState.pageFromRoot("site/access/login.jsp");
  logout.command = Commands.LOGOUT;

//  ButtonLink home = new ButtonLink(homePs);


%><style type="text/css">
  
</style>

<div id="siteTopNav" class="topMenu">

  <h1 onclick="getHome()" class="logotype_" title="<%=I18n.g("QA_APP_NAME")%>">
    <img src="/applications/QA/images/logo.png" alt="<%=I18n.g("QA_APP_NAME")%>" id="logoQA">
    <span id="claim"><%=I18n.g("QA_PITCH")%></span>
  </h1>

  <a href="/applications/QA/"><%=I18n.get("QA_HOME")%></a>

  <a href="<%=I18n.get("QA_REFERENCE_SITE")%>"><%=I18n.get("QA_REFERENCE")%></a>

  <a href="/applications/QA/site/contacts.jsp"><%=I18n.get("QA_CONTACTS")%></a>

  <%if (pageState.getAttribute("DONT_DRAW_SEARCH")==null) {%>
  <form action="/applications/QA/site/search.jsp" method="POST" enctype="application/x-www-form-urlencoded" id="formSearch">
    <input type="hidden" name="CM" value="<%=Commands.FIND%>">
    <input type="text" size="20" id="searchStrip" name="FILTER" class="searchField" value="" type="search"
           onkeypress="if (event.keyCode==13){$(this).closest('form').submit();}">
  </form>
  <%}%>
  <br>

<a class="playNow" id="MPGOTOPLAYLINK" href="/applications/QA/talk/"><%=I18n.get("QA_PLAY_NOW")%></a>

</div><br style="clear: both">