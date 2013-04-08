<%@ page
        import="com.QA.QAOperator, com.QA.Question, com.QA.QuestionRevision, com.QA.waf.UserDrawer, org.jblooming.utilities.JSP, org.jblooming.waf.ScreenArea, org.jblooming.waf.settings.I18n, org.jblooming.waf.view.PageState, java.util.List, com.QA.waf.QAScreenApp" %>
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

%> <div id="content">
<script type="text/javascript" src="../js/jsdiff.js"></script>
  <h2><span><%=I18n.g("QUESTION_REVISIONS")%></span></h2>

  <div class="QAMenu actions">

    <ul><li><a href="<%=q.getURL().toLinkToHref()%>" class="backBtn">&nbsp;&nbsp;&nbsp;<%=I18n.g("QUESTION_BACK")%>&nbsp;&nbsp;&nbsp;</a></li></ul>
  </div>

<%



  %><div class="contentBox revisions"><%

  List<QuestionRevision> questionRevisions = q.getQuestionRevisions();

  if (questionRevisions.size() > 0) {

    QuestionRevision current = QuestionRevision.createRevision(q,q.getOwner());
    questionRevisions.add(current);
    QuestionRevision latest = null;
    QuestionRevision previous = null;
    boolean firstDiff = true;
    for (QuestionRevision qr : questionRevisions) {



    //latest revision
    if (latest==null) {
      latest=qr;
      continue;
    }



%>

  <hr><%=I18n.g("QUESTION_REVISIONS_OF")%><h3 class="questionTitle"><a href="<%=q.getURL().toLinkToHref()%>"><%=q.getSubject()%></a></h3>
  <div>

    <div style="display: inline-block;"><%=I18n.g("QUESTION_REVISIONS_FROM")%>
  <%

    QAOperator rev = latest.getEditor();
    if (firstDiff) {
      rev = q.getOwner();
      firstDiff = false;
    } else {
      rev = previous.getEditor();
    }
  %>

  <%new UserDrawer(rev,true,30).toHtml(pageContext);%>
  <%=JSP.w(latest.getRevisionDate())%></div>

    <div style="display: inline-block;"><%=I18n.g("QUESTION_REVISIONS_TO")%>

  <%
    rev = latest.getEditor();
    /*if (qr.equals(current)) {
      rev =  latest.getEditor();
    } */
  %>
  <%new UserDrawer(rev,true,30).toHtml(pageContext);%>
  <%=JSP.w(qr.getRevisionDate())%></div></div><%

%><br>Subject:
<script>
  document.write(diffString(
          "<%=JSP.javascriptEncode(latest.getFormerSubject())%>",
          "<%=JSP.javascriptEncode(qr.getFormerSubject())%>"));
</script>
<br><br>

Question:
<script>
  document.write(diffString(
          "<%=JSP.javascriptEncode(latest.getFormerDescription())%>",
          "<%=JSP.javascriptEncode(qr.getFormerDescription())%>"));
</script>
<br><br>
<%
    previous = latest;
    latest = qr;

  }
    } else {
      %><%=I18n.g("QUESTION_HAS_NO_REVISIONS_YET")%><%
    }

    %></div></div><%

  }
%>