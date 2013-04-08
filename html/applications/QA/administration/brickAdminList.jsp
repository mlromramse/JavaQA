<%@ page contentType="text/html; charset=utf-8" pageEncoding="UTF-8"%><%@ page import="com.QA.Brick, com.QA.QAOperator, com.QA.waf.BrickDrawer, com.QA.waf.QAScreenApp, org.jblooming.operator.Operator,
org.jblooming.oql.QueryHelper, org.jblooming.page.HibernatePage, org.jblooming.page.Page, org.jblooming.persistence.PersistenceHome, org.jblooming.utilities.CodeValueList,
 org.jblooming.utilities.JSP, org.jblooming.waf.ScreenArea, org.jblooming.waf.constants.Commands, org.jblooming.waf.constants.Fields, org.jblooming.waf.constants.I18nConstants,
 org.jblooming.waf.html.button.ButtonImg, org.jblooming.waf.html.button.ButtonLink, org.jblooming.waf.html.button.ButtonSubmit, org.jblooming.waf.html.container.ButtonBar,
 org.jblooming.waf.html.display.Img, org.jblooming.waf.html.display.Paginator, org.jblooming.waf.html.input.CheckField, org.jblooming.waf.html.input.Combo,
 org.jblooming.waf.html.input.LoadSaveFilter, org.jblooming.waf.html.input.TextField, org.jblooming.waf.html.layout.Skin, org.jblooming.waf.html.state.Form,
 org.jblooming.waf.html.table.ListHeader, org.jblooming.waf.settings.I18n, org.jblooming.waf.state.PersistentSearch, org.jblooming.waf.view.PageSeed, org.jblooming.waf.view.PageState, java.util.List, org.hibernate.Query" %><%

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

    String hql = "select brick from " + Brick.class.getName() + " as brick order by brick.id desc";

    QueryHelper qhelp = new QueryHelper(hql);

    String filter = pageState.getEntry(Fields.FORM_PREFIX + "filter").stringValueNullIfEmpty();
    if (filter != null && filter.trim().length() > 0) {
      qhelp.addQBEClause("description","description",filter,QueryHelper.TYPE_CHAR);
    }

    boolean show_deleted_bricks = pageState.getEntry("SHOW_DELETED_BRICKS").checkFieldValue();
    if(!show_deleted_bricks)
      qhelp.addOQLClause("brick.deleted=:showHidden","showHidden",false);

    boolean show_template_bricks = pageState.getEntry("SHOW_TEMPLATE_BRICKS").checkFieldValue();
    if(!show_template_bricks)
      qhelp.addOQLClause("brick.template=:showtemplate","showtemplate",false);

    String rating = pageState.getEntry("RATING").stringValueNullIfEmpty();
    if (JSP.ex(rating)) {
      qhelp.addOQLClause("brick.contentRating=:contentRating","contentRating",rating);
    }
    
    

    PersistentSearch.saveSearch("BRICK_LIST", pageState);

    ListHeader.orderAction(qhelp, "BRICKLH", pageState);
    Query query = qhelp.toHql().getQuery();
    query.setMaxResults(51);
    pageState.setPage(
            HibernatePage.getHibernatePageInstance(query,
                    Paginator.getWantedPageNumber(pageState),
                    Paginator.getWantedPageSize(pageState)));

    pageState.toHtml(pageContext);

  } else {

    QAOperator logged = (QAOperator) pageState.getLoggedOperator();
    logged.testIsAdministrator();

    PageSeed self = pageState.thisPage(request);
    self.setCommand(Commands.FIND);
    Form f = new Form(self);
    pageState.setForm(f);

    f.start(pageContext);

%>



<div class="admin">
  <h1><span>Brick Admin List</span></h1>


<div style="background-color: #ffffff; padding: 20px;">
<%

  TextField tfCode = new TextField("TEXT", "SEARCH_BRICK_TEXT", Fields.FORM_PREFIX + "filter", "<br>", 40, false);
  tfCode.label="Contents";
  tfCode.addKeyPressControl(13, "$('" + f.id + "').submit();", "onkeyup");

  CheckField show_deleted_bricks = new CheckField(pageState.getI18n("SHOW_DELETED_BRICKS"),"SHOW_DELETED_BRICKS","",false);
  show_deleted_bricks.label="Show deleted";


  CheckField show_template_bricks = new CheckField(pageState.getI18n("SHOW_TEMPLATE_BRICKS"),"SHOW_TEMPLATE_BRICKS","",false);
  show_template_bricks.label="Include templates";




%><%tfCode.toHtml(pageContext);%><br>
  <%show_deleted_bricks.toHtml(pageContext);%><br>
  <%show_template_bricks.toHtml(pageContext);%><br><br>

  <%
    CodeValueList cvl = CodeValueList.getI18nInstance(pageState,"CENSORED","SIGNALLED","OFFTOPIC","DUPLICATE","ARGUMENTATIVE");
    cvl.addChoose(pageState);
    Combo c = new Combo("RATING","<br>",null,60,cvl,null);
    c.label="Content rating";
    c.toHtml(pageContext);
    
    
  %>

</div>


<%


  ButtonBar bb= new ButtonBar();
  bb.spacing = "2";
  /*LoadSaveFilter lsf = new LoadSaveFilter("TS", Operator.OPERATOR, f);
  bb.align = "right";
  bb.addButton(lsf);*/

  Skin skin = pageState.getSkin();
  PageSeed resetPage = pageState.thisPage(request);
  resetPage.setCommand(Commands.FIND);

  ButtonLink newB = new ButtonLink(pageState.pageInThisFolder("brickAdminEditor.jsp",request));
  newB.label = "create brick";
  newB.pageSeed.command = Commands.ADD;
  newB.pageSeed.mainObjectId = PersistenceHome.NEW_EMPTY_ID;
  bb.addButton(newB);

  bb.addSeparator(20);

  ButtonSubmit bs = new ButtonSubmit(f);
  bs.label = "<b>"+pageState.getI18n(I18nConstants.SEARCH)+"</b>";
  bb.addButton(bs);
%><div class="buttons"><%
  bb.toHtml(pageContext);
%></div><%

  Page bricks = pageState.getPage();

  if (bricks != null || pageState.getCommand() != null) {

  %>
<table border="0" style="background-color: #f8d4ba;" width="100%">
  <tr> <td> &nbsp;</td> <td><%=(bricks != null && bricks.getTotalNumberOfElements()>50?"<big>MORE THAN 50 RESULTS - OMITTED</big><br>":"")%><% new Paginator("OPPG", f, pageState).toHtml(pageContext); %> </td> </tr>
</table> <table width="100%" cellpadding="0" cellspacing="0" class="brickList"> <tr><%

  ListHeader lh = new ListHeader("BRICKLH", f);
  lh.addHeaderFitAndCentered("id");
  lh.addHeader("text", "brick.description");
  lh.addHeader("rating", "brick.contentRating");



  lh.toHtml(pageContext);

%></tr><%
  PageSeed edit = pageState.pageInThisFolder("brickAdminEditor.jsp", request);
  if (bricks != null) {
    for (Brick brick: (List<Brick>)bricks.getThisPageElements()) {
      edit.setMainObjectId(brick.getId());
      ButtonLink editLink = ButtonLink.getTextualInstance("", edit);
      ButtonImg bEdit = new ButtonImg(editLink,new Img(skin.imgPath+"list/edit.gif",""+brick.getId()));

      edit.setCommand(Commands.EDIT);

%><tr class="alternate" >
  <td class="<%=JSP.w(brick.getContentRating())%>"><%=brick.getId()%><%bEdit.toHtml(pageContext);%></td>
  <td class="<%=JSP.w(brick.getContentRating())%>"><%
    new BrickDrawer(brick).toHtml(pageContext);
  %></td>
  <td class="<%=JSP.w(brick.getContentRating())%>"><%=JSP.w(brick.getContentRating())%></td>
</tr><%
    }
  }

%></table><%
    }

    f.end(pageContext);
  }

%></div>