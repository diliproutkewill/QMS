
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

import com.foursoft.esupply.accesscontrol.dao.UserRoleDAO;
import com.foursoft.esupply.accesscontrol.ejb.bmp.RoleMaster;
import com.foursoft.esupply.accesscontrol.ejb.bmp.RoleMasterBean;
import com.foursoft.esupply.accesscontrol.ejb.bmp.RoleMasterBeanPK;
import com.foursoft.esupply.accesscontrol.ejb.bmp.RoleMasterHome;
import com.foursoft.esupply.accesscontrol.ejb.bmp.UserMaster;
import com.foursoft.esupply.accesscontrol.ejb.bmp.UserMasterBean;
import com.foursoft.esupply.accesscontrol.ejb.bmp.UserMasterBeanPK;
import com.foursoft.esupply.accesscontrol.ejb.bmp.UserMasterHome;
import com.foursoft.esupply.accesscontrol.exception.AlreadyExistsException;
import com.foursoft.esupply.accesscontrol.exception.DataIntegrityViolationException;
import com.foursoft.esupply.accesscontrol.exception.NotExistsException;
import com.foursoft.esupply.accesscontrol.java.RoleModel;
import com.foursoft.esupply.accesscontrol.java.TxDetailVOB;
import com.foursoft.esupply.accesscontrol.java.UserModel;
import com.foursoft.esupply.accesscontrol.java.UserRoleDOB;
import com.foursoft.esupply.common.dao.UserLogDAO;
import com.foursoft.esupply.common.ejb.sls.Mailer;
import com.foursoft.esupply.common.ejb.sls.MailerHome;
import com.foursoft.esupply.common.exception.DBSysException;
import com.foursoft.esupply.common.exception.FoursoftException;
import com.foursoft.esupply.common.java.EMailMessage;
import com.foursoft.esupply.common.java.FoursoftConfig;
import com.foursoft.esupply.common.java.LookUpBean;
import com.foursoft.esupply.common.java.UserLogVOB;
import com.foursoft.esupply.common.util.ConnectionUtil;
//import com.foursoft.esupply.common.util.Logger;
import javax.servlet.http.HttpSessionContext;
import org.apache.log4j.Logger;


import java.io.UnsupportedEncodingException;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Random;

import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ejb.FinderException;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import javax.sql.DataSource;

import sun.misc.BASE64Encoder;
import com.foursoft.esupply.common.bean.ESupplyGlobalParameters;//SUBRAHMANYAM
import javax.servlet.http.HttpServletRequest;
//import com.foursoft.esupply.accesscontrol.ejb.bmp.UserMasterHome;
//import com.foursoft.esupply.accesscontrol.ejb.bmp.UserMaster;
//import com.foursoft.esupply.accesscontrol.ejb.bmp.RoleMasterHome;
//import com.foursoft.esupply.accesscontrol.ejb.bmp.RoleMaster


/**
 * File			: UserRoleRegistrationSessionBean.java
 * module 		: esupply
 * sub-module	: AccessControl
 * This used as the controller for UserMaster and RoleMaster
 * It manages the flow in registering User , Role and modifying the same
 *
 *
 * @author	Madhusudhan Rao. P,
 * @date	28-08-2001
 * 
 * modified history
 * Modifed By		Modified Date		Reason
 * Amit Parekh		16/10/2002			EJB Homes looked up only once in the setSessionContext
 * 										and reused again. Some queries were optimized to static strings
 * Amit Parekh		21/10/2002			The global datasource name 'jdbc/DB' or otherwise is now
 *										maintained globally in FourSoftConfig.java
 *                                      Also the Home lookup-once-and-cache pattern is implemented
 * Amit Parekh		12/11/2002			A new method 'getWarehousesAndRoles' was added for User Registration
 *										of a COMPANY User in EP (and WAREHOUSE User in ELOG).
 *                                      Its for fetching all warehouses ( or customer warehouses in ELOG) under
 *                                      the parent Company ( Warehouse in ELOG) and generic and specific roles
 *                                      associated with them
 * Amit Parekh		19/11/2002			Updated to handle optional roles for a COMPANY User for EP and
 * 										a WAREHOUSE User for ELOG. A new table FS_OPTIONALROLES was added
 *                                  	in the database for these changes. A User can now be regsitered with
 *                                      optional roles that are separate from his master role. This file also
 *                                      serves the info about child facilities and the generic/specific role
 *                                      for each of them.
 */

public class UserRoleRegistrationSessionBean implements SessionBean
{

	private SessionContext sessionContext = null;

	private	transient	Connection		connection;
    private	transient	DataSource		datasource;
	private transient	InitialContext	ic;
//	private transient	RoleMasterHome	rmHome;
//	private transient	UserMasterHome	umHome;
	private transient	RoleMasterHome	rmHome;
	private transient	UserMasterHome	umHome;
    private transient	MailerHome      mailHome;
	private transient	LookUpBean		lookUpBean;
	private transient	UserRoleDAO		userRoleDAO;
	
	private	static final String	FILE_NAME		= "UserRolesRegistrationSessionBean ";
	private	boolean			VERBOSE			= true;
  private static Logger logger = null;


	//Place your business methods here.
   /**
	* this retrieve the TransactionIds based on the module and accessType
	*
	* @param module
	* @param accessType
	*
	* @return ArrayList - return list of TransactionIds
	*/
	public	ArrayList getTransactionIds(String module, String accessType) throws FoursoftException
	{
        try
        {
            return getModuleWiseTransactionIds(module, accessType);
        }
        catch(FoursoftException exp)
        {
            //Logger.error(FILE_NAME,"FoursoftException ", exp);
            logger.error(FILE_NAME+" FoursoftException "+ exp);
            sessionContext.setRollbackOnly();
            throw exp;
        }
	} // end of getTransactionIds(String module, String accessType)
		
	public	ArrayList getTransactionIds(String module, int shipmentMode, String accessType) throws FoursoftException
	{
        try
        {
            return getModuleWiseTransactionIds(module, shipmentMode, accessType);
        }
        catch(FoursoftException exp)
        {
            //Logger.error(FILE_NAME,"FoursoftException ", exp);
            logger.error(FILE_NAME+" FoursoftException "+ exp);
            sessionContext.setRollbackOnly();
            throw exp;
        }
	} // end of getTransactionIds(String module, String accessType)

	/**
	* this retrieve the TransactionIds based on the accessType
	*
	* @param accessType
	*
	* @return ArrayList - return list of TransactionIds
	*/
	public	ArrayList getTransactionIds(String accessType) throws FoursoftException
	{
		try
        {
            return getModuleWiseTransactionIds("ALL", accessType);
        }
        catch(FoursoftException exp)
        {
            //Logger.error(FILE_NAME,"FoursoftException ", exp);
            logger.error(FILE_NAME+"FoursoftException "+ exp);
            sessionContext.setRollbackOnly();
            throw exp;
        }
	} // end of getTransactionIds(String accessType)

	/**
	* this retrieve the TransactionIds
	*
	* @return ArrayList - return list of TransactionIds
	*/
	public	ArrayList getTransactionIds() throws FoursoftException
	{
		try
        {
            return getModuleWiseTransactionIds("ALL", "ALL");
        }
        catch(FoursoftException exp)
        {
            //Logger.error(FILE_NAME,"FoursoftException ", exp);
            logger.error(FILE_NAME+" FoursoftException "+ exp);
            sessionContext.setRollbackOnly();
            throw exp;
        }
	} // end of getTransactionIds()


   /**
	* Which is used to retrieve the transactionIds modulewise
	*
	* @param module
	* @param accessType
	*
	* @return ArrayList - list of txIds
	*/
	private	ArrayList getModuleWiseTransactionIds(String module, String accessType) throws FoursoftException
	{
  	    ArrayList	transactionList	= new ArrayList();
        Statement	stmt			= null;
        ResultSet	rs				= null;

		String txSQL	= "";
		if (module.equals("ALL") )
		{
			txSQL = "SELECT FS_TXDETAIL.TXID FROM FS_TXMASTER, FS_TXDETAIL "
						+"WHERE FS_TXDETAIL.ACCESSTYPE = '"+ accessType +"' AND FS_TXDETAIL.TXID = FS_TXMASTER.TXID "
						+"ORDER BY FS_TXDETAIL.TXID";
			if(accessType.equalsIgnoreCase("ALL") )
			{
				txSQL = "SELECT TXID FROM FS_TXMASTER ORDER BY MODULE, TXID";
			}

		}
		else
		{
			  txSQL = "SELECT FS_TXDETAIL.TXID FROM FS_TXMASTER, FS_TXDETAIL "
						+"WHERE FS_TXDETAIL.ACCESSTYPE = '"+ accessType +"' AND FS_TXDETAIL.TXID = FS_TXMASTER.TXID AND "
						+"FS_TXMASTER.MODULE = '"+ module +"' ORDER BY FS_TXDETAIL.TXID";
		}

		try
		{
			makeConnection();
            stmt = connection.createStatement();
            rs	= stmt.executeQuery(txSQL);

            while(rs.next())
            {
 	            transactionList.add(rs.getString(1));
            }
			if(rs!=null)
				rs.close();

		}
		catch(SQLException sqle)
		{
			//Logger.info(FILE_NAME, "getModuleWiseTransactionIds(String module, String accessType)","SQL Exception  ", sqle);
      logger.info(FILE_NAME+ " getModuleWiseTransactionIds(String module, String accessType)"+" SQL Exception  "+ sqle);
		}
		catch(DBSysException dbs)
		{
			//Logger.error(FILE_NAME,"DBSysException ", dbs);
      logger.error(FILE_NAME+" DBSysException "+ dbs);
			//throw new EJBException(dbs.getMessage());
            throw FoursoftException.getException(dbs,FILE_NAME,"getModuleWiseTransactionIds");
		}
		finally
		{
				ConnectionUtil.closeConnection(connection, stmt);
		}
		return transactionList;
	} // end of getModuleWiseTransactionIds(String module, String accessType)
   

	private	ArrayList getModuleWiseTransactionIds(String module, int shipmentMode, String accessType) throws FoursoftException
	{
  	    ArrayList	transactionList	= new ArrayList();
        Statement	stmt			= null;
        ResultSet	rs				= null;

		String txSQL	= "";
		if (module.equals("ALL") )
		{
			txSQL = "SELECT FS_TXDETAIL.TXID FROM FS_TXMASTER, FS_TXDETAIL "
						+"WHERE FS_TXDETAIL.ACCESSTYPE = '"+ accessType +"' AND "
						+"FS_TXDETAIL.TXID = FS_TXMASTER.TXID "
						+"ORDER BY FS_TXDETAIL.TXID";
			if(accessType.equalsIgnoreCase("ALL") )
			{
				txSQL = "SELECT TXID FROM FS_TXMASTER ORDER BY MODULE, TXID";
			}

		}
		else
		{
			txSQL = "SELECT FS_TXDETAIL.TXID FROM FS_TXMASTER, FS_TXDETAIL "
						+"WHERE FS_TXDETAIL.ACCESSTYPE = '"+ accessType +"' AND FS_TXDETAIL.TXID = FS_TXMASTER.TXID AND "
						+"FS_TXMASTER.MODULE = '"+ module +"' AND FS_TXMASTER.SHIPMENTMODE = "+shipmentMode+" "
						+"ORDER BY FS_TXDETAIL.TXID";
		}
        
		try
		{
			makeConnection();
            stmt = connection.createStatement();
            rs	= stmt.executeQuery(txSQL);

            while(rs.next())
            {
 	            transactionList.add(rs.getString(1));
            }
			if(rs!=null)
				rs.close();

		}
		catch(SQLException sqle)
		{
			//Logger.info(FILE_NAME, "getModuleWiseTransactionIds(String module, String accessType)","SQL Exception  ", sqle);
      logger.info(FILE_NAME+ "getModuleWiseTransactionIds(String module, String accessType)"+" SQL Exception  "+ sqle);
		}
		catch(DBSysException dbs)
		{
			//Logger.error(FILE_NAME,"DBSysException ", dbs);
      logger.error(FILE_NAME+" DBSysException "+ dbs);
			//throw new EJBException(dbs.getMessage());
            throw FoursoftException.getException(dbs,FILE_NAME,"getModuleWiseTransactionIds");
		}
		finally
		{
				ConnectionUtil.closeConnection(connection, stmt);
		}
		return transactionList;
	} // end of getModuleWiseTransactionIds(String module, String accessType)

	/**
	* this retrieve the TransactionDetailVOB's based on the module and accessType
	*
	* @param module
	* @param accessType
	*
	* @return ArrayList - return list of TransactionDetailVOB's
	*/
	public	ArrayList getTransactionDetailVOBs(String module, String accessType) throws FoursoftException
	{
		try
        {
            return getModuleWiseTransactionDetailVOBs(module, accessType);
        }
        catch(FoursoftException exp)
        {
            //Logger.error(FILE_NAME,"FoursoftException ", exp);
            logger.error(FILE_NAME+" FoursoftException "+ exp);
            sessionContext.setRollbackOnly();
            throw exp;
        }
	} // end of  getTransactionDetailVOBs(String module, String accessType)
		
	public	ArrayList getTransactionDetailVOBs(String module, int shipmentMode, String accessType) throws FoursoftException
	{
		try
        {
            return getModuleWiseTransactionDetailVOBs(module, shipmentMode, accessType);
        }
        catch(FoursoftException exp)
        {
            //Logger.error(FILE_NAME,"FoursoftException ", exp);
            logger.error(FILE_NAME+"FoursoftException "+ exp);
            sessionContext.setRollbackOnly();
            throw exp;
        }
	} // end of  getTransactionDetailVOBs(String module, String accessType)

	/**
	* this retrieve the TransactionDetailVOB's based on the accessType
	*
	* @param accessType
	*
	* @return ArrayList - return list of TransactionDetailVOB's
	*/
	public	ArrayList getTransactionDetailVOBs(String accessType) throws FoursoftException
	{
        try
        {
            return getModuleWiseTransactionDetailVOBs("ALL", accessType);
        }
        catch(FoursoftException exp)
        {
            //Logger.error(FILE_NAME,"FoursoftException ", exp);
            logger.error(FILE_NAME+" FoursoftException "+ exp);
            sessionContext.setRollbackOnly();
            throw exp;
        }
	} // end of getTransactionDetailVOBs(String accessType)
	
	/**
	* Retrieves TransactionDetailVOBs by taking module and accessType as arguments
	*
	*
	* @param module
	* @param accessType
	*
	* @return ArrayList	- list of TransactionDetailVOBs
	*/
	private	ArrayList getModuleWiseTransactionDetailVOBs(String module, String accessType) throws FoursoftException
	{
  	    ArrayList	transactionList	= new ArrayList();
        Statement	stmt			= null;
        ResultSet	rs				= null;

		String txSQL	= "";
		if (module.equals("ALL") )
		{
			txSQL =	"SELECT "+
					"	FS_TXDETAIL.TXID, "+
					"	FS_TXDETAIL.ACCESSLEVEL, "+
					"	FS_TXMASTER.SHIPMENTMODE "+
					"FROM "+
					"	FS_TXMASTER, "+
					"	FS_TXDETAIL "+
					"WHERE "+
					"	FS_TXDETAIL.ACCESSTYPE = '"+ accessType +"' AND "+
					"	FS_TXDETAIL.TXID = FS_TXMASTER.TXID "+
					"ORDER BY "+
					"	FS_TXDETAIL.TXID";
		}
		else
		{
			txSQL =	"SELECT "+
					"	FS_TXDETAIL.TXID, "+
					"	FS_TXDETAIL.ACCESSLEVEL, "+
					"	FS_TXMASTER.SHIPMENTMODE "+
					"FROM "+
					"	FS_TXMASTER, "+
					"	FS_TXDETAIL "+
					"WHERE "+
					"	FS_TXDETAIL.ACCESSTYPE = '"+ accessType +"' AND "+
					"	FS_TXDETAIL.TXID = FS_TXMASTER.TXID AND "+
					"	FS_TXMASTER.MODULE = '"+ module +"' "+
					"ORDER BY "+
					"	FS_TXDETAIL.TXID";
		}

		//Logger.info(FILE_NAME,"getModuleWiseTransactionDetailVOBs(String module, String accessType)","TX SQL : "+txSQL);
		try
		{
			makeConnection();
            stmt = connection.createStatement();
            rs	= stmt.executeQuery(txSQL);

            while(rs.next()) 
            {
				TxDetailVOB	txDetailVOB	=	new TxDetailVOB( rs.getString(1), rs.getInt(2) );
				
				if(rs.getObject(3) != null) 
                {
					txDetailVOB.shipmentMode	=	rs.getInt(3);
				}
				
 	            transactionList.add( txDetailVOB );
            }
			if(rs!=null)
				rs.close();

		}
		catch(SQLException sqle)
		{
			//Logger.error(FILE_NAME,"getModuleWiseTransactionDetailVOBs(String module, String accessType)","SQL Exception ",sqle);
      logger.error(FILE_NAME+" getModuleWiseTransactionDetailVOBs(String module, String accessType)"+"SQL Exception "+sqle);
		}
		catch(DBSysException dbs)
		{
			//Logger.error(FILE_NAME,"DBSysException ", dbs);
      logger.error(FILE_NAME+" DBSysException "+ dbs);
			//throw new EJBException(dbs.getMessage());
            throw FoursoftException.getException(dbs,FILE_NAME,"getModuleWiseTransactionDetailVOBs()");
		}
		finally
		{
				ConnectionUtil.closeConnection(connection, stmt);
		}
		return transactionList;
	} //	end of getModuleWiseTransactionDetailVOBs(String module, String accessType)
	
	
	private	ArrayList getModuleWiseTransactionDetailVOBs(String module, int shipmentMode, String accessType) throws FoursoftException
	{
  	    ArrayList	transactionList	= new ArrayList();
        Statement	stmt			= null;
        ResultSet	rs				= null;

		String txSQL	= "";
		if (module.equals("ALL") )
		{
			txSQL =	"SELECT "+
					"	FS_TXDETAIL.TXID, "+
					"	FS_TXDETAIL.ACCESSLEVEL, "+
					"	FS_TXMASTER.SHIPMENTMODE "+
					"FROM "+
					"	FS_TXMASTER, "+
					"	FS_TXDETAIL "+
					"WHERE "+
					"	FS_TXDETAIL.ACCESSTYPE = '"+ accessType +"' AND "+
					"	FS_TXDETAIL.TXID = FS_TXMASTER.TXID "+
					"ORDER BY "+
					"	FS_TXDETAIL.TXID";
		}
		else
		{
			txSQL = "SELECT "+
					"	FS_TXDETAIL.TXID, "+
					"	FS_TXDETAIL.ACCESSLEVEL, "+
					"	FS_TXMASTER.SHIPMENTMODE "+
					"FROM "+
					"	FS_TXMASTER, "+
					"	FS_TXDETAIL "+
					"WHERE "+
					"	FS_TXDETAIL.ACCESSTYPE = '"+ accessType +"' AND "+
					"	FS_TXDETAIL.TXID = FS_TXMASTER.TXID AND "+
					"	FS_TXMASTER.MODULE = '"+ module +"' AND "+
					"	FS_TXMASTER.SHIPMENTMODE = "+shipmentMode+" "+
					"ORDER BY "+
					"	FS_TXDETAIL.TXID";
		}

		//Logger.info(FILE_NAME,"getModuleWiseTransactionDetailVOBs(String module, String accessType)","TX SQL : "+txSQL);
		try
		{
			makeConnection();
            stmt = connection.createStatement();
            rs	= stmt.executeQuery(txSQL);
			
            while(rs.next())
            {
				TxDetailVOB	txDetailVOB	=	new TxDetailVOB( rs.getString(1), rs.getInt(2) );
				
				if(rs.getObject(3) != null) {
					txDetailVOB.shipmentMode	=	rs.getInt(3);
				}
				
 	            transactionList.add( txDetailVOB );
            }
			if(rs!=null)
				rs.close();
		}
		catch(SQLException sqle)
		{
			//Logger.error(FILE_NAME,"getModuleWiseTransactionDetailVOBs(String module, String accessType)","SQL Exception ",sqle);
      logger.error(FILE_NAME+" getModuleWiseTransactionDetailVOBs(String module, String accessType)"+"SQL Exception "+sqle);
		}
		catch(DBSysException dbs)
		{
			//Logger.error(FILE_NAME,"DBSysException ", dbs);
      logger.error(FILE_NAME+" DBSysException "+ dbs);
			//throw new EJBException(dbs.getMessage());
            throw FoursoftException.getException(dbs,FILE_NAME,"getModuleWiseTransactionDetailVOBs()");
		}
		finally
		{
			ConnectionUtil.closeConnection(connection, stmt);
		}
		return transactionList;
	} //	end of getModuleWiseTransactionDetailVOBs(String module, int shipmentMode, String accessType)
    
	/**
	* It will check whether any user is associated with the given role
	* If any user is is assosited with this role then it will return true
	*
	* @param roleId
	* @param locationId
	*
	* @return boolean
	**/
	private boolean isUserAssociatedWithRole(String roleId,String locationId) throws FoursoftException
	{
        Statement	stmt		= null;
		ResultSet	rs			= null;

        boolean isAssociated 	= false;

        String IS_ASSOCIATED = "SELECT ROLEID, ROLE_LOCATIONID FROM FS_USERROLES "
                    +"WHERE ROLEID = '"+ roleId + "' AND ROLE_LOCATIONID = '"+ locationId + "' ";
        try
        {
            makeConnection();

            stmt = connection.createStatement();
            rs		= stmt.executeQuery(IS_ASSOCIATED);

            while ( rs.next() )
            {
                isAssociated = true;
            }
        }
        catch (SQLException sql)
        {
            //Logger.error(FILE_NAME,"isUserAssociatedWithRole(String roleId,String locationId)","SQL Exception ",sql);
            logger.error(FILE_NAME+" isUserAssociatedWithRole(String roleId,String locationId)"+" SQL Exception "+sql);
        }
        catch(DBSysException dbs)
        {
            //Logger.error(FILE_NAME,"DBSysException ", dbs);
            logger.error(FILE_NAME+" DBSysException "+ dbs);
            //throw new EJBException(dbs.getMessage());
            throw FoursoftException.getException(dbs,FILE_NAME,"isUserAssociatedWithRole");
        }
        finally
        {
        //Modified By RajKumari on 23-10-2008 for Connection Leakages.
           // ConnectionUtil.closeConnection(connection, stmt);
            ConnectionUtil.closeConnection(connection, stmt, rs);
        }
        return isAssociated;
	} // end of isUserAssociatedWithRole(String roleId,String locationId)
	
	/**
	* Deleting Role through Entity Bean
	* @param roleId
	* @param locationId
	*
	* @exception DataIntegrityViolationException - it will be thrown, If some Users are assosiated with this Role
	*
	* @return boolean - returns true, if the deletion succeeds
	*/
    public boolean removeRole(String roleId, String locationId, String createUserId, String createLocationId) throws DataIntegrityViolationException , FoursoftException
	{
		//RoleMaster		remote	= null;
		RoleMaster		role	= null;
		RoleMasterBeanPK	pkObj	= null;

		boolean	successFlag	= false;

		pkObj	= new RoleMasterBeanPK();
		pkObj.roleId		= roleId;
		pkObj.locationId	= locationId;
		try
	    {

		    if ( isUserAssociatedWithRole(roleId, locationId) )
				throw new DataIntegrityViolationException("There are some users associated with this Role .So you can't delete this Role  : "+ locationId+"/"+roleId);


			//initializeEjbHomes();
		    //remote		= (RoleMaster) rmHome.findByPrimaryKey(pkObj);
			role		= (RoleMaster) rmHome.findByPrimaryKey(pkObj);

			//remote.remove();
			role.remove();

			UserLogDAO userLogDAO = new UserLogDAO();

			Timestamp createTimestamp	= new Timestamp((new java.util.Date().getTime()) );
			UserLogVOB	userLogVOB = new UserLogVOB(createLocationId, createUserId, "RoleRegistration", locationId+"."+roleId, createTimestamp, "DELETE" );
			userLogDAO.setTransactionDetails(userLogVOB);

			successFlag	= true;
	    }
		catch(DataIntegrityViolationException de)
	    {
			//Logger.error(FILE_NAME,"removeRole(String roleId, String locationId, String createUserId, String createLocationId)","DataIntegrity Exception ",de);
      logger.error(FILE_NAME+"removeRole(String roleId, String locationId, String createUserId, String createLocationId)"+"DataIntegrity Exception "+de);
			throw de;
	    }
	    catch(Exception ex)
	    {
			//Logger.error(FILE_NAME,"removeRole(String roleId, String locationId, String createUserId, String createLocationId)","Unknown Exception ",ex);
      logger.error(FILE_NAME+"removeRole(String roleId, String locationId, String createUserId, String createLocationId)"+"Unknown Exception "+ex);
			//throw new javax.ejb.EJBException("Error in deleting the Role" );
            sessionContext.setRollbackOnly();
            throw FoursoftException.getException(ex,FILE_NAME,"removeRole");
	    }
		return successFlag;
	} // end of removeRole(String roleId, String locationId, String createUserId, String createLocationId) 


	/**
	* This is used to retrieve the RoleId (locationId) which are belongs to a specific location, module and accessType
	* @param roleId
	* @param roleLocationId
	* @param locationId
	* @param accessType
	* @param usersLocation
	* @param usersRoleAccessType
	*
	* @return ArrayList -
	*/
	public ArrayList getRoleIdLocation(String roleId, String roleLocationId, String locationId, String accessType, String usersLocation, String usersRoleAccessType, String roleModule) throws FoursoftException
	{
		ArrayList	roleLocationList = new ArrayList();
		
		try
		{
			roleLocationList	= userRoleDAO.getRoleIdLocation(roleId, roleLocationId, locationId, accessType, usersLocation, usersRoleAccessType, roleModule);
		}
		catch(Exception e)
		{
			//Logger.error(FILE_NAME,"getRoleIdLocation(String roleId, String roleLocationId, String locationId, String accessType, String usersLocation, String usersRoleAccessType)","Unknown Exception ",e);
      logger.error(FILE_NAME+"getRoleIdLocation(String roleId, String roleLocationId, String locationId, String accessType, String usersLocation, String usersRoleAccessType)"+"Unknown Exception "+e);
			//throw new EJBException("Exception in retriving the Role Id and Location. ");
            sessionContext.setRollbackOnly();
            throw FoursoftException.getException(e,FILE_NAME,"getRoleIdLocation");
		}

		return roleLocationList;

	} // end of getRoleIdLocation(String locationId, String accessType, String roleModule, String usersLocation, String usersRoleAccessType)


	/**
	* Which is used to getting the LocationIds
	* @param locationId
	* @param userType - access type of the user who is creating Role/User
	* @param accessType		- access type to which User/role creating
	* @exception	SQLException
	*
	* @retun ArrayList	- list of locations
	*/
	public ArrayList getLocationIds( String locationId, String userType, String accessType, String locationFilter) throws FoursoftException
	{
		ArrayList	locationList = new ArrayList();
		try
		{
			locationList	= userRoleDAO.getLocationIds(locationId, userType, accessType, locationFilter);
		}
		catch(Exception e)
		{
			//Logger.error(FILE_NAME,"getLocationIds( String locationId, String userType, String accessType, String locationFilter)","Unknown Exception ",e);
      logger.error(FILE_NAME+" getLocationIds( String locationId, String userType, String accessType, String locationFilter)"+"Unknown Exception "+e);
			//throw new EJBException("Exception in retrieving the Location Ids. ");
            //this.sessionContext.setRollbackOnly();
            throw FoursoftException.getException(e,FILE_NAME,"getLocationIds");
		}
		return locationList;
	} // end of getLocationIds( String locationId, String userType, String accessType, String locationFilter)

	/**
	* Retrieve the Roles Based on the Level ( ie., CORPORATE or TERMINL )	AND ROLE_TYPE (SPECIFIC | GENERIC)
	*
	* @param locationId
	* @param accessType
	* @param filter
	*
	* @return ArrayList	- list of Roles
	*/
	public ArrayList getRoleIds(String locationId, String accessType, String filter) throws FoursoftException
	{
  	    ArrayList	roleList	= new ArrayList();
        Statement	stmtRoles	= null;
        ResultSet	rsRoles		= null;

		String ROLE_SELECT = "";
		if(filter.equals("") )
			ROLE_SELECT = "SELECT ROLEID,DESCRIPTION FROM FS_ROLEMASTER WHERE LOCATIONID = '"+ locationId +"' AND ROLELEVEL = '"+ accessType +"' ORDER BY ROLEID";
		else 
			ROLE_SELECT = "SELECT ROLEID,DESCRIPTION FROM FS_ROLEMASTER WHERE LOCATIONID = '"+ locationId +"' AND ROLELEVEL = '"+ accessType +"' AND ROLEID LIKE '"+ filter +"%' ORDER BY ROLEID";

		//Logger.info(FILE_NAME, "getRoleIds(String locationId, String accessType)", "RoleIds SQL : "+ROLE_SELECT);
		try
		{
			makeConnection();
            stmtRoles = connection.createStatement();

            rsRoles= stmtRoles.executeQuery(ROLE_SELECT);
            while(rsRoles.next())
            {
				String desc =rsRoles.getString(2);
				if(desc==null)
				roleList.add(rsRoles.getString(1));
				else
 	            roleList.add(rsRoles.getString(1)+"["+desc+"]");
            }
			if(rsRoles!=null)
				rsRoles.close();

		}
		catch(SQLException sqle)
		{
			//Logger.error(FILE_NAME,"getRoleIds(String locationId, String accessType, String filter)","ERROR in Retrive the RoleIds - SQL Exception ",sqle);
      logger.error(FILE_NAME+" getRoleIds(String locationId, String accessType, String filter)"+"ERROR in Retrive the RoleIds - SQL Exception "+sqle);
		}
		catch(DBSysException dbs)
		{
			//Logger.error(FILE_NAME,"DBSysException ", dbs);
      logger.error(FILE_NAME+"DBSysException "+ dbs);
			//throw new EJBException(dbs.getMessage());
            this.sessionContext.setRollbackOnly();
            throw FoursoftException.getException(dbs,FILE_NAME,"getRoleIds");
		}
		finally
		{
				ConnectionUtil.closeConnection(connection, stmtRoles);
		}
		return roleList;
	} // end of getRoleIds(String locationId, String accessType, String filter)

	/**
	* Used to update the Role  -- updates thru Entity Bean
	* @param roleModel			-- RoleModel
	* @param createUserId		-- created UserId
	* @param createLocationId 	-- created LocationId
	*
	*/
	public void updateRoleModel(RoleModel roleModel, String createUserId, String createLocationId) throws FoursoftException, DataIntegrityViolationException
	{

		//RoleMaster		remote	= null;
		RoleMaster		role	= null;

		RoleMasterBeanPK	pkObj	= null;

		pkObj	= new RoleMasterBeanPK();
		pkObj.roleId		= roleModel.getRoleId();
		pkObj.locationId	= roleModel.getLocationId();

		try
	    {
			//initializeEjbHomes();
		    //remote		= (RoleMaster) rmHome.findByPrimaryKey(pkObj);
			role		= (RoleMaster) rmHome.findByPrimaryKey(pkObj);

			//remote.setRoleModel(roleModel);
			role.setRoleModel(roleModel);

			UserLogDAO  userLogDAO = new UserLogDAO();

			Timestamp createTimestamp	= new Timestamp((new java.util.Date().getTime()) );

			UserLogVOB	userLogVOB	= new UserLogVOB(createLocationId, createUserId, "RoleRegistration", roleModel.getLocationId()+"."+roleModel.getRoleId(), createTimestamp, "MODIFY" );
			userLogDAO.setTransactionDetails(userLogVOB);
	    }
		catch(DataIntegrityViolationException dExc)
		{
			//Logger.warning(FILE_NAME, "updateRoleModel(...)","StaleData Error ",dExc);
      logger.warn(FILE_NAME+ "updateRoleModel(...)"+"StaleData Error "+dExc);
			throw dExc;
		}
	    catch(Exception ex)
	    {
			 //Logger.error(FILE_NAME,"updateRoleModel(RoleModel roleModel, String createUserId, String createLocationId)","Exception in updating ", ex);
       logger.error(FILE_NAME+" updateRoleModel(RoleModel roleModel, String createUserId, String createLocationId)"+"Exception in updating "+ ex);
			 //throw new javax.ejb.EJBException("Error in  Role Modification" );
             this.sessionContext.setRollbackOnly();
             throw FoursoftException.getException(ex,FILE_NAME,"updateRoleModel");
	    }
	} // end of updateRoleModel(RoleModel roleModel, String createUserId, String createLocationId)	

	/**
	* To get all the permissions asscosiated with the role and locationId
	*
	* @param roleId
	* @param locationId
	*
	* @return Hashtable - (key:value) - (txId:accessLevel)
	*/
	public Hashtable getAllRolePermissions( String roleId, String locationId ) throws FoursoftException
	{
		Hashtable hashRolePermissions = null;

		Statement	stmt		= null;
		ResultSet	rs			= null;

		String ROLE_PERMISSIONS = "SELECT TXID, ACCESSLEVEL FROM FS_ROLEPERMISSIONS "
					+"WHERE ROLEID = '"+ roleId + "' AND LOCATIONID = '"+ locationId + "' ORDER BY TXID ";
		try
	  	{
        	makeConnection();

			stmt = connection.createStatement();

			rs		= stmt.executeQuery(ROLE_PERMISSIONS);

			hashRolePermissions = new Hashtable();
			while ( rs.next() )
			{
				hashRolePermissions.put(rs.getString(1),new Integer(rs.getInt(2)) );
			}
			if(rs!=null)
				rs.close();
      	}
		catch (SQLException sql)
		{
			//Logger.error(FILE_NAME,"getAllRolePermissions( String roleId, String locationId )","SQL Exception ",sql);
      logger.error(FILE_NAME+" getAllRolePermissions( String roleId, String locationId )"+"SQL Exception "+sql);
		}
		catch(DBSysException dbs)
		{
			//Logger.error(FILE_NAME,"DBSysException ", dbs);
      logger.error(FILE_NAME+" DBSysException "+ dbs);
			//throw new EJBException(dbs.getMessage());
            sessionContext.setRollbackOnly();
            throw FoursoftException.getException(dbs,FILE_NAME,"getAllRolePermissions");
		}
		finally
		{
			ConnectionUtil.closeConnection(connection, stmt);
		}

		return hashRolePermissions;
	} // end of getAllRolePermissions( String roleId, String locationId )

	/**
	* Retrieving the Role Info
	* @param roleId
	* @param locationId
	*
	* @return RoleMasterDOB - retrieve the RoleMasterDOB value Object
	*/
    public RoleModel getRoleModel(String roleId, String locationId) throws FoursoftException,NotExistsException
	{

  	RoleMaster			role		= null;

		RoleMasterBeanPK	pkObj		= null;
		RoleModel			roleModel	= null;

		pkObj	= new RoleMasterBeanPK();
		pkObj.roleId		= roleId;
		pkObj.locationId	= locationId;

    String message = "Uable to find the Role Id details for Role Id:"+roleId;
    
		try
	    {
			//initializeEjbHomes();
		    //remote		= (RoleMaster)	rmHome.findByPrimaryKey(pkObj);
			role		= (RoleMaster)	rmHome.findByPrimaryKey(pkObj);

			//roleModel	= (RoleModel)	remote.getRoleModel();
			roleModel	= (RoleModel)	role.getRoleModel();
	    }
		  catch(FinderException fe)
		  {
			  //Logger.error(FILE_NAME, "getRoleModel(String roleId, String locationId)","ObjectNot Found Exception ",fe);
        logger.error(FILE_NAME+ " getRoleModel(String roleId, String locationId)"+" ObjectNot Found Exception "+fe);
			  throw FoursoftException.getException(fe,FILE_NAME,"getRoleModel",message,NotExistsException.ERRORCODE);
		  }
	    catch(Exception ex)
	    { 
			  //Logger.error(FILE_NAME,"getRoleModel(String roleId, String locationId)","Unknown Exceptions",ex);
        logger.error(FILE_NAME+" getRoleModel(String roleId, String locationId)"+" Unknown Exceptions"+ex);
			  sessionContext.setRollbackOnly();
				
	    }
		return roleModel;
	} // end of getRoleModel(String roleId, String locationId)
	
	/**
	* This is used to retrieve the RolePermissions (TxApplicationVOB)
	*
	* @param roleId
	* @param locationId
	*
	* @return ArrayList	- list TxApplicationVOB
	*/

	private static final String	ROLE_PERMISSIONS_QUERY = 
							 "SELECT FS_ROLEPERMISSIONS.TXID, FS_TXMASTER.DESCRIPTION, FS_ROLEPERMISSIONS.ACCESSLEVEL "
							+"FROM FS_ROLEPERMISSIONS, FS_TXMASTER "
							+"WHERE FS_ROLEPERMISSIONS.ROLEID = ? AND "
							+"FS_ROLEPERMISSIONS.LOCATIONID = ? AND "
							+"FS_ROLEPERMISSIONS.TXID = FS_TXMASTER.TXID ORDER BY FS_ROLEPERMISSIONS.TXID";
	
	public ArrayList getRolePermissions(String roleId, String locationId ) throws FoursoftException
	{

		PreparedStatement	pstmt	= null;
        ResultSet			rs		= null;
		ArrayList			accessList	= new ArrayList();

		try
		{
			makeConnection();
          	pstmt  = connection.prepareStatement( ROLE_PERMISSIONS_QUERY );
			pstmt.setString(1, roleId);
			pstmt.setString(2, locationId);
            rs		= pstmt.executeQuery();
            while(rs.next())
            {
				accessList.add(new TxDetailVOB(rs.getString(1), rs.getString(2), rs.getInt(3)) );
            }
			if(rs!=null)
				rs.close();
		}
		catch(SQLException sqle)
		{
			//Logger.error(FILE_NAME,"getRolePermissions(String roleId, String locationId )","SQL Exception ",sqle);
      logger.error(FILE_NAME+" getRolePermissions(String roleId, String locationId )"+"SQL Exception "+sqle);
		}
		catch(DBSysException dbs)
		{
			//Logger.error(FILE_NAME,"DBSysException ", dbs);
      logger.error(FILE_NAME+" DBSysException "+ dbs);
			//throw new EJBException(dbs.getMessage());
            sessionContext.setRollbackOnly();
            throw FoursoftException.getException(dbs,FILE_NAME,"getRolePermissions");
		}
		finally
		{
				ConnectionUtil.closeConnection(connection, pstmt);
		}
		accessList.trimToSize();
		return accessList;
	} // end of getRolePermissions(String roleId, String locationId )
	
	/**
	 * This is used to getting the Roles information for View All
	 * Returns list of RoleMasterDOB's
	 *
	 * @return ArrayList - return the RoleMasterDOB list
	 *
	 */

	private static final String	ROLE_LIST_QUERY	=
							"SELECT ROLEID, LOCATIONID, ROLEMODULE, ROLETYPE, ROLELEVEL, DESCRIPTION "+
							"FROM FS_ROLEMASTER ORDER BY LOCATIONID, ROLELEVEL, ROLEID";
              
    private static final String	ROLE_LIST_QUERY_FILTER	=
							"SELECT ROLEID, LOCATIONID, ROLEMODULE, ROLETYPE, ROLELEVEL, DESCRIPTION "+
							"FROM FS_ROLEMASTER WHERE LOCATIONID = ? ORDER BY LOCATIONID, ROLELEVEL, ROLEID";

	////////////////////
	private static final String	LICENSEE_ACCESS_QUERY	=
							"SELECT ROLEID, LOCATIONID, ROLEMODULE, ROLETYPE, ROLELEVEL, DESCRIPTION "+
							"FROM FS_ROLEMASTER WHERE ROLELEVEL IN ('LICENSEE', 'HO') ORDER BY LOCATIONID, ROLELEVEL, ROLEID";
    private static final String	HO_ACCESS_TYPES	=
							"SELECT ROLEID, LOCATIONID, ROLEMODULE, ROLETYPE, ROLELEVEL, DESCRIPTION "+
							"FROM FS_ROLEMASTER WHERE ROLELEVEL IN ('HO', 'COMPANY', 'WAREHOUSE', 'PROJECTLOG', 'CUSTWH') ORDER BY LOCATIONID, ROLELEVEL, ROLEID ";
    private static final String	COMPANY_ACCESS_QUERY	=
							"SELECT ROLEID, LOCATIONID, ROLEMODULE, ROLETYPE, ROLELEVEL, DESCRIPTION "+
							"FROM FS_ROLEMASTER WHERE ROLELEVEL IN ('COMPANY', 'WAREHOUSE', 'PROJECTLOG', 'CUSTWH') ORDER BY LOCATIONID, ROLELEVEL, ROLEID";
    private static final String	PROJECTLOG_ACCESS_QUERY	=
							"SELECT ROLEID, LOCATIONID, ROLEMODULE, ROLETYPE, ROLELEVEL, DESCRIPTION "+
							"FROM FS_ROLEMASTER WHERE ROLELEVEL IN ('PROJECTLOG', 'CUSTWH') ORDER BY LOCATIONID, ROLELEVEL, ROLEID";
    private static final String	WAREHOUSE_ACCESS_QUERY	=
							"SELECT ROLEID, LOCATIONID, ROLEMODULE, ROLETYPE, ROLELEVEL, DESCRIPTION "+
							"FROM FS_ROLEMASTER WHERE ROLELEVEL IN ('WAREHOUSE', 'CUSTWH','ELTRM') ORDER BY LOCATIONID, ROLELEVEL, ROLEID";
    private static final String	CUSTWH_ACCESS_QUERY	=
							"SELECT ROLEID, LOCATIONID, ROLEMODULE, ROLETYPE, ROLELEVEL, DESCRIPTION "+
							"FROM FS_ROLEMASTER WHERE ROLELEVEL IN ('CUSTWH', 'ELCRM', 'ELVRM') ORDER BY LOCATIONID, ROLELEVEL, ROLEID";
    private static final String	ELCRM_ACCESS_QUERY	=
							"SELECT ROLEID, LOCATIONID, ROLEMODULE, ROLETYPE, ROLELEVEL, DESCRIPTION "+
							"FROM FS_ROLEMASTERWHERE ROLELEVEL IN ('ELCRM') ORDER BY LOCATIONID, ROLELEVEL, ROLEID ";
    private static final String	ELVRM_ACCESS_QUERY	=
							"SELECT ROLEID, LOCATIONID, ROLEMODULE, ROLETYPE, ROLELEVEL, DESCRIPTION "+
							"FROM FS_ROLEMASTER WHERE ROLELEVEL IN ('ELVRM') ORDER BY LOCATIONID, ROLELEVEL, ROLEID";
    private static final String	ELTRM_ACCESS_QUERY	=
							"SELECT ROLEID, LOCATIONID, ROLEMODULE, ROLETYPE, ROLELEVEL, DESCRIPTION "+
							"FROM FS_ROLEMASTER WHERE ROLELEVEL IN ('ELTRM') ORDER BY LOCATIONID, ROLELEVEL, ROLEID";
    
	// used for ELOG

	public ArrayList getRoleViewAllInformation(String accessType) throws FoursoftException
	{
  	    ArrayList	roleList	= new ArrayList();

		RoleModel	roleModel = null;

		Statement			stmt	= null;
        ResultSet			rs		= null;
		String			ROLES_QUERY = null;
		
		try
		{
			makeConnection();
          	stmt  = connection.createStatement();
			if(accessType.equalsIgnoreCase("LICENSEE"))
			{
				ROLES_QUERY=LICENSEE_ACCESS_QUERY;
			}
			else if( accessType.equalsIgnoreCase( "HO" ) || accessType.equalsIgnoreCase( "HO_TERMINAL" ))
			{
				ROLES_QUERY=HO_ACCESS_TYPES;
			}
			else if(accessType.equalsIgnoreCase("COMPANY"))
			{
				ROLES_QUERY=COMPANY_ACCESS_QUERY;
			}
			else if(accessType.equalsIgnoreCase("PROJECTLOG"))
			{
				ROLES_QUERY=PROJECTLOG_ACCESS_QUERY;
			}
			else if(accessType.equalsIgnoreCase("WAREHOUSE"))
			{
				ROLES_QUERY=WAREHOUSE_ACCESS_QUERY;
			}
			else if(accessType.equalsIgnoreCase("CUSTWH"))
			{
				ROLES_QUERY=CUSTWH_ACCESS_QUERY;
			}
			else if(accessType.equalsIgnoreCase("ELVRM"))
			{
				ROLES_QUERY=ELVRM_ACCESS_QUERY;
			}
			else if(accessType.equalsIgnoreCase("ELCRM"))
			{
				ROLES_QUERY=ELCRM_ACCESS_QUERY;
			}
			else if(accessType.equalsIgnoreCase("ELTRM"))
			{
				ROLES_QUERY=ELTRM_ACCESS_QUERY;
			}

			
			//System.out.println("ROLES_QUERY= "+ROLES_QUERY);

            rs		= stmt.executeQuery( ROLES_QUERY );
			
            while(rs.next())
            {
				roleList.add( new RoleModel(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6)) );
            }
			if(rs!=null)
				rs.close();
		}
		catch(SQLException sqle)
		{
			//Logger.error(FILE_NAME, "getRoleViewAllInformation()","SQL Exception ",sqle);
      logger.error(FILE_NAME+ "getRoleViewAllInformation()"+"SQL Exception "+sqle);
		}
		catch(DBSysException dbs)
		{
			//Logger.error(FILE_NAME,"DBSysException ", dbs);
      logger.error(FILE_NAME+" DBSysException "+ dbs);
			//throw new EJBException(dbs.getMessage());
            sessionContext.setRollbackOnly();
            throw FoursoftException.getException(dbs,FILE_NAME,"getRoleViewAllInformation()");
		}
		finally
		{
			ConnectionUtil.closeConnection(connection, stmt);
		}
		roleList.trimToSize();
		return roleList;
	} // end of getRoleViewAllInformation()

// General Method
	public ArrayList getRoleViewAllInformation() throws FoursoftException
	{
  	    ArrayList	roleList	= new ArrayList();

		RoleModel	roleModel = null;

		Statement			stmt	= null;
        ResultSet			rs		= null;

		try
		{
			makeConnection();
          	stmt  = connection.createStatement();
            rs		= stmt.executeQuery( ROLE_LIST_QUERY );
			
            while(rs.next())
            {
				roleList.add( new RoleModel(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6)) );
            }
			if(rs!=null)
				rs.close();
		}
		catch(SQLException sqle)
		{
			//Logger.error(FILE_NAME, "getRoleViewAllInformation()","SQL Exception ",sqle);
      logger.error(FILE_NAME+ " getRoleViewAllInformation()"+"SQL Exception "+sqle);
		}
		catch(DBSysException dbs)
		{
			//Logger.error(FILE_NAME,"DBSysException ", dbs);
      logger.error(FILE_NAME+" DBSysException "+ dbs);
			//throw new EJBException(dbs.getMessage());
            sessionContext.setRollbackOnly();
            throw FoursoftException.getException(dbs,FILE_NAME,"getRoleViewAllInformation()");
		}
		finally
		{
			ConnectionUtil.closeConnection(connection, stmt);
		}
		roleList.trimToSize();
		return roleList;
	} // end of getRoleViewAllInformation()

/** Exclusively used for EP
	public ArrayList getRoleViewAllInformation(String locationId) throws FoursoftException
	{
        ArrayList	roleList	= new ArrayList();
		RoleModel	roleModel = null;
		PreparedStatement			pStmt	= null;
        ResultSet			rs		= null;

		try
		{
            makeConnection();
            pStmt  = connection.prepareStatement(ROLE_LIST_QUERY_FILTER);
            pStmt.setString(1,locationId);
            rs		= pStmt.executeQuery();
			
            while(rs.next())
            {
                roleList.add( new RoleModel(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6)) );
            }
            if(rs!=null)
                rs.close();
        }
        catch(SQLException sqle)
		{
			Logger.error(FILE_NAME, "getRoleViewAllInformation()","SQL Exception ",sqle);
		}
		catch(DBSysException dbs)
		{
			Logger.error(FILE_NAME,"DBSysException ", dbs);
			//throw new EJBException(dbs.getMessage());
            sessionContext.setRollbackOnly();
            throw FoursoftException.getException(dbs,FILE_NAME,"getRoleViewAllInformation()");
		}
		finally
		{
			ConnectionUtil.closeConnection(connection, pStmt);
		}
		roleList.trimToSize();
		return roleList;
	} // end of getRoleViewAllInformation()
  
  *///
	/**
	* This is used to remove the user
	* It uses the Entity bean to remove the user
	*
	* @param userId
	* @param locationId
	*
	* @exception NotExistsException
	*/
    public boolean removeUser(String userId, String locationId, String createUserId, String createLocationId)  throws NotExistsException, FoursoftException
	{
		//UserMaster		remote	= null;
		UserMaster		user	= null;

		UserMasterBeanPK	pkObj	= null;

		boolean	successFlag	= false;

		pkObj	= new UserMasterBeanPK();
		pkObj.userId		= userId;
		pkObj.locationId	= locationId;
		try
	    {
			//initializeEjbHomes();
		    //remote		= (UserMaster) umHome.findByPrimaryKey(pkObj);
			user		= (UserMaster) umHome.findByPrimaryKey(pkObj);

			//remote.remove();
			user.remove();

			UserLogDAO  userLogDAO = new UserLogDAO();

			Timestamp createTimestamp	= new Timestamp((new java.util.Date().getTime()) );
			UserLogVOB userLogVOB	= new UserLogVOB(createLocationId, createUserId, "UserRegistration", locationId+"."+userId, createTimestamp, "DELETE" );
			userLogDAO.setTransactionDetails(userLogVOB);

			successFlag	= true;
	    }
		catch(javax.ejb.ObjectNotFoundException oe)
		{
			//Logger.error(FILE_NAME, "removeUser(String userId, String locationId, String createUserId, String createLocationId)","ObjectNot Found Exception ",oe);
      logger.error(FILE_NAME+ "removeUser(String userId, String locationId, String createUserId, String createLocationId)"+"ObjectNot Found Exception "+oe);
			throw new NotExistsException("The User '"+userId+"/"+locationId+"' does not exist.");
		}
	    catch(Exception ex)
	    {
			//Logger.error(FILE_NAME, "removeUser(String userId, String locationId, String createUserId, String createLocationId)"," Exception ",ex);
      logger.error(FILE_NAME+ "removeUser(String userId, String locationId, String createUserId, String createLocationId)"+" Exception "+ex);
			//throw new javax.ejb.EJBException("Error in  deleting the user" );
            sessionContext.setRollbackOnly();
            throw FoursoftException.getException(ex,FILE_NAME,"removeUser");
	    }
		return successFlag;
	} // end of removeUser(String userId, String locationId, String createUserId, String createLocationId)


	/**
	*  This is used to retrieve the UserIds
	*
	* @param locationId
	* @param accessType
	* @param filter
	*
	* @return ArrayList	- list of userIds
	*/
	public ArrayList getUserIds(String locationId, String accessType, String filter,String terminalId,String terminalType) throws FoursoftException
	{
  	    ArrayList	userList	= new ArrayList();
        Statement	stmtUser	= null;
        ResultSet	rsUser		= null;
		String USER_SELECT = "";
		String userFilter = filter.toUpperCase();
//@@ Commented by subrahmanyam for Enhancement 154384 on 10/04/09     
// USER_SELECT = "SELECT USERID FROM FS_USERMASTER WHERE LOCATIONID = '"+ locationId +"' AND USER_LEVEL = '"+ accessType +"' AND USERID LIKE '"+userFilter+"%' ORDER BY USERID";     
//Added by subrahmanyam for Enhancement 154384 on 10/04/09    
    if("OPER_TERMINAL".equalsIgnoreCase(accessType) && "ADMN_TERMINAL".equalsIgnoreCase(terminalType))
    {
      USER_SELECT = " SELECT USERID  FROM FS_USERMASTER UM, FS_FR_TERMINAL_REGN TR  WHERE UM.LOCATIONID =  '"+ locationId +"' AND USER_LEVEL = '"+ accessType +
      "' AND USERID LIKE '"+userFilter+"%' AND UM.LOCATIONID IN(SELECT CHILD_TERMINAL_ID FROM FS_FR_TERMINAL_REGN TR WHERE PARENT_TERMINAL_ID = '"+terminalId+
      "') AND UM.LOCATIONID = TR.CHILD_TERMINAL_ID ORDER BY USERID";
    }   
    else
      USER_SELECT = "SELECT USERID FROM FS_USERMASTER WHERE LOCATIONID = '"+ locationId +"' AND USER_LEVEL = '"+ accessType +"' AND USERID LIKE '"+userFilter+"%' ORDER BY USERID";
//Ended by subrahmanyam for Enhancement 154384  on 10/04/09      

		//Logger.info(FILE_NAME,"getUserIds(String locationId, String accessType, String filter)", "UserIds Query : "+USER_SELECT);
		try
		{
			makeConnection();
            stmtUser = connection.createStatement();

            rsUser = stmtUser.executeQuery(USER_SELECT);
            while(rsUser.next())
            {
 	            userList.add(rsUser.getString(1));
            }
			if(rsUser!=null)
				rsUser.close();
		}
		catch(SQLException sqle)
		{
			//Logger.error(FILE_NAME,"getUserIds(String locationId, String accessType, String filter)","naming Exception ", sqle);
      logger.error(FILE_NAME+" getUserIds(String locationId, String accessType, String filter)"+" naming Exception "+ sqle);
		}
		catch(DBSysException dbs)
		{
			//Logger.error(FILE_NAME,"DBSysException ", dbs);
      logger.error(FILE_NAME+" DBSysException "+ dbs);
			//throw new EJBException(dbs.getMessage());
            sessionContext.setRollbackOnly();
            throw FoursoftException.getException(dbs,FILE_NAME,"getUserIds()");
		}
		finally
		{
				ConnectionUtil.closeConnection(connection, stmtUser);
		}
		return userList;
	} // end of getUserIds(String locationId, String accessType, String filter)


	/**
	*  This is used to retrieve the UserIds
	*
	* @param accessType
	*
	* @return ArrayList	- list of userIds
	*/

	
	public static final String	LOCATION_USERS_QUERY	=	
				"SELECT USERID, USERNAME, USER_LEVEL FROM FS_USERMASTER WHERE LOCATIONID = ? AND USER_LEVEL != 'LICENSEE' ORDER BY USERID";


	public ArrayList getUserIds(String locationId) throws FoursoftException
	{
  	    ArrayList			userList	= new ArrayList(10);
        PreparedStatement	stmtUser	= null;
        ResultSet			rsUser		= null;

		try
		{
			makeConnection();
            stmtUser = connection.prepareStatement( LOCATION_USERS_QUERY );

			stmtUser.setString( 1, locationId );

            rsUser = stmtUser.executeQuery();
			
            while(rsUser.next())
            {
 	            userList.add( "User Id: "+rsUser.getString(1)+", User Name : "+rsUser.getString(2)+", User's Access Level :"+ rsUser.getString(3) );
            }
			if(rsUser!=null)
				rsUser.close();
		}
		catch(SQLException sqle)
		{
			//Logger.error(FILE_NAME, "getUserIds(String locationId)","SQL Exception ",sqle);
      logger.error(FILE_NAME+ " getUserIds(String locationId)"+" SQL Exception "+sqle);
			//throw new EJBException(sqle.getMessage());
            sessionContext.setRollbackOnly();
            throw FoursoftException.getException(sqle,FILE_NAME,"getUserIds");
		}
		catch(DBSysException dbs)
		{
			//Logger.error(FILE_NAME,"DBSysException ", dbs);
      logger.error(FILE_NAME+"DBSysException "+ dbs);
			//throw new EJBException(dbs.getMessage());
            sessionContext.setRollbackOnly();
            throw FoursoftException.getException(dbs,FILE_NAME,"getUserIds");
		}

		finally
		{
			ConnectionUtil.closeConnection(connection, stmtUser);
		}
		return userList;
	} // end of getUserIds(String locationId)
	/*
	* This is used to retrieve role Id along with roleLocation and description of the role
	* @return ArrayList - list of RoleMasterDOB's
	* @param String locationId
	* @param String access type
	* @param String roleModule
	*/
	public ArrayList getRoleIdLocDescription(String locationId, String roleAccessType, String roleModule) throws FoursoftException
	{
		/*
		String ROLEIDS = "SELECT ROLEID, LOCATIONID, DESCRIPTION FROM FS_ROLEMASTER "
			+"WHERE ROLELEVEL = '"+ roleAccessType +"' AND LOCATIONID = '"+locationId+"' AND ROLEMODULE = '"+roleModule+"' "
			+"UNION "
			+"SELECT ROLEID, LOCATIONID, DESCRIPTION FROM FS_ROLEMASTER "
			+"WHERE ROLELEVEL = '"+ roleAccessType +"' AND ROLETYPE= 'GENERIC' AND ROLEMODULE = '"+roleModule+"' ";
		*/
		String ROLEIDS = "SELECT ROLEID, LOCATIONID, DESCRIPTION FROM FS_ROLEMASTER "
			+"WHERE ROLELEVEL = '"+ roleAccessType +"' AND LOCATIONID = '"+locationId+"' "
			+"UNION "
			+"SELECT ROLEID, LOCATIONID, DESCRIPTION FROM FS_ROLEMASTER "
			+"WHERE ROLELEVEL = '"+ roleAccessType +"' AND ROLETYPE= 'GENERIC' ";

		Statement	stmt		= null;
		ResultSet	rs			= null;

		ArrayList	roleList	= new ArrayList();

		String roleId 			= "";
		String roleLocationId	= "";
		String description		= "";
		try
		{
			makeConnection();
			stmt	= connection.createStatement();
			rs = stmt.executeQuery(ROLEIDS);
			while(rs.next())
			{
				roleId			= rs.getString(1);
				roleLocationId	= rs.getString(2);
				description 	= rs.getString(3);
				roleList.add(new RoleModel(roleId, roleLocationId, description) );
			}
			if(rs!=null)
				rs.close();
		}
		catch(SQLException se)
		{
			//Logger.error(FILE_NAME, "getRoleIdLocDescription(String locationId, String roleAccessType, String roleModule)","SQL Exception ", se);
      logger.error(FILE_NAME+ "getRoleIdLocDescription(String locationId, String roleAccessType, String roleModule)"+"SQL Exception "+ se);
			//throw new EJBException("SQLException in retrieving the Roles. ");
            sessionContext.setRollbackOnly();
            throw FoursoftException.getException(se,FILE_NAME,"getRoleIdLocDescription");
		}
		catch(Exception e)
		{
			//Logger.error(FILE_NAME, "getRoleIdLocDescription(String locationId, String roleAccessType, String roleModule)","Exception ",e);
      logger.error(FILE_NAME+ "getRoleIdLocDescription(String locationId, String roleAccessType, String roleModule)"+"Exception "+e);
			//throw new EJBException("Exception in retrieving the Roles. ");
            sessionContext.setRollbackOnly();
            throw FoursoftException.getException(e,FILE_NAME,"getRoleIdLocDescription");
		}
		finally
		{
				ConnectionUtil.closeConnection(connection, stmt);
		}
		return roleList;
	} // end of getRoleIdLocDescription(String locationId, String roleAccessType, String roleModule)
	
	
	/**
	* Checks whether the UserLimit Exceeded or Not
	* It uses config file
	* It returns true if the User Limit is not Exceed the max allowed limit
	* It will consider Regular users only (Not the Users of the Customers)
	*
	* @return boolean
	*/
	public boolean isMaxLimitNotExceeded() throws FoursoftException
	{
		int maxAllowedUsers = 0;
		int currentUsers = 0;
		Statement stmt = null;
		ResultSet rs = null;
		try
		{
			maxAllowedUsers	= FoursoftConfig.MAX_USERS_LIMIT;
			makeConnection();
			stmt = connection.createStatement();
			rs = stmt.executeQuery("SELECT COUNT(*) FROM FS_USERMASTER WHERE USER_LEVEL NOT IN('ETCRM', 'ELCRM', 'ELVRM','ELTRM','EPCRM','EPVRM','EPTRM','ESCRM', 'ESVRM')");
			if(rs.next()) {
				currentUsers = rs.getInt(1);
			}
			if(rs!=null)
				rs.close();

			//Logger.info(FILE_NAME,"Number of Licensed Users = "+FoursoftConfig.MAX_USERS_LIMIT+", Number of Registered Users = "+currentUsers);
			
		}
		catch(DBSysException dbs)
		{
			//Logger.error(FILE_NAME,"DBSysException ", dbs);
      logger.error(FILE_NAME+"DBSysException "+ dbs);
			//throw new EJBException(dbs.getMessage());
            sessionContext.setRollbackOnly();
            throw FoursoftException.getException(dbs,FILE_NAME,"isMaxLimitNotExceeded");
		}
		catch(Exception ex)
		{
			//Logger.error(FILE_NAME, "isMaxLimitNotExceeded()","Max No Of user exceeded - Exception ", ex);
      logger.error(FILE_NAME+ "isMaxLimitNotExceeded()"+"Max No Of user exceeded - Exception "+ ex);
		}
		finally
		{
				ConnectionUtil.closeConnection(connection, stmt);
		}
		boolean flag = currentUsers < maxAllowedUsers;
		return flag;

	} // end of isMaxLimitNotExceeded()
	
	/**
	* This is used to Register the User
	* First it check whether the user Exists or not ? If not It will instantiate Entity to create User
	*
	* @param userId
	* @param username
	* @param locationId
	* @param password
	* @param empId
	* @param department
	* @param companyId
	* @param userLevel
	* @param roleId
	* @param roleLocationId
	*
	* @exception AlreadyExistsException
	*/
    public void registerUser(UserModel userModel, String createUserId, String createLocationId, String eMailNotification)  throws AlreadyExistsException, FoursoftException
	{
		try
		{
				makeConnection();
   logger.info("in bean");
     ArrayList			listValues				=	 null;	
      listValues=userModel.getRepOfficersCode2();
     // logger.info("listValues"+listValues.size());
      
			if( isUserExists(userModel.getUserId(), userModel.getLocationId() ))
				throw new AlreadyExistsException(" User is Already Exist : "+ userModel.getLocationId()+"/"+userModel.getUserId() );
        // added by VLAKSHMI for WPBN issue 167659 (CR) on 22/04/2009
			int listValSize	=	listValues.size();
         for (int i=0;i<listValSize;i++)
						{
            if(listValues.get(i)!=null && !"".equalsIgnoreCase(listValues.get(i).toString().trim())  )
            {
             userModel.setRepOfficersCode(listValues.get(i).toString());
           if(!isReportingOfficerValid(userModel.getRepOfficersCode(),userModel.getLocationId(),userModel.getAccessLevel(),userModel.getDesignationId()))
				 throw new AlreadyExistsException("The Reporting Officer's Code is not exit");  
            }
            }
            // end  for WPBN issue 167659 (CR) on 22/04/2009
      if(!isUserExists1(userModel.getDesignationId()))           
				throw new AlreadyExistsException("The Designation Id is not Exist");
         if( isUserExists(userModel.getEmpId()))
				throw new AlreadyExistsException("Employee Id is Already Exist");
          
       
       
		
            }
		catch(SQLException se)
		{
			//Logger.error(FILE_NAME, "registerUser(UserModel userModel, String createUserId, String createLocationId)","caught in exception in checking the User Exist  ",se);
      logger.error(FILE_NAME+ "registerUser(UserModel userModel, String createUserId, String createLocationId)"+"caught in exception in checking the User Exist  "+se);
		}
		catch(DBSysException dbs)
		{
			//Logger.error(FILE_NAME,"DBSysException ", dbs);
      logger.error(FILE_NAME+"DBSysException "+ dbs);
			//throw new EJBException(dbs.getMessage());
            sessionContext.setRollbackOnly();
            throw FoursoftException.getException(dbs,FILE_NAME,"registerUser");
		}
		finally
		{
			ConnectionUtil.closeConnection(connection);
		}

		//UserMaster		remoteUser	= null;
		UserMaster		user	= null;
		Mailer			mail   = null;
		
	    try
	    {
			String newpassword ="";
      //String mailpassword=eMailNotification;
			String mailpassword=getMailword(eMailNotification);
			if(userModel.getPassword().equals("A1MyGHTY")){
			newpassword =generatePassword();
			//userModel.setPassword(newpassword);
      userModel.setPassword(encryptSHA(newpassword));
			mailpassword=newpassword;
			}

			user		= (UserMaster) umHome.create(userModel);
      
			//if (eMailNotification != null && eMailNotification.equalsIgnoreCase("Y")) //removed optional feature and sending mail made mandatory
			//{
					mail = mailHome.create();
					EMailMessage message = new EMailMessage();
					
					
					StringBuffer	msge	=	new StringBuffer("New QUOTESHOP User Successfully Registered With Following Details:\n\n");
					msge.append("User Id\t\t: '"+ userModel.getUserId() +"'\n");
					msge.append("Location Id\t\t: '"+ userModel.getLocationId() +"'\n");
					msge.append("Password\t\t: '"+ mailpassword+"'\n");
					msge.append("\n\n"+"Thank you,\n");
					msge.append("Webmaster,\n");
					//msge.append(FoursoftConfig.LICENSEE_ID+".");
          msge.append("QUOTESHOP");
					msge.append("\n\n________________________________________________________");
					msge.append("\nIf you are not an intended recipient of this e-mail, please notify");
					msge.append("\nthe sender, delete it and do not read, act upon, print, disclose,");
					msge.append("copy, retain or redistribute it.");
					message.setMessage(msge.toString());
					message.setToAddress(userModel.getEMailId());
					message.setFromAddress("webmaster@dhl.com");
					message.setSubject("New QUOTESHOP User Created");
					mail.sendMail(message);
			 //}

			UserLogDAO userLogDAO = new UserLogDAO();

			Timestamp createTimestamp	= new Timestamp((new java.util.Date().getTime()) );
			UserLogVOB	userLogVOB	= new UserLogVOB(createLocationId, createUserId, "UserRegistration", userModel.getLocationId()+"."+userModel.getUserId(), createTimestamp, "ADD" );
			userLogDAO.setTransactionDetails(userLogVOB);

	    }
/**	    
        catch(DBSysException dbs)
	    {
		     Logger.error(FILE_NAME, "registerUser(UserModel userModel, String createUserId, String createLocationId)", "DBSysException ");
			 throw new EJBException("Unable to register user");
	    }	    
*/		catch(Exception ex)
	    {
		     //Logger.error(FILE_NAME, "registerUser(UserModel userModel, String createUserId, String createLocationId)","Exception ", ex);
         
         logger.error(FILE_NAME+ "registerUser(UserModel userModel, String createUserId, String createLocationId)"+"Exception "+ ex);
			 //throw new javax.ejb.EJBException( "Unable to register user" );
             sessionContext.setRollbackOnly();
             throw FoursoftException.getException(ex,FILE_NAME,"registerUser()");
	    }
	} // end of registerUser(UserModel userModel, String createUserId, String createLocationId)

	
	/**
	 * This is used to getting the uSer role information for View All
	 * Returns list of UserRoleDOB's
	 *
	 * @return ArrayList - return the UserRoleDOB list
	 *
	 */

	private static final String	ALL_USERS_QUERY =
							 "SELECT A.USERID, A.LOCATIONID, A.USERNAME, B.ROLELEVEL, B.ROLEID, B.LOCATIONID, B.DESCRIPTION, A.EMAILID, A.EMPID, A.DESIGNATION_ID, D.LEVEL_NO,A.PHONE_NO,A.FAX_NO,A.MOBILE_NO "
							+"FROM FS_USERMASTER A, FS_ROLEMASTER B, FS_USERROLES C,QMS_DESIGNATION D "
							+"WHERE A.USERID=C.USERID AND A.LOCATIONID=C.LOCATIONID AND "
							+"C.ROLEID=B.ROLEID AND C.ROLE_LOCATIONID=B.LOCATIONID AND A.DESIGNATION_ID=D.DESIGNATION_ID(+) ORDER BY A.LOCATIONID, A.USERID";//@@ Modified by Kameswari for the WPBN issue-61303

	private static final String	ALL_USERS_QUERY2 =
		 "SELECT A.USERID, A.LOCATIONID, A.USERNAME, B.ROLELEVEL, B.ROLEID, B.LOCATIONID, B.DESCRIPTION, A.EMAILID, A.EMPID, A.DESIGNATION_ID, D.LEVEL_NO,A.PHONE_NO,A.FAX_NO,A.MOBILE_NO "
		+"FROM FS_USERMASTER A, FS_ROLEMASTER B, FS_USERROLES C,QMS_DESIGNATION D "
		+"WHERE A.USERID=C.USERID AND A.LOCATIONID=C.LOCATIONID AND "
		+"C.ROLEID=B.ROLEID AND C.ROLE_LOCATIONID=B.LOCATIONID AND A.DESIGNATION_ID=D.DESIGNATION_ID(+) AND (SELECT DISTINCT COUNTRYID FROM FS_ADDRESS WHERE ADDRESSID IN(SELECT TM.CONTACTADDRESSID  FROM FS_FR_TERMINALMASTER TM WHERE TERMINALID =A.LOCATIONID))=(SELECT DISTINCT COUNTRYID FROM FS_ADDRESS WHERE ADDRESSID IN(SELECT TM.CONTACTADDRESSID  FROM FS_FR_TERMINALMASTER TM WHERE TERMINALID =?)) ORDER BY A.LOCATIONID, A.USERID";//@@ Modified by Kameswari for the WPBN issue-61303
	
	private static final String	ALL_USERS_QUERY_FILTER =
							 "SELECT A.USERID, A.LOCATIONID, A.USERNAME, B.ROLELEVEL, B.ROLEID, B.LOCATIONID, B.DESCRIPTION, A.EMAILID"
							+"FROM FS_USERMASTER A, FS_ROLEMASTER B, FS_USERROLES C "
							+"WHERE A.USERID=C.USERID AND A.LOCATIONID=C.LOCATIONID AND "
							+"C.ROLEID=B.ROLEID AND C.ROLE_LOCATIONID=B.LOCATIONID AND A.LOCATIONID = ? ORDER BY A.LOCATIONID, A.USERID";
              
	public ArrayList getUserViewAllInformation() throws FoursoftException
	{
		Statement	stmt		= null;
		ResultSet	rs			= null;

		ArrayList userInfoList	= new ArrayList();

		try
		{
			makeConnection();

			stmt = connection.createStatement();

			rs = stmt.executeQuery( ALL_USERS_QUERY );

			while ( rs.next() )
			{
        //@@Commented and Modified by Kameswari for the WPBN issue-61303
				//userInfoList.add( new UserRoleDOB(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6), rs.getString(7), rs.getString(8), rs.getString(9), rs.getString(10), rs.getString(11) );
        userInfoList.add( new UserRoleDOB(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6), rs.getString(7), rs.getString(8), rs.getString(9), rs.getString(10), rs.getString(11),rs.getString(12),rs.getString(13),rs.getString(14)) );
			 //@@WPBN issue-61303
      }
		}
		catch (SQLException sql)
		{
			//Logger.error(FILE_NAME, "getUserViewAllInformation()","SQL Exception ",sql);
      logger.error(FILE_NAME+ "getUserViewAllInformation()"+"SQL Exception "+sql);
		}
		catch(DBSysException dbs)
		{
			//Logger.error(FILE_NAME,"DBSysException ", dbs);
      logger.error(FILE_NAME+"DBSysException "+ dbs);
			EJBException ejbE = new EJBException("");
			Exception cause = ejbE.getCausedByException();
			//throw new EJBException(dbs.getMessage());
            sessionContext.setRollbackOnly();
            throw FoursoftException.getException(dbs,FILE_NAME,"getUserViewAllInformation()");
		}
		finally
		{
    //Modified By RajKumari on 23-10-2008 for Connection Leakages.
			//ConnectionUtil.closeConnection(connection, stmt);
      ConnectionUtil.closeConnection(connection, stmt, rs);
		}
		userInfoList.trimToSize();
		return userInfoList;
	} // end of getUserViewAllInformation()

	public ArrayList getUserViewAllInformation(String locationId) throws FoursoftException
	{
		PreparedStatement	pStmt		= null;
		ResultSet	rs			= null;

		ArrayList userInfoList	= new ArrayList();

		try
		{
			makeConnection();

			pStmt = connection.prepareStatement(ALL_USERS_QUERY_FILTER);
            pStmt.setString(1,locationId);
			rs		= pStmt.executeQuery();

			while ( rs.next() )
			{
					userInfoList.add( new UserRoleDOB(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6), rs.getString(7), rs.getString(8)) );
			}
		}
		catch (SQLException sql)
		{
			//Logger.error(FILE_NAME, "getUserViewAllInformation()","SQL Exception ",sql);
      logger.error(FILE_NAME+ " getUserViewAllInformation()"+"SQL Exception "+sql);
		}
		catch(DBSysException dbs)
		{
			//Logger.error(FILE_NAME,"DBSysException ", dbs);
      logger.error(FILE_NAME+"DBSysException "+ dbs);
			EJBException ejbE = new EJBException("");
			Exception cause = ejbE.getCausedByException();
			//throw new EJBException(dbs.getMessage());
            sessionContext.setRollbackOnly();
            throw FoursoftException.getException(dbs,FILE_NAME,"getUserViewAllInformation()");
		}
		finally
		{
    //Modified By RajKumari on 23-10-2008 for Connection Leakages.
			//ConnectionUtil.closeConnection(connection, pStmt);
      ConnectionUtil.closeConnection(connection, pStmt, rs);
		}
		userInfoList.trimToSize();
		return userInfoList;
	} // end of getUserViewAllInformation()
  /**
	* Checks whethe the user is exist or not
	*
	* @param userId
	* @param locationId
	*
	* @exception SQLException
	*
	* @return boolean - return true if user exist
	*/
/*	private boolean isUserExists11(String userId, String locationId) throws SQLException
	{
		boolean isValid = false;

		String isUserExists = "SELECT UM.EMPID FROM FS_USERMASTER UM,QMS_QUOTE_MASTER QM WHERE UM.EMPID=QM.SALES_PERSON AND UM.EMPID='"+userId+"' AND UM.LOCATIONID ='"+locationId+"'";
    
    
   // SELECT USERID FROM FS_USERMASTER "
		//						+"WHERE USERID = '"+ userId +"' AND LOCATIONID = '"+ locationId +"'";

		Statement	stmt = null;
		ResultSet	rsValidUser	= null;
		try
		{
			stmt	= connection.createStatement();
			rsValidUser		= stmt.executeQuery(isUserExists);
			if(rsValidUser.next() ) {
				isValid = true;
			}
			if(rsValidUser!=null)
				rsValidUser.close();
        
        System.out.println("dsdsdsdsdsdd isValidisValid ::: "+isValid);
		}
		finally
		{
			ConnectionUtil.closeStatement(stmt);
		}
		return isValid;
	}*/
	/**
	* Checks whethe the user is exist or not
	*
	* @param userId
	* @param locationId
	*
	* @exception SQLException
	*
	* @return boolean - return true if user exist
	*/
	private boolean isUserExists(String userId, String locationId) throws SQLException
	{
		boolean isValid = false;

		String isUserExists = "SELECT USERID FROM FS_USERMASTER "
								+"WHERE USERID = '"+ userId +"' AND LOCATIONID = '"+ locationId +"'";

		Statement	stmt = null;
		ResultSet	rsValidUser	= null;
		try
		{
			stmt	= connection.createStatement();
			rsValidUser		= stmt.executeQuery(isUserExists);
			if(rsValidUser.next() ) {
				isValid = true;
			}
			if(rsValidUser!=null)
				rsValidUser.close();
		}
		finally
		{
			ConnectionUtil.closeStatement(stmt);
		}
		return isValid;
	}
  
  private boolean isUserExists(String empId) throws SQLException
	{
		boolean isValid = false;

		String isUserExists = "SELECT EMPID FROM FS_USERMASTER "
								+"WHERE EMPID = '"+empId+"'";

		Statement	stmt = null;
		ResultSet	rsValidUser	= null;
		try
		{
			stmt	= connection.createStatement();
			rsValidUser		= stmt.executeQuery(isUserExists);
			if(rsValidUser.next() ) {
				isValid = true;
			}
			if(rsValidUser!=null)
				rsValidUser.close();
		}
		finally
		{
			ConnectionUtil.closeStatement(stmt);
		}
		return isValid;
	}
	private boolean isUserExists1(String empId) throws SQLException
	{
		boolean isValid = false;

		String isUserExists = "SELECT DESIGNATION_ID  FROM QMS_DESIGNATION "
								+"WHERE DESIGNATION_ID  = '"+empId+"'";

		Statement	stmt = null;
		ResultSet	rsValidUser	= null;
		try
		{
			stmt	= connection.createStatement();
			rsValidUser		= stmt.executeQuery(isUserExists);
			if(rsValidUser.next() ) {
				isValid = true;
			}
			if(rsValidUser!=null)
				rsValidUser.close();
		}
		finally
		{
			ConnectionUtil.closeStatement(stmt);
		}
		return isValid;
	}
	private boolean isReportingOfficerValid(String repOfficerId,String terminalId,String accessLevel,String designationId) throws SQLException
  {
    StringBuffer terminalQuery =  new StringBuffer("");
    String       isUserExists  =  "";
    Statement	   stmt          = null;
		ResultSet	   rsValidUser	 = null;
    boolean      isValid       = false;
    try
    {
      /*if("HO_TERMINAL".equalsIgnoreCase(accessLevel))
        terminalQuery.append(" SELECT TERMINALID FROM FS_FR_TERMINALMASTER ");
      else if("LICENSEE".equalsIgnoreCase(accessLevel))
        terminalQuery.append(" SELECT LOCATIONID FROM FS_USERMASTER WHERE USER_LEVEL='LICENSEE' ");
      else
      {
        terminalQuery.append(" SELECT PARENT_TERMINAL_ID TERM_ID FROM FS_FR_TERMINAL_REGN CONNECT BY CHILD_TERMINAL_ID = PRIOR PARENT_TERMINAL_ID START WITH CHILD_TERMINAL_ID = '")
                   .append(terminalId).append("'")
                   .append( " UNION ")
                   .append( " SELECT TERMINALID TERM_ID FROM FS_FR_TERMINALMASTER WHERE OPER_ADMIN_FLAG = 'H' ")
                   .append( " UNION ")
                   .append( " SELECT '").append(terminalId).append("' TERM_ID FROM DUAL ")
                   .append( " UNION ")
                   .append( " SELECT CHILD_TERMINAL_ID TERM_ID FROM FS_FR_TERMINAL_REGN CONNECT BY PRIOR CHILD_TERMINAL_ID = PARENT_TERMINAL_ID START WITH PARENT_TERMINAL_ID = '")
                   .append(terminalId).append("'");
      }*/
      
     /* isUserExists  =" SELECT EMPID FROM FS_USERMASTER WHERE EMPID = '"+repOfficerId+"' AND DESIGNATION_ID IN (SELECT DESIGNATION_ID FROM QMS_DESIGNATION WHERE TO_NUMBER(LEVEL_NO)<=(SELECT TO_NUMBER(LEVEL_NO) FROM QMS_DESIGNATION WHERE DESIGNATION_ID='"+designationId+"' ))"+
                    " AND LOCATIONID IN ("+terminalQuery+")";*/
        isUserExists  =" SELECT EMPID FROM FS_USERMASTER WHERE EMPID = '"+repOfficerId+"' AND DESIGNATION_ID IN (SELECT DESIGNATION_ID FROM QMS_DESIGNATION WHERE TO_NUMBER(LEVEL_NO)<=(SELECT TO_NUMBER(LEVEL_NO) FROM QMS_DESIGNATION WHERE DESIGNATION_ID='"+designationId+"' ))";
       //@@Modified by kameswari for the issue                          
                    
      stmt        =   connection.createStatement();
      rsValidUser =   stmt.executeQuery(isUserExists);
      
      if(rsValidUser.next())
        isValid = true;
    }
    catch(Exception e)
    {
      //Logger.error(FILE_NAME,"Exception while validating Reporting Officer: "+e.toString());
      logger.error(FILE_NAME+"Exception while validating Reporting Officer: "+e.toString());
      e.printStackTrace();
      throw new SQLException(e.toString());
    }
    finally
    {
      ConnectionUtil.closeConnection(null,stmt,rsValidUser);
    }
    return isValid;
  }
	/**
	* This is used to Update the user Information including the user's Role
	* It user entity bean to update the data
	*
	* @param userId
	* @param locationId
	* @param userName
	* @param empId
	* @param department
	* @param roleId
	* @param roleLocationId
	*
	* @exception NotExistsException
	* @see		 UserMasterEntityBean
	*
	*/
	public void updateUserInformation(UserModel userModel, String createUserId, String createLocationId)  throws NotExistsException,AlreadyExistsException, FoursoftException, DataIntegrityViolationException
	{
		//UserMaster		remote	= null;
		UserMaster		user	= null;
		UserMasterBeanPK	pkObj	= null;
        
		pkObj	= new UserMasterBeanPK();
		pkObj.userId		= userModel.getUserId();
		pkObj.locationId    = userModel.getLocationId();

		boolean	isRoleAvailable		= false;       

		try
	    {
			makeConnection();
			// check wheather the assosiated role is there or not
       ArrayList			listValuesRef				=	 null;	
      listValuesRef=userModel.getRepOfficersCode2();
     
        if(! isRoleExists(userModel.getRoleId(), userModel.getRoleLocationId() ) )
          throw new NotExistsException("1 ("+userModel.getRoleLocationId()+"/"+userModel.getRoleId()+ ") is not Available ");
       // if( !isUserExists(userModel.getEmpId()))
       //   throw new NotExistsException("Employee Id is not Exist");
       // added by VLAKSHMI for WPBN issue 167659 (CR) on 22/04/2009
        int listValRefSize	=	listValuesRef.size();
         for (int i=0;i<listValRefSize;i++)
						{
            if(listValuesRef.get(i)!=null && !"".equalsIgnoreCase(listValuesRef.get(i).toString().trim())  )
            {
           
            userModel.setRepOfficersCode(listValuesRef.get(i).toString());
       
        if(!isReportingOfficerValid(userModel.getRepOfficersCode(),userModel.getLocationId(),userModel.getAccessLevel(),userModel.getDesignationId()))
          throw new NotExistsException("The Reporting Officer's Code"+userModel.getRepOfficersCode()+" dooes not exist");
            }
            } 
            //end  for WPBN issue 167659 (CR) on 22/04/2009
        if(!isUserExists1(userModel.getDesignationId()))           
          throw new NotExistsException("The Designation Id is not Exist");
            //initializeEjbHomes();
		    //remote		= (UserMaster) umHome.findByPrimaryKey(pkObj);
			user		= (UserMaster) umHome.findByPrimaryKey(pkObj);
			//remote.setUserModel(userModel);
			userModel.setBackPassword("DO NOT UPDATE"); // This line is added to avoide updation of password when updating other information.
			user.setUserModel(userModel);

			UserLogDAO userLogDAO = new UserLogDAO();

			Timestamp createTimestamp	= new Timestamp((new java.util.Date().getTime()) );
			userLogDAO.setTransactionDetails( new UserLogVOB(createLocationId, createUserId, "UserRegistration", userModel.getLocationId()+"."+userModel.getUserId(), createTimestamp, "MODIFY" ));

	    }
		catch(NotExistsException ne)
		{
			//Logger.warning(FILE_NAME, "updateUserInformation(UserModel userModel, String createUserId, String createLocationId)","Role is Not available  ");
      logger.warn(FILE_NAME+ "updateUserInformation(UserModel userModel, String createUserId, String createLocationId)"+"Role is Not available  ");
			throw ne;
		}
    
		catch(javax.ejb.ObjectNotFoundException oe)
		{
			//Logger.warning(FILE_NAME, "updateUserInformation(UserModel userModel String createUserId, String createLocationId)","Object Not found exception  ",oe);
      logger.warn(FILE_NAME+ " updateUserInformation(UserModel userModel String createUserId, String createLocationId)"+"Object Not found exception  "+oe);
			throw new NotExistsException("User "+ userModel.getUserId() +"/"+ userModel.getLocationId() +" is not Exist ");
		}
		catch(DataIntegrityViolationException dExc)
		{
			//Logger.warning(FILE_NAME, "updateUserInformation(UserModel userModel String createUserId, String createLocationId)","StaleData Error ",dExc);
      logger.warn(FILE_NAME+ "updateUserInformation(UserModel userModel String createUserId, String createLocationId)"+"StaleData Error ",dExc);
			throw dExc;
		}
	    catch(Exception ex)
	    {
			//Logger.error(FILE_NAME, "updateUserInformation(UserModel userModel String createUserId, String createLocationId)"," Exception  ",ex);
      logger.error(FILE_NAME+ "updateUserInformation(UserModel userModel String createUserId, String createLocationId)"+" Exception  "+ex);
			//throw new javax.ejb.EJBException("Error in  updateUserInformation" );
            sessionContext.setRollbackOnly();
            throw FoursoftException.getException(ex,FILE_NAME,"updateUserInformation");
	    }
		finally
		{
			ConnectionUtil.closeConnection(connection);
		}
	} // end of updateUserInformation(String userId, String locationId, String userName, String empId, String department, String roleId, String roleLocationId)
	
	/**
	* Registering the role by using entity Bean
	* First whether Role Exist or Not
	*
	* @param roleMasterDOB	- roleAttributes
	* @param rolePrevilleges - list of privilleges
	*
	* @exception AlreadyExistsException	- throws AlreadyExistsException, if the role already exists
	*
	*/
    public void registerRole(RoleModel roleModel,String createUserId, String createLocationId) throws AlreadyExistsException, FoursoftException
    {

		try
		{
			makeConnection();
			if( isRoleExists(roleModel.getRoleId(), roleModel.getLocationId()) )
				throw new AlreadyExistsException("Error: The Role '"+roleModel.getRoleId()+"' already exists for '"+roleModel.getLocationId()+"'.");
		}
		catch(DBSysException se)
		{
			//Logger.error(FILE_NAME, "registerRole(RoleModel roleModel,String createUserId, String createLocationId)","caught in exception in checking the Role Exist  ", se);
      logger.error(FILE_NAME+ "registerRole(RoleModel roleModel,String createUserId, String createLocationId)"+"caught in exception in checking the Role Exist  "+ se);
      se.printStackTrace();
		}
        catch(SQLException se)
		{
			//Logger.error(FILE_NAME, "registerRole(RoleModel roleModel,String createUserId, String createLocationId)","caught in exception in checking the Role Exist  ", se);
      logger.error(FILE_NAME+ "registerRole(RoleModel roleModel,String createUserId, String createLocationId)"+"caught in exception in checking the Role Exist  "+ se);
      se.printStackTrace();
            throw FoursoftException.getException(se,FILE_NAME,"registerRole()");
		}
		finally
		{
			ConnectionUtil.closeConnection(connection);
		}

		//RoleMaster		remote	= null;
		RoleMaster		role	= null;

	    try
	    {
			//initializeEjbHomes();
		    //remote		= (RoleMaster) rmHome.create(roleModel);
			role		= (RoleMaster) rmHome.create(roleModel);

			UserLogDAO userLogDAO = new UserLogDAO();

			Timestamp createTimestamp	= new Timestamp((new java.util.Date().getTime()) );
			UserLogVOB	userLogVOB	= new UserLogVOB(createLocationId, createUserId, "RoleRegistration", roleModel.getLocationId()+"."+roleModel.getRoleId(), createTimestamp, "ADD" );
			userLogDAO.setTransactionDetails(userLogVOB);
	    }
	    catch(Exception ex)
	    {
			//Logger.error(FILE_NAME, "registerRole(RoleModel roleModel,String createUserId, String createLocationId)","Exception ",ex);
      logger.error(FILE_NAME+ "registerRole(RoleModel roleModel,String createUserId, String createLocationId)"+"Exception "+ex);
      ex.printStackTrace();
            //throw new EJBException("Unable to register the Role ");
            sessionContext.setRollbackOnly();
            throw FoursoftException.getException(ex,FILE_NAME,"registerRole");
	    }
    } // end of registerRole(RoleMasterDOB roleMasterDOB, ArrayList allPermissions)
	
	/**
	* checks for whether the Role exist or not
	* @param roleId
	* @param locationId
	*
	* @exception SQLException
	*
	* @return boolean - returns true if role Exists
	*/
	private boolean isRoleExists(String roleId, String locationId) throws SQLException
	{
		boolean isValid = false;

		String ROLE_EXISTS = "SELECT ROLEID FROM FS_ROLEMASTER WHERE ROLEID= ? AND LOCATIONID = ?";

		//Logger.info(FILE_NAME, "isRoleExists(String roleId, String locationId, Connection connection )","Role Check SQL : "+ROLE_EXISTS);
		PreparedStatement	stmt = null;
		ResultSet	rsValid	= null;
		try
		{
			stmt	= connection.prepareStatement(ROLE_EXISTS);
			stmt.setString(1,roleId);
			stmt.setString(2,locationId);

			rsValid		= stmt.executeQuery();

			if(rsValid.next() )
			{
				//Logger.info(FILE_NAME,"isRoleExists(String roleId, String locationId )","Record Already Exists : "+roleId+" : "+locationId);
				isValid = true;
			}
		}
		finally
		{
			if(rsValid!=null)
				rsValid.close();
				
			ConnectionUtil.closeStatement(stmt);
		}
		return isValid;
	} // end of isRoleExists(String roleId, String locationId)

   /**
	* This is used to modify the password of the user
	*
	* @param userId
	* @param locationId
	* @param oldPassword
	* @param newPassword
	*
	* @return int - returns the status based on the oldPassword correct and success of Password updation
	*/
	public int changePassword(String userId, String locationId, String oldPassword, String newPassword, String emailPassword) throws FoursoftException
    {
		//UserMaster		remote	= null;
		UserMaster		user	= null;
		UserMasterBeanPK	pkObj	= null;
		
		pkObj	= new UserMasterBeanPK();
		pkObj.userId		= userId;
		pkObj.locationId	= locationId;

		int    STATUS = 0;

		PreparedStatement pStmt = null;
        ResultSet rs = null;
        String password = null;

		try
	    {
			makeConnection();
			
			//initializeEjbHomes();
		    //remote		= (UserMaster) umHome.findByPrimaryKey(pkObj);
			user		= (UserMaster) umHome.findByPrimaryKey(pkObj);
			 
			//UserModel userModel = remote.getUserModel();
			UserModel userModel = user.getUserModel();

			
			if(userModel.getPassword().equals(oldPassword) )
			{
				/*
         * Modified by Yuvraj for Password Config issue. Check for the no of previous passwords as configured in FS_PASSWORDCONFIGURATION
         * */
        String passwordHstryQry = "SELECT PASSWORD FROM FS_PREVIOUSPASSWORDHISTORY WHERE USERID = ? AND LOCATIONID = ? AND PASSWORD = ? AND PASSWORDDATE IN "+
                                  "(SELECT PASSWORDDATE FROM (SELECT T1.PASSWORDDATE, ROWNUM RN FROM (SELECT PASSWORDDATE FROM FS_PREVIOUSPASSWORDHISTORY "+
                                  "WHERE USERID = ? AND LOCATIONID = ? ORDER BY PASSWORDDATE DESC) T1 WHERE ROWNUM <= (SELECT PREVIOUS_PWDS + 1 FROM FS_PASSWORDCONFIGURATION)))";				
				
        //pStmt  = connection.prepareStatement("SELECT PASSWORD FROM FS_PREVIOUSPASSWORDHISTORY WHERE USERID = ? AND LOCATIONID = ? AND PASSWORD=?");
        pStmt  = connection.prepareStatement(passwordHstryQry);
        //@@Yuvraj
				pStmt.setString(1, userModel.getUserId());
	      pStmt.setString(2, userModel.getLocationId());
				pStmt.setString(3, newPassword);
        //@@Added by Yuvraj
        pStmt.setString(4,userModel.getUserId());
        pStmt.setString(5,userModel.getLocationId());
        //@@Yuvraj
		        rs = pStmt.executeQuery();
			    if (rs.next()){
					STATUS=1;
				rs.close();
				}
				else
				{
					
				userModel.setPassword(newPassword);
        //Added By RajKumari on 23-10-2008 for Connection Leakages.
			  if(pStmt!=null)
        pStmt.close();
        
				pStmt  = connection.prepareStatement("SELECT PASSWORD FROM FS_USERMASTER WHERE USERID = ? AND LOCATIONID = ?");
				pStmt.setString(1, userModel.getUserId());
	            pStmt.setString(2, userModel.getLocationId());
		        rs = pStmt.executeQuery();
			    if (rs.next())
				    userModel.setDbPassword(rs.getString(1));
					
					
	            //System.out.println("userModel.getDbPassword() = "+userModel.getDbPassword());
		        //System.out.println("userModel.getUserId() = "+userModel.getUserId());
			    //System.out.println("userModel.getLocationId() = "+userModel.getLocationId());

				//remote.setUserModel(userModel);
				//System.out.println("In changePassword user password = "+userModel.getPassword());
                user.setUserModel(userModel);            				
				STATUS  = 2;
				
        //sendEMail(userId,locationId,emailPassword,userModel.getEMailId(),"Change Password");
				sendEMail(userId,locationId,getMailword(emailPassword),userModel.getEMailId(),"Change Password");
				}
				
				if(STATUS == 1)
				{
					//Logger.error(FILE_NAME,"changePassword(String userId, String locationId, String oldPassword, String newPassword)","Previous Password is not allowed");
          logger.error(FILE_NAME+"changePassword(String userId, String locationId, String oldPassword, String newPassword)"+"Previous Password is not allowed");
				}
				else if(STATUS == 2)
				{
					//Logger.info(FILE_NAME,"changePassword(String userId, String locationId, String oldPassword, String newPassword)","Password Successfully Changed.");
          logger.info(FILE_NAME+" changePassword(String userId, String locationId, String oldPassword, String newPassword)"+"Password Successfully Changed.");
				}
			}
			
	    }
	    catch(Exception ex)
	    {
			//Logger.error(FILE_NAME,"changePassword(String userId, String locationId, String oldPassword, String newPassword)","Exception ",ex);
      logger.error(FILE_NAME+" changePassword(String userId, String locationId, String oldPassword, String newPassword)"+"Exception "+ex);
			//throw new javax.ejb.EJBException("Error occured in changing Password ");
            sessionContext.setRollbackOnly();
            throw FoursoftException.getException(ex,FILE_NAME,"changePassword");
	    }
		finally
		{
            ConnectionUtil.closePreparedStatement(pStmt,rs);
			ConnectionUtil.closeConnection(connection);
		}
		return STATUS;
    } // end of changePassword(String userId, String locationId, String oldPassword, String newPassword)
	
	/**
	* This method checks the user password history
	* @param loactionId
	* @param userId
	* @param password
	* @return UserInfo
	**/
	/*private checkPasswordHistory(String userId, String locationId, String newPassword)
	{
		PreparedStatement psmt= null;
		int status=0;

	pStmt  = connection.prepareStatement("SELECT PASSWORD FROM FS_PREVIOUSPASSWORDHISTORY WHERE USERID = ? AND LOCATIONID = ? AND PASSWORD=?");
				pStmt.setString(1, userId);
	            pStmt.setString(2, locationId);
				pStmt.setString(3, newPassword);
		        rs = pStmt.executeQuery();
			    
				if (rs.next())
				return status;
				else{
				
				}
	}
	*/
	/**
	* This is used to get the user information
	* Which includes UserName, empId, Department, Role (and role's Location)
	*
	* @param loactionId
	* @param userId
	*
	* @return UserInfo
	*
	*/
	public UserModel getUserInformation(String locationId, String userId ) throws NotExistsException, FoursoftException
	{
		//UserMaster		remote	= null;
		UserMaster		user	= null;
		UserMasterBeanPK	pkObj		= null;
		UserModel			 a			= null;
		boolean	successFlag	= false;

		pkObj	= new UserMasterBeanPK();
		pkObj.userId		= userId;
		pkObj.locationId	= locationId;
		
		try
	    {
			//initializeEjbHomes();
		    //remote		= (UserMaster) umHome.findByPrimaryKey(pkObj);
       
   			user		= (UserMaster) umHome.findByPrimaryKey(pkObj);
			a  = user.getUserModel();      
      
      	
	    }
		//catch(FinderException fe)
		  //{
			 // Logger.error(FILE_NAME, "getUserInformation(String locationId, String userId)","ObjectNot Found Exception ",fe);
			 // throw FoursoftException.getException(fe,FILE_NAME,"getUserInformation");
		//  }
	    catch(Exception ex)
	    { 
			  //Logger.error(FILE_NAME,"getUserInformation(String locationId, String userId)","ObjectNot Found Exception :(",ex);
        logger.error(FILE_NAME+" getUserInformation(String locationId, String userId)"+"ObjectNot Found Exception :("+ex);
			  //sessionContext.setRollbackOnly();
			throw FoursoftException.getException(ex,FILE_NAME,"getUserInformation","Uable to find the userId and LocationId combination details for user Id:"+userId,NotExistsException.ERRORCODE);
			//throw FoursoftException.getException(ex,FILE_NAME,"getUserInformation");
	    }
		return a;
		
	} // end of getUserInformation(String locationId, String userId )	
		
	
	/*
	* getting the transaction Ids for specified modules
	*
	* @param	module
	* @return	Hashtable - (key : value) -- (TxId:description)
	*/
    public Hashtable getTransactionIdAndDescription(String module)
    {

		String		TX_SQL = "SELECT TXID, DESCRIPTION FROM FS_TXMASTER  WHERE MODULE = '"+module+"' ORDER BY TXID";

		Statement		stmt	= null;
		ResultSet		rs		= null;
		Hashtable		txList		= new Hashtable();
		try
	  	{
        	makeConnection();
			stmt	= connection.createStatement();
			rs = stmt.executeQuery(TX_SQL);
			while(rs.next())
			{
				txList.put(rs.getString(1), rs.getString(2));
			}
			if(rs!=null)
				rs.close();
		}
		catch(SQLException se)
		{
			//Logger.error(FILE_NAME,"getTransactionIdAndDescription(String module)","SQL Exception ",se);
      logger.error(FILE_NAME+"getTransactionIdAndDescription(String module)"+"SQL Exception "+se);
		}
		catch(Exception e)
		{
			//Logger.error(FILE_NAME,"getTransactionIdAndDescription(String module)"," Exception ",e);
      logger.error(FILE_NAME+"getTransactionIdAndDescription(String module)"+" Exception "+e);
		}
		finally
		{
			ConnectionUtil.closeConnection(connection, stmt);
		}
        return txList;
    } // end of getTransactionIdAndDescription(String module)
	
	
	/**
	* Retrieving TransactionId and Description
	*
	* @return Hashtable - (key : value) -- (TxId:description)
	*/

	private static final String	TXID_SQL =
							"SELECT TXID, DESCRIPTION FROM FS_TXMASTER ORDER BY TXID";
	
    public Hashtable getTransactionIdAndDescription()
    {
		Statement		stmt	= null;
		ResultSet		rs		= null;
		Hashtable		txList		= new Hashtable();
		
		try
	  	{
        	makeConnection();

			stmt	= connection.createStatement();
			rs = stmt.executeQuery( TXID_SQL );
			
			while(rs.next())
			{
				txList.put(rs.getString(1), rs.getString(2));
			}
			if(rs!=null)
				rs.close();
		}
		catch(SQLException se)
		{
			//Logger.error(FILE_NAME,"getTransactionIdAndDescription()","SQL Exception  ",se);
      logger.error(FILE_NAME+"getTransactionIdAndDescription()"+"SQL Exception  "+se);
		}
		catch(Exception e)
		{
			//Logger.error(FILE_NAME,"getTransactionIdAndDescription()","Exception ",e);		
      logger.error(FILE_NAME+"getTransactionIdAndDescription()"+"Exception "+e);		
		}
		finally
		{
			ConnectionUtil.closeConnection(connection, stmt);
		}
        return txList;
    } // end of getTransactionIdAndDescription()


	public void ejbCreate()
		throws CreateException
	{
		//System.out.println("FROM ejbCreate of '"+this.getClass().getName()+"'");

		//System.out.println("FROM ejbCreate rmHome '"+rmHome+"'");
		//System.out.println("FROM ejbCreate umHome '"+umHome+"'");
	}
	

	public UserRoleRegistrationSessionBean() {
		//System.out.println("FROM Constructor of '"+this.getClass().getName()+"'");
    logger  = Logger.getLogger(UserRoleRegistrationSessionBean.class);
	}
	
	//Implementing SessionBeanInterface methods.....//
	public void setSessionContext(SessionContext sessionCtx)
		throws javax.ejb.EJBException
	{
		//System.out.println("FROM setSessionContext of '"+this.getClass().getName()+"'");

		this.sessionContext = sessionCtx;
        try 
		{
			userRoleDAO	=	new UserRoleDAO();
			lookUpBean	=	new LookUpBean();

          //  ic = new InitialContext();
			
            datasource  =	lookUpBean.lookupDataSource();
			//rmHome		=	(RoleMasterHome) lookUpBean.lookupEjbHome("RoleMasterBean");
			//umHome		=	(UserMasterHome) lookUpBean.lookupEjbHome("UserMasterBean");

			rmHome		=	(RoleMasterHome) getRoleMasterHome(); //lookUpBean.lookupEjbHome("java:comp/env/ejb/RoleMaster");
			umHome		=	(UserMasterHome) getUserMasterHome(); //lookUpBean.lookupEjbHome("java:comp/env/ejb/UserMaster");
            mailHome   =   (MailerHome) getMailerHome();
			//System.out.println("FROM setSessionContext rmHome '"+rmHome+"'");
			//System.out.println("FROM setSessionContext umHome '"+umHome+"'");

			
			//initializeEjbHomes();
        } 
		catch (NamingException ne) 
		{
            throw new EJBException("Naming Exception while looking up Data Source "+FoursoftConfig.DATA_SOURCE+"\n"+ 
										ne.getMessage());
        }
	}

	public void ejbRemove()
		throws javax.ejb.EJBException
	{
	}
	
	public void ejbActivate()
		throws javax.ejb.EJBException
	{
	}
	
	public void ejbPassivate()
		throws javax.ejb.EJBException
	{
	}
	
	//End of Implementation of SessionBeanInterface methods.

	//Serialization of BeanObject is to be done here.....//
	private void writeObject(java.io.ObjectOutputStream out)
		throws java.io.IOException
	{
		//write non-serializable attributes here

		out.defaultWriteObject();
	}

	private void readObject(java.io.ObjectInputStream in)
		throws java.io.IOException, ClassNotFoundException
	{
		//read non-serializable attributes here

		in.defaultReadObject();
	}
	// serialization done //

//-------------------------------------------//	
// user log methos will starts here
//------------------------------------------//
	/**
	* Retrieving User usage activities Dat wise
	*
	* @param locationId
	* @param userId
	* @param fromDate
	* @param toDate
	*
	* @return ArrayList	- list of user log events
	*/

	private static final String	USERLOG_QUERY =
									"SELECT DOCTYPE, DOCREFNO, DOCDATE, TRANSACTIONTYPE "+
									"FROM FS_USERLOG WHERE "+
									"LOCATIONID = ? AND USERID = ? AND DOCDATE BETWEEN ? AND ?";
	
	public ArrayList getUserLogDetails(String locationId, String userId, Timestamp fromDate, Timestamp toDate) throws FoursoftException
	{
  	    ArrayList			userLogList		= new ArrayList();
        PreparedStatement	pstmt			= null;
        ResultSet			rs				= null;

		//Logger.info(FILE_NAME,"getUserLogDetails(String locationId, String userId, Timestamp fromDate, Timestamp toDate)","LOG SQL : "+USERLOG_QUERY);
		
		try
		{
			makeConnection();
			
            pstmt = connection.prepareStatement(USERLOG_QUERY);
			
			pstmt.setString(1, locationId);
			pstmt.setString(2, userId);
			pstmt.setTimestamp(3, fromDate);
			pstmt.setTimestamp(4, toDate);

            rs	= pstmt.executeQuery();

			UserLogVOB	userLogVOB	= null;

            while(rs.next())
            {
				userLogVOB	= new UserLogVOB();
				userLogVOB.documentType = rs.getString("DOCTYPE");
				userLogVOB.documentRefNo = rs.getString("DOCREFNO");
				userLogVOB.documentDate = rs.getTimestamp("DOCDATE");
				userLogVOB.transactionType = rs.getString("TRANSACTIONTYPE");
				
				userLogList.add(userLogVOB);

            }
			if(rs!=null)
				rs.close();

		}
		catch(SQLException sqle)
		{
			//Logger.error(FILE_NAME,"getUserLogDetails(String locationId, String userId, Timestamp fromDate, Timestamp toDate)","SQL Exception ",sqle);
      logger.error(FILE_NAME+" getUserLogDetails(String locationId, String userId, Timestamp fromDate, Timestamp toDate)"+"SQL Exception "+sqle);
		}
		catch(DBSysException dbs)
		{
			//Logger.error(FILE_NAME,"DBSysException ", dbs);
      logger.error(FILE_NAME+"DBSysException "+ dbs);
			//throw new EJBException(dbs.getMessage());
            sessionContext.setRollbackOnly();
            throw FoursoftException.getException(dbs,FILE_NAME,"getUserLogDetails");
		}
		catch(Exception exp)
        {
            //Logger.error(FILE_NAME,"Error in getUserLogDetails",exp);
            logger.error(FILE_NAME+"Error in getUserLogDetails"+exp);
            throw FoursoftException.getException(exp,FILE_NAME,"getUserLogDetails");
			
        }   
		finally
		{
				ConnectionUtil.closeConnection(connection, pstmt);
		}
		return userLogList;
	} //	end of getUserLogDetails(String locationId, String userId, Timestamp fromDate, Timestamp toDate)


	/**
	* Retrieving User usage activities Dat wise
	*
	* @param locationId
	* @param userId
	* @param fromDate
	* @param toDate
	*
	* @return ArrayList	- list of user log events
	*/

	private static final String	USERLOG_ALLUSERS_QUERY =
									"SELECT USERID, DOCTYPE, DOCREFNO, DOCDATE, TRANSACTIONTYPE "+
									"FROM FS_USERLOG WHERE "+
									"LOCATIONID = ?  AND DOCDATE BETWEEN ? AND ? ORDER BY DOCDATE";
				
	public ArrayList getUserLogViewAllDetails(String locationId, Timestamp fromDate, Timestamp toDate) throws FoursoftException
	{
  	    ArrayList			userLogList		= new ArrayList();
        PreparedStatement	pstmt			= null;
        ResultSet			rs				= null;

		//Logger.info(FILE_NAME,"getUserLogViewAllDetails(String locationId,  Timestamp fromDate, Timestamp toDate)","LOG SQL : "+USERLOG_ALLUSERS_QUERY);
		try
		{
			makeConnection();

            pstmt = connection.prepareStatement( USERLOG_ALLUSERS_QUERY );

			pstmt.setString(1, locationId);
			pstmt.setTimestamp(2, fromDate);
			pstmt.setTimestamp(3, toDate);

            rs	= pstmt.executeQuery();

			UserLogVOB	userLogVOB	= null;
			
            while(rs.next())
            {
				userLogVOB	= new UserLogVOB();
				userLogVOB.userId			= rs.getString("USERID");
				userLogVOB.documentType 	= rs.getString("DOCTYPE");
				userLogVOB.documentRefNo 	= rs.getString("DOCREFNO");
				userLogVOB.documentDate 	= rs.getTimestamp("DOCDATE");
				userLogVOB.transactionType 	= rs.getString("TRANSACTIONTYPE");
				
				userLogList.add(userLogVOB);
            }
			if(rs!=null)
				rs.close();

		}
		catch(SQLException sqle)
		{
			//Logger.error(FILE_NAME,"getUserLogViewAllDetails(String locationId, String userId, Timestamp fromDate, Timestamp toDate)","SQL Exception ",sqle);
      logger.error(FILE_NAME+"getUserLogViewAllDetails(String locationId, String userId, Timestamp fromDate, Timestamp toDate)"+"SQL Exception "+sqle);
		}
		catch(DBSysException dbs)
		{
			//Logger.error(FILE_NAME,"DBSysException ", dbs);
      logger.error(FILE_NAME+"DBSysException "+ dbs);
			//throw new EJBException(dbs.getMessage());
            sessionContext.setRollbackOnly();
            throw FoursoftException.getException(dbs,FILE_NAME,"getUserLogViewAllDetails");
		}
		finally
		{
			ConnectionUtil.closeConnection(connection, pstmt);
		}
		return userLogList;
	} //	end of getUserLogViewAllDetails(String locationId, String userId, Timestamp fromDate, Timestamp toDate)


	// Utility methods starts here.


	/**
	* This is a private method which is used to get the connection from the Connection pool
	*
	* @throws DBSysException
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
            throw new DBSysException("SQL Exception while getting " +
                                      "Database connection : \n" + se.getMessage());
        }	
	}


	/**
	* Retrieving Warehouse under Company (or customer warehouse under a Warehouse)
	* and all the generic / specific roles associated with them
	*
	* @param parentId
	* @param accessType
	*
	* @return HashMap - the above said data of warehouses and roles
	*/

	public static final String	EP_WAREHOUSE_QUERY		=	"SELECT WHID FROM FS_LG_WHMASTER WHERE COMPANYID = ?";

	public static final String	ELOG_WAREHOUSE_QUERY	=	"SELECT CUSTWHID FROM FS_LG_CUSTWHMASTER WHERE WHID = ?";

	public static final String	GENERIC_ROLE_QUERY		=	"SELECT DISTINCT ROLEID FROM FS_ROLEMASTER WHERE ROLELEVEL = ? AND LOCATIONID = ? AND ROLETYPE = 'GENERIC'";

	public static final String	SPECIFIC_ROLE_QUERY		=	"SELECT DISTINCT ROLEID FROM FS_ROLEMASTER WHERE ROLELEVEL = ? AND LOCATIONID = ? AND ROLETYPE = 'SPECIFIC'";

	
	
	public HashMap getWarehousesAndRoles(String parentId, String accessType)  throws FoursoftException
    {

		HashMap	hmWareHouseAndRolesData		=	new HashMap(20);

  	    ArrayList			alWareHouses	= new ArrayList(15);
		ArrayList			alGenericRoles	= new ArrayList(25);
		
       // PreparedStatement	pstmtWh			= null;//Commented By RajKumari on 23-10-2008 for Connection Leakages.
		PreparedStatement	pstmtRole		= null;
		
      //  ResultSet			rsWh			= null;//  Commented by Govind on 15-02-2010 for Connection Leakages
		ResultSet			rsRole			= null;

		//Logger.info(FILE_NAME,"getWarehousesAndRoles(String locationId,  Timestamp fromDate, Timestamp toDate)","LOG SQL : "+USERLOG_ALLUSERS_QUERY);
		
		try
		{
			makeConnection();

//Commented By RajKumari on 23-10-2008 for Connection Leakages.
			/*pstmtWh.setString(1, parentId);
			
            rsWh	= pstmtWh.executeQuery();

            while(rsWh.next()) {
				alWareHouses.add( rsWh.getString(1) );

            }
			if(rsWh!=null)
				rsWh.close();*/


			pstmtRole = connection.prepareStatement( GENERIC_ROLE_QUERY );
			int alWarHousSize	=	alWareHouses.size();
			for(int i=0; i < alWarHousSize; i++) {

				String		whName 			=	(String) alWareHouses.get(i);
				pstmtRole.setString(1, accessType);
				pstmtRole.setString(2, whName);
						
    	        rsRole	= pstmtRole.executeQuery();

        	    while(rsRole.next()) {
					alGenericRoles.add( rsRole.getString(1) );
    	        }
				if(rsRole!=null)
					rsRole.close();

			}
      //Added By RajKumari on 23-10-2008 for Connection Leakages.
			if(pstmtRole!=null)
					pstmtRole.close();
			// Now get Roles specific to each Warehouse (Customer Warehouse) and put it in the HashMap
			
			pstmtRole = connection.prepareStatement( SPECIFIC_ROLE_QUERY );

			for(int i=0; i < alWarHousSize; i++) {

				String		whName 			=	(String) alWareHouses.get(i);
				ResultSet	rsSpRole		=	null;
				ArrayList	alSpecificRoles	=	new ArrayList(15);

				pstmtRole.clearParameters();

				pstmtRole.setString(1, accessType);
				pstmtRole.setString(2, whName);

				rsSpRole	=	pstmtRole.executeQuery();

				while(rsSpRole.next()) {
					alSpecificRoles.add( rsSpRole.getString(1) );
				}
				if(rsSpRole!=null)
					rsSpRole.close();

				alSpecificRoles.trimToSize();

				hmWareHouseAndRolesData.put( whName, alSpecificRoles);
				
			} // specific while ends

		} catch(SQLException sqle) {
			//Logger.error(FILE_NAME,"getWarehousesAndRoles(String locationId, String userId, Timestamp fromDate, Timestamp toDate)","SQL Exception ",sqle);
      logger.error(FILE_NAME+"getWarehousesAndRoles(String locationId, String userId, Timestamp fromDate, Timestamp toDate)"+"SQL Exception "+sqle);
		} catch(DBSysException dbs) {
			//Logger.error(FILE_NAME,"DBSysException ", dbs);
      logger.error(FILE_NAME+"DBSysException "+dbs);
			//throw new EJBException(dbs.getMessage());
            sessionContext.setRollbackOnly();
            throw FoursoftException.getException(dbs,FILE_NAME,"getWarehousesAndRoles");
		} finally {
			try {
				//if(pstmtWh!=null)
				//	pstmtWh.close();//Commented By RajKumari on 23-10-2008 for Connection Leakages.
				if(pstmtRole!=null)
					pstmtRole.close();
			} catch(Exception sqe){}
			ConnectionUtil.closeConnection(connection);
		}

		hmWareHouseAndRolesData.put("warehouses", alWareHouses);
		hmWareHouseAndRolesData.put("roles", alGenericRoles);
		
		return hmWareHouseAndRolesData;
	}

    private UserMasterHome getUserMasterHome() throws NamingException
    {
        final InitialContext context = new InitialContext();
        return (UserMasterHome)context.lookup("UserMasterBean");
    }

    private RoleMasterHome getRoleMasterHome() throws NamingException
    {
        final InitialContext context = new InitialContext();
        return (RoleMasterHome)context.lookup("RoleMasterBean");
    }

    public ArrayList getLocationIds(String locationId) throws FoursoftException
    {
        ArrayList	locationList = new ArrayList();
		try
		{
			locationList	= userRoleDAO.getLocationIds(locationId);
		}
		catch(Exception e)
		{
			//Logger.error(FILE_NAME,"getLocationIds()","Unknown Exception ",e);
      logger.error(FILE_NAME+"getLocationIds()"+"Unknown Exception "+e);
			//this.sessionContext.setRollbackOnly();
            throw FoursoftException.getException(e,FILE_NAME,"getLocationIds");
		}
		return locationList;
    }
	
	//added by purushothamanan to validate location id in user add screen

	public boolean isLocationIdExists(String locationId, String accessType) throws FoursoftException,NotExistsException
    {
        boolean status = false;
		try
		{
			status	= userRoleDAO.isExists(locationId, accessType);

    	if(!status)
			throw new NotExistsException(locationId+" is not Available ");
		}
		catch(NotExistsException e)
		{
			//Logger.error(FILE_NAME,"isLocationIdExists()","NotExistsException ",e);
      logger.error(FILE_NAME+"isLocationIdExists()"+"NotExistsException "+e);
			//this.sessionContext.setRollbackOnly();
            throw FoursoftException.getException(e,FILE_NAME,"isLocationIdExists",""+locationId+" is not Available ",NotExistsException.ERRORCODE);
		}
		return status;
    }

	public boolean IsLocIdExists( String locationId, String userType, String accessType, String custlocId) throws FoursoftException,NotExistsException
    {
        boolean status = false;
		try
		{
			status	= userRoleDAO.IsLocIdExists(locationId,userType,accessType,custlocId);

		if(!status)
			throw new NotExistsException(locationId+" is not Available ");
		}
		catch(NotExistsException e)
		{
			//Logger.error(FILE_NAME,"isLocationIdExists()","NotExistsException ",e);
      logger.error(FILE_NAME+"isLocationIdExists()"+"NotExistsException "+e);
			//this.sessionContext.setRollbackOnly();
            throw FoursoftException.getException(e,FILE_NAME,"isLocationIdExists",""+locationId+" is not Available ",NotExistsException.ERRORCODE);
		}
		return status;
    }
	//new methods ends here
    public String upDateUserWithPassword(String userId, String locationId,String ipAddress) throws FoursoftException
    {
        String		UPDATE_QUERY = "UPDATE FS_USERMASTER SET PASSWORD = ?,USERSTATUS=1 WHERE USERID = ? AND LOCATIONID = ?";
		String		SELECT_QUERY = "SELECT EMAILID FROM FS_USERMASTER WHERE USERID = ? AND LOCATIONID = ?";
		String		UPDATE_PASSWORDHISTORY = "UPDATE FS_PASSWORDHISTORY SET ALERTCOUNT = 0, PASSWORDDATE = ? WHERE USERID = ? AND LOCATIONID = ?";

		PreparedStatement	pStmt = null;
		ResultSet	rs = null;
		String		emailAdd = null;
		String		password = null;
		
		try
		{
			makeConnection();
			password = generatePassword();
            //System.out.println("password = "+password);
			pStmt = connection.prepareStatement(UPDATE_QUERY);
      pStmt.setString(2,userId);
			pStmt.setString(3,locationId);
			//pStmt.setString(1,password);
      pStmt.setString(1,encryptSHA(password));
            int i = pStmt.executeUpdate();
      if (i == 0)
			{
				throw new EJBException("There is no User with "+userId+" with Location "+locationId);
			}
			pStmt.close();
			pStmt = null;

			pStmt = connection.prepareStatement(UPDATE_PASSWORDHISTORY);
			pStmt.setTimestamp(1,new Timestamp(new java.util.Date().getTime()));
			pStmt.setString(2,userId);            
			pStmt.setString(3,locationId);
			int j = pStmt.executeUpdate();

			pStmt.close();
			pStmt = null;

			//updatePasswordHistory(userId,locationId,password);
      
      /* Commented By Yuvraj for Password Configuration Issue. 
         * updatePasswordHistory(userId,locationId,encryptSHA(password));
         * Maintain the whole history & fetch as per Prev_password field in Password Configuration
      */
        recordPasswordHistory(userId,locationId,encryptSHA(password));
      //@@Yuvraj
            
			pStmt = connection.prepareStatement(SELECT_QUERY);
			pStmt.setString(1,userId);
			pStmt.setString(2,locationId);
			
			rs = pStmt.executeQuery();
			if (rs.next())
				emailAdd = rs.getString(1);

			sendEMail(userId, locationId, password, emailAdd, "Forgot Password",ipAddress);
		}
		catch(Exception exp)
		{
			//Logger.error(FILE_NAME,"upDateUserWithPassword(..)","Unknown Exception ",exp);
      logger.error(FILE_NAME+"upDateUserWithPassword(..)"+"Unknown Exception "+exp);
            sessionContext.setRollbackOnly();
			throw FoursoftException.getException(exp,FILE_NAME,"upDateUserWithPassword");
		}
		finally
		{
			ConnectionUtil.closeConnection(connection,pStmt,rs);
		}
		return emailAdd;
    }
	
	private void sendEMail(String userId, String locationId, String password, String emailAdd, String subject,String ipAddress)throws Exception
	{
		Mailer      mail = null;
		try
		{
			mail = mailHome.create();
			EMailMessage message = new EMailMessage();
			//message.setMessage("User details are as follows \nUserId is "+userId+" password is "+password+" at Location "+locationId);
			StringBuffer	msge	=	new StringBuffer("QUOTESHOP User Password Details are as Follows:\n\n");
			msge.append("User Id\t\t: '"+ userId +"'\n");
			msge.append("Location Id\t\t: '"+ locationId +"'\n");
			msge.append("Password\t\t: '"+ password+"'\n");
			msge.append("\n");
			msge.append("Forgot password request has been generated from "+ipAddress);
			msge.append("\n\n"+"Thank you,\n");
			msge.append("Webmaster,\n");
			//msge.append(FoursoftConfig.LICENSEE_ID+".");
      msge.append("QUOTESHOP");
			msge.append("\n\n________________________________________________________");
			msge.append("\nIf you are not an intended recipient of this e-mail, please notify");
			msge.append("\nthe sender, delete it and do not read, act upon, print, disclose,");
			msge.append("copy, retain or redistribute it.");
			message.setMessage(msge.toString());
			message.setToAddress(emailAdd);
			message.setFromAddress("webmaster@dhl.com");
			message.setSubject(subject);
			mail.sendMail(message);
		}
		catch(Exception exp)
		{
			//Logger.error(FILE_NAME,"sendEMail(..)","Unknown Exception ",exp);
      logger.error(FILE_NAME+" sendEMail(..)"+"Unknown Exception "+exp);
			throw exp;
		}

	}


	private void sendEMail(String userId, String locationId, String password, String emailAdd, String subject)throws Exception
	{
		Mailer      mail = null;
		try
		{
			mail = mailHome.create();
			EMailMessage message = new EMailMessage();
			//message.setMessage("User details are as follows \nUserId is "+userId+" password is "+password+" at Location "+locationId);
			StringBuffer	msge	=	new StringBuffer("QUOTESHOP User Password Details are as Follows:\n\n");
			msge.append("User Id\t\t: '"+ userId +"'\n");
			msge.append("Location Id\t\t: '"+ locationId +"'\n");
			msge.append("Password\t\t: '"+ password+"'\n");
			msge.append("\n"+"Thank you,\n");
			msge.append("Webmaster,\n");
			//msge.append(FoursoftConfig.LICENSEE_ID+".");
      msge.append("QUOTESHOP");
			msge.append("\n\n________________________________________________________");
			msge.append("\nIf you are not an intended recipient of this e-mail, please notify");
			msge.append("\nthe sender, delete it and do not read, act upon, print, disclose,");
			msge.append("copy, retain or redistribute it.");
			message.setMessage(msge.toString());
			message.setToAddress(emailAdd);
			message.setFromAddress("webmaster@dhl.com");
			message.setSubject(subject);
			mail.sendMail(message);
		}
		catch(Exception exp)
		{
			//Logger.error(FILE_NAME,"sendEMail(..)","Unknown Exception ",exp);
      logger.error(FILE_NAME+"sendEMail(..)"+"Unknown Exception "+exp);
			throw exp;
		}

	}

    private MailerHome getMailerHome() throws NamingException
    {
        final InitialContext context = new InitialContext();
        return (MailerHome)context.lookup("MailerBean");
    }

	//private String generatePassword(String userName)
	private String generatePassword()
    {
        int[] ran = {48,49,50,51,52,53,54,55,56,57,65,66,67,68,69,70,71,72,73,74,75,76,77,78,79,80,81,82,83,84,85,86,87,88,89,90,97,98,99,100,101,102,103,104,105,106,107,108,109,110,111,112,113,114,115,116,117,118,119,120,121,122};
        Random random = new Random(System.currentTimeMillis());
        StringBuffer sb = new StringBuffer();
        int passwordLenght = random.nextInt(10);
       // System.out.println("passwordLenght="+passwordLenght);
		if (passwordLenght < 5)
            passwordLenght +=4;    
        //passwordLenght = userName.length();
		// System.out.println("passwordLenght="+passwordLenght);
        for(int i=0;i<passwordLenght;i++)
        {
            int val = random.nextInt(62);
            sb.append((char) ran[val]);
        }
        return sb.toString();
    }

	private String encryptSHA(String str)
    {
        MessageDigest md = null;
		String plainText ="";
        try
        {
            md = MessageDigest.getInstance("SHA-1");
        }
        catch(NoSuchAlgorithmException e)
        {
            System.out.println("Error is " + e.getMessage());
        }
       
            md.update(str.getBytes());
       
			byte[] raw = md.digest();
        //String hash = (new BASE64Encoder()).encode(raw);
        //return hash;
		
		StringBuffer hash = new StringBuffer();
		int rawLen	=	raw.length;
        for (int i = 0; i < rawLen; i++) {
            plainText = Integer.toHexString(0xFF & raw[i]);

            if (plainText.length() < 2) {
                plainText = "0" + plainText;
            }

            hash.append(plainText);
        }

        return hash.toString();
    }

	public void updateSecuritySettings(ArrayList arrayList) throws FoursoftException
    {
		String UPDATE_QUERY = "UPDATE FS_PASSWORDCONFIGURATION SET PASSWORD_DURATION=?,PASSWORD_MINLENGTH=?,PASSWORD_MAXLENGTH=?,PASSWORD_COMBINATION=?,NO_FAIL_ATTEMPTS=?,PREVIOUS_PWDS=?";
		PreparedStatement	pStmt = null;

		try
		{
			makeConnection();
            pStmt = connection.prepareStatement(UPDATE_QUERY);
			pStmt.setInt(1,Integer.parseInt((String)arrayList.get(0)));
			pStmt.setInt(2,Integer.parseInt((String)arrayList.get(1)));
			pStmt.setInt(3,Integer.parseInt((String)arrayList.get(2)));
			pStmt.setInt(4,Integer.parseInt((String)arrayList.get(3)));
			pStmt.setInt(5,Integer.parseInt((String)arrayList.get(4)));
			pStmt.setInt(6,Integer.parseInt((String)arrayList.get(5)));

			int i = pStmt.executeUpdate(); 
		
		}
		catch(Exception exp)
		{
			//Logger.error(FILE_NAME,"updateSecuritySettings(..)","Unknown Exception ",exp);
      logger.error(FILE_NAME+"updateSecuritySettings(..)"+"Unknown Exception "+exp);
			sessionContext.setRollbackOnly();
            throw FoursoftException.getException(exp,FILE_NAME,"updatePasswordDuration(int duration)");
		}
		finally
		{
			ConnectionUtil.closeConnection(connection,pStmt);
		}
    }

	public ArrayList getSecuritySettings() throws FoursoftException
    {
		String SELECT_QUERY = "SELECT PASSWORD_DURATION,PASSWORD_MINLENGTH,PASSWORD_MAXLENGTH,PASSWORD_COMBINATION,NO_FAIL_ATTEMPTS,PREVIOUS_PWDS FROM FS_PASSWORDCONFIGURATION";
		Statement	stmt = null;
		ResultSet	rs = null;
		
		ArrayList arrayList = new ArrayList();

		try
		{
			makeConnection();
            stmt = connection.createStatement();
			rs = stmt.executeQuery(SELECT_QUERY);
			
			if (rs.next()){
			arrayList.add(rs.getString(1));
			arrayList.add(rs.getString(2));
			arrayList.add(rs.getString(3));
			arrayList.add(rs.getString(4));
			arrayList.add(rs.getString(5));
			arrayList.add(rs.getString(6));
			}else
			{
			String duration="30";
				arrayList.add(duration);
			String min="4";
				arrayList.add(min);
			String max="20";
				arrayList.add(max);
			String combination="1";
				arrayList.add(combination);
			String attempts="3";
				arrayList.add(attempts);
			String previous="5";
				arrayList.add(previous);
			}

		}
		catch(Exception exp)
		{
			//Logger.error(FILE_NAME,"getSecuritySettings(..)","Unknown Exception ",exp);
      logger.error(FILE_NAME+"getSecuritySettings(..)"+"Unknown Exception "+exp);
			sessionContext.setRollbackOnly();
            throw FoursoftException.getException(exp,FILE_NAME,"getSecuritySettings()");
		}
		finally
		{
			ConnectionUtil.closeConnection(connection,stmt,rs);
		}
		
		return arrayList;
	}

    public String resetUserPassword(String userId, String locationId, String pwd, String eMailNotification) throws FoursoftException, NotExistsException
    {
        //String defaultPassword = generatePassword();

		String newpassword ="";
			//String mailpassword=eMailNotification;
			String mailpassword=getMailword(eMailNotification);
			if(pwd.equals("A1MyGHTY")){
			mailpassword =generatePassword();
			//newpassword=mailpassword;
      newpassword=encryptSHA(mailpassword);
			}
			else{
			newpassword=pwd;
			}
					
        String		SQL_Query = "UPDATE FS_USERMASTER SET PASSWORD = '"+newpassword+"',USERSTATUS=1 WHERE USERID = '"+userId+"' AND LOCATIONID = '"+locationId+"'";
		String		SQL_Query1 = "SELECT EMAILID FROM FS_USERMASTER WHERE USERID = '"+userId+"' AND LOCATIONID = '"+locationId+"'";
		String		UPDATE_PASSWORDHISTORY = "UPDATE FS_PASSWORDHISTORY SET ALERTCOUNT = 0, PASSWORDDATE = ? WHERE USERID = ? AND LOCATIONID = ?";
		Statement	stmt = null;
		ResultSet	rs = null;
		String		emailAdd = null;
		PreparedStatement pStmt=null;
		try
		{
			
			makeConnection();

            stmt = connection.createStatement();
			int i = stmt.executeUpdate(SQL_Query);
			
			
      /* Commented By Yuvraj for Password Configuration Issue. 
         * updatePasswordHistory(userId,locationId,newpassword);
         * Maintain the whole history & fetch as per Prev_password field in Password Configuration
      */
        recordPasswordHistory(userId,locationId,newpassword);
      //@@Yuvraj
			
			pStmt = connection.prepareStatement(UPDATE_PASSWORDHISTORY);
			pStmt.setTimestamp(1,new Timestamp(new java.util.Date().getTime()));
			pStmt.setString(2,userId);            
			pStmt.setString(3,locationId);
			int j = pStmt.executeUpdate();
			pStmt.close();

      //Logger.info(FILE_NAME,"SQL_Query1SQL_Query1::"+SQL_Query1);
      logger.info(FILE_NAME+"SQL_Query1SQL_Query1::"+SQL_Query1);
			rs	= stmt.executeQuery(SQL_Query1);
			if (rs.next())
				emailAdd = rs.getString(1);
			else
			{
				throw new NotExistsException("There is no User with Location "+locationId);
			}

			sendEMail(userId, locationId, mailpassword, emailAdd, "Reset Password");
		}
		catch(NotExistsException exp)
		{
			//Logger.error(FILE_NAME,"resetUserPassword(..)","NotExists Exception ",exp);
      logger.error(FILE_NAME+"resetUserPassword(..)"+"NotExists Exception "+exp);
			sessionContext.setRollbackOnly();
            throw FoursoftException.getException(exp,FILE_NAME,"There is no User with Location",""+locationId+" is not Available ",NotExistsException.ERRORCODE);
			
		}
    catch (FoursoftException fs)
    {
      //Logger.error(FILE_NAME,"resetUserPassword(..)","FoursoftException Exception ",fs);
      logger.error(FILE_NAME+"resetUserPassword(..)"+"FoursoftException Exception "+fs);
			sessionContext.setRollbackOnly();
      throw FoursoftException.getException(fs,FILE_NAME,"resetUserPassword(String userId, String locationId)",fs.getMessage());
    }
		catch(Exception exp)
		{
			//Logger.error(FILE_NAME,"resetUserPassword(..)","UnKnown Exception ",exp);
      logger.error(FILE_NAME+" resetUserPassword(..)"+"UnKnown Exception "+exp);
			sessionContext.setRollbackOnly();
            throw FoursoftException.getException(exp,FILE_NAME,"resetUserPassword(String userId, String locationId)");
		}

		finally
		{
			ConnectionUtil.closeConnection(connection,stmt,rs);
		}
		return emailAdd;
    }

    public ArrayList getUserIds(String locationId, String filterString) throws FoursoftException
    {
        ArrayList	userList	= new ArrayList();
        Statement	stmt	= null;
        ResultSet	rs		= null;

		String USER_SELECT = "SELECT USERID FROM FS_USERMASTER WHERE LOCATIONID = '"+ locationId +"' AND USERID LIKE '"+filterString+"%' ORDER BY USERID";
		//Logger.info(FILE_NAME, "getUserIds(String locationId)", USER_SELECT);

		try
		{
			makeConnection();
            stmt = connection.createStatement();

            rs = stmt.executeQuery(USER_SELECT);
            while(rs.next())
            {
 	            userList.add(rs.getString(1));
            }
		}
		catch(Exception exp)
		{
			//Logger.error(FILE_NAME,"getUserIds ", exp);
      logger.error(FILE_NAME+" getUserIds "+ exp);
			sessionContext.setRollbackOnly();
            throw FoursoftException.getException(exp,FILE_NAME,"getUserIds(String locationId, String filterString)");
		}
	
	finally
		{
			ConnectionUtil.closeConnection(connection, stmt, rs);
		}
		return userList;
    }
    
    //this method is added by ramakrishna for reporting officers lov in user add
     public ArrayList getRepOfficersIds(String filterString) throws FoursoftException
    {
        ArrayList	userList	= new ArrayList();
        Statement	stmt	= null;
        ResultSet	rs		= null;

		String USER_SELECT = "SELECT DISTINCT(EMPID),USERNAME,DESIGNATION_ID  FROM FS_USERMASTER WHERE  EMPID LIKE '"+filterString+"%' ORDER BY EMPID";
		//Logger.info(FILE_NAME, "getUserIds(String locationId)", USER_SELECT);

		try
		{System.out.print("Inside Bean");
			makeConnection();
            stmt = connection.createStatement();

            rs = stmt.executeQuery(USER_SELECT);
            while(rs.next())
            {
 	            userList.add(rs.getString(1)+","+rs.getString(2)+","+(rs.getString(3)!=null?rs.getString(3):"")+" ,");
            }
		}
		catch(Exception exp)
		{
			//Logger.error(FILE_NAME,"getUserIds ", exp);
      logger.error(FILE_NAME+"getUserIds "+ exp);
			sessionContext.setRollbackOnly();
            throw FoursoftException.getException(exp,FILE_NAME,"getUserIds(String locationId, String filterString)");
		}
	
		finally
		{
			ConnectionUtil.closeConnection(connection, stmt, rs);
		}
		return userList;
    }
    //***********************************
		//this method is used for decryptiing the encrypted password
		private String getMailword(String lstring) throws FoursoftException
		{
		String astring1=":aAb`BcVCd/eX'DfEYg FZhi?jGk|HlmI,nJo@TKpqL.WMrsNt!uvwOx<yPz>0QR12~3S4;^567U89%$#*()-_=+\"";
		String astring2="È‚‰Â‡ÁÍÎÓÏ≈…Ê∆ÙˆÚ˚˘÷‹¢£•É·Ì«¸Ò—™∫ø¨Ωº°´ª¶";
		String astring =astring1+astring2;
   	String retstr="";
    try
    {
      int lStrLen	=	lstring.length();
      for (int i=0;i<lStrLen; i++)
      {
         int aNum=astring.indexOf(lstring.substring(i,i+1),0);
         aNum=aNum^25;
         retstr=retstr+astring.substring(aNum,aNum+1);
        
      }
         
    }
    catch(Exception e)
    {
      //Logger.error(FILE_NAME,"exception in Mail Word:"+e);
      logger.error(FILE_NAME+"exception in Mail Word:"+e);
      e.printStackTrace();
      throw new FoursoftException("An Error Has Occurred While Encrypting the Password For Sending Mail. Please Try Again.",e);
    }
       return retstr;
		
	}

	private void updatePasswordHistory(String userId, String locationId, String password)
    {
        
		PreparedStatement pStmt		= null;
		PreparedStatement pStmt1	= null;
		ResultSet rs				= null;
        try
		{
			pStmt=connection.prepareStatement("Select count(*) from FS_PREVIOUSPASSWORDHISTORY where USERID=? AND LOCATIONID=?");
			pStmt.setString(1,userId);
            pStmt.setString(2,locationId);
            rs=pStmt.executeQuery();
			int five=0;
			if(rs.next())
			{
			five=rs.getInt(1);
			}
			rs.close();
      //Added By RajKumari on 23-10-2008 for Connection Leakages.
      if(pStmt!=null)
      pStmt.close();
			pStmt=null;
			int prv_set = 3;
			pStmt=connection.prepareStatement("Select PREVIOUS_PWDS from FS_PASSWORDCONFIGURATION");
			rs=pStmt.executeQuery();
			
			if(rs.next())
			{
			prv_set=rs.getInt(1);
			}
			rs.close();
			
			if(prv_set==0)
			prv_set=5;

			if(five < prv_set)
			{
			recordPasswordHistory(userId,locationId,password);
			}
			else{
			pStmt1 = connection.prepareStatement("UPDATE FS_PREVIOUSPASSWORDHISTORY SET PASSWORD = ?, PASSWORDDATE  = ? WHERE USERID = ? AND LOCATIONID = ? AND PASSWORDDATE =(SELECT MIN(PASSWORDDATE) FROM FS_PREVIOUSPASSWORDHISTORY WHERE USERID = ? AND LOCATIONID = ?)");
            pStmt1.setString(1,password);
            pStmt1.setTimestamp(2,new Timestamp( new java.util.Date().getTime()));
            pStmt1.setString(3,userId);
            pStmt1.setString(4,locationId);
			pStmt1.setString(5,userId);
            pStmt1.setString(6,locationId);
			pStmt1.executeUpdate();
			}            
		}
        catch(Exception exp)
        {
            //Logger.error(FILE_NAME, "FS_PREVIOUSPASSWORDHISTORY(String userId, String locationId)","Error while entry into FS_PREVIOUSPASSWORDHISTORY ",exp);
            logger.error(FILE_NAME+ "FS_PREVIOUSPASSWORDHISTORY(String userId, String locationId)"+"Error while entry into FS_PREVIOUSPASSWORDHISTORY "+exp);
            
        }
        finally
        {
            ConnectionUtil.closeStatement(pStmt);
			ConnectionUtil.closeStatement(pStmt1);
        }
    }

	private void recordPasswordHistory(String userId, String locationId, String password)
    {
        PreparedStatement pStmt	= null;
		
        try
		{
			pStmt = connection.prepareStatement("INSERT INTO FS_PREVIOUSPASSWORDHISTORY (USERID, LOCATIONID, PASSWORD, PASSWORDDATE) VALUES (?,?,?,?)");
            Timestamp entryDate = new Timestamp( new java.util.Date().getTime());
            pStmt.setString(1,userId);
            pStmt.setString(2,locationId);
            pStmt.setString(3,password);
            pStmt.setTimestamp(4,entryDate);
            pStmt.executeUpdate();
        }
        catch(Exception exp)
        {
            //Logger.error(FILE_NAME, "recordPasswordHistory(String userId, String locationId, String password)","Error while entry into FS_PREVIOUSPASSWORDHISTORY ",exp);
            logger.error(FILE_NAME+ "recordPasswordHistory(String userId, String locationId, String password)"+"Error while entry into FS_PREVIOUSPASSWORDHISTORY "+exp);
         }
        finally
        {
            ConnectionUtil.closeStatement(pStmt);
        }
    }
    //@@ Added by subrahmanyam for the Enhancement 154384 on 28/01/09   
     public ArrayList getSalesPersons(String locationId, String delSalesPerson, String accessTerminal)throws FoursoftException
     {
       
    	 PreparedStatement    pst                = null;
              ArrayList salesPersons                   =new ArrayList();
              ResultSet                 rs          = null;
              String salesPersonOPRQry="SELECT EMPID FROM FS_USERMASTER WHERE LOCATIONID IN(SELECT CHILD_TERMINAL_ID FROM FS_FR_TERMINAL_REGN WHERE PARENT_TERMINAL_ID IN(SELECT PARENT_TERMINAL_ID FROM FS_FR_TERMINAL_REGN WHERE CHILD_TERMINAL_ID=?)) AND EMPID NOT IN(?) ORDER BY EMPID";
              String salesPersonHOADMqry="SELECT EMPID FROM FS_USERMASTER WHERE LOCATIONID=? AND EMPID NOT IN(?) ORDER BY EMPID";
              
              try
              {
                  makeConnection();
                  if("HO_TERMINAL".equalsIgnoreCase(accessTerminal)||"ADMN_TERMINAL".equalsIgnoreCase(accessTerminal))
                     pst                 = connection.prepareStatement(salesPersonHOADMqry);
                 else
                    pst                 = connection.prepareStatement(salesPersonOPRQry);
                    
                    pst.setString(1,locationId);
                    pst.setString(2,delSalesPerson);
                  
                    rs=pst.executeQuery();
                    
                    while(rs.next())
                    {
                      salesPersons.add(rs.getString("EMPID"));
                      
                    }
                    
              }catch(Exception e)
              {
                e.printStackTrace();
                throw new FoursoftException();
              }
             finally
              {
            ConnectionUtil.closeConnection(connection, pst,rs);//@@ edited by govind on 15-02-2010 for connection lekages. 	                                                       
          	   
              }
    
            return salesPersons;
  }
   //@@Commented & Added by subrahmanyam for the pbn id:208582 on 17-Jun-10  
     //public void updateSalesPerson(String newSalesPerson,String delSalesPerson)throws FoursoftException
     public void updateSalesPerson(String newSalesPerson,String delSalesPerson,String accessType)throws FoursoftException     
  {
        PreparedStatement    pst                = null;
        String              updateQry           = "UPDATE QMS_QUOTE_MASTER SET SALES_PERSON=? WHERE SALES_PERSON=? ";
       //@@Added by subrahmanyam for the pbn id:208582 on 17-Jun-10
        PreparedStatement    pst1                = null;
       	PreparedStatement    pst2                = null;
    	PreparedStatement    pst3                = null;

        String              updateEscalteTO      = "UPDATE QMS_QUOTE_MASTER SET ESCALATED_TO=? WHERE ESCALATED_TO=? ";
        String 				updRepOfficer	 	 = "UPDATE FS_REP_OFFICERS_MASTER RM SET RM.REP_OFFICERS_ID=? WHERE RM.REP_OFFICERS_ID=?";
        String 				updRepOffUserMaster	 = "UPDATE FS_USERMASTER UM SET UM.REP_OFFICERS_ID=? WHERE UM.REP_OFFICERS_ID=?";
        //@@Ended by subrahmanyam for the pbn id:208582 on 17-Jun-10
        try
      {
            makeConnection();
            pst                 = connection.prepareStatement(updateQry);
            pst.setString(1,newSalesPerson);
            pst.setString(2,delSalesPerson);

            pst.executeUpdate();
            //@@Added by subrahmanyam for the pbn id:208582 on 17-Jun-10            
            pst1                 = connection.prepareStatement(updateEscalteTO);
            pst1.setString(1,newSalesPerson);
            pst1.setString(2,delSalesPerson);
            pst1.executeUpdate();
            
            if(accessType.equalsIgnoreCase("ADMN_TERMINAL"))
            {
            	pst2                 = connection.prepareStatement(updRepOfficer);
            	pst3                 = connection.prepareStatement(updRepOffUserMaster);
                pst2.setString(1,newSalesPerson);
                pst2.setString(2,delSalesPerson);
                pst2.executeUpdate();
 
                pst3.setString(1,newSalesPerson);
                pst3.setString(2,delSalesPerson);
                pst3.executeUpdate();
                    
            }
            //@@Ended by subrahmanyam for the pbn id:208582 on 17-Jun-10            
      }catch(Exception e)
       {
            e.printStackTrace();
            throw new FoursoftException();
       }
       finally
        {
            ConnectionUtil.closeConnection(connection, pst);
            ConnectionUtil.closePreparedStatement(pst1); //@@Added by subrahmanyam for the pbn id:208582 on 17-Jun-10
            ConnectionUtil.closePreparedStatement(pst2); 
            ConnectionUtil.closePreparedStatement(pst3); 
        }
  }
  public String getDelSalesPerson(String delUserId)throws FoursoftException
  {
    PreparedStatement    pst                = null;
    ResultSet           rs                 =null;
    String              createdByQry        =  "SELECT EMPID FROM FS_USERMASTER WHERE USERID=?";
    String              delSalesPerson        =null;
    try
      {
            makeConnection();
            pst                 = connection.prepareStatement(createdByQry);
            pst.setString(1,delUserId);
           
            rs=pst.executeQuery();
            while(rs.next())
            {
              delSalesPerson=rs.getString("EMPID");
            }
      }catch(Exception e)
       {
            e.printStackTrace();
            throw new FoursoftException();
       }
       finally
        {
            ConnectionUtil.closeConnection(connection, pst,rs);//@@ edited by govind on 15-02-2010 for connection lekages. 
        }
        return delSalesPerson;
  }
     
//@@ Ended by subrahmanyam for the Enhancement 154384 on 28/01/09   
  //Added by Rakesh
  public ArrayList getUserViewAllInformation2(String locationId) throws FoursoftException
	{
		PreparedStatement	pStmt		= null;
		ResultSet	rs			= null;

		ArrayList userInfoList	= new ArrayList();

		try
		{
			makeConnection();

			pStmt = connection.prepareStatement(ALL_USERS_QUERY2);
          pStmt.setString(1,locationId);
			rs		= pStmt.executeQuery();

			while ( rs.next() )
			{
				userInfoList.add( new UserRoleDOB(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6), rs.getString(7), rs.getString(8), rs.getString(9), rs.getString(10), rs.getString(11),rs.getString(12),rs.getString(13),rs.getString(14)) );
			}
		}
		catch (SQLException sql)
		{
			//Logger.error(FILE_NAME, "getUserViewAllInformation()","SQL Exception ",sql);
    logger.error(FILE_NAME+ " getUserViewAllInformation()"+"SQL Exception "+sql);
		}
		catch(DBSysException dbs)
		{
			//Logger.error(FILE_NAME,"DBSysException ", dbs);
    logger.error(FILE_NAME+"DBSysException "+ dbs);
			EJBException ejbE = new EJBException("");
			Exception cause = ejbE.getCausedByException();
			//throw new EJBException(dbs.getMessage());
          sessionContext.setRollbackOnly();
          throw FoursoftException.getException(dbs,FILE_NAME,"getUserViewAllInformation()");
		}
		finally
		{
  //Modified By RajKumari on 23-10-2008 for Connection Leakages.
			//ConnectionUtil.closeConnection(connection, pStmt);
    ConnectionUtil.closeConnection(connection, pStmt, rs);
		}
		userInfoList.trimToSize();
		return userInfoList;
	} // end of getUserViewAllInformation()
  
}