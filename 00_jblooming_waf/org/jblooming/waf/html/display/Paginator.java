package org.jblooming.waf.html.display;

import org.jblooming.PlatformRuntimeException;
import org.jblooming.operator.Operator;
import org.jblooming.operator.businessLogic.OptionAction;
import org.jblooming.persistence.exceptions.PersistenceException;
import org.jblooming.waf.constants.Fields;
import org.jblooming.waf.constants.I18nConstants;
import org.jblooming.waf.constants.OperatorConstants;
import org.jblooming.waf.exceptions.ActionException;
import org.jblooming.waf.html.core.HtmlBootstrap;
import org.jblooming.waf.html.core.JspHelper;
import org.jblooming.waf.html.state.Form;
import org.jblooming.waf.settings.I18n;
import org.jblooming.waf.view.PageState;

import javax.servlet.ServletException;
import java.io.IOException;
import java.text.ParseException;

/**
 * @author Pietro Polsinelli ppolsinelli@open-lab.com
 */
public class Paginator extends JspHelper implements HtmlBootstrap {
  public static final String init = Paginator.class.getName();
  public Form form;
  public String objectsFound;
  public String pageSize;
  public String previous;
  public String page;
  public String of;
  public String next;
  public String last;
  public String no_filters;
  public String paginatorTitle;
  public boolean showGoLink = true;
  public static final String FLD_PAGE_NUMBER = Fields.FORM_PREFIX + "PG_N";
  public static final String FLD_PAGE_SIZE = Fields.FORM_PREFIX + "PG_S";

  public static final int DEFAULT_PAGE_SIZE = 10;
  public Modality modality = Modality.DEFAULT;

  public static enum Modality {REPORT,DEFAULT,EXTENDED};


  protected Paginator(String id, Form form) {
    super();
    this.id = id;
    this.form = form;
    urlToInclude = "/commons/layout/partPagePaginator.jsp";
  }

  public Paginator(String id, Form form, PageState pageState) {
    super();
    this.id = id;
    urlToInclude = "/commons/layout/partPagePaginator.jsp";
    this.form = form;

    objectsFound = I18n.get(I18nConstants.I18N_OBJECTS_FOUND);
    previous = I18n.get(I18nConstants.PREV);
    pageSize = I18n.get(I18nConstants.PAGE_SIZE);
    page = I18n.get(I18nConstants.PAGE);
    of = I18n.get(I18nConstants.OF);
    next = I18n.get(I18nConstants.NEXT);
    last = I18n.get(I18nConstants.LAST);
    no_filters = I18n.get(I18nConstants.NO_FILTERS);
  }

  public String getDiscriminator() {
    return init;
  }

  public boolean validate(PageState ps) throws IOException, ServletException {
    return ps.initedElements.contains(init);
  }

  public static int getWantedPageNumber(PageState pageState) {
    int result = 0;
    try {
      result = Math.max(0, pageState.getEntry(FLD_PAGE_NUMBER).intValue() - 1);

    } catch (ActionException e) {
    } catch (ParseException e) {
    }
    return result;
  }

  /**
   * Default page size = 6
   *
   * @param pageState
   */
  public static int getWantedPageSize(PageState pageState) {

    return getWantedPageSize(pageState, 0);
  }

  public static int getWantedPageSize(String pageName, PageState pageState) {

    return getWantedPageSize(pageName, 0, pageState);
  }

  public static int getWantedPageSize(PageState pageState, int defaultSize) {
    return getWantedPageSize("", defaultSize, pageState);
  }

  public static int getWantedPageSize(String pageName, int defaultSize, PageState pageState) {

    if (defaultSize == 0) {
      defaultSize = DEFAULT_PAGE_SIZE;
      String option = Operator.getOperatorOption(pageState.getLoggedOperator(), OperatorConstants.OP_PAGE_SIZE + pageName);
      if (option != null && option.trim().length() > 0)
        defaultSize = Integer.parseInt(option);
      else {
        option = Operator.getOperatorOption(pageState.getLoggedOperator(), OperatorConstants.OP_PAGE_SIZE);
        if (option != null && option.trim().length() > 0)
          defaultSize = Integer.parseInt(option);
      }
    }

    try {
      int currentValue = pageState.getEntry(FLD_PAGE_SIZE).intValue();
      if (currentValue != defaultSize) {
        OptionAction.cmdUpdateLoggedOption(pageState, OperatorConstants.OP_PAGE_SIZE + pageName, currentValue + "");
      }
      if (currentValue <= 0)
        currentValue = defaultSize;
      return currentValue;
    } catch (ParseException pe) {
      return defaultSize;
    } catch (ActionException ae) {
      return defaultSize;
    } catch (PersistenceException e) {
      throw new PlatformRuntimeException(e);
    }
  }
}
