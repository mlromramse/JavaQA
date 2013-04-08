<%@ page import="org.jblooming.waf.html.input.CheckField, org.jblooming.waf.view.PageState" %>
<script type="text/javascript">
  function enableCheck(){
    if(obj('ck_CHECK_UNZIP').checked)
    obj('ck_CHECK_OVERWRITE').disabled = false;
    else
     obj('ck_CHECK_OVERWRITE').disabled = true;
  }
</script>
<div id="_check">
  <%
    PageState pageState = PageState.getCurrentPageState();
    if("y".equals(pageState.getEntry("DISPLAY_CHECK_UNZIP").stringValueNullIfEmpty())){
  %>
  <table>
    <tr>
      <td nowrap> <%
          CheckField check = new CheckField("CHECK_UNZIP", "", false);
          check.label = "<small>" + I18n.get("UNZIP") + "</small>";
          check.toolTip = I18n.get("CHECK_FOR_UNZIP_DOC");
          check.additionalOnclickScript = " enableCheck(); ";
          check.toHtml(pageContext);
        %></td>
    </tr><%

      } 
if("y".equals(pageState.getEntry("DISPLAY_CHECK_OVERWRITE").stringValueNullIfEmpty())){
        
      %><tr>
      <td nowrap><%
        CheckField check = new CheckField("CHECK_OVERWRITE", "", false);
        check.label = "<small>" + I18n.get("OVERWRITE") + "</small>";
        check.toolTip = I18n.get("CHECK_FOR_OVERWRITE_DOC");
        check.toHtml(pageContext);
      %></td>
    </tr><%
    }
      %>
  </table>
</div>