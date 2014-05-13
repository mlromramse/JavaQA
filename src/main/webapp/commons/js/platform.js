//browser detection

// works also for IE8 beta
var isExplorer = (navigator.appVersion.indexOf("MSIE") != -1) ? true : false;
var isOpera = (navigator.userAgent.indexOf("Opera") != -1) ? true : false;
var isNetscape = (navigator.userAgent.indexOf("Netscape") != -1) ? true : false;
var isFirefox = (navigator.userAgent.indexOf("Firefox") != -1) ? true : false;
var isChrome = (navigator.userAgent.indexOf("Chrome") != -1) ? true : false;
var isSafari = !isChrome && (navigator.userAgent.indexOf("Safari") != -1) ? true : false;

isNetscape = isNetscape || isFirefox;

//OS detection
var isWin = (navigator.appVersion.toLowerCase().indexOf("win") != -1) ? true : false;
var isMac = (navigator.appVersion.toLowerCase().indexOf("mac") != -1) ? true : false;
var isUnix = (navigator.appVersion.toLowerCase().indexOf("x11") != -1) ? true : false;
var isLinux = (navigator.appVersion.toLowerCase().indexOf("linux") != -1) ? true : false;

//Version detection
var is_major = parseInt(navigator.appVersion);
var is_minor = parseFloat(navigator.appVersion);
var version = navigator.appVersion.substring(0, 1);


// global variables
var muteAlertOnChange = false;


// decrecated use $("#domid").get()
function obj(element) {
  if (arguments.length > 1) {
    alert("invalid use of obj with multiple params:" + element)
  }
  return document.getElementById(element);
}

if(!window.console) {
  window.console = new function() {
    this.log = function(str) {/*alert(str)*/};
    this.debug = function(str) {/*alert(str)*/};
    this.error = function(str) {/*alert(str)*/};
  };
}
if(!window.console.debug || !window.console.error|| !window.console.log ) {
  window.console = new function() {
    this.log = function(str) {/*alert(str)*/};
    this.debug = function(str) {/*alert(str)*/};
    this.error = function(str) {/*alert(str)*/};
  };
}

function sendWindowResizeMessage() {
  executeCommand('UPDATEWINSIZE', "PAGE_HEIGHT=" + $(window).height() + "&PAGE_WIDTH=" + $(window).width());
}

function centerPopup(url, target, w, h, scroll, resiz) {
  var winl = (screen.width - w) / 2;
  var wint = (screen.height - h) / 2;
  var winprops = 'height=' + h + ',width=' + w + ',top=' + wint + ',left=' + winl + ',scrollbars=' + scroll + ',resizable=' + resiz + ', toolbars=false, status=false, menubar=false';
  var win = window.open(url, target, winprops);
  if (!win)
    alert("A popup blocker was detected: please allow them for this application (check out the upper part of the browser window).");
  if (parseInt(navigator.appVersion) >= 4) {
    win.window.focus();
  }
}

function openCenteredWindow(url, target, winprops) {
  var prop_array = winprops.split(",");
  var i = 0;
  var w = 800;
  var h = 600;
  if (winprops && winprops != '') {
    while (i < prop_array.length) {
      if (prop_array[i].indexOf('width') > -1) {
        s = prop_array[i].substring(prop_array[i].indexOf('=') + 1);
        w = parseInt(s);
      } else if (prop_array[i].indexOf('height') > -1) {
        s = prop_array[i].substring(prop_array[i].indexOf('=') + 1);
        h = parseInt(s);
      }
      i += 1;
    }
    var winl = (screen.width - w) / 2;
    var wint = (screen.height - h) / 2;
    winprops = winprops + ",top=" + wint + ",left=" + winl;
  }
  win = window.open(url, target, winprops);
  if (!win)
    alert("A popup blocker was detected: please allow them for this application (check out the upper part of the browser window).");
  if (parseInt(navigator.appVersion) >= 4) {
    win.window.focus();
  }
}


//----------------------------------positioning-----------------------------------------------
$.fn.bringToFront=function(){
      var zi=10;
      $('*').each(function() {
        if($(this).css("position")=="absolute"){
          var cur = parseInt($(this).css('zIndex'));
          zi = cur > zi ? parseInt($(this).css('zIndex')) : zi;
        }
      });

      $(this).css('zIndex',zi+=10);
    };


function bringToFront(elid) {
  $("#"+elid).bringToFront();
}

function nearBestPosition(whereId, theObjId) {
    var el=whereId;
    var target=theObjId;
    if (typeof whereId != "object"){ el = $("#"+whereId); }
    if (typeof theObjId != "object"){target = $("#"+theObjId);}
  if (el) {
    target.css("visibility","hidden");
    var trueX = el.offset().left;
    var trueY = el.offset().top;
    var h = el.outerHeight();
    var elHeight = parseFloat(h);
    trueY += parseFloat(elHeight);
    var left = trueX + "px";
    var top = trueY + "px";
    var barHeight = (isExplorer) ? 45 : 35;
    var barWidth = (isExplorer) ? 20 : 0;
    if (trueX && trueY) {
      target.css("left", left);
      target.css("top", top);
    }
    if (target.offset().left >= ($(window).width() - target.outerWidth())) {
      left = ($(window).width() - target.outerWidth() - barWidth )+ "px";
      target.css("left", left);
    }
    if ((target.offset().top  + target.outerHeight() >= (($(window).height() - barHeight))) && (target.outerHeight() < $(window).height())) {
      target.css("margin-top",(-(el.outerHeight() + target.outerHeight())) + "px");
    }
    target.css("visibility","visible");
  }
}

$.fn.keepItVisible= function(){
  var isFixed= false;  // todo: verify
  var thisTop = $(this).offset().top;
  var thisLeft = $(this).offset().left;
  var windowH = isFixed ? $(window).height() : $(window).height() + $(window).scrollTop();
  var windowW = isFixed ? $(window).width(): $(window).width() + $(window).scrollLeft();
  if(thisTop + $(this).outerHeight() > windowH)
   $(this).css("margin-top", -$(this).outerHeight());
  if(thisLeft + $(this).outerWidth() > windowW)
   $(this).css("margin-left", -$(this).outerWidth());
  $(this).css("visibility","visible");
}

//END positioning


// event handling START -------------------------------
function dragStart(event, id) {
  $("#"+id).draggable({stop:function(e,o){$(this).draggable("destroy");}});
  bringToFront(id);
}

//-- START AJAX ---------------------------------------------------------------------------

//var xmlHttpRequestObject;
function getXMLObj() {
  var req;
  if (window.XMLHttpRequest) {
    try {
      req = new XMLHttpRequest();
    } catch(e) {
      req = false;
    }
    // branch for IE/Windows ActiveX version
  } else if (window.ActiveXObject) {
    try {
      req = new ActiveXObject("Msxml2.XMLHTTP");
    } catch(e) {
      try {
        req = new ActiveXObject("Microsoft.XMLHTTP");
      } catch(e) {
        req = false;
      }
    }
  }
  return req;
}

function getContent(href, data) {
  // in order to avoid caching on IE
  if (href.indexOf("?") < 0)
    href = href + "?_=" + new Date().getMilliseconds();
  else
    href = href + "&_=" + new Date().getMilliseconds();
  //ret must be absolute!!!!!!!!!!!!!!
  ret = "";
  var req = getXMLObj();
  req.open('POST', href, false);
  req.setRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");

  if (data) {
    req.send(data);
  } else {
    req.send('');
  }

  if (req.status == 200) {
    ret = req.responseText;
    return ret;

    // 404 url not found -
  } else if (req.status == 404) {
    return 'File not found';

    // 500 internal server error
  } else if (req.status == 500) {
    //alert('ajax request status is ' + req.status + ' for ' + href) ;
    return 'ajax request status is ' + req.status + ' for ' + href;
  }
}

function loadSyncroContent(href, objId, data) {
  $("#"+objId).html(getContent(href, data));
}


function executeCommand(command, data) {
  return getContent(contextPath + "/command.jsp?CM=" + command, data);
}



// start asynchronous part


/**
 *load obj.innerHtml with content of href asynchronously
 *if objId is null call the href but do not refresh
 */
function loadAsyncroHtml(href, objId, data, callBackFunction) {
  var ajax = getXMLObj();
  ajax.open('POST', href, true);
  ajax.setRequestHeader("connection", "close");
  ajax.setRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
  ajax.onreadystatechange = function() {
    if (ajax.readyState === 4) {
      window.status = "Ajax loading...";
      if (ajax.status == 200) {
        if (objId)
          $('#' + objId).html(ajax.responseText)
        window.status = "";
        if (callBackFunction) {
          callBackFunction.call();
        }
      } else {
        alert("Error opening:[" + href + "] status:" + ajax.status);
      }
    }
  };
  if (data)
    ajax.send(data);
  else
    ajax.send(null);
}

/**
 *load obj.outerHtml with content of href asynchronously
 *if objId is null call the href but do not refresh
 */
//todo ammazzare
function loadAsyncroContent(href, objId, data, callBackFunction) {

  console.debug("non ci posso credere!")

  var ajax = getXMLObj();
  ajax.open('POST', href, true);
  ajax.setRequestHeader("connection", "close");
  ajax.setRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
  ajax.onreadystatechange = function() {
    if (ajax.readyState === 4) {
      window.status = "Ajax loading...";
      if (ajax.status == 200) {
        if (objId)
          $('#' + objId).replaceWith(ajax.responseText)
        window.status = "";
        if (callBackFunction) {
          //alert('calling callback!'+callBackFunction);
          callBackFunction.call();
        }
      } else {
        alert("Error opening:[" + href + "] status:" + ajax.status);
      }
    }
  };
  if (data)
    ajax.send(data);
  else
    ajax.send(null);
}

//todo ammazzare
function ajaxSubmit(formId, domIdToReload, callBackFunction) {
  loadSyncroContent(obj(formId).action, domIdToReload, getDataFromForm(formId));
  if (callBackFunction)
    callBackFunction();
}

function getDataFromForm(formId) {
  var f,first,el;
  f = obj(formId);
  var url = '';
  first = true;
  for (var i = 0; i < f.elements.length; i++) {
    el = f.elements[i];
    var value;
    if (el.type == 'radio') {
      //alert(el.name+' '+ el.checked+' '+el.value);
      if (el.checked) {
        value = el.value;
      } else {
        value = null;
      }
    } else {
      value = el.value;
    }
    if (value != null) {
      //url = url + (first ? '' : '&') + el.name + '=' + escape(el.value);  // escape() is lesser safe than encodeURIComponent() see: http://xkr.us/articles/javascript/encode-compare/
      url = url + (first ? '' : '&') + el.name + '=' + encodeURIComponent(value);
      first = false;
    }
  }
  return url;
}

function showSavingMessage(){
  $("#SAVINGMESSAGE:hidden").fadeIn();
}
function hideSavingMessage(){
  $("#SAVINGMESSAGE:visible").fadeOut();
}

// END AJAX ----------------------------------------------------------------------------

// isRequired ----------------------------------------------------------------------------

//return true if every mandatory field is filled and highlight empty ones
jQuery.fn.isFullfilled = function() {
  var canSubmit = true;
  var firstErrorElement = "";

  this.each(function() {
    var theElement = $(this);
    theElement.removeClass("formElementsError");
    if (theElement.val().trim().length == 0 || theElement.attr("invalid") == "true") {
      if (theElement.attr("type") == "hidden") {
        theElement = $("#" + theElement.attr("id") + "_txt");
      }
      theElement.addClass("formElementsError");
      canSubmit = false;
      if (firstErrorElement == "")
        firstErrorElement = theElement;
    }
  });

  if (!canSubmit) {
    // get the tabdiv
    var theTabDiv = firstErrorElement.parents("[isTabSetDiv='true']");
    if (theTabDiv.length > 0)
      hideTabsAndShow(theTabDiv.attr("thisTabId"), theTabDiv.attr("thisTabsetId"));

    // highlight element
    firstErrorElement.effect("highlight", { color: "red" }, 1500);
  }
  return canSubmit;

};


function canSubmitForm(idForm) {
  return $("#" + idForm).find("[required=true],[invalid=true]").isFullfilled();
}


/*   Caret Functions
     Use setSelection with start = end to set caret
 */
function setSelection(input, start, end) {
  if (isNetscape) {
    input.setSelectionRange(start, end);
  } else {
    // assumed IE
    var range = input.createTextRange();
    range.collapse(true);
    range.moveStart('character', start);
    range.moveEnd('character', end - start);
    range.select();
  }
  //input.focus();  //removed per doppio evento focus su smartcombo
}

//-- Caret Functions END ---------------------------------------------------------------------------- --

/*    Escape function   */

String.prototype.trim = function () {
  return this.replace(/^\s*(\S*(\s+\S+)*)\s*$/, "$1");
};

String.prototype.startsWith = function(t, i) {
  if (!i) {
    return (t == this.substring(0, t.length));
  } else {
    return (t.toLowerCase()== this.substring(0, t.length).toLowerCase());
  }
};

String.prototype.endsWith = function(t, i) {
  if (!i) {
    return (t== this.substring(this.length - t.length));
  } else {
    return (t.toLowerCase() == this.substring(this.length -t.length).toLowerCase());
  }
};

// leaves only char from A to Z, numbers, _ -> valid ID
String.prototype.asId = function () {
  return this.replace(/[^a-zA-Z0-9_]+/g, '');
};
/* --- Escape --- */

if (!Array.prototype.indexOf) {
  Array.prototype.indexOf = function (searchElement, fromIndex) {
    if (this == null) {
      throw new TypeError();
    }
    var t = Object(this);
    var len = t.length >>> 0;
    if (len === 0) {
      return -1;
    }
    var n = 0;
    if (arguments.length > 0) {
      n = Number(arguments[1]);
      if (n != n) { // shortcut for verifying if it's NaN
        n = 0;
      } else if (n != 0 && n != Infinity && n != -Infinity) {
        n = (n > 0 || -1) * Math.floor(Math.abs(n));
      }
    }
    if (n >= len) {
      return -1;
    }
    var k = n >= 0 ? n : Math.max(len - Math.abs(n), 0);
    for (; k < len; k++) {
      if (k in t && t[k] === searchElement) {
        return k;
      }
    }
    return -1;
  };
}


Object.size = function(obj) {
    var size = 0, key;
    for (key in obj) {
        if (obj.hasOwnProperty(key)) size++;
    }
    return size;
};


// transform string values to printable: \n in <br>
function transformToPrintable(data){
  for (var prop in data) {
    var value = data[prop];
    if (typeof(value)=="string")
      data[prop]=(value + "").replace(/\n/g, "<br>");
  }
  return data;
}


/* Types Function */

function isValidURL(url){
  var RegExp = /^(([\w]+:)?\/\/)?(([\d\w]|%[a-fA-f\d]{2,2})+(:([\d\w]|%[a-fA-f\d]{2,2})+)?@)?([\d\w][-\d\w]{0,253}[\d\w]\.)+[\w]{2,4}(:[\d]+)?(\/([-+_~.\d\w]|%[a-fA-f\d]{2,2})*)*(\?(&?([-+_~.\d\w]|%[a-fA-f\d]{2,2})=?)*)?(#([-+_~.\d\w]|%[a-fA-f\d]{2,2})*)?$/;
  return RegExp.test(url);
}

function isValidEmail(email){
  var RegExp = /^((([a-z]|[0-9]|!|#|$|%|&|'|\*|\+|\-|\/|=|\?|\^|_|`|\{|\||\}|~)+(\.([a-z]|[0-9]|!|#|$|%|&|'|\*|\+|\-|\/|=|\?|\^|_|`|\{|\||\}|~)+)*)@((((([a-z]|[0-9])([a-z]|[0-9]|\-){0,61}([a-z]|[0-9])\.))*([a-z]|[0-9])([a-z]|[0-9]|\-){0,61}([a-z]|[0-9])\.)[\w]{2,4}|(((([0-9]){1,3}\.){3}([0-9]){1,3}))|(\[((([0-9]){1,3}\.){3}([0-9]){1,3})\])))$/;
  return RegExp.test(email);
}

function isValidInteger(n) {
    reg = new RegExp("^[-+]{0,1}[0-9]*$");
    return reg.test(n);
}

function isValidDouble(n) {
    var sep = Number.decimalSeparator;
    reg = new RegExp("^[-+]{0,1}[0-9]*[" + sep + "]{0,1}[0-9]*$");
    return reg.test(n);
}

function isValidTime(n) {
    return !!millisFromHourMinute(n);
}

function isValidDurationDays(n) {
    return !!daysFromString(n);
}

function isValidDurationMillis(n) {
    return !!millisFromString(n);
}

function isValidDurationMillis(n) {
    return !!millisFromString(n);
}


/*
supports almost all Java currency format e.g.: ###,##0.00EUR   €#,###.00  #,###.00€  -$#,###.00  $-#,###.00
 */
function isValidCurrency(numStr){
  //first try to convert format in a regex
  var regex="";
  var format=Number.currencyFormat+"";

  var minusFound=false;
  var numFound=false;
  var currencyString="";
  var numberRegex="[0-9\\"+Number.groupingSeparator+"]+[\\"+Number.decimalSeparator+"]?[0-9]*";

  for (var i=0; i<format.length; i++){
    var ch= format.charAt(i);

    if (ch=="." || ch=="," || ch=="0"){
      //skip it
      if(currencyString!=""){
        regex=regex+"(?:"+RegExp.quote(currencyString)+")?";
        currencyString="";
      }

    } else if (ch=="#") {
      if(currencyString!=""){
        regex=regex+"(?:"+RegExp.quote(currencyString)+")?";
        currencyString="";
      }

      if (!numFound){
        numFound=true;
        regex=regex+numberRegex;
      }

    } else if (ch=="-"){
      if(currencyString!=""){
        regex=regex+"(?:"+RegExp.quote(currencyString)+")?";
        currencyString="";
      }
      if (!minusFound){
        minusFound=true;
        regex=regex+ "[-]?";
      }

    } else {
      currencyString=currencyString+ch;
    }
  }
  if (!minusFound)
    regex="[-]?"+regex;

  if(currencyString!="")
    regex=regex+"(?:"+RegExp.quote(currencyString)+")?";

  regex="^"+regex+"$";

  var rg=new RegExp(regex);
  return rg.test(numStr);
}

function getCurrencyValue(numStr){
  if (isValidCurrency(numStr))
    return NaN;
  return parseFloat(numStr.replace(Number.groupingSeparator,"").replace(Number.decimalSeparator,".").replace(/[^0123456789.]/,""));
}


RegExp.quote = function(str) {
    return str.replace(/([.?*+^$[\]\\(){}-])/g, "\\$1");
};

/* ----- millis format --------- */
/**
* @param         str         - Striga da riempire
* @param         len         - Numero totale di caratteri, comprensivo degli "zeri"
* @param         ch          - Carattere usato per riempire
*/
function pad(str, len, ch){
  if ((str+"").length<len){
    return new Array(len-(''+str).length+1).join(ch) + str;
  } else{
    return str
  }
}

function getMillisInHours(millis) {
  if (!millis)
    return "";
  var sgn=millis>=0?1:-1;
  var hour = Math.floor(millis / 3600000);
  return  (sgn>0?"":"-")+pad(hour,2,"0");  
}
function getMillisInHoursMinutes(millis) {
    if (!millis)
      return "";

    var sgn=millis>=0?1:-1;
    millis=Math.abs(millis);
    var hour = Math.floor(millis / 3600000);
    var min = Math.floor((millis % 3600000) / 60000);
    return  (sgn>0?"":"-")+pad(hour,2,"0") + ":" + pad(min,2,"0");
}

function getMillisInDaysHoursMinutes(millis) {
  if (!millis)
    return "";
    // millisInWorkingDay is set on partHeaderFooter
    var sgn=millis>=0?1:-1;
    millis=Math.abs(millis);
    var days = Math.floor(millis / millisInWorkingDay);
    var hour = Math.floor((millis % millisInWorkingDay) / 3600000);
    var min = Math.floor((millis-days*millisInWorkingDay-hour*3600000) / 60000);
    return (sgn>=0?"":"-")+(days > 0 ? days + "  " : "") + pad(hour,2,"0") + ":" + pad(min,2,"0");
}

function millisFromHourMinute(stringHourMinutes) { //All this format are valid: "12:58" "13.75"  "63635676000" (this is already in milliseconds)
  var result = 0;
  stringHourMinutes.replace(",",".");
  var semiColSeparator = stringHourMinutes.indexOf(":");
  var dotSeparator = stringHourMinutes.indexOf(".");

  if (semiColSeparator < 0 && dotSeparator < 0 && stringHourMinutes.length > 5) {
    return parseInt(stringHourMinutes, 10); //already in millis
  } else {

    if (dotSeparator > -1) {
      var d = parseFloat(stringHourMinutes);
      result = d * 3600000;
    } else {
      var hour = 0;
      var minute = 0;
      if (semiColSeparator == -1)
        hour = parseInt(stringHourMinutes, 10);
      else {
        hour = parseInt(stringHourMinutes.substring(0, semiColSeparator), 10);
        minute = parseInt(stringHourMinutes.substring(semiColSeparator + 1), 10);
      }
      result = hour * 3600000 + minute * 60000;
    }
    if (!result)
      result=NaN;
    return result;
  }
}


/**
 * @param string              "3y 4d", "4D:08:10", "12M/3d", "2H4D", "3M4d,2h", "12:30", "11", "3", "1.5", "2m/3D", "12/3d", "1234"
 *                            by default 2 means 2 hours 1.5 means 1:30
 * @param considerWorkingdays if true day lenght is from global.properties CompanyCalendar.MILLIS_IN_WORKING_DAY  otherwise in 24
 * @return milliseconds. 0 if invalid string
 */
function millisFromString(string,considerWorkingdays) {
  if (!string)
    return undefined;

  var regex = new RegExp("(\\d+[Yy])|(\\d+[M])|(\\d+[Ww])|(\\d+[Dd])|(\\d+[Hh])|(\\d+[m])|(\\d+[Ss])|(\\d+:\\d+)|(:\\d+)|(\\d*[\\.,]\\d+)|(\\d+)","g");

  var matcher = regex.exec(string);
  var totMillis=0;

  if (!matcher)
    return NaN;

  while (matcher!=null) {
    for (var i = 1; i < matcher.length; i++) {
      var match = matcher[i];
      if (match) {
        var number = 0;
        try {
          number = parseInt(match);
        } catch (e) {
        }
        if (i == 1) { // years
          totMillis = totMillis + number * (considerWorkingdays ? millisInWorkingDay * workingDaysPerWeek * 52 : 3600000 * 24 * 365);
        } else if (i == 2) { // months
          totMillis = totMillis + number * (considerWorkingdays ? millisInWorkingDay * workingDaysPerWeek * 4 : 3600000 * 24 * 30);
        } else if (i == 3) { // weeks
          totMillis = totMillis + number * (considerWorkingdays ? millisInWorkingDay * workingDaysPerWeek : 3600000 * 24 * 7);
        } else if (i == 4) { // days
          totMillis = totMillis + number * (considerWorkingdays ? millisInWorkingDay : 3600000 * 24);
        } else if (i == 5) { // hours
          totMillis = totMillis + number * 3600000;
        } else if (i == 6) { // minutes
          totMillis = totMillis + number * 60000;
        } else if (i == 7) { // seconds
          totMillis = totMillis + number * 1000;
        } else if (i == 8) { // hour:minutes
          totMillis = totMillis + millisFromHourMinute(match);
        } else if (i == 9) { // :minutes
          totMillis = totMillis + millisFromHourMinute(match);
        } else if (i == 10) { // hour.minutes
          totMillis = totMillis + millisFromHourMinute(match);
        } else if (i == 11) { // hours
          totMillis = totMillis + number * 3600000;
        }
      }
    }
    matcher=regex.exec(string);
  }

  return totMillis;
}

/**
 * @param string              "3y 4d", "4D:08:10", "12M/3d", "2H4D", "3M4d,2h", "12:30", "11", "3", "1.5", "2m/3D", "12/3d", "1234"
 *                            by default 2 means 2 hours 1.5 means 1:30
 * @param considerWorkingdays if true day lenght is from global.properties CompanyCalendar.MILLIS_IN_WORKING_DAY  otherwise in 24
 * @return milliseconds. 0 if invalid string
 */
function daysFromString(string,considerWorkingdays) {
  if (!string)
    return undefined;

  var regex = new RegExp("(\\d+[Yy])|(\\d+[Mm])|(\\d+[Ww])|(\\d+[Dd])|(\\d*[\\.,]\\d+)|(\\d+)","g");

  var matcher = regex.exec(string);
  var totDays=0;

  if (!matcher)
    return NaN;
  
  while (matcher != null) {
    for (var i = 1; i < matcher.length; i++) {
      var match = matcher[i];
      if (match) {
        var number = 0;
        try {
          number = parseInt(match);
        } catch (e) {
        }
        if (i == 1) { // years
          totDays = totDays + number * (considerWorkingdays ? workingDaysPerWeek * 52 : 365);
        } else if (i == 2) { // months
          totDays = totDays + number * (considerWorkingdays ? workingDaysPerWeek * 4 : 30);
        } else if (i == 3) { // weeks
          totDays = totDays + number * (considerWorkingdays ? workingDaysPerWeek : 7);
        } else if (i == 4) { // days
          totDays = totDays + number;
        } else if (i == 5) { // days.minutes
          totDays = totDays + number;
        } else if (i == 6) { // days
          totDays = totDays + number;
        }
      }
    }
    matcher=regex.exec(string);
  }

  return totDays;
}



/* Object Functions */

function stopBubble(e) {
  if (isNetscape) {
    e.stopPropagation();
    e.preventDefault();
  }
  if (isExplorer) e = event;
  e.cancelBubble = true;
  e.returnValue = false;
  return false;
}

//validation functions - used by textfield
function validateField(e) {
  var rett=true;
   $(this).clearErrorAlert();
  // check serverside only if not empty
  var value = $(this).val();
  if (value) {

    var type = $(this).attr('entryType').toUpperCase();

    if (type == "INTEGER") {
      rett = isValidInteger(value);
    } else if (type == "DOUBLE") {
      rett = isValidDouble(value);
    } else if (type == "PERCENTILE") {
      rett = isValidDouble(value);
    } else if (type == "URL") {
      rett = isValidURL(value);
    } else if (type == "EMAIL") {
      rett = isValidEmail(value);
    } else if (type == "DURATIONMILLIS") {
      rett = isValidDurationMillis(value);
    } else if (type == "DURATIONDAYS") {
      rett = isValidDurationDays(value);
    } else if (type == "DATE") {
      rett = Date.isValid(value);
    } else if (type == "TIME") {
      rett = isValidTime(value);
    } else if (type == "CURRENCY") {
      rett = isValidCurrency(value);
    }

    if (!rett) {
      $(this).createErrorAlert(i18n.ERROR_ON_FIELD,i18n.INVALID_DATA);
    }
  }
  return rett;
}

jQuery.fn.clearErrorAlert= function(){
  this.each(function(){
    var el = $(this);
    el.removeAttr("invalid").removeClass("formElementsError");
    $("#"+el.attr("id")+"error").remove();
  });
  return this;
};

jQuery.fn.createErrorAlert = function(errorCode, message) {
  this.each(function() {
    var el = $(this);
    el.attr("invalid", "true").addClass("formElementsError");
    if ($("#" + el.attr("id") + "error").size() <= 0) {
      var errMess = errorCode + ": " + message;
      var err = "<img width='17' heigh='17' id=\"" + el.attr("id") + "error\" error='1'";
      err += " onclick=\"alert($(this).attr('title'))\" border='0' align='absmiddle'>";
      err=$(err);
      err.attr("title",errMess).attr("src",skinImgPath+"alert.png");
      el.after(err);
    }
  });
  return this;
};


var _messagesFromController; //filled by partHeaderFooter, also templates are loaded there

function showFeedbackMessage(type, message){
  var place=$("#__FEEDBACKMESSAGEPLACE");
  var mess={type:type,message:message};
  place.append($.JST.createFromTemplate(mess,"errorTemplate"));
  place.fadeIn();
  $("body").oneTime(1200,function(){$(".FFC_OK").slideUp();});
}


function showFeedbackMessageInDiv(type, message, divId){
  var place=$("#"+divId);
  var mess={type:type,message:message};
  place.prepend($.JST.createFromTemplate(mess,"errorTemplate"));
  place.fadeIn();
  $("body").oneTime(1200,function(){$(".FFC_OK").slideUp();});
}

function clearFeedbackMessages(type){
  var place=$("#__FEEDBACKMESSAGEPLACE");
  if (type)
    place.find(".FFC_"+type.toUpperCase()).remove();
  else
    place.empty().hide();
}

// button submit support BEGIN ------------------

function saveFormValues(idForm) {
  var formx = obj(idForm);
  formx.setAttribute("savedAction", formx.action);
  formx.setAttribute("savedTarget", formx.target);
  var el = formx.elements;
  for (i = 0; i < el.length; i++) {
    if (el[i].getAttribute("savedValue") != null) {
      el[i].setAttribute("savedValue", el[i].value);
    }
  }
}

function restoreFormValues(idForm) {
  var formx = obj(idForm);
  formx.action = formx.getAttribute("savedAction");
  formx.target = formx.getAttribute("savedTarget");
  var el = formx.elements;
  for (i = 0; i < el.length; i++) {
    if (el[i].getAttribute("savedValue") != null) {
      el[i].value = el[i].getAttribute("savedValue");
    }
  }
}
// button submit support END ------------------


// textarea limit size -------------------------------------------------
function limitSize(ob) {
  if (ob.getAttribute("maxlength")) {
    var ml = ob.getAttribute("maxlength");
    if (ob.value.length > ml) {
      ob.value = ob.value.substr(0, ml);
      alert("ERR_FIELD_MAX_SIZE_EXCEEDED" + ":" + ml);
    }
  }
}

// verify before unload BEGIN ----------------------------------------------------------------------------
function alertOnUnload() {
  if (!muteAlertOnChange) {
    for (var i = 0; i < document.forms.length; i++) {
      var currForm = document.forms[i];
      if ('true' == '' + currForm.getAttribute('alertOnChange')) {
        for (var j = 0; j < currForm.elements.length; j++) {
          var anInput = currForm.elements[j];
          if (!('true' == '' + anInput.getAttribute('excludeFromAlert')) && "" + anInput.getAttribute('oldValue') == "1") {
            var oldValue = (__oldValues__[anInput.id] + "").trim();
            if ($(anInput).attr("maleficoTiny")) {
              if (tinymce.EditorManager.get($(anInput).attr("id")).isDirty()) {
                //alert("isDirty:" + tinymce.EditorManager.get($(anInput).attr("id")).isDirty());
                if (isExplorer) {
                  event.returnValue = i18n.FORM_IS_CHANGED + " \"" + anInput.name + "\"";
                } else {
                  return i18n.FORM_IS_CHANGED + " \"" + anInput.name + "\"";
                }
              }

            } else  if ($(anInput).val().trim() != oldValue) {
              if (isExplorer) {
                event.returnValue = i18n.FORM_IS_CHANGED + " \"" + anInput.name + "\"";
              } else {
                return i18n.FORM_IS_CHANGED + " \"" + anInput.name + "\"";
              }
            }
          }
        }
      }
    }
  }
}

// verify before unload END ----------------------------------------------------------------------------

// ---------------------------------- oldvalues management
var __oldValues__= new Object();


// update all values selected
jQuery.fn.updateOldValue= function(){
  this.each(function(){
    var el = $(this);
    var id = el.attr("name");
    if (!id)
      id=el.attr("id");
    if (id!="")    {
      __oldValues__[id] = el.val();
    }
  });
  return this;
};

// return true if at least one element has changed
jQuery.fn.isValueChanged=function (){
  var ret=false;
  this.each(function(){
    var el = $(this);
    var id = el.attr("name");
    if (!id)
      id=el.attr("id");
    if (id && el.val()+"" != __oldValues__[id] + ""){
      ret=true;
      return false;
    }
  });
  return ret;
};

jQuery.fn.getOldValue=function(){
  var id = $(this).attr("name");
  if (!id)
    id=$(this).attr("id");
  if (id)
    return __oldValues__[id]+"";
  else
    return undefined;
};

// ------ ------- -------- wraps http://www.mysite.com/.......   with <a href="...">
jQuery.fn.activateLinks = function (showImages) {
  var httpRE = /(['"]\s*)?(http[s]?:[\d]*\/\/[^"<>\s]*)/g;
  var wwwRE = /(['"/]\s*)?(www.[^"<>\s]*)/g;
  var imgRE = /(['"]\s*)?(http[s]?:[\d]*\/\/[^"<>\s]*\.(?:gif|jpg|png|jpeg|bmp))/g;


  this.each(function() {
    var el = $(this);
    var html = el.html();

    if (showImages) {
    // workaround for negative look ahead
    html = html.replace(imgRE, function($0, $1) {
        //return $1 ? $0 : "<div class='imgWrap'><a href='" + $0 + "' target='_blank'><img src='" + $0 + "'></a></div>";
        return $1 ? $0 : "<div class='imgWrap'><img src='" + $0 + "' title='"+$0+"'></div>";
    });
    }

    html = html.replace(httpRE, function($0, $1) {
      return $1 ? $0 : "<a href='" + $0 + "' target='_blank'>" + $0 + "</a>";
    });

    html = html.replace(wwwRE, function($0, $1) {
      return $1 ? $0 : "<a href='http://" + $0 + "' target='_blank'>" + $0 + "</a>";
    });


    el.empty().append(html);


    if (showImages) {
      //inject expand capability on images
      el.find("div.imgWrap").each(function () {
        var imageDiv = $(this);


        imageDiv.click(function(e) {
          if(e.ctrlKey) {
            window.open(imageDiv.find("img").attr("src"), "_blank");
          } else {
            var imageClone = imageDiv.find("img").clone();
            imageClone.mouseout(function() {
              $(this).remove();
            });
            imageClone.addClass("imageClone").css({"position":"absolute", "display":"none","top":imageDiv.position().top,"left":imageDiv.position().left,"z-index":1000000});
            imageDiv.after(imageClone);
            imageClone.fadeIn();
          }
        });
      });
    }

  });
  return this;
};

jQuery.fn.emoticonize = function () {
  function convert(text) {
    var faccRE = /(:\))|(:-\))|(:-])|(:-\()|(:\()|(:-\/)|(:-\\)|(:-\|)|(;-\))|(:-D)|(:-P)|(:-p)|(:-0)|(:-o)|(:-O)|(:'-\()|(\(@\))/g;
    return text.replace(faccRE, function(str) {
      var ret = {":)": "smile",
        ":-)": "smile",
        ":-]": "polite_smile",
        ":-(": "frown",
        ":(": "frown",
        ":-/": "skepticism",
        ":-\\": "skepticism",
        ":-|": "sarcasm",
        ";-)": "wink",
        ":-D": "grin",
        ":-P": "tongue",
        ":-p": "tongue",
        ":-o": "surprise",
        ":-O": "surprise",
        ":-0": "surprise",
        ":'-(": "tear",
        "(@)": "angry"}[str];
      if (ret) {
        ret = "<img src='" + imgCommonPath + "smiley/" + ret + ".png' align='absmiddle'>";
        return ret;
      } else
        return str;
    });
  };

  function addBold(text) {
      var returnedValue;
      var faccRE = /\*\*[^*]*\*\*/ig;
      return text.replace(faccRE, function(str) {
          var temp = str.substr(2);
          var temp2 = temp.substr(0, temp.length - 2);
          return "<b>" + temp2 + "</b>";
      });
  };

  this.each(function() {
    var el = $(this);
    var html = convert(el.html());
    html = addBold(html);
    el.html(html);
  });
  return this;
};


// ------------------ jQuery.confirm
$.fn.confirm = function(action, message) {
  if (typeof(action) != "function")
    return;
  this.each(function() {
    var el = $(this);
    var div = $("<div>").addClass("confirmBox").
            html(message ? message : i18n.DO_YOU_CONFIRM);
    div.css({"min-width":el.outerWidth(),"min-height":el.outerHeight()});
    div.oneTime(5000, "autoHide", function() {
      $(this).fadeOut(100, function() {
        el.css({opacity:1});
        $(this).remove();
      });
    });
    var no = $("<span>").addClass("confirmNo")
            .html(i18n.NO).click(function() {
      $(this).parent().fadeOut(100, function() {
        el.css({opacity:1});
        $(this).remove();
      }).stopTime("autoHide");
    });
    var yes = $("<span>").addClass("confirmYes")
            .html(i18n.YES).click(function() {
      $(this).parent().fadeOut(100, function() {
        el.css({opacity:1}).oneTime(1, "doaction", function(){action(el);});
        $(this).remove();
      }).stopTime("autoHide");
    });

    div.append("&nbsp;&nbsp;")
            .append(yes)
            .append("&nbsp;/&nbsp;")
            .append(no);
    el.css({opacity:0}).before(div);

  });

  return this;
};


// ---------------------------------- initialize management
var __initedComponents= new Object();

function initialize(url,includeAsScript){
  var normUrl=url.asId();
  if (!__initedComponents[normUrl]){
    if (!includeAsScript){
      //console.debug(url+" as DOM");
      //var text = getContent(url);
      url= url + (url.indexOf("?")>-1?"&":"?")+buildNumber;
      var text =  $.ajax({
          type:"GET",
          url: url,
          async:false,
          dataType:"html",
          cache:true
         }).responseText;
      $("body").before(text);
    } else{
      $.ajax({type: "GET",
              url: url+"?"+buildNumber,
              dataType: "script",
              async:false,
              cache:true});
    }
    __initedComponents[normUrl]="1";
  }
}

/*
// centralized onload system
*/


$(document).ready(function() {
      window.onbeforeunload = alertOnUnload;
    //again because... something does not work.. RB and MB don't remember..


  $("input[innerLabel][value='']:visible").each(function (i) {
    var theField = $(this);
    theField.wrap("<span style='position:relative;'></span>");
    theField.after("<span id='lbdv_" + this.id + "' class='innerLabel' style='position:absolute;top:-3;left:0;'>" + theField.attr("innerLabel") + "</span>");
    var theSpan = $("#lbdv_" + this.id);
    theSpan.width(theField.outerWidth()).height(theField.outerHeight()).bind('click', function() {
      theField.focus();
    });
    theField.bind('focus', function(e) {
      theSpan.fadeOut(50);
    }).bind('blur', function() {
      if ($(this).val() == "") {
        theSpan.fadeIn();
      }
    });
  });


  $(":input[oldValue]").livequery(function(){$(this).updateOldValue()});

  $('.validated').livequery('blur', validateField);


  // Disable caching of AJAX responses */
  // set default jquery ajax encoding
  $.ajaxSetup({
    cache: false,
    contentType:"application/x-www-form-urlencoded; charset=UTF-8"
  });

});




