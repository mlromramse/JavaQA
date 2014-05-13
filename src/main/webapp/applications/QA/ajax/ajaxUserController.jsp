<%@ page
        import=" com.QA.businessLogic.QAUserAction,net.sf.json.JSONObject, org.jblooming.waf.JSONHelper, org.jblooming.waf.view.PageState" %><%@page pageEncoding="UTF-8" %>
<%

  PageState pageState = PageState.getCurrentPageState();


  JSONHelper jsonHelper = new JSONHelper();
  JSONObject json = jsonHelper.json;

  try {
    QAUserAction action = new QAUserAction();

    String command = pageState.command;
    if ("DELETE_USER".equals(command)) {
      json = action.cmdDeleteUser(json);

    } else if ("SAVE_NAME".equals(command)) {
      json = action.cmdSaveName(json);

    } else if ("SAVE_EMAIL".equals(command)) {
      json = action.cmdSaveEmail(json);

    } else if ("SUBSCRIBE_EMAIL".equals(command)) {
      json = action.cmdSubsEmail(json);

    } else if ("SUBSCRIBE_USER".equals(command)) {
      json = action.cmdSubsUnsubsUser(json);


    } else if ("SUBSCRIBE_MAILING".equals(command)) {
      json = action.cmdSubsMailing(json);

    } else if ("CHANGE_PSW".equals(command)) {
      json = action.cmdChangePsw(json);

    }

  } catch (Throwable t) {
    jsonHelper.error(t);
  }
  jsonHelper.close(pageContext);

%>