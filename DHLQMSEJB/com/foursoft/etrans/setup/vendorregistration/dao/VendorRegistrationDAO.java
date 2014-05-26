package com.foursoft.etrans.setup.vendorregistration.dao;
/*
 * @(#) VendorregistrationDOB.java		1.0		2001/10/9
 *
 * Copyright (c) 2001 Four Soft Pvt Ltd.
 * 5Q1A3, Cyber Towers, 5th floor, HiTec City, Madhapur, Hyderabad - 33.
 * All rights reserved.
 *
 * This Software is the Confidential and proprietary information of Four Soft Pvt Ltd.
 * ("Confidential Information"). You shall not disclose such Confidential Information
 * and shall use it only in accordance with the terms of the license agreement
 * you entered into with Four Soft.
 */
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.sql.SQLException;
import javax.sql.DataSource;
import java.util.Date;
import java.text.DateFormat;
import java.util.Locale;
import java.util.ArrayList;
import java.text.ParseException;
import java.rmi.RemoteException;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import javax.ejb.ObjectNotFoundException;
import javax.ejb.CreateException;
import javax.ejb.DuplicateKeyException;
import javax.ejb.EJBException;

import com.foursoft.esupply.common.util.ConnectionUtil;
//import com.foursoft.esupply.common.util.Logger;
import org.apache.log4j.Logger;
import com.foursoft.esupply.common.util.StringUtility;

import com.foursoft.etrans.setup.vendorregistration.java.VendorRegistrationJava;
import com.foursoft.etrans.common.bean.Address;
import com.foursoft.etrans.setup.vendorregistration.ejb.bmp.VendorRegistrationEnitityPK;

/**
 * 
 * This class will be useful to .
 * 
 * File		  : VendorRegistrationDOB.java
 * @version	: etrans 1.6
 * @author	: Srinivasa Rao Koppurauri 
 * 
 * @date	  : 2002-03-24
 *
 */

public class VendorRegistrationDAO 
{
	private transient DataSource dataSource	= null;
	private static final String FILE_NAME			= "VendorRegistrationDOB.java";
	private static final String insVendorQry[]		= new String[3];
    private static final String prmiaryQuery		=	" SELECT VENDOR_ID FROM  FS_FR_VENDOR_MASTER WHERE VENDOR_ID = ? ";

	private static final String vendorQuery		=	" SELECT VENDOR_ID,TRML_ID,ABBR_NAME,SHPMNT_MODE,COMPANY_NAME, "+
																	" CARRER_ID,OPER_AC_INDICATOR,CONTACT_NAME,DESIGNATION, "+
																	" VENDOR_ADDRESS_ID,OPERATIONS_EMAILID,NOTES,VENDOR_REG_FLAG FROM  "+
																	" FS_FR_VENDOR_MASTER WHERE VENDOR_ID = ? ";

	private static final String customerQuery	=  " SELECT ADDRESSLINE1,ADDRESSLINE2,CITY,STATE,ZIPCODE,COUNTRYID,PHONENO,EMAILID, "+
																	" FAX FROM 	FS_ADDRESS WHERE ADDRESSID=? ";
	
	private static final String vendorUpdateQry	=	" UPDATE FS_FR_VENDOR_MASTER SET 	SHPMNT_MODE=?,COMPANY_NAME=?,CARRER_ID=?, "+
																	" OPER_AC_INDICATOR=?,CONTACT_NAME=?,DESIGNATION=?,OPERATIONS_EMAILID=?, "+
																	" NOTES=?,VENDOR_REG_FLAG=? WHERE VENDOR_ID=? ";

	private static final String addressUpdateQry	=	" UPDATE FS_ADDRESS SET ADDRESSLINE1=?,ADDRESSLINE2=?,CITY=?,STATE=?,ZIPCODE=?, "+
																	    " COUNTRYID=?,PHONENO=?,EMAILID=?,FAX=? WHERE ADDRESSID=? ";

	private	static final String vendorDeleteQry	= " DELETE FROM FS_FR_VENDOR_MASTER WHERE VENDOR_ID=? ";

	private	static final String addressDeleteQry	= " DELETE FROM FS_ADDRESS WHERE ADDRESSID=? ";

	private	static final String getAddressIdQry	=  " SELECT VENDOR_ADDRESS_ID FROM FS_FR_VENDOR_MASTER WHERE VENDOR_ID=? ";
  private static Logger logger = null;

	static{
					insVendorQry[1]										=	" INSERT INTO  FS_FR_VENDOR_MASTER(VENDOR_ID,TRML_ID,ABBR_NAME,SHPMNT_MODE, "+
																					" COMPANY_NAME,CARRER_ID,OPER_AC_INDICATOR,CONTACT_NAME,DESIGNATION, "+
																					" VENDOR_ADDRESS_ID,OPERATIONS_EMAILID,NOTES,VENDOR_REG_FLAG)VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?) ";
					
					insVendorQry[2]										=	" INSERT INTO FS_ADDRESS(ADDRESSID ,ADDRESSLINE1,ADDRESSLINE2,CITY,STATE,ZIPCODE, "+
																					" COUNTRYID,PHONENO,EMAILID,FAX)VALUES(?,?,?,?,?,?,?,?,?,?) ";

		  }
	VendorRegistrationEnitityPK  pkObj		=	null;
  /**
   * 
   */
  public VendorRegistrationDAO()
  {
    logger  = Logger.getLogger(VendorRegistrationDAO.class);
	 	try
		{
			InitialContext ic		= new InitialContext();
			dataSource			=(DataSource) ic.lookup("java:comp/env/jdbc/DB");
		}
		catch(NamingException nmEx)
		{
			//Logger.error(FILE_NAME,"VendorRegistrationDOB(VendorRegistrationDOB())"+nmEx.toString());
      logger.error(FILE_NAME+"VendorRegistrationDOB(VendorRegistrationDOB())"+nmEx.toString());
			throw new EJBException(nmEx.toString());
		}
  }
  	
  /**
   * 
   * @param pkObj
   * @return VendorRegistrationEnitityPK
   * @throws java.sql.SQLException
   * @throws javax.ejb.ObjectNotFoundException
   */
	public VendorRegistrationEnitityPK findByPrimaryKey(VendorRegistrationEnitityPK	pkObj)throws  SQLException, ObjectNotFoundException
	{
									 
		PreparedStatement				pStmtFindPK 		= null;
		boolean								vendorRows 		= false;
		Connection							connection			= null;
    ResultSet rs   = null;//Added By RajKumari on 24-10-2008 for Connection Leakages.
		try
		{
				connection					= this.getConnection(); 
				pStmtFindPK				= connection.prepareStatement(prmiaryQuery);
				pStmtFindPK.setString(1,pkObj.vendorId);
				rs				= pStmtFindPK.executeQuery();//Modified By RajKumari on 24-10-2008 for Connection Leakages.
				if(rs.next())
				{
					vendorRows = true;
				}
		}
		catch(Exception sqEx)
		{
			//Logger.error(FILE_NAME,"VendorRegistrationDOB[findByPrimaryKey(pkObj)] -> "+sqEx.toString());
      logger.error(FILE_NAME+"VendorRegistrationDOB[findByPrimaryKey(pkObj)] -> "+sqEx.toString());
			throw new EJBException(sqEx.toString());
		}
		finally
		{
			try
			{
				ConnectionUtil.closeConnection(connection, pStmtFindPK);
        ConnectionUtil.closeStatement(null,rs);//Added By RajKumari on 24-10-2008 for Connection Leakages.
			}
			catch(EJBException ex)
			{
				//Logger.error(FILE_NAME,"Finally : VendorRegistrationDOB[findByPrimaryKey(pkObj)]-> "+ex.toString());
        logger.error(FILE_NAME+"Finally : VendorRegistrationDOB[findByPrimaryKey(pkObj)]-> "+ex.toString());
				throw new EJBException(ex.toString());
			}
		}
		if (vendorRows)
    {
			return pkObj;
    }
		else
    {
			throw new ObjectNotFoundException("Could not find bean	with Id "+pkObj.vendorId);
    }
	}
  /**
   * 
   * @param vendorRegistrationJava
   * @throws javax.ejb.CreateException
   * @throws javax.ejb.DuplicateKeyException
   * @throws java.sql.SQLException
   */
	public void create(VendorRegistrationJava vendorRegistrationJava)throws CreateException,DuplicateKeyException,SQLException
	{
		Connection connection			=	null;
		try
		{
			connection = this.getConnection();
			insertAddressDetails(vendorRegistrationJava,connection);
			insertVendorDetails(vendorRegistrationJava,connection);
		
		}
		catch(Exception e)
		{
				//Logger.error(FILE_NAME,"create()","Exception while Creating VendorDetails ", e);
        logger.error(FILE_NAME+"create()"+"Exception while Creating VendorDetails "+ e);
				throw new CreateException(e.toString());
		}
		finally
		{
			try
			{
				ConnectionUtil.closeConnection(connection);
			}
			catch(EJBException ex)
			{
				//Logger.error(FILE_NAME,"in Finally Block in Create() method() -> "+ex.toString());
        logger.error(FILE_NAME+"in Finally Block in Create() method() -> "+ex.toString());
				throw new EJBException(ex.toString());
			}
		}
	}
  /**
   * 
   * @param vendorPkObj
   * @return VendorRegistrationJava
   * @throws java.sql.SQLException
   */
	public VendorRegistrationJava load(VendorRegistrationEnitityPK vendorPkObj)throws SQLException
	{
		VendorRegistrationJava		vendorRegistrationJava			=	null;
		Address								addressObj							=	null;
		int										addressId								=	0;
		try
		{
				vendorRegistrationJava		=		loadVendorDetails(vendorPkObj.vendorId);
		}
		catch(Exception e)
		{
			//Logger.error(FILE_NAME,"in load() method ","Exception while Loading Vendor Details ", e);
      logger.error(FILE_NAME+"in load() method "+"Exception while Loading Vendor Details "+ e);
			throw new SQLException(e.toString());
		}
	return vendorRegistrationJava;
	}
  /**
   * 
   * @param vendorRegistrationJava
   * @throws java.sql.SQLException
   */
	public void store(VendorRegistrationJava vendorRegistrationJava)throws SQLException
	{
		try
		{
			updateVendorDetails(vendorRegistrationJava);
		}
		catch(Exception ex)
		{
			//Logger.error(FILE_NAME,"in load() method ","Exception while storing Vendor Details ", ex);
      logger.error(FILE_NAME+"in load() method "+"Exception while storing Vendor Details "+ ex);
			throw new SQLException(ex.toString());
		}
	}
  /**
   * 
   * @param vendorPkObj
   * @throws java.sql.SQLException
   */
	public void remove(VendorRegistrationEnitityPK vendorPkObj)throws SQLException
	{
		try
		{
			deleteVendorAddressDetails(vendorPkObj.vendorId);
		}
		catch(Exception e)
		{
				//Logger.error(FILE_NAME,"VendorRegistrationDOB()--->remove()", e);
        logger.error(FILE_NAME+"VendorRegistrationDOB()--->remove()"+ e);
				throw new EJBException(e.toString());
		}
	}
	private void deleteVendorAddressDetails(String vendorId)throws SQLException
	{
		Connection							connection		=	null;
		PreparedStatement				pStmt			=	null;
		ResultSet							rs					=	null;
		int										addressId		=	0;
		try
		{
			connection		=	this.getConnection();
			pStmt			=	connection.prepareStatement(getAddressIdQry);
			pStmt.setString(1,vendorId);
			rs					=	pStmt.executeQuery();
			if(rs.next())
      {
				addressId	=	rs.getInt(1);
      }
      //Added By RajKumari on 24-10-2008 for Connection Leakages.
      if(pStmt!=null)
      {
        pStmt.close();
        pStmt = null;    
      }
		
			
			pStmt = connection.prepareStatement(vendorDeleteQry);
			pStmt.setString(1,vendorId);
			pStmt.executeUpdate();

		 //Added By RajKumari on 24-10-2008 for Connection Leakages.
      if(pStmt!=null)
      {
        pStmt.close();
        pStmt = null;    
      }
			
			pStmt = connection.prepareStatement(addressDeleteQry);
			pStmt.setInt(1,addressId);
			pStmt.executeUpdate();
			
			pStmt.close();

		}
		catch(SQLException ex) 
		{
			//Logger.error(FILE_NAME,"deleteVendorAddressDetails()","Exception while deleting  the Vendordetails ", ex);
      logger.error(FILE_NAME+"deleteVendorAddressDetails()"+"Exception while deleting  the Vendordetails "+ ex);
				throw new SQLException(ex.toString());
		}
		finally
		{
			try
			{
				ConnectionUtil.closeConnection(connection);
        ConnectionUtil.closeStatement(null,rs);//Added By RajKumari on 24-10-2008 for Connection Leakages.
			}
			catch(EJBException e)
			{
				//Logger.error(FILE_NAME,"in Finally Block in Delete() method() -> "+e.toString());
        logger.error(FILE_NAME+"in Finally Block in Delete() method() -> "+e.toString());
				throw new EJBException(e.toString());
			}
		}
	}
	private void updateVendorDetails(VendorRegistrationJava vendorRegistrationJava)throws SQLException
	{
		Connection						connection		=	null;
		PreparedStatement			pStmt			=	null;
		Address							addressObj	=	null;
		try
		{
			addressObj	=	vendorRegistrationJava.getAddressObj();
			connection	 =	 this.getConnection();
			pStmt		=	connection.prepareStatement(vendorUpdateQry);
			pStmt.setInt(1,Integer.parseInt(vendorRegistrationJava.getShipmentMode()));
			pStmt.setString(2,vendorRegistrationJava.getCompanyName());
			pStmt.setString(3,vendorRegistrationJava.getCarrierId());
			pStmt.setString(4,vendorRegistrationJava.getIndicator());
			pStmt.setString(5,vendorRegistrationJava.getContactName());
			pStmt.setString(6,vendorRegistrationJava.getDesignation());
			pStmt.setString(7,vendorRegistrationJava.getOperationMailId());
			pStmt.setString(8,vendorRegistrationJava.getNotes());
			pStmt.setString(9,vendorRegistrationJava.getVendRegFlag());      
			pStmt.setString(10,vendorRegistrationJava.getVendorId());
			pStmt.executeUpdate();
			pStmt.close();
			pStmt	=	null;
			pStmt	=	connection.prepareStatement(addressUpdateQry);
			pStmt.setString(1,addressObj.getAddressLine1());
			pStmt.setString(2,addressObj.getAddressLine2());
			pStmt.setString(3,addressObj.getCity());
			pStmt.setString(4,addressObj.getState());
			pStmt.setString(5,addressObj.getZipCode());
			pStmt.setString(6,addressObj.getCountryId());
			pStmt.setString(7,addressObj.getPhoneNo());
			pStmt.setString(8,addressObj.getEmailId());
			pStmt.setString(9,addressObj.getFax());
			pStmt.setInt(10,addressObj.getAddressId());
			pStmt.executeUpdate();
			pStmt.close();
		}
		catch(Exception ex)
		{
			//Logger.error(FILE_NAME,"updateVendorDetails()","Exception while Updateing  the Vendordetails ", ex);
      logger.error(FILE_NAME+"updateVendorDetails()"+"Exception while Updateing  the Vendordetails "+ ex);
				throw new SQLException(ex.toString());
		}
		finally
		{
			try
			{
				ConnectionUtil.closeConnection(connection);
			}
			catch(EJBException e)
			{
				//Logger.error(FILE_NAME,"in Finally Block in Update() method() -> "+e.toString());
        logger.error(FILE_NAME+"in Finally Block in Update() method() -> "+e.toString());
				throw new EJBException(e.toString());
			}
		}
	}
	private VendorRegistrationJava loadVendorDetails(String vendorId)throws SQLException
	{
		PreparedStatement				pStmt							      =	null;
		ResultSet							  rs									    =	null;
		Connection							connection						  =	null;
		int										  addressId						    =	0;
		VendorRegistrationJava	vendorRegistrationJava	=	new VendorRegistrationJava();
		Address								  addressObj					    =	new Address();
		try
		{
			connection					=		this.getConnection(); 
			pStmt						    =		connection.prepareStatement(vendorQuery);
			pStmt.setString(1,vendorId);
			rs								  =		pStmt.executeQuery();
			while(rs.next())
			{
					vendorRegistrationJava.setVendorId(rs.getString(1));
					vendorRegistrationJava.setTerminalId(rs.getString(2));
					vendorRegistrationJava.setAbbrName(rs.getString(3));
					vendorRegistrationJava.setShipmentMode(String.valueOf(rs.getInt(4)));
					vendorRegistrationJava.setCompanyName	(rs.getString(5));
					if(rs.getString(6)!=null)
          {
						vendorRegistrationJava.setCarrierId(rs.getString(6));
          }
					else
          {
						vendorRegistrationJava.setCarrierId("");
          }
					vendorRegistrationJava.setIndicator(rs.getString(7));
					if(rs.getString(8)!=null)
          {
						vendorRegistrationJava.setContactName(rs.getString(8));
          }
					else
          {
						vendorRegistrationJava.setContactName("");
          }
					if(rs.getString(9)!=null)
          {
						vendorRegistrationJava.setDesignation(rs.getString(9));
          }
					else
          {
						vendorRegistrationJava.setDesignation("");
          }
					addressObj.setAddressId(rs.getInt(10));
					if(rs.getString(11)!=null)
          {
						vendorRegistrationJava.setOperationMailId(rs.getString(11));
          }
					else
          {
						vendorRegistrationJava.setOperationMailId("");
          }
					if(rs.getString(12)!=null)
          {
						vendorRegistrationJava.setNotes(rs.getString(12));
          }
					else
          {
						vendorRegistrationJava.setNotes("");
          }
					if(rs.getString(13)!=null)
          {
						vendorRegistrationJava.setVendRegFlag(rs.getString(13));
          }
					else
          {
						vendorRegistrationJava.setVendRegFlag("");            
          }
			}
      //Added By RajKumari on 24-10-2008 for Connection Leakages.
      if(pStmt!=null)
      {
        pStmt.close();
        pStmt=	null;
      }
			
			rs=null;
			pStmt						=		connection.prepareStatement(customerQuery);
			pStmt.setInt(1,addressObj.getAddressId());
			rs								=		pStmt.executeQuery();
			while(rs.next())
			{
					addressObj.setAddressLine1(rs.getString(1));
					if(rs.getString(2)!=null)
          {
						addressObj.setAddressLine2(rs.getString(2));
          }
					else
          {
						addressObj.setAddressLine2("");
          }
					addressObj.setCity(rs.getString(3));
					if(rs.getString(4)!=null)
          {
						addressObj.setState(rs.getString(4));
          }
					else
          {
						addressObj.setState("");
          }
					if(rs.getString(5)!=null)
          {
						addressObj.setZipCode(rs.getString(5));
          }
					else
          {
						addressObj.setZipCode("");
          }
					addressObj.setCountryId(rs.getString(6));
					if(rs.getString(7)!=null)
          {
						addressObj.setPhoneNo(rs.getString(7));
          }
					else
          {
						addressObj.setPhoneNo("");
          }
					if(rs.getString(8)!=null)
          {
						addressObj.setEmailId(rs.getString(8));
          }
					else	
          {
						addressObj.setEmailId("");
          }
					if(rs.getString(9)!=null)
          {
						addressObj.setFax(rs.getString(9));
          }
					else
          {
						addressObj.setFax("");	
          }
			}
	
			vendorRegistrationJava.setAddressObj(addressObj);
			
		}
		catch(Exception ex)
		{
			//Logger.error(FILE_NAME,"loadVendorDetails()","in Catch Block  ",ex);
      logger.error(FILE_NAME+"loadVendorDetails()"+"in Catch Block  "+ex);
			throw new SQLException(ex.toString());
		}
		finally
		{
			try
			{
				if(rs!=null)
        {
					rs.close();
        }
				if(pStmt!=null)
        {
					pStmt.close();
        }
				if(connection!=null)
        {
					connection.close();
        }
			}
			catch(Exception e)
			{
				//Logger.error(FILE_NAME,"loadVendorDetails()","in final Block connections are not closing  ",e);
        logger.error(FILE_NAME+"loadVendorDetails()"+"in final Block connections are not closing  "+e);
			}
    }
	return	vendorRegistrationJava;
	}
	private void insertAddressDetails(VendorRegistrationJava vendorRegistrationJava,Connection connection)throws SQLException
	{
		PreparedStatement		pStmt				=	null;
		Address						addressObj		=	null;
		addressObj										=	vendorRegistrationJava.getAddressObj();
		try
		{
			pStmt		=	connection.prepareStatement(insVendorQry[2]	);
			pStmt.setInt(1,addressObj.getAddressId());
			pStmt.setString(2,addressObj.getAddressLine1());
			pStmt.setString(3,addressObj.getAddressLine2());
			pStmt.setString(4,addressObj.getCity());
			pStmt.setString(5,addressObj.getState());
			pStmt.setString(6,addressObj.getZipCode());
			pStmt.setString(7,addressObj.getCountryId());
			pStmt.setString(8,addressObj.getPhoneNo());
			pStmt.setString(9,addressObj.getEmailId());
			pStmt.setString(10,addressObj.getFax());

			pStmt.executeUpdate();
			pStmt.close();
		}
		catch(Exception e)
		{
				//Logger.error(FILE_NAME,"insertAddressDetails()","Exception while inserting  the Addressdetails ", e);
        logger.error(FILE_NAME+"insertAddressDetails()"+"Exception while inserting  the Addressdetails "+ e);
				e.printStackTrace();
				throw new SQLException(e.toString());
		}
		
	}
	private void insertVendorDetails(VendorRegistrationJava vendorRegistrationJava,Connection connection)throws SQLException
	{
		PreparedStatement		pStmt				=	null;
		Address						addressObj		=	null;
		addressObj										=	vendorRegistrationJava .getAddressObj();
		try
		{
			pStmt		=	connection.prepareStatement(insVendorQry[1]	);
//    System.out.println("INSERT INTO  FS_FR_VENDOR_MASTER(VENDOR_ID,TRML_ID,ABBR_NAME,SHPMNT_MODE,COMPANY_NAME,CARRER_ID,OPER_AC_INDICATOR,CONTACT_NAME,DESIGNATION,VENDOR_ADDRESS_ID,OPERATIONS_EMAILID,NOTES,VENDOR_REG_FLAG) VALUES("+vendorRegistrationJava.getVendorId()+","+vendorRegistrationJava.getTerminalId()+","+vendorRegistrationJava.getAbbrName()+","+Integer.parseInt(vendorRegistrationJava.getShipmentMode())+","+vendorRegistrationJava.getCompanyName()+","+vendorRegistrationJava.getCarrierId()+","+vendorRegistrationJava.getIndicator()+","+vendorRegistrationJava.getContactName()+","+vendorRegistrationJava.getDesignation()+","+addressObj.getAddressId()+","+vendorRegistrationJava.getOperationMailId()+","+vendorRegistrationJava.getNotes()+","+vendorRegistrationJava.getVendRegFlag()+")");
			pStmt.setString(1,vendorRegistrationJava.getVendorId());
			pStmt.setString(2,vendorRegistrationJava.getTerminalId());
			pStmt.setString(3,vendorRegistrationJava.getAbbrName());
			pStmt.setInt(4,Integer.parseInt(vendorRegistrationJava.getShipmentMode()));
			pStmt.setString(5,vendorRegistrationJava.getCompanyName());
			pStmt.setString(6,vendorRegistrationJava.getCarrierId());
			pStmt.setString(7,vendorRegistrationJava.getIndicator());
			pStmt.setString(8,vendorRegistrationJava.getContactName());
			pStmt.setString(9,vendorRegistrationJava.getDesignation());
			pStmt.setInt(10,addressObj.getAddressId());
			pStmt.setString(11,vendorRegistrationJava.getOperationMailId());
			pStmt.setString(12,vendorRegistrationJava.getNotes());
			pStmt.setString(13,vendorRegistrationJava.getVendRegFlag());      

			pStmt.executeUpdate();
			pStmt.close();
		}
		catch(Exception e)
		{
				//Logger.error(FILE_NAME,"insertVendorDetails()","Exception while inserting  the insertVendorDetails ", e);
        logger.error(FILE_NAME+"insertVendorDetails()"+"Exception while inserting  the insertVendorDetails "+ e);
				e.printStackTrace();
				throw new SQLException(e.toString());
		}
		
	}
	private Connection getConnection() throws SQLException
	{
		return dataSource.getConnection();
	}

}