<%@ page import="com.QA.QAOperator, com.QA.businessLogic.QATalkAction, com.QA.waf.QAScreenApp, com.QA.waf.QuestionDrawer, com.QA.waf.UserDrawer,
org.jblooming.waf.ScreenArea, org.jblooming.waf.constants.Commands, org.jblooming.waf.html.button.ButtonSubmit, org.jblooming.waf.html.input.TextField,
org.jblooming.waf.html.state.Form, org.jblooming.waf.settings.I18n, org.jblooming.waf.view.PageSeed, org.jblooming.waf.view.PageState" %><%@ page pageEncoding="UTF-8" %><%

  PageState pageState = PageState.getCurrentPageState();
  pageState.setAttribute("DONT_DRAW_SEARCH",true);


  if (!pageState.screenRunning) {
    pageState.screenRunning = true;
    final ScreenArea body = new ScreenArea(request);
    QAScreenApp lw = new QAScreenApp(body);
    pageState.getHeaderFooter().toolTip = I18n.g("QA_APP_NAME")+ " - " + I18n.g("QA_SEARCH");
    lw.register(pageState);
    pageState.perform(request, response);

    pageState.toHtml(pageContext);

  } else {

    %>

<jsp:include page="../parts/partCommunityMenu.jsp">
    <jsp:param name="SHOW_ADD" value="no"></jsp:param><jsp:param name="SHOW_BACK" value="yes"></jsp:param>
</jsp:include>
<div id="content">

  <style type="text/css">
  .high {
    background-color: #ff2;


  }
  .topMenu.mini .compactMenu {
      margin-top: 55px
  }
</style>
<script>
  jQuery.fn.highlight = function (str, className) {
    var regex = new RegExp(str, "gi");
    return this.each(function () {
      $(this).contents().filter(function() {
        return this.nodeType == 3 && regex.test(this.nodeValue);
      }).replaceWith(function() {
                return (this.nodeValue || "").replace(regex, function(match) {
                  return "<span class=\"" + className + "\">" + match + "</span>";
                });
              });
    });
  };
</script>

<h2><span><%=I18n.g("QA_SEARCH")%></span></h2>

    <div class="blockTitle" style="padding-top: 25px"><%

  Form f = new Form(pageState.thisPage(request));
  f.start(pageContext);

  TextField tf = new TextField("FILTER","&nbsp;");
  tf.label = "";
  tf.fieldSize=55;
  tf.toHtml(pageContext);


  ButtonSubmit bs = new ButtonSubmit("QA_DO_SEARCH",Commands.FIND,f);
  bs.additionalCssClass="search";
  bs.toHtmlI18n(pageContext);
%></div><%

  int pageSize = pageState.getEntry("PAGESIZE").intValueNoErrorCodeNoExc();
  if (pageSize==0)
    pageSize = 10;
  else if (pageSize>100)
    pageSize = 100;


  String filter = pageState.getEntry("FILTER").stringValueNullIfEmpty();


    /**
     * QUESTIONS
     */
%><%--<h1 style="text-align: center"><span><%=I18n.g("SEARCH_QA")%></span></h1>--%>

<%

  //todo: maxresults fused from the 3 searches

  QATalkAction.SearchResults srs = QATalkAction.search(pageSize, filter);
  int shown = 0;
  QATalkAction.SearchResultType previousType = null;
  for (QATalkAction.SearchResult sr: srs.searchResults) {

    if (!sr.type.equals(previousType))   {

    %><h3 style="width:100%;"><span><%=I18n.g(sr.type+"_RESULT")%></span></h3>


   <%
     }


    if (sr.type.equals(QATalkAction.SearchResultType.USER)) {
      %><br><%
      UserDrawer ud = new UserDrawer(QAOperator.load(sr.abstractz),true,40);
      ud.toHtml(pageContext);
%><br><%

    } else {

      %><%--p style="font-family: courier;" class="abstract">
  <%=sr.abstractz%>
</p--%><%
    QuestionDrawer brickDrawer = new QuestionDrawer(sr.reference);
    brickDrawer.toHtmlCompact(pageContext);
    }

    shown++;
    if (shown>=pageSize)
      break;

   previousType =  sr.type;

  }



     if (srs.searchResults==null || srs.searchResults.size()==0){
   %><h1 align="center" margin-top:40px><%=I18n.g("NO_Q&A_FOUND")%></h1><%
  }






  if (srs.hasMore) {
    PageSeed search = pageState.thisPage(request);
    search.addClientEntry(pageState.getEntry("FILTER"));
    search.command = Commands.FIND;
    search.addClientEntry("PAGESIZE",pageSize+10);

%><div class="moreEntry"><a class="button" href="<%=search.toLinkToHref()%>#MORE_RESULTS" name="MORE_RESULTS" title="<%=I18n.g("QA_MORE_RESULTS")%>"><%=I18n.g("QA_MORE_RESULTS")%></a></div> <%
    }

    %><script>
  $(function() {
    var closest = $(".abstract");
    closest.each(function(){
      $(this).highlight("<%=filter%>","high");
    });
  });
</script>
<%


    f.end(pageContext);

%></div> <%

}

%>
