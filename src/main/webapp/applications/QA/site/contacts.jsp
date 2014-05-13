<%@ page import="com.QA.waf.QAScreenApp,
                 org.jblooming.system.SystemConstants,
                 org.jblooming.waf.ScreenArea,
                 org.jblooming.waf.settings.ApplicationState, org.jblooming.waf.settings.I18n, org.jblooming.waf.view.PageState" %>
<%@ page pageEncoding="UTF-8" %>
<%

  PageState pageState = PageState.getCurrentPageState();

  if (!pageState.screenRunning) {
    pageState.screenRunning = true;
    final ScreenArea body = new ScreenArea(request);
    QAScreenApp lw = new QAScreenApp(body);
    pageState.getHeaderFooter().toolTip = I18n.g("QA_APP_NAME") + " - " + I18n.g("QA_CONTACTS");
    lw.register(pageState);
    pageState.perform(request, response).toHtml(pageContext);

  } else {

%>
<jsp:include page="../parts/partCommunityMenu.jsp">
    <jsp:param name="SHOW_ADD" value="no"></jsp:param>
    <jsp:param name="SHOW_BACK" value="yes"></jsp:param>
</jsp:include>
<div id="content" class="page">

  <h2><span>Contatti</span></h2>
  <div class="contentBox">

    <p><strong><%=I18n.g("QA_APP_NAME")%> </strong><br><br>

       <%
         String fromEmail = ApplicationState.getApplicationSetting(SystemConstants.FLD_MAIL_FROM);
         String fromEmailDomain = fromEmail.substring(fromEmail.indexOf("@")+1);
       %>
      E-mail: <a href="mailto:<%=fromEmail%>" target="_blank"><%=fromEmail%></a><br><br>

      <%=I18n.g("QA_CONTACTS_TEXT")%>

    </p>


  </div>
</div>


<%
  }
%>
