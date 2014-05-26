<%@ page import= "org.apache.log4j.Logger, java.util.Hashtable, java.util.Properties, java.util.Enumeration,java.net.URL,java.io.*,com.foursoft.esupply.accesscontrol.java.UIPermissions,com.foursoft.esupply.accesscontrol.util.TreeUtility,java.util.ResourceBundle" %>
<%@ page import="com.foursoft.esupply.common.exception.FoursoftException, com.foursoft.esupply.common.bean.UserCredentials, com.foursoft.esupply.common.java.FoursoftWebConfig, com.foursoft.esupply.delegate.AccessControlDelegate,javax.servlet.jsp.jstl.fmt.LocalizationContext"	%>
<jsp:useBean id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/>

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
 % File			: ESMenuTreeGenerator.jsp
 % sub-module	: AccessControl
 % module		: esupply
 %
 % This is used to Generate the Tree
 % 
 % author		: Sasi Bhushan. P
 % date			: 22-12-2001
 % Modification  history
 % Amit Parekh		24/10/2002		Logout functionality changed to handle higher level even handling
 % Amit Parekh		30/10/2002		Error handling for login made more robust
 % Amit Parekh		14/11/2002		Functionality added for switcing Warehouse in EP at COMPANY level and
 % 									switchng Customer Warehouses in ELOG at WAREHOUSE level.
 %										
 %									The file wh.properties is PHASED OUT with this version for both EP and ELOG
 %										
 %									Additionally for ELOG/EP, not all the customer warehouse/warehouses
 %									will come by default but only those will come for which a User has
 %									got some permissions i.e. a optional role has been assigned.
 %									In other owrds only those child warehouse for which an optional role
 %									is defined for the current login User, will appear in the select box
 %									of the Menu heading
 % Amit Parekh		19/11/2002		Changes made to handle facility switching in EP for a COMPANY User and
 %									warehouse customer location switching for a WAREHOUSE level User in ELOG
 % Amit Parekh		27/02/2003		Change sone to make switching of warehouses/locations and thus roles possible in SP.
--%>

<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt_rt" prefix="fmt_rt"%>
<%!
  private static Logger logger = null;
	String fileName	= "ESMenuTreeGenerator.jsp";	
%>
<%
  logger  = Logger.getLogger(fileName);
  String language = loginbean.getUserPreferences().getLanguage();
%>
<fmt_rt:setLocale value="<%=language%>"/>
	<fmt:setBundle basename="Lang" var="lang" scope="application"/>
<%//Added by Sanjay as per singleton implementation
   UIPermissions uiPermissions    = UIPermissions.getInstance();
   UIPermissions uiPermissionsCRM = UIPermissions.getInstance();
   UIPermissions uiPermissionsVRM = UIPermissions.getInstance();
   UIPermissions uiPermissionsTRM = UIPermissions.getInstance();
 /*<jsp:useBean id ="uiPermissions"    class= "com.foursoft.esupply.accesscontrol.java.UIPermissions" scope ="application" />
 <jsp:useBean id ="uiPermissionsCRM" class= "com.foursoft.esupply.accesscontrol.java.UIPermissions" scope ="application" />
 <jsp:useBean id ="uiPermissionsVRM" class= "com.foursoft.esupply.accesscontrol.java.UIPermissions" scope ="application" />
 <jsp:useBean id ="uiPermissionsTRM" class= "com.foursoft.esupply.accesscontrol.java.UIPermissions" scope ="application" />*/
 %>
<%
    
	boolean	errorOcurred	=	false;
    AccessControlDelegate accessDelegate = new AccessControlDelegate();

	String	accessType		=	(String) session.getAttribute("ACCESSTYPE");
	String	userType		=	loginbean.getUserType();

	UserCredentials	uc		=	(UserCredentials) session.getAttribute("userCredentials");

	String	windowName		=	(String)	uc.getWindowName();
	String	loginPage		=	"ESupplyLogin?userType="+userType+"&windowName="+windowName;
	String	errorMessage	=	"An error occurred in the Log-in process. Please try again.";

	//Logger.info(fileName, "Login Page = "+loginPage);

	try {
	
		// This is the default XML file that assumes that the User is logging into the Core Application
		String	xmlMenuFile =	"menu-config.xml";

		if(FoursoftWebConfig.MODULE.equals("EP")) 
		{
			if(userType.equals("EPC")) 
				xmlMenuFile	=	"menu-config-epc.xml";
			
			else if(userType.equals("EPV")) 				
				xmlMenuFile	=	"menu-config-epv.xml";
			
			else if(userType.equals("EPT")) 
				xmlMenuFile	=	"menu-config-ept.xml";
			
			else 
				xmlMenuFile =	"menu-config.xml";
			
		} 
		else if(FoursoftWebConfig.MODULE.equals("ELOG")) 
		{
			if(userType.equals("ELC"))
				xmlMenuFile	=	"menu-config-elc.xml";
			
			else if(userType.equals("ELV")) 
				xmlMenuFile	=	"menu-config-elv.xml";

			else if(userType.equals("ELT")) 
				xmlMenuFile	=	"menu-config-elt.xml";
			
			else
				xmlMenuFile =	"menu-config.xml";
		
		} 
		else if(FoursoftWebConfig.MODULE.equals("SP")) 
		{
			if(userType.equals("ESC")) 
				xmlMenuFile	=	"menu-config-esc.xml";

			else if(userType.equals("ESV"))
				xmlMenuFile	=	"menu-config-esv.xml";

			else 
				xmlMenuFile =	"menu-config.xml";
			
		}
		else
		{
			xmlMenuFile =	"menu-config.xml";
		}

		URL u = application.getResource("/WEB-INF/xml/"+xmlMenuFile);
		String configFile = u.toString();
		//Logger.info(fileName,"configFile : "+configFile);
    logger.info(fileName+"configFile : "+configFile);
	

		if(FoursoftWebConfig.MODULE.equals("EP")) 
		{
			if(userType.equals("EPC")) 
			{
				if(uiPermissionsCRM.transactionsTable == null) 
				{
					uiPermissionsCRM.process(configFile);
				}
			} 
			else if(userType.equals("EPV")) 
			{
				if(uiPermissionsVRM.transactionsTable == null) 
				{
					uiPermissionsVRM.process(configFile);
				}
			} 
			else if(userType.equals("EPT")) 
			{
				if(uiPermissionsTRM.transactionsTable == null) 
				{
					uiPermissionsTRM.process(configFile);
				}
			} 
			else 
			{
				if(uiPermissions.transactionsTable == null) 
				{
					uiPermissions.process(configFile);
				}
			}

		} 
		else if(FoursoftWebConfig.MODULE.equals("ELOG")) 
		{
			if(userType.equals("ELC")) 
			{
				if(uiPermissionsCRM.transactionsTable == null) 
				{
					uiPermissionsCRM.process(configFile);
				}
			} 
			else if(userType.equals("ELV")) 
			{
				if(uiPermissionsVRM.transactionsTable == null) 
				{
					uiPermissionsVRM.process(configFile);
				}
			} 
			else if(userType.equals("ELT"))
			{
				if(uiPermissionsTRM.transactionsTable == null)
				{
					uiPermissionsTRM.process(configFile);
				}
			}
			else 
			{
				if(uiPermissions.transactionsTable == null) 
				{
					uiPermissions.process(configFile);
				}
			}
		} 
		else if(FoursoftWebConfig.MODULE.equals("SP")) 
		{
			if(userType.equals("ESC")) 
			{
				if(uiPermissionsCRM.transactionsTable == null) 
				{
					uiPermissionsCRM.process(configFile);
				}
			} 
			else if(userType.equals("ESV")) 
			{
				if(uiPermissionsVRM.transactionsTable == null) 
				{
					uiPermissionsVRM.process(configFile);
				}
			} 
			else 
			{
				if(uiPermissions.transactionsTable == null) 
				{
					uiPermissions.process(configFile);
				}
			}
		} 
		else 
		{
			if(uiPermissions.transactionsTable == null) 
			{
				uiPermissions.process(configFile);
			}
		}

		Hashtable alevel = null;
	
		int shipmentMode = ((Integer) session.getAttribute("shipmentMode")).intValue();

		String	sessionAccessType	=	(String) session.getAttribute("ACCESSTYPE");

		//System.out.println("AMIT...sessionAccessType = "+sessionAccessType);
		
	
		if(	(sessionAccessType.equals("WAREHOUSE") && (FoursoftWebConfig.MODULE.equals("ELOG") || FoursoftWebConfig.MODULE.equals("SP")) ) ||
			(sessionAccessType.equals("COMPANY")   && FoursoftWebConfig.MODULE.equals("EP")) )
		{
			String	isWareHouse		=	(String) session.getAttribute("isWareHouse");

			String	loginAccessType	=	loginbean.getAccessType();

			//System.out.println("AMIT...loginAccessType = "+loginAccessType);

			if(loginAccessType.equals( sessionAccessType )) {
				/*	If the access type in loginbean and the session are same, it means that
					this is the actual initial access type and just give the permissions from the
					accessList cached in session.
				*/
				alevel = (Hashtable) session.getAttribute("accessList");
			}
			else
			{	
				String userLocationId	=	"";
				
				/*	If the access type in loginbean and the session are NOPT EQUAL, it means that
					this is not the actual initial access type.
					We will have to fetch the Optional Role for the current access type for
					the WAREHOUSE in EP (or CUSTWH in ELOG)
					
					The actual accessList will remain cached and untouched in session.
					The Optional Role accessList will be assigned dynamically to the User
				*/

				// Get the namw of the warehouse / customer warehouse
				if(sessionAccessType.equals("WAREHOUSE") && (FoursoftWebConfig.MODULE.equals("ELOG") || FoursoftWebConfig.MODULE.equals("SP")))
				{
					userLocationId		=	loginbean.getWareHouseId();
				}

				if(sessionAccessType.equals("COMPANY") && FoursoftWebConfig.MODULE.equals("EP"))
				{
					userLocationId		=	loginbean.getCompanyId();
				}

				// Here we have to make a remote call to get the access List according to the role
				// for the warehouse. The one kept in session will not do.

				//alevel	= remote.getRolePermissions( loginbean.getRoleId(), loginbean.getRoleLocationId() );
				alevel	= accessDelegate.getOptionalRolePermissions( loginbean.getUserId(), userLocationId, loginbean.getLocationId() );
				//alevel	= remote.getOptionalRolePermissions( loginbean.getUserId(), userLocationId, loginbean.getLocationId() );

				//if(alevel==null || alevel.size()==0) {
				//	//System.out.println("AMIT.......alevel is NULL");
				//}
				session.removeAttribute("isWareHouse");

			}
		
		}
		else // DEFAULT CASES follow the simple approach
		{
			alevel = (Hashtable) session.getAttribute("accessList");
			//System.out.println("AMIT......USING SAME ACCESS LIST");
			
		}
%>
<%
    TreeUtility treeUtility = TreeUtility.getInstance();
    TreeUtility treeUtilityCRM = TreeUtility.getInstance();
    TreeUtility treeUtilityVRM = TreeUtility.getInstance();
    TreeUtility treeUtilityTRM = TreeUtility.getInstance();
    /*<jsp:useBean id ="treeUtility"    class= "com.foursoft.esupply.accesscontrol.util.TreeUtility" scope ="application" />
    <jsp:useBean id ="treeUtilityCRM" class= "com.foursoft.esupply.accesscontrol.util.TreeUtility" scope ="application" />
    <jsp:useBean id ="treeUtilityVRM" class= "com.foursoft.esupply.accesscontrol.util.TreeUtility" scope ="application" />
    <jsp:useBean id ="treeUtilityTRM" class= "com.foursoft.esupply.accesscontrol.util.TreeUtility" scope ="application" />*/
%>
<%
		// This is the default XML file that assumes that the User is logging into the Core Application
		String	xmlTreeFile	=	"tree-config.xml";

		if(FoursoftWebConfig.MODULE.equals("EP")) 
		{
			if(userType.equals("EPC")) 
				xmlTreeFile	=	"tree-config-epc.xml";
			
			else if(userType.equals("EPV"))
				xmlTreeFile	=	"tree-config-epv.xml";
			
			else if(userType.equals("EPT"))
				xmlTreeFile	=	"tree-config-ept.xml";
			
			else 
				xmlTreeFile	=	"tree-config.xml";
			
		} 
		else if(FoursoftWebConfig.MODULE.equals("ELOG")) 
		{
			if(userType.equals("ELC")) 
				xmlTreeFile	=	"tree-config-elc.xml";
			
			else if(userType.equals("ELV")) 
				xmlTreeFile	=	"tree-config-elv.xml";
			
			else if(userType.equals("ELT")) 
				xmlTreeFile	=	"tree-config-elt.xml";
			
			else 
				xmlTreeFile	=	"tree-config.xml";
			
		} 
		else if(FoursoftWebConfig.MODULE.equals("SP")) 
		{
			if(userType.equals("ESC")) 
				xmlTreeFile	=	"tree-config-esc.xml";

			else if(userType.equals("ESV")) 
				xmlTreeFile	=	"tree-config-esv.xml";

			else 
				xmlTreeFile	=	"tree-config.xml";
		
		} 
		else 
		{
			xmlTreeFile	=	"tree-config.xml";
		}


		URL u1 = application.getResource("/WEB-INF/xml/"+xmlTreeFile);
		String treeFile = u1.toString();
		//Logger.info(fileName,"treeFile : "+treeFile);	
    logger.info(fileName+"treeFile : "+treeFile);	

		long presentTime = new java.io.File(treeFile).lastModified();

		session.setAttribute("proTable",alevel);

		if(FoursoftWebConfig.MODULE.equals("EP")) 
		{
			if(userType.equals("EPC")) 
				treeUtilityCRM.setMenuListTable( uiPermissionsCRM.getLeafTable(alevel) );
			
			else if(userType.equals("EPV")) 
				treeUtilityVRM.setMenuListTable( uiPermissionsVRM.getLeafTable(alevel) );
			
			else if(userType.equals("EPT")) 
				treeUtilityTRM.setMenuListTable( uiPermissionsTRM.getLeafTable(alevel) );
			
			else 
				treeUtility.setMenuListTable( uiPermissions.getLeafTable(alevel) );
	
		} 
		else if(FoursoftWebConfig.MODULE.equals("ELOG")) 
		{
			if(userType.equals("ELC")) 
				treeUtilityCRM.setMenuListTable( uiPermissionsCRM.getLeafTable(alevel) );
			
			else if(userType.equals("ELV")) 
				treeUtilityVRM.setMenuListTable( uiPermissionsVRM.getLeafTable(alevel) );
			
			else if(userType.equals("ELT")) 
				treeUtilityTRM.setMenuListTable( uiPermissionsTRM.getLeafTable(alevel) );
			
			else 
				treeUtility.setMenuListTable( uiPermissions.getLeafTable(alevel) );
			
		} 
		else if(FoursoftWebConfig.MODULE.equals("SP")) 
		{
			if(userType.equals("ESC")) 
				treeUtilityCRM.setMenuListTable( uiPermissionsCRM.getLeafTable(alevel) );
			
			else if(userType.equals("ESV")) 
				treeUtilityVRM.setMenuListTable( uiPermissionsVRM.getLeafTable(alevel) );
			
			else 
				treeUtility.setMenuListTable( uiPermissions.getLeafTable(alevel) );
			
		} 
		else 
			treeUtility.setMenuListTable( uiPermissions.getLeafTable(alevel) );
	
	
		try 
		{
			if(FoursoftWebConfig.MODULE.equals("EP")) 
			{
				if(userType.equals("EPC")) 
				{
					if(treeUtilityCRM.xmlDocument == null) 
					{
						treeUtilityCRM.setTreeFile(treeFile);
						treeUtilityCRM.parse();
					}
				} 
				else if(userType.equals("EPV")) 
				{
					if(treeUtilityVRM.xmlDocument == null)
					{
						treeUtilityVRM.setTreeFile(treeFile);
						treeUtilityVRM.parse();
					}
				} 
				else if(userType.equals("EPT")) 
				{
					if(treeUtilityTRM.xmlDocument == null) 
					{
						treeUtilityTRM.setTreeFile(treeFile);
						treeUtilityTRM.parse(); 
					}
				} 
				else 
				{
					if(treeUtility.xmlDocument == null) 
					{
						treeUtility.setTreeFile(treeFile);
						treeUtility.parse();
					}
				}
			} 
			else if(FoursoftWebConfig.MODULE.equals("ELOG"))
			{
				if(userType.equals("ELC")) 
				{
					if(treeUtilityCRM.xmlDocument == null) 
					{
						treeUtilityCRM.setTreeFile(treeFile);
						treeUtilityCRM.parse();
					}
				} 
				else if(userType.equals("ELV")) 
					{
					if(treeUtilityVRM.xmlDocument == null) 
					{
						treeUtilityVRM.setTreeFile(treeFile);
						treeUtilityVRM.parse();
					}
				} 
				else if(userType.equals("ELT")) 
					{
					if(treeUtilityTRM.xmlDocument == null) 
					{
						treeUtilityTRM.setTreeFile(treeFile);
						treeUtilityTRM.parse();
					}
				} 
				else 
				{
					if(treeUtility.xmlDocument == null) 
					{
						treeUtility.setTreeFile(treeFile);
						treeUtility.parse();
					}
				}
			} 
			else if(FoursoftWebConfig.MODULE.equals("SP")) 
			{
				if(userType.equals("ESC")) 
				{
					if(treeUtilityCRM.xmlDocument == null) 
					{
						treeUtilityCRM.setTreeFile(treeFile);
						treeUtilityCRM.parse();
					}
				} 
				else if(userType.equals("ESV"))
				{
					if(treeUtilityVRM.xmlDocument == null) 
					{
						treeUtilityVRM.setTreeFile(treeFile);
						treeUtilityVRM.parse();
					}
				} 
				else 
				{
					if(treeUtility.xmlDocument == null) 
					{
						treeUtility.setTreeFile(treeFile);
						treeUtility.parse();
					}
				}
			} 
			else 
			{
				if(treeUtility.xmlDocument == null)
				{
					treeUtility.setTreeFile(treeFile);
					treeUtility.parse();
				}	
			}
		
		} 
		catch(FoursoftException fse)
		{
			//Logger.warning(fileName,"IN FOURSOFT EXCEPTION  "+fse.toString());
      logger.warn(fileName+"IN FOURSOFT EXCEPTION  "+fse.toString());
			//String errorMessage = fse.toString();
			request.setAttribute("Login_Message",errorMessage);
		} 
		catch(Exception e)
		{
			//Logger.warning(fileName,"EXCEPTION OCCURED WHILE PARSING "+e.toString());
      logger.warn(fileName+"EXCEPTION OCCURED WHILE PARSING "+e.toString());
		}

		//Logger.info(fileName,"AFTER MENU:"+new java.sql.Timestamp(new java.util.Date().getTime()));
    logger.info(fileName+"AFTER MENU:"+new java.sql.Timestamp(new java.util.Date().getTime()));

		String processedScript	=	"";

		if(FoursoftWebConfig.MODULE.equals("EP")) 
		{
			if(userType.equals("EPC")) 
				processedScript = treeUtilityCRM.process();
			
			else if(userType.equals("EPV")) 
				processedScript = treeUtilityVRM.process();

			else if(userType.equals("EPT")) 
				processedScript = treeUtilityTRM.process();
			
			else 
			{
				treeUtility.shipmentMode = shipmentMode;
				processedScript = treeUtility.process();
			}
		} 
		else if(FoursoftWebConfig.MODULE.equals("ELOG") || FoursoftWebConfig.MODULE.equals("SP")) 
		{
			if(userType.equals("ELC")) 
				processedScript = treeUtilityCRM.process();
			
			else if(userType.equals("ELV")) 
				processedScript = treeUtilityVRM.process();
			
			else if(userType.equals("ELT")) 
				processedScript = treeUtilityTRM.process();
			 
			else if(userType.equals("ESC")) 
				processedScript = treeUtilityCRM.process();
			 
			else if(userType.equals("ESV")) 
				processedScript = treeUtilityVRM.process();
			
			else 
			{
				treeUtility.shipmentMode = shipmentMode;
				processedScript = treeUtility.process();
			}
		} 
		else 
		{
			treeUtility.shipmentMode = shipmentMode;
			processedScript = treeUtility.process();		
		}
		
		processedScript = "<script language='javascript'>"+processedScript+"</script>";

		if(alevel!=null && alevel.size()>0) 
		{
	
%>
<html>
<head>
<script>
	var NL = "\n";
	var v1= '<fmt:message key="100502" bundle="${lang}"/>';
	var	F_KeyFunction =
		NL+	"function handleKeyDown(e) "+
		NL+	"{ "+
		NL+	"	var whichCode = (window.Event) ? e.which : e.keyCode; "+
		NL+	"	var ctrlPressed   = (window.Event) ? e.modifiers & Event.CONTROL_MASK : e.ctrlKey;  "+
		NL+	"	var shiftPressed  = (window.Event) ? e.modifiers & Event.SHIFT_MASK : e.shiftKey;  "+
		NL+	"	var altPressed    = (window.Event) ? e.modifiers & Event.ALT_MASK : e.altKey;  "+			
		NL+	"	var key	= '';  "+
		NL+	"	var cancelBubbling	= false;  "+
		NL+	"	if(ctrlPressed && !shiftPressed && !altPressed) {   "+
		NL+	"		key	= String.fromCharCode(whichCode);  "+
		NL+	"		key.toUpperCase();  "+
		NL+	"		//alert('KEY = '+key+'    CTRL = '+ctrlPressed+'  whichCode = '+whichCode);  "+
		NL+	"		if(key=='W' || key=='E' || key=='N' || key=='O' || key=='L' || key=='P' || key=='A' || key=='H' || key=='R' || key=='S') { "+
		NL+	"			cancelBubbling = true; "+
		NL+	'			alert(\"<fmt:message key="100502" bundle="${lang}"/>\"); '+
		NL+	"			return false; "+
		NL+	"		} "+
		NL+	"		if(key=='T')  { "+
		NL+	'		parent.frames["heading"].OpenClose(); '+
		NL+	"		return false; "+
		NL+	"		} "+
		NL+	"	}  "+
		NL+	"} "+
		NL;
	var v2= '<fmt:message key="100501" bundle="${lang}"/>';
	var treeCommonScript1  =
		NL+	'<script language="JavaScript">'+
		NL+	' var browser = ""; '+
		NL+	' if(navigator.appName=="Netscape") { '+
		NL+	' 	browser = "NN"; '+
		NL+	' } '+
		NL+	' if(navigator.appName=="Microsoft Internet Explorer") { '+
		NL+	' 	browser = "IE"; '+
		NL+	' } '+
		NL+
		NL+	'function handleMouseEvents(e) { '+
		NL+	'	if(browser == "IE") { '+
		NL+	'		if(document.all) { '+
		NL+	'			if(event.button==2 || event.button==3) { '+
		NL+	'				alert(\"<fmt:message key="100501" bundle="${lang}"/>\"); '+
		NL+	'				return false; '+
		NL+	'			} '+
		NL+	'			if(event.button==1) { '+
		NL+	'				var shiftPressed = (window.Event) ? e.modifiers & Event.SHIFT_MASK : window.event.shiftKey;'+
		NL+	'				if(shiftPressed) { '+
		NL+	'					alert(\"<fmt:message key="100502" bundle="${lang}"/>\"); '+
		NL+	'					return false; '+
		NL+	'				} '+
		NL+	'			} '+
		NL+	'		} '+
		NL+	'	} '+
		NL+	'	if(browser == "NN") { '+
		NL+	'		e.cancelBubble = true; '+
		NL+	'		if(e.which == 2 || e.which == 3) {  '+
		NL+	'			alert(\"<fmt:message key="100501" bundle="${lang}"/>\"); '+
		NL+	'			return false; '+
		NL+	'		} '+
		NL+	'	} '+
		NL+	'} '+
		NL+
		NL+	'if(browser == "IE") { '+
		NL+	'	document.onmousedown = handleMouseEvents; '+
		NL+	'} '+
		NL+	'if(browser == "NN") { '+
		NL+	'	window.captureEvents(Event.MOUSEDOWN | Event.KEYPRESS); '+
		NL+	'	window.onmousedown = handleMouseEvents; '+
		NL+	'} '+
		NL+
		NL+	'function handlePress(e) { '+
		NL+	'  var codeFrame = parent.frames["code"];	'+
		NL+	'  try { '+
		NL+	'	var whichCode    = (window.Event) ? e.which : e.keyCode;'+			
		NL+
//		NL+	'	alert(" INITIAL whichCode = "+ whichCode+".... "+window.Event); '+			
		NL+
		NL+ '	var altPressed   = (window.Event) ? e.modifiers & Event.ALT_MASK : window.event.altKey;'+
		NL+	'	var shiftPressed = (window.Event) ? e.modifiers & Event.SHIFT_MASK : window.event.shiftKey;'+
		NL+ '	var ctrlPressed  = (window.Event) ? e.modifiers & Event.CONTROL_MASK : window.event.ctrlKey;'+
/*	
		NL+	'	alert("SHIFT_MASK = "+ Event.SHIFT_MASK ); '+
		NL+	'	alert("CONTROL_MASK  = "+ Event.CONTROL_MASK ); '+
		NL+	'	alert("ALT_MASK   = "+ Event.ALT_MASK ); '+
		NL+	'	alert("\\n ALT   = "+ e.altKey + "\\n CTRL  = "+ e.ctrlKey + "\\n SHIFT = "+ e.shiftKey); '+
		NL+	'	alert(" e.modifiers ="+e.modifiers +"  AND Event.SHIFT_MASK = "+ Event.SHIFT_MASK); '+
		NL+	'	alert(" shiftPressed = "+shiftPressed ); '+
*/			
		NL+	'	var key = "" '+

//		NL+	'	//alert(" AFTER manipulating whichCode = "+ whichCode + "   SHIFT = "+shiftPressed); '+

		NL+	'	key = String.fromCharCode(whichCode); '+

		NL+	'	if(!ctrlPressed && !shiftPressed && !altPressed) {'+
		NL+	'		if(whichCode==116) {'+
//		NL+	'			parent.frames["heading"].key = "F5"; '+
		NL+	'			parent.key = "F5"; '+
		NL+	'		}'+
		NL+	'		if(whichCode==118) {'+
		NL+	'			parent.frames["menu"].focus();'+
		NL+	'		}'+
		NL+	'		if(whichCode==119) {'+
		NL+	'			parent.frames["text"].focus();'+
		NL+	'		}'+
		NL+	'		if(whichCode==120) {'+
		NL+	'			parent.frames["heading"].focus();'+
		NL+	'		}'+
		NL+	'		if(whichCode==123) {'+
		NL+	'			parent.frames["heading"].document.images("logoutImage").click(); '+
		NL+	'		}'+
		NL+	'	}'+
			
//		NL+	'	alert("KEY = "+ key + "\\n ALT   = "+ e.altKey + "\\n CTRL  = "+ e.ctrlKey + "\\n SHIFT = "+ e.shiftKey); '+			

		NL+	'	var hotkey; '+
		NL+	'	var ctrlkey; '+
		NL+	'	var shiftkey; '+
		NL+	'	var altkey; '+
		NL+	'	var url; '+
		NL+	'	var target; '+
		NL+	'	var name; '+
			
		NL+	'	for( i=0; i < codeFrame.hotKeys.length; i++) { '+
		NL+	'		hotkey    = codeFrame.hotKeys[i]; '+
		NL+	'		ctrlkey   = codeFrame.ctrlKeys[i]; '+
		NL+	'		shiftkey  = codeFrame.shiftKeys[i]; '+
		NL+	'		altkey    = codeFrame.altKeys[i]; '+
		NL+	'		url       = codeFrame.urls[i]; '+
		NL+	'		target    = codeFrame.targets[i]; '+
		NL+	'		name      = codeFrame.names[i]; '+
		NL+
		NL+	'		if(key==hotkey || key.toUpperCase()==hotkey) { '+
		NL+	'			var hotkeyWithCtrl	= ctrlkey  =="Y" ? true : false; '+			
		NL+	'			var hotkeyWithShift	= shiftkey =="Y" ? true : false; '+
		NL+	'			var hotkeyWithAlt	= altkey   =="Y" ? true : false; '+
		NL+
//		NL+	'			alert("hotKeys["+i+"] = "+hotkey+"\\nhotkeyWithShift = "+ shiftkey + "\\nhotkeyWithCtrl   = "+ ctrlkey + "\\nhotkeyWithAlt  = "+ altkey); '+
		NL+	'			if(e.ctrlKey==hotkeyWithCtrl && e.shiftKey==hotkeyWithShift && e.altKey==hotkeyWithAlt) { '+
//		NL+	'				alert("hotKeys In url selection : urls["+i+"] = "+url); '+
		NL;

	// THE VARIABLE "treeCommonScript2" acts as a separator from the first and the actual script
	var v3= '<fmt:message key="100503" bundle="${lang}"/>';
	var treeCommonScript2 =
		NL+
		NL+	'			}'+
			
		NL+	'		}'+
		NL+	'	}'+
			
		NL+	'  } catch(e) { '+
//		NL+	'		alert("Invalid Key Combination");'+
		NL+	'  }'+
			
		NL+	'}'+
			
		NL+NL+
			
		NL+	'function stopError(){'+
		NL+	'	alert(\"<fmt:message key="100503" bundle="${lang}"/>\");'+
		NL+	'	return false;'+
		NL+	'}'+
			
		NL+NL+

		NL+	'var menulinks = parent.frames["menu"].document.links; 	'+
			
		NL+	'function handleLinkFocus(linkname) { '+
		NL+	'	for(var n=0; n < document.links.length; n++) { '+
		NL+	'		if(document.links.item(n).name == linkname) { '+
		NL+	'	 		document.links.item(n).style.fontStyle  = "italic"; '+
		NL+	'	 		document.links.item(n).style.fontWeight = "bold"; '+
		NL+	'		} else { '+
		NL+	'			document.links.item(n).style.fontStyle  = "normal"; '+
		NL+	'			document.links.item(n).style.fontWeight = "normal"; '+
		NL+	'		} '+
		NL+	'	} '+
		NL+	'	return true; '+
		NL+	'}'+
			
		NL+NL+
			
		NL+	'function handleLinkBlur(linkname){'+
		NL+	'	for(var n=0; n < document.links.length; n++) {'+
		NL+	'		if(document.links.href == "javascript:;") {'+
		NL+	'			if(document.links.item(n).name != lastClickedLink) {'+
		NL+	'				document.links.item(n).style.fontStyle  = "normal"; '+
		NL+	'				document.links.item(n).style.fontWeight = "normal"; '+
		NL+	'			} '+
		NL+	'		} '+
		NL+	'	} '+
		NL+	'}'+

		NL+NL+

		NL+	'function getToLastClickedLink() { '+
		NL+	'	var lastClickedLink = parent.frames[\'code\'].lastClickedLink;	'+
//		NL+	'	alert("ON LOAD...LAST CLICKED LINK = \'"+lastClickedLink+"\'"); '+
		NL+	'	if(lastClickedLink!=null && lastClickedLink!="" && lastClickedLink.length > 0) { '+
//		NL+	'		alert("PLACING FOCUS ON LAST CLICKED LINK "+lastClickedLink); '+
		NL+	'		if(menulinks.item( lastClickedLink )) { '+
		NL+	'			menulinks.item( lastClickedLink ).focus(); '+
		NL+	'		} '+
		NL+	'	} else { '+
		NL+	'		var firstLink = menulinks.item(0); '+
		NL+	'		if(firstLink) { '+
		NL+	'			firstLink.focus(); '+			
		NL+	'		} '+
		NL+	'	} '+
		NL+	'}'+
			
		NL+NL+

		NL+	'function showLastClickedLink() { '+
		NL+	'	var lastClickedLink = parent.frames[\'code\'].lastClickedLink;	'+
//		NL+	'		alert("ONLOAD.....lastClickedLink = \'"+lastClickedLink+"\'"); 	'+
		NL+	'	for(var n=0; n < menulinks.length; n++) { '+
		NL+	'		if(parent.frames["menu"].document.links.item(n).name != lastClickedLink) { '+
		NL+	'			menulinks.item(n).style.fontStyle  = "normal"; '+
		NL+	'			menulinks.item(n).style.fontWeight = "normal"; '+
		NL+	'		} '+
		NL+	'	} '+
		NL+	'}'+

		NL+NL+
			
		NL+NL+	' window.onerror=stopError; '+
			
		// The Frame swithing function is added here
			
		NL+NL+ F_KeyFunction +NL+NL+
			
		'<\/script>';


	/******************************************************************************
	* Define the MenuItem object.                                                 *
	******************************************************************************/

	var lastClickedLink = "";

	var hotKeys		= new Array();
	var ctrlKeys	= new Array();
	var shiftKeys	= new Array();
	var altKeys 	= new Array();

	var names = new Array();
	var urls = new Array();
	var targets = new Array();

	var myItems = new Array();
	var myString = "";
	var addedItems = new Array();


	function setDefault(str)
	{
		if(str==null)
			return "";
		else
			return str;
	}

	function MTMenuItem( text, url, target, itemtype, itemaction, tooltip, icon, closedIcon, hotKey, ctrlKey, shiftKey, altKey) 
	{
		text=setDefault(text);
		url=setDefault(url);
		target=setDefault(target);
		itemtype=setDefault(itemtype);
		itemaction=setDefault(itemaction);
		tooltip=setDefault(tooltip);
		icon=setDefault(icon);

		this.text = text;
		this.url = url ? url : "";
		this.target =  target ? target : "";
		this.itemtype= itemtype;
		this.itemaction= itemaction;
		this.tooltip = tooltip;
		this.icon = icon ? icon : "";
		
		this.number = MTMNumber++;
		this.name = "sub"+this.number;
		this.itemnumber = this.number;

		this.hotKey		= setDefault( hotKey );
		this.ctrlKey	= setDefault( ctrlKey );
		this.shiftKey	= setDefault( shiftKey );
		this.altKey		= setDefault( altKey );
		
		if(ctrlKey==null || ctrlKey=="") {
			ctrlKey = "N";
		}
		if(shiftKey==null || shiftKey=="") {
			shiftKey = "N";
		}
		if(altKey==null || altKey=="") {
			altKey = "N";
		}
		
		if(hotKey != null && hotKey != "" && hotKey.length > 0)
		{
			if(ctrlKey.length > 0 || shiftKey.length > 0 || altKey.length > 0)
			{
				hotKeys[hotKeys.length]		= hotKey.toUpperCase();
				ctrlKeys[ctrlKeys.length]	= ctrlKey.toUpperCase();
				shiftKeys[shiftKeys.length]	= shiftKey.toUpperCase();
				altKeys[altKeys.length]		= altKey.toUpperCase();

				this.tooltip += " (";
				
				if(ctrlKey   == "Y") {
					this.tooltip += "Ctrl+";
				}
				if(shiftKey == "Y") {
					this.tooltip += "Shift+";
				}
				if(altKey   == "Y") {
					this.tooltip += "Alt+";
				}
				this.tooltip += hotKey;
				this.tooltip += ")";
			}
			
			urls[urls.length]		= url;
			targets[targets.length] = target;
			myItems[myItems.length] = text;
			names[names.length]		= "sub"+this.number;
		}

		this.submenu     = null;
		this.expanded    = false;
		this.MTMakeSubmenu = MTMakeSubmenu;
	}

	function MTMakeSubmenu(eSupply, isExpanded, collapseIcon, expandIcon) {
	  this.submenu = eSupply;
	  this.expanded = isExpanded;

	  this.collapseIcon = collapseIcon ? collapseIcon : "menu_folder_closed.gif";
	  this.expandIcon = expandIcon ? expandIcon : "menu_folder_open.gif";
	}

	/******************************************************************************
	* Define the Menu object.                                                     *
	******************************************************************************/

	function MTMenu() {
	  this.items   = new Array();
	  this.MTMAddItem = MTMAddItem;
	}

	function MTMAddItem(item) {
	  this.items[this.items.length] = item;
	}

	/******************************************************************************
	* Define the icon list, addIcon function and MTMIcon item.                    *
	******************************************************************************/

	function IconList() {
	  this.items = new Array();
	  this.addIcon = addIcon;
	}

	function addIcon(item) {
	  this.items[this.items.length] = item;
	}

	function MTMIcon(iconfile, match, type) {
	  this.file = iconfile;
	  this.match = match;
	  this.type = type;
	}

	function MTMBrowser() {
	  this.preHREF = "";
	  this.MTMable = false;
	  this.cssEnabled = true;
	  this.browserType = "other";

	  if(navigator.appName == "Netscape" && navigator.userAgent.indexOf("WebTV") == -1) {
	    if(parseInt(navigator.appVersion) == 3 && (navigator.userAgent.indexOf("Opera") == -1)) {
	      this.MTMable = true;
	      this.browserType = "NN3";
	      this.cssEnabled = false;

	    } else if(parseInt(navigator.appVersion) >= 4) {
	      this.MTMable = true;
	      this.browserType = parseInt(navigator.appVersion) == 4 ? "NN4" : "NN5";
	    }
	  } else if(navigator.appName == "Microsoft Internet Explorer" && parseInt(navigator.appVersion) >= 4) {
	    this.MTMable = true;
	    this.browserType = "IE4";
	  } else if(navigator.appName == "Opera" && parseInt(navigator.appVersion) >= 5) {
	    this.MTMable = true;
	    this.browserType = "O5";
	  }

	  if(this.browserType != "NN4") {
	    this.preHREF = location.href.substring(0, location.href.lastIndexOf("/") +1)
	  }
	}


	/******************************************************************************
	* Global variables.  Not to be altered unless you know what you're doing.     *
	* User-configurable options are at the end of this document.                  *
	******************************************************************************/
	var MTMLoaded = false;
	var MTMLevel;
	var MTMBar = new Array();
	var MTMIndices = new Array();

	var MTMUA = new MTMBrowser();

	var MTMClickedItem = false;
	var MTMExpansion = false;

	var MTMNumber = 1;
	var itemnumber=0;
	var MTMTrackedItem = false;
	var MTMTrack = false;
	var MTMFrameNames;

	var MTMFirstRun = true;
	var MTMCurrentTime = 0; // for checking timeout.
	var MTMUpdating = false;
	var MTMWinSize, MTMyval, MTMxval;
	var MTMOutputString = "";
	/******************************************************************************
	* Code that picks up frame names of frames in the parent frameset.            *
	******************************************************************************/

	function MTMgetFrames() {
	  if(MTMUA.MTMable) {
	    MTMFrameNames = new Array();
	    for(i = 0; i < parent.frames.length; i++) {
	      MTMFrameNames[i] = parent.frames[i].name;
	    }
	  }
	}
	/******************************************************************************
	* Functions to draw the menu.                                                 *
	******************************************************************************/

	function MTMSubAction(SubItem) {

	  SubItem.expanded = (SubItem.expanded) ? false : true;
	  if(SubItem.expanded) {
	    MTMExpansion = true;
	  }

	  MTMClickedItem = SubItem.number;

	  if(MTMTrackedItem && MTMTrackedItem != SubItem.number) {
	    MTMTrackedItem = false;
	  }
	    
	    setTimeout("MTMDisplayMenu()", 10);
		
		if(SubItem.itemtype && SubItem.itemtype!="")
		{
			//alert(	"SubItem.url = "+ SubItem.url +"\n"+
			//		"SubItem.itemaction = "+SubItem.itemaction +"\n"+
			//		"SubItem.itemtype = "+SubItem.itemtype);

			parent.frames['text'].location.href = SubItem.itemaction;
			
		}
		//alert("In MTMSubAction returning false : SubItem.expanded = "+SubItem.expanded);
		return false;

	}


	function MTMStartMenu() {

		//initializeMenus();
		
	  MTMLoaded = true;
	  if(MTMFirstRun) {
	    MTMCurrentTime++;
	    if(MTMCurrentTime == MTMTimeOut) {
	      MTMDisplayMenu();
	    } else {
	      MTMStartMenu();
	    }
	  } 
	}

	function MTMDisplayMenu() {
		
		myString = "";
		
	  if(MTMUA.MTMable && !MTMUpdating) {
	    MTMUpdating = true;

	    if(MTMFirstRun) {
	      MTMgetFrames();
	    }

	    if(MTMTrack) { MTMTrackedItem = MTMTrackExpand(eSupply); }

	    if(MTMExpansion && MTMSubsAutoClose) { MTMCloseSubs(eSupply); }

	    MTMLevel = 0;
	    MTMDoc = parent.frames[MTMenuFrame].document
	    MTMDoc.open("text/html", "replace");
	    MTMOutputString = '<html><head> \n';
	    if(MTMLinkedSS) {
	      MTMOutputString += '<link rel="stylesheet" type="text/css" href="' + MTMUA.preHREF + MTMSSHREF + '">\n';
	    } else if(MTMUA.cssEnabled) {

			MTMOutputString += '<style type="text/css">\nbody {\n\tcolor:' + MTMTextColor + ';\n}\n';
			MTMOutputString += '#root {\n\tcolor:' + MTMRootColor + ';\n\tbackground:transparent;\n\tfont-family:' + MTMRootFont + ';\n\tfont-size:' + MTMRootCSSize + ';\n}\n';
			MTMOutputString += 'a {\n\tfont-family:' + MTMenuFont + ';\n\tfont-size:' + MTMenuCSSize + ';\n\ttext-decoration:none;\n\tcolor:' + MTMLinkColor + ';\n\tbackground:transparent;\n}\n';
			MTMOutputString += MTMakeA('pseudo', 'hover', MTMAhoverColor);
			MTMOutputString += MTMakeA('class', 'tracked', MTMTrackColor);
			MTMOutputString += MTMakeA('class', 'subexpanded', MTMSubExpandColor);
			MTMOutputString += MTMakeA('class', 'subclosed', MTMSubClosedColor) + MTMExtraCSS + '\n<\/style>\n';

			MTMOutputString += treeCommonScript1;
			
			MTMLevel =0;
			getListItem(eSupply);
			MTMLevel =0;
			MTMOutputString += '\t'+ myString;

			MTMOutputString += treeCommonScript2;

	   }
		MTMOutputString += '<\/head>\n<body ';
	    if(MTMBackground != "") {
	      MTMOutputString += 'background="' + MTMUA.preHREF + MTMenuImageDirectory + MTMBackground + '" ';
	    }
	    
	    MTMOutputString += 'bgcolor=white text="' + MTMTextColor + '" link="' + MTMLinkColor + '" vlink="' + MTMLinkColor + '" alink="' + MTMLinkColor + '" onLoad=" this.focus(); getToLastClickedLink(); " onKeyDown="handleKeyDown(event); handlePress(event);">\n';		
	  
		MTMOutputString += MTMHeader + '\n<table border="0" cellpadding="0" cellspacing="0" width="' + MTMTableWidth + '">\n';
	    MTMOutputString += '<tr valign="top"><td nowrap><img src="' + MTMUA.preHREF + MTMenuImageDirectory + MTMRootIcon + '" align="left" border="0" vspace="0" hspace="0">';
	    if(MTMUA.cssEnabled) {
	      MTMOutputString += '<span id="root"><font size=2>&nbsp;' + MTMenuText + '<\/span>';
		  
	    } else {
	      MTMOutputString += '<font size="' + MTMRootFontSize + '" face="' + MTMRootFont + '" color="' + MTMRootColor + '">' + MTMenuText + '<\/font>';
	    }
	    MTMDoc.writeln(MTMOutputString + '</td></tr>');

	    MTMListItems(eSupply);

	    MTMDoc.writeln('<\/table>\n' + MTMFooter + '\n<\/body>\n<\/html>');
	    MTMDoc.close();

	    if(MTMUA.browserType == "NN5") {
	      parent.frames[MTMenuFrame].scrollTo(0, 0);
	    }

	    if((MTMClickedItem || MTMTrackedItem) && MTMUA.browserType != "NN3" && !MTMFirstRun) {
	      MTMItemName = "sub" + (MTMClickedItem ? MTMClickedItem : MTMTrackedItem);
	      if(document.layers && parent.frames[MTMenuFrame].scrollbars) {
	        MTMyval = parent.frames[MTMenuFrame].document.anchors.item[MTMItemName].y;
	        MTMWinSize = parent.frames[MTMenuFrame].innerHeight;
	      } else if(MTMUA.browserType != "O5") {
	        if(MTMUA.browserType == "NN5") {
	          parent.frames[MTMenuFrame].document.all = parent.frames[MTMenuFrame].document.getElementsByTagName("*");
	        }
	        MTMyval = MTMGetYPos(parent.frames[MTMenuFrame].document.all[MTMItemName]);
	        MTMWinSize = MTMUA.browserType == "NN5" ? parent.frames[MTMenuFrame].innerHeight : parent.frames[MTMenuFrame].document.body.offsetHeight;
	      }
	      if(MTMyval > (MTMWinSize - 60)) {
	        parent.frames[MTMenuFrame].scrollBy(0, parseInt(MTMyval - (MTMWinSize * 1/3)));
	      }
	    }
	    MTMFirstRun = false;
	    MTMClickedItem = false;
	    MTMExpansion = false;
	    MTMTrack = false;
	  }
	MTMUpdating = false;
	}

	function getListItem(menu) 
	{
		var MTMClickCmd = "";
		var MTMfrm = "parent.frames['code']";
		var i, isLast;


		var codeFrame = parent.frames["code"];

		for (i = 0; i < menu.items.length; i++) 
		{
			var MTMOutputString1 = "";
			
			MTMIndices[MTMLevel] = i;
			var MTMref = '.eSupply.items[' + MTMIndices[0] + ']';
			isLast = (i == menu.items.length -1);
			   
			if(menu.items[i].submenu)
			{
				
				if(MTMLevel > 0)  {
					for(k = 1; k <= MTMLevel; k++)  {
						MTMref += ".submenu.items[" + MTMIndices[k] + "]";
					}
				}				
					
				if(menu.items[i].hotKey!=null && menu.items[i].hotKey != "" && menu.items[i].hotKey.length > 0)
				{
					//alert("NOT EXPANDED menu.items["+i+"].text "+menu.items[i].text);
						
					MTMClickCmd = "return " + MTMfrm + ".MTMSubAction(" + MTMfrm + MTMref + ");";

					MTMOutputString1 +=	NL+'				if(url == "'+menu.items[i].url+'" && hotkey=="'+menu.items[i].hotKey.toUpperCase()+'" && ctrlkey== "'+menu.items[i].ctrlKey.toUpperCase()+'" && shiftkey== "'+menu.items[i].shiftKey.toUpperCase()+'" && altkey=="'+menu.items[i].altKey.toUpperCase()+'") { '+
										NL+'					if(parent.frames["menu"].document.links.item(name)) { '+
										NL+'						codeFrame.lastClickedLink = name;'+
										NL+'						'+MTMClickCmd+
										NL+'					}'+
										NL+'				}';
						
						//alert("added flag = "+flag);
					myString += MTMOutputString1;
					addedItems[addedItems.length] = menu.items[i].text;
				}
					
				MTMLevel++;
				getListItem(menu.items[i].submenu);
				MTMLevel--;
				
			} else {
				//alert("menu.items[i].submenu is FALSE for "+menu.items[i].text);
				if(menu.items[i].hotKey!="" && menu.items[i].hotKey!=null && menu.items[i].hotKey!=0)
				{
					//alert("ADDED "+menu.items[i].text);
					
					MTMOutputString1 += NL+'				if(url == "'+menu.items[i].url+'" && hotkey=="'+menu.items[i].hotKey.toUpperCase()+'" && ctrlkey== "'+menu.items[i].ctrlKey.toUpperCase()+'" && shiftkey== "'+menu.items[i].shiftKey.toUpperCase()+'" && altkey=="'+menu.items[i].altKey.toUpperCase()+'") { '+
										NL+'					if(parent.frames["menu"].document.links.item(name)) { '+
										NL+'						codeFrame.lastClickedLink = name;'+
										NL+'						handleLinkFocus( name ); '+
										NL+'						parent.frames[ target ].location.href = url; '+
										NL+'						break; '+
										NL+'					}'+
										NL+'				}';

					myString += MTMOutputString1;
					addedItems[addedItems.length] = menu.items[i].text;

				}
				
			}
		}
	}

	function MTMListItems(eSupply) {
	  var i, isLast;
	  for (i = 0; i < eSupply.items.length; i++) {
	    MTMIndices[MTMLevel] = i;
	    isLast = (i == eSupply.items.length -1);
	    MTMDisplayItem(eSupply.items[i], isLast);

	    if(eSupply.items[i].submenu && eSupply.items[i].expanded) {
	      MTMBar[MTMLevel] = (isLast) ? false : true;
	      MTMLevel++;
	      MTMListItems(eSupply.items[i].submenu);
	      MTMLevel--;
	    } else {
	      MTMBar[MTMLevel] = false;
	    } 
	  }
	}

	function MTMDisplayItem(item, last) 
	{
		var i, img;

		var MTMfrm = "parent.frames['code']";
		var MTMref = '.eSupply.items[' + MTMIndices[0] + ']';

		if(MTMLevel > 0) 
		{
			for(i = 1; i <= MTMLevel; i++) 
			{
				MTMref += ".submenu.items[" + MTMIndices[i] + "]";
			}
		}
		if(item.submenu) 
		{
			var usePlusMinus = false;
			if(MTMSubsGetPlus.toLowerCase() == "always" || MTMEmulateWE) 
			{
				usePlusMinus = true;
			}
			else if(MTMSubsGetPlus.toLowerCase() == "submenu") 
			{
				for (i = 0; i < item.submenu.items.length; i++) 
				{
					if (item.submenu.items[i].submenu) 
					{
						usePlusMinus = true; 
						break;
					}
				}
			}


			if(item.submenu) 
			{
				var MTMClickCmd = "return " + MTMfrm + ".MTMSubAction(" + MTMfrm + MTMref + ");";
			}

			var MTMouseOverCmd = "parent.status='" + (item.expanded ? "4S " : "4S ") + (item.text.indexOf("'") != -1 ? MTMEscapeQuotes(item.text) : item.text) + "';return true;";
			var MTMouseOutCmd = "parent.status=parent.defaultStatus;return true;";
		}

		MTMOutputString = '<tr valign="top"><td nowrap >';

		if(MTMLevel > 0) 
		{
			for (i = 0; i < MTMLevel; i++) 
			{
				MTMOutputString += (MTMBar[i]) ? MTMakeImage("menu_bar.gif") : MTMakeImage("menu_pixel.gif");
			}
		}

		if(item.submenu && usePlusMinus) 
		{
			//alert("ITEM '"+item.text +"' has Sub Menu and uses plus-minus\n"+
			//	  "MTMClickCmd = "+ MTMClickCmd );
			if(item.url == "") 
			{
				MTMOutputString += MTMakeLink(item, true, true, true, MTMClickCmd, MTMouseOverCmd, MTMouseOutCmd);
			} 
			else 
			{
				if(MTMEmulateWE) 
				{
					MTMOutputString += MTMakeLink(item, true, true, false, MTMClickCmd, MTMouseOverCmd, MTMouseOutCmd);
				} 
				else 
				{
					//alert("MTMEmulateWE = "+MTMEmulateWE);
					
					MTMOutputString += MTMakeLink(item, false, false, true, MTMClickCmd, MTMouseOverCmd, MTMouseOutCmd);
				}
			}
			if(item.expanded) 
			{
				img = (last) ? "menu_corner_minus.gif" : "menu_tee_minus.gif";
			} 
			else 
			{
				img = (last) ? "menu_corner_plus.gif" : "menu_tee_plus.gif";
			}
		} 
		else 
		{
			img = (last) ? "menu_corner.gif" : "menu_tee.gif";
		}

		MTMOutputString += MTMakeImage(img);

		if(item.submenu) 
		{
			if(MTMEmulateWE && item.url != "") 
			{
				  
				MTMOutputString += '</a>' + MTMakeLink(item, false, false, true, MTMClickCmd);
			} 
			else if(!usePlusMinus) 
			{
				if(item.url == "") 
				{
					MTMOutputString += MTMakeLink(item, true, true, true, MTMClickCmd, MTMouseOverCmd, MTMouseOutCmd);
				}
				else if(!item.expanded) 
				{
					MTMOutputString += MTMakeLink(item, false, true, true, MTMClickCmd);
				} 
				else 
				{
					MTMOutputString += MTMakeLink(item, true, true, false, MTMClickCmd, MTMouseOverCmd, MTMouseOutCmd);
				}
			}
			img = (item.icon != "") ? item.icon : MTMFetchIcon(item.itemtype);
		}
		else 
		{
			MTMOutputString += MTMakeLink(item, false, true, true, MTMClickCmd, false, false);
			img = (item.icon != "") ? item.icon : MTMFetchIcon(item.itemtype);
		}
		
		MTMOutputString += MTMakeImage(img);
		
		//if(item.submenu && item.url != "" && item.expanded && !MTMEmulateWE)
		if(item.submenu && item.url != "" && !MTMEmulateWE) 
		{
			//alert("ITEM '"+item.text +"' has Sub Menu \n"+
			//	  "MTMClickCmd = "+ MTMClickCmd );
			// MTMOutputString += '</a>' + MTMakeLink(item, false, false, true);
			MTMOutputString += '</a>' + MTMakeLink(item, false, true, true, MTMClickCmd, MTMouseOverCmd, MTMouseOutCmd);
		}

		if(MTMUA.browserType == "NN3" && !MTMLinkedSS) 
		{
			var stringColor;
			if(item.submenu && (item.url == "") && (item.number == MTMClickedItem)) 
			{
				stringColor = (item.expanded) ? MTMSubExpandColor : MTMSubClosedColor;
			}
			else if(MTMTrackedItem && MTMTrackedItem == item.number) 
			{
				stringColor = MTMTrackColor;
			}
			else 
			{
				stringColor = MTMLinkColor;
			}
			MTMOutputString += '<font color="' + stringColor + '" size="' + MTMenuFontSize + '" face="' + MTMenuFont + '">';
		}

		var tmpStr =item.text;
		try
		{
			if(item.hotKey!=null && item.hotKey!="")
			{
				var str = item.text;
				var hotkey = item.hotKey;
				var first;
				var second;
				var foundFlag = false;
				for (c=0; c<str.length; c++)
				{
					if(str.charAt(c).toUpperCase()==hotkey.toUpperCase())
					{
						first = str.substring(0,c)
						first+="<u>"+str.charAt(c)+"</u>";
						second = str.substring(c+1,str.length)
						foundFlag = true;
						break;
					}
				}
				if(!foundFlag)
					tmpStr = item.text
				else
					tmpStr = first+second;
			}
			else
				tmpStr = item.text;
		}
		catch(e)
		{
			tmpStr = item.text;
		}

		MTMOutputString += '&nbsp;' + tmpStr + ((MTMUA.browserType == "NN3" && !MTMLinkedSS) ? '</font>' : '') + '</a>' ;
		MTMDoc.writeln(MTMOutputString + '</td></tr>');
	}

	function MTMEscapeQuotes(mString) {
	  var newString = "";
	  var cur_pos = mString.indexOf("'");
	  var prev_pos = 0;
	  while (cur_pos != -1) {
	    if(cur_pos == 0) {
	      newString += "\\";
	    } else if(mString.charAt(cur_pos-1) != "\\") {
	      newString += mString.substring(prev_pos, cur_pos) + "\\";
	    } else if(mString.charAt(cur_pos-1) == "\\") {
	      newString += mString.substring(prev_pos, cur_pos);
	    }
	    prev_pos = cur_pos++;
	    cur_pos = mString.indexOf("'", cur_pos);
	  }
	  return(newString + mString.substring(prev_pos, mString.length));
	}

	function MTMTrackExpand(thisMenu) {
	  var i, targetPath, targetLocation;
	  var foundNumber = false;
	  for(i = 0; i < thisMenu.items.length; i++) {
	    if(thisMenu.items[i].url != "" && MTMTrackTarget(thisMenu.items[i].target)) {
	      targetLocation = parent.frames[thisMenu.items[i].target].location;
	      targetPath = targetLocation.pathname + targetLocation.search;
	      if(MTMUA.browserType == "IE4" && targetLocation.protocol == "file:") {
	        var regExp = /\\/g;
	        targetPath = targetPath.replace(regExp, "\/");
	      }
	      if(targetPath.lastIndexOf(thisMenu.items[i].url) != -1 && (targetPath.lastIndexOf(thisMenu.items[i].url) + thisMenu.items[i].url.length) == targetPath.length) {
	        return(thisMenu.items[i].number);
	      }
	    }
	    if(thisMenu.items[i].submenu) {
	      foundNumber = MTMTrackExpand(thisMenu.items[i].submenu);
	      if(foundNumber) {
	        if(!thisMenu.items[i].expanded) {
	          thisMenu.items[i].expanded = true;
	          if(!MTMClickedItem) { MTMClickedItem = thisMenu.items[i].number; }
	          MTMExpansion = true;
	        }
	        return(foundNumber);
	      }
	    }
	  }
	return(foundNumber);
	}

	function MTMCloseSubs(thisMenu) {
	  var i, j;
	  var foundMatch = false;
	  for(i = 0; i < thisMenu.items.length; i++) {
	    if(thisMenu.items[i].submenu && thisMenu.items[i].expanded) {
	      if(thisMenu.items[i].number == MTMClickedItem) {
	        foundMatch = true;
	        for(j = 0; j < thisMenu.items[i].submenu.items.length; j++) {
	          if(thisMenu.items[i].submenu.items[j].expanded) {
	            thisMenu.items[i].submenu.items[j].expanded = false;
	          }
	        }
	      } else {
	        if(foundMatch) {
	          thisMenu.items[i].expanded = false; 
	        } else {
	          foundMatch = MTMCloseSubs(thisMenu.items[i].submenu);
	          if(!foundMatch) {
	            thisMenu.items[i].expanded = false;
	          }
	        }
	      }
	    }
	  }
	return(foundMatch);
	}

	function MTMFetchIcon(testString) 
	{
	  return("Tree_setup_open.gif");
	}

	function MTMGetYPos(myObj) {
		try {
	  		return( myObj.offsetTop + ((myObj.offsetParent) ? MTMGetYPos(myObj.offsetParent) : 0));
		}catch(e){
			parent.status = "The linked object is not available";
		}
	}

	function MTMCheckURL(myURL) {
		var tempString = "";
		if((myURL.indexOf("http://") == 0) || (myURL.indexOf("https://") == 0) || (myURL.indexOf("mailto:") == 0) || (myURL.indexOf("ftp://") == 0) || (myURL.indexOf("telnet:") == 0) || (myURL.indexOf("news:") == 0) || (myURL.indexOf("gopher:") == 0) || (myURL.indexOf("nntp:") == 0) || (myURL.indexOf("javascript:") == 0)) {
			tempString += myURL;
		} else {
			tempString += MTMUA.preHREF + myURL;
		}
		return(tempString);
	}
	

	function MTMakeLink(thisItem, voidURL, addName, addTitle, clickEvent, mouseOverEvent, mouseOutEvent) 
	{
						
		var tempString =	'<a '+
							' onFocus=" handleLinkFocus( this.name ); " '+
	  						' onBlur =" handleLinkBlur( this.name );" '+
							' href="' + (voidURL ? 'javascript:;' : MTMCheckURL(thisItem.url)) + '" ';

		if(MTMUseToolTips && addTitle && thisItem.tooltip) {
			tempString += 'title="' + thisItem.tooltip + '" ';
		}
		if(addName) {
			tempString += 'name="sub' + thisItem.number + '" ';
		}
		if(clickEvent) {
			tempString += 'onclick=" parent.frames[\'code\'].lastClickedLink =\'sub'+thisItem.number+'\'; ' + clickEvent + '" ';
		} else {
			tempString += 'onclick=" parent.frames[\'code\'].lastClickedLink =\'sub'+thisItem.number+'\';" ';
		}
	  
		if(mouseOverEvent && mouseOverEvent != "") {
			tempString += 'onmouseover="' + mouseOverEvent + '" ';
		}
		if(mouseOutEvent && mouseOutEvent != "") {
			tempString += 'onmouseout="' + mouseOutEvent + '" ';
		}
		if(thisItem.submenu && MTMClickedItem && thisItem.number == MTMClickedItem) {
			tempString += 'class="' + (thisItem.expanded ? "subexpanded" : "subclosed") + '" ';
		} else if(MTMTrackedItem && thisItem.number == MTMTrackedItem) {
			tempString += 'class="tracked"';
		}
		if(thisItem.target != "") {
			tempString += 'target="' + thisItem.target + '" ';
		}
	  
		//alert(	"MTMMakeLink....."+thisItem.text+"\n"+
		//		"voidURL = "+voidURL + "\n"+
		//		"thisItem.url = "+thisItem.url+"\n\n"+
		//		tempString + ">"
		//		);
	  
	  return(tempString + '>');
	}

	function MTMakeImage(thisImage) {
	  return('<img src="' + MTMUA.preHREF + MTMenuImageDirectory + thisImage + '" align="left" border="0" vspace="0" hspace="0" width="18" height="18">');
	}

	function MTMakeBackImage(thisImage) {
	  var tempString = 'transparent url("' + ((MTMUA.preHREF == "") ? "" : MTMUA.preHREF);
	  tempString += MTMenuImageDirectory + thisImage + '")'
	  return(tempString);
	}

	function MTMakeA(thisType, thisText, thisColor) {
	  var tempString = "";
	  tempString += 'a' + ((thisType == "pseudo") ? ':' : '.');
	  return(tempString + thisText + ' {\n\tcolor:' + thisColor + ';\n\tbackground:transparent;\n}\n');
	}

	function MTMTrackTarget(thisTarget) {
	  if(thisTarget.charAt(0) == "_") {
	    return false;
	  } else {
	    for(i = 0; i < MTMFrameNames.length; i++) {
	      if(thisTarget == MTMFrameNames[i]) {
	        return true;
	      }
	    }
	  }
	  return false;
	}


	var MTMTableWidth = "100%";

	var MTMenuFrame = "menu";

	var MTMSubsGetPlus = "Always";

	var MTMEmulateWE = false;

	var MTMenuImageDirectory = "images/";

	var MTMBGColor = "#000000";
	var MTMBackground = "blueback1`s.jpg";
	var MTMTextColor = "black";

	var MTMLinkColor = "black";

	var MTMAhoverColor = "red";

	var MTMTrackColor ="yellow";
	var MTMSubExpandColor = "black";
	var MTMSubClosedColor = "black";

	var MTMRootIcon = "Tree_root.gif";
	var MTMenuText = "<%= FoursoftWebConfig.TREE_TOP_LABEL %>";
	var MTMRootColor = "black";
	var MTMRootFont = "Arial, Helvetica, sans-serif";
	var MTMRootCSSize = "84%";
	var MTMRootFontSize = "2";

	var MTMenuFont = "Arial, Helvetica, sans-serif";
	var MTMenuCSSize = "84%";
	var MTMenuFontSize = "-1";


	var MTMLinkedSS = false;
	var MTMSSHREF = "style/menu.css";


	var MTMExtraCSS = "";

	var MTMHeader = "";
	var MTMFooter = "";

	var MTMSubsAutoClose = false;


	var MTMTimeOut = 25;

	var MTMUseToolTips = true;

	var menu = menu = new MTMenu();
		
</script>

<%=	processedScript %>

</head>
<body onload="MTMStartMenu()">
</body>
</html>
<%		} else { // if alevel !=null && alevel size > 0
%>
<html>
<head>
<script language="javascript">

function writeMessage() {
	var msg = "<html><body><font face='Verdana' size='2' color='red'><b><p>Sorry! You no longer have this Role (permissions) to access the application. Please contact your Administrator for further clarifications.</p></b></font></body></html>";

	var MTMDoc = parent.frames["menu"].document

	MTMDoc.open("text/html", "replace");
	MTMDoc.writeln( msg );
	MTMDoc.close();
	
}

</script>
<body onLoad="writeMessage();">
</body>
</html>

<%		}
%>


<%
	} 
	catch(Exception ex) 
	{
		errorOcurred = true;
		//inValidateSession( request );
		//Logger.error(fileName, "Log-in process was terminated as an error occurred while opening application operations page.");				
    logger.error(fileName+ "Log-in process was terminated as an error occurred while opening application operations page.");				
		request.setAttribute("Login_Message",errorMessage);
		%><jsp:forward page="<%=loginPage%>" /><%
	} 
	finally 
	{
		if(errorOcurred==false) 
			request.setAttribute("windowName", windowName);
	}

%>
<%!
	private void inValidateSession( HttpServletRequest request ) {

		try {
			if(request!=null) {
				HttpSession		session	=	request.getSession();
				if(session!=null) {
					session.invalidate();
					//Logger.error(fileName, "Log-in process was terminated as an error occurred while logging in.");				
          logger.error(fileName+ "Log-in process was terminated as an error occurred while logging in.");				
				}
			}
		} catch(Exception ex) {}
		
	}
%>