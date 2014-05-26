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
 % ESACRoleIdLocationIdLOV.jsp
 % sub-module	: AccessControl
 % module		: esupply
 %
 % This is used to display RoleIds, which also retrieves the role's Location 
 % 
 % author		: Madhu. P
 % date			: 5-09-2001
--%>

<%@ page import= "com.foursoft.esupply.common.bean.ESupplyGlobalParameters,
				com.foursoft.esupply.accesscontrol.util.UserAccessUtility,
				com.foursoft.esupply.accesscontrol.java.RoleModel,
				org.apache.log4j.Logger,com.foursoft.esupply.common.java.LOVListHandler,
				com.foursoft.esupply.delegate.UserRoleDelegate,
                com.foursoft.esupply.common.exception.FoursoftException,
                com.foursoft.esupply.common.java.ErrorMessage,
							java.util.ArrayList,java.util.ResourceBundle,
				java.util.Iterator,javax.servlet.jsp.jstl.fmt.LocalizationContext" %>
<jsp:useBean id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/>

<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt_rt" prefix="fmt_rt"%>
<%@ taglib  uri="WEB-INF/lib/PageIterator.jar"  prefix="pageIterator"  %>

<%!
  private static Logger logger = null;  
	String fileName	= "ESACRoleIdLocationIdLOV.jsp";
			
%>
<%
    logger  = Logger.getLogger(fileName);
	   String language = loginbean.getUserPreferences().getLanguage();
%>
<fmt_rt:setLocale value="<%=language%>"/>
	<fmt:setBundle basename="Lang" var="lang" scope="application"/>
<%
	

    LOVListHandler listHandler   = null;
    ArrayList requiredAttributes = null;	
  %>		

	<%        
	try
	{  
		if(request.getParameter("pageNo")!= null)
		{
			listHandler           = (LOVListHandler)session.getAttribute("roleIdLocationId");
			requiredAttributes    = listHandler.requiredAttributes; 
		}
	}
	catch (Exception e)
	{
		listHandler = null;
	}  

	
	ArrayList  	roleList	= null;

	String	roleId			= "";
	String	roleLocationId	= "";	
	String	locationId		= "";
	String	accessType		= "";
	String	roleModule		= "";
	String	roleModuleLabel	= "";

	if(listHandler == null)
	{
		roleId			= request.getParameter("roleId");
		roleLocationId	= request.getParameter("roleLocationId");
		locationId      = request.getParameter("locationId");
		accessType		= request.getParameter("accessType");

		roleModule		= request.getParameter("roleModule");
		//requiredAttributes.add(roleId);
		//requiredAttributes.add(roleLocationId);
		//requiredAttributes.add(locationId);
		//requiredAttributes.add(accessType);

		//requiredAttributes.add(roleModule);					

		UserRoleDelegate userDelegate = null;
		try
		{
			userDelegate = new UserRoleDelegate();

			if(roleModule!=null && roleModule.length() > 0)
			{
				int	iRoleModule	=	0;

				try 
				{
					iRoleModule	=	Integer.parseInt( roleModule );
				}
				catch(NumberFormatException nfe) 
				{
				}

				if(iRoleModule > 0) 
				{
					roleModuleLabel	=	UserAccessUtility.convertIntoModuleString( iRoleModule );
				}

			}
			//Logger.info(fileName,"Role Id :  Location Id : accessType : User's Location - User's Access Type :: "+roleLocationId+"/"+roleId+" - "+locationId+" - "+accessType+" - "+loginbean.getLocationId()+" - "+ loginbean.getAccessType());

			roleList	= userDelegate.getRoleIdLocation(roleId, roleLocationId, locationId, accessType, loginbean.getLocationId(), loginbean.getAccessType(), roleModule);
			//roleList	= (ArrayList) remote.getRoleIdLocation(roleId, roleLocationId, locationId, accessType, loginbean.getLocationId(), loginbean.getAccessType(), roleModule);

			if(roleList!=null)
			{
				listHandler = new LOVListHandler(roleList,Integer.parseInt(loginbean.getUserPreferences().getLovSize()),requiredAttributes);
				session.setAttribute("roleIdLocationId", listHandler);
			}

		}
		catch(FoursoftException exp)
		{
			String errorMessage = ((LocalizationContext)application.getAttribute("lang")).getResourceBundle().getString("100240")+exp.getErrorCode();
		}    
		//String fileName	= "ESACRoleLocationIdLOV.jsp";
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

	try
	{
		if(requiredAttributes!=null)
		{
			roleId			= (String)requiredAttributes.get(0);
			roleLocationId	= (String)requiredAttributes.get(1);
			locationId      = (String)requiredAttributes.get(2);
			accessType		= (String)requiredAttributes.get(3);
			roleModule		= (String)requiredAttributes.get(4);  
		}		

	}
	catch(Exception ex)
	{
	  //Logger.error(fileName,"ETransLOVPRQIds.jsp : " +ex);
    logger.error(fileName+"ETransLOVPRQIds.jsp : " +ex);
	}
%>
<HTML>
<HEAD>
<TITLE><fmt:message key="100014" bundle="${lang}"/></TITLE>
<link rel="stylesheet" href="ESFoursoft_css.jsp">
<%@ include file="/ESEventHandler.jsp" %>
<SCRIPT language = "javascript" >

	function selectRoleId()
	{
		var index = document.forms[0].roleIds.selectedIndex;

		var targetForm	=	window.opener.document.forms[0];
		
		targetForm.roleId.value				= RoleIds[ index ];
		targetForm.roleLocationId.value 	= RoleLocationIds[ index ];
		targetForm.roleDescription.value	= RoleDescriptions[ index ];
		targetForm.roleModule.value 		= "<%= roleModuleLabel %>";
		resetValues();
		window.close();
	}
	

	function handleEvent( type )
	{
		if(type=='enter') {
			
			var keycode	= (window.Event) ? window.event.which : window.event.keyCode;

			if(keycode == 13)	{ // ENTER key is pressed
				
				if(document.forms[0].roleIds.selectedIndex == -1) {
					alert('<fmt:message key="100542" bundle="${lang}"/>');
				} else {
					selectRoleId();
				}
			}
			if(keycode == 27) {
				window.close();
			}
		}
		
		if(type=='dblClick') {
			if(document.forms[0].roleIds.selectedIndex == -1) {
				alert('<fmt:message key="100542" bundle="${lang}"/>');
			} else {
				selectRoleId();
			}
		}
	}

	function openWin()
	{
		var index	=	document.forms[0].roleIds.selectedIndex;

		if(index < 0) {
			alert('<fmt:message key="100548" bundle="${lang}"/>');
			return false;
		}
		
		var rolId			= RoleIds[ index ];
		var roleLocationId	= RoleLocationIds[ index ];
		var accessType		= '<%=accessType%>';
		
		var	width	=	800;
		var	height	=	600;
		var	top		=	(screen.availHeight - height) / 2;
		var	left	=	(screen.availWidth  - width)  / 2;
		
		var myUrl	= "ESACRoleRegistrationController.jsp?action=VIEW&screen_name=user_role_entry&roleId="+rolId+"&locationId="+roleLocationId+"&accessType="+accessType+"&isPopUp=yes";
		var Bars	= 'directories=no, location=no,menubar=no,status=yes,titlebar=no';
		var Options	= 'scrollbars=yes, width='+width+', height='+height+', top='+top+', left='+left+', resizable=no';
		
		var Features	= Bars+', '+Options;
		var Win=open(myUrl,'MyDoc',Features);
		
		if (!Win.opener) 
			Win.opener = self;
		if (Win.focus != null) 
			Win.focus();
		return false;
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
			window.location.href="ESupplyRemoveAttribute.jsp?valueList=roleIdLocationId";
		}
	}

	function resetValues()
	{
		isAttributeRemoved  = 'true';
		document.forms[0].action="ESupplyRemoveAttribute.jsp?valueList=roleIdLocationId";
		document.forms[0].submit();   
	}
///// Added for LOV Paging /////

	var	RoleIds			=	new Array();
	var	RoleLocationIds	=	new Array();
	var	RoleDescriptions=	new Array();
	
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

<body class='formdata' onUnLoad='toKillSession();' onKeyPress="handleEvent('enter');">

<%
	String opt = null;
%>
	<FORM NAME="f1" METHOD="POST" onKeyPress='onEscKey()'>
<%
//System.out.println("currentPageList.size()           "+currentPageList.size());
	if(currentPageList.size() > 0) 
	{ 
%>
	  <table width="210" align="center" height="250">
		<tr valign="top" >
			<td height="15" >
			  <p align="center"><b><fmt:message key="100015" bundle="${lang}"/><br></b></p>
			</td>
		</tr>
		<tr valign="top">

			<TD>
				<p align="center">
				<SELECT SIZE="10" NAME="roleIds" class="select" onDblClick="handleEvent('dblClick')" onKeyPress="handleEvent('enter');">
<%
				RoleModel roleModel = null;
				int	index = 0;
				for(int j=0;j<currentPageList.size();j++)
				{
					roleModel	= (RoleModel)currentPageList.get(j);
					
					String	selectedStr = "";
					
					if(index==0) 
					{
						selectedStr = "selected";
					}

%>
					<OPTION VALUE="<%= roleModel.getRoleId() %>#<%= roleModel.getLocationId() %>" <%=selectedStr%> > <%= roleModel.getRoleId() %>[<%= roleModel.getLocationId() %>]  </OPTION>
					<script language="javascript">
						RoleIds[<%=index%>]			=	"<%= roleModel.getRoleId()%>";
						RoleLocationIds[<%=index%>]	=	"<%= roleModel.getLocationId()%>";
						RoleDescriptions[<%=index%>]=	"<%= roleModel.getDescription()%>";
					</script>
<%
					index++;
				}
%>
				</SELECT>
				</p>
				<table cellSpacing=0 width=95%>
					<tr  class="formdata"> 
						<td width=100% align='center'><fmt:message key="100016" bundle="${lang}"/> &nbsp;
							<pageIterator:PageIterator currentPageNo="<%= listHandler.currentPageIndex %>" noOfPages="<%= listHandler.getNoOfPages() %>" noOfSegments="<%= Integer.parseInt(loginbean.getUserPreferences().getSegmentSize()) %>" fileName="<%= fileName %>"/>
						</td>
					</tr>	
				</table>
			</TD>
		</TR>		
		<TR>
			<TD>
			  <p align="center">
			  <input type="button"  value='<fmt:message key="100254" bundle="${lang}"/>' name="roleBtn" onClick = 'openWin()' class="input"><br><br>
			  <input type="submit" name="Select" value='<fmt:message key="1111" bundle="${lang}"/>' onClick = "selectRoleId()"  class='input'>
			  <input type="submit" name="Select" value='<fmt:message key="2222" bundle="${lang}"/>' onClick='onCancel()' class='input'>
			  </p>
			</TD>
		</TR>

<%
	}
	else
	{%>
		<TR>
			<TD>

		<center><fmt:message key="100255" bundle="${lang}"/></center>
		<p align='center'><INPUT TYPE=BUTTON VALUE='<fmt:message key="2222" bundle="${lang}"/>' onClick='javascript:window.close()' class='input'>
		    </TD>
		</TR>
		<%
	}
%>
	  </table>
	</FORM>
</BODY>
</HTML>

