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
 % ESACRolePermissionsConfirm.jsp
 % sub-module	: AccessControl
 % module		: esupply
 %
 % This is used to display the all Roles information 
 % 
 % author		: Madhu. P
 % date			: 5-09-2001
--%>
<%@ page import= "com.foursoft.esupply.common.bean.ESupplyGlobalParameters,
				org.apache.log4j.Logger,
				com.foursoft.esupply.accesscontrol.util.UserAccessUtility,
				com.foursoft.esupply.accesscontrol.java.UserAccessConfig,
				com.foursoft.esupply.accesscontrol.java.RoleModel,
				com.foursoft.esupply.common.java.*,
				java.util.ArrayList,
				java.util.Iterator,
				java.util.Hashtable,
				java.util.Collections,java.util.ResourceBundle,javax.servlet.jsp.jstl.fmt.LocalizationContext " %>
<jsp:useBean id="_rolePermissionControllerJBean" scope="session" class="com.foursoft.esupply.accesscontrol.bean.RolePermissionControllerJBean"/>
<jsp:useBean id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/>	
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt_rt" prefix="fmt_rt"%>
<%!
	String	fileName	= "ESACRolePermissionsConfirm.jsp";
	
%>
<%
	   String language = loginbean.getUserPreferences().getLanguage();
%>
<fmt_rt:setLocale value="<%=language%>"/>
	<fmt:setBundle basename="Lang" var="lang" scope="application"/>
<%
	//System.out.println();
	//System.out.println("PERMISSIONs CONFIRMATION begins");	
	
	CachedTransactions	transList	=	(CachedTransactions) CacheManager.getCache("transactions-list");
	Hashtable			txIdDescription	= transList.getTxList();

	String action = null;
	action = request.getParameter("action");
	
	RoleModel	roleModel	= (RoleModel) session.getAttribute("roleModel");
	
	String		actionPage	= "ESACRoleRegistrationController.jsp";

	String		locationIdLeble		= UserAccessUtility.getLocationLable( roleModel.getRoleLevel() );	

	Hashtable	htRolePermissions	= null;
	Iterator	itPermissions		= null;
	String		transactionId		= null;
	String		readCheck			= "";
	String		writeCheck			= "";
	String		deleteCheck			= "";
	String		invalidateCheck		= "";
	int			accessLevel			= 0; 

	// We had stashed away al permissions select by the User in a Hashtable of the bean
	// '_rolePermissionControllerJBean'. We now get them from that and show to USer for
	// final confirmation
	
	//System.out.println(" locationIdLeble = "+locationIdLeble);
	
	htRolePermissions = _rolePermissionControllerJBean.getAllPermissions();
	
	//System.out.println(" got permissions from bean '_rolePermissionControllerJBean'");
	//System.out.println(" Checking Keys");
	
	Iterator	testItr		= htRolePermissions.keySet().iterator();
	int cnt = 0;	
%>

<html>
<head>
<title><fmt:message key="100256" bundle="${lang}"/></title>
<link rel="stylesheet" href="ESFoursoft_css.jsp">
<style type="text/css">
tr.report {
	background-color: #f2f2f9;
	border-width : thin;
	font-family : Verdana;
	font-size : 10px;
}
tr.heading {
	background-color: #b3b3d9;
	border-width : thin;
	font-family : Verdana;
	font-size : 12px;
	font-weight : bold;	
}
</style>
<%@ include file="/ESEventHandler.jsp" %>
<% 
/*
if (!FoursoftWebConfig.MODULE.equals(FoursoftWebConfig.EP))
{
   response.setHeader("Cache-Control","no-cache"); // HTTP 1.1
   response.setHeader("Pragma","no-cache"); // HTTP 1.0
   response.setDateHeader ("Expires", -1); // Prevents caching at the proxy server
}
*/
%>
</head>
<body >
<form method="POST" action="<%= actionPage %>" >

<table width="760" border="0" cellspacing="0" cellpadding="0">
 <tr valign="top" bgcolor="#FFFFFF"> 
   <td>

	<table width="100%" class='formlabel' border="0">
	  <tr>
	    <td class="formlabel"><fmt:message key="100286" bundle="${lang}"/></td>
		<td class="formlabel" align="right"><%=loginbean.generateUniqueId(fileName,action)%></td>
	  </tr>
	</table>
	
	<br>
	
	<table width="100%" border="0" cellspacing="1" cellpadding="0">
	  
	  <tr>
	    <td colspan="2">
	      <table width="100%" border="0" cellspacing="1" cellpadding="1">

<%
	String[] modules	= (String[]) session.getAttribute("module");
	String[] modes		= (String[]) session.getAttribute("mode");
	boolean	 hasModes	= ((Boolean) session.getAttribute("hasModes")).booleanValue();
	
	int	modesLength	=	0;
	int	modeCount	=	0;
	
	if(hasModes && modes!=null) {
		modesLength	=	modes.length;
	}
	
%>
	        <tr class="formlabel"  height="25">
	          <td width="64%" valign="middle"><fmt:message key="100258" bundle="${lang}"/></td>
	          <td width="12%" valign="middle" align="center"><fmt:message key="100259" bundle="${lang}"/></td>
	          <td width="12%" valign="middle" align="center"><fmt:message key="100260" bundle="${lang}"/></td>
	          <td width="12%" valign="middle" align="center"><fmt:message key="100261" bundle="${lang}"/></td>
			  <td width="12%" valign="middle" align="center"><fmt:message key="100627" bundle="${lang}"/></td>
	        </tr>
<%
	
	int moduleCount	=	0;
	boolean moduleChanged	= true;
	boolean modeChanged		= false;
	
	while(moduleCount < modules.length)
	{
		//System.out.println(" moduleCount = "+moduleCount);
		Hashtable	tempTable = null;
		
		//System.out.println(" Module at index "+moduleCount+" = "+modules[ moduleCount ] );
		
		int 	currentModuleIndex	= Integer.parseInt( modules[ moduleCount ] );
		String	currentModule		= UserAccessUtility.getRoleModule( currentModuleIndex );
		String	subModule			= "";
		String	currentShipmentMode	= "";
		int		shipmentMode		= 0;
		
		if(currentModule.equals("ETRANS") && hasModes && modeCount < modesLength) {
			
			modeChanged = true;
			currentShipmentMode	=	modes[modeCount];
			shipmentMode		=	ShipmentMode.getShipmentMode( currentShipmentMode );
			subModule			= currentModule + "@" + shipmentMode ;
			
			tempTable	=	(Hashtable) htRolePermissions.get( subModule );
			//System.out.println(" Got the Permissions Hash table for '"+subModule+"'");
			
			//System.out.println(" modeCount = "+modeCount);
			//System.out.println(" subModule = '"+subModule+"'");
			//System.out.println(" Now incrementing mode counter");
			
			modeCount++;

		} else {
			// Process normally for other modes
			tempTable	=	(Hashtable) htRolePermissions.get( currentModule );
			//System.out.println(" Got the Permissions Hash table for '"+currentModule+"'");
		}
		
		if(moduleChanged) {
%>
	        <tr class="formlabel" height="20">
	          <td colspan=5 valign="middle"><fmt:message key="100287" bundle="${lang}"/> <%= UserAccessConfig.getModuleLabel( currentModule ).equals("ETRANS")?"QuoteShop":UserAccessConfig.getModuleLabel( currentModule ) %></td>
	        </tr>
<%
			moduleChanged = false;
		}
		
		if(modeChanged) {
			String label = "";
			
			if(shipmentMode == 0) {
				label = "Setup";
			}

			if(shipmentMode > 0) {
				label = "Shipment Mode : "+ currentShipmentMode;
			}
%>
			<tr class="formdata">
			  <td colspan=5 valign="middle"><b><%= label %></b></td>
			</tr>
<%
			modeChanged = false;
		}
	
//		Logger.info(fileName,"Lists of "+i+" is :  "+tempTable );
		/**
		* replaced with the below code to Sort
		*/
		//System.out.println(" Now processing permissions hashtable");
		
	    Iterator	txIteratorHash	= tempTable.keySet().iterator();
		ArrayList	txArrList		= new ArrayList();
		
	    while(txIteratorHash.hasNext())
		{
			txArrList.add( (String) txIteratorHash.next() );	
		}
		
		txIteratorHash = null;
		Collections.sort(txArrList);
		itPermissions = txArrList.iterator();
		
		/*
		 * based on the accessLevel of the each previllage, the check boxes will be selected
		 * If the accessLevel is 7, then the role has got all Previllages for that tranasction
		 */		  

		//System.out.println(" Got ArrayList iterator");
		//System.out.println();

		while(itPermissions.hasNext())
		{
			transactionId	= (String)	 itPermissions.next();
			accessLevel		= ((Integer) tempTable.get(transactionId)).intValue();
			
			//System.out.println("  TXID = '"+transactionId+"'	:  accessLevel = "+accessLevel);
			
			readCheck         = "";
			writeCheck        = "";
		    deleteCheck       = "";
			invalidateCheck	  = "";
			switch(accessLevel)
			{
				case 15 :
				{
					readCheck			= "checked";
					writeCheck			= "checked";
					deleteCheck			= "checked";
					invalidateCheck		= "checked";
					break;
				}
				case 14 :
				{
					writeCheck			= "checked";
					deleteCheck			= "checked";
					invalidateCheck		= "checked";
					break;
				}
				case 13 :
				{
					readCheck			= "checked";
					deleteCheck			= "checked";
					invalidateCheck		= "checked";
					break;
				}
				case 12 :
				{
					deleteCheck			= "checked";
					invalidateCheck		= "checked";
					break;
				}
				case 11 :
				{
					readCheck			= "checked";
					writeCheck			= "checked";
					invalidateCheck		= "checked";
					break;
				}
				case 10 :
				{
					writeCheck			= "checked";
					invalidateCheck		= "checked";
					break;
				}
				case 9 :
				{
					readCheck			= "checked";
					invalidateCheck		= "checked";
					break;
				}
				case 8 :
				{
					invalidateCheck		= "checked";
					break;
				}
				case 7 :
				{
					readCheck         = "checked";
					writeCheck        = "checked";
					deleteCheck       = "checked";
					break;
				}
				case 6 :
				{
					writeCheck        = "checked";
					deleteCheck       = "checked";
					break;
				}
				case 5 :
				{
					readCheck         = "checked";
					deleteCheck       = "checked";
					break;
				}
				case 4 :
				{
					deleteCheck       = "checked";
					break;
				}
				case 3 :
				{
					readCheck         = "checked";
					writeCheck        = "checked";
					break;
				}
				case 2 :
				{
					writeCheck        = "checked";
					break;
				}
				case 1 :
				{
					readCheck         = "checked";
					break;
				}
				default :
				{
					break;
				}
			}			
			
%> 
	        <tr class="formdata">
	          <td width="64%" valign="middle"><%= txIdDescription.get(transactionId) %></td>
	          <td width="12%" align="center" valign="middle"><input type="checkbox" name="<%= transactionId %>" <%=readCheck%> value="R" disabled ></td>
	          <td width="12%" align="center" valign="middle"><input type="checkbox" name="<%= transactionId %>" <%=writeCheck%> value="W" disabled ></td>
	          <td width="12%" align="center" valign="middle"><input type="checkbox" name="<%= transactionId %>" <%=deleteCheck%> value="D" disabled ></td>
			   <td width="12%" align="center" valign="middle"><input type="checkbox" name="<%= transactionId %>" <%=invalidateCheck%> value="I" disabled ></td>
	        </tr>
<%

		}// end of while
		
		//System.out.println();
		
		if(! (currentModule.equals("ETRANS") && hasModes && modeCount < modesLength) ) {
			moduleCount++;
			//System.out.println("  Now incrementing module counter to "+moduleCount);
			moduleChanged = true;
		}
		
	} // end of modules while
	
%> 
        
    		</table>
	  	  </td>
		</tr>
		
	</table>
	
	<table width="100%" border="0" cellspacing="1" cellpadding="4">
  		<tr>
	      <td align="right">
			<input type=hidden name=screen_name value="role_confirmation" >
			<input type="hidden" name="action"  value="<%= action %>">
			<input type="submit" name="Submit"  value='<fmt:message key="6666" bundle="${lang}"/>' class="input">
		  </td>
		</tr>
<%
	if(roleModel.getVersion() > 0) {
%>
		<tr valign="top">
          <td width="100%" >
              <font face="Verdana" size="1"> <%= roleModel.getModificationInfoMessage("HTML") %>
			  </font>
		  </td>
		</tr>
<%	
	}
%>
	</table>


  </td>
 </tr>
</table>
<input type="hidden" readOnly name="version" value="<%= roleModel.getVersion() %>">
</form>
</body>
</html>