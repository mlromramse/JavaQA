<%@ page import="org.jblooming.persistence.hibernate.PersistenceContext, org.jblooming.tracer.Tracer, org.jblooming.waf.view.PageState, java.sql.Connection, java.sql.ResultSet, java.sql.Statement, java.sql.SQLException, com.QA.*, java.util.Map, java.util.HashMap, org.jblooming.utilities.math.MathParse, org.jblooming.utilities.JSP, java.lang.management.MemoryPoolMXBean, org.jblooming.utilities.StringUtilities, org.jblooming.waf.settings.ApplicationState, org.jblooming.persistence.exceptions.FindByPrimaryKeyException, org.jblooming.utilities.DateUtilities, java.util.Date" %>
<%!
  static String dbName = "pup";

  public void importTagForQuestion(Question q) throws SQLException {
    ResultSet rs = null;
    PersistenceContext pc = PersistenceContext.getDefaultPersistenceContext();
    Connection conn = pc.session.connection();
    Statement statement = conn.createStatement();
    try{
    String query = " select tag.name name from `"+dbName+"`.`forum_tag` tag, `"+dbName+"`.`forum_node_tags` mix where mix.tag_id = tag.id  and mix.node_id =  " + q.getExternalCode();
    rs = statement.executeQuery(query);
    while (rs.next()) {
      String name = rs.getString("name");
      if (JSP.ex(name)) {
      Tag tag = Tag.loadOrCreate(name);
      q.getTags().add(tag);
      }
    }
    }catch(Throwable t){
      Tracer.platformLogger.error("-----------> cannot import tag for question " + q.getExternalCode(), t);
    }  finally {
      if(rs != null){
        rs.close();
        statement.close();
      }
    }
  }

  public void importAnswers(Question q, Map<String, QAOperator> users) throws SQLException {
    PersistenceContext pc = PersistenceContext.getDefaultPersistenceContext();
    Connection conn = pc.session.connection();
    Statement statement = conn.createStatement();
    ResultSet rs = null;
    try{
      String query = "select * from `"+dbName+"`.`forum_node` where node_type = 'answer' and state_string <> '(deleted)' and parent_id = " +  q.getExternalCode();
      rs = statement.executeQuery(query);
      while (rs.next()) {
        String body = rs.getString("body");
        String stato = rs.getString("state_string");
        String answerOsqaId = rs.getString("id");
        String ownerId = rs.getString("author_id");
        int score = rs.getInt("score");
        QAOperator owner = getOrCreateUser(ownerId,users);
        Answer a = new Answer();
        a.setText(body);
        a.store();
        a.setOwner(owner);
        a.setQuestion(q);
        if("(accepted)".equalsIgnoreCase(stato)){
          a.setAsAccepted();
          q.hit(owner, QAEvent.QUESTION_ANSWER_ACCEPT);
        }
        //a.setVotes(score);
        for(int i=0; i<score; i++){
          Upvote u = new Upvote();
          u.setOperator(owner);
          u.setAnswerAndPropagate(a);
          u.store();
          q.hit(owner, QAEvent.QUESTION_ANSWER_UPVOTE);
        }
        q.hit(owner, QAEvent.QUESTION_ANSWER_CREATE);
        importAnswerComments(a, answerOsqaId,users);

      }
    }catch(Throwable t){
      Tracer.platformLogger.error("-----------> cannot import answers for question " + q.getExternalCode(), t);
    }  finally {
      if(rs != null){
        rs.close();
        statement.close();
      }
    }
  }

  public void importAnswerComments(Answer a, String answerOsqaId,Map<String, QAOperator> users) throws SQLException {
    PersistenceContext pc = PersistenceContext.getDefaultPersistenceContext();
    Connection conn = pc.session.connection();
    Statement statement = conn.createStatement();
    ResultSet rs = null;
    try{
      String query = "select * from `"+dbName+"`.`forum_node` where node_type = 'comment' and state_string <> '(deleted)' and parent_id = " +  answerOsqaId;
      rs = statement.executeQuery(query);
      Question q = a.getQuestion();
      while (rs.next()) {
        String body = rs.getString("body");
        String ownerId = rs.getString("author_id");
        QAOperator owner = getOrCreateUser(ownerId, users);
        Comment c = new Comment();
        c.store();
        c.setAnswer(a);
        c.setOwner(owner);
        c.setText(body);
        a.getComments().add(c);
        q.hit(owner, QAEvent.QUESTION_ANSWER_COMMENT_CREATE);
      }
    }catch(Throwable t){
      Tracer.platformLogger.error("-----------> cannot import comment for answer " + answerOsqaId, t);
    }  finally {
      if(rs != null){
        rs.close();
        statement.close();
      }
    }

  }

  public void importQuestionComments(Question q, Map<String, QAOperator> users) throws SQLException {
    PersistenceContext pc = PersistenceContext.getDefaultPersistenceContext();
    Connection conn = pc.session.connection();
    Statement statement = conn.createStatement();
    ResultSet rs = null;
    try{
      String query = "select * from `"+dbName+"`.`forum_node` where node_type = 'comment' and state_string <> '(deleted)' and parent_id = " +  q.getExternalCode();
      rs = statement.executeQuery(query);
      while (rs.next()) {
        String body = rs.getString("body");
        String ownerId = rs.getString("author_id");
        QAOperator owner = getOrCreateUser(ownerId, users);
        Comment c = new Comment();
        c.setQuestion(q);
        c.setText(body);
        c.store();
        c.setOwner(owner);
        q.getComments().add(c);
        q.hit(owner, QAEvent.QUESTION_COMMENT_CREATE);
      }
    }catch(Throwable t){
      Tracer.platformLogger.error("-----------> cannot import comment for question " + q.getExternalCode(), t);
    }  finally {
      if(rs != null){
        rs.close();
        statement.close();
      }
    }

  }

  public QAOperator getOrCreateUser(String userID, Map<String, QAOperator> users) throws Throwable {

    QAOperator op = users.get(userID);
    if(op != null){
      return op;
    }else{
      
      PersistenceContext pc = PersistenceContext.getDefaultPersistenceContext();
      Connection conn = pc.session.connection();
      Statement statement = conn.createStatement();
      ResultSet rs = null;
      try{
        String query = "select * from `"+dbName+"`.`auth_user` where is_active = 1 and id= " + userID;
        rs = statement.executeQuery(query);
        while (rs.next()) {
          
          String username = rs.getString("username");
          String name = rs.getString("first_name");
          String email = rs.getString("email");
          op = QAOperator.loadByLoginName(username);
          if (op == null){
            op = new QAOperator();
            op.setLoginName(username);
            op.changePassword(StringUtilities.md5Encode(username + Math.random(), "d4sty"));

            //search for auth
            String queryAuth = "select * from `"+dbName+"`.`forum_authkeyuserassociation` where user_id= " + userID;
            Statement statementA = conn.createStatement();
            ResultSet rsA = statementA.executeQuery(queryAuth);
            if (rsA.next()){
            String provider =  rsA.getString("provider");
              if ("google".equalsIgnoreCase(provider) && JSP.ex(email)) {
                op.setWebsite(email+"@google");
              }
            }
            rsA.close();
            statementA.close();

            op.setName(JSP.ex(name) ? name : username);
            if (JSP.ex(email)) {
              boolean duplicateEmail = false;
              for (QAOperator qe : users.values()) {
                if (email.equals(qe.getEmail())) {
                  duplicateEmail = true;
                  break;
                }
              }
              if (!duplicateEmail)
                op.setEmail(email);

            }
            op.setEnabled(true);
            op.store();
          }

        }
        
        if(op == null){
          op = QAOperator.load(1);
        } else
          users.put(userID,op);

      }catch(Throwable t){
        Tracer.platformLogger.error("-----------> cannot import operator " + userID, t);
        try {
          op= QAOperator.load(1);
        } catch (FindByPrimaryKeyException e) {
        }
      }  finally {
        if(rs != null){
          rs.close();
          statement.close();
        }
      }

      return op;
    }
   }
%><%

  PageState pageState = PageState.getCurrentPageState();
  QAOperator adm = (QAOperator) pageState.getLoggedOperator();
  adm.testIsAdministrator();

  String query = "SELECT  *  FROM    `"+dbName+"`.`forum_node` t " +
          "  WHERE   t.node_type = 'question' and state_string <> '(deleted)' ";
  PersistenceContext pc = null;
  pc = new PersistenceContext();
  Connection conn = null;
  Statement statement = null;
  ResultSet rs = null;
  Map<String, QAOperator> users = new HashMap();
  try {
    pc = PersistenceContext.getDefaultPersistenceContext();
    conn = pc.session.connection();
    statement = conn.createStatement();
    rs = statement.executeQuery(query);
    int c = 0;

    while (rs.next()) {
      String extCode = rs.getString("id");

      String userId = rs.getString("author_id");
      QAOperator owner = getOrCreateUser(userId, users);
      if (owner!=null) {

      Question q = Question.loadByExternalId(extCode);
      if(q == null){
       q = new Question();
       q.setExternalCode(extCode);
       String dateS = rs.getString("last_activity_at");
       if (JSP.ex(dateS)) {
         Date d = DateUtilities.dateFromString(dateS, "yyyy-MM-dd HH:mm:ss");
         q.setCreationDate(d);
       }
      }

      String body = rs.getString("body");
      String title = rs.getString("title");

      int score = rs.getInt("score");

      q.setSubject(title);
      q.setDescription(body);
      q.setOwner(owner);
      q.store();

      // import tag 
      importTagForQuestion(q);
      // import answers
      importAnswers(q, users);
      // import comments
      importQuestionComments(q, users);

      for(int i=0; i<score; i++){
        Upvote u = new Upvote();
        u.setOperator(owner);
        u.setQuestionAndPropagate(q);
        u.store();
        q.hit(owner, QAEvent.QUESTION_UPVOTE);
      }

      c++;
      if (c % 20 == 0)
        pc.checkPoint();
      }
    }
  } catch (Throwable t) {
    Tracer.desperatelyLog("", true, t);
  } finally {
    rs.close();
    statement.close();
    if (pc != null)
      pc.commitAndClose();
  }
%>OK