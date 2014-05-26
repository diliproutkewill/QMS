/**
 * 
 * Copyright (c) 2000-2001 by FourSoft, Inc. All Rights Reserved.
 * This software is the proprietary information of FourSoft, Pvt Ltd.
 * Use is subject to license terms.
 *
 * esupply - v 1.x 
 *
 */
 
package com.foursoft.esupply.accesscontrol.dao;

import java.sql.Connection; 
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;

import java.util.ArrayList;


import javax.ejb.EJBException;

import com.foursoft.esupply.common.exception.DBSysException;
import com.foursoft.esupply.accesscontrol.java.RoleModel;
import com.foursoft.esupply.common.util.ConnectionUtil;
import com.foursoft.esupply.common.dao.BaseDAOImpl;
//import com.foursoft.esupply.common.util.Logger;
import com.foursoft.esupply.common.java.FoursoftConfig;
import org.apache.log4j.Logger;

/**
 * File			: UserRoleDAO
 * sub-module	: AccessControl
 * module		: esupply
 *
 * This is DAO, which is used fetch the details locationIds, which changes freequently
 *
 * @author	Madhusudhan Rao. P
 * @date		17-08-01
 *
 * Modified History
 * 2002-01-27 -- Madhusudhana Rao. P
 * - canged the queries for Fetching the location Ids 
 *				(this time it addresses the Accounting Terminals which is not there in Freight Terminals
 * 2002-01-28 -- Madhusudhana Rao. P
 *		Fixed the problem of Not able Creating Role and User for GateWays also.
 * 		Here Locations are fetching from FS_FR_GATEWAYMASTER instead of FS_FR_TERMINALMASTER
 * 2002-11-19
 * -- Amit Parekh
 *		Changes for modules EP and ELOG made as different products with different access types.
 * 		Bug fixex for an ELOG's COMPANY User creating CUSTWH Role.
 * 2002-12-11
 * -- Amit Parekh
 *		Changes for ELOG's HO access type for fetching location ids respective to the HO id.
 */

public class UserRoleDAO extends BaseDAOImpl
{
	private static final String fileName="UserRoleDAO";
  private static Logger logger = null;

	public UserRoleDAO()
	{
		//super();
    logger  = Logger.getLogger(UserRoleDAO.class);
	}

	/**
	* Which is used to getting the LocationIds
	* @param locationId
	* @param userType - access type of the user who is creating Role/User
	* @param accessType		- access type to which User/role creating
	* @exception	SQLException
	*
	* @retun ArrayList	- list of locations
	*/
	public ArrayList getLocationIds( String locationId, String userType, String accessType, String filter)
	{
 
		ArrayList locationList = new ArrayList();
		String locationQry = "";
		String locationFilter = filter.toUpperCase();
		System.out.println("userType = "+userType+" accessType = "+accessType);
		//Logger.info(fileName,"getLocationIds( String locationId, String userType, String accessType, String locationFilter )","Location Id - User Type - Access Type - LocationFilter :: " + locationId+"-"+userType+"-"+accessType+"-"+locationFilter );
    logger.info(fileName+" getLocationIds( String locationId, String userType, String accessType, String locationFilter )"+"Location Id - User Type - Access Type - LocationFilter :: " + locationId+"-"+userType+"-"+accessType+"-"+locationFilter );
		if( userType.equals( "LICENSEE" ) )
		{

			if( accessType.equals( "LICENSEE" ))
			{	
				locationQry = "SELECT LOCATIONID FROM FS_USERMASTER "
						+"WHERE USER_LEVEL = 'LICENSEE' AND LOCATIONID LIKE '"+locationFilter+"%' ";
			}

			if( accessType.equals( "HO_TERMINAL" ))
			{	
				locationQry = "SELECT TERMINALID FROM FS_FR_TERMINALMASTER "
						//+"WHERE OPER_ADMIN_FLAG = 'H'  AND  TERMINALID LIKE '"+locationFilter+"%' ";
            +"WHERE OPER_ADMIN_FLAG = 'H' AND INVALIDATE='F' AND  TERMINALID LIKE '"+locationFilter+"%' ";
			}
			
			if( accessType.equals( "ADMN_TERMINAL" ) )
			{	
				locationQry = "SELECT TERMINALID FROM FS_FR_TERMINALMASTER "
						//+"WHERE OPER_ADMIN_FLAG = 'A'  AND TERMINALID LIKE '"+locationFilter+"%' ";
            +"WHERE OPER_ADMIN_FLAG = 'A' AND INVALIDATE='F' AND TERMINALID LIKE '"+locationFilter+"%' ";
			}
			
			// To be activated for eLog when it is using HO. Comfirm the clumn names and table name for "HO" functionality
			else if( accessType.equals( "HO" ) )
			{
				locationQry = "SELECT HOID FROM FS_LG_HOMASTER WHERE HOID LIKE '"+locationFilter+"%' ORDER BY HOID";
			}

			else if( accessType.equals( "COMPANY" ) )
			{
				locationQry = "SELECT COMPANYID FROM FS_LG_COMPANYMASTER WHERE COMPANYID LIKE '"+locationFilter+"%' ORDER BY COMPANYID";
			}
			
		}
		else if( userType.equals( "HO_TERMINAL" ) )
		{
			if( accessType.equals( "HO_TERMINAL" ) )
			{	
				locationQry = "SELECT TERMINALID FROM FS_FR_TERMINALMASTER "
						//+"WHERE OPER_ADMIN_FLAG = 'H' AND INVALIDATE='F' AND TERMINALID LIKE '"+locationFilter+"%' ";
            +"WHERE OPER_ADMIN_FLAG = 'H'  AND TERMINALID LIKE '"+locationFilter+"%' ";
			}
			else if( accessType.equals( "ADMN_TERMINAL" ) )
			{	
				locationQry = "SELECT TERMINALID FROM FS_FR_TERMINALMASTER "
						//+"WHERE OPER_ADMIN_FLAG = 'A'  AND TERMINALID LIKE '"+locationFilter+"%' ";
            +"WHERE OPER_ADMIN_FLAG = 'A' AND INVALIDATE='F'  AND TERMINALID LIKE '"+locationFilter+"%' ";
			}
			else if( accessType.equals( "OPER_TERMINAL" ) )
			{
				locationQry = "SELECT TERMINALID FROM FS_FR_TERMINALMASTER "
						//+"WHERE OPER_ADMIN_FLAG='O'  AND TERMINALTYPE='S' AND TERMINALID LIKE '"+locationFilter+"%' "
            +"WHERE OPER_ADMIN_FLAG='O' AND INVALIDATE='F' AND TERMINALTYPE='S' AND TERMINALID LIKE '"+locationFilter+"%' "
						+"UNION "
						+"SELECT DISTINCT COMPANYID FROM FS_AC_COMPANYINFO "
						+"WHERE COMPANYID LIKE '"+locationFilter+"%' AND MODULE='EACT'";
			}
			else if( accessType.equals( "AGENT_TERMINAL" ) )
			{
				locationQry = "SELECT TERMINALID FROM FS_FR_TERMINALMASTER "
						+"WHERE TERMINALTYPE='N' AND TERMINALID LIKE '"+locationFilter+"%' ";
			}
			else if( accessType.equals( "GATEWAY" ) )
			{
				locationQry	= "SELECT GATEWAYID FROM FS_FR_GATEWAYMASTER "
						+"WHERE GATEWAYID LIKE '"+locationFilter+"%' AND LENGTH(GATEWAYID) = 4";
			}				
			else if( accessType.equals( "ETCRM" ) )
			{
				locationQry = "SELECT CUSTOMERID FROM FS_FR_CUSTOMERMASTER WHERE TERMINALID ='" + locationId + "' AND REGISTERED='R' AND CUSTOMERID LIKE '"+locationFilter+"%' ORDER BY CUSTOMERID";
			}
			
			
			else if( accessType.equals( "ESCRM" ) )
			{
				locationQry = "SELECT CUSTOMERID FROM FS_FR_CUSTOMERMASTER WHERE TERMINALID ='" + locationId + "' AND REGISTERED='R' AND CUSTOMERID LIKE '"+locationFilter+"%' ORDER BY CUSTOMERID";
			}
			else if( accessType.equals( "COMPANY" ) )
			{
				locationQry = "SELECT COMPANYID FROM FS_LG_COMPANYMASTER WHERE COMPANYID LIKE '"+locationFilter+"%' ORDER BY COMPANYID";
			}
			else if( accessType.equals( "PROJECTLOG" ) )
			{
				locationQry = "SELECT PROJECTID FROM FS_LG_PROJECTMASTER WHERE PROJECTID LIKE '"+locationFilter+"%' ORDER BY PROJECTID";
			}
			else if( accessType.equals( "PROJECTTRANS" ) )
			{
				locationQry = "SELECT PROJECTID FROM FS_FR_PROJECTMASTER WHERE PROJECTID LIKE '"+locationFilter+"%' ORDER BY PROJECTID";
			}
			else if( accessType.equals( "WAREHOUSE" ) )
			{
				locationQry = "SELECT WHID FROM FS_LG_WHMASTER WHERE WHID LIKE '"+locationFilter+"%' ORDER BY WHID";
			}
			else if( accessType.equals( "CUSTWH" ) )
			{
				locationQry = "SELECT CUSTWHID FROM FS_LG_CUSTWHMASTER WHERE CUSTWHID LIKE '"+locationFilter+"%' ORDER BY CUSTWHID";
			}
			else if( accessType.equals( "HO" ) )
			{
				locationQry = "SELECT HOID FROM FS_LG_HOMASTER WHERE HOID LIKE '"+locationFilter+"%' ORDER BY HOID";
			}
			
		}
		else if( userType.equals( "ADMN_TERMINAL" ) )
		{
			if( accessType.equals( "ADMN_TERMINAL" ) )
			{	
				locationQry = "SELECT TERMINALID FROM FS_FR_TERMINALMASTER "
						//+"WHERE OPER_ADMIN_FLAG = 'A'  AND TERMINALID LIKE '"+locationFilter+"%'";
            +"WHERE OPER_ADMIN_FLAG = 'A' AND INVALIDATE='F' AND TERMINALID LIKE '"+locationFilter+"%'";
			}
			else if( accessType.equals( "OPER_TERMINAL" ) )
			{
			//@@ Commented by subrahmanyam for Enhancement 154384 on 10/04/09
      	/*locationQry = "SELECT TERMINALID FROM FS_FR_TERMINALMASTER "
						//+"WHERE OPER_ADMIN_FLAG='O' AND TERMINALTYPE='S' AND TERMINALID LIKE '"+locationFilter+"%' "
            +"WHERE OPER_ADMIN_FLAG='O' AND INVALIDATE='F' AND TERMINALTYPE='S' AND TERMINALID LIKE '"+locationFilter+"%' "
						+"UNION "
						+"SELECT DISTINCT COMPANYID FROM FS_AC_COMPANYINFO "
						+"WHERE COMPANYID LIKE '"+locationFilter+"%' AND MODULE='EACT'";*/
    //@@Added by subrahmanyam for Enhancement 154384  on 10/04/09
       locationQry = "SELECT TERMINALID FROM FS_FR_TERMINALMASTER "
						//+"WHERE OPER_ADMIN_FLAG='O' AND TERMINALTYPE='S' AND TERMINALID LIKE '"+locationFilter+"%' "
            +"WHERE OPER_ADMIN_FLAG='O' AND INVALIDATE='F' AND TERMINALTYPE='S' AND TERMINALID IN (SELECT CHILD_TERMINAL_ID FROM FS_FR_TERMINAL_REGN "
            +" WHERE PARENT_TERMINAL_ID= '"+locationId+"' AND CHILD_TERMINAL_ID LIKE '"+locationFilter+"%' ) " 
						+"UNION "
						+"SELECT DISTINCT COMPANYID FROM FS_AC_COMPANYINFO "
						+"WHERE COMPANYID LIKE '"+locationFilter+"%' AND MODULE='EACT'";     
			}
			else if( accessType.equals( "AGENT_TERMINAL" ) )
			{
				locationQry = "SELECT TERMINALID FROM FS_FR_TERMINALMASTER "
						+"WHERE TERMINALTYPE='N' AND TERMINALID LIKE '"+locationFilter+"%' ";
			}
			else if( accessType.equals( "GATEWAY" ) )
			{
				locationQry	= "SELECT GATEWAYID FROM FS_FR_GATEWAYMASTER "
						+"WHERE GATEWAYID LIKE '"+locationFilter+"%' AND LENGTH(GATEWAYID) = 4";
			}				
			else if( accessType.equals( "ETCRM" ) )
			{
				locationQry = "SELECT CUSTOMERID FROM FS_FR_CUSTOMERMASTER WHERE TERMINALID ='" + locationId + "' AND REGISTERED='R' AND CUSTOMERID LIKE '"+locationFilter+"%' ORDER BY CUSTOMERID";
			}
			else if( accessType.equals( "ESCRM" ) )
			{
				locationQry = "SELECT CUSTOMERID FROM FS_FR_CUSTOMERMASTER WHERE TERMINALID ='" + locationId + "' AND REGISTERED='R' AND CUSTOMERID LIKE '"+locationFilter+"%' ORDER BY CUSTOMERID";
			}
			else if( accessType.equals( "WAREHOUSE" ) )
			{
				locationQry = "SELECT WHID FROM FS_LG_WHMASTER WHERE WHID LIKE '"+locationFilter+"%' ORDER BY WHID";
			}
			else if( accessType.equals( "CUSTWH" ) )
			{
				locationQry = "SELECT CUSTWHID FROM FS_LG_CUSTWHMASTER WHERE CUSTWHID LIKE '"+locationFilter+"%' ORDER BY CUSTWHID";
			}
		}
		else if( userType.equals( "AGENT_TERMINAL" ) )
		{
				locationQry = "SELECT TERMINALID FROM FS_FR_TERMINALMASTER "
						+"WHERE TERMINALTYPE='N' AND TERMINALID LIKE '"+locationFilter+"%' ";
		}
		else if( userType.equals( "GATEWAY" ) )
		{
				locationQry	= "SELECT GATEWAYID FROM FS_FR_GATEWAYMASTER "
						+"WHERE GATEWAYID LIKE '"+locationFilter+"%' AND LENGTH(GATEWAYID) = 4";
		}				
		else if( userType.equals( "ETCRM" ) )
		{
				locationQry = "SELECT CUSTOMERID FROM FS_FR_CUSTOMERMASTER WHERE TERMINALID ='" + locationId + "' AND REGISTERED='R' AND CUSTOMERID LIKE '"+locationFilter+"%' ORDER BY CUSTOMERID";
		}
		else if( userType.equals( "HO" ) )
		{
			if( accessType.equals( "HO" ) )
			{
				locationQry = "SELECT DISTINCT HOID FROM FS_LG_HOMASTER WHERE HOID LIKE '"+locationFilter+"%' ORDER BY HOID";
			}
			if( accessType.equals( "COMPANY" ) )
			{
				locationQry = "SELECT DISTINCT COMPANYID FROM FS_LG_COMPANYMASTER WHERE COMPANYID LIKE '"+locationFilter+"%' ORDER BY COMPANYID";
			}
			if( accessType.equals( "PROJECTLOG" ) )
			{
				locationQry = "SELECT DISTINCT PROJECTID FROM FS_LG_PROJECTMASTER WHERE PROJECTID LIKE '"+locationFilter+"%' ORDER BY PROJECTID";
			}
			else if( accessType.equals( "WAREHOUSE" ) )
			{
				locationQry = "SELECT DISTINCT WHID FROM FS_LG_WHMASTER WHERE WHID LIKE '"+locationFilter+"%' ORDER BY WHID";
			}
			else if( accessType.equals( "CUSTWH" ) )
			{
				locationQry = "SELECT DISTINCT CUSTWHID FROM FS_LG_CUSTWHMASTER WHERE CUSTWHID LIKE '"+locationFilter+"%' ORDER BY CUSTWHID";
			}
		}
		else if( userType.equals( "COMPANY" ) )
		{
			
			if(FoursoftConfig.MODULE.equals("ELOG") || FoursoftConfig.MODULE.equals("SP")) 
			{

				if( accessType.equals( "PROJECTLOG" ) )
				{
					locationQry = "SELECT PROJECTID FROM FS_LG_PROJECTMASTER WHERE COMPANYID='" + locationId + "' AND PROJECTID LIKE '"+locationFilter+"%' ORDER BY PROJECTID";
				}
				else if( accessType.equals( "WAREHOUSE" ) )
				{
					locationQry = "SELECT WHID FROM FS_LG_WHMASTER WHERE COMPANYID='" + locationId + "' AND WHID LIKE '"+locationFilter+"%' ORDER BY WHID";
				}
				else if( accessType.equals( "CUSTWH" ) )
				{
					locationQry =
									//"SELECT CUSTWHID FROM FS_LG_CUSTWHMASTER WHERE WHID='" + locationId + "' AND "+
									"SELECT CUSTWHID FROM FS_LG_CUSTWHMASTER WHERE WHID IN (SELECT WHID FROM FS_LG_WHMASTER WHERE COMPANYID='" + locationId + "') AND "+
									"CUSTWHID LIKE '"+locationFilter+"%' ORDER BY CUSTWHID";
				}
			}
			if(FoursoftConfig.MODULE.equals("EP")) 
			{
				if( accessType.equals("WAREHOUSE"))
				{
					locationQry = "SELECT WHID FROM FS_LG_WHMASTER WHERE COMPANYID='" + locationId + "' AND WHID LIKE '"+locationFilter+"%' ORDER BY WHID";
				}
				else if( accessType.equals( "EPCRM" ) )
				{
					locationQry = "SELECT PARTYID FROM FS_LG_PARTYMASTER WHERE COMPANYID ='" + locationId + "' AND PARTYID LIKE '"+locationFilter+"%' AND PARTYFLAG='CUSTOMER' ORDER BY PARTYID";
				}
				else if( accessType.equals( "EPVRM" ) )
				{
					locationQry = "SELECT PARTYID FROM FS_LG_PARTYMASTER WHERE COMPANYID ='" + locationId + "' AND PARTYID LIKE '"+locationFilter+"%' AND PARTYFLAG='VENDOR' ORDER BY PARTYID";
				}
				else if( accessType.equals( "EPTRM" ) )
				{
					locationQry = "SELECT TERMINALID FROM FS_FR_TERMINALMASTER WHERE COMPANYID ='"+ locationId + "' AND TERMINALID LIKE '"+locationFilter+"%' ORDER BY TERMINALID";
				}
			}
			if( accessType.equals( "COMPANY" ) )
			{
				locationQry = "SELECT DISTINCT COMPANYID FROM FS_LG_COMPANYMASTER WHERE COMPANYID LIKE '"+locationFilter+"%' ORDER BY COMPANYID";
			}
			
		}
		else if( userType.equals( "EPCRM" ) )
		{
					locationQry = "SELECT PARTYID FROM FS_LG_PARTYMASTER WHERE COMPANYID ='" + locationId + "' AND PARTYID LIKE '"+locationFilter+"%' AND PARTYFLAG='CUSTOMER' ORDER BY PARTYID";
		}
		else if( userType.equals( "EPVRM" ) )
		{
					locationQry = "SELECT PARTYID FROM FS_LG_PARTYMASTER WHERE COMPANYID ='" + locationId + "' AND PARTYID LIKE '"+locationFilter+"%' AND PARTYFLAG='VENDOR' ORDER BY PARTYID";
		}
		else if( userType.equals( "EPTRM" ) )
		{
					locationQry = "SELECT TERMINALID FROM FS_FR_TERMINALMASTER WHERE COMPANYID ='"+ locationId + "' AND TERMINALID LIKE '"+locationFilter+"%' ORDER BY TERMINALID";
		}
		else if( userType.equals( "PROJECTLOG" ) )
		{
			if( accessType.equals( "PROJECTLOG" ) )
			{
					locationQry = "SELECT PROJECTID FROM FS_LG_PROJECTMASTER WHERE COMPANYID='" + locationId + "' AND PROJECTID LIKE '"+locationFilter+"%' ORDER BY PROJECTID";
			}
			if( accessType.equals( "CUSTWH" ) )
			{
				locationQry = "SELECT CUSTWHID FROM FS_LG_CUSTWHMASTER WHERE PROJECTID='" + locationId + "' AND CUSTWHID LIKE '"+locationFilter+"%' ORDER BY CUSTWHID";
			}
		}
		else if( userType.equals( "WAREHOUSE" ) )
		{
			if(FoursoftConfig.MODULE.equals("ELOG") || FoursoftConfig.MODULE.equals("SP")) 
			{
				if( accessType.equals( "CUSTWH" ) )
				{
					locationQry = "SELECT CUSTWHID FROM FS_LG_CUSTWHMASTER WHERE WHID='" + locationId + "' AND CUSTWHID LIKE '"+locationFilter+"%' ORDER BY CUSTWHID";
				}
				else if( accessType.equals( "ELTRM" ))
				{
					locationQry  = "SELECT TERMINALID FROM FS_FR_TERMINALMASTER WHERE  TERMINALID LIKE '"+locationFilter+"%' ORDER BY TERMINALID";
				}
			}
			if(FoursoftConfig.MODULE.equals("EP")) 
			{
				if( accessType.equals( "EPCRM" ) )
				{
					locationQry = "SELECT PARTYID FROM FS_LG_PARTYMASTERDTL WHERE WHID ='" + locationId + "' AND PARTYID LIKE '"+locationFilter+"%' AND PARTYFLAG='CUSTOMER' ORDER BY PARTYID";
				}
				else if( accessType.equals( "EPVRM" ) )
				{
					locationQry = "SELECT PARTYID FROM FS_LG_PARTYMASTERDTL WHERE WHID ='" + locationId + "' AND PARTYID LIKE '"+locationFilter+"%' AND PARTYFLAG='VENDOR' ORDER BY PARTYID";
				}
				
			}
			
			if( accessType.equals("WAREHOUSE"))
			{
					locationQry = "SELECT WHID FROM FS_LG_WHMASTER WHERE COMPANYID='" + locationId + "' AND WHID LIKE '"+locationFilter+"%' ORDER BY WHID";
			}
				
		}
		else if( userType.equals( "PROJECTTRANS" ) )
		{
			locationQry = "SELECT PROJECTID FROM FS_FR_PROJECTMASTER WHERE PROJECTID LIKE '"+locationFilter+"%' ORDER BY PROJECTID";
		}
		else if( userType.equals( "CUSTWH" ) )
		{
			if( accessType.equals( "CUSTWH" ) )
			{
					locationQry = "SELECT CUSTWHID FROM FS_LG_CUSTWHMASTER WHERE WHID='" + locationId + "' AND CUSTWHID LIKE '"+locationFilter+"%' ORDER BY CUSTWHID";
			}
			else if( accessType.equals( "ELCRM" ) )
			{
				locationQry = "SELECT PARTYID FROM FS_LG_PARTYMASTERDTL WHERE CUSTWHID ='" + locationId + "' AND PARTYID LIKE '"+locationFilter+"%' AND PARTYFLAG='CUSTOMER' ORDER BY PARTYID";
			}
			else if( accessType.equals( "ELVRM" ) )
			{
				locationQry = "SELECT PARTYID FROM FS_LG_PARTYMASTERDTL WHERE CUSTWHID ='" + locationId + "' AND PARTYID LIKE '"+locationFilter+"%' AND PARTYFLAG='VENDOR' ORDER BY PARTYID";
			}
			else if( accessType.equals( "ESCRM" ) )
			{
				locationQry = "SELECT PARTYID FROM FS_LG_PARTYMASTERDTL WHERE CUSTWHID ='" + locationId + "' AND PARTYID LIKE '"+locationFilter+"%' AND PARTYFLAG='CUSTOMER' ORDER BY PARTYID";
			}
			else if( accessType.equals( "ESVRM" ) )
			{
				locationQry = "SELECT PARTYID FROM FS_LG_PARTYMASTERDTL WHERE CUSTWHID ='" + locationId + "' AND PARTYID LIKE '"+locationFilter+"%' AND PARTYFLAG='VENDOR' ORDER BY PARTYID";
			}
		}
		else if( userType.equals( "ELCRM" ) )
		{
				locationQry = "SELECT PARTYID FROM FS_LG_PARTYMASTERDTL WHERE CUSTWHID ='" + locationId + "' AND PARTYID LIKE '"+locationFilter+"%' AND PARTYFLAG='CUSTOMER' ORDER BY PARTYID";
		}
		else if( userType.equals( "ELVRM" ) )
		{
				locationQry = "SELECT PARTYID FROM FS_LG_PARTYMASTERDTL WHERE CUSTWHID ='" + locationId + "' AND PARTYID LIKE '"+locationFilter+"%' AND PARTYFLAG='VENDOR' ORDER BY PARTYID";
		}
		else if( userType.equals( "OPER_TERMINAL" ))
		{
			if( accessType.equals( "ETCRM" ) )
			{
				locationQry = "SELECT CUSTOMERID FROM FS_FR_CUSTOMERMASTER WHERE TERMINALID ='" + locationId + "' AND REGISTERED='R' AND CUSTOMERID LIKE '"+locationFilter+"%' ORDER BY CUSTOMERID";
			}
			else if( accessType.equals( "ESCRM" ) )
			{
				locationQry = "SELECT CUSTOMERID FROM FS_FR_CUSTOMERMASTER WHERE TERMINALID ='" + locationId + "' AND REGISTERED='R' AND CUSTOMERID LIKE '"+locationFilter+"%' ORDER BY CUSTOMERID";
			}
			else if( accessType.equals( "OPER_TERMINAL" ) )
			{
				locationQry = "SELECT TERMINALID FROM FS_FR_TERMINALMASTER "
						+"WHERE OPER_ADMIN_FLAG='O' AND TERMINALTYPE='S' AND TERMINALID LIKE '"+locationFilter+"%' "
						+"UNION "
						+"SELECT DISTINCT COMPANYID FROM FS_AC_COMPANYINFO "
						+"WHERE COMPANYID LIKE '"+locationFilter+"%' AND MODULE='EACT'";
			}
		}
		else if( userType.equals( "ESCRM" ) )
		{
			locationQry = "SELECT PARTYID FROM FS_LG_PARTYMASTERDTL WHERE CUSTWHID ='" + locationId + "' AND PARTYID LIKE '"+locationFilter+"%' AND PARTYFLAG='CUSTOMER' ORDER BY PARTYID";
		}
		else if( userType.equals( "ESVRM" ) )
		{
			locationQry = "SELECT PARTYID FROM FS_LG_PARTYMASTERDTL WHERE CUSTWHID ='" + locationId + "' AND PARTYID LIKE '"+locationFilter+"%' AND PARTYFLAG='VENDOR' ORDER BY PARTYID";
		}

		//Logger.info(fileName,"getLocationIds( String locationId, String userType, String accessType, String locationFilter)","Locations Query : " + locationQry );
    logger.info(fileName+" getLocationIds( String locationId, String userType, String accessType, String locationFilter)"+"Locations Query : " + locationQry );

		Statement	stmt	= null;
		ResultSet	rs		= null;
		try
		{
			getConnection();
			stmt		= connection.createStatement();
            rs = stmt.executeQuery(locationQry);
            while(rs.next())
            {
 	            locationList.add(rs.getString(1));
            }
			if(rs!=null)			
				rs.close();
		}
		catch( Exception ex )
		{
			//Logger.error(fileName, "getLocationIds( String locationId, String userType, String accessType, String locationFilter)", "Problem Encountered in Retrieving the Location Ids : ",ex);
      logger.error(fileName+ " getLocationIds( String locationId, String userType, String accessType, String locationFilter)"+ " Problem Encountered in Retrieving the Location Ids : "+ex);
		}
		finally
		{
			ConnectionUtil.closeConnection(connection, stmt);
		}
		return locationList;
	}

	/**
	* This is used to retrieve the RoleId (locationId) which are belongs to a specific location, module and accessType
	*
	* @param roleId
	* @param roleLocationId
	* @param locationId
	* @param accessType
	* @param usersLocation
	* @param usersRoleAccessType
	*
	* @return ArrayList -
	*/
	public ArrayList getRoleIdLocation(String roleId, String roleLocationId, String locationId, String accessType, String usersLocation, String usersRoleAccessType, String roleModule)
	{
		String ROLEIDS = "";
		
		ArrayList	roleList	= new ArrayList();
		//String roleModule	= null;
		Statement	stmt	= null;
		ResultSet	rs		= null;
		
		try
		{
			getConnection();

			/*	UNCOMMENT this to enable Role fetching by Role Module
			 * 	Currently this has been disabled.
			 */
			//roleModule	= getRoleModule(roleLocationId, roleId);
			
			if(roleModule != null)
			{
				ROLEIDS = "SELECT ROLEID, LOCATIONID, DESCRIPTION FROM FS_ROLEMASTER "
					+"WHERE ROLELEVEL = '"+ accessType +"' AND LOCATIONID = '"+locationId+"' AND ROLEMODULE = '"+roleModule+"'"
					+"UNION "
					+"SELECT ROLEID, LOCATIONID, DESCRIPTION FROM FS_ROLEMASTER "
					+"WHERE ROLELEVEL = '"+ accessType +"' AND ROLETYPE= 'GENERIC' AND ROLEMODULE = '"+roleModule+"'";				
			}
			else
			{
				ROLEIDS = "SELECT ROLEID, LOCATIONID, DESCRIPTION FROM FS_ROLEMASTER "
					+"WHERE ROLELEVEL = '"+ accessType +"' AND LOCATIONID = '"+locationId+"' "
					+"UNION "
					+"SELECT ROLEID, LOCATIONID, DESCRIPTION FROM FS_ROLEMASTER "
					+"WHERE ROLELEVEL = '"+ accessType +"' AND ROLETYPE= 'GENERIC'";
			}
			

			//Logger.info(fileName, "getRoleIdLocation(String locationId, String accessType, String roleModule, String usersLocation, String usersRoleAccessType)"," SQL String ::  : "+ROLEIDS);
      logger.info(fileName+ " getRoleIdLocation(String locationId, String accessType, String roleModule, String usersLocation, String usersRoleAccessType)"+" SQL String ::  : "+ROLEIDS);
			
			stmt		= connection.createStatement();
			rs = stmt.executeQuery(ROLEIDS);
			while(rs.next())
			{
				roleId			= rs.getString(1);
				roleLocationId	= rs.getString(2);
				
				RoleModel	roleModel	=	new RoleModel(roleId, roleLocationId);

				String	description	= rs.getString(3);

				if(description==null)
					description = "";
				
				roleModel.setDescription( description );
				
				roleList.add( roleModel );
			}
			if(rs!=null)
				rs.close();
		}
		catch( Exception ex )
		{
			//Logger.error(fileName, "getLocationIds( String locationId, String locationType, String accessType, String locationFilter)", "Problem Encountered in Retrieving the Location Ids : ",ex);
      logger.error(fileName+ " getLocationIds( String locationId, String locationType, String accessType, String locationFilter)"+ "Problem Encountered in Retrieving the Location Ids : "+ex);
		}
		finally
		{
			ConnectionUtil.closeConnection(connection, stmt);
		}
		return roleList;
	} // end of getRoleIdLocation(String locationId, String accessType, String roleModule, String usersLocation, String usersRoleAccessType)


	/**
	* which is used to retrieve the roleModule 
	* @param locationId
	* @param roleId
	*
	* @return roleModule
	*/
	private String getRoleModule(String locationId, String roleId)
	{
		Statement	stmt	= null;
		ResultSet	rs		= null;
		String roleModule	= null;
		try
		{
			stmt		= connection.createStatement();
			rs = stmt.executeQuery("SELECT ROLEMODULE FROM FS_ROLEMASTER WHERE LOCATIONID = '"+ locationId +"' AND ROLEID='"+ roleId +"'" );
			while(rs.next())
			{
				roleModule		= rs.getString(1);
			}
			if(rs!=null)
				rs.close();
		}
		catch( Exception ex )
		{
			//Logger.error(fileName, "getRoleModule(String locationId, String roleId)", "Error in Retrieving the Role Module : ",ex);
      logger.error(fileName+ " getRoleModule(String locationId, String roleId)"+ " Error in Retrieving the Role Module : "+ex);
		}
		finally
		{
			ConnectionUtil.closeStatement(stmt);
		}
		return roleModule;

	}

	public ArrayList getLocationIds(String filter)
	{
		ArrayList locationList = new ArrayList();
		String locationQry = "SELECT DISTINCT(LOCATIONID) FROM FS_USERMASTER WHERE LOCATIONID LIKE '"+filter+"%' ";

		Statement	stmt	= null;
		ResultSet	rs		= null;
	
		try
		{
			getConnection();
			stmt		= connection.createStatement();
			rs = stmt.executeQuery(locationQry);
			while(rs.next())
			{
 				locationList.add(rs.getString(1));
			}
			if(rs!=null)
				rs.close();
		}
		catch( Exception ex )
		{
			//Logger.error(fileName, "getLocationIds( String locationId, String userType, String accessType, String locationFilter)", "Problem Encountered in Retrieving the Location Ids : ",ex);
      logger.error(fileName+ " getLocationIds( String locationId, String userType, String accessType, String locationFilter)"+ "Problem Encountered in Retrieving the Location Ids : "+ex);
		}
		finally
		{
			ConnectionUtil.closeConnection(connection, stmt);
		}
		return locationList;
	}



//added by purushothamanan to validate location id in user add screen

public boolean isExists(String locationId,String accessType)
	{
		boolean status = false;
		String locationQry = "SELECT LOCATIONID FROM FS_ROLEMASTER WHERE ROLELEVEL = '"+ accessType +"' AND LOCATIONID = '"+locationId+"'";

		Statement	stmt	= null;
		ResultSet	rs		= null;
	
		try
		{
			getConnection();
			stmt		= connection.createStatement();
			rs = stmt.executeQuery(locationQry);
			
			if(rs.next())
			status = true;

			if(rs!=null)
				rs.close();
		}
		catch( Exception ex )
		{
			//Logger.error(fileName, "isExists( String locationId, String accessType)", "Problem Encountered in checking the Location Ids : ",ex);
      logger.error(fileName+ " isExists( String locationId, String accessType)"+ "Problem Encountered in checking the Location Ids : "+ex);
		}
		finally
		{
			ConnectionUtil.closeConnection(connection, stmt);
		}
		return status;
	}


	//to validate in role add screen

	
/**
	* Which is used to validate the LocationIds
	* @param locationId
	* @param userType - access type of the user who is creating Role/User
	* @param accessType		- access type to which User/role creating
	* @exception	SQLException
	*
	* @retun boolean	- status of locationid
	*/
	public boolean IsLocIdExists( String loginlocationId, String userType, String accessType, String locationId)
	{
		String locationQry = "";
		String locationFilter = loginlocationId.toUpperCase();

		if( userType.equals( "LICENSEE" ) )
		{

			if( accessType.equals( "LICENSEE" ))
			{	
				locationQry = "SELECT LOCATIONID FROM FS_USERMASTER "
						+"WHERE USER_LEVEL = 'LICENSEE' AND LOCATIONID ='"+locationFilter+"' ";
			}

			if( accessType.equals( "HO_TERMINAL" ))
			{	
				locationQry = "SELECT TERMINALID FROM FS_FR_TERMINALMASTER "
						//+"WHERE OPER_ADMIN_FLAG = 'H' AND TERMINALID ='"+locationFilter+"' ";
            +"WHERE OPER_ADMIN_FLAG = 'H' AND INVALIDATE='F' AND TERMINALID ='"+locationFilter+"' ";
			}
			
			if( accessType.equals( "ADMN_TERMINAL" ) )
			{	
				locationQry = "SELECT TERMINALID FROM FS_FR_TERMINALMASTER "
						//+"WHERE OPER_ADMIN_FLAG = 'A' AND TERMINALID ='"+locationFilter+"' ";
            +"WHERE OPER_ADMIN_FLAG = 'A' AND INVALIDATE='F' AND TERMINALID ='"+locationFilter+"' ";
			}
			
			// To be activated for eLog when it is using HO. Comfirm the clumn names and table name for "HO" functionality
			else if( accessType.equals( "HO" ) )
			{
				locationQry = "SELECT HOID FROM FS_LG_HOMASTER WHERE HOID ='"+locationFilter+"' ORDER BY HOID";
			}

			else if( accessType.equals( "COMPANY" ) )
			{
				locationQry = "SELECT COMPANYID FROM FS_LG_COMPANYMASTER WHERE COMPANYID ='"+locationFilter+"' ORDER BY COMPANYID";
			}
			
		}
		else if( userType.equals( "HO_TERMINAL" ) )
		{
			if( accessType.equals( "HO_TERMINAL" ) )
			{	
				locationQry = "SELECT TERMINALID FROM FS_FR_TERMINALMASTER "
						//+"WHERE OPER_ADMIN_FLAG = 'H'  AND TERMINALID ='"+locationFilter+"' ";
            +"WHERE OPER_ADMIN_FLAG = 'H' AND INVALIDATE='F' AND TERMINALID ='"+locationFilter+"' ";
			}
			else if( accessType.equals( "ADMN_TERMINAL" ) )
			{	
				locationQry = "SELECT TERMINALID FROM FS_FR_TERMINALMASTER "
						//+"WHERE OPER_ADMIN_FLAG = 'A' AND TERMINALID ='"+locationFilter+"' ";
            +"WHERE OPER_ADMIN_FLAG = 'A' AND INVALIDATE='F' AND TERMINALID ='"+locationFilter+"' ";
			}
			else if( accessType.equals( "OPER_TERMINAL" ) )
			{
				locationQry = "SELECT TERMINALID FROM FS_FR_TERMINALMASTER "
						//+"WHERE OPER_ADMIN_FLAG='O'  AND TERMINALTYPE='S' AND TERMINALID ='"+locationFilter+"' "
            +"WHERE OPER_ADMIN_FLAG='O' AND INVALIDATE='F' AND TERMINALTYPE='S' AND TERMINALID ='"+locationFilter+"' "
						+"UNION "
						+"SELECT DISTINCT COMPANYID FROM FS_AC_COMPANYINFO "
						+"WHERE COMPANYID ='"+locationFilter+"' AND MODULE='EACT'";
			}
			else if( accessType.equals( "AGENT_TERMINAL" ) )
			{
				locationQry = "SELECT TERMINALID FROM FS_FR_TERMINALMASTER "
						+"WHERE TERMINALTYPE='N' AND TERMINALID ='"+locationFilter+"' ";
			}
			else if( accessType.equals( "GATEWAY" ) )
			{
				locationQry	= "SELECT GATEWAYID FROM FS_FR_GATEWAYMASTER "
						+"WHERE GATEWAYID ='"+locationFilter+"' AND LENGTH(GATEWAYID) = 4";
			}				
			else if( accessType.equals( "ETCRM" ) )
			{
				locationQry = "SELECT CUSTOMERID FROM FS_FR_CUSTOMERMASTER WHERE TERMINALID ='" + locationId + "' AND REGISTERED='R' AND CUSTOMERID ='"+locationFilter+"' ORDER BY CUSTOMERID";
			}
			
			
			else if( accessType.equals( "ESCRM" ) )
			{
				locationQry = "SELECT CUSTOMERID FROM FS_FR_CUSTOMERMASTER WHERE TERMINALID ='" + locationId + "' AND REGISTERED='R' AND CUSTOMERID ='"+locationFilter+"' ORDER BY CUSTOMERID";
			}
			else if( accessType.equals( "COMPANY" ) )
			{
				locationQry = "SELECT COMPANYID FROM FS_LG_COMPANYMASTER WHERE COMPANYID ='"+locationFilter+"' ORDER BY COMPANYID";
			}
			else if( accessType.equals( "PROJECTLOG" ) )
			{
				locationQry = "SELECT PROJECTID FROM FS_LG_PROJECTMASTER WHERE PROJECTID ='"+locationFilter+"' ORDER BY PROJECTID";
			}
			else if( accessType.equals( "PROJECTTRANS" ) )
			{
				locationQry = "SELECT PROJECTID FROM FS_FR_PROJECTMASTER WHERE PROJECTID ='"+locationFilter+"' ORDER BY PROJECTID";
			}
			else if( accessType.equals( "WAREHOUSE" ) )
			{
				locationQry = "SELECT WHID FROM FS_LG_WHMASTER WHERE WHID ='"+locationFilter+"' ORDER BY WHID";
			}
			else if( accessType.equals( "CUSTWH" ) )
			{
				locationQry = "SELECT CUSTWHID FROM FS_LG_CUSTWHMASTER WHERE CUSTWHID ='"+locationFilter+"' ORDER BY CUSTWHID";
			}
			else if( accessType.equals( "HO" ) )
			{
				locationQry = "SELECT HOID FROM FS_LG_HOMASTER WHERE HOID ='"+locationFilter+"' ORDER BY HOID";
			}
			
		}
		else if( userType.equals( "ADMN_TERMINAL" ) )
		{
			if( accessType.equals( "ADMN_TERMINAL" ) )
			{	
				locationQry = "SELECT TERMINALID FROM FS_FR_TERMINALMASTER "
						//+"WHERE OPER_ADMIN_FLAG = 'A' AND TERMINALID ='"+locationFilter+"'";
            +"WHERE OPER_ADMIN_FLAG = 'A' AND INVALIDATE='F' AND TERMINALID ='"+locationFilter+"'";
			}
			else if( accessType.equals( "OPER_TERMINAL" ) )
			{
				locationQry = "SELECT TERMINALID FROM FS_FR_TERMINALMASTER "
						//+"WHERE OPER_ADMIN_FLAG='O' AND TERMINALTYPE='S' AND TERMINALID ='"+locationFilter+"' "
            +"WHERE OPER_ADMIN_FLAG='O' AND INVALIDATE='F' AND TERMINALTYPE='S' AND TERMINALID ='"+locationFilter+"' "
						+"UNION "
						+"SELECT DISTINCT COMPANYID FROM FS_AC_COMPANYINFO "
						+"WHERE COMPANYID ='"+locationFilter+"' AND MODULE='EACT'";
			}
			else if( accessType.equals( "AGENT_TERMINAL" ) )
			{
				locationQry = "SELECT TERMINALID FROM FS_FR_TERMINALMASTER "
						+"WHERE TERMINALTYPE='N' AND TERMINALID ='"+locationFilter+"' ";
			}
			else if( accessType.equals( "GATEWAY" ) )
			{
				locationQry	= "SELECT GATEWAYID FROM FS_FR_GATEWAYMASTER "
						+"WHERE GATEWAYID ='"+locationFilter+"' AND LENGTH(GATEWAYID) = 4";
			}				
			else if( accessType.equals( "ETCRM" ) )
			{
				locationQry = "SELECT CUSTOMERID FROM FS_FR_CUSTOMERMASTER WHERE TERMINALID ='" + locationId + "' AND REGISTERED='R' AND CUSTOMERID ='"+locationFilter+"' ORDER BY CUSTOMERID";
			}
			else if( accessType.equals( "ESCRM" ) )
			{
				locationQry = "SELECT CUSTOMERID FROM FS_FR_CUSTOMERMASTER WHERE TERMINALID ='" + locationId + "' AND REGISTERED='R' AND CUSTOMERID ='"+locationFilter+"' ORDER BY CUSTOMERID";
			}
			else if( accessType.equals( "WAREHOUSE" ) )
			{
				locationQry = "SELECT WHID FROM FS_LG_WHMASTER WHERE WHID ='"+locationFilter+"' ORDER BY WHID";
			}
			else if( accessType.equals( "CUSTWH" ) )
			{
				locationQry = "SELECT CUSTWHID FROM FS_LG_CUSTWHMASTER WHERE CUSTWHID ='"+locationFilter+"' ORDER BY CUSTWHID";
			}
		}
		else if( userType.equals( "AGENT_TERMINAL" ) )
		{
				locationQry = "SELECT TERMINALID FROM FS_FR_TERMINALMASTER "
						+"WHERE TERMINALTYPE='N' AND TERMINALID ='"+locationFilter+"' ";
		}
		else if( userType.equals( "GATEWAY" ) )
		{
				locationQry	= "SELECT GATEWAYID FROM FS_FR_GATEWAYMASTER "
						+"WHERE GATEWAYID ='"+locationFilter+"' AND LENGTH(GATEWAYID) = 4";
		}				
		else if( userType.equals( "ETCRM" ) )
		{
				locationQry = "SELECT CUSTOMERID FROM FS_FR_CUSTOMERMASTER WHERE TERMINALID ='" + locationId + "' AND REGISTERED='R' AND CUSTOMERID ='"+locationFilter+"' ORDER BY CUSTOMERID";
		}
		else if( userType.equals( "HO" ) )
		{
			if( accessType.equals( "HO" ) )
			{
				locationQry = "SELECT DISTINCT HOID FROM FS_LG_HOMASTER WHERE HOID ='"+locationFilter+"' ORDER BY HOID";
			}
			if( accessType.equals( "COMPANY" ) )
			{
				locationQry = "SELECT DISTINCT COMPANYID FROM FS_LG_COMPANYMASTER WHERE COMPANYID ='"+locationFilter+"' ORDER BY COMPANYID";
			}
			if( accessType.equals( "PROJECTLOG" ) )
			{
				locationQry = "SELECT DISTINCT PROJECTID FROM FS_LG_PROJECTMASTER WHERE PROJECTID ='"+locationFilter+"' ORDER BY PROJECTID";
			}
			else if( accessType.equals( "WAREHOUSE" ) )
			{
				locationQry = "SELECT DISTINCT WHID FROM FS_LG_WHMASTER WHERE WHID ='"+locationFilter+"' ORDER BY WHID";
			}
			else if( accessType.equals( "CUSTWH" ) )
			{
				locationQry = "SELECT DISTINCT CUSTWHID FROM FS_LG_CUSTWHMASTER WHERE CUSTWHID ='"+locationFilter+"' ORDER BY CUSTWHID";
			}
		}
		else if( userType.equals( "COMPANY" ) )
		{
			
			if(FoursoftConfig.MODULE.equals("ELOG") || FoursoftConfig.MODULE.equals("SP")) 
			{

				if( accessType.equals( "PROJECTLOG" ) )
				{
					locationQry = "SELECT PROJECTID FROM FS_LG_PROJECTMASTER WHERE COMPANYID='" + locationId + "' AND PROJECTID ='"+locationFilter+"' ORDER BY PROJECTID";
				}
				else if( accessType.equals( "WAREHOUSE" ) )
				{
					locationQry = "SELECT WHID FROM FS_LG_WHMASTER WHERE COMPANYID='" + locationId + "' AND WHID ='"+locationFilter+"' ORDER BY WHID";
				}
				else if( accessType.equals( "CUSTWH" ) )
				{
					locationQry =
									//"SELECT CUSTWHID FROM FS_LG_CUSTWHMASTER WHERE WHID='" + locationId + "' AND "+
									"SELECT CUSTWHID FROM FS_LG_CUSTWHMASTER WHERE WHID IN (SELECT WHID FROM FS_LG_WHMASTER WHERE COMPANYID='" + locationId + "') AND "+
									"CUSTWHID ='"+locationFilter+"' ORDER BY CUSTWHID";
				}
			}
			if(FoursoftConfig.MODULE.equals("EP")) 
			{
				if( accessType.equals("WAREHOUSE"))
				{
					locationQry = "SELECT WHID FROM FS_LG_WHMASTER WHERE COMPANYID='" + locationId + "' AND WHID ='"+locationFilter+"' ORDER BY WHID";
				}
				else if( accessType.equals( "EPCRM" ) )
				{
					locationQry = "SELECT PARTYID FROM FS_LG_PARTYMASTER WHERE COMPANYID ='" + locationId + "' AND PARTYID ='"+locationFilter+"' AND PARTYFLAG='CUSTOMER' ORDER BY PARTYID";
				}
				else if( accessType.equals( "EPVRM" ) )
				{
					locationQry = "SELECT PARTYID FROM FS_LG_PARTYMASTER WHERE COMPANYID ='" + locationId + "' AND PARTYID ='"+locationFilter+"' AND PARTYFLAG='VENDOR' ORDER BY PARTYID";
				}
				else if( accessType.equals( "EPTRM" ) )
				{
					locationQry = "SELECT TERMINALID FROM FS_FR_TERMINALMASTER WHERE COMPANYID ='"+ locationId + "' AND TERMINALID ='"+locationFilter+"' ORDER BY TERMINALID";
				}
			}
			if( accessType.equals( "COMPANY" ) )
			{
				locationQry = "SELECT DISTINCT COMPANYID FROM FS_LG_COMPANYMASTER WHERE COMPANYID ='"+locationFilter+"' ORDER BY COMPANYID";
			}
			
		}
		else if( userType.equals( "EPCRM" ) )
		{
					locationQry = "SELECT PARTYID FROM FS_LG_PARTYMASTER WHERE COMPANYID ='" + locationId + "' AND PARTYID ='"+locationFilter+"' AND PARTYFLAG='CUSTOMER' ORDER BY PARTYID";
		}
		else if( userType.equals( "EPVRM" ) )
		{
					locationQry = "SELECT PARTYID FROM FS_LG_PARTYMASTER WHERE COMPANYID ='" + locationId + "' AND PARTYID ='"+locationFilter+"' AND PARTYFLAG='VENDOR' ORDER BY PARTYID";
		}
		else if( userType.equals( "EPTRM" ) )
		{
					locationQry = "SELECT TERMINALID FROM FS_FR_TERMINALMASTER WHERE COMPANYID ='"+ locationId + "' AND TERMINALID ='"+locationFilter+"' ORDER BY TERMINALID";
		}
		else if( userType.equals( "PROJECTLOG" ) )
		{
			if( accessType.equals( "PROJECTLOG" ) )
			{
					locationQry = "SELECT PROJECTID FROM FS_LG_PROJECTMASTER WHERE COMPANYID='" + locationId + "' AND PROJECTID ='"+locationFilter+"' ORDER BY PROJECTID";
			}
			if( accessType.equals( "CUSTWH" ) )
			{
				locationQry = "SELECT CUSTWHID FROM FS_LG_CUSTWHMASTER WHERE PROJECTID='" + locationId + "' AND CUSTWHID ='"+locationFilter+"' ORDER BY CUSTWHID";
			}
		}
		else if( userType.equals( "WAREHOUSE" ) )
		{
			if(FoursoftConfig.MODULE.equals("ELOG") || FoursoftConfig.MODULE.equals("SP")) 
			{
				if( accessType.equals( "CUSTWH" ) )
				{
					locationQry = "SELECT CUSTWHID FROM FS_LG_CUSTWHMASTER WHERE WHID='" + locationId + "' AND CUSTWHID ='"+locationFilter+"' ORDER BY CUSTWHID";
				}
				else if( accessType.equals( "ELTRM" ))
				{
					locationQry  = "SELECT TERMINALID FROM FS_FR_TERMINALMASTER WHERE  TERMINALID ='"+locationFilter+"' ORDER BY TERMINALID";
				}
			}
			if(FoursoftConfig.MODULE.equals("EP")) 
			{
				if( accessType.equals( "EPCRM" ) )
				{
					locationQry = "SELECT PARTYID FROM FS_LG_PARTYMASTERDTL WHERE WHID ='" + locationId + "' AND PARTYID ='"+locationFilter+"' AND PARTYFLAG='CUSTOMER' ORDER BY PARTYID";
				}
				else if( accessType.equals( "EPVRM" ) )
				{
					locationQry = "SELECT PARTYID FROM FS_LG_PARTYMASTERDTL WHERE WHID ='" + locationId + "' AND PARTYID ='"+locationFilter+"' AND PARTYFLAG='VENDOR' ORDER BY PARTYID";
				}
				
			}
			
			if( accessType.equals("WAREHOUSE"))
			{
					locationQry = "SELECT DISTINCT WHID FROM FS_LG_WHMASTER WHERE WHID = '"+locationFilter+"'";
					
			}
				
		}
		else if( userType.equals( "PROJECTTRANS" ) )
		{
			locationQry = "SELECT PROJECTID FROM FS_FR_PROJECTMASTER WHERE PROJECTID ='"+locationFilter+"' ORDER BY PROJECTID";
		}
		else if( userType.equals( "CUSTWH" ) )
		{
			if( accessType.equals( "CUSTWH" ) )
			{
					locationQry = "SELECT CUSTWHID FROM FS_LG_CUSTWHMASTER WHERE WHID='" + locationId + "' AND CUSTWHID ='"+locationFilter+"' ORDER BY CUSTWHID";
			}
			else if( accessType.equals( "ELCRM" ) )
			{
				locationQry = "SELECT PARTYID FROM FS_LG_PARTYMASTERDTL WHERE CUSTWHID ='" + locationId + "' AND PARTYID ='"+locationFilter+"' AND PARTYFLAG='CUSTOMER' ORDER BY PARTYID";
			}
			else if( accessType.equals( "ELVRM" ) )
			{
				locationQry = "SELECT PARTYID FROM FS_LG_PARTYMASTERDTL WHERE CUSTWHID ='" + locationId + "' AND PARTYID ='"+locationFilter+"' AND PARTYFLAG='VENDOR' ORDER BY PARTYID";
			}
			else if( accessType.equals( "ESCRM" ) )
			{
				locationQry = "SELECT PARTYID FROM FS_LG_PARTYMASTERDTL WHERE CUSTWHID ='" + locationId + "' AND PARTYID ='"+locationFilter+"' AND PARTYFLAG='CUSTOMER' ORDER BY PARTYID";
			}
			else if( accessType.equals( "ESVRM" ) )
			{
				locationQry = "SELECT PARTYID FROM FS_LG_PARTYMASTERDTL WHERE CUSTWHID ='" + locationId + "' AND PARTYID ='"+locationFilter+"' AND PARTYFLAG='VENDOR' ORDER BY PARTYID";
			}
		}
		else if( userType.equals( "ELCRM" ) )
		{
				locationQry = "SELECT PARTYID FROM FS_LG_PARTYMASTERDTL WHERE CUSTWHID ='" + locationId + "' AND PARTYID ='"+locationFilter+"' AND PARTYFLAG='CUSTOMER' ORDER BY PARTYID";
		}
		else if( userType.equals( "ELVRM" ) )
		{
				locationQry = "SELECT PARTYID FROM FS_LG_PARTYMASTERDTL WHERE CUSTWHID ='" + locationId + "' AND PARTYID ='"+locationFilter+"' AND PARTYFLAG='VENDOR' ORDER BY PARTYID";
		}
		else if( userType.equals( "OPER_TERMINAL" ))
		{
			if( accessType.equals( "ETCRM" ) )
			{
				locationQry = "SELECT CUSTOMERID FROM FS_FR_CUSTOMERMASTER WHERE TERMINALID ='" + locationId + "' AND REGISTERED='R' AND CUSTOMERID ='"+locationFilter+"' ORDER BY CUSTOMERID";
			}
			else if( accessType.equals( "ESCRM" ) )
			{
				locationQry = "SELECT CUSTOMERID FROM FS_FR_CUSTOMERMASTER WHERE TERMINALID ='" + locationId + "' AND REGISTERED='R' AND CUSTOMERID ='"+locationFilter+"' ORDER BY CUSTOMERID";
			}
			else if( accessType.equals( "OPER_TERMINAL" ) )
			{
				locationQry = "SELECT TERMINALID FROM FS_FR_TERMINALMASTER "
						//+"WHERE OPER_ADMIN_FLAG='O' AND TERMINALTYPE='S' AND TERMINALID ='"+locationFilter+"' "
            +"WHERE OPER_ADMIN_FLAG='O' AND INVALIDATE='F' AND TERMINALTYPE='S' AND TERMINALID ='"+locationFilter+"' "
						+"UNION "
						+"SELECT DISTINCT COMPANYID FROM FS_AC_COMPANYINFO "
						+"WHERE COMPANYID ='"+locationFilter+"' AND MODULE='EACT'";
			}
		}
		else if( userType.equals( "ESCRM" ) )
		{
			locationQry = "SELECT PARTYID FROM FS_LG_PARTYMASTERDTL WHERE CUSTWHID ='" + locationId + "' AND PARTYID ='"+locationFilter+"' AND PARTYFLAG='CUSTOMER' ORDER BY PARTYID";
		}
		else if( userType.equals( "ESVRM" ) )
		{
			locationQry = "SELECT PARTYID FROM FS_LG_PARTYMASTERDTL WHERE CUSTWHID ='" + locationId + "' AND PARTYID ='"+locationFilter+"' AND PARTYFLAG='VENDOR' ORDER BY PARTYID";
		}
		
		//Logger.info(fileName,"getLocationIds( String locationId, String userType, String accessType, String locationFilter)","Locations Query : " + locationQry );
    logger.info(fileName+" getLocationIds( String locationId, String userType, String accessType, String locationFilter)"+" Locations Query : " + locationQry );

		Statement	stmt	= null;
		ResultSet	rs		= null;
		boolean status = false;
		try
		{
			getConnection();
			stmt		= connection.createStatement();
            rs = stmt.executeQuery(locationQry);
            if(rs.next())
            {
 	           status=true;
            }
			if(rs!=null)			
				rs.close();
		}
		catch( Exception ex )
		{
			//Logger.error(fileName, "getLocationIds( String locationId, String userType, String accessType, String locationFilter)", "Problem Encountered in Retrieving the Location Ids : ",ex);
      logger.error(fileName+ " getLocationIds( String locationId, String userType, String accessType, String locationFilter)"+ "Problem Encountered in Retrieving the Location Ids : "+ex);
		}
		finally
		{
			ConnectionUtil.closeConnection(connection, stmt);
		}
		return status;
	}

}


