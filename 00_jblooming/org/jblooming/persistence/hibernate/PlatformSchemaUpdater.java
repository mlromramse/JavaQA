package org.jblooming.persistence.hibernate;

import org.hibernate.mapping.Column;
import org.jblooming.PlatformRuntimeException;
import org.jblooming.waf.constants.Fields;
import org.jblooming.waf.settings.ApplicationState;
import org.jblooming.tracer.Tracer;
import org.jblooming.scheduler.Executable;
import org.jblooming.scheduler.JobLogData;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

/**
 * Created by Open Lab
 * info@open-lab.com
 * Date: Sep 7, 2006
 * Time: 3:57:17 PM
 */
public class PlatformSchemaUpdater {

  public static List<Release> releases = new ArrayList();

  public static void updateToLatestVersion() {

    boolean updateSchema = false;
    boolean doSetup = !Fields.TRUE.equals(ApplicationState.applicationSettings.get("SETUP_DB_UPDATE_DONE"));

    for (Release r : releases) {
      r.verifyIfUpdateNeeded();
      updateSchema = r.needsToBeLaunched || updateSchema;
      if (r.needsToBeLaunched || doSetup) {
        r.schemaRefinementBeforeHibernateFactory();
      }
    }

    if (updateSchema || doSetup) {
      PersistenceContext pc=null;
      try {
        pc = new PersistenceContext();
        HibernateUtilities.generateSchema(false, false, false, null, "tw_,testSuite_", true, ApplicationState.webAppFileSystemRootPath, pc);
        pc.commitAndClose();
      } catch (Throwable e) {
        if (pc!=null)
          pc.rollbackAndClose();
        Tracer.logExceptionOnPlatformOrOther(e);
        throw new PlatformRuntimeException(e);
      }
    }

    for (Release r : releases) {
      if (r.needsToBeLaunched || doSetup)
        r.propertyFillAfterHibernateFactory();
    }


    for (Release r : releases) {
      if (r.needsToBeLaunched || doSetup)
        r.schemaRefinementAfterHibernateFactory();

    }

  }


  public static boolean isSomeUpdateNeeded() {
    for (Release r : releases) {
      if (r.needsToBeLaunched)
        return true;
    }
    return false;
  }

}
