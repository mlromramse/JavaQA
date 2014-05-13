package org.jblooming.ontology;

import java.util.Iterator;

/**
 * @author Pietro Polsinelli : ppolsinelli@open-lab.com
 */
public interface PerformantNode extends Node {

  static final String SEPARATOR = "^";

  String getAncestorIds();

  void setAncestorIds(String ancestorIds);

  int getDepth();

  Iterator<PerformantNodeSupport> getChildrenIterator();

  int getChildrenSize();

}
