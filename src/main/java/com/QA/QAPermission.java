package com.QA;

import org.jblooming.security.Permission;

public class QAPermission extends Permission {

  public int reputationRequired;

  public static final QAPermission QUESTION_CREATE = new QAPermission("QUESTION_CREATE",5);
  public static final QAPermission QUESTION_CREATE_NO_RECAPTCHA = new QAPermission("QUESTION_CREATE_NO_RECAPTCHA",10);
  public static final QAPermission QUESTION_EDIT = new QAPermission("QUESTION_EDIT",100);
  public static final QAPermission QUESTION_RATE = new QAPermission("QUESTION_RATE",1);
  public static final QAPermission QUESTION_COMMENT = new QAPermission("QUESTION_COMMENT",0);

  public static final QAPermission TAG_CREATE = new QAPermission("TAG_CREATE",5);

  public static final QAPermission ANSWER_CREATE = new QAPermission("ANSWER_CREATE",1);
  public static final QAPermission ANSWER_CREATE_NO_RECAPTCHA = new QAPermission("ANSWER_CREATE_NO_RECAPTCHA",5);
  public static final QAPermission ANSWER_EDIT = new QAPermission("ANSWER_EDIT",30);
  public static final QAPermission ANSWER_RATE = new QAPermission("ANSWER_RATE",1);

  public static final QAPermission MODERATOR = new QAPermission("MODERATOR",10000);



  public QAPermission(String name, int reputationRequired) {
    super(name);
    this.reputationRequired =  reputationRequired;
  }
  

}
