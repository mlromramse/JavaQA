package org.jblooming.operator.businessLogic;

import org.jblooming.ApplicationException;
import org.jblooming.PlatformRuntimeException;
import org.jblooming.security.PlatformPermissions;
import org.jblooming.operator.Operator;
import org.jblooming.persistence.exceptions.PersistenceException;
import org.jblooming.persistence.exceptions.RemoveException;
import org.jblooming.waf.ActionController;
import org.jblooming.waf.constants.*;
import org.jblooming.waf.exceptions.ActionException;
import org.jblooming.waf.view.PageSeed;
import org.jblooming.waf.view.PageState;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Pietro Polsinelli : ppolsinelli@open-lab.com
 */
public class OperatorController implements ActionController {

  public Class operatorClass;
  public String hrefAfterDelete = "/commons/security/operator/operatorList.jsp";
  private  OperatorAction userAction = new OperatorAction();


  public OperatorController() {
    this.operatorClass = Operator.class;
  }

  public OperatorController(Class operatorClass) {
    this.operatorClass = operatorClass;
  }

  public OperatorController(Class operatorClass,OperatorAction userAction) {
    this.operatorClass = operatorClass;
    this.userAction=userAction;
  }

  public PageState perform(HttpServletRequest request, HttpServletResponse response) throws PersistenceException, ActionException, org.jblooming.security.SecurityException, ApplicationException {
    return perform(request, response,userAction);
  }

  public PageState perform(HttpServletRequest request, HttpServletResponse response,OperatorAction userAction) throws PersistenceException, ActionException, org.jblooming.security.SecurityException, ApplicationException {

    PageState pageState = PageState.getCurrentPageState(request);

    final String command = pageState.getCommand();
    // test for user permissions
    Operator logged = pageState.getLoggedOperator();

    if (logged != null && logged.hasPermissionFor(PlatformPermissions.operator_canWrite )) {
      if (command != null && command.equals(Commands.ADD)) {
        userAction.cmdAdd(pageState, operatorClass);

      } else if (command != null && command.equals(Commands.LOGOUT)) {
        pageState.setClientEntries(null);



      } else if (command != null && command.equals(Commands.EDIT) || Commands.DELETE_PREVIEW.equals(command)) {
        userAction.cmdEdit(pageState, Operator.class);

      } else if (Commands.SAVE.equals(command)) {
        try {
          userAction.cmdSave(pageState, operatorClass);
        } catch (ActionException e) {
          e.printStackTrace();
        }

      } else if (Commands.DELETE.equals(command)) {
        try {
          userAction.cmdDelete(pageState);
          PageSeed ps = new PageSeed(hrefAfterDelete); //request.getContextPath() +
          pageState.redirect(ps);
        } catch (RemoveException ex) {
          // in order to feedback operator il partDelete.jsp
          pageState.setAttribute(PlatformConstants.DELETE_EXCEPTION, ex);
        } catch (Exception ex) {
          throw new PlatformRuntimeException(ex);
        }

      } else if (Commands.FIND.equals(command)) {
        userAction.cmdFind(pageState);
      }

    } else { // user allowed in its own page only
      if (Commands.SAVE.equals(command)) {
        try {
          userAction.cmdSave(pageState, operatorClass);
        } catch (ActionException e) {
          e.printStackTrace();
        }
      } else if (Commands.EDIT.equals(command)) {
        userAction.cmdEdit(pageState, Operator.class);
      } else {
        // redirect to logged user form
        PageSeed edit = new PageSeed("operatorEditor.jsp");
        edit.setCommand(Commands.EDIT);
        edit.setMainObjectId(logged.getId());
        pageState.redirect(edit);
      }
    }
    return pageState;
  }
}

