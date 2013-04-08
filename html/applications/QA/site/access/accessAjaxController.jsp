<%@ page contentType="text/html; charset=utf-8" pageEncoding="UTF-8" %><%@ page import="com.QA.QAOperator, net.sf.json.JSONObject, org.jblooming.persistence.PersistenceHome, org.jblooming.utilities.JSP, org.jblooming.waf.view.PageState" %><%

  PageState pageState = PageState.getCurrentPageState();
  QAOperator logged = (QAOperator) pageState.getLoggedOperator();
  JSONObject jsResponse = new JSONObject();

  //------------------------------------   CHECK UNIQUENESS ------------------------------------------------
  try {
    if ("CKUNIQUN".equals(pageState.command)) {
      //pageState.tokenValidate("tkunqun", false);
      QAOperator tp = QAOperator.loadByLoginName(pageState.getEntry("un").stringValueNullIfEmpty());

      if (tp == null || logged != null && logged.getId().equals(tp.getId()))
        jsResponse.element("unique", true);
      else
        jsResponse.element("unique", false);

      if (tp!=null){
        if (JSP.ex(tp.getEmail()))
          jsResponse.element("hasValidEmail", true);
        else if (JSP.ex(tp.getUnverifiedEmail()))
          jsResponse.element("hasUnverifiedEmail", true);
      }

    } else if ("CKUNIQEM".equals(pageState.command)) {
      //pageState.tokenValidate("tkunqem", false);
      String email = pageState.getEntry("un").stringValueNullIfEmpty();
      if (JSP.ex(email)) {
        QAOperator tp = (QAOperator) PersistenceHome.findUniqueNullIfEmpty(QAOperator.class, "email", email);
        if (tp == null || logged != null && logged.getId().equals(tp.getId()))
          jsResponse.element("unique", true);
        else
          jsResponse.element("unique", false);

        QAOperator unconfUtp = (QAOperator) PersistenceHome.findFirst(QAOperator.class, "unverifiedEmail", email);
        if (unconfUtp!=null && !unconfUtp.equals(tp))
          jsResponse.element("hasUnverifiedEmail", true);

        /*} else {
         jsResponse.element("unique", false);
       } */
      } else {
        jsResponse.element("unique", true);
      }
    }
    jsResponse.element("ok", true);
    
  } catch (Throwable t) {
    jsResponse.element("ok", false);
    jsResponse.element("message", t.getMessage());
  }

  out.print(jsResponse.toString());

%>