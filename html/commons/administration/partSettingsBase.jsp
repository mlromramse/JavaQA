<%@ page import="org.jblooming.system.SystemConstants,
                  org.jblooming.utilities.CodeValueList,
                  org.jblooming.utilities.JSP,
                  org.jblooming.waf.constants.Commands,
                  org.jblooming.waf.constants.Fields,
                  org.jblooming.waf.html.button.ButtonLink,
                  org.jblooming.waf.html.input.Combo,
                  org.jblooming.waf.html.input.TextField, org.jblooming.waf.settings.ApplicationState, org.jblooming.waf.view.PageSeed, org.jblooming.waf.view.PageState, java.io.File" %><%
  PageState pageState = PageState.getCurrentPageState();

  if (!Commands.SAVE.equals(pageState.command)) {
    pageState.addClientEntry(SystemConstants.FLD_REPOSITORY_URL, ApplicationState.getApplicationSetting(SystemConstants.FLD_REPOSITORY_URL));
    pageState.addClientEntry(SystemConstants.STORAGE_PATH_ALLOWED, ApplicationState.getApplicationSetting(SystemConstants.STORAGE_PATH_ALLOWED));
    pageState.addClientEntry(SystemConstants.PUBLIC_SERVER_NAME, ApplicationState.getApplicationSetting(SystemConstants.PUBLIC_SERVER_NAME));
    pageState.addClientEntry(SystemConstants.PUBLIC_SERVER_PORT, ApplicationState.getApplicationSetting(SystemConstants.PUBLIC_SERVER_PORT));
    pageState.addClientEntry(SystemConstants.HTTP_PROTOCOL, ApplicationState.getApplicationSetting(SystemConstants.HTTP_PROTOCOL));
    pageState.addClientEntry(SystemConstants.UPLOAD_MAX_SIZE, ApplicationState.getApplicationSetting(SystemConstants.UPLOAD_MAX_SIZE));
  }

%><tr><th colspan="99">Base configuration</th></tr>
<tr><td><%

     TextField textField = new TextField(SystemConstants.FLD_REPOSITORY_URL, SystemConstants.FLD_REPOSITORY_URL, "</td><td>", 30, false);
     textField.toolTip = SystemConstants.FLD_REPOSITORY_URL;
    textField.readOnly = Fields.TRUE.equals(ApplicationState.getApplicationSetting("TEAMWORK_HOST_MODE"));
     textField.toHtmlI18n(pageContext);%>
  </td>
  <td><%

  if (System.getProperty("os.name").indexOf("Windows")>-1) {
    %>c:\demo\repository <%
  } else {
    %>usr/local/teamwork/repository<%
  }

  %></td>
  <td>Folder where all files uploaded will be saved.<%

    String FLD_REPOSITORY_URL = ApplicationState.getApplicationSetting(SystemConstants.FLD_REPOSITORY_URL);
    if (FLD_REPOSITORY_URL != null && !new File(FLD_REPOSITORY_URL).exists()) {
  %><br><big><font color="<%=pageState.sessionState.getSkin().COLOR_WARNING%>"><b><%=SystemConstants.FLD_REPOSITORY_URL%> does
    not seem to exist: <%=FLD_REPOSITORY_URL%>!</b></font></big><%
  }
    %></td>
</tr>

<tr><td><%
     textField = TextField.getIntegerInstance(SystemConstants.UPLOAD_MAX_SIZE);
     textField.fieldSize=4;
     textField.toolTip = SystemConstants.UPLOAD_MAX_SIZE;
     textField.toHtmlI18n(pageContext);%>
  </td>
  <td>20</td>
  <td>Max size in MB of files uploaded.</td>
</tr>



<tr>
  <td><%

    textField = new TextField(SystemConstants.STORAGE_PATH_ALLOWED, SystemConstants.STORAGE_PATH_ALLOWED, "</td><td>", 30, false);
    textField.toolTip = SystemConstants.STORAGE_PATH_ALLOWED;
    textField.readOnly = Fields.TRUE.equals(ApplicationState.getApplicationSetting("TEAMWORK_HOST_MODE"));      
    textField.toHtmlI18n(pageContext);

  %></td>
  <td><%
     if (System.getProperty("os.name").indexOf("Windows")>-1) {
    %>c:\demo\projects,d:\shared\files<%
  } else {
    %>/usr/local/demo/projects,/usr/local/demo/dataExchange<%
  }
  %></td>
  <td>Use comma separated paths.<%

  String STORAGE = ApplicationState.getApplicationSetting(SystemConstants.STORAGE_PATH_ALLOWED);
  if (STORAGE != null && STORAGE.indexOf(";") > -1) {
    %><big><font color="<%=pageState.sessionState.getSkin().COLOR_WARNING%>"><b>Must use comma ","
    as separator for storage path (<%=SystemConstants.STORAGE_PATH_ALLOWED%>),
    not semi-colon ";"!</b></font></big><br><%
  }
  else
  if (STORAGE != null && STORAGE.indexOf(",") == -1 && !new File(STORAGE).exists()) {
    %><big><font color="<%=pageState.sessionState.getSkin().COLOR_WARNING%>"><b><%=SystemConstants.STORAGE_PATH_ALLOWED%>
    contains a non existent root:
    <%=STORAGE%>!</b></font></big><%
  }

  %></td>
</tr>
<tr>
  <td><%
   String psn = ApplicationState.getApplicationSetting(SystemConstants.PUBLIC_SERVER_NAME);

    textField = new TextField(SystemConstants.PUBLIC_SERVER_NAME, SystemConstants.PUBLIC_SERVER_NAME, "</td><td>", 20, false);
    textField.toolTip = SystemConstants.PUBLIC_SERVER_NAME;
    textField.toHtmlI18n(pageContext);%>

 </td>
  <td>servername.yourdomain.com</td>
  <td><a href="#domain">For details, see below.</a><br><br>
    After having saved a new value, you may test it. Warn: it may take some time: <%
    PageSeed test = pageState.pageFromCommonsRoot("administration/testSocket.jsp");
    test.addClientEntry("SOCKET_SERVER", psn);
    test.addClientEntry("SOCKET_PORT", 80);
    ButtonLink testBL = new ButtonLink("click to test server", test);
    testBL.popup_height = "100";
    testBL.popup_width = "400";
    testBL.target = ButtonLink.TARGET_BLANK;
    testBL.toHtmlInTextOnlyModality(pageContext);
  %></td>
</tr>
<tr>
  <td><%

    textField = new TextField(SystemConstants.PUBLIC_SERVER_PORT, SystemConstants.PUBLIC_SERVER_PORT, "</td><td>", 4, false);
    textField.toolTip = SystemConstants.PUBLIC_SERVER_PORT;
    textField.toHtmlI18n(pageContext);%>
 </td>
  <td><%=request.getServerPort()%></td>
  <td>Normally, leave this value empty. This has to be set up by hand ONLY if the http port used locally by the server is different from
    the port which is used to contact the server from the client's browsers. Otherwise itis automatically configured.<%
     String psp = ApplicationState.getApplicationSetting(SystemConstants.PUBLIC_SERVER_PORT);
    if (psp==null || psp.trim().length()==0) {
      %>Current port used on the server is <%=request.getServerPort()%>.<%
    }
    %></td>
</tr>

<tr>
  <td><%

    String protocol = request.getRequestURL().substring(0, request.getRequestURL().indexOf("//"));

    CodeValueList cvl = new CodeValueList();
    cvl.add("", "leave it as it is");
    cvl.add("http","http");
    cvl.add("https","https (http+SSL)");

    Combo cb = new Combo(SystemConstants.HTTP_PROTOCOL, "</td><td>", null, 20, cvl, null);
    cb.toolTip = SystemConstants.HTTP_PROTOCOL;
    cb.toHtmlI18n(pageContext);
%>
 </td>
  <td>http</td>
  <td>Normally, leave this value empty. This has to be set up by hand ONLY if the http protocol used locally by the server is different from
    the port which is used to contact the server from the client's browsers. Otherwise it is automatically configured.<br><%
    String protocolValue = ApplicationState.getApplicationSetting(SystemConstants.HTTP_PROTOCOL);
    if (JSP.ex(protocol)) {
      %>Protocol for this request is <%=protocol%>. <%
    }
    if (JSP.ex(protocolValue)) {
      %>Protocol in memory value is <%=protocolValue%>.<%
    }
    %> Server complete URL used is <%=ApplicationState.serverURL%>.</td>
</tr>