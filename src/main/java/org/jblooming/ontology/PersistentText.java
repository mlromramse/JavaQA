package org.jblooming.ontology;

import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author Pietro Polsinelli ppolsinelli@open-lab.com
 */

@Entity
@Table(name = "_persistenttext")
public class  PersistentText extends IdentifiableSupport {

  private String text;

  @Id
  @Type(type = "int")
  @GeneratedValue(strategy = GenerationType.AUTO)
  public Serializable getId() {
      return super.getId();
  }

  @Lob
  //@Type(type = "org.hibernate.type.TextType")
  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

}
