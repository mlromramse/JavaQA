package com.QA.messages;

import com.QA.QAOperator;
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
@Table(name = "qa_stickynote")
public class StickyNote extends LoggableIdentifiableSupport {

  private String type;
  private QAOperator author;
  private QAOperator receiver;
  private String message;
  private Date created;
  private Date read;


  public StickyNote() {
  }

  public StickyNote(Serializable id) {
    super(id);
  }

  public StickyNote(int id, String message) {
    super(id);
    this.message = message;
  }

  @Id
  @Type(type = "int")
  @GeneratedValue(strategy = GenerationType.AUTO)
  @DocumentId
  @FieldBridge(impl = IntegerBridge.class)
  public Serializable getId() {
    return super.getId();
  }

  @ManyToOne(targetEntity = QAOperator.class)
  @ForeignKey(name = "fk_sn_aut")
  @Index(name = "idx_sn_aut")
  @JoinColumn(name = "author")
  public QAOperator getAuthor() {
    return author;
  }

  public void setAuthor(QAOperator author) {
    this.author = author;
  }

  @ManyToOne(targetEntity = QAOperator.class)
  @ForeignKey(name = "fk_sn_rec")
  @Index(name = "idx_sn_rec")
  @JoinColumn(name = "receiver")
  public QAOperator getReceiver() {
    return receiver;
  }

  public void setReceiver(QAOperator receiver) {
    this.receiver = receiver;
  }

  @Lob
  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public Date getCreated() {
    return created;
  }

  public void setCreated(Date created) {
    this.created = created;
  }

  @Column(name="readx")
  public Date getRead() {
    return read;
  }

  public void setRead(Date read) {
    this.read = read;
  }


  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  @Transient
  public String getName() {
    return getMessage();
  }

  public JSONObject jsonify() {

    JSONObject ret = new JSONObject();
    ret.element("id", getId());
    ret.element("type", getType());
    ret.element("message", getMessage());
    ret.element("lastModifiedMillis", getLastModified().getTime());
    ret.element("creationMillis", getCreationDate().getTime());
    QAOperator auth = getAuthor();
    if (auth != null) {
      ret.element("authorId", auth.getId());
      ret.element("authorName", auth.getDisplayName());
    }
    QAOperator rec = getReceiver();
    if (rec != null) {
      ret.element("receiverId", rec.getId());
      ret.element("receiverName", rec.getDisplayName());
    }
    return ret;
  }
}
