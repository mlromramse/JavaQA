package org.jblooming.waf;

import net.sf.json.JSONObject;
import org.jblooming.utilities.JSP;
import org.jblooming.ApplicationRuntimeException;
import org.jblooming.waf.view.PageState;
import org.jblooming.waf.settings.I18n;
import org.jblooming.tracer.Tracer;

import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.JspWriter;
import java.io.IOException;

public class JSONHelper {
  public JSONObject json;
  public PageState pageState= PageState.getCurrentPageState();

  public JSONHelper(){
    json = new JSONObject();
    json.element("ok", true);
  }
  
  
  public void error(Throwable t){
    Tracer.platformLogger.error(t);
    Tracer.platformLogger.error(ApplicationRuntimeException.getStackTrace(t));

    pageState.setError("JSON Error"); //force FCF to rollback

    JSONObject ret = new JSONObject();
    ret.element("ok", false);

    String message = I18n.get("ERROR_APOLOGIES") + "\n";

    if (JSP.ex(t.getMessage()))
      message += I18n.get(t.getMessage());
    else
      message += I18n.get("ERROR_GENERIC_EXCEPTION");

    ret.element("message", message);

    json = ret;
  }


  public void close(PageContext pageContext) throws IOException {
    // JSONP OBJECT
    JspWriter out = pageContext.getOut();
    if (JSP.ex(pageState.getEntry("__jsonp_callback"))) {
      out.print(pageState.getEntry("__jsonp_callback").stringValueNullIfEmpty() + "(");
      out.print(json.toString());
      out.print(");");

      // JSON OBJECT
    } else {
      out.print(json.toString());
    }

  }


}
