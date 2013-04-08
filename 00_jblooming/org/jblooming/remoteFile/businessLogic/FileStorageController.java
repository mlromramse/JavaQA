package org.jblooming.remoteFile.businessLogic;

import org.jblooming.waf.ActionController;
import org.jblooming.waf.SessionState;
import org.jblooming.waf.constants.Commands;
import org.jblooming.waf.constants.PlatformConstants;
import org.jblooming.waf.exceptions.ActionException;
import org.jblooming.waf.view.PageState;
import org.jblooming.waf.view.PageSeed;
import org.jblooming.persistence.exceptions.PersistenceException;
import org.jblooming.persistence.exceptions.RemoveException;
import org.jblooming.ApplicationException;
import org.jblooming.remoteFile.BasicDocument;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class FileStorageController implements ActionController {


  public Class documentClass;


  public FileStorageController (Class docClass) {
    this.documentClass = docClass;
  }

  public FileStorageController () {
    this.documentClass = BasicDocument.class;
  }
  public PageState perform(HttpServletRequest request, HttpServletResponse response)
    throws PersistenceException, ActionException, org.jblooming.security.SecurityException, ApplicationException {

    SessionState sessionState = SessionState.getSessionState(request);
    PageState pageState = sessionState.getPageState(request, response);

    FileStorageAction fsa = new FileStorageAction(pageState);
    String command = pageState.getCommand();

    if (Commands.ADD.equals(command)) {
      fsa.cmdAdd();

    } else if (Commands.EDIT.equals(command) || Commands.DELETE_PREVIEW.equals(command))
      fsa.cmdEdit();

    else if (Commands.SAVE.equals(command))
      fsa.cmdSave();

    else if (Commands.DELETE.equals(command)) {
        try {
          fsa.cmdDelete();
          PageSeed   ps = new PageSeed ("fileStorageList.jsp");
          pageState.redirect(ps);
        } catch (RemoveException ex) {
          // in order to feedback operator in partDelete.jsp
          pageState.setAttribute(PlatformConstants.DELETE_EXCEPTION, ex);
        }
    }

    return pageState;
  }


}
