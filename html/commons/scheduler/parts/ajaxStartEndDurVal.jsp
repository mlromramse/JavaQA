<%@ page import="org.jblooming.agenda.CompanyCalendar, org.jblooming.utilities.DateUtilities,
org.jblooming.waf.constants.AgendaConstants, org.jblooming.waf.exceptions.ActionException, org.jblooming.waf.view.PageState, java.text.ParseException" %><%

  PageState pageState = PageState.getCurrentPageState();

  String fieldLeft = pageState.getEntry("OBJLEFT").stringValueNullIfEmpty();

  String answer = "";

  if (fieldLeft != null) {

    try {
      long dur = pageState.getEntry(AgendaConstants.FLD_DURATION).timeValueInMillis();
      long start = pageState.getEntry(AgendaConstants.FLD_START_HOUR).timeValueInMillis();
      long end = pageState.getEntry(AgendaConstants.FLD_END_HOUR).timeValueInMillis();

      if (AgendaConstants.FLD_START_HOUR.equals(fieldLeft)) {
        end = start + dur;

      } else if (AgendaConstants.FLD_END_HOUR.equals(fieldLeft)) {
        dur = end - start;
        if (dur < 0)
          dur = 0;

      } else if (AgendaConstants.FLD_DURATION.equals(fieldLeft)) {
        end = start + dur;
      }
      if (end > CompanyCalendar.MILLIS_IN_DAY)
        end=end%(CompanyCalendar.MILLIS_IN_DAY);
      
      answer = DateUtilities.getMillisInHoursMinutes(start) + "_" + DateUtilities.getMillisInHoursMinutes(end) + "_" + DateUtilities.getMillisInHoursMinutes(dur);
    } catch (ActionException e) {
    } catch (ParseException e) {
    }
  }
%><%=answer%><%

%>