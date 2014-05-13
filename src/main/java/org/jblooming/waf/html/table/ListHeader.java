package org.jblooming.waf.html.table;

import org.jblooming.oql.QueryHelper;
import org.jblooming.utilities.JSP;
import org.jblooming.waf.exceptions.ActionException;
import org.jblooming.waf.html.button.ButtonSubmit;
import org.jblooming.waf.html.core.HtmlBootstrap;
import org.jblooming.waf.html.core.JspHelper;
import org.jblooming.waf.html.display.Img;
import org.jblooming.waf.html.state.Form;
import org.jblooming.waf.settings.I18n;
import org.jblooming.waf.view.PageState;

import javax.servlet.jsp.PageContext;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles drawing of sortable list headers. Relies on template
 * <p/>
 * <a href="/commons/layout/partCollector.jsp">partListHeader.jsp</a>
 * <p/>
 * There is a complete example of usage in
 * <p/>
 * <a href="/test/testCollector.jsp">/test/testTable.jsp</a>
 *
 * @author Pietro Polsinelli ppolsinelli@open-lab.com
 */
public class ListHeader extends JspHelper implements HtmlBootstrap {
  private List headers = new ArrayList();
  public Form form;
  public QueryHelper queryHelper;

  public String ASC_TO_DESC_TITLE = "ASC_TO_DESC_TITLE";
  public String DESC_TO_UNORD_TITLE = "DESC_TO_UNORD_TITLE";
  public String TO_ASC_TITLE = "TO_ASC_TITLE";

  // used to ajax submit style
  private boolean ajaxEnabledLoc =false;
  private String  ajaxDomIdToReloadLoc =null;



  public ListHeader(String id, Form form) {
    this.id = id;
    urlToInclude = "/commons/layout/partListHeader.jsp";
    this.form = form;

  }


  public static ListHeader getAjaxInstance(String id, Form form, String domIdToRelead){
    ListHeader ret= new ListHeader(id,form);
    ret.ajaxEnabledLoc = true;
    ret.ajaxDomIdToReloadLoc=domIdToRelead;
    return ret;
  }


  public void addHeader(String label) {

    addHeader(label, null, null);
  }

  public void addHeaderFitAndCentered(String label) {

    addHeader(label, "1%", null);
  }

  public void addHeader(String label, String orderingHql) {

    addHeader(label, null, orderingHql);
  }

   public void addHeaderFitAndCentered(String label, String orderingHql) {

    addHeader(label, "1%", orderingHql);
  }

  public void addHeader(String label, String width, String orderingHql) {
    addHeader(label, width, null, orderingHql);
  }

  public void addHeader(String label, String width, String align, String orderingHql) {
    ListHeaderButton bs = new ListHeaderButton(form);
    final Header header = new Header();
    header.bs = bs;
    header.orderingHql = orderingHql;
    bs.drawOrderBy = orderingHql != null;
    header.width = width;
    header.align = align;
    header.setLabel(label);
    getHeaders().add(header);
  }

  public String getDiscriminator() {
    return ListHeader.class.getName();
  }

  public boolean validate(PageState pageState) {
    return true;
  }

  public List getHeaders() {
    return headers;
  }

  public class Header {
    public ListHeaderButton bs;
    public String orderingHql;
    public String width;
    public String align;
    private String label;
    public int state = UNORDERED;
    public static final int UNORDERED = 0;
    public static final int ASCENDING = 1;
    public static final int DESCENDING = 2;

    public void toHtml(PageContext pageContext)  {
      PageState pageState = PageState.getCurrentPageState();
      if (bs.drawOrderBy) {
        try {
          String FLD_FORM_ORDER_BY = pageState.getEntry(Form.FLD_FORM_ORDER_BY + id).stringValue();
          if (orderingHql != null && FLD_FORM_ORDER_BY!=null &&  FLD_FORM_ORDER_BY.indexOf("asc") > -1 && FLD_FORM_ORDER_BY.indexOf(orderingHql.trim()) > -1)
            state = ASCENDING;
          else if (orderingHql != null && FLD_FORM_ORDER_BY!=null && FLD_FORM_ORDER_BY.indexOf("desc") > -1 && FLD_FORM_ORDER_BY.indexOf(orderingHql.trim()) > -1)
            state = DESCENDING;
        } catch (ActionException e1) {
          state = UNORDERED;
        }

        if (state == UNORDERED) {
          Img img = new Img(pageState.sessionState.getSkin().imgPath + "table/nosort.gif", I18n.get(TO_ASC_TITLE));
          img.translateToolTip = true;
          bs.label = getLabel() + "&nbsp;" + img.toHtmlStringBuffer();
          bs.variationsFromForm.addClientEntry(Form.FLD_FORM_ORDER_BY + id, orderingHql + " asc");
          bs.toolTip = I18n.get(TO_ASC_TITLE);
        } else if (state == ASCENDING) {
          bs.label = getLabel() + "&nbsp;" + new Img(pageState.sessionState.getSkin().imgPath + "table/asc.gif", I18n.get(ASC_TO_DESC_TITLE)).toHtmlStringBuffer();

          bs.toolTip = I18n.get(ASC_TO_DESC_TITLE);
          bs.variationsFromForm.addClientEntry(Form.FLD_FORM_ORDER_BY + id, orderingHql + " desc");
        } else {
          bs.label = getLabel() + "&nbsp;" + new Img(pageState.sessionState.getSkin().imgPath + "table/desc.gif", I18n.get(DESC_TO_UNORD_TITLE)).toHtmlStringBuffer();
          bs.toolTip = I18n.get(DESC_TO_UNORD_TITLE);
          bs.variationsFromForm.addClientEntry(Form.FLD_FORM_ORDER_BY + id, "");
        }
      } else {
        bs.label = getLabel();
        bs.toolTip = getLabel();
      }
      bs.alertOnChange = false;
      bs.toHtml(pageContext);
    }

    public String getLabel() {
      return label;
    }

    public void setLabel(String label) {
      this.label = label;
    }
  }

  public class ListHeaderButton extends ButtonSubmit {
    public boolean drawOrderBy = true;

    public ListHeaderButton(Form form) {
      super(form);
      this.ajaxDomIdToReload= ajaxDomIdToReloadLoc;
      this.ajaxEnabled=ajaxEnabledLoc;
      urlToInclude = "/commons/layout/partListHeaderButton.jsp";
    }
  }


   public static void orderAction(QueryHelper qhelp, String s, PageState pageState) {
    orderAction(qhelp, s, pageState, null);
  }

  /**
   * must be called by actions of list of HibernatePages
   *
   * @param qhelp
   * @param listHeaderId
   * @param pageState
   */
  public static void orderAction(QueryHelper qhelp, String listHeaderId, PageState pageState, String defaultOrder) {

    String FLD_FORM_ORDER_BY = pageState.getEntry(Form.FLD_FORM_ORDER_BY + listHeaderId).stringValueNullIfEmpty();
    if (FLD_FORM_ORDER_BY==null)
      FLD_FORM_ORDER_BY = defaultOrder;

    if (JSP.ex(FLD_FORM_ORDER_BY)){
      int pos = qhelp.getHqlString().toLowerCase().indexOf(" order by ");
      if (pos >-1 ){
        String hql=qhelp.getHqlString().substring(0,pos);
        qhelp.setHqlString(hql);
      }
      qhelp.addToHqlString(" order by " + FLD_FORM_ORDER_BY);
    }

  }

  public static String orderAction(String oql, String listHeaderId, PageState pageState) {
    return orderAction(oql, listHeaderId, pageState, null);
  }

  public static String orderAction(String oql, String listHeaderId, PageState pageState, String defaultOrder) {

    String FLD_FORM_ORDER_BY = pageState.getEntry(Form.FLD_FORM_ORDER_BY + listHeaderId).stringValueNullIfEmpty();
    if (FLD_FORM_ORDER_BY==null)
      FLD_FORM_ORDER_BY = defaultOrder;

      if (JSP.ex(FLD_FORM_ORDER_BY)){
        int pos = oql.toLowerCase().indexOf(" order by ");
        if (pos >-1 ){
          oql=oql.substring(0,pos);
        }
      oql = oql+" order by " + FLD_FORM_ORDER_BY;
      }

    return oql;
  }

}
