<%@ page
        import="com.QA.QAOperator,
                com.QA.Question,
                com.QA.QuestionRevision,
                com.QA.Tag,
                com.QA.waf.QAScreenApp,
                com.QA.waf.UserDrawer,
                org.jblooming.utilities.JSP,
                org.jblooming.waf.ScreenArea,
                org.jblooming.waf.settings.I18n,
                org.jblooming.waf.view.PageState,
                java.util.ArrayList,
                java.util.Date,
                java.util.List" %>
<%!
  class QuestionRevisionLocal {

    private QAOperator editor;
    private Date revisionDate;
    private Question revisionOf;

    private String formerSubject;
    private String formerDescription;
    private List<Tag> formerTags = new ArrayList();

    public QuestionRevisionLocal() {

    }

    public QuestionRevisionLocal(QuestionRevision questionRevision) {

      super();
      this.formerDescription = questionRevision.getFormerDescription();

      this.formerSubject = questionRevision.getFormerSubject();
      this.formerTags = questionRevision.getFormerTags();
      this.revisionOf = questionRevision.getRevisionOf();
      this.editor = questionRevision.getEditor();
      this.revisionDate = questionRevision.getRevisionDate();
    }

  }
%>
<%
  PageState pageState = PageState.getCurrentPageState();

  if (!pageState.screenRunning) {

    pageState.screenRunning = true;
    final ScreenArea body = new ScreenArea(request);
    QAScreenApp lw = new QAScreenApp(body);
    lw.register(pageState);
    pageState.getHeaderFooter().toolTip = I18n.g("QA_APP_NAME") + " - " + I18n.g("QA_QUESTION_REVISIONS");
    pageState.perform(request, response);
    pageState.toHtml(pageContext);

  } else {

    Question q = Question.load(pageState.mainObjectId);

%>
<div id="content">
  <script type="text/javascript" src="../js/jsdiff.js"></script>
  <h2><span><%=I18n.g("QUESTION_REVISIONS")%></span></h2>

  <div class="QAMenu actions">

    <ul>
      <li><a href="<%=q.getURL().toLinkToHref()%>" class="backBtn">&nbsp;&nbsp;&nbsp;<%=I18n.g("QUESTION_BACK")%>&nbsp;&nbsp;&nbsp;</a>
      </li>
    </ul>
  </div>

  <div class="contentBox revisions"><%

    if (q.getQuestionRevisions().size() > 0) {

      List<QuestionRevisionLocal> questionRevisions = new ArrayList();
      List<QuestionRevision> revisions = q.getRevisions();

      //add a fake one
      QuestionRevisionLocal current = new QuestionRevisionLocal();
      current.formerDescription = q.getDescription();

      current.formerSubject = q.getSubject();
      current.formerTags = q.getTags();
      current.revisionOf = q;
      current.editor = revisions.get(0).getEditor();
      current.revisionDate = new Date();//q.getLastModified();
      questionRevisions.add(current);



      for (QuestionRevision qr : revisions) {
        questionRevisions.add(new QuestionRevisionLocal(qr));
      }


      QuestionRevisionLocal latest = null;

      int prog = 0;
      for (QuestionRevisionLocal qr : questionRevisions) {

        prog++;

        //latest revision
        if (latest == null) {
          latest = qr;
          continue;
        }
    %>
    <hr>
    <%=I18n.g("QUESTION_REVISIONS_OF")%><h3 class="questionTitle"><a
            href="<%=q.getURL().toLinkToHref()%>"><%=q.getSubject()%>
    </a></h3>

    <div>
      <%--<hr>--%>
      <%--QR: FROM <%=qr.formerDescription%> (user:<%=qr.editor.getDisplayName()%>) TO <%=latest.formerDescription%> (user:<%=latest.editor.getDisplayName()%>)--%>
      <%--<hr>--%>

      <div style="display: inline-block;"><%=I18n.g("QUESTION_REVISIONS_FROM")%>
        <%
          QAOperator rev = qr.editor;
          if (questionRevisions.get(questionRevisions.size() - 1).equals(qr))
            rev = q.getOwner();
          else {
            int index = questionRevisions.indexOf(qr)+1;
            rev =  questionRevisions.get(index).editor;
          }
        %>
        <%new UserDrawer(rev, true, 30).toHtml(pageContext);%>
        <%=JSP.w(latest.revisionDate)%>
      </div>

      <div style="display: inline-block;"><%=I18n.g("QUESTION_REVISIONS_TO")%>

        <%
          rev = qr.editor;
    /*if (qr.equals(current)) {
      rev =  latest.getEditor();
    } */
        %>
        <%new UserDrawer(rev, true, 30).toHtml(pageContext);%>
        <%=JSP.w(qr.revisionDate)%>
      </div>
    </div>
    <%

    %><br>Subject:
    <div id="Rev_<%=prog%>"></div>
    <script>
      $("#Rev_<%=prog%>").html(diffString(
              "<%=JSP.javascriptEncode(qr.formerSubject)%>",
              "<%=JSP.javascriptEncode(latest.formerSubject)%>"));
    </script>
    <br><br>

    Question:
    <div id="Revq_<%=prog%>"></div>

    <script>
      $("#Revq_<%=prog%>").html(diffString(
              "<%=JSP.javascriptEncode(qr.formerDescription)%>",
              "<%=JSP.javascriptEncode(latest.formerDescription)%>"));
    </script>
    <br><br>
    <%

        latest = qr;

      }
    } else {
    %><%=I18n.g("QUESTION_HAS_NO_REVISIONS_YET")%><%
      }

    %></div>

</div>
<%

  }
%>
