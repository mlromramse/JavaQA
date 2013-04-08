package org.jblooming;

import org.jblooming.utilities.DateUtilities;
import org.jblooming.waf.settings.ApplicationState;
import org.jblooming.tracer.Tracer;

import java.io.Writer;
import java.io.StringWriter;
import java.io.PrintWriter;
import java.util.Date;

public class ApplicationRuntimeException extends RuntimeException {

  public ApplicationRuntimeException(String message) {
    super(PlatformRuntimeException.getTime() + message);
    if (!ApplicationState.platformConfiguration.development)
      Tracer.platformLogger.fatal(message, this);
  }

  public ApplicationRuntimeException(String message, Throwable cause) {
    super(PlatformRuntimeException.getTime() + message, cause);
    if (!ApplicationState.platformConfiguration.development)
      Tracer.platformLogger.fatal(message, this);
  }

  public ApplicationRuntimeException(Throwable cause) {
    super(PlatformRuntimeException.getTime(), cause);
    if (!ApplicationState.platformConfiguration.development)
      Tracer.platformLogger.fatal(null, this);
  }

  public static String getStackTrace(Throwable aThrowable) {
    if (aThrowable!=null) {
    final Writer result = new StringWriter();
    final PrintWriter printWriter = new PrintWriter(result);
    aThrowable.printStackTrace(printWriter);
    return result.toString();
    } else {
      return "JBlooming: No throwable object available";
    }
  }

  protected static String getTime() {
    return "Server time: " + DateUtilities.dateToString(new Date(), "yyyy MM dd HH:mm:ss") + ". ";
  }
}
