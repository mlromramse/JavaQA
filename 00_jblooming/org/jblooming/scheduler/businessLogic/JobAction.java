package org.jblooming.scheduler.businessLogic;

import org.jblooming.agenda.CompanyCalendar;
import org.jblooming.agenda.ScheduleSupport;
import org.jblooming.agenda.Period;
import org.jblooming.oql.QueryHelper;
import org.jblooming.page.HibernatePage;
import org.jblooming.persistence.PersistenceHome;
import org.jblooming.persistence.hibernate.PersistenceContext;
import org.jblooming.persistence.exceptions.FindByPrimaryKeyException;
import org.jblooming.persistence.exceptions.PersistenceException;
import org.jblooming.persistence.exceptions.StoreException;
import org.jblooming.persistence.exceptions.RemoveException;
import org.jblooming.scheduler.Job;
import org.jblooming.scheduler.Scheduler;
import org.jblooming.scheduler.Executable;
import org.jblooming.scheduler.Parameter;
import org.jblooming.utilities.DateUtilities;
import org.jblooming.utilities.ReflectionUtilities;
import org.jblooming.utilities.JSP;
import org.jblooming.waf.constants.AgendaConstants;
import org.jblooming.waf.constants.Fields;
import org.jblooming.waf.exceptions.ActionException;
import org.jblooming.waf.html.display.Paginator;
import org.jblooming.waf.html.input.ScheduleComposer;
import org.jblooming.waf.html.table.ListHeader;
import org.jblooming.waf.view.PageState;
import org.jblooming.waf.view.ClientEntry;
import org.jblooming.waf.ActionUtilities;
import org.jblooming.ontology.businessLogic.DeleteHelper;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.util.*;

public class JobAction {
  private SortedMap<Long, Long> zoominmap;
  private SortedMap<Long, Long> zoomoutmap;
  private SortedMap<Long, Integer> defaultDeltaColumnsRay;

  public JobAction() {

    zoomoutmap = new TreeMap<Long, Long>();
    zoomoutmap.put(CompanyCalendar.MILLIS_IN_MINUTE, (CompanyCalendar.MILLIS_IN_MINUTE * 5));
    zoomoutmap.put((CompanyCalendar.MILLIS_IN_MINUTE * 5), (CompanyCalendar.MILLIS_IN_MINUTE * 30));
    zoomoutmap.put((CompanyCalendar.MILLIS_IN_MINUTE * 30), CompanyCalendar.MILLIS_IN_HOUR);
    zoomoutmap.put(CompanyCalendar.MILLIS_IN_HOUR, CompanyCalendar.MILLIS_IN_DAY);
    zoomoutmap.put(CompanyCalendar.MILLIS_IN_DAY, CompanyCalendar.MILLIS_IN_MONTH);
    zoomoutmap.put(CompanyCalendar.MILLIS_IN_MONTH, CompanyCalendar.MILLIS_IN_MONTH);

    zoominmap = new TreeMap<Long, Long>();
    zoominmap.put(CompanyCalendar.MILLIS_IN_MINUTE, CompanyCalendar.MILLIS_IN_MINUTE);
    zoominmap.put((CompanyCalendar.MILLIS_IN_MINUTE * 5), CompanyCalendar.MILLIS_IN_MINUTE);
    zoominmap.put((CompanyCalendar.MILLIS_IN_MINUTE * 30), (CompanyCalendar.MILLIS_IN_MINUTE * 5));
    zoominmap.put(CompanyCalendar.MILLIS_IN_HOUR, (CompanyCalendar.MILLIS_IN_MINUTE * 30));
    zoominmap.put(CompanyCalendar.MILLIS_IN_DAY, CompanyCalendar.MILLIS_IN_HOUR);
    zoominmap.put(CompanyCalendar.MILLIS_IN_MONTH, CompanyCalendar.MILLIS_IN_DAY);

    defaultDeltaColumnsRay = new TreeMap<Long, Integer>();
    defaultDeltaColumnsRay.put(CompanyCalendar.MILLIS_IN_MINUTE, 7);
    defaultDeltaColumnsRay.put((CompanyCalendar.MILLIS_IN_MINUTE * 5), 6);
    defaultDeltaColumnsRay.put((CompanyCalendar.MILLIS_IN_MINUTE * 30), 10);
    defaultDeltaColumnsRay.put(CompanyCalendar.MILLIS_IN_HOUR, 12);
    defaultDeltaColumnsRay.put(CompanyCalendar.MILLIS_IN_DAY, 7);
    defaultDeltaColumnsRay.put(CompanyCalendar.MILLIS_IN_MONTH, 6);
  }


  public void cmdSave(PageState pageState) throws PersistenceException, ActionException,  RemoveException {

//______________________________________________________________________________________________________________________________________________________________________


// Job


//______________________________________________________________________________________________________________________________________________________________________

    Job job = null;
    if (PersistenceHome.NEW_EMPTY_ID.equals(pageState.getMainObjectId())) {
      job = new Job();
      job.setIdAsNew();
    } else {
      job = (Job) PersistenceHome.findByPrimaryKey(Job.class, pageState.getMainObjectId());
    }
    pageState.setMainObject(job);

    try {
      job.setName(pageState.getEntryAndSetRequired(AgendaConstants.FLD_NAME).stringValue());
    } catch (ActionException e) {
    }

    job.setDescription(pageState.getEntry(AgendaConstants.FLD_OBJECT).stringValue());

    try {
      job.setExecutable(pageState.getEntryAndSetRequired("LAUNCHER_CLASS").stringValue());
    } catch (ActionException e) {
    }
    try {
      job.setEstimatedDuration(pageState.getEntry(AgendaConstants.FLD_ESTIMATED_DURATION).intValue());
    } catch (ParseException e) {
    }

    ActionUtilities.setString(pageState.getEntry("DESCRIPTION"),job,"description");
    ActionUtilities.setLong(pageState.getEntry("timeoutTime"),job,"timeoutTime");
    ActionUtilities.setBoolean(pageState.getEntry("onErrorSuspendScheduling"),job,"onErrorSuspendScheduling");
    ActionUtilities.setBoolean(pageState.getEntry("onErrorRetryNow"),job,"onErrorRetryNow");
    ActionUtilities.setBoolean(pageState.getEntry("enabled"),job,"enabled");

//______________________________________________________________________________________________________________________________________________________________________


// ScheduleSupport


//______________________________________________________________________________________________________________________________________________________________________

    ScheduleSupport schedule = ScheduleComposer.getSchedule("ScheduleComposer", pageState);

//______________________________________________________________________________________________________________________________________________________________________


// Class


//______________________________________________________________________________________________________________________________________________________________________


    ClientEntry clazz = pageState.getEntry("LAUNCHER_CLASS");

    if (clazz.stringValueNullIfEmpty() != null) {

      try {
        Class theClass = Class.forName(clazz.stringValue());
        if (ReflectionUtilities.getInheritedClasses(theClass).contains(Executable.class)) {
          List<Field>  flds = ReflectionUtilities.getDeclaredInheritedParameterFields(theClass, Parameter.class);

            for (Field field : flds) {

              
              ClientEntry entry = pageState.getEntry(field.getName());
              if (entry.stringValue() != null) {
                String value = entry.stringValue();
                if (value != null && value.trim().length() > 0) {
                  job.getParameters().put(field.getName(), value);
                } else  {
                  job.getParameters().put(field.getName(), "");

                  //job.getParameters().remove(field.getName());
                }
              }
            }

        }
      } catch (ClassNotFoundException e) {
      }
    }

//______________________________________________________________________________________________________________________________________________________________________


// store


//______________________________________________________________________________________________________________________________________________________________________

    if (!pageState.validEntries())
      throw new ActionException();

    ScheduleSupport oldSchedule = job.getSchedule();
    if (oldSchedule !=null){
      job.setSchedule(null);
      oldSchedule.remove();
    }
    schedule.store();
    job.setSchedule(schedule);
    job.store();

//______________________________________________________________________________________________________________________________________________________________________


// addJob


//______________________________________________________________________________________________________________________________________________________________________


    /*Scheduler instance = Scheduler.getInstance();
    if (instance != null)
      instance.addJob(job);*/
    //17 Mar 2008: must commit otherwise reread in Scheduler goes MAD
    PersistenceContext.get(Job.class).checkPoint();
    Scheduler instance = Scheduler.getInstance();
    if (instance != null) {
      instance.stop();
      Scheduler.instantiate(instance.tick, pageState.getLoggedOperator().getDisplayName());
    }
  }

  public void cmdAdd(PageState pageState) {
    Job job = new Job();
    job.setIdAsNew();
    pageState.setMainObject(job);
    //make(pageState, job);
    Period p = new Period(new Date(),new Date(System.currentTimeMillis()+CompanyCalendar.MILLIS_IN_HOUR));
    ScheduleComposer.make("ScheduleComposer", p, pageState);
  }

  public void cmdEdit(PageState pageState) throws FindByPrimaryKeyException {
    Job job = (Job) PersistenceHome.findByPrimaryKey(Job.class, pageState.getMainObjectId());
    pageState.setMainObject(job);
    make(pageState, job);

    if (job.getSchedule() != null)
      ScheduleComposer.make("ScheduleComposer", job.getSchedule(), pageState);
  }

  private void make(PageState pageState, Job job) {
    pageState.addClientEntry(AgendaConstants.FLD_NAME, job.getName());
    pageState.addClientEntry(AgendaConstants.FLD_OBJECT, job.getDescription());
    pageState.addClientEntry("LAUNCHER_CLASS", job.getExecutable());

    pageState.addClientEntry(AgendaConstants.FLD_ESTIMATED_DURATION, job.getEstimatedDuration());
    pageState.addClientEntry("DESCRIPTION", JSP.w(job.getDescription()));
    pageState.addClientEntry("timeoutTime", job.getTimeoutTime() + "");
    pageState.addClientEntry("enabled", job.isEnabled()?Fields.TRUE:Fields.FALSE);
    pageState.addClientEntry("onErrorSuspendScheduling", job.isOnErrorSuspendScheduling()?Fields.TRUE:Fields.FALSE);
    pageState.addClientEntry("onErrorRetryNow", job.isOnErrorRetryNow()?Fields.TRUE:Fields.FALSE);

    if (job.getParameters() != null && job.getParameters().size() > 0) {
      for (String key : job.getParameters().keySet()) {
        String value = job.getParameters().get(key);
        pageState.addClientEntry(key, value);
      }
    }

  }

  public void cmdFind(PageState pageState) throws PersistenceException {
    String hql = "from " + Job.class.getName() + " as job";
    QueryHelper qhelp = new QueryHelper(hql);
    String filter = pageState.getEntry(Fields.FORM_PREFIX + "search").stringValueNullIfEmpty();
    if (filter != null && filter.trim().length() > 0) {
      qhelp.addQBEClause("job.name", "name", filter, QueryHelper.TYPE_CHAR);
    }
    ListHeader.orderAction(qhelp, "LH", pageState, "job.name");
    pageState.setPage(HibernatePage.getHibernatePageInstance(qhelp.toHql().getQuery(),
            Paginator.getWantedPageNumber(pageState),
            Paginator.getWantedPageSize("JOBPS", pageState)));
  }


  public void cmdDelete(PageState pageState) throws PersistenceException {
    Job delenda = (Job) PersistenceHome.findByPrimaryKey(Job.class, pageState.getMainObjectId());
    DeleteHelper.cmdDelete(delenda, pageState);
    Scheduler instance = Scheduler.getInstance();
    if (instance!=null)
      instance.removeJob(delenda);
  }

  public void cmdRunNow(PageState pageState) throws PersistenceException {
    cmdEdit(pageState);
    Job job = (Job)pageState.mainObject;

    Scheduler instance = Scheduler.getInstance();
    if (instance==null) {
      Scheduler.instantiate(5000, pageState.getLoggedOperator().getDisplayName());
      instance= Scheduler.getInstance();
      instance.run();
    }
    job.resetLastExecutionTime();
    job.store();
    instance.runAJob(job);
  }
}
