<%@ page import ="java.util.*,javax.servlet.jsp.jstl.fmt.LocalizationContext"%>
<jsp:useBean id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/>

	<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt"%>
  <%@ taglib uri="http://java.sun.com/jstl/fmt_rt" prefix="fmt_rt"%>  

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
			for(i = 6; i < 10;i++)
			{
				document.layers[i].visibility = "hide";
			}
			document.layers[id].visibility = "show";
		}
		if(is_ie())
		{
			for (i = 0; i<4;i++)
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
			document.layers[2].left = document.layers[1].document.width -2 ;
			document.layers[3].left = document.layers[1].document.width + document.layers[2].document.width -4;
			document.layers[4].left = document.layers[1].document.width + document.layers[2].document.width + document.layers[3].document.width -6;
			document.layers[5].left = document.layers[1].document.width + document.layers[2].document.width + document.layers[3].document.width + document.layers[4].document.width -8;

			document.layers[6].left = 0;
			document.layers[7].left = document.layers[1].left;
			document.layers[8].left = document.layers[2].left;

			document.layers[1].visibility = "show";
			document.layers[2].visibility = "show";
			document.layers[3].visibility = "show";

		}
		if(is_ie())
		{
			document.all.jay1.style.left = jay0.clientWidth-2;
			document.all.jay2.style.left = jay0.clientWidth + jay1.clientWidth-4;
			document.all.jay3.style.left = jay0.clientWidth + jay1.clientWidth+jay2.clientWidth-6;

			document.all.jay0.style.visibility = "visible";
			document.all.jay1.style.visibility = "visible";
			document.all.jay2.style.visibility = "visible";

			menu1.style.left = jay1.style.left;
			menu2.style.left = jay2.style.left;
			menu3.style.left = jay3.style.left;

		}
	}

	var B = "Border=2 width=125";
	var Size = new Array("width=50","width=70","width=75","width=105","width=65","width=75","width=75");

	if (is_ie())
	{
		Size = new Array("","","");
		B = "";
	}

	var X = '<table border=0 ';
	var Y = ' cellspacing="0" cellpadding=2><tr><td class="menu" nowrap>';
	var Z = '</td></tr></table>';

	headers = new Array();
	headers[0] = '<U><fmt:message key="100510" bundle="${lang}"/></U>';
	headers[1] = '<U><fmt:message key="100511" bundle="${lang}"/></U>';
	headers[2] = '<U><fmt:message key="100521" bundle="${lang}"/></U>';

	for (i = 0; i<3;i++)
	{
		headers[i] = X + Size[i] + Y + headers[i] + Z;
	}

	var T = '<tr><td class="MenuItem" nowrap><a class="menu" href="';

	tables = new Array();
	tables[0] = '<table '+B+' cellspacing="0">'+T+'ELPartyMasterCustomerCRMModify.jsp?Process=MODIFY&partyFlag=CUSTOMER"><fmt:message key="100513" bundle="${lang}"/></a></td></tr>'+T+'ELPartyMasterCustomerCRMView.jsp?Process=VIEW&partyFlag=CUSTOMER"><fmt:message key="100507" bundle="${lang}"/></a></td><tr></FORM></tr></table>';
	tables[1] = '<table '+B+' cellspacing="0">'+T+'ELPartVendorController.jsp?Process=MODIFY&flag=CUSTOMER"><fmt:message key="100507" bundle="${lang}"/></a></td></tr>'+T+'ELPartVendorController.jsp?Process=VIEW&flag=CUSTOMER"><fmt:message key="100321" bundle="${lang}"/></a></td><tr></FORM></tr></table>';
	tables[2] = '<table '+B+' cellspacing="0">'+T+'ELSalesContractController.jsp?Process=VIEW"><fmt:message key="100507" bundle="${lang}"/></a></td><tr></FORM></tr></table>';

	if(is_ns4())
	{
		document.open();
		document.write('<layer width="100%" top="0" left="0"><table cellspacing="0" width="100%"><tr><td class="menu"><a href="http://www.four-soft.com"><fmt:message key="100048" bundle="${lang}"/></a></td></tr></table></layer>\n');
		 for(i=0;i<4;i++)
		 {
			document.write('<layer width="0" top="0" visibility="hide" onMouseOver="show(' + (i+6) + ')" onMouseOut="hide(' + (i+7) + ')">' + headers[i] + '</layer>\n');
		 }
		 for(i=0;i<4;i++)
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
			for(i = 0; i < 4; i++)
			{
				document.write('<div id="jay' + i + '" style="position:absolute; width:0; top:0; visibility:hidden;" onMouseOver="show(' + i + ')" onMouseOut="hide(' + i + ')">' + headers[i] + '</div>');
			}
			for(i = 0; i < 4; i++)
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
