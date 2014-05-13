package com.QA;

import com.QA.rank.Hit;
import com.google.common.collect.Lists;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.IndexColumn;
import org.hibernate.annotations.Type;
import org.hibernate.search.annotations.DocumentId;
import org.hibernate.search.annotations.FieldBridge;
import org.hibernate.search.bridge.builtin.IntegerBridge;
import org.jblooming.agenda.CompanyCalendar;
import org.jblooming.ontology.SecuredLoggableSupport;
import org.jblooming.operator.User;
import org.jblooming.oql.OqlQuery;
import org.jblooming.persistence.PersistenceHome;
import org.jblooming.persistence.exceptions.FindByPrimaryKeyException;
import org.jblooming.persistence.exceptions.FindException;
import org.jblooming.persistence.exceptions.StoreException;
import org.jblooming.security.Permission;
import org.jblooming.utilities.JSP;
import org.jblooming.waf.settings.ApplicationState;
import org.jblooming.waf.settings.I18n;
import org.jblooming.waf.view.PageSeed;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

@Entity
@Table(name = "qa_question")
public class Question extends SecuredLoggableSupport {

  private String subject;
  private String description;
  private String code;
  //for imports
  private String externalCode;

  private boolean deleted = false;

  private QAOperator owner;

  private Category category;

  //activity level * likes
  private int communityInterestRank;

  private List<Tag> tags = new ArrayList();
  private List<QuestionRevision> questionRevisions = new ArrayList();

  private JSONObject calderon = new JSONObject();

  //de norm
  private Date answerAcceptedOn;
  private Answer acceptedAnswer;

  private Set<Answer> answers = new HashSet(); // inverse
  private List<Comment> comments = new ArrayList();  // inverse
  private Set<Upvote> upvotes = new HashSet();  // inverse

  //denormalized social weight
  private double totUpvotesFromQandA;

  public enum ContentRatingValue {
    CENSORED, SIGNALLED, OFFTOPIC, DUPLICATE, ARGUMENTATIVE
  }

  @Id
  @Type(type = "int")
  @GeneratedValue(strategy = GenerationType.AUTO)
  @DocumentId
  @FieldBridge(impl = IntegerBridge.class)
  public Serializable getId() {
    return super.getId();
  }

  public String getExternalCode() {
    return externalCode;
  }

  public void setExternalCode(String externalCode) {
    this.externalCode = externalCode;
  }

  public double getTotUpvotesFromQandA() {
    return totUpvotesFromQandA;
  }

  public void setTotUpvotesFromQandA(double totUpvotesFromQandA) {
    this.totUpvotesFromQandA = totUpvotesFromQandA;
  }

  public Date getAnswerAcceptedOn() {
    return answerAcceptedOn;
  }

  protected void setAnswerAcceptedOn(Date answerAcceptedOn) {
    this.answerAcceptedOn = answerAcceptedOn;
  }

  @ManyToOne(targetEntity = Answer.class)
  @ForeignKey(name = "fk_quest_accans")
  @Index(name = "idx_quest_accans")
  public Answer getAcceptedAnswer() {
    return acceptedAnswer;
  }


  protected void setAcceptedAnswer(Answer acceptedAnswer) {
    this.acceptedAnswer = acceptedAnswer;
  }


  public int getCommunityInterestRank() {
    return communityInterestRank;
  }

  public void setCommunityInterestRank(int communityInterestRank) {
    this.communityInterestRank = communityInterestRank;
  }

  @OneToMany(targetEntity = Upvote.class)
  @JoinColumn(name = "question")
  @OrderBy("operator")
  public Set<Upvote> getUpvotes() {
    return upvotes;
  }

  private void setUpvotes(Set<Upvote> upvotes) {
    this.upvotes = upvotes;
  }


  @ManyToOne(targetEntity = Category.class)
  @ForeignKey(name = "fk_quest_cat")
  @Index(name = "idx_quest_cat")
  public Category getCategory() {
    return category;
  }

  public void setCategory(Category category) {
    this.category = category;
  }

  @ManyToMany(cascade = {CascadeType.PERSIST})
  @JoinTable(name = "qa_tagquest", joinColumns = {@JoinColumn(name = "id")})
  @IndexColumn(name = "tagPosition", base = 0)
  public List<Tag> getTags() {
    return tags;
  }

  public void setTags(List<Tag> tags) {
    this.tags = tags;
  }


  @OneToMany(targetEntity = Comment.class, cascade = CascadeType.REMOVE)
  @JoinColumn(name = "question")
  public List<Comment> getComments() throws FindException {
    return comments;
  }

  public void setComments(List<Comment> comments) {
    this.comments = comments;
  }


  @Column(length = 900)
  public String getSubject() {
    return subject;
  }

  public void setSubject(String name) {
    this.subject = name;
  }

  @Lob
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }


  @ManyToOne(targetEntity = QAOperator.class)
  @ForeignKey(name = "fk_quest_own")
  @Index(name = "idx_quest_own")
  @JoinColumn(name = "ownerx")
  public QAOperator getOwner() {
    return owner;
  }

  public void setOwner(QAOperator owner) {
    this.owner = owner;
  }

  @OneToMany(targetEntity = QuestionRevision.class, cascade = CascadeType.REMOVE)
  @JoinColumn(name = "revisionOf")
  public List<QuestionRevision> getQuestionRevisions() {
    return questionRevisions;
  }

  private void setQuestionRevisions(List<QuestionRevision> questionRevisions) {
    this.questionRevisions = questionRevisions;
  }

  @Transient
  public List<QuestionRevision> getRevisions() {
    List<QuestionRevision> qr =  getQuestionRevisions();
    return Lists.reverse(qr);
  }



  @OneToMany(targetEntity = Answer.class, cascade = CascadeType.REMOVE)
  @JoinColumn(name = "question")
  /**
   * @deprecated use getAnswersNotDeleted
   */
  private Set<Answer> getAnswers() {
    return answers;
  }

  private void setAnswers(Set<Answer> ps) {
    this.answers = ps;
  }


  @Type(type = "org.jblooming.ontology.JSONObjectType")
  public JSONObject getCalderon() {
    return calderon;
  }

  public void setCalderon(JSONObject calderon) {
    this.calderon = calderon;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    if (code != null && code.length() > 250)
      code = code.substring(0, 250);
    this.code = code;
  }

  @Transient
  public static Question load(Serializable id) throws FindByPrimaryKeyException {
    return (Question) PersistenceHome.findByPrimaryKey(Question.class, id);
  }

  @Transient
  public static Question loadByExternalId(String extId) throws FindException {
    OqlQuery oqlQuery = new OqlQuery("from " + Question.class.getName() + " as question where question.externalCode = :extId");
    oqlQuery.getQuery().setMaxResults(1);
    oqlQuery.getQuery().setParameter("extId", extId);
    Question question = (Question) oqlQuery.uniqueResultNullIfEmpty();
    return question;
  }



  public boolean isDeleted() {
    return deleted;
  }

  public void setDeleted(boolean deleted) {
    this.deleted = deleted;
  }

  /*@Transient
  public static List<Question> getMostUsedManifestos(int maxResults, List<Integer> except) throws org.jblooming.persistence.exceptions.PersistenceException {

    String hql = "from " + Hit.class.getName() + " as hit where hit.manifestoId!=0 order by hit.when desc";
    if (JSP.ex(except))
      hql = hql + " and hit.teamId not in (:ids)";

    hql = hql + " order by hit.when desc";

    OqlQuery oql = new OqlQuery(hql);

    if (JSP.ex(except))
      oql.getQuery().setParameterList("ids", except);

    oql.getQuery().setMaxResults(300);


    List<Hit> hits = oql.list();

    List<EntityGroupRank> ranks = RankUtilities.getRanked(RankUtilities.computeWeightForManifestos(hits), maxResults * 5);
    List<Question> ret = new ArrayList();
    for (EntityGroupRank egr : ranks) {
      Question manifesto = (Question) egr.getEntity();
      if (manifesto != null && !manifesto.isDeleted() && !Question.ContentRatingValue.CENSORED.toString().equals(manifesto.getContentRating())) {
        ret.add(manifesto);
      }
      if (ret.size() > maxResults)
        break;
    }
    return ret;
  }  */

  @Transient
  public void hit(QAOperator operator, QAEvent event) throws StoreException {
    Hit hit = Hit.newHit(this, operator, event.toString(), event.value);
    hit.setQuestionId(this.getIntId());
    hit.store();

    if (operator != null) {
      operator.setKarma(operator.getKarma() + event.value);
      operator.store();
    }
  }


  @Transient
  public void hitAndNotify(QAOperator logged, QAEvent QAEvent) throws StoreException {
    hit(logged, QAEvent);
    // brick text, brick link, op name, op link
    getOwner().sendNote(
            I18n.get(QAEvent.toString() + "_SUBJECT"),

            //  server, brick id, brick text, server, user id, user name
            I18n.get(QAEvent.toString() + "_BODY_%%_%%_%%_%%",
                    ApplicationState.serverURL, getId() + "", JSP.htmlEncodeApexesAndTags(JSP.limWr(getDescription(), 30)),
                    ApplicationState.serverURL, logged.getLoginName(), logged.getDisplayName()),

            QAEvent.toString());
  }


  @Transient
  public JSONObject jsonify(QAOperator logged, boolean getAnswers) {
    Question question = this;
    QAOperator owner = question.getOwner();

    JSONObject jm = new JSONObject();
    jm.element("queId", question.getId());
    jm.element("queName", question.getSubject());
    jm.element("queDescription", question.getDescription());
    jm.element("queCode", question.getCode());
    //jm.element("queUrl", question.getURL());
    jm.element("queOwner", getOwner().jsonify());
    jm.element("queOwnerName", getOwner().getDisplayName());
    jm.element("queUpvotes", getUpvotes().size());

    if (getAnswers) {
      JSONArray jsonArray = new JSONArray();
      for (Answer prop : getAnswersNotDeleted()) {
        JSONObject jp = prop.jsonify(logged, true);
        jsonArray.add(jp);
      }
      jm.element("queProposals", jsonArray);
    }

    if (logged != null)
      jm.element("queIsOwner", getOwner().getIntId() == logged.getIntId());

    return jm;
  }

  @Transient
  public PageSeed getURL() {
    PageSeed ps = new PageSeed("/question/" + getId());
    ps.disableCache = false;
    return ps;
  }


  @Transient
  public boolean hasAcceptedAnswer() {
    boolean haa = false;
    for (Answer a : getAnswersNotDeleted()) {
      if (a.isAccepted()) {
        haa = true;
        break;
      }
    }
    return haa;
  }

  @Transient
  public boolean likedBy(QAOperator QAOperator) {
    boolean likedBy = false;
    for (Upvote a : getUpvotes()) {
      if (a.getOperator() != null && a.getOperator().equals(QAOperator)) {
        likedBy = true;
        break;
      }
    }
    return likedBy;
  }


  @Transient
  public List<Answer> getAnswersNotDeleted() {
    String hql = "from " + Answer.class.getName() + " as ans where ans.question=:qq and ans.deleted = false";
    OqlQuery oql = new OqlQuery(hql);
    oql.getQuery().setEntity("qq", this);
    return oql.getQuery().list();
  }

  @Transient
  public List<Answer> getAnswersByRelevance() {

    String hql= "select answ from "+ Answer.class.getName()+" as answ where answ.deleted=false and answ.question=:qst order by answ.totUpvotesAndAcceptance desc, answ.lastModified desc";

    org.hibernate.Query query = new OqlQuery(hql).getQuery();
    query.setEntity("qst",this);
    return query.list();
  }

  @Transient
  public boolean hasPermissionFor(User u, Permission p) {
    QAOperator mpu = (QAOperator) u;
    boolean result = false;

    result = super.hasPermissionFor(mpu, p);


    if (!result) {
      String arq = ApplicationState.getApplicationSetting("RECAPTCHA_QUESTION");

      boolean dealtBySpecialCase = false;

      //special case MpPermission.QUESTION_CREATE
      if (p.equals(QAPermission.QUESTION_CREATE) && mpu.getQuestionsNotDeletedSize() < 4) {

        result = true;
        dealtBySpecialCase = true;

        //special case MpPermission.QUESTION_CREATE_NO_RECAPTCHA
        //ALWAYS,NEVER,MAYBE
      } else if (p.equals(QAPermission.QUESTION_CREATE_NO_RECAPTCHA)) {

        if ("NEVER".equals(arq)) {

          result = true;
          dealtBySpecialCase = true;

        } else if ("ALWAYS".equals(arq)) {

          result = false;
          dealtBySpecialCase = true;

        } else if ("MAYBE".equals(arq)) {

          //&& u.getCreationDate().getTime()>CompanyCalendar.) {
          long crTime = u.getCreationDate().getTime();
          CompanyCalendar cc = new CompanyCalendar();
          cc.add(CompanyCalendar.DAY_OF_YEAR,-2);

          if (crTime>cc.getTime().getTime()) {
            result = false;
          } else
            result = true;
          dealtBySpecialCase = true;


        } else if (mpu.getQuestionsNotDeletedSize() == 0) {

          result = true;
          dealtBySpecialCase = true;
        }

      } else if (p.equals(QAPermission.ANSWER_CREATE) && mpu.getAnswersNotDeleted().size()<3) {

          result = true;
          dealtBySpecialCase = true;

      } else if (p.equals(QAPermission.ANSWER_CREATE_NO_RECAPTCHA)) {

        if ("NEVER".equals(arq)) {

          result = true;
          dealtBySpecialCase = true;

        } else if ("ALWAYS".equals(arq)) {

          result = false;
          dealtBySpecialCase = true;

        } else if (this.getAnswersNotDeleted().size() == 0) {

          result = true;
          dealtBySpecialCase = true;
        }
      }

      if (!dealtBySpecialCase)
        result = ((QAOperator) u).getKarma() > ((QAPermission) p).reputationRequired;
    }

    return result;
  }

  @Transient
  public List<Question> getRelatedQuestions() throws FindException {



    /*
    ------------- 5 BY SUB ----------------------------
     */

    /*
    about,after,all,also,an,and,another,any,are,as,at,be,because,been,before
being,between,both,but,by,came,can,come,could,did,do,each,for,from,get
got,has,had,he,have,her,here,him,himself,his,how,if,in,into,is,it,like
make,many,me,might,more,most,much,must,my,never,now,of,on,only,or,other
our,out,over,said,same,see,should,since,some,still,such,take,than,that
the,their,them,then,there,these,they,this,those,through,to,too,under,up
very,was,way,we,well,were,what,where,which,while,who,with,would,you,your,a
b,c,d,e,f,g,h,i,j,k,l,m,n,o,p,q,r,s,t,u,v,w,x,y,z,$,1,2,3,4,5,6,7,8,9,0,_
     */


    /*List<Question> subResults = new ArrayList();
    List<String> subtok = Arrays.asList(getSubject().split(" "));

    int tok = 0;


    String hqlS= "select q from "+ Question.class.getName()+" as q where q.subject like :subtok ";

    hqlS += " and q != :tq order by q.totUpvotesFromQandA desc, q.lastModified desc";


    OqlQuery oqlQs = new OqlQuery(hqlS);
    oqlQs.getQuery().setEntity("tq", this);
    oqlQs.getQuery().setMaxResults(3);

        */

    /*
    ------------- 5 BY TAG ----------------------------
     */

    List<Question> tagResults = new ArrayList();

    String hql= "select q from "+ Question.class.getName()+" as q where ";
    boolean isFirst = true;
    int tn = 0;
    for (Tag t : getTags()) {
      hql = hql + (isFirst?" ":" and ")+":tag"+tn+" in elements(q.tags)";
      tn++;
      isFirst = false;
    }
    hql += " and q != :tq order by q.totUpvotesFromQandA desc, q.lastModified desc";

    OqlQuery oqlQ = new OqlQuery(hql);
    tn = 0;
    for (Tag t : getTags()) {
      oqlQ.getQuery().setEntity("tag"+tn,t);
      tn++;
    }

    oqlQ.getQuery().setEntity("tq", this);


    oqlQ.getQuery().setMaxResults(5);
    tagResults.addAll(oqlQ.list());

    if (tagResults.size()<5) {

      List<Tag> sort = new ArrayList(getTags());

      //tag by inverse pop!!!
      Collections.sort(sort, new Comparator<Tag>() {
        public int compare(Tag o1, Tag o2) {
          return o1.getQuestionsCount()-o2.getQuestionsCount();
        }
      });

      for (Tag ts : sort) {
        List<Question> questions = ts.getQuestions(10);
        for (Question qts : questions) {
          if (!tagResults.contains(qts) && !this.equals(qts)) {
            tagResults.add(qts);
            if (tagResults.size()>9)
              break;
          }
        }

        if (tagResults.size()>=5)
          break;
      }


    }
    return tagResults;
  }

  public static List<Question> getHotQuestions(int maxResults, boolean onlyUnAnswered) throws FindException, FindByPrimaryKeyException {

    String hql = "select sum(hit.weight), questionId from " + Hit.class.getName() + " as hit where hit.when>:since and questionId<>null group by questionId order by sum(hit.weight) desc";
    OqlQuery oql = new OqlQuery(hql);

    CompanyCalendar oneMonthAgo = new CompanyCalendar();
    oneMonthAgo.add(CompanyCalendar.MONTH, -1);
    oneMonthAgo.setAndGetTimeToDayStart();
    oql.getQuery().setLong("since", oneMonthAgo.getTime().getTime());
    oql.getQuery().setMaxResults(maxResults*5);

    int picked = 0;
    List<Question> openQ = new ArrayList<Question>();

    List<Object[]>os = oql.list();
    for (Object[] oo : os) {
      Question qst = Question.load((Serializable) oo[1]);
      if (qst!=null && !qst.isDeleted() && (!onlyUnAnswered || !qst.hasAcceptedAnswer())) {
        openQ.add(qst);
        picked++;
      }
      if (picked>=maxResults)
        break;
    }

    return openQ;
  }


  public static List<Question> getTopQuestions(int maxResults, boolean onlyUnAnswered) throws FindException {

    String hql= "select q from "+ Question.class.getName()+" as q where q.deleted=false";
    if (onlyUnAnswered)
      hql += " and q.acceptedAnswer = null";

    hql += " order by q.totUpvotesFromQandA desc, q.lastModified desc";
    org.hibernate.Query query = new OqlQuery(hql).getQuery();
    query.setMaxResults(maxResults);
    return query.list();
  }


}