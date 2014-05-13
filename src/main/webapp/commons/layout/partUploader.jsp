<%@ page import="org.jblooming.ontology.PersistentFile,
                 org.jblooming.tracer.Tracer,
                 org.jblooming.utilities.JSP,
                 org.jblooming.waf.constants.Commands,
                 org.jblooming.waf.constants.Fields,
                 org.jblooming.waf.html.button.ButtonLink,
                 org.jblooming.waf.html.core.JspIncluderSupport,
                 org.jblooming.waf.html.display.Img,
                 org.jblooming.waf.html.input.Uploader,
                 org.jblooming.waf.html.layout.Skin,
                 org.jblooming.waf.view.ClientEntry,
                 org.jblooming.waf.view.PageSeed,
                  org.jblooming.waf.view.PageState"%><%

  Uploader upl = (Uploader) JspIncluderSupport.getCurrentInstance(request);
  PageState pageState = PageState.getCurrentPageState();
  Skin skin = upl.skin;
  String label = upl.label;
  if (label == null)
    label = upl.fieldName;
  if (label != null && label.length() > 0) {
    if (upl.classLabelName != null && upl.classLabelName.length() > 0) {
      %><span class="<%=upl.classLabelName%>"><%
    }
    %><%=label%><%=(upl.required && label.indexOf("*") == -1 ? "*" : "")%><%

    if (upl.classLabelName != null && upl.classLabelName.length() > 0) {
      %></span><%
    }
  }

  %><%=upl.separator%><table border="0" width="<%=upl.size+10%>"><tr><td nowrap><%

  if (upl.className == null || upl.className.trim().length() == 0) {
    upl.className = "formElements";
  }

  ClientEntry entry = pageState.getEntry(upl.fieldName);
  ClientEntry persistedFileUID = pageState.getEntry("sp_fi_br_" + upl.id);

  String uplUID =entry.stringValueNullIfEmpty();

  uplUID = uplUID!=null && uplUID .trim().length()>0 ? uplUID : persistedFileUID.stringValueNullIfEmpty();
  //uplUID = uplUID!=null && uplUID.trim().length()>0 ? uplUID : amTrick.stringValueNullIfEmpty();

  String spanlabel = "";
  if (uplUID != null && !"null".equals(uplUID) && uplUID.length() > 0) {
    PersistentFile persistentFile = null;

    //shitty behaviour on invalid entries - hence patched with try-catch
    try {
      persistentFile = PersistentFile.deserialize(uplUID);
    } catch (Throwable t) {
      Tracer.platformLogger.warn(t);
    }

    if (persistentFile!=null)
      spanlabel = persistentFile.getName();
  }


  boolean showUpload = (uplUID == null || (uplUID!=null && uplUID.equals("null")));
  boolean fileDisabled = !showUpload || upl.disabled ;
  String preservedSpanLabel = uplUID!=null && uplUID.trim().length()>0 ? uplUID : persistedFileUID.stringValue();

  %><input type="hidden" id="sp_fi_br_<%=upl.id%>" name="sp_fi_br_<%=upl.id%>" value="<%=preservedSpanLabel%>">
  <span id="sp_fi_<%=upl.id%>" style="display:<%=(showUpload ? "":"none;")%>">
    <input type="file" name="<%=upl.fieldName%>" id="<%=upl.id%>" size="<%=upl.size%>" class="<%=upl.className%>" <%=upl.generateToolTip()%> <%=(upl.readOnly || fileDisabled ? " disabled" : "")%> <%=(upl.jsScript != null && upl.jsScript.length() > 0 ? ' ' + upl.jsScript : "")%><%
    if (upl.launchedJsOnActionListened != null && !fileDisabled) {
      %><%=upl.actionListened%>= "if (event.keyCode==<%=upl.keyToHandle%>) { <%=upl.launchedJsOnActionListened%> }"<%
    }
   %>><%

  if(showUpload) {
    out.print("</td> <td nowrap>");
  }

  PageSeed myself = new PageSeed(request.getContextPath() + "/commons/layout/partUploaderView.jsp");
  myself.setCommand(Commands.FILE_VIEW);
  myself.addClientEntry("TREATASATTACH", upl.treatAsAttachment ? Fields.TRUE : Fields.FALSE);
  myself.addClientEntry(Fields.FILE_TO_UPLOAD, uplUID);
  ButtonLink doc = new ButtonLink(myself);
  doc.width=""+upl.size;
  doc.label = spanlabel;
  doc.target = "_blank";
  doc.enabled=!JSP.ex(entry.errorCode);

  %></span>

  <div title="<%=uplUID%>" id="sp_tf_<%=upl.id%>" style="display:<%=(showUpload ? "none;":"")%>" <%=upl.className != null ? "class=\"" + upl.className + "\"" : ""%>">
    <%doc.toHtmlInTextOnlyModality(pageContext);%>
  </div> <%

  if (!showUpload ) {
    %></td><td><%
    Img act = new Img(skin.imgPath + "uploader/" + (showUpload ? "link.gif" : "unlink.gif"), "");
    act.align = "absmiddle";

    if (!upl.disabled && !upl.readOnly) {
      act.script = "onClick=\"if (obj('sp_fi_" + upl.id + "').style.display=='none'){" +
        "obj('sp_fi_br_" + upl.id + "').value='';" +
              "obj('sp_fi_" + upl.id + "').style.display='';" +
              "obj('" + upl.id + "').disabled=false;"+
              "obj('sp_tf_" + upl.id + "').style.display='none';" +
              "obj('" + act.id + "').src='" + skin.imgPath + "uploader/link.gif';" +
              "}else{" +
              "obj('sp_fi_br_" + upl.id + "').value='"+uplUID.replaceAll("'", "&rsquo;")+"';" +
              "obj('sp_fi_" + upl.id + "').style.display='none';" +
              "obj('" + upl.id + "').disabled=true;" +
              "obj('sp_tf_" + upl.id + "').style.display='';" +
              "obj('" + act.id + "').src='" + skin.imgPath + "uploader/unlink.gif';" +
              "}\"";
    }
    act.toHtml(pageContext);
  }

  if (upl.doFeedBackError) {
    %></td><td><%
     JSP.feedbackError(entry, upl.translateError, pageContext);
  }

%></td></tr>
</table>