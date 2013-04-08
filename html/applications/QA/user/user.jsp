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
<div id="content">
<h2><span><%=I18n.g("QA_APP_NAME")%>: <%=I18n.get("QA_USER_EDIT")%></span></h2>
<jsp:include page="partUserMenu.jsp"/> <br>
  <div class="contentBox">
    <%

      String howLogged = I18n.get("QA_LOGGED_IN_WITH_QA");

      if (JSP.ex(logged.getWebsite()) && logged.getWebsite().indexOf("facebook")>-1) {
        howLogged = "Facebook";
      } else if (JSP.ex(logged.getWebsite()) && logged.getWebsite().indexOf("twitter")>-1) {
        howLogged = "Twitter";
      } else if (JSP.ex(logged.getWebsite()) && logged.getWebsite().indexOf("gmail")>-1) {
        howLogged = "Gmail";
      }

      boolean internalLogin = howLogged.equals(I18n.get("QA_LOGGED_IN_WITH_QA"));

    %>



    <%=I18n.get("QA_LOGGED_IN_WITH_%%", howLogged)%><%
      if (!JSP.ex(logged.getWebsite())) {
    %> <%=I18n.get("QA_LOGGED_IN_WITH_LOGINAME")%> <b style=""><%=logged.getLoginName()%></b>.<br><br>
      
      <%
        if (JSP.ex(logged.getEmail())) {
      %>
      <a href="http://gravatar.com/" target="_blank"><%=I18n.g("CHANGE_GRAVATAR")%></a>.
      <%
          } else {
            %><%=I18n.g("INSERT_EMAIL_GRAVATAR")%><%
          }
      }
    %><hr class="separator"><br>
      <label><%=I18n.get("QA_FULLNAME")%>:</label><%
        pageState.addClientEntry("fullName",logged.getName());
        org.jblooming.waf.html.input.TextField tf= new org.jblooming.waf.html.input.TextField("fullName","");
        tf.label="";
        tf.fieldSize=30;
        tf.script="onchange='saveUserName()'";
        tf.toHtml(pageContext);
      %><br>


    <label><%=I18n.get("QA_EMAIL")%>:</label><%
    pageState.addClientEntry("email",logged.getEmail());
    tf= new org.jblooming.waf.html.input.TextField("email","");
    tf.label="";
    tf.fieldSize=30;
    tf.script="onchange='saveEmail()'";
    tf.toHtml(pageContext);

  %><span id="emailFeedback"><%
      if (Fields.TRUE.equals(pageState.getEntry("fromEmailConfirm").stringValue())) {
        pageState.addMessageOK(I18n.g("QA_EMAIL_CONFIRMED"));
        %> - <%=I18n.g("QA_EMAIL_CONFIRMED")%><%
      }
  %></span>

    <%
      if (internalLogin) {

        %>
    <%--<h3><span><%=I18n.get("QA_CHANGEPASSWORD")%></span></h3>--%>
     <div id="changePasswordDiv">
    <label><%=I18n.get("QA_NEWPASSWORD")%>:</label><%
        TextField tfp1 = new TextField("newpassword1","");
        tfp1.type="password";
        tfp1.label="";
        tfp1.toHtml(pageContext);

        %><br><label><%=I18n.get("QA_NEWPASSWORD_CONFIRM")%>:</label><%
        TextField tfp2 = new TextField("newpassword2","");
        tfp2.type="password";
        tfp2.label="";
        tfp2.toHtml(pageContext);

         %></div><%
      }
    %>
    <script type="text/javascript">
      function doChange() {
        if ($("#newpassword1").val() && $("#newpassword1").val()==$("#newpassword2").val()) {
          var data = {newpsw:$("#newpassword1").val()};
          data.CM = 'CHANGE_PSW';

          $.ajax({
            url: ajaxController,
            data: data,
            dataType: 'json',
            success:  function(response) {

              if (response.ok)
                $("#changePasswordDiv").empty();
                $("#changePasswordDiv").html("<%=I18n.g("PASSWORD_CHANGED")%>");
                $("#doSaveButton").fadeOut();

            }, error: function(er){
              showFeedbackMessage("ERROR", er.statusText);
            }
          });
        }
      }
    </script>
<hr>
<div class="btnBar">

  <div><button id="doSaveButton" onclick="doChange();"><%=I18n.g("SAVE")%></button><button id="manDelete" class="buttonSmall" style="float: left;" onclick="$(this).confirm(deleteUser,'<%=I18n.get("QA_USER_REMOVE_SURE")%>');"><%=I18n.g("QA_REMOVE_ME")%></button></div>
   <br>


</div>
  </div>


<script type="text/javascript">

  var ajaxController = "/applications/QA/ajax/ajaxUserController.jsp";

  function deleteUser(){
    var el=this;

        var data = {};
        data.CM = 'DELETE_USER';

        $.ajax({
          url: ajaxController,
          data: data,
          dataType: 'json',
          success:  function(response) {

            if (response.ok)
              self.location.href='<%=logout.toLinkToHref()%>';

          }, error: function(er){
            showFeedbackMessage("ERROR", er.statusText);
          }
        });

  };


  function saveUserName() {
    var data = {};
    data.CM = 'SAVE_NAME';
    data.fullName=$("#fullName").val();

    $.ajax({
      url: ajaxController,
      data: data,
      dataType: 'json',
      success:  function(response) {
        $("#fullName").append('<%=I18n.g("QA_NAME_SAVED")%>')

      },
      error: function(er){
        showFeedbackMessage("ERROR", er.statusText);
      }
    });
  }

  function subscribeEmail() {
    var data = {};
    data.CM = 'SUBSCRIBE_EMAIL';

    $.ajax({
      url: ajaxController,
      data: data,
      dataType: 'json',
      success:  function(response) {
        $("#subscribeEmail").html('<%=I18n.g("QA_EMAIL_SUBSCRIBED")%>')

      },
      error: function(er){
        showFeedbackMessage("ERROR", er.statusText);
      }
    });
  }

  function unsubscribeEmail() {
    var data = {};
    data.CM = 'UNSUBSCRIBE_EMAIL';

    $.ajax({
      url: ajaxController,
      data: data,
      dataType: 'json',
      success:  function(response) {
        $("#unsubscribeEmail").html('<%=I18n.g("QA_EMAIL_UNSUBSCRIBED")%>');
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
        $("#subscribeMailing").html('<%=I18n.g("QA_MAILING_SUBSCRIBED")%>')

      },
      error: function(er){
        showFeedbackMessage("ERROR", er.statusText);
      }
    });
  }

  function unsubscribeEmail() {
    var data = {};
    data.CM = 'UNSUBSCRIBE_MAILING';

    $.ajax({
      url: ajaxController,
      data: data,
      dataType: 'json',
      success:  function(response) {
        $("#unsubscribeEmail").html('<%=I18n.g("QA_MAILING_UNSUBSCRIBED")%>');
      },
      error: function(er){
        showFeedbackMessage("ERROR", er.statusText);
      }
    });
  }




  function saveEmail() {
    var data = {};
    data.CM = 'SAVE_EMAIL';
    data.email=$("#email").val();

    $.ajax({
      url: ajaxController,
      data: data,
      dataType: 'json',
      success:  function(response) {
        if (response.action=="sent")
          $('#emailFeedback').append(' - <%=I18n.g("QA_VERIFICATION_SENT")%>');
        else
          $('#emailFeedback').append(' - <%=I18n.g("QA_EMAIL_RESET")%>');
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