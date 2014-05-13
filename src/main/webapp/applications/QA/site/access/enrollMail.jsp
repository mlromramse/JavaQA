<%@ page contentType="text/html; charset=utf-8" pageEncoding="UTF-8"%><%@ page import="com.QA.QAOperator,  com.QA.waf.QALoginAction, com.QA.waf.QAScreenApp,
org.jblooming.utilities.JSP, org.jblooming.waf.ScreenArea, org.jblooming.waf.html.button.ButtonLink, org.jblooming.waf.html.input.TextField,
org.jblooming.waf.html.state.Form, org.jblooming.waf.settings.ApplicationState, org.jblooming.waf.settings.I18n, org.jblooming.waf.view.PageSeed, org.jblooming.waf.view.PageState" %><%
  PageState pageState = PageState.getCurrentPageState();

  QAOperator logged = (QAOperator) pageState.getLoggedOperator();

  if(logged==null){
    Cookie loginCookie = null;
    //check if exists login cookie
    if (request.getCookies()!=null){
      for (Cookie coo : request.getCookies()) {
        if ("QALOG".equals(coo.getName()) && JSP.ex(coo.getValue())) {
          loginCookie=coo;
          break;
        }
      }
    }
    if(loginCookie!= null){
      String URL= "/applications/QA/talk/";
      PageSeed pendingUrl = new PageSeed(URL);
      pendingUrl.disableCache=false;
      pageState.sessionState.setLoginPendingUrl(pendingUrl);
      response.sendRedirect("/applications/QA/site/access/login.jsp");
    }
  }

  PageSeed home = pageState.pageFromRoot("talk/index.jsp");

  if (!pageState.screenRunning) {
    pageState.screenRunning = true;
    final ScreenArea body = new ScreenArea(request);
    QAScreenApp lw = new QAScreenApp(body);
    lw.register(pageState);
    pageState.getHeaderFooter().toolTip = I18n.g("QA_APP_NAME")+" - "+I18n.g("QA_PLAY");
    pageState.perform(request, response);

    if ("ENROLL".equals(pageState.command)) {
      // FIRST CHECK the token in session (and invalidate it in) in orded to avoid hack
      //pageState.tokenValidate("enrollEmail");

      QAOperator operator= QALoginAction.enroll(pageState, request, response);
      if (operator != null) {
        //login and redir to when not from client
        PageSeed startWorking = pageState.pageFromRoot("/talk/index.jsp");
        response.sendRedirect(startWorking.toLinkToHref());
        return;
      }
    }
    pageState.toHtml(pageContext);

  } else {


%><style type="text/css">

  label{
    margin:0;
  }
  .mpEnroll input{
    width:100%!important;
    margin:0px;
  }
  .tipTxt{
    display:block;
  }

  .mpEnroll{
    min-height: 380px
  }

</style>

<jsp:include page="../../parts/partCommunityMenu.jsp">
    <jsp:param name="SHOW_ADD" value="no"></jsp:param>
    <jsp:param name="SHOW_BACK" value="yes"></jsp:param>
</jsp:include>

 <div id="content">
    <h2><span><%=I18n.g("QA_APP_NAME")%>: <%=I18n.get("QA_DOENROLL")%></span></h2>


     <div id="wsAccess"><%

      PageSeed psc = pageState.thisPage(request);

      // generate a token in session in order to avoid hack
      pageState.tokenCreate("enrollEmail",psc);

      Form form = new Form(psc);
      form.id="enroll";
      form.alertOnChange = false;
      form.start(pageContext);
      pageState.setForm(form);

      PageSeed login = pageState.pageFromRoot("site/access/login.jsp");


    %>

      <div class="loginStart"><%=I18n.get("QA_ENROLL_START_WRITING",login.toLinkToHref())%></div> <br>

<div align="center">
      <div class="servicesEnroll">
        <span style="display: block; margin-bottom: 20px"><%=I18n.g("QA_ENROLL_SOCIAL")%>:</span>
        <%

          ButtonLink log = new ButtonLink(login);
          log.label = "click here";
          log.additionalCssClass = "smallBtn";


        %><div><jsp:include page="servicesPart.jsp"/>
          <div><br>or<br><br><button style="margin-bottom: 8px" onclick="showHideSpan('QA_ENROLL'); return false;" ><%=I18n.g("QA_CREATE_ACCOUNT")%></button><br><a
                  href="<%=login.toLinkToHref()%>"><%=I18n.g("QA_ALREADY_HAVE_ACCOUNT")%></a>
              <br><br>
          </div>

      </div>
      </div>
  <div style="font-size: 12px; margin-bottom: 20px"><%=I18n.g("QA_PRIVACY_TERMS")%></div>

  <div class="mpEnroll" <%if (pageState.validEntries()) {%>style="display: none"<%}%> id="QA_ENROLL">
        <span style="display: block; margin-bottom: 20px; text-align: center"><%=I18n.get("QA_ENROLL_YOUR_ACCOUNT")%>:</span>
        <table border="0" cellspacing="0" cellpadding="5" class="enrollField" align="left">
          <tr>
            <td class="fieldLabel" valign="top">
              <p><%
              TextField username = new TextField("","USERNAME","",20,false);
              username.script="onkeyup=\"checkUniqueAccountAndChars($(this),event);\"  autocomplete=\"off\"";
              username.fieldSize=41;
              username.required = true;
              username.tabIndex = 2;
              username.script+=" placeHolder='"+I18n.g("USERNAME")+"'";
              username.toHtmlI18n(pageContext);

            %><span class="tipTxt" style="font-size:1em; color:#808080"> (<%=I18n.get("WILL_BE_PUBLIC")%>)</span>
              <p id ="useValidChars" class="helpLOG" style="font-size:12px;color:#c13c21; font-weight:normal; display:none;"><%=I18n.get("LOGINNAME_USE_VALID_CHARS")%></p>
              <p id ="alreadExstUN" class="helpLOG" style="font-size:12px;color:#c13c21; font-weight:normal; display:none;">
                <%=I18n.get("ALREADY_EXISTING_LOGINNAME")%><br>
                  <span class="alreadExst validEmail"><%=I18n.get("ALREADY_ENROLLED_RESET_PASSWORD")%>&nbsp;<a  href="#" class="smallBtn dark textual" onclick="resetPassword($('#USERNAME').val());">
                    <%=I18n.get("CLICK_HERE")%></a></span>
                  <span class="alreadExst unverifiedEmail"><%=I18n.get("ALREADY_ENROLLED_UNVERIFIED_EMAIL")%><a  href="#" class="smallBtn dark textual" onclick="resendConfirmationEmail($('#USERNAME').val());">
                    <%=I18n.get("CLICK_HERE")%></a></span>
                  <span class="alreadExst noEmail"><%=I18n.get("ALREADY_ENROLLED_NO_EMAIL")%><a  href="#" class="smallBtn dark textual" onclick="contactUs($('#USERNAME').val());">
                    <%=I18n.get("CLICK_HERE")%></a></span>
                  <span class="alreadExst goToLogin"><br><br><%=I18n.get("ALREADY_ENROLLED_GOTO_LOGIN")%><a href="#" class="smallBtn dark textual" onclick="document.location.href='login.jsp?USERNAME='+$('#USERNAME').val();">
                    <%=I18n.get("CLICK_HERE")%></a></span>
              </p>
            </td>
          </tr>
          <tr>
            <td class="fieldLabel" valign="top" ><p>
              <%

              TextField email = TextField.getEmailInstance("EMAIL");
              email.label = "";
              email.separator= "";
              email.type="email";
              email.script="onkeyup=\"checkUniqueEmail($(this),event);\"  autocomplete=\"off\"";
              email.fieldSize=41;
              email.tabIndex = 3;
              email.required = true;
              email.script+=" placeHolder='e-mail' type='email'";


              //email.addKeyPressControl(Dom.RETURN, "obj('PASSWORD1').focus();", Dom.KEYPRESS);
              email.toHtmlI18n(pageContext);

            %><br><span class="tipTxt" style="font-size:1em; color:#808080"><%=I18n.get("PLEASE_INSERT_EMAIL")%></span>
              <div id ="alreadExstEmail" class="helpLOG alreadExstEmail"  style="font-size:12px; display:none;"><%=I18n.get("ALREADY_EXISTING_EMAIL")%></div>
              <div id ="alreadExstUNEM" class="alreadExst unverifiedEmail" style="font-size:12px; display:none;"><%=I18n.get("ALREADY_UNVERIFIED_EMAIL")%><a  href="#" class="smallBtn dark textual" onclick="resendConfirmationEmailUnconfirmed($('#EMAIL').val());"><%=I18n.get("CLICK_HERE")%></a></div>
            </td>
          </tr>
          <tr>
            <td class="fieldLabel" valign="top"><p>
              <%

              TextField tf = new TextField("password","PASSWORD1", "",20);
              tf.required = true;
              tf.label = "";
              //tf.addKeyPressControl(Dom.RETURN, "obj('PASSWORD2').focus();", Dom.KEYPRESS);
              tf.script = tf.script + " autocomplete=\"off\"";
              tf.tabIndex = 4;
              tf.script+=" placeHolder='password'";
              tf.toHtmlI18n(pageContext);

            %></p> </td>
          </tr>
          <tr>
            <td class="fieldLabel" valign="top"><p>
              <%--<label>&nbsp;<%=I18n.g("PASSWORD_RETYPE")%>*</label>--%><%

              tf = new TextField("password","PASSWORD2", "",20);
              tf.required = true;
              tf.label = "";
              //tf.addKeyPressControl(Dom.RETURN, "obj('" + form.getUniqueName() + "').submit();", Dom.KEYPRESS);
              tf.script = tf.script + " autocomplete=\"off\"";
              tf.tabIndex = 5;
              tf.script+=" placeHolder='"+I18n.g("PASSWORD_RETYPE")+"'";
              tf.toHtmlI18n(pageContext);

            %></p></td>
          </tr>
          <tr>
            <td>
              <div class="wsTerms"><%

                log.label = "click here";
                log.additionalCssClass = "smallBtn";


              %>


                <div class="btnBar"><button
                        type="submit" onclick="stopBubble(event);obj('enrollCM').value='ENROLL';
                        if (canSubmitForm('form'))  {muteAlertOnChange=true; try {obj('enroll').submit();} catch(e){}} "><%=I18n.g("QA_SIGN_UP")%></button> <br></div>
                <br>


              </td>
          </tr>
        </table>

      </div>
       <%

         form.end(pageContext);

       %>
       <%
         String mailTo = ApplicationState.getApplicationSetting("MAIL_TO");

       %><jsp:include page="uniquenessTesterJs.jsp"/>
<br style="clear: both;">
      </div>
     </div>
</div>





<script type="text/javascript">
  var tk="<%=pageState.tokenCreate("login")%>";
  function resetPassword(username){
    document.location.href="login.jsp?CM=SENDRESETPASS&<%=PageState.TOKEN%>="+tk+"&USERNAME="+username;
  }

  function resendConfirmationEmail(username){
    document.location.href="login.jsp?CM=SENDCONFIRMEMAIL&<%=PageState.TOKEN%>="+tk+"&USERNAME="+username;
  }

  function resendConfirmationEmailUnconfirmed(unconfEmail){
    document.location.href="login.jsp?CM=SENDCONFIRMEMAIL&<%=PageState.TOKEN%>="+tk+"&UNCONFIRMEDEMAIL="+unconfEmail;
  }

  function contactUs(username){
    document.location.href="mailto:<%=mailTo%>?subject="+encodeURIComponent(I18n.g("QA_APP_NAME")+" account password forgotten for \""+username+"\"");
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
%>
<%
  }
%>