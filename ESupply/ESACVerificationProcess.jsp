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
 % File			: ESACVerificationProcess.jsp
 % sub-module	: AccessControl
 % module		: esupply
 %
 % This is used to check the the user has got previllage or not for an Accessed Page
 % It takes the txId and Required Access level form the request and check with Permissions list
 % which is in session
 % 
 % author		: Madhu. P
 % date			: 5-09-2001
--%>
<%@ page import="java.util.Hashtable, 
				org.apache.log4j.Logger"%>
<jsp:useBean id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/>
<%!
  private static Logger logger = null;
	String fileName	= "ESACVerifcationProcess.jsp";
%>
<%
  logger  = Logger.getLogger(fileName);
	 
	String		url					= null;
	String		transactionId		= null;
	String		txId				= "";
	int			requiredAccessLevel	= 0; 

    String		newURL				= null;
	String		process 			= "";

	boolean		accessFlag			=  false;
	Hashtable	hashPermissions		= null;

%>
<%
	url				= request.getParameter("URL");
	transactionId	= request.getParameter("TXID");
	
	if(request.getParameter("Process") != null)
	{
		process	= request.getParameter("Process");
		session.setAttribute("Process",process);
	}
	else
	{
		session.setAttribute("Process",process);	
	}
	
	txId					= transactionId.substring(0, 5);
	requiredAccessLevel		= Integer.parseInt(transactionId.substring(5) );
	
	//Logger.info(fileName,"TX ID - Access Level -  Process : "+transactionId+"-"+requiredAccessLevel+"-"+process);
	try
	{
		if(loginbean.getUserId() == null)
		{
			request.setAttribute("ErrorMessage","Users Session Expired! Please ReLogin ");
	%>
		<jsp:forward page="ESACSessionExpired.jsp" />
	<%		
		}
	}
	catch(NullPointerException ne)
	{
	}	
%>
<%
	int accessLevel = 0;
	try
	{
		// get the access level for that txId from the PermissionsList, which is in session
		accessLevel = ((Integer)((Hashtable)session.getAttribute("accessList")).get(txId) ).intValue();
		accessFlag = false;
		
		boolean addFlag		= false;
		boolean deleteFlag	= false;
		boolean viewFlag	= false;
		boolean	invalFlag	= false;
	/*
	 * this conditional statement get the what all possible operation can do with the the User's access leve
	 *
	 */				
		switch(accessLevel)
		{
			case 15 :
			{
				addFlag		= true;
				deleteFlag	= true;
				viewFlag	= true;
				invalFlag	= true;
				break;
			}
			case 14 :
			{
				deleteFlag	= true;
				viewFlag	= true;
				invalFlag	= true;
				break;
			}
			case 13 :
			{
				addFlag		= true;
				deleteFlag	= true;
				invalFlag	= true;
				break;
			}
			case 12 :
			{
				deleteFlag	= true;
				invalFlag	= true;
				break;
			}
			case 11 :
			{
				addFlag		= true;
				viewFlag	= true;
				invalFlag	= true;
				break;
			}
			case 10 :
			{
				viewFlag	= true;
				invalFlag	= true;
				break;
			}
			case 9 :
			{
				addFlag		= true;
				invalFlag	= true;
				break;
			}
			case 8 :
			{
				invalFlag		= true;
				break;
			}
			case 7 :
			{
				addFlag		= true;
				deleteFlag	= true;
				viewFlag	= true;
				break;
			}
			case 6 :
			{
				addFlag		= true;
				deleteFlag	= true;
				break;
			}
			case 5 :
			{
				deleteFlag	= true;
				viewFlag	= true;
				break;
			}
			case 4 :
			{
				deleteFlag	= true;
				break;
			}
			case 3 :
			{
				addFlag		= true;
				viewFlag	= true;
				break;
			}
			case 2 :
			{
				addFlag		= true;
				break;
			}
			case 1 :
			{
				viewFlag	= true;
				break;
			}
			default :
			{
				break;
			}
		}
		//System.out.println("requiredAccessLevelrequiredAccessLevel 444444444444444444444444:: "+requiredAccessLevel);
		if(requiredAccessLevel == 2)
		{
			accessFlag = addFlag;
		}
		else if(requiredAccessLevel == 1)
		{
			accessFlag = viewFlag;
		}
		else if(requiredAccessLevel == 4)
		{
			accessFlag = deleteFlag;
		}
		else if(requiredAccessLevel == 8)
		{
			accessFlag	=	invalFlag;
		}
		if(accessFlag)
		{
			newURL = url;
		}
		else
		{
			newURL = "ESupplyAccessDenied.jsp";	
		}
	}
	catch(Exception exp)
	{
		//Logger.error(fileName,"Access Denied ",exp);
    logger.error(fileName+"Access Denied "+exp);
		newURL = "ESupplyAccessDenied.jsp";
	}

	//Logger.warning(fileName,loginbean.getUserId()+"/"+loginbean.getLocationId()+" "+" accessed : "+txId+"/"+request.getParameter("Operation")+" at "+new java.util.Date());		
  logger.warn(fileName+loginbean.getUserId()+"/"+loginbean.getLocationId()+" "+" accessed : "+txId+"/"+request.getParameter("Operation")+" at "+new java.util.Date());		
%>
	<jsp:forward page="<%= newURL %>" />	
		