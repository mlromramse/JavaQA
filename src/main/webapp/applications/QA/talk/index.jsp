<%@ page contentType="text/html; charset=utf-8" pageEncoding="UTF-8" %><%@ page import="com.QA.Answer,
                 com.QA.QAOperator,
                 com.QA.Question,
                 com.QA.Tag,
                 com.QA.waf.QAScreenApp,
                 com.QA.waf.QuestionDrawer,
                 org.jblooming.oql.OqlQuery,
                 org.jblooming.utilities.JSP,
                 org.jblooming.waf.ScreenArea,
                 org.jblooming.waf.settings.I18n,
                 org.jblooming.waf.view.PageSeed,
                 org.jblooming.waf.view.PageState,
                 java.util.List" %><%

  PageState pageState = PageState.getCurrentPageState();
  QAOperator loggOperator = (QAOperator) pageState.getLoggedOperator();

    boolean logCookie = false;
    //check if exists login cookie
    if (request.getCookies()!=null){
        for (Cookie coo : request.getCookies()) {
            if ("QALOG".equals(coo.getName())) {
                logCookie=true;
                break;
            }
        }
    }

    if(loggOperator==null && logCookie) {

        PageSeed redirTo = pageState.pageFromRoot("site/access/login.jsp");
        redirTo.addClientEntry("LOGINPENDINGURL",pageState.thisPage(request).toLinkToHref());
        redirTo.disableCache=false;
        response.sendRedirect(redirTo.toLinkToHref());
        return;
    }


  if (!pageState.screenRunning) {

    pageState.screenRunning = true;
    final ScreenArea body = new ScreenArea(request);
    QAScreenApp mpScreenApp = new QAScreenApp(body);
    mpScreenApp.hasRightColumn = false;
    mpScreenApp.register(pageState);

    pageState.getHeaderFooter().toolTip = I18n.g("QA_APP_NAME") + " - " + I18n.g("QA_TALK");
    pageState.perform(request, response);
    pageState.toHtml(pageContext);

  } else {

    QAOperator logged = (QAOperator) pageState.getLoggedOperator();

    String what = pageState.getEntry("WHAT").stringValueNullIfEmpty();

    PageSeed here = pageState.thisPage(request);

    int pageSize = pageState.getEntry("PAGESIZE").intValueNoErrorCodeNoExc();
    if (pageSize==0)
      pageSize = 3;
    else if (pageSize>20)
      pageSize = 20;

%>
<div style="width: 100%">



    <jsp:include page="../parts/partCommunityMenu.jsp">
    <jsp:param name="SHOW_ADD" value="yes"></jsp:param><jsp:param name="SHOW_BACK" value="no"></jsp:param>
</jsp:include></div>
<div id="content">



  <%

      if ("TAG".equals(what)) {
          String tagIdorName = pageState.getEntry("TAG").stringValue();
          int id = 0;
          Tag t = null;
          try {
              id = Integer.parseInt(tagIdorName);
          } catch (NumberFormatException e) {
          }
          if (id > 0)
              t = Tag.load(tagIdorName);
          else {
              t = Tag.loadByName(tagIdorName);
          }
  %><h3><span><%=I18n.get("QUESTION_TAGGED_%%",t.getName())%></span></h3><%
    }

  if ("TOP".equals(what)) {

%><h3><span><%=I18n.g("QUESTIONS_TOP")%></span></h3>

  <div>
    <div>
      <%

        List<Question> qs = Question.getTopQuestions(pageSize, false);

        for (Question q : qs) {

          new QuestionDrawer(q).toHtmlCompact(pageContext);

        }%></div>
  </div>
  <%

  } else if ("HOT".equals(what)) {

  %>
  <h3><span><%=I18n.g("QUESTIONS_HOT")%></span></h3>

  <div>
    <div>
      <%

        List<Question> qh = Question.getHotQuestions(pageSize, false);


        for (Question q : qh) {

          new QuestionDrawer(q).toHtmlCompact(pageContext);

        }%></div>
  </div>
  <%

  } else if ("UNANSWERED".equals(what)) {

  %>
  <h3><span><%=I18n.g("QUESTIONS_OPEN")%></span></h3>

  <div>
    <div style="width: 100%;">
      <%
        List<Question> qtop = Question.getTopQuestions(pageSize, true);
        for (Question q : qtop) {
          new QuestionDrawer(q).toHtmlCompact(pageContext);
        }
      %>
    </div>
  </div>
  <h3><span><%=I18n.g("QA_LATEST")%></span></h3>

  <div>
    <div style="width: 100%;">
      <%
        OqlQuery oqlQuery = new OqlQuery("from " + Question.class.getName() + " as q where q.acceptedAnswer is null and q.deleted=false order by q.creationDate desc");
        oqlQuery.getQuery().setMaxResults(pageSize);
        List<Question> qcl = oqlQuery.list();


        for (Question q : qcl) {
          new QuestionDrawer(q).toHtmlCompact(pageContext);
        }
      %></div>
  </div>
  <%

  } else if ("TAGS".equals(what)) {

  %>
  <jsp:include page="partTagView.jsp"/>
  <%

  } else if ("TAG".equals(what)) {
  %>
  <div>
    <div style="width: 100%;"><%

      //todo top questions for this tag
      String tagIdorName = pageState.getEntry("TAG").stringValue();

      int id = 0;
      Tag t = null;
      try {
        id = Integer.parseInt(tagIdorName);
      } catch (NumberFormatException e) {
      }
      if (id > 0)
        t = Tag.load(tagIdorName);
      else {
        t = Tag.loadByName(tagIdorName);
      }

      List<Question> qs = t.getQuestions(pageSize*2);
      for (Question q : qs) {
        new QuestionDrawer(q).toHtmlCompact(pageContext);
      }

    %></div>
  </div>
  <%

  } else {

  %><h3><span><%=I18n.g("QA_LATEST")%></span></h3>

    <div>
        <div style="width: 100%;">
            <%
                OqlQuery oqlQuery = new OqlQuery("from " + Question.class.getName() + " as q where q.acceptedAnswer is null and q.deleted=false order by q.creationDate desc");
                oqlQuery.getQuery().setMaxResults(pageSize);
                List<Question> qcl = oqlQuery.list();

                for (Question q : qcl) {
                    new QuestionDrawer(q).toHtmlCompact(pageContext);
                }
            %></div>
    </div>


  <h3><span><%=I18n.g("QUESTIONS_HOT")%></span></h3>

  <div>
    <div>
      <%
        List<Question> qh = Question.getHotQuestions(pageSize, false);
        for (Question q : qh) {

          new QuestionDrawer(q).toHtmlCompact(pageContext);

        }%>
    </div>
  </div>
  <h3><span><%=I18n.g("QUESTIONS_OPEN")%></span></h3>

  <div>
    <div style="width: 100%;">
      <%
        List<Question> qtop = Question.getTopQuestions(pageSize, true);
        for (Question q : qtop) {
          new QuestionDrawer(q).toHtmlCompact(pageContext);
        }
      %>
    </div>
  </div>




  <%
    }

    if (!"TAGS".equals(what)) {

    PageSeed search = pageState.thisPage(request);
    search.addClientEntry("PAGESIZE",pageSize+3);
    search.addClientEntry(pageState.getEntry("WHAT"));
    search.addClientEntry(pageState.getEntry("TAG"));

  %><div class="moreEntry"><a class="button" href="<%=search.toLinkToHref()%>#MORE_RESULTS" name="MORE_RESULTS" title="<%=I18n.g("QA_MORE_RESULTS")%>"><%=I18n.g("QA_MORE_RESULTS")%></a></div> <%

    }
  %>
</div>

<div id="rightColumn">


    <div class="contentBox stats">
        <%
            String hql = "select count(qst.id) from " + Question.class.getName() + " as qst where qst.deleted = false";
            org.hibernate.Query query = new OqlQuery(hql).getQuery();

            int qi = ((Long)query.uniqueResult()).intValue();

        %><p><%=I18n.get("QA_QUESTION_TOTAL",qi+"")%></p>


        <%
            hql = "select count(ans.id) from " + Answer.class.getName() + " as ans where ans.deleted = false";
            query = new OqlQuery(hql).getQuery();

            int ai = ((Long)query.uniqueResult()).intValue();

        %><p><%=I18n.get("QA_ANSWER_TOTAL",ai+"")%></p>


    </div>

      <jsp:include page="../parts/partPromo.jsp"/>
      <jsp:include page="../parts/partShare.jsp"/>
      <jsp:include page="../parts/partBox1.jsp"/>
    <jsp:include page="../parts/partNewsletter.jsp"/>
    <jsp:include page="../parts/partBox2.jsp"/>
      <jsp:include page="partTopTags.jsp"/>


</div>


<%

  }
%>
