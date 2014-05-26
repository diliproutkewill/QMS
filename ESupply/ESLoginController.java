/**
 *
 * Copyright (c) 1999-2001 by FourSoft, Pvt Ltd. All Rights Reserved.
 * This software is the proprietary information of FourSoft, Pvt Ltd.
 * Use is subject to license terms.
 *
 * esupply - v 1.x 
 *
 */
/**
 * File			: ESLoginController.java
 * sub-module	: AccessControl
 * module		: esupply
 *
 * Earlier the functionality in this Servlet existed in the file ESACLoginController.jsp but for
 * purposes regarding to access to sensitive source code, the same functionality now exists as this
 * Servlet. The class file of this Servlet should be desireably obfuscated before use.
 * 
 * This is used to verify the userName and Password, the access level for the User
 * and to get his login parameters (loginbean object) and store it in session.
 * This also checks whether the user is Already Logged in or Not.
 * Currently, one only registered User (as in the database table FS_USERMASTER) will be able to
 * login with only one session i.e. through a single browser window from anywhere in the world.
 * He as a physical Uaer (or any other physical User) is not allowed to work with the same credentials
 * like Username, Password and Location from any other browser window in the world. In other world a User
 * with a particular set of credentials (Username, Password and Location) is allowed to have only
 * one browser session with the application. Once the limit (number) of such single User sessions
 * exceeds the number of licensed concurrent Users, no other Users are allowed to login in.
 * 
 * author		: Amit Parekh
 * date			: 12-10-2002
 * Modified History
 * Amit Parekh		24/10/2002			Logout functionality changed to handle higher level even handling
 * Amit Parekh		30/10/2002			Logout on error / exception improved and extended to subsequent files
 * 										like ESMenuTreeGenerator.jsp and ESMenuHeading.jsp
 * Amit Parekh		01/11/2002			In case the User gets locked in the application upon losing connection
 * 										or error in logout process we have to invalidate his session. For this
 *                                      purpose now a handle to his session is kept in his UserCredentials object
 *                                      which is used to log him out in case he gets locked. His session will be
 *                                      invalidated just like a normal session with no noticeable difference.
 *                                      Upon invalidation of a User's session his UserCredentials object
 *                                      will remove itself from the application level Hashtable i.e. the
 *                                      Logged-In User List.
 * Amit Parekh		19/11/2002			Changes like references to a variable VendorId in loginbean have been removed 
 * 										along with its accessor and mutator methods.
 * RSR Krishna		xx/02/2003			Changes made to handle Access Control for eSupply SP Users. (Comment put by Amit)
 * Amit Parekh		28/02/2003			The Single Thread Model interface was removed. The service method is now
 * 										synchronized (inner code block) to handle only one Login at a time.
 *                                      Additionally, some changes were made by R.S.R. Krishna and V. Madhu to handle
 *                                      Access Control for eSupply SP during my tenure in EGL team. The above comments
 *                                      in their name is put by me as a reminder for these guys to write Modified history
 *                                      so that its easier to maintain files.
 *                                      
 */
 

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.Enumeration;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.sql.Timestamp;

import com.foursoft.esupply.common.bean.ESupplyGlobalParameters;
import com.foursoft.esupply.accesscontrol.exception.AlreadyLoggedInException;
import com.foursoft.esupply.accesscontrol.exception.InvalidUserException;
import com.foursoft.esupply.common.exception.FoursoftException;
import com.foursoft.esupply.common.bean.UserCredentials;
//import com.foursoft.esupply.common.util.Logger;
import org.apache.log4j.Logger;
import com.foursoft.esupply.delegate.AccessControlDelegate;
import com.foursoft.esupply.common.util.BundleFile;
import java.net.URL;
import javax.xml.parsers.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import java.util.Timer;
import java.util.Date;
import com.foursoft.esupply.common.util.TestTimer;

public final class ESLoginController extends HttpServlet {

	private static final String CONTENT_TYPE	= "text/html";
	private static final String fileName		= "ESLoginController.java";
	private static Logger logger = null;

	public void init(ServletConfig config) throws ServletException {
		logger  = Logger.getLogger(ESLoginController.class);
		super.init(config);
			
		//System.out.println("init method in login controller called");
			//Date dd = new Date();
			//Timer t = new Timer();
			//TestTimer tt = new TestTimer(); 
			//t.schedule(tt,dd,20000);
			

	}

	public void service(HttpServletRequest request, HttpServletResponse response){

		synchronized (this) 
		{
			HttpSession	session		=	null;

			AccessControlDelegate accessDelegate = null;
			

			Hashtable	appSettings	=	null;

			boolean	errorOccurred = false;

			String brName = null;
	
			String	userId 		= "";
			String	locationId 	= "";
			String	password	= "";
			String	accessType	= "";
			String	userType	= "";
			String	windowName	=	"";

			String		actionPage			= "ESMenuIndex.jsp";
			boolean		isAuthorised		= false;
			boolean		alreadyLoggedIn		= false;
			boolean		sameSession			= false;
			String		errorMessage		= "";
			String		sessionId			= "";
			String		loggingUserId		= "";
			String		ipAddress			= request.getRemoteHost();
			String		invalidateSession	= "";

			userId		= request.getParameter("userId");
			password	= request.getParameter("password");
			userType	= request.getParameter("userType");	
			locationId	= request.getParameter("locationId").toUpperCase();
			windowName	= request.getParameter("windowName");

			invalidateSession = request.getParameter("invalidate");
			//System.out.println("invalidateSession="+invalidateSession);
     
     //Included by Shyam for DHL for 13213
    if(invalidateSession==null)
        invalidateSession="Y";
          
			 //for displaying IP Address
			
		
			ESupplyGlobalParameters	globalParameteres	=	null;
			Hashtable				permissionsList		=	new Hashtable(30);
			Integer					shipmentMode		=	new Integer(0);

			try
			{
				accessDelegate = new AccessControlDelegate();

				session	=	request.getSession();
				session.setAttribute("ipAddress",ipAddress);
				brName = request.getParameter("brName");

				if(userType != null) {
					session.setAttribute("userType",userType);
				}

				request.setAttribute("windowName", windowName);

				session.setAttribute("browserName",brName);
			
				appSettings	=	(Hashtable) this.getServletContext().getAttribute("appSettings");
			
				if(appSettings != null) {
					ESupplyGlobalParameters.setTerminalUserId( appSettings );
				} else {
					appSettings = new Hashtable();
					this.getServletContext().setAttribute("appSettings", appSettings );
				}

				// user can be Registerd User (or) can be CRM User .. based on the userType Credentials has to verify

			//	Logger.info(fileName, "userType = "+userType);

				HashMap	supportLanguage	=	null;
				supportLanguage	=	(HashMap) this.getServletContext().getAttribute("supportLanguage");
		
				if(supportLanguage == null)
				{
					supportLanguage = new HashMap();
                    //java.io.InputStream io = this.getServletContext().getResourceAsStream("/WEB-INF/xml/language.xml");
                    //System.out.println("io.toString() = "+io.toString());
					//java.net.URL url = this.getServletContext().getResource("/WEB-INF/xml/language.xml");
                    //System.out.println("url.toString() = "+url.toString().substring(6));
					//supportLanguage = parse(new java.io.File(url.toString().substring(6)));
                    supportLanguage = parse(this.getServletContext().getResourceAsStream("/WEB-INF/xml/language.xml"));
					this.getServletContext().setAttribute("supportLanguage",supportLanguage);
				}
		
				if(userType.equalsIgnoreCase("ETC") )
				{
					// ETRANS CUSTOMER
					//globalParameteres	= (ESupplyGlobalParameters)remote.verifyETransCRMUser(userId, password, locationId, userType);
					//permissionsList		= remote.getRolePermissions( globalParameteres.getRoleId(), globalParameteres.getRoleLocationId() );
					//shipmentMode	 	= remote.getTerminalType(locationId, globalParameteres.getAccessType());

					globalParameteres	= accessDelegate.verifyETransCRMUser(userId, password, locationId, userType);
					permissionsList		= accessDelegate.getRolePermissions( globalParameteres.getRoleId(), globalParameteres.getRoleLocationId() );
					shipmentMode	 	= accessDelegate.getTerminalType(locationId, globalParameteres.getAccessType());

					globalParameteres.setCustomerId(locationId);
					globalParameteres.setLocationId(locationId);
					isAuthorised		= true;
				}
				else if(userType.equalsIgnoreCase("ESC") || userType.equalsIgnoreCase("ESV"))
				{
					// SP CUSTOMER or VENDOR
			   
					globalParameteres	= (ESupplyGlobalParameters) accessDelegate.verifySPCRMVRMUser(userId, password, locationId, userType);
					//globalParameteres	= (ESupplyGlobalParameters) remote.verifySPCRMVRMUser(userId, password, locationId, userType);
					//Logger.info(fileName, "globalParameteres = "+globalParameteres);
					permissionsList		= accessDelegate.getRolePermissions( globalParameteres.getRoleId(), globalParameteres.getRoleLocationId() );
					//permissionsList		= remote.getRolePermissions( globalParameteres.getRoleId(), globalParameteres.getRoleLocationId() );
					//Logger.info(fileName, "permissionsList = "+permissionsList);
					if(userType.equalsIgnoreCase("ESC")) 
					{
						globalParameteres.setCustomerId( locationId );
						globalParameteres.setLocationId(locationId);
						shipmentMode	 	= accessDelegate.getTerminalType(locationId, globalParameteres.getAccessType());
						//shipmentMode	 	= remote.getTerminalType(locationId, globalParameteres.getAccessType());
					}
					if(userType.equalsIgnoreCase("ESV")) 
					{
						globalParameteres.setCustomerId( locationId );
						globalParameteres.setLocationId(locationId);
						shipmentMode	 	= accessDelegate.getTerminalType(locationId, globalParameteres.getAccessType());
						//shipmentMode	 	= remote.getTerminalType(locationId, globalParameteres.getAccessType());
					}

					if(!userType.equalsIgnoreCase("ESC"))
						globalParameteres.setTerminalId( null );

					//System.out.println()

					isAuthorised		= true;
				}
				else if(userType.equalsIgnoreCase("ELC") || userType.equalsIgnoreCase("ELV") || userType.equalsIgnoreCase("ELT"))
				{
					// ELOG CUSTOMER or VENDOR
					globalParameteres	= (ESupplyGlobalParameters) accessDelegate.verifyELogCRMVRMUser(userId, password, locationId, userType);
					//globalParameteres	= (ESupplyGlobalParameters) remote.verifyELogCRMVRMUser(userId, password, locationId, userType);
					permissionsList		= accessDelegate.getRolePermissions( globalParameteres.getRoleId(), globalParameteres.getRoleLocationId() );
					//permissionsList		= remote.getRolePermissions( globalParameteres.getRoleId(), globalParameteres.getRoleLocationId() );

					if(userType.equalsIgnoreCase("ELC")) 
					{
						globalParameteres.setCustomerId(locationId);
						globalParameteres.setLocationId(locationId);
					}
					if(userType.equalsIgnoreCase("ELV")) 
					{
						globalParameteres.setCustomerId(locationId);
						globalParameteres.setLocationId(locationId);
					}
					if(userType.equalsIgnoreCase("ELT")) 
					{
						globalParameteres.setLocationId(locationId);
						globalParameteres.setTerminalId(locationId);
					}
					//globalParameteres.setTerminalId( null );

					isAuthorised		= true;
				}
				else if(userType.equalsIgnoreCase("EPC") || userType.equalsIgnoreCase("EPV") || userType.equalsIgnoreCase("EPT"))
				{
					// ESUPPLY_EP CUSTOMER, VENDOR or TRANSPORTER
				
					globalParameteres	= (ESupplyGlobalParameters) accessDelegate.verifyELogCRMVRMUser(userId, password, locationId, userType);
					//globalParameteres	= (ESupplyGlobalParameters) remote.verifyELogCRMVRMUser(userId, password, locationId, userType);
					permissionsList		= accessDelegate.getRolePermissions( globalParameteres.getRoleId(), globalParameteres.getRoleLocationId() );
					//permissionsList		= remote.getRolePermissions( globalParameteres.getRoleId(), globalParameteres.getRoleLocationId() );

					globalParameteres.setLocationId(locationId);

					if(userType.equalsIgnoreCase("EPC")) {
						globalParameteres.setCustomerId( locationId );
					}
					if(userType.equalsIgnoreCase("EPV")) {
						globalParameteres.setCustomerId( locationId );
					}
					if(!userType.equalsIgnoreCase("EPT")) {
						globalParameteres.setTerminalId( null );
					}
				
					isAuthorised		= true;
				}
				else 
				{
					// ETRANS or ELOG or ESUPPLY-EP or ESUPPLY-SP User
					// If User type is ETS, ELG, EEP or ESP
					globalParameteres	= (ESupplyGlobalParameters) accessDelegate.getESupplyGlobalParameters(userId, password, locationId, userType);
					//globalParameteres	= (ESupplyGlobalParameters) remote.getESupplyGlobalParameters(userId, password, locationId, userType);

                    URL languageXML = this.getServletContext().getResource("/WEB-INF/xml/language.xml");                                       
			
					permissionsList		= accessDelegate.getRolePermissions( globalParameteres.getRoleId(), globalParameteres.getRoleLocationId() );
					//permissionsList		= remote.getRolePermissions( globalParameteres.getRoleId(), globalParameteres.getRoleLocationId() );
			
					if(!userType.equalsIgnoreCase("ELG") && !userType.equalsIgnoreCase("EEP")) 
					{
						shipmentMode	= accessDelegate.getTerminalType(locationId, globalParameteres.getAccessType());
						//shipmentMode	= remote.getTerminalType(locationId, globalParameteres.getAccessType());
					}
			
					//Logger.info(fileName, "permissionsList = "+permissionsList);
			
					isAuthorised		= true;
					globalParameteres.setLocationId(locationId);

					if(userType.equalsIgnoreCase("EEP") || userType.equalsIgnoreCase("ELG")) 
					{
						globalParameteres.setTerminalId( null );
					}
				}

                
				// The date format will have to be used from UserPreferences object rather
				// than the loginbean for all future coding

				if(userType.equalsIgnoreCase("ETC")) {
					actionPage	= "ESMenuETCIndex.jsp";
				}

				sessionId = session.getId();
				
				
				/* this is used to track the current users using servlet context/application 
				* set the userId as userId.locationId and the set the ServletContext, reason for this
				* remove the user form the context when the session expires or the user is explicitly logged out
				*/
				loggingUserId	= userId+"/"+locationId+"/"+userType;
        if(loggingUserId!=null)
        logger.info("loggingUserId"+loggingUserId);
				/*
				* It will search thru all the users currently logged in and look for the current user,
				* if the user Exists it will throw AlreadyLoggedInException
				*/

				boolean hasExistingSession = appSettings.containsKey( sessionId );

				if(hasExistingSession) 
                {

					/*	If the User tries to login in from the same browser window,
						the session id will be the same. Hence, if we find the session id
						in the Hashtable, it means that a User is already logged in from
						the calling browser window or any one of its chidren.
						set an appropriate message here
					*/

					UserCredentials	uc	= (UserCredentials) appSettings.get( sessionId );

					String	loggedUserKey		= uc.getUserId();
					String	loggedWindowName	= uc.getWindowName();

					//Logger.info(fileName, "loggedWindowName : '"+loggedWindowName+"'" );

				 if(loggedUserKey!=null)
            
        	if(loggedUserKey.equalsIgnoreCase( loggingUserId )) 
                    {
						/*	The same User is trying to login with the same credentials
							(LOCATIONID/USERID) using the same browser window
							or one of its chidren
							Donot put him in the Logged n hash table again
						*/

						if(!loggedWindowName.equals( windowName )) 
                        {
							//Logger.info(fileName, "SAME USER, SAME SESSION, DIFFERENT LOGIN WINDOW");
							alreadyLoggedIn = true;
							sameSession		= true;
						}
					
					} 
                    else 
                    {
						/*	The same User is trying to login with different credentials
							(LOCATIONID/USERID) using the same browser window
							or one of its chidren
						*/
						//Logger.info(fileName, "SAME SESSION, SAME USER trying again with similar / different redentials ");

						sameSession		= true;
						alreadyLoggedIn = true;					

					}
				
				} 
                else 
                {

					//Logger.info(fileName, "DIFFERENT SESSION, DIFFERENT or SAME USER trying again with unknown credentails");

					/*	(1)	The User who is already logged in may be trying to log in again
							from a different browser window with same or different credentials
							of (LOCATIONID / USERID).
						(2)	A different User may be trying to log in with the credentials of a
							User who is already logged in.

						Hence, we have to check each and every UserCredentials Object in
						the Hashtable to see if in any case a User with the credentails of the
						given LOCATIONID / USERID is already logged in or not
					*/

					Enumeration loggedInsessionIds = appSettings.keys();

					while(loggedInsessionIds.hasMoreElements()) 
                    {
				
						String ssnId = (String) loggedInsessionIds.nextElement();

						UserCredentials	uc	= (UserCredentials) appSettings.get( ssnId );

						String loggedUserId	= uc.getUserId();

						if(loggedUserId.equalsIgnoreCase( loggingUserId )) 
                        {
							HttpSession	oldSession	=	uc.getSession();
							ipAddress = (String)oldSession.getAttribute("ipAddress");
							alreadyLoggedIn = true;
            
							break;
						}
					
					} // while

				}
					
					//session objects removal logic implementation (added on 20041206)
		 
			 ArrayList defaultSessionList = new ArrayList(18);
					
			 defaultSessionList.add("browserName");
			 defaultSessionList.add("loginbean");
			 defaultSessionList.add("accessList");
			 defaultSessionList.add("ipAddress");
			 defaultSessionList.add("appSettings");        
			 defaultSessionList.add("userType");
			 defaultSessionList.add("bundle");
			 defaultSessionList.add("ACCESSTYPE");
			 defaultSessionList.add("windowName");
			 defaultSessionList.add("supportLanguage");
			 defaultSessionList.add("userCredentials");
			 defaultSessionList.add("shipmentMode");
			 defaultSessionList.add("proTable");
			 defaultSessionList.add("userPasswordValidation");
			 defaultSessionList.add("warningLimitCrossed");
			 defaultSessionList.add("firstLogin");
			 defaultSessionList.add("Login_Message");
		 
			 defaultSessionList.add("DEFAULT_SESSION_LIST");
		 
			 session.setAttribute("DEFAULT_SESSION_LIST",defaultSessionList);
			 
			 /////////////////ends //////////////////////////////					
					//System.out.println("sameSession="+sameSession);


					
					//System.out.println("sameSession="+sameSession);
				//to invalidate the old session.

        //Logger.info(fileName,"invalidateSession--"+invalidateSession+"sameSession---"+sameSession);
        logger.info(fileName+"invalidateSession--"+invalidateSession+"sameSession---"+sameSession);

				if((invalidateSession!=null)&&(!sameSession))
				{

        //Logger.info(fileName,"Inside if invalidateSession--"+invalidateSession+"sameSession---"+sameSession);
        logger.info(fileName+"Inside if invalidateSession--"+invalidateSession+"sameSession---"+sameSession);

					Enumeration loggedInsessionIds = appSettings.keys();

					while(loggedInsessionIds.hasMoreElements()) 
                    {

				//Logger.info(fileName,"Inside the While");
        logger.info(fileName+"Inside the While");

						String ssnId = (String) loggedInsessionIds.nextElement();

						UserCredentials	uc	= (UserCredentials) appSettings.get( ssnId );

						String loggedUserId	= uc.getUserId();

						if(loggedUserId.equalsIgnoreCase( loggingUserId )) 
                        {
							// when user invalidates the session.
							HttpSession	oldSession	=	uc.getSession();
							oldSession.invalidate();
							appSettings.remove( ssnId );
							alreadyLoggedIn = false;
							//Logger.info(fileName,"Current Session has been invalidated from: "+ipAddress);
							//removing from request
							request.removeAttribute("invalidate");
							request.removeAttribute("password");
							request.removeAttribute("userId");
							request.removeAttribute("locationId");
							break;
						}
					
					} // while
							
				}

				//Logger.info(fileName, "alreadyLoggedIn"+alreadyLoggedIn);

				if(alreadyLoggedIn) 
                {
					throw new AlreadyLoggedInException();
				} 
                else 
                {
					UserCredentials userCredentials	= new UserCredentials();
				
					userCredentials.setUserId( loggingUserId );
					userCredentials.setWindowName( windowName );
					userCredentials.setSession( session );
					userCredentials.setSessionId( sessionId );
					userCredentials.setServletContext( this.getServletContext() );
					session.setAttribute("userCredentials",userCredentials);
				}
		
				//Logger.info(fileName,"ESupplyGlobalParameters :: "+globalParameteres);

			} 
            catch(AlreadyLoggedInException ale) 
            {

				request.setAttribute("message","Failed - User is already Logged In");
//				Logger.error(fileName,"User is already Logged In : "+userId);
				logger.error(fileName+"User is already Logged In : "+userId);
		
				if(sameSession) 
                {
					//errorMessage =	"A User is already logged in from the same browser window (or one of its sibling/child windows).\n"+
									//"Please use a new browser window.";
									errorMessage =	"Please close the index window and use a new browser window to Login.";
				} 
                else 
                {
					errorMessage = "The User is already Logged In at "+ipAddress+". Would you like to invalidate that session?";
					request.setAttribute("invalidate","y");
					request.setAttribute("password",password);
					request.setAttribute("userId",userId);
					request.setAttribute("locationId",locationId);
          
          
          //Included by Shyam for DHL for 13213
         UserCredentials userCredentials	= new UserCredentials();
				
					userCredentials.setUserId( loggingUserId );
					userCredentials.setWindowName( windowName );
					userCredentials.setSession( session );
					userCredentials.setSessionId( sessionId );
					userCredentials.setServletContext( this.getServletContext() );
					session.setAttribute("userCredentials",userCredentials);
          
          alreadyLoggedIn = false;
          //Included by Shyam for DHL for 13213
          
       
				}
		
			} 
/*			catch(InvalidUserException iu) 
            {
				request.setAttribute("message","Failed : Un-authorised User.");
				Logger.error(fileName," Un-authorised User : "+userId+" : "+iu);
				errorMessage = iu.getMessage();
			} 
*/            
            catch(FoursoftException fse) 
            {
				errorMessage = fse.getMessage();
				System.out.println("errorMessage="+errorMessage);

				if(errorMessage==null)
				errorMessage="Invalid Username / Password / Location Id.";

				isAuthorised = false;
			} 
            catch(Exception e) 
            {
				errorOccurred = true;
//				Logger.error(fileName," Un-authorised User : Not Enough Credentials : "+userId, e);
				logger.error(fileName+" Un-authorised User : Not Enough Credentials : "+userId+ e);
				errorMessage = "Un-authorised User : Not Enough Credentials.";
			}

			try 
            {
            
				if(!errorOccurred ) 
                {
			
					if(!isAuthorised || alreadyLoggedIn) 
                    {
						String attempts ="";  
						
						if(!isAuthorised)
						{
								if(!errorMessage.equals("User Locked, reset with Forgot Password."))
								{
										attempts = (String)session.getAttribute(userId);
										int attemptsSet = accessDelegate.getNoFailAttempts();

										if(attempts==null)
										{
										attempts = "a"; 
										session.setAttribute(userId,attempts);
										errorMessage = errorMessage+" "+(attemptsSet-attempts.length())+" chance(s) left.";
										}
										else
										{
											

											if(attemptsSet<0)
												attemptsSet=1;

											if(attempts.length()>(attemptsSet-2))
											{
											boolean status= accessDelegate.lockUser(userId, locationId);
												if(status=true)
												{
												errorMessage="User Locked, reset with Forgot Password";
												session.removeAttribute(userId);
												}	
											}
											else
											{
											attempts += "a";	
											session.setAttribute(userId,attempts);
											errorMessage = errorMessage+" "+(attemptsSet-attempts.length())+" chance(s) left.";
											}
										}
								}
						
						}

						actionPage	= "ESupplyLogin";
						request.setAttribute("Login_Message",errorMessage);
						
					} 
                    else 
                    {
                    
						boolean isExpired = accessDelegate.getPasswordModifiedDate(userId, locationId);
						boolean isWarningLimitCrossed = accessDelegate.getWarningCountExceeded(userId, locationId);
						boolean isFirstLogin=accessDelegate.getLoginCount(userId, locationId);
            
						if(isWarningLimitCrossed){
						accessDelegate.lockUser(userId, locationId);
						}
						
						if(isFirstLogin){
						session.setAttribute("firstLogin",new Boolean(isFirstLogin));
						}
						
						session.setAttribute("userPasswordValidation", new Boolean(isExpired));
						session.setAttribute("warningLimitCrossed", new Boolean(isWarningLimitCrossed));
            
						if(globalParameteres.getTimeZone() != null) 
                        {		
							Calendar gc = Calendar.getInstance();
							TimeZone tz = TimeZone.getDefault(); // default TimeZone
							TimeZone t = TimeZone.getTimeZone(globalParameteres.getTimeZone()); // userTimezone
							// def TimeZone offset 		
							int serverOffset = tz.getOffset(Calendar.ERA, Calendar.YEAR, Calendar.MONTH, Calendar.DATE,Calendar.DAY_OF_WEEK,Calendar.MILLISECOND);
							// userTimeZone offset
							int localOffset = t.getOffset(Calendar.ERA, Calendar.YEAR, Calendar.MONTH, Calendar.DATE,Calendar.DAY_OF_WEEK,Calendar.MILLISECOND);
							int relativeOffset	= localOffset - serverOffset;
							globalParameteres.setRelativeOffset(relativeOffset);
				
						} 
                        else 
                        {
							globalParameteres.setRelativeOffset(0);
						}
	
						//Logger.info(fileName,"Terminal Local Time "+globalParameteres.getLocalTime() );
						session.setAttribute("loginbean", globalParameteres);
						session.setAttribute("accessList",permissionsList);
						session.setAttribute("shipmentMode",shipmentMode);
            
						//Logger.warning(fileName, "User '"+loggingUserId+"' Logged In. The Session ["+session.getId()+"]' Started at "+new java.util.Date()+".");
            logger.warn(fileName+ "User '"+loggingUserId+"' Logged In. The Session ["+session.getId()+"]' Started at "+new java.util.Date()+".");

						accessType = globalParameteres.getAccessType();
						session.setAttribute("ACCESSTYPE",accessType);
												
						//BundleFile bundle = (BundleFile)session.getAttribute("bundleFile");
						BundleFile bundle = null;

						if (bundle == null)
						{
							//String usedLanguage = globalParameteres.getUserPreferences().getLanguage();
							/*bundle =  new BundleFile();
							bundle.setLanguage(globalParameteres.getUserPreferences().getLanguage());
							session.setAttribute("bundle",bundle);*/
              //System.out.println("globalParameteres.getUserPreferences().getLanguage() : "+globalParameteres.getUserPreferences().getLanguage());
							bundle =  new BundleFile();
							bundle.setLanguage(globalParameteres.getUserPreferences().getLanguage());
							getServletConfig().getServletContext().setAttribute("bundle",bundle);
						}
					}

				} 
                else 
                {
					throw new Exception();
				}
			} 
            catch(Exception ex) 
            {
				inValidateSession( request );
//				Logger.error(fileName, "Log-in process of User '"+loggingUserId+"' was terminated as an error occurred while logging in.",ex);				
				logger.error(fileName+ "Log-in process of User '"+loggingUserId+"' was terminated as an error occurred while logging in."+ex);				
				errorMessage	=	"An error occurred in the Log-in process. Please try again.";
				actionPage	= "ESupplyLogin";
				request.setAttribute("Login_Message",errorMessage);
			}

		
			try 
            {
				doDispatcher( request, response, actionPage);
			} 
            catch(IOException ioEx) 
            {
				inValidateSession( request );
			} 
            catch(ServletException sevEx) 
            {
				inValidateSession( request );
			}

		} // synchronized
	}

	private void doDispatcher(HttpServletRequest request, HttpServletResponse response, String forwardFile) 
					throws IOException, ServletException
	{
		try
		{
			RequestDispatcher rd = request.getRequestDispatcher(forwardFile);
			rd.forward(request, response);
		}
		catch(Exception ex)
		{
			inValidateSession( request );
            ex.printStackTrace();
//			Logger.error(fileName, " [doDispatcher() ", " Exception in forwarding to "+forwardFile, ex);
			logger.error(fileName+ " [doDispatcher() "+ " Exception in forwarding to "+forwardFile+ ex);
		}
	}

	private void inValidateSession( HttpServletRequest request ) 
	{
		try 
		{
			if(request!=null) 
			{
				HttpSession		session	=	request.getSession();
				if(session!=null) 
				{
					session.invalidate();
//					Logger.error(fileName, "Log-in process was terminated as an error occurred while logging in.");				
					logger.error(fileName+ "Log-in process was terminated as an error occurred while logging in.");				
				}
			}
		} 
		catch(Exception ex) 
		{
		}
	}

	//private ArrayList parse(java.io.File file)
  private HashMap parse(java.io.InputStream file)
	{
		ArrayList list = new ArrayList();
    HashMap   langs = new HashMap();
		Document xmlDocument;
		DocumentBuilderFactory factory;
		DocumentBuilder builder;
		Element ele;

    Node packageNode; 
		Node languageList; 
		Node language;
		try 
		{
			factory = DocumentBuilderFactory.newInstance();
			builder = factory.newDocumentBuilder();
			xmlDocument = builder.parse(file);
			ele = xmlDocument.getDocumentElement();
			if (ele.getNodeName().equals("language-list") && ele.hasChildNodes())
			{
				//for (int i=0;i<ele.getChildNodes().getLength();i++)   //Package List
        for (Node child = ele.getFirstChild();child != null;child = child.getNextSibling())
				{
					/*packageNode = ele.getChildNodes().item(i);
					if (packageNode.getNodeName().equals("name"))
						list.add(packageNode.getChildNodes().item(0).getNodeValue());*/
          if (child.getNodeType() == child.ELEMENT_NODE)
          {
           if(child.getNodeName().equalsIgnoreCase("language"))
            {
              String name  = null;
              String value = null;
              
              NodeList nls = child.getChildNodes();							
              for (int i=0;i<nls.getLength();i++)
               {
                 Node nd = nls.item(i);
                 if(nd.getNodeType() == child.ELEMENT_NODE)
                 {
                  if(null!=nd.getFirstChild())
                   {
                     if(nd.getNodeName().equalsIgnoreCase("name"))
                     {
                      name = nd.getFirstChild().getNodeValue();
                      //System.out.println("name ::: "+name);
                     }
                     else if(nd.getNodeName().equalsIgnoreCase("value"))
                     {
                      value = nd.getFirstChild().getNodeValue();                       
                      langs.put(name,value);
                      //System.out.println("name ::: "+name+" -- value :::"+value);
                      name = null;
                      value = null;
                     }						  
                   }
                 }
               }
             }			  
            }
				}
			}
		}
		catch(Exception exp)
		{
			System.out.println("Exception while parsing "+exp.toString());
		}
		return langs;
	}

}