  <%@ page import="java.util.Enumeration"%>
   request:<br><br><%
    Enumeration parameters =  request.getParameterNames();

    out.print(System.currentTimeMillis()+"<br>");

    //Iterator i = parameters.iterator();
    while (parameters.hasMoreElements()) {

      String requestString = (String)parameters.nextElement();
      %><%=requestString%> - <%=request.getParameter(requestString)%><br><%
    }
      %><hr>
    header:<br><br><%
    Enumeration en = request.getHeaderNames();
    while (en.hasMoreElements()) {
      String s = (String) en.nextElement();
      out.println(s+": "+request.getHeader(s)+"<br>");
    }

  %>request.getContextPath():<%=request.getContextPath()%><br>
  %>request.getContentType():<%=request.getContentType()%><br><br>
  %>request.getRemoteAddr():<%=request.getRemoteAddr()%><br>
  %>request.getRemoteHost():<%=request.getRemoteHost()%><br>
