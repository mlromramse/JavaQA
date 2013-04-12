<%@ page import="com.QA.*, com.QA.waf.QuestionDrawer, com.QA.waf.UserDrawer, org.jblooming.utilities.JSP, org.jblooming.waf.constants.Commands,
org.jblooming.waf.html.core.JspIncluderSupport, org.jblooming.waf.html.input.CheckField, org.jblooming.waf.html.input.TextArea, org.jblooming.waf.settings.I18n, org.jblooming.waf.view.PageSeed, org.jblooming.waf.view.PageState, java.util.List, org.jblooming.utilities.StringUtilities" %>
<%@ page import="org.jblooming.utilities.DateUtilities" %>
<%
  PageState pageState = PageState.getCurrentPageState();
  QAOperator logged = (QAOperator) pageState.getLoggedOperator();
  QuestionDrawer drawer = (QuestionDrawer) JspIncluderSupport.getCurrentInstance(request);
  Question question = drawer.question;

/*
________________________________________________________________________________________________________________________________________________________________________


           DRAW_QUESTION

________________________________________________________________________________________________________________________________________________________________________

*/

if ("DRAW_QUESTION".equals(request.getAttribute(QuestionDrawer.ACTION))) {

%><%--

  view.mainObjectId = q.getId();
%>
<hr>
<div class="QAStats"><big class="upvotes"><%=q.getUpvotes()!=null?q.getUpvotes().size():"0"%><span>voti su</span></big>
  <big class="answers"><%=q.getAnswers()!=null ? q.getAnswers().size()+"":"0"%><span>risposte</span></big></div>
<div class="QASummary">
  <a href="<%=view.toLinkToHref()%>"><%=q.getSubject()%> [<%=JSP.limWr(q.getDescription(), 10)%>]</a><br>
  <h5>Cat: <%=q.getCategory()!=null?q.getCategory().getName():"-"%> Tags: <%=q.getTags()%></h5>
</div>

  --%>
<%

  int size = question.getUpvotes().size();
  QAOperator user = question.getOwner();

  // print all vote tools (vote up, vote down, favourite, question views)
  %>


<div class="QWrapper">
  <%--<div class="voteCell">
      &lt;%&ndash;<a class="voteUp icon tip" title="<%=I18n.g("VOTE_UP")%>">u</a><span class="voteCount">NUM</span><a class="voteDown icon tip" title="<%=I18n.g("VOTE_DOWN")%>">&ugrave;</a><span class="favoriteCount"></span>&ndash;%&gt;
      <span class="voteBtn"><a id='like_<%=question.getId()%>'
                                 class="<%=question.likedBy(logged) ? "brkUnlike" : "brkLike" %> tip"
                                 href="javascript:void(0);" title="<%=logged==null ? I18n.get("PLEASE_LOGIN_TO_VOTE") :(question.likedBy(logged)  ? I18n.get("LIKE_THIS_QUESTION_NOMORE") : I18n.get("LIKE_THIS_QUESTION"))%>"
                <%if (!question.getOwner().equals(logged)) {%><%=" onclick=\"manageLikeQ('" + question.getId() + "')\""%><%}%>
                ></a><%=("<em class='likeSize' id='likeSize" + question.getId() + "'>" + size + "</em>")%></span>
  </div>--%>

    <div class="voteCell">
      <%
      if (logged != null && !logged.equals(question.getOwner())) {

        String title = "";
        if (size == 0)
          title = I18n.g("QUESTION_LIKE");
        else {
          //someone likes it but not you
          if (size > 0 && !question.likedBy(logged))
            title =I18n.get("QA_LIKE_THIS_IN_TOTAL_%%",size+"");
          else {
            //only you like it
            if (size == 1 && question.likedBy(logged))
              title = I18n.g("QA_YOU_LIKE");
            else {
              //you and others
              title = I18n.g("QA_QUESTION_LIKE_YOU_AND") + " " + (size) + " " + I18n.g("QA_LIKE_THIS");
            }
          }
        }

    %><span class="voteBtn"><a id='like_<%=question.getId()%>' class="<%=question.likedBy(logged) ? "questionUnlike" : "questionLike" %> icon tip"
                               href="javascript:void(0);" title="<%=title%>"
            <%if (!question.getOwner().equals(logged)) {%><%=" onclick=\"manageLikeQ('" + question.getId() + "')\""%><%}%>
            >i</a><%=("<em class='likeSize' id='likeSize" + question.getId() + "'>" + size + "</em>")%></span><%

    } else if (logged == null) {

      PageSeed login = pageState.pageFromRoot("site/access/login.jsp");
      login.addClientEntry("LOGINPENDINGURL",question.getURL().href);



    %>
        <span class="voteBtn">
          <a id='like_<%=question.getId()%>' class="icon disabled tip"
             href="<%=login.toLinkToHref()%>" title="<%=I18n.g("PLEASE_LOGIN_TO_VOTE")%>">i</a><%=("<em class='likeSize' id='likeSize" + question.getId() + "'>" + size + "</em>")%>
        </span>

      <%

      } else {
      %>
        <span class="voteBtn">
          <a id='like_<%=question.getId()%>' class="icon disabled tip"
             href="javascript:void(0);" title="<%=I18n.g("OWNER_CANT_LIKE_QUESTION")%>"<%--onclick="managequestionLike('<%=question.getId()%>')"--%>>i</a><%=("<em class='likeSize' id='likeSize" + question.getId() + "'>" + size + "</em>")%>
        </span>

      <%

        }%>

    </div>
  
  
<div class="contentWrapper"><h2 class="questionTitle">
    <script>
        var converter = Markdown.getSanitizingConverter();
        document.write(converter.makeHtml("<%=JSP.htmlEncodeApexesAndTags(question.getSubject())%>"));
    </script>
    </h2>
    <%
        String desc = StringUtilities.replaceAllNoRegex(question.getDescription(),"</script>","</ script>");

    %>
    <script>document.write(converter.makeHtml("<%=JSP.javascriptEncode(desc)%>"));</script>

 <span class="meta">

<%
  if(logged!=null && question.hasPermissionFor(logged, QAPermission.QUESTION_EDIT)) {

  PageSeed edit = pageState.pageFromRoot("talk/write/askQuestion.jsp");
  edit.command= Commands.EDIT;
  edit.mainObjectId=question.getId();
  %><a class="edit" href="<%=edit.toLinkToHref()%>"><%=logged.equals(question.getOwner())?I18n.g("EDIT_QUESTION")+"</a>":I18n.g("IMPROVE_QUESTION")+"</a>"+" <span>|</span>"%><%

}%>

<%
  if(logged!=null && !logged.equals(question.getOwner())) {%>
 <a href="javascript:void(0)" class="report"  id="qReportId_<%=question.getId()%>" questionId="<%=question.getId()%>"
       onclick="$(this).confirm(<%=!logged.isModerator() ? "questionReport" : "banQuestion"%>,'<%=!logged.isModerator() ? I18n.get("QUESTION_REPORT_SURE") : I18n.get("QUESTION_BAN_SURE")%>')">
    <%--<span class="icon" style="color: #4A5053" title="<%=I18n.get("COMMENT_REPORT")%>">D</span>&nbsp;--%><%=!logged.isModerator() ? I18n.get("QUESTION_REPORT") :
    (question.isDeleted()?"un-ban":I18n.get("QUESTION_BAN"))%></a><%
  }

  %>
</span>

  <br><%
    PageSeed tf = pageState.pageFromRoot("talk/index.jsp");
    tf.addClientEntry("WHAT","TAG");
    for (Tag tag : question.getTags()) {
      tf.addClientEntry("TAG", tag.getId());
  %><span class="tags"> <a href="<%=tf.toLinkToHref()%>"><%=tag.getName()%></a> <%=question.getTags().indexOf(tag) < (question.getTags().size() - 1) ? "" : ""%></span><%
    }%>
</div>
</div>


<div class="authors"><%
  //first editors
  for (QuestionRevision qr : question.getRevisions()) {
    QAOperator rev = qr.getEditor();
      PageSeed ps = pageState.pageFromRoot("talk/questionHistory.jsp");
      ps.mainObjectId=question.getId();
      ps.addClientEntry("QR",qr.getId());
%><div class="revisedBy"><%=I18n.g("QUESTION_REVISED")%><a href="<%=ps.toLinkToHref()%>"> <%=I18n.g("QUESTION_REVISED_ON")%> <%=JSP.w(qr.getRevisionDate())%></a><br>
  <%
    UserDrawer userDrawer = new UserDrawer(rev, true, 30);
    userDrawer.toHtml(pageContext);%>




  </div>

  <%
    }
  %>

  <div class="createdBy">
    <%=I18n.get("QUESTION_CREATED_ON", DateUtilities.dateToString(question.getCreationDate(),"d MMMM yyyy"))%><br>
    <%
      UserDrawer userDrawer = new UserDrawer(user, true, 30);
      userDrawer.toHtml(pageContext);%>
  </div>
</div>
<%

  // print all comments
  List<Comment> comments = question.getComments();
  if (comments.size()>0) {
    %><div class="QAComments"><br><%
  for (Comment c : comments) {


%><hr><div id="commQId_<%=c.getId()%>" class="linkEnabled"><%=c.getText()%> <span class="commOwner"><%=I18n.get("BY")%> <%=c.getOwner().getDisplayName()%></span><%
  if (logged!=null && logged.equals(c.getOwner()) || logged!=null && logged.isModerator()) {
%> <a href="javascript:void(0);" onclick="$(this).confirm(removeQuestionComment,'<%=I18n.get("QUESTION_COMMENT_REMOVE_SURE")%>');">(<%=I18n.get("QUESTION_COMMENT_REMOVE")%>)</a><%
} else if (logged!=null) {
  //report
%> &nbsp;&nbsp;<a id="commQIdR_<%=c.getId()%>" href="javascript:void(0);" onclick="$(this).confirm(commentQuestionReport,'<%=I18n.get("COMMENT_REPORT_SURE")%>');"><span class="icon" style="color: #888" title="<%=I18n.get("COMMENT_REPORT")%>">D</span></a>
  <%
  }
  %></div><%
  }
%></div><%
  }
  //do comment
  if (logged!=null){
%><a  title="<%=I18n.g("ADD_COMMENT")%>" class="noteBtn"
                           href="javascript:void(0);" onclick="
        $('#commQ_<%=question.getId()%>').slideDown('fast');"><span class="icon">&#164;</span><%=I18n.g("ADD_COMMENT")%></a><%}

%><div class="commentArea" id="commQ_<%=question.getId()%>" style="display:none;"><%
  TextArea ta = new TextArea("COMMENT_"+question.getId(),"",70,2,null);
  ta.label="";
  ta.script="style='width:100%'";
  ta.toHtml(pageContext);

%> <div class="btnBar"><button class="buttonSmall black" onclick="saveQuestionComment('<%=question.getId()%>')"><%=I18n.g("COMMENT_SAVE")%></button></div>
</div>



<%

/*
________________________________________________________________________________________________________________________________________________________________________


           DRAW_QUESTION_COMPACT

________________________________________________________________________________________________________________________________________________________________________

*/


} else if ("DRAW_QUESTION_COMPACT".equals(request.getAttribute(QuestionDrawer.ACTION))) {

  PageSeed view = pageState.pageFromRoot("talk/question.jsp");
  view.mainObjectId = question.getId();
  if(JSP.ex(question.getSubject())){
%>
<div class="question box">
  <div class="stats" style="float: left;">
    <span class="answers"><a href="<%=question.getURL()%>"><%=question.getAnswersNotDeleted().size()%><label><%=I18n.g("QA_ANSWERS")%></label></a></span>
    <span class="votes"><a href="<%=question.getURL()%>"><%=question.getUpvotes().size()%><label><%=I18n.g("QA_VOTES")%></label></a></span>
  </div>
  <div class="content"><h3 class="questionTitle">
    <a href="<%=question.getURL()%>" title="<%=JSP.htmlEncodeApexesAndTags(question.getSubject())%>" >
        <script>
            var converter = Markdown.getSanitizingConverter();
            document.write(converter.makeHtml("<%=JSP.htmlEncodeApexesAndTags(question.getSubject())%>"));
        </script>
    </a></h3>
    <%
        String abstractS = JSP.javascriptEncode((JSP.limWr(question.getDescription(),100)));

        abstractS = StringUtilities.replaceAllNoRegex(abstractS,"</script>","</ script>");

    %>
    <script>
      var converter = Markdown.getSanitizingConverter();
      var text = converter.makeHtml("<%=abstractS%>");
      text = text.replace(/<a\b[^>]*>(.*?)<\/a>/i,"");
      document.write(text);
    </script>
  <div class="tagInlineGroup"><%
    PageSeed tf = pageState.pageFromRoot("talk/index.jsp");
    tf.addClientEntry("WHAT","TAG");
    for(Tag tag : question.getTags()){
      tf.addClientEntry("TAG",tag.getId());
      %><span class="tags"><a href="<%=tf.toLinkToHref()%>"><%=tag.getName()%></a></span>



      <%
    }

  %></div><div class="createdBy">
          <span class="creationDate"><%=DateUtilities.dateToRelative(question.getCreationDate())%></span>&nbsp;&nbsp;  <%  QAOperator user = question.getOwner();  %>

          <a class="userGravatar" title="<%=user.getDisplayName()%>" href="<%=user.getPublicProfileURL()%>">
              <img src="<%=user.getGravatarUrl(20)%>" align="top" alt="<%=user.getDisplayName()%>">&nbsp;<%=user.getDisplayName()%></a> <span><span class="icon" style="font-size: 12px">*</span><%=(int) user.getKarma()%>
      </div>
  </div>

</div><hr class="break">

<%
    }

} else if ("DRAW_QUESTION_SIMPLE".equals(request.getAttribute(QuestionDrawer.ACTION))) {


  if(JSP.ex(question.getSubject())){



//  int size = question.getUpvotes().size();
  QAOperator user = question.getOwner();


%>
<div>
  <div style="float: left; margin-right: 8px; margin-top: 6px; ">
    <%--<span><a href="<%=question.getURL()%>"><%=question.getUpvotes().size()%></a></span>--%>
      <%
        UserDrawer userDrawer = new UserDrawer(user, false, 20);
        userDrawer.toHtml(pageContext);%>
  </div>
  <h2 style="margin-left: 33px; min-height: 30px">
    <a href="<%=question.getURL().toLinkToHref()%>">
        <script>
            var converter = Markdown.getSanitizingConverter();
            document.write(converter.makeHtml("<%=JSP.javascriptEncode(question.getSubject())%>"));
        </script>
    </a><br>
 </h2></div>
<%
    }

}
%>
