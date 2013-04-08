<%@ page import="com.QA.QAOperator, net.sf.json.JSONArray, net.sf.json.JSONObject, org.jblooming.agenda.CompanyCalendar,
org.jblooming.waf.settings.I18n, org.jblooming.waf.view.PageState" %><%@ page contentType="text/html; charset=utf-8" pageEncoding="UTF-8"%><%

  PageState pageState = PageState.getCurrentPageState();
  QAOperator logged=(QAOperator) pageState.getLoggedOperator();

%><%if (false){%>
<script type="text/javascript">
<%}%>


function callFacebook() {
  var req = { "CM":"FACEBOOKAUTH"};
  $.getJSON('/applications/QA/site/access/parts/oauthRequester.jsp', req, function (response) {
    if (response.ok)
      location.href = response.url;
    else
      alert("Facebook login is not currently available.")
  });
}

function getHome(){
  self.location.href='/index.jsp';
}

function dateToRelative(localTime) {
  var diff=new Date().getTime()-localTime;
  var ret="";

  var min=<%=CompanyCalendar.MILLIS_IN_MINUTE%>;
  var hour=<%=CompanyCalendar.MILLIS_IN_HOUR%>;
  var day=<%=CompanyCalendar.MILLIS_IN_DAY%>;
  var wee=<%=CompanyCalendar.MILLIS_IN_WEEK%>;
  var mon=<%=CompanyCalendar.MILLIS_IN_MONTH%>;
  var yea=<%=CompanyCalendar.MILLIS_IN_YEAR%>;

  if (diff<-yea*2)
    ret ="<%=I18n.get("DATEREL_IN_%%_YEARS","##")%>".replace("##",(-diff/yea).toFixed(0));

  else if (diff<-mon*9)
    ret ="<%=I18n.get("DATEREL_IN_%%_MONTHS","##")%>".replace("##",(-diff/mon).toFixed(0));

  else if (diff<-wee*5)
    ret ="<%=I18n.get("DATEREL_IN_%%_WEEKS","##")%>".replace("##",(-diff/wee).toFixed(0));

  else if (diff<-day*2)
    ret ="<%=I18n.get("DATEREL_IN_%%_DAYS","##")%>".replace("##",(-diff/day).toFixed(0));

  else if (diff<-hour)
    ret ="<%=I18n.get("DATEREL_IN_%%_HOURS","##")%>".replace("##",(-diff/hour).toFixed(0));

  else if (diff<-min*35)
    ret ="<%=I18n.get("DATEREL_IN_ABOUT_1_HOUR")%>";

  else if (diff<-min*25)
    ret ="<%=I18n.get("DATEREL_IN_ABOUT_HALF_HOUR")%>";

  else if (diff<-min*10)
    ret ="<%=I18n.get("DATEREL_IN_SOME_MINUTES")%>";

  else if (diff<-min*2)
    ret ="<%=I18n.get("DATEREL_IN_FEW_MINUTES")%>";

  else if (diff<=min)
    ret ="<%=I18n.get("DATEREL_JUSTNOW")%>";

  else if (diff<=min*5)
    ret ="<%=I18n.get("DATEREL_FEW_MINUTES_AGO")%>";

  else if (diff<=min*15)
    ret ="<%=I18n.get("DATEREL_SOME_MINUTES_AGO")%>";

  else if (diff<=min*35)
    ret ="<%=I18n.get("DATEREL_ABOUT_HALF_HOUR_AGO")%>";

  else if (diff<=min*75)
    ret ="<%=I18n.get("DATEREL_ABOUT_1_HOUR_AGO")%>";

  else if (diff<=hour*5)
    ret ="<%=I18n.get("DATEREL_FEW_HOURS_AGO")%>";

  else if (diff<=hour*24)
    ret ="<%=I18n.get("DATEREL_%%_HOURS_AGO","##")%>".replace("##",(diff/hour).toFixed(0));

  else if (diff<=day*7)
    ret ="<%=I18n.get("DATEREL_%%_DAYS_AGO","##")%>".replace("##",(diff/day).toFixed(0));

  else if (diff<=wee*5)
    ret ="<%=I18n.get("DATEREL_%%_WEEKS_AGO","##")%>".replace("##",(diff/wee).toFixed(0));

  else if (diff<=mon*12)
    ret ="<%=I18n.get("DATEREL_%%_MONTHS_AGO","##")%>".replace("##",(diff/mon).toFixed(0));

  else
    ret ="<%=I18n.get("DATEREL_%%_YEARS_AGO","##")%>".replace("##",(diff/yea).toFixed(0));

  return ret;
}

// transform string values to printable: \n in <br>
function transformToPrintable(data){
  for (var prop in data) {
    var value = data[prop];
    if (typeof(value)=="string")
      data[prop]=(value + "").replace(/\n/g, "<br>");
  }
  return data;
}

// see http://www.tinymce.com/tryit/ajax_load_save.php
function tinyInitializer(tinyId, callback) {

  if (isiOsDevice)
    return;

  tinyMCE.init( {
    tmce_ta_id : tinyId,
    theme : "advanced",
    mode: "exact",
    //maxlength_onchange_callback : "alertOnMaxLen",
    elements : tinyId,
    valid_elements : "*[*]",
    relative_urls : true,
    remove_script_host : true,
    //document_base_url : baseUrl,
    cleanup: true,
    force_br_newlines: true,
    forced_root_block:false, plugins: " paste",
    theme_advanced_toolbar_location : "top",
    theme_advanced_toolbar_align : "left",
    theme_advanced_buttons1 : "bold,italic,underline, separator, pastetext,pasteword, separator,link,unlink",
    theme_advanced_buttons2 : "",
    theme_advanced_buttons3 : "",
    theme_advanced_buttons4 : "",
    init_instance_callback: callback
  });
}


<%------------------  BLACK POPUP-PAG MANAGEMENT ------------------------------------------------------------------------------%>
//returns a jquery object where to write content
function createBlackPage(width,height,onCloseCallBack){
  if (!width)
    width='900px';
  if (!height)
    height='500px';

  $("#__blackpopup__").remove();

  var bg=$("<div>").attr("id","__blackpopup__");
  bg.css({position:'fixed',top:"0px",paddingTop:"50px", left:0,width:'100%',height:'100%', backgroundImage:"url('/applications/QA/images/overlay.png')"});
  bg.append("<div id='bwinPopupd' name='bwinPopupd'></div>");
  bg.bringToFront();

  var ret=bg.find("#bwinPopupd");
  ret.css({width:width, height:height,top:10, "-moz-box-shadow":'1px 1px 6px #333333',overflow:'auto',"-webkit-box-shadow":'1px 1px 6px #333333', border:'1px solid #777', backgroundColor:"#fff", margin:"auto" });

  var bdiv= $("<div>").css({width:width,position:"relative",height:"0px", textAlign:"right", margin:"auto" });
  var img=$("<img src='/applications/QA/images/closeBig.png' style='cursor:pointer;position:absolute;right:-40px;top:5px;' title='<%=I18n.get("CLOSE")%>'>");
  bdiv.append(img);
  img.click( function(){
    bg.trigger("close");
  });

  bg.prepend(bdiv);
  $("body").append(bg);

  //close call callback
  bg.bind("close",function(){
    bg.remove();
    if (typeof(onCloseCallBack)=="function")
      onCloseCallBack();
  });

  //destroy do not call callback
  bg.bind("destroy",function(){
    bg.remove();
  });

  return ret;
}

function getBlackPopup(){
  var ret=$("#__blackpopup__");
  if (typeof(top)!="undefined"){
    ret=top.$("#__blackpopup__");
  }
  return ret;
}

function closeBlackPopup(divId){
  if(!divId)
    divId = "__blackpopup__";
  getBlackPopup(divId).trigger("close");
}

function openBlackPopup(url,width,height,onCloseCallBack) {
  if (!width)
    width='1024px';
  if (!height)
    height='730px';

  $("#__blackpopup__").remove();

  var bg=$("<div>").attr("id","__blackpopup__");
  bg.css({position:'fixed',top:0, left:0,width:'100%',height:'100%', backgroundImage:"url('/applications/QA/images/overlay.png')",textAlign:'center'});
  bg.append("<iframe id='bwinPopup' name='bwinPopup' frameborder='0' style='margin-top:0'></iframe>");
  bg.bringToFront();

  bg.find("#bwinPopup").attr("src",url).css({width:width, height:height,top:100, "-moz-box-shadow":'1px 1px 6px #333333', "-webkit-box-shadow":'1px 1px 6px #333333', border:'1px solid #777', margin:"auto", "margin-top":'0'});

  var bdiv= $("<div>").css({width:width,position:"relative",height:"5px", textAlign:"right", margin:"auto" });
  var img=$("<img src='/applications/QA/images/closeBig.png' class='closeOverlay' style='z-index:15000' title='<%=I18n.get("CLOSE")%>'>");
  bdiv.append(img);
  img.click( function(){
    bg.trigger("close");
  });

  bg.prepend(bdiv);
  $("body").append(bg);
  $("body").scrollTo(0).css("overflow","hidden");

  //close call callback
  bg.bind("close",function(){
    bg.remove();

    $("body").css("overflow","auto");

    if (typeof(onCloseCallBack)=="function")
      onCloseCallBack();
  });

  //destroy do not call callback
  bdiv.bind("destroy",function(){
    bdiv.remove();
  });

}

// make autoresize textareas
function textAreaAutoResize (domScheda) {
  //var textArea = $('#notes');
  var textArea = domScheda.find('textarea.autosize');

  if($.browser.msie) {
    textArea.focus(function(){$(this).trigger('change');}).oneTime(150, "taautosize", function() {
      $(this).trigger('change');
    });
  } else {
    textArea.autoResize({
      animateDuration : 300,
      extraSpace : 40,
      limit:400
    }).focus(function(){$(this).trigger('change');}).oneTime(150, "taautosize", function() {
      $(this).trigger('change');
    });
  }
}


// manage bread crumbs animation
$.animateHover = {
  name: "mb.selectSlide",
  author: "Matteo Bicocchi",
  version: "1.0",
  defaults: {
    eventListener: true,
    childrenTag: "span.voice",
    cssSelect: "selectedBlock",
    cssHover: "hoverBlock",
    cssRoot:"rootBlock",
    duration: 250,
    onInitialized:function(){},
    onSelect:function(){}
  },
  run: function(options) {
    var opt = {};
    $.extend(opt, $.animateHover.defaults, options);
    return this.each(function() {
      var menu = $(this).get(0);
      menu.opt = opt;
      var $menu = $(menu);
      var elements = $(this).children(opt.childrenTag);
      menu.elements = elements;
      var wrapper = $("<div/>").addClass("selectSlideWrapper");
      var selector = $("<div/>").addClass(opt.cssSelect).css({
        opacity: 0,
        position: "absolute"
      });
      var hover = $("<div/>").addClass(opt.cssHover).css({
        opacity: 0,
        position: "absolute"
      });
      var root=$("<div/>").addClass(opt.cssRoot).css({
        opacity:0,
        position:"absolute"
      });
      elements.css({
        position: "relative"
      });
      $menu.wrapInner(wrapper);
      $(".selectSlideWrapper", $menu).prepend(selector).prepend(hover).prepend(root);
      selector.css({
        height: selector.height() ? selector.height() : elements.eq(0).outerHeight(),
        top:0
      });
      hover.css({
        height: hover.height() ? hover.height() : elements.eq(0).outerHeight(),
        top:0
      });
      root.css({
        height:root.height()?root.height():elements.eq(0).outerHeight()
      });

      $(window).resize(function(){
        $menu.resetPosition();
      });

      if (opt.eventListener)
        elements.each(function() {
          $(this).bind("click", function() {
            var voice = $(this);
            if (voice.hasClass("sel")) return;
            hover.animate({
              opacity: 0,
              left: voice.position().left,
              width: voice.outerWidth()
            }, opt.duration);
            elements.removeClass("hover");
            elements.filter(".sel").removeClass("sel");
            selector.stop().animate({
              opacity: 1,
              left: voice.position().left,
              top:voice.position().top,
              width: voice.outerWidth()
            }, opt.duration, function() {
              voice.addClass("sel")
            });
            root.stop().animate({opacity:1,width:voice.position().left},opt.duration);
          }).bind("mouseenter", function() {
            var voice = $(this);
            elements.filter(".hover").removeClass("hover");
            if (voice.hasClass("sel")) {
              hover.animate({
                opacity: 0,
                left: voice.position().left,
                top:voice.position().top,
                width: voice.outerWidth()
              }, opt.duration);
              return;
            }
            elements.removeClass("hover");
            hover.stop(true, true).animate({
              opacity: 1,
              left: voice.position().left,
              top:voice.position().top,
              width: voice.outerWidth()
            }, opt.duration, function() {
              elements.filter(".hover").removeClass("hover");
              voice.addClass("hover");
            });
          });
          $menu.bind("mouseleave", function() {
            var left = elements.filter(".sel").length > 0 ? elements.filter(".sel").position().left : 0;
            hover.animate({
              opacity: 0,
              left: left,
              top:elements.filter(".sel").position().top,
              width: 0
            }, opt.duration, function() {
              elements.removeClass("hover");
            });
            elements.removeClass("hover");
          });

          if (typeof opt.onInitialized=="function")
            opt.onInitialized($menu, selector, root, hover);
        })
    })
  },
  goToStep: function(step, slide) {

    if (slide == undefined) slide = true;
    if (step=="")
      return;
    var menu = this.get(0);
    var $menu = $(menu);
    var duration = slide ? menu.opt.duration : 0;
    step = step > menu.elements.length ? menu.elements.length - 1 : !parseFloat(step) > 0 ? -1 : step - 1;

    var voice = menu.elements.eq(step);
    var root=$("." + menu.opt.cssRoot,$menu);

    var selector = $("." + menu.opt.cssSelect, $menu);

    if (step === -1) {
      selector.stop().animate({
        opacity: 0,
        left: 0,
        top:voice.position().top,
        width: 0
      }, menu.opt.duration);
      root.stop().animate({opacity:0,width:0},menu.opt.duration);
      menu.elements.filter(".sel").removeClass("sel");
      return;
    }

    if (voice.hasClass("sel"))
      return;

    menu.elements.filter(".sel").removeClass("sel");
    selector.stop().animate({
      opacity: 1,
      left: voice.position().left,
      top:voice.position().top,
      width: voice.outerWidth()
    }, duration, function() {
      voice.addClass("sel");
      for (var i=0; i<step; i++){
        menu.elements.eq(i).addClass("visited");
      }
      if (typeof menu.opt.onSelect== "function")
        menu.opt.onSelect(voice);

    });

    root.stop().animate({opacity:1,width:voice.position().left},duration);

    if (voice.data("color")){
      selector.css({backgroundColor:selector.css("backgroundColor")});
      selector.animate({backgroundColor:voice.data("color")},duration);
    }
  },
  resetPosition:function(){
    var menu = this.get(0);
    var $menu = $(menu);
    var voice = menu.elements.filter(".sel");
    var selector = $("." + menu.opt.cssSelect, $menu);

    if(voice.length>0)
      selector.stop().animate({
        opacity: 1,
        left: voice.position().left,
        top:voice.position().top,
        width: voice.outerWidth()
      }, 0);
    
  }
};

$.fn.animateHover = $.animateHover.run;
$.fn.goToStep = $.animateHover.goToStep;
$.fn.resetPosition = $.animateHover.resetPosition;

$.fn.safety=function(callback, message){

  var el= this;
  if(el.get(0).cancelTimeOut)
    clearTimeout(el.get(0).cancelTimeOut);

  function cancel(){
    el.show();
    el.next(".safety").remove();
    el.attr("onClick",el.data("click"));

  }

  if(typeof callback=="function"){
    $(".safety").remove();
    el.data("click",el.attr("onClick"));
    el.removeAttr("onClick");
  }else{
    cancel();
    return;
  }

  var sure=$("<span/>").addClass("sure").html(message?message:"sure?&nbsp;&nbsp;&nbsp;&nbsp;");

  var y= $("<span/>").addClass("confirm").html("yes").bind("click",function(){
    if (typeof callback == "function"){
      callback();
    }
  });

  var n=$("<span/>").addClass("cancel").html("no").click(function(){
    el.safety("cancel");
  });

  var safety=$("<span/>").addClass("safety").css({cursor: "pointer"});
  safety.append(sure).append(y).append(" / ").append(n);
  safety.css({position:"absolute", top: el.position().top, left: el.position().left, marginTop:el.css("margin-top"), minWidth:el.width(), minHeight:el.height(), boxSizing:"border-box", whiteSpace:"nowrap"});
  el.after(safety);
  //el.hide();

  el.get(0).cancelTimeOut= setTimeout(cancel,4000);
};


function animationManager() {
  setTimeout(function(){
    $("#ycmBreadCrumbs").goToStep(document.ycm.step);
  },250);
}


/**
 * jQuery.fn.sortElements
 * --------------
 * @param Function comparator:
 *   Exactly the same behaviour as [1,2,3].sort(comparator)
 *
 * @param Function getSortable
 *   A function that should return the element that is
 *   to be sorted. The comparator will run on the
 *   current collection, but you may want the actual
 *   resulting sort to occur on a parent or another
 *   associated element.
 *
 *   E.g. $('td').sortElements(comparator, function(){
 *      return this.parentNode;
 *   })
 *
 *   The <td>'s parent (<tr>) will be sorted instead
 *   of the <td> itself.
 */
jQuery.fn.sortElements = function(){

  var sort = [].sort;

  return function(comparator, getSortable) {

    getSortable = getSortable || function(){return this;};

    var placements = this.map(function(){

      var sortElement = getSortable.call(this),
        parentNode = sortElement.parentNode,

        // Since the element itself will change position, we have
        // to have some way of storing its original position in
        // the DOM. The easiest way is to have a 'flag' node:
        nextSibling = parentNode.insertBefore(
          document.createTextNode(''),
          sortElement.nextSibling
          );

      return function() {

        if (parentNode === this) {
          throw new Error(
            "You can't sort elements if any one is a descendant of another."
            );
        }

        // Insert before flag:
        parentNode.insertBefore(this, nextSibling);
        // Remove flag:
        parentNode.removeChild(nextSibling);

      };

    });

    return sort.call(this, comparator).each(function(i){
      placements[i].call(getSortable.call(this));
    });
  };
};

/*===Q&A===*/

/*===QUESTION===*/

function manageLikeQ(questionId){
  if($("#like_" +questionId).is(".questionLike")){
    likeQuestions(questionId)

  } else{
    unlikeQuestion(questionId)
  }
}

/**MANAGE ANSWER LIKE**/

function manageLikeA(answerId){
  if($("#like_" +answerId).is(".answerLike")){
    likeAnswer(answerId)

  } else{
    unlikeAnswer(answerId)
  }
}

function likeQuestions(questionId){

  var req={CM:"LIKE_QUESTION",questionId:questionId};
  $.getJSON("/applications/QA/talk/ajaxTalkController.jsp",req,function(res){
    if (res.ok==true){

      //update likes

      var upVotes= res.question.queUpvotes;

      var likeSize = $('#likeSize'+questionId);
      if (likeSize.length) {

        var title = upVotes-1 == 0 ? "<%=I18n.g("QA_YOU_LIKE")%>" : "<%=I18n.g("QA_LIKE_YOU_AND")%> "+(upVotes)+" <%=I18n.g("QA_LIKE_THIS")%>";
        var brickLike = $('#like_'+questionId);
        brickLike.attr("title", title);

        likeSize.html(upVotes);

        brickLike.removeClass("questionLike").addClass("questionUnlike");

        /*ANIMATE LIKE*/

        var clone = brickLike.clone();
        clone.addClass("clone");
        clone.css({position:"absolute", left:brickLike.offset().left, top:brickLike.offset().top});
        $("body").append(clone);

        setTimeout(function(){
          $(".clone").addClass("animate");
          setTimeout(function(){$(".clone").remove()},500);
        },500);

      }

    }
  });
}

function unlikeQuestion(questionId){

  var req={CM:"UNLIKE_QUESTION",questionId:questionId};
  $.getJSON("/applications/QA/talk/ajaxTalkController.jsp",req,function(res){
    if (res.ok==true){
      //update likes

      var upVotes= res.question.queUpvotes;

      var likeSize = $('#likeSize'+questionId);
      if (likeSize) {
        var title = upVotes==0 ? "<%=I18n.g("QA_DO_LIKE_BRICKS_AND")%>" : "<%=I18n.g("QA_PERSON_LIKE_THIS")%> "+upVotes+" <%=I18n.g("QA_LIKE_THIS_IN_TOTAL")%>";

        var brickUnlike = $('#like_'+questionId);
        brickUnlike.attr("title", title);

        likeSize.html(upVotes);
        brickUnlike.removeClass("questionUnlike").addClass("questionLike");

        /*ANIMATE LIKE*/

        var clone = brickUnlike.clone();
        clone.addClass("clone");
        clone.css({position:"absolute", left:brickUnlike.offset().left, top:brickUnlike.offset().top});
        $("body").append(clone);

        setTimeout(function(){
          $(".clone").addClass("animate");
          setTimeout(function(){$(".clone").remove()},500);
        },500);

      }
    }
  });
}


function manageAnswerLike(answerId){
  if($("#like_" +answerId).is(".answerLike")){
    likeAnswer(answerId)

  } else{
    unlikeAnswer(answerId)
  }
}

function likeAnswer(answerId){

  var req={CM:"LIKE_ANSWER",answerId:answerId};
  $.getJSON("/applications/QA/talk/ajaxTalkController.jsp",req,function(res){
    if (res.ok==true){

      //update likes

      var upVotes= res.answer.propVotes;

      var likeSize = $('#likeSize'+answerId);
      if (likeSize.length) {

        var title = upVotes-1 == 0 ? "<%=I18n.g("QA_YOU_LIKE")%>" : "<%=I18n.g("QA_LIKE_YOU_AND")%> "+(upVotes)+" <%=I18n.g("QA_LIKE_THIS")%>";
        var answerLike = $('#like_'+answerId);
        answerLike.attr("title", title);

        likeSize.html(upVotes);

        answerLike.removeClass("answerLike").addClass("answerUnlike");

        /*ANIMATE ANSWER LIKE*/

        var clone = answerLike.clone();
        clone.addClass("clone");
        clone.css({position:"absolute", left:answerLike.offset().left, top:answerLike.offset().top});
        $("body").append(clone);

        setTimeout(function(){
          $(".clone").addClass("animate");
          setTimeout(function(){$(".clone").remove()},500);
        },500);

      }
    }
  });
}

function unlikeAnswer(answerId){

  var req={CM:"UNLIKE_ANSWER",answerId:answerId};
  $.getJSON("/applications/QA/talk/ajaxTalkController.jsp",req,function(res){
    if (res.ok==true){

      //update likes

      var upVotes= res.answer.propVotes;

      var likeSize = $('#likeSize'+answerId);

      if (likeSize) {
        var title = upVotes==0 ? "<%=I18n.g("QA_DO_LIKE_BRICKS_AND")%>" : "<%=I18n.g("QA_PERSON_LIKE_THIS")%> "+upVotes+" <%=I18n.g("QA_LIKE_THIS_IN_TOTAL")%>";



        var answerUnlike = $('#like_'+answerId);
        answerUnlike.attr("title", title);

        likeSize.html(upVotes);
        answerUnlike.removeClass("answerUnlike").addClass("answerLike");

        /*ANIMATE ANSWER UNLIKE*/

        var clone = answerUnlike.clone();
        clone.addClass("clone");
        clone.css({position:"absolute", left:answerUnlike.offset().left, top:answerUnlike.offset().top});
        $("body").append(clone);

        setTimeout(function(){
          $(".clone").addClass("animate");
          setTimeout(function(){$(".clone").remove()},500);
        },500);

      }
    }
  });
}


function refuteAnswer(answerId){

  var req={CM:"REFUTE_ANSWER",answerId:answerId};
  $.getJSON("/applications/QA/talk/ajaxTalkController.jsp",req,function(res){
    if (res.ok==true){

      //update layout
      window.location.reload();

    }
  });
}

function acceptAnswer(answerId){

  var req={CM:"ACCEPT_ANSWER",answerId:answerId};
  $.getJSON("/applications/QA/talk/ajaxTalkController.jsp",req,function(res){
    if (res.ok==true){

      //update layout
      window.location.reload();

    }
  });
}


function questionReport(el){
  var questionId = $(el).attr("questionId");
  var req={CM:"REPORT_QUESTION",questionId:questionId};
  $.getJSON("/applications/QA/talk/ajaxTalkController.jsp",req,function(res){
    if (res.ok==true){
      $('#qReportId_'+questionId).fadeOut();
      alert("<%=I18n.get("QUESTION_CONTENT_REPORTED")%>");
    }
  });
}

function banQuestion(el){
  var questionId = $(el).attr("questionId");
  //console.debug("------------> id ",brickId)
  var req={CM:"BAN_QUESTION",questionId:questionId};
  $.getJSON("/applications/QA/talk/ajaxTalkController.jsp",req,function(res){
    if (res.ok==true){
      $('#qReportId_'+questionId).fadeOut();
      alert("Done");
    }
  });
}

function saveQuestionComment(questionId){
  var comm = $('#COMMENT_'+questionId).val();

  var req={CM:"COMMENT_QUESTION",questionId:questionId,commentText:comm};
  $.getJSON("/applications/QA/talk/ajaxTalkController.jsp",req,function(res){
    if (res.ok==true){

      window.location.reload();
    }
  });
}

function removeQuestionComment(el){
  var commentId = $(el).closest("div").attr('id').substr("commQId_".length);

  var req={CM:"REMOVE_QUESTION_COMMENT",commentId:commentId};
  $.getJSON("/applications/QA/talk/ajaxTalkController.jsp",req,function(res){
    if (res.ok==true){
      window.location.reload();
    }
  });
}



function commentQuestionReport(el){
  var commentId = $(el).closest("a").attr('id').substr("commQIdR_".length);
  var req={CM:"REPORT_QUESTION_COMMENT",commentId:commentId};
  $.getJSON("/applications/QA/talk/ajaxTalkController.jsp",req,function(res){
    if (res.ok==true){
      $('#commQIdR_'+commentId).fadeOut();
      alert("<%=I18n.get("QUESTION_COMMENT_CONTENT_REPORTED")%>");
    }
  });
}

function saveAnswerComment(answerId){
  var comm = $('#ANSWER_'+answerId).val();

  var req={CM:"COMMENT_ANSWER",answerId:answerId,commentText:comm};
  $.getJSON("/applications/QA/talk/ajaxTalkController.jsp",req,function(res){
    if (res.ok==true){

      window.location.reload();
    }
  });
}

function removeAnswerComment(el){
  var commentId = $(el).closest("div").attr('id').substr("commAId_".length);

  var req={CM:"REMOVE_ANSWER_COMMENT",commentId:commentId};
  $.getJSON("/applications/QA/talk/ajaxTalkController.jsp",req,function(res){
    if (res.ok==true){
      window.location.reload();
    }
  });
}



function commentAnswerReport(el){
  var commentId = $(el).closest("a").attr('id').substr("commAIdR_".length);
  var req={CM:"REPORT_ANSWER_COMMENT",commentId:commentId};
  $.getJSON("/applications/QA/talk/ajaxTalkController.jsp",req,function(res){
    if (res.ok==true){
      $('#commAIdR_'+commentId).fadeOut();
      alert("<%=I18n.get("ANSWER_COMMENT_CONTENT_REPORTED")%>");
    }
  });
}




function answerReport(el){
  var answerId = $(el).attr("answerId");
  var req={CM:"REPORT_ANSWER",answerId:answerId};
  $.getJSON("/applications/QA/talk/ajaxTalkController.jsp",req,function(res){
    if (res.ok==true){
      $('#aReportId_'+answerId).fadeOut();
      alert("<%=I18n.get("ANSWER_CONTENT_REPORTED")%>");
    }
  });
}

function banAnswer(el){
  var answerId = $(el).attr("answerId");
  //console.debug("------------> id ",brickId)
  var req={CM:"BAN_ANSWER",answerId:answerId};
  $.getJSON("/applications/QA/talk/ajaxTalkController.jsp",req,function(res){
    if (res.ok==true){
      $('#aReportId_'+answerId).fadeOut();
      alert("Banned");
    }
  });
}





/*===BRICKS===*/

function manageLike(brickId){
  if($("#like_" +brickId).is(".brkLike")){
    likeBrick(brickId)

  } else{
    unlikeBrick(brickId)
  }
}

function likeBrick(brickId){

  var req={CM:"LIKE_BRICK",brickId:brickId};
  $.getJSON("/applications/QA/ajax/ajaxBrickController.jsp",req,function(res){
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

function unlikeBrick(brickId){

  var req={CM:"UNLIKE_BRICK",brickId:brickId};
  $.getJSON("/applications/QA/ajax/ajaxBrickController.jsp",req,function(res){
    if (res.ok==true){
      //update likes

      var upVotes= res.brick.brickUpvotes;

      var likeSize = $('#likeSize'+brickId);
      if (likeSize) {
        var title = upVotes==0 ? "<%=I18n.g("QA_DO_LIKE_BRICKS_AND")%>" : upVotes+" <%=I18n.g("QA_LIKE_THIS_IN_TOTAL")%>";

        var brickUnlike = $('#like_'+brickId);
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

function banBrick(el){
  var brickId = $(el).closest("a").attr('id').substr("brickIdB_".length);
  //console.debug("------------> id ",brickId)
  var req={CM:"BAN_BRICK",brickId:brickId};
  $.getJSON("/applications/QA/ajax/ajaxBrickController.jsp",req,function(res){
    alert("Banned");
  });
}

function commentReport(el){
  var commentId = $(el).closest("a").attr('id').substr("commIdR_".length);
  var req={CM:"REPORT_COMMENT",commentId:commentId};
  $.getJSON("/applications/QA/ajax/ajaxBrickController.jsp",req,function(res){
    if (res.ok==true){
      $('#commIdR_'+commentId).fadeOut();
      alert("<%=I18n.get("BRICK_COMMENT_CONTENT_REPORTED")%>");
    }
  });
}

function brickReport(el){
  var brickId = $(el).closest("a").attr('id').substr("brickIdR_".length);
  var req={CM:"REPORT_BRICK",brickId:brickId};
  $.getJSON("/applications/QA/ajax/ajaxBrickController.jsp",req,function(res){
    if (res.ok==true){
      $('#brickIdR_'+brickId).addClass("brkReport").removeAttr("onclick");
      alert("<%=I18n.get("BRICK_CONTENT_REPORTED")%>");
    }
  });
}


function saveAndInsertComment(brickId){
  var comm = $('#COMMENT_'+brickId).val();

  var moderatorComment = $('#moderatorComment_'+brickId).val();

  var req={CM:"COMMENT_BRICK",brickId:brickId,commentText:comm,moderatorComment:moderatorComment};
  $.getJSON("/applications/QA/ajax/ajaxBrickController.jsp",req,function(res){
    if (res.ok==true){

      if (res.comment) {

        if (res.comment.cmtIsModeratorComment) {

          $('#comm_'+brickId).fadeOut();
          $('#doComment_'+brickId).fadeOut();
          top.location.reload();

        } else {

          $('#commslist_'+brickId).append('<hr>'+res.comment.cmtText+'<span>'+'<%=I18n.get("BY")%> '+res.comment.cmtOwnerName + '');
      $('#comm_'+brickId).fadeOut();
      $('#doComment_'+brickId).fadeOut();
      var cN = parseInt($('#totComm_'+brickId).html());
      cN = cN ? cN : 0;
          var number = cN+1;
          $('#totComm_'+brickId).find("em").html("(" + (number) + ")");
        }
      }

      //add total


    }
  });
}

function removeComment(el){
  var commentId = $(el).closest("div").attr('id').substr("commId_".length);

  var req={CM:"REMOVE_COMMENT",commentId:commentId};
  $.getJSON("/applications/QA/ajax/ajaxBrickController.jsp",req,function(res){
    if (res.ok==true){


      $('#commId_'+commentId).fadeOut();

      var brickId = res.brickId;
      $('#totComm_'+brickId).html(parseInt($('#totComm_'+brickId).html())-1);
    }
  });
}


/*
 * ScrollToElement 1.0
 * Copyright (c) 2009 Lauri Huovila, Neovica Oy
 *  lauri.huovila@neovica.fi
 *  http://www.neovica.fi
 *
 * Dual licensed under the MIT and GPL licenses.
 * $("#my-element).scrollTo();
 * $("#other-element").scrollTo(1500); //Speed
 *  $.scrollToElement( $("#my-element") );
 */


(function($) {
  $.scrollToElement = function( $element, speed , offset) {

    speed = speed || 750;
    offset = offset || 0;

    $("html, body").animate({
      scrollTop: $element.offset().top-offset,
      scrollLeft: $element.offset().left
    }, speed);
    return $element;
  };

  $.fn.scrollTo = function( speed , offset) {
    speed = speed || "normal";
    return $.scrollToElement( this, speed, offset );
  };
})(jQuery);

$(function(){
  $(".linkEnabled").emoticonize(true).activateLinks(true);
})

function brickHighlight(brickId, callback) {
  var brickId = "#brick_"+brickId;
  if($(brickId).length>0){
    $(brickId).addClass("highLight");
    var top = $(brickId).offset().top - $("#header").height();
    setTimeout(function(){
      $(brickId).scrollTo(1000, 200);
    }, 500);
  }
  setTimeout(function(){
    if(typeof callback=="function")
      callback();
  },1000)

}

// return true when set to visible
function showHideSpan(spanId) {
  var span = $('#' + spanId);
  var visibility;
  if (span.css("display") == 'none') {
    span.fadeIn();
    visibility = true;
  } else {
    span.fadeOut();
    visibility = false;
  }
  return visibility;
}


/*
WebFontConfig = {
  google: { families: [ 'Antic+Slab::latin', 'Viga::latin', 'Ropa+Sans::latin', 'PT+Mono::latin', 'Droid+Sans+Mono::latin', 'Della+Respira::latin', 'Graduate::latin', 'Trocchi::latin', 'Imprima::latin' ] }
};
(function() {
  var wf = document.createElement('script');
  wf.src = ('https:' == document.location.protocol ? 'https' : 'http') +
          '://ajax.googleapis.com/ajax/libs/webfont/1/webfont.js';
  wf.type = 'text/javascript';
  wf.async = 'true';
  var s = document.getElementsByTagName('script')[0];
  s.parentNode.insertBefore(wf, s);
})();
*/


/*ALIAS
   font-family: 'Antic Slab', serif;
   font-family: 'Viga', sans-serif;
   font-family: 'Ropa Sans', sans-serif;
   font-family: 'PT Mono', sans-serif;
   font-family: 'Droid Sans Mono', sans-serif;
   font-family: 'Della Respira', serif;
   font-family: 'Graduate', cursive;
   font-family: 'Trocchi', serif;
   font-family: 'Imprima', sans-serif;
*/

<%if (false){%>
</script>
<%}%>