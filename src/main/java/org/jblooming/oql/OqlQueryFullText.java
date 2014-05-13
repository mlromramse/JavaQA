package org.jblooming.oql;

import org.hibernate.Query;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.jblooming.persistence.hibernate.HibernateFactory;
import org.jblooming.tracer.Tracer;

/**
 * (c) Open Lab - www.open-lab.com
 * Date: Feb 5, 2007
 * Time: 10:42:27 AM
 */
public class OqlQueryFullText extends OqlQuery {

  FullTextSession fullTextSession;

  public OqlQueryFullText(String oql) {

    FullTextSession fullTextSession = Search.createFullTextSession(HibernateFactory.getSession());
    this.fullTextSession = fullTextSession;
    
    Query phQuery = fullTextSession.createQuery(oql);
    this.setQuery(phQuery);
    if (Tracer.oqlDebug)
      Tracer.getInstance().addOqlTrace("<br>hql: " + oql + "<br>");
  }
}
