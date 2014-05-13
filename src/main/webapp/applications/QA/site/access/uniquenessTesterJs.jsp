<%@ page import="org.jblooming.waf.view.PageState" %><%

  PageState pageState= PageState.getCurrentPageState();
%>
  <script type="text/javascript">

  function checkUniqueAccountAndChars(el, ev) {
    el.stopTime("checkUniqueAccount");
    if (!el.val().match(/^[a-zA-Z0-9\-]*$/)){
      $("#useValidChars").slideDown();
      el.createErrorAlert("INVALID_CHARS","");
      $("#freeAccount").hide();
    } else {
      $("#useValidChars").hide();
      el.clearErrorAlert();

      el.oneTime(1000, "checkUniqueAccount", function() {
        var req = {
          "CM":"CKUNIQUN",
          "__tk":"<%=pageState.tokenCreate("tkunqun")%>",
          un:el.val() };
        $.getJSON('/applications/QA/site/access/accessAjaxController.jsp', req, function(response) {
          if (response.ok) {
            if (!response.unique) {
              el.createErrorAlert("NOT_UNIQUE","");
              $("#freeAccount").hide();

              $("#alreadExstUN .alreadExst").hide();

              if (response.hasValidEmail)
                $("#alreadExstUN .validEmail").show();
              else if(response.hasUnverifiedEmail)
                $("#alreadExstUN .unverifiedEmail").show();
              else
                $("#alreadExstUN .noEmail").show();

              $("#alreadExstUN .goToLogin").show();

              $("#alreadExstUN").slideDown();

              el.focus();

            } else {
              $("#freeAccount").slideDown();
              $("#alreadExstUN").hide();
              el.clearErrorAlert();
            }
          } else {
            showFeedbackMessage("ERROR", "error contacting server:" + response.message);
          }
        });

      });
    }
    if (ev.keyCode == 13)
      $("#EMAIL").focus();
  }

  function checkUniqueEmail (el,ev){
    el.stopTime("checkUniqueEmail");
    el.oneTime(1000,"checkUniqueEmail",function(){

      var req={
        "CM":"CKUNIQEM",
        "__tk":"<%=pageState.tokenCreate("tkunqem")%>",
        un:el.val() };
      $.getJSON('/applications/QA/site/access/accessAjaxController.jsp',req, function(response) {
        if (response.ok) {
          if (!response.unique){
            el.createErrorAlert("NOT_UNIQUE","");
            //el.effect("highlight", { "color": "red" }, 1500);
            $("#alreadExstEmail").slideDown();
            el.focus();
          } else {
            $("#alreadExstEmail").hide();
            el.clearErrorAlert();
            if(response.hasUnverifiedEmail)
                $("#alreadExstUNEM").show();
            if(el.val() != ""){
             $("#mailing").show();
            } else{
              $("#mailing").hide();
            }
          }
        } else {
          showFeedbackMessage("ERROR","error contacting server:"+response.message);
        }
      });
    });

    if (ev.keyCode==13)
      $("#EMAIL").focus();
  }
</script>