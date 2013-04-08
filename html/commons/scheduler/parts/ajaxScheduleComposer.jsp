<%@ page import="org.jblooming.waf.SessionState, org.jblooming.waf.html.button.ButtonSubmit, org.jblooming.waf.html.input.RadioButton, org.jblooming.waf.html.input.ScheduleComposer, org.jblooming.waf.html.state.Form, org.jblooming.waf.view.PageState" %>
<%!
  public RadioButton addRB(String caption, String fieldName, String compType, Form f, PageState pageState, HttpServletRequest request) {

    ButtonSubmit bs = ButtonSubmit.getAjaxButton(f, "SCHEDULER_DIV");
    bs.variationsFromForm.href = pageState.pagePart(request).href;
    bs.preserveFormStatus = true;    
    RadioButton rb = new RadioButton(I18n.get(caption), fieldName, fieldName+compType, "&nbsp;",null,false,bs.generateJs().toString(),pageState);
    return rb;
  }
%>
<%
  PageState pageState = PageState.getCurrentPageState();
  SessionState sessionState = pageState.sessionState;

  ScheduleComposer composer = (ScheduleComposer) sessionState.getAttribute(ScheduleComposer.class.getName());
%><div id="SCHEDULER_DIV"> <table><tr><td nowrap valign="top"><%

  String comps = pageState.getEntry(composer.id + "_ts_").stringValueNullIfEmpty();  
  if (comps==null)
    pageState.addClientEntry(composer.id + "_ts_",composer.id + "_ts_"+ScheduleComposer.Schedule_Type.PERIOD.toString());

  if (composer.isMinute) {
    addRB("I18N_MINUTE", composer.id + "_ts_", ScheduleComposer.Schedule_Type.MINUTE.toString(), composer.form,pageState,request).toHtml(pageContext);
    %><br><%
  }
  if (composer.isSingle) {
    addRB("I18N_SINGLE", composer.id + "_ts_", ScheduleComposer.Schedule_Type.PERIOD.toString(), composer.form,pageState,request).toHtml(pageContext);
    %><br><%
  }
  if (composer.isDaily) {
    addRB("I18N_DAILY", composer.id + "_ts_", ScheduleComposer.Schedule_Type.DAILY.toString(), composer.form,pageState,request).toHtml(pageContext);
    %><br><%
  }
  if (composer.isWeekly) {
    addRB("I18N_WEEKLY", composer.id + "_ts_", ScheduleComposer.Schedule_Type.WEEKLY.toString(), composer.form,pageState,request).toHtml(pageContext);
    %><br><%
  }
  if (composer.isMonthly) {
    addRB("I18N_MONTHLY", composer.id + "_ts_", ScheduleComposer.Schedule_Type.MONTHLY.toString(), composer.form,pageState,request).toHtml(pageContext);
    %><br><%
  }
  if (composer.isYearly) {
    addRB("I18N_YEARLY", composer.id + "_ts_", ScheduleComposer.Schedule_Type.YEARLY.toString(), composer.form,pageState,request).toHtml(pageContext);
  }


%></td>
  <td style="border-left:1px solid gray" valign="top"><jsp:include page="ajaxSchedulerPane.jsp"/></td>
</tr></table></div>