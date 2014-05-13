<%@ page import="com.QA.waf.QAScreenApp,
                 org.jblooming.waf.ScreenBasic,
                 org.jblooming.waf.settings.businessLogic.I18nController, org.jblooming.waf.view.PageState"%>
 <%--
Jsp of the Open Lab JBlooming development platform - www.jblooming.org
--%>
<%
PageState pageState = PageState.getCurrentPageState();
pageState.getLoggedOperator().testIsAdministrator();

if (!pageState.screenRunning) {



  QAScreenApp mpScreenApp = (QAScreenApp) ScreenBasic.preparePage(new I18nController(), pageContext);
  mpScreenApp.hasRightColumn=false;
 pageState.perform(request, response).toHtml(pageContext);

} else {

    %>
   <style>
       .container{
           background-color: #fff!important;
           margin-top: 30px;
       }

       .container label {display: inline;}
       table#multi {padding-top: 30px}

   </style>

<jsp:include page="partI18nManager.jsp" /><%


}
%>

