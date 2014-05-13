<%@ page import="org.jblooming.utilities.JSP,
                 org.jblooming.waf.html.button.ButtonSupport,
                 org.jblooming.waf.html.container.ButtonBar,
                 org.jblooming.waf.html.container.RibbonBar,
                 org.jblooming.waf.html.core.JspIncluder,
                 org.jblooming.waf.html.core.JspIncluderSupport,
                 org.jblooming.waf.html.layout.Skin,
                 org.jblooming.waf.view.PageState"%><%

  RibbonBar ribbonBar = (RibbonBar) JspIncluderSupport.getCurrentInstance(request);
  PageState pageState = PageState.getCurrentPageState();
  Skin skin = pageState.sessionState.getSkin();

%>
<table width="100%" cellpadding="0" cellspacing="0" border="0" id="<%=ribbonBar.id%>"class="noprint">
  <tr ><td align="<%=ribbonBar.align%>" valign="middle" class="ribbonbar">
     <%
    if (ribbonBar.buttonList.size() > 0) {
      for (JspIncluder button : ribbonBar.buttonList) {

        if (button instanceof ButtonSupport) {
          ((ButtonSupport)button).toHtml(pageContext);
        } else if (button instanceof ButtonBar.Separator) {

          %><span class="separator" style="width:<%=((ButtonBar.Separator)button).width%>px">&nbsp;</span><%
        } else if (button instanceof ButtonBar.Label) {
          %><span style="color:<%=skin.COLOR_TEXT_MENUBAR%>"><%=((ButtonBar.Label)button).label%>&nbsp;</span><%
        } else
          button.toHtml(pageContext);
      }
    }
      %></td><%
    
%> </tr></table>
