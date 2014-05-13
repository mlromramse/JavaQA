package org.jblooming.persistence.hibernate.oracle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.exception.JDBCExceptionHelper;
import org.hibernate.id.Configurable;
import org.hibernate.id.PersistentIdentifierGenerator;
import org.hibernate.id.IdentifierGeneratorHelper;
import org.hibernate.id.factory.IdentifierGeneratorFactory;
import org.hibernate.id.factory.DefaultIdentifierGeneratorFactory;
import org.hibernate.mapping.Table;
import org.hibernate.type.Type;
import org.hibernate.util.PropertiesHelper;
import org.jblooming.utilities.ReflectionUtilities;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import static java.util.Arrays.asList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

/**
 * @author Pietro Polsinelli ppolsinelli@open-lab.com
 * @author Roberto Bicchierai rbicchierai@open-lab.com
 */
public class PlatformSequenceGenerator implements PersistentIdentifierGenerator, Configurable {

  /**
   * The sequence parameter
   */
  public static final String SEQUENCE = "sequence";

  /**
   * The parameters parameter, appended to the create sequence DDL.
   * For example (Oracle): <tt>INCREMENT BY 1 START WITH 1 MAXVALUE 100 NOCACHE</tt>.
   */
  public static final String PARAMETERS = "parameters";

  private String sequenceName;
  private String parameters;
  private Type identifierType;
  private String sql;

  private static Collection<Sequence> sequences;


  private static final Log log = LogFactory.getLog(PlatformSequenceGenerator.class);

  public void configure(Type type, Properties params, Dialect dialect) throws MappingException {

    this.sequenceName = PropertiesHelper.getString(SEQUENCE, params, "hibernate_sequence");
    this.parameters = params.getProperty(PARAMETERS);
    String schemaName = params.getProperty(SCHEMA);
    String catalogName = params.getProperty(CATALOG);

    if (sequenceName.indexOf(".") < 0) {
      sequenceName = Table.qualify(catalogName, schemaName, sequenceName);
    }

    this.identifierType = type;
    sql = dialect.getSequenceNextValString(sequenceName);
  }

  public static void inject(Collection<Sequence> sequences, Dialect dialect) {
    PlatformSequenceGenerator.sequences = sequences;
    for (Sequence sequence : sequences) {
      sequence.sql = dialect.getSequenceNextValString(sequence.name);
    }
  }

  public Serializable generate(SessionImplementor session, Object obj) throws HibernateException {

    PreparedStatement st = null;
    try {

      boolean found = false;
      if (sequences != null)
        for (Sequence sequence : sequences) {
          if (ReflectionUtilities.instanceOfPersistent(obj, sequence.superClass)) {
            st = session.getBatcher().prepareSelectStatement(sequence.sql);
            found = true;
            break;
          }
        }
      if (!found)
        st = session.getBatcher().prepareSelectStatement(sql);

      try {
        ResultSet rs = st.executeQuery();
        final Serializable result;
        try {
          rs.next();
          result = IdentifierGeneratorHelper.get(rs, identifierType);
        }
        finally {
          rs.close();
        }
        if (log.isDebugEnabled())
          log.debug("Sequence identifier generated: " + result);
        return result;
      }
      finally {
        session.getBatcher().closeStatement(st);
      }

    }
    catch (SQLException sqle) {
      throw JDBCExceptionHelper.convert(
        session.getFactory().getSQLExceptionConverter(),
        sqle,
        "could not get next sequence value",
        sql
      );
    }
  }

  public String[] sqlCreateStrings(Dialect dialect) throws HibernateException {

    String[] mainDdl = dialect.getCreateSequenceStrings(sequenceName);
    if (parameters != null) mainDdl[mainDdl.length - 1] += ' ' + parameters;

    List<String> ddlSimple = new ArrayList(asList(mainDdl));

    if (sequences != null)
      for (Sequence sequence : sequences) {
        String[] tmpDdl = dialect.getCreateSequenceStrings(sequence.name);
        if (sequence.parameters != null) tmpDdl[tmpDdl.length - 1] += ' ' + sequence.parameters;
        List<String> tmp = asList(tmpDdl);
        ddlSimple.addAll(tmp);
      }

    int i = 0;
    String[] ddl = new String[ddlSimple.size()];
    for (String sn : ddlSimple) {
      ddl[i] = sn;
      i++;
    }
    return ddl;
  }

  public String[] sqlDropStrings(Dialect dialect) throws HibernateException {
    return dialect.getDropSequenceStrings(sequenceName);
  }

  public Object generatorKey() {
    return sequenceName;
  }


}
