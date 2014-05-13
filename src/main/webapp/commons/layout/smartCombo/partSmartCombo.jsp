<%@ page import="org.jblooming.oql.OqlQuery,
                org.jblooming.tracer.Tracer,
                org.jblooming.utilities.JSP,
                org.jblooming.utilities.StringUtilities,
                org.jblooming.waf.SessionState,
                org.jblooming.waf.html.core.JspIncluderSupport,
                org.jblooming.waf.html.display.Img,
                org.jblooming.waf.html.input.SmartCombo,
                org.jblooming.waf.html.input.TextField,org.jblooming.waf.settings.PersistenceConfiguration,
                org.jblooming.waf.view.PageState,
                java.io.Serializable,
                java.util.List, org.jblooming.waf.settings.I18n"%><%

  PageState pageState = PageState.getCurrentPageState();
  SessionState sm = pageState.getSessionState();

  if (SmartCombo.INITIALIZE.equals(request.getAttribute(SmartCombo.ACTION))) { // Container INITIALIZE
    /* --------------------------------------- START INITIALIZE PART ----------------------------------------------------------------- */
    %>
<style>
  .trNormal{background-color:white;}
  .trSel{background-color:yellow;}
  .trHl{background-color:#eeeeee;font-style:italic;font-weight:bolder;}
  .comboTable td{border-bottom: 1px solid #e0e0e0;text-align:left;}
  .comboTable{background-color:white; }
</style>
<script type="text/javascript">
  $(function(){initialize(contextPath+"/commons/layout/smartCombo/partSmartCombo.js",true)});

  function scDropDownRowClicked(row){
    //console.debug("scDropDownRowClicked");
    var dropDown=row.parents(".cbDropDown:first");
    var hiddField=dropDown.nextAll("input:hidden:first");
    var txtField=dropDown.prevAll("input:text:first");
    txtField.val(row.attr('selectText'));
    hiddField.val(row.attr('selectValue'));
  }


</script>
<%

  } else if (SmartCombo.DRAW_INPUT.equals(request.getAttribute(SmartCombo.ACTION))) {
  SmartCombo smartCombo = (SmartCombo) JspIncluderSupport.getCurrentInstance(request);

    /* ---- define letsubmit function only when needed-------- */

    if (JSP.ex(smartCombo.onValueSelectedScript) || smartCombo.autoSubmit){
      %><script>
      function letSubmit<%=smartCombo.fieldName%>() {
        <%=JSP.w(smartCombo.onValueSelectedScript)%>
        <%if(smartCombo.autoSubmit){%>
          obj('<%=smartCombo.idForm%>').submit();
        <%}%>
      }
    </script><%
  }

    /* --------------------------------------- START TEXT FIELD PART ----------------------------------------------------------------- */

    sm.getAttributes().put(smartCombo.fieldName, smartCombo);
    // HIDDEN

  // 4/8/08 modifica da readonly=true a false  fatta da Matteo Bicocchi per problemi sul back e settaggio di cliententry su campi hidden
    TextField tfh = new TextField("hidden", "", smartCombo.fieldName, "", 10, false);
    if (smartCombo.required && !smartCombo.addAllowed) { // only if ! addAllowed the hidden part is required, otherwise is required the visible one
        tfh.required = true;
    }
    tfh.preserveOldValue = smartCombo.preserveOldValue;

    //if there is an id, get the value
    Serializable id = pageState.getEntry(smartCombo.fieldName).stringValueNullIfEmpty();
    if (JSP.ex(id)) {
      OqlQuery oqlForId;

      oqlForId = new OqlQuery(smartCombo.hql + " " + smartCombo.whereForId);

      // modified R&P for PosgreSql on  Nov 11, 2008:
     if (PersistenceConfiguration.getDefaultPersistenceConfiguration().dialect.equals(org.hibernate.dialect.PostgreSQLDialectDBBlobs.class))
      //if (PersistenceConfiguration.getDefaultPersistenceConfiguration().dialect.equals(PostgreSQLDialect.class))
        oqlForId.getQuery().setInteger(SmartCombo.FILTER_PARAM_NAME, Integer.parseInt(id+""));
      else
      oqlForId.getQuery().setString(SmartCombo.FILTER_PARAM_NAME, id+"");

      Object result = null;

      try {
        result = oqlForId.uniqueResultNullIfEmpty();
      } catch (Throwable e) {
        Tracer.platformLogger.error(e);
      }

      if (result != null) {
        Object o = ((Object[]) result)[smartCombo.columnToCopyInDescription];
        pageState.addClientEntry(smartCombo.fieldName + SmartCombo.TEXT_FIELD_POSTFIX, "" + o);
      }
    }

    // VISIBLE
    TextField tf = new TextField("text", smartCombo.label, smartCombo.fieldName + SmartCombo.TEXT_FIELD_POSTFIX, smartCombo.separator, smartCombo.maxLenght, smartCombo.disabled);
    tf.required = smartCombo.addAllowed && smartCombo.required; // only if add allowed the visible part is required, otherwise is required the hidden part
    tf.readOnly = smartCombo.readOnly;
    tf.disabled = smartCombo.disabled;
    tf.fieldClass = smartCombo.fieldClass;
    tf.label = smartCombo.label+(smartCombo.required?"*":"");
    tf.innerLabel=smartCombo.innerLabel;

    tf.preserveOldValue = false;

    if (!smartCombo.disabled && !smartCombo.readOnly) {
      StringBuffer script = new StringBuffer();
      script.append(" autocomplete=\"off\" onfocus=\"createDropDown($(this),"+smartCombo.iframe_width+","+smartCombo.iframe_height+"); refreshDropDown ($(this).nextAll('.cbDropDown'),this.value); setSelection(this,0,1024)\" ");
      script.append("onblur=\"finalizeOperation($(this).nextAll('.cbDropDown:first')," + smartCombo.required +","+ smartCombo.addAllowed + " );" +
          (smartCombo.onBlurAdditionalScript != null && smartCombo.onBlurAdditionalScript.trim().length() > 0 ? smartCombo.onBlurAdditionalScript : "") + "\"");
      if (smartCombo.script != null && smartCombo.script.trim().length() > 0)
        script.append(" "+smartCombo.script);

      // test 17/07/2006 introduced onkeyPress to prevent the submission of a form with 1 only sc.
      script.append(" onKeyUp=\"manageKeyEvent ($(this),event," + smartCombo.required +","+ smartCombo.addAllowed + ");\" onKeyPress=\"stopKeyEvent(event);\"");
      tf.script = script.toString();
    }

    Img open= new Img(pageState.getSkin().imgPath+"smartComboOpen.gif","");
    open.disabled = smartCombo.disabled || smartCombo.readOnly;
    if (!open.disabled) {
      open.script = " onClick=\" if ( $(this).prevAll('.cbDropDown:first').size()<=0) {$(this).prevAll('input:text:first').focus(); " +
          "} else { finalizeOperation($(this).prevAll('.cbDropDown:first')," + smartCombo.required +","+ smartCombo.addAllowed + " ); }\"";
    }


    tf.toHtml(pageContext);
    open.toHtml(pageContext);
    tfh.toHtml(pageContext);  //DO NOT CHANGE ORDER hidden is the last one in order to be easy to find it via jquery

    if (smartCombo.linkToEntity!=null) {
      if (JSP.ex(id)) {
        %><span id="<%=smartCombo.fieldName + SmartCombo.LINKENTITY_POSTFIX%>" class="<%=SmartCombo.LINKENTITY_POSTFIX%>"><%
        smartCombo.linkToEntity.setMainObjectId(id);
        smartCombo.linkToEntity.toHtml(pageContext);
        %></span><%
      }
    }

  } else {
    /* --------------------------------------- START DROP DOWN PART ----------------------------------------------------------------- */

    SmartCombo smartCombo = (SmartCombo) sm.getAttributes().get(request.getParameter("id"));
    if (smartCombo == null) {
      //here probably the sesssion is dead and there is no combo, show a error message
      %><%=I18n.get("SESSION_EXPIRED_REFRESH_PAGE")%><%

    } else {
      String filter = request.getParameter("filter");
      if (filter!=null)
        filter = StringUtilities.replaceAllNoRegex(filter, "\\", "\\\\");
      if (filter == null)
        filter = "";


      if (filter.length() > 0) {
        // remove last char if backspace
        if ("8".equals(request.getParameter("key"))) {
            filter = filter.substring(0, filter.length() - 1);
        }
      }

      if (smartCombo.convertToUpper)
        filter = filter.toUpperCase();

      // fill the list with the filter
      String hiddenValue=pageState.getEntry("hiddenValue").stringValueNullIfEmpty();
      List<Object[]> prs = smartCombo.fillResultList(filter,hiddenValue);


      if (prs != null) {

        if (prs.size() > 0) {

          %><table width="100%" border="0" class="comboTable" style="cursor:pointer"><%
          int row=1;
          for (Object[] value : prs) {
            String res1 = value[smartCombo.columnToCopyInDescription] + "";
            if (!smartCombo.useTableForResults) {
              res1 = "";
              for (int i = 1; i < value.length; i++) {
                res1 = res1 + JSP.w(value[i]) + " ";
              }
            }
            %><tr class="trNormal <%=smartCombo.highlightedIds.contains(value[0])?"trHl":""%>"
                  id="ROW_<%=row%>"
                  selectText="<%=JSP.htmlEncodeTag(JSP.htmlEncodeApexes(res1.trim()))%>"
                  selectValue="<%=JSP.javascriptEncode(JSP.w(value[0]))%>"
                  onMouseDown="scDropDownRowClicked($(this));">
                  <%=!smartCombo.useTableForResults ? "<td>" : ""%>
            <%
            for (int i = 1; i < value.length; i++) {
              %><%=smartCombo.useTableForResults ? "<td>" : ""%><%=JSP.cleanHTML(JSP.w(value[i]))%>&nbsp;<%=smartCombo.useTableForResults ? "</td>" : ""%><%
            }
            %><%=!smartCombo.useTableForResults ? "</td>" : ""%></tr><%
            row++;
          }

          if (row==2) {
            %><tr style="cursor:default"><td colspan="90" bgcolor="#f0f0f0"><small><i>(<%=I18n.get("RESULT_EXACTLY_ONE_HELP")%>)</i></small></td></tr><%
          }

          //this may get it wrong if prs.size() is exactly the total number foundable, but piuommen its ok
          if (smartCombo.maxRowToFetch<=prs.size()) {
            %><tr style="cursor:default"><td colspan="90" bgcolor="#f0f0f0"><i>...<%=I18n.get("RESULT_LIMITED_TO_%%",smartCombo.maxRowToFetch+"")%></i></td></tr><%
          }
          %></table><%
   
        }
      }
    }
  }
%>