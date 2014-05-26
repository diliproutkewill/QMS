/**
 * @ (#) CountryManagerBean.java
 * Copyright (c) 2001 The Four Soft Pvt Ltd., 
 * 5Q1A3, Cyber Towers, 5th floor, HiTec City, Madhapur, Hyderabad - 33.
 * All rights reserved.
 *
 * This Software is the Confidential and proprietary information of Four Soft Pvt Ltd.
 * ("Confidential Information"). You shall not disclose such Confidential Information
 * and shall use it only in accordance with the terms of the license agreement
 * you entered into with Four Soft.
 */

/**
 * File : CountryManagerBean.java
 * Sub-Module : CountryManager
 * Module : esupply
 * @author : Sowmindra.R
 * @data 25-02-2002
 * Modified by      Date     Reason
 */

package com.foursoft.esupply.common.ejb.sls;

import com.foursoft.esupply.common.java.CountryMasterDOB;
import com.foursoft.esupply.common.java.UserLogVOB;
import com.foursoft.esupply.common.dao.UserLogDAO;

import java.rmi.RemoteException;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;
import javax.ejb.CreateException;

import javax.naming.InitialContext;

import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.sql.SQLException;

import java.util.ArrayList;

public class CountryManagerBean	implements SessionBean
{

	private  SessionContext sessionContext = null;
	private  InitialContext ic = null;
	private  DataSource     dataSource = null;

	public UserLogVOB userLogVOB = null;
	public UserLogDAO userLogDAO = null;

	//Place your business methods here.
    
	/**
	 * This is insertCountry()  method 
	 * This method is used to  insert the data in contrymaster
	 * @param locationId
	 * @param loginId
	 * @param txDate
	 * @param CountryMasterDOB object
	*/
	public void insertCountry(String locationId, String loginId, Timestamp txDate, CountryMasterDOB countryDOB)
	{
		Connection connection = null;
		PreparedStatement pStmt = null;
		try
		{
			connection = this.getConnection();
			pStmt = connection.prepareStatement("INSERT INTO FS_COUNTRYMASTER(COUNTRYID, COUNTRYNAME, CURRENCYID, REGION) VALUES (?,?,?,?)");
			pStmt.setString(1, countryDOB.getCountryId());
			pStmt.setString(2, countryDOB.getCountryName());
			pStmt.setString(3, countryDOB.getCurrencyId());
			pStmt.setString(4, countryDOB.getRegion());
			
			pStmt.executeUpdate();
			
			ic = new InitialContext();
			userLogVOB = new UserLogVOB(locationId, loginId, "COUNTRY MASTER", countryDOB.getCountryId(), txDate, "ADD");
			userLogDAO = new UserLogDAO();
			userLogDAO.setTransactionDetails(userLogVOB);

		}
		catch(Exception ex)
		{
			System.out.println("Exception in insertRecord() - CountryMasterBean "+ex);
			throw new javax.ejb.EJBException(ex.toString());
		}
		finally
		{
			try
			{
				if(pStmt!=null)
					pStmt.close();
				if(connection!=null)
					connection.close();
			}
			catch(SQLException sqEx)
			{
				System.out.println("Exception in Closing Connection insertCountry() - CountryMasterBean "+sqEx);
			}
		}
	}
	
	/**
	 * This is updateCountry()  method 
	 * This method is used to  update the data in to contrymaster
	 * @param locationId
	 * @param loginId
	 * @param txDate
	 * @param CountryMasterDOB object
	*/
	public void updateCountry(String locationId, String loginId, Timestamp txDate, CountryMasterDOB countryDOB)
	{
		Connection connection = null;
		PreparedStatement pStmt = null;
		try
		{
			connection = this.getConnection();
			pStmt = connection.prepareStatement("UPDATE FS_COUNTRYMASTER SET COUNTRYNAME = ?, CURRENCYID = ?, REGION = ? WHERE COUNTRYID = ?");
			pStmt.setString(1, countryDOB.getCountryName());
			pStmt.setString(2, countryDOB.getCurrencyId());
			pStmt.setString(3, countryDOB.getRegion());
			pStmt.setString(4, countryDOB.getCountryId());
			
			pStmt.executeUpdate();
			
			ic = new InitialContext();
			userLogVOB = new UserLogVOB(locationId, loginId, "COUNTRY MASTER", countryDOB.getCountryId(), txDate, "MODIFY");
			userLogDAO = new UserLogDAO();
			userLogDAO.setTransactionDetails(userLogVOB);
		}
		catch(Exception ex)
		{
			System.out.println("Exception in updateCountry() - CountryMasterBean "+ex);
			throw new javax.ejb.EJBException(ex.toString());
		}
		finally
		{
			try
			{
				if(pStmt!=null)
					pStmt.close();
				if(connection!=null)
					connection.close();
			}
			catch(SQLException sqEx)
			{
				System.out.println("Exception in Closing Connection updateCountry() - CountryMasterBean "+sqEx);
			}
		}
	  }
    /**
	 * This is deleteCountry()  method 
	 * This method is used to  delete the data from contrymaster
	 * @param locationId
	 * @param loginId
	 * @param txDate
	 * @param countryId
    */
	public void deleteCountry(String locationId, String loginId, Timestamp txDate, String countryId)
	{
		Connection connection = null;
		PreparedStatement pStmt = null;
		try
		{
			connection = this.getConnection();
			pStmt = connection.prepareStatement("DELETE FROM FS_COUNTRYMASTER WHERE COUNTRYID = ?");
			pStmt.setString(1, countryId);
			
			pStmt.executeUpdate();
			
			ic = new InitialContext();			
			userLogVOB = new UserLogVOB(locationId, loginId, "COUNTRY MASTER", countryId, txDate, "DELETE");
			userLogDAO = new UserLogDAO();
			userLogDAO.setTransactionDetails(userLogVOB);
		}
		catch(Exception ex)
		{
			System.out.println("Exception in deleteCountry() - CountryMasterBean "+ex);
			throw new javax.ejb.EJBException(ex.toString());
		}
		finally
		{
			try
			{
				if(pStmt!=null)
					pStmt.close();
				if(connection!=null)
					connection.close();
			}
			catch(SQLException sqEx)
			{
				System.out.println("Exception in Closing Connection deleteCountry() - CountryMasterBean "+sqEx);
			}
		}
	}

	/**
	 * This method is used to get the CountryDetails based on the CountryId.
	 * @param java.lang.String countryId to be provided.
	 * @erturn java.util.ArrayList  
	 */	
	public ArrayList getCountryDetails(String countryId)
	{
		Connection connection = null;
		PreparedStatement pStmt = null;
		ResultSet rs = null;
		ArrayList al = new ArrayList();
		CountryMasterDOB cDOB = null;
		try
		{
			connection = this.getConnection();
			pStmt = connection.prepareStatement("SELECT COUNTRYNAME, CURRENCYID, REGION FROM FS_COUNTRYMASTER WHERE COUNTRYID = ?");
			pStmt.setString(1, countryId);
			rs = pStmt.executeQuery();
			if(rs.next())
			{
				cDOB = new CountryMasterDOB(countryId, rs.getString(1), rs.getString(2), rs.getString(3));
				al.add(cDOB);
			}
		}
		catch(Exception ex)
		{
			System.out.println("Exception in getCountryDetails() - CountryMasterBean "+ex);
		}
		finally
		{
			try
			{
				if(rs!=null)
					rs.close();
				if(pStmt!=null)
					pStmt.close();
				if(connection!=null)
					connection.close();
			}
			catch(SQLException sqEx)
			{
				System.out.println("Exception in Closing Connection getCountryDetails() - CountryMasterBean "+sqEx);
			}
		}
		return al;
	}
	
	/**
	 * This is getCountryIds() method
     * This method is used to get values from FS_LG_COUNTRYMASTER	
	 * @retrun ArrayList alist
	 */	
	public ArrayList getCountryIds(String countryId)
	{
		Connection connection   = null;
		PreparedStatement pStmt = null;
		ResultSet rs            = null;
		ArrayList alist = new ArrayList();
		CountryMasterDOB cDOB = null;
		try
		{
			connection = this.getConnection();
			pStmt = connection.prepareStatement("SELECT COUNTRYID, COUNTRYNAME, CURRENCYID, REGION FROM FS_COUNTRYMASTER WHERE COUNTRYID LIKE '"+countryId+"%' ORDER BY COUNTRYNAME");
			rs = pStmt.executeQuery();			
			while(rs.next())
			{
				cDOB = new CountryMasterDOB(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4));
				alist.add(cDOB);				
			}
		}
		catch(Exception ex)
		{
			System.out.println("Exception in getCountryIds() - CountryMasterBean "+ex);
		}
		finally
		{
			try
			{
				if(rs!=null)
					rs.close();
				if(pStmt!=null)
					pStmt.close();
				if(connection!=null)
					connection.close();
			}
			catch(SQLException sqEx)
			{
				System.out.println("Exception in Closing Connection getCountryIds() - CountryMasterBean "+sqEx);
			}
		}
		return alist;
	}

	/**
	 * This is getCurrencyId() method
	 * This method is used to get currencyId's from currencymaster
	 * @param countryId
	 * @return currency 
	 */ 
	public String getCurrencyId(String countryId)
	{
		Connection connection = null;
		PreparedStatement pStmt = null;
		ResultSet rs = null;
		String currency = null;
		try
		{
			connection = this.getConnection();
			pStmt = connection.prepareStatement("SELECT CURRENCYID FROM FS_LG_COUNTRYMASTER WHERE COUNTRYID = ?");
			pStmt.setString(1, countryId);
			rs = pStmt.executeQuery();
			if(rs.next())
			{
				currency = rs.getString(1);
			}
		}
		catch(Exception ex)
		{
			System.out.println("Exception in getCurrencyId() - CountryMasterBean "+ex);
		}
		finally
		{
			try
			{
				if(rs!=null)
					rs.close();
				if(pStmt!=null)
					pStmt.close();
				if(connection!=null)
					connection.close();
			}
			catch(SQLException sqEx)
			{
				System.out.println("Exception in Closing Connection getCurencyId() - CountryMasterBean "+sqEx);
			}
		}
		return currency;
	}

	/**
	 * This is getCountries() method
	 * This method is used to get cuntries's from FS_LG_COUNTRYMASTER
	 * @param currencyId
	 * @return al 
	 */ 
	public ArrayList getCountries(String currencyId)
	{
		Connection connection = null;
		PreparedStatement pStmt = null;
		ResultSet rs = null;
		ArrayList al = new ArrayList();
		try
		{
			connection = this.getConnection();
			pStmt = connection.prepareStatement("SELECT COUNTRYID FROM FS_LG_COUNTRYMASTER WHERE CURRENCYID = ? ORDER BY COUNTRYID");
			pStmt.setString(1, currencyId);
      rs = pStmt.executeQuery();//Added By RajKumari on 23-10-2008 for Connection Leakages.
			while(rs.next())
			{
				al.add(rs.getString(1));
			}
		}
		catch(Exception ex)
		{
			System.out.println("Exception in getCountries() - CountryMasterBean "+ex);
		}
		finally
		{
			try
			{
				if(rs!=null)
					rs.close();
				if(pStmt!=null)
					pStmt.close();
				if(connection!=null)
					connection.close();
			}
			catch(SQLException sqEx)
			{
				System.out.println("Exception in Closing Connection getCountries() - CountryMasterBean "+sqEx);
			}
		}
		return al;
	}

    /**
	 * This method is getConnection() method
	 * This method is used to get connection
	 */ 
	private Connection getConnection() throws SQLException
	{
		try
		{
			if(dataSource == null)
			{
				ic = new InitialContext();
				dataSource = (DataSource)ic.lookup("java:comp/env/jdbc/DB");
			}
		}
		catch(Exception ex)
		{
			System.out.println("Error in getting Connection() -> "+ex.toString());
		}
		return dataSource.getConnection();
	}	
	public void ejbCreate() throws CreateException
	{
	}
	
	
	//Implementing SessionBeanInterface methods.....//
	public void setSessionContext(SessionContext sessionCtx) throws javax.ejb.EJBException
	{
		this.sessionContext = sessionCtx;
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
	
}//End Of The Class
