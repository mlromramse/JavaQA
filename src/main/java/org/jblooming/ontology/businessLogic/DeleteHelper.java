package org.jblooming.ontology.businessLogic;

import org.hibernate.*;
import org.hibernate.engine.CascadesProxy;
import org.hibernate.engine.SessionFactoryImplementor;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.type.CollectionType;
import org.hibernate.type.EntityType;
import org.hibernate.type.OneToOneType;
import org.hibernate.type.Type;
import org.jblooming.PlatformRuntimeException;
import org.jblooming.messaging.Listener;
import org.jblooming.ontology.*;
import org.jblooming.oql.OqlQuery;
import org.jblooming.oql.QueryHelper;
import org.jblooming.persistence.PersistenceHome;
import org.jblooming.persistence.exceptions.PersistenceException;
import org.jblooming.persistence.exceptions.RemoveException;
import org.jblooming.persistence.exceptions.FindByPrimaryKeyException;
import org.jblooming.persistence.hibernate.HibernateFactory;
import org.jblooming.persistence.hibernate.HibernateUtilities;
import org.jblooming.persistence.hibernate.PersistenceContext;
import org.jblooming.tracer.Tracer;
import org.jblooming.utilities.ReflectionUtilities;
import org.jblooming.waf.constants.Commands;
import org.jblooming.waf.constants.ObjectEditorConstants;
import org.jblooming.waf.view.ClientEntries;
import org.jblooming.waf.view.ClientEntry;
import org.jblooming.waf.view.PageState;

import java.lang.reflect.InvocationTargetException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.io.Serializable;

public class DeleteHelper {

  /**
   * This method should be used when
   * @param delendo
   * @param pageState
   * @param propertyName_command one or more Pair (name_of_the_property, command) where command is one of Commands.UNLINK, Commands.UP, Commands.DELETE_DESCENDANTS
   */
  public void cmdDeleteForced(IdentifiableSupport delendo,PageState pageState, Pair... propertyName_command) throws PersistenceException {
    for (Pair nc:propertyName_command){
      pageState.addClientEntry(ObjectEditorConstants.FLD_DELETE_STYLE + "__" + nc.first,nc.second+"");
    }
    cmdDelete(delendo,pageState);
  }


  public static void cmdDelete(IdentifiableSupport delendo, PageState pageState) throws PersistenceException {

    try {
      delendo = (IdentifiableSupport) ReflectionUtilities.getUnderlyingObject(delendo);
      boolean delendaIsNode = delendo instanceof Node;
      Node parent = null;
      if (delendaIsNode)
        parent = ((Node) delendo).getParentNode();
      final ClientEntries clientEntries = pageState.getClientEntries();
      Set cesKeys = clientEntries.getEntryKeys();

      Class objClass = delendo.getClass();
      String clazzName = PersistenceHome.deProxy(objClass.getName());
      objClass = Class.forName(clazzName);
      PersistenceContext persistenceContext = PersistenceContext.get(delendo);
      SessionFactory sf = persistenceContext.persistenceConfiguration.getSessionFactory();

      EntityPersister entityPersister = null;

      entityPersister = (EntityPersister) sf.getClassMetadata(objClass);
      PersistentClass pclass = HibernateUtilities.getClassMapping(objClass);

      List<Property> allProps = new ArrayList();

      Iterator it = pclass.getPropertyClosureIterator();
      while (it.hasNext()) {
        allProps.add((Property) it.next());
      }
      CascadesProxy csp = new CascadesProxy();

      //String[] names = entityPersister.getPropertyNames();

      for (Property property : allProps) {

        String name = property.getName();

        EntityMode mode = PersistenceContext.get(delendo).session.getEntityMode();
        Object propertyValue = entityPersister.getPropertyValue(delendo, name, mode);

        if (propertyValue != null) {
          if (propertyValue instanceof Collection && ((Collection) propertyValue).size() > 0) {
            Collection coll = (Collection) propertyValue;
            Object sample = coll.iterator().next();
            boolean membersAreChildren = delendaIsNode && coll.equals(((Node) delendo).getChildrenNode());
            if (sample instanceof Identifiable) {
              if (cesKeys != null && cesKeys.size() > 0) {

                final ClientEntry entry = clientEntries.getEntry(ObjectEditorConstants.FLD_DELETE_STYLE + "__" + name);
                boolean globalUnlinkForThisColl = entry != null && entry.stringValueNullIfEmpty() != null && Commands.UNLINK.equals(entry.stringValueNullIfEmpty());
                boolean globalUpForThisColl = entry != null && entry.stringValueNullIfEmpty() != null && Commands.UP.equals(entry.stringValueNullIfEmpty());
                boolean globalDelDescForThisColl = entry != null && entry.stringValueNullIfEmpty() != null && Commands.DELETE_DESCENDANTS.equals(entry.stringValueNullIfEmpty());

                for (Iterator iterator = new ArrayList(coll).iterator(); iterator.hasNext();) {
                  Identifiable memberOfCollection = (Identifiable) iterator.next();
                  String selectedCommand = pageState.getEntry(ObjectEditorConstants.FLD_DELETE_STYLE + "__" + name + "__" + memberOfCollection.getId()).stringValueNullIfEmpty();

                  boolean doesPropertyCascade = csp.doesCascadeOnDelete(property.getCascadeStyle());

                  // move to root
                  if (globalUnlinkForThisColl || Commands.UNLINK.equals(selectedCommand)) {

                    //remove from this collection if is no cascade
                    if (!doesPropertyCascade)
                      coll.remove(memberOfCollection);

                    if (membersAreChildren) {
                      if (memberOfCollection instanceof PerformantNode)
                        ((PerformantNodeSupport) memberOfCollection).setParentAndStore(null);
                      else
                        ((Node) memberOfCollection).setParentNode(null);
                    }
                    //move to parent
                  } else if (globalUpForThisColl || Commands.UP.equals(selectedCommand)) {

                    //remove from this collection if is no cascade
                    if (!doesPropertyCascade)
                      coll.remove(memberOfCollection);

                    if (membersAreChildren) {
                      if (memberOfCollection instanceof PerformantNode)
                        ((PerformantNodeSupport) memberOfCollection).setParentAndStore((PerformantNodeSupport) parent);
                      else {
                        ((Node) memberOfCollection).setParentNode(parent);
                        //add to parent's collection
                        ((Collection) entityPersister.getPropertyValue(parent, name, mode)).add(memberOfCollection);
                      }
                    } else {
                      //add to parent's collection
                      ((Collection) entityPersister.getPropertyValue(parent, name, mode)).add(memberOfCollection);
                    }

                    //delete also members
                  } else if (globalDelDescForThisColl || Commands.DELETE_DESCENDANTS.equals(selectedCommand)) {

                    //remove from this collection if is no cascade
                    if (!doesPropertyCascade)
                      coll.remove(memberOfCollection);

                    final Node node = (Node) memberOfCollection;
                    if (membersAreChildren) {
                      node.setParentNode(null);
                      recursivelyDeleteNode(node, pageState);
                    } else
                      deleteIdentifiable((IdentifiableSupport)memberOfCollection, pageState);
                  }
                }
              }
            }
          }
        }
      }
      deleteIdentifiable(delendo, pageState);
    } catch (ClassNotFoundException e) {
      throw new PlatformRuntimeException(e);
    } catch (HibernateException e) {
      throw new PlatformRuntimeException(e);
    }
  }

  private static void deleteIdentifiable(IdentifiableSupport ident, PageState ps) throws RemoveException {

    PersistenceContext dpc = PersistenceContext.get(ident);

    try {
      ident.remove();
      dpc.session.flush();
      // delete all relative listeners
      QueryHelper qh = new QueryHelper("from " + Listener.class.getName());
      qh.addQBEClause("theClass", "theClass", ident.getClass().getName(), QueryHelper.TYPE_CHAR);
      qh.addQBEClause("identifiableId", "identifiableId", ident.getId().toString(), QueryHelper.TYPE_CHAR);
    } catch (Throwable e) {

      Transaction t = dpc.session.getTransaction();
      if (t != null && !t.wasRolledBack()) {
        t.rollback();
        dpc.session.beginTransaction();
      }
      Serializable id = ident.getId();
      Class cl = ReflectionUtilities.getUnderlyingObjectClass(ident);
      // 3Dec2007 changed to clear: evict could leave cascading refences "alive" and set as deleted
      //dpc.session.evict(ident);
      dpc.session.clear();

      try {
        ident = (IdentifiableSupport) PersistenceHome.findByPrimaryKey(cl, id);
      } catch (FindByPrimaryKeyException e1) {
      }

      ps.setMainObjectId(ident.getId());
      ps.setMainObject(ident);
      ps.resetLoggedOperator();
      
      throw new RemoveException(e);
    }
  }

  public static void recursivelyDeleteNode(Node node, PageState ps) throws RemoveException {

    Collection children = node.getChildrenNode();
    if (children != null && children.size() > 0) {
      for (Iterator iterator = children.iterator(); iterator.hasNext();) {
        Node child = (Node) iterator.next();
        recursivelyDeleteNode(child, ps);
      }
    }
    deleteIdentifiable((IdentifiableSupport)node, ps);
  }

  public static void cmdDisintegrate(Collection disintegrandas) throws PersistenceException {
    for (Object r : disintegrandas) {
      DeleteHelper.cmdDisintegrate((IdentifiableSupport) r);
    }
  }

  public static void cmdDisintegrate(IdentifiableSupport delendo) throws PersistenceException {

    PersistenceContext pc = new PersistenceContext();
    Object underlyingObject = ReflectionUtilities.getUnderlyingObjectAsObject(delendo);
    Object value = null;
    try {
      value = underlyingObject.getClass().getMethod("getId").invoke(underlyingObject);
    } catch (Exception e) {
      throw new PlatformRuntimeException(e);
    }

    Object realDelendo = null;
    try {
      realDelendo = PersistenceHome.findUniqueObject(underlyingObject.getClass(), "id", value, pc);
    } finally {
    pc.commitAndClose();
    }

    pc = new PersistenceContext();

    Class realClass = realDelendo.getClass();

    Map<String, org.hibernate.mapping.Collection> inverses = HibernateUtilities.getAllInversesOnTarget(realDelendo);
    PersistenceContext persistenceContext = PersistenceContext.get(delendo);
    SessionFactory sf = persistenceContext.persistenceConfiguration.getSessionFactory();

    PersistentClass realClassPc = HibernateUtilities.getClassMapping(realClass);

    CascadesProxy csp = new CascadesProxy();

    Iterator i = persistenceContext.persistenceConfiguration.getHibernateConfiguration().getClassMappings();
    while (i.hasNext()) {

      PersistentClass persistentClass = (PersistentClass) i.next();
      Iterator j = persistentClass.getPropertyClosureIterator();
      while (j.hasNext()) {

        Property property = (Property) j.next();
        Column col = null;
        if (property.getColumnIterator().hasNext())
          col = (Column) property.getColumnIterator().next();

        //is there a cascading inverse for this property on realClass ?
        boolean cascadingInverseOnRealClass = false;

        for (String key : inverses.keySet()) {

          org.hibernate.mapping.Collection hibInvCollOfDelendo = inverses.get(key);
          try {
            Property propertyWhichIsCollection = realClassPc.getProperty(key);
            if (((Column) (hibInvCollOfDelendo.getKey().getColumnIterator().next())).getName().equals(property.getName()) &&
              csp.doesCascadeOnDelete(propertyWhichIsCollection.getCascadeStyle())
              ) {
              cascadingInverseOnRealClass = true;
              break;
            }
          } catch (MappingException e) {
          }
        }

        boolean isNullable = property.getValue().isNullable();

        //remove references from entities to delendo
        if (
          !cascadingInverseOnRealClass &&
            isNullable &&
            property.getType() instanceof EntityType &&
            ! (property.getType() instanceof OneToOneType) &&
            property.getType().getReturnedClass() != null &&
            //property.getType().getReturnedClass().getName().equals(realClass.getName()) &&
            realClassPc.getTable().getName().equals(HibernateUtilities.getTableName(property.getType().getReturnedClass())) &&
            //ReflectionUtilities.extendsOrImplements(realClass, property.getType().getReturnedClass()) &&
            !csp.doesCascadeOnDelete(property.getCascadeStyle())
          ) {
          String hql = "update " + persistentClass.getEntityName() + " set " + property.getName() + "=null where " + property.getName() + " = :disintegrando";
          OqlQuery oql = new OqlQuery(hql, pc);
          oql.getQuery().setEntity("disintegrando", realDelendo);
          Tracer.platformLogger.debug("cmdDisintegrate reference " + hql);
          oql.getQuery().executeUpdate();

          //remove references to delendo from non inverse external collections
        } else if (
          property.getType() instanceof CollectionType
          ) {

          Type elementType = ((CollectionType) property.getType()).getElementType((SessionFactoryImplementor) sf);
          if (elementType.isEntityType()) {
            Class collectionOf = elementType.getReturnedClass();
            String tableName = HibernateUtilities.getTableName(collectionOf);
            if (tableName != null && realClassPc.getTable().getName().equals(tableName)) {
              org.hibernate.mapping.Collection collection =  persistenceContext.persistenceConfiguration.getHibernateConfiguration().getCollectionMapping(((CollectionType) property.getType()).getRole());
              if (!collection.isInverse()) {
                String valueColumn = ((Column) collection.getElement().getColumnIterator().next()).getName();
                PreparedStatement ps = null;
                try {
                  String sql = "DELETE FROM " + collection.getCollectionTable().getName() + " WHERE " + valueColumn + "= ?";
                  ps = pc.session.connection().prepareStatement(sql);
                  Tracer.platformLogger.debug("cmdDisintegrate CollectionType " + sql);
                  //ps.setString(1, realDelendo.getId().toString());
                  ps.setString(1, realDelendo.getClass().getMethod("getId").invoke(realDelendo).toString());
                  ps.executeUpdate();
                  ps.close();
                } catch (SQLException e) {
                  throw new PlatformRuntimeException(e);
                } catch (NoSuchMethodException e) {
                  throw new PlatformRuntimeException(e);
                } catch (IllegalAccessException e) {
                  throw new PlatformRuntimeException(e);
                } catch (InvocationTargetException e) {
                  throw new PlatformRuntimeException(e);
                }
              }
            }
          }
        }
      }
    }

    pc.session.delete(realDelendo);
    pc.commitAndClose();
  }



}

//}
