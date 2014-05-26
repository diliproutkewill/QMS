/**
 * 
 * Copyright (c) 2000-2001 by FourSoft, Inc. All Rights Reserved.
 * This software is the proprietary information of FourSoft, Pvt Ltd.
 * Use is subject to license terms.
 *
 * esupply - v 1.x 
 *
 */
package com.foursoft.esupply.common.dao;

//import com.foursoft.esupply.common.util.Logger;
import org.apache.log4j.Logger;
import com.foursoft.esupply.common.util.ConnectionUtil;
import com.foursoft.esupply.common.java.UserLogVOB;
import com.foursoft.esupply.common.exception.DBSysException;
import com.foursoft.esupply.common.java.FoursoftConfig;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.naming.InitialContext;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.ejb.EJBException;
 
/**
 * File			: UserLogDAO.java
 * sub-module	: common
 * module		: esupply
 *
 * This is a DataAccessObjects
 * This class encapsulates all the JDBC calls made to create entry in UserLog
 *
 * @author	Madhusudhan Rao. P, 
 * @date	28-08-2001
 *
 * Modified History 
 * Amit Parekh	21/10/2002		The global datasource name 'jdbc/DB' or otherwise is now
 *								maintained globally in FourSoftConfig.java
 *                        		Also the Home lookup-once-and-cache pattern is implemented
 */

 /**
 * Method summery
 *
 * setTransactionDetails(UserLogVOB userLogVOB) throws EJBException
 *
 */
public class UserLogDAO {

//    private Connection connection = null;
	private DataSource dataSource = null;
  private static Logger logger = null;

	
	/**
	* Empty constructor, in which we will initialize the resources datasource
	*/
	public UserLogDAO()
	{
    logger  = Logger.getLogger(UserLogDAO.class);
		try
		{
			Context initialContext = new InitialContext();
			dataSource = (DataSource)initialContext.lookup( FoursoftConfig.DATA_SOURCE );
		}
		catch( Exception e )
		{
			logger.error("UserLogDAO"+ "UserLogDAO()"+ "unable to create the datasource "+e);		
			throw new EJBException("Unable To create the database");
		}

	}
	
	/**
	* This is used to set the transaction details
	* @param userLogVOB
	*
	*/
	public void setTransactionDetails(UserLogVOB userLogVOB) throws DBSysException
	{
		Connection connection = null;
		PreparedStatement pstmt = null;
		try
		{
			connection = getConnection();
			String sql = "INSERT INTO FS_USERLOG(LOCATIONID, USERID, DOCTYPE, DOCREFNO, DOCDATE, TRANSACTIONTYPE ) " + "VALUES(?,?,?,?,?,?)";

			//Logger.info("UserLogDAO", "setTransactionDetails()", "QUERY -- "+sql);
			
			pstmt = connection.prepareStatement( sql );
			pstmt.setString( 1, userLogVOB.locationId );
			pstmt.setString( 2, userLogVOB.userId );
			pstmt.setString( 3, userLogVOB.documentType );
			pstmt.setString( 4, userLogVOB.documentRefNo );
			pstmt.setTimestamp(5, userLogVOB.documentDate );
			pstmt.setString( 6, userLogVOB.transactionType );
			
			pstmt.executeUpdate();
		
		}
		catch( SQLException e )
		{
			//Logger.error("UserLogDAO", "setTransactionDetails()", e.getMessage());
      logger.error("UserLogDAO"+ "setTransactionDetails()"+ e.getMessage());
			throw new DBSysException("Error in Inserting in User Log ");
		}
		finally
		{
			ConnectionUtil.closeConnection(connection, pstmt);
		}
	} //end of setting transaction details
	
	/**
	* is private method which is used to get the connection
	*/
	private Connection getConnection()
	{
		Connection con = null;
		try
		{
			con = dataSource.getConnection();
		}
		catch( Exception ex )
		{
			//Logger.error("UserLogDAO", "getConnection()", "Error in getting the connection ", ex);			
      logger.error("UserLogDAO"+ "getConnection()"+ "Error in getting the connection "+ ex);			
			throw new EJBException( "Error in getting the connection from datasource ");
		}
		return con;
	} // end of getConnection()
	 

}
