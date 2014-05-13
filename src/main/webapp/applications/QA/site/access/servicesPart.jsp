<%@ page import="org.jblooming.waf.settings.ApplicationState, org.jblooming.waf.settings.I18n, org.jblooming.waf.view.PageSeed, org.jblooming.waf.view.PageState" %><%

  PageState pageState = PageState.getCurrentPageState();

  PageSeed ps = pageState.pageFromRoot("/site/access/parts/redirToGoogle.jsp");
  ps.addClientEntry("OPENID", "https://www.google.com/accounts/o8/id");
  boolean showLogin = !"no".equals(request.getParameter("showLogin"));

    %><a href="#"><img src="<%=ApplicationState.contextPath + "/applications/QA/images/twitterMine.png"%>" class="oauth" alt="<%=I18n.g("ENROLL_USING_TWITTER")%>" onclick="callTwitter()" style="border-left: none"></a>
    <a href="#"><img src="<%=ApplicationState.contextPath + "/applications/QA/images/facebookMineSmall.png"%>" class="oauth facebook" alt="<%=I18n.g("ENROLL_USING_FACEBOOK")%>" onclick="callFacebook()"></a>
    <a href="<%=ps.toLinkToHref()%>"><img src="<%=ApplicationState.contextPath + "/applications/QA/images/googleMineSmall.png"%>" class="oauth" alt="<%=I18n.g("ENROLL_USING_GOOGLE")%>" ></a>

