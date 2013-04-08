package com.QA.rank;

import com.QA.Question;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Type;
import org.jblooming.ontology.Identifiable;
import org.jblooming.ontology.IdentifiableSupport;
import org.jblooming.operator.Operator;
import org.jblooming.oql.OqlQuery;
import org.jblooming.persistence.PersistenceBricks;
import org.jblooming.persistence.PersistenceHome;
import org.jblooming.persistence.exceptions.PersistenceException;
import org.jblooming.persistence.exceptions.StoreException;
import org.jblooming.persistence.hibernate.PersistenceContext;
import org.jblooming.tracer.Tracer;
import org.jblooming.utilities.CodeValue;
import org.jblooming.utilities.CodeValueList;
import org.jblooming.utilities.JSP;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

@Entity
@Table(name = "qa_rank_hit")
public class Hit extends IdentifiableSupport {

  private String event;
  private String entityClass;
  private String entityId;
  private int operatorId;
  private double weight;
  private long when;

  private int questionId;
  private int brickId;

  public static final double WEIGHT_MIN = 0.1;
  public static final double WEIGHT_MAX = 1;

  /**
   * Edit operation should be below .5, and save from .5 upwards.
   *
   * @return
   */

  @Id
  @Type(type = "int")
  @GeneratedValue(strategy = GenerationType.AUTO)
  public Serializable getId() {
    return super.getId();
  }


  @Index(name = "idx_hit_op")
  public int getOperatorId() {
    return operatorId;
  }

  public void setOperatorId(int operatorId) {
    this.operatorId = operatorId;
  }

  public double getWeight() {
    return weight;
  }

  public void setWeight(double weight) {
    //if (weight<WEIGHT_MIN || weight>WEIGHT_MAX)
    //  throw new PlatformRuntimeException("weight should be between" + WEIGHT_MIN+ " and "+WEIGHT_MAX);

    // remmed out for badges trick
    /*if (weight < WEIGHT_MIN)
      weight = WEIGHT_MIN;
    else if (weight > WEIGHT_MAX)
      weight = WEIGHT_MAX;  */

    this.weight = weight;
  }

  @Column(name = "whenx")
  @Index(name = "idx_hit_when")
  public long getWhen() {
    return when;
  }

  public void setWhen(long when) {
    this.when = when;
  }


  @Index(name = "idx_hit_entClass")
  public String getEntityClass() {
    return entityClass;
  }

  public void setEntityClass(String entityClass) {
    this.entityClass = entityClass;
  }

  @Index(name = "idx_hit_entId")
  public String getEntityId() {
    return entityId;
  }

  public void setEntityId(String entityId) {
    this.entityId = entityId;
  }


  @Index(name = "idx_hit_entEv")
  public String getEvent() {
    return event;
  }

  public void setEvent(String event) {
    this.event = event;
  }

  @Index(name = "idx_hit_questionId")
  public int getQuestionId() {
    return questionId;
  }

  public void setQuestionId(int questionId) {
    this.questionId = questionId;
  }

  @Index(name = "idx_hit_brickId")
  public int getBrickId() {
    return brickId;
  }

  public void setBrickId(int brickId) {
    this.brickId = brickId;
  }


  public static Hit newHit(Identifiable i, Operator logged, String event, double weight) {
    Hit hit = new Hit();
    hit.setEntityClass(PersistenceHome.deProxy(i.getClass().getName()));
    hit.setEntityId(i.getId() + "");
    if (logged!=null)
      hit.setOperatorId((Integer) logged.getId());
    hit.setWhen(System.currentTimeMillis());
    hit.setEvent(event);
    hit.setWeight(weight);

    return hit;
  }


  public static Hit getInstanceAndStore(Identifiable i, Operator logged, String event, double weight) throws StoreException {
    Hit hit = newHit(i, logged, event, weight);
    hit.store();
    return hit;
  }

  public static Set<Hit> getInstanceAndStore(Identifiable i, Question team,Collection<Operator> involved, String event, double weight) throws StoreException {
    Set hits = new HashSet();
    for (Operator person : involved) {
        hits.add(getInstanceAndStore(i, person,event, weight));
    }
    return hits;
  }

  public String toString() {
    return entityClass + " id: " + entityId  +" op id:" + operatorId +" event:"+ event+ " weight:" + weight + " when:" + JSP.w(new Date(when));
  }

  public static void removeDeleted(Date since) throws PersistenceException {

      CodeValueList valueList = PersistenceBricks.getPersistentEntities(IdentifiableSupport.class);
      valueList.sort();

      for (CodeValue codeValue : valueList.getList()) {

        String entityClass = codeValue.code;

        //get hitted ids
        String hql = "select distinct hit.entityId from " + Hit.class.getName() + " as hit where hit.entityClass = :entityClass";
        if (since!=null)
          hql = hql + " and hit.when > :when";
          OqlQuery oql = new OqlQuery(hql);
        oql.getQuery().setString("entityClass", entityClass);
        if (since!=null)
          oql.getQuery().setLong("when", since.getTime());

        List<String> ids = oql.list();

        if (JSP.ex(ids)) {
          //get entities hitted
          hql = "select hitted.id from " + entityClass + " as hitted";
          oql = new OqlQuery(hql);
          List<String> existingHittedIds = oql.list();
          ids.removeAll(existingHittedIds);

          for (String presumedEntityHittedId : ids) {

            Tracer.platformLogger.info("Hit repairing: not found "+entityClass+" of id "+presumedEntityHittedId);

            //delete all such hits
            hql = "select hit from " + Hit.class.getName() + " as hit where hit.entityClass = :entityClass and hit.entityId=:entityId";
            oql = new OqlQuery(hql);
            oql.getQuery().setString("entityClass", entityClass);
            oql.getQuery().setString("entityId", presumedEntityHittedId);
            List<Hit> damHits = oql.list();
            int i = 0;
            for (Hit damHit : damHits) {
              i++;
              damHit.remove();
              if (i % 20 == 0) {
                PersistenceContext pc = PersistenceContext.getDefaultPersistenceContext();
                pc.session.flush();
                pc.session.clear();
              }
            }
            //}
          }
        }
      }

  }



}
