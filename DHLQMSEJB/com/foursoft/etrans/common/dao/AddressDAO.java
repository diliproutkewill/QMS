/*
 * Copyright ©.
 */
package com.foursoft.etrans.common.dao;

import javax.ejb.EJBException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
//import com.foursoft.esupply.common.util.Logger;
import org.apache.log4j.Logger;
import com.foursoft.esupply.common.util.ConnectionUtil;
import com.foursoft.etrans.common.util.ejb.sls.OIDSessionHome;
import com.foursoft.etrans.common.util.ejb.sls.OIDSession;

//JS
import java.util.ArrayList;


import com.foursoft.etrans.common.bean.Address;
/**
 * @author rajesh
 * @version etrans1.6
 */
public class AddressDAO
{
	InitialContext 						context 		= null;
	private	transient		Connection	connection		= null;
	private transient 		DataSource 	dataSource		= null;
	private static final 	String 		FILE_NAME		= "AddressDAO.java";
	private static Logger logger = null;

  /**
   * @param
   */
	public AddressDAO()
	{
    logger  = Logger.getLogger(AddressDAO.class);
		try
		{
			InitialContext ic	= new InitialContext();
			dataSource			=(DataSource) ic.lookup("java:comp/env/jdbc/DB");
		}
		catch(NamingException nmEx)
		{
			//Logger.error(FILE_NAME, "AddressDAO", nmEx.toString());
      logger.error(FILE_NAME+ "AddressDAO"+ nmEx.toString());
			throw new EJBException(nmEx.toString());
		}
	}
  /**
   * 
   * @param address
   * @return int
   * @throws javax.ejb.EJBException
   */
	public int create(Address address)throws javax.ejb.EJBException
	{
		int addressId=0;
		try
		{
			if(address.getAddressId()==0)
			{
				address.setAddressId(this.getAddressId());
			}
			addressId=address.getAddressId();
			insertIntoAddress(address);
		}
		catch(Exception ex)
		{
			//Logger.error(FILE_NAME,"create addressId"+addressId,ex.toString(),ex);
      logger.error(FILE_NAME+"create addressId"+addressId+ex.toString()+ex);
			throw new javax.ejb.EJBException(ex.toString());
		}
		return address.getAddressId();
	}
	
	//JS
  /**
   * 
   * @param addList
   * @return int
   * @throws javax.ejb.EJBException
   */
	public int create(ArrayList addList)throws javax.ejb.EJBException
	{
		int addId = 0;
		int addListSize = 0; //Added for loopPerformance
		try
		{
			

			if(addList!=null && addList.size()>0 ){
				addListSize	=	addList.size();
					for(int i=0;i<addListSize;i++){
						Address addObj	=	(Address)addList.get(i);
						addId = addObj.getAddressId();
						//Logger.info(FILE_NAME," sIZE OF THE addObj --->"+addList.size());
							insertIntoAddress(addObj);
					}
			}
		}
		catch(Exception ex)
		{
			//Logger.error(FILE_NAME,"create addressId "+addId,ex.toString(),ex);
      logger.error(FILE_NAME+"create addressId "+addId+ex.toString()+ex);
			throw new javax.ejb.EJBException(ex.toString());
		}
		return addId;
	}
	//JS

  /**
   * 
   * @param addressId
   * @param address
   * @throws javax.ejb.EJBException
   */
	public void insertIntoAddress(int addressId,Address address)throws EJBException
	{
		PreparedStatement pStmt=null;
		String insQry = "INSERT INTO FS_ADDRESS(ADDRESSID,ADDRESSLINE1,ADDRESSLINE2,ADDRESSLINE3,CITY,STATE,ZIPCODE," +
							"COUNTRYID,PHONENO,EMAILID,FAX,HELPLINE) VALUES(?,?,?,?,?,?,?,?,?,?,?,?)";
		try
		{
			connection = this.getConnection();
			pStmt = connection.prepareStatement(insQry);
				pStmt.setInt(1, addressId);
                pStmt.setString(2, address.getAddressLine1());
                pStmt.setString(3, address.getAddressLine2()!=null?address.getAddressLine2():"");
				pStmt.setString(4, address.getAddressLine3()!=null?address.getAddressLine3():"");
                pStmt.setString(5, address.getCity()!=null?address.getCity():"");
                pStmt.setString(6, address.getState()!=null?address.getState():"");
                pStmt.setString(7, address.getZipCode()!=null?address.getZipCode():"");
                pStmt.setString(8, address.getCountryId());
                pStmt.setString(9, address.getPhoneNo()!=null?address.getPhoneNo():"");
                pStmt.setString(10, address.getEmailId()!=null?address.getEmailId():"");
                pStmt.setString(11, address.getFax()!=null?address.getFax():"");
				pStmt.setString(12, address.getHelpLine()!=null?address.getHelpLine():"");
                pStmt.executeUpdate();

		} 
		catch(Exception e)
		{
			//Logger.error(FILE_NAME,"insertIntoAddress(int addressId,Address address)",e.toString(),e);
      logger.error(FILE_NAME+"insertIntoAddress(int addressId,Address address)"+e.toString()+e);
		}	
		finally
		{
			ConnectionUtil.closeConnection(connection,pStmt);
		}
	}


	//JS


  /**
   * 
   * @param address
   * @throws java.sql.SQLException
   */
	private void insertIntoAddress(Address address)throws SQLException
	{
		PreparedStatement pStmt=null;
		String insQry = "INSERT INTO FS_ADDRESS(ADDRESSID,ADDRESSLINE1,ADDRESSLINE2,ADDRESSLINE3,CITY,STATE,ZIPCODE," +
							"COUNTRYID,PHONENO,EMAILID,FAX,HELPLINE) VALUES(?,?,?,?,?,?,?,?,?,?,?,?)";
//Added by subrahmanyam for the customer upload address arrangement pbn id:186252
    String addrLine1    = "";
    String addrLine2    = "";
    String addrLine3    = "";
    //Ended for pbn id:186252

		try
		{
  
			connection = this.getConnection();
			pStmt = connection.prepareStatement(insQry);
      //@@ added by subrahmanyam for the customer address changes while uploading
      if( address.getAddressLine1()!=null && address.getAddressLine1().indexOf(";")!=-1)
          addrLine1 = address.getAddressLine1().replace(';',',');
       else
        addrLine1 = address.getAddressLine1();

       if( address.getAddressLine2()!=null && address.getAddressLine2().indexOf(";")!=-1)
          addrLine2 = address.getAddressLine2().replace(';',',');
       else
        addrLine2 = address.getAddressLine2();

        if( address.getAddressLine3()!=null && address.getAddressLine3().indexOf(";")!=-1)
          addrLine3 = address.getAddressLine3().replace(';',',');
       else
        addrLine3 = address.getAddressLine3();

//@@ Ended by subrahmanyam for the customer address changes while uploading id:186252

				pStmt.setInt(1, address.getAddressId());
    //@@ Commented and added by subrahmanyam for the customer address changes while uploading pbn id:186252
            /*    pStmt.setString(2, address.getAddressLine1());
                pStmt.setString(3, address.getAddressLine2()!=null?address.getAddressLine2():"");
                 pStmt.setString(4, address.getAddressLine3()!=null?address.getAddressLine3():"");
                 */
                pStmt.setString(2, addrLine1!=null?addrLine1:"");
                pStmt.setString(3, addrLine2!=null?addrLine2:"");
                pStmt.setString(4, addrLine3!=null?addrLine3:"");
//@@ ended by subrahmanyam for the customer address changes while uploading.pbn id:186252
                pStmt.setString(5, address.getCity()!=null?address.getCity():"");
                pStmt.setString(6, address.getState()!=null?address.getState():"");
                pStmt.setString(7, address.getZipCode()!=null?address.getZipCode():"");
                pStmt.setString(8, address.getCountryId());
                pStmt.setString(9, address.getPhoneNo()!=null?address.getPhoneNo():"");
                pStmt.setString(10, address.getEmailId()!=null?address.getEmailId():"");
                pStmt.setString(11, address.getFax()!=null?address.getFax():"");
				pStmt.setString(12, address.getHelpLine()!=null?address.getHelpLine():"");
                pStmt.executeUpdate();

		} 
		catch(Exception e)
		{
			//Logger.error(FILE_NAME,"insertIntoAddress",e.toString(),e);
      logger.error(FILE_NAME+"insertIntoAddress"+e.toString()+e);
		}	
		finally
		{
			ConnectionUtil.closeConnection(connection,pStmt);
		}
	}
  /**
   * 
   * @param addressId
   * @return Address
   * @throws javax.ejb.EJBException
   * @throws java.sql.SQLException
   */
	public Address load(int addressId)throws EJBException, SQLException
	{
		Statement 	stmt		= null;
		ResultSet 	rs 			= null;
		Address 	address 	= null;
		try
		{
			connection 	= this.getConnection();
			stmt		= connection.createStatement();			
			rs 			= stmt.executeQuery("SELECT ADDRESSLINE1, ADDRESSLINE2,ADDRESSLINE3, CITY, STATE, ZIPCODE, COUNTRYID, PHONENO, EMAILID, FAX, HELPLINE FROM FS_ADDRESS WHERE ADDRESSID = "+addressId);
			if(rs.next())
			{
				address 	= new Address(rs.getString(1),rs.getString(2),rs.getString(3),rs.getString(4),rs.getString(5),rs.getString(6),rs.getString(7),rs.getString(8),rs.getString(9),rs.getString(10),addressId);
				address.setHelpLine(rs.getString(11));
			}
		}
		catch(Exception ex)
		{
			//Logger.error(FILE_NAME, "load", ex.toString(),ex);
      logger.error(FILE_NAME+ "load"+ ex.toString()+ex);
			throw new EJBException(ex.toString());
		}
		finally
		{
      //ConnectionUtil.closeConnection(connection);
			ConnectionUtil.closeConnection(connection,stmt,rs);//closed by madhu
		}
		return address;	
	}
  /**
   * 
   * @param address
   * @throws javax.ejb.EJBException
   */
	public void store(Address address)throws EJBException
	{
		try
		{
//			removeAddress(addressId);
//			insertIntoAddress(addressDOB, addressId);
			updateAddress(address);
		}
		catch(Exception ex)
		{
			//Logger.error(FILE_NAME, "store", ex.toString(),ex);
      logger.error(FILE_NAME+ "store"+ ex.toString()+ex);
			throw new EJBException(ex.toString());
		}
	}
  /**
   * 
   * @param address
   * @throws java.sql.SQLException
   */
	public void updateAddress(Address address)throws SQLException
	{
		PreparedStatement pstmt = null;
		try
		{
			connection 			= this.getConnection();
			String updateQry	= "	UPDATE FS_ADDRESS SET ADDRESSLINE1=?,ADDRESSLINE2=?,ADDRESSLINE3=?,CITY=?,STATE=?,ZIPCODE=?,COUNTRYID=?,PHONENO=?,EMAILID=?,FAX=?,HELPLINE=?"+
					  " WHERE ADDRESSID =	?";
				pstmt=connection.prepareStatement(updateQry);
				pstmt.setString(1,address.getAddressLine1());
				pstmt.setString(2,address.getAddressLine2());
				pstmt.setString(3,address.getAddressLine3());
				pstmt.setString(4,address.getCity());
				pstmt.setString(5,address.getState());
				pstmt.setString(6,address.getZipCode());
				pstmt.setString(7,address.getCountryId());
				pstmt.setString(8,address.getPhoneNo());
				pstmt.setString(9,address.getEmailId());
				pstmt.setString(10,address.getFax());
				pstmt.setString(11,address.getHelpLine());
				pstmt.setInt(12,address.getAddressId());
				pstmt.executeUpdate();
		
		}
		catch(Exception ex)
		{
			//Logger.error(FILE_NAME, "updateAddress", ex.toString(),ex);
      logger.error(FILE_NAME+ "updateAddress"+ ex.toString()+ex);
		}
		finally
		{
			ConnectionUtil.closeConnection(connection, pstmt);
		}
	}
  /**
   * 
   * @param addressId
   * @throws java.sql.SQLException
   */
	public void removeAddress(int addressId)throws SQLException
	{
		Statement 	stmt 		= null;
		try
		{
			connection  = this.getConnection();
			stmt 		= connection.createStatement();
			stmt.executeUpdate("DELETE FROM FS_ADDRESS WHERE ADDRESSID = "+addressId);		
		}
		catch(SQLException sqEx)
		{
			//Logger.error(FILE_NAME, "removeAddress", sqEx);
      logger.error(FILE_NAME+ "removeAddress"+ sqEx);
			throw new javax.ejb.EJBException();
		}
		finally
		{
			ConnectionUtil.closeConnection(connection,stmt);
		}
	}
  /**
   * 
   * @return Connection
   * @throws java.sql.SQLException
   */
	private Connection getConnection() throws SQLException
	{
/**		try
		{
			if(dataSource != null)
			{	
				connection 	= dataSource.getConnection();
			}
		}
		catch(SQLException sqEx)
		{
			Logger.error(FILE_NAME,"ShipmentDAO[getConnection()] ->	"+sqEx.toString());
			throw new SQLException(sqEx.toString());
		}*/
		return dataSource.getConnection();
	}
	
  /**
   * 
   * @return int
   * @throws javax.ejb.EJBException
   */
	private int getAddressId()throws javax.ejb.EJBException
	{
		OIDSession remote=null;
		int addressId=0;
		try
		{
		InitialContext initialContext =	new	InitialContext();
		OIDSessionHome home	= (OIDSessionHome)initialContext.lookup("OIDSessionBean");
		remote = (OIDSession)home.create();
		addressId=remote.getAddressOID();
		}catch(Exception ex)
		{
			//Logger.error(FILE_NAME,"Unable to get thae address Id from OIDSession Bean addressId="+addressId);
      logger.error(FILE_NAME+"Unable to get thae address Id from OIDSession Bean addressId="+addressId);
			throw new EJBException(ex);
		}
		return addressId;
			
	}//End of the Method.

}

