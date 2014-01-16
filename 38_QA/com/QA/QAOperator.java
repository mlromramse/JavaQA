package com.QA;

import com.QA.messages.StickyNote;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.jblooming.ApplicationException;
import org.jblooming.messaging.MailHelper;
import org.jblooming.ontology.SerializedList;
import org.jblooming.operator.Operator;
import org.jblooming.oql.OqlQuery;
import org.jblooming.persistence.PersistenceHome;
import org.jblooming.persistence.exceptions.FindByPrimaryKeyException;
import org.jblooming.persistence.exceptions.FindException;
import org.jblooming.persistence.exceptions.StoreException;
import org.jblooming.system.SystemConstants;
import org.jblooming.utilities.JSP;
import org.jblooming.utilities.StringUtilities;
import org.jblooming.waf.constants.Fields;
import org.jblooming.waf.settings.ApplicationState;
import org.jblooming.waf.settings.I18n;
import org.jblooming.waf.view.PageSeed;
import org.jblooming.waf.view.PageState;

import javax.persistence.Transient;
import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.util.*;

// mapped on file meltAPlot.hbm.xml
public class QAOperator extends Operator {

  private String email;
  private String unverifiedEmail;
  private String website;
  private String phone;
  private String mobile;
  private String address;
  private String aboutMe;
  private Date expiryDate;

  private double karma;


  private SerializedList<String> badges = new SerializedList();

  private Set<Upvote> votes = new HashSet(); // inverse
  private Set<Question> questions = new HashSet();  // inverse
  private Set<Answer> answers = new HashSet();  // inverse

  private JSONObject calderon = new JSONObject();

  public double getKarma() {
    return karma;
  }

  public void setKarma(double karma) {
    this.karma = karma;
  }

  public SerializedList<String> getBadges() {
    return badges;
  }

  public void setBadges(SerializedList<String> badges) {
    this.badges = badges;
  }


  public Date getExpiryDate() {
    return expiryDate;
  }

  public void setExpiryDate(Date expiryDate) {
    this.expiryDate = expiryDate;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  @Transient
  public void setConfirmedEmail(String email) {

    setEmail(email);
    getOptions().put("SEND_NOTIF_BY_EMAIL", Fields.TRUE);


  }


  public String getWebsite() {
    return website;
  }

  public void setWebsite(String website) {
    this.website = website;
  }

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public String getMobile() {
    return mobile;
  }

  public void setMobile(String mobile) {
    this.mobile = mobile;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public String getAboutMe() {
    return aboutMe;
  }

  public void setAboutMe(String aboutMe) {
    this.aboutMe = aboutMe;
  }

  public String getUnverifiedEmail() {
    return unverifiedEmail;
  }

  public void setUnverifiedEmail(String unverifiedEmail) {
    this.unverifiedEmail = unverifiedEmail;
  }


  public boolean upvotesContain(Answer answer) {
    Set<Upvote> votes = this.getVotes();
    for (Upvote u : votes) {
      if (u.getAnswer().getId().equals(answer.getId()))
        return true;
    }
    return false;
  }


  public JSONObject getCalderon() {
    return calderon;
  }

  public void setCalderon(JSONObject calderon) {
    this.calderon = calderon;
  }


  public Set<Upvote> getVotes() {
    return votes;
  }

  private void setVotes(Set<Upvote> ups) {
    this.votes = ups;
  }


  private Set<Question> getQuestions() {
    return questions;
  }

  @Transient
  public List<Question> getQuestionsNotDeleted(int maxResults) {
    String hql = "from " + Question.class.getName() + " as qst where qst.owner=:op and qst.deleted = false order by qst.lastModified desc";
    org.hibernate.Query query = new OqlQuery(hql).getQuery();
    query.setEntity("op", this);
    query.setMaxResults(maxResults);
    return query.list();
  }

  @Transient
  public int getQuestionsNotDeletedSize() {
    String hql = "select count(qst.id) from " + Question.class.getName() + " as qst where qst.owner=:op and qst.deleted = false";
    org.hibernate.Query query = new OqlQuery(hql).getQuery();
    query.setEntity("op", this);
    return ((Long)query.uniqueResult()).intValue();
  }

  @Transient
  public List<Question> getQuestionsByRelevance(int maxResults) {

    String hql= "select q from "+ Question.class.getName()+" as q where q.deleted=false and q.owner=:own";
    hql += " order by q.totUpvotesFromQandA desc, q.lastModified desc";
    org.hibernate.Query query = new OqlQuery(hql).getQuery();
    query.setEntity("own",this);
    query.setMaxResults(maxResults);
    return query.list();
  }

  public void setQuestions(Set<Question> manifests) {
    this.questions = manifests;
  }


  private Set<Answer> getAnswers() {
    return answers;
  }

  @Transient
  public List<Answer> getAnswersNotDeleted() {
    String hql = "from " + Answer.class.getName() + " as ans where ans.owner=:op and ans.deleted = false";
    OqlQuery oql = new OqlQuery(hql);
    oql.getQuery().setEntity("op", this);
    return oql.getQuery().list();
  }

  @Transient
  public List<Answer> getAnswersByRelevance(int maxResults) {

    String hql= "select answ from "+ Answer.class.getName()+" as answ where answ.deleted=false and answ.question.deleted=false and answ.owner=:mop order by answ.totUpvotesAndAcceptance desc, answ.lastModified desc";
    org.hibernate.Query query = new OqlQuery(hql).getQuery();
    query.setEntity("mop",this);
    query.setMaxResults(maxResults);
    return query.list();
  }

  @Transient
  public List<Answer> getAnswersByDate(int maxResults) {

    String hql= "select answ from "+ Answer.class.getName()+" as answ where answ.deleted=false and answ.question.deleted=false and answ.owner=:mop order by answ.creationDate desc, answ.lastModified desc";
    org.hibernate.Query query = new OqlQuery(hql).getQuery();
    query.setEntity("mop",this);
    query.setMaxResults(maxResults);
    return query.list();
  }

  public void setAnswers(Set<Answer> answers) {
    this.answers = answers;
  }




  public String getDisplayName() {
    return getFullname();
  }

  public String getFullname() {
    if (JSP.ex(getName()))
      return JSP.w(getName());
    return getLoginName();
  }


  public JSONObject jsonify() {
    JSONObject jsResponse = new JSONObject();
    jsResponse.element("id", getId());
    jsResponse.element("userName", getDisplayName());
    jsResponse.element("loginName", getLoginName());
    jsResponse.element("gravatar", getGravatarUrl(80));
    jsResponse.element("publicUrl", getPublicProfileURL());
    jsResponse.element("email", getEmail());
    String SKYPE_NAME = getOption("SKYPE_NAME");
    if (JSP.ex(SKYPE_NAME))
      jsResponse.element("skype", SKYPE_NAME);
    return jsResponse;
  }

  @Transient
  public List<Answer> getAcceptedAnswers() throws FindException {
    String hql = "from " + Answer.class.getName() + " as ans where ans.accepted=true and ans.owner=:op and ans.deleted = false order by ans.lastModified desc";
    OqlQuery oql = new OqlQuery(hql);
    oql.getQuery().setEntity("op", this);
    return oql.list();
  }



  @Transient
  public int getTimeZoneOffset(PageState pageState) {
    int ret = 0;
    try {
      ret = Integer.parseInt(pageState.sessionState.getAttribute("TIME_OFFSET_CLIENT") + "");
    } catch (Throwable t) {
    }
    return ret;
  }

  public TimeZone getTimeZone(PageState pageState) {
    return new SimpleTimeZone(getTimeZoneOffset(pageState), "LICTZ");
  }

  @Transient
  public static QAOperator load(Serializable id) throws FindByPrimaryKeyException {
    return (QAOperator) PersistenceHome.findByPrimaryKey(QAOperator.class, id);
  }

  public static QAOperator loadByLoginName(String loginName) throws FindByPrimaryKeyException {
    return (QAOperator) PersistenceHome.findUniqueNullIfEmpty(QAOperator.class, "loginName", loginName);
  }


  // SPAZZATURA ----------------------------------------------------------------------------------------------------------------------------


  @Transient
  public String getAPIKey() throws NoSuchAlgorithmException {
    return getId() + "x" + StringUtilities.md5Encode(getId() + "fdwqaddagy65'#[3cfsd]");
  }


  public static QAOperator loadByEmail(String email) throws FindByPrimaryKeyException {
    return (QAOperator) PersistenceHome.findUniqueNullIfEmpty(QAOperator.class, "email", email);
  }

  public static QAOperator loadByFullname(String fullname) throws FindByPrimaryKeyException {
    return (QAOperator) PersistenceHome.findUniqueNullIfEmpty(QAOperator.class, "name", fullname);
  }

  @Transient
  public static QAOperator findByToken(String serviceToken) {
    return (QAOperator) PersistenceHome.findUniqueNullIfEmpty(QAOperator.class, "website", serviceToken);
  }

  @Transient
  public String getDefaultEmail() {
    return getEmail();
  }

  @Transient
  public void changePassword(String password) throws ApplicationException {
    try {
      setLastPasswordChangeDate(new Date());
      String prefixedPassword = computePassword(password);
      this.setPassword(prefixedPassword);
      getLastPasswordList().add(prefixedPassword);
    } catch (NoSuchAlgorithmException e) {
      throw new ApplicationException(e);
    }
  }

  @Transient
  public String getGravatarUrl(int size) {
    //first check on calderon
    String pictureUrl;
    if (getCalderon() != null && getCalderon().get("gravatarUrl") != null)
      pictureUrl = getCalderon().getString("gravatarUrl");
    else {
      pictureUrl = JSP.ex(getEmail()) ? getEmail() : (JSP.ex(getUnverifiedEmail()) ? getUnverifiedEmail() : getLoginName() + "@"+ApplicationState.getApplicationSetting(SystemConstants.PUBLIC_SERVER_NAME));
      pictureUrl = "http://www.gravatar.com/avatar/" + StringUtilities.md5Encode(pictureUrl, "") + "?s=" + size + "&d=identicon";
    }
    return pictureUrl;
  }


  public void setGravatarUrl(String pictureUrl) {
    if (getCalderon() == null)
      setCalderon(new JSONObject());
    getCalderon().element("gravatarUrl", pictureUrl);
  }

  public String computePassword(String password) throws NoSuchAlgorithmException {
    return StringUtilities.md5Encode("qw27&5%" + password + " '.y89$=bb");
  }

  public void sendEnrollEmailMesssage(PageState pageState) throws NoSuchAlgorithmException, ApplicationException {
    if (JSP.ex(getUnverifiedEmail())) {

        PageSeed ps = new PageSeed(ApplicationState.serverURL + "/applications/QA/site/access/confirm.jsp");
        ps.command = "CF";
        ps.addClientEntry("UID", getId());
        ps.addClientEntry("CK", StringUtilities.md5Encode(getId() + getUnverifiedEmail() + "vivazoe"));

        String header = I18n.get("QA_CONFIRM_EMAIL_HEADER");
        String subject = I18n.get("QA_EMAIL_DO_CONFIRM");
        String footer = I18n.get("QA_CONFIRM_EMAIL_FOOTER");
        String message = header + "<div style='background-color:#eeeeee; font-size:1.15em; line-height:1.4em; max-width: 570px; margin: 0 auto; padding:20px 40px 40px; border-radius:5px; -webkit-border-radius:5px; -moz-border-radius:5px; color:#555'>"
                + I18n.get("QA_ENROLL_MAIL" , getDisplayName(), "<a href='"+ps.toLinkToHref()+"'>"+ps.toLinkToHref()+"</a>") + "</div>" + footer;
        String fromEmail = ApplicationState.getApplicationSetting(SystemConstants.FLD_MAIL_FROM);
        String from = I18n.g("QA_APP_NAME")+" Support <"+fromEmail+">;";

        if (ApplicationState.platformConfiguration.development)
            from = I18n.g("QA_APP_NAME")+" Support DEV <ppolsinelli@gmail.com>;";
        MailHelper.sendHtmlMail(from, getUnverifiedEmail(), subject, message);

      pageState.addMessageOK("The activation e-mail has been sent correctly. Please check your inbox.");
    }
  }

  public void sendWelcomeEmailMesssage(PageState pageState) throws NoSuchAlgorithmException, ApplicationException {
    if (JSP.ex(getEmail())) {

      String header = I18n.get("QA_CONFIRM_EMAIL_HEADER");
      String subject = I18n.get("QA_EMAIL_WELCOME");
      String footer = I18n.get("QA_CONFIRM_EMAIL_FOOTER");
      String message = header + "<div style='background-color:#eeeeee; font-size:1.15em; line-height:1.4em; max-width: 570px; margin: 0 auto; padding:20px 40px 40px; border-radius:5px; -webkit-border-radius:5px; -moz-border-radius:5px; color:#555'>"
              + I18n.get("QA_EMAIL_WELCOME_BODY" , getDisplayName()) + "</div>" + footer;
      String fromEmail = ApplicationState.getApplicationSetting(SystemConstants.FLD_MAIL_FROM);
      String from = I18n.g("QA_APP_NAME")+" Support <"+fromEmail+">;";

      if (ApplicationState.platformConfiguration.development)
        from = I18n.g("QA_APP_NAME")+" Support DEV <ppolsinelli@open-lab.com>;";
      MailHelper.sendHtmlMail(from, getEmail(), subject, message);

      pageState.addMessageOK("The activation e-mail has been sent correctly. Please check your inbox.");
    }
  }





  public void sendNote(String subject, String message) throws StoreException {
    sendNote(subject, message, null);
  }

  public void sendNote(String subject, String message, String type) throws StoreException {

    StickyNote stik = new StickyNote();
    stik.setReceiver(this);
    stik.setMessage(message);
    stik.setType(type);
    stik.store();

    if (Fields.TRUE.equals(this.getOption("SEND_NOTIF_BY_EMAIL"))) {

      PageSeed ps = new PageSeed(ApplicationState.serverURL + "/applications/QA/site/access/confirm.jsp");
      ps.command = "UNSCRIBE";
      ps.addClientEntry("UID", getId());
      ps.addClientEntry("CK", StringUtilities.md5Encode(getId() + getDefaultEmail() + "vivazoe"));
      message = message + "<br><br><br>"+I18n.get("QA_UNSUBSCRIBE_%%", ps.toLinkToHref());
      String fromEmail = ApplicationState.getApplicationSetting(SystemConstants.FLD_MAIL_FROM);
      String from = I18n.g("QA_APP_NAME")+" Notification <"+fromEmail+">";
      if (ApplicationState.platformConfiguration.development) {
        from = I18n.g("QA_APP_NAME")+" Notification DEV <ppolsinelli@open-lab.com>;";
      }

      MailHelper.sendHtmlMailInSeparateThread(from, getDefaultEmail(), subject, message);
    }
  }

  @Transient
  public String getPublicProfileURL() {
    return ApplicationState.serverURL + "/user/" + getLoginName();
  }

  public static List<QAOperator> getModerators() throws FindException {
    String hql = "select mop from " + QAOperator.class.getName() + " as mop where mop.enabled=true and (mop.administrator=true or mop.calderon like '%moderator%' " +
            "or mop.karma > " + QAPermission.MODERATOR.reputationRequired + ")";

    OqlQuery oql = new OqlQuery(hql);
    List<QAOperator> os = oql.list();
    List<QAOperator> osmod = new ArrayList();
    for (QAOperator op : os)
      if (op.isModerator())
        osmod.add(op);
    return osmod;
  }

  @Transient
  public boolean isModerator() {
    //return hasPermissionAsAdmin() || getKarma() > MpPermission.MODERATOR.reputationRequired;
    boolean moderator = getCalderon().get("moderator")!=null && getCalderon().getBoolean("moderator");
    return hasPermissionAsAdmin() || moderator || getKarma()> QAPermission.MODERATOR.reputationRequired;
  }

  @Transient
  public int getLevel() {

    /*1) runner
    2) junior writer
    3) senior writer
    4) script supervisor
    5) author*/

    if (isModerator())
      return 999;

    int level = 1;
    if (getKarma() > 3000) {
      level = 5;
    } else if (getKarma() > 1000) {
      level = 4;
    } else if (getKarma() > 100) {
      level = 3;
    } else if (getKarma() > 10) {
      level = 2;
    }
    return level;
  }

  @Transient
  public Long getUnreadMessagesTotal() {
    String hql = "select count(sn) from " + StickyNote.class.getName() + " as sn where sn.read is null and sn.receiver=:op order by sn.created desc";
    OqlQuery oql = new OqlQuery(hql);
    oql.getQuery().setEntity("op", this);
    return (Long) oql.uniqueResultNullIfEmpty();
  }

  @Transient
  public JSONArray getMySubscribersIds() {
    JSONArray subsA = new JSONArray();

    Object subs = getCalderon().get("subscribed");
    if (subs!=null) {
      subsA =  getCalderon().getJSONArray("subscribed");
  }
    return subsA;
  }

  @Transient
  public JSONArray getIAmSubscribedToIds() {
    JSONArray subsA = new JSONArray();

    Object subs = getCalderon().get("subscribingTo");
    if (subs!=null) {
      subsA =  getCalderon().getJSONArray("subscribingTo");
    }
    return subsA;
  }

  @Transient
  public boolean isMySubscriber(QAOperator mop) {
    JSONArray subsA =  getMySubscribersIds();
    boolean ret = false;
    for (int i = 0; i < subsA.size(); i++) {
      String id = subsA.get(i)+"";
      if ((mop.getId()+"").equals(id)) {
        ret = true;
        break;
      }
    }
    return ret;
  }


  @Transient
  public static List<QAOperator> getTopOperators(int maxResults) throws FindException {
    String hql = "select mop from " + QAOperator.class.getName() + " as mop where mop.enabled=true order by mop.karma desc";
    OqlQuery oql = new OqlQuery(hql);
    oql.getQuery().setMaxResults(maxResults);
   return (List<QAOperator>)oql.list();
  }




//  @Transient
//  public List<Question> getContributions() throws FindException {
//    List<Question> list = new ArrayList<Question>();
//    List<Question> manifestoList = getOwnedManifests();
//    list.addAll(manifestoList);

  /*OqlQuery oqlQuery = new OqlQuery("select distinct prop.manifesto from " + Answer.class.getName() + " as prop where prop.owner=:own " + (list.size() > 0 ? " and prop.manifesto not in (:createdMans)" : "") + "  and prop.manifesto.deleted=false order by prop.manifesto.finalMeeting desc");
 oqlQuery.getQuery().setEntity("own", this);
 if (list.size() > 0)
   oqlQuery.getQuery().setParameterList("createdMans", manifestoList);
 List<Question> listProp = oqlQuery.list();
 list.addAll(listProp);

 //comments
   oqlQuery = new OqlQuery("select distinct comm.proposal.manifesto from " + Comment.class.getName() + " as comm where comm.owner=:own " + (list.size() > 0 ? " and comm.proposal.manifesto not in (:createdMans)" : "") + " and comm.proposal.manifesto.deleted=false order by comm.proposal.manifesto.finalMeeting desc");
 oqlQuery.getQuery().setEntity("own", this);
 if (list.size() > 0)
   oqlQuery.getQuery().setParameterList("createdMans", list);
 List<Question> listComm = oqlQuery.list();
 list.addAll(listComm);

 //votes?
 oqlQuery = new OqlQuery("select distinct upv.proposal.manifesto from " + Upvote.class.getName() + " as upv where upv.operator=:own " + (list.size() > 0 ? " and upv.proposal.manifesto not in (:createdMans)" : "") + " and upv.proposal.manifesto.deleted=false order by upv.proposal.manifesto.finalMeeting desc");
 oqlQuery.getQuery().setEntity("own", this);
 if (list.size() > 0)
   oqlQuery.getQuery().setParameterList("createdMans", list);
 List<Question> listVotes = oqlQuery.list();
 list.addAll(listVotes); */

//    return list;
//  }


}
