package org.jblooming.security;

import org.jblooming.persistence.PersistenceHome;
import org.jblooming.persistence.exceptions.FindByPrimaryKeyException;
import org.jblooming.persistence.exceptions.RemoveException;
import org.jblooming.persistence.exceptions.StoreException;


/**
 * @author Pietro Polsinelli, Roberto Bicchierai
 * @version 2 alpha
 * @since JDK 1.3.01 02
 */

public class RoleHome {


  protected static RoleHome sh = new RoleHome();

  public static RoleHome getInstance() {
    return sh;
  }

  private RoleHome() {
  }

  public Role create(int id) {
    Role r = new Role();
    r.setId(id);
    return r;
  }

  public void remove(Role r) throws RemoveException {
    PersistenceHome.remove(r);
  }

  public Role findByPrimaryKey(int roleId) throws FindByPrimaryKeyException {
    return (Role) PersistenceHome.findByPrimaryKey(Role.class, roleId);
  }


  /**
   * Method storeRole avoids storing two roles with same name,
   * without impeding update of
   * a stored role: trying to store different roles with same id and/or name
   * will fail
   *
   * @param role a  Role
   * @throws org.jblooming.persistence.exceptions.StoreException
   *
   */
  public void store(Role role) throws StoreException {
    role.store();
  }


}
