<%@ page import="org.jblooming.operator.Operator,
                org.jblooming.page.Page,
                org.jblooming.scheduler.Job,
                org.jblooming.scheduler.businessLogic.JobController,
                org.jblooming.waf.ScreenBasic,
                org.jblooming.waf.SessionState,
                org.jblooming.waf.constants.Commands,
                org.jblooming.waf.constants.Fields,
                org.jblooming.waf.constants.I18nConstants,
                org.jblooming.waf.html.button.ButtonImg,
                org.jblooming.waf.html.button.ButtonLink,
                org.jblooming.waf.html.button.ButtonSubmit,
                org.jblooming.waf.html.container.ButtonBar,
                org.jblooming.waf.html.container.Container,
                org.jblooming.waf.html.display.Img,
                org.jblooming.waf.html.display.Paginator,
                org.jblooming.waf.html.input.TextField,
                org.jblooming.waf.html.layout.Skin,
                org.jblooming.waf.html.state.Form,
                org.jblooming.waf.html.table.ListHeader,
                org.jblooming.waf.view.PageSeed,
                org.jblooming.waf.view.PageState,
                java.util.Iterator"%><%

PageState pageState = PageState.getCurrentPageState();
pageState.getLoggedOperator().testIsAdministrator();


if (!pageState.screenRunning) {

  ScreenBasic.preparePage(new JobController(),pageContext);
  pageState.perform(request, response).toHtml(pageContext);

} else {

  SessionState sessionState = SessionState.getSessionState(request);
  Operator loggedUser=pageState.getLoggedOperator();
  Skin skin = sessionState.getSkin();
  PageSeed self = pageState.thisPage(request);
  self.setCommand(Commands.FIND);
  Form f = new Form(self);
  f.addKeyPressControl(13, "this.submit();", "onkeyup");
  
  pageState.setForm(f);
  f.start(pageContext);

  Container filter = new Container();
  filter.title = I18n.get("JOB_LIST");
  filter.start(pageContext);
  TextField tfCode = new TextField("TEXT", I18n.get("JOB_NAME"), Fields.FORM_PREFIX + "filter", "</td><td>", 30, false);
  tfCode.addKeyPressControl(13, "obj('" + f.id + "').submit();", "onkeyup");
%>
<table class="table" cellspacing="5">
  <tr>
    <td width="10%" nowrap >
      <%tfCode.toHtml(pageContext);%>
    </td>
  </tr>
</table>
<%
  ButtonBar bb= new ButtonBar();

  PageSeed sm = new PageSeed("scheduleManager.jsp");
  ButtonLink smlist = new ButtonLink(sm);
  smlist.label = I18n.get("SCHEDULER");
  bb.addButton(smlist);

  PageSeed ad = new PageSeed("jobEditor.jsp");
  ad.setCommand(Commands.ADD);
  ButtonLink add  = new ButtonLink(I18n.get("ADD"),ad);
  bb.addButton(add);

  ButtonSubmit bs = new ButtonSubmit(f);
  bs.label = "<b>"+I18n.get(I18nConstants.SEARCH)+"</b>";


  bb.addButton(bs);
  bb.toHtml(pageContext);
  Page jobs = pageState.getPage();
  if (jobs != null || pageState.getCommand() != null) {
%>
<table border="0" style="background-color: <%=skin.COLOR_BACKGROUND_TITLE02%>;" width="100%">
  <tr>
    <td>
      &nbsp;
    </td>
    <td>
      <% new Paginator("OPPG", f, pageState).toHtml(pageContext); %>
    </td>
  </tr>
</table>
<table width="100%">
  <tr>
<%
    ListHeader lh = new ListHeader("LH", f);
    lh.addHeaderFitAndCentered(I18n.get("EDIT_SHORT"));
    lh.addHeader("name", "job.name");
    lh.addHeader("description", "job.description");
    lh.addHeader("executable", "job.executable");
    lh.addHeaderFitAndCentered(I18n.get("DELETE_SHORT"));
    lh.toHtml(pageContext);
%>
  </tr>
<%
    PageSeed edit = new PageSeed("jobEditor.jsp");
    edit.setCommand(Commands.EDIT);
    if (jobs != null) {
      for (Iterator iterator = jobs.getThisPageElements().iterator(); iterator.hasNext(); ) {
        Job job = (Job) iterator.next();
        edit.setMainObjectId(job.getId());
        ButtonLink editLink = ButtonLink.getTextualInstance("", edit);
        ButtonImg bEdit = new ButtonImg(editLink,new Img(skin.imgPath+"list/edit.gif",""+job.getIntId()));
  boolean swap = false;
%>
  <a href="<%=edit.toLinkToHref()%>">
  <tr class="alternate" >
    <td>
      <%bEdit.toHtml(pageContext);%>
    </td>
    <td>
      <%=job.getName()%>
    </td>
    <td>
      <%=job.getDescription()!=null ? job.getDescription() : ""%>
    </td>
    <td>
      <%=job.getExecutable()!=null ? job.getExecutable() : ""%>
    </td>
<%
          PageSeed del = new PageSeed("jobEditor.jsp");
          del.setMainObjectId(job.getId());
          del.setCommand(Commands.DELETE_PREVIEW);
          ButtonLink delLink = new ButtonLink(del);
          ButtonImg bDel = new ButtonImg(delLink,new Img(skin.imgPath+"list/del.gif",""+job.getIntId()));
%>
    <td align="center">
      <%bDel.toHtml(pageContext);%>
    </td>
  </tr>
  </a>
<%
      }
    }
%> </table> <%
  }

  f.end(pageContext);
  filter.end(pageContext);
  pageState.setFocusedObjectDomId(tfCode.id);
}
%>
