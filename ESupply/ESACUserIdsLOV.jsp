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
 % File			: ESACUserIdsLOV.jsp
 % sub-module	: AccessControl
 % module		: esupply
 %
 % This is used to display the UserIds based on the Operating User's AccessType
 % 
 % author		: Madhu. P
 % date			: 5-09-2001
--%>
<%@ page import= "com.foursoft.esupply.common.bean.ESupplyGlobalParameters,
                com.foursoft.esupply.common.exception.FoursoftException,
				org.apache.log4j.Logger,
                com.foursoft.esupply.common.java.ErrorMessage,
				com.foursoft.esupply.delegate.UserRoleDelegate,
				java.util.ArrayList, com.foursoft.esupply.common.java.LOVListHandler,
				com.qms.setup.ejb.sls.SetUpSession,com.qms.setup.ejb.sls.SetUpSessionHome,javax.naming.InitialContext,
				java.util.Iterator,java.util.ResourceBundle,javax.servlet.jsp.jstl.fmt.LocalizationContext" %>
<jsp:useBean id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/>

<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>
<%@ taglib  uri="WEB-INF/lib/PageIterator.jar"  prefix="pageIterator"  %>

<%!
  private static Logger logger = null;
	String fileName = "ESACUserIdsLOV.jsp";
%>
<%
    logger  = Logger.getLogger(fileName);
     String  fromWhat  = request.getParameter("fromWhat"); ;
	   String language = loginbean.getUserPreferences().getLanguage();
%>
<fmt:setLocale value="${lLanguage}"/>
	<fmt:setBundle basename="Lang" var="lang" scope="application"/>
<%
	
    LOVListHandler listHandler   = null;
    ArrayList requiredAttributes = null;
    
	String  selection			=	(request.getParameter("selection")!=null)?request.getParameter("selection"):"single";
	String  locations           =   request.getParameter("terminalIds");
    String  multiple			=	"";
	String	accessType			= request.getParameter("accessType");

	if(accessType == null || accessType.equals("") )
		accessType	= loginbean.getAccessType();

	String			locationId	= "";
	
	ArrayList	currentPageList	= null;
	SetUpSession   remote       = null;
	SetUpSessionHome home       = null;
	InitialContext   initial    = null;
    
	if(selection!=null && selection.equals("") || selection==null)
	{
		selection	=	"single";
	}
	if(selection!=null && selection.equals("multiple"))
	{
		multiple	=	"multiple";
	}
	try
	{  	
		//Logger.info(fileName,"selection::"+selection);
		if(request.getParameter("pageNo")!= null && selection.equals("single"))
		{
			listHandler           = (LOVListHandler)session.getAttribute("userList");
			requiredAttributes    = listHandler.requiredAttributes; 
			if(requiredAttributes!=null)
			{
				accessType        = (String)requiredAttributes.get(0);
				selection         = (String)requiredAttributes.get(1);
				fromWhat		  = (String)requiredAttributes.get(2);
			}
		}
	}
	catch (Exception e)
	{
		listHandler = null;
	}  

	
	ArrayList		userList	= null;
	Iterator        li			= null;
	String			filterString	= "";
    String          message         = "";
    
	UserRoleDelegate userDelegate = null;

	

	locationId      = request.getParameter("locationId");
	filterString	= request.getParameter("filterString");

  if(listHandler == null)
	{
		try
		{
			userDelegate = new UserRoleDelegate();
			
			initial									=		new InitialContext();		
			if(filterString == null)
				filterString = "";

			//Logger.info(fileName, " AccessType :: "+accessType);

			if (accessType.equals("password"))
			{
				userList =(ArrayList) userDelegate.getUserIds(locationId, filterString.trim());
			}
			else if(accessType.equals("repOfficer"))
			{
				userList =(ArrayList) userDelegate.getRepOfficersIds(filterString.trim());
			}
			else if("report".equals(fromWhat))
			{
			  
			    home		=		(SetUpSessionHome)initial.lookup("SetUpSessionBean");
				remote		=		(SetUpSession)home.create();
				userList =(ArrayList)remote.getSalesPersonIds(loginbean,locations);
			  
			}
			else
			{
			//commented by subrahmanyam for enhancement 154384 on 10/04/2009
				//userList =(ArrayList) userDelegate.getUserIds(locationId, accessType, filterString.trim());
			//added by subrahmanyam for rEnhancement 154384 on 10/04/2009
			
				userList =(ArrayList) userDelegate.getUserIds(locationId, accessType, filterString.trim(),loginbean.getTerminalId(),loginbean.getAccessType());
				
			}

			 requiredAttributes	=	new ArrayList();
			requiredAttributes.add(accessType);
			requiredAttributes.add(selection);
			requiredAttributes.add(fromWhat);

			if(userList!=null && selection.equals("single"))
			{
				listHandler = new LOVListHandler(userList,Integer.parseInt(loginbean.getUserPreferences().getLovSize()),requiredAttributes);
				session.setAttribute("userList", listHandler);
			}
		}
		catch(FoursoftException exp)
		{
			String errorMessage = ((LocalizationContext)application.getAttribute("lang")).getResourceBundle().getString("100240")+exp.getErrorCode();
			ErrorMessage errMsg = null;
		}
	}	

  
	String pageNo			= request.getParameter("pageNo");
  	if(pageNo == null && selection.equals("single"))
	{
		pageNo = "1";
		listHandler.currentPageIndex = 1;
	    currentPageList= listHandler.getCurrentPageData();

	}
	else if(selection.equals("single"))
	{
		listHandler.currentPageIndex = Integer.parseInt(pageNo);
	    currentPageList= listHandler.getCurrentPageData();

	}
    else if(selection.equals("multiple"))
	{
		currentPageList = userList;
	}

	

		//String fileName = "ESACUserIdsLOV.jsp";

%>
<HTML>
<HEAD>

<TITLE><fmt:message key="100018" bundle="${lang}"/></TITLE>
<link rel="stylesheet" href="ESFoursoft_css.jsp">
<%@ include file="/ESEventHandler.jsp" %>
<SCRIPT language = "javascript" >


	function selectUserId()
	{		
		 
		var indx = document.forms[0].userIds.selectedIndex;
			uId  = document.f1.userIds.options[indx].value;
			<% if("report".equalsIgnoreCase(fromWhat)) {  %> 
				window.opener.setSalesPersonCode(document.forms[0].userIds);
			
			<%}
		      else if(!accessType.equals("repOfficer")){%>
		window.opener.document.forms[0].userId.value = document.f1.userIds.options[indx].value;
		<%}else if("Quote".equalsIgnoreCase(fromWhat)){%>
    window.opener.document.forms[0].salesPersonCode.value = document.f1.userIds.options[indx].value;
    <%} 
	 else{
			 for(int i=0;i<currentPageList.size();i++)
			 {%>
				 tempUserNameArray =  '<%=currentPageList.get(i)%>'.split(',');
				 if(uId == tempUserNameArray[0])
				 {
                     window.opener.document.forms[0].repOffCode.value = tempUserNameArray[0];
					 window.opener.document.forms[0].repOffName.value = tempUserNameArray[1];
					 window.opener.document.forms[0].superDesignationId.value = tempUserNameArray[2];
				 }                    
			<% }
			}%>
			
			
		resetValues();
		window.close();
	}
	
	
	function selectFirstOption()
	{
		if(document.forms[0].userIds != null) {
			if(document.forms[0].userIds.options.length > 0) {
				document.forms[0].userIds.options[0].selected = true;
			}
			document.forms[0].userIds.focus();
		}
	}
	
	function handleEvent( type )
	{
		
		if(type=='enter') {
			
			var keycode	= (window.Event) ? window.event.which : window.event.keyCode;

			if(keycode == 13)	{ // ENTER key is pressed
				
				if(document.forms[0].userIds.selectedIndex == -1) {
					alert('<fmt:message key="100542" bundle="${lang}"/>');
				} else {
					selectUserId();
				}
			}
			if(keycode == 27) {
				window.close();
			}
		}
		
		if(type=='dblClick') {
			if(document.forms[0].userIds.selectedIndex == -1) 
			{
				var s1 ='<fmt:message key="100542" bundle="${lang}"/>';
				alert(s1);
			} else {
				selectUserId();
			}
			resetValues();
		}
	}

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

	function onEscKey(){
		if(event.keyCode == 27){
			onCancel();
		}
	}

	var closeWindow = 'true';

	function setVar()
	{
		closeWindow = 'false';
	}

	function onEnterKey()
	{	
		if(event.keyCode == 27)
		{
			onCancel();
		}
	}

	function toKillSession()
	{
		if(closeWindow == 'true' && isAttributeRemoved=='false')
		{
			window.location.href="ESupplyRemoveAttribute.jsp?valueList=userList";
		}
	}

	function resetValues()
	{
		isAttributeRemoved  = 'true';
		document.forms[0].action="ESupplyRemoveAttribute.jsp?valueList=userList";
		document.forms[0].submit();   
	}
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

<body class='formdata' onLoad="selectFirstOption();" onUnLoad='toKillSession()' onKeyPress="handleEvent('enter');">
<%
	String opt = null;
    
%>
<FORM NAME="f1" METHOD="POST" onKeyPress='onEscKey()' >
<%
	if(currentPageList.size() > 0)	
    {
%>
	  <table width="210" align="center" height="250">
		<tr valign="top" >
			<td height="15" >
			  <p align="center"><b><fmt:message key="100018" bundle="${lang}"/><br></b></p>
			</td>
		</tr>
		<tr valign="top">
			<TD height="192">
				<p align="center">
				<SELECT SIZE="10" NAME="userIds" class="select" <%=multiple%> onDblClick="handleEvent('dblClick')" onKeyPress="onEnterKey();handleEvent('enter');">
<%
        String		userId	= "";
		int i =0;
		String tempUserId ="";
		String tempUserName= "";
		String tempUserDesignation = "";
		String[] values      =null;
		for(int j=0;j<currentPageList.size();j++)
		{
			userId	= currentPageList.get(j).toString();
			if(accessType.equals("repOfficer")){			
			values  = userId.split(",");
			tempUserId = values[0];
			tempUserName = values[1];
			tempUserDesignation = values[2];
			userId   =  tempUserId;
			}

%>
			<OPTION VALUE="<%= userId %>" > <%= userId %>  </OPTION>
<%
		}
%>
				</SELECT>
				</p>
	<% if(selection.equals("single")) { %>			
				<table cellSpacing=0 width=95%>
					<tr  class="formdata"> 
						<td width=100% align='center'><fmt:message key="Pages" bundle="${lang}"/> :&nbsp;
							<pageIterator:PageIterator currentPageNo="<%= listHandler.currentPageIndex %>" noOfPages="<%= listHandler.getNoOfPages() %>" noOfSegments="<%= Integer.parseInt(loginbean.getUserPreferences().getSegmentSize()) %>" fileName="<%= fileName %>"/>
						</td>
					</tr>	
				</table>
 <% } %>
			</TD>
		</TR>
		
		<TR>
			<TD height="27">
			  <p align="center"><INPUT TYPE="submit" NAME="Select" VALUE='<fmt:message key="1111" bundle="${lang}"/>' onClick = "selectUserId()"  class='input'>
			  <input type="button" value='<fmt:message key="2222" bundle="${lang}"/>' name="B3"  onClick='onCancel()'  class='input'></p>
			</TD>
		</TR>
<%	}
	else 
	{
  %>
             <p align="center">
			 <br>
			 <br>
      			<SELECT SIZE="10" NAME="UserId" class="select" >
            
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