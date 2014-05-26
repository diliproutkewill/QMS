/**
 * 
 * Copyright (c) 2000-2001 by FourSoft, Inc. All Rights Reserved.
 * This software is the proprietary information of FourSoft, Pvt Ltd.
 * Use is subject to license terms.
 *
 * esupply - v 1.x 
 *
 */
package com.foursoft.esupply.common.util;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.ejb.EJBException;

/**
 * File			: ConnectionUtil.java
 * sub-module	: common
 * module		: esupply
 *
 * This is an Utility Class.
 * This class is used to close the connection, statement and Results 
 *
 * @author	Madhusudhan Rao. P, 
 * @date	28-08-2001
 */

 /**
 * Method summery
 *
 * closeConnection( Connection conn, Statement st )
 * closeConnection( Connection conn, Statement[] st )
 * closeConnection( Connection conn )
 * closeStatement( Statement st )
 */

public class ConnectionUtil
{
	
	/**
	 * Method closeConnection
	 * This is used to close the connection and  statement /prepared statement
	 *
	 * @param conn	 
	 * @param st	 
	 * @exception javax.ejb.EJBException
	 */
	public static void closeConnection( Connection conn, Statement st )
	{
		boolean closeError = false;
		if( st != null )
		{
			try
			{
				st.close();
				st = null;
			}
			catch( SQLException e )
			{
				e.printStackTrace( System.err );
				closeError = true;
			}
		}
		if( conn != null )
		{
			try
			{
				conn.close();
				conn = null;
			}
			catch( SQLException e )
			{
				e.printStackTrace( System.err );
				closeError = true;
			}
		}
		if( closeError )
		{
			throw new EJBException( "Exceptions trying to close connection!" );
		}
	}// end of closeConnection( Connection conn, Statement st )
	

	public static void closeConnection( Connection conn, Statement st, ResultSet rs)
	{
		boolean closeError = false;

		if( rs != null )
		{
			try
			{
				rs.close();
				rs = null;
			}
			catch( SQLException e )
			{
				e.printStackTrace( System.err );
				closeError = true;
			}
		}

		if( st != null )
		{
			try
			{
				st.close();
				st = null;
			}
			catch( SQLException e )
			{
				e.printStackTrace( System.err );
				closeError = true;
			}
		}
		if( conn != null )
		{
			try
			{
				conn.close();
				conn = null;
			}
			catch( SQLException e )
			{
				e.printStackTrace( System.err );
				closeError = true;
			}
		}
		if( closeError )
		{
			throw new EJBException( "Exceptions trying to close connection!" );
		}
	}// end of closeConnection( Connection conn, Statement st )

	public static void closePreparedStatement(PreparedStatement pStmt, ResultSet rs)
	{
		boolean closeError = false;

		if( rs != null )
		{
			try
			{
				rs.close();
				rs = null;
			}
			catch( SQLException e )
			{
				e.printStackTrace( System.err );
				closeError = true;
			}
		}

		if( pStmt != null )
		{
			try
			{
				pStmt.close();
				pStmt = null;
			}
			catch( SQLException e )
			{
				e.printStackTrace( System.err );
				closeError = true;
			}
		}
		if( closeError )
		{
			throw new EJBException( "Exceptions trying to close connection!" );
		}
	}// end of closeConnection( PreparedStatement, ResultSet) 
	


	/**
	 * Method closeConnection
	 * This is used to close the connection and  statement /prepared statement
	 *
	 * @param conn	 
	 * @param st - statement array	 
	 *
	 */
	public static void closeConnection( Connection conn, Statement[] st )
	{
		boolean closeError = false;
		int stLen	=	st.length;
		for( int i = 0;i < stLen;i++ )
		{
			if( st[i] != null )
			{
				try
				{
					st[i].close();
					st[i] = null;
				}
				catch( SQLException e )
				{
					e.printStackTrace( System.err );
					closeError = true;
				}
			}
		}
		if( conn != null )
		{
			try
			{
				conn.close();
				conn = null;
			}
			catch( SQLException e )
			{
				e.printStackTrace( System.err );
				closeError = true;
			}
		}
		if( closeError )
		{
			throw new EJBException( "Exceptions trying to close connection!" );
		}
	} //closeConnection( Connection conn, Statement[] st )
	
	/**
	 * Method closeConnection
	 * This is used to close the connection
	 *
	 * @param conn - connection
	 * @exception javax.ejb.EJBException
	 */
	public static void closeConnection( Connection conn )
	{
		boolean closeError = false;
		if( conn != null )
		{
			try
			{
				conn.close();
				conn = null;
			}
			catch( SQLException e )
			{
				e.printStackTrace( System.err );
				closeError = true;
			}
		}
		if( closeError )
		{
			throw new EJBException( "Exceptions trying to close connection!" );
		}
	} // closeConnection( Connection conn )
	
	/**
	 * Method closeStatement
	 * This is used to close the statement and prepared statement
	 *
	 * @param st
	 * @exception javax.ejb.EJBException
	 */
	public static void closeStatement( Statement st )
	{
		boolean closeError = false;
		if( st != null )
		{
			try
			{
				st.close();
				st = null;
			}
			catch( SQLException e )
			{
				e.printStackTrace( System.err );
				closeError = true;
			}
		}
		if( closeError )
		{
			throw new EJBException( "Exceptions trying to close connection!" );
		}
	} //closeStatement( Statement st )


	/**
	 * Method closeStatement
	 * This is used to close the statement and prepared statement
	 *
	 * @param Statement
	 * @param ResultSet
	 * @exception javax.ejb.EJBException
	 */
	public static void closeStatement( Statement st , ResultSet rs)
	{
		boolean closeError = false;
		if( rs != null )
		{
			try
			{
				rs.close();
				rs = null;
			}
			catch( SQLException e )
			{
				e.printStackTrace( System.err );
				closeError = true;
			}
		}
        if( st != null )
		{
			try
			{				
				st.close();
				st = null;
			}
			catch( SQLException e )
			{
				e.printStackTrace( System.err );
				closeError = true;
			}
		}
		if( closeError )
		{
			throw new EJBException( "Exceptions trying to close connection!" );
		}
	} //closeStatement( Statement st , ResultSet rs)	
	private ConnectionUtil() 
	{
		
	}
  public static void closePreparedStatement(PreparedStatement pStmt)
	{
		boolean closeError = false;

		if( pStmt != null )
		{
			try
			{
				pStmt.close();
				pStmt = null;
			}
			catch( SQLException e )
			{
				e.printStackTrace( System.err );
				closeError = true;
			}
		}
		if( closeError )
		{
			throw new EJBException( "Exceptions trying to close PreparedStatement!" );
		}
	}// end of closeConnection( PreparedStatement, ResultSet)
}
