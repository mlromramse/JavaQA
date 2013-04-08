<%@ page import="com.QA.waf.QAScreenApp,
                 org.jblooming.waf.ScreenArea,
                 org.jblooming.waf.settings.I18n,
                 org.jblooming.waf.view.PageState" %>
<%@ page pageEncoding="UTF-8" %>
<%

  PageState pageState = PageState.getCurrentPageState();

  if (!pageState.screenRunning) {
    pageState.screenRunning = true;
    final ScreenArea body = new ScreenArea(request);
    QAScreenApp lw = new QAScreenApp(body);
    pageState.getHeaderFooter().toolTip = I18n.g("QA_APP_NAME") + " - " + I18n.g("QA_CONTACTS");
    lw.register(pageState);
    pageState.perform(request, response).toHtml(pageContext);

  } else {

%>  <div id="content">
<script type="text/javascript" src="https://www.google.com/jsapi"></script>
<script type="text/javascript">

  google.load("feeds", "1");

  function initialize() {
    var feed = new google.feeds.Feed("http://blog.meltaplot.com/feed/?a=2");
    feed.load(function (result) {
      if (!result.error) {
        //console.debug("result", result);
        var container = $("#feed");
        //var entry = result.feed.entries[0];


        for (var i = 0; i < result.feed.entries.length; i++) {
          var entry = result.feed.entries[i];
          //is it a news entry?
          var cats = entry.categories;
          var isNews = false;
          for (var ci in cats) {
            if (cats[ci] == "news") {
              isNews = true;
              break;
            }
          }
          if (isNews) {
            var div = $("<div/>");
            div.append(entry.title);
            div.append("<hr>");
            div.append(entry.content);
            container.append(div);
            break;
          }
        }
      }
    });
  }
  google.setOnLoadCallback(initialize);
</script>
<div id="feed"></div>
</div>

<%
  }
%>
