<%@ page import="com.QA.QAOperator,org.jblooming.oql.OqlQuery,org.jblooming.waf.SessionState,org.jblooming.waf.html.layout.Skin, org.jblooming.waf.settings.I18n, org.jblooming.waf.view.PageSeed, org.jblooming.waf.view.PageState, java.util.List" %>
<%@ page import="org.jblooming.utilities.JSP" %>
<%@ page buffer="16kb" %><%

  PageState pageState = PageState.getCurrentPageState();
  SessionState sessionState = pageState.sessionState;
  Skin skin = sessionState.getSkin();
  QAOperator logged = (QAOperator) pageState.getLoggedOperator();


  PageSeed homePs;
  if (logged == null)
    homePs = pageState.pageFromRoot("index.jsp");
  else
    homePs = pageState.pageFromRoot("talk/index.jsp");

//  PageSeed login = pageState.pageFromRoot("site/access/login.jsp");
//  PageSeed enroll = pageState.pageFromRoot("site/access/enrollMail.jsp");
//  ButtonLink home = new ButtonLink(homePs);



%>
<%--<script type="text/javascript">

  $(function () {
    $("#mbCarousel").disclose({autoPlay:true, slideInterval:4000, autoRestart:8000,inTimer:600,outTimer:1200});
  });

</script>--%>


  <div id="carouselWrapper" style="width: 940px; margin: 0 auto 0; padding-right: 110px; position: relative;">
    <div id="slideIndex" style="position: absolute; top:260px; left: 0; z-index: 10"></div>

    <img src="/applications/QA/images/logo.png" style="position: absolute; top:50px; left: 50px; z-index: 10">
    <img src="/applications/QA/images/logo.png" onmouseover="$('#mbCarousel').disclose('pause')" onmouseout="$('#mbCarousel').disclose('restore')" style="position: absolute; z-index: 100; top:0; right: 30px">

      <%String yti = I18n.g("YOUTUBE_INTRO");
      if (JSP.ex(yti) && !"YOUTUBE_INTRO".equals(yti)) {%>
      <a class="playVideo tip" title="<%=I18n.g("WATCH_VIDEO")%>" onclick="showVideo(false)">&nbsp;</a>
      <%}%>


    <!--  START CAROUSEL -->
    <div id="mbCarousel" style="position:relative; height: 335px; overflow: hidden; width: 100%">

      <div id="banner1" class="banner"
           data-animationin='slideLeft'
           data-animationout='slideLeft'>
        <img src="/applications/QA/images/ph_01_01.png"
             data-animate=false>
      </div>

      <div id="banner2" class="banner"
           data-animationin='slideLeft'
           data-animationout='slideLeft'>
        <img src="/applications/QA/images/ph_01_02.png"
             data-animate=false>
      </div>

      <div id="banner3" class="banner"
           data-animationin='slideLeft'
           data-animationout='slideLeft'>
        <img src="/applications/QA/images/ph_01_03.png"
             data-animate=false>
      </div>

      <div id="banner4" class="banner"
           data-animationin='slideLeft'
           data-animationout='slideLeft'
           data-time=8000>
        <img src="/applications/QA/images/ph_01_04.png"
             data-animate=false>
      </div>

    </div>
  </div>
  <!--  END CAROUSEL -->

<%--<script>function showVideo(close){
  if(!close){

    var video= $("<iframe/>").css({width:"100%", height:"100%"}).attr("src","http://www.youtube.com/embed/Md-ER7nHcxg?rel=0&amp;fmt=22&amp;showinfo=0&amp;controls=1&amp;autoplay=1&color1=0xffffff&color2=0xffffff");

    $("#mainVideo").append(video);
    overlay("#mainVideo", true, function(){showVideo(true)});
  }else{
    $("#mainVideo").find("iframe").remove();
  }
}</script>--%>
