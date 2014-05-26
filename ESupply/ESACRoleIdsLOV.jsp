<%--
 % 
 % Copyright (c) 1999-2001 by FourSoft, Pvt Ltd. All Rights Reserved.
 % This software is the proprietary information of FourSoft, Pvt Ltd.
 % Use is subject to license terms.
 %
 % esupply - v 1.x 
 %
 --%
<%--
 % ESACRoleIdsLOV.jsp
 % sub-module	: AccessControl
 % module		: esupply
 %
 % This is used to display the RoleIds based on the access type of the user is doing the Operation
 % 
 % author		: Madhu. P
 % date			: 5-09-2001
--%>

<%@ page import="org.apache.log4j.Logger,
				com.foursoft.esupply.delegate.UserRoleDelegate,
                com.foursoft.esupply.common.exception.FoursoftException,
                com.foursoft.esupply.common.java.ErrorMessage,
				java.util.ArrayList, com.foursoft.esupply.common.java.LOVListHandler,
				java.util.Iterator,java.util.ResourceBundle,javax.servlet.jsp.jstl.fmt.LocalizationContext" %>
<jsp:useBean id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/>
<%@ taglib  uri="WEB-INF/lib/PageIterator.jar"  prefix="pageIterator"  %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt_rt" prefix="fmt_rt"%>
<%!
	String fileName = "ESACRoleIdsLOV.jsp";
		
%>
<%
	   String language = loginbean.getUserPreferences().getLanguage();
%>
<%
	
    LOVListHandler listHandler   = null;
    ArrayList requiredAttributes = null;
	ArrayList currentPageList = null;
%>
	<fmt_rt:setLocale value="<%=language%>"/>
	<fmt:setBundle basename="Lang" var="lang" scope="application"/>

	<%try 
	{  
       //rb = ResourceBundle.getBundle("lang");
		//System.out.println("asdfasdfasdfsdf  "+ ((LocalizationContext)application.getAttribute("lang")).getResourceBundle().getString("100240"));
		if(request.getParameter("pageNo")!= null)
		{
			listHandler           = (LOVListHandler)session.getAttribute("roleList");
			requiredAttributes    = listHandler.requiredAttributes; 
		}
	}
	catch (Exception e)
	{
		listHandler = null;
	}  

    UserRoleDelegate userDelegate = null;
    ArrayList  roleList = null;
    Iterator li = null;
    String	accessType		= "";
    String	locationId		= "";
    String  roleFilter		= "";
    String 	message			= "";
	String description      ="";
    

    if(listHandler == null)
	{
		try
		{
			userDelegate = new UserRoleDelegate();
			accessType	= request.getParameter("accessType");
			if(accessType == null || accessType.equals("") )
				accessType	= loginbean.getAccessType();

			locationId      = request.getParameter("locationId");

			roleFilter      = request.getParameter("filterString");
             description    = request.getParameter("Description");
			if(roleFilter == null)
				roleFilter = "";
		
			//Logger.info(fileName , " LocationId - AccessType - RoleId Filter:: "+locationId + " - "+accessType+"-"+roleFilter);

			/*if(accessType.equals(loginbean.getAccessType())  && !locationId.equals(loginbean.getLocationId()) )
			{
				message = bundle.getBundle().getString("100017");
			}
			else
			{  */
				roleList	= userDelegate.getRoleIds(locationId, accessType, roleFilter.trim());
				//roleList	= (ArrayList) remote.getRoleIds(locationId, accessType, roleFilter.trim());
		//}

			if(roleList!=null)
			{
				listHandler  = new LOVListHandler(roleList,Integer.parseInt(loginbean.getUserPreferences().getLovSize()),requiredAttributes);
				session.setAttribute("roleList", listHandler);
			}
		}
		catch(FoursoftException exp)
		{
			String errorMessage = ((LocalizationContext)application.getAttribute("lang")).getResourceBundle().getString("100240")+exp.getErrorCode();//rb.getString("100240")+exp.getErrorCode();
			ErrorMessage errMsg = new ErrorMessage(errorMessage, "ESACRoleRegistration.jsp",exp.getErrorCode(),exp.getComponentDetails());
			request.setAttribute("errorMessage", errMsg); 
		}

	}
		String pageNo			= request.getParameter("pageNo");

		if(pageNo == null)
		{
			pageNo = "1";
			listHandler.currentPageIndex = 1;
		}
		else
		{
			listHandler.currentPageIndex = Integer.parseInt(pageNo);
		}

		currentPageList	= listHandler.getCurrentPageData();
		String fileName = "ESACRoleIdsLOV.jsp";
	
%>
<HTML>
<HEAD>
<TITLE><fmt:message key="Role_Ids" bundle="${lang}"/></TITLE>
<link rel="stylesheet" href="ESFoursoft_css.jsp">
<%@ include file="/ESEventHandler.jsp" %>

<script language = "javascript" >
	function selectRoleId()
	{
		var indx = document.forms[0].roleIds.selectedIndex;
		var roleidWithDesc = document.forms[0].roleIds.options[indx].text;
		var roleid =roleidWithDesc.split('[');

		window.opener.document.forms[0].roleId.value = roleid[0];
		resetValues();
		window.close();
	}
	
	function selectFirstOption()
	{
		if(document.forms[0].roleIds != null) 
		{
			if(document.forms[0].roleIds.options.length > 0) 
			{
				document.forms[0].roleIds.options[0].selected = true;
			}
			document.forms[0].roleIds.focus();
		}
	}
	
	function handleEvent( type )
	{
		
		if(type=='enter')
		{			
			var keycode	= (window.Event) ? window.event.which : window.event.keyCode;

			if(keycode == 13)	
			{ 				
				if(document.forms[0].roleIds.selectedIndex == -1) 
				{
					alert('<fmt:message key="100542" bundle="${lang}"/>');
				} 
				else 
				{
					selectRoleId();
				}
			}
			if(keycode == 27) 
			{
				window.close();
			}
		}
		
		if(type=='dblClick') 
		{
			if(document.forms[0].roleIds.selectedIndex == -1) 
			{
				alert('<fmt:message key="100542" bundle="${lang}"/>');
			} 
			else 
			{
				selectRoleId();
			}
		}
	}

///// Added for LOV Paging /////

	function onEscKey()
	{
		if(event.keyCode == 27)
		{
			resetValues();
		}
	}

	var isAttributeRemoved  = 'false';
	var isRegistered  = 'false';
	
	function onCancel()
	{
		//removeLOVValues();
		resetValues();
		window.close();
	}

	var closeWindow = 'true';

	function setVar()
	{
		closeWindow = 'false';
	}

	function toKillSession()
	{
		if(closeWindow == 'true' && isAttributeRemoved=='false')
		{
			window.location.href="ESupplyRemoveAttribute.jsp?valueList=roleList";
		}
	}

	function resetValues()
	{
		isAttributeRemoved  = 'true';
		document.forms[0].action="ESupplyRemoveAttribute.jsp?valueList=roleList";
		document.forms[0].submit();   
	}
///// Added for LOV Paging /////
	
</SCRIPT>
<% 
   /* if (!FoursoftWebConfig.MODULE.equals(FoursoftWebConfig.EP))
    {
       response.setHeader("Cache-Control","no-cache"); // HTTP 1.1
       response.setHeader("Pragma","no-cache"); // HTTP 1.0
       response.setDateHeader ("Expires", -1); // Prevents caching at the proxy server
    }  
*/
%>

</HEAD>

<body class='formdata' onLoad='selectFirstOption();' onKeyPress="handleEvent('enter');onEscKey();" onUnLoad='toKillSession()'  >
	<FORM NAME="f1" METHOD="POST">
<%	if(currentPageList.size() > 0)	
	{ 
%>
	  <table width="210" align="center" height="250">
		<tr valign="top" >
			<td height="15" >
			  <p align="center"><b><fmt:message key="Role_Ids" bundle="${lang}"/><br></b></p>
			</td>
		</tr>
		<tr valign="top">

			<TD height="192">
				<p align="center">
				<SELECT SIZE="10" NAME="roleIds" class="select" onDblClick="handleEvent('dblClick')" onKeyPress="handleEvent('enter');">
<%
				String		roleId	= "";
				int i =0;
				for(int j=0;j<currentPageList.size();j++)
				{
					roleId	= currentPageList.get(j).toString();
%>					<OPTION VALUE="<%= roleId %>"> <%= roleId %>  </OPTION>
<%		
				} 
%>
				</SELECT>
				</p>
				<table cellSpacing=0 width=95%>
				<tr  class="formdata"> 
					<td width=100% align='center'><fmt:message key="Pages" bundle="${lang}"/> :&nbsp;
						<pageIterator:PageIterator currentPageNo="<%= listHandler.currentPageIndex %>" noOfPages="<%= listHandler.getNoOfPages() %>" noOfSegments="<%= Integer.parseInt(loginbean.getUserPreferences().getSegmentSize()) %>" fileName="<%= fileName %>"/>
					</td>
				</tr>	
			</table>
			</TD>
			
		</TR>
		
		<TR>
			<TD height="27">
			  <p align="center"><INPUT TYPE="submit" NAME="Select" VALUE='<fmt:message key="1111" bundle="${lang}"/>' onClick = "selectRoleId()"  class='input'>
			  <INPUT TYPE='submit' NAME='Select' VALUE='<fmt:message key="2222" bundle="${lang}"/>' onClick='onCancel()'  class='input'></p>
			</TD>
		</TR>
<%	}
	else 
	{
  %>     
	
             <p align="center">
			 <br>
			 <br>
      			<SELECT SIZE="10" NAME="locationId" class="select" >
            
            <option > No Data Found</option>
            </select>


          <p align='center'><INPUT TYPE=BUTTON VALUE="Close" onClick='javascript:window.close()'  class='input'>
    
  <%    
	}
  %>
	</table>
</FORM>
</BODY>
</HTML>
