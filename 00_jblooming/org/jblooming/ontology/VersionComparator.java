package org.jblooming.ontology;

import org.jblooming.utilities.NumberUtilities;

import java.util.Comparator;

/**
 * @author pietro polsinelli info@twproject.com
 */
public class VersionComparator implements Comparator {

  public int compare(Object o1, Object o2) {
    if (o1 == null || o2 == null)
      return 0;

    String s1 = (String) o1;
    String s2 = (String) o2;

    /**
     * numeric case
     */
    try {

      return new Integer(Integer.parseInt(s1)).compareTo(new Integer(Integer.parseInt(s2)));

    } catch (NumberFormatException e) {

      /**
       * roman case
       */
      try {

        return new Integer(NumberUtilities.romanToInt(s1)).compareTo(new Integer(NumberUtilities.romanToInt(s2)));

      } catch (NumberFormatException e1) {

        /**
         * alpha case
         */
        return s1.compareTo(s2);
      }
    }
  }
}
