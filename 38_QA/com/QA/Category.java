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
@Table(name = "qa_category")
public class Category extends LoggableIdentifiableSupport {

  private String name;


  @Id
  @Type(type = "int")
  @GeneratedValue(strategy = GenerationType.AUTO)
  @DocumentId
  @FieldBridge(impl = IntegerBridge.class)
  public Serializable getId() {
    return super.getId();
  }



  @Column(length = 900)
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }


  @Transient
  public static Category load(Serializable id) throws FindByPrimaryKeyException {
    return (Category) PersistenceHome.findByPrimaryKey(Category.class, id);
  }

  @Transient
  public static Category loadOrCreate(String category) throws org.jblooming.persistence.exceptions.PersistenceException {
    if (JSP.ex(category)) {

      Category t = null;
      OqlQuery oqlQuery = new OqlQuery("select category from " + Category.class.getName() + " as category where category.name=:name");
      oqlQuery.getQuery().setMaxResults(1);
      List<Category> ts = oqlQuery.getQuery().setString("name", category).list();
      if (ts.size()>0)
        t = ts.get(0);

      if (t == null) {
        t = new Category();
        t.setName(category);
        t.store();
      }

      return t;
    } else
      return null;
  }


}
