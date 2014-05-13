package com.QA.businessLogic;

import com.QA.*;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.jblooming.ApplicationException;
import org.jblooming.oql.OqlQuery;
import org.jblooming.persistence.exceptions.FindByPrimaryKeyException;
import org.jblooming.persistence.exceptions.PersistenceException;
import org.jblooming.persistence.exceptions.StoreException;
import org.jblooming.utilities.JSP;
import org.jblooming.waf.constants.Fields;
import org.jblooming.waf.settings.I18n;
import org.jblooming.waf.view.PageState;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.security.NoSuchAlgorithmException;
import java.util.List;


public class QAUserAction {

  PageState pageState = null;
  QAOperator logged = null;

  public QAUserAction() throws PersistenceException {
    pageState = PageState.getCurrentPageState();
    logged = (QAOperator) pageState.getLoggedOperator();
  }

  public JSONObject cmdSaveName(JSONObject jsResponse) throws StoreException {
    if (logged != null) {
      String newName = pageState.getEntry("fullName").stringValueNullIfEmpty();
      if (JSP.ex(newName)) {
        logged.setName(newName);
        logged.store();
      }
      jsResponse.element("ok", true);
    }

    return jsResponse;
  }

  public JSONObject cmdSaveEmail(JSONObject jsResponse) throws StoreException, NoSuchAlgorithmException, ApplicationException {
    if (logged != null) {
      String email = pageState.getEntry("email").stringValueNullIfEmpty();
      if (JSP.ex(email)) {
        logged.setUnverifiedEmail(email);
        logged.store();
        logged.sendEnrollEmailMesssage(pageState);
        jsResponse.element("action", "sent");
      } else {
        logged.setUnverifiedEmail(null);
        logged.setEmail(null);
        logged.store();
        jsResponse.element("action", "emptied");
      }
      jsResponse.element("ok", true);
    }

    return jsResponse;
  }


  public JSONObject cmdDeleteUser(JSONObject jsResponse) throws PersistenceException {

    if (logged != null) {

      /**
       * QUESTIONS todo
       */

      //Set<Question> list = logged.getQuestions();

      //votes
      OqlQuery oqlQuery = new OqlQuery("select distinct upv from " + Upvote.class.getName() + " as upv where upv.operator=:own");
      oqlQuery.getQuery().setEntity("own", logged);
      List<Upvote> listVotes = oqlQuery.list();
      for (Upvote u : listVotes) {
        u.remove();
      }

      //comments
      oqlQuery = new OqlQuery("select distinct comm from " + Comment.class.getName() + " as comm where comm.owner=:own");
      oqlQuery.getQuery().setEntity("own", logged);
      List<Comment> cL = oqlQuery.list();
      for (Comment u : cL) {
        u.remove();
      }

      //remove Answers??? todo
      /*oqlQuery = new OqlQuery("select distinct prop from " + Answer.class.getName() + " as prop where prop.owner=:own " + (list.size() > 0 ? " and prop.manifesto not in (:createdMans)" : "") +
              "  and prop.manifesto.deleted=false order by prop.manifesto.finalMeeting desc");
      if (list.size() > 0)
        oqlQuery.getQuery().setParameterList("createdMans", list);
      oqlQuery.getQuery().setEntity("own", logged);
      List<Answer> pL = oqlQuery.list();
      for (Answer u : pL) {
        u.remove();
      } */

      for (Question manifesto : logged.getQuestionsNotDeleted(100000)) {
        manifesto.setDeleted(true);
        manifesto.store();
      }

      /**
       * BRICKS
       */

      //remove all my comments & upvotes: done above




      logged.setEnabled(false);
      logged.setLoginName("DELETED_" + logged.getId());
      logged.setName(I18n.g("QA_DELETED_USER"));
      logged.setWebsite("DEL_" + logged.getWebsite() + "__DEL__" + logged.getId());
      logged.getOptions().put("OLD_LOGINNAME", logged.getLoginName());
      logged.setUnverifiedEmail(null);
      logged.setEmail(null);
      logged.getCalderon().put("EMAIL_WAS",logged.getEmail());
      logged.setGravatarUrl(null);
      logged.store();

      jsResponse.element("ok", true);
    }


    return jsResponse;
  }


  public JSONObject cmdSubsEmail(JSONObject jsResponse) throws StoreException {
    if (logged != null) {

      if (Fields.TRUE.equals(logged.getOption("SEND_NOTIF_BY_EMAIL"))) {
        logged.getOptions().put("SEND_NOTIF_BY_EMAIL", Fields.FALSE);
        jsResponse.element("subs", false);
      } else {
        logged.getOptions().put("SEND_NOTIF_BY_EMAIL", Fields.TRUE);
        jsResponse.element("subs", true);

      }

      logged.store();



      jsResponse.element("ok", true);
    }

    return jsResponse;
  }



  public JSONObject cmdSubsMailing(JSONObject jsResponse) throws Exception {
    if (logged != null) {

      //String apikey = "b2b8fcbd0092cb5612d0e84d48b901f4-us6";
      //String listId = "d223f03ad6";
      //String mailingServerDomain = "us6.api.mailchimp.com";

      String mailingServerDomain = I18n.g("QA_MAILING_DOMAIN");
      String apikey = I18n.g("QA_MAILING_APIKEY");
      String listId = I18n.g("QA_MAILING_LISTID");

      if (Fields.TRUE.equals(logged.getOption("QA_MAILINGLIST"))) {
        logged.getOptions().put("QA_MAILINGLIST", Fields.FALSE);
        jsResponse.element("subs", false);

        if (JSP.ex(logged.getEmail())) {
        URL url = new URL("http://" + mailingServerDomain + "/1.3/?method=listUnsubscribe&apikey=" + apikey + "&id=" + listId + "&email_address=" +logged.getEmail()+"&output=json");
        URLConnection conn = url.openConnection();
        conn.setDoOutput(true);
        // Get the response
        String resp = "";
        BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line;
        while ((line = rd.readLine()) != null) {
          resp += line;
        }
        rd.close();
        }

      } else {
        logged.getOptions().put("QA_MAILINGLIST", Fields.TRUE);
        jsResponse.element("subs", true);

        if (JSP.ex(logged.getEmail())) {
          URL url = new URL("http://" + mailingServerDomain + "/1.3/?method=listSubscribe&apikey=" + apikey + "&id=" + listId + "&email_address=" +logged.getEmail()+"&output=json");
          URLConnection conn = url.openConnection();
          conn.setDoOutput(true);
          // Get the response
          String resp = "";
          BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
          String line;
          while ((line = rd.readLine()) != null) {
            resp += line;
          }
          rd.close();
        }

      }


      logged.store();

      jsResponse.element("ok", true);
    }

    return jsResponse;
  }


  public JSONObject cmdChangePsw(JSONObject jsResponse) throws ApplicationException {
    String newpsw = pageState.getEntry("newpsw").stringValueNullIfEmpty();
    if (JSP.ex(newpsw)) {
       logged.changePassword(newpsw);
    }
    return jsResponse;
  }

  public JSONObject cmdSubsUnsubsUser(JSONObject jsResponse) throws FindByPrimaryKeyException, StoreException {
    if (logged != null) {
      String userid = pageState.getEntry("USERID").stringValueNullIfEmpty();
      if (JSP.ex(userid)) {
        QAOperator user = QAOperator.load(userid);
        //unsub
        if (user.isMySubscriber(logged)) {
          JSONArray subsA =  user.getMySubscribersIds();
          subsA.remove(logged.getId());
          user.getCalderon().put("subscribed",subsA);

          user.setKarma(user.getKarma() - QAEvent.QUESTION_ANSWER_UPVOTE.value);
        //sub
        } else {
          JSONArray subsA =  user.getMySubscribersIds();
          subsA.add(logged.getId());
          user.getCalderon().put("subscribed",subsA);

          JSONArray subsToA =  logged.getIAmSubscribedToIds();
          subsToA.add(user.getId());
          logged.getCalderon().put("subscribingTo",subsToA);


          user.setKarma(user.getKarma() + QAEvent.QUESTION_ANSWER_UPVOTE.value);
        }
        user.store();
        logged.store();
      }
      jsResponse.element("ok", true);
    }

    return jsResponse;
  }
}