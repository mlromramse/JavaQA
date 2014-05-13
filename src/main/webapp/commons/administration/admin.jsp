<%@ page import="net.sf.ehcache.CacheManager,
                 org.jblooming.http.ByPassFilter,
                 org.jblooming.persistence.hibernate.PersistenceContext,
                 org.jblooming.persistence.hibernate.PlatformSchemaUpdater,
                 org.jblooming.persistence.hibernate.Release,
                 org.jblooming.tracer.Tracer,
                 org.jblooming.waf.SessionState, org.jblooming.waf.constants.Commands, org.jblooming.waf.settings.AdminControllerAction, org.jblooming.waf.settings.ApplicationState, org.jblooming.waf.settings.PersistenceConfiguration,
                 java.io.File, java.util.Enumeration" %>
<%

  String caucho = "commons" + File.separator + "administration" + File.separator + "caucho";
  ByPassFilter.freeFolders.add(caucho);
  String message = AdminControllerAction.perform(request, response);

  String command = request.getParameter(Commands.COMMAND);
  if ("forceRelease".equals(command)) {

      PersistenceContext pc = null;
      try {
        pc = PersistenceContext.getDefaultPersistenceContext();
        for (Release r : PlatformSchemaUpdater.releases) {
          if (r.releaseLabel.startsWith("4."))
            r.propertyFillAfterHibernateFactory();
          r.schemaRefinementAfterHibernateFactory();
        }
        pc.commitAndClose();
      } catch (Throwable e) {
        if (pc != null) {
          pc.rollbackAndClose();
        }
        throw e;
      }
  }
  
%><html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
  <title>platform webapp admin</title>
</head>

<body leftmargin="0" style="font-family:Verdana, Arial, Helvetica, sans-serif; font-size:12px;"
      onLoad="document.getElementById('psw').focus();">

<table width="100%" border="0" cellspacing="1" cellpadding="1"
       style="font-family: Verdana, Arial, Helvetica, sans-serif;font-size: 20px;">
  <tr valign="middle">
    <td><img src="../../logo.gif" border="0"></td>
    <td>JBlooming webapp admin<br><br></td>
    <td></td>
  </tr>
</table>

<hr>
<form action="admin.jsp" method="POST" id="admin">
<table width="100%" cellspacing="1" cellpadding="3" style="font-family: Verdana, Arial, Helvetica, sans-serif;font-size: 12px;">

<tr>
<td valign="top">

<br>
administrative password <input name="psw" id="psw" type="PASSWORD"> <br><br><br>
<input name="<%=Commands.COMMAND%>" id="<%=Commands.COMMAND%>" type="HIDDEN">

<hr>

<table><tr><td>
<%
  try {
%>
version: <%=ApplicationState.getVersion("")%><br><br>
<%
} catch (Exception e) {
%>version: versions unreadable<br><br><%
  }
%>
settings status: <%=ApplicationState.loaded ? "loaded" : "not loaded"%><br>
loaded <%=PersistenceConfiguration.persistenceConfigurations.size()%> persistence configurations<br>
server ip: <%=request.getLocalAddr()%><br>
<hr>

<table style="font-size:8pt;" border="1" cellpadding="3">
  <tr>
    <th colspan="8">Databases</th>
  </tr>

  <tr>
    <th>config name</th>
    <th>pool</th>
    <th>dialect</th>
    <th>db data</th>
    <th>select</th>

  </tr>

  <%

    for (PersistenceConfiguration pc : PersistenceConfiguration.persistenceConfigurations.values()) {
  %>
  <tr>
    <td valign="top"><%=pc.name%>
    </td>
    <td valign="top"><%=pc.poolAlias%>: <%=pc.poolLoaded ? "loaded" : "not loaded"%>
    </td>
    <td valign="top"><%=pc.dialect.getSimpleName()%>
    </td>
    <td><%=pc.driver_url%><br><%=pc.driver_class%><br><%=pc.db_user_name%>
    </td>

    <td>
      <input type="radio" name="FOCUSED_DB" id="FOCUSED_DB" value="<%=pc.name%>" <%=PersistenceConfiguration.persistenceConfigurations.values().size()==1 ? "checked":""%>>
    </td>


  </tr>
  <%
    }
  %></table>


<%--
________________________________________________________________________________________________________________________________________________________________________


  DB and data

________________________________________________________________________________________________________________________________________________________________________

--%>
<a href="#"
   onclick="
admin.<%=Commands.COMMAND%>.value='dbInfo';
admin.submit();">database info</a> <br>
<br>


<a href="#"
   onclick="
admin.<%=Commands.COMMAND%>.value='do_version_update';
admin.submit();">force version update</a> this will launch the application updater with schema evolution and data
transformation (!) <br>
<br>

<a href="#"
   onclick="
document.getElementById('<%=Commands.COMMAND%>').value='show_updates';
admin.submit();"
        >show necessary updates of db schema</a>

<br><br>

<a href="#"
   onclick="
document.getElementById('<%=Commands.COMMAND%>').value='update';
admin.submit();"
        >update db schema</a>

<br><br>

<a href="#"
   onclick="
document.getElementById('<%=Commands.COMMAND%>').value='forceRelease';
admin.submit();"
        >force release update</a>

<br><br>



<a href="#"
   onclick="
document.getElementById('<%=Commands.COMMAND%>').value='fill';
admin.submit();"
        >attempt data fill</a>

<br><br>

<a href="#"
   onclick="
document.getElementById('<%=Commands.COMMAND%>').value='export';
admin.submit();"
        >analyse/export entire db schema</a>

<br><br>

<a href="#"
   onclick="
document.getElementById('<%=Commands.COMMAND%>').value='rebuild';
admin.submit();"
        >force schema rebuild (!!!)</a>

<br><br>

table prefix to ignore: <input name="tableExcluded" id="tableExcluded" type="text" value="tw_,testSuite_">

<br><br>

<a href="#"
   onclick="
admin.action='<%=request.getContextPath()%>/commons/administration/treeCheck.jsp';
admin.submit();"
        >do a tree check</a>

<br><br>
<a href="#"
           onclick="
        document.getElementById('<%=Commands.COMMAND%>').value='show_releases';
        admin.submit();"
                >show available releases</a><%


%>



<hr>


<%--
________________________________________________________________________________________________________________________________________________________________________


  PLATFORM

________________________________________________________________________________________________________________________________________________________________________

--%>

<a href="#"
   onclick="
document.getElementById('<%=Commands.COMMAND%>').value='platform_info';
admin.submit();"
        >platform config info</a>

<br>
<br>

<a href="#"
   onclick="
admin.<%=Commands.COMMAND%>.value='restart';
admin.submit();">attempt restart settings</a><br>

<hr>
<%--
________________________________________________________________________________________________________________________________________________________________________


  SERVER

________________________________________________________________________________________________________________________________________________________________________

--%>

<a href="#"
   onclick="
document.getElementById('<%=Commands.COMMAND%>').value='server_info';
admin.submit();"
        >server info</a>
&nbsp;&nbsp;&nbsp;&nbsp;
<a href="#"
   onclick="
document.getElementById('<%=Commands.COMMAND%>').value='gc';
admin.submit();"
        >do a gc</a>

<br>
<hr>
<%--
________________________________________________________________________________________________________________________________________________________________________


  app server

________________________________________________________________________________________________________________________________________________________________________

--%>
<%
  try {
    Class.forName("com.caucho.util.QDate");%>

<a href="#"
   onclick="
admin.action='<%=request.getContextPath()%>/commons/administration/caucho/resin3.jsp';
admin.submit();"
        >access resin3 administration</a><br><br>

<%} catch (Throwable t) {%>
<%}
%>
<hr>
Total session states: <%=SessionState.totalSessionStates%><br>
<%
  Enumeration attributeNames = session.getAttributeNames();
  while (attributeNames.hasMoreElements()) {
    String name = (String) attributeNames.nextElement();
    %><%=name%>:<%=Tracer.measureSize(session.getAttribute(name))%><br><%
  }
%>
size current session: <%=session==null?"no session":Tracer.measureSize(session)%><br>

<hr>
<%
  try {
  if (PersistenceConfiguration.getDefaultPersistenceConfiguration().useEHCache) {
    CacheManager cm = CacheManager.create();
    
    String[] cc = cm.getCacheNames();
    %>EH caches:<br><%

    for (String s : cc) {

      if ("yes".equals(request.getParameter("clearstatistics")))
        cm.getCache(s).getStatistics().clearStatistics();

      if (cm.getCache(s).getSize()>0) {
        %><small><%=cm.getCache(s).getStatistics().toString()%></small><br><%
      }
    }
  }
  } catch (Throwable t) {
        
      }
%>
</td>
</tr>
</table>



</td>
<td valign="top" bgcolor="#E0E0E0">
<%

  if (command!=null) {
%>
<table width="90%" align="center" border="0" cellpadding="0" cellspacing="0">
  <tr>
    <td height="30" align="left" valign="middle" class="lineDarkBottom">
      <p class="textCredits"><b>JBlooming database utility</b><br>Attempting action: "<%=command%>"..<br>
       <%
           if ("show_releases".equals(command)) {
                %>

Available releases: <br><br><%
    for (Release r : PlatformSchemaUpdater.releases) {
        %><%=r.releaseLabel%> <a href="#"
           onclick="
        document.getElementById('<%=Commands.COMMAND%>').value='force_release<%=r.releaseLabel%>';
        admin.submit();"
                >force launch of this release update</a>

        <br><br>

        <%
                }


        } else {

       %>


        <%=message != null ? message : ""%><%
              }
        %>
      </p>
    </td>
  </tr>
</table><%
  }
%>
</td>
  </tr>
</table>

</form>

</body>
</html>