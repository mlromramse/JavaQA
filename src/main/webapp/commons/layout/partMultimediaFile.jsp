<%@ page import ="org.jblooming.ontology.PersistentFile,
                  org.jblooming.tracer.Tracer,
                  org.jblooming.utilities.HttpUtilities,
                  org.jblooming.utilities.JSP,
                  org.jblooming.utilities.StringUtilities,
                  org.jblooming.utilities.file.FileUtilities,
                  org.jblooming.waf.SessionState,
                  org.jblooming.waf.constants.Commands,
                  org.jblooming.waf.constants.Fields,
                  org.jblooming.waf.html.core.JspIncluderSupport,
                  org.jblooming.waf.html.display.Img,
                  org.jblooming.waf.html.display.MultimediaFile,
                  org.jblooming.waf.settings.ApplicationState,
                  org.jblooming.waf.view.PageSeed,
                  org.jblooming.waf.view.PageState, java.io.File, java.util.Map"%><%@page pageEncoding="UTF-8"%><%

  /**
   * W3C COMPLIANT!!!!!!!!!!!!!!
   */
  PageState pageState = PageState.getCurrentPageState();
  SessionState sessionState = pageState.sessionState;
  MultimediaFile mmFile = (MultimediaFile) JspIncluderSupport.getCurrentInstance(request);

  PersistentFile psFile = mmFile.pf;
  String type = psFile.getType();
  String url = StringUtilities.replaceAllNoRegex(mmFile.fileUrl, "\\\\", "\\");

  boolean viewable = mmFile.viewable;
  boolean pdfMod = Fields.TRUE.equals(pageState.getAttribute("PDF_MODALITY"));

  if (psFile != null) {
    /**
     * MultimediFile has a new property:: viewable (default value=true)
     * if setted false, when mmFile is instanced, the related object is treat as attach independently from file extension
     */
    if (!viewable) {
      // no matter file extension
      // todo se file video allora sostituire il file .avi
      String uplUID = psFile.serialize();
      PageSeed myself = new PageSeed("/commons/layout/partUploaderView.jsp");
      myself.setCommand(Commands.FILE_VIEW);
      myself.addClientEntry(Fields.FILE_TO_UPLOAD, uplUID);
      //if(sessionState.isFirefox())
      myself.addClientEntry("TREATASATTACH", Fields.TRUE);
      String label = psFile.getOriginalFileName();
      if (JSP.ex(mmFile.label))
        label = mmFile.label;

%><span><a href="<%=(pdfMod ? ApplicationState.serverURL : request.getContextPath() ) +myself.toLinkToHref()%>" <%=mmFile.generateToolTip()%>><%=label%></a></span><%

    /**
     * default behaviour
     */
    } else {
      String fileExt = FileUtilities.getFileExt(psFile.getOriginalFileName());
      boolean isDoc = FileUtilities.isDocByFileExt( fileExt ) || FileUtilities.isMsApplicationExtension(fileExt);
      boolean isImage = FileUtilities.isImageByFileExt( fileExt );
      boolean isApplet = FileUtilities.isAppletByFileExt(fileExt);
      boolean isJsp = FileUtilities.isJspByFileExt(fileExt);
      boolean isZip = FileUtilities.isArchiveByFileExt(fileExt);
    //      boolean isAudio = FileUtilities.isAudiotByFileExt(fileExt);
      boolean isFlash = FileUtilities.isFlashByFileExt( fileExt );
      boolean isQuick = FileUtilities.isQuickByFileExt( fileExt );
      boolean isMsMovie = FileUtilities.isWindowsMovieByFileExt(fileExt);
      boolean isVideo = isFlash || isQuick || isMsMovie;
      String width = mmFile.width;
      String height = mmFile.height;
      if(!JSP.ex(width) && isVideo)
        width = "480";
      if(!JSP.ex(height) && isVideo)
        height = "280";

      // stream of flash/movies
      if (PersistentFile.TYPE_FILESTORAGE.equals(type) || PersistentFile.TYPE_FILESTORAGE_ABSOLUTE.equals(type)) {
        String moviePath = psFile.serialize();
        moviePath = StringUtilities.replaceAllNoRegex(moviePath, "_", "%5f");
        moviePath = StringUtilities.replaceAllNoRegex(moviePath, ".", "%2e");
        moviePath = StringUtilities.replaceAllNoRegex(moviePath, "+", "%2b");
        moviePath = StringUtilities.replaceAllNoRegex(moviePath, "/", "%2f");
        url = request.getContextPath() + "/commons/layout/partUploaderView.jsp?FILE_TO_UPLOAD="+moviePath;
        url = StringUtilities.replaceAllNoRegex(url, "\\", "/");
      }

      /**
       * DOC
       */
      if (isDoc || isZip) {
        String uplUID = psFile.serialize();
        PageSeed myself = new PageSeed("/commons/layout/partUploaderView.jsp");
        myself.setCommand(Commands.FILE_VIEW);
        myself.addClientEntry(Fields.FILE_TO_UPLOAD, uplUID);
        // if(sessionState.isFirefox()) ?????????????????? why this?
        myself.addClientEntry("TREATASATTACH", Fields.TRUE);
        String label =psFile.getOriginalFileName();
        if (mmFile.label!=null)
          label = mmFile.label;
        %><span><a href="<%= (pdfMod ? ApplicationState.serverURL : request.getContextPath() ) +myself.toLinkToHref()%>" <%=mmFile.generateToolTip()%>><%=label%></a></span><%

        /**
         * JSP (portlet)
         */
      } else if (isJsp) {
        File jspFile = new File(HttpUtilities.getFileSystemRootPathForRequest(request)+ File.separator+psFile.getFileLocation());
        String path = "/" + psFile.getFileLocation();
        if (jspFile.exists()) {
          // DEVELOPMENT :: we like crashes!....
          if (ApplicationState.platformConfiguration.development) {
            %><jsp:include page="<%=path%>"><jsp:param name="AREANAME" value="<%=mmFile.areaName%>"/></jsp:include><%

          // ... but users do not! PRODUCTION
          } else {
            try {
              %><jsp:include page="<%=path%>"><jsp:param name="AREANAME" value="<%=mmFile.areaName%>"/></jsp:include><%
            } catch (Throwable a) {
              %><%="<small>" + I18n.get("NO_PREVIEW_AVAILABLE") + " ::<br>" + path + "<br><br>" + a + "</small><br>"%><%
              Tracer.platformLogger.error("WEBSITE_ERROR", a);
            }
          }
        }

      /**
       * IMAGE
       */
      } else if (isImage) {
        Img image = new Img(psFile, "");
        if(JSP.ex(mmFile.id))
          image.id = mmFile.id;
        image.width = mmFile.width;
        image.height = mmFile.height;
        image.style = mmFile.style;
        image.toolTip = JSP.w(mmFile.toolTip);
        image.script = mmFile.script;
        if (pdfMod)
          image.imageUrl = ApplicationState.serverURL + "/" + StringUtilities.replaceAllNoRegex(psFile.getFileLocation(), "\\", "/") + "?ts=" + System.currentTimeMillis();
        image.toHtml(pageContext);

        /**
         * applet case
         */
      } else if(isApplet) {
        %>TODO: work in progress<%

        /**
         * EVERYTHING ELSE
         */
      } else {
        String pathFile = "/" + psFile.getFileLocation();
        pathFile = StringUtilities.replaceAllNoRegex(pathFile, "\\", "/");
        String flvThumb = FileUtilities.getNameWithoutExt(psFile.getOriginalFileName()) + ".jpg";
        String flvPath = "";
        if (JSP.ex(psFile.fileDir)) {
          flvPath = psFile.fileDir + (psFile.fileDir.endsWith(File.separator) || flvThumb.startsWith(File.separator) ? "" : "/") + flvThumb;
          flvPath = StringUtilities.replaceAllNoRegex(flvPath, "\\", "/");
          flvPath = StringUtilities.replaceAllNoRegex(flvPath, "\\\\", "/");
        }
        if (PersistentFile.TYPE_DB.equals(type)) {
          pathFile = url;
          flvPath = request.getContextPath() + mmFile.alternativeImage;
        }

        url = url + (JSP.ex(mmFile.flashParams) ? "?" + mmFile.flashParams : "");

        %><script type="text/javascript">
            $(function(){
              initialize(contextPath+"/commons/js/jquery/jquery.media.js" , true );
            $.fn.media.defaults.flvPlayer = contextPath+'/commons/layout/multimedia/flv/mediaplayer.swf';
            $.fn.media.defaults.mp3Player = contextPath +'/commons/layout/multimedia/singlemp3player.swf';<%

            String path =  request.getContextPath() + (flvPath.startsWith("/") || request.getContextPath().endsWith("/") ? "" : "/" ) + flvPath;
            if (sessionState.isWindows()) {
            %>
            $.fn.media.mapFormat( 'mpg', 'winmedia');
              $.fn.media.mapFormat( 'mpeg', 'winmedia');
              <%
             }

            if(!mmFile.showAsList) {
            %>$('#pf_<%=psFile.getUID()%>').media( {
                id: '<%=mmFile.id%>',
                width: '<%=width%>',
                height: '<%=height%>'
                ,flashvars:{image:'<%=path%>'}
                , allowfullscreen:'true' 
                <%
              Map params = mmFile.parameters;
              for (Object o1 : params.keySet()) {
                String key = (String) o1;
                Object val = (Object) params.get(key);
                %>,
                <%=key + ": " + val%><%
              }
              %>}
              );<%
            }
            %>});
          </script><a id="pf_<%=psFile.getUID()%>" href="<%=url%>" <%
            if(mmFile.showAsList) {
              %> onclick="$('#pf_<%=psFile.getUID()%>').media( { width: <%=width%>, height: <%=height%>, autoplay:<%=mmFile.autoplay%> } ); return false;"<%
            }

          %>></a><%
      }
    }

  } else {
    %><%=I18n.get("FILE_NULL")%><%
  }

%>