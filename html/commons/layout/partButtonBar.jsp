<%@ page import="org.jblooming.ontology.LoggableIdentifiableSupport,
                 org.jblooming.persistence.PersistenceHome,
                 org.jblooming.security.Area,
                 org.jblooming.security.SecurableWithArea,
                 org.jblooming.utilities.DateUtilities,
                 org.jblooming.utilities.JSP,
                 org.jblooming.waf.html.container.ButtonBar,
                 org.jblooming.waf.html.core.JspIncluder,
                 org.jblooming.waf.html.core.JspIncluderSupport,
                 org.jblooming.waf.html.layout.Skin,
                 org.jblooming.waf.view.PageState, org.jblooming.waf.settings.I18n, org.jblooming.waf.html.container.ButtonBox"%><%

  ButtonBar buttonBar = (ButtonBar) JspIncluderSupport.getCurrentInstance(request);
  PageState pageState = PageState.getCurrentPageState();
  Skin skin = pageState.sessionState.getSkin();

%><div class="noprint <%=buttonBar.buttonAreaHtmlClass%>" id="<%=buttonBar.id%>" style="text-align:<%=buttonBar.align%>;" ><%
    if (buttonBar instanceof ButtonBox){
      ButtonBox bb= (ButtonBox) buttonBar;
      if (JSP.ex(bb.title)){
        %><h2><%=bb.title%></h2><%
      }
    }



    if (buttonBar.buttonList.size() > 0) {
      %><div class="bbButtons"><%
      for (JspIncluder button : buttonBar.buttonList) {
        button.toHtml(pageContext);
      }
      %></div><%
    }

  LoggableIdentifiableSupport it = buttonBar.loggableIdentifiableSupport;

    if (it != null) {
      %><div class="bbLoggedInfo"><%
      if (!it.isNew()) {
        %><b><%=I18n.get("CREATED_BY")%></b>&nbsp;<%=it.getCreator()!=null ? it.getCreator() : "-"%>&nbsp;
        <b><%=I18n.get("ON_DATE")%></b>&nbsp;<%=DateUtilities.dateAndHourToString(it.getCreationDate())%><%

        if (it instanceof SecurableWithArea) {
          Area area = ((SecurableWithArea)it).getArea();
          %>&nbsp;&nbsp;&nbsp;<b><%=I18n.get("AREA")%></b>&nbsp;
          <%=JSP.w(area!=null ? area.getName() :"<font color=\""+ skin.COLOR_WARNING+"\">"+I18n.get("NO_AREA")+"</font>")%>&nbsp;&nbsp;<%
        }
        %>&nbsp;<b><%=I18n.get("LAST_MODIFIED_BY")%></b>&nbsp;<%=it.getLastModifier()!=null ? it.getLastModifier() : "-"%>&nbsp;
        <b><%=I18n.get("ON_DATE")%></b>&nbsp;<%=DateUtilities.dateAndHourToString(it.getLastModified())%><%
        if (PersistenceHome.NEW_EMPTY_ID.equals(pageState.mainObjectId) && !it.isNew()) {
          %>&nbsp;<font color="<%=skin.COLOR_FEEDBACK%>"><%=I18n.get("NEW_OBJECT_INSERTED_IN_DB",I18n.get(it.getClass().getSimpleName().toUpperCase()))%></font><%
        }

    } else {
      %><font color="<%=skin.COLOR_FEEDBACK%>"><%=I18n.get("CREATING_NEW_OBJECT",I18n.get(it.getClass().getSimpleName().toUpperCase()))%></font><%
    }
    %></div><%
    }

%></div>
