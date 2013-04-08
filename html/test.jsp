<%@ page import="com.QA.*, org.jblooming.persistence.exceptions.PersistenceException, org.jblooming.waf.view.PageState" %><%

  PageState pageState = PageState.getCurrentPageState();
  QAOperator logged = (QAOperator) pageState.getLoggedOperator();
  if (logged == null)
    return;
  logged.testIsAdministrator();


  QAOperator gz = null;
  try {
    gz = (QAOperator) QAOperator.findByLoginName("gz");
  } catch (PersistenceException e) {
  }
  if (gz ==null) {
    gz = new QAOperator();
    gz.setName("Gino Zucchino");
    gz.setLoginName("gz");
    gz.setPassword("a");
    gz.store();
  }

  Question q = new Question();
  q.setSubject("Is the Good, The Bad and The Ugly set before a Fistful of Dollars?");
  q.setDescription("In the Man With no Name trilogy the third movie to be made (The Good, The Bad and the Ugly (GBU)) " +
          "is sometimes said to be set before the first of the movies to be released, A Fistful of Dollars (FFD).<br>" +
          "<br>" +
          "Part of the evidence is the clothes worn by Clint Eastwood's nameless character. He picks up a poncho late in GBU and " +
          "that is what he is wearing at the start of FFD. But he seems moneyless at the start of FFD despite having uncovered large quantities of gold in GBU.<br>" +
          "<br>" +
          "Is the chronology really clear? Was it intended? Is it the same poncho? Or are we just over interpreting movies where the characters are only vaguely connected?<br>" +
          "");
  q.setOwner(gz);
  q.store();
  q.hit(logged, QAEvent.QUESTION_CREATE);

  Tag tag = Tag.loadOrCreate("lucasarts");
  q.getTags().add(tag);

  tag = Tag.loadOrCreate("star wars");
  q.getTags().add(tag);

  Category category = Category.loadOrCreate("western");
  q.setCategory(category);

  Upvote u = new Upvote();
  u.setQuestionAndPropagate(q);
  u.setOperator(gz);
  u.store();
  q.hit(gz, QAEvent.QUESTION_UPVOTE);
  //you got an upvote

  /*SomethingHappened sh = new SomethingHappened(MpEvent.QUESTION_UPVOTE,)
  SomethingHappened change = new SomethingHappened();
  change.setIdAsNew();
  change.setEventType("BOARD_POST_MODIFIED");
  change.setMessageTemplate("BOARD_POST_MODIFIED_MESSAGE_TEMPLATE");
  String boardName = board.getName();
  change.getMessageParams().put("board", boardName);
  change.getMessageParams().put("subject", stik.getType());
  change.getMessageParams().put("whom", logged.getDisplayName());
  change.setWhoCausedTheEvent(logged);
  PageSeed ps = new PageSeed(ApplicationState.serverURL + "/applications/teamwork/board/boardEditor.jsp");
  ps.setCommand(Commands.EDIT);
  ps.setMainObjectId(board.getId());
  ButtonLink edit = new ButtonLink(ps);
  edit.label = boardName;
  change.setLink(edit.toPlainLink());
  change.setIdentifiable(board);
  change.store();*/

  logged.sendNote("Upvote in Meltin'Plot", "You got a new upvote on <a>" + q.getSubject() + "</a> from <a>" + gz.getDisplayName() + "</a>.");

  //Message m = new Message("NEW_UPVOTE","You got a new upvote on <a>"+q.getSubject() + "</a> from <a>"+gz.getDisplayName()+"</a>.",q.getOwner(),);
  //m.setGroupMessageId(MpEvent.QUESTION_UPVOTE);


  Comment c = new Comment();
  c.setQuestion(q);
  c.setText("question sucks");
  c.setOwner(q.getOwner());
  c.store();

  Answer a = new Answer();
  a.setQuestion(q);
  a.setText("There are subtle clues inside the films to establish their individual timeframes, but no concrete evidence that they are connected in any way beyond the actor Clint Eastwood, and any connecting mannerisms he places in the character. Sergio Leone himself said that it was merely a packaging ploy by the American distribution company to link the films. The best we can do is establish their times by visual clues inside the films themselves.<br>" +
          "<br>" +
          "The Good, The Bad, and The Ugly has grave markers in the military cemetary shown dated 1862 and 1864, while the war is still active, so the events occur in 1864 - 1866.<br>" +
          "<br>" +
          "Fistful of Dollars and For a Few Dollars More has Clint Eastwood's revolver as the \"Single Action Army revolver\" introduced 1872. Fistful of Dollars has a gravestone dated 1873, and For a Few Dollars More shows a newspaper archive dated 1873. So these films' timeframe is 1873 or later.<br>" +
          "<br>" +
          "So the films show GBU to be at least 7-8 years before the established timeframe of the other two. Plenty of time to lose, spend, or bury those bags of gold, but, since GBU was filmed later, and not intended by Leone to be connected, that's mere conjecture.<br>" +
          "<br>" +
          "Much of this data is attributed to a variety of websites devoted to details of the films and the director, Sergio Leone.");
  a.setOwner(gz);

  a.store();


  u = new Upvote();
  u.setAnswerAndPropagate(a);
  u.setOperator(logged);
  u.store();

  u = new Upvote();
  u.setAnswerAndPropagate(a);
  u.setOperator(gz);
  u.store();


  c = new Comment();
  c.setAnswer(a);
  c.setText("answer sucks 1");
  c.setOwner(logged);
  c.store();
  a.getQuestion().hit(c.getOwner(),QAEvent.QUESTION_ANSWER_CREATE);

  c = new Comment();
  c.setAnswer(a);
  c.setText("answer sucks 2");
  c.store();
%>