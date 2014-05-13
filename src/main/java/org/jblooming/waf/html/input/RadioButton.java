package org.jblooming.waf.html.input;

import org.jblooming.ApplicationException;
import org.jblooming.utilities.JSP;
import org.jblooming.waf.view.ClientEntry;
import org.jblooming.waf.view.PageState;

import javax.servlet.jsp.PageContext;

public class RadioButton extends InputElement {

  public String buttonValue;
  public String style;

  PageState view;

  public RadioButton() {
  }

  public RadioButton(String label,
                     String fieldName,
                     String buttonValue,
                     String separator,
                     String htmlClass,
                     boolean disabled,
                     String script,
                     PageState view) {

    this.fieldName = fieldName;
    this.buttonValue = buttonValue;
    this.label = label;
    this.separator = separator;
    this.fieldClass = htmlClass;
    this.disabled = disabled;
    this.script = script;
    this.view = view;
  }


  public StringBuffer toHtmlStringBuffer()  {

    StringBuffer sb = new StringBuffer();

    RadioButton rb = this;

    sb.append("<input type=\"radio\" name=\"");
    sb.append(rb.fieldName).append('\"').append(" id=\"").append(id).append('\"');
    if (JSP.ex( rb.fieldClass))
      sb.append(" class=\"").append(rb.fieldClass).append('\"');
    if (JSP.ex(toolTip) ) {
      sb.append(" title=\"").append(getToolTip()).append('\"');
    }
    sb.append(disabled ? " disabled" : "");

    sb.append(" value=\"").append(rb.buttonValue).append('\"');

    ClientEntry field = value;
    if (field != null && field.stringValueNullIfEmpty() != null && !field.stringValueNullIfEmpty().equals("") && field.stringValueNullIfEmpty().equals(rb.buttonValue)) {
      sb.append(" checked ");
    }

    if (script != null) {
      sb.append(" onClick=\"");
      sb.append(rb.script);
      sb.append('\"');
    }

    if (rb.style != null)
      sb.append(rb.style);

    sb.append(">\n");
    sb.append(separator).append('\n');

    if (rb.label != null && rb.label.trim().length() > 0)
      sb.append(JSP.makeTag("label", "for=\""+rb.id+"\"", translateLabel ? view.getI18n(rb.label) : label));


    return sb;
  }


}