<%@ page import="org.jblooming.utilities.JSP,
                 org.jblooming.waf.html.core.JspIncluderSupport,
                 org.jblooming.waf.html.display.Img,
                 org.jblooming.waf.html.input.ComboBox,
                 org.jblooming.waf.html.input.TextField,
                 org.jblooming.waf.view.PageState" %><%

  ComboBox comboBox = (ComboBox) JspIncluderSupport.getCurrentInstance(request);
  PageState pageState = PageState.getCurrentPageState();
  String choosenCode = pageState.getEntry(comboBox.fieldName).stringValueNullIfEmpty();

  if (ComboBox.INITIALIZE.equals(request.getAttribute(ComboBox.ACTION))) {
%><script>
  $(function(){initialize(contextPath+"/commons/layout/comboBox/partComboBox.js.jsp")});
</script><%

} else if (ComboBox.FINALIZE.equals(request.getAttribute(ComboBox.ACTION))) {
  boolean swap = false;
  String style = "display:none;position:absolute;background-color:#ffffff;border:#808080 1px solid;cursor:pointer;z-index:1000;overflow:auto;";
  if (comboBox.style!=null)
    style= style +comboBox.style;
  style = style +(comboBox.divHeight>0 ?"height:" + comboBox.divHeight +"px;":"")+ (comboBox.divWidth>0? "width:"+comboBox.divWidth+"px;":"");
%>
<div id='<%=comboBox.getType()%>_DIV' style="<%=style%>" openerId="" totRows="<%=comboBox.values.size()%>" currentRow="0" onmousedown="obj(this.getAttribute('openerId')).setAttribute('dontHide', 'true');" onSelectScript="<%=JSP.w(comboBox.onSelectScript)%>">
  <table border="0" cellpadding="1" cellspacing="0" width="100%"><%
    int i = 0;
    for (String value : comboBox.values) {
      i++;
  %>
    <tr class="alternate" onMouseOver="cb_mouseOverRow(this,'<%=comboBox.getType()%>','<%=i%>');" onMouseOut="this.className=this.getAttribute('oldclass');"
                                                     id="<%=comboBox.getType()%>_ROW_<%=i%>" value="<%=JSP.w(value)%>" oldClass=""
                                                     onmousedown="cb_clickRow(this, '<%=comboBox.getType()%>');stopBubble(event);">
      <td>
        <%=JSP.w(value)%>
      </td>
    </tr>
    <%
      }
      if (comboBox.values.size()<=0){
    %><tr id="<%=comboBox.getType()%>_ROW_1" value="" oldClass="">
    <td>
      <%=I18n.get("COMBO_NO_VALUES")%>
    </td>
  </tr>
    <%
      }


    %></table>
</div>
<%
  } else {


    String script = "onFocus=\"cb_initializeCombo(this,'" + comboBox.getType()+ "', '" + comboBox.fieldName  + "',event); \" "+JSP.w(comboBox.script);
    script += " onBlur=\"if (this.getAttribute('dontHide')!='true'){ $('#" + comboBox.getType() + "_DIV').hide();"+JSP.w(comboBox.additionalOnBlurScript)+";}else{this.focus();}\"";
    script += " onKeyDown=\"cb_manageKeyEvent ('" + comboBox.getType() + "','" + comboBox.fieldName + "',event.keyCode);\"";
    script += " autocomplete=\"off\"";

    TextField textField = new TextField("text", comboBox.label, comboBox.fieldName, comboBox.separator, comboBox.fieldSize, comboBox.disabled, comboBox.readOnly, (comboBox.readOnly || comboBox.disabled) ? "" : script );
    textField.toolTip = comboBox.toolTip;
    textField.fieldClass=comboBox.htmlClass;
    textField.preserveOldValue = comboBox.preserveOldValue;
    textField.searchField=comboBox.forQBE;
    textField.entryType=comboBox.entryType;
    textField.required=comboBox.required;
    textField.innerLabel=comboBox.innerLabel;
    textField.script=textField.script + ( JSP.ex(comboBox.style)? "style=\""+comboBox.style+"\"":"");
    textField.toHtml(pageContext);


    Img open = new Img(pageState.getSkin().imgPath + (comboBox.forQBE ? "cbxQBE.gif" : "smartComboOpen.gif"), "");
    open.disabled = comboBox.readOnly || comboBox.disabled;
    if(!open.disabled)
     open.script = " onMouseDown=\"if (obj('"+comboBox.getType() + "_DIV').style.display=='none' ) {obj('" + comboBox.fieldName + "').blur(); obj('" + comboBox.fieldName + "').focus();return false;} \"";
    if (comboBox.forQBE)
      open.toolTip = I18n.get("USE_QBE_IN_THIS_FIELD");
    open.toHtml(pageContext);
  }

%>