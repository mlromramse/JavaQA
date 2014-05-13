package org.jblooming.waf.html.input;

import org.jblooming.utilities.JSP;
import org.jblooming.waf.html.core.HtmlBootstrap;
import org.jblooming.waf.html.core.JspHelper;
import org.jblooming.waf.view.PageState;

import javax.servlet.ServletException;
import javax.servlet.jsp.PageContext;
import java.io.IOException;

public class DateDurationInput extends JspHelper implements HtmlBootstrap {

  public DateField startField;
  public String onBlurStartAdditionalScript;
  public CheckField startMilestoneField;

  public DateField endField;
  public String onBlurEndAdditionalScript;
  public CheckField endMilestoneField;

  public TextField durationField;
  public String onBlurDurationAdditionalScript;
  public boolean durationInWorkingDays=true;
  public boolean durationInTime=false;

  public static final String COMPUTE_FIELDS = "CMPF";

  public DateDurationInput(String prefix, PageState pageState) {
    this(prefix + "START", prefix + "STARTISMILES", prefix + "END", prefix + "ENDISMILES", prefix + "DURATION", pageState);
  }

  public DateDurationInput(String startField, String startMilestoneField, String endField, String endMilestoneField, String durationField, PageState pageState) {

    this.startField = new DateField(startField, pageState);
    this.startMilestoneField = new CheckField(startMilestoneField, "&nbsp;", false);
    this.endField = new DateField(endField, pageState);
    this.endMilestoneField = new CheckField(endMilestoneField, "&nbsp;", false);
    this.durationField = TextField.getDurationInDaysInstance(durationField);

    this.startField.onblurOnDateValid = JSP.w(onBlurStartAdditionalScript) +
            "resynchDates('START','" + this.startField.id + "','" + this.startMilestoneField.id + "','" + this.durationField.id + "','" + this.endField.id + "','" + this.endMilestoneField.id + "');";
    this.startMilestoneField.additionalOnclickScript =
            "resynchDates('MILES','" + this.startField.id + "','" + this.startMilestoneField.id + "','" + this.durationField.id + "','" + this.endField.id + "','" + this.endMilestoneField.id + "');";
    this.endField.onblurOnDateValid = JSP.w(onBlurEndAdditionalScript) +
            "resynchDates('END','" + this.startField.id + "','" + this.startMilestoneField.id + "','" + this.durationField.id + "','" + this.endField.id + "','" + this.endMilestoneField.id + "');";
    this.endMilestoneField.additionalOnclickScript =
            "resynchDates('MILES','" + this.startField.id + "','" + this.startMilestoneField.id + "','" + this.durationField.id + "','" + this.endField.id + "','" + this.endMilestoneField.id + "');";
    this.durationField.script = " autocomplete=\"off\" onBlur=\"" + JSP.w(onBlurDurationAdditionalScript) +
            "resynchDates('TASK_DURATION','" + this.startField.id + "','" + this.startMilestoneField.id + "','" + this.durationField.id + "','" + this.endField.id + "','" + this.endMilestoneField.id + "');\" ";

    this.urlToInclude = "/commons/layout/dateDurationInput/partDateDurationInput.jsp";

  }

  public String getDiscriminator() {
    return DateDurationInput.class.getName();
  }

  public boolean validate(PageState pageState) throws IOException, ServletException {
    return true;
  }

  public void init(PageContext pageContext) {
    PageState ps = PageState.getCurrentPageState();
    if (!ps.initedElements.contains(getDiscriminator())) {
      pageContext.getRequest().setAttribute(ACTION, INITIALIZE);
      super.toHtml(pageContext);
      ps.initedElements.add(getDiscriminator());
    }
  }

  public void drawStart(PageContext pageContext) {
    init(pageContext);
    startField.toHtml(pageContext);
  }

  public void drawStartMiles(PageContext pageContext) {
    init(pageContext);
    startMilestoneField.toHtml(pageContext);
  }

  public void drawDuration(PageContext pageContext) {
    init(pageContext);
    durationField.toHtml(pageContext);
  }


  public void drawEnd(PageContext pageContext) {
    init(pageContext);
    endField.toHtml(pageContext);
  }

  public void drawEndMiles(PageContext pageContext) {
    init(pageContext);
    endMilestoneField.toHtml(pageContext);
  }

  /**
   * @deprecated
   */
  public void toHtml(PageContext pageContext) {
    throw new RuntimeException("Call drawStart,drawStartMiles,drawDuration,drawEnd and drawEndMiles");
  }

}
