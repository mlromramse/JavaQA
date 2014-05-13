package com.QA.rank;

import org.jblooming.PlatformRuntimeException;
import org.jblooming.ontology.Identifiable;
import org.jblooming.persistence.PersistenceHome;
import org.jblooming.utilities.StringUtilities;

import java.util.List;

/**
 * (c) Open Lab - www.open-lab.com
 * Date: Jan 24, 2008
 * Time: 5:04:37 PM
 */
public class EntityGroupRank implements Comparable<EntityGroupRank> {

  private Identifiable entity=null;

  public String classAndId;
  //computed
  public long lastSeen;
  public double weight;

  public int compareTo(EntityGroupRank o) {
    return new Double(o.weight).compareTo(new Double(weight));
  }


  public Identifiable getEntity() {
    if (entity==null){
      try {
        List<String> bl = StringUtilities.splitToList(classAndId, "^");
        Identifiable is = PersistenceHome.findByPrimaryKey((Class<? extends Identifiable>) Class.forName(bl.get(0)), bl.get(1));
        if (is != null) {
          entity = is;
        }
      } catch (ClassNotFoundException e) {
        throw new PlatformRuntimeException(e);
      } catch (Throwable e) {
      }
    }
    return entity;
  }

}

