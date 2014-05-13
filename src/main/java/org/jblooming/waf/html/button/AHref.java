package org.jblooming.waf.html.button;

import org.jblooming.waf.html.core.JspHelper;
import org.jblooming.waf.view.PageSeed;
import org.jblooming.utilities.JSP;

import javax.servlet.jsp.PageContext;
import java.io.IOException;


public class AHref extends JspHelper {
  public String href;
  public String label;
  public String target;


  public AHref(String label, String href) {
    this(label, href, null);
  }

  public AHref(String label, PageSeed ps) {
    this(label, ps.toLinkToHref(), null);
  }


  public AHref(String label, String href, String target) {
    this.href = href;
    this.label = label;
    this.target = target;
    this.urlToInclude = "NOTUSED";
  }

  public void toHtml(PageContext pageContext) {

    try {
      pageContext.getOut().print("<a href=\"");

      if (!JSP.ex(href))
        pageContext.getOut().print("#");
      else
        pageContext.getOut().print(href);
      pageContext.getOut().print("\" id=\"" + id + "\"");

      if (JSP.ex(target))
        pageContext.getOut().print(" target=\"" + target + "\"");


      pageContext.getOut().print(">" + label + "</a>");

    } catch (IOException e) {
    }

  }

  public ButtonSupport getButton(){
    ButtonSupport ret;
    if (href.startsWith("javascript:")){
      ret= new ButtonJS(label,href.substring(11));
    } else {
      ret= new ButtonLink(label,new PageSeed(href));
    }
    return ret;
  }


}
