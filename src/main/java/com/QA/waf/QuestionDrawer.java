package com.QA.waf;

import com.QA.Question;
import org.jblooming.waf.html.core.JspHelper;

import javax.servlet.jsp.PageContext;

public class QuestionDrawer extends JspHelper {

  public Question question;

  public boolean isFocused = false;


  public QuestionDrawer(Question question) {
    super();
    this.question = question;
    this.urlToInclude = "/applications/QA/parts/partQuestionDrawer.jsp";
  }

  public void toHtml(PageContext pageContext) {
    pageContext.getRequest().setAttribute(ACTION, "DRAW_QUESTION");
    super.toHtml(pageContext);
  }


  public void toHtmlCompact(PageContext pageContext) {
    pageContext.getRequest().setAttribute(ACTION, "DRAW_QUESTION_COMPACT");
    super.toHtml(pageContext);
  }

  public void toHtmlSimple(PageContext pageContext) {
    pageContext.getRequest().setAttribute(ACTION, "DRAW_QUESTION_SIMPLE");
    super.toHtml(pageContext);
  }


}
