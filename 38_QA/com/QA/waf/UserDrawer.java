package com.QA.waf;

import com.QA.QAOperator;
import org.jblooming.waf.html.core.JspHelper;

import javax.servlet.jsp.PageContext;

public class UserDrawer extends JspHelper {

  public QAOperator QAOperator;
  public int gravatarSize=100;
  public boolean printFull =false;

  public UserDrawer(QAOperator QAOperator,boolean printFull,int gravatarSize) {
    this(QAOperator);
    this.printFull = printFull;
    this.gravatarSize=gravatarSize;
  }

  public UserDrawer(QAOperator QAOperator,boolean printFull) {
    this(QAOperator);
    this.printFull = printFull;
  }

  public UserDrawer(QAOperator QAOperator,int gravatarSize) {
    this(QAOperator);
    this.gravatarSize=gravatarSize;
  }

  public UserDrawer(QAOperator QAOperator) {
    super();
    this.QAOperator = QAOperator;
    this.urlToInclude = "/applications/QA/parts/partUserDrawer.jsp";
  }

  public void toHtml(PageContext pageContext) {
    pageContext.getRequest().setAttribute(ACTION, "DRAW_USER");
    super.toHtml(pageContext);
  }


}

