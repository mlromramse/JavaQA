<%@ page import="com.QA.waf.QAScreenApp" %>
<%@ page import="org.jblooming.scheduler.Scheduler" %>
<%@ page import="org.jblooming.system.ServerInfo" %>
<%@ page import="org.jblooming.utilities.file.fileStorage.FileStorageUtilities" %>
<%@ page import="org.jblooming.waf.ScreenArea, org.jblooming.waf.SessionState, org.jblooming.waf.html.button.ButtonLink, org.jblooming.waf.html.container.Container, org.jblooming.waf.html.layout.Skin, org.jblooming.waf.settings.ApplicationState, org.jblooming.waf.settings.I18n, org.jblooming.waf.settings.PersistenceConfiguration, org.jblooming.waf.settings.PlatformConfiguration, org.jblooming.waf.view.PageSeed, org.jblooming.waf.view.PageState, java.io.File, java.util.Iterator, java.util.Properties, java.util.TreeSet" %>
<%
  PageState pageState = PageState.getCurrentPageState();

  if (!pageState.screenRunning) {
    pageState.screenRunning = true;
    //put controller !
    final ScreenArea body = new ScreenArea(request);
    QAScreenApp lw = new QAScreenApp(body);
    pageState.setPopup(true);
    lw.register(pageState);
    pageState.perform(request, response).toHtml(pageContext);

    if ("VERSION".equals(pageState.command)) {
      %><%=ApplicationState.getApplicationVersion()%><%
      return;
    } else if (pageState.getLoggedOperator()==null || !pageState.getLoggedOperator().hasPermissionAsAdmin()) {
        throw new SecurityException("Access denied");
    }

  } else {

    SessionState sessionState = pageState.getSessionState();
    Skin skin = sessionState.getSkin();
    String color_warning = pageState.sessionState.getSkin().COLOR_WARNING;

    Container sc = new Container(pageState);
    sc.title = "Teamwork system check";
    sc.start(pageContext);

    Container app = new Container(pageState);
    app.title = "Application";
    app.level=1;
    app.start(pageContext);

    %><table><tr><td><%=I18n.get("APP_VERSION")%>:&nbsp;<%=ApplicationState.getApplicationVersion()+"."+ApplicationState.getBuild()%></td><%

    //get current version from online
    PageSeed cu = new PageSeed("http://www.twproject.com/checkUpdates.page");
    cu.addClientEntry("TW_CURRENT_VERSION",ApplicationState.getApplicationVersion()+"."+ApplicationState.getBuild());
    cu.command = "CHECK_UPDATES";
    ButtonLink scbl = new ButtonLink("Check for updates on the web",cu);
    %><td><%scbl.toHtml(pageContext);%></td></tr><%

    //warn if scheduler is stopped
    if (!Scheduler.isRunning()) {
      PageSeed sched = pageState.pageFromCommonsRoot("/scheduler/scheduleManager.jsp");
      ButtonLink schBL = new ButtonLink(I18n.get("MONITOR"),sched);
      schBL.target=ButtonLink.TARGET_BLANK;
      %><tr><td><font color="<%=color_warning%>">Scheduler is not running</font></td><td><%schBL.toHtml(pageContext);%></td></tr><%
    }

    %><%--
    //administrators without passwords
    String hql = "select operator from "+ BugsVoiceOperator.class.getName()+" as operator where operator.administrator=:truth and operator.enabled=:truth";
    OqlQuery oql = new OqlQuery(hql);
    oql.getQuery().setBoolean("truth",Boolean.TRUE);
    List<BugsVoiceOperator> all = oql.list();
    PageSeed edPers = pageState.pageFromRoot("resource/resourceEditor.jsp");
    edPers.command = Commands.EDIT;
    TabSet.pointToTab("resourceTabSet","securityTab",edPers);
    ButtonLink edResBL = new ButtonLink("",edPers);
    edResBL.target = ButtonLink.TARGET_BLANK;

    for (BugsVoiceOperator operator : all) {
      String prefixedPassword = operator.getLoginName() + "";
      prefixedPassword = StringUtilities.md5Encode(prefixedPassword);
      if (operator.getPassword().equals(prefixedPassword) && operator.isEnabled()) {
        try {
        edResBL.pageSeed.mainObjectId = operator.getPerson().getId();
        edResBL.label = operator.getPerson().getDisplayName();
        %><tr><td><%edResBL.toHtmlInTextOnlyModality(pageContext);%>:</td><td><font color="<%=color_warning%>"> has empty password!</font></td></tr><%
      } catch (Exception e) {
        Tracer.platformLogger.error("operator "+operator.getId() +" has no corresponding resource");
      }
    }

  }--%><%

%></table><%

  // application root
  %><hr><b><%=I18n.get("APPLICATION_ROOT")%></b>:&nbsp;<%=ApplicationState.webAppFileSystemRootPath%><%


    app.end(pageContext);

    Container lic = new Container(pageState);
    lic.title = "License data";
    lic.level=1;
    lic.start(pageContext);

    String error = (String) ApplicationState.applicationParameters.get("FILTER_ERROR");
    if (error!=null && error.trim().length()>0) {
      %><font color="<%=color_warning%>"><%=error%></font>&nbsp;&nbsp;&nbsp;<a href="http://www.twproject.com" target="_blank">get a license</a><br><%
    }

    %><%--
    Properties //p = new Properties();
    String appGloblaPath = HttpUtilities.getFileSystemRootPathForRequest(request) + "WEB-INF" + File.separator + "lic.properties";
    p.load(new FileInputStream(appGloblaPath));

    int clients = Integer.parseInt(p.getProperty("licenses"));
    String expires = p.getProperty("expires");

    %>Number of users: <%=clients%>.&nbsp;<%
    //warn if close to limit
    String hql = "select count(op) from "+ Operator.class.getName()+" as op where op.enabled = :truth";
    OqlQuery oql = new OqlQuery(hql);
    oql.getQuery().setBoolean("truth",Boolean.TRUE);
    long totOp = (Long)oql.uniqueResult();
    if (Math.abs(clients-totOp)<3) {
      %><font color="<%=color_warning%>">You have only <%=clients-totOp%> users available:<br><br></font><%

      PageSeed licLink = new PageSeed("http://www.twproject.com/licensing.page");
      ButtonLink licLinkB = new ButtonLink("Extend your license",licLink);
      licLinkB.target=ButtonLink.TARGET_BLANK;
      %><%licLinkB.toHtml(pageContext);%><%

    } else {
      %>There are <%=totOp%> users created.<%
    }

    %><br>License expires: <%=expires%>.&nbsp;<%
    //warn if close to limit
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.UK);
    Date date;
    try {
      date = sdf.parse(expires);
    } catch (ParseException e) {
      throw new PlatformRuntimeException(e);
    }
    long daysRemaining = (date.getTime() - System.currentTimeMillis()) / CompanyCalendar.MILLIS_IN_DAY;
    if (daysRemaining < 20) {
      %><font color="<%=color_warning%>">You have only <%=daysRemaining%> usage days remaining:<br><br></font><%

      PageSeed licLink = new PageSeed("http://www.twproject.com/licensing.page");
      ButtonLink licLinkB = new ButtonLink("Extend your license",licLink);
      licLinkB.target=ButtonLink.TARGET_BLANK;
      %><%licLinkB.toHtml(pageContext);%><%
    } else {
      %><br><%
    }

    //get current version from online
    PageSeed reg = pageState.pageInThisFolder("register.jsp",request);
    ButtonLink regB = new ButtonLink("Insert new Teamwork license",reg);
    %><br><%regB.toHtml(pageContext);%><br><%

    lic.end(pageContext);

    --%><%

    Container sic = new Container(pageState);
    sic.title = "Server information";
    sic.level=1;
    sic.start(pageContext);

    ServerInfo si = new ServerInfo();
    %><b>Databases</b><br><%

   for (PersistenceConfiguration pc : PersistenceConfiguration.persistenceConfigurations.values()){

    %>Connected to: <%=ApplicationState.platformConfiguration!=null ? pc.driver_url : "no connection"%><br>
    with user: <%=ApplicationState.platformConfiguration!=null ? pc.db_user_name : "no connection"%><br> <%
    if (pc.dialect.getName().toUpperCase().indexOf("HSQL")>-1) {
       %><font color="<%=color_warning%>">Using HSQLDB as database: remember to frequently backup the folder <br><big><b><%=ApplicationState.platformConfiguration!=null ? pc.driver_url.substring(12) : "no connection"%></b></big></font><br><%
    }
  }
%>

<br>
    <b>System properties</b><br>
    <%=si.systemProps(false)%><br>
    <b>System state</b><br>
    <%=si.systemState()%><br><%

  //warn about low allowed memory
  if ( (si.getMaxMemory()/1024)<70E3 ) {
    %><font color="<%=color_warning%>">Max memory setting is too low: allow at least 128MB.<br><%
  }

  //warn about low memory in use
  if ( (si.getFreeMemory()/1024)<10E3 ) {
    %><font color="<%=color_warning%>">Free memory is too low: increase max memory setting.<br><%
  }

  Container allProps = new Container(pageState);
  allProps.title = "All properties";
  allProps.level=2;
  allProps.collapsable=true;
  allProps.status=Container.COLLAPSED;
  allProps.start(pageContext);
  //... Add property list data to text area.
  Properties pr = System.getProperties();
  TreeSet propKeys = new TreeSet(pr.keySet());  // TreeSet sorts keys
  for (Iterator it = propKeys.iterator(); it.hasNext(); ) {
    String key = (String)it.next();
    %><%=key%>=<%=pr.get(key)%><br><%
  }
  allProps.end(pageContext);

  sic.end(pageContext);

  String root = request.getSession().getServletContext().getRealPath("/").replaceAll("\\\\", "/");
  String logPath = root + "WEB-INF/log/";

  if (PlatformConfiguration.logOnFile) {
    Container logFiles = new Container(pageState);
    logFiles.title = I18n.get("LOG_FILES");
    logFiles.level=1;
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
  }


  sc.end(pageContext);

  }
%>