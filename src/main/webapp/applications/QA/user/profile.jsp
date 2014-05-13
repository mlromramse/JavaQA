<%@ page contentType="text/html; charset=utf-8" pageEncoding="UTF-8" %><%@ page import="com.QA.*,
                 org.jblooming.utilities.JSP,
                 org.jblooming.utilities.StringUtilities,
                 org.jblooming.waf.ScreenArea,
                 org.jblooming.waf.SessionState, org.jblooming.waf.constants.Commands, org.jblooming.waf.settings.I18n, org.jblooming.waf.view.PageSeed,
                 org.jblooming.waf.view.PageState, java.util.List, net.sf.json.JSONArray, com.QA.waf.*" %><%

  PageState pageState = PageState.getCurrentPageState();
  SessionState sessionState = pageState.getSessionState();

  if (!pageState.screenRunning) {

    pageState.screenRunning = true;
    final ScreenArea body = new ScreenArea(request);
    new QAScreenApp(body).register(pageState);
    pageState.perform(request, response);
    pageState.toHtml(pageContext);

  } else {

    %><div id="content"><%

    QAOperator user = null;
    String lname = pageState.getEntry("LNAME").stringValueNullIfEmpty();
    if (JSP.ex(lname))
      user = QAOperator.loadByLoginName(lname);
    else
      user = QAOperator.load(pageState.mainObjectId);

    if (!user.isEnabled()) {
%><h2><%=I18n.g("QA_USER_DISABLED")%>
</h2><%
    return;
  }
%>

<h2><span><%=I18n.get("USER_PROFILE")%></span>
</h2>

<div class="contentBox vcard">
  <div style="width: 100%;">

    <div class="vcard-actions" style="position: relative;float: right;">

    </div>

    

  </div>

     <jsp:include page="partUserMenu.jsp"/>


<%


  JSONArray mySubscribersIds = user.getMySubscribersIds();
  if (mySubscribersIds.size()>0) {
     %><%--<hr class="separator">--%>
  <div class="userSubscribed">
      <h4 id="USER_SUBSCRIBED"><%=I18n.get("USER_SUBSCRIBED", user.getDisplayName())%></h4>

    <%
      JSONArray subsA = mySubscribersIds;
      for (int i = 0; i < subsA.size(); i++) {
        String id = subsA.get(i)+"";
        QAOperator sub = QAOperator.load(id);
        if (sub.isEnabled()) {
          %><a href="<%=sub.getPublicProfileURL()%>"><%new UserDrawer(sub, 40).toHtml(pageContext);%></a><%
        }
      }
    %>
  </div>
  <%
  }

    JSONArray iAmSubscribedToIds = user.getIAmSubscribedToIds();
    if (iAmSubscribedToIds.size()>0) {
  %><%--<hr class="separator">--%>
  <div class="userSubscribed"><h4 id="USER_SUBSCRIBED"><%=I18n.get("USER_SUBSCRIBING_TO", user.getDisplayName())%></h4>

    <%
      JSONArray subsA = iAmSubscribedToIds;
      for (int i = 0; i < subsA.size(); i++) {
        String id = subsA.get(i)+"";
        QAOperator sub = QAOperator.load(id);
        if (sub.isEnabled()) {
    %><a href="<%=sub.getPublicProfileURL()%>"><%new UserDrawer(sub, 40).toHtml(pageContext);%></a><%
        }
      }
    %>
  </div>
  <%
    }



  if (user.getBadges().size() > 0) {

    %><hr class="separator">
  <div class="myBadges" style="margin-top:10px;"><h5><%=pageState.getI18n("MY_BADGES")%>:</h5><%
    for (String badge : user.getBadges()) {
  %><span class="<%=StringUtilities.replaceAllNoRegex(pageState.getI18n(badge)," ","")%>"><img
          src="/applications/licorize/images/badges/badge-<%=badge%>-b.png"
          alt="" align="center"><br><%=pageState.getI18n(badge)%></span><%
  %>
  <%--
  } else {
  %><%=I18n.get("USER_BADGES_NO_YET")%><%
    }

  --%>
  <%
        //todo
      }%></div><%
    }
  %></div>


<h2 class="blockTitle"><%=I18n.get("USER_QANDA_ACTIVITY_%%", user.getDisplayName())%>
</h2>
<h3><span><%=I18n.get("USER_QUESTIONS_CREATED")%></span></h3>
<%
  List<Question> qICreated = user.getQuestionsByRelevance(5);
  if (qICreated.size() > 0) {
    for (Question b : qICreated) {

      QuestionDrawer brickDrawer = new QuestionDrawer(b);
      brickDrawer.toHtmlCompact(pageContext);
    }
  } else {
%><%=I18n.get("NO_QUESTIONS_CREATED_YET")%><%
  }
%>
<h3><span><%=I18n.get("USER_ANSWERS_GIVEN")%></span></h3>

<%
  List<Answer> ans = user.getAnswersByDate(10);
  if (ans.size() > 0) {
    for (Answer b : ans) {

      QuestionDrawer brickDrawer = new QuestionDrawer(b.getQuestion());
      brickDrawer.toHtmlCompact(pageContext);
    }
  } else {
%><%=I18n.get("NO_ANSWERS_CREATED_YET")%><%
  }
%><%

%></div><%


}
%>
