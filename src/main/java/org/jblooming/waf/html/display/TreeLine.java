package org.jblooming.waf.html.display;

import org.jblooming.ontology.Node;
import org.jblooming.waf.html.core.JspHelper;
import org.jblooming.waf.html.layout.Skin;

/**
 * Created by IntelliJ IDEA.
 * User: idigaeta
 * Date: 26-nov-2004
 * Time: 13.06.38
 * To change this template use File | Settings | File Templates.
 */
public class TreeLine extends JspHelper {

  public Node node;
  public Img image;
  public Skin skin;

  public TreeLine(Node node, Img image, Skin skin) {
    super("/commons/layout/partTreeLine.jsp");
    this.skin = skin;
    this.node = node;
    this.image = image;
  }
}

