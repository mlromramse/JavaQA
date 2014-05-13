package org.jblooming.waf.html.core;

import org.jblooming.utilities.HashTable;
import org.jblooming.utilities.JSP;

import java.util.Map;


public class JspHelper extends JspIncluderSupport {

  public String id = DOM_ID + hashCode();

  public Map parameters = new HashTable();

  public String toolTip;
  protected String css_postfix;

  public JspHelper() {
  }

  public JspHelper(String urlToInclude) {
    this.urlToInclude = urlToInclude;
  }

  public String getToolTip() {
   return JSP.htmlEncodeApexes(toolTip);
 }

  public String generateToolTip() {
   return (JSP.ex(getToolTip()) ? "title=\""+getToolTip()+"\"":"");
 }

  public void setToolTip(String toolTip) {
    this.toolTip = toolTip;
  }

  public String getCssPostfix() {
    return JSP.w(css_postfix);
  }

  /**
   * @param css_postfix added as postfix to container css classes
   *                    Example: Css.containerContent+container.getCssPostfix()
   */
  public void setCssPostfix(String css_postfix) {
    this.css_postfix = css_postfix;
  }

}
