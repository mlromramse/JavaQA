<%@ page
        import="org.jblooming.waf.settings.ApplicationState, org.jblooming.waf.settings.I18n, org.jblooming.waf.view.PageState, java.util.Date" %>
<%@page pageEncoding="UTF-8" %>
<br clear="all">
<br><br><br>

<div>
<div id="qaFooter">

  <div class="footerWrapper">
    <div class="leftCol"><h2></h2>

    <ul>
      <li style="width: 180px"><img src="/applications/QA/images/logoGray.png" alt="" class="logoSmall"></li>


      <li>
        <ul>
            <li><a href="<%=I18n.get("QA_REFERENCE_SITE")%>"><%=I18n.get("QA_REFERENCE")%></a></li>
          <li><a href="/applications/QA/site/privacyAndTermsOfService.jsp" title="<%=I18n.g("QA_PRIVACY_TERMS_LINK")%>"><%=I18n.g("QA_PRIVACY_TERMS_LINK")%></a></li>
          <li><a href="/applications/QA/site/contacts.jsp"><%=I18n.get("QA_CONTACTS")%></a></li>

        </ul>
      </li>

    </ul>

    </div>
    <div class="rightCol">
        <a class="social" href="<%=I18n.g("QA_FACEBOOK_LINK")%>" target="_blank"><span class="icon">4</span></a>
        <a class="social" href="<%=I18n.g("QA_TWITTER_LINK")%>" target="_blank"><span class="icon">2</span></a>
        <a class="social" href="/feed/"><span class="icon">_</span></a>
        <br style="clear: left;">
      <a style="padding-top: 5px; display: block; font-size: 11px" href="<%=I18n.g("QA_TWITTER_TAG_LINK")%>" target="_blank" title="<%=I18n.g("QA_SUGGESTED_TAG")%>"><%=I18n.g("QA_SUGGESTED_TAG")%></a>


    </div>
    <br style="clear: both;"><br>
</div><div style="border-top: 1px solid rgba(0,0,0,0.15);"></div>
  <div class="bottomLinks">

    Q&A free and open source software provided by <a href="http://www.open-lab.com" target="_blank" title="Open lab HTML5 solutions">Open Lab</a> - complete sources on GitHub -
    <span><%=I18n.get("APP_VERSION")%>: <%=ApplicationState.getApplicationVersion()%> (build <%=ApplicationState.getBuild()%>)</span><br>
    </div>
</div>
</div>
