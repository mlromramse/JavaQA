package com.QA;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Type;
import org.hibernate.search.annotations.DocumentId;
import org.hibernate.search.annotations.FieldBridge;
import org.hibernate.search.bridge.builtin.IntegerBridge;
import org.jblooming.ontology.SecuredLoggableSupport;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "qa_answer_revision")
public class AnswerRevision extends SecuredLoggableSupport {

  private QAOperator editor;
  private Date revisionDate;
  private Answer revisionOf;

  private String formerText;


  @Id
  @Type(type = "int")
  @GeneratedValue(strategy = GenerationType.AUTO)
  @DocumentId
  @FieldBridge(impl = IntegerBridge.class)
  public Serializable getId() {
    return super.getId();
  }


  @ManyToOne(targetEntity = QAOperator.class)
  @ForeignKey(name = "fk_answrev_own")
  @Index(name = "idx_answrev_own")
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

  @ManyToOne(targetEntity = Answer.class)
  @ForeignKey(name = "fk_answrev_qst")
  @Index(name = "idx_answrev_qst")
  public Answer getRevisionOf() {
    return revisionOf;
  }

  public void setRevisionOf(Answer revisionOf) {
    this.revisionOf = revisionOf;
  }

  @Lob
  public String getFormerText() {
    return formerText;
  }

  public void setFormerText(String formerText) {
    this.formerText = formerText;
  }

  public static AnswerRevision createRevision(Answer answer, QAOperator editor) {
    AnswerRevision qr = new AnswerRevision();
    qr.setFormerText(answer.getText());
    qr.setRevisionOf(answer);
    qr.setEditor(editor);
    qr.setRevisionDate(new Date());
    return qr;
  }


}
