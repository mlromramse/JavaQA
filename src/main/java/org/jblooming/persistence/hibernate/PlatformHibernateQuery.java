package org.jblooming.persistence.hibernate;


import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.FlushMode;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.impl.QueryImpl;
import org.hibernate.impl.AbstractSessionImpl;
import org.hibernate.type.Type;
import org.jblooming.waf.settings.ApplicationState;
import org.jblooming.waf.settings.PlatformConfiguration;
import org.jblooming.waf.settings.PersistenceConfiguration;

import java.util.Collection;

/**
 * @author Pietro Polsinelli ppolsinelli@open-lab.com
 * @author Roberto Bicchierai rbicchierai@open-lab.com
 */
public class PlatformHibernateQuery extends QueryImpl {

  public PlatformHibernateQuery(String oql, SessionImplementor sessionImplementor) {
    super(oql, null,sessionImplementor,null);//((AbstractSessionImpl)sessionImplementor).getHQLQueryPlan( oql, false ).getParameterMetadata());
  }

  public Query setParameter(String name, Object val, Type type) {
    if ((
            !PersistenceConfiguration.getDefaultPersistenceConfiguration().stringPrimitiveFieldsConversion.equals(PlatformConfiguration.StringConversionType.NONE) ||
                    !PersistenceConfiguration.getDefaultPersistenceConfiguration().stringPrimitiveFieldsConversion.equals(PlatformConfiguration.StringConversionType.NONE)) &&
            type.equals(Hibernate.STRING)) {
      if (PersistenceConfiguration.getDefaultPersistenceConfiguration().stringPrimitiveFieldsConversion.equals(PlatformConfiguration.StringConversionType.LOWER))
        val = ((String) val).toLowerCase();
      else
        val = ((String) val).toUpperCase();
    }
    return super.setParameter(name, val, type);
  }


  public Query setParameter(int position, Object val, Type type) {

    if ((!PersistenceConfiguration.getDefaultPersistenceConfiguration().stringPrimitiveFieldsConversion.equals(PlatformConfiguration.StringConversionType.NONE) ||
            !PersistenceConfiguration.getDefaultPersistenceConfiguration().stringPrimitiveFieldsConversion.equals(PlatformConfiguration.StringConversionType.NONE)) &&
            type.equals(Hibernate.STRING)) {
      if (PersistenceConfiguration.getDefaultPersistenceConfiguration().stringPrimitiveFieldsConversion.equals(PlatformConfiguration.StringConversionType.LOWER))
        val = ((String) val).toLowerCase();
      else
        val = ((String) val).toUpperCase();
    }
    return super.setParameter(position, val, type);
  }

  public Query setParameterList(String name, Collection vals, Type type) throws HibernateException {
    if ((!PersistenceConfiguration.getDefaultPersistenceConfiguration().stringPrimitiveFieldsConversion.equals(PlatformConfiguration.StringConversionType.NONE) ||
            !PersistenceConfiguration.getDefaultPersistenceConfiguration().stringPrimitiveFieldsConversion.equals(PlatformConfiguration.StringConversionType.NONE)) &&
            type.equals(Hibernate.STRING)) {
      if (PersistenceConfiguration.getDefaultPersistenceConfiguration().stringPrimitiveFieldsConversion.equals(PlatformConfiguration.StringConversionType.LOWER))
        for (Object val : vals) {
          val = ((String) val).toLowerCase();
        }
      else
        for (Object val : vals) {
          val = ((String) val).toUpperCase();
        }
    }
    return super.setParameterList(name, vals, type);
  }


}
