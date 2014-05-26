<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt_rt" prefix="fmt_rt"%>
<jsp:useBean id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/>

<%
	  String  language = loginbean.getUserPreferences().getLanguage();
		%>
<fmt_rt:setLocale value="<%=language%>"/>
	<fmt:setBundle basename="Lang" var="lang" scope="application"/>

<html>
<head>
<title>Untitled Document</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
</head><style>
.heading {
	FONT-SIZE: 75%; FONT-FAMILY: verdana
}
.blurb {
	FONT-SIZE: 70%; FONT-FAMILY: verdana
}
.body {
	LINE-HEIGHT: 130%; FONT-FAMILY: verdana
}
.smheading {
	FONT-WEIGHT: bold; FONT-SIZE: 70%; FONT-FAMILY: verdana
}
A.definition {
	COLOR: #ff6600; TEXT-DECORATION: none
}
.headcolor {
	COLOR: #3366cc
}
UL {
	MARGIN-TOP: 0pt
}
A:hover {
	COLOR: #0099ff
}
.chartlabel {
	FONT-SIZE: 75%
}
.cancelvlink {
	COLOR: #0033cc
}
.sublink {
	FONT-SIZE: 70%; LINE-HEIGHT: 150%
}
.subtext {
	FONT-SIZE: 70%
}
.price {
	FONT-SIZE: 11px; FONT-FAMILY: verdana
}
.order {
	FONT-SIZE: 10px; FONT-FAMILY: verdana
}
.blacklink {
	COLOR: #000000
}
.hilitelink {
	COLOR: #339933
}
FONT.noul A {
	COLOR: #3366cc; TEXT-DECORATION: none
}
.tblMatrix {
	BORDER-LEFT-COLOR: #999999; BORDER-BOTTOM-COLOR: #999999; BORDER-TOP-COLOR: #999999; BORDER-RIGHT-COLOR: #999999
}
.tblHeader {
	FONT-WEIGHT: bold; FONT-SIZE: 75%; FONT-FAMILY: verdana, helvetica, sans-serif; BACKGROUND-COLOR: #ffcc66
}
.tblColOne {
	FONT-WEIGHT: bold; FONT-SIZE: 70%; FONT-FAMILY: verdana, helvetica, sans-serif; BACKGROUND-COLOR: #fffaea
}
.tblColOneTrans {
	FONT-WEIGHT: bold; FONT-SIZE: 70%; FONT-FAMILY: verdana, helvetica, sans-serif
}
.tblData {
	FONT-SIZE: 70%; FONT-FAMILY: verdana, helvetica, sans-serif
}
.LeftNavOff {
	BORDER-RIGHT: #3366cc 1px solid; PADDING-RIGHT: 3px; BORDER-TOP: #3366cc 1px solid; PADDING-LEFT: 10px; FONT-WEIGHT: bold; FONT-SIZE: 10px; PADDING-BOTTOM: 0px; MARGIN: 2px 0px; BORDER-LEFT: #3366cc 1px solid; WIDTH: 80px; CURSOR: hand; COLOR: #ffffff; LINE-HEIGHT: 20px; PADDING-TOP: 0px; BORDER-BOTTOM: #3366cc 1px solid; FONT-FAMILY: verdana; LETTER-SPACING: -0.5pt; BACKGROUND-COLOR: #3366cc; TEXT-DECORATION: none
}
.LeftNavUp {
	BORDER-RIGHT: #000066 1px solid; PADDING-RIGHT: 3px; BORDER-TOP: #33ccff 1px solid; PADDING-LEFT: 10px; FONT-WEIGHT: bold; FONT-SIZE: 11px; PADDING-BOTTOM: 0px; MARGIN: 2px 0px; BORDER-LEFT: #33ccff 1px solid; WIDTH: 164px; CURSOR: hand; COLOR: #ffffff; LINE-HEIGHT: 20px; PADDING-TOP: 0px; BORDER-BOTTOM: #000066 1px solid; FONT-FAMILY: verdana; LETTER-SPACING: -0.5pt; HEIGHT: 0pt; BACKGROUND-COLOR: #3366cc; TEXT-DECORATION: none
}
.LeftNavDown {
	BORDER-RIGHT: #99ccff 1px solid; PADDING-RIGHT: 3px; BORDER-TOP: #003399 1px solid; PADDING-LEFT: 10px; FONT-WEIGHT: bold; FONT-SIZE: 11px; PADDING-BOTTOM: 0px; MARGIN: 2px 0px; BORDER-LEFT: #003399 1px solid; WIDTH: 164px; CURSOR: hand; COLOR: #ffffff; LINE-HEIGHT: 20px; PADDING-TOP: 0px; BORDER-BOTTOM: #99ccff 1px solid; FONT-FAMILY: verdana; LETTER-SPACING: -0.5pt; BACKGROUND-COLOR: #3366cc; TEXT-DECORATION: none
}
.LeftNavOn {
	BORDER-RIGHT: #99ccff 1px solid; PADDING-RIGHT: 3px; BORDER-TOP: #003399 1px solid; PADDING-LEFT: 10px; FONT-WEIGHT: bold; FONT-SIZE: 11px; PADDING-BOTTOM: 0px; MARGIN: 2px 0px; BORDER-LEFT: #003399 1px solid; WIDTH: 164px; LINE-HEIGHT: 20px; PADDING-TOP: 0px; BORDER-BOTTOM: #99ccff 1px solid; FONT-FAMILY: verdana; LETTER-SPACING: -0.5pt; BACKGROUND-COLOR: #ffa500; TEXT-DECORATION: none
}
.LeftNavChosen {
	BORDER-RIGHT: #3366cc 1px solid; PADDING-RIGHT: 3px; BORDER-TOP: #3366cc 1px solid; PADDING-LEFT: 1px; FONT-WEIGHT: bold; FONT-SIZE: 11px; PADDING-BOTTOM: 0px; MARGIN: 2px 0px; BORDER-LEFT: #3366cc 1px solid; WIDTH: 164px; CURSOR: hand; COLOR: #ffcc00; LINE-HEIGHT: 20px; PADDING-TOP: 0px; BORDER-BOTTOM: #3366cc 1px solid; FONT-FAMILY: verdana; LETTER-SPACING: -0.5pt; BACKGROUND-COLOR: #3366cc; TEXT-DECORATION: none
}
.LeftNavChosenUp {
	BORDER-RIGHT: #000066 1px solid; PADDING-RIGHT: 3px; BORDER-TOP: #33ccff 1px solid; PADDING-LEFT: 1px; FONT-WEIGHT: bold; FONT-SIZE: 11px; PADDING-BOTTOM: 0px; MARGIN: 2px 0px; BORDER-LEFT: #33ccff 1px solid; WIDTH: 164px; CURSOR: hand; COLOR: #ffcc00; LINE-HEIGHT: 20px; PADDING-TOP: 0px; BORDER-BOTTOM: #000066 1px solid; FONT-FAMILY: verdana; LETTER-SPACING: -0.5pt; HEIGHT: 0pt; BACKGROUND-COLOR: #3366cc; TEXT-DECORATION: none
}
.LeftNavChosenDown {
	BORDER-RIGHT: #99ccff 1px solid; PADDING-RIGHT: 3px; BORDER-TOP: #003399 1px solid; PADDING-LEFT: 5px; FONT-WEIGHT: bold; FONT-SIZE: 11px; PADDING-BOTTOM: 0px; MARGIN: 2px 0px; BORDER-LEFT: #003399 1px solid; WIDTH: 164px; CURSOR: hand; COLOR: #ffcc00; LINE-HEIGHT: 20px; PADDING-TOP: 0px; BORDER-BOTTOM: #99ccff 1px solid; FONT-FAMILY: verdana; LETTER-SPACING: -0.5pt; BACKGROUND-COLOR: #3366cc; TEXT-DECORATION: none
}
.LeftNavSubOff {
	BORDER-RIGHT: #3366cc 1px solid; PADDING-RIGHT: 3px; BORDER-TOP: #3366cc 1px solid; PADDING-LEFT: 30px; FONT-SIZE: 11px; PADDING-BOTTOM: 0px; MARGIN: 2px 0px; BORDER-LEFT: #3366cc 1px solid; WIDTH: 164px; CURSOR: hand; COLOR: #ffffff; LINE-HEIGHT: 20px; PADDING-TOP: 0px; BORDER-BOTTOM: #3366cc 1px solid; FONT-FAMILY: verdana; LETTER-SPACING: -0.5pt; BACKGROUND-COLOR: #3366cc; TEXT-DECORATION: none
}
.LeftNavSubUp {
	BORDER-RIGHT: #000066 1px solid; PADDING-RIGHT: 3px; BORDER-TOP: #33ccff 1px solid; PADDING-LEFT: 30px; FONT-SIZE: 11px; PADDING-BOTTOM: 0px; MARGIN: 2px 0px; BORDER-LEFT: #33ccff 1px solid; WIDTH: 164px; CURSOR: hand; COLOR: #ffffff; LINE-HEIGHT: 20px; PADDING-TOP: 0px; BORDER-BOTTOM: #000066 1px solid; FONT-FAMILY: verdana; LETTER-SPACING: -0.5pt; BACKGROUND-COLOR: #3366cc; TEXT-DECORATION: none
}
.LeftNavSubDown {
	BORDER-RIGHT: #99ccff 1px solid; PADDING-RIGHT: 3px; BORDER-TOP: #003399 1px solid; PADDING-LEFT: 30px; FONT-SIZE: 11px; PADDING-BOTTOM: 0px; MARGIN: 2px 0px; BORDER-LEFT: #003399 1px solid; WIDTH: 164px; CURSOR: hand; COLOR: #ffffff; LINE-HEIGHT: 20px; PADDING-TOP: 0px; BORDER-BOTTOM: #99ccff 1px solid; FONT-FAMILY: verdana; LETTER-SPACING: -0.5pt; BACKGROUND-COLOR: #3366cc; TEXT-DECORATION: none
}
.LeftNavSubChosen {
	BORDER-RIGHT: #3366cc 1px solid; PADDING-RIGHT: 3px; BORDER-TOP: #3366cc 1px solid; PADDING-LEFT: 22px; FONT-SIZE: 11px; PADDING-BOTTOM: 0px; MARGIN: 2px 0px; BORDER-LEFT: #3366cc 1px solid; WIDTH: 164px; CURSOR: hand; COLOR: #ffcc00; LINE-HEIGHT: 20px; PADDING-TOP: 0px; BORDER-BOTTOM: #3366cc 1px solid; FONT-FAMILY: verdana; LETTER-SPACING: -0.5pt; BACKGROUND-COLOR: #3366cc; TEXT-DECORATION: none
}
.LeftNavSubChosenUp {
	BORDER-RIGHT: #000066 1px solid; PADDING-RIGHT: 3px; BORDER-TOP: #33ccff 1px solid; PADDING-LEFT: 22px; FONT-SIZE: 11px; PADDING-BOTTOM: 0px; MARGIN: 2px 0px; BORDER-LEFT: #33ccff 1px solid; WIDTH: 164px; CURSOR: hand; COLOR: #ffcc00; LINE-HEIGHT: 20px; PADDING-TOP: 0px; BORDER-BOTTOM: #000066 1px solid; FONT-FAMILY: verdana; LETTER-SPACING: -0.5pt; HEIGHT: 0pt; BACKGROUND-COLOR: #3366cc; TEXT-DECORATION: none
}
.LeftNavSubChosenDown {
	BORDER-RIGHT: #99ccff 1px solid; PADDING-RIGHT: 3px; BORDER-TOP: #003399 1px solid; PADDING-LEFT: 22px; FONT-SIZE: 11px; PADDING-BOTTOM: 0px; MARGIN: 2px 0px; BORDER-LEFT: #003399 1px solid; WIDTH: 164px; CURSOR: hand; COLOR: #ffcc00; LINE-HEIGHT: 20px; PADDING-TOP: 0px; BORDER-BOTTOM: #99ccff 1px solid; FONT-FAMILY: verdana; LETTER-SPACING: -0.5pt; BACKGROUND-COLOR: #3366cc; TEXT-DECORATION: none
}
P.navpages { font-size:70%; font-family:verdana } TD.navpages { font-family:verdana;font-size:70%; line-height:140%; } TD.leading { font-size:80%; line-height:190%;font-family:verdana } TD.headlineblurb { font-size : 100%; font-family : Verdana; font-weight:bold; } A.inline { font-weight:bold; font-family:verdana} 

HR.blue {
    color:#FFCC33;
    height: 1px
 }
 .clsMenu
{
    BACKGROUND: #eeeee6;
    BORDER-BOTTOM: black 1px solid;
    BORDER-LEFT: black 2px solid;
    BORDER-RIGHT: black 1px solid;
    BORDER-TOP: black 2px solid;
    PADDING-BOTTOM: 0px;
    POSITION: absolute;
    DISPLAY: none;
    WIDTH: 80px
}
.cellOff
{
    BACKGROUND: #eeeee6;
    BORDER-BOTTOM: black 1px solid;
    BORDER-RIGHT: black 1px solid;
    CURSOR: hand;
    FONT-FAMILY: 'sans-serif';
    FONT-SIZE: 8pt;
    FONT-WEIGHT: bold;
    HEIGHT: 20px;
    PADDING-BOTTOM: 2px;
    PADDING-LEFT: 1px;
    PADDING-RIGHT: 4px;
    PADDING-TOP: 1px;
    TEXT-DECORATION: none;
    WIDTH: 100%
}
.cellOff1
{
    BACKGROUND: #eeeee6;
    BORDER-BOTTOM: black 1px solid;
    BORDER-RIGHT: black 1px solid;
    FONT-FAMILY: 'sans-serif';
    FONT-SIZE: 8pt;
    FONT-WEIGHT: bold;
    HEIGHT: 20px;
    PADDING-BOTTOM: 2px;
    PADDING-LEFT: 4px;
    PADDING-RIGHT: 4px;
    PADDING-TOP: 2px;
    TEXT-DECORATION: none;
    WIDTH: 100%
}

TD.toolCell
{
    BORDER-LEFT: #ffffff 1px;
    BORDER-RIGHT: #ffffff solid 1px;
    BORDER-TOP: #ffffff 1px;
    BORDER-BOTTOM-: #ffffff 1px
}
</style>
<%@ include file="/ESEventHandler.jsp" %>
<SCRIPT LANGUAGE="JAVASCRIPT" TYPE="text/javascript">
function preloadImages() 
{
	if (document.images) 
	{
		var imgFiles = preloadImages.arguments;
		var preloadArray = new Array();
		for (var i=0; i<imgFiles.length; i++) 
		{
			preloadArray[i] = new Image;
			preloadArray[i].src = imgFiles[i];
		}
	}
}
function swapImage() 
{
	var i,j=0,objStr,obj,swapArray=new Array,oldArray=document.swapImgData;
	for (i=0; i < (swapImage.arguments.length-2); i+=3) 
	{
		objStr = swapImage.arguments[(navigator.appName == 'Netscape')?i:i+1];
		if ((objStr.indexOf('document.layers[')==0 && document.layers==null) || (objStr.indexOf('document.all[')==0 && document.all==null))
			      objStr = 'document'+objStr.substring(objStr.lastIndexOf('.'),objStr.length);
		obj = eval(objStr);
		if (obj != null) 
		{
			swapArray[j++] = obj;
			swapArray[j++] = (oldArray==null || oldArray[j-1]!=obj)?obj.src:oldArray[j];
			obj.src = swapImage.arguments[i+2];
		}
	}
	document.swapImgData = swapArray;
}
function swapImgRestore() 
{
	if (document.swapImgData != null)
	for (var i=0; i<(document.swapImgData.length-1); i+=2)
		document.swapImgData[i].src = document.swapImgData[i+1];
}
//-->
</SCRIPT>
<SCRIPT LANGUAGE="JAVASCRIPT" TYPE="text/javascript">
<!--
var oLastBtn=0;

function openChat()
{
	var Bars = 'directories=no,location=no,menubar=no,status=no,titlebar=no';
	var Options='scrollbar=no,width=275,height=100,resizable=no';
	var Features=Bars+' '+Options;
    window.open('ETCcustomer.html','Doc',Features)
}
function RaiseButton()
{
	window.event.cancelBubble=true;
	oBtn = window.event.srcElement;
	var bChosen = false;
	if(oLastBtn && oLastBtn != oBtn)
	{
		HideButton();
	}
	if(oBtn.buttonType)
	{
		oBtn.className = oBtn.buttonType + "Up";
		oLastBtn=oBtn;
	}
	else 
	{
		oLastBtn = 0;
	}
}
function DepressButton()
{
	window.event.cancelBubble=true;
	oBtn = window.event.srcElement;
	if(oBtn.buttonType)
	{
		oBtn.className = oBtn.buttonType + "Down";
	}
}
function HideButton()
{
	if ((oLastBtn.buttonType == "LeftNavChosen") || (oLastBtn.buttonType == "LeftNavSubChosen") || (oLastBtn.buttonType == "appNavChosen") || (oLastBtn.buttonType == "appNavSubChosen")) 
		oLastBtn.className = oLastBtn.buttonType;
	else 
		oLastBtn.className = oLastBtn.buttonType + "Off";
}
var currentSpanElement		= "";			//Needed to track current span element
var menuArray				= new Array();	//Tracks what divs are showing so it knows what to hide when a new span is clicked
var onTextColor				= "#FFFFFF";	//Text color with mouseover
var offTextColor			= "#000000";	//Text color with NO mouseover
var onCellColor				= "#A61616";	//Span color with mouseover
var offCellColor			= "#EEEEE6";	//Span color with nomouseover
var offsetMenuX				= 25;			//X Distance new menu will be from parent menu
var offsetMenuY				= 5;			//Y Distance increase of new menu compared to parent span
var startDistanceX			= -10;			//Sets how far off onMouseOver start element we should go
var startDistanceY			= 5;			//Sets how far off onMouseOver start element we should go
var menuOn					= false;		//Only one set of menus can be displayed at once
var started					= false;		//Used to track if we just started the menus
var clickStart				= true;			//Allow an onClick event to start the menu or not here
var clickX					= 0;			//Location X of event to start menu
var clickY					= 0;			//Location Y of event to start menu
var selectCount				= 0;
var select					= "";
var appletCount				= 0;			//If we have applets, track the # so we can temporarily hide them
var applets					= "";			//Tracks the document.all.tags("applet") collection so we can reference it once rather than twice

function startIt(menu,thisItem,level) {						//menu = menu to display,thisItem=coordinates of item to use,level=current depth of menus
	if (menuOn == true) {
		window.event.cancelBubble = true;
		hideAllDivs();
		return;												//Only allow one menu to be activated at a time
	} else {
		select = document.all.tags("select");
		selectCount = select.length;
		if (selectCount > 0) {
			for (i=0;i<selectCount;i++) {
				select.item(i).style.visibility = "hidden";
			}
		}
		applets = document.all.tags("applet");
		appletCount = applets.length;
		if (appletCount > 0) {
			for (i=0;i<appletCount;i++) {
				applets.item(i).style.visibility = "hidden";
			}
		}
		menuOn = true;			
		started = true;										//Lets us know we're coming in for the 1st time
		clickX = event.clientX;
		clickY = event.clientY;
		if (clickStart) window.event.cancelBubble = true;		
		stateChange(menu,thisItem,level);
	}	
}

function stateChange(menu,thisItem,level) {									//menu = menu to display,thisItem=name of span item to use,level=current depth of menus
	
	if (currentSpanElement != thisItem.id && started != true) {							//Only hit this if they changed span elements	
		if (currentSpanElement == "") currentSpanElement = thisItem.id;	//Used 1st time through only	

		eItemOld = eval("document.all('" + currentSpanElement + "')");
		eItemNew = eval("document.all('" + thisItem.id + "')");
		eParent = eItemNew.parentElement;
		eParent.style.background = offCellColor;						//Must set DIV background color or it will be transparent by default	
			
		//Turn off whatever span was turned on
		eItemOld.style.background = offCellColor;
		eItemOld.style.color = offTextColor;
		//Turn on new span
		eItemNew.style.background = onCellColor;
		eItemNew.style.color = onTextColor;

		currentSpanElement = thisItem.id;					//Track where the last mouseover came from
	}
	
	if (menu != "") {
		eMenu = eval("document.all('" + menu + "')");			
		eItem = eval("document.all('" + thisItem.id + "')");				//Used for x,y coordinates
		hideDiv(level);
		menuArray[menuArray.length] = menu;									//Tracks open menus	

		var positionX =  eItem.parentElement.offsetLeft + offsetMenuX + document.body.scrollLeft;
		var positionY =   eItem.parentElement.offsetTop + eItem.offsetTop + offsetMenuY + document.body.scrollTop;
		if (started) {
			positionX =	clickX + startDistanceX	+ document.body.scrollLeft	//eItem.offsetLeft + startDistanceX;
			positionY =	clickY + startDistanceY	+ document.body.scrollTop	//eItem.offsetTop + startDistanceY;
		}
		//If screen isn't wide enough to fit menu, bump menu back to the left some
		if ((positionX + eMenu.offsetWidth) >= document.body.clientWidth) {
			positionX -= (eMenu.offsetWidth * 1.3);
			positionY += 15;
		}
		//If the menu is too far to the left to display, bump it to the right some
		if ((positionX + eMenu.offsetWidth) <= eMenu.offsetWidth) {
			positionX += (eMenu.offsetWidth * 1.3);
		}
		//If the menu is too far down, bump the menu up to the bottom equals the body clientHeight property
		if ((positionY + eMenu.offsetHeight) >= document.body.clientHeight) {
			if (started != true) positionY = document.body.clientHeight - eMenu.offsetHeight;
		}
	
		eMenu.style.left = positionX;
		eMenu.style.top = positionY;
		//eMenu.style.zIndex = level;									//Only use this if we don't reverse the arrays in the ASP/XML Script
		eMenu.style.display="block";
	}
	
	started = false;												//After 1st menu, turn of started variable
}

function hideDiv(currentLevel) {
		for (var i=currentLevel;i<menuArray.length;i++) {
			var arrayString = new String(menuArray[i]);
			if (arrayString == "undefined") continue;
			eval("document.all('" + menuArray[i] + "').style.display='none'");
		}
			menuArray.length = currentLevel;
}

function hideAllDivs() {
	if (menuOn == true) {		//Don't loop through document elements if they clicked a hyperlink since it wastes time
		for (var i=0;i<menuArray.length;i++) {
			var arrayString = new String(menuArray[i]);
			if (arrayString == "undefined") continue;
			document.all(menuArray[i]).style.display = "none";
			document.all(menuArray[i]).style.left = 0;
			document.all(menuArray[i]).style.top = 0;

		}
		if (currentSpanElement != "") {					//No currentSpanElement if they haven't mousedOver any span element
			eItem = eval("document.all('" + currentSpanElement + "')");
			eItem.style.background = offCellColor;		//Ensure current span's color is changed back to "off" color
			eItem.style.color = offTextColor;		//Ensure current span's text color is changed back to "off" color
			menuArray = new Array();	
			currentSpanElement = "";		
		}
		if (selectCount > 0) {
			for (i=0;i<selectCount;i++) {
				select.item(i).style.visibility = "visible";
			}
			selectCount = 0;
			select = "";
		}
		if (appletCount > 0) {
			for (i=0;i<appletCount;i++) {
				applets.item(i).style.visibility = "visible";
			}
			appletCount = 0;
			applets = "";
		}
	}
	menuOn = false;								//Menus off, so set this to false


}
document.onclick = hideAllDivs;
//-->
</SCRIPT>
<% 
   response.setHeader("Cache-Control","no-cache"); // HTTP 1.1
   response.setHeader("Pragma","no-cache"); // HTTP 1.0
   response.setDateHeader ("Expires", -1); // Prevents caching at the proxy server
%>
</head>
<body bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0">
<table width="100%" border="0" cellspacing="1" cellpadding="1">
  <tr> 

	<td width="8%"  valign="bottom"><img src="images/registration_form.gif" width="108" height="50"></td>	   
	<td width="8%"  valign="bottom"><img src="images/Pickup.gif" width="60" height="50"></td>
	<td width="7%" valign="bottom"><object classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" codebase="http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=4,0,2,0" width="60" height="40">
        <param name=movie value="images/Track.swf">
        <param name=quality value=high>
        <embed src="images/Track.swf" quality=high pluginspage="http://www.macromedia.com/shockwave/download/index.cgi?P1_Prod_Version=ShockwaveFlash" type="application/x-shockwave-flash" width="60" height="40">
        </embed> 
      </object><img src="images/Track1.gif" width="60" height="12"></td>
    <td width="8%" valign="bottom"><img src="images/Report.gif" width="60" height="50"></td>
    <td width="8%"  valign="bottom"><img src="images/Print.gif" width="60" height="50"></td>
	<td width="7%"  valign="bottom"><img src="images/Setup.gif" width="60" height="50"></td>
    <td width="12%" valign="bottom"><img src="images/CustomerSupport.gif" width="108" height="50"></td>
    <td rowspan="2"  valign="bottom" width="28%"><font face="Verdana" size="5"><b><font size="7" color="#FF0000">
<fmt:message key="100323" bundle="${lang}"/></font><font size="7">
<fmt:message key="100324" bundle="${lang}"/></font></b></font></td>
  </tr>
  <tr > 
    
	<td width="12%"><A id="start8" onClick="startIt('Customer',this,0)"><FONT SIZE="2" FACE="Verdana,Arial,Helvetica" STYLE="COLOR:#FFFFFF"><SPAN buttonType="LeftNav" CLASS="LeftNavOff">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;C&nbsp;u&nbsp;s&nbsp;t&nbsp;o&nbsp;m&nbsp;e&nbsp;r&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</SPAN></FONT></A></td>

    <td width="8%"><A id="start2" onClick="startIt('Pickup',this,0)"><FONT SIZE="2" FACE="Verdana,Arial,Helvetica" STYLE="COLOR:#FFFFFF"><SPAN buttonType="LeftNav" CLASS="LeftNavOff">P&nbsp;i&nbsp;c&nbsp;k&nbsp;u&nbsp;p&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</SPAN></FONT></A></td>

	<td width="7%"><a id="start1" onClick="startIt('Track',this,0)"><font size="3" face="Verdana,Arial,Helvetica" style="COLOR:#FFFFFF"><span buttontype="LeftNav" class="LeftNavOff">&nbsp;&nbsp;T&nbsp;r&nbsp;a&nbsp;c&nbsp;k</span></font></a></td>

	<td width="8%"><A id="start6" onClick="startIt('Report',this,0)"><FONT SIZE="2" FACE="Verdana,Arial,Helvetica" STYLE="COLOR:#FFFFFF"><SPAN buttonType="LeftNav" CLASS="LeftNavOff">R&nbsp;e&nbsp;p&nbsp;o&nbsp;r&nbsp;t&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</SPAN></FONT></A></td>
    
    <td width="8%"><A id="start3" onClick="startIt('Rate',this,0)"><FONT SIZE="2" FACE="Verdana,Arial,Helvetica" STYLE="COLOR:#FFFFFF"><SPAN buttonType="LeftNav" CLASS="LeftNavOff">P&nbsp;r&nbsp;i&nbsp;n&nbsp;t&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</SPAN></A></td>

    <td width="7%"><A id="start5" onClick="startIt('Setup',this,0)"><FONT SIZE="2" FACE="Verdana,Arial,Helvetica" STYLE="COLOR:#FFFFFF"><SPAN buttonType="LeftNav" CLASS="LeftNavOff">S&nbsp;e&nbsp;t&nbsp;u&nbsp;p</SPAN></FONT></A></td>

    <td width="12%"><A id="start7" onClick="startIt('CustomerSupport',this,0)"><FONT SIZE="2" FACE="Verdana,Arial,Helvetica" STYLE="COLOR:#FFFFFF"><SPAN buttonType="LeftNav" CLASS="LeftNavOff">C&nbsp;u&nbsp;s&nbsp;t&nbsp;o&nbsp;m&nbsp;e&nbsp;r&nbsp;&nbsp;S&nbsp;u&nbsp;p&nbsp;p&nbsp;o&nbsp;r&nbsp;t</SPAN></FONT></A></td>
    </tr>
</table>
<div id='Customer' class='clsMenu'>
<span id="Customer_span2" class='cellOff' onMouseOver="stateChange('',this,'');hideDiv(1)" onMouseOut="stateChange('',this,'')" onClick="parent.mainFrame.location.href='ETCCustomerRegistrationAdd.jsp?Operation=Add'">
<fmt:message key="100003" bundle="${lang}"/></span><br>
<span id="Customer_span4" class='cellOff' onMouseOver="stateChange('',this,'');hideDiv(1)" onMouseOut="stateChange('',this,'')" onClick="parent.mainFrame.location.href='ETCCustomerRegistrationEnterId.jsp?Operation=Modify'">
<fmt:message key="100005" bundle="${lang}"/></span><br>
<span id="Customer_span6" class='cellOff' onMouseOver="stateChange('',this,'');hideDiv(1)" onMouseOut="stateChange('',this,'')" onClick="parent.mainFrame.location.href='ETCCustomerRegistrationEnterId.jsp?Operation=View'">
<fmt:message key="100006" bundle="${lang}"/></span><br>
<span id="Customer_span8" class='cellOff' onMouseOver="stateChange('',this,'');hideDiv(1)" onMouseOut="stateChange('',this,'')" onClick="parent.mainFrame.location.href='ETCCustomerRegistrationEnterId.jsp?Operation=Delete'">
<fmt:message key="100004" bundle="${lang}"/></span><br>
</div>
<div id='Track' class='clsMenu'>
<span id="Track_span2" class='cellOff' onMouseOver="stateChange('',this,'');hideDiv(1)" onMouseOut="stateChange('',this,'')" onClick="parent.mainFrame.location.href='ETCFastrak.jsp'">
<fmt:message key="100325" bundle="${lang}"/></span><br>
<span id="Track_span4" class='cellOff' onMouseOver="stateChange('',this,'');hideDiv(1)" onMouseOut="stateChange('',this,'')" onClick="parent.mainFrame.location.href='ETCSpecificTracking.jsp'">
<fmt:message key="100326" bundle="${lang}"/></span><br>
<span id="Track_span6" class='cellOff' onMouseOver="stateChange('',this,'');hideDiv(1)" onMouseOut="stateChange('',this,'')" onClick="parent.mainFrame.location.href='ETCDetailedTracking.jsp'">
<fmt:message key="100327" bundle="${lang}"/></span><br>
</div>
<div id='Pickup' class='clsMenu'><span id="Pickup_span2" class='cellOff' onMouseOver="stateChange('',this,'');hideDiv(1)" onMouseOut="stateChange('',this,'')" onClick="parent.mainFrame.location.href='ETCPRQAdd.jsp?Operation=Add&userType=ETC'">
<fmt:message key="100003" bundle="${lang}"/></span><br>
<span id="Pickup_span6" class='cellOff' onMouseOver="stateChange('',this,'');hideDiv(1)" onMouseOut="stateChange('',this,'')" onClick="parent.mainFrame.location.href='ETCPRQEnterId.jsp?Operation=View&userType=ETC'">
<fmt:message key="100006" bundle="${lang}"/></span><br>
</div>
<div id='Rate' class='clsMenu'><span id="Rate_span2" class='cellOff' onMouseOver="stateChange('',this,'');hideDiv(1)" onMouseOut="stateChange('',this,'')" onClick="parent.mainFrame.location.href='ETCPrintDocuments.jsp'">
<fmt:message key="100328" bundle="${lang}"/></span><br>
</div>
<div id='Transit' class='clsMenu'><span id="Transit_span2" class='cellOff' onMouseOver="stateChange('',this,'');hideDiv(1)" onMouseOut="stateChange('',this,'')" onClick="parent.mainFrame.location.href='samp.jsp'">
<fmt:message key="100329" bundle="${lang}"/></span><br>
</div>
<div id='Setup' class='clsMenu'><span id="Setup_span2" class='cellOff' onMouseOver="stateChange('',this,'');hideDiv(1)" onMouseOut="stateChange('',this,'')" onClick="parent.mainFrame.location.href='ETCChangePassword.jsp'">
<fmt:message key="100330" bundle="${lang}"/>&nbsp;
<fmt:message key="100331" bundle="${lang}"/></span><br>
</div>
<div id='Report' class='clsMenu'><span id="Report_span2" class='cellOff' onMouseOver="stateChange('',this,'');hideDiv(1)" onMouseOut="stateChange('',this,'')" onClick="parent.mainFrame.location.href='ETCAPBillwiseOutstandingReport.jsp'">
<fmt:message key="100332" bundle="${lang}"/></span><br>
<span id="Report_span4" class='cellOff' onMouseOver="stateChange('',this,'');hideDiv(1)" onMouseOut="stateChange('',this,'')" onClick="parent.mainFrame.location.href='ETCTonnageReport.jsp'">
<fmt:message key="100333" bundle="${lang}"/></span><br>
<span id="Report_span6" class='cellOff' onMouseOver="stateChange('',this,'');hideDiv(1)" onMouseOut="stateChange('',this,'')" onClick="parent.mainFrame.location.href='ETCTATReport.jsp'">
<fmt:message key="100334" bundle="${lang}"/></span><br>
</div>
<div id='CustomerSupport' class='clsMenu'><span id="CustomerSupport_span2" class='cellOff' onMouseOver="stateChange('',this,'');hideDiv(1)" onMouseOut="stateChange('',this,'')" onClick="return openChat()"'">
<fmt:message key="100335" bundle="${lang}"/>&nbsp;
<fmt:message key="100336" bundle="${lang}"/></span><br>
</div>
</div>
</body>
</html>
