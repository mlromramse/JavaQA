package org.jblooming.waf.html.display;

import org.jblooming.waf.html.input.InputElement;
import org.jblooming.waf.view.PageState;

import javax.servlet.jsp.PageContext;

/**
 * @deprecated 
 */
public class TextDisplay extends InputElement {

  public String fieldValue;


  public TextDisplay(String label) {
    this.label = label;
    translateLabel = true;
  }

  /**
   * this i18ns by default
   */
  public TextDisplay(String label, String separator, String value) {
    this.label = label;
    this.separator = separator;
    this.fieldValue = value;
    translateLabel = true;
  }

  public StringBuffer toHtmlStringBuffer() {
    StringBuffer sb = new StringBuffer(512);

    if (labelClass != null)
      sb.append("<span class=\"" + labelClass + "\">");

    if (label != null)
      sb.append(label);

    if (labelClass != null)
      sb.append("</span>");

    if (separator != null)
      sb.append(separator);

    if (fieldClass != null)
      sb.append("<span class=\"" + fieldClass + "\">");

    if (fieldValue != null)
      sb.append(fieldValue);

    if (fieldClass != null)
      sb.append("</span>");

    return sb;
  }

  public void toHtml(PageContext pageContext) {

    if (translateLabel) {
      PageState ps = PageState.getCurrentPageState();
      label = ps.getI18n(label);
    }
    super.toHtml(pageContext);

  }


}
