package org.jblooming.waf.html.container;

import org.jblooming.waf.constants.Commands;
import org.jblooming.waf.html.button.ButtonSupport;
import org.jblooming.waf.html.core.JspHelper;
import org.jblooming.waf.html.display.Img;
import org.jblooming.waf.view.PageState;

import javax.servlet.ServletException;
import javax.servlet.jsp.PageContext;
import java.io.IOException;

public class Tab extends JspHelper {

  boolean openTabCalled;
   boolean closeTabCalled;

  public String domId = "d" + hashCode();
  public String id;
  public String caption;
  public boolean focused = false;
//  public PageSeed variationsFromForm = new PageSeed();
  public boolean enabled = true;

  public TabSet tabSet;
  public final static String START = "START";
  public final static String END = "END";

  public ButtonSupport button = null;
  public String additionalScript;

  //added icon on tab
  //todo public enum ImgPosition { LEFT, RIGHT }
  //public ImgPosition imgPosition=ImgPosition.LEFT;
  public Img imgTab ;
  
//  public boolean doSubmit=false;

  /**
   * @deprecated give an id
   */
  public Tab(String caption) {
    this.caption=caption;
  }

  public Tab(String id, String caption) {
    this.domId = id;
    this.id = id;
    this.caption=caption;
  }

  public Tab(String id,ButtonSupport bs) {
    this.domId = id;
    this.id = id;
    this.button=bs;
    this.caption = bs.label;
  }


  public void start(PageContext pageContext)  {
    pageContext.getRequest().setAttribute(Commands.COMMAND, START);
    toHtml(pageContext);
    openTabCalled = true;
  }

  public void end(PageContext pageContext) {
    pageContext.getRequest().setAttribute(Commands.COMMAND, END);
    toHtml(pageContext);
    closeTabCalled = true;
  }

  public void focusedIfNoneFocused(TabSet ts, PageState pageState) {
    if (pageState.getEntry(ts.id).stringValueNullIfEmpty()==null)
      this.focused = true;
  }
}
