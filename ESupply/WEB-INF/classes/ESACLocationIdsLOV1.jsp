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
 % File				: ESACLocationIdsLOV.jsp
 % sub-module		: AccessControl
 % module			: esupply
 %
 % This is used to LocationId's(it can be company, project, corporate, terminal etc..)
 % Based on the user's access Type
 % 
 % author		: Madhu. P
 % date			: 5-09-2001
--%>
<%@ page import="com.foursoft.esupply.delegate.UserRoleDelegate,
                 com.foursoft.esupply.common.exception.FoursoftException,
                 com.foursoft.esupply.common.java.ErrorMessage,
				 java.util.ArrayList, com.foursoft.esupply.common.java.LOVListHandler,
				  java.util.Iterator,
                 com.foursoft.esupply.common.util.BundleFile,
				 org.apache.log4j.Logger,java.util.ResourceBundle,javax.servlet.jsp.jstl.fmt.LocalizationContext" %>
                 

<jsp:useBean id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt_rt" prefix="fmt_rt"%>
<%@ taglib  uri="WEB-INF/lib/PageIterator.jar"  prefix="pageIterator"  %>

<%!
	String fileName	= "ESACLocationIdsLOV.jsp";		  
	private static Logger logger = null;
%>
<%
		logger  = Logger.getLogger(fileName);
	    String language = loginbean.getUserPreferences().getLanguage();

    LOVListHandler listHandler   = null;
    ArrayList requiredAttributes = null;
 %>
<fmt_rt:setLocale value="<%=language%>"/>
<fmt:setBundle basename="Lang" var="lang" scope="application"/>  
<%	try
	{
		if(request.getParameter("pageNo")!= null)
		{
			listHandler           = (LOVListHandler)session.getAttribute("esacLocationIds");
			requiredAttributes    = listHandler.requiredAttributes; 
			
		}
	}
	catch (Exception e)
	{
		listHandler = null;
	}  
		
	String 			accessType 		= "";
	String 			title			= "Terminal Ids";
	String			locationIdFilter= "";
	String 			opt 			= null;
		
	ArrayList  		locationList 	= new ArrayList();
	String 			loginAccessType	= "";
	
		if(requiredAttributes==null)
		{	
			requiredAttributes = new ArrayList();
			
			accessType		= request.getParameter("accessType");
			requiredAttributes.add(accessType);

		}
		else
		{
		accessType		=	(String)requiredAttributes.get(0);
		}
	
	if(accessType.equals("ETCRM"))
	{
		title = "Customer Ids" ;
	}

	locationIdFilter= request.getParameter("locationIdFilter");
	if(locationIdFilter == null)
		locationIdFilter = "";

	loginAccessType	= loginbean.getAccessType();

	UserRoleDelegate userDelegate = null;

	if(listHandler == null)
	{
		try
		{
			userDelegate = new UserRoleDelegate();

//			Logger.info(fileName, "LoginAccessType - accessType - Filter :: "+loginAccessType+" - "+accessType+" - "+ locationIdFilter);
			logger.info(fileName+ "LoginAccessType - accessType - Filter :: "+loginAccessType+" - "+accessType+" - "+ locationIdFilter);

			if(accessType.equalsIgnoreCase("password"))
			{	
				System.out.println("in password");
				try	
				{
					locationList	= userDelegate.getLocationIds(locationIdFilter.trim());
				}
				catch(Exception ex)
				{
//					Logger.error(fileName,"Error in Retrieving RoleIds ",ex);
					logger.error(fileName+"Error in Retrieving RoleIds "+ex);
				}
			}
			else if(accessType.equals(loginAccessType) )
			{
				System.out.println("in loginbean.getUserTerminalType() 114:	"+loginbean.getUserTerminalType());

				if(loginbean.getUserTerminalType() != null && loginbean.getUserTerminalType().equals("H") )
					locationList	= userDelegate.getLocationIds(loginbean.getLocationId(), loginAccessType, accessType, locationIdFilter);
				else if(loginbean.getUserTerminalType() != null && (!(loginbean.getUserTerminalType().equals("H"))) )
					locationList.add(loginbean.getLocationId() );
			}
			else{
				locationList	= userDelegate.getLocationIds(loginbean.getLocationId(), loginAccessType, accessType, locationIdFilter);
					
			}
			if(locationList!=null)
			{
				listHandler = new LOVListHandler(locationList,Integer.parseInt(loginbean.getUserPreferences().getLovSize()),requiredAttributes);
				session.setAttribute("esacLocationIds", listHandler);				
			}
		}
		catch(FoursoftException exp)
		{
			String errorMessage = ((LocalizationContext)application.getAttribute("lang")).getResourceBundle().getString("100240")+exp.getErrorCode();
		} 
		String fileName	= "ESACLocationIdsLOV.jsp";
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

	ArrayList	currentPageList	= listHandler.getCurrentPageData();
%>
<HTML>
<HEAD>
<TITLE><fmt:message key="Terminal_Ids" bundle="${lang}"/></TITLE>
<link rel="stylesheet" href="ESFoursoft_css.jsp">
<%@ include file="/ESEventHandler.jsp" %>
<SCRIPT language = "javascript" >

	function selectLocationId()
	{
		var indx = document.forms[0].locationId.selectedIndex;
		window.opener.document.forms[0].locationId.value = document.forms[0].locationId.options[indx].text;
		window.close();
	}
	
	function selectFirstOption()
	{
		if(document.forms[0].locationId != null) {
			if(document.forms[0].locationId.options.length > 0) {
				document.forms[0].locationId.options[0].selected = true;
			}
			document.forms[0].locationId.focus();
		}
	}
	
	function handleEvent( type )
	{	
		if(type=='enter') 
		{			
			var keycode	= (window.Event) ? window.event.which : window.event.keyCode;
			if(keycode == 13)	
			{ // ENTER key is pressed
				
				if(document.forms[0].locationId.selectedIndex == -1) 
				{
					var s1 ='<fmt:message key="100542" bundle="${lang}"/>';
					alert(s1);
				} 
				else 
				{
					selectLocationId()
				}
			}
			if(keycode == 27) 
			{
				window.close();
			}
		}
		
		if(type=='dblClick') 
		{
			if(document.forms[0].locationId.selectedIndex == -1) 
			{
				var s2 ='<fmt:message key="100542" bundle="${lang}"/>';
				alert(s2);
			} 
			else 
			{
				selectLocationId()
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
			window.location.href="ESupplyRemoveAttribute.jsp?valueList=esacLocationIds";
		}
	}

	function resetValues()
	{
		isAttributeRemoved  = 'true';
		document.forms[0].action="ESupplyRemoveAttribute.jsp?valueList=esacLocationIds";
		document.forms[0].submit();   
	}
///// Added for LOV Paging /////
	
</SCRIPT>
<% 
    if (!FoursoftWebConfig.MODULE.equals(FoursoftWebConfig.ETRANS))
    {
       response.setHeader("Cache-Control","no-cache"); // HTTP 1.1
       response.setHeader("Pragma","no-cache"); // HTTP 1.0
       response.setDateHeader ("Expires", -1); // Prevents caching at the proxy server
    } 
%>
</HEAD>

<body class='formdata' onLoad="selectFirstOption();" onUnLoad='toKillSession();'  onKeyPress="handleEvent('enter');">

<FORM NAME="f1" METHOD="POST" >
<%
	if(currentPageList.size() > 0) { %>
		<table width="210" align="center" height="250">

			<tr valign="top" >
				<td height="15" >
				  <p align="center"><b><fmt:message key="Terminal_Ids" bundle="${lang}"/><br></b></p>
				</td>
			</tr>
			<tr valign="top">

				<TD height="192">
					<p align="center">
					<SELECT SIZE="10" NAME="locationId" class="select" onDblClick="handleEvent('dblClick')" onKeyPress="handleEvent('enter');">
<%
					String		roleLocationId	= "";
					for(int j=0;j<currentPageList.size();j++)
					{
						roleLocationId	= currentPageList.get(j).toString();
%>
						<OPTION VALUE="<%= roleLocationId %>"> <%= roleLocationId %>  </OPTION>
<%					}	%>
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
				  <p align="center"><INPUT TYPE="submit" NAME="Select" VALUE='<fmt:message key="1111" bundle="${lang}"/>' onClick = "selectLocationId()"  class='input'>
				  <INPUT TYPE=BUTTON NAME="Cancel" VALUE='<fmt:message key="2222" bundle="${lang}"/>' onClick='onCancel()' class='input'></p>
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