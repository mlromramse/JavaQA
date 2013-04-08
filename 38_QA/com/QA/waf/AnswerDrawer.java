package com.QA.waf;

import com.QA.Answer;
import org.jblooming.waf.html.core.JspHelper;

import javax.servlet.jsp.PageContext;

public class AnswerDrawer extends JspHelper {

  public Answer answer;

  public boolean isFocused = false;


  public AnswerDrawer(Answer answer) {
    super();
    this.answer = answer;
    this.urlToInclude = "/applications/QA/parts/partAnswerDrawer.jsp";
  }

  public void toHtml(PageContext pageContext) {
    pageContext.getRequest().setAttribute(ACTION, "DRAW_ANSWER");
    super.toHtml(pageContext);
  }


}
