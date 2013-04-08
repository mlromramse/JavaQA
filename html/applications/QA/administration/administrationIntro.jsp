<%@ page import="org.jblooming.waf.ScreenBasic,
                 org.jblooming.waf.constants.Commands,
                 org.jblooming.waf.html.button.ButtonJS,
                 org.jblooming.waf.html.button.ButtonLink,
                 org.jblooming.waf.html.container.*,
                 org.jblooming.waf.html.display.PercentileDisplay, org.jblooming.waf.html.layout.Skin, org.jblooming.waf.settings.PlatformConfiguration, org.jblooming.waf.view.PageSeed, org.jblooming.waf.view.PageState" %>
<%
  PageState pageState = PageState.getCurrentPageState();

  pageState.getLoggedOperator().testIsAdministrator();

  if (!pageState.screenRunning) {
    ScreenBasic.preparePage(pageContext);
    pageState.perform(request, response).toHtml(pageContext);
  } else {


    pageState.saveInHistoryIfNeeded();

    Skin skin = pageState.getSkin();

    %><script>$("#ADMINISTRATION_ROOT_MENU").find("a").addClass('selected');</script><%
  RibbonBar rb = new RibbonBar();
  rb.toHtml(pageContext);


  PageSeed configPage = pageState.pageFromRoot("administration/globalSettings.jsp");
  String firstColumnWidth = "15%";

  double totalScore = 0;
  double score = 0;

  Container main = Container.getPopupInstance("What is configured?", pageState);
  main.closeable = true;
  main.start(pageContext);


  main.end(pageContext);

  Box box = new Box();
  box.start(pageContext);

  ButtonJS opener = main.getOpenerButton(true);
  opener.label = "why?";

  PercentileDisplay pc = new PercentileDisplay((score / totalScore) * 100);
  pc.height = 30;
  pc.width = 150;
  pc.percentileColor = skin.COLOR_BACKGROUND_TITLE02;
  pc.backgroundColor = skin.COLOR_BACKGROUND_TITLE;

%>
<style type="text/css">
  li{
    list-style-type:circle;
  }
</style><br>

<jsp:include page="../parts/appMenu.jsp"/>

<big><font size="6">
<li><%
  new ButtonLink("operators", pageState.pageFromRoot("administration/operatorList.jsp")).toHtmlInTextOnlyModality(pageContext);
%></li>
<li><%
new ButtonLink("labels", pageState.pageFromCommonsRoot("/administration/i18nManager.jsp")).toHtmlInTextOnlyModality(pageContext);
%></li>
     </font></big>
<hr><%


          %>

            &nbsp;&nbsp;<a href="checkExternalImport.jsp" target="_blank">check external imports</a>
            &nbsp;&nbsp;<a href="generateCodes.jsp" target="_blank">generate codes</a>
            &nbsp;&nbsp;<a href="/commons/oauth/applications.jsp" target="_blank">OAuth admin</a>
            &nbsp;&nbsp;<a href="purchasesManager.jsp" target="_blank">License Manager</a>
            &nbsp;&nbsp;<a href="sendBulkMessage.jsp" target="_blank">Send bulk message</a>

<hr>

<table width="100%" border="0" cellpadding="2" cellspacing="2">
<%    //----------------------------------------------------------------------- 1 ----------------------------------------------------------------%>
<tr>
   <td width="1%" style="font-size:20pt;background-color:<%=skin.COLOR_BACKGROUND_TITLE02%>" align="center">1.</td>
   <td width="<%=firstColumnWidth%>" valign="top"><strong>Basic web application configuration: network and e-mail</strong><br><br>
     Configure how Teamwork responds on the network and enabling e-mail reminders and actions.
   </td>

  <td valign="top">

  <%
    //----------------------------------------------------------------------- NETWORK AND SECURITY ----------------------------------------------------------------

    ContainerPlus network = new ContainerPlus(pageState);
    network.title = "Network and security";
    network.start(pageContext);

    //
  %>
  <table>
    <tr>     
      <td valign="top" nowrap><li><%
          TabSet.pointToTab("genTabSet", "bcts", configPage);
          new ButtonLink("set domain name - HTTP server", configPage).toHtmlInTextOnlyModality(pageContext);
        %></li><br>

        <li><% new ButtonLink("set repository path and exposed files", configPage).toHtmlInTextOnlyModality(pageContext); %></li><br>

        <li><%
          TabSet.pointToTab("genTabSet", "secTS", configPage);
          new ButtonLink("configure security: password expiry etc.", configPage).toHtmlInTextOnlyModality(pageContext);
      %></li>
       <li><%
          TabSet.pointToTab("genTabSet", "secTS", configPage);
          new ButtonLink("LDAP integration", configPage).toHtmlInTextOnlyModality(pageContext);
         %> - <%
         new ButtonLink("import users", pageState.pageFromRoot("administration/importLdapUsers.jsp")).toHtmlInTextOnlyModality(pageContext);
      %></li>
      </td>

    </tr>
  </table>
  <%network.end(pageContext);
  %></td>
  
   <td valign="top"><%
     //----------------------------------------------------------------------- "EMAIL CONFIGURATION" ----------------------------------------------------------------
     ContainerPlus email = new ContainerPlus(pageState);
     email.title = "Email configuration";
     email.start(pageContext);

      TabSet.pointToTab("genTabSet", "mailTS", configPage);
   %>
   <table>
     <tr>
       <td valign="top" nowrap>
         <li>reminders may be sent from Teamwork - <%
         new ButtonLink("configure SMTP", configPage).toHtmlInTextOnlyModality(pageContext);
       %></li><br>
         <li>actions may be sent to Teamwork - <%

           new ButtonLink("configure POP3", configPage).toHtmlInTextOnlyModality(pageContext);
         %><br>
         <%
           PageSeed log = pageState.pageFromRoot("administration/showLog.jsp");
           log.addClientEntry("LOG","email.log");
           ButtonLink.getPopupInstance("see e-mail downloaded logs",600,800, log).toHtmlInTextOnlyModality(pageContext);
       %></li><br>
         
         <li>verify that downloader is running: <%
       new ButtonLink("e-mail downloader ",pageState.pageFromCommonsRoot("scheduler/scheduleManager.jsp")).toHtmlInTextOnlyModality(pageContext);
       %></li>
       </td>

     </tr>
   </table>
   <%email.end(pageContext);     
   %></td>

   <td valign="top"><%
  //----------------------------------------------------------------------- Fulltext index ----------------------------------------------------------------
            ContainerPlus indexing = new ContainerPlus(pageState);
            indexing.title = "Full-text indexing and ranking";
            indexing.start(pageContext);
          %>
            <table>
              <tr>
                
                <td valign="top" >
                  <li>all information is indexed and ranked for fast search and smart linking - <%
                    TabSet.pointToTab("genTabSet", "indexingTS", configPage);
                    new ButtonLink("index configuration", configPage).toHtmlInTextOnlyModality(pageContext);
                    %> - <%
                    PageSeed indexi =   pageState.pageFromRoot("administration/indexingTeamwork.jsp");
                    PageSeed hits = pageState.pageFromRoot("administration/hitList.jsp");
                    new ButtonLink("index management", indexi).toHtmlInTextOnlyModality(pageContext);
                  %></li><br>

                  <li>hits are used to track activity: <%
                  new ButtonLink("hits admin", hits).toHtmlInTextOnlyModality(pageContext);
                %></li>
                </td>

              </tr>
            </table>
            <%indexing.end(pageContext);
        %></td>
  

</tr>
<%//----------------------------------------------------------------------- 2 ----------------------------------------------------------------%>

<tr>
   <td width="1%" style="font-size:20pt;background-color:<%=skin.COLOR_BACKGROUND_TITLE02%>" align="center">2.</td>
   <td width="<%=firstColumnWidth%>" valign="top"><strong>BugsVoice error collector</strong><br><br>
     Application templates and defaults.
   </td>

  <td valign="top">
  <%
              //----------------------------------------------------------------------- "BUGSVOICE" ----------------------------------------------------------------
              ContainerPlus dash = new ContainerPlus(pageState);
              dash.title = "BugsVoice";
              dash.start(pageContext);
            %>
            <table>
              <tr>
                <td style="border-right:1px solid #e0e0e0;" valign="top" width="120"><big>BugsVoice.</big></td>
                <td valign="top" nowrap>
                  <li><%
                    new ButtonLink("rules", pageState.pageFromRoot("administration/rule/ruleList.jsp")).toHtmlInTextOnlyModality(pageContext);
                  %></li><li><%
                  new ButtonLink("templates", pageState.pageFromRoot("administration/template/templateList.jsp")).toHtmlInTextOnlyModality(pageContext);
                  %></li><li><%
                  new ButtonLink("operators", pageState.pageFromRoot("administration/operatorList.jsp")).toHtmlInTextOnlyModality(pageContext);
                %></li><br><li><%
                  new ButtonLink("banned IP", pageState.pageFromRoot("administration/bannedIP.jsp")).toHtmlInTextOnlyModality(pageContext);
                %></li><li><%
                new ButtonLink("statistics", pageState.pageFromRoot("administration/statistics.jsp")).toHtmlInTextOnlyModality(pageContext);
                  %></li>
                </td>

              </tr>
            </table>
            <%dash.end(pageContext);%>
           </td>    

  <td valign="top" colspan="2">
  <%
    //----------------------------------------------------------------------- "APPLICATION CONFIGURATION" ----------------------------------------------------------------
    ContainerPlus appli = new ContainerPlus(pageState);
    appli.title = "User interface and data";
    appli.start(pageContext);
  %>
  <table>
    <tr>
      <td valign="top" nowrap>

        <li>set and customize language, working days, currency and date formats - <%
        TabSet.pointToTab("genTabSet", "i18nTS", configPage);
        new ButtonLink("internationalization", configPage).toHtmlInTextOnlyModality(pageContext);
        %></li>

        <li>user default skins and day intervals - <%
        TabSet.pointToTab("genTabSet", "userTS", configPage);
        new ButtonLink("user defaults", configPage).toHtmlInTextOnlyModality(pageContext);
        %></li>

        <li>configure global holidays - <%
        new ButtonLink("holidays", pageState.pageFromCommonsRoot("/administration/holidays.jsp")).toHtmlInTextOnlyModality(pageContext);
      %></li>

        <li>pick names for default roles - <%
        TabSet.pointToTab("genTabSet", "pmTS", configPage);
        new ButtonLink("default project role names", configPage).toHtmlInTextOnlyModality(pageContext);
      %></li>

        <li>change interface' labels - <%
        new ButtonLink("labels", pageState.pageFromCommonsRoot("/administration/i18nManager.jsp")).toHtmlInTextOnlyModality(pageContext);
      %></li>




      </td>

    </tr>
  </table>
  <%appli.end(pageContext);%>
  </td>



</tr>
<%//----------------------------------------------------------------------- 3 ----------------------------------------------------------------%>
<tr>
   <td width="1%" style="font-size:20pt;background-color:<%=skin.COLOR_BACKGROUND_TITLE02%>" align="center">3.</td>
   <td width="<%=firstColumnWidth%>" valign="top"><strong>Advanced configuration</strong><br><br>
     Change and extend default behaviour. See the <a href="http://www.twproject.com/documentation.page" target="_blank">user guide</a> for details.</td>

  <td valign="top">
    <%
              //----------------------------------------------------------------------- "SECURITY" ----------------------------------------------------------------
              ContainerPlus secur = new ContainerPlus(pageState);
              secur.title = "Teamwork security";
              secur.start(pageContext);
            %>
            <table>
              <tr>
                <td style="border-right:1px solid #e0e0e0;" valign="top">Create additional areas and roles.</td>
                <td valign="top" nowrap>
                  <li><%
                    new ButtonLink("area creation wizard", pageState.pageFromRoot("security/security.jsp")).toHtmlInTextOnlyModality(pageContext);
                  %></li><li><%
                  new ButtonLink("roles management", pageState.pageFromRoot("security/roleList.jsp")).toHtmlInTextOnlyModality(pageContext);
                %></li><li><%
                  new ButtonLink("area management", pageState.pageFromRoot("security/area.jsp")).toHtmlInTextOnlyModality(pageContext);
                %></li><li><%
                    new ButtonLink("check permission on tasks", pageState.pageFromRoot("administration/securityTest.jsp")).toHtmlInTextOnlyModality(pageContext);
                  %></li>
                </td>

              </tr>
            </table>
            <%secur.end(pageContext);%>
  </td>

  <td valign="top">
    <%
              //----------------------------------------------------------------------- "Business processes" ----------------------------------------------------------------
              ContainerPlus flow = new ContainerPlus(pageState);
              flow.title = "Business processes - projects as flows";
              flow.start(pageContext);

              /*
               Menu tools = root.addContentLine("back office").setPermissionRequired(FlowPermissions.canManageFlows).setHideWhenDisabled(true);
  tools.addContentLine("amministrazione lookup", pageState.pageFromRoot("/smsBackoffice/lookup.jsp"));
  tools.addContentLine("visualizza prodotti-istanze", pageState.pageFromRoot("/smsBackoffice/productFluxes.jsp"));
  PageSeed fa = pageState.pageFromRoot("/smsBackoffice/fieldsAvailable.jsp");
  fa.setCommand(Commands.FIND);
  tools.addContentLine("definizione proprietà", fa);
  tools.addContentLine(I18n.get("OPERATOR_MANAGEMENT"), pageState.pageFromRoot("/operator/operatorList.jsp"));
  tools.addContentLine("configurazione messaggi (e-mail)", pageState.pageFromRoot("/smsBackoffice/fixEmailEditor.jsp"));

    processPhase01Approved
                    first phase approved
  java.lang.Boolean
               */

            %>
            <table>
              <tr>
                <td style="border-right:1px solid #e0e0e0;" valign="top">In flow's backoffice you find the default flows that define projects as processes.</td>
                <td valign="top" nowrap>
                  <br><li><%
                    new ButtonLink("flow administration", pageState.pageFromCommonsRoot("flowork/backoffice/deployList.jsp")).toHtmlInTextOnlyModality(pageContext);
                  %></li>

                  <%--<li><%
                  PageSeed fa = pageState.pageFromRoot("processes/fieldsAvailable.jsp");
                  fa.setCommand(Commands.FIND);
                  new ButtonLink("fields definition", fa).toHtmlInTextOnlyModality(pageContext);
                %></li>--%>
                </td>

              </tr>
            </table>
            <%flow.end(pageContext);%>
   </td>

    <td valign="top">
    <%
              //----------------------------------------------------------------------- "custom forms and plugin" ----------------------------------------------------------------
              ContainerPlus cust = new ContainerPlus(pageState);
              cust.title = "Custom forms and pages";
              cust.start(pageContext);

  %> <table>
              <tr>

                <td valign="top" nowrap>

                  <li>create your custom <%
                  new ButtonLink("forms and plugin", pageState.pageFromCommonsRoot("administration/pluginAdmin.jsp")).toHtmlInTextOnlyModality(pageContext);
                  %> - <%                    
                    PageSeed monitorPS = pageState.pageFromCommonsRoot("/administration/monitor.jsp");
                    monitorPS.setPopup(true);
                    ButtonLink.getPopupInstance("debug",600,800,monitorPS).toHtmlInTextOnlyModality(pageContext);
                  %></li>

                  <br><li>define additional <%
                  PageSeed custlabel = pageState.pageFromCommonsRoot("/administration/i18nManager.jsp");
                  custlabel.command=Commands.FIND;
                  custlabel.addClientEntry("SEARCH_TEXT","TASK_CUSTOM_FIELD_");
                  new ButtonLink("fields on task", custlabel).toHtmlInTextOnlyModality(pageContext);

                %></li>
              </tr>
            </table>
            <%cust.end(pageContext);%>


</tr>
<%//----------------------------------------------------------------------- 4 ----------------------------------------------------------------%>
<tr>
   <td width="1%" style="font-size:30pt;background-color:<%=skin.COLOR_BACKGROUND_TITLE02%>"><b>&#8734;</b></td>
   <td width="<%=firstColumnWidth%>" valign="top"><strong>Monitoring</strong><br><br>
     Teamwork offers several web-based monitoring tools.
   </td>

  <td valign="top">
    <%
              //----------------------------------------------------------------------- "SCHEDULED PROCESSES" ----------------------------------------------------------------
              ContainerPlus scheduler = new ContainerPlus(pageState);
              scheduler.title = "System check";
              scheduler.start(pageContext);
            %>
            <table>
              <tr>

                <td valign="top" nowrap>

                  <li><%
                   ButtonLink.getPopupInstance("system check",600,800, pageState.pageFromRoot("/administration/systemCheck.jsp")).toHtmlInTextOnlyModality(pageContext);
                  %></li><br>



                  <li><%
                 ButtonLink.getPopupInstance("tree structure check",600,800, pageState.pageFromCommonsRoot("/administration/treeCheck.jsp")).toHtmlInTextOnlyModality(pageContext);

                %></li>

                </td>

              </tr>
            </table>
            <%scheduler.end(pageContext);%></td>



   <td valign="top">
            <%
              //----------------------------------------------------------------------- MONITORING ----------------------------------------------------------------

              ContainerPlus debug = new ContainerPlus(pageState);
              debug.title = "Monitoring";
              debug.start(pageContext);
            %>
            <table>
              <tr>
                <td valign="top" nowrap>

                   <li><%
                    new ButtonLink("scheduler monitor", pageState.pageFromCommonsRoot("scheduler/scheduleManager.jsp")).toHtmlInTextOnlyModality(pageContext);
                   %> - <%
                  PageSeed ps = pageState.pageFromCommonsRoot("scheduler/jobList.jsp");
                  ps.command = Commands.FIND;
                  new ButtonLink("job list", ps).toHtmlInTextOnlyModality(pageContext);
                %></li>


                    <li><%
                  ButtonLink.getPopupInstance("log levels", 600,800, pageState.pageFromCommonsRoot("administration/log.jsp")).toHtmlInTextOnlyModality(pageContext);
                  %> - <%
                     if (PlatformConfiguration.logOnFile) {

                      ButtonLink.getPopupInstance("show logs",600,800, pageState.pageFromRoot("administration/showLog.jsp")).toHtmlInTextOnlyModality(pageContext);
                      %> - <%
                        PageSeed pageSeed = pageState.pageFromRoot("administration/showLog.jsp");
                        pageSeed.command = "zip";
                        ButtonLink.getPopupInstance("zip main log",600,800,pageSeed).toHtmlInTextOnlyModality(pageContext);
                        %></li><%
                    }
                %>

                <li>queues: <%

                    PageSeed eventManager = new PageSeed(request.getContextPath() + "/applications/QA/administration/messaging/eventManager.jsp");
                    PageSeed listenerManager = new PageSeed(request.getContextPath() + "/applications/QA/administration/messaging/listenerManager.jsp");
                    PageSeed messageManager = new PageSeed(request.getContextPath() + "/applications/QA/administration/messaging/messageManager.jsp");
                    new ButtonLink("events", eventManager).toHtmlInTextOnlyModality(pageContext);
                  %> - <%
                  new ButtonLink("subscriptions", listenerManager).toHtmlInTextOnlyModality(pageContext);
                  %> - <%
                    new ButtonLink("messages", messageManager).toHtmlInTextOnlyModality(pageContext);
                %></li>

                </td>

              </tr>
            </table>
            <%debug.end(pageContext);%>
          </td>

           <td valign="top">
  <%//----------------------------------------------------------------------- Messaging system ----------------------------------------------------------------
              ContainerPlus messaging = new ContainerPlus(pageState);
              messaging.title = "Licensing";
              messaging.start(pageContext);
              messaging.end(pageContext);
              %>
            </td>
         </tr>

</table><%
   box.end(pageContext);
  }
%>
