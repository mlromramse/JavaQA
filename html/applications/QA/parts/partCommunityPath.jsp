<%@ page import="com.QA.QAOperator, com.QA.QAPermission, com.QA.Question, com.QA.Tag, org.jblooming.waf.settings.I18n, org.jblooming.waf.view.PageSeed, org.jblooming.waf.view.PageState" %> <%
  PageState pageState = PageState.getCurrentPageState();
QAOperator logged = (QAOperator) pageState.getLoggedOperator();

String what = pageState.getEntry("WHAT").stringValueNullIfEmpty();

PageSeed here = pageState.pageFromRoot("talk/index.jsp");
%>


<%--<h1 style="margin-bottom: 0"><span><%=I18n.g("DISCUSS_FORUM")%></span></h1>--%>


    <div class="QAMenu actions">

      <ul><li><a href="/applications/QA/talk/index.jsp" class="backBtn"><%=I18n.g("COMMUNITY_BACK")%></a></li></ul>
      <%--<ul>
        <% Tag t = null;
          if ("TAG".equals(what)) {
            t = Tag.load(pageState.getEntry("TAG").stringValue());
        %><li class="currentVoiceMenu"><a><%=t.getName()%></a></li><%
        }

        here.addClientEntry("WHAT","TOP"); %>
        <li <%="TOP".equals(what)?" class='currentVoiceMenu'":""%>><a class="tip" title="top questions" href="<%=here.toLinkToHref()%>">top</a></li>

        <% here.addClientEntry("WHAT","HOT"); %>
        <li<%="HOT".equals(what)?" class='currentVoiceMenu'":""%>><a href="<%=here.toLinkToHref()%>">hot</a></li>

        <% here.addClientEntry("WHAT","UNANSWERED"); %>
        <li <%="UNANSWERED".equals(what)?" class='currentVoiceMenu'":""%>><a href="<%=here.toLinkToHref()%>"><%=I18n.g("QUESTION_UNANSWERED")%></a></li>

        <% here.addClientEntry("WHAT","TAGS"); %>
        <li <%="TAGS".equals(what)?" class='currentVoiceMenu'":""%>><a href="<%=here.toLinkToHref()%>">tags</a></li>

        <%
          boolean showAdd = "yes".equals(request.getParameter("SHOW_ADD"));
          if (!"TAG".equals(what) && showAdd && logged!=null) {
            if (new Question().hasPermissionFor(logged, MpPermission.QUESTION_CREATE)) {
        %> <li><a class="newQ" href="/applications/QA/talk/write/askQuestion.jsp" ><%=I18n.g("ASK_NEW_QUESTION")%></a></li><%
      } else {
      %> <li><a class="askForQ" href="alert('todo)"><%=I18n.g("NEW_REPUTATION_FOR_ASK_NEW_QUESTION")%></a></li><%
          }
        }
      %>
      </ul>--%>
    </div>
<br>

