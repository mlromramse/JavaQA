<%@ page import="org.jblooming.persistence.PersistenceHome,
                 org.jblooming.persistence.objectEditor.FieldDrawer,
                 org.jblooming.persistence.objectEditor.FieldFeature,
                 org.jblooming.scheduler.Executable,
                 org.jblooming.scheduler.Job,
                 org.jblooming.scheduler.Parameter,
                 org.jblooming.scheduler.businessLogic.JobController,
                 org.jblooming.utilities.ReflectionUtilities,
                 org.jblooming.utilities.StringUtilities,
                 org.jblooming.waf.ScreenBasic,
                 org.jblooming.waf.SessionState,
                 org.jblooming.waf.constants.AgendaConstants,
                 org.jblooming.waf.constants.Commands,
                 org.jblooming.waf.constants.I18nConstants,
                 org.jblooming.waf.html.button.ButtonJS,
                 org.jblooming.waf.html.button.ButtonLink,
                 org.jblooming.waf.html.button.ButtonSubmit,
                 org.jblooming.waf.html.container.ButtonBar,
                 org.jblooming.waf.html.container.Container,
                 org.jblooming.waf.html.display.DeletePreviewer,
                 org.jblooming.waf.html.input.CheckField,
                 org.jblooming.waf.html.input.ScheduleComposer,
                 org.jblooming.waf.html.input.TextArea,
                 org.jblooming.waf.html.input.TextField,
                 org.jblooming.waf.html.layout.HtmlColors,
                 org.jblooming.waf.html.layout.Skin,
                 org.jblooming.waf.html.state.Form,
                 org.jblooming.waf.view.ClientEntry,
                 org.jblooming.waf.view.PageSeed,
                 org.jblooming.waf.view.PageState,
                 java.lang.reflect.Field,
                 java.util.List" %><%

  PageState pageState = PageState.getCurrentPageState();
  pageState.getLoggedOperator().testIsAdministrator();

  if (!pageState.screenRunning) {
    ScreenBasic.preparePage(new JobController(), pageContext);
    pageState.perform(request, response).toHtml(pageContext);
  } else {
    SessionState sessionState = pageState.getSessionState();
    Skin skin = sessionState.getSkin();
    Job root = (Job) pageState.getMainObject();
    PageSeed ps = pageState.thisPage(request);
    ps.setCommand(Commands.SAVE);
    if (root == null)
      ps.setMainObjectId(PersistenceHome.NEW_EMPTY_ID);
    else
      ps.setMainObjectId(root.getId());
    Form form = new Form(ps);
    form.alertOnChange=true;
    form.start(pageContext);
%>
<table width="100%" cellpadding="0" cellspacing="0" border="0">

<tr>
<td>
<%
  Container cn = new Container();
  cn.title = "job editor";
  cn.start(pageContext);
%>
<table class="table" cellpadding="3" cellspacing="10">
  <tr>
    <td width="40%"  bgcolor="<%=HtmlColors.modifyLight(skin.COLOR_BACKGROUND_MAIN, -10, -10, -10)%>"  >
      <table >
        <tr><% TextField tf = new TextField(I18n.get(AgendaConstants.FLD_NAME), AgendaConstants.FLD_NAME, "</td><td colspan=\"4\">", 30, false);
            tf.required=true;
        %> <td><%tf.toHtml(pageContext);%></td></tr>
        <tr>
          <%
            TextArea ta = new TextArea(I18n.get("DESCRIPTION"), "DESCRIPTION", "</td><td>", 30, 2, null);
          %> <td><%ta.toHtml(pageContext);%></td></tr>
        <tr><td>
          <% tf = new TextField(I18n.get(AgendaConstants.FLD_ESTIMATED_DURATION), AgendaConstants.FLD_ESTIMATED_DURATION, "</td><td>", 5, false); %> <%tf.toHtml(pageContext);%>&nbsp;&nbsp;&nbsp;
          <% tf = new TextField(I18n.get("timeoutTime"), "timeoutTime", "&nbsp;", 5, false);%> <%tf.toHtml(pageContext);%>
        </td></tr>

        <tr><td colspan="2">
          <%new CheckField(I18n.get("ENABLED"), "enabled", "&nbsp;", false).toHtml(pageContext);%>
          <%new CheckField(I18n.get("onErrorRetryNow"), "onErrorRetryNow", "&nbsp;", false).toHtml(pageContext);%>
          <%new CheckField(I18n.get("onErrorSuspendScheduling"), "onErrorSuspendScheduling", "&nbsp;", false).toHtml(pageContext);%>
        </td>
        </tr>

      </table>
    </td>


    <td valign="top" rowspan="2" width="60%"  bgcolor="<%=HtmlColors.modifyLight(skin.COLOR_BACKGROUND_MAIN, -10, -10, -10)%>">
      <table>
        <tr><% tf = new TextField(I18n.get("LAUNCHER_CLASS"), "LAUNCHER_CLASS", "</td></tr><tr><td colspan=\"4\">", 50, false);
        %> <td><%tf.toHtml(pageContext);%></td></tr>


        <tr><td colspan="4"><%=I18n.get("SCHEDULER_CLASS_HELP")%>
        </td></tr>
        <%
          if (!(PersistenceHome.NEW_EMPTY_ID.equals(root.getId()))) {

            ClientEntry clazz = pageState.getEntry("LAUNCHER_CLASS");

            if (clazz.stringValueNullIfEmpty() != null) {

              try {
                Class theClass = Class.forName(clazz.stringValue());
                if (ReflectionUtilities.getInheritedClasses(theClass).contains(Executable.class)) {
                  List<Field> flds = ReflectionUtilities.getDeclaredInheritedParameterFields(theClass, Parameter.class);

                    for (Field field : flds) {

                      String defaultValue = ((Parameter) field.getAnnotation(Parameter.class)).value();
                      //should not do make for existing but empty ces
                      if (pageState.getEntry(field.getName()).name==null && pageState.getEntry(field.getName()).stringValueNullIfEmpty() == null && defaultValue != null) {
                        pageState.addClientEntry(field.getName(), defaultValue);
                      }

                      FieldFeature fieldFeature = new FieldFeature(field.getName(), StringUtilities.deCamel(field.getName()));
                      FieldDrawer fd = new FieldDrawer(fieldFeature, theClass, form);
                      fd.autoSize = true;

        %>
        <tr><%fd.toHtml(pageContext);%></tr>
        <%
            }

        } else {
        %>
        <tr><td colspan="4"> <span style="color:<%=skin.COLOR_WARNING%>;"><%=I18n.get("CLASS_MUST_IMPLEMENT_EXECUTABLE")%></span> </td></tr>
        <%
          }
        } catch (ClassNotFoundException e) {
        %>
        <tr><td colspan="4"> <span style="color:<%=skin.COLOR_WARNING%>;"><%=I18n.get("CLASS_NOT_FOUND_IN_SERVER_PATH")%></span> </td></tr>
        <%
              }

            }
          }%>
      </table>
    </td>
  </tr>
  <tr>
    <td bgcolor=" <%=HtmlColors.modifyLight(skin.COLOR_BACKGROUND_MAIN, -20, -20, -20)%>" >

      <table>
        <tr>
          <td>
            <%
              ScheduleComposer c1 = new ScheduleComposer("ScheduleComposer", form);
              c1.isMinute = true;
              c1.toHtml(pageContext);
            %>
          </td>
        </tr>
      </table>

    </td>
  </tr>
</table>

<%
  ButtonBar bb = new ButtonBar();

  PageSeed sm = new PageSeed("scheduleManager.jsp");
  ButtonLink smlist = new ButtonLink(sm);
  smlist.label = I18n.get("SCHEDULER");
  bb.addButton(smlist);

  PageSeed ls = pageState.pageInThisFolder("jobList.jsp", request);
  ls.setCommand(Commands.FIND);
  ButtonLink list = new ButtonLink(ls);
  list.label = I18n.get("RETURN_TO_LIST");
  bb.addButton(list);

  if (root != null && !PersistenceHome.NEW_EMPTY_ID.equals(root.getId())) {
    ButtonSubmit del = new ButtonSubmit(form);
    del.label = I18n.get("DELETE");
    del.variationsFromForm.setCommand(Commands.DELETE_PREVIEW);
    bb.addButton(del);


    ButtonSubmit bs = new ButtonSubmit(form);
    bs.variationsFromForm.setCommand("RUN_NOW");
    bs.label = I18n.get("RUN_NOW");
    bb.addButton(bs);

  }

  ButtonJS breset = new ButtonJS();
  breset.label = I18n.get(I18nConstants.RESET);
  breset.onClickScript = form.getUniqueName() + ".reset();";
  bb.addButton(breset);

  ButtonSubmit bs = ButtonSubmit.getSaveInstance(form, I18n.get("SAVE"));
  bb.addButton(bs);

  bb.toHtml(pageContext);

  new DeletePreviewer(form).toHtml(pageContext);

  cn.end(pageContext);
%>
</td>
</tr>

</table>
<%
    form.end(pageContext);

  }
%>