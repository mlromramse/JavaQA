package org.jblooming.waf.html.display;

import org.jblooming.waf.html.core.HtmlBootstrap;
import org.jblooming.waf.html.core.JspHelper;
import org.jblooming.waf.view.PageState;

import javax.servlet.ServletException;
import javax.servlet.jsp.PageContext;
import java.io.IOException;

public class Graph extends JspHelper implements HtmlBootstrap {


  public String fieldName;  //generatef [fieldName]_values [fieldName]_config [fieldName]_url

  public String values="";
  public String configuration="";
  public String dataUrl="";
  public String additionalOnclickScriptOnEdit;

  public boolean editableData =true;
  public boolean editableConfig =true;
  public int maxSize = 0;


  public Graph(String fieldName) {
    this.fieldName=fieldName;
    this.urlToInclude = "/commons/layout/graph/partGraph.jsp";
  }

  public String getDiscriminator() {
    return Graph.class.getName();
  }

  public boolean validate(PageState pageState) throws IOException, ServletException {
    return true;
  }

  public void init(PageContext pageContext) {
    PageState ps = PageState.getCurrentPageState();
    if (!ps.initedElements.contains(getDiscriminator())) {
      pageContext.getRequest().setAttribute(ACTION, INITIALIZE);
      super.toHtml(pageContext);
      ps.initedElements.add(getDiscriminator());
    }
  }

  public void toHtml(PageContext pageContext) {

    init(pageContext);

    pageContext.getRequest().setAttribute(ACTION, "DRAW" );
    super.toHtml(pageContext);
  }


}
