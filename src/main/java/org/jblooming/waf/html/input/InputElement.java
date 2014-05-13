package org.jblooming.waf.html.input;

import org.jblooming.utilities.JSP;
import org.jblooming.waf.settings.I18n;
import org.jblooming.waf.view.ClientEntry;
import org.jblooming.waf.view.PageState;

import javax.servlet.jsp.PageContext;

/**
 * @author Pietro Polsinelli ppolsinelli@open-lab.com
 * @author Roberto Bicchierai rbicchierai@open-lab.com
 */
public abstract class InputElement extends HtmlElement {

  public String label;
  public boolean translateLabel = false;
  public boolean showLabel = true;

  public String separator;

  public String fieldName;
  public int fieldSize;

  protected ClientEntry value;

  public String labelClass;
  public String fieldClass = "formElements";

  public boolean fieldValueHasToBeEncoded = true;
  public boolean required = false;

  /**
   * @deprecated use !preserveOldValue
   */
  public boolean excludeFromAlert = false;
  public int tabIndex;
  public boolean readOnly = false;

  public String script;

  public boolean preserveOldValue = true;  

  /**
   * key controls
   */
  public String actionListened;
  public int keyToHandle;
  public boolean checkCtrlKey=false;
  public String launchedJsOnActionListened;

  public static enum EntryType {INTEGER, DOUBLE, TIME, PERCENTILE, CURRENCY, DURATIONDAYS, URL, EMAIL, DURATIONMILLIS, DATE  };


  public void toHtml(PageContext pageContext) {

    PageState ps = PageState.getCurrentPageState();
    value = ps.getEntry(fieldName);

    super.toHtml(pageContext);

    if (value != null && value.errorCode != null)
      JSP.feedbackError(value, pageContext);
  }

  /**
   * Handling of DOM events relatively to this control
   *
   * @param keyToHandle    the int code of the key
   * @param launchedJs     on pressing the key, this js will be launched
   * @param actionListened the code is the event listened e.g. keypress
   */
  public void addKeyPressControl(int keyToHandle, String launchedJs, String actionListened) {
    this.keyToHandle = keyToHandle;
    this.launchedJsOnActionListened = launchedJs;
    this.actionListened = actionListened;
  }


   public void toHtmlI18n(PageContext pageContext) {
     PageState pageState = PageState.getCurrentPageState();
     if (label==null)
       label = I18n.get(fieldName);
     else
      label = I18n.get(label);
     toHtml(pageContext);
   }

  public void setValue(ClientEntry value) {
    this.value = value;
  }


}

