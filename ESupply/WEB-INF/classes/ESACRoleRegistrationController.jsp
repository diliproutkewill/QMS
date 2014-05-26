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
 % File Name	: ESACRoleRegistrationController.jsp
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
				com.foursoft.esupply.common.java.*,
				com.foursoft.esupply.accesscontrol.java.UserAccessConfig,
				com.foursoft.esupply.accesscontrol.util.UserAccessUtility,
				com.foursoft.esupply.accesscontrol.java.UserAccessConfigFactory,
				com.foursoft.esupply.accesscontrol.java.RoleModel,
				com.foursoft.esupply.accesscontrol.java.TxDetailVOB,
				com.foursoft.esupply.common.util.ArraySupport,
				org.apache.log4j.Logger,
				com.foursoft.esupply.common.java.KeyValue,
				com.foursoft.esupply.common.java.ErrorMessage,
				com.foursoft.esupply.common.exception.FoursoftException,
				com.foursoft.esupply.delegate.UserRoleDelegate,
				com.foursoft.esupply.accesscontrol.exception.DataIntegrityViolationException,
				java.util.ArrayList,
				java.util.Iterator,
				java.util.Hashtable,java.util.ResourceBundle,javax.servlet.jsp.jstl.fmt.LocalizationContext" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt_rt" prefix="fmt_rt"%>
<jsp:useBean id="_rolePermissionControllerJBean" scope="session" class="com.foursoft.esupply.accesscontrol.bean.RolePermissionControllerJBean"/>
<jsp:useBean id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/>		   

<%!
  private static Logger logger = null;
	String FINAL_PERMISSIONS_PAGE	= "ESACRolePermissionsConfirm.jsp";
	String PERMISSIONS_VIEW_PAGE	= "ESACRolePermissions.jsp";
	String fileName					= "ESACRoleRegistrationController.jsp";
	
%>
<%
     logger  = Logger.getLogger(fileName);
	   String language = loginbean.getUserPreferences().getLanguage();
%>
<fmt_rt:setLocale value="<%=language%>"/>
	<fmt:setBundle basename="Lang" var="lang" scope="application"/> 
<%
    
	String		action		= null;
	String		screen_name	= null;
	String[]	modules		= null;
	String[]	modes		= null;
	String		actionPage	= null;
	Hashtable	currentPermissions	= null; // which is used to store current previllages
	ArrayList	txList		= null;	// list to be displayed
	UserAccessConfig accessConfig	= UserAccessConfigFactory.getUserAccessConfig();
	String	ETRANS	=	accessConfig.getETrans();
	
	action				= request.getParameter("action");
	screen_name			= request.getParameter("screen_name");

    UserRoleDelegate userDelegate = null;
    try
    {
        userDelegate = new UserRoleDelegate();
    }
    catch(FoursoftException exp)
    {
        String errorMessage = "Error in Lookup "+exp.getErrorCode();
        ErrorMessage errMsg = null;
        if (action.equalsIgnoreCase("add"))
            errMsg = new ErrorMessage(errorMessage, "ESACRoleRegistration.jsp",exp.getErrorCode(),exp.getComponentDetails());
        else if (action.equalsIgnoreCase("ViewAll"))
            errMsg = new ErrorMessage(errorMessage,"ESACRoleRegistrationController.jsp?action="+action,exp.getErrorCode(),exp.getComponentDetails());
        else
            errMsg = new ErrorMessage(errorMessage, "ESACUserRoleEnterId.jsp?action="+action+"&UIName=Role",exp.getErrorCode(),exp.getComponentDetails());
        request.setAttribute("errorMessage", errMsg); 
%>
	<jsp:forward page="ESupplyMessagePage.jsp" />
<%
    }
    
	if(screen_name == null)
	{
		screen_name = "";
	}
	int currentIndex		= 0;	// this used to track the current page and next page to be displayed
	int	currentModeIndex	= 0;	// Initially no mode is selected but later we keep track of this
	String	currentModule	= "";
	String	currentMode		= "";

	//Logger.info(fileName,"screen_name : action :: "+screen_name+" : "+action);
	RoleModel	roleModel			= null;

	roleModel	= (RoleModel)session.getAttribute("roleModel");
	if(roleModel == null)
	{
		roleModel	= new RoleModel();
		session.setAttribute("roleModel", roleModel);
	}
	currentPermissions	= new Hashtable();

	if(screen_name.equals("role_registration") )
	{
		if(action.equals("add") )
		{
			String					errorMessage	= "";
			ErrorMessage			errMsg		= null;

			String		roleModule			= "";
			String		roleId				= "";
			String		roleDescription		= "";
			String		roleLocationId		= "";
			String		roleAccessType		= "";
			
			roleId				= request.getParameter("roleId");
			roleDescription		= request.getParameter("description");
			roleLocationId		= request.getParameter("locationId");
			roleAccessType		= request.getParameter("accessType");
			
			// The next page called will be the Role Permissions Page as we will now give permissions to the Role
			actionPage = PERMISSIONS_VIEW_PAGE;
			
			

		
			try
			{
				//System.out.println("roleLocationId="+roleLocationId+"loginbean.getAccessType()="+loginbean.getAccessType()+"roleAccessType="+roleAccessType+" loginbean.getLocationId()="+loginbean.getLocationId());
				
				/*if(loginbean.getAccessType().equals("CUSTWH"))
						userDelegate.IsLocIdExists(roleLocationId,loginbean.getAccessType(),roleAccessType,loginbean.getWareHouseId());
					else if(loginbean.getAccessType().equals("WAREHOUSE"))
						userDelegate.IsLocIdExists(roleLocationId,loginbean.getAccessType(),roleAccessType,loginbean.getCompanyId());
					else
						userDelegate.IsLocIdExists(roleLocationId,loginbean.getAccessType(),roleAccessType,loginbean.getLocationId());*/
			

					userDelegate.IsLocIdExists(roleLocationId,loginbean.getAccessType(),roleAccessType,loginbean.getLocationId());
				
			}
			catch(FoursoftException fe)
			{
			
			//Logger.error(fileName,"NotExists Exception ",fe);
      logger.error(fileName+"NotExists Exception "+fe);
			 if (fe.getMessage()!= null)
			 {
				errorMessage =((LocalizationContext)application.getAttribute("lang")).getResourceBundle().getString("100623")+"\n"+" "+roleLocationId;
				errMsg = new ErrorMessage(errorMessage, "ESACRoleRegistration.jsp",fe.getErrorCode(),fe.getComponentDetails());
				ArrayList keyValueList = new ArrayList();
				keyValueList.add(new KeyValue("UIName","Role") );
				keyValueList.add(new KeyValue("action",action) );
				errMsg.setKeyValueList(keyValueList);
				request.setAttribute("errorMessage", errMsg);
				actionPage = "ESupplyMessagePage.jsp";

			 }
			 }

			currentIndex 		= 0;	// Points to the current Module in future calls to this page
			currentModeIndex  	= -1;	// Points to the Current Shipment Mode
			
			modules = request.getParameterValues("module");	// The checkboxes of Modules
			modes	= request.getParameterValues("mode");	// The checkboxes of Shipment Modes under Etrans (if any)
				
			int moduleIndex = 0;
			
			// The moduleIndex will give the Combination of modules ported
			for(int i=0; i < modules.length; i++)
			{
				moduleIndex += Integer.parseInt(modules[i] );
			}
			
			roleModule = "" + moduleIndex;	// Convert to a String to stach in 'roleModel' obj in session

			roleModel.setRoleId( roleId );
			roleModel.setDescription( roleDescription );
			roleModel.setLocationId( roleLocationId );
			roleModel.setRoleLevel( roleAccessType );
			roleModel.setRoleModule( roleModule );

			
			// If the Role's location id and the login's location id are same, then the
			// Role Type is SPECIFIC to that Location else its GENERIC (for all locations)

			if(roleModel.getLocationId().equals(loginbean.getLocationId()) )
			{
				roleModel.setRoleType("SPECIFIC");
			}
			else
			{
				roleModel.setRoleType("GENERIC");
			}
					
			session.setAttribute("module", modules);	// Stash in session
			
			if(modes!=null && modes.length > 0) {		// If modes are there (for ETRANS)
				session.setAttribute("mode", modes);	// Stash them in the session for reuse

				session.setAttribute("hasModes",  new Boolean(true) );
			} else {
				session.setAttribute("hasModes",  new Boolean(false) );
			}
			
			// Stashed in request for next component and for tracking the modules to be configured
			request.setAttribute("currentIndex",	new Integer(currentIndex));

							
			// Find the modules index (a unique number specific to that modules defined by us)
			int moduleIndx = Integer.parseInt( modules[currentIndex] );
			
			// From the module index get the module's name
			currentModule = UserAccessUtility.getRoleModule( moduleIndx );
			
			if(modes!=null && modes.length > 0) {
				currentModeIndex = 0;	// set pointer to the first Shipment Mode
			}

			request.setAttribute("currentModeIndex", new Integer(currentModeIndex));
						
			// Get a list of available Transaction Names (which will have corresponding ids in database)
			// for the FIRST MODULE selected by the User
			if(currentModule.equals(ETRANS) && currentModeIndex > -1) {
				int	shipmentMode = ShipmentMode.getShipmentMode( modes[currentModeIndex] );

				txList	= getModuleWiseTransactions( currentModule, shipmentMode, roleModel.getRoleLevel(), loginbean );
			} else {
				txList	= getModuleWiseTransactions( currentModule, roleModel.getRoleLevel(), loginbean );
			}
					
			// Stash in request to be displayed in next component
			request.setAttribute("modulewise_transactions_list", txList );
		}

	}
	else if(screen_name.equals("role_permissions") ) 
	{
		if(action.equals("add") )
		{		
			ArrayList txListFetched = null;
			int txLevel		= 1;
			
			modules	= (String[]) session.getAttribute("module");
			modes	= (String[]) session.getAttribute("mode");
			
			currentIndex		= Integer.parseInt( request.getParameter("currentIndex") );
			currentModeIndex	= Integer.parseInt( request.getParameter("currentModeIndex") );
			
			boolean	hasModes	=	((Boolean) session.getAttribute("hasModes")).booleanValue();

			try
			{				
				//Logger.info(fileName," Module Traversing5555555555555555555  is : "+ modules[currentIndex]);

				int currentModuleIndex	= Integer.parseInt( modules[currentIndex] );
				currentModule	= UserAccessUtility.getRoleModule( currentModuleIndex );
				
				if(currentModule.equals(ETRANS) && hasModes && currentModeIndex < modes.length)
				{
					// There are modes and still some left
					int	shipmentModeId	=	ShipmentMode.getShipmentMode( modes[currentModeIndex] );
					txListFetched = userDelegate.getTransactionIds( currentModule, shipmentModeId, roleModel.getRoleLevel() );
				}
				else
				{
					txListFetched = userDelegate.getTransactionIds( currentModule, roleModel.getRoleLevel() );
					//txListFetched = remote.getTransactionIds( currentModule, roleModel.getRoleLevel() );
				}
			} 
			catch(FoursoftException e) 
			{
				//Logger.error(fileName,"Exception is :  ",e);
        logger.error(fileName+"Exception is :  "+e);
			}

			// To store the USer selected permissions for the current module transactions
			Hashtable selectedPermissions = new Hashtable();
			
			Iterator txIterator = null;
			txIterator = txListFetched.iterator();
			String txId = "";

			while(txIterator.hasNext())		//	TXNs
			{
				txId = (String) txIterator.next();					

				/*
				* Retrieves the Selected Permissions and set to RolePermissionControllerJBean
				* based the selection we will set accessLevel
				* if the user selectes RWD - access level 7
				* RW - access level 3
				* this is like file permissions in UNIX
				*/

				String[] arrTemp = request.getParameterValues(txId);
				
				int accessLevel = 0;
				
				try
				{
					if(arrTemp!=null) {	// just making sure
						
						for(int i = 0; i < arrTemp.length; i++)
						{
							if(arrTemp[i].equalsIgnoreCase("R"))
							{
								accessLevel += 1;
							}
							else if(arrTemp[i].equalsIgnoreCase("W"))
							{
								accessLevel += 2;					
							}
							else if(arrTemp[i].equalsIgnoreCase("D"))
							{
								accessLevel += 4;					
							}
							else if(arrTemp[i].equalsIgnoreCase("I"))//Added by madhu on 16-09-05
							{
								accessLevel	+= 8;					
							}//End
						}
					}
				}
				catch(NullPointerException ne)
				{
					System.out.println("Got Null Pointer Exception - RolePermissionsAddProcess : JSP");
				}
				
				if(accessLevel != 0)
				{
					selectedPermissions.put( txId, new Integer(accessLevel) );
				}
				
			} // while TXNs


//			Logger.info(fileName, "Module in Controller and List "+modules[currentIndex] + " : "+selectedPermissions);

			// Here we have to decide whether to increment the Mode Counter or the Module Counter
			
			if(currentModule.equals(ETRANS) && hasModes && currentModeIndex < modes.length)
			{
				// There are modes and still some left
				int	shipmentMode	=	ShipmentMode.getShipmentMode( modes[currentModeIndex] );
				String subModule	=	currentModule+"@"+shipmentMode;
								
				_rolePermissionControllerJBean.addPermissions( subModule , selectedPermissions);
				currentModeIndex += 1;	// There are modes and still some left
			}
			else
			{
				_rolePermissionControllerJBean.addPermissions( currentModule , selectedPermissions);
				currentIndex += 1;	// This module is not Etrans hence increment counter
			}
			
			if(currentModule.equals(ETRANS) && hasModes && currentModeIndex == modes.length) {
				// Increment module counter if modes sorting is over
				currentIndex += 1;
				// Put arraylist of Modes in hashtable agains module Etrans
			}

			if(modules.length <= currentIndex)
			{
				actionPage	= FINAL_PERMISSIONS_PAGE;
			}
			else
			{
				int moduleIndex = Integer.parseInt(modules[currentIndex] );
				currentModule = UserAccessUtility.getRoleModule(moduleIndex);
				
				// Get the transaction ids to show in next screen
				if(currentModule.equals(ETRANS) && hasModes && currentModeIndex < modes.length)
				{
					int shipmentMode = ShipmentMode.getShipmentMode( modes[currentModeIndex] );
					txList	= getModuleWiseTransactions(currentModule, shipmentMode, roleModel.getRoleLevel(), loginbean );
				}
				else
				{
					txList	= getModuleWiseTransactions(currentModule, roleModel.getRoleLevel(), loginbean );
				}
				
				request.setAttribute("modulewise_transactions_list", txList );
				actionPage	= PERMISSIONS_VIEW_PAGE;
			}

			request.setAttribute("currentIndex", 	 new Integer(currentIndex));
			request.setAttribute("currentModeIndex", new Integer(currentModeIndex));
			session.setAttribute("_rolePermissionControllerJBean",_rolePermissionControllerJBean);//@@Added by Yuvraj for Clustering Issues on Load Balancer.
		}
		else // @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
		{
			//Logger.info(fileName,"screen_name : rolePermissions");
      logger.info(fileName+"screen_name : rolePermissions");

			roleModel		= (RoleModel) session.getAttribute("roleModel");
			int txLevel		= 1;
			
			ArrayList txListFetched = new ArrayList();
			
			modules	= (String[]) session.getAttribute("module");
			modes	= (String[]) session.getAttribute("mode");
			
			currentIndex		= Integer.parseInt( request.getParameter("currentIndex") );
			currentModeIndex	= Integer.parseInt( request.getParameter("currentModeIndex") );
			
			boolean	hasModes	=	((Boolean) session.getAttribute("hasModes")).booleanValue();
			
			int 	currentModuleIndex	= Integer.parseInt( modules[currentIndex] );
			currentModule	= UserAccessUtility.getRoleModule( currentModuleIndex );
			
			if(currentModule.equals(ETRANS) && hasModes && currentModeIndex < modes.length) {
				currentMode		= modes[currentModeIndex];
			}
			
			try
			{
				//Logger.info(fileName," Module Traversing  is : "+ modules[currentIndex]);

				if(currentModule.equals(ETRANS) && hasModes && currentModeIndex < modes.length)
				{
					// There are modes and still some left
					int	shipmentModeId	=	ShipmentMode.getShipmentMode( modes[currentModeIndex] );	
					txListFetched = userDelegate.getTransactionIds( currentModule, shipmentModeId, roleModel.getRoleLevel() );
					//txListFetched = remote.getTransactionIds( currentModule, shipmentModeId, roleModel.getRoleLevel() );
				}
				else
				{
					txListFetched = userDelegate.getTransactionIds( currentModule, roleModel.getRoleLevel() );
					//txListFetched = remote.getTransactionIds( currentModule, roleModel.getRoleLevel() );
				}
			} 
			catch(FoursoftException e) 
			{
				//Logger.error(fileName,"Exception is :  ",e);
        logger.error(fileName+"Exception is :  "+e);
			}


			Hashtable	selectedPermissions = new Hashtable();
			Iterator	txIterator			= txListFetched.iterator();

			String txId = "";
			
			while(txIterator.hasNext())	//	 while TXN
			{
				txId = (String)txIterator.next();

				/*
				* Retrieves the Selected Permissions and set to RolePermissionControllerJBean
				* based the selection we will set accessLevel
				* if the user selectes RWD - access level 7
				* RW - access level 3
				* this is like file permissions in UNIX
				*/
				
				String[] arrTemp = request.getParameterValues( txId );
				int accessLevel = 0;
				
				try
				{
					for(int i = 0; i < arrTemp.length; i++)
					{
						if(arrTemp[i].equalsIgnoreCase("R"))
						{
							accessLevel+=1;
						}
						else if(arrTemp[i].equalsIgnoreCase("W"))
						{
							accessLevel+=2;					
						}
						else if(arrTemp[i].equalsIgnoreCase("D"))
						{
							accessLevel+=4;					
						}
						else if(arrTemp[i].equalsIgnoreCase("I"))//Added by madhu on 16-09-05
						{
							accessLevel+=8;					
						}//End
					}

				} 
				catch(NullPointerException ne) 
				{
					System.out.println("Got Null Pointer Exception - RolePermissionsAddProcess : JSP");
				}
				
				if(accessLevel != 0) 
				{
					selectedPermissions.put( txId, new Integer(accessLevel) );
				}
				
			} // while TXN
			
			//			Logger.info(fileName,"Selected Permissions are : "+selectedPermissions);
			
			//_rolePermissionControllerJBean.addPermissions( modules[currentIndex], selectedPermissions );

			//Logger.info(fileName,"Modules length before despatching : currentIndex - "+modules.length+" : "+currentIndex);	
			// currentIndex += 1;
			
			//$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
			
			if(currentModule.equals(ETRANS) && hasModes && currentModeIndex < modes.length)
			{
				// There are modes and still some left
				int	shipmentMode	=	ShipmentMode.getShipmentMode( modes[currentModeIndex] );
				String subModule	=	currentModule+"@"+shipmentMode;
				
				_rolePermissionControllerJBean.addPermissions( subModule , selectedPermissions);
				currentModeIndex += 1;	// There are modes and still some left
			}
			else
			{
				_rolePermissionControllerJBean.addPermissions( currentModule , selectedPermissions);
				currentIndex += 1;	// This module is not Etrans hence increment counter
			}
			
			if(currentModule.equals(ETRANS) && hasModes && currentModeIndex == modes.length) {
				// Increment module counter if modes sorting is over
				currentIndex += 1;
				// Put arraylist of Modes in hashtable agains module Etrans
			}
			
			//$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$

			if(modules.length <= currentIndex)
			{
				actionPage	= FINAL_PERMISSIONS_PAGE;
			}
			else
			{
				int moduleIndex = Integer.parseInt(modules[currentIndex] );
				currentModule = UserAccessUtility.getRoleModule(moduleIndex);
								
				//Logger.info(fileName,"currentModule : roleModel : "+currentModule+ " : "+roleModel.getRoleLevel());
				
				//	txList	= getModuleWiseTransactions(currentModule, roleModel.getRoleLevel() );
				
				// Get the transaction ids to show in next screen
				if(currentModule.equals(ETRANS) && hasModes && currentModeIndex < modes.length)
				{
					int shipmentMode = ShipmentMode.getShipmentMode( modes[currentModeIndex] );
					txList	= getModuleWiseTransactions(currentModule, shipmentMode, roleModel.getRoleLevel(), loginbean );
				}
				else
				{
					txList	= getModuleWiseTransactions(currentModule, roleModel.getRoleLevel(), loginbean );
				}
				
				request.setAttribute("modulewise_transactions_list", txList );
				
				actionPage	= PERMISSIONS_VIEW_PAGE;
			}
			request.setAttribute("currentIndex", 	 new Integer(currentIndex));
			request.setAttribute("currentModeIndex", new Integer(currentModeIndex));
			session.setAttribute("_rolePermissionControllerJBean",_rolePermissionControllerJBean);//@@Added by Yuvraj for Clustering Issues on Load Balancer.
			
		}
	}
	else if(screen_name.equals("user_role_entry") )
	{		
		if(action.equalsIgnoreCase("Modify") )
		{
			String					errorMessage	= "";
			ErrorMessage			errMsg		= null;
			boolean status =false ;

			if(request.getParameter("currentIndex") == null)
			{
				currentIndex = 0;
				request.setAttribute("currentIndex", 	 new Integer(0) );
				request.setAttribute("currentModeIndex", new Integer(0) );
				
				String		roleId		= request.getParameter("roleId");
				String		locationId	= request.getParameter("locationId");
				String		accessType	= request.getParameter("accessType");		
				
				try
				{
					//status = userDelegate.isLocationIdExists(locationId,accessType);
					//status = userDelegate.IsLocIdExists(locationId,loginbean.getAccessType(),accessType,loginbean.getLocationId()); 

					status = userDelegate.IsLocIdExists(locationId,loginbean.getAccessType(),accessType,loginbean.getLocationId());
				
					
					roleModel	= userDelegate.getRoleModel(roleId, locationId);
					
					ArrayList listPermissions	= roleModel.getRolePermissions();
					//Logger.info(fileName,"listPermissions "+listPermissions);
					TxDetailVOB txDetailVOB = null;

					ArrayList	alShipmentModesFound = new ArrayList();

					int	lastFoundMode	=	-1;
					
					for(int i=0; i < listPermissions.size(); i++)
					{
						txDetailVOB	= (TxDetailVOB) listPermissions.get(i);
						
						int shipmentMode = txDetailVOB.shipmentMode;
						
						if(shipmentMode > -1 && !alShipmentModesFound.contains(new Integer( shipmentMode ))) 
						{
							alShipmentModesFound.add( new Integer( shipmentMode ) );
							//lastFoundMode = shipmentMode;
						}

						currentPermissions.put( txDetailVOB.txId, new Integer(txDetailVOB.accessLevel) );
					}

					// Save the info abut modes in session
					// to identify the modules assosiated
					int roleModuleIndex	= Integer.parseInt( roleModel.getRoleModule() );
					modules	= ArraySupport.getBinaryNumbers( roleModuleIndex );	// converting the roleModule into list of Modules					
					
					session.setAttribute("module", modules);
					
					// Initialize Mode Array
					int	modeSummation = 0;
					
					for(int n=0; n < alShipmentModesFound.size(); n++) {
						modeSummation += ((Integer) alShipmentModesFound.get(n)).intValue();
					}
					
					// Here we have to set the mode also.
					modes = ShipmentMode.getShipmentModes( modeSummation );
					session.setAttribute("mode", modes);

					boolean	hasModes	=	false;
					
					if(modes!=null && modes.length > 0) {
						hasModes	=	true;
					}
					session.setAttribute("hasModes", new Boolean( hasModes) );
					
					int moduleIndx = Integer.parseInt( modules[currentIndex] );
					
					currentModule = UserAccessUtility.getRoleModule( moduleIndx );
					
					//Logger.info(fileName,"Current Module : "+currentModule);
					
					if(currentModule.equals(ETRANS)) {
						txList	= getModuleWiseTransactions( currentModule, 0, roleModel.getRoleLevel(), loginbean );
					} else {
						txList	= getModuleWiseTransactions( currentModule, roleModel.getRoleLevel(), loginbean );
					}
					
					//Logger.info(fileName,"TxList : "+txList);
					request.setAttribute("modulewise_transactions_list", txList);

					session.setAttribute("role_permissions", currentPermissions);
					session.setAttribute("roleModel", roleModel);

					//			Logger..info(fileName," Role Permissions List "+currentPermissions);
			
				if(roleModel == null)
				{
					errMsg = new ErrorMessage("The Role '"+roleId+"' does not exist for '"+locationId+"'", "ESACUserRoleEnterId.jsp");
					ArrayList keyValueList = new ArrayList();
					keyValueList.add(new KeyValue("UIName","Role") );
					keyValueList.add(new KeyValue("action","Modify") );
					errMsg.setKeyValueList(keyValueList);
					request.setAttribute("errorMessage", errMsg); 
					actionPage = "ESupplyMessagePage.jsp";

			   }

				// to identify the modules assosiated
				actionPage = PERMISSIONS_VIEW_PAGE;
				}
				catch(FoursoftException fe) 
				{
					//Logger.error(fileName," Error in Retreiving role permissions "+ fe);
          logger.error(fileName+" Error in Retreiving role permissions "+ fe);

					if (fe.getMessage()!= null)
					{
						if(status){
						errorMessage = ((LocalizationContext)application.getAttribute("lang")).getResourceBundle().getString("100071")+"'"+roleId+"' "+((LocalizationContext)application.getAttribute("lang")).getResourceBundle().getString("100065")+"' "+locationId+"'";//+bundle.getBundle().getString("100081")+"' "+accessType+"'";
						}
						else{
						errorMessage =((LocalizationContext)application.getAttribute("lang")).getResourceBundle().getString("100623")+"\n"+" '"+locationId+"'";
						}
						//errorMessage = errorMessage + roleId;
						errMsg = new ErrorMessage(errorMessage, "ESACUserRoleEnterId.jsp",fe.getErrorCode(),fe.getComponentDetails());	
						ArrayList keyValueList = new ArrayList();
						keyValueList.add(new KeyValue("UIName","Role") );
						keyValueList.add(new KeyValue("action","Modify") );
						errMsg.setKeyValueList(keyValueList);
						request.setAttribute("errorMessage", errMsg); 
						actionPage = "ESupplyMessagePage.jsp";


					}
					else
					{
                       //System.out.println("fe.getMessage() = ====== "+fe.getMessage());
					   //System.out.println("bundle = ====== "+bundle);	
						errorMessage = ((LocalizationContext)application.getAttribute("lang")).getResourceBundle().getString(fe.getMessage());
						errorMessage = errorMessage + roleId;
						//System.out.println("errorMessage = ====== "+errorMessage);	
					}
				}
			
				}
			else
			{
				
				modules			= (String[]) session.getAttribute("module");
				currentIndex	= Integer.parseInt(request.getParameter("currentIndex"));
				roleModel		= (RoleModel)session.getAttribute("roleModel");

				int txLevel		= 1;

				Hashtable selectedPermissions = new Hashtable();
				Iterator txIterator = null;
				txIterator = txList.iterator();
				String txId = "";
				while(txIterator.hasNext())
				{
					txId = (String)txIterator.next();

			/*
			* Retrieves the Selected Permissions and set to RolePermissionControllerJBean
			* based the selection we will set accessLevel
			* if the user selectes RWD - access level 7
			* RW - access level 3
			* this is like file permissions in UNIX
			*/
					String[] arrTemp = request.getParameterValues(txId);
					int accessLevel = 0;
					try
					{
						for(int i = 0; i < arrTemp.length; i++)
						{
							if(arrTemp[i].equalsIgnoreCase("R"))
							{
								accessLevel+=1;
							}
							else if(arrTemp[i].equalsIgnoreCase("W"))
							{
								accessLevel+=2;					
							}
							else if(arrTemp[i].equalsIgnoreCase("D"))
							{
								accessLevel+=4;					
							}
							else if(arrTemp[i].equalsIgnoreCase("I"))//Added by madhu on 16-09-05
							{
								accessLevel+=8;					
							}//End
						}
					}
					catch(NullPointerException ne)
					{
						System.out.println("Got Null Pointer Exception - RolePermissionsAddProcess : JSP");
					}
					if(accessLevel != 0)
					{
						selectedPermissions.put(txId,new Integer(accessLevel));
					}
				}
	//			Logger.info(fileName,"Selected Permissions are : "+selectedPermissions);
				_rolePermissionControllerJBean.addPermissions(modules[currentIndex], selectedPermissions);

				//Logger.info(fileName,"Modules length before despatching : currentIndex - "+modules.length+" : "+currentIndex);	
				currentIndex += 1;

				if(modules.length <= currentIndex)
				{
					actionPage	= FINAL_PERMISSIONS_PAGE;
				}
				else
				{
					actionPage	= PERMISSIONS_VIEW_PAGE;
					
					int moduleIndex = Integer.parseInt(modules[currentIndex] );
					currentModule = UserAccessUtility.getRoleModule(moduleIndex);
					
					//Logger.info(fileName,"currentModule : roleModel : "+currentModule+ " : "+roleModel.getRoleLevel());
					
					txList	= getModuleWiseTransactions(currentModule, roleModel.getRoleLevel(), loginbean );
					
					request.setAttribute("modulewise_transactions_list", txList );
				}
			}
			request.setAttribute("currentIndex",new Integer(currentIndex));
			session.setAttribute("_rolePermissionControllerJBean",_rolePermissionControllerJBean);//@@Added by Yuvraj for Clustering Issues on Load Balancer.
		}
		else if(action.equalsIgnoreCase("View") || action.equalsIgnoreCase("Delete") ) 
		{
				String			errorMessage	= "";
				ErrorMessage	errMsg			= null;
				boolean			status			= false;
				
				currentIndex = 0;
				request.setAttribute("currentIndex", 	 new Integer(0) );
				request.setAttribute("currentModeIndex", new Integer(0) );
				
				String		roleId		= request.getParameter("roleId");
				String		locationId	= request.getParameter("locationId");
				String		accessType	= request.getParameter("accessType");				
				
										
				try
				{
					
					//status = userDelegate.isLocationIdExists(locationId,accessType);
					//status = userDelegate.IsLocIdExists(locationId,loginbean.getAccessType(),accessType,loginbean.getLocationId()); 

					status = userDelegate.IsLocIdExists(locationId,loginbean.getAccessType(),accessType,loginbean.getLocationId());
							
					roleModel	= userDelegate.getRoleModel( roleId, locationId);
					
					session.setAttribute("roleModel", roleModel);
				
				if(roleModel == null)
				{					
				// Modified for the New access control 
						if(status)
						{
						errorMessage = ((LocalizationContext)application.getAttribute("lang")).getResourceBundle().getString("100071")+"'"+roleId+"' "+((LocalizationContext)application.getAttribute("lang")).getResourceBundle().getString("100065")+"' "+locationId+"'"; //+bundle.getBundle().getString("100081")+"' "+accessType+"'";
						}
						else
						{
						errorMessage =((LocalizationContext)application.getAttribute("lang")).getResourceBundle().getString("100623")+"\n"+" '"+locationId+"'";
						}			
				
				errMsg = new ErrorMessage(errorMessage, "ESACUserRoleEnterId.jsp");
			
					ArrayList keyValueList = new ArrayList();
					keyValueList.add(new KeyValue("UIName","Role") );
					keyValueList.add(new KeyValue("action",action) );
					errMsg.setKeyValueList(keyValueList);
					request.setAttribute("errorMessage", errMsg); 
					actionPage = "ESupplyMessagePage.jsp";

				}
				else{ 
				%> <jsp:forward page="ESACRolePermissionsView.jsp" /> <%
				}
				}
				catch(FoursoftException fe)
				{
					//Logger.error(fileName," Error in Retreiving role permissions "+ fe);
          logger.error(fileName+" Error in Retreiving role permissions "+ fe);

					if (fe.getMessage()!= null)
					{
						if(status)
						{
						errorMessage = ((LocalizationContext)application.getAttribute("lang")).getResourceBundle().getString("100071")+"'"+roleId+"' "+((LocalizationContext)application.getAttribute("lang")).getResourceBundle().getString("100065")+"' "+locationId+"'"; //+bundle.getBundle().getString("100081")+"' "+accessType+"'";
						}
						else
						{
						errorMessage =((LocalizationContext)application.getAttribute("lang")).getResourceBundle().getString("100623")+"\n"+" '"+locationId+"'";
						}
						errMsg = new ErrorMessage(errorMessage, "ESACUserRoleEnterId.jsp",fe.getErrorCode(),fe.getComponentDetails());
						ArrayList keyValueList = new ArrayList();
						keyValueList.add(new KeyValue("UIName","Role") );
						keyValueList.add(new KeyValue("action",action) );
						errMsg.setKeyValueList(keyValueList);
						request.setAttribute("errorMessage", errMsg);
						actionPage = "ESupplyMessagePage.jsp";


					}
					else
					{
                       errorMessage = ((LocalizationContext)application.getAttribute("lang")).getResourceBundle().getString(fe.getMessage());
						errorMessage = errorMessage + roleId;
					}
				}
								
				
							
		}
		
	}
	else if(screen_name.equals("role_confirmation") )
	{
//		Logger.info(fileName," Role Model : "+roleModel);
		if(action.equals("add") )
		{
			roleModel.setRolePermissions(_rolePermissionControllerJBean.getRolePermissionsList() );
			String					errorMessage	= "";
			ErrorMessage			errMsg		= null;
			try
			{
				//Logger.info("ESACRoleRegistrationController.jsp","roleModel.getRolePermissions().size()::"+roleModel.getRolePermissions().size());
        if(roleModel.getRolePermissions().size() > 0)
				{
					userDelegate.registerRole(roleModel, loginbean.getUserId(), loginbean.getLocationId());
					//remote.registerRole(roleModel, loginbean.getUserId(), loginbean.getLocationId());
					errorMessage =	((LocalizationContext)application.getAttribute("lang")).getResourceBundle().getString("100069")+"\n"+
									((LocalizationContext)application.getAttribute("lang")).getResourceBundle().getString("100024")+roleModel.getRoleId() +"\n"+
									((LocalizationContext)application.getAttribute("lang")).getResourceBundle().getString("100268")+roleModel.getLocationId();
				}
				else
				{
					errorMessage = ((LocalizationContext)application.getAttribute("lang")).getResourceBundle().getString("100070");
				}
				errMsg = new ErrorMessage(errorMessage, "ESACRoleRegistration.jsp");
			}
			catch(FoursoftException fe)
			{				
				//Logger.error(fileName,"Already Exists Exception ",fe);
        logger.error(fileName+"Already Exists Exception "+fe);
				
				if (fe.getMessage()!= null)
				{
					//errorMessage = bundle.getBundle().getString("100073");					
					errorMessage = ((LocalizationContext)application.getAttribute("lang")).getResourceBundle().getString(fe.getMessage());
				}
				else
					errorMessage = fe.getMessage();
				errMsg = new ErrorMessage(errorMessage, "ESACRoleRegistration.jsp", fe.getErrorCode(), fe.getComponentDetails());
			}
			finally
			{
				session.removeAttribute("_rolePermissionControllerJBean");
				session.removeAttribute("module");
				session.removeAttribute("roleModule");
				request.setAttribute("errorMessage", errMsg); 
			}
			actionPage = "ESupplyMessagePage.jsp";
		}
		else
		{
			String					errorMessage	= "";
			ErrorMessage			errMsg		= null;
			ArrayList keyVlaueList = new ArrayList(2);
			roleModel.setRolePermissions(_rolePermissionControllerJBean.getRolePermissionsList() );
			
			String	versionNo	=	request.getParameter("version");
			int		version		=	0;
			try 
			{
				version = Integer.parseInt( versionNo );
			} 
			catch(NumberFormatException nfe){}

			roleModel.setVersion( version );
			roleModel.setModifiedBy( loginbean.getUserId() );
			roleModel.setModifiedDate( new java.sql.Timestamp( (new java.util.Date()).getTime() ) );

			try
			{
//				Logger.info(fileName, "Role Model : "+roleModel);
				if(roleModel.getRolePermissions().size() > 0)
				{
					userDelegate.updateRoleModel(roleModel, loginbean.getUserId(), loginbean.getLocationId());
					//remote.updateRoleModel(roleModel, loginbean.getUserId(), loginbean.getLocationId());
					
					errorMessage =	((LocalizationContext)application.getAttribute("lang")).getResourceBundle().getString("100023")+"\n"+ ((LocalizationContext)application.getAttribute("lang")).getResourceBundle().getString("100024")+roleModel.getRoleId() +"\n"+ ((LocalizationContext)application.getAttribute("lang")).getResourceBundle().getString("100268")+ roleModel.getLocationId();				
				}
				else
				{
					errorMessage = ((LocalizationContext)application.getAttribute("lang")).getResourceBundle().getString("100025");
				}		

				session.removeAttribute("_rolePermissionControllerJBean");
				session.removeAttribute("roleModel");
				session.removeAttribute("role_permissions");
				errMsg = new ErrorMessage(errorMessage, "ESACUserRoleEnterId.jsp");
				
			}
			catch(FoursoftException ex)
			{
				//Logger.error(fileName," error in Updating RolePermissions ",ex.getMessage());
        logger.error(fileName+" error in Updating RolePermissions "+ex.getMessage());
				errorMessage = ((LocalizationContext)application.getAttribute("lang")).getResourceBundle().getString("100026");
				errMsg = new ErrorMessage(errorMessage, "ESACUserRoleEnterId.jsp", ex.getErrorCode(), ex.getComponentDetails());
			}
			finally
			{				
				keyVlaueList.add(new KeyValue("UIName", "Role") );
				keyVlaueList.add(new KeyValue("action", "Modify") );
				errMsg.setKeyValueList(keyVlaueList);
				request.setAttribute("errorMessage", errMsg); 			
			}
			actionPage = "ESupplyMessagePage.jsp";
		}
	}
	else if(screen_name.equals("role_permissions_view") )
	{
//		Logger.info(fileName, "Role to be Deleted  :: "+roleModel);
		String nextNavigation	= "";
		String					errorMessage	= "";
		ErrorMessage			errMsg		= null;
		boolean deleteFlag	= false;
		try
		{
			deleteFlag	= userDelegate.removeRole(roleModel.getRoleId(), roleModel.getLocationId(), loginbean.getUserId(), loginbean.getLocationId());
			//deleteFlag	= remote.removeRole(roleModel.getRoleId(), roleModel.getLocationId(), loginbean.getUserId(), loginbean.getLocationId());
			errorMessage =	((LocalizationContext)application.getAttribute("lang")).getResourceBundle().getString("100027")+"\n"+((LocalizationContext)application.getAttribute("lang")).getResourceBundle().getString("100024")+roleModel.getRoleId()+"\n"+ ((LocalizationContext)application.getAttribute("lang")).getResourceBundle().getString("100268")+roleModel.getLocationId();
			errMsg = new ErrorMessage(errorMessage, "ESACUserRoleEnterId.jsp");

		}
		catch(FoursoftException ex)
		{
			//Logger.error(fileName,"error in Deleting  Role ",ex);
      logger.error(fileName+"error in Deleting  Role "+ex);
			errorMessage = ex.getMessage();
			//errorMessage = bundle.getBundle().getString("100068");
			errMsg = new ErrorMessage(errorMessage, "ESACUserRoleEnterId.jsp",ex.getErrorCode(),ex.getComponentDetails());
		}
		finally
		{						
			ArrayList keyVlaueList = new ArrayList();
			keyVlaueList.add(new KeyValue("UIName", "Role") );
			keyVlaueList.add(new KeyValue("action", "Delete") );
			errMsg.setKeyValueList(keyVlaueList);
			request.setAttribute("errorMessage", errMsg); 			
		}
		actionPage="ESupplyMessagePage.jsp";
	}
	else
	{
		if(action.equalsIgnoreCase("viewAll") )
		{
			ArrayList         roleList  = new ArrayList();
			try
			{
				roleList	= userDelegate.getRoleViewAllInformation();
					//roleList	= remote.getRoleViewAllInformation();
				
				request.setAttribute("roleList", roleList);

			}
			catch(FoursoftException ex)
			{
				//Logger.info(fileName,"Error in getting Role's Information ", ex);
        logger.info(fileName="Error in getting Role's Information "+ ex);
			}			
		}
		actionPage="ESACRolesViewAll.jsp";
	}
	//System.out.println("actionPageactionPageactionPageactionPage"+actionPage);
%>
<jsp:forward page="<%= actionPage %>" />
<%!
    public ArrayList getModuleWiseTransactions(String moduleName, String roleLevel, ESupplyGlobalParameters loginbean)
	{
        ArrayList 						txListVOB	= null;								
		try
		{
            UserRoleDelegate userDelegate = new UserRoleDelegate();
			txListVOB =(ArrayList) userDelegate.getTransactionDetailVOBs(moduleName, roleLevel);
			//txListVOB =(ArrayList) remote1.getTransactionDetailVOBs(moduleName, roleLevel);

		}
		catch(FoursoftException e)
		{
			//Logger.error(fileName," Exception in reteiving module wise transactions is  ",e);
      logger.error(fileName+" Exception in reteiving module wise transactions is  "+e);
		}
		return txListVOB;

	}
	
    public ArrayList getModuleWiseTransactions(String moduleName, int shipmentMode, String roleLevel, ESupplyGlobalParameters loginbean)
	{
		ArrayList 						txListVOB	= null;						
		
		try
		{
            UserRoleDelegate userDelegate = new UserRoleDelegate();		
			txListVOB = userDelegate.getTransactionDetailVOBs(moduleName, shipmentMode, roleLevel);
			//txListVOB =(ArrayList) remote1.getTransactionDetailVOBs(moduleName, shipmentMode, roleLevel);

		}
		catch(Exception e)
		{
			//Logger.error(fileName," Exception in reteiving module wise transactions is  ",e);
      logger.error(fileName+" Exception in reteiving module wise transactions is  "+e);
		}
		return txListVOB;

	}
%>