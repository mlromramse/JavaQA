package org.jblooming.waf.html.button;

import org.jblooming.PlatformRuntimeException;
import org.jblooming.utilities.JSP;
import org.jblooming.waf.html.state.Form;
import org.jblooming.waf.settings.I18n;
import org.jblooming.waf.view.PageState;

import java.io.Serializable;


public class ButtonJS extends ButtonSupport {

  public String onClickScript;

  public ButtonJS() {
    super();
  }

  public ButtonJS(String onClickScript) {
    this("",onClickScript);
  }

  public ButtonJS(String label,String onClickScript) {
    super("partButton.jsp");
    this.label=label;
    this.onClickScript = onClickScript;
  }

  public String getLaunchedJsOnActionListened() {
    String ret = null;
    if (onClickScript != null)
      ret = onClickScript;
    if (additionalScript != null)
      ret = ret + " " + additionalScript;
    return ret;
  }

  public void setMainObjectId(Serializable id) {
    throw new PlatformRuntimeException("setMainObjectId implementation not supported: do it by hand, lazy fool! Hahahahaha!");
  }


  public String generateLaunchJs() {

    StringBuffer sb = new StringBuffer();
    if (enabled) {
      sb.append(" onClick=\"");
      sb.append(generateJs());

      sb.append(";return false;\" ");// close onclick string

      if (additionalScript != null && additionalScript.trim().length() > 0)
        sb.append(' ').append(additionalScript);

    }
    return sb.toString();
  }


  public String getLabel() {
    return label;
  }

  public boolean isEnabled() {
    return enabled;
  }

  public String getToolTip() {
    return toolTip;
  }

  public StringBuffer generateJs() {

    StringBuffer sb = new StringBuffer();
    if (enabled) {
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

      sb.append(JSP.w(onClickScript));
      sb.append(JSP.w(additionalOnClickScript));

      if (confirmRequire) {
        sb.append("}"); // close if block
      }

    }
    return sb;
  }

  public static ButtonJS getResetInstance(Form f, PageState pageState) {
    return getResetInstance(f, I18n.get("RESET"));
  }

  public static ButtonJS getResetInstance(Form form, String label) {
    ButtonJS bs = new ButtonJS();
    bs.onClickScript = "obj('" + form.id + "').reset();";
    bs.label = label;
    return bs;
  }

  /**
   * ButtonJS delData = ButtonJS.getCommandInstance("DELETE_WORKLOG","WORKLOG_ID",workLog.getId()+"");
   */
  public static ButtonJS getCommandInstance(String command, String... nameAndValue) {
    ButtonJS bj = new ButtonJS();
    bj.onClickScript = getCommandJS(command, nameAndValue);
    return bj;
  }

  public static String getCommandJS(String command, String... nameAndValue) {
    if (nameAndValue.length % 2 != 0)
      throw new PlatformRuntimeException("nameAndValue must be an even lenght list of arguments");
    String commandExe = "executeCommand('" + command;
    if (nameAndValue.length > 0)
      commandExe = commandExe + "','";
    for (int i = 0; i < nameAndValue.length; i += 2) {
      commandExe = commandExe + (i > 0 ? "," : "") + nameAndValue[i] + "=" + JSP.urlEncode(nameAndValue[i + 1]);
    }
    return commandExe + "');";
  }


}

