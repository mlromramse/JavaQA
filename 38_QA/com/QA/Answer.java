package com.QA;

import com.QA.rank.Hit;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Type;
import org.hibernate.search.annotations.DocumentId;
import org.hibernate.search.annotations.FieldBridge;
import org.hibernate.search.bridge.builtin.IntegerBridge;
import org.jblooming.ontology.SecuredLoggableSupport;
import org.jblooming.operator.User;
import org.jblooming.oql.OqlQuery;
import org.jblooming.persistence.PersistenceHome;
import org.jblooming.persistence.exceptions.FindByPrimaryKeyException;
import org.jblooming.persistence.exceptions.FindException;
import org.jblooming.persistence.exceptions.StoreException;
import org.jblooming.utilities.JSP;
import org.jblooming.waf.settings.ApplicationState;
import org.jblooming.waf.settings.I18n;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

@Entity
@Table(name = "qa_answer")
public class Answer extends SecuredLoggableSupport {

  private Question question;
  private String text;
  private int finalOrder = 0;
  private QAOperator owner;

  private boolean deleted = false;

  private boolean accepted = false;
  private String contentRating;

  private Set<Upvote> upvotes = new HashSet();  // inverse
  private List<Comment> comments = new ArrayList();  // inverse
  private List<AnswerRevision> answerRevision = new ArrayList(); // inverse

  //denormalized social weight
  private double totUpvotesAndAcceptance;


  @Id
  @Type(type = "int")
  @GeneratedValue(strategy = GenerationType.AUTO)
  @DocumentId
  @FieldBridge(impl = IntegerBridge.class)
  public Serializable getId() {
    return super.getId();
  }

  public boolean isDeleted() {
    return deleted;
  }

  public void setDeleted(boolean deleted) {
    this.deleted = deleted;
  }

  @ManyToOne(targetEntity = Question.class)
  @ForeignKey(name = "fk_prop_man")
  @Index(name = "idx_prop_man")
  public Question getQuestion() {
    return question;
  }

  public void setQuestion(Question question) {
    this.question = question;
  }

  @Lob
  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  @OneToMany(targetEntity = Upvote.class)
  @JoinColumn(name = "answer")
  @OrderBy("operator")
  public Set<Upvote> getUpvotes() {
    return upvotes;
  }

  private void setUpvotes(Set<Upvote> upvotes) {
    this.upvotes = upvotes;
  }

  @OneToMany(targetEntity = Comment.class)
  @JoinColumn(name = "answer")
  //@OrderBy("parent")
  public List<Comment> getComments() {
    return comments;
  }

  private void setComments(List<Comment> cts) {
    this.comments= cts;
  }

  @ManyToOne(targetEntity = QAOperator.class)
  @ForeignKey(name = "fk_answ_own")
  @Index(name = "idx_answ_own")
  @JoinColumn(name = "ownerx")
  public QAOperator getOwner() {
    return owner;
  }

  public void setOwner(QAOperator owner) {
    this.owner = owner;
  }

  protected boolean isAccepted() {
    return accepted;
  }

  private void setAccepted(boolean accepted) {
    this.accepted = accepted;
  }
  
  public int getFinalOrder() {
    return finalOrder;
  }

  public void setFinalOrder(int finalOrder) {
    this.finalOrder = finalOrder;
  }

  @OneToMany(targetEntity = AnswerRevision.class, cascade = CascadeType.REMOVE)
  @JoinColumn(name = "revisionOf")
  public List<AnswerRevision> getAnswerRevision() {
    return answerRevision;
  }

  private void setAnswerRevision(List<AnswerRevision> answerRevision) {
    this.answerRevision = answerRevision;
  }


  @Transient
  public static Answer load(Serializable id) throws FindByPrimaryKeyException {
    return (Answer) PersistenceHome.findByPrimaryKey(Answer.class, id);
  }

  @Transient
  public JSONObject jsonify(QAOperator logged, boolean getComments) {
    Answer answer = this;

    JSONObject t = new JSONObject();
    t.element("propId", answer.getId());
    t.element("propText", answer.getText());
    t.element("propMan", answer.getQuestion().jsonify(logged, false));
    t.element("propVotes", answer.getUpvotes().size());
    t.element("propSelected", answer.isAccepted());
    t.element("propOwnerName", answer.getOwner().getDisplayName());
    t.element("propOwner", answer.getOwner().jsonify());

    if (getComments){
      JSONArray comments = new JSONArray();
      for (Comment comment : getComments()) {
        JSONObject jc = comment.jsonify(logged);
        comments.add(jc);
      }
      t.element("propComments", comments);
    }

    if (logged != null)
      t.element("propIsOwner", answer.getOwner().getIntId() == logged.getIntId());

    return t;
  }

  @Transient
  public boolean alreadyVoted(QAOperator logged) throws FindException {
    OqlQuery oq = new OqlQuery("from " + Upvote.class.getName() + " as up where up.operator=:op and up.answer=:answer" );
    oq.getQuery().setEntity("op", logged).setEntity("answer", this);
    List<Upvote> votes = oq.list();
    return votes.size()>0;
  }

  public static Comparator finalOrderComparator = new Comparator() {
   public int compare(Object o1, Object o2) {
     Answer p1 = (Answer) o1;
     Answer p2 = (Answer) o2;
     return p1.getFinalOrder() - p2.getFinalOrder();
   }
  };

  public String getContentRating() {
    return contentRating;
  }

  public void setContentRating(String contentRating) {
    this.contentRating = contentRating;
  }
  
  @Transient
  public void setAsAccepted() throws StoreException {
    for (Answer otherAnswer : getQuestion().getAnswersNotDeleted()) {
      if (!otherAnswer.getId().equals(getId()) && otherAnswer.isAccepted()) {
        otherAnswer.setAsRefuted();
      }
    }
    setAccepted(true);
    setTotUpvotesAndAcceptance(getTotUpvotesAndAcceptance()+1000);
    getQuestion().setAnswerAcceptedOn(new Date());
    getQuestion().setAcceptedAnswer(this);
    getQuestion().store();
    store();
  }

  @Transient
  public void setAsRefuted() throws StoreException {
    setAccepted(false);
    setTotUpvotesAndAcceptance(getTotUpvotesAndAcceptance()-1000);
    getQuestion().setAnswerAcceptedOn(null);
    getQuestion().setAcceptedAnswer(null);
    getQuestion().store();
    store();
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
  public boolean hasPermissionFor(User u, QAPermission p) {
    boolean result = false;

    result = super.hasPermissionFor(u, p);

    if (!result)
      result = ((QAOperator)u).getKarma() > p.reputationRequired;

    return result;
  }

  @Transient
  public void hitAndNotify(QAOperator logged, QAEvent QAEvent) throws StoreException {
    hit(logged, QAEvent);
    // brick text, brick link, op name, op link
    getOwner().sendNote(
            I18n.get(QAEvent.toString() + "_SUBJECT"),

            //  server, brick id, brick text, server, user id, user name
            I18n.get(QAEvent.toString() + "_BODY_%%_%%_%%_%%",
                    ApplicationState.serverURL, getQuestion().getId() + "", JSP.limWr(JSP.htmlEncodeApexesAndTags(getText()), 30),
                    ApplicationState.serverURL, logged.getLoginName(), logged.getDisplayName()),

            QAEvent.toString());
  }

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

  public double getTotUpvotesAndAcceptance() {
    return totUpvotesAndAcceptance;
  }

  public void setTotUpvotesAndAcceptance(double totUpvotesAndAcceptance) {
    this.totUpvotesAndAcceptance = totUpvotesAndAcceptance;
  }
}