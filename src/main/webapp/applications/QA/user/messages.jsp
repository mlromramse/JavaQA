<%@ page contentType="text/html; charset=utf-8" pageEncoding="UTF-8"%><%@ page
        import="com.QA.QAOperator, com.QA.messages.StickyNote,
        org.jblooming.oql.OqlQuery, org.jblooming.utilities.DateUtilities, org.jblooming.waf.ScreenArea, org.jblooming.waf.constants.Commands,
        org.jblooming.waf.settings.I18n, org.jblooming.waf.view.PageSeed, org.jblooming.waf.view.PageState, java.util.Date, java.util.List, com.QA.waf.QAScreenApp" %>
<%
  PageState pageState = PageState.getCurrentPageState();

  if (!pageState.screenRunning) {

    pageState.screenRunning = true;
    final ScreenArea body = new ScreenArea(request);
    QAScreenApp lw = new QAScreenApp(body);
    lw.register(pageState);
    pageState.getHeaderFooter().toolTip = I18n.g("QA_APP_NAME")+" - "+I18n.g("QA_MESSAGES");
    pageState.perform(request, response);
    pageState.toHtml(pageContext);
  } else {

    int pageSize = pageState.getEntry("PAGESIZE").intValueNoErrorCodeNoExc();
    if (pageSize==0)
      pageSize = 10;
    else if (pageSize>100)
      pageSize = 100;


    QAOperator logged = (QAOperator) pageState.getLoggedOperator();

    String hql = "select sn from " + StickyNote.class.getName() + " as sn where sn.read is not null and sn.receiver=:op order by sn.creationDate desc ";
    OqlQuery oql = new OqlQuery(hql);
    oql.getQuery().setEntity("op", logged);
    oql.getQuery().setMaxResults(50);
    List<StickyNote> snsun = oql.list();

    hql = "select sn from " + StickyNote.class.getName() + " as sn where sn.read is null and sn.receiver=:op order by sn.creationDate desc ";
    oql = new OqlQuery(hql);
    oql.getQuery().setEntity("op", logged);
    List<StickyNote> sns = oql.list();

    boolean shownM= false;

%> <div id="content">
<h2><span><%=I18n.get("QA_MESSAGES")%></span></h2>

  <jsp:include page="partUserMenu.jsp"/><br><br>
  <div>
<%

  if (sns.size() > 0) {

    for (StickyNote sn : sns) {
      %><p class="messages unread <%=sn.getType()%>"><%=sn.getMessage()%> <span class="meta"><%=DateUtilities.dateToRelative(sn.getCreationDate())%></span></p><%
      sn.setRead(new Date());
      sn.store();
      shownM = true;
    }
  }

  if (snsun.size() > 0) {
    int shown = 0;
    for (StickyNote sn : snsun) {
        %><p class="messages <%=sn.getType()%>"><%=sn.getMessage()%> <span class="meta"><%=DateUtilities.dateToRelative(sn.getCreationDate())%></span></p><%
      shownM = true;
      shown++;
      if (shown>=pageSize)
        break;

      }
    }%></div><%

  if (snsun.size()>pageSize) {
    PageSeed search = pageState.thisPage(request);
    search.addClientEntry(pageState.getEntry("FILTER"));
    search.command = Commands.FIND;
    search.addClientEntry("PAGESIZE",pageSize+10);

%><div class="moreEntry"><a class="button" href="<%=search.toLinkToHref()%>#MORE_RESULTS" name="MORE_RESULTS" title="<%=I18n.g("QA_MORE_RESULTS")%>"><%=I18n.g("QA_MORE_RESULTS")%></a></div> <%
  }

  if (!shownM) {
    %><%=I18n.g("NO_MESSAGES_YET")%><%
  }


%></div><%
  }
%>