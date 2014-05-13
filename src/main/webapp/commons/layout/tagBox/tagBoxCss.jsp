<%@ page import="org.jblooming.waf.settings.ApplicationState" %>
<style type="text/css">
  .tagBoxSel {
    background-color:yellow;
  }
  .tbDiv{
  display:none;
  background-color:white;
  position:absolute;
  overflow:auto;
  border:2px solid #d0d0d0;
  padding:2px;
  }
  .tagBoxLine{
    border-bottom:1px solid #e0e0e0;
    color:black;
    font-weight:normal;
  }
  .tagBox{
padding-left:20px;
background-image:url("<%=ApplicationState.contextPath%>/commons/skin/images/tags.gif");
background-position:2px 2px;
background-repeat:no-repeat;
  }
</style>