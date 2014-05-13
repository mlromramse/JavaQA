package org.jblooming.waf;

import org.jblooming.PlatformRuntimeException;
import org.jblooming.utilities.JSP;
import org.jblooming.waf.constants.Fields;
import org.jblooming.waf.constants.Commands;
import org.jblooming.waf.settings.Application;
import org.jblooming.waf.view.PageState;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;
import java.lang.reflect.Constructor;


public abstract class ScreenBasic extends ScreenRoot {

  public boolean showHeaderAndFooter = true;
  public ScreenArea menu;
  public ScreenArea partPathToObject;

  protected ScreenBasic() {
  }


  /**
   * @param body
   */
  public ScreenBasic(ScreenArea body) {
    super();
    this.setBody(body);
    PageState pageState = PageState.getCurrentPageState();
    SessionState sessionState = pageState.sessionState;

    if (!pageState.isPopup() && (Commands.EDIT.equals(pageState.getCommand()) || Commands.FIND.equals(pageState.getCommand()) || !JSP.ex(pageState.getCommand()))) {
      pageState.saveInHistory();
    }

    //if not coming from back, go to the end of history
    if (
            !pageState.isPopup() &&
                    !Fields.TRUE.equals(pageState.getEntry("THIS_PAGE_RECORDED").stringValueNullIfEmpty()) &&
                    !Fields.TRUE.equals(pageState.getEntry("BACK_REDIRECTED").stringValueNullIfEmpty()) &&
                    //attempt to avoid back-back 
                    !Commands.SAVE.equals(pageState.command)
            )
      sessionState.pageHistory.whereAmI = sessionState.pageHistory.history.size();
  }


  public String toString() {
    return super.toString() + "\nbody = " + body + "\nmenu = " + menu + "\npartPathToObject = " + partPathToObject + "\n\n";
  }

  public abstract void initialize(ScreenArea body);


  public static ScreenBasic getInstance(Application current, ScreenArea body, PageContext pageContext) {
    try {
      Constructor constructor = null;
      try {
        constructor = current.getDefaultScreenClass().getConstructor(ScreenArea.class, PageContext.class);
        return (ScreenBasic) constructor.newInstance(body, pageContext);
      } catch (NoSuchMethodException e) {
        return (ScreenBasic) current.getDefaultScreenClass().newInstance();
      }

    } catch (Exception e) {
      throw new PlatformRuntimeException(e);
    }

  }

  public static ScreenBasic preparePage(PageContext pageContext) {
    return preparePage(null, pageContext);
  }

  public static ScreenBasic preparePage(ActionController ac, PageContext pageContext) {
    HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
    final ScreenArea body = new ScreenArea(ac, request);
    PageState pageState = PageState.getCurrentPageState();
    pageState.screenRunning = true;
    ScreenBasic lw = ScreenBasic.getInstance(pageState.sessionState.getApplication(), body, pageContext);
    lw.initialize(body);
    lw.register(pageState);
    return lw;
  }

}
