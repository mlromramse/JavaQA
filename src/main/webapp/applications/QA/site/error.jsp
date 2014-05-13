<%@ page import="org.jblooming.ApplicationRuntimeException, org.jblooming.tracer.Tracer, org.jblooming.utilities.JSP, org.jblooming.waf.settings.I18n" %><%@page isErrorPage="true" %><%@page pageEncoding="UTF-8" %><%

  String exc = ApplicationRuntimeException.getStackTrace(exception);

  try {
    Tracer.desperatelyLog(exception.getMessage(), false, exception);
  } catch (Throwable e) {
    e.printStackTrace();
  }


%><html><head><meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/><title><%=I18n.g("QA_APP_NAME")%> - error</title></head><body>
<img src="/applications/QA/images/logo.png" alt="">

<h3>Something went wrong- should discuss it on Q&A :-D</h3>

<a href="/index.jsp">Back to <%=I18n.g("QA_APP_NAME")%></a>
<hr>
Eccezione (riportata per il supporto tecnico):
<pre><%=exc%></pre>

<hr>
<%
  try {
    String errorCode = JSP.w(request.getAttribute("javax.servlet.error.status_code"));
    String errorMessage = JSP.w(request.getAttribute("javax.servlet.error.message")) + "\n" +
            "servlet path:" + JSP.w(request.getAttribute("javax.servlet.forward.servlet_path")) + "\n" +
            "servlet: " + JSP.w(request.getAttribute("javax.servlet.error.servlet_name")) + "\n" +
            "exception type: " + JSP.w(request.getAttribute("javax.servlet.error.exception_type"));

    String requestUrl = JSP.w(request.getAttribute("javax.servlet.error.request_uri"));

%>
More info:<pre>
  errorCode:<%=errorCode%><hr>
  errorMessage:<%=errorMessage%><hr>
  requestUrl:<%=requestUrl%><hr>
</pre>
<%
  } catch (Throwable e) {
    e.printStackTrace();
  }
%>


</body>
</html>