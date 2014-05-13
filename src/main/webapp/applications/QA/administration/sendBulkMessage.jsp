<%@ page import = "com.QA.QAOperator,
                 org.jblooming.agenda.CompanyCalendar, org.jblooming.oql.OqlQuery, org.jblooming.utilities.JSP, org.jblooming.waf.ScreenArea, org.jblooming.waf.constants.Commands,
                 org.jblooming.waf.html.button.ButtonSubmit, org.jblooming.waf.html.container.ButtonBar, org.jblooming.waf.html.container.Container, org.jblooming.waf.html.input.TextArea, org.jblooming.waf.html.layout.Css,
                 org.jblooming.waf.html.state.Form, org.jblooming.waf.view.PageSeed, org.jblooming.waf.view.PageState, java.util.List, com.QA.waf.QAScreenApp"%><%

  PageState pageState = PageState.getCurrentPageState();
  QAOperator loggedUser = (QAOperator) pageState.getLoggedOperator();
  loggedUser.testIsAdministrator();

  if (!pageState.screenRunning) {
    pageState.screenRunning = true;

    if (!JSP.ex(pageState.getEntry(Form.FLD_FORM_ORDER_BY +"OPLH").stringValueNullIfEmpty()))
      pageState.addClientEntry(Form.FLD_FORM_ORDER_BY +"OPLH","user.lastLoggedOn desc");
    final ScreenArea body = new ScreenArea(request);
    new QAScreenApp(body).register(pageState);
    pageState.perform(request, response);

    if ("SEND_MESSAGE".equals(pageState.command)) {
      String message = pageState.getEntry("MESSAGE_TO_OPS").stringValueNullIfEmpty();
      if (JSP.ex(message)) {

        //String hql = "select sum(hit.weight), operatorId from " + Hit.class.getName() + " as hit  group by operatorId having sum(hit.weight)>5";
        //String hql = "select operatorId from " + QAOperator.class.getName() + " as op  group by operatorId having sum(hit.weight)>5";

        String hql = "select op from " + QAOperator.class.getName() + " as op where op.lastLoggedOn>:daysAgo";
        OqlQuery  oql = new OqlQuery(hql);
        CompanyCalendar  cc = new CompanyCalendar();
        cc.add(CompanyCalendar.DAY_OF_YEAR,-7);
        cc.setAndGetTimeToDayStart();
        oql.getQuery().setDate("daysAgo",cc.getTime());

        List<QAOperator>  ops= oql.list();

      }
    }

    pageState.toHtml(pageContext);

  } else {

  PageSeed self = pageState.thisPage(request);
  self.setCommand(Commands.FIND);
  Form f = new Form(self);
  pageState.setForm(f);

  f.start(pageContext);

%><table width="100%" cellpadding="0" cellspacing="0" border="0"> <%

  Container filter = new Container("Send message to active user");
  filter.start(pageContext);


  %><table cellspacing="5" border="0">
  <tr><td COLSPAN="2" ><H1 STYLE="COLOR:RED"> THIS PAGE IS USED TO SEND A MESSAGE TO ALL ACTIVE USERS IN QA - PAY ATTENTION!</H1></td></tr>
  <tr><td width="10%" height="50" nowrap ><%
  TextArea ta = new TextArea("MESSAGE_TO_OPS","</td><td>",150,14,null);
  ta.label = "Send message to active user";
  ta.toHtmlI18n(pageContext);
    %></td>
    </tr>
</table><%
  filter.end(pageContext);

  ButtonBar bb= new ButtonBar();
  bb.spacing = "2";


  PageSeed resetPage = pageState.thisPage(request);
  resetPage.setCommand(Commands.FIND);

  ButtonSubmit bs = new ButtonSubmit(f);
  bs.variationsFromForm.command = "SEND_MESSAGE";
  bs.confirmRequire = true;
  bs.label = "<b>send message TO ALL ACTIVE USERS IN YCM</b>";
  bb.addButton(bs);

  bb.toHtml(pageContext);
  f.end(pageContext);
  }
%>
