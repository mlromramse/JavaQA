package org.jblooming.operator.businessLogic;

import org.jblooming.ApplicationException;
import org.jblooming.PlatformRuntimeException;
import org.jblooming.anagraphicalData.AnagraphicalData;
import org.jblooming.messaging.MailHelper;
import org.jblooming.ontology.businessLogic.DeleteHelper;
import org.jblooming.operator.Operator;
import org.jblooming.oql.OqlQuery;
import org.jblooming.oql.QueryHelper;
import org.jblooming.page.HibernatePage;
import org.jblooming.persistence.PersistenceHome;
import org.jblooming.persistence.exceptions.PersistenceException;
import org.jblooming.security.*;
import org.jblooming.security.SecurityException;
import org.jblooming.system.SystemConstants;
import org.jblooming.waf.constants.*;
import org.jblooming.waf.exceptions.ActionException;
import org.jblooming.waf.html.display.Paginator;
import org.jblooming.waf.html.input.Selector;
import org.jblooming.waf.html.table.ListHeader;
import org.jblooming.waf.settings.ApplicationState;
import org.jblooming.waf.state.PersistentSearch;
import org.jblooming.waf.view.ClientEntries;
import org.jblooming.waf.view.ClientEntry;
import org.jblooming.waf.view.PageState;

import javax.servlet.http.HttpServletRequest;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Pietro Polsinelli : ppolsinelli@open-lab.com
 */
public class OperatorAction {

  public void cmdAdd(PageState pageState, Class operatorClass) throws org.jblooming.security.SecurityException, ApplicationException, PersistenceException {

    Operator operator;
    try {
      operator = (Operator) operatorClass.newInstance();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    operator.setIdAsNew();
    Operator logged = pageState.getLoggedOperator();
    OperatorHelper.SecuritySettings sst = OperatorHelper.getSecuritySettings(logged, operator);
    if (!sst.canWrite)
      throw new SecurityException(SecurityConstants.I18N_PERMISSION_LACKING, PlatformPermissions.operator_canWrite);

    make(operator, pageState);
    pageState.setMainObject(operator);

  }

  public void cmdEdit(PageState pageState, Class operatorClass) throws SecurityException, PersistenceException, ApplicationException {

    Operator operator = (Operator) PersistenceHome.findByPrimaryKey(operatorClass, pageState.getMainObjectId());

    if (operator.getAnagraphicalData() == null) {
      AnagraphicalData ad = new AnagraphicalData();
      ad.store();
      operator.setAnagraphicalData(ad);
    }
    make(operator, pageState);
    pageState.setMainObject(operator);
  }


  public void cmdFind(PageState pageState) throws  PersistenceException {

    Operator logged = pageState.getLoggedOperator();

    String hql = "select user from " + Operator.class.getName() + " as user order by user.name";

    QueryHelper qhelp = new QueryHelper(hql);

    boolean recoveredFromSavedFilter = PersistentSearch.feedFromSavedSearch(pageState);

    String filter = pageState.getEntry(Fields.FORM_PREFIX + "filter").stringValueNullIfEmpty();
    if (filter != null && filter.trim().length() > 0) {

      qhelp.addQBEORClauses(
              filter,
              qhelp.getOrElement("user.loginName", "loginName", QueryHelper.TYPE_CHAR),
              qhelp.getOrElement("user.name", "name", QueryHelper.TYPE_CHAR),
              qhelp.getOrElement("user.surname", "surname", QueryHelper.TYPE_CHAR)
              );
      
    }
    boolean showHidden = pageState.getEntry("SHOW_HIDDEN_OPERATOR").checkFieldValue();
    qhelp.addOQLClause("user.enabled=:showHidden","showHidden",!showHidden);


    ListHeader.orderAction(qhelp, "OPLH", pageState);
    pageState.setPage(
      HibernatePage.getHibernatePageInstance(qhelp.toHql().getQuery(),
        Paginator.getWantedPageNumber(pageState),
        Paginator.getWantedPageSize(pageState)));

  }

  public void cmdDelete(PageState pageState) throws SecurityException, PersistenceException {
    Operator logged = pageState.getLoggedOperator();
    Operator operator = (Operator) PersistenceHome.findByPrimaryKey(Operator.class, pageState.getMainObjectId());
    OperatorHelper.SecuritySettings sst = OperatorHelper.getSecuritySettings(logged, operator);
    if (!sst.canRead)
      throw new SecurityException(SecurityConstants.I18N_PERMISSION_LACKING, PlatformPermissions.operator_canWrite);
    pageState.setMainObject(operator);
    DeleteHelper.cmdDelete(operator, pageState);
  }

  public void cmdSaveAndLogin(PageState pageState, Class operatorClass, HttpServletRequest request) throws SecurityException, ActionException, PersistenceException, ApplicationException {
    cmdSave(pageState, operatorClass);
    pageState.getSessionState().setLoggedOperator((Operator) pageState.getMainObject());
  }

  public void cmdSave(PageState pageState, Class operatorClass) throws SecurityException, ActionException, PersistenceException, ApplicationException {

    boolean invalidClientEntries = false;

    Operator operator;
    AnagraphicalData ad;
    if (PersistenceHome.NEW_EMPTY_ID.equals(pageState.mainObjectId)) {
      try {
        operator = (Operator) operatorClass.newInstance();
        operator.setIdAsNew();
      } catch (InstantiationException e) {
        throw new PlatformRuntimeException(e);
      } catch (IllegalAccessException e) {
        throw new PlatformRuntimeException(e);
      }
      ad = new AnagraphicalData();
      operator.setAnagraphicalData(ad);
    } else
      operator = (Operator) PersistenceHome.findByPrimaryKey(operatorClass, pageState.getMainObjectId());

    Operator logged = pageState.getLoggedOperator();
    OperatorHelper.SecuritySettings sst = OperatorHelper.getSecuritySettings(logged, operator);
    if (!sst.canWrite)
      throw new SecurityException(SecurityConstants.I18N_PERMISSION_LACKING, PlatformPermissions.operator_canWrite);
    pageState.setMainObject(operator);

    try {
      operator.setName(pageState.getEntry(OperatorConstants.FLD_NAME).stringValue());
    } catch (ActionException e) {
      e.printStackTrace();
    }

    try {
      operator.setSurname(pageState.getEntry(OperatorConstants.FLD_SURNAME).stringValue());
    } catch (ActionException e) {
      invalidClientEntries = true;
    }

    try {
      final ClientEntry entry = pageState.getEntryAndSetRequired("LOGIN_NAME");
      final String loginName = entry.stringValue();
      if (!loginName.equals(operator.getLoginName())) {
        OqlQuery oq = new OqlQuery("from " + Operator.class.getName() + " user where user.loginName = :userLN");
        oq.getQuery().setString("userLN", loginName);
        List sameLogin = oq.list();
        if (sameLogin != null && sameLogin.size() > 0) {
          entry.errorCode = FieldErrorConstants.ERR_KEY_MUST_BE_UNIQUE;
          throw new ActionException();
        }
        operator.setLoginName(loginName);
      }
    } catch (ActionException e) {
      invalidClientEntries = true;
    }

    try {
      ClientEntry pswCe = pageState.getEntry("PWD");
      ClientEntry pswCeRT = pageState.getEntry("PWD_RETYPE");
      String psw = pswCe.stringValue();
      if (!psw.equals(OperatorConstants.PASSWORD_MASK)) {

        // test for password sufficient length
        final String minLen = ApplicationState.getApplicationSetting(SystemConstants.FLD_PASSWORD_MIN_LEN);
        int minLength = minLen != null ? Integer.parseInt(minLen) : 0;
        if (psw.length() < minLength) {
          pswCe.errorCode = "ERR_PASSWORD_TOO_SHORT";
          throw new ActionException();
        }

        // test for retyped password identical
        if (!psw.equals(pswCeRT.stringValue())) {
          pswCe.errorCode = "ERR_PASSWORD_MUST_BE_IDENTICAL";
          pswCeRT.setValue("");
          throw new ActionException();
        }
        operator.changePassword(psw);
      }
    } catch (ActionException e) {
      invalidClientEntries = true;
    }

    try {
      operator.setPersonalIdentificationAnswer(pageState.getEntry("USER_PIA").stringValue());
    } catch (ActionException e) {
      e.printStackTrace();
    }

    try {
      operator.setPersonalIdentificationQuestion(pageState.getEntry("USER_PIQ").stringValue());
    } catch (ActionException e) {
      e.printStackTrace();
    }

    if (operator.getAnagraphicalData() != null) {
      operator.getAnagraphicalData().setEmail(pageState.getEntry(AnagraphicalDataConstants.FLD_EMAIL).stringValueNullIfEmpty());
      operator.getAnagraphicalData().setAddress(pageState.getEntry(AnagraphicalDataConstants.FLD_ADDRESS).stringValueNullIfEmpty());
      operator.getAnagraphicalData().setCity(pageState.getEntry(AnagraphicalDataConstants.FLD_CITY).stringValueNullIfEmpty());
      operator.getAnagraphicalData().setFax(pageState.getEntry(AnagraphicalDataConstants.FLD_FAX).stringValueNullIfEmpty());
      operator.getAnagraphicalData().setMobile(pageState.getEntry(AnagraphicalDataConstants.FLD_MOBILE).stringValueNullIfEmpty());
      operator.getAnagraphicalData().setState(pageState.getEntry(AnagraphicalDataConstants.FLD_STATE).stringValueNullIfEmpty());
      operator.getAnagraphicalData().setTelephone(pageState.getEntry(AnagraphicalDataConstants.FLD_TELEPHONE).stringValueNullIfEmpty());
      operator.getAnagraphicalData().setZip(pageState.getEntry(AnagraphicalDataConstants.FLD_ZIP).stringValueNullIfEmpty());
      operator.getAnagraphicalData().setCountry(pageState.getEntry(AnagraphicalDataConstants.FLD_COUNTRY).stringValueNullIfEmpty());
    }

    ClientEntry adminCE = pageState.getEntry(OperatorConstants.FLD_ADMINISTRATOR);
    String admin = adminCE.stringValueNullIfEmpty();
    if (admin != null)
      operator.setAdministrator(adminCE.checkFieldValue());

    ClientEntry enabCE = pageState.getEntry(OperatorConstants.FLD_IS_ENABLED);
    String enab = enabCE.stringValueNullIfEmpty();
    if (enab != null)
      operator.setEnabled(enabCE.checkFieldValue());

    if (invalidClientEntries)
      throw new ActionException();

    //notice: operator must be stored before adding persistent relation objects
    if (operator.getAnagraphicalData() != null)
      operator.getAnagraphicalData().store();
    operator.store();

    {
      //remove unsel operator roles
      Set<String> selIds = Selector.chosen("roles", pageState).keySet();
      Iterator<OperatorRole> i = operator.getOperatorRolesIterator();
      while (i.hasNext()) {
        OperatorRole or = i.next();
        if (!selIds.contains(or.getRole().getId().toString()))
          operator.removeRoleAndPersist(or);

      }
      for (String roleId : selIds) {
        Role ro = (Role) PersistenceHome.findByPrimaryKey(Role.class, roleId);
        operator.addRoleAndPersist(ro);
      }
    }
    {
      //remove unsel operator groups
      Set<String> selGrpIds = Selector.chosen("direct_groups", pageState).keySet();
      Iterator<OperatorGroup> i = operator.getOperatorGroupsIterator();
      while (i.hasNext()) {
        OperatorGroup or = i.next();
        if (!selGrpIds.contains(or.getGroup().getId().toString()))
          operator.removeGroupAndPersist(or);
      }
      for (String grpId : selGrpIds) {
        Group gp = (Group) PersistenceHome.findByPrimaryKey(Group.class, grpId);
        operator.addGroupAndPersist(gp);
      }
    }
  }

  public void make(Operator user, PageState pageState) throws PersistenceException {

    boolean isNew = PersistenceHome.NEW_EMPTY_ID.equals(user.getId());

    ClientEntries ces = new ClientEntries();
    ces.addEntry(new ClientEntry(OperatorConstants.FLD_NAME, user.getName()));
    ces.addEntry(new ClientEntry(OperatorConstants.FLD_SURNAME, user.getSurname()));

    pageState.addClientEntry(OperatorConstants.FLD_IS_ENABLED,user.isEnabled());

    ces.addEntry(new ClientEntry("LOGIN_NAME", user.getLoginName()));
    ces.addEntry(new ClientEntry("FLD_ID", user.getId() + ""));
    ces.addEntry(new ClientEntry("USER_PIA", user.getPersonalIdentificationAnswer()));
    ces.addEntry(new ClientEntry("USER_PIQ", user.getPersonalIdentificationQuestion()));
    if (!isNew) {
      ces.addEntry(new ClientEntry("PWD", OperatorConstants.PASSWORD_MASK));
      ces.addEntry(new ClientEntry("PWD_RETYPE", OperatorConstants.PASSWORD_MASK));
      if (user.getLocation() != null)
        ces.addEntry(new ClientEntry("LOCATION", user.getLocation().getId() + ""));
    }

    if (user.getAnagraphicalData() == null)
      user.setAnagraphicalData(new AnagraphicalData());
    ces.addEntry(new ClientEntry(AnagraphicalDataConstants.FLD_EMAIL, user.getAnagraphicalData().getEmail()));
    ces.addEntry(new ClientEntry(AnagraphicalDataConstants.FLD_TELEPHONE, user.getAnagraphicalData().getTelephone()));
    ces.addEntry(new ClientEntry(AnagraphicalDataConstants.FLD_FAX, user.getAnagraphicalData().getFax()));
    ces.addEntry(new ClientEntry(AnagraphicalDataConstants.FLD_ADDRESS, user.getAnagraphicalData().getAddress()));
    ces.addEntry(new ClientEntry(AnagraphicalDataConstants.FLD_CITY, user.getAnagraphicalData().getCity()));
    ces.addEntry(new ClientEntry(AnagraphicalDataConstants.FLD_COUNTRY, user.getAnagraphicalData().getCountry()));
    ces.addEntry(new ClientEntry(AnagraphicalDataConstants.FLD_MOBILE, user.getAnagraphicalData().getMobile()));
    ces.addEntry(new ClientEntry(AnagraphicalDataConstants.FLD_STATE, user.getAnagraphicalData().getState()));
    ces.addEntry(new ClientEntry(AnagraphicalDataConstants.FLD_ZIP, user.getAnagraphicalData().getZip()));

    pageState.addClientEntry(OperatorConstants.FLD_ADMINISTRATOR,user.isAdministrator());

    pageState.addClientEntries(ces);

    //direct roles
    Iterator<OperatorRole> chosen = user.getOperatorRolesIterator();
    TreeMap<String, String> ctm = new TreeMap<String, String>();
    while (chosen.hasNext()) {
      Role role = chosen.next().getRole();
      ctm.put(role.getId().toString(), role.getName());
    }

    OqlQuery oqlQuery = new OqlQuery("from " + Role.class.getName() +" rl order by rl.description  ");
    // + " as rol where rol.class = :rc");
    //oqlQuery.getQuery().setParameter("rc", Role.class);
    List<Role> cand = (List<Role>) oqlQuery.list();
    TreeMap<String, String> candTm = new TreeMap<String, String>();
    if (cand != null && cand.size() > 0) {
      for (Role role : cand) {
        if (chosen == null || !ctm.keySet().contains(role.getId().toString())) {
          candTm.put(role.getId().toString(), role.getName());
        }
      }
    }
    Selector.make("roles", candTm, ctm, pageState);

    //direct groups
    Iterator<OperatorGroup> chosenGrp = user.getOperatorGroupsIterator();
    TreeMap<String, String> ctmGrp = new TreeMap<String, String>();
    while (chosenGrp.hasNext()) {
      OperatorGroup opgroup = chosenGrp.next();
      ctmGrp.put(opgroup.getGroup().getId().toString(), opgroup.getGroup().getName());
    }
    List<Group> candGrp = (List<Group>) new OqlQuery("from " + Group.class.getName()+" gp order by gp.description ").list();
    TreeMap<String, String> candTmGrp = new TreeMap<String, String>();
    if (candGrp != null && candGrp.size() > 0) {
      for (Group group : candGrp) {
        if (candGrp == null || !ctmGrp.keySet().contains(group.getId().toString()))
          candTmGrp.put(group.getId().toString(), group.getName());
      }
    }
    Selector.make("direct_groups", candTmGrp, ctmGrp, pageState);

  }

  public void cmdVerifyAccount(PageState pageState) throws ActionException, PersistenceException, ApplicationException {

    boolean accountIsOk = false;

    try {
      ClientEntry ceFullName = pageState.getEntry(OperatorConstants.FLD_SURNAME);
      OqlQuery oq = new OqlQuery("from " + Operator.class.getName() + " user where user.surname = :surname");
      oq.getQuery().setString("surname", ceFullName.stringValue());
      List usr = oq.list();
      if (usr != null && usr.size() > 0) {
        accountIsOk = true;
        pageState.setMainObjectId(((Operator) usr.get(0)).getId());
      }

    } catch (ActionException e) {
      e.printStackTrace();
    }

    if (!accountIsOk) {
      try {
        ClientEntry ceLoginName = pageState.getEntry(OperatorConstants.FLD_LOGIN_NAME);
        OqlQuery oq = new OqlQuery("from " + Operator.class.getName() + " user where user.loginName = :ln");
        oq.getQuery().setString("ln", ceLoginName.stringValue());
        List usr = oq.list();
        if (usr != null && usr.size() > 0) {
          accountIsOk = true;
          pageState.setMainObjectId(((Operator) usr.get(0)).getId());
        }
      } catch (ActionException e) {
        e.printStackTrace();
      }
    }

    if (!accountIsOk)
      throw new ActionException();

    Operator user = (Operator) PersistenceHome.findByPrimaryKey(Operator.class, pageState.getMainObjectId());
    //gen new psw
    //String newPsw = generatePassword(Math.max(6,Integer.parseInt(ApplicationStateImpl.getApplicationSetting(SystemConstants.FLD_PASSWORD_MIN_LEN))));
    //user.changePassword(newPsw);
    user.store();

    //send mail
    if (user.getAnagraphicalData().getEmail() != null && user.getAnagraphicalData().getEmail().trim().length() > 0)
      MailHelper.sendPwdMail(user, user.getLoginName(), pageState);

  }

}

