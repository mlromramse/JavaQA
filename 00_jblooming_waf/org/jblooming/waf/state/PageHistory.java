package org.jblooming.waf.state;

import org.jblooming.waf.constants.*;
import org.jblooming.waf.settings.ApplicationState;
import org.jblooming.waf.view.PageSeed;
import org.jblooming.waf.view.PageState;
import org.jblooming.waf.exceptions.ActionException;
import org.jblooming.operator.Operator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.text.ParseException;

/**
 * @author Pietro Polsinelli ppolsinelli@open-lab.com
 * @author Roberto Bicchierai rbicchierai@open-lab.com
 */
public class PageHistory {

  public static Set<String> recordedPages = new HashSet();

  public List<PageSeed> history = new LinkedList<PageSeed>();
  public int whereAmI = 0;
  public final static String ORIGINAL_COMMAND = "ORIGINAL_COMMAND";

  public void saveInHistory(PageState pageState) {

    if (!Fields.TRUE.equals(pageState.getEntry("BACK_REDIRECTED").stringValueNullIfEmpty())) {

      PageSeed ps = pageState.getNewInstance();
      List<PageSeed> history = pageState.sessionState.pageHistory.history;


      history.add(ps);
      pageState.addClientEntry("THIS_PAGE_RECORDED", Fields.TRUE);

      int rvs = 10;
      try {
        rvs = Integer.parseInt(Operator.getOperatorOption(pageState.getLoggedOperator(), OperatorConstants.RECENT_VIEWS_SIZE));
      } catch (Exception e) {
      }
      if (history.size() > rvs) {
        history.remove(0);
      }
      whereAmI = history.size() - 1;
    }
  }

  public boolean goBack(HttpServletRequest request, HttpServletResponse response) throws IOException {

    if (request.getParameter("BACK_REDIRECTED") == null) {
      if (whereAmI > 0) {
        if (whereAmI == history.size() - 1)
          history.remove(history.size() - 1);
        whereAmI = history.size() - 1;
      }
      redirectBack(response);
      return false;

    } else {

      PageState currentPageState = PageState.getCurrentPageState();
      PageSeed pageSeed = history.get(whereAmI);
      currentPageState.addClientEntries(pageSeed.getClientEntries());
      currentPageState.setCommand(pageSeed.getCommand());
      currentPageState.mainObjectId = pageSeed.mainObjectId;
      currentPageState.setPopup(pageSeed.isPopup());

    }
    return true;
  }

  public boolean goBackTo(HttpServletRequest request, HttpServletResponse response, PageState pageState) throws ParseException, ActionException, IOException {

    if (request.getParameter("BACK_REDIRECTED") == null) {
      int backTo = pageState.getEntry("BACK_TO").intValue();
      whereAmI = backTo;
      redirectBack(response);
      return false;
    } else
      return true;
  }

  private void redirectBack(HttpServletResponse response) throws IOException {
    if (history.size() > 0) {
      PageSeed goingTo = new PageSeed(history.get(whereAmI).href);
      goingTo.addClientEntry("BACK_REDIRECTED", Fields.TRUE);
      goingTo.setCommand(Commands.BACK);
      response.resetBuffer();
      response.sendRedirect(goingTo.toLinkToHref());
    } else
      response.sendRedirect(ApplicationState.contextPath+ApplicationState.platformConfiguration.defaultIndex);
  }

  public void noMoreHistory() {
    history = new LinkedList<PageSeed>();
    whereAmI = 0;
  }

  public int calculateWhereBack(String pageFile) {
    int result = 0;
    for (int j = history.size() - 1; j >= 0; j--) {
      PageSeed hist = history.get(j);
      if (hist.href.indexOf(pageFile) > -1) {
        //result = (history.size()-freq);
        result = j;
        break;
      }
    }
    return result;
  }

}
