<%@ page
        import="org.jblooming.utilities.JSP, org.jblooming.waf.html.core.JspIncluder, org.jblooming.waf.html.core.JspIncluderSupport, org.jblooming.waf.html.display.Img, org.jblooming.waf.html.layout.Skin, org.jblooming.waf.html.menu.MenuPlus, org.jblooming.waf.view.PageState" %>
<%

  MenuPlus mp = (MenuPlus) JspIncluderSupport.getCurrentInstance(request);
  PageState pageState = PageState.getCurrentPageState();
  Skin skin = pageState.getSkin();

  if (JspIncluder.INITIALIZE.equals(request.getAttribute(JspIncluder.ACTION))) {
%>
<script type="text/javascript">
  $(function(){
    //initialize(contextPath+"/commons/layout/menuPlus/mbMenu.min.js",true);
    initialize(contextPath+"/commons/layout/menuPlus/menuPlus.js",true);
    initialize(contextPath+"/commons/layout/menuPlus/menuPlusCss.jsp");
  });
</script><%
} else if (MenuPlus.DRAW_ROOT.equals(request.getAttribute(JspIncluder.ACTION))) {
%>
<script type="text/javascript">

  $(function() {
    $("#<%=mp.id%>menuDiv").buildMenu(
    {
      template:"<%=mp.drawer%>",
      additionalData:"",
      menuWidth:<%=JSP.ex(mp.width) ? mp.width : "150"%>,
      openOnRight:false,
      menuSelector: ".menuPlusContainer",
      iconPath:"<%=skin.imgPath%>",
      hasImages:false,
      fadeTime:200,
      menuTop:0,
      menuLeft:0,
      submenuTop:0,
      submenuLeft:2,
      opacity:.90,
      shadow:true,
      closeOnMouseOut:true,
      closeAfter:2000
    });
  });
</script>
<div id="<%=mp.id%>menuDiv">
  <table cellspacing='0' cellpadding='0' border='0' class="menuPlus">
    <tr><%
      for (MenuPlus.MenuPlusElement root : mp.roots) {
    %>
      <td menu="<%=root.id%>" id="root_<%=root.id%>"><%
        if (root.imgPath != null) {
          new Img(root.imgPath,root.tooltip).toHtml(pageContext);
          if (JSP.ex(root.label)) {
            %><span class="buddyLabel"><%=root.label%></span><%
          }
        } else {
          if (JSP.ex(root.label)) {
      %><%=root.label%><%
        }
        if (JSP.ex(root.tooltip)) {
            %><%=root.tooltip%><%
          }
        }
      %></td>
      <%
        }
      %></tr>
  </table>
</div>
<%
  }  else {

    String menuId = pageState.getEntry("menuId").stringValue();

     %><div id="<%=menuId%>" class="menu"><%
    for (MenuPlus.MenuPlusElement element : mp.elements) {

      String openSubMenu = "";
      if (element.openSubMenu)
        openSubMenu = " menu=\""+element.id+"\"";

      if (element.type.equals(MenuPlus.Type.TITLE)) {
         %><a rel="title" <%=JSP.ex(element.imgPath) ? "img=\""+element.imgPath+"\"":""%>><%=element.tooltip%></a><%

      } if (element.type.equals(MenuPlus.Type.SUBMENU)) {
         %><a <%=openSubMenu%> ><%=element.tooltip%></a><%

      } else if (element.type.equals(MenuPlus.Type.SEPARATOR)) {
        %><a rel="separator"></a><%

      } else if (element.type.equals(MenuPlus.Type.ACTION)) {
        
        %><a action="<%=element.script%>" <%=openSubMenu%>><%=element.tooltip%></a><%

      } else if (element.type.equals(MenuPlus.Type.LINK)) {
        %><a href="<%=JSP.ex(element.href) ? element.href : element.pageSeed.toLinkToHref()%>" <%=openSubMenu%><%=element.popup? " target=\"_blank\"":""%>><%=element.tooltip%></a><%
      }

    }

     %></div><%

  }
%>