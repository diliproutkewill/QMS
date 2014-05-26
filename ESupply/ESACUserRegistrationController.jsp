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
 % File Name	: ESACUserRegistrationController.jsp
 % sub-module	: AccessControl
 % module		: esupply
 %
 % This the Controller file, which tracks the Current Module for displaying Transactions and
 % capturing the selected transactions from the request.
 % 
 % author		: Madhu. P
 % date			: 5-09-2001
--%>
<%@ page import="com.foursoft.esupply.common.bean.ESupplyGlobalParameters,
				com.foursoft.esupply.accesscontrol.java.UserModel, 
				com.foursoft.esupply.accesscontrol.java.RoleModel, 
				org.apache.log4j.Logger, 
				com.foursoft.esupply.common.java.KeyValue, 
				com.foursoft.esupply.common.java.ErrorMessage, 
				com.foursoft.esupply.common.java.FoursoftWebConfig, 
				com.foursoft.esupply.common.exception.FoursoftException, 
				com.foursoft.esupply.accesscontrol.exception.NotExistsException, 
				com.foursoft.esupply.delegate.UserRoleDelegate, 
				java.util.ArrayList, 
				com.foursoft.esupply.accesscontrol.exception.DataIntegrityViolationException, 
				java.util.Iterator, java.util.Hashtable,
				javax.mail.Session, javax.mail.Transport, 
				javax.mail.Message,
				javax.mail.internet.MimeMessage, 
				javax.naming.InitialContext,
javax.mail.internet.InternetAddress,java.util.ResourceBundle,javax.servlet.jsp.jstl.fmt.LocalizationContext"%>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt_rt" prefix="fmt_rt"%>
<jsp:useBean id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/>

<%!
  private static Logger logger = null;
	String fileName = "ESACUserRegistrationController.jsp";
			
%>
<%
    logger  = Logger.getLogger(fileName);
	  String  langu = loginbean.getUserPreferences().getLanguage();
	  
%>
<fmt_rt:setLocale value="<%=langu%>"/>
	<fmt:setBundle basename="Lang" var="lang" scope="application"/>
<%
	
	String		action		= null;
	String		screen_name	= null;
	String[]	modules		= null;
	//String		actionPage	= "ESupplyMessagePage.jsp";
    String		actionPage	= null;

	String		accessType			= "";
	String		userModule			= "";
	String     newSalesPerson       = null;
	ErrorMessage	errMsg	= null;
	
	action				= request.getParameter("action");
	screen_name			= request.getParameter("screen_name");
	if(screen_name == null)
	{
		screen_name = "";
	}

    UserRoleDelegate userDelegate = null;
    
	try
    {
        userDelegate = new UserRoleDelegate();
    }
    catch(FoursoftException exp)
    {
        String errorMessage = ((LocalizationContext)application.getAttribute("lang")).getResourceBundle().getString("100240")+exp.getErrorCode();
        if (action.equalsIgnoreCase("add"))
            errMsg = new ErrorMessage(errorMessage, "ESACUserRegistrationEntry.jsp",exp.getErrorCode(),exp.getComponentDetails());
        else if (action.equalsIgnoreCase("ViewAll"))
            errMsg = new ErrorMessage(errorMessage,"ESACRoleRegistrationController.jsp?action="+action,exp.getErrorCode(),exp.getComponentDetails());
        else
            errMsg = new ErrorMessage(errorMessage, "ESACUserRoleEnterId.jsp?action="+action+"&UIName=User",exp.getErrorCode(),exp.getComponentDetails());
        request.setAttribute("errorMessage", errMsg); 
%>
	<jsp:forward page="ESupplyMessagePage.jsp" />
<%
    }
	//Logger.info(fileName,"screen_name : action :: "+screen_name+" : "+action);

	UserModel	userModel	= null;
	RoleModel	roleModel	= null;

	userModel	= (UserModel) session.getAttribute("userModel");
	roleModel	= (RoleModel) session.getAttribute("roleModel");
	
	if(userModel == null)
	{
		userModel	= new UserModel();
		session.setAttribute("userModel", userModel);
	}
%>
<%

	if(screen_name.equals("user_registration_entry") )
	{
		String		locationId			= "";
		String errorMessage = "";
    
		if(action.equals("add") )
		{
			ArrayList	roleList			= new ArrayList();	

			locationId			= request.getParameter("locationId");
			accessType			= request.getParameter("accessType");

			
			userModel.setLocationId(locationId.toUpperCase());
			userModel.setUserLevel(accessType);
			//System.out.println(loginbean.getCompanyId());
			userModel.setCompanyId(loginbean.getCompanyId());
			actionPage = "ESACUserRegistrationAdd.jsp";
			try
			{
				//userDelegate.isLocationIdExists(locationId,accessType);
				//userDelegate.IsLocIdExists(locationId,loginbean.getAccessType(),accessType,loginbean.getLocationId());
          userDelegate.IsLocIdExists(locationId,loginbean.getAccessType(),accessType,loginbean.getLocationId());
							

			}
			catch(FoursoftException fe)
			{
			
			//Logger.error(fileName,"NotExists Exception ",fe);
      logger.error(fileName+"NotExists Exception "+fe);
			 if (fe.getMessage()!= null)
			 {
				errorMessage =((LocalizationContext)application.getAttribute("lang")).getResourceBundle().getString("100623")+"\n"+" '"+locationId+"'";
				errMsg = new ErrorMessage(errorMessage, "ESACUserRegistrationEntry.jsp",fe.getErrorCode(),fe.getComponentDetails());
				ArrayList keyValueList = new ArrayList();
				keyValueList.add(new KeyValue("UIName","Role") );
				keyValueList.add(new KeyValue("action",action) );
				errMsg.setKeyValueList(keyValueList);
				request.setAttribute("errorMessage", errMsg);
				actionPage = "ESupplyMessagePage.jsp";

			 }
			 }

		}
	}
	else if(screen_name.equals("user_registration") )
	{
		accessType		= request.getParameter("accessType");
		userModel.setAccessLevel(loginbean.getAccessType());
		userModel.setUserLevel(accessType);
		//System.out.println("userModel.getUserLevel"+userModel.getUserLevel());
		if(action.equalsIgnoreCase("add") )
		{		
      String[]				listValues				=	 null;	
			String[]				listValuesShip				=	 null;	
			String[]				listValuesAlloted			=	 null;	
			ArrayList                 listValuesList  =	 new ArrayList();	
			ArrayList                  listValuesShipList				=	 new ArrayList();	
			ArrayList                  listValuesAllotedList				=	 new ArrayList();
			userModel.setUserId( request.getParameter("userId").trim() );
			userModel.setLocationId( request.getParameter("locationId").toUpperCase() );
			userModel.setPassword( request.getParameter("password") );
			userModel.setCompanyId( request.getParameter("companyId") );
			userModel.setUserName( request.getParameter("userName") );
			//@@Added by Kameswari for the WPBN isssue-61303
			userModel.setPhoneNo(request.getParameter("phoneNo"));
			userModel.setFaxNo(request.getParameter("faxNo"));
			userModel.setMobileNo(request.getParameter("mobileNo"));
			//@@WPBN isssue-61303
			userModel.setEmpId(request.getParameter("empId"));
			userModel.setEMailId(request.getParameter("eMailId"));
			userModel.setDepartment(request.getParameter("department") );
			userModel.setRoleId(request.getParameter("roleId") );
			userModel.setRoleLocationId(request.getParameter("roleLocationId") );
			//System.out.println("*************************************"+request.getParameter("repOffCode"));
			listValues=request.getParameterValues("repOffCode");//added by VLAKSHMI for WPBN issue 167659 (CR) on 22/04/2009
			listValuesAlloted=request.getParameterValues("allotedTime");//added by VLAKSHMI for WPBN issue 167659 (CR) on 22/04/2009
			userModel.setDesignationId(request.getParameter("designation") );
//@@ Added by subrahmanyam for the Enhancement 167668 on 28/04/09
			userModel.setCustAddr1(request.getParameter("custaddr1"));
			userModel.setCustAddr2(request.getParameter("custaddr2"));
			userModel.setCustAddr3(request.getParameter("custaddr3"));
//@@ Ended by subrahmanyam for the Enhancement 167668 on 28/04/09

			listValuesShip=request.getParameterValues("checkboxName");//added by VLAKSHMI for WPBN issue 167659 (CR) on 22/04/2009
			//added by VLAKSHMI for WPBN issue 167659 (CR) on 22/04/2009
			for(int i=0;i<listValues.length;i++)
			{ if(listValues[i]!=null && !"".equalsIgnoreCase(listValues[i].trim()))
				{
				listValuesList.add(listValues[i]);
				listValuesShipList.add(listValuesShip[i]);
				listValuesAllotedList.add(listValuesAlloted[i]);
				}
        	}
		 userModel.setRepOfficersCode2(listValuesList);
        userModel.setShipmentModeCode2(listValuesShipList);
		userModel.setAllotedTime2(listValuesAllotedList);	
			userModel	=	processOptionalRoles( userModel, request );

			Hashtable userPreferences = new Hashtable(7);
			String dateFormat	= request.getParameter("dateFormat");
			
			String dimension	= request.getParameter("dimension");
			String volume		= dimension.equalsIgnoreCase("METRIC")?"CUBIC_METER":"CUBIC_FEET";//request.getParameter("volume");
			String weight		= dimension.equalsIgnoreCase("METRIC")?"KG":"LB";//request.getParameter("weight");
			String lovSize	= (String)request.getParameter("lovPagingSize");
			String segmentSize	= (String)request.getParameter("segmentSize");
			String language	= (String)request.getParameter("language");

			userPreferences.put("date_format",dateFormat);
			userPreferences.put("volume",volume);
			userPreferences.put("dimension",dimension);
			userPreferences.put("weight",weight);
			userPreferences.put("lovPageSize",lovSize);
			userPreferences.put("segmentSize",segmentSize);
			userPreferences.put("language",language);

			userModel.setUserPreferences(userPreferences);

			boolean	isMaxLimitExceeded = false;
			String errorMessage = "";
			try
			{
				   
				//Logger.info(fileName, "User Model : "+userModel);		
				
				Hashtable ht = userModel.validateUserModel();		   
				if(ht.size() > 0)
				{
					java.util.Enumeration	enum1 = ht.keys();
					String param	= "";
					errorMessage	= ((LocalizationContext)application.getAttribute("lang")).getResourceBundle().getString("100058");
					while(enum1.hasMoreElements() )
					{
						param	= (String)enum1.nextElement();
						errorMessage	+= ( param + " : "+(String) ht.get(param)+"\n" );
					}
				}
				else
				{		   


					//if(!remote.isMaxLimitNotExceeded())
					if(!userDelegate.isMaxLimitNotExceeded())
					{
						isMaxLimitExceeded = true;
						errorMessage =	((LocalizationContext)application.getAttribute("lang")).getResourceBundle().getString("100059")+"\n"+((LocalizationContext)application.getAttribute("lang")).getResourceBundle().getString("100060");

						//Logger.info(fileName, "Maximum Users Limit was exceeded ");
            logger.info(fileName+ "Maximum Users Limit was exceeded ");
					}   
					else
					{
						userDelegate.registerUser(userModel, loginbean.getUserId(), loginbean.getLocationId(),request.getParameter("eMailNotification"));
						//remote.registerUser(userModel, loginbean.getUserId(), loginbean.getLocationId());
						
						errorMessage =	((LocalizationContext)application.getAttribute("lang")).getResourceBundle().getString("100061")+ "\n"+((LocalizationContext)application.getAttribute("lang")).getResourceBundle().getString("100276")+userModel.getUserId() +"\n"+((LocalizationContext)application.getAttribute("lang")).getResourceBundle().getString("100268")+userModel.getLocationId();
					}
				}
				errMsg = new ErrorMessage(errorMessage, "ESACUserRegistrationEntry.jsp");
			}
			catch(FoursoftException fe )
			{
				//Logger.warning(fileName, "User Already Exists ", fe);
        logger.warn(fileName+ "User Already Exists "+ fe);
        //errorMessage = "Error: The User '"+userModel.getUserId()+"' already exists for '"+userModel.getLocationId()+"'.";
				if (fe.getMessage()!= null)
				{
					//System.out.println("fgfghffhfhfhfhf ::::::: "+(LocalizationContext)application.getAttribute("lang"));
										
					//errorMessage = ((LocalizationContext)application.getAttribute("lang")).getResourceBundle().getString("100073");
					if("100628".equals(fe.getMessage()))
						errorMessage ="  Employee Id Already Exists."; 
					else if("100629".equals(fe.getMessage()))
						errorMessage =" Designation Id is not Exist."; 
					else if("100630".equals(fe.getMessage()))
						errorMessage =" Reporting Officer's Code  not exists ."; 
					else if("100075".equals(fe.getMessage()))				          //@@Added by Kiran.v for the WPBN issue -261423 on 26/07/2011
						errorMessage = " User Id Already Exists .";
					else
						errorMessage = fe.getMessage();

				}
				else
					errorMessage = ((LocalizationContext)application.getAttribute("lang")).getResourceBundle().getString("100057");
				//errorMessage = fe.getMessage();
				//errorMessage = fe.getErrorCode();				
				errMsg = new ErrorMessage(errorMessage, "ESACUserRegistrationEntry.jsp",fe.getErrorCode(),fe.getComponentDetails());
			}
			finally
			{				
				request.setAttribute("errorMessage", errMsg); 
			}
			
			actionPage = "ESupplyMessagePage.jsp";
		}
		else if(action.equalsIgnoreCase("modify") )
		{
			String errorMessage = "";
      String[]				listValues				=	 null;	
			String[]				listValuesShip				=	 null;	
			String[]				listValuesAlloted			=	 null;	
			ArrayList                 listValuesList  =	 new ArrayList();	
			ArrayList                  listValuesShipList				=	 new ArrayList();	
			ArrayList                  listValuesAllotedList				=	 new ArrayList();
			userModel.setUserId( request.getParameter("userId") );
			userModel.setLocationId( request.getParameter("locationId").toUpperCase() );
			userModel.setCompanyId( request.getParameter("companyId") );
			userModel.setUserName( request.getParameter("userName") );
			//@@Added by Kameswari for the WPBN isssue-61303
			userModel.setPhoneNo(request.getParameter("phoneNo"));
			userModel.setFaxNo(request.getParameter("faxNo"));
			userModel.setMobileNo(request.getParameter("mobileNo"));
			//@@WPBN isssue-61303
			userModel.setEmpId(request.getParameter("empId"));
			userModel.setEMailId(request.getParameter("eMailId"));
			userModel.setDepartment(request.getParameter("department") );
			userModel.setRoleId(request.getParameter("roleId") );
			userModel.setRoleLocationId(request.getParameter("roleLocationId") );
			//userModel.setRepOfficersCode(request.getParameter("repOffCode") );//added by rk
			//userModel.setAllotedTime(request.getParameter("allotedTime") );
			userModel.setDesignationId(request.getParameter("designation") );
//@@ Added by subrahmanyam for the Enhancement 167668 on 28/04/09
			userModel.setCustAddr1(request.getParameter("custaddr1"));
			userModel.setCustAddr2(request.getParameter("custaddr2"));
			userModel.setCustAddr3(request.getParameter("custaddr3"));

//@@ Ended by subrahmanyam for the Enhancement 167668 on 28/04/09

            listValues=request.getParameterValues("repOffCode");//added by VLAKSHMI for WPBN issue 167659 (CR) on 22/04/2009
				listValuesAlloted=request.getParameterValues("allotedTime");//added by VLAKSHMI for WPBN issue 167659 (CR) on 22/04/2009
		
			String	versionNo	=	request.getParameter("version");
			int		version		=	0;
		listValuesShip=request.getParameterValues("checkboxName");//added by VLAKSHMI for WPBN issue 167659 (CR) on 22/04/2009
			//added by VLAKSHMI for WPBN issue 167659 (CR) on 22/04/2009
			for(int i=0;i<listValues.length;i++)
			{ if(listValues[i]!=null && !"".equalsIgnoreCase(listValues[i].trim()))
				{
				listValuesList.add(listValues[i]);
				listValuesShipList.add(listValuesShip[i]);
				listValuesAllotedList.add(listValuesAlloted[i]);
				}
        	}
		 userModel.setRepOfficersCode2(listValuesList);
        userModel.setShipmentModeCode2(listValuesShipList);
		userModel.setAllotedTime2(listValuesAllotedList);	
			try 
			{
				version = Integer.parseInt( versionNo );

			} 
			catch(NumberFormatException nfe){}

			//System.out.println("  USER VERSION in Controller = "+ version);
				
			userModel.setVersion( version );
			userModel.setModifiedBy( loginbean.getUserId() );
			userModel.setModifiedDate( new java.sql.Timestamp( (new java.util.Date()).getTime() ) );

			userModel	=	processOptionalRoles( userModel, request );
			
			Hashtable userPreferences = new Hashtable(5);
			String dateFormat	= request.getParameter("dateFormat");
			String dimension	= request.getParameter("dimension");
			String volume		= dimension.equalsIgnoreCase("METRIC")?"CUBIC_METER":"CUBIC_FEET";//request.getParameter("volume");
			String weight		= dimension.equalsIgnoreCase("METRIC")?"KG":"LB";//request.getParameter("weight");
			String lovSize	= request.getParameter("lovPagingSize");
			String segmentSize	= request.getParameter("segmentSize");
			String language	= request.getParameter("language");

			userPreferences.put("date_format",dateFormat);
			userPreferences.put("volume",volume);
			userPreferences.put("dimension",dimension);
			userPreferences.put("weight",weight);
			userPreferences.put("lovPageSize",lovSize);
			userPreferences.put("segmentSize",segmentSize);
			userPreferences.put("language",language);

			userModel.setUserPreferences(userPreferences);
			
	try
			{
				userDelegate.updateUserInformation(userModel, loginbean.getUserId(), loginbean.getLocationId() );
				//remote.updateUserInformation(userModel, loginbean.getUserId(), loginbean.getLocationId() );

				errorMessage =	((LocalizationContext)application.getAttribute("lang")).getResourceBundle().getString("100062")+"\n"+
								((LocalizationContext)application.getAttribute("lang")).getResourceBundle().getString("100276")+userModel.getUserId() + "\n"+
								((LocalizationContext)application.getAttribute("lang")).getResourceBundle().getString("100024") + userModel.getRoleId() + "\n"+
								((LocalizationContext)application.getAttribute("lang")).getResourceBundle().getString("100268")+ userModel.getLocationId();
                errMsg = new ErrorMessage(errorMessage, "ESACUserRoleEnterId.jsp");               
			}
			catch(FoursoftException ne)
			{
				if (ne.getMessage()!= null)
				{
					if("100629".equals(ne.getMessage()))
						errorMessage ="The Designation Id is not Exist."; 
					else if("100630".equals(ne.getMessage()))
						errorMessage ="The Reporting Officer's Code is not exit .";
					else
					errorMessage = ((LocalizationContext)application.getAttribute("lang")).getResourceBundle().getString(ne.getMessage());
				}
				else
					errorMessage = ((LocalizationContext)application.getAttribute("lang")).getResourceBundle().getString("100066");
				errMsg = new ErrorMessage(errorMessage, "ESACUserRoleEnterId.jsp",ne.getErrorCode(),ne.getComponentDetails());				
			}
			finally
			{
				ArrayList keyVlaueList = new ArrayList();
				keyVlaueList.add(new KeyValue("UIName", "User") );
				keyVlaueList.add(new KeyValue("action", "Modify") );
				errMsg.setKeyValueList(keyVlaueList);
				request.setAttribute("errorMessage", errMsg); 		
				actionPage = "ESupplyMessagePage.jsp";
			}
	
		}
		else if(action.equalsIgnoreCase("delete") )
		{
			
			String userId		= request.getParameter("userId");
			String locationId	= request.getParameter("locationId").toUpperCase();
			String errorMessage = "";
			boolean deleteFlag = false;
//@@ Added by subrahmanyam for the Enhancement 154384 on 27/01/09
			
			 newSalesPerson=request.getParameter("NewSalesPerson").toUpperCase();//subrahmanyam
			
			String newCreatedBy=null;//subrahmanyam
//@@ Ended by subrahmanyam for the Enhancement 154384 on 27/01/09
			
			
			
			try
			{
//@@ Added by subrahmanyam for the Enhancement 154384 on 27/01/09
				if(newSalesPerson!=null&&newSalesPerson.trim().length()>0)
				{
					String delSalesPerson   = userDelegate.getDelSalesPerson(userId);//to get the SalesPerson for the deleting EmpId
					//@@Commented & Added by subrahmanyam for the pbn id:208582 on 17-Jun-10
					//userDelegate.updateSalesPerson(newSalesPerson,delSalesPerson;
					accessType	=	request.getParameter("accessType");
					userDelegate.updateSalesPerson(newSalesPerson,delSalesPerson,accessType);
			//@@Ended by subrahmanyam for the pbn id:208582 on 17-Jun-10
				}
//@@ Ended by subrahmanyam for the Enhancement 154384 on 27/01/09
				deleteFlag		= userDelegate.removeUser(userId, locationId, loginbean.getUserId(), loginbean.getLocationId());
				//deleteFlag		= remote.removeUser(userId, locationId, loginbean.getUserId(), loginbean.getLocationId());
				
				errorMessage =	((LocalizationContext)application.getAttribute("lang")).getResourceBundle().getString("100063")+"\n"+
								((LocalizationContext)application.getAttribute("lang")).getResourceBundle().getString("100276")+userId + "\n"+
								((LocalizationContext)application.getAttribute("lang")).getResourceBundle().getString("100268")+ locationId;
				errMsg = new ErrorMessage(errorMessage, "ESACUserRoleEnterId.jsp");				
			}
			catch(FoursoftException ne)
			{
				//Logger.warning(fileName, "User does '"+userId+"' not exists", ne);
        logger.warn(fileName+ "User does '"+userId+"' not exists"+ ne);
				//errorMessage = ne.toString();
				//errorMessage = ne.getMessage();
				errorMessage = ((LocalizationContext)application.getAttribute("lang")).getResourceBundle().getString("100067");
				errMsg = new ErrorMessage(errorMessage, "ESACUserRoleEnterId.jsp",ne.getErrorCode(),ne.getComponentDetails());				
			}
			finally
			{
				ArrayList keyVlaueList = new ArrayList();
				keyVlaueList.add(new KeyValue("UIName", "User") );
				keyVlaueList.add(new KeyValue("action", "Delete") );
				errMsg.setKeyValueList(keyVlaueList);
				request.setAttribute("errorMessage", errMsg); 		
				actionPage = "ESupplyMessagePage.jsp";
			}
		}
	}
	else if(screen_name.equals("user_role_entry") )
	{
		String userId		= request.getParameter("userId");
		String locationId	= request.getParameter("locationId").toUpperCase();
		accessType			= request.getParameter("accessType");
		String errorMessage = null;
		boolean status = false;
		userModel.setUserLevel(accessType);
		//System.out.println("userModel.getUserLevel"+userModel.getUserLevel());
		try
		{
		//userModel	=	getUserModel(locationId, userId, accessType);
			//UserRoleDelegate userDelegate = new UserRoleDelegate();

			//status = userDelegate.IsLocIdExists(locationId,loginbean.getAccessType(),accessType,loginbean.getLocationId()); 

					status = userDelegate.IsLocIdExists(locationId,loginbean.getAccessType(),accessType,loginbean.getLocationId());
				
				//userDelegate.isLocationIdExists(locationId,accessType);
			
			userModel	= (UserModel)userDelegate.getUserInformation(locationId, userId);

		if(userModel == null)
		{
			actionPage = "ESupplyMessagePage.jsp";
			
			if(status)
			{
		    errorMessage = ((LocalizationContext)application.getAttribute("lang")).getResourceBundle().getString("100064")+"'"+userId+"' "+((LocalizationContext)application.getAttribute("lang")).getResourceBundle().getString("100065")+" '"+locationId+"' "+((LocalizationContext)application.getAttribute("lang")).getResourceBundle().getString("100081")+" '"+accessType+"'";
			}
			else
			{
			errorMessage =((LocalizationContext)application.getAttribute("lang")).getResourceBundle().getString("100623")+"\n"+" "+locationId;
			}
			errMsg = new ErrorMessage(errorMessage, "ESACUserRoleEnterId.jsp");
			ArrayList keyVlaueList = new ArrayList();
			keyVlaueList.add(new KeyValue("UIName", "User") );
			keyVlaueList.add(new KeyValue("action", action) );
			errMsg.setKeyValueList(keyVlaueList);
			request.setAttribute("errorMessage", errMsg); 		
		}
		else
		{
			session.setAttribute("userModel", userModel);
			//session.setAttribute("roleModel", roleModel);
			actionPage = "ESACUserRegistrationAdd.jsp";
		}

		}
		catch(FoursoftException fe)
			{
				
				actionPage = "ESupplyMessagePage.jsp";
				//Logger.warning(fileName, fe.getMessage());
        logger.warn(fileName+ fe.getMessage());
				if (fe.getMessage()!= null)
				{
				if(status)
				{
				errorMessage = ((LocalizationContext)application.getAttribute("lang")).getResourceBundle().getString("100064")+"'"+userId+"' "+((LocalizationContext)application.getAttribute("lang")).getResourceBundle().getString("100065")+" '"+locationId+"'"; //"+bundle.getBundle().getString("100081")+" '"+accessType+"'";
				}
				else
				{
				errorMessage =((LocalizationContext)application.getAttribute("lang")).getResourceBundle().getString("100623")+"\n"+" '"+locationId+"'";
				}
				errMsg = new ErrorMessage(errorMessage, "ESACUserRoleEnterId.jsp",fe.getErrorCode(),fe.getComponentDetails());
				ArrayList keyVlaueList = new ArrayList();
				keyVlaueList.add(new KeyValue("UIName", "User") );
				keyVlaueList.add(new KeyValue("action", action) );
				errMsg.setKeyValueList(keyVlaueList);
				request.setAttribute("errorMessage", errMsg); 	
			}
	}
		
		String	roleId			=	null;
		String	roleLocationId	=	null;

		if(userModel!=null) {
			roleId			=	userModel.getRoleId();
			roleLocationId	=	userModel.getRoleLocationId();
		}

		if(roleId != null && roleLocationId!=null && roleId.length() > 0 && roleLocationId.length() > 0) {
			roleModel	=	getRoleModel( roleId, roleLocationId, loginbean );
		}

		session.setAttribute("roleModel", roleModel);
		
	}
	else
	{
		if(action.equalsIgnoreCase("viewAll") )
		{
			ArrayList						userList			= new ArrayList();
			try
			{	   //Modified by Rakesh for Issue:                    on 08-03-2011
					if("A".equalsIgnoreCase(loginbean.getUserTerminalType())){	 
					userList=userDelegate.getUserViewAllInformation2(loginbean.getLocationId());
					}else{
					userList	= userDelegate.getUserViewAllInformation();
					}
					//userList	= remote.getUserViewAllInformation();
			}
			catch(FoursoftException ex)
			{
				//Logger.error(fileName,"Error in getting User's Information : "+ex.toString());
        logger.error(fileName+"Error in getting User's Information : "+ex.toString());
			}	
			request.setAttribute("usersList", userList);
			actionPage="ESACUsersViewAll.jsp";		
		}
	}
    
%>

<jsp:forward page="<%= actionPage %>" />

<%!
/*
	private UserModel getUserModel(String locationId, String userId, String accessType) throws FoursoftException,NotExistsException
	{
		
		UserModel						userModel	= null;
		System.out.println("accessType====================="+accessType);
		try
		{	
            UserRoleDelegate userDelegate = new UserRoleDelegate();

			userDelegate.isLocationIdExists(locationId,accessType);
			
			userModel	= (UserModel)userDelegate.getUserInformation(locationId, userId);
			
		}
		catch(Exception ex)
		{
			Logger.error(fileName,"Error in Retrieving the UserInformation",ex.getMessage());
			
			throw FoursoftException.getException(ex,fileName,"getUserModel");
		}
		return userModel;
	}
	*/

	private RoleModel getRoleModel(String roleId, String roleLocationId, ESupplyGlobalParameters loginbean)
	{		
		RoleModel						roleModel	= null;
		
		try
		{
			UserRoleDelegate userDelegate = new UserRoleDelegate();
            
			
			roleModel	= (RoleModel) userDelegate.getRoleModel(roleId, roleLocationId);
			
			//roleModel	= (RoleModel) remote.getRoleModel(roleId, roleLocationId);
		}
		catch(Exception ex)
		{
			//Logger.error(fileName,"Error in Retrieving the RoleInformation",ex.getMessage());
      logger.error(fileName+"Error in Retrieving the RoleInformation"+ex.getMessage());
		}
		return roleModel;
	}
	
	private UserModel	processOptionalRoles( UserModel userModel, HttpServletRequest request) {

		String[]	saWarehouses	= null;
		String[]	saRoles			= null;

		ArrayList	alWareHouses	=	new ArrayList(10);
		ArrayList	alRoles			=	new ArrayList(10);
		
		saWarehouses	= request.getParameterValues("selWarehouses");
		saRoles			= request.getParameterValues("selRoles");
	
		if(saWarehouses!=null && saRoles!=null && saWarehouses.length==saRoles.length)
		{
		    //System.out.println("In controller 0:");

			for(int x=0; x < saWarehouses.length; x++) {
				alWareHouses.add( x, saWarehouses[x] );
				alRoles.add( x, saRoles[x] );
			}
		}
		
	 	userModel.setOtherRoleIds( alRoles );
		userModel.setOtherRoleLocationIds( alWareHouses );

		return userModel;
	}
	
		
%>