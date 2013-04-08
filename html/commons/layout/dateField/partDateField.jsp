<%@ page import="org.jblooming.utilities.DateUtilities,
                 org.jblooming.utilities.JSP,
                 org.jblooming.waf.SessionState,
                 org.jblooming.waf.constants.FieldErrorConstants,
                 org.jblooming.waf.html.core.JspIncluderSupport,
                 org.jblooming.waf.html.input.DateField,
                 org.jblooming.waf.html.input.TextField,
                 org.jblooming.waf.html.layout.Skin,
                 org.jblooming.waf.view.ClientEntry,
                 org.jblooming.waf.view.PageState,
                 java.text.ParseException,
                 java.text.SimpleDateFormat,
                 java.util.Date, org.jblooming.agenda.CompanyCalendar"%><%

    DateField dateField = (DateField)JspIncluderSupport.getCurrentInstance(request);
    StringBuffer inputScript=new StringBuffer();
    StringBuffer separ=new StringBuffer();
    PageState pageState = PageState.getCurrentPageState();
    SessionState sessionState = SessionState.getSessionState(request);
    Skin skin = sessionState.getSkin();


    if (!JSP.ex(dateField.dateFormat)){
      dateField.dateFormat=DateUtilities.getFormat(DateUtilities.DATE_SHORT);
    }


    if (dateField.className==null)
      dateField.className="formElements";
    if (dateField.separator==null || (dateField.labelstr!=null && dateField.labelstr.length()>0 && dateField.separator.length()==0))
      dateField.separator="</td><td>"; //

    separ.append( dateField.separator);
    separ.append("<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\"  valign=\"top\"><tr>");
    separ.append("<td valign=\"middle\" nowrap>");

      if(!dateField.isSearchField()) {
        inputScript.append(" onblur =\"if (!Date.isValid($(this).val(),'"+dateField.dateFormat+"')) {");

        inputScript.append("this.focus();return false;} ");

        if (dateField.onblurOnDateValid!=null)
          inputScript.append(" else{"+dateField.onblurOnDateValid+"}");

        inputScript.append("\" ");
      }

      if((dateField.disabled)||(dateField.readOnly) )
        dateField.readOnly=true;

      inputScript.append(JSP.w(dateField.script));
      inputScript.append(" format=\""+dateField.dateFormat+"\"");

      TextField tf = new TextField("text",
              dateField.labelstr,
              dateField.fieldName,
              separ.toString(), //dateField.labelstr!=null && dateField.labelstr.trim().length()>0 ? separ.toString() : "",
              dateField.size,
              false,
              dateField.readOnly,
              255,
              inputScript.toString());
      tf.searchField=dateField.isSearchField();
      tf.preserveOldValue = dateField.preserveOldValue;
      tf.disabled=dateField.disabled;
      tf.toolTip = dateField.toolTip;
      tf.fieldClass=dateField.className;
      tf.id=dateField.id;

        if(dateField.required)
            tf.required = true;

        if(JSP.ex(dateField.getKeyToHandle())&&JSP.ex(dateField.getLaunchedJsOnActionListened())&&JSP.ex(dateField.getActionListened()))
          tf.addKeyPressControl(dateField.getKeyToHandle(),dateField.getLaunchedJsOnActionListened(),dateField.getActionListened());
        tf.toHtml(pageContext);
        %> </td> <%
          %><td  valign="middle">&nbsp;</td>
            <td valign="middle">
              <span title="<%=I18n.get("DATEFIELDCALENDAR")%>" id ="<%=dateField.id%>s_inputDate"style="cursor:pointer; height:5;"
                    onclick="$(this).dateField({inputField:$('#<%=dateField.id%>'),dateFormat:'<%=dateField.dateFormat%>',isSearchField:<%=dateField.isSearchField()?"true":"false"%>});">
              <img src="<%=skin.imgPath%>datePicker/calendarLittle.gif" /></span>
            </td><%
      %></tr></table>