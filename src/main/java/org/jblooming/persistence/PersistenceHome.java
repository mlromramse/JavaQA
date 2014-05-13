package org.jblooming.persistence;

import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.type.Type;
import org.jblooming.cursor.Cursor;
import org.jblooming.ontology.Identifiable;
import org.jblooming.ontology.IdentifiableSupport;
import org.jblooming.oql.OqlQuery;
import org.jblooming.persistence.exceptions.*;
import org.jblooming.persistence.hibernate.HibernateFactory;
import org.jblooming.persistence.hibernate.PersistenceContext;
import org.jblooming.utilities.JSP;

import java.io.Serializable;
import java.util.List;

/**
 * Handles the persistence of all identifiable objects
 */
public class PersistenceHome {

  public static final NewEmptyId NEW_EMPTY_ID = new NewEmptyId();

  /**
   * @param clazz
   * @param id
   * @return
   * @throws FindByPrimaryKeyException if not found
   */
  public static Identifiable findByPrimaryKey(Class<? extends Identifiable> clazz, int id) throws FindByPrimaryKeyException {
    return HibernateFactory.findByPrimaryKey(clazz, id);
  }

  public static Identifiable findByPrimaryKey(Class<? extends Identifiable> clazz, Serializable id) throws FindByPrimaryKeyException {
    return HibernateFactory.findByPrimaryKey(clazz, id);
  }

  public static Identifiable findFirst(Class<? extends Identifiable> clazz, String field, Object value) throws PersistenceException {
    final OqlQuery oqlQuery = new OqlQuery("from " + clazz.getName() + " as obj where obj." + field + " = :aparam");
    oqlQuery.getQuery().setMaxResults(1);
    try {
      List list = oqlQuery.getQuery().setParameter("aparam", value).list();
      if (JSP.ex(list))
        return (Identifiable) list.get(0);
      else
        return null;
    } catch (HibernateException e) {
      throw new PersistenceException(oqlQuery.doDebug(new Object[]{value}),e);
    }
  }



  public static Identifiable findUnique(Class<? extends Identifiable> clazz, String field, Object value) throws PersistenceException {
    final OqlQuery oqlQuery = new OqlQuery("from " + clazz.getName() + " as obj where obj." + field + " = :aparam");
    try {
      return (Identifiable) oqlQuery.getQuery().setParameter("aparam", value).uniqueResult();
    } catch (HibernateException e) {
      throw new PersistenceException(oqlQuery.doDebug(new Object[]{value}),e);
    }
  }

  public static Identifiable findUnique(Class<? extends Identifiable> clazz, String field, Object value, PersistenceContext pc) throws PersistenceException {
    final OqlQuery oqlQuery = new OqlQuery("from " + clazz.getName() + " as obj where obj." + field + " = :aparam", pc);
    try {
      return (Identifiable) oqlQuery.getQuery().setParameter("aparam", value).uniqueResult();
    } catch (HibernateException e) {
      throw new PersistenceException(oqlQuery.doDebug(new Object[]{value}),e);
    }
  }

  public static Object findUniqueObject(Class clazz, String field, Object value, PersistenceContext pc) throws PersistenceException {
    final OqlQuery oqlQuery = new OqlQuery("from " + clazz.getName() + " as obj where obj." + field + " = :aparam", pc);
    try {
      return oqlQuery.getQuery().setParameter("aparam", value).uniqueResult();
    } catch (HibernateException e) {
      throw new PersistenceException(oqlQuery.doDebug(new Object[]{value}),e);
    }
  }


  public static Identifiable findUniqueNullIfEmpty(Class<? extends Identifiable> clazz, String field, Object value) {
    Identifiable res = null;
    try {
      res = findUnique(clazz, field, value);
    } catch (PersistenceException e) {
    }
    return res;
  }

  public static Identifiable findUniqueNullIfEmpty(Class<? extends Identifiable> clazz, String field, Object value, PersistenceContext pc) {
    Identifiable res = null;
    try {
      res = findUnique(clazz, field, value, pc);
    } catch (PersistenceException e) {
    }
    return res;
  }

  public static Identifiable findByPrimaryKey(Class<? extends Identifiable> clazz, Serializable id, PersistenceContext pc) throws FindByPrimaryKeyException {
    return HibernateFactory.findByPrimaryKey(clazz, id, pc);
  }

  /**
   * @param i
   * @throws StoreException if anything goes wrong, say the database is unreachable, the exception is wrapped in this
   * @deprecated use i.store()
   */
  public static void store(IdentifiableSupport i) throws StoreException {
    i.store();
  }

  public static void store(Identifiable i, PersistenceContext pc) throws StoreException {
    HibernateFactory.store(i, pc);
  }



  public static void refresh(IdentifiableSupport i) {
    PersistenceContext.get(i).session.refresh(i);
  }

  public static void reAssociateUnmodified(IdentifiableSupport i){
    PersistenceContext.get(i).session.lock(i, LockMode.NONE);
  }

  /**
   * @deprecated use i.remove()
   */
  public static void remove(IdentifiableSupport i) throws RemoveException {
    i.remove();
  }

  public static void remove(IdentifiableSupport i, PersistenceContext pc) throws RemoveException {
    HibernateFactory.remove(i, pc.session);
  }

  /**
   * deprecated use Hibernate.getClass on the object
   */
  public static String deProxy(String className) {

    String result = className;
    int cglibbed = className.indexOf("_$$");
    if (cglibbed > -1)
      result = className.substring(0, cglibbed);
    else {
      cglibbed = className.indexOf("$$");
      if (cglibbed > -1)
        result = className.substring(0, cglibbed);      
    }
    return result;

  }

  public static boolean isInitialized(Object o) {
    return HibernateFactory.isInitialized(o);
  }

  public static void initialize(Object o) throws HibernateException {
    HibernateFactory.initialize(o);
  }

  public static String dePackage(String className) {
    return PersistenceHome.deProxy(className).substring(PersistenceHome.deProxy(className).lastIndexOf('.') + 1);
  }


  public static class NewEmptyId implements Serializable {

    static final String newEmptyId = "newEmptyId";

    public boolean equals(Object o) {
      return newEmptyId.equals(o + "");
    }

    public String toString() {
      return newEmptyId;
    }

  }

}
