<%@ page import="org.jblooming.agenda.CompanyCalendar, org.jblooming.utilities.CodeValueList, org.jblooming.utilities.DateUtilities, org.jblooming.waf.SessionState, org.jblooming.waf.constants.AgendaConstants, org.jblooming.waf.exceptions.ActionException, org.jblooming.waf.html.button.ButtonSubmit, org.jblooming.waf.html.input.*, org.jblooming.waf.view.PageState, java.text.ParseException, java.util.*" %>
<script type="text/javascript">
  function validateSED(objLeftId) {

    var url = contextPath+"/commons/scheduler/parts/ajaxStartEndDurVal.jsp?OBJLEFT="+ objLeftId;

    url = url + '&<%=AgendaConstants.FLD_START_HOUR%>='+obj('<%=AgendaConstants.FLD_START_HOUR%>').value;
    url = url + '&<%=AgendaConstants.FLD_END_HOUR%>='+obj('<%=AgendaConstants.FLD_END_HOUR%>').value;
    url = url + '&<%=AgendaConstants.FLD_DURATION%>='+obj('<%=AgendaConstants.FLD_DURATION%>').value;
    //alert(url);
    var content = getContent(url);
    if ("KO"==content)
      alert('Invalid '+objLeftId+' value');
    else {
      var strings = content.split('_');
      if (strings.length==3) {
        obj('<%=AgendaConstants.FLD_START_HOUR%>').value = strings[0];
        obj('<%=AgendaConstants.FLD_END_HOUR%>').value = strings[1];
        obj('<%=AgendaConstants.FLD_DURATION%>').value = strings[2];
      }
    }
  }

</script>
<%

  PageState pageState = PageState.getCurrentPageState();
  SessionState sessionState = pageState.sessionState;

  ScheduleComposer composer = (ScheduleComposer) sessionState.getAttribute(ScheduleComposer.class.getName());

  ButtonSubmit bs = ButtonSubmit.getAjaxButton(composer.form, "SCHEDULER_DIV");
  bs.variationsFromForm.href = pageState.pageFromCommonsRoot("scheduler/parts/ajaxScheduleComposer.jsp").href;
  bs.preserveFormStatus = true;
  String blurLaunch = bs.generateJs().toString();

  List<String> days = new ArrayList<String>();
  for (int i = 1; i < 32; i++) {
    days.add(String.valueOf(i));
  }
  CodeValueList months = ScheduleComposer.getMonths(pageState);
  CodeValueList daysOfWeek = ScheduleComposer.getDaysOfWeek(pageState);
  CodeValueList weeksOfMonth = ScheduleComposer.getWeeksOfMonth(pageState);

  String comps = pageState.getEntry(composer.id + "_ts_").stringValueNullIfEmpty();


  if (pageState.getEntry(AgendaConstants.FLD_RECURRENT_EVERY).stringValueNullIfEmpty()==null)
    pageState.addClientEntry(AgendaConstants.FLD_RECURRENT_EVERY,"1");

  if (pageState.getEntry(AgendaConstants.FLD_RECURRENT_HOWMANYTIMES).stringValueNullIfEmpty()==null)
    pageState.addClientEntry(AgendaConstants.FLD_RECURRENT_HOWMANYTIMES,"0");

  if (pageState.getEntry(AgendaConstants.FLD_RECURRENT_MONTHLY).stringValueNullIfEmpty()==null)
    pageState.addClientEntry(AgendaConstants.FLD_RECURRENT_MONTHLY,"1");

  if (pageState.getEntry(AgendaConstants.FLD_RECURRENT_YEARLY).stringValueNullIfEmpty()==null)
    pageState.addClientEntry(AgendaConstants.FLD_RECURRENT_YEARLY,"1");


  boolean isMinute = (composer.id + "_ts_"+ScheduleComposer.Schedule_Type.MINUTE.toString()).equals(comps);
  boolean isSingle = (composer.id + "_ts_"+ScheduleComposer.Schedule_Type.PERIOD.toString()).equals(comps);
  boolean isDaily = (composer.id + "_ts_"+ScheduleComposer.Schedule_Type.DAILY.toString()).equals(comps);
  boolean isWeekly = (composer.id + "_ts_"+ScheduleComposer.Schedule_Type.WEEKLY.toString()).equals(comps);
  boolean isMonthly = (composer.id + "_ts_"+ScheduleComposer.Schedule_Type.MONTHLY.toString()).equals(comps);
  boolean isYearly = (composer.id + "_ts_"+ScheduleComposer.Schedule_Type.YEARLY.toString()).equals(comps);


%> <table> <%

  %><tr><td nowrap><%
  DateField startField = new DateField(AgendaConstants.FLD_START, pageState);
  startField.toHtmlI18n(pageContext);

  %></td><td nowrap><%

  ComboBox startHour = ComboBox.getTimeInstance(AgendaConstants.FLD_START_HOUR, AgendaConstants.FLD_START_HOUR, "TIMECMBST", 0, pageState);
  startHour.additionalOnBlurScript = "validateSED('"+AgendaConstants.FLD_START_HOUR+"');";
  startHour.separator = "</td><td nowrap>";
  startHour.toHtmlI18n(pageContext);

%></td><td nowrap><%

  ComboBox durationField = ComboBox.getTimeInstance(AgendaConstants.FLD_DURATION, AgendaConstants.FLD_DURATION, pageState);
  durationField.additionalOnBlurScript = "validateSED('"+AgendaConstants.FLD_DURATION+"');";
  durationField.separator="</td><td nowrap>";
  durationField.toHtmlI18n(pageContext);
  
  %></td></tr><%

  %><tr><td nowrap><%
  DateField endField = new DateField(AgendaConstants.FLD_END, pageState);
  endField.readOnly = !isSingle;
  endField.toHtmlI18n(pageContext);

  %></td><td nowrap><%
  int startTime = 0;
  try {
    startTime = (int) pageState.getEntry(AgendaConstants.FLD_START_HOUR).timeValueInMillis();
  } catch (ActionException e) {
  } catch (ParseException e) {
  }

  ComboBox endHour = ComboBox.getTimeInstance(AgendaConstants.FLD_END_HOUR, AgendaConstants.FLD_END_HOUR, "TIMECMBET", 0, pageState);
  //endHour.additionalOnBlurScript = "obj('OBJLEFT').value='" + AgendaConstants.FLD_END_HOUR+"';"+blurLaunch+"obj('"+"AGENDA_SUMMARY"+"').focus();";
  endHour.additionalOnBlurScript = "validateSED('"+AgendaConstants.FLD_END_HOUR+"');";  
  endHour.separator = "</td><td nowrap>";
  endHour.toHtmlI18n(pageContext);

%></td><td nowrap><%

  CheckField cf = new CheckField("ALL_DAY", "</td><td nowrap>", true);
  cf.additionalOnclickScript = "saveStateSingleId(); ";
  cf.toHtmlI18n(pageContext);

  %></td></tr><%

  %></table>
  <table> <%
/*
________________________________________________________________________________________________________________________________________________________________________


  isMinute

________________________________________________________________________________________________________________________________________________________________________

*/

  if (isMinute) {

    %><tr><td nowrap><%
      TextField tf = new TextField(I18n.get(AgendaConstants.FLD_RECURRENT_MINUTE_EVERY), AgendaConstants.FLD_RECURRENT_EVERY, "</td><td>", 4, false);
      tf.toHtml(pageContext);
    %></td><td nowrap><%
      tf = new TextField(I18n.get(AgendaConstants.FLD_RECURRENT_MINUTE_HOWMANYTIMES), AgendaConstants.FLD_RECURRENT_HOWMANYTIMES, "</td><td>", 4, false);
      tf.toHtml(pageContext);
    %></td></tr><%
/*
________________________________________________________________________________________________________________________________________________________________________


  isDaily

________________________________________________________________________________________________________________________________________________________________________

*/

  } else if (isDaily) {

    %><tr><td nowrap><%
      TextField tf = new TextField(I18n.get(AgendaConstants.FLD_RECURRENT_DAILY_EVERY), AgendaConstants.FLD_RECURRENT_EVERY, "</td><td>", 4, false);
      tf.toHtml(pageContext);
    %></td>

    <td><%=I18n.get(AgendaConstants.I18N_DAYS)%></td>

    <td><%
    tf = new TextField(I18n.get(AgendaConstants.FLD_RECURRENT_DAILY_HOWMANYTIMES), AgendaConstants.FLD_RECURRENT_HOWMANYTIMES, "</td><td>", 4, false);
    tf.toHtml(pageContext);
    %></td>

    <td nowrap><%=I18n.get("AGENDA_TIMES")%></td>

    </tr><%
/*
________________________________________________________________________________________________________________________________________________________________________


  isWeekly

________________________________________________________________________________________________________________________________________________________________________

*/

  } else if (isWeekly) {
    %>
       <tr>
        <td>
          <%
            TextField tf = new TextField(I18n.get(AgendaConstants.FLD_RECURRENT_WEEKLY_EVERY), AgendaConstants.FLD_RECURRENT_EVERY, "</td><td>", 2, false);
            tf.toHtml(pageContext);
          %>
        </td>
        <td>
          <%=I18n.get(AgendaConstants.I18N_WEEKS)%>
        </td>
        <td>
          <%
            tf = new TextField(I18n.get(AgendaConstants.FLD_RECURRENT_WEEKLY_HOWMANYTIMES), AgendaConstants.FLD_RECURRENT_HOWMANYTIMES, "</td><td>", 2, false);
            tf.toHtml(pageContext);
          %>
        </td>
        <td nowrap>
          <%=I18n.get("IN_THE_WEEK_DAYS")%>:
        </td>
      </tr>
    <tr>
      <td colspan="8">
        <table>

          <%
            Locale locale = pageState.sessionState.getLocale();
            CompanyCalendar cal = new CompanyCalendar(locale);
            cal.setTime(new Date());
            cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
            cal.setMillisFromMidnight(1);

          %> <tr><%

          for (int i = 0; i < 7; i++) {

        %> <td><%
          CheckField cb = new CheckField("DAY_OF_WEEK_" + cal.get(CompanyCalendar.DAY_OF_WEEK), "</td><td>", false);
          cb.label = DateUtilities.dateToString(cal.getTime(), "EEEE");
          cb.toHtml(pageContext);
        %></td><%

          if (i == 3) {
        %></tr><tr><%
            }

            cal.add(CompanyCalendar.DAY_OF_WEEK, 1);

          }


        %></tr></table>
      </td>
    </tr>
      
    <%
/*
________________________________________________________________________________________________________________________________________________________________________


  isMonthly

________________________________________________________________________________________________________________________________________________________________________

*/

  } else if (isMonthly) {

    %><tr>
      <td colspan="8">
        <table>
          <tr>
            <td>
              <%
                RadioButton rb = new RadioButton("", AgendaConstants.FLD_RECURRENT_MONTHLY, "1", "&nbsp;", null, false, "", pageState);
                rb.id=AgendaConstants.FLD_RECURRENT_MONTHLY+"1";
                rb.toHtml(pageContext);
              %>
            </td>
            <td  nowrap>
              <%
                TextField tf = new TextField(I18n.get(AgendaConstants.FLD_RECURRENT_MONTHLY), AgendaConstants.FLD_RECURRENT_EVERY, "</td><td nowrap>", 2, false);
                tf.toHtml(pageContext);
              %>
            </td>
            <td  nowrap>
              <%=I18n.get(AgendaConstants.I18N_MONTHS)%>
            </td>
            <td nowrap>
              <%
                tf = new TextField(I18n.get(AgendaConstants.FLD_RECURRENT_MONTHLY_HOWMANYTIMES), AgendaConstants.FLD_RECURRENT_HOWMANYTIMES, "</td><td nowrap>", 2, false);
                tf.toHtml(pageContext);
              %>
            </td>
            <td nowrap>
              <%=I18n.get("AGENDA_TIMES")%>
            </td>
          </tr>
        </table>
      </td>
    </tr>
    <tr>
      <td colspan="8">
        <table>
          <tr>
            <td>
              <%
                rb = new RadioButton("", AgendaConstants.FLD_RECURRENT_MONTHLY, "2", "&nbsp;", null, false, "", pageState);
                rb.id=AgendaConstants.FLD_RECURRENT_MONTHLY+"2";
                rb.toHtml(pageContext);
              %>
            </td>
            <td nowrap>
              <%
                String value = pageState.getEntry(AgendaConstants.FLD_RECURRENT_MONTHLY_WEEKS).stringValueNullIfEmpty();
                Combo cbb = new Combo(AgendaConstants.FLD_RECURRENT_MONTHLY_WEEKS, "</td><td nowrap>", "formElements", 10, (value != null ? value : ""), weeksOfMonth, "");
                cbb.label = "";
                cbb.required = false;
                cbb.toHtml(pageContext);%>
            </td>
            <td>
              <%
                value = pageState.getEntry(AgendaConstants.FLD_RECURRENT_MONTHLY_DAYS).stringValueNullIfEmpty();
                cbb = new Combo(AgendaConstants.FLD_RECURRENT_MONTHLY_DAYS, "</td><td nowrap>", "formElements", 10, (value != null ? value : ""), daysOfWeek, "");
                cbb.label = "";
                cbb.required = false;
                cbb.toHtml(pageContext);
              %>
            </td>
            <td nowrap>
              <%
                tf = new TextField(I18n.get(AgendaConstants.FLD_RECURRENT_MONTHLY_MONTHSNUMBER), AgendaConstants.FLD_RECURRENT_MONTHLY_MONTHSNUMBER, "</td><td nowrap>", 2, false);
                tf.toHtml(pageContext);%>
            </td>
            <td nowrap>
              <%=I18n.get(AgendaConstants.I18N_MONTHS)%>
            </td>
            <td>
              <%
                tf = new TextField(I18n.get(AgendaConstants.FLD_RECURRENT_MONTHLY_HOWMANYTIMES), AgendaConstants.FLD_RECURRENT_MONTHLY_HOWMANYTIMES, "</td><td>", 2, false);
                tf.toHtml(pageContext);%>
            </td>
            <td nowrap>
              <%=I18n.get("AGENDA_TIMES")%>
            </td>
          </tr>
        </table>
      </td>
    </tr><%
    /*
    ________________________________________________________________________________________________________________________________________________________________________


      isYearly

    ________________________________________________________________________________________________________________________________________________________________________

    */

  } else if (isYearly) {

  %><tr>
        <td colspan="8">
          <table>
            <tr>
              <td nowrap>
                <%
                  RadioButton rb = new RadioButton(I18n.get(AgendaConstants.FLD_RECURRENT_YEARLY), AgendaConstants.FLD_RECURRENT_YEARLY, "1", "&nbsp;", null, false, "", pageState);
                  rb.id=AgendaConstants.FLD_RECURRENT_YEARLY+"1";
                  rb.toHtml(pageContext);%>
              </td>
              <td nowrap>
                <%
                  CodeValueList cvl = new CodeValueList(days);
                  String value = pageState.getEntry(AgendaConstants.FLD_RECURRENT_YEARLY_DAYNUMBER).stringValueNullIfEmpty();

                  Combo cbb = new Combo(AgendaConstants.FLD_RECURRENT_YEARLY_DAYNUMBER, "</td><td>", "formElements", 10, (value != null ? value : ""), cvl, "");
                  cbb.label = "";
                  cbb.required = false;
                  cbb.toHtml(pageContext);%>
              </td>
              <td> 
                <%
                  value = pageState.getEntry(AgendaConstants.FLD_RECURRENT_YEARLY_MONTH).stringValueNullIfEmpty();

                  cbb = new Combo(AgendaConstants.FLD_RECURRENT_YEARLY_MONTH, "</td><td>", "formElements", 10, (value != null ? value : ""), months, "");
                  cbb.label = "";
                  cbb.required = false;
                  cbb.toHtml(pageContext);%>
              </td>
              <td>
                <%
                  TextField tf = new TextField(I18n.get(AgendaConstants.FLD_RECURRENT_YEARLY_HOWMANYTIMES), AgendaConstants.FLD_RECURRENT_YEARLY_HOWMANYTIMES, "</td><td>", 2, false);
                  tf.toHtml(pageContext);%>
              </td>
              <td>
                <%=I18n.get("AGENDA_TIMES")%>
              </td>
            </tr>
          </table>
        </td>
      </tr>
      <tr>
        <td colspan="8">
          <table width="100%">
            <tr>
              <td nowrap>
                <%
                  rb = new RadioButton(I18n.get(AgendaConstants.FLD_RECURRENT_YEARLY), AgendaConstants.FLD_RECURRENT_YEARLY, "2", "&nbsp;", null, false, "", pageState);
                  rb.id=AgendaConstants.FLD_RECURRENT_YEARLY+"2";
                  rb.toHtml(pageContext);%>
              </td>
              <td>
                <%
                   value = pageState.getEntry(AgendaConstants.FLD_RECURRENT_YEARLY_WEEKS).stringValueNullIfEmpty();

                  cbb = new Combo(AgendaConstants.FLD_RECURRENT_YEARLY_WEEKS, "</td><td>", "formElements", 10, (value != null ? value : ""), weeksOfMonth, "");
                  cbb.label = "";
                  cbb.required = false;
                  cbb.toHtml(pageContext);%>

              </td>
              <td>
                <%
                   value = pageState.getEntry(AgendaConstants.FLD_RECURRENT_YEARLY_DAYS).stringValueNullIfEmpty();

                  cbb = new Combo(AgendaConstants.FLD_RECURRENT_YEARLY_DAYS, "</td><td>", "formElements", 10, (value != null ? value : ""), daysOfWeek, "");
                  cbb.label = "";
                  cbb.required = false;
                  cbb.toHtml(pageContext);%>
              </td>
              <td>
                <%
                  value = pageState.getEntry(AgendaConstants.FLD_RECURRENT_YEARLY_MONTH_1).stringValueNullIfEmpty();

                  cbb = new Combo(AgendaConstants.FLD_RECURRENT_YEARLY_MONTH_1, "</td><td>", "formElements", 10, (value != null ? value : ""), months, "");
                  cbb.label = I18n.get(AgendaConstants.I18N_OF);
                  cbb.required = false;
                  cbb.toHtml(pageContext);%>
              </td>
              <td>
                <%
                  tf = new TextField(I18n.get(AgendaConstants.FLD_RECURRENT_YEARLY_HOWMANYTIMES), AgendaConstants.FLD_RECURRENT_YEARLY_HOWMANYTIMES_1, "</td><td>", 2, false);
                  tf.toHtml(pageContext);%>
              </td>
              <td nowrap>
                <%=I18n.get("AGENDA_TIMES")%>
              </td>
            </tr>
          </table>
        </td>
      </tr><%


    }

  %></table>