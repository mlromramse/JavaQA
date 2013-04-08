<%@ page import="org.jblooming.waf.SessionState,
                 org.jblooming.waf.html.core.JspHelper,
                 org.jblooming.waf.html.layout.Skin"%>
<%

  SessionState sessionState = SessionState.getSessionState(request);
  Skin skin = sessionState.getSkin();

  if (skin!=null && Skin.LOAD_COLORS.equals(request.getAttribute(JspHelper.ACTION))) {

  skin.COLOR_TEXT_MAIN="#000000";
  skin.COLOR_TEXT_LINK="#992200";
  skin.COLOR_TEXT_TOOLBAR = "#FFFFFF";
  skin.COLOR_TEXT_TITLE="#FFFFFF";
  skin.COLOR_TEXT_TITLE01="#FFFFFF";
  skin.COLOR_TEXT_TITLE02="#006699";

  skin.COLOR_BACKGROUND_MAIN_CONTAINER="#ffffff";
  skin.COLOR_BACKGROUND_MAIN="#FFFFFF";
  skin.COLOR_BACKGROUND_TITLE="#617777";
  skin.COLOR_BACKGROUND_TOOLBAR="#3399CC";
  skin.COLOR_BACKGROUND_TITLE01="#86bbd0";
  skin.COLOR_BACKGROUND_TITLE02="#E3EDED";
  skin.COLOR_BACKGROUND_CONTENT = "#FFFFFF";
  skin.COLOR_WARNING = "#D30202";
  skin.COLOR_FEEDBACK = "#996600";

  skin.COLOR_BACKGROUND_MENUBAR="#69BFE0";
  skin.COLOR_TEXT_MENUBAR="#FFFFFF";
  skin.COLOR_BACKGROUND_MENU="#f3f3f3";
  skin.COLOR_TEXT_MENU="#666666";
  skin.COLOR_DEPTH="#e3eded";

  skin.colorsLoaded=true;

} else if (skin!=null) {
  response.setContentType("text/css");
  %>
<jsp:include page="../default/partPlatformCss.jsp" />

<%}%>