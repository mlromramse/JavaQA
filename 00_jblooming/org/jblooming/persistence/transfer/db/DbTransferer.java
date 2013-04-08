package org.jblooming.persistence.transfer.db;

import org.jblooming.persistence.exceptions.PersistenceException;

public interface DbTransferer {
  public void receive() throws PersistenceException;

  public void send() throws PersistenceException;
}
