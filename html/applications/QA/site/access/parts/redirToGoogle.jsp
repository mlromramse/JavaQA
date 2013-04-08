<%@ page import="com.QA.waf.QAScreenApp,org.jblooming.PlatformRuntimeException,org.jblooming.waf.ScreenArea,org.jblooming.waf.settings.ApplicationState,org.jblooming.waf.settings.I18n,org.jblooming.waf.view.PageSeed,
                 org.jblooming.waf.view.PageState,org.openid4java.consumer.ConsumerManager,org.openid4java.discovery.DiscoveryInformation,org.openid4java.message.AuthRequest,org.openid4java.message.ax.FetchRequest,org.openid4java.message.sreg.SRegRequest,
                 java.net.URL, java.util.List, java.util.Map" %><%@page pageEncoding="UTF-8" %><%

  PageState pageState = PageState.getCurrentPageState();

  if (!pageState.screenRunning) {
    pageState.screenRunning = true;
    final ScreenArea body = new ScreenArea(request);
    new QAScreenApp(body).register(pageState);
    pageState.perform(request, response);

    // README:
    // Set the returnToUrl string to the appropriate value for this JSP
    // Since you may be deployed behind apache, etc, the jsp has no real idea what the
    // absolute URI is to get back here.

    ConsumerManager manager = (ConsumerManager) pageContext.getAttribute("consumermanager", PageContext.APPLICATION_SCOPE);
    if (manager == null) {
      manager = new ConsumerManager();
      pageContext.setAttribute("consumermanager", manager, PageContext.APPLICATION_SCOPE);
    }
    String openid = pageState.getEntryAndSetRequired("OPENID").stringValue();

    try {
      if (!openid.toLowerCase().contains("google") && !openid.toLowerCase().contains("wordpress") )
        throw new PlatformRuntimeException("Actually, only Google and Wordpress are supported.");

      // determine a return_to URL where your application will receive
      // the authentication responses from the OpenID provider
      // YOU SHOULD CHANGE THIS TO GO TO THE

      URL url = new URL(request.getRequestURL() + "");
      String returnToUrl;
      if (url.getPort() == -1 || url.getDefaultPort() == url.getPort())
        returnToUrl = new URL(url.getProtocol(), url.getHost(), request.getContextPath() + "/applications/QA/site/access/parts/googleLoginAuth.jsp").toExternalForm();
      else
        returnToUrl = new URL(url.getProtocol(), url.getHost(), url.getPort(), request.getContextPath() + "/applications/QA/site/access/parts/googleLoginAuth.jsp").toExternalForm();


      // perform discovery on the user-supplied identifier
      List discoveries = manager.discover(openid);

      // attempt to associate with an OpenID provider
      // and retrieve one service endpoint for authentication
      DiscoveryInformation discovered = manager.associate(discoveries);

      // store the discovery information in the user's session
      session.setAttribute("openid-disco", discovered);

      // obtain a AuthRequest message to be sent to the OpenID provider
      AuthRequest authReq = manager.authenticate(discovered, returnToUrl);
      pageState.setAttribute("AuthRequest",authReq);

      // set the realm
      authReq.setRealm(ApplicationState.serverURL);

      // Attribute Exchange example: fetching the 'email' attribute
      FetchRequest fetch = FetchRequest.createFetchRequest();
      fetch.addAttribute("email", "http://schema.openid.net/contact/email", true);
      // attach the extension to the authentication request
      authReq.addExtension(fetch);

      SRegRequest sregReq = SRegRequest.createFetchRequest();
      sregReq.addAttribute("email", false);

      authReq.addExtension(sregReq);

      if (!discovered.isVersion2()) {
        // Option 1: GET HTTP-redirect to the OpenID Provider endpoint
        // The only method supported in OpenID 1.x
        // redirect-URL usually limited ~2048 bytes
        response.sendRedirect(authReq.getDestinationUrl(true));
      } else {
        pageState.setAttribute("needFormRedirection","1");
      }

    } catch (Throwable e) {
      PageSeed loginPage = pageState.pageFromRoot("site/access/login.jsp");
      loginPage.addClientEntry("OPENID_ERROR_CODE", e.getMessage());
      loginPage.addClientEntry("OPENID", openid);
      response.sendRedirect(loginPage.toLinkToHref());
    }
    pageState.toHtml(pageContext);

  } else {
    if ("1".equals(pageState.getAttribute("needFormRedirection")+"")) {
      AuthRequest authReq = (AuthRequest) pageState.getAttribute("AuthRequest");

%><div class="siteContainer">


  <div id="wrapper" class="inside">

    <h1><span>
      <%=I18n.g("QA_APP_NAME")%> redirecting...</span>
    </h1>

    <div id="wsAccess">
      <form id="openid-form-redirection" action="<%= authReq.getOPEndpoint() %>" method="post" accept-charset="utf-8"><%

        Map pm = authReq.getParameterMap();
        for (Object key : pm.keySet()) {
      %><input type="hidden" name="<%= key%>" value="<%= pm.get(key)%>"/><%
        }

      %><div class="newEnroll"><p>
        <h1><span>You are about to be redirected to<br>your OpenId provider</span></h1>
        <h1><span>Just wait...</span></h1>
      </div>
      </form>
    </div></div>
  <script type="text/javascript">
    $(document).ready(function() {
      $("#openid-form-redirection").submit();
    });
  </script>
    <%
    }
  }
%>
