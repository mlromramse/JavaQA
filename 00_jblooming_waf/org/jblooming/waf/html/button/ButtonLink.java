package org.jblooming.waf.html.button;

import org.jblooming.waf.constants.Commands;
import org.jblooming.waf.constants.Fields;
import org.jblooming.waf.html.core.UrlComposer;
import org.jblooming.waf.html.display.Img;
import org.jblooming.waf.view.PageSeed;
import org.jblooming.waf.view.PageState;
import org.jblooming.waf.settings.ApplicationState;
import org.jblooming.waf.settings.I18n;
import org.jblooming.waf.ActionController;
import org.jblooming.utilities.JSP;
import org.jblooming.utilities.DateUtilities;
import org.jblooming.ontology.LoggableIdentifiableSupport;
import org.jblooming.ontology.IdentifiableSupport;
import org.jblooming.security.SecurableWithArea;
import org.jblooming.security.Area;
import org.jblooming.persistence.PersistenceHome;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;
import java.io.Serializable;

public class ButtonLink extends ButtonSupport {

  public String popup_resizable;
  public String popup_scrollbars;
  public String popup_toolbar;
  public String popup_height;
  public String popup_width;


  public PageSeed pageSeed = new PageSeed();

  //in order to generate a link without any parameter
  public boolean inhibitParams = false;

  //these useful params are for preserving from the incumbent danger of html syntax changes :-D
  public static String TARGET_BLANK = "_blank";
  public static String TARGET_TOP = "_top";

  // used to ajax Link style
  private boolean ajaxEnabled = false;
  private String ajaxDomIdToReload = null;
  public String  ajaxCallbackFunction =null;


  public ButtonLink(PageSeed ps) {
    this(null, ps);
  }

  public ButtonLink(String label, PageSeed ps) {
    super();
    this.pageSeed = ps;
    this.label = label;
  }

  public String generateLaunchJs() {

    StringBuffer sb = new StringBuffer();
    if (enabled) {
      sb.append(" onClick=\"stopBubble(event);");
      sb.append(generateJs());
      sb.append("\" ");// close onclick string
      if (additionalScript != null && additionalScript.trim().length() > 0)
        sb.append(' ').append(additionalScript);
    }
    return sb.toString();
  }

  public boolean isEnabled() {
    return enabled;
  }

  public void setMainObjectId(Serializable id) {
    pageSeed.mainObjectId=id;
  }

  public String getCEHref() {
    Link fake = new Link(pageSeed);
    fake.outputModality = UrlComposer.OUTPUT_AS_JS_LAUNCH_CES;
    return fake.getHref();
  }

  public StringBuffer generateJs() {
    StringBuffer sb = new StringBuffer();
    if (confirmRequire) {
      sb.append("if (confirm('");
      if (confirmQuestion != null && confirmQuestion.length() > 0)
        sb.append(JSP.javascriptEncode(confirmQuestion));
      else
        sb.append("proceed?");
      sb.append("')");
      sb.append(')'); // close if parenthesis
      sb.append("{"); // open if block
    }

    if (additionalOnClickScript != null)
      sb.append(additionalOnClickScript);

    // when ajax is enabled change the call
    if (!ajaxEnabled) {
      sb.append("try{ ");
      if (target==null)
        sb.append("window.open('");
      else
        sb.append("openCenteredWindow('");

      sb.append(pageSeed.getHref());
      if (!inhibitParams)

        sb.append('?' + getCEHref());

      sb.append("',")
              .append(target == null ? "'_self'" : '\'' + target + '\'')
              .append(",'" + getWindowFeatures() + '\'')
              .append(") } catch(e){};"); // needed for unload alert
    } else {
      sb.append("loadAsyncroContent('" + pageSeed.getHref());
      if (!inhibitParams)
        sb.append('?' + getCEHref());
      sb.append("'" + (ajaxDomIdToReload == null ? ");" : ",'" + ajaxDomIdToReload + "'"+(JSP.ex(ajaxCallbackFunction) ? ","+ajaxCallbackFunction:"")+");"));      
    }

    if (confirmRequire) {
      sb.append("}"); // close if block
    }

    return sb;
  }

  public String toLink() {
    return "<a href=\"#\"" + this.generateLaunchJs() + ">" + this.label + "</a>";
  }

  public String toPlainLink() {
    StringBuffer sb = new StringBuffer();

    sb.append("<a href=\"");

    sb.append(toUrlWithParams());
    sb.append("\" ");
    if (target != null)
      sb.append(" target=\"").append(target).append("\"");
    sb.append(">");
    sb.append(label);
    sb.append("</a>");
    return sb.toString();
  }

  public String toUrlWithParams() {
    StringBuffer sb = new StringBuffer();
    sb.append(pageSeed.getHref());
    if (!inhibitParams)
      sb.append('?' + getCEHref());
    return sb.toString();
  }

  private String getWindowFeatures() {

    if (target == null)
      return "";
    StringBuffer sb = new StringBuffer();
    sb.append(popup_resizable != null ? "resizable=" + popup_resizable + ',' : "");
    sb.append(popup_scrollbars != null ? "scrollbars=" + popup_scrollbars + ',' : "");
    sb.append(popup_toolbar != null ? "toolbar=" + popup_toolbar + ',' : "");
    sb.append(popup_height != null ? "height=" + popup_height + ',' : "");
    sb.append(popup_width != null ? "width=" + popup_width + ',' : "");

    return sb.substring(0, Math.max(sb.length() - 1, 0));
  }

  public String getLabel() {
    return label;
  }


  public static ButtonLink getTextualInstance(String label, PageSeed ps) {
    ButtonLink bl = new ButtonLink(label, ps);
    bl.outputModality = ButtonSupport.TEXT_ONLY;
    return bl;
  }

  public static ButtonLink getDescriptiveLinkInstance(String label, String href) {
    ButtonLink bl = new ButtonLink(JSP.wHelp(label), new PageSeed(href));
    bl.outputModality = ButtonSupport.TEXT_ONLY;
    bl.inhibitParams=true;
    bl.target= TARGET_BLANK;
    return bl;
  }

  public static ButtonLink getPopupTextualInstance(String label, int popup_height, int popup_width, PageSeed ps) {
    ButtonLink bl = getPopupInstance(label,popup_height,popup_width,ps);
    bl.outputModality = ButtonSupport.TEXT_ONLY;
    return bl;
  }

   public static ButtonLink getPopupInstance(String label, int popup_height, int popup_width, PageSeed ps) {
    ps.setPopup(true);
    ButtonLink bl = new ButtonLink(label, ps);
    bl.target = ButtonLink.TARGET_BLANK;
    bl.popup_height = popup_height+"";
    bl.popup_width = popup_width+"";
    bl.popup_scrollbars = "yes";
    bl.popup_resizable = "yes";    
    return bl;
  }

  public static ButtonSupport getBlackInstance(String label, int height, int width, PageSeed ps) {
    return new ButtonJS(label,"openBlackPopup('"+ps.toLinkToHref()+"','"+width+"px','"+height+"px');");
  }

  public static ButtonLink getBackInstance(PageState pageState) {

    return getBackInstance(I18n.get("BACK"), pageState);
  }

  public static ButtonLink getBackInstance(String label, PageState pageState) {
    PageSeed ps = new PageSeed(ApplicationState.contextPath + pageState.href);
    ps.command = Commands.BACK;
    ButtonLink buttonLink = new ButtonLink(label, ps);
    buttonLink.alertOnChange = true;
    return buttonLink;
  }


  //todo ammazzare
  public static ButtonLink getAjaxButton(PageSeed ps, String domIdToReload) {
    ButtonLink pl = new ButtonLink(ps);
    pl.ajaxEnabled = true;
    pl.ajaxDomIdToReload = domIdToReload;
    return pl;
  }

  public static ButtonLink getDeleteInstance(String editPage,  IdentifiableSupport editando, HttpServletRequest request) {
    PageState pageState = PageState.getCurrentPageState();
    PageSeed edit = pageState.pageInThisFolder(editPage,request);
    edit.mainObjectId=editando.getId();
    edit.setCommand(Commands.DELETE_PREVIEW);
    ButtonLink editLink = ButtonLink.getTextualInstance(I18n.get("DELETE"), edit);
    return editLink;
  }


  public static ButtonLink getEditInstance(String editPage, IdentifiableSupport editando, HttpServletRequest request) {
    PageState pageState = PageState.getCurrentPageState();
    PageSeed edit = pageState.pageInThisFolder(editPage,request);
    edit.mainObjectId=editando.getId();
    edit.setCommand(Commands.EDIT);
    ButtonLink editLink = ButtonLink.getTextualInstance(I18n.get("EDIT"), edit);
    return editLink;
  }

  public static ButtonImg getEditInstanceForList(String editPage, IdentifiableSupport editando, HttpServletRequest request) {
    PageState pageState = PageState.getCurrentPageState();
    PageSeed edit = pageState.pageInThisFolder(editPage,request);
    edit.mainObjectId=editando.getId();
    edit.setCommand(Commands.EDIT);
    ButtonLink editLink = ButtonLink.getTextualInstance("", edit);
    Img img = new Img(pageState.getSkin().imgPath + "list/edit.gif", getToolTipForIdentifiable(editando,pageState), "", "");
    return new ButtonImg(editLink, img);
  }

  public static ButtonImg getDeleteInstanceForList(String editPage, IdentifiableSupport editando, HttpServletRequest request) {
    PageState pageState = PageState.getCurrentPageState();
    PageSeed edit = pageState.pageInThisFolder(editPage,request);
    edit.mainObjectId=editando.getId();
    edit.setCommand(Commands.DELETE_PREVIEW);
    ButtonLink editLink = ButtonLink.getTextualInstance("", edit);
    Img img = new Img(pageState.getSkin().imgPath + "list/del.gif", getToolTipForIdentifiable(editando,pageState), "", "");
    return new ButtonImg(editLink, img);
  }


  public static ButtonImg getAjaxDeleteInstanceForList(IdentifiableSupport editando, String command, Class<? extends ActionController> controller, String tableRowid, PageState pageState) {
    return getAjaxDeleteInstanceForList(editando.getId(), command, controller, tableRowid, pageState);
  }

  public static ButtonImg getAjaxDeleteInstanceForList(Serializable objId, String command, Class<? extends ActionController> controller, String tableRowid, PageState pageState) {
    ButtonJS deleteLink = new ButtonJS();
    deleteLink.confirmRequire = true;
    String ajaxScript = "$('#" + tableRowid + "').remove();";

    if (JSP.ex(objId) && !(objId + "").equalsIgnoreCase(PersistenceHome.NEW_EMPTY_ID + "")) {
      ajaxScript = ajaxScript + "executeCommand('CALLCONTR','CTCL=" + controller.getName() + "&CTRM=" + command + "&OBID=" + objId + "');";
    }

    deleteLink.onClickScript = ajaxScript;
    Img img = new Img(pageState.getSkin().imgPath + "list/del.gif", "", "", "");

    return new ButtonImg(deleteLink, img);
  }

  public static String getToolTipForIdentifiable(IdentifiableSupport it, PageState pageState) {

    String separator="&nbsp;";

    StringBuffer result = new StringBuffer();
    if (it != null) {

      result.append("Database id: " + separator + it.getId()+ separator);
      if (it instanceof SecurableWithArea) {
        Area area = ((SecurableWithArea) it).getArea();
        result.append(separator+separator+I18n.get("AREA") + separator);
        String name = null;
        if (area!=null)
           name = JSP.htmlEncodeApexesAndTags(area.getName());
        result.append(JSP.w(area != null ? name : I18n.get("NO_AREA") ));
      }

      if (it instanceof LoggableIdentifiableSupport) {
        LoggableIdentifiableSupport lit = (LoggableIdentifiableSupport) it;
        String creator = JSP.htmlEncodeApexesAndTags(lit.getCreator());
        String lastModifier = JSP.htmlEncodeApexesAndTags(lit.getLastModifier());

      result.append("<br>"+I18n.get("CREATED_BY") + separator + (creator != null ? creator : "-") + separator);
        if (lit.getCreationDate() != null)
          result.append(I18n.get("ON_DATE") + separator + DateUtilities.dateAndHourToString(lit.getCreationDate()));
      result.append("<br>"+I18n.get("LAST_MODIFIED_BY") + separator + (lastModifier != null ? lastModifier+ separator : "-" + separator));
        result.append(I18n.get("ON_DATE") + separator + DateUtilities.dateAndHourToString(lit.getLastModified()) + separator);

    }
    }
    return result.toString();
  }

  public static ButtonImg getPDFPrintButton(PageSeed ps, PageState pageState) {
    PageSeed seed = new PageSeed(ApplicationState.contextPath + "/commons/tools/printPDF.jsp");
    seed.addClientEntry("PRINTING_PDF", Fields.TRUE);
    seed.addClientEntry("PAGE_TO_PRINT",ps.href);
    seed.addClientEntries(ps.getClientEntries());
    seed.command=ps.command;
    seed.mainObjectId=ps.mainObjectId;

    Img img = new Img(pageState.getSkin().imgPath + "mime/application_pdf.gif", "PDF");
    return new ButtonImg(new ButtonLink(seed), img);
  }

  public static ButtonImg getPDFPrintButton(PageSeed ps, PageState pageState, String fileName) {
    PageSeed seed = new PageSeed(ApplicationState.contextPath + "/commons/tools/printPDF.jsp");
    seed.addClientEntry("PRINTING_PDF", Fields.TRUE);
    seed.addClientEntry("PAGE_TO_PRINT",ps.href);
    if(!fileName.endsWith(".pdf"))
    fileName = fileName + ".pdf";
    seed.addClientEntry("FILE_NAME_FOR_ATTACHMENT", fileName);
    seed.addClientEntries(ps.getClientEntries());
    seed.command=ps.command;
    seed.mainObjectId=ps.mainObjectId;

    Img img = new Img(pageState.getSkin().imgPath + "mime/application_pdf.gif", "PDF");
    return new ButtonImg(new ButtonLink(seed), img);
  }

  public static ButtonLink getPDFFreezeButton(PageSeed pageToPrintNoContextPath, PageSeed pageToRedirToCompletePath, String fileNamePrefix) {
      PageSeed seed = new PageSeed(ApplicationState.contextPath + "/commons/tools/freezePDF.jsp");
      seed.addClientEntry("PRINTING_PDF", Fields.TRUE);
      seed.addClientEntry("PAGE_TO_PRINT",pageToPrintNoContextPath.href);
      seed.addClientEntries(pageToPrintNoContextPath.getClientEntries());
      seed.addClientEntry("REDIR_URL",pageToRedirToCompletePath.toLinkToHref());
      seed.addClientEntry("FREEZE_PREFIX",fileNamePrefix);
      seed.command=pageToPrintNoContextPath.command;
      seed.mainObjectId=pageToPrintNoContextPath.mainObjectId;
      return new ButtonLink(seed);
    }



}

