<%@ page import="com.QA.waf.QAScreenApp,org.jblooming.waf.ScreenArea,org.jblooming.waf.settings.I18n, org.jblooming.waf.view.PageSeed, org.jblooming.waf.view.PageState" %><%@page pageEncoding="UTF-8" %><%

    PageState pageState = PageState.getCurrentPageState();
    PageSeed home = pageState.pageFromRoot("talk/index.jsp");

    if (!pageState.screenRunning) {
        pageState.screenRunning = true;
        final ScreenArea body = new ScreenArea(request);
        new QAScreenApp(body).register(pageState);
        pageState.getHeaderFooter().toolTip = I18n.g("QA_APP_NAME")+" - "+ I18n.g("QA_TERMS");
        pageState.getHeaderFooter().meta = "<meta name=\"title\" content=\""+I18n.g("QA_APP_NAME")+": terms\">\n" +
                "<meta name=\"description\" content=\"\">\n" +
                "<meta name=\"keywords\" content=\"\">";
        pageState.perform(request, response);
        pageState.toHtml(pageContext);

    } else {

%>

<jsp:include page="../parts/partCommunityMenu.jsp">
    <jsp:param name="SHOW_ADD" value="no"></jsp:param><jsp:param name="SHOW_BACK" value="yes"></jsp:param>
</jsp:include>
<div id="content" class="page">

    <h2><span>Terms & Conditions</span></h2>
    <div class="contentBox page">

        By using the "Q&A" service you are agreeing to be bound by the following terms and conditions (“Terms of Service”).<br>
        Your use of the Service is at your sole risk. The service is provided on an "as is" and "as available" basis. Open Lab (supplier of Q&A) is not liable for any damage, and we may any time suspend your account's access.<br>
        We claim no intellectual property rights over the material you provide to the Q&A service; at our sole discretion, we may refuse or remove any content that is available via the Q&A service.

    </div>
</div>

<%
    }
%>
