package org.jblooming.waf;

import org.jblooming.waf.view.PageState;

/**
 * @author Pietro Polsinelli ppolsinelli@open-lab.com
 * @author Roberto Bicchierai rbicchierai@open-lab.com
 */
public class ActionSupport {

  protected PageState pageState;

  public ActionSupport(PageState pageState) {
    this.pageState = pageState;
  }

}
