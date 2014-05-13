<%@ page import=" com.QA.QAOperator,com.QA.waf.QAScreenApp, org.jblooming.utilities.JSP, org.jblooming.waf.ScreenArea, org.jblooming.waf.SessionState,
org.jblooming.waf.constants.Commands, org.jblooming.waf.constants.Fields, org.jblooming.waf.html.input.TextField, org.jblooming.waf.settings.I18n, org.jblooming.waf.view.PageSeed, org.jblooming.waf.view.PageState"%><%@page pageEncoding="UTF-8" %><%

  PageState pageState = PageState.getCurrentPageState();
  SessionState sessionState = pageState.getSessionState();

  if (!pageState.screenRunning) {

    pageState.screenRunning = true;
    final ScreenArea body = new ScreenArea(request);
    new QAScreenApp(body).register(pageState);
    pageState.getHeaderFooter().toolTip = I18n.g("QA_APP_NAME")+" - "+I18n.g("QA_USER");
    pageState.perform(request, response);
    pageState.toHtml(pageContext);

  } else {

    QAOperator logged = (QAOperator) pageState.getLoggedOperator();
    PageSeed logout = pageState.pageFromRoot("site/access/login.jsp");
    logout.command = Commands.LOGOUT;

%>
<jsp:include page="../parts/partCommunityMenu.jsp">
    <jsp:param name="SHOW_ADD" value="no"></jsp:param>
    <jsp:param name="SHOW_BACK" value="yes"></jsp:param>
</jsp:include>
<div id="content" style="min-width: 670px">
<h2><span><%=I18n.g("QA_APP_NAME")%>: <%=I18n.get("QA_USER_SUBSCRIPTIONS")%></span></h2>

  <jsp:include page="partUserMenu.jsp"/><br><div class="contentBox"><%





  if (!JSP.ex(logged.getEmail())) {
    %><%=I18n.g("NO_EMAIL_CONFIRMED_YET")%><br>
  <a href="user.jsp"><%=I18n.g("INSERT_EMAIL")%></a>
  <%
  } else {
    %> <h2><span><%=I18n.get("QA_EMAIL")%>:</span></h2><%=logged.getEmail()%><%
  }

%><span id="emailFeedback"><%
  if (Fields.TRUE.equals(pageState.getEntry("fromEmailConfirm").stringValue())) {
    pageState.addMessageOK(I18n.g("QA_EMAIL_CONFIRMED"));
%> - <%=I18n.g("QA_EMAIL_CONFIRMED")%><br><br><br> <h4><span><%=I18n.get("QA_EMAIL_CONFIRMED_ACT",logged.getDisplayName())%></span></h4><%
  }
%></span>
  <div style="margin-top: 20px">

    <%
      if (JSP.ex(logged.getEmail())) {

        %><h2><span><%=I18n.g("EMAIL_SUBSCRIPTIONS")%></span></h2><%

       %><input type="checkbox" <%=Fields.TRUE.equals(logged.getOption("SEND_NOTIF_BY_EMAIL"))?" checked":""%> onclick="subscribeEmail();"> <%=I18n.g("QA_NOTIFY_EMAIL")%>
    &nbsp;&nbsp;&nbsp;<span id="subscribeMail"></span>

    <h2><span><%=I18n.g("MAILING_SUBSCRIPTIONS")%></span></h2>


    <input type="checkbox" <%=Fields.TRUE.equals(logged.getOption("QA_MAILINGLIST"))?" checked":""%> onclick="subscribeMailing();"> <%=I18n.g("QA_MAILINGLIST_SUBSCRIBE")%>
    &nbsp;&nbsp;&nbsp;<span id="subscribeMailing"></span>

    <%
    }
  %>


  </div>
</div>

<style>
  #subscribeMail i, #subscribeMailing i {color: #2dbe2d;}
  input[type="checkbox"]{vertical-align: baseline;}
</style>
<script type="text/javascript">

  var ajaxController = "/applications/QA/ajax/ajaxUserController.jsp";

  function subscribeEmail() {
    var data = {};
    data.CM = 'SUBSCRIBE_EMAIL';

    $.ajax({
      url: ajaxController,
      data: data,
      dataType: 'json',
      success:  function(response) {
        $("#subscribeMail").fadeOut();
        $("#subscribeMail").html('<i><%=I18n.g("DONE")%></i>');
        $("#subscribeMail").fadeIn();

      },
      error: function(er){
        showFeedbackMessage("ERROR", er.statusText);
      }
    });
  }



  function subscribeMailing() {
    var data = {};
    data.CM = 'SUBSCRIBE_MAILING';

    $.ajax({
      url: ajaxController,
      data: data,
      dataType: 'json',
      success:  function(response) {
        $("#subscribeMailing").fadeOut();
        $("#subscribeMailing").html('<i><%=I18n.g("DONE")%></i>');
        $("#subscribeMailing").fadeIn();

      },
      error: function(er){
        showFeedbackMessage("ERROR", er.statusText);
      }
    });
  }




</script>


</div>


<%


  }


%>