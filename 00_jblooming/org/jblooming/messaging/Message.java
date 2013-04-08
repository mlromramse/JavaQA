package org.jblooming.messaging;

import org.jblooming.ontology.IdentifiableSupport;
import org.jblooming.ontology.SerializedMap;
import org.jblooming.operator.Operator;
import org.jblooming.persistence.PersistenceHome;
import org.jblooming.persistence.exceptions.StoreException;
import org.jblooming.persistence.hibernate.PersistenceContext;
import org.jblooming.utilities.StringUtilities;
import org.jblooming.utilities.JSP;
import org.jblooming.waf.constants.OperatorConstants;
import org.jblooming.waf.SessionState;
import org.jblooming.waf.settings.ApplicationState;
import org.jblooming.waf.settings.Application;
import org.jblooming.security.Group;

import java.util.*;
import java.io.Serializable;

public class Message extends IdentifiableSupport {

  private String subject;
  private String messageBody;
  private Operator fromOperator;
  private Operator toOperator;
  private String media;
  private Date lastTry;
  private String status;
  private int numberOfTries;
  private Date expires;
  private Date received;
  private String lastError;
  private String link;
  private int groupMessageId;

  public Message() {
  }

  public Message(String subject, String messageBody, Operator toOperator, String media) {

    this.subject = subject;
    this.messageBody = messageBody;
    this.toOperator = toOperator;
    this.media = media;
  }

  public String getSubject() {
    return subject;
  }

  public void setSubject(String subject) {
    this.subject = subject;
  }

  public String getMessageBody() {
    return messageBody;
  }

  public void setMessageBody(String messageBody) {
    this.messageBody = messageBody;
  }

  public Operator getFromOperator() {
    return fromOperator;
  }

  public void setFromOperator(Operator fromOperator) {
    this.fromOperator = fromOperator;
  }

  public Operator getToOperator() {
    return toOperator;
  }

  public void setToOperator(Operator toOperator) {
    this.toOperator = toOperator;
  }


  public String getMedia() {
    return media;
  }

  public void setMedia(String media) {
    this.media = media;
  }

  public Date getLastTry() {
    return lastTry;
  }

  public void setLastTry(Date lastTry) {
    this.lastTry = lastTry;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public int getNumberOfTries() {
    return numberOfTries;
  }

  public void setNumberOfTries(int numberOfTries) {
    this.numberOfTries = numberOfTries;
  }

  public Date getExpires() {
    return expires;
  }

  public void setExpires(Date expires) {
    this.expires = expires;
  }

  public Date getReceived() {
    return received;
  }

  public void setReceived(Date received) {
    this.received = received;
  }

  public String getLastError() {
    return lastError;
  }

  public void setLastError(String lastError) {
    this.lastError = lastError;
  }

  public String getLink() {
    return link;
  }

  public void setLink(String link) {
    this.link = link;
  }


  public int getGroupMessageId() {
    return groupMessageId;
  }

  public void setGroupMessageId(int groupMessageId) {
    this.groupMessageId = groupMessageId;
  }
}
