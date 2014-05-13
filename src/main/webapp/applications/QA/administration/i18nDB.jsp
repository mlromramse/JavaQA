<%@ page import="com.QA.waf.QAScreenApp, org.jblooming.oql.OqlQuery, org.jblooming.waf.ScreenBasic, org.jblooming.waf.settings.I18nEntryPersistent, org.jblooming.waf.view.PageState, java.util.List" %><%

    PageState pageState = PageState.getCurrentPageState();
    pageState.getLoggedOperator().testIsAdministrator();

    if (!pageState.screenRunning) {

      QAScreenApp mpScreenApp = (QAScreenApp) ScreenBasic.preparePage(pageContext);
      mpScreenApp.hasRightColumn = false;
      pageState.perform(request, response).toHtml(pageContext);

    } else {

      %><h2>i18n from DB</h2><%
      List<I18nEntryPersistent> en = new OqlQuery("from " + I18nEntryPersistent.class.getName()).list();
      for (I18nEntryPersistent iiep : en ) {
        %><%=iiep.getCode()%>:<%=iiep.getValue()%><br><%
      }

    }

  %>