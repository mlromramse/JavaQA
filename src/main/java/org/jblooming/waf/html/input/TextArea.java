package org.jblooming.waf.html.input;

import org.jblooming.utilities.JSP;
import org.jblooming.waf.settings.I18n;
import org.jblooming.waf.view.ClientEntry;
import org.jblooming.waf.view.PageState;

import javax.servlet.jsp.PageContext;

public class TextArea extends InputElement {

  public int maxlength=2000;
  public int fieldCols;
  public int fieldRows;
  private PageState pageState;
  public String innerLabel; // printed inside the field



  //auto grow support
  private int maxHeight=0;
  private int minHeight=0;
  private int lineHeight=0;
  private boolean autogrow=false;


  public TextArea(String fieldName,
                  String separator,
                  int fieldCols,
                  int fieldRows,
                  String fieldClass) {


    this(fieldName, fieldName,
            separator,
            fieldCols,
            fieldRows,
            fieldClass,
            false,
            false,
            "");
  }

  public TextArea(String label, String fieldName,
                  String separator,
                  int fieldCols,
                  int fieldRows,
                  String script) {


    this(label, fieldName,
            separator,
            fieldCols,
            fieldRows,
            "",
            false,
            false,
            script);
  }



  public TextArea(String label, String fieldName,
                  String separator,
                  int fieldCols,
                  int fieldRows,
                  String fieldClass,
                  boolean disabled,
                  boolean readOnly,
                  String script) {

    this.fieldName = fieldName;
    this.id = fieldName;
    this.label = label;
    this.separator = separator;
    this.fieldCols = fieldCols;
    this.fieldRows = fieldRows;
    this.fieldClass = fieldClass;
    this.disabled = disabled;
    this.readOnly = readOnly;
    this.script = script;

  }


  public StringBuffer toHtmlStringBuffer() {

    ClientEntry field = null;
    if (pageState != null && pageState.getClientEntries() != null)
      field = pageState.getClientEntries().getEntry(fieldName);

    if (field == null)
      field = new ClientEntry(null, null);

    StringBuffer sb = new StringBuffer(512);

    if (label == null){
      label = I18n.get(fieldName);
    }
    if(showLabel)
      sb.append(label);

    /*if (required && label.indexOf("*") == -1)
      sb.append('*');*/


    sb.append(separator);

    sb.append("<textarea ").append(" name=\"").append(fieldName).append('\"').append(" id=\"").append(id).append('\"');
    if (fieldCols > 0) sb.append(" COLS=\"").append(fieldCols).append('\"');
    if (fieldRows > 0) sb.append(" ROWS=\"").append(fieldRows).append('\"');

    if (fieldClass == null || fieldClass.trim().length() == 0) {
      fieldClass = "formElements";
    }

    if (JSP.ex(innerLabel))
      sb.append(" innerLabel=\""+innerLabel+"\"");

    sb.append(" class=\"").append(fieldClass).append('\"');

    if (tabIndex != 0) {
      sb.append(" tabindex=\"").append(tabIndex).append('\"');
    }

    sb.append(disabled ? " disabled" : "");
    sb.append(readOnly ? " readonly" : "");

    sb.append(JSP.ex(script) ? ' ' + script : "");

    if (toolTip != null && toolTip.trim().length() > 0) 
      sb.append(" title=\"").append(getToolTip()).append('\"');

    if (required)
      sb.append(" required=\"true\" ");

//    if (excludeFromAlert)
//      sb.append(" excludeFromAlert=\"true\" ");

    String displayedValue = JSP.htmlEncode(field.stringValueNullIfEmpty());

//    if (preserveOldValue)
//       sb. append(" oldValue=\"").append(JSP.w(JSP.ex(value.errorCode) ? "" :displayedValue)).append('\"');
    if (preserveOldValue && !JSP.ex(value.errorCode))
        sb.append(" oldValue='1'");

    if (maxlength>0){
      sb.append(" maxlength=" + maxlength + " onKeyUp=\"limitSize(this);\" onKeyDown=\"limitSize(this);\" onBlur=\"limitSize(this);\"");

    }

    sb.append('>');
    if (field.stringValueNullIfEmpty() != null) {
      sb.append(displayedValue);
    }

    sb.append("</textarea>");

    if (autogrow){
      sb.append("<script>$(document).ready (function(){ initialize(contextPath+'/commons/js/jquery/jquery.autogrow.js',true); $('#"+id+"')" +
              ".autogrow({"+
              (maxHeight>0?"maxHeight: "+maxHeight:"")+
              (minHeight>0? (maxHeight>0?",":"")+"minHeight: "+minHeight:"")+
              (lineHeight>0? ((maxHeight+minHeight>0?",":""))+"lineHeight: "+lineHeight:"")+
              "});});</script>");
    }
    
    return sb;
  }

   public void toHtml(PageContext pageContext) {
      PageState ps = PageState.getCurrentPageState();
      this.pageState = ps;
      super.toHtml(pageContext);
  }

  /**
   *
   * @param minHeight use 0 if no limit
   * @param maxHeight use 0 if no limit
   * @param lineHeight use 0 for default
   */
  public void setAutogrow( int minHeight,int maxHeight,int lineHeight){
    autogrow=true;
    this.minHeight=minHeight;
    this.maxHeight=maxHeight;
    this.lineHeight=lineHeight;
  }


}

