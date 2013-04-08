package com.QA.connections.twitter;

import com.QA.QAOperator;
import org.jblooming.operator.Operator;
import org.jblooming.persistence.exceptions.StoreException;
import org.jblooming.utilities.JSP;
import org.jblooming.waf.settings.ApplicationState;
import twitter4j.*;
import twitter4j.http.AccessToken;

public class TwitterUtilities {
/*
Access level Read-only Consumer key
qLvln2UqPGzTmYXVoiOhWw
Consumer secret
fpmDrhXufy12SA1TP7LnB0GSLysfLHSrgH9reDksZ0
Request token URL
https://api.twitter.com/oauth/request_token
Authorize URL
https://api.twitter.com/oauth/authorize
Access token URL
https://api.twitter.com/oauth/access_token
Callback URL
http://meltaplot.com
 */



  public static String getApiKey() {
    if (ApplicationState.platformConfiguration.development) {
      return ApplicationState.getApplicationSetting("TW_API_KEY_DEV");
    } else {
      return ApplicationState.getApplicationSetting("TW_API_KEY");
    }
  }

  public static String getApiKeySecret() {
    if (ApplicationState.platformConfiguration.development) {
      return ApplicationState.getApplicationSetting("TW_API_KEY_SECRET_DEV");
    } else {
      return ApplicationState.getApplicationSetting("TW_API_KEY_SECRET");
    }
  }


  public static boolean testAccessToken(QAOperator logged) {
    boolean hasValidAcces = true;
    String settings = ""; //logged.getTwitterSettings();
    Twitter twitter = new TwitterFactory().getInstance();
    twitter.setOAuthConsumer(getApiKey(), getApiKeySecret());
    if (JSP.ex(settings)) {
      String[] settingsArray = settings.split("_");
      String aceessToken = settingsArray[1];
      String accessTokenSecret = settingsArray[2];
      try {
        twitter.setOAuthAccessToken(new AccessToken(aceessToken, accessTokenSecret));
        twitter.verifyCredentials();
      } catch (TwitterException e) {
        if (e.getStatusCode() != 420) ;
        hasValidAcces = false;
      }
    } else {
      hasValidAcces = false;
    }
    return hasValidAcces;
  }

  /*if the user has a twitter username still unverified this method gives back the code*/

  public static String getTwitterTemporaryCode(Operator op) {
    String code = "";
    String twitterName = op.getOption("TWITTER_NAME");
    if (JSP.ex(twitterName)) {
      String[] params = twitterName.split("---");
      if (params.length > 1) {
        code = params[1];
      }
    }
    return code;
  }

  /* this method gives you the twitter username of the operator it does not check if it is verified or not*/

  public static String getTwitterUserName(Operator op) {
    String name = "";
    String twitterName = op.getOption("TWITTER_NAME");
    if (JSP.ex(twitterName)) {
      String[] params = twitterName.split("---");
      name = params[0];
    }
    return name;
  }

  /*this method return if a user has or not the twitter verified username*/

  public static boolean hasVerifiedTwitterUsername(Operator op) {
    boolean isValid = true;
    String twitterName = op.getOption("TWITTER_NAME");
    if (JSP.ex(twitterName)) {
      String[] params = twitterName.split("---");
      if (params.length > 1) {
        isValid = false;
      }
    } else {
      isValid = false;
    }
    return isValid;
  }


  public static void setTwitterUserNameAsVerified(Operator op) throws StoreException, TwitterException {
    String name = "";
    String twitterName = op.getOption("TWITTER_NAME");
    if (JSP.ex(twitterName)) {
      String[] params = twitterName.split("---");
      name = params[0];
    }
    op.getOptions().put("TWITTER_NAME", name);
    op.store();
  }

  public static boolean checkDMCode(DirectMessage message, QAOperator op) throws StoreException, TwitterException {
    String opName = TwitterUtilities.getTwitterUserName(op);
    String opCode = TwitterUtilities.getTwitterTemporaryCode(op);
    String sender = message.getSender().getScreenName();
    String code = message.getText();
    boolean result = false;
    if (sender.equals(opName)) {
      if (JSP.ex(opCode) && code.contains(opCode)) {
        TwitterUtilities.setTwitterUserNameAsVerified(op);
        result = true;
      }
    }
    return result;
  }

}