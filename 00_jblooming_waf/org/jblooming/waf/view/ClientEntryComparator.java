package org.jblooming.waf.view;

import org.jblooming.waf.exceptions.ActionException;

import java.util.Comparator;

/**
 * @author Pietro Polsinelli ppolsinelli@open-lab.com
 * @author Roberto Bicchierai rbicchierai@open-lab.com
 */
public class ClientEntryComparator implements Comparator {
  ClientEntries ces;
  boolean caseSensitive = false;

  public ClientEntryComparator(ClientEntries ces, boolean caseSensitive) {
    super();
    this.ces = ces;
    this.caseSensitive = caseSensitive;
  }

  public int compare(Object b, Object a) {
    String aS = (String) a;
    String bS = (String) b;

    try {
      String valueA = ces.getEntry(aS).stringValue();
      String valueB = ces.getEntry(bS).stringValue();

      if (valueA != null && valueB != null) {
        if (!caseSensitive) {
          valueA = valueA.toLowerCase();
          valueB = valueB.toLowerCase();
        }
        return valueB.compareTo(valueA);
      }

      else
        return 0;
    } catch (ActionException e) {
      return 0;
    }

  }
}
