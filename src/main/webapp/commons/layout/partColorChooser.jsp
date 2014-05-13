<%@ page import="org.jblooming.utilities.JSP,
                 org.jblooming.waf.html.core.JspIncluderSupport,
                 org.jblooming.waf.html.input.ColorChooser,
                 org.jblooming.waf.html.input.TextField,
                 org.jblooming.waf.view.PageState"%><%

  ColorChooser cc = (ColorChooser)JspIncluderSupport.getCurrentInstance(request);
  PageState pageState= PageState.getCurrentPageState();
  String choosenColor = pageState.getEntry(cc.fieldName).stringValueNullIfEmpty();

  TextField hidden=new TextField("hidden",cc.fieldName,"",2);
  hidden.label="";

  String folioId = ColorChooser.COLOR_POPUP_PREFIX + cc.fieldName;
  String visibleId = ColorChooser.COLOR_PREFIX + cc.fieldName;

  int popW = cc.howManyColumnsInPopup * (cc.colorSquareSize+(JSP.ex(cc.colorSquareBorderColor)?2:0));
  int popH = (cc.colors.size() / cc.howManyColumnsInPopup )*cc.colorSquareSize;




  %>
<style type="text/css">
  .colorRect{
    width:<%=cc.colorSquareSize%>px;
    height:<%=cc.colorSquareSize%>px;
    float:left;
    <%=JSP.ex(cc.colorSquareBorderColor)?"border:1px solid "+cc.colorSquareBorderColor:""%>
  }
</style>

<div id="<%=folioId%>" style="display:none; position:absolute; cursor:pointer; border:4px solid gray; width:<%=popW%>px;background-color:white;"><%
  for (String color:cc.colors){
    %><div style="background-color:<%=color%>;" class="colorRect"></div><%
  }
  %>
  <div style="clear:both;"></div>
<div id="colEx" style="background-color:<%=choosenColor%>; border:2px solid white; height:<%=cc.colorSquareSize%>px;"></div>


</div>
<script type="text/javascript">
  $(document).ready(function () {
  $("#<%=folioId%> div").bind("click",function (e){
    var color=$(this).css("backgroundColor");
    $("#<%=cc.fieldName%>").val(color);
    $("#<%=visibleId%>").css("backgroundColor",color);
    $("#<%=folioId%>").fadeOut();
    <%=JSP.w(cc.onSelectScript)%>
  }).bind("mouseover",function(e){
    $("#colEx").css('backgroundColor',$(this).css("backgroundColor"));
  });
  })
</script>


<%

  hidden.toHtml(pageContext);

  %><%=JSP.w(cc.label)%><%=JSP.w(cc.separator)%><input type="text" id="<%=ColorChooser.COLOR_PREFIX+cc.fieldName%>"
           style="background-color:<%=JSP.w(choosenColor)%>; cursor:pointer;"
           onClick="nearBestPosition(this.id, '<%=folioId%>'); $('#<%=folioId%>').toggle();$('#colEx').css('backgroundColor',$('#<%=cc.fieldName%>'))"
           value="" readonly size="<%=cc.fieldSize%>" class="<%=hidden.fieldClass%>"><% 
%>