package com.QA;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.IndexColumn;
import org.hibernate.annotations.Type;
import org.hibernate.search.annotations.DocumentId;
import org.hibernate.search.annotations.FieldBridge;
import org.hibernate.search.bridge.builtin.IntegerBridge;
import org.jblooming.ontology.SecuredLoggableSupport;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "qa_question_revision")
public class QuestionRevision extends SecuredLoggableSupport {

  private QAOperator editor;
  private Date revisionDate;
  private Question revisionOf;

  private String formerSubject;
  private String formerDescription;
  private List<Tag> formerTags = new ArrayList();

  @Id
  @Type(type = "int")
  @GeneratedValue(strategy = GenerationType.AUTO)
  @DocumentId
  @FieldBridge(impl = IntegerBridge.class)
  public Serializable getId() {
    return super.getId();
  }


  @ManyToOne(targetEntity = QAOperator.class)
  @ForeignKey(name = "fk_questrev_own")
  @Index(name = "idx_questrev_own")
  @JoinColumn(name = "editorx")
  public QAOperator getEditor() {
    return editor;
  }

  public void setEditor(QAOperator editor) {
    this.editor = editor;
  }

  public Date getRevisionDate() {
    return revisionDate;
  }

  public void setRevisionDate(Date revisionDate) {
    this.revisionDate = revisionDate;
  }

  @ManyToOne(targetEntity = Question.class)
  @ForeignKey(name = "fk_questrev_qst")
  @Index(name = "idx_questrev_qst")
  public Question getRevisionOf() {
    return revisionOf;
  }

  public void setRevisionOf(Question revisionOf) {
    this.revisionOf = revisionOf;
  }

  @Column(length = 900)
  public String getFormerSubject() {
    return formerSubject;
  }

  public void setFormerSubject(String formerSubject) {
    this.formerSubject = formerSubject;
  }

  @Lob
  public String getFormerDescription() {
    return formerDescription;
  }

  public void setFormerDescription(String formerDescription) {
    this.formerDescription = formerDescription;
  }

  @ManyToMany(cascade = {CascadeType.PERSIST})
  @JoinTable(name = "qa_tagquestrev", joinColumns = {@JoinColumn(name = "id")})
  @IndexColumn(name = "tagPosition", base = 0)
  public List<Tag> getFormerTags() {
    return formerTags;
  }

  public void setFormerTags(List<Tag> formerTags) {
    this.formerTags = formerTags;
  }

  public static QuestionRevision createRevision(Question question, QAOperator editor) {
    QuestionRevision qr = new QuestionRevision();
    qr.setFormerDescription(question.getDescription());
    qr.setFormerSubject(question.getSubject());
    qr.setFormerTags(question.getTags());
    qr.setRevisionOf(question);
    qr.setEditor(editor);
    qr.setRevisionDate(new Date());
    return qr;
  }

}
