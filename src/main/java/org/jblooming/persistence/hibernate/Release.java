package org.jblooming.persistence.hibernate;

import org.jblooming.scheduler.Executable;
import org.jblooming.scheduler.JobLogData;
import org.jblooming.tracer.Tracer;
import org.jblooming.waf.constants.Fields;
import org.jblooming.waf.settings.ApplicationState;
import org.jblooming.waf.settings.PersistenceConfiguration;
import org.jblooming.PlatformRuntimeException;
import org.jblooming.utilities.JSP;
import org.jblooming.utilities.StringUtilities;
import org.hibernate.mapping.Column;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.MySQLDialect;

import java.util.List;
import java.util.ArrayList;
import java.sql.*;

/**
 * (c) Open Lab - www.open-lab.com
 * Date: Jul 17, 2008
 * Time: 2:36:12 PM
 * <p/>
 * To force release launch, put "SETUP_DB_DONE_"+releaseLabel = no in global settings
 */
public class Release {

  public boolean needsToBeLaunched;
  public List<String> beforeHibSql = new ArrayList();
  public List<String> postHibSql = new ArrayList();
  public List<Executable> execs = new ArrayList();
  public List<PropertyWithDefault> pwds = new ArrayList();
  public String releaseLabel;

  public Release(String releaseLabel) {
    this.releaseLabel = releaseLabel;
    PlatformSchemaUpdater.releases.add(this);
  }


  public PropertyWithDefault addPropertyToCheck(Class clazz, String propertyName, Object defaultValue) {
    PropertyWithDefault pwd = new PropertyWithDefault();
    pwd.clazz = clazz;
    pwd.propertyName = propertyName;
    pwd.defaultValue = defaultValue;
    pwds.add(pwd);
    return pwd;
  }

  public PropertyWithDefault getProperty(Class clazz, String propertyName) {
    for (PropertyWithDefault propertyWithDefault : pwds) {
      if (propertyName.equalsIgnoreCase(propertyWithDefault.propertyName) && clazz.equals(propertyWithDefault.clazz))
        return propertyWithDefault;
    }
    return null;
  }

  public void verifyIfUpdateNeeded() {

    Connection conn = null;
    try {
      conn = PersistenceContext.getNewConnection();
      conn.setAutoCommit(false);
      ResultSet ps = null;
      PreparedStatement pst = null;
      for (PropertyWithDefault propertyWithDefault : pwds) {
        try {

          String tableName = HibernateUtilities.getTableName(propertyWithDefault.clazz);
          propertyWithDefault.table = tableName;

          //get column name
          Column c = HibernateUtilities.getColumn(propertyWithDefault.clazz, propertyWithDefault.propertyName);
          if (c != null) {
            propertyWithDefault.column = c.getName();

            String sql = "select * from " + tableName;
            try {
              Dialect d = (Dialect) PersistenceConfiguration.getDefaultPersistenceConfiguration().dialect.newInstance();
              if (d.supportsLimit()) {
                // MySQLDialect doesn't support next method until MySQL fix Connector/J bug 
                //if (MySQLDialect.class.getName().equals(d.getClass().getName())) {
                if (d instanceof MySQLDialect) {
                  sql = "select * from " + tableName + " LIMIT 1";

                } else {
                  sql = d.getLimitString("select * from " + tableName, 0, 1);
                }

              }
            } catch (Throwable e) {
              Tracer.platformLogger.error(e);
            }

            pst = conn.prepareStatement(sql);
            int count = StringUtilities.count(sql, '?');
            if (count == 1) // if there is a limit string there is also a parameter to set
              pst.setInt(1, 1);
            else if (count == 2) {
              pst.setInt(1, 0);
              pst.setInt(2, 1);
            }

            ps = pst.executeQuery();

            ResultSetMetaData metaData = ps.getMetaData();
            int cc = metaData.getColumnCount();
            boolean existsColumn = false;
            for (int i = 1; i <= cc; i++) {
              String columnName = metaData.getColumnName(i);
              if (c.getName().equalsIgnoreCase(columnName)) {
                existsColumn = true;
                break;
              }
            }
            propertyWithDefault.needsToBeUpdated = !existsColumn;
            if (!needsToBeLaunched)
              needsToBeLaunched = !existsColumn;
            ps.close();
            pst.close();
            conn.commit();
          }
        } catch (SQLException e) {
          //beautiful hack to cover cases where table itself is missing
          propertyWithDefault.needsToBeUpdated = true;
          needsToBeLaunched = true;
          throw new Exception(e);

        } catch (Throwable e) {

          if (ps != null)
            try {
              ps.close();
              pst.close();
            } catch (SQLException e1) {
              Tracer.platformLogger.warn(e1);
            }

          //may fail as schema does not exist
          //throw new PlatformRuntimeException(e);
          Tracer.platformLogger.warn("propertyWithDefault.column: " + propertyWithDefault.column, e);
        }
      }
      conn.close();

    } catch (Exception e) {

      try {
        if (conn != null && !conn.isClosed())
          conn.close();
      } catch (SQLException e1) {
        Tracer.platformLogger.warn(e1);
      }
      Tracer.platformLogger.warn(e);
    }

    if (!needsToBeLaunched)
      needsToBeLaunched = Fields.FALSE.equals(ApplicationState.applicationSettings.get("SETUP_DB_DONE_" + releaseLabel));


  }

  public void schemaRefinementBeforeHibernateFactory() {
    PersistenceContext pc = null;
    try {
      pc = new PersistenceContext();
      Connection conn = pc.session.connection();

      for (String sql : beforeHibSql) {
        PreparedStatement ps = null;
        try {
          Tracer.platformLogger.debug("beforeHibSql:" + sql);
          ps = conn.prepareStatement(sql);
          ps.execute();
          ps.close();
        } catch (Throwable e) {
          if (ps != null)
            try {
              ps.close();
            } catch (Throwable e1) {
              Tracer.platformLogger.warn(e);
            }
          Tracer.platformLogger.warn(e);
        }
      }

      pc.commitAndClose();

    } catch (Exception e) {

      if (pc != null)
        pc.rollbackAndClose();
      Tracer.platformLogger.warn(e);
    }
  }


  public void propertyFillAfterHibernateFactory() {

    PersistenceContext pc = null;
    try {
      pc = new PersistenceContext();
      Connection conn = pc.session.connection();

      for (PropertyWithDefault propertyWithDefault : pwds) {

        if (propertyWithDefault.needsToBeUpdated) {
          if (propertyWithDefault.defaultValue != null) {
            PreparedStatement ps = null;
            try {
              String sql = "update " + propertyWithDefault.table + " set " + propertyWithDefault.column + " = ?";
              ps = conn.prepareStatement(sql);
              ps.setObject(1, propertyWithDefault.defaultValue);
              Tracer.platformLogger.debug("defaultValue:" + sql);
              ps.execute();
            } catch (Throwable e) {
              Tracer.platformLogger.error(e);
            } finally {
              if (ps != null)
                ps.close();
            }
          }
        }
      }
      pc.commitAndClose();
    } catch (Throwable e) {
      if (pc != null)
        pc.rollbackAndClose();
      Tracer.platformLogger.error("Update to release " + releaseLabel + " failed.");
      throw new PlatformRuntimeException(e);
    }
  }

  public void schemaRefinementAfterHibernateFactory() {

    PersistenceContext pc = null;
    try {
      pc = new PersistenceContext();
      Connection conn = pc.session.connection();

      for (Executable exe : execs) {

        try {
          JobLogData log = new JobLogData();
          Tracer.platformLogger.debug("exe:" + exe);
          exe.run(log);
          pc.checkPoint();
          if (log.notes != null && log.notes.length() > 0)
            Tracer.platformLogger.info(log.notes);
        } catch (Throwable e) {
          Tracer.platformLogger.error(e);
        }
      }

      for (String sql : postHibSql) {
        PreparedStatement ps = null;
        try {
          ps = conn.prepareStatement(sql);
          Tracer.platformLogger.debug("postSql:" + sql);
          ps.execute();
          ps.close();
          pc.checkPoint();
        } catch (Throwable t) {
          if (ps != null)
            ps.close();
          if (t.getMessage() == null || !t.getMessage().toLowerCase().contains("column not found"))
            Tracer.platformLogger.error(t);
        }
      }

      pc.commitAndClose();

      if (Fields.FALSE.equals(ApplicationState.applicationSettings.get("SETUP_DB_DONE_" + releaseLabel)))
        ApplicationState.applicationSettings.put("SETUP_DB_DONE_" + releaseLabel, Fields.TRUE);

      Tracer.platformLogger.debug("Update to release " + releaseLabel + " succeeded.");

    } catch (Throwable e) {
      if (pc != null)
        pc.rollbackAndClose();
      Tracer.platformLogger.error("Update to release " + releaseLabel + " failed.");

      throw new PlatformRuntimeException(e);
    }
  }


}
