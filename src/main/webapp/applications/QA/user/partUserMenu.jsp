<%@ page import="com.QA.QAOperator, org.jblooming.utilities.JSP, org.jblooming.waf.constants.Commands, org.jblooming.waf.settings.I18n, org.jblooming.waf.view.PageSeed, org.jblooming.waf.view.PageState" %>
<%
  PageState pageState = PageState.getCurrentPageState();
  QAOperator logged = (QAOperator) pageState.getLoggedOperator();

  QAOperator user = null;
  String lname = pageState.getEntry("LNAME").stringValueNullIfEmpty();
  if (JSP.ex(lname))
    user = QAOperator.loadByLoginName(lname);
  else if (JSP.ex(pageState.mainObjectId))
    user = QAOperator.load(pageState.mainObjectId);
  else
    user = logged;

  PageSeed logout = pageState.pageFromRoot("site/access/login.jsp");
  logout.command = Commands.LOGOUT;

%> <div class="userActions">

    <div class="userImage"><div style="float:left; margin-right: 10px"><img src="<%=user.getGravatarUrl(100)%>" align="top" alt="<%=user.getDisplayName()%>"
                                                     title="<%=user.getDisplayName()%>" class="avatar">
    </div>
    <div class="userData">
        <h4><%=user.getDisplayName()%></h4><span class="level"><%=I18n.get("KARMA_LEVEL_" + user.getLevel())%></span><br>
        <span class="stats tip" style="font-size: 14px" title="<%=I18n.get("USER_REPUTATION")%>"><strong class="icon">*</strong><%=(int) (user.getKarma())%></span>
    </div><br style="clear: both;"></div>



<div class="userSettings">
  <%
    if (logged != null && logged.equals(user)) {

  %><a href="/applications/QA/user/messages.jsp" ><%=I18n.get("QA_MESSAGES")%></a> |


  <%
    }

    if (logged != null && !logged.equals(user)) {
     if (user.isMySubscriber(logged)) {
        %> <%=I18n.get("YOU_ARE_USER_SUBSCRIBED")%> <a style="cursor: pointer" class="buttonSmall black" style="margin-right: 15px" onclick="subscribeUnsubscribeUser()" id="QA_UNSUBSCRIBE_USER"><%=I18n.get("QA_UNSUBSCRIBE_USER")%></a> | <%
     } else {
        %> <button style="cursor: pointer" class="buttonSmall black" style="margin-right: 15px" onclick="subscribeUnsubscribeUser()" id="QA_SUBSCRIBE_USER"><%=I18n.get("QA_SUBSCRIBE_USER")%></button> | <%
     }
    }
  %>
  <a style="cursor: pointer" class="buttonSmall black" style="margin-right: 15px" onclick="self.location.href='/applications/QA/user/feed.jsp?OBJID=<%=user.getId()%>'">RSS feed</a><%
  if (logged != null && logged.equals(user)) {

%> | <a style="cursor: pointer" class="buttonSmall black" style="margin-right: 15px" onclick="self.location.href='/applications/QA/user/user.jsp'"><%=I18n.get("QA_MODIFY")%> </a> |
  <a style="cursor: pointer" class="buttonSmall black" style="margin-right: 15px" onclick="self.location.href='/applications/QA/user/subscriptions.jsp'"><%=I18n.get("SUBSCRIPTIONS")%> </a> |

  <%

    if (!JSP.ex(logged.getEmail())) {
  %><a  href="/applications/QA/user/user.jsp" title="<%=I18n.get("QA_USER_SETEMAIL")%>"><%=I18n.get("QA_USER_SETEMAIL")%></a> | <%
    }

%><a style="cursor: pointer" class="buttonSmall textual" onclick="$(this).confirm(function(){self.location.href='<%=logout.toLinkToHref()%>'},'<%=I18n.g("QA_LOGOUT_SURE")%>')"><%=I18n.g("QA_LOGOUT")%></a><%
  }%></div>
  </div>
<script>

  function subscribeUnsubscribeUser() {

      var el=this;

      var data = {};
      data.CM = 'SUBSCRIBE_USER';
      data.USERID = '<%=user.getId()%>';
      var ajaxController = "/applications/QA/ajax/ajaxUserController.jsp";

    $.ajax({
        url: ajaxController,
        data: data,
        dataType: 'json',
        success:  function(response) {

          if (response.ok) {
            window.location.reload();
          }

        }, error: function(er){
          showFeedbackMessage("ERROR", er.statusText);
        }
      });

    }
</script>