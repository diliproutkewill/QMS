/**
 * Copyright (c) 2001-2001 Four-Soft Pvt Ltd, 
 * 5Q1A3, Hi-Tech City, Madhapur, Hyderabad-33, India.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of  Four-Soft Pvt Ltd,
 * ("Confidential Information").  You shall not disclose such Confidential Information 
 * and shall use it only in accordance with the terms of the license agreement you entered 
 * into with Four-Soft. For more information on the Four Soft Pvt Ltd
 */
package com.foursoft.etrans.common.routeplan.dao;

/**
 * File		: ETMultiModeRoutePlanEntityBean.java
 * @author	: Srinivasa Rao Koppurauri 
 * @date	: 2002-05-20
 *
 */
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Types;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import javax.ejb.EntityBean; 
import javax.ejb.EntityContext;
import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ejb.FinderException;
import javax.ejb.ObjectNotFoundException;
import javax.ejb.RemoveException;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import com.foursoft.esupply.common.util.ConnectionUtil;
//import com.foursoft.esupply.common.util.Logger;
import org.apache.log4j.Logger;
import com.foursoft.etrans.common.routeplan.java.ETMultiModeRoutePlanHdrDOB;
import com.foursoft.etrans.common.routeplan.java.ETMultiModeRoutePlanDtlDOB;
/**class name ETMultiModeRoutePlanDAO
 * @author 
 * @version: 1.6
 */
public class ETMultiModeRoutePlanDAO
{
	private static final String FILE_NAME = "ETCustomerContractDAO.java";
  private static Logger logger = null;

	private transient DataSource dataSource	= null;

	private static String[] insQuery	= new String[3];
	private static String[]	delQuery	= new String[2];
	private static String[]	updateQuery = new String[1];
	private static String[]	loadQuery	= new String[2];
	private static String	pkQuery		= null;

	static 
	{
		insQuery[0]		= "INSERT INTO FS_RT_PLAN(RT_PLAN_ID, PRQ_ID, HAWB_ID, ORIG_TRML_ID, DEST_TRML_ID, ORIG_LOC_ID, DEST_LOC_ID, SHIPPER_ID, CONSIGNEE_ID, PRMY_MODE, OVER_WRITE_FLAG, CRTD_TIMESTMP, LAST_UPDTD_TIMESTMP, SHPMNT_STATUS) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		insQuery[1]		= "INSERT INTO FS_RT_LEG(SERIAL_NO, RT_PLAN_ID, ORIG_TRML_ID, DEST_TRML_ID, SHPMNT_MODE, SHPMNT_STATUS, AUTO_MNUL_FLAG, MSTER_DOC_ID, LEG_TYPE, LEG_VALID_FLAG, PIECES_RECEIVED, RECEIVED_DATE, REMARKS, COSTAMOUNT)VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		//loadQuery[0]	= "SELECT RT_PLAN_ID, PRQ_ID, HAWB_ID, ORIG_TRML_ID, DEST_TRML_ID, ORIG_LOC_ID, DEST_LOC_ID, SHIPPER_ID, CONSIGNEE_ID, PRMY_MODE, OVER_WRITE_FLAG, CRTD_TIMESTMP, LAST_UPDTD_TIMESTMP, SHPMNT_STATUS FROM FS_RT_PLAN WHERE RT_PLAN_ID = ?";
    loadQuery[0]	= "SELECT RT_PLAN_ID, QUOTE_ID, ORIG_TRML_ID, DEST_TRML_ID, ORIG_LOC_ID, DEST_LOC_ID, SHIPPER_ID, CONSIGNEE_ID, PRMY_MODE, OVER_WRITE_FLAG, CRTD_TIMESTMP, LAST_UPDTD_TIMESTMP, SHPMNT_STATUS FROM FS_RT_PLAN WHERE RT_PLAN_ID = ?";
		loadQuery[1]	= "SELECT SERIAL_NO, RT_PLAN_ID,ORIG_TRML_ID,DEST_TRML_ID , SHPMNT_MODE, SHPMNT_STATUS, AUTO_MNUL_FLAG, MSTER_DOC_ID, LEG_TYPE, LEG_VALID_FLAG, PIECES_RECEIVED, RECEIVED_DATE, REMARKS, COSTAMOUNT,ORIG_LOC,DEST_LOC FROM FS_RT_LEG WHERE RT_PLAN_ID = ?";
		delQuery[0]		= "DELETE FROM FS_RT_PLAN WHERE RT_PLAN_ID = ?";
		delQuery[1]		= "DELETE FROM FS_RT_LEG WHERE RT_PLAN_ID = ?";

		updateQuery[0]	= "UPDATE FS_RT_PLAN SET PRQ_ID=?, HAWB_ID=?, ORIG_TRML_ID=?, DEST_TRML_ID=?, ORIG_LOC_ID=?, DEST_LOC_ID=?, SHIPPER_ID=?, CONSIGNEE_ID=?, PRMY_MODE=?, OVER_WRITE_FLAG=?, CRTD_TIMESTMP=?, LAST_UPDTD_TIMESTMP=?, SHPMNT_STATUS=? WHERE RT_PLAN_ID = ?";
		pkQuery			= "SELECT RT_PLAN_ID FROM FS_RT_PLAN WHERE RT_PLAN_ID = ?";
    
    insQuery[2]		= "INSERT INTO FS_RT_LEG(SERIAL_NO, RT_PLAN_ID,LEG_TYPE,ORIG_LOC,DEST_LOC,ORIG_TRML_ID, DEST_TRML_ID,SHPMNT_MODE,REMARKS )VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
	}

	/**
	 *	Default constructor. Takes no arguments
	 *	gets the DataSource
	 */
	public ETMultiModeRoutePlanDAO() throws Exception
	{
    logger  = Logger.getLogger(ETMultiModeRoutePlanDAO.class);
		try
		{
			InitialContext ic	= new InitialContext();
			dataSource			=(DataSource) ic.lookup("java:comp/env/jdbc/DB");
		}
		catch(Exception nmEx)
		{
			//Logger.error(FILE_NAME, "[ETMultiModeRoutePlanDAO()] -> "+nmEx.toString());
      logger.error(FILE_NAME+ "[ETMultiModeRoutePlanDAO()] -> "+nmEx.toString());
			throw new EJBException(nmEx.toString());
		}
	}

  /**
   * 
   * @param pkObj
   * @return pkObj
   * @throws java.sql.SQLException
   * @throws javax.ejb.ObjectNotFoundException
   */
	public Long findByPrimaryKey(Long pkObj)
					throws  SQLException, ObjectNotFoundException
	{
		Connection			connection	= null;
		PreparedStatement	pStmtFindPK = null;
		ResultSet			rs			= null;

		boolean	hasRows = false;

		try
		{
			connection = this.getConnection();
			pStmtFindPK = connection.prepareStatement(pkQuery);
					pStmtFindPK.setLong(1, pkObj.longValue());
			rs = pStmtFindPK.executeQuery();
			if(rs.next())
			{
				hasRows = true;
			}
		}
		catch(Exception ex)
		{
			//Logger.error(FILE_NAME, " [findByPrimaryKey(routePlanId)] -> ");
      logger.error(FILE_NAME+ " [findByPrimaryKey(routePlanId)] -> ");
			throw new javax.ejb.EJBException(ex.toString());
		} 
		finally 
		{
			ConnectionUtil.closeConnection(connection, pStmtFindPK, rs);
		}
		if (hasRows)
			{ return pkObj;}
		else
			{ throw new javax.ejb.ObjectNotFoundException("Could not find bean");}
	}

  /**
   * 
   * @param routePlanHdrDOB
   * @return pkObj
   * @throws javax.ejb.CreateException
   */
	public Long create(ETMultiModeRoutePlanHdrDOB routePlanHdrDOB)
		throws CreateException
	{
		Connection connection = null;
		Long pkObj = null;

		try
		{
			connection = this.getConnection();
			insertRoutePlanHdr(connection, routePlanHdrDOB);
			insertRoutePlanDtl(connection, routePlanHdrDOB);
			pkObj = new Long(routePlanHdrDOB.getRoutePlanId());
		}
		catch(SQLException sqEx)
		{
			//Logger.error(FILE_NAME, " [create(routePlanHdrDOB)] -> "+sqEx.toString());
      logger.error(FILE_NAME+ " [create(routePlanHdrDOB)] -> "+sqEx.toString());
			throw new EJBException(sqEx.toString());
		}
		finally
		{
			ConnectionUtil.closeConnection(connection);
		}
		return pkObj;
	}

  /**
   * 
   * @param routePlanId
   * @return routePlanHdrDOB
   * @throws javax.ejb.EJBException
   */
	public ETMultiModeRoutePlanHdrDOB load(long routePlanId)
		throws EJBException
	{
		ETMultiModeRoutePlanHdrDOB routePlanHdrDOB = null;
		Connection connection = null;

		try
		{
			connection = this.getConnection();
			routePlanHdrDOB = loadRoutePlanHdr(connection, routePlanId);
		}
		catch(Exception ex)
		{
			//Logger.error(FILE_NAME, " [load()] -> "+ex.toString());
      logger.error(FILE_NAME+ " [load()] -> "+ex.toString());
			throw new EJBException(ex.toString());
		}
		finally
		{
			ConnectionUtil.closeConnection(connection);
		}
		return routePlanHdrDOB;
	}

  /**
   * 
   * @param routePlanHdrDOB
   * @throws javax.ejb.EJBException
   */
	public void store(ETMultiModeRoutePlanHdrDOB routePlanHdrDOB)
		throws EJBException
	{
		Connection connection = null;

		try
		{
			connection = this.getConnection();
			_update(connection, routePlanHdrDOB);
			
			deleteRoutePlan(connection, delQuery[1], routePlanHdrDOB.getRoutePlanId());
			insertRoutePlanDtl(connection, routePlanHdrDOB);

		}
		catch(Exception remEx) 
		{
			//Logger.error(FILE_NAME, " [store()] -> "+remEx.toString());
      logger.error(FILE_NAME+ " [store()] -> "+remEx.toString());
			throw new EJBException(remEx.toString());
		}	
		finally
		{
			ConnectionUtil.closeConnection(connection);
		}
	}
/**
   * 
   * @param routePlanHdrDOB
   * @throws javax.ejb.EJBException
   */
	public void store(ArrayList  routePlanList,String quoteid,com.foursoft.esupply.common.bean.ESupplyGlobalParameters loginBean)
		throws EJBException
	{
		Connection connection = null;

		try
		{
    
			connection = this.getConnection();
      long  routPlanId  = getRoutPlanID(quoteid,connection);
			deleteRoutePlan(connection, delQuery[1], routPlanId);
			insertRoutePlanDtl(connection, routePlanList,routPlanId,loginBean);
		}
		catch(Exception remEx) 
		{
			//Logger.error(FILE_NAME, " [store()] -> "+remEx.toString());
      logger.error(FILE_NAME+ " [store()] -> "+remEx.toString());
			throw new EJBException(remEx.toString());
		}	
		finally
		{
			ConnectionUtil.closeConnection(connection);
		}
	}
private long getRoutPlanID(String quoteid,Connection connection)
{
    PreparedStatement	    pStmt	  = null;
		ResultSet			        rs		  = null;
    long                  routeId = 0;
    try
		{
        pStmt = connection.prepareStatement("SELECT RT_PLAN_ID FROM FS_RT_PLAN WHERE QUOTE_ID='"+quoteid+"'");
        rs		= pStmt.executeQuery();
        while(rs.next())
        {
          routeId = rs.getLong("RT_PLAN_ID");
        }
       // System.out.println("routeIdrouteIdrouteId in DAO : "+routeId);
		}
		catch(SQLException sqEx)
		{
			//Logger.error(FILE_NAME, " [getRoutPlanID()] -> "+sqEx.toString());
      logger.error(FILE_NAME+ " [getRoutPlanID()] -> "+sqEx.toString());
			throw new EJBException(sqEx.toString());
		}
		finally
		{
			try
			{
        if(rs!=null)
        {  rs.close();}
				if(pStmt != null)
				{ 	pStmt.close(); }
			}
			catch(SQLException sqEx)
			{
				//Logger.error(FILE_NAME, " [getRoutPlanID()] -> "+sqEx.toString());
        logger.error(FILE_NAME+ " [getRoutPlanID()] -> "+sqEx.toString());
				throw new EJBException(sqEx.toString());
			}
		}
    return routeId;
}
 /**
   * 
   * @param connection
   * @param routePlanHdrDOB
   */
	private void insertRoutePlanDtl(Connection connection,ArrayList  routePlanList,long routeId,com.foursoft.esupply.common.bean.ESupplyGlobalParameters loginBean)
	{
      PreparedStatement	          pStmt	            = null;
      ETMultiModeRoutePlanDtlDOB  routePlanDtlDOB   = null;
      try
      {
          pStmt = connection.prepareStatement(insQuery[2]);
          if(routePlanList != null)
          {
              int routePlanListSize = routePlanList.size();
              for(int i=0; i<routePlanListSize;i++)
              {
              
                  routePlanDtlDOB = (ETMultiModeRoutePlanDtlDOB)routePlanList.get(i);
                  pStmt.setInt(1,(i+1));
                  pStmt.setLong(2,routeId);
                 // System.out.println("getLegTypegetLegTypegetLegTypegetLegTypegetLegType in DAO : "+routePlanDtlDOB.getLegType());
                  pStmt.setString(3,routePlanDtlDOB.getLegType());
                  pStmt.setString(4, routePlanDtlDOB.getOrgLoc());
                  pStmt.setString(5, routePlanDtlDOB.getDestLoc());
                  pStmt.setString(6, "");
                  pStmt.setString(7, "");
                  pStmt.setInt(8, routePlanDtlDOB.getShipmentMode());
                  pStmt.setString(9,routePlanDtlDOB.getRemarks());      
                  pStmt.executeUpdate();
            }
        }
		}
		catch(SQLException sqEx)
		{
			//Logger.error(FILE_NAME, " [insertRoutePlanDtl()] -> "+sqEx.toString());
      logger.error(FILE_NAME+ " [insertRoutePlanDtl()] -> "+sqEx.toString());
			throw new EJBException(sqEx.toString());
		}
		finally
		{
			try
			{
				if(pStmt != null)
					{ pStmt.close(); }
			}
			catch(SQLException sqEx)
			{
				//Logger.error(FILE_NAME, " [insertRoutePlanDtl()] -> "+sqEx.toString());
        logger.error(FILE_NAME+ " [insertRoutePlanDtl()] -> "+sqEx.toString());
				throw new EJBException(sqEx.toString());
			}
		}
	}
  /**
   * 
   * @param routePlanId
   * @throws javax.ejb.RemoveException
   * @throws javax.ejb.EJBException
   */
	public void remove(long routePlanId)
		throws RemoveException, EJBException
	{
		Connection connection = null;
		try
		{
			connection = this.getConnection();
			deleteRoutePlan(connection, delQuery[1], routePlanId);
			deleteRoutePlan(connection, delQuery[0], routePlanId);
		}
		catch(Exception ex)
		{
			//Logger.error(FILE_NAME, " [remove()] -> "+ex.toString());
      logger.error(FILE_NAME+ " [remove()] -> "+ex.toString());
			throw new EJBException(ex.toString());
		}
		finally
		{
			ConnectionUtil.closeConnection(connection);
		}
	}

  /**
   * 
   * @param connection
   * @param routePlanHdrDOB
   * @throws java.lang.Exception
   */
	private void _update(Connection connection, ETMultiModeRoutePlanHdrDOB routePlanHdrDOB)
		throws Exception
	{
		PreparedStatement	pStmt	= null;
		//ResultSet			rs		= null;//Commented By RajKumari on 23-10-2008 for Connection Leakages.

		try
		{
			pStmt = connection.prepareStatement(updateQuery[0]);

				if(routePlanHdrDOB.getPRQId() != null)
				{	pStmt.setString(1, routePlanHdrDOB.getPRQId()); }
				else 
					 { pStmt.setNull(1, Types.VARCHAR);}

				if(routePlanHdrDOB.getHouseDocumentId() != null)
				{	pStmt.setString(2, routePlanHdrDOB.getHouseDocumentId());}
				else 
					{pStmt.setNull(2, Types.VARCHAR);}

				pStmt.setString(3, routePlanHdrDOB.getOriginTerminalId());
				pStmt.setString(4, routePlanHdrDOB.getDestinationTerminalId());
				pStmt.setString(5, routePlanHdrDOB.getOriginLocationId());
				pStmt.setString(6, routePlanHdrDOB.getDestinationLocationId());
				pStmt.setString(7, routePlanHdrDOB.getShipperId());

				if(routePlanHdrDOB.getConsigneeId() != null)
					{ pStmt.setString(8, routePlanHdrDOB.getConsigneeId()); }
				else 
					 { pStmt.setNull(8, Types.VARCHAR); }

				pStmt.setInt(9, routePlanHdrDOB.getPrimaryMode());
				pStmt.setString(10, routePlanHdrDOB.getOverWriteFlag());
				pStmt.setTimestamp(11, routePlanHdrDOB.getCreateTimestamp());
				pStmt.setTimestamp(12, routePlanHdrDOB.getLastUpdateTimestamp());
				pStmt.setString(13, routePlanHdrDOB.getShipmentStatus());
				pStmt.setLong(14, routePlanHdrDOB.getRoutePlanId());
			pStmt.executeUpdate();
		}
		catch(SQLException sqEx)
		{
			//Logger.error(FILE_NAME, " [_update(connection, routePlanHdrDOB)] -> "+sqEx.toString());
      logger.error(FILE_NAME+ " [_update(connection, routePlanHdrDOB)] -> "+sqEx.toString());
			throw new EJBException(sqEx.toString());
		}
		finally
		{
			try
			{
				if(pStmt != null)
				{ 	pStmt.close(); }
			}
			catch(SQLException sqEx)
			{
				//Logger.error(FILE_NAME, " [_update(connection, routePlanHdrDOB)] -> "+sqEx.toString());
        logger.error(FILE_NAME+ " [_update(connection, routePlanHdrDOB)] -> "+sqEx.toString());
				throw new EJBException(sqEx.toString());
			}
		}
	}

  /**
   * 
   * @param out
   * @throws java.io.IOException
   */
    //@@ Commented by Sreelakshmi KVA -TogetherJ-UPCM
   /*
	private void writeObject(java.io.ObjectOutputStream out)
		throws IOException 
	{
		out.defaultWriteObject();
	}*/

  /**
   * 
   * @param in
   * @throws java.io.IOException
   * @throws java.lang.ClassNotFoundException
   */
   /*
	private void readObject(java.io.ObjectInputStream in)
		throws IOException, ClassNotFoundException 
	{
		in.defaultReadObject();
	}*/
	//@@

  /**
   * 
   * @param connection
   * @param routePlanId
   * @return routePlanHdrDOB
   */
	private ETMultiModeRoutePlanHdrDOB loadRoutePlanHdr(Connection connection, long routePlanId)
	{
		PreparedStatement	pStmt	= null;
		ResultSet			rs		= null;

		ETMultiModeRoutePlanHdrDOB routePlanHdrDOB = null;

		try
		{
			pStmt = connection.prepareStatement(loadQuery[0]);
				pStmt.setLong(1, routePlanId);
			rs = pStmt.executeQuery();
			while(rs.next())
			{
				String originTerminal		= rs.getString(3);
				String destinationTerminal	= rs.getString(4);
				String originLocation		= rs.getString(5);
				String destinationLocation	= rs.getString(6);

				routePlanHdrDOB = new ETMultiModeRoutePlanHdrDOB(rs.getLong(1), rs.getString(2),"", originTerminal, destinationTerminal, originLocation, destinationLocation, rs.getString(7), rs.getString(8), rs.getInt(9), rs.getString(10), rs.getTimestamp(11), rs.getTimestamp(12), rs.getString(13));
				routePlanHdrDOB.setOriginTerminalLocation(getLocationName("Terminal", originTerminal));
				routePlanHdrDOB.setDestinationTerminalLocation(getLocationName("Terminal", destinationTerminal));
				routePlanHdrDOB.setOriginLocationName(getLocationName("Location", originLocation));
				routePlanHdrDOB.setDestinationLocationName(getLocationName("Location", destinationLocation));
			}
			routePlanHdrDOB.setRoutePlanDtlDOB(loadRoutePlanDtl(connection, routePlanId));
		}
		catch(SQLException sqEx)
		{
			//Logger.error(FILE_NAME, " [loadRoutePlanHdr(connection, routePlanId)] -> "+sqEx.toString());
      logger.error(FILE_NAME+ " [loadRoutePlanHdr(connection, routePlanId)] -> "+sqEx.toString());
			throw new EJBException(sqEx.toString());
		}
		finally
		{
			try
			{
				if(rs != null)
					{ rs.close(); }
				if(pStmt != null)
					{ pStmt.close(); }
			}
			catch(SQLException sqEx)
			{
				//Logger.error(FILE_NAME, " [loadRoutePlanHdr(connection, routePlanId)] -> "+sqEx.toString());
        logger.error(FILE_NAME+ " [loadRoutePlanHdr(connection, routePlanId)] -> "+sqEx.toString());
				throw new EJBException(sqEx.toString());
			}
		}
		return routePlanHdrDOB;
	}

  /**
   * 
   * @param connection
   * @param routePlanId
   * @return routePlanDtlDOB
   */
	private ETMultiModeRoutePlanDtlDOB[] loadRoutePlanDtl(Connection connection, long routePlanId)
	{
		PreparedStatement	pStmt	= null;
		ResultSet			rs		= null;

		ETMultiModeRoutePlanDtlDOB[] routePlanDtlDOB = null;

		try
		{
			pStmt = connection.prepareStatement(loadQuery[1]);
				pStmt.setLong(1, routePlanId);
			rs = pStmt.executeQuery();

			int count = 0;

			while(rs.next())
		 {		count++; }

			routePlanDtlDOB = new ETMultiModeRoutePlanDtlDOB[count];
			rs = pStmt.executeQuery();
			count = 0;

			while(rs.next())
			{
				String originTerminal = rs.getString(3);
				String destinationTerminal = rs.getString(4);
				String legType = rs.getString(9);				routePlanDtlDOB[count] = new ETMultiModeRoutePlanDtlDOB(rs.getInt(1), rs.getLong(2), originTerminal, destinationTerminal, rs.getInt(5), rs.getString(6), rs.getString(7), rs.getString(8), legType, rs.getString(10));


				routePlanDtlDOB[count].setPiecesReceived(rs.getInt(11));
				routePlanDtlDOB[count].setReceivedDate(rs.getTimestamp(12));
				routePlanDtlDOB[count].setRemarks(rs.getString(13));
				routePlanDtlDOB[count].setCostAmount(rs.getDouble(14));

                routePlanDtlDOB[count].setMasterDocId(rs.getString(8));
				if(!(legType.equals("LT") && legType.equals("TL")))
				{
					routePlanDtlDOB[count].setOriginTerminalLocation(rs.getString(15));
					routePlanDtlDOB[count].setDestinationTerminalLocation(rs.getString(16));

					if(routePlanDtlDOB[count].getMasterDocId() != null && !routePlanDtlDOB[count].getMasterDocId().equals(""))
					{
						routePlanDtlDOB[count].setCarrier(getCarrier(routePlanDtlDOB[count].getMasterDocId(), routePlanDtlDOB[count].getShipmentMode()));
						routePlanDtlDOB[count].setETD(getETD(routePlanDtlDOB[count].getMasterDocId(), routePlanDtlDOB[count].getShipmentMode()));
						routePlanDtlDOB[count].setETA(getETA(routePlanDtlDOB[count].getMasterDocId(), routePlanDtlDOB[count].getShipmentMode()));
					}

				}
				count++;
			}
		}
		catch(SQLException sqEx)
		{
			//Logger.error(FILE_NAME, " [loadRoutePlanDtl(connection, routePlanId)] -> "+sqEx.toString());
      logger.error(FILE_NAME+ " [loadRoutePlanDtl(connection, routePlanId)] -> "+sqEx.toString());
			throw new EJBException(sqEx.toString());
		}
		finally
		{
			try
			{
				if(rs != null)
				{ 	rs.close(); }
				if(pStmt != null)
					{ pStmt.close(); }
			}
			catch(SQLException sqEx)
			{
				//Logger.error(FILE_NAME, " [loadRoutePlanDtl(connection, routePlanId)] -> "+sqEx.toString());\
        logger.error(FILE_NAME+ " [loadRoutePlanDtl(connection, routePlanId)] -> "+sqEx.toString());
				throw new EJBException(sqEx.toString());
			}
		}
		return routePlanDtlDOB;
	}

  /**
   * 
   * @param connection
   * @param routePlanHdrDOB
   */
	private void insertRoutePlanHdr(Connection connection, ETMultiModeRoutePlanHdrDOB routePlanHdrDOB)
	{
		PreparedStatement	pStmt	= null;
		//ResultSet			rs		= null;//Commented By RajKumari on 23-10-2008 for Connection Leakages.

		try
		{
			pStmt = connection.prepareStatement(insQuery[0]);
				pStmt.setLong(1, routePlanHdrDOB.getRoutePlanId());

				if(routePlanHdrDOB.getPRQId() != null)
					{ pStmt.setString(2, routePlanHdrDOB.getPRQId()); }
				else 
        { pStmt.setNull(2, Types.VARCHAR);}

				if(routePlanHdrDOB.getHouseDocumentId() != null)
					pStmt.setString(3, routePlanHdrDOB.getHouseDocumentId());
				else pStmt.setNull(3, Types.VARCHAR);

				pStmt.setString(4, routePlanHdrDOB.getOriginTerminalId());
				pStmt.setString(5, routePlanHdrDOB.getDestinationTerminalId());
				pStmt.setString(6, routePlanHdrDOB.getOriginLocationId());
				pStmt.setString(7, routePlanHdrDOB.getDestinationLocationId());
				pStmt.setString(8, routePlanHdrDOB.getShipperId());

				if(routePlanHdrDOB.getConsigneeId() != null)
				{	pStmt.setString(9, routePlanHdrDOB.getConsigneeId()); }
				else { pStmt.setNull(9, Types.VARCHAR); }

				pStmt.setInt(10, routePlanHdrDOB.getPrimaryMode());
				pStmt.setString(11, routePlanHdrDOB.getOverWriteFlag());
				pStmt.setTimestamp(12, routePlanHdrDOB.getCreateTimestamp());
				pStmt.setTimestamp(13, routePlanHdrDOB.getLastUpdateTimestamp());
				pStmt.setString(14, routePlanHdrDOB.getShipmentStatus());
			pStmt.executeUpdate();
		}
		catch(SQLException sqEx)
		{
			//Logger.error(FILE_NAME, " [insertRoutePlanHdr(connection, routePlanHdrDOB)] -> "+sqEx.toString());
      logger.error(FILE_NAME+ " [insertRoutePlanHdr(connection, routePlanHdrDOB)] -> "+sqEx.toString());
			throw new EJBException(sqEx.toString());
		}
		finally
		{
			try
			{
				if(pStmt != null)
					{ pStmt.close(); }
			}
			catch(SQLException sqEx)
			{
				//Logger.error(FILE_NAME, " [insertRoutePlanHdr(connection, routePlanHdrDOB)] -> "+sqEx.toString());
        logger.error(FILE_NAME+ " [insertRoutePlanHdr(connection, routePlanHdrDOB)] -> "+sqEx.toString());
				throw new EJBException(sqEx.toString());
			}
		}
	}

  /**
   * 
   * @param connection
   * @param routePlanHdrDOB
   */
	private void insertRoutePlanDtl(Connection connection, ETMultiModeRoutePlanHdrDOB routePlanHdrDOB)
	{
		PreparedStatement	pStmt	= null;
		//ResultSet			rs		= null;//Commented By RajKumari on 23-10-2008 for Connection Leakages.
		ETMultiModeRoutePlanDtlDOB[] routePlanDtlDOB = routePlanHdrDOB.getRoutePlanDtlDOB();

		try
		{
			pStmt = connection.prepareStatement(insQuery[1]);

			if(routePlanDtlDOB != null)
			{
				for(int i=0; i<routePlanDtlDOB.length; i++)
				{
					if(routePlanDtlDOB[i] != null)
					{
						pStmt.setInt(1, (i+1));
						pStmt.setLong(2, routePlanHdrDOB.getRoutePlanId());
						pStmt.setString(3, routePlanDtlDOB[i].getOriginTerminalId());
						pStmt.setString(4, routePlanDtlDOB[i].getDestinationTerminalId());
						pStmt.setInt(5, routePlanDtlDOB[i].getShipmentMode());
						pStmt.setString(6, routePlanDtlDOB[i].getShipmentStatus());
						pStmt.setString(7, routePlanDtlDOB[i].getAutoManualFlag());

						if(routePlanDtlDOB[i].getMasterDocId() != null)
							{ pStmt.setString(8, routePlanDtlDOB[i].getMasterDocId()); }
						else
							 { pStmt.setNull(8, Types.VARCHAR); }
						pStmt.setString(9, routePlanDtlDOB[i].getLegType());
						pStmt.setString(10, routePlanDtlDOB[i].getLegValidFlag());
						pStmt.setInt(11, routePlanDtlDOB[i].getPiecesReceived());

						if(routePlanDtlDOB[i].getReceivedDate() != null)
						{	pStmt.setTimestamp(12, routePlanDtlDOB[i].getReceivedDate()); }
						else { pStmt.setNull(12, Types.TIMESTAMP); }

						if(routePlanDtlDOB[i].getRemarks() != null)
							 { pStmt.setString(13, routePlanDtlDOB[i].getRemarks()); }
						else { pStmt.setNull(13, Types.VARCHAR); }

						pStmt.setDouble(14, routePlanDtlDOB[i].getCostAmount());

						pStmt.executeUpdate();
					}
				}
			}
		}
		catch(SQLException sqEx)
		{
			//Logger.error(FILE_NAME, " [insertRoutePlanDtl(connection, routePlanDtlDOB)] -> "+sqEx.toString());
      logger.error(FILE_NAME+ " [insertRoutePlanDtl(connection, routePlanDtlDOB)] -> "+sqEx.toString());
			throw new EJBException(sqEx.toString());
		}
		finally
		{
			try
			{
				if(pStmt != null)
					{ pStmt.close(); }
			}
			catch(SQLException sqEx)
			{
				//Logger.error(FILE_NAME, " [insertRoutePlanDtl(connection, routePlanDtlDOB)] -> "+sqEx.toString());
        logger.error(FILE_NAME+" [insertRoutePlanDtl(connection, routePlanDtlDOB)] -> "+sqEx.toString());
				throw new EJBException(sqEx.toString());
			}
		}
	}




  /**
   * 
   * @param connection
   * @param sql
   * @param routePlanId
   */
	private void deleteRoutePlan(Connection connection, String sql, long routePlanId)
	{
		PreparedStatement	stmt	= null;

		try
		{
			stmt = connection.prepareStatement(sql);
				stmt.setLong(1, routePlanId);
			stmt.executeUpdate();
		}
		catch(SQLException sqEx)
		{
			//Logger.error(FILE_NAME, " [deleteRoutePlan(connection, sql)] -> "+sqEx.toString());
      logger.error(FILE_NAME+ " [deleteRoutePlan(connection, sql)] -> "+sqEx.toString());
			throw new EJBException(sqEx.toString());
		}
		finally
		{
			try
			{
				if(stmt != null)
					{ stmt.close(); }
			}
			catch(SQLException sqEx)
			{
				//Logger.error(FILE_NAME, " [deleteRoutePlan(connection, sql, routePlanId)] -> "+sqEx.toString());
        logger.error(FILE_NAME+ " [deleteRoutePlan(connection, sql, routePlanId)] -> "+sqEx.toString());
				throw new EJBException(sqEx.toString());
			}
		}
	}

  /**
   * 
   * @param type
   * @param locationId
   * @return locationName
   */
	private String getLocationName(String type, String locationId)
	{
		Connection connection = null;
		Statement stmt = null;
		ResultSet rs = null;
		String sql = "";
		String locationName = null;

		try
		{
			if(type.equals("Location"))
		{		sql = "SELECT CITY FROM FS_FR_LOCATIONMASTER WHERE LOCATIONID='"+locationId+"'"; }
			else if(type.equals("Terminal"))
				{ sql = "SELECT A.CITY FROM FS_ADDRESS A, FS_FR_TERMINALMASTER T WHERE T.TERMINALID='"+locationId+"' AND T.CONTACTADDRESSID = A.ADDRESSID";}
			else if(type.equals("Gateway"))
				{ sql = "SELECT A.CITY FROM FS_ADDRESS A, FS_FR_GATEWAYMASTER G WHERE G.GATEWAYID='"+locationId+"' AND G.CONTACTADDRESSID = A.ADDRESSID";}

			//Logger.info(FILE_NAME, "SQL -> "+sql);

			connection = this.getConnection();
			stmt = connection.createStatement();
			rs = stmt.executeQuery(sql);
			while(rs.next())
				 { locationName = rs.getString(1); }
		}
		catch(Exception ex)
		{
			//Logger.error(FILE_NAME, " [getLocationName(type, terminalId)] -> ", ex);
      logger.error(FILE_NAME+ " [getLocationName(type, terminalId)] -> "+ ex);
			throw new EJBException(ex.toString());
		}
		finally
		{
			ConnectionUtil.closeConnection(connection, stmt, rs);
		}
		return locationName;
	}

  /**
   * 
   * @param masterDocNo
   * @param shipmentMode
   * @return carrier
   */
	private String getCarrier(String masterDocNo, int shipmentMode)
	{
		Connection connection = null;
		Statement stmt = null;
		ResultSet rs = null;
		String sql = "";
		String carrier = null;

		try
		{
			if(shipmentMode == 1 || shipmentMode == 4)
		{		sql = "SELECT CARRIERID FROM FS_FR_MASTERDOCHDR WHERE MASTERDOCID='"+masterDocNo+"'"; }
			else if(shipmentMode == 2)
				{sql = "SELECT D.CARRIERID FROM FS_FRS_CONSOLEMASTER M, FS_FRS_CONSOLEVESSELDTL D WHERE M.CONSOLEID='"+masterDocNo+"' AND D.CONSOLEID=M.CONSOLEID";}

			//Logger.info(FILE_NAME, "SQL -> "+sql);

			connection = this.getConnection();
			stmt = connection.createStatement();
			rs = stmt.executeQuery(sql);
			while(rs.next())
			{ 	carrier = rs.getString(1); }
		}
		catch(Exception ex)
		{
			//Logger.error(FILE_NAME, " [getCarrier(type, terminalId)] -> ", ex);
      logger.error(FILE_NAME+ " [getCarrier(type, terminalId)] -> "+ ex);
			throw new EJBException(ex.toString());
		}
		finally
		{
			ConnectionUtil.closeConnection(connection, stmt, rs);
		}
		return carrier;
	}

	private Timestamp getETD(String masterDocNo, int shipmentMode)
	{
		Connection connection = null;
		Statement stmt = null;
		ResultSet rs = null;
		String sql = "";
		Timestamp ETD = null;

		try
		{
			if(shipmentMode == 1 || shipmentMode == 4)
				sql = "SELECT F.ETD FROM FS_FR_MASTERDOCHDR M, FS_FR_MASTERFLIGHTDTL F WHERE M.MASTERDOCID='"+masterDocNo+"' AND F.MASTERDOCID = M.MASTERDOCID";
			else if(shipmentMode == 2)
				sql = "SELECT D.ETD FROM FS_FRS_CONSOLEMASTER M, FS_FRS_CONSOLEVESSELDTL D WHERE M.CONSOLEID='"+masterDocNo+"' AND D.CONSOLEID=M.CONSOLEID";

			//Logger.info(FILE_NAME, "SQL -> "+sql);

			connection = this.getConnection();
			stmt = connection.createStatement();
			rs = stmt.executeQuery(sql);
			while(rs.next())
			{	ETD = rs.getTimestamp(1); }
		}
		catch(Exception ex)
		{
			//Logger.error(FILE_NAME, " [getETD(type, terminalId)] -> ", ex);
      logger.error(FILE_NAME+ " [getETD(type, terminalId)] -> "+ ex);
			throw new EJBException(ex.toString());
		}
		finally
		{
			ConnectionUtil.closeConnection(connection, stmt, rs);
		}
		return ETD;
	}

	private Timestamp getETA(String masterDocNo, int shipmentMode)
	{
		Connection connection = null;
		Statement stmt = null;
		ResultSet rs = null;
		String sql = "";
		Timestamp ETA = null;

		try
		{
			if(shipmentMode == 1 || shipmentMode == 4)
				{ sql = "SELECT F.ETA FROM FS_FR_MASTERDOCHDR M, FS_FR_MASTERFLIGHTDTL F WHERE M.MASTERDOCID='"+masterDocNo+"' AND F.MASTERDOCID = M.MASTERDOCID"; }
			else if(shipmentMode == 2)
				{ sql = "SELECT D.ETA FROM FS_FRS_CONSOLEMASTER M, FS_FRS_CONSOLEVESSELDTL D WHERE M.CONSOLEID='"+masterDocNo+"' AND D.CONSOLEID=M.CONSOLEID"; }

			//Logger.info(FILE_NAME, "SQL -> "+sql);

			connection = this.getConnection();
			stmt = connection.createStatement();
			rs = stmt.executeQuery(sql);

			int cnt = 0;
			while(rs.next())
			{
				if(cnt == 0)
				{	ETA = rs.getTimestamp(1); }
			}
		}
		catch(Exception ex)
		{
			//Logger.error(FILE_NAME, " [getETA(type, terminalId)] -> ", ex);
      logger.error(FILE_NAME+ " [getETA(type, terminalId)] -> "+ ex);
			throw new EJBException(ex.toString());
		}
		finally
		{
			ConnectionUtil.closeConnection(connection, stmt, rs);
		}
		return ETA;
	}

	private Connection getConnection() throws SQLException
	{
		return dataSource.getConnection();
	}
}
