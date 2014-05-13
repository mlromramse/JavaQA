<%@ page contentType="text/html; charset=utf-8" pageEncoding="UTF-8" %><%@ page import="org.jblooming.utilities.JSP, org.jblooming.waf.html.display.HeaderFooter, org.jblooming.waf.settings.ApplicationState, org.jblooming.waf.settings.I18n, org.jblooming.waf.view.PageState, java.util.Date" %><%
  PageState pageState = PageState.getCurrentPageState();
  HeaderFooter hf = pageState.getHeaderFooter();
  if (!JSP.ex(hf.toolTip))
    hf.toolTip = I18n.g("QA_APP_NAME");
  if (!JSP.ex(hf.meta))
    hf.meta = "<meta name=\"title\" content=\""+hf.toolTip+"\">\n" +
      "      <meta name=\"keywords\" content=\"\">\n" +
      "      <meta name=\"AUTHOR\" content=\"Pupunzi\">\n" +
      "      <meta name=\"Copyright\" content=\"Copyright © 2012-"+(new Date().getYear()+1900)+", Matteo Bicocchi, Open Lab\">\n" +
      "      <meta name=\"owner\" content=\"Pupunzi (Matteo Bicocchi)\" >\n" +
      "      <meta name=\"robots\" content=\"ALL\">" +

      "<meta name=\"viewport\" content=\"user-scalable=no, width=device-width, initial-scale=1.0, maximum-scale=1.0\"/>\n" +
      "<meta name=\"apple-mobile-web-app-capable\" content=\"yes\" />\n" +
      "<meta property=\"og:image\" content=\""+ApplicationState.serverURL+"/applications/QA/images/logo.png\">";


  if (!ApplicationState.platformConfiguration.development) {
    String googleAnalytics =  "<script type=\"text/javascript\">\n" +
      "\n" +
      "  var _gaq = _gaq || [];\n" +
      "  _gaq.push(['_setAccount', '"+ I18n.g("QA_GOOGLE_CODE")+ "']);\n" +
      "  _gaq.push(['_trackPageview']);\n" +
      "\n" +
      "  (function() {\n" +
      "    var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;\n" +
      "    ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';\n" +
      "    var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);\n" +
      "  })();\n" +
      "\n" +
      "</script>";

    hf.meta = hf.meta + googleAnalytics;
  }
%>
