<%@ page import ="com.QA.QAOperator,com.QA.waf.QAScreenApp,
                  org.jblooming.operator.businessLogic.OperatorController, org.jblooming.page.Page,org.jblooming.utilities.JSP, org.jblooming.waf.ScreenArea, org.jblooming.waf.constants.Commands,
                  org.jblooming.waf.constants.Fields, org.jblooming.waf.constants.I18nConstants,org.jblooming.waf.html.button.ButtonImg, org.jblooming.waf.html.button.ButtonLink,
                  org.jblooming.waf.html.button.ButtonSubmit,
                  org.jblooming.waf.html.container.ButtonBar, org.jblooming.waf.html.container.Container,org.jblooming.waf.html.display.Img,
                  org.jblooming.waf.html.display.Paginator,org.jblooming.waf.html.input.CheckField,
                  org.jblooming.waf.html.input.LoadSaveFilter, org.jblooming.waf.html.input.TextArea, org.jblooming.waf.html.input.TextField,
                  org.jblooming.waf.html.layout.Skin, org.jblooming.waf.html.state.Form, org.jblooming.waf.html.table.ListHeader, org.jblooming.waf.settings.I18n, org.jblooming.waf.view.PageSeed, org.jblooming.waf.view.PageState, java.util.List"%><%

  PageState pageState = PageState.getCurrentPageState();
  QAOperator loggedUser = (QAOperator) pageState.getLoggedOperator();
  loggedUser.testIsAdministrator();

  if (!pageState.screenRunning) {

    pageState.screenRunning = true;

    if (!JSP.ex(pageState.getEntry(Form.FLD_FORM_ORDER_BY +"OPLH").stringValueNullIfEmpty()))
      pageState.addClientEntry(Form.FLD_FORM_ORDER_BY +"OPLH","user.lastLoggedOn desc");


    pageState.screenRunning = true;
    final ScreenArea body = new ScreenArea(new OperatorController(QAOperator.class),request);
    QAScreenApp mpScreenApp = new QAScreenApp(body);
    mpScreenApp.hasRightColumn=false;
    mpScreenApp.register(pageState);
    pageState.getHeaderFooter().toolTip = I18n.g("QA_APP_NAME");
    pageState.perform(request, response);
    pageState.toHtml(pageContext);



  } else {

  PageSeed self = pageState.thisPage(request);
  self.setCommand(Commands.FIND);
  Form f = new Form(self);
  pageState.setForm(f);

  f.start(pageContext);

%>

<h2><span><%=I18n.g("QA_USERS")%></span></h2>
<div class="box"><table cellpadding="0" width="90%" cellspacing="0" border="0" style="background-color: #fff;"><%
    %>

 <table  cellspacing="5" border="0">  <tr><td colspan="4"></td></tr>
  <tr><%

  TextField tfCode = new TextField("TEXT", I18n.get("SEARCH_OPERATOR_NAME"), Fields.FORM_PREFIX + "filter", "</td><td>", 30, false);
  CheckField ck_showHiddenOperator = new CheckField(I18n.get("SHOW_HIDDEN_OPERATOR"),"SHOW_HIDDEN_OPERATOR","</td><td>",true);

  %>
  <td width="10%" height="50" nowrap ><%tfCode.toHtml(pageContext);%></td>
  <td width="10%" height="50" nowrap ><%ck_showHiddenOperator.toHtml(pageContext);%></td>
    </tr>




</table></div><%


  ButtonBar bb= new ButtonBar();
  bb.spacing = "2";
  LoadSaveFilter lsf = new LoadSaveFilter("TS", "OPS", f);
  bb.align = "right";  /* aggiunto per allineare a destra come in tutta la piattaforma*/
  bb.addButton(lsf);

  Skin skin = pageState.getSkin();
  PageSeed resetPage = pageState.thisPage(request);
  resetPage.setCommand(Commands.FIND);

  ButtonSubmit bs = new ButtonSubmit(f);
  bs.label = "<b>"+I18n.get(I18nConstants.SEARCH)+"</b>";
  bb.addButton(bs);


  bb.toHtml(pageContext);

  Page operators = pageState.getPage();

  if (operators != null || pageState.getCommand() != null) {

  %><table border="0" style="background-color: <%=skin.COLOR_BACKGROUND_TITLE02%>;" width="100%">
      <tr> <td> &nbsp;</td> <td><% new Paginator("OPPG", f, pageState).toHtml(pageContext); %> </td> </tr>
      </table> <table width="100%" bgcolor="white" style="padding: 20px" class="admin"> <tr><%

          ListHeader lh = new ListHeader("OPLH", f);
          lh.addHeaderFitAndCentered(I18n.get("EDIT_SHORT"));
          lh.addHeaderFitAndCentered("id");
          lh.addHeader("login name", "user.loginName");
          lh.addHeader("email","user.email");
          lh.addHeader("unver.email","user.unverifiedEmail");
          lh.addHeader("creation date", "user.creationDate");
          lh.addHeader("last login", "user.lastLoggedOn");


          lh.toHtml(pageContext);

      %></tr><%
        PageSeed edit = pageState.pageInThisFolder("operatorEditor.jsp",request);
        if (operators != null) {
          for (QAOperator operator: (List<QAOperator>)operators.getThisPageElements()) {
            edit.setMainObjectId(operator.getId());
            ButtonLink editLink = ButtonLink.getTextualInstance("", edit);
            ButtonImg bEdit = new ButtonImg(editLink,new Img(skin.imgPath+"list/edit.gif",""+operator.getId()));

             edit.setCommand(Commands.EDIT);

          %><tr class="alternate">
                <td align="center">
                  <img src="<%=operator.getGravatarUrl(30)%>" width="30">
                </td>
                <td><%=operator.getId()%><%bEdit.toHtml(pageContext);%></td>
                <td><%=operator.getLoginName()%></td>
                <td><%=JSP.w(operator.getEmail())%></td>
                <td><%=JSP.w(operator.getUnverifiedEmail())%></td>
                <td><%=JSP.timeStamp(operator.getCreationDate())%></td>
                <td><%=JSP.timeStamp(operator.getLastLoggedOn())%></td>
            </tr><%
          }
        }
  
      %></table><%
  }

  f.end(pageContext);
  }
%>
