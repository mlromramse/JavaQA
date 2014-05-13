package org.jblooming.waf.html.display;

import org.jblooming.waf.constants.Commands;
import org.jblooming.waf.html.core.JspHelper;
import org.jblooming.waf.html.state.Form;
import org.jblooming.waf.view.PageState;
import org.jblooming.waf.view.ClientEntry;
import org.jblooming.ontology.Identifiable;

import javax.servlet.jsp.PageContext;
import java.util.List;

/**
 * @author Pietro Polsinelli ppolsinelli@open-lab.com
 * @author Roberto Bicchierai rbicchierai@open-lab.com
 */
public class DeletePreviewer extends JspHelper {

  public Form form;
  public String commandToListen;
  public Identifiable delendo;
  public String cmdDelete = Commands.DELETE;
  public Class normalizeInstanceToSuperclass;

  public List<ClientEntry> additionalEntries;


  public DeletePreviewer(Form form) {
    this.form = form;
    urlToInclude = "/commons/layout/partDeletePreview.jsp";

  }

  public DeletePreviewer(Identifiable delendo, String commandToListen, String commandForDeletion, Form form) {
    this(form);
    this.commandToListen = commandToListen;
    this.cmdDelete = commandForDeletion;
    this.delendo = delendo;
  }

  public void toHtml(PageContext pageContext) {

    PageState ps = PageState.getCurrentPageState();

    if (delendo == null) {
      delendo = ps.mainObject;
    }

    if (commandToListen == null) {
      commandToListen = Commands.DELETE_PREVIEW;
    }

    if (commandToListen.equals(ps.getCommand()) || Commands.DELETE.equals(ps.getCommand()))
      super.toHtml(pageContext);

  }

}
