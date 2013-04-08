package org.jblooming.security.businessLogic;

import org.jblooming.ApplicationException;
import org.jblooming.PlatformRuntimeException;
import org.jblooming.ldap.LdapUtilities;
import org.jblooming.scheduler.Scheduler;
import org.jblooming.tracer.Tracer;
import org.jblooming.agenda.CompanyCalendar;
import org.jblooming.operator.Operator;
import org.jblooming.persistence.PersistenceHome;
import org.jblooming.persistence.exceptions.PersistenceException;
import org.jblooming.persistence.exceptions.FindException;
import org.jblooming.system.SystemConstants;
import org.jblooming.utilities.JSP;
import org.jblooming.waf.SessionState;
import org.jblooming.waf.constants.Commands;
import org.jblooming.waf.constants.OperatorConstants;
import org.jblooming.waf.constants.Fields;
import org.jblooming.waf.exceptions.ActionException;
import org.jblooming.waf.html.state.ScreenElementStatus;
import org.jblooming.waf.settings.ApplicationState;
import org.jblooming.waf.settings.PlatformConfiguration;
import org.jblooming.waf.settings.I18n;
import org.jblooming.waf.view.ClientEntry;
import org.jblooming.waf.view.PageState;
import org.jblooming.waf.view.ClientEntries;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.Date;

/**
 * @author Pietro Polsinelli ppolsinelli@open-lab.com
 */
public class LoginAction {

  public void login(PageState pageState, HttpServletRequest request, HttpServletResponse response) throws PersistenceException, ApplicationException {

    if (pageState.getCommand() != null && pageState.getCommand().equals(Commands.LOGOUT)) {
      pageState.setClientEntries(new ClientEntries());
      return;
    }

    Operator user = null;

    String password = null;

    String auth_type = ApplicationState.getApplicationSetting(SystemConstants.AUTHENTICATION_TYPE);


    // ----------------------------------------------------- LDAP_AUTHENTICATION ----------------------------------------------------------------------------
    if (SystemConstants.ENABLE_AUTHENTICATION_TYPE.ENABLE_LDAP_AUTHENTICATION.toString().equals(auth_type)) {
      user = ldapAuthentication(pageState);

      // ----------------------------------------------------- LDAP_AUTHENTICATION_WITH_STANDARD_FALLBACK ----------------------------------------------------------------------------
    } else if (SystemConstants.ENABLE_AUTHENTICATION_TYPE.ENABLE_LDAP_AUTHENTICATION_WITH_FALLBACK_ON_STANDARD.toString().equals(auth_type)) {
      user = ldapAuthentication(pageState);
      if (user == null){
        pageState.getEntry(OperatorConstants.FLD_LOGIN_NAME).errorCode =null;
        pageState.getEntry(OperatorConstants.FLD_LOGIN_NEW_PWD).errorCode =null;
        user = standardAuthentication(pageState);
      }

      // ----------------------------------------------------- HTTP_AUTHENTICATION ----------------------------------------------------------------------------
    } else if (SystemConstants.ENABLE_AUTHENTICATION_TYPE.ENABLE_HTTP_AUTHENTICATION.toString().equals(auth_type)) {
      user = httpAuthentication(pageState, request);

      // ----------------------------------------------------- STANDARD_AUTHENTICATION ----------------------------------------------------------------------------
    } else {
      user = standardAuthentication(pageState);
    }


    if (user != null && pageState.validEntries()) {

      SessionState sm = pageState.getSessionState();
      Operator op = (Operator) PersistenceHome.findByPrimaryKey(PlatformConfiguration.defaultOperatorSubclass, Integer.parseInt(user.getId().toString()));

      doLog(op, sm);
      /*if (pageState.getEntry("REMEMBER_ME").checkFieldValue())
        pageState.sessionState.setAttribute("SAVE_PSW_IN_COOKIE", password);
      */
      pageState.setCommand(Commands.FIND);
    }
  }

  private Operator standardAuthentication(PageState pageState) throws PersistenceException, ApplicationException {
    String password;
    Operator user = null;

    try {
      ClientEntry ceName = pageState.getEntryAndSetRequired(OperatorConstants.FLD_LOGIN_NAME);
      ClientEntry cePassword = pageState.getEntry(OperatorConstants.FLD_PWD);

      password = cePassword.stringValue();
      String username = ceName.stringValue();

      String newPassword = null;

      try {
        user = Operator.authenticateUser(password, username, pageState.getApplication().isLoginCookieEnabled());
      } catch (org.jblooming.security.SecurityException e) {
        cePassword.errorCode = e.getMessage();
      } catch (FindException e) {
        ceName.errorCode = e.getMessage();
      }

      if (pageState.validEntries()) {

        final String pass_exp = ApplicationState.getApplicationSetting(SystemConstants.FLD_PASSWORD_EXPIRY);
        int maxDaysPassed = 0;

        try {
          maxDaysPassed = pass_exp != null ? Integer.parseInt(pass_exp) : 0;
        } catch (Throwable e) {
          Tracer.platformLogger.error("Invalid password expiry value in global settings:" + SystemConstants.FLD_PASSWORD_EXPIRY + "=" + pass_exp, e);
        }

        if ((maxDaysPassed > 0 &&
                user.getLastPasswordChangeDate() != null &&
                ((System.currentTimeMillis() - user.getLastPasswordChangeDate().getTime()) / (CompanyCalendar.MILLIS_IN_HOUR * 24)) > maxDaysPassed)) {

          try {
            ClientEntry ceNewPassword = pageState.getEntryAndSetRequired(OperatorConstants.FLD_LOGIN_NEW_PWD);
            ClientEntry ceNewPasswordConfirm = pageState.getEntryAndSetRequired(OperatorConstants.FLD_LOGIN_NEW_PWD_RETYPE);

            if (!ceNewPassword.stringValue().equals(ceNewPasswordConfirm.stringValue())) {
              ceNewPasswordConfirm.errorCode = "ERR_PASSWORD_MUST_BE_IDENTICAL";
              throw new ActionException();
            }
            final Iterator lastPasswordIterator = user.getLastPasswordIterator();
            while (lastPasswordIterator.hasNext()) {
              String s = (String) lastPasswordIterator.next();
              try {
                if (s.equals(user.computePassword(ceNewPassword.stringValue()))) {
                  ceNewPassword.errorCode = "ERR_PASSWORD_ALREADY_USED";
                  throw new ActionException();
                }
              } catch (NoSuchAlgorithmException e) {
                throw new ApplicationException(e);
              }
            }

            //passed all obstacles
            newPassword = ceNewPassword.stringValue();

            if (newPassword != null) {
              user.changePassword(newPassword);
            }


          } catch (ActionException e) {
          }
        }
      }

    } catch (ActionException e) {
    }
    return user;
  }

  private Operator httpAuthentication(PageState pageState, HttpServletRequest request) throws PersistenceException {
    Operator user = null;
    if (request.getRemoteUser() != null) {
      user = Operator.findByLoginName(request.getRemoteUser());
    } else {
      pageState.getEntry(OperatorConstants.FLD_LOGIN_NAME).errorCode = SystemConstants.ENABLE_AUTHENTICATION_TYPE.ENABLE_HTTP_AUTHENTICATION + "=yes on " +
              PlatformConfiguration.globalSettingsFileName + " but no user (request.getRemoteUser()) is provided by the web app context ";
    }
    return user;
  }


  private Operator ldapAuthentication(PageState pageState) {
    String password;
    String username = pageState.getEntryAndSetRequired(OperatorConstants.FLD_LOGIN_NAME).stringValueNullIfEmpty();
    password = pageState.getEntry(OperatorConstants.FLD_PWD).stringValueNullIfEmpty();
    Operator user = null;

    String domain = ApplicationState.getApplicationSetting(LdapUtilities.DOMAIN_NAME);
    String provider = ApplicationState.getApplicationSetting(LdapUtilities.PROVIDER_URL);
    String secAuth = ApplicationState.getApplicationSetting(LdapUtilities.SECURITY_AUTHENTICATION);

    if (username != null && password != null) {
      String msgError = LdapUtilities.checkUser(provider, domain, username, secAuth, password);
      if (msgError != null) {
        pageState.getEntry(OperatorConstants.FLD_LOGIN_NAME).errorCode = I18n.get("ERR_INVALID_LOGIN") + " (LDAP)";

      } else {

        //got authorized; now search user
        try {
          user = Operator.findByLoginName(username);
        } catch (PersistenceException e) {
          Tracer.platformLogger.debug(e);
        }
        if (user == null) {
          boolean create = Fields.TRUE.equals(ApplicationState.getApplicationSetting(LdapUtilities.CREATE_USERS_ON_LOGIN));
          if (create) {
            user = createPlatformUserFromLDAP(username, pageState);
          } else {
            pageState.getEntry(OperatorConstants.FLD_LOGIN_NAME).errorCode = I18n.get("ERR_INVALID_LOGIN") + " (LDAP)";
          }
        }
      }
    } else {
      pageState.getEntry(OperatorConstants.FLD_LOGIN_NAME).errorCode = I18n.get("ERR_INVALID_LOGIN") + " (LDAP)";
    }
    return user;
  }

  protected Operator createPlatformUserFromLDAP(String username, PageState pageState) {
    throw new PlatformRuntimeException("LoginAction:createPlatformUserFromLDAP you must provide your implementation");
  }

  public static void doLog(Operator op, SessionState sessionState) throws PersistenceException, ApplicationException {
    op.setLastLoggedOn(new Date());
    op.store();
    sessionState.setLoggedOperator(op);
    sessionState.screenElementsStatus = ScreenElementStatus.getInstanceFromOptions(op);

    //no skin reset
    sessionState.setSkin(SessionState.createSkin(ApplicationState.contextPath, "", ApplicationState.platformConfiguration.defaultApplication.getRootFolder()));

    if (ApplicationState.platformConfiguration.schedulerRunsByDefault && !Scheduler.isRunning()) {
      ApplicationState.applicationSettings.put(SystemConstants.ADMIN_MESSAGE, "Scheduler is NOT running. Start it in admin -> monitoring -> scheduler monitor");
    }

    //reset locale on session
    String language = op.getOption(OperatorConstants.FLD_SELECT_LANG);
    if (JSP.ex(language))
      sessionState.setLocale(I18n.getLocale(language));
  }


  public void logout(PageState pageState, HttpServletRequest request, HttpServletResponse response) {
    request.getSession().removeAttribute(Fields.SESSION);

    //session.invalidate DOES NOT WORK IN TOMCAT!

    //this is meant to bind SessionState to a NEW http session;
    // but with this, SessionState is bound to the old, dying http session
    // and after 1 minute the user is logged out (by http session)!!!
    //request.getSession().setMaxInactiveInterval(1);

    //so we keep the SAME http session, and we hope for the best
    pageState.sessionState = null;
    pageState.sessionState = SessionState.getSessionState(request);
    request.getSession().setAttribute("CMD_LOG_OUT_PARAM_SESSION_KEY", "y"); // add graziella - per evitare che il ping si repeta anche dopo il log out
  }


}
