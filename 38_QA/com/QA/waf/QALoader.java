package com.QA.waf;

import com.QA.waf.settings.QA;
import org.jblooming.waf.AccessControlFilter;
import org.jblooming.waf.FrontControllerFilter;
import org.jblooming.waf.configuration.LoaderSupport;
import org.jblooming.waf.settings.ApplicationState;

import javax.servlet.ServletContextEvent;
import java.util.Properties;

public class QALoader extends LoaderSupport {
  public void configApplications() {

    // keeep it as last otherwise the defaultOperatorSubclass is lost!
    QA settings = new QA();
    ApplicationState.platformConfiguration.addApplication(settings);

    AccessControlFilter.LOGIN_PAGE_PATH_FROM_ROOT = "/applications/QA/site/access/login.jsp";
    FrontControllerFilter.ERROR_PAGE_PATH_FROM_ROOT = "/applications/QA/site/error.jsp";
    ApplicationState.platformConfiguration.defaultIndex = "/applications/QA/talk/index.jsp";
    ApplicationState.platformConfiguration.defaultApplication = settings;
  }


  protected void configLog4J(Properties p) {
    super.configLog4J(p);
    //createLogger(PlatformConfiguration.logOnConsole, PlatformConfiguration.logOnFile, PlatformConfiguration.logPattern, PlatformConfiguration.logFilesRoot, APITracer.oauthLogger, "oauthLoggerConsoleAppender", "oauth.log", "oauthLoggerFileAppender");
  }

  public void contextDestroyed(ServletContextEvent event) {

    // call super
    super.contextDestroyed(event);

  }


}
