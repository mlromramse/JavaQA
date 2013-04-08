package org.jblooming.waf.html.container;

import org.jblooming.persistence.exceptions.PersistenceException;
import org.jblooming.tracer.Tracer;
import org.jblooming.waf.constants.Commands;
import org.jblooming.waf.html.button.ButtonJS;
import org.jblooming.waf.html.core.HtmlBootstrap;
import org.jblooming.waf.html.core.JspHelper;
import org.jblooming.waf.html.layout.Skin;
import org.jblooming.waf.html.state.Form;
import org.jblooming.waf.settings.I18n;
import org.jblooming.waf.view.PageSeed;
import org.jblooming.waf.view.PageState;

import javax.servlet.ServletException;
import javax.servlet.jsp.PageContext;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class TabSet extends JspHelper implements HtmlBootstrap {

  public static final String init = TabSet.class.getName();

  public Skin skin;
  boolean openTabBarCalled;
  boolean closeTabBarCalled;

  public boolean wideTabs;
  
  /**
   * if true first tab is indented
   */
  public boolean indentTab=true;

  boolean jsEmitted = false;

  public String style = null;

  public Form form;

  LinkedList<Tab> tabset=new LinkedList<Tab>();

  PageState pageState;

  public final static String BAR = "TABSET_BAR";
  public final static String END = "TABSET_END";
  public static final String EMIT_JS = "EMIT_JS";

  public TabSet(String id, PageState pageState) {
    this.id = id;
    this.skin = pageState.getSkin();
    this.urlToInclude = "/commons/layout/tabSet/partTabSet.jsp";
    this.pageState = pageState;
  }

  public TabSet(Form form, PageState pageState) {
    this("tabSetId", pageState);
    this.form = form;
  }

  private void init(PageContext pageContext) {
    PageState ps = PageState.getCurrentPageState();
    if (!ps.initedElements.contains(init)) {
      pageContext.getRequest().setAttribute(Commands.COMMAND, INITIALIZE);
      super.toHtml(pageContext);
      ps.initedElements.add(init);
    }
  }

  public void drawBar(PageContext pageContext) {
    init(pageContext);
    pageContext.getRequest().setAttribute(Commands.COMMAND, BAR);
    toHtml(pageContext);
    openTabBarCalled = true;
  }

  public void end(PageContext pageContext) {
    pageContext.getRequest().setAttribute(Commands.COMMAND, END);
    toHtml(pageContext);
    closeTabBarCalled = true;
  }


  public void addTab(Tab tab) {
    tab.tabSet = this;
    tab.domId = this.id + tab.id;
    tab.urlToInclude = this.urlToInclude;
    if (!tab.focused)
      tab.focused = (this.id+tab.id).equals(pageState.getEntry(this.id).stringValueNullIfEmpty());
    tabset.add(tab);
  }

  public Iterator iterator() {
    if (tabset != null)
      return tabset.iterator();
    else
      return null;
  }

  public int tabSetSize() {
    if (tabset != null)
      return tabset.size();
    else
      return 0;
  }

  public Tab getTab(String tabId){
    for (Tab tab:tabset){
      if (tab.id.equals(tabId))
        return tab;
    }
    return null;
  }

  public Tab getTabByDomId(String focusedTabDomId) {
    for (Tab tab:tabset){
      if (tab.domId.equals(focusedTabDomId))
        return tab;
    }
    Tracer.platformLogger.error("No tab found with id "+focusedTabDomId);
    return null;
  }

  public String getDiscriminator() {
    return id;
  }

  public boolean validate(PageState pageState) throws IOException, ServletException {
    boolean result = false;
    for (Iterator iterator = tabset.iterator(); iterator.hasNext();) {
      Tab tab = (Tab) iterator.next();
      result = tab.openTabCalled && tab.closeTabCalled;
      if (!result)
        break;
    }

    return result && openTabBarCalled && closeTabBarCalled;
  }

  public Tab setFocusedTab(String focusedId) {
    Tab tab = null;
    Tab result = null;
    for (Iterator iterator = tabset.iterator(); iterator.hasNext();) {
      tab = (Tab) iterator.next();
      if (tab.domId.equals(focusedId)) {
        tab.focused = true;
        result = tab;
      } else {
        tab.focused = false;
      }
    }
    return result;
  }

  public ButtonJS getShowTabButton(Tab tab2) {
    ButtonJS showTabButton = new ButtonJS();
    showTabButton.alertOnChange = false;
    showTabButton.onClickScript = "setTabHiddenValue('"+tab2.domId+"','"+tab2.domId+"');hideTabsAndShow('"+tab2.domId+"','"+tab2.domId+"')";
    showTabButton.label = I18n.get("NEXT");
    return showTabButton;
  }


  /**
   * should be use in partTabset.jsp only
   *
   * How the focused tab is determined? using following priority
   *
   * 1) if there is the client entry it win ever!
   *
   * 2) else if there is a tab selected, it will be focused
   *
   * 3) then if the tabset is sortable and user-sorted, first in order is shown
   *
   * 4) nada de nada? the first one!
   *
   */
  public Tab setFocusedTab(PageState pageState) throws PersistenceException {

    Tab focusedTab=null;

    // this force the selected tab using the CE
    String focusedTabFromCE = pageState.getEntry(id).stringValueNullIfEmpty();
    if (focusedTabFromCE!=null) {
      focusedTab=setFocusedTab(focusedTabFromCE);
    }

    // no tab focused from CE?
    if (focusedTab==null) {

      // there is a tab focused by tab.focused=true?
      focusedTab=getFocusedTabByTab();

      // no tab focused directly?
      if (focusedTab==null){

        // is still null? then focus on first one
        if (focusedTab==null){
          focusedTab=tabset.get(0);
        }
      }
    }

    //set tab as focused
    if (focusedTab!=null)
      focusedTab.focused=true;


    return focusedTab;
  }

  private Tab getFocusedTabByTab() {
    Tab ret=null;
    for (Tab tab:tabset){
      if (tab.focused){
        ret=tab;
        break;
      }
    }
    return ret;
  }

  public Tab addLazyLoadInstance(String id, String label, PageSeed urlToInclude) {

    ButtonJS loadAiax = new ButtonJS();
    loadAiax.label = label;
    loadAiax.onClickScript = "loadTabSet('" + urlToInclude.toLinkToHref() + "','" + this.id+id + "');";
    Tab tab = new Tab(id,loadAiax);
    this.addTab(tab);
    return tab;
  }

  public static void pointToTab(String tabSetId, String tabId, PageSeed pointer) {
    pointer.addClientEntry(tabSetId,tabSetId+tabId);
  }

  public static String pointToTab(String tabSetId, String tabId) {
    return "&"+tabSetId+"="+tabSetId+tabId;
  }

  public void sortTabs(List<String> tabSetSortedNames) {

    LinkedList<Tab> newSort = new LinkedList<Tab>();
    for (String tabSetSortedName : tabSetSortedNames) {
      for (Tab tab : tabset) {
        if (tab.id.equals(tabSetSortedName)) {
          newSort.add(tab);
          break;
        }
      }
    }
    tabset.removeAll(newSort);
    newSort.addAll(tabset);
    tabset = newSort;
  }


}