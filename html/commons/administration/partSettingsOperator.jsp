<%@ page import="org.jblooming.utilities.CodeValueList,
                 org.jblooming.utilities.JSP,
                 org.jblooming.waf.constants.Commands,
                 org.jblooming.waf.constants.Fields,
                 org.jblooming.waf.constants.OperatorConstants,
                 org.jblooming.waf.html.input.CheckField,
                 org.jblooming.waf.html.input.Combo,
                 org.jblooming.waf.html.input.ComboBox,
                 org.jblooming.waf.html.input.TextField, org.jblooming.waf.html.layout.Skin, org.jblooming.waf.settings.ApplicationState, org.jblooming.waf.view.PageState" %><%

  PageState pageState = PageState.getCurrentPageState();
  if (!Commands.SAVE.equals(pageState.command)) {
    pageState.addClientEntry(OperatorConstants.FLD_CURRENT_SKIN, ApplicationState.getApplicationSetting(OperatorConstants.FLD_CURRENT_SKIN));
    pageState.addClientEntry(OperatorConstants.OP_PAGE_SIZE, ApplicationState.getApplicationSetting(OperatorConstants.OP_PAGE_SIZE));
    pageState.addClientEntry(OperatorConstants.RECENT_VIEWS_SIZE, ApplicationState.getApplicationSetting(OperatorConstants.RECENT_VIEWS_SIZE));
    pageState.addClientEntry(OperatorConstants.FLD_WORKING_HOUR_BEGIN, ApplicationState.getApplicationSetting(OperatorConstants.FLD_WORKING_HOUR_BEGIN));
    pageState.addClientEntry(OperatorConstants.FLD_WORKING_HOUR_END, ApplicationState.getApplicationSetting(OperatorConstants.FLD_WORKING_HOUR_END));
    pageState.addClientEntry("SHOW_USER_SCORES", JSP.ex(ApplicationState.getApplicationSetting("SHOW_USER_SCORES")) ? ApplicationState.getApplicationSetting("SHOW_USER_SCORES") : Fields.TRUE);
  }

  %><tr><th colspan="99">Operator default options</th></tr><%


%><tr><td><%
    ComboBox start = ComboBox.getTimeInstance(OperatorConstants.FLD_HOUR_DAY_START, OperatorConstants.FLD_HOUR_DAY_START, pageState);
    start.toolTip = OperatorConstants.FLD_HOUR_DAY_START;
    start.separator = "</td><td>";
    start.toHtmlI18n(pageContext);

  %></td>
  <td>08:00</td>
  <td>Default start hour for agenda visualization.</td>
</tr>
<tr><td><%

    ComboBox end = ComboBox.getTimeInstance(OperatorConstants.FLD_HOUR_DAY_END, OperatorConstants.FLD_HOUR_DAY_END, pageState);
    end.separator = "</td><td>";
    start.toolTip = OperatorConstants.FLD_HOUR_DAY_END;
    end.toHtmlI18n(pageContext);
  %></td>
  <td>17:00</td>
  <td>Default end hour for agenda visualization.</td>
</tr>
<tr><td><%

    TextField textField = new TextField(OperatorConstants.OP_PAGE_SIZE, OperatorConstants.OP_PAGE_SIZE, "</td><td>", 2, false);
    textField.toolTip = OperatorConstants.OP_PAGE_SIZE;
    textField.toHtmlI18n(pageContext);

   %></td>
  <td>20</td>
  <td>Default page size.</td>
</tr>
<tr><td><%

    textField = new TextField("OPERATOR_RECENT_VIEWS_SIZE", OperatorConstants.RECENT_VIEWS_SIZE, "</td><td>", 2, false);
    textField.toolTip = OperatorConstants.RECENT_VIEWS_SIZE;
    textField.toHtmlI18n(pageContext);

  %></td>
  <td>10</td>
  <td>&nbsp;</td>
</tr>
<tr><td><%

    ComboBox whb = ComboBox.getTimeInstance(OperatorConstants.FLD_WORKING_HOUR_BEGIN, OperatorConstants.FLD_WORKING_HOUR_BEGIN, pageState);
    whb.separator = "</td><td>";
    whb.toHtmlI18n(pageContext);

  %></td>
  <td>09:00</td>
  <td>Default start hour for working day.</td>
</tr>
<tr>
  <td><%

    ComboBox whe = ComboBox.getTimeInstance(OperatorConstants.FLD_WORKING_HOUR_END, OperatorConstants.FLD_WORKING_HOUR_END, pageState);
    whe.separator = "</td><td>";
    whe.toolTip = OperatorConstants.FLD_WORKING_HOUR_END;
    whe.toHtmlI18n(pageContext);

  %></td>
  <td>18:00</td>
  <td>Default end hour for working day.</td>
</tr>
<tr>
  <td><%

    CheckField opSco = new CheckField("SHOW_USER_SCORES","</td><td>",true);
    opSco.toolTip = "SHOW_USER_SCORES";
    opSco.toHtmlI18n(pageContext);

  %></td>
  <td>yes</td>
  <td>Show user's scores whre possible.</td>
</tr>
