package org.jblooming.ontology;

import javax.persistence.MappedSuperclass;
import java.util.HashSet;
import java.util.Set;
import java.util.List;

/**
 * @author Ilaria Di gaeta idigaeta@open-lab.com
 */
@MappedSuperclass
public abstract class SecuredDirectedGraphSupport extends SecuredLoggableHideableSupport {
  private Set<SecuredDirectedGraphSupport> previousNodes = new HashSet<SecuredDirectedGraphSupport>();
  private Set<SecuredDirectedGraphSupport> nextNodes = new HashSet<SecuredDirectedGraphSupport>();


  public Set<SecuredDirectedGraphSupport> getPreviousNodes() {
    return previousNodes;
  }

  public void setPreviousNodes(Set<SecuredDirectedGraphSupport> previousNodes) {
    this.previousNodes = previousNodes;
  }

  public Set<SecuredDirectedGraphSupport> getNextNodes() {
    return nextNodes;
  }

  public void setNextNodes(Set<SecuredDirectedGraphSupport> nextNodes) {
    this.nextNodes = nextNodes;
  }


  public List<SecuredDirectedGraphSupport> getDescendantNodes() {
    return null;
  }

  public List<SecuredDirectedGraphSupport> getPreceedingNodes() {
    return null;
  }
}
