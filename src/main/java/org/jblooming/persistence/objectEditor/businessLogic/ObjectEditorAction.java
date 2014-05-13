package org.jblooming.persistence.objectEditor.businessLogic;

import org.hibernate.Query;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.SimpleValue;
import org.hibernate.type.*;
import org.jblooming.ApplicationException;
import org.jblooming.PlatformRuntimeException;
import org.jblooming.ontology.*;
import org.jblooming.ontology.businessLogic.DeleteHelper;
import org.jblooming.operator.Operator;
import org.jblooming.oql.QueryHelper;
import org.jblooming.page.HibernatePage;
import org.jblooming.persistence.PersistenceHome;
import org.jblooming.persistence.exceptions.FindByPrimaryKeyException;
import org.jblooming.persistence.exceptions.PersistenceException;
import org.jblooming.persistence.exceptions.StoreException;
import org.jblooming.persistence.hibernate.HibernateFactory;
import org.jblooming.persistence.hibernate.HibernateUtilities;
import org.jblooming.persistence.hibernate.PersistenceContext;
import org.jblooming.persistence.objectEditor.FieldFeature;
import org.jblooming.persistence.objectEditor.ObjectEditor;
import org.jblooming.system.SystemConstants;
import org.jblooming.utilities.JSP;
import org.jblooming.utilities.ReflectionUtilities;
import org.jblooming.waf.exceptions.ActionException;
import org.jblooming.waf.html.display.Paginator;
import org.jblooming.waf.html.input.SmartCombo;
import org.jblooming.waf.html.input.Uploader;
import org.jblooming.waf.html.table.ListHeader;
import org.jblooming.waf.settings.ApplicationState;
import org.jblooming.waf.state.PersistentSearch;
import org.jblooming.waf.view.ClientEntry;
import org.jblooming.waf.view.PageSeed;
import org.jblooming.waf.view.PageState;
import org.jblooming.waf.constants.FieldErrorConstants;
import org.jblooming.waf.constants.Fields;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.NumberFormat;
import java.util.Date;
import java.util.List;

/**
 * @author Pietro Polsinelli ppolsinelli@open-lab.com
 * @author Roberto Bicchierai rbicchierai@open-lab.com
 */
public class ObjectEditorAction {

  public void cmdFind(ObjectEditor objectEditor, PageState pageState) throws PersistenceException {

    final Operator logged = pageState.getLoggedOperator();

    QueryHelper qhelp = null;
    if (JSP.ex(objectEditor.query))
      qhelp = new QueryHelper(objectEditor.query);
    else
      qhelp = objectEditor.queryHelper;

    boolean recoveredFromSavedFilter = PersistentSearch.feedFromSavedSearch(pageState);

    /*String FLD_des = pageState.getEntry("name").stringValueNullIfEmpty();
    if (FLD_des != null) {
      qhelp.addQBEClause("role.name", "name", FLD_des, QueryHelper.TYPE_CHAR);
      somethingSearched = true;
    }*/

    int paramCount = 0;
    for (FieldFeature fieldFeature : objectEditor.displayFields.values()) {

      Class type = null;

      if (fieldFeature.blank != null) continue;

      if (fieldFeature.smartComboClass != null)
        type = fieldFeature.smartComboClass;
      else {
        Field field = ReflectionUtilities.getField(fieldFeature.propertyName, objectEditor.getMainObjectClass());
        type = field.getType();
      }
      List classes = ReflectionUtilities.getInheritedClasses(type);

      String value = null;
      String fieldName = null;
      if (fieldFeature.smartCombo != null)
        fieldName = fieldFeature.smartCombo.fieldName;
      else
        fieldName = fieldFeature.propertyName;

      value = pageState.getEntry(fieldName).stringValueNullIfEmpty();

      if (value != null || (type.equals(Boolean.class) || "boolean".equals(type.toString()))) {

        if (type.equals(String.class)) {


          qhelp.addQBEClause(objectEditor.mainHqlAlias + "." + fieldName, "param" + paramCount, value, QueryHelper.TYPE_CHAR);

        } else if (classes.contains(Date.class)) {
          qhelp.addQBEClause(objectEditor.mainHqlAlias + "." + fieldName, "param" + paramCount, value, QueryHelper.TYPE_DATE);

        } else if (type.equals(Boolean.class) || "boolean".equals(type.toString())) {
          if (Fields.TRUE.equals(value) || "on".equals(value))
            qhelp.addOQLClause(objectEditor.mainHqlAlias + "." + fieldFeature.propertyName + "=:param" + paramCount, "param" + paramCount, Boolean.TRUE);
          else if (Fields.FALSE.equals(value))
            qhelp.addOQLClause(objectEditor.mainHqlAlias + "." + fieldFeature.propertyName + "=:param" + paramCount, "param" + paramCount, Boolean.FALSE);


        } else if (classes.contains(PersistentFile.class)) {
          qhelp.addQBEClause(objectEditor.mainHqlAlias + "." + fieldName, "param" + paramCount, value, QueryHelper.TYPE_CHAR);

        } else if (classes.contains(Identifiable.class)) {
          Type t = HibernateUtilities.getIdType(type);
          if (t instanceof IntegerType || t instanceof LongType)
            qhelp.addOQLClause(objectEditor.mainHqlAlias + "." + fieldFeature.propertyName + ".id=:" + "param" + paramCount, "param" + paramCount, Integer.parseInt(value));
          else {
            qhelp.addOQLClause(objectEditor.mainHqlAlias + "." + fieldFeature.propertyName + ".id=:" + "param" + paramCount, "param" + paramCount, value);
          }

        } else if (type.equals(Integer.class)) {
          qhelp.addQBEClause(objectEditor.mainHqlAlias + "." + fieldName, "param" + paramCount, value, QueryHelper.TYPE_INT);

        } else if (type.equals(Serializable.class)) { // ADDED
          PersistentClass pc = HibernateUtilities.getClassMapping(objectEditor.getMainObjectClass());
          if (!fieldName.contains(".")) {
            Type propertyType = pc.getProperty(fieldName).getType();
            if (propertyType instanceof StringType) {
              qhelp.addQBEClause(objectEditor.mainHqlAlias + "." + fieldName, "param" + paramCount, value, QueryHelper.TYPE_CHAR);
            } else
              qhelp.addQBEClause(objectEditor.mainHqlAlias + "." + fieldName, "param" + paramCount, value, QueryHelper.TYPE_INT);
          } else {
            String topProperty = fieldName.substring(0, fieldName.indexOf('.'));
            Class chidClass = pc.getProperty(topProperty).getType().getReturnedClass();
            PersistentClass pcChild = HibernateUtilities.getClassMapping(chidClass);
            if (pcChild != null) {
              Type propertyType = pcChild.getProperty(fieldName.substring(fieldName.indexOf('.') + 1)).getType();
              if (propertyType instanceof StringType) {
                qhelp.addQBEClause(objectEditor.mainHqlAlias + "." + fieldName, "param" + paramCount, value, QueryHelper.TYPE_CHAR);
              } else
                qhelp.addQBEClause(objectEditor.mainHqlAlias + "." + fieldName, "param" + paramCount, value, QueryHelper.TYPE_INT);
            }
          }

        }


      }
      paramCount++;
    }


    ListHeader.orderAction(qhelp, "OBJEDLH" + objectEditor.title, pageState, objectEditor.defaultOrderBy);

    int pageNum = Paginator.getWantedPageNumber(pageState);
    int pageSize = Paginator.getWantedPageSize(pageState);
    Query query = qhelp.toHql().getQuery();
    HibernatePage hibernatePageInstance = HibernatePage.getHibernatePageInstance(query, pageNum, pageSize);

    pageState.setPage(hibernatePageInstance);

    PageSeed ps = new PageSeed(pageState.href);
    ps.setClientEntries(pageState.getClientEntries());
    ps.setMainObjectId(pageState.mainObjectId);

  }

  public void cmdAdd(ObjectEditor objectEditor, PageState pageState) {
    objectEditor.urlToInclude = ObjectEditor.editUrl;
    IdentifiableSupport identifiable = null;
    try {
      identifiable = (IdentifiableSupport) objectEditor.getMainObjectClass().newInstance();
      identifiable.setIdAsNew();
    } catch (Exception e) {
      throw new PlatformRuntimeException(e);
    }
    pageState.mainObject = identifiable;
    unmake(objectEditor, pageState);
    fillDefaultData(objectEditor, pageState);
  }

  public void unmake(ObjectEditor objectEditor, PageState pageState) {
    for (FieldFeature ff : objectEditor.displayFields.values()) {
      pageState.removeEntry(ff.fieldName);
      if (ff.smartCombo != null)
        pageState.removeEntry(ff.fieldName + SmartCombo.TEXT_FIELD_POSTFIX);

    }
    for (FieldFeature ff : objectEditor.editFields.values()) {
      pageState.removeEntry(ff.fieldName);
      if (ff.smartCombo != null)
        pageState.removeEntry(ff.fieldName + SmartCombo.TEXT_FIELD_POSTFIX);

    }

  }

  private void fillDefaultData(ObjectEditor objectEditor, PageState pageState) {
    for (FieldFeature ff : objectEditor.editFields.values()) {
      if (ff.initialValue != null)
        pageState.addClientEntry(ff.fieldName, ff.initialValue);
    }
  }

  public void cmdEdit(ObjectEditor objectEditor, PageState pageState) throws FindByPrimaryKeyException {

    objectEditor.urlToInclude = ObjectEditor.editUrl;
    Identifiable i = PersistenceHome.findByPrimaryKey(objectEditor.getMainObjectClass(), pageState.getMainObjectId());
    make(objectEditor, i, pageState);
  }

  public void cmdDeletePreview(ObjectEditor objectEditor, PageState pageState) throws PersistenceException {
    objectEditor.urlToInclude = ObjectEditor.editUrl;
    Identifiable i = PersistenceHome.findByPrimaryKey(objectEditor.getMainObjectClass(), pageState.getMainObjectId());
    make(objectEditor, i, pageState);
  }

  public void cmdDelete(ObjectEditor objectEditor, PageState pageState) throws PersistenceException {
    cmdEdit(objectEditor, pageState);
    DeleteHelper.cmdDelete((IdentifiableSupport) pageState.mainObject, pageState);
  }

  public void cmdSave(ObjectEditor objectEditor, PageState pageState) throws FindByPrimaryKeyException, StoreException, ActionException {

    objectEditor.urlToInclude = ObjectEditor.editUrl;
    Serializable id = pageState.getMainObjectId();
    IdentifiableSupport main = null;
    if (PersistenceHome.NEW_EMPTY_ID.equals(id)) {
      try {
        main = (IdentifiableSupport) objectEditor.getMainObjectClass().newInstance();
        main.setIdAsNew();
      } catch (Exception e) {
        throw new PlatformRuntimeException(e);
      }

    } else {
      main = (IdentifiableSupport) PersistenceHome.findByPrimaryKey(objectEditor.getMainObjectClass(), id);
    }

    pageState.mainObject = main;

    if (objectEditor.editFields == null || objectEditor.editFields.size() == 0)
      objectEditor.fillFieldFeatures(main);

    Exception pendingException = null;
    boolean store = true;
    for (FieldFeature fieldFeature : objectEditor.editFields.values()) {

      Field field = ReflectionUtilities.getField(fieldFeature.propertyName, objectEditor.getMainObjectClass());
      if (field == null)
        continue;

      field.setAccessible(true);
      String value = null;
      Class type = field.getType();

      String entryName = fieldFeature.fieldName;
      if (fieldFeature.smartComboClass != null) {
        type = fieldFeature.smartComboClass;
        entryName=fieldFeature.smartCombo.fieldName;
      }

      List classes = ReflectionUtilities.getInheritedClasses(type);
      ClientEntry entry = null;
      if (fieldFeature.required) {
        entry = pageState.getEntryAndSetRequired(entryName);
      } else {
        entry = pageState.getEntry(entryName);
      }
      value = entry.stringValue();
      if ((value == null || value.trim().length() == 0) && fieldFeature.required)
        store = false;

      Object fieldValue = null;
      try {
        fieldValue = ReflectionUtilities.getFieldValue(fieldFeature.propertyName, main);
      } catch (Exception e) {
        throw new PlatformRuntimeException(e);
      }

      // ------------------ Serializable --------------------------
      if (type.equals(Serializable.class)) {   //ADDED
        // check from persistence
        PersistentClass pc = HibernateUtilities.getClassMapping(main.getClass());
        //Property propertyId = pc.getIdentifierProperty();
        //if (field.getName().equals(propertyId.getName())) {
        if (pc.getIdentifier() instanceof SimpleValue) {
          try {

            SimpleValue sv = (SimpleValue) pc.getIdentifier();
            if (sv.getType() instanceof IntegerType) {
              ReflectionUtilities.setFieldValue(fieldFeature.propertyName, main, new Integer(value));
            } else if (sv.getType() instanceof LongType) {
              ReflectionUtilities.setFieldValue(fieldFeature.propertyName, main, new Long(value));
            } else if (sv.getType() instanceof StringType) {
              ReflectionUtilities.setFieldValue(fieldFeature.propertyName, main, value);
            } else if (sv.getType() instanceof DoubleType) {
              ReflectionUtilities.setFieldValue(fieldFeature.propertyName, main, new DoubleType().fromStringValue(value));
            }
          } catch (Throwable e) {
            throw new PlatformRuntimeException(e);
          }
          // }
        }

        // ------------------ STRING --------------------------
      } else if (type.equals(String.class)) {
        try {
          if (!objectEditor.convertToUpperCase)
            ReflectionUtilities.setFieldValue(fieldFeature.propertyName, main, value);
          else
            ReflectionUtilities.setFieldValue(fieldFeature.propertyName, main, value.toUpperCase());
        } catch (Throwable e) {
          throw new PlatformRuntimeException(e);
        }

        // ------------------ DATE --------------------------
      } else if (classes.contains(Date.class)) {
        try {
          Date date = entry.dateValue();
          ReflectionUtilities.setFieldValue(fieldFeature.propertyName, main, date);
        } catch (Throwable e) {
          throw new PlatformRuntimeException(e);
        }

        // ------------------ BOOLEAN --------------------------
      } else if ((type.equals(boolean.class))) { //if ("boolean".equals(type.getName())) {
        if (value != null)
          try {
            if(value.equals(Fields.TRUE))
             ReflectionUtilities.setFieldValue(fieldFeature.propertyName, main, Boolean.TRUE);
            else
             ReflectionUtilities.setFieldValue(fieldFeature.propertyName, main, Boolean.FALSE);
          } catch (Exception e) {
            throw new PlatformRuntimeException(e);
          }
        else
          try {
            ReflectionUtilities.setFieldValue(fieldFeature.propertyName, main, Boolean.FALSE);
          } catch (Exception e) {
            throw new PlatformRuntimeException(e);
          }

        // ------------------ SERIALIZED MAP --------------------------
      } else if (type.equals(SerializedMap.class) || type.equals(Serializable.class)) {
        try {
          ReflectionUtilities.setFieldValue(fieldFeature.propertyName, main, value);
        } catch (Exception e) {
          throw new PlatformRuntimeException(e);
        }

        // ------------------ SERIALIZED LIST --------------------------
      } else if (type.equals(SerializedList.class) || type.equals(Serializable.class)) {
        try {
          ReflectionUtilities.setFieldValue(fieldFeature.propertyName, main, value);
        } catch (Exception e) {
          throw new PlatformRuntimeException(e);
        }

        // ------------------ PersistentFile --------------------------
        // currently PersistentFile usage on object editor is deprecated
      } else if (classes.contains(PersistentFile.class)) {

        if (fieldValue == null) {

          PersistentFile persistentFile = new PersistentFile(0, null, PersistentFile.TYPE_FILESTORAGE_ABSOLUTE);
          persistentFile.fileDir = ApplicationState.getApplicationSetting(SystemConstants.FLD_REPOSITORY_URL);
          fieldValue = persistentFile;
        } else {
          try {
            //tWonderfully disgusting hack: as basic entity persister does not trap custom type extensions, we have to force setting it to null
            //DNMWAP
            ReflectionUtilities.setFieldValue(fieldFeature.propertyName, main, Uploader.save(main, null, entryName, pageState));
            HibernateFactory.checkPoint();
          } catch (Exception e) {
            throw new PlatformRuntimeException();
          }
        }

        try {
          ReflectionUtilities.setFieldValue(fieldFeature.propertyName, main, Uploader.save(main, (PersistentFile) fieldValue, entryName, pageState));

        } catch (ApplicationException e) {
          pendingException = e;
        } catch (PersistenceException e) {
          throw new PlatformRuntimeException(e);

        } catch (Exception e) {
          throw new PlatformRuntimeException(e);
        }

        // ------------------ IDENTIFIABLE --------------------------
      } else if (classes.contains(Identifiable.class)) {
        try {
          if (value != null) {
            Serializable lookId = value;
            Identifiable l = (Identifiable) PersistenceHome.findByPrimaryKey(type, lookId);

            if (classes.contains(PerformantNodeSupport.class) && l instanceof PerformantNodeSupport && "parent".equals(fieldFeature.propertyName)) {
              ((PerformantNodeSupport) main).setParentAndStore((PerformantNodeSupport) l);
            } else
              ReflectionUtilities.setFieldValue(fieldFeature.propertyName, main, l);
          } else
            ReflectionUtilities.setFieldValue(fieldFeature.propertyName, main, null);
        } catch (NumberFormatException e) {
          //reset the reference
          try {
            ReflectionUtilities.setFieldValue(fieldFeature.propertyName, main, null);
          } catch (Exception e1) {
            throw new PlatformRuntimeException(e);
          }
        } catch (Exception e) {
          throw new PlatformRuntimeException(e);
        }

        // ------------------ INT --------------------------
      } else if (type.equals(int.class) || (type.equals(Integer.class))) {
        try {
          ReflectionUtilities.setFieldValue(fieldFeature.propertyName, main, NumberFormat.getInstance().parse(value).intValue());
        } catch (NumberFormatException e) {
          entry.errorCode = FieldErrorConstants.ERR_FIELD_MUST_BE_NUMERIC;
          pendingException = e;
        } catch (Exception e) {
          throw new PlatformRuntimeException(e);
        }

        // ------------------ FLOAT --------------------------
      } else if (type.equals(float.class) || (type.equals(Float.class))) {
        try {
          ReflectionUtilities.setFieldValue(fieldFeature.propertyName, main, NumberFormat.getInstance().parse(value).floatValue());
        } catch (NumberFormatException e) {
          entry.errorCode = FieldErrorConstants.ERR_FIELD_MUST_BE_NUMERIC;
          pendingException = e;
        } catch (Exception e) {
          throw new PlatformRuntimeException(e);
        }

        // ------------------ DOUBLE --------------------------
      } else if (type.equals(double.class) || (type.equals(Double.class))) {
        try {
          ReflectionUtilities.setFieldValue(fieldFeature.propertyName, main, NumberFormat.getInstance().parse(value).doubleValue());
        } catch (NumberFormatException e) {
          entry.errorCode = FieldErrorConstants.ERR_FIELD_MUST_BE_NUMERIC;
          pendingException = e;
        } catch (Exception e) {
          throw new PlatformRuntimeException(e);
        }

        // ------------------ LONG --------------------------
      } else if (type.equals(long.class)) {
        try {
          ReflectionUtilities.setFieldValue(fieldFeature.propertyName, main, NumberFormat.getInstance().parse(value).longValue());
        } catch (NumberFormatException e) {
          entry.errorCode = FieldErrorConstants.ERR_FIELD_MUST_BE_NUMERIC;
          pendingException = e;
        } catch (Exception e) {
          throw new PlatformRuntimeException(e);
        }

        // ------------------ BOOLEAN ----------------------------
      } else if (type.equals(Boolean.class)) {
        try {
          boolean booleanVal = false;
          if (Fields.TRUE.equalsIgnoreCase(value))
            booleanVal = true;
          ReflectionUtilities.setFieldValue(fieldFeature.propertyName, main, booleanVal);
        } catch (Exception e) {
          throw new PlatformRuntimeException(e);
        }
				// ------------------ ENUM ----------------------------
			} else if (type.isEnum()) {
				try {
					ReflectionUtilities.setFieldValue(fieldFeature.propertyName, main, Enum.valueOf(type, value));
				} catch (IllegalArgumentException e) {
					entry.errorCode = FieldErrorConstants.ERR_NAME_USED;
					pendingException = e;
        } catch (Exception e) {
          throw new PlatformRuntimeException(e);
        }

        // ------------------ UNHANDLED --------------------------
      } else
        throw new ActionException("Unhandled type: " + type);
    }

    if (pendingException != null)
      throw new ActionException(pendingException);
    else {
      if (store)
        main.store();

      //is it a linked object ?
      try {
        int parent_id = pageState.getEntry("PARENT_ID").intValue();
        Class parent_class = Class.forName(pageState.getEntry("PARENT_CLASS").stringValue());
        String parent_property = pageState.getEntry("PARENT_PROPERTY").stringValue();
        Identifiable parent = PersistenceHome.findByPrimaryKey(parent_class, parent_id);
        ReflectionUtilities.setFieldValue(parent_property, parent, main);
      } catch (ParseException e) {

      } catch (Exception e) {
        throw new PlatformRuntimeException(e);
      }
    }
  }

  protected void make(ObjectEditor objectEditor, Identifiable i, PageState pageState) {

    pageState.mainObject = i;

    if (objectEditor.editFields == null || objectEditor.editFields.size() == 0)
      objectEditor.fillFieldFeatures(i);

    for (FieldFeature fieldFeature : objectEditor.editFields.values()) {
      Class mainObjectClass = objectEditor.getMainObjectClass();

      String propertyName = fieldFeature.propertyName;
      String fieldName = fieldFeature.fieldName;
      if (fieldFeature.smartCombo != null) {
        fieldName = fieldFeature.smartCombo.fieldName;
      }

      if (fieldFeature.blank == null) {

        ClientEntry ce = ReflectionUtilities.makeCe(fieldFeature.required, fieldName, propertyName, mainObjectClass, i);
        if (ce != null)
          pageState.addClientEntry(ce);
      }
    }
  }

  public void cmdDuplicate(ObjectEditor objectEditor, PageState pageState) throws FindByPrimaryKeyException {
    cmdEdit(objectEditor, pageState);
    ((IdentifiableSupport) (pageState.mainObject)).setIdAsNew();
    // what pearl!
    PersistenceContext.get((IdentifiableSupport) pageState.mainObject).session.evict(pageState.mainObject);
    pageState.mainObjectId = PersistenceHome.NEW_EMPTY_ID;

  }
}
