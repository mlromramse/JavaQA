<%@ page
        import="com.QA.*, com.QA.businessLogic.QATalkAction, com.QA.waf.AnswerDrawer, com.QA.waf.QAScreenApp, com.QA.waf.QuestionDrawer, net.tanesha.recaptcha.ReCaptcha, net.tanesha.recaptcha.ReCaptchaFactory, org.jblooming.PlatformRuntimeException, org.jblooming.utilities.JSP, org.jblooming.waf.ScreenArea, org.jblooming.waf.constants.Commands, org.jblooming.waf.html.button.ButtonSubmit, org.jblooming.waf.html.display.HeaderFooter, org.jblooming.waf.html.input.TextArea, org.jblooming.waf.html.state.Form, org.jblooming.waf.settings.ApplicationState, org.jblooming.waf.settings.I18n, org.jblooming.waf.view.PageSeed, org.jblooming.waf.view.PageState, java.util.ArrayList, java.util.List, java.util.Properties" %>
<%@ page import="org.jblooming.waf.exceptions.ActionException" %>
<%@ page import="org.jblooming.persistence.exceptions.PersistenceException" %>
<%@ page contentType="text/html; charset=utf-8" pageEncoding="UTF-8" %>
<%
    PageState pageState = PageState.getCurrentPageState();

    if (!pageState.screenRunning) {

        pageState.screenRunning = true;
        final ScreenArea body = new ScreenArea(request);
        QAScreenApp lw = new QAScreenApp(body);
        lw.register(pageState);
        lw.hasRightColumn = false;
        pageState.perform(request, response);

        if ("SAVE_ANSWER".equals(pageState.command)) {

            try {
                new QATalkAction().cmdSaveAnswer(request);
            } catch (ActionException e) {
                pageState.addMessageError(I18n.g(e.getMessage()));
            }

        } else if (Commands.DELETE_PREVIEW.equals(pageState.command)) {

            int aId = pageState.getEntry("ANSWER_ID").intValueNoErrorCodeNoExc();

            Answer a = Answer.load(aId);
            a.testPermission(pageState.getLoggedOperator(), QAPermission.ANSWER_EDIT);
            if (a.equals(a.getQuestion().getAcceptedAnswer())) {
                a.setAsRefuted();
            }
            a.setDeleted(true);
            a.store();
            PageSeed url = a.getQuestion().getURL();
            url.addClientEntry("HAPPILY_JUST_SAVED", "yes");
            response.sendRedirect(url.toLinkToHref());
            return;
        }

        Question q = Question.load(pageState.mainObjectId);
        pageState.getHeaderFooter().toolTip = I18n.g("QA_APP_NAME") + " - " + I18n.g("QA_QUESTION") +" "+q.getSubject();
        pageState.toHtml(pageContext);

    } else {

%>
<jsp:include page="../parts/partCommunityMenu.jsp">
    <jsp:param name="SHOW_ADD" value="no"></jsp:param><jsp:param name="SHOW_BACK" value="yes"></jsp:param>
</jsp:include>

<div id="content">
    <h3>&nbsp;</h3>
    <%

        QAOperator logged = (QAOperator) pageState.getLoggedOperator();

        if (JSP.ex(pageState.getEntry("TAG_NOT_CREATED").stringValueNullIfEmpty()))
            pageState.addMessageInfo(I18n.get("TAG_NOT_CREATED_%%", pageState.getEntry("TAG_NOT_CREATED").stringValueNullIfEmpty()));

        Question q = Question.load(pageState.mainObjectId);
        if (q.isDeleted() ) {
//      if (logged == null || !logged.isModerator()) {
//        throw new PlatformRuntimeException();
//      } else {
    %><h1 style="background-color: red"><span>NOTE: THIS QUESTION IS DELETED AND VISIBLE ONLY TO ADMINS</span></h1><%
        if (logged == null || !logged.isModerator())
            return;
    }


    //hit on view
    Object visitedInSession = pageState.sessionState.getAttribute("VISITEDQID");
    List<Integer> visitedbricksid = null;
    if (visitedInSession == null) {
        visitedbricksid = new ArrayList<Integer>();
        pageState.sessionState.setAttribute("VISITEDQID", visitedbricksid);
    } else
        visitedbricksid = (List<Integer>) visitedInSession;

    if (!visitedbricksid.contains(visitedbricksid) && (logged == null || q.getOwner() == null || !q.getOwner().equals(logged))) {
        q.hit(logged, QAEvent.QUESTION_VISITED);
        visitedbricksid.add(q.getIntId());
        q.store();
    }


    int aId = pageState.getEntry("ANSWER_ID").intValueNoErrorCodeNoExc();
    boolean editingAnAnswer = aId > 0;
    Answer a = Answer.load(aId);

%>
    <div class="contentBox"><%new QuestionDrawer(q).toHtml(pageContext);%></div>
    <%

        List<Answer> answersNotDeletedByRelevance = q.getAnswersByRelevance();
        if (answersNotDeletedByRelevance.size() > 0) {
    %><h3><span><%=I18n.g("QA_ANSWERS")%> <i>( <%=answersNotDeletedByRelevance.size()%> )</i></span></h3>

    <div class="contentBox answer"><%

        for (Answer an : answersNotDeletedByRelevance) {
            if (!(aId == an.getIntId()))
                new AnswerDrawer(an).toHtml(pageContext);

        }
    %></div>
    <%
        }

        boolean hasAnswerEditor = logged != null && (
                editingAnAnswer ||
                         q.hasPermissionFor(logged, QAPermission.ANSWER_CREATE)
        ) && !"yes".equals(pageState.getEntry("HAPPILY_JUST_SAVED").stringValue());

        if (hasAnswerEditor) {

            PageSeed pageSeed = pageState.thisPage(request);
            pageSeed.mainObjectId = q.getId();
            pageSeed.command = "SAVE_ANSWER";
            if (aId > 0) {
                pageSeed.addClientEntry("ANSWER_ID", a.getId());
                pageState.addClientEntry("yourAnswer", a.getText());
            }

            Form f = new Form(pageSeed);
            pageState.setForm(f);
    %>



    <div class="contentBox" style="margin-top: -20px"><h3 id="ANSWER_NOW" style="margin-top: 0; margin-bottom: 0"><span><%=I18n.g("QUESTION_YOUR_ANSWER")%></span></h3>
        <%


            f.start(pageContext);
            //todo: if not logged, go thorough FB login
            //TinyMCE ansEd = new TinyMCE("", "yourAnswer", "", 80, 6, pageState);
        %>
        <div class="wmd-panel">
            <div id="wmd-button-bar"></div>
            <%
                TextArea ta = new TextArea("yourAnswer", "", 80, 6, null);
                ta.id = "wmd-input";
                ta.required = true;
                ta.label = "";
                ta.fieldClass = JSP.w(ta.fieldClass) + " wmd-input";
                ta.toHtml(pageContext);
            %></div>
        <div id="wmd-preview" class="wmd-panel wmd-preview"></div>
        <br><%

            boolean recaptchaNeeded = true;
            recaptchaNeeded = !("yes".equals(pageState.getSessionState().getAttribute("PASSED_RECAPTCHA"))) && !q.hasPermissionFor(logged, QAPermission.ANSWER_CREATE_NO_RECAPTCHA);
            if (recaptchaNeeded) {
                ReCaptcha c1 = ReCaptchaFactory.newReCaptcha("6Lc0hQwAAAAAAGeeIfaQzbsALTT3ETogSCIaO-3x", "6Lc0hQwAAAAAAGxnyfgUo8o5-4NHdiOJ7H5TEGY-", false);

        %>
        <div>

  <span class="hint block"><%=I18n.get("RECAPTCHA_INTRO")%>
</span></div>

        <%
            Properties p = new Properties();
            p.setProperty("lang", "DE");
            p.setProperty("theme", "clean");
            out.print(c1.createRecaptchaHtml(I18n.g("RECAPTCHA_FAIL"), p));

        %><br><%
            }
        %><div class="btnBar"><%

            if (a != null && !a.isNew()) {
                ButtonSubmit bs = ButtonSubmit.getDeleteInstance(f, pageState);
                //bs.label = I18n.g("ANSWER_SAVE");
                bs.confirmRequire = true;
                bs.confirmQuestion = I18n.get("ANSWER_REMOVE_SURE");
                //bs.additionalOnClickScript="$(this).confirm('"+I18n.get("ANSWER_REMOVE_SURE")+"');";

                bs.additionalCssClass = "delete";
                bs.toHtml(pageContext);
        %>&nbsp;&nbsp;<%
            }

            ButtonSubmit bs = new ButtonSubmit(f);
            bs.label = I18n.g("ANSWER_SAVE");
            bs.additionalCssClass ="big";
            bs.toHtml(pageContext);
        %></div><%




            f.end(pageContext); %></div>
    <%

    } else if (answersNotDeletedByRelevance.size() == 0) {
    %><h2><%=I18n.g("QUESTION_NO_ANSWERS_YET")%></h2><%
    }

    if (q!=null && !q.isNew()) {

        if (logged == null) {

            PageSeed login = pageState.pageFromRoot("site/access/login.jsp");
            login.addClientEntry("LOGINPENDINGURL",q.getURL().href);

    %><div class="btnBar"><a href="<%=login.toLinkToHref()%>" title="<%=I18n.g("LOGIN_TO_ANSWER")%>" class="button"><%=I18n.g("LOGIN_TO_ANSWER")%></a></div><%
        }

%>
    <h3><span><%=I18n.get("QUESTION_SHARE_SOCIALLY_TITLE")%></span></h3>
    <div id="shareSocially">

        <p><%=I18n.get("QUESTION_SHARE_SOCIALLY_BODY")%>
            <%
                String qUrl = ApplicationState.serverURL + q.getURL().toLinkToHref();
            %><%=I18n.get("QUESTION_SHARE_SOCIALLY_LINK")%>: <a href="<%=qUrl%>"><%=qUrl%></a>

            <%
                String description = JSP.limWr(q.getDescription(), 15);

                String tweet  = JSP.urlEncode(I18n.get("QUESTION_EXPLORING_%%_%%", description, qUrl));
                String twitterURL="http://twitter.com/intent/tweet?text="+tweet;

            %>
            &nbsp;<a class="twitterBtn" title="<%=I18n.get("QUESTION_TWEET")%>" target="_blank" href="<%=twitterURL%>"><span class="icon">2</span> tweet</a></p>

    </div>

    <%
        }

        if (hasAnswerEditor) {
    %>
    <script>
        (function () {
            var converter1 = Markdown.getSanitizingConverter();
            var editor1 = new Markdown.Editor(converter1);
            editor1.run();
        })();
    </script>
    <%
        }
    %></div>

<div id="rightColumn">
    <%-- ads --%>
    <jsp:include page="partQuestionSidebar.jsp"/>
</div>


<%
    }
%>
