package org.jblooming.waf.html.input;

import org.jblooming.utilities.JSP;
import org.jblooming.waf.html.state.Form;
import org.jblooming.waf.view.PageState;

import javax.servlet.jsp.PageContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Date: 19-feb-2003
 * Time: 17.25.51
 *
 * @author Pietro Polsinelli ppolsinelli@open-lab.com
 */
public class TextField extends InputElement {

  public static final String init = TextField.class.getName();

  public String type;
  public boolean doFeedBackError = true;
  public int maxlength=255;
  public boolean autoSize = false;
  public boolean searchField = false;

  public String innerLabel; // printed inside the field

  //auto validation
  public EntryType entryType;

  private TextField() {
  }

  public TextField(String fieldName, String separator) {
    this("text", fieldName, fieldName, separator, 20, false);
  }

  public TextField(String type,
                   String fieldName,
                   String separator,
                   int fieldSize) {


     this(type, fieldName, fieldName, separator, fieldSize, false, false, 255, null);

  }

  public TextField(String label,
                   String fieldName,
                   String separator,
                   int fieldSize,
                   boolean readOnly) {

    this("text", label, fieldName, separator, fieldSize, false, readOnly, 255, null);
  }

  public TextField(String label,
                   String fieldName,
                   String separator,
                   int fieldSize,
                   boolean readOnly,
                   int maxlength) {


    this("text", label, fieldName, separator, fieldSize, false, readOnly, maxlength, null);

  }

  public  TextField(String type,
                   String label,
                   String fieldName,
                   String separator,
                   int fieldSize,
                   boolean readOnly) {

    this(type, label, fieldName, separator, fieldSize, false, readOnly, 255, null);
  }


  /**
   * @param type
   * @param label
   * @param fieldName
   * @param separator
   * @param fieldSize
   * @param disabled
   * @param readOnly
   * @param script
   */
  public TextField(String type,
                   String label,
                   String fieldName,
                   String separator,
                   int fieldSize,
                   boolean disabled,
                   boolean readOnly,
                   String script) {

    this(type, label, fieldName, separator, fieldSize, disabled, readOnly, 255, script);

  }

  public TextField(String type,
                   String label,
                   String fieldName,
                   String separator,
                   int fieldSize,
                   boolean disabled,
                   boolean readOnly,
                   int maxlength,
                   String script) {

    id = fieldName;
    this.fieldName = fieldName;
    this.maxlength = maxlength;
    this.id = fieldName;
    this.type = type;
    this.label = label;
    this.separator = separator;
    this.fieldSize = fieldSize;
    this.disabled = disabled;
    this.readOnly = readOnly;
    this.script = script;
  }

  public static TextField getIntegerInstance(String fieldName) {
    return getInstance(fieldName,EntryType.INTEGER);
  }

  public static TextField getDoubleInstance(String fieldName) {
    return getInstance(fieldName,EntryType.DOUBLE);
  }

  public static TextField getTimeInstance(String fieldName) {
    TextField tf = getInstance(fieldName,EntryType.TIME);
    tf.fieldSize=7;
    return tf;
  }

  public static TextField getDurationInDaysInstance(String fieldName) {
    TextField tf = getInstance(fieldName,EntryType.DURATIONDAYS);
    tf.fieldSize=4;
    return tf;
  }

  public static TextField getDurationInMillisInstance(String fieldName) {
    TextField tf = getInstance(fieldName,EntryType.DURATIONMILLIS);
    tf.fieldSize=7;
    return tf;
  }

  public static TextField getPercentileInstance(String fieldName) {
    return getInstance(fieldName,EntryType.PERCENTILE);
  }

  public static TextField getCurrencyInstance(String fieldName) {
    TextField field = getInstance(fieldName, EntryType.CURRENCY);
    field.fieldSize=6;
    return field;
  }

  public static TextField getURLInstance(String fieldName) {
    TextField instance = getInstance(fieldName, EntryType.URL);
    instance.type="url";
    return instance;
  }

  public static TextField getEmailInstance(String fieldName) {
    TextField instance = getInstance(fieldName, EntryType.EMAIL);
    instance.type="email";
    return instance;
  }


  private static TextField getInstance(String fieldName, EntryType entryType) {
    TextField tf = new TextField(fieldName,"</td><td>");
    tf.entryType=entryType;
    return tf;
  }

  public void toHtml(PageContext pageContext) {
    super.toHtml(pageContext);
  }

  protected int getDecodedValueLength() {
    if (value == null || value.stringValueNullIfEmpty() == null)
      return 0;
    Matcher counter = Pattern.compile("&[^&;]+;").matcher(value.stringValueNullIfEmpty());
    String tmp = "";
    int start = 0;
    while (counter.find(start)) {
      int groupStart = counter.start();
      tmp += value.stringValueNullIfEmpty().substring(start, groupStart);
      tmp += "?";
      start = counter.end();
    }
    tmp += JSP.encode(value.stringValueNullIfEmpty().substring(start));
    return tmp.length();
  }

  public StringBuffer toHtmlStringBuffer() {

    StringBuffer sb = new StringBuffer();

    if (label == null) label = fieldName;

    if (label != null && label.length() > 0 && showLabel) {
      if (labelClass != null && labelClass.length() > 0)
        sb.append("<span class=\"" + labelClass + "\">");
      sb.append(label);
      if (required && label.indexOf("*") == -1)
        sb.append('*');

      if (labelClass != null && labelClass.length() > 0)
        sb.append("</span>");
    }

    sb.append(JSP.w(separator));
    
    if(!JSP.ex(type))
     type= "text";
    sb.append("<input type=").append(type).append(" name=\"");

    sb.append(fieldName).append('\"').append(" id=\"").append(id).append('\"');

    //compute value
    String displayedValue = "";

    if (value != null && value.stringValueNullIfEmpty() != null) {
      if (fieldValueHasToBeEncoded)
        displayedValue = JSP.encode(value.stringValueNullIfEmpty());
      else
        displayedValue = value.stringValueNullIfEmpty();
    }

    if (JSP.ex(innerLabel))
      sb.append(" innerLabel=\""+innerLabel+"\"");

    //print size
    sb.append(" size=");
    if (!autoSize)
      sb.append(fieldSize);
    else
      sb.append(Math.min(displayedValue.length(),fieldSize));

    if (!JSP.ex(fieldClass) && !type.equalsIgnoreCase("hidden")) {
      fieldClass = "formElements";
    }

    if (JSP.ex(fieldClass))
      sb.append(" class=\"").append(fieldClass+getBackgroundClass()).append('\"');

    if (tabIndex != 0) {
      sb.append(" tabindex=\"").append(tabIndex).append('\"');
    }

    if (JSP.ex(toolTip)) {
      sb.append(" title=\"").append(getToolTip()).append('\"');
    }

    if (disabled)  {
        sb.append(" disabled");
    }

    sb.append(readOnly ? " readonly" : "");

    //this is because explorer does not make it visible
    //if (disabled || readOnly)
    //  sb.append(" style=\"background-color:#f3f3f3\"");

    if (maxlength>0) sb.append(" maxlength=" + maxlength);

    sb.append(script != null && script.length() > 0 ? ' ' + script : "");

    sb. append(" value=\"").append(displayedValue).append('\"');

    if (launchedJsOnActionListened != null) {
      sb.append(' ' + actionListened + "= \"if (event.keyCode==" + keyToHandle +(checkCtrlKey ? " && event.ctrlKey==true " : "") + ") { " + launchedJsOnActionListened);
      sb.append("return false;}\"");
    }

    if (required)
      sb.append(" required=\"true\" ");
    
//    if (excludeFromAlert)
//      sb.append(" excludeFromAlert=\"true\" ");

    if (preserveOldValue && !JSP.ex(value.errorCode)) {
      //sb. append(" oldValue=\"").append(JSP.ex(value.errorCode) ? "" :displayedValue).append('\"');
      sb.append(" oldValue='1'");
    }

    if (entryType!=null) {
      sb.append(" entryType=\""+entryType+"\"");
    }

    sb.append(" >");

    return sb;
  }

  private String getBackgroundClass() {
    String result = "";

    if (entryType!=null) {
        result=" validated "+entryType.toString().toLowerCase();
     
    } else if (searchField)
      result=" qbe";

    if (readOnly || disabled)
      result=result+" grayed";

    return result;
  }


  /**
   * @deprecated
   */
  public static TextField getTransparentInstance(String type,
                                                 String label,
                                                 String fieldName,
                                                 String separator,
                                                 int fieldSize,
                                                 boolean readonly,
                                                 boolean focused,
                                                 PageState view) {
    TextField tf = new TextField(type,
      label,
      fieldName,
      separator,
      fieldSize,
      false, readonly,
      " onFocus=\"this.className='formElementsTransparentOver'\" onBlur=\"this.className='formElementsTransparent'\"");

    return tf;
  }


  private static void hiddenInstanceToHtmlPrivate(String id,String fieldName,PageContext pageContext) {
    TextField tf = new TextField("hidden",fieldName,"",1);
    tf.id = id;
    tf.label = "";
    tf.preserveOldValue = false;
    tf.fieldClass="";
    tf.toHtml(pageContext);
  }

   public static void hiddenInstanceToHtml(String fieldName,PageContext pageContext) {
     hiddenInstanceToHtmlPrivate(fieldName,fieldName,pageContext);
  }

  public static void hiddenInstanceToHtml(String fieldName,String value, PageContext pageContext) {
    PageState ps = PageState.getCurrentPageState();
    ps.addClientEntry(fieldName,value);
    hiddenInstanceToHtmlPrivate(fieldName,fieldName,pageContext);
  }

  public static void hiddenInstanceOfFormToHtml(String ceName, Form form, PageContext pageContext) {
    hiddenInstanceToHtmlPrivate(form.getUniqueName()+ceName,ceName,pageContext);
  }
}
