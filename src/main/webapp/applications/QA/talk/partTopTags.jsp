<%@ page contentType="text/html; charset=utf-8" pageEncoding="UTF-8" %>
<%@ page
        import="com.QA.Tag, org.jblooming.waf.settings.ApplicationState, org.jblooming.waf.view.PageState, java.util.List, org.jblooming.waf.view.PageSeed, org.jblooming.waf.settings.I18n" %>
<%
  PageState pageState = PageState.getCurrentPageState();

%>




<div class="contentBox right tags">
    <h3><span><%=I18n.g("TOP_TAGS")%></span></h3>

  <%

    List<Object[]> tags = Tag.getMostUsedTagEntities(Integer.parseInt(ApplicationState.applicationSettings.get("QUESTION_TAG_CUTOFF")),10);
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
