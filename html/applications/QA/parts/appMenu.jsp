<%@ page
        import="com.QA.QAOperator,org.jblooming.waf.SessionState,org.jblooming.waf.constants.Commands,org.jblooming.waf.html.layout.Skin, org.jblooming.waf.settings.I18n, org.jblooming.waf.view.PageSeed, org.jblooming.waf.view.PageState, org.jblooming.utilities.JSP" %>
<%@ page buffer="16kb" %>
<%

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
<div id="appTopNav" class="topMenu full">
    <h1 onclick="getHome()" title="">
        <span id="claim"><%=I18n.g("QA_PITCH")%></span>
    </h1>

    <a href="/index.jsp"><%=I18n.get("QA_HOME")%>
    </a>

    <a href="<%=I18n.get("QA_REFERENCE_SITE")%>"><%=I18n.get("QA_REFERENCE")%>
    </a>

    <a href="/applications/QA/talk/users.jsp"><%=I18n.get("QA_USERS")%>
    </a>


    <a href="/applications/QA/talk/index.jsp?WHAT=TAG&TAG=meta">meta</a>


    <a href="/applications/QA/site/contacts.jsp"><%=I18n.get("QA_CONTACTS")%>
    </a>

    <%
        if (logged == null) {

            PageSeed login = pageState.pageFromRoot("site/access/login.jsp");

    %><a href="<%=login.toLinkToHref()%>">log in</a><%
    }


    if (logged != null && logged.hasPermissionAsAdmin()) {

        PageSeed adm = pageState.pageFromRoot("administration/QAAdminPanel.jsp");

%><a href="<%=adm.toLinkToHref()%>"><span class="icon" style="font-size: 16px">g</span></a><%
    }%>
    <div class="loggedUser">
        <div>

            <% if (logged != null) {

                PageSeed profile = new PageSeed(logged.getPublicProfileURL());
                profile.disableCache = false;
            %>
            <div class="loggedUserContent" id="USER_LINKS">
                <a href="<%=logged.getPublicProfileURL()%>" title="<%=I18n.get("QA_USER_SETEMAIL")%>">
                    <span><%=logged.getDisplayName()%></span><span class="reputation" title="<%=I18n.get("REPUTATION")%>"><span class="icon">*</span><%=(int) (logged.getKarma())%></span>
                    <img src="<%=logged.getGravatarUrl(100)%>" width="20" align="absmiddle" class="avatar tip"
                         title="<%=I18n.g("USER_PUBLIC_PAGE")%>">
                </a>

            </div>
            <% }
            %>
        </div>
    </div>
    <%
        if (pageState.getAttribute("DONT_DRAW_SEARCH") == null) {%>
    <form action="/applications/QA/site/search.jsp" method="POST" enctype="application/x-www-form-urlencoded"
          id="formSearch">
        <div style="position: relative;"><input type="hidden" name="CM" value="<%=Commands.FIND%>">
        <input type="text" size="20" id="searchStrip" name="FILTER" class="searchField" value=""
               onkeypress="if (event.keyCode==13){$(this).closest('form').submit();}">
        <div class="icon"></div>
        </div>
    </form>
    <%}%>

    <% if (logged != null) {
        long messTot = logged.getUnreadMessagesTotal();
    %>
    <div class="notifyMessages">
        <%
            //unread messages

            if (messTot > 0) {
                PageSeed mess = pageState.pageFromRoot("user/messages.jsp");
        %>
        <a class="warning tip" onclick="event.stopPropagation();"
           title="<%=messTot == 1 ? I18n.get("QA_MESSAGE_ONE") : I18n.get("QA_MESSAGES_%%", messTot + "")%>"
           href="<%=mess.toLinkToHref()%>"><span><%=messTot == 1 ? "1" : I18n.get(messTot + "")%></span><%=messTot == 1 ? I18n.get("QA_MESSAGE_ONE") : I18n.get("QA_MESSAGES_%%", messTot + "")%>
        </a><%
        }
    %>
    </div><% }%>

</div>


<div id="appTopNav" class="topMenu mini">
    <%--<h1 onclick="getHome()" title="">
        <span id=""><%=I18n.g("QA_PITCH")%></span>
    </h1>--%>


        <div class="loggedUser">


            <%
                if (logged == null) {

                    PageSeed login = pageState.pageFromRoot("site/access/login.jsp");

            %><a href="<%=login.toLinkToHref()%>"><span class="icon">b</span>&nbsp;log in</a><%
            }%>


                <% if (logged != null) {

                    PageSeed profile = new PageSeed(logged.getPublicProfileURL());
                    profile.disableCache = false;
                %>
                <div class="loggedUserContent" id="USER_LINKS">

                    <%
                        if (logged.hasPermissionAsAdmin()) {

                            PageSeed adm = pageState.pageFromRoot("administration/QAAdminPanel.jsp");

                    %><a href="<%=adm.toLinkToHref()%>"><span class="icon" style="font-size: 16px">g</span></a><%
                    }%>


                    <a href="/applications/QA/user/user.jsp" title="<%=I18n.get("QA_USER_SETEMAIL")%>">
                        <span><%=logged.getDisplayName()%></span><span class="reputation" title="<%=I18n.get("REPUTATION")%>"><span class="icon">*</span><%=(int) (logged.getKarma())%></span>

                    </a>

                    <%
                        //unread messages
                        long messTot = logged.getUnreadMessagesTotal();

                        if (messTot > 0) {
                            PageSeed mess = pageState.pageFromRoot("user/messages.jsp");
                    %>
                    <a class="warning tip" onclick="event.stopPropagation();"
                       title="<%=messTot == 1 ? I18n.get("QA_MESSAGE_ONE") : I18n.get("QA_MESSAGES_%%", messTot + "")%>"
                       href="<%=mess.toLinkToHref()%>"><span><%=messTot == 1 ? "1" : I18n.get(messTot + "")%></span><em><%=messTot == 1 ? I18n.get("QA_MESSAGE_ONE") : I18n.get("QA_MESSAGES_%%", messTot + "")%></em>
                    </a><%
                    }
                %>

                </div>

                <% }
                %>



        </div>

        <%
            if (pageState.getAttribute("DONT_DRAW_SEARCH") == null) {%>
        <div style="width: 100%"><form action="/applications/QA/site/search.jsp" method="POST" enctype="application/x-www-form-urlencoded"
              id="formSearch">
            <div style="position: relative;"><input type="hidden" name="CM" value="<%=Commands.FIND%>">
                <input type="text" size="20" id="searchStrip" name="FILTER" class="searchField" value=""
                       onkeypress="if (event.keyCode==13){$(this).closest('form').submit();}">
                <div class="icon"></div>
            </div>
        </form></div>
        <%}%>

        <div class="compactMenu"><select onchange="self.location.href=$(this).val()" name="sections" id="subsection-select">
            <option value="menu" selected="selected">- menu -</option>

                <option value="/index.jsp"><%=I18n.get("QA_HOME")%></option>
                <option value="<%=I18n.get("QA_REFERENCE_SITE")%>"><%=I18n.get("QA_REFERENCE")%></option>
                <option value="/applications/QA/talk/users.jsp"><%=I18n.get("QA_USERS")%></option>
                <option value="/applications/QA/talk/index.jsp?WHAT=TAG&TAG=meta">meta</option>
                <option value="/applications/QA/site/contacts.jsp"><%=I18n.get("QA_CONTACTS")%></option>



        </select>
        </div>






</div>


