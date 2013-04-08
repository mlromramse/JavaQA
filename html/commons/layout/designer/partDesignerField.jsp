<%@ page import="org.jblooming.designer.DesignerField, org.jblooming.ontology.Identifiable, org.jblooming.ontology.LookupSupport, org.jblooming.ontology.PersistentFile,
                 org.jblooming.persistence.PersistenceHome, org.jblooming.remoteFile.Document, org.jblooming.utilities.*, org.jblooming.waf.constants.Fields, org.jblooming.waf.html.core.JspIncluderSupport,
                 org.jblooming.waf.html.display.Img, org.jblooming.waf.html.display.MultimediaFile, org.jblooming.waf.html.input.*, org.jblooming.waf.view.PageState, java.util.Currency,
                 java.util.Date, java.util.List"%><%

  
PageState pageState = PageState.getCurrentPageState();
DesignerField.Drawer fd = (DesignerField.Drawer) JspIncluderSupport.getCurrentInstance(request);
DesignerField designerField = fd.designerField;

String name = DesignerField.DESIGNER_FIELD_PREFIX + designerField.name;
String separator = (designerField.separator == null) ? "</td><td>" : designerField.separator;
Class type = Class.forName(designerField.kind);
List classes = ReflectionUtilities.getInheritedClasses(type);
// -----------------  SMART COMBO DEFINED BY USER ------------------------
if (designerField.smartCombo != null) {
  designerField.smartCombo.fieldName = DesignerField.DESIGNER_FIELD_PREFIX + designerField.smartCombo.fieldName;
  designerField.smartCombo.label = designerField.label;
  designerField.smartCombo.readOnly = designerField.readOnly;//designer.readOnly || designerField.readOnly;
  designerField.smartCombo.required = designerField.required;
  designerField.smartCombo.separator = separator;
  designerField.smartCombo.preserveOldValue=designerField.preserveOldValue;
  if(designerField.maxLength > 0)
    designerField.smartCombo.maxLenght = designerField.maxLength;
  designerField.smartCombo.toHtml(pageContext);
}else if(designerField.jspHelper!=null){
  if(!designerField.exportable){//!designer.exportable){
  designerField.jspHelper.id=name;
  designerField.jspHelper.toHtml(pageContext);
  }
}else {
  // -----------------  STRING  ------------------------
  if (type.equals(String.class)) {
    if(designerField.exportable) {
       %><%=designerField.label%><%=separator%><%
       String value = pageState.getEntry(DesignerField.DESIGNER_FIELD_PREFIX+designerField.name).stringValue();
       %><b><%=StringUtilities.replaceAllNoRegex((value != null ? value : ""),"\n","<br>")%></b><%
    } else {
      if (designerField.rowsLength > 1 || (designerField.maxLength > 80 && designerField.fieldSize < 1)) { //se non è stata inserita una formattazione per il testo
        int rows = 5;                                                                                      //se la maxLenght supera 80 si crea una text area 5X80
        int cols = 80;
        if (designerField.rowsLength > 1)
          rows = designerField.rowsLength;
        if (designerField.fieldSize > 0)
          cols = designerField.fieldSize;
        TextArea ta = new TextArea(designerField.label, name, separator, cols, rows, "", false, false, designerField.script);
        ta.readOnly = designerField.readOnly;
        ta.required = designerField.required;
        ta.preserveOldValue=designerField.preserveOldValue;

        ta.toHtml(pageContext);
      } else {
        int size = 40;    //senza info esterse in textField è lungo 40
        if (designerField.fieldSize > 0)
          size = designerField.fieldSize;
        else if (designerField.maxLength > 0)
          size = designerField.maxLength; //se non ci sono info su fieldSize ma ci sono sul maxLenght si usano queste
        TextField tf = new TextField(designerField.label, name, separator,size, designerField.readOnly);
        if(designerField.maxLength>0)
          tf.maxlength = designerField.maxLength;
        tf.autoSize = designerField.autoSize;
        tf.required = designerField.required;
        tf.fieldSize = designerField.fieldSize;
        tf.script=designerField.script;
        tf.preserveOldValue=designerField.preserveOldValue;

        tf.toHtml(pageContext);
      }
    }

  // -----------------  DATE  ------------------------
  } else if (classes.contains(Date.class)) {
    if(designerField.exportable) {
%><%=designerField.label%><%=separator%><%
    String value = pageState.getEntry(DesignerField.DESIGNER_FIELD_PREFIX+designerField.name).stringValue();
%><b><%=(value != null ? value : "")%></b><%
    } else {
      DateField datefield = new DateField(name, pageState);
      datefield.size = 10;
      datefield.setSearchField(false);
      datefield.labelstr = designerField.label;
      datefield.separator = designerField.separator;
      datefield.readOnly = designerField.readOnly;
      datefield.required = designerField.required;
      datefield.preserveOldValue=designerField.preserveOldValue;

      datefield.toHtml(pageContext);
    }

  // -----------------  INTEGER  ------------------------
  } else if (classes.contains(Integer.class) || classes.contains(int.class)) {
    if(designerField.exportable) {
%><%=designerField.label%><%=separator%><%
  String value = pageState.getEntry(DesignerField.DESIGNER_FIELD_PREFIX+designerField.name).stringValue();
%><b><%=(value != null ? value : "")%></b><%
    } else {
      int columnLength = designerField.fieldSize;
      if (classes.contains(int.class)) columnLength = 4;
      TextField tf = TextField.getIntegerInstance(name);
      tf.separator=separator;
      tf.label = designerField.label;
      tf.readOnly = designerField.readOnly;
      tf.required = designerField.required;
      if(designerField.maxLength > 0)
        tf.maxlength = designerField.maxLength;
      tf.fieldSize = columnLength;
      tf.script = designerField.script;
      tf.preserveOldValue=designerField.preserveOldValue;
      tf.toHtml(pageContext);
    }

  // -----------------  DOUBLE  ------------------------
  } else if (classes.contains(Double.class)  || classes.contains(double.class)) {
    if(designerField.exportable) {
%><%=designerField.label%><%=separator%><%
  String value = pageState.getEntry(DesignerField.DESIGNER_FIELD_PREFIX+designerField.name).stringValue();
%><b><%=(value != null ? value : "")%></b><%
    } else {
      int columnLength = designerField.fieldSize;
      if (classes.contains(int.class)) columnLength = 4;
      TextField tf = TextField.getDoubleInstance(name);
      tf.separator=separator;
      tf.label = designerField.label;
      tf.readOnly = designerField.readOnly;
      tf.required = designerField.required;
      if(designerField.maxLength > 0)
        tf.maxlength = designerField.maxLength;
      tf.fieldSize = columnLength;
      tf.script = designerField.script;
      tf.preserveOldValue=designerField.preserveOldValue;

      // activate compute on return key via ajax
      tf.addKeyPressControl(13,"this.value=getContent('"+request.getContextPath()+"/commons/tools/mathEval.jsp?EVAL='+encodeURIComponent(this.value));this.focus();return false;", "onkeyup");

      tf.toHtml(pageContext);
    }

  // -----------------  CURRENCY  ------------------------
  } else if (classes.contains(Currency.class)) {
    if(designerField.exportable) {
%><%=designerField.label%><%=separator%><%
  String value = pageState.getEntry(DesignerField.DESIGNER_FIELD_PREFIX + designerField.name).stringValue();
%><b><%=(value != null ? value : "")%></b><%
    } else {
      int columnLength = designerField.fieldSize;
      if (classes.contains(int.class)) columnLength = 4;
      TextField tf = TextField.getCurrencyInstance(name);
      tf.separator=separator;
      tf.label = designerField.label;
      tf.readOnly = designerField.readOnly;
      tf.required = designerField.required;
      if(designerField.maxLength > 0)
        tf.maxlength = designerField.maxLength;
      tf.fieldSize = columnLength;
      tf.script = designerField.script;
      tf.preserveOldValue=designerField.preserveOldValue;

      // activate compute on return key via ajax
      //tf.addKeyPressControl(13,"this.value=getContent('"+request.getContextPath()+"/commons/tools/mathEval.jsp?EVAL='+encodeURIComponent(this.value));this.focus();return false;", "onkeyup");

      tf.toHtml(pageContext);
    }

    //------------- Document  ----------------
} else if (classes.contains(Document.class)) {
    // recupero l'id del file storage
    String docContent = pageState.getEntry(DesignerField.DESIGNER_FIELD_PREFIX + designerField.name).stringValueNullIfEmpty();
    if (docContent == null)
        docContent = designerField.initialValue;
    String referralObjectId = null;
    if (docContent != null && docContent.startsWith("RF")) {
        String string = docContent.substring(2);
        String[] valori = string.split(":");
        if (valori != null && valori.length > 0) {
            referralObjectId = valori[0];
        }
    }

    UrlFileStorage ufs = new UrlFileStorage(name);
    ufs.separator = designerField.separator;
    ufs.label = "";
    ufs.initialValue = designerField.initialValue;
    ufs.downloadOnly = designerField.readOnly;
    //ufs.preserveOldValue=designerField.preserveOldValue;

    if (JSP.ex(designerField.urlFileStorage_urlToInclude))
        ufs.urlToInclude = designerField.urlFileStorage_urlToInclude;
    ufs.referralObjectId = docContent == null ? PersistenceHome.NEW_EMPTY_ID : referralObjectId;
    ufs.toHtml(pageContext);

  // -----------------  LOOKUP  ------------------------
  } else if (classes.contains(LookupSupport.class)) {
    if(designerField.exportable) {
%><%=designerField.label%><%=separator%><%
      String value = pageState.getEntry(DesignerField.DESIGNER_FIELD_PREFIX+designerField.name).stringValue();
      if(value != null && !"".equals(value)) {
        LookupSupport filter = (LookupSupport)PersistenceHome.findByPrimaryKey(type,value);
%><b><%=filter.getDescription()%></b><%
      }
    } else {
      String hql = "select p.id, p.description from " + type.getName() + " as p";
      String whereForFiltering = "where p.description like :" + SmartCombo.FILTER_PARAM_NAME + " order by p.description";
      String whereForId = "where p.id = :" + SmartCombo.FILTER_PARAM_NAME;
      SmartCombo filter = new SmartCombo(name, hql, whereForFiltering, whereForId);
      filter.label = designerField.label;
      filter.separator = separator;
      filter.classic = designerField.classic;
      if(designerField.maxLength > 0)
        filter.maxLenght = designerField.maxLength;
      filter.readOnly = designerField.readOnly;
      filter.disabled = designerField.readOnly;
      filter.required = designerField.required;
      filter.script = designerField.script;
      filter.preserveOldValue=designerField.preserveOldValue;
      filter.firstEmpty=true;
      filter.toHtml(pageContext);
    }

  // -----------------  IDENTIFIABLE  ------------------------
  } else if (classes.contains(Identifiable.class)) {
    if(designerField.exportable) {
%><%=designerField.label%><%=separator%><%
  String value = pageState.getEntry(DesignerField.DESIGNER_FIELD_PREFIX+designerField.name).stringValue();
        if(value != null && !"".equals(value)) {
          Identifiable identifiable = PersistenceHome.findByPrimaryKey(type,value);
%><b><%=identifiable.getName()%></b><%
        }
    } else {
      String hql = "select p.id, p.name from " + type.getName() + " as p";
      String whereForFiltering = "where p.name like :" + SmartCombo.FILTER_PARAM_NAME + " order by p.name";
      String whereForId = "where p.id = :" + SmartCombo.FILTER_PARAM_NAME;
      SmartCombo filter = new SmartCombo(name, hql, whereForFiltering, whereForId);
      filter.label = designerField.label;
      filter.separator = separator;
      filter.classic = designerField.classic;
      filter.readOnly = designerField.readOnly;
      filter.disabled = designerField.readOnly;
      filter.required = designerField.required;
      filter.preserveOldValue=designerField.preserveOldValue;

      if(designerField.maxLength > 0)
        filter.maxLenght = designerField.maxLength;
      filter.script = designerField.script;

      filter.toHtml(pageContext);
    }

  // -----------------  PERSISTENT FILE  ------------------------
  } else if (classes.contains(PersistentFile.class)) {
    if(designerField.exportable) {
%><%=designerField.label%><%=separator%><%
  String value = pageState.getEntry(DesignerField.DESIGNER_FIELD_PREFIX+designerField.name).stringValue();
          if(value != null && !"".equals(value)) {
        PersistentFile pf =PersistentFile.deserialize(value);
        if(pf != null) {
%><%=pf.getName()%><%
        }
  }
    } else {
        if(designerField.readOnly) {
%><%=designerField.label%><%=separator%><%
          String value = pageState.getEntry(DesignerField.DESIGNER_FIELD_PREFIX+designerField.name).stringValue();
              if(value != null && !"".equals(value)) {
            PersistentFile pf =PersistentFile.deserialize(value);
            if(pf != null) {
              MultimediaFile mf = new MultimediaFile(pf,request);
                    mf.script = designerField.script;

              mf.toHtml(pageContext);
            }
              }
        } else {
          Uploader upl = new Uploader(name, pageState);
          upl.separator = separator;
          upl.label = designerField.label;
          upl.size = designerField.maxLength;
          upl.required = designerField.required;
          //upl.preserveOldValue=designerField.preserveOldValue;

          upl.toHtml(pageContext);
        }
    }

  // -----------------  BOOLEAN  ------------------------
  } else if (type.equals(Boolean.class) || "boolean".equals(type.toString())) {
    if(designerField.exportable) {
%><%=designerField.label%><%=separator%><%
  String value = pageState.getEntry(DesignerField.DESIGNER_FIELD_PREFIX+designerField.name).stringValue();
          if(value != null && !"".equals(value)) {
        if (designerField.displayAsCombo) {
          if(Fields.TRUE.equals(value)) {
%><b><%=I18n.get("TRUE")%></b><%
          } else if(Fields.FALSE.equals(value)) {
%><b><%=I18n.get("FALSE")%></b><%
          }
        } else {
          if(Fields.TRUE.equals(value)) {
            Img img = new Img(pageState.getSkin().imgPath + "list/checked.gif", I18n.get("TRUE"));
                    img.script = designerField.script;
            img.toHtml(pageContext);
          } else if(Fields.FALSE.equals(value)) {
            Img img = new Img(pageState.getSkin().imgPath + "list/unchecked.gif", I18n.get("TRUE"));
                    img.script = designerField.script;
            img.toHtml(pageContext);
          }
        }
    }
    } else {
      if (designerField.displayAsCombo) {
        CodeValueList cvl = new CodeValueList();
        cvl.addChoose(pageState);
        if (designerField.usedComboForSearch)
          cvl.add("ALL", I18n.get("ALL_MASCULINE"));
        if (designerField.useEmptyForAll)
          cvl.add("ALL", "");
        cvl.add(Fields.TRUE, I18n.get("TRUE"));
        cvl.add(Fields.FALSE, I18n.get("FALSE"));
        Combo boolC = new Combo(name, separator, "", 10, null, cvl, "");
        boolC.disabled =  designerField.readOnly;
        boolC.required = designerField.required;
        boolC.label = designerField.label;
        boolC.preserveOldValue=designerField.preserveOldValue;
        boolC.script = designerField.script;
        boolC.toHtml(pageContext);
      } else {
        CheckField cb = new CheckField(name, separator,designerField.putLabelFirst);
        cb.label = designerField.label;
        cb.disabled =  designerField.readOnly;
        cb.script = designerField.script;
        cb.preserveOldValue=designerField.preserveOldValue;
        cb.toHtml(pageContext);
      }
    }

  // -----------------  CODE VALUE  ------------------------
  } else if (type.equals(CodeValue.class)) {
    if(designerField.exportable) {
%><%=designerField.label%><%=separator%><%
  String value = pageState.getEntry(DesignerField.DESIGNER_FIELD_PREFIX+designerField.name).stringValue();
    if(value != null && !"".equals(value)) {
        if(designerField.cvl != null && designerField.cvl.keySet().contains(value)) {
%><b><%=designerField.cvl.get(value)%></b><%
        }
      }
    } else {
      if(designerField.displayAsCombo) {
        CodeValueList cvl = new CodeValueList();
        cvl.addAll(designerField.cvl);
        cvl.addChoose(pageState);
        Combo boolC = new Combo(name, separator, "", designerField.fieldSize, null, cvl, "");
        boolC.readOnly = designerField.readOnly;
        boolC.required = designerField.required;
        boolC.label = designerField.label;
        boolC.script = designerField.script;
        boolC.preserveOldValue=designerField.preserveOldValue;
        boolC.toHtml(pageContext);
      } else {
        if(designerField.cvl != null && designerField.cvl.size()>0) {
  %><%=designerField.label+(designerField.label != null && !"".equals(designerField.label) && designerField.required ? "*":"")+separator%>
  <table class="table"><tr><%
          for(CodeValue cv : designerField.cvl.getList()) {
  %>
  <td>
  <%
            RadioButton rb = new RadioButton(cv.value,name,cv.code,"&nbsp;","",false,"",pageState);
            rb.disabled = designerField.readOnly;
            rb.required = designerField.required;
            rb.script = designerField.script;
            rb.preserveOldValue=designerField.preserveOldValue;

            rb.toHtml(pageContext);
  %>
  </td>
  <%
          }
  %>
  </tr></table>
  <%

        }
      }
    }
  } else {
    %>Unhandled field: <%=designerField.label%>  <%=separator%> type: <%=type.getName()%><%
  }
}
%><%--} else if (fieldFeature.pageSeed!=null) {
      fieldFeature.pageSeed.addClientEntry("PARENT_ID",pageState.mainObject.getIntId());
      fieldFeature.pageSeed.addClientEntry("PARENT_CLASS",Hibernate.getClass(pageState.mainObject).getName());
      fieldFeature.pageSeed.addClientEntry("PARENT_PROPERTY",fieldFeature.propertyName);
      fieldFeature.pageSeed.addClientEntry("PARENT_URL",request.getContextPath()+"/"+pageState.href);
      ButtonLink bl = ButtonLink.getTextualInstance(fieldFeature.fieldName,fieldFeature.pageSeed);
      Object value = ReflectionUtilities.getFieldValue(fieldFeature.propertyName, pageState.mainObject);

      //if value is not null, activate link with edit
      if (value!=null) {
        fieldFeature.pageSeed.setCommand(Commands.EDIT);
        fieldFeature.pageSeed.setMainObjectId(((Identifiable)value).getIntId());

      //otherwise with add
      } else {
        fieldFeature.pageSeed.setCommand(Commands.ADD);
      }
      %><%=I18n.get("LINK_TO")%>:<%=separator%><%bl.toHtml(pageContext);%><% --%><%

      %>