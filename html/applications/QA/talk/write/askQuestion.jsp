<%@ page contentType="text/html; charset=utf-8" pageEncoding="UTF-8" %>
<%@ page import="com.QA.QAOperator,
                 com.QA.QAPermission,
                 com.QA.Question,
                 com.QA.Tag,
                 com.QA.businessLogic.QATalkAction,
                 com.QA.waf.QAScreenApp,
                 net.sf.json.JSONArray,
                 net.tanesha.recaptcha.ReCaptcha,
                 net.tanesha.recaptcha.ReCaptchaFactory,
                 net.tanesha.recaptcha.ReCaptchaImpl,
                 net.tanesha.recaptcha.ReCaptchaResponse,
                 org.jblooming.utilities.JSP,
                 org.jblooming.waf.ScreenArea,
                 org.jblooming.waf.constants.Commands,
                 org.jblooming.waf.exceptions.ActionException,
                 org.jblooming.waf.html.button.ButtonSubmit,
                 org.jblooming.waf.html.input.TextArea,
                 org.jblooming.waf.html.input.TextField,
                 org.jblooming.waf.html.state.Form,
                 org.jblooming.waf.settings.ApplicationState,
                 org.jblooming.waf.settings.I18n, org.jblooming.waf.view.PageSeed, org.jblooming.waf.view.PageState, java.util.List, java.util.Properties" %>
<%

  PageState pageState = PageState.getCurrentPageState();
  QAOperator logged = (QAOperator) pageState.getLoggedOperator();

  if (logged == null || !logged.isEnabled()) {

    %><h2><%=I18n.get("MUST_BE_LOGGED")%></h2><%
    return;
  }

  if (!pageState.screenRunning) {

    pageState.screenRunning = true;
    final ScreenArea body = new ScreenArea(request);
    QAScreenApp mpScreenApp = new QAScreenApp(body);
    mpScreenApp.hasRightColumn=false;
    mpScreenApp.register(pageState);
    pageState.getHeaderFooter().toolTip = I18n.g("QA_APP_NAME") + " - " + I18n.g("QA_TALK");
    pageState.perform(request, response);


    if (logged != null && Commands.SAVE.equals(pageState.command)) {

      Question qTmp = new Question();
      if (pageState.mainObjectId != null)
        qTmp = Question.load(pageState.mainObjectId);
      boolean recaptchaNeeded = !("yes".equals(pageState.getSessionState().getAttribute("PASSED_RECAPTCHA"))) && !qTmp.hasPermissionFor(logged, QAPermission.QUESTION_CREATE_NO_RECAPTCHA);
      boolean isResponseCorrect = false;
      if (recaptchaNeeded) {
        ReCaptchaImpl reCaptcha = new ReCaptchaImpl();
        reCaptcha.setPrivateKey("6Lc0hQwAAAAAAGxnyfgUo8o5-4NHdiOJ7H5TEGY-");

        String challenge = pageState.getEntry("recaptcha_challenge_field").stringValue();
        String uresponse = pageState.getEntry("recaptcha_response_field").stringValue();
        if (JSP.ex(challenge, uresponse)) {
          ReCaptchaResponse reCaptchaResponse = reCaptcha.checkAnswer(request.getRemoteAddr(), challenge, uresponse);
          isResponseCorrect = reCaptchaResponse.isValid();
        }
      }
      if (!recaptchaNeeded || isResponseCorrect) {
        if (isResponseCorrect)
          pageState.getSessionState().setAttribute("PASSED_RECAPTCHA", "yes");



        try {
          Question leaf = new QATalkAction().cmdSave();
        PageSeed url = leaf.getURL();
        /*String TAG_NOT_CREATED = pageState.getEntry("TAG_NOT_CREATED").stringValueNullIfEmpty();
        if (JSP.ex(TAG_NOT_CREATED)) {
          url.addClientEntry(pageState.getEntry("TAG_NOT_CREATED"));
        }*/
        response.sendRedirect(url.toLinkToHref());
        return;
        } catch (ActionException e) {
            pageState.addMessageError(I18n.g(e.getMessage()));
        }



//        } catch (ActionException e) {
//          pageState.getEntry("TAGS").errorCode = e.getMessage();
//        }
      } else {
        pageState.getEntry("recaptcha_response_field").errorCode = "RECAPTCHA_FAIL";
      }

    } else if (logged != null && Commands.DELETE.equals(pageState.command)) {

      new QATalkAction().cmdDelete();
      response.sendRedirect("/");
      return;
    }

    pageState.toHtml(pageContext);

  } else {

    boolean recaptchaNeeded = true;

    Question leaf = null;
    if (pageState.mainObjectId != null) {

      leaf = Question.load(pageState.mainObjectId);
      recaptchaNeeded = !("yes".equals(pageState.getSessionState().getAttribute("PASSED_RECAPTCHA"))) && !leaf.hasPermissionFor(logged, QAPermission.QUESTION_CREATE_NO_RECAPTCHA);
      pageState.addClientEntry("SUBJECT", leaf.getSubject());
      pageState.addClientEntry("QUESTION", leaf.getDescription());
      String tags = "";
      boolean isFirst=true;
      for (Tag tag : leaf.getTags()) {
        tags +=(!isFirst?",":"")+tag.getName();
        isFirst=false;
      }
      pageState.addClientEntry("TAGS", tags);

    } else {
      Question qTmp = new Question();
      qTmp.testPermission(logged, QAPermission.QUESTION_CREATE);
      recaptchaNeeded = !("yes".equals(pageState.getSessionState().getAttribute("PASSED_RECAPTCHA"))) && !qTmp.hasPermissionFor(logged, QAPermission.QUESTION_CREATE_NO_RECAPTCHA);

    }
    boolean isNew = leaf == null;


%>
<jsp:include page="../../parts/partCommunityMenu.jsp">
<jsp:param name="SHOW_ADD" value="no"></jsp:param><jsp:param name="SHOW_BACK" value="yes"></jsp:param>
</jsp:include>
<div id="content">

<link rel="stylesheet" type="text/css" href="../../css/jquery.tagit.css">
<link rel="stylesheet" type="text/css" href="../../css/jquery-ui.css">
<script src="../../js/tag-it.js" type="text/javascript" charset="utf-8"></script>

<h3><span><%=I18n.g("QAA_ASK_QUESTION")%></span></h3>

<div class="contentBox qa">

   <%
    PageSeed saveBrick = pageState.thisPage(request);
    if (!isNew)
      saveBrick.mainObjectId = leaf.getId();
    Form f = new Form(saveBrick);
    pageState.setForm(f);
    f.start(pageContext);


    TextField sub = new TextField("SUBJECT", "&nbsp;");
    //ta.maxlength = 140;
    sub.required = true;
    sub.label = "QUESTION_SUBJECT";
    sub.script = " style=\"width:100%;\" onkeydown=\"showSearch()\" onchange=\"showSearch()\"";

    %><h3 style="position: relative;"><%sub.toHtmlI18n(pageContext);%><span id="searchTopicDiv" style="display: none;"><a id="searchTopic" onclick="searchTopic()" href="javascript:void(0)"><%=I18n.g("SEARCH_THIS_TOPIC")%><span class="icon">L</span></a></span></h3>
    <span class="hint" style="float: right;">


     <script>

       function showSearch() {
         var val = $("#SUBJECT").val();
         var $std = $("#searchTopicDiv");
         if (val && val.trim().length>0 && !$std.is(":visible")){
           $std.fadeIn();
         } else if ( (!val || val.trim().length==0) && $std.is(":visible")){
           $std.fadeOut();
         }
       }

        function searchTopic() {
          var val = $("#SUBJECT").val();

          var target = "/applications/QA/site/search.jsp?FILTER="+val;
          $("#searchTopic").attr("href",target);
          $("#searchTopic").click();

        }
     </script>
    <%=I18n.get("USE_LINKS_AND_EMOTICONS")%></span>
  <span class="hint block"><%=I18n.g("QAA_BEFORE_ASK_QUESTION")%></span>
    <div class="wmd-panel">
    <div id="wmd-button-bar"></div>
    <%

      TextArea ta = new TextArea("QUESTION", "<hr class='break'>", 80, 8, null);
      //ta.maxlength = 140;
      ta.id="wmd-input";
      ta.required = true;
      ta.label = "";
      ta.fieldClass = JSP.w(ta.fieldClass) + " wmd-input";


/*
  ta.label="<span class='char'>"+I18n.get("140_CHARS")+"</span>";
*/
      ta.script = " style=\"width:100%;\"";
      ta.toHtml(pageContext);

    %></div>
  <div id="wmd-preview" class="wmd-panel wmd-preview"></div>
  <h3><%=I18n.g("QUESTION_TAGS")%>*</h3><%

  TextField tagsTF = new TextField("TAGS", "&nbsp;");
  //ta.maxlength = 140;
  //  tags.required = true;
  tagsTF.label = "";
  //tags.script = " style=\"width:70%;\"";
  %><%tagsTF.toHtmlI18n(pageContext);%>



  <br><%

    List<Object[]> tags = Tag.getMostUsedTagEntities(Integer.parseInt(ApplicationState.applicationSettings.get("QUESTION_TAG_CUTOFF")),10);
  if (tags.size()==0)
    tags = Tag.getMostUsedTagEntities(0,10);

    %><div><%

    for (Object[] o : tags) {

      Tag tag = (Tag) o[1];

      PageSeed t = pageState.pageFromRoot("talk/index.jsp");
      t.addClientEntry("WHAT","TAG");
      t.addClientEntry("TAG",tag.getId());

  %>

  <div class="tagDiv"><span><a href="javascript:void(0)" onclick="$('.ui-widget-content .ui-autocomplete-input').val('<%=JSP.javascriptEncode(tag.getName())%>');$('.ui-widget-content .ui-autocomplete-input').focus();"><%=tag.getName()%></a></span></div>


  <%

    }
  %></div><br style="clear: left;">


  <%--

  List<Object[]> tags = Tag.getMostUsedTagEntities(Integer.parseInt(ApplicationState.applicationSettings.get("QUESTION_TAG_CUTOFF")),10);

  for (Object[] o : tags) {

    Tag tag = (Tag) o[1];

    PageSeed t = pageState.pageFromRoot("talk/index.jsp");
    t.addClientEntry("WHAT","TAG");
    t.addClientEntry("TAG",tag.getId());

%>

  <div class="tagDiv"><span><a href="<%=t.toLinkToHref()%>"><%=tag.getName()%></a> <b><small>x <%=o[0]%></small></b></span></div>


  <%

    }


 --%><%


    if (recaptchaNeeded) {
      ReCaptcha c1 = ReCaptchaFactory.newReCaptcha("6Lc0hQwAAAAAAGeeIfaQzbsALTT3ETogSCIaO-3x", "6Lc0hQwAAAAAAGxnyfgUo8o5-4NHdiOJ7H5TEGY-", false);

  %>
  <div>
    <br>
    <br><span class="hint block"><%=I18n.get("RECAPTCHA_INTRO")%>
  </span></div>
  <%
      Properties p = new Properties();
      p.setProperty("lang", "it");
      p.setProperty("theme", "clean");
      out.print(c1.createRecaptchaHtml(I18n.g("RECAPTCHA_FAIL"), p));
    }

  %>
  <div class="btnBar"><%
    if (!isNew && leaf.getAnswersNotDeleted().size()==0) {
      ButtonSubmit bs = ButtonSubmit.getSaveInstance(f, I18n.get("QUESTION_DELETE"));
      bs.variationsFromForm.command = Commands.DELETE;
      bs.confirmRequire = true;
      bs.confirmQuestion = I18n.g("QUESTION_DELETE_SURE");
      bs.additionalCssClass = "delete";
      bs.toHtml(pageContext);
    }

    ButtonSubmit bs = ButtonSubmit.getSaveInstance(f, I18n.get("QUESTION_SAVE"));
    bs.additionalCssClass ="big";
    bs.toHtml(pageContext);
    f.end(pageContext);

  %></div>
</div>


<script> $(document).ready(function () {

  (function () {
    var converter1 = Markdown.getSanitizingConverter();
    var editor1 = new Markdown.Editor(converter1);
    editor1.run();

    <%
     JSONArray tagsA = new JSONArray();
      tagsA.addAll(Tag.getMostUsedTags(true,Integer.parseInt(ApplicationState.applicationSettings.get("QUESTION_TAG_CUTOFF"))));
    %>

    var mostUsedTags = <%=tagsA%>;

    $('#TAGS').tagit({
      availableTags: mostUsedTags,
      allowSpaces: true,
      removeConfirmation: true
    });



  })();

  $("#SUBJECT").focus();
}); </script>
    </div>

<div id="rightColumn">

  <div style="width: 100%;" class="contentBox right">
    <h3 class="inline accent"><span><%=I18n.g("QA_HOW_TO_ASK_TITLE")%></span></h3>

    <p><%=I18n.g("QA_HOW_TO_ASK_RULES")%></p>

    </div>

  <jsp:include page="../partTopTags.jsp"/>
</div>


<%
  }
%>