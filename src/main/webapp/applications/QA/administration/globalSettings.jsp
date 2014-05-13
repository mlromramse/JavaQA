<%@ page import="com.QA.waf.QAScreenApp,
                 org.jblooming.PlatformRuntimeException,
                 org.jblooming.oql.OqlQuery,
                 org.jblooming.persistence.exceptions.StoreException,
                 org.jblooming.utilities.HttpUtilities,
                 org.jblooming.utilities.file.FileUtilities,
                 org.jblooming.waf.ScreenBasic,
                 org.jblooming.waf.constants.Commands,
                 org.jblooming.waf.html.button.ButtonSubmit,
                 org.jblooming.waf.html.input.TextArea,
                 org.jblooming.waf.html.state.Form,
                 org.jblooming.waf.settings.ApplicationState, org.jblooming.waf.settings.I18n, org.jblooming.waf.settings.I18nEntryPersistent, org.jblooming.waf.settings.PlatformConfiguration, org.jblooming.waf.view.PageSeed, org.jblooming.waf.view.PageState, java.io.File, java.util.Iterator, java.util.List, java.util.Properties, java.util.TreeSet" %>
<%

  PageState pageState = PageState.getCurrentPageState();
  pageState.getLoggedOperator().testIsAdministrator();

  if (!pageState.screenRunning) {

    QAScreenApp mpScreenApp = (QAScreenApp) ScreenBasic.preparePage(pageContext);
    mpScreenApp.hasRightColumn = false;
    pageState.perform(request, response).toHtml(pageContext);

  } else {

    if (Commands.SAVE.equals(pageState.command)) {

      String value = pageState.getEntry("GLOBALSETTINGS").stringValueNullIfEmpty();

      String fileName = HttpUtilities.getFileSystemRootPathForRequest((HttpServletRequest) pageContext.getRequest()) +
              File.separator + "commons" + File.separator + "settings" + File.separator + PlatformConfiguration.globalSettingsFileName;

      FileUtilities.writeToFile(fileName, value, "UTF-8"); //was in old times value.replace('\\','/'), who knows why ? ppolsinelli@open-lab.com

      String globalPath = HttpUtilities.getFileSystemRootPathForRequest(request) +
              File.separator + "commons" + File.separator + "settings" + File.separator + PlatformConfiguration.globalSettingsFileName;

      File global = new File(globalPath);
      if (!global.exists())
        throw new PlatformRuntimeException("Global Settings File Name points to a non existing file: " + globalPath);

      Properties properties = FileUtilities.getProperties(globalPath);

      // save in i18n peristent



      ApplicationState.refreshGlobalSettings(properties, request);
    }


  %><h2>Edit values</h2><%
  final PageSeed iddo = pageState.thisPage(request);
  Form form = new Form(iddo);
  form.start(pageContext);

  TreeSet sett = new TreeSet(ApplicationState.applicationSettings.keySet());

  String value = "";
  for (Iterator iterator = sett.iterator(); iterator.hasNext(); ) {
    String key = (String) iterator.next();
    value = value + key + "=" + ApplicationState.applicationSettings.get(key) + "\n";
  }
  pageState.addClientEntry("GLOBALSETTINGS", value);

  TextArea ta = new TextArea("GLOBALSETTINGS", "<br>", 60, 20, null);
  ta.toHtml(pageContext);


  ButtonSubmit save = new ButtonSubmit(form);
  save.variationsFromForm.setCommand(Commands.SAVE);
  save.label = I18n.get("SAVE");
  save.toHtml(pageContext);

  form.end(pageContext);


%><h2>Current values</h2>
<small><%



  for (Iterator iterator = sett.iterator(); iterator.hasNext(); ) {
    String key = (String) iterator.next();
%><%=key%>=<%=ApplicationState.applicationSettings.get(key)%><br><%
  }

%></small>
<%

  }
%>