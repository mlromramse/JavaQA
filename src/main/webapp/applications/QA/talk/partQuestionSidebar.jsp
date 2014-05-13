<%@ page contentType="text/html; charset=utf-8" pageEncoding="UTF-8" %>
<%@ page
        import="com.QA.Question, com.QA.Tag, org.jblooming.waf.settings.I18n, org.jblooming.waf.view.PageSeed, org.jblooming.waf.view.PageState, java.util.List" %>
<%
  PageState pageState = PageState.getCurrentPageState();
  Question q = Question.load(pageState.mainObjectId);

%>




<div class="contentBox right tags">
  <h3 class="inline"><span><%=I18n.g("QUESTION_TAGS")%></span></h3>

  <%

    List<Tag> tags = q.getTags();

    for (Tag tag : tags) {


      PageSeed t = pageState.pageFromRoot("talk/index.jsp");
      t.addClientEntry("WHAT","TAG");
      t.addClientEntry("TAG",tag.getId());

  %> <div class="tagDiv"><span><a href="<%=t.toLinkToHref()%>"><%=tag.getName()%></a></span> x<%=tag.getQuestionsCount()%></div><%

    }

  %><br style="clear: both"></div>



<div style="width: 100%;" class="contentBox right related">
<h3 class="inline"><span><%=I18n.g("QUESTION_RELATED")%></span></h3>
<%

   List<Question> qs = q.getRelatedQuestions();
  for (Question qst: qs) {
    %><div class="spacer"><a href="<%=qst.getURL().toLinkToHref()%>"><%=qst.getSubject()%></a></div><%
  }


  %> </div>

<jsp:include page="../parts/partBox1.jsp"/>
<jsp:include page="../parts/partBox2.jsp"/>
