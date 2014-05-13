package com.QA;

import org.hibernate.annotations.Type;
import org.hibernate.search.annotations.DocumentId;
import org.hibernate.search.annotations.FieldBridge;
import org.hibernate.search.bridge.builtin.IntegerBridge;
import org.jblooming.ontology.LoggableIdentifiableSupport;
import org.jblooming.oql.OqlQuery;
import org.jblooming.persistence.PersistenceHome;
import org.jblooming.persistence.exceptions.FindByPrimaryKeyException;
import org.jblooming.utilities.JSP;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "qa_tag")
public class Tag extends LoggableIdentifiableSupport {

  private String description;
  private String name;


  @Id
  @Type(type = "int")
  @GeneratedValue(strategy = GenerationType.AUTO)
  @DocumentId
  @FieldBridge(impl = IntegerBridge.class)
  public Serializable getId() {
    return super.getId();
  }

  @Lob
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  @Column(length = 900)
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Transient
  public static Tag load(Serializable id) throws FindByPrimaryKeyException {
    return (Tag) PersistenceHome.findByPrimaryKey(Tag.class, id);
  }

  @Transient
  public static Tag loadByName(String tag) throws FindByPrimaryKeyException {
    if (JSP.ex(tag)) {

      Tag t = null;
      OqlQuery oqlQuery = new OqlQuery("select tag from " + Tag.class.getName() + " as tag where tag.name=:tag");
      oqlQuery.getQuery().setMaxResults(1);
      List<Tag> ts = oqlQuery.getQuery().setString("tag", tag).list();
      if (ts.size() > 0)
        t = ts.get(0);
      return t;
    } else
      return null;
  }

  @Transient
  public static Tag loadOrCreate(String tag) throws org.jblooming.persistence.exceptions.PersistenceException {
    if (JSP.ex(tag)) {

      Tag t = null;
      OqlQuery oqlQuery = new OqlQuery("select tag from " + Tag.class.getName() + " as tag where tag.name=:tag");
      oqlQuery.getQuery().setMaxResults(1);
      List<Tag> ts = oqlQuery.getQuery().setString("tag", tag).list();
      if (ts.size() > 0)
        t = ts.get(0);

      if (t == null) {
        t = new Tag();
        t.setName(tag);
        t.store();
      }

      return t;
    } else
      return null;
  }

  @Transient
  public List<Question> getQuestions(int maxResults) {

    String hql = "select q from " + Question.class.getName() + " as q join q.tags as tag where q.deleted=false and tag=:tag" +
            " order by q.totUpvotesFromQandA desc";
    org.hibernate.Query query = new OqlQuery(hql).getQuery();
    query.setEntity("tag", this);
    query.setMaxResults(maxResults);
    return query.list();
  }

  @Transient
  public int getQuestionsCount() {

    String hql = "select count(q) from " + Question.class.getName() + " as q join q.tags as tag where q.deleted=false and tag=:tag";
    org.hibernate.Query query = new OqlQuery(hql).getQuery();
    query.setEntity("tag", this);
    return ((Long)query.uniqueResult()).intValue();
  }


  private static List<String> mostUsedTags;

  @Transient
  public static List<String> getMostUsedTags(boolean recomputeTheWholeList, double cutOff) {

    if (recomputeTheWholeList) {

      String hql = "select distinct tag.name from " + Question.class.getName() + " as q join q.tags as tag where q.deleted=false group by tag.name " +
              " having count(tag.name)>" + cutOff + " order by count(tag.name) desc";

      org.hibernate.Query query = new OqlQuery(hql).getQuery();

      //query.setInteger("lowBound",4); // PROPOSE ONLY TAGS USED AT LEAST by 10% of community
      mostUsedTags = query.list();
    }
    return mostUsedTags;
  }

  @Transient
  public static List<Object[]> getMostUsedTagEntities(double cutOff, int maxResults) {

    String hql = "select count(tag.name), tag from " + Question.class.getName() + " as q join q.tags as tag where q.deleted=false group by tag.name " +
            " having count(tag.name)>" + cutOff + " order by count(tag.name) desc";

    org.hibernate.Query query = new OqlQuery(hql).getQuery();
    query.setMaxResults(maxResults);
    return query.list();
  }

}
