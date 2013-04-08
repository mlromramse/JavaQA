<%@ page import="org.jblooming.waf.SessionState,
                 org.jblooming.waf.html.container.DivOnMouseover,
                 org.jblooming.waf.html.core.JspHelper,
                 org.jblooming.waf.html.core.JspIncluderSupport,
                 org.jblooming.waf.html.input.HtmlElement,
                 org.jblooming.waf.html.layout.Skin, org.jblooming.utilities.JSP, org.jblooming.waf.html.core.JspIncluder, org.jblooming.waf.html.button.ButtonSupport" %><%


  DivOnMouseover divOnMouseover = (DivOnMouseover) JspIncluderSupport.getCurrentInstance(request);
  SessionState sessionState = SessionState.getSessionState(request);

  if (DivOnMouseover.INITIALIZE.equals(request.getAttribute(DivOnMouseover.ACTION))) {
    %>
<script type="text/javascript">

  var _timerIdForDivOnMouseover = new Object();


  function bjs_showDivOnMouseover(divId, bjsId) {
    $("#"+divId).show();
    nearBestPosition(bjsId, divId);
    bringToFront(divId);
    if (_timerIdForDivOnMouseover[divId] !== null)
      clearTimeout(_timerIdForDivOnMouseover[divId]);
  }

  function bjs_hideDivOnMouseover(divid) {
    $("#"+divid).hide();
  }

  function div_hideDivOnMouseoverWithTimeout(divId) {
    _timerIdForDivOnMouseover[divId] = setTimeout("bjs_hideDivOnMouseover('"+divId+"')", 500);
  }

  function div_showDivOnMouseover(divId) {
    if (_timerIdForDivOnMouseover !== null)
      clearTimeout(_timerIdForDivOnMouseover[divId]);
    $("#"+divId).show();
  }
</script>
<% } else {
  Skin skin = sessionState.getSkin();

  //get the launcher id
  String launcherId = "";
  JspIncluder opener = divOnMouseover.opener;
  if (opener instanceof HtmlElement)
     launcherId = ((HtmlElement) opener).id;
  else
    launcherId = ((JspHelper) opener).id;

  String divId = "divdomo_"+divOnMouseover.id;
  String additionalScript = "onmouseover=\"bjs_showDivOnMouseover('" + divId + "', '" + launcherId + "');  \" " +
          " onmouseout=\"bjs_hideDivOnMouseover('"+divId+"'); \"";


%><span <%=additionalScript%>><%
  if (opener instanceof ButtonSupport)
    ((ButtonSupport)opener).toHtmlInTextOnlyModality(pageContext);
  else 
    opener.toHtml(pageContext);
%></span>
<div id="<%=divId%>" onmouse<%=sessionState.isExplorer()?"leave":"out"%>="div_hideDivOnMouseoverWithTimeout('<%=divId%>');"
     onmouseover="div_showDivOnMouseover('<%=divId%>');" class="divomo"
     style="display:none; position: absolute;">
  
  <%
    if (divOnMouseover.content!=null)
      divOnMouseover.content.toHtml(pageContext);
    if (JSP.ex(divOnMouseover.buttonList)){
      for (JspIncluder jh:divOnMouseover.buttonList)  {

      if (jh instanceof ButtonSupport) {
          ((ButtonSupport)jh).toHtmlInTextOnlyModality(pageContext);
      } else {
        jh.toHtml(pageContext);
      }
    }
   }
  %>
</div><%
  }
%>