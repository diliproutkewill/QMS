<%--
 % 
 % Copyright (c) 1999-2001 by FourSoft, Pvt Ltd. All Rights Reserved.
 % This software is the proprietary information of FourSoft, Pvt Ltd.
 % Use is subject to license terms.
 %
 % esupply - v 1.x 
 --%>
<%--
 % ESACRolePermissions.jsp
 % sub-module	: AccessControl
 % module		: esupply
 %
 % This is the UI for selecting the transaction Page, which displays modulewise
 % 
 % author		: Madhu. P
 % date			: 5-09-2001
 % Modified History
 % Madhusudhana rao. P		-- 28-01-2002
 %		Fixed the problem of taking spaces in RoleId.. It filters the spaces in RoleId  
--%>
<%@ page   import="java.util.ArrayList, 
					java.util.Iterator,
					com.foursoft.esupply.common.java.*,
					com.foursoft.esupply.accesscontrol.java.TxDetailVOB,
					com.foursoft.esupply.accesscontrol.java.ETransUserAccessConfig,
					com.foursoft.esupply.accesscontrol.java.UserAccessConfig,
					com.foursoft.esupply.accesscontrol.util.UserAccessUtility,
					com.foursoft.esupply.accesscontrol.java.RoleModel,
					org.apache.log4j.Logger,
					java.util.Hashtable,java.util.ResourceBundle,javax.servlet.jsp.jstl.fmt.LocalizationContext" %>
<jsp:useBean id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/>		   		   
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt_rt" prefix="fmt_rt"%>

<%!
     private static Logger logger = null;
	  public final static String   FILE_NAME = "ESACRolePermissions.jsp";
	String fileName	= "ESACRolePermissions.jsp";			
%>
<%
	    logger  = Logger.getLogger(FILE_NAME); 
	   String language = loginbean.getUserPreferences().getLanguage();
%>	
<fmt_rt:setLocale value="<%=language%>"/>
	<fmt:setBundle basename="Lang" var="lang" scope="application"/>
<%
	//System.out.println();
	
    
	CachedTransactions	transList	=	(CachedTransactions) CacheManager.getCache("transactions-list");
	
	Hashtable			txIdDescription	= transList.getTxList();

//	Logger.info(fileName,"txIdDescription : "+txIdDescription);
System.out.println("ROLE PERMISSIONS begins");
	String	readOnlyRoleId	= "";
	String	ETRANS	=	ETransUserAccessConfig.ETRANS;
	
	String action	= null;
	action	= request.getParameter("action");
	String lable=((LocalizationContext)application.getAttribute("lang")).getResourceBundle().getString("100257");
	//System.out.println(" action = "+action);
	
	if(action == null){
		action	= "Add";
	lable=((LocalizationContext)application.getAttribute("lang")).getResourceBundle().getString("100257");
	}
	if(action.equalsIgnoreCase("Modify") )
	{
		readOnlyRoleId = "readonly";
		lable=((LocalizationContext)application.getAttribute("lang")).getResourceBundle().getString("100199");
	}		
	
	String 	modules[]	= null;
	String 	modes[]		= null;

	RoleModel	roleModel	= (RoleModel) session.getAttribute("roleModel");
	
	Hashtable currentPermissions	= convertIntoHashTable(roleModel.getRolePermissions() );
//	Logger.info(fileName,"currentPermissions "+currentPermissions);
	ArrayList txList = (ArrayList) request.getAttribute("modulewise_transactions_list");
//	Logger.info(fileName,"txList "+txList);
	
	int 	currentIndex		= 0;
	int 	currentModeIndex	= 0;
	
	String 	actionPage		= "ESACRoleRegistrationController.jsp";
	
	// Get from request as put by controller
	currentIndex			= ((Integer) request.getAttribute("currentIndex")).intValue();
	currentModeIndex		= ((Integer) request.getAttribute("currentModeIndex")).intValue();
	// Get from session stash
	modules					= (String[]) session.getAttribute("module");
	modes					= (String[]) session.getAttribute("mode");
	
	//System.out.println(" currentIndex 	  = "+currentIndex);
	//System.out.println(" currentModeIndex = "+currentModeIndex);
	
	//System.out.println(" modules.length = "+modules.length);
	
	int	currentShipmentMode	=	-1;

	
	String	locationIdLeble	= UserAccessUtility.getLocationLable( roleModel.getRoleLevel() );
	
	String	currentModule	=	UserAccessUtility.getRoleModule(Integer.parseInt( modules[currentIndex])  );
	String	currentMode		=	"";	
	
	//System.out.println("  currentModule = "+currentModule);
	
	if(currentModule.equals(ETRANS) && modes!=null && modes.length > 0 && currentModeIndex < modes.length) {
		currentMode		=	modes[ currentModeIndex ];
	}
	
	int	modezNumber	=	ShipmentMode.getShipmentMode( currentMode );
	
	//System.out.println("  currentMode = "+currentMode);
	//System.out.println("  modezNumber = "+modezNumber);
	
	String	modeLabel = "";
	
	if(currentMode!=null && currentMode.length() > 0) {
		modeLabel = " : "+ currentMode;
	}

	String	operationLabel = UserAccessConfig.getModuleLabel( currentModule );

	if(operationLabel.equalsIgnoreCase("ETRANS"))
		operationLabel = "QuoteShop";
	
%>

<%--
 % this will get the current page from the request and get the current module 
 % to which you need to disply the previllages
 % it will get the corresponding previllages and displayed to the user
--%>
<%	
//    ArrayList txList	= new ArrayList();
//	txList				= (ArrayList)request.getAttribute("modulewise_transactions_list");
%>
<html>
<head>
<title><fmt:message key="100256" bundle="${lang}"/></title>
<link rel="stylesheet" href="ESFoursoft_css.jsp">
<%@ include file="/ESEventHandler.jsp" %>
<script language="JavaScript" >
	function stringFilter (input) 
	{
		s = input.value;
		filteredValues = "'";     // Characters stripped out
		var i;
		var returnString = "";
		for (i = 0; i < s.length; i++) 
		{  // Search through string and append to unfiltered values to returnString.
			var c = s.charAt(i);
			if (filteredValues.indexOf(c) == -1) returnString += c.toUpperCase();
		}
		input.value = returnString;
	}
	function stringFilterNoSpaces(input) 
	{
		s = input.value;
		filteredValues = "'";     // Characters stripped out
		var i;
		var returnString = "";
		for (i = 0; i < s.length; i++) 
		{  // Search through string and append to unfiltered values to returnString.
			var c = s.charAt(i);
			if(c != ' ')
			{
				if (filteredValues.indexOf(c)) 
					returnString += c.toUpperCase();
			}
		}
		input.value = returnString;
	}	

	function checkAllDiagonal(obj)
	{
		var txId	= obj.name;
		var flag	= obj.checked
		for(i=0; i < document.forms[0].elements.length; i++)
		{
				document.forms[0].elements[i].checked = flag;

		}
	}

	function checkAll(obj)
	{
		var txId	= obj.name;
		var flag	= obj.checked
		for(i=0; i < document.forms[0].elements.length; i++)
		{
			if(document.forms[0].elements[i].name == txId)
			{
				document.forms[0].elements[i].checked = flag;
			}
		}
	}

	function validate()
	{
		var flag	= false;
		
		for(i=0; i < document.forms[0].elements.length; i++)
		{
			if(document.forms[0].elements[i].checked == true)
			{
				flag=true;
				break;
			}
		}
		
		if(!flag)
		alert('<fmt:message key="100105" bundle="${lang}"/>');
		
		return flag;
	}

	function checkPerticular(type, obj)
	{
		var val		= "R";
		var flag	= obj.checked

		if(type == "read")
		{
			val = "R";
		}
		else if(type == "write")
		{
			val = "W";
		}
		else if(type == "delete")
		{
			val = "D";
		}
		else if(type == "invalidate")
		{
			val = "I";
		}
		for(i=0; i < document.forms[0].elements.length; i++)
		{
			if(document.forms[0].elements[i].value == val)
			{
				document.forms[0].elements[i].checked = flag;
			}
		}
	}
	// Modified For New Access Control

	function resetCheckBoxes() {
		for(var i=0; i < document.forms[0].elements.length; i++) 
        {
			if(document.forms[0].elements[i].name == "mode") 
            {
				document.forms[0].elements[i].checked = false;
				document.forms[0].elements[i].disabled = true;
			}
		}
	}
</script>
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
<form method="POST" action="<%= actionPage %>" name="form1" onSubmit="return validate();" onReset="resetCheckBoxes();">

<table width="760" border="0" cellspacing="0" cellpadding="0">
 <tr valign="top" bgcolor="#FFFFFF"> 
   <td>

	<table width="100%" class='formlabel' border="0">
	    <tr>
		  <td class="formlabel"><%=lable%> &nbsp;&nbsp; 
		   [<%= operationLabel %><%=modeLabel %>]
		  </td>
		  <td class="formlabel" align=right><%=loginbean.generateUniqueId(fileName,action)%></td>
		</tr>
	</table>

    <br>
		
	<table width="100%" border="0" cellspacing="1" cellpadding="1">

	          <tr class="formheader" align="center"  height="25"> 
	            <td width="52%" valign="middle"><fmt:message key="100258" bundle="${lang}"/></td>
	            <td width="12%" valign="middle"><fmt:message key="100259" bundle="${lang}"/> </td>
	            <td width="12%" valign="middle"><fmt:message key="100260" bundle="${lang}"/> </td>
	            <td width="12%" valign="middle"><fmt:message key="100261" bundle="${lang}"/> </td>
	            <td width="12%" valign="middle"><fmt:message key="100627" bundle="${lang}"/></td>
				 <td width="12%" valign="middle"><fmt:message key="100262" bundle="${lang}"/></td>
	          </tr>
<%
		String _txId = "";

        Iterator txIterator = txList.iterator();
		TxDetailVOB	txDetailVOB = null;
		
		int	lastModeFound	=	modezNumber;
		int x = 0;		
		while(txIterator.hasNext())
		{
			if(x==0) {
				//System.out.println(" IN Loop ");
				x++;
			}
			
			txDetailVOB	= (TxDetailVOB) txIterator.next();
			//@@Modified by kameswari for the WPBN issue-66143
			if(!("TAXES".equalsIgnoreCase((String)txIdDescription.get(txDetailVOB.txId)))&&!("VENDOR REGISTRATION"  .equalsIgnoreCase((String)txIdDescription.get(txDetailVOB.txId)))&&!("GENERAL RATES".equalsIgnoreCase((String)txIdDescription.get(txDetailVOB.txId)))&&!("MANAGEMENT REPORTS".equalsIgnoreCase((String)txIdDescription.get(txDetailVOB.txId)))&&!("VENDOR CONTRACTS".equalsIgnoreCase((String)txIdDescription.get(txDetailVOB.txId)))&&!("MANUFACTURER MASTER".equalsIgnoreCase((String)txIdDescription.get(txDetailVOB.txId)))&&!("STANDARD CHARGES" .equalsIgnoreCase((String)txIdDescription.get(txDetailVOB.txId))))  
			{
			 
			int	shipmentMode	=	txDetailVOB.shipmentMode;

			//System.out.println("   shipmentMode = "+shipmentMode+"  lastModeFound ="+lastModeFound);
                
			//if(currentModule.equals(ETRANS) && shipmentMode != lastModeFound) {
				//System.out.println("   shipmentMode = "+shipmentMode);
				//System.out.println("   lastModeFound = "+lastModeFound);
				//break;
			//}
			
			//if(currentModule.equals(ETRANS) && modes!=null && modes.length > 0) {
			//	currentMode		=	modes[ currentModeIndex ];
			//}

			String readCheck		= "";
			String writeCheck 		= "";
			String deleteCheck 		= "";
			String invalidateCheck 	= "";

			int accessLevel = 0;
			try
			{
				accessLevel = ((Integer) currentPermissions.get(txDetailVOB.txId) ).intValue();
			}
			catch(Exception e)
			{
				accessLevel = 0;
			}
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
          <tr class="formdata" align="center"> 
            <td width="52%" align="left" valign="middle"><%= txIdDescription.get(txDetailVOB.txId) %>
				</td>
			
            <td width="12%" valign="middle">
<%			if( txDetailVOB.accessLevel == 1 || txDetailVOB.accessLevel == 3 || txDetailVOB.accessLevel == 5 || txDetailVOB.accessLevel == 7 || txDetailVOB.accessLevel == 9 || txDetailVOB.accessLevel == 11 || txDetailVOB.accessLevel == 13 || txDetailVOB.accessLevel == 15) {
%>              <input type="checkbox" name="<%= txDetailVOB.txId %>" <%=readCheck %> value="R" >
<%			} // if Read
%>			</td>
			
            <td width="12%" valign="middle"> 
<%			if( txDetailVOB.accessLevel == 2 || txDetailVOB.accessLevel == 3 || txDetailVOB.accessLevel == 6  || txDetailVOB.accessLevel == 7 || txDetailVOB.accessLevel == 10 || txDetailVOB.accessLevel == 11 || txDetailVOB.accessLevel == 14 || txDetailVOB.accessLevel == 15) {
%>              <input type="checkbox" name="<%= txDetailVOB.txId %>" <%=writeCheck %> value="W" >
<%			} // if Write
%>			</td>
			
            <td width="12%" valign="middle">
<%			if( txDetailVOB.accessLevel == 4 || txDetailVOB.accessLevel == 6 || txDetailVOB.accessLevel == 7 || txDetailVOB.accessLevel == 12 || txDetailVOB.accessLevel == 13 || txDetailVOB.accessLevel == 14 || txDetailVOB.accessLevel == 15) {
%>              <input type="checkbox" name="<%= txDetailVOB.txId %>" <%=deleteCheck %> value="D" >
<%			} // if Delete
%>		    </td>
			<td width="12%" valign="middle">
<%			if( txDetailVOB.accessLevel == 8 || txDetailVOB.accessLevel == 9 || txDetailVOB.accessLevel == 10 || txDetailVOB.accessLevel == 11 || txDetailVOB.accessLevel == 12 || txDetailVOB.accessLevel == 13 || txDetailVOB.accessLevel == 14 || txDetailVOB.accessLevel == 15) {
%>              <input type="checkbox" name="<%= txDetailVOB.txId %>" <%=invalidateCheck%> value="I" >
<%			} // if Delete
%>		    </td>
			
            <td width="12%" valign="middle"> 
              <input type="checkbox" name="<%= txDetailVOB.txId %>" value="ALL" onClick="return checkAll(this)">
            </td>
			
          </tr>
<%
		}
		}// while
%> 
	          <tr class="formheader" align="center" height="25"> 
	            <td width="52%" valign="middle"><fmt:message key="100262" bundle="${lang}"/></td>
	            <td width="12%" valign="middle"> 
	              <input type="checkbox" name="readCheck" value="ALL" onClick="return checkPerticular('read', this)">
	            </td>
	            <td width="12%" valign="middle"> 
	              <input type="checkbox" name="writeCheck" value="ALL" onClick="return checkPerticular('write', this)">
	            </td>
	            <td width="12%" valign="middle"> 
	              <input type="checkbox" name="deleteCheck" value="ALL" onClick="return checkPerticular('delete', this)">
	            </td>
				 <td width="12%" valign="middle"> 
	              <input type="checkbox" name="invalidateCheck" value="ALL" onClick="return checkPerticular('invalidate', this)">
	            </td>
				<td width="12%" valign="middle"> 
	              <input type="checkbox" name="allCheck" value="ALL" onClick="return checkAllDiagonal(this)">
	            </td>
	            <td width="12%" valign="middle"> 
	            </td>
	          </tr>

    </table>
	
	<table width="100%" border="0" cellspacing="1" cellpadding="4">
		<tr  align="right">
	
			<input type=hidden name=action value="<%= action %>" >
	       	<input type="hidden" name="currentIndex" value="<%= currentIndex %>" >
			<input type="hidden" name="currentModeIndex" value="<%= currentModeIndex %>" >
			<input type="hidden" name="screen_name"  value="role_permissions">
			<input type="hidden" name="action"  value="add">
			<input type="hidden" name="shipmentmode"  value="">
       
		  <td colspan="2"> 
<% if(currentIndex <= modules.length) {		 %>
        	<input type="submit" name="Submit"  value='<fmt:message key="3334" bundle="${lang}"/>' class="input">
<%	} else {		%>
        	<input type="submit" name="Submit"  value='<fmt:message key="6666" bundle="${lang}"/>' class="input">
<%	}
		


	//System.out.println("ROLE PERMISSIONS Ends");
	//System.out.println();
%>

<!-- New Access Control-->
<input type="reset" name="Submit" value='<fmt:message key="8890" bundle="${lang}"/>' class="input">
	      </td>
		</tr>

	</table>

  </td>
 </tr>
</table>
</form>
</body>
</html>

<%!
	public Hashtable convertIntoHashTable(ArrayList rolePermissions)
	{
		Hashtable ht = new Hashtable(rolePermissions.size() );

		TxDetailVOB	txDetail	= null;
		for(int i=0; i < rolePermissions.size(); i++)
		{
			txDetail	= (TxDetailVOB)rolePermissions.get(i);
			ht.put(txDetail.txId, new Integer(txDetail.accessLevel) );
		}
		return ht;
		
	}

%>