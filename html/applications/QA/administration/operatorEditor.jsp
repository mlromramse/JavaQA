<%@ page import="com.QA.QAOperator, com.QA.Question, com.QA.waf.QAScreenApp, org.jblooming.utilities.JSP, org.jblooming.waf.ScreenArea, org.jblooming.waf.constants.Commands, org.jblooming.waf.html.button.ButtonLink, org.jblooming.waf.html.button.ButtonSubmit, org.jblooming.waf.html.container.ButtonBar, org.jblooming.waf.html.input.CheckField, org.jblooming.waf.html.state.Form, org.jblooming.waf.settings.I18n, org.jblooming.waf.view.PageSeed, org.jblooming.waf.view.PageState, java.util.List" %><%
  PageState pageState = PageState.getCurrentPageState();
  QAOperator loggedUser = (QAOperator) pageState.getLoggedOperator();
  loggedUser.testIsAdministrator();

  if (!pageState.screenRunning) {

    pageState.screenRunning = true;

    if (!JSP.ex(pageState.getEntry(Form.FLD_FORM_ORDER_BY + "OPLH").stringValueNullIfEmpty()))
      pageState.addClientEntry(Form.FLD_FORM_ORDER_BY +"OPLH","user.lastLoggedOn desc");


    pageState.screenRunning = true;
    final ScreenArea body = new ScreenArea(request);
    QAScreenApp mpScreenApp = new QAScreenApp(body);
    mpScreenApp.hasRightColumn=false;
    mpScreenApp.register(pageState);
    pageState.getHeaderFooter().toolTip = I18n.g("QA_APP_NAME");
    pageState.perform(request, response);

    if (Commands.SAVE.equals(pageState.command)) {
      QAOperator edit = QAOperator.load(pageState.mainObjectId);
      edit.setEnabled(pageState.getEntry("enabled").checkFieldValue());
      edit.getCalderon().put("moderator",pageState.getEntry("enabled").checkFieldValue());

      /*int oId = pageState.getEntry("owner").intValueNoErrorCodeNoExc();
      if (oId>0)
        edit.setOwner(QAOperator.load(oId));
      else if (edit.getOwner()==null)
        edit.setOwner(pageState.getLoggedOperator());


      edit.setDescription(pageState.getEntry("description").stringValue());

      edit.setDeleted(pageState.getEntry("deleted").checkFieldValue());
      edit.setTemplate(pageState.getEntry("template").checkFieldValue());
      if (edit.isTemplate())
        edit.setLevel(0);

      edit.setCommunityInterestRank(pageState.getEntry("communityInterestRank").intValue());

      String contentRating = pageState.getEntry("contentRating").stringValue();
      if (JSP.ex(contentRating))
        edit.setContentRating(contentRating);
      else
        edit.setContentRating(null);

      edit.setCalderon(JSONObject.fromObject(pageState.getEntry("calderon").stringValue())); */

      edit.store();
      pageState.mainObjectId = edit.getId();


    } else if ("BAN".equals(pageState.command)) {

      QAOperator edit = QAOperator.load(pageState.mainObjectId);
      edit.setEnabled(false);
      List<Question> bricks = edit.getQuestionsNotDeleted(10000);
      for (Question brick : bricks) {
        brick.setDeleted(true);
        brick.store();

      }
    }
    pageState.toHtml(pageContext);

  } else {

    QAOperator edit = QAOperator.load(pageState.mainObjectId);

    PageSeed self = pageState.thisPage(request);
    self.setCommand(Commands.EDIT);
    self.mainObjectId=pageState.mainObjectId;
    Form f = new Form(self);
    pageState.setForm(f);

    f.start(pageContext);
%>
<table>

  <tr><td>
    <img src="<%=edit.getGravatarUrl(180)%>" align="top" alt="<%=edit.getDisplayName()%>"
         title="<%=edit.getDisplayName()%>" width="89" class="avatar"><br>
    ID: <%=edit.getId()%><br><br>
    DisplayName <%=edit.getDisplayName()%><br><br>
    Email <%=JSP.w(edit.getEmail())%><br><br>
    UnverifiedEmail <%=JSP.w(edit.getUnverifiedEmail())%><br><br>
    Login name <%=edit.getLoginName()%><br><br>
    Login site <%=JSP.w(edit.getWebsite())%><br><br>



  </td>
    </tr>

  <tr><td><%
  pageState.addClientEntry("enabled", edit.isEnabled());
  CheckField cf = new CheckField("enabled","</td><td>",true);
  cf.toHtml(pageContext);
%></td></tr>

  <tr><td><%
    pageState.addClientEntry("moderator", edit.isModerator());
    cf = new CheckField("moderator","</td><td>",true);
    cf.toHtml(pageContext);
  %></td></tr>


  <%--tr>
    <td>
      <%
        final TextField nameTf = new TextField("TEXT",pageState.getI18n("FLD_NAME"), OperatorConstants.FLD_NAME,"</td><td>",15,!sst.canEditRootData);

        pageState.setFocusedObjectDomId(nameTf.id);
        nameTf.toHtml(pageContext);
      %>
    </td>
    <td>
      <%
        TextField surn=new TextField("TEXT",pageState.getI18n("FLD_SURNAME"), OperatorConstants.FLD_SURNAME,"</td><td>",40,!sst.canEditRootData);

        surn.toHtml(pageContext);
      %>
    </td>
  </tr>
  <tr>
    <td>
      <%
        TextField tf = new TextField( pageState.getI18n("FLD_LOGIN_NAME"),"LOGIN_NAME", "</td><td>", 15, !sst.canEditLoginName);
        tf.script = " autocomplete=\"off\"";
        tf.required = true;
        tf.toHtml(pageContext);
      %>
    </td>
  </tr>
  <tr>
    <td>
      <%
        tf = new TextField("PASSWORD",pageState.getI18n("FLD_PWD"),"PWD", "</td><td>", 15,false);
        tf.script = " autocomplete=\"off\"";
        //tf.required = true;
        tf.toHtml(pageContext);
      %>
    </td>
    <td>
      <%
        tf = new TextField("PASSWORD",pageState.getI18n("FLD_PWD_RETYPE"),"PWD_RETYPE", "</td><td>", 15,false);
        //tf.required = true;
        tf.script = " autocomplete=\"off\"";
        tf.toHtml(pageContext);
      %>
    </td>
  </tr--%>
  </table>
  <%
    ButtonBar bb2= new ButtonBar();

    bb2.addButton(new ButtonLink("ops list",pageState.pageInThisFolder("operatorList.jsp",request)));

    bb2.addSeparator(30);
    ButtonSubmit bs = new ButtonSubmit("ban","BAN",f);
    bs.confirmRequire=true;
    bb2.addButton(bs);

    bb2.addSeparator(30);
    bs = ButtonSubmit.getSaveInstance(f, pageState.getI18n("SAVE"));
    bb2.addButton(bs);

    bb2.toHtml(pageContext);

    f.end(pageContext);
  }
%>