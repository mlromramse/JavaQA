<%@ page import="org.jblooming.waf.SessionState, org.jblooming.waf.html.layout.HtmlColors, org.jblooming.waf.html.layout.Skin" %>
<%

  SessionState sessionState = SessionState.getSessionState(request);
  Skin skin = sessionState.getSkin();

  out.println("/*");
%>
<style type="text/css"><%
  out.println("*/");%>



  /*
  -------------------------------------------------------
  body styles
  -------------------------------------------------------
  */
BODY, TBODY {
  font-family: arial;
  font-size: 14px;
  margin: 0;
  color: <%=skin.COLOR_TEXT_MAIN%>;
  text-decoration: none;
}


.underline {
  border-bottom: 1px solid <%=skin.COLOR_BACKGROUND_TOOLBAR%>;
}

.descrEl {
  font-size: 11px;
}

.highlight {
  background-color: yellow
}

.footerWarning {
  text-align: center;
  border-top: 1px solid <%=skin.COLOR_BACKGROUND_TITLE%>;
  border-bottom: 1px solid <%=skin.COLOR_BACKGROUND_TITLE%>;
  padding:10px 0;
  font-size: 16px;
}

#twMainContainer {
  width:1230px;
  margin: 0 auto
}

#twInnerContainer {
  background-color:<%=skin.COLOR_BACKGROUND_MAIN%>;
  border-radius:10px;
  -moz-border-radius:10px;
  -webkit-border-radius:10px;
  -o-border-radius:10px;
  box-shadow: 6px 6px 6px #778d8f;
  -moz-box-shadow: 6px 6px 6px #778d8f;
  -webkit-box-shadow: 6px 6px 6px #778d8f;
  -o-box-shadow: 6px 6px 6px #778d8f;
  box-sizing:border-box;
  -moz-box-sizing:border-box;
  -webkit-box-sizing:border-box;
  -o-box-sizing:border-box;
  padding:15px
}

.mainColumn {
  margin-right:300px
}

.rightColumn {
  width:250px;
  float:right
}


/*
  -------------------------------------------------------
  teamwork icon
  -------------------------------------------------------
  */


.teamworkIcon {
  font-family: 'TeamworkRegular', arial, sans-serif;
  color:#617777;
  padding-right:5px;
  font-weight:normal;
  font-size:120%
}

  /*
  -------------------------------------------------------
  head bar
  -------------------------------------------------------
  */

.headBar {
  text-align:right;
  margin-bottom:20px
}


.headBar button.textual {
  font-size:18px;
  margin-left:10px

}

.divomo {

  background-color: #fff;
  border: 1px solid #a2c1c3;
  box-shadow:3px 3px 3px #999;
  -moz-box-shadow:3px 3px 3px #999;
  -webkit-box-shadow:3px 3px 3px #999;
  -o-box-shadow:3px 3px 3px #999;
  border-radius:10px;
  -moz-border-radius:10px;
  -webkit-border-radius:10px;
  -o-border-radius:10px;
  padding:10px;

}
.divomo:before {
  position:absolute;
  left:20px;
  top:-13px;
  content:url('<%=skin.imgPathPlus%>/menuArrow.png');
  

}

.divomo button {
  display:block;
  font-size:16px;
  line-height:30px;
  color:#000;
  border-bottom:1px solid #d4d4d4;
  text-align:left;
  margin:0;
  padding:0 5px;
  width:100%;
  box-sizing:border-box;
  -moz-box-sizing:border-box;
  -webkit-box-sizing:border-box;
  -o-box-sizing:border-box;
}

/*
  -------------------------------------------------------
  path to object
  -------------------------------------------------------
  */


.pathToObject {
  font-size: 16px;
}

.pathToObject .currentNode {
  font-size: 32px;
  font-weight: normal;
  padding:0 0 20px 0
}


  /*
  -------------------------------------------------------
  side bar
  -------------------------------------------------------
  */

.sideBar {
   padding: 0 0 20px 0
}

 .sideBar button.textual {
   font-size: 22px;
   line-height:34px;
   border-bottom:1px solid #d4d4d4;
   display:block;
   width:100%;
   text-align:left;
   color:#009E94
}

.sideBar button:hover.textual {
  color:#617777
 }

.buttonBox, .buttonBoxInline {
  border:1px solid #d1d1d1;
  padding:20px;
  margin:0 0 20px 0
}

.buttonBox  button.textual, .buttonBoxInline  button.textual {
   font-size: 14px;
   line-height:24px;
   display:block;
   text-align:left;
   color:#617777
}

.buttonBoxInline  button.textual {
   font-size: 14px;
   line-height:24px;
   display:inline-block;
   text-align:left;
   color:#617777
}



.buttonBox  .separator {
  border-bottom:1px solid #91B4B7;
  margin-bottom:5px;
  font-size:9px
}

.buttonBox  .deleteSmall {
  cursor:pointer;
  float:left;
  line-height: 24px;
}




.saveFilter {
  float:left;
  color: #617777;
  cursor:pointer
}

.filterName {
  float:left;

}


.estIss, .wlg {
  font-size:20px;
  font-weight:normal
}

.headerTable {
  color:#fff;
  text-align:center;
  padding:5px;
  background-color:#009E94;
  border-radius: 5px 5px 0 0;
  -moz-border-radius: 5px 5px 0 0;
  -webkit-border-radius: 5px 5px 0 0;
  -o-border-radius: 5px 5px 0 0;
}


.graph {
  background: url(<%=skin.imgPathPlus%>/graph.png) no-repeat left center;
}

.facesBox {
  text-align:right;
  padding:5px
}

.face {
  width:40px;
  height:40px;
  border:1px solid #e5e5e5;
  margin:2px;
  
}

.face.small {
  width:25px;
  height:25px;

}

.percentileLabel {
  font-weight:bold;
  padding-left:5px
}

.codeValueChooserLine {
  padding: 0 10px;
  box-sizing:border-box;
  -moz-box-sizing:border-box;
  -webkit-box-sizing:border-box;
  -o-box-sizing:border-box;
}

a.faceMore {
  width:40px;
  height:40px;
  border:none;
  padding:2px;
  display:inline-block;
}

a.faceMore:hover {
  text-decoration:none;
}

a.faceMore:hover span {
  color:#fff
}

.summary {
  font-size:12px;
  color:#666;
  margin-bottom:5px
}

.period {
  font-size:16px;
  color:#8A8C8F;
  padding-top:10px
}

.period span.date {
  color:#000;
}
.period span.error {
  color:#B61E2D;
}


.listData {
  padding:10px 25px;
  position:relative;
}

.listData h3 {
  font-size:12px;
  padding:5px 0;
  color:#617777;
  font-style:italic;
}







  /*
  -------------------------------------------------------
  container styles
  -------------------------------------------------------
  */

.container {
  position:relative;
  border: 1px solid #D1D1D1;
  margin: 0 0 20px;
  padding: 0 20px 20px;
}

.container[status=HIDDEN]{
  display:none;
}


.container.warn {
  border: 2px solid <%=skin.COLOR_WARNING%>;
  background-color:#fff;
  padding: 0 10px 10px;
}

.container.draggable,.container.centeredOnScreen,.container.absolutePosition{
  position:absolute;
}
.container.centeredOnScreen{
  display:none;
}

.container.draggable > .containerTitle{
  cursor:move;
}

.container .stsButtons img{
  display:none;
}

.container .stsButtons img{
  cursor:pointer;
}

.container.collapsable[status=COLLAPSED] > .containerBody,
.container.collapsable[status=COLLAPSED] >.containerTitle>.stsButtons> img.stsCollapse{
  display:none;
}

.container.collapsable[status=DEFAULT]>.containerTitle>.stsButtons> img.stsRestore{
  display:none;
}

.container.collapsable[status=DEFAULT]>.containerTitle>.stsButtons> img.stsCollapse{
  display:inherit;
}
.container.collapsable[status=COLLAPSED]>.containerTitle>.stsButtons> img.stsRestore{
  display:inherit;
}


 .container.level_1 {
  background-color: #fff;
  margin: 0;
  padding: 20px;
  border:none
}

 .container.level_2 {
  background-color: #E3EDED;
  margin: 0;
  padding: 20px;
  border:none
}

.container.level_3 {
  border: 1px solid #D1D1D1;
  margin: 0;
  padding: 0 20px 20px;
}

.container.level_3 td {
  color:#617777
}



.containerTitle {
  padding: 6px 0 6px 0;
  font-size: 24px;
  height: 30px;
  font-weight: normal;
  color: <%=skin.COLOR_BACKGROUND_TITLE%>;
  cursor:default;
}


.containerTitle.level_1 {
  font-size: 20px;
  font-weight: normal;
  color:#617777;

}

.containerTitle.level_2 {
  font-size: 12px;
  font-weight: bold;
  color: <%=skin.COLOR_TEXT_TITLE02%>;
  background-color: <%=skin.COLOR_BACKGROUND_TITLE02%>;
}

.containerTitle.level_3 {
  font-size: 10px;
  font-weight: bold;
  height: 10px;
  color: <%=skin.COLOR_TEXT_TITLE02%>;
  background-color: <%=skin.COLOR_BACKGROUND_MAIN%>;
}

.containerTitle.thin {
  font-size: 10px;
  font-weight: bold;
}

.containerTitle.Light {
  font-size: 11px;
  font-weight: bold;
  background-color: <%=skin.COLOR_BACKGROUND_TITLE01%>;
}

.containerTitle.warn {
  font-weight: bold;
  color: <%=skin.COLOR_WARNING%>;
  font-size:16px
}

.containerTitle a {
  text-decoration: none;
  color: <%=skin.COLOR_BACKGROUND_MAIN%>;
}

.containerTitle a:hover {
  text-decoration: none;
  color: <%=skin.COLOR_TEXT_LINK%>;
}

.containerTitle .titleIcon,
.containerTitle .title{
  display:inline-block;
  margin-left:3px;
  white-space:nowrap;
  float:left;
}


.containerTitle .titleRight{
  display:inline-block;
  margin-right:3px;
  white-space:nowrap;
  float:right;
}

.containerTitle .stsButtons{
  position:absolute;
  display:inline-block;
  right:3px;
  white-space:nowrap;
}

.containerBody {
  position:relative;
  width:100%;
  height:100%;
}


.ui-resizable { position: relative;}
.ui-resizable-handle { position: absolute;font-size: 0.1px;z-index: 99999; display: block; }
.ui-resizable-disabled .ui-resizable-handle, .ui-resizable-autohide .ui-resizable-handle { display: none; }
.ui-resizable-n { cursor: n-resize; height: 7px; width: 100%; top: -5px; left: 0; }
.ui-resizable-s { cursor: s-resize; height: 7px; width: 100%; bottom: -5px; left: 0; }
.ui-resizable-e { cursor: e-resize; width: 7px; right: -5px; top: 0; height: 100%; }
.ui-resizable-w { cursor: w-resize; width: 7px; left: -5px; top: 0; height: 100%; }
.ui-resizable-se { cursor: se-resize; width: 12px; height: 12px; right: 1px; bottom: 1px; }
.ui-resizable-sw { cursor: sw-resize; width: 9px; height: 9px; left: -5px; bottom: -5px; }
.ui-resizable-nw { cursor: nw-resize; width: 9px; height: 9px; left: -5px; top: -5px; }
.ui-resizable-ne { cursor: ne-resize; width: 9px; height: 9px; right: -5px; top: -5px;}


  /*
  -------------------------------------------------------
  table styles
  -------------------------------------------------------
  */

.table {
  width: 100%;
}

TH, .tableHeader {
  font-weight: bold;
  color: <%=skin.COLOR_TEXT_TITLE%>;
  border: none;
  background-color: #13AFA5;
}

TH a {
  color: <%=skin.COLOR_TEXT_TITLE%>;
}

.tableContent {
  background-color: <%=skin.COLOR_BACKGROUND_MAIN%>;
}

tr.alternate:nth-child(even) {
background-color: <%=HtmlColors.modifyLight(skin.COLOR_BACKGROUND_MAIN,-20)%> ;
}
tr.alternate:nth-child(odd) {
background-color: <%=skin.COLOR_BACKGROUND_MAIN%> ;
}

tr[rlovr]:hover {
  background-color: <%=HtmlColors.modifyLight(skin.COLOR_BACKGROUND_MAIN,-40,-40,-40)%>;
}

.tableBackground {
  background-color: <%=skin.COLOR_BACKGROUND_TOOLBAR%>;
}

tr.draggingtr td {
  background-color: <%=skin.COLOR_BACKGROUND_TITLE02%>;
}


  /*
  -------------------------------------------------------
  Buttons
  -------------------------------------------------------
  */

button {
  font-size:100%;
  font-family:Arial, sans-serif;
  color:#fff;
  cursor:pointer;
  background-color:#91B4B7;
   -moz-box-shadow:2px 2px 2px #999;
   -webkit-box-shadow:2px 2px 2px #999;
   -o-box-shadow:2px 2px 2px #999;
   box-shadow:2px 2px 2px #999;
   -moz-border-radius:2px;
   -webkit-border-radius:2px;
   -o-border-radius:2px;
   border-radius:2px;
   border:none;
   padding:7px 10px;
   margin-bottom:10px;
   margin-right:10px;
   -moz-box-sizing:border-box;
   -webkit-box-sizing:border-box;
   -o-box-sizing:border-box;
   box-sizing:border-box;
}

button[disabled] {
  cursor: default;
  background-color:#BABABA
}

button:hover[disabled] {
  background-color:#BABABA
}

button.textual, button.buttonImg {
  border: none;
  background-color: transparent;
  -moz-box-shadow:0 0 0 #999;
   -webkit-box-shadow:0 0 0 #999;
   -o-box-shadow:0 0 0 #999;
   box-shadow:0 0 0 #999;
  -moz-border-radius:0;
   -webkit-border-radius:0;
   -o-border-radius:0;
   border-radius:0;
  padding:0;
  margin:0
}

button.focused {
  font-weight: bolder;
}

button.edit {
  color:#009E94;
  padding:0;
  margin:0
}

button.delete {
  color:#B61E2D;
  padding:0;
  margin:0
}

button:hover {
  background-color: #6D9DA0;
}
button.textual:hover {
  background-color: transparent;
}

.buttonArea {
  background-color: #E3EDED;
  padding: 5px;
}

.buttonArea  button.textual {
   display:inline-block;
}

  /*
  * small button
  */
button.smallButton {
  font-size: 13px;
}

.smallButton .separator {
  background: url(../images/separator.png) repeat-y center;
  display: inline-block;
  width: 25px;
}

button.big {
    font-size:22px;
  }

button.full {
    width:100%
  }



 button.first {
     background-color:#13afa5;
   }


button.first:hover {
     color:#fff;
     background-color:#019188;
   }

.buttonArea button{
  display:inline-block;
}

.bbLoggedInfo {
  font-size:12px;
  color: #91B4B7;
  text-align:left
}

span.separator {
  display: inline-block;
}

  /*--------------------------------------------
  Ribbonbar
  ---------------------------------------------*/
.ribbonbar {
  padding-top: 2px;
  padding-left: 10px;
  margin: 0;
  height: 30px;
  vertical-align: middle;
}

.ribbonbar button {
  font-size: 13px;
  margin: 2px;
  border: none;
}

.ribbonbar hr {
  height: 1px;
  padding: 0;
  background-color: <%=skin.COLOR_TEXT_TITLE01%>;
  border: none;
}

  /*
  -------------------------------------------------------
  form elements
  -------------------------------------------------------
  */
form {
  margin: 0;
  padding: 0;
}

select {
  border: 1px solid #cccccc;
  font-family: Arial, Helvetica, sans-serif;
  font-size: 18px;
}

.searchInput {
  border:none;
  border-radius:5px;
  -moz-border-radius:5px;
  -webkit-border-radius:5px;
  -o-border-radius:5px;
  padding:0 5px;
  margin:10px 0 0;
  width:200px;
  height:28px
}

.buttonImg.searchButton {
  background: url("<%=skin.imgPathPlus%>/lens.gif") no-repeat;
  height: 28px;
  margin-left: -35px;
  width: 25px;
  border:none
}


.formElements {
  padding: 4px;
  font-size: 16px;
  border:1px solid #ccc;

  font-family: Arial, Helvetica, sans-serif;
  box-sizing:border-box;
  -moz-box-sizing:border-box;
  -webkit-box-sizing:border-box;
  -o-box-sizing:border-box;
  position: relative;
}

.enrollField {
  right: 8px;
  margin-top: 8px;
}

.formElementsError {
  border: 1px solid #ff0000;
  padding: 2px 2px;
  font-size: 12px;
}

.formElementsError + img {
  position: absolute;
  right: 7px;
  margin-top: 6px;
}

.errImg{
  position: absolute;
  right: 8px;
  top: 3px;
}

.formElementExclamation {
  width: 15px;
  height: 25px;
  mmmargin-left: -20px;
  mmmposition: absolute;
  background: url("../images/alert.png") no-repeat;
}


.formElementsWarning {
  border: 1px solid #FB7000;
  font-family: Arial;
  padding: 2px 2px;
  font-size: 10pt;
  background-color: #FB7000;
  color: white;
}

.inputAlert {
  border: 1px solid #FF6600;
  margin: 0 0 10px 0;

}

.paginator {
  margin: 0 0 10px 0;
}

.paginator td {
  font-size: 11px;
  color: #617777;
}

.paginator input {
  border: 1px solid #cccccc;
  margin: 0;
}


textarea.formElements {
  color: #3b3b3b;
  font-size: 13px;

}

span#FLD_LOGIN_NAMEerror, span#FLD_PWDerror {
  margin-left: -23px;
  margin-top: 2px;
}

.qbe, .time, .integer, .double, .percentile, .currency, .durationmillis, .durationdays {
  padding-left: 12px;
  background-repeat: no-repeat;
  background-position: 0 0;
}

.qbe {
  background-image: url('../images/qbe.png');
}

.time {
  background-image: url('../images/time.png');
}

.durationmillis {
  background-image: url('../images/durationmillis.png');
}

.durationdays {
  background-image: url('../images/durationdays.png');
}

.integer {
  background-image: url('../images/integer.png');
}

.double {
  background-image: url('../images/double.png');
}

.percentile {
  background-image: url('../images/percentile.png');
}

.currency {
  background-image: url('../images/currency.png');
}

.grayed {
  background-color: #f3f3f3
}

/* Do NOT change anything!!!!!!!!! */
.innerLabel {
  position: absolute;
  color: #999999;
  text-align: center;
  font-size: 12px;
  font-style: italic;
  vertical-align: middle;
  overflow: hidden;
  margin-top: 3px;
}

  /*
  -------------------------------------------------------
  tab elements
  -------------------------------------------------------

  */

.tabSelected {
  font-weight: bold !important;
  border-bottom: 1px solid #fff;
}

.tabUnselected {
  color: #cccccc;
  border-bottom: 1px solid #cccccc;
  opacity: .90;
  -khtml-opacity: 0.5 filter: alpha(opacity = 90);

}

.tabUnselected .tabImg {
  opacity: .50;
  filter: alpha(opacity = 50);
}

.tabDisabled {
  color: #333333;
  border-bottom: 1px solid #cccccc;
  opacity: .4;
  filter: alpha(opacity = 40);
}

.tabDisabled .tabImg {
  opacity: .30;
  filter: alpha(opacity = 30);
}

.tabDisabled td a {
  color: #969696;
}

.tabUnselected td a {
  color: #8F8F8F;
}

.tabContainer {
  background-color: <%=skin.COLOR_BACKGROUND_MAIN%>;
  border: 1px solid #cccccc;
  border-top: 0px solid #cccccc;
  border-left: 1px solid #999999;
  border-bottom: 1px solid #999999;
}

.footer {
  text-align: right;
}

  /*
  -------------------------------------------------------
  drag
  ------------------------------------------------------

  */
.draggable, .draggable td {
  font: normal 11px Verdana, Arial, Helvetica, sans-serif;
  color: black;
  border-bottom: none;
}

.draggableOver, .draggableOver td {
  font: normal 11px Verdana, Arial, Helvetica, sans-serif;
  color: gray;
  border-bottom: 1px solid gray;
}

.droppingEl, .droppingEl td {
  font: normal 11px Verdana, Arial, Helvetica, sans-serif;
  color: #000000;
  opacity: .5;
  filter: alpha(opacity = 50);
  border-bottom: 1px solid gray;
}

.dragHandler {
  background-color: #E7E7E7;
  cursor: move;
  background-image: url(../images/grip.png);
  background-position: left;
  background-repeat: repeat-y;
  border: 2px solid #E7E7E7
}

  /*
  PORTLETS
  */

.portletParams {
/*position:absolute;*/
  background-color: <%=skin.COLOR_BACKGROUND_TITLE01%>;
  background-color: #eee;
  border: 1px solid gray;
  padding: 5px;
  -webkit-border-radius: 5px;
  -moz-border-radius: 5px;
}

.wlDayNav {
  margin-top: 5px;
  margin-bottom: 5px;
  -moz-border-radius: 7px;
  -webkit-border-radius: 7px;
  -o-border-radius: 7px;
  border-radius: 7px;
}

.score {
  color:#F2A740;
  font-size:16px;
  line-height:25px;
  background-color: #fff;
  padding: 0;
  height:25px;
  width:25px;
  text-align:center;
  border:1px solid #fff

}

.scoreSmall {
  color:#617777;
  font-size:13px;

}

.FFC_Global button {
  background-color: #333;
  border: 0;
  padding: 2px;
  color: white;
  margin: 3px;
  -moz-border-radius: 3px;
  -webkit-border-radius: 3px;
}

.FFC_Global button:hover {
  background-color: #666;
}

.FFC_ERROR table {
  border: 1px solid #ff0000;
  background-color: #ffc8c8
}

.FFC_WARNING table {
  border: 1px solid #FF9900;
  background-color: #ffffc8
}

.FFC_INFO table {
  border: 1px solid #000099;
  background-color: #c8c8ff
}

.FFC_OK table {
  border: 1px solid #00a000;
  background-color: #c8ffc8
}

.labelPart {
  -moz-border-radius: 3px;
  -webkit-border-radius: 3px
}

.labelPart table {
  -moz-border-radius: 3px;
  -webkit-border-radius: 3px;
}

.labelPart {
  border: 2px solid #D4D0C8;
  padding: 3px
}

.loadCell .loadPerc span {
  background-color: #fff;
  -moz-border-radius: 3px;
  -webkit-border-radius: 3px;
}

.confirmBox{
  display:inline-block;
  z-index:10000;
  vertical-align:middle;
  text-align:center;
  color: #8f8f8f;
  font-size: 12px;
  margin-top: -5px;
  padding: 5px 10px!important
}
.confirmBox .confirmNo{
  color: #c95757;
  cursor:pointer;
  font-weight:bolder;
}
.confirmBox .confirmYes{
color: #5d994d;
cursor:pointer;
  font-weight:bolder;
}

<%
    out.println("/*");
%>
</style><%
  out.println("*/");%>
