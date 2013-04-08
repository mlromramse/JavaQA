package org.jblooming.waf.html.input.documentDrawer;

import org.jblooming.waf.html.core.JspHelper;
import org.jblooming.waf.html.button.ButtonLink;
import org.jblooming.ontology.Documentable;
import org.jblooming.remoteFile.Document;
import org.jblooming.remoteFile.BasicDocument;

import javax.servlet.jsp.PageContext;

/**
 * @author Pietro Polsinelli ppolsinelli@open-lab.com
 * @author Roberto Bicchierai rbicchierai@open-lab.com
 */
public class DocumentDrawer extends JspHelper {

  public boolean recurseOnChildren = true;
  public Documentable documentable;
  public Document currentDocument;
  public boolean sorted = false;
  public boolean drawOnlyRoots = true;
  public ButtonLink editLink;

  public DocumentDrawer(Documentable task) {
    super();
    this.urlToInclude = "/applications/teamwork/document/partDocumentDrawer.jsp";
    this.documentable = task;
  }

   public void drawDocumentable(PageContext pageContext)  {
    pageContext.getRequest().setAttribute(ACTION, "TASKPART");
    super.toHtml(pageContext);
  }

  public void drawDocument(BasicDocument document, PageContext pageContext) {
    currentDocument = document;
    if (drawOnlyRoots && document.getParent()!=null)
      return;
    pageContext.getRequest().setAttribute(ACTION, "DOCPART");
    super.toHtml(pageContext);

  }


  /**
   * @deprecated
   */
  public void toHtml(PageContext pageContext) {
    throw new RuntimeException("Call task and doc");
  }


}
