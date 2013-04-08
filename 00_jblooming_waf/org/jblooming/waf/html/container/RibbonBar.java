package org.jblooming.waf.html.container;

import org.jblooming.waf.html.core.JspHelper;

import java.util.List;



public class RibbonBar extends ButtonBar {

  public RibbonBar() {   
    super();
    align="left";
    buttonAreaHtmlClass = "ribbonBarArea";
    this.urlToInclude = "/commons/layout/partRibbonBar.jsp";
  }

  public void addButtons(List<? extends JspHelper> bs) {
    buttonList.addAll(bs);
  }

 
}