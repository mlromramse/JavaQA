<%@ page import="com.opnlb.website.identifier.SiteIdentifier,
                com.opnlb.webwork.messaging.UsableTag,
                org.jblooming.utilities.JSP,
                org.jblooming.waf.SessionState,
                org.jblooming.waf.constants.OperatorConstants,
                org.jblooming.waf.html.layout.Css,
                org.jblooming.waf.html.layout.Skin,
                org.jblooming.waf.settings.ApplicationState,
                org.jblooming.waf.view.PageState,
                java.util.List"%><%@ page pageEncoding="UTF-8" %><html><head><%

  PageState pageState = PageState.getCurrentPageState();
  SessionState sessionState = pageState.sessionState;
  SiteIdentifier site = (SiteIdentifier) sessionState.getAttribute("WW4_SITE");
  Skin skin = sessionState.getSkin();
  if (skin == null) {
    sessionState.setSkinForApplicationIfNull(request.getContextPath(), ApplicationState.getApplicationSetting(OperatorConstants.FLD_CURRENT_SKIN), "applications/blooming");
    skin = sessionState.getSkin();
  }

%><script type="text/javascript" src="../../tiny_mce_popup.js"></script>
  <script type="javascript" src="<%=request.getContextPath()+"/applications/webwork/js/webwork.js"%>"></script>
  <script type="javascript" src="<%=request.getContextPath()+"/applications/webwork/js/showThumb.js"%>"></script>
  <script type="text/javascript" src="<%=request.getContextPath()%>/commons/layout/tinymce/plugins/usableTags/js/usableTags.js"></script>
  <link rel=stylesheet href="<%=skin.css%>platformCss.jsp" type="text/css" media="screen">
	<base target="_self"/>
	</head>
  <body>
  <table width="100%" border="0">
    <tr><td colspan="2" class="<%=Css.containerTitle%>"><%=I18n.get("USABLE_TAG")%></td></tr>
    <tr><td><%

      List<UsableTag> tags = UsableTag.usableTagsList(site);
      if (!JSP.ex(tags)) {
      %><tr><td><%=I18n.get("NO_TAGS")%></td></tr><%

      } else {
      %><tr class="<%=Css.tableHeader%>">
          <td><%=I18n.get("NAME")%></td>
          <td><%=I18n.get("DESCRIPTION")%></td>
        </tr><%
      for (UsableTag tag : tags) {
      %><tr class="<%=Css.alternate()%>" height="30">
          <td onclick="UsableTagsDialog.insert('<%=tag.getName()%>', '<%=tag.getName()%>');"
              style="cursor:pointer;"
              title="<%=I18n.get("CLICK_TO_INSERT")%>" ><b><%=tag.getName()%></b></td>
          <td><%=JSP.cellContent(tag.getDescription())%></td>
        </tr><%
      }
    }

%></td></tr></table></body></html>