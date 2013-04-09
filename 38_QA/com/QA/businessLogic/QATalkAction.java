package com.QA.businessLogic;

import com.QA.*;
import net.sf.json.JSONObject;
import net.tanesha.recaptcha.ReCaptchaImpl;
import net.tanesha.recaptcha.ReCaptchaResponse;
import org.jblooming.agenda.CompanyCalendar;
import org.jblooming.oql.OqlQuery;
import org.jblooming.persistence.exceptions.*;
import org.jblooming.utilities.JSP;
import org.jblooming.utilities.StringUtilities;
import org.jblooming.waf.exceptions.ActionException;
import org.jblooming.waf.settings.ApplicationState;
import org.jblooming.waf.settings.I18n;
import org.jblooming.waf.view.PageState;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;


public class QATalkAction {

  PageState pageState = null;
  QAOperator logged = null;

  public QATalkAction() throws PersistenceException {
    pageState = PageState.getCurrentPageState();
    logged = (QAOperator) pageState.getLoggedOperator();
  }


  public JSONObject cmdLikeQuestion(JSONObject jsResponse) throws PersistenceException {

    int id = pageState.getEntry("questionId").intValueNoErrorCodeNoExc();
    Question question = Question.load(id);

    //did you upvote it?
    Upvote mine = null;
    for (Upvote a : question.getUpvotes()) {
      if (a.getOperator() != null && a.getOperator().equals(logged)) {
        mine = a;
        break;
      }
    }
    if (mine == null) {
      Upvote u = new Upvote();
      u.setQuestionAndPropagate(question);
      question.getUpvotes().add(u);
      u.setOperator(logged);
      u.store();
      question.hitAndNotify(logged, QAEvent.QUESTION_UPVOTE);
    }

    jsResponse.element("question", question.jsonify(logged, false));

    return jsResponse;
  }

  public JSONObject cmdUnLikeQuestion(JSONObject jsResponse) throws PersistenceException {

    int id = pageState.getEntry("questionId").intValueNoErrorCodeNoExc();
    Question question = Question.load(id);
    Upvote mine = null;
    for (Upvote a : question.getUpvotes()) {
      if (a.getOperator() != null && a.getOperator().equals(logged)) {
        mine = a;
        break;
      }
    }
    if (mine != null) {
      question.getUpvotes().remove(mine);
      mine.remove();
    }

    jsResponse.element("question", question.jsonify(logged, false));

    return jsResponse;
  }

  public JSONObject cmdCommentQuestion(JSONObject jsResponse) throws PersistenceException, ActionException {

    int id = pageState.getEntry("questionId").intValueNoErrorCodeNoExc();
    Question question = Question.load(id);

    String comm = pageState.getEntry("commentText").stringValue();
    if (JSP.ex(comm)) {
      Comment c = new Comment();
      c.setQuestion(question);
      c.setOwner(logged);
      c.setText(comm);
      c.store();
      question.hitAndNotify(logged, QAEvent.QUESTION_COMMENT_CREATE);
      jsResponse.element("comment", c.jsonify(logged));
    }


    return jsResponse;
  }

  public JSONObject cmdCommentRemove(JSONObject jsResponse) throws PersistenceException, ActionException {

    int id = pageState.getEntry("commentId").intValueNoErrorCodeNoExc();
    Comment c = Comment.load(id);
    c.remove();
    jsResponse.element("questionId", c.getQuestion().getId());

    return jsResponse;
  }


  public JSONObject cmdReportQuestion(JSONObject jsResponse) throws PersistenceException {
    int id = pageState.getEntry("questionId").intValueNoErrorCodeNoExc();
    Question question = Question.load(id);

    List<QAOperator> mods = QAOperator.getModerators();
    String message = I18n.get("QUESTION_SIGNALLED_BY_%%", "(" + logged.getId() + ") " + logged.getDisplayName()) + ": <a href=\"" + question.getURL().toLinkToHref() + "\">" +
            JSP.htmlEncodeApexesAndTags(question.getDescription()) + "</a>";
    for (QAOperator mod : mods)
      mod.sendNote("QUESTION_SIGNALLED", message, QAEvent.QUESTION_SIGNALLED.toString());
    return jsResponse;
  }

  public JSONObject cmdReportComment(JSONObject jsResponse) throws PersistenceException {
    int id = pageState.getEntry("commentId").intValueNoErrorCodeNoExc();
    Comment comment = Comment.load(id);

    List<QAOperator> mods = QAOperator.getModerators();
    String message = I18n.get("COMMENT_SIGNALLED_BY_%%", "(" + logged.getId() + ") " + logged.getDisplayName()) + ": <a href=\"" + comment.getQuestion().getURL().toLinkToHref() + "\">" +
            JSP.htmlEncodeApexesAndTags(comment.getText()) + "</a>";
    for (QAOperator mod : mods)
      mod.sendNote("COMMENT_SIGNALLED", message, QAEvent.COMMENT_SIGNALED.toString());
    return jsResponse;
  }


  public JSONObject cmdCommentAnswer(JSONObject jsResponse) throws PersistenceException, ActionException {

    int id = pageState.getEntry("answerId").intValueNoErrorCodeNoExc();
    Answer question = Answer.load(id);

    String comm = pageState.getEntry("commentText").stringValue();
    if (JSP.ex(comm)) {
      Comment c = new Comment();
      c.setAnswer(question);
      c.setOwner(logged);
      c.setText(comm);
      c.store();
      question.hitAndNotify(logged, QAEvent.QUESTION_ANSWER_COMMENT_CREATE);
      jsResponse.element("comment", c.jsonify(logged));
    }


    return jsResponse;
  }

  public JSONObject cmdCommentAnswerRemove(JSONObject jsResponse) throws PersistenceException, ActionException {

    int id = pageState.getEntry("commentId").intValueNoErrorCodeNoExc();
    Comment c = Comment.load(id);
    c.remove();
    jsResponse.element("answerId", c.getAnswer().getId());

    return jsResponse;
  }

  public JSONObject cmdReportAnswerComment(JSONObject jsResponse) throws PersistenceException {
    int id = pageState.getEntry("commentId").intValueNoErrorCodeNoExc();
    Comment comment = Comment.load(id);

    List<QAOperator> mods = QAOperator.getModerators();
    String message = I18n.get("COMMENT_ANSWER_SIGNALLED_BY_%%_%%_%%", comment.getText(), "(" + logged.getId() + ")" + logged.getDisplayName(), ": <a href=\"" +
            comment.getAnswer().getQuestion().getURL().toLinkToHref() + "\">" + JSP.htmlEncodeApexesAndTags(comment.getText()) + "</a>");
    for (QAOperator mod : mods)
      mod.sendNote("COMMENT_SIGNALLED", message, QAEvent.COMMENT_SIGNALED.toString());
    return jsResponse;
  }


  public JSONObject cmdBanQuestion(JSONObject jsResponse) throws PersistenceException {
    int id = pageState.getEntry("questionId").intValueNoErrorCodeNoExc();
    Question question = Question.load(id);
    if (logged.isModerator()) {
      question.setDeleted(!question.isDeleted());
      question.store();
    }
    return jsResponse;
  }

  public JSONObject cmdReportAnswer(JSONObject jsResponse) throws PersistenceException {
    int id = pageState.getEntry("answerId").intValueNoErrorCodeNoExc();
    Answer answer = Answer.load(id);

    List<QAOperator> mods = QAOperator.getModerators();
    String message = I18n.get("ANSWER_SIGNALLED_BY_%%", "(" + logged.getId() + ") " + logged.getDisplayName()) + ": <a href=\"" + answer.getQuestion().getURL().toLinkToHref() + "\">" +
            JSP.htmlEncodeApexesAndTags(answer.getText()) + "</a>";
    for (QAOperator mod : mods)
      mod.sendNote("ANSWER_SIGNALLED", message, QAEvent.QUESTION_SIGNALLED.toString());
    return jsResponse;
  }

  public JSONObject cmdBanAnswer(JSONObject jsResponse) throws PersistenceException {
    int id = pageState.getEntry("answerId").intValueNoErrorCodeNoExc();
    Answer answer = Answer.load(id);
    if (logged.isModerator()) {
      answer.setDeleted(true);
      answer.store();
    }
    return jsResponse;
  }


  public Question cmdSave() throws PersistenceException, org.jblooming.security.SecurityException, ActionException {


    Question question = null;
    String sub = pageState.getEntryAndSetRequired("SUBJECT").stringValueNullIfEmpty();
    String text = pageState.getEntryAndSetRequired("QUESTION").stringValueNullIfEmpty();
    String tags = pageState.getEntryAndSetRequired("TAGS").stringValueNullIfEmpty();

    boolean isNew = false;
    if (pageState.mainObjectId == null) {

      //create new
      question = new Question();
      question.testPermission(logged, QAPermission.QUESTION_CREATE);
      isNew = true;
    } else {
      question = Question.load(pageState.mainObjectId);
      question.testPermission(logged, QAPermission.QUESTION_EDIT);
    }

    //
    if (!isNew && (question.getQuestionRevisions().size() > 0 || !logged.equals(question.getOwner()))) {
      QuestionRevision qr = QuestionRevision.createRevision(question, logged);
      qr.store();

      question.getQuestionRevisions().add(qr);
    } else {
      question.setOwner(logged);
    }

    question.setSubject(sub);
    question.setDescription(text);


    //can create brand new tags?
    String grace = ApplicationState.getApplicationSetting("QUESTION_GRACE");
    boolean createBrandNewTags = "yes".equals(grace) || logged.isModerator() || logged.getKarma() > QAPermission.TAG_CREATE.reputationRequired;

    if (JSP.ex(tags)) {
      List<String> tagsL = StringUtilities.splitToList(tags, ",");
      question.setTags(new ArrayList<Tag>());
      for (String tagname : tagsL) {
        if (createBrandNewTags) {
          Tag tag1 = Tag.loadOrCreate(tagname);
          question.getTags().add(tag1);
        } else {
          Tag tag1 = Tag.loadByName(tagname);
          if (tag1 != null)
            question.getTags().add(tag1);
          else {
            //should tell that could not be created
            //pageState.addClientEntry("TAG_NOT_CREATED", JSP.w(pageState.getEntry("TAG_NOT_CREATED").stringValueNullIfEmpty()) + " " + tagname);
            pageState.removeEntry("TAGS");
            throw new ActionException("USE_ONLY_EXISTING_TAG");
          }
        }
      }
    } else {
      if (createBrandNewTags)
        throw new ActionException("MISSING_TAG");
      else
        throw new ActionException("PICK_A_TAG");
    }

    question.store();
    question.hit(logged, isNew ? QAEvent.QUESTION_CREATE : QAEvent.QUESTION_EDIT);

    return question;
  }


  public void cmdDelete() throws FindByPrimaryKeyException, RemoveException, StoreException {
    Question leaf = Question.load(pageState.mainObjectId);
    leaf.setDeleted(true);
    leaf.store();
  }

  public static SearchResults findPopularQuestions(int maxResults, String filter) throws FindException {

    String hqlFirstPart = "select count(upvote), question from " + Upvote.class.getName() + " as upvote where upvote.question.deleted=false and upvote.question.contentRating is null ";
    String hqlSecondPart = " group by question order by count(upvote) desc, upvote.lastModified desc";
    String hql = hqlFirstPart;
    boolean hasFilter = JSP.ex(filter);

    if (hasFilter) {
      hql = hql + " and upvote.question.description like :param";
    } else {
      hql = hql + " and upvote.question.lastModified>:sixdays";
    }
    hql = hql + hqlSecondPart;

    //String hql = "select sum(hit.weight), operatorId from "+ Hit.class.getName()+" as hit where hit.when>:since group by operatorId order by sum(hit.weight) desc";
    OqlQuery oql = new OqlQuery(hql);

    if (!hasFilter) {
      CompanyCalendar cc = new CompanyCalendar();
      cc.add(CompanyCalendar.MONTH, -2);
      cc.setAndGetTimeToDayStart();
      oql.getQuery().setTimestamp("sixdays", cc.getTime());
    } else {
      oql.getQuery().setString("param", "%" + filter + "%");
    }

    oql.getQuery().setMaxResults(maxResults + 1);
    List<Object[]> os = oql.list();
    SearchResults r = new SearchResults();
    int inserted = 0;
    for (Object[] o : os) {
      Question q = (Question) o[1];
      r.add(SearchResultType.QUESTION, q.getDescription(), q);
      inserted++;
      if (inserted > maxResults)
        break;
    }
    r.hasMore = os.size() > maxResults;
    return r;
  }


  public static SearchResults findQuestions(int maxResults, String filter) throws FindException {

    String hqlFirstPart = "select question from " + Question.class.getName() + " as question where question.deleted=false ";
    String hqlSecondPart = " order by question.totUpvotesFromQandA desc, question.lastModified desc";
    String hql = hqlFirstPart;

    hql = hql + " and (question.description like :param or question.subject like :param)";

    hql = hql + hqlSecondPart;

    OqlQuery oql = new OqlQuery(hql);

    oql.getQuery().setString("param", "%" + filter + "%");

    oql.getQuery().setMaxResults(maxResults + 1);
    List<Question> os = oql.list();
    int inserted = 0;
    SearchResults r = new SearchResults();
    for (Question q : os) {
      r.add(SearchResultType.QUESTION, q.getDescription(), q);
      inserted++;
      if (inserted > maxResults)
        break;
    }
    r.hasMore = os.size() > maxResults;

    //inject comments
    if (!r.hasMore) {

      hqlFirstPart = "select comm from " + Comment.class.getName() + " as comm where comm.question.deleted=false ";
      hqlSecondPart = " order by comm.question.totUpvotesFromQandA desc, comm.lastModified desc";
      hql = hqlFirstPart;

      hql = hql + " and (comm.text like :param)";

      hql = hql + hqlSecondPart;

      oql = new OqlQuery(hql);

      oql.getQuery().setString("param", "%" + filter + "%");

      oql.getQuery().setMaxResults(maxResults + 1);
      List<Comment> oc = oql.list();
      inserted = 0;
      for (Comment c : oc) {
        r.add(SearchResultType.QUESTION_COMMENT, c.getText(), c.getQuestion());
        inserted++;
        if (inserted > maxResults)
          break;
      }
    }

    return r;
  }

  public static SearchResults findHotQuestions(int maxResults, String filter) throws FindException {

    String hqlFirstPart = "select count(upvote), question from " + Upvote.class.getName() + " as upvote where  upvote.question.deleted=false ";
    String hqlSecondPart = " group by question order by count(upvote) desc, upvote.question.lastModified desc";
    String hql = hqlFirstPart;
    boolean hasFilter = JSP.ex(filter);

    if (hasFilter) {
      hql = hql + " and upvote.question.description like :param";
    } else {
      hql = hql + " and upvote.question.lastModified>:sixtydays";
    }
    hql = hql + hqlSecondPart;

    //String hql = "select sum(hit.weight), operatorId from "+ Hit.class.getName()+" as hit where hit.when>:since group by operatorId order by sum(hit.weight) desc";
    OqlQuery oql = new OqlQuery(hql);

    if (!hasFilter) {
      CompanyCalendar cc = new CompanyCalendar();
      cc.add(CompanyCalendar.MONTH, -2);
      cc.setAndGetTimeToDayStart();
      oql.getQuery().setTimestamp("sixtydays", cc.getTime());
    } else {
      oql.getQuery().setString("param", "%" + filter + "%");
    }

    oql.getQuery().setMaxResults(maxResults + 1);
    List<Object[]> os = oql.list();
    SearchResults r = new SearchResults();
    int inserted = 0;
    for (Object[] o : os) {
      Question q = (Question) o[1];
      r.add(SearchResultType.QUESTION, q.getDescription(), q);
      inserted++;
      if (inserted > maxResults)
        break;
    }
    r.hasMore = os.size() > maxResults;
    return r;
  }

  public static SearchResults findAnswers(int maxResults, String filter) throws FindException {

    String hqlFirstPart = "select ans from " + Answer.class.getName() + " as ans where ans.question.deleted=false and ans.deleted=false ";
    String hqlSecondPart = " order by ans.question.totUpvotesFromQandA desc, ans.question.lastModified desc";
    String hql = hqlFirstPart;

    hql = hql + " and ans.text like :param";

    hql = hql + hqlSecondPart;

    OqlQuery oql = new OqlQuery(hql);

    oql.getQuery().setString("param", "%" + filter + "%");

    oql.getQuery().setMaxResults(maxResults + 1);
    List<Answer> os = oql.list();
    int inserted = 0;
    SearchResults r = new SearchResults();
    for (Answer answer : os) {
      r.add(SearchResultType.ANSWER, answer.getText(), answer.getQuestion());
      inserted++;
      if (inserted > maxResults)
        break;
    }
    r.hasMore = os.size() > maxResults;

    //inject comments
    if (!r.hasMore) {

      hqlFirstPart = "select comm from " + Comment.class.getName() + " as comm where comm.answer.deleted=false ";
      hqlSecondPart = " order by comm.answer.totUpvotesAndAcceptance desc, comm.lastModified desc";
      hql = hqlFirstPart;

      hql = hql + " and (comm.text like :param)";

      hql = hql + hqlSecondPart;

      oql = new OqlQuery(hql);

      oql.getQuery().setString("param", "%" + filter + "%");

      oql.getQuery().setMaxResults(maxResults + 1);
      List<Comment> oc = oql.list();
      inserted = 0;
      for (Comment c : oc) {
        r.add(SearchResultType.ANSWER_COMMENT, c.getText(), c.getAnswer().getQuestion());
        inserted++;
        if (inserted > maxResults)
          break;
      }
    }


    return r;
  }

  public static SearchResults findTags(int maxResults, String filter) throws FindException {

    String hql = "select tag from " + Tag.class.getName() + " as tag where tag.name like :filter ";
    OqlQuery oql = new OqlQuery(hql);
    oql.getQuery().setString("filter", "%" + filter + "%");
    oql.getQuery().setMaxResults(maxResults + 1);
    List<Tag> os = oql.list();
    int inserted = 0;
    SearchResults r = new SearchResults();

    for (Tag answer : os) {
      r.add(SearchResultType.TAG, answer.getName(), answer.getQuestions(1).get(0));
      inserted++;
      if (inserted > maxResults)
        break;
    }
    r.hasMore = os.size() > maxResults;

    return r;
  }

  public static SearchResults findUsers(int maxResults, String filter) throws FindException {

    String hql = "select u from " + QAOperator.class.getName() + " as u where u.enabled=true AND (u.name like :filter OR u.email like :filter OR u.loginName like :filter)";
    OqlQuery oql = new OqlQuery(hql);
    oql.getQuery().setString("filter", "%" + filter + "%");
    oql.getQuery().setMaxResults(maxResults + 1);
    List<QAOperator> os = oql.list();
    int inserted = 0;
    SearchResults r = new SearchResults();

    for (QAOperator answer : os) {
      r.add(SearchResultType.USER, answer.getId()+"", null);
      inserted++;
      if (inserted > maxResults)
        break;
    }
    r.hasMore = os.size() > maxResults;

    return r;
  }

  public static SearchResults search(int maxResults, String filter) throws FindException {
    SearchResults sr = findQuestions(maxResults, filter);
    int remainingToSearch = maxResults - sr.searchResults.size();
    if (!sr.hasMore && remainingToSearch > 0) {
      SearchResults sra = findAnswers(maxResults, filter);
      sr.searchResults.addAll(sra.searchResults);
      remainingToSearch = remainingToSearch - sra.searchResults.size();
      if (!sra.hasMore && remainingToSearch > 0) {
        SearchResults srt = findTags(maxResults, filter);
        sr.searchResults.addAll(srt.searchResults);
      }
    }

    SearchResults sru = findUsers(maxResults, filter);
    sr.searchResults.addAll(sru.searchResults);

    return sr;
  }


  public JSONObject cmdAcceptAnswer(JSONObject jsResponse) throws FindByPrimaryKeyException, StoreException {
    int id = pageState.getEntry("answerId").intValueNoErrorCodeNoExc();
    Answer a = Answer.load(id);
    Question question = a.getQuestion();
    if (question.getOwner().equals(logged) || logged.hasPermissionAsAdmin() ) {
      a.setAsAccepted();
      a.store();
      question.store();
      a.hitAndNotify(question.getOwner(), QAEvent.QUESTION_ANSWER_ACCEPT);
      jsResponse.element("answer", a.jsonify(logged, false));
    }
    return jsResponse;
  }

  public JSONObject cmdRefuteAnswer(JSONObject jsResponse) throws FindByPrimaryKeyException, StoreException {
    int id = pageState.getEntry("answerId").intValueNoErrorCodeNoExc();
    Answer a = Answer.load(id);
    Question question = a.getQuestion();
    if (question.getOwner().equals(logged) || logged.hasPermissionAsAdmin()) {
      a.setAsRefuted();
      a.store();
      question.store();
      jsResponse.element("answer", a.jsonify(logged, false));
    }
    return jsResponse;
  }

  public JSONObject cmdLikeAnswer(JSONObject jsResponse) throws FindByPrimaryKeyException, StoreException {
    int id = pageState.getEntry("answerId").intValueNoErrorCodeNoExc();
    Answer answer = Answer.load(id);

    //did you upvote it?
    Upvote mine = null;
    for (Upvote a : answer.getUpvotes()) {
      if (a.getOperator() != null && a.getOperator().equals(logged)) {
        mine = a;
        break;
      }
    }
    if (mine == null) {
      Upvote u = new Upvote();
      u.setAnswerAndPropagate(answer);
      answer.getUpvotes().add(u);
      u.setOperator(logged);
      u.store();
      answer.hitAndNotify(logged, QAEvent.QUESTION_ANSWER_UPVOTE);
    }

    jsResponse.element("answer", answer.jsonify(logged, false));

    return jsResponse;
  }

  public JSONObject cmdUnLikeAnswer(JSONObject jsResponse) throws PersistenceException {

    int id = pageState.getEntry("answerId").intValueNoErrorCodeNoExc();
    Answer answer = Answer.load(id);
    Upvote mine = null;
    for (Upvote a : answer.getUpvotes()) {
      if (a.getOperator() != null && a.getOperator().equals(logged)) {
        mine = a;
        break;
      }
    }
    if (mine != null) {
      answer.getUpvotes().remove(mine);
      mine.remove();
    }

    jsResponse.element("answer", answer.jsonify(logged, false));

    return jsResponse;
  }

  public void cmdSaveAnswer(HttpServletRequest request) throws FindByPrimaryKeyException, ActionException, StoreException {
    Question q = Question.load(pageState.mainObjectId);
    QAOperator logged = (QAOperator) pageState.getLoggedOperator();

    String text = pageState.getEntry("yourAnswer").stringValueNullIfEmpty();
    if (!JSP.ex(text) || text.trim().length()<20) {
       throw new ActionException("QA_ANSWER_TOO_SHORT");
    }

    Answer a = null;
    boolean isNew = true;
    int aId = pageState.getEntry("ANSWER_ID").intValueNoErrorCodeNoExc();
    if (aId > 0) {
      a = Answer.load(aId);
      isNew = false;
    }
    if (logged != null && JSP.ex(text) &&
            (((a != null && a.hasPermissionFor(logged, QAPermission.ANSWER_EDIT))) || q.hasPermissionFor(logged, QAPermission.ANSWER_CREATE))
            ) {

      boolean recaptchaNeeded = !("yes".equals(pageState.getSessionState().getAttribute("PASSED_RECAPTCHA"))) && !q.hasPermissionFor(logged, QAPermission.ANSWER_CREATE_NO_RECAPTCHA);
      boolean isResponseCorrect = false;
      if (recaptchaNeeded) {
        ReCaptchaImpl reCaptcha = new ReCaptchaImpl();
        reCaptcha.setPrivateKey("6Lc0hQwAAAAAAGxnyfgUo8o5-4NHdiOJ7H5TEGY-");

        String challenge = pageState.getEntry("recaptcha_challenge_field").stringValue();
        String uresponse = pageState.getEntry("recaptcha_response_field").stringValue();
        if (JSP.ex(challenge, uresponse)) {
          ReCaptchaResponse reCaptchaResponse = reCaptcha.checkAnswer(request.getRemoteAddr(), challenge, uresponse);
          isResponseCorrect = reCaptchaResponse.isValid();
        }
      }
      if (!recaptchaNeeded || isResponseCorrect) {
        if (isResponseCorrect)
          pageState.getSessionState().setAttribute("PASSED_RECAPTCHA", "yes");

        if (a == null)
          a = new Answer();

        a.setQuestion(q);

        if (!isNew && (a.getAnswerRevision().size() > 0 || !logged.equals(a.getOwner()))) {
          AnswerRevision qr = AnswerRevision.createRevision(a, logged);
          qr.store();
          a.getAnswerRevision().add(qr);
        } else {
          a.setOwner(logged);
        }

        a.setText(text);
        a.store();

        q.hitAndNotify(logged, QAEvent.QUESTION_ANSWER_CREATE);
        pageState.removeEntry("yourAnswer");
        pageState.removeEntry("ANSWER_ID");
        pageState.addClientEntry("HAPPILY_JUST_SAVED", "yes");

      } else {

        pageState.getEntry("recaptcha_response_field").errorCode = "RECAPTCHA_FAIL";

      }
    }
  }


  public static class SearchResults {
    public boolean hasMore = false;
    public List<SearchResult> searchResults = new ArrayList();

    public void add(SearchResultType type, String abstractz, Question q) {
      SearchResult s = new SearchResult();
      s.type = type;
      s.abstractz = abstractz;
      s.reference = q;
      searchResults.add(s);
    }
  }

  public enum SearchResultType {QUESTION, QUESTION_COMMENT, ANSWER, ANSWER_COMMENT, TAG, USER};

  public static class SearchResult {
    public String abstractz;
    public SearchResultType type;
    public Question reference;

  }


}
