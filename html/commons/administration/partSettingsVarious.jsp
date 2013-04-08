<%@ page import="org.jblooming.oql.QueryHelper,
                 org.jblooming.system.SystemConstants,
                 org.jblooming.utilities.StringUtilities,
                 org.jblooming.waf.constants.Commands,
                 org.jblooming.waf.html.input.CheckField,
                 org.jblooming.waf.settings.ApplicationState,
                 org.jblooming.waf.view.PageState" %><%
  
  PageState pageState = PageState.getCurrentPageState();
  if (!Commands.SAVE.equals(pageState.command)) {
    pageState.addClientEntry(QueryHelper.QBE_CONVERT_TO_UPPER, ApplicationState.getApplicationSetting(QueryHelper.QBE_CONVERT_TO_UPPER));
    pageState.addClientEntry(SystemConstants.SETUP_DB_UPDATE_DONE, ApplicationState.getApplicationSetting(SystemConstants.SETUP_DB_UPDATE_DONE));
    pageState.addClientEntry(SystemConstants.SETUP_NOTIFIED_ADMIN_WIZARDS, ApplicationState.getApplicationSetting(SystemConstants.SETUP_NOTIFIED_ADMIN_WIZARDS));
    pageState.addClientEntry(SystemConstants.AUDIT, ApplicationState.getApplicationSetting(SystemConstants.AUDIT));
  }

%><tr><th colspan="99">Advanced configuration - change only if sure</th></tr>
<tr><td><%

    CheckField cf = new CheckField(StringUtilities.replaceAllNoRegex(QueryHelper.QBE_CONVERT_TO_UPPER, "_", " ").toLowerCase(), QueryHelper.QBE_CONVERT_TO_UPPER, "</td><td>", true);
    cf.toolTip = QueryHelper.QBE_CONVERT_TO_UPPER;
    cf.toHtml(pageContext);

%></td>
  <td>yes</td>
  <td>
    This is useful for case sensitive databases, like Oracle, but for the others may be set to false and improve
    performance.
  </td>
</tr>
<tr><td><%

    cf = new CheckField(StringUtilities.replaceAllNoRegex(SystemConstants.SETUP_DB_UPDATE_DONE, "_", " ").toLowerCase(), SystemConstants.SETUP_DB_UPDATE_DONE, "</td><td>", true);
    cf.toolTip = SystemConstants.SETUP_DB_UPDATE_DONE;
    cf.toHtml(pageContext);

%></td>
  <td>&nbsp;</td>
  <td>If absent or "no", at start-up the application does a database schema verification, and eventually an update.</td>
</tr>
<tr>
   <td><%

     cf = new CheckField(StringUtilities.replaceAllNoRegex(SystemConstants.SETUP_NOTIFIED_ADMIN_WIZARDS, "_", " ").toLowerCase(), SystemConstants.SETUP_NOTIFIED_ADMIN_WIZARDS, "</td><td>", true);
     cf.toolTip = SystemConstants.SETUP_NOTIFIED_ADMIN_WIZARDS;
     cf.toHtml(pageContext);

   %></td>
  <td>&nbsp;</td>
  <td>Whether at launch the application has notified the admin of the existence of the wizards page.</td>
</tr>
