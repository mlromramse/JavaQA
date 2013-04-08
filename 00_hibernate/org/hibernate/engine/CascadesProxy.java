package org.hibernate.engine;

import org.hibernate.persister.entity.SingleTableEntityPersister;
import org.hibernate.SessionFactory;
import org.hibernate.Hibernate;
import org.jblooming.persistence.hibernate.*;
import org.jblooming.persistence.hibernate.PersistenceContext;
import org.jblooming.ontology.IdentifiableSupport;

/**
 * @author Pietro Polsinelli ppolsinelli@open-lab.com
 * @author Roberto Bicchierai rbicchierai@open-lab.com
 */
public class CascadesProxy extends CascadeStyle {

  public boolean doCascade(CascadingAction cascadingAction) {
    return false;
  }

  public boolean doesCascadeOnDelete(int i, Object delendo) {

    PersistenceContext persistenceContext = PersistenceContext.get((IdentifiableSupport)delendo);
    SessionFactory sf = persistenceContext.persistenceConfiguration.getSessionFactory();

    SingleTableEntityPersister entityPersister = (SingleTableEntityPersister) sf.getClassMetadata(Hibernate.getClass(delendo));
    CascadeStyle[] cs = entityPersister.getPropertyCascadeStyles();
    CascadeStyle cascade = cs[i];

    return cascade.doCascade(CascadingAction.DELETE);
  }

  public boolean doesCascadeOnDelete(CascadeStyle cesso) {
    return cesso.doCascade(CascadingAction.DELETE);
  }


}
