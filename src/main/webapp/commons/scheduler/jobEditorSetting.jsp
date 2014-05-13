<%@ page import="org.jblooming.agenda.ScheduleSupport,
                 org.jblooming.persistence.PersistenceHome,
                 org.jblooming.persistence.objectEditor.FieldDrawer,
                 org.jblooming.persistence.objectEditor.FieldFeature,
                 org.jblooming.scheduler.Job,
                 org.jblooming.scheduler.businessLogic.JobController,
                 org.jblooming.waf.ScreenBasic,
                 org.jblooming.waf.SessionState,
                 org.jblooming.waf.constants.Commands,
                 org.jblooming.waf.constants.I18nConstants,
                 org.jblooming.waf.html.button.ButtonSubmit,
                 org.jblooming.waf.html.container.ButtonBar,
                 org.jblooming.waf.html.layout.Skin,
                 org.jblooming.waf.html.state.Form,
                 org.jblooming.waf.view.PageSeed,
                 org.jblooming.waf.view.PageState,
                 java.lang.reflect.Field"%><%
  
PageState pageState = PageState.getCurrentPageState();
pageState.getLoggedOperator().testIsAdministrator();

if (!pageState.screenRunning) {

  ScreenBasic.preparePage(new JobController(),pageContext);
  pageState.perform(request, response).toHtml(pageContext);

} else {

  SessionState sessionState = pageState.getSessionState();
  Skin skin = sessionState.getSkin();
  Job job = null;
  if(pageState.getMainObject() != null)
    job = (Job) pageState.getMainObject();
  PageSeed ps = pageState.thisPage(request);
  ps.setCommand(Commands.SAVE_CHILDREN);
  if(job == null)
    ps.setMainObjectId(PersistenceHome.NEW_EMPTY_ID);
  else
    ps.setMainObjectId(job.getIntId());
  Form form = new Form(ps);
%>
<form <%=form.toHtml()%>>
<%
  if(job != null && job.getExecutable() != null && !"".equals(job.getExecutable())) {
    Field[] fields = Class.forName(job.getExecutable()).getDeclaredFields();
    if(fields != null && fields.length>0) {
  %>
  <table>
  <%
      for(int i = 0; i < fields.length; i++) {
        Field field = fields[i];
        FieldFeature fieldFeature = new FieldFeature(field.getName(),field.getName());
        FieldDrawer fd = new FieldDrawer(fieldFeature,ScheduleSupport.class,form);
  %>
  <tr>
  <td>
  <%fd.toHtml(pageContext);%>
  </td>
  </tr>
  <%
      }
  }
%>
</table>
<%
  }
  ButtonBar bb = new ButtonBar();
  ButtonSubmit bs = new ButtonSubmit(form);
  bs.label=I18n.get(I18nConstants.SAVE);
  bb.addButton(bs);
  bb.toHtml(pageContext);
%>
</form>
<%
}
%>