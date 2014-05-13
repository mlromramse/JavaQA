<html><head>
  <script type="text/javascript" src="<%=request.getContextPath()+"/commons/js/jquery/jquery-1.2.6.js"%>"></script>
  <script type="text/javascript" src="<%=request.getContextPath()+"/commons/layout/tinymce/tiny_mce_popup.js"%>"></script>
  <script type="text/javascript" src="<%=request.getContextPath()+"/applications/webwork/js/webwork.js"%>"></script>
  <script type="text/javascript" src="<%=request.getContextPath()+"/commons/js/showThumb.js"%>"></script>
  <script type="text/javascript" src="<%=request.getContextPath()%>/commons/layout/tinymce/plugins/imagesManager/js/imagesManager.js"></script>
  <%
  // DO NOT MOVE FROM HERE (security exception in IE)

  PageState pageState = PageState.getCurrentPageState();
  SessionState sessionState = pageState.sessionState;
  SiteIdentifier site = (SiteIdentifier) sessionState.getAttribute("WW4_SITE");
  
  String siteId = JSP.ex(site) ? site.getId()+"" : null;
  Operator logged = pageState.getLoggedOperator();
  Skin skin = sessionState.getSkin();
   if (skin == null) {
    sessionState.setSkinForApplicationIfNull(request.getContextPath(), ApplicationState.getApplicationSetting(OperatorConstants.FLD_CURRENT_SKIN), "applications/blooming");
    skin = sessionState.getSkin();
  }
  %>
  <script type="text/javascript">
      var serverURL = "<%=ApplicationState.serverURL%>";
      var contextPath = "<%=ApplicationState.contextPath%>";
      var inhibitTooltips = "true"; <%//=Fields.TRUE.equals(sessionState.getOperatorOption("INHIBIT_TOOLTIPS")) ? "true" : "false"%>;
      var skinImgPath = "<%=pageState!=null ? pageState.getSkin().imgPath : "images"%>";
  </script>
  <link rel=stylesheet href="<%=skin.css%>platformCss.jsp" type="text/css" media="screen">
	<base target="_self"/>
	</head>
<%@ page import="com.opnlb.website.identifier.SiteIdentifier,
                com.opnlb.website.identifier.SiteIdentifierBricks,
                com.opnlb.website.util.WebsiteUtilities,
                com.opnlb.webwork.webutilities.WebResource,
                org.jblooming.ApplicationException,
                org.jblooming.ontology.BinaryLargeObject,
                org.jblooming.ontology.PersistentFile,
                org.jblooming.operator.Operator,
                org.jblooming.oql.OqlQuery,
                org.jblooming.persistence.PersistenceHome,
                org.jblooming.persistence.exceptions.PersistenceException,
                org.jblooming.persistence.hibernate.PersistenceContext,
                org.jblooming.system.SystemConstants,
                org.jblooming.tracer.Tracer,
                org.jblooming.utilities.HttpUtilities,
                org.jblooming.utilities.JSP,
                org.jblooming.utilities.file.FileUtilities,
                org.jblooming.waf.ImagesRelocatorFilter,
                org.jblooming.waf.SessionState,
                org.jblooming.waf.UploadHelper,
                org.jblooming.waf.constants.Commands,
                org.jblooming.waf.constants.Fields,
                org.jblooming.waf.constants.OperatorConstants,
                org.jblooming.waf.exceptions.ActionException,
                org.jblooming.waf.html.button.ButtonJS,
                org.jblooming.waf.html.button.ButtonSubmit,
                org.jblooming.waf.html.container.ButtonBar,
                org.jblooming.waf.html.display.MultimediaFile,
                org.jblooming.waf.html.input.CheckField,
                org.jblooming.waf.html.input.TextField,
                org.jblooming.waf.html.input.Uploader,
                org.jblooming.waf.html.layout.Css,
                org.jblooming.waf.html.layout.Skin,
                org.jblooming.waf.html.state.Form,
                org.jblooming.waf.settings.ApplicationState,
                org.jblooming.waf.view.ClientEntry,
                org.jblooming.waf.view.PageSeed,
                org.jblooming.waf.view.PageState,
                java.io.File,
                java.util.Collections, java.util.Comparator, java.util.Date, java.util.List"%><%@ page pageEncoding="UTF-8" %><%!

  private void uploadImage(WebResource res, SiteIdentifier site, String defaultPFtype, PageState pageState, HttpServletRequest request) throws ActionException, PersistenceException, ApplicationException {
    String newRepositoryPath = ImagesRelocatorFilter.newPhysicalPath;
    String oldWebappPath = ImagesRelocatorFilter.oldVirtualPath;
    boolean externalRepositoryFolder = JSP.ex(newRepositoryPath) && JSP.ex(oldWebappPath);
    String relPath = "";
    String path = request.getParameter("PATH");
    if (!JSP.ex(path) || "null".equalsIgnoreCase(path)) {
      String id = SiteIdentifierBricks.getApplicationSettingAccordingToSite(site, "INSTALLATION_ID");
      relPath = "applications/webwork/site_" + id + "/local/document";     // applications/webwork/site_medicina/local/document
    } else
      relPath = path + "/document";

    // as it was
    //relPath = relPath + (externalRepositoryFolder ? "/" + ImagesRelocatorFilter.targetFolder : "");
    relPath = relPath + WebsiteUtilities.manageFileSeparator(relPath, ImagesRelocatorFilter.targetFolder) + ImagesRelocatorFilter.targetFolder;

    File root = null;
    // repository esterno alla webapp
    if (externalRepositoryFolder) {
      root = new File(newRepositoryPath);
      if (!root.exists())
        root.mkdir();

      // repository interno alla webapp
    } else {
      root = new File(HttpUtilities.getFileSystemRootPathForRequest(request) + File.separator + relPath);    // C:\\develop\\java\\webwork4\\html\\applications\\webwork\\site_medicina\\local\\document
      if (!root.exists())
        root.mkdir();
    }

    // da WebsitePageAction upload image
    ClientEntry entry = pageState.getEntry("UPLOAD");
    String value = entry.stringValue();
    PersistentFile pf = res.getFile();

    if (pf == null) {
      String folderPath = null;
      if (PersistentFile.TYPE_WEBAPP_FILESTORAGE.equals(defaultPFtype)) {
        pf = new PersistentFile(0, null, PersistentFile.TYPE_WEBAPP_FILESTORAGE);
        pf.fileDir = relPath;      //applications/webwork/site_medicina/local/document\\uploaded_img
        folderPath = HttpUtilities.getFileSystemRootPathForRequest(request) + relPath;
        File folder = new File(folderPath);
        if (!folder.exists())
          folder.mkdir();

      } else if (PersistentFile.TYPE_FILESTORAGE.equals(defaultPFtype)) {
        pf = new PersistentFile(0, null, PersistentFile.TYPE_FILESTORAGE);
        String repository = SiteIdentifierBricks.getApplicationSettingAccordingToSite(site, SystemConstants.FLD_REPOSITORY_URL);
        pf.fileDir = relPath;
        File folder = new File(repository + (repository.endsWith(File.separator) ? relPath : pf.fileDir));
        if (!folder.exists())
          folder.mkdir();
        // nuova gestione end

      } else
        folderPath = pf.getFileLocation();
    }

    if (value != null && value.trim().length() <= 0)
      value = null;
    if (JSP.ex(value)) {
      String ext = FileUtilities.getFileExt(value);
      // verifica se businessLogic valida deve essere immagine
      if (FileUtilities.isImageByFileExt(ext)) {
        res.setFile(Uploader.save(res, pf, "UPLOAD", pageState));

      } else {
        ClientEntry ceNull = new ClientEntry("UPLOAD", null);
        ceNull.errorCode = I18n.get("FILE_MUST_BE_IMAGE");
        pageState.addClientEntry(ceNull);
      }
      // no entry to delete pf
    }
  }

  Comparator byDate = new Comparator() {
    long getLong(Object o) {
      try {
        WebResource fp = (WebResource) o;
        return fp.getLastModified().getTime();
      } catch (Throwable t) {
        return 0;
      }
    }

    public int compare(Object o1, Object o2) {
      long res = getLong(o2) - getLong(o1);
      if (res < 0L)
        return -1;
      else if (res > 0L)
        return 1;
      else
        return 0;
    }
  };

%><%

  PageSeed self = pageState.thisPage(request);
  self.setCommand("UPLOAD");
  Form form = new Form(self);
  form.encType = Form.MULTIPART_FORM_DATA;
  form.usePost = true;
  pageState.setForm(form);
  form.start(pageContext);

  String newRepositoryPath = ImagesRelocatorFilter.newPhysicalPath;
  String oldWebappPath = ImagesRelocatorFilter.oldVirtualPath;
  boolean externalRepositoryFolder = JSP.ex(newRepositoryPath) && JSP.ex(oldWebappPath);

  String defaultPFtype = SiteIdentifierBricks.getApplicationSettingAccordingToSite(site, SystemConstants.DEFAULT_STORAGE_TYPE);
  if (!JSP.ex(defaultPFtype))
    defaultPFtype = PersistentFile.TYPE_WEBAPP_FILESTORAGE;
  boolean isBlob = PersistentFile.TYPE_DB.equals(defaultPFtype);

  String relPath = "";
  String path = request.getParameter("PATH");

  if (!JSP.ex(path) || "null".equalsIgnoreCase(path)) {
    String id = SiteIdentifierBricks.getApplicationSettingAccordingToSite(site, "INSTALLATION_ID");
    relPath = "applications/webwork/site_" + id + "/local/document";
  } else {
    relPath = path + "/document";
  }

  relPath = relPath + (externalRepositoryFolder ? "/" + ImagesRelocatorFilter.targetFolder : "");

  TextField.hiddenInstanceOfFormToHtml("WEBRES_ID", form, pageContext);
  TextField.hiddenInstanceOfFormToHtml("SEARCH_NULL", form, pageContext);
  TextField.hiddenInstanceOfFormToHtml("imageSearchField", form, pageContext);
  TextField.hiddenInstanceOfFormToHtml("objectClass", form, pageContext);

  String domain = (String) SiteIdentifierBricks.getApplicationParameterAccordingToSite(site, "SITE_URL");
  if (!JSP.ex(domain) && JSP.ex(site))
    domain = site.getDomainUrl();

  int maxSize = 0;
  try {
    maxSize = new Integer(SiteIdentifierBricks.getApplicationSettingAccordingToSite(site, "IMG_MAX_UPLOADING_SIZE"));
  } catch (NumberFormatException a) {
    Tracer.addTrace("IMG_MAX_UPLOADING_SIZE: " + a);
  }

  File root = null;
  String absPath = null;
  // repository esterno alla webapp
  if (externalRepositoryFolder) {
    root = new File(newRepositoryPath);
    if (!root.exists()) {
      out.write("Your image directory has not been configured correctly:: " + root.getAbsolutePath() + " doesn't exists</b>");
      return;
    }
    absPath = root.getPath();

    // repository interno alla webapp
  } else {
    root = new File(HttpUtilities.getFileSystemRootPathForRequest(request) + File.separator + relPath);
    if (!root.exists()) {
      out.write("Your image directory has not been configured correctly:: " + root.getAbsolutePath() + " doesn't exists</b>");
      return;
    }
    absPath = root.getPath() + File.separator + ImagesRelocatorFilter.targetFolder;
  }

  String searchField = pageState.getEntry("SEARCH").stringValueNullIfEmpty();
  boolean resetSearch = Fields.TRUE.equals(pageState.getEntry("SEARCH_NULL").stringValueNullIfEmpty());
  if (resetSearch)
    pageState.addClientEntry("SEARCH", "");
  String uploading = pageState.getEntry("UPLOAD").stringValueNullIfEmpty();
  String resId = pageState.getEntry("WEBRES_ID").stringValueNullIfEmpty();
  boolean showAll = pageState.getEntry("SHOW_ALL").checkFieldValue();

  /**
   * UPLOAD PART
   */
  if (Commands.SAVE.equals(pageState.getCommand()) && uploading != null) {
    String ext = FileUtilities.getFileExt(uploading);
    boolean validFileType = FileUtilities.isImageByFileExt(ext);

    if (!isBlob) {
      // directory making
      File targetFolder = new File(absPath);
      if (!targetFolder.exists())
        targetFolder.mkdir();
    }

    if (!validFileType) {
      ClientEntry ceNull = new ClientEntry("UPLOAD", null);
      ceNull.errorCode = I18n.get("NOT_ALLOWED_FILE_EXT");
      pageState.addClientEntry(ceNull);

    } else {
      boolean validSize = UploadHelper.fileSizeAllowed("UPLOAD", maxSize, pageState);
      if (!validSize) {
        ClientEntry ceNull = new ClientEntry("UPLOAD", null);
        ceNull.errorCode = I18n.get("SIZE_EXCEED") + ": " + FileUtilities.convertFileSize(maxSize);
        pageState.addClientEntry(ceNull);

      } else {
        UploadHelper auh = UploadHelper.getInstance("UPLOAD", pageState);
        String fileName = auh.originalFileName;
        WebResource res = new WebResource();
        res.setIdAsNew();
        res.setDescription(fileName);
        res.setSiteId(site);
        res.setCreationDate(new Date());
        res.setCreator(logged.getDisplayName());
        res.setLastModified(new Date());
        res.setLastModifier(logged.getDisplayName());
        if (!isBlob)
          uploadImage(res, site, defaultPFtype, pageState, request);

        res.store();

        // BLOB
        if (isBlob) {
          /**
           * BLOB must be stored after mainObj stored
           */
          PersistentFile pf = new PersistentFile(0, null, PersistentFile.TYPE_DB);
          res.setFile(Uploader.save(res, pf, "UPLOAD", pageState));
          res.store();
        }
        ClientEntry ceNull = new ClientEntry("UPLOAD", null);
        pageState.addClientEntry(ceNull);
        /*
        // FILE SYSTEM
        else {
          /*
          // previous file verifier
          File previousFile = new File(absPath + File.separator + fileName);
          if (!previousFile.exists() || (previousFile.exists() && replaceExist)) {
            if (JSP.ex(res)) {
              auh.saveInFolder("UPLOAD", 0, null, absPath, null, null, false, pageState);
              ClientEntry ceNull = new ClientEntry("UPLOAD", null);
              pageState.addClientEntry(ceNull);
            }
          } else if (previousFile.exists() && !replaceExist) {
            ClientEntry ceNull = new ClientEntry("UPLOAD", null);
            ceNull.errorCode = I18n.get("EXISTING_IMAGE");
            pageState.addClientEntry(ceNull);
          }
        }
        */
      }
    }

    /**
     * DELETE PART
     */
  } else if (Commands.DELETE.equals(pageState.getCommand()) && JSP.ex(resId)) {
    /*
  // FILE SYSTEM (inner or outer the web-app)
    if(JSP.ex(delendumFile)) {
      String fsRootPath = externalRepositoryFolder ? newRepositoryPath : HttpUtilities.getFileSystemRootPathForRequest(request);
      delendumFile = StringUtilities.replaceAllNoRegex(delendumFile, "/", File.separator);
      // ts=currentimeMillisecond must be removed
      if (delendumFile.indexOf("?") > -1)
        delendumFile = delendumFile.substring(0, delendumFile.lastIndexOf("?"));
      File del = null;
      if (externalRepositoryFolder) {
        delendumFile = delendumFile.substring(delendumFile.lastIndexOf("\\"), delendumFile.length());
        del = new File(fsRootPath + delendumFile);
      } else {
        String filePath = fsRootPath + delendumFile;
        if (JSP.ex(request.getContextPath())) {
          fsRootPath = fsRootPath.substring(0, fsRootPath.length() - (request.getContextPath().length() + 1));
          filePath = fsRootPath + delendumFile;
        }
        del = new File(filePath);
      }
      if (del.exists()) {
        FileUtilities.tryHardToDeleteDir(del);
      }
    }
    */
    WebResource delenda = (WebResource) PersistenceHome.findByPrimaryKey(WebResource.class, resId);
    PersistentFile file = delenda.getFile();

    if (JSP.ex(file)) {
      if (isBlob) {
        // binary obj must be manually removed
        BinaryLargeObject bo = file.getBlob(PersistenceContext.getDefaultPersistenceContext());
        if (bo != null)
          bo.remove();

      } else {
        path = "";
        if (PersistentFile.TYPE_WEBAPP_FILESTORAGE.equals(file.getType()))
          path = ApplicationState.webAppFileSystemRootPath + WebsiteUtilities.manageFileSeparator(file.getFileLocation()) + file.getFileLocation();
        else if (PersistentFile.TYPE_FILESTORAGE.equals(file.getType())) {
          String fileStorage = SiteIdentifierBricks.getApplicationSettingAccordingToSite(site, SystemConstants.FLD_REPOSITORY_URL);
          path = fileStorage + file.getFileLocation();
        } else
          path = file.getFileLocation();

        File delendo = new File(path);
        FileUtilities.tryHardToDeleteFile(delendo);
      }
    }
    delenda.remove();
  }

%>
  <body>
  <table class="<%=Css.table%>" bgcolor="<%=skin.COLOR_BACKGROUND_TITLE%>" border="0" width="100%">
    <tr><td><%=I18n.get("CHOOSE_IMAGE")%></td></tr>
  </table>
   <table width="100%" class="<%=Css.table%>" border="0">
     <tr><td width="1%" nowrap="nowrap"><%

        Uploader imageFile = new Uploader("UPLOAD", form, pageState);
        imageFile.label = "<b>"+I18n.get("UPLOAD")+"</b>";
        imageFile.separator = "</td><td width=\"20%\">";
        imageFile.size = 30;
        imageFile.toHtml(pageContext);

    %></td>
      <td width="*"><table width="100%" border="0"><tr><td><%--
        // upload via file system can't have images with the same name BUT they are prefixed by pf UID ==> none can have the same name
        if(!isBlob) {
          CheckField replace = new CheckField(I18n.get("REPLACE_EXISTING"), "REPLACE", "&nbsp;", false);
          replace.toHtml(pageContext);
          %><%="<br>"%><%
        }
        --%><%="<small>" + I18n.get("UPLOAD_MAX_SIZE") + ":<b> " + FileUtilities.convertFileSize(maxSize) + "</b></small>"%></td><td align="right"><%

        ButtonSubmit upload = ButtonSubmit.getSaveInstance(form, I18n.get("SAVE"));
        upload.label = "<b>"+I18n.get("UPLOAD")+"</b>";
        upload.toHtml(pageContext);

     %></td></tr></table></td></tr>
     <tr><td width="1%" nowrap="nowrap"><%

        TextField search = new TextField("TEXT", "SEARCH","</td><td>",25,false);
        search.label = "<b>"+I18n.get("SEARCH")+"</b>";
        search.toHtml(pageContext);

    %></td>
     <td width="*"><table width="100%" border="0"><tr><td><%

       CheckField show = new CheckField(I18n.get("SHOW_ALL"), "SHOW_ALL", "&nbsp;", false);
       show.toHtml(pageContext);

        %></td><td align="right"><%

      ButtonSubmit searchButton = ButtonSubmit.getSearchInstance(form, pageState);
      searchButton.toHtml(pageContext);

     %></td></tr></table></td>
   </tr></table><%

    // BUTTON BAR
    ButtonBar bbar=new ButtonBar();

    // close
    ButtonJS close = new ButtonJS();
    close.onClickScript= " tinyMCEPopup.close(); ";
    close.label = I18n.get("CLOSE");
    bbar.addButton( close );

    // reset
//    self.setCommand(Commands.FIND);
//    self.addClientEntry("PATH", path);
//    ButtonLink reset = new ButtonLink(I18n.get("RESET"), self);
    ButtonSubmit reset = new ButtonSubmit(form);
    reset.variationsFromForm.setCommand(Commands.FIND);
    reset.variationsFromForm.addClientEntry("SEARCH_NULL", Fields.TRUE);
    reset.label = I18n.get("RESET");
    bbar.addButton( reset );

    bbar.toHtml(pageContext);

   %><div style="height:300px; overflow:auto; width:680px;">
    <table border="0" width=100% cellpadding="2" cellspacing="0"><%

      String feedBack = pageState.getEntry("IMG_USED").stringValueNullIfEmpty();
      if (feedBack!=null) {
        %><tr><td colspan="4"><font color="red"><b><%=feedBack%></b></font></td></tr><%
      }

      /**
       * SEARCH PART
       */
      // IMAGES LIST FILLING START
//    List<FileAndRelativePath> imageList = new ArrayList<FileAndRelativePath>();

      OqlQuery oql = new OqlQuery(" from " + WebResource.class.getName() + " as res where res.siteId=:stix " + (JSP.ex(searchField) && !resetSearch ? " and res.description like :serx " : ""));
      oql.getQuery().setString("stix", siteId);
      if (JSP.ex(searchField) && !resetSearch)
        oql.getQuery().setString("serx", "%" + searchField + "%");
      List<WebResource> imageList = oql.list();
      int resultsSize = imageList.size();

      Collections.sort(imageList, byDate);
      int counter = 0;
      int total = 0;
      int limit = 20;
      String limitFeedback = showAll ? resultsSize+"" : (resultsSize<limit ? resultsSize : limit)+"";

      try {
        if (JSP.ex(imageList)) {
        %><tr><td align="center" valign="middle" colspan="4" style="border-bottom:1px solid #FFC36B;">
          <%="<br>&nbsp;<b>" +I18n.get("DISPLAYED_IMAGES")+ ": " + limitFeedback + " " + I18n.get("OF") + " " + resultsSize +"</b>"%></td></tr><%

          for (int i = 0; i < imageList.size(); i++) {
            if (!showAll && searchField == null && limit == total)
              break;
            
            WebResource webRes = imageList.get(i);
            PersistentFile image = webRes.getFile();
//            String url = fp.getUrl() + "?ts=" + System.currentTimeMillis();
//            String insertPath = fp.getInsertPath();
            String fileName = image.getOriginalFileName();
            String ext = FileUtilities.getFileExt(fileName);

            if (FileUtilities.isImageByFileExt(ext)) {
              if (counter == 2) {
                %></tr><tr><%
                counter=0;
              }

            %><td style="border-bottom: 1px solid #999999"><%

              MultimediaFile mf = new MultimediaFile(image, request);
              mf.id = webRes.getId()+"_img";
              mf.width = "100";
              mf.script = WebsiteUtilities.showPreviewOnClick(site, image, webRes.getDescription(), 0, true);
              mf.style = " cursor:pointer; ";
              mf.toHtml(pageContext);

              %></td>
              <td style="border-bottom: 1px solid #999999"><b>
                <div style="cursor:pointer;" onclick="ImagesManagerDialog.insert(obj('<%=webRes.getId()%>_img').src,'<%=JSP.javascriptEncode(image.getOriginalFileName())%>', false, false)">insert <%=fileName%></div></b><%

              ButtonSubmit del = new ButtonSubmit(form);
              del.variationsFromForm.addClientEntry("WEBRES_ID", webRes.getId());
              //del.variationsFromForm.addClientEntry("PATH", path);
              del.variationsFromForm.setCommand(Commands.DELETE);
              del.label = I18n.get("DELETE");
              del.toolTip = I18n.get("DELETE") + " file: " + image.getName();
              del.confirmQuestion = I18n.get("DELETE_PERMANENTLY_FILE") + " \'" + image.getName() + "\'?";
              del.confirmRequire = true;

              // ???? forse per pf FR ????
              //if (url.toLowerCase().indexOf(ImagesRelocatorFilter.targetFolder.toLowerCase())>-1) {
                %><br><div><b><%
                del.toHtmlInTextOnlyModality(pageContext);
                %></b></div><%
              //} else {
               %><%-- %>&nbsp;<% --%><%
              //}

              %></td><%
              counter++;
              total++;
            }
          }

        } else {
          %><tr><td align="center"><%="<br>&nbsp;<b>" +I18n.get("NO_IMAGES_FOUND")+"</b>"%></td></tr><%
        }
      } catch(Throwable a) {
        Tracer.platformLogger.error("TinyMCE imagesManager: an error occured!! " + a);
        %><td colspan="4"><%="<b>TinyMCE imagesManager: an error occured!!</b><br>" + a%></td></tr><%
      }

     %></table></div><%

    form.end(pageContext);

%></body></html>