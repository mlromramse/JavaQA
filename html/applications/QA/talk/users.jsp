<%@ page
        import="com.QA.QAOperator, com.QA.waf.QAScreenApp, com.QA.waf.UserDrawer, org.jblooming.oql.OqlQuery, org.jblooming.waf.ScreenArea, org.jblooming.waf.constants.Commands, org.jblooming.waf.settings.I18n, org.jblooming.waf.view.PageSeed, org.jblooming.waf.view.PageState, java.util.List" %>
<%
  PageState pageState = PageState.getCurrentPageState();

  if (!pageState.screenRunning) {

    pageState.screenRunning = true;
    final ScreenArea body = new ScreenArea(request);
    QAScreenApp mpScreenApp = new QAScreenApp(body);
    //mpScreenApp.hasRightColumn=false;
    mpScreenApp.register(pageState);
    pageState.getHeaderFooter().toolTip = I18n.g("QA_APP_NAME")+" - "+ I18n.g("QA_COMMUNITY_USERS");
    pageState.perform(request, response);
    pageState.toHtml(pageContext);

  } else {

    int pageSize = pageState.getEntry("PAGESIZE").intValueNoErrorCodeNoExc();
    if (pageSize==0)
      pageSize = 50;
    else if (pageSize>200)
      pageSize = 200;

    List<QAOperator> tops = QAOperator.getTopOperators(pageSize);

    String hql = "select count(mop) from " + QAOperator.class.getName() + " as mop where mop.enabled=true";
    int tot = ((Long)new OqlQuery(hql).uniqueResult()).intValue();

%>
<jsp:include page="../parts/partCommunityMenu.jsp">
    <jsp:param name="SHOW_ADD" value="no"></jsp:param>
    <jsp:param name="SHOW_BACK" value="yes"></jsp:param>
</jsp:include>

<div id="content">

  <h2><span><%=I18n.g("QA_COMMUNITY_USERS")%> (<%=pageSize%>/<%=tot%> total)</span></h2>

  <div class="contentBox" style="padding-right: 20px"> <%
     for (QAOperator mop : tops) {
       %><div class="userGrid"><% new UserDrawer(mop, true, 40).toHtml(pageContext); %></div><%
     }
     %><br style="clear: both"></div><%



  if (tot>tops.size()) {
    PageSeed search = pageState.thisPage(request);
    search.addClientEntry(pageState.getEntry("FILTER"));
    search.command = Commands.FIND;
    search.addClientEntry("PAGESIZE",pageSize+10);
    %><div class="moreEntry"><a class="button" href="<%=search.toLinkToHref()%>#MORE_RESULTS" name="MORE_RESULTS" title="<%=I18n.g("QA_MORE_RESULTS")%>"><%=I18n.g("QA_MORE_RESULTS")%></a></div> <%
  }
%>
</div><%

  }
%>