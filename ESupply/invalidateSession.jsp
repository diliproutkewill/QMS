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
 % File			: ESMenuIndex.jsp
 % sub-module	: AccessControl
 % module		: esupply
 %
 % This is a simple frame set
 % 
 % author		: Sasi Bhushan. P
 % date			: 22-12-2001
 % Modification  history
 % Amit Parekh		24/10/2002		Logout functionality changed to handle higher level even handling
 % Amit Parekh		19/11/2002		Changes made to handle facility switching in EP for a COMPANY User and
 %									warehouse customer location switching for a WAREHOUSE level User in ELOG
 % Amit Parekh		27/02/2003		Change sone to make switching of warehouses/locations and thus roles possible in SP.
 % Amit Parekh		06/05/2003		This file's method is now synchronized as it handles (creates or manipulates) an application level object
--%>
<%@ page import= "java.io.PrintWriter, java.io.IOException, java.util.Hashtable, org.apache.log4j.Logger,com.foursoft.esupply.common.bean.ESupplyGlobalParameters, com.foursoft.esupply.common.java.ErrorMessage, com.foursoft.esupply.common.bean.UserCredentials, com.foursoft.esupply.common.util.BundleFile" %>
<%!
      private static Logger logger = null;
			private static final String fileName		= "invalidateSession.jsp";	%>
<%				
      logger  = Logger.getLogger(fileName);
			String	from = request.getParameter("from");

			String	userId		=	"";
			String	locationId	=	"";
			String	userType	=	"";
			String	sessionId	=	"";
			String	message		=	"";

			
			Hashtable	appSettings	=	null;

			try {

				if(request!=null) {
					session = request.getSession(false);
				}
			
				if(from.equals("unlog")) {

					sessionId	= (String) session.getId();

					String	ssnId	=	request.getParameter("user");				

					if(sessionId.equals(ssnId)) 
					{
						message = ((com.foursoft.esupply.common.util.BundleFile) session.getAttribute("bundle")).getBundle().getString("100522");
					} else {
					
						appSettings	=	(Hashtable) this.getServletConfig().getServletContext().getAttribute("appSettings");

						if(appSettings.containsKey( ssnId )) {

							UserCredentials	uc	=	(UserCredentials) appSettings.get( ssnId );

							if(uc!=null) {
								String	lockedUserId = new String( uc.getUserId() );

								HttpSession	removedSession	=	uc.getSession();
							
								if(removedSession!=null) {
									removedSession.invalidate();
								} else {
									appSettings.remove( ssnId );
								}

								//Logger.warning(fileName, "User '"+lockedUserId+"' had to be Logged Out externally. The session['"+ssnId+"'] was invalidated.");
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
						}
					}

				}

				doDispatcher( request, response, "ESLoginController" );
			
			} catch (Exception ex)  {
				//Logger.error(fileName, "Error While Logging Out :\n", ex);
        logger.error(fileName+ "Error While Logging Out :\n"+ ex);
				
			} finally  {			
				try {
					if(session != null && !from.equals("unlog")) {
						session.invalidate();
						//Logger.warning(fileName, "User '"+userId+"/"+locationId+"/"+userType+"' Explicitly Logged Out.");
            logger.warn(fileName+ "User '"+userId+"/"+locationId+"/"+userType+"' Explicitly Logged Out.");
					} 
				} catch (Exception ex)  {
					//Logger.error(fileName, "Error on explicit logging out by User '"+userId+"/"+locationId+"/"+userType+"':\n", ex);
          logger.error(fileName+ "Error on explicit logging out by User '"+userId+"/"+locationId+"/"+userType+"':\n"+ ex);
				}
			}

	%>			
<%!
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
				//Logger.error(fileName, " [doDispatcher() ", " Exception in forwarding to "+forwardFile, ex);
        logger.error(fileName+ " [doDispatcher() "+ " Exception in forwarding to "+forwardFile+ ex);
		}
	}
				
%>