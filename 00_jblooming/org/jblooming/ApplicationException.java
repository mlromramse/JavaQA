package org.jblooming;

import org.jblooming.utilities.DateUtilities;

import java.util.Date;

/**
 * Date: 16-dic-2002
 * Time: 16.42.44
 *
 * @author Pietro Polsinelli dev@open-lab.com
 */
public class ApplicationException extends Exception {

  public ApplicationException() {
    super("Server time: " + DateUtilities.dateToString(new Date()) + ". ");
  }

  public ApplicationException(String s) {
    super("Server time: " + DateUtilities.dateToString(new Date()) + ". " + s);
  }

  public ApplicationException(Exception e) {
    super("Server time: " + DateUtilities.dateToString(new Date()) + ". ", e);
  }

  public ApplicationException(String s, Exception e) {
    super("Server time: " + DateUtilities.dateToString(new Date()) + ". " + s, e);
  }
}
