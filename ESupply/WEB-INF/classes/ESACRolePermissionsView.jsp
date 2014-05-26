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
 % ESACRolePermissionsView.jsp
 % sub-module	: AccessControl
 % module		: esupply
 %
 % This is used to display the Roles Previllages
 % 
 % author		: Madhu. P
 % date			: 5-09-2001
 % Modified History
 % Madhusudhana rao. P		-- 29-01-2002
 %		Fixed the problem of Displaying the role when thre is no previlleges attached to it 
--%>
<%@ page  import="java.util.ArrayList, java.util.Iterator, com.foursoft.esupply.accesscontrol.java.TxDetailVOB, com.foursoft.esupply.common.java.*, java.util.Hashtable, com.foursoft.esupply.accesscontrol.java.UserAccessConfig, com.foursoft.esupply.accesscontrol.java.RoleModel, org.apache.log4j.Logger,java.util.ResourceBundle,javax.servlet.jsp.jstl.fmt.LocalizationContext" %>

<jsp:useBean id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt_rt" prefix="fmt_rt"%>
<%!
     String fileName = "ESACRolePermissionsView.jsp"; 
		
%>
<%
	   String language = loginbean.getUserPreferences().getLanguage();
%>
<fmt_rt:setLocale value="<%=language%>"/>
	<fmt:setBundle basename="Lang" var="lang" scope="application"/>
<%
	//System.out.println();
	//System.out.println("ESACRolePermissionsView.jsp Starts");
	
	String	roleId      	= null;
	String	locationId  	= null;
	String	actionPage  	= "ESACUserRoleEnterId.jsp";
	String 	action			= request.getParameter("action");
	String lable			= ((LocalizationContext)application.getAttribute("lang")).getResourceBundle().getString("100300");
	RoleModel roleModel = (RoleModel) session.getAttribute("roleModel");
	
	
	String isPopUp		= request.getParameter("isPopUp");
	
	if(isPopUp == null)
		isPopUp = "no";
		
	if(action != null) {
		if(action.equalsIgnoreCase("Delete") ) {
			actionPage	= "ESACRoleRegistrationController.jsp";
			lable			=((LocalizationContext)application.getAttribute("lang")).getResourceBundle().getString("100198");
		}
	} else {
		action = "View";
		lable			=((LocalizationContext)application.getAttribute("lang")).getResourceBundle().getString("100300");
	}

	CachedTransactions	transList	=	(CachedTransactions)CacheManager.getCache("transactions-list");
	Hashtable			txIdDescription	= transList.getTxList();
//	Logger.info(fileName,"Role Model "+roleModel);
%>

<html>
<head>
<title><fmt:message key="100217" bundle="${lang}"/> </title>
<link rel="stylesheet" href="ESFoursoft_css.jsp">
<script language="javascript">
	function handleEvent()
	{	
		var keycode	= (window.Event) ? window.event.which : window.event.keyCode;

		if(keycode == 27 || keycode == 13) {
			window.close();
		}
	}
</script>

<%@ include file="/ESEventHandler.jsp" %>
<% 
//if (!FoursoftWebConfig.MODULE.equals(FoursoftWebConfig.EP))
//{
//   response.setHeader("Cache-Control","no-cache"); // HTTP 1.1
//   response.setHeader("Pragma","no-cache"); // HTTP 1.0
//   response.setDateHeader ("Expires", -1); // Prevents caching at the proxy server
//}

%>
</head>
<%
if(isPopUp.equalsIgnoreCase("yes") )
{
%>
<body onKeyPress="handleEvent();">
<%
} else {
%>
<body>
<%
}
%>
<form method="POST" action="<%= actionPage %>">

  <table width="760" border="0" cellspacing="0" cellpadding="0">
    <tr>
      <td bgcolor="ffffff">
        
        <table width="760" class='formlabel' border="0" >
          <tr height="25"> 
            <td  class="formlabel" colspan="6"><%=lable%></td>
			<td  class="formlabel" align="right"><%=loginbean.generateUniqueId(fileName,action)!=null?loginbean.generateUniqueId(fileName,action):"QS0010142"%></td>
          </tr>
		</table>

		<br>
		 
		<table width="760" border="0" cellspacing="1" cellpadding="1">
          <tr class="formdata">
			<td colspan="3"><fmt:message key="100213" bundle="${lang}"/><%= roleModel.getRoleId() %></td>
	        <td colspan="3"><fmt:message key="100247" bundle="${lang}"/><%= roleModel.getLocationId() %></td>
          </tr>
          <tr class="formdata">
			<td colspan="3">&nbsp;</td>
	        <td colspan="3">&nbsp;</td>
          </tr>
        </table>
		
        <table width="760" border="0" cellspacing="1" cellpadding="1" >
          <tr class="formheader" height="25"> 
            <td width="64%" valign="middle"><fmt:message key="100258" bundle="${lang}"/></td>
            <td width="12%" valign="middle" align="center"><fmt:message key="100259" bundle="${lang}"/></td>
            <td width="12%" valign="middle" align="center"><fmt:message key="100260" bundle="${lang}"/></td>
            <td width="12%" valign="middle" align="center"><fmt:message key="100261" bundle="${lang}"/></td>
			<td width="12%" valign="middle" align="center"><fmt:message key="100627" bundle="${lang}"/></td>
          </tr>
    <%--
	 % based on the accessLevel of the each previllage, the check boxes will be selected
	 % If the accessLevel is 7, then the role has got all Previllages for that tranasction
	--%> 
	<%
		ArrayList rolePermissions = roleModel.getRolePermissions();

		String module				=	"";
		String readCheck			=	"";
		String writeCheck			=	"";
		String deleteCheck			=	"";
		String invalidateCheck 		=	"";

		TxDetailVOB	appVOB	= null;
		
		//String	currentModule	=	(String)
		
		String	lastModuleFound		=	"";
		int		lastModeFound		=	-1;
		
		boolean	moduleChanged	= true;
		boolean	modeChanged	= true;
		
		for(int i=0; i < rolePermissions.size(); i++)
		{

			appVOB	= (TxDetailVOB) rolePermissions.get(i);
			
			int		shipmentMode	=	appVOB.shipmentMode;
			String	currentModule	=	appVOB.module;
			String	label				= "";
			String	currentShipmentMode	= "";
			
			if(currentModule.equals( lastModuleFound )==false) {
				String operationModule = UserAccessConfig.getModuleLabel( currentModule );
				if(operationModule.equalsIgnoreCase("ETRANS"))
					operationModule = "QuoteShop";
%>
		        <tr class="formlabel" height="20">
		          <td colspan="5" valign="middle" ><fmt:message key="100287" bundle="${lang}"/> <%= operationModule %></td>
	        	</tr>
<%
				lastModuleFound = currentModule;
			}
			
			if(shipmentMode != lastModeFound) {
			
				if(shipmentMode == 0) {
					//label = "Setup";
				}
				
				if(shipmentMode > 0) {
					currentShipmentMode = ShipmentMode.getShipmentMode( shipmentMode );
					label = ((LocalizationContext)application.getAttribute("lang")).getResourceBundle().getString("100301")+ currentShipmentMode;
				}
				
				if(shipmentMode >= 0) {
%>
				<tr class="report">
				  <td colspan="4" valign="middle" ><b><%= label %></b></td>
				</tr>
<%
				}
				lastModeFound = shipmentMode;
			}

			readCheck         = "";
			writeCheck        = "";
			deleteCheck       = "";
			invalidateCheck	  = "";
			// this swith sataement is used to help in displaying the previllages to eact transaction based on the access Level
			switch(appVOB.accessLevel)
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
          <tr class="formdata" align="center"> 
            <td align="left" width="64%" valign="middle" > <%= txIdDescription.get(appVOB.txId) %> </td>
            <td width="12%" valign="middle" > 
              <input type="checkbox" name="<%= appVOB.txId %>" <%=readCheck%> value="R" disabled >
            </td>
            <td width="12%" valign="middle" > 
              <input type="checkbox" name="<%= appVOB.txId %>" <%=writeCheck%> value="W" disabled >
            </td>
            <td width="12%" valign="middle" > 
              <input type="checkbox" name="<%= appVOB.txId %>" <%=deleteCheck%> value="D" disabled >
            </td>
			<td width="12%" valign="middle" > 
              <input type="checkbox" name="<%= appVOB.txId %>" <%=invalidateCheck%> value="I" disabled >
            </td>
          </tr>
<%

		} // for
%> 
		</table>
		
		<table width="760" border="0" cellspacing="1" cellpadding="4" >
          <tr  align="right"> 
            <td colspan="5" height="21" >
			<input type=hidden name=screen_name value="role_permissions_view" >
			 
<%
	if(!isPopUp.equalsIgnoreCase("yes") )
	{
		if(action.equalsIgnoreCase("Delete") )	{ %> 
              <input type=submit name="C"  value='<fmt:message key="100004" bundle="${lang}"/>' class="input">
<%		} else {
%>            <input type=hidden name="action" value="<%= action %>">
			  <input type=hidden name="UIName" value="Role">	
			  <input type=submit name="C" class="input" value='<fmt:message key="7777" bundle="${lang}"/>'>
<%		}
	}
	else
	{
%>
        <input type=button name="Close" value='<fmt:message key="8888" bundle="${lang}"/>' class="input" onClick="javascript:window.close()">
<%		
	}
	
	//System.out.println("ESACRolePermissionsView.jsp Ends");
	//System.out.println();
%>
		</td>
          </tr>
        </table>
      </td>
    </tr>
  </table>
</form>
</body>
</html>
