<%@ page import="org.jblooming.operator.Operator,
                    org.jblooming.persistence.PersistenceHome,
                    org.jblooming.utilities.JSP,
                    org.jblooming.waf.ScreenBasic,
                    org.jblooming.waf.html.button.ButtonSubmit,
                    org.jblooming.waf.html.input.SmartCombo,
                    org.jblooming.waf.html.state.Form,
                    org.jblooming.waf.view.PageSeed,
                    org.jblooming.waf.view.PageState, org.jblooming.waf.AccessControlFilter" %><%
PageState pageState = PageState.getCurrentPageState(request);

Operator logged = pageState.getLoggedOperator();
Operator realOperator = (Operator)pageState.sessionState.getAttribute("__REAL_OPERATOR__");
boolean impersonate = realOperator != null;

if(logged == null) {
    pageState.sessionState.setLoginPendingUrl(pageState);
    String loginPage = request.getContextPath() + AccessControlFilter.LOGIN_PAGE_PATH_FROM_ROOT;
    response.sendRedirect(loginPage);
    return;
}

if (!logged.hasPermissionAsAdmin() && (impersonate && !realOperator.hasPermissionAsAdmin()))
    throw new SecurityException("Hahahahahahahaha!!!!");

if (!pageState.screenRunning) {
    pageState.screenRunning = true;
    ScreenBasic.preparePage(pageContext);
    pageState.perform(request, response);

    if("LOGOUT".equals(pageState.getCommand()) && impersonate) {
        pageState.sessionState.setAttribute("__REAL_OPERATOR__", null);
        pageState.sessionState.setLoggedOperator(realOperator);
    } else if("LOGIN".equals(pageState.getCommand())) {
        Operator newOperator = (Operator)PersistenceHome.findByPrimaryKey(Operator.class, pageState.getEntry("AVAILABLE_OPERATOR").intValueNoErrorCodeNoExc());
        if(newOperator != null) {
            if(!impersonate) {
                pageState.sessionState.setAttribute("__REAL_OPERATOR__", logged);
                System.out.println("set real");
            }
            pageState.sessionState.setLoggedOperator(newOperator);
        }
    }

    pageState.toHtml(pageContext);
} else {
    PageSeed self = pageState.thisPage(request);
    Form f = new Form(self);
    pageState.setForm(f);
    f.start(pageContext);

    if(impersonate) {
        %>Logged with: <%=JSP.w(logged.getLoginName())%> - <%=JSP.w(logged.getDisplayName())%><%
        ButtonSubmit logoutFromFakeUser = new ButtonSubmit("Logout", "LOGOUT", f);
        logoutFromFakeUser.toHtml(pageContext);
        %><br><%
    }

    SmartCombo availableUser = new SmartCombo();
    availableUser.fieldName = "AVAILABLE_OPERATOR";
    availableUser.label = "Available Operator";
    availableUser.hql = "select p.id, p.loginName from " + Operator.class.getName() + " as p";
    availableUser.whereForFiltering = "where p.enabled = true and p.hidden = false " +
            " and (upper(p.loginName) like upper(:" + SmartCombo.FILTER_PARAM_NAME + "))" +
            " order by p.loginName";
    availableUser.whereForId = "where p.id = :" + SmartCombo.FILTER_PARAM_NAME;
    availableUser.required = false;
    availableUser.maxLenght = 60;
    availableUser.iframe_width = 380;
    availableUser.separator = "&nbsp;";
    availableUser.toHtml(pageContext);

    ButtonSubmit loginWithFakeUser = new ButtonSubmit("Login with this Operator", "LOGIN", f);
    loginWithFakeUser.toHtml(pageContext);
    
    f.end(pageContext);
}
%>