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
import java.sql.Timestamp;
import java.sql.Types;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.Iterator;


import javax.ejb.CreateException;
import javax.ejb.DuplicateKeyException;
import javax.ejb.EJBException;
import javax.ejb.FinderException;
import javax.ejb.ObjectNotFoundException;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import com.foursoft.esupply.common.exception.DBSysException;
import com.foursoft.esupply.accesscontrol.exception.RoleDAOAppException;
import com.foursoft.esupply.accesscontrol.java.RoleModel;
import com.foursoft.esupply.accesscontrol.java.TxDetailVOB;
import com.foursoft.esupply.common.util.ConnectionUtil;
//import com.foursoft.esupply.common.util.Logger;
import com.foursoft.esupply.common.dao.BaseDAOImpl;
import org.apache.log4j.Logger;

/**
 * File			: RoleDAO.java
 * sub-module	: AccessControl
 * module		: esupply
 * 
 * This is used as DAO for Role Creation/ Modify/ View/ Delete
 * 
 * @author	Madhu. P, 
 * @date	28-10-2001
 * 
 * Modified History
 * 18-11-2202		Amit Parekh		Updated to handle optional roles for a COMPANY User for EP and
 * 									a WAREHOUSE User for ELOG. A new table FS_OPTIONALROLES was added
 *                                  in the database for these changes. This file handles only the
 *                                  deletion of the mention of a role from the optional roles table
 *                                  when a role is deleted from the master table
 *
 * 01/03/2003		Amit Parekh		Updated to handle versioning the entitiy. This changes also require
 * 									changes in the database
 */

public class RoleDAO extends BaseDAOImpl
{
    private static final String[]		insQuery = new String[2];
	private static final String[]		delQuery = new String[3];
	private static final String[]		updateQuery = new String[2];
	private static final String			pkQuery;
	private static final String[]		loadQuery = new String[2];
	private static final String fileName = "RoleDAO.java ";
  private static Logger logger = null;
	static {

		insQuery[0]		=	"INSERT INTO FS_ROLEMASTER ( "+
							"	ROLEID, "+
							"	LOCATIONID, "+
							"	ROLEMODULE, "+
							"	ROLELEVEL, "+
							"	ROLETYPE, "+
							"	DESCRIPTION, "+
							"	VERSION "+
							")" +
							"	VALUES(?,?,?,?,?,?,?)";
							
		insQuery[1]		=	"INSERT INTO FS_ROLEPERMISSIONS ( "+
							"	ROLEID, "+
							"	LOCATIONID, "+
							"	TXID, "+
							"	ACCESSLEVEL "+
							") "+
							"	VALUES(?,?,?,?)";
														
		pkQuery			=	"SELECT "+
							"	ROLEID, "+
							"	LOCATIONID " +
				 			"FROM "+
							"	FS_ROLEMASTER " +
							"WHERE "+
							"	ROLEID = ? AND LOCATIONID = ?";

							
		loadQuery[0]	=	"SELECT "+
							"	ROLEID, "+
							"	LOCATIONID, "+
							"	ROLEMODULE, "+
							"	ROLELEVEL, "+
							"	ROLETYPE, "+
							"	DESCRIPTION, " +
							"	VERSION, " +
							"	MODIFIED_BY, " +
							"	MODIFIED_DATE " +
							"FROM "+
							"	FS_ROLEMASTER " +
							"WHERE "+
							"	ROLEID = ? AND LOCATIONID = ?";

		loadQuery[1]	=	"SELECT "+
							"	RP.TXID, "+
							"	RP.ACCESSLEVEL, "+
							"	TM.SHIPMENTMODE, "+
							"	TM.MODULE "+
							"FROM "+
							"	FS_ROLEPERMISSIONS RP, "+
							"	FS_TXMASTER TM "+
							"WHERE "+
							"	RP.ROLEID = ? AND RP.LOCATIONID = ? AND "+
							"	RP.TXID = TM.TXID "+
							"ORDER BY "+
							"	RP.TXID, TM.SHIPMENTMODE";

		updateQuery[0]	=	"UPDATE FS_ROLEMASTER SET "+
							"	ROLEMODULE = ?, "+
							"	ROLELEVEL =?, "+
							"	ROLETYPE =?, "+
							"	DESCRIPTION =?, "+
							"	VERSION = ?, "+
							"	MODIFIED_BY = ?, "+
							"	MODIFIED_DATE = ? " +
							"WHERE "+
							"	ROLEID = ? AND LOCATIONID = ?" ;
									
		updateQuery[1]	=	"UPDATE FS_ROLEPERMISSIONS SET "+
							"	ACCESSLEVEL = ? "+
							"WHERE "+
							"	ROLEID = ? AND LOCATIONID = ? AND TXID = ? ";

		delQuery[0]		=	"DELETE FROM FS_ROLEMASTER WHERE ROLEID = ? AND LOCATIONID = ?";
		
		delQuery[1]		=	"DELETE FROM FS_ROLEPERMISSIONS WHERE ROLEID = ? AND LOCATIONID = ?";

		delQuery[2]		=	"DELETE FROM FS_OPTIONALROLES WHERE ROLEID = ? ";
	}

	public RoleDAO()
	{
		//super();
    logger  = Logger.getLogger(RoleDAO.class);
	}
	public void create(RoleModel roleModel) throws RoleDAOAppException, DuplicateKeyException, DBSysException
	{
		//Logger.info(fileName,"create(RoleModel roleModel)",roleModel.toString());
    logger.info(fileName+" create(RoleModel roleModel) "+roleModel.toString());
		try
		{
			getConnection();

			inserIntoRoleMaster(roleModel);

			insertIntoRolePermissions(roleModel);
		}
		finally
		{
				ConnectionUtil.closeConnection(connection);
				roleModel	= null;
		}
	}

	public RoleModel load(String roleId, String locationId) throws RoleDAOAppException, DBSysException 
	{
		// insert dataBase code here
		try
		{
			getConnection();
			RoleModel roleModel = new RoleModel();
			roleModel.setRoleId(roleId);
			roleModel.setLocationId(locationId);
//		load the RoleMaster Details
			roleModel = loadRoleMaster(roleId, locationId);

//		load the RolePermissions Details
			roleModel.setRolePermissions(loadRolePermissions(roleId, locationId) );
			
			return roleModel;			
		}
		finally
		{
				ConnectionUtil.closeConnection(connection);
		}

	}
	public void store(RoleModel roleModel) throws RoleDAOAppException, DBSysException
	{
		try
		{
			getConnection();
			updateRoleMaster(roleModel);

			removeRolePermissions(roleModel.getRoleId(), roleModel.getLocationId());
			insertIntoRolePermissions(roleModel);
		}
		finally
		{
				ConnectionUtil.closeConnection(connection);
		}
		
	}
	
	public void remove(String roleId, String locationId) throws RoleDAOAppException, DBSysException
	{
		try
		{
			getConnection();
			removeOptionalRolePermissions(roleId, locationId);
			removeRolePermissions(roleId, locationId);
			removeRoleMaster(roleId, locationId);
		}
		finally
		{
			ConnectionUtil.closeConnection(connection);
		}
	}
	
	public boolean findByPrimaryKey(String roleId, String locationId) throws javax.ejb.ObjectNotFoundException
	{
		boolean hasRows		= false;

		PreparedStatement pstmt   = null;
		ResultSet rs = null;
		try
		{
			getConnection();

			pstmt = connection.prepareStatement(pkQuery);
					pstmt.setString(1,(String)roleId);
					pstmt.setString(2,(String)locationId);
			rs = pstmt.executeQuery();
			if(rs.next()){
				hasRows = true;
			}
			if(rs!=null)
				rs.close();
		}
		catch(SQLException se)
		{
			//Logger.error("RoleDAO", "findByPrimaryKey(String roleId, String locationId)","Error in Find RoleModel - SQLException ",se);
      logger.error("RoleDAO "+ " findByPrimaryKey(String roleId, String locationId)"+" Error in Find RoleModel - SQLException "+se);
		}
		catch(Exception e)
		{
			//Logger.error("RoleDAO", "findByPrimaryKey(String roleId, String locationId)","Error in Find RoleModel - Exception ",e);
      logger.error("RoleDAO "+ "findByPrimaryKey(String roleId, String locationId)"+"Error in Find RoleModel - Exception "+e);
		}
		finally
		{
			ConnectionUtil.closeConnection(connection, pstmt);
		}
		if(hasRows)
			return true;
		else
			throw new javax.ejb.ObjectNotFoundException("Could not find bean");

	}

	private void inserIntoRoleMaster(RoleModel roleModel) throws RoleDAOAppException, DBSysException
	{
		PreparedStatement		pstmt = null;
		java.lang.String pkObj = null;
		try
		{
			pstmt = connection.prepareStatement(insQuery[0]);

			if(roleModel.getRoleId() != null){
				pstmt.setString(1,(String)roleModel.getRoleId());
			}else{
				pstmt.setNull(1,Types.VARCHAR);
			}
			if(roleModel.getLocationId() != null){
				pstmt.setString(2,(String)roleModel.getLocationId());
			}else{
				pstmt.setNull(2,Types.VARCHAR);
			}
			if(roleModel.getRoleModule() != null){
				pstmt.setString(3,(String)roleModel.getRoleModule());
			}else{
				pstmt.setNull(3,Types.VARCHAR);
			}
			if(roleModel.getRoleLevel() != null){
				pstmt.setString(4,(String)roleModel.getRoleLevel());
			}else{
				pstmt.setNull(4,Types.VARCHAR);
			}
			if(roleModel.getRoleType() != null){
				pstmt.setString(5,(String)roleModel.getRoleType());
			}else{
				pstmt.setNull(5,Types.VARCHAR);
			}
			if(roleModel.getDescription() != null){
				pstmt.setString(6,(String)roleModel.getDescription());
			}else{
				pstmt.setNull(6,Types.VARCHAR);
			}

			pstmt.setInt(7, roleModel.getVersion());

			int resultCount = pstmt.executeUpdate();
			if(resultCount != 1)
				throw new RoleDAOAppException("Error in registering the Role  !! resultCount = "+resultCount );
		}
		catch(SQLException se)
		{
			throw new DBSysException("SQLException while Registering the Role : \n"+se);
		}
		finally
		{
			ConnectionUtil.closeStatement(pstmt);
     // ConnectionUtil.closeConnection(conn);
		}
		
	}
	
	private void insertIntoRolePermissions(RoleModel roleModel) throws RoleDAOAppException, DBSysException
	{
		ArrayList rolePermissions = roleModel.getRolePermissions();
		TxDetailVOB txDetailVOB	= null;
		PreparedStatement pstmt   = null;
		try
		{
			pstmt = connection.prepareStatement(insQuery[1]);
			pstmt.setString(1, (String)roleModel.getRoleId() );
			pstmt.setString(2, (String)roleModel.getLocationId() );

			Iterator	permissionItr	= rolePermissions.iterator();
			
			while(permissionItr.hasNext())
			{
				txDetailVOB	= (TxDetailVOB)permissionItr.next();
				pstmt.setString(3, (String)txDetailVOB.txId);
				pstmt.setInt(4, txDetailVOB.accessLevel);

				int resultCount	= pstmt.executeUpdate();
				if(resultCount != 1)
					throw new RoleDAOAppException("Error in registering the Role previllages !! resultCount = "+resultCount );
			}
		}
		catch(SQLException se)
		{
			throw new DBSysException("SQLException while Registering the Role preveliges : \n"+se);
		}
		finally
		{
			ConnectionUtil.closeStatement(pstmt);	
			rolePermissions	= null;
		}
	}

	/**
	* Which is used to load the Role Details from the database
	*
	* @exception	SQLException
	*
	*/
	private RoleModel loadRoleMaster(String roleId, String locationId) throws RoleDAOAppException, DBSysException
	{
		PreparedStatement   pstmt = null;
		RoleModel roleModel	= null;
		try
		{
			roleModel		= new RoleModel();
			pstmt = connection.prepareStatement(loadQuery[0]);

			pstmt.setString(1,(String)roleId);
			pstmt.setString(2,(String)locationId );
			ResultSet rs = pstmt.executeQuery();
			if(rs.next()){
				roleModel.setRoleId(rs.getString(1) );
				roleModel.setLocationId(rs.getString(2) );
				roleModel.setRoleModule(rs.getString(3) );
				roleModel.setRoleLevel(rs.getString(4) );
				roleModel.setRoleType(rs.getString(5) );
				roleModel.setDescription(rs.getString(6) );

				roleModel.setVersion(rs.getInt(7) );
				roleModel.setModifiedBy(rs.getString(8) );
				roleModel.setModifiedDate(rs.getTimestamp(9) );
			}
			if(rs!=null)
				rs.close();
				
			return roleModel;
		}
		catch(SQLException se)
		{
			throw new DBSysException("SQLException while loading the Role attributes : \n"+se);
		}
		finally
		{
			ConnectionUtil.closeStatement(pstmt);	
		}
	} // end of loadRoleMaster(Connection connection)

	/**
	* Which is used to load the Role Previlleges from the database
	* Assign these values to the rolePermissions attribute of the bean
	*
	* @exception	SQLException
	*
	*/
	private ArrayList loadRolePermissions(String roleId, String locationId) throws DBSysException
	{
		PreparedStatement	pstmt	= null;
		ResultSet			rs				= null;
		try
		{
			pstmt = connection.prepareStatement(loadQuery[1]);

			ArrayList	permissionsList	= new ArrayList();

			pstmt.setString(1, (String)roleId);
			pstmt.setString(2, (String)locationId);
			rs	= pstmt.executeQuery();


			TxDetailVOB	txDetailVOB	= null;

			while(rs.next())
			{
				txDetailVOB	= new TxDetailVOB( rs.getString(1), rs.getInt(2) );
				
				Object obj	= rs.getObject( 3 );
				
				if(obj==null) {
					//System.out.println(" shipmentMode Integer obj is NULL");
					txDetailVOB.shipmentMode = -1;
				} else {
					txDetailVOB.shipmentMode = rs.getInt(3);
					//System.out.println(" shipmentMode Integer obj is having value = "+txDetailVOB.shipmentMode);
				}
				if(rs.getString(4)!=null) {
					txDetailVOB.module =rs.getString(4);
					//System.out.println("     txDetailVOB.module = "+txDetailVOB.module);
				}
				 
				permissionsList.add(txDetailVOB);
			}

			permissionsList.trimToSize();
			if(rs!=null)
				rs.close();
				
			return permissionsList;
		}
		catch(SQLException se)
		{
			throw new DBSysException("SQLException while loading the Role Permissions : \n"+se);
		}
		finally
		{
			ConnectionUtil.closeStatement(pstmt);	
		}
		

	} // end of loadRolePermissions()
		
	/**
	* Which is used to update the databes with current values of the Bean
	*
	* @exception	RoleDAOAppException, DBSysException
	*
	*/
	private void updateRoleMaster(RoleModel roleModel) throws RoleDAOAppException, DBSysException
	{
		PreparedStatement		pstmt = null;
		try{

			pstmt = connection.prepareStatement(updateQuery[0]);

			pstmt.setString(1,(String)roleModel.getRoleModule() );
			pstmt.setString(2,(String)roleModel.getRoleLevel() );
			pstmt.setString(3,(String)roleModel.getRoleType() );
			pstmt.setString(4,(String)roleModel.getDescription() );

			pstmt.setInt(5,roleModel.getVersion() );
			pstmt.setString(6,roleModel.getModifiedBy() );
			pstmt.setTimestamp(7,roleModel.getModifiedDate() );

			pstmt.setString(8,(String)roleModel.getRoleId() );
			pstmt.setString(9,(String)roleModel.getLocationId() );

			int resultCount = pstmt.executeUpdate();
			if(resultCount != 1)
				throw new RoleDAOAppException("Error in Updating the Role properties !! resultCount = "+resultCount );
		}
		catch(SQLException se)
		{
			throw new DBSysException("SQLException while updating Role : \n"+se);
		}
		finally
		{
			ConnectionUtil.closeStatement(pstmt);
		}
	}
		
	/**
	* Which is used to remove the record from RoleMaster
	*
	* @exception	RoleDAOAppException, DBSysException
	*
	*/
	private void removeRoleMaster(String roleId, String locationId) throws RoleDAOAppException, DBSysException
	{
		PreparedStatement	pstmt	= null;
		try
		{
			pstmt = connection.prepareStatement(delQuery[0]);

			pstmt.setString(1, (String)roleId);
			pstmt.setString(2, (String)locationId);
			int resultCount = pstmt.executeUpdate();
			if(resultCount != 1)
				throw new RoleDAOAppException("Error in Removing the Role properties !! resultCount = "+resultCount );
		}
		catch(SQLException se)
		{
			throw new DBSysException("SQLException while removing Role : \n"+se);
		}
		finally
		{
			ConnectionUtil.closeStatement(pstmt);	
		}
	} // end of removeRoleMaster(Connection connection)


	/**
	* Which is used to remove the record from RolePermissions
	*
	* @exception	RoleDAOAppException, DBSysException
	*
	*/
	private void removeRolePermissions(String roleId, String locationId) throws RoleDAOAppException, DBSysException
	{
		PreparedStatement	pstmt	= null;
		
		try
		{
			pstmt = connection.prepareStatement(delQuery[1]);

			pstmt.setString(1, (String) roleId);
			pstmt.setString(2, (String) locationId);
			
			int resultCount = pstmt.executeUpdate();
			
			if(resultCount < 1)
				throw new RoleDAOAppException("Error in removing Role Permissions !! resultCount = "+resultCount );

		}
		catch(SQLException se)
		{
			throw new DBSysException("SQLException while removing Role Permissions : \n"+se);
		}		
		finally
		{
			ConnectionUtil.closeStatement(pstmt);
		}

	} // end of removeRolePermissions(Connection connection)

	/**
	* Which is used to remove the record from RolePermissions
	*
	* @exception	RoleDAOAppException, DBSysException
	*
	*/
	private void removeOptionalRolePermissions(String roleId, String locationId) throws RoleDAOAppException, DBSysException
	{
		PreparedStatement	pstmt	= null;
		try
		{

			pstmt = connection.prepareStatement(delQuery[2]);

			pstmt.setString(1, (String) roleId );

			int resultCount1 = pstmt.executeUpdate();
		}
		catch(SQLException se)
		{
			throw new DBSysException("SQLException while removing Role Permissions : \n"+se);
		}		
		finally
		{
			ConnectionUtil.closeStatement(pstmt);
		}

	} // end of removeRolePermissions(Connection connection)

}
