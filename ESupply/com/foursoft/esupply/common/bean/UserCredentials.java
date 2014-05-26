/**
 * 
 * Copyright (c) 2000-2001 by FourSoft, Inc. All Rights Reserved.
 * This software is the proprietary information of FourSoft, Pvt Ltd.
 * Use is subject to license terms.
 *
 * esupply - v 1.x 
 *
 */
package com.foursoft.esupply.common.bean;

import java.sql.Timestamp;
import java.util.Vector;
import java.util.Hashtable;
import java.util.Date;
import javax.servlet.http.HttpSessionBindingListener;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.ServletContext;
import com.foursoft.esupply.accesscontrol.exception.AlreadyExistsException;
//import com.foursoft.esupply.common.util.Logger;
import org.apache.log4j.Logger;
import javax.servlet.*;
import javax.servlet.http.*;

/**
* File			: UserCredentials.java
* sub-module 	: Access Control
* module 		: esupply
* 
* This is used eliminate the 2 users logged in at the same time 
* It implements the HttpSessionBinding Listner
* 
* 
* @author	Madhusudhan Rao. P, 
* @date	28-08-2001
* Modification History
* Amit				14.10.2002		The User will now be removed from the list of concurrent Users by this object itself.Module Indicator and Shipment mode are referring the FoursoftConfig class
* 									In other words, this object will remove its own reference from the concurrent Users
*                   				list when it is unbound from the session
* Amit Parekh		01/11/2002		In case the User gets locked in the application upon losing connection
* 									or error in logout process we have to invalidate his session. For this
*									purpose now a handle to his session is kept in his UserCredentials object
*									which is used to log him out in case he gets locked. His session will be
*									invalidated just like a normal session with no noticeable difference.
*									Upon invalidation of a User's session his UserCredentials object
*									will remove itself from the application level Hashtable i.e. the
*									Logged-In User List.
* Amit Parekh		07/05/2003		A getter method was put for the session id string value.
*/
public class UserCredentials implements java.io.Serializable,HttpSessionBindingListener
{
	private static final String	fileName	=	"UserCredentials.java";
	private	String	windowName;
	private String	sessionId;
	private	transient HttpSession	userzSession;//transient added by Sanjay to restrict this variable from Serialization
	private String	userId; // this is the combinationation of userId.locationId
	private transient ServletContext context; // servlet context used to store the reference of the context
                                            //transient added by Sanjay to restrict this variable from Serialization
	private static Logger logger = null;
	/*
	* Empty argument constructor 
	*/
	public UserCredentials() 
	{
    logger  = Logger.getLogger(UserCredentials.class);
	}
	
	/*
	* this is getter method to set the userId
	* @return String
	*/
	public String getUserId()
	{
		return userId;
	}

	public String getWindowName()
	{
		return windowName;
	}

	public HttpSession getSession()
	{
		return this.userzSession;
	}
	
	public String getSessionId()
	{
		return this.sessionId;
	}
	
	/*
	* the is used to set the servlet context
	* @return void
	* @param ServletContext
	*
	* @see ServletContext
	*/
	public void setServletContext( ServletContext context )
	{
		this.context = context;
	}
	/*
	* this is setter method to set the userId
	* @return void
	* @param userId
	*/
	public void setUserId( String newUserId )
	{
		this.userId = newUserId;
	}

	public void setWindowName( String windowName )
	{
		this.windowName = windowName;
	}

	public void setSessionId( String sessionId )
	{
		this.sessionId = sessionId;
	}

	public void setSession( HttpSession theSession )
	{
		this.userzSession = theSession;
	}

	/*
	* this is callback method which is called, when the object is about to set to the session
	* this is used to track the current users
	* As and when logged into the site this object will be be set into the session
	* In the first time it creates the a vector for storing the current-users and will kept in ServletContext
	* later on this object will be added to the current-user which is in ServletContext
	* 
	* @param event	HttpSessionBindingEvent 
	*
	* @see HttpSessionBindingListner
	*/
	public void valueBound( HttpSessionBindingEvent event )
	{

		try {
			Hashtable appSettings = (Hashtable) context.getAttribute( "appSettings" );
			
			int	nosOfUsersBeforeAddingToList	= 0;
			int	nosOfUsersAfterAddingToList		= 0;

			//Logger.info(fileName,"Credentials object is : "+this);
      logger.info(fileName+"Credentials object is : "+this);

			if(appSettings==null) {
				appSettings	=	new Hashtable();
				context.setAttribute( "appSettings", appSettings);
			} 

			nosOfUsersBeforeAddingToList		=	appSettings.size();

			appSettings.put( this.sessionId, this );

			nosOfUsersAfterAddingToList		=	appSettings.size();

			//Logger.info(fileName,"Logged-In Users :- Before adding = "+ nosOfUsersBeforeAddingToList+" : After adding = "+nosOfUsersAfterAddingToList);
      logger.info(fileName+"Logged-In Users :- Before adding = "+ nosOfUsersBeforeAddingToList+" : After adding = "+nosOfUsersAfterAddingToList);

		} catch (Exception ex)  {
			//Logger.error(fileName,"Error while binding UserCredentials to session.");
      logger.error(fileName+"Error while binding UserCredentials to session.");
		}
	}

	/*
	* this is callback method which is called, when the object is about to remove from the session or
	* session is invalidated
	* this is used to track the current users
	* Here the object kept in session will be removed from the servlet context
	* 
	* 
	* @param event	HttpSessionBindingEvent 
	*
	* @see HttpSessionBindingListner
	*/
	public void valueUnbound( HttpSessionBindingEvent event )
	{
		try  {

			Hashtable appSettings = (Hashtable) context.getAttribute( "appSettings" );

			int	nosOfUsersBeforeRemovingFromList	=	appSettings.size();
				
			appSettings.remove( this.sessionId );

			int	nosOfUsersAfterRemovingFromList	=	appSettings.size();
	
			//Logger.info(fileName,"Logged-In Users :- Before removing = "+ nosOfUsersBeforeRemovingFromList +" : After removing "+nosOfUsersAfterRemovingFromList);
      logger.info(fileName+"Logged-In Users :- Before removing = "+ nosOfUsersBeforeRemovingFromList +" : After removing "+nosOfUsersAfterRemovingFromList);

			//Logger.warning(fileName, "User '"+userId+"' Session ['"+this.sessionId+"'] ended at "+new java.util.Date()+".");
      logger.warn(fileName+ "User '"+userId+"' Session ['"+this.sessionId+"'] ended at "+new java.util.Date()+".");
			
		} catch (Exception ex)  {
			//Logger.error(fileName,"Error while unbinding UserCredentials from session.");
      logger.error(fileName+"Error while unbinding UserCredentials from session.");
		}
	}
	
	
	/*
	* this overriding of toString()
	*/
	public String toString()
	{
		return userId;
	}
	
}
