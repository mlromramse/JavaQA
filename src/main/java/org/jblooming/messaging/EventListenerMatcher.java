package org.jblooming.messaging;

import org.jblooming.PlatformRuntimeException;
import org.jblooming.agenda.CompanyCalendar;
import org.jblooming.ontology.PerformantNode;
import org.jblooming.ontology.SerializedMap;
import org.jblooming.operator.Operator;
import org.jblooming.oql.OqlQuery;
import org.jblooming.persistence.PersistenceHome;
import org.jblooming.persistence.exceptions.PersistenceException;
import org.jblooming.persistence.exceptions.StoreException;
import org.jblooming.persistence.hibernate.PersistenceContext;
import org.jblooming.scheduler.ExecutableSupport;
import org.jblooming.scheduler.JobLogData;
import org.jblooming.security.Group;
import org.jblooming.tracer.Tracer;
import org.jblooming.utilities.*;
import org.jblooming.waf.constants.Fields;
import org.jblooming.waf.constants.OperatorConstants;
import org.jblooming.waf.settings.Application;
import org.jblooming.waf.settings.ApplicationState;
import org.jblooming.waf.settings.I18n;

import java.io.IOException;
import java.util.*;

public class EventListenerMatcher extends ExecutableSupport {

  public JobLogData run(JobLogData jobLogData) throws Exception {
    PersistenceContext pc = null;

    try {

      pc = PersistenceContext.getDefaultPersistenceContext();

      CompanyCalendar cc = new CompanyCalendar();
      cc = new CompanyCalendar();
      cc.add(CompanyCalendar.DAY_OF_YEAR, -7);
      //remove old somthing happened if more then 100

      String hqlSomethingHappened = "select count(sh) from " + SomethingHappened.class.getName() + " as sh";
      OqlQuery oql = new OqlQuery(hqlSomethingHappened);
      long count = (Long) oql.uniqueResultNullIfEmpty();
      if (count > 100) {
        cc = new CompanyCalendar();
        cc.add(CompanyCalendar.DAY_OF_YEAR, -1);
        OqlQuery oql2 = new OqlQuery("delete from " + SomethingHappened.class.getName() + " as sh where sh.happenedAt < :when");
        oql2.getQuery().setDate("when", cc.getTime());
        oql2.getQuery().executeUpdate();
      }

      pc.checkPoint();
      // get all the events
      OqlQuery eventQH = new OqlQuery("from " + SomethingHappened.class.getName());
      List<SomethingHappened> events = eventQH.list();

      // loop through each event
      for (SomethingHappened event : events) {
        try {
          List<Listener> listeners = getListeners(event);
          // these listeners already match the event
          // generate the appropriate message

          for (Listener l : listeners) {
            generateAndPersistMessage(l, event);
            l.setLastMatchingDate(new Date());
            if (l.isOneShot())
              l.remove();
            else
              l.store();
          }
        } catch (Throwable e) {
          Tracer.platformLogger.error("EventListenerMatcher error", e);
        }
        event.remove();
      }

      /*for (SomethingHappened event : events)
        event.remove();  */

      pc.commitAndClose();

      jobLogData.notes = jobLogData.notes + "EventListenerMatcher executed on " + DateUtilities.dateAndHourToString(new Date());
    } catch (Throwable e) {
      Tracer.platformLogger.error("EventListenerMatcher error", e);
      if (pc != null) {
        pc.rollbackAndClose();
      }
      jobLogData.successfull = false;
    }
    return jobLogData;
  }

  private List<Listener> getListeners(SomethingHappened event) throws PersistenceException {

    List<Listener> listeners = new ArrayList();
    List<Listener> listeners2 = new ArrayList();
    if (event.getHappeningExpiryDate() == null || event.getHappeningExpiryDate().getTime() > System.currentTimeMillis()) {

      List<String> ids = new ArrayList();

      //does event refer to node ?
      boolean refersToNode = false;
      Class main = null;
      try {
        main = Class.forName(event.getTheClass());
      } catch (ClassNotFoundException e) {
        throw new PlatformRuntimeException(e);
      }
      if (event.getTheClass() != null) {

        refersToNode = ReflectionUtilities.extendsOrImplements(main, PerformantNode.class);

      }
      //get ancestors
      if (refersToNode) {
        try {
          PerformantNode pn = (PerformantNode) PersistenceHome.findByPrimaryKey(main, event.getIdentifiableId());
          if (pn != null && pn.getAncestorIds() != null) {
            ids = StringUtilities.splitToList(pn.getAncestorIds(), PerformantNode.SEPARATOR);
          }
        } catch (Throwable e) {
          //do nothing
        }
      }
      ids.add(event.getIdentifiableId());

      String hql = "from " + Listener.class.getName() + " as l ";
      hql = hql + " where l.theClass=:theClass ";
      hql = hql + " and (l.identifiableId in (:identifiableIds) or l.identifiableId is null) ";
      hql = hql + " and (l.eventType = :eventType or l.eventType is null) ";
      hql = hql + " and ( l.validityEnd is null or (l.validityEnd >= :nownow) )";
      hql = hql + " and ( l.validityStart is null or (l.validityStart <= :nownow) )";

      OqlQuery oql = new OqlQuery(hql);
      oql.getQuery().setString("theClass", event.getTheClass());
      oql.getQuery().setParameterList("identifiableIds", ids);
      oql.getQuery().setString("eventType", event.getEventType());
      oql.getQuery().setTimestamp("nownow", event.getHappenedAt());

      listeners = oql.list();
      for (Listener l : listeners) {
        if (l.getIdentifiableId().equals(event.getIdentifiableId()) || l.isListenDescendants()) {
          listeners2.add(l);
        }
      }
    }
    return listeners2;
  }

  public static void generateAndPersistMessage(Listener l, SomethingHappened s) throws StoreException, IOException {
    Set<Operator> ops = new HashSet<Operator>();

    Group grp = l.getGroup();
    if (grp == null) {
      Operator op2 = l.getOwner();
      ops.add(op2);
    } else {
      ops.addAll(grp.getOperators());
    }

    for (Operator op : ops) {

      // avoid to bother user with its events  or notify disabled users
      if (!op.isEnabled() || (op.equals(s.getWhoCausedTheEvent()) && !Fields.TRUE.equals(op.getOption(OperatorConstants.NOTIFY_MY_EVENTS_TO_MYSELF))))
        continue;

      Application app = ApplicationState.platformConfiguration.defaultApplication;
      String language = op.getLanguage();

      String template = I18n.getLabel(JSP.w(s.getMessageTemplate()), app.getName(), language);

      SerializedMap<String, String> mps = s.getMessageParams();
      if (mps != null) {

        for (String key : mps.keySet()) {
          String value = mps.get(key);
          if (value == null || value.trim().length() == 0)
            value = "-";
          else
            value = I18n.getLabel(value, app.getName(), language);
          template = StringUtilities.replaceAllNoRegex(template, "$" + key + "$", value);
        }
      }

      List<String> medias = StringUtilities.splitToList(l.getMedia(), ",");
      for (String media : medias) {
        Message message = new Message();
        if (op != null)
          message.setToOperator(op);
          //31/03/2006 commented out by Pietro when trying to get TLog to talk to task -> after reset as it was
        else if (s.getWhoCausedTheEvent() != null)
          message.setToOperator(s.getWhoCausedTheEvent());

        message.setExpires(new Date(System.currentTimeMillis() + CompanyCalendar.MILLIS_IN_3_MONTH));

        message.setMedia(media);

        if (s.getWhoCausedTheEvent() != null)
          message.setFromOperator(s.getWhoCausedTheEvent());

        // if there is a param called "subject" or "SUBJECT" it will be appended to subject
        String addSubject = JSP.w(mps.get("subject")) + "" + JSP.w(mps.get("SUBJECT"));
        if (JSP.ex(addSubject))
          message.setSubject(I18n.getLabel("EVENT_" + s.getEventType(), app.getName(), language) + ": " + addSubject);
        else if (JSP.ex(mps.get("SUBJECT_REPLACEMENT")))
          message.setSubject(mps.get("SUBJECT_REPLACEMENT"));
        else
          message.setSubject(I18n.getLabel("EVENT_" + s.getEventType(), app.getName(), language));


        if (MessagingSystem.Media.RSS.toString().equals(media)) {
          String link = s.getLink();
          link = link.substring(link.indexOf("\"") + 1);
          link = link.substring(0, link.indexOf("\""));
          message.setLink(link);
        } else
          message.setLink(s.getLink());

        if (MessagingSystem.Media.TWITTER.toString().equals(media)) {
          String link = HttpUtilities.getShortenedUrl(s.getLink());
          message.setLink(link);
          int length = 135 - (link.length()); // start from 135 instead of 140 just to be sure, it can contains br or something like that
          if (template.length() > length)
            template = JSP.limWr(template, length);
        }
        message.setMessageBody(template);
        message.setReceived(new Date());
        message.store();
      }
    }
  }
}
