package org.jblooming.ontology;

import java.io.Serializable;


public interface Identifiable  {
  /**
   * @deprecated
   */
  public int getIntId();

  public Serializable getId();

  public void setId(Serializable id);

   public String getName();

 
}
