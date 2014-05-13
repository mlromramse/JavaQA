package org.jblooming.ontology;

import javax.persistence.MappedSuperclass;
import java.util.Date;
import java.io.Serializable;

/**
 * @author Pietro Polsinelli ppolsinelli@open-lab.com
 */
@MappedSuperclass
public abstract class LoggableIdentifiableSupport extends IdentifiableSupport implements LoggableIdentifiable {

  protected Date lastModified;
  protected String lastModifier;
  protected String creator;
  protected Date creationDate;

  public LoggableIdentifiableSupport() {
  }

  public LoggableIdentifiableSupport(Serializable id) {
    this.id = id;
  }

  public Date getLastModified() {
    return lastModified;
  }

  public void setLastModified(Date lastModified) {
    this.lastModified = lastModified;
  }

  public String getLastModifier() {
    return lastModifier;
  }

  public void setLastModifier(String lastModifier) {
    this.lastModifier = lastModifier;
  }

  public String getCreator() {
    return creator;
  }

  public void setCreator(String creator) {
    this.creator = creator;
  }


  public Date getCreationDate() {
    return creationDate;
  }

  public void setCreationDate(Date creationDate) {
    this.creationDate = creationDate;
  }
}
