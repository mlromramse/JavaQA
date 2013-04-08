<%@ page
        import="com.QA.waf.QAScreenApp, org.jblooming.waf.ScreenArea, org.jblooming.waf.settings.I18n, org.jblooming.waf.view.PageState" %>
<%@ page pageEncoding="UTF-8" %>
<%

  PageState pageState = PageState.getCurrentPageState();
  if (!pageState.screenRunning) {
    pageState.screenRunning = true;
    final ScreenArea body = new ScreenArea(request);
    QAScreenApp lw = new QAScreenApp(body);
    pageState.getHeaderFooter().toolTip = I18n.g("QA_APP_NAME") + " - " + I18n.g("QA_HOWITWORKS");
    lw.register(pageState);
    pageState.perform(request, response).toHtml(pageContext);

  } else {

%>
<style>

  p.keywords {

    padding: 35px;
    font: 18px/25px "BitterRegular",Courier,serif!important;

    background-color: #fefbd4;
    color: #88735c;
  }

  p.keywords > span {
    font-size: 30px;
  }

</style>
<div id="content">


<h2><span><%=I18n.g("QA_HOWDOESITWORK_TITLE")%></span></h2>

<div class="contentBox" style="padding: 10px">
<div>

<%--<iframe frameborder="0" allowfullscreen style="width: 570px; height: 335px "
        src="http://www.youtube.com/embed/Gph_Ha--IlY?rel=0&fmt=22&showinfo=0&controls=0&rel=0&hd=1&autoplay=0&color1=0xffffff&color2=0xffffff&wmode=transparent"></iframe>--%>



  <p class="keywords" align="center" style="padding: 20px 0 0 0">
    <img src="/applications/QA/images/logo.png" alt="<%=I18n.g("QA_APP_NAME")%> card" align="middle"></p>

 

    <%--
    boolean more = Fields.TRUE.equals(pageState.getEntry("MORE").stringValue());
    if (more) {
        %><hr>MORE MORE MORE<hr><%
      }
    --%>

    <%--
      if (!more) {

      PageSeed ps = pageState.thisPage(request);
      ps.addClientEntry("MORE", Fields.TRUE);
    %>

    <br><br><a href="<%=ps.toLinkToHref()%>"><%=I18n.g("MORE_EXPLAIN")%></a><br><br>
    <%
      }
    --%>
 
  </div>


<%--=I18n.g("QA_HOWDOESITWORK_BODY_1")%>
</p>
<a href="/applications/QA/write/index.jsp"><%=I18n.g("QA_PLAY_NOW")%>.</a>
<p>
  <%=I18n.g("QA_HOWDOESITWORK_BODY_2")--%>


</div></div>


</div><%
  }
%>
