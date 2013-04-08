package com.QA.waf.settings;

import com.QA.*;
import com.QA.messages.StickyNote;
import com.QA.rank.Hit;
import com.QA.waf.QAScreenApp;
import org.hibernate.MappingException;
import org.jblooming.persistence.hibernate.HibernateFactory;
import org.jblooming.persistence.hibernate.PlatformAnnotationConfiguration;
import org.jblooming.security.Permissions;
import org.jblooming.tracer.Tracer;
import org.jblooming.utilities.JSP;
import org.jblooming.waf.AccessControlFilter;
import org.jblooming.waf.ScreenBasic;
import org.jblooming.waf.settings.ApplicationState;
import org.jblooming.waf.settings.ApplicationSupport;
import org.jblooming.waf.settings.I18n;
import org.jblooming.waf.settings.PlatformConfiguration;

import javax.servlet.jsp.PageContext;
import java.io.File;
import java.net.URL;
import java.util.SortedMap;

public class QA extends ApplicationSupport {
  public QA() {
    super(QAOperator.class, new Permissions());

    // ovverride the default command controller
    //ApplicationState.commandController = new TeamworkCommandController();

    ApplicationState.platformConfiguration.schedulerRunsByDefault = false;
  }

  /**
   * used in inerithed class
   */
  protected QA(Class defaultOperatoSubClass, Permissions[] permissionsImpl) {
    super(defaultOperatoSubClass, permissionsImpl);
  }

  public boolean isLoginCookieEnabled() {
    return true;
  }

  public String getName() {
    return "QA";
  }

  public String getRootFolder() {
    return "applications/QA";
  }

  public void configurePersistence(PlatformConfiguration pc) throws Exception {
    try {

      PlatformAnnotationConfiguration hibConfiguration = (PlatformAnnotationConfiguration) HibernateFactory.getConfig();

      URL ce = HibernateFactory.class.getClassLoader().getResource("qa.hbm.xml");
      hibConfiguration.addURL(ce);
      hibConfiguration.addAnnotatedClass(Tag.class);
      hibConfiguration.addAnnotatedClass(Category.class);
      //hibConfiguration.addAnnotatedClass(Badge.class);
      hibConfiguration.addAnnotatedClass(Question.class);
      hibConfiguration.addAnnotatedClass(QuestionRevision.class);
      hibConfiguration.addAnnotatedClass(Answer.class);
      hibConfiguration.addAnnotatedClass(AnswerRevision.class);
      hibConfiguration.addAnnotatedClass(Comment.class);
      hibConfiguration.addAnnotatedClass(Upvote.class);
      hibConfiguration.addAnnotatedClass(Hit.class);
      hibConfiguration.addAnnotatedClass(StickyNote.class);

    } catch (MappingException e) {
      throw new RuntimeException(e);
    }

  }

  public void configureFreeAccess(PlatformConfiguration pc) {

    AccessControlFilter.freeFolders.add("applications" + File.separator + "QA" + File.separator + "site");
    AccessControlFilter.freeFolders.add("applications" + File.separator + "QA" + File.separator + "talk");
    AccessControlFilter.securedSubFolders.add("applications" + File.separator + "QA" + File.separator + "talk"+ File.separator + "write");
    AccessControlFilter.freeFolders.add("applications" + File.separator + "QA" + File.separator + "show");
    //AccessControlFilter.freeFolders.add("applications" + File.separator + "QA" + File.separator + "write");
    AccessControlFilter.freeFolders.add("applications" + File.separator + "QA" + File.separator + "screen");
    AccessControlFilter.freeFolders.add("applications" + File.separator + "QA" + File.separator + "css");
    AccessControlFilter.freeFolders.add("applications" + File.separator + "QA" + File.separator + "js");

    AccessControlFilter.freeFiles.add("");
    AccessControlFilter.freeFiles.add("index.jsp");
    AccessControlFilter.freeFiles.add("mailing.jsp");
    AccessControlFilter.freeFiles.add("applications");
    AccessControlFilter.freeFiles.add("applications" + File.separator + "index.jsp");
    AccessControlFilter.freeFiles.add("applications" + File.separator + "QA");
    AccessControlFilter.freeFiles.add("applications" + File.separator + "QA" + File.separator + "index.jsp");
    /*AccessControlFilter.freeFiles.add("applications" + File.separator + "QA" + File.separator + "manage"+ File.separator + "manifesto.jsp");
    AccessControlFilter.freeFiles.add("applications" + File.separator + "QA" + File.separator + "manage"+ File.separator + "parts" + File.separator +  "manifestPassword.jsp");
    AccessControlFilter.freeFiles.add("applications" + File.separator + "QA" + File.separator + "manage"+ File.separator + "proposal.jsp");*/
    //AccessControlFilter.freeFiles.add("applications" + File.separator + "QA" + File.separator + "ajax"+ File.separator + "ajaxLoginPendingURLUpdater.jsp");
  }



  public void configureNeedingPersistence(PlatformConfiguration pc) {

    /*try {
      MpJobsLauncher.launch("system");
    } catch (Exception e) {
      Tracer.logExceptionOnPlatformOrOther(e);
    } */

  }

  public ScreenBasic getDefaultScreenInstance() {
    return new QAScreenApp();
  }


  public void configureNeedingPageContext(PageContext pageContext) {

    //here insert from i18n
    SortedMap<String, I18n.I18nEntry> i18nGS = I18n.getEntriesForApplication("QA");
    for (String code : i18nGS.keySet()) {
      if (JSP.ex(code) && code.startsWith("GLOBALSETTINGS_")) {
        ApplicationState.applicationSettings.put(code.substring("GLOBALSETTINGS_".length()), i18nGS.get(code).getLabel("QA","it"));
      }
    }


    try {
      pageContext.include("/applications/QA/settings/badges.jsp");
    } catch (Throwable e) {
      Tracer.platformLogger.error(e);
    }
  }

  public void applicationDestroy(){
  }

}
