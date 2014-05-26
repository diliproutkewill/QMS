<%@ page import ="java.util.*,javax.servlet.jsp.jstl.fmt.LocalizationContext"%>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt_rt" prefix="fmt_rt"%>
<jsp:useBean id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/>

<%
	  String  language = loginbean.getUserPreferences().getLanguage();
%>
<fmt_rt:setLocale value="<%=language%>"/>
	<fmt:setBundle basename="Lang" var="lang" scope="application"/>
<HTML>
<HEAD>
<STYLE type = "text/css">
body {
 font-family: verdana,Arial, Helvetica, sans-serif;
 font-size: 10pt;
 color: #000000;
 background:#ffffff;
 margin-left:0px;
 margin-right:0px;
 margin-width:0px;
 margin-height:0px;
 margin-top:18px;
}

a:link { color: #000000; }
a:visited { color: #222222; }
a:active { color: #444444; }
a:hover { color: #778899; }
a{TEXT-DECORATION: none}
.8pt {font-size:8pt; font-family:arial;}
.8ptBold { font-size:8pt; font-weight:bold; font-family:arial;}
.10ptRed { color:#ff0000; }
.10ptBoldBlue { font-size: 10pt; font-weight:bold; font-family:arial; color:#336699;}
.12pt {font-size:12pt; font-family:arial;}
.12ptBold { font-size: 12pt; font-weight:bold; font-family:arial;}
.12ptBoldBlue { font-size: 12pt; font-weight:bold; font-family:arial; color:#336699;}
.14pt { font-size: 18pt; font-family:arial;}
.14ptBold { font-size: 18pt; font-weight:bold; font-family:arial;}
.16pt { font-size: 18pt; font-family:arial;}
.16ptBold { font-size: 18pt; font-weight:bold; font-family:arial;}
.16ptBoldBlue { font-size: 16pt; font-weight:bold; font-family:arial; color:#336699;}
.18pt { font-size: 18pt; font-family:arial;}
.18ptBold { font-size: 18pt; font-weight:bold; font-family:arial;}

td {
 font-family: verdana, Arial, Helvetica, sans-serif;
 font-size: 10pt;
 background: #000000;
 border-style: groove;
 border-width:2px;
 border-color:#ffffff;
 padding:2px;
}


.Grey {background: #dddddd; border-width:0px;}
.GreyBox {background: #dddddd;}
.GreyList{background: #dddddd; border-width:0px; padding-top:20px; padding-bottom:5px; border-bottom-width:2px;}
.White {background: #ffffff; border-width:0px; padding-left:10px; padding-right:5px;}
.WhiteBox {background: #ffffff;}
.WhiteList{background: #ffffff; border-width:0px; padding-top:20px; padding-bottom:5px; border-bottom-width:2px; padding-left:10px; padding-right:5px;}
.LightGrey {background: #eeeeee; border-width:0px;}
.LightGreyBox {background: #eeeeee;}
.LightGreyList{background: #eeeeee; border-width:0px; padding-top:20px; padding-bottom:5px; border-bottom-width:2px; padding-left:10px; padding-right:5px;}
.OffWhite {background: #f5f5f5; border-width:0px;}
.OffWhiteBox {background: #f5f5f5;}
.OffWhiteList{background: #f5f5f5; border-width:0px; padding-top:20px; padding-bottom:5px; border-bottom-width:2px; padding-left:10px; padding-right:5px;}
.Green {background: #aabbbb; border-width:0px;}
.GreenBox {background: #aabbbb;}
.GreenBoxPadding0 {background: #aabbbb; padding:0px;}
.GreenList {background: #aabbbb; border-width:0px; padding-top:20px; padding-bottom:5px; border-bottom-width:2px; padding-left:10px; padding-right:5px;}
.Blue {background: #99aabb; border-width:0px;}
.BlueBox {background: #99aabb;}
.BlueList {background: #99aabb; border-width:0px; padding-top:20px; padding-bottom:5px; border-bottom-width:2px;}

td.Menu {background: #99aabb; padding:1px; padding-left:12px; padding-right:12px; font-size:10pt; font-family:arial;}
td.MenuItem {background: #99aabb; padding:0px; padding-left:12px; padding-right:12px; font-size:10pt; border-top-width:0px; font-family:arial;}
td.Nav {background:#dddddd; border-top-width:0;padding-top:0px; padding-bottom:0px; font-family:arial;}
A.menu:VISITED {color:blue;}

A.menu:HOVER {color:white;}

A.GreenBox:HOVER {color:#eeeeee;}

.empty {
  padding:0px;
  margin:0px;
  border:0px;
  background:#ffffff;
 }
</STYLE>
<%@ include file="/ESEventHandler.jsp" %>
<SCRIPT language = "javascript">
<!--
window.offscreenBuffering = true;
var browser = navigator.appName;


	function is_ie()
	{	if (browser == "Microsoft Internet Explorer") return true;
		return false;
	}
	function is_ns4()
	{	if(browser == "Netscape" && parseInt(navigator.appVersion) < 5 ) return true;
		return false;
	}

	function show(id)
	{
		if(is_ns4())
		{
			for(i = 6; i < 8;i++)
			{
				document.layers[i].visibility = "hide";
			}
			document.layers[id].visibility = "show";
		}
		if(is_ie())
		{
			for (i = 0; i<2;i++)
			{
				document.all["menu" + i].style.visibility = "hidden";
			}
			document.all["menu" + id].style.visibility = "visible";
		}
	}
	function hide(id)
	{
		if(browser == "Netscape")
		{
			document.layers[id].visibility = "hide";
		}
		if(is_ie())
		{
			document.all["menu" + id].style.visibility = "hidden";
		}
	}

	function init()
	{
		if(is_ns4())
		{
			document.layers[1].left = 0;

			document.layers[1].visibility = "show";
			
		}
		if(is_ie())
		{
			document.all.jay1.style.left = jay0.clientWidth-2;

			document.all.jay0.style.visibility = "visible";

			menu1.style.left = jay1.style.left;

		}
	}

	var B = "Border=2 width=125";
	var Size = new Array("width=50","width=70","width=75","width=105","width=65","width=75","width=75");

	if (is_ie())
	{
		Size = new Array("","");
		B = "";
	}

	var X = '<table border=0 ';
	var Y = ' cellspacing="0" cellpadding=2><tr><td class="menu" nowrap>';
	var Z = '</td></tr></table>';

	headers = new Array();
	headers[0] = '<U><fmt:message key="100505" bundle="${lang}"/></U>';

	for (i = 0; i<2;i++)
	{
		headers[i] = X + Size[i] + Y + headers[i] + Z;
	}

	var T = '<tr><td class="MenuItem" nowrap><a class="menu" href="';

	tables = new Array();
	//tables[0] = '<table '+B+' cellspacing="0">'+T+'.jsp">Add</a></td></tr>'+T+'.jsp">View</a></td></tr>'+T+'.jsp">Cancel</a></td><tr></FORM></tr></table>';
	tables[0] = '<table '+B+' cellspacing="0">'+T+'ELSOController.jsp?Process=CRMADD">&nbsp;&nbsp;&nbsp;&nbsp;<fmt:message key="100506" bundle="${lang}"/>&nbsp;&nbsp;&nbsp;&nbsp;</a></td></tr>' + T + 'ELSOController.jsp?Process=CRMVIEW">&nbsp;&nbsp;&nbsp;&nbsp;<fmt:message key="100507" bundle="${lang}"/>&nbsp;&nbsp;&nbsp;&nbsp;</a></td></tr>' + T + 'ELSOController.jsp?Process=CRMPRINT">&nbsp;&nbsp;&nbsp;&nbsp;<fmt:message key="100508" bundle="${lang}"/>&nbsp;&nbsp;&nbsp;&nbsp;</a></td></tr>' + T + 'ELSOController.jsp?Process=CRMCANCEL">&nbsp;&nbsp;&nbsp;&nbsp;<fmt:message key="100509" bundle="${lang}"/>&nbsp;&nbsp;&nbsp;&nbsp;</a></td></tr></table>';
	if(is_ns4())
	{
		document.open();
		document.write('<layer width="100%" top="0" left="0"><table cellspacing="0" width="100%"><tr><td class="menu"><a href="http://www.four-soft.com"><fmt:message key="100048" bundle="${lang}"/></a></td></tr></table></layer>\n');
		 for(i=0;i<2;i++)
		 {
			document.write('<layer width="0" top="0" visibility="hide" onMouseOver="show(' + (i+6) + ')" onMouseOut="hide(' + (i+7) + ')">' + headers[i] + '</layer>\n');
		 }
		 for(i=0;i<2;i++)
		 {
			document.write('<layer width="0" top="20" visibility="hide" onMouseOver="show(' + (i+6) + ')" onMouseOut="hide(' + (i+7) + ')">' + tables[i] + '</layer>\n');
		 }
		 document.close();
	}
	else
	{
		document.write('<div style="position:absolute; width:100%; top:0;"><table cellspacing="0" width="100%"><tr><td class="menu" width="100%"><a href="http://www.four-soft.com"><fmt:message key="100048" bundle="${lang}"/></a></td></tr></table></div>');
		if(is_ie())
		{
			for(i = 0; i <2; i++)
			{
				document.write('<div id="jay' + i + '" style="position:absolute; width:0; top:0; visibility:hidden;" onMouseOver="show(' + i + ')" onMouseOut="hide(' + i + ')">' + headers[i] + '</div>');
			}
			for(i = 0; i <2; i++)
			{
				document.write('<div id="menu' + i + '" style="position:absolute; width:0; top:22; visibility:hidden;" onMouseOver="show(' + i + ')" onMouseOut="hide(' + i + ')">' + tables[i] + '</div>');
			}
		}
	}
	//-->
</SCRIPT>
</HEAD>
<BODY onLoad=init() bgcolor="#ffffff">
</TABLE>
</body>
</html>
