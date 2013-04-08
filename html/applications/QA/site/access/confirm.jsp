<%@ page import="com.QA.QAOperator, com.QA.waf.QAScreenApp,
                 org.jblooming.utilities.JSP, org.jblooming.utilities.StringUtilities, org.jblooming.waf.ScreenArea, org.jblooming.waf.constants.Fields,
                 org.jblooming.waf.exceptions.ActionException, org.jblooming.waf.settings.I18n, org.jblooming.waf.view.ClientEntry, org.jblooming.waf.view.PageSeed, org.jblooming.waf.view.PageState" %><%


  PageState pageState = PageState.getCurrentPageState();

  if (!pageState.screenRunning) {
    pageState.screenRunning = true;
    final ScreenArea body = new ScreenArea(request);
    QAScreenApp lw = new QAScreenApp(body);
    lw.menu = null;
    lw.register(pageState);
    pageState.perform(request, response);

    try {
      if ("CF".equals(pageState.command)) {
        ClientEntry cke = pageState.getEntryAndSetRequired("CK");
        String ck = cke.stringValue();
        int uid = pageState.getEntryAndSetRequired("UID").intValue();

        boolean doRedir = true;
        boolean addEntry = false;
        QAOperator operator = QAOperator.load(uid);
        if (operator == null || !ck.equalsIgnoreCase(StringUtilities.md5Encode(operator.getId() + operator.getUnverifiedEmail() + "vivazoe"))) {
          pageState.addClientEntry("PAGE_MESSAGE", "User doesn't exist!");
          doRedir = false;
        }

        if (operator!=null && JSP.ex(operator.getUnverifiedEmail()) && !operator.getUnverifiedEmail().equals(operator.getEmail())) {
          QAOperator lop =  QAOperator.loadByEmail(operator.getUnverifiedEmail());
          if (lop==null || operator.equals(lop)) {
            operator.setConfirmedEmail(operator.getUnverifiedEmail());
            operator.store();
            addEntry = true;
            
          } else {
            pageState.addClientEntry("PAGE_MESSAGE", "E-mail has already been confirmed: " + operator.getUnverifiedEmail() +".");
          }
        }

        if(doRedir) {
          PageSeed index = pageState.pageFromRoot("user/subscriptions.jsp");
          if(addEntry)
            index.addClientEntry("fromEmailConfirm", Fields.TRUE);
          response.sendRedirect(index.toLinkToHref());
          return;
        }

      // USER DOESN'T WANT TO RECEIVE EMAILS ANY LONGER
      } else if("UNSCRIBE".equals(pageState.command)) {
        ClientEntry cke = pageState.getEntryAndSetRequired("CK");
        String ck = cke.stringValue();
        int uid = pageState.getEntryAndSetRequired("UID").intValue();
        QAOperator operator = QAOperator.load(uid);
        if (operator == null || !ck.equalsIgnoreCase(StringUtilities.md5Encode(operator.getId() + operator.getDefaultEmail() + "vivazoe"))) {
          pageState.addClientEntry("PAGE_MESSAGE", "User doesn't exist!");

        } else {
          operator.getOptions().put("SEND_NOTIF_BY_EMAIL",Fields.FALSE);
          operator.store();
          pageState.addClientEntry("PAGE_MESSAGE", I18n.g("MAIL_NOTIF_UNSUBSCRIBED"));
        }

      // USER WANT TO RECEIVE EMAILS AGAIN
      } else if("SCRIBE".equals(pageState.command)) {
        ClientEntry cke = pageState.getEntryAndSetRequired("CK");
        String ck = cke.stringValue();
        int uid = pageState.getEntryAndSetRequired("UID").intValue();
        QAOperator operator = QAOperator.load(uid);
        if (operator == null || !ck.equalsIgnoreCase(StringUtilities.md5Encode(operator.getId() + operator.getDefaultEmail() + "vivazoe"))) {
          pageState.addClientEntry("PAGE_MESSAGE", "User doesn't exist!");

        } else {
          operator.getOptions().put("SEND_NOTIF_BY_EMAIL",Fields.TRUE);
          operator.store();
          pageState.addClientEntry("PAGE_MESSAGE", I18n.g("MAIL_NOTIF_SUBSCRIBED"));
        }
      }

    } catch (ActionException e) {}

    pageState.toHtml(pageContext);

  } else {

    %><div id="content"><%

    //do nothing  a redirect should be pending
    String message = pageState.getEntry("PAGE_MESSAGE").stringValueNullIfEmpty();
    if (JSP.ex(message)) {
      %> <div><h3><span><%=message%></span></h3></div> <%
    }
    %></div><%
  }
%>