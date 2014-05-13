package org.jblooming.waf.html.menu;

import org.jblooming.waf.html.core.JspHelper;
import org.jblooming.waf.view.PageState;
import org.jblooming.waf.view.PageSeed;

import javax.servlet.jsp.PageContext;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.ArrayList;

public class MenuPlus extends JspHelper {

  public static final String init = MenuPlus.class.getName();

  public static final String DRAW_ROOT = "DRAW_ROOT";
  public static final String DRAW_CONTENT = "DRAW_CONTENT";

  public String drawer;
  public List<MenuPlusElement> roots = new ArrayList();
  public List<MenuPlusElement> elements = new ArrayList();

  public String width;

  public static enum Type {
    TITLE, ACTION, SEPARATOR, LINK, SUBMENU
  }

  public MenuPlus() {
    urlToInclude = "/commons/layout/menuPlus/partMenuPlus.jsp";
  }

  private void init(PageContext pageContext) {
    PageState ps = PageState.getCurrentPageState();
    if (!ps.initedElements.contains(init)) {
      pageContext.getRequest().setAttribute(ACTION, INITIALIZE);
      super.toHtml(pageContext);
      ps.initedElements.add(init);
    }
  }

  public MenuPlusElement addRoot(String id, String label, String imgPath) {
    MenuPlusElement mpe = new MenuPlusElement();
    mpe.id = id;
    mpe.imgPath = imgPath;
    mpe.tooltip = label;
    roots.add(mpe);
    return mpe;
  }

  public MenuPlusElement addTitle(String title) {
    MenuPlusElement mpe = new MenuPlusElement();
    mpe.type = Type.TITLE;
    mpe.tooltip = title;
    elements.add(mpe);
    return mpe;
  }

  public MenuPlusElement addSubmenuLine(String label) {
    MenuPlusElement mpe = new MenuPlusElement();
    mpe.type = Type.SUBMENU;
    mpe.tooltip = label;
    mpe.openSubMenu=true;
    elements.add(mpe);
    return mpe;
  }

  public MenuPlusElement addContentLine(String label, PageSeed pageSeed) {
    MenuPlusElement mpe = new MenuPlusElement();
    mpe.type = Type.LINK;
    mpe.tooltip = label;
    mpe.pageSeed = pageSeed;
    elements.add(mpe);
    return mpe;
  }

  public MenuPlusElement addContentLine(String label, String href) {
    MenuPlusElement mpe = new MenuPlusElement();
    mpe.type = Type.LINK;
    mpe.tooltip = label;
    mpe.href = href;
    elements.add(mpe);
    return mpe;
  }

  public MenuPlusElement addContentLine(String label, String imgPath, String href) {
    MenuPlusElement mpe = new MenuPlusElement();
    mpe.type = Type.LINK;
    mpe.tooltip = label;
    mpe.href = href;
    mpe.imgPath = imgPath;
    elements.add(mpe);
    return mpe;
  }

   public MenuPlusElement addContentScript(String label, String script) {
    MenuPlusElement mpe = new MenuPlusElement();
    mpe.type = Type.ACTION;
    mpe.tooltip = label;
    mpe.script = script;
    elements.add(mpe);
    return mpe;
  }

  public MenuPlus addSeparator() {
    MenuPlusElement mpe = new MenuPlusElement();
    mpe.type = Type.SEPARATOR;
    elements.add(mpe);
    return this;
  }


  public void drawRoot(PageContext pageContext) {
    init(pageContext);
    HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
    request.setAttribute(ACTION, DRAW_ROOT);
    PageState state = PageState.getCurrentPageState();
    state.sessionState.setAttribute("CURRENTURL",request.getContextPath()+state.toLinkToHref());
    super.toHtml(pageContext);
  }

  public void drawContent(PageContext pageContext) {
    pageContext.getRequest().setAttribute(ACTION, DRAW_CONTENT);
    super.toHtml(pageContext);
  }

  public void toHtml(PageContext pageContext) {
    throw new RuntimeException("Call drawRoot and drawContent");
  }


  public class MenuPlusElement {
    public Type type;
    public String id;
    public String imgPath;
    public String tooltip;
    public String label;
    public String script;
    public String href;
    public boolean popup=false;
    public PageSeed pageSeed;
    public boolean openSubMenu=false;
  }

}
