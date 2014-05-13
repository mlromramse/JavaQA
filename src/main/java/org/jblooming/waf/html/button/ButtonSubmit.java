package org.jblooming.waf.html.button;

import org.jblooming.PlatformRuntimeException;
import org.jblooming.utilities.HTMLEncoderOld;
import org.jblooming.utilities.JSP;
import org.jblooming.waf.constants.Commands;
import org.jblooming.waf.constants.Fields;
import org.jblooming.waf.constants.I18nConstants;
import org.jblooming.waf.exceptions.ActionException;
import org.jblooming.waf.html.core.UrlComposer;
import org.jblooming.waf.html.display.Img;
import org.jblooming.waf.html.state.Form;
import org.jblooming.waf.settings.ApplicationState;
import org.jblooming.waf.settings.I18n;
import org.jblooming.waf.view.PageSeed;
import org.jblooming.waf.view.PageState;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;
import java.io.Serializable;

public class ButtonSubmit extends ButtonSupport {

  public Form form;
  public PageSeed variationsFromForm = new PageSeed();
  public boolean preserveFormStatus = false;

  public boolean alertOnRequired = false;

  // used to ajax submit style
  public boolean ajaxEnabled = false;
  public String ajaxDomIdToReload = null;
  public String ajaxCallbackFunction = null;


  public ButtonSubmit(Form form) {
    super();
    this.form = form;
    if (form != null && form.url != null && form.url.getCommand() != null)
      variationsFromForm.setCommand(form.url.getCommand());
  }

  public ButtonSubmit(String label, String command, Form form) {
    this(form);
    this.label = label;
    this.variationsFromForm.command = command;
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

    Link fake = new Link(variationsFromForm);
    fake.outputModality = UrlComposer.OUTPUT_AS_JS_SUBMIT;
    fake.id = form.id;
    sb.append(fake.getHref());

    if (preserveFormStatus)
      sb.append("saveFormValues('" + form.getUniqueName() + "');  ");

    if (target != null && target.trim().length() > 0)
      sb.append("obj('").append(form.getUniqueName()).append("').target='").append(HTMLEncoderOld.removeBadCharsFromJSConstant(target)).append("'; ");

    //issue_ELE_6

    if (variationsFromForm.getHref() != null && variationsFromForm.getHref().length() > 0 && !variationsFromForm.getHref().equals(form.url.getHref()))
      sb.append("obj('").append(form.getUniqueName()).append("').action='" + variationsFromForm.getHref() + "'; ");

    if (additionalOnClickScript != null)
      sb.append(additionalOnClickScript);

    String submitMethod;
    // if the button is ajax style change the submit method
    if (ajaxEnabled) {
      submitMethod = "ajaxSubmit('" + form.getUniqueName() + "'" + (ajaxDomIdToReload == null ? ");" : ",'" + ajaxDomIdToReload + "'" + (JSP.ex(ajaxCallbackFunction) ? "," + ajaxCallbackFunction : "") + ");");

    } else
      submitMethod = "try {obj('" + form.getUniqueName() + "').submit();} catch(e){};";
    // submitMethod="obj('"+form.getUniqueName()+"').submit();";  // REMOVED by bicch matti per eccezione onUnload in caso cancel per IE

    submitMethod = (alertOnChange ? "" : "muteAlertOnChange=true;") + submitMethod;

    if (alertOnRequired)
      sb.append("if (canSubmitForm('").append(form.getUniqueName()).append("'))  {" + submitMethod + "} ");
    else
      sb.append(submitMethod);

    if (preserveFormStatus)
      sb.append("restoreFormValues('" + form.getUniqueName() + "');  ");

    if (confirmRequire) {
      sb.append("}"); // close if block
    }

    sb.append(";return false;");

    return sb;
  }

  public String getLabel() {
    return label;
  }

  public static void toHtmlSave(Form form, String label, PageContext pageContext) {
    ButtonSubmit bs = getSaveInstance(form, label);
    bs.toHtml(pageContext);
  }

  public static ButtonSubmit getSaveInstance(PageState pageState) {
    if (pageState.getForm() == null)
      throw new PlatformRuntimeException("getSaveInstance(PageState pageState) assumes that form is on pageState, but here is null");
    return getSaveInstance(pageState.getForm(), I18n.get("SAVE"), true);
  }

  public static ButtonSubmit getSaveInstance(Form form, String label) {
    return getSaveInstance(form, label, true);
  }

  public static ButtonSubmit getSaveInstance(Form form, String label, boolean boldify) {
    ButtonSubmit bs = new ButtonSubmit(form);
    bs.variationsFromForm.setCommand(Commands.SAVE);
    if (boldify)
      bs.label = JSP.makeTag("b", null, label);
    else
      bs.label = label;
    bs.alertOnRequired = true;
    bs.alertOnChange = false;
    return bs;
  }

  public static ButtonSubmit getDeleteInstance(Form form, PageState pageState) {
    ButtonSubmit bs = new ButtonSubmit(form);
    bs.variationsFromForm.setCommand(Commands.DELETE_PREVIEW);
    bs.label = I18n.get("DELETE");
    return bs;
  }

  public static ButtonSubmit getSearchInstance(Form form, PageState pageState) {
    ButtonSubmit bs = new ButtonSubmit(form);
    bs.variationsFromForm.setCommand(Commands.FIND);
    bs.label = I18n.get(I18nConstants.SEARCH);
    bs.label = JSP.makeTag("b", null, bs.label);
    return bs;
  }

  public static ButtonSubmit getTextualInstance(String label, Form form) {
    ButtonSubmit bl = new ButtonSubmit(form);
    bl.label = label;
    bl.outputModality = ButtonSupport.TEXT_ONLY;
    return bl;
  }


  public static ButtonSubmit getPrintInstance(String printPage, Form form, PageState pageState, HttpServletRequest request) {
    ButtonSubmit bs = new ButtonSubmit(form);
    bs.variationsFromForm.setCommand(Commands.FIND);
    bs.variationsFromForm.setHref(pageState.pageInThisFolder(printPage, request).getHref());
    bs.label = I18n.get("PRINT");
    return bs;
  }

  public static ButtonSupport getSubmitInstanceInBlack(Form form, String actionHref, int w, int h) {
    ButtonJS pl = new ButtonJS();
    pl.onClickScript =
            "openBlackPopup('', '" + w + "px', '" + h + "px',null,'"+form.id+"_ifr"+"');" +
                    "if (!obj('FORM_ID') ) {" +

                    "var oNewNode = document.createElement('input');" +
                    "oNewNode.type='hidden';oNewNode.name='FORM_ID';oNewNode.id='FORM_ID';oNewNode.value='" + form.getUniqueName() + "';" +
                    "obj('" + form.getUniqueName() + "').appendChild(oNewNode);" +

                    "}" +

                    "if (!obj('" + form.getUniqueName() + Fields.POPUP + "') ) {" +

                    "var oNewNode2 = document.createElement('input');" +
                    "oNewNode2.type='hidden';oNewNode2.name='" + form.getUniqueName() + Fields.POPUP + "';oNewNode2.id='" + form.getUniqueName() + Fields.POPUP + "';oNewNode2.value='" + Fields.TRUE + "';" +
                    "obj('" + form.getUniqueName() + "').appendChild(oNewNode2);" +

                    "}" +
                    "saveFormValues('" + form.getUniqueName() + "');" +
                    "obj('" + form.getUniqueName() + "').action='" + actionHref + "';" +
                    "obj('" + form.getUniqueName() + "').target='"+form.id+"_ifr"+"';" +
                    "obj('" + form.getUniqueName() + "').submit();window.name='mainWindow';" +
                    "restoreFormValues('" + form.getUniqueName() + "');";
    return pl;


  }

  public static ButtonSupport getSubmitInstanceInPopup(Form form, String actionHref, int w, int h) {
    ButtonJS pl = new ButtonJS();
    pl.onClickScript =

            "centerPopup('', 'winPopup', '" + w + "', '" + h + "', 'yes','yes');" +

                    "if (!obj('FORM_ID') ) {" +

                    "var oNewNode = document.createElement('input');" +
                    "oNewNode.type='hidden';oNewNode.name='FORM_ID';oNewNode.id='FORM_ID';oNewNode.value='" + form.getUniqueName() + "';" +
                    "obj('" + form.getUniqueName() + "').appendChild(oNewNode);" +

                    "}" +

                    "if (!obj('" + form.getUniqueName() + Fields.POPUP + "') ) {" +

                    "var oNewNode2 = document.createElement('input');" +
                    "oNewNode2.type='hidden';oNewNode2.name='" + form.getUniqueName() + Fields.POPUP + "';oNewNode2.id='" + form.getUniqueName() + Fields.POPUP + "';oNewNode2.value='" + Fields.TRUE + "';" +
                    "obj('" + form.getUniqueName() + "').appendChild(oNewNode2);" +

                    "}" +
                    "saveFormValues('" + form.getUniqueName() + "');" +
                    "obj('" + form.getUniqueName() + "').action='" + actionHref + "';" +
                    "obj('" + form.getUniqueName() + "').target='winPopup';" +
                    "obj('" + form.getUniqueName() + "').submit();window.name='mainWindow';" +
                    "restoreFormValues('" + form.getUniqueName() + "');";

    //pl.urlToInclude = "/commons/layout/partButtonPopup.jsp";
    return pl;

  }


  public static ButtonSupport getSubmitInstanceFromPopup(Form form, String command, PageState pageState) throws ActionException {

    ButtonJS pl = new ButtonJS();

    String formId = pageState.getEntry("FORM_ID").stringValue();

    pl.onClickScript =
            //pietro 11Dec2008 saved filters may not have passed the id
            (JSP.ex(formId) ? "window.opener.document.getElementById('" + formId + "').setAttribute('alertOnChange','false');":"") +
                    "obj('" + form.getUniqueName() + "').action=window.opener.location.pathname;" +
                    "obj('" + form.getUniqueName() + "').target='mainWindow';" +
                    "obj('" + form.getUniqueName() + Commands.COMMAND + "').value='" + command + "';" +

                    //this because the calling page is not popup
                    "obj('" + form.getUniqueName() + Fields.POPUP + "').disabled=true;" +
                    "obj('" + form.getUniqueName() + "').submit();window.close();";

    return pl;

  }


  public static ButtonSubmit getAjaxButton(Form form, String domIdToReload) {
    ButtonSubmit pl = new ButtonSubmit(form);
    pl.ajaxEnabled = true;
    pl.ajaxDomIdToReload = domIdToReload;
    return pl;
  }

  public static ButtonImg getPDFPrintButton(Form form, PageState pageState) {
    return getPDFPrintButton(form, null, pageState);
  }

  /*
  ADD
   */
  public static ButtonImg getPDFPrintButton(Form form, String pdfFileName, PageState pageState) {
    return getPDFPrintButton(form, pdfFileName, null, pageState);
  }

  public static ButtonImg getPDFPrintButton(Form form, String pdfFileName, PageSeed pageToPrint, PageState pageState) {
    ButtonSubmit pdf = new ButtonSubmit(form);
    pdf.variationsFromForm.setHref(ApplicationState.contextPath + "/commons/tools/printPDF.jsp");
    pdf.variationsFromForm.addClientEntry("PRINTING_PDF", Fields.TRUE);
    Img img = new Img(pageState.getSkin().imgPath + "mime/application_pdf.gif", "PDF");
    form.url.addClientEntry("PRINTING_PDF", Fields.FALSE);
    if(pageToPrint != null)
     form.url.addClientEntry("PAGE_TO_PRINT", pageToPrint.href);
    else
     form.url.addClientEntry("PAGE_TO_PRINT", pageState.href);
    if (JSP.ex(pdfFileName))
      form.url.addClientEntry("FILE_NAME_FOR_ATTACHMENT", pdfFileName + ".pdf");
    //img.script="><input type=\"hidden\" id=\""+form.getUniqueName()+"PRINTING_PDF\" name=\"PRINTING_PDF\" value=\""+Fields.FALSE+"\"><input type=\"hidden\" name=\"PAGE_TO_PRINT\" value=\""+pageState.href+"\" ";
    pdf.preserveFormStatus=true;
    return new ButtonImg(pdf, img);
  }

   public static ButtonSubmit getPDFPrintButtonSubmit(Form form, String pdfFileName, PageSeed pageToPrint, PageState pageState) {
    ButtonSubmit pdf = new ButtonSubmit(form);
    pdf.variationsFromForm.setHref(ApplicationState.contextPath + "/commons/tools/printPDF.jsp");
    pdf.variationsFromForm.addClientEntry("PRINTING_PDF", Fields.TRUE);
    Img img = new Img(pageState.getSkin().imgPath + "mime/application_pdf.gif", "PDF");
    form.url.addClientEntry("PRINTING_PDF", Fields.FALSE);
    if(pageToPrint != null)
     form.url.addClientEntry("PAGE_TO_PRINT", pageToPrint.href);
    else
     form.url.addClientEntry("PAGE_TO_PRINT", pageState.href);
    if (JSP.ex(pdfFileName))
      form.url.addClientEntry("FILE_NAME_FOR_ATTACHMENT", pdfFileName + ".pdf");
    //img.script="><input type=\"hidden\" id=\""+form.getUniqueName()+"PRINTING_PDF\" name=\"PRINTING_PDF\" value=\""+Fields.FALSE+"\"><input type=\"hidden\" name=\"PAGE_TO_PRINT\" value=\""+pageState.href+"\" ";
    pdf.preserveFormStatus=true;
    return pdf;
  }

  public void setMainObjectId(Serializable id) {
    variationsFromForm.mainObjectId = id;
  }

}

