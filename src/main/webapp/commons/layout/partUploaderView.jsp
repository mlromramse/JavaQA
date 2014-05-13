<%@ page import="org.jblooming.persistence.exceptions.FindByPrimaryKeyException,
                 org.jblooming.tracer.Tracer,
                 org.jblooming.waf.html.input.Uploader,
                 org.jblooming.waf.view.PageState,java.io.IOException" %><%@ page contentType="text/html; charset=utf-8" pageEncoding="UTF-8"%><%

  PageState pageState = PageState.getCurrentPageState();
  boolean treatAsAttachment = pageState.getEntry("TREATASATTACH").checkFieldValue();
  try {
    try {
      Uploader.displayFile(treatAsAttachment, pageState, response);
    } catch (FindByPrimaryKeyException e) {
        Tracer.platformLogger.error(e);

    }
  } catch (IOException a) {
    Tracer.platformLogger.error(a);
  }

%>