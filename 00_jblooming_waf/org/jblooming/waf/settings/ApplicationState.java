package org.jblooming.waf.settings;

import org.jblooming.security.Permission;
import org.jblooming.tracer.Tracer;
import org.jblooming.utilities.*;
import org.jblooming.utilities.file.FileUtilities;
import org.jblooming.waf.ActionController;
import org.jblooming.waf.DefaultCommandController;
import org.jblooming.waf.EntityViewerBricks;
import org.jblooming.waf.configuration.LoaderSupport;
import org.jblooming.waf.constants.Fields;
import org.jblooming.waf.constants.OperatorConstants;
import org.jblooming.waf.html.core.UrlComposer;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.util.*;

public class ApplicationState {

  public static I18n i18n = new I18n();

  private static Map configuredUrls = new FinalKeyMap();

  public static PlatformConfiguration platformConfiguration = new PlatformConfiguration();

  public static boolean loaded;
  private static String version;
  private static String build;


  public static Map<String, String> applicationSettings = new HashTable();
  public static Map<String, Object> applicationParameters = new HashTable<String, Object>();

  public static Map<String, Permission> permissions = new HashTable();

  public static Locale SYSTEM_LOCALE = Locale.US;
  public static final TimeZone SYSTEM_TIME_ZONE = DateFormat.getTimeInstance().getTimeZone();
  private static String[] localizedDateFormats=null;

  
  /**
   * this is the controller used by the command.jsp to communicate client->server.
   * Can be overriden by other application's specific controller
   */
  public static Class<? extends ActionController> commandController = DefaultCommandController.class;
  /**
   * http(s)/server name:server port/context path
   */
  public static String serverURL;
  /**
   * does not end with file separator
   */
  public static String webAppFileSystemRootPath;
  public static String contextPath = "";

  public static Map<String, EntityViewerBricks> entityViewers = new HashTable();

  private ApplicationState() {
  }

  public static String getApplicationSetting(String key) {
    if (ApplicationState.getApplicationSettings() == null)
      return null;

    Object o = ApplicationState.getApplicationSettings().get(key);
    if (o == null)
      return null;

    return (String) o;
  }

  public static String getApplicationSetting(String key, String defaultValue) {
    String value = getApplicationSetting(key);
    if (!JSP.ex(value))
      value = defaultValue;
    return value;
  }

  public static Map getApplicationSettings() {
    return applicationSettings;
  }

  public static Map getConfiguredUrls() {
    return configuredUrls;
  }

  /**
   * @deprecated use the parametric version
   */
  public static String getVersion() throws IOException {
    return getVersion("", false);
  }

  public static String getVersion(String app) throws IOException {
    return getVersion(app, false);
  }

  /**
   * @param app    it's the "appName.number" file in which the version of the particular application is set
   * @param reload
   * @return jblooming.number concatenated to opnlb.number concatenated to app concatenated to build.number
   * @throws IOException
   */
  public static String getVersion(String app, boolean reload) throws IOException {


    if (ApplicationState.version == null || reload) {

      String version = "";

      boolean found = false;

      Application application = platformConfiguration.defaultApplication;
      if (JSP.ex(app)) {

        //for backward compatibility
        if (app.indexOf(".number") > -1)
          app = app.substring(0, app.indexOf(".number"));

        application = platformConfiguration.applications.get(app);
      }

      if (application != null) {
        version += application.getVersion() ;

        found = true;
      } else
        version += "[" + app + " not found].";

/*      version += " (jblooming framework version: ";
      version += jbloomingVersion();
      version += ")";*/
      if (found)
        ApplicationState.version = version;
    }
    return version;
  }


  public static String getApplicationVersion() {
    return platformConfiguration.defaultApplication.getVersion();
  }


  public static String getApplicationSchemaLog(Application application) throws IOException {
    String rootFolder = StringUtilities.replaceAllNoRegex(application.getRootFolder(), "/", File.separator);
    String pathname = ApplicationState.webAppFileSystemRootPath +
            File.separator + rootFolder + File.separator + "settings" + File.separator + application.getName() + "SchemaLog.number";
    File file = new File(pathname);
    String result = "";
    if (file.exists()) {
      result = FileUtilities.readTextFile(pathname);
    }
    return result;
  }

  public static void setApplicationSchemaLog(Application application, String content) throws  IOException {
    String rootFolder = StringUtilities.replaceAllNoRegex(application.getRootFolder(), "/", File.separator);
    String pathname = ApplicationState.webAppFileSystemRootPath +
            File.separator + rootFolder + File.separator + "settings" + File.separator + application.getName() + "SchemaLog.number";
    FileUtilities.writeToFile(pathname, content);
  }

  public static void setApplicationBuild(Application application, Properties p) throws IOException {
    String rootFolder = StringUtilities.replaceAllNoRegex(application.getRootFolder(), "/", File.separator);

    FileOutputStream fos = new FileOutputStream(
            ApplicationState.webAppFileSystemRootPath + File.separator + rootFolder + File.separator + "settings" + File.separator + application.getName() + ".number");

    p.store(fos, "");
  }

  public static String getBuild() {
    if (!JSP.ex(build)) {
      Properties props = null;
      try {
        props = getBuildProps();
        build = props.getProperty("build").trim();
      } catch (IOException e) {
        Tracer.platformLogger.error(e);
        build = "ERROR";
      }
    }
    return build;
  }


  public static Properties getBuildProps() throws IOException {

    Properties props = new Properties();
    FileInputStream is =  new FileInputStream(
            ApplicationState.webAppFileSystemRootPath + File.separator + "commons" + File.separator + "settings" + File.separator + "Platform.number");

    if (is != null) {
      props.load(is);
    }

    return props;
  }

  public static String jbloomingVersion() {
    String platform = "";
    Properties props = null;
    try {
      props = getBuildProps();
      platform += props.getProperty("version") + ".";
      platform += props.getProperty("build");
    } catch (Exception e) {
      Tracer.platformLogger.error("File Platform.number not found in settings.", e);
    }

    return platform;
  }


  public static void dumpApplicationSettings() {

    String globalPath = ApplicationState.webAppFileSystemRootPath + File.separator +
            "commons" + File.separator + "settings" + File.separator + PlatformConfiguration.globalSettingsFileName;

    Properties properties = new Properties();
    TreeSet<String> sett = new TreeSet(ApplicationState.applicationSettings.keySet());
    for (String key : sett) {
      properties.put(key, ApplicationState.applicationSettings.get(key));
    }
    FileUtilities.savePropertiesInUTF8(properties, globalPath);
  }

  public static Map<String, Permission> getPermissions() {
    synchronized (permissions) {
      return permissions;
    }
  }

  public static void refreshGlobalSettings(Properties properties, HttpServletRequest request) {
    ApplicationState.getApplicationSettings().putAll(properties);

    UrlComposer.DISABLE_VIEW_ID = Fields.TRUE.equalsIgnoreCase(ApplicationState.getApplicationSetting("DISABLE_VIEW_ID"));

    ApplicationState.serverURL = HttpUtilities.serverURL(request); // this uses PUBLIC_SERVER_NAME if exists
    LoaderSupport.applySystemSettings();
  }

  public static void setLocale() {
    //reset date formats
    localizedDateFormats=null;
    
    ApplicationState.SYSTEM_LOCALE = I18n.getLocale(ApplicationState.getApplicationSetting(OperatorConstants.FLD_SELECT_LANG));
    //very important! so the JDK is aware of the application needs
    Locale.setDefault(ApplicationState.SYSTEM_LOCALE);
  }


  public static String getSystemLocalizedDateFormat(int field){
    if (localizedDateFormats==null){
      localizedDateFormats =DateUtilities.getLocalizedDateFormats(SYSTEM_LOCALE);
    }
    return localizedDateFormats[field];
  }

}