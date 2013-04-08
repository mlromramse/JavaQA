package com.QA;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Type;
import org.hibernate.search.annotations.DocumentId;
import org.hibernate.search.annotations.FieldBridge;
import org.hibernate.search.bridge.builtin.IntegerBridge;
import org.jblooming.ontology.LoggableIdentifiableSupport;
import org.jblooming.oql.OqlQuery;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "qa_upvote")
public class Upvote extends LoggableIdentifiableSupport {

  private QAOperator operator;
  private Answer answer;
  private Question question;






  @Id
  @Type(type = "int")
  @GeneratedValue(strategy = GenerationType.AUTO)
  @DocumentId
  @FieldBridge(impl = IntegerBridge.class)
  public Serializable getId() {
    return super.getId();
  }

  @ManyToOne( targetEntity = QAOperator.class)
  @ForeignKey(name = "fk_up_op")
  @Index(name = "idx_up_op")
  public QAOperator getOperator() {
    return operator;
  }

  public void setOperator(QAOperator op) {
    this.operator = op;
  }



  @ManyToOne ( targetEntity = Question.class)
  @ForeignKey(name = "fk_up_quest")
  @Index(name = "idx_up_quest")
  public Question getQuestion() {
    return question;
  }

  private void setQuestion(Question question) {
    this.question = question;
  }

  @Transient
  public void setQuestionAndPropagate(Question question) {
    this.setQuestion(question);
    question.setTotUpvotesFromQandA(question.getTotUpvotesFromQandA()+1);
  }

  @ManyToOne ( targetEntity = Answer.class)
  @ForeignKey(name = "fk_up_answ")
  @Index(name = "idx_up_answ")
  public Answer getAnswer() {
    return answer;
  }

  private void setAnswer(Answer answer) {
    this.answer = answer;
  }

  @Transient
  public void setAnswerAndPropagate(Answer answer) {
    this.setAnswer(answer);
    answer.setTotUpvotesAndAcceptance(answer.getTotUpvotesAndAcceptance()+1);
    answer.getQuestion().setTotUpvotesFromQandA(answer.getQuestion().getTotUpvotesFromQandA()+.5);
  }


  @Transient
  public static Upvote getVote(QAOperator op, Answer answer){
    OqlQuery oq = new OqlQuery("from " + Upvote.class.getName() + " as uv where uv.operator=:op and uv.answer=:answer");
    oq.getQuery().setEntity("op", op).setEntity("answer", answer);
    Upvote uv = null;
    try {
      uv = (Upvote) oq.uniqueResult();
    } catch (Throwable a) {}

    return uv;
  }

}