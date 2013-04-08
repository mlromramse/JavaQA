package org.jblooming.waf.html.display;

import org.jblooming.ontology.Node;
import org.jblooming.ontology.PerformantNodeSupport;
import org.jblooming.waf.html.button.ButtonSupport;
import org.jblooming.waf.html.core.JspHelper;
import org.jblooming.waf.html.core.HtmlBootstrap;
import org.jblooming.waf.view.PageSeed;
import org.jblooming.waf.view.PageState;
import org.jblooming.PlatformRuntimeException;
import org.jblooming.security.Permission;

import javax.servlet.jsp.PageContext;
import java.lang.reflect.Method;
import java.util.Comparator;

/**
 * @author Pietro Polsinelli ppolsinelli@open-lab.com
 * @author Roberto Bicchierai rbicchierai@open-lab.com
 */
public class PathToObject extends JspHelper implements HtmlBootstrap {

  public PerformantNodeSupport node;
  public PageSeed destination;
  public ButtonSupport rootDestination;
  public String separator = "/";
  public boolean useParentIfNew = false;
  public Class mainClass;

  public Comparator comparator;

  public Permission canClick;

  public static final String init = PathToObject.class.getName();
  public static final String DRAW = "DRAW";
  public static final String CLOSE = "CLOSE";
  private boolean drawCalled=false;
  private boolean closeCalled=false;


  public PathToObject(PerformantNodeSupport node) throws NoSuchMethodException {
    this.node = node;
    urlToInclude = "/commons/layout/pathToObject/partPathToObject.jsp";
  }

  public void useParentIfNew(Class mainClass) {
    useParentIfNew = true;
    this.mainClass = mainClass;
  }

  public String getDiscriminator() {
    return init;
  }

  private void init(PageContext pageContext)  {
    PageState ps = PageState.getCurrentPageState();
    if (!ps.initedElements.contains(init)) {
      pageContext.getRequest().setAttribute(ACTION, INITIALIZE);
      super.toHtml(pageContext);
      ps.initedElements.add(init);
    }
  }

  public void draw(PageContext pageContext)  {
    drawCalled = true;
    init(pageContext);
    pageContext.getRequest().setAttribute(ACTION, DRAW);
    super.toHtml(pageContext);
  }

  public boolean validate(PageState pageState) {
    return drawCalled && closeCalled;
  }

  public void close(PageContext pageContext){
    if (!drawCalled)
      throw new PlatformRuntimeException("Call start before end");
    closeCalled = true;
    pageContext.getRequest().setAttribute(ACTION, CLOSE);
    super.toHtml(pageContext);
  }

  public void toHtml(PageContext pageContext) {
      this.draw(pageContext);
      this.close(pageContext);
  }



}
