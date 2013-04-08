package org.jblooming.agenda;

import net.sf.json.JSONObject;

import java.util.*;


public class ScheduleDaily extends ScheduleSupport implements Schedule {

  public ScheduleDaily() {
  }

  public ScheduleDaily(Date start, int duration, boolean onlyWorkingDays) {
    this (start,duration,1,1,onlyWorkingDays);
  }


  public ScheduleDaily(Date start, int duration, int freq, int rep, boolean onlyWorkingDays) {
    this.setStart(start);
    this.setDuration(duration);
    this.setFreq((freq > 0 ? freq : 1));
    this.setRepeat(rep);
    this.setOnlyWorkingDays(onlyWorkingDays);
    recalculateFields();
  }

  public ScheduleDaily(Date start,int startTime, int duration, boolean onlyWorkingDays) {
    this (start,startTime,duration,1,1,onlyWorkingDays);
  }

  public ScheduleDaily(Date start, int startTime, int duration, int freq, int rep, boolean onlyWorkingDays) {
    this.setStartTime(startTime);
    this.setStart(start);
    CompanyCalendar cal = new CompanyCalendar();
    cal.setTimeInMillis(this.getValidityStartTime());
    cal.setMillisFromMidnight(startTime);
    this.setStart(cal.getTime());
    this.setDuration(duration);
    this.setFreq((freq > 0 ? freq : 1));
    this.setRepeat(rep);
    this.setOnlyWorkingDays(onlyWorkingDays);
    recalculateFields();
  }

  public void recalculateFields() {
    CompanyCalendar cal = new CompanyCalendar();
    cal.setTimeInMillis(this.getValidityStartTime());
    this.setStartTime(cal.getMillisFromMidnight());
    if (this.getRepeat() > 0) {
      int freq = (this.getFreq()>0?this.getFreq() :1);
      int val = ((this.getRepeat() - 1) * freq);
      cal.add(Calendar.DATE, val);
    } else {
      this.setRepeat(0);
      cal = new CompanyCalendar();
      cal.setTime(CompanyCalendar.MAX_DATE);
      cal.set(Calendar.HOUR_OF_DAY, 0);
      cal.clear(Calendar.MINUTE);
      cal.clear(Calendar.SECOND);
      cal.clear(Calendar.MILLISECOND);
      cal.add(Calendar.MILLISECOND, this.getStartTime());
    }
    cal.add(Calendar.MILLISECOND, (int)this.getDuration());
    this.setEnd(cal.getTime());
  }

  public long getNextFireTimeAfter(long afterTime) {
    long returnTime = Long.MAX_VALUE;
    if (afterTime <= getEnd().getTime()) {
      if (afterTime > getStart().getTime()) {
        CompanyCalendar cal = new CompanyCalendar();
        long lstart = getStart().getTime();
        cal.setTimeInMillis(lstart);
        TimeZone timeZone = cal.getTimeZone();
        int ofset = (timeZone.getOffset(lstart) - timeZone.getOffset(afterTime)); // questo server per calcolare i millisecondi effettivi in caso di ora legale
        long distInMillisec = afterTime - lstart - ofset;
        double ddistInDays = (double) distInMillisec / (double) CompanyCalendar.MILLIS_IN_DAY;
        int distInDays = (int) ddistInDays;
        int freq = (this.getFrequency()>0?this.getFreq() :1);
        int rest = freq - (int) distInDays % freq;

        cal.setTime(getStart());
        cal.add(Calendar.DATE, (int) distInDays + rest);

        if (isOnlyWorkingDays()) {
          while (!cal.isWorkingDay()) {
            cal.add(Calendar.DATE, 1);
          }
        }
        if (cal.getTime().getTime() <= getEnd().getTime())
          returnTime = cal.getTime().getTime();
      } else
        returnTime = getStart().getTime();
    }
    return returnTime;
  }


  public long getPreviousFireTimeBefore(long beforeTime) {
    long returnTime = Long.MIN_VALUE;
    if (beforeTime > getStart().getTime()) {
      if(beforeTime > getEnd().getTime())
        beforeTime = getEnd().getTime();

      CompanyCalendar cal = new CompanyCalendar();
      long lstart = getStart().getTime();
      cal.setTimeInMillis(lstart);      
      TimeZone timeZone = cal.getTimeZone();
      int ofset = (timeZone.getOffset(lstart) - timeZone.getOffset(beforeTime)); // questo server per calcolare i millisecondi effettivi in caso di ora legale
      long distInMillisec = beforeTime - lstart - ofset;
      double ddistInDays = (double) distInMillisec / (double) CompanyCalendar.MILLIS_IN_DAY;
      int distInDays = (int) ddistInDays;
      int freq = (this.getFreq()>0?this.getFreq() :1);
      int rest = (int) distInDays % freq;
      cal.setTime(getStart());
      cal.add(Calendar.DATE, (int) distInDays - rest);
      // onlyWorkingDays  non è possibile sapere quale giorno è il prec. es: tutti i giorni non lav.
      // sab-> lun dom->lun se chiedo il prev. di lunedì quale ritorno?

      returnTime = cal.getTimeInMillis();
    }
    return returnTime;
  }


  public String getName() {
      return "daily";
  }


  public JSONObject jsonify() {
    JSONObject ret = super.jsonify();
    ret.element("type","daily");

    return ret;
  }


}

