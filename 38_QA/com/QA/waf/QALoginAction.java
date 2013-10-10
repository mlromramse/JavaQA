package com.QA.waf;

import com.QA.QAOperator;
import com.QA.connections.facebook.FacebookUtilities;
import com.google.gdata.client.authn.oauth.OAuthException;
import net.sf.json.JSONObject;
import org.jblooming.ApplicationException;
import org.jblooming.messaging.MailHelper;
import org.jblooming.persistence.exceptions.FindByPrimaryKeyException;
import org.jblooming.persistence.exceptions.FindException;
import org.jblooming.persistence.exceptions.PersistenceException;
import org.jblooming.persistence.exceptions.StoreException;
import org.jblooming.persistence.hibernate.PersistenceContext;
import org.jblooming.security.businessLogic.LoginAction;
import org.jblooming.system.SystemConstants;
import org.jblooming.utilities.CollectionUtilities;
import org.jblooming.utilities.DateUtilities;
import org.jblooming.utilities.JSP;
import org.jblooming.utilities.StringUtilities;
import org.jblooming.waf.constants.FieldErrorConstants;
import org.jblooming.waf.constants.Fields;
import org.jblooming.waf.exceptions.ActionException;
import org.jblooming.waf.settings.ApplicationState;
import org.jblooming.waf.settings.I18n;
import org.jblooming.waf.view.ClientEntry;
import org.jblooming.waf.view.PageSeed;
import org.jblooming.waf.view.PageState;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class QALoginAction {

  public static List<BadgeChecker> badgeCheckers = new ArrayList();

  // log the user reset counters and set cookies

  public static void doLog(QAOperator operator, PageState pageState, HttpServletRequest request, HttpServletResponse response) throws PersistenceException, ApplicationException, IOException, NoSuchAlgorithmException {
    LoginAction.doLog(operator, pageState.sessionState);

    // thest if user want to remember cookies or not
    if (!Fields.TRUE.equals(operator.getOption("DO_NOT_REMEMBER_LOGIN"))) {

      // set the cookie
      Cookie coo = new Cookie("COOKIMMENSO", operator.getAPIKey());
      coo.setMaxAge(60 * 60 * 24 * 60);
      coo.setPath(ApplicationState.contextPath + "/applications/QA/site/access");
      response.addCookie(coo);

      //set another cookie
      coo = new Cookie("QALOG", "yes");
      coo.setMaxAge(60 * 60 * 24 * 60);
      coo.setPath(ApplicationState.contextPath + "/");
      response.addCookie(coo);
    }

    pageState.sessionState.setAttribute("invalidLoginCount", new Integer(0));

    //store ip and time of last login
    // move thisLogin to last
    if (JSP.ex(operator.getOptions().get("login_nextfuturelast")))
      operator.getOptions().put("login_last", operator.getOptions().get("login_nextfuturelast"));

    String thisLogin = DateUtilities.dateAndHourToFullString(new Date()) + "**" + request.getRemoteAddr();

    operator.getOptions().put("login_nextfuturelast", thisLogin);
    operator.store();
  }

  public static QAOperator enroll(PageState pageState, HttpServletRequest request, HttpServletResponse response) throws ApplicationException, PersistenceException, NoSuchAlgorithmException, IOException {
    QAOperator operator = null;

    try {
      ClientEntry lnEntry = pageState.getEntryAndSetRequired("USERNAME");
      String username = lnEntry.stringValue();
      ClientEntry pwdce = pageState.getEntryAndSetRequired("PASSWORD1");
      String pwd1 = pwdce.stringValue();
      String pwd2 = pageState.getEntryAndSetRequired("PASSWORD2").stringValue();

      if (!pwd1.equals(pwd2)) {
        pwdce.errorCode = "Le password sono diverse";
        throw new ActionException("Le password sono diverse");
      }

      try {
        operator = (QAOperator) QAOperator.findByLoginName(username);
      } catch (PersistenceException e) {
        operator = null;
      }
      if (operator != null) {
        lnEntry.errorCode = I18n.get("LOGIN_NOT_AVAILABLE");
        throw new ActionException(FieldErrorConstants.ERR_KEY_MUST_BE_UNIQUE);
      }

      ClientEntry emailCe = pageState.getEntry("EMAIL");
      String email = emailCe.emailValue();
      operator = new QAOperator();

      if (JSP.ex(email)) {
        operator.setUnverifiedEmail(email);
        //if (JSP.ex(email) && (!operator.isUnique("email") || !operator.isUnique("unverifiedEmail"))) {
        if (JSP.ex(email) && !operator.isUnique("email")) {
          emailCe.errorCode = I18n.get("EMAIL_ALREADY_IN_USE");
          throw new ActionException(FieldErrorConstants.ERR_KEY_MUST_BE_UNIQUE);
        }
      }

      //create default work plan
      String fullname = pageState.getEntry("FULLNAME").stringValueNullIfEmpty();
      if (!JSP.ex(fullname))
        fullname = username;

      operator.setName(fullname);
      operator.setLoginName(username);
      operator.changePassword(pwd1);
      // create enabled operator
      operator.setEnabled(true);

      // this is the first demo setup
      // maybe the user has been already expiryDate because has been purchased by someone else, in this case do not reset the date
//      if (!JSP.ex(operator.getExpiryDate())){
//        operator.setExpiryDate(new Date(System.currentTimeMillis()+ CompanyCalendar.MILLIS_IN_DAY*15));
//      }

      operator.store();

      operator.sendEnrollEmailMesssage(pageState);
      pageState.addClientEntry("remoteAddr", request.getRemoteAddr());
      setLanguageAndNotify(operator, username, pageState);

      PersistenceContext.getDefaultPersistenceContext().checkPoint();

      // perform login
      QALoginAction.doLog(operator, pageState, request, response);
      pageState.command = "ENROLL_OK";

    } catch (ActionException e) {
    }

    return operator;
  }

  public static QAOperator enrollWithTwitter(PageState pageState, HttpServletRequest request, HttpServletResponse response) throws ApplicationException, PersistenceException, NoSuchAlgorithmException, IOException, TwitterException, ActionException {


    Twitter twitter = TwitterFactory.getSingleton();
    //RequestToken twitterRequestToken = twitter.getOAuthRequestToken();
    AccessToken accessToken = twitter.getOAuthAccessToken(pageState.getEntry("oauth_verifier").stringValueNullIfEmpty());

    //AccessToken accessToken = twitter.getOAuthAccessToken();

    String twitterLoginName = accessToken.getScreenName();
    User twitterUser = twitter.showUser(twitterLoginName);
    String name = JSP.ex(twitterUser.getName()) ? twitterUser.getName() : twitterLoginName;
    //String token = accessToken.getToken();

    QAOperator logged = (QAOperator) QAOperator.findByToken(twitterLoginName + "@twitter");
    if (logged != null && !logged.isEnabled())
      throw new ActionException();
    String loginName = checkFreeUsername(twitterLoginName);
    logged = createUser(name, logged, twitterLoginName + "@twitter", loginName, "", pageState);
    logged.setGravatarUrl(twitterUser.getProfileImageURL());
    logged.store();

    QALoginAction.doLog(logged, pageState, request, response);

    return logged;
  }

  public static String enrollWithFacebook(PageState pageState, HttpServletRequest request, HttpServletResponse response) throws ApplicationException, PersistenceException, NoSuchAlgorithmException, IOException, TwitterException, ActionException {
    QAOperator operator = null;
    String code = pageState.getEntry("code").stringValueNullIfEmpty();
    /*FacebookUtilities facebookUtilities = null;
    if (ApplicationState.platformConfiguration.development) {
      facebookUtilities = new FacebookUtilities(
              ApplicationState.getApplicationSetting(FacebookUtilities.API_PERMISSIONS),
              FacebookUtilities.getApiKey(),
              ApplicationState.serverURL + "/applications/QA/site/access/parts/facebookLoginAuth.jsp",
              "1cf4881d82348d7b2542e1bfeaa70be3");
    } else {*/
    FacebookUtilities facebookUtilities = new FacebookUtilities(
            ApplicationState.getApplicationSetting(FacebookUtilities.API_PERMISSIONS),
            FacebookUtilities.getApiKey(),
            ApplicationState.serverURL + "/applications/QA/site/access/parts/facebookLoginAuth.jsp",
            FacebookUtilities.getApiKeySecret());
    //}
    String accessToken = facebookUtilities.getAccessToken(code);

    JSONObject userData = facebookUtilities.getFacebookUserData(accessToken);
    String userName = userData.get("name") + "";
    userName = userName.replaceAll(" ", "");
    String facebookId = userData.get("id") + "";
    String email = userData.get("email") + "";

    QAOperator logged = QAOperator.findByToken(facebookId + "@facebook");

    if (logged != null && !logged.isEnabled())
      throw new ActionException();

    String available = checkFreeUsername(userName);

    logged = createUser(userData.get("name") + "", logged, facebookId + "@facebook", available, email, pageState);

    if (JSP.ex(email) && QAOperator.loadByEmail(email) == null) {
      logged.setConfirmedEmail(email);
      //logged.sendWelcomeEmailMesssage(pageState);
    }

    String avatarUrl = "";
    avatarUrl = "https://graph.facebook.com/" + facebookId + "/picture";
    logged.setGravatarUrl(avatarUrl);
    logged.store();


    QALoginAction.doLog(logged, pageState, request, response);

    return accessToken;
  }

  public static void enrollWithGoogle(PageState pageState, String openId, String email, HttpServletRequest request, HttpServletResponse response) throws OAuthException, IOException, NoSuchAlgorithmException, PersistenceException, ApplicationException, ActionException {

    QAOperator logged = QAOperator.findByToken(email + "@google");
    if (logged != null && !logged.isEnabled())
      throw new ActionException();
    if (logged == null) {
      String[] userName = email.split("@");
      String user = userName[0];
      String available = checkFreeUsername(user);
      logged = createUser(available, logged, email + "@google", available, email, pageState);
    }

    if (JSP.ex(email) && QAOperator.loadByEmail(email) == null) {
      logged.setConfirmedEmail(email);
      //logged.sendWelcomeEmailMesssage(pageState);
    }

    QALoginAction.doLog(logged, pageState, request, response);

  }

  /**
   * con
   * Get QAOperator from cookie
   *
   * @param loginCookie cookieImmenso
   * @return QAOperator if cookie contains a right id, null otherwise
   * @throws org.jblooming.persistence.exceptions.FindByPrimaryKeyException
   *
   * @throws NoSuchAlgorithmException Excepion
   */
  public static QAOperator getFromCookie(Cookie loginCookie) throws FindByPrimaryKeyException, NoSuchAlgorithmException {
    QAOperator operator = null;
    if (loginCookie != null)
      operator = getFromCookie(loginCookie.getValue());

    return operator;
  }

  /**
   * Get QAOperator from String ( it may be the famous cookieImmenso )
   *
   * @param cookieImmenso string with cookieImmenso
   * @return QAOperator op if CookieImmenso cointains a right Id
   * @throws FindByPrimaryKeyException exception
   * @throws NoSuchAlgorithmException  exception
   */
  public static QAOperator getFromCookie(String cookieImmenso) throws FindByPrimaryKeyException, NoSuchAlgorithmException {
    QAOperator operator = null;

    if (JSP.ex(cookieImmenso)) {
      List<String> vars = StringUtilities.splitToList(cookieImmenso.replace(';', 'x'), "x");
      if (vars.size() >= 2) {
        QAOperator ope = QAOperator.load(vars.get(0));
        if (ope != null && ope.getAPIKey().equalsIgnoreCase(cookieImmenso))
          operator = ope;
      }
    }
    return operator;
  }

  private static String checkFreeUsername(String user) throws PersistenceException {
    String available = "";
    String temp = "";
    QAOperator twin = (QAOperator) QAOperator.loadByLoginName(user);
    if (twin == null) {
      available = user;

    } else {
      boolean free = false;
      int i = 0;
      while (!free) {
        temp = user + i;
        twin = QAOperator.loadByLoginName(temp);
        if (twin == null) {
          available = temp;
          free = true;
        } else {
          i++;
        }
      }
    }
    return available;
  }

  public static QAOperator createUser(String name, QAOperator operator, String token, String loginName, String email, PageState pageState) throws ApplicationException, StoreException, NoSuchAlgorithmException {
    if (operator == null) {
      operator = new QAOperator();
      // iff using google youmay have the email if unique i set it   -- isUnique means there is already one
      if (JSP.ex(email) && !operator.isUnique("email"))
        operator.setUnverifiedEmail(email);

      //create default work plan


      if (!JSP.ex(name))
        name = loginName;

      operator.setName(name);
      operator.setLoginName(loginName);
      operator.setName(name);
      operator.changePassword(token);
      operator.setWebsite(token);
      // create enabled operator
      operator.setEnabled(true);

      // this is the first demo setup
      // maybe the user has been already expiryDate because has been purchased by someone else, in this case do not reset the date
//      if (!JSP.ex(operator.getExpiryDate())) {
//        CompanyCalendar cc = new CompanyCalendar();
//        cc.set(CompanyCalendar.YEAR, 2010);
//        cc.set(CompanyCalendar.MONTH, 9);
//        cc.set(CompanyCalendar.DAY_OF_MONTH, 2);
//        cc.set(CompanyCalendar.HOUR_OF_DAY, 12);
//        if (new Date().before(cc.getTime())) {    // still in beta
//          cc.set(CompanyCalendar.YEAR, 2099);
//          cc.set(CompanyCalendar.MONTH, 11);
//          cc.set(CompanyCalendar.DAY_OF_MONTH, 31);
//          operator.setExpiryDate(new Date(System.currentTimeMillis() + cc.getTime().getTime()));
//        } else {
//          operator.setExpiryDate(new Date(System.currentTimeMillis() + CompanyCalendar.MILLIS_IN_MONTH * 2));
//        }
//      }

      operator.store();
      operator.sendEnrollEmailMesssage(pageState);

      setLanguageAndNotify(operator, loginName, pageState);

      PersistenceContext.getDefaultPersistenceContext().checkPoint();


    }

    return operator;
  }

  private static void setLanguageAndNotify(QAOperator operator, String loginName, PageState pageState) {

    pageState.sessionState.setAttribute("JUSTENROLLED", "yes");

    Locale loc = operator.getLocale();
    if (loc != null && ApplicationState.i18n.supportedLanguages.contains(loc.getLanguage().toUpperCase()))
      operator.setLanguage(loc.getLanguage());


    //cc to us

    String ra = pageState.getEntry("remoteAddr").stringValueNullIfEmpty();

    String mailTo = ApplicationState.getApplicationSetting("MAIL_TO");
    String fromEmail = ApplicationState.getApplicationSetting(SystemConstants.FLD_MAIL_FROM);
    MailHelper.sendHtmlMailInSeparateThread(
            (ApplicationState.platformConfiguration.development ? I18n.g("QA_APP_NAME") + " Dev Support <ppolsinelli@open-lab.com>;" : I18n.g("QA_APP_NAME") + " Support <" + fromEmail + ">;"),
            CollectionUtilities.toSet(mailTo),
            "New user enrolled in " + I18n.g("QA_APP_NAME") + ": " + JSP.w(loginName) + (JSP.ex(ra) ? " ip:" + ra : "") + " id:" + JSP.w(operator.getId()),
            JSP.w(loginName) + " id:" + JSP.w(operator.getId()) + " email:" + JSP.w(operator.getEmail()) + " " + JSP.w(operator.getUnverifiedEmail()));
  }

  public static PageSeed magicalRedir(QAOperator logged, PageState pageState) throws IOException, FindException {
    PageSeed redirTo = null;

    PageSeed loginPendingUrl = pageState.sessionState.getLoginPendingUrl();
    if (loginPendingUrl != null && loginPendingUrl.href.indexOf("talk/index.jsp") == -1) {
      //no need to show them at redirect
      loginPendingUrl.removeEntry("USERNAME");
      loginPendingUrl.removeEntry("PWD");
      String url = ApplicationState.contextPath + loginPendingUrl.toLinkToHref();
      pageState.sessionState.setLoginPendingUrl(null);
      redirTo = new PageSeed(url);
    } else {
      // redirect to user home
      redirTo = pageState.pageFromRoot("talk/index.jsp");
      /*List<Question> manifests = logged.getContributions();
      if (manifests.size() == 1)
        redirTo = new PageSeed(manifests.get(0).getURL());
      else if (manifests.size() > 1)
        redirTo = pageState.pageFromRoot("manage/myProposals.jsp");*/
    }
    redirTo.disableCache = false;
    return redirTo;
  }

  public static interface BadgeChecker {
    String check(QAOperator operator);

    String badgeName();

    long scoreGainedOrLost();

  }

}