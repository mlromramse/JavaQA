<%@ page import="com.QA.StripTypes, org.jblooming.utilities.CollectionUtilities, java.util.Collections, java.util.List" %>
<table border="1" cellpadding="2" cellspacing="2"><tr>
  <th>getFilterTemplateTypes</th>
  <th>getVisibleFilterTypes</th>
  <th>getWorkWeekTypes</th>
  <th>getAddBookmarkTypes</th>
  <!--th>getAdditionalFilterTypes</th>
  <th>getAddUserTypes</th-->
  <th>getBookletTypes</th>
  <th>getEditorTypes</th>
  <th>getKanbanTypes</th>
  <th>getStripTemplatesInitedTypes</th>
  <th>getTagOrganizerTypes</th>
  <th>getMobileListTypes</th>
  <th>TEST</th>

</tr>
  <tr>
    <td valign="top">
  <%
  List<String> stringList = StripTypes.getFilterTemplateTypes();
  Collections.sort(stringList);
  for (String s : stringList) {
     %><%=s%><br><%
  }
  %></td>
    
   <td valign="top"><%
  stringList = StripTypes.getVisibleFilterTypes();
  Collections.sort(stringList);
  for (String s : stringList) {
     %><%=s%><br><%
  }
  %></td>

  <td valign="top"><%
  stringList = StripTypes.getWorkWeekTypes();
  Collections.sort(stringList);
  for (String s : stringList) {
     %><%=s%><br><%
  }
  %></td>

    <td valign="top"><%
    stringList = StripTypes.getAddBookmarkTypes();
    Collections.sort(stringList);
    for (String s : stringList) {
       %><%=s%><br><%
    }
    %></td>

    <%--td valign="top"><%
    stringList = StripTypes.getAdditionalFilterTypes();
    Collections.sort(stringList);
    for (String s : stringList) {
       %><%=s%><br><%
    }
    %></td>

    <td valign="top"><%
    stringList = StripTypes.getAddUserTypes();
    Collections.sort(stringList);
    for (String s : stringList) {
       %><%=s%><br><%
    }
    %></td--%>

    <td valign="top"><%
        stringList = StripTypes.getBookletTypes();
        Collections.sort(stringList);
        for (String s : stringList) {
           %><%=s%><br><%
        }
        %></td>

    <td valign="top"><%
        stringList = StripTypes.getEditorTypes();
        Collections.sort(stringList);
        for (String s : stringList) {
           %><%=s%><br><%
        }
        %></td>

     <td valign="top"><%
        stringList = StripTypes.getKanbanTypes();
        Collections.sort(stringList);
        for (String s : stringList) {
           %><%=s%><br><%
        }
        %></td>

    <td valign="top"><%
        stringList = StripTypes.getStripTemplatesInitedTypes();
        Collections.sort(stringList);
        for (String s : stringList) {
           %><%=s%><br><%
        }
        %></td>

    <td valign="top"><%
        stringList = StripTypes.getTagOrganizerTypes();
        Collections.sort(stringList);
        for (String s : stringList) {
           %><%=s%><br><%
        }
        %></td>

     <td valign="top"><%
        stringList = StripTypes.getMobileListTypes();
        Collections.sort(stringList);
        for (String s : stringList) {
           %><%=s%><br><%
        }
        %></td>



  <td valign="top"><%

  java.util.List<String> tot = CollectionUtilities.toList("TODO","IDEA","NOTE","IMAGE","BOOKMARK","MILESTONE","WORKLOG",
                 "REMINDER","APPOINTMENT","OUTCOME","EFFORT","COST","BUDGET","CONTACT_PERSON","CONTACT_COMPANY","COST_REPORT","WEEKLY_REVIEW","STATUS_UPDATE","EVERNOTE"
);

  java.util.Collections.sort(tot);
  for (String s : tot) {
     %><%=s%><br><%
  }
  %></td>


</tr></table>