package org.jblooming.messaging;

import org.jblooming.oql.OqlQuery;
import org.jblooming.persistence.hibernate.PersistenceContext;
import org.jblooming.scheduler.ExecutableSupport;
import org.jblooming.scheduler.JobLogData;
import org.jblooming.tracer.Tracer;
import org.jblooming.utilities.DateUtilities;
import org.jblooming.utilities.JSP;
import org.jblooming.waf.settings.ApplicationState;
import org.jblooming.waf.settings.Application;
import org.jblooming.waf.settings.I18n;
import org.jblooming.waf.constants.Fields;
import org.jblooming.operator.Operator;
import org.jblooming.agenda.CompanyCalendar;

import java.util.*;

public class DigestMessageDispatcher extends ExecutableSupport {

  public JobLogData run(JobLogData jobLogData) throws Exception {
    PersistenceContext pc = null;
    try {
      pc = PersistenceContext.getDefaultPersistenceContext();
      // get all messages in DIGEST channel
      String hql = "from " + Message.class.getName() + " as mess where mess.media = :media order by mess.toOperator, mess.received";
      OqlQuery query = new OqlQuery(hql);
      query.getQuery().setString("media", MessagingSystem.Media.DIGEST.toString());

      List<Message> messages = query.list();

      // loop for all messages in order to spread them in a map<Operator,map<Date,List<Messages>>>
      Map<Operator, Map<Date,List<Message>>> digests= new HashMap<Operator, Map<Date,List<Message>>>();

      for (Message message : messages) {

        //test already have an entry
        Map<Date,List<Message>> digestsForOperator=digests.get(message.getToOperator());
        if (digestsForOperator==null){
          digestsForOperator= new HashMap<Date,List<Message>>();
          digests.put(message.getToOperator(),digestsForOperator);
        }

        // get the day bagin for the message in order to group by date
        Date dayNormalized= new CompanyCalendar(message.getReceived()).setAndGetTimeToDayStart();
        List<Message> massagesOfDay=digestsForOperator.get(dayNormalized);
        if (massagesOfDay==null){
          massagesOfDay= new ArrayList<Message>();
          digestsForOperator.put(dayNormalized,massagesOfDay);
        }

        //add message to the day list
        massagesOfDay.add(message);

      }

      Operator systemOperator = Operator.getSystemOperator();

      // lop for all operators in digests
      for (Operator operator:digests.keySet()){

        String language = operator.getLanguage();

        String digested="";

        Map<Date, List<Message>> userMessages = digests.get(operator);
        for (Date aDay: userMessages.keySet()){
          digested+= JSP.makeTag("div","style=\"background-color:gold;\"",JSP.makeTag("font","size=5",DateUtilities.dateToFullString(aDay)));

          //loop for messages
          for (Message message:userMessages.get(aDay)){
            digested+=JSP.makeTag("font","size=4",message.getSubject())+"<br>";
            digested+=JSP.w(message.getMessageBody());
            if (JSP.ex(message.getLink())){
              digested+="<br>"+JSP.w(message.getLink());
            }
            digested+="<hr>";

            //remove the message
            message.remove();
          }
          digested+="<br><br>";
        }


        if (Fields.TRUE.equals(operator.getOption("MESSAGE_DIGESTER_BY_EMAIL"))){
          //Creates a new message for email/rss channels depending on user options
          Message digestedMessage= new Message();
          digestedMessage.setExpires(new Date(System.currentTimeMillis()+ CompanyCalendar.MILLIS_IN_3_MONTH));
          digestedMessage.setFromOperator(systemOperator);
          digestedMessage.setMedia(MessagingSystem.Media.EMAIL+"");
          digestedMessage.setMessageBody(digested);
          digestedMessage.setReceived(new Date());
          //digestedMessage.setStatus();
          Application app = ApplicationState.platformConfiguration.defaultApplication;
          String subject = I18n.getLabel("DIGEST_MESSAGE_SUBJECT", app.getName(), language);

          digestedMessage.setSubject(subject);
          digestedMessage.setToOperator(operator);

          digestedMessage.store();
        }
        if (Fields.TRUE.equals(operator.getOption("MESSAGE_DIGESTER_BY_RSS"))){
          //Creates a new message for email/rss channels depending on user options
          Message digestedMessage= new Message();
          digestedMessage.setExpires(new Date(System.currentTimeMillis()+ CompanyCalendar.MILLIS_IN_3_MONTH));
          digestedMessage.setFromOperator(systemOperator);
          digestedMessage.setMedia(MessagingSystem.Media.RSS+"");
          digestedMessage.setMessageBody(digested);
          digestedMessage.setReceived(new Date());
          //digestedMessage.setStatus();
          Application app = ApplicationState.platformConfiguration.defaultApplication;
          String subject = I18n.getLabel("DIGEST_MESSAGE_SUBJECT", app.getName(), language);

          digestedMessage.setSubject(subject);
          digestedMessage.setToOperator(operator);

          digestedMessage.store();
        }

      }


      pc.commitAndClose();
      jobLogData.notes = jobLogData.notes + "DigestMessageDispatcher executed on " + DateUtilities.dateAndHourToString(new Date());
    } catch (Throwable e) {
      Tracer.platformLogger.error("DigestMessageDispatcher error",e);
      Tracer.emailLogger.error("DigestMessageDispatcher error",e);
      jobLogData.successfull = false;
      if (pc!=null)
        pc.rollbackAndClose();
    }

    return jobLogData;
  }
}