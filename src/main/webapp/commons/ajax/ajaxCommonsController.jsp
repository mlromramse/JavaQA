<%@ page contentType="application/json; charset=utf-8" pageEncoding="UTF-8" %>
<%@ page import="com.twproject.mobile.MobileAjaxController,
                 net.sf.json.JSON,
                 org.jblooming.utilities.JSP,
                 org.jblooming.waf.view.PageState,
                 com.twproject.operator.TeamworkOperator,
                 net.sf.json.JSONObject,
                 org.jblooming.tracer.Tracer,
                 org.jblooming.ApplicationRuntimeException, org.jblooming.operator.Operator, org.jblooming.persistence.PersistenceHome, org.jblooming.waf.constants.Fields, org.jblooming.waf.state.PersistentSearch" %>
<%

  PageState pageState = PageState.getCurrentPageState();

  Operator logged = pageState.getLoggedOperator();
  JSONObject json = new JSONObject();
  json.element("ok", true);
  try {

    //---------------------------- SAVE PERSISTENT SEARCH - LOAD SAVE FILTER ----------------------------------------------------
    if ("SVFILTER".equals(pageState.command)) {
      if (logged != null) {
        String category = pageState.getEntry("cat").stringValueNullIfEmpty();
        PersistentSearch.saveSearch(category, pageState);
      }

      //---------------------------- REMOVE PERSISTENT SEARCH FILETR - LOAD SAVE FILTER ----------------------------------------------------
    } else if ("RMFILTER".equals(pageState.command)) {
      if (logged != null) {
        String filterCategory = pageState.getEntry("cat").stringValueNullIfEmpty();
        String filterName = JSP.w(pageState.getEntry(Fields.FLD_FILTER_NAME).stringValueNullIfEmpty());

        String filterCategoryName = JSP.w(filterCategory) + filterName;
        logged.getFilters().remove(filterCategoryName);
        logged.store();
      }
    }


  } catch (Throwable t) {
    Tracer.platformLogger.error(t);
    Tracer.platformLogger.error(ApplicationRuntimeException.getStackTrace(t));

    JSONObject ret = new JSONObject();
    ret.element("ok", false);

    String message = I18n.get("ERROR_APOLOGIES") + "/n";

    if (JSP.ex(t.getMessage()))
      message += I18n.get(t.getMessage());
    else
      message += I18n.get("ERROR_GENERIC_EXCEPTION");

    ret.element("message", message);

    json = ret;
  }

  // JSONP OBJECT
  if (JSP.ex(pageState.getEntry("__jsonp_callback"))) {
    out.print(pageState.getEntry("__jsonp_callback").stringValue() + "(");
    out.print(json.toString());
    out.print(");");

    // JSON OBJECT
  } else {
    out.print(json.toString());
  }
%>