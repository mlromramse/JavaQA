package org.jblooming.waf.settings;

import org.jblooming.security.Permission;
import org.jblooming.security.Permissions;
import org.jblooming.tracer.Tracer;
import org.jblooming.utilities.HashTable;
import org.jblooming.utilities.StringUtilities;
import org.jblooming.waf.ScreenBasic;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

public abstract class ApplicationSupport implements Application {

  String version = null;


    protected ApplicationSupport(Permissions permissionsImpl) {
        this(PlatformConfiguration.defaultOperatorSubclass, permissionsImpl);
    }

    protected ApplicationSupport(Class defaultOperatoSubClass, Permissions permissionsImpl) {
        PlatformConfiguration.defaultOperatorSubclass = defaultOperatoSubClass;
        if (permissionsImpl != null)
            Permission.addPermissions(permissionsImpl, permissions);
    }

    protected ApplicationSupport(Class defaultOperatoSubClass, Permissions[] permissionsImpl) {
        PlatformConfiguration.defaultOperatorSubclass = defaultOperatoSubClass;
        if (permissionsImpl != null) {
            for (int i = 0; i < permissionsImpl.length; i++) {
                Permissions permissions1 = permissionsImpl[i];
                Permission.addPermissions(permissions1, permissions);
            }
        }
    }

    protected Map<String, Permission> permissions = new HashTable();

    public Map<String, Permission> getPermissions() {
        return permissions;
    }


    public Class getDefaultScreenClass() {
        return getDefaultScreenInstance().getClass();
    }

    public ScreenBasic getDefaultScreenInstance() {
        return null;
    }

    public void configureNeedingPageContext(PageContext pageContext) {

    }

    public void configBeforePerform(HttpServletRequest request) {
    }

    public void sessionStateValueBound() {

    }

    public void sessionStateValueUnbound() {

    }

  public void applicationDestroy() {
    // in specific applications styop scheduler etc.
  }

  public String getVersion() {

    if (version == null) {
      Properties props = new Properties();
      try {
        String rootFolder = StringUtilities.replaceAllNoRegex(getRootFolder(), "/", File.separator);
        InputStream is = new FileInputStream(
                ApplicationState.webAppFileSystemRootPath +
                        File.separator + rootFolder + File.separator + "settings" + File.separator + getName() + ".number");

        props.load(is);
        version = props.getProperty("version");
      } catch (Throwable t) {
        version = "ERROR";
        Tracer.platformLogger.error(t);
      }
    }
    return version;
  }

}
