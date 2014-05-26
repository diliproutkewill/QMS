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
 % File			: ESMenuIndex.jsp
 % sub-module	: AccessControl
 % module		: esupply
 %
 % This is a simple frame set
 % 
 % author		: Sasi Bhushan. P
 % date			: 22-12-2001
 % Modification  history
 % Amit Parekh		24/10/2002		Logout functionality changed to handle higher level even handling
 % Amit Parekh		19/11/2002		Changes made to handle facility switching in EP for a COMPANY User and
 %									warehouse customer location switching for a WAREHOUSE level User in ELOG
 % Amit Parekh		27/02/2003		Change sone to make switching of warehouses/locations and thus roles possible in SP.
 % Amit Parekh		06/05/2003		This file's method is now synchronized as it handles (creates or manipulates) an application level object
--%>
<%@ page import="javax.naming.*, org.apache.log4j.Logger, java.util.*, com.foursoft.esupply.common.bean.UserCredentials, com.foursoft.esupply.delegate.AccessControlDelegate,javax.servlet.jsp.jstl.fmt.LocalizationContext "%>

<jsp:useBean id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/>
<jsp:useBean id="appSettings" class="java.util.Hashtable" scope="application"/>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt_rt" prefix="fmt_rt"%>
<%!
  private static Logger logger = null;
	String fileName	= "ESMenuIndex.jsp";
	
	%>
<%
   logger  = Logger.getLogger(fileName);
   String language = loginbean.getUserPreferences().getLanguage();
	System.out.println("loginbean.getUserPreferences().getLanguage()"+loginbean.getUserPreferences().getLanguage());
%>

<fmt_rt:setLocale value="<%=language%>"/>
<fmt:setBundle basename="Lang" var="lang" scope="application"/>
<% 
	synchronized(this) {
        
        AccessControlDelegate accessDelegate = new AccessControlDelegate();
		String	title = FoursoftWebConfig.TREE_TOP_LABEL;
		boolean	errorOcurred	=	false;

		String	userType		=	(String)	session.getAttribute("userType");
		String	windowName		=	(String)	request.getAttribute("windowName");
	
		String	loginPage		=	"ESupplyLogin?userType="+userType+"&windowName="+windowName;
		String	errorMessage	=	((LocalizationContext)application.getAttribute("lang")).getResourceBundle().getString("100034");

		try {

			if(session.getAttribute("ACCESSTYPE").equals("WAREHOUSE") && (FoursoftWebConfig.MODULE.equals("ELOG") || FoursoftWebConfig.MODULE.equals("SP")))
			{
				//System.out.println("session.getAttribute(ACCESSTYPE) %%%%%%%%%%%%%%%%%%%%%%%%%% = "+session.getAttribute("ACCESSTYPE")+" FoursoftWebConfig.MODULE = "+FoursoftWebConfig.MODULE +" request.getParameter(warehouse) ="+request.getParameter("warehouse"));
				if(request.getParameter("warehouse") != null)
				{
	
					String wareHouse = request.getParameter("warehouse");
					String type		 = request.getParameter("type");
					//System.out.println("type %%%%%%%%%%%%% ="+type+" wareHouse ="+wareHouse);
					//Logger.info(fileName,"ELOG / WAREHOUSE : = '"+wareHouse+"'  ;  type = '"+type+"'");
          logger.info(fileName+"ELOG / WAREHOUSE : = '"+wareHouse+"'  ;  type = '"+type+"'");
		
					//Logger.info(fileName,"Switching to Warehouse "+wareHouse+" and type "+type);
          logger.info(fileName+"Switching to Warehouse "+wareHouse+" and type "+type);

					if(type.equals("WH"))	// If its a CUSTWH
					{
						//session.setAttribute("isCustWH","yes");
						session.setAttribute("isWareHouse","yes");
			
						loginbean.setProjectId((String)loginbean.getELogCredentials().getProjectInfo().get(wareHouse));
						loginbean.setCustWHId( wareHouse );
						loginbean.setAccessType("CUSTWH");

						//Get Optional Role and its Role Location Id

						System.out.println("In ESMenuIndex if type is CUSTWH");
					}
				
					if(type.equals("PT"))	// If its a WAREHOUSE
					{
						//session.setAttribute("isCustWH","no");
						session.setAttribute("isWareHouse","no");

						loginbean.setProjectId("");
						loginbean.setWareHouseId( wareHouse );
						loginbean.setAccessType("WAREHOUSE");

						System.out.println("In ESMenuIndex if type is WAREHOUSE");

						//Get Optional Role and its Role Location Id
					}

					// Process Common to both
					if(type.equals("WH") || type.equals("PT")) 
					{
						String	currencyId = accessDelegate.getETransCurrencyId( wareHouse );
						//String	currencyId = remote.getETransCurrencyId( wareHouse );
						//Logger.info(fileName,"Changed currencyId to:"+currencyId);
            logger.info(fileName+"Changed currencyId to:"+currencyId);

						loginbean.setCurrencyId( currencyId );
						loginbean.setLocationId( wareHouse );
						loginbean.setAccountsCredentials( accessDelegate.prepareAccountsCredentials( loginbean.getLocationId(), loginbean.getAccessType()) );

						System.out.println("In ESMenuIndex if type is wAREHOUSE AND CUSTWH");

						//loginbean.setAccountsCredentials( remote.prepareAccountsCredentials( loginbean.getLocationId(), loginbean.getAccessType()) );
					}

					//String	loggingUserId	=	loginbean.getLocationId() +"."+loginbean.getUserId();
					String loggingUserId	=	 loginbean.getUserId()+"/"+loginbean.getLocationId()+"/"+session.getAttribute("userType");
				
					String sessionId	=	session.getId();
		
					UserCredentials uc	= (UserCredentials) appSettings.get( sessionId );

					//uc.setUserId( loggingUserId );

					//Logger.info(fileName, "Before Removing from Logged In List : Size = "+appSettings.size() +" :: session Id : '"+sessionId+"'" );
          logger.info(fileName+ "Before Removing from Logged In List : Size = "+appSettings.size() +" :: session Id : '"+sessionId+"'" );
					//appSettings.remove( sessionId );
					//Logger.info(fileName,"After Removing from Logged In List : Size = "+appSettings.size());
          logger.info(fileName+"After Removing from Logged In List : Size = "+appSettings.size());
			

					//appSettings.put( sessionId, uc );
	
					//Logger.info(fileName,"The User Credentials Changed on Warehouse Switching --> Credentials are: '"+uc.getUserId()+"' ; Size = "+appSettings.size());
          logger.info(fileName+"The User Credentials Changed on Warehouse Switching --> Credentials are: '"+uc.getUserId()+"' ; Size = "+appSettings.size());

				}
	
				//Logger.info(fileName,"ESupplyGlobalParameters : ELOG: After Switching Cust Warehouses::\n"+loginbean);
        logger.info(fileName+"ESupplyGlobalParameters : ELOG: After Switching Cust Warehouses::\n"+loginbean);
			}

			// This is for eSupply-EP
			if(session.getAttribute("ACCESSTYPE").equals("COMPANY") && FoursoftWebConfig.MODULE.equals("EP"))
			{
	
				if(request.getParameter("warehouse") != null)
				{
					String wareHouse = request.getParameter("warehouse");
					String type		 = request.getParameter("type");

					//Logger.info(fileName,"EP / WAREHOUSE : = '"+wareHouse+"'  ;  type = '"+type+"'");
          logger.info(fileName+"EP / WAREHOUSE : = '"+wareHouse+"'  ;  type = '"+type+"'");
		
					//Logger.info(fileName,"Switching to Warehouse "+wareHouse+" and type "+type);
          logger.info(fileName+"Switching to Warehouse "+wareHouse+" and type "+type);
		
					if(type.equals("WH"))	// If its a WAREHOUSE
					{
						//session.setAttribute("isCustWH","yes");
						session.setAttribute("isWareHouse","yes");

						loginbean.setWareHouseId( wareHouse );
						loginbean.setAccessType("WAREHOUSE");
					}
				
					if(type.equals("PT"))	// If its a COMPANY
					{
						//session.setAttribute("isCustWH","no");
						session.setAttribute("isWareHouse","no");
						//loginbean.setWareHouseId( wareHouse ); This will be used for only Franlin Application
						
						loginbean.setCompanyId( wareHouse );
						loginbean.setAccessType("COMPANY");
					}

					// Process Common to both
					if(type.equals("WH") || type.equals("PT")) 
					{
						String	currencyId = accessDelegate.getETransCurrencyId( wareHouse );
						//String	currencyId = remote.getETransCurrencyId( wareHouse );
						//Logger.info(fileName,"Changed currencyId to:"+currencyId);
            logger.info(fileName+"Changed currencyId to:"+currencyId);

						loginbean.setCurrencyId( currencyId );
						loginbean.setLocationId( wareHouse );
						loginbean.setAccountsCredentials( accessDelegate.prepareAccountsCredentials( loginbean.getLocationId(), loginbean.getAccessType()) );
						//loginbean.setAccountsCredentials( remote.prepareAccountsCredentials( loginbean.getLocationId(), loginbean.getAccessType()) );
						loginbean.setProjectId("");
					}

					//String	loggingUserId	=	loginbean.getLocationId() +"."+loginbean.getUserId();
					String loggingUserId	=	 loginbean.getUserId()+"/"+loginbean.getLocationId()+"/"+session.getAttribute("userType");
				
					String sessionId	=	session.getId();
		
					UserCredentials uc	= (UserCredentials) appSettings.get( sessionId );

					//uc.setUserId( loggingUserId );

					//Logger.info(fileName, "Before Removing from Logged In List : Size = "+appSettings.size() +" :: session Id : '"+sessionId+"'" );
          logger.info(fileName+ "Before Removing from Logged In List : Size = "+appSettings.size() +" :: session Id : '"+sessionId+"'" );
					//appSettings.remove( sessionId );
					//Logger.info(fileName,"After Removing from Logged In List : Size = "+appSettings.size());
          logger.info(fileName+"After Removing from Logged In List : Size = "+appSettings.size());
			

					//appSettings.put( sessionId, uc );
	
					//Logger.info(fileName,"The User Credentials Changed on Warehouse Switching --> Credentials are: '"+uc.getUserId()+"' ; Size = "+appSettings.size()s);
          logger.info(fileName+"The User Credentials Changed on Warehouse Switching --> Credentials are: '"+uc.getUserId()+"' ; Size = "+appSettings.size());

				}
	
				//Logger.info(fileName,"ESupplyGlobalParameters : EP: After Switching Warehouses::\n"+loginbean);
        logger.info(fileName+"ESupplyGlobalParameters : EP: After Switching Warehouses::\n"+loginbean);
			}


			if(session.getAttribute("ACCESSTYPE").equals("HO_TERMINAL") && (FoursoftWebConfig.MODULE.equals("ELOG") || FoursoftWebConfig.MODULE.equals("SP")))
			{
				ArrayList	list	=	loginbean.getELogCredentials().getCustWHIdInfo();
				//System.out.println();
				//for(int i=0; i < list.size(); i++) {
					///System.out.println("COMPANYID["+i+"] = "+(String) list.get(i) );
				//}
				//System.out.println();
				//System.out.println("CURRENCYID = "+loginbean.getCurrencyId() );
			}

%>
<html>
<HEAD>
<title><fmt:message key="100317" bundle="${lang}"/> <%= title %> </title>
<%
	response.setHeader("Expires", "0");
	response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
	response.setHeader("Pragma", "public");
%>
<script language="jscript">

	var key		=	'';
	var from	=	'body';
	var mskey	=	'initial';

	function unloader() 
	{

	
				var iX = window.document.body.offsetWidth - window.event.clientX;
				var iY = window.event.clientY;
				//alert("OFFSET"+window.document.body.offsetWidth);
				//alert("CLIENT X"+event.clientX);
				//alert(iX);
				//alert(iY);

				if((iX <=20)&&(iY<0))
				{
				mskey="obul";
				return "";
				}
	
		if(window.event.keyCode==0)
			mskey='refresh';
		

	}

</script>

<%@ include file="ESEventHandler.jsp" %>
</HEAD>

<frameset frameborder="1" framespacing="0" border="1" cols="*" rows="35,*" onunload="heading.Xclose();" onBeforeUnload="return unloader();">

   <frameset frameborder="1" framespacing="0" border="1" cols="850,*">
      <frame marginwidth="0" marginheight="1" src="ESMenuHeading.jsp"  name="heading" scrolling="no" frameborder="0">
	  <%/*
	 	if(loginbean.getAccessType()!=null && (loginbean.getAccessType().equals("OPER_TERMINAL")))	
		{*/
			
%>
      <!--frame marginwidth="0" marginheight="1" src="ETTaskEvntController?terminal=<%//=loginbean.getTerminalId()%>"  name="taskTrack" scrolling="no" frameborder="0"-->
<%      //} %>
      <frame marginwidth="0" marginheight="5" src="ESMenuHeadingRight" name="headingright"  scrolling="no" frameborder="0">
    </frameset>

   <frameset id="TreeFrame" frameborder="1" framespacing="1" border="1" cols="175,*" rows="*">  
	 <frameset frameborder="1" framespacing="0" border="1" cols="*" rows="0,*">
       <frame marginwidth="1" marginheight="1" src="ESMenuTreeGenerator.jsp" name="code"  scrolling="no" frameborder="0">
       <frame marginwidth="5" marginheight="5" src="html/ESMenuEmpty.html" name="menu"  scrolling="auto" frameborder="2">
     </frameset>	
     <frame marginwidth="5" marginheight="5" src="ESMenuMainPage" name="text"  scrolling="AUTO" frameborder="2" >
   </frameset>

  
	<noframes>
	  <p><fmt:message key="100036" bundle="${lang}"/> <code><fmt:message key="100037" bundle="${lang}"/></code> <fmt:message key="100035" bundle="${lang}"/></p>
	</noframes>
	
</frameset>
 </frameset>
</html>
<%
		} catch(Exception ex) {
			errorOcurred = true;
			inValidateSession( request );
			//Logger.error(fileName, "Log-in process was terminated as an error occurred while opening application operations page.");				
      logger.error(fileName+ "Log-in process was terminated as an error occurred while opening application operations page.");				
			request.setAttribute("Login_Message",errorMessage);
			%><jsp:forward page="<%=loginPage%>" /><%
		} finally {
			if(errorOcurred==false) {
				request.setAttribute("windowName", windowName);
			}
		}

	} // sync block ends

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