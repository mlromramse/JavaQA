package org.jblooming.operator.businessLogic;

import org.jblooming.ApplicationException;
import org.jblooming.PlatformRuntimeException;
import org.jblooming.messaging.Listener;
import org.jblooming.messaging.MessagingSystem;
import org.jblooming.operator.Operator;
import org.jblooming.persistence.PersistenceHome;
import org.jblooming.persistence.exceptions.PersistenceException;
import org.jblooming.utilities.JSP;
import org.jblooming.waf.SessionState;
import org.jblooming.waf.constants.OperatorConstants;
import static org.jblooming.waf.constants.OperatorConstants.*;
import org.jblooming.waf.settings.ApplicationState;
import org.jblooming.waf.view.PageState;


/**
 * @author Pietro Polsinelli ppolsinelli@open-lab.com
 * @author Roberto Bicchierai rbicchierai@open-lab.com
 */
public class OptionAction {


  public void cmdEdit(PageState pageState) throws PersistenceException {

    make(pageState);
  }

  protected void make(PageState pageState) {
    make(pageState.getLoggedOperator(), pageState);
  }

  public static void make(Operator to, PageState pageState) {

    pageState.addClientEntry(FLD_SELECT_LANG, to.getLanguage());
    String whb = to.getOption(FLD_WORKING_HOUR_BEGIN);
    if (whb != null) {
      pageState.addClientEntry(FLD_WORKING_HOUR_BEGIN, whb);
    }

    String whe = to.getOption(FLD_WORKING_HOUR_END);
    if (whe != null) {
      pageState.addClientEntry(FLD_WORKING_HOUR_END, whe);
    }

    whe = to.getOption(FLD_WORKING_HOUR_TOTAL);
    if (whe != null) {
      pageState.addClientEntry(FLD_WORKING_HOUR_TOTAL, whe);
    }

    pageState.addClientEntry(OP_PAGE_SIZE, to.getOption(OP_PAGE_SIZE));
    String s = to.getOption(FLD_CURRENT_SKIN);
    if (!JSP.ex(s))
      s = ApplicationState.getApplicationSetting(FLD_CURRENT_SKIN);
    pageState.addClientEntry(FLD_CURRENT_SKIN, s);

    String operatorOption = to.getOption(SEND_EVENT_BY_ICAL);
    pageState.addClientEntry(SEND_EVENT_BY_ICAL, operatorOption);
    MessagingSystem.makeMedias(to.getOption(MEDIA_PREFERRED_CHANNEL), MEDIA_PREFERRED_CHANNEL + "_", pageState);

    pageState.addClientEntry("BUDDY_IMAGE", to.getOption("BUDDY_IMAGE"));
    pageState.addClientEntry("PREFERRED_COLOR", to.getOption("PREFERRED_COLOR"));

    pageState.addClientEntry("ADD_PHOTO_FROM_GRAVATAR", to.getOption("ADD_PHOTO_FROM_GRAVATAR"));

    pageState.addClientEntry("SVN_LOGIN_USER_OPT", to.getOption("SVN_LOGIN_USER_OPT"));
    pageState.addClientEntry("SVN_LOGIN_PWD_OPT", to.getOption("SVN_LOGIN_PWD_OPT"));

    pageState.addClientEntry("SEND_TO_TWITTER", to.getOption("SEND_TO_TWITTER"));

    pageState.addClientEntry("GOOGLE_LOGIN_USER", to.getOption("GOOGLE_LOGIN_USER"));
    pageState.addClientEntry("SEND_TO_GOOGLE", to.getOption("SEND_TO_GOOGLE"));
    pageState.addClientEntry("DEFAULT_EXT_CALENDAR", to.getOption("DEFAULT_EXT_CALENDAR"));
    pageState.addClientEntry( OperatorConstants.NOTIFY_MY_EVENTS_TO_MYSELF, to.getOption( OperatorConstants.NOTIFY_MY_EVENTS_TO_MYSELF));
    pageState.addClientEntry( "MESSAGE_DIGESTER_BY_EMAIL", to.getOption(  "MESSAGE_DIGESTER_BY_EMAIL"));
    pageState.addClientEntry( "MESSAGE_DIGESTER_BY_RSS", to.getOption(  "MESSAGE_DIGESTER_BY_RSS"));
    pageState.addClientEntry("REMEMBER_LOGIN" , to.getOption( "REMEMBER_LOGIN"));


    pageState.addClientEntry("HOME_PAGE", to.getOption("HOME_PAGE"));
  }

  public void cmdSave(PageState pageState, String contextPath) throws PersistenceException {

    SessionState sessionState = pageState.sessionState;
    Operator logged = pageState.getLoggedOperator();
    logged = (Operator) PersistenceHome.findByPrimaryKey(Operator.class, logged.getId());
    pageState.mainObject = logged;

    saveOptions(logged, pageState);

    String value = pageState.getEntry(FLD_CURRENT_SKIN).stringValueNullIfEmpty();
    if (value != null) {
      sessionState.setSkin(SessionState.createSkin(contextPath, value, pageState.getApplication().getRootFolder()));
    }

    logged.store();
  }

  public static void saveOptions(Operator operator, PageState pageState) {

    String value = pageState.getEntry(FLD_SELECT_LANG).stringValueNullIfEmpty();
    if (value != null)
      operator.putOption(FLD_SELECT_LANG, value);
    else
      operator.getOptions().remove(FLD_SELECT_LANG);

    value = pageState.getEntry(OP_PAGE_SIZE).stringValueNullIfEmpty();
    if (value != null)
      operator.putOption(OP_PAGE_SIZE, value);
    else
      operator.getOptions().remove(OP_PAGE_SIZE);

    operator.putOption(SEND_EVENT_BY_ICAL, pageState.getEntry(SEND_EVENT_BY_ICAL).checkFieldHtmlValue());

    value = pageState.getEntry(FLD_WORKING_HOUR_BEGIN).stringValueNullIfEmpty();
    if (value != null)
      operator.putOption(FLD_WORKING_HOUR_BEGIN, value);
    else
      operator.getOptions().remove(FLD_WORKING_HOUR_BEGIN);

    value = pageState.getEntry(FLD_WORKING_HOUR_END).stringValueNullIfEmpty();
    if (value != null)
      operator.putOption(FLD_WORKING_HOUR_END, value);
    else
      operator.getOptions().remove(FLD_WORKING_HOUR_END);

    value = pageState.getEntry(FLD_WORKING_HOUR_TOTAL).stringValueNullIfEmpty();
    if (value != null)
      operator.putOption(FLD_WORKING_HOUR_TOTAL, value);
    else
      operator.getOptions().remove(FLD_WORKING_HOUR_TOTAL);

    value = pageState.getEntry(FLD_CURRENT_SKIN).stringValueNullIfEmpty();
    if (value != null) {
      operator.putOption(FLD_CURRENT_SKIN, value);
    } else
      operator.getOptions().remove(FLD_CURRENT_SKIN);

    value = pageState.getEntry("PREFERRED_COLOR").stringValueNullIfEmpty();
    if (value != null) {
      operator.putOption("PREFERRED_COLOR", value);
    } else
      operator.getOptions().remove("PREFERRED_COLOR");

    value = pageState.getEntry("BUDDY_IMAGE").stringValueNullIfEmpty();
    if (value != null) {
      operator.putOption("BUDDY_IMAGE", value);
    } else
      operator.getOptions().remove("BUDDY_IMAGE");
    value = pageState.getEntry("REMEMBER_LOGIN").stringValueNullIfEmpty();
       if (value != null)
     operator.putOption("REMEMBER_LOGIN", value);


    value = pageState.getEntry("SVN_LOGIN_USER_OPT").stringValueNullIfEmpty();
    if (value != null) {
      operator.putOption("SVN_LOGIN_USER_OPT", value);
    } else
      operator.getOptions().remove("SVN_LOGIN_USER_OPT");
    value = pageState.getEntry("SVN_LOGIN_PWD_OPT").stringValueNullIfEmpty();
    if (value != null) {
      operator.putOption("SVN_LOGIN_PWD_OPT", value);
    } else
      operator.getOptions().remove("SVN_LOGIN_PWD_OPT");

    value = pageState.getEntry("SEND_TO_TWITTER").stringValueNullIfEmpty();
    if (value != null)
      operator.putOption("SEND_TO_TWITTER", value);


    value = pageState.getEntry("GOOGLE_LOGIN_USER").stringValueNullIfEmpty();
    if (value != null) {
      operator.putOption("GOOGLE_LOGIN_USER", value);
    } else
      operator.getOptions().remove("GOOGLE_LOGIN_USER");

    value = pageState.getEntry("SEND_TO_GOOGLE").stringValueNullIfEmpty();
    if (value != null)
      operator.putOption("SEND_TO_GOOGLE", value);

    value = pageState.getEntry("DEFAULT_EXT_CALENDAR").stringValueNullIfEmpty();
    if (value != null)
      operator.putOption("DEFAULT_EXT_CALENDAR", value);

  }

  public static void cmdUpdateLoggedOption(PageState pageState, String optionName, String optionValue) throws PersistenceException {
    SessionState sessionState = pageState.sessionState;
    Operator logged = pageState.getLoggedOperator();
    if (logged != null) {
      logged = (Operator) PersistenceHome.findByPrimaryKey(Operator.class, logged.getId());
      logged.putOption(optionName, optionValue);
      logged.store();
    }
  }

  public void cmdDelete(PageState pageState) throws PersistenceException {
    String id = pageState.getMainObjectId().toString();
    Listener l = (Listener) PersistenceHome.findByPrimaryKey(Listener.class, id);
    if (l != null)
      l.remove();
    pageState.stopPageAfterController = true;
    //make(pageState);
  }

  public void cmdRemoveOption(PageState pageState) throws PersistenceException {

    String[] opId_key = ((String) pageState.mainObjectId).split("___");

    String opId = opId_key[0];
    String key = opId_key[1];

    Operator operator = (Operator) PersistenceHome.findByPrimaryKey(Operator.class, opId);
    operator.getOptions().remove(key);
    operator.store();

    SessionState sessionState = pageState.sessionState;
    Operator logged = pageState.getLoggedOperator();

    if (logged.equals(operator))
      logged.getOptions().remove(key);
  }

}
