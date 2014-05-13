package org.jblooming.messaging;

import org.jblooming.oql.OqlQuery;
import org.jblooming.persistence.hibernate.PersistenceContext;
import org.jblooming.scheduler.ExecutableSupport;
import org.jblooming.scheduler.JobLogData;
import org.jblooming.system.SystemConstants;
import org.jblooming.tracer.Tracer;
import org.jblooming.utilities.DateUtilities;
import org.jblooming.utilities.JSP;
import org.jblooming.waf.settings.ApplicationState;
import org.jblooming.operator.Operator;

import java.util.Date;
import java.util.List;

public class EmailMessageDispatcher extends ExecutableSupport {

  public JobLogData run(JobLogData jobLogData) throws Exception {
      PersistenceContext pc = null;
      try {
        pc = PersistenceContext.getDefaultPersistenceContext();
        String hql = "from " + Message.class.getName() + " as mess where mess.media = :media and (mess.lastTry is null or mess.lastTry < :nowMinusDelay)";
        OqlQuery query = new OqlQuery(hql);
        query.getQuery().setString("media", MessagingSystem.Media.EMAIL.toString());
        Date nmd = new Date(System.currentTimeMillis() - MessagingSystem.DELAY_BETWEEN_ATTEMPTS);
        query.getQuery().setTimestamp("nowMinusDelay", nmd);

        List<Message> messages = query.list();

        for (Message message : messages) {

          if (message.getNumberOfTries() > MessagingSystem.NUMBER_OF_ATTEMPTS) {
            message.remove();
            String err = "EmailMessageDispatcher: could not send mail message with parameters: to operator of id " + message.getToOperator();
            Tracer.platformLogger.error(err);
            Tracer.emailLogger.error(err);
          } else
            try {

              String fromEmail = null;
              Operator operator = message.getFromOperator();
              if (operator != null) {
                fromEmail = operator.getDefaultEmail();
                String displayName = operator.getDisplayName();
                if (JSP.ex(fromEmail, displayName) && fromEmail.indexOf("<") == -1 && fromEmail.indexOf(",") == -1) {
                  fromEmail = displayName + " <" + fromEmail + ">;";
                }

              }

              if (!JSP.ex(fromEmail)) {
                fromEmail = ApplicationState.getApplicationSetting(SystemConstants.FLD_MAIL_FROM);
                String an = ApplicationState.platformConfiguration.getDefaultApplication().getName();
                String anForced = ApplicationState.getApplicationSetting("MAIL_FROM_NAME");
                if (JSP.ex(anForced))
                  an = anForced;
                if (JSP.ex(fromEmail, an) && fromEmail.indexOf("<") == -1 && fromEmail.indexOf(",") == -1) {
                  fromEmail = an + " <" + fromEmail + ">;";
                }
              }

              if (fromEmail == null)
                fromEmail = "";

              if (message.getToOperator() != null) {
                Operator toOp = message.getToOperator();
                String toEmail = toOp.getDefaultEmail();
                if (toEmail != null) {
                  String body = message.getMessageBody();
                  if (message.getLink() != null)
                    body = body + "<hr>" + message.getLink();
                  // silvia added prefix to email from Teamwork
                  String subjectPrefix = ApplicationState.getApplicationSetting(SystemConstants.FLD_MAIL_SUBJECT);
                  MailHelper.sendHtmlMail(fromEmail, toEmail, JSP.w(subjectPrefix) + (JSP.ex(subjectPrefix) ? " " : "") + message.getSubject(), body);
                }
              }
              message.remove();
            } catch (Throwable e) {
              Tracer.platformLogger.error("Problem sending e-mail", e);
              Tracer.emailLogger.error("Problem sending e-mail", e);
              message.setNumberOfTries(message.getNumberOfTries() + 1);
              message.setLastTry(new Date());
              message.store();
            }

          message.remove();
        }

        pc.commitAndClose();
        jobLogData.notes = jobLogData.notes + "EmailMessageDispatcher executed on " + DateUtilities.dateAndHourToString(new Date());
      } catch (Throwable e) {
        Tracer.platformLogger.error("EmailMessageDispatcher error", e);
        Tracer.emailLogger.error("EmailMessageDispatcher error", e);
        jobLogData.successfull = false;
        if (pc != null)
          pc.rollbackAndClose();
      }
    return jobLogData;
  }
}
