package org.jblooming.waf.html.display;

import org.jblooming.ontology.PersistentFile;
import org.jblooming.system.SystemConstants;
import org.jblooming.utilities.JSP;
import org.jblooming.utilities.StringUtilities;
import org.jblooming.utilities.file.FileUtilities;
import org.jblooming.waf.SessionState;
import org.jblooming.waf.constants.Fields;
import org.jblooming.waf.html.input.HtmlElement;
import org.jblooming.waf.html.layout.Skin;
import org.jblooming.waf.settings.ApplicationState;
import org.jblooming.waf.settings.I18n;
import org.jblooming.waf.view.PageSeed;
import org.jblooming.waf.view.PageState;

import javax.servlet.jsp.PageContext;

/**
 * @author Pietro Polsinelli ppolsinelli@open-lab.com
 */
public class Img extends HtmlElement {

  public boolean required = false;
  public SessionState sessionState;
  public String script;
  public String imageUrl;
  public String width;
  public String height;
  public String style;
  public String align = "";


  public Img(PersistentFile pf, String title) {
    if (!PersistentFile.TYPE_WEBAPP_FILESTORAGE.equals(pf.getType())) {
      this.imageUrl = getPersistentFileUrl(pf);
    } else {
      this.imageUrl = ApplicationState.contextPath + "/" + StringUtilities.replaceAllNoRegex(pf.getFileLocation(), "\\", "/");
    }
    this.toolTip = title;
    this.translateToolTip = false;
  }

  private static String getPersistentFileUrl(PersistentFile pf) {
    String uplUID = pf.serialize();

    PageSeed imageUrlps = new PageSeed(ApplicationState.contextPath + "/commons/layout/partUploaderView.jsp");
    //myself.setCommand(Commands.FILE_VIEW);
    imageUrlps.addClientEntry(Fields.FILE_TO_UPLOAD, uplUID);
    String imageUrl = imageUrlps.toLinkToHref();
    return imageUrl;
  }

  /**
   * NB:: this method needs that the jsp include showTumb.js
   *
   * @param pf
   * @param underlyingText
   */
  public static String imgOnMouseOver(PersistentFile pf, String underlyingText) {
    String imageUrl = "";

    if (PersistentFile.TYPE_FILESTORAGE.equals(pf.getType())) {
      imageUrl = ApplicationState.getApplicationSetting(SystemConstants.FLD_REPOSITORY_URL) + pf.getFileLocation();

    } else if (PersistentFile.TYPE_FILESTORAGE_ABSOLUTE.equals(pf.getType())) {
      imageUrl = pf.getFileLocation();

    } else if (PersistentFile.TYPE_WEBAPP_FILESTORAGE.equals(pf.getType())) {
      imageUrl = ApplicationState.webAppFileSystemRootPath + pf.getFileLocation();
    }
    if (FileUtilities.isImageByFileExt(FileUtilities.getFileExt(pf.getOriginalFileName())))
      return "onMouseOver=\"showPreview('" + StringUtilities.replaceAllNoRegex(imageUrl, "\\", "/") + "','" + JSP.javascriptEncode(underlyingText) + "')\"";
    else
      return "";
  }


  public Img(String imageUrl, String title) {
    this.imageUrl = imageUrl;
    this.toolTip = title;
    this.translateToolTip = false;
  }

  public Img(String imageUrl, String title, String width, String heigth) {
    this.imageUrl = imageUrl;
    this.toolTip = title;
    this.width = width;
    this.height = heigth;
  }


  public StringBuffer toHtmlStringBuffer() {

    StringBuffer sb = new StringBuffer();

    sb.append("<img src=\"").append(imageUrl).append('\"');

    imgFill(sb);
    if (JSP.ex(align))
      // issue_ELE_4
      sb.append(" align=\"").append(align).append('\"');

    if (disabled)
      sb.append(" style=\"filter: progid:DXImageTransform.Microsoft.Alpha(opacity=50); -moz-opacity:0.5\" ");

    //MUST be last otherwise ButtonSubmit.getPDFPrintButton gets sputtanated
    if (script != null)
      sb.append(' ').append(script);

    sb.append(">");

    return sb;
  }


  private StringBuffer imgFill(StringBuffer sb) {
    if (JSP.ex(width))
      sb.append(" width=\"").append(width).append("\" ");

    if (JSP.ex(height))
      sb.append(" height=\"").append(height).append("\" ");

    sb.append(" border=\"0\"");

    sb.append(" name=\"").append(id).append("\"");

    if (JSP.ex(style))
      sb.append(" style=\"").append(style).append("\" ");

    if (JSP.ex(toolTip)) {
      sb.append(" title=\"").append(JSP.ex(getToolTip()) ? toolTip : "").append("\"");
      sb.append(" alt=\"").append(JSP.ex(getToolTip()) ? toolTip : "").append("\"");
    }

    sb.append(" id=\"").append(id).append("\" ");

    return sb;
  }

  public String getImageUrlOver() {
    int indexOfDot = imageUrl.lastIndexOf(".");
    return (imageUrl.substring(0, indexOfDot)) + "Over." + imageUrl.substring(indexOfDot + 1);
  }

  /**
   * @\deprecated Use with and height as string
   */
  public static void imgSpacer(int width, int height, PageContext pageContext) {
    imgSpacer("" + width, "" + height, pageContext);
  }

  public static void imgSpacer(String width, String height, PageContext pageContext) {
    imgSpacer(width, height, "", pageContext);
  }

  public static void imgSpacer(String width, String height, String tooltip, PageContext pageContext) {

    PageState ps = null;
    ps = PageState.getCurrentPageState();
    Img i = new Img(ps.sessionState.getSkin().imgPath + "blank.gif", "");
    i.width = width;
    i.height = height;
    i.toolTip = tooltip;
    i.toHtml(pageContext);

  }

  public void toHtml(PageContext pageContext) {
    PageState ps = PageState.getCurrentPageState();
    fixPathAndTranslateTooltip(ps);
    super.toHtml(pageContext);
  }

  public void fixPathAndTranslateTooltip(PageState pageState) {

    Skin skin = pageState.sessionState.getSkin();

    if (imageUrl.indexOf("/") == -1 && pageState.sessionState != null) {
      imageUrl = skin.imgPath + imageUrl;
    }

    if (translateToolTip) {
      toolTip = I18n.get(toolTip);
      translateToolTip = false;
    }
  }


}
