package org.jblooming.anagraphicalData;

import java.util.Comparator;

/**
 * Date: 2-gen-2003
 * Time: 17.25.11
 *
 * @author Pietro Polsinelli dev@open-lab.com
 */
public class AnagraphicalDataComp implements Comparator {

  public int compare(Object first, Object second) {
    int o1 = ((AnagraphicalData) first).getOrderFactor();
    int o2 = ((AnagraphicalData) second).getOrderFactor();
    return (o1-o2);
  }
}
