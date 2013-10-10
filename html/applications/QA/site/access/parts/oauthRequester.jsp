<%@ page import="com.QA.connections.facebook.FacebookUtilities, com.QA.connections.twitter.TwitterUtilities, net.sf.json.JSONObject, org.jblooming.ApplicationRuntimeException,
org.jblooming.tracer.Tracer, org.jblooming.utilities.JSP, org.jblooming.waf.settings.ApplicationState, org.jblooming.waf.settings.I18n, org.jblooming.waf.view.PageSeed,
org.jblooming.waf.view.PageState, twitter4j.Twitter, twitter4j.TwitterFactory, twitter4j.auth.RequestToken" %><%

  /*----------------------------------------------------------------

  OAUTH PART FOR LOGIN

  ----------------------------------------------------------------*/

  PageState pageState = org.jblooming.waf.view.PageState.getCurrentPageState();
  JSONObject json = new JSONObject();

  try {
    if ("TWITTERAUTH".equals(pageState.command)) {

        PageSeed twitterAuth = pageState.pageFromRoot("/site/access/parts/twitterLoginAuth.jsp");

        Twitter twitter = TwitterFactory.getSingleton();
        twitter.setOAuthAccessToken(null);
        RequestToken twitterRequestToken = twitter.getOAuthRequestToken(ApplicationState.serverURL + twitterAuth.toLinkToHref());

        String authorizationUrl = twitterRequestToken.getAuthenticationURL();
        session.setAttribute("twRT", twitterRequestToken.getToken());
        session.setAttribute("twRTS", twitterRequestToken.getTokenSecret());
        json.element("ok", true);
        json.element("url", authorizationUrl);

    } else if ("FACEBOOKAUTH".equals(pageState.command)) {
      /*FacebookUtilities facebookUtilities = null;
      if (ApplicationState.platformConfiguration.development)
        facebookUtilities = new FacebookUtilities(ApplicationState.getApplicationSetting(FacebookUtilities.API_PERMISSIONS), FacebookUtilities.getApiKey(), ApplicationState.serverURL +"/applications/QA/site/access/parts/facebookLoginAuth.jsp", "1cf4881d82348d7b2542e1bfeaa70be3");
      else*/
      FacebookUtilities facebookUtilities = new FacebookUtilities(ApplicationState.getApplicationSetting(FacebookUtilities.API_PERMISSIONS), FacebookUtilities.getApiKey(),
              ApplicationState.serverURL + "/applications/QA/site/access/parts/facebookLoginAuth.jsp", FacebookUtilities.getApiKeySecret());

      String authorizationUrl = facebookUtilities.getRequestTokenUrl();
      json.element("ok", true);
      json.element("url", authorizationUrl);
    }

  } catch (Throwable t) {
    Tracer.platformLogger.error(t);
    Tracer.platformLogger.error(ApplicationRuntimeException.getStackTrace(t));

    JSONObject ret = new JSONObject();
    ret.element("ok", false);
    ret.element("error", true);
    ret.element("message", I18n.get(t.getMessage()));
    json = ret;
  }

  // JSONP OBJECT
  if (JSP.ex(pageState.getEntry("__jsonp_callback"))) {
    out.print(pageState.getEntry("__jsonp_callback").stringValue() + "(");
    out.print(json.toString());
    out.print(");");

    // JSON OBJECT
  } else {
    out.print(json.toString());
  }

%>