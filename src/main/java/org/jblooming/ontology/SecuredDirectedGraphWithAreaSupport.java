package org.jblooming.ontology;

import org.jblooming.operator.User;
import org.jblooming.security.Area;
import org.jblooming.security.Permission;

import javax.persistence.MappedSuperclass;

/**
 * @author Ilaria Di gaeta idigaeta@open-lab.com
 */
@MappedSuperclass
public abstract class SecuredDirectedGraphWithAreaSupport extends SecuredDirectedGraphSupport {
  protected Area area;

  public Area getArea() {
    return area;
  }

  public void setArea(Area area) {
    this.area = area;
  }

  public boolean hasPermissionFor(User u, Permission p) {

    if (getOwner() != null && getOwner().equals(u))
      return true;

    boolean result = false;

    if (getArea() == null)
      result = u.hasPermissionAsAdmin();
    else
      result = area.hasPermissionFor(u, p);

    return result;

  }
}
