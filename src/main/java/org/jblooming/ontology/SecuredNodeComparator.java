package org.jblooming.ontology;

import java.util.Comparator;

/**
 * User: Pietro Polsinelli
 * Date: 10-apr-2003
 * Time: 15.36.51
 * To change this template use Options | File Templates.
 */
public class SecuredNodeComparator implements Comparator {

  public int compare(Object o1, Object o2) {
    if (o1 == null || o2 == null || ((SecuredNodeWithAreaSupport) o1).getName() == null || ((SecuredNodeWithAreaSupport) o2).getName() == null)
      return 0;
    return ((SecuredNodeWithAreaSupport) o1).getName().compareTo(((SecuredNodeWithAreaSupport) o2).getName());
  }

}

