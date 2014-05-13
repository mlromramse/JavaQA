package org.jblooming.ontology;

import org.hibernate.usertype.UserType;
import org.hibernate.HibernateException;

import java.sql.Types;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.io.Serializable;

import net.sf.json.JSONObject;

public class JSONObjectType implements UserType {

  private static final int[] SQL_TYPES = {Types.CLOB};


  public int[] sqlTypes() {
    return JSONObjectType.SQL_TYPES;
  }

  public Class returnedClass() {
    return JSONObject.class;
  }

  public boolean equals(Object x, Object y) throws HibernateException {
    if (x == y) return true;
    if (x == null || y == null) return false;
    return x.equals(y);
  }

  public int hashCode(Object object) throws HibernateException {
    int ret=0;
    if (object!=null)
      ret=object.hashCode();
    return ret;
  }

  public Object nullSafeGet(ResultSet resultSet, String[] names, Object owner) throws HibernateException, SQLException {
    JSONObject result = null;
    //if (resultSet.wasNull()) return null;
    if (resultSet != null) {
      String serObj = resultSet.getString(names[0]);
      if (!resultSet.wasNull() && serObj != null)
        result = JSONObject.fromObject(serObj);
    }
    return result;
  }

  public void nullSafeSet(PreparedStatement statement, Object value, int index) throws HibernateException, SQLException {
    if (value == null) {
      statement.setNull(index, Types.CLOB);
    } else {
      JSONObject jsonObject = (JSONObject) value;
      statement.setString(index, jsonObject.toString());
    }
  }

  public Object deepCopy(Object o) throws HibernateException {
    if (o!=null)
      return JSONObject.fromObject(((JSONObject)o).toString());
    else
    return o;
  }

  public boolean isMutable() {
    return false;
  }

  public Serializable disassemble(Object object) throws HibernateException {
    return null;
  }

  public Object assemble(Serializable serializable,Object object) throws HibernateException {
    return null;
  }

  public Object replace(Object object,Object object1,Object object2) throws HibernateException {
    return null;
  }

}