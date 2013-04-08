<%@ page contentType="text/html; charset=utf-8" pageEncoding="UTF-8" %>
<%@ page
        import="com.QA.Tag, org.jblooming.waf.settings.ApplicationState, org.jblooming.waf.view.PageState, java.util.List, org.jblooming.waf.view.PageSeed" %>
<%
  PageState pageState = PageState.getCurrentPageState();

%>
<style type="text/css">
  .tagDivDetail {
    text-overflow: ellipsis;
    overflow: hidden;
    float: left;
    background-color: white;
    padding: 3px;
    margin: 3px;
    height: 50px;
    width: 20%;
  }

  .tagDiv{
      height: auto; margin-bottom: 15px
  }

</style>


<h2><span>Tags</span></h2>
<div style="width: 100%" class="contentBox tagViewer">
  <%

    List<Object[]> tags = Tag.getMostUsedTagEntities(Integer.parseInt(ApplicationState.applicationSettings.get("QUESTION_TAG_CUTOFF")),50);
    if (tags.size()==0)
      tags = Tag.getMostUsedTagEntities(0,10);

    for (Object[] o : tags) {

      Tag tag = (Tag) o[1];

      PageSeed t = pageState.pageFromRoot("talk/index.jsp");
      t.addClientEntry("WHAT","TAG");
      t.addClientEntry("TAG",tag.getId());

      %>

  <div class="tagDiv"><span><a href="<%=t.toLinkToHref()%>"><%=tag.getName()%></a> <b><small>x <%=o[0]%></small></b></span></div>

  <%

    }


  %><br style="clear: both"></div>
