<%@ page import="org.jblooming.system.SystemConstants,
                 org.jblooming.utilities.JSP,
                 org.jblooming.utilities.StringUtilities,
                 org.jblooming.waf.constants.Commands,
                 org.jblooming.waf.html.button.ButtonLink,
                 org.jblooming.waf.html.input.CheckField,
                 org.jblooming.waf.html.input.ComboBox,
                 org.jblooming.waf.html.input.TextField, org.jblooming.waf.settings.ApplicationState, org.jblooming.waf.view.PageSeed, org.jblooming.waf.view.PageState" %><%

  PageState pageState = PageState.getCurrentPageState();

  if (!Commands.SAVE.equals(pageState.command)) {
    pageState.addClientEntry(SystemConstants.FLD_MAIL_FROM, ApplicationState.getApplicationSetting(SystemConstants.FLD_MAIL_FROM));
    pageState.addClientEntry(SystemConstants.FLD_MAIL_SMTP, ApplicationState.getApplicationSetting(SystemConstants.FLD_MAIL_SMTP));
    pageState.addClientEntry(SystemConstants.FLD_MAIL_SMTP_PORT, ApplicationState.getApplicationSetting(SystemConstants.FLD_MAIL_SMTP_PORT));
    pageState.addClientEntry(SystemConstants.FLD_MAIL_SUBJECT, ApplicationState.getApplicationSetting(SystemConstants.FLD_MAIL_SUBJECT));

    pageState.addClientEntry(SystemConstants.FLD_MAIL_USE_AUTHENTICATED, ApplicationState.getApplicationSetting(SystemConstants.FLD_MAIL_USE_AUTHENTICATED));
    pageState.addClientEntry(SystemConstants.FLD_MAIL_USER, ApplicationState.getApplicationSetting(SystemConstants.FLD_MAIL_USER));
    pageState.addClientEntry(SystemConstants.FLD_MAIL_PWD, ApplicationState.getApplicationSetting(SystemConstants.FLD_MAIL_PWD));
    pageState.addClientEntry(SystemConstants.FLD_MAIL_PROTOCOL, ApplicationState.getApplicationSetting(SystemConstants.FLD_MAIL_PROTOCOL));

    pageState.addClientEntry(SystemConstants.FLD_POP3_USER, ApplicationState.getApplicationSetting(SystemConstants.FLD_POP3_USER));
    pageState.addClientEntry(SystemConstants.FLD_POP3_PSW, ApplicationState.getApplicationSetting(SystemConstants.FLD_POP3_PSW));
    pageState.addClientEntry(SystemConstants.FLD_POP3_HOST, ApplicationState.getApplicationSetting(SystemConstants.FLD_POP3_HOST));    
    pageState.addClientEntry(SystemConstants.FLD_POP3_PORT, ApplicationState.getApplicationSetting(SystemConstants.FLD_POP3_PORT));
    pageState.addClientEntry(SystemConstants.FLD_EMAIL_DOWNLOAD_PROTOCOL, ApplicationState.getApplicationSetting(SystemConstants.FLD_EMAIL_DOWNLOAD_PROTOCOL));

  }
%><tr><th colspan="4">Mail</th></tr>
<tr><td><%

    TextField textField = new TextField(SystemConstants.FLD_MAIL_FROM, SystemConstants.FLD_MAIL_FROM, "</td><td>", 30, false);
    textField.toolTip = SystemConstants.FLD_MAIL_FROM;
    textField.toHtmlI18n(pageContext);
  %></td>
  <td>teamworkmail@yourdomain.com</td>
  <td>This address will be used to send messages, if SMTP is configured. If POP3 (or IMAP,...) is configured, this should be the e-mail
    address corresponding to the POP3 account.
    In order to send messages by mail, and receive email notifications, this must be set.
  </td>
</tr>
<tr><td><%
    TextField defaultSubject = new TextField(SystemConstants.FLD_MAIL_SUBJECT, SystemConstants.FLD_MAIL_SUBJECT, "</td><td>", 30, false);
    defaultSubject.toolTip = SystemConstants.FLD_MAIL_SUBJECT;
    defaultSubject.toHtmlI18n(pageContext);
  %></td>
  <td>Teamwork notification:</td>
  <td>This text will be added as prefix in the subject of each notification sent from Teamwork by e-mail.
  </td>
</tr>
<tr><th colspan="4" align="center"><small>SMTP - sending</small></th></tr>
<tr>
  <td><%
    String smtp = ApplicationState.getApplicationSetting(SystemConstants.FLD_MAIL_SMTP);

    textField = new TextField(SystemConstants.FLD_MAIL_SMTP, SystemConstants.FLD_MAIL_SMTP, "</td><td>", 30, false);
    textField.toolTip = SystemConstants.FLD_MAIL_SMTP;
    textField.toHtmlI18n(pageContext);
  %></td>
  <td>smtp.yourserver.com</td>
  <td>In order to send messages by mail, and receive email notifications, this must be set.<br><br>
    After having saved a new value, you may test it. Warn: it may take some time: <%
    PageSeed test = pageState.pageFromCommonsRoot("administration/testSocket.jsp");
    test.addClientEntry("SOCKET_SERVER", smtp);
    int port = 25;
    String portS = ApplicationState.getApplicationSetting(SystemConstants.FLD_MAIL_SMTP_PORT);
    try {
      port = Integer.parseInt(portS);
    } catch (NumberFormatException e) {
    }
    test.addClientEntry("SOCKET_PORT", port);
    ButtonLink testBL = new ButtonLink("click to test server", test);
    testBL.popup_height = "100";
    testBL.popup_width = "400";
    testBL.target = ButtonLink.TARGET_BLANK;
    testBL.toHtmlInTextOnlyModality(pageContext);

  %></td>
</tr>

<tr>
  <td nowrap><%
    textField = new TextField(StringUtilities.replaceAllNoRegex(SystemConstants.FLD_MAIL_SMTP_PORT, "_", " ").toLowerCase(), SystemConstants.FLD_MAIL_SMTP_PORT, "</td><td>", 30, false);
    textField.toolTip = SystemConstants.FLD_MAIL_SMTP_PORT;
    textField.fieldSize = 4;
    textField.toHtmlI18n(pageContext);
  %>
  </td>
  <td>25</td>
  <td>If left empty, the standard port (25 or 465 for smtps) will be used.</td>
</tr>


<tr>
  <td nowrap><%

    CheckField cf = new CheckField(SystemConstants.FLD_MAIL_USE_AUTHENTICATED, SystemConstants.FLD_MAIL_USE_AUTHENTICATED, "</td><td>", true);
    cf.toolTip = SystemConstants.FLD_MAIL_USE_AUTHENTICATED;
    cf.toHtmlI18n(pageContext);%>
  </td>
  <td>&nbsp;</td>
  <td>For details, see the "e-mail configuration" chapter of the <a href="http://www.twproject.com/documentation.page" target="_blank">user guide</a>.</td>
</tr>
<tr><td><%

    textField = new TextField(SystemConstants.FLD_MAIL_USER, SystemConstants.FLD_MAIL_USER, "</td><td>", 30, false);
    textField.toolTip = SystemConstants.FLD_MAIL_USER;
    textField.toHtmlI18n(pageContext);
  %></td>
  <td>&nbsp;</td>
  <td></td>
</tr>
<tr><td><%

    textField = new TextField("PASSWORD",SystemConstants.FLD_MAIL_PWD, SystemConstants.FLD_MAIL_PWD, "</td><td>", 20, false);
    textField.toolTip = SystemConstants.FLD_MAIL_PWD;
    textField.toHtmlI18n(pageContext);
  %></td>
  <td>&nbsp;</td>
  <td></td>
</tr>
<tr><td><%

  ComboBox mailProtocol = new ComboBox(SystemConstants.FLD_MAIL_PROTOCOL,SystemConstants.FLD_MAIL_PROTOCOL,SystemConstants.FLD_MAIL_PROTOCOL,pageState);
  mailProtocol.separator =  "</td><td>";
  mailProtocol.toolTip = SystemConstants.FLD_MAIL_PROTOCOL;
  mailProtocol.addValue("smtp");
  mailProtocol.addValue("smtps");
  mailProtocol.toHtmlI18n(pageContext);

  %></td>
<td>smtp</td>
  <td>Use smtps for example to use Gmail as SMTP server.</td></tr>


<%-----------------------     POP3/IMAP --------------------------------------------------------------------------------------------------------------%>
<tr><th colspan="4" align="center"><small>POP3/IMAP - receiving</small></th></tr>
<tr><td><%
    String pop3 = ApplicationState.getApplicationSetting(SystemConstants.FLD_POP3_HOST);
    textField = new TextField(SystemConstants.FLD_POP3_HOST, SystemConstants.FLD_POP3_HOST, "</td><td>", 30, false);
    textField.toolTip = SystemConstants.FLD_POP3_HOST;
    textField.toHtmlI18n(pageContext);

    int pop3Port=pageState.getEntry(SystemConstants.FLD_POP3_PORT).intValueNoErrorCodeNoExc();
    pop3Port=pop3Port==0?-1:pop3Port;


  %></td>
  <td>pop3.yourserver.com</td>
  <td>In order to enable Teamwork to receive messages by mail, this must be set.<br><br>
    After having saved a new value, you may test it. Warn: it may take some time: <%
    test = pageState.pageFromCommonsRoot("administration/testSocket.jsp");
    test.addClientEntry("SOCKET_SERVER", pop3);
    test.addClientEntry("SOCKET_PORT", pop3Port);
    testBL = new ButtonLink("click to test server", test);
    testBL.popup_height = "100";
    testBL.popup_width = "400";
    testBL.target = ButtonLink.TARGET_BLANK;
    testBL.toHtmlInTextOnlyModality(pageContext);
  %></td>
</tr>
<tr><td><%

    textField = new TextField(SystemConstants.FLD_POP3_USER, SystemConstants.FLD_POP3_USER, "</td><td>", 30, false);
    textField.toolTip = SystemConstants.FLD_POP3_USER;
    textField.toHtmlI18n(pageContext);

  %></td>
  <td>&nbsp;</td>
  <td>In order to enable Teamwork to receive messages by mail, this must be set.</td>
</tr>
<tr>
  <td><%

    textField = new TextField("PASSWORD",SystemConstants.FLD_POP3_PSW, SystemConstants.FLD_POP3_PSW, "</td><td>", 20, false);
    textField.toolTip = SystemConstants.FLD_POP3_PSW;
    textField.toHtmlI18n(pageContext);

  %></td>
  <td>&nbsp;</td>
  <td>In order to enable Teamwork to receive messages by mail, this must be set. <%

    if (JSP.ex(ApplicationState.getApplicationSetting(SystemConstants.FLD_POP3_HOST))) {
       PageSeed log = pageState.pageFromRoot("administration/showLog.jsp");
           log.addClientEntry("LOG","email.log");
           ButtonLink.getPopupInstance("see e-mail downloaded logs",600,800, log).toHtmlInTextOnlyModality(pageContext);
    }

  %></td>
</tr>
<tr><td><%

  ComboBox mailDownProtocol = new ComboBox(SystemConstants.FLD_EMAIL_DOWNLOAD_PROTOCOL,SystemConstants.FLD_EMAIL_DOWNLOAD_PROTOCOL,SystemConstants.FLD_EMAIL_DOWNLOAD_PROTOCOL,pageState);
  mailDownProtocol.separator =  "</td><td>";
  mailDownProtocol.toolTip = SystemConstants.FLD_EMAIL_DOWNLOAD_PROTOCOL;
  mailDownProtocol.addValue("pop3");
  mailDownProtocol.addValue("pop3s");
  mailDownProtocol.addValue("imap");
  mailDownProtocol.toHtmlI18n(pageContext);

  %></td>
<td>pop3</td>
  <td>Use pop3s for example to use Gmail as POP3 server.</td>
</tr>
<tr>
  <td><%

    textField = TextField.getIntegerInstance(SystemConstants.FLD_POP3_PORT);
    textField.toolTip = SystemConstants.FLD_POP3_PORT;
    textField.fieldSize = 5;
    textField.toHtmlI18n(pageContext);

  %></td>
  <td>-1</td>
  <td>Leave -1 for default values: e.g: pop3=110, pop3s=995, imap=143 </td>
</tr>