package org.jblooming.waf.html.input;

import org.jblooming.waf.html.core.JspHelper;
import org.jblooming.waf.html.core.HtmlBootstrap;
import org.jblooming.waf.html.state.Form;
import org.jblooming.waf.view.PageState;
import org.jblooming.waf.view.ClientEntry;
import org.jblooming.waf.constants.AgendaConstants;
import org.jblooming.waf.constants.Fields;
import org.jblooming.waf.exceptions.ActionException;
import org.jblooming.waf.settings.I18n;
import org.jblooming.agenda.*;
import org.jblooming.utilities.ReflectionUtilities;
import org.jblooming.utilities.DateUtilities;
import org.jblooming.utilities.CodeValueList;

import java.util.*;
import java.text.ParseException;

public class ScheduleComposer extends JspHelper implements HtmlBootstrap {
  public enum Schedule_Type {
    MINUTE, PERIOD, DAILY, WEEKLY, MONTHLY, YEARLY
  }

  public boolean isMinute = false;
  public boolean isSingle = true;
  public boolean isDaily = true;
  public boolean isWeekly = true;
  public boolean isMonthly = true;
  public boolean isYearly = true;
  public Form form;
  public String height = "155";
  public boolean durationInWorkingDays = false;
  public boolean durationInTime = true;
  public boolean addJavaScript = false;
  public String divWidth = "560px";


  public ScheduleComposer(String id, Form form) {
    this.id = id;
    this.form = form;
    urlToInclude = "/commons/scheduler/partScheduleComposer.jsp";
  }

  public String getDiscriminator() {
    return this.getClass().getName();
  }

  public boolean validate(PageState pageState) {
    return true;
  }

  /*
  Metodi per fare il make di uno schedule
  */
  public static void make(String scId, ScheduleSupport schedule, PageState pageState) {
    String key = scId + "_ts_";
    schedule = (ScheduleSupport) ReflectionUtilities.getUnderlyingObject(schedule);

    if (schedule instanceof ScheduleMinute) {
      pageState.addClientEntry(key, key + Schedule_Type.MINUTE.toString());
      makeMinute(pageState, (ScheduleMinute) schedule);
    }
    if (schedule instanceof Period) {
      pageState.addClientEntry(key, key + Schedule_Type.PERIOD.toString());
      makePeriod(pageState, (Period) schedule);
    }
    if (schedule instanceof ScheduleDaily) {
      pageState.addClientEntry(key, key + Schedule_Type.DAILY.toString());
      makeDaily(pageState, (ScheduleDaily) schedule);
    }
    if (schedule instanceof ScheduleWeekly) {
      pageState.addClientEntry(key, key + Schedule_Type.WEEKLY.toString());
      makeWeekly(pageState, (ScheduleWeekly) schedule);
    }
    if (schedule instanceof ScheduleMonthly) {
      pageState.addClientEntry(key, key + Schedule_Type.MONTHLY.toString());
      makeMonthly(pageState, (ScheduleMonthly) schedule);
    }
    if (schedule instanceof ScheduleYearly) {
      pageState.addClientEntry(key, key + Schedule_Type.YEARLY.toString());
      makeYearly(pageState, (ScheduleYearly) schedule);
    }
  }

  private static void makeMinute(PageState pageState, ScheduleMinute schedule) {
    makePeriod(pageState, schedule);    
    pageState.addClientEntry(AgendaConstants.FLD_RECURRENT_EVERY, String.valueOf(schedule.getFrequency()));
    pageState.addClientEntry(AgendaConstants.FLD_RECURRENT_HOWMANYTIMES, String.valueOf(schedule.getRepetitions()));
  }

  public static ScheduleSupport saveMinute(PageState pageState) {
    Period p = computeStartEnd(pageState);
    Date startDate = p.getStartDate();

    try {
      startDate = pageState.getEntryAndSetRequired(AgendaConstants.FLD_START).dateValue();
    } catch (ActionException e) {
    } catch (ParseException e) {
    }

    int millisStart = 0;

    try {
      millisStart = (int) pageState.getEntryAndSetRequired(AgendaConstants.FLD_START_HOUR).timeValueInMillis();
    } catch (ActionException e) {
    } catch (ParseException e) {
    }

    int freq = 1;
    try {
      freq = pageState.getEntry(AgendaConstants.FLD_RECURRENT_EVERY).intValue();
    } catch (ActionException e) {
    } catch (ParseException e) {
    }

    int rep = 1;
    try {
      rep = pageState.getEntry(AgendaConstants.FLD_RECURRENT_HOWMANYTIMES).intValue();
    } catch (ActionException e) {
    } catch (ParseException e) {
    }

    ScheduleMinute sd = null;
    int duration = 0;
    try {
      duration = (int)pageState.getEntry(AgendaConstants.FLD_DURATION).timeValueInMillis();
    } catch (ActionException e) {
    } catch (ParseException e) {
    }
    if (pageState.validEntries()) {
      sd = new ScheduleMinute(startDate, millisStart, duration, freq, rep);
      makeMinute(pageState, sd);
    }
    return sd;
  }


  private static void makePeriod(PageState pageState, ScheduleSupport schedule) {
    pageState.addClientEntry(AgendaConstants.FLD_START, DateUtilities.dateToString(schedule.getStartDate()));
    pageState.addClientEntry(AgendaConstants.FLD_START_HOUR, DateUtilities.dateToHourMinutes(schedule.getStartDate()));
    pageState.addClientEntry(AgendaConstants.FLD_END, DateUtilities.dateToString(schedule.getEndDate()));
    pageState.addClientEntry(AgendaConstants.FLD_END_HOUR, DateUtilities.dateToHourMinutes(schedule.getEndDate()));
    pageState.addClientEntryTime(AgendaConstants.FLD_DURATION, schedule.getDurationInMillis()%CompanyCalendar.MILLIS_IN_DAY);
  }

  private static void makeDaily(PageState pageState, ScheduleDaily schedule) {
    makePeriod(pageState, schedule);
    pageState.addClientEntry(AgendaConstants.FLD_RECURRENT_EVERY, String.valueOf(schedule.getFrequency()));
    pageState.addClientEntry(AgendaConstants.FLD_RECURRENT_HOWMANYTIMES, String.valueOf(schedule.getRepetitions()));
  }

  private static void makeWeekly(PageState pageState, ScheduleWeekly schedule) {

    makePeriod(pageState, schedule);

    pageState.addClientEntry(AgendaConstants.FLD_RECURRENT_EVERY, String.valueOf(schedule.getFrequency()));
    pageState.addClientEntry(AgendaConstants.FLD_RECURRENT_HOWMANYTIMES, String.valueOf(schedule.getRepetitions()));

    int[] days = schedule.getDays();
    if (days != null && days.length > 0) {
      for (int i : days) {
        //    int day = (i - 1);
        pageState.addClientEntry("DAY_OF_WEEK_" + i, Fields.TRUE);
      }
    }

  }

  private static void makeMonthly(PageState pageState, ScheduleMonthly schedule) {

    makePeriod(pageState, schedule);

    if (schedule.getWeekInMonth() > 0 &&  schedule.getDayInWeek() > 0) {
      pageState.addClientEntry(AgendaConstants.FLD_RECURRENT_MONTHLY, "2");
      pageState.addClientEntry(AgendaConstants.FLD_RECURRENT_MONTHLY_WEEKS, ((ScheduleMonthly) schedule).getWeekInMonth());
      pageState.addClientEntry(AgendaConstants.FLD_RECURRENT_MONTHLY_DAYS, ((ScheduleMonthly) schedule).getDayInWeek());
      pageState.addClientEntry(AgendaConstants.FLD_RECURRENT_MONTHLY_MONTHSNUMBER, String.valueOf(schedule.getFrequency()));
      pageState.addClientEntry(AgendaConstants.FLD_RECURRENT_MONTHLY_HOWMANYTIMES, String.valueOf(schedule.getRepetitions()));
    } else {

      pageState.addClientEntry(AgendaConstants.FLD_RECURRENT_MONTHLY_WEEKS, "1");
      pageState.addClientEntry(AgendaConstants.FLD_RECURRENT_MONTHLY_DAYS, "2");


      pageState.addClientEntry(AgendaConstants.FLD_RECURRENT_MONTHLY, "1");
      pageState.addClientEntry(AgendaConstants.FLD_RECURRENT_EVERY, String.valueOf(schedule.getFrequency()));
      pageState.addClientEntry(AgendaConstants.FLD_RECURRENT_HOWMANYTIMES, String.valueOf(schedule.getRepetitions()));
    }

  }

  private static void makeYearly(PageState pageState, ScheduleYearly schedule) {

    makePeriod(pageState, schedule);

    CompanyCalendar cal = new CompanyCalendar();
    cal.setMillisFromMidnight((int) schedule.getDurationInMillis());

    if ((schedule).getWeekInMonth() < 1) {
      pageState.addClientEntry(AgendaConstants.FLD_RECURRENT_YEARLY, "1");
      cal.setTime(schedule.getStartDate());
      pageState.addClientEntry(AgendaConstants.FLD_RECURRENT_YEARLY_DAYNUMBER, String.valueOf(cal.get(Calendar.DAY_OF_MONTH)));
      pageState.addClientEntry(AgendaConstants.FLD_RECURRENT_YEARLY_MONTH, cal.get(Calendar.MONTH));
      pageState.addClientEntry(AgendaConstants.FLD_RECURRENT_YEARLY_HOWMANYTIMES, String.valueOf(schedule.getRepetitions()));

    } else {
      pageState.addClientEntry(AgendaConstants.FLD_RECURRENT_YEARLY, "2");
      cal.setTime(schedule.getStartDate());
      pageState.addClientEntry(AgendaConstants.FLD_RECURRENT_YEARLY_MONTH_1,cal.get(Calendar.MONTH));
      pageState.addClientEntry(AgendaConstants.FLD_RECURRENT_YEARLY_WEEKS, ((ScheduleYearly) schedule).getWeekInMonth());
      //dovrebbe andare bene così
      pageState.addClientEntry(AgendaConstants.FLD_RECURRENT_YEARLY_DAYS, ((ScheduleYearly) schedule).getDayInWeek());
      pageState.addClientEntry(AgendaConstants.FLD_RECURRENT_YEARLY_HOWMANYTIMES_1, String.valueOf(schedule.getRepetitions()));

    }

  }

/*
Metodi per il salvataggio di uno schedule
*/

  public static ScheduleSupport getSchedule(String scId, PageState pageState) {
    ScheduleSupport schedule = null;
    String key = "";
    if (scId != null)
      key = scId + "_ts_";
    String value = pageState.getEntryAndSetRequired(key).stringValueNullIfEmpty();
    if (value != null) {
      if ((key + Schedule_Type.MINUTE.toString()).equals(value)) {
        schedule = saveMinute(pageState);
      } else if ((key + Schedule_Type.PERIOD.toString()).equals(value)) {
        schedule = savePeriod(pageState);
      } else if ((key + Schedule_Type.DAILY.toString()).equals(value)) {
        schedule = saveDaily(pageState);
      } else if ((key + Schedule_Type.WEEKLY.toString()).equals(value)) {
        schedule = saveWeekly(pageState);
      } else if ((key + Schedule_Type.MONTHLY.toString()).equals(value)) {
        schedule = saveMontly(pageState);
      } else if ((key + Schedule_Type.YEARLY.toString()).equals(value)) {
        schedule = saveYearly(pageState);
      }
    }
    return schedule;
  }

  private static Period computeStartEnd(PageState pageState) {
    CompanyCalendar cal = new CompanyCalendar(pageState.sessionState.getLocale());
    Date startDate = null;
    Date endDate = null;
    try {
      startDate = pageState.getEntryAndSetRequired(AgendaConstants.FLD_START ).dateValue();
    } catch (ActionException e) {
    } catch (ParseException e) {
    }

    int millisEnd = 0;
    int millisStart = 0;

    try {
      millisStart = (int) pageState.getEntryAndSetRequired(AgendaConstants.FLD_START_HOUR).timeValueInMillis();
    } catch (ActionException e) {
    } catch (ParseException e) {
    }

    if (pageState.validEntries()) {
      cal.setTime(startDate);
      cal.setMillisFromMidnight(millisStart);
      startDate = cal.getTime();
    }

    try {
      endDate = pageState.getEntryAndSetRequired(AgendaConstants.FLD_END ).dateValue();
    } catch (ActionException e) {
    } catch (ParseException e) {
    }

    try {
      millisEnd = (int) pageState.getEntryAndSetRequired(AgendaConstants.FLD_END_HOUR ).timeValueInMillis();
    } catch (ActionException e) {
    } catch (ParseException e) {
    }

    if (pageState.validEntries()) {
      cal.setTime(endDate);
      cal.setMillisFromMidnight((int) millisEnd);
      if (cal.getTimeInMillis() > startDate.getTime())
        endDate = cal.getTime();
      else {
        endDate = startDate;
      }
    }
    return new Period(startDate, endDate);
  }

  public static ScheduleSupport savePeriod(PageState pageState) {
    Period period = computeStartEnd(pageState);

    if (pageState.validEntries()) {
      makePeriod(pageState, period);
      return period;
    } else
      return null;
  }

  public static ScheduleSupport saveDaily(PageState pageState) {
    Period p = computeStartEnd(pageState);
    Date startDate = p.getStartDate();

    int freq = 1;
    try {
      freq = pageState.getEntry(AgendaConstants.FLD_RECURRENT_EVERY).intValue();
    } catch (ActionException e) {
    } catch (ParseException e) {
    }

    int rep = 1;
    try {
      rep = pageState.getEntry(AgendaConstants.FLD_RECURRENT_HOWMANYTIMES).intValue();
    } catch (ActionException e) {
    } catch (ParseException e) {
    }

    ScheduleDaily sd = null;
    int duration = 0;
    try {
      duration = (int)pageState.getEntry(AgendaConstants.FLD_DURATION).timeValueInMillis();
    } catch (ActionException e) {
    } catch (ParseException e) {
    }
    if (pageState.validEntries()) {
      sd = new ScheduleDaily(startDate, duration, freq, rep, false);
      makeDaily(pageState, sd);
    }
    return sd;
  }

  public static ScheduleSupport saveWeekly(PageState pageState) {
    Period p = computeStartEnd(pageState);
    Date startDate = p.getStartDate();

    int freq = pageState.getEntry(AgendaConstants.FLD_RECURRENT_EVERY).intValueNoErrorCodeNoExc();

    int rep = 1;
    try {
      rep = pageState.getEntry(AgendaConstants.FLD_RECURRENT_HOWMANYTIMES).intValue();
    } catch (ActionException e) {
    } catch (ParseException e) {
    }

    ScheduleWeekly sw = null;
    int duration = 0;
    try {
      duration = (int)pageState.getEntry(AgendaConstants.FLD_DURATION).timeValueInMillis();
    } catch (ActionException e) {
    } catch (ParseException e) {
    }
    if (pageState.validEntries()) {
      List<Integer> dayArray = new ArrayList<Integer>();
      Map<String, ClientEntry> daysSelected = pageState.getClientEntries().getEntriesStartingWithStripped("DAY_OF_WEEK_");
      for (String key : daysSelected.keySet()) {
        boolean ckValue = daysSelected.get(key).checkFieldValue();
        if (ckValue)
          dayArray.add(new Integer(key));
      }
      if (dayArray.size() > 0) {
        int daysArr[] = new int[dayArray.size()];
        int j = 0;
        for (Integer i : dayArray) {
          daysArr[j] = i;
          j++;
        }
        sw = new ScheduleWeekly(daysArr, startDate, duration, freq, rep, false);
        makeWeekly(pageState, sw);
      } else{
        ClientEntry ce=pageState.getEntry("DAY_OF_WEEK_"+CompanyCalendar.MONDAY);
        ce.errorCode=I18n.get("AT_LEAST_ONE_DAY_REQUIRED");
        pageState.addClientEntry(ce);
      }
    }
    return sw;
  }

  public static ScheduleSupport saveMontly(PageState pageState) {
    //CompanyCalendar cal = new CompanyCalendar(pageState.sessionState.getCurrentLocale());
    Period p = computeStartEnd(pageState);
    Date startDate = p.getStartDate();

    String value = pageState.getEntryAndSetRequired(AgendaConstants.FLD_RECURRENT_MONTHLY).stringValueNullIfEmpty();
    int freq = 1;
    int rep = 1;
    int dayInWeek = 0;
    int weekInMonth = 0;
    if (value != null) {
      if ("1".equals(value)) {
        value = pageState.getEntry(AgendaConstants.FLD_RECURRENT_EVERY).stringValueNullIfEmpty();
        if (value != null && !"".equals(value)) {
          try {
            freq = Integer.parseInt(value);
          } catch (NumberFormatException e) {

          }
        }
        value = pageState.getEntry(AgendaConstants.FLD_RECURRENT_HOWMANYTIMES).stringValueNullIfEmpty();
        if (value != null && !"".equals(value)) {
          try {
            rep = Integer.parseInt(value);
          } catch (NumberFormatException e) {

          }
        }
      } else {
        value = pageState.getEntry(AgendaConstants.FLD_RECURRENT_MONTHLY_WEEKS).stringValueNullIfEmpty();
        if (value != null) {
          weekInMonth = Integer.parseInt(value);
        }
        value = pageState.getEntry(AgendaConstants.FLD_RECURRENT_MONTHLY_DAYS).stringValueNullIfEmpty();
        if (value != null) {
          dayInWeek = Integer.parseInt(value);
        }
        value = pageState.getEntry(AgendaConstants.FLD_RECURRENT_MONTHLY_MONTHSNUMBER).stringValueNullIfEmpty();
        if (value != null) {
          try {
            freq = Integer.parseInt(value);
          } catch (NumberFormatException e) {

          }
        }
        value = pageState.getEntry(AgendaConstants.FLD_RECURRENT_MONTHLY_HOWMANYTIMES).stringValueNullIfEmpty();
        if (value != null) {
          try {
            rep = Integer.parseInt(value);
          } catch (NumberFormatException e) {

          }
        }
      }
    }
    ScheduleMonthly sm = null;
    int duration = 0;
    try {
      duration = (int)pageState.getEntry(AgendaConstants.FLD_DURATION).timeValueInMillis();
    } catch (ActionException e) {
    } catch (ParseException e) {
    }
    if (pageState.validEntries()) {
      if (weekInMonth > 0 && dayInWeek > 0) {
        sm = new ScheduleMonthly(dayInWeek, weekInMonth, startDate, duration, freq, rep, false);
      } else {
        sm = new ScheduleMonthly(startDate, duration, freq, rep, false);
      }
      makeMonthly(pageState, sm);
    }
    return sm;
  }

  public static ScheduleSupport saveYearly(PageState pageState) {

    Period p = computeStartEnd(pageState);
    Date startDate = p.getStartDate();
    CompanyCalendar cal = new CompanyCalendar(pageState.sessionState.getLocale());
    cal.setTime(p.getStartDate());

    int millisStart = cal.getMillisFromMidnight();
    int freq = 1;
    int rep = 1;
    int dayInWeek = 0;
    int weekInMonth = 0;
    String value = pageState.getEntryAndSetRequired(AgendaConstants.FLD_RECURRENT_YEARLY).stringValueNullIfEmpty();
    if (value != null) {
      if ("1".equals(value)) {
        value = pageState.getEntry(AgendaConstants.FLD_RECURRENT_YEARLY_DAYNUMBER).stringValueNullIfEmpty();
        if (value != null) {
          int day = Integer.parseInt(value);

          int month = pageState.getEntry(AgendaConstants.FLD_RECURRENT_YEARLY_MONTH).intValueNoErrorCodeNoExc();
          if (month>0) {
            cal.set(Calendar.MONTH, month);
          }
          cal.set(Calendar.DAY_OF_MONTH, day);
          cal.setMillisFromMidnight(millisStart);
          startDate = cal.getTime();
        }
        value = pageState.getEntry(AgendaConstants.FLD_RECURRENT_YEARLY_HOWMANYTIMES).stringValueNullIfEmpty();
        if (value != null) {
          try {
            rep = Integer.parseInt(value);
          } catch (NumberFormatException e) {
          }
        }
      } else {
        int month = pageState.getEntry(AgendaConstants.FLD_RECURRENT_YEARLY_MONTH_1).intValueNoErrorCodeNoExc();
        if (month>0) {
          cal.set(Calendar.MONTH, month);
        }
        value = pageState.getEntry(AgendaConstants.FLD_RECURRENT_YEARLY_DAYS).stringValueNullIfEmpty();
        if (value != null) {
          dayInWeek = Integer.parseInt(value);
        }
        value = pageState.getEntry(AgendaConstants.FLD_RECURRENT_YEARLY_WEEKS).stringValueNullIfEmpty();
        if (value != null) {
          weekInMonth = Integer.parseInt(value);
        }
        cal.setMillisFromMidnight(millisStart);
        startDate = cal.getTime();
        value = pageState.getEntry(AgendaConstants.FLD_RECURRENT_YEARLY_HOWMANYTIMES_1).stringValueNullIfEmpty();
        if (value != null) {
          try {
            rep = Integer.parseInt(value);
          } catch (NumberFormatException e) {

          }
        }
      }
    }

    ScheduleYearly sy = null;
    int duration = 0;
    try {
      duration = (int)pageState.getEntry(AgendaConstants.FLD_DURATION).timeValueInMillis();
    } catch (ActionException e) {
    } catch (ParseException e) {
    }
    if (pageState.validEntries()) {
      //int) Math.abs((endDate.getTime() - startDate.getTime()) % CompanyCalendar.MILLIS_IN_DAY);
      if (dayInWeek > 0 && weekInMonth > 0) {
        sy = new ScheduleYearly(dayInWeek, weekInMonth, startDate, duration, freq, rep, false);
      } else {
        sy = new ScheduleYearly(startDate, duration, freq, rep, false);
      }
      makeYearly(pageState, sy);
    }
    return sy;
  }

  public static CodeValueList getMonths(PageState pageState) {
    CodeValueList months = new CodeValueList();
    Locale locale = pageState.sessionState.getLocale();
    CompanyCalendar cal = new CompanyCalendar(locale);
    cal.setTime(new Date());
    cal.set(Calendar.MONTH, Calendar.JANUARY);
    for (int i = 0; i < 12; i++) {
      months.add(cal.get(Calendar.MONTH)+"", DateUtilities.dateToString(cal.getTime(), "MMMM"));
      cal.add(CompanyCalendar.MONTH, 1);
    }
    return months;
  }

  public static CodeValueList getDaysOfWeek(PageState pageState) {
    CodeValueList daysOfWeek = new CodeValueList();
    Locale locale = pageState.sessionState.getLocale();
    CompanyCalendar cal = new CompanyCalendar(locale);
    cal.setTime(new Date());
    cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
    cal.setMillisFromMidnight(1);
    for (int i = 0; i < 7; i++) {
      daysOfWeek.add(cal.get(CompanyCalendar.DAY_OF_WEEK) + "", DateUtilities.dateToString(cal.getTime(), "EEEE"));
      cal.add(CompanyCalendar.DAY_OF_WEEK, 1);
    }
    return daysOfWeek;
  }

  public static CodeValueList getWeeksOfMonth(PageState pageState) {
    CodeValueList weeksOfMonth = new CodeValueList();
    weeksOfMonth.add("1", I18n.get(AgendaConstants.I18N_FIRST));
    weeksOfMonth.add("2", I18n.get(AgendaConstants.I18N_SECOND));
    weeksOfMonth.add("3", I18n.get(AgendaConstants.I18N_THIRD));
    weeksOfMonth.add("4", I18n.get(AgendaConstants.I18N_FOURTH));
    weeksOfMonth.add("5", I18n.get(AgendaConstants.I18N_LAST));
    return weeksOfMonth;
  }

  public static String getScheduleDescription(ScheduleSupport ss) {

    String result="";
    if (ss instanceof Period) {
      Period p = (Period)ss;
      result = I18n.getLabel("SCHEDULE_PERIOD_CONTENT_%%...",
              DateUtilities.dateAndHourToString(p.getStartDate()),
              DateUtilities.dateAndHourToString(p.getEndDate()));

    } else if (ss instanceof ScheduleMinute) {

      ScheduleMinute p = (ScheduleMinute)ss;
      result= I18n.get("SCHEDULE_MINUTE_CONTENT_%%...",
              DateUtilities.dateAndHourToString(p.getStartDate()),
              DateUtilities.dateAndHourToString(p.getEndDate()),
              p.getFrequency()+"",
              p.getRepetitions()+""
              );

    } else if (ss instanceof ScheduleDaily) {

      ScheduleDaily p = (ScheduleDaily)ss;
      result= I18n.get("SCHEDULE_DAILY_CONTENT_%%...",
              DateUtilities.dateAndHourToString(p.getStartDate()),
              DateUtilities.dateAndHourToString(p.getEndDate()),
              p.getFrequency()+"",
              p.getRepetitions()+""
              );

    } else if (ss instanceof ScheduleWeekly) {

      ScheduleWeekly p = (ScheduleWeekly)ss;
      CompanyCalendar cal = new CompanyCalendar();
      cal.setTime(new Date());
      String week="";
      for (int i : p.getDays()) {
        cal.set(Calendar.DAY_OF_WEEK, i);
        week = week + " " + DateUtilities.dateToString(cal.getTime(), "EEEE");
      }
      result= I18n.get("SCHEDULE_WEEKLY_CONTENT_%%...",
              DateUtilities.dateAndHourToString(p.getStartDate()),
              p.getFrequency()+"",
              p.getRepetitions()+"",
              week
              );

    } else if (ss instanceof ScheduleMonthly) {
      ScheduleMonthly p = (ScheduleMonthly)ss;
      result= I18n.getLabel("SCHEDULE_MONTHLY_CONTENT_%%...",
              DateUtilities.dateAndHourToString(p.getStartDate()),
              DateUtilities.dateAndHourToString(p.getEndDate())
              );

    } else if (ss instanceof ScheduleYearly) {

      ScheduleYearly p = (ScheduleYearly)ss;
      result= I18n.getLabel("SCHEDULE_YEARLY_CONTENT_%%...",
              DateUtilities.dateAndHourToString(p.getStartDate()),
              DateUtilities.dateAndHourToString(p.getEndDate())
              );

    }

    return result;
  }

}
