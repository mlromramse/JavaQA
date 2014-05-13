<%@ page import="org.apache.log4j.Level,
                 org.jblooming.system.SystemConstants,
                 org.jblooming.tracer.Tracer,
                 org.jblooming.utilities.file.fileStorage.FileStorageUtilities,
                 org.jblooming.waf.ScreenBasic,
                 org.jblooming.waf.SessionState,
                 org.jblooming.waf.constants.Commands,
                 org.jblooming.waf.constants.Fields,
                 org.jblooming.waf.html.button.ButtonJS,
                 org.jblooming.waf.html.button.ButtonSubmit,
                 org.jblooming.waf.html.container.ButtonBar,
                 org.jblooming.waf.html.container.Container,
                 org.jblooming.waf.html.input.RadioButton,
                 org.jblooming.waf.html.layout.Skin,
                 org.jblooming.waf.html.state.Form,
                 org.jblooming.waf.settings.I18n,
                 org.jblooming.waf.settings.PlatformConfiguration,
                 org.jblooming.waf.view.PageSeed,
                 org.jblooming.waf.view.PageState, java.io.File"%><%

  PageState pageState = PageState.getCurrentPageState();

  pageState.getLoggedOperator().testIsAdministrator();

  if (!pageState.screenRunning) {
    ScreenBasic.preparePage(pageContext);
    pageState.perform(request, response).toHtml(pageContext);
  } else {


  if (Commands.SAVE.equals(pageState.getCommand())) {
    Tracer.platformLogger.setLevel(Level.toLevel(pageState.getEntry(SystemConstants.FLD_LOG_PLATFORM_LEVEL).intValue()));
    Tracer.hibernateLogger.setLevel(Level.toLevel(pageState.getEntry(SystemConstants.FLD_LOG_HIB_LEVEL).intValue()));
    Tracer.i18nLogger.setLevel(Level.toLevel(pageState.getEntry(SystemConstants.FLD_LOG_I18N_LEVEL).intValue()));
    Tracer.jobLogger.setLevel(Level.toLevel(pageState.getEntry(SystemConstants.FLD_LOG_JOB_LEVEL).intValue()));
    Tracer.emailLogger.setLevel(Level.toLevel(pageState.getEntry(SystemConstants.FLD_LOG_EMAIL_LEVEL).intValue()));

    //String logOut = pageState.getEntry("LOG_OUT").stringValue();
   /* if ("LOG_ON_FILE".equals(logOut)) {
      lc.setLogOnFile(true);
      lc.setLogOnConsole(false);
    } else {
      lc.setLogOnFile(false);
      lc.setLogOnConsole(true);
    }*/
  } else {
    pageState.addClientEntry(SystemConstants.FLD_LOG_PLATFORM_LEVEL, Tracer.platformLogger.getLevel().toInt() + "");
    pageState.addClientEntry(SystemConstants.FLD_LOG_HIB_LEVEL, Tracer.hibernateLogger.getLevel().toInt() + "");
    pageState.addClientEntry(SystemConstants.FLD_LOG_I18N_LEVEL, Tracer.i18nLogger.getLevel().toInt() + "");
    pageState.addClientEntry(SystemConstants.FLD_LOG_JOB_LEVEL, Tracer.jobLogger.getLevel().toInt() + "");
    pageState.addClientEntry(SystemConstants.FLD_LOG_EMAIL_LEVEL, Tracer.emailLogger.getLevel().toInt() + "");
  }

  //ce.addEntry(new ClientEntry("LOG_OUT",lc.isLogOnFile() ? "LOG_ON_FILE" : "LOG_ON_CONSOLE"));

  SessionState sessionState = pageState.getSessionState();
  Skin skin = sessionState.getSkin();

  PageSeed self = pageState.thisPage(request);
  self.setCommand(Commands.SAVE);
  if (pageState.getEntry(Fields.APPLICATION_NAME).stringValueNullIfEmpty() != null)
    self.addClientEntry(Fields.APPLICATION_NAME, pageState.getEntry(Fields.APPLICATION_NAME).stringValueNullIfEmpty());

  Form f = new Form(self);
  pageState.setForm(f);
  f.start(pageContext);

  Container logAndMonitoring = new Container();
  logAndMonitoring.id = "ct_logAndMonitoring";
  logAndMonitoring.title = I18n.get("LOG_CONFIGURATION");
  logAndMonitoring.start(pageContext);

  String root = request.getSession().getServletContext().getRealPath("/").replaceAll("\\\\", "/");
  String logPath = root + "WEB-INF/log/";

  %><table><tr><td><%

  Level levels [] = {Level.ALL,Level.DEBUG, Level.INFO,Level.WARN,Level.ERROR,Level.FATAL};
  %><tr><td>"platform" <%=I18n.get("LOG_LEVEL")%>: </td><td><%
  for (int i = 0; i < levels.length; i++) {
    RadioButton rbs = new RadioButton(levels[i].toString().toLowerCase(), SystemConstants.FLD_LOG_PLATFORM_LEVEL, levels[i].toInt()+"",
                  "&nbsp;", "", false, "", pageState);
    rbs.translateLabel = false;
    rbs.toHtml(pageContext);
  }

  %></td></tr><tr><td>"persistence" <%=I18n.get("LOG_LEVEL")%>:</td><td><%
  for (int i = 0; i < levels.length; i++) {
    RadioButton rbs = new RadioButton(levels[i].toString().toLowerCase(), SystemConstants.FLD_LOG_HIB_LEVEL, levels[i].toInt()+"",
                  "&nbsp;", "", false, "", pageState);
    rbs.translateLabel = false;
    rbs.toHtml(pageContext);
  }

  %></td></tr>

<tr><td>"i18n" <%=I18n.get("LOG_LEVEL")%>: </td><td><%
  for (int i = 0; i < levels.length; i++) {
    RadioButton rbs = new RadioButton(levels[i].toString().toLowerCase(), SystemConstants.FLD_LOG_I18N_LEVEL, levels[i].toInt()+"",
                  "&nbsp;", "", false, "", pageState);
    rbs.translateLabel = false;
    rbs.toHtml(pageContext);
  }
  %></td></tr>

<tr><td>"job" <%=I18n.get("LOG_LEVEL")%>: </td><td><%
  for (int i = 0; i < levels.length; i++) {
    RadioButton rbs = new RadioButton(levels[i].toString().toLowerCase(), SystemConstants.FLD_LOG_JOB_LEVEL, levels[i].toInt()+"",
                  "&nbsp;", "", false, "", pageState);
    rbs.translateLabel = false;
    rbs.toHtml(pageContext);
  }
  %></td></tr>

  <tr><td>"email" <%=I18n.get("LOG_LEVEL")%>: </td><td><%
  for (int i = 0; i < levels.length; i++) {
    RadioButton rbs = new RadioButton(levels[i].toString().toLowerCase(), SystemConstants.FLD_LOG_EMAIL_LEVEL, levels[i].toInt()+"",
                  "&nbsp;", "", false, "", pageState);
    rbs.translateLabel = false;
    rbs.toHtml(pageContext);
  }
  %></td></tr>
 
  <tr><td>&nbsp;</td></tr>
  <tr><td><%=I18n.get("LOG_OUT_MODALITY")%>: <%=I18n.get(PlatformConfiguration.logOnFile ? "LOG_ON_FILE" : "LOG_ON_CONSOLE")%></td><%

/*    RadioButton rbs = new RadioButton(I18n.get("LOG_ON_FILE"), "LOG_OUT", "LOG_ON_FILE", "&nbsp;", null, false, null, pageState);
    rbs.toHtml(pageContext);
    rbs = new RadioButton(I18n.get("LOG_ON_CONSOLE"), "LOG_OUT", "LOG_ON_CONSOLE", "&nbsp;", null, false, null, pageState);
    rbs.toHtml(pageContext);*/

  %></tr>
  </table><%

  if (PlatformConfiguration.logOnFile) {
    Container logFiles = new Container();
    logFiles.title = I18n.get("LOG_FILES");
    logFiles.setCssPostfix("thin");
    logFiles.start(pageContext);
    %> <%=I18n.get("LOG_FILES_IN")%> <%=logPath%>:<br><br><%

    File logs = new File(logPath);
    File[] logFls = logs.listFiles();
    if (logFls!=null && logFls.length>0) {
      for (int i = 0; i < logFls.length; i++) {
        File logFl = logFls[i];
        %><%=logFl.getName()%>( <%=FileStorageUtilities.convertFileSize(logFl.length())%>)<br><%
      }
    } else {
      %><%=I18n.get("NO_LOG_FILES")%><%
    }
    logFiles.end(pageContext);
    %><br><%
  }

  logAndMonitoring.end(pageContext);
  
  ButtonBar bb2= new ButtonBar();
/*
  PageSeed helpJob = new PageSeed(request.getContextPath()+"/commons/help.jsp");
  //helpJob.addClientEntry("TOPIC","commons/scheduler/jobeditor.jsp"+Help.separator+"LAUNCHER_CLASS");
  helpJob.addClientEntry("TOPIC","commons/administration/log.jsp");
  ButtonLink helpJobLK  = new ButtonLink(I18n.get("HELP"),helpJob);
  helpJobLK.target = "blank";
  helpJobLK.popup_height = "300";
  helpJobLK.popup_width = "400";
  bb2.*/

  bb2.addButton(ButtonJS.getResetInstance(f,pageState));

  ButtonSubmit save = new ButtonSubmit(f);
  save.label =I18n.get("SAVE");
  bb2.addButton(save);
  bb2.toHtml(pageContext);

  f.end(pageContext);
}
%>