<%@ page import="com.QA.QAOperator,
                 com.QA.Question,
                 com.QA.businessLogic.QATalkAction,
                 org.jblooming.agenda.CompanyCalendar,
                 org.jblooming.tracer.Tracer,
                 org.jblooming.waf.SessionState,
                 org.jblooming.waf.settings.ApplicationState,
                 org.jblooming.waf.settings.I18n,
                 org.jblooming.waf.view.PageState,
                 java.io.IOException, java.text.SimpleDateFormat, java.util.Locale" %><%@ page pageEncoding="UTF-8" %><%!

  public static void channelBuilderStart(JspWriter out, String title, String url) throws IOException {
    out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
    out.write("<rss version=\"2.0\" xmlns:media=\"http://search.yahoo.com/mrss/\"  >\n");


    out.write("<channel>\n");

    out.write("<title><![CDATA[" + title + "]]></title>\n");
    //out.write("<description><![CDATA[" + feed.getDescription() + "]]></description>\n");
    out.write("<link>" + url + "</link>\n");
    out.write("<language><![CDATA[en]]></language>\n");

    String copy = "(C) "+ I18n.g("QA_APP_NAME");
    out.write("<copyright><![CDATA[" + copy + "]]></copyright>\n");

    String date = CompanyCalendar.getInstance().getTime().toString();
    out.write("<pubDate>" + date + "</pubDate>\n");
    out.write("<lastBuildDate>" + date + "</lastBuildDate>\n");

    out.write("<image>\n");
    out.write("<url>"+ApplicationState.serverURL+"/applications/QA/images/logo-onwhite.png</url>\n");
    out.write("<title>"+I18n.g("QA_APP_NAME")+" RSS</title>\n");
    out.write("<link>"+ApplicationState.serverURL+"</link>\n");
    out.write("</image>\n");
  }




  private void createItemDomNewsQuestion(Question question, PageState pageState, JspWriter out) {
    try {

      SimpleDateFormat formatDate = new SimpleDateFormat("EEE, d MMM yyyy hh:mm:ss Z", Locale.ENGLISH);

      out.write("<item>\n");

      out.write("<title><![CDATA[" + question.getSubject() + "]]></title>\n");

      out.write("<link>" +ApplicationState.serverURL +  question.getURL().toLinkToHref() + "</link>\n");
      out.write("<description><![CDATA[" + question.getDescription() + "]]></description>\n");
      out.write("<author><![CDATA[" + question.getOwner().getDisplayName() + "]]></author>\n");
      out.write("<guid isPermaLink=\"false\">" + question.getIntId() + "</guid>\n");
      out.write("<pubDate>" + formatDate.format(question.getCreationDate()) + "</pubDate>\n");


      out.write("</item>\n");

    } catch (Throwable e) {
      Tracer.platformLogger.error("rssGenerator.jsp:: " + e);
    }
  }


  public static void channelBuilderEnd(JspWriter out) throws IOException {
    out.write("</channel>\n");
    out.write("</rss>");
  }
%><% // ------------------------------------------------------ REAL PAGE STARTS HERE --------------------------------------------------------------------------------------

  PageState pageState = PageState.getCurrentPageState();
  SessionState sessionState = pageState.getSessionState();
  QAOperator user = QAOperator.load(pageState.mainObjectId);

  // un feed user-defined
  if (!sessionState.isExplorer()) {
    response.setContentType("application/xhtml+xml");
    response.setCharacterEncoding("UTF-8");
    response.setHeader("content-disposition", "inline");
  }

  channelBuilderStart(out, I18n.g("QA_APP_NAME")+" "+user.getDisplayName()+" RSS", "http://meltaplot.rai.it");





  QATalkAction.SearchResults searchResults = QATalkAction.findHotQuestions(10, null);

  for (QATalkAction.SearchResult sr : searchResults.searchResults) {

    createItemDomNewsQuestion(sr.reference, pageState, out);
  }



  channelBuilderEnd(out);

%>