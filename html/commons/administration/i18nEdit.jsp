<%@ page import="com.QA.waf.QAScreenApp,
                 org.jblooming.waf.ScreenBasic,
                 org.jblooming.waf.settings.I18n,
                 org.jblooming.waf.settings.businessLogic.I18nController, org.jblooming.waf.view.PageState" %>
<%--
Jsp of the Open Lab JBlooming development platform - www.jblooming.org
--%>

<%
  PageState pageState = PageState.getCurrentPageState();
  pageState.getLoggedOperator().testIsAdministrator();

  if (!pageState.screenRunning) {

    QAScreenApp mpScreenApp = (QAScreenApp) ScreenBasic.preparePage(new I18nController(), pageContext);
    mpScreenApp.hasRightColumn=false;
    if (I18n.EDIT_STATUS_EDIT.equals(I18n.getEditStatus())) {
      mpScreenApp.menu = null;
    }


    // hack to temporary disable i18nedit
    String old_status = I18n.getEditStatus();
    I18n.setEditStatus(I18n.EDIT_STATUS_READ);
    // temporary disable catching labels
    boolean catchState = I18n.catchUsedLabels;
    I18n.catchUsedLabels = false;

    pageState.perform(request, response).toHtml(pageContext);

    // hack to temporary disable i18nedit
    I18n.setEditStatus(old_status);
    I18n.catchUsedLabels = catchState;
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

<jsp:include page="partI18nEdit.jsp"/>
<%

  }
%>