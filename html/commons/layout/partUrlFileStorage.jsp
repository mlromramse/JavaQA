<%@ page import="org.jblooming.remoteFile.BasicDocumentBricks,
                 org.jblooming.waf.SessionState,
                 org.jblooming.waf.html.button.ButtonImg,
                 org.jblooming.waf.html.button.ButtonJS,
                 org.jblooming.waf.html.button.ButtonLink,
                 org.jblooming.waf.html.core.JspIncluderSupport,
                 org.jblooming.waf.html.display.Img,
                 org.jblooming.waf.html.input.TextField,
                 org.jblooming.waf.html.input.UrlFileStorage,
                 org.jblooming.waf.html.layout.Skin,
                 org.jblooming.waf.view.PageSeed, org.jblooming.waf.view.PageState" %>
<%
  PageState pageState = PageState.getCurrentPageState();
  SessionState sm = pageState.getSessionState();
  Skin skin = pageState.getSkin();

  if (UrlFileStorage.DRAW.equals(request.getAttribute(UrlFileStorage.ACTION))) {
    /* --------------------------------------- START DRAW PART ----------------------------------------------------------------- */

    UrlFileStorage ufs = (UrlFileStorage) JspIncluderSupport.getCurrentInstance(request);

%><%=ufs.label%><%=ufs.separator%><%
  String fieldContent = pageState.getEntry(ufs.fieldName).stringValueNullIfEmpty();
  String explorerPart = ufs.explorerPart;
  PageSeed downOrExplore = BasicDocumentBricks.getPageSeedForContent(fieldContent, explorerPart, pageState);

  boolean fieldAlreadyFilled = (fieldContent != null);

  // draw text field and link button
%>
<div id="reload_"><span id="<%=ufs.id%>_link" style="<%=(!fieldAlreadyFilled ? "display:inline;" : "display:none;")%>">
  <table>
    <tr>
      <td><%

        TextField tf = new TextField(ufs.fieldName, "");
        tf.label = "";
        tf.fieldSize = 40;

        tf.readOnly = ufs.readOnly;
        tf.toHtmlI18n(pageContext);
      %></td>
      <td><%

        Img linkImage = new Img(skin.imgPath + "uploader/link.gif", "");
        // pageSeed and button for editing
        PageSeed explorer = new PageSeed(ufs.popupPart);
        explorer.setCommand("VIEW_LIST");
        explorer.mainObjectId = ufs.referralObjectId;
        explorer.addClientEntry("OPENER_FIELD_ID", tf.id);
        ButtonLink popUp = null;
        if (fieldAlreadyFilled) {
          popUp = new ButtonLink(downOrExplore); // modified
        } else {
          popUp = new ButtonLink(explorer);
        }
        popUp.target = "ddd";
        popUp.popup_width = "924";
        popUp.popup_height = "768";
        popUp.popup_scrollbars = "yes";

        ButtonImg butImg = new ButtonImg(popUp, linkImage);
        butImg.toHtml(pageContext);
      %></td>
    </tr>
  </table></span></div>
<%

  // draw link to document and un-link button
  if (fieldAlreadyFilled && !ufs.readOnly) {
%><span id="<%=ufs.id%>_unlink">
  <table>
    <tr>
      <td><%
        // pageSeed for display data
        //String explorerPart = ufs.explorerPart;
        //PageSeed downOrExplore = DocumentBricks.getPageSeedForExplorer(fieldContent, explorerPart, pageState);
        if (downOrExplore != null) {
          downOrExplore.addClientEntry("OPENER_FIELD_ID", tf.id);
          ButtonLink openDownloadDocument = new ButtonLink(fieldContent, downOrExplore);
          openDownloadDocument.style = (fieldAlreadyFilled ? "display:block;" : "display:none;");
          openDownloadDocument.target = "ddd";
          openDownloadDocument.popup_width = "924";
          openDownloadDocument.popup_height = "768";
          openDownloadDocument.popup_scrollbars = "yes";
          openDownloadDocument.popup_resizable = "yes";
          openDownloadDocument.toHtmlInTextOnlyModality(pageContext);
      %></td>
      <td><%
        Img unlinkImage = new Img(skin.imgPath + "uploader/unlink.gif", "");
        ButtonJS bjs = new ButtonJS("$('#" + ufs.id + "_link').show();$('#" + ufs.id + "_unlink').hide();");
        ButtonImg butunl = new ButtonImg(bjs, unlinkImage);
        butunl.toHtml(pageContext);
      } else {
      %><%=I18n.get("INVALID_DOCUMENT_REFERENCE")%>
        :<%=pageState.getEntry(ufs.fieldName).stringValueNullIfEmpty()%><%
        }
      %></td>
    </tr>
  </table></span><%
    }
  }
%>