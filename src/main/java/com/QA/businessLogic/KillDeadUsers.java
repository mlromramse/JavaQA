package com.QA.businessLogic;

import com.QA.QAOperator;
import org.jblooming.agenda.CompanyCalendar;
import org.jblooming.messaging.Listener;
import org.jblooming.oql.OqlQuery;
import org.jblooming.persistence.hibernate.PersistenceContext;
import org.jblooming.scheduler.ExecutableSupport;
import org.jblooming.scheduler.JobLogData;
import org.jblooming.tracer.Tracer;

import java.util.Date;
import java.util.List;

public class KillDeadUsers extends ExecutableSupport {

  public JobLogData run(JobLogData jobLogData) throws Exception {
    PersistenceContext pc = null;
    try {
      pc = new PersistenceContext();
      OqlQuery oqlQuery = new OqlQuery("SELECT o FROM " + QAOperator.class.getName() + " as o where o.lastLoggedOn - o.creationDate < 60 and o.lastLoggedOn<:date", pc);

      oqlQuery.getQuery().setMaxResults(50);  //  ----------------------------------  ONLI 50 AT THE VOLT

      StringBuffer report = new StringBuffer();
      oqlQuery.getQuery().setDate("date", new Date(System.currentTimeMillis() - CompanyCalendar.MILLIS_IN_MONTH));
      List<QAOperator> operators = oqlQuery.list();
      int i = 1;
      for (QAOperator op : operators) {

        try {
          report.append(i++ + ": " + " <b>" + op.getIntId() + "</b> " + op.getLoginName() + "\n");
          report.append(" used for " + ((op.getLastLoggedOn().getTime() - op.getCreationDate().getTime()) / 1000) + " seconds\n");

          //reset user invited
          oqlQuery = new OqlQuery("update " + QAOperator.class.getName() + " as op set op.owner=null where (op.owner=:o)", pc);
          oqlQuery.getQuery().setEntity("o", op);
          int q = oqlQuery.getQuery().executeUpdate();
          report.append("inv removed=" + q + "\n");

          pc.checkPoint();

          //ammazz the listeners
          oqlQuery = new OqlQuery("delete from " + Listener.class.getName() + " as l where (l.owner=:o)", pc);
          oqlQuery.getQuery().setEntity("o", op);
          q = oqlQuery.getQuery().executeUpdate();
          report.append("ass removed=" + q + "\n");

          pc.checkPoint();

          //ammazz the user
          op.remove(pc);

          pc.checkPoint();

          report.append("ok!\n");
        } catch (Throwable t) {
          report.append("ERROR DELETING " + op.getLoginName() + "\n");
          Tracer.platformLogger.error("ERROR DELETING " + op.getLoginName(),t );
        }
        report.append("\n-----------------------------------------------------------------------------------\n");

        Tracer.jobLogger.debug(report.toString());
      }

      pc.commitAndClose();

    } catch (Throwable e) {
      Tracer.platformLogger.error("KillDeadUsers import error", e);
      if (pc != null) {
        pc.rollbackAndClose();
      }
      jobLogData.successfull = false;
    }
    return jobLogData;
  }
}
