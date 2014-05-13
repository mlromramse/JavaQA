package com.QA;

import net.sf.json.JSONObject;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Type;
import org.hibernate.search.annotations.DocumentId;
import org.hibernate.search.annotations.FieldBridge;
import org.hibernate.search.bridge.builtin.IntegerBridge;
import org.jblooming.ontology.LoggableIdentifiableSupport;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "qa_contest")
public class Contest extends LoggableIdentifiableSupport {

  private String description;
  private QAOperator owner;
  private JSONObject calderon = new JSONObject();
  private Date startDate;
  private Date endDate;
  private boolean hidden = false;
  private boolean sticky = false;


  @Id
  @Type(type = "int")
  @GeneratedValue(strategy = GenerationType.AUTO)
  @DocumentId
  @FieldBridge(impl = IntegerBridge.class)
  public Serializable getId() {
    return super.getId();
  }

  @Lob
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  @ManyToOne(targetEntity = QAOperator.class)
  @ForeignKey(name = "fk_cont_own")
  @Index(name = "idx_cont_own")
  @JoinColumn(name = "ownerx")
  public QAOperator getOwner() {
    return owner;
  }

  public void setOwner(QAOperator owner) {
    this.owner = owner;
  }

  @Type(type = "org.jblooming.ontology.JSONObjectType")
  public JSONObject getCalderon() {
    return calderon;
  }

  public void setCalderon(JSONObject calderon) {
    this.calderon = calderon;
  }


  public Date getStartDate() {
    return startDate;
  }

  public void setStartDate(Date startDate) {
    this.startDate = startDate;
  }

  public Date getEndDate() {
    return endDate;
  }

  public void setEndDate(Date endDate) {
    this.endDate = endDate;
  }

  public boolean isHidden() {
    return hidden;
  }

  public void setHidden(boolean hidden) {
    this.hidden = hidden;
  }

  public boolean isSticky() {
    return sticky;
  }

  public void setSticky(boolean sticky) {
    this.sticky = sticky;
  }
}
