<%@ page import="com.QA.QAOperator, com.QA.QAPermission, com.QA.Question, com.QA.Tag, org.jblooming.waf.settings.I18n, org.jblooming.waf.view.PageSeed, org.jblooming.waf.view.PageState" %> <%
  PageState pageState = PageState.getCurrentPageState();
QAOperator logged = (QAOperator) pageState.getLoggedOperator();

String what = pageState.getEntry("WHAT").stringValueNullIfEmpty();

PageSeed here = pageState.pageFromRoot("talk/index.jsp");
%>
<%--<h1 style="margin-bottom: 0"><span><%=I18n.g("DISCUSS_FORUM")%></span></h1>--%>
    <div class="QAMenu actions">
      <ul>
          <%
              if ("yes".equals(request.getParameter("SHOW_BACK"))) {
          %>
          <li><a href="/applications/QA/talk/index.jsp" class="backBtn"><%=I18n.g("COMMUNITY_BACK")%></a></li>
        <%
            }

        here.addClientEntry("WHAT","TOP"); %>
        <li <%="TOP".equals(what)?" class='currentVoiceMenu'":""%>><a href="<%=here.toLinkToHref()%>">top</a></li>

        <%-- here.addClientEntry("WHAT","HOT"); %>
        <li<%="HOT".equals(what)?" class='currentVoiceMenu'":""%>><a class="hotQ" href="<%=here.toLinkToHref()%>">hot</a></li>  --%>

        <% here.addClientEntry("WHAT","UNANSWERED"); %>
        <li <%="UNANSWERED".equals(what)?" class='currentVoiceMenu'":""%>><a href="<%=here.toLinkToHref()%>"><%=I18n.g("QUESTION_UNANSWERED")%></a></li>

        <% here.addClientEntry("WHAT","TAGS"); %>
        <li <%="TAGS".equals(what)?" class='currentVoiceMenu'":""%>><a href="<%=here.toLinkToHref()%>">tags</a></li>

          <%
              if ("yes".equals(request.getParameter("SHOW_ADD"))) {
          %>
            <li><a class="newQ" href="/applications/QA/talk/write/askQuestion.jsp" ><%=I18n.g("ASK_NEW_QUESTION")%></a></li>
          <%}%>

      </ul>
    </div>

