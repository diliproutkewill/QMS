/**
 * 
 * Copyright (c) 2000-2001 by FourSoft, Inc. All Rights Reserved.
 * This software is the proprietary information of FourSoft, Pvt Ltd.
 * Use is subject to license terms.
 *
 * esupply - v 1.x 
 *
 */
package com.foursoft.esupply.accesscontrol.ejb.sls;

import com.foursoft.eaccounts.common.java.AccountsCredentials;
import com.foursoft.elog.common.java.ELogCredentials;
import com.foursoft.esupply.accesscontrol.exception.InvalidUserException;
import com.foursoft.esupply.common.bean.ESupplyGlobalParameters;
import com.foursoft.esupply.common.bean.UserPreferences;
import com.foursoft.esupply.common.exception.FoursoftException;
import com.foursoft.esupply.common.exception.DBSysException;
import com.foursoft.esupply.common.java.FoursoftConfig;
import com.foursoft.esupply.common.java.LookUpBean;
import com.foursoft.esupply.common.util.ConnectionUtil;
//import com.foursoft.esupply.common.util.Logger;
import org.apache.log4j.Logger;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.HashMap;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import sun.misc.BASE64Encoder;
import java.io.UnsupportedEncodingException;

import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;

import javax.naming.NamingException;

import javax.sql.DataSource;


/**
 * File			: AccessControlSessionBean.java
 * module 		: esupply
 * sub-module	: AccessControl
 *
 *
 * This is used for AccessControl Class.
 * It validates the user id, password
 * It retrieves the User's credentials
 * It retirves the allowed Previllages
 * arrays. It is used to serch a String in String Arry
 *
 *
 * @author	Madhusudhan Rao. P,
 * @date	28-08-2001
 *
 * Modified History 
 * 2002-05-07	Madhusudhana Rao. P added isValidLocation() to avoid dangling users to login into the system
 * 2002-05-23	Madhusudhana Rao. P changed preapreAccountsCredentials() to incorporate GATEWAY acct Id setup
 * 2002-08-09	Amit Parekh         changed mehods to incorporate GATEWAY access type
 * 2002-09-11	Amit Parekh       	changed mehods to incorporate ELOG currency id
 * 2002-10-21	Amit Parekh			The global datasource name 'jdbc/DB' or otherwise is now
 *									maintained globally in FourSoftConfig.java
 *                                  Also the Home lookup-once-and-cache pattern is implemented
 * 2002-10-24	Amit Parekh         Actual User Level is now cheched in all global parameters with the
 * 									one claimed at the time of login e.g. EPCRM cannot login as COMPANY and
 *                                  vice versa. Ditto for other prodct modules
 * 2002-11-07	Amit Parekh         User Preferences object is now initialized for all types of Users i.e.
 * 									ESP, ETS, ELG, EEP, ETC, ELC, EPC, EPT, ELV, EPV, etc
 * 2002-11-19	Amit Parekh			Updated to handle optional roles for a COMPANY User for EP and
 * 									a WAREHOUSE User for ELOG. A new table FS_OPTIONALROLES was added
 *                                  in the database for these changes. also bug fizes for location id with only
 *                                  less than 3 characters also made. This file now switches the role permissions
 *                                  on the fly for a User accredited with the optional role.
 * 2002-12-11	Amit Parekh			Handles the HO access type for ELOG. The method for preparing elog credentials was
 *									modified to populate the Company Ids for the HO. Also the Curreny for the HO
 *                                  will be populated from the table FS_LG_HOMASTER as it is already defined there.
 * 2004-3-27   Purushotham			incorporated features to encrypt password,lock user, unlock user and is locked.
 */

public class AccessControlSessionBean implements SessionBean
{
	private		SessionContext		sessionContext	= null;
	private		transient			Connection	connection;
    private		transient			DataSource	datasource	=	null;
	private		transient			LookUpBean	lookUpBean	=	null;

	private		boolean				VERBOSE			= true;
	private	static final String		FILE_NAME		= "AccessControlSessionBean";
	private static Logger logger = null;
 
	private static final String		ESPQuery =
										"SELECT "+
										"	FS_ROLEMASTER.ROLEID, "+
										"	FS_ROLEMASTER.LOCATIONID, "+
										"	FS_ROLEMASTER.ROLELEVEL, "+
										"	FS_USERMASTER.USER_LEVEL, "+
                    "	FS_USERMASTER.EMPID "+
										"FROM "+
										"	FS_ROLEMASTER, FS_USERROLES, FS_USERMASTER "+
										"WHERE "+
										"	FS_USERROLES.USERID = ? AND "+
										"	FS_USERROLES.LOCATIONID = ? AND "+
										"	FS_USERROLES.USERID			 = FS_USERMASTER.USERID AND "+
										"	FS_USERROLES.LOCATIONID		 = FS_USERMASTER.LOCATIONID AND "+
										"	FS_USERROLES.ROLEID			 = FS_ROLEMASTER.ROLEID AND "+
										"	FS_USERROLES.ROLE_LOCATIONID = FS_ROLEMASTER.LOCATIONID";

	private static final String		UserPrefQuery	=
										"SELECT "+
										"	PARAM_NAME, "+
										"	PARAM_VALUE "+
										"FROM "+
										"	FS_USER_PREFERENCES "+
										"WHERE "+
										"	USERID = ? AND LOCATIONID = ? ";
 
	//Place your business methods here.

	/**
	* It is used to fetch the User's credentials
	* First it validates the user
	* Retrives the role
	* Retrieve the other credentials
	*
	* @param userId		- userId
	* @param password	- password
	* @param locationId	- user's location(terminal/custwh/project)
	* @exception InvalidUserException
	*
	* @return ESupplyGlobalParameters	- this Object holds the Credentials of the User
	*
	*/
	public ESupplyGlobalParameters	getESupplyGlobalParameters(String userId, String password, String locationId,String userType) throws InvalidUserException, FoursoftException
	{
		ESupplyGlobalParameters globalParameters	=  null;
		String terminalId	= null;
		String projectId	= null;
		String custWHId		= null;
		
		String USER_LEVEL = null;
		String ROLE_LEVEL = null;
    String EMP_ID     = null;
		try
	  	{
        	makeConnection();

			if( !isValidUser(userId, password, locationId) )
			{
					if(isUserLocked(userId,locationId))
					{
					//Logger.warning(FILE_NAME,"getESupplyGlobalParameters(String userId, String password, String locationId,String userType)","Not Authorised User ");
          logger.warn(FILE_NAME+" getESupplyGlobalParameters(String userId, String password, String locationId,String userType)"+"Not Authorised User ");
					throw new InvalidUserException("User Locked, reset with Forgot Password.");
					}
					else
					{
					//Logger.warning(FILE_NAME,"getESupplyGlobalParameters(String userId, String password, String locationId,String userType)","Not Authorised User ");
          logger.warn(FILE_NAME+"getESupplyGlobalParameters(String userId, String password, String locationId,String userType)"+"Not Authorised User ");
					throw new InvalidUserException("Invalid Username / Password / Location Id.");
					}
			}
			
			if(isUserLocked(userId,locationId))
			{
					//Logger.warning(FILE_NAME,"getESupplyGlobalParameters(String userId, String password, String locationId,String userType)","Not Authorised User ");
          logger.warn(FILE_NAME+"getESupplyGlobalParameters(String userId, String password, String locationId,String userType)"+"Not Authorised User ");
					throw new InvalidUserException("User Locked, reset with Forgot Password.");
			}

			globalParameters	=  new ESupplyGlobalParameters(userId, locationId, userType, FoursoftConfig.LICENSEE_ID);
      
      String accessType = globalParameters.getAccessType();
     // Logger.info(FILE_NAME,"accessType::"+accessType);
      

      
			PreparedStatement pstmt = connection.prepareStatement(ESPQuery);
			pstmt.setString(1, userId);
			pstmt.setString(2, locationId);

			ResultSet rs = pstmt.executeQuery();
			
			if(rs.next())
			{
				globalParameters.setRoleId(rs.getString(1));
				globalParameters.setRoleLocationId(rs.getString(2));
				ROLE_LEVEL = rs.getString(3);
				USER_LEVEL = rs.getString(4);
        EMP_ID     = rs.getString(5);
				globalParameters.setAccessType(ROLE_LEVEL);
				globalParameters.setUserLevel(USER_LEVEL);
        globalParameters.setEmpId(EMP_ID);
  		}
			if(rs != null)
				rs.close();
			if(pstmt != null)
				pstmt.close();

			// Here check the User's registered ROLE LEVEL against the one claimed by him
			// during login

			String	actualRoleLevel	=	globalParameters.getUserLevel();

			if(	actualRoleLevel.equals("EPCRM") ||
				actualRoleLevel.equals("EPVRM") ||
				actualRoleLevel.equals("EPTRM") ||
				actualRoleLevel.equals("ELCRM") ||
				actualRoleLevel.equals("ELVRM") || 
				actualRoleLevel.equals("ELTRM") ) 
			{
				//Logger.warning(FILE_NAME,"User '"+userId+"/"+locationId+"' having actual Role Level '"+actualRoleLevel+"' tried to Login as an '"+userType+"' User.");
        logger.warn(FILE_NAME+" User '"+userId+"/"+locationId+"' having actual Role Level '"+actualRoleLevel+"' tried to Login as an '"+userType+"' User.");
				throw new InvalidUserException("Invalid Username / Password / Location Id.");
			}
			
			 
			// added the below code to check the user's Location exists are not
			if(globalParameters.getAccessType().equals("OPER_TERMINAL") || globalParameters.getAccessType().equals("HO_TERMINAL") || globalParameters.getAccessType().equals("ADMN_TERMINAL") || globalParameters.getAccessType().equals("AGENT_TERMINAL") || globalParameters.getAccessType().equals("GATEWAY") )
			{
				if(!isValidLocation(locationId) )
				{
					//Logger.warning(FILE_NAME,"getESupplyGlobalParameters(String userId, String password, String locationId,String userType)","Users location is not available ");
          logger.warn(FILE_NAME+" getESupplyGlobalParameters(String userId, String password, String locationId,String userType)"+"Users location is not available ");
					throw new InvalidUserException("Invalid Username / Password / Location Id.");
				}

			}

			globalParameters.setUserPreferences( getUserPreferences( userId, locationId ) );
			
			if(userType.equalsIgnoreCase("ESP"))
			{
               // System.out.println("In ESP Block globalParameters.getAccessType()="+globalParameters.getAccessType());
				if(globalParameters.getAccessType().equals("HO_TERMINAL") || globalParameters.getAccessType().equals("ADMN_TERMINAL"))
				{
					globalParameters.setTerminalId(locationId);
					//globalParameters.setAccountsCredentials(prepareAccountsCredentials(locationId, globalParameters.getAccessType()) );
					globalParameters.setCurrencyId(getETransCurrencyId(locationId));
					globalParameters.setTimeZone(getTimeZone(ROLE_LEVEL,locationId));
					globalParameters.setUserTerminalType(getUserTerminalType(locationId) );
					globalParameters.setCompanyId(getCompanyId(locationId, globalParameters.getAccessType()));
					globalParameters	= getELogCredentials(globalParameters, ROLE_LEVEL, locationId);
				}
				else if(globalParameters.getAccessType().equals("OPER_TERMINAL") || globalParameters.getAccessType().equals("AGENT_TERMINAL") || globalParameters.getAccessType().equals("GATEWAY") )
				{
					globalParameters.setTerminalId(locationId);
					//globalParameters.setAccountsCredentials(prepareAccountsCredentials(locationId, globalParameters.getAccessType()) );
					globalParameters.setCurrencyId(getETransCurrencyId(locationId));
					globalParameters.setTimeZone(getTimeZone(ROLE_LEVEL,locationId));
					globalParameters.setUserTerminalType(getUserTerminalType(locationId) );
					globalParameters.setCompanyId(getCompanyId(locationId, globalParameters.getAccessType()));
				}
				else
				{
					globalParameters	= getELogCredentials(globalParameters, ROLE_LEVEL, locationId);
					//globalParameters.setAccountsCredentials(prepareAccountsCredentials(locationId, globalParameters.getAccessType()) );
				}
			}
			else if(userType.equalsIgnoreCase("ETS"))
			{
				if(globalParameters.getAccessType().equals("OPER_TERMINAL") || globalParameters.getAccessType().equals("HO_TERMINAL") || globalParameters.getAccessType().equals("ADMN_TERMINAL") || globalParameters.getAccessType().equals("AGENT_TERMINAL") || globalParameters.getAccessType().equals("GATEWAY") )
				{
					globalParameters.setTerminalId(locationId);
					//globalParameters.setAccountsCredentials(prepareAccountsCredentials(locationId, globalParameters.getAccessType()) );
					globalParameters.setCurrencyId(getETransCurrencyId(locationId));
					globalParameters.setTimeZone(getTimeZone(ROLE_LEVEL,locationId));
					globalParameters.setUserTerminalType(getUserTerminalType(locationId) );
					globalParameters.setCompanyId(getCompanyId(locationId, globalParameters.getAccessType()));
				}
				else if(globalParameters.getAccessType().equals("LICENSEE") )
				{
					globalParameters.setTerminalId( locationId );
				}
				else
				{
					throw new InvalidUserException("Invalid Username / Password / Location Id.");
				}
			}
			else if(userType.equalsIgnoreCase("ELG"))
			{
				globalParameters.setCurrencyId(getETransCurrencyId(locationId));
				globalParameters	= getELogCredentials(globalParameters, ROLE_LEVEL, locationId);

				// Setting HO for ELOG Credentials

				setHOIdForELog(userId,locationId,globalParameters);

				//globalParameters.setAccountsCredentials(prepareAccountsCredentials(locationId, globalParameters.getAccessType()) );
				
				//Logger.warning(FILE_NAME,"Made Elog Credentials - Curr Id = "+globalParameters.getCurrencyId() );
        logger.warn(FILE_NAME+" Made Elog Credentials - Curr Id = "+globalParameters.getCurrencyId() );
			}
			else if(userType.equalsIgnoreCase("EEP"))
			{
				globalParameters	= getEPCredentials(globalParameters, ROLE_LEVEL, locationId);
				
				//globalParameters.setAccountsCredentials(prepareAccountsCredentials(locationId, globalParameters.getAccessType()) );
				
				//Logger.warning(FILE_NAME,"Made Elog Credentials - Curr Id = "+globalParameters.getCurrencyId() );
        logger.warn(FILE_NAME+" Made Elog Credentials - Curr Id = "+globalParameters.getCurrencyId() );
			}
		}
		catch(SQLException sqle)
		{
			//Logger.error(FILE_NAME,"getESupplyGlobalParameters(String userId, String password, String locationId,String userType)","EXCEPTION IS ",sqle);	
      logger.error(FILE_NAME+" getESupplyGlobalParameters(String userId, String password, String locationId,String userType)"+"EXCEPTION IS "+sqle);	
			sessionContext.setRollbackOnly();
            throw FoursoftException.getException(sqle,FILE_NAME,"getESupplyGlobalParameters()");
		}
		catch(DBSysException dbs)
		{
			//throw new EJBException(dbs.getMessage());
            sessionContext.setRollbackOnly();
            throw FoursoftException.getException(dbs,FILE_NAME,"getESupplyGlobalParameters()");
		}
		finally
		{
			ConnectionUtil.closeConnection(connection);
		}

		return globalParameters;
	}  // end of getESupplyGlobalParameters(String userId, String password, String locationId,String userType)
	
	public ESupplyGlobalParameters verifySPCRMVRMUser(String userId, String password, String customerId, String userType) throws InvalidUserException, FoursoftException
	{
		ESupplyGlobalParameters globalParameters	= new ESupplyGlobalParameters();
		PreparedStatement 		pstmt 	= null;
		ResultSet				rs		= null;
		boolean 				flag 	= false;
		String		projectId			= null;
		String		custWHId			= null;
		String 		currencyId			= null;
		String 		partyFlag 			= "";
		String		prjQuery = "";
		String		userName = "";
		String		loginAccessType		= "";
		String		terminalId	=null;
		
		try
		{
			if(userType.equals("ESV")) {
				userName	=	"Vendor";
				loginAccessType	=	"ESVRM";
			} else if(userType.equals("ESC")) {
				userName	=	"Customer";
				loginAccessType	=	"ESCRM";
			}
			
			makeConnection();

			if ( !isValidUser(userId, password, customerId) )
			{
				if(isUserLocked(userId, customerId) )
				{
					//Logger.warning(FILE_NAME,"getESupplyGlobalParameters(String userId, String password, String locationId,String userType)","Not Authorised User ");
          logger.warn(FILE_NAME+" getESupplyGlobalParameters(String userId, String password, String locationId,String userType)"+"Not Authorised User ");
					throw new InvalidUserException("User Locked, reset with Forgot Password.");
				}else
				{
				//Logger.warning(FILE_NAME,"verifySPCRMVRMUser(String userId, String password, String customerId, String userType)","Not Authorised User ");
        logger.warn(FILE_NAME+" verifySPCRMVRMUser(String userId, String password, String customerId, String userType)"+"Not Authorised User ");
				throw new InvalidUserException("Invalid Username / Password / Location Id.");
				}
			}
			if(isUserLocked(userId, customerId) )
			{
					//Logger.warning(FILE_NAME,"getESupplyGlobalParameters(String userId, String password, String locationId,String userType)","Not Authorised User ");
          logger.warn(FILE_NAME+" getESupplyGlobalParameters(String userId, String password, String locationId,String userType)"+"Not Authorised User ");
					throw new InvalidUserException("User Locked, reset with Forgot Password.");
			}

			pstmt = connection.prepareStatement(ESPQuery);

			pstmt.setString(1, userId);
			pstmt.setString(2, customerId);
			
			rs = pstmt.executeQuery();
			
			if(rs.next())
			{
				globalParameters.setRoleId(rs.getString(1));
				globalParameters.setRoleLocationId(rs.getString(2));
				globalParameters.setAccessType(rs.getString(3));
				globalParameters.setUserLevel(rs.getString(4));
			}
			if(rs != null)
				rs.close();
			if(pstmt != null)
				pstmt.close();

			String	userzRegisteredAccesType =  globalParameters.getUserLevel();

			if( !loginAccessType.equals( userzRegisteredAccesType ) ) {
				//Logger.warning(FILE_NAME,"User '"+userId+"/"+customerId+"' having actual Role Level '"+userzRegisteredAccesType+"' tried to Login as an '"+userType+"' User.");
        logger.warn(FILE_NAME+" User '"+userId+"/"+customerId+"' having actual Role Level '"+userzRegisteredAccesType+"' tried to Login as an '"+userType+"' User.");
				throw new InvalidUserException("Invalid Username / Password / Location Id.");
			}
			
			if(userType.equals("ESV"))
			{			
				prjQuery = "SELECT PROJECTID FROM FS_LG_PARTYMASTER WHERE PARTYID = ? AND PARTYFLAG='VENDOR'";
				partyFlag = "VENDOR";
			}
			else if(userType.equals("ESC"))
			{
				prjQuery = "SELECT PROJECTID FROM FS_LG_PARTYMASTER WHERE PARTYID = ? AND PARTYFLAG='CUSTOMER'";
				partyFlag = "CUSTOMER";		
			//Code Added by shivaram v on 29th May for etrans terminalid starts here				
				String sql	=	"SELECT TERMINALID FROM FS_FR_CUSTOMERMASTER WHERE CUSTOMERID = ?";
				pstmt		=	connection.prepareStatement(sql);
				pstmt.setString(1, customerId);
				rs			=	pstmt.executeQuery();
				if(rs.next())
				{
					terminalId = rs.getString("TERMINALID");
					//Logger.info(FILE_NAME, "verifyETransCRMUser(String userId, String password, String customerId, String userType)", "Terminal Id is : "+ terminalId);		
					flag	= true;			
				}
				if(!flag) {
					throw new InvalidUserException("Invalid Username / Password / Location Id.");
				}
			}
			else
			{ 
				throw new InvalidUserException("Invalid Username / Password / Location Id.");
			}
			pstmt = connection.prepareStatement(prjQuery);
				
			pstmt.setString(1, customerId);
			
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				projectId = rs.getString(1);
			}
			
			if (rs != null)
				rs.close();
			if(pstmt != null)
				pstmt.close();
			
			if(rs != null)
				rs.close();
			if(pstmt != null)
				pstmt.close();

			globalParameters.setUserPreferences( getUserPreferences( userId, customerId ) );
			globalParameters.setUserId(userId);
			//globalParameters.setCustWHId(custWHId);
			globalParameters.setLocationId(customerId);
			globalParameters.setProjectId(projectId);
			globalParameters.setTerminalId( terminalId );

			if (userType.equals("ESC"))
				globalParameters.setAccessType("ESCRM");
			else
				globalParameters.setAccessType("ESVRM");

			globalParameters.setUserType(userType);
			globalParameters.setCurrencyId(currencyId);
		//globalParameters.setAccountsCredentials(prepareAccountsCredentials(terminalId, globalParameters.getAccessType()));
		}
		catch(InvalidUserException iu)
		{
			throw new InvalidUserException("Invalid Username / Password / "+userName+" Id.");
		}
		catch(SQLException sqle)
		{
			//Logger.error(FILE_NAME,"verifyELogCRMVRMUser(String userId, String password, String userType)","Exception ",sqle);
      logger.error(FILE_NAME+" verifyELogCRMVRMUser(String userId, String password, String userType)"+"Exception "+sqle);
			//throw new EJBException(sqle.getMessage());
            sessionContext.setRollbackOnly();
            throw FoursoftException.getException(sqle,FILE_NAME,"verifySPCRMVRMUser()");
		}		
		catch(DBSysException dbs)
		{
			//Logger.error(FILE_NAME,"verifyELogCRMVRMUser(String userId, String password, String userType)","Exception ",dbs);
      logger.error(FILE_NAME+" verifyELogCRMVRMUser(String userId, String password, String userType)"+" Exception "+dbs);
			//throw new EJBException(dbs.getMessage());
            sessionContext.setRollbackOnly();
            throw FoursoftException.getException(dbs,FILE_NAME,"verifySPCRMVRMUser()");
		}
		finally
		{
			ConnectionUtil.closeConnection(connection);
		}
		return globalParameters;
		
	} // end of verifySPCRMVRMUser(String userId, String password, String userType)


	/**
	* It is used to validate the CustomerId
	*
	* @param customerId	- Customer's Id
	* @param password	- password
	* @param userType
	* @return boolean
	*
	*/
	public ESupplyGlobalParameters verifyELogCRMVRMUser(String userId, String password, String customerId, String userType) throws InvalidUserException, FoursoftException
	{
		ESupplyGlobalParameters globalParameters	= new ESupplyGlobalParameters();
		PreparedStatement 		pstmt 	= null;
		ResultSet				rs		= null;
		boolean 				flag 	= false;
		String		projectId			= null;
		String		custWHId			= null;
		String 		currencyId			= null;
		String 		partyFlag 			= "";
		String		prjQuery = "";
		String		userName = "";
		String		loginAccessType		= "";
		
		try
		{
			if(userType.equals("ELV") || userType.equals("ESV") || userType.equals("EPV") ) 
            {
				userName	=	"Vendor";
				if(userType.equals("ELV"))
                {
					loginAccessType	=	"ELVRM";
				}
				if(userType.equals("ESV")) 
                {
					loginAccessType	=	"ESVRM";
				}
				if(userType.equals("EPV")) 
                {
					loginAccessType	=	"EPVRM";
				}
			} 
            else if(userType.equals("ELC") || userType.equals("ESC") || userType.equals("EPC") ) 
            {
				userName	=	"Customer";
				if(userType.equals("ELC")) 
                {
					loginAccessType	=	"ELCRM";
				}
                if(userType.equals("ESC")) 
                {
					loginAccessType	=	"ESCRM";
				}
				if(userType.equals("EPC")) 
                {
					loginAccessType	=	"EPCRM";
				}
			} 
            else 
            {
				userName	=	"Transporter";
				if(userType.equals("ELT")) 
                {
					loginAccessType	=	"ELTRM";
				}
				else 
					loginAccessType	=	"EPTRM";
				
			}
			
			makeConnection();

			if ( !isValidUser(userId, password, customerId) )
			{
				if(isUserLocked(userId,customerId))
				{
				//Logger.warning(FILE_NAME,"getESupplyGlobalParameters(String userId, String password, String locationId,String userType)","Not Authorised User ");
        logger.warn(FILE_NAME+" getESupplyGlobalParameters(String userId, String password, String locationId,String userType)"+"Not Authorised User ");
				throw new InvalidUserException("User Locked, reset with Forgot Password.");
				}
				else
				{
				//Logger.warning(FILE_NAME,"verifyELogCRMVRMUser(String userId, String password, String customerId, String userType)","Not Authorised User ");
        logger.warn(FILE_NAME+" verifyELogCRMVRMUser(String userId, String password, String customerId, String userType)"+"Not Authorised User ");
				throw new InvalidUserException("Invalid Username / Password / Location Id.");
				}
			}
			if(isUserLocked(userId,customerId))
			{
					//Logger.warning(FILE_NAME,"getESupplyGlobalParameters(String userId, String password, String locationId,String userType)","Not Authorised User ");
          logger.warn(FILE_NAME+" getESupplyGlobalParameters(String userId, String password, String locationId,String userType)"+"Not Authorised User ");
					throw new InvalidUserException("User Locked, reset with Forgot Password.");
			}
			
			pstmt = connection.prepareStatement(ESPQuery);

			pstmt.setString(1, userId);
			pstmt.setString(2, customerId);
			
			rs = pstmt.executeQuery();
			
			if(rs.next())
			{
				globalParameters.setRoleId(rs.getString(1));
				globalParameters.setRoleLocationId(rs.getString(2));
				globalParameters.setAccessType(rs.getString(3));
				globalParameters.setUserLevel(rs.getString(4));
			}
			if(rs != null)
				rs.close();
			if(pstmt != null)
				pstmt.close();

			String	userzRegisteredAccesType =  globalParameters.getUserLevel();

			if( !loginAccessType.equals( userzRegisteredAccesType ) ) 
            {
				//Logger.warning(FILE_NAME,"User '"+userId+"/"+customerId+"' having actual Role Level '"+userzRegisteredAccesType+"' tried to Login as an '"+userType+"' User.");
        logger.warn(FILE_NAME+"User '"+userId+"/"+customerId+"' having actual Role Level '"+userzRegisteredAccesType+"' tried to Login as an '"+userType+"' User.");
				throw new InvalidUserException("Invalid Username / Password / Location Id.");
			}
			
			if(userType.equals("ELV"))
			{			
				prjQuery = "SELECT PROJECTID FROM FS_LG_PARTYMASTER WHERE PARTYID = ? AND PARTYFLAG='VENDOR'";
				partyFlag = "VENDOR";
			}
			else if(userType.equals("EPV"))
			{			
				prjQuery = "SELECT COMPANYID FROM FS_LG_PARTYMASTER WHERE PARTYID = ? AND PARTYFLAG='VENDOR'";
				partyFlag = "VENDOR";
			}
			else if(userType.equals("ELC"))
			{
				prjQuery = "SELECT PROJECTID FROM FS_LG_PARTYMASTER WHERE PARTYID = ? AND PARTYFLAG='CUSTOMER'";
				partyFlag = "CUSTOMER";				
			}
			else if(userType.equals("EPC"))
			{
				prjQuery = "SELECT COMPANYID FROM FS_LG_PARTYMASTER WHERE PARTYID = ? AND PARTYFLAG='CUSTOMER'";
				partyFlag = "CUSTOMER";				
			}
			else if(userType.equals("EPT"))
			{
				prjQuery = "SELECT COMPANYID FROM FS_FR_TERMINALMASTER WHERE TERMINALID = ?";				
			}
			else if(userType.equals("ELT"))
			{
				prjQuery = "SELECT COMPANYID FROM FS_FR_TERMINALMASTER WHERE TERMINALID = ?";				
			}
			else
			{ 
				throw new InvalidUserException("Invalid Username / Password / Location Id.");
			}

			pstmt = connection.prepareStatement(prjQuery);
				
			pstmt.setString(1, customerId);
			
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				projectId = rs.getString(1);
			}
			
			if (rs != null)
				rs.close();
			if(pstmt != null)
				pstmt.close();

			if(userType.equals("EPT") && (userType.equals("ELT"))) 
			{
				globalParameters.setTerminalId( customerId );
				currencyId  = getCurrencyId( userType,customerId );
			}
			
			if(rs != null)
				rs.close();
			if(pstmt != null)
				pstmt.close();

			globalParameters.setUserPreferences( getUserPreferences( userId, customerId ) );
			if(userType.equals("ELV")){
				globalParameters.setCodeCustStatus(getCodeCustProjectLevel("ELVRM",customerId,connection));
			}else if(userType.equals("ELC")){
				globalParameters.setCodeCustStatus(getCodeCustProjectLevel("ELCRM",customerId,connection));
			}
		}
		catch(InvalidUserException iu)
		{
			throw new InvalidUserException("Invalid Username / Password / "+userName+" Id.");
		}
		catch(SQLException sqle)
		{
			//Logger.error(FILE_NAME,"verifyELogCRMVRMUser(String userId, String password, String userType)","Exception ",sqle);
      logger.error(FILE_NAME+" verifyELogCRMVRMUser(String userId, String password, String userType)"+"Exception "+sqle);
			//throw new EJBException(sqle.getMessage());
            sessionContext.setRollbackOnly();
            throw FoursoftException.getException(sqle,FILE_NAME,"verifyELogCRMVRMUser()");
		}		
		catch(DBSysException dbs)
		{
			//Logger.error(FILE_NAME,"verifyELogCRMVRMUser(String userId, String password, String userType)","Exception ",dbs);
      logger.error(FILE_NAME+" verifyELogCRMVRMUser(String userId, String password, String userType)"+" Exception "+dbs);
//			throw new EJBException(dbs.getMessage());
            sessionContext.setRollbackOnly();
            throw FoursoftException.getException(dbs,FILE_NAME,"verifyELogCRMVRMUser()");
		}
		finally
		{
			ConnectionUtil.closeConnection(connection);
		}
		globalParameters.setUserId(userId);
		globalParameters.setCustWHId(custWHId);
		globalParameters.setProjectId(projectId);
		if(userType.substring(0,2).equals("EP")) 
        {
			globalParameters.setCompanyId(projectId);
		}
		globalParameters.setUserType(userType);
		globalParameters.setCurrencyId(currencyId);

		return globalParameters;
		
	} // end of verifyELogCRMVRMUser(String userId, String password, String userType)

	/**
	* This is used to validate the CRM user of the ETRANS
	*
	* @param userId	- Customer's Id
	* @param password - password
	* @param userType 
	*
	*/

	public ESupplyGlobalParameters verifyETransCRMUser(String userId, String password, String customerId, String userType) throws InvalidUserException, FoursoftException
	{
    	PreparedStatement 		pstmt		 	= null;
	    ResultSet      	rs			  	= null;		
		
		boolean flag	= false;	
		String terminalId = null;
		ESupplyGlobalParameters globalParameters	=  new ESupplyGlobalParameters();
		
		try	
		{
			if(!userType.equalsIgnoreCase("ETC") )
			{
				throw new InvalidUserException("Invalid Username / Password / Location Id.");
			}
			
			makeConnection();

			if ( !isValidUser(userId, password, customerId) )
			{
				if(isUserLocked(userId,customerId))
				{
					//Logger.warning(FILE_NAME,"getESupplyGlobalParameters(String userId, String password, String locationId,String userType)","Not Authorised User ");
          logger.warn(FILE_NAME+" getESupplyGlobalParameters(String userId, String password, String locationId,String userType)"+"Not Authorised User ");
					throw new InvalidUserException("User Locked, reset with Forgot Password.");
				}
				else
				{
			
				//Logger.warning(FILE_NAME,"verifyETransCRMUser(String userId, String password, String customerId, String userType)","Not Authorised User ");
        logger.warn(FILE_NAME+" verifyETransCRMUser(String userId, String password, String customerId, String userType)"+"Not Authorised User ");
				throw new InvalidUserException("Invalid Username / Password / Location Id.");
				}
			}
			
			if(isUserLocked(userId,customerId))
			{
					//Logger.warning(FILE_NAME,"getESupplyGlobalParameters(String userId, String password, String locationId,String userType)","Not Authorised User ");
          logger.warn(FILE_NAME+" getESupplyGlobalParameters(String userId, String password, String locationId,String userType)"+"Not Authorised User ");
					throw new InvalidUserException("User Locked, reset with Forgot Password.");
			}
			
			pstmt = connection.prepareStatement(ESPQuery);
			pstmt.setString(1, userId);
			pstmt.setString(2, customerId);
			rs = pstmt.executeQuery();
			if(rs.next())
			{
				globalParameters.setRoleId(rs.getString(1));
				globalParameters.setRoleLocationId(rs.getString(2));
				globalParameters.setAccessType(rs.getString(3));
				globalParameters.setUserLevel(rs.getString(4));
			}
			if(rs != null)
				rs.close();
			if(pstmt != null)
				pstmt.close();

			String	actualRoleLevel =  globalParameters.getUserLevel();

			if( !actualRoleLevel.equals( "ETCRM" ) ) {
				//Logger.warning(FILE_NAME,"User '"+userId+"/"+customerId+"' having actual Role Level '"+actualRoleLevel+"' tried to Login as an 'ETCRM' User.");
        logger.warn(FILE_NAME+" User '"+userId+"/"+customerId+"' having actual Role Level '"+actualRoleLevel+"' tried to Login as an 'ETCRM' User.");
				throw new InvalidUserException("Invalid Username / Password / Location Id.");
			}

			String sql	=	"SELECT TERMINALID FROM FS_FR_CUSTOMERMASTER WHERE CUSTOMERID = ?";
			pstmt		=	connection.prepareStatement(sql);
			pstmt.setString(1, customerId);
			rs			=	pstmt.executeQuery();
			if(rs.next())
			{
				terminalId = rs.getString("TERMINALID");
				//Logger.info(FILE_NAME, "verifyETransCRMUser(String userId, String password, String customerId, String userType)", "Terminal Id is : "+ terminalId);		
				flag	= true;			
			}
			if(!flag) {
				throw new InvalidUserException("Invalid Username / Password / Location Id.");
			}
				
		}
		catch(InvalidUserException iu)
		{
			throw new InvalidUserException("Invalid Username / Password / Customer Id.");
		}
		catch(SQLException se)
		{
			//Logger.error(FILE_NAME, "verifyETransCRMUser(String userId, String password, String customerId, String userType)","SQL EXception ",se);
      logger.error(FILE_NAME+ "verifyETransCRMUser(String userId, String password, String customerId, String userType)"+"SQL EXception "+se);
			sessionContext.setRollbackOnly();
            throw FoursoftException.getException(se,FILE_NAME,"verifyETransCRMUser");
		}
		catch(DBSysException dbs)
		{
			//Logger.error(FILE_NAME,"verifyETransCRMUser(String userId, String password, String customerId, String userType)","DBSysException ",dbs);
      logger.error(FILE_NAME+" verifyETransCRMUser(String userId, String password, String customerId, String userType)"+" DBSysException "+dbs);
			//throw new EJBException(dbs.getMessage());
            sessionContext.setRollbackOnly();
            throw FoursoftException.getException(dbs,FILE_NAME,"verifyETransCRMUser");
		}
		finally
		{
			if(flag)
			{
				globalParameters.setUserId(userId);
				globalParameters.setCustomerId(customerId);
				globalParameters.setLocationId(customerId);
				globalParameters.setTerminalId(terminalId);
				globalParameters.setUserType(userType);
				globalParameters.setAccessType("ETCRM");
				globalParameters.setCurrencyId(getETransCurrencyId(terminalId));
				globalParameters.setUserPreferences( getUserPreferences( userId, customerId ) );
				//globalParameters.setAccountsCredentials(prepareAccountsCredentials(terminalId, globalParameters.getAccessType()));
			}
      //Modified By RajKumari on 23-10-2008 for Connection Leakages.
			//ConnectionUtil.closeConnection(connection, pstmt);
      ConnectionUtil.closeConnection(connection, pstmt, rs);
		}
		return globalParameters;
	} // end of verifyETransCRMUser(String userId, String password, String customerId, String userType)



	/**
	* This is used to retrieve the retrieve the previllages assosiated with a Role
	*
	* @param roleId
	* @param locationId
	*
	* @return Hashtable - hashtable which contains txId and accessLevel of allowed previllages
	*
	*/

	public static final String	PERMISSIONS_LIST_QUERY = "SELECT TXID, ACCESSLEVEL FROM FS_ROLEPERMISSIONS WHERE ROLEID = ? AND LOCATIONID = ? ORDER BY TXID ";  
	
	public Hashtable getRolePermissions(String roleId, String locationId) throws FoursoftException
	{
		boolean flag = false;

		PreparedStatement	pstmt	= null;
		ResultSet			rs		= null;

		Hashtable userPermissions = new Hashtable();
		try
		{
			makeConnection();

			pstmt	= connection.prepareStatement(PERMISSIONS_LIST_QUERY);
			pstmt.setString(1, roleId);
			pstmt.setString(2, locationId);
			rs		= pstmt.executeQuery();
			String txId = "";
			int access = 0;
			while(rs.next())
			{
				txId 	= rs.getString(1);
				access		= rs.getInt(2);
			    userPermissions.put(txId, new Integer(access));
			}
			if(rs!=null)
				rs.close();
		}
		catch(SQLException sql)
		{
			//Logger.error(FILE_NAME,"getRolePermissions(String roleId, String locationId)","SQLException ",sql);
      logger.error(FILE_NAME+" getRolePermissions(String roleId, String locationId)"+"SQLException "+sql);
			//throw new EJBException(sql.getMessage());
            sessionContext.setRollbackOnly();
            throw FoursoftException.getException(sql,FILE_NAME,"getRolePermissions()");
		}		
		catch(DBSysException dbs)
		{
			//Logger.error(FILE_NAME,"getRolePermissions(String roleId, String locationId)","DBSysException ",dbs);
      logger.error(FILE_NAME+" getRolePermissions(String roleId, String locationId)"+"DBSysException "+dbs);
			//throw new EJBException(dbs.getMessage());
            sessionContext.setRollbackOnly();
            throw FoursoftException.getException(dbs,FILE_NAME,"getRolePermissions()");
		}
		finally
		{
			ConnectionUtil.closeConnection(connection, pstmt);
		}
		return userPermissions;
	} // end of getRolePermissions(String roleId, String locationId)


	public Integer getTerminalType(String locationId, String accessType) throws FoursoftException
	{
		String	TERMINAL_TYPE = "";

		if(accessType.equals("GATEWAY")) {
			TERMINAL_TYPE = "SELECT GATEWAYTYPE FROM FS_FR_GATEWAYMASTER WHERE GATEWAYID = ?";
		} else {
			TERMINAL_TYPE = "SELECT SHIPMENTMODE FROM FS_FR_TERMINALMASTER WHERE TERMINALID = ? ";
		}

		PreparedStatement	pstmt	= null;
		ResultSet			rs		= null;
		Integer shipmentMode = new Integer(0);
		try
		{
			makeConnection();

			pstmt	= connection.prepareStatement(TERMINAL_TYPE);
			
			pstmt.setString(1, locationId);
			
			rs		= pstmt.executeQuery();
			
			String txId = "";
			int access = 0;
			if(rs.next())
			{
				shipmentMode = new Integer(rs.getInt(1));
			}
			if(rs!=null)
				rs.close();
			
			//Logger.info(" Shipment mode in Gateway Master = "+shipmentMode.intValue());
		}
		catch(SQLException sql)
		{
			//Logger.error(FILE_NAME,"getTerminalType(String locationId)","SQLException ",sql);
      logger.error(FILE_NAME+"getTerminalType(String locationId)"+"SQLException "+sql);
			//throw new EJBException(sql.getMessage());
            sessionContext.setRollbackOnly();
            throw FoursoftException.getException(sql,FILE_NAME,"getTerminalType");
		}		
		catch(DBSysException dbs)
		{
			//Logger.error(FILE_NAME,"getTerminalType(String locationId)","DBSysException ",dbs);
      logger.error(FILE_NAME+"getTerminalType(String locationId)"+"DBSysException "+dbs);
			//throw new EJBException(dbs.getMessage());
			sessionContext.setRollbackOnly();
            throw FoursoftException.getException(dbs,FILE_NAME,"getTerminalType");
		}
		finally
		{
			ConnectionUtil.closeConnection(connection, pstmt);
		}
		return shipmentMode;
	} // end of getRolePermissions(String roleId, String locationId)
	
	/**
	* This is used to validate the User
	*
	* @param userId
	* @param password
	* @param locationId
	*
	* @exception SQLException
	*
	* @retrun boolean (true if the user is valid otherwise false
	*/
  
  public static final String	IS_VALID_USER_QUERY = "SELECT USERSTATUS FROM FS_USERMASTER  WHERE USERID = ? AND PASSWORD = ? AND LOCATIONID = ?  ";
  //Added Invalidate='F'--- by I.V.Sekhar
	//public static final String	IS_VALID_USER_QUERY = "SELECT A.USERSTATUS FROM FS_USERMASTER A,FS_FR_TERMINALMASTER B WHERE A.USERID = ? AND A.PASSWORD = ? AND A.LOCATIONID = ? AND A.LOCATIONID = B.TERMINALID AND B.INVALIDATE='F' ";
	
	private boolean isValidUser(String userId, String password, String locationId) throws FoursoftException
	{
		boolean isValid = false;
//		Logger.info(FILE_NAME,"VALID_QUERY : "+IS_VALID_USER+" : "+userId+" : "+password+" : "+locationId);

		PreparedStatement	pstmt = null;
		ResultSet	rs	= null;
		try
		{
			pstmt	= connection.prepareStatement(IS_VALID_USER_QUERY);
			pstmt.setString(1, userId);
			pstmt.setString(2, password);
			pstmt.setString(3, locationId);
			rs		= pstmt.executeQuery();

			if(rs.next() ) {
				isValid = true;
			}
			if(rs!=null)
				rs.close();
		}
        catch(SQLException sqlE)
        {
            throw FoursoftException.getException(sqlE,FILE_NAME,"isValidUser()");
        }
		finally
		{
			ConnectionUtil.closeStatement(pstmt);
		}

		return isValid;
	} // end of isValidUser(String userId, String password, String locationId)

	public static final String	IS_USER_LOCKED_QUERY = "SELECT USERSTATUS FROM FS_USERMASTER WHERE USERID = ? AND LOCATIONID = ?";
	
	private boolean isUserLocked(String userId, String locationId) throws FoursoftException
	{
		boolean isLocked = false;
		PreparedStatement	pstmt = null;
		ResultSet	rs	= null;
		
		try
		{
			pstmt	= connection.prepareStatement(IS_USER_LOCKED_QUERY);
			pstmt.setString(1, userId);
			pstmt.setString(2, locationId);
			rs		= pstmt.executeQuery();

			if(rs.next() ) {
				int isLoc=rs.getInt(1);
					if(isLoc==0)
					isLocked = true;
			}
			if(rs!=null)
				rs.close();
		}
        catch(SQLException sqlE)
        {
            throw FoursoftException.getException(sqlE,FILE_NAME,"isValidUser()");
        }
		finally
		{
			ConnectionUtil.closeStatement(pstmt);
		}

		return isLocked;
	} // end of isValidUser(String userId, String password, String locationId)


	public void ejbCreate()	throws CreateException
	{
		//System.out.println("FROM ejbCreate of '"+this.getClass().getName()+"'");
	}

	public AccessControlSessionBean() {
		//System.out.println("FROM Constructor of '"+this.getClass().getName()+"'");
       logger  = Logger.getLogger(AccessControlSessionBean.class);
	}

	//Implementing SessionBeanInterface methods.....//
	public void setSessionContext(SessionContext sessionCtx)
	{
		//System.out.println("FROM setSessionContext of '"+this.getClass().getName()+"'");
		this.sessionContext = sessionCtx;
        try 
		{
			lookUpBean	=	new LookUpBean();
			datasource  = lookUpBean.lookupDataSource();
        } 
		catch (NamingException ne) 
		{
            throw new EJBException(	"NamingException while looking " +
                                        "up Data Source Connection " + 
                                        FoursoftConfig.DATA_SOURCE+"  \n" + ne.getMessage());
        }
	}

	public void ejbRemove() throws javax.ejb.EJBException
	{
	}

	public void ejbActivate() throws javax.ejb.EJBException
	{
	}

	public void ejbPassivate() throws javax.ejb.EJBException
	{
	}


	/**
	* This is used by eLog to retrieve the CustWHId's which the user can able to access
	* The accessType can be (WAREHOUSE, PROJECT, COMPANY)
	*
	* @param locationId	- locationId of the user (it may be one of the WAREHOUSE, PROJECT)
	* @param accessType	- accessType of the user (WAREHOUSE/PROJECT/COMPANY)
	*
	* @return ArrayList - list of CustWHIds
	*
	*/
	private ESupplyGlobalParameters getELogCredentials(ESupplyGlobalParameters globalParameters, String roleLevel, String locationId) throws FoursoftException
	{
			Statement stmt = null;
			ResultSet rs = null ;
			String COMP_SQL = null;
			String CUSTWH_SQL = null;
			String WH_SQL = null;
			String PROJ_SQL = null;
			String whId = null;
			String projectId = null;
			String companyId = null;
			String whName = null;
			String custWHId = null;
			String terminalId = null;
			String licenseeId = null;
			try
			{
				stmt = connection.createStatement();
				if(roleLevel.equalsIgnoreCase("CUSTWH") )
				{
					CUSTWH_SQL = "SELECT FS_LG_CUSTWHMASTER.WHID, FS_LG_CUSTWHMASTER.PROJECTID, FS_LG_WHMASTER.COMPANYID, FS_LG_WHMASTER.WHNAME "
									+"FROM FS_LG_CUSTWHMASTER, FS_LG_WHMASTER "
									+"WHERE CUSTWHID='"+ locationId +"' AND FS_LG_CUSTWHMASTER.WHID=FS_LG_WHMASTER.WHID";

					rs = stmt.executeQuery(CUSTWH_SQL);
					//Logger.info(FILE_NAME,"setELogCredentials(ESupplyGlobalParameters globalParameters, String roleLevel, String locationId)","CUSTWH_SQL : "+CUSTWH_SQL);
					while(rs.next() )
					{
						whId			= rs.getString(1);
						projectId		= rs.getString(2);
						companyId		= rs.getString(3);
						whName			= rs.getString(4);
					}
					custWHId	= locationId;
					globalParameters.setCustWHId(locationId);
					globalParameters.setWareHouseId(whId);
					globalParameters.setProjectId(projectId);
					globalParameters.setCompanyId(companyId);

					globalParameters.setCurrencyId(getCurrencyId("CUSTWH", locationId) );

					
					if(rs!=null)
						rs.close();
				}
				else if( roleLevel.equalsIgnoreCase("WAREHOUSE") ) 
				{
					WH_SQL = "SELECT COMPANYID FROM FS_LG_WHMASTER WHERE WHID='"+ locationId +"'";
					String WH_DTL_SQL	= "SELECT CUSTWHID FROM FS_LG_CUSTWHMASTER WHERE WHID ='"+locationId +"'";

					rs = stmt.executeQuery(WH_SQL);

					//Logger.info(FILE_NAME,"setELogCredentials(ESupplyGlobalParameters globalParameters, String roleLevel, String locationId)","WH_SQL : "+WH_SQL);
					
					while(rs.next() )
					{
							companyId		= rs.getString(1);
					}
					if(rs!=null)
						rs.close();
						
					rs	= stmt.executeQuery(WH_DTL_SQL);
					
					ArrayList	custwhList	= new ArrayList();
					
					while(rs.next() )
					{
						custwhList.add(rs.getString(1) );
					}
					if(rs!=null)
						rs.close();
					////////////////////

					PreparedStatement pstmt	=	null;
					 int custwhListSize		= custwhList.size();//Added for loop performance
					ArrayList	permittedWHIds	= new ArrayList();

					pstmt = connection.prepareStatement( OPT_ROLES_WH_SQL );

					pstmt.setString(1, globalParameters.getUserId() );
					pstmt.setString(2, globalParameters.getLocationId() );

					rs	=	pstmt.executeQuery();

					while(rs.next() ) {
						String	permittedWHID = rs.getString(1);

						InLoop: for(int i=0; i < custwhListSize; i++) {
									String	whid = (String) custwhList.get(i);
									if(permittedWHID.equals(whid)) {
										permittedWHIds.add( permittedWHID );
										break InLoop;
									}
								}

					}
					if(rs!=null)
						rs.close();
					if(pstmt!=null)
						pstmt.close();
					
					////////////////////

					pstmt = connection.prepareStatement("SELECT PROJECTID FROM FS_LG_CUSTWHMASTER WHERE CUSTWHID= ?");

					ResultSet rsPrj = null;

					Hashtable projectTable = new Hashtable();
					
					for(int i=0;i<custwhListSize;i++)
					{
						pstmt.setString(1,(String)custwhList.get(i));
						rsPrj = pstmt.executeQuery();
						if(rsPrj.next())
						{
							projectTable.put(custwhList.get(i),rsPrj.getString(1));
						}
					}
					if(rsPrj!=null)
						rsPrj.close();
					if(pstmt!=null)
						pstmt.close();
					
					ELogCredentials	elogCredentials = new ELogCredentials();
					
					elogCredentials.setCustWHInfo(custwhList);
					elogCredentials.setPermittedWHIds( permittedWHIds );
					elogCredentials.setProjectInfo(projectTable);
					globalParameters.setELogCredentials(elogCredentials);
					globalParameters.setWareHouseId(locationId);
					globalParameters.setCompanyId(companyId);
					globalParameters.setCurrencyId(getCurrencyId("WAREHOUSE", locationId) );


				}
				else if(roleLevel.equalsIgnoreCase("PROJECTLOG") )
				{
					PROJ_SQL = "SELECT COMPANYID FROM FS_LG_PROJECTMASTER WHERE PROJECTID='"+ locationId +"'";
					String PR_DTL_SQL	= "SELECT CUSTWHID FROM FS_LG_CUSTWHMASTER WHERE PROJECTID ='"+locationId +"'";
					
					rs = stmt.executeQuery(PROJ_SQL);

					//Logger.info(FILE_NAME,"setELogCredentials(ESupplyGlobalParameters globalParameters, String roleLevel, String locationId)","PROJ_SQL : "+PROJ_SQL);

					while(rs.next() )
					{
						companyId		= rs.getString(1);
					}
					if(rs!=null)
						rs.close();

					rs	= stmt.executeQuery(PR_DTL_SQL);
					ArrayList	custwhList	= new ArrayList(); 

					while(rs.next() )
					{
						custwhList.add(rs.getString(1) );
					}
					ELogCredentials	elogCredentials = new ELogCredentials();
					elogCredentials.setCustWHInfo(custwhList);
					globalParameters.setELogCredentials(elogCredentials);
					globalParameters.setProjectId(locationId);
					globalParameters.setCompanyId(companyId);

					globalParameters.setCurrencyId(getCurrencyId("PROJECTLOG", locationId) );

					if(rs!=null)
						rs.close();
				}
				else if(roleLevel.equalsIgnoreCase("HO_TERMINAL") )
				{
					ArrayList	companyList	= new ArrayList();
					
					COMP_SQL = "SELECT DISTINCT COMPANYID FROM FS_LG_COMPANYMASTER";
					
					rs = stmt.executeQuery(COMP_SQL);

					//Logger.info(FILE_NAME,"setELogCredentials(ESupplyGlobalParameters globalParameters, String roleLevel, String locationId)","COMP_SQL : "+COMP_SQL);

					while(rs.next() )
					{
						companyList.add( rs.getString(1) );
					}
					if(rs!=null)
						rs.close();

					ELogCredentials	elogCredentials = new ELogCredentials();

					// Customer Warehouses will not be there for HO User hence use the same
					// construct for storing Company Ids for HO User
					elogCredentials.setCustWHInfo( companyList );

					globalParameters.setELogCredentials(elogCredentials);

					globalParameters.setCurrencyId( getCurrencyId("HO_TERMINAL", locationId) );

                    setHOIdForELog(globalParameters.getUserId(),locationId,globalParameters);

				}
				else if(roleLevel.equalsIgnoreCase("COMPANY") )
				{
					companyId	= locationId;
					globalParameters.setCompanyId(companyId);
					globalParameters.setCurrencyId(getCurrencyId("COMPANY", locationId) );

				}
				else if(roleLevel.equalsIgnoreCase("LICENSEE") )
				{
					globalParameters.setTerminalId( locationId );
					licenseeId	= locationId;
				}
			}
			catch(SQLException sqle)
			{
				//Logger.warning(FILE_NAME,"getELogCredentials(ESupplyGlobalParameters globalParameters, String roleLevel, String locationId) : "+sqle.getMessage());
        logger.warn(FILE_NAME+" getELogCredentials(ESupplyGlobalParameters globalParameters, String roleLevel, String locationId) : "+sqle.getMessage());
                throw FoursoftException.getException(sqle,FILE_NAME,"getELogCredentials");
			}
			finally
			{
				ConnectionUtil.closeStatement(stmt);
			}
			
			return globalParameters;
	} // end of getELogCredentials(ESupplyGlobalParameters globalParameters, String roleLevel, String locationId)


	private static final String	OPT_ROLES_WH_SQL	= "SELECT ROLE_LOCATIONID FROM FS_OPTIONALROLES WHERE USERID = ? AND LOCATIONID = ? ORDER BY ROLE_LOCATIONID";

	/**
	* This is used to retrieve the Warehouses into under the Company for eSupply EP
	* The accessType can be (COMPANY)
	*
	* @param locationId	- locationId of the user (it may be one of the WAREHOUSE, PROJECT)
	* @param accessType	- accessType of the user (WAREHOUSE/PROJECT/COMPANY)
	*
	* @return ESupplyGlobalParameters - modified loginbean
	*
	*/
	private ESupplyGlobalParameters getEPCredentials(ESupplyGlobalParameters globalParameters, String roleLevel, String locationId) throws FoursoftException
	{

		Statement stmt = null;
		PreparedStatement	pstmt	=	null;
		ResultSet rs = null ;
		String CUSTWH_SQL = null;
		String WH_SQL = null;
		String PROJ_SQL = null;
		String whId = null;
		String projectId = null;
		String companyId = null;
		String whName = null;
		String custWHId = null;
		String terminalId = null;
		String licenseeId = null;
		try
		{
			stmt = connection.createStatement();
			//AccountsCredentials accountsCredentials = globalParameters.getAccountsCredentials();
			//Logger.info(FILE_NAME,"accountsCredentials : bookId = "+accountsCredentials.getBookId());
			
			if(roleLevel.equalsIgnoreCase("WAREHOUSE") )
			{
				CUSTWH_SQL = "SELECT WHID, COMPANYID, WHNAME FROM FS_LG_WHMASTER WHERE WHID = '"+ locationId +"'";

				rs = stmt.executeQuery(CUSTWH_SQL);
				
				//Logger.info(FILE_NAME,"getEPCredentials(ESupplyGlobalParameters globalParameters, String roleLevel, String locationId)","CUSTWH_SQL : "+CUSTWH_SQL);
				
				while(rs.next() )
				{
					whId			= rs.getString(1);						
					companyId		= rs.getString(2);
					whName			= rs.getString(3);
				}
				//custWHId	= locationId;
				//globalParameters.setCustWHId(locationId);
				globalParameters.setWareHouseId(whId);
				//accountsCredentials.setAcctCompanyId(locationId);
				//globalParameters.setProjectId(projectId);
				globalParameters.setCompanyId(companyId);
				globalParameters.setCurrencyId(getCurrencyId("WAREHOUSE", locationId) );

				if(rs!=null)
					rs.close();
			}
			else if(roleLevel.equalsIgnoreCase("COMPANY"))
			{
				PROJ_SQL = "SELECT COMPANYID FROM FS_LG_COMPANYMASTER WHERE COMPANYID='"+ locationId +"'";
				String WH_DTL_SQL	= "SELECT WHID FROM FS_LG_WHMASTER WHERE COMPANYID ='"+locationId +"' ORDER BY WHID";
					
				rs = stmt.executeQuery(PROJ_SQL);

				//Logger.info(FILE_NAME,"getEPCredentials(ESupplyGlobalParameters globalParameters, String roleLevel, String locationId)","PROJ_SQL : "+PROJ_SQL);
				//Logger.info(FILE_NAME,"getEPCredentials(ESupplyGlobalParameters globalParameters, String roleLevel, String locationId)","WH_DTL_SQL : "+WH_DTL_SQL);

				while(rs.next() ) {
					companyId		= rs.getString(1);
				}
				if(rs!=null)
					rs.close();

				rs	= stmt.executeQuery(WH_DTL_SQL);

				ArrayList	custwhList		= new ArrayList();

				while(rs.next() ) {
					custwhList.add(rs.getString(1) );
				}
				if(rs!=null)
					rs.close();
					
				//Logger.info(FILE_NAME,"custwhList.size() : "+custwhList.size());

				ArrayList	permittedWHIds	= new ArrayList();
					int custwhListSize 	= custwhList.size();
				pstmt = connection.prepareStatement( OPT_ROLES_WH_SQL );

				pstmt.setString(1, globalParameters.getUserId() );
				pstmt.setString(2, globalParameters.getLocationId() );

				rs	=	pstmt.executeQuery();

				while(rs.next() ) {
					String	permittedWHID = rs.getString(1);

					InLoop: for(int i=0; i < custwhListSize; i++) {
								String	whid = (String) custwhList.get(i);
								if(permittedWHID.equals(whid)) {
									permittedWHIds.add( permittedWHID );
									break InLoop;
								}
							}

				}
				if(rs!=null)
					rs.close();
				if(pstmt!=null)
					pstmt.close();

				ELogCredentials	elogCredentials = new ELogCredentials();
				
				elogCredentials.setWHInfo(custwhList);
				elogCredentials.setPermittedWHIds( permittedWHIds );
				globalParameters.setELogCredentials(elogCredentials);
				//globalParameters.setProjectId(locationId);
				globalParameters.setCompanyId(companyId);
				globalParameters.setWareHouseId(companyId);
				//accountsCredentials.setAcctCompanyId(locationId);

				globalParameters.setCurrencyId(getCurrencyId("COMPANY", locationId) );

			}				
			else if(roleLevel.equalsIgnoreCase("LICENSEE") )
			{
				licenseeId	= locationId;
			}
			//globalParameters.setAccountsCredentials(accountsCredentials);
				

		} catch(SQLException sqle) {
			//Logger.warning(FILE_NAME,"getEPCredentials(ESupplyGlobalParameters globalParameters, String roleLevel, String locationId) : "+sqle.getMessage());
      logger.warn(FILE_NAME+" getEPCredentials(ESupplyGlobalParameters globalParameters, String roleLevel, String locationId) : "+sqle.getMessage());
            throw FoursoftException.getException(sqle,FILE_NAME,"getEPCredentials()");
		} finally {
			ConnectionUtil.closeStatement(stmt);
		}
		
		return globalParameters;
		
	} // end of getELogCredentials(ESupplyGlobalParameters globalParameters, String roleLevel, String locationId)

	/**
	 * This method is used to get the terminal type(H/A/O) of the user logged in
	 * H -HO Terminal 
	 * O -Operational Terminal
	 * A -Admin Terminal 
	 */
	private String getUserTerminalType(String locationId) throws FoursoftException
	{
		Statement	stmt	= null;
		ResultSet	rs		= null;
		String		userTerminalType	= "";
		String		SQL_QUERY	= "";

		SQL_QUERY = "SELECT OPER_ADMIN_FLAG FROM FS_FR_TERMINALMASTER WHERE TERMINALID = '"+locationId+"' ";

		try
		{
			stmt		= connection.createStatement();
			rs		= stmt.executeQuery(SQL_QUERY);
			if(rs.next() )
			{
				userTerminalType	= rs.getString(1);
			}
			if(rs != null)
				rs.close();
		}
		catch(Exception e)
		{
			//Logger.error(FILE_NAME,"getUserTerminalType(String locationId)"," Error in Retrieving User Terminal Type ",e);
      logger.error(FILE_NAME+"getUserTerminalType(String locationId)"+" Error in Retrieving User Terminal Type "+e);
            throw FoursoftException.getException(e,FILE_NAME,"getUserTerminalType()");
		}
		finally
		{
			ConnectionUtil.closeStatement(stmt);
		}
		return userTerminalType;	
	}

	/**
	 * This method is used to get the Company Id of the Terminal 
	 */
	private String getCompanyId(String locationId, String accessType) throws FoursoftException
	{
		Statement	stmt	= null;
		ResultSet	rs		= null;
		String		companyId	= "";
		String		SQL_QUERY	= "";
		
		if(accessType.equals("GATEWAY")) {
			SQL_QUERY = "SELECT GATEWAYID FROM FS_FR_GATEWAYMASTER WHERE GATEWAYID = '"+locationId+"' ";			
		} else {
			SQL_QUERY = "SELECT COMPANYID FROM FS_FR_TERMINALMASTER WHERE TERMINALID = '"+locationId+"' ";
		}


		try
		{
			stmt		= connection.createStatement();
			rs		= stmt.executeQuery(SQL_QUERY);
			if(rs.next() )
			{
				companyId	= rs.getString(1);
			}
			if(rs != null)
				rs.close();
		}
		catch(Exception e)
		{
			//Logger.error(FILE_NAME,"getCompanyId(String locationId)"," Error in Retrieving Terminal's Company Id ",e);
      logger.error(FILE_NAME+" getCompanyId(String locationId)"+" Error in Retrieving Terminal's Company Id "+e);
            FoursoftException.getException(e,FILE_NAME,"getCompanyId()");
		}
		finally
		{
			ConnectionUtil.closeStatement(stmt);
		}
		return companyId;	
	}

	private String getCurrencyId(String locationType, String locationId) throws FoursoftException
	{
		Statement	stmt	= null;
		ResultSet	rs		= null;
		String		currencyId	= "";
		String		currencySQL	= "";
		//Logger.info(FILE_NAME,"getCurrencyId(String locationType, String locationId)","locationId  "+locationId+" locationType "+locationType);

		if(locationType.equals("CUSTWH") )
		{
			currencySQL	= "SELECT CURRENCYID FROM FS_LG_CUSTWHMASTER WHERE CUSTWHID = '"+locationId+"'";
		}
		else if(locationType.equals("WAREHOUSE") )
		{
			currencySQL	= "SELECT CURRENCYID FROM FS_LG_WHMASTER WHERE WHID = '"+locationId+"'";
		}
		else if(locationType.equals("PROJECTLOG") )
		{
			currencySQL	= "SELECT CURRENCYID FROM FS_LG_PROJECTMASTER WHERE PROJECTID = '"+locationId+"'";
		}
		else if(locationType.equals("HO") )
		{
			currencySQL	= "SELECT CURRENCYID FROM FS_LG_HOMASTER WHERE HOID = '"+locationId+"'";
		}
		else if(locationType.equals("COMPANY") )
		{
			currencySQL	= "SELECT CURRENCYID FROM FS_LG_COMPANYMASTER WHERE COMPANYID = '"+locationId+"'";
		}
		else if(locationType.equals("ELV") || locationType.equals("EPV"))
		{
			currencySQL	= "SELECT CURRENCYID FROM FS_LG_PARTYMASTER WHERE PARTYID = '"+locationId+"' AND PARTYFLAG='VENDOR'";
		}
		else if(locationType.equals("ELC") || locationType.equals("EPC"))
		{
			currencySQL	= "SELECT CURRENCYID FROM FS_LG_PARTYMASTER WHERE PARTYID = '"+locationId+"' AND PARTYFLAG='CUSTOMER'";
		}
		try
		{
			//Logger.info(FILE_NAME,"getCurrencyId(String locationType, String locationId)","Currency SQL : "+currencySQL);
			stmt		= connection.createStatement();
			rs		= stmt.executeQuery(currencySQL);
			if(rs.next() )
			{
				currencyId	= rs.getString(1);
			}
			if(rs != null)
				rs.close();
		}
		catch(Exception e)
		{
			//Logger.error(FILE_NAME,"getCurrencyId(String locationType, String locationId)"," Error in Retrieving Currency Id ",e);
      logger.error(FILE_NAME+"getCurrencyId(String locationType, String locationId)"+" Error in Retrieving Currency Id "+e);
            throw FoursoftException.getException(e,FILE_NAME,"getCurrencyId()");
		}
		finally
		{
			ConnectionUtil.closeStatement(stmt);
		}
		return currencyId;	
	}


	/**
	 * This method is used to get the currencyIds for eLog
	 *
	 */
	public String getELogCurrencyId(String locationType, String locationId) throws FoursoftException
    {
		Statement	stmt	= null;
		ResultSet	rs		= null;
		String		currencyId	= "";
		String		currencySQL	= "";
		if(locationType.equals("CUSTWH") )
		{
			currencySQL	= "SELECT CURRENCYID FROM FS_LG_CUSTWHMASTER WHERE CUSTWHID = '"+locationId+"'";
		}
		else if(locationType.equals("WAREHOUSE") )
		{
			currencySQL	= "SELECT CURRENCYID FROM FS_LG_WHMASTER WHERE WHID = '"+locationId+"'";
		}
		else if(locationType.equals("PROJECTLOG") )
		{
			currencySQL	= "SELECT CURRENCYID FROM FS_LG_PROJECTMASTER WHERE PROJECTID = '"+locationId+"'";
		}
		try
		{
        	makeConnection();
			//Logger.info(FILE_NAME,"getCurrencyId(String locationType, String locationId)","Currency SQL : "+currencySQL);
			stmt		= connection.createStatement();
			rs		= stmt.executeQuery(currencySQL);
			if(rs.next() )
			{
				currencyId	= rs.getString(1);
			}
			if(rs != null)
				rs.close();
		}
		catch(DBSysException dbs)
		{
			//Logger.error(FILE_NAME,"getCurrencyId(String locationType, String locationId)"," Error in Retrieving Currency Id ",dbs);
      logger.error(FILE_NAME+" getCurrencyId(String locationType, String locationId)"+" Error in Retrieving Currency Id "+dbs);
			sessionContext.setRollbackOnly();
            throw FoursoftException.getException(dbs,FILE_NAME,"getELogCurrencyId()");
		}
		catch(Exception e)
		{
			//Logger.error(FILE_NAME,"getCurrencyId(String locationType, String locationId)"," Error in Retrieving Currency Id ",e);
      logger.error(FILE_NAME+" getCurrencyId(String locationType, String locationId)"+" Error in Retrieving Currency Id "+e);
			sessionContext.setRollbackOnly();
            throw FoursoftException.getException(e,FILE_NAME,"getELogCurrencyId()");
		}
		finally
		{
			ConnectionUtil.closeStatement(stmt);
			ConnectionUtil.closeConnection(connection);
		}
		return currencyId;	


	}
	
	/**
	* Used to fetch the currencyId for Freight
	* 
	* @param terminalId
	* @return cussencyId
	*/
	public String getETransCurrencyId(String terminalId) throws FoursoftException
	{
		Connection	conn	= null;
		Statement	stmt	= null;
		ResultSet	rs		= null;
		String currencyId	= "";
		
		try
		{
			conn	=	datasource.getConnection();
			
			String query2 = "SELECT CM.CURRENCYID FROM FS_FR_TERMINALMASTER TM,FS_COUNTRYMASTER CM,FS_ADDRESS AD WHERE "+
                      "TM.CONTACTADDRESSID=AD.ADDRESSID AND AD.COUNTRYID=CM.COUNTRYID AND  TM.TERMINALID= '"+terminalId+"' "+
                      "AND (ACTIVEINACTIVE='A' OR ACTIVEINACTIVE IS NULL) AND CM.INVALIDATE ='F'";
      
      String HOQuery = "SELECT CM.CURRENCYID FROM FS_FR_TERMINALMASTER TM,FS_COUNTRYMASTER CM,FS_ADDRESS AD WHERE "+
                      "TM.CONTACTADDRESSID=AD.ADDRESSID AND AD.COUNTRYID=CM.COUNTRYID AND  TM.OPER_ADMIN_FLAG='H' ";
			
			stmt	= conn.createStatement();
			rs		= stmt.executeQuery(query2);
			
			if(rs.next() )
			{
				currencyId	= rs.getString(1);
			}
      else//@@If Terminal Currency is Invalidated, get the HO Currency Id.
      {
        if(rs != null)
          rs.close();
        if(stmt!=null)
          stmt.close();
          
        stmt	= conn.createStatement();
        rs		= stmt.executeQuery(HOQuery);
        
        if(rs.next() )
        {
          currencyId	= rs.getString(1);
        }
      }
			if(rs != null)
				rs.close();
		}
		catch(Exception e)
		{
			//Logger.error(FILE_NAME,"getETransCurrencyId(String terminalId)"," Error in Retrieving Currency Id ",e);
      logger.error(FILE_NAME+" getETransCurrencyId(String terminalId)"+" Error in Retrieving Currency Id "+e);
			sessionContext.setRollbackOnly();
            throw FoursoftException.getException(e,FILE_NAME,"getETransCurrencyId()");
		}
		finally
		{
			ConnectionUtil.closeStatement(stmt);
			ConnectionUtil.closeConnection(conn);
		}
		return currencyId;	
	}
	
	/**
	* Used to check the Location is valid
	* this will check the terminal or Gateway exist or not ... this will restrict dangling users to Login
	* @param terminalId
	* @return boolean
	*/
	private boolean isValidLocation(String terminalId)
	{
		Statement	stmt	= null;
		ResultSet	rs		= null;
		boolean		flag	= false;
		try
		{

			stmt	= connection.createStatement();
			rs		= stmt.executeQuery(
						"SELECT TERMINALID FROM FS_FR_TERMINALMASTER WHERE TERMINALID = '"+terminalId+"' AND (INVALIDATE IS NULL OR INVALIDATE ='F')  "+
						"UNION "+
//						"SELECT DISTINCT COMPANYID FROM FS_AC_COMPANYINFO WHERE COMPANYID = '"+terminalId+"' "+
//						"UNION "+
						"SELECT GATEWAYID FROM FS_FR_GATEWAYMASTER WHERE GATEWAYID = '"+terminalId+"'"
						);
			
			if(rs.next() )
			{
				flag = true;
			}
			if(rs != null)
				rs.close();
		}
		catch(Exception e)
		{
			//Logger.error(FILE_NAME,"getETransCurrencyId(String terminalId)"," Error in Retrieving Currency Id ",e);
      logger.error(FILE_NAME+" getETransCurrencyId(String terminalId)"+" Error in Retrieving Currency Id ",e);
		}
		finally
		{
			ConnectionUtil.closeStatement(stmt);
		}
		return flag;	
	}

	/**
	* used to fetch the timezone from appropriate table based on the RoleLevel
	* @param locationType (ie., RoleLevel ..whether is HO, ADMIN , Operational etc...)
	*
	**/
	private String getTimeZone(String locationType, String locationId) throws FoursoftException
	{
		Statement	stmt			= null;
		ResultSet	rs				= null;
		String 		timeZone		= null;
		String 		query			= "";
		if(locationType.equals("OPER_TERMINAL") || locationType.equals("HO_TERMINAL") || locationType.equals("AGENT_TERMINAL") || locationType.equals("ADMN_TERMINAL"))
		{
			query = "SELECT TIME_ZONE FROM FS_FR_TERMINALMASTER WHERE TERMINALID = '"+locationId+"'";			
		}
		else if(locationType.equals("GATEWAY") )
		{
			query = "SELECT TIME_ZONE FROM FS_FR_GATEWAYMASTER WHERE GATEWAYID = '"+locationId+"'";			
		}
		else
		{
			query = "SELECT TIME_ZONE FROM FS_AC_COMPANYINFO WHERE COMPANYID = '"+locationId+"'";			
		}
				
		try
		{
			stmt		= connection.createStatement();
			rs		= stmt.executeQuery(query);
			if(rs.next() )
			{
				timeZone	= rs.getString(1);
			}
			if(rs != null)
				rs.close();
		}
		catch(Exception e)
		{
			//Logger.error(FILE_NAME,"getTimeZone("+locationType+", "+locationId+")"," Error in Retrieving TimeZone : ",e);
      logger.error(FILE_NAME+" getTimeZone("+locationType+", "+locationId+")"+" Error in Retrieving TimeZone : "+e);
            throw FoursoftException.getException(e,FILE_NAME,"getTimeZone");
		}
		finally
		{
			ConnectionUtil.closeStatement(stmt);
		}
		return timeZone;			

	}
	
	/**
	* this is used to retrieve the values relavant to accounts operations
	* 
	* @param terminalId
	* @return AccountsCredentials	(which consists of Books begening date, books begeing from etc.,
	*/
	public AccountsCredentials prepareAccountsCredentials(String terminalId, String accessType) throws FoursoftException
	{
		AccountsCredentials accountsCredentials = new AccountsCredentials();
		
		Connection			con			= null;
		PreparedStatement 	pstmt 		= null;
		PreparedStatement	pstmt1		= null;
		java.sql.Timestamp 	bbdate   	= null;
		ResultSet 			rs          = null;
		java.sql.Timestamp	sdate		= null;
		long 				bookid		= 0;
		Integer 			bkdays 		= null;
		String 				booksStatus	="";
		String				acctId		="";
		String				tranId		="";
		
		String				acctYear	= "";
		String				module		= "";
		
		int 				backdays 	= 0;
		java.sql.Timestamp	backdate 	= null;
		java.sql.Timestamp		finenddate 	= null;		
		//String 				companyId 	= terminalId.substring(0,3);

		java.sql.Timestamp ts			= new java.sql.Timestamp(new java.util.Date().getTime());
		java.util.Calendar c			= new java.util.GregorianCalendar() ;
		c.setTime(ts);
		int year  = c.get(java.util.Calendar.YEAR);
		int month = c.get(java.util.Calendar.MONTH);
		int date  = c.get(java.util.Calendar.DATE);
		
		java.util.Calendar 	cC			= new java.util.GregorianCalendar(year,month,date);
		java.sql.Timestamp 	ts1			= new java.sql.Timestamp(cC.getTime().getTime());

		String GET_BBDATE = " SELECT BOOKID,MAX(BOOKSSTARTINGFROM),BOOKSSTATUS,BACKDAYS,BACKDATE,FINANCIALYEARTO, ACCTYEAR, MODULE FROM  FS_AC_COMPANYINFO " +
							" WHERE COMPANYID = ? AND BOOKSSTARTINGFROM <= ? GROUP BY BOOKID,BOOKSSTATUS,BACKDAYS,BACKDATE,FINANCIALYEARTO, ACCTYEAR, MODULE ";

		//String GET_ACCOUNTINGID =" SELECT ACCOUNTID,TXID FROM FS_AC_ACCTIDSETUP WHERE COMPANYID=? " ;
		String GET_ACCOUNTINGID =" SELECT ACCOUNTID,TXID FROM FS_AC_ACCTIDSETUP" ;
							
		try
		{
			con		=	datasource.getConnection();
			pstmt	=	con.prepareStatement(GET_BBDATE);
			pstmt.setString(1,terminalId);
			pstmt.setTimestamp(2,ts1);						
			rs=pstmt.executeQuery();
			while(rs.next())
			{
				bookid      = rs.getLong(1);
				bbdate      = rs.getTimestamp(2);
				booksStatus = rs.getString(3);
				backdays    = rs.getInt(4);
				backdate    = rs.getTimestamp(5); 
				finenddate  = rs.getTimestamp(6); 
				acctYear	= rs.getString(7);
				module		= rs.getString(8);
				
			}
			
			if(rs!=null)
				rs.close();
			sdate=bbdate;
			accountsCredentials.setBookId(bookid);
			accountsCredentials.setBooksBeginingFrom(sdate);			
			accountsCredentials.setBooksStatus(booksStatus);
			if(acctYear != null)
				accountsCredentials.setAcctYear(acctYear);
			if(module != null)
				accountsCredentials.setModule(module);

			//if(!accessType.equalsIgnoreCase("GATEWAY")) {
				pstmt1=con.prepareStatement(GET_ACCOUNTINGID);
				//pstmt1.setString(1,companyId);
				rs=pstmt1.executeQuery();
				while(rs.next())
				{
					acctId = rs.getString(1);
					tranId = rs.getString(2);				
				}
				if(rs!=null)
					rs.close();
			//} else {
			//	acctId = "yes";
			//	tranId = "yes";
			//}
			accountsCredentials.setAcctIdStatus(acctId);
			accountsCredentials.setTransactionIdStatus(tranId);			
			accountsCredentials.setBackDays(backdays);
			accountsCredentials.setBackDate(backdate);
			accountsCredentials.setFinancialYearTo(finenddate);
			
			// This is to facilitate Accounts module as they donot need to check the module
			accountsCredentials.setAcctCompanyId( terminalId );
		}
		catch(SQLException se)
		{
			//Logger.error(FILE_NAME,"prepareAccountsCredentials(String terminalId)","SQL Excpetion in retreiving the acct details : ",se);
      logger.error(FILE_NAME+" prepareAccountsCredentials(String terminalId)"+"SQL Excpetion in retreiving the acct details : "+se);
			sessionContext.setRollbackOnly();
            throw FoursoftException.getException(se,FILE_NAME,"prepareAccountsCredentials");
		}
		catch(Exception e)
		{
			//Logger.error(FILE_NAME,"prepareAccountsCredentials(String terminalId)","Excpetion in retreiving the acct details : ",e); 
      logger.error(FILE_NAME+" prepareAccountsCredentials(String terminalId)"+" Excpetion in retreiving the acct details : "+e); 
			sessionContext.setRollbackOnly();
            throw FoursoftException.getException(e,FILE_NAME,"prepareAccountsCredentials");
		}
		finally
		{
			try
			{
				ConnectionUtil.closeStatement(pstmt);
				ConnectionUtil.closeStatement( pstmt1);
				ConnectionUtil.closeConnection( con );
			}
			catch(Exception e)
			{ }
		}
		return accountsCredentials;
	} //end of prepareAccountsCredentials(String terminalId)

	/**
	* this is used to retrieve the preferences of date formart, UOM etc relavant to the User
	* who is logging into the application
	* 
	* @param userId (String)
	* @param locationId (String)
	* @return AccountsCredentials	(which consists of Books begening date, books begeing from etc.,
	*/
	
	private UserPreferences getUserPreferences( String userId, String locationId) throws FoursoftException
    {
		
		UserPreferences		userPreferences	=	null;
		PreparedStatement	pstmt	= null;
		ResultSet			rs		= null;
		
		String	dateFormat	=	"";
		String	dimension	=	"";
		String	weight		=	"";
		String	volume		=	"";
		String  lovSize		=   "";
		String  segmentSize	=   "";
		String  language	=   "";
		
		try {
			
			pstmt = connection.prepareStatement( UserPrefQuery );
				
			pstmt.setString(1, userId);
			pstmt.setString(2, locationId);
			
			rs	=	pstmt.executeQuery();
			
			while(rs!=null && rs.next()) {
				
				String	param_name	=	rs.getString(1);
				String	param_value	=	rs.getString(2);
				
				if(param_value!=null && param_value.length() > 0) {
					if(param_name.equals("date_format"))
						dateFormat	=	param_value;
					if(param_name.equals("dimension"))
						dimension	=	param_value;
					if(param_name.equals("weight"))
						weight		=	param_value;
					if(param_name.equals("volume"))
						volume		=	param_value;
					if(param_name.equals("lovPageSize"))
						lovSize		=	param_value;
					if(param_name.equals("segmentSize"))
						segmentSize	=	param_value;
					if(param_name.equals("language"))
						language	=	param_value;
				}
				
			}			
						
		} catch(SQLException se) {
			//Logger.error(FILE_NAME,"getUserPreferences(String userId, String locationId)","SQL Exception in retriving the User Preferences : ",se); 
      logger.error(FILE_NAME+" getUserPreferences(String userId, String locationId)"+" SQL Exception in retriving the User Preferences : "+se); 
            throw FoursoftException.getException(se,FILE_NAME,"getUserPreferences()");
		} catch(Exception e) {
			//Logger.error(FILE_NAME,"getUserPreferences(String userId, String locationId)","Exception in retriving the User Preferences : ",e); 
            throw FoursoftException.getException(e,FILE_NAME,"getUserPreferences()");
		} finally {
			try {
				if(rs!=null)
					rs.close();
				ConnectionUtil.closeStatement(pstmt);
			} catch(Exception e) { }
			
			// Create the value object
			userPreferences = new UserPreferences(dateFormat, dimension, weight, volume, lovSize, segmentSize, language);
		}
		
		return userPreferences;
	}


	/**
	* This is a private method which is used to get the connection from the Connection pool
	*
	* @exception NamingException
	* @exception SQLException
	*
	* @return Connection
	*
	*/
	private void makeConnection() throws DBSysException
	{
        try 
		{
           	connection = datasource.getConnection();
        } 
		catch (SQLException se) 
		{
            throw new DBSysException("SQLException ocurred while getting " +
                                      "DB connection : \n" + se.getMessage());
        }	
	} // end of makeConnection()


	/**
	* this is used to retrieve the role permissions for optional roles for an
	* ELOG's WAREHOUSE User and for and EP's COMPANY User on the fly
	* when he switches between facilities / warehouses
	* 
	* @param userId (String)
	* @param userLocationId (String)
	* @param otherRoleLocationId (String)
	* @return java.util.Hashtable; the Role permissions for the Role i.e. TXIDs and their access levels
	*/

	public static final String	OPT_ROLE_QUERY = "SELECT ROLEID, ROLE_LOCATIONID FROM FS_OPTIONALROLES WHERE USERID = ? AND LOCATIONID = ?";

	public static final String	OPT_PERMISSIONS_LIST_QUERY = "SELECT TXID, ACCESSLEVEL FROM FS_ROLEPERMISSIONS WHERE ROLEID = ? ORDER BY TXID ";	
	
	public Hashtable getOptionalRolePermissions(String userId, String userLocationId, String otherRoleLocationId) throws FoursoftException
    {

		String	roleId	=	"";
		
		PreparedStatement	pstmt	= null, pstmt1 = null;
		ResultSet			rs		= null;

		Hashtable userPermissions = new Hashtable();
		
		try
		{
			makeConnection();

			// First get the Role Id for the Company User for the warehouse

			pstmt	= connection.prepareStatement(OPT_ROLE_QUERY);

			pstmt.setString(1, userId);
			pstmt.setString(2, userLocationId );
			
			rs		= pstmt.executeQuery();
				
			while(rs.next())
			{
				String registeredRoleId 		= rs.getString(1);
				String registeredLocationId 	= rs.getString(2);

				if(otherRoleLocationId.equals( registeredLocationId )) {
					roleId = registeredRoleId;
					break;
				}
			}
			if(pstmt!=null)
				pstmt.close();
			if(rs!=null)
				rs.close();

			// Now get the actual permissions

			pstmt1	= connection.prepareStatement(OPT_PERMISSIONS_LIST_QUERY);
			
			pstmt1.setString(1, roleId);
			
			rs		= pstmt1.executeQuery();
			
			String txId = "";
			int access = 0;

			while(rs.next())
			{
				txId 	= rs.getString(1);
				access		= rs.getInt(2);
			    userPermissions.put(txId, new Integer(access));
			}
			if(rs!=null)
				rs.close();
		}
		catch(SQLException sql)
		{
			//Logger.error(FILE_NAME,"getRolePermissions(String roleId, String locationId)","SQLException ",sql);
      logger.error(FILE_NAME+" getRolePermissions(String roleId, String locationId)"+" SQLException "+sql);
			//throw new EJBException(sql.getMessage());
            sessionContext.setRollbackOnly();
            throw FoursoftException.getException(sql,FILE_NAME,"getOptionalRolePermissions()");
		}		
		catch(DBSysException dbs)
		{
			//Logger.error(FILE_NAME,"getOptionalRolePermissions(String userId, String userLocationId, String otheRoleLocationId)","DBSysException ",dbs);
      logger.error(FILE_NAME+" getOptionalRolePermissions(String userId, String userLocationId, String otheRoleLocationId)"+"DBSysException "+dbs);
			//throw new EJBException(dbs.getMessage());
            sessionContext.setRollbackOnly();
            throw FoursoftException.getException(dbs,FILE_NAME,"getOptionalRolePermissions()");
		}
		finally
		{
			ConnectionUtil.closeConnection(connection, pstmt1);
		}
		
		return userPermissions;
	}  // getOptionalRolePermissions
	
		/**
	 *	Tjis code is added for temporaryly to set HOId for ELog
	 * @date 28-01-03
	 */

	private void setHOIdForELog(String userId, String locationId, ESupplyGlobalParameters globalParam) throws FoursoftException
	{
		String sqlQuery ="SELECT T.TERMINALID FROM FS_USERMASTER U, FS_FR_TERMINALMASTER T WHERE U.LOCATIONID=T.TERMINALID"+
			" AND T.OPER_ADMIN_FLAG='H' AND U.USERID= ? AND U.LOCATIONID= ? ";
		PreparedStatement pStmt = null;
		ResultSet rs = null;
		try
		{

			pStmt = connection.prepareStatement(sqlQuery);
			pStmt.setString(1,userId);
			pStmt.setString(2,locationId);
			rs = pStmt.executeQuery();
			if (rs.next())
			{				
				globalParam.setHOId(rs.getString(1));
			}
			else
				globalParam.setHOId(null);
		}
		catch(Exception exc)
		{
			//Logger.error(FILE_NAME,"setHOIdForELog(String userId, String locationId,ESupplyGlobalParameters globalParam)","EXCEPTION IS ",exc);	
      logger.error(FILE_NAME+" setHOIdForELog(String userId, String locationId,ESupplyGlobalParameters globalParam)"+"EXCEPTION IS "+exc);	
			//throw new SQLException(exc.toString());
            throw FoursoftException.getException(exc,FILE_NAME,"setHOIdForELog()");
		}
		finally
		{
			try
			{
				ConnectionUtil.closePreparedStatement( pStmt,rs);
			}
			catch(Exception exp)
			{
				//Logger.error(FILE_NAME,"setHOIdForELog(String userId, String locationId,ESupplyGlobalParameters globalParam)","EXCEPTION IS ",exp);	
        logger.error(FILE_NAME+"setHOIdForELog(String userId, String locationId,ESupplyGlobalParameters globalParam)"+"EXCEPTION IS "+exp);	
			}
		}
	}

	/**
	 *	This method is added for password encryption using SHA Algorithm with MessageDigest
	 */
    private String encryptSHA(String password)
    {
        MessageDigest md = null;
        try
        {
            md = MessageDigest.getInstance("SHA-1");
        }
        catch(NoSuchAlgorithmException noAlgori)
        {
            //Logger.error("UserDAO", "encryptSHA(String password)","Error while getting MessageDigest for Password Encryption ",noAlgori);
            logger.error("UserDAO"+ "encryptSHA(String password)"+"Error while getting MessageDigest for Password Encryption "+noAlgori);
			throw new EJBException(noAlgori);
        }
        try
        {
            md.update(password.getBytes("UTF-8"));
        }
        catch(UnsupportedEncodingException unSupportErr)
        {
			//Logger.error("UserDAO", "encryptSHA(String password)","Error while generating Encrypted Password ",unSupportErr);
      logger.error("UserDAO"+ "encryptSHA(String password) "+"Error while generating Encrypted Password "+unSupportErr);
			throw new EJBException(unSupportErr);
        }
        byte raw[] = md.digest();
        return (new BASE64Encoder()).encode(raw);
    }

    public boolean getPasswordModifiedDate(String userId, String locationId) throws FoursoftException
    {
        Statement stmt = null;
		ResultSet rs   = null;	
		boolean passwordCorssed = false;
		Timestamp passwordDate = null;
		long x = 0l;
		int duration = 30;

		String SQL_PASSWORD = "SELECT PASSWORDDATE FROM FS_PASSWORDHISTORY WHERE USERID ='"+userId+"' AND LOCATIONID = '"+locationId+"'";
		String SELECTQUERY_UPDATE = "SELECT PASSWORD_DURATION FROM FS_PASSWORDCONFIGURATION";
		
		try
		{
			makeConnection();
			stmt = connection.createStatement();
			rs = stmt.executeQuery(SELECTQUERY_UPDATE);
			if (rs.next())
				duration = rs.getInt(1);
			
			rs.close();
			rs = null;
			stmt.close();
			stmt = null;

			stmt = connection.createStatement();
			rs = stmt.executeQuery(SQL_PASSWORD);
			if (rs.next())
				passwordDate = rs.getTimestamp(1);

			long y = new java.util.Date().getTime();

			if (passwordDate != null)
				x = passwordDate.getTime();
			
			rs.close();
			rs = null;
			stmt.close();
			stmt = null;

			long z = duration * 86400;
			long diff = (y - x)/1000;
			if (diff > z)
			{
				passwordCorssed = true;
				SQL_PASSWORD = "UPDATE FS_PASSWORDHISTORY SET ALERTCOUNT = ALERTCOUNT+1 WHERE USERID ='"+userId+"' AND LOCATIONID = '"+locationId+"'";
				stmt = connection.createStatement();
				int i = stmt.executeUpdate(SQL_PASSWORD);
			}
		}
		catch(Exception exp)
		{
            //Logger.error(FILE_NAME,"getPasswordModifiedDate(String userId, String LocationId)","Exception ",exp);
            logger.error(FILE_NAME+" getPasswordModifiedDate(String userId, String LocationId)"+"Exception "+exp);
            sessionContext.setRollbackOnly();
            throw FoursoftException.getException(exp,FILE_NAME,"getPasswordModifiedDate()");
		}
		finally
		{
			try
			{
                ConnectionUtil.closeConnection(connection, stmt, rs);
			}
			catch(Exception exp)
			{
				//Logger.error(FILE_NAME,"getPasswordModifiedDate(String userId, String LocationId)","Exception ",exp);
        logger.error(FILE_NAME+" getPasswordModifiedDate(String userId, String LocationId)"+" Exception "+exp);
			}
		}

		return passwordCorssed;
    }

    public boolean getWarningCountExceeded(String userId, String locationId) throws FoursoftException
    {
        Statement stmt = null;
		ResultSet rs   = null;	
		int retValue = 0;

		String SQL_WORNING = "SELECT ALERTCOUNT FROM FS_PASSWORDHISTORY WHERE USERID ='"+userId+"' AND LOCATIONID = '"+locationId+"'";
        try
        {
            makeConnection();
			stmt = connection.createStatement();
			rs = stmt.executeQuery(SQL_WORNING);
			if (rs.next())
                retValue = rs.getInt(1);
        }
        catch(Exception exp)
		{
            //Logger.error(FILE_NAME,"getWarningCountExceeded(String userId, String LocationId)","Exception ",exp);
            logger.error(FILE_NAME+" getWarningCountExceeded(String userId, String LocationId)"+"Exception "+exp);
            sessionContext.setRollbackOnly();
            throw FoursoftException.getException(exp,FILE_NAME,"getWarningCountExceeded()");
		}
		finally
		{
			try
			{
				ConnectionUtil.closeConnection(connection, stmt, rs);
			}
			catch(Exception exp)
			{
				//Logger.error(FILE_NAME,"getWarningCountExceeded","EXCEPTION IS ",exp);	
        logger.error(FILE_NAME+" getWarningCountExceeded"+"EXCEPTION IS "+exp);	
			}
		}
        if (retValue >= 5)
            return true;
        return false;
    }

	public boolean getLoginCount(String userId, String locationId) throws FoursoftException
    {
        Statement stmt = null;
		ResultSet rs   = null;	
		int retValue = 0;
    boolean boolValue=false;

		String SQL_LOGINCOUNT = "SELECT LOGINCOUNT FROM FS_USERMASTER WHERE USERID ='"+userId+"' AND LOCATIONID = '"+locationId+"'";
        try
        {
            
			makeConnection();
			stmt = connection.createStatement();
			rs = stmt.executeQuery(SQL_LOGINCOUNT);
			if (rs.next())
                retValue = rs.getInt(1);

			if (retValue == 0)
			{
				boolValue=true;
			}
      if(stmt!=null)
         stmt.close();
				String SQL_COUNTPLUS = "UPDATE FS_USERMASTER SET LOGINCOUNT = LOGINCOUNT+1 WHERE USERID ='"+userId+"' AND LOCATIONID = '"+locationId+"'";
				stmt = connection.createStatement();
				int i = stmt.executeUpdate(SQL_COUNTPLUS);
	    }
        catch(Exception exp)
		{
            //Logger.error(FILE_NAME,"getLoginCount(String userId, String LocationId)","Exception ",exp);
            logger.error(FILE_NAME+" getLoginCount(String userId, String LocationId)"+"Exception "+exp);
            sessionContext.setRollbackOnly();
            throw FoursoftException.getException(exp,FILE_NAME,"getLoginCount()");
		}
		finally
		{
			try
			{
				ConnectionUtil.closeConnection(connection, stmt, rs);
			}
			catch(Exception exp)
			{
				//Logger.error(FILE_NAME,"getLoginCount","EXCEPTION IS ",exp);	
        logger.error(FILE_NAME+" getLoginCount"+"EXCEPTION IS "+exp);	

			}
		}
        
        return boolValue;
    }

		public int getNoFailAttempts() throws FoursoftException
		{

		Statement stmt = null;
		ResultSet rs=null;
		int attempts = 3;
		String SELECT_QUERY = "SELECT NO_FAIL_ATTEMPTS FROM FS_PASSWORDCONFIGURATION";
		try
		{
			makeConnection();
			stmt = connection.createStatement();
			rs = stmt.executeQuery(SELECT_QUERY);
			if (rs.next())
                attempts = rs.getInt(1);
		
		if(attempts==0)
			attempts = 3;
		
		}
		catch(Exception exp)
		{
            //Logger.error(FILE_NAME,"getNoFailAttempts()","Exception ",exp);
            logger.error(FILE_NAME+" getNoFailAttempts()"+"Exception "+exp);
            sessionContext.setRollbackOnly();
            throw FoursoftException.getException(exp,FILE_NAME,"getNoFailAttempts()");
		}
		finally
		{
			try
			{
				ConnectionUtil.closeConnection(connection, stmt, rs);
			}
			catch(Exception exp)
			{
				//Logger.error(FILE_NAME,"getNoFailAttempts","EXCEPTION IS ",exp);	
        logger.error(FILE_NAME+" getNoFailAttempts"+" EXCEPTION IS "+exp);	
        
			}
		}
        
        return attempts;
    }


	
	
	public boolean lockUser(String userId, String locationId) throws FoursoftException
		{

		PreparedStatement pStmt = null;
		//ResultSet rs=null;//Commented By RajKumari on 23-10-2008 for Connection Leakages.
		try
		{
		makeConnection();
		// INACTIVE =0;
		pStmt = connection.prepareStatement("UPDATE FS_USERMASTER SET USERSTATUS=0 WHERE USERID=? AND LOCATIONID=?");
		pStmt.setString(1,userId);
		pStmt.setString(2,locationId);
		int i=pStmt.executeUpdate();
		
		if(i>0)
		return true;
		
		}catch(Exception exc)
		{
			//Logger.error(FILE_NAME,"lockUser(String userId, String locationId)","EXCEPTION IS ",exc);	
      logger.error(FILE_NAME+"lockUser(String userId, String locationId)"+"EXCEPTION IS "+exc);	
			//throw new SQLException(exc.toString());
            throw FoursoftException.getException(exc,FILE_NAME,"setHOIdForELog()");
		}
		finally
		{
			try
			{
      //Modified By RajKumari on 23-10-2008 for Connection Leakages.
				//ConnectionUtil.closeConnection(connection,pStmt, rs);
        ConnectionUtil.closeConnection(connection,pStmt);
			}
			catch(Exception exp)
			{
				//Logger.error(FILE_NAME,"lockUser(String userId, String locationId)","EXCEPTION IS ",exp);	
        logger.error(FILE_NAME+"lockUser(String userId, String locationId)"+"EXCEPTION IS "+exp);	
			}
		}
			return false;
	
	}

	public boolean unlockUser(String userId, String locationId) throws FoursoftException
		{

		PreparedStatement pStmt = null;
		//ResultSet rs=null;//Commented By RajKumari on 23-10-2008 for Connection Leakages.
		try
		{
		makeConnection();
		// INACTIVE =0;
		pStmt = connection.prepareStatement("UPDATE FS_USERMASTER SET USERSTATUS=1 WHERE USERID=? AND LOCATIONID=?");
		pStmt.setString(1,userId);
		pStmt.setString(2,locationId);
		int i=pStmt.executeUpdate();
		
		if(i>0)
		return true;
		
		}catch(Exception exc)
		{
			//Logger.error(FILE_NAME,"lockUser(String userId, String locationId)","EXCEPTION IS ",exc);	
      logger.error(FILE_NAME+" lockUser(String userId, String locationId)"+"EXCEPTION IS "+exc);	
			//throw new SQLException(exc.toString());
            throw FoursoftException.getException(exc,FILE_NAME,"setHOIdForELog()");
		}
		finally
		{
			try
			{
      //Modified By RajKumari on 23-10-2008 for Connection Leakages.
				//ConnectionUtil.closeConnection(connection, pStmt, rs);
        ConnectionUtil.closeConnection(connection, pStmt);
			}
			catch(Exception exp)
			{
				//Logger.error(FILE_NAME,"lockUser(String userId, String locationId)","EXCEPTION IS ",exp);	
        logger.error(FILE_NAME+" lockUser(String userId, String locationId)"+"EXCEPTION IS "+exp);	
			}
		}
			return false;
	
	}



private  HashMap  getCodeCustProjectLevel(String accessType,String locationId,Connection connection) throws java.sql.SQLException
{ 
  String sqlQuery = "";
  String sqlQuery1 = "";
  PreparedStatement pStmt = null;
	ResultSet rs = null;
  PreparedStatement pStmt1 = null;
	ResultSet rs1 = null;
  HashMap hMap = new HashMap();

  ArrayList codeList = new ArrayList(10);
  try{
    if(accessType.equals("WAREHOUSE"))
      sqlQuery = "SELECT CUSTWHID,PROJECTID FROM FS_LG_CUSTWHMASTER WHERE WHID = ?";
    else if(accessType.equals("CUSTWH"))
        sqlQuery = "SELECT CUSTWHID,PROJECTID FROM FS_LG_CUSTWHMASTER WHERE CUSTWHID = ?";
    else if(accessType.equals("PROJECTLOG"))
        sqlQuery = "SELECT CUSTWHID,PROJECTID FROM FS_LG_CUSTWHMASTER WHERE PROJECTID = ?";
    else if(accessType.equals("ELVRM"))
        sqlQuery = "SELECT CUSTWHID, PROJECTID FROM FS_LG_CUSTWHMASTER WHERE CUSTWHID IN(" +
					"SELECT CUSTWHID FROM FS_LG_PARTYMASTER PM, FS_LG_PARTYMASTERDTL PD WHERE "+
					"PM.PARTYID=PD.PARTYID AND PM.PARTYID=? AND PM.PARTYFLAG='VENDOR')";
    else if(accessType.equals("ELCRM"))
        sqlQuery = "SELECT CUSTWHID, PROJECTID FROM FS_LG_CUSTWHMASTER WHERE CUSTWHID IN(" +
					"SELECT CUSTWHID FROM FS_LG_PARTYMASTER PM, FS_LG_PARTYMASTERDTL PD WHERE "+
					"PM.PARTYID=PD.PARTYID AND PM.PARTYID=? AND PM.PARTYFLAG='CUSTOMER')";
    //System.out.println("sqlQuery-------------"+sqlQuery);
      sqlQuery1 = "SELECT CODEIDNAME  FROM FS_LG_CONFIGPARAM WHERE PROJECTID = ?";
      // System.out.println("sqlQuery1------"+sqlQuery1);
      pStmt = connection.prepareStatement(sqlQuery);
      pStmt.setString(1,locationId);
      rs = pStmt.executeQuery();
      pStmt1 = connection.prepareStatement(sqlQuery1);
      String custWHId = "";
      ArrayList temp = new ArrayList();
      String projectId = "";
       while(rs.next())
      {
        custWHId = rs.getString(1);
        projectId = rs.getString(2);
          pStmt1.setString(1,projectId);
          rs1 = pStmt1.executeQuery();
          while(rs1.next()){
            codeList.add(rs1.getString("CODEIDNAME"));
           }
          temp = (ArrayList)codeList.clone();
           hMap.put(custWHId,temp);
           
           codeList.clear();
           pStmt1.clearParameters();
           custWHId="";
            if(rs1!= null)
                rs1.close();

      }
  }catch(Exception exc)
  {
    //Logger.error(FILE_NAME,"getCodeCustStatus","EXCEPTION IS ", exc);	
    logger.error(FILE_NAME+"getCodeCustStatus"+"EXCEPTION IS "+ exc);	
		throw new SQLException(exc.toString());
  }finally
  {
   
  if(pStmt1 != null)
    pStmt1.close();//Modified By RajKumari on 23-10-2008 for Connection Leakages.
  if(rs!= null)
    rs.close();
  if(pStmt != null)
    pStmt.close();
    
  }

  return hMap;
}
//@@Added for no'of users logged
public boolean logOutUpdate(String userId, String locationId) throws FoursoftException
{
    
    PreparedStatement pStmt	= null;    
	int retValue = 0;
boolean boolValue=false;
String logOutTimeQry = "UPDATE QMS_USERLOG SET LOGOUTTIME = ? WHERE ID=(SELECT MAX(ID) FROM QMS_USERLOG WHERE USERID ='"+userId+"' AND LOCATIONID = '"+locationId+"' AND LOGOUTTIME IS  NULL )AND  USERID ='"+userId+"' AND LOCATIONID = '"+locationId+"' AND LOGOUTTIME IS  NULL";
    try
    {
        
		makeConnection();
		pStmt	=	connection.prepareStatement(logOutTimeQry);
		pStmt.setTimestamp(1, new java.sql.Timestamp((new java.util.Date()).getTime()));
		retValue = pStmt.executeUpdate();

		if (retValue == 1)
			boolValue=true;
		else
			boolValue	= false;
		
  /*if(pStmt!=null)
	  pStmt.close();*/
    }
    catch(Exception exp)
	{
        //Logger.error(FILE_NAME,"getLoginCount(String userId, String LocationId)","Exception ",exp);
        logger.error(FILE_NAME+" logOutUpdate(String userId, String LocationId)"+"Exception "+exp);
        sessionContext.setRollbackOnly();
        throw FoursoftException.getException(exp,FILE_NAME,"getLoginCount()");
	}
	finally
	{
		try
		{
			ConnectionUtil.closeConnection(connection, pStmt);
		}
		catch(Exception exp)
		{
			//Logger.error(FILE_NAME,"getLoginCount","EXCEPTION IS ",exp);	
    logger.error(FILE_NAME+" logOutUpdate"+"EXCEPTION IS "+exp);	

		}
	}
    
    return boolValue;
}
//@@ended for no'of users logged
}

