package com.foursoft.etrans.setup.vendorregistration.ejb.sls;
/**
 * @(#)VendoeRegistrationSessionBean.java         20/01/2003 
 *        
 * Copyright (c) 2000-2001 Four-Soft Pvt Ltd,
 * 5Q1A3, Hi-Tech City, Madhapur, Hyderabad-33, India.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of  Four-Soft Pvt Ltd,
 * ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Four-Soft. For more information on the Four Soft vist : 'www.four-soft.com'
 */
 
/**
 * File			: VendorRegistrationbean.java 
 * @author	:  Nageswara Rao.D
 * @date		: 20/01/2003
 */
import java.sql.Connection; 
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Date;

import javax.ejb.SessionBean;
import javax.ejb.SessionContext;
import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.ejb.FinderException;
import java.rmi.RemoteException;
import javax.ejb.ObjectNotFoundException;
import javax.sql.DataSource;

import com.foursoft.esupply.common.bean.ESupplyGlobalParameters;
import com.foursoft.esupply.common.util.ConnectionUtil;
//import com.foursoft.esupply.common.util.Logger;
import org.apache.log4j.Logger;
import com.foursoft.etrans.common.util.java.OperationsImpl;
import com.foursoft.etrans.common.bean.Address;
//import com.foursoft.etrans.common.dao.AddressDAO;

import com.foursoft.etrans.setup.vendorregistration.ejb.bmp.VendorRegistrationEnitityHome;
import com.foursoft.etrans.setup.vendorregistration.ejb.bmp.VendorRegistrationEnitity;
import com.foursoft.etrans.setup.vendorregistration.ejb.bmp.VendorRegistrationEnitityPK;
import com.foursoft.etrans.setup.vendorregistration.java.VendorRegistrationJava;
//import com.foursoft.etrans.setup.vendorregistration.dao.VendorRegistrationDOB;

import com.foursoft.etrans.setup.codecust.exception.CodeCustNotDoneException;

/**
 * 
 * This class will be useful to .
 * 
 * File		  : VendorRegistrationSessionBean.java
 * @version	: etrans 1.6
 * @author	: Nageswara Rao.D
 * 
 * @date	  : 20/01/2003
 *
 */


public class VendorRegistrationSessionBean implements SessionBean 
{
	private		SessionContext		sessionContext		= null;
	private		OperationsImpl		operationsImpl		= null;
	private		InitialContext			initialContext			= null; //storing initial context object
	private		DataSource			dataSource			= null; //storing datasource object
	static			String					FILE_NAME			= "VendorRegistrationSessionBean ";
	VendorRegistrationEnitityHome  home			=	null;
	VendorRegistrationEnitity			  remote		=	null;
  private static Logger logger = null;
	
  public VendorRegistrationSessionBean()
  {
    logger  = Logger.getLogger(VendorRegistrationSessionBean.class);
  }
  
  /**
   * 
   * @param vendorRegistrationJava
   * @param loginbean
   * @return String
   * @throws com.foursoft.etrans.setup.codecust.exception.CodeCustNotDoneException
   */
public String insertVendorDetails(VendorRegistrationJava vendorRegistrationJava,ESupplyGlobalParameters loginbean)throws CodeCustNotDoneException
{
	String					message	=	null;
	
	try
	{
		initialContext   	=  new InitialContext();
		home				=	 (VendorRegistrationEnitityHome)initialContext.lookup("VendorRegistrationEnitityBean");
		remote				=	(VendorRegistrationEnitity) home.create(vendorRegistrationJava);

		operationsImpl.setTransactionDetails(vendorRegistrationJava.getTerminalId(),
												 loginbean.getUserId(),
												 "Vendor",
												 vendorRegistrationJava.getVendorId(),
												 new java.sql.Timestamp((new java.util.Date()).getTime() ),
												 "ADD");
		
		message			=	"Record sucesfully added with VendorId : ";	
 
	}
	catch(Exception Exp)
	{
		//Logger.error(FILE_NAME,"addPickUpRequestDetails()",Exp.toString());
    logger.error(FILE_NAME+"addPickUpRequestDetails()"+Exp.toString());
		throw new EJBException();
	}
		   return	message;
}
  /**
   * 
   * @param vendorId
   * @param terminalId
   * @param subOperation
   * @return VendorRegistrationJava
   * @throws javax.ejb.ObjectNotFoundException
   */
//@@ Modified By Ravi Kumar on 26-04-2005
public VendorRegistrationJava isValidIdGetData(String vendorId,String terminalId,String subOperation, String vendAcctOperation)throws ObjectNotFoundException 
{
	ResultSet									rs										=	null;
	Statement									stmt									=  null;
	Connection									connection							=  null;
	String										sql									=	null;
	String										message							=	null;
	boolean										vendorType						=	false;
	VendorRegistrationJava						vendorRegistrationJava		=	null;
	//VendorRegistrationEnitityHome			home								=	null;
	//VendorRegistrationEnitity				remote								=	null;
	// @@ Replaced by G.Srinivas for TogetherArchitect on 20050111
	VendorRegistrationEnitityHome				home1								=	null;
	VendorRegistrationEnitity					remote1								=	null;

	VendorRegistrationEnitityPK					vendorPkObj						=	new  VendorRegistrationEnitityPK();

	try
	{
		connection		=	operationsImpl.getConnection();
		stmt				=	connection.createStatement();
		if(subOperation != null && (subOperation.equals("Accounts") || vendAcctOperation.equals("Accounts"))) //@@ Modified By Ravi Kumar on 26-04-2005
    {
//@@ Query Changed By Ujwala as per issues SPETI-5856  on Date : -25/05/2005
			//sql				=	" SELECT VENDOR_ID FROM FS_FR_VENDOR_MASTER WHERE VENDOR_ID='"+vendorId+"'  AND TRML_ID='"+terminalId+"' AND OPER_AC_INDICATOR = 'N' ";
      sql				=	" SELECT VENDOR_ID FROM FS_FR_VENDOR_MASTER WHERE VENDOR_ID='"+vendorId+"'  AND TRML_ID='"+terminalId+"' ";
//@@ End By Ujwala as per issues SPETI-5856  on Date : -25/05/2005 
    }
		else
    {
			// @@ Suneetha  added on 20050503
			if("delete".equalsIgnoreCase(subOperation)){
				sql				=	" SELECT VENDOR_ID FROM FS_FR_VENDOR_MASTER WHERE VENDOR_ID='"+vendorId+"'  AND TRML_ID='"+terminalId+"' AND OPER_AC_INDICATOR = 'Y'  AND VENDOR_ID NOT IN(SELECT DISTINCT M.VENDOR_ID FROM FS_FR_MASTERDOCHDR M WHERE M.VENDOR_ID LIKE '"+vendorId+"%') ";
			}
			else {
			// @@ 20050503
			sql				=	" SELECT VENDOR_ID FROM FS_FR_VENDOR_MASTER WHERE VENDOR_ID='"+vendorId+"'  AND TRML_ID='"+terminalId+"' AND OPER_AC_INDICATOR = 'Y' ";
			// @@ Suneetha  added on 20050503
			}
			// @@ 20050503
    }

		message		=	" No Record exist with VendorId "+vendorId+" " ;
		rs					=	stmt.executeQuery(sql);
		if(rs.next())
		{
			vendorPkObj.vendorId			= vendorId;
			initialContext   				=  new InitialContext();
			home1							=	 (VendorRegistrationEnitityHome)initialContext.lookup("VendorRegistrationEnitityBean");
			remote1							=	(VendorRegistrationEnitity)home1.findByPrimaryKey(vendorPkObj);
			vendorRegistrationJava			=	remote1.getVendorRegistrationJava();
			vendorType						=	true;
			
		}
		else
    {
			vendorRegistrationJava	=	null;
    }
	}
	catch(Exception exp)
	{
		//Logger.error(FILE_NAME,"isValidIdGetData() for vendorId",exp);
    logger.error(FILE_NAME+"isValidIdGetData() for vendorId"+exp);
		exp.printStackTrace();
		throw new EJBException();
	}
	finally
	{
		//closeConnection(rs,stmt,connection);
    ConnectionUtil.closeConnection(connection, stmt, rs);
	}
	if(!vendorType)
  {
			throw new ObjectNotFoundException(message);		
  }
	return vendorRegistrationJava;
} 
  /**
   * 
   * @param vendorRegistrationJava
   * @return String
   */
public String updateVendorDetails(VendorRegistrationJava vendorRegistrationJava)
{
	String										vendorMessage					=	null;
	VendorRegistrationEnitityHome	home2								=	null;
	VendorRegistrationEnitity				remote2								=	null;
	VendorRegistrationEnitityPK			vendorPkObj						=	new  VendorRegistrationEnitityPK();
	try
	{
		vendorPkObj.vendorId			=	vendorRegistrationJava.getVendorId();
		initialContext   					=  new InitialContext();
		home2								= (VendorRegistrationEnitityHome)initialContext.lookup("VendorRegistrationEnitityBean");
		remote2								=	(VendorRegistrationEnitity)home2.findByPrimaryKey(vendorPkObj);
		remote2.setVendorRegistrationJava(vendorRegistrationJava);
		vendorMessage					=	"The VendorId " +vendorRegistrationJava.getVendorId();	
	}
	catch(Exception rExp)
		{
			//Logger.error(FILE_NAME,"updateVendorDetails()",rExp.toString());
      logger.error(FILE_NAME+"updateVendorDetails()"+rExp.toString());
			throw new EJBException();
		}
	return	vendorMessage;
}
  /**
   * 
   * @param vendorId
   * @return String
   */
public String deleteVendorDetails(String vendorId)
{
	String									vendorMessage					=	null;
	//VendorRegistrationEnitityHome			home										=	null;
	//VendorRegistrationEnitity				remote								=	null;
	// @@ Replaced by G.Srinivas for TogetherArchitect on 20050111
	VendorRegistrationEnitityHome			home3										=	null;
	VendorRegistrationEnitity				remote3								=	null;

	VendorRegistrationEnitityPK			vendorPkObj						=	new  VendorRegistrationEnitityPK();
	try
	{
		vendorPkObj.vendorId			=	vendorId;
		initialContext   					=  new InitialContext();
		home3								= (VendorRegistrationEnitityHome)initialContext.lookup("VendorRegistrationEnitityBean");
		remote3								=	(VendorRegistrationEnitity)home3.findByPrimaryKey(vendorPkObj);
		remote3.remove();
		vendorMessage					="The VendorId " +	vendorId;	
	}

  catch(Exception fExp)
		{
			//Logger.error(FILE_NAME,"updateVendorDetails()",fExp);
      logger.error(FILE_NAME+"updateVendorDetails()"+fExp);
			throw new EJBException();
		}
	return	vendorMessage;
}
  /**
   * 
   * @param carrierId
   * @param countryId
   * @return StringBuffer
   */
public StringBuffer	getValidityFields(String carrierId,String countryId)
{
	StringBuffer		errorMessage  		=	null;
	String				carrierIdResult			=	null;
	String				countryIdResult			=	null;
	ResultSet		rs				=	null;
	Statement		stmt			=	null;
	Connection		connection	=	null;
	String			sql			=	null;
	errorMessage = new StringBuffer();
	try
	{
		connection	 =  this.getConnection();
		if(carrierId != null &&  carrierId.length()>0)
		{
			sql			 =	 " SELECT CARRIERID FROM FS_FR_CAMASTER WHERE CARRIERID='"+carrierId+"' ";
			stmt			 =  connection.createStatement();
			rs				 =  stmt.executeQuery(sql);
			if(rs.next())
      {
				carrierIdResult = rs.getString(1);
      }
			if(carrierIdResult==null)
      {
				errorMessage.append("Carrier Id<br>");
      }
		}
    //Added By RajKumari on 24-10-2008 for Connection Leakages.
		if(stmt!=null)
    {
      stmt.close();
      stmt = null;
    }
    
		rs		=	null;
		if(countryId != null && countryId.length()>0)
		{
			sql			 =	 " SELECT COUNTRYID FROM FS_COUNTRYMASTER WHERE COUNTRYID='"+countryId+"' ";
			stmt			 =  connection.createStatement();
			rs				 =  stmt.executeQuery(sql);
			if(rs.next())
      {
				countryIdResult = rs.getString(1);
      }
			if(countryIdResult==null)
      {
				errorMessage.append("Country Id<br>");
      }
		}
	}
	catch(Exception ex)
	{
		//Logger.error(FILE_NAME,"getValidityFields()",ex);
    logger.error(FILE_NAME+"getValidityFields()"+ex);
		throw new EJBException();
	}
	finally
	{
		ConnectionUtil.closeConnection(connection, stmt, rs);
	}
return	errorMessage;
}

  /**
   * 
   */
  public void ejbCreate()
  {
  }

  /**
   * 
   * @throws javax.ejb.EJBException
   */
  public void ejbActivate()throws javax.ejb.EJBException{}
  {
  }

  /**
   * 
   * @throws javax.ejb.EJBException
   */
  public void ejbPassivate()throws javax.ejb.EJBException{}
  {
  }

  /**
   * 
   * @throws javax.ejb.EJBException
   */
  public void ejbRemove()throws javax.ejb.EJBException{}
  {
  }
  /**
   * 
   * @return 
   * @throws java.sql.SQLException
   */
private Connection getConnection() throws SQLException
{
	return dataSource.getConnection();
}

  /**
   * 
   * @throws javax.ejb.EJBException
   */
  private void getDataSource() throws EJBException
  {
    try
    {
      initialContext = new InitialContext();
      dataSource = (DataSource)initialContext.lookup("java:comp/env/jdbc/DB");
    }
    catch( Exception e )
    {
      //Logger.error(FILE_NAME,"Exception in getDataSource() method of VendorRegistrationSession bean: "+e.toString());
      logger.error(FILE_NAME+"Exception in getDataSource() method of VendorRegistrationSession bean: "+e.toString());

    }
  }

  /**
   * 
   * @param sessionCtx
   * @throws javax.ejb.EJBException
   */
 	public void	setSessionContext(SessionContext sessionCtx)throws javax.ejb.EJBException
	{
		this.sessionContext	= sessionCtx;
		getDataSource();
		operationsImpl	= new OperationsImpl();
	}
	private	void closeConnection(ResultSet rs,Statement	st,Connection connection )
	{
		try
		{
			if(	rs != null )
      {
				rs.close();
      }
			if(	st != null )
      {
				st.close();
      }
			if(	connection != null )
      {
				connection.close();
      }
		}
		catch( SQLException	sqlexp )
		{
			//Logger.error(FILE_NAME,"closeConnection(st)",sqlexp);
      logger.error(FILE_NAME+"closeConnection(st)"+sqlexp);
		}
	} // End of closeConnection(ResultSet rs,Statement	st,Connection connection ) method

}