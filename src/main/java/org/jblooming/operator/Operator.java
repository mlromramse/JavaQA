package org.jblooming.operator;


import org.jblooming.ApplicationException;
import org.jblooming.PlatformRuntimeException;
import org.jblooming.anagraphicalData.AnagraphicalData;
import org.jblooming.company.Location;
import org.jblooming.logging.Auditable;
import org.jblooming.logging.Inaudited;
import org.jblooming.oql.OqlQuery;
import org.jblooming.persistence.PersistenceHome;
import org.jblooming.persistence.exceptions.*;
import org.jblooming.security.*;
import org.jblooming.security.SecurityException;
import org.jblooming.system.SystemConstants;
import org.jblooming.utilities.DateUtilities;
import org.jblooming.utilities.HashTable;
import org.jblooming.utilities.JSP;
import org.jblooming.waf.constants.Fields;
import org.jblooming.waf.constants.OperatorConstants;
import org.jblooming.waf.constants.SecurityConstants;
import org.jblooming.waf.settings.ApplicationState;
import org.jblooming.waf.settings.I18n;
import org.jblooming.waf.settings.PlatformConfiguration;

import javax.persistence.Transient;
import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class Operator extends User implements OperatorAggregator, Auditable {

  @Inaudited
  private Date lastLoggedOn;
  private AnagraphicalData anagraphicalData;
  private Map<String, String> options = new TreeMap<String, String>();
  private Map<String, String> filters = new HashTable<String, String>();
  private Map<String, String> favoriteUrls = new HashTable();

  private Operator owner;

  private Location location;

  public static final String OPERATOR = "OP";


  // not persistent properties  BEGIN
  private Date lastRequestOn;
  String dateFormat=null;
  // not persistent properties  END



  public Operator() {
  }

  public String getFilter(String key) {
    return getFilters().get(key);
  }

  public void setAnagraphicalData(AnagraphicalData anagraphicalData) {
    this.anagraphicalData = anagraphicalData;
  }

  public AnagraphicalData getAnagraphicalData() {
    return anagraphicalData;
  }

  public Map<String, String> getOptions() {
    return options;
  }

  private void setOptions(Map options) {
    this.options = options;
  }


  public String getOption(String key) {
    return (String) getOptions().get(key);
  }

  public void putOption(String key, String value) {
    getOptions().put(key, value);
  }

  public String getOptionOrDefault(String name){
    boolean found = false;
    String result = null;
    Object o = getOption(name);
    if (o != null) {
      result = (String) o;
      found = true;
    }
    if (!found)
      result = ApplicationState.getApplicationSetting(name);

    return result;
  }

  /**
   *
   * @param op  op can be null; if null option is get from ApplicationState
   * @param name
   * @return
   */
  public static String getOperatorOption(Operator op, String name) {
    String result = null;
    if (op != null)
      result=op.getOptionOrDefault(name);
    else
      result = ApplicationState.getApplicationSetting(name);

    return result;
  }


  public Collection getOperators() {
    Collection coll = new HashSet();
    coll.add(this);
    return coll;
  }

  public boolean isOperatorIn(Operator o) {
    return (this.equals(o));
  }

  public Operator getOwner() {
    return owner;
  }

  public void setOwner(Operator operator) {
    this.owner = operator;
  }

  public void addRoleAndPersist(Role role) throws StoreException {

    //check whether existing
    Iterator<OperatorRole> i = getOperatorRolesIterator();
    boolean exists = false;
    while (i.hasNext()) {
      OperatorRole or = i.next();
      if (role.equals(or.getRole())) {
        exists = true;
        break;
      }
    }
    if (!exists) {
      OperatorRole or = new OperatorRole();
      or.setOperator(this);
      or.setRole(role);
      or.store();
    }
  }

  public void addGroupAndPersist(Group group) throws StoreException {


    //check whether existing
    Iterator<OperatorGroup> i = getOperatorGroupsIterator();
    boolean exists = false;
    while (i.hasNext()) {
      OperatorGroup og = i.next();
      if (group.equals(og.getGroup())) {
        exists = true;
        break;
      }
    }
    if (!exists) {
      OperatorGroup or = new OperatorGroup();
      or.setOperator(this);
      or.setGroup(group);
      or.store();
    }
  }

  /**
   * if u is owner, is always true, else calls super
   */
  public boolean hasPermissionFor(User u, Permission p) {
    if (getOwner() != null && getOwner().equals(u))
      return true;
    else
      return u.hasPermissionFor(p);
  }

  public void testPermission(User u, Permission p) throws SecurityException {
    if (!hasPermissionFor(u, p))
      throw new SecurityException(SecurityConstants.I18N_PERMISSION_LACKING, p);
  }

  public void removeRoleAndPersist(OperatorRole or) throws RemoveException {
    or.remove();
  }

  public void removeRoleAndPersist(Role role) throws RemoveException {
    for (Iterator<OperatorRole> i = getOperatorRolesIterator(); i.hasNext();) {
      OperatorRole or = i.next();
      if (or.getRole().equals(role)) {
        removeRoleAndPersist(or);
        for (Iterator<OperatorRole> it = or.getRole().getOperatorsIterator(); it.hasNext();) {
          OperatorRole operRol = it.next();
          if (operRol.equals(or)) {
            it.remove();
            break;
          }
        }
        break;
      }
    }
  }

  public void removeGroupAndPersist(OperatorGroup or) throws RemoveException {
    or.remove();
  }

  public Location getLocation() {
    return location;
  }

  public void setLocation(Location location) {
    this.location = location;
  }

  public Map<String,String> getFilters() {
    return filters;
  }

  private void setFilters(Map filters) {
    this.filters = filters;
  }

  public Map<String, String> getFavoriteUrls() {
    return favoriteUrls;
  }

  public void setFavoriteUrls(Map<String, String> favoriteUrls) {
    this.favoriteUrls = favoriteUrls;
  }

  public Date getLastLoggedOn() {
    return lastLoggedOn;
  }

  public void setLastLoggedOn(Date lastLoggedOn) {
    this.lastLoggedOn = lastLoggedOn;
  }

  public String getDisplayName() {
    return JSP.w(getName()) + " " + JSP.w(getSurname());
  }

  public String getDefaultEmail() {
    if (getAnagraphicalData() != null)
      return getAnagraphicalData().getEmail();
    else
      return null;
  }


  /**
   * only place where case insensitiveness always holds
   */
  public static Operator findByLoginName(String name) throws PersistenceException {
    Operator gr = null;

    OqlQuery oql = new OqlQuery("from " + PlatformConfiguration.defaultOperatorSubclass.getName() + " as op " +
            "where lower(op.loginName) = :name");
    oql.getQuery().setString("name", (JSP.w(name)).toLowerCase());
    gr = (Operator) oql.uniqueResult();

    return gr;
  }

  public static Operator createSystemOperator() throws StoreException, ApplicationException {

    Operator operator = new Operator();
    operator.setId(OperatorConstants.SYSTEM_OPERATOR_ID);
    operator.setLoginName(OperatorConstants.SYSTEM_OPERATOR);
    operator.changePassword("");
    operator.setAdministrator(true);
    operator.setEnabled(true);
    operator.setSurname(OperatorConstants.SYSTEM_OPERATOR);
    operator.store();
    return operator;
  }

  public static Operator getSystemOperator() {
    Operator op = null;
    try {
      op = (Operator) PersistenceHome.findByPrimaryKey(Operator.class, OperatorConstants.SYSTEM_OPERATOR_ID);
    } catch (Throwable e) {
      try {
        op = createSystemOperator();
      } catch (StoreException e1) {
        throw new PlatformRuntimeException(e);
      } catch (ApplicationException ae) {
        throw new PlatformRuntimeException(e);
      }
    }
    return op;
  }

  public static Operator authenticateUser(String password, String username, boolean enabledCookies) throws PersistenceException, ApplicationException, org.jblooming.security.SecurityException {

    Operator user = null;
    try {
      user = findByLoginName(username);
      if (user == null)
        throw new FindException();
      if (!user.isEnabled()) {
        throw new org.jblooming.security.SecurityException("ERR_OPERATOR_DISABLED");
      } else if (user.getEnabledOnlyOn() != null && (!user.getEnabledOnlyOn().contains(new Date())))
        throw new SecurityException("ERR_OPERATOR_OUTSIDE_TIME_WINDOW");
    } catch (FindByPrimaryKeyException e) {
      throw new SecurityException("ERR_INVALID_LOGIN");
    } /*catch (FindException e) {
      throw new SecurityException("ERR_INVALID_LOGIN");
    } */

    try {
      boolean pwdNull = user.getPassword() != null;
      boolean allowEmpty = !Fields.FALSE.equals(ApplicationState.getApplicationSetting(SystemConstants.ALLOW_EMPTY_STRING_PSW));
      boolean userPwdEmpty = user.getPassword()==null || user.getPassword().trim().equals("") ;
      boolean pwdEmpty = password==null || password.trim().equals("") ;
      boolean md5 = user.computePassword(password).equalsIgnoreCase(user.getPassword());

      if (  pwdNull &&
          //authentication methods:
          //allowing empty string psw
           ! ( 
               ( allowEmpty && userPwdEmpty && pwdEmpty ) ||
                   ( md5 )
           )
         ) {
        if (!md5)

        throw new SecurityException("ERR_INVALID_LOGIN");
      }

    } catch (NoSuchAlgorithmException e) {
      throw new ApplicationException(e);
    }
    return user;
  }

  public Date getLastRequestOn() {
    return lastRequestOn;
  }

  public void setLastRequestOn(Date lastRequestOn) {
    this.lastRequestOn = lastRequestOn;
  }

  @Transient
  public String getLanguage(){
    String ret = getOptionOrDefault(OperatorConstants.FLD_SELECT_LANG);
    return ret;
  }

  public void setLanguage(String language){
    if (JSP.ex(language))
      putOption(OperatorConstants.FLD_SELECT_LANG,language);
    else
      getOptions().remove(OperatorConstants.FLD_SELECT_LANG);
  }

  @Transient
  public Locale getLocale(){
    return I18n.getLocale(getLanguage());
  }
  
  @Transient
  public String getDateFormat(){
    if (dateFormat==null)
      dateFormat=DateUtilities.getLocalizedDateFormats(getLocale())[DateUtilities.DATE_DEFAULT];
    return dateFormat;
  }


  @Transient
  public static Operator load(Serializable id) throws FindByPrimaryKeyException {
    return (Operator) PersistenceHome.findByPrimaryKey(Operator.class, id);
  }

}