<%@ page import="com.patapage.ontology.PataBinaryLargeObject,org.jblooming.ApplicationException,org.jblooming.ontology.Identifiable,org.jblooming.ontology.PersistentFile,org.jblooming.operator.Operator,
                org.jblooming.oql.OqlQuery,org.jblooming.persistence.PersistenceHome,org.jblooming.persistence.exceptions.FindException,org.jblooming.tracer.Tracer,org.jblooming.utilities.JSP,
                org.jblooming.utilities.file.FileUtilities,org.jblooming.waf.SessionState,org.jblooming.waf.UploadHelper,org.jblooming.waf.constants.Commands,org.jblooming.waf.html.button.ButtonJS,org.jblooming.waf.html.button.ButtonSubmit,
                org.jblooming.waf.html.display.MultimediaFile,org.jblooming.waf.html.input.InputElement, org.jblooming.waf.html.input.TextField, org.jblooming.waf.html.input.Uploader,
                org.jblooming.waf.html.layout.Css,org.jblooming.waf.html.layout.Skin, org.jblooming.waf.html.state.Form, org.jblooming.waf.settings.ApplicationState,org.jblooming.waf.view.ClientEntry,
                org.jblooming.waf.view.PageSeed, org.jblooming.waf.view.PageState, java.io.FileInputStream,
                java.io.FileNotFoundException, java.util.List"%><%@ page pageEncoding="UTF-8" %><%!

  private static List<PataBinaryLargeObject> siteImages (String objectClassName, int objectId) throws FindException {
    StringBuffer sb = new StringBuffer();
    sb.append(" from "+ PataBinaryLargeObject.class.getName()+" as bl ");
      sb.append(" where bl.key=:cnt ");
    if((JSP.ex(objectClassName) || JSP.ex(objectId)))
      sb.append(" and ");

    if(JSP.ex(objectClassName)) {
      sb.append(" bl.referralClass=:stix ");
      if(JSP.ex(objectId))
        sb.append(" and");
    }
    if(JSP.ex(objectId))
      sb.append(" bl.referralId=:stid");

    sb.append(" order by bl.creationDate desc");

    OqlQuery oql = new OqlQuery(sb.toString());
    oql.getQuery().setString("cnt", "IMG_CNT");
    if(JSP.ex(objectClassName))
      oql.getQuery().setString("stix", objectClassName);
    if(JSP.ex(objectId))
      oql.getQuery().setInteger("stid",objectId);

    List<PataBinaryLargeObject> blobs = oql.list();
    return blobs;
  }

%><!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html><head>
  <link rel=stylesheet href="<%=request.getContextPath()+"/commons/skin/patapage/css/platformCss.jsp"%>?<%=ApplicationState.getBuild()%>" type="text/css" media="screen">    
  <script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.3/jquery.min.js?<%=ApplicationState.getBuild()%>"></script>
  <script type="text/javascript" src="<%=request.getContextPath()+"/commons/js/jquery/jquery.livequery.min.js"%>?<%=ApplicationState.getBuild()%>"></script>
  <script type="text/javascript" src="<%=request.getContextPath()+"/commons/layout/tinymce/tiny_mce_popup.js"%>"></script>
  <script type="text/javascript" src="<%=request.getContextPath()%>/commons/layout/tinymce/plugins/imagesUploader/js/imagesUploader.js"></script>
  <script language="javascript" type="text/javascript" src="../../tiny_mce_popup.js"></script>
  <script type="text/javascript" src="<%=request.getContextPath()+"/commons/js/platform.js"%>?<%=ApplicationState.getBuild()%>"></script><%

  // DO NOT MOVE FROM HERE (security exception in IE)

  PageState pageState = PageState.getCurrentPageState();
  SessionState sessionState = pageState.sessionState;
  Operator logged = (Operator) pageState.getLoggedOperator();
  String objectClass = request.getParameter("objectClass");
  String objectId = request.getParameter("objectId");
  String uploading = pageState.getEntry("UPLOAD").stringValueNullIfEmpty();
  String blid = pageState.getEntry("BLOBX_ID").stringValueNullIfEmpty();

  Identifiable mainObject = null;
  if(JSP.ex(objectClass) && JSP.ex(objectId))
    mainObject = (Identifiable) PersistenceHome.findByPrimaryKey((Class<? extends Identifiable>) Class.forName(objectClass), objectId);

  /**
   * UPLOAD PART
   */
  if (mainObject!=null && Commands.SAVE.equals(pageState.getCommand()) && uploading != null) {
    pageState.tokenValidate("imageUpl");
    String ext = FileUtilities.getFileExt(uploading);
    boolean validFileType = FileUtilities.isImageByFileExt(ext); // jpg, jpeg, gif, bmp, png, dwg

    if (!validFileType) {
      ClientEntry ceNull = new ClientEntry("UPLOAD", null);
      ceNull.errorCode = "Unallowed file extension";
      pageState.addClientEntry(ceNull);

    } else {

      UploadHelper auh = UploadHelper.getInstance("UPLOAD", pageState);
      ClientEntry entry = pageState.getEntry("UPLOAD");

      if (auh != null && JSP.ex(auh.originalFileName)) {
        //check file size 500Kb max allowed
        if (auh.temporaryFile.exists()) {
          if (auh.temporaryFile.length() > 500 * 1024)
            pageState.getEntry("UPLOAD").errorCode = "Please upload a smaller file. 500Kb is max size allowed.";
            entry.setValue(null);
        }

        if(pageState.validEntries()) {
          try {
            PataBinaryLargeObject blo = new PataBinaryLargeObject();
            blo.feed(new FileInputStream(auh.temporaryFile));
            blo.setReferral(mainObject);
            blo.setOriginalFileName(auh.originalFileName);
            blo.setKey("IMG_CNT");
            blo.store();
            entry.setValue(null);

          } catch (FileNotFoundException e) {
            throw new ApplicationException(e);
          }
        }
      }
    }

  /**
   * DELETE PART
   */
  } else if (Commands.DELETE.equals(pageState.getCommand()) && JSP.ex(blid)) {
      pageState.tokenValidate("imageUpl");
      PataBinaryLargeObject bo = PataBinaryLargeObject.load(blid);
      if (bo != null)
        bo.remove();
  }

  %><script type="text/javascript">
      var serverURL = "<%=ApplicationState.serverURL%>";
      var contextPath = "<%=ApplicationState.contextPath%>";
      var inhibitTooltips = "true";
      var skinImgPath = "<%=pageState!=null ? pageState.getSkin().imgPath : "images"%>";
  </script>
	<base target="_self"/>
	</head><body><%

  Skin skin = sessionState.getSkin();

  PageSeed self = pageState.thisPage(request);
  self.setCommand("UPLOAD");
  pageState.tokenCreate("imageUpl", self);
  Form form = new Form(self);
  form.encType = Form.MULTIPART_FORM_DATA;
  form.usePost = true;
  pageState.setForm(form);
  form.start(pageContext);

  String pfType = ApplicationState.getApplicationSetting("DEFAULT_STORAGE_TYPE");
  boolean isBlob = PersistentFile.TYPE_DB.equalsIgnoreCase(pfType);

  if(isBlob) {
    int oId = 0;
    if(JSP.ex(objectId)) {
      try {
        oId = Integer.parseInt(objectId);
      } catch (NumberFormatException e) {
        Tracer.platformLogger.error("imagesUploader: " + e);
      }
    }

    TextField.hiddenInstanceOfFormToHtml("BLOBX_ID", form, pageContext);
    TextField.hiddenInstanceOfFormToHtml("objectClass", form, pageContext);
    TextField.hiddenInstanceOfFormToHtml("objectId", form, pageContext);

  %><base target="_self"/>
    <table class="<%=Css.table%>" border="0" width="100%" cellpadding="4">
      <tr><td colspan="3" style="font-size:12px; background-color:<%=skin.COLOR_BACKGROUND_TITLE%>">Insert an absolute image path (for example http://example.com/images/diagram.jpg)...</td></tr>
       <tr><td width="1%" nowrap="nowrap"><%

          TextField imgPath = new TextField("<b>Image path</b>", "IMG_PATH", "</td><td width=\"20%\">", 50, false);
          imgPath.entryType = InputElement.EntryType.URL;
          imgPath.toHtml(pageContext);

      %></td><td width="*"><table width="100%" border="0"><tr>
        <td align="right"><span style="cursor:pointer"><%

          ButtonJS insert = new ButtonJS();
          insert.additionalCssClass="brown";
          insert.label = "<b>Insert</b>";
          insert.additionalOnClickScript = " var val=$('#IMG_PATH').val(); if(val && val!='http://') { ImagesUploaderDialog.insert( val, ''); } else { alert('Insert image path!') }";
          insert.toHtml(pageContext);

       %></span></td></tr></table></td></tr><%

        if(logged!=null)  {
        %><tr><td colspan="3">&nbsp;</td></tr>
          <tr><td colspan="3" style="font-size:12px; background-color:<%=skin.COLOR_BACKGROUND_TITLE%>">... or upload a new one</td></tr><%

          if(JSP.ex(mainObject)) {
          %><tr><td width="1%" nowrap="nowrap"><%

            Uploader imageFile = new Uploader("UPLOAD", pageState);
            imageFile.label = "<b>Upload</b>";
            imageFile.separator = "</td><td width=\"20%\">";
            imageFile.size = 40;
            imageFile.toHtml(pageContext);

            %> (max file size: 500Kb)</td><td width="*"><table width="100%" border="0"><tr>
              <td align="right"><span style="cursor:pointer"><%

            ButtonSubmit upload = ButtonSubmit.getSaveInstance(form, "Save");
            upload.additionalCssClass="brown";
            upload.label = "<b>Upload</b>";
            upload.toHtml(pageContext);

           %></span></td></tr></table></td></tr><%
            } else {
            %><%--<tr><td colspan="3">To upload blob images you have to initialize javascript variables <code>var objectClass</code> and <code>var objectId</code> in your calling page!</td></tr>--%><%
            Tracer.platformLogger.error("To upload blob images you have to initialize javascript variables var objectClass and var objectId in your calling page!");
          }
        }

       %><tr><td align="right" colspan="3"><%

      // close
      ButtonJS close = new ButtonJS();
      close.additionalCssClass="brown";
      close.onClickScript= " tinyMCEPopup.close(); ";
      close.label = "Close";
      %><span style="cursor:pointer;"><b><%
      close.toHtml(pageContext);
      %></b></span><%

       %></td></tr></table><%

      if(logged!=null)  {
     %><div style="height:250px; overflow:auto; width:680px; background-color:white">
      <table border="0" width=100% cellpadding="2" cellspacing="0">
        <tr><%

        // IMAGES LIST FILLING END
        List<PataBinaryLargeObject>imageList = siteImages(objectClass, oId);
        int counter = 0;
        int total = 0;
        int limit = 20;
        try {
          if (imageList!=null && imageList.size()>0) {
            for (PataBinaryLargeObject blob : imageList) {

              if (limit==total)
                break;

              PersistentFile image = new PersistentFile(Integer.parseInt(blob.getId().toString()), blob.getOriginalFileName());
              String fileName = image.getName();
              String ext = FileUtilities.getFileExt(fileName);

              if (FileUtilities.isImageByFileExt(ext)) {
                if (counter==2) {
                  %></tr><tr><%
                  counter=0;
                }

              %><td style="border-bottom: 1px solid #999999" ><%

                MultimediaFile mf = new MultimediaFile(image, request);
                mf.id = blob.getId()+"_img";
                mf.width = "100";
                mf.style = "cursor:pointer";
                %><span  onclick="ImagesUploaderDialog .insert(obj('<%=blob.getId()%>_img').src,' <%=image.getOriginalFileName()%>', false, false);"><%
                mf.toHtml(pageContext);

              %></span></td>
                <td style="border-bottom: 1px solid #999999"><div style=""><%
                  ButtonJS insertJS = new ButtonJS("ImagesUploaderDialog .insert(obj('"+blob.getId()+"_img').src,'" +image.getOriginalFileName()+"', false, false);");
                  insertJS.label = "<b>Insert</b>";
                  insertJS.additionalCssClass="orange";
                  insertJS.toHtml(pageContext);

                %><%--<div style="cursor:pointer;" onclick="ImagesUploaderDialog .insert(obj('<%=blob.getId()%>_img').src,' <%=image.getOriginalFileName()%>', false, false);"><b>Insert</b> <%=fileName%></div>--%><%

                ButtonSubmit del = new ButtonSubmit(form);
                del.additionalCssClass="bright";
                del.variationsFromForm.addClientEntry("BLOBX_ID", blob.getId());
                del.variationsFromForm.setCommand(Commands.DELETE);
                del.label = "Delete";
                del.toolTip = "Delete file: " + image.getName();
                del.confirmQuestion = "Do you want to permanently delete the image \'" + image.getName() + "\'?";
                del.confirmRequire = true;
                del.toHtml(pageContext);

                %></div></td><%
                counter++;
                total++;
              }
            }
          } else {
            %><tr><td>No images found</td></tr><%
          }
        } catch(Exception a) {
          Tracer.platformLogger.error("TinyMCE imagesUploader: images are too heavy!! " + a);
          %><td colspan="4"><%="<b>TinyMCE imagesUploader: images are too heavy!!</b><br>" + a%></td></tr><%
        }

       %></table></div><%
      }
      form.end(pageContext);

  //  }

  } else {
    %><div>Sorry: this plugin is implemented for blob uploading only!</div><%
  }

%></body></html>