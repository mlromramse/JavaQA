package org.jblooming.waf.html.input;

import org.jblooming.waf.html.core.JspHelper;
import org.jblooming.waf.settings.ApplicationState;
import org.jblooming.waf.view.PageState;

import javax.servlet.jsp.PageContext;
import java.io.Serializable;


/**
 * @author Roberto Bicchierai rbicchierai@open-lab.com
 *         Date: 23-feb-2007 : 15.22.38
 */
public class UrlFileStorage extends JspHelper {

  public boolean readOnly = false;
  public boolean downloadOnly = false;
  public String initialValue;
  public static final String DRAW = "DRAW";
  public String fieldName;

  public String explorerPart = ApplicationState.contextPath + "/commons/document/explorer.jsp";
  public String popupPart = ApplicationState.contextPath + "/commons/document/popUpFileStorage.jsp";
  public String separator = "&sbsp;";
  public String label;
  public Serializable referralObjectId;


  public UrlFileStorage(String fieldName) {
    this.urlToInclude = "/commons/layout/partUrlFileStorage.jsp";
    this.fieldName = fieldName;
    this.id = fieldName;
  }

  public String getDiscriminator() {
    return UrlFileStorage.class.getName();
  }


  public void init(PageContext pageContext) {
    PageState ps = PageState.getCurrentPageState();
    if (!ps.initedElements.contains(getDiscriminator())) {
      ps.initedElements.add(getDiscriminator());
      pageContext.getRequest().setAttribute(ACTION, INITIALIZE);
      super.toHtml(pageContext);
    }
  }


  public void toHtml(PageContext pageContext) {
    init(pageContext);
    pageContext.getRequest().setAttribute(ACTION, DRAW);
    super.toHtml(pageContext);
  }


}
