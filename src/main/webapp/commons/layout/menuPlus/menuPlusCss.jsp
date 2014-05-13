<%@ page import="org.jblooming.waf.html.layout.Skin, org.jblooming.waf.view.PageState" %><%
  response.setContentType("text/css");
  PageState pageState = PageState.getCurrentPageState();
  Skin skin = pageState.getSkin();
%><style type="text/css">
.menuPlus{
  padding:0;
  margin:0;
}

.menuPlus td{
  padding:0;
  margin:0;
}

.menuShadow{
    padding:2px;
    padding-bottom:0;
    left:-2px;
    top:1px;
}

.menuPlusLabel {

}

.menuPlusContainer{
    background:#fff;
    border:0px solid #267298;
}
.menuPlusContainer .line{
    background-color:transparent;
    width:100%;
}

.menuPlusContainer .line.title {
    background-color:<%=skin.COLOR_BACKGROUND_TOOLBAR%>;
}
.menuPlusContainer .line.title a{
	font-size:14px;
  font-family:Arial,Helvetica,sans-serif;
  font-size:14px;
  color:white;
}
.menuPlusContainer td a, .menuPlusContainer td a:hover{
    text-decoration:none;
    color: #000;
    display:block;
}

.menuPlusContainer td.voice{
    font-family:Arial,Helvetica,sans-serif;
    font-size:12px;
    padding:5px;
}

.menuPlusContainer div.separator{
    background-color:#999;
    font-size:1px;
    line-height:1px;
    height:1px;
    overflow: hidden;
}
.menuPlusContainer td.img{
    font-family:Arial,Helvetica,sans-serif;
    text-align:center;
    font-size:12px;
    color: #c3c3c3;
    background-color:transparent;
    width:24px;
    padding:5px;
}
.menuPlusContainer td.img img{
    width:20px;
}
.menuPlusContainer .textBox{
    padding: 10px;
    font-family:Arial,Helvetica,sans-serif;
    font-size:12px;
    background:dimgray;
    color: #c3c3c3;
}
.menuPlusContainer .selected td{
    background:url( "<%=skin.imgPathPlus%>/toolBar/voice_bgnd.png" )
}
.menuPlusContainer .selected td a{
    bcolor:#fff;
}
.menuPlusContainer .disabled td, .menuPlusContainer .disabled td a {
    color: #cccccc;
}
.menuPlusContainer .subMenuOpener{
    background-image:url("<%=skin.imgPathPlus%>/toolBar/menuArrow.gif");
    background-repeat:no-repeat;
    background-position:right;
}
.menuPlusContainer .selected .subMenuOpener{
    background-image:url("<%=skin.imgPathPlus%>/toolBar/menuArrow.gif");
    background-repeat:no-repeat;
    background-position:right;
}


.menuContainer {
border-top: 1px solid <%=skin.COLOR_TEXT_MENU%>;
border-bottom: 1px solid <%=skin.COLOR_TEXT_MENU%>;
border-left: 1px solid <%=skin.COLOR_TEXT_MENU%>;
border-right: 1px solid <%=skin.COLOR_TEXT_MENU%>;
}
  
</style>
