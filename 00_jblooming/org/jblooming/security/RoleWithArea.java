package org.jblooming.security;

import org.jblooming.operator.User;
import org.jblooming.operator.Operator;
import org.jblooming.ontology.SecuredNodeWithAreaSupport;
import org.jblooming.waf.constants.SecurityConstants;

/**
 * @author Pietro Polsinelli ppolsinelli@open-lab.com
 * @author Roberto Bicchierai rbicchierai@open-lab.com
 */
public class RoleWithArea extends Role implements SecurableWithArea {

  private Area area;

  private Operator owner;

  public Operator getOwner() {
    return owner;
  }

  public void setOwner(Operator owner) {
    this.owner = owner;
  }

  public Area getArea() {
    return area;
  }

  public void setArea(Area area) {
    this.area = area;
  }

  public void testPermission(User u, Permission p) throws org.jblooming.security.SecurityException {
    if (!hasPermissionFor(u, p))
      throw new SecurityException(SecurityConstants.I18N_PERMISSION_LACKING, p);
  }

  public boolean hasPermissionFor(User u, Permission p) {

    if (getOwner() != null && getOwner().equals(u))
      return true;

    if (getArea() == null)
      return u.hasPermissionAsAdmin();

    return getArea().hasPermissionFor(u, p);

  }


}
