package org.jblooming.waf.view;


import org.jblooming.PlatformRuntimeException;
import org.jblooming.ontology.Identifiable;
import org.jblooming.operator.Operator;
import org.jblooming.page.Page;
import org.jblooming.persistence.PersistenceHome;
import org.jblooming.persistence.ThreadLocalPersistenceContextCarrier;
import org.jblooming.persistence.exceptions.PersistenceException;
import org.jblooming.persistence.hibernate.PersistenceContext;
import org.jblooming.security.InvalidTokenException;
import org.jblooming.utilities.*;
import org.jblooming.waf.ActionController;
import org.jblooming.waf.ScreenArea;
import org.jblooming.waf.ScreenRoot;
import org.jblooming.waf.SessionState;
import org.jblooming.waf.constants.Commands;
import org.jblooming.waf.constants.Fields;
import org.jblooming.waf.html.container.ButtonBar;
import org.jblooming.waf.html.core.HtmlBootstrap;
import org.jblooming.waf.html.core.JspIncluder;
import org.jblooming.waf.html.display.DeletePreviewer;
import org.jblooming.waf.html.display.HeaderFooter;
import org.jblooming.waf.html.layout.Skin;
import org.jblooming.waf.html.state.Form;
import org.jblooming.waf.settings.Application;
import org.jblooming.waf.settings.ApplicationState;
import org.jblooming.waf.settings.PlatformConfiguration;
import org.jblooming.waf.settings.I18n;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;
import java.io.IOException;
import java.util.*;

public class PageState extends PageSeed {

  private Operator operator = null; // added after reforma 7/10/2010

  private static ThreadLocal<PageState> __threadLocalPageState = new ThreadLocal<PageState>() {
    protected PageState initialValue() {
      return null;
    }
  };



  public Map<String, Page> pages = new HashTable();
  public SessionState sessionState;
  public Identifiable mainObject;
  public Map attributes = new HashMap();

  public Set initedElements = new HashSet();

  /**
   * this list is used to manage communication from controller to the page. CE errors are not inserted ther, but displayed automatically by the component
   */
  public List<Message> messagesFromController = new ArrayList<Message>();

  /**
   * elements of the screen
   */
  private Form form;
  private HeaderFooter headerFooter;

  /**
   * template composition
   */
  public boolean screenRunning = false;


  /**
   * stopPageAfterController inhibit the call to page generation
   * it should be set to true when a page/controller is called by ajax. E.G removing a line from a list
   * without refreshing the page and having this command inside the controller
   */
  public boolean stopPageAfterController = false;

  public ScreenArea runningControllerScreenArea;

  private Stack screenAreas = new Stack();
  public ScreenRoot rootScreen;

  /**
   * display validation
   */
  public HtmlBootstrap.HtmlBootstrappers htmlBootstrappers = new HtmlBootstrap.HtmlBootstrappers();

  private String focusedObjectDomId = null;

  private JspIncluder mainJspIncluder;
  private ButtonBar mainButtonBar;

  public DeletePreviewer deletePreviewer;

  public boolean multipart = false;

  public PageState(String url, SessionState sm) {
    if (url == null || url.trim().length() == 0)
      throw new PlatformRuntimeException("ViewHlp::buildView invalid parameters.");
    __threadLocalPageState.set(this);
    setHref(url);
    setSessionState(sm);
  }

  public void setFocusedObjectDomId(String domId) {
    focusedObjectDomId = domId;
  }


  public String getColor(String colorName) {
    return getI18n("COLOR_" + colorName);
  }

  /**
   * @deprecated use I8n.get(name)
   * @param name
   * @return
   */
  public String getI18n(String name) {
    return I18n.get(name);
  }


  public SessionState getSessionState() {
    return sessionState;
  }

  public void setSessionState(SessionState sessionState) {
    this.sessionState = (SessionState) sessionState;
  }

  public Identifiable getMainObject() {
    return mainObject;
  }

  public void setMainObject(Identifiable mainObject) {
    this.mainObject = mainObject;
  }

  public Object getAttribute(String key) {
    return attributes.get(key);
  }

  public void setAttribute(String key, Object value) {
    attributes.put(key, value);
  }


  /**
   * Gte the pageState from threadLocal
   * @return
   */
  public static PageState getCurrentPageState() {
    return __threadLocalPageState.get();
  }

  /**
   * @deprecated use without request getCurrentPageState(). DO NOT REMOVE FOR BACK COMPATIBILITY (e.g.: custom portlets)
    * @param request
   * @return
   */
  public static PageState getCurrentPageState(HttpServletRequest request) {
    return getCurrentPageState();
  }

  public PageState perform(HttpServletRequest request, HttpServletResponse response) {
    while (!screenAreas.empty()) {
      try {
        ScreenArea screenArea = (ScreenArea) screenAreas.pop();
        ActionController controller = screenArea.controller;
        runningControllerScreenArea = screenArea;
        if (controller != null) {
          controller.perform(request, response);
        }
      } catch (Exception e) {
        throw new PlatformRuntimeException(e);
      }
    }

    if (validEntries()) {
      ThreadLocalPersistenceContextCarrier carrier = PersistenceContext.threadLocalPersistenceContextCarrier.get();
      if (carrier != null) {
        for (PersistenceContext pc : carrier.persistenceContextMap.values()) {
          pc.checkPoint();
        }
      } else {
        System.out.println("Carrier is null");
      }
    }
    return this;
  }

  public void setForm(Form f) {
    this.form = f;
  }

  public Form getForm() {
    return form;
  }

  public void focusToHtml(PageContext pageContext) throws IOException {
    if (focusedObjectDomId != null)
      pageContext.getOut().print("<script>$(document).ready(function(){$('#" + focusedObjectDomId + "').focus()});</script>");
  }

  public PageState registerPart(ScreenArea screenArea) {
    this.screenAreas.push(screenArea);
    return this;
  }

  public String toString() {
    return "\nScreenBasic = " + rootScreen;
  }

  public void toHtml(PageContext pageContext) {
    if (rootScreen == null)
      throw new PlatformRuntimeException("PageState calling toHtml on rootScreen==null");
    if (!stopPageAfterController)
      rootScreen.toHtml(pageContext);
  }

  public static void buildPartsAndClientEntries(HttpServletRequest request, PageState pageState) {

    ClientEntries clientEntries = new ClientEntries();

    Enumeration parameters = request.getParameterNames();
    while (parameters.hasMoreElements()) {
      String paramName = (String) parameters.nextElement();
      if (paramName != null) {
        String viewPropertyValue = request.getParameter(paramName).trim();

        if (Commands.COMMAND.equals(paramName)) {
          pageState.setCommand(request.getParameter(paramName));

        } else if (paramName.equals(Fields.OBJECT_ID)) {
          pageState.setMainObjectId(viewPropertyValue);

        } else if (paramName.equals(Fields.POPUP)) {
          pageState.setPopup(true);

        } else {
          clientEntries.addEntry(new ClientEntry(paramName, StringUtilities.arrayToString(request.getParameterValues(paramName), ",").trim()));
        }
      }

    }
    pageState.setClientEntries(clientEntries);
  }

  public HeaderFooter getHeaderFooter() {
    if (headerFooter == null)
      headerFooter = new HeaderFooter(this);
    return headerFooter;
  }

  public Skin getSkin() {
    return sessionState.getSkin();
  }

  public Page getPage() {
    return pages.get("DEFAULT_PAGE");
  }

  public void setPage(Page page) {
    this.pages.put("DEFAULT_PAGE", page);
  }

  private void updateSeed(PageSeed newSeed) {
    this.href = newSeed.getHref();
    this.setMainObjectId(newSeed.getMainObjectId());
    setClientEntries(newSeed.getClientEntries());
    this.command = newSeed.getCommand();
    setLoginRequiring(newSeed.isLoginRequiring());
    setPopup(newSeed.isPopup());
  }

  /**
   * @see org.jblooming.waf.ScreenBasic changeBody(org.jblooming.waf.view.PageState, org.jblooming.waf.view.PageSeed)
   *      WARNING: do not use contextPath in newPageseed:
   *      CORRECT: newpageseed= new PageSeed("/commons/bblabla/xxx.jsp");
   *      NOT CORRECT: newpageseed= pagestate.pageFromCommonRoot("/bblabla/xxx.jsp");
   *      NOT CORRECT: newpageseed= pagestate.pageFromRoot("xxx.jsp");
   */
  public void redirect(PageSeed newSeed) {
    updateSeed(newSeed);
    final ScreenArea basicScreen = this.runningControllerScreenArea;
    ScreenRoot lw = basicScreen.parent;
    this.screenRunning = false;
    lw.urlToInclude = newSeed.href;
  }

  public void setApplication(Application application) {
    sessionState.setApplication(application);
  }

  public Application getApplication() {
    return sessionState.getApplication();
  }

  //the new pageSeed is created from history's one if matching
  // but the href is taken from the one passed as in case of pageState.redirect
  // you don't have to put the context path
  public void backTo(PageSeed filterByUrl) {
    PageSeed backTo = filterByUrl.getNewInstance();
    boolean found = false;
    for (int i = sessionState.pageHistory.history.size() - 1; i >= 0 && !found; i--) {
      String absoluteHref = sessionState.pageHistory.history.get(i).href;
      if (absoluteHref.endsWith(filterByUrl.href)) {
        backTo = sessionState.pageHistory.history.get(i).getNewInstance();
        backTo.href = filterByUrl.href;
        found = true;
        break;
      }
    }
    redirect(backTo);
  }

  public PageSeed thisPage(HttpServletRequest request) {
    int i = href.indexOf("?");
    if (i == -1)
      i = href.length();

    String newHref = href.substring(0, i);
    //if there is no path, do not touch it
    if (!(newHref.indexOf("/") == -1))
      newHref = request.getContextPath() + newHref;
    return new PageSeed(newHref);
  }

  public void setMainJspIncluder(JspIncluder jspIncluder) {
    this.mainJspIncluder = jspIncluder;
  }

  public JspIncluder getMainJspIncluder() {
    return mainJspIncluder;
  }

  public PageSeed pageInThisFolder(String page, HttpServletRequest request) {
    String realURI = HttpUtilities.realURI(request);
    String href = request.getContextPath() + realURI.substring(0, realURI.lastIndexOf("/")) + "/" + page;
    return new PageSeed(href);
  }

  /**
   * @param request
   * @return the pagesed representing the current part. It is used tipically in the webParts
   */
  public PageSeed pagePart(HttpServletRequest request) {
    String realURI = HttpUtilities.realURI(request);
    return new PageSeed(request.getContextPath() + realURI);
  }


  /**
   * @param page
   * @return the page from the root of web application
   */
  public PageSeed pageFromWebApp(String page) {
    if (!page.startsWith("/"))
      page = "/" + page;
    return new PageSeed(ApplicationState.contextPath + page);
  }


  /**
   * @param page
   * @return the page from the root of applications
   */
  public PageSeed pageFromApplications(String page) {
    if (!page.startsWith("/"))
      page = "/" + page;
    return new PageSeed(ApplicationState.contextPath + "/applications" + page);
  }

  /**
   * @param page
   * @return the page from root of current application
   */
  public PageSeed pageFromRoot(String page) {
    if (!page.startsWith("/"))
      page = "/" + page;
    String rootF = getApplication().getRootFolder();
    if (!rootF.startsWith("/"))
      rootF = "/" + rootF;
    return new PageSeed(ApplicationState.contextPath + rootF + page);
  }

  /**
   * @param page
   * @return the page from root of current application with context path
   */
  public PageSeed pageFromCommonsRoot(String page) {
    if (!page.startsWith("/"))
      page = "/" + page;
    return new PageSeed(ApplicationState.contextPath + "/commons" + page);
  }

  public void setButtonBar(ButtonBar bb2) {
    this.mainButtonBar = bb2;
  }

  public ButtonBar getButtonBar() {
    return mainButtonBar;
  }

  public boolean validEntries() {
    return getClientEntries().validEntries();
  }

  public ClientEntry getEntryOrDefault(String name) throws PersistenceException {
    ClientEntry ce = getEntry(name);
    ce.persistInOptions = true;
    if (ce.name == null) {
      Operator op = getLoggedOperator();
      String option = null;
      if (op!=null)
         option=op.getOptionOrDefault(name);
      if (option != null) {
        ce.name = name;
        ce.setValue(option);
        addClientEntry(ce);
      }
    }
    return ce;
  }

  public ClientEntry getEntryOrDefault(String name, String defaultValue) throws PersistenceException {
    ClientEntry ce = getEntryOrDefault(name);
    if (ce.stringValueNullIfEmpty() == null) {
      ce.name = name;
      ce.setValue(defaultValue);
      addClientEntry(ce);
    }
    return ce;
  }


  public void saveEntriesInDefaults() throws PersistenceException {
    Operator op = getLoggedOperator();
    if (op != null) {
      boolean changedSomeOption = false;
      for (ClientEntry ce : getClientEntriesSet()) {
        if (ce.persistInOptions) {

          String value = ce.stringValueNullIfEmpty();
          String optValue = op.getOption(ce.name);
          if (value != null && !value.equals(optValue)) {
            op.putOption(ce.name, ce.stringValueNullIfEmpty());
            changedSomeOption = true;
          } else if (value == null && optValue != null) {
            op.getOptions().remove(ce.name);
            changedSomeOption = true;
          }
        }
      }
      if (changedSomeOption) {
        op.store();
      }
    }
  }


  public void saveInHistory() {
    sessionState.pageHistory.saveInHistory(this);
  }

  public void saveInHistoryAndResetCommand() {
    command = null;
    sessionState.pageHistory.saveInHistory(this);
  }

  public boolean saveInHistoryIfNeeded() {
    boolean didSaveItHere = false;
    if (!isPopup() &&
            JSP.ex(command) &&
            !Commands.EDIT.equals(getCommand()) &&
            !Commands.FIND.equals(getCommand()) &&
            !Commands.SAVE.equals(getCommand()) &&
            !Commands.DELETE.equals(getCommand()) &&
            !Commands.DELETE_PREVIEW.equals(getCommand()) &&
            !Commands.ADD.equals(getCommand())
            ) {
      saveInHistory();
      didSaveItHere = true;
    }
    return didSaveItHere;
  }


  public PageSeed getNewInstance() {
    PageSeed ps = super.getNewInstance();
    // pageState differs satanically from pageSeed in that its href by default is not prefixed with context path
    ps.href = ApplicationState.contextPath + ps.href;
    return ps;
  }

  public String tokenCreate(String tokenName) {
    String token = StringUtilities.generatePassword(20);
    this.sessionState.setAttribute(tokenName, token);
    return token;
  }

  public static String TOKEN = "__tk";

  public void tokenCreate(String tokenName, PageSeed destination) {
    destination.addClientEntry(TOKEN, tokenCreate(tokenName));
  }

  public void tokenValidate(String tokenName) throws InvalidTokenException {
    tokenValidate(tokenName, true);
  }

  public void tokenValidate(String tokenName, boolean removeToken) throws InvalidTokenException {
    String token = sessionState.getAttribute(tokenName) + "";
    if (removeToken)
      sessionState.getAttributes().remove(tokenName);
    String s = getEntry(TOKEN).stringValueNullIfEmpty();
    if (!token.equals(s)) {
      addMessageError(getI18n("INVALID_SECURITY_TOKEN"));
      throw new InvalidTokenException("INVALID_SECURITY_TOKEN");
    }
  }

  public void tokenClone(String tokenName, PageSeed destination) {
    String sesTk = sessionState.getAttribute(tokenName) + "";
    destination.addClientEntry(TOKEN, sesTk);
  }

  public String tokenGetCurrent(String tokenName) {
    return sessionState.getAttribute(tokenName) + "";
  }

  public String tokenGetCurrent() {
    return this.getEntry(TOKEN).stringValueNullIfEmpty();
  }

  public void setError(String errorCode) {
    //create an error to force FCF to rollback transaction
    ClientEntry ce = new ClientEntry("__ERROR", "error");
    ce.errorCode=JSP.ex(ce)? errorCode :"JSON Error";
    addClientEntry(ce);
  }

//---------------------------------------------------- MESSAGES FROM CONTROLLER MANAGEMENT ------------ START ---------------------------------------

  public static enum MessageType {ERROR,WARNING,INFO, OK }


  public Message addMessageError(String message) {
    Message message1 = new Message(MessageType.ERROR, message);
    messagesFromController.add(message1);
    return message1;
  }

  public Message addMessageWarning(String message) {
    Message message1 = new Message(MessageType.WARNING, message);
    messagesFromController.add(message1);
    return message1;
  }

  public Message addMessageInfo(String message) {
    Message message1 = new Message(MessageType.INFO, message);
    messagesFromController.add(message1);
    return message1;
  }

  public Message addMessageOK(String message) {
    Message message1 = new Message(MessageType.OK, message);
    messagesFromController.add(message1);
    return message1;
  }

  public static class Message {
    public MessageType type;
    public String title;
    public String message;

    public Message(MessageType type, String message) {
      this.type = type;
      this.message = message;
    }
  }

//---------------------------------------------------- MESSAGES FROM CONTROLLER MANAGEMENT ------------ END ---------------------------------------


  public void resetLoggedOperator() {
    operator=null;
    getLoggedOperator();
  }

  public Operator getLoggedOperator() {
    try {
      if (sessionState.getOpid() != -1) {
        if (operator == null) {
          this.operator = (Operator) PersistenceHome.findByPrimaryKey(PlatformConfiguration.defaultOperatorSubclass, sessionState.getOpid());
        }
        return operator;
      } else
        return null;
    } catch (Throwable t) {
      throw new PlatformRuntimeException(t);
    }
  }
}


