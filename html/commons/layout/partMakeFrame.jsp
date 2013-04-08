<%@ page import="org.jblooming.waf.ScreenBasic,
org.jblooming.waf.view.PageState"%><%

 PageState pageState = PageState.getCurrentPageState();

if (!pageState.screenRunning) {
  pageState.screenRunning = true;
  ScreenBasic.preparePage(pageContext);
  pageState.perform(request, response).toHtml(pageContext);

  } else {

   String link=pageState.getEntry("LINK").stringValueNullIfEmpty();
  if((link!=null)) {
  %> </td></tr></table>
    <iframe src="<%=link%>" id="contentFrame" style="height:<%=pageState.sessionState.getPageHeight()-100%>px; width:100%"
        frameborder="0" framespacing="0" border="0" ></iframe>
    <table width="100%"><tr><td align="center" >
<%
  }
}
%>