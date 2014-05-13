package com.QA.connections.facebook;

import net.sf.json.JSONObject;
import org.jblooming.utilities.JSP;
import org.jblooming.waf.settings.ApplicationState;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;


public class FacebookUtilities {

  public static final String REQUEST_TOKEN_URL = "https://graph.facebook.com/oauth/authorize?";
  public static final String ACCESS_TOKEN_URL = "https://graph.facebook.com/oauth/access_token?";

  public static final String API_PERMISSIONS = "FB_API_PERMISSIONS";

  String scope;
  String clientId;
  String clientSecret;
  String redirectUri;


  public static String getApiKey() {
    if (ApplicationState.platformConfiguration.development) {
      return ApplicationState.getApplicationSetting("FB_API_KEY_DEV");
    } else {
      return ApplicationState.getApplicationSetting("FB_API_KEY");
    }
  }

  public static String getApiKeySecret() {
    if (ApplicationState.platformConfiguration.development) {
      return ApplicationState.getApplicationSetting("FB_API_KEY_SECRET_DEV");
    } else {
      return ApplicationState.getApplicationSetting("FB_API_KEY_SECRET");
    }
  }

  public FacebookUtilities(String scope, String clientId, String redirectUri, String clientSecret) {
    this.scope = scope;
    this.clientId = clientId;
    this.clientSecret = clientSecret;
    this.redirectUri = redirectUri;

  }

  public String getRequestTokenUrl() {
    String url = REQUEST_TOKEN_URL +
            "scope=" + scope +
            "&client_id=" + clientId +
            "&redirect_uri=" + redirectUri;
    return url;
  }

  public String getAccessToken(String code) throws IOException {
    String accessToken = "";
    String data = "client_id=" + clientId + "&redirect_uri=" + redirectUri + "&client_secret=" + clientSecret + "&code=" + code;
    URL url = new URL(ACCESS_TOKEN_URL);
    URLConnection conn = url.openConnection();
    conn.setDoOutput(true);
    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
    wr.write(data);
    wr.flush();
    wr.close();
    // Get the response
    BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
    String line;
    while ((line = rd.readLine()) != null) {
      accessToken += line;
    }
    rd.close();

    if (JSP.ex(accessToken) && accessToken.indexOf("access_token=") != -1) {
      accessToken = accessToken.substring("access_token=".length(), accessToken.length());
    }
    if (JSP.ex(accessToken) && accessToken.indexOf("&expires=") != -1) {
      accessToken = accessToken.substring(0, accessToken.indexOf("&expires="));
    }
    return accessToken;
  }

  public JSONObject getFacebookUserData(String accessToken) throws IOException {

    String response = "";
    if (JSP.ex(accessToken)) {
      URL url = new URL("https://graph.facebook.com/me?access_token=" + accessToken);
      //URL url = new URL("https://graph.facebook.com/me/feed");
      URLConnection conn = url.openConnection();
      conn.setDoOutput(true);
      BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
      String line;
      while ((line = rd.readLine()) != null) {
        response += line;
      }
      rd.close();

    }
    JSONObject result = JSONObject.fromObject(response);


    //https://graph.facebook.com/me
    return result;
  }

  public String getPictureUrl(String accessToken) throws IOException {

    String response = "";
    if (JSP.ex(accessToken)) {
      URL url = new URL("https://graph.facebook.com/me/picture?access_token=" + accessToken);
      //URL url = new URL("https://graph.facebook.com/me/feed");
      URLConnection conn = url.openConnection();
      conn.setDoOutput(true);
      BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
      String line;
      while ((line = rd.readLine()) != null) {
        response += line;
      }
      rd.close();
    }
    System.out.println("---------------" + response);
    JSONObject result = JSONObject.fromObject(response);
    String url = result.getString("picture");

    //https://graph.facebook.com/me
    return url;
  }

}