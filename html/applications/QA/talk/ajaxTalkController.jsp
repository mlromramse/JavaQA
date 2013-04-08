<%@ page import=" com.QA.businessLogic.QATalkAction,net.sf.json.JSONObject, org.jblooming.waf.JSONHelper, org.jblooming.waf.view.PageState"%><%@page pageEncoding="UTF-8" %><%

  PageState pageState = PageState.getCurrentPageState();


  JSONHelper jsonHelper = new JSONHelper();
  JSONObject json = jsonHelper.json;

  try {
    QATalkAction action = new QATalkAction();

    String command = pageState.command;
    if ("LIKE_QUESTION".equals(command)) {
      json = action.cmdLikeQuestion(json);

    } else if ("UNLIKE_QUESTION".equals(command)) {
      json = action.cmdUnLikeQuestion(json);

    } else if ("REPORT_QUESTION".equals(command)) {
      json = action.cmdReportQuestion(json);

    } else if ("BAN_QUESTION".equals(command)) {
      json = action.cmdBanQuestion(json);


    } else if ("ACCEPT_ANSWER".equals(command)) {
      json = action.cmdAcceptAnswer(json);

    } else if ("REFUTE_ANSWER".equals(command)) {
      json = action.cmdRefuteAnswer(json);

    } else if ("LIKE_ANSWER".equals(command)) {
      json = action.cmdLikeAnswer(json);

    } else if ("UNLIKE_ANSWER".equals(command)) {
      json = action.cmdUnLikeAnswer(json);

    } else if ("REPORT_ANSWER".equals(command)) {
      json = action.cmdReportAnswer(json);

    } else if ("BAN_ANSWER".equals(command)) {
      json = action.cmdBanAnswer(json);

    } else if ("COMMENT_QUESTION".equals(command)) {
      json = action.cmdCommentQuestion(json);

    } else if ("REMOVE_QUESTION_COMMENT".equals(command)) {
      json = action.cmdCommentRemove(json);

    } else if ("REPORT_QUESTION_COMMENT".equals(command)) {
      json = action.cmdReportComment(json);

    } else if ("COMMENT_ANSWER".equals(command)) {
      json = action.cmdCommentAnswer(json);

    } else if ("REMOVE_ANSWER_COMMENT".equals(command)) {
      json = action.cmdCommentAnswerRemove(json);

    } else if ("REPORT_ANSWER_COMMENT".equals(command)) {
      json = action.cmdReportAnswerComment(json);


    }

  } catch (Throwable t) {
    jsonHelper.error(t);
  }
  jsonHelper.close(pageContext);

%>