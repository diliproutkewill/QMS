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
 % File			: ESMenuRightPanel.jsp
 % sub-module	: AccessControl
 % module		: esupply
 %
 % This is used to display menulist of modules for perticular user and perticular group. 
 % Group means setup ,operation ,reports etc.
 % 
 % author		: Sasi Bhushan. P
 % date			: 22-12-2001
 % Modification  history
 % Amit Parekh		24/10/2002			Logout functionality changed to handle higher level even handling
 % Amit Parekh		19/11/2002			A bug fix realted to warehouse switching i.e. swapping role permissions.
 % Amit Parekh		25/04/2003			A bug in sub menu display was fixed. The bug was related to allowing a limit of only 10 menus on the page.
 %										After fixing, the limit is now set to maximum of 20.
 % 
--%>
<%@ page import = "com.foursoft.esupply.accesscontrol.java.MenuModule, com.foursoft.esupply.accesscontrol.java.UserInterface, com.foursoft.esupply.accesscontrol.util.MenuUtility, com.foursoft.esupply.common.java.FoursoftWebConfig, java.util.Hashtable, org.apache.log4j.Logger,java.util.ArrayList,java.util.Enumeration,com.foursoft.esupply.accesscontrol.java.UIPermissions,java.util.ResourceBundle,javax.servlet.jsp.jstl.fmt.LocalizationContext" 
%>
<jsp:useBean id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt_rt" prefix="fmt_rt"%>
<%!
  private static Logger logger = null;
	String fileName	= "ESMenuRightPanel.jsp";	
%>
<%
  logger  = Logger.getLogger(fileName);
  String language = loginbean.getUserPreferences().getLanguage();
%>
<fmt_rt:setLocale value="<%=language%>"/>
	<fmt:setBundle basename="Lang" var="lang" scope="application"/>
<%
	//Logger.info(fileName, "session["+session.getId()+"] -- "+loginbean.getLocationId()+"/"+loginbean.getUserId()+" accessed : "+request.getParameter("link") );

ArrayList defaultSessionList = (ArrayList)session.getAttribute("DEFAULT_SESSION_LIST");
// Null pointer Exception checked by Santhosam
 if(defaultSessionList!=null){
	 Enumeration er = session.getAttributeNames();
	 while(er.hasMoreElements())
	 {
	  String sessionName = (String)er.nextElement();
	  if(!defaultSessionList.contains(sessionName))
	  {
	   session.removeAttribute(sessionName);
	   System.out.println("Removed from session:"+ sessionName);
	  }
	 }
 }
 // @@ ends here




    
	try {
		if(loginbean.getUserId() == null) {
			request.setAttribute("ErrorMessage","The User Session Has Expired! Please Re-Login.");
			%> <jsp:forward page="ESACSessionExpired.jsp" /> <%
		}
	} catch(NullPointerException ne) { }
%>
<%//Added by Sanjay as per singleton implementation
   UIPermissions uiPermissions    = UIPermissions.getInstance();
   UIPermissions uiPermissionsCRM = UIPermissions.getInstance();
   UIPermissions uiPermissionsVRM = UIPermissions.getInstance();
   UIPermissions uiPermissionsTRM = UIPermissions.getInstance();
   //Below code commented by Sanjay as per singleton implementation
  /* <jsp:useBean id ="uiPermissions"    class= "com.foursoft.esupply.accesscontrol.java.UIPermissions" scope ="application" />
     <jsp:useBean id ="uiPermissionsCRM" class= "com.foursoft.esupply.accesscontrol.java.UIPermissions" scope ="application" />
     <jsp:useBean id ="uiPermissionsVRM" class= "com.foursoft.esupply.accesscontrol.java.UIPermissions" scope ="application" />
     <jsp:useBean id ="uiPermissionsTRM" class= "com.foursoft.esupply.accesscontrol.java.UIPermissions" scope ="application" />*/
%>

<%!
	String styleStr = "";
	
	public void jspInit()
	{
		 //System.out.println("INIT CALLED ");
		 styleStr = "<style>"
		+"\nbody {"

		 +"\nfont-family: verdana,Arial, Helvetica, sans-serif;"

		 +"\nfont-size: 10pt;"

		 +"\ncolor: #000000;"

		 +"\nbackground:#ffffff;"

		 +"\nmargin-left:0px;"

		 +"\nmargin-right:0px;"

		 +"\nmargin-width:0px;"

		 +"\nmargin-height:0px;"

		 +"\nmargin-top:18px;"

		+"\n}"



		+"\na:link { color: #000000; }"

		+"\na:visited { color: #222222; }"

		+"\na:active { color: #444444; }"

		+"\na:hover { color: #778899; }"



		+"\n.8pt {font-size:8pt; font-family:verdana;}"

		+"\n.8ptBold { font-size:8pt; font-weight:bold; font-family:verdana;}"

		+"\n.10ptRed { color:#ff0000; }"

		+"\n.10ptBold { font-size:10pt; font-weight:bold; font-family:verdana;}"

		+"\n.10ptBoldBlue { font-size: 10pt; font-weight:bold; font-family:verdana; color:#336699;}"

		+"\n.12pt {font-size:12pt; font-family:verdana;}"

		+"\n.12ptBold { font-size: 12pt; font-weight:bold; font-family:verdana;}"

		+"\n.12ptBoldBlue { font-size: 12pt; font-weight:bold; font-family:verdana; color:#336699;}"

		+"\n.14pt { font-size: 18pt; font-family:verdana;}"

		+"\n.14ptBold { font-size: 18pt; font-weight:bold; font-family:verdana;}"

		+"\n.16pt { font-size: 18pt; font-family:verdana;}"

		+"\n.16ptBold { font-size: 18pt; font-weight:bold; font-family:verdana;}"

		+"\n.16ptBoldBlue { font-size: 16pt; font-weight:bold; font-family:verdana; color:#336699;}"

		+"\n.18pt { font-size: 18pt; font-family:verdana;}"

		+"\n.18ptBold { font-size: 18pt; font-weight:bold; font-family:verdana;}"



		+"\ntd {"

		 +"\nfont-family: verdana, verdana, Helvetica, sans-serif;"

		 +"\nfont-size: 10pt;"

		 +"\nbackground: #aabbbb;"

		 +"\nborder-style: groove;"

		 +"\nborder-width:2px;"

		 +"\nborder-color:#ffffff;"

		 +"\npadding:2px;"

		+"\n}"





		+"\n.Grey {background: #dddddd; border-width:0px;}"

		+"\n.GreyBox {background: #dddddd;}"

		+"\n.GreyList{background: #dddddd; border-width:0px; padding-top:20px; padding-bottom:5px; border-bottom-width:2px;}"

		+"\n.clear {background: #ffffff; border-width:0px; padding-left:0px; padding-right:0px;}"

		+"\n.Clear {background: #ffffff; border-width:0px; padding-left:0px; padding-right:0px;}"



		+"\n.CodeExample{background: #eeeeee; font-family: Courier, Helvetica, verdana;}"

		+"\n.Figure{background: #eeeeee;}"

		+"\n.White {background: #ffffff; border-width:0px; padding-left:10px; padding-right:5px;}"

		+"\n.WhiteBox {background: #ffffff;}"

		+"\n.WhiteList{background: #ffffff; border-width:0px; padding-top:20px; padding-bottom:5px; border-bottom-width:2px; padding-left:10px; padding-right:5px;}"

		+"\n.LightGrey {background: #eeeeee; border-width:0px;}"

		+"\n.LightGreyBox {background: #eeeeee;}"

		+"\n.LightGreyBoxTPad {background: #eeeeee; border-top-width:0px; padding-left:10px; padding-top:0px; margin-top:0px;}"

		+"\n.LightGreyList{background: #eeeeee; border-width:0px; padding-top:20px; padding-bottom:5px; border-bottom-width:2px; padding-left:10px; padding-right:5px;}"

		+"\n.OffWhite {background: #f5f5f5; border-width:0px;}"

		+"\n.OffWhiteBox {background: #f5f5f5;}"

		+"\n.OffWhiteList{background: #f5f5f5; border-width:0px; padding-top:20px; padding-bottom:5px; border-bottom-width:2px; padding-left:10px; padding-right:5px;}"

		+"\n.OffWhiteBoxT {background: #f5f5f5; border-top-width:0px;}"

		+"\n.OffWhiteBoxL {background: #f5f5f5; border-left-width:0px;}"

		+"\n.OffWhiteBoxTPad {background: #f5f5f5; border-top-width:0px; padding-left:10px; padding-top:0px; margin-top:0px;}"

		+"\n.Green {background: #aabbbb; border-width:0px;}"

		+"\n.GreenBox {background: #aabbbb;}"

		+"\n.GreenBoxPadding0 {background: #aabbbb; padding:0px;}"

		+"\n.GreenList {background: #aabbbb; border-width:0px; padding-top:20px; padding-bottom:5px; border-bottom-width:2px; padding-left:10px; padding-right:5px;}"

		+"\n.Blue {background: #99aabb; border-width:0px;}"

		+"\n.BlueBox {background: #99aabb;}"

		+"\n.BlueList {background: #99aabb; border-width:0px; padding-top:20px; padding-bottom:5px; border-bottom-width:2px;}"



		+"\n.menuBar {"

		+"\n position:absolute;"

		+"\n top:0px;"

		+"\n}"



		+"\n.menuItem {"

		+"\n background: #D4D0C8;"

		+"\n border-style: groove;"

		+"\n border-width:2px;"

		+"\n border-top-width:0px;"

		 +"\nborder-color:#ffffff;"

		+"\n padding:0px;"

		+"\n padding-left:12px;"

		+"\n padding-right:12px;"

		+"\n font-size:10pt;"

		+"\n font-family:verdana;"

		+"\n}"



		+"\n.menuButton {"

		+"\n background: #D4D0C8;"

		+"\n border-style: groove;"

		 +"\nborder-width:2px;"

		 +"\nborder-left-width:0px;"

		 +"\nborder-color:#ffffff;"

		 +"\npadding:0px;"

		 +"\npadding-left:12px;"

		 +"\npadding-right:12px;"

		 +"\nfont-size:10pt;"

		 +"\nfont-family:verdana;"

		+"\n}"



		+"\n.menuLink {"

		 +"\nfont-size:10pt;"

		 +"\nfont-family:verdana;"

		+"\n}"



		+"\n.menuLink:hover {"

		 +"\ncolor:#000000;"

		+"\n}"



		+"\ntd.Nav {background:#dddddd; border-top-width:0;padding-top:0px; padding-bottom:0px; font-family:verdana;}"

		+"\nA.menu:VISITED {color:#000000;}"

		+"\nA.menu:HOVER {color:#eeeeee;}"

		+"\nA.GreenBox:HOVER {color:#eeeeee;}"



		+"\n.empty {"

		  +"\npadding:0px;"

		  +"\nmargin:0px;"

		  +"\nborder:0px;"

		  +"\nbackground:#ffffff;"

		 +"\n}"



		+"\n.codehighlight {background-color:#ffbbbb;}"



		+"\n.element {"

		+"\n	padding:0px;"

		+"\n	padding-left:10px;"

		+"\n	border-width:0px;"

		+"\n	text-decoration:none;"

		 +"\n}"

		+"\n.one:link{"

		+"\n	text-decoration:none;"

		+"\n	color:black;"

		+"\n}"

		+"\n.zeroormore:link {"

		+"\n	text-decoration:none;"

		+"\n	color:purple;"

		+"\n}"

		+"\n.oneormore:link {"

		+"\n	text-decoration:none;"

		+"\n	color:red;"

		+"\n}"

		+"\n.zeroorone:link {"

		+"\n	color:blue;"

		+"\n	text-decoration:none;"

		+"\n}"

		+"\n.oneof:link {"

		+"\n	color:orange;"

		+"\n	text-decoration:none;"

		+"\n}"

		+"\n.one:visited{"

		+"\n	text-decoration:none;"

		+"\n	color:black;"

		+"\n}"

		+"\n.zeroormore:visited {"

		+"\n	text-decoration:none;"

		+"\n	color:purple;"

		+"\n}"

		+"\n.oneormore:visited {"

		+"\n	text-decoration:none;"

		+"\n	color:red;"

		+"\n}"

		+"\n.zeroorone:visited {"

		+"\n	color:blue;"

		+"\n	text-decoration:none;"

		+"\n}"

		+"\n.oneof:visited {"

		+"\n	color:orange;"

			+"\ntext-decoration:none;"

		+"\n}"
		+"\n</style>";
	}
%>
<%
	String browserName = (String)session.getAttribute("browserName");
	String linkName  = request.getParameter("link");
	//Logger.info(fileName," Browsername is:"+browserName);
	//Logger.info(fileName," BEFORE STARTING MENU:"+new java.sql.Timestamp(new Date().getTime()));
	Hashtable alevel = null;

	String	userType	=	(String)	loginbean.getUserType();

	ArrayList tabList = new ArrayList(0);

	if(FoursoftWebConfig.MODULE.equals("EP")) {
		if(userType.equals("EPC")) {
			tabList	=	(ArrayList) uiPermissionsCRM.transactionsTable.get(linkName);
		} else if(userType.equals("EPV")) {
			tabList	=	(ArrayList) uiPermissionsVRM.transactionsTable.get(linkName);
		} else if(userType.equals("EPT")) {
			tabList	=	(ArrayList) uiPermissionsTRM.transactionsTable.get(linkName);
		} else {
			tabList	=	(ArrayList) uiPermissions.transactionsTable.get(linkName);
		}
	} else if(FoursoftWebConfig.MODULE.equals("ELOG") || FoursoftWebConfig.MODULE.equals("SP")) {
		if(userType.equals("ELC")) {
			tabList	=	(ArrayList) uiPermissionsCRM.transactionsTable.get(linkName);
		} else if(userType.equals("ELV")) {
			tabList	=	(ArrayList) uiPermissionsVRM.transactionsTable.get(linkName);
		}
		else if(userType.equals("ELT")) {
			tabList	=	(ArrayList) uiPermissionsTRM.transactionsTable.get(linkName);
		}
		else if(userType.equals("ESC")) {
			tabList	=	(ArrayList) uiPermissionsCRM.transactionsTable.get(linkName);
		} else if(userType.equals("ESV")) {
			tabList	=	(ArrayList) uiPermissionsVRM.transactionsTable.get(linkName);
		}
		else {
			tabList	=	(ArrayList) uiPermissions.transactionsTable.get(linkName);
		}
	} else {
		tabList	=	(ArrayList) uiPermissions.transactionsTable.get(linkName);
	}
	

	//Logger.info(fileName,"  AFTER GETTING ARRAYLIST");
	String [] scriptArray;
	
	//if(session.getAttribute("ACCESSTYPE").equals("WAREHOUSE")) {
		alevel = (Hashtable)session.getAttribute("proTable");
	//} else {
	//	alevel = (Hashtable)session.getAttribute("accessList");
	//}
	
	if(browserName.equalsIgnoreCase("Netscape")) {
		scriptArray = new MenuUtility().getNSScript( tabList, alevel );
	} else {
		scriptArray = new MenuUtility().getIEScript( tabList, alevel );
	}
	//Logger.info(fileName," AFTER MENU:"+new java.sql.Timestamp(new Date().getTime()));

%>
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "DTD/xhtml1-transitional.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title><fmt:message key="100319" bundle="${lang}"/></title>

<%= styleStr %>
<script language="javascript">

	<%=scriptArray[0]%>
	}

	
	function hideOthers(column, row) 
{
 
 // Hide all of the sub-menus except the one that should be showing and its super-menus.
 
 for(column_count = 0; document.getElementById("subMenu" + column_count + "0") != null; column_count++)
 {
 
      if ( column == column_count )
      {
           var hide = false;
           for (row_count = 0; document.getElementById("subMenu" + column_count + row_count) != null; row_count++)
           {
                if (row_count == row)
                 {
                 hide = true;
                 }
                if (hide)
                 {
                 var currentElement = document.getElementById("subMenu"+ column_count + "0");
                 currentElement.style.visibility = "visible";
                 }
           }
      }
      else
       {
       document.getElementById("subMenu"+column_count+"0").style.visibility = "hidden";
       }
  }
}



// Hide all the menus

function hide(id){

	// Very poorly done, however works...need to glamorize this later

	var currentElement = document.getElementById("clearAll");

	if(currentElement != null) currentElement.style.visibility = "hidden";

}



// Shows the submenu for the menu item

function showSubMenu(column, row) {

	hideOthers(column, row);

	// Make the background layer visible for catching the mouse as it leaves the menu

	var currentElement = document.getElementById("clearAll");
	currentElement.style.visibility = "visible";

	// Show the correct sub-menu

	if(document.getElementById("subMenu"+column+row) != null)
	{
		var currentElement = document.getElementById("subMenu"+column+row);
		currentElement.style.visibility = "visible";
		lowLightAll();
	}

}

</script>
<% 
   response.setHeader("Cache-Control","no-cache"); // HTTP 1.1
   response.setHeader("Pragma","no-cache"); // HTTP 1.0
   response.setDateHeader ("Expires", -1); // Prevents caching at the proxy server
%>
</head>

<body
	onload="init(); showLastLink(); parent.frames['text'].focus();"
	onKeyDown="handleKeyDown(event);"
	style="margin-bottom:0px; padding-bottom:0px;margin-top:14px;">
<noscript> <fmt:message key="100045" bundle="${lang}"/>  <a href="http://www.jspinsider.com/main/sitemap.html"><fmt:message key="100046" bundle="${lang}"/></a> <fmt:message key="100047" bundle="${lang}"/></noscript>
<div id="clearAll" style="position:absolute; top:0px; left:0px; width:100%; height:100%;" onMouseOver="hideOthers(20,20);">
</div>


<div id="sitemap" style="position:absolute; top:0px; left:0px; width:100%;">
 <table cellpadding="0" cellspacing="0" width="100%">
  <tr>
   <td class="menuButton" width="100%">
   <fmt:message key="100048" bundle="${lang}"/>
   </td>
  </tr>
 </table>
</div>
<%=scriptArray[1]%> 
<%=scriptArray[2]%>
<script>
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
	
	function handleMouseEvents(e)
	{	
		
		if(browser == "IE") {
			event.cancelBubble = true;
			if(document.all) {
				if(event.button==2 || event.button==3) {
					alert(m1);
					return false;
				}
				if(event.button==1) {
					var shiftPressed = (window.Event) ? e.modifiers & Event.SHIFT_MASK : window.event.shiftKey;
					if(shiftPressed) {
						alert(m2);
						return false;
					}
				}
			}
		}
			
		if(browser == "NN") {
			e.cancelBubble = true;
			//alert("e.which = "+ e.which);
			if(e.which == 2 || e.which == 3) { 
				alert(m1);
				return false;
			}
		}
	}
	

	if(browser == "IE") {
		//alert("navigator.appName = "+navigator.appName);
		document.onmousedown = handleMouseEvents;
	}

	if(browser == "NN") {
		//alert("navigator.appName = "+navigator.appName);
		window.captureEvents(Event.MOUSEDOWN);
		window.onmousedown = handleMouseEvents;
	}
	
	function showLastLink() {
		//alert("FROM RIGHT MENU....LAST VISITED LINK = "+parent.frames["code"].lastClickedLink );
		parent.frames["menu"].showLastClickedLink();
	}


<%
	boolean hasModifier = false;
	if(FoursoftWebConfig.MENU_HOTKEY_MODIFIER != null && FoursoftWebConfig.MENU_HOTKEY_MODIFIER.length() > 0) {
		hasModifier	= true;
	}
%>

	function handlePress(e)
	{
		var altPressed 		= (window.Event) ? e.modifiers & Event.ALT_MASK : e.altKey;
		var shiftPressed 	= (window.Event) ? e.modifiers & Event.SHIFT_MASK : e.shiftKey;
		
		var whichCode 		= (window.Event) ? e.which : e.keyCode;
		var key 			= String.fromCharCode(whichCode);

<%	if(FoursoftWebConfig.MENU_HOTKEY_MODIFIER.equals("SHIFT")) {
%>		if(shiftPressed)  {
<%	}	%>
<%	if(FoursoftWebConfig.MENU_HOTKEY_MODIFIER.equals("ALT")) {
%>		if(altPressed)  {
<%	}	%>		
		
			<%=scriptArray[3]%>
			
			showSubMenu(currentMenu,0);		
			document.getElementById("subMenu"+currentMenu+"01").focus();
			focussedElement = 1;
			lowLightAll();
			highLight();
<%	if(hasModifier) {
%>		}
<%	}
%>
		
	}
	
	function handleKeyDown(e)
	{
		
		var altPressed 		= (window.Event) ? e.modifiers & Event.ALT_MASK : e.altKey;
		var shiftPressed 	= (window.Event) ? e.modifiers & Event.SHIFT_MASK : e.shiftKey;
		var ctrlPressed 	= (window.Event) ? e.modifiers & Event.CTRL_MASK : e.ctrlKey;
		
		var whichCode 		= (window.Event) ? e.which : e.keyCode;
		var key 			= String.fromCharCode(whichCode);

		if(!ctrlPressed && !shiftPressed && !altPressed) {
			if(whichCode==116) {	// F5 - Place focus on the menu tree
				//parent.frames['heading'].key = "F5";
				parent.key = "F5";
			}
			if(whichCode==118) {	// F7 - Place focus on the menu tree
				//alert("F7 in Menu Panel: whichCode = "+whichCode);
				hideOthers(20,20);
				lowLightAll();
				parent.frames['menu'].focus();
			}
			if(whichCode==119) {	// F8 - Place focus on the Right Menu
				//alert("F8 in Menu Panel: whichCode = "+whichCode);
				parent.frames['text'].focus();
			}
			if(whichCode==120) {	// F9 - Place focus on the Heading bar
				//alert("F9 in Menu Panel: whichCode = "+whichCode);
				hideOthers(20,20);
				lowLightAll();
				parent.frames['heading'].focus();
			}
			if(whichCode==123) {	// F12 - Log Out
				parent.frames["heading"].document.images("logoutImage").click();
			}
		}


<%	if(FoursoftWebConfig.MENU_HOTKEY_MODIFIER.equals("SHIFT")) {
%>		if(shiftPressed && whichCode!=27 && whichCode!=37 && whichCode!=38 && whichCode!=39 && whichCode!=40) //&& e.ctrlKey
		{
<%	} else	if(FoursoftWebConfig.MENU_HOTKEY_MODIFIER.equals("ALT")) {
%>		if(altPressed && whichCode!=27 && whichCode!=37 && whichCode!=38 && whichCode!=39 && whichCode!=40) //&& e.ctrlKey
		{
<%	} else {
%>		if(whichCode!=27 && whichCode!=37 && whichCode!=38 && whichCode!=39 && whichCode!=40) //&& e.ctrlKey
		{
<%	}
%>
			<%=scriptArray[3]%>
			
			showSubMenu(currentMenu,0);		
			document.getElementById("subMenu"+currentMenu+"01").focus();
			focussedElement = 1;
			lowLightAll();
			highLight();
			
		}
		
		var currentElement 	= document.getElementById("clearAll");
		
		var myObj 			= document.getElementById("subMenu"+currentMenu+"0").getElementsByTagName("td");
		var myMenu 			= document.getElementById("menuBar").getElementsByTagName("td");
		



		if(whichCode==40 || whichCode==38) // 'Up' or 'Down' Arrow keys
		{
			focussedElement = (whichCode==38)?focussedElement-1:focussedElement+1;

			if(focussedElement>myObj.length)
				focussedElement=1;
			else if(focussedElement<=0)
				focussedElement=myObj.length;
				
			document.getElementById("subMenu"+currentMenu+"0"+focussedElement).focus();

			if(document.all)
				event.returnValue=false;
			else
				e.returnValue=false;
		}
		
		if(whichCode==39 || whichCode==37) // 'Left' or 'Right' arrow keys
		{
			currentMenu = (whichCode==37)? currentMenu-1:currentMenu+1;
			if(currentMenu>=myMenu.length)
				currentMenu = 0;
			if(currentMenu<0)
				currentMenu = myMenu.length-1;
			showSubMenu(currentMenu,0);
			document.getElementById("subMenu"+currentMenu+"01").focus();
			focussedElement = 1;
		}
		
		lowLightAll();
		highLight();
		
		if(whichCode==27)	// 'ESCAPE' key is pressed
		{
			hideOthers(20,20);
			lowLightAll();
		}
		
		if(ctrlPressed && !shiftPressed && !altPressed) {
			key.toUpperCase();
			//alert('KEY = '+key+'    CTRL = '+ctrlPressed+'  whichCode = '+whichCode);
			if(key=='W' || key=='E' || key=='N' || key=='O' || key=='L' || key=='P' || key=='A' || key=='H' || key=='R' || key=='S') {
				alert(m3);
				return false;
			}
		}

	}
	
	function highLight(myObj)
	{
		//alert("currentMenu = "+currentMenu+",  focussedElement = "+focussedElement);

		var obj;
		var obj1;

		if(myObj!=null)
		{
			obj	 = myObj;
			obj1 = document.getElementById(myObj.getAttribute("id").substring(2,myObj.getAttribute("id").length));
		}
		else
		{
			obj	 = document.getElementById("tdsubMenu"+currentMenu+"0"+focussedElement);
			obj1 = document.getElementById("subMenu"+currentMenu+"0"+focussedElement);
		}

		if(obj!=null && obj1!=null)
		{
			//alert("obj and obj1 not null");
			obj.style.backgroundColor	= "#B0B0B0";
			//obj1.style.visibility		= "visible";
			obj1.style.color			= "BLACK";
		}
	}

	function lowLightAll()
	{
		var myMenu = document.getElementById("menuBar").getElementsByTagName("td");
		
		for(i=0;i<myMenu.length;i++)
		{
			var myObj= document.getElementById("subMenu"+i+"0").getElementsByTagName("td");
			
			for(j=1;j<=myObj.length;j++)
			{
				var obj= document.getElementById("tdsubMenu"+i+"0"+j);
				var obj1= document.getElementById("subMenu"+i+"0"+j);

				if(obj!=null)
				{
					obj.style.backgroundColor	= ""
					//obj1.style.visibility		= "visible";
					obj1.style.color			= "black"
					obj1.style.fontWeight		= ""
					obj1.style.fontSize			= ""
					obj1.style.fontStyle		= ""
				}
			}
		}
	}

	function stopError()
	{
		//alert("focussedElement "+focussedElement+" currentMenu "+currentMenu);
		return true;
	}
	
	window.onerror		= stopError;
	var focussedElement = 1;
	var currentMenu 	= 0;

</script>