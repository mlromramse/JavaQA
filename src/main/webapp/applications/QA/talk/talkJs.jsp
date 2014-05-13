<%@ page import="org.jblooming.waf.settings.I18n" %>
<%if (false){%>
<script type="text/javascript">
  <%}%>

//todo: transform to those for bricks with manage etc...
//todo introduce like answer too

function manageQLike(questionId){

  if($("#like_" +questionId).is(".brkLike")){
    likeBrick(questionId)

  } else{
    unlikeBrick(questionId)

  }
}

function likeQuestion(brickId){

  var req={CM:"LIKE_BRICK",brickId:brickId};
  $.getJSON("/applications/QA/talk/ajaxTalkController.jsp",req,function(res){
    if (res.ok==true){

      //update likes

      var upVotes= res.brick.brickUpvotes;

      var likeSize = $('#likeSize'+brickId);
      if (likeSize.length) {

        var title = upVotes-1 == 0 ? "<%=I18n.g("QA_YOU_LIKE")%>" : "<%=I18n.g("QA_LIKE_YOU_AND")%> "+(upVotes-1)+" <%=I18n.g("QA_LIKE_THIS")%>";
        var brickLike = $('#like_'+brickId);
        brickLike.attr("title", title);

        likeSize.html(res.brick.brickUpvotes);

        brickLike.removeClass("brkLike").addClass("brkUnlike");

        /*ANIMATE LIKE*/

        var clone = brickLike.clone();
        clone.addClass("clone");
        clone.css({position:"absolute", left:brickLike.offset().left, top:brickLike.offset().top});
        $("body").append(clone);

        setTimeout(function(){
          $(".clone").addClass("animate");
          setTimeout(function(){$(".clone").remove()},1000);
        },1000);

      }

    }
  });
}

function unlikeQuestion(questionId){

  var req={CM:"UNLIKE_BRICK",brickId:questionId};
  $.getJSON("/applications/QA/ajax/ajaxTalkController.jsp",req,function(res){
    if (res.ok==true){
      //update likes

      var upVotes= res.brick.brickUpvotes;

      var likeSize = $('#likeSize'+questionId);
      if (likeSize) {
        var title = upVotes==0 ? "<%=I18n.g("QA_DO_LIKE_BRICKS_AND")%>" : upVotes+" <%=I18n.g("QA_LIKE_THIS_IN_TOTAL")%>";

        var brickUnlike = $('#like_'+questionId);
        brickUnlike.attr("title", title);

        likeSize.html(res.brick.brickUpvotes);
        brickUnlike.removeClass("brkUnlike").addClass("brkLike");

        /*ANIMATE LIKE*/

        var clone = brickUnlike.clone();
        clone.addClass("clone");
        clone.css({position:"absolute", left:brickUnlike.offset().left, top:brickUnlike.offset().top});
        $("body").append(clone);

        setTimeout(function(){
          $(".clone").addClass("animate");
          setTimeout(function(){$(".clone").remove()},1000);
        },500);

      }
    }
  });
}

  <%if (false){%>
</script>
<%}%>