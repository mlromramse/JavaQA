<%@ page import ="org.jblooming.utilities.JSP,
                   org.jblooming.waf.html.button.ButtonSupport,
                   org.jblooming.waf.html.core.JspIncluderSupport"%><%
  ButtonSupport button = (ButtonSupport) JspIncluderSupport.getCurrentInstance(request);
  String textual=ButtonSupport.GRAPHICAL.equals(button.outputModality)?"":"textual";
  String focus=button.hasFocus?"focused":"";
  String disabled = !button.enabled ? "disabled" : "";
  String style = JSP.w(button.style) + (JSP.ex(button.width) ? "width:" + JSP.w(button.width) + ";" : "");


%><button class="noprint <%=textual%> <%=focus%> <%=JSP.w(button.additionalCssClass)%>" <%=disabled%> id="<%=button.getId()%>" <%=JSP.ex(style)?"style=\""+style+"\"":""%> <%=button.generateToolTip()%> <%=button.generateLaunchJs()%>><%
  if (JSP.ex(button.iconChar)){
    %><span class='teamworkIcon'><%=button.iconChar%></span><%
  }
%><%=button.label%></button>