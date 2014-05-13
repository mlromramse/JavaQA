package org.jblooming.waf.html.display;

import org.jblooming.PlatformRuntimeException;
import org.jblooming.utilities.JSP;
import org.jblooming.waf.html.input.HtmlElement;
import org.jblooming.waf.view.PageState;

import javax.servlet.jsp.PageContext;
import java.io.IOException;

/**
 * @author Pietro Polsinelli : ppolsinelli@open-lab.com
 */
public class FeedbackError extends HtmlElement {
  public boolean translateError;
  public String errorCode;
  public String suggestedValue;

  public FeedbackError() {

  }

  public void toHtml(PageContext pageContext) {

    if (errorCode != null && errorCode.length() > 0) {
      try {
        PageState ps = PageState.getCurrentPageState();

        pageContext.getOut().write("<span id=\""+id+"error\" class=\"errImg\">&nbsp;");

        if (suggestedValue != null && suggestedValue.length() > 0)
          errorCode = errorCode + ' ' + suggestedValue;

        pageContext.getOut().write("<a style=\"cursor:pointer;\" onclick=\"alert('"); // change by graziella - href='#' non funziona con i popup, viene fatto un submit sulla pagina che ha richiamato il popup
        if (translateError) {
          errorCode = ps.getI18n(errorCode);
        }
        String errorCodeAlert = JSP.javascriptEncode(errorCode);
        pageContext.getOut().write(errorCodeAlert + "\\n");
        pageContext.getOut().write("')\" >");

        Img i = new Img(ps.sessionState.getSkin().imgPath + "alert.png", "alert");
        i.toolTip = errorCodeAlert;
        //i.width = "17";
        //i.height = "17";
        i.script = " align='absmiddle'";
        i.translateToolTip = translateError;
        i.toHtml(pageContext);
        pageContext.getOut().write("</a></span>\n");

      } catch (IOException e) {
        throw new PlatformRuntimeException(e);

      }
    }


  }

  public StringBuffer toHtmlStringBuffer() {
    throw new PlatformRuntimeException("Do not use this");
  }
}
