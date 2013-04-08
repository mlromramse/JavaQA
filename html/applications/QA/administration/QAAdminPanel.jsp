<%@ page
        import="com.QA.QAOperator, com.QA.waf.QAScreenApp, org.jblooming.waf.ScreenArea, org.jblooming.waf.html.button.ButtonLink, org.jblooming.waf.settings.I18n, org.jblooming.waf.view.PageState" %>
<%

  PageState pageState = PageState.getCurrentPageState();
  QAOperator logged = (QAOperator) pageState.getLoggedOperator();
  logged.testIsAdministrator();

  if (!pageState.screenRunning) {

    pageState.screenRunning = true;
    final ScreenArea body = new ScreenArea(request);
    QAScreenApp mpScreenApp = new QAScreenApp(body);
    mpScreenApp.hasRightColumn=false;
    mpScreenApp.register(pageState);
    pageState.getHeaderFooter().toolTip = I18n.g("QA_APP_NAME");
    pageState.perform(request, response);
    pageState.toHtml(pageContext);

  } else {

%>

<div class="admin" style="padding-top: 50px">
  <h2><span>Brick Admin Panel</span></h2>


  <div class="contentBox" style="text-align: center">
   <%


  new ButtonLink("users", pageState.pageInThisFolder("operatorList.jsp", request)).toHtml(pageContext); %>
    &nbsp;<%

    new ButtonLink("messages", pageState.pageFromRoot("user/messages.jsp")).toHtml(pageContext); %>&nbsp;<%

    new ButtonLink("labels", pageState.pageFromCommonsRoot("administration/i18nManager.jsp")).toHtml(pageContext); %>
    &nbsp;<%

    new ButtonLink("settings", pageState.pageFromRoot("administration/globalSettings.jsp")).toHtml(pageContext); %>
    &nbsp;<%

  %>
  </div>
  <%

    }
  %>
</div>