package org.hibernate.dialect;

public class MySQL5InnoDBUTF8Dialect extends MySQL5InnoDBDialect {

  // Create all tables as default UTF8!
  @Override
  public String getTableTypeString() {
    return " ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE utf8_general_ci";
  }
}
