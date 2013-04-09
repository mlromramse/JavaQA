<%@ page contentType="text/html; charset=utf-8" pageEncoding="UTF-8"%><%@ page buffer="16kb" %><%@ page import="org.jblooming.agenda.CompanyCalendar,
                 org.jblooming.scheduler.Scheduler,
                 org.jblooming.security.PlatformPermissions,
                 org.jblooming.system.SystemConstants,
                 org.jblooming.tracer.Tracer,
                 org.jblooming.utilities.HTMLEncoderOld,
                 org.jblooming.utilities.JSP,
                 org.jblooming.utilities.StringUtilities,
                 org.jblooming.waf.SessionState,
                 org.jblooming.waf.configuration.LoaderSupport,
                 org.jblooming.waf.constants.Commands,
                 org.jblooming.waf.constants.Fields,
                 org.jblooming.waf.constants.OperatorConstants,
                 org.jblooming.waf.constants.SettingsConstants,
                 org.jblooming.waf.html.core.*,
                 org.jblooming.waf.html.display.FeedbackFromController,
                 org.jblooming.waf.html.display.HeaderFooter,
                 org.jblooming.waf.html.display.Img,
                 org.jblooming.waf.html.layout.Skin, org.jblooming.waf.settings.ApplicationState, org.jblooming.waf.settings.I18n, org.jblooming.waf.settings.PersistenceConfiguration,
                 org.jblooming.waf.view.PageSeed, org.jblooming.waf.view.PageState, java.util.Iterator, org.jblooming.operator.Operator" %><%
    HeaderFooter headerFooter = (HeaderFooter) JspIncluderSupport.getCurrentInstance(request);

    PageState pageState = PageState.getCurrentPageState();

    SessionState sessionState = pageState.sessionState;
    Skin skin = sessionState.getSkin();

    if (skin == null) {                                                                                                                              // TODO check this V
        sessionState.setSkinForApplicationIfNull(request.getContextPath(), ApplicationState.getApplicationSetting(OperatorConstants.FLD_CURRENT_SKIN), "applications/blooming");
        skin = sessionState.getSkin();
    }
/*
________________________________________________________________________________________________________________________________________________________________________


  HEADER

________________________________________________________________________________________________________________________________________________________________________

*/
    if (Fields.TRUE.equals(request.getAttribute(HeaderFooter.HEADER))) {
        if (!skin.colorsLoaded) {
            request.setAttribute(JspHelper.ACTION, Skin.LOAD_COLORS);
            pageContext.include("/commons/skin/" + skin.name + "/platformCss.jsp");
        }

        if (headerFooter.toolTip == null)
            headerFooter.toolTip = I18n.get(pageState.getApplication().getName());
%><!DOCTYPE HTML>
<html>
<head>
  <script type="text/javascript">
        var serverURL = "<%=ApplicationState.serverURL%>";
        var contextPath = "<%=ApplicationState.contextPath%>";
        var skinImgPath = "<%=pageState.getSkin().imgPath%>";
        var imgCommonPath = "<%=pageState.getSkin().imgPath%>";
        var buildNumber="<%=ApplicationState.getBuild()%>";
    </script>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
  <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />
  <%--<meta name="viewport" content="width=device-width, initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0"/>
  <meta name="apple-mobile-web-app-capable" content="yes"/>
  <meta name="apple-mobile-web-app-status-bar-style" content="black"/>
  <link id="appIcon" rel="apple-touch-icon-precomposed" href="/applications/QA/images/apple-touch-icon.png"/>--%>
  <link rel="Shortcut Icon" type="image/ico" href="<%=skin!=null ? skin.imgPathPlus : request.getContextPath()+"/"%>favicon.ico">
  <title><%=headerFooter.toolTip%></title>
    <%=headerFooter.meta != null ? headerFooter.meta : ""%>
    <link rel=stylesheet href="<%=skin.css%>platformCss.jsp?_=<%=ApplicationState.getBuild()%>" type="text/css" media="screen">
    <%--<link rel=stylesheet href="<%=request.getContextPath()+"/commons/skin/print/platformCss.jsp"%>?_=<%=ApplicationState.getBuild()%>" type="text/css" media="print">--%>
    <script src="<%=request.getContextPath()+"/commons/js/jquery/jquery-1.7.min.js"%>?_=<%=ApplicationState.getBuild()%>"></script>

    <script src="<%=request.getContextPath()+"/commons/js/jquery/ui/jquery-ui.min.js"%>?_=<%=ApplicationState.getBuild()%>"></script>
    <script src="<%=request.getContextPath()+"/commons/js/jquery/jquery.livequery.min.js"%>?_=<%=ApplicationState.getBuild()%>"></script>
    <script src="<%=request.getContextPath()+"/commons/js/jquery/jquery.timers.js"%>?_=<%=ApplicationState.getBuild()%>"></script>

  <script type="text/javascript" src="<%=request.getContextPath()%>/commons/layout/smartCombo/partSmartCombo.js"></script>

  <script type="text/javascript" src="<%=request.getContextPath()%>/commons/js/jquery/dateField/jquery.dateField.js"></script>
  <link rel=stylesheet href="<%=request.getContextPath()+"/commons/js/jquery/dateField/jquery.dateField.css"%>?<%=ApplicationState.getBuild()%>" type="text/css" media="screen">

  <script type="text/javascript" src="<%=request.getContextPath()+"/commons/js/jquery/JST/jquery.JST.js"%>?<%=ApplicationState.getBuild()%>"></script>
  <script src="<%=request.getContextPath()+"/commons/js/platform.js"%>?_=<%=ApplicationState.getBuild()%>"></script>
  <script src="<%=request.getContextPath()+"/commons/js/date.js"%>?_=<%=ApplicationState.getBuild()%>"></script>
  <script src="<%=request.getContextPath()+"/commons/js/i18nJs.jsp"%>?_=<%=ApplicationState.getBuild()%>"></script>

  <link id="meltaplotRss" rel="alternate" type="application/rss+xml" title="<%=I18n.g("QA_APP_NAME")%> RSS" href="/feed/meltaplotTop">
  <link rel=stylesheet href="/applications/QA/css/qaCss.jsp?_=<%=ApplicationState.getBuild()%>" type="text/css">
  <link rel=stylesheet href="/applications/QA/js/jquery.qtip.css" type="text/css">


  <script type="text/javascript" src="/applications/QA/js/mpJs.jsp?_=<%=ApplicationState.getBuild()%>"></script>
  <script type="text/javascript" src="/applications/QA/js/jquery.qtip.min.js?_=<%=ApplicationState.getBuild()%>"></script>
  <script type="text/javascript" src="/applications/QA/js/jquery.placeholder.min.js?_=<%=ApplicationState.getBuild()%>"></script>

  <link rel="stylesheet" type="text/css" href="/applications/QA/css/markdown.css"/>

  <script src="https://google-code-prettify.googlecode.com/svn/loader/run_prettify.js"></script>
  <script type="text/javascript" src="/applications/QA/js/Markdown.Converter.js"></script>
  <script type="text/javascript" src="/applications/QA/js/Markdown.Sanitizer.js"></script>
  <script type="text/javascript" src="/applications/QA/js/Markdown.Editor.js"></script>


    <script>
        $(function(){

            $("pre").addClass("prettyprint");
            $.getScript("https://google-code-prettify.googlecode.com/svn/loader/run_prettify.js");

        })
    </script>

  <%-- MP specific end --%>



  <%

%></head><%
    /*
    ________________________________________________________________________________________________________________________________________________________________________


      BODY

    ________________________________________________________________________________________________________________________________________________________________________

    */
    if (headerFooter.printBody) {
     %><body  <%
    final String jsbeString = Operator.getOperatorOption(pageState.getLoggedOperator(),OperatorConstants.FLD_LOGOUT_TIME);
    int logoutTime = 0;
    if (JSP.ex(jsbeString))
        logoutTime = Integer.parseInt(jsbeString);

    boolean handleLogout = sessionState != null && pageState.getLoggedOperator() != null && logoutTime > 0;
    if (handleLogout) {
      %><%="onMouseMove=\"minutesToLock=" + logoutTime + "\""%><%
    }

    //CLOSING BODY
    %>><%

    if (handleLogout) {
       //log out handling
      %><script >
          var minutesToLock = <%=logoutTime%>;
          var idTimer;
          function count() {
              if (minutesToLock > 0) {
                  minutesToLock--;
                  if (isNetscape)
                      document.title = "<%=headerFooter.toolTip%>" + " (" + minutesToLock + " <%=I18n.get("MIN_TO_LOGOUT")%>)";
                  else
                      window.status = " (" + minutesToLock + " <%=I18n.get("MIN_TO_LOGOUT")%>)";
              } else {
                  window.clearInterval(idTimer);
              <%if (pageState.isPopup()) {%>
                  muteAlertOnChange = true;
                  window.close();
              <%} else {
              PageSeed ps = PageSeed.getConfiguredInstance(SettingsConstants.ROOT_LOGIN);
              ps.setCommand(Commands.LOGOUT);
              %>
                  muteAlertOnChange = true;
                  window.location = '<%=HTMLEncoderOld.removeBadCharsFromJSConstant(ps.toLinkToHref()) %>';
              <%}%>
              }
          }
          idTimer = setInterval("count()", 60000);
      </script>
      <%
    }
    /*
    _______________________________________________________________________________________________________________________________________________________________________


        keep alive

    ________________________________________________________________________________________________________________________________________________________________________

    */
%>
<script >
    $("body").everyTime(600000,"pingServer",function(){
      <%if (request.getSession().getAttribute("CMD_LOG_OUT_PARAM_SESSION_KEY") != null) { %>
          $("body").stopTime("pingServer");
      <%}else{%>
          getContent('<%=ApplicationState.contextPath%>/command.jsp');
      <%}%>
    });
</script>
<%

    if (headerFooter.keepAsTop) {
      %><script>
          if (window != top)
              parent.location.href = this.location.href;
      </script><%
    }

/*
________________________________________________________________________________________________________________________________________________________________________


  FOOTER

________________________________________________________________________________________________________________________________________________________________________

*/
    }
} else {


    // first frull for HtmlFinalizer
    for (Iterator iterator = pageState.htmlBootstrappers.iterator(); iterator.hasNext();) {
        HtmlBootstrap htmlBootstrap = (HtmlBootstrap) iterator.next();
        if (htmlBootstrap instanceof HtmlFinalizer) {
            ((HtmlFinalizer) htmlBootstrap).finalize(pageContext);
        }
    }

    // second frull for bootstrap validate
    for (Iterator iterator = pageState.htmlBootstrappers.iterator(); iterator.hasNext();) {
        HtmlBootstrap htmlBootstrap = (HtmlBootstrap) iterator.next();
        if (!htmlBootstrap.validate(pageState))
            throw new NullPointerException("Invalid validate for object " + htmlBootstrap.getClass().getName() + " of id '" + htmlBootstrap.getDiscriminator() + "'");
    }

    if (pageState.isPopup()) {
      %><script type="text/javascript">window.focus();</script><%
    }

    String ADMIN_MESSAGE = ApplicationState.applicationSettings.get(SystemConstants.ADMIN_MESSAGE);
    if (ADMIN_MESSAGE != null && ADMIN_MESSAGE.trim().length() > 0 && pageState.getLoggedOperator() != null && pageState.getLoggedOperator().hasPermissionFor(PlatformPermissions.schedule_manage)) {
        if (Scheduler.isRunning()) {
            ApplicationState.applicationSettings.remove(SystemConstants.ADMIN_MESSAGE);
        } else {
          %>
          <hr>
          <p align="center"><big><font color="<%=pageState.sessionState.getSkin().COLOR_WARNING%>"><%=ADMIN_MESSAGE%>
          </font></big></p>
          <hr>
          <%
        }
    }
  %>
  <div id="_errorTemplates" style="display:none;">
  <%=JST.start("errorTemplate")%>
  <div style="padding:0" class="FFC_(#=type#) FFC_Global" type="(#=type#)">
    <table cellpadding="0" cellspacing="0" align="center" style="width:100%;padding: 5px;">
      <tr>
        <td valign="top" width="1"><%new Img(pageState.getSkin().imgPath +"(#=type.toLowerCase()#).png","").toHtml(pageContext);%></td>
        <td>(#if(obj.title){#)
          <b>(#=title#)</b><br>
          (#}#)(#=message#)<br></td>
        <td width="1" valign="top" onclick="$(this).parents('div:first').slideUp()"><%new Img(pageState.getSkin().imgPath +"close.png","").toHtml(pageContext);%></td>
      </tr>
    </table>
  </div>
  <%=JST.end()%>
  </div>

<script>
  /*$(window).resize(function(ev){
   if (ev.target==window)
    sendWindowResizeMessage(); //hack to avoid event bubbling when using ui.resizable
  });*/

  $("#_errorTemplates").loadTemplates().remove();

  _messagesFromController=<%=FeedbackFromController.getPageStateMessages(pageState)%>;
  var place=$("#__FEEDBACKMESSAGEPLACE");
  if (_messagesFromController.length>0){
    for (var i in _messagesFromController){
      place.append($.JST.createFromTemplate(_messagesFromController[i],"errorTemplate"));
    }
    place.fadeIn();
    $("body").oneTime(3000,"hideffc",function(){$(".FFC_<%=PageState.MessageType.OK%>").slideUp();});
  } else {
    place.hide().empty();    
  }

</script><%

    pageState.focusToHtml(pageContext);

/*
________________________________________________________________________________________________________________________________________________________________________


  htmlDebug START

________________________________________________________________________________________________________________________________________________________________________

*/

    if (ApplicationState.platformConfiguration.development && !headerFooter.isPartFooter) {

%>
<div class="noprint">    
    <small><br>&nbsp; Recording i18n on files. <%
        if (request.getAttribute("time") != null) {
    %>Server side generated in <%=System.currentTimeMillis() - ((Long) request.getAttribute("time"))%> ms.<%
        if (request.getAttribute("lapTime") != null) {
    %>&nbsp;of which <%=((Long) request.getAttribute("lapTime")) - ((Long) request.getAttribute("time"))%> ms. for
        controller.<%
            }
        }
    %> n. of elements <script type="text/javascript">document.write("div:"+$("div").length+" table:"+$("table").length+" td:"+$("td").length+" a:"+$("a").length+" input:"+$("input").length+" total:"+$("*").length); </script> 
    </small>
    <br></div>
<%
    }

/*
________________________________________________________________________________________________________________________________________________________________________


  htmlDebug END

________________________________________________________________________________________________________________________________________________________________________

*/

    synchronized (this) {
        if (!ApplicationState.platformConfiguration.loadedWithPageContextSettings) {
            LoaderSupport.withPageContextSetup(pageContext);
        }
    }

    if (I18n.EDIT_STATUS_EDIT.equals(I18n.getEditStatus()) &&
            pageState.getLoggedOperator() != null &&
            I18n.getI18nEditingOperatorId() == pageState.getLoggedOperator().getId()) {
        PageSeed edit = new PageSeed(ApplicationState.contextPath + "/commons/administration/i18nEdit.jsp");
        edit.addClientEntry(Fields.APPLICATION_NAME, pageState.getApplication().getName());
        edit.setCommand(Commands.EDIT);


%>
<script type="text/javascript">
    var i18nEditWin;
    $(function() {
        $("span[i18n]").css("border", "1px solid red").bind("click", function(e) {
            i18nEditWin = window.open("<%=edit.toLinkToHref()%>&<%=Fields.FORM_PREFIX%>code=" + $(this).attr("i18n"), 'i18nwindow', 'height=500,width=650,scrollbars=yes');
            i18nEditWin.focus();
        });
    });
</script>
<%
    }

  if (PersistenceConfiguration.getDefaultPersistenceConfiguration().useHibStats) {
    %><hr>&nbsp;&nbsp;&nbsp;<%=StringUtilities.replaceAllNoRegex(Tracer.traceHibernateEnd(),"\n","<br><br>&nbsp;&nbsp;&nbsp;")%><hr><%
  }

  if (!ApplicationState.platformConfiguration.development) {
    %><!-- START Nielsen Online SiteCensus V5.3 -->
<!-- COPYRIGHT 2009 Nielsen Online -->
<script type="text/javascript">
  var _rsCI="rainet-it";
  var _rsCG="0";
  var _rsDN="//secure-it.imrworldwide.com/";
  var _rsCC=0;
  var _rsIP=1;
  var _rsPLfl=0;
</script>
<script type="text/javascript" src="//secure-it.imrworldwide.com/v53.js"></script>
<noscript>
  <div><img src="//secure-it.imrworldwide.com/cgi-bin/m?ci=rainet-it&amp;cg=0" style="visibility:hidden;position:absolute;left:0px;top:0px;z-index:-1" alt=""/></div>
</noscript>
<!-- END Nielsen Online SiteCensus V5.3 --><%
  }
%>


</body></html><%
}
%>
