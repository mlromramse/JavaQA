<%@ page import=" com.QA.QAOperator,org.jblooming.utilities.JSP,org.jblooming.waf.ScreenBasic,org.jblooming.waf.SessionState,org.jblooming.waf.html.button.ButtonLink,
                  org.jblooming.waf.html.core.JspIncluderSupport,org.jblooming.waf.html.display.HeaderFooter,org.jblooming.waf.settings.ApplicationState, org.jblooming.waf.settings.I18n, org.jblooming.waf.view.PageSeed, org.jblooming.waf.view.PageState, com.QA.waf.QAScreenApp"%><%@page pageEncoding="utf-8"%><%

  PageState pageState = PageState.getCurrentPageState();

  final SessionState sessionState = SessionState.getSessionState(request);
  sessionState.setSkinForApplicationIfNull();

  QAScreenApp screen = (QAScreenApp) JspIncluderSupport.getCurrentInstance(request);
  if (screen.showHeaderAndFooter) {
    HeaderFooter hf = pageState.getHeaderFooter();
%><jsp:include page="partHeaderTags.jsp"/><%
  if (!JSP.ex(hf.toolTip))
    hf.toolTip = I18n.g("QA_APP_NAME");
  if (pageState.getLoggedOperator()!=null) {
    long messTot = ((QAOperator)pageState.getLoggedOperator()).getUnreadMessagesTotal();
    if (messTot > 0) {
      hf.toolTip =  hf.toolTip + " ("+messTot+")";
    }
  }
  hf.header(pageContext);

%>

<%--<%

  if (pageState.sessionState.getAttribute("TIME_OFFSET_CLIENT")==null) {
%><script type="text/javascript"> executeCommand("SETOFFSET","offset=-"+(new Date().getTimezoneOffset()*60000)); </script><%
  }
%>--%>


<div id="__FEEDBACKMESSAGEPLACE"></div>
<div id="SAVINGMESSAGE" align="center" style="display:none;position:fixed;top:0;left:0;">&nbsp;&nbsp;<%=I18n.get("SAVING_MESSAGE")%></div>

<div id="header" class="header" style="z-index: 20">
  <jsp:include page="../parts/appMenu.jsp"/>
</div>

<div class="site" id="TOP">

  <div class="siteContainer">
      <div class="customLogo">
          <a onclick="getHome()"><img src="/applications/QA/images/logo.png" alt="<%=I18n.g("QA_APP_NAME")%>" id="logoQA"></a>
      </div>

    <div id="wrapper">
      <a id="jumpTop" class="jumpTop"><img src="/applications/QA/images/jumpTop.png" onclick=scrollToTop();></a>

  <%
  }

  if (screen.menu != null && !pageState.isPopup()) {
    screen.menu.toHtml(pageContext);
  }
  screen.getBody().toHtml(pageContext);

  QAOperator operator = (QAOperator) pageState.getLoggedOperator();

  if (screen.showHeaderAndFooter &&!pageState.isPopup()) {
    if (operator != null) {

      String error = (String) ApplicationState.applicationParameters.get("FILTER_ERROR");
      if (error != null && error.trim().length() > 0) {

        PageSeed sc = pageState.pageFromRoot("administration/systemCheck.jsp");
        sc.setPopup(true);
        ButtonLink scbl = new ButtonLink("see system check page", sc);
        scbl.target = ButtonLink.TARGET_BLANK;
        scbl.popup_width = "680";
        scbl.popup_height = "680";
        scbl.popup_resizable = "yes";
        scbl.enabled = operator.hasPermissionAsAdmin();
        pageState.addMessageWarning(error);
        pageState.addMessageWarning(scbl.toLink());
      }
    }

%>




    </div>
    <%-- RIGHT COLUMN --%>
    <%
    if(screen.hasRightColumn){
    %>
    <div id="rightColumn">

        <jsp:include page="../parts/partSidebar.jsp"/>

    </div>
    <%
      }
    %>    <br style="clear: both;">

</div>
</div>
<div class="footer"><jsp:include page="../parts/qaFooter.jsp"/></div>

<%
  }

  if (screen.showHeaderAndFooter) {
    pageState.getHeaderFooter().footer(pageContext);
  }
%>