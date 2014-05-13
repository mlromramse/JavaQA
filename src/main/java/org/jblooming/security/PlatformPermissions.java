package org.jblooming.security;

/**
 * @author Pietro Polsinelli ppolsinelli@open-lab.com
 * @author Roberto Bicchierai rbicchierai@open-lab.com
 */
public class PlatformPermissions extends Permissions {

  public static final String BASE = "PL_";

  public static final Permission role_canRead = new Permission(BASE + "role_canRead");
  public static final Permission role_canWrite = new Permission(BASE + "role_canWrite");
  public static final Permission role_canCreate = new Permission(BASE + "role_canCreate");

  public static final Permission affiliate_canCreate = new Permission(BASE + "affiliate_canCreate");

  public static final Permission area_canManage = new Permission(BASE + "area_canManage");

  public static final Permission operator_canWrite = new Permission(BASE + "operator_canWrite");
  public static final Permission operator_canRead = new Permission(BASE + "operator_canRead");
  public static final Permission operator_canCreate = new Permission(BASE + "operator_canCreate");

  public static final Permission i18n_manage = new Permission(BASE + "i18n_manage");
  public static final Permission help_manage = new Permission(BASE + "help_manage");
  public static final Permission location_manage = new Permission(BASE + "location_manage");

  public final static Permission object_editor = new Permission(BASE + "object_editor");

  public final static Permission schedule_manage = new Permission(BASE + "schedule_manage");

  public static final Permission group_canWrite = new Permission(BASE + "group_canWrite");
  public static final Permission group_canRead = new Permission(BASE + "group_canRead");
  public static final Permission group_canCreate = new Permission(BASE + "group_canCreate");

  public static final Permission dept_canWrite = new Permission(BASE + "dept_canWrite");
  public static final Permission dept_canRead = new Permission(BASE + "dept_canRead");
  public static final Permission dept_canCreate = new Permission(BASE + "dept_canCreate");


  public static final Permission fileStorage_canRead = new Permission(BASE + "fileStorage_canRead");
  public static final Permission fileStorage_canWrite = new Permission(BASE + "fileStorage_canWrite");
  public static final Permission fileStorage_canCreate = new Permission(BASE + "fileStorage_canCreate");

  public static final Permission document_canRead = new Permission(BASE + "document_canRead");
  public static final Permission document_canWrite = new Permission(BASE + "document_canWrite");
  public static final Permission document_canCreate = new Permission(BASE + "document_canCreate");

  public static final Permission reservableService_canRead = new Permission(BASE + "reservableService_canRead");
  public static final Permission reservableService_canWrite = new Permission(BASE + "reservableService_canWrite");
  public static final Permission reservableService_canCreate = new Permission(BASE + "reservableService_canCreate");

  public static final Permission postill_manage = new Permission(BASE + "postill_manage");
  public static final Permission postill_user = new Permission(BASE + "postill_user");
  public static final Permission message_manage = new Permission(BASE + "message_manage");
  public static final Permission message_user = new Permission(BASE + "message_user");

  public static final Permission doc_canRead = new Permission(BASE + "doc_canRead");


  public static final String ORG = "ORG_";

}
