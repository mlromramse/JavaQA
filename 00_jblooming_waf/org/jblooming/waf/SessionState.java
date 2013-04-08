package org.jblooming.waf;

import org.jblooming.ApplicationException;
import org.jblooming.PlatformRuntimeException;
import org.jblooming.cursor.Cursor;
import org.jblooming.ontology.Identifiable;
import org.jblooming.operator.Operator;
import org.jblooming.persistence.PersistenceHome;
import org.jblooming.persistence.exceptions.FindByPrimaryKeyException;
import org.jblooming.persistence.exceptions.PersistenceException;
import org.jblooming.persistence.hibernate.PersistenceContext;
import org.jblooming.tracer.Tracer;
import org.jblooming.utilities.HashTable;
import org.jblooming.utilities.JSP;
import org.jblooming.utilities.ReflectionUtilities;
import org.jblooming.utilities.DateUtilities;
import org.jblooming.waf.constants.Fields;
import org.jblooming.waf.constants.OperatorConstants;
import org.jblooming.waf.html.layout.Skin;
import org.jblooming.waf.html.state.ScreenElementStatus;
import org.jblooming.waf.settings.*;
import org.jblooming.waf.state.PageHistory;
import org.jblooming.waf.view.PageSeed;
import org.jblooming.waf.view.PageState;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionBindingEvent;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class SessionState implements javax.servlet.http.HttpSessionBindingListener {

  private Map attributes = new HashTable();

  private PageSeed loginPendingUrl;

  public Stack<ClipProxy> clipboard = new Stack<ClipProxy>();

  public PageHistory pageHistory = new PageHistory();

  private Locale locale;
  private TimeZone timeZone;

  private String[] localizedDateFormats=null;

  private int opId = -1;

  @Deprecated // use js
  private int pageWidth;
  @Deprecated // use js
  private int pageHeight;
  @Deprecated // use js
  private String browser = null;

  private Skin skin;

  public Map<String, ScreenElementStatus> screenElementsStatus = new HashMap<String, ScreenElementStatus>();

  public static int totalSessionStates = 0;
  public static Set<Integer> totalOpIds = new HashSet<Integer>();

  private Application application;

  public SessionState() {

  }

  public int getDefaultPageSize()  {
    int height = getPageHeight();
    if (height > 250)
      return ((height - 200) / 35);
    else
      return 2;
  }

  public PageSeed getLoginPendingUrl() {
    return loginPendingUrl;
  }

  public void setLoginPendingUrl(PageSeed loginPendingUrl) {
    this.loginPendingUrl = loginPendingUrl;
  }

  public void setAttribute(Object key, Object object) {
    if (getAttributes() == null) {
      setAttributes(new HashTable());
    }
    if (object == null)
      getAttributes().remove(key);
    else
      getAttributes().put(key, object);
  }

  public Object getAttribute(Object key) {
    if (getAttributes() == null) {
      setAttributes(new HashTable());
    }
    return getAttributes().get(key);
  }

  public Locale getLocale() {
    if (locale == null)
      try {
        String language=null;
        //Operator operator = Operator.load(getOpid());
        // teoros: added to call admin.jsp
        Operator operator = getOpid()>-1 ? Operator.load(getOpid()) :  null;
        if (operator!=null)
          language = operator.getOption(OperatorConstants.FLD_SELECT_LANG);
        if (language != null)
          locale = I18n.getLocale(language);
        else
          locale = ApplicationState.SYSTEM_LOCALE;
      } catch (PersistenceException e) {
        throw new PlatformRuntimeException(e);
    }
    return locale;
  }


  //todo implementare il timezone dalle opzioni utente
  public TimeZone getTimeZone() {
    return ApplicationState.SYSTEM_TIME_ZONE;
  }


  public String getLocalizedDateFormat(int field){
    if (localizedDateFormats==null){
      localizedDateFormats =DateUtilities.getLocalizedDateFormats(getLocale());
    }
    return localizedDateFormats[field];
  }

  public boolean isOperatorLogged() {
    return (opId != -1);
  }

  /**
   * @deprecated
   */
  public void setCursor(String pageName, Cursor c) {
    if (attributes == null) {
      attributes = new HashMap();
    }
    if (c == null)
      attributes.remove(Fields.PAGER_PREFIX + pageName);
    else
      this.setAttribute(Fields.PAGER_PREFIX + pageName, c);
  }

  /**
   * @deprecated
   */
  public Cursor getCursor(String cursorName) {
    try {
      return (Cursor) this.getAttribute(Fields.PAGER_PREFIX + cursorName);
    } catch (Exception e) {
      return null;
    }
  }

  public Map getAttributes() {
    return attributes;
  }

  public void setAttributes(Map attributes) {
    this.attributes = attributes;
  }

  /**
   * @deprecated use skin
   */
  public String getPathToImages() {
    return getSkin().imgPath;
  }

  public void clipboardPush(Identifiable o) {
    clipboard.push(new ClipProxy(o.getId(), ReflectionUtilities.getUnderlyingObjectClass(o)));
  }

  public Identifiable clipboardPop() throws FindByPrimaryKeyException {
    ClipProxy cp = clipboard.pop();
    return PersistenceHome.findByPrimaryKey(cp.clazz, cp.id);
  }

  public Identifiable clipboardPeek() throws FindByPrimaryKeyException {
    ClipProxy cp = clipboard.peek();
    return PersistenceHome.findByPrimaryKey(cp.clazz, cp.id);
  }


  public void setLoggedOperator(Operator op) throws PersistenceException, ApplicationException {
    this.opId = (Integer) op.getId();
    totalOpIds.add(opId);
  }


  public int getOpid(){
    return opId;
  }

  public void setLocale(Locale locale) {
    this.locale = locale;

    //when set locale reset date formats
    localizedDateFormats=null;
  }

  public int getPageWidth() {
    return pageWidth;
  }

  public void setPageWidth(int pageWidth) {
    this.pageWidth = pageWidth;
  }

  public int getPageHeight() {
    return pageHeight;
  }

  public void setPageHeight(int pageHeight) {
    this.pageHeight = pageHeight;
  }

  public static SessionState getSessionState(HttpServletRequest request) {

    HttpSession session = request.getSession(true);
    SessionState stateMachine = (SessionState) session.getAttribute(Fields.SESSION);

    if (stateMachine == null) {
      stateMachine = new SessionState();
      if (session != null) {
        session.setAttribute(Fields.SESSION, stateMachine);
        session.setAttribute("__ACTIVE_SESSIONS__", new SessionCounter());
      }
      // fill the browser type
      if(JSP.ex(request.getHeader("User-Agent")))
       stateMachine.setBrowser(request.getHeader("User-Agent").toUpperCase());
      else
       stateMachine.setBrowser("FIREFOX");
      try {
        stateMachine.setSkinForApplicationIfNull();
      } catch (PersistenceException e) {
        throw new PlatformRuntimeException(e);

      }
    }

    if (stateMachine.getLocale() == null) {
      stateMachine.setLocale(request.getLocale());
    }

    return stateMachine;
  }

  public static Skin createSkin(String contextPath, String skinDefaultName, String applicationRootFolder) {

    skinDefaultName = "bw";

    Skin skin = new Skin();
    skin.name = skinDefaultName;
    skin.imgPath = contextPath + "/commons/skin/images/";
    skin.imgPathPlus=contextPath + "/" + applicationRootFolder + "/images/";
    skin.css = contextPath + "/commons/skin/" + skinDefaultName + "/";
    return skin;
  }

  public void setSkinForApplicationIfNull() throws PersistenceException {
    String currSkin = ApplicationState.getApplicationSetting(OperatorConstants.FLD_CURRENT_SKIN);
    setSkinForApplicationIfNull(
            ApplicationState.contextPath,
            currSkin,
            getApplication().getRootFolder());
  }

  public Skin setSkinForApplicationIfNull(String contextPath, String skinDefaultName, String applicationRootFolder) throws PersistenceException {

//    String skinNameFromOptions = getLoggedOperator() != null ? getOperatorOption(OperatorConstants.FLD_CURRENT_SKIN) : null;

    if (skin == null || skin.imgPathPlus.indexOf(applicationRootFolder) == -1 ){//|| !skin.name.equals(skinNameFromOptions)) {
      //Operator operator = Operator.load(getOpid());
      // teoros: added to call admin.jsp
      Operator operator = getOpid()>-1 ? Operator.load(getOpid()) :  null;
      String skinNameFromOptions = operator!= null ? operator.getOption(OperatorConstants.FLD_CURRENT_SKIN) : null;

      if (skinNameFromOptions != null)
        setSkin(SessionState.createSkin(contextPath, skinNameFromOptions, applicationRootFolder));
      else
        setSkin(SessionState.createSkin(contextPath, skinDefaultName, applicationRootFolder));
    }
    return skin;
  }

  public Skin getSkin() {
    return skin;
  }

  public void setSkin(Skin skin) {
    this.skin = skin;
  }

  public PageState getPageState(HttpServletRequest request, HttpServletResponse response) {

    String url = request.getServletPath();
    PageState pageState = (PageState) request.getAttribute(Fields.VIEW);
    if (pageState == null) {
      pageState = new PageState(url, this);

      String contentType = request.getContentType();
      pageState.multipart = contentType != null && contentType.startsWith("multipart/form-data");

      PageState.buildPartsAndClientEntries(request, pageState);
      request.setAttribute(Fields.VIEW, pageState);

    }
    return pageState;
  }

  public Application getApplication() {
    if (application == null)
      application = ApplicationState.platformConfiguration.getDefaultApplication();
    return application;
  }

  public void setApplication(Application application) {
    this.application = application;
  }

  public void valueBound(HttpSessionBindingEvent event) { // add graziella - 23/10/2008
    Collection<Application> apps = ApplicationState.platformConfiguration.applications.values();
    for (Application app : apps)
      if (app instanceof ApplicationSupport) {
        ApplicationSupport _app = (ApplicationSupport) app;
        _app.sessionStateValueBound();
      }
  }

  public void valueUnbound(HttpSessionBindingEvent event) { // add graziella - 23/10/2008
    Collection<Application> apps = ApplicationState.platformConfiguration.applications.values();
    for (Application app : apps)
      if (app instanceof ApplicationSupport) {
        ApplicationSupport _app = (ApplicationSupport) app;
        _app.sessionStateValueUnbound();
      }

    if (opId > 0) {
    PersistenceContext pc = null;
    try {
        pc = new PersistenceContext();
        Operator op = (Operator) PersistenceHome.findByPrimaryKey(PlatformConfiguration.defaultOperatorSubclass, opId, pc);
        if (op != null) {
        final Map screenElementsStatuses = this.screenElementsStatus;
        if (screenElementsStatuses != null && screenElementsStatuses.size() > 0) {
          for (Iterator iterator = screenElementsStatuses.keySet().iterator(); iterator.hasNext();) {
            final String key = (String) iterator.next();
            ScreenElementStatus screenElementStatus = (ScreenElementStatus) screenElementsStatuses.get(key);
            String value = screenElementStatus.toPersistentString(key);
            if (JSP.ex(value))
              op.putOption(ScreenElementStatus.SES_QUALIFIER + key, value);
            else
              op.getOptions().remove(ScreenElementStatus.SES_QUALIFIER + key);
          }
        }

          op.setLastRequestOn(new Date());
        op.store(pc);
        totalOpIds.remove(op.getId());
      }
        pc.commitAndClose();
    } catch (Throwable throwable) {
        if (pc != null)
          pc.rollbackAndClose();
      Tracer.platformLogger.warn("Attempting to find operator in session and save it in valueUnbound - failed: " + throwable.getMessage(), throwable);
      }
    }
  }

  public String getBrowser() {
    return browser;
  }

  public void setBrowser(String browser) {
    this.browser = browser;
  }

  public boolean isFirefox() {
    return getBrowser().contains("FIREFOX");
  }

  public boolean isChrome() {
    return getBrowser().contains("CHROME");
  }

  public boolean isSafari() {
    return !isChrome() && getBrowser().contains("SAFARI");
  }


  public boolean isExplorer() {
    return getBrowser().contains("MSIE");
  }

  public boolean isWindows() {
    return getBrowser().contains("WINDOWS");
  }

  public boolean isMac() {
    return getBrowser().contains("MAC");
  }

  public boolean isLinux() {
    return getBrowser().contains("LINUX");
  }

  public boolean isIPhone() {
    // screen size 480x320
    return getBrowser().contains("IPHONE");
  }

  public boolean isIPad() {
    return getBrowser().contains("IPAD");
  }

  public boolean isIPod() {
    return getBrowser().contains("IPOD");
  }

  public boolean isAndroid() {
    return getBrowser().contains("ANDROID");
  }

  public boolean isMobile() {
    boolean foundMatch = false;
    Pattern regex = Pattern.compile("(iemobile|windows ce|netfront|playstation|playstation|like mac os x|midp|up\\.browser|symbian|nintendo|android)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
    Matcher regexMatcher = regex.matcher(getBrowser().toLowerCase());
    foundMatch = regexMatcher.find();
    return foundMatch;
    //return this.isIPad() || this.isIPhone() || this.isIPod() || isAndroid();
 }

  public void setApplication(String applicationName) {
    setApplication(ApplicationState.platformConfiguration.applications.get(applicationName));
  }

}