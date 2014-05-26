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
 * File			: ESLogout.java
 * sub-module	: AccessControl
 * module		: esupply
 *
 * Earlier the functionality in this Servlet existed in the file ESACLogout.jsp but for
 * purposes regarding to access to sensitive source code, the same functionality now exists as this
 * Servlet. The class file of this Servlet should be desireably obfuscated before use.
 * 
 * This is used to invalidate the User's session and remove him from the list of
 * concurrently logged in Users.
 * 
 * author		: Amit Parekh
 * date			: 12-10-2002
 * Modified History
 * Modifed By		Modified Date		Reason
 * Amit Parekh		21/10/2002			This file now also removes logged in Users from the
 *										application if a high-level User (admin/licensee) wants to
 *                                      log-off a particular User in case his session is blocked
 *                                      due to a power failure at the client-side or if the User gets
 *                                      disconnected due to a fault in network connection or an error
 *                                      takes place in the logout process.
 * Amit Parekh		24/10/2002			Logout functionality changed to handle higher level even handling
 *
 * Amit Parekh		01/11/2002			In case the User was locked in and had to be logged out externally,
 * 										this file now handles the functonality to get a handle to the Users
 *                                      session from his credentials and invalidate it just as if it were its
 *                                      own session. There is no noticable difference in implemenmtation.
 *                                      Upon invalidation of a User's session his UserCredentials object
 *                                      will remove itself from the application level Hashtable i.e. the
 *                                      Logged-In User List.
 * Amit Parekh		28/02/2003			The Single Thread Model interface was removed. The service method is now
 * 										synchronized (inner code block) to handle only one Logout at a time.
 * Amit Parekh		07/05/2003			Before removing a locked User, his session is now checked for null reference
 * Amit Parekh		22/05/2003			If a Usercredential object is in hashtable and his session is null, it can be
 *										now removed from the 'Remove User' interface.
 */

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.Hashtable;

//import com.foursoft.esupply.common.util.Logger;
import org.apache.log4j.Logger;
import com.foursoft.esupply.common.bean.ESupplyGlobalParameters;
import com.foursoft.esupply.common.java.ErrorMessage;
import com.foursoft.esupply.common.bean.UserCredentials;
import com.foursoft.esupply.common.util.BundleFile;
import com.foursoft.esupply.delegate.AccessControlDelegate;

public final class ESLogout extends HttpServlet {

	private static final String CONTENT_TYPE	= "text/html";
	private static final String fileName		= "ESLogout.java";
	private static Logger logger = null;
	
	public void init(ServletConfig config) throws ServletException 
	{
		logger  = Logger.getLogger(ESLogout.class);
		super.init(config);
	}

	public void service(HttpServletRequest request, HttpServletResponse response)
	{

		synchronized (this) {		
			String	from = request.getParameter("from");

			String	userId		=	"";
			String	locationId	=	"";
			String	userType	=	"";
			String	sessionId	=	"";
			String	message		=	"";
      

			HttpSession	session		=	null;
			Hashtable	appSettings	=	null;

			try {

				if(request!=null) {
					session = request.getSession(false);
				}
				System.out.println("from...."+from);
				if(from.equals("unlog")) {

					sessionId	= (String) session.getId();

					String	ssnId	=	request.getParameter("user");				

					if(sessionId.equals(ssnId)) 
					{
						message = ((com.foursoft.esupply.common.util.BundleFile) session.getAttribute("bundle")).getBundle().getString("100522");
					} else {
					
						appSettings	=	(Hashtable) this.getServletContext().getAttribute("appSettings");

						if(appSettings.containsKey( ssnId )) {

							UserCredentials	uc	=	(UserCredentials) appSettings.get( ssnId );

							if(uc!=null) {
								String	lockedUserId =  uc.getUserId() ;

								HttpSession	removedSession	=	uc.getSession();
							
								if(removedSession!=null) {
									removedSession.invalidate();
								} else {
									appSettings.remove( ssnId );
								}

//								Logger.warning(fileName, "User '"+lockedUserId+"' had to be Logged Out externally. The session['"+ssnId+"'] was invalidated.");
								logger.warn(fileName+ "User '"+lockedUserId+"' had to be Logged Out externally. The session['"+ssnId+"'] was invalidated.");

								message = ((com.foursoft.esupply.common.util.BundleFile) session.getAttribute("bundle")).getBundle().getString("100523")+"'"+lockedUserId+"' "+((com.foursoft.esupply.common.util.BundleFile) session.getAttribute("bundle")).getBundle().getString("100524");
							} else {
								message = ((com.foursoft.esupply.common.util.BundleFile) session.getAttribute("bundle")).getBundle().getString("100525");
							}
					
						} else {
							message = ((com.foursoft.esupply.common.util.BundleFile) session.getAttribute("bundle")).getBundle().getString("100525");
						}	
					}
			
				} else {
	
					if(session != null) {

						userType	= new String ((String) session.getAttribute("userType"));
						sessionId	= new String( (String) session.getId() );
            
            
		
						ESupplyGlobalParameters loginbean = (ESupplyGlobalParameters) session.getAttribute("loginbean");
						if(loginbean != null) {
							userId		=	loginbean.getUserId();
							locationId	=	loginbean.getLocationId();	
							//@@Added for no'of users logged
							AccessControlDelegate accessDelegate = null;
							accessDelegate = new AccessControlDelegate();
							boolean b	=	accessDelegate.logoutUpdate(userId, locationId);
							if(!b)
								throw new Exception("Error While Update the logoutTime in QMS_USERLOG");
							//@@ended for no'of users logged
						}
					}

				}

				sendResponse( request, response, from, message );
			
			} catch (Exception ex)  {
//				Logger.error(fileName, "Error While Logging Out :\n", ex);
				logger.error(fileName+ "Error While Logging Out :\n"+ ex);
				try {
					if(!from.equals("unlog")) {
						sendResponse( request, response, from, message );
					}
				} catch(Exception e) {}
			} finally  {			
				try {
					if(session != null && !from.equals("unlog")) {
						session.invalidate();
//						Logger.warning(fileName, "User '"+userId+"/"+locationId+"/"+userType+"' Explicitly Logged Out.");
						logger.warn(fileName+ "User '"+userId+"/"+locationId+"/"+userType+"' Explicitly Logged Out.");
					} 
				} catch (Exception ex)  {
//					logger.error(fileName, "Error on explicit logging out by User '"+userId+"/"+locationId+"/"+userType+"':\n", ex);
					logger.error(fileName+ "Error on explicit logging out by User '"+userId+"/"+locationId+"/"+userType+"':\n", ex);
				}
			}

		}
	}

	private void sendResponse( HttpServletRequest request, HttpServletResponse response, String from, String message ) throws IOException {
	
		response.setHeader("Cache-Control","no-cache"); // HTTP 1.1
		response.setHeader("Pragma","no-cache"); // HTTP 1.0
		response.setDateHeader ("Expires", -1); // Prevents caching at the proxy server
		response.setContentType( CONTENT_TYPE );
		PrintWriter	out	=	response.getWriter();
     String	NL = "\n";
		if(!from.equals("unlog")) {
       
			out.write(
						"<html><link rel=\"stylesheet\" href=\"ESFoursoft_css.jsp\"><head>"+
						//"<script language=\"javascript\" src=\"/eventhandler.js\">"+
						"<script language=\"javascript\" >"+
            "function newWindow(userType){ "+NL+NL
            +"var windowFeatures	=	'toolbar=0,status=1,location=no,menubar=no,directories=no,scrollbars=yes,resizable=yes'; "+NL+NL
								+"	// If winName is not passed just reuse current windows.name; "+NL
								+"	var millisecs	=	(new Date()).getTime(); "+NL
								+"	var	winName		=	'LoginAt'+millisecs; "+NL
								+"	var newWin = window.open('ESupplyLogin?userType='+userType+'&windowName='+winName ,winName, windowFeatures); "+NL					  
								+"	newWin.resizeTo(screen.availWidth,screen.availHeight) "+NL
								+"	newWin.focus(); "+NL
								+"	newWin.moveTo(0,0); "+NL
                +" this.close();"
								+"}"+NL+NL
						+"function maximizeWin() {"+
						//"//if (window.screen) {"+
						//"//var aw = 400;\n"+
						//"//var ah = 260;\n"+
						//"//window.moveTo(360, 200);\n"+
						//"//window.resizeTo(aw, ah);\n"+
						//"}//}"+
						"window.location.href=\"html/Signedout.html\";"+
						"}"+
						"</script>"+
						"</head>"+
						"<body  onload=\"newWindow('ETS')\">"+
						"</body>"+
						"</html>"
						);

			out.flush();
			out.close();
		} else {
			try {
				ErrorMessage errMsg = new ErrorMessage( message, "ESACLoggedUsers.jsp");

				request.setAttribute("errorMessage", errMsg); 
			
				RequestDispatcher rd = request.getRequestDispatcher("ESupplyMessagePage.jsp");

				rd.forward(request, response);
				
			} catch(Exception e) {
//				Logger.error(fileName,s "Error while forwarding logged out message.");
				logger.error(fileName+ "Error while forwarding logged out message.");
			}
		}
	}
}