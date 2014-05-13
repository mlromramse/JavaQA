package org.jblooming.waf.html.input;

import org.jblooming.waf.html.core.JspHelper;
import org.jblooming.waf.html.core.HtmlFinalizer;
import org.jblooming.waf.view.PageState;

import javax.servlet.jsp.PageContext;
import javax.servlet.ServletException;
import java.util.List;
import java.util.ArrayList;
import java.io.IOException;

/**
 * @author Roberto Bicchierai rbicchierai@open-lab.com
 *         Date: 16-feb-2006 : 15.05.54
 */
public class ColorValueChooser extends JspHelper implements HtmlFinalizer {

  /**
   * in case you want to use several ColorValueChooser on the same page, you will need to have distinct div in the initialize phase
   */  
  private String type = "STATUS";

  public String fieldName;

  /**
   * this is used in the hidden filed of this componente
   */
  public String script;

  public int height = 12;
  public int width= 100;

  public boolean showOpener=false;

  /**
   * list of color like "#ff6655"
   */
  public List<CodeColorValue> codeColorValues = new ArrayList();

  public boolean disabled = false;
  public boolean readOnly = false;
  public boolean displayValue = true;
  public String label;
  public String separator;
  public String htmlClass="formElements";
  public String onChangeScript;
  public boolean preserveOldValue = true;
  public String style;


  public ColorValueChooser(String fieldName, String type, PageState pageState) {
    this.urlToInclude = "/commons/layout/colorValueChooser/partColorValueChooser.jsp";
    this.fieldName = fieldName;
    this.type=type;
    pageState.htmlBootstrappers.add(this);
  }


  public void addCodeColorValue(String code, String color, String value){
    CodeColorValue ccv = new CodeColorValue();
    ccv.code = code;
    ccv.color = color;
    ccv.value=value;
    codeColorValues.add(ccv);
  }

   public String getDiscriminator() {
    return ColorValueChooser.class.getName()+type;
  }

  public boolean validate(PageState pageState) throws IOException, ServletException {
    return true;
  }

  public void init(PageContext pageContext) {
    PageState ps = PageState.getCurrentPageState();
    if (!ps.initedElements.contains(ColorValueChooser.class.getName())) {
      pageContext.getRequest().setAttribute(ACTION, INITIALIZE);
      super.toHtml(pageContext);
      ps.initedElements.add(ColorValueChooser.class.getName());
    }
  }

  public void toHtml(PageContext pageContext) {
    init(pageContext);
    pageContext.getRequest().setAttribute(ACTION, "VAI");
    super.toHtml(pageContext);
  }

  public void finalize(PageContext pageContext) {
    PageState ps = PageState.getCurrentPageState();
    if (!ps.initedElements.contains(getDiscriminator()+"FIN")) {
      pageContext.getRequest().setAttribute(ACTION, FINALIZE);
      super.toHtml(pageContext);
      ps.initedElements.add(getDiscriminator()+"FIN");
    }
    //To change body of implemented methods use File | Settings | File Templates.
  }


  public class CodeColorValue {
    public String code;
    public String color;
    public String value;

  }


  public String getType() {
    return type;
  }
}
