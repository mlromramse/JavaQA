package org.jblooming.ontology;

import org.jblooming.security.GroupRole;
import com.sun.net.ssl.internal.www.protocol.https.HttpsURLConnectionOldImpl;

/**
 * @author Pietro Polsinelli ppolsinelli@open-lab.com
 * @author Roberto Bicchierai rbicchierai@open-lab.com
 */
public abstract class RelationalSupport extends IdentifiableSupport {

  protected abstract IdentifiableSupport getFirstComponent();
  protected abstract IdentifiableSupport getSecondComponent();

  public boolean equals(Object o) {
    return this.compareTo(o) == 0;
  }

  public int hashCode() {
    return getFirstComponent().hashCode() + getSecondComponent().hashCode();
  }


  public int compareTo(Object o) {
    if (this == o)
      return 0;
    if (o == null)
      return -1;
    else {
      RelationalSupport gr2 = (RelationalSupport) o;
      //group is heavier
      return ((getFirstComponent().compareTo(gr2.getFirstComponent())) * 2) + getSecondComponent().compareTo(gr2.getSecondComponent());
    }
  }
}
