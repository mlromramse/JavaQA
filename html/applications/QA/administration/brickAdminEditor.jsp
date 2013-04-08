<%@ page contentType="text/html; charset=utf-8" pageEncoding="UTF-8"%><%@ page import="com.QA.Brick, com.QA.QAOperator, com.QA.waf.QAScreenApp, net.sf.json.JSONObject, org.jblooming.persistence.PersistenceHome,
org.jblooming.utilities.CodeValue, org.jblooming.utilities.CodeValueList, org.jblooming.utilities.JSP, org.jblooming.waf.ScreenArea, org.jblooming.waf.constants.Commands,
org.jblooming.waf.html.button.ButtonLink, org.jblooming.waf.html.button.ButtonSubmit, org.jblooming.waf.html.container.ButtonBar, org.jblooming.waf.html.input.*,
org.jblooming.waf.html.state.Form, org.jblooming.waf.settings.I18n, org.jblooming.waf.view.PageSeed, org.jblooming.waf.view.PageState" %><%

  PageState pageState = PageState.getCurrentPageState();

  if (!pageState.screenRunning) {

    pageState.screenRunning = true;
    final ScreenArea body = new ScreenArea(request);
    QAScreenApp mpScreenApp = new QAScreenApp(body);
    mpScreenApp.hasRightColumn=false;
    mpScreenApp.register(pageState);
    pageState.getHeaderFooter().toolTip = I18n.g("QA_APP_NAME");
    pageState.perform(request, response);

    QAOperator logged = (QAOperator) pageState.getLoggedOperator();
    logged.testIsAdministrator();

    if (Commands.SAVE.equals(pageState.command)) {
      Brick edit = null;
      if (!PersistenceHome.NEW_EMPTY_ID.equals(pageState.mainObjectId))
        edit = Brick.load(pageState.mainObjectId);
      else
        edit = new Brick();

      int oId = pageState.getEntry("owner").intValueNoErrorCodeNoExc();
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

      edit.setCalderon(JSONObject.fromObject(pageState.getEntry("calderon").stringValue()));

      edit.store();
      pageState.mainObjectId = edit.getId();
    } 
    

    pageState.toHtml(pageContext);

  } else {

    QAOperator logged = (QAOperator) pageState.getLoggedOperator();
    logged.testIsAdministrator();


    Brick edit = null;
    if (!Commands.ADD.equals(pageState.command))
      edit = Brick.load(pageState.mainObjectId);
    else
      edit= new Brick();
    PageSeed self = pageState.thisPage(request);
    self.mainObjectId=pageState.mainObjectId;
    self.setCommand(Commands.SAVE);
    Form f = new Form(self);
    pageState.setForm(f);

    f.start(pageContext);


    pageState.addClientEntry("description", edit.getDescription());

    %>
<div class="admin">
  <h1><span>Create a new sparkle</span></h1>
 <div class="box new">
   <table cellpadding="10" cellspacing="20">
  <tr><td>
ID: <%=edit.getId()%></td></tr>

  <tr><td><%

    TextArea tf = new TextArea("description","</td><td>",70,2,null);
    tf.maxlength=140;
    tf.required=true;
    %><%tf.toHtml(pageContext);%></td></tr><%
  
    if (edit.getParent()!=null) {
      %><tr><td>parent: </td><td><%=edit.getParent().getDescription()%></td></tr><%
    }

    if (edit.getOwner()!=null) {
    pageState.addClientEntry("owner",edit.getOwner().getId());
    String hql = "select operator.id, operator.name  from " + QAOperator.class.getName() + " as operator ";

    String whereForId = "where operator.id = :" + SmartCombo.FILTER_PARAM_NAME;

    String whereForFiltering =
            " where operator.name  like :" + SmartCombo.FILTER_PARAM_NAME;

    SmartCombo owner = new SmartCombo("owner", hql, whereForFiltering, whereForId);
    owner.label="creator";
    owner.separator="</td><td>";
    %><tr><td><%owner.toHtml(pageContext);%></td></tr><%

    } else {
      %><tr><td>SYSTEM BRICK</td></tr><%
    }
    %><tr><td><%
    pageState.addClientEntry("deleted", edit.isDeleted());
    CheckField cf = new CheckField("deleted","</td><td>",true);
    cf.toHtml(pageContext);
    %></td></tr><%

/*
    pageState.addClientEntry("level", edit.getLevel());
    TextField level = TextField.getIntegerInstance("level");
    level.separator = "&nbsp;";
    level.toHtml(pageContext);
*/

    %>

  <tr><td><%


    pageState.addClientEntry("template", edit.isTemplate());
    cf = new CheckField("template","</td><td>",true);
    cf.toHtml(pageContext);
    %></td></tr>


  <tr><td><%

    pageState.addClientEntry("communityInterestRank", edit.getCommunityInterestRank());
    TextField communityInterestRank = TextField.getIntegerInstance("communityInterestRank");
    communityInterestRank.separator = "</td><td>";
    communityInterestRank.toHtml(pageContext);
    %></td></tr>

  <tr><td><%

    pageState.addClientEntry("contentRating", JSP.w(edit.getContentRating()));
    CodeValueList cvl = new CodeValueList();
    cvl.add("","");
    for (Brick.ContentRatingValue crv : Brick.ContentRatingValue.values()) {
      cvl.add(new CodeValue(crv.name(), crv.name()));
    }
    Combo c = new Combo("contentRating","</td><td>",null,30, cvl,null);
    c.toHtml(pageContext);
    %></td></tr>

  <tr><td><%

    pageState.addClientEntry("calderon",edit.getCalderon());
    TextArea tc = new TextArea("calderon","</td><td>",80,4,null);
    %><%tc.toHtml(pageContext);%></td></tr>

</table></div>
</div>

<%
    ButtonBar bb = new ButtonBar();
    bb.addButton(new ButtonLink("brick panel",pageState.pageInThisFolder("brickAdminPanel.jsp",request)));
    bb.addButton(new ButtonLink("brick list",pageState.pageInThisFolder("brickAdminList.jsp",request)));
    bb.addButton(ButtonSubmit.getSaveInstance(f,"SAVE"));
    bb.toHtml(pageContext);

    f.end(pageContext);
  }
%>