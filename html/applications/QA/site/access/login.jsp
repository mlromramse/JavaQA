<%@ page contentType="text/html; charset=utf-8" pageEncoding="UTF-8"%><%@ page import="com.QA.QAOperator,
                 com.QA.waf.QALoginAction,
                 com.QA.waf.QAScreenApp,
                 org.jblooming.messaging.MailHelper,
                 org.jblooming.operator.Operator,
                 org.jblooming.persistence.PersistenceHome,
                 org.jblooming.security.businessLogic.LoginAction,
                 org.jblooming.system.SystemConstants,
                 org.jblooming.tracer.Tracer,
                 org.jblooming.utilities.CollectionUtilities,
                 org.jblooming.utilities.JSP,
                 org.jblooming.utilities.StringUtilities,
                 org.jblooming.waf.ScreenArea,
                 org.jblooming.waf.constants.Commands,
                 org.jblooming.waf.html.button.ButtonLink,
                 org.jblooming.waf.html.button.ButtonSubmit,
                 org.jblooming.waf.html.input.TextField,
                 org.jblooming.waf.html.state.Form,
                 org.jblooming.waf.settings.ApplicationState, org.jblooming.waf.settings.I18n, org.jblooming.waf.view.ClientEntry, org.jblooming.waf.view.PageSeed, org.jblooming.waf.view.PageState" %><%

  PageState pageState = PageState.getCurrentPageState();

  if (!pageState.screenRunning) {

    String lpu = pageState.getEntry("LOGINPENDINGURL").stringValueNullIfEmpty();
    if (JSP.ex(lpu)) {
      pageState.sessionState.setLoginPendingUrl(new PageSeed(lpu));
    }

    pageState.screenRunning = true;
    final ScreenArea body = new ScreenArea(request);

    QAScreenApp lw = new QAScreenApp(body);
    lw.hasRightColumn=false;
    lw.register(pageState);
    pageState.getHeaderFooter().toolTip = I18n.g("QA_APP_NAME")+" - "+I18n.g("QA_LOGIN");
    pageState.perform(request, response);

    Cookie loginCookie = null;
    //check if exists login cookie
    if (request.getCookies() != null) {
      for (Cookie coo : request.getCookies()) {
        if ("COOKIMMENSO".equals(coo.getName()) && JSP.ex(coo.getValue())) {
          loginCookie = coo;
          //generate a fake login token
          pageState.tokenCreate("login", pageState);
          break;
        }
      }
    }

    if ("log".equals(pageState.command) || (loginCookie != null && !Commands.LOGOUT.equals(pageState.command))) {

      // FIRST CHECK the token in session (and invalidate it in) in orded to avoid hack
      //pageState.tokenValidate("login");

      // increment a counter for failed login in the session
      Integer count = (Integer) pageState.sessionState.getAttribute("invalidLoginCount");
      if (count == null)
        count = 0;
      pageState.sessionState.setAttribute("invalidLoginCount", new Integer(count + 1));

      // if invalid login count is greater than x return or
      if (count > 50)
        return;

      QAOperator operator = null;

      //try with cookie
      operator = QALoginAction.getFromCookie(loginCookie);

      // login not via username+pwd
      if (operator == null || !operator.isEnabled()) {
        ClientEntry ceUsername = pageState.getEntry("USERNAME");
        String username = ceUsername.stringValue();
        ClientEntry cePwd = pageState.getEntry("PWD");
        String password = cePwd.stringValue();

        try {
          operator = (QAOperator) Operator.authenticateUser(password, username, pageState.getApplication().isLoginCookieEnabled());
        } catch (org.jblooming.security.SecurityException s) {
          cePwd.errorCode = s.getMessage();
        } catch (Exception e) {
          Tracer.platformLogger.error(e);
          ceUsername.errorCode = I18n.get("LOGIN_NAME_NONEXISTING");
        }
      }

      if (operator != null && operator.isEnabled()) {
        QALoginAction.doLog(operator, pageState, request, response);

        operator.store();

        PageSeed redirTo = QALoginAction.magicalRedir(operator, pageState);

        /*response.sendRedirect(redirTo.toLinkToHref());*/

%>
<script type="text/javascript">

  if (typeof(top) != "undefined") {
    top.location.href = "<%=redirTo.toLinkToHref()%>";
  }
</script>
<%
  }

} else if ("RESETPASS".equals(pageState.command)) {

  String newPass = pageState.getEntry("NP").stringValueNullIfEmpty();
  String oldPass = pageState.getEntry("OP").stringValueNullIfEmpty();
  int userId = pageState.getEntry("OPID").intValueNoErrorCodeNoExc();

  if (JSP.ex(newPass) && JSP.ex(oldPass) && userId > 0) {

    QAOperator op = QAOperator.load(userId);
    if (op != null && oldPass.equals(op.getPassword())) {
      op.changePassword(newPass);
      op.store();
      pageState.addClientEntry("USERNAME", op.getLoginName());
      pageState.addClientEntry("PWD", newPass);
      pageState.addMessageInfo("Your password has been changed to:" + newPass + "");

    } else {
      pageState.addMessageError("Invalid data resetting password. Invalid operator.");
    }

  } else {
    pageState.addMessageError("Invalid data resetting password. Fields empty.");
  }

} else if ("SENDRESETPASS".equals(pageState.command)) {
  // FIRST CHECK the token in session (and invalidate it in) in orded to avoid hack
  //pageState.tokenValidate("login");

  ClientEntry ceUsername = pageState.getEntry("USERNAME");
  String username = ceUsername.stringValue();

  QAOperator op = (QAOperator) PersistenceHome.findUniqueNullIfEmpty(QAOperator.class, "loginName", username);

  //try via e-mail
  if (op==null && username.indexOf("@")>-1) {
    op = QAOperator.loadByEmail(username);
  }

  if (op != null) {
    if (JSP.ex(op.getEmail())) {
      String newPassword = StringUtilities.generatePassword(6);
      // reset password link
      PageSeed resetLink = new PageSeed(ApplicationState.serverURL+"/applications/QA/site/access/login.jsp");
      resetLink.command = "RESETPASS";
      resetLink.addClientEntry("NP", newPassword);
      resetLink.addClientEntry("OP", op.getPassword());
      resetLink.addClientEntry("OPID", op.getId());

      // Send password to user
      String mailMessage = "Hi " + op.getDisplayName() + ", <br><br>" +
              "&nbsp;&nbsp;this e-mail has been sent to you in order to reset your password. <br><br>" +
              "If you did not ask for resetting the password, ignore this message and do nothing. <br>" +
              "In case you really need to reset you password just click on the link below and you password will be changed.<br><hr>" +
              "New password active after reset: " + newPassword + "<br>" +
              "You can anytime change your password.<hr><br>" +
              "Click here to reset you password: <a href=\"" + resetLink.toLinkToHref() + "\">" + resetLink.toLinkToHref() + "</a><br><br>" +
              "Best regards,<br>" +
              I18n.g("QA_APP_NAME")+" Support";

      String fromEmail = ApplicationState.getApplicationSetting(SystemConstants.FLD_MAIL_FROM);
      MailHelper.sendHtmlMailInSeparateThread(I18n.g("QA_APP_NAME")+" Support <"+fromEmail+">;", CollectionUtilities.toSet(op.getEmail()), I18n.g("QA_APP_NAME")+" password reset", mailMessage);
      //cc to us
      String mailTo = ApplicationState.getApplicationSetting("MAIL_TO");
      MailHelper.sendHtmlMailInSeparateThread(I18n.g("QA_APP_NAME")+" Support <"+fromEmail+">;", CollectionUtilities.toSet(mailTo),
              I18n.g("QA_APP_NAME")+" password reset for " + op.getDisplayName() + " " + op.getEmail(), op.getEmail());

      // wait 2 sec just for
      try {
        Thread.sleep(2000);
      } catch (InterruptedException e) {
        Tracer.platformLogger.error(e);
      }

      // show message
      pageState.addMessageInfo("A verification e-mail has been sent to: \"" + op.getDisplayName() + "\" to change password.<br>Check e-mail and click verification link.");

      //send forgot password message
    } else
      pageState.addMessageInfo("Impossible to send a new password to " + op.getDisplayName() + " as no e-mail is set. Contact "+I18n.g("QA_APP_NAME")+" Support.");


  } else {
    pageState.addMessageError("The e-mail is not on our archives: \"" + username + "\"");

  }

} else if (Commands.LOGOUT.equals(pageState.command)) {
  new LoginAction().logout(pageState, request, response);

  //remove login cookies
  Cookie killMyCookie = new Cookie("COOKIMMENSO", null);
  killMyCookie.setMaxAge(0);
  killMyCookie.setPath(ApplicationState.contextPath + "/applications/QA/site/access");
  response.addCookie(killMyCookie);

  killMyCookie = new Cookie("QALOG", null);
  killMyCookie.setMaxAge(0);
  killMyCookie.setPath(ApplicationState.contextPath + "/");
  response.addCookie(killMyCookie);

  // redirect to site home
  PageSeed redirTo = pageState.pageFromRoot("talk/index.jsp");
  response.sendRedirect(redirTo.toLinkToHref());
}

  pageState.toHtml(pageContext);

} else {

 PageSeed enroll = pageState.pageFromRoot("site/access/enrollMail.jsp");


%>

<jsp:include page="../../parts/partCommunityMenu.jsp">
    <jsp:param name="SHOW_ADD" value="no"></jsp:param>
    <jsp:param name="SHOW_BACK" value="yes"></jsp:param>
</jsp:include>
<div id="content">

    <h2><span><%=I18n.g("QA_APP_NAME")%>: <%=I18n.get("QA_DOLOGIN")%></span></h2>
    


    <div id="wsAccess"><%

      PageSeed maips= pageState.thisPage(request);
      maips.command="log";
      pageState.tokenCreate("login",maips);
      Form maif= new Form(maips);
      maif.id="login";
      maif.start(pageContext);

    %><div class="loginStart"><%=I18n.get("QA_LOGIN_START_WRITING",enroll.toLinkToHref())%></div><br>
      <div align="center"><div class="servicesEnroll">
        <span style="display: block; margin-bottom: 20px"><%=I18n.get("USING_WEB_SERVICES")%>:</span>
        <%

        PageSeed login = pageState.pageFromRoot("site/access/login.jsp");
        ButtonLink log = new ButtonLink(login);
        log.label = "click here";


      %><div><jsp:include page="servicesPart.jsp"><jsp:param name="showLogin" value="no"></jsp:param></jsp:include></div>

      </div>
          <div style="text-align: center"><h3 class="inlineHeader">or</h3></div>
      <div class="mpEnroll">
        <span style="display: block; margin-bottom: 20px; text-align: center"><%=I18n.get("QA_LOGIN_YOUR_ACCOUNT")%>:</span>
        <p><%--<label></label>--%>
        <%
        TextField username = new TextField("","USERNAME","",20,false);
        username.fieldSize=41;
        username.required=true;
        username.script="placeHolder='"+I18n.g("USERNAME")+"'";

        username.addKeyPressControl(13, "$('#PWD').focus();", "onkeyup");
        username.toHtmlI18n(pageContext);
          %></p><p><%--<label></label>--%><%
        TextField pwd= new TextField("PASSWORD","PWD","",27);
        pwd.label = "";
        pwd.script="placeHolder='password'";

        //pwd.required=true;
        pwd.addKeyPressControl(13, "$('#login').submit();", "onkeyup");
        pwd.toHtmlI18n(pageContext);

      %></p><%

        ButtonSubmit resp = ButtonSubmit.getSaveInstance(maif, I18n.g("SENDRESETPASS"));
        resp.variationsFromForm.command="SENDRESETPASS";
        resp.variationsFromForm.setPopup(pageState.isPopup());

/*
        ButtonSubmit bsem = ButtonSubmit.getSaveInstance(maif,"login");
        bsem.variationsFromForm.command="log";
        bsem.label="log in";
        bsem.toHtml(pageContext);
*/

      %><div style="text-align:right"><button type="submit" onclick="stopBubble(event);obj('loginCM').value='log';if (canSubmitForm('login'))  {muteAlertOnChange=true; try {obj('login').submit();} catch(e){}} ">log in</button></div>

        <br><p class="resetPwd" style="line-height:14px; margin:0">
        <%=I18n.g("SENDRESETPASS_QUESTION")%> <%resp.toHtmlInTextOnlyModality(pageContext);%></p><%

        if (JSP.ex(pageState.getEntry("USERNAME").errorCode) && pageState.getEntry("USERNAME").errorCode.indexOf("object not found")>-1) {
      %><big><%=I18n.g("QA_REGISTER_NEW")%></big><%
        }

        if ("ENROLL".equals(pageState.command)) {
      %><span style="font-size:14px">Click <a href="enrollMail.jsp" class="buttonSmall">here</a> to register a new account.</span>If you have an account already, log in below.<%
        }
      %></div><br style="clear: both;"
      <%

        maif.end(pageContext);

      %></div></div>

</div>


<%-- RIGHT COLUMN --%>
<div id="rightColumn">
  <%-- ads --%>
      <jsp:include page="../../parts/partPromo.jsp"/>
      <jsp:include page="../../parts/partShare.jsp"/>
</div>



<style>
  form p { position:relative }
  label  { position:absolute; top:0; left:0}
</style>


<script type="text/javascript">
  $(document).ready(function() {
    initialize("<%=request.getContextPath()+"/commons/js/jquery/jquery.caret.js"%>",true);
    $(".oidButton,.oidButtonSmall").bind("click", clickBtn);
    $("#USERNAME").focus();
  });

  function clickBtn(e) {
    var url = $(this).attr("url");
    $("#OPENID").val(url);
    $("#OPENID").caret("[YOUR ACCOUNT]");
  }

  function callTwitter(){
    var req = { "CM":"TWITTERAUTH"};
    $.getJSON('/applications/QA/site/access/parts/oauthRequester.jsp', req, function(response) {
      if (response.ok)
        location.href =response.url;
      else
        alert("Twitter login is not currently available.")
    });
  }
  function callFacebook(){
    var req = { "CM":"FACEBOOKAUTH"};
    $.getJSON('/applications/QA/site/access/parts/oauthRequester.jsp', req, function(response) {
      if (response.ok)
        location.href =response.url;
      else
        alert("Facebook login is not currently available.")
    });
  }
</script><%

  if (!ApplicationState.platformConfiguration.development) {
%><script type="text/javascript">

  var gaJsHost = (("https:" == document.location.protocol) ? "https://ssl." : "http://www.");
  document.write(unescape("%3Cscript src='" + gaJsHost + "google-analytics.com/ga.js' type='text/javascript'%3E%3C/script%3E"));

  try {
    var pageTracker = _gat._getTracker("");
    pageTracker._trackPageview();
  } catch(err) {}
</script><%
    }
  }

%>