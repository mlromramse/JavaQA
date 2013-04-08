package org.jblooming.waf.html.core;

import org.jblooming.waf.view.PageState;

import javax.servlet.jsp.PageContext;
import javax.servlet.ServletException;
import java.io.IOException;


public interface HtmlFinalizer extends HtmlBootstrap{

  /**
   *  This method is called before validate. should be implemented when the component needs some extra script or something else at the end of the page
   * @param pageContext
   * @throws IOException
   * @throws ServletException
   */
  void finalize(PageContext pageContext);
  String FINALIZE = "JSPFINALIZE";
  

}
