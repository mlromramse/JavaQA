package org.jblooming.persistence.hibernate;


import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.mapping.PersistentClass;
import org.jblooming.PlatformRuntimeException;
import org.jblooming.logging.Sniffer;
import org.jblooming.ontology.IdentifiableSupport;
import org.jblooming.persistence.ThreadLocalPersistenceContextCarrier;
import org.jblooming.persistence.PersistenceHome;
import org.jblooming.persistence.exceptions.PersistenceException;
import org.jblooming.system.SystemConstants;
import org.jblooming.tracer.Tracer;
import org.jblooming.utilities.JSP;
import org.jblooming.waf.constants.Fields;
import org.jblooming.waf.settings.ApplicationState;
import org.jblooming.waf.settings.PersistenceConfiguration;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Iterator;

/**
 * @author Pietro Polsinelli ppolsinelli@open-lab.com
 */
public class PersistenceContext {

  public Session session;
  public PersistenceConfiguration persistenceConfiguration;

  public static ThreadLocal<ThreadLocalPersistenceContextCarrier> threadLocalPersistenceContextCarrier = new ThreadLocal<ThreadLocalPersistenceContextCarrier>() {

    protected ThreadLocalPersistenceContextCarrier initialValue() {
      return null;
    }
  };

  public PersistenceContext() {
    this((Connection) null);
  }


  public PersistenceContext(Connection c) {
    this(null, c);
  }


  /**
   * this is not managed by the filter
   */
  public PersistenceContext(String persistenceConfigurationName, Connection c) {

    if (JSP.ex(persistenceConfigurationName))
      persistenceConfiguration = PersistenceConfiguration.persistenceConfigurations.get(persistenceConfigurationName);
    else
      persistenceConfiguration = PersistenceConfiguration.getFirstPersistenceConfiguration();


    Sniffer interceptor = null;
    if (Fields.TRUE.equals(ApplicationState.getApplicationSetting(SystemConstants.AUDIT)))
      interceptor = new Sniffer();

    Throwable t = null;
    try {
      if (c != null && interceptor != null)
        session = persistenceConfiguration.getSessionFactory().openSession(c, interceptor);
      else if (c != null)
        session = persistenceConfiguration.getSessionFactory().openSession(c);
      else if (interceptor != null)
        session = persistenceConfiguration.getSessionFactory().openSession(interceptor);
      else
        session = persistenceConfiguration.getSessionFactory().openSession();

      session.beginTransaction();

    } catch (Throwable e) {
      t = e;
    }

    if (session == null )
      throw new PlatformRuntimeException("PersistenceContext: unable to create session. It's null.");
    else if ( t != null )
      throw new PlatformRuntimeException(t);

   
  }

  public static Connection getNewConnection() throws ClassNotFoundException, SQLException {
    PersistenceConfiguration pcf = PersistenceConfiguration.getDefaultPersistenceConfiguration();
    String datasource = pcf.dataSource;
    if(JSP.ex(datasource)) {
      Connection dataSourceConnection = null;
      try {
        Context ctx = new InitialContext();
        if (ctx == null )
          throw new PlatformRuntimeException("No javax.naming.Context: connection  can not be established!");
        DataSource ds = (DataSource)ctx.lookup(datasource);
        dataSourceConnection = ds.getConnection();
      } catch (Throwable throwable) {
        throw new PlatformRuntimeException("A connection can not be established:: " +throwable);
      }
      return dataSourceConnection;

    } else {
    Class.forName(pcf.driver_class);
    return DriverManager.getConnection(pcf.driver_url, pcf.db_user_name, pcf.db_user_psw);
  }
  }

  private void close(boolean thereHasBeenAnException) throws PersistenceException {

    boolean exceptionInCommit = true;
    if (!thereHasBeenAnException) {

      try {
        if (session.getTransaction() != null) {
          session.getTransaction().commit();
          exceptionInCommit = false;
        }
      } catch (Throwable throwable) {
        Tracer.platformLogger.error(throwable);
      }
    }

    if (thereHasBeenAnException || exceptionInCommit) {
      try {
        if (session.getTransaction() != null) {
          session.getTransaction().rollback();
        }
      } catch (Throwable throwable) {
        Tracer.platformLogger.error(throwable);
      }
    }

    try {
      //as we are NOT using Hibernate managed context, in contrast with FrontControllerFilter, there is need after commit/rollbacdk to close session
      session.close();
    } catch (Throwable throwable) {
      throw new PersistenceException(throwable);
    } finally {
      session=null;
      ThreadLocalPersistenceContextCarrier carrier = threadLocalPersistenceContextCarrier.get();
      if (carrier!=null && this.equals(carrier.currentPC)){
        threadLocalPersistenceContextCarrier.get().currentPC = null;
        threadLocalPersistenceContextCarrier.set(null);
      }
    }
  }

  public void checkPoint() {
    if (session.isOpen()) {
      Transaction currentTransaction = session.getTransaction();
      if (session.isOpen() && currentTransaction != null && !currentTransaction.wasRolledBack()) {
        session.flush();
        currentTransaction.commit();
        session.beginTransaction();
      }
    }
  }

  public void commitAndClose() throws PersistenceException {
    close(false);
  }

  public void rollbackAndClose() {
    try {
      close(true);
    } catch (PersistenceException e) {
    }
  }



  public static PersistenceContext getDefaultPersistenceContext() {
    return switchTo((String) null);
  }

  public static PersistenceContext switchToFirst() {
    return switchTo(PersistenceConfiguration.getFirstPersistenceConfiguration().name);
  }

  public static PersistenceContext switchTo(Class<? extends IdentifiableSupport> persistentClass) {
    PersistenceContext persistenceContext = get(persistentClass);
    threadLocalPersistenceContextCarrier.get().currentPC = persistenceContext;
    return persistenceContext;
  }


  public static PersistenceContext switchTo(String persistenceConfigurationName) {
    // set last used as current
    PersistenceContext pc = getByConfigurationName(persistenceConfigurationName);
    threadLocalPersistenceContextCarrier.get().currentPC = pc;
    return pc;
  }

    public static PersistenceContext get(Class<? extends IdentifiableSupport> persistentClass) {
    String conf = null;
    for (String confName : PersistenceConfiguration.persistenceConfigurations.keySet()) {
      PersistenceConfiguration pcf = PersistenceConfiguration.persistenceConfigurations.get(confName);
      if (pcf.getHibernateConfiguration().getClassMapping(persistentClass.getName()) != null) {
        conf = confName;
        break;
      }
    }
    return getByConfigurationName(conf);
  }


  public static PersistenceContext get(IdentifiableSupport persistentObj) {
    String className=PersistenceHome.deProxy(persistentObj.getClass().getName());
    String conf = null;
    for (String confName : PersistenceConfiguration.persistenceConfigurations.keySet()) {
      PersistenceConfiguration pcf = PersistenceConfiguration.persistenceConfigurations.get(confName);
      if (pcf.getHibernateConfiguration().getClassMapping(className) != null) {
        conf = confName;
        break;
      }
    }
    return getByConfigurationName(conf);
  }


 public static PersistenceContext get(String className) {
    PersistenceContext pc;
    try {
      pc = PersistenceContext.get((Class<? extends IdentifiableSupport>) Class.forName(className));
    } catch (ClassNotFoundException e) {
      String conf = null;
      for (String confName : PersistenceConfiguration.persistenceConfigurations.keySet()) {
        PersistenceConfiguration pcf = PersistenceConfiguration.persistenceConfigurations.get(confName);
        Iterator ite= pcf.getHibernateConfiguration().getClassMappings();

        while (ite.hasNext()){
          PersistentClass pcla= (PersistentClass) ite.next();
          if (pcla.getNodeName().equals(className)){
            conf=confName;
            break;
          }
        }

      }
      if (JSP.ex(conf))
        return getByConfigurationName(conf);
      else
        throw new PlatformRuntimeException(e);
    }
    return pc;
  }


  private static PersistenceContext getByConfigurationName(String persistenceConfigurationName) {
    //there could be already a closed session on it

    ThreadLocalPersistenceContextCarrier localPersistenceContextCarrier = null;
    // exists ThreadLocal?
    if (threadLocalPersistenceContextCarrier.get() == null) {
      localPersistenceContextCarrier = new ThreadLocalPersistenceContextCarrier();
      threadLocalPersistenceContextCarrier.set(localPersistenceContextCarrier);
    }
    localPersistenceContextCarrier = threadLocalPersistenceContextCarrier.get();

    // guess the right persistenceConfiguration Name
    PersistenceConfiguration persistenceConfiguration;
    if (JSP.ex(persistenceConfigurationName))
      persistenceConfiguration = PersistenceConfiguration.persistenceConfigurations.get(persistenceConfigurationName);
    else {
      // if there is a current in use sedimented on threadlocal, give that
      if (localPersistenceContextCarrier.currentPC != null)
        persistenceConfiguration = localPersistenceContextCarrier.currentPC.persistenceConfiguration;
        //no current sedimented on threadlocal, use the first, "main" one
      else
        persistenceConfiguration = PersistenceConfiguration.getFirstPersistenceConfiguration();
    }

    // exist a PersistentContext for this conf ?
    PersistenceContext persistenceContext = localPersistenceContextCarrier.getPersistenceContext(persistenceConfiguration.name);
    if (persistenceContext == null) {
      persistenceContext = new PersistenceContext(persistenceConfigurationName, null);
      localPersistenceContextCarrier.putPersistenceContext(persistenceContext);
    } else {
      // check if sessions are still good
      if (persistenceContext.session != null && !(persistenceContext.session.isOpen())) {
        persistenceContext = new PersistenceContext(persistenceConfigurationName, null);
        localPersistenceContextCarrier.putPersistenceContext(persistenceContext);
        Tracer.platformLogger.error("Creating currupted new pc thlocid:"+localPersistenceContextCarrier.hashCode()+"\n"+Tracer.getCallTrace(true));
        
      }
    }

    return persistenceContext;

  }



}