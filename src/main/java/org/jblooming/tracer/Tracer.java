package org.jblooming.tracer;

import org.apache.log4j.Logger;
import org.hibernate.stat.Statistics;
import org.jblooming.persistence.hibernate.HibernateFactory;
import org.jblooming.utilities.HashTable;
import org.jblooming.PlatformRuntimeException;
import org.jblooming.utilities.StringUtilities;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

public class Tracer {

  protected static Tracer singleton = new Tracer();

  private static final MemoryCounter mc = new MemoryCounter();

  public static TracerConstants DEFAULT_LOG_MODALITY = TracerConstants.HTTP;

  public static boolean oqlDebug = false;

  private Map<String, StringBuffer> trace = new HashTable();

  private StringBuffer oqlTrace = new StringBuffer();

  public static Logger platformLogger = Logger.getLogger("platformLogger");
  public static Logger i18nLogger = Logger.getLogger("i18nLogger");
  public static Logger hibernateLogger = Logger.getLogger("org.hibernate");
  public static Logger jobLogger = Logger.getLogger("jobLogger");
  public static Logger emailLogger = Logger.getLogger("emailLogger");


  public Tracer() {
    trace = new Hashtable();
    trace.put("127.0.0.1", new StringBuffer(512));
  }

  public static Tracer getInstance() {
    return singleton;
  }

  public static synchronized void addTrace(String s) {
    if (DEFAULT_LOG_MODALITY.equals(TracerConstants.HTTP))
      s = s + "<br>";
    Tracer.getInstance().trace.get("127.0.0.1").append(s);
  }

  public static synchronized void addTrace(String s, HttpServletRequest request) {
    addTrace(new StringBuffer(s), request);
  }

  public static synchronized void addTrace(StringBuffer s, HttpServletRequest request) {
    StringBuffer currentTrace = Tracer.getInstance().trace.get(request.getRemoteAddr());
    if (currentTrace == null)
      Tracer.getInstance().trace.put(request.getRemoteAddr(), new StringBuffer(512));
    Tracer.getInstance().trace.get(request.getRemoteAddr()).append(s);
  }

  public static StringBuffer getTrace(HttpServletRequest request) {
    return Tracer.getInstance().trace.get(request.getRemoteAddr());
  }

  public static synchronized void resetTrace(HttpServletRequest request) {
    Tracer.getInstance().trace.put(request.getRemoteAddr(), new StringBuffer(512));
    Tracer.getInstance().oqlTrace = new StringBuffer();
  }

  public static void printTrace(String s) {
    printTrace(TracerConstants.SYSTEM, s);
  }

  public static void printTrace(TracerConstants modality, String s) {
    if (modality.equals(TracerConstants.SYSTEM))
      System.out.print(s + '\n');
  }

  public static void printTrace(Throwable throwable) {
    printTrace(TracerConstants.SYSTEM, throwable);
  }

  public static void printTrace(TracerConstants modality, Throwable e) {
    if (modality.equals(TracerConstants.SYSTEM))
      e.printStackTrace();
  }

  /**
   * to allow object breakpoints on jsp pages
   *
   * @param o
   */
  public static void objectDebug(Object o) {
    if (o != null)
      o.toString();
    int breakPoint = 0;
  }

  public static void objectDebug(Object... os) {
   if (os != null)
      os.toString();
    int breakPoint = 0;
  }
  
  public String getOqlTrace(boolean reset) {
    String result = oqlTrace.toString();
    if (reset)
      oqlTrace = new StringBuffer();
    return result;
  }

  public void addOqlTrace(String oqlTrace) {
    this.oqlTrace.append(oqlTrace);
  }

  public static final String where() {
    StackTraceElement[] elems = new Exception().getStackTrace();
    return (elems.length < 2) ?
            "" :
            elems[1].getFileName() + "@" + elems[1].getLineNumber() + ":" + elems[1].getClassName() + "." + elems[1].getMethodName() + "()";
  }


  public static String traceRequest(HttpServletRequest request) {
    StringBuffer rd = new StringBuffer();

    //authentication
    if (request.getUserPrincipal() != null)
      rd.append("request.getUserPrincipal().getName() " + request.getUserPrincipal().getName() + "<br>");
    rd.append("request.getAuthType()" + request.getAuthType() + "<br>");
    rd.append("request.getRemoteUser() " + request.getRemoteUser() + "<br>");
    rd.append("request.isUserInRole(\"user\") " + request.isUserInRole("user") + "<br>");
    rd.append("request.isSecure() " + request.isSecure() + "<br><br>");

    rd.append("request.getRequestedSessionId() " + request.getRequestedSessionId() + "<br>");
    rd.append("request.getCharacterEncoding() " + request.getCharacterEncoding() + "<br>");
    rd.append("request.getMethod() " + request.getMethod() + "<br><br>");

    //params
    Enumeration parameters = request.getParameterNames();
    rd.append(System.currentTimeMillis() + "<br>");

    rd.append("header:<br><br>");
    Enumeration en = request.getHeaderNames();
    while (en.hasMoreElements()) {
      String s = (String) en.nextElement();
      rd.append(s + ": " + request.getHeader(s) + "<br>");
    }
    rd.append("<hr>");

    rd.append("attributes:<br><br>");
    Enumeration attr = request.getAttributeNames();
    while (attr.hasMoreElements()) {
      String requestString = (String) attr.nextElement();
      rd.append(requestString + " - " + request.getAttribute(requestString) + "<br>");
    }
    rd.append("<hr>");

    rd.append("parameters:<br><br>");
    while (parameters.hasMoreElements()) {
      String requestString = (String) parameters.nextElement();
      rd.append(requestString + " - " + request.getParameter(requestString) + "<br>");
    }
    rd.append("<hr>");

    rd.append("request.getContextPath():" + request.getContextPath() + "<br>");
    rd.append("request.getContentType():" + request.getContentType() + "<br>");
    rd.append("request.getRemoteAddr():" + request.getRemoteAddr() + "<br>");
    rd.append("request.getRemoteHost():" + request.getRemoteHost() + "<br>");
    rd.append("request.getRequestURI():" + request.getRequestURI() + "<br>");
    rd.append("request.getRequestURL():" + request.getRequestURL() + "<br>");

    return rd.toString();

  }

  public static String measureSize(Object o) {
    return measureSize(o,false);
  }


  public static String measureSize(Object o, boolean includeStatic) {
    long mem = mc.estimate(o,includeStatic);
    String s = o.getClass().getSimpleName() + " memory usage size: " + objectSize(mem);
    return s;
  }

  public static String objectSize(long size) {
    int divisor = 1;
    String unit = "bytes";
    if (size >= 1024 * 1024) {
      divisor = 1024 * 1024;
      unit = "MB";
    } else if (size >= 1024) {
      divisor = 1024;
      unit = "KB";
    }
    if (divisor == 1) return size / divisor + " " + unit;
    String aftercomma = "" + 100 * (size % divisor) / divisor;
    if (aftercomma.length() == 1) aftercomma = '0' + aftercomma;
    return size / divisor + "." + aftercomma + ' ' + unit;
  }


  public static String getCallTrace(boolean showFullStack) {

    StringBuffer sb = new StringBuffer();
    Throwable stea = new Throwable();

    StackTraceElement[] trace = stea.getStackTrace();
    //jump the first element
    for (int i = 1; i < trace.length; i++) {
      StackTraceElement element = trace[i];
      try {
        String className = element.getClassName();
        if (showFullStack || (className.indexOf("com.caucho") == -1 && className.indexOf("org.apache") == -1 ))
          sb.append("\n at " + element);
      } catch (Exception e) {
        throw new PlatformRuntimeException(e);
      }
    }

    /*final Writer result = new StringWriter();
    final PrintWriter printWriter = new PrintWriter(result);
    stea.printStackTrace(printWriter);
    return result.toString();*/
    return sb.toString();
  }

  public static void logExceptionOnPlatformOrOther(Throwable throwable) {
    if (platformLogger!=null && platformLogger.getAllAppenders().hasMoreElements()) {
      platformLogger.error(throwable.getMessage(),throwable);
    } else
      desperatelyLog(throwable.getMessage(),false,throwable);
  }

  public static void desperatelyLog(String message, boolean throwPlatformRuntimeException, Throwable e) {

    if (platformLogger != null && platformLogger.getAllAppenders().hasMoreElements()) {
      platformLogger.fatal(e.getMessage(),e);
      return;
    }

    try {
      if (Logger.getRootLogger() != null)
      Logger.getRootLogger().fatal(message);
    } catch (Exception ex) {
    }

    System.out.println(message);

    if (throwPlatformRuntimeException) {
      if (e!=null)
        throw new PlatformRuntimeException(message,e);
      else
        throw new PlatformRuntimeException(message);
    }
  }

  public static void traceHibernateStart() {
    HibernateFactory.getSessionFactory().getStatistics().clear();
  }

  public static String traceHibernateEnd() {
    String qq = "";
    Statistics statistics = HibernateFactory.getSessionFactory().getStatistics();

    for (String q : statistics.getQueries()) {
      qq = qq + q+"\n";
    }
    qq = qq + "\n"+"Total HQL queries: "+ statistics.getQueries().length+"\n";

    qq = qq + "----------------------------\n"+"Total queries executed to database: "+ statistics.getQueryExecutionCount()+"\n";

    qq = qq + "----------------------------\n"+"Slowest query: "+ statistics.getQueryExecutionMaxTimeQueryString()+" TOOK "+statistics.getQueryExecutionMaxTime()+"\n";

    qq = qq + "----------------------------\n"+ StringUtilities.replaceAllNoRegex(statistics+"",",","\n");

    return qq;
  }


}

