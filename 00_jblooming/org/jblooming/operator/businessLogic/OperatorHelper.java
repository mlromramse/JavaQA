package org.jblooming.operator.businessLogic;

import org.jblooming.operator.Operator;
import org.jblooming.operator.User;
import org.jblooming.persistence.PersistenceHome;
import org.jblooming.security.Area;
import org.jblooming.security.PlatformPermissions;

/**
 * @author Pietro Polsinelli : ppolsinelli@open-lab.com
 */
public class OperatorHelper {

  private OperatorHelper.SecuritySettings getSecuritySettingsInstance(Operator loggedUser, Operator operator) {

    OperatorHelper.SecuritySettings sst = new OperatorHelper.SecuritySettings();
    sst.loggedUser = loggedUser;
    sst.operator = operator;

    final Area area = null;

    sst.area = area;
    sst.isAdmin = loggedUser != null && loggedUser.hasPermissionAsAdmin();
    sst.isNew = operator==null || operator.isNew();
    sst.editingMyself = !sst.isNew && ((operator != null) && operator.getId().equals(loggedUser.getId()));
    boolean opW = loggedUser.hasPermissionFor(PlatformPermissions.operator_canWrite);
    sst.canEditRootData = sst.isNew || sst.editingMyself || opW;

    sst.canEditSecurityArea = (sst.area != null && sst.area.getOwner() != null && sst.area.getOwner().equals(loggedUser)) || (sst.area == null && sst.isAdmin);
    sst.canEditLoginName = sst.editingMyself || opW; //sst.isNew;

    sst.canRead = (
            (sst.isNew && opW) ||
            (!sst.isNew && (sst.editingMyself || opW))
            );

    sst.canWrite = (
            (sst.isNew && opW) ||
            (!sst.isNew && (sst.editingMyself || opW))
            );


    return sst;
  }

  public static OperatorHelper.SecuritySettings getSecuritySettings(Operator loggedUser, Operator operator) {
    OperatorHelper oh = new OperatorHelper();
    return oh.getSecuritySettingsInstance(loggedUser, operator);
  }

  public class SecuritySettings {

    public User loggedUser;
    public User operator;
    public Area area;
    public boolean isAdmin;
    public boolean isNew;
    public boolean canRead;
    public boolean canWrite;
    public boolean editingMyself;
    public boolean canEditRootData;
    public boolean canEditSecurityArea;
    public boolean canEditLoginName;


    public String toString() {

      final String name = (area != null ? area.getName() : " is null");
      return "isAdmin " + isAdmin + "<br>" +
              "isNew " + isNew + "<br>" +
              "area " + name + "<br>" +
              "canRead " + canRead + "<br>" +
              "canWrite " + canWrite + "<br>" +
              "editingMyself " + editingMyself + "<br>" +
              "canEditRootData " + canEditRootData + "<br>" +
        "canEditSecurityArea " + canEditSecurityArea + "<br>" +
              "canEditLoginName " + canEditLoginName + "<br>";
    }
  }

}
