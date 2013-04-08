package com.QA;

import net.sf.json.JSONObject;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Type;
import org.hibernate.search.annotations.DocumentId;
import org.hibernate.search.annotations.FieldBridge;
import org.hibernate.search.bridge.builtin.IntegerBridge;
import org.jblooming.ontology.SecuredLoggableSupport;
import org.jblooming.operator.User;
import org.jblooming.persistence.PersistenceHome;
import org.jblooming.persistence.exceptions.FindByPrimaryKeyException;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Comment (c) 2011 - Open Lab - www.open-lab.com
 */
@Entity
@Table(name = "qa_comment")
public class Comment extends SecuredLoggableSupport {

  private String text;

  private Answer answer;
  private Question question;
  private QAOperator owner;

  private boolean moderatorComment;

  private String contentRating;
  private Set<QAOperator> likes = new HashSet();


  @Id
  @Type(type = "int")
  @GeneratedValue(strategy = GenerationType.AUTO)
  @DocumentId
  @FieldBridge(impl = IntegerBridge.class)
  public Serializable getId() {
    return super.getId();
  }

  @ManyToMany(cascade = {CascadeType.PERSIST})
  @JoinTable(name = "qa_commlikes", joinColumns = {@JoinColumn(name = "id")})
  public Set<QAOperator> getLikes() {
    return likes;
  }

  public void setLikes(Set<QAOperator> likes) {
    this.likes = likes;
  }

  @ManyToOne(targetEntity = Question.class)
  @ForeignKey(name = "fk_cmt_qst")
  @Index(name = "idx_cmt_qst")
  public Question getQuestion() {
    return question;
  }

  public void setQuestion(Question question) {
    this.question = question;
  }


  @ManyToOne(targetEntity = Answer.class)
  @ForeignKey(name = "fk_cmt_prop")
  @Index(name = "idx_cmt_prop")
  public Answer getAnswer() {
    return answer;
  }

  public void setAnswer(Answer answer) {
    this.answer = answer;
  }


  @Lob
  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  @ManyToOne(targetEntity = QAOperator.class)
  @ForeignKey(name = "fk_cmt_own")
  @Index(name = "idx_cmt_own")
  @JoinColumn(name = "ownerx")
  public QAOperator getOwner() {
    return owner;
  }

  public void setOwner(QAOperator owner) {
    this.owner = owner;
  }



  @Transient
  public static Comment load(Serializable id) throws FindByPrimaryKeyException {
    return (Comment) PersistenceHome.findByPrimaryKey(Comment.class, id);
  }

  @Transient
  public JSONObject jsonify(QAOperator logged) {
    Comment comment = this;

    JSONObject t = new JSONObject();
    t.element("cmtId", comment.getId());
    t.element("cmtText", comment.getText());
    t.element("cmtIsModeratorComment", comment.isModeratorComment());

    if (comment.getAnswer()!=null)
      t.element("answer", comment.getAnswer().jsonify(logged, false));

    t.element("cmtOwnerName", comment.getOwner().getDisplayName());
    //t.element("cmtOwner", comment.getOwner().jsonify());


    if (logged != null)
      t.element("cmtIsOwner", comment.getOwner().getIntId() == logged.getIntId());

    return t;
  }

  public static final Comparator dateComparator = new Comparator() {
    public int compare(Object o1, Object o2) {
      if (o1 == o2)
        return 0;
      Date d1 = ((Comment) o1).getCreationDate();
      Date d2 = ((Comment) o2).getCreationDate();
      if (d1 != null && d2 != null)
        return d1.compareTo(d2);
      else
        return d1 == null ? -1 : 1;
    }
  };


  public String getContentRating() {
    return contentRating;
  }

  public void setContentRating(String contentRating) {
    this.contentRating = contentRating;
  }

  @Transient
  public boolean hasPermissionFor(User u, QAPermission p) {
    boolean result = false;

    result = super.hasPermissionFor(u, p);

    if (!result)
      result = ((QAOperator)u).getKarma() > p.reputationRequired;

    return result;
  }


  public boolean isModeratorComment() {
    return moderatorComment;
  }

  public void setModeratorComment(boolean moderatorComment) {
    this.moderatorComment = moderatorComment;
  }
}
