package org.jblooming.waf.html.container;

import org.jblooming.waf.html.core.JspHelper;
import org.jblooming.waf.html.core.JspIncluder;
import org.jblooming.waf.view.PageState;

import javax.servlet.jsp.PageContext;
import java.util.LinkedList;
import java.util.List;

public class DivOnMouseover extends JspHelper {

  public List<JspIncluder> buttonList = new LinkedList<JspIncluder>();
  public JspIncluder content;
  public JspIncluder opener;

 public static final String DRAW="DRAW";

  public DivOnMouseover(JspIncluder rolloverElement) {
    this(null,rolloverElement);
  }

  public DivOnMouseover(JspIncluder content, JspIncluder rolloverElement) {
    this.content = content;
    this.opener = rolloverElement;
    this.urlToInclude = "/commons/layout/partDivOnMouseover.jsp";
  }

  public void init(PageContext pageContext) {
    PageState ps = PageState.getCurrentPageState();
    if (!ps.initedElements.contains(DivOnMouseover.class.getName())) {
      pageContext.getRequest().setAttribute(ACTION, INITIALIZE);
      super.toHtml(pageContext);
      ps.initedElements.add(DivOnMouseover.class.getName());
    }
  }

  public void addButton(JspIncluder element){
    buttonList.add(element);
  }

  public void addButtons(List<? extends JspHelper> bs) {
    buttonList.addAll(bs);
  }

  public void toHtml(PageContext pc) {
    if (content!=null ||buttonList.size()>0 ){
      init(pc);
      pc.getRequest().setAttribute(ACTION, DRAW);
      super.toHtml(pc);
    }

  }

}
