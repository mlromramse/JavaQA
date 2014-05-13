package org.jblooming.waf;

import org.jblooming.waf.html.button.ButtonLink;
import org.jblooming.waf.html.button.Button;
import org.jblooming.waf.html.button.ButtonSupport;
import org.jblooming.waf.html.display.Img;
import org.jblooming.waf.view.PageState;
import org.jblooming.ontology.Identifiable;
import org.jblooming.security.Permission;

/**
 * (c) Open Lab - www.open-lab.com
 * Date: Oct 29, 2007
 * Time: 4:17:26 PM
 */
public interface EntityViewerBricks {

  public EntityLinkSupport getLinkSupportForEntity(Identifiable i, PageState pageState);

  public class EntityLinkSupport {
    public ButtonSupport bs;
    public Img icon;
    public Img smallIcon;
    public String shortDescription;
    public String longDescription;
    public Permission readPermission;

    public ButtonSupport getButton() {
      return bs;
    }
  }


}
