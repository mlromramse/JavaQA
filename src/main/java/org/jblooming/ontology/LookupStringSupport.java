package org.jblooming.ontology;

import javax.persistence.MappedSuperclass;

/**
 * LookupSupport still uses by default id as value; this handles cases where you have to carry a specific String as value
 *
 * @author Pietro Polsinelli ppolsinelli@open-lab.com
 * @author Roberto Bicchierai rbicchierai@open-lab.com
 */
@MappedSuperclass
public class LookupStringSupport extends LookupSupport implements LookupString {

  private String stringValue;

  public String getStringValue() {
    return stringValue;
  }

  public void setStringValue(String stringValue) {
    this.stringValue = stringValue;
  }

}
