<%@ page
        import="com.QA.QAOperator, com.QA.waf.QuestionDrawer, com.QA.waf.UserDrawer, org.jblooming.waf.html.core.JspIncluderSupport, org.jblooming.waf.view.PageState" %>
<%
  PageState pageState = PageState.getCurrentPageState();
  UserDrawer drawer = (UserDrawer) JspIncluderSupport.getCurrentInstance(request);
  QAOperator user = drawer.QAOperator;

  if ("DRAW_USER".equals(request.getAttribute(QuestionDrawer.ACTION))) {

%>
<%
  if (drawer.printFull) {


%>
<div style="font-size: 12px; min-width: 100px"><a class="userGravatar" title="<%=user.getDisplayName()%>" href="<%=user.getPublicProfileURL()%>" style="float: left;">
  <img src="<%=user.getGravatarUrl(drawer.gravatarSize)%>" align="top" alt="<%=user.getDisplayName()%>" style="width:<%=drawer.gravatarSize%>px">
</a>

<div style="margin-left:<%=drawer.gravatarSize+15%>px; line-height: 15px">
  <a class="userGravatar" title="<%=user.getDisplayName()%>"
     href="<%=user.getPublicProfileURL()%>"><%=user.getDisplayName()%>
  </a><br>
  <span style="color: #888"><span class="icon" style="font-size: 12px">*</span><%=(int) user.getKarma()%>
    <%
      if (user.getBadges().size()>0) {
        String badge = user.getBadges().get(user.getBadges().size()-1);
        %><span class="icon" style="font-size: 12px">s</span><%=badge%><%
      }
    %>
    </span>
</div><hr class="clearBoth"></div>

<%
} else {
%><a class="userGravatar" title="<%=user.getDisplayName()%>" href="<%=user.getPublicProfileURL()%>"><img
        src="<%=user.getGravatarUrl(drawer.gravatarSize)%>" align="top" alt="<%=user.getDisplayName()%>" style="width:<%=drawer.gravatarSize%>px"></a><%
  }
%>

<%
  }
%>