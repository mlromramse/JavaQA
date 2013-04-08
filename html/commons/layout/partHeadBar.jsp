<%@ page import="org.jblooming.waf.html.button.ButtonSupport,
                 org.jblooming.waf.html.container.ButtonBar,
                 org.jblooming.waf.html.container.HeadBar,
                 org.jblooming.waf.html.core.JspIncluder,
                 org.jblooming.waf.html.core.JspIncluderSupport,
                 org.jblooming.waf.html.layout.Skin,
                 org.jblooming.waf.view.PageState"%><%

  HeadBar headBar = (HeadBar) JspIncluderSupport.getCurrentInstance(request);
  PageState pageState = PageState.getCurrentPageState();
  Skin skin = pageState.sessionState.getSkin();

%>
<div class="headBar noprint">
     <%
    if (headBar.buttonList.size() > 0) {
      for (JspIncluder button : headBar.buttonList) {

        if (button instanceof ButtonSupport) {
          ((ButtonSupport)button).toHtmlInTextOnlyModality(pageContext);
        } else if (button instanceof ButtonBar.Separator) {
          %><span class="separator" style="width:<%=((ButtonBar.Separator)button).width%>px">&nbsp;</span><%
        } else if (button instanceof ButtonBar.Label) {
          %><span style="color:<%=skin.COLOR_TEXT_MENUBAR%>"><%=((ButtonBar.Label)button).label%>&nbsp;</span><%
        } else
          button.toHtml(pageContext);
      }
    }
    
%> </div>
