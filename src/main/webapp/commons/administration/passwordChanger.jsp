<%@ page import="org.jblooming.waf.view.PageState, org.jblooming.waf.ScreenBasic, org.jblooming.waf.view.PageSeed, org.jblooming.waf.html.state.Form, org.jblooming.waf.html.input.TextField, org.jblooming.waf.html.button.ButtonSubmit, org.jblooming.utilities.JSP, org.jblooming.operator.Operator" %><%
  PageState pageState = PageState.getCurrentPageState(request);
  pageState.getLoggedOperator().testIsAdministrator();

  if (!pageState.screenRunning) {

  ScreenBasic.preparePage(null,pageContext);
  pageState.perform(request, response).toHtml(pageContext);

 } else {
    Operator o = null;
    String noi = pageState.getEntry("opid").stringValueNullIfEmpty();
    if (JSP.ex(noi)) {
      o = Operator.load(noi);
    }
    String oln = pageState.getEntry("opln").stringValueNullIfEmpty();
    if (JSP.ex(oln)) {
      try {
      o = Operator.findByLoginName(oln);
      } catch (Throwable t) {
        %><big>not found</big><hr><%
      }
    }
     if (o!=null) {
      o.changePassword(pageState.getEntry("newPassword").stringValueNullIfEmpty());
      o.store();
      %><big>DONE!!</big><hr><%
     }


    PageSeed ps = pageState.thisPage(request);
    Form form = new Form(ps);
    pageState.setForm(form);
    form.start(pageContext);

    TextField opId = new TextField("opid","&nbsp;");
    opId.toHtml(pageContext);

    TextField opLN = new TextField("opln","&nbsp;");
    opLN.toHtml(pageContext);

    TextField np = new TextField("newPassword","&nbsp;");
    np.toHtml(pageContext);

    ButtonSubmit bs = ButtonSubmit.getSaveInstance(pageState);
    bs.toHtml(pageContext);


    form.end(pageContext);

  }

%>