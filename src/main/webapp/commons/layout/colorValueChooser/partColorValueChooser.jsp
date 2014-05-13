<%@ page import="org.jblooming.utilities.JSP,
                 org.jblooming.waf.html.core.JspIncluderSupport,
                 org.jblooming.waf.html.display.Img,
                 org.jblooming.waf.html.input.ColorChooser,
                 org.jblooming.waf.html.input.ColorValueChooser,
                 org.jblooming.waf.html.input.TextField,org.jblooming.waf.view.PageState, org.jblooming.waf.html.layout.HtmlColors, org.jblooming.waf.html.button.ButtonJS"%><%

  ColorValueChooser cc = (ColorValueChooser) JspIncluderSupport.getCurrentInstance(request);
  PageState pageState = PageState.getCurrentPageState();
  String choosenCode = pageState.getEntry(cc.fieldName).stringValueNullIfEmpty();

  if (ColorValueChooser.INITIALIZE.equals(request.getAttribute(ColorValueChooser.ACTION))) {
  %><script type="text/javascript">
   function cvc_clickColValCh(type,fieldId,tableId,event) {

     var divList = $("#"+type+"div_tpl");
     if (divList.size()>0){
       var targetObj = $("#"+tableId);
       var clone=divList.clone(true);
       targetObj.after(clone);

       clone.css("z-index",100000).attr("id",type+"div").attr('openerId', fieldId);
       clone.bind("mouseover",function(){clone.stopTime("hide");clone.show();});
       clone.bind("mouseout",function(){$(this).oneTime(200,"hide",function(){$(this).remove();})});
       $(event.target).bind("mouseout",function(){clone.trigger("mouseout")});
       clone.show();
       $('#'+type+'div').keepItVisible();
     }
     stopBubble(event);
     return false;
   }
   </script><%



  } else   if (ColorValueChooser.FINALIZE.equals(request.getAttribute(ColorValueChooser.ACTION))) {
 
    boolean swap = false;


  //----------------------------------------------------------------- START DROPDOWN TABLE -----------------------------------------------------------------
%>
<div id='<%=cc.getType()%>div_tpl' style="display:none;position:absolute;background-color:#ffffff;border:#a0a0a0 1px solid;cursor:pointer;" openerId="" ><%

        int i=1;
        for (ColorValueChooser.CodeColorValue codeColorValue : cc.codeColorValues) {
          String textColor = HtmlColors.contrastColor(codeColorValue.color);
          %><div class="codeValueChooserLine" id="tr<%=cc.getType()%>_<%=codeColorValue.code%>" style="background-color:<%=JSP.w(codeColorValue.color)%>; color:<%=textColor%>;height:<%=cc.height%>px;width:100%;line-height:<%=cc.height%>px;"
            cCode="<%=codeColorValue.code%>" cColor="<%=JSP.w(codeColorValue.color)%>" cTcolor="<%=textColor%>" cValue="<%=codeColorValue.value%>" pos="<%=i%>"><%=codeColorValue.value%></div><%
      i++;
    }
   %></div>

<script type="text/javascript">

  $(document).ready(function () {
    $("#<%=cc.getType()%>div [cValue]").live("click", function() {
      var tr = $(this);
      var colorDiv = $("#<%=cc.getType()%>div");
      var hidden = $("#" + colorDiv.attr("openerId"));
      var idStart = '<%=ColorChooser.COLOR_PREFIX%>' + hidden.attr("id");
      $("#" + idStart).css("background-color", tr.attr("cColor"));
      $("#" + idStart).css("color", tr.attr("cTcolor"));
      <%
        if (cc.displayValue) {
           %>$("#" + idStart + 'TD').html(tr.attr("cValue"));<%
        }
      %>
      if (hidden.val() != tr.attr("cCode")) {
        hidden.val(tr.attr("cCode"));
        hidden.attr("color", tr.attr("cColor"));
        hidden.attr("text", tr.text());
        hidden.attr("pos", tr.attr("pos"));


      <%=JSP.w(cc.onChangeScript)%>
      }
      colorDiv.remove();

    });
  });
</script>

<%--   //----------------------------------------------------------------- END DROPDOWN TABLE ----------------------------------------------------------------- --%>

<%

} else {

  ColorValueChooser.CodeColorValue codeColorValueChosen = null;
  for (ColorValueChooser.CodeColorValue codeColorValue : cc.codeColorValues) {
    if (codeColorValue!=null && codeColorValue.code!=null && codeColorValue.code.equalsIgnoreCase(JSP.w(choosenCode))) {
      codeColorValueChosen = codeColorValue;
      break;
    }
  }
  if (codeColorValueChosen==null)
    codeColorValueChosen = cc.codeColorValues.get(0);

  if (!cc.disabled) {
   if (pageState.getEntry(cc.fieldName).stringValueNullIfEmpty()==null)
      pageState.addClientEntry(cc.fieldName,codeColorValueChosen.code);

    TextField hidden = new TextField("hidden", cc.fieldName, "", 2);
    hidden.label = "";
    hidden.preserveOldValue = cc.preserveOldValue;
    hidden.script=cc.script;
    hidden.toHtml(pageContext);
  }

  if (cc.label!=null) {
    %><%=cc.label%><%
  }
  if (cc.separator!=null) {
    %><%=cc.separator%><%
  }
  String textColor = HtmlColors.contrastColor(codeColorValueChosen.color);

%><div id="<%=ColorChooser.COLOR_PREFIX+"TBL"+cc.fieldName%>" style="<%=!cc.readOnly?"cursor:pointer;":""%><%=JSP.w(cc.style)%>" <%
    if (!cc.disabled && !cc.readOnly) {
      %>onClick="cvc_clickColValCh('<%=cc.getType()%>','<%=cc.fieldName%>',this.id,event);"><%
    }
    %><div id="<%=ColorChooser.COLOR_PREFIX+cc.fieldName%>" title="<%=JSP.w(codeColorValueChosen.value)%>" style="background-color:<%=JSP.w(codeColorValueChosen.color)%>;height:<%=cc.height%>px;width:<%=cc.width%>px;border:#fff 2px solid; color:<%=textColor%>; -moz-box-shadow:0px 0px 5px #999"><%

    if (cc.displayValue) {
      %><span id="<%=ColorChooser.COLOR_PREFIX+cc.fieldName+"TD"%>" style="white-space:nowrap;padding: 0 0 0 10px;line-height:<%=cc.height%>px"><%=codeColorValueChosen.value%></span><%
      if (cc.showOpener) {
        %><span class="teamworkIcon" style="float:right;line-height:<%=cc.height%>px">[</span><%
      }
    }
    %></div></div><%

  }

%>