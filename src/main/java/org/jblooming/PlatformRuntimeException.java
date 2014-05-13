package org.jblooming;

import org.jblooming.persistence.ThreadLocalPersistenceContextCarrier;
import org.jblooming.persistence.hibernate.PersistenceContext;
import org.jblooming.operator.Operator;

/**
 * @author Pietro Polsinelli : ppolsinelli@open-lab.com
 */
public class PlatformRuntimeException extends ApplicationRuntimeException {

  public PlatformRuntimeException() {

    this("");
  }

  public PlatformRuntimeException(Throwable cause) {
    this(cause.getMessage(),cause);
  }

  public PlatformRuntimeException(String message) {
    super(getExtendedMessage(message));
  }

  public PlatformRuntimeException(String message, Throwable cause) {
    super(getExtendedMessage(message)+"\nThe exception thrown:\n", cause);
  }

  private static String getExtendedMessage(String message) {

    //believe it or not, we put it here! HAHAHAHAHA (with a Satanic ring)!
    try {
      ThreadLocalPersistenceContextCarrier threadLocalPersistenceContextCarrier = PersistenceContext.threadLocalPersistenceContextCarrier.get();
      if (threadLocalPersistenceContextCarrier !=null && threadLocalPersistenceContextCarrier.getOperator() !=null) {
        Operator operator = threadLocalPersistenceContextCarrier.getOperator();
        message = message + " logged operator: ("+operator.getId()+") "+ operator.getDisplayName()+"\n";
      }
    } catch (Exception e) {
    }

    message = message + PlatformRuntimeException.getTime();
    return message;
  }

}
