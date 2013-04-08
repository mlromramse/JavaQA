<%@ page import="org.jblooming.waf.SessionState"%><%
  SessionState sessionState = SessionState.getSessionState(request);
   final String imgPath = sessionState.getSkin().imgPath;
%>
<html>
<body style="border:0;margin:0">
<table style="border-top:1px #000000 solid" width="100%" border="0" ><tr><td align="right">
<a href="http://www.open-lab.com" target="_blank" title="open lab - software engineering">
 <img src="openlab.gif" border="0"></a>
</td></tr></table>
</body></html>