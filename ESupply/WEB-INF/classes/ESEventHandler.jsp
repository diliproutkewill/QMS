<%--
 % 
 % Copyright (c) 1999-2001 by FourSoft, Pvt Ltd. All Rights Reserved.
 % This software is the proprietary information of FourSoft, Pvt Ltd.
 % Use is subject to license terms.
 %
 % esupply - v 1.x 
 %
 --%>
<%--
 % File			: ESEventHandler.jsp
 % sub-module	: AccessControl
 % module		: esupply
 %
 % This is use as an included JSP for handling events from other HTML ouputted
 % by other application JSPs
 % 
 % author		: Sasibhushan. P
 % date			: 22-12-2001
 % Modification  history
--%>

<%@ page import="com.foursoft.esupply.common.java.FoursoftWebConfig" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt_rt" prefix="fmt_rt"%>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<%
	   String  langu = ((com.foursoft.esupply.common.bean.ESupplyGlobalParameters)session.getAttribute("loginbean")).getUserPreferences().getLanguage();
%>
<fmt_rt:setLocale value="<%=langu%>"/>
<fmt:setBundle basename="Lang" var="lang" scope="application"/>

<script language="javascript">
var browser = "";
if(navigator.appName=="Netscape") {
	browser = "NN";
}
if(navigator.appName=="Microsoft Internet Explorer") {
	browser = "IE";
}

var m1 = '<fmt:message key="100501" bundle="${lang}"/>';
var m2 = '<fmt:message key="100502" bundle="${lang}"/>';
var m3 = '<fmt:message key="100503" bundle="${lang}"/>';

<%
if(FoursoftWebConfig.HANDLE_MOUSE_EVENTS) {
%>
	function handleMouseEvents(e)
	{	
		if(browser == "IE") {
			event.cancelBubble = true;
			if(document.all) {
				if(event.button==2 || event.button==3) {
					return false;
				}
				if(event.button==1) {
					var shiftPressed = (window.Event) ? e.modifiers & Event.SHIFT_MASK : window.event.shiftKey;
					if(shiftPressed) {
						//alert(m2);
						return false;
					}
				}
			}
		}
		if(browser == "NN") {
			e.cancelBubble = true;
			//alert("e.which = "+ e.which);
			if(e.which == 2 || e.which == 3) { 
				//alert(m1);
				return false;
			}
		}
	}
<%
}
%>
	function handleKeyDown(e)
	{

		try {
		
			var altPressed 		= (window.Event) ? Event.ALT_MASK & e.modifiers : event.altKey;
			var shiftPressed 	= (window.Event) ? Event.SHIFT_MASK & e.modifiers: event.shiftKey;
			var ctrlPressed 	= (window.Event) ? Event.CONTROL_MASK & e.modifiers : event.ctrlKey;
		
			var whichCode 		= (window.Event) ? e.which : event.keyCode;
		
			//alert(	"ctrlPressed  = "+ctrlPressed +"\n"+
			//		"shiftPressed = "+shiftPressed +"\n"+
			//		"altPressed   = "+altPressed	);
		
			var key 			= String.fromCharCode(whichCode);

			if(whichCode==116) {	// F5 - Place focus on the menu tree
				//parent.frames['heading'].key = "F5";
				parent.key = "F5";
			}
			if(whichCode==118) {	// F7 - Place focus on the menu tree
				parent.frames['menu'].focus();
			}
			if(whichCode==119) {	// F8 - Place focus on the Right Menu
				parent.frames['text'].focus();
			}
			if(whichCode==120) {	// F9 - Place focus on the Heading bar
				parent.frames['heading'].focus();
			}
			
			

			if(whichCode==20) {	// F12 - Log Out
				//parent.frames["heading"].OpenClose();
			}
		
		
			if(ctrlPressed && !shiftPressed && !altPressed) {
				key.toUpperCase();
				//alert('KEY = '+key+'    CTRL = '+ctrlPressed+'  whichCode = '+whichCode);
				if(key=='W' || key=='E' || key=='N' || key=='O' || key=='L' || key=='P' || key=='A' || key=='H' || key=='R' || key=='S') {
					//alert(m3);
					return false;
				}
				if( key=='T') 
				{	
				parent.frames["heading"].OpenClose();
				return false;
				}
			}
		
		} catch(e) {}

	}

if(browser == "IE") {
	//alert("navigator.appName = "+navigator.appName);
<%
	if(FoursoftWebConfig.HANDLE_MOUSE_EVENTS) {
%>		document.onmousedown = handleMouseEvents;
<%	}
%>
	document.onkeydown = handleKeyDown;
}


if(browser == "NN") {
	//alert("navigator.appName = "+navigator.appName);
<%
	if(FoursoftWebConfig.HANDLE_MOUSE_EVENTS) {
%>		window.captureEvents(Event.MOUSEDOWN | Event.KEYPRESS);
		window.onmousedown = handleMouseEvents;
<%	} else {
%>		window.captureEvents(Event.KEYPRESS);
<%	}
%>
	window.onkeydown = handleKeyDown;
}

function checkEscape(input)
{
	var val ='';
	if(input!=null)
	{
		for(var i=0;i<input.length;i++)
		{
			var indexValue = input.charAt(i);
			if(indexValue=='"')
			{
			   indexValue = "\'";
			}
			val+=indexValue;	
		}
	}
	return val;
}

</script>
