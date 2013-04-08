package org.jblooming.waf;

import org.jblooming.waf.settings.PlatformConfiguration;
import org.jblooming.waf.settings.ApplicationSupport;
import org.jblooming.waf.settings.PersistenceConfiguration;
import org.jblooming.waf.settings.I18nEntryPersistent;
import org.jblooming.ontology.PersistentText;
import org.jblooming.persistence.hibernate.PlatformAnnotationConfiguration;
import org.jblooming.persistence.hibernate.HibernateFactory;
import org.jblooming.security.PlatformPermissions;
import org.jblooming.logging.AuditLogRecord;
import org.hibernate.MappingException;

import javax.servlet.jsp.PageContext;
import java.io.File;
import java.net.URL;

/**
 * (c) Open Lab - www.open-lab.com
 * Date: Jun 27, 2007
 * Time: 3:42:06 PM
 */
public class PlatformSettings extends ApplicationSupport {

  public PlatformSettings() {
    super(new PlatformPermissions());
  }

  public boolean isLoginCookieEnabled() {
    return false;
  }

  public String getName() {
    return PLATFORM_APP_NAME;
  }

  public String getRootFolder() {
    return "commons";
  }

  public void configurePersistence(PlatformConfiguration pc) {

    PlatformAnnotationConfiguration hibConfiguration = PersistenceConfiguration.getFirstPersistenceConfiguration().getHibernateConfiguration();

    URL common = HibernateFactory.class.getClassLoader().getResource("common.hbm.xml");
    try {
      hibConfiguration.addURL(common);
    } catch (MappingException e) {
      throw new RuntimeException(e);
    }
    hibConfiguration.addAnnotatedClass(AuditLogRecord.class);
    hibConfiguration.addAnnotatedClass(PersistentText.class);
    hibConfiguration.addAnnotatedClass(I18nEntryPersistent.class);
  }

  public void configureFreeAccess(PlatformConfiguration pc) {

    FrontControllerFilter.ignoredPatterns.add("platformCss.jsp");

    // please do not put index.jsp as free file here: eventually put it in specific application settings
    AccessControlFilter.freePatterns.add("login.jsp");
    AccessControlFilter.freePatterns.add("openlab.jsp");
    AccessControlFilter.freePatterns.add("error.jsp");
    AccessControlFilter.freeFolders.add("commons" + File.separator + "settings");
    AccessControlFilter.freeFolders.add("commons" + File.separator + "skin");
    AccessControlFilter.freeFolders.add("commons" + File.separator + "js");
    AccessControlFilter.freeFolders.add("commons" + File.separator + "layout");
    AccessControlFilter.freeFiles.add("commons" + File.separator + "security" + File.separator + "login.jsp");
    AccessControlFilter.freeFiles.add("command.jsp");
  }

  public void configureNotMonitored(PlatformConfiguration pc) {
  }

  public void configureViews(PlatformConfiguration pc) {
  }

  public void configureNeedingPersistence(PlatformConfiguration pc) {

  }

  public void configureNeedingPageContext(PageContext pageContext) {

  }
}