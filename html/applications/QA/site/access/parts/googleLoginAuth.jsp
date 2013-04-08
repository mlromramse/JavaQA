<%@ page import="com.QA.QAOperator,com.QA.waf.QALoginAction,org.jblooming.tracer.Tracer,org.jblooming.utilities.JSP,
                 org.jblooming.waf.exceptions.ActionException,org.jblooming.waf.settings.ApplicationState,org.jblooming.waf.view.PageSeed,org.jblooming.waf.view.PageState,
                 org.openid4java.OpenIDException,org.openid4java.consumer.ConsumerManager,org.openid4java.consumer.VerificationResult,org.openid4java.discovery.DiscoveryInformation, org.openid4java.discovery.Identifier,
                 org.openid4java.message.AuthSuccess, org.openid4java.message.ParameterList, org.openid4java.message.ax.AxMessage, org.openid4java.message.ax.FetchResponse, java.util.List" %><%@page pageEncoding="UTF-8" %><%

  PageState pageState = PageState.getCurrentPageState();

  PageSeed redirTo = pageState.pageInThisFolder("login.jsp", request);
  ConsumerManager manager = (ConsumerManager) pageContext.getAttribute("consumermanager", PageContext.APPLICATION_SCOPE);
  try {
    // --- processing the authentication response

    // extract the parameters from the authentication response
    // (which comes in as a HTTP request from the OpenID provider)
    ParameterList responselist = new ParameterList(request.getParameterMap());

    // retrieve the previously stored discovery information
    DiscoveryInformation discovered = (DiscoveryInformation) session.getAttribute("openid-disco");

    // extract the receiving URL from the HTTP request
    StringBuffer receivingURL = request.getRequestURL();
    String queryString = request.getQueryString();
    if (queryString != null && queryString.length() > 0)
      receivingURL.append("?").append(request.getQueryString());

    // verify the response; ConsumerManager needs to be the same
    // (static) instance used to place the authentication request
    VerificationResult verification = manager.verify(receivingURL.toString(), responselist, discovered);

    // examine the verification result and extract the verified identifier
    Identifier verified = verification.getVerifiedId();

    if (verified != null) {
      String email = "";
      AuthSuccess authSuccess = (AuthSuccess) verification.getAuthResponse();
      if (authSuccess.hasExtension(AxMessage.OPENID_NS_AX)) {
        FetchResponse fetchResp = (FetchResponse) authSuccess.getExtension(AxMessage.OPENID_NS_AX);

        List emails = fetchResp.getAttributeValues("email");
        email = (String) emails.get(0);
        if(!JSP.ex(email))
          throw new ActionException("GOOGLE_DOES_NOT_ANSWER");
      }

      String openid = authSuccess.getIdentity();
      QALoginAction.enrollWithGoogle(pageState, openid, email, request, response);
      redirTo = QALoginAction.magicalRedir((QAOperator) pageState.getLoggedOperator(), pageState);


    } else {
      // login invalid
      // go to login again with error as ce
      redirTo.addClientEntry("OPENID_ERROR_CODE", "Not authenticated.");
    }

    response.sendRedirect(redirTo.toLinkToHref());

  } catch (OpenIDException e) {
    // something wrong
    // go to login.jsp with error
    Tracer.platformLogger.error(e);
    response.sendRedirect(ApplicationState.contextPath + "/applications/QA/site/access/login.jsp");
  }




%>