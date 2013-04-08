package org.jblooming.utilities;

import java.util.Comparator;

/**
 * Date: 20-mar-2003
 * Time: 17.41.56
 *
 * @author Pietro Polsinelli dev@open-lab.com
 */
public class StringIdComparator implements Comparator {

  public int compare(Object o1, Object o2) {
    return (new Integer(Integer.parseInt((String) o1)).compareTo(new Integer(Integer.parseInt((String) o2))));
  }
}
