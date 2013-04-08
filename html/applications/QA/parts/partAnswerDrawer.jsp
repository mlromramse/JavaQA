<%@ page
        import="com.QA.*, com.QA.waf.AnswerDrawer, com.QA.waf.UserDrawer, org.jblooming.utilities.JSP, org.jblooming.waf.html.core.JspIncluderSupport, org.jblooming.waf.html.input.TextArea, org.jblooming.waf.settings.I18n, org.jblooming.waf.view.PageSeed, org.jblooming.waf.view.PageState, java.util.List" %>
<%@ page import="org.jblooming.utilities.DateUtilities" %>
<%
  PageState pageState = PageState.getCurrentPageState();
  QAOperator logged = (QAOperator) pageState.getLoggedOperator();
  AnswerDrawer drawer = (AnswerDrawer) JspIncluderSupport.getCurrentInstance(request);
  Answer answer = drawer.answer;
  QAOperator user = answer.getOwner();


/*
________________________________________________________________________________________________________________________________________________________________________


    draw manifesto title - description - countdown

________________________________________________________________________________________________________________________________________________________________________

*/
  if ("DRAW_ANSWER".equals(request.getAttribute(AnswerDrawer.ACTION))) {


%>
<div class="answer <%if (answer.equals(answer.getQuestion().getAcceptedAnswer())) {%>accepted<%}%>">
  <div class="QWrapper">
    <%

      int size = answer.getUpvotes().size();

      // print all vote tools (vote up, vote down, favourite, question views)
    %>

    <div class="voteCell">
           <%
          if (answer.equals(answer.getQuestion().getAcceptedAnswer())) {
        %><span><span class="icon <%if (answer.equals(answer.getQuestion().getAcceptedAnswer())) {%>accepted<%}%>">a</span></span><%
        }
        Question question = answer.getQuestion();
        if (logged != null && !logged.equals(answer.getOwner())) {

          String title = "";
          if (size == 0)
            title = I18n.g("ANSWER_LIKE");
          else {
            //someone likes it but not you
            if (size > 0 && !answer.likedBy(logged))
              title =I18n.get("QA_LIKE_THIS_IN_TOTAL_%%",size+"");
            else {
              //only you like it
              if (size == 1 && answer.likedBy(logged))
                title = I18n.g("QA_YOU_LIKE");
              else {
                //you and others
                title = I18n.g("QA_ANSWER_LIKE_YOU_AND") + " " + (size) + " " + I18n.g("QA_LIKE_THIS");
              }
            }
          }

      %><span class="voteBtn"><a id='like_<%=answer.getId()%>' class="<%=answer.likedBy(logged) ? "answerUnlike" : "answerLike" %> icon tip"
            href="javascript:void(0);" title="<%=title%>"
                <%if (!answer.getOwner().equals(logged)) {%><%=" onclick=\"manageAnswerLike('" + answer.getId() + "')\""%><%}%>
                >i</a><%=("<em class='likeSize' id='likeSize" + answer.getId() + "'>" + size + "</em>")%></span><%

          } else if (logged == null) {
            PageSeed login = pageState.pageFromRoot("site/access/login.jsp");
            login.addClientEntry("LOGINPENDINGURL",question.getURL().href);

            %>
        <span class="voteBtn">
          <a id='like_<%=answer.getId()%>' class="icon disabled tip"
             href="<%=login.toLinkToHref()%>" title="<%=I18n.g("PLEASE_LOGIN_TO_VOTE")%>">i</a><%=("<em class='likeSize' id='likeSize" + answer.getId() + "'>" + size + "</em>")%>
        </span>

        <%

          } else {
        %>
        <span class="voteBtn">
          <a id='like_<%=answer.getId()%>' class="icon disabled tip"
             href="javascript:void(0);" title="<%=I18n.g("OWNER_CANT_LIKE_QUESTION")%>"<%--onclick="manageAnswerLike('<%=answer.getId()%>')"--%>>i</a><%=("<em class='likeSize' id='likeSize" + answer.getId() + "'>" + size + "</em>")%>
        </span>

        <%

          }%>


    </div>

<div class="contentWrapper">
    <script>
      var converter = Markdown.getSanitizingConverter();

      document.write(converter.makeHtml("<%=JSP.javascriptEncode(answer.getText())%>"));

    </script>


<span class="meta">
<%

  if (logged != null && (logged.equals(question.getOwner()) || logged.hasPermissionAsAdmin())) {
    if (answer.equals(question.getAcceptedAnswer())) {
%><a href="javascript:void(0)" onclick="refuteAnswer('<%=answer.getId()%>')"><span class="icon">x</span><%=I18n.g("ANSWER_ACCEPT_NO_MORE")%>
</a>|<%
} else {
%><a class="acceptThis" href="javascript:void(0)" onclick="acceptAnswer('<%=answer.getId()%>')"><span class="icon">a</span><%=I18n.g("ANSWER_ACCEPT")%>
</a>|<%
    }
  }

  if (logged != null && answer.hasPermissionFor(logged, QAPermission.ANSWER_EDIT)) {
    PageSeed qa = question.getURL();
    qa.addClientEntry("ANSWER_ID", answer.getId());
%> <a href="<%=qa.toLinkToHref() + "#ANSWER_NOW"%>"><%=logged.equals(question.getOwner()) ? I18n.g("ANSWER_EDIT") : I18n.g("IMPROVE_ANSWER")%>
</a>| <%
  }

  if (logged != null && !logged.equals(answer.getOwner())) {%>
 <a href="javascript:void(0)" class="report" id="aReportId_<%=answer.getId()%>" answerId="<%=answer.getId()%>"
       onclick="$(this).confirm(<%=!logged.isModerator() ? "answerReport" : "banAnswer"%>,'<%=!logged.isModerator() ? I18n.get("QUESTION_REPORT_SURE") : I18n.get("ANSWER_BAN_SURE")%>')">
    <%=!logged.isModerator() ? I18n.get("QUESTION_REPORT") : I18n.get("ANSWER_BAN")%></a>&nbsp;|<%

  }

  //do comment
  if (logged != null) {
%>&nbsp;<a title="<%=I18n.g("ADD_COMMENT")%>" class="addComment"
                           href="javascript:void(0);" onclick="
        $('#commA_<%=answer.getId()%>').slideDown('fast');"><span class="icon">&#164;</span><%=I18n.g("ADD_COMMENT")%>
</a>
  <%}%>

</span>
</div>
  </div>

  <div class="authors">
    <div class="createdBy"><%=I18n.get("ANSWER_CREATED_ON", DateUtilities.dateToString(question.getCreationDate(), "d MMMM yyyy"))%><br><%new UserDrawer(user, true, 30).toHtml(pageContext);%></div>
  </div>

  <%

    // print all comments
    List<Comment> comments = answer.getComments();
    if (comments.size() > 0) {
  %>
  <div class="QAComments"><h3 class="inline"><span><%=I18n.g("QA_COMMENTS")%></span></h3><%
    for (Comment c : comments) {


  %>
    <hr>
    <div id="commAId_<%=c.getId()%>" class="linkEnabled"><%=c.getText()%> <span
            class="commOwner"><%=I18n.get("BY")%> <%=c.getOwner().getDisplayName()%></span><%
      if (logged != null && logged.equals(c.getOwner()) || logged != null && logged.isModerator()) {
    %> <a href="javascript:void(0);" onclick="$(this).confirm(removeAnswerComment,'<%=I18n.get("QUESTION_COMMENT_REMOVE_SURE")%>');">(<%=I18n.get("QUESTION_COMMENT_REMOVE")%>
      )</a><%
    } else if (logged != null) {
      //report
    %> &nbsp;&nbsp;<a id="commAIdR_<%=c.getId()%>" href="javascript:void(0);"
                      onclick="$(this).confirm(commentAnswerReport,'<%=I18n.get("COMMENT_REPORT_SURE")%>');"><span class="icon btnGray" title="<%=I18n.get("COMMENT_REPORT")%>">D</span></a><%
      }
    %></div>
    <%

        }
        %></div><%
      }

      %>

    <div id="commA_<%=answer.getId()%>" style="display:none;" class="commentArea"><%
      TextArea ta = new TextArea("ANSWER_" + answer.getId(), "", 70, 2, null);
      ta.label = "";
      ta.script = "style='width:100%'";
      ta.toHtml(pageContext);

    %>
      <div class="btnBar">
        <button class="buttonSmall black" onclick="saveAnswerComment('<%=answer.getId()%>')"><%=I18n.g("COMMENT_SAVE")%>
        </button>
      </div>

    </div>


    </div>


<%
  }
%>
