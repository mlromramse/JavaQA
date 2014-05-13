package org.hibernate.dialect;
import org.hibernate.dialect.Oracle10gDialect;

import java.sql.Types;

/**
 * Created by IntelliJ IDEA.
 * User: sChelazzi
 * Date: Jul 20, 2011
 * Time: 9:59:54 AM
 * To change this template use File | Settings | File Templates.
 */
public class Oracle10gDialectDBText extends Oracle10gDialect{

  public Oracle10gDialectDBText() {
     super();
      registerColumnType(Types.LONGVARCHAR, "clob");
    
   }


}
