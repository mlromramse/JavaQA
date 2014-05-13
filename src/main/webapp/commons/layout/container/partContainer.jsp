<%@page pageEncoding="UTF-8" %><%@ page import="org.jblooming.system.SystemConstants,
                 org.jblooming.utilities.JSP,
                 org.jblooming.waf.SessionState,
                 org.jblooming.waf.constants.Commands,
                 org.jblooming.waf.html.button.ButtonJS,
                 org.jblooming.waf.html.container.Container,
                 org.jblooming.waf.html.core.JspIncluder,
                 org.jblooming.waf.html.core.JspIncluderSupport,
                 org.jblooming.waf.html.display.Img,
                 org.jblooming.waf.html.layout.HtmlColors,
                 org.jblooming.waf.html.layout.Skin,
                 org.jblooming.waf.html.state.ScreenElementStatus, org.jblooming.waf.html.button.ButtonImg"%><%

  Container container = (Container) JspIncluderSupport.getCurrentInstance(request);
  SessionState sessionState = SessionState.getSessionState(request);

  if (Container.BOX_INITIALIZE.equals(request.getAttribute(Container.ACTION))) { // ------------------------------------------------------ Container INITIALIZE
%><script type="text/javascript">
    $(function(){initialize(contextPath+"/commons/layout/container/partContainer.js",true);});
</script><%

  } else if (Container.BOX_START.equals(request.getAttribute(Container.ACTION))) { // ------------------------------------------------------ Container START


    Skin skin = sessionState.getSkin();
    String imgPath = skin.imgPath;

    StringBuffer containerStyle = new StringBuffer();
    StringBuffer containerBodyStyle = new StringBuffer();

    //set absolute position correctly
    container.absolutePosition= container.absolutePosition || container.draggable || container.centeredOnScreen;

    //recover container status
    ScreenElementStatus screenElementStatus=null;
    if(container.saveStatus)
      screenElementStatus= sessionState.screenElementsStatus.get(container.getId());


    if (screenElementStatus == null) {
      screenElementStatus = new ScreenElementStatus(container.getId());
      screenElementStatus.status = container.status;
      if (container.absolutePosition) {
        if (container.top != 0)
          screenElementStatus.y = container.top;
        if (container.left != 0)
          screenElementStatus.x = container.left;

        screenElementStatus.w = container.width;
        screenElementStatus.h = container.height;
      }
    }
    


    if (screenElementStatus.w != null) {
      screenElementStatus.w += (!screenElementStatus.w.endsWith("%") && !screenElementStatus.w.endsWith("px") ? "px" : ""); // add px if no specified
      containerStyle.append("width:" + screenElementStatus.w + "; ");
    }

    if (screenElementStatus.h != null) {
      screenElementStatus.h += (!screenElementStatus.h.endsWith("%") && !screenElementStatus.h.endsWith("px") ? "px" : ""); // add px if no specified
      containerBodyStyle.append("height:" + screenElementStatus.h + "; ");
    }

    //force overflow
    if (screenElementStatus.h != null || container.resizable){
      container.overflow = JSP.ex(container.overflow) ? container.overflow : "auto"; //in case height is specified overflow is "auto" if not differently specified
    }

    if (container.overflow!=null)
      containerBodyStyle.append("overflow:"+container.overflow+";");

    if (container.absolutePosition && (screenElementStatus.x != 0 || screenElementStatus.y != 0)) {
      if (screenElementStatus.x != 0 )
        containerStyle.append("left:").append(screenElementStatus.x).append("px;");
      if (screenElementStatus.y != 0 )
        containerStyle.append("top:").append(screenElementStatus.y).append("px;");

    }

    if (container.contentAlign != null)
      containerBodyStyle.append("text-align:" + container.contentAlign + ";");

    if (JSP.ex(container.color)) {
      containerStyle.append("background:" + container.color + ";");
      containerBodyStyle.append("color:" + HtmlColors.contrastColor(container.color) + ";");
    }

    String containerClass="";
    containerClass+=container.draggable?"draggable ":"";
    containerClass+=container.collapsable?"collapsable ":"";
    containerClass+=container.closeable?"closeable ":"";
    containerClass+=container.iconizable?"iconizable ":"";
    containerClass+=container.absolutePosition?"absolutePosition ":"";
    containerClass+=container.centeredOnScreen?"centeredOnScreen ":"";
    containerClass+=container.resizable?"resizable ":"";

%>
<div id="<%=container.getContainerId()%>" class="container <%=container.getCssPostfix()%> <%=container.getCssLevel()%> <%=containerClass%>" status="<%=screenElementStatus.status%>" <%=container.saveStatus?"saveStatus":""%> style="<%=containerStyle%>" cmdSuffix="<%=container.commandSuffix%>" <%=JSP.ex(container.containment) ? "containment=\"" + container.containment + "\"" : ""%>>

  <% if (JSP.ex(container.title)) { //containers without title are like old BOX %>
    <div id="<%=container.getContainerTitleId()%>" class="containerTitle <%=container.getCssPostfix()%> <%=container.getCssLevel()%>">
      <div class="titleIcon" style="padding-left:3px"><%
        if (container.icon != null)
          container.icon.toHtml(pageContext);

        if (container.embeddedInTitle != null) {
          container.embeddedInTitle.toHtml(pageContext);
        }
      %></div>
      <div class="title"><%=container.title == null ? "&nbsp;" : container.title %></div>
      <%
        // add left buttons if any
        if (container.titleRightElements != null && container.titleRightElements.size() > 0) {
          for (JspIncluder button : container.titleRightElements) {
            %><div class="titleRight"><%button.toHtml(pageContext);%></div><%
          }
        }
      //container status buttons
      %>
      <div class="stsButtons" align="right">
        <img src="<%=imgPath+"container/ico.png"%>" class="stsIconize" onclick="$(this).closest('.container').trigger('iconize');">
        <img src="<%=imgPath+"container/max.png"%>" class="stsRestore" onclick="$(this).closest('.container').trigger('restore');">
        <img src="<%=imgPath+"container/min.png"%>" class="stsCollapse" onclick="$(this).closest('.container').trigger('collapse');">
        <img src="<%=imgPath+"container/close.png"%>" class="stsHide" onclick="$(this).closest('.container').trigger('hide');">
      </div>
    </div>
  <%}%>
  <div id="<%=container.getContainerBodyId()%>" class="containerBody" style="<%=containerBodyStyle%>"><%

  } else if (Container.BOX_END.equals(request.getAttribute(Container.ACTION))) { // ------------------------------------------------------ Container END
  %></div></div>
<script type="text/javascript">
  $(function() {
    $("#<%=container.getId()%>").containerBuilder();
  });
</script>
<%

  }
%>