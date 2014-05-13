package org.jblooming.remoteFile;

import org.jblooming.ontology.Node;
import org.jblooming.ontology.VersionComparator;
import org.jblooming.operator.User;
import org.jblooming.operator.Operator;
import org.jblooming.security.Permission;
import org.jblooming.persistence.exceptions.PersistenceException;


public class BasicDocument extends Document{
  public BasicDocumentBricks bricks = new BasicDocumentBricks(this);

  public void setParent(BasicDocument n) {
     parent = n;
   }

   public void setParentNode(Node node) {
     setParent((BasicDocument) node);
   }

  public BasicDocument getParent() {
    return (BasicDocument) parent;
  }

  public Node getParentNode() {
    return getParent();
  }

  public int compareVersionTo(BasicDocument doc) {
    VersionComparator vc = new VersionComparator();
    return vc.compare(this.getVersion(), doc.getVersion());
  }


  public boolean isEnabled(Operator logged) {
    return true ;
  }

}
