<%@ page import="org.jblooming.utilities.DateUtilities, org.jblooming.utilities.JSP, org.jblooming.waf.SessionState, org.jblooming.waf.constants.AgendaConstants, org.jblooming.waf.constants.OperatorConstants, org.jblooming.waf.html.core.JspIncluderSupport, org.jblooming.waf.html.input.ScheduleComposer, org.jblooming.waf.view.PageState, org.jblooming.operator.Operator" %>
<%
  PageState pageState = PageState.getCurrentPageState();
  SessionState sessionState = pageState.sessionState;

  ScheduleComposer composer = (ScheduleComposer) JspIncluderSupport.getCurrentInstance(request);
  sessionState.getAttributes().put(ScheduleComposer.class.getName(), composer);
  Operator loggedOperator = pageState.getLoggedOperator();
  String hb = loggedOperator.getOptionOrDefault(OperatorConstants.FLD_WORKING_HOUR_BEGIN);
  if (!JSP.ex(hb))
    hb = "9:00";
  final int startOfWorkingDay = (int) DateUtilities.millisFromHourMinuteSmart(hb);
  String he = loggedOperator.getOptionOrDefault(OperatorConstants.FLD_WORKING_HOUR_END);
  if (!JSP.ex(hb))
    hb = "18:00";

  final int endOfWorkingDay = (int) DateUtilities.millisFromHourMinuteSmart(he);

%><script>
    function saveStateSingleId()  {
      obj('<%=AgendaConstants.FLD_START_HOUR%>').value='<%=DateUtilities.getMillisInHoursMinutes(startOfWorkingDay)%>';
      obj('<%=AgendaConstants.FLD_END_HOUR%>').value='<%=DateUtilities.getMillisInHoursMinutes(endOfWorkingDay)%>';
      obj('<%=AgendaConstants.FLD_DURATION%>').value='<%=DateUtilities.getMillisInHoursMinutes(endOfWorkingDay-startOfWorkingDay)%>';
    }
  </script><jsp:include page="parts/ajaxScheduleComposer.jsp"/>