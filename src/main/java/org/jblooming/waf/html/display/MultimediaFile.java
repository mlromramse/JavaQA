package org.jblooming.waf.html.display;

import org.jblooming.ontology.PersistentFile;
import org.jblooming.utilities.StringUtilities;
import org.jblooming.waf.html.core.JspHelper;
import org.jblooming.waf.view.PageSeed;
import org.jblooming.waf.constants.Fields;
import org.jblooming.waf.settings.ApplicationState;
import org.jblooming.system.SystemConstants;

import javax.servlet.http.HttpServletRequest;
import java.io.File;

/**
 * MultimediaFile (c) 2005 - Open Lab - www.open-lab.com
 */
public class MultimediaFile extends JspHelper {

  public String fileUrl;

  public String areaName = "";
  public String label;
  public boolean viewable = true; // if not it means the obj is 'downloadable' (i.e. treat as attach)

  public String border;
  public String width;
  public String height;
  public String align = "";
  public String script = "";
  public String style = "";
  public String movieShowControl = "1";
  public String movieShowDisplay = "0";
  public String movieShowStatusBar = "0";
  public String movieWidth = "320";
  public String movieHeight = "282";
  public String flashParams = "";
  public String loop = "0";
  public boolean showAsList = false;
  public PersistentFile pf;
  public boolean inhibitToolTip = true;
  public String alternativeImage = "";
  // it can be modified to customize flash params
  public String flashEmbedderUrl = "/commons/js/embedFlash.js";

  // for quicktime, if a number is the scaling factor
  // use "tofit" to make the video fit a rectangle without keeping the aspect ratio
  // use "aspect" to make the video fit as much of a rectangle as possible while keeping the aspect ratio
  public String scale = "1";
  public String autoplay = "true";

  public MultimediaFile (PersistentFile pf, HttpServletRequest request) {
    this(pf, null, null, null, null, request);
  }

  public MultimediaFile (PersistentFile pf, String width, String height, String align, String loop, HttpServletRequest request) {
    this.width = width;
    this.height = height;
    this.align = align;
    this.loop = loop;
    if (PersistentFile.TYPE_WEBAPP_FILESTORAGE.equals(pf.getType())) {
      this.fileUrl = request.getContextPath()+  (request.getContextPath().endsWith("/") || pf.getFileLocation().startsWith("/") ? "" : "/" ) + StringUtilities.replaceAllNoRegex(pf.getFileLocation(),"\\","/");
    } else if(PersistentFile.TYPE_FILESTORAGE.equals(pf.getType())) {
      this.fileUrl = ApplicationState.getApplicationSetting(SystemConstants.FLD_REPOSITORY_URL)+ File.separator+pf.getFileLocation();
      // TYPE_DB
    } else {
      this.fileUrl = getPersistentFileUrl(pf);
    }
    this.pf = pf;
    this.urlToInclude = "/commons/layout/partMultimediaFile.jsp";
  }

  public static String getPersistentFileUrl(PersistentFile pf) {
    if(PersistentFile.TYPE_DB.equals(pf.getType()) || PersistentFile.TYPE_FILESTORAGE.equals(pf.getType()) || PersistentFile.TYPE_FILESTORAGE_ABSOLUTE.equals(pf.getType()) ) {
      String uplUID = pf.serialize();
      PageSeed ps = new PageSeed(ApplicationState.contextPath + "/commons/layout/partUploaderView.jsp");
      ps.addClientEntry(Fields.FILE_TO_UPLOAD, uplUID);
      return ps.toLinkToHref();
      
    } else { // PersistentFile.TYPE_WEBAPP_FILESTORAGE
      return ApplicationState.contextPath + "/" + StringUtilities.replaceAllNoRegex(pf.getFileLocation(), "\\", "/");
    }
  }

  public MultimediaFile (String url) {
    this.fileUrl = url;
  }

}