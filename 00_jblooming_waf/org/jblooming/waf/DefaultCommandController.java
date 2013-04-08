package org.jblooming.waf;

import org.jblooming.ApplicationException;
import org.jblooming.messaging.MailHelper;
import org.jblooming.ontology.Identifiable;
import org.jblooming.ontology.IdentifiableSupport;
import org.jblooming.ontology.SerializedList;
import org.jblooming.persistence.PersistenceHome;
import org.jblooming.persistence.exceptions.PersistenceException;
import org.jblooming.system.SystemConstants;
import org.jblooming.tracer.Tracer;
import org.jblooming.utilities.JSP;
import org.jblooming.utilities.ReflectionUtilities;
import org.jblooming.utilities.StringUtilities;
import org.jblooming.waf.constants.Commands;
import org.jblooming.waf.exceptions.ActionException;
import org.jblooming.waf.html.container.Container;
import org.jblooming.waf.html.core.JspIncluder;
import org.jblooming.waf.html.input.SmartCombo;
import org.jblooming.waf.html.state.ScreenElementStatus;
import org.jblooming.waf.settings.I18n;
import org.jblooming.waf.settings.businessLogic.I18nController;
import org.jblooming.waf.view.ClientEntry;
import org.jblooming.waf.view.PageState;

import javax.mail.internet.MimeMultipart;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Roberto Bicchierai rbicchierai@open-lab.com
 *         Date: 14-feb-2006 : 18.01.30
 */
public class DefaultCommandController implements ActionController {

  PageState pageState;
  SessionState sm;
  String domId;


  public PageState perform(HttpServletRequest request, HttpServletResponse response) throws PersistenceException, ActionException, org.jblooming.security.SecurityException, ApplicationException, IOException {

    sm = SessionState.getSessionState(request);
    boolean debug = false;
    pageState = PageState.getCurrentPageState();
    domId = pageState.getEntry(SystemConstants.DOM_ID).stringValueNullIfEmpty();
    response.setContentType("text/html");
    try {

      if (pageState != null && pageState.getCommand() != null) {
        String command = pageState.getCommand();
        if (debug) {
          Tracer.platformLogger.info(" System.out commands.jsp: " + command + "\n" + pageState.getClientEntries().toString());
        }


        if (command.equals(Commands.UPDATE_WIN_SIZE)) {
          cmdUpdateWinSize();

        } else if (command.equals(Commands.CMD_MOVE)) {
          cmdMove();

        } else if (command.equals(Commands.CMD_COLLAPSE)) {
          cmdCollapse();

        } else if (command.equals(Commands.CMD_RESTORE)) {
          cmdRestore();

        } else if (command.equals(Commands.CMD_HIDE)) {
          cmdHide();

        } else if (command.equals(Commands.CMD_ICONIZE)) {
          cmdIconize();

        } else if (command.equals(Commands.CMD_SHOW)) {
          cmdShow();

        } else if (command.equals(Commands.CMD_RESIZE)) {
          cmdResize();

        } else if (command.equals(I18n.CMD_STORE_LABEL)) {
          new I18nController().perform(request, response);


          // call generic controller
        } else if ("CALLCONTR".equals(command)) {
          cmdCallController(request, response);


        } else if ("SAVE_IN_OPT".equalsIgnoreCase(command)) {
          cmdSaveEntryInOptions();

        } else if ("sendPdfByEmail".equalsIgnoreCase(command)) {
          cmdSendMail();
        }

      }
    } catch (Throwable e) {
      Tracer.platformLogger.error(e);
    }
    return pageState;
  }

  private void cmdSaveEntryInOptions() throws PersistenceException {
    String name = pageState.getEntry("CEName").stringValueNullIfEmpty();
    String value = pageState.getEntry("CEValue").stringValueNullIfEmpty();
    if (JSP.ex(name)) {
      ClientEntry ce = pageState.getEntryOrDefault(name);
      if (JSP.ex(value)) {
        ce.setValue(value);
        pageState.saveEntriesInDefaults();
      }
    }
  }

  private void cmdCallController(HttpServletRequest request, HttpServletResponse response) {
    //"executeCommand(\"CALLCONTR\",\"CTCL=" + controller.getName() + "&CTRM=" + command + "&OBID=" + objId +  "\");";
    //what if I have to pass more than one argument? Just set it as concainschifated value of  OBID
    String className = pageState.getEntry("CTCL").stringValueNullIfEmpty();
    String cmd = pageState.getEntry("CTRM").stringValueNullIfEmpty();
    String obId = pageState.getEntry("OBID").stringValueNullIfEmpty();

    if (JSP.ex(className) && JSP.ex(cmd)) { //&& JSP.ex(obId)
      try {
        Class<? extends ActionController> controllerCl = (Class<? extends ActionController>) Class.forName(className);
        Constructor cWithPageState = null;
        try {
          cWithPageState = controllerCl.getConstructor(PageState.class);
        } catch (NoSuchMethodException e) {
        } catch (SecurityException e) {
        }
        ActionController controller = null;
        if (cWithPageState != null) {
          controller = (ActionController) cWithPageState.newInstance(pageState);
        } else
          controller = controllerCl.newInstance();

        pageState.command = cmd;
        pageState.mainObjectId = obId;
        controller.perform(request, response);
      } catch (Throwable e) {
        Tracer.platformLogger.error("DefaultCommandController: CallController. classname:" + className + " object id:" + obId, e);
      }

    } else {
      Tracer.platformLogger.error("DefaultCommandController: CallController. ControllerClass param missing, or command missing"); //or obj id missing,
    }
  }

  private void cmdResize() throws ApplicationException {
    if (!domId.startsWith(JspIncluder.DOM_ID)) {
      ScreenElementStatus cs = sm.screenElementsStatus.get(domId);
      if (cs == null)
        cs = new ScreenElementStatus(domId);
      String w = pageState.getEntry("W").stringValueNullIfEmpty();
      String h = pageState.getEntry("H").stringValueNullIfEmpty();
      cs.w = w;
      cs.h = h;

      sm.screenElementsStatus.put(domId, cs);
    }
  }

  private void cmdHide() throws ApplicationException {
    if (!domId.startsWith(JspIncluder.DOM_ID)) {
      ScreenElementStatus cs = sm.screenElementsStatus.get(domId);
      if (cs == null)
        cs = new ScreenElementStatus(domId);
      cs.status = Container.HIDDEN;
      sm.screenElementsStatus.put(domId, cs);
    }
  }

  private void cmdIconize() throws ApplicationException {
    if (!domId.startsWith(JspIncluder.DOM_ID)) {
      ScreenElementStatus cs = sm.screenElementsStatus.get(domId);
      if (cs == null)
        cs = new ScreenElementStatus(domId);
      cs.status = Container.ICONIZED;
      sm.screenElementsStatus.put(domId, cs);
    }
  }

  private void cmdCollapse() throws ApplicationException {
    if (!domId.startsWith(JspIncluder.DOM_ID)) {
      ScreenElementStatus cs = sm.screenElementsStatus.get(domId);
      if (cs == null)
        cs = new ScreenElementStatus(domId);
      cs.status =  Container.COLLAPSED;
      sm.screenElementsStatus.put(domId, cs);
    }
  }

  private void cmdRestore() throws ApplicationException {
    if (!domId.startsWith(JspIncluder.DOM_ID)) {
      ScreenElementStatus cs = sm.screenElementsStatus.get(domId);
      if (cs == null)
        cs = new ScreenElementStatus(domId);
      cs.status = Container.DEFAULT;
      sm.screenElementsStatus.put(domId, cs);
    }
  }

  private void cmdShow() throws ApplicationException {
    if (!domId.startsWith(JspIncluder.DOM_ID)) {
      ScreenElementStatus cs = sm.screenElementsStatus.get(domId);
      if (cs == null)
        cs = new ScreenElementStatus(domId);
      cs.status = Container.DEFAULT ;
      sm.screenElementsStatus.put(domId, cs);
    }
  }

  private void cmdMove() throws ApplicationException {
    if (!domId.startsWith(JspIncluder.DOM_ID)) {
      int x = pageState.getEntry("X").intValueNoErrorCodeNoExc();
      int y = pageState.getEntry("Y").intValueNoErrorCodeNoExc();
      ScreenElementStatus cs = sm.screenElementsStatus.get(domId);
      if (cs == null)
        cs = new ScreenElementStatus(domId);
      cs.x = x;
      cs.y = y;
      sm.screenElementsStatus.put(domId, cs);
    }
  }

  private void cmdUpdateWinSize() throws ActionException {
    ClientEntry ceW = pageState.getEntry("PAGE_WIDTH");
    ClientEntry ceH = pageState.getEntry("PAGE_HEIGHT");
    if (ceW.stringValue() != null && ceH.stringValue() != null) {
      String width = ceW.stringValue();
      String height = ceH.stringValue();
      try {
        pageState.getSessionState().setPageHeight(Integer.parseInt(height));
        pageState.getSessionState().setPageWidth(Integer.parseInt(width));
      } catch (Exception e) {
        Tracer.platformLogger.warn(e);
      }
    }
  }

  private void cmdSendMail() throws ActionException {
    String args = pageState.getEntry("ARGS").stringValueNullIfEmpty();
    if (JSP.ex(args)) {
      String filePath = pageState.getEntry("FILE_PATH").stringValue();
      String from = pageState.getEntry("FROM").stringValue();
      String subject = pageState.getEntry("SUBJECT").stringValue();
      String body = pageState.getEntry("BODY").stringValue();
      String objClassName = pageState.getEntry("OBJ_CLASS_NAME").stringValue();
      String methodName = pageState.getEntry("METHOD_NAME").stringValue();

      File filePdf = new File(filePath);
      try {
        byte[] bytesOfFile = new byte[(int) filePdf.length()];
        DataInputStream dis = new DataInputStream(new FileInputStream(filePdf));
        dis.readFully(bytesOfFile);
        dis.close();
        Class objClass = Class.forName(objClassName);
        Method method = objClass.getMethod(methodName);
        String[] ids = args.split(";");
        for (int i = 0; i < ids.length; i++) {
          String id = ids[i];
          Object resource = PersistenceHome.findByPrimaryKey(objClass, id);
          if (resource != null) {
            String to = (String) method.invoke(resource);//resource.getDefaultEmail();
            MailHelper.sendMailWithAttachFile(new MimeMultipart(), from, to, subject, body, filePdf.getName(), bytesOfFile, "UTF-8", "application/pdf");
          }
        }

      } catch (Throwable e) {
        Tracer.platformLogger.error(e);
        pageState.addClientEntry("INFO_SEND_MAIL", "ERROR SENDING AN EMAIL: " + e.toString());
        throw new ActionException("ERROR SENDING AN EMAIL: " + e);

      } finally {
        filePdf.deleteOnExit();
      }
      pageState.addClientEntry("INFO_SEND_MAIL", "email sent correctly");

    }
  }

}
