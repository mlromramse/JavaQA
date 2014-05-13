<%@ page import="org.jblooming.ontology.PersistentFile,
                 org.jblooming.waf.html.display.Img,
                 org.jblooming.waf.view.PageState"%><%@page pageEncoding="UTF-8" %><%

  PageState pageState = PageState.getCurrentPageState();
  String pfSer = pageState.getEntry("FILE_TO_SHOW").stringValueNullIfEmpty();
  String title = pageState.getEntry("TITLE").stringValueNullIfEmpty();
  String width = pageState.getEntry("IMGW").stringValueNullIfEmpty();

  if (pfSer != null) {
    PersistentFile image = PersistentFile.deserialize(pfSer);
    Img img = new Img(image, title);
    img.style = "visibility:visible";
    if (width != null && !width.equals("0"))
      img.width = width;

    img.toHtml(pageContext);
  }
%>