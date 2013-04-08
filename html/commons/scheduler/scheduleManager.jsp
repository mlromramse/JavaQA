<%@ page import="org.jblooming.persistence.PersistenceHome,
                 org.jblooming.scheduler.Job,
                 org.jblooming.scheduler.Scheduler,
                 org.jblooming.utilities.DateUtilities,
                 org.jblooming.utilities.JSP,
                 org.jblooming.utilities.SmileyUtilities,
                 org.jblooming.waf.ScreenBasic,
                 org.jblooming.waf.SessionState,
                 org.jblooming.waf.constants.Commands,
                 org.jblooming.waf.constants.Fields,
                 org.jblooming.waf.exceptions.ActionException,
                 org.jblooming.waf.html.button.ButtonImg,
                 org.jblooming.waf.html.button.ButtonLink,
                 org.jblooming.waf.html.button.ButtonSubmit,
                 org.jblooming.waf.html.container.ButtonBar,
                 org.jblooming.waf.html.container.Container,
                 org.jblooming.waf.html.display.Img,
                 org.jblooming.waf.html.input.TextField,
                 org.jblooming.waf.html.layout.Skin,
                 org.jblooming.waf.html.state.Form,
                 org.jblooming.waf.html.table.ListHeader,
                 org.jblooming.waf.view.PageSeed,
                 org.jblooming.waf.view.PageState,
                 java.io.Serializable,
                 java.text.ParseException,
                 java.util.Date,
                 java.util.Map,
                 java.util.TreeSet"
  %><%

  PageState pageState = PageState.getCurrentPageState();
  pageState.getLoggedOperator().testIsAdministrator();

  if (!pageState.screenRunning) {

    pageState.screenRunning = true;

    if (Commands.START.equals(pageState.getCommand())) {

      try {
        long tick = pageState.getEntryAndSetRequired("TICK").longValue() * 1000;
        Scheduler.instantiate(tick, pageState.getLoggedOperator());
      } catch (ActionException e) {
      } catch (ParseException e) {
      }

    } else if (Commands.STOP.equals(pageState.getCommand())) {
      Scheduler.getInstance().stop();
    } else if ("UPDATE".equals(pageState.getCommand())) {
      Scheduler.getInstance().fillFromPersistence();
    }

    //make
    if (Scheduler.isRunning()) {
      pageState.addClientEntry("TICK", (Scheduler.getInstance().tick / 1000) + "");
    }

    ScreenBasic.preparePage(null, pageContext);
    pageState.perform(request, response).toHtml(pageContext);

  } else {

    SessionState sessionState = pageState.getSessionState();
    Skin skin = sessionState.getSkin();

    Form f = new Form(pageState.thisPage(request));
    f.url.addClientEntry(Fields.APPLICATION_NAME, pageState.getEntry(Fields.APPLICATION_NAME).stringValueNullIfEmpty());

  f.start(pageContext);

  Container main = new Container();
  main.title = I18n.get("SCHEDULER_MANAGER");
  main.start(pageContext);

%>
<table class="table"><tr><%

  if (Scheduler.getInstance() != null) {

      Date it = Scheduler.getInstance().instantiationTime;
      %><td><h1><%=SmileyUtilities.getTextWithSmileys(I18n.get("SCHEDULER_IS_RUNNING"),pageContext)%></h1>
  <%=I18n.get("SCHEDULER_INSTANTIATED_AT")%>&nbsp;<%=DateUtilities.dateAndHourToString(it)%></td><%
      %><td><%=I18n.get("SCHEDULER_INSTANTIATED_BY")%>&nbsp;<%=Scheduler.getInstance().instantiator%></td><%
    } else {
      %><td><h1><%=SmileyUtilities.getTextWithSmileys(I18n.get("SCHEDULER_IS_NOT_RUNNING"),pageContext)%></h1></td><%
    }


%><td><%=I18n.get("PAGE_DRAWN_AT_SERVER_TIME")%>&nbsp;<%=DateUtilities.dateAndHourToString(new Date())%></td><%

  TextField tf = new TextField("TICK", "&nbsp;");
  tf.label = I18n.get("TICK_IN_SECONDS");
  tf.fieldSize = 4;
  tf.disabled = Scheduler.isRunning();

%><td><%tf.toHtml(pageContext);%></td><%


%></tr></table>
<%

  ButtonBar bb = new ButtonBar();

  ButtonSubmit upd = new ButtonSubmit(f);
  upd.variationsFromForm.setCommand("UPDATE");
  upd.label = I18n.get("UPDATE");
  bb.addButton(upd);

  ButtonSubmit bs = new ButtonSubmit(f);
  bs.variationsFromForm.setCommand(Commands.START);
  bs.label = I18n.get("START");
  bs.enabled = !Scheduler.isRunning();
  bb.addButton(bs);

  bs = new ButtonSubmit(f);
  bs.variationsFromForm.setCommand(Commands.STOP);
  bs.label = I18n.get("STOP");
  bs.enabled = Scheduler.isRunning();
  bb.addButton(bs);

%><%bb.toHtml(pageContext);%><%

  main.end(pageContext);

  Scheduler scheduler = Scheduler.getInstance();
  PageSeed pi = new PageSeed("jobEditor.jsp");
  ButtonLink bl = new ButtonLink(pi);
  ButtonImg bEdit = new ButtonImg(bl, new Img(skin.imgPath + "list/edit.gif", ""));
  ButtonLink delLink = new ButtonLink(pi);
  ButtonImg bDel = new ButtonImg(delLink, new Img(skin.imgPath + "list/del.gif", ""));

  Container running = new Container();
  running.title = "jobs recently launched";//I18n.get("JOBS_RUNNING");
  running.start(pageContext);

  Map<Serializable, Scheduler.FutureIsPink> jf = null;
  if (scheduler != null)
    jf = scheduler.inExecution;

  if (scheduler != null && jf.keySet().size() > 0) {

    %>
    <table class="table"><tr><%
      ListHeader lh = new ListHeader("JOBS", f);
      lh.addHeaderFitAndCentered("id");
      lh.addHeader(I18n.get("NAME"));
      lh.addHeader("last run time");
      lh.toHtml(pageContext);
    %></tr><%


  for (Serializable jobId : jf.keySet()) {

    Job job = (Job) PersistenceHome.findByPrimaryKey(Job.class, (Integer) jobId);
    %><tr class="alternate" >
      <td><%=job.getId()%></td>
      <td><%=job.getName()%></td>
      <td><%=JSP.timeStamp(new Date(job.getLastExecutionTime()))%></td>
    </tr><%

  }
    %></table> <%
  } else {
    %><%=I18n.get("NO_JOBS_RUNNING")%><br><%
  }
  running.end(pageContext);


  Container jobs = new Container();
  jobs.title = I18n.get("JOBS_TO_BE_EXECUTED");
  jobs.start(pageContext);

  TreeSet<Scheduler.OrderedJob> ojs = null;
  if (scheduler != null)
    ojs = scheduler.toBeExecuted;

  if (scheduler != null && ojs != null && ojs.size() > 0) {

%>
<table class="table"><tr><%
  ListHeader lh = new ListHeader("JOBS", f);
  lh.addHeaderFitAndCentered(I18n.get("EDIT_SHORT"));
  lh.addHeader("next run time");
  lh.addHeader("last run time");
  lh.addHeader(I18n.get("NAME"));
  lh.addHeader(I18n.get("RUN_NOW"));
  lh.addHeaderFitAndCentered(I18n.get("DELETE_SHORT"));
  lh.toHtml(pageContext);
%></tr><%

  boolean swap = false;

  for (Scheduler.OrderedJob oj : ojs) {
    Job job = (Job) PersistenceHome.findByPrimaryKey(Job.class, oj.jobId);
    if (job != null) {
      pi.setMainObjectId(job.getId());
      pi.setCommand(Commands.EDIT);

    %> <tr class="alternate" >

  <td><%bEdit.toHtml(pageContext);%></td>
  <td nowrap><%=DateUtilities.dateAndHourToString(oj.exeTimeDate)%></td>
  <td nowrap><%=job.getLastExecutionTime()>0 ? DateUtilities.dateToRelative(new Date(job.getLastExecutionTime())) : "never run"%></td>
  <td><%=job.getName()%></td> <%

  PageSeed edit = pageState.pageInThisFolder("jobEditor.jsp", request);
  edit.mainObjectId = job.getId();
  edit.setCommand("RUN_NOW");
  ButtonLink runNowLink = new ButtonLink(edit);
  runNowLink.label = "RUN_NOW";
  %><td align="center"><%runNowLink.toHtmlI18n(pageContext);%></td>
  <%pi.setCommand(Commands.DELETE_PREVIEW);%>
  <td><%bDel.toHtml(pageContext);%></td>

</tr><%
    }
  }
%></table>
<%
} else {
%><%=I18n.get("NO_JOBS_TO_BE_EXECUTED")%><br><%
  }

  jobs.end(pageContext);

  ButtonBar bb2 = new ButtonBar();

  PageSeed ps = new PageSeed("jobList.jsp");
  ps.addClientEntry(Fields.APPLICATION_NAME, pageState.getEntry(Fields.APPLICATION_NAME).stringValueNullIfEmpty());
  ButtonLink bll = new ButtonLink(I18n.get("JOB_LIST"), ps);

  bb2.addButton(bll);
  bb2.toHtml(pageContext);

  f.end(pageContext);

  }
%>