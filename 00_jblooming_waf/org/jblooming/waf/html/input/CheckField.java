package org.jblooming.waf.html.input;

import org.jblooming.waf.constants.Fields;
import org.jblooming.waf.html.core.JspHelper;
import org.jblooming.waf.settings.I18n;
import org.jblooming.waf.view.PageState;

import javax.servlet.jsp.PageContext;
import java.util.Arrays;
import java.util.List;

public class CheckField extends JspHelper {

  public String label;
  public String fieldName;
  public boolean putLabelFirst;
  public String script;
  public String additionalOnclickScript="";
  public String separator;
  public boolean disabled = false;
  public boolean preserveOldValue = true;

  public String[] trueFalseValues = {Fields.TRUE, Fields.FALSE};

  public String selector = "";




  public CheckField(String fieldName, String separator, boolean putLabelFirst) {
    this(fieldName, fieldName, separator, putLabelFirst);
  }

  public CheckField(String label, String fieldName, String separator, boolean putLabelFirst) {
    this.urlToInclude = "/commons/layout/partCheckField.jsp";
    this.label = label;
    this.fieldName = fieldName;
    this.putLabelFirst = putLabelFirst;
    this.separator = separator;
    this.id = fieldName;
  }

  public void addKeyPressControl(int keyToHandle, String launchedJs, String actionListened) {
    int keyToHandle1=keyToHandle;
    String launchedJsOnActionListened=launchedJs;
    String actionListened1=actionListened;
  }

  public void toHtmlI18n(PageContext pageContext) {
    PageState pageState = PageState.getCurrentPageState();
    if (label == null)
      label = I18n.get(fieldName);
    else
      label = I18n.get(label);
    toHtml(pageContext);
  }


  public boolean isChecked(PageContext pageContext) {
    PageState pageState = PageState.getCurrentPageState();
    return Fields.TRUE.equals(pageState.getEntry(fieldName).stringValueNullIfEmpty());
  }

  public static CheckField getMasterCheckField(String fieldName, String... checkBoxesFieldPrefixes) {
    return getMasterCheckField(fieldName,Arrays.asList(checkBoxesFieldPrefixes));    
  }

  public static CheckField getMasterCheckField(String fieldName, List<String> checkBoxesFieldPrefixes) {

    CheckField toReturn = new CheckField(fieldName, "", false);

    boolean isFirst = true;
    for (String checkBoxesFieldName : checkBoxesFieldPrefixes) {

      if (!isFirst)
       toReturn.selector = toReturn.selector+",";

      isFirst = false;
      toReturn.selector = toReturn.selector +"input[id*=ck_" + checkBoxesFieldName + "][type=checkbox]:enabled ";
    }

    return toReturn;

  }



 }
