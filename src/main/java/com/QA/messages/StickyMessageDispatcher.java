package com.QA.messages;

import com.QA.QAOperator;
import org.jblooming.messaging.Message;
import org.jblooming.messaging.MessagingSystem;
import org.jblooming.oql.OqlQuery;
import org.jblooming.persistence.PersistenceHome;
import org.jblooming.persistence.hibernate.PersistenceContext;
import org.jblooming.scheduler.ExecutableSupport;
import org.jblooming.scheduler.JobLogData;
import org.jblooming.tracer.Tracer;
import org.jblooming.utilities.DateUtilities;

import java.util.Date;
import java.util.List;

public class StickyMessageDispatcher extends ExecutableSupport {

  public JobLogData run(JobLogData jobLogData) throws Exception {
    PersistenceContext pc = null;

    try {

      pc = PersistenceContext.getDefaultPersistenceContext();

      String hql = "from " + Message.class.getName() + " as mess where mess.media = :media";
      OqlQuery query = new OqlQuery(hql, pc);
      query.getQuery().setString("media", MessagingSystem.Media.STICKY.toString());
      List<Message> messages = query.list();

      for (Message message : messages) {
        if (message.getToOperator() != null) {
          String body = message.getMessageBody();
          if (message.getLink() != null)
            body = body + "<hr>link:&nbsp;" + message.getLink();
          StickyNote sn = new StickyNote();
          sn.setIdAsNew();

          if (message.getFromOperator() != null) {
            QAOperator frop = (QAOperator) PersistenceHome.findByPrimaryKey(QAOperator.class, message.getFromOperator().getId());
            sn.setAuthor(frop);
          }

          QAOperator top = (QAOperator) PersistenceHome.findByPrimaryKey(QAOperator.class, message.getToOperator().getId());
          sn.setReceiver(top);

          sn.setType(message.getSubject());
          sn.setMessage(body);
          sn.store();
        }
        message.remove();
      }
      pc.commitAndClose();

      jobLogData.notes = jobLogData.notes + "StickyMessageDispatcher executed on " + DateUtilities.dateAndHourToString(new Date());
    } catch (Throwable e) {
      Tracer.platformLogger.error("StickyMessageDispatcher error", e);
      if (pc != null) {
        pc.rollbackAndClose();
      }
      jobLogData.successfull = false;
    }

    return jobLogData;
  }
}


