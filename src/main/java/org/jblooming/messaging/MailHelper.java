package org.jblooming.messaging;

import org.jblooming.ApplicationException;
import org.jblooming.PlatformRuntimeException;
import org.jblooming.uidgen.CounterHome;
import org.jblooming.ontology.PersistentFile;
import org.jblooming.operator.Operator;
import org.jblooming.persistence.exceptions.StoreException;
import org.jblooming.scheduler.PlatformExecutionService;
import org.jblooming.system.SystemConstants;
import org.jblooming.tracer.Tracer;
import org.jblooming.utilities.*;
import org.jblooming.utilities.file.FileUtilities;
import org.jblooming.waf.constants.Fields;
import org.jblooming.waf.exceptions.ActionException;
import org.jblooming.waf.settings.ApplicationState;
import org.jblooming.waf.view.PageState;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.*;
import javax.mail.internet.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.Future;

//import oracle.sql.DATE;

/**
 * @author Pietro Polsinelli : ppolsinelli@open-lab.com
 * @author Ezio Manetti : emanetti@open-lab.com
 */
public class MailHelper {

  static MailDateFormat mailDateFormat = new MailDateFormat();

  // See Section 3.6.1 of RFC 2822

  /**
   * The name of the RFC 2822 header that stores the mail date.
   */
  protected static final String RFC_2822_DATE = "Date";

  /*static {
    MailcapCommandMap mailcapCommandMap = (MailcapCommandMap) MailcapCommandMap.getDefaultCommandMap();
    mailcapCommandMap.addMailcap("application/zip; ; x-java-content-handler=" + ApplicationZipHandler.class.getName());
    MailcapCommandMap.setDefaultCommandMap(mailcapCommandMap);
  }*/

  /**
   * The following application settings keys are required: SystemConstants.FLD_MAIL_SMTP
   *
   * @param receiver
   * @param psw
   * @param pageState
   * @throws ActionException
   * @throws ApplicationException
   */
  public static void sendPwdMail(Operator receiver, String psw, PageState pageState) throws ApplicationException {

    if (receiver != null) {

      String date = DateUtilities.dateToRelative(new Date());

      String message = "As you requested, this is your new password: " + psw;

      message += "\n\n[sent with platform messaging system - " + date + ']';

      sendPlainTextMail(receiver.getAnagraphicalData().getEmail(), receiver.getAnagraphicalData().getEmail(), "message - " + date, message);
    }
  }

  public static void sendPlainTextMail(String from, String to, String subject, String body) throws ApplicationException {
    javax.mail.Session mailSession = getEmailSession();
    if (mailSession != null)
      sendPlainTextMail(mailSession, from, to, subject, body);
  }


  public static void sendPlainTextMail(javax.mail.Session mailSession, String from, String to, String subject, String body) throws ApplicationException {
    Set<String> tos = mailTos(to);
    sendPlainTextMail(mailSession, from, tos, subject, body);
  }


  public static void sendPlainTextMail(String from, Set<String> to, String subject, String body) throws ApplicationException {
    javax.mail.Session mailSession = getEmailSession();
    if (mailSession != null)
      sendPlainTextMail(mailSession, from, to, subject, body);
  }

  public static void sendPlainTextMail(javax.mail.Session mailSession, String from, Set<String> to, String subject, String body) throws ApplicationException {
    sendPlainTextMail(mailSession, from, null, to, subject, body);
  }

  public static void sendPlainTextMail(javax.mail.Session mailSession, String from, String fromName, Set<String> to, String subject, String body) throws ApplicationException {
    try {
      MimeMessage message = new MimeMessage(mailSession);

      List<String> froms = mailTosAsList(from);

      InternetAddress internetAddress = new InternetAddress(froms.get(0));
      if (JSP.ex(fromName))
        internetAddress.setPersonal(fromName, "UTF-8");

      message.setFrom(internetAddress);
      for (String rec : to) {
        message.addRecipient(MimeMessage.RecipientType.TO, new InternetAddress(rec));
      }
      //pietro added UTF-8 support Sept29 2008
      message.setSubject(subject, "UTF-8");
      message.setText(body, "UTF-8");

      String date = mailHeaderDate();
      if (message.getHeader(RFC_2822_DATE) == null) {
        message.addHeader(RFC_2822_DATE, date);
      }
      //this seems to be buggy
      //message.setSentDate(new Date());

      send(message, mailSession);
    } catch (MessagingException e) {
      throw new ApplicationException(e);
    } catch (UnsupportedEncodingException e) {
      throw new ApplicationException(e);
    }

  }


  public static void sendHtmlMail(String from, String to, String subject, String body) throws ApplicationException {
    //javax.mail.Session mailSession = getMailSession(null);
    if (JSP.ex(from, to)) {
      Set<String> tos = mailTos(to);
      sendHtmlMail(from, tos, subject, body);
    }
//    if (mailSession!=null)
//      sendHtmlMail(mailSession, from, tos, subject, body);
  }

  public static void sendHtmlMail(String from, Set<String> to, String subject, String body) throws ApplicationException {
    javax.mail.Session mailSession = getEmailSession();
    if (mailSession != null)
      sendHtmlMail(mailSession, from, to, subject, body);
    else
      Tracer.platformLogger.warn("MailHelper.sendHtmlMail cannot create session");
  }

  public static Future sendHtmlMailInSeparateThread(final String from, final String to, final String subject, final String body) {
      return sendHtmlMailInSeparateThread(from, CollectionUtilities.toSet(to), subject, body);
  }
  
  public static Future sendHtmlMailInSeparateThread(final String from, final Set<String> to, final String subject, final String body) {
    return PlatformExecutionService.executorService.submit(
            new Thread() {
              public void run() {
                try {
                  sendHtmlMail(from, to, subject, body);
                } catch (ApplicationException e) {
                  Tracer.platformLogger.error("MailHelper.sendHtmlMailInSeparateThread", e);
                }
              }
            });
  }

  public static Future sendPlainTextMailInSeparateThread(final String from, final Set<String> to, final String subject, final String body) {
    return PlatformExecutionService.executorService.submit(
            new Thread() {
              public void run() {
                try {
                  sendPlainTextMail(from, to, subject, body);
                } catch (ApplicationException e) {
                  Tracer.platformLogger.error("MailHelper.sendPlainTextMailInSeparateThread", e);
                }
              }
            });
  }

  public static void sendHtmlMail(javax.mail.Session mailSession, String from, Set<String> to, String subject, String body) throws ApplicationException {
    try {

      MimeMessage message = new MimeMessage(mailSession);

      List<String> froms = mailTosAsList(from);

      message.setFrom(new InternetAddress(froms.get(0)));
      for (String rec : to) {
        message.addRecipient(MimeMessage.RecipientType.TO, new InternetAddress(rec));
      }
      message.setSubject(subject, "UTF-8");
      message.setContent(body, "text/html; charset=\"UTF-8\"");
      String date = mailHeaderDate();
      if (message.getHeader(RFC_2822_DATE) == null) {
        message.addHeader(RFC_2822_DATE, date);
      }
      message.addHeader("X-Mailer", "JBlooming Platform");

      //this seems to be buggy
      //message.setSentDate(new Date());
      send(message, mailSession);

    } catch (MessagingException e) {
      throw new ApplicationException(e);
    }

  }

  public static String mailHeaderDate() {
    return mailDateFormat.format(new Date());
  }

  public static void sendAuthenticatedMail(String from, String to, String subject, String body) throws ApplicationException {
    javax.mail.Session mailSession = getEmailSession();
    sendPlainTextMail(mailSession, from, to, subject, body);
  }

  /**
   * @deprecated does it already   getEmailSession
   */
  public static Session getAuthenticatedSession() {
    return getEmailSession();
  }

  public static Session getEmailSession() {

    Properties mailProps = new Properties();

    boolean useAuth = Fields.TRUE.equalsIgnoreCase(ApplicationState.getApplicationSetting(SystemConstants.FLD_MAIL_USE_AUTHENTICATED));
    String mailHost = ApplicationState.getApplicationSetting(SystemConstants.FLD_MAIL_SMTP);
    String protocol = "smtp";
    String protocolParam = ApplicationState.getApplicationSetting(SystemConstants.FLD_MAIL_PROTOCOL);
    if (JSP.ex(protocolParam))
      protocol = protocolParam;

    if (JSP.ex(mailHost)) {
      mailProps.put("mail." + protocol + ".host", mailHost);

      int port = 25;
      String portS = ApplicationState.getApplicationSetting(SystemConstants.FLD_MAIL_SMTP_PORT);
      try {
        port = Integer.parseInt(portS);
      } catch (NumberFormatException e) {
      }

      //Pietro: incredible, unbelievable bug!!!!
      //http://forums.sun.com/thread.jspa?threadID=778568
      //
      // this was: mailProps.put("mail." + protocol + ".port", port);
      //
      //  "The problem with my code was that smtpPort was int, and autoboxing made it Integer and added to the props without any compile time problems.
      //   But props.getProperty("mail.smtp.port") returns null unless the value is String. That caused JavaMail to use the default port."
      //  this was mailProps.put("mail." + protocol + ".port", port); which for mailProps.getProperty("mail." + protocol + ".port") return NULL!!!!!!!!!!
      mailProps.setProperty("mail." + protocol + ".port", port + "");

      String heloHost = ApplicationState.getApplicationSetting(SystemConstants.FLD_MAIL_HELO_HOST);
      if (!JSP.ex(heloHost))
        heloHost = mailHost;
      mailProps.setProperty("mail." + protocol + ".localhost", heloHost);
      if (useAuth) {
        mailProps.setProperty("mail." + protocol + ".auth", "true");
        //if it solves, introduce the config TLS filed on web interface
        //mailProps.setProperty("mail." + protocol + ".starttls.enable", "true");
        //mailProps.setProperty("mail." + protocol + ".socketFactory.class", "javax.net.ssl.SSLSocketFactory");
      }
    } else
      Tracer.platformLogger.warn("Tried to send mail without mail host configured in settings:" + SystemConstants.FLD_MAIL_SMTP);

    String useAuthenticatedMail = ApplicationState.getApplicationSetting(SystemConstants.FLD_MAIL_USE_AUTHENTICATED);
    MailAutenticator autenticator = null;
    if (Fields.TRUE.equalsIgnoreCase(useAuthenticatedMail)) {
      String smtpUser = ApplicationState.getApplicationSetting(SystemConstants.FLD_MAIL_USER);
      String smtpPwd = ApplicationState.getApplicationSetting(SystemConstants.FLD_MAIL_PWD);
      autenticator = new MailAutenticator(smtpUser, smtpPwd);
    }

    Session mailSession = Session.getInstance(mailProps, autenticator);

    return mailSession;
  }


  public static void attach(MimeMultipart multipart, String text, String charset, String disposition, String nameToBeGivenToAttach) throws MessagingException {

    int xid = multipart.getCount() + 1;
    //String xname = "TEXT" + xid + ".TXT";

    MimeBodyPart xbody = new MimeBodyPart();
    xbody.setText(text, charset);

    // UNDONE
    //xbody.setContentLanguage( String ); // this could be language from Locale
    //xbody.setContentMD5( String md5 ); // don't know about this yet
    xbody.setDescription("Text Attachment: " + nameToBeGivenToAttach, charset);
    xbody.setDisposition(disposition);
    setFileName(xbody, nameToBeGivenToAttach, charset);

    multipart.addBodyPart(xbody);
  }

  public static void attachFile(MimeMultipart multipart, String filePathAndName, String charset) throws MessagingException {

    int xid = multipart.getCount() + 1;
    //String xname = "TEXT" + xid + ".TXT";

    MimeBodyPart xbody = new MimeBodyPart();
    xbody.setFileName(filePathAndName);

    String nameToBeGivenToAttach = new File(filePathAndName).getName();
    // UNDONE
    //xbody.setContentLanguage( String ); // this could be language from Locale
    //xbody.setContentMD5( String md5 ); // don't know about this yet
    //xbody.setDescription("Text Attachment: " + xname, charset);
    xbody.setDisposition(Part.ATTACHMENT);
    setFileName(xbody, nameToBeGivenToAttach, charset);

    multipart.addBodyPart(xbody);


  }

  // ADD
  public static void sendMailWithAttachFile(MimeMultipart multipart, String from, String to, String subject, String body, String filePathAndName, String charset) throws MessagingException {

    Session mailSession = getEmailSession();

    // Define message
    MimeMessage message = new MimeMessage(mailSession);

    message.setFrom(new InternetAddress(from));
//    message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

    List<String> tos = mailTosAsList(to);
    for (String rec : tos) {
      message.addRecipient(MimeMessage.RecipientType.TO, new InternetAddress(rec));
    }

    message.setSubject(subject);
    // create the message part
    MimeBodyPart messageBodyPart = new MimeBodyPart();

    //fill message
    messageBodyPart.setText(body, charset);
    // Multipart multipart = new MimeMultipart();
    multipart.addBodyPart(messageBodyPart);

    messageBodyPart = new MimeBodyPart();
    DataSource source = new FileDataSource(filePathAndName);
    messageBodyPart.setDataHandler(new DataHandler(source));
    //messageBodyPart.setFileName(new File(filePathAndName).getName());
    // in order to send attachment file with utf8 characters (èòà)
    try {
      String encodedName = MimeUtility.encodeText(new File(filePathAndName).getName(), "UTF8", "B");
      messageBodyPart.setFileName(encodedName);
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    }

    multipart.addBodyPart(messageBodyPart);

    // Put parts in message
    message.setContent(multipart);
    send(message, mailSession);
  }


  // ADD

  public static void sendMailWithAttachFile(String from, String to, String subject, String body, String filePathAndName, String charset) throws MessagingException {
    MimeMultipart multipart = new MimeMultipart();
    sendMailWithAttachFile(multipart, from, to, subject, body, filePathAndName, charset);
  }

  // --- ADD
  public static void sendMailWithAttachFile(MimeMultipart multipart, String from, String to, String subject, String body, String fileName, byte[] byteContent, String charset, String contentType) throws MessagingException {

    Session mailSession = getEmailSession();

    // Define message
    MimeMessage message = new MimeMessage(mailSession);

    message.setFrom(new InternetAddress(from));

    List<String> tos = mailTosAsList(to);
    for (String rec : tos) {
      message.addRecipient(MimeMessage.RecipientType.TO, new InternetAddress(rec));
    }

    message.setSubject(subject);
    // create the message part
    MimeBodyPart messageBodyPart = new MimeBodyPart();

    //fill message
    messageBodyPart.setText(body, charset);
    // Multipart multipart = new MimeMultipart();
    multipart.addBodyPart(messageBodyPart);

    messageBodyPart = new MimeBodyPart();
    DataSource source = new MyDataSource(byteContent, contentType);
    //  new FileDataSource(filePathAndName);
    messageBodyPart.setDataHandler(new DataHandler(source));
    //messageBodyPart.setFileName(fileName);
    // in order to send attachment file with utf8 characters (èòà)
    try {
      String encodedName = MimeUtility.encodeText(new File(fileName).getName(), "UTF8", "B");
      messageBodyPart.setFileName(encodedName);
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    }

    multipart.addBodyPart(messageBodyPart);

    // Put parts in message
    message.setContent(multipart);
    send(message, mailSession);
  }

  /**
   * A better version of setFileName() which will encode the name if necessary.
   * Why doesn't JAVA Mail do this?
   *
   * @param part    the part to manipulate
   * @param name    the give file name encoded in UTF (JAVA)
   * @param charset the encoding character set
   */
  public static void setFileName(Part part, String name, String charset) throws MessagingException {

    try {
      name = MimeUtility.encodeWord(name, charset, null);
    } catch (Exception xex) {
    }
    part.setFileName(name);
  }

  public static Future sendMultipartTextMail(final String from, final Set<String> tos, final String subject, final String fileName, final String textAttached) {

    return PlatformExecutionService.executorService.submit(
            new Thread() {
              public void run() {
                try {

                  // Get session
                  Session session = getAuthenticatedSession();

                  // Define message
                  MimeMessage message = new MimeMessage(session);

                  List<String> froms = mailTosAsList(from);
                  message.setFrom(new InternetAddress(froms.get(0)));

                  for (String rec : tos) {
                    message.addRecipient(MimeMessage.RecipientType.TO, new InternetAddress(rec));
                  }
                  message.setSubject(subject);

                  MimeMultipart multipart = new MimeMultipart();

                  attach(multipart, textAttached, "UTF-8", Part.ATTACHMENT, fileName);

                  // Put parts in message
                  message.setContent(multipart);

                  // Send message
                  send(message, session);

                } catch (Exception e) {
                  Tracer.platformLogger.error(e);
                }
              }


            });
  }

  private static void send(Message message, Session session) throws MessagingException {

    String protocol = "smtp";
    String protocolParam = ApplicationState.getApplicationSetting(SystemConstants.FLD_MAIL_PROTOCOL);
    if (JSP.ex(protocolParam))
      protocol = protocolParam;

    Transport t = session.getTransport(protocol);

    boolean useAuth = Fields.TRUE.equalsIgnoreCase(ApplicationState.getApplicationSetting(SystemConstants.FLD_MAIL_USE_AUTHENTICATED));
    String mailHost = ApplicationState.getApplicationSetting(SystemConstants.FLD_MAIL_SMTP);

    if (useAuth) {
      String smtpUser = ApplicationState.getApplicationSetting(SystemConstants.FLD_MAIL_USER);
      String smtpPwd = ApplicationState.getApplicationSetting(SystemConstants.FLD_MAIL_PWD);
      t.connect(mailHost, smtpUser, smtpPwd);
      t.sendMessage(message, message.getAllRecipients());
    } else
      t.send(message);

    t.close();
  }


  public static Future sendPlainTextMailWithAttach(final String from, final String to, final Set<String> toBCC, final String subject, final String body, final String filePathAndName) {

    return PlatformExecutionService.executorService.submit(
            new Thread() {
              public void run() {
                try {
                  // Get session
                  Session session = MailHelper.getAuthenticatedSession();

                  // create a message
                  MimeMessage msg = new MimeMessage(session);

                  List<String> froms = mailTosAsList(from);
                  msg.setFrom(new InternetAddress(froms.get(0)));

                  Set<String> tos = mailTos(to);
                  for (String t : tos) {
                    msg.addRecipient(MimeMessage.RecipientType.TO, new InternetAddress(t));
                  }

                  for (String rec : toBCC) {
                    msg.addRecipient(MimeMessage.RecipientType.BCC, new InternetAddress(rec));
                  }

                  msg.setSubject(subject);
                  msg.setSentDate(new Date());

                  // create and fill the first message part
                  MimeBodyPart mbp1 = new MimeBodyPart();
                  mbp1.setText(body);


                  File file = new File(filePathAndName);
                  DataSource ds = new FileDataSource(file) {
                    public String getContentType() {
                      return HttpUtilities.getContentType(filePathAndName);
                    }
                  };
                  MimeBodyPart mbp2 = new MimeBodyPart();
                  mbp2.setDataHandler(new DataHandler(ds));
                  // mbp2.setFileName(file.getName());
                  // in order to send attachment file with utf8 characters (èòà)
                  try {
                    String encodedName = MimeUtility.encodeText(file.getName(), "UTF8", "B");
                    mbp2.setFileName(encodedName);
                  } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                  }

                  mbp2.setDisposition(Part.ATTACHMENT);

                  // create the Multipart and its parts to it
                  Multipart mp = new MimeMultipart();
                  mp.addBodyPart(mbp1);
                  mp.addBodyPart(mbp2);

                  // add the Multipart to the message
                  msg.setContent(mp);

                  // Send message
                  send(msg, session);

                } catch (Exception e) {
                  //Tracer.platformLogger.error(e);
                  throw new PlatformRuntimeException(e);
                }
              }
            });
  }


  public static Future sendMailWithHeader(final InternetAddress from, final Set<InternetAddress> tos, final String subject, final String header, final String body) { //final Map<String,String> nameAndMail

    return PlatformExecutionService.executorService.submit(
            new Thread() {
              public void run() {
                try {

                  Session session = getAuthenticatedSession();
                  MimeMessage message = new MimeMessage(session);

                  //StringDataSource source = new StringDataSource(body, header, "aDataHandler");
                  //message.setDataHandler(new DataHandler(source));

                  // first set the body as text/plain
                  message.setText(body, "UTF-8");
                  // then force the content-type
                  message.addHeader("Content-Type", header);

                  message.setFrom(from);

                  for (InternetAddress to : tos) {
                    message.addRecipient(MimeMessage.RecipientType.TO, to);
                  }

                  message.setSubject(subject, "UTF-8");

                  String date = mailHeaderDate();
                  if (message.getHeader(RFC_2822_DATE) == null) {
                    message.addHeader(RFC_2822_DATE, date);
                  }
                  send(message, session);

                } catch (Exception e) {
                  //Tracer.platformLogger.error(e);
                  throw new PlatformRuntimeException(e);
                }
              }
            });
  }

  public static Store getPop3AndConnect() throws MessagingException {
    String port = ApplicationState.getApplicationSetting(SystemConstants.FLD_POP3_PORT);
    int FLD_POP3_PORT = JSP.ex(port) ? Integer.parseInt(port) : -1;

    String protocol = "pop3";
    String protocolParam = ApplicationState.getApplicationSetting(SystemConstants.FLD_EMAIL_DOWNLOAD_PROTOCOL);
    if (JSP.ex(protocolParam))
      protocol = protocolParam;

    return getPop3AndConnect(ApplicationState.getApplicationSetting(SystemConstants.FLD_POP3_HOST),
            ApplicationState.getApplicationSetting(SystemConstants.FLD_POP3_USER),
            ApplicationState.getApplicationSetting(SystemConstants.FLD_POP3_PSW),
            FLD_POP3_PORT,
            protocol
    );
  }

  public static Store getPop3AndConnect(String host, String user, String password, int port, String protocol) throws MessagingException {

    Session session = getAuthenticatedSession();

    Store store = session.getStore(protocol);

    if (JSP.ex(host, user)) {
      store.connect(host, port, user, password);
    }
    return store;
  }

  /*// The read() methods use the relative ByteBuffer get() methods. 
  public static InputStream newInputStream(final ByteBuffer buf) {
    return new InputStream() {
      public synchronized int read() throws IOException {
        if (!buf.hasRemaining()) {
          return -1;
        }
        return buf.get();
      }

      public synchronized int read(byte[] bytes, int off, int len) throws IOException {
        if (!buf.hasRemaining()) {
          return -1;
        }
        // Read only what's left
        len = Math.min(len, buf.remaining());
        buf.get(bytes, off, len);
        return len;
      }
    };
  }


  private static class StringDataSource implements DataSource {
    private String contents;
    private String mimetype;
    private String name;


    public StringDataSource(String contents, String mimetype, String name) {
      this.contents = contents;
      this.mimetype = mimetype;
      this.name = name;
    }

    public String getContentType() {
      return (mimetype);
    }

    public String getName() {
      return (name);
    }

    public InputStream getInputStream() {
      return (new StringBufferInputStream(contents));
    }

    public OutputStream getOutputStream() {
      throw new IllegalAccessError("This datasource cannot be written to");
    }
  }*/


  public static InternetAddress getSystemInternetAddress() {
    InternetAddress fromIA = null;
    try {
      String compFrom = ApplicationState.getApplicationSetting(SystemConstants.FLD_MAIL_FROM);
      if (compFrom != null && compFrom.trim().length() > 0) {
        fromIA = new InternetAddress(compFrom);
        fromIA.setPersonal("Mail Service", "UTF-8");
      } else
        Tracer.platformLogger.warn(SystemConstants.FLD_MAIL_FROM + " is not configured in global settings");
    } catch (Exception e) {
      throw new PlatformRuntimeException(e);
    }
    return fromIA;
  }

  public static List<String> mailTosAsList(String manyButNotAllWaysSeparatedText) {

    List<String> tos = new ArrayList<String>();
    manyButNotAllWaysSeparatedText = normalizeEmailSep(manyButNotAllWaysSeparatedText);

    List<String> tosTmp = StringUtilities.splitToList(manyButNotAllWaysSeparatedText, ",");
    if (JSP.ex(tosTmp))
      for (String to : tosTmp) {
        if (JSP.ex(to) && to.indexOf("@") > -1)
          tos.add(to);
      }
    return tos;
  }


  public static Set<String> mailTos(String manyButNotAllWaysSeparatedText) {

    Set<String> tos = new HashSet<String>();
    manyButNotAllWaysSeparatedText = normalizeEmailSep(manyButNotAllWaysSeparatedText);

    Set<String> tosTmp = StringUtilities.splitToSet(manyButNotAllWaysSeparatedText, ",");
    if (JSP.ex(tosTmp))
      for (String to : tosTmp) {
        if (JSP.ex(to) && to.indexOf("@") > -1)
          tos.add(to);
      }
    return tos;
  }

  private static String normalizeEmailSep(String manyButNotAllWaysSeparatedText) {
    manyButNotAllWaysSeparatedText = StringUtilities.replaceAllNoRegex(manyButNotAllWaysSeparatedText, ";", ",");
    manyButNotAllWaysSeparatedText = StringUtilities.replaceAllNoRegex(manyButNotAllWaysSeparatedText, ":", ",");
    manyButNotAllWaysSeparatedText = StringUtilities.replaceAllNoRegex(manyButNotAllWaysSeparatedText, "\r", "");
    manyButNotAllWaysSeparatedText = StringUtilities.replaceAllNoRegex(manyButNotAllWaysSeparatedText, "\n", ",");
    manyButNotAllWaysSeparatedText = StringUtilities.replaceAllNoRegex(manyButNotAllWaysSeparatedText, "\t", ",");
    manyButNotAllWaysSeparatedText = StringUtilities.replaceAllNoRegex(manyButNotAllWaysSeparatedText, "<br>", ",");
    return manyButNotAllWaysSeparatedText;
  }


  public static void replyToMessage(Message message, String newMessageText) throws MessagingException {
    replyToMessage(message, newMessageText, false);
  }

  public static void replyToMessage(Message message, String newMessageText, boolean includeOriginalText) throws MessagingException {
    Message reply = message.reply(false);

    String replyText = "";
    if (includeOriginalText) {
      try {
        replyText = "\n\n----------------------------------------------\n\n"+
                (JSP.ex(message.getSubject())?">"+JSP.w(message.getSubject())+"\n":"")+
                (message.getContent() + "").replaceAll("(?m)^", "> ");
      } catch (IOException io) {
      }
    }
    reply.setText(newMessageText+replyText);
    reply.setFrom(getSystemInternetAddress());
    send(reply, getEmailSession());
  }

  // --- ADD
  private static class MyDataSource implements DataSource {

    byte[] content;
    String contentType;

    public MyDataSource(byte[] content, String contentType) {
      this.content = content;
      this.contentType = contentType;
    }

    public String getContentType() {
      return contentType;
    }

    public InputStream getInputStream() throws IOException {
      return new ByteArrayInputStream(content);
    }

    public String getName() {
      return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public OutputStream getOutputStream() throws IOException {
      return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
  }

  public static boolean verifyEmailFormat(String email) {  // creato da silvia chelazzi 14/10/08
    boolean isCorrect = false;
    if (JSP.ex(email)) {
      email = email.trim();
      if (email.indexOf("@") != -1) {
        email = email.substring(email.indexOf("@"), email.length());
        if (email.indexOf(".") != -1)
          isCorrect = true;
      }
    }
    return isCorrect;

  }

  public static String mailToUrl(String fromEmail, String toEmail, String subject, String body) {
    String mailTo = "";

    if (JSP.ex(fromEmail, toEmail)) {

      toEmail = JSP.urlEncode(toEmail);
      subject = JSP.urlEncode(subject);
      body = JSP.urlEncode(body);
      fromEmail = fromEmail.toLowerCase().trim();

      if (fromEmail.endsWith("@gmail.com")) {
        mailTo = "http://mail.google.com/mail/?view=cm&fs=1&to=" + toEmail + (JSP.ex(subject) ? "&su=" + subject : "") + (JSP.ex(body) ? "&body=" + body : "") + "&ui=1";
      } else if (fromEmail.endsWith("@hotmail.com")) {
        mailTo = "http://www.hotmail.msn.com/secure/start?action=compose&to=" + toEmail + (JSP.ex(subject) ? "&subject=" + subject : "") + (JSP.ex(body) ? "&body=" + body : "");
      } else if (fromEmail.endsWith("@yahoo.com")) {
        mailTo = "http://compose.mail.yahoo.com/?To=" + toEmail + (JSP.ex(subject) ? "&Subject=" + subject : "") + (JSP.ex(body) ? "&body=" + body : "");
      } else if (fromEmail.endsWith("@aol.com")) {
        mailTo = "http://webmail.aol.com/25045/aol/en-us/Mail/compose-message.aspx?to=" + toEmail + (JSP.ex(subject) ? "&Subject=" + subject : "") + (JSP.ex(body) ? "&body=" + body : "");
      } else {
        mailTo = "mailto:" + toEmail + (JSP.ex(subject) || JSP.ex(body) ? "?" : "") + (JSP.ex(subject) ? "subject=" + subject + (JSP.ex(body) ? "&" : "") : "") + (JSP.ex(body) ? "body=" + body : "");
      }
    }
    return mailTo;
  }

  public static List<Part> getMessageParts(Message message) throws MessagingException, IOException {
    List<Part> ret = new ArrayList();

    if (message.isMimeType("multipart/*")) {

      Multipart multipart = (Multipart) message.getContent();

      buildPartInfoList(ret, multipart);
    }
    return ret;
  }

  private static void buildPartInfoList(List<Part> partlist, Multipart mp) throws MessagingException, IOException {

    for (int i = 0; i < mp.getCount(); i++) {
      //Get part
      Part apart = mp.getBodyPart(i);
      //handle single & multiparts
      if (apart.isMimeType("multipart/*")) {
        //recurse
        buildPartInfoList(partlist, (Multipart) apart.getContent());
      } else {
        //append the part
        partlist.add(apart);
      }
    }
  }

  public static PersistentFile extractPeristentFileFromEmailPart(Part part) throws MessagingException, StoreException, IOException {

    String fileNamePossiblyEncoded1 = JSP.ex(part.getFileName()) ? MimeUtility.decodeText(part.getFileName()) : "attachment";
    PersistentFile persistentFile = new PersistentFile(0, null, PersistentFile.TYPE_FILESTORAGE);
    persistentFile.setUID(CounterHome.next(PersistentFile.PERSISTENTFILE_ID));
    persistentFile.fileDir = File.separator + "documents";
    new File(ApplicationState.getApplicationSetting(SystemConstants.FLD_REPOSITORY_URL) + persistentFile.fileDir).mkdirs();
    String folderLocation = ApplicationState.getApplicationSetting(SystemConstants.FLD_REPOSITORY_URL) + File.separator + "documents";
    String code = FileUtilities.padd(persistentFile.getUID() + "", 6, "0");
    String fileName = (code != "" ? code + '.' : "") + fileNamePossiblyEncoded1;
    String completePath = folderLocation + (!folderLocation.endsWith("/") ? "/" : "") + fileName;

    FileOutputStream fos = new FileOutputStream(completePath);
    InputStream inputStream = part.getInputStream();

    byte[] byteCount = new byte[1024]; // la dimensione puo essere "aggiustata" per esigenze specifiche
    int read;
    for (; ;) {
      read = inputStream.read(byteCount);
      if (read < 0)
        break;
      fos.write(byteCount, 0, read);
    }
    fos.flush();
    inputStream.close();
    fos.close();

    persistentFile.setType(PersistentFile.TYPE_FILESTORAGE);
    persistentFile.setOriginalFileName(fileNamePossiblyEncoded1);
    persistentFile.setFileLocation(persistentFile.fileDir + File.separator + fileName);

    return persistentFile;
  }


  public static class Email {

    public String subject;
    public String text;
    public List<PersistentFile> attachments = new ArrayList();


    public Email(Message message) throws MessagingException, IOException, StoreException {
      ContentType ct = MailMessageUtilities.getContentType(message.getContentType());
      subject = message.getSubject();
      StringBuffer content = new StringBuffer();

      if ("multipart".equalsIgnoreCase(ct.getPrimaryType())) {

        for (Part part : MailHelper.getMessageParts(message)) {

          ContentType ctPart = MailMessageUtilities.getContentType(part.getContentType());

          String disposition = part.getDisposition();
          if (Part.ATTACHMENT.equals(disposition)) { //|| Part.INLINE.equals(disposition)

            PersistentFile attachedFile = MailHelper.extractPeristentFileFromEmailPart(part);
            attachments.add(attachedFile);

          } else if ("text".equalsIgnoreCase(ctPart.getPrimaryType()) && "plain".equalsIgnoreCase(ctPart.getSubType())) {
            /*/ use the charset to read data correctly
           String charset=ctPart.getParameter("charset");
           if (!JSP.ex(charset))
             charset="UTF-8";
           FileUtilities.readInputStream(part.getInputStream(), content,charset);*/
            MailMessageUtilities.decodeTextPlain(content, part);
          } else if ("multipart".equalsIgnoreCase(ctPart.getPrimaryType()) && "alternative".equalsIgnoreCase(ctPart.getSubType())) {
            MailMessageUtilities.decodeContent(part, content, null, null);
          }
        }
      } else {
        if (message.getContent() instanceof String) {
          content.append(message.getContent());
          Tracer.emailLogger.debug("\ncontent: " + text);
        } else {
          InputStream inputStream = (InputStream) message.getContent();
          FileUtilities.readInputStream(inputStream, content);
        }
      }

      text = content.toString();
    }
  }


}