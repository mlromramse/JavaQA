package org.jblooming.ontology;

import org.jblooming.operator.Operator;
import org.jblooming.operator.User;
import org.jblooming.security.*;
import org.jblooming.security.SecurityException;
import org.jblooming.waf.constants.SecurityConstants;
import org.hibernate.annotations.ForeignKey;

import javax.persistence.*;
import java.util.Map;

/**
 * @author Pietro Polsinelli ppolsinelli@open-lab.com
 * @author Roberto Bicchierai rbicchierai@open-lab.com
 */

@MappedSuperclass
public abstract class SecuredNodeSupport extends PerformantNodeSupport implements Securable {

  private Operator owner;
  private boolean inherit = false;
  private boolean propagate = true;

  /**
   * default implementation; should be extended with the business logic
   */
   public boolean hasPermissionFor(User u, Permission p) {
    if (u==null)
      return false;
    if (getOwner() != null && getOwner().equals(u))
      return true;
    else if (u.hasPermissionFor(p))
      return true;
    else {
      SecuredNodeSupport parent = (SecuredNodeSupport) getParentNode();
      if (parent != null && (isInherit() || parent.isPropagate()))
        return parent.hasPermissionFor(u, p);
      else
        return false;
    }
  }

  public void testPermission(User u, Permission p) throws org.jblooming.security.SecurityException {
    if (!hasPermissionFor(u, p))
      throw new SecurityException(SecurityConstants.I18N_PERMISSION_LACKING,p);
  }

  @Transient
  public Operator getOwner() {
    return owner;
  }

  public void setOwner(Operator owner) {
    this.owner = owner;
  }

  public boolean isPropagate() {
    return propagate;
  }

  public void setPropagate(boolean propagate) {
    this.propagate = propagate;
  }

  public boolean isInherit() {
    return inherit;
  }

  public void setInherit(boolean inherit) {
    this.inherit = inherit;
  }


}
