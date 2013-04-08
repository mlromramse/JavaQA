<%@ page import="org.jblooming.oql.OqlQuery, org.jblooming.security.SecurableWithArea, org.jblooming.utilities.ReflectionUtilities, org.jblooming.utilities.StringUtilities, org.jblooming.waf.view.PageState, java.util.List, java.util.Set, java.util.TreeSet" %>
<%
  PageState pageState = PageState.getCurrentPageState();

  String search = pageState.getEntry("FN").stringValue();
  //if (JSP.ex(search)) {

    search = ""+search.toLowerCase();
    Class taggableClass = Class.forName(pageState.getEntry("CL").stringValue());

    int areaId = pageState.getEntry("AID").intValueNoErrorCodeNoExc();
    int maxResult = pageState.getEntry("MR").intValueNoErrorCodeNoExc();
    String tagPropertyName = pageState.getEntryAndSetRequired("TPN").stringValueNullIfEmpty();

    // check if the class is secured with area or not
    boolean areaSecured = ReflectionUtilities.extendsOrImplements(taggableClass, SecurableWithArea.class);


    // select distinct in the area
    String oql = "select distinct ob." + tagPropertyName + " from " + taggableClass.getName() + " as ob where ob." + tagPropertyName + " is not null";

    if (areaSecured)
      oql = oql + " and ob.area.id=:aid";


    OqlQuery oq = new OqlQuery(oql);
    if (areaSecured)
      oq.getQuery().setInteger("aid", areaId);


    List<String> tagsraw = oq.list();

    Set<String> unsorted = new TreeSet();

    int n = 0;
    for (String tagsmul : tagsraw) {
      List<String> tags = StringUtilities.splitToList(tagsmul, ",");
      for (String tag : tags) {
        if (tag.trim().toLowerCase().startsWith(search)) {
          unsorted.add(tag.trim());
          if (unsorted.size() >= maxResult)
            break;
        }
      }
      if (unsorted.size() >= maxResult)
        break;
    }

    for (String tag : unsorted) {
        %> <div class="tagBoxLine"><%=tag%> </div> <%
    }

  //}

%>