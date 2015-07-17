package com.qms.setup.ejb.sls;
import com.foursoft.esupply.common.bean.ESupplyGlobalParameters;
import com.foursoft.esupply.common.exception.FoursoftException;
import com.foursoft.esupply.common.java.CountryMasterDOB;
import com.foursoft.esupply.common.java.FoursoftConfig;
import com.foursoft.esupply.common.java.LookUpBean;
import com.foursoft.esupply.common.util.ConnectionUtil;
//import com.foursoft.esupply.common.util.Logger;
import com.foursoft.esupply.common.util.StringUtility;
import com.qms.operations.quote.dob.QuoteFinalDOB;
import com.qms.operations.quote.dob.QuoteMasterDOB;
import com.qms.setup.java.QMSAttachmentDOB;
import com.qms.setup.java.QMSAttachmentDetailDOB;
import com.qms.setup.java.QMSAttachmentFileDOB;
import com.qms.setup.java.QMSEmailTextDOB;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.StringReader;
import java.sql.Blob;
import org.apache.log4j.Logger;
import com.foursoft.etrans.common.bean.Address;
import com.foursoft.etrans.common.dao.AddressDAO;
import com.foursoft.etrans.common.util.ejb.sls.OIDSession;
import com.foursoft.etrans.common.util.ejb.sls.OIDSessionHome;
import com.foursoft.etrans.common.util.java.OperationsImpl;
import com.foursoft.etrans.sea.setup.port.bean.PortMasterJSPBean;
import com.foursoft.etrans.setup.IATARateMaster.dao.IATARatesDAO;
import com.foursoft.etrans.setup.IATARateMaster.java.IATADtlModel;
import com.foursoft.etrans.setup.carrier.bean.CarrierDetail;
import com.foursoft.etrans.setup.codecust.bean.CodeCustModelDOB;
import com.foursoft.etrans.setup.codecust.bean.CodeCustomisationDOB;
import com.foursoft.etrans.setup.codecust.bean.CodeCustomiseJSPBean;
import com.foursoft.etrans.setup.codecust.exception.CodeCustNotDoneException;
import com.foursoft.etrans.setup.commodity.bean.CommodityJspBean;
import com.foursoft.etrans.setup.company.bean.HORegistrationJspBean;
import com.foursoft.etrans.setup.country.bean.CountryMaster;
import com.foursoft.etrans.setup.customer.dao.CustomerDAO;
import com.foursoft.etrans.setup.customer.java.CustContactDtl;
import com.foursoft.etrans.setup.customer.java.CustomerModel;
import com.foursoft.etrans.setup.gateway.bean.GatewayJSPBean;
import com.foursoft.etrans.setup.jointventure.bean.JointVentureJspBean;
import com.foursoft.etrans.setup.location.bean.LocationMasterJspBean;
import com.foursoft.etrans.setup.servicelevel.bean.ServiceLevelJspBean;
import com.foursoft.etrans.setup.taxes.bean.TaxMaster;
import com.foursoft.etrans.setup.terminal.bean.TerminalRegJspBean;
import com.foursoft.etrans.setup.terminal.ejb.bmp.TerminalRegistrationEntity;
import com.foursoft.etrans.setup.terminal.ejb.bmp.TerminalRegistrationEntityHome;
import com.foursoft.etrans.setup.vendorregistration.ejb.bmp.VendorRegistrationEnitity;
import com.foursoft.etrans.setup.vendorregistration.ejb.bmp.VendorRegistrationEnitityHome;
import com.foursoft.etrans.setup.vendorregistration.ejb.bmp.VendorRegistrationEnitityPK;
import com.foursoft.etrans.setup.vendorregistration.java.VendorRegistrationJava;
import com.foursoft.etrans.truck.setup.bean.ETransTruckingVendor;

import com.qms.operations.charges.java.BuySellChargesEnterIdDOB;
import com.qms.setup.chargebasis.ejb.bmp.ChargeBasisMasterEntityBeanLocal;
import com.qms.setup.chargebasis.ejb.bmp.ChargeBasisMasterEntityBeanLocalHome;
import com.qms.setup.chargebasis.ejb.bmp.ChargeBasisMasterEntityBeanPK;
import com.qms.setup.chargegroup.dao.ChargeGroupingDAO;
import com.qms.setup.chargemaster.ejb.bmp.ChargesMasterEntityBeanLocal;
import com.qms.setup.chargemaster.ejb.bmp.ChargesMasterEntityBeanLocalHome;
import com.qms.setup.chargemaster.ejb.bmp.ChargesMasterEntityBeanPK;
import com.qms.setup.dao.IndustryRegDAO;
import com.qms.setup.dao.ListMasterDAO;
import com.qms.setup.dao.MarginLimitMasterDAO;
import com.qms.setup.ejb.bmp.IndustryRegEntityLocal;
import com.qms.setup.ejb.bmp.IndustryRegEntityLocalHome;
import com.qms.setup.ejb.bmp.IndustryRegEntityPK;
import com.qms.setup.ejb.bmp.ListMasterEntityBeanLocal;
import com.qms.setup.ejb.bmp.ListMasterEntityBeanLocalHome;
import com.qms.setup.ejb.bmp.ListMasterEntityBeanPK;
import com.qms.setup.ejb.bmp.MarginLimitMasterBeanLocal;
import com.qms.setup.ejb.bmp.MarginLimitMasterBeanLocalHome;
import com.qms.setup.ejb.bmp.MarginLimitMasterBeanPK;
import com.qms.setup.java.ChargeBasisMasterDOB;
import com.qms.setup.java.ChargeGroupingDOB;
import com.qms.setup.java.ChargesMasterDOB;
import com.qms.setup.java.IndustryRegDOB;
import com.qms.setup.java.ListMasterDOB;
import com.qms.setup.java.MarginLimitMasterDOB;
import com.qms.setup.java.QMSAdvSearchHelperObj;
import com.qms.setup.java.QMSAdvSearchLOVDOB;
import com.qms.setup.java.QMSContentDOB;
import java.rmi.RemoteException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.ejb.EJBException;
import javax.ejb.FinderException;
import javax.ejb.ObjectNotFoundException;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import oracle.jdbc.OracleTypes;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
public class SetUpSessionBean implements SessionBean 
{
  private static final String FILE_NAME = "SetUpSessionBean.java";
	private	 SessionContext	sessionContext = null;
	private	 String	 error = null;
	private	 OperationsImpl	operationsImpl = null;
  private	 DataSource		dataSource	   = null;
  private static Logger logger = null;
 
  public SetUpSessionBean()
  {
    logger  = Logger.getLogger(SetUpSessionBean.class);
  }

  public void ejbCreate()
  {
		operationsImpl	= new OperationsImpl();
		operationsImpl.createDataSource();
    
  }

  public void ejbActivate()
  {
  }

  public void ejbPassivate()
  {
  }

  public void ejbRemove()
  {
    operationsImpl=null;
  }

  public void setSessionContext(SessionContext ctx)
  {
	  this.sessionContext	= ctx;
		operationsImpl  = new OperationsImpl();
  }

  /**
   * 
   * @param searchId
   * @return 
   */
public java.util.Vector[] getChargeIds_Taxes(String searchId)
{
    ////System.out.println("this is inside the getChargeids_taxes()...");	
	Connection        	con			   		= 	null;
	java.util.Vector	vecChargeDetails[] 	=	new java.util.Vector[3];
	java.util.Vector	vecChargeId  		=	new java.util.Vector();
	java.util.Vector	vecAbbrChargeDesc  	=	new java.util.Vector();
	java.util.Vector 	vecDesc      		= 	new java.util.Vector();
	PreparedStatement pre	= null;		  
	ResultSet		  res	= null;
	String            chargeType ="I";		 
	String		sqlSelQuery1	=	"SELECT CHARGEID,ABBRCHARGEDESC,CHARGEDESC FROM FS_FR_CHARGESMASTER WHERE  CHARGEIDTYPE=? AND CHARGEID LIKE '"+searchId+"%' ORDER BY LPAD(CHARGEID,6,'0')";
	

	try
	{ 
		con = getConnection();
		pre = con.prepareStatement(sqlSelQuery1);
		////System.out.println(" sqlSelQuery  "+sqlSelQuery1);		
		pre.setString(1,chargeType);
		res=pre.executeQuery();
		while(res.next())
		{
			vecChargeId.addElement(res.getString(1));
			vecAbbrChargeDesc.addElement(res.getString(2));
			vecDesc.addElement(res.getString(3));
		}
		vecChargeDetails[0] = vecChargeId;
		vecChargeDetails[1] = vecAbbrChargeDesc;
		vecChargeDetails[2] = vecDesc;
	}catch(SQLException sqlex)
	{
     //Logger.error(FILE_NAME,"SQL Exception ingetChargeIds_Taxes() method of EtransHOSuperUserSessionBean  : "+sqlex.toString());
     logger.error(FILE_NAME+"SQL Exception ingetChargeIds_Taxes() method of EtransHOSuperUserSessionBean  : "+sqlex.toString());
		//System.out.println("SQL Exception ingetChargeIds_Taxes() method of EtransHOSuperUserSessionBean  : "+ sqlex.toString());
	}
	catch(Exception e)
	{
    //Logger.error(FILE_NAME,"Naming Exception in getChargeIds_Taxes() method of EtransHOSuperUserSessionBean  : "+e.toString());
    logger.error(FILE_NAME+"Naming Exception in getChargeIds_Taxes() method of EtransHOSuperUserSessionBean  : "+e.toString());
		//System.out.println("Naming Exception in getChargeIds_Taxes() method of EtransHOSuperUserSessionBean  : "+ e.toString());
	}
	finally{
		try{
			if(res !=null)
			{res.close(); }                
			if( pre != null )
			{pre.close();}
			if(con!= null )
			{con.close();}
		}catch(SQLException e)
	  	{
       //Logger.error(FILE_NAME,"Exception in finally block of getChargeIds_Taxes() method of EtransHOSuperUserSessionBean : "+e.toString());
       logger.error(FILE_NAME+"Exception in finally block of getChargeIds_Taxes() method of EtransHOSuperUserSessionBean : "+e.toString());
			//System.out.println("Exception in finally block of getChargeIds_Taxes() method of EtransHOSuperUserSessionBean : "+e.toString());
		}
	  }//finally
	return vecChargeDetails;
} // end of getChargeIds


  /**
   * 
   * @param terminalId
   * @return 
   */
	public java.util.ArrayList getAllCustomerDetails(String terminalId) 
	{
	
		Connection   							  connection        = 	null;
		Statement    							  stmt          	= 	null;
		ResultSet    							  rs            	= 	null;
		ArrayList									allDetails		=	null;
		String										sql				=	null;
		CustomerModel								custModel		=	null;
		try
		{
			allDetails=new ArrayList();
			connection = operationsImpl.getConnection();
			stmt           =  connection.createStatement();
			sql="SELECT CM.ABBRNAME, CM.COMPANYNAME,CM.OPERATIONS_EMAILID,A.STATE,A.CITY,A.ZIPCODE,A.ADDRESSLINE1,A.ADDRESSLINE2 "+
				" FROM FS_FR_CUSTOMERMASTER CM,FS_ADDRESS A WHERE  CM.CUSTOMERADDRESSID=A.ADDRESSID AND CM.TERMINALID='"+terminalId+"'";
			rs=stmt.executeQuery(sql);
			while(rs.next())
			{
				custModel=new CustomerModel();
				custModel.setAbbrName(rs.getString(1)!=null ? rs.getString(1):"");
				custModel.setCompanyName(rs.getString(2)!=null ? rs.getString(2):"");				
				custModel.setOpEmailId(rs.getString(3)!=null ? rs.getString(3):"");
				custModel.setSCode(rs.getString(4)!=null ? rs.getString(4):"");       //setting the state
                custModel.setCorpCustomerId(rs.getString(5)!=null ? rs.getString(5):""); //setting the city
				custModel.setContactName(rs.getString(6)!=null ? rs.getString(6):"");  //setting the zipcode
				custModel.setNotes(rs.getString(7)!=null ? rs.getString(7):"");		//setting the first address line 1
				custModel.setRegistered(rs.getString(8)!=null ? rs.getString(8):"");	//setting the second address line 2

				allDetails.add(custModel);
			}

		}
		catch(SQLException sqle)
		{
			//Logger.error(FILE_NAME,"SQLEXception in getRemarks()-->"+sqle.toString());
      logger.error(FILE_NAME+"SQLEXception in getRemarks()-->"+sqle.toString());
			sqle.printStackTrace();
			throw new EJBException(sqle.toString());
		}
		catch(Exception e)
		{
			//Logger.error(FILE_NAME,"EXception in getRemarks()-->"+e.toString());
      logger.error(FILE_NAME+"EXception in getRemarks()-->"+e.toString());
			e.printStackTrace();
			throw new EJBException(e.toString());
		}
		finally
		{
			try
			{
				if(rs!=null)
					{rs.close();}
				if(stmt!=null)
					{stmt.close();}
				if(connection!=null)
					{connection.close();}
			
			}
			catch(SQLException sqle)
			{
				//Logger.error(FILE_NAME,"SQLEXception in finally getRemarks()-->"+sqle.toString());
        logger.error(FILE_NAME+"SQLEXception in finally getRemarks()-->"+sqle.toString());
				sqle.printStackTrace();
				throw new EJBException(sqle.toString());
			}
	
		}
		return allDetails;
	
	}
	/**
   * 
   * @param customerId
   * @param esupplyGlobalParameters
   * @param customerType
   * @param registrationLevel
   * @param operation
   * @return 
   */
public ArrayList getCustomerDetail(String customerId, ESupplyGlobalParameters esupplyGlobalParameters,String customerType,String registrationLevel,String operation)
			
	{
			CustomerModel	customerModel	= null;
			Address			addressModel	= null;	
			ArrayList		arrayList		= new ArrayList();
			Connection		connection		= null;
			Statement		stmt			= null;		 
			String			sqlQuery		= null;
			ResultSet		rs				= null;
	//Js
			ArrayList		custMorAddList	= new ArrayList();
// Replaced 'terminalId' with 'terminalId1' by Suneetha for TogetherJ on 12 Jan 05
			String			terminalId1		= esupplyGlobalParameters.getTerminalId();
			String			terminalQry		= "";
      String      accessType    = esupplyGlobalParameters.getAccessType();//@@Added by subrahmanyam for Enhancement_CR 167669 on 26/May/09


	    try
	    {

			if(registrationLevel==null || registrationLevel.equals("T"))//terminal Level 
			{
			//@@Commented by subrahmanyam for CR_Enhancement_167669 on 26/May/09      
//terminalQry		= " AND CUSTOMERTYPE='Customer' AND TERMINALID='"+terminalId1+"'";      
//@@Added by subrahmanyam for CR_Enhancement_167669 on 26/May/09
        if("ADMN_TERMINAL".equalsIgnoreCase(accessType))
        {
        terminalQry		= " AND CUSTOMERTYPE='Customer' AND TERMINALID IN(SELECT CHILD_TERMINAL_ID FROM FS_FR_TERMINAL_REGN WHERE PARENT_TERMINAL_ID='"+terminalId1+"')";  
        }
        else if("OPER_TERMINAL".equalsIgnoreCase(accessType))
        {
        terminalQry		= " AND CUSTOMERTYPE='Customer' AND TERMINALID IN(SELECT CHILD_TERMINAL_ID FROM FS_FR_TERMINAL_REGN WHERE PARENT_TERMINAL_ID=(SELECT PARENT_TERMINAL_ID FROM FS_FR_TERMINAL_REGN WHERE CHILD_TERMINAL_ID='"+terminalId1+"'))";    
        }
        else
				terminalQry		= " AND CUSTOMERTYPE='Customer' AND TERMINALID='"+terminalId1+"'";
//@@Ended by subrahmanyam for The CR_Enhancement_167669 on 26/May/09        

			}else if(registrationLevel.equals("C") && operation.equals("View"))
			{
				terminalQry		= " AND CUSTOMERTYPE='Corporate' ";
			}else if(registrationLevel.equals("C") ) //like modify ,delete
			{
				terminalQry		= " AND CUSTOMERTYPE='Corporate' AND TERMINALID='"+terminalId1+"'";
			}

			connection 	=	getConnection();
			sqlQuery		=   " SELECT CUSTOMERID,TERMINALID FROM FS_FR_CUSTOMERMASTER WHERE CUSTOMERID='"+customerId+"' "+terminalQry+" AND REGISTERED='"+customerType+"'";
		    stmt		=	connection.createStatement();			   
		   rs= stmt.executeQuery(sqlQuery);
		   if(!rs.next())
		   {
			 return null;
		   }	
			terminalId1 = rs.getString(2);
		   rs.close();	
			
			CustomerDAO      customerDAO     =  new CustomerDAO(); 
			AddressDAO      addressDAO       =  new AddressDAO(); 
			customerDAO.findByPrimaryKey(customerId,esupplyGlobalParameters.getTerminalId());
			customerModel	=   customerDAO.load(customerId,terminalId1);
			addressModel	=   addressDAO.load(customerModel.getCustomerAddressId());
		//js
			ArrayList	custAddrList	=	new ArrayList();
			custAddrList				=	getCustomerMoreAddressIdsList(customerId,terminalId1,connection);
			int custAddrListSize	=	custAddrList.size();
			for(int i=0;i<custAddrListSize;i++){
				CustomerModel	custModelObj	=	null;
				custModelObj	=	(CustomerModel)custAddrList.get(i);
				custModelObj.getCustomerAddressId();
				custMorAddList.add(addressDAO.load(custModelObj.getCustomerAddressId()));
			}
	        arrayList.add(customerModel);
			arrayList.add(addressModel);
	//JS	
			arrayList.add(custAddrList);
			arrayList.add(custMorAddList);
	    }catch(Exception ex)
	     {
	      //Logger.error(FILE_NAME,"Exception :: getCustomerDetail() ::: " + ex);
        logger.error(FILE_NAME+"Exception :: getCustomerDetail() ::: " + ex);
		  throw new javax.ejb.EJBException(ex.toString());
	     }
		  finally
		  {
		      try
		      {
		       if(stmt!=null)
				 {stmt.close();}
				if(rs!=null)
				 {rs.close();}
			   if(connection!=null)
				 {connection.close();}	 	 
			  }catch(Exception ex)
			  {
			   //Logger.error(FILE_NAME,"Exception caught :: finally :: validateTermsCusts() " + ex.toString() );
         logger.error(FILE_NAME+"Exception caught :: finally :: validateTermsCusts() " + ex.toString() );
			  }
		  
		  }
		  
	      return arrayList;
	}

 

	/**
   * 
   * @param customerModel
   * @param addressModel
   * @param eSupplyGlobalParameters
   * @param customerAddressList
   * @return 
   */
  public String createCustomerDetails(CustomerModel customerModel, Address addressModel, ESupplyGlobalParameters eSupplyGlobalParameters,ArrayList customerAddressList)throws FoursoftException,CodeCustNotDoneException
{
	 		// @@ Suneetha Added on 20050308 for Bulk Invoicing
			Connection 		connection		= null;
			// @@  20050308 for Bulk Invoicing
		  String sqlExist = "select CUSTOMERID from FS_FR_CUSTOMERMASTER where CUSTOMERID  = ?";
      PreparedStatement pstmt1 = null;
      ResultSet       rs     = null;
		try
	    {
       
       connection = getConnection();
       
       pstmt1 = connection.prepareStatement(sqlExist);
       pstmt1.setString(1,customerModel.getCustomerId());
       rs = pstmt1.executeQuery();
       
       if(rs.next())
       {
         throw new FoursoftException();
       }
       

      
		// @@ Suneetha Added on 20050308 for Bulk Invoicing
		   	
		// @@ 20050308 for Bulk Invoicing
	       InitialContext jndiContext  = new InitialContext();
		   OIDSessionHome oidHome      =(OIDSessionHome)jndiContext.lookup("OIDSessionBean");
		   OIDSession	  oidRemote	   =(OIDSession)oidHome.create();
		   CustomerDAO	  customerDAO  = new CustomerDAO();
		   AddressDAO	  addressDAO   = new AddressDAO();
		   String   	 customerId	 	= customerModel.getCustomerId(); 
			 CodeCustModelDOB codeCustDOB	=	new CodeCustModelDOB();
		   ArrayList	custMoreAddTabList	= new ArrayList();
		   ArrayList	custAddressTabList	= new ArrayList();
  	       int 		addressId  =  oidRemote.getAddressOID();
		    if(customerId==null||customerId.length()==0)
		   {
				codeCustDOB.companyId	=	customerModel.getAbbrName();
        //codeCustDOB.companyId	=	eSupplyGlobalParameters.getCompanyId();
//@@ Commented by subrahmanyam for 167669 on 27/04/09        
				/*if(eSupplyGlobalParameters.getLocationId().length() == 6)
					{codeCustDOB.locationId	=	eSupplyGlobalParameters.getLocationId().substring(3,6);}
				if(eSupplyGlobalParameters.getLocationId().length() == 4 || eSupplyGlobalParameters.getLocationId().length() == 7)
					{codeCustDOB.locationId	=	eSupplyGlobalParameters.getLocationId().substring(0,3);}
        codeCustDOB.terminalId = eSupplyGlobalParameters.getTerminalId();
				customerId 		= 	oidRemote.getCodeCustomisationId( "CUSTOMER",codeCustDOB );*/
//@@ Added by subrahmanyam for 167669 on 27/04/09

    if(customerModel.getTerminalId().length() == 6)
        codeCustDOB.locationId = customerModel.getTerminalId().substring(3,6);
    if(customerModel.getTerminalId().length() == 4 || customerModel.getTerminalId().length() == 7)
        codeCustDOB.locationId = customerModel.getTerminalId().substring(0,3);
  //@@ Ended by subrahmanyam for 167669 on 27/04/09
     
      
       codeCustDOB.terminalId = eSupplyGlobalParameters.getTerminalId();
				customerId 		= 	oidRemote.getCodeCustomisationId( "CUSTOMER",codeCustDOB );
             //customerId 		= 	oidRemote.getCustomerOID(eSupplyGlobalParameters.getTerminalId(),customerModel.getAbbrName());
        
			  
         	customerModel.setCustomerId(customerId);
		   }	    
		   addressModel.setAddressId(addressId);
		   customerModel.setCustomerAddressId(addressId);
		   customerModel.setRegistered("R");
//JS		
		if(customerAddressList!=null){
			int custAddrListSize	=	customerAddressList.size();
			for(int i=0;i<custAddrListSize;i++){
				
				Address addDetl	=	(Address)customerAddressList.get(i);
				int		addressId1  =  oidRemote.getAddressOID();
		
				custAddressTabList.add(new Address( addressId1,addDetl.getAddressLine1(),addDetl.getAddressLine2(),addDetl.getCity(),addDetl.getState(),addDetl.getZipCode(),addDetl.getCountryId(),addDetl.getPhoneNo(),addDetl.getEmailId(),"",""));
				custMoreAddTabList.add(new CustomerModel(customerId, eSupplyGlobalParameters.getTerminalId(),addressId1,addDetl.getContactName(),addDetl.getDesignation(),addDetl.getDelFlag(),addDetl.getAdddressType()));

			}
		}

		   customerModel.setCustomerAddressId(addressDAO.create(addressModel));
		   customerDAO.create(customerModel);
		   // @@ Suneetha Added on 20050308 for Bulk Invoicing
				if("Y".equals(customerModel.getBulkInvoiceRequired()))
			    {
					int result=	createCustomerInvoiceInfo(customerId,eSupplyGlobalParameters.getTerminalId(),customerModel.getInvoiceFrequencyValidDate(),customerModel.getInvoiceFrequencyFlag(),customerModel.getInvoiceInfo(),connection);
			   }
			// @@ 20050308 for Bulk Invoicing
		   addressDAO.create(custAddressTabList);
		   customerDAO.create(custMoreAddTabList);
        // Added K.N.V.Prasada Reddy
       if((customerModel.getServiceLevelId()!=null)&&(customerModel.getServiceLevelId().length()>0))
               {
                 createCustServiceLevel(customerId,eSupplyGlobalParameters.getTerminalId(),customerModel.getServiceLevelId(),connection);
               }

		   operationsImpl.setTransactionDetails(eSupplyGlobalParameters.getTerminalId(),
												eSupplyGlobalParameters.getUserId(),
												"CustomerReg",
												customerId,
												eSupplyGlobalParameters.getLocalTime(),
												"Add");   
		  
	    }
      catch(CodeCustNotDoneException ccnd)
      {
        ccnd.printStackTrace();
        throw new CodeCustNotDoneException("Code Customization Has Not Been Done.");
      }
      catch(FoursoftException e)
      {
        e.printStackTrace();
        throw new FoursoftException();
      }catch(Exception ex)
	    {
	     //Logger.error(FILE_NAME,"Exception :: createCustomerDetails() :::: " + ex.getMessage());
       logger.error(FILE_NAME+"Exception :: createCustomerDetails() :::: " + ex.getMessage());
	     throw new EJBException(ex.toString());
	    } 
		// @@ Suneetha Added on 20050308 for Bulk Invoicing
		finally
		{
		     try
		      {
            if(rs!=null)
              {rs.close();}
            if(pstmt1!=null)
              {pstmt1.close();}
            if(connection!=null)
              connection.close();	 	 
			  }catch(Exception ex)
			  {
			   //Logger.error(FILE_NAME,"Exception caught :: finally :: validateTermsCusts() " + ex.toString() );
         logger.error(FILE_NAME+"Exception caught :: finally :: validateTermsCusts() " + ex.toString() );
			  }  
		  }
   		// @@  20050308 for Bulk Invoicing
	   return customerModel.getCustomerId(); 
	}

	 /**
   * 
   * @param customerModel
   * @param addressModel
   * @param esupplyGlobalParameters
   * @param costAddressListME
   * @param costAddressListM
   * @return 
   */
	public boolean  updateCustomerDetails(CustomerModel customerModel,Address addressModel,ESupplyGlobalParameters esupplyGlobalParameters,ArrayList costAddressListME,ArrayList costAddressListM)
				{
	       
		  // Address	addObj	=	null;
			// @@ Suneetha Added on 20050308 for Bulk Invoicing
			Connection 		connection		= null;
			// @@  20050308 for Bulk Invoicing
		
		try
	    {
			// @@ Suneetha Added on 20050308 for Bulk Invoicing
			  connection = getConnection();	
			// @@ 20050308 for Bulk Invoicing
		   
		  	 CustomerDAO customerDAO = new CustomerDAO();
			 AddressDAO addressDAO = new AddressDAO();

			 customerDAO.findByPrimaryKey(customerModel.getCustomerId(),esupplyGlobalParameters.getTerminalId());

			 addressModel.setAddressId(customerModel.getCustomerAddressId());

			 addressDAO.store(addressModel);    
			 customerDAO.store(customerModel);

			// @@ Suneetha Added on 20050308 for Bulk Invoicing
				if("Y".equals(customerModel.getBulkInvoiceRequired()))
			    {
					int result=	createCustomerInvoiceInfo(customerModel.getCustomerId(), esupplyGlobalParameters.getTerminalId(),customerModel.getInvoiceFrequencyValidDate(),customerModel.getInvoiceFrequencyFlag(),customerModel.getInvoiceInfo(),connection);
			   }
			// @@ 20050308 for Bulk Invoicing
		

			 //JS 
			  customerDAO.store(costAddressListME,costAddressListM,customerModel.getCustomerId(),esupplyGlobalParameters.getTerminalId());
			  if(costAddressListM!=null && costAddressListM.size() > 0){
					   InitialContext	jndiContext  = new InitialContext();
					   OIDSessionHome	oidHome      = (OIDSessionHome)jndiContext.lookup("OIDSessionBean");
					   OIDSession		oidRemote	 = (OIDSession)oidHome.create();
					   int costAddrListMSize	=	costAddressListM.size();
					  for(int i=0;i<costAddrListMSize;i++){
						  Address	custModelObj	=	(Address)costAddressListM.get(i);
						  if(custModelObj.getDelFlag().equals("N")){
							int 	addressId  =  oidRemote.getAddressOID();
							addressDAO.insertIntoAddress(addressId,custModelObj);
							customerDAO.create(addressId,customerModel.getCustomerId(),esupplyGlobalParameters.getTerminalId(),custModelObj);
						  }
					  }
			  }//End of code JS
        //K.N.V.Prasada Reddy
			 /*  if ((customerModel.getServiceLevelId()!=null)&&(customerModel.getServiceLevelId().length()>0))
                  {
                  deleteCustServiceLevel(customerModel.getCustomerId(),connection);
                  createCustServiceLevel(customerModel.getCustomerId(),esupplyGlobalParameters.getTerminalId(),customerModel.getServiceLevelId(),connection);
                          
                  }
             else
				  {
					deleteCustServiceLevel(customerModel.getCustomerId(),connection);
				  }//End by prasad
*/
		
			operationsImpl.setTransactionDetails(esupplyGlobalParameters.getTerminalId(),
												 esupplyGlobalParameters.getUserId(),
												 "CustomerReg",
												 customerModel.getCustomerId(),
												 esupplyGlobalParameters.getLocalTime(),
												 "Modify");
			
			
			return true;

	       }catch(Exception ex)
	       {
	        //Logger.error(FILE_NAME,"Exception :: updateCustomerDetails() ::: " + ex.toString());
          logger.error(FILE_NAME+"Exception :: updateCustomerDetails() ::: " + ex.toString());
	        throw new javax.ejb.EJBException(ex.toString());
	       }
	 	// @@ Suneetha Added on 20050308 for Bulk Invoicing
		  finally
		  {
		     try
		      {
			   if(connection!=null)
				 connection.close();	 	 
			  }catch(Exception ex)
			  {
			   //Logger.error(FILE_NAME,"Exception caught :: finally :: validateTermsCusts() " + ex.toString() );
         logger.error(FILE_NAME+"Exception caught :: finally :: validateTermsCusts() " + ex.toString() );
			  }  
		  }
   		// @@  20050308 for Bulk Invoicing
	
	}
	//End of Modify Logic JS

	/**
   * 
   * @param customerModel
   * @param addressModel
   * @param esupplyGlobalParameters
   * @return 
   */
	public boolean deleteCustomerDetail(CustomerModel customerModel,Address addressModel,ESupplyGlobalParameters esupplyGlobalParameters)
		   
	{
        
			Statement		stmt			= null;	
			Connection 		connection		= null;
			//ResultSet		rs				= null;//Commented By RajKumari on 30-10-2008 for Connection Leakages.
			ArrayList		alist			= new ArrayList();
			int count=0;
			
		try
	     {
			connection = getConnection();
			stmt	   = connection.createStatement();
			CustomerDAO customerDAO = new CustomerDAO();
			AddressDAO addressDAO = new AddressDAO();
			String CustomerId1=customerModel.getCustomerId();
			String TerminalId1=customerModel.getTerminalId();
  		/*String PRQCustomerQuery="SELECT DISTINCT CUSTOMERID FROM QMS_PICKUPREQUEST  WHERE CUSTOMERID='"+CustomerId1+"' AND TERMINALID='"+TerminalId1+"'";
		    rs=stmt.executeQuery(PRQCustomerQuery);
			if(rs.next())
			 {
				count++;
			 }
			rs=null;
			String HouseCustomerQuery="SELECT DISTINCT SHIPPERID FROM QMS_HOUSEDOCHDR WHERE SHIPPERID='"+CustomerId1+"' AND TERMINALID='"+TerminalId1+"'";
			rs=stmt.executeQuery(HouseCustomerQuery);
			if(rs.next())
			 {
				count++;
			 }
			rs=null;
			String ContarctCustomerQuery="SELECT DISTINCT CD.SHIPPERID FROM QMS_CUSTCONTRACTDTL CD,QMS_CUSTCONTRACTMASTER CM WHERE CM.CONTRACTID=CD.CONTRACTID AND CD.SHIPPERID='"+CustomerId1+"' AND CM.TERMINALID='"+TerminalId1+"'";
			rs=stmt.executeQuery(ContarctCustomerQuery);
			if(rs.next())
			 {
				count++;
			 }
			if(count>0)
			{return false;}
			else*/
		// {
			// Delete Prefered Service Level 
			//deleteCustServiceLevel(customerModel.getCustomerId(),connection);//By Prasada Reddy
			customerDAO.remove(customerModel.getCustomerId(),customerModel.getTerminalId());
//			customerDAO.remove(customerModel.getCustomerId(),customerModel.getTerminalId(),addressModel.getAddressId());
		//	addressDAO.removeAddress(addressModel.getAddressId());
//JS
			alist =(ArrayList)getCustomerMoreAddressIdsList(customerModel.getCustomerId(),customerModel.getTerminalId(),connection);		
			 if(alist!=null && alist.size() > 0){
					int aListSize	=	alist.size();
				for(int i=0;i<aListSize;i++){	
					CustomerModel	custObj	=	(CustomerModel)alist.get(i);
				//	addressDAO.removeAddress(custObj.getCustomerAddressId());
				}//for
			 }//if

		 
		 
//JS
	        operationsImpl.setTransactionDetails(esupplyGlobalParameters.getTerminalId(),
												 esupplyGlobalParameters.getUserId(),
												 "CustomerReg",
												 customerModel.getCustomerId(),
												 esupplyGlobalParameters.getLocalTime(),
												 "Delete");
	        return true;
		// }
	     }catch(Exception ex)
	      {
	       //Logger.error(FILE_NAME,"Exception :: deleteCustomerDetail() :: " + ex.toString());
         logger.error(FILE_NAME+"Exception :: deleteCustomerDetail() :: " + ex.toString());
	       throw new javax.ejb.EJBException(ex.toString());
	      }
		  finally
	     {
	       try
	       {
	       	/*if(rs!=null)
			   {rs.close();}*///Commented By RajKumari on 30-10-2008 for Connection Leakages.
	       }catch(Exception ex)
	       {
	        //Logger.error(FILE_NAME,"Exception finally ::: deleteCustomerDetail() " + ex.toString() );
          logger.error(FILE_NAME+"Exception finally ::: deleteCustomerDetail() " + ex.toString() );
	       }
	       ConnectionUtil.closeConnection(connection,stmt);       
		   
	     }
	 
	}

	/**
   * 
   * @param customerModel
   * @return 
   */
  public boolean upgradeCustomerStatus(CustomerModel customerModel) 
		   
    {
    Connection 		connection		= null;
         try
	     {
            connection = getConnection();
            customerModel.setRegistered("R");
		    customerModel.setTypeOfCustomer("Customer");
			customerModel.setUpgradeFlag(true);
			 
			 CustomerDAO customerDAO = new CustomerDAO();
			 customerDAO.findByPrimaryKey(customerModel.getCustomerId(),customerModel.getTerminalId());
			 customerDAO.store(customerModel);
              // Added K.N.V.Prasada Reddy
             if((customerModel.getServiceLevelId()!=null)&&(customerModel.getServiceLevelId().length()>0))
               {
                 createCustServiceLevel(customerModel.customerId,customerModel.getTerminalId(),customerModel.getServiceLevelId(),connection);
               }

	        return true;
	     }catch(Exception ex)
	     {
	      //Logger.error(FILE_NAME,"Exception :: upgradeCustomerStatus() ::: " +  ex);
        logger.error(FILE_NAME+"Exception :: upgradeCustomerStatus() ::: " +  ex);
	      throw new javax.ejb.EJBException(ex.toString());
	     }finally
         {
             ConnectionUtil.closeConnection(connection,null,null);  
         }
    }

	/**
   * 
   * @param customerId
   * @param shipOrCons
   * @param terminalId
   * @param shipOrConsTerminalId
   * @return 
   */
	public ArrayList getShipConsDtls(String customerId,String shipOrCons,String terminalId,String shipOrConsTerminalId) 
	{
	       ArrayList 		mainList    		=  new ArrayList();
		   StringBuffer     selectQuery  		=  new StringBuffer();
		   String			shipOrConsQuery 	=  null;		
		   Connection		connection	 		=  null;
		   Statement		stmt		 		=  null ;//, stmt1=null;//Commented By RajKumari on 30-10-2008 for Connection Leakages.
		   ResultSet		rs			 		=  null ;		//Modified By RajKumari on 30-10-2008 for Connection Leakages.
	       ArrayList		checkList			=  new ArrayList();				   
	    
	    
	       if(shipOrCons.equals("ShipLOV"))
	       {
	        shipOrConsQuery = "  SELECT A.CUSTOMERID,A.COMPANYNAME,A.TERMINALID FROM"
							+ " FS_FR_CUSTOMERMASTER A  WHERE A.CUSTOMERID NOT IN"
							+ " (SELECT CUSTOMER_ID2 FROM FS_FR_CUSTOMERS_MAPPING WHERE "
							+ " CUSTOMER_ID1='"+customerId+"' AND  TERMINAL_ID1='"+terminalId+"'"
							+ " AND  CUSTOMER_TYPE='S') "
							+ " AND A.TERMINALID='"+shipOrConsTerminalId+"' AND REGISTERED='R' AND CUSTOMERTYPE='Customer' " 
							+ " ORDER BY A.CUSTOMERID ";
							 
	       
	       }
		   else if(shipOrCons.equals("ConsLOV"))
		   {
		     shipOrConsQuery = "  SELECT A.CUSTOMERID,A.COMPANYNAME,A.TERMINALID FROM"
							+ " FS_FR_CUSTOMERMASTER A  WHERE A.CUSTOMERID NOT IN"
							+ " (SELECT CUSTOMER_ID2 FROM FS_FR_CUSTOMERS_MAPPING WHERE "
							+ "CUSTOMER_ID1='"+customerId+"' AND  TERMINAL_ID1='"+terminalId+"'"
							+ " AND  CUSTOMER_TYPE='C') "
							+ " AND A.TERMINALID='"+shipOrConsTerminalId+"' AND REGISTERED='R' AND CUSTOMERTYPE='Customer' " 
							+ "	ORDER BY A.CUSTOMERID";
		   }
			
	    
	    
	    try
	    {
	         connection  =  getConnection();
			 stmt  = connection.createStatement();
			 rs	= stmt.executeQuery(shipOrConsQuery);			
			 while(rs.next())
			 {		  
			          StringBuffer tempBuffer = new StringBuffer();
			     	  tempBuffer.append(rs.getString(1));
				 	  tempBuffer.append("[");
				 	  tempBuffer.append(rs.getString(2));
				 	  tempBuffer.append(",");
				 	  tempBuffer.append(rs.getString(3));
				 	  tempBuffer.append("]");
				 	  
				 	  mainList.add(tempBuffer.toString());
			    
			    
			 }
	    
	     }catch(Exception ex)
	     {
	      //Logger.error(FILE_NAME,"Exception caught in getShipConsDtls :::: " + ex.toString());
        logger.error(FILE_NAME+"Exception caught in getShipConsDtls :::: " + ex.toString());
		  throw new javax.ejb.EJBException(ex.toString());
	     }   
	     finally
	     {
	       try
	       {
	    	   ConnectionUtil.closeConnection(connection,stmt,rs);   
	       }catch(Exception ex)
	       {
	        //Logger.error(FILE_NAME,"Exception finally ::: getShipOrConsDtls() " + ex.toString() );
          logger.error(FILE_NAME+"Exception finally ::: getShipOrConsDtls() " + ex.toString() );
	       }
	           
		   
	     }
	   return mainList;  
	}

	/**
   * 
   * @param terminalId
   * @param customerId
   * @param listOfValues
   * @return 
   */
	public String validateCustomer(String terminalId,String customerId,ArrayList listOfValues) 
	{
	      String		    errorMessage =  null;
	      Connection		connection	 =	null;
		  Statement 		stmt		 =  null;		 
		  String 			sqlQuery	 =  null;
		  ResultSet			rs = null;
		  String NF	="<br>";
		  
		  
		  String[] outBoundTerminals = (String[])listOfValues.get(0);
		  String[] consigneeIds		 = (String[])listOfValues.get(1);
		  String[] inBoundTerminals	 = (String[])listOfValues.get(2);
		  String[] shipperIds		 = (String[])listOfValues.get(3);
		  
		  try
		  {
		       connection 	=	getConnection();
			   sqlQuery		=   " SELECT TERMINALID FROM FS_FR_TERMINALMASTER WHERE TERMINALID='"+terminalId+"' "
			                +   "  AND OPER_ADMIN_FLAG='O' "; 
		       stmt		=	connection.createStatement();			   
			   rs= stmt.executeQuery(sqlQuery);
			   if(!rs.next())
			   {
			     errorMessage="Not a valid Terminal.";
			   }	
		       rs = null;
			   
			    sqlQuery	=	" SELECT CUSTOMERID FROM FS_FR_CUSTOMERMASTER WHERE  CUSTOMERTYPE='Customer' AND TERMINALID='"+terminalId+"' "
			                +   " AND CUSTOMERID='"+customerId+"' "; 
		        rs= stmt.executeQuery(sqlQuery);
		       
		       
		       if(!rs.next())
			   {
			     if(errorMessage != null)
					{errorMessage=errorMessage+ NF +" Not a valid Customer for this Terminal.Please enter correct Customer Id ";	}				
				else
				{	errorMessage="Not a valid Customer for this Terminal.Please enter correct Customer Id.";}
			   }	
		       
		       boolean terminalFlag = false;
		       int	   indexOfCustomers	= 0;	
		       if(outBoundTerminals!=null && consigneeIds!=null)
		       {
		    	   int outBoundTermLen	=	outBoundTerminals.length;
			       for(int i=0;i<outBoundTermLen;i++)
			       {
			         if(outBoundTerminals[i]!=null)
			         {
			           	   sqlQuery	 =  " SELECT TERMINALID FROM FS_FR_TERMINALMASTER WHERE TERMINALID='"+outBoundTerminals[i]+"' "
			                	+       "  AND OPER_ADMIN_FLAG='O' ";   
			           
				           rs = null;
						   rs= stmt.executeQuery(sqlQuery);
				           if(!rs.next())
				           {
				             if(errorMessage != null)
								{errorMessage=errorMessage+NF+" Not a Valid OutBound Terminal Id " + outBoundTerminals[i]; }
				             else
							   { errorMessage="Not a Valid OutBound Terminal Id " + outBoundTerminals[i];  }
				           }
			               else
						    { terminalFlag = true;}
			         }
			         if(terminalFlag == true)
			         {
			           terminalFlag = false;
			           
			           
			           String tempValue ="(";
			           int consgIdsLen	=	consigneeIds.length;
			           for(int k=indexOfCustomers;k<consgIdsLen;k++)
			           {
			             if(consigneeIds[k].indexOf('&') != -1)
			             {	
							tempValue +="'"+consigneeIds[k].substring(0,consigneeIds[k].length()-1)+"')";
			                indexOfCustomers = k+1;		
			                
			                break;
			             }
			             else
						   { tempValue +="'"+consigneeIds[k]+"',";}
			           } 
			         
			           

			        
			           sqlQuery = " SELECT DISTINCT TERMINALID FROM FS_fR_CUSTOMERMASTER "
					   			+ " WHERE CUSTOMERID IN " + tempValue; 
			        
			           rs = null;
			           rs= stmt.executeQuery(sqlQuery);
			           if(rs.next())
			           {
				           if(!rs.getString(1).equals(outBoundTerminals[i]))
                   {
				             if(errorMessage!=null)  
                     {
				               errorMessage += NF +" Not Valid OutBound Customers for the Corresponding OutBound Terminal " + outBoundTerminals[i];
                     }
				             else
							  { errorMessage = " Not Valid OutBound Customers for the Corresponding OutBound Terminal " + outBoundTerminals[i];}
                   }
			          }
			        }
			      
			      }
		      }
		      
		      indexOfCustomers = 0;
		      if(inBoundTerminals!=null && shipperIds!=null)
		       {
		    	  int iBoundTermLen	=	inBoundTerminals.length;
			       for(int i=0;i<iBoundTermLen;i++)
			       {
			         if(inBoundTerminals[i]!=null)
			         {
			           	   sqlQuery	 =  " SELECT TERMINALID FROM FS_FR_TERMINALMASTER WHERE TERMINALID='"+inBoundTerminals[i]+"' "
			                	+       "  AND OPER_ADMIN_FLAG='O' ";   
			           

				           rs = null;
						   rs= stmt.executeQuery(sqlQuery);
				           if(!rs.next())
				           {
				             if(errorMessage != null)
							{	errorMessage=errorMessage+NF+" Not a Valid InBound Terminal Id " + inBoundTerminals[i];}
				             else
							   { errorMessage="Not a Valid InBound Terminal Id " + inBoundTerminals[i];}
				           }
			               else
						     {terminalFlag = true;}
			         }

			         
			         if(terminalFlag == true)
			         {
			           terminalFlag = false;
			           
			           String tempValue ="(";
			           int shpIdsLen	=	shipperIds.length;
			           for(int k=indexOfCustomers;k<shpIdsLen;k++)
			           {
			             if(shipperIds[k].indexOf('&') != -1)
			             {	
							tempValue += "'"+shipperIds[k].substring(0,shipperIds[k].length()-1)+"')";
			             	indexOfCustomers = k+1;		
			                break;
			             }
			             else
						   { tempValue += "'"+shipperIds[k]+"',";}
			             
			             
			           } 
			         

			           
			           sqlQuery = " SELECT DISTINCT TERMINALID FROM FS_fR_CUSTOMERMASTER "
					   			+ " WHERE CUSTOMERID IN " + tempValue; 
			        

			           rs = null;
			           rs= stmt.executeQuery(sqlQuery);
			           if(rs.next())
			           {
				           if(!rs.getString(1).equals(inBoundTerminals[i]))
                   {
				             if(errorMessage!=null)  
				              { errorMessage += NF+" Not Valid InBound Customers for the Corresponding InBound Terminal " +inBoundTerminals[i];}
				             else
							   {errorMessage = " Not Valid InBound Customers for the Corresponding InBound Terminal " +inBoundTerminals[i];}
                   }
			          }
			        }
			      
			      }
		      }
		      
		       
		       
		       
		       
		       rs.close();			   
		      
		  }catch(Exception ex)
		  {
		   //Logger.error(FILE_NAME,"Exception caught :: validateTermsCusts() " + ex.toString());
       logger.error(FILE_NAME+"Exception caught :: validateTermsCusts() " + ex.toString());
		   throw new javax.ejb.EJBException(ex.toString());
		  }
		  finally
		  {
		      try
		      {
		       if(stmt!=null)
				 {stmt.close();	}		   
			   if(connection!=null)
				{ connection.close();	} 	 
			  }catch(Exception ex)
			  {
			   //Logger.error(FILE_NAME,"Exception caught :: validateTermsCusts() " + ex.toString());
         logger.error(FILE_NAME+"Exception caught :: validateTermsCusts() " + ex.toString());
			  }
		  
		  }
		  
		  return errorMessage;	
	  	}

		 /**
   * 
   * @param arrayList
   * @return 
   */
	public  boolean mapShipOrConsDtls(ArrayList arrayList) 
	{
	       ArrayList 		listOfValues 		=  null; 
	       Connection		connection	 		=	null;
		   PreparedStatement pStmt		 		=  null;
		   String 			sqlQuery	 		=  null;	
	// @@ 'TerminalId' is replaced with 'TerminalId2' for TogetherJ by Suneetha on 12 Jan 05
		   String			terminalId2   		=  null;
		   String 			customerId	 		=  null;
		   String			terminalId1	 		=  null;
		   String			customerId1  		=  null;	
		   String[]			shipperIds	 		=  null;
		   String[]         consigneeIds 		=  null;  	
		   String[] 		outBoundTerminalIds = null;
		   String[]		    inBoundTerminalIds	= null; 
		   try
		   {
		      connection  =  getConnection();
			  sqlQuery    =  " INSERT INTO FS_FR_CUSTOMERS_MAPPING(CUSTOMER_ID1,TERMINAL_ID1,CUSTOMER_ID2,"
			              +  " TERMINAL_ID2,CUSTOMER_TYPE) VALUES(?,?,?,?,?) ";
			  if(arrayList!=null)
			  {
			       customerId   		= (String)arrayList.get(0);
				   terminalId2   		= (String)arrayList.get(1);
				   shipperIds   		= (String[])arrayList.get(2);
				   consigneeIds 		= (String[])arrayList.get(3);
				   outBoundTerminalIds  = (String[])arrayList.get(4);
				   inBoundTerminalIds   = (String[])arrayList.get(5); 
				   
				   pStmt        = connection.prepareStatement(sqlQuery);
				   
				   
				   int tempIndex   = 0;
				   boolean flag = false;
				   
				   if(inBoundTerminalIds!=null && inBoundTerminalIds.length>0)
				   {
					   int inBoundTermLen	=	inBoundTerminalIds.length;
				      for(int i=0;i<inBoundTermLen;i++)
				      {
				        if(shipperIds!=null)
				        {	
				         int shpIdsLen	=	shipperIds.length;
					        for(int k=tempIndex;k<shpIdsLen;k++)
					        {
					           
					           pStmt.setString(1,customerId);
						   	   pStmt.setString(2,terminalId2);

							   if(shipperIds[k].indexOf('&')!=-1)
							   {	 
							   	 pStmt.setString(3,shipperIds[k].substring(0,shipperIds[k].length()-1));
							     flag = true;
							   }
							   else
							    { pStmt.setString(3,shipperIds[k]);		}	   
					        
					             pStmt.setString(4,inBoundTerminalIds[i]); 
					             pStmt.setString(5,"S");
						    
						      if(flag == true)
						      {
						         pStmt.executeUpdate();
								 tempIndex = k+1;
								 flag = false;
								 break;
						      }
							   pStmt.executeUpdate();
						   }
				      }	
				    }
				  }   
				  
				  pStmt.clearParameters();
				  
				  tempIndex = 0;
				  flag = false;
				  if(outBoundTerminalIds!=null && outBoundTerminalIds.length > 0)
				   {
					  int outBoundTermIdsLen	=	outBoundTerminalIds.length;
				      for(int i=0;i<outBoundTermIdsLen;i++)
				      {
					    if(consigneeIds!=null)
					    {
					    	int consIdsLen	=	consigneeIds.length;
					        for(int k=tempIndex;k<consIdsLen;k++)
					        {
					           pStmt.setString(1,customerId);
						   	   pStmt.setString(2,terminalId2);

							   
							   if(consigneeIds[k].indexOf('&')!=-1)
							   {	 
							   	 pStmt.setString(3,consigneeIds[k].substring(0,consigneeIds[k].length()-1));
							     flag = true;
							   }
							   else
							     {pStmt.setString(3,consigneeIds[k]);			}   
					        
					             pStmt.setString(4,outBoundTerminalIds[i]); 
					             pStmt.setString(5,"C");
						    
						      if(flag == true)
						      {
						         pStmt.executeUpdate();
								 tempIndex = k+1;
								 flag = false;
								 break;
						      }
							   pStmt.executeUpdate();
						   }
					  }
				    }
				  }   
				   
				  
			 }	  
				  
			 return true;  
		  }catch(Exception ex)
		  {
		   //Logger.error(FILE_NAME,"Exception :: mapShipOrConsDtls() " + ex.toString());
       logger.error(FILE_NAME+"Exception :: mapShipOrConsDtls() " + ex.toString());
		   throw new javax.ejb.EJBException(ex.toString());
		  }    
	      finally
	      {
	       try
	       {
			if(pStmt!=null)
			{ pStmt.close();}
	       }catch(Exception ex)
	        {
	         //Logger.error(FILE_NAME,"Exception in finally :: mapShipOrConsDtls :: " + ex.toString());
           logger.error(FILE_NAME+"Exception in finally :: mapShipOrConsDtls :: " + ex.toString());
	        }
	       ConnectionUtil.closeConnection(connection);
	      }
	
	}


	/**
   * 
   * @param attachedId
   * @return 
   */
	public java.util.Vector getTerminals(String attachedId)
	{
		Connection		connection		=	null;
		ResultSet		rs				=	null;
		boolean			flag			=	false;
		String			termQry			=	null;
		java.util.Vector			termnlId		=	new java.util.Vector();
		Statement 		stmt			=	null;
		try
		{
			connection	=	getConnection();
			termQry		=	"SELECT ORIGINTERMINAL,DESTTERMINAL FROM FS_FR_HOUSEDOCHDR WHERE HOUSEDOCID='"+attachedId+"'";
			stmt		= connection.createStatement();
			rs	=	stmt.executeQuery(termQry);
			while(rs.next())
			{
				termnlId.addElement(rs.getString(1));
				termnlId.addElement(rs.getString(2));
			}
		}
		catch(Exception ex)
		{
			
			//Logger.error(FILE_NAME,"Exception caught in getTerminals():" + ex.toString());
      logger.error(FILE_NAME+"Exception caught in getTerminals():" + ex.toString());
      throw new javax.ejb.EJBException(ex.toString());
		}
		finally
		{
			try
			{
				if(stmt!=null)
					{stmt.close();}
				if(rs!=null)
					{rs.close();}
				if(connection!=null)
					{connection.close();}
			}
			catch(Exception ex)
			{
				//Logger.error(FILE_NAME,"Exception caught in finally block of checkAttachedId:" + ex.toString());
        logger.error(FILE_NAME+"Exception caught in finally block of checkAttachedId:" + ex.toString());
			}
		}
		return termnlId;
	}

	/**
   * 
   * @param searchString
   * @return 
   */
public ArrayList getCurrencyIds(String searchString)
    {
        Connection connection   = null;
        Statement  stmt         = null;
        ResultSet  resultset    = null;

		ArrayList currencyIds = new ArrayList();
		
		if(searchString!=null && !(searchString.equals(""))){
			searchString = " WHERE COUNTRYID LIKE '"+searchString+"%'";
		}else{
			searchString = "";
		}
		
		String sql = "SELECT COUNTRYNAME,CURRENCYID FROM FS_COUNTRYMASTER    "+searchString+" ORDER BY COUNTRYNAME";
	      try
        {
            connection  = this.getConnection();
            stmt        = connection.createStatement();
            resultset   = stmt.executeQuery(sql);
            while(resultset.next())
            {
                currencyIds.add(resultset.getString(1)+"  ["+resultset.getString(2)+"]");
            }
        }
        catch(SQLException sqEx)
        {
			//Logger.error(FILE_NAME, "[getCurrencyIds(whereclause)] -> "+sqEx.toString());
      logger.error(FILE_NAME+"[getCurrencyIds(whereclause)] -> "+sqEx.toString());
            throw new EJBException(sqEx.toString());
        }
        finally
        {
			ConnectionUtil.closeConnection(connection, stmt, resultset);
        }
        return currencyIds;
    }

   public String getCurrencyId(String countryId)
	{
		Connection connection = null;
		PreparedStatement pStmt = null;
		ResultSet rs = null;
		String currency = null;
		try
		{
			connection = this.getConnection();
			pStmt = connection.prepareStatement("SELECT CURRENCYID FROM FS_COUNTRYMASTER WHERE COUNTRYID = ?");
			pStmt.setString(1, countryId);
			rs = pStmt.executeQuery();
			if(rs.next())
			{
				currency = rs.getString(1);
			}
		}
		catch(Exception ex)
		{
			System.out.println("Exception in getCurrencyId() - SetupSessionBean "+ex);
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
   * 
   * @param param
   * @param whereClause
   * @return 
   */
    public ArrayList getCustomerIds(ESupplyGlobalParameters param, String whereClause)
    {
		Connection	connection	= null;  
		//Statement	stmt		= null;   //Commented By RajKumari on 30-10-2008 for Connection Leakages.
		ResultSet	rs          = null; 
        CallableStatement cstmt =null;
		
		ArrayList customerIds = new ArrayList();

		try
		{
			connection = this.getConnection();
			//stmt = connection.createStatement();
			String terminalId = param.getTerminalId();
			//rs = stmt.executeQuery("SELECT DISTINCT A.CUSTOMERID,COMPANYNAME FROM FS_FR_CUSTOMERMASTER A, FS_ADDRESS B WHERE "+
				//						"A.CUSTOMERADDRESSID=B.ADDRESSID AND "+whereClause+"");
			  cstmt        = connection.prepareCall("{ ?= call QMS_SETUPS.GET_CUSTOMER_2(?)}");
              cstmt.registerOutParameter(1,OracleTypes.CURSOR);
              cstmt.setString(2,whereClause);
              cstmt.execute();
			  rs  =  (ResultSet)cstmt.getObject(1);
			while(rs.next())
			{customerIds.add(rs.getString(1)+" ["+rs.getString(2)+"]");}
		}
		catch(SQLException sqEx)
        {sqEx.printStackTrace();
			//Logger.error(FILE_NAME, "[getCustomerIds(ESupplyGlobalParameters, whereClause)] -> "+sqEx.toString());
      logger.error(FILE_NAME+"[getCustomerIds(ESupplyGlobalParameters, whereClause)] -> "+sqEx.toString());
            throw new EJBException(sqEx.toString());
        }
        catch(Exception sqEx)
        {sqEx.printStackTrace();
			//Logger.error(FILE_NAME, "[getCustomerIds(ESupplyGlobalParameters, whereClause)] -> "+sqEx.toString());
      logger.error(FILE_NAME+"[getCustomerIds(ESupplyGlobalParameters, whereClause)] -> "+sqEx.toString());
            throw new EJBException(sqEx.toString());
        }
        finally
        {
			ConnectionUtil.closeConnection(connection, cstmt, rs);
        }
		return customerIds;
	} 
/**
   * This method is written insted of getCustomerIds(ESupplyGlobalParameters param, String whereClause)
   * in order to consider 2 (Two) more Parameters and avoid Queries in JSP'S
   * @param searchstring
   * @param terminalId
   * @param customerType
   * @param registered
   * @return 
   */
		public ArrayList getCustomerIds(String searchstring,String terminalId,String customerType,String registered,String operation)
    {
		Connection	connection	= null;  
		//Statement	stmt		= null;   //Commented By RajKumari on 30-10-2008 for Connection Leakages.
		ResultSet	rs          = null; 
		CallableStatement cstmt = null;
        String flag = "";
	/*	if(searchstring!=null && !(searchstring.equals(""))){
			searchstring=" AND CUSTOMERID LIKE '"+searchstring+"%'";
		}else{
			searchstring="";
		}*/
		if(searchstring==null)
          searchstring="";//added by rk

		String	reg =""	;
		if(	registered !=null && !registered.equals("All"))
		{reg = " AND REGISTERED = '"+registered+"'  "	 ;}

		if(terminalId!=null && !(terminalId.equals(""))){
			//terminalId=" AND CUSTOMERTYPE = '"+customerType+"' "+reg+" AND TERMINALID='"+terminalId+"'";
		flag = "TRUE";
		}else{
			terminalId="";
		flag = "FALSE";
		}
		
		//String	sqlQry	=	"SELECT DISTINCT A.CUSTOMERID,COMPANYNAME FROM FS_FR_CUSTOMERMASTER A, FS_ADDRESS B WHERE "+
										//"A.CUSTOMERADDRESSID=B.ADDRESSID "+searchstring+" "+terminalId+"";

		ArrayList customerIds = new ArrayList();

		try
		{
			connection = this.getConnection();
			//stmt = connection.createStatement();
	//		String terminalId = param.getTerminalId();
			//rs = stmt.executeQuery(sqlQry);
			cstmt        = connection.prepareCall("{ ?= call QMS_SETUPS.GET_CUSTOMER_1(?,?,?,?,?)}");
        cstmt.registerOutParameter(1,OracleTypes.CURSOR);
        cstmt.setString(2,terminalId);
        cstmt.setString(3,customerType);
        cstmt.setString(4,operation);
        cstmt.setString(5,(searchstring+"%"));              
        cstmt.setString(6,flag);    
        cstmt.execute();
        rs  =  (ResultSet)cstmt.getObject(1);
			while(rs.next())
			{customerIds.add(rs.getString(1)+" ["+rs.getString(2)+"]");}
		}
		catch(SQLException sqEx)
        {sqEx.printStackTrace();
			//Logger.error(FILE_NAME, "[getCustomerIds(String searchstring,String terminalId,String customerType,String registered)] -> "+sqEx.toString());
      logger.error(FILE_NAME+ "[getCustomerIds(String searchstring,String terminalId,String customerType,String registered)] -> "+sqEx.toString());
            throw new EJBException(sqEx.toString());
        }
        catch(Exception sqEx)
        {sqEx.printStackTrace();
			//Logger.error(FILE_NAME, "[getCustomerIds(String searchstring,String terminalId,String customerType,String registered)] -> "+sqEx.toString());
      logger.error(FILE_NAME+ "[getCustomerIds(String searchstring,String terminalId,String customerType,String registered)] -> "+sqEx.toString());
            throw new EJBException(sqEx.toString());
        }
        finally
        {
			ConnectionUtil.closeConnection(connection, cstmt, rs);
        }
		return customerIds;
	}
//New Method added by JS as on 20/09/02 for removing Querys from JSPs
//Modifyed as on 10/10/02
  /**
   * 
   * @param searchstring
   * @param terminalId
   * @param customerType
   * @param registered
   * @param registrationLevel
   * @param operation
   * @return 
   */
 public ArrayList getCustomerIds(String searchstring,String terminalId,String customerType,String registered,String registrationLevel,String operation)
     {
		Connection	connection	= null;  
		//Statement	stmt		= null;   //Commented By RajKumari on 30-10-2008 for Connection Leakages.
		ResultSet	rs          = null; 
		String		whereClause	= null;
		CallableStatement cstmt = null;
		if(searchstring==null)
      searchstring="";
/*	if(searchstring!=null && !(searchstring.equals(""))){

			searchstring=" AND CUSTOMERID LIKE '"+searchstring+"%'";
		}else{
			searchstring="";
		}
		if(registrationLevel.equalsIgnoreCase("C")){
			if(!operation.equalsIgnoreCase("View")){
				whereClause=" AND CUSTOMERTYPE='Corporate' AND TERMINALID='"+terminalId+"'";
			}else{
				whereClause=" AND CUSTOMERTYPE='Corporate'";
			}
		}else if(registrationLevel.equalsIgnoreCase("T")){
			if(registered.equalsIgnoreCase("R")){
				whereClause=" AND CUSTOMERTYPE='Customer' AND TERMINALID='"+terminalId+"' AND REGISTERED='"+registered+"' ";	  
			}if(registered.equalsIgnoreCase("U")){
				whereClause=" AND CUSTOMERTYPE='Customer' AND TERMINALID='"+terminalId+"' AND REGISTERED='"+registered+"' "; 
			}
		}*/

	  //String	sqlQry	=	"SELECT DISTINCT A.CUSTOMERID,COMPANYNAME FROM FS_FR_CUSTOMERMASTER A, FS_ADDRESS B WHERE "+
									//	"A.CUSTOMERADDRESSID=B.ADDRESSID  "+whereClause+
										 // @@ Anup added  on 20050105 
									//	" AND A.REGISTERED_TERMINALID IS NULL "+
										//@@ 20050105 
		                              //    searchstring;

		ArrayList customerIds = new ArrayList();

		try
		{
			connection = this.getConnection();
	//		stmt = connection.createStatement();
	//		String terminalId = param.getTerminalId();
		//	rs = stmt.executeQuery(sqlQry);
    
     // System.out.println("terminalId+"+terminalId);
      //System.out.println("operation"+operation);
      //System.out.println("searchstring"+searchstring);
     // System.out.println("registered"+registered);
      //System.out.println("registrationLevel"+registrationLevel);
			  cstmt        = connection.prepareCall("{ ?= call QMS_SETUPS.GET_CUSTOMER(?,?,?,?,?)}");
              cstmt.registerOutParameter(1,OracleTypes.CURSOR);
              cstmt.setString(2,terminalId);
              cstmt.setString(3,operation);
              cstmt.setString(4,(searchstring+"%"));          
              cstmt.setString(5,registered);
              cstmt.setString(6,registrationLevel);
              cstmt.execute();
              rs  =  (ResultSet)cstmt.getObject(1);
			while(rs.next())
			{customerIds.add(rs.getString(1)+" ["+rs.getString(2)+"]");}
		}
		catch(SQLException sqEx)
        {sqEx.printStackTrace();
			//Logger.error(FILE_NAME, "[getCustomerIds(String searchstring,String terminalId,String customerType,String registered,String registrationLevel,String operation)] -> "+sqEx.toString());
      logger.error(FILE_NAME+ "[getCustomerIds(String searchstring,String terminalId,String customerType,String registered,String registrationLevel,String operation)] -> "+sqEx.toString());
            throw new EJBException(sqEx.toString());
        }
        catch(Exception sqEx)
        {sqEx.printStackTrace();
			//Logger.error(FILE_NAME, "[getCustomerIds(String searchstring,String terminalId,String customerType,String registered,String registrationLevel,String operation)] -> "+sqEx.toString());
      logger.error(FILE_NAME+ "[getCustomerIds(String searchstring,String terminalId,String customerType,String registered,String registrationLevel,String operation)] -> "+sqEx.toString());
            throw new EJBException(sqEx.toString());
        }
        finally
        {
			ConnectionUtil.closeConnection(connection, cstmt, rs);
        }
		return customerIds;
	}

	/**
   * 
   * @param searchString
   */
   public ArrayList getCountryIds(String searchString,String terminalId,String operation)
    {
        Connection	connection	= null;
       // Statement   stmt		= null;//Commented By RajKumari on 30-10-2008 for Connection Leakages.
        ResultSet   rs			= null;
		CallableStatement cstmt =null;

		ArrayList countryIds = new ArrayList();
	/*	if(searchString!=null && !(searchString.equals(""))){
			searchString = " WHERE COUNTRYID LIKE '"+searchString+"%'";
		}else{
			searchString = "";
		}*/
	
	
	//	String sql = "SELECT COUNTRYID,COUNTRYNAME FROM FS_COUNTRYMASTER "+searchString+" ORDER BY    COUNTRYNAME";
        try
        {
			connection = this.getConnection();
            //stmt = connection.createStatement();
            //rs = stmt.executeQuery(sql);

            cstmt        = connection.prepareCall("{ ?= call QMS_SETUPS.GET_COUNTRY(?,?,?)}");
            cstmt.registerOutParameter(1,OracleTypes.CURSOR);
            cstmt.setString(2,terminalId);            
            cstmt.setString(3,operation);
            cstmt.setString(4,(searchString+"%"));
            cstmt.execute();
            rs  =  (ResultSet)cstmt.getObject(1);
            while(rs.next())
			{countryIds.add(rs.getString(2)+" ["+rs.getString(1)+"]");}
        }
        catch(SQLException sqEx)
        {sqEx.printStackTrace();
			//Logger.error(FILE_NAME, "[getCountryIds(whereclause)] -> "+sqEx.toString());
      logger.error(FILE_NAME+ "[getCountryIds(whereclause)] -> "+sqEx.toString());
            throw new EJBException(sqEx.toString());
        }
		catch(Exception e)
        {
          e.printStackTrace();
          throw new EJBException(e.toString());
        }
        finally
        {
			ConnectionUtil.closeConnection(connection, cstmt, rs);
        }
        return countryIds;
    }
    
    //@@Added by Yuvraj for CR_DHLQMS_1006
  /**
   * 
   * @param searchString
   * @param locationId
   */
   public ArrayList getCountryIds(String searchString,String locationId)
    {
        Connection	connection	= null;
       // Statement   stmt		= null;//Commented By RajKumari on 30-10-2008 for Connection Leakages.
        ResultSet   rs			= null;
		CallableStatement cstmt     = null;
		ArrayList countryIds = new ArrayList();
	/*	if(searchString!=null && !(searchString.equals(""))){
			searchString = " AND CON.COUNTRYID LIKE '"+searchString+"%'";
		}else{
			searchString = "";
		}*/
	if(searchString == null)
    searchString = "";
	
	//	String sql = "SELECT CON.COUNTRYID,COUNTRYNAME FROM FS_COUNTRYMASTER CON,FS_FR_LOCATIONMASTER LOC "+
        //          " WHERE LOC.COUNTRYID=CON.COUNTRYID AND LOC.LOCATIONID LIKE '"+locationId+"%'"+searchString+
         //        " ORDER BY COUNTRYNAME";

        try
        {
            connection = this.getConnection();
          //  stmt = connection.createStatement();
           // rs = stmt.executeQuery(sql);
			cstmt        = connection.prepareCall("{ ?= call QMS_SETUPS.GET_COUNTRY(?,?)}");
              cstmt.registerOutParameter(1,OracleTypes.CURSOR);
              cstmt.setString(2,searchString+"%");
              cstmt.setString(3,(locationId+"%"));
              cstmt.execute();
              rs  =  (ResultSet)cstmt.getObject(1);
            while(rs.next())
			{countryIds.add(rs.getString(2)+" ["+rs.getString(1)+"]");}
        }
        catch(SQLException sqEx)
        {sqEx.printStackTrace();
			//Logger.error(FILE_NAME, "[getCountryIds(whereclause)] -> "+sqEx.toString());
      logger.error(FILE_NAME+ "[getCountryIds(whereclause)] -> "+sqEx.toString());
            throw new EJBException(sqEx.toString());
        }
        catch(Exception sqEx)
        {sqEx.printStackTrace();
			//Logger.error(FILE_NAME, "[getCountryIds(whereclause)] -> "+sqEx.toString());
      logger.error(FILE_NAME+ "[getCountryIds(whereclause)] -> "+sqEx.toString());
            throw new EJBException(sqEx.toString());
        }
        finally
        {
			ConnectionUtil.closeConnection(connection, cstmt, rs);
        }
        return countryIds;
    }
//@@Yuvraj


	/**
   * 
   * @param terminalId
   * @param companyId
   * @param shipmentMode
   * @param terminalType
   * @param searchString
   * @param originTerminal
   * @param destinationTerminal
   * @return 
   */
	public ArrayList getTerminalIdsforThirdStation(String terminalId,String companyId,String shipmentMode,String terminalType,String searchString,String originTerminal,String destinationTerminal)   
    {
		Connection	connection	= null;
		Statement	stmt		= null;
        ResultSet	rs			= null;
		ArrayList terminalIds = new ArrayList();
		String sqlTerminalType	= "";

		if(shipmentMode.equals("1")){
			shipmentMode = " WHERE SHIPMENTMODE IN (1,3,5,7) ";
		}else if(shipmentMode.equals("4")){
			{shipmentMode= " WHERE SHIPMENTMODE IN (4,5,6,7) ";}


		if(terminalType!=null){
			sqlTerminalType = " AND OPER_ADMIN_FLAG='"+terminalType+"' ";
		}
		}else if(shipmentMode.equals("2")){
			shipmentMode= " AND SHIPMENTMODE IN (2,3,6,7) ";
		}else{
			shipmentMode= "";
		}
		if(companyId!=null && !(companyId.equals(""))){
			companyId=" AND TERMINALID LIKE '"+companyId+"%' ";
		}else{
			companyId="";
		}
		if(terminalId!=null && !(terminalId.equals(""))){
			terminalId=" AND TERMINALID LIKE '"+terminalId+"%' ";
		}else{
			terminalId="";
		}
		if(((searchString!=null) && !(searchString.equals("")))){
			searchString	=	" AND TERMINALID LIKE '"+searchString+"%' ";
		}else{
			searchString	=	"";
		}
		
		
		String		sqlQry		=	"SELECT TERMINALID"+
									" FROM FS_FR_TERMINALMASTER"+
									shipmentMode+
									sqlTerminalType+
									companyId+
									terminalId+
									searchString+
									"  AND AGENTIND='c' AND TERMINALTYPE='S'"+
									" AND TERMINALID NOT IN ('"+originTerminal+"','"+destinationTerminal+"') ORDER BY TERMINALID";	
        
		try
        {
			connection = this.getConnection();
            stmt = connection.createStatement();
			rs =  stmt.executeQuery(sqlQry);
            while(rs.next())
			{terminalIds.add(rs.getString(1));}
        }
        catch(SQLException sqEx)
        {
			//Logger.error(FILE_NAME, "[getTerminalIds(whereclause)] -> "+sqEx.toString());
      logger.error(FILE_NAME+ "[getTerminalIds(whereclause)] -> "+sqEx.toString());
            throw new EJBException(sqEx.toString());
        }
        finally
        {
			ConnectionUtil.closeConnection(connection, stmt, rs);
        }
        return terminalIds;
    }

	//@@ Srivegi Added on 20050316 (IATA-PR)
	 public ArrayList getLocations(String searchString,String lovType,String module,String origin,String dest,String serviceLevel)
    {
        Connection	connection	= null;
        Statement	stmt		= null;
        ResultSet	rs          = null;
		String location= "";

        ArrayList   locationIds  = new ArrayList();

		String sql = "";
		try{
           if(module.equalsIgnoreCase("IATAAdd"))
         	{// Srivegi Modified on 20050413 (Order by LOCATIONID is added)
				sql ="SELECT LOCATIONID FROM FS_FR_LOCATIONMASTER WHERE LOCATIONID LIKE '"+searchString+"%' AND  SHIPMENTMODE=7 ORDER BY LOCATIONID ";
    	        connection = this.getConnection();
	    		stmt = connection.createStatement();
		        rs = stmt.executeQuery(sql);
                while(rs.next())
                locationIds.add(rs.getString(1));
			}
			else
			{

				StringBuffer locationSql= new StringBuffer("SELECT DISTINCT ");
		    if (lovType.equalsIgnoreCase("originGatewayId"))
			    {
					location="ORIGINLOCATION";
			    }
				else if (lovType.equalsIgnoreCase("destinationGatewayId"))
				{
					location="DESTLOCATION";
				}
				else if (lovType.equalsIgnoreCase("serviceLevelId"))
				{
                    location="SERVICELEVEL";
				}
        				
                locationSql.append(location);
            	locationSql.append(" FROM FS_FR_IATARATEMASTERHDR WHERE  ");
				
					locationSql.append(" ORIGINLOCATION LIKE '");
					locationSql.append(origin);
					locationSql.append("%' ");

					locationSql.append(" AND DESTLOCATION  LIKE '");
					locationSql.append(dest);
					locationSql.append("%' ");
				
					locationSql.append(" AND SERVICELEVEL  LIKE '");
					locationSql.append(serviceLevel);
					locationSql.append("%'");
				
    	        connection = this.getConnection();
				stmt = connection.createStatement();
		        rs = stmt.executeQuery(locationSql.toString());
                while(rs.next())
                locationIds.add(rs.getString(1));
			}
		}
		catch(SQLException sqEx)
        {
			//Logger.error(FILE_NAME, "[getLocations(whereClause)] -> "+sqEx.toString());
      logger.error(FILE_NAME+ "[getLocations(whereClause)] -> "+sqEx.toString());
            throw new EJBException(sqEx.toString());
        }
        finally
        {
			ConnectionUtil.closeConnection(connection, stmt, rs);
        }
        return locationIds;
	}

	 /**
   * 
   * @param whereclause
   * @return 
   */
    public ArrayList getTerminalIds(String whereclause)
    {
		Connection	connection	= null;
		Statement	stmt		= null;
        ResultSet	rs			= null;

        ArrayList terminalIds = new ArrayList();

        try
        {
			connection = this.getConnection();
            stmt = connection.createStatement();
            rs =  stmt.executeQuery("SELECT TERMINALID FROM FS_FR_TERMINALMASTER "+whereclause+" ORDER BY TERMINALID");
            while(rs.next())
			{terminalIds.add(rs.getString(1));}
        }
        catch(SQLException sqEx)
        {
			//Logger.error(FILE_NAME, "[getTerminalIds(whereclause)] -> "+sqEx.toString());
      logger.error(FILE_NAME+ "[getTerminalIds(whereclause)] -> "+sqEx.toString());
            throw new EJBException(sqEx.toString());
        }
        finally
        {
			ConnectionUtil.closeConnection(connection, stmt, rs);
        }
        return terminalIds;
    }

	/**
   * This Method is written insted of 	getTerminalIds(String whereClause)
   * in Order to consider Four more Parameters and avoid Queryies in JSP'S
   * @param terminalId
   * @param companyId
   * @param shipmentMode
   * @param terminalType
   * @param searchString
   * @return 
   */


public ArrayList getTerminalIds(String terminalId,String companyId,String shipmentMode,String terminalType,String searchString,String accessType)   
    {
		Connection	connection	= null;
		//Statement	stmt		= null;//Commented By RajKumari on 30-10-2008 for Connection Leakages.
        ResultSet	rs			= null;
        PreparedStatement pst = null;
		ArrayList terminalIds = new ArrayList();
		CallableStatement cstmt = null;
   String  terminalIdQuery   =null;
		if(terminalType!=null){
			terminalType=terminalType;
		}else{
			terminalType="O";
		}
	/*	if(shipmentMode.equals("1")){
			shipmentMode = " AND SHIPMENTMODE IN (1,3,5,7)";
		}else if(shipmentMode.equals("4")){
			shipmentMode= " AND SHIPMENTMODE IN (4,5,6,7)";
		}else if(shipmentMode.equals("2")){
			shipmentMode= " AND SHIPMENTMODE IN (2,3,6,7)";
		}else{
			shipmentMode= "";
		}*/
	/*	if(companyId!=null && !(companyId.equals(""))){
			companyId=" AND TERMINALID LIKE '"+companyId+"%' ";
		}else{
			companyId="";
		}
		if(terminalId!=null && !(terminalId.equals(""))){
			terminalId=" AND TERMINALID LIKE '"+terminalId+"%' ";
		}else{
			terminalId="";
		}*/
		if(searchString==null)
          searchString = "";

    /*  if(((searchString!=null) && !(searchString.equals("")))){
			searchString	=	" AND TERMINALID LIKE '"+searchString+"%' ";
		}else{
			searchString	=	"";
		}*/
		//logger.info("terminalType  "+terminalType);
    //logger.info("terminalId  "+terminalId);
		//String		sqlQry		= "SELECT TERMINALID FROM FS_FR_TERMINALMASTER WHERE OPER_ADMIN_FLAG='"+terminalType+"' "+shipmentMode+"  "+companyId+" "+terminalId+" "+searchString+"  ORDER BY TERMINALID";
        try
        {
        connection = this.getConnection();
        // if condition is added by VLAKSHMI for  issue 173655 on 20090629
        //if(terminalId.equals("DHLAUAD") && terminalType.equals("O") && ("".equals(companyId)))
        if(accessType.equalsIgnoreCase("ADMN_TERMINAL") && terminalType.equals("O") && ("".equals(companyId)))
        {
        if(searchString!=null && !"".equalsIgnoreCase(searchString) && searchString.length()>0)
        {
           terminalIdQuery="select child_terminal_id term_id from fs_fr_terminal_regn "+ 
           "  where parent_terminal_id='"+terminalId+"'and CHILD_TERMINAL_ID LIKE'"+searchString+"%'";
        }else
        {
          terminalIdQuery="select child_terminal_id term_id from fs_fr_terminal_regn "+ 
           "  where parent_terminal_id='"+terminalId+"'";
        }
        pst               =  connection.prepareStatement(terminalIdQuery);
        rs                =  pst.executeQuery();
        while(rs.next())
        {
          terminalIds.add(rs.getString(1));
        }
        }
        else if(accessType.equalsIgnoreCase("OPER_TERMINAL") && terminalType.equals("O"))
        {
        	if(searchString!=null && !"".equalsIgnoreCase(searchString) && searchString.length()>0)
        	{
        		terminalIdQuery= "SELECT TERMINALID FROM FS_FR_TERMINALMASTER WHERE TERMINALID='"+terminalId+"' AND TERMINALID LIKE '"+searchString+"%'";
        	}
        	else
        	{
        		terminalIdQuery= "SELECT TERMINALID FROM FS_FR_TERMINALMASTER WHERE TERMINALID='"+terminalId+"'";
        	}
            pst               =  connection.prepareStatement(terminalIdQuery);
            rs                =  pst.executeQuery();
            while(rs.next())
            {
              terminalIds.add(rs.getString(1));
            }
      
        }
        else{
        	
			  cstmt        = connection.prepareCall("{ ?= call QMS_SETUPS.GET_TERMINAL_1(?,?,?,?,?)}");
              cstmt.registerOutParameter(1,OracleTypes.CURSOR);
              cstmt.setString(2,terminalId);
              cstmt.setString(3,companyId);
              cstmt.setString(4,shipmentMode);          
              cstmt.setString(5,(searchString+"%"));
              cstmt.setString(6,terminalType);
             
              cstmt.execute();
              rs  =  (ResultSet)cstmt.getObject(1);
            while(rs.next())
			  {
         terminalIds.add(rs.getString(1));}
        }
        }
        catch(SQLException sqEx)
        {sqEx.printStackTrace();
			//Logger.error(FILE_NAME, "[getTerminalIds(whereclause)] -> "+sqEx.toString());
      logger.error(FILE_NAME+ "[getTerminalIds(whereclause)] -> "+sqEx.toString());
            throw new EJBException(sqEx.toString());
        }
        catch(Exception sqEx)
        {sqEx.printStackTrace();
			//Logger.error(FILE_NAME, "[getTerminalIds(whereclause)] -> "+sqEx.toString());
      logger.error(FILE_NAME+"[getTerminalIds(whereclause)] -> "+sqEx.toString());
            throw new EJBException(sqEx.toString());
        }
        finally
        {
			ConnectionUtil.closeConnection(connection, cstmt, rs);
			ConnectionUtil.closePreparedStatement(pst);//Added by govind on 15-02-2010 fro ConnectionLeakage.
        }
        return terminalIds;
    }

	//start the getViewCustomerRegistrationReports method
/**
	*The below method is used to view the Customer registration Reports
    *This method takes noOfcols,terminal,terminalType as arguments and returns a Double Dimensional Array
	*@param String str,int noOfcols,String terminal,String terminalType
	*@return String[][] terminals
	*/
	/*public String[][] getViewCustomerRegistrationReports(String str,int noOfcols,String terminal,String terminalType) 
	{
		Connection connection = null;
		Statement stmt        = null;
		ResultSet rs          = null;
		String terminals[][]  = null;
		ResultSet rs1         = null;
		ResultSet rs2         = null;
		String names[]   	  = null;
		String whereClause	  = "";
		int noOfrows = 0;
		String  comp=null;
		try
		{
			connection =this.getConnection();
			stmt       =connection.createStatement();
			str = str.substring(0,str.length()-1);
		   

			String sql="SELECT COMPANYID FROM FS_FR_TERMINALMASTER WHERE TERMINALID='"+terminal+"'";
			rs=stmt.executeQuery(sql);
			if(rs.next())
			{comp=rs.getString(1);}
			
	//add by NageswaraRao.D	
			if(terminalType.equals("CustomerTerminal"))
			{whereClause= " AND C.CUSTOMERTYPE='Customer' AND C.TERMINALID ='"+terminal+"' ";}
			else 
			{whereClause=" AND C.CUSTOMERTYPE='Corporate' AND C.TERMINALID LIKE '"+comp+"%' ";	}
			
	//end here		 
			sql= "SELECT COUNT(*) NO_ROWS FROM FS_FR_CUSTOMERMASTER C , FS_ADDRESS A WHERE C.CUSTOMERADDRESSID = A.ADDRESSID "+whereClause+"  ORDER BY C.CUSTOMERID";
			rs1=stmt.executeQuery(sql);
			if(rs1.next())
			{noOfrows=rs1.getInt("NO_ROWS");}
				sql= "SELECT "+ str +" FROM FS_FR_CUSTOMERMASTER C, FS_ADDRESS A WHERE C.CUSTOMERADDRESSID= A.ADDRESSID "+whereClause+"   ORDER BY C.TERMINALID";
			terminals    = new String[noOfrows][noOfcols];
			rs2    =stmt.executeQuery(sql);
			for(int i=0;rs2.next();i++)
			{
				for(int j=0;j<noOfcols;j++)
				{
					terminals[i][j] = rs2.getString(j+1);
				}
			}
		}
			catch(SQLException sqEx)
			{
        sqEx.printStackTrace();
				Logger.error(FILE_NAME, " -> "+sqEx.toString());
        
				throw new EJBException(sqEx.toString());
			}
			finally
			{
				ConnectionUtil.closeConnection(connection, stmt, rs);
        ConnectionUtil.closeConnection(null, null, rs1);
        ConnectionUtil.closeConnection(null, null, rs2);
			}
		return terminals;
	}//end of getViewCustomerRegistrationReports Method*/

	public ArrayList getViewCustomerRegistrationReports(String str,int noOfcols,String terminal,String terminalType,int noOfRecs,int pageNo,String sortBy,String sortOrder ) //shyam
	{
		Connection connection = null;
		Statement stmt        = null;
		ResultSet rs          = null;
		String terminals[][]  = null;
		ResultSet rs1         = null;
		ResultSet rs2         = null;
		String names[]   	  = null;
		String whereClause	  = "";
		String orderClause	  = "";//Shyam
		String row_Query	  = "";//Shyam
		int		noPages		  = 0 ;//Shyam
		int		no_of_recs		  = 0 ;//Shyam
		int noOfrows = 0;

		ArrayList	mainList	= new ArrayList();//shyam

		String  comp=null;
     String accessType     = "";   //@@ Added by subrahmanyam for the CR_Enhancement_167669 on 26/May/09
    Statement stmt1        = null;//@@ Added by subrahmanyam for the CR_Enhancement_167669 on 26/May/09

		try
		{
			connection =this.getConnection();
			stmt       =connection.createStatement();
			str = str.substring(0,str.length()-1);
	//@@ Added by subrahmanyam for the Cr_Enhancement_167669 on 26/May/09
       stmt1       =connection.createStatement();
      String accessTypeQry     = "SELECT OPER_ADMIN_FLAG FROM FS_FR_TERMINALMASTER  WHERE TERMINALID='"+terminal+"'";
      rs=stmt1.executeQuery(accessTypeQry);
			if(rs.next())
			{accessType=rs.getString(1);}
      if(stmt1!=null)
        stmt1.close();
      if(rs!=null)
        rs.close();
//@@ Ended by subrahmanyam for the Cr_Enhancement_167669 on 26/May/09 

			String sql="SELECT COMPANYID FROM FS_FR_TERMINALMASTER WHERE TERMINALID='"+terminal+"'";
			rs=stmt.executeQuery(sql);
			if(rs.next())
			{comp=rs.getString(1);}
			
	//add by NageswaraRao.D	
			if(terminalType.equals("CustomerTerminal"))
			{
            //@@Commented by subrahmanyam for the Enhancement_Cr_167669 on 26/May/09
        //whereClause= " AND C.CUSTOMERTYPE='Customer' AND C.TERMINALID ='"+terminal+"' ";
        //@@Added by subrahmanyam for the Enhancement_Cr_167669 on 26/May/09
        if("A".equalsIgnoreCase(accessType))
        {
          whereClause= " AND C.CUSTOMERTYPE='Customer' AND C.TERMINALID IN(SELECT CHILD_TERMINAL_ID FROM FS_FR_TERMINAL_REGN WHERE PARENT_TERMINAL_ID='"+terminal+"') ";
        }
        else if("O".equalsIgnoreCase(accessType))
        {
          whereClause= " AND C.CUSTOMERTYPE='Customer' AND C.TERMINALID IN(SELECT CHILD_TERMINAL_ID FROM FS_FR_TERMINAL_REGN WHERE PARENT_TERMINAL_ID=(SELECT PARENT_TERMINAL_ID FROM FS_FR_TERMINAL_REGN WHERE CHILD_TERMINAL_ID='"+terminal+"')) "; 
        }
        else
          whereClause= " AND C.CUSTOMERTYPE='Customer' AND C.TERMINALID ='"+terminal+"' ";
        //@@Ended by subrahmanyam for the Enhancement_Cr_167669 on 26/May/09

      }
			else 
			{whereClause=" AND C.CUSTOMERTYPE='Corporate' AND C.TERMINALID LIKE '"+comp+"%' ";	}
			
	//end here	
	
			row_Query = ")T1  WHERE   ROWNUM <= (("+pageNo+"- 1) * "+noOfRecs+") + "+noOfRecs+") WHERE   RN > (("+pageNo+"- 1) * "+noOfRecs+")";//shyam

			if(sortBy.equalsIgnoreCase("CUSTOMERID"))
				orderClause ="ORDER BY C.CUSTOMERID "+sortOrder;//shyam
			else if(sortBy.equalsIgnoreCase("COMPANYNAME"))
				orderClause ="ORDER BY C.COMPANYNAME "+sortOrder;//shyam
			else if(sortBy.equalsIgnoreCase("CONTACTNAME"))
				orderClause ="ORDER BY C.CONTACTNAME "+sortOrder;//shyam
			else if(sortBy.equalsIgnoreCase("TERMINALID"))
				orderClause ="ORDER BY C.TERMINALID "+sortOrder;//shyam
			else if(sortBy.equalsIgnoreCase("CITY"))
				orderClause ="ORDER BY A.CITY "+sortOrder;//shyam
			
			sql= "SELECT COUNT(*) NO_ROWS FROM FS_FR_CUSTOMERMASTER C , FS_ADDRESS A WHERE C.CUSTOMERADDRESSID = A.ADDRESSID "+whereClause+"  ORDER BY C.CUSTOMERID";
			//sql= "SELECT COUNT(*) NO_ROWS FROM (SELECT T1.*, ROWNUM RN FROM (SELECT CUSTOMERID FROM FS_FR_CUSTOMERMASTER C , FS_ADDRESS A WHERE C.CUSTOMERADDRESSID = A.ADDRESSID "+whereClause+ orderClause + row_Query;//shyam
			rs1=stmt.executeQuery(sql);
			if(rs1.next())
			{
				noOfrows=rs1.getInt("NO_ROWS");
			}
			noPages = noOfrows/noOfRecs; //shyam
			
			int extraPages=noOfrows%noOfRecs;

			if(extraPages>0)//shyam
				noPages++;

			if(noPages==0) //shyam
				noPages = 1;

			if(noPages==pageNo)
				no_of_recs = noOfrows%noOfRecs;
			else
				no_of_recs = noOfRecs;

			//int extraRecs = noOfrows%noOfRecs;

			//if(extraRecs==0)
				//no_of_recs=noOfRecs;
			//else
				//no_of_recs=extraRecs;
			
			//System.out.println("no_of_recs---"+no_of_recs);
			//System.out.println("noOfRecs---"+noOfRecs);
			//System.out.println("no_of_recs---"+extraRecs);
			
				//sql= "SELECT "+ str +" FROM FS_FR_CUSTOMERMASTER C, FS_ADDRESS A WHERE C.CUSTOMERADDRESSID= A.ADDRESSID "+whereClause+"   ORDER BY C.TERMINALID";
			

			sql= "SELECT * FROM (SELECT T1.*, ROWNUM RN FROM (SELECT "+ str +" FROM FS_FR_CUSTOMERMASTER C, FS_ADDRESS A WHERE C.CUSTOMERADDRESSID= A.ADDRESSID "+whereClause + orderClause + row_Query;//shyam

			terminals    = new String[no_of_recs][noOfcols];
			rs2    =stmt.executeQuery(sql);
			
			for(int i=0;rs2.next();i++)
			{
				for(int j=0;j<noOfcols;j++)
				{
					terminals[i][j] = rs2.getString(j+1);
				}
			}
			mainList.add(new Integer(no_of_recs));//shyam
			mainList.add(new Integer(noPages));//shyam
			mainList.add(terminals);//shyam
		}
			
      catch(SQLException sqEx)
			{
        sqEx.printStackTrace();
				//Logger.error(FILE_NAME, " -> "+sqEx.toString());
        logger.error(FILE_NAME+ " -> "+sqEx.toString());
        
				throw new EJBException(sqEx.toString());
			}
      catch(Exception e)
      {
        //Logger.error(FILE_NAME,"Exception in getViewCustomerRegistrationReports "+e);
        logger.error(FILE_NAME+"Exception in getViewCustomerRegistrationReports "+e);
        e.printStackTrace();
        throw new EJBException(e);
      }
			finally
			{
				ConnectionUtil.closeConnection(connection, stmt, rs);
        ConnectionUtil.closeConnection(null, stmt1, rs1);//ADDED stmt1 as second parameter for connection leakage by govind on 15-02-2010
        ConnectionUtil.closeConnection(null, null, rs2);
			}
		//return terminals;
		return mainList;
	}//end of getViewCustomerRegistrationReports Method


	
	//added by durga on 6th april 2001 from raghu.reddy system (not from raghu.reddy ) starts here
/**  *  This method is used to view the Carrier Registration
       *  @ param String Str,int numberOfColumns
       *  @ return  String[][] terminals
 */
public String[][] getViewCarrierRegistration(String str,int noOfcols)
{
	Connection connection=null;
	Statement stmt        = null;
	ResultSet rs          = null;
	String terminals[][]  = null;
	ResultSet rs1         = null;
	int noOfrows = 0;
	try
	{
		connection =this.getConnection();
		stmt       =connection.createStatement();
		str = str.substring(0,str.length()-1);

		String sql= "SELECT "+ str +" FROM  FS_FR_CAMASTER A , FS_ADDRESS B WHERE A.ADDRESSID = B.ADDRESSID order by A.carrierid";

		String sql1= "select count(*) NO_ROWS FROM    FS_FR_CAMASTER A , FS_ADDRESS B WHERE  A.ADDRESSID = B.ADDRESSID order by A.carrierid";
		rs         =stmt.executeQuery(sql1);
		rs.next();
		noOfrows     = rs.getInt("NO_ROWS");
		terminals    = new String[noOfrows][noOfcols];
		rs1    =stmt.executeQuery(sql);
		for(int i=0;rs1.next();i++)
		{
			for(int j=0;j<noOfcols;j++)
			{
				terminals[i][j] = rs1.getString(j+1);
			}
		}
	}
	catch(Exception e)
	{
		
		//Logger.error(FILE_NAME, "Exception in getViewCarrierRegistration() at EtransReportSessionBean"+e.toString());
    logger.error(FILE_NAME+ "Exception in getViewCarrierRegistration() at EtransReportSessionBean"+e.toString());
    throw new javax.ejb.EJBException(e.toString());
	}
	finally
	{
		try
		{
			if(rs!=null)
				{rs.close();}
      //Added By RajKumari on 30-10-2008 for Connection Leakages.
      if(rs1!=null)
      {rs1.close();}
			if(stmt!=null)
				{stmt.close();}
			if(connection!=null)
				{connection.close();}
		}
		catch(SQLException e)
		{
			//Logger.error(FILE_NAME, "Exception in getViewCarrierRegistration() at  ETransReportSessionBean"+e.toString());
      logger.error(FILE_NAME+ "Exception in getViewCarrierRegistration() at  ETransReportSessionBean"+e.toString());
		}
	}
	return terminals;
}//end of getViewCarrierRegistration method

//starts the getViewTerminalReports
 /**
	*The below method is used to view the Terminal Reports
    *This method takes count,terminalId as arguments and returns a Double Dimensional Array
	*@param String str,int count,String terminalId
	*@return String[][] terminals
	*/
public String[][] getViewTerminalReports(String str,int count,String terminalId)
{
	Connection connection = null;
	Statement stmt        = null;
	ResultSet rs          = null;
	String terminals[][]  = null;
	//ResultSet rs1         = null;//Commented By RajKumari on 30-10-2008 for Connection Leakages.
	String names[]   	  = null;
	int index			  = 0;
	int noOfrows = 0;
	String terminal=null ;
	try
	{
		connection =this.getConnection();
		stmt       =connection.createStatement();
		str = str.substring(0,str.length()-1);

		String sql="SELECT COMPANYID FROM FS_FR_TERMINALMASTER WHERE TERMINALID='"+terminalId+"' ";

		rs         =stmt.executeQuery(sql);
		if(rs.next())
		{terminal=rs.getString(1);}


		sql= "SELECT COUNT(*) NO_ROWS FROM FS_FR_TERMINALMASTER T, FS_ADDRESS A WHERE T.CONTACTADDRESSID= A.ADDRESSID  ORDER BY T.TERMINALID";

		rs         =stmt.executeQuery(sql);
		if(rs.next())
		sql= "SELECT "+ str +" FROM FS_FR_TERMINALMASTER T, FS_ADDRESS A WHERE T.CONTACTADDRESSID= A.ADDRESSID  ORDER BY T.TERMINALID";

		noOfrows     = rs.getInt("NO_ROWS");

		terminals    = new String[noOfrows][count];
		rs    =stmt.executeQuery(sql);
		for(int i=0;rs.next();i++)
		{
			for(int j=0;j<count;j++)
			{
				terminals[i][j] = rs.getString(j+1);
			}
		}
	}
		catch(SQLException sqEx)
        {
			//Logger.error(FILE_NAME, " -> "+sqEx.toString());
      logger.error(FILE_NAME+ " -> "+sqEx.toString());
            throw new EJBException(sqEx.toString());
        }
        finally
        {
			ConnectionUtil.closeConnection(connection, stmt, rs);
        }
	return terminals;
}//end of getViewTerminalReports() Method

// starts the getViewRevenueSharingReport method
/**
	*The below method is used to view the RevenueSharingReport
    *This method takes strQuery,count as arguments and returns a Double Dimensional Array
	*@param String strQuery,int count
	*@return String[][] Revenue
	*/
public String[][] getViewRevenueSharingReport(String strQuery,int count)
{
	Connection connection=null;
	Statement stmt        = null;
	ResultSet rs          = null;
	String Revenue[][]  = null;
	ResultSet rs1         = null;
	int noOfrows = 0;
	int   noOfcols=6;
	try
	{
		connection =this.getConnection();
		stmt       =connection.createStatement();

	strQuery = strQuery.substring(0,strQuery.length()-1);

	String sql= "SELECT "+strQuery+" FROM FS_FR_SHARINGSETUP ORDER BY CTYPE1";
	String sql1= "SELECT COUNT(*) NO_ROWS FROM FS_FR_SHARINGSETUP ORDER BY CTYPE1";
	rs        =stmt.executeQuery(sql1);
	if(rs.next())
		{noOfrows     = rs.getInt("NO_ROWS");}
	Revenue    = new String[noOfrows][count];
	rs1    =stmt.executeQuery(sql);
	for(int i=0;rs1.next();i++)
	{
		for(int j=0;j<count;j++)
		{
			Revenue[i][j] = rs1.getString(j+1);
		}
	}
	}
	catch(SQLException sqEx)
        {
			//Logger.error(FILE_NAME, " -> "+sqEx.toString());
      logger.error(FILE_NAME+" -> "+sqEx.toString());
            throw new EJBException(sqEx.toString());
        }
        finally
        {
			ConnectionUtil.closeConnection(connection, stmt, rs);
      ConnectionUtil.closeStatement(null,rs1);//Added By RajKumari on 30-10-2008 for Connection Leakages.
        }
	return Revenue;
}//end of getRevenueSharingReport() Method

public boolean isCarrierExists(String carrierId,String shipmentMode)
	{
		InitialContext 	initial		=	null; // to store InitialContext Object
	// @@ 'connection' & 'dataSource' are replaced with 'connection1' & 'dataSource1' by Suneetha for TogehterJ on 12 Jan 05
		DataSource 		dataSource1	=	null; // to store the DataSource Object
		Connection 		connection1 	= 	null; // to store the Connection Object
	// @@
		ResultSet		rs			=	null; // to store the ResultSet Object
		Statement 		stmt		=	null; // to store the Statement Object
		String 			sql 		= 	null; // to sql querry
		int				shipMode    =   0;
		try
		{
			initial	=	new InitialContext();
			dataSource1	=	(DataSource)initial.lookup("java:comp/env/jdbc/DB");
			connection1 = dataSource1.getConnection();
			stmt  = connection1.createStatement();
			shipMode    =   Integer.parseInt(shipmentMode.trim());
			sql		=	"SELECT CARRIERID FROM FS_FR_CAMASTER WHERE CARRIERID= '" + carrierId +"' ";
			rs = stmt.executeQuery(sql);
			if(!rs.next())
      {
				return false;
      }
			else
      {
				return true;
      }
		}
		catch(SQLException e)
		{
			
			//Logger.error(FILE_NAME,"ETransHOAdminSetupSessionBean :boolean deleteCarrierDetail(CarrierDetail carrierDetail, Address address, ESupplyGlobalParameters param)"+e.toString());
      logger.error(FILE_NAME+"ETransHOAdminSetupSessionBean :boolean deleteCarrierDetail(CarrierDetail carrierDetail, Address address, ESupplyGlobalParameters param)"+e.toString());
     
      return false;
      
		}
		catch(NamingException e)
		{
			
			//Logger.error(FILE_NAME,"ETransHOAdminSetupSessionBean :boolean deleteCarrierDetail(CarrierDetail carrierDetail, Address address, ESupplyGlobalParameters param)"+e.toString());
      logger.error(FILE_NAME+"ETransHOAdminSetupSessionBean :boolean deleteCarrierDetail(CarrierDetail carrierDetail, Address address, ESupplyGlobalParameters param)"+e.toString());
			return false;
			
		}
		catch(Exception e)
		{
			
			//Logger.error(FILE_NAME,"ETransHOAdminSetupSessionBean :boolean deleteCarrierDetail(CarrierDetail carrierDetail, Address address, ESupplyGlobalParameters param)"+e.toString());
      logger.error(FILE_NAME+"ETransHOAdminSetupSessionBean :boolean deleteCarrierDetail(CarrierDetail carrierDetail, Address address, ESupplyGlobalParameters param)"+e.toString());
			return false;
		}
		finally
		{
			try
			{
				initial	=	null;
				if(rs !=null)
        {
					rs.close();
        }
				if( stmt	!=	null)
        {
					stmt.close();
        }
				if(connection1 != null)
        {
					connection1.close();
        }
			}
			catch(SQLException e)
			{
				//Logger.error(FILE_NAME,"ETransHOAdminSetupSessionBean:boolean deleteCarrierDetail(CarrierDetail carrierDetail, Address address, ESupplyGlobalParameters param)"+e.toString());
        logger.error(FILE_NAME+"ETransHOAdminSetupSessionBean:boolean deleteCarrierDetail(CarrierDetail carrierDetail, Address address, ESupplyGlobalParameters param)"+e.toString());
			}
		}//finally
	}

	/**
   * This method is used to insert the data related to the new Carrier.
   * This method takes the Objects CarrierDetail, Address and ESupplyGlobalParameters as arguments
   * and inserts the Carrier Details along with the address into the tables FS_FR_CAMASTER and FS_ADDRESS.
   * This method also calls OperationImpl file which inturn inserts the user data into the table FS_USERLOG.
   *
   * @param					carrierDetail				Object		Details related to the Carrier
   * @param					address						Object		Details related to the Carrier Address
   * @param					ESupplyGlobalParameters     Object		ESupply Global Parameters
   *
   * @return				customerId					String		Customer Id
   *
   * @exception				javax.ejb.EJBException
   */
	public boolean setCarrierDetail(CarrierDetail			carrierDetail,
									Address					address,
									ESupplyGlobalParameters param)

	{
	// @@ 'connection' & 'dataSource' are replaced with 'connection1' & 'dataSource1' by Suneetha for TogehterJ on 12 Jan 05		
		DataSource 			dataSource1		=	null; // to store the DataSource Object
		Connection 			connection1 		= 	null; // to store the Connection Object
	// @@
		PreparedStatement	pstmt			=	null;
		try
		{
		
			InitialContext initial		=	new InitialContext();
			dataSource1					=	(DataSource)initial.lookup("java:comp/env/jdbc/DB");
			String			carrierId	=	carrierDetail.getCarrierId();
			connection1					=	dataSource1.getConnection();
			
			AddressDAO addressDAO= new AddressDAO();
			int addressId = addressDAO.create(address);
			
			String sql_Carrier	=	"INSERT INTO FS_FR_CAMASTER(CARRIERID,CARRIERNAME,NUMERICCODE,SHIPMENTMODE, "+
									"ADDRESSID,AUTO_NUMBER,TERMINALID) VALUES(?,?,?,?,?,?,?) ";
			pstmt				=	connection1.prepareStatement(sql_Carrier);
			pstmt.setString(1,carrierId);
			pstmt.setString(2,carrierDetail.getCarrierName());
			pstmt.setString(3,carrierDetail.getNumericCode());
			pstmt.setInt(4,Integer.parseInt(carrierDetail.getShipmentMode().trim()));
			pstmt.setInt(5,addressId);
			pstmt.setString(6,carrierDetail.getCarrierNumber());
			pstmt.setString(7,param.getTerminalId());
			pstmt.executeUpdate( );
			OperationsImpl impl	=	new OperationsImpl();
			impl.createDataSource();
			impl.setTransactionDetails( param.getTerminalId(),
										param.getUserId(),
										"CarrierReg",
										carrierId,
										param.getLocalTime(),
										"Add");
             //@@ Commented By kiran Kumar As Ledgers are not required for this build                           
		//	setLedger(	carrierId,param.getTerminalId(),addressId,param.getLocalTime());
			}
			catch(Exception e)
			{
				//Logger.error(FILE_NAME," CarrierRegistrationSessionBean :setCarrierDetail(CarrierDetail carrierDetail,Address address,ESupplyGlobalParameters param) "+e.toString());
        logger.error(FILE_NAME+" CarrierRegistrationSessionBean :setCarrierDetail(CarrierDetail carrierDetail,Address address,ESupplyGlobalParameters param) "+e.toString());
				throw new EJBException(e.toString());
			}
			finally
			{
				try
				{
					if(pstmt !=null)
          {
						pstmt.close();
          }
					if(connection1 != null)
          {
						connection1.close();
          }
				}
				catch(SQLException e)
				{
					//Logger.error(FILE_NAME,"CarrierRegistrationSessionBean:setCarrierDetail(CarrierDetail carrierDetail,Address address,ESupplyGlobalParameters param) "+e.toString());
          logger.error(FILE_NAME+"CarrierRegistrationSessionBean:setCarrierDetail(CarrierDetail carrierDetail,Address address,ESupplyGlobalParameters param) "+e.toString());
				}
		}//finally
		return true;
	} //setCarrierDetail

	/**
    * This method is used to update the data related to the CustomerDetail Object and Address Object
	* which inturn updates the data in the tables FS_FR_CAMASTER and FS_ADDRESS.
	* This method also calls OperationImpl file which inturn inserts the user data into the table FS_USERLOG.
	*
    * @param					carrierDetail				Object		Details related to the Carrier
	* @param					address						Object		Details related to the Carrier Address
    * @param					ESupplyGlobalParameters     Object		ESupply Global Parameters
    *
    * @exception	
    * @return
    */
	public int updateCarrierDetail(CarrierDetail			carrierDetail,
									Address					address,
									ESupplyGlobalParameters param,
									String					shipmentMode)

	{
		InitialContext 	initial		=	null; // to store InitialContext Object
	// @@ 'connection' & 'dataSource' are replaced with 'connection1' & 'dataSource1' by Suneetha for TogehterJ on 12 Jan 05
		DataSource 		dataSource1	=	null; // to store the DataSource Object
		Connection 		connection1 	= 	null; // to store the Connection Object
	// @2
		ResultSet		rs1			=	null; // to store the ResultSet Object
		Statement 		stmt		=	null; // to store the Statement Object
		Statement		stmt1		=	null; // to store the Statement Object
		int 			addressId	=	0;    //// to store the AddressId
		int				shipMode	=   0;
		int				result		=   0;
		try
		{
			shipMode = Integer.parseInt(carrierDetail.getShipmentMode().trim());
		}
		catch(NumberFormatException e)
		{
			//Logger.error(FILE_NAME," CarrierRegistrationSessionBean :updateCarrierDetail(CarrierDetail carrierDetail,Address	address,ESupplyGlobalParameters param,String shipmentMode) "+e.toString());
      logger.error(FILE_NAME+" CarrierRegistrationSessionBean :updateCarrierDetail(CarrierDetail carrierDetail,Address	address,ESupplyGlobalParameters param,String shipmentMode) "+e.toString());
		}
		try
		{
			initial				= new InitialContext();
			dataSource1			= (DataSource)initial.lookup("java:comp/env/jdbc/DB");
			connection1			= dataSource1.getConnection();
			String carrierId	= carrierDetail.getCarrierId();
			stmt1				= connection1.createStatement();
			String sql			= "SELECT ADDRESSID FROM FS_FR_CAMASTER WHERE CARRIERID='" + carrierId + "' AND SHIPMENTMODE = "+shipMode+"";
			rs1 = stmt1.executeQuery(sql);
			if(rs1.next())
      {
				addressId =  rs1.getInt(1);
      }
			sql = "UPDATE FS_FR_CAMASTER SET CARRIERNAME='" + carrierDetail.getCarrierName() +
								 "',NUMERICCODE='" + carrierDetail.getNumericCode() +
								 "',AUTO_NUMBER='" + carrierDetail.getCarrierNumber() +
								 "',SHIPMENTMODE=" + shipMode +
								 " WHERE CARRIERID='"+ carrierId +"' ";//AND SHIPMENTMODE = '"+shipmentMode+"' ";
			stmt = connection1.createStatement();
			result = stmt.executeUpdate(sql);
			address.setAddressId(addressId);
			AddressDAO addressDAO= new AddressDAO();
			addressDAO.updateAddress(address);
			OperationsImpl impl	=	new OperationsImpl();
			impl.createDataSource();
			impl.setTransactionDetails( param.getTerminalId(),
										param.getUserId(),
										"CarrierReg",
										carrierId,
										param.getLocalTime(),
										"Modify");
		}
		catch(SQLException e)
		{
			
			//Logger.error(FILE_NAME,"CarrierRegistrationSessionBean : void updateCarrierDetail(CarrierDetail carrierDetail,Address address,ESupplyGlobalParameters param) "+e.toString());
      logger.error(FILE_NAME+"CarrierRegistrationSessionBean : void updateCarrierDetail(CarrierDetail carrierDetail,Address address,ESupplyGlobalParameters param) "+e.toString());
      throw new javax.ejb.EJBException(e.toString());
		}
		catch(NamingException e)
		{
			
			//Logger.error(FILE_NAME," CarrierRegistrationSessionBean :void updateCarrierDetail(CarrierDetail carrierDetail,Address address,ESupplyGlobalParameters param) "+e.toString());
      logger.error(FILE_NAME+" CarrierRegistrationSessionBean :void updateCarrierDetail(CarrierDetail carrierDetail,Address address,ESupplyGlobalParameters param) "+e.toString());
      throw new javax.ejb.EJBException(e.toString());
		}
		catch(Exception e)
		{
			
			//Logger.error(FILE_NAME,"CarrierRegistrationSessionBean : void updateCarrierDetail(CarrierDetail carrierDetail,Address address,ESupplyGlobalParameters param)"+e.toString());
      logger.error(FILE_NAME+"CarrierRegistrationSessionBean : void updateCarrierDetail(CarrierDetail carrierDetail,Address address,ESupplyGlobalParameters param)"+e.toString());
      throw new javax.ejb.EJBException(e.toString());
		}
		finally
		{
			try
			{
				initial	=	null;
				if(rs1 !=null)
        {
					rs1.close();
        }
				if(stmt != null)
        {
					stmt.close();
        }
				if(stmt1 != null)
        {
					stmt1.close();
        }
				if(connection1 != null)
        {
					connection1.close();
        }
			}
			catch(SQLException e)
			{
				//Logger.error(FILE_NAME,"CarrierRegistrationSessionBean : void updateCarrierDetail(CarrierDetail carrierDetail,Address address,ESupplyGlobalParameters param)"+e.toString());
        logger.error(FILE_NAME+"CarrierRegistrationSessionBean : void updateCarrierDetail(CarrierDetail carrierDetail,Address address,ESupplyGlobalParameters param)"+e.toString());
			}
		}//finally
		return result;
	}//updateCarrierDetail

	/**
    * This method is used to delete the data related to the CustomerDetail Object and Address Object
	* which inturn deltes the record from the tables FS_FR_CAMASTER and FS_ADDRESS.
	* This method also calls OperationImpl file which inturn inserts the user data into the table FS_USERLOG.
	*
    * @param					carrierDetail				Object		Details related to the Carrier
	* @param					address						Object		Details related to the Carrier Address
    * @param					ESupplyGlobalParameters     Object		ESupply Global Parameters
    *
    * @exception				javax.ejb.EJBException
    */
	public boolean deleteCarrierDetail(CarrierDetail carrierDetail, Address address, ESupplyGlobalParameters param,String shipmentMode)

	{
		InitialContext 	initial		=	null; // to store InitialContext Object
	// @@ 'connection' & 'dataSource' are replaced with 'connection1' & 'dataSource1' by Suneetha for TogehterJ on 12 Jan 05
		DataSource 		dataSource1	=	null; // to store the DataSource Object
		Connection 		connection1 	= 	null; // to store the Connection Object
	// @2
		ResultSet		rs			=	null; // to store the ResultSet Object
		Statement 		stmt		=	null; // to store the Statement Object
		Statement		stmt2		=	null; // to store the Statement Object
		int 			addressId	=	0;    // to store the AddressId
		String 			sql 		= 	null; // to sql querry
		int				shipMode	=   0;
		try
		{
			shipMode = Integer.parseInt(carrierDetail.getShipmentMode().trim());
		}
		catch(NumberFormatException e)
		{
			//Logger.error(FILE_NAME," CarrierRegistrationSessionBean :deleteCarrierDetail(CarrierDetail carrierDetail, Address address, ESupplyGlobalParameters param,String shipmentMode) "+e.toString());
      logger.error(FILE_NAME+" CarrierRegistrationSessionBean :deleteCarrierDetail(CarrierDetail carrierDetail, Address address, ESupplyGlobalParameters param,String shipmentMode) "+e.toString());
		}
		try
		{
			initial	=	new InitialContext();
			dataSource1	=	(DataSource)initial.lookup("java:comp/env/jdbc/DB");
			connection1 = dataSource1.getConnection();
			stmt  = connection1.createStatement();
			stmt2 = connection1.createStatement();
			String carrierId = carrierDetail.getCarrierId();
			// Address....
			sql = "SELECT ADDRESSID FROM FS_FR_CAMASTER WHERE CARRIERID='" + carrierId + "' AND SHIPMENTMODE = "+shipMode+" ";
			rs = stmt.executeQuery(sql);
			if(rs.next())
				addressId =  rs.getInt(1);
			// Carrier Master....
			sql = "DELETE FROM FS_FR_CAMASTER WHERE CARRIERID='"+ carrierId + "' AND SHIPMENTMODE = "+shipMode+" ";
			stmt2.executeUpdate(sql);
		
			AddressDAO addressDAO= new AddressDAO();
			addressDAO.removeAddress(addressId);

			OperationsImpl impl	=	new OperationsImpl();
			impl.createDataSource();
			impl.setTransactionDetails( param.getTerminalId(),
										param.getUserId(),
										"CarrierReg",
										carrierId,
										param.getLocalTime(),
										"Delete");
			return true;
		}
		catch(SQLException e)
		{
			
			//Logger.error(FILE_NAME,"CarrierRegistrationSessionBean :boolean deleteCarrierDetail(CarrierDetail carrierDetail, Address address, ESupplyGlobalParameters param)"+e.toString());
      logger.error(FILE_NAME+"CarrierRegistrationSessionBean :boolean deleteCarrierDetail(CarrierDetail carrierDetail, Address address, ESupplyGlobalParameters param)"+e.toString());
			return false;
		}
		catch(NamingException e)
		{
			
			//Logger.error(FILE_NAME," CarrierRegistrationSessionBean :boolean deleteCarrierDetail(CarrierDetail carrierDetail, Address address, ESupplyGlobalParameters param)"+e.toString());
      logger.error(FILE_NAME+" CarrierRegistrationSessionBean :boolean deleteCarrierDetail(CarrierDetail carrierDetail, Address address, ESupplyGlobalParameters param)"+e.toString());
			return false;
		}
		catch(Exception e)
		{
			
			//Logger.error(FILE_NAME," CarrierRegistrationSessionBean :boolean deleteCarrierDetail(CarrierDetail carrierDetail, Address address, ESupplyGlobalParameters param)"+e.toString());
      logger.error(FILE_NAME+" CarrierRegistrationSessionBean :boolean deleteCarrierDetail(CarrierDetail carrierDetail, Address address, ESupplyGlobalParameters param)"+e.toString());
			return false;
		}
		finally
		{
			try
			{
				initial	=	null;
				if(rs !=null)
        {
					rs.close();
        }
				if( stmt	!=	null)
        {
					stmt.close();
        }
				if(stmt2 != null)
        {
					stmt2.close();
        }
				if(connection1 != null)
        {
					connection1.close();
        }
			}
			catch(SQLException e)
			{
				//Logger.error(FILE_NAME,"CarrierRegistrationSessionBean:boolean deleteCarrierDetail(CarrierDetail carrierDetail, Address address, ESupplyGlobalParameters param)"+e.toString());
        logger.error(FILE_NAME+"CarrierRegistrationSessionBean:boolean deleteCarrierDetail(CarrierDetail carrierDetail, Address address, ESupplyGlobalParameters param)"+e.toString());
			}
		}//finally
	} //deleteCarrierDetail

	/**
   * 
   * @param CarrierId
   * @param shipmentMode
   * @return 
   */
	public boolean checkCarrier(String CarrierId,String shipmentMode)
	{
		InitialContext 	initial		=	null; // to store InitialContext Object
	// @@ 'connection' & 'dataSource' are replaced with 'connection1' & 'dataSource1' by Suneetha for TogehterJ on 12 Jan 05
		DataSource 		dataSource1	=	null; // to store the DataSource Object
		Connection 		connection1 	= 	null; // to store the Connection Object
	// @@
		ResultSet 		rs			=	null; // to store the ResultSet Object
		Statement 		stmt		=	null; // to store the Statement Object
/*********
		try
		{
			shipMode = Integer.parseInt(shipmentMode.trim());
			if(shipMode == 1)
      {
				shipmentMode = "(1,3,5,7)";
        }
			if(shipMode == 2)
      {
				shipmentMode = "(2,3,6,7)";
        }
			if(shipMode == 4)
      {
				shipmentMode = "(4,5,6,7)";
        }
		}
		catch(NumberFormatException e)
		{
			Logger.error(FILE_NAME," ETransHOAdminSetupSessionBean :checkCarrier(String CarrierId,String shipmentMode "+e.toString());
      
		}
*********/
		try
		{
			initial			=	new InitialContext();
			dataSource1	=	(DataSource) initial.lookup("java:comp/env/jdbc/DB");
			connection1	=	dataSource1.getConnection();
			stmt		=	connection1.createStatement();
		//	String sql	=	"SELECT CARRIERID FROM FS_FR_CAMASTER WHERE CARRIERID='"+ CarrierId + "' AND SHIPMENTMODE IN "+shipmentMode+"";
			String sql	=	"SELECT CARRIERID FROM FS_FR_CAMASTER WHERE CARRIERID='"+ CarrierId + "' ";
			rs	=	stmt.executeQuery(sql);
			if(rs.next())
      {
				return true;
      }
			else
      {
				return false;
      }
		}
		catch(NamingException e)
		{
			
			//Logger.error(FILE_NAME,"ETransHOAdminSetupSessionBean :checkCarrier(String CarrierId):"+e.toString());
      logger.error(FILE_NAME+"ETransHOAdminSetupSessionBean :checkCarrier(String CarrierId):"+e.toString());
      throw new javax.ejb.EJBException(e.toString());
		}
		catch(SQLException e)
		{
			
			//Logger.error(FILE_NAME," ETransHOAdminSetupSessionBean :checkCarrier(String CarrierId):"+e.toString());
      logger.error(FILE_NAME+" ETransHOAdminSetupSessionBean :checkCarrier(String CarrierId):"+e.toString());
      throw new javax.ejb.EJBException(e.toString());
		}
		finally
		{
			try
			{
				initial	=	null;
				if(rs !=null)
        {
					rs.close();
        }
				if(stmt != null)
        {
					stmt.close();
        }
				if(connection1 != null)
        {
					connection1.close();
        }
			}
			catch(SQLException e)
			{
					//Logger.error(FILE_NAME,"ETransHOAdminSetupSessionBean :checkCarrier(String CarrierId):"+e.toString());
          logger.error(FILE_NAME+"ETransHOAdminSetupSessionBean :checkCarrier(String CarrierId):"+e.toString());
			}
		}//finally
	}

	/**
   * This method is used to retrieve the Carrier Details for the specifed Carrier Id
   * This method takes Carrier Id as arguments and returns an Object  that contains
   * Carrier Details for this Carrier Id from the table FS_FR_CAMASTER
   *
   * @param			carrierId			String			Carrier Id
   *
   * @return		CarrierDetail		Object with all the above details
   *
   * @exception		javax.ejb.EJBException
   */
	public CarrierDetail getCarrierDetail(String carrierId,String shipmentMode)

	{
		InitialContext 	initial		=	null; // to store InitialContext Object
	// @@ 'connection' & 'dataSource' are replaced with 'connection1' & 'dataSource1' by Suneetha for TogehterJ on 12 Jan 05
		DataSource 		dataSource1	=	null; // to store the DataSource Object
		Connection 		connection1 	= 	null; // to store the Connection Object
	// @@
		ResultSet 		rs			=	null; // to store the ResultSet Object
		Statement 		stmt		=	null; // to store the Statement Object
		CarrierDetail carrierDetails = null; // to store the CarrierDetail Object
		int				shipMode	=   0;
/*
		try
		{
			shipMode = Integer.parseInt(shipmentMode.trim());
			if(shipMode == 1)
				shipmentMode = "(1,3,5,7)";
			if(shipMode == 2)
				shipmentMode = "(2,3,6,7)";
			if(shipMode == 4)
				shipmentMode = "(4,5,6,7)";
		}
		catch(NumberFormatException e)
		{
			Logger.error(FILE_NAME," CarrierRegistrationSessionBean :getCarrierDetail(String carrierId,String shipmentMode) "+e.toString());
		}
*/
		try
		{
			carrierDetails = new CarrierDetail();
			initial	=	new InitialContext();
			dataSource1	=	(DataSource)initial.lookup("java:comp/env/jdbc/DB");
			connection1 = dataSource1.getConnection();
/*			String sql = "SELECT A.CARRIERID,A.CARRIERNAME,A.NUMERICCODE,A.SHIPMENTMODE,A.ADDRESSID FROM FS_FR_CAMASTER A,"+
							"FS_ADDRESS B WHERE A.ADDRESSID = B.ADDRESSID AND CARRIERID ='" + carrierId +"' AND A.SHIPMENTMODE IN "+shipmentMode+"";
*/
			String sql = "SELECT A.CARRIERID,A.CARRIERNAME,A.NUMERICCODE,A.SHIPMENTMODE,A.ADDRESSID,A.AUTO_NUMBER FROM FS_FR_CAMASTER A,"+
							"FS_ADDRESS B WHERE A.ADDRESSID = B.ADDRESSID AND CARRIERID ='" + carrierId +"' ";
			stmt = connection1.createStatement();
			rs   = stmt.executeQuery(sql);
			if(rs.next())
			{
				carrierDetails.setCarrierId(rs.getString(1));
				carrierDetails.setCarrierName(rs.getString(2));
				carrierDetails.setNumericCode(rs.getString(3));
				carrierDetails.setShipmentMode(rs.getString(4));
				carrierDetails.setAddressId(rs.getInt(5));
				carrierDetails.setCarrierNumber(rs.getString(6));

			}
		}
		catch(SQLException e)
		{
			
			//Logger.error(FILE_NAME," ETransHOAdminSetupSessionBean : CarrierDetail getCarrierDetail(String carrierId)"+e.toString());
      logger.error(FILE_NAME+" ETransHOAdminSetupSessionBean : CarrierDetail getCarrierDetail(String carrierId)"+e.toString());
			return null;
		}
		catch(NamingException e)
		{
			
			//Logger.error(FILE_NAME,"ETransHOAdminSetupSessionBean : CarrierDetail getCarrierDetail(String carrierId)"+e.toString());
      logger.error(FILE_NAME+"ETransHOAdminSetupSessionBean : CarrierDetail getCarrierDetail(String carrierId)"+e.toString());
			return	null;
		}
		catch(Exception e)
		{
			
			//Logger.error(FILE_NAME," ETransHOAdminSetupSessionBean: CarrierDetail getCarrierDetail(String carrierId) "+e.toString());
      logger.error(FILE_NAME+" ETransHOAdminSetupSessionBean: CarrierDetail getCarrierDetail(String carrierId) "+e.toString());
			return null;
		}
		finally
		{
			try
			{
				initial	=	null;
				if(rs !=null)
        {
					rs.close();
        }
				if(stmt != null)
        {
					stmt.close();
        }
				if(connection1 != null)
        {
					connection1.close();
        }
			}
			catch(SQLException e)
			{
				//Logger.error(FILE_NAME," ETransHOAdminSetupSessionBean:CarrierDetail getCarrierDetail(String carrierId) "+e.toString());
        logger.error(FILE_NAME+" ETransHOAdminSetupSessionBean:CarrierDetail getCarrierDetail(String carrierId) "+e.toString());
			}
		}//finally
		return 	carrierDetails;
	} //getCarrierDetail

	/**
   * 
   * @param addressId
   * @return 
   */
	public Address getAddressDetails(int addressId)
	{
		Address address = null;                  // Address Object
		AddressDAO addressDAO= new AddressDAO();
		try
		{
		address	= addressDAO.load(addressId);
		}catch(Exception sqlEx)
		{
			//Logger.error(FILE_NAME, "[getAddressDetails(addressId)] -> "+sqlEx.toString());
      logger.error(FILE_NAME+ "[getAddressDetails(addressId)] -> "+sqlEx.toString());
			throw new EJBException(sqlEx);
		}
	  
		return address;
	} 

	 /**
   * 
   * @param shipmentMode
   * @param searchString
   * @return 
   */
	public ArrayList getSpcificCarrierIds(String shipmentMode,String searchString)
	{
        ArrayList carrierIds = new ArrayList();

        Connection	connection	= null;
        Statement	stmt		= null;
        ResultSet	rs			= null;

        try
        {
			

		if(shipmentMode.equals("Air"))
			{shipmentMode = "(1,3)";}
		if(shipmentMode.equals("Sea"))
			{shipmentMode = "(2,3)";}
		if(shipmentMode.equals("All"))
			{shipmentMode = "(3)";}

	
		if(searchString!=null && !(searchString.equals("")))
		{
			searchString= " AND A.CARRIERID LIKE '"+searchString+"%'  ";
		}
		else
		{
			searchString = "";	 			 
		}
		
			
			connection = this.getConnection();
			stmt = connection.createStatement();
			String sql	=	" SELECT DISTINCT A.CARRIERID,A.CARRIERNAME,A.SHIPMENTMODE FROM FS_FR_CAMASTER A, FS_ADDRESS B "+ 							"  WHERE A.SHIPMENTMODE IN "+shipmentMode+" "+searchString+"  ORDER BY A.CARRIERID ";
			rs= stmt.executeQuery(sql);
			//System.out.println("the qyery is " + sql);
			while(rs.next())
			{carrierIds.add(rs.getString(1)+" [ "+rs.getString(2)+" ] "+" [ "+getShipmentModeValue(rs.getInt(3))+" ] ");}
        }
        catch(SQLException sqEx)
        {
			//Logger.error(FILE_NAME, "[getCarrierIds(whereClause)] -> "+sqEx.toString());
      logger.error(FILE_NAME+ "[getCarrierIds(whereClause)] -> "+sqEx.toString());
            throw new EJBException(sqEx.toString());
        }
        finally
        {
			ConnectionUtil.closeConnection(connection, stmt, rs);
        }
        return carrierIds;
    }

	//New Method 
  /**
   * 
   * @param shipmentMode
   * @param searchString
   * @param address
   * @return 
   */
public ArrayList getCarrierIds(String shipmentMode,String searchString,String address,String operation,String terminalId)
	{
        ArrayList carrierIds = new ArrayList();

        Connection	connection	= null;
       // Statement	stmt		= null;//Commented By RajKumari on 30-10-2008 for Connection Leakages.
        ResultSet	rs			= null;
		CallableStatement cstmt = null;

        try
        {
			
/*
		if(shipmentMode.equals("All"))
			{shipmentMode = "(1,2,3,4,5,6,7)";		}
		else if(shipmentMode.equals("Air"))
			{shipmentMode = "(1,3,5,7)";}
		else if(shipmentMode.equals("Truck"))
			{shipmentMode = "(4,5,6,7)";}
		else if(shipmentMode.equals("Sea"))
			{shipmentMode = "(2,3,6,7)";}

	
		if(searchString!=null && !(searchString.equals(""))){
			searchString= " WHERE A.CARRIERID LIKE '"+searchString+"%' AND A.SHIPMENTMODE IN "+shipmentMode+" ";
		}else{
			searchString = " WHERE A.SHIPMENTMODE IN "+shipmentMode+" ";	 			 
		}
		if(address!=null && address.equals("yes") ){
			address	=	" AND  A.ADDRESSID=B.ADDRESSID ";
		}else{
			address = "";
		}*/
			
			connection = this.getConnection();
			//stmt = connection.createStatement();
			//rs= stmt.executeQuery("SELECT DISTINCT A.CARRIERID,A.CARRIERNAME,A.SHIPMENTMODE FROM FS_FR_CAMASTER A, FS_ADDRESS B "+searchString+" "+ address+" ORDER BY A.CARRIERID");
      cstmt        = connection.prepareCall("{ ?= call QMS_SETUPS.GET_CARRIER(?,?,?,?)}");
      cstmt.registerOutParameter(1,OracleTypes.CURSOR);
      cstmt.setString(2,terminalId);      
      cstmt.setString(3,operation);
      cstmt.setString(4,(searchString+"%"));
      cstmt.setString(5,shipmentMode);
      cstmt.execute();
      rs  =  (ResultSet)cstmt.getObject(1);
			while(rs.next())
			{carrierIds.add(rs.getString(1)+" [ "+rs.getString(2)+" ] "+" [ "+getShipmentModeValue(rs.getInt(3))+" ] ");}
        }
        catch(SQLException sqEx)
        {sqEx.printStackTrace();
			//Logger.error(FILE_NAME, "[getCarrierIds(whereClause)] -> "+sqEx.toString());
      logger.error(FILE_NAME+ "[getCarrierIds(whereClause)] -> "+sqEx.toString());
            throw new EJBException(sqEx.toString());
        }
        catch(Exception sqEx)
        {sqEx.printStackTrace();
			//Logger.error(FILE_NAME, "[getCarrierIds(whereClause)] -> "+sqEx.toString());
      logger.error(FILE_NAME+ "[getCarrierIds(whereClause)] -> "+sqEx.toString());
            throw new EJBException(sqEx.toString());
        }
        finally
        {
			ConnectionUtil.closeConnection(connection, cstmt, rs);
        }
        return carrierIds;
    }
//added by rk for buyrates
public ArrayList getCarrierIds1(String shipmentMode,String searchString,String address,String operation,String terminalId)
{
        ArrayList carrierIds = new ArrayList();

        Connection	connection	= null;
        //Statement	stmt		= null;//Commented By RajKumari on 30-10-2008 for Connection Leakages.
        ResultSet	rs			= null;
		    CallableStatement cstmt = null;
        String carrierId  = "";

        try
        {
          if(searchString!=null)
             carrierId = searchString.replaceAll(",","','");
          connection = this.getConnection();
          cstmt        = connection.prepareCall("{ ?= call QMS_SETUPS.get_carrier_buyrate(?,?,?,?)}");
          cstmt.registerOutParameter(1,OracleTypes.CURSOR);
          cstmt.setString(2,terminalId);      
          cstmt.setString(3,operation);
          cstmt.setString(4,carrierId);
          cstmt.setString(5,shipmentMode);
          //System.out.println("carrierId "+carrierId);
          //System.out.println("shipmentMode "+shipmentMode);
          //System.out.println("terminalId "+terminalId);
          cstmt.execute();
          rs  =  (ResultSet)cstmt.getObject(1);
          //System.out.println("rs "+rs);
          while(rs.next())
          {
            carrierIds.add(rs.getString(1)+" [ "+rs.getString(2)+" ] "+" [ "+getShipmentModeValue(rs.getInt(3))+" ] ");}
          }
        catch(SQLException sqEx)
        {
          sqEx.printStackTrace();
          //Logger.error(FILE_NAME, "[getCarrierIds(whereClause)] -> "+sqEx.toString());
          logger.error(FILE_NAME+ "[getCarrierIds(whereClause)] -> "+sqEx.toString());
            throw new EJBException(sqEx.toString());
        }
        catch(Exception sqEx)
        {
            sqEx.printStackTrace();
			      //Logger.error(FILE_NAME, "[getCarrierIds(whereClause)] -> "+sqEx.toString());
            logger.error(FILE_NAME+ "[getCarrierIds(whereClause)] -> "+sqEx.toString());
            throw new EJBException(sqEx.toString());
        }
        finally
        {
            ConnectionUtil.closeConnection(connection, cstmt, rs);
        }
        return carrierIds;
    }
  
  public ArrayList getCarriersForCSR(String shipmentMode,String originLoc,String destLoc,String terminalId)
  {
        ArrayList carrierIds = new ArrayList();

        Connection	connection	= null;
        //Statement	stmt		= null;////Commented By RajKumari on 30-10-2008 for Connection Leakages.
        ResultSet	rs			= null;
		    CallableStatement cstmt = null;
        String carrierId  = "";

        try
        {
          connection = this.getConnection();
          
          if(originLoc!=null && originLoc.trim().length()!=0)
              originLoc = originLoc+"%";
          else
              originLoc = "%";
          
          if(destLoc!=null && destLoc.trim().length()!=0)
              destLoc = destLoc+"%";
          else
              destLoc = "%";
          
          cstmt        = connection.prepareCall("{ ?= call QMS_SETUPS.get_carrier_csr(?,?,?,?)}");
          cstmt.registerOutParameter(1,OracleTypes.CURSOR);
          cstmt.setString(2,terminalId);      
          cstmt.setString(3,originLoc);
          cstmt.setString(4,destLoc);
          cstmt.setString(5,shipmentMode);
          cstmt.execute();
          rs  =  (ResultSet)cstmt.getObject(1);
          //System.out.println("rs "+rs);
          while(rs.next())
          {
            carrierIds.add(rs.getString(1)+" [ "+rs.getString(2)+" ] "+" [ "+getShipmentModeValue(rs.getInt(3))+" ] ");}
          }
        catch(SQLException sqEx)
        {
          sqEx.printStackTrace();
          //Logger.error(FILE_NAME, "[getCarrierIds(whereClause)] -> "+sqEx.toString());
          logger.error(FILE_NAME+ "[getCarrierIds(whereClause)] -> "+sqEx.toString());
            throw new EJBException(sqEx.toString());
        }
        catch(Exception sqEx)
        {
            sqEx.printStackTrace();
			      //Logger.error(FILE_NAME, "[getCarrierIds(whereClause)] -> "+sqEx.toString());
            logger.error(FILE_NAME+ "[getCarrierIds(whereClause)] -> "+sqEx.toString());
            throw new EJBException(sqEx.toString());
        }
        finally
        {
            ConnectionUtil.closeConnection(connection, cstmt, rs);
        }
        return carrierIds;
    }
	/*Used to Get Event Data*/
  /**
   * 
   * @return ArrayList
   */
//Added by rk for getting locationids with portids for sea
public ArrayList getLocationIdsAndPorts(String terminalId,String operation,String searchString,String shipmentMode)
	{
		Connection        connection =  null;
    //PreparedStatement pStmt      =  null;//Commented By RajKumari on 30-10-2008 for Connection Leakages.
    ResultSet         rs          =  null;
    ArrayList locationIds=new ArrayList();
    CallableStatement cstmt = null;

	try
		{
	   connection     =   this.getConnection();
		
		  cstmt        = connection.prepareCall("{ ?= call QMS_SETUPS.get_locidsWithPortIds(?,?,?,?)}");
          cstmt.registerOutParameter(1,OracleTypes.CURSOR);
          cstmt.setString(2,terminalId);
          cstmt.setString(3,operation);
          cstmt.setString(4,(searchString+"%"));
          cstmt.setString(5,shipmentMode);
          cstmt.execute();
          rs  =  (ResultSet)cstmt.getObject(1);
		while(rs.next())
		{
			locationIds.add(rs.getString(1)+"["+rs.getString(2)+"]");			
		}
		}catch(Exception e)
		{
		e.printStackTrace();

      logger.info(FILE_NAME+"Error while retrieving contentIds"+e.toString());
		  throw new EJBException("Error while retriving the locationIds");

		}
		 finally
    {
      ConnectionUtil.closeConnection(connection,cstmt,rs);//Modified By RajKumari on 30-10-2008 for Connection Leakages.
    }
    return locationIds;
	}

  /**
   * 
   * @param searchString
   * @return 
   */
public String[] getLocationIds(String searchString,String str0)
     {
    String locationIds[]=null;
// @@ Replaced by Suneetha for TogetherJ on 12 Jan 05
//    String str="";
    StringBuffer str= new StringBuffer();
// @@
    Connection con=null;
    PreparedStatement ptst=null;
    ResultSet rs=null;
    int count=0; 
       try
      {
       con=this.getConnection();
       // @@ Sailaja added LOCATIONNAME in the query on 2005 05 26 for SPETI-1768
       //ptst=con.prepareStatement("SELECT LOCATIONID FROM FS_FR_LOCATIONMASTER ORDER BY LOCATIONID");
       ptst=con.prepareStatement("SELECT LOCATIONID,LOCATIONNAME FROM FS_FR_LOCATIONMASTER ORDER BY LOCATIONID");
       // @@ 2005 05 26 for SPETI-1768
       rs=ptst.executeQuery();
      while (rs.next())
         {
         count++;
		 // @@ Replaced by Suneetha for TogetherJ on 12 Jan 05
         // str=str+"**"+rs.getString(1);
                 //Modified By G.Srinivas on 20050118 TJ Fix
         //str.append(str);
		 // @@

    		 str.append("**");
			 str.append(rs.getString(1));
			 // @@ Added by Sailaja on 2005 05 26 for SPETI-1768
		     str.append("("+rs.getString(2)+")");
			 // @@ 2005 05 26 for SPETI-1768
		 // @@
         }
     //System.out.println("Count Value:"+count);    
     if(count>0)
       {
       StringTokenizer tokenizer=new StringTokenizer(str.toString(),"**");
       ////System.out.println("Number of Tokens:"+tokenizer.countTokens());  

       locationIds=new String[count];
       count=0;
       while (tokenizer.hasMoreTokens())
         {
          
           locationIds[count]=(String)tokenizer.nextElement();
           count++;
         }
         
         }
        
      }
      catch(SQLException sqlEx)
        {
		  
          //Logger.error(FILE_NAME,"getLocationIds(String)-->",sqlEx.toString());
          logger.error(FILE_NAME+"getLocationIds(String)-->"+sqlEx.toString());
          throw new EJBException(sqlEx.toString());

        }
      finally
           {
             ConnectionUtil.closeConnection(con, ptst, rs);
             //System.out.println(count);
           }
       return locationIds;
    }

	/**
   * 
   * @param serviceId
   * @return 
   */
public int isServiceLevelIdExists( String serviceId)
	{
		Connection connection =	null;
		Statement stmt 		  = null;
		ResultSet rs 		  = null;
		int sMode			  = 0;
		try
		{
			String sqlQuery	= "	SELECT SHIPMENTMODE FROM FS_FR_SERVICELEVELMASTER WHERE FS_FR_SERVICELEVELMASTER.SERVICELEVELID = '"+serviceId.trim()+"'";
		connection 	= 	getConnection();
			stmt = connection.createStatement();
			rs = stmt.executeQuery(	sqlQuery );
			if(	rs.next())
				{sMode	=	rs.getInt(1);}
			else
				{return 0;}
		}
		catch( SQLException	sqle)
		{
			
		  //Logger.error(FILE_NAME," ETransHOSuperUserSetupSessionBean : SQLException in isServiceProvides() : "+sqle.toString());
      logger.error(FILE_NAME+" ETransHOSuperUserSetupSessionBean : SQLException in isServiceProvides() : "+sqle.toString());
      throw new EJBException(sqle.toString());
		}
		catch(Exception	e)
		{
				//Logger.error(FILE_NAME,"ETransHOSuperUserSetupSessionBean	: isServiceProvides() :	"+e.toString());
        logger.error(FILE_NAME+"ETransHOSuperUserSetupSessionBean	: isServiceProvides() :	"+e.toString());
        throw new EJBException(e.toString());

		}
		finally
		{
			ConnectionUtil.closeConnection(connection,stmt,rs);
		}
		return sMode;
	}//end of isServiceLevelIdExists*/
/*Used to Get Event Data*/
  /**
   * 
   * @return ArrayList
   */
public ArrayList getEventData()
  {
    Connection connection = null;
    Statement stmt        = null;
    ResultSet  rs         = null;
    ArrayList  evntData   = new ArrayList();
    String[]     evntDesc = null;
    int[]        evntId   = null; 
    int          count    = 0;
    try
      {
        connection = this.getConnection();
        stmt       = connection.createStatement();
        rs         = stmt.executeQuery("SELECT EVNT_ID,EVNT_DESC FROM FS_FR_EVNT");
        while (rs.next())
          count++;
         if(count>0)
          {
            evntDesc = new String[count];
            evntId   = new int[count];
            rs.close();
            stmt.close();
            stmt       = connection.createStatement();
            rs         = stmt.executeQuery("SELECT EVNT_ID,EVNT_DESC FROM FS_FR_EVNT ORDER BY EVNT_ID");
            count =0;
            while(rs.next())
               {
                evntId[count]  = rs.getInt(1); 
                 evntDesc[count] = rs.getString(2);
                 count++;
               }
           evntData.add(0,evntId);
           evntData.add(1,evntDesc);
          }
        
      }
   catch(SQLException sqlEx)
     {
	   
       //Logger.error(FILE_NAME,"Error in getting Event Information in getEventData()"+sqlEx.toString());
       logger.error(FILE_NAME+"Error in getting Event Information in getEventData()"+sqlEx.toString());
       throw new EJBException(sqlEx.toString());
     }
   finally
        {
        try
          {
          ConnectionUtil.closeConnection(connection,stmt,rs);
          }
        catch(Exception ex)
           {
             //Logger.error(FILE_NAME,"Error in Closing Database Connection in getEventData()");
             logger.error(FILE_NAME+"Error in Closing Database Connection in getEventData()");
           }
        }
     return evntData;   
  }

  /**
   * 
   * @param serviceLevel
   * @param loginDetails
   * @return 
   */
public boolean addServiceLevelDetails(ServiceLevelJspBean serviceLevel,ESupplyGlobalParameters loginDetails)
	{
	Connection connection =	null;
	Statement stmt = null;
    PreparedStatement ptst=null;
    String[] EventIds=null;
    int[] AllocatedTime=null;
    int[] AlertTime=null;
    String[] OriginLocations=null;
    String[] DestiLocations=null;
		try
		{
			connection = getConnection();
			stmt 	   = connection.createStatement();
			String sid =serviceLevel.getServiceLevelId().trim();
			String sdesc = serviceLevel.getServiceLevelDescription();
			String strRemarks =	serviceLevel.getRemarks();
            String guaranteeDelivery = serviceLevel.getGuaranteeDelivery();
			int smode= serviceLevel.getShipmentMode(); //"Air";	loginDetails.getShipmentMode();
			if (sid==null)
				{sid="";}
			if (sdesc==null)
				{sdesc="";}
			if (strRemarks==null)
				{strRemarks="";}
            if(guaranteeDelivery==null) 
              { guaranteeDelivery="N";}
        if(!serviceLevel.getFlag())
        {
           String sqlInsert = "INSERT	INTO FS_FR_SERVICELEVELMASTER (SERVICELEVELID, SERVICELEVELDESC ,REMARKS,SHIPMENTMODE,GUARANT_DELIV,TERMINALID,INVALIDATE)VALUES ('"+sid+"','"+sdesc+"','"+strRemarks+"','"+smode+"','"+guaranteeDelivery+"','"+loginDetails.getTerminalId()+"','F')";
           boolean flag=stmt.execute( sqlInsert );
        }
      if (serviceLevel.getOriginLocations()!=null)
         {
           EventIds =serviceLevel.getLocEventIds(); 
		   AllocatedTime=serviceLevel.getLocAllocatedTime();
           AlertTime=serviceLevel.getLocAlertTime();
           OriginLocations=serviceLevel.getOriginLocations();
           DestiLocations=serviceLevel.getDestiLocations();
           ptst=connection.prepareStatement("INSERT INTO FS_FR_LOC_TASK_PLAN (SERVICELEVELID,EVNT_ID,ORIG_LOC_ID,DEST_LOC_ID,ALLOC_TIME,ALERT_TIME) VALUES ('"+sid+"',?,?,?,?,?)");
           int eventIdsLen	=	EventIds.length;
           for(int i=0;i<eventIdsLen;i++)   
                {
               if(EventIds[i].length()>0)   
                 {
            	   int orgLocLen	=	OriginLocations.length;
            	   int destLocLen	=	DestiLocations.length;
                    for(int j=0;j<orgLocLen;j++)
                     {
                       for(int k=0;k<destLocLen;k++)
                       {
                        if (!isTaskExist(sid,Integer.parseInt(EventIds[i]),OriginLocations[j],DestiLocations[k],connection)) 
                         {
                             ptst.setInt(1,Integer.parseInt(EventIds[i]));
                             ptst.setString(2,OriginLocations[j]);
                             ptst.setString(3,DestiLocations[k]);
                             ptst.setInt(4,AllocatedTime[i]);
                             ptst.setInt(5,AlertTime[i]);
                             ptst.execute();
                          }
                         else
                           { throw new EJBException("ServiceLevel "+sid+" for Task Event"+Integer.parseInt(EventIds[i]) +" between "+OriginLocations[j]+" And "+DestiLocations[k]+" Already defined");}
                          
                       }//close  for loop k
                     }//close for loop j 
                  }//If close
                }// Close for outer for(i)
           } 
          
        if(serviceLevel.getEventIds()!=null)
            {
			   EventIds =serviceLevel.getEventIds(); 
		       AllocatedTime=serviceLevel.getAllocatedTime();
               AlertTime=serviceLevel.getAlertTime();
               //Added By RajKumari on 30-10-2008 for Connection Leakages.
               if(ptst!=null)
               { ptst.close();}
              ptst=connection.prepareStatement("INSERT INTO FS_FR_SRVC_LVL_TASK_PLAN (SERVICELEVELID,EVNT_ID,ALLOC_TIME,ALERT_TIME)VALUES (?,?,?,?)");
           //  int rowCount = 0;
              int evenIdsLen	=	EventIds.length;
               for(int i=0;i<evenIdsLen;i++)
                  {
                    if(EventIds[i].length()>0)
                      {
                        if (!isTaskExist(sid,Integer.parseInt(EventIds[i]),null,null,connection) )
                            {
                            ptst.setString(1,sid);
                            ptst.setInt(2,Integer.parseInt(EventIds[i]));
                            ptst.setInt(3,AllocatedTime[i]);
                            ptst.setInt(4,AlertTime[i]);
                            boolean rowCount = ptst.execute();
                           }
                         else  
                            {throw new EJBException("ServiceLevel "+sid+" for Task Event"+Integer.parseInt(EventIds[i]) +" Already defined");}
                        }
                  }
                 }
             
      
		  	operationsImpl.setTransactionDetails( loginDetails.getTerminalId(),
														loginDetails.getUserId(),
														"ServiceLevel",
														sid,
														loginDetails.getLocalTime(),
														"ADD");
		
		}
		catch (	SQLException sqle )
		{
			//Logger.error(FILE_NAME,"ETransHOSuperUserSessionBean : SQLException in setServiceLevelDetails() : "+sqle.toString());
      logger.error(FILE_NAME+"ETransHOSuperUserSessionBean : SQLException in setServiceLevelDetails() : "+sqle.toString());
			throw new EJBException("**"+"Operation Failed .Try Again.");
		}
    catch(EJBException ejbex)
    {  
    	//Logger.error(FILE_NAME,"ETransHOSuperUserSessionBean : EJBException in addServiceLevelDetails() : "+ejbex.toString());
      logger.error(FILE_NAME+"ETransHOSuperUserSessionBean : EJBException in addServiceLevelDetails() : "+ejbex.toString());
      throw new EJBException("**"+ejbex.getMessage());
    }
    catch(NumberFormatException nfe)
    {
      //Logger.error(FILE_NAME,"ETransHOSuperUserSessionBean : NumberFormatException in addServiceLevelDetails() : "+nfe.toString());
      logger.error(FILE_NAME+"ETransHOSuperUserSessionBean : NumberFormatException in addServiceLevelDetails() : "+nfe.toString());
    }
    
   
    finally
		{
			ConnectionUtil.closeConnection(connection,stmt);
			//Connection Leakage ----------------------- 7-DEC-2004 ---------------- Santhosam.P
			ConnectionUtil.closeConnection(connection, ptst);
		}
    	return true;
	}//	end	of addServiceLevelDetails


	/**
   * 
   * @param serviceLevel
   * @param loginDetails
   * @param shipmentMode
   * @return 
   */
	public boolean updateServiceLevelDetails(ServiceLevelJspBean serviceLevel,ESupplyGlobalParameters loginDetails,int shipmentMode)
	{
		Connection connection =	null;
		PreparedStatement stmt = null;
		try
		{
			connection 	 =	 getConnection();
			String sid   =   serviceLevel.getServiceLevelId().trim();
			String sdesc =	 serviceLevel.getServiceLevelDescription();
			int  smode	 =	serviceLevel.getShipmentMode();
			String strRemarks =	serviceLevel.getRemarks();
            String guaranteeDelivery = serviceLevel.getGuaranteeDelivery();
			if (sdesc==null)  {sdesc="";}
			if (strRemarks==null)  {strRemarks="";}
            if (guaranteeDelivery == null) {guaranteeDelivery = "N";}
			String sqlUpdate = " UPDATE	FS_FR_SERVICELEVELMASTER SET SERVICELEVELDESC =?,REMARKS =?,SHIPMENTMODE =?,GUARANT_DELIV=? WHERE SERVICELEVELID=?";
            stmt 		 = 	 connection.prepareStatement(sqlUpdate);
 		    stmt.setString(1,sdesc);
		    stmt.setString(2,strRemarks);
		    stmt.setInt(3,smode);
            stmt.setString(4,guaranteeDelivery);
		    stmt.setString(5,sid);
				int i = stmt.executeUpdate();
		    deleteEvntDetails(sid,connection);
		   boolean flag= addServiceLevelDetails(serviceLevel,loginDetails);
		 		operationsImpl.setTransactionDetails(loginDetails.getTerminalId(),
													 loginDetails.getUserId(),
													 "ServiceLevel",
													  sid,
													 loginDetails.getLocalTime(),
													  "MODIFY");
				return true;
		}
		catch (	SQLException sqle )
		{
			//Logger.error(FILE_NAME,"ETransHOSuperUserSetupSessionBean	: SQLException in updateServiceLevelDetails() :	"+sqle.toString());
      logger.error(FILE_NAME+"ETransHOSuperUserSetupSessionBean	: SQLException in updateServiceLevelDetails() :	"+sqle.toString());
        
			
			return false;
		}
		catch( Exception cnfe )
		{
			throw new javax.ejb.EJBException(cnfe.getMessage());
		}
		finally
		{
			try
			{
				if( stmt != null )
					{stmt.close();}
				if(connection !=	null )
					{connection.close();}
			}
			catch(SQLException sqle)
			{
				//Logger.error(FILE_NAME,"ETransHOSuperUserSetupSessionBean : SQLException in finally block of updateServiceLevelDetails() :	"+sqle.toString());
        logger.error(FILE_NAME+"ETransHOSuperUserSetupSessionBean : SQLException in finally block of updateServiceLevelDetails() :	"+sqle.toString());
			}
		}
	}

	/**
   * 
   * @param serviceLevelId
   * @param loginDetails
   * @param shipmentMode
   * @return 
   */
public boolean deleteServiceLevelDetails(String	serviceLevelId,	ESupplyGlobalParameters	loginDetails,int shipmentMode )
	{
		Connection connection =	null;
		Statement stmt 		  = null;
		Statement stmt1		  = null;
		ResultSet rs 		  = null;
		try
		{
			connection 	=	getConnection();
			stmt 		=	connection.createStatement();
			stmt1 		=	connection.createStatement();
			String sqlQuery	= "SELECT *	FROM FS_FR_SERVICELEVELMASTER WHERE	SERVICELEVELId='"+serviceLevelId.trim()+"'  ";
			rs	= stmt1.executeQuery(sqlQuery);
			if(rs.next())
			{
                deleteEvntDetails(serviceLevelId.trim(),connection);
				String sqlDelete = " DELETE	FROM FS_FR_SERVICELEVELMASTER WHERE	SERVICELEVELId='"+serviceLevelId.trim()+"' AND SHIPMENTMODE = "+shipmentMode+" ";
				stmt.executeUpdate(sqlDelete);
 				 operationsImpl.setTransactionDetails(	loginDetails.getTerminalId(),
														loginDetails.getUserId(),
														"ServiceLevel",
														serviceLevelId,
														loginDetails.getLocalTime(),
														"DELETE");
			}
			else
			{
				error =	"RECORD	IS NOT PRESENT ";
				return false;
			}
			return true;
		}
		catch (	SQLException sqle )
		{
			//Logger.error(FILE_NAME,"ETransHOSuperUserSetupSessionBean	: SQLException in deleteServiceLevelDetails() :	"+sqle.toString());
      logger.error(FILE_NAME+"ETransHOSuperUserSetupSessionBean	: SQLException in deleteServiceLevelDetails() :	"+sqle.toString());
			error =	sqle.toString();
			
			return false;
		}
		catch( Exception cnfe )
		{
			throw new javax.ejb.EJBException("EJBException	in deleteServiceLevelDetails of	ETransHOSuperUserSetupSessionBean	"+cnfe.toString());
		}
		finally
		{
			ConnectionUtil.closeConnection(connection,stmt,rs);
			ConnectionUtil.closeConnection(connection,stmt1,rs);
		}
	}// End of ETransHOSuper ServiceLevel Methods

	/**
   * 
   * @param serviceId
   * @return 
   */
	public ServiceLevelJspBean getServiceLevelDetails(String serviceId)
	{
		Connection connection =	null;
		Statement stmt  =  null;
        Statement stmt1 = null;
		ResultSet rs  =  null;
        ResultSet rs1 = null;
		ServiceLevelJspBean	serviceLevel = null;
        String[] originLocationIds = null;
        String[] destLocationIds = null;
        String[] eventIds = null;
        String[] eventDesc=null;
        int[]    allocatedTime = null;
        int[] alertTime = null;
    	    
		try
		{
			String sqlQuery =	" SELECT * FROM	FS_FR_SERVICELEVELMASTER WHERE SERVICELEVELID =	'"+serviceId.trim()+"' AND (INVALIDATE='F' OR INVALIDATE IS NULL)";
			connection = getConnection();
			stmt = connection.createStatement();
      stmt1=connection.createStatement();
			rs = stmt.executeQuery(	sqlQuery );
		if(	rs.next())
			{
				String serviceLevelId 		=	rs.getString(1);
				String serviceDescription   =	rs.	getString(2);
				String remarks	 			=	rs.getString(3);
				int shipmentmode 			=	rs.getInt(4);
		        String guaranteeDelivery    =   rs.getString(5);
				if (serviceLevelId==null) { serviceLevelId="";}
				if (serviceDescription==null)  {serviceDescription="";}
				if (remarks==null){	remarks="";}
				serviceLevel = new ServiceLevelJspBean();
				serviceLevel.setServiceLevelId(serviceLevelId);
				serviceLevel.setServiceLevelDescription(serviceDescription);
				serviceLevel.setRemarks(remarks	);
     		    serviceLevel.setShipmentMode(shipmentmode);
                serviceLevel.setGuaranteeDelivery(guaranteeDelivery);
               // sqlQuery="SELECT DISTINCT(FS_FR_LOC_TASK_PLAN.EVNT_ID),ALLOC_TIME,ALERT_TIME,EVNT_DESC FROM   FS_FR_LOC_TASK_PLAN,FS_FR_EVNT WHERE SERVICELEVELID='"+serviceLevelId+"' AND FS_FR_LOC_TASK_PLAN.EVNT_ID= FS_FR_EVNT.EVNT_ID";
                sqlQuery="SELECT DISTINCT(FS_FR_LOC_TASK_PLAN.EVNT_ID),ALLOC_TIME,ALERT_TIME,EVNT_DESC FROM   FS_FR_LOC_TASK_PLAN,FS_FR_EVNT WHERE SERVICELEVELID='"+serviceLevelId+"' AND FS_FR_LOC_TASK_PLAN.EVNT_ID= FS_FR_EVNT.EVNT_ID";
                rs1=stmt1.executeQuery(sqlQuery);
				int count=0;
				while(rs1.next())
					  {count++;}
				 if (count>0)     
					 {
						   rs1.close();
						   allocatedTime=new int[count];
						   alertTime=new int[count];
						   eventIds=new String[count];
						   eventDesc=new String[count];
						   
						   rs1=stmt1.executeQuery(sqlQuery);
						   count=0;
						   while(rs1.next())
						   {
							 eventIds[count]=""+rs1.getInt(1)+"";
							 allocatedTime[count]=rs1.getInt(2);
							 alertTime[count]=rs1.getInt(3);
							 eventDesc[count]=rs1.getString(4);
							 count++;
						   }
						   count=0;
						   rs1.close();
						   sqlQuery="SELECT DISTINCT ORIG_LOC_ID FROM FS_FR_LOC_TASK_PLAN WHERE SERVICELEVELID='"+serviceLevelId+"' AND  EVNT_ID="+Integer.parseInt(eventIds[0]);
						   rs1=stmt1.executeQuery(sqlQuery);
						  while(rs1.next())
						  {
							count++;
						  }
						  originLocationIds=new String[count];
						  rs1.close();
						  rs1=stmt1.executeQuery(sqlQuery);
						  count=0;
						  while(rs1.next())
							{
							  originLocationIds[count]=rs1.getString(1);
							  count++;
							}
						  rs1.close();
						  sqlQuery="SELECT DISTINCT DEST_LOC_ID FROM FS_FR_LOC_TASK_PLAN WHERE SERVICELEVELID='"+serviceLevelId+"' AND EVNT_ID="+Integer.parseInt(eventIds[0]);
						  rs1=stmt1.executeQuery(sqlQuery);
						  count=0;
						  while(rs1.next())
							{
							  count++;
							}
						   rs1.close();
						   destLocationIds=new String[count];
						   rs1=stmt1.executeQuery(sqlQuery);
						   count=0;    
						   while(rs1.next())
							 {
							 destLocationIds[count]=rs1.getString(1);
							 count++;
							 } 
						   rs1.close();  
				   serviceLevel.setLocEventIds(eventIds);
				   serviceLevel.setLocAllocatedTime(allocatedTime);
				   serviceLevel.setLocAlertTime(alertTime);
				   serviceLevel.setLocEventDesc(eventDesc);
				   serviceLevel.setOriginLocations(originLocationIds);
				   serviceLevel.setDestiLocations(destLocationIds);
          }
      sqlQuery="SELECT DISTINCT(FS_FR_SRVC_LVL_TASK_PLAN.EVNT_ID),ALLOC_TIME,ALERT_TIME,EVNT_DESC FROM   FS_FR_SRVC_LVL_TASK_PLAN,FS_FR_EVNT  WHERE SERVICELEVELID='"+serviceLevelId+"' AND FS_FR_SRVC_LVL_TASK_PLAN.EVNT_ID= FS_FR_EVNT.EVNT_ID";
           rs1=stmt1.executeQuery(sqlQuery); 
           count=0;
           while(rs1.next())
             {count++;}
           if(count>0)
              {
                rs1.close();
                rs1=stmt1.executeQuery(sqlQuery); 
                allocatedTime=new int[count];
                alertTime=new int[count];
                eventIds=new String[count];     
                eventDesc=new String[count];
                count=0;
                while(rs1.next())
                   {
                     eventIds[count]=""+rs1.getInt(1)+"";
                     allocatedTime[count]=rs1.getInt(2);
                     alertTime[count]=rs1.getInt(3);        
                     eventDesc[count]=rs1.getString(4);
                     count++;
                   }
                    serviceLevel.setAllocatedTime(allocatedTime);
                    serviceLevel.setAlertTime(alertTime);
                    serviceLevel.setEventIds(eventIds);
                    serviceLevel.setEventDesc(eventDesc);

                }
        
                  
        }
     else
				{return null;}
        
          
    
    }
		catch( SQLException	sqle)
		{
			
		  //Logger.error(FILE_NAME,"ETransHOSuperUserSetupSessionBean :	SQLException in	getServiceLevelDetails() : "+sqle.toString());
      logger.error(FILE_NAME+"ETransHOSuperUserSetupSessionBean :	SQLException in	getServiceLevelDetails() : "+sqle.toString());
      throw new EJBException(sqle.toString());
		}
		finally
		{
        ConnectionUtil.closeConnection(connection, stmt, rs);
        ConnectionUtil.closeConnection(connection, stmt1, rs1);
			//closeConnection(rs,stmt,connection);
			//Connection Leakage ------------- 7-DEC-2004 ---------------- Santhosam.P
		//	closeConnection(rs1,stmt1,connection);
		}
		return serviceLevel;
	} // End of	getServiceLevelDetails

	/**
   *This Method is written insted of getServiceLevelIds(String whereclause)
   *in Order to consider Two Parameters. and avoid query in JSP'S *
   *@param searchString
   *@param shipmentMode
   *@return 
   */

	

 public ArrayList getServiceLevelIds(String searchString,String shipmentMode,String terminalId,String operation)
    {
		Connection	connection	= null;
       // Statement	stmt 		= null;//Commented By RajKumari on 30-10-2008 for Connection Leakages.
        ResultSet	rs   		= null;
		String		whereclause = null;
		CallableStatement cstmt = null;

		// @@ Added by G.Srinivas for TogetherJ on 20050105
		//StringBuffer   servId1           = new StringBuffer();
		//StringBuffer   sMode1            = new StringBuffer();
		// @@

		if(searchString==null){
			searchString= "";
		}
		
		if(shipmentMode!=null){			
				
				if(shipmentMode.equals("4")){
					whereclause = "WHERE SERVICELEVELID LIKE '"+searchString+"%' AND SHIPMENTMODE IN (4,5,6,7)";
				}else 	if(shipmentMode.equals("1")){
					whereclause = "WHERE SERVICELEVELID LIKE '"+searchString+"%' AND SHIPMENTMODE IN (1,3,5,7) ";
				}else 	if(shipmentMode.equals("All")){
					whereclause = "WHERE SERVICELEVELID LIKE '"+searchString+"%'  ";
				}else{
					whereclause = "WHERE SERVICELEVELID LIKE '"+searchString+"%' AND SHIPMENTMODE IN (2,3,6,7) ";
				}
		
		}
		
	/*	if(searchString!=null){
			whereclause = " WHERE SERVICELEVELID LIKE '"+searchString+"%' AND SHIPMENTMODE IN (1,3,5,7)";
		}else{
			whereclause = " WHERE SERVICELEVELID LIKE '%' AND SHIPMENTMODE IN (1,3,5,7)";
		} */

		//WHERE SERVICELEVELID LIKE '"+searchString+"' AND SHIPMENTMODE IN (1,3,5,7)


		String sMode = "";	
		//String sql= "SELECT SERVICELEVELID,SERVICELEVELDESC,SHIPMENTMODE FROM FS_FR_SERVICELEVELMASTER "+whereclause+" ORDER BY SERVICELEVELID";
		ArrayList serviceLevelIds = new ArrayList();

		try
        {
			connection = this.getConnection();
			//stmt = connection.createStatement();
			//rs = stmt.executeQuery(sql);
		cstmt        = connection.prepareCall("{ ?= call QMS_SETUPS.GET_SERVICELEVEL(?,?,?,?)}");
      cstmt.registerOutParameter(1,OracleTypes.CURSOR);
      cstmt.setString(2,terminalId);
      cstmt.setString(3,shipmentMode);
      cstmt.setString(4,(searchString+"%"));
      cstmt.setString(5,operation);
      cstmt.execute();
      rs  =  (ResultSet)cstmt.getObject(1);
			//connection = this.getConnection();
			//stmt = connection.createStatement();
			//rs = stmt.executeQuery(sql);

			while ( rs.next() )
			{
				// @@ Added by G.Srinivas for TogetherJ Fix on 20050105
		StringBuffer   servId1           = new StringBuffer();
		StringBuffer   sMode1            = new StringBuffer();
		// @@

				String servId = rs.getString(1);
				String name = rs.getString(2);
				if (name==null)
				{name="";}
					servId1.append(servId);
				  servId1.append(" ");
				  servId = servId1.toString();				
		/*		if(servId.length() == 4)	
				//{servId = servId+" ";	}
				// @@ Replaced by G.Srinivas for TogetherJ on 20050105
                 {servId1.append(servId);
				  servId1.append(" ");
				  servId = servId1.toString();}
				// @@
				
				if(servId.length() == 3)
				//{servId = servId+"   ";}		
				// @@ Replaced by G.Srinivas for TogetherJ on 20050105
                 {servId1.append(servId);
				  servId1.append(" ");
				  servId = servId1.toString();}
				// @@
			
				if(servId.length() == 2)	
				//{servId = servId+"    ";}	
				// @@ Replaced by G.Srinivas for TogetherJ on 20050105
                 {servId1.append(servId);
				  servId1.append(" ");
				  servId = servId1.toString();}
				// @@
			
				if(servId.length() == 1)
				//{servId = servId+"     ";}	
				// @@ Replaced by G.Srinivas for TogetherJ on 20050105
                 {servId1.append(servId);
				  servId1.append(" ");
				  servId = servId1.toString();}
				// @@*/
			
				sMode = (new String()).valueOf(rs.getInt(3));
				 
				if(sMode!=null)
				{ 
					if( sMode.equals("7"))   
					{sMode="AST";}
					if(sMode.equals("1"))
					{sMode="A  ";}
					if(sMode.equals("2"))
					{sMode=" S ";}		
					if(sMode.equals("3"))
					{sMode="AS ";}
					if(sMode.equals("4"))
					{sMode="  T";}
					if(sMode.equals("5"))
					{sMode="A T";}
					if(sMode.equals("6"))
					{sMode=" ST";}
				}	
				
				//sMode = servId+"[" +sMode+ "]"+"[" +name+ "]";
				// @@ Replaced by G.Srinivas for TogetherJ on 20050105
				  sMode1.append(servId);
				  sMode1.append("[" );
				  sMode1.append(sMode);
				  sMode1.append( "]");
				  sMode1.append("[" );
				  sMode1.append(name);
				  sMode1.append( "]");
				  sMode = sMode1.toString();
                // @@

				serviceLevelIds.add(sMode);
			}
		}
		catch(SQLException sqEx)
        {sqEx.printStackTrace();
			//Logger.error(FILE_NAME, "[getServiceLevelIds(whereclause)] -> "+sqEx.toString());
      logger.error(FILE_NAME+ "[getServiceLevelIds(whereclause)] -> "+sqEx.toString());
            throw new EJBException(sqEx.toString());
        }
		catch(Exception e)
        {
          e.printStackTrace();
           throw new EJBException(e.toString());
        }
        finally
        {
			ConnectionUtil.closeConnection(connection, cstmt, rs);
        }
        return serviceLevelIds;
    }
    
    
    
public ArrayList getServiceLevelIds1(String searchString,String shipmentMode,String terminalId,String operation)
{
        Connection	      connection	= null;
        //Statement	        stmt 		    = null;//Commeneted By RajKumari on 30-10-2008 for Connection Leakages.
        ResultSet	        rs   		    = null;
        String		        whereclause = null;
        CallableStatement cstmt       = null;
        String            sMode       = "";
        String            serviceLevelId ="";
        if(searchString==null){
          searchString= "";
        }
        ArrayList serviceLevelIds = new ArrayList();

		try
      {
      
      if(searchString!=null)
        serviceLevelId =searchString.replaceAll(",","','");
			connection = this.getConnection();			
      cstmt        = connection.prepareCall("{ ?= call QMS_SETUPS.GET_SERVICELEVEL_buyrate(?,?,?,?)}");
      cstmt.registerOutParameter(1,OracleTypes.CURSOR);
      cstmt.setString(2,terminalId);
      cstmt.setString(3,shipmentMode);
      cstmt.setString(4,serviceLevelId);
      cstmt.setString(5,operation);
      cstmt.execute();
      rs  =  (ResultSet)cstmt.getObject(1);
			
			while ( rs.next() )
			{
				
          StringBuffer   servId1           = new StringBuffer();
          StringBuffer   sMode1            = new StringBuffer();
          String servId = rs.getString(1);
          String name = rs.getString(2);
          if (name==null)
          {
              name="";}
              servId1.append(servId);
              servId1.append(" ");
              servId = servId1.toString();			
              sMode = (new String()).valueOf(rs.getInt(3));
				 
              if(sMode!=null)
              { 
                if( sMode.equals("7"))   
                {sMode="AST";}
                if(sMode.equals("1"))
                {sMode="A  ";}
                if(sMode.equals("2"))
                {sMode=" S ";}		
                if(sMode.equals("3"))
                {sMode="AS ";}
                if(sMode.equals("4"))
                {sMode="  T";}
                if(sMode.equals("5"))
                {sMode="A T";}
                if(sMode.equals("6"))
                {sMode=" ST";}
              }
              sMode1.append(servId);
              sMode1.append("[" );
              sMode1.append(sMode);
              sMode1.append( "]");
              sMode1.append("[" );
              sMode1.append(name);
              sMode1.append( "]");
              sMode = sMode1.toString();
              serviceLevelIds.add(sMode);
		  }
		}
		catch(SQLException sqEx)
        {sqEx.printStackTrace();
			//Logger.error(FILE_NAME+ "[getServiceLevelIds(whereclause)] -> "+sqEx.toString());
      logger.error(FILE_NAME+ "[getServiceLevelIds(whereclause)] -> "+sqEx.toString());
            throw new EJBException(sqEx.toString());
        }
		catch(Exception e)
        {
          e.printStackTrace();
           throw new EJBException(e.toString());
        }
        finally
        {
			ConnectionUtil.closeConnection(connection, cstmt, rs);
        }
        return serviceLevelIds;
    }

	/**
	*The below method is used to get the Country details for report
    *This method  returns a Vector
	*@return Vector records	 
	*/
public java.util.Vector getCountryDetails()
{
	Connection connection = null;
	Statement stmt        = null;
	ResultSet rs          = null;
	String sl           	= 	null;
	Vector records = new Vector();
	try
	{
		connection = getConnection();
		stmt       =connection.createStatement();
		rs = stmt.executeQuery("select COUNTRYID,COUNTRYNAME,CURRENCYID, REGION from FS_COUNTRYMASTER ORDER BY COUNTRYID");
		while(rs.next())
		{
			records.addElement(rs.getString(1)+"?"+rs.getString(2)+"?"+rs.getString(3)+"?"+rs.getString(4));
		}
	}
		catch(SQLException sqEx)
        {
			//Logger.error(FILE_NAME, "[getCountryDetails()] -> "+sqEx.toString());
      logger.error(FILE_NAME+ "[getCountryDetails()] -> "+sqEx.toString());
            throw new EJBException(sqEx.toString());
        }
        finally
        {
			ConnectionUtil.closeConnection(connection, stmt, rs);
        }
	return records;
}// end of getCountryDetails method

/**
	*The below method is used to get the CompanyRegistrationInformation
    *This method returns a Vector
	*@return Vector records	 
	*/
public java.util.Vector getCompanyInfo() 
{
	//Initiliazing all required variables
	Connection   							         connection     =	 null;
	Statement    							         stmt           =	 null;
	ResultSet    							         rs           	 =	 null;
	String 									         sql			 =	 null;
	com.foursoft.etrans.setup.company.bean.HORegistrationJspBean   HOObj  		 =	 null;
	Vector 									         records        =   new Vector(); 
	try
	{
		connection 	 	=	 this.getConnection();
		stmt   		 	=	 connection.createStatement();
		rs				=	 stmt.executeQuery("SELECT C.COMPANYID CompanyId,C.COMPANYNAME CompanyName,C.HCURRENCY Currency,C.DAYLIGHTSAVING DLightSaving, A.CITY City "+
												"FROM FS_COMPANYINFO C , FS_ADDRESS A WHERE C.COMPANYADDRESSID = A.ADDRESSID AND AGENTJVIND='C' ORDER BY C.COMPANYID");
		while(rs.next())
		{
			HOObj	= 	new com.foursoft.etrans.setup.company.bean.HORegistrationJspBean();
			HOObj.setCompanyId(rs.getString("CompanyId"));
			HOObj.setCompanyName(rs.getString("CompanyName"));
//			HOObj.setDateFormat(rs.getString("DFormat"));
//			HOObj.setTimeZone(rs.getString("TZ"));
			HOObj.setHCurrency(rs.getString("Currency"));
			HOObj.setDayLightSavings(rs.getString("DLightSaving"));
			HOObj.setCity(rs.getString("City"));
			records.addElement(HOObj);
		}
	}
		catch(SQLException sqEx)
        {
			//Logger.error(FILE_NAME, "[getCompanyInfo()] -> "+sqEx.toString());
      logger.error(FILE_NAME+ "[getCompanyInfo()] -> "+sqEx.toString());
            throw new EJBException(sqEx.toString());
        }
        finally
        {
			ConnectionUtil.closeConnection(connection, stmt, rs);
        }
	return records;
}

/**
	*The below method is used to get the COMPANYNAME,PERCENTOFEQUITY, SALESTAXREGNO, DESIGNATION
    *This method returns a vector
	*@return Vector vec_AgentJV	 
	*/
public Vector getAgentJVDetails() 
{
    JointVentureJspBean	AJVObj				= null;
	Vector vec_AgentJV = new Vector();
	Connection  connection = null;
	Statement stmt     = null;
	ResultSet rs       = null;
	
	try
	{
		connection = getConnection();
		stmt = connection.createStatement();
	    String sqlQuery = "SELECT A.COMPANYID companyid,A.COMPANYNAME companyname,A.PERCENTOFEQUITY percentofequity,A.AGENTJVIND agvid,A.SALESTAXREGNO saltaxgegno,A.DESIGNATION designation,"+
		"A.LOCALCURRENCY currency,A.CONTACTNAME contactper,C.CITY city FROM FS_COMPANYINFO A,FS_ADDRESS C WHERE A.COMPANYADDRESSID=C.ADDRESSID AND A.AGENTJVIND IN('J','A') ORDER BY A.COMPANYID";
		rs = stmt.executeQuery(sqlQuery);
		while ( rs.next() )
		{
			AJVObj	= 	new com.foursoft.etrans.setup.jointventure.bean.JointVentureJspBean();
			AJVObj.setCompanyId(rs.getString("companyid")==null? "Not Available" : rs.getString("companyid"));
			AJVObj.setCompanyName(rs.getString("companyname")==null? "Not Available" : rs.getString("companyname"));
			AJVObj.setPercentOfEquity(rs.getInt("percentofequity"));
			AJVObj.setAgentJV(rs.getString("agvid")==null? "Not Available" : rs.getString("agvid"));
			AJVObj.setSalesTaxRegNo(rs.getString("saltaxgegno")==null? "Not Available" : rs.getString("saltaxgegno"));
			AJVObj.setDesignation(rs.getString("designation")==null? "Not Available" : rs.getString("designation"));
			AJVObj.setLocalCurrency(rs.getString("currency")==null? "Not Available" : rs.getString("currency") );
			AJVObj.setContactPerson(rs.getString("contactper")==null? "Not Available" : rs.getString("contactper"));
			AJVObj.setCity(rs.getString("city"));
			vec_AgentJV.addElement(AJVObj);
		}
	}
		catch(SQLException sqEx)
        {
			//Logger.error(FILE_NAME, "[getAgentJVDetails()] -> "+sqEx.toString());
      logger.error(FILE_NAME+ "[getAgentJVDetails()] -> "+sqEx.toString());
            throw new EJBException(sqEx.toString());
        }
        finally
        {
			ConnectionUtil.closeConnection(connection, stmt, rs);
        }
	return vec_AgentJV;
} // end of getAgentJVDetails

/**
	*The below method is used to get the Charge details
    *This method returns a vector
	*@return Vector records	 
	*/
public java.util.Vector getChargeDetails()
{
	Connection   							         connection     =	 null;
	Statement    							         stmt           =	 null;
	ResultSet    							         rs           	=	 null;
	String 									         sql			=	 null;
	com.foursoft.etrans.air.charges.bean.ChargeMasterJSPBean     ChargeObj  	=	 null;
	Vector 									         records        =   new Vector();

	try
	{
		connection     =this.getConnection();
		stmt   		 	=	 connection.createStatement();
		rs				=	 stmt.executeQuery("SELECT CHARGEID,CHARGEDESC,COSTINCURREDAT  FROM FS_FR_CHARGESMASTER ORDER BY CHARGEID ");
		while(rs.next())
		{
			records.addElement(rs.getInt("CHARGEID")+"?"+rs.getString("CHARGEDESC")+"?"+rs.getString("COSTINCURREDAT"));
		}
	}
		catch(SQLException sqEx)
        {
			//Logger.error(FILE_NAME+ "[getChargeDetails()] -> "+sqEx.toString());
      logger.error(FILE_NAME+ "[getChargeDetails()] -> "+sqEx.toString());
            throw new EJBException(sqEx.toString());
        }
        finally
        {
			ConnectionUtil.closeConnection(connection, stmt, rs);
        }
	return records;
}

/**
	*The below method is used to get the SERVICELEVELID,SERVICELEVELDESC,REMARKS
    *This method takes  sMode as argument and returns a vector
	*@param String sMode
	*@return Vector records	 
	*/
public java.util.Vector getServiceLevelDetails(String sMode,String str) 
{
	if(sMode.equalsIgnoreCase("Air"))
	  {sMode="(1,3,5,7)";}
	else if(sMode.equalsIgnoreCase("Sea"))
	  {sMode="(2,3,6,7)";}
	else if(sMode.equalsIgnoreCase("Surface"))
	  {sMode="(4,5,6,7)";}
  else if(sMode.equalsIgnoreCase("ALL"))
	  {sMode="(1,2,3,4,5,6,7)";}
	CallableStatement  cstmt = null;

	String sql1="SELECT SERVICELEVELID,SERVICELEVELDESC,REMARKS  FROM FS_FR_SERVICELEVELMASTER WHERE SHIPMENTMODE IN "+sMode+" ORDER BY  SERVICELEVELID";

	Connection connection = null;
	Statement stmt        = null;
	ResultSet rs          = null;
	Vector records = new Vector();
	try
	{
		connection     =this.getConnection();
		stmt       =connection.createStatement();
		rs         =stmt.executeQuery(sql1);

		while(rs.next())
		{
			String strserviceDesc = rs.getString("SERVICELEVELDESC");
			if(strserviceDesc == null)
			{strserviceDesc="Not Available";}
			String strRemarks = rs.getString("REMARKS");
			if(strRemarks == null)
			{strRemarks="Not Available";}
			records.addElement(rs.getString("SERVICELEVELID")+"?"+strserviceDesc+"?"+strRemarks+"?");
		}
	}
		catch(SQLException sqEx)
        {
			//Logger.error(FILE_NAME, "[getServiceLevelDetails(String sMode) ] -> "+sqEx.toString());
      logger.error(FILE_NAME+ "[getServiceLevelDetails(String sMode) ] -> "+sqEx.toString());
            throw new EJBException(sqEx.toString());
        }
        finally
        {
			ConnectionUtil.closeConnection(connection, cstmt, rs);
      ConnectionUtil.closeStatement(stmt);//Added By RajKumari on 30-10-2008 for Connection Leakages.
        }
	return records;
}//end of serviceLevelDetails() method.

/**
	*The below method is used to get the COMODITYID,COMODITYDESCRIPTION,HANDLINGINFO,COMODITYTYPE
    *This method returns a vector
	*@return Vector records	 
	*/
//public java.util.Vector getViewCommodityReports()
public java.util.ArrayList getViewCommodityReports(int noOfRecs,int pageNo,String sortBy,String sortOrder)
{
	Connection connection = null;
	Statement stmt        = null;
	ResultSet rs          = null;
	ResultSet rs1          = null;//shyam
	com.foursoft.etrans.setup.commodity.bean.CommodityJspBean commodityjspbean = null;
	Vector records = new Vector();
	String handlingInfo = " ";

	String orderClause	  = "";//Shyam
	String row_Query	  = "";//Shyam
	int		noPages		  = 0 ;//Shyam
	int		no_of_recs		  = 0 ;//Shyam
	int		noOfrows = 0;

	ArrayList	mainList	= new ArrayList();//shyam

	try
	{
		
		connection = getConnection();
		stmt       =connection.createStatement();
		
		//shyam starts here
		row_Query = ")T1  WHERE   ROWNUM <= (("+pageNo+"- 1) * "+noOfRecs+") + "+noOfRecs+") WHERE   RN > (("+pageNo+"- 1) * "+noOfRecs+")";
		
		if(sortBy.equalsIgnoreCase("COMODITYID"))
			orderClause ="ORDER BY COMODITYID "+sortOrder;
		else if(sortBy.equalsIgnoreCase("COMODITYDESCRIPTION"))
			orderClause ="ORDER BY COMODITYDESCRIPTION "+sortOrder;
		else if(sortBy.equalsIgnoreCase("HANDLINGINFO"))
			orderClause ="ORDER BY HANDLINGINFO "+sortOrder;
		else if(sortBy.equalsIgnoreCase("COMODITYTYPE"))
			orderClause ="ORDER BY COMODITYTYPE "+sortOrder;

		String str = "";
		str	=	"SELECT COUNT(*) NO_ROWS FROM FS_FR_COMODITYMASTER";
		rs1	=	stmt.executeQuery(str);
		
		if(rs1.next())
		{
			noOfrows=rs1.getInt("NO_ROWS");
		}

		noPages = noOfrows/noOfRecs; 
			
			int extraPages=noOfrows%noOfRecs;

			if(extraPages>0)
				noPages++;

			if(noPages==0) 
				noPages = 1;

			//int extraRecs = noOfrows%noOfRecs;

			if(noPages==pageNo)
				no_of_recs = noOfrows%noOfRecs;
			else
				no_of_recs = noOfRecs;

			/*if(extraRecs==0)
				no_of_recs=noOfRecs;
			else
				no_of_recs=extraRecs;*/


		//shyam ends here

		str= "SELECT * FROM (SELECT T1.*, ROWNUM RN FROM (SELECT  COMODITYID,COMODITYDESCRIPTION,HANDLINGINFO,COMODITYTYPE FROM FS_FR_COMODITYMASTER "+ orderClause + row_Query;  //shyam
		
		//rs         =stmt.executeQuery("SELECT * FROM (SELECT T1.*, ROWNUM RN FROM (SELECT  COMODITYID,COMODITYDESCRIPTION,HANDLINGINFO,COMODITYTYPE FROM FS_FR_COMODITYMASTER ORDER BY COMODITYID");
		rs         =stmt.executeQuery(str);//Shyam
		
		while(rs.next())
		{
			
			String strDesc = rs.getString("COMODITYDESCRIPTION");
			if(strDesc == null)
			{strDesc="Not Available";}
				
			handlingInfo  = rs.getString("HANDLINGINFO");
			
			if(handlingInfo == null)
			{handlingInfo = " ";}
				
			//Logger.error(FILE_NAME, "HandlingInfo..."+handlingInfo);	
				
			records.addElement(rs.getString("COMODITYID")+"?"+strDesc+"?"+handlingInfo+"?"+rs.getString("COMODITYTYPE"));
		}
		
		mainList.add(new Integer(no_of_recs));//shyam
		mainList.add(new Integer(noPages));//shyam
		mainList.add(records);//shyam
		
	}
		catch(SQLException sqEx)
        {
			//Logger.error(FILE_NAME, "[getViewCommodityReports() ] -> "+sqEx.toString());
      logger.error(FILE_NAME+ "[getViewCommodityReports() ] -> "+sqEx.toString());
            throw new EJBException(sqEx.toString());
        }
        finally
        {
			ConnectionUtil.closeConnection(connection, stmt, rs);
			ConnectionUtil.closeConnection(null, null, rs1);//shyam

        }
	//return records;
	return mainList;
} //end of getViewCommodityReports() method.


/**
	*The below method is used to get the Tax details
    *This method takes  terminalId as argument and returns a Vector
	*@param String terminalId
	*@return Vector records	 
	*/
public java.util.Vector getTaxDetails(String terminalId) 
{
	Connection   							         connection     =	 null;
	Statement    							         stmt           =	 null;
	ResultSet    							         rs           	=	 null;
	String 									         sql			=	 null;
	com.foursoft.etrans.setup.taxes.bean.TaxMaster               TaxObj  	    =	 null;
	Vector 									         records        =    new Vector();
	String											 ho_terminalId	=	"";			
	try
	{
		connection 	 	=	 this.getConnection();
		
			String  	q1	= " SELECT TERMINALID FROM FS_FR_TERMINALMASTER WHERE OPER_ADMIN_FLAG ='H' " ;
			stmt	   		= connection.createStatement();
			rs  			= stmt.executeQuery(q1);
			while(rs.next())
			{
				ho_terminalId = rs.getString(1);
			}	
			rs		=	null;
      //Added By RajKumari on 30-10-2008 for Connection Leakages.
      if(stmt!=null)
      {
        stmt.close();
        stmt	=	null;
      }
			

			stmt   		 	=	 connection.createStatement();
			rs				=	 stmt.executeQuery(" SELECT TAXID,TAXDESC,REMARKS FROM FS_FR_TAXMASTER WHERE TRML_ID ='"+terminalId+"' "+ 
											   " UNION   "+
											   " SELECT TAXID,TAXDESC,REMARKS FROM FS_FR_TAXMASTER WHERE TRML_ID='"+ho_terminalId+"'  ORDER BY TAXID ");
		while(rs.next())
		{
			String strRemarks = rs.getString(3);
			if(strRemarks == null)
			{strRemarks="-";}
			records.addElement(rs.getString("TAXID")+"?"+rs.getString("TAXDESC")+"?"+strRemarks);
		}

	}
		catch(SQLException sqEx)
        {
			//Logger.error(FILE_NAME, "[getTaxDetails()] -> "+sqEx.toString());
      logger.error(FILE_NAME+ "[getTaxDetails()] -> "+sqEx.toString());
            throw new EJBException(sqEx.toString());
        }
        finally
        {
			ConnectionUtil.closeConnection(connection, stmt, rs);
        }
	return records;
} //end of getTaxDetails method

/**
	*The below method is used to get the location details
    *This method takes  values, count as arguments and returns a double dimensional array
	*@param String values,int count
	*@return String[][] records	 
	*/

public String[][] getLocationMaster(String values,int count)
{
	Connection  connection = null;
	Statement   stmt       = null;
	ResultSet   rs         = null;
	ResultSet   rs1        = null;
	String      sql        = null;
	String      sql1       = null;
	int         noOfrows   = 0;
	String[][]  records    = null;
	String[]    names      = null;

	try
	{
		connection       =this.getConnection();
		stmt             =connection.createStatement();
		values           =values.substring(0,values.length()-1);
		sql              ="SELECT "+values+" FROM FS_FR_LOCATIONMASTER ORDER BY LOCATIONID";
		sql1             ="SELECT COUNT(*) NO_ROWS FROM FS_FR_LOCATIONMASTER";
		rs               =stmt.executeQuery(sql1);
		rs.next();
		noOfrows          = rs.getInt("NO_ROWS");
		records           = new String[noOfrows][count];
		rs1               = stmt.executeQuery(sql);
		for(int i=0;rs1.next();i++)
		{
			for(int j=0;j<count;j++)
			{
				records[i][j]=rs1.getString(j+1);
			}
		}
	}
		catch(SQLException sqEx)
        {
			//Logger.error(FILE_NAME, "[getLocationMaster(String values,int count)] -> "+sqEx.toString());
      logger.error(FILE_NAME+ "[getLocationMaster(String values,int count)] -> "+sqEx.toString());
            throw new EJBException(sqEx.toString());
        }
        finally
        {
			ConnectionUtil.closeConnection(connection, stmt, rs);
      ConnectionUtil.closeStatement(null,rs1);//Added By RajKumari on 30-10-2008 for Connection Leakages.
        }
	return records;
} //end of the getLocationMaster method

/**
   * 
   * @param commodity
   * @param loginDetails
   * @return 
   */
	public boolean addCommodityDetails(CommodityJspBean	commodity ,ESupplyGlobalParameters loginDetails) 
	{
		 Connection	 connection	= null;
		 Statement	 stmt		= null;
		 ResultSet	 rs			= null;
		 try
		 {
			connection		 	= operationsImpl.getConnection();
			stmt			 	= connection.createStatement();
			String sid		 	= commodity.getCommodityId();
			String sdesc	 	= commodity.getCommodityDescription();
			String sHandling 	= commodity.getCommodityHandlingInfo();
			String sType	 	= commodity.getCommodityType();
			String hIndicator	= commodity.getHazardIndicator();
      String subClass   = commodity.getSubClass();
      String unNumber   = commodity.getUnNumber();
      String classType= commodity.getClassType();
			rs = stmt.executeQuery("SELECT COUNT(*)	NO_ROWS	FROM FS_FR_COMODITYMASTER  WHERE	COMODITYID = '"+sid+"'");
			rs.next();
			int	count =	rs.getInt("NO_ROWS");
			if(count > 0)
			{return false;}
			if(sHandling ==	null)
			{
				sHandling =	"";
			}
			String sqlInsert = " INSERT	INTO FS_FR_COMODITYMASTER(COMODITYID , COMODITYDESCRIPTION , HANDLINGINFO ,COMODITYTYPE,HAZARD_INDICATOR,SUBCLASS,UNNUMBER ,CLASSTYPE,TERMINALID,INVALIDATE  ) VALUES ('" + sid+"','"+ sdesc	+
									"','" +sHandling+ "','"	+sType+	"','"+hIndicator+"','"+subClass+"','"+unNumber+"','"+classType+"','"+loginDetails.getTerminalId()+"','F')";//added by rk
	//stmt.executeQuery( sqlInsert );
      //For 10g server implementation
      stmt.executeUpdate( sqlInsert );
			operationsImpl.setTransactionDetails( loginDetails.getTerminalId(),
														loginDetails.getUserId(),
														"CommodityMaster",
														sid,
														loginDetails.getLocalTime(),
														"ADD");
			return true;
		 }
		 catch ( SQLException sqle )
		 {
			 //Logger.error(FILE_NAME,"Exception in setCommodityDetails()	method of ETransHOSuperUserSetupSessionBean : "+sqle.toString());
       logger.error(FILE_NAME+"Exception in setCommodityDetails()	method of ETransHOSuperUserSetupSessionBean : "+sqle.toString());
			  error	= sqle.toString();
			  throw	new	javax.ejb.EJBException("Exception	in setCommodityDetails() method	of ETransHOSuperUserSetupSessionBean :	"+sqle.toString());
		  }
		  catch( Exception cnfe	)
		  {
			//Logger.error(FILE_NAME,"Exception in setCommodityDetails() method of ETransHOSuperUserSetupSessionBean	:  "+cnfe.toString());
      logger.error(FILE_NAME+"Exception in setCommodityDetails() method of ETransHOSuperUserSetupSessionBean	:  "+cnfe.toString());
			   throw new javax.ejb.EJBException("Remote Exception	in setCommodityDetails() method	of ETransHOSuperUserSetupSessionBean :	 "+cnfe.toString());
		  }
		  finally
		  {
			try
			  {
				if(	stmt !=	null )
				  { stmt.close();}
				//Connection Leakage ----------------- 7-DEC-2004 ---------------------Santhosam.P
				if(	rs !=	null )
				   {rs.close();}
				if(	connection != null )
				  { connection.close();}
			}
		  catch(SQLException sqle)
		  {
		  //Logger.error(FILE_NAME,"Exception	in finally block of	setCommodityDetails() method of	ETransHOSuperUserSetupSessionBean : "+sqle.toString());
      logger.error(FILE_NAME+"Exception	in finally block of	setCommodityDetails() method of	ETransHOSuperUserSetupSessionBean : "+sqle.toString());
		  }
		}
	}//	end	of setCommodityDetails

	/**
   * 
   * @param commodity
   * @param loginDetails
   * @return 
   */
	public boolean updateCommodityMasterDetails(CommodityJspBean commodity,ESupplyGlobalParameters loginDetails)
	{
		Connection	connection = null;
		Statement	stmt	   = null;
		try
		{
			connection        = operationsImpl.getConnection();
			stmt              = connection.createStatement();
			String sid        = commodity.getCommodityId();
			String sdesc      = commodity.getCommodityDescription();
			String sHandling  = commodity.getCommodityHandlingInfo();
			String sType      = commodity.getCommodityType();
			String hIndicator	= commodity.getHazardIndicator();
      String subClass   = commodity.getSubClass();
      String unNumber   = commodity.getUnNumber();
      String classType  = commodity.getClassType();
			if(sHandling ==	null)
				{sHandling =	"";}
			String sqlUpdate = " UPDATE	FS_FR_COMODITYMASTER  SET COMODITYDESCRIPTION ='" +sdesc	+"',HANDLINGINFO ='" + sHandling +"',COMODITYTYPE ='" +	sType +"',HAZARD_INDICATOR='"+hIndicator+"',SUBCLASS='"+subClass+"',UNNUMBER ='"+unNumber+"', "+
                        " CLASSTYPE = '"+classType+"' WHERE	COMODITYID='"+sid+"'";
			stmt.executeUpdate(	sqlUpdate );
			operationsImpl.setTransactionDetails( loginDetails.getTerminalId(),
														loginDetails.getUserId(),
														"CommodityMaster",
														sid,
														loginDetails.getLocalTime(),
														"MODIFY");
					return true;
		}
		catch (	SQLException sqle )
		{
			//Logger.error(FILE_NAME,"Exception in updateCommodityMasterDetails()	method of ETransHOSuperUserSetupSessionBean : "+sqle.toString());
      logger.error(FILE_NAME+"Exception in updateCommodityMasterDetails()	method of ETransHOSuperUserSetupSessionBean : "+sqle.toString());
			error =	sqle.toString();
			
			return false;
		}
		catch( Exception cnfe )
		{
			throw new javax.ejb.EJBException("Remote Exception in	updateCommodityMasterDetails() method of ETransHOSuperUserSetupSessionBean	:  "+cnfe.toString());
		}
		finally
		{
			try
			{
				if(	stmt !=	null )
				   {stmt.close();}
				if(	connection != null )
				   {connection.close();}
			}
			catch(SQLException sqle)
		   {
		   //Logger.error(FILE_NAME,"Exception in	finally	block of  setCommodityDetails()	method of ETransHOSuperUserSetupSessionBean :	"+sqle.toString());
       logger.error(FILE_NAME+"Exception in	finally	block of  setCommodityDetails()	method of ETransHOSuperUserSetupSessionBean :	"+sqle.toString());
		   }
		}
	}//	end	of updateCommodityMasterDetails

	/**
   * 
   * @param commodityId
   * @param loginDetails
   * @return 
   */
	public boolean deleteCommodityMasterDetails(String commodityId ,ESupplyGlobalParameters	loginDetails) 
	{
		Connection	connection = null;
		Statement	stmt	   = null;
		Statement	stmt1	   = null;
    ResultSet	rs       = null;//Added By RajKumari on 30-10-2008 for Connection Leakages.
		try
		{
			connection = operationsImpl.getConnection();
			stmt = connection.createStatement();
			stmt1	= connection.createStatement();
			String sqlQuery =	"SELECT	* FROM FS_FR_COMODITYMASTER 	 WHERE COMODITYID='"+commodityId+"'";
			rs = stmt1.executeQuery(sqlQuery);//Commented By RajKumari on 30-10-2008 for Connection Leakages.
			if(rs.next())
			{
				String sqlDelete = "DELETE FROM FS_FR_COMODITYMASTER 	WHERE COMODITYID='"+commodityId+"'";
				stmt.executeUpdate( sqlDelete	 );
				operationsImpl.setTransactionDetails(	loginDetails.getTerminalId(),
														loginDetails.getUserId(),
														"CommodityMaster",
														commodityId,
														loginDetails.getLocalTime(),
														"DELETE");
			}
			else
			{
					error =	"RECORD	IS NOT PRESENT ";
					return false;
			}
			return true;
		}
		catch( SQLException sqle	)
		{
			//Logger.error(FILE_NAME,"Exception in deleteCommodityMasterDetails() method	of ETransHOSuperUserSetupSessionBean :	"+sqle.toString());
      logger.error(FILE_NAME+"Exception in deleteCommodityMasterDetails() method	of ETransHOSuperUserSetupSessionBean :	"+sqle.toString());
			error = sqle.toString();
			
			return false;
		}
		catch( Exception cnfe )
		{
			throw new javax.ejb.EJBException("Remote Exception in	deleteCommodityMasterDetails() method of ETransHOSuperUserSetupSessionBean	: "+cnfe.toString());
		}
		finally
		{
		try
			{
      //Added By RajKumari on 30-10-2008 for Connection Leakages.
        if(rs!= null)
        {
          rs.close();
        }
				if(	stmt !=	null )
				   {stmt.close();}
				//Connection Leakage ---------------- 7-DEC-2004 --------- Santhosam.P
				if(	stmt1 !=	null )
				  { stmt1.close();}
				if(	connection != null )
				  { connection.close();}
		  }
		catch(SQLException sqle)
		{
			//Logger.error(FILE_NAME,"Exception in finally block of deleteCommodityMasterDetails() method	of ETransHOSuperUserSetupSessionBean :	" +	sqle.toString());
      logger.error(FILE_NAME+"Exception in finally block of deleteCommodityMasterDetails() method	of ETransHOSuperUserSetupSessionBean :	" +	sqle.toString());
		}
	}
	}//	end	of deleteCommodityMasterDetails

	/**
   * 
   * @param commodityId
   * @return 
   */
	public CommodityJspBean	getCommodityDetails(String commodityId)	
	{
		 Connection	 connection	= null;	// variable	to store the connection	object
		 Statement stmt	= null;	// variable	to store the Statement object
		 ResultSet rs =	null; // variable to store the ResultSet object
		 CommodityJspBean commodity	= null;	// varible to store	the	CommodityJspBean object
		 try
		 {
		 connection	= operationsImpl.getConnection();
		 String	sqlQuery = "SELECT * FROM FS_FR_COMODITYMASTER  WHERE COMODITYID	= '"+	commodityId+"' AND INVALIDATE='F'";
			 stmt =	connection.createStatement();
			 rs	= stmt.executeQuery( sqlQuery );
			 if( rs.next())
			  {
					String commodityMasterId	 	= rs.getString( "COMODITYID" );
					String commodityDescription	 	= rs.getString( "COMODITYDESCRIPTION"	);
					String commodityHandlingInfo 	= rs.getString( "HANDLINGINFO"	);
					String commodityType 			= rs.getString( "COMODITYTYPE" );
					String hIndicator				= rs.getString("HAZARD_INDICATOR");
					String subClass					= rs.getString("SUBCLASS");
					String unNumber					= rs.getString("UNNUMBER");
					String classType				= rs.getString("CLASSTYPE");
					if (commodityHandlingInfo==null)
					{
						commodityHandlingInfo="";
					}
					if (commodityType==null || commodityType.equals(null))
					{
						commodityType="";
					}
					if(subClass==null || subClass.equals(null))
					{
						subClass="";
					}
					if(unNumber==null || unNumber.equals(null))
					{
						unNumber="";
					}
					if(classType==null || classType.equals(null))
					{
						classType="";
					}
					commodity =	new	CommodityJspBean();
					commodity.setCommodityId(commodityMasterId);
					commodity.setCommodityDescription(commodityDescription);
					commodity.setCommodityHandlingInfo(commodityHandlingInfo );
					commodity.setCommodityType(commodityType );
					commodity.setHazardIndicator(hIndicator );
					commodity.setSubClass(subClass);
					commodity.setUnNumber(unNumber);
					commodity.setClassType(classType);
			 }
		   else
			{
				return null;
			}
		}
		 catch(	SQLException sqle)
		 {
			 
			//Logger.error(FILE_NAME,"Exception in getCommodityDetails() method of ETransHOSuperUserSetupSessionBean	: "+sqle.toString());
      logger.error(FILE_NAME+"Exception in getCommodityDetails() method of ETransHOSuperUserSetupSessionBean	: "+sqle.toString());
      throw new EJBException(sqle.toString());
		 }
		 finally
		 {
			try
			{
			   if( rs != null )
				   {rs.close();}
				if(	stmt !=	null )
				  { stmt.close();}
				if(	connection != null )
				   {connection.close();}
		  }
			catch(SQLException sqle)
		  {
		  //Logger.error(FILE_NAME,"Exception	in getCommodityDetails() method	of ETransHOSuperUserSetupSessionBean :	"+sqle.toString());
      logger.error(FILE_NAME+"Exception	in getCommodityDetails() method	of ETransHOSuperUserSetupSessionBean :	"+sqle.toString());
		  }
		 }
		 return	commodity;
	} // End of	getCommodityMasterDetails

	/**
   * 
   * @param searchString
   * @return 
   */
    public ArrayList getCommodityIds(String searchString,String terminalId,String operation)
    {
        Connection  connection	= null;
       // Statement	stmt		= null;//Commented By RajKumari on 30-10-2008 for Connection Leakages.
        ResultSet	rs			= null;
		CallableStatement cstmt = null;
		ArrayList commodityIds = new ArrayList();

			/*	if(searchString!=null && !(searchString.equals(""))){
			searchString=" WHERE COMODITYID LIKE '"+searchString+"%' ";
		}else{
			searchString="WHERE COMODITYID LIKE '%' ";
    }*/
    if(searchString==null)
      searchString ="";
		//String	sqlQry	=	"SELECT COMODITYID,COMODITYDESCRIPTION FROM FS_FR_COMODITYMASTER "+searchString+" ORDER BY COMODITYID";
		try
        {
            connection = this.getConnection();
            //stmt       = connection.createStatement();
            //rs = stmt.executeQuery(sqlQry);
			cstmt        = connection.prepareCall("{ ?= call QMS_SETUPS.GET_COMMODITY(?,?,?)}");
            cstmt.registerOutParameter(1,OracleTypes.CURSOR);
            cstmt.setString(2,terminalId);            
            cstmt.setString(3,operation);
            cstmt.setString(4,searchString+"%");
            cstmt.execute();
            rs  =  (ResultSet)cstmt.getObject(1);            
            while(rs.next())
			{commodityIds.add(rs.getString(1)+","+rs.getString(2));}
        }
        catch(SQLException sqEx)
        {sqEx.printStackTrace();
			//Logger.error(FILE_NAME, "[getCommodityIds(whereclause)] -> "+sqEx.toString());
      logger.error(FILE_NAME+ "[getCommodityIds(whereclause)] -> "+sqEx.toString());
            throw new EJBException(sqEx.toString());
        }
        catch(Exception sqEx)
        {sqEx.printStackTrace();
			//Logger.error(FILE_NAME, "[getCommodityIds(whereclause)] -> "+sqEx.toString());
      logger.error(FILE_NAME+ "[getCommodityIds(whereclause)] -> "+sqEx.toString());
            throw new EJBException(sqEx.toString());
        }
        finally
        {
			ConnectionUtil.closeConnection(connection, cstmt, rs);
        }
        return commodityIds;
    }
//Added by rk for Getting commodity ids for commodity types
/**
   * 
   * @param searchString
   * @param comodityType
   * @param terminalId
   * @return 
   */ 
    
    public ArrayList getCommodityIds(String searchString,String comodityType,ESupplyGlobalParameters loginbean)
    {
        Connection  connection	  = null;
        PreparedStatement	pStmt		= null;
        ResultSet	rs			        = null;

		ArrayList commodityIds = new ArrayList();
    comodityType  =   comodityType+"%";

		if(searchString==null || (searchString!=null && searchString.trim().length()==0))
        searchString  = "";
    
    searchString  = searchString + "%";
    
    String terminalQry ="";
    
    if(!"HO_TERMINAL".equalsIgnoreCase(loginbean.getAccessType()))
    {
      terminalQry  =  " AND TERMINALID IN(SELECT TERMINALID FROM FS_FR_TERMINALMASTER WHERE TERMINALID = ? " +
                      " UNION SELECT CHILD_TERMINAL_ID TERMINALID FROM FS_FR_TERMINAL_REGN CONNECT BY PRIOR CHILD_TERMINAL_ID=PARENT_TERMINAL_ID START WITH PARENT_TERMINAL_ID=? "+
                      " UNION SELECT PARENT_TERMINAL_ID TERMINALID FROM FS_FR_TERMINAL_REGN CONNECT BY PRIOR PARENT_TERMINAL_ID=CHILD_TERMINAL_ID START WITH CHILD_TERMINAL_ID=? "+
                      " UNION SELECT TERMINALID FROM FS_FR_TERMINALMASTER WHERE OPER_ADMIN_FLAG = 'H')" ;
    }
		String	sqlQry	=	"SELECT COMODITYID,COMODITYDESCRIPTION,HAZARD_INDICATOR ,CLASSTYPE ,UNNUMBER FROM FS_FR_COMODITYMASTER WHERE COMODITYID LIKE ? AND COMODITYTYPE  LIKE ? "
                      +terminalQry+" AND INVALIDATE = 'F' "+
                      " ORDER BY COMODITYID";
       try
        {
            connection = this.getConnection();
            pStmt       = connection.prepareStatement(sqlQry);
            pStmt.setString(1,searchString);
            pStmt.setString(2,comodityType);
            if(!"HO_TERMINAL".equalsIgnoreCase(loginbean.getAccessType()))
            {
              pStmt.setString(3,loginbean.getTerminalId());
              pStmt.setString(4,loginbean.getTerminalId());
              pStmt.setString(5,loginbean.getTerminalId());
            }
            rs = pStmt.executeQuery();
            
            while(rs.next())
			      {
              commodityIds.add(rs.getString(1)+","+rs.getString(2)+","+rs.getString(3)+","+(rs.getString(4)!=null?rs.getString(4):"")+","+(rs.getString(5)!=null?rs.getString(5):""));
            }
        }
        catch(SQLException sqEx)
        {
            logger.error(FILE_NAME+ "[getCommodityIds(whereclause)] -> "+sqEx.toString());
            throw new EJBException(sqEx.toString());
        }
        finally
        {
            ConnectionUtil.closeConnection(connection, pStmt, rs);
        }
        return commodityIds;
    }

	/**
   * 
   * @return 
   */
	public	ArrayList getAllCountryIds() 
	{
		ArrayList		 vec		 = new ArrayList();
		Connection  connection1  =	null;
		Statement	  stmt		  =	null;
		ResultSet	  rs		  =	null;
		try
		{
				connection1	=	getConnection();
				stmt		=	connection1.createStatement();
				String sqlQuery	=	"SELECT	COUNTRYID FROM FS_COUNTRYMASTER ORDER BY COUNTRYNAME";
				rs			=	stmt.executeQuery(sqlQuery);
				while(rs.next())	// Getting all countryId from the database.
					{vec.add(rs.getString(1));}
		}
		catch(SQLException se)
		{
			
			//Logger.error(FILE_NAME,"ETTSetupSessionBean : getAllCountryIds() : "+se.toString());
      logger.error(FILE_NAME+"ETTSetupSessionBean : getAllCountryIds() : "+se.toString());
      throw new EJBException(se.toString());
		}
		finally
		{
			try
			{
				if(	rs != null )
					{rs.close();}
				if(	stmt !=	null )
					{stmt.close();}
				if(	connection1 != null )
					{connection1.close();}
			}//End Of Try Block.
			catch(SQLException	e)
			{
				//Logger.error(FILE_NAME,"ETTSetupSessionBean : getAllCountryIds() : "+e.toString());
        logger.error(FILE_NAME+"ETTSetupSessionBean : getAllCountryIds() : "+e.toString());
			}
		}//end of finally.
		return vec;
	}	// end of getAllCountryIds()

	/**
   * 
   * @param vendorId
   * @return 
   * @throws java.sql.SQLException
   */
	public ArrayList getVendorDetails(String vendorId) throws java.sql.SQLException
	{		
		Connection con = null;
		PreparedStatement pStmt  = null;
		//PreparedStatement pStmt2 = null;//Commented By RajKumari on 30-10-2008 for Connection Leakages.
		ResultSet rs = null;			
		ArrayList aList = new ArrayList();				
		String sql1 = null;		
		String vendorAdd = null;		
	try
		{
			con = getConnection();				
			sql1 = "SELECT COMPANY_NAME, VENDOR_ADDRESS_ID FROM FS_FR_VENDOR_MASTER WHERE VENDOR_ID=?";			
			pStmt = con.prepareStatement(sql1);
			pStmt.setString(1,vendorId);			
			rs = pStmt.executeQuery();			
			rs.next();
			{
				aList.add(rs.getString(1));				
				aList.add(rs.getString(2));
				vendorAdd	= rs.getString(2);				
			}
		}						
	catch(Exception ex)
		{
			//Logger.error(FILE_NAME,"ETTSetupSessionBean : getVendorDetails() : "+ex);
      logger.error(FILE_NAME+"ETTSetupSessionBean : getVendorDetails() : "+ex);
			throw new EJBException(ex.toString());
		}
		finally
		{
			try
			{
				if(rs!=null)
					{rs.close();}
				if(pStmt!=null)
					{pStmt.close();}
				if(con!=null)
					{con.close();}
			}
			catch(SQLException sqEx)
			{
				//Logger.error(FILE_NAME,"ETTSetupSessionBean : getVendorDetails() : "+sqEx);
        logger.error(FILE_NAME+"ETTSetupSessionBean : getVendorDetails() : "+sqEx);
			}
		}
		return aList;
	}

	/**
   * 
   * @param vendorAdd
   * @return 
   * @throws java.sql.SQLException
   */
	public ArrayList getVendorAddressDetails(String vendorAdd) throws java.sql.SQLException
	{
		Connection con = null;
		PreparedStatement pStmt  = null;		
		ResultSet rs = null;				
		ArrayList alist = new ArrayList();		
		String sql1 = null;		
		
	try
		{
			con =getConnection();				
			sql1 = "SELECT ADDRESSLINE1, ADDRESSLINE2, CITY, STATE, PHONENO, FAX, ZIPCODE, EMAILID, FS_ADDRESS.COUNTRYID COUNTRYID, HELPLINE,COUNTRYNAME FROM FS_ADDRESS,FS_COUNTRYMASTER WHERE FS_ADDRESS.COUNTRYID = FS_COUNTRYMASTER.COUNTRYID AND ADDRESSID = ?";
			pStmt = con.prepareStatement(sql1);
			pStmt.setString(1,vendorAdd);			
			rs = pStmt.executeQuery();
			rs.next();
			{				
				alist.add(rs.getString(1));				     
				alist.add(rs.getString(2));				
				alist.add(rs.getString(3));
				alist.add(rs.getString(4));
				alist.add(rs.getString(5));
				alist.add(rs.getString(6));
				alist.add(rs.getString(7));
				alist.add(rs.getString(8));
				alist.add(rs.getString(9));
				alist.add(rs.getString(10));					
			    alist.add(rs.getString(11));
			}
		}			
	catch(Exception ex)
		{
			//Logger.error(FILE_NAME,"ETTSetupSessionBean : getVendorAddressDetails() : "+ex);
      logger.error(FILE_NAME+"ETTSetupSessionBean : getVendorAddressDetails() : "+ex);
			throw new EJBException(ex.toString());
		}
		finally
		{
			try
			{
				if(rs!=null)
					{rs.close();}
				if(pStmt!=null)
					{pStmt.close();}
				if(con!=null)
					{con.close();}
			}
			catch(SQLException sqEx)
			{
				//Logger.error(FILE_NAME,"ETTSetupSessionBean : getVendorAddressDetails() : "+sqEx);
        logger.error(FILE_NAME+"ETTSetupSessionBean : getVendorAddressDetails() : "+sqEx);
			}
		}
		return alist;
	}

	/**
   * 
   * @param vendorDtlObj
   * @param vendorId
   * @param vendorAdd
   * @return 
   * @throws java.sql.SQLException
   */
	public int setAddVendorDetails(ETransTruckingVendor vendorDtlObj,String vendorId, String vendorAdd) throws java.sql.SQLException
	{
		Connection con = null;
		PreparedStatement pStmt2 = null;
		int i 		= 0;
		String sql2 = null;
		try
		{
			con =getConnection();
			String vCheck1 = vendorAdd;			
			String vCheck2 = vendorId;	
				Address address =new Address();
				address.setAddressLine1(vendorDtlObj.getAddLine1());
				address.setAddressLine2(vendorDtlObj.getAddLine2());
				address.setCity(vendorDtlObj.getCity());  
				address.setState(vendorDtlObj.getState());  
				address.setZipCode(vendorDtlObj.getZipCode());
				address.setCountryId(vendorDtlObj.getCountryId());
				address.setPhoneNo(vendorDtlObj.getContactNo());
				address.setEmailId(vendorDtlObj.getEmail());
				address.setFax(vendorDtlObj.getFax());  
				address.setHelpLine(vendorDtlObj.getHelpLine());
				
				AddressDAO addressDAO= new AddressDAO();
				int addressId = addressDAO.create(address);

			sql2 =  "INSERT INTO FS_FR_VENDOR_MASTER(VENDOR_ID,COMPANY_NAME,VENDOR_ADDRESS_ID) VALUES(?,?,?)";							
			pStmt2 = con.prepareStatement(sql2);								
			pStmt2.setString(1,vendorId);
			pStmt2.setString(2,vendorDtlObj.getVendorName());				
			pStmt2.setInt(3,addressId);
			i = pStmt2.executeUpdate();
		}						
		catch(Exception ex)
		{	
			
			//Logger.error(FILE_NAME,"ETTSetupSessionBean : setAddVendorDetails() : "+ex.toString());			
      logger.error(FILE_NAME+"ETTSetupSessionBean : setAddVendorDetails() : "+ex.toString());			
      throw new EJBException(ex.toString());
		}
		finally
		{
			try
			{
				if(pStmt2!=null)
					{pStmt2.close();}
				if(con!=null)
					{con.close();}
			}
			catch(SQLException sqEx)
			{
				
				//Logger.error(FILE_NAME,"ETTSetupSessionBean : setAddVendorDetails() : "+sqEx);
        logger.error(FILE_NAME+"ETTSetupSessionBean : setAddVendorDetails() : "+sqEx);
			}
		}
		return i;
	}
//	setAddVendorDetails ends here	

	/**
   * 
   * @param vendorDtlObj
   * @return 
   * @throws java.sql.SQLException
   */
	public int setModifyVendorDetails(ETransTruckingVendor vendorDtlObj)throws java.sql.SQLException
	{		
		Connection con = null;
		PreparedStatement pStmt1 = null;
		PreparedStatement pStmt2 = null;				
		int i 		= 0;
		String sql1 = null;
		String sql2 = null;					
		try
		{
			con =getConnection();
			
			sql1 =  "UPDATE FS_ADDRESS SET ADDRESSLINE1=?,ADDRESSLINE2=?, CITY=?, STATE=?, ZIPCODE=?, COUNTRYID=?, PHONENO=?, EMAILID=?, FAX=?, HELPLINE=? WHERE ADDRESSID=?";				
			pStmt1 = con.prepareStatement(sql1);				
			pStmt1.setString(1,vendorDtlObj.getAddLine1());				
			pStmt1.setString(2,vendorDtlObj.getAddLine2());
			pStmt1.setString(3,vendorDtlObj.getCity());
			pStmt1.setString(4,vendorDtlObj.getState());
			pStmt1.setString(5,vendorDtlObj.getZipCode());
			pStmt1.setString(6,vendorDtlObj.getCountryId());
			pStmt1.setString(7,vendorDtlObj.getContactNo());				
			pStmt1.setString(8,vendorDtlObj.getEmail());
			pStmt1.setString(9,vendorDtlObj.getFax());
			pStmt1.setString(10,vendorDtlObj.getHelpLine());
			pStmt1.setString(11,vendorDtlObj.getVendorAdd());				
			pStmt1.executeUpdate();
			sql2 =  "UPDATE FS_FR_VENDOR_MASTER SET COMPANY_NAME=? WHERE VENDOR_ID=?";								
			pStmt2 = con.prepareStatement(sql2);				
			pStmt2.setString(1,vendorDtlObj.getVendorName());			
			pStmt2.setString(2,vendorDtlObj.getVendorId());
			i = pStmt2.executeUpdate();				
		}
		catch(Exception ex)
		{	
			
			//Logger.error(FILE_NAME,"ETTSetupSessionBean : setModifyVendorDetails() : "+ex.toString());			
      logger.error(FILE_NAME+"ETTSetupSessionBean : setModifyVendorDetails() : "+ex.toString());			
      throw new EJBException(ex.toString());
		}
		finally
		{
			try
			{
				if(pStmt1!=null)
					{pStmt1.close();}
				if(pStmt2!=null)
					{pStmt2.close();}
				if(con!=null)
					{con.close();}
			}
			catch(SQLException sqEx)
			{
				
				//Logger.error(FILE_NAME,"ETTSetupSessionBean : setModifyVendorDetails() : "+sqEx);
        logger.error(FILE_NAME+"ETTSetupSessionBean : setModifyVendorDetails() : "+sqEx);
			}
		}
		return i;
	}	
//	setModifyVendorDetails ends here	

	/**
   * 
   * @param vendorId
   * @param vendorAdd
   * @return 
   * @throws java.sql.SQLException
   */
	public boolean deleteVendorDetails(String vendorId, String vendorAdd)throws java.sql.SQLException
	{
		Connection connection1		= null;
		Statement stmt2			= null; 	 	 	 	 
		PreparedStatement  pstmt	= null; 
		String sql1				= null;
		String sql2				= null;
		ResultSet rs				= null;
		int len					= 0;
		try
		{	
			connection1   =getConnection();			
			sql1 = "SELECT COUNT(*) COMPANY_NAME FROM FS_FR_VENDOR_MASTER WHERE VENDOR_ID ='"+vendorId+"'";
			pstmt        = connection1.prepareStatement(sql1);
			rs = pstmt.executeQuery();
			rs.next();
			len = rs.getInt("COMPANY_NAME");  
			if(len == 0)
			return false; 
			stmt2 			= connection1.createStatement();		   
			sql2 = "DELETE FROM FS_FR_VENDOR_MASTER WHERE VENDOR_ID ='"+vendorId+"'";
			stmt2.executeUpdate(sql2);							 					
			return true;
		}
		catch(Exception e)
		{
			//Logger.error(FILE_NAME,"ETTSetupSessionBean  deleteVendorDetails.jsp() : "+e.toString());
      logger.error(FILE_NAME+"ETTSetupSessionBean  deleteVendorDetails.jsp() : "+e.toString());
			throw new javax.ejb.EJBException(e.toString());	
		}
		finally 
		{
			try
			{	    
				if(stmt2 != null)
					{stmt2.close();}
				if(rs != null)
					{rs.close();}
        //Added By RajKumari on 30-10-2008 for Connection Leakages.
        if(pstmt != null)
        {pstmt.close();}
				if(connection1 != null)
					{connection1.close();}
			}
			catch(Exception e)
			{
				//Logger.error(FILE_NAME,"ETTSetupSessionBean : deleteVendorDetails.jsp() : "+e );
        logger.error(FILE_NAME+"ETTSetupSessionBean : deleteVendorDetails.jsp() : "+e );
			}
		}
	}	
//	deleteVendorDetails ends here.

	/**
   * 
   * @return 
   */
	public	ArrayList getAllVendorIds()  
	{
		ArrayList		 vec	  = new ArrayList();
		Connection  connection1	  =	null;
		Statement	  stmt		  =	null;
		ResultSet	  rs		  =	null;
		try
		{
				connection1		=	getConnection();
				stmt			=	connection1.createStatement();
				String sqlQuery	=	"SELECT	VENDOR_ID FROM FS_FR_VENDOR_MASTER ORDER BY VENDOR_ID";
				rs				=	stmt.executeQuery(sqlQuery);
				while(rs.next())	// Getting all vendorIds from the database.
				{	vec.add(rs.getString(1));}
		}
		catch(SQLException se)
		{
			
			//Logger.error(FILE_NAME,"ETTSetupSessionBean : getAllVendorIds(): "+se.toString());
      logger.error(FILE_NAME+"ETTSetupSessionBean : getAllVendorIds(): "+se.toString());
      throw new EJBException(se.toString());
		}
		finally
		{
			try
			{
				if(	rs != null )
					{rs.close();}
				if(	stmt !=	null )
					{stmt.close();}
				if(	connection1 != null )
					{connection1.close();}
			}//End Of Try Block.
			catch(SQLException	e)
			{
				//Logger.error(FILE_NAME,"ETTSetupSessionBean : getAllVendorIds() : "+e.toString());
        logger.error(FILE_NAME+"ETTSetupSessionBean : getAllVendorIds() : "+e.toString());
			}
		}//end of finally.
		return vec;
	}	// end of getAllVendorIds()

	 /**
   * 
   * @param terminalId
   * @param searchString
   * @param indicator
   * @param masterid
   * @param stra
   * @return 
   */
public ArrayList getVendorIds(String terminalId,String searchString,String indicator,String masterid,String stra)
    {
        Connection	connection	= null;
        Statement   stmt		= null;
        ResultSet   rs			= null;
		String sql				= "";
		ArrayList vendorIds = new ArrayList();
		// @@ Suneetha Added on 20050429
		String	searchStr = "";
		if("Delete".equalsIgnoreCase(stra)){
		  	searchStr = " AND VENDOR_ID NOT IN(SELECT DISTINCT M.VENDOR_ID FROM FS_FR_MASTERDOCHDR M WHERE M.VENDOR_ID LIKE '"+searchString+"%') ";
		}
		// @@ 20050429
		if(searchString!=null && !(searchString.equals("")))
		{
			searchString = " AND  VENDOR_ID LIKE '"+searchString+"%'";
		}
		else
		{
			searchString = "";
		}
		// @@ Suneetha Replaced on 20050429
	/*	sql = "SELECT VENDOR_ID ,COMPANY_NAME  FROM FS_FR_VENDOR_MASTER WHERE TRML_ID ='"+terminalId+"' "
		+searchString+" AND OPER_AC_INDICATOR = '"+indicator+"'"
   			   +" UNION "
			   +" SELECT MH.COLORDERID,COLOADERNAME   FROM FS_FR_MASTERDOCHDR MH,FS_FR_COLORDERMASTER CM  " 
			   +" WHERE MH.COLORDERID=CM.COLOADERID  AND MH.MASTERDOCID='"+masterid+"'";
	*/
  
 //@@Query changed by Ujwala as per jira SPETI-5856 onDate :25/05/2005  
  
/*		sql = "SELECT VENDOR_ID ,COMPANY_NAME  FROM FS_FR_VENDOR_MASTER WHERE TRML_ID ='"+terminalId+"' "
		+searchString+" AND OPER_AC_INDICATOR = '"+indicator+"'"
			   +searchStr
   			   +" UNION "
			   +" SELECT MH.COLORDERID,COLOADERNAME   FROM FS_FR_MASTERDOCHDR MH,FS_FR_COLORDERMASTER CM  " 
			   +" WHERE MH.COLORDERID=CM.COLOADERID  AND MH.MASTERDOCID='"+masterid+"'";
		// @@ 20050429
*/
sql = "SELECT VENDOR_ID ,COMPANY_NAME  FROM FS_FR_VENDOR_MASTER WHERE TRML_ID ='"+terminalId+"' "
		+searchString   +searchStr
   			   +" UNION "
			   +" SELECT MH.COLORDERID,COLOADERNAME   FROM FS_FR_MASTERDOCHDR MH,FS_FR_COLORDERMASTER CM  " 
			   +" WHERE MH.COLORDERID=CM.COLOADERID  AND MH.MASTERDOCID='"+masterid+"'";
         
		  
//@@ End - Ujwala as per jira SPETI-5856 onDate :25/05/2005  

        try
        {
			connection = this.getConnection();
            stmt = connection.createStatement();
            rs = stmt.executeQuery(sql);
            while(rs.next())
			{vendorIds.add(rs.getString(1)+" ["+rs.getString(2)+"]");}
        }
        catch(SQLException sqEx)
        {
			//Logger.error(FILE_NAME, "[getVendoryIds(whereclause)] -> "+sqEx.toString());
      logger.error(FILE_NAME+ "[getVendoryIds(whereclause)] -> "+sqEx.toString());
            throw new EJBException(sqEx.toString());
        }
        finally
        {
			ConnectionUtil.closeConnection(connection, stmt, rs);
        }
        return vendorIds;
    }
  
//METHOD ADD BY Nageshwarrao

	/**
   * 
   * @param terminalId
   * @param searchString
   * @param careerId
   * @param shipmentMode
   * @param masterid
   * @return 
   */
 public ArrayList getVendorIdsForAll(String terminalId,String searchString,String careerId,String shipmentMode,String masterid)
    {
        Connection	connection	= null;
        Statement   stmt		= null;
        ResultSet   rs			= null;
		String sql				= "";
		ArrayList vendorIds = new ArrayList();
		if( shipmentMode.equals("Air"))
		{shipmentMode="(1,3,5,7)";   }
		else if( shipmentMode.equals("Sea"))
		{shipmentMode="(2,3,6,7)";}
		else if (shipmentMode.equals("Truck"))  
		{shipmentMode="(4,5,6,7)";}
		if(searchString!=null && !(searchString.equals("")))
		{
			searchString = " AND  VENDOR_ID LIKE '"+searchString+"%'";
		}
		else
		{
			searchString = "";
		}
		if(careerId!=null && !(careerId.equals("")))
		{
          if( shipmentMode.equals("(2,3,6,7)"))
			{sql =  "SELECT VENDOR_ID ,COMPANY_NAME  FROM FS_FR_VENDOR_MASTER WHERE TRML_ID ='"+terminalId+"'  AND CARRER_ID ='"+careerId+"' "+searchString+"  AND SHPMNT_MODE  IN "+shipmentMode+" AND OPER_AC_INDICATOR = 'Y' " 
			   +" UNION "
		       +" SELECT MH.COLOADERID ,COLOADERNAME   FROM FS_FRS_OBLMASTER MH,FS_FR_COLORDERMASTER CM  " 
			   +" WHERE MH.COLOADERID =CM.COLOADERID  AND MH.CONSOLEID='"+masterid+"'";}
		 else 
			{sql =  "SELECT VENDOR_ID ,COMPANY_NAME  FROM FS_FR_VENDOR_MASTER WHERE TRML_ID ='"+terminalId+"'  AND CARRER_ID ='"+careerId+"' "+searchString+"  AND SHPMNT_MODE  IN "+shipmentMode+" AND OPER_AC_INDICATOR = 'Y' " 
			   +" UNION "
		       +" SELECT MH.COLORDERID,COLOADERNAME   FROM FS_FR_MASTERDOCHDR MH,FS_FR_COLORDERMASTER CM  " 
			   +" WHERE MH.COLORDERID=CM.COLOADERID  AND MH.MASTERDOCID='"+masterid+"'";}
		}
		else
		{
             if( shipmentMode.equals("(2,3,6,7)"))
			{ sql = "SELECT VENDOR_ID ,COMPANY_NAME  FROM FS_FR_VENDOR_MASTER WHERE TRML_ID ='"+terminalId+"' "
								+searchString+"AND SHPMNT_MODE  IN "+shipmentMode+" AND OPER_AC_INDICATOR = 'Y' "
						   +" UNION "
						   +" SELECT MH.COLOADERID ,COLOADERNAME   FROM FS_FRS_OBLMASTER MH,FS_FR_COLORDERMASTER CM  " 
						   +" WHERE MH.COLOADERID =CM.COLOADERID  AND MH.MASTERDOCID='"+masterid+"'";}
               else 
					{  sql = "SELECT VENDOR_ID ,COMPANY_NAME  FROM FS_FR_VENDOR_MASTER WHERE TRML_ID ='"+terminalId+"' "
								+searchString+"AND SHPMNT_MODE  IN "+shipmentMode+" AND OPER_AC_INDICATOR = 'Y' "
						   +" UNION "
						   +" SELECT MH.COLORDERID,COLOADERNAME   FROM FS_FR_MASTERDOCHDR MH,FS_FR_COLORDERMASTER CM  " 
						   +" WHERE MH.COLORDERID=CM.COLOADERID  AND MH.MASTERDOCID='"+masterid+"'";}

		}
        try
        {
			connection = this.getConnection();
            stmt = connection.createStatement();
            rs = stmt.executeQuery(sql);
            while(rs.next())
			{vendorIds.add(rs.getString(1)+" ["+rs.getString(2)+"]");}
        }
        catch(SQLException sqEx)
        {
			//Logger.error(FILE_NAME, "[getVendoryIds(whereclause)] -> "+sqEx.toString());
      logger.error(FILE_NAME+ "[getVendoryIds(whereclause)] -> "+sqEx.toString());
            throw new EJBException(sqEx.toString());
        }
        finally
        {
			ConnectionUtil.closeConnection(connection, stmt, rs);
        }
        return vendorIds;
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
    //Added By RajKumari on 30-10-2008 for Connection Leakages.
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
   * @param vendorRegistrationJava
   * @param loginbean
   * @return String
   * @throws com.foursoft.etrans.setup.codecust.exception.CodeCustNotDoneException
   */
public String insertVendorDetails(VendorRegistrationJava vendorRegistrationJava,ESupplyGlobalParameters loginbean)throws CodeCustNotDoneException
{
	String					message	        =	null;
    InitialContext          initialContext  =   null;
    VendorRegistrationEnitityHome   home    =   null;
    VendorRegistrationEnitity   remote      =   null;
	
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
    InitialContext                              initialContext                  =   null;

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
		ConnectionUtil.closeConnection(connection,stmt,rs);
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
    InitialContext         initialContext     =   null;
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
        InitialContext      initialContext  =   null;
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
   * This method takes the LocationMasterJspBean, ESupplyGlobalParameters as orguments which consists of
   * details related to	Charge Details and login details. If the details are available then	the	data is	inserted
   * into the table	FS_FR_LOCATIONMASTER and calls the method setLedger	to enter subgroup details
   * @param 	LocationMasterJspBean			Oject		Contains all the Location Details
   * @param 	ESupplyGlobalParameters		Oject		Contains the login details
   *  
   * @param locationId
   * @return 
   */
	public boolean isLocationMasterLocationIdExists(String locationId)
	{
		ResultSet  rs	= null;
		Statement  stmt	= null;
		Connection connection =	null;
		try
		{
			connection=operationsImpl.getConnection();
			stmt=connection.createStatement();
			 String	sqlQuery="SELECT LOCATIONID FROM FS_FR_LOCATIONMASTER WHERE LOCATIONID= '"	+locationId+"'" ;
			 rs= stmt.executeQuery(sqlQuery);
			 if( rs.next() )
				{return true;}
			 else
				{return false;}
		}
		catch(SQLException sqle	)
		{
			
			//Logger.error(FILE_NAME,"SQL	Error ETransHOSuperUserSetupSessionBean::isLocationMasterLocationIdExists "+sqle.toString());
      logger.error(FILE_NAME+"SQL	Error ETransHOSuperUserSetupSessionBean::isLocationMasterLocationIdExists "+sqle.toString());
      throw new EJBException(sqle.toString());
		}
		catch(Exception	exp	)
		{
			
			//Logger.error(FILE_NAME,"Error ETransHOSuperUserSetupSessionBean::isLocationMasterLocationIdExists " +exp.toString());
      logger.error(FILE_NAME+"Error ETransHOSuperUserSetupSessionBean::isLocationMasterLocationIdExists " +exp.toString());
      throw new EJBException(exp.toString());
		}
		finally
		{
			try
			{
					if(	rs != null )
						{rs.close();}
					if(	stmt !=	null )
						{stmt.close();}
					if(	connection != null )
						{connection.close();}
			}
			catch(SQLException sqle	)
			{
				//Logger.error(FILE_NAME,"SQL	Error while	closing	connections	ETransHOSuperUserSetupSessionBean::isLocationMasterLocationIdExists "+sqle.toString());
        logger.error(FILE_NAME+"SQL	Error while	closing	connections	ETransHOSuperUserSetupSessionBean::isLocationMasterLocationIdExists "+sqle.toString());
			}
		}//End of finally block.
	}

	/**
   * 
   * @param locationMaster
   * @param loginDetails
   * @return 
   */
	public	 boolean addLocationMasterDetails(LocationMasterJspBean locationMaster,ESupplyGlobalParameters	loginDetails )
	{
		Connection connection =	null;
		Statement stmt		  =	null;
		ResultSet rs		  =	null;
		try
		{
			connection = operationsImpl.getConnection();
			stmt	   = connection.createStatement();
			String lid = locationMaster.getLocationId();
			String lnm = locationMaster.getLocationName();
			String cid = locationMaster.getCountryId();
			String city			=	locationMaster.getCity();
			String zipCode		=	locationMaster.getZipCode();
			String shipmentMode	=	locationMaster.getShipmentMode();
			rs = stmt.executeQuery("SELECT COUNT(*)	NO_ROWS	FROM FS_FR_LOCATIONMASTER WHERE	LOCATIONID = '"+lid+"'");
			rs.next();
			int	count =	rs.getInt("NO_ROWS");
			if(count > 0)
			{return false;}
			String sqlInsert = "INSERT INTO	FS_FR_LOCATIONMASTER(LOCATIONID,LOCATIONNAME,COUNTRYID,CITY,ZIPCODE,SHIPMENTMODE,TERMINALID) VALUES('" +lid+"','"+lnm+"','"+cid+"','"+city+"','"+zipCode+"','"+shipmentMode+"','"+loginDetails.getTerminalId()+"')";//added by rk
			stmt.executeUpdate(sqlInsert);
			operationsImpl.setTransactionDetails( loginDetails.getTerminalId(),
														loginDetails.getUserId(),
														"LocationMaster",
														lid,
														loginDetails.getLocalTime(),
														"ADD");
		return true;
		}
		catch(SQLException sqle)
		 {
			//Logger.error(FILE_NAME,"SQL	Exception in setLocationMaster() method	of ETransHOSuperUserSetupSessionBean : "+sqle.toString());
      logger.error(FILE_NAME+"SQL	Exception in setLocationMaster() method	of ETransHOSuperUserSetupSessionBean : "+sqle.toString());
			throw new javax.ejb.EJBException("SQL	Exception in setLocationMaster() method	of ETransHOSuperUserSetupSessionBean : "+sqle.toString());
		 }
		catch(Exception	cnfe)
		 {
			//Logger.error(FILE_NAME,"Exception in setLocationMaster() method	of ETransHOSuperUserSetupSessionBean : "+cnfe.toString());
      logger.error(FILE_NAME+"Exception in setLocationMaster() method	of ETransHOSuperUserSetupSessionBean : "+cnfe.toString());
			throw new javax.ejb.EJBException("Remote Exception in	setLocationMaster()	method of ETransHOSuperUserSetupSessionBean	: "+cnfe.toString());
		  }
		finally
		 {
			try
			 {
				if(	stmt !=	null )
				 {stmt.close();}
				if(rs != null)
				  {rs.close();}
				if(	connection != null )
				   {connection.close();}
			 }
			 catch(SQLException	sqle)
			 {
			 //Logger.error(FILE_NAME,"Exception in finally block	of setLocationMaster() method of ETransHOSuperUserSetupSessionBean : "+sqle.toString());
       logger.error(FILE_NAME+"Exception in finally block	of setLocationMaster() method of ETransHOSuperUserSetupSessionBean : "+sqle.toString());
			  }
		 }
	 }

	 /**
	* This method takes	LocationMasterJspBean and ESupplyGlobalParameters as arguments.
	* If the details are available then	the	data  modified is updated into the table FS_FR_LOCATIONMASTER
	* @param	LocationMasterJspBean	 Object	 Contains all the  details of locations
	* @param	ESupplyGlobalParameters	 Object	 Contains all the login	details
	* @return					boolean
	* @exception			   
	*						   if there	is a problem in	inserting the data
	*/
	public	boolean	updateLocationMasterDetails(LocationMasterJspBean locationMaster,ESupplyGlobalParameters loginDetails) 
	{
		Connection connection =	null;
		Statement stmt = null;
		try
		{
			connection = operationsImpl.getConnection();
			stmt = connection.createStatement();
			String lid =locationMaster.getLocationId();
			String lnm = locationMaster.getLocationName();
			String cid = locationMaster.getCountryId();
			String city			=	locationMaster.getCity();
			String zipCode		=	locationMaster.getZipCode();
			String shipmentMode	=	locationMaster.getShipmentMode();
			String sqlUpdate = " UPDATE	FS_FR_LOCATIONMASTER SET LOCATIONNAME ='" +lnm +"',	COUNTRYID ='" +	cid	+"',CITY='"+city+"', ZIPCODE='"+zipCode+"',SHIPMENTMODE='"+shipmentMode+"'	WHERE LOCATIONID='"+lid+"'";
			stmt.executeUpdate(	sqlUpdate );
			operationsImpl.setTransactionDetails( loginDetails.getTerminalId(),
														loginDetails.getUserId(),
														"LocationMaster",
														lid,
														loginDetails.getLocalTime(),
														"MODIFY");
			return true;
		}
		catch (SQLException	sqle )
		{
			//Logger.error(FILE_NAME,"Exception in updateLocationMasterDetails() method of ETransHOSuperUserSetupSessionBean : "+sqle.toString());
      logger.error(FILE_NAME+"Exception in updateLocationMasterDetails() method of ETransHOSuperUserSetupSessionBean : "+sqle.toString());
			error =	sqle.toString();
			
			return false;
		}
		catch( Exception cnfe )
		{
			throw new javax.ejb.EJBException("Remote Naming Exception	in updateLocationMasterDetails() method	of ETransHOSuperUserSetupSessionBean :"+cnfe.toString());
		}
		finally
		{
			try
			{
				if(	stmt !=	null )
					{stmt.close();}
				if(	connection != null )
					{connection.close();}
			}
			catch(SQLException sqle)
			{
				//Logger.error(FILE_NAME,"Exception in	finally	block of updateLocationMasterDetails() method of ETransHOSuperUserSetupSessionBean : "+sqle.toString());
        logger.error(FILE_NAME+"Exception in	finally	block of updateLocationMasterDetails() method of ETransHOSuperUserSetupSessionBean : "+sqle.toString());
			}
		}
	}//	end	of updateLocationDetails


	/**
	* This method takes	locationMasterId and ESupplyGlobalParameters as	arguments.
	* First	it checks if the details are null or not.
	* If the details are available then	the	data  is deleted .
	* @param locationMasterId		 int	 Location Id
	* @param	ESupplyGlobalParameters	 Object	 Contains all the login	details
	* @return	 boolean
	* @exception			if there	is a problem in	inserting the data   
	*						   
	*/
	public	boolean	deleteLocationMasterDetails(String	locationMasterId,ESupplyGlobalParameters loginDetails) 
	{
		Connection connection =	null;
		Statement stmt = null;
		Statement stmt1	= null;
    ResultSet rs    = null;//Added By RajKumari on 30-10-2008 for Connection Leakages.
		try
		{
			connection = operationsImpl.getConnection();
			stmt = connection.createStatement();
			stmt1 =	connection.createStatement();
			String sqlQuery	= "SELECT *	FROM FS_FR_LOCATIONMASTER  WHERE LOCATIONID='"+locationMasterId+"'";
			rs = stmt1.executeQuery(sqlQuery);//Modified By RajKumari on 30-10-2008 for Connection Leakages.
			if(rs.next())
			{
				String sqlDelete = "DELETE FROM	FS_FR_LOCATIONMASTER  WHERE	LOCATIONID='"+locationMasterId+"'";
				stmt.executeUpdate(	sqlDelete  );
				operationsImpl.setTransactionDetails( loginDetails.getTerminalId(),
														loginDetails.getUserId(),
														"LocationMaster",
														locationMasterId,
														loginDetails.getLocalTime(),
														"DELETE");
			}
			else
			{
				error =	"RECORD	IS NOT PRESENT ";
				return false;
			}
			return true;
		}
		catch (	SQLException sqle )
		{
			//Logger.error(FILE_NAME,"SQL	Exception in  deleteLocationMaster() method	of ETransHOSuperUserSetupSessionBean : "+sqle.toString());
      logger.error(FILE_NAME+"SQL	Exception in  deleteLocationMaster() method	of ETransHOSuperUserSetupSessionBean : "+sqle.toString());
			error =	sqle.toString();
		
			return false;
		}
		catch( Exception cnfe )
		{
			throw new javax.ejb.EJBException("Exception in deleteLocationMaster()	method of ETransHOSuperUserSetupSessionBean	: "+cnfe.toString());
		}
		finally
		{
			try
			{
      //Added By RajKumari on 30-10-2008 for Connection Leakages.
        if(rs!= null)
        { rs.close(); }
				if(	stmt !=	null )
					{stmt.close();}
				//Connection Leakage -------------------- 7-DEC-2004 ----------------- Santhosam.P
				if(	stmt1 !=	null )
					{stmt1.close();}
				if(	connection != null )
					{connection.close();}
			}
			catch(SQLException sqle)
			{
				//Logger.error(FILE_NAME,"Exception	in finally blo9ck of deleteLocationMaster()	method of ETransHOSuperUserSetupSessionBean	: "+sqle.toString());
        logger.error(FILE_NAME+"Exception	in finally blo9ck of deleteLocationMaster()	method of ETransHOSuperUserSetupSessionBean	: "+sqle.toString());
			}
		}
	}	// END OF DELETING LOCATION	MASTER

	/**
	* This method is	used to	retrive	all	the	Details	of Location	according to given Location	Id.
	* This method takes chargeid	as argument	and
	* This method returns array of String  that contains	all	the	Location details from FS_FR_LOCATIONMASTER
	* @param locationId			String	Location Ids
	*
	* @return locationMaster Object	with all Location details.
	*/
	public	LocationMasterJspBean getLocationMasterDetails(String locationId)
	{
		Connection connection =	null;
		Statement stmt = null;
		ResultSet rs = null;
		LocationMasterJspBean locationMaster = null;
		try
		{
			String sqlQuery	= "SELECT *	FROM FS_FR_LOCATIONMASTER WHERE	FS_FR_LOCATIONMASTER.LOCATIONID	= '"+locationId+"'";
			connection = operationsImpl.getConnection();
			stmt = connection.createStatement();
			rs = stmt.executeQuery(	sqlQuery );
			if(	rs.next())
			{
				locationMaster = new LocationMasterJspBean();
				locationMaster.setLocationId(rs.getString("LOCATIONID" ));
				locationMaster.setLocationName(rs. getString("LOCATIONNAME"));
				locationMaster.setCountryId(rs.getString("COUNTRYID"));
				locationMaster.setCity(rs.getString("CITY"));
				locationMaster.setZipCode(rs.getString("ZIPCODE"));
				locationMaster.setShipmentMode(rs.getString("SHIPMENTMODE"));
			}
			else
				{return null;}
			return locationMaster;
		}
		catch(Exception	sqle)
		{
		  
		  //Logger.error(FILE_NAME,"Exception	in getLocationMasterDetails() method of	ETransHOSuperUserSetupSessionBean :	"+sqle.toString());
      logger.error(FILE_NAME+"Exception	in getLocationMasterDetails() method of	ETransHOSuperUserSetupSessionBean :	"+sqle.toString());
		  return null;
		}
		finally
		{
			try
			{
				if(	rs != null )
					{rs.close();}
				if(	stmt !=	null )
					{stmt.close();}
				if(	connection != null )
					{connection.close();}
			}
			catch(SQLException sqle)
			{
			//Logger.error(FILE_NAME,"Exception in	finally	block of  getLocationMasterDetails() method	of ETransHOSuperUserSetupSessionBean : "+sqle.toString());
      logger.error(FILE_NAME+"Exception in	finally	block of  getLocationMasterDetails() method	of ETransHOSuperUserSetupSessionBean : "+sqle.toString());
			}
		}
	} //END	OF GET COUNTRYMASTER DETAILS


	 /**
   * This method is    used to retrive the Country Id's from the database.
   * It    returns a ArrayList that contains country Id's. It  throws javax.ejb.EJBException, when it is not able to reterive the data from the database.
   * @return the ArrayList    which contains  coountry Ids.
   * @exception                  javax.ejb.EJBException
   *   modified by Supraja CKM to show Shipmentmode also.
   * @param searchString
   */
    public ArrayList getLocationIds(String searchString,String operation,String terminalId,String shimode)
    {
        ArrayList      vec              = new ArrayList();
        Connection     connection       = null;
        //Statement      stmt             = null;//Commented By RajKumari on 30-10-2008 for Connection Leakages.
        ResultSet      rs               = null;
		String		   sMode			= "";	
		String		   temp				= "";	
		String		   locId			= "";	
		CallableStatement cstmt  = null;
		// @@ Added by G.Srinivas for TogetherJ on 20050105
		//StringBuffer   locId1           = new StringBuffer();
		//StringBuffer   sMode1           = new StringBuffer();
		// @@
        try
        {
		/*	if(searchString!=null && !(searchString.equals(""))){
				searchString = " WHERE LOCATIONID LIKE '"+searchString+"%' ";
			}else{
				searchString = "";
			}*/
      if(searchString==null)searchString = "";
            connection = this.getConnection();
            //stmt = connection.createStatement();
            //String sqlQuery = "SELECT LOCATIONID,LOCATIONNAME,SHIPMENTMODE FROM  FS_FR_LOCATIONMASTER " + searchString +" ORDER BY LOCATIONID ";
            //rs = stmt.executeQuery(sqlQuery);
			cstmt        = connection.prepareCall("{ ?= call QMS_SETUPS.get_location(?,?,?,?)}");
            cstmt.registerOutParameter(1,OracleTypes.CURSOR);
            cstmt.setString(2,terminalId);
            cstmt.setString(3,operation);
            cstmt.setString(4,(searchString+"%"));
            cstmt.setString(5,shimode);
            cstmt.execute();
			rs  =  (ResultSet)cstmt.getObject(1);
            while(rs.next())
            {

				// @@ Added by G.Srinivas for TogetherJ FIX on 20050117
				StringBuffer   sMode1            = new StringBuffer();
				StringBuffer   locId1           = new StringBuffer();
				// @@

				locId = rs.getString(1);	
                temp = rs.getString(2);
				sMode = rs.getString(3);
                if(sMode==null)
				{  sMode="   ";}
                    
				if(sMode!=null)
				{ 
					if(sMode.equals("7"))	{sMode="AST";}
					if(sMode.equals("1"))	{sMode="A  ";}
					if(sMode.equals("2"))	{sMode=" S ";}		
					if(sMode.equals("3"))	{sMode="AS ";}
					if(sMode.equals("4"))	{sMode="  T";}
					if(sMode.equals("5"))	{sMode="A T";}
					if(sMode.equals("6"))	{sMode=" ST";}
				}	
					
				if(locId.length() == 2)	
				//{locId = locId+" ";	}

				// @@ Replaced by G.Srinivas for TogetherJ on 20050105
                 {locId1.append(locId);
				  locId1.append(" ");
				  locId = locId1.toString();}
				// @@

				if(locId.length() == 1)

				//{locId = locId+"  ";	}
				// @@ Replaced by G.Srinivas for TogetherJ on 20050105
				   {locId1.append(locId);
					locId1.append(" ");
					locId = locId1.toString();}	
				// @@

				//sMode = locId+"[" +sMode+ "]"+"[" +temp+"]"; 
				// @@ Replaced by G.Srinivas for TogetherJ on 20050105
				  sMode1.append(locId);
				  sMode1.append("[" );
				  sMode1.append(sMode);
				  sMode1.append( "]");
				  sMode1.append("[" );
				  sMode1.append(temp);
				  sMode1.append( "]");
				  sMode = sMode1.toString();
                // @@
                vec.add(sMode);
			}
        }
        catch(SQLException sqEx)
        {
		
			sqEx.printStackTrace();
			//Logger.error(FILE_NAME, "[getLocationIds(whereclause)] -> "+sqEx.toString());
      logger.error(FILE_NAME+ "[getLocationIds(whereclause)] -> "+sqEx.toString());
            throw new EJBException(sqEx.toString());
        }
        catch(Exception sqEx)
        {
			sqEx.printStackTrace();
			//Logger.error(FILE_NAME, "[getLocationIds(whereclause)] -> "+sqEx.toString());
      logger.error(FILE_NAME+ "[getLocationIds(whereclause)] -> "+sqEx.toString());
            throw new EJBException(sqEx.toString());
        }
        finally
        {
			ConnectionUtil.closeConnection(connection, cstmt, rs);
        }
        return vec;
    }
    
    
  //Added by rk for buyrates & sell rates
    public ArrayList getLocationIds1(String searchString,String operation,String terminalId,String shimode)
    {
        ArrayList      vec              = new ArrayList();
        Connection     connection       = null;
        //Statement      stmt             = null;//Commented By RajKumari on 30-10-2008 for Connection Leakages.
        ResultSet      rs               = null;
        String		     sMode			      = "";	
        String		     temp				      = "";	
        String		     locId			      = "";	
        CallableStatement cstmt         = null;
        String         locationIds      = "";
		
        try
        {
		
            if(searchString==null)
                searchString = "";
            else
                locationIds  = searchString.replaceAll(",","','");
                
            connection = this.getConnection();            
            cstmt        = connection.prepareCall("{ ?= call QMS_SETUPS.get_location_buyrates(?,?,?,?)}");
            cstmt.registerOutParameter(1,OracleTypes.CURSOR);
            cstmt.setString(2,terminalId);
            cstmt.setString(3,operation);
            cstmt.setString(4,locationIds);
            cstmt.setString(5,shimode);
            cstmt.execute();
            rs  =  (ResultSet)cstmt.getObject(1);
            while(rs.next())
            {
      
              
              StringBuffer   sMode1            = new StringBuffer();
              StringBuffer   locId1           = new StringBuffer();
              
      
              locId = rs.getString(1);	
              temp = rs.getString(2);
              sMode = rs.getString(3);
              
               if(sMode==null)
               {  
                  sMode="   ";
               }
                          
              if(sMode!=null)
              { 
                if(sMode.equals("7"))	{sMode="AST";}
                if(sMode.equals("1"))	{sMode="A  ";}
                if(sMode.equals("2"))	{sMode=" S ";}		
                if(sMode.equals("3"))	{sMode="AS ";}
                if(sMode.equals("4"))	{sMode="  T";}
                if(sMode.equals("5"))	{sMode="A T";}
                if(sMode.equals("6"))	{sMode=" ST";}
              }	
                
              if(locId.length() == 2)	
              {
                locId1.append(locId);
                locId1.append(" ");
                locId = locId1.toString();}
                if(locId.length() == 1)
                {
                  locId1.append(locId);
                  locId1.append(" ");
                  locId = locId1.toString();
                }				
                sMode1.append(locId);
                sMode1.append("[" );
                sMode1.append(sMode);
                sMode1.append( "]");
                sMode1.append("[" );
                sMode1.append(temp);
                sMode1.append( "]");
                sMode = sMode1.toString();
                vec.add(sMode);
            }
        }
        catch(SQLException sqEx)
        {
            sqEx.printStackTrace();
            //Logger.error(FILE_NAME, "[getLocationIds(whereclause)] -> "+sqEx.toString());
            logger.error(FILE_NAME+ "[getLocationIds(whereclause)] -> "+sqEx.toString());
            throw new EJBException(sqEx.toString());
        }
        catch(Exception sqEx)
        {
            sqEx.printStackTrace();
            //Logger.error(FILE_NAME, "[getLocationIds(whereclause)] -> "+sqEx.toString());
            logger.error(FILE_NAME+ "[getLocationIds(whereclause)] -> "+sqEx.toString());
            throw new EJBException(sqEx.toString());
        }
        finally
        {
            ConnectionUtil.closeConnection(connection, cstmt, rs);
        }
        return vec;
    }
    //@@Added by Yuvraj for CR_DHLQMS_1006 
  /**
   * This method is used to retrive the Location Ids based on CountryId from the database.
   * It    returns a ArrayList that contains country Id's. It  throws javax.ejb.EJBException, when it is not able to reterive the data from the database.
   * @return the ArrayList    which contains  coountry Ids.
   * @exception                  javax.ejb.EJBException
   * @param searchString,countryId
   */
    public ArrayList getLocationIdsForCountry(String searchString,String countryId,String terminalId,String shipMode)
    {
        ArrayList      vec              = new ArrayList();
        Connection     connection       = null;
        Statement      stmt             = null;
        ResultSet      rs               = null;
        String		     sMode		        = "";	
        String		     temp			        = "";	
        String		     locId		        = "";
        String         sqlQuery         = "";
       // CallableStatement cstmt         = null;
        
        try
        {
          if(searchString!=null && searchString.trim().length()!=0)
          {
            if("2".equalsIgnoreCase(shipMode))
              searchString = " AND PORTID LIKE '"+searchString+"%' ";
            else
              searchString = " AND LOCATIONID LIKE '"+searchString+"%' ";
          }
          else
            searchString = "";
          if(searchString==null)
                  searchString = "";
          
          connection = this.getConnection();
          stmt = connection.createStatement();
          if("2".equalsIgnoreCase(shipMode))
          {
            sqlQuery  = " SELECT PORTID,PORTNAME,COUNTRYID FROM FS_FRS_PORTMASTER "+
                        " WHERE COUNTRYID LIKE '"+countryId+"%'" + searchString +
                      " AND ( INVALIDATE='F' OR INVALIDATE IS NULL) ORDER BY PORTID ";
          }
          else
          {
            sqlQuery = "SELECT LOCATIONID,LOCATIONNAME,SHIPMENTMODE FROM  FS_FR_LOCATIONMASTER"+
                      " WHERE COUNTRYID LIKE '"+countryId+"%'" + searchString +
                      " AND ( INVALIDATE='F' OR INVALIDATE IS NULL) ORDER BY LOCATIONID ";
          }
          rs = stmt.executeQuery(sqlQuery);
          
         /*     cstmt        = connection.prepareCall("{ ?= call QMS_SETUPS.GET_LOCATIONIDSFORCOUNTRY(?,?,?,?)}");
              cstmt.registerOutParameter(1,OracleTypes.CURSOR);
              cstmt.setString(2,terminalId);
              cstmt.setString(3,operation);
              cstmt.setString(4,(searchString+"%"));
              cstmt.setString(5,(countryId+"%"));          
              
              cstmt.execute();
              rs  =  (ResultSet)cstmt.getObject(1);*/
          while(rs.next())
          {
            StringBuffer   sMode1            = new StringBuffer();
            StringBuffer   locId1            = new StringBuffer();

            locId = rs.getString(1);	
            temp  = rs.getString(2);
            sMode = rs.getString(3);
            
            if(!"2".equalsIgnoreCase(shipMode))
            {
                  if(sMode==null)
                      sMode="   ";
                            
                if(sMode!=null)
                { 
                  if(sMode.equals("7"))	{sMode="AST";}
                  if(sMode.equals("1"))	{sMode="A  ";}
                  if(sMode.equals("2"))	{sMode=" S ";}		
                  if(sMode.equals("3"))	{sMode="AS ";}
                  if(sMode.equals("4"))	{sMode="  T";}
                  if(sMode.equals("5"))	{sMode="A T";}
                  if(sMode.equals("6"))	{sMode=" ST";}
                }
            }
            if(locId.length() == 2)	
            {
              locId1.append(locId);
              locId1.append(" ");
              locId = locId1.toString();
            }
            // @@
    
            if(locId.length() == 1)
            {
              locId1.append(locId);
              locId1.append(" ");
              locId = locId1.toString();
            }	
            
              
            sMode1.append(locId);
            sMode1.append("[" );
            sMode1.append(sMode);
            sMode1.append( "]");
            sMode1.append("[" );
            sMode1.append(temp);
            sMode1.append( "]");
            sMode = sMode1.toString();
            
            vec.add(sMode);
          }
        }
        catch(SQLException sqEx)
        {sqEx.printStackTrace();
            //Logger.error(FILE_NAME, "[getLocationIds(whereclause)] -> "+sqEx.toString());
            logger.error(FILE_NAME+ "[getLocationIds(whereclause)] -> "+sqEx.toString());
            throw new EJBException(sqEx.toString());
        }
        catch(Exception sqEx)
        {sqEx.printStackTrace();
            //Logger.error(FILE_NAME, "[getLocationIds(whereclause)] -> "+sqEx.toString());
            logger.error(FILE_NAME+ "[getLocationIds(whereclause)] -> "+sqEx.toString());
            throw new EJBException(sqEx.toString());
        }
        finally
        {
            ConnectionUtil.closeConnection(connection, stmt, rs);
        }
        
        return vec;
    }
//@@Yuvraj
  /**
   * @param    String terminalId
   *@return     ArrayList LocationIds
   */
  public ArrayList  getLocationsUnderTerminal(String terminalId,String accessLevel,String searchString,String shipmentMode) throws EJBException
  {
    ArrayList   locationIds         =   null;
    
    Connection  conn                =   null;
    Statement   stmt                =   null;
    ResultSet   rs                  =   null;
    
    String      locationSubQuery    =   null;
    String      locationAccess_sql  =   null;
    
    String      searchQuery         =   "";
    
    String      shipMode            =   null;
    
    StringBuffer  sb                =   null;
    
    try
    {
      conn          =   this.getConnection();
      stmt          =   conn.createStatement();
      
      if(searchString!=null && searchString.trim().length()!=0)
          searchQuery   = " AND LM.LOCATIONID LIKE '"+searchString+"%'";
      
      locationIds   =   new ArrayList();
      
      if("H".equalsIgnoreCase(accessLevel))
        locationSubQuery     =    "SELECT TERMINALID FROM FS_FR_TERMINALMASTER";
      else
        locationSubQuery     =    "SELECT CHILD_TERMINAL_ID TERM_ID FROM FS_FR_TERMINAL_REGN CONNECT BY PRIOR CHILD_TERMINAL_ID= "+
                                  "PARENT_TERMINAL_ID START WITH PARENT_TERMINAL_ID='"+terminalId+"' UNION SELECT TERMINALID FROM "+
                                  "FS_FR_TERMINALMASTER WHERE TERMINALID='"+terminalId+"'";
      
      locationAccess_sql     =    "SELECT TL.LOCATIONID,LM.LOCATIONNAME FROM FS_FR_TERMINALLOCATION TL,FS_FR_LOCATIONMASTER LM WHERE "+ 
                                  "TL.LOCATIONID=LM.LOCATIONID AND TL.TERMINALID IN("+locationSubQuery+") "+searchQuery;
      
      if(shipmentMode!=null)
      { 
        if(shipmentMode.equals("7"))	{shipMode="AST";}
        else if(shipmentMode.equals("1"))	{shipMode="A  ";}
        else if(shipmentMode.equals("2"))	{shipMode=" S ";}		
        else if(shipmentMode.equals("3"))	{shipMode="AS ";}
        else if(shipmentMode.equals("4"))	{shipMode="  T";}
        else if(shipmentMode.equals("5"))	{shipMode="A T";}
        else if(shipmentMode.equals("6"))	{shipMode=" ST";}
      }	
      
      rs                     =     stmt.executeQuery(locationAccess_sql);
      
      while(rs.next())
      {
        sb                =   new StringBuffer("");
        sb.append(rs.getString("LOCATIONID")).append("[").append(shipMode).append("][").append(rs.getString("LOCATIONNAME")).append("]");
        locationIds.add(sb.toString());
      }
      
      
    }
    catch(EJBException ejb)
    {
      //Logger.error(FILE_NAME,"Error while Fetching Location Ids:"+ejb.toString());
      logger.error(FILE_NAME+"Error while Fetching Location Ids:"+ejb.toString());
      ejb.printStackTrace();
      throw new EJBException(ejb);
    }
    catch(Exception e)
    {
      //Logger.error(FILE_NAME,"Error while Fetching Location Ids:"+e.toString());
      logger.error(FILE_NAME+"Error while Fetching Location Ids:"+e.toString());
      e.printStackTrace();
      throw new EJBException(e);
    }
    finally
    {
      ConnectionUtil.closeConnection(conn,stmt,rs);
    }
    return locationIds;
  }
	/**
   * @param    String portId
   *@return     portMasterObj as PortMasterJSPBean
   */
	public	PortMasterJSPBean getPortMasterDetails(String portId)
	{
		Connection connection	=	null;
		Statement stmt			=	null;
		ResultSet rs			=	null;
		PortMasterJSPBean portMasterObj = null;
		try
		{
			String sqlQuery	=	"SELECT	*	FROM FS_FRS_PORTMASTER WHERE PORTID = '"+portId+"' AND (INVALIDATE='F' OR INVALIDATE IS NULL)";//13089 ISSUE
			connection = operationsImpl.getConnection();
			stmt		=	connection.createStatement();
			rs			=	stmt.executeQuery(	sqlQuery );
			if(	rs.next())
			{
				portMasterObj = new PortMasterJSPBean();
				portMasterObj.setPortId(rs.getString(1));
				portMasterObj.setPortName(rs.getString(2));
				portMasterObj.setCountryId(rs.getString(3));
				portMasterObj.setDescription(rs.getString(4));
				portMasterObj.setScheduleD(rs.getString(6));
				portMasterObj.setScheduleK(rs.getString(7));
			}
			else{
				return null;}
		}
		catch(Exception	sqle)
		{
			
			//Logger.error(FILE_NAME,"Exception	in getPortMasterDetails() method of	ETSSetupSessionBean  :	"+sqle.toString());
      logger.error(FILE_NAME+"Exception	in getPortMasterDetails() method of	ETSSetupSessionBean  :	"+sqle.toString());
      throw new EJBException(sqle.toString());
		}
		finally
		{
			try
			{
				if(rs != null ){
					rs.close();}
				if(stmt!=	null ){
					stmt.close();}
				if(connection != null ){
					connection.close();}
			}
			catch(SQLException	sqle)
			{
				//Logger.error(FILE_NAME,"Exception in	finally	block of  getPortMasterDetails() method	of ETSSetupSessionBean : "+sqle.toString());
        logger.error(FILE_NAME+"Exception in	finally	block of  getPortMasterDetails() method	of ETSSetupSessionBean : "+sqle.toString());
			}
		}
	return portMasterObj;
	}

	/**
   * @param  PortMasterJSPBean   portMasterObject,
   * @param  ESupplyGlobalParameters loginDetails
	 *@return  boolean
   */
	public	 boolean addPortMasterDetails(PortMasterJSPBean portMasterObject,ESupplyGlobalParameters loginDetails)
	{
		Connection connection	=	null;
		Statement stmt			=	null;
		//ResultSet rs			=	null;//Commented By RajKumari on 30-10-2008 for Connection Leakages.
		try
		{
				connection	=	operationsImpl.getConnection();
				stmt		=	connection.createStatement();
				String portId		=	portMasterObject.getPortId();
				String portName		=	portMasterObject.getPortName();
				String countryId	=	portMasterObject.getCountryId();
				String description	=	portMasterObject.getDescription();
				String scheduleD	=	portMasterObject.getScheduleD();
				String scheduleK	=	portMasterObject.getScheduleK();
				String	sqlInsert	=	"INSERT INTO	FS_FRS_PORTMASTER(PORTID,PORTNAME,COUNTRYID,DESCRIPTION,SHEDULED,SHEDULEK,TERMINALID ,INVALIDATE) VALUES('" +portId+"','"+portName+"','"+countryId+"','"+description+"','"+scheduleD+"','"+scheduleK+"','"+loginDetails.getTerminalId()+"','F')";//added by rk
					//stmt.executeQuery(sqlInsert);
				//For 10g server implementation
				stmt.executeUpdate(sqlInsert);
				operationsImpl.setTransactionDetails( loginDetails.getTerminalId(),
														loginDetails.getUserId(),
														"PortMaster",
														portId,
														loginDetails.getLocalTime(),
														"ADD");
				return true;
		}catch(SQLException sqle) {
			//Logger.error(FILE_NAME,"SQL	Exception in addPortMasterDetails() method	of ETSSetupSessionBean : " +sqle.toString());
      logger.error(FILE_NAME+"SQL	Exception in addPortMasterDetails() method	of ETSSetupSessionBean : " +sqle.toString());
			throw new javax.ejb.EJBException("SQL Exception in addPortMasterDetails() method	of ETSSetupSessionBean : "+sqle.toString());
		}
		catch(Exception	ex)	{
			//Logger.error(FILE_NAME,"Exception in addPortMasterDetails() method	of ETSSetupSessionBean : " + ex.toString());
      logger.error(FILE_NAME+"Exception in addPortMasterDetails() method	of ETSSetupSessionBean : " + ex.toString());
			throw new javax.ejb.EJBException("Remote Exception in	addPortMasterDetails()	method of ETSSetupSessionBean	: " + ex.toString());
		}
		finally  {
			try	{
			if(stmt !=	null)
				{stmt.close();}
			/*if(rs != null){
				rs.close();}*///Commented By RajKumari on 30-10-2008 for Connection Leakages.
			if(connection != null ){
				connection.close();}
	    }catch(SQLException	sqle)	{
			//Logger.error(FILE_NAME,"Exception in finally block	of addPortMasterDetails() method of ETSSetupSessionBean : "+sqle.toString());
      logger.error(FILE_NAME+"Exception in finally block	of addPortMasterDetails() method of ETSSetupSessionBean : "+sqle.toString());
		}
		}
	}

	/**
   * @param     PortMasterJSPBean portMasterObj
   * @param     ESupplyGlobalParameters loginDetails
   *@return     boolean
   
   */
	public	boolean	updatePortMasterDetails(PortMasterJSPBean portMasterObj,ESupplyGlobalParameters loginDetails)
	{
		Connection	connection	=	null;
		Statement	stmt		=	null;
		try
		{
			connection	=	operationsImpl.getConnection();
			stmt		=	connection.createStatement();
			String portId		=	portMasterObj.getPortId();
			String portName		=	portMasterObj.getPortName();
			String countryId	=	portMasterObj.getCountryId();
			String description	=	portMasterObj.getDescription();
			String scheduleD	=   portMasterObj.getScheduleD();
			String scheduleK	=	portMasterObj.getScheduleK();
			String sqlUpdate	=	"UPDATE	FS_FRS_PORTMASTER SET PORTNAME ='"+portName+"',COUNTRYID ='"+countryId+"',DESCRIPTION='"+description+"',SHEDULED='"+scheduleD+"',SHEDULEK='"+scheduleK+"' WHERE PORTID='"+portId+"' ";
			stmt.executeUpdate(	sqlUpdate);
			operationsImpl.setTransactionDetails( loginDetails.getTerminalId(),
														loginDetails.getUserId(),
														"PortMaster",
														portId,
														loginDetails.getLocalTime(),
														"MODIFY");
			return true;
		}
		catch (SQLException	sqle )
		{
			
			//Logger.error(FILE_NAME,"Exception in updatePortMasterDetails() method of ETSSetupSessionBean : "+sqle.toString());
      logger.error(FILE_NAME+"Exception in updatePortMasterDetails() method of ETSSetupSessionBean : "+sqle.toString());
			return false;
		}
		catch( Exception cnfe )
		{
			throw new javax.ejb.EJBException("Remote Naming Exception	in updatePortMasterDetails() method	of ETSSetupSessionBean :"+cnfe.toString());
		}
		finally
		{
			try
			{
				if(	stmt !=	null ){
					stmt.close();}
				if(	connection != null ){
					connection.close();}
			}
			catch(SQLException sqle)
			{
				//Logger.error(FILE_NAME,"Exception in	finally	block of updatePortMasterDetails() method of ETSSetupSessionBean : " + sqle.toString());
        logger.error(FILE_NAME+"Exception in	finally	block of updatePortMasterDetails() method of ETSSetupSessionBean : " + sqle.toString());
			}
		}
	}

	/**
   * @param     String	portMasterId
   * @param     ESupplyGlobalParameters loginDetails
   *@return     boolean
   
   */
	public	boolean	deletePortMasterDetails(String	portMasterId,ESupplyGlobalParameters loginDetails)
	{
		Connection	connection	=	null;
		Statement	stmt		=	null;
		//Statement	stmt1		=	null;
		ResultSet	rs      = null;//Added By RajKumari on 30-10-2008 for Connection Leakages.
//@@ Added by subrahmanyam for the wpbn id: 196133 on 02/Feb/10    	
		PreparedStatement pStmtRates = null;
    	ResultSet		  rsRates	= null;
    	PreparedStatement pStmtQuotes = null;
    	ResultSet		  rsQuotes	= null;
//@@ Ended by subrahmanyam for the wpbn id: 196133 on 02/Feb/10    	
    	
		try
		{
			connection	=	operationsImpl.getConnection();
			stmt = connection.createStatement();
			String	sqlQuery	= "SELECT PORTID FROM  FS_FRS_PORTMASTER  WHERE PORTID='"+portMasterId+"'";
//@@ Added by subrahmanyam for the wpbn id: 196133 on 02/Feb/10			
			String ratesChkQry	=	" SELECT COUNT(*) FROM QMS_BUYRATES_DTL BD WHERE (BD.ORIGIN=? OR "+
									" BD.DESTINATION=?) AND BD.ACTIVEINACTIVE IS NULL AND BD.LINE_NO=0";
			String quotesChkQry	=	"SELECT COUNT(*) FROM QMS_QUOTE_MASTER QM WHERE "+
									"( QM.DESTIONATION_PORT=? OR QM.DESTIONATION_PORT=?)";
//@@ Ended by subrahmanyam for the wpbn id: 196133 on 02/Feb/10			
			
			rs = stmt.executeQuery(sqlQuery);//Modified By RajKumari on 30-10-2008 for Connection Leakages.
			if(rs.next())
			{
//@@ Added by subrahmanyam for the wpbn id: 196133 on 02/Feb/10				
				pStmtRates	=	 connection.prepareStatement(ratesChkQry);
				pStmtRates.setString(1, portMasterId);
				pStmtRates.setString(2, portMasterId);
				rsRates	= pStmtRates.executeQuery();
				if(rsRates.next() && rsRates.getInt(1)>0)
				{
					return false;
				}
				else
				{
					pStmtQuotes	= connection.prepareStatement(quotesChkQry);
					pStmtQuotes.setString(1, portMasterId);
					pStmtQuotes.setString(2, portMasterId);	
					rsQuotes	=	pStmtQuotes.executeQuery();
					if(rsQuotes.next() && rsQuotes.getInt(1)>0)
					{
						return false;
					}
					else
					{
						
						String sqlDelete = "DELETE FROM	FS_FRS_PORTMASTER  WHERE PORTID='"+portMasterId+"'";
						stmt.executeUpdate(sqlDelete);
						operationsImpl.setTransactionDetails( loginDetails.getTerminalId(),
																loginDetails.getUserId(),
																"PortMaster",
																portMasterId,
																loginDetails.getLocalTime(),
																"DELETE");
						return true;

					}
				}
//@@ Ended by subrahmanyam for the wpbn id: 196133 on 02/Feb/10				
//@@ Commented by subrahmanyam for the wpbn id: 196133 on 02/Feb/10				
			/*String sqlDelete = "DELETE FROM	FS_FRS_PORTMASTER  WHERE PORTID='"+portMasterId+"'";
				stmt.executeUpdate(sqlDelete);
				operationsImpl.setTransactionDetails( loginDetails.getTerminalId(),
														loginDetails.getUserId(),
														"PortMaster",
														portMasterId,
														loginDetails.getLocalTime(),
														"DELETE");*/
			}
			else
			{
				return false;
			}
			
		}
		catch (	SQLException sqle )
		{
			
			//Logger.error(FILE_NAME,"SQL	Exception in  deletePortMasterDetails() method	of ETSSetupSessionBean : "+sqle.toString());
      logger.error(FILE_NAME+"SQL	Exception in  deletePortMasterDetails() method	of ETSSetupSessionBean : "+sqle.toString());
			return false;
		}
		catch( Exception cnfe )
		{
			throw new javax.ejb.EJBException("Exception in deletePortMasterDetails()	method of ETSSetupSessionBean	: "+cnfe.toString());
		}
		finally
		{
			try
			{
      //Added By RajKumari on 30-10-2008 for Connection Leakages.
        if( rs != null)
        {
          rs.close();
        }
        //Added by Govind on 15-02-2010 for connection Leakages
        if (rsRates != null)
        {
        	rsRates.close();
        }
      //Added by Govind on 15-02-2010 for connection Leakages
        if(rsQuotes!=null)
        {
        	rsQuotes.close();
        }
      //Added by Govind on 15-02-2010 for connection Leakages
        if(pStmtRates!=null)
        {
        	pStmtRates.close();
        }
        //Added by Govind on 15-02-2010 for connection Leakages
        if(pStmtQuotes!=null)
        {
        	pStmtQuotes.close();
        }
        
				if(	stmt !=	null ){
					stmt.close();
					}
				if(	connection != null ){
					connection.close();}
			}
			catch(SQLException sqle)
			{
				//Logger.error(FILE_NAME,"Exception	in finally blo9ck of deletePortMasterDetails()	method of ETSSetupSessionBean	: "+sqle.toString());
        logger.error(FILE_NAME+"Exception	in finally blo9ck of deletePortMasterDetails()	method of ETSSetupSessionBean	: "+sqle.toString());
			}
		}
	}
	/**
   * @param     String routeId
   * @param     String consoleNo
   * @param     String loadDisc
   * @param     String searchStr
	 *@return     portArray as java.util.arraylist
   */
	public ArrayList  getPortIds(String routeId,String consoleNo,String loadDisc,String searchStr,String operation,String terminalId)
	{
		Connection connection	=	null;
		//Statement stmt			=	null;//Commented By RajKumari on 30-10-2008 for Connection Leakages.
		ResultSet rs			=	null;
		ArrayList portArray		=	new ArrayList();
		CallableStatement cstmt = null;
		try
		{
		connection		=	operationsImpl.getConnection();
		//stmt			=	connection.createStatement();//Commented By RajKumari on 30-10-2008 for Connection Leakages.
		//String sqlQuery	=	"	SELECT DISTINCT PORTID,PORTNAME FROM FS_FRS_PORTMASTER  WHERE PORTID LIKE  '"+searchStr+"%' ";
		//String column		="PORTOFLOADING";
		String subQry		="";
		
		
	

		/*if(loadDisc!=null &&loadDisc.equals("portOfLoading"))
		{
			column		=	"PORTOFLOADING ";
		}else
		{
			column		=	"PORTOFDISCHARGE ";
		}
		if(consoleNo!=null&& consoleNo.length()>0)
		{
			subQry="(SELECT ROUTEID FROM FS_FRS_CONSOLEMASTER WHERE CONSOLEID='"+consoleNo+"')";
			sqlQuery= " SELECT "+column+" FROM FS_FRS_ROUTEMASTER WHERE "+column+" LIKE '"+searchStr+"%' AND ROUTEID = "+subQry;
		}
		else if(routeId!=null &&routeId.length()>0 )
		{
			sqlQuery= " SELECT "+column+" FROM FS_FRS_ROUTEMASTER WHERE "+column+" LIKE '"+searchStr+"%' AND ROUTEID = '"+routeId+"'";
		}
		else
		{
			sqlQuery	=	" SELECT DISTINCT PORTID,PORTNAME FROM FS_FRS_PORTMASTER  WHERE PORTID LIKE  '"+searchStr+"%' ";
		}*/
		//rs	=	stmt.executeQuery(sqlQuery);
	cstmt        = connection.prepareCall("{ ?= call QMS_SETUPS.GET_PORT(?,?,?)}");
      cstmt.registerOutParameter(1,OracleTypes.CURSOR);
      cstmt.setString(2,terminalId);
      cstmt.setString(3,(searchStr+"%"));
      cstmt.setString(4,operation);
      cstmt.execute();
      rs  =  (ResultSet)cstmt.getObject(1);
		while(rs.next())
		{
			if(consoleNo!=null&& consoleNo.length()>0)
			{
				portArray.add(rs.getString(1));
			}
			else if(routeId!=null &&routeId.length()>0 )
			{
				portArray.add(rs.getString(1));
			}
			else
			{
				portArray.add(rs.getString(1)+"["+rs.getString(2)+"]");
			}		
		}
		}catch(Exception ex)
		{
			ex.printStackTrace();
			//Logger.error(FILE_NAME,"Exception caught in getPortIds :::: ETSSetupSessionBean :" + ex.toString());
      logger.error(FILE_NAME+"Exception caught in getPortIds :::: ETSSetupSessionBean :" + ex.toString());
      throw new EJBException(ex.toString());
		}
		finally
		{
			try
			{
				if(rs!=null){
		 			rs.close();}
				if(cstmt!=null){
					cstmt.close();}
				if(connection!=null){
					connection.close();}
		}catch(Exception ex)
		{
			//Logger.error(FILE_NAME,"Exception caught in finally getPortIds  :::: ETSSetupSessionBean "  + ex.toString());
      logger.error(FILE_NAME+"Exception caught in finally getPortIds  :::: ETSSetupSessionBean "  + ex.toString());
		}
	}
		return portArray;
	}

	/**
   * 
   * @param loginDetails
   * @return 
   */
 public CodeCustomisationDOB getCodeCustDetails(ESupplyGlobalParameters loginDetails )
  {
      ArrayList allIds        = new ArrayList();
      ArrayList codeIds       = new ArrayList();
      String[] codeValues     = null;
      String   codeCustValues = null;
      String[]  codeNames     =  null;
      int    sMode            =  0;
      try
      {
         /*String	codeId[] = {"VENDOR","CUSTOMER","CARRIERCONTRACT","CUSTOMERCONTRACT","CARRIERSCHEDULE","AIRPRQ",
                     "SEAPRQ","TRUCKPRQ","HOUSEDOCUMENT","AIRINVOICE","SEAINVOICE","TRUCKINVOICE","AIRDELIVERYORDER","SEADELIVERYORDER","TRUCKDELIVERYORDER","MASTERDOCUMENT","HBL","CONSOLE","PRS",
                     "DRS","CONSIGNEMENT","MANIFEST","ROUTE","WAREHOUSE","TRIPSHEET",
                     "VENDORCONTRACT"};*/
          String	codeId[] = {"VENDOR","CUSTOMER","QUOTE"};
 
                     
        CodeCustomisationDOB  codeCust          = new CodeCustomisationDOB();
        String companyId   = loginDetails.getCompanyId();
		// @@ 'TerminalId' is replaced with 'TerminalId1' for TogetherJ by Suneetha on 12 Jan 05
        String terminalId1  = loginDetails.getTerminalId();
        String locationId  = loginDetails.getLocationId();    
        sMode              = loginDetails.getShipmentMode();
        
        int codeIdLen	=	codeId.length;
        for(int i=0;i<codeIdLen;i++)
        {
          codeIds.add(codeId[i]);
            codeValues     = new String[3];

          if(codeId[i].equals("VENDOR") || codeId[i].equals("CUSTOMER")||codeId[i].equals("HBL")||codeId[i].equals("CONSIGNEMENT"))
          {
             codeCustValues = "COMPANY,LOCATION";
          } 
          if(codeId[i].equals("AIRINVOICE") || codeId[i].equals("SEAINVOICE") || codeId[i].equals("TRUCKINVOICE"))
          {
                 codeCustValues = "COMPANY,LOCATION,SHIPPERNAME";		  
          }	
          if(codeId[i].equals("CARRIERCONTRACT"))
          {
              codeCustValues="CARRIER,ORIGIN,DESTINATION";
          }
          if(codeId[i].equals("CUSTOMERCONTRACT"))
          {
              codeCustValues="COMPANYID,FREIGHTCOMPANY";
          }
          if(codeId[i].equals("CARRIERSCHEDULE"))
          {
              codeCustValues="ORIGIN,DAY,MONTH";
          }
          if(codeId[i].equals("AIRPRQ") || codeId[i].equals("SEAPRQ") || codeId[i].equals("TRUCKPRQ"))
          {
              codeCustValues="SHIPMENTMODE,ORIGIN,DESTINATION,SHIPPERNAME";
          }
          if(codeId[i].equals("HOUSEDOCUMENT"))
          {
              codeCustValues="COMPANY,ORIGIN,DESTINATION,SHIPPER,CONSIGNEE";
          }
          if(codeId[i].equals("AIRDELIVERYORDER") || codeId[i].equals("SEADELIVERYORDER") || codeId[i].equals("TRUCKDELIVERYORDER"))
          {
              codeCustValues="DESTLOCATION,MONTHYEAR,CONSIGNEE";
          }
          if(codeId[i].equals("CONSOLE"))
          {
              codeCustValues="CONSOLETYPE,PORTOFLOADING,PORTOFDISCHARGE,LOCATION";
          }
          if(codeId[i].equals("PRS") || codeId[i].equals("DRS"))
          {
              codeCustValues="TRUCKID,DAY,MONTH";
          }
          if(codeId[i].equals("MANIFEST"))
          {
              codeCustValues="MANIFESTTYPE,ORIGIN";
          }
          if(codeId[i].equals("ROUTE"))
          {
              codeCustValues="ORIGINLOCATION,DESTLOCATION";
          }
          if(codeId[i].equals("QUOTE"))
          {
              codeCustValues="CUSTOMER,ORIGIN,DESTLOCATION";
          }
          if(codeId[i].equals("POMS")||codeId[i].equals("IMS"))
          {
              codeCustValues="CONSIGNEE,DESTLOCATION,SHIPPER,ORIGINLOCATION";
          }
          if(codeId[i].equals("WAREHOUSE"))
          {
              codeCustValues="TERMINALID,DAY,MONTH";
          }
          if(sMode==2)
          {
              if(codeId[i].equals("ROUTE"))
              {
                  codeCustValues="PORTOFLOADING,PORTOFDISCHARGE,ORIGIN LOCATION";
              }
          }
          if(codeId[i].equals("TRIPSHEET"))
          {
               codeCustValues="ORIGIN,DESTINATION,COMPANY";
          }
          if(codeId[i].equals("VENDORCONTRACT"))
          {
               codeCustValues="VENDORID,ORIGIN,COMPANYID";
          }
  

                codeValues[0] = codeId[i];
                codeValues[1] = codeCustValues;
                allIds.add(codeValues);
        }
        codeCust.setCompanyId(companyId);
        codeCust.setTerminalId(terminalId1);
        codeCust.setLocationId(locationId);
        codeCust.setIdsList(allIds);
        return	codeCust;

      }
      catch( Exception e)
      {
        e.printStackTrace();
		
    
        //Logger.error(FILE_NAME,"ETransHOSuperUserSetupSessionBean.java :	getCodeCustomisationDetails	: "+e);
        logger.error(FILE_NAME+"ETransHOSuperUserSetupSessionBean.java :	getCodeCustomisationDetails	: "+e);
        return null;
      }
      	
  }

  /**
   * 
   * @param operation
   * @param sMode
   * @param terminalId
   * @param searchString
   * @return 
   */
		public ArrayList	getCodeCustomizationCodeType(String operation,int sMode,String terminalId,String searchString)//new
	{
		Connection	connection		=	null;
		Statement	  stmt			=	null;
		ResultSet	  rs			=	null;
		String			sql			=	null;
	    ArrayList codeNames = new ArrayList();    
		CallableStatement cstmt = null;
		int			 len			=	0;

		if(searchString!=null && (!searchString.equals(""))) 
			{searchString="ETS"+searchString+"%ID";}
		else
			{searchString="";}

		try
    {

      /*String	codeId[] = {"VENDOR","CUSTOMER","CARRIERCONTRACT","CUSTOMERCONTRACT","CARRIERSCHEDULE","AIRPRQ",
                     "SEAPRQ","TRUCKPRQ","HOUSEDOCUMENT","AIRINVOICE","SEAINVOICE","TRUCKINVOICE","AIRDELIVERYORDER","SEADELIVERYORDER","TRUCKDELIVERYORDER","HBL","CONSOLE","PRS",
                     "DRS","CONSIGNEMENT","MANIFEST","ROUTE","WAREHOUSE","TRIPSHEET",
                     "VENDORCONTRACT"};*/
       String	codeId[] = {"VENDOR","CUSTOMER","QUOTE"};

		  connection	=	operationsImpl.getConnection();
			stmt		    =	connection.createStatement();
      
  		if(searchString!=null && (!searchString.equals("")))
		{
   
  		sql	=	"SELECT	CODEIDNAME NEWID FROM  FS_FR_CONFIGPARAM WHERE  TERMINALID='"+terminalId+"'"+" AND CODEIDNAME LIKE '"+searchString+"'"; //new
      //Commented by Sanjay on 29-12-2005 as per removed string ETS and ID from the string codeIdName
      //sql	=	"SELECT	SUBSTR(CODEIDNAME,4,LENGTH(CODEIDNAME)-5) NEWID FROM  FS_FR_CONFIGPARAM WHERE  TERMINALID='"+terminalId+"'"+" AND CODEIDNAME LIKE '"+searchString+"'"; //new
		}
		else
		{
    sql =	"SELECT	CODEIDNAME NEWID FROM  FS_FR_CONFIGPARAM WHERE  TERMINALID='"+terminalId+"'";
		//Commented by Sanjay on 29-12-2005 as per removed string ETS and ID from the string codeIdName
    //sql =	"SELECT	SUBSTR(CODEIDNAME,4,LENGTH(CODEIDNAME)-5) NEWID FROM  FS_FR_CONFIGPARAM WHERE  TERMINALID='"+terminalId+"'";
		}
		if(searchString==null)
        searchString ="";
			
			rs			=	stmt.executeQuery(sql);
			while(rs.next())
				{len++;}


			rs			=   null;
			//rs			=	stmt.executeQuery(sql);
			  cstmt        = connection.prepareCall("{ ?= call QMS_SETUPS.GET_CODECUSTOMIZATION(?,?,?)}");
              cstmt.registerOutParameter(1,OracleTypes.CURSOR);
              cstmt.setString(2,terminalId);       
              cstmt.setString(3,operation);
              cstmt.setString(4,(searchString+"%"));
              cstmt.execute();
              rs  =  (ResultSet)cstmt.getObject(1);

		  if(operation.equals("Add"))
		  {
			  int codeIdLen	=	codeId.length;
			  for(int i=0; codeId!=null && i < codeIdLen; i++) 
			  {    
				 codeNames.add(codeId[i]);
			  }
		
			  while(rs.next())
			  {
				  if(codeNames.contains(rs.getString(1)))
				  {
					codeNames.remove(codeNames.indexOf(rs.getString(1)));                     
				  }
			  }
		   }  
		   else if(operation.equals("Modify") || operation.equals("View"))
		   {
			 
			  while(rs.next())
					{codeNames.add(rs.getString(1));  }                   
				 
		   }	
		}
		catch(	SQLException sqle)
		{
			//Logger.error(FILE_NAME,"ETransHOSuperUserSetupSessionBean.java :	getCodeType	: Sql Exception	 : "+sqle);
      logger.error(FILE_NAME+"ETransHOSuperUserSetupSessionBean.java :	getCodeType	: Sql Exception	 : "+sqle);
			sqle.printStackTrace();  
			
			return null;
		}
		catch( Exception e)
		{
			//Logger.error(FILE_NAME,"ETransHOSuperUserSetupSessionBean.java :	getCodeType	: :	"+e);
      logger.error(FILE_NAME+"ETransHOSuperUserSetupSessionBean.java :	getCodeType	: :	"+e);
			e.printStackTrace();
			return null;
		}
		finally
		{
				ConnectionUtil.closeConnection(connection, cstmt, rs);
        ConnectionUtil.closeStatement(stmt);//Added By RajKumari on 30-10-2008 for Connection Leakages.
    }
		return	codeNames;
	} // End ofgetCodeType

	/**
   * 
   * @param codeType
   * @param terminalId
   * @return 
   */
	public CodeCustomiseJSPBean	getCodeCustomisationDetails(String codeType,String terminalId)
	{
		Connection				connection	  = null;
		Statement				stmt		  = null;
		ResultSet				rs			  = null;
		String[]				valIndctrs    = new String[3];
		String[]				valGrps       = new String[3];
		int[]					valLens       = new int[3];
	    CodeCustomiseJSPBean	codeCust  = null;
    
		try
		{
			connection	=	operationsImpl.getConnection();
			stmt		=	connection.createStatement();
			//String sql = "SELECT CODEIDNAME,VALGRP1,VALLEN1,VALDESC1,VALIND1,VALGRP2,VALLEN2,VALDESC2,VALIND2,VALGRP3,VALLEN3,VALDESC3,VALIND3,NOOFGRPS,STARTINGSLNO,SHIPMENTMODE,TERMINALID,DEFLT_CUS_FLAG FROM	FS_FR_CONFIGPARAM WHERE	CODEIDNAME='"+codeType+"' AND TERMINALID='"+terminalId+"'";
      
      String sql = "SELECT CODEIDNAME,VALGRP1,VALLEN1,VALDESC1,VALIND1,VALGRP2,VALLEN2,VALDESC2,VALIND2,VALGRP3,VALLEN3,VALDESC3,VALIND3,NOOFGRPS,STARTINGSLNO,SHIPMENTMODE,TERMINALID,DEFLT_CUS_FLAG FROM	FS_FR_CONFIGPARAM WHERE	CODEIDNAME='"+codeType+"'";
      
			rs = stmt.executeQuery(sql);
			if(rs.next())
			{
				codeCust = new CodeCustomiseJSPBean();
				codeCust.setCodeIdName(rs.getString(1));
				codeCust.setValGrp1(rs.getString(2));
		       	codeCust.setValLen1(rs.getInt(3));
				codeCust.setValDesc1(rs.getString(4));
				codeCust.setValInd1(rs.getString(5));
				codeCust.setValGrp2(rs.getString(6));
				codeCust.setValLen2(rs.getInt(7));
				codeCust.setValDesc2(rs.getString(8));
				codeCust.setValInd2(rs.getString(9));
				codeCust.setValGrp3(rs.getString(10));
				codeCust.setValLen3(rs.getInt(11));
				codeCust.setValDesc3(rs.getString(12));
				codeCust.setValInd3(rs.getString(13));
				codeCust.setNoOfGrps(rs.getInt(14));
				codeCust.setStartingSlNo(rs.getLong(15));
				codeCust.setShipmentMode(rs.getInt(16));
				codeCust.setTerminalId(rs.getString(17));
				codeCust.setCustFlag(rs.getString(18));
				valIndctrs[0] = codeCust.getValInd1();
				valIndctrs[1] = codeCust.getValInd2();
				valIndctrs[2] = codeCust.getValInd3();

				codeCust.setValInds(valIndctrs);

				valGrps[0] = codeCust.getValGrp1();
				valGrps[1] = codeCust.getValGrp2();
				valGrps[2] = codeCust.getValGrp3();

				codeCust.setValGrps(valGrps);

				valLens[0] = codeCust.getValLen1();
		//Logger.info(FILE_NAME,"valLens[0]:  "+valLens[0]);        
				valLens[1] = codeCust.getValLen2();
		//Logger.info(FILE_NAME,"valLens[1]:  "+valLens[1]);                
				valLens[2] = codeCust.getValLen3();
		//Logger.info(FILE_NAME,"valLens[2]:  "+valLens[2]);                

				codeCust.setValLens(valLens);

			}
		}
		catch(	SQLException sqle)
		{
  		//Logger.error(FILE_NAME,"ETransHOSuperUserSetupSessionBean.java :	SQL	Exception in 'getCodeCustomisationDetails' : "+sqle);
      logger.error(FILE_NAME+"ETransHOSuperUserSetupSessionBean.java :	SQL	Exception in 'getCodeCustomisationDetails' : "+sqle);
      sqle.printStackTrace();
	 
			return null;
		}
		catch( Exception e)
		{
			//Logger.error(FILE_NAME,"ETransHOSuperUserSetupSessionBean.java :	getCodeCustomisationDetails	: "+e);
      logger.error(FILE_NAME+"ETransHOSuperUserSetupSessionBean.java :	getCodeCustomisationDetails	: "+e);
 			e.printStackTrace(); 
			
			return null;
		}
		finally
		{
      ConnectionUtil.closeConnection(connection, stmt, rs);		
    }
      
		return	codeCust;
	} //getCodeCustomisationDetails

	/**
   * 
   * @param CodeCust
   * @param loginDetails
   * @return 
   */
  public int	updateCodeCustomisationDetails(CodeCustomiseJSPBean	CodeCust,ESupplyGlobalParameters loginDetails )
	{
		Connection				connection  = null;
		PreparedStatement		pstmt		= null;
		//ResultSet				rs			= null;//Commented By RajKumari on 30-10-2008 for Connection Leakages.
		String					codeIdName	= null;
		String					codeName	= null;
		int						n           = 0;
		String[]				valGrps     = null;
		int[]					valLens     = null;
		String[]				valInds     = null;
    
		try
		{
			//Logger.info(FILE_NAME,"comes for modification man");
			connection	=	operationsImpl.getConnection();
			codeIdName	=	CodeCust.getCodeIdName();
			pstmt		=	connection.prepareStatement("UPDATE	FS_FR_CONFIGPARAM SET VALGRP1=?,VALLEN1=?,VALDESC1=?,VALIND1=?,"+
															  "VALGRP2=?,VALLEN2=?,VALDESC2=?,VALIND2=?,"+
															  "VALGRP3=?,VALLEN3=?,VALDESC3=?,VALIND3=?,"+
															  "NOOFGRPS=?,STARTINGSLNO=?,SHIPMENTMODE=?,TERMINALID=?,DEFLT_CUS_FLAG=? WHERE CODEIDNAME=? ");
                                
     
			valGrps   =   CodeCust.getValGrps();
			valLens   =   CodeCust.getValLens();
			valInds   =   CodeCust.getValInds();
        
			pstmt.setString(1,valGrps[0]);
			pstmt.setInt(2,valLens[0]);
			pstmt.setString(3,CodeCust.getValDesc1());
			pstmt.setString(4,valInds[0]);     
			if(valGrps.length >1)
				{pstmt.setString(5,valGrps[1]);}
			else
				{pstmt.setNull(5,Types.VARCHAR);}
//Logger.info(FILE_NAME,"ValGrp222222: "+CodeCust.getValGrp2());           
		  if(valLens.length >1)
			{	pstmt.setInt(6,valLens[1]);}
		  else
				{pstmt.setInt(6,0);}
			pstmt.setString(7,CodeCust.getValDesc2());
//Logger.info(FILE_NAME,"ValInd000: "+CodeCust.getValInd2());       
      if(valInds.length >1)
			{pstmt.setString(8,valInds[1]);}
      else
 			{ pstmt.setNull(8,Types.VARCHAR);}
			 if(valGrps.length >2)
          {pstmt.setString(9,valGrps[2]);}
       else
          {pstmt.setNull(9,Types.VARCHAR);}
      if(valLens.length >2)
          { pstmt.setInt(10,valLens[2]);}
      else
         { pstmt.setInt(10,0);}
  		pstmt.setString(11,CodeCust.getValDesc3());
      if(valInds.length >2)
          { pstmt.setString(12,valInds[2]);}
      else
         { pstmt.setNull(12,Types.VARCHAR);}

			pstmt.setInt(13,CodeCust.getNoOfGrps());
//Logger.info(FILE_NAME,"ValGrp222222: "+CodeCust.getValGrp2());                       
			pstmt.setLong(14,CodeCust.getStartingSlNo());
			//pstmt.setInt(15,CodeCust.getShipmentMode());
			if(CodeCust.getCodeIdName().equals("ETSAIRPRQID") || CodeCust.getCodeIdName().equals("ETSAIRINVOICEID") || CodeCust.getCodeIdName().equals("ETSTRUCKINVOICEID"))
			{pstmt.setInt(15,1);}
			if(CodeCust.getCodeIdName().equals("ETSSEAPRQID") || CodeCust.getCodeIdName().equals("ETSSEAINVOICEID") || CodeCust.getCodeIdName().equals("ETSSEAINVOICEID"))
			{pstmt.setInt(15,2);}
			if(CodeCust.getCodeIdName().equals("ETSTRUCKPRQID") || CodeCust.getCodeIdName().equals("ETSTRUCKINVOICEID") || CodeCust.getCodeIdName().equals("ETSTRUCKINVOICEID"))
			{pstmt.setInt(15,4);}
			else
			{pstmt.setInt(15,CodeCust.getShipmentMode());}
			pstmt.setString(16,CodeCust.getTerminalId());
      //Logger.info(FILE_NAME,"CustFlag: "+CodeCust.getCustFlag());      
      if(CodeCust.getCustFlag() != null && !CodeCust.getCustFlag().equals("null"))
      			{pstmt.setString(17,CodeCust.getCustFlag());}
      else
      			{pstmt.setString(17,"C");}
			pstmt.setString(18,CodeCust.getCodeIdName());
			n =	pstmt.executeUpdate();
//Logger.info(FILE_NAME,"RowaUpdated: "+n);      
			if(	codeIdName.equals("CustomerContractID")	)
				{codeName	=	"CustContractID";}
			else if( codeIdName.equals("CarrierContractID"))
				{codeName	=	"CaContractID";}
			else
				{codeName	=	codeIdName;}
			operationsImpl.setTransactionDetails( loginDetails.getTerminalId(),
														loginDetails.getUserId(),
														"CodeCustomise",
														codeName,
														loginDetails.getLocalTime(),
														"MODIFY");
		}
		catch(	SQLException sqle)
		{
			//Logger.error(FILE_NAME,"ETransHOSuperUserSetupSessionBean.java :	updateCodeCustomisationDetails : Sql Exception : "+sqle);
      logger.error(FILE_NAME+"ETransHOSuperUserSetupSessionBean.java :	updateCodeCustomisationDetails : Sql Exception : "+sqle);
			sqle.printStackTrace();
			throw new EJBException(sqle.toString());
		}
		catch( Exception e)
		{
			//Logger.error(FILE_NAME,"ETransHOSuperUserSetupSessionBean.java :	updateCodeCustomisationDetails : "+e);
      logger.error(FILE_NAME+"ETransHOSuperUserSetupSessionBean.java :	updateCodeCustomisationDetails : "+e);
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if(	pstmt != null )
					{
          pstmt.close();
          }
				/*if(	rs != null )
					{rs.close();}*///Commented By RajKumari on 30-10-2008 for Connection Leakages.
				if(connection != null )
					{connection.close();}
			}
			catch(SQLException	sqle)
			{
				//Logger.error(FILE_NAME,"ETransHOSuperUserSetupSessionBean.java :	updateCodeCustomisationDetails : Sql Exception:	" +sqle);
        logger.error(FILE_NAME+"ETransHOSuperUserSetupSessionBean.java :	updateCodeCustomisationDetails : Sql Exception:	" +sqle);
			}
		}
    return n;
	} //updateCodeCustomisationDetails	

	/**
   * 
   * @param CodeCust
   * @param loginDetails
   * @return 
   */
	public boolean addCodeCustomisationDetails(CodeCustomiseJSPBean	CodeCust,ESupplyGlobalParameters loginDetails )
	{
		Connection				connection	=	null;
		PreparedStatement		pstmt		=	null;
		Statement			    stmt		=	null;
		String				    codeName	=	null;
		String				    codeIdName	=	null;
		int						noGrps      =	0;
		String[]				valGrps     =	null;
		String[]				valInds     =	null;    
		int[]					valLens     =	null;
		String					custFlag    =   null;
		long					slNo        =	0L;
    
		try
		{
			connection	=	operationsImpl.getConnection();
			codeIdName	=	CodeCust.getCodeIdName();
			noGrps      =	CodeCust.getNoOfGrps();
			custFlag    =	CodeCust.getCustFlag();
			//Commented by Santhosam on 25-NOV-2004 during DHL Integration
			/*pstmt		=	connection.prepareStatement("INSERT	INTO FS_FR_CONFIGPARAM (CODEIDNAME,VALLEN1,VALDESC1,VALIND1,VALLEN2,VALDESC2,VALIND2,VALLEN3,VALDESC3,VALIND3,NOOFGRPS,STARTINGSLNO,SHIPMENTMODE,TERMINALID,DEFLT_CUS_FLAG,VALGRP1,VALGRP2,VALGRP3)VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");      */
			//Integrated by Santhosam on 25-NOV-2004
			pstmt		=	connection.prepareStatement("INSERT	INTO FS_FR_CONFIGPARAM"+
							" (CODEIDNAME, VALLEN1, VALDESC1, VALIND1,"+
							" VALLEN2, VALDESC2, VALIND2, VALLEN3,"+
							" VALDESC3, VALIND3, NOOFGRPS, STARTINGSLNO,"+
							" SHIPMENTMODE, TERMINALID, DEFLT_CUS_FLAG, VALGRP1,"+
							" VALGRP2,VALGRP3"+
							//@@ Avinash added on 20041104 
							" ,OPER_HO_FLAG"+
							//@@ 20041104
							")"+
							" VALUES (?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,"+
							" (SELECT OPER_ADMIN_FLAG FROM FS_FR_TERMINALMASTER WHERE TERMINALID = ? ) )");      
			//@@ 20041104

      
			if(CodeCust.getCodeIdName().equals("ETSHOUSEDOCUMENTID")){
				if(CodeCust.getCustFlag() != null && CodeCust.getCustFlag().equals("D")){
					CodeCust  =   getCodeCustomisationDetails("ETSAIRPRQID",CodeCust.getTerminalId());
					noGrps      = CodeCust.getNoOfGrps();
					custFlag  =   "D";
					slNo      =   CodeCust.getStartingSlNo();
					CodeCust.setCodeIdName("ETSHOUSEDOCUMENTID");
				}
			}else if(CodeCust.getCodeIdName().equals("ETSHBLID")){
				if(CodeCust.getCustFlag() != null && CodeCust.getCustFlag().equals("D")){
					CodeCust  =   getCodeCustomisationDetails("ETSSEAPRQID",CodeCust.getTerminalId());
					noGrps      = CodeCust.getNoOfGrps();
					custFlag  =   "D";
					slNo      =   CodeCust.getStartingSlNo();
					CodeCust.setCodeIdName("ETSHBLID");
				}
			}
			if(CodeCust.getCodeIdName().equals("ETSCONSIGNEMENTID")){
				if(CodeCust.getCustFlag() != null && CodeCust.getCustFlag().equals("D")){
					CodeCust  =   getCodeCustomisationDetails("ETSTRUCKPRQID",CodeCust.getTerminalId());
					noGrps    =	  CodeCust.getNoOfGrps();
					custFlag  =   "D";
					slNo      =   CodeCust.getStartingSlNo();
					CodeCust.setCodeIdName("ETSCONSIGNEMENTID");
				}
			}
			valInds   =   CodeCust.getValInds(); 
			valLens   =   CodeCust.getValLens(); 


			pstmt.setString(1,CodeCust.getCodeIdName());
			pstmt.setInt(2,valLens[0]);
			pstmt.setString(3,CodeCust.getValDesc1());
			pstmt.setString(4,valInds[0]);
			if(valInds.length >1){
				pstmt.setInt(5,valLens[1]);
			}
			else{
				pstmt.setInt(5,0); 
			}
			pstmt.setString(6,CodeCust.getValDesc2());
			if(valInds.length >1){
				pstmt.setString(7,valInds[1]);
			}
			else{
				pstmt.setNull(7,Types.VARCHAR);
			}
			if(valLens.length >1){
				pstmt.setInt(8,valLens[2]);
			}
			else{
				pstmt.setInt(8,0);
			}
			pstmt.setString(9,CodeCust.getValDesc3());
			if(valInds.length >2){
				pstmt.setString(10,valInds[2]);
			}
			else{
				pstmt.setNull(10,Types.VARCHAR);
			}
			pstmt.setInt(11,noGrps);
			pstmt.setLong(12,CodeCust.getStartingSlNo());

			if(CodeCust.getCodeIdName().equals("ETSAIRPRQID") || CodeCust.getCodeIdName().equals("ETSAIRINVOICEID") || CodeCust.getCodeIdName().equals("ETSTRUCKINVOICEID")){
				pstmt.setInt(13,1);
			}
			if(CodeCust.getCodeIdName().equals("ETSSEAPRQID") || CodeCust.getCodeIdName().equals("ETSSEAINVOICEID") || CodeCust.getCodeIdName().equals("ETSSEAINVOICEID")){
				pstmt.setInt(13,2);
			}
			if(CodeCust.getCodeIdName().equals("ETSTRUCKPRQID") || CodeCust.getCodeIdName().equals("ETSTRUCKINVOICEID") || CodeCust.getCodeIdName().equals("ETSTRUCKINVOICEID")){
				pstmt.setInt(13,4);
			}
			else{
				pstmt.setInt(13,CodeCust.getShipmentMode());
			}
			pstmt.setString(14,CodeCust.getTerminalId());
			if(custFlag != null && !custFlag.equals("null")){
				pstmt.setString(15,custFlag);
			}
			else{
				pstmt.setString(15,"C");
			}
            valGrps   =   CodeCust.getValGrps();
		    pstmt.setString(16,valGrps[0]);
		    if(valGrps.length >1){
				pstmt.setString(17,valGrps[1]);
			}
		    else{
				pstmt.setNull(17,Types.VARCHAR);
			}
            if(valGrps.length >2){
				pstmt.setString(18,valGrps[2]);
			}
		    else{
				pstmt.setNull(18,Types.VARCHAR);
			}			
			//Integrated by Santhosam on 25-NOV-2004
			//@@ Avinash added on 20041104
			pstmt.setString(19,CodeCust.getTerminalId());
			//@@ 20041104
       
			int		  n	  =	pstmt.executeUpdate();
			String	sql	=	"UPDATE	FS_FR_OID SET SNO='"+CodeCust.getStartingSlNo()+"' WHERE REFCODE = '"+CodeCust.getCodeIdName()+"'";
			stmt	=	connection.createStatement();
			stmt.executeUpdate(sql);
			if(	codeIdName.equals("CustomerContractID")	){
				codeName	=	"CustContractID";
			}
			else if( codeIdName.equals("CarrierContractID")){
				codeName	=	"CaContractID";
			}
			else{
				codeName	=	codeIdName;			 
			}
      //Added By RajKumari on 30-10-2008 for Connection Leakages.
      if(pstmt!= null)
      {
        pstmt.close();
        pstmt = null;
      }
			//@@ Avinash added on 20050323 SEQUENCE IMPLEMENTATION
			pstmt = connection.prepareStatement(" CREATE SEQUENCE "+ CodeCust.getCodeIdName()+"_SEQ"+
												" INCREMENT BY 1"+
												" START WITH "+CodeCust.getStartingSlNo() +" NOCACHE");

			pstmt.execute();
			//@@ 20050323 SEQUENCE IMPLEMENTATION 
			operationsImpl.setTransactionDetails( loginDetails.getTerminalId(),
														loginDetails.getUserId(),
														"CodeCustomise",
														codeName,
														loginDetails.getLocalTime(),
														"ADD");
		}
    catch(SQLException	sqle)
		{
 			sqle.printStackTrace();
			
			//Logger.error(FILE_NAME,"ETransHOSuperUserSetupSessionBean.java :	setCustomisationDetails	: Sql Exception: "+	sqle );
      logger.error(FILE_NAME+"ETransHOSuperUserSetupSessionBean.java :	setCustomisationDetails	: Sql Exception: "+	sqle );
			return false;
		}
		catch(Exception	e)
		{
			e.printStackTrace();
			
			//Logger.error(FILE_NAME,"ETransHOSuperUserSetupSessionBean.java :	setCustomisationDetails	:" + e);
      logger.error(FILE_NAME+"ETransHOSuperUserSetupSessionBean.java :	setCustomisationDetails	:" + e);
			return false;
		}
		finally
		{
			ConnectionUtil.closeConnection(connection, pstmt);
			//Connection Leakage -------------- 7-DEC-2004 ------------- Santhosam.P
			ConnectionUtil.closeConnection(connection, stmt);
		}
		 return	true;
	}

	/**
   * 
   * @param shipmentMode
   * @param docType
   * @return 
   */
	public ArrayList getReportFormatNames(String shipmentMode,String docType)
    {
        Connection	connection	= null;
        Statement	  stmt        = null;
        ResultSet	  rs          = null;

        ArrayList frmtNames = new ArrayList();
        String sql = "SELECT FRMT_NAME,FRMT_ID FROM FS_FR_DOC_FRMT WHERE  SHPMNT_MODE = "+shipmentMode+" AND DOC_TYPE ='"+docType+"' ORDER BY FRMT_NAME" ;

        try
        {
            connection = this.getConnection();
            stmt = connection.createStatement();
			//Logger.info(FILE_NAME,"SQL:	"+sql);
            rs= stmt.executeQuery(sql);

            while(rs.next()){
              String[] str = new String[2];
              str[0] = rs.getString(1);
              str[1] = rs.getString(2);
                frmtNames.add(str);
          }       
        }
        catch(SQLException sqEx)
        {
          sqEx.printStackTrace();
		  
          //Logger.error(FILE_NAME, "[getCurrencyMasterCurrencyId(whereclause)] -> "+sqEx.toString());
          logger.error(FILE_NAME+ "[getCurrencyMasterCurrencyId(whereclause)] -> "+sqEx.toString());
          throw new EJBException(sqEx.toString());
        }
        finally
        {
          ConnectionUtil.closeConnection(connection, stmt, rs);
        }
        return frmtNames;
    }

	public String validateInfo(String originId,String destinationId,String serviceLevelId,String currencyId,String operation)
	{
	    String            errorCodes 	 = "";
		PreparedStatement pStmt      	 = null;
		Connection		  connection 	 = null;
		
	    String  selQuery2 = null;
	    String  selQuery1 = "  SELECT LOCATIONID FROM FS_FR_LOCATIONMASTER WHERE LOCATIONID =?";
		String  selQuery3 = " SELECT CURRENCYID FROM FS_COUNTRYMASTER WHERE CURRENCYID=?";
		String  modifyOriginCheck = "SELECT ORIGINLOCATION FROM FS_FR_IATARATEMASTERHDR WHERE ORIGINLOCATION=?";
		String  modifyDestCheck = "SELECT DESTLOCATION FROM FS_FR_IATARATEMASTERHDR WHERE DESTLOCATION=?";
        String  modifyServiceLevelCheck = "SELECT SERVICELEVEL FROM FS_FR_IATARATEMASTERHDR WHERE SERVICELEVEL=?";


		try
	    {
	        connection = operationsImpl.getConnection();
			if(!operation.equalsIgnoreCase("Modify"))
			{
				if(operation.equalsIgnoreCase("ModifyDtls"))
			      { pStmt	   = connection.prepareStatement(modifyOriginCheck);}
				else
				  { pStmt	   = connection.prepareStatement(selQuery1);}
				pStmt.setString(1,originId);
				if(!pStmt.executeQuery().next())	{		
					errorCodes = " Please Enter Correct Data for OriginId ";}
          //Added By RajKumari on 30-10-2008 for Connection Leakages.
          if(pStmt != null)
          {
            pStmt.close();
             pStmt=null;
          }
               
                if(operation.equalsIgnoreCase("ModifyDtls"))
			      { pStmt	   = connection.prepareStatement(modifyDestCheck);}
				else
				  { pStmt	   = connection.prepareStatement(selQuery1);}
				pStmt.setString(1,destinationId);
				if(!pStmt.executeQuery().next())			
				{
				  if(!errorCodes.equals(""))
					{ errorCodes += " DestinationId";}
				  else
					{ errorCodes = " Please Enter Correct Data for Destination Id, "; 	 }
				}	
				//Added By RajKumari on 30-10-2008 for Connection Leakages.
          if(pStmt != null)
          {
            pStmt.close();
             pStmt=null;
          }
				if(operation.equalsIgnoreCase("ModifyDtls"))
			      { pStmt	   = connection.prepareStatement(modifyServiceLevelCheck);
				    pStmt.setString(1,serviceLevelId);  }
				else
				  { 
					selQuery2 = " SELECT SERVICELEVELID FROM FS_FR_SERVICELEVELMASTER WHERE SERVICELEVELID ='"+serviceLevelId+"'";
					pStmt	   = connection.prepareStatement(selQuery2);
				  }
				if(!pStmt.executeQuery().next())			
				{
				  if(!errorCodes.equals(""))
					{ errorCodes += " ServiceLevelId ";}
				  else
					{ errorCodes = " Please Enter Correct Data for ServiceLevel Id ";}
				}
				//Added By RajKumari on 30-10-2008 for Connection Leakages.
          if(pStmt != null)
          {
            pStmt.close();
             pStmt=null;
          }
				}
            // for checking currency id in the 2nd section
			if(operation.equalsIgnoreCase("Add") || operation.equalsIgnoreCase("Modify")) 
			{
            pStmt = connection.prepareStatement(selQuery3);
			
			pStmt.setString(1,currencyId);

			if(!pStmt.executeQuery().next())	
			{
			  if(!errorCodes.equals(""))
			     {errorCodes += " Currency Id";}
			  else
			     {errorCodes  = " Please Enter Correct Data for Currency Id ";}
			}
			
			}
	   
	    }
	    catch(SQLException sqlEx)
	    {
		 
	      //Logger.error(FILE_NAME,"validateInfo()"," SQL Exception While Validating Lane Info" + sqlEx);
        logger.error(FILE_NAME+"validateInfo()"+" SQL Exception While Validating Lane Info" + sqlEx);
		  throw new EJBException(sqlEx.toString());
	    }
	    finally
	    {
	      ConnectionUtil.closeConnection(connection,pStmt);
	    }
       return errorCodes;
	}

	public ArrayList getIATADtls(int masterId)
	{
	  IATARatesDAO          IATADao                 = new IATARatesDAO();          
	  IATADtlModel          rateDtls                = new IATADtlModel();
    ArrayList result = new ArrayList();
	  
	   try
	   {
		   //Logger.info(FILE_NAME," :in getIATADtls ");
		   
		result	= IATADao.load(masterId);
		// Logger.info(FILE_NAME,"=======getOriginLocation=================== "+rateDtls.getOriginLocation()); 
	   }
	   catch(Exception ex)
	   {
		
	     //Logger.info(FILE_NAME,"getCarrierContractDtls()","Finder Exception While Locating CarrierContract Object",ex);
       logger.info(FILE_NAME+"getCarrierContractDtls()"+"Finder Exception While Locating CarrierContract Object"+ex);
	     throw new EJBException(ex.toString());
	   } 
	   if(result!=null)
		{
	          return result;}
	  else
		{return null;}
	}

	public IATADtlModel getIATADtls(String originTerminalId,String destinationTerminalId,String serviceLevelId,String operation,int masterId)
	{
	  IATARatesDAO          IATADao                 = new IATARatesDAO();          
	  IATADtlModel          rateDtls                = new IATADtlModel();
	  
	   try
	   {
		  // Logger.info(FILE_NAME," :in getIATADtls ");
		   
		rateDtls	= IATADao.load(originTerminalId,destinationTerminalId,serviceLevelId,operation,masterId);
		 //Logger.info(FILE_NAME,"=======getOriginLocation=================== "+rateDtls.getOriginLocation()); 
	   }
	   catch(Exception ex)
	   {
		 
	     //Logger.info(FILE_NAME,"getCarrierContractDtls()","Finder Exception While Locating CarrierContract Object",ex);
       logger.info(FILE_NAME+"getCarrierContractDtls()"+"Finder Exception While Locating CarrierContract Object"+ex);
	     throw new EJBException(ex.toString());
	   } 
      if(rateDtls!=null) 
		{return rateDtls;}
	  else
		{ return null;}
	}

	public String addIATADtls(IATADtlModel  rateDtlModel) throws javax.ejb.EJBException
{
    //Connection            con                     = null;//Commented By RajKumari on 30-10-2008 for Connection Leakages.
    //PreparedStatement     pstmt                   = null;//Commented By RajKumari on 30-10-2008 for Connection Leakages.
    //Logger.error(FILE_NAME," :in 77777777777777777777777 ");	
    logger.error(FILE_NAME+" :in 77777777777777777777777 ");	
	IATARatesDAO          IATADao                 = new IATARatesDAO();          
	IATADtlModel          rateDtls                = new IATADtlModel();
    try
	   {	
              //Logger.error(FILE_NAME," :in addIATADtls ");		     
              logger.error(FILE_NAME+" :in addIATADtls ");		     
			  boolean flag            = IATADao.create(rateDtlModel);
			  //Logger.error(FILE_NAME," :after create in addIATADtls ");		     
        logger.error(FILE_NAME+" :after create in addIATADtls ");		     
			  if(!flag)
			   return null; 
       
          
       }
    catch(Exception ex)
      {
		
      //Logger.error(FILE_NAME," :addIATADtls SQLException : "+ex.toString());
      logger.error(FILE_NAME+" :addIATADtls SQLException : "+ex.toString());
      	try
        {
          //if(pstmt!=null)pstmt.close();//Commented By RajKumari on 30-10-2008 for Connection Leakages.
          //if(con!=null)con.close();//Commented By RajKumari on 30-10-2008 for Connection Leakages.
          }catch(Exception exp)
          {
            //Logger.error(FILE_NAME,"Error while closing database resources");
            logger.error(FILE_NAME+"Error while closing database resources");
            
          }
          throw new EJBException(ex.toString());
          
      }
      finally
       {
       	try
        {
         // if(pstmt!=null)pstmt.close();//Commented By RajKumari on 30-10-2008 for Connection Leakages.
          //if(con!=null)con.close();//Commented By RajKumari on 30-10-2008 for Connection Leakages.
          }catch(Exception ex)
          {
            //Logger.error(FILE_NAME,"Error while closing database resources");
            logger.error(FILE_NAME+"Error while closing database resources");
          }
       }
       return "Added";
	}

	public String addIATADtls(IATADtlModel  rateDtlModel,ArrayList chargeValues) throws javax.ejb.EJBException
{
    //Connection            con                     = null;//Commented By RajKumari on 30-10-2008 for Connection Leakages.
    //PreparedStatement     pstmt                   = null;//Commented By RajKumari on 30-10-2008 for Connection Leakages.
    //Logger.error(FILE_NAME," :in 77777777777777777777777 ");	
    logger.error(FILE_NAME+" :in 77777777777777777777777 ");	
	IATARatesDAO          IATADao                 = new IATARatesDAO();          
	IATADtlModel          rateDtls                = new IATADtlModel();
    try
	   {	
              //Logger.error(FILE_NAME," :in addIATADtls ");		     
              logger.error(FILE_NAME+" :in addIATADtls ");		     
			  boolean flag            = IATADao.create(rateDtlModel,chargeValues);
			  //Logger.error(FILE_NAME," :after create in addIATADtls ");		     
        logger.error(FILE_NAME+" :after create in addIATADtls ");		     
			  if(!flag)
			   return null; 
       
          
       }
    catch(Exception ex)
      {
	
      //Logger.error(FILE_NAME," :addIATADtls SQLException : "+ex.toString());
      logger.error(FILE_NAME+" :addIATADtls SQLException : "+ex.toString());
      	try
        {
         // if(pstmt!=null)pstmt.close();//Commented By RajKumari on 30-10-2008 for Connection Leakages.
          //if(con!=null)con.close();//Commented By RajKumari on 30-10-2008 for Connection Leakages.
          }catch(Exception exp)
          {
            //Logger.error(FILE_NAME,"Error while closing database resources");
            logger.error(FILE_NAME+"Error while closing database resources");
          }
          	throw new EJBException(ex.toString());
      }
      finally
       {
       	try
        {
          //if(pstmt!=null)pstmt.close();//Commented By RajKumari on 30-10-2008 for Connection Leakages.
          //if(con!=null)con.close();//Commented By RajKumari on 30-10-2008 for Connection Leakages.
          }catch(Exception ex)
          {
            //Logger.error(FILE_NAME,"Error while closing database resources");
            logger.error(FILE_NAME+"Error while closing database resources");
          }
       }
	   
        return "Added";
	   
	}

	public String setIATADtls(IATADtlModel  rateDtlModel) throws javax.ejb.EJBException
	{
       IATARatesDAO          IATADao                 = new IATARatesDAO();          
	  IATADtlModel          rateDtls                = new IATADtlModel();
	  String result  = "";
       try
	   {
		   //Logger.info(FILE_NAME," :in setIATADtls ");
		   result	= IATADao.store(rateDtlModel);
		
	   }
	   catch(Exception ex)
	   {
		  
	     //Logger.info(FILE_NAME,"getCarrierContractDtls()","Finder Exception While Locating CarrierContract Object",ex);
       logger.info(FILE_NAME+"getCarrierContractDtls()"+"Finder Exception While Locating CarrierContract Object"+ex);
	     throw new EJBException(ex.toString());
	   } 
	   
	  if(result!=null) 
		{return "Modified";}
	  else
		{return null;}

        
	}

	public String updateIATADtls(IATADtlModel  rateDtlModel,ArrayList chargeValues) throws javax.ejb.EJBException
	{
       IATARatesDAO          IATADao                 = new IATARatesDAO();          
	  IATADtlModel          rateDtls                = new IATADtlModel();
	  boolean result  = true;
       try
	   {
		   //Logger.info(FILE_NAME," :in setIATADtls ");
		   result	= IATADao.store(rateDtlModel,chargeValues);
		
	   }
	   catch(Exception ex)
	   {
		   
	     //Logger.info(FILE_NAME,"getCarrierContractDtls()","Finder Exception While Locating CarrierContract Object",ex);
       logger.info(FILE_NAME+"getCarrierContractDtls()"+"Finder Exception While Locating CarrierContract Object"+ex);
	     throw new EJBException(ex.toString());
	   } 
	  if(result) 
		{return "Modified";}
	  else
		{return null;}

        
	}

	public String  removeIATADtls(int IATAmasterId) throws javax.ejb.EJBException
	{
       IATARatesDAO          IATADao                 = new IATARatesDAO();          
	   String result  = "";
       try
	   {
		   //Logger.info(FILE_NAME," :in setIATADtls ");
		   result	= IATADao.remove(IATAmasterId);
		
	   }
	   catch(Exception ex)
	   {
		   
	     //Logger.info(FILE_NAME,"getCarrierContractDtls()","Finder Exception While Locating CarrierContract Object"+ex);
       logger.info(FILE_NAME+"getCarrierContractDtls()"+"Finder Exception While Locating CarrierContract Object"+ex);
	     throw new EJBException(ex.toString());
	   } 
	  if(result!=null) 
		{ return "Deleted";}
	  else
		{ return null;}

        
	}

	/**
	* This method is	used to	verify ,if any	company	registered with	that companyId.
	* It	returns	true if	any	company	registered with	that companyId else	it returns false.
	* @param	companyId  String  companyId
	* @returns boolean.
*/
	public boolean isHORegistrationCompanyExists(String	companyId )
	{
		Connection	connection = null;
		Statement	stmt	   = null;
		ResultSet	rs		   = null;
		try
		{
			connection = operationsImpl.getConnection();
			String sqlQuery	= "SELECT COMPANYID	FROM  FS_COMPANYINFO	WHERE COMPANYID= '"	+companyId+"' AND AGENTJVIND='C' " ;
			stmt = connection.createStatement();
			rs = stmt.executeQuery(sqlQuery);
			if(	rs.next() )
				{return true;}
			else
				{return false;}
		}
		catch(SQLException sqle)
		{
		
			//Logger.error(FILE_NAME,"SQL	Error in ETransHOSuperUserSetupSessionBean :: isCompanyExists "	+ sqle.toString() );
      logger.error(FILE_NAME+"SQL	Error in ETransHOSuperUserSetupSessionBean :: isCompanyExists "	+ sqle.toString() );
      	throw new EJBException(sqle.toString());
		}
		catch(Exception exp)
		{
			
			//Logger.error(FILE_NAME,"Error in ETransHOSuperUserSetupSessionBean :: isCompanyExists "	+ exp.toString() );
      logger.error(FILE_NAME+"Error in ETransHOSuperUserSetupSessionBean :: isCompanyExists "	+ exp.toString() );
      throw new EJBException(exp.toString());
		}
		finally
		{
			try
			{
				if(	rs != null )
					{rs.close();}
				if(	stmt !=	null )
					{stmt.close();}
				if(	connection != null )
					{connection.close();}
			}
			catch( Exception exp )
			{
				//Logger.error(FILE_NAME,"Error while	Closing	Connection in ETransHOSuperUserSetupSessionBean	:: isCompanyExists " + exp.toString());
        logger.error(FILE_NAME+"Error while	Closing	Connection in ETransHOSuperUserSetupSessionBean	:: isCompanyExists " + exp.toString());
			}
		}//end of finally.
	}


	/** 
  * This method is	used to	register the company with a	particular companyID.
	* It	returns	true if	the	company	details	successfully inserts into the database else	it returns false.
	* This method may throw javax.ejb.EJBException	if it is not able to insert	the	data in	to the database.
	* @param	company	 HORegistrationJspBean,	contains all the company details
	* @param	address	 Address, which	contains company address
	* @param	loginDetails  ESupplyGlobalParameters,which	contains the login dteatils	like userId,terminalId etc.
	* @returns boolean.
	* @exception				  
*/
	public boolean addHORegistrationDetails(HORegistrationJspBean company,Address address, ESupplyGlobalParameters loginDetails)	
	{
		int sAddressId  =0;
		try
		{
			InitialContext initialContext = new InitialContext();
		
			address.setAddressId(company.getAddressId());
            AddressDAO addressDAO= new AddressDAO();
			sAddressId = addressDAO.create(address);
        	
		}catch(Exception e)
		{
			//Logger.error(FILE_NAME,"Error while Inserting	Address into HOCompanyRegistration	:: setHORegistrationDetails()"+e.toString());
      logger.error(FILE_NAME+"Error while Inserting	Address into HOCompanyRegistration	:: setHORegistrationDetails()"+e.toString());
			throw new EJBException("Error while Inserting	Address into HOCompanyRegistration	:: setHORegistrationDetails()"+e.toString());
		}
		Connection		connection	   	= null;
		Statement		stmt 			= null;
		try
		{
			connection 				= operationsImpl.getConnection();
			stmt	  				= connection.createStatement();
			String sid			 	= company.getCompanyId();
			String sName		 	= company.getCompanyName();
//			int	 sAddressId			= company.getAddressId();
			String sDateFormat	 	= company.getDateFormat();
			String sTimeZone	 	= company.getTimeZone();
			String sHCurrency		= company.getHCurrency();
			String sDayLightSavings	= company.getDayLightSavings();
			String iataCode			= company.getIataCode();
			String opEmailId		= company.getOpEmailId();
			String designation		= company.getDesignation();
			String contactName		= company.getContactName();
  //@@ Added By Ravi Kumar to Resolve the Issues SPETI-4168 & 4169 on 01-05-2005  
			String contactLastName	= company.getContactLastName();
			String companyEIN		= company.getCompanyEIN();

			String sqlInsert		= "	INSERT INTO	FS_COMPANYINFO(COMPANYID,COMPANYNAME,COMPANYADDRESSID,HCURRENCY,DAYLIGHTSAVING,IATACODE,OPE_EMAILID,DESIGNATION,CONTACTNAME,AGENTJVIND,CONTACT_LASTNAME, COMPANY_EIN) VALUES ('" +	sid+"','"+ sName +
										"'," +sAddressId+	",'" +sHCurrency+	"','" +sDayLightSavings+ "','"+iataCode+"','"+opEmailId+"','"+designation+"','"+contactName+"','C','"+contactLastName+"','"+companyEIN+"')";
//@@ 01-05-2005
		//	Logger.info(FILE_NAME,"SQL Insert "+ sqlInsert);
			stmt.executeUpdate(	sqlInsert );
			operationsImpl.setTransactionDetails(	loginDetails.getTerminalId(),
														loginDetails.getUserId(),
														"HOCompany",
														sid,
														loginDetails.getLocalTime(),
														"ADD");
		}
		catch( Exception cnfe)
		{
			//Logger.error(FILE_NAME,"Error while Inserting	Data into HOCompanyRegistration	:: setHORegistrationDetails()"+cnfe.toString());
      logger.error(FILE_NAME+"Error while Inserting	Data into HOCompanyRegistration	:: setHORegistrationDetails()"+cnfe.toString());
			throw new EJBException("Error while Inserting	Data into HOCompanyRegistration	:: setHORegistrationDetails()"+cnfe.toString());
		}
		finally
		{
			try
			{
				if( stmt	!= null	)
					{stmt.close();}
				if( connection != null )
				{connection.close();}
			}
			catch(SQLException sqle)
			{
				//Logger.error(FILE_NAME,"	finally	block SQLException in setHORegistrationDetails of ETransHOSuperUserSetupSessionBean"+sqle.toString());
        logger.error(FILE_NAME+"	finally	block SQLException in setHORegistrationDetails of ETransHOSuperUserSetupSessionBean"+sqle.toString());
			}
		}
		return true;
	}//	end	of setHORegistrationDetails


  /** This method is	used to	update the company details .
   * It	returns	true if	the	 successfully update the company details else it returns false.
   * This method may throw javax.ejb.EJBException	if it is not able to update.
   * @param 	company	 HORegistrationJspBean,	contains all the company details
   * @param 	loginDetails  ESupplyGlobalParameters,which	contains the login dteatils	like userId,terminalId etc.
   * @return boolean.
   * @exception 				  
   * @param compAdddress
   * @param address
   */
	public boolean updateHORegistrationDetails(HORegistrationJspBean company, ESupplyGlobalParameters loginDetails,int	compAdddress, Address address)
	{
		Connection connection = null;
		Statement stmt		 = null;
		try
		{
			connection = operationsImpl.getConnection();
			stmt	   = connection.createStatement();
			String sid				= company.getCompanyId();
			String sName			= company.getCompanyName();
			int	sAddressId			= company.getAddressId();
			String sDateFormat		= company.getDateFormat();
			String sTimeZone		= company.getTimeZone();
			String sHCurrency		= company.getHCurrency();
			String sDayLightSavings	= company.getDayLightSavings();
			String iataCode  	    = company.getIataCode();
			String contactName      = company.getContactName();
//@@ Added By Ravi Kumar to Resolve the Issues SPETI-4168 & 4169 on 01-05-2005 
			String contactLastName      = company.getContactLastName();
			String companyEIN      = company.getCompanyEIN();
//@@ 01-05-2005
			String designation 	    = company.getDesignation();
			String opEmailId		= company.getOpEmailId();
//@@ This query modified By Ravi Kumar to Resolve the Issues SPETI-4168 & 4169 on 01-05-2005 			
			String sqlUpdate		= "	UPDATE FS_COMPANYINFO SET COMPANYNAME='"+ sName +"',COMPANYADDRESSID=" +sAddressId+ ",HCURRENCY='" +sHCurrency+ "',DAYLIGHTSAVING='" +sDayLightSavings+ "',IATACODE='"+iataCode+"',OPE_EMAILID='"+opEmailId+"',CONTACTNAME='"+contactName+"',DESIGNATION='"+designation+"',CONTACT_LASTNAME='"+contactLastName+"',COMPANY_EIN='"+companyEIN+"' WHERE COMPANYID='"+sid+"'";
//@@ Ravi, 01-05-2005
			stmt.executeUpdate( sqlUpdate );
			InitialContext initialContext = new InitialContext();
			
            address.setAddressId(compAdddress);
			AddressDAO addressDAO= new AddressDAO();
			addressDAO.updateAddress(address);
            
           
			operationsImpl.setTransactionDetails( loginDetails.getTerminalId(),
														loginDetails.getUserId(),
														"HOCompany",
														sid,
														loginDetails.getLocalTime(),
														"MODIFY");
			return true;
		}
		catch(SQLException sqle)
		{
				//Logger.error(FILE_NAME,"SQLException in	updateHORegistrationDetails	of ETransHOSuperUserSetupSessionBean"+sqle.toString());
        logger.error(FILE_NAME+"SQLException in	updateHORegistrationDetails	of ETransHOSuperUserSetupSessionBean"+sqle.toString());
				throw new EJBException("SQLException in	updateHORegistrationDetails	of ETransHOSuperUserSetupSessionBean"+sqle.toString());
		}
		catch( Exception	cnfe )
		{
			//Logger.error(FILE_NAME,"Exception in updateHORegistrationDetails of ETransHOSuperUserSetupSessionBean"+cnfe.toString());
      logger.error(FILE_NAME+"Exception in updateHORegistrationDetails of ETransHOSuperUserSetupSessionBean"+cnfe.toString());
			throw new EJBException("Exception in	updateHORegistrationDetails	of ETransHOSuperUserSetupSessionBean"+cnfe.toString());
		}
		finally
		{
			try
			{
				if( stmt	!= null	)
					{stmt.close();}
				if( connection != null )
					{connection.close();}
			}
			catch(SQLException sqle)
			{
				//Logger.error(FILE_NAME,"Error while	closing	Connections	 in	updateHORegistrationDetails"+sqle.toString());
        logger.error(FILE_NAME+"Error while	closing	Connections	 in	updateHORegistrationDetails"+sqle.toString());
			}
		}//end of finally.
	}//	end	of updateHORegistrationDetails

	/**
	* This method is	used to	delete the company details from	the	database.
	* It	returns	true if	the	 successfully delete the company details from the database else	it returns false.
	* This method may throw javax.ejb.EJBException	if it is not able to delete.
	* @param	companyId  String, delete the company details for this Id.
	* @param	loginDetails  ESupplyGlobalParameters,which	contains the login dteatils	like userId,terminalId etc.
	* @returns boolean.
	* @exception				  javax.ejb.EJBException
	*/
	public boolean deleteHORegistrationDetails(String companyId	, ESupplyGlobalParameters loginDetails,int addId)	
	{
		Connection	connection	= null;
		Statement 	stmt		= null;
		Statement 	stmt1		= null;
		Statement 	stmt2		= null;
		String	  	tCompanyId  = null;
    ResultSet rs     = null;//Added By RajKumari on 30-10-2008 for Connection Leakages.
    ResultSet rs1     = null;//Added By RajKumari on 30-10-2008 for Connection Leakages.
		try
		{
			connection = operationsImpl.getConnection();
			stmt  =	connection.createStatement();
			stmt1 =	connection.createStatement();
			stmt2 =	connection.createStatement();
			String sqlQuery	= "SELECT *	FROM FS_COMPANYINFO	WHERE COMPANYID='"+companyId+"' AND AGENTJVIND='C' ";
			rs	= stmt1.executeQuery(sqlQuery);//Modified By RajKumari on 30-10-2008 for Connection Leakages.
			if(rs.next())
			{
				String		sqlTerminal	=	"SELECT COMPANYID FROM FS_FR_TERMINALMASTER WHERE COMPANYID ='"+companyId+"'";
				rs1			=	stmt2.executeQuery(sqlTerminal);//Modified By RajKumari on 30-10-2008 for Connection Leakages.
				if(rs1.next())
				{
					tCompanyId = rs1.getString("COMPANYID");
				}
				if(tCompanyId==null)
				{
					String sqlDelete = "DELETE FROM	FS_COMPANYINFO WHERE COMPANYID='"+companyId+"' AND AGENTJVIND='C' ";
					stmt.executeUpdate(sqlDelete);
					InitialContext initialContext = new InitialContext();
						AddressDAO addressDAO= new AddressDAO();
                        addressDAO.removeAddress(addId);
					
					operationsImpl.setTransactionDetails(	loginDetails.getTerminalId(),
															loginDetails.getUserId(),
															"HOCompany",
															companyId,
															loginDetails.getLocalTime(),
															"DELETE");
				}
				else
				{
					error = "RECORD IS IN USE";
				 	return	false;
				}
			}
			else
			{
				 error = "RECORD IS	NOT	PRESENT";
				 return	false;
			}
		}
		catch (Exception sqle )
		{
			//Logger.error(FILE_NAME,"Exception in deleteHORegistrationDetails of ETransHOSuperUserSetupSessionBean : "+sqle.toString());
      logger.error(FILE_NAME+"Exception in deleteHORegistrationDetails of ETransHOSuperUserSetupSessionBean : "+sqle.toString());
			error =	sqle.toString();
			throw new EJBException("Exception in	deleteHORegistrationDetails	of ETransHOSuperUserSetupSessionBean"+sqle.toString());
		}
		finally
		{
			try
			{
        //Added By RajKumari on 30-10-2008 for Connection Leakages.
        if(rs !=null)
        { rs.close();
          rs = null;}
        if(rs1 !=null)
        { rs1.close();
          rs1 = null;}
				//Connection Leakage --------------- 7-DEC-2004 ------------------ Santhosam.P
				if( stmt	!= null	)
					{stmt.close();}
				if( stmt1	!= null	)
					{stmt1.close();}
				if( stmt2	!= null	)
					{stmt2.close();}
				if( connection != null )
					{connection.close();}
			}
			catch(SQLException sqle)
			{
				//Logger.error(FILE_NAME,"SQL	Error while	closing	connections	 in	deleteHORegistrationDetails"+sqle.toString());
        logger.error(FILE_NAME+"SQL	Error while	closing	connections	 in	deleteHORegistrationDetails"+sqle.toString());
			}
		}
			return true;
	}//	end	of deleteCommodityMasterDetails

	/**
   * 
   * @param fromWhat
   * @param searchString
   * @return 
   */
    public ArrayList getCompanyIds(String fromWhat, String searchString,String operation,String terminalId)
    {
		Connection	connection	= null;
       // Statement	stmt        = null;//Commented By RajKumari on 30-10-2008 for Connection Leakages.
        ResultSet	rs          = null;

		ArrayList companyIds = new ArrayList();
		String sql			 = null;
        String whereClause   = null;
		CallableStatement cstmt = null;

        try
        {
            connection = this.getConnection();
            //stmt = connection.createStatement();//Commented By RajKumari on 30-10-2008 for Connection Leakages.

           		if(fromWhat.equals("JV"))
					{whereClause = " AND AGENTJVIND IN('J','A') ";}
			else
					{whereClause = " AND AGENTJVIND IN('C') ";}
			           
			//sql="SELECT COMPANYID,COMPANYNAME FROM FS_COMPANYINFO WHERE COMPANYID LIKE '"+searchString+"%'" + whereClause +" ORDER BY COMPANYID" ;

            //rs= stmt.executeQuery(sql);
		  cstmt        = connection.prepareCall("{ ?= call QMS_SETUPS.GET_COMPANY(?,?,?,?)}");
          cstmt.registerOutParameter(1,OracleTypes.CURSOR);
          cstmt.setString(2,terminalId);
          cstmt.setString(3,(searchString+"%"));
          cstmt.setString(4,fromWhat);
          cstmt.setString(5,operation);
          cstmt.execute();
          rs  =  (ResultSet)cstmt.getObject(1);
            while(rs.next())
			{companyIds.add(rs.getString(2) +"  ["+rs.getString(1)+"]");}
        }
        catch(SQLException sqEx)
        {sqEx.printStackTrace();
			//Logger.error(FILE_NAME, "[getCompanyIds(fromWhat, searchString)] -> "+sqEx.toString());
      logger.error(FILE_NAME+ "[getCompanyIds(fromWhat, searchString)] -> "+sqEx.toString());
            throw new EJBException(sqEx.toString());
        }
        catch(Exception sqEx)
        {sqEx.printStackTrace();
			//Logger.error(FILE_NAME, "[getCompanyIds(fromWhat, searchString)] -> "+sqEx.toString());
      logger.error(FILE_NAME+ "[getCompanyIds(fromWhat, searchString)] -> "+sqEx.toString());
            throw new EJBException(sqEx.toString());
        }
        finally
        {
			ConnectionUtil.closeConnection(connection, cstmt, rs);
        }
        return companyIds;
    }

	/**
   * 
   * @param companyid
   * @param searchString
   * @return 
   */
		public java.util.ArrayList	getCurrencyList2(String	companyid,String searchString,String terminalId,String operation)
		{
			InitialContext ictx		=	null;
			String companyCurrency	=	null;
			String sqlQuery			=	null;
			if(!companyid.equals("ALL"))
			companyCurrency = this.getHcurrency(companyid);
		// @@ 'connection' is replaced with 'connection1' by Suneetha for TogehterJ on 12 Jan 05
			Connection connection1	=	null;
			//Statement stmt			=	null,stmt1=null;//Commented By RajKumari on 30-10-2008 for Connection Leakages.
			ResultSet rs			=	null;
			//ResultSet rs1			=	null;//Commented By RajKumari on 30-10-2008 for Connection Leakages.
			String COUNTRY			=	null;
			ArrayList currencyid= new ArrayList();
			CallableStatement cstmt =null;
			try
			{
				if(	dataSource == null )
				{
					ictx = new InitialContext();
					dataSource = ( DataSource )	ictx.lookup( "java:comp/env/jdbc/DB" );
				}
				/*if(companyCurrency != null)
					sqlQuery="SELECT DISTINCT CURRENCY2	CURRENCYID,COUNTRYNAME,CONVERSIONFACTOR	FROM FS_FR_CURRENCYMASTER,FS_COUNTRYMASTER WHERE	CURRENCY1='"+ companyCurrency+ "' AND CURRENCYID=CURRENCY2 ORDER BY	CURRENCYID";
				else
				*/
					//sqlQuery = "SELECT DISTINCT	CURRENCYID,COUNTRYNAME FROM	FS_COUNTRYMASTER WHERE CURRENCYID LIKE '"+searchString+"%' 	ORDER BY CURRENCYID";
					connection1 = dataSource.getConnection();
					//stmt = connection1.createStatement();
					//rs = stmt.executeQuery(	sqlQuery );
          if(searchString==null)
            searchString="";
			  cstmt        = connection1.prepareCall("{ ?= call QMS_SETUPS.GET_CURRENCY_2(?,?,?)}");
              cstmt.registerOutParameter(1,OracleTypes.CURSOR);
              cstmt.setString(2,terminalId);
              cstmt.setString(3,(searchString+"%"));
              //cstmt.setString(4,("F"));          
            //  cstmt.setString(4,operation);
            //  cstmt.setString(4,operation);
                cstmt.setString(4,operation);
              cstmt.execute();
              rs  =  (ResultSet)cstmt.getObject(1);
					while( rs.next())
					currencyid.add(rs.getString("CURRENCYID")+	" [" +rs.getString("COUNTRYNAME")+"]" );
			}
			catch( SQLException	sqle)
			{
				sqle.printStackTrace();
				//Logger.error(FILE_NAME,"CurrencyConversionSessionBean.java :  getCurrencyList2()"+sqle);
        logger.error(FILE_NAME+"CurrencyConversionSessionBean.java :  getCurrencyList2()"+sqle);
        throw new EJBException(sqle.toString());
			}
			catch(javax.naming.NamingException nme)
			{
				nme.printStackTrace();
				//Logger.error(FILE_NAME,"CurrencyConversionSessionBean.java : getCurrencyList2() "+nme );
        logger.error(FILE_NAME+"CurrencyConversionSessionBean.java : getCurrencyList2() "+nme );
        throw new EJBException(nme.toString());
			}
		catch( Exception	sqle)
			{
				sqle.printStackTrace();
				//Logger.error(FILE_NAME,"CurrencyConversionSessionBean.java :  getCurrencyList2()"+sqle);
        logger.error(FILE_NAME+"CurrencyConversionSessionBean.java :  getCurrencyList2()"+sqle);
        throw new EJBException(sqle.toString());
			}
			finally
			{
				try
				{
					ictx =	null;
					if(	rs != null )
          {
					rs.close();
          }
					if(	cstmt !=	null )
          {
					cstmt.close();
          }
					if(	connection1 != null )
          {
					connection1.close();
          }
				}
			catch(SQLException sqle)
			{
				//Logger.error(FILE_NAME,"CurrencyConversionSessionBean.java :  getCurrencyList2()"+sqle);
        logger.error(FILE_NAME+"CurrencyConversionSessionBean.java :  getCurrencyList2()"+sqle);
			}
			}
			return currencyid;
		}

		/**
		*This method is	used to	retrive	the	Currency Ids .
		*This method takes companyid as	argument and
		*returns a vector that contains	currency Ids   from	 the table FS_FR_CURRENCYMASTER	if hcurrency of	login terminal based company
		*exist, else it takes the currency	ids	from the table FS_COUNTRYMASTER.
		*@param companyid	  String  CompanyId
		*@return currencyid	   Vector with currency	Ids	.
		*/
		public java.util.ArrayList getCurrencyList(String companyid,String searchString)
		{
			InitialContext ictx		=	null;
			String companyCurrency	=	null;
			String sqlQuery			=	null;
			if(!companyid.equals("ALL"))
			companyCurrency = this.getHcurrency(companyid);
		// @@ 'connection' is replaced with 'connection1' by Suneetha for TogehterJ on 12 Jan 05
			Connection connection1	=	null;
			//Statement stmt			=	null;//Commented By RajKumari on 30-10-2008 for Connection Leakages.
			//Statement stmt1					=	null;//Commented By RajKumari on 30-10-2008 for Connection Leakages.
			ResultSet rs			=	null;
			//ResultSet rs1			=	null;//Commented By RajKumari on 30-10-2008 for Connection Leakages.
			String COUNTRY			=	null;
			ArrayList currencyid= new ArrayList();
			CallableStatement cstmt =null;
			try
			{
				if( dataSource == null )
				{
					ictx = new InitialContext();
					dataSource = ( DataSource )	ictx.lookup( "java:comp/env/jdbc/DB" );
				}
				  if(searchString==null)
                    searchString ="";	
                 //sqlQuery = " SELECT CURRENCYID,COUNTRYNAME FROM FS_COUNTRYMASTER WHERE CURRENCYID IN "+ "(SELECT CURRENCY1 FROM FS_FR_CURRENCYMASTER) AND CURRENCYID LIKE '"+searchString+"%' ORDER BY COUNTRYNAME ";
					connection1 = dataSource.getConnection();
					//stmt = connection1.createStatement();
					//rs = stmt.executeQuery( sqlQuery );
          
			cstmt        = connection1.prepareCall("{ ?= call QMS_SETUPS.GET_CURRENCYIDS(?)}");
              cstmt.registerOutParameter(1,OracleTypes.CURSOR);
              cstmt.setString(2,searchString+"%");
              cstmt.execute();
              rs  =  (ResultSet)cstmt.getObject(1);
					while( rs.next())
					{
						currencyid.add(rs.getString(1)+ " [" +rs.getString(2)+"]" );
					}
			}
			catch( SQLException sqle)
			{
				sqle.printStackTrace();
				//Logger.error(FILE_NAME,"CurrencyConversionSessionBean.java :  getCurrencyList()"+sqle.toString());
        logger.error(FILE_NAME+"CurrencyConversionSessionBean.java :  getCurrencyList()"+sqle.toString());
        throw new EJBException(sqle.toString());
			}
			catch(javax.naming.NamingException nme)
			{
				nme.printStackTrace();
				//Logger.error(FILE_NAME,"CurrencyConversionSessionBean.java :  getCurrencyList()"+nme.toString() );
        logger.error(FILE_NAME+"CurrencyConversionSessionBean.java :  getCurrencyList()"+nme.toString() );
        throw new EJBException(nme.toString());
			}
		catch( Exception sqle)
			{
				sqle.printStackTrace();
				//Logger.error(FILE_NAME,"CurrencyConversionSessionBean.java :  getCurrencyList()"+sqle.toString());
        logger.error(FILE_NAME+"CurrencyConversionSessionBean.java :  getCurrencyList()"+sqle.toString());
        throw new EJBException(sqle.toString());
			}
			finally
			{
				try
				{
					ictx = null;
					if( rs != null )
          {
					rs.close();
          }
					if( cstmt != null )
          {
					cstmt.close();
          }
					if( connection1 != null )
          {
					connection1.close();
          }
				}
				catch(SQLException sqle)
				{
					//Logger.error(FILE_NAME,"CurrencyConversionSessionBean.java :  getCurrencyList()"+sqle.toString());
          logger.error(FILE_NAME+"CurrencyConversionSessionBean.java :  getCurrencyList()"+sqle.toString());
				}
			}
			return currencyid;
		}

		/**
   * 
   * @param baseCurrency
   * @param selectedCurrency
   * @param BuyRate
   * @param SellRate
   * @param SpecifiedRate
   * @param radioChkd
   * @return 
   */
		public boolean updateCurrencyConversion(String baseCurrency,String[] selectedCurrency,
												String[] BuyRate,String[] SellRate,
												String[] SpecifiedRate,String radioChkd) 
		{
			//PreparedStatement pstmt	 = null;//Commented By RajKumari on 30-10-2008 for Connection Leakages.
			PreparedStatement pstmt1 = null;
			PreparedStatement pstmt2 = null;
			PreparedStatement pstmt3 = null;
		// @@ 'connection' is replaced with 'connection1' by Suneetha for TogehterJ on 12 Jan 05
			Connection connection1	 = null;
			ResultSet  rs			 = null;
			int	size				 = 0;
			String sql_update		 = null;
			try
			{
				connection1	= getConnection();
				if(selectedCurrency	!= null)
				size = selectedCurrency.length;
				String sql_check	  =	"SELECT	COUNT(*) NO_ROWS FROM FS_FR_CURRENCYMASTER WHERE CURRENCY1 = ? AND CURRENCY2 = ? ";
				if(radioChkd.equals("I"))
        {
				sql_update	= "UPDATE FS_FR_CURRENCYMASTER SET BUYRATE=?,SELLRATE=?,SPECIFIEDRATE=? WHERE	IEFLAG='I' AND CURRENCY1 =	? AND CURRENCY2	= ? ";
        }
				else
        {
				sql_update	 = "UPDATE FS_FR_CURRENCYMASTER	SET	BUYRATE=?,SELLRATE=?,SPECIFIEDRATE=? WHERE IEFLAG='E' AND CURRENCY1 = ? AND CURRENCY2 = ?  " ;
        }
        //Added by Rajkumari on 04-11-2008 for Connection Leakages in Loop
				pstmt1 = connection1.prepareStatement(sql_check);
        pstmt2	=	connection1.prepareStatement(sql_update);
        pstmt3	= connection1.prepareStatement(sql_update);
				for(int	j=0;j<size;j++)
				{
					int	 count	= 0;
					//pstmt1 = connection1.prepareStatement(sql_check);//Commented by Rajkumari on 04-11-2008 for Connection Leakages in Loop
					pstmt1.setString(1,baseCurrency);
					pstmt1.setString(2,selectedCurrency[j]);
					rs = pstmt1.executeQuery();
					if(rs.next())
					{
						count =	rs.getInt("NO_ROWS");
					} // end of	resultSet if
					/*if(pstmt1!=null)
            pstmt1.close();*///Commented by Rajkumari on 04-11-2008 for Connection Leakages in Loop
          if(rs!=null)
            rs.close();
					if(count >	0)
					{
						//pstmt3	= connection1.prepareStatement(sql_update);//Commented by Rajkumari on 04-11-2008 for Connection Leakages in Loop
						pstmt3.setString(1,BuyRate[j]);
						pstmt3.setString(2,SellRate[j]);
						pstmt3.setString(3,SpecifiedRate[j]);
						pstmt3.setString(4,baseCurrency);
						pstmt3.setString(5,selectedCurrency[j]);
						pstmt3.executeUpdate();
            
					} // end of	 if
					if(!(baseCurrency.equals(selectedCurrency[j])))
					{
						//pstmt2	=	connection1.prepareStatement(sql_update);//Commented by Rajkumari on 04-11-2008 for Connection Leakages in Loop
						pstmt2.setDouble(1,1/Double.parseDouble(BuyRate[j]));
						pstmt2.setDouble(2,1/Double.parseDouble(SellRate[j]));
						pstmt2.setDouble(3,1/Double.parseDouble(SpecifiedRate[j]));
						pstmt2.setString(4,selectedCurrency[j]);
						pstmt2.setString(5,baseCurrency);
						pstmt2.executeUpdate();
					}//	end	of currency	validation if condition
         /* if(pstmt3!=null)
            pstmt3.close();
           if(pstmt2!=null)
            pstmt2.close();*///Commented by Rajkumari on 04-11-2008 for Connection Leakages in Loop
				}//	end	of for loop
				
			}
			catch(Exception	e)
			{
				e.printStackTrace();
				//Logger.error(FILE_NAME,"CurrencyConversionSessionBean.java :  updateCurrencyConversion()" + e);
        logger.error(FILE_NAME+"CurrencyConversionSessionBean.java :  updateCurrencyConversion()" + e);
				throw new EJBException("CurrencyConversionSessionBean.java : updateCurrencyConversion()"	+ e);
				
			}
			finally
			{
				try
				{
					/*if(pstmt!=null)
          {
					pstmt.close();
          }*///Commented By RajKumari on 30-10-2008 for Connection Leakages.
					if(pstmt1!=null)
          {
					pstmt1.close();
          }
					if(pstmt2!=null)
          {
					pstmt2.close();
          }
					if(pstmt3!=null)
          {
					pstmt3.close();
          }
					if(connection1!=null)
          {
					connection1.close();
          }
				}
				catch(Exception	se)
				{
        
					//Logger.error(FILE_NAME,"CurrencyConversionSessionBean.java :  updateCurrencyConversion()"+se);
          logger.error(FILE_NAME+"CurrencyConversionSessionBean.java :  updateCurrencyConversion()"+se);
					throw new EJBException("CurrencyConversionSessionBean.java :updateCurrencyConversion()"+ se);
					
				}
			}
			
			return true;
		}


		/**
   * 
   * @param baseCurrency
   * @param selectedCurrency
   * @param BuyRate
   * @param SellRate
   * @param SpecifiedRate
   * @param radioChkd
   * @return 
   */
		public boolean addConversionFactor(String baseCurrency,String[]	selectedCurrency,String[] BuyRate,String[] SellRate,String[] SpecifiedRate,String radioChkd)	
		{
			PreparedStatement pstmt	 = null;
			PreparedStatement pstmt1 = null;
			PreparedStatement pstmt2 = null;
		// @@ 'connection' is replaced with 'connection1' by Suneetha for TogehterJ on 12 Jan 05
			Connection connection1	 = null;
			ResultSet  rs			 = null;
			int	size				 = 0;
			String currency_sql		 = null;
			String reverseCurrenysql = null;
			try
			{
				if(selectedCurrency	!= null)
        {
				size = selectedCurrency.length;
        }
				String sql_check  =	"SELECT	COUNT(*) NO_ROWS FROM FS_FR_CURRENCYMASTER WHERE CURRENCY1 = ? AND CURRENCY2 = ? ";
				currency_sql	  =	"INSERT	INTO FS_FR_CURRENCYMASTER (CURRENCY1,CURRENCY2,BUYRATE,SELLRATE,SPECIFIEDRATE,IEFLAG) VALUES (?,?,?,?,?,?) ";
				reverseCurrenysql =	"INSERT	INTO FS_FR_CURRENCYMASTER (CURRENCY1,CURRENCY2,BUYRATE,SELLRATE,SPECIFIEDRATE,IEFLAG) VALUES (?,?,?,?,?,?) ";
				connection1		  =	 operationsImpl.getConnection();
        //Added by Rajkumari on 04-11-2008 for Connection Leakages in Loop
        pstmt1 = connection1.prepareStatement(sql_check);
        pstmt	=	connection1.prepareStatement(currency_sql);
        pstmt2	=	connection1.prepareStatement(reverseCurrenysql);
				for(int	j =	0;j	< size;j++)
				{
					int	 count	= 0;
					//pstmt1 = connection1.prepareStatement(sql_check);//Commented by Rajkumari on 04-11-2008 for Connection Leakages in Loop
					pstmt1.setString(1,baseCurrency);
					pstmt1.setString(2,selectedCurrency[j]);
					rs	   = pstmt1.executeQuery();
					if(rs.next())
					{
						count =	rs.getInt("NO_ROWS");
					}
					
						//pstmt	=	connection1.prepareStatement(currency_sql);//Commented by Rajkumari on 04-11-2008 for Connection Leakages in Loop
						pstmt.setString(1,baseCurrency);
						pstmt.setString(2,selectedCurrency[j]);
						pstmt.setString(3,BuyRate[j]);
						pstmt.setString(4,SellRate[j]);
						pstmt.setString(5,SpecifiedRate[j]);
						pstmt.setString(6,radioChkd);
						pstmt.executeUpdate();
						if(!(baseCurrency.equals(selectedCurrency[j])))
						{
							//pstmt2	=	connection1.prepareStatement(reverseCurrenysql);//Commented by Rajkumari on 04-11-2008 for Connection Leakages in Loop
							pstmt2.setString(1,selectedCurrency[j]);
							pstmt2.setString(2,baseCurrency);
							pstmt2.setDouble(3,1/Double.parseDouble(BuyRate[j]));
							pstmt2.setDouble(4,1/Double.parseDouble(SellRate[j]));
							pstmt2.setDouble(5,1/Double.parseDouble(SpecifiedRate[j]));
							pstmt2.setString(6,radioChkd);
							pstmt2.executeUpdate();
						}//	end	of currency	validation if condition
					/*if(pstmt1!=null)
            pstmt1.close();
          if(pstmt!=null)
            pstmt.close();
          if(pstmt2!=null)
            pstmt2.close();*///Commented by Rajkumari on 04-11-2008 for Connection Leakages in Loop
          if(rs!=null)
            rs.close();
				}//end of for loop
			}
			catch(Exception	e)
			{
				//Logger.error(FILE_NAME,"CurrencyConversionSessionBean.java : addConversionFactor()" +	e);
        logger.error(FILE_NAME+"CurrencyConversionSessionBean.java : addConversionFactor()" +	e);
				throw new EJBException("CurrencyConversionSessionBean.java : addConversionFactor()" + e);
			}
			finally
			{
				try
				{
					if(pstmt!=null)
          {
					pstmt.close();
          }
					if(pstmt1!=null)
          {
					pstmt1.close();
          }
					if(pstmt2!=null)
          {
					pstmt2.close();
          }
					if(connection1!=null)
          {
					connection1.close();
          }
				}
				catch(Exception	se)
				{
					//Logger.error(FILE_NAME,"CurrencyConversionSessionBean.java : addConversionFactor() "+se);
          logger.error(FILE_NAME+"CurrencyConversionSessionBean.java : addConversionFactor() "+se);
					throw new EJBException("CurrencyConversionSessionBean.java : addConversionFactor()" + se);
				}
			}
			return true;
		}

		/**
   * 
   * @param baseCurrency
   * @param sltdCurrency
   * @param radioChkd
   * @return 
   */
		public String[][]	getConversionFactor(String baseCurrency,String[] sltdCurrency,String radioChkd)
		{
			InitialContext ictx		=	null;
		// @@ 'connection' is replaced with 'connection1' by Suneetha for TogehterJ on 12 Jan 05
			Connection connection1	=	null;
			Statement stmt			=	null;
			ResultSet rs			=	null;
			String[][] conFactors	  =	new	String[sltdCurrency.length][3];
			int sltdCurrLen			=	sltdCurrency.length;
			try
			{
				if(	dataSource == null )
				{
					ictx = new InitialContext();
					dataSource = ( DataSource )	ictx.lookup( "java:comp/env/jdbc/DB" );
				}
				connection1 = dataSource.getConnection();
				stmt = connection1.createStatement();
				for(int	i=0;i<sltdCurrLen;i++)
				{
					if(radioChkd.equalsIgnoreCase("I"))
					{
						String sqlQuery	= "SELECT BUYRATE,SELLRATE,SPECIFIEDRATE FROM	FS_FR_CURRENCYMASTER  WHERE	CURRENCY1='"+baseCurrency+"' AND CURRENCY2='"+sltdCurrency[i]+"' AND IEFLAG='I' ";
						rs = stmt.executeQuery(sqlQuery);
						
					}
					else
					{
						String sqlQuery	= "SELECT BUYRATE,SELLRATE,SPECIFIEDRATE FROM	FS_FR_CURRENCYMASTER  WHERE	CURRENCY1='"+baseCurrency+"' AND CURRENCY2='"+sltdCurrency[i]+"' AND IEFLAG='E' ";
						rs = stmt.executeQuery(sqlQuery);
					
					}
					while(rs.next())
					{
						conFactors[i][0] =	rs.getString(1);
						conFactors[i][1] =	rs.getString(2);
						conFactors[i][2] =	rs.getString(3);
						
					}
				}
			}
			catch (	SQLException sqle )
			{
				
				
				error =	sqle.toString();
				try
				{
					connection1.rollback();
				}
				catch(Exception	exp)
				{
					//Logger.error(FILE_NAME,"CurrencyConversionSessionBean.java :  getConversionFactor()"+sqle);
          logger.error(FILE_NAME+"CurrencyConversionSessionBean.java :  getConversionFactor()"+sqle);
				}
        throw new EJBException(sqle.toString());
			}
			catch( javax.naming.NamingException	nmex)
			{
				error =	nmex.toString();
				//Logger.error(FILE_NAME,"CurrencyConversionSessionBean.java :  getConversionFactor()"+nmex);
        logger.error(FILE_NAME+"CurrencyConversionSessionBean.java :  getConversionFactor()"+nmex);
			}
			catch( Exception cnfe )
			{
				//Logger.error(FILE_NAME,"CurrencyConversionSessionBean.java :  getConversionFactor()"+cnfe);
        logger.error(FILE_NAME+"CurrencyConversionSessionBean.java :  getConversionFactor()"+cnfe);
			}
			finally
			{
				try
				{
				ictx = null;
        //Added By RajKumari on 30-10-2008 for Connection Leakages.
        if(rs != null)
        {
         rs.close();
        }
				if(	stmt !=	null )
        {
				stmt.close();
        }
				if(	connection1 != null )
        {
				connection1.close();
        }
				}
				catch(SQLException sqle)
				{
				//Logger.error(FILE_NAME,"CurrencyConversionSessionBean.java : getConversionFactor()"+sqle);
        logger.error(FILE_NAME+"CurrencyConversionSessionBean.java : getConversionFactor()"+sqle);
				}
			}
      
			return conFactors;
			
		}//	end	of updating	currencyconversionDetails

		/**
   * 
   * @param currencyIdHide
   * @param radioChkd
   * @return 
   */
		public java.util.Vector	 getCFactorViewAll(String currencyIdHide,String	radioChkd)
		{
			InitialContext ictx		=	null;
		// @@ 'connection' is replaced with 'connection1' by Suneetha for TogehterJ on 12 Jan 05
			Connection connection1	=	null;
			Statement stmt			=	null;
			ResultSet rs			=	null;
			String buyRate			=	"";
			String sellRate			=	"";
			String specifiedRate	=	"";
			Vector vector			=	new Vector();
			Vector vector1			=	new Vector();
			Vector vector2			=	new Vector();
			String curr1="",curr2				= "";
			// @@ Replaced by Suneetha for TogetherJ on 12 Jan 05
			// String convFctr			=	"";
			// @@ Suneetha replaced on 20050429
			// StringBuffer convFctr		=	new StringBuffer();
			StringBuffer convFctr		=	null;
			// @@ 20050429
			// @@
			try
			{
				if(	dataSource == null )
				{
					ictx = new InitialContext();
					dataSource = ( DataSource )	ictx.lookup( "java:comp/env/jdbc/DB" );
				}
					connection1 = dataSource.getConnection();
				if(radioChkd.equals("I"))
				{
					stmt = connection1.createStatement();
					String query = "SELECT DISTINCT	CURRENCY1,CURRENCY2,BUYRATE,SELLRATE,SPECIFIEDRATE FROM FS_FR_CURRENCYMASTER WHERE CURRENCY1 IN ("+currencyIdHide+") AND BUYRATE	IS NOT NULL AND IEFLAG='I' ";
					rs = stmt.executeQuery(query);
				while(rs.next())
				{
					curr1			=	rs.getString(1);
					curr2			=	rs.getString(2);
					buyRate			=	rs.getString(3);
					sellRate		=	rs.getString(4);
					specifiedRate	=	rs.getString(5);
				if(buyRate==null)
        {
					buyRate	= "";
        }
				if(sellRate==null)
        {
					sellRate	= "";
        }
				if(specifiedRate==null)
        {
					specifiedRate	= "";
        }
				// @@ Replaced by Suneetha for TogetherJ on 12 Jan 05
				/*	convFctr = curr1+"$"+curr2+"$"+buyRate+"$"+sellRate+"$"+specifiedRate;
					vector.addElement(convFctr);
				*/
					// @@ Suneetha Added on 20050429
					convFctr		=	new StringBuffer();
					// @@ 20050429
					convFctr.append(curr1);
					convFctr.append("$");
					convFctr.append(curr2);
					convFctr.append("$");
					convFctr.append(buyRate);
					convFctr.append("$");
					convFctr.append(sellRate);
					convFctr.append("$");
					convFctr.append(specifiedRate);
					vector.addElement(convFctr.toString());
				// @@
				}
			}
			else
			{
				stmt = connection1.createStatement();
				String query = "SELECT DISTINCT	CURRENCY1,CURRENCY2,BUYRATE,SELLRATE,SPECIFIEDRATE FROM FS_FR_CURRENCYMASTER WHERE CURRENCY1 IN ("+currencyIdHide+") AND BUYRATE	IS NOT NULL AND IEFLAG='E'";
				rs = stmt.executeQuery(query);
				while(rs.next())
				{
					curr1	=	rs.getString(1);
					curr2	=	rs.getString(2);
					buyRate			=	rs.getString(3);
					sellRate		=	rs.getString(4);
					specifiedRate	=	rs.getString(5);
				if(buyRate==null)
        {
					buyRate	= "";
        }
				if(sellRate==null)
        {
					sellRate	= "";
        }
				if(specifiedRate==null)
        {
					specifiedRate	= "";
        }
				// @@ Replaced by Suneetha for TogetherJ on 12 Jan 05
				/*	convFctr = curr1+"$"+curr2+"$"+buyRate+"$"+sellRate+"$"+specifiedRate;
					vector.addElement(convFctr);
				*/
					// @@ Suneetha Added on 20050429
					convFctr		=	new StringBuffer();
					// @@ 20050429
					convFctr.append(curr1);
					convFctr.append("$");
					convFctr.append(curr2);
					convFctr.append("$");
					convFctr.append(buyRate);
					convFctr.append("$");
					convFctr.append(sellRate);
					convFctr.append("$");
					convFctr.append(specifiedRate);
					vector.addElement(convFctr.toString());
				// @@
				}
			}
		}
		catch(Exception e)
		{
			
			//Logger.error(FILE_NAME,"CurrencyConversionSessionBean.java : getCFactorViewAll()" + e);
      logger.error(FILE_NAME+"CurrencyConversionSessionBean.java : getCFactorViewAll()" + e);
      throw new EJBException(e.toString());
		}
		finally
		{
			try
			{
				if(stmt!=null)
        {
				stmt.close();
        }
				if(rs!=null)
        {
				rs.close();
        }
				if(connection1!=null)
        {
				connection1.close();
        }
			}
			catch(Exception	ex)
			{
				//Logger.error(FILE_NAME,"CurrencyConversionSessionBean.java :  getCFactorViewAll()" +ex);
        logger.error(FILE_NAME+"CurrencyConversionSessionBean.java :  getCFactorViewAll()" +ex);
			}
		}
		return vector;
	}

	/**
   * 
   * @param baseCurrency
   * @param sltdCurrency
   * @param radioChkd
   * @return 
   */
		public String[][]	getConversionFactor1(String[] baseCurrency,String[]	sltdCurrency,String	radioChkd)
		{
			InitialContext ictx		=	null;
		// @@ 'connection' is replaced with 'connection1' by Suneetha for TogehterJ on 12 Jan 05
			Connection connection1	=	null;
			Statement stmt			=	null;
			ResultSet rs			=	null;
			String[][] conFactors	  =	new	String[sltdCurrency.length][3];
			int sltdCurrLen			=	sltdCurrency.length;
			try
			{
				if(	dataSource == null )
				{
					ictx = new InitialContext();
					dataSource = ( DataSource )	ictx.lookup( "java:comp/env/jdbc/DB" );
				}
					connection1 = dataSource.getConnection();
					stmt = connection1.createStatement();
				for(int	i=0;i<sltdCurrLen;i++)
				{	
					if(radioChkd.equalsIgnoreCase("I"))
					{
						String sqlQuery	= "SELECT BUYRATE,SELLRATE,SPECIFIEDRATE FROM	FS_FR_CURRENCYMASTER  WHERE	CURRENCY1='"+baseCurrency[i]+"'	AND	CURRENCY2='"+sltdCurrency[i]+"' AND IEFLAG='I' ";
						rs = stmt.executeQuery(sqlQuery);
					}
					else
					{
					String sqlQuery	= "SELECT BUYRATE,SELLRATE,SPECIFIEDRATE FROM	FS_FR_CURRENCYMASTER  WHERE	CURRENCY1='"+baseCurrency[i]+"'	AND	CURRENCY2='"+sltdCurrency[i]+"' AND IEFLAG='E' ";
					rs = stmt.executeQuery(sqlQuery);
					}
					while(rs.next())
					{
						conFactors[i][0] =	rs.getString(1);
						conFactors[i][1] =	rs.getString(2);
						conFactors[i][2] =	rs.getString(3);
					}
				}
			}
			catch (	SQLException sqle )
			{
				error =	sqle.toString();
				
				try
				{
					connection1.rollback();
				}
				catch(Exception	exp)
				{
					//Logger.error(FILE_NAME,"CurrencyConversionSessionBean.java : getConversionFactor1()"+sqle);
          logger.error(FILE_NAME+"CurrencyConversionSessionBean.java : getConversionFactor1()"+sqle);
				}
        throw new EJBException(sqle.toString());
			}
			catch( javax.naming.NamingException	nmex)
			{
				error =	nmex.toString();
				//Logger.error(FILE_NAME,"CurrencyConversionSessionBean.java :  getConversionFactor1()"+nmex);
        logger.error(FILE_NAME+"CurrencyConversionSessionBean.java :  getConversionFactor1()"+nmex);
			}
			catch( Exception cnfe )
			{
				//Logger.error(FILE_NAME,"CurrencyConversionSessionBean.java : getConversionFactor1()"+cnfe);
        logger.error(FILE_NAME+"CurrencyConversionSessionBean.java : getConversionFactor1()"+cnfe);
			}
			finally
			{
				try
				{
					ictx = null;
          //Added By RajKumari on 30-10-2008 for Connection Leakages.
          if(rs !=null)
          {
            rs.close();
          }
					if(	stmt !=	null )
          {
					stmt.close();
          }
					if(	connection1 != null )
          {
					connection1.close();
          }
				}
				catch(SQLException sqle)
				{
					//Logger.error(FILE_NAME,"CurrencyConversionSessionBean.java : getConversionFactor1()"+sqle);
          logger.error(FILE_NAME+"CurrencyConversionSessionBean.java : getConversionFactor1()"+sqle);
				}
			}
			return conFactors;
		}//	end	of updating	currencyconversionDetails

	/**
   * 
   * @param baseCurrency
   * @return 
   */
		public String checkValidOrNot1(String[]	baseCurrency) 
		{
			Statement	 stmt		=	null;
			ResultSet	 rs			=	null;
			String validMsg			=	"";
		// @@ 'connection' is replaced with 'connection1' by Suneetha for TogehterJ on 12 Jan 05
			Connection connection1	=	null;
			try
			{
				connection1 =	dataSource.getConnection();
				stmt	= connection1.createStatement();
				int baseCurrLen	=	baseCurrency.length;
				for(int i=0;i<baseCurrLen;i++)
				{
					if(!baseCurrency[i] .equals(""))
					{
					String sql =	"SELECT	CURRENCYID FROM	FS_COUNTRYMASTER	WHERE CURRENCYID='"+baseCurrency[i]+"'";
					String strId=null;
					rs	  =	stmt.executeQuery( sql );
					if(rs.next())
          {
					strId	= rs.getString(1);
          }
					else
          {
					validMsg="C";
          }
					}
				}
			}
			catch( SQLException	e )
			{

				//Logger.error(FILE_NAME,	"CurrencyConversionSessionBean.java :  checkValidOrNot1()"+e);
        logger.error(FILE_NAME+	"CurrencyConversionSessionBean.java :  checkValidOrNot1()"+e);
				return null;
			}
			catch( Exception e )
			{
				//Logger.error(FILE_NAME,	"CurrencyConversionSessionBean.java : checkValidOrNot1()" +e);
        logger.error(FILE_NAME+	"CurrencyConversionSessionBean.java : checkValidOrNot1()" +e);
				return null;
			}
			finally
			{
				try
				{
					if(rs !=null)
          {
					rs.close();
          }
					if(	stmt !=	null )
          {
					stmt.close();
          }
					if(connection1 != null )
          {
					connection1.close();
          }
				}
			catch( SQLException	e )
			{
				//Logger.error(FILE_NAME,"CurrencyConversionSessionBean.java : checkValidOrNot1()"+e);
        logger.error(FILE_NAME+"CurrencyConversionSessionBean.java : checkValidOrNot1()"+e);
			}
			}
			return validMsg;
		} //end	of checkValidOrNot1


		/**
   * 
   * @param searchString
   * @return 
   */
		public java.util.Vector	getCurrencyView(String searchString,String terminalId,String operation)
		{
			InitialContext ictx		=	null;
			String companyCurrency	=	null;
			String sqlQuery			=	null;
		// @@ 'connection' is replaced with 'connection1' by Suneetha for TogehterJ on 12 Jan 05
			Connection connection1	=	null;
			//Statement stmt			=	null,stmt1=null;//Commented By RajKumari on 30-10-2008 for Connection Leakages.
			ResultSet rs			=	null;
			//ResultSet rs1			=	null;//Commented By RajKumari on 30-10-2008 for Connection Leakages.
			String COUNTRY			=	null;
			Vector currencyid= new Vector();
			CallableStatement cstmt = null;
			try
			{
				if(	dataSource == null )
				{
					ictx = new InitialContext();
					dataSource = ( DataSource )	ictx.lookup( "java:comp/env/jdbc/DB" );
				}
					//sqlQuery = "SELECT DISTINCT	CURRENCYID	FROM FS_COUNTRYMASTER WHERE CURRENCYID LIKE '"+searchString+"%' ORDER BY CURRENCYID ";
					connection1 = dataSource.getConnection();
					//stmt = connection1.createStatement();
					//rs = stmt.executeQuery(	sqlQuery );
			  cstmt        = connection1.prepareCall("{ ?= call QMS_SETUPS.GET_CURRENCY_1(?,?,?)}");
              cstmt.registerOutParameter(1,OracleTypes.CURSOR);
              cstmt.setString(2,terminalId);
              cstmt.setString(3,(searchString+"%"));
              //cstmt.setString(4,("F"));          
              cstmt.setString(4,operation);
              cstmt.execute();
              rs  =  (ResultSet)cstmt.getObject(1);
				while( rs.next())
				{
					currencyid.addElement(rs.getString(1)	);
				}
			}
			catch( SQLException	sqle)
			{
				sqle.printStackTrace();
				//Logger.error(FILE_NAME,"CurrencyConversionSessionBean.java : getCurrencyView()"+sqle);
        logger.error(FILE_NAME+"CurrencyConversionSessionBean.java : getCurrencyView()"+sqle);
				throw new EJBException(sqle.toString());
			}
			catch(javax.naming.NamingException nme)
			{
				//Logger.error(FILE_NAME,"CurrencyConversionSessionBean.java : getCurrencyView()"+nme );
        logger.error(FILE_NAME+"CurrencyConversionSessionBean.java : getCurrencyView()"+nme );
		     }catch( Exception	sqle)
			{
				sqle.printStackTrace();
				//Logger.error(FILE_NAME,"CurrencyConversionSessionBean.java : getCurrencyView()"+sqle);
        logger.error(FILE_NAME+"CurrencyConversionSessionBean.java : getCurrencyView()"+sqle);
				throw new EJBException(sqle.toString());
			}
			
			finally
			{
				try
				{
					ictx =	null;
					if(	rs != null )
          {
					rs.close();
          }
					if(	cstmt !=	null )
          {
					cstmt.close();
          }
					if(	connection1 != null )
          {
					connection1.close();
          }
				}
			catch(SQLException sqle)
			{
				//Logger.error(FILE_NAME,"CurrencyConversionSessionBean.java : getCurrencyView() "+sqle);
        logger.error(FILE_NAME+"CurrencyConversionSessionBean.java : getCurrencyView() "+sqle);
			}
			}
			return currencyid;
		}

		/**
   * 
   * @param baseCurrency
   * @param radioChkd
   * @return 
   */
		public java.util.Vector	getSelectedCurrency(String baseCurrency,String radioChkd,String terminalId,String operation)
		{
			InitialContext ictx		=	null;
		// @@ 'connection' is replaced with 'connection1' by Suneetha for TogehterJ on 12 Jan 05
			Connection connection1	=	null;
			//Statement stmt			=	null;//Commented By RajKumari on 30-10-2008 for Connection Leakages.
			ResultSet rs			=	null;
			String	cFactor			=	"";
			String conFactors		=	"";
			Vector vector			=	new Vector();
			String strsql			=	"";
			CallableStatement cstmt = null;
			try
			{
				if(	dataSource == null )
				{
					ictx = new InitialContext();
					dataSource = ( DataSource )	ictx.lookup( "java:comp/env/jdbc/DB" );
				}
				connection1 = dataSource.getConnection();
				//stmt=connection1.createStatement();
			/*	if(radioChkd.equalsIgnoreCase("I"))
				{
					//@@ Srivegi Modified on 20050524 for PBN-1759
					strsql	=  "SELECT DISTINCT CURRENCYID FROM FS_COUNTRYMASTER WHERE CURRENCYID NOT IN (SELECT CURRENCY2 FROM FS_FR_CURRENCYMASTER WHERE CURRENCY1='"+baseCurrency+"' AND IEFLAG='I')	ORDER BY CURRENCYID";
					//@@ 20050524 for PBN-1759
				}
				if(radioChkd.equalsIgnoreCase("E"))
				{
					strsql	=	" SELECT DISTINCT CM.CURRENCYID	FROM FS_COUNTRYMASTER CM,FS_FR_CURRENCYMASTER C "+
								" WHERE	 CM.CURRENCYID NOT IN (SELECT CURRENCY2	FROM FS_FR_CURRENCYMASTER WHERE	CURRENCY1='"+baseCurrency+"' AND IEFLAG='E') ORDER BY	CURRENCYID";
				}*/
				//rs=stmt.executeQuery(strsql);
			  cstmt        = connection1.prepareCall("{ ?= call QMS_SETUPS.GET_SELECTEDCURRENCY(?,?,?,?)}");
              cstmt.registerOutParameter(1,OracleTypes.CURSOR);
              cstmt.setString(2,terminalId);
              cstmt.setString(3,baseCurrency);
              cstmt.setString(4,operation);
              cstmt.setString(5,radioChkd);          
              
              cstmt.execute();
              rs  =  (ResultSet)cstmt.getObject(1);
				while(rs.next())
				{
					if(radioChkd.equalsIgnoreCase("I"))
					{
						cFactor	= rs.getString(1);
						vector.add(cFactor);
					}
					else if(radioChkd.equalsIgnoreCase("E"))
					{
							cFactor	= rs.getString(1);
							vector.add(cFactor);
					}
				}
					
			}
			catch(Exception	e)
			{
				e.printStackTrace();
				//Logger.error(FILE_NAME,"CurrencyConversionSessionBean.java : getSelectedCurrency()" + e);
        logger.error(FILE_NAME+"CurrencyConversionSessionBean.java : getSelectedCurrency()" + e);
				throw new EJBException(e.toString());
			}
	  finally
      {
      try
      {
        if(rs!=null)
        rs.close();
        if(cstmt!=null)
					cstmt.close();
          if(connection1!=null)
					connection1.close();
      }
      catch(Exception e)
      {
        e.printStackTrace();
      }
      }
			return vector;
		}

		/**
   * 
   * @param baseCurrency
   * @param radioChkd
   * @return 
   */
		public java.util.Vector	getModifiedCurrencyList(String baseCurrency,String radioChkd,String terminalId,String operation)
		{
			InitialContext ictx		=	null;
		// @@ 'connection' is replaced with 'connection1' by Suneetha for TogehterJ on 12 Jan 05
			Connection connection1	=	null;
			//Statement stmt			=	null;//Commented By RajKumari on 30-10-2008 for Connection Leakages.
			ResultSet rs			=	null;
			String	cFactor			=	"";
			String conFactors		=	"";
			Vector vector			=	new Vector();
			String strsql			=	"";
			CallableStatement cstmt = null;
			try
			{
				if(	dataSource == null )
				{
					ictx = new InitialContext();
					dataSource = ( DataSource )	ictx.lookup( "java:comp/env/jdbc/DB" );
				}
				connection1 = dataSource.getConnection();
				//stmt=connection1.createStatement();//Commented By RajKumari on 30-10-2008 for Connection Leakages.
			/*	if(radioChkd.equalsIgnoreCase("I"))
				{
					strsql	=  "SELECT DISTINCT	CURRENCY2 FROM FS_FR_CURRENCYMASTER WHERE CURRENCY1='"+baseCurrency+"' AND IEFLAG='I' " 			+ "ORDER BY	CURRENCY2";
				}
				if(radioChkd.equalsIgnoreCase("E"))
				{
					strsql	=	"SELECT DISTINCT CURRENCY2 FROM FS_FR_CURRENCYMASTER WHERE CURRENCY1='"+baseCurrency+"' AND IEFLAG='E' " 			+ "ORDER BY	CURRENCY2";
				}*/
				//rs=stmt.executeQuery(strsql);
        
			cstmt        = connection1.prepareCall("{ ?= call QMS_SETUPS.GET_MODIFIEDCURRENCY(?,?,?,?)}");
              cstmt.registerOutParameter(1,OracleTypes.CURSOR);
              cstmt.setString(2,terminalId);
              cstmt.setString(3,baseCurrency);
              cstmt.setString(4,operation);
              cstmt.setString(5,radioChkd);          
              
              cstmt.execute();
              rs  =  (ResultSet)cstmt.getObject(1);
				while(rs.next())
				{
					if(radioChkd.equalsIgnoreCase("I"))
					{
						cFactor	= rs.getString(1);
						vector.add(cFactor);
					}
					else if(radioChkd.equalsIgnoreCase("E"))
					{
							cFactor	= rs.getString(1);
							vector.add(cFactor);
					}
				}
					
			}
			catch(Exception	e)
			{
				e.printStackTrace();
				//Logger.error(FILE_NAME,"CurrencyConversionSessionBean.java : getSelectedCurrency()" + e);
        logger.error(FILE_NAME+"CurrencyConversionSessionBean.java : getSelectedCurrency()" + e);
				throw new EJBException(e.toString());
			}finally
      {
      try
      {
        if(rs!=null)
        rs.close();
        if(cstmt!=null)
					cstmt.close();
          if(connection1!=null)
					connection1.close();
      }
      catch(Exception e)
      {
        e.printStackTrace();
      }
      }
			return vector;
		}//End Of Modify LOV Method

		/**
   * 
   * @param companyid
   * @return 
   */
		public java.util.Vector	getCurrencyList1(String	companyid)
		{
			InitialContext ictx		=	null;
			String companyCurrency	=	null;
			String sqlQuery			=	null;
			CallableStatement cstmt = null;
			if(!companyid.equals("ALL"))
      {
			companyCurrency = this.getHcurrency(companyid);
      }
		// @@ 'connection' is replaced with 'connection1' by Suneetha for TogehterJ on 12 Jan 05
			Connection connection1	=	null;
			//Statement stmt			=	null,stmt1=null;//Commented By RajKumari on 30-10-2008 for Connection Leakages.
			ResultSet rs			=	null;
			//ResultSet rs1			=	null;//Commented By RajKumari on 30-10-2008 for Connection Leakages.
			String COUNTRY			=	null;
			Vector currencyid= new Vector();
			try
			{
				if(	dataSource == null )
				{
					ictx = new InitialContext();
					dataSource = ( DataSource )	ictx.lookup( "java:comp/env/jdbc/DB" );
				}
					//sqlQuery	= "SELECT DISTINCT	C.CURRENCY2,CM.COUNTRYNAME FROM	FS_FR_CURRENCYMASTER C,	FS_COUNTRYMASTER	CM WHERE C.CURRENCY2=CM.CURRENCYID ORDER BY	CURRENCY2";
					connection1	= dataSource.getConnection();
					//stmt		= connection1.createStatement();
					//rs			= stmt.executeQuery(	sqlQuery );
			  cstmt        = connection1.prepareCall("{ ?= call QMS_SETUPS.GET_CURRENTLIST(?)}");
              cstmt.registerOutParameter(1,OracleTypes.CURSOR);
              cstmt.setString(2,companyid);
              cstmt.execute();
              rs  =  (ResultSet)cstmt.getObject(1);
					while( rs.next())
					{
						currencyid.addElement(rs.getString(1));
					}
			}
			catch( SQLException	sqle)
			{
				sqle.printStackTrace();
				//Logger.error(FILE_NAME,"CurrencyConversionSessionBean :  getCurrencyList1()"+sqle);
        logger.error(FILE_NAME+"CurrencyConversionSessionBean :  getCurrencyList1()"+sqle);
				throw new EJBException(sqle.toString());
			}
			catch(javax.naming.NamingException nme)
			{
				nme.printStackTrace();
				//Logger.error(FILE_NAME,"CurrencyConversionSessionBean :  getCurrencyList1()"+nme	);
        logger.error(FILE_NAME+"CurrencyConversionSessionBean :  getCurrencyList1()"+nme	);
				throw new EJBException(nme.toString());
			}
			catch( Exception	sqle)
			{
				sqle.printStackTrace();
				//Logger.error(FILE_NAME,"CurrencyConversionSessionBean :  getCurrencyList1()"+sqle);
        logger.error(FILE_NAME+"CurrencyConversionSessionBean :  getCurrencyList1()"+sqle);
				throw new EJBException(sqle.toString());
			}
			finally
			{
				try
				{
					ictx =	null;
					if(	rs != null )
          {
					rs.close();
          }
					if(	cstmt !=	null )
          {
					cstmt.close();
          }
					if(	connection1 != null )
          {
					connection1.close();
          }
				}
			catch(SQLException sqle)
			{
				//Logger.error(FILE_NAME,"CurrencyConversionSessionBean.java : getCurrencyList1()"+sqle);
        logger.error(FILE_NAME+"CurrencyConversionSessionBean.java : getCurrencyList1()"+sqle);
			}
			}
			return currencyid;
		}

		/**
   * 
   * @return 
   * @throws java.sql.SQLException
   */
public String getUserTerminalType() throws SQLException
	{
		ResultSet  rs			= null;
		Statement  stmt			= null;
		// @@ 'connection' is replaced with 'connection1' by Suneetha for TogehterJ on 12 Jan 05
		Connection connection1	= null;
		String sqlQuery			= null;
		String	userTerminal	= null;
		try
		{
			connection1=operationsImpl.getConnection();
			stmt=connection1.createStatement();
			sqlQuery="SELECT TERMINALID	FROM FS_FR_TERMINALMASTER WHERE OPER_ADMIN_FLAG='H'";
			rs=	stmt.executeQuery(sqlQuery);
			if(	rs.next() )
      {
				userTerminal = rs.getString(1);
      }
			else
      {
				userTerminal = null;
      }
		}
		catch(SQLException sqlexp)
		{
			
			//Logger.error(FILE_NAME,"SQLException in getUserTerminalType in TerminalRegistration SessionBean : ",sqlexp);
      logger.error(FILE_NAME+"SQLException in getUserTerminalType in TerminalRegistration SessionBean : ",sqlexp);
			throw new EJBException(sqlexp.toString());
		}
		catch(EJBException rExp)
		{
			//Logger.error(FILE_NAME,"EJBException in getUserTerminalType in TerminalRegistration SessionBean : ",rExp);
      logger.error(FILE_NAME+"EJBException in getUserTerminalType in TerminalRegistration SessionBean : ",rExp);
			throw new EJBException(rExp.toString());
		}
		finally
		{
			try
       		{
       			if( rs!=null )
            {
       				rs.close();
            }
       			if( stmt!=null )
            {
       				stmt.close();
            }
       			if( connection1!=null )
            {
       				connection1.close();
            }
       		}
       		catch( Exception e )
       		{
				//Logger.error(FILE_NAME,"Exception in finally block of getUserTerminalType() method of TerminalInformationSessionBean ",e);
        logger.error(FILE_NAME+"Exception in finally block of getUserTerminalType() method of TerminalInformationSessionBean ",e);
	    	}
		}
		return userTerminal;
	} // End of getUserTerminalType

	/**
   * 
   * @return 
   */
	public String[] getCompanyIds() 
	{
		String 		strCompanyIds[]  = null;    // a String array to store company ids
		Statement	stmt = null;    // a Statement variable
		ResultSet 	rs = null;    // a ResultSet variable
		int nRows  = 0;    // integer to store number of rows
		int  i=0;     // an integer to maintain count
        int nRows1 = 0;    // integer to store number of rows
		int nRows2 = 0;    // integer to store number of rows
        Connection connection=null;
		try
		{
			String sql1 = "SELECT COUNT(*) NO_ROWS FROM FS_COMPANYINFO WHERE AGENTJVIND IN('C')";    // a String having  a sql query
			String sql3 = "SELECT COUNT(*) NO_ROWS FROM FS_COMPANYINFO WHERE AGENTJVIND IN('J','A')";    // a String having  a sql query
			String sql2 = "SELECT COMPANYID,AGENTJVIND AJV FROM FS_COMPANYINFO WHERE AGENTJVIND IN('C','J','A')  ORDER BY COMPANYID";    // a String having  a sql query
			connection = operationsImpl.getConnection();
			stmt = connection.createStatement();
			rs = stmt.executeQuery(sql1);
			while( rs.next() )
			{
				nRows1 = rs.getInt("NO_ROWS");
			}
			rs = stmt.executeQuery(sql3);
			while( rs.next() )
			{
				nRows2 = rs.getInt("NO_ROWS");
			}
			nRows = nRows1+nRows2;
			if( nRows==0 )
      {
				return null;
      }
			strCompanyIds = new String[ nRows ];
      //Added By RajKumari on 30-10-2008 for Connection Leakages.
      if(stmt !=null)
      {
        stmt.close();
        stmt = null;
      }
			stmt = connection.createStatement();
			rs = stmt.executeQuery( sql2 );
			while( rs.next() )
			{
				strCompanyIds[i] = rs.getString("COMPANYID")+"#"+rs.getString("AJV");
				i++;
			}
			return strCompanyIds;
		}
		catch(SQLException sqlexp )
		{
			
			//Logger.error(FILE_NAME,"Exception in  getCompanyIds() method of TerminalRegistrationSessionBean : "+sqlexp);
      logger.error(FILE_NAME+"Exception in  getCompanyIds() method of TerminalRegistrationSessionBean : "+sqlexp);
			return null;
		}
		catch(Exception exp )
		{
			//Logger.error(FILE_NAME,"Exception in  getCompanyIds()() method of TerminalRegistrationSessionBean : "+exp);
      logger.error(FILE_NAME+"Exception in  getCompanyIds()() method of TerminalRegistrationSessionBean : "+exp);
			return null;
		}
		finally
		{
			try
			{
				if( rs!=null )
        {
					rs.close();
        }
				if( stmt!=null )
        {
					stmt.close();
        }
				if( connection!=null )
        {
					connection.close();
        }
			}
			catch(Exception exp )
			{
				//Logger.error(FILE_NAME,"Exception in  getCompanyIds()() method of TerminalRegistrationSessionBean : "+exp);
        logger.error(FILE_NAME+"Exception in  getCompanyIds()() method of TerminalRegistrationSessionBean : "+exp);
               throw new EJBException("Exception in  getCompanyIds()() method of TerminalRegistrationSessionBean : "+exp.toString());
			}
		}
	}

	/**
   * 
   * @return 
   * @throws java.sql.SQLException
   */
public boolean checkHOTerminal() throws SQLException
	{
		ResultSet  rs			= null;
		Statement  stmt			= null;
		// @@ 'connection' is replaced with 'connection1' by Suneetha for TogehterJ on 12 Jan 05
		Connection connection1	= null;
		String sqlQuery			= null;
		boolean	flag			= false;
		try
		{
			connection1=operationsImpl.getConnection();
			stmt=connection1.createStatement();
			sqlQuery="SELECT TERMINALID	FROM FS_FR_TERMINALMASTER WHERE OPER_ADMIN_FLAG='H'";
			rs=	stmt.executeQuery(sqlQuery);
			if(	rs.next() )
      {
				flag=true;
      }
			else
      {
				flag= false;
      }
		}
		catch(SQLException sqlexp)
		{
			
			//Logger.error(FILE_NAME,"SQLException in checkHOTerminal in TerminalRegistration SessionBean : ",sqlexp);
      logger.error(FILE_NAME+"SQLException in checkHOTerminal in TerminalRegistration SessionBean : "+sqlexp);
			throw new EJBException(sqlexp.toString());
		}
		catch(EJBException rExp)
		{
			//Logger.error(FILE_NAME,"EJBException in checkHOTerminal in TerminalRegistration SessionBean : ",rExp);
      logger.error(FILE_NAME+"EJBException in checkHOTerminal in TerminalRegistration SessionBean : "+rExp);
			throw new EJBException(rExp.toString());
		}
		finally
		{
			try
       		{
       			if( rs!=null )
            {
       				rs.close();
            }
       			if( stmt!=null )
            {
       				stmt.close();
            }
       			if( connection1!=null )
            {
       				connection1.close();
            }
       		}
       		catch( Exception e )
       		{
				//Logger.error(FILE_NAME,"Exception in finally block of checkHOTerminal() method of TerminalInformationSessionBean ",e);
        logger.error(FILE_NAME+"Exception in finally block of checkHOTerminal() method of TerminalInformationSessionBean "+e);
	    	}
		}
		return flag;
	} // End of checkHOTerminal

	/**
   * 
   * @param shipmentMode
   * @return 
   */
	public Vector getLocationInfo(String shipmentMode,String countryId,String companyId) 
	{
		Vector vecLocationInfo  = new Vector();    // a Vector to store location information
		Statement stmt 			    = null;    // a Statement variable
		ResultSet rs 			      = null;    // a ResultSet variable
		String sqlQuery 		    = null;
    Connection connection   = null;

		String whereClause      = "";
    String subQuery         = "";


    //System.out.println("shipmentMode"+shipmentMode+"countryId"+countryId);
    
		if (shipmentMode == null || shipmentMode.trim().length() == 0)
		{
			whereClause = "WHERE ";
		}
    
    if(companyId!=null && companyId.trim().length()!=0)
      subQuery    = " SELECT TL.LOCATIONID FROM FS_FR_TERMINALLOCATION TL,FS_FR_TERMINALMASTER TM WHERE TL.TERMINALID=TM.TERMINALID AND TM.COMPANYID='"+companyId+"'";
    else
      subQuery    = " SELECT LOCATIONID  FROM FS_FR_TERMINALLOCATION ";

		if(countryId ==null || countryId.trim().length() == 0)
		{
			System.out.println("Null country Id " );	
		}
		else
		{
     if(whereClause==null || "".equals(whereClause.trim()))
     {whereClause = " WHERE "; }
			whereClause = whereClause + " COUNTRYID='"+countryId+"' AND";
		}
	

		//System.out.println("Where Clause:"+whereClause+"shipmentMode:"+shipmentMode+"countryId:"+countryId);
		try
		{
			//if(opTerminalType!=null && opTerminalType.equals("O"))
//				sqlQuery 	= "SELECT LOCATIONID, LOCATIONNAME, COUNTRYID,SHIPMENTMODE FROM FS_FR_LOCATIONMASTER  "+shipmentMode+" "+countryId+" LOCATIONID NOT IN (SELECT LOCATIONID  FROM FS_FR_TERMINALLOCATION) ORDER BY LOCATIONID";    // a String having  a sql query
				sqlQuery 	= "SELECT LOCATIONID, LOCATIONNAME, COUNTRYID,SHIPMENTMODE FROM FS_FR_LOCATIONMASTER  "+ whereClause +" LOCATIONID NOT IN ("+subQuery+") ORDER BY LOCATIONID";    // a String having  a sql query
			//else
			//	sqlQuery	= " SELECT CHILD_TERMINAL_ID FROM FS_FR_TERMINAL_REGN WHERE "+
			//				  " PARENT_TERMINAL_ID = '"+terminalId+"'";
			//Logger.info(FILE_NAME,"SQLQuery (getLocationInfo): "+sqlQuery);
			connection = operationsImpl.getConnection();
			stmt = connection.createStatement();
			rs = stmt.executeQuery(sqlQuery);
			while(rs.next())
			{
             // @@ Replaced by Suneetha for TogetherJ on 12 Jan 05
			/*	String strCarrierInfo = "";
				strCarrierInfo = strCarrierInfo +rs.getString(1)+"%"+rs.getString(2)+"%"+rs.getString(3)+"%"+rs.getInt(4);
				vecLocationInfo.addElement( strCarrierInfo );
            */ 
				StringBuffer strCarrierInfo = new StringBuffer();
				strCarrierInfo.append(strCarrierInfo);
				strCarrierInfo.append(rs.getString(1));
				strCarrierInfo.append("%");
				strCarrierInfo.append(rs.getString(2));
				strCarrierInfo.append("%");
				strCarrierInfo.append(rs.getString(3));
				strCarrierInfo.append("%");
				strCarrierInfo.append(rs.getInt(4));
				vecLocationInfo.addElement( strCarrierInfo.toString());
             // @@
			}
		}
		catch(SQLException sqlexp )
		{
			//Logger.error(FILE_NAME,"Exception in  getLocationInfo() method of TerminalRegistrationSessionBean : "+sqlexp);
      logger.error(FILE_NAME+"Exception in  getLocationInfo() method of TerminalRegistrationSessionBean : "+sqlexp);
			return null;
		}
		catch(Exception exp )
		{
			//Logger.error(FILE_NAME,"Exception in  getLocationInfo) method of TerminalRegistrationSessionBean : "+exp);
      logger.error(FILE_NAME+"Exception in  getLocationInfo) method of TerminalRegistrationSessionBean : "+exp);
			return null;
		}
		finally
		{
			try
			{
				if( rs!=null )
        {
					rs.close();
        }
				if( stmt!=null )
        {
					stmt.close();
        }
				if( connection!=null )
        {
					connection.close();
        }
			}
			catch(Exception exp )
			{
				//Logger.error(FILE_NAME,"Exception in  getLocationInfo() method of TerminalRegistrationSessionBean : "+exp);
        logger.error(FILE_NAME+"Exception in  getLocationInfo() method of TerminalRegistrationSessionBean : "+exp);
	            throw new EJBException("Exception in  getLocationInfo() method of TerminalRegistrationSessionBean : "+exp.toString());
			}
		}
		return vecLocationInfo;
	}

	/**
   * 
   * @param id
   * @return 
   * @throws java.sql.SQLException
   */
public boolean isExists(String id) throws SQLException
	{
		ResultSet  rs			= null;
		Statement  stmt			= null;
		// @@ 'connection' is replaced with 'connection1' by Suneetha for TogehterJ on 12 Jan 05
		Connection connection1	= null;
		String sqlQuery			= null;
		boolean	flag			= false;
		try
		{
			connection1=operationsImpl.getConnection();
			stmt=connection1.createStatement();
			sqlQuery="SELECT TERMINALID	FROM FS_FR_TERMINALMASTER WHERE TERMINALID='"+id+"'";
			rs=	stmt.executeQuery(sqlQuery);
			if(	rs.next() )
      {
				flag=true;
      }
			else
      {
				flag= false;
      }
		}
		catch(SQLException sqlexp)
		{
			
			//Logger.error(FILE_NAME,"SQLException in isExists in TerminalRegistration SessionBean : ",sqlexp);
      logger.error(FILE_NAME+"SQLException in isExists in TerminalRegistration SessionBean : "+sqlexp);
			throw new EJBException(sqlexp.toString());
		}
		catch(EJBException rExp)
		{
			//Logger.error(FILE_NAME,"EJBException in isExists in TerminalRegistration SessionBean : ",rExp);
      logger.error(FILE_NAME+"EJBException in isExists in TerminalRegistration SessionBean : "+rExp);
			throw new EJBException(rExp.toString());
		}
		finally
		{
			try
       		{
       			if( rs!=null )
            {
       				rs.close();
            }
       			if( stmt!=null )
            {
       				stmt.close();
            }
       			if( connection1!=null )
            {
       				connection1.close();
            }
       		}
       		catch( Exception e )
       		{
				//Logger.error(FILE_NAME,"Exception in finally block of isExists() method of TerminalInformationSessionBean ",e);
        logger.error(FILE_NAME+"Exception in finally block of isExists() method of TerminalInformationSessionBean "+e);
	    	}
		}
		return flag;
	} // End of isExists

	/**
   * 
   * @param terminalRegObj
   * @param address
   * @param esupplyGlobalParameters
   * @return 
   */
  public String setTerminalRegDetails(	TerminalRegJspBean		terminalRegObj ,
										Address					address,
										ESupplyGlobalParameters esupplyGlobalParameters )
  {
  		Context initial										= null;	// variable to store InitialContext
        TerminalRegistrationEntityHome  terminalRegHome		= null;	// variable to store entity bean Home Object
        TerminalRegistrationEntity 		terminalRegRemote	= null;	// variable to store entity bean remote Object
	//	CompanyInfoSession				compInfoRemote		= null;
	//	CompanyInfoSessionHome			compInfoHome		= null;
		// @@ 'connection' is replaced with 'connection1' by Suneetha for TogehterJ on 12 Jan 05
        Connection						connection1			= null;	// variable for Connection
        Statement						stmt 				= null;	// variable for Statement
        ResultSet						rs					= null;	// variable for ResultSet
		int								shipMode			= 0;
   		try
   	    {
               String   terminalId  =  terminalRegObj.getTerminalId();    // String to store termianl id.
               String	sql			= "SELECT COUNT(*) NO_ROWS FROM FS_FR_TERMINALMASTER WHERE TERMINALID='"+ terminalId +"'";
			   connection1	= operationsImpl.getConnection();
			   stmt			= connection1.createStatement();
			   rs			= stmt.executeQuery( sql );
			   int cnt		= 0;
			   if(rs.next())
         {
			   	 cnt = rs.getInt("NO_ROWS");
         }
   	    	   if( cnt > 0 )
             {
   	    	   		return null;
             }
				initial						=  new InitialContext();
				terminalRegHome				=  (TerminalRegistrationEntityHome)initial.lookup("TerminalRegistrationEntityBean");

				Address address1			=  terminalRegObj.getAddress();			// creating Address Object
				String   companyid			=  terminalRegObj.getCompanyId();		// String to store companyid
				String   add				=  terminalRegObj.getContactName();		// String to store contact name
				String   desg				=  terminalRegObj.getDesignation();		// String to store designation
				String   notes				=  terminalRegObj.getNotes();			// String to store notes
				String   gate				=  terminalRegObj.getAgentInd();		// String to store agent ind.
				String   iata				=  terminalRegObj.getIataCode();		// String to store IATA code
				String   account			=  terminalRegObj.getAccountCode();		// String to store account code
				String   tax				=  terminalRegObj.getTaxRegNo();		// String to store tax registration number
				String   bankName			=  terminalRegObj.getBankName();		// String to store bank Name
				String   branchName			=  terminalRegObj.getBranchName();		// String to store branch Name
				String   bankAcctNo			=  terminalRegObj.getAccountNumber();	// String to store bank Account Number
				String   bankCity			=  terminalRegObj.getBankCity();		// String to store bank city
				String   bankAddress	    =  terminalRegObj.getBankAddress();		// String to store bank Address
				String   invoiceCategory	=  terminalRegObj.getInvoiceCategory();	// string to store invoicecategory
				String   terminalType		=  terminalRegObj.getTerminalType();	// string to store terminalType
				String   opTerminalType		=  terminalRegObj.getOperationTerminalType();
				String   emailStatus		=  terminalRegObj.getEmailStatus();
				String   timeZone			=  terminalRegObj.getTimeZone();
				String   shipmentCollect	=  terminalRegObj.getCollectShipment();
				String   shipmentMode		=  terminalRegObj.getShipmentMode();
			    String   serverTimeDiff     =  terminalRegObj.getServerTimeDiff();
				int      discrepancy		=  terminalRegObj.getDiscrepancy();		// int to store discrepancy
				double   interestRate		=  terminalRegObj.getInterestRate();	// double to store interestRate
			   	int		 weightScale		=  terminalRegObj.getWeightScale();	
				//@@ Srivegi Added on 20050419 (Invoice-PR)
 			    String   stockedInvoiceIdsCheck     =  terminalRegObj.getStockedInvoiceIdsCheck();
				//@@ 20050419 (Invoice-PR)
			   String childTerminalFlag = terminalRegObj.getChildTerminalFlag();		
         // Added By RajKumari on 11/28/2008 for 146448 starts
         String 	frequency	   = terminalRegObj.getFrequency();
         String 	carrier	     = terminalRegObj.getCarrier();
         String 	transitTime	 = terminalRegObj.getTransitTime();
         String 	rateValidity	 = terminalRegObj.getRateValidity();
         // Added By RajKumari on 11/28/2008 for 146448 ends
			   // checking agent ind
			   if(gate != null && gate.equalsIgnoreCase("COMPANY"))
         {
			   gate = "c";
         }
			   //Logger.info(FILE_NAME,"7");
			   if(gate != null && gate.equalsIgnoreCase("AGENT"))
         {
			   gate = "a";
         }
			   if(gate != null && gate.equalsIgnoreCase("JOINT VENTURE"))
         {
			   gate = "j";
         }
				try
				{
					if(shipmentMode != null)
          {
						shipMode	=	Integer.parseInt(shipmentMode.trim());
          }
				}
				catch(NumberFormatException nEx)
				{
					//Logger.error(FILE_NAME,"Exception in setTerminalData()...ETransHOAdminSetupSessionBean.."+nEx.toString());
          logger.error(FILE_NAME+"Exception in setTerminalData()...ETransHOAdminSetupSessionBean.."+nEx.toString());
				}
				/*
                terminalRegRemote = (TerminalRegistrationEntity)terminalRegHome.create( terminalId ,
               																			 companyid,
																						 add,
																						 desg,
																						 notes,
																						 address.getAddressId(),
																						 gate,
																						 iata,
																						 account,
																						 tax,
																						 address,
																						 bankName,
																						 branchName,
																						 bankAcctNo,
																						 bankAddress,
																						 bankCity,
																						 discrepancy,
																						 interestRate,
																						 invoiceCategory,
																						 terminalType,
																						 opTerminalType,
																						 emailStatus,
																						 timeZone,
																						 shipmentCollect,
																						 shipMode,
                                                                                		serverTimeDiff,
																						 weightScale);
			*/
			//@@ Avinash added on 20041222 
			terminalRegObj.setAddress(address);
			//@@ 20041222
  
			terminalRegRemote = (TerminalRegistrationEntity)terminalRegHome.create( terminalRegObj);
 
			String str = terminalRegObj.getOperationTerminalType();
            if(str==null ||  str.equals("O"))	  
            {
				//@@ Avinash replaced on 20041208 (removing methods with SQL from CMP)
				//terminalRegRemote.setLocationId(terminalId,terminalRegObj.getLocationId()); // setting location
				this.setLocationId(terminalId,terminalRegObj.getLocationId(),connection1); // setting location
				//@@ 20041208 (removing methods with SQL from CMP)
            }
			else
      {
				setOperationTerminalIds(terminalId,terminalRegObj.getLocationId(),terminalRegObj.getCBTLocationId(),esupplyGlobalParameters);	
      }
      // setting location
		 	if(str==null ||  str.equals("O"))
		 	{
		 		insertGatewayDetails( terminalId,  shipMode,		// inserting gateway details
									  address.getCity(),
									  notes,
									  add,
									  address.getAddressId(),
									  notes,
									  timeZone,
									  shipmentCollect,
									  terminalType,
                                      connection1);
		 	}
		//	long bookId = esupplyGlobalParameters.getAccountsCredentials().getBookId();		// a long variable to store book id
		//	String oldTerminalId =  esupplyGlobalParameters.getTerminalId();					// String to store old terminal id

		/*
			Posting Accounting default entries like Vouchers and ledgers
			as and when create the Terminal from ETrans
								Added by Ram Kishore
		*/
  	   String 	module			 =	"ETAT";
	   String	currencyId		 =	null;
	   String	dateFormat		 =	"dd/mm/yy" ;	//For time being dd/mm/yy is treated as default Date Format
	   String	sqlCurrency	 	 = 	"SELECT  CURRENCYID FROM FS_COUNTRYMASTER WHERE COUNTRYID='"+ address.getCountryId() +"'";
	   rs						 =	stmt.executeQuery( sqlCurrency );
	   if(rs.next())
     {
	   		currencyId	=	rs.getString(1);
     }
	   Timestamp tsCreationDate =	esupplyGlobalParameters.getLocalTime();
	  // compInfoHome 			= 	(CompanyInfoSessionHome)initial.lookup("CompanyInfoSessionBean");
	  /// compInfoRemote 			=	(CompanyInfoSession)compInfoHome.create();
       //                 insertAcctCompInfo(  notes,
	//									   terminalId,
	//									   address.getAddressId(),
	//									   currencyId,
	//									   dateFormat,
	//									   module,
	//									   tsCreationDate,
	//									   timeZone,
      //                                     connection1);
		//compInfoRemote.insertAcctCompInfo( notes, terminalid, address.getAddressId(),currencyId,dateFormat,module);
		 //End Accounting entries
		operationsImpl.setTransactionDetails(	esupplyGlobalParameters.getTerminalId(),
												esupplyGlobalParameters.getUserId(),
												"TerminalReg",
												terminalId,
												esupplyGlobalParameters.getLocalTime(),
												"ADD" );
			   return terminalId;
   		 }
   		 catch( Exception exp )
   		 {
			//Logger.error(FILE_NAME,"Exception in  setTerminalRegDetails() method of TerminalRegistrationSessionBean : "+exp);
      logger.error(FILE_NAME+"Exception in  setTerminalRegDetails() method of TerminalRegistrationSessionBean : "+exp);
   		    throw new EJBException( "Exception in  setTerminalRegDetails() method of TerminalRegistrationSessionBean : "+exp.toString() );
   		 }
   		 finally
	 	 {
	 	 		initial 			= null;
	 	 		terminalRegHome		= null;
	 	 		terminalRegRemote	= null;
	 	 		try
 	 			{
 					if( rs!=null )
          {
 						rs.close();
          }
 					if( stmt!=null )
          {
 						stmt.close();
          }
 					if( connection1!=null )
          {
 						connection1.close();
          }
 	 			}
 	 			catch( Exception e )
 				{
					//Logger.error(FILE_NAME,"Final Exception in  setTerminalRegDetails() method of TerminalRegistrationSessionBean : "+e);
          logger.error(FILE_NAME+"Final Exception in  setTerminalRegDetails() method of TerminalRegistrationSessionBean : "+e);
		   		    throw new EJBException( "Exception in  setTerminalRegDetails() method of TerminalRegistrationSessionBean : "+e.toString() );
				}
 		 }
    }  // END OF SET   TERMINALREG DETAILS

	public Vector getOperationTerminalInfo(	String					terminalId,
											ESupplyGlobalParameters loginbean,
											String					adminType,
											String					like,
                      String          fetchTerminalType) 
	{
		Vector		vecLocationInfo		= new Vector();		// a Vector to store location information
		Statement	stmt				= null;				// a Statement variable
		ResultSet	rs					= null;				// a ResultSet variable
		String		sqlQuery			= null;
        Connection  connection          =   null;
		try
		{
			if(adminType!=null)
			{
				if(adminType.equals("A"))	// IF USER IS TRYING TO CREATE AN ADMIN TERMINAL
				{
					if(terminalId==null)	// IF LOGIN TYPE IS LICENCEE TERMINALID WILL BE NULL
					{
						sqlQuery =  "SELECT TERMINALID, TERMINALID,TERMINALID "+
									"FROM	"+
											"FS_FR_TERMINALMASTER	"+									
											"WHERE "+
											//JS
									//		"TERMINALID NOT IN	"+
									//		"(SELECT CHILD_TERMINAL_ID FROM FS_FR_TERMINAL_REGN ) AND "+
								  			" TERMINALID LIKE '"+like+"%' AND "+
									//		"OPER_ADMIN_FLAG != 'H' ";//Comment By I.V.Sekhar
                  "OPER_ADMIN_FLAG = '"+fetchTerminalType+"' AND INVALIDATE='F' ";
					}
					else	// IF LOGIN TRYE IS NOT LINCINCEE
					{
					  /*sqlQuery  =	"SELECT TERMINALID, TERMINALID,TERMINALID  "+
					 				"FROM	"+
											"FS_FR_TERMINALMASTER	"+
									"WHERE	"+
											"TERMINALID NOT IN "+
											"(SELECT CHILD_TERMINAL_ID FROM FS_FR_TERMINAL_REGN ) "+
											"AND TERMINALID LIKE '"+like+"%' "+
											"AND OPER_ADMIN_FLAG IN('O','A') AND "+
											"TERMINALID !='"+terminalId+"' ORDER BY 1 ";*/
						 sqlQuery  =	"SELECT TERMINALID, TERMINALID,TERMINALID "+
										"FROM	"+
												"FS_FR_TERMINALMASTER   "+
							 //JS
										"WHERE   "+
								//					"TERMINALID NOT IN(	SELECT  PARENT_TERMINAL_ID  "+
								//										"FROM	"+
								//												"FS_FR_TERMINAL_REGN "+
								//										"WHERE  "+
								//												"CHILD_TERMINAL_ID='"+terminalId+"' )	"+
								//		"AND TERMINALID NOT IN (SELECT CHILD_TERMINAL_ID  "+
								//								"FROM	"+
								//										"FS_FR_TERMINAL_REGN ) AND	"+
										" TERMINALID LIKE '"+like+"%'  AND  "+
                //    "OPER_ADMIN_FLAG IN('O','A')  "+//Comment By I.V.Sekhar
                    "OPER_ADMIN_FLAG = '"+fetchTerminalType+"'"+
										"AND TERMINALID !='"+terminalId+"' AND INVALIDATE='F' ";
					}
				}
				else
				{
					sqlQuery =		"SELECT TERMINALID, TERMINALID,TERMINALID "+
									"FROM	"+
											"FS_FR_TERMINALMASTER "+
									"WHERE	"+
									//		"OPER_ADMIN_FLAG IN('O','A') " ;//Comment By I.V.Sekhar
                  "OPER_ADMIN_FLAG = '"+fetchTerminalType+"' AND INVALIDATE='F' ";
						//JS
								//"AND TERMINALID NOT IN	"+
								//	"(SELECT CHILD_TERMINAL_ID FROM FS_FR_TERMINAL_REGN) ";
				}
			}
      //ADDED BY I.V.SEKHAR--TO FILTER THE TERMINAL IDS WHICH ARE ALREADY MAPPED
      sqlQuery = sqlQuery+" AND TERMINALID NOT IN(SELECT  CHILD_TERMINAL_ID FROM FS_FR_TERMINAL_REGN) ORDER BY 1";
      
			//Logger.info(FILE_NAME,"222222222222222222 JS sqlQuery : "+sqlQuery);
			connection	= operationsImpl.getConnection();
			stmt		= connection.createStatement();
			rs			= stmt.executeQuery(sqlQuery);
			while(rs.next())
			{
			// @@ Replaced by Suneetha for TogetherJ on 12 Jan 05
			/*	String strCarrierInfo = "";
				strCarrierInfo = strCarrierInfo +rs.getString(1)+"%"+rs.getString(2)+"%"+rs.getString(3);
				vecLocationInfo.addElement( strCarrierInfo );
			*/
				StringBuffer strCarrierInfo = new StringBuffer();
				strCarrierInfo.append(strCarrierInfo);
				strCarrierInfo.append(rs.getString(1));
				strCarrierInfo.append("%");
				strCarrierInfo.append(rs.getString(2));
				strCarrierInfo.append("%");
				strCarrierInfo.append(rs.getString(3));
				vecLocationInfo.addElement( strCarrierInfo.toString());
			// @@
			}
		}
		catch(SQLException sqlexp )
		{
			//Logger.error(FILE_NAME,"Exception in  getOperationTerminalInfo() method of TerminalRegistrationSessionBean : "+sqlexp);
      logger.error(FILE_NAME+"Exception in  getOperationTerminalInfo() method of TerminalRegistrationSessionBean : "+sqlexp);
			return null;
		}
		catch(Exception exp )
		{
			//Logger.error(FILE_NAME,"Exception in  getOperationTerminalInfo() method of TerminalRegistrationSessionBean : "+exp);
      logger.error(FILE_NAME+"Exception in  getOperationTerminalInfo() method of TerminalRegistrationSessionBean : "+exp);
			return null;
		}
		finally
		{
			try
			{
				if( rs!=null )
        {
					rs.close();
        }
				if( stmt!=null )
        {
					stmt.close();
        }
				if( connection!=null )
        {
					connection.close();
        }
			}
			catch(Exception exp )
			{
				//Logger.error(FILE_NAME,"Exception in  getLocationInfo() method of TerminalRegistrationSessionBean : "+exp);
        logger.error(FILE_NAME+"Exception in  getLocationInfo() method of TerminalRegistrationSessionBean : "+exp);
	            throw new EJBException("Exception in  getLocationInfo() method of TerminalRegistrationSessionBean : "+exp.toString());
			}
		}
		return vecLocationInfo;
	}
	/**
   * 
   * @param terminalId
   * @param loginbean
   * @param adminType
   * @param like
   * @return 
   */
	public Vector getOperationTerminalInfo(	String					terminalId,
											ESupplyGlobalParameters loginbean,
											String					adminType,
											String					like) 
	{
		Vector		vecLocationInfo		= new Vector();		// a Vector to store location information
		Statement	stmt				= null;				// a Statement variable
		ResultSet	rs					= null;				// a ResultSet variable
		String		sqlQuery			= null;
        Connection  connection          =   null;
		try
		{
			if(adminType!=null)
			{
				if(adminType.equals("A"))	// IF USER IS TRYING TO CREATE AN ADMIN TERMINAL
				{
					if(terminalId==null)	// IF LOGIN TYPE IS LICENCEE TERMINALID WILL BE NULL
					{
						sqlQuery =  "SELECT TERMINALID, TERMINALID,TERMINALID "+
									"FROM	"+
											"FS_FR_TERMINALMASTER	"+									
											"WHERE "+
											//JS
									//		"TERMINALID NOT IN	"+
									//		"(SELECT CHILD_TERMINAL_ID FROM FS_FR_TERMINAL_REGN ) AND "+
								  			" TERMINALID LIKE '"+like+"%' AND "+
											"OPER_ADMIN_FLAG != 'H' "+
											"ORDER BY 1 ";
					}
					else	// IF LOGIN TRYE IS NOT LINCINCEE
					{
					  /*sqlQuery  =	"SELECT TERMINALID, TERMINALID,TERMINALID  "+
					 				"FROM	"+
											"FS_FR_TERMINALMASTER	"+
									"WHERE	"+
											"TERMINALID NOT IN "+
											"(SELECT CHILD_TERMINAL_ID FROM FS_FR_TERMINAL_REGN ) "+
											"AND TERMINALID LIKE '"+like+"%' "+
											"AND OPER_ADMIN_FLAG IN('O','A') AND "+
											"TERMINALID !='"+terminalId+"' ORDER BY 1 ";*/
						 sqlQuery  =	"SELECT TERMINALID, TERMINALID,TERMINALID "+
										"FROM	"+
												"FS_FR_TERMINALMASTER   "+
							 //JS
										"WHERE   "+
								//					"TERMINALID NOT IN(	SELECT  PARENT_TERMINAL_ID  "+
								//										"FROM	"+
								//												"FS_FR_TERMINAL_REGN "+
								//										"WHERE  "+
								//												"CHILD_TERMINAL_ID='"+terminalId+"' )	"+
								//		"AND TERMINALID NOT IN (SELECT CHILD_TERMINAL_ID  "+
								//								"FROM	"+
								//										"FS_FR_TERMINAL_REGN ) AND	"+
										" TERMINALID LIKE '"+like+"%'  AND  OPER_ADMIN_FLAG IN('O','A')  "+
										"AND TERMINALID !='"+terminalId+"' ORDER BY 1 ";
					}
				}
				else
				{
					sqlQuery =		"SELECT TERMINALID, TERMINALID,TERMINALID "+
									"FROM	"+
											"FS_FR_TERMINALMASTER "+
									"WHERE	"+
											"OPER_ADMIN_FLAG IN('O','A')  ORDER BY 1 " ;
						//JS
								//"AND TERMINALID NOT IN	"+
								//	"(SELECT CHILD_TERMINAL_ID FROM FS_FR_TERMINAL_REGN) ";
				}
			}
			//Logger.info(FILE_NAME,"222222222222222222 JS sqlQuery : "+sqlQuery);
			connection	= operationsImpl.getConnection();
			stmt		= connection.createStatement();
			rs			= stmt.executeQuery(sqlQuery);
			while(rs.next())
			{
			// @@ Replaced by Suneetha for TogetherJ on 12 Jan 05
			/*	String strCarrierInfo = "";
				strCarrierInfo = strCarrierInfo +rs.getString(1)+"%"+rs.getString(2)+"%"+rs.getString(3);
				vecLocationInfo.addElement( strCarrierInfo );
			*/
				StringBuffer strCarrierInfo = new StringBuffer();
				strCarrierInfo.append(strCarrierInfo);
				strCarrierInfo.append(rs.getString(1));
				strCarrierInfo.append("%");
				strCarrierInfo.append(rs.getString(2));
				strCarrierInfo.append("%");
				strCarrierInfo.append(rs.getString(3));
				vecLocationInfo.addElement( strCarrierInfo.toString());
			// @@
			}
		}
		catch(SQLException sqlexp )
		{
			//Logger.error(FILE_NAME,"Exception in  getOperationTerminalInfo() method of TerminalRegistrationSessionBean : "+sqlexp);
      logger.error(FILE_NAME+"Exception in  getOperationTerminalInfo() method of TerminalRegistrationSessionBean : "+sqlexp);
			return null;
		}
		catch(Exception exp )
		{
			//Logger.error(FILE_NAME,"Exception in  getOperationTerminalInfo() method of TerminalRegistrationSessionBean : "+exp);
      logger.error(FILE_NAME+"Exception in  getOperationTerminalInfo() method of TerminalRegistrationSessionBean : "+exp);
			return null;
		}
		finally
		{
			try
			{
				if( rs!=null )
        {
					rs.close();
        }
				if( stmt!=null )
        {
					stmt.close();
        }
				if( connection!=null )
        {
					connection.close();
        }
			}
			catch(Exception exp )
			{
				//Logger.error(FILE_NAME,"Exception in  getLocationInfo() method of TerminalRegistrationSessionBean : "+exp);
        logger.error(FILE_NAME+"Exception in  getLocationInfo() method of TerminalRegistrationSessionBean : "+exp);
	            throw new EJBException("Exception in  getLocationInfo() method of TerminalRegistrationSessionBean : "+exp.toString());
			}
		}
		return vecLocationInfo;
	}		//Added by Shravan for Actv Status

	/**
   * 
   * @param terminalId1
   * @param opTerminal
   * @return 
   */
   //loginbean is added by VLAKSHMI for  issue 173655 on 20090629
  public TerminalRegJspBean  getTerminalRegDetails( String terminalId1, String opTerminal ,ESupplyGlobalParameters loginbean) 
    {
	    	Statement stmt   = null;     // a Statement variable
        Statement stmtQuery   = null;     // a Statement variable
            Statement stmt1  = null;     // a Statement variable
	    	ResultSet rs     = null;     // a ResultSet variable
            ResultSet rs1    = null;     // a ResultSet variable
		    TerminalRegJspBean terminalReg = null;   // a TerminalRegJspBean variable
		    Address address  =  null;    // an Address class variable
			String abbrName	    = null;    // String to store abbreviated name
            String contactName  = null;    // String to store contact name
            String designation  = null;    // String to store designation
            String notes 		= null;    // String to store notes
            int addressId	    = 0;     // an integer to store address id
            String agentInd  	= null;    // String to store agent id
			String iatacode	    = null;    // String to store IATA code
			String accoutcode	= null;    // String to store account code
			String regno	    = null;    // String to store regno
    		String bankName	    = null;    // String to store bankName
			String branchName   = null;    // String to store branchName
			String bankCity     = null;    // String to store bank city
			String accountNo	= null;    // String to store Accout Number
			String bankAddress	= null;    // String to store bank Address
			double interestRate = 0.0;	   // int to store interestRate
    		int discrepancy  = 0;    // double to store discrepancy
			String invoiceCategory=null;   // String to store invoice Category
			String terminalType   = null;    // String to store Terminal Type
			String opTerminalId	  = null;
			String emailStatus	  = null;
			String timeZone		  = null;
			String shipmentCollect= null;
			String shipmentMode	  = null;
			Vector cbtTerminal    = new   Vector();
			int    weightScale	  = 0;
            //@@ Srivegi Added on 20050419 (Invoice-PR)
			String stockedInvoiceIdsCheck = null;
      String adminROTerminal  = null;
            Connection  connection  =   null;
			//@@ 20050419 (Invoice-PR) 
      
    // Added By RajKumari on 11/28/2008 for 146448 starts
     String 	frequency	 = null;
     String 	carrier	 = null;
     String 	transitTime	 = null;
    // String 	freightRate	 = null;
      String 	rateValdiity	 = null;
   // Added By RajKumari on 11/28/2008 for 146448 ends
     String marginType = null ; // //added by phani sekhar for wpbn 170758 on 20090626
   String discountType = null;
    		try
	        {
			    connection = operationsImpl.getConnection();
		    stmtQuery = connection.createStatement(); 
        //if condition is added by VLAKSHMI for  issue 173655 on 20090629
              if(loginbean!=null&&loginbean.getUserTerminalType().equals("A") && opTerminal.equals("O"))
        {
        
           String sqlQuery="select COUNT(*) NO_ROWS from fs_fr_terminal_regn "+ 
           "  where parent_terminal_id='"+loginbean.getTerminalId()+"'and CHILD_TERMINAL_ID= '"+terminalId1+"'";
       
        stmtQuery               = connection.createStatement();
        rs                =  stmtQuery.executeQuery(sqlQuery);
        rs.next();
        int cnt1 = rs.getInt("NO_ROWS");
				if( cnt1==0 )
        {
					return null;
        } 
        }
       // ENDfor  issue 173655
        stmt = connection.createStatement();
			    String sql  = "SELECT COUNT(*) NO_ROWS FROM FS_FR_TERMINALMASTER WHERE TERMINALID='"+ terminalId1 +"' AND OPER_ADMIN_FLAG='"+opTerminal+"'";
		        String sql0 = "SELECT COMPANYID, CONTACTNAME, DESIGNATION, NOTES, CONTACTADDRESSID, AGENTIND, IATACODE, ACCOUNTNO, TAXREGNO,"
				            + " BANKNAME,BRANCHNAME,BANKACCTNO,BANKADDRESS,BANKCITY,DISCREPANCY,INTERESTRATE,INVOICECATEGORY,TERMINALTYPE ,"
							+ " OPER_ADMIN_FLAG, EMAIL_ACTVE_FLAG ,TIME_ZONE ,CC_SHIPMNT_FLAG,SHIPMENTMODE,CONV_WGT_SCAL"
							//@@ Srivegi added on 20050419 (Invoice-PR)
							+" ,USESTOCKEDINVOICE"
							//@@ 20050419 (Invoice-PR)
              +" ,CHILDTERMINAL_FLAG "  //Added By I.V.Sekhar"
              + " ,FREQUENCY,CARRIER,TRANSITTIME,RATEVALIDITY ,MARGIN_TYPE,DISCOUNT_TYPE "// Added By RajKumari on 11/28/2008 for 146448 
							+"  FROM FS_FR_TERMINALMASTER WHERE TERMINALID='"+terminalId1+"' AND OPER_ADMIN_FLAG='"+opTerminal+"'";
				rs = stmt.executeQuery( sql );
				rs.next();
				int cnt = rs.getInt("NO_ROWS");
				if( cnt==0 )
        {
					return null;
        }
		        rs = stmt.executeQuery( sql0 );
		        while(rs.next())
		        {
                 abbrName	    	= rs.getString("COMPANYID");
                 contactName    	= rs.getString("CONTACTNAME");
                 designation    	= rs.getString("DESIGNATION");
                 notes 		    	= rs.getString("NOTES");
                 addressId	    	= rs.getInt("CONTACTADDRESSID");
                 agentInd  	    	= rs.getString("AGENTIND");
				 iatacode	    	= rs.getString("IATACODE");
				 accoutcode	    	= rs.getString("ACCOUNTNO");
				 regno	        	= rs.getString("TAXREGNO");
		         bankName       	= rs.getString("BANKNAME");
				 branchName     	= rs.getString("BRANCHNAME");
				 accountNo			= rs.getString("BANKACCTNO");
				 bankAddress    	= rs.getString("BANKADDRESS");
				 bankCity			= rs.getString("BANKCITY");
				 discrepancy		= rs.getInt("DISCREPANCY");
				 interestRate		= rs.getDouble("INTERESTRATE");
				 invoiceCategory	= rs.getString("INVOICECATEGORY");
				 terminalType   	= rs.getString("TERMINALTYPE");
				 opTerminalId	  	= rs.getString("OPER_ADMIN_FLAG");
				 emailStatus	  	= rs.getString("EMAIL_ACTVE_FLAG");
				 timeZone		  	= rs.getString("TIME_ZONE");
				 shipmentCollect	= rs.getString("CC_SHIPMNT_FLAG");
				 shipmentMode		= rs.getString("SHIPMENTMODE");
				 weightScale		= rs.getInt("CONV_WGT_SCAL");
				 //@@ Srivegi Added on 20050419 (Invoice-PR)
                 stockedInvoiceIdsCheck		= rs.getString("USESTOCKEDINVOICE");
				 //@@ 20050419 (Invoice-PR)
         adminROTerminal   = rs.getString("CHILDTERMINAL_FLAG");
          // Added By RajKumari on 11/28/2008 for 146448 starts
           frequency	 = rs.getString("FREQUENCY");;
           carrier	   = rs.getString("CARRIER");;
           transitTime	 = rs.getString("TRANSITTIME");;
           rateValdiity	 = rs.getString("RATEVALIDITY");;
         // Added By RajKumari on 11/28/2008 for 146448 ends
         marginType = rs.getString("MARGIN_TYPE");
         discountType = rs.getString("DISCOUNT_TYPE");
		        }

		        AddressDAO addressDAO= new AddressDAO();
				address	= addressDAO.load(addressId);


			 	
   			   	String sql3=null,sql4=null;
   			   	if(opTerminalId==null || opTerminalId.equals("O"))
   			   	{
   			   		sql3 = "SELECT COUNT(*) NO_ROWS FROM FS_FR_TERMINALLOCATION WHERE TERMINALID='"+terminalId1+"'";    // a String having a sql query
   			   	   	sql4 = "SELECT LOCATIONID,'N' FLAG  FROM FS_FR_TERMINALLOCATION WHERE TERMINALID='"+terminalId1+"'";
   			   	}
				else if(!opTerminalId.equals("O"))
   			   	{
   			   		sql3 = "SELECT COUNT(*) NO_ROWS FROM FS_FR_TERMINAL_REGN WHERE PARENT_TERMINAL_ID='"+terminalId1+"'";    // a String having a sql query
   			   	    sql4 = "SELECT CHILD_TERMINAL_ID LOCATIONID,CBT_FLAG  FLAG  FROM FS_FR_TERMINAL_REGN WHERE PARENT_TERMINAL_ID='"+terminalId1+"'";
   			   	}
			    stmt1 =  connection.createStatement();
			    rs1 = stmt1.executeQuery( sql3 );
			    rs1.next();
			    cnt = rs1.getInt("NO_ROWS");
			    String[]  locationId = null;    //  a String array to store location ids
			    if( cnt > 0 )
		    	{
				   locationId = new String[cnt];
				   rs1= stmt1.executeQuery( sql4 );
				   for(int j=0; rs1.next(); j++ )
			   	   {
				   	locationId[j] = rs1.getString("LOCATIONID");
					String flag = rs1.getString("FLAG");
                    if((flag!= null)&&(flag.equals("Y")))
                       cbtTerminal.add(locationId[j]);
			   	   }
		   	    }
				//@@ Srivegi Modified on 20050419 (Invoice-PR) - stockedInvoiceIdsCheck  parameter is added in the below method
                terminalReg = new TerminalRegJspBean( terminalId1, locationId, abbrName, contactName , designation ,
														notes, addressId, agentInd ,iatacode, accoutcode, regno,address,
														bankName,branchName,accountNo,bankAddress,bankCity,discrepancy,
														interestRate,invoiceCategory,terminalType,opTerminalId,emailStatus,
														timeZone,shipmentCollect,shipmentMode,weightScale,stockedInvoiceIdsCheck,frequency,carrier,transitTime,rateValdiity);
        terminalReg.setChildTerminalFlag(adminROTerminal);
				terminalReg.setCBTLocationId(cbtTerminal);
				//added by phani sekhar for wpbn 170758 on 20090626
        terminalReg.setMarginType(marginType);
        terminalReg.setDiscountType(discountType);
        //ends 170758
      	//Logger.info(FILE_NAME,"TerminalRegistration Account Number is :" + terminalReg.getAccountNumber());
                return terminalReg;
	 	   }
		   catch( SQLException e )
		   {
		   		//Logger.error(FILE_NAME, "Exception in  getTerminalRegDetails() method of TerminalRegistrationSessionBean : "+e.toString() );
          logger.error(FILE_NAME+ "Exception in  getTerminalRegDetails() method of TerminalRegistrationSessionBean : "+e.toString() );
				return null;
	       }
		   catch( Exception e )
	       {
		   		//Logger.error(FILE_NAME, "Exception in  getTerminalRegDetails() method of TerminalRegistrationSessionBean : "+e.toString() );
          logger.error(FILE_NAME+ "Exception in  getTerminalRegDetails() method of TerminalRegistrationSessionBean : "+e.toString() );
				return null;
	       }
		   finally
	       {
		 	   try
		       {
		       		if( rs!=null )
              {
	   				  rs.close();
              }
	   				if( rs1!=null )
            {
	   				  rs1.close();
            }
	   				if( stmt!=null )
            {
		   			  stmt.close();
            }
	   				if( stmt1!=null )
            {
		   			  stmt1.close();
            }
	   				if( connection!=null )
            {
	   				  connection.close();
            }
	   				if(stmtQuery !=null)//@@ added by govind on 15-02-2010 for connection leakage
	   				{
	   					stmtQuery.close();
	   				}
	   				
		       }
			   catch( Exception e )
			   {
					//Logger.error(FILE_NAME,"Exception in  getTerminalRegDetails() method of TerminalRegistrationSessionBean : "+e);
          logger.error(FILE_NAME+"Exception in  getTerminalRegDetails() method of TerminalRegistrationSessionBean : "+e);
		   			throw new EJBException( "Exception in  getTerminalRegDetails() method of TerminalRegistrationSessionBean : "+e.toString() );
		       }
		   }
     }// end of getTerminalmasterCountryMasterDetails


	 /**
   * 
   * @param terminalRegObj
   * @param address
   * @param oldlocationId
   * @param esupplyGlobalParameters
   * @return 
   */
  public boolean updateTerminalReg( TerminalRegJspBean terminalRegObj, Address address, String oldlocationId, com.foursoft.esupply.common.bean.ESupplyGlobalParameters esupplyGlobalParameters )
   {
		String 				terminalId  = null;    // String to store terminal id
		// @@ 'connection' is replaced with 'connection1' by Suneetha for TogehterJ on 12 Jan 05
		Connection 			connection1 	= null;    // Connection variable
		PreparedStatement 	pstmt 		= null;    // PreparedStatement variable
		Statement 			stmt 		= null;    // Statement variable
		ResultSet			rs			= null;    // ResultSet variable
		int					shipMode	= 0;
		try
		{
			terminalId = terminalRegObj.getTerminalId();
			connection1 = operationsImpl.getConnection();
			String sql1 = "SELECT COUNT(*) NO_ROWS FROM FS_FR_TERMINALMASTER WHERE TERMINALID='"+ terminalId +"'";
			String sql2 =	" UPDATE FS_FR_TERMINALMASTER SET CONTACTNAME=?, DESIGNATION=?, NOTES=?,IATACODE = ?,"+
							" ACCOUNTNO = ?,TAXREGNO = ?,BANKNAME = ?,BRANCHNAME=?,BANKACCTNO=?,BANKADDRESS=?,BANKCITY=?,"+
							" DISCREPANCY=?,INTERESTRATE=?,INVOICECATEGORY=?,TERMINALTYPE=?,EMAIL_ACTVE_FLAG=?,TIME_ZONE =?,"+
							" CC_SHIPMNT_FLAG =?,LAST_UPDTD_TIMESTMP=?,SHIPMENTMODE=?,CONV_WGT_SCAL = ?,SERVER_TIME_DIFF =?"+
							//@@ Srivegi added on 20050419 (Invoice-PR)
							", USESTOCKEDINVOICE = ?,CHILDTERMINAL_FLAG=?"+
							//@@ 20050419 (Invoice-PR)
              ", FREQUENCY =?, CARRIER =?, TRANSITTIME =?, RATEVALIDITY =?"+//Added By RajKumari on 11/28/2008 for 146448
			    " , MARGIN_TYPE=?,DISCOUNT_TYPE=? " + //modified by phani sekhar for wpbn 170758 on 20090626
							"  WHERE TERMINALID=?  ";		    
			String sql3 = "UPDATE FS_FR_GATEWAYMASTER SET COMPANYNAME=?, CONTACTNAME=?, TIME_ZONE=?, CC_SHIPMNT_FLAG=?,GATEWAY_TYPE=? WHERE GATEWAYID=? ";
          	stmt = connection1.createStatement();
			rs = stmt.executeQuery( sql1 );
			rs.next();
			int cnt = rs.getInt("NO_ROWS");
			// if no rows
			if( cnt == 0 )
      {
		     return false;
      }
			try
			{
				if(terminalRegObj.getShipmentMode() != null)
        {
					shipMode	=	Integer.parseInt(terminalRegObj.getShipmentMode().trim());
        }
			}
			catch(NumberFormatException nEx)
			{
				//Logger.error(FILE_NAME,"Exception in updateTerminalReg()...ETransHOAdminSetupSessionBean.."+nEx.toString());
        logger.error(FILE_NAME+"Exception in updateTerminalReg()...ETransHOAdminSetupSessionBean.."+nEx.toString());
			}

       		pstmt = connection1.prepareStatement( sql2 );
			pstmt.setString( 1, terminalRegObj.getContactName() );
			pstmt.setString( 2, terminalRegObj.getDesignation() );
			pstmt.setString( 3, terminalRegObj.getNotes() );
			pstmt.setString( 4, terminalRegObj.getIataCode());
			pstmt.setString( 5, terminalRegObj.getAccountCode() );
			pstmt.setString( 6, terminalRegObj.getTaxRegNo() );
			pstmt.setString( 7, terminalRegObj.getBankName() );
			pstmt.setString( 8, terminalRegObj.getBranchName() );
			pstmt.setString( 9, terminalRegObj.getAccountNumber() );
			pstmt.setString(10, terminalRegObj.getBankAddress() );
			pstmt.setString(11, terminalRegObj.getBankCity());
			pstmt.setInt(12,terminalRegObj.getDiscrepancy());
			pstmt.setDouble(13,terminalRegObj.getInterestRate());
			pstmt.setString(14,terminalRegObj.getInvoiceCategory());
			pstmt.setString(15,terminalRegObj.getTerminalType());
			pstmt.setString(16,terminalRegObj.getEmailStatus());
			pstmt.setString(17,terminalRegObj.getTimeZone());
			pstmt.setString(18,terminalRegObj.getCollectShipment());
			pstmt.setTimestamp(19,new Timestamp(System.currentTimeMillis() ));
			pstmt.setInt(20,shipMode);
			pstmt.setInt(21,terminalRegObj.getWeightScale());
    		pstmt.setString(22,terminalRegObj.getServerTimeDiff());
			//@@ Srivegi Added on 20050419 (Invoice-PR)
			pstmt.setString(23,terminalRegObj.getStockedInvoiceIdsCheck());
			//@@ 20050419 (Invoice-PR) 
      pstmt.setString(24,terminalRegObj.getChildTerminalFlag());//Added By I.V.Sekhar   
      //Added By RajKumari on 11/28/2008 for 146448 starts
      pstmt.setString(25,terminalRegObj.getFrequency());
      pstmt.setString(26,terminalRegObj.getCarrier());
      pstmt.setString(27,terminalRegObj.getTransitTime());
      pstmt.setString(28,terminalRegObj.getRateValidity());
      //Added By RajKumari on 11/28/2008 for 146448 ends
		 //MODIFIED BY PHANI SEKHAR FOR WPBN 170758 on 20090626 
     pstmt.setString(29, terminalRegObj.getMarginType());
     pstmt.setString(30,terminalRegObj.getDiscountType());
    pstmt.setString(31,terminalId);  
      //ends 170758
			pstmt.executeUpdate();
      //Added By RajKumari on 30-10-2008 for Connection Leakages.
      if(pstmt!=null)
      {
        pstmt.close();
        pstmt= null;
      }
			pstmt = connection1.prepareStatement( sql3 );
			pstmt.setString( 1, terminalRegObj.getNotes() );
			pstmt.setString( 2, terminalRegObj.getContactName() );
			pstmt.setString( 3, terminalRegObj.getTimeZone() );
			pstmt.setString( 4, terminalRegObj.getCollectShipment() );
			pstmt.setString( 5, terminalRegObj.getTerminalType());
			pstmt.setString( 6, terminalId );
			pstmt.executeUpdate();

			InitialContext initialContext 		= new InitialContext();
		//	ETAUtilitiesSessionHome utilHome = (ETAUtilitiesSessionHome) initialContext.lookup("ETAUtilitiesSessionBean");
		//	ETAUtilitiesSession utilRemote 	= (ETAUtilitiesSession) utilHome.create();
            
            address.setAddressId(terminalRegObj.getContactAddressId());
			AddressDAO addressDAO= new AddressDAO();
			addressDAO.updateAddress(address);
            
		//	utilRemote.updateAddressDetails(address,terminalRegObj.getContactAddressId());
			//Logger.info(FILE_NAME,"terminalRegObj.getOperationTerminalType() in UPDATE..."+terminalRegObj.getOperationTerminalType());

			if(terminalRegObj.getOperationTerminalType()==null || terminalRegObj.getOperationTerminalType().equals("O"))
      {
				updateLocationId(terminalRegObj.getTerminalId(),terminalRegObj.getLocationId(),connection1);
      }
			else
      {
				updateOperationTerminalId(terminalRegObj.getTerminalId(),terminalRegObj.getLocationId(),terminalRegObj.getShipmentMode(),"",terminalRegObj.getCBTLocationId(),esupplyGlobalParameters,connection1);
				operationsImpl.setTransactionDetails( 	esupplyGlobalParameters.getTerminalId(),
														esupplyGlobalParameters.getUserId(),
														"TerminalReg",
														terminalId,
														esupplyGlobalParameters.getLocalTime(),
														"MODIFY" );
      }
		    return true;
		}
		catch( Exception rex )
		{
			//Logger.error(FILE_NAME,"Exception in  updateTerminalReg() method of TerminalRegistrationSessionBean : "+rex);
      logger.error(FILE_NAME+"Exception in  updateTerminalReg() method of TerminalRegistrationSessionBean : "+rex);
   		    throw new EJBException( "Exception in  updateTerminalReg() method of TerminalRegistrationSessionBean : "+rex.toString() );
		}
		finally
		{
			try
			{
				if( pstmt!=null )
        {
					pstmt.close();
        }
				if( rs!=null )
        {
					rs.close();
        }
				if( stmt!=null )
        {
					stmt.close();
        }
				if( connection1!=null )
        {
					connection1.close();
        }
			}
			catch( Exception e )
			{
				//Logger.error(FILE_NAME,"Exception in  updateTerminalReg() method of TerminalRegistrationSessionBean : "+e);
        logger.error(FILE_NAME+"Exception in  updateTerminalReg() method of TerminalRegistrationSessionBean : "+e);
   			    throw new EJBException( "Exception in  updateTerminalReg() method of TerminalRegistrationSessionBean : "+e.toString() );
			}
		}
	}

	/**
   * 
   * @param terminalId
   * @param addressId
   * @param oldlocationId
   * @param terminalType
   * @param esupplyGlobalParameters
   * @return 
   */
   public boolean deleteTerminalReg( String terminalId, int addressId, String oldlocationId,String terminalType, com.foursoft.esupply.common.bean.ESupplyGlobalParameters esupplyGlobalParameters ) 
   {
  		Context initial                     = null;     // an InitialContext variable
        TerminalRegistrationEntityHome trsh = null;     // a TerminalRegistrationEntityHome variable
        TerminalRegistrationEntity trr      = null;     // a TerminalRegistrationEntity
		//CompanyInfoSession				compInfoRemote	=	null;
		//CompanyInfoSessionHome			compInfoHome	=	null;
 	    try
	    {
			initial  = new InitialContext();
			trsh     =  (TerminalRegistrationEntityHome )initial.lookup("TerminalRegistrationEntityBean");
            trr      =  trsh.findByPrimaryKey(terminalId);
			//compInfoHome 	= 	(CompanyInfoSessionHome)initial.lookup("CompanyInfoSessionBean");
			//compInfoRemote 	=	(CompanyInfoSession)compInfoHome.create();
            if( trr!=null )
            {
			//	Logger.info(FILE_NAME,"Entered...deleteTerminalReg : "+terminalType);
				//Logger.info(FILE_NAME,"Entered...deleteTerminalReg : "+trr);
				//@@ Avinash replaced on 20041208 (removing methods with SQL from CMP)
				//if( trr.deleteLocationId(terminalId,terminalType) && trr.deleteGatewayId(terminalId) && compInfoRemote.deleteAccountsDetails(terminalId))
				if( this.deleteLocationId(terminalId,terminalType) && this.deleteGatewayId(terminalId) )
				//@@ 20041208 (removing methods with SQL from CMP)
				{
				   	trr.remove();
				   // ETAUtilitiesSessionHome utilHome = (ETAUtilitiesSessionHome)initial.lookup("ETAUtilitiesSessionBean");
					//ETAUtilitiesSession	utilRemote	= (ETAUtilitiesSession) utilHome.create();
					//utilRemote.deleteAddressDetails(addressId);
 				   	operationsImpl.setTransactionDetails( esupplyGlobalParameters.getTerminalId(),
													  esupplyGlobalParameters.getUserId(),
													  "TerminalReg",
													   terminalId,
													   esupplyGlobalParameters.getLocalTime(),
													  "DELETE" );
				   return true;
			    }
			    else
			    {
			      throw new EJBException( "Exception in  deleteTerminalReg() method of TerminalRegistrationSessionBean :" );
			    }
			}
			else
			{
			 return false;
			}
	    }
        catch( NamingException nme )
		{
			//Logger.error(FILE_NAME,"Exception in  deleteTerminalReg() method of TerminalRegistrationSessionBean : "+nme);
      logger.error(FILE_NAME+"Exception in  deleteTerminalReg() method of TerminalRegistrationSessionBean : "+nme);
   		    throw new EJBException( "Exception in  deleteTerminalReg() method of TerminalRegistrationSessionBean : "+nme.toString() );
		}
		
		catch( Exception rex )
		{
			//Logger.error(FILE_NAME,"Exception in  deleteTerminalReg() method of TerminalRegistrationSessionBean : "+rex);
      logger.error(FILE_NAME+"Exception in  deleteTerminalReg() method of TerminalRegistrationSessionBean : "+rex);
   		    throw new EJBException( "Exception in  deleteTerminalReg() method of TerminalRegistrationSessionBean : "+rex.toString() );
		}
		finally
		{
			initial = null;
			trsh	= null;
			trr		= null;
		}
 	}

	/**
   * 
   * @param gatewayId
   * @return 
   */
		public boolean checkGatewayId(String gatewayId)
		{
			Connection con = null;			// Connection Object
			PreparedStatement pstmt	= null;	//PreparedStatement	Object
			ResultSet rs   = null;			//ResultSet	Object
			boolean	b=false;
			try
			{
				con=getConnection();
				pstmt=con.prepareStatement("SELECT * FROM FS_FR_GATEWAYMASTER WHERE	GATEWAYID=?");
				pstmt.setString(1,gatewayId);
				rs=pstmt.executeQuery();
				if(rs.next())
				{
					b=true;
				}
			}
			catch(Exception	e)
			{
				
				//Logger.error(FILE_NAME,"checkGatewayId()", e);
        logger.error(FILE_NAME+"checkGatewayId()"+ e);
				throw new EJBException(e.toString());
			}
			finally
			{ // finally
				ConnectionUtil.closeConnection(con,pstmt,rs);
			} // finally
			return b;
		} //method	end


		public int selectId() 
		 {
			OIDSessionHome oidHome 			= null;
			OIDSession	   oid	   			= null;
			InitialContext initialContext	= null;
			try
			{
				initialContext =  new InitialContext();
				oidHome	= (OIDSessionHome)initialContext.lookup("OIDSessionBean");
				oid		= (OIDSession)oidHome.create();
				int	id	= oid.getAddressOID();
				return id;
			}
			catch( Exception e )
			{
				//Logger.error(FILE_NAME,"selectId()", e);
        logger.error(FILE_NAME+"selectId()"+ e);
				throw new EJBException("Remote Exception	in selectId() method of ETransGatewaySetupSessionBean : "+e.toString()	);
			}
			finally
			{
				initialContext = null;
				oidHome	=	null;
				oid		=	null;
			}
		}
		

		/**
   * 
   * @param GatewayId
   * @param GatewayType
   * @param GatewayName
   * @param CompanyName
   * @param ContactName
   * @param contactAddressId
   * @param Notes
   * @param Indicator
   * @param gatewayJSPBean
   * @param address
   * @param terminals
   * @param esupplyGlobalParameters
   * @param addressId
   */
		public void	insGatewayDB(	String					GatewayId,
									String					GatewayType,
									String					GatewayName,
									String					CompanyName,
									String					ContactName,
									int						contactAddressId,
									String					Notes,
									String					Indicator,
									GatewayJSPBean			gatewayJSPBean,
									Address					address,
									String[]				terminals,
									ESupplyGlobalParameters esupplyGlobalParameters,
									int						addressId)
		{
			Connection 		  con	 	=	getConnection();
			PreparedStatement psmt 		=	null;
        	Statement  		  stmt 	  	=	null;    // variable for Statement
        	ResultSet  		  rs		=	null;    // variable for ResultSet
				
			InitialContext context =null;
		//	CompanyInfoSession				compInfoRemote	=	null;
		//	CompanyInfoSessionHome			compInfoHome	=	null;
			
			try
			{
				int type	=	Integer.parseInt(GatewayType.trim());
				context =new InitialContext();
				
				AddressDAO addressDAO= new AddressDAO();
				int addressID					= addressDAO.create(address);
				address.setAddressId(addressID);
				
		    	String sqlQuery = "INSERT INTO FS_FR_GATEWAYMASTER(GATEWAYID,GATEWAYTYPE,GATEWAYNAME,"+
				 		  		  "COMPANYNAME,CONTACTNAME,CONTACTADDRESSID,NOTES,INDICATOR,TIME_ZONE,GATEWAY_TYPE,SERVER_TIME_DIFF)"+
						  		  "VALUES(?,?,?,?,?,?,?,?,?,?,?)";
						  
				psmt = con.prepareStatement(sqlQuery);
				psmt.setString(1,GatewayId);
				psmt.setInt(2,type);
				psmt.setString(3,GatewayName);
				psmt.setString(4,CompanyName);
				psmt.setString(5,ContactName);
				psmt.setInt(6,addressID);
				psmt.setString(7,Notes==null?"":Notes );
				psmt.setString(8,Indicator==null?"":Indicator);
				psmt.setString(9,gatewayJSPBean.getTimeZone());
				psmt.setString(10,"S");
				psmt.setString(11,gatewayJSPBean.getServerTimeDiff());
				psmt.executeUpdate();

				// Added By Raghu.G - Ends Here
        //Added By RajKumari on 30-10-2008 for Connection Leakages.
        if(psmt!=null)
        {
          psmt.close();
          psmt = null;
        }
				psmt =	con.prepareStatement("INSERT INTO FS_FR_TERMINALGATEWAY (GATEWAYID,TERMINALID) VALUES(?,?)");
				int termLen	=	terminals.length;
				for(int	i=0;i<termLen;i++)
				{
					psmt.setString(1,GatewayId);
					psmt.setString(2,terminals[i]);
					psmt.executeUpdate();
				}
				//Logger.info(FILE_NAME,"1");
				long bookId	= esupplyGlobalParameters.getAccountsCredentials().getBookId();	// for accounting entries.
				//Logger.info(FILE_NAME,"bookId in GateWayMasterSessionBean : "+bookId);
				String oldGatewayId	= esupplyGlobalParameters.getTerminalId(); // for accounting	entries.
				
				//Posting Accounting default entries like Vouchers and ledgers
				//as and when create the Terminal from ETrans
				//Added by Ram Kishore				
				
			    String	currencyId		 =	null;
				String	moduleId		 =  "ETAG";	
			    String	dateFormat		 =	"dd/mm/yy" ;	//For time being dd/mm/yy is treated as default Date Format
				
				String	countryId		 =  address.getCountryId();
			    String	sqlCurrency	 	 = 	"SELECT  CURRENCYID FROM FS_COUNTRYMASTER WHERE COUNTRYID='"+ countryId +"'";
			
				stmt	=	con.createStatement();
			    rs   	=	stmt.executeQuery( sqlCurrency );
			    if(rs.next())       	
			   		{currencyId	=	rs.getString(1);}

			   // compInfoHome 	= 	(CompanyInfoSessionHome)context.lookup("CompanyInfoSessionBean");		  
			  //  compInfoRemote 	=	(CompanyInfoSession)compInfoHome.create();
				//Logger.info(FILE_NAME,"before insertAcctCompInfo in GateWayMasterSessionBean : "+bookId);
			   // compInfoRemote.insertAcctCompInfo( CompanyName, GatewayId, addressID,currencyId,dateFormat,moduleId);
				//Logger.info(FILE_NAME,"after insertAcctCompInfo in GateWayMasterSessionBean : "+bookId);
				//Accounting entries end here
				
				java.util.Date date	= new java.util.Date();	 //	creation of	current	system Date
				java.sql.Timestamp loginDate = new java.sql.Timestamp(date.getTime());
				operationsImpl.setTransactionDetails(esupplyGlobalParameters.getTerminalId(),esupplyGlobalParameters.getUserId(),"GatewayMaster",GatewayId,esupplyGlobalParameters.getLocalTime(),"ADD");
			}
			catch(Exception	e)
			{
				//Logger.error(FILE_NAME,"insGatewayDB()", e);
        logger.error(FILE_NAME+"insGatewayDB()"+ e);
				throw new javax.ejb.EJBException("Exception in insGatewayDB()	method of ETransGatewaySetupSessionBean	: "+e.toString());
			}
			finally
			{//	finally
				ConnectionUtil.closeConnection(con,psmt,rs);//Modified By RajKumari on 30-10-2008 for Connection Leakages.
			  ConnectionUtil.closeStatement(stmt);//Added By RajKumari on 30-10-2008 for Connection Leakages.
      }//	finally
		}



		public String[]	loadGatewayId()
		{
			Connection con = null;			// Connection Object
			PreparedStatement pstmt	= null;	// PreparedStatement Object
			ResultSet rs1  = null;			// ResultSet Objects
			ResultSet rs   = null;
			String gateway[]		= null;	// for storing all the Gateway Ids
			String gatewayFilter	= null;	// for Filtering the Gateway.Terminal is a Gateway if its Length is	$.
			try
			{ // try block
				int	count =	0; // temporary	variables
				int	num=0;
				int	len	= 0;
				con=getConnection();
				pstmt =	con.prepareStatement("SELECT COUNT(GATEWAYID) FROM FS_FR_GATEWAYMASTER WHERE GATEWAYID NOT IN (SELECT TERMINALID FROM FS_FR_TERMINALMASTER)");
				rs1=pstmt.executeQuery();
				if(rs1.next())
				{
					count =	rs1.getInt(1);
					gateway	= new String[count];
				}
				pstmt =	con.prepareStatement("SELECT GATEWAYID FROM	FS_FR_GATEWAYMASTER	WHERE GATEWAYID	NOT	IN (SELECT TERMINALID FROM FS_FR_TERMINALMASTER) ORDER BY GATEWAYID");
				rs=pstmt.executeQuery();
				while(rs.next())
				{ // while
					gatewayFilter=rs.getString("GATEWAYID");
					len	= gatewayFilter.length();
					if(len <= 4)
					{
						gateway[num] = gatewayFilter;
						num++;
					}
				}//	while
			} // try block
			catch(Exception	e)
			{
				//Logger.error(FILE_NAME,"loadGatewayId()", e);
        logger.error(FILE_NAME+"loadGatewayId()"+ e);
				throw new EJBException(e.toString());

			}
			finally
			{ // finally
				ConnectionUtil.closeConnection(con,pstmt,rs);
				ConnectionUtil.closeConnection(con,pstmt,rs1);
			} // finally
			return gateway;
		} // method	end
		


		/**
   * 
   * @param gatewayId
   * @return 
   */
		public GatewayJSPBean viewGatewayDB(String gatewayId) 
		{
			Connection con			= null;		  // Connection	Object for connecting to the Database
			PreparedStatement pstmt	= null;		  // PreparedStatement Object
			ResultSet rs			= null;		  // ResultSet Object
			GatewayJSPBean gatewayJSPBean =	null; // GatewayJSPBean	Object
			Address	address	 =	null;			  // Address Object	for	storing	the	Address	Details
			String timeZone	 = null;
			String			gatewayType	   		= 	null;	  // for sotring all the GatewayType values
			String			gatewayName	   		= 	null;	  // for storing all the GatewayName values
			String			contactName	   		= 	null;	  // for storing the ContactName
			String			companyName	   		= 	null;	  // for storing the Company Name
			int 			addressId	   		= 	0;		  // for storing the Address Id	Value
			String			notes		   		= 	null;
			try
			{
				con	= getConnection();
				pstmt =	con.prepareStatement("SELECT COUNT(*) NO_ROWS FROM FS_FR_GATEWAYMASTER WHERE GATEWAYID=?");
				pstmt.setString(1,gatewayId);
				rs = pstmt.executeQuery();
				rs.next();
				int	count =	rs.getInt("NO_ROWS");
				if(count ==	0)
				{return null;}
        //Added By RajKumari on 30-10-2008 for Connection Leakages.
        if(pstmt!=null)
        {
          pstmt.close();
          pstmt=null;
        }
				pstmt =	con.prepareStatement("SELECT * FROM	FS_FR_GATEWAYMASTER	WHERE GATEWAYID=?");
				pstmt.setString(1,gatewayId);
				rs = pstmt.executeQuery();
				
		
				if(rs.next())
				{//	if begin
					gatewayName	= rs.getString("GATEWAYNAME");
					contactName	= rs.getString("CONTACTNAME");
					companyName	= rs.getString("COMPANYNAME");
					addressId	= rs.getInt("CONTACTADDRESSID");
					notes		= rs.getString("NOTES");
					gatewayType	= rs.getString("GATEWAYTYPE");
					timeZone	= rs.getString("TIME_ZONE");
				} // if	end
				
				gatewayJSPBean = new com.foursoft.etrans.setup.gateway.bean.GatewayJSPBean();
				gatewayJSPBean.setGatewayName(gatewayName);
				gatewayJSPBean.setContactName(contactName);
				gatewayJSPBean.setCompanyName(companyName);
				gatewayJSPBean.setNotes(notes);
				gatewayJSPBean.setTimeZone(timeZone);
				gatewayJSPBean.setTypeofGateway(gatewayType);
				
				AddressDAO addressDAO=new AddressDAO();
				address	= addressDAO.load(addressId);
				gatewayJSPBean.setAddress(address);
				String[]		terminals	   		= (String[])getTerminalIds(gatewayId).toArray();
				gatewayJSPBean.setTerminals(terminals);
			}
			catch(Exception	e)
			{
				gatewayJSPBean=null;
				//Logger.error(FILE_NAME,"viewGatewayDB()", e);
        logger.error(FILE_NAME+"viewGatewayDB()"+ e);
				throw new EJBException(e.toString());
			}
			finally
			{//	finally
				ConnectionUtil.closeConnection(con,pstmt,rs);
			}//	finally
			return gatewayJSPBean;
		}



			/**
   *
   *@@@ This	method is used for retrieving all the Terminals	atttached to a particular GatewayId
   *@@@ return String[]
   *@@@ Author Anand.A
   *@@@ Date	Septeber 12,2001
   *@param gatewayId
   *@return 
   */
		public String[]	 getTerminalIds(String gatewayId,String str)
		{
			Connection con = null;			// Connection Object
			PreparedStatement pstmt	= null;	// PreparedStatement Object
			ResultSet rs = null;			// ResultSet Objects
			ResultSet rs1 =	null;
			String terminalIds[] = null;	// for storing all the TerminalIds Array
			int	i =0;						// Temporary Variables
			try
			{ // try block
				con=getConnection();
				pstmt=con.prepareStatement("SELECT COUNT(TERMINALID) FROM FS_FR_TERMINALGATEWAY	WHERE GATEWAYID=?");
				pstmt.setString(1,gatewayId);
				rs1	= pstmt.executeQuery();
				if(rs1.next())
				{
					terminalIds	= new String[rs1.getInt(1)];
				}
				pstmt=con.prepareStatement("SELECT TERMINALID FROM FS_FR_TERMINALGATEWAY WHERE GATEWAYID=? ORDER BY	TERMINALID");
				pstmt.setString(1,gatewayId);
				rs = pstmt.executeQuery();
				while(rs.next())
				{
					terminalIds[i] = rs.getString("TERMINALID");
					i++;
				}
			} // try block
			catch(Exception	e)
			{
				
				//Logger.error(FILE_NAME,"getTerminalIds()", e);
        logger.error(FILE_NAME+"getTerminalIds()"+ e);
				throw new EJBException(e.toString());
			}
			finally
			{ // finally
				ConnectionUtil.closeConnection(con,pstmt,rs);
				ConnectionUtil.closeConnection(con,pstmt,rs1);
			} // finally
			return terminalIds;
		}//	method end
		

//		public String[]	loadLocationName()
	//	{
	//		return locationName;
	//	}

		/**
   * 
   * @param gatewayId
   * @param esupplyGlobalParameters
   * @return 
   */
		public boolean removeGateway(String gatewayId,com.foursoft.esupply.common.bean.ESupplyGlobalParameters esupplyGlobalParameters)
		{
			Connection 			con			= null;	  // Connection	Object
			PreparedStatement 	pStmt		= null;	  // PrepearedStatement	Object
			Statement 			stmt 		= null;
			ResultSet 			rs 			= null;
			int 				addressId 	= 0;
			InitialContext 		context 	= null;
		//	CompanyInfoSession				compInfoRemote	=	null;
		//	CompanyInfoSessionHome			compInfoHome	=	null;			    
			try
			{	// try
			    con		 = getConnection();
				context  = new InitialContext();
				//first deleting from child table
				//ie FS_FR_TERMINALGATEWAY
				pStmt =	con.prepareStatement("DELETE FROM FS_FR_TERMINALGATEWAY	 WHERE GATEWAYID=?");
				pStmt.setString(1,gatewayId);
				int	row=pStmt.executeUpdate();
				//before deleting the Master table get the AddressInfo
				String sqlAddrQuery 	=	"SELECT CONTACTADDRESSID FROM FS_FR_GATEWAYMASTER WHERE GATEWAYID='"+gatewayId+"'";
				stmt 	= con.createStatement();
				rs = stmt.executeQuery(sqlAddrQuery);
				if(rs.next())
					{addressId = rs.getInt("CONTACTADDRESSID");}
				//deleting from Master table ie FS_FR_GATEWAYMASTER
        //Added By RajKumari on 30-10-2008 for Connection Leakages.
        if(pStmt!=null)
        {
          pStmt.close();
          pStmt = null;
        }
				pStmt =	con.prepareStatement("DELETE FROM FS_FR_GATEWAYMASTER	WHERE GATEWAYID=?");
				pStmt.setString(1,gatewayId);
				pStmt.executeUpdate();
			   // compInfoHome 	= 	(CompanyInfoSessionHome)context.lookup("CompanyInfoSessionBean");		  
			   // compInfoRemote 	=	(CompanyInfoSession)compInfoHome.create();
				//compInfoRemote.deleteAccountsDetails(gatewayId);
				AddressDAO addressDAO=new AddressDAO();
				addressDAO.removeAddress(addressId);
				operationsImpl.setTransactionDetails(esupplyGlobalParameters.getTerminalId(),esupplyGlobalParameters.getUserId(),"GatewayMaster",gatewayId,esupplyGlobalParameters.getLocalTime(),"DELETE");
			} // try
			catch(Exception	e)
			{
				
				//Logger.error(FILE_NAME," Exception in  deleteGatewayReg() method	of ETransGatewaySetupSessionBean :",  e);
        logger.error(FILE_NAME+" Exception in  deleteGatewayReg() method	of ETransGatewaySetupSessionBean :"+  e);
				return false;
			}
			finally
			{ // finally
				ConnectionUtil.closeConnection(con,pStmt,rs);
                ConnectionUtil.closeConnection(con,stmt,rs);
			} // finally
			return true;
		
		} //	method end


		 /**
   * 
   * @param gatewayId
   * @param gatewayType
   * @param gatewayName
   * @param companyName
   * @param contactName
   * @param notes
   * @param gatewayJSPBean
   * @param address
   * @param terminals
   * @param esupplyGlobalParameters
   * @return 
   */
		public boolean updateGatewayDB(String gatewayId,String gatewayType,String gatewayName,
										String companyName,String contactName,String notes,
										com.foursoft.etrans.setup.gateway.bean.GatewayJSPBean	gatewayJSPBean,
										com.foursoft.etrans.common.bean.Address address,String[] terminals,
										com.foursoft.esupply.common.bean.ESupplyGlobalParameters esupplyGlobalParameters)
		{
			boolean	b					= true;	 //	boolean	for	checking whether the record	is Updated or Not
			Connection con				= null;	 //	Connection Object for connecting to	the	Database
			PreparedStatement pstmt		= null;	 //	PreparedStatement Object
			PreparedStatement pstmt1	= null;	 //	PreparedStatment Object
			ResultSet rs				= null;	 //	ResultSet Object
			int	row						= 0;	 //	to count the No	Of Rows
			try
			{
				int type	=	Integer.parseInt(gatewayType.trim());

				con	= getConnection();
				pstmt =	con.prepareStatement("UPDATE FS_FR_GATEWAYMASTER SET GATEWAYTYPE=?,GATEWAYNAME=?,COMPANYNAME=?,CONTACTNAME=?,NOTES=?,TIME_ZONE=?,SERVER_TIME_DIFF =?  WHERE GATEWAYID=?");
				pstmt.setInt(1,type);
				pstmt.setString(2,gatewayName);
				pstmt.setString(3,companyName);
				pstmt.setString(4,contactName);
				pstmt.setString(5,notes);
				pstmt.setString(6,gatewayJSPBean.getTimeZone());
				pstmt.setString(7,gatewayJSPBean.getServerTimeDiff());
				pstmt.setString(8,gatewayId);

				row	= pstmt.executeUpdate();
				pstmt1 = con.prepareStatement("SELECT CONTACTADDRESSID FROM	FS_FR_GATEWAYMASTER	WHERE GATEWAYID=?");
				pstmt1.setString(1,gatewayId);
				rs = pstmt1.executeQuery();
				int addressId =0;
				if(rs.next())
				{
					addressId =	rs.getInt("CONTACTADDRESSID");
				}

				AddressDAO addressDAO= new AddressDAO();
				address.setAddressId(addressId);
				addressDAO.updateAddress(address);
				con	= getConnection();
        //Added By RajKumari on 30-10-2008 for Connection Leakages.
        if(pstmt1!=null)
        {
          pstmt1.close();
          pstmt1 = null;
        }
				pstmt1 = con.prepareStatement("DELETE FROM FS_FR_TERMINALGATEWAY  WHERE	GATEWAYID=?");
				pstmt1.setString(1,gatewayId);
				pstmt1.executeUpdate();
        //Added By RajKumari on 30-10-2008 for Connection Leakages.
        if(pstmt!=null)
        {
          pstmt.close();
          pstmt = null;
        }
				pstmt =	con.prepareStatement("INSERT INTO FS_FR_TERMINALGATEWAY	(TERMINALID,GATEWAYID) VALUES(?,?)");
				int termLen	=	terminals.length;
				for(int	i=0;i<termLen;i++)
				{
					pstmt.setString(1,terminals[i]);
					pstmt.setString(2,gatewayId);
					row=pstmt.executeUpdate();
					if(row==0)
						{b=false;}
				}
				java.util.Date date	= new java.util.Date();
				java.sql.Timestamp loginDate = new java.sql.Timestamp(date.getTime());
				operationsImpl.setTransactionDetails(esupplyGlobalParameters.getTerminalId(),esupplyGlobalParameters.getUserId(),"GatewayMaster",gatewayId,esupplyGlobalParameters.getLocalTime(),"MODIFY");
				return true;
			}
			catch(Exception	e)
			{
				
				//Logger.error(FILE_NAME,"updateGatewayDB()", e);
        logger.error(FILE_NAME+"updateGatewayDB()"+ e);
				throw new javax.ejb.EJBException("Exception in updateGatewayDB() method of ETransGatewaySetupSessionBean : "+e.toString());
			}
			finally
			{//	finally
				ConnectionUtil.closeConnection(con,pstmt,rs);
                ConnectionUtil.closeConnection(con,pstmt1,rs);
			}//	finally
		}// method end


		/**
   * This method is used for removing querys from JSP'S 
   * @param terminalType
   * @param searchString
   * @param shipmentMode
   * @param type
   * @return 
   */
	public ArrayList getGatewayIds(String terminalType,String searchString,String shipmentMode,String type)
    {
        Connection	connection	= null;
        Statement	stmt		= null;
        ResultSet	rs          = null;

        ArrayList   gateWayIds  = new ArrayList();

		String sql = "";
        try
        {
            connection = this.getConnection();
            stmt = connection.createStatement();
			if(type.equals("one"))
			{
				if(terminalType.equalsIgnoreCase("NSIB"))
				{sql = " SELECT GATEWAYID FROM FS_FR_GATEWAYMASTER WHERE GATEWAY_TYPE = 'N' AND GATEWAYID LIKE '"+searchString+"%' ";}
				else
				{sql = " SELECT GATEWAYID FROM FS_FR_GATEWAYMASTER WHERE GATEWAYID LIKE '"+searchString+"%' ORDER BY GATEWAYID ";}
			}
			else if(type.equals("two"))
			{
					sql = " SELECT GATEWAYID FROM FS_FR_GATEWAYMASTER WHERE LENGTH(GATEWAYID)=4 AND GATEWAYID LIKE '"+searchString+"%' ORDER BY GATEWAYID ";
			}
            rs = stmt.executeQuery(sql);
            while( rs.next() )
			{gateWayIds.add(rs.getString(1));}
        }
        catch(SQLException sqEx)
        {
			
			//Logger.error(FILE_NAME, "[getGatewayIds(whereClause)] -> "+sqEx.toString());
      logger.error(FILE_NAME+ "[getGatewayIds(whereClause)] -> "+sqEx.toString());
            throw new EJBException(sqEx.toString());
        }
        finally
        {
			ConnectionUtil.closeConnection(connection, stmt, rs);
        }
        return gateWayIds;
    }	


	/**
   * This method is used to retrive the GatewayDetails's Data from the database for a
   * particular field.This method takes values,termianlId and count as agrguments,values represents the records
   * and count is represents the records size,terminalId represent the area
   * It returns a records this is String double array that contains CustomerId,ContactName,Designation,
   * TerminalId,city
   *
   * @see                  SelectCustomesDetailes.jsp
   * @see                  CustomesMaster.jsp
   * @see                  ESupplyGlobalParameters.java
   * @Params               String values, int count,String terminalId
   * @return               records
   * @exception            javax.ejb.EJBException
   * @author               NAGESWARARAO.D
   */
public String[][] getCustomsMaster(String values,String terminalId,int count)
{
	Connection connection  = null;
	ResultSet  rs          = null;
	ResultSet  rs1         = null;
	Statement  stmt        = null;
	String records[][]     = null;
	int noOfrows           = 0;
	String sql             = null;
	String sql1            = null;

	try
	{
		
		connection     =getConnection();
		stmt           =connection.createStatement();
		values         =values.substring(0,values.length()-1);
		sql            ="SELECT "+values+" FROM FS_FR_CUSTOMSMASTER A ,FS_ADDRESS B"+
						" WHERE A.CONTACTADDRESSID = B.ADDRESSID ORDER BY CUSTOMSID";

		sql1           ="SELECT COUNT(*) NO_ROWS FROM FS_FR_CUSTOMSMASTER A ,FS_ADDRESS B"+
						" WHERE A.CONTACTADDRESSID = B.ADDRESSID ORDER BY CUSTOMSID";
		rs           =stmt.executeQuery(sql1);
		rs.next();
		noOfrows     = rs.getInt("NO_ROWS");
		records      = new String[noOfrows][count];
		rs1    =stmt.executeQuery(sql);
		if(rs1 != null)
		{
		for(int i=0;rs1.next();i++)
		{
			for(int j=0;j<count;j++)
			{
				records[i][j] = rs1.getString(j+1);
			}
		}
		}
	}
		catch(SQLException sqEx)
        {
			
			//Logger.error(FILE_NAME, "[getCustomsMaster(String values,String terminalId,int count)] -> "+sqEx.toString());
      logger.error(FILE_NAME+ "[getCustomsMaster(String values,String terminalId,int count)] -> "+sqEx.toString());
            throw new EJBException(sqEx.toString());
        }
        finally
        {
          ConnectionUtil.closeConnection(connection, stmt, rs);
          ConnectionUtil.closeConnection(null, null, rs1);
        }
	return records;
}//end the  getCustomsMaster method


/**
* This method is used to retrive the CarrierContracts's Data from the database for a
* particular field.This method takes values,terminalId and count as agrguments,values represents the records
* and count is represents the records size,terminalId is represent the Id of the Place
* It returns a records this is String double array that contains CarrierId,CarrierName,CarrierContractId
*
* @see                  SelectCarrier.jsp
* @see                  CarrierContracts.jsp
* @see                  ESupplyGlobalParameters.java
* @Params               String values, int count,String terminalId
* @return               records
* @exception            javax.ejb.EJBException
* @author               NAGESWARARAO.D
*/
public String[][] getGWCarrierContracts(String values,String terminalId,int count)
{
	Connection connection  = null;
	ResultSet  rs          = null;
	ResultSet  rs1         = null;
	Statement  stmt        = null;
	String records[][]     = null;
	int noOfrows           = 0;
	String sql             = null;
	String sql1            = null;


	try
	{
		connection     =this.getConnection();
		stmt           =connection.createStatement();
		values         =values.substring(0,values.length()-1);
		sql            ="SELECT "+values+" FROM FS_FR_CACONTRACTMASTER A , FS_FR_CAMASTER B WHERE"+
						" A.CARRIERID = B.CARRIERID AND A.TERMINALID=UPPER('"+terminalId+"')ORDER BY A.CARRIERID";

		sql1           ="SELECT COUNT(*) NO_ROWS FROM FS_FR_CACONTRACTMASTER A , FS_FR_CAMASTER B WHERE"+
                        " A.CARRIERID = B.CARRIERID AND A.TERMINALID=UPPER('"+terminalId+"')ORDER BY A.CARRIERID";
		rs           =stmt.executeQuery(sql1);
		rs.next();
		noOfrows     = rs.getInt("NO_ROWS");
		records      = new String[noOfrows][count];
		rs1    =stmt.executeQuery(sql);
		for(int i=0;rs1.next();i++)
		{
			for(int j=0;j<count;j++)
			{
				records[i][j] = rs1.getString(j+1);
			}
		}
	}
		catch(SQLException sqEx)
        {
			
			//Logger.error(FILE_NAME, "[getGWCarrierContracts(String values,String terminalId,int count)] -> "+sqEx.toString());
      logger.error(FILE_NAME+ "[getGWCarrierContracts(String values,String terminalId,int count)] -> "+sqEx.toString());
            throw new EJBException(sqEx.toString());
        }
        finally
        {
          ConnectionUtil.closeConnection(connection, stmt, rs);
          ConnectionUtil.closeConnection(null, null, rs1);
        }
	return records;
}//end the CarrierContracts method


//starts the Terminalmaster method
 /**
   * This method is used to retrive the TerminalMaster's Data from the database for a
   * particular field.This method takes valuesand count as agrguments,values represents the records
   * and count is represents the records size
   * It returns a records this is String double array that contains TerminalId,ContactPerson,Designation,city
   *
   * @see                  SelectTerminal.jsp
   * @see                  TerminalMasterProcess.jsp
   * @see                  ESupplyGlobalParameters.java
   * @Params               String values, int count
   * @return               records
   * @exception            javax.ejb.EJBException
   * @author               NAGESWARARAO.D
   */
 public String[][] getTerminalMaster(String values,int count,String terminal)
 {
	Connection connection = null;
	ResultSet   rs        = null;
	ResultSet   rs1       = null;
	Statement   stmt      = null;
    int noOfrows          = 0;
	String records[][]    = null;
	String sql            = null;
	String sql1           = null;
	try
	{
		connection     = this.getConnection();
		stmt           =connection.createStatement();
		values         =values.substring(0,values.length()-1);
		sql="SELECT COMPANYID FROM FS_FR_TERMINALMASTER WHERE TERMINALID='"+terminal+"'";
		String comp= null;
		rs=stmt.executeQuery(sql);

		if(rs.next())
		{comp=rs.getString(1);}
		sql            ="SELECT "+values+" FROM FS_FR_TERMINALMASTER A , FS_ADDRESS B"+
                        " WHERE A.CONTACTADDRESSID=B.ADDRESSID  ORDER BY A.TERMINALID";

		sql1           ="SELECT COUNT(*) NO_ROWS FROM FS_FR_TERMINALMASTER A , FS_ADDRESS B"+
                        " WHERE A.CONTACTADDRESSID=B.ADDRESSID  ORDER BY A.TERMINALID";
		rs             = stmt.executeQuery(sql1);
		rs.next();
		noOfrows      = rs.getInt("NO_ROWS");
		records        = new String[noOfrows][count];
		rs1            = stmt.executeQuery(sql);
		for(int i=0;rs1.next();i++)
		{
			for(int j=0;j<count;j++)
			{
				records[i][j]=rs1.getString(j+1);

			}
		}
	}
		catch(SQLException sqEx)
        {
			//Logger.error(FILE_NAME, "[getTerminalMaster(String values,int count,String terminal)] -> "+sqEx.toString());
      logger.error(FILE_NAME+ "[getTerminalMaster(String values,int count,String terminal)] -> "+sqEx.toString());
            throw new EJBException(sqEx.toString());
        }
        finally
        {
          ConnectionUtil.closeConnection(connection, stmt, rs);
          ConnectionUtil.closeConnection(null, null, rs1);
        }
	return records;
} //end the terminalmaster method


//added by durga from sravan of anand (tat report) ends here
//added by durga on 4th april 2001 from raghu ram reddy starts here
/**
	*The below method is used to get Customer Contracts
    *This method takes values,terminalId,count as arguments and returns a double Dimensional array
	*@param String values,String terminalId,int count
	*@return String[][] records
	*/
public String[][] getCustomerContracts(String values,String terminalId,int count)
{
	Connection connection  = null;
	ResultSet  rs          = null;
	ResultSet  rs1         = null;
	Statement  stmt        = null;
	String records[][]     = null;
	int noOfrows           = 0;
	String sql             = null;
	String sql1            = null;
	try
	{
		connection     =this.getConnection();
		stmt           =connection.createStatement();
		values         =values.substring(0,values.length()-1);
		sql            ="SELECT "+values+" FROM FS_FR_CUSTCONTRACTMASTER A,FS_FR_CUSTOMERMASTER B"+
                        " WHERE A.ID = B.CUSTOMERID AND A.TERMINALID=UPPER('"+terminalId+"') AND A.CONTRACTLEVEL = 'T' ORDER BY A.ID";

		sql1           ="SELECT COUNT(*) NO_ROWS FROM FS_FR_CUSTCONTRACTMASTER A,FS_FR_CUSTOMERMASTER B"+
                        " WHERE A.ID = B.CUSTOMERID AND A.TERMINALID=UPPER('"+terminalId+"') AND A.CONTRACTLEVEL = 'T' ORDER BY A.ID";

		rs           =stmt.executeQuery(sql1);
		if(rs.next())
			{noOfrows = rs.getInt("NO_ROWS");}
		records	= new String[noOfrows][count];
		rs1    = stmt.executeQuery(sql);
		for(int i=0;rs1.next();i++)
		{
			for(int j=0;j<count;j++)
			{
				records[i][j] = rs1.getString(j+1);
			}
		}
	}
	catch(Exception e)
	{
		
		//Logger.error(FILE_NAME, "Exception in getCustomerContracts() method of ETransReportSessionBean"+e.toString());
    logger.error(FILE_NAME+ "Exception in getCustomerContracts() method of ETransReportSessionBean"+e.toString());
		throw new EJBException(e.toString());
	}
	finally
	{
		try
		{
			if(rs!=null)
				{rs.close();}
			if(rs1!=null)
				{rs1.close();}
			if(stmt!=null)
				{stmt.close();}
			if(connection!=null)
				{connection.close();}
		}
		catch(Exception e)
		{
			//Logger.error(FILE_NAME, "Exception in Finally block of getCustomerContracts() of ETransReportSessionBean"+e.toString());
      logger.error(FILE_NAME+ "Exception in Finally block of getCustomerContracts() of ETransReportSessionBean"+e.toString());
		}
	}
	return records;
} //end of the getCustomerContracts method

//Start the CustomerRegistration
 /**
   * This method is used to retrive the CustomerRegistration's Data from the database for a
   * particular field.This method takes values,termianlId and count as agrguments,values represents the records
   * and count is represents the records size,terminalId represent the area
   * It returns a records this is String double array that contains CustomerId,CustomerName,Registration,City
   *
   * @see                  SelectCustomerRegistration.jsp
   * @see                  CustomerRegistrationProcess.jsp
   * @see                  ESupplyGlobalParameters.java
   * @Params               String values, int count,String terminalId
   * @return               records
   * @exception            javax.ejb.EJBException
   * @author               NAGESWARARAO.D
  */
public String[][] getCustomerRegistration(String values,String terminalId,int count)
{
	Connection connection  = null;
	ResultSet  rs          = null;
	ResultSet  rs1         = null;
	Statement  stmt        = null;
	String records[][]     = null;
	int noOfrows           = 0;
	String sql             = null;
	String sql1            = null;
	try
	{
		connection     =this.getConnection();
		stmt           =connection.createStatement();
		values         =values.substring(0,values.length()-1);
		sql            ="SELECT "+values+" FROM FS_FR_CUSTOMERMASTER A,FS_ADDRESS B"+
                        " WHERE A.CUSTOMERADDRESSID = B.ADDRESSID AND A.TERMINALID=UPPER('"+terminalId+"') ORDER BY A.CUSTOMERID";


        sql1           ="SELECT COUNT(*) NO_ROWS FROM FS_FR_CUSTOMERMASTER A,FS_ADDRESS B"+
                        " WHERE A.CUSTOMERADDRESSID = B.ADDRESSID AND A.TERMINALID=UPPER('"+terminalId+"') ORDER BY A.CUSTOMERID";

	    rs           =stmt.executeQuery(sql1);
		rs.next();
		noOfrows     = rs.getInt("NO_ROWS");
		records      = new String[noOfrows][count];
		rs1    =stmt.executeQuery(sql);
		for(int i=0;rs1.next();i++)
		{
			for(int j=0;j<count;j++)
			{
				records[i][j] = rs1.getString(j+1);
			}
		}
	}
		catch(SQLException sqEx)
        {

			//Logger.error(FILE_NAME, "[getCustomerRegistration(String values,String terminalId,int count)] -> "+sqEx.toString());
      logger.error(FILE_NAME+"[getCustomerRegistration(String values,String terminalId,int count)] -> "+sqEx.toString());
            throw new EJBException(sqEx.toString());
        }
        finally
        {
          ConnectionUtil.closeConnection(connection, stmt, rs);
          ConnectionUtil.closeConnection(null, null, rs1);
        }
	return records;
}
//end the CustomerRegstration method

/**
* This method is used to retrive the GatewayDetails's Data from the database for a
* particular field.This method takes values and count as agrguments,values represents the records
* and count is represents the records size
* It returns a records this is String double array that contains GatewayId,GatewayName,GatewayType,
* CompanyName,ContactPerson,Indicator,city
*
* @see                  SelectGatewayDetails.jsp
* @see                  GatewayDetailes.jsp
* @see                  ESupplyGlobalParameters.java
* @Params               String values, int count
* @return               records
* @exception            javax.ejb.EJBException
* @author               NAGESWARARAO.D
*/
public String[][] getGatewayDetails(String values,int count)
{
	Connection connection  = null;
	ResultSet  rs          = null;
	ResultSet  rs1         = null;
	Statement  stmt        = null;
	String records[][]     = null;
	String names[]         = null;
	int noOfrows           = 0;

	try
	{
		connection     =this.getConnection();
		stmt           =connection.createStatement();
		values         =values.substring(0,values.length()-1);
		String sql ="SELECT "+values+" FROM FS_FR_GATEWAYMASTER A , FS_ADDRESS B"+
		            " WHERE A.CONTACTADDRESSID = B.ADDRESSID ORDER BY A.GATEWAYID";

		String sql1 ="SELECT COUNT(*) NO_ROWS FROM FS_FR_GATEWAYMASTER A , FS_ADDRESS B"+
		             " WHERE A.CONTACTADDRESSID = B.ADDRESSID ORDER BY A.GATEWAYID";

		rs           =stmt.executeQuery(sql1);
		rs.next();
		noOfrows     = rs.getInt("NO_ROWS");
		records      = new String[noOfrows][count];
		rs1    =stmt.executeQuery(sql);

		for(int i=0;rs1.next();i++)
		{
			for(int j=0;j<count;j++)
			{
				records[i][j] = rs1.getString(j+1);
			}
		}
	}
		catch(SQLException sqEx)
        {
			//Logger.error(FILE_NAME, "[getGatewayDetails(String values,int count)] -> "+sqEx.toString());
      logger.error(FILE_NAME+ "[getGatewayDetails(String values,int count)] -> "+sqEx.toString());
            throw new EJBException(sqEx.toString());
        }
        finally
        {
          ConnectionUtil.closeConnection(connection, stmt, rs);
          ConnectionUtil.closeConnection(null, null, rs1);
        }
	return records;
} // end the GatewayDetails method

// ETransHOSuperUser Country Master	Methods	Starts here	  --- Author : Ratan Malik
  /**
   * 
   * @param countryId
   * @return 
   */
	public boolean isCountryMasterCountryAlreadyExists(	String countryId	)
	{
		ResultSet  rs	= null;
		Statement  stmt	= null;
		Connection connection =	null;
		try
		{
			connection=operationsImpl.getConnection();
			stmt=connection.createStatement();
			 String	sqlQuery="SELECT COUNTRYID FROM	FS_COUNTRYMASTER	WHERE COUNTRYID= '"	+countryId+"'" ;
			 rs= stmt.executeQuery(sqlQuery);
			 if( rs.next() )
       {
				return true;
       }
			 else
       {
				return false;
       }
		}
		catch(SQLException sqle	)
		{
			
			//Logger.error(FILE_NAME,"SQL	Error ETransHOSuperUserSetupSessionBean::isCompanyAlreadyExists "+sqle.toString());
      logger.error(FILE_NAME+"SQL	Error ETransHOSuperUserSetupSessionBean::isCompanyAlreadyExists "+sqle.toString());
			throw new EJBException(sqle.toString());
		}
		catch(Exception	exp	)
		{
			
			//Logger.error(FILE_NAME,"Error ETransHOSuperUserSetupSessionBean::isCompanyAlreadyExists " +exp.toString());
      logger.error(FILE_NAME+"Error ETransHOSuperUserSetupSessionBean::isCompanyAlreadyExists " +exp.toString());
			throw new EJBException(exp.toString());
		}
		finally
		{
			try
			{
					if(	rs != null )
          {
						rs.close();
          }
					if(	stmt !=	null )
          {
						stmt.close();
          }
					if(	connection != null )
          {
						connection.close();
          }
			}
			catch(SQLException sqle	)
			{
				//Logger.error(FILE_NAME,"SQL	Error while	closing	connections	ETransHOSuperUserSetupSessionBean::isCompanyAlreadyExists "+sqle.toString());
        logger.error(FILE_NAME+"SQL	Error while	closing	connections	ETransHOSuperUserSetupSessionBean::isCompanyAlreadyExists "+sqle.toString());
			}
		}//End of finally block.

	}

	 /**
   * 
   * @param countryMaster
   * @param loginDetails
   */
	public	void addCountryMasterDetails( CountryMaster	countryMaster, ESupplyGlobalParameters loginDetails	)
	{
		Connection connection =	null;
		Statement stmt=	null;
		try
		{
			connection=operationsImpl.getConnection();
			stmt= connection.createStatement();
			String[]	cid= countryMaster.getCountryId();
			String[]	cnm= countryMaster.getCountryName();
			String[]	curid=countryMaster.getCurrencyId();
			String[]	reg= countryMaster.getRegion();
			String	area= countryMaster.getArea();
			//Logger.info(FILE_NAME,"getCountryId"+cid[0]);
			//Logger.info(FILE_NAME,"getCountryName"+cnm[0]);
			//Logger.info(FILE_NAME,"getCurrencyId"+curid[0]);
			//Logger.info(FILE_NAME,"getRegion"+reg[0]);
			String sqlInsert	= "INSERT INTO	FS_COUNTRYMASTER (COUNTRYID ,COUNTRYNAME ,CURRENCYID ,REGION ,AREA ,INVALIDATE ,CURRENCY_INVALIDATE ,TERMINALID )	VALUES('"+cid[0]+"','"+cnm[0]+"','"+curid[0]+"','"+reg[0]+"','"+area+"','F','F','"+loginDetails.getTerminalId()+"')";//terminalid added by rk
			stmt.executeUpdate(sqlInsert);
			operationsImpl.setTransactionDetails(	loginDetails.getTerminalId(),
														loginDetails.getUserId(),
														"CountryMaster",
														cid[0],
														loginDetails.getLocalTime(),
														"ADD");
		}
		catch(SQLException sqle)
		{
			
			throw new EJBException("SQLException	in setCountryMaster	ETransHOSuperUserSetupSessionBean "+sqle.toString());
		}
		catch(Exception	cnfe)
		{
			throw new javax.ejb.EJBException("Exception in	setCountryMaster ETransHOSuperUserSetupSessionBean "+cnfe.toString());
		}
		finally
		{
			try
			{
				if(	stmt !=	null )
        {
					stmt.close();
        }
				if(	connection != null )
        {
					connection.close();
        }
			}//end	of try
			catch(SQLException sqle)
			{
				//Logger.error(FILE_NAME,"SQLException in	setCountryMaster Closing ETransHOSuperUserSetupSessionBean "+sqle.toString());
        logger.error(FILE_NAME+"SQLException in	setCountryMaster Closing ETransHOSuperUserSetupSessionBean "+sqle.toString());
			}
		}
	}//end	of method.

	/**
   * 
   * @param countryMaster
   * @param loginDetails
   * @return 
   */
	public boolean updateCountryMasterDetails(CountryMaster	countryMaster,ESupplyGlobalParameters	loginDetails) 
	{
			Connection connection =	null;
			Statement stmt = null;
			try
			{
				connection=operationsImpl.getConnection();
				stmt = connection.createStatement();
				String[]  cid	   = countryMaster.getCountryId();
				String[]  cnm	   = countryMaster.getCountryName();
				String[]  curid	   = countryMaster.getCurrencyId();
				String[]  reg	   = countryMaster.getRegion();
				String area	   = countryMaster.getArea();
				String sqlUpdate  =	" UPDATE FS_COUNTRYMASTER SET COUNTRYNAME ='" +cnm[0] +"', CURRENCYID ='" + curid[0]	+"'	, REGION ='" + reg[0] +"' , AREA = '"+area+"' WHERE COUNTRYID='"+cid[0]+"'";
				stmt.executeUpdate(	sqlUpdate );
				  operationsImpl.setTransactionDetails(loginDetails.getTerminalId(),
														loginDetails.getUserId(),
														"CountryMaster",
														cid[0],
														loginDetails.getLocalTime(),
														"MODIFY");
				return true;
			}
			catch (	SQLException	sqle )
			{
				//Logger.error(FILE_NAME,"SQLException	in updateCountryMasterDetails of ETransHOSuperUserSetupSessionBean "+sqle.toString());
        logger.error(FILE_NAME+"SQLException	in updateCountryMasterDetails of ETransHOSuperUserSetupSessionBean "+sqle.toString());
				error =	sqle.toString();
				throw new EJBException(sqle.toString());
			}
			catch( Exception	cnfe )
			{
				throw new javax.ejb.EJBException("EJBException	in updateCountryMasterDetails of	CountryMaster "	+cnfe.toString());
			}
			finally
			{
				try
				{
					if(	stmt !=	null )
						{stmt.close();}
					if(	connection != null )
						{connection.close();}
				}
				catch(SQLException sqle)
				{
					//Logger.error(FILE_NAME,"SQLException	in updateCountryMasterDetails of ETransHOSuperUserSetupSessionBean "+sqle.toString());
          logger.error(FILE_NAME+"SQLException	in updateCountryMasterDetails of ETransHOSuperUserSetupSessionBean "+sqle.toString());
				}
			}
			
	}//	end	of updateCountryDetails

	 /**
   * 
   * @param countryMasterId
   * @param loginDetails
   * @return 
   */
	public boolean deleteCountryMasterDetails(String countryMasterId ,ESupplyGlobalParameters loginDetails)	
	{
		Connection	  connection  =	null;
		Statement	   stmt		  =	null;
		Statement	   stmt1	  =	null;
    ResultSet rs          = null;//Added By RajKumari on 30-10-2008 for Connection Leakages.
		try
		{
			connection=operationsImpl.getConnection();
			stmt =	connection.createStatement();
			stmt1 = connection.createStatement();
			String	sqlQuery = "SELECT * FROM FS_COUNTRYMASTER WHERE	COUNTRYID='"+countryMasterId+"'";
			rs =	stmt1.executeQuery(sqlQuery);//Modified By RajKumari on 30-10-2008 for Connection Leakages.
			if(rs.next())
			{
				String sqlDelete = "DELETE FROM	FS_COUNTRYMASTER	WHERE COUNTRYID='"+countryMasterId+"'";
				stmt.executeUpdate(	sqlDelete  );
				operationsImpl.setTransactionDetails(loginDetails.getTerminalId(),
														loginDetails.getUserId(),
														"CountryMaster",
														countryMasterId,
														loginDetails.getLocalTime(),
														"DELETE");
			}
			else
			{
				error =	"RECORD	IS NOT PRESENT ";
				return false;
			}
			return	true;
		}
		catch (	SQLException sqle )
		{
			
			//Logger.error(FILE_NAME,"SQLException	in deleteCountryMaster of ETransHOSuperUserSetupSessionBean "+sqle.toString());
      logger.error(FILE_NAME+"SQLException	in deleteCountryMaster of ETransHOSuperUserSetupSessionBean "+sqle.toString());
			error =	sqle.toString();
			return false;
		}
		catch( Exception cnfe	)
		{
			throw	new	javax.ejb.EJBException("Exception	in deleteCountryMaster of countryMasterETransHOSuperUserSetupSessionBean "+cnfe.toString() );
		}
		finally
		{
			try
			{
        //Added By RajKumari on 30-10-2008 for Connection Leakages.
        if(rs!=null)
        {
          rs.close();
          rs = null;
        }
				if(	stmt !=	null )
					{stmt.close();}
				//Connection Leakage ------------------ 7-DEC-2004--------- Santhosam.P
				if(	stmt1 !=	null )
					{stmt1.close();}
				if(	connection != null )
					{connection.close();}
			}
			catch(SQLException sqle)
			{
				//Logger.error(FILE_NAME,"SQLException in	deleteCountryMaster	of ETransHOSuperUserSetupSessionBean	"+sqle.toString());
        logger.error(FILE_NAME+"SQLException in	deleteCountryMaster	of ETransHOSuperUserSetupSessionBean	"+sqle.toString());
			}
		}
	}	// END OF DELETING COUNTRY MASTER

	/**
   * 
   * @param countryId
   * @return 
   */
	public CountryMaster getCountryMasterDetails(String	countryId) 
	{
			Connection	connection = null;
			Statement stmt	= null;
			ResultSet rs =	null;
			CountryMaster countryMaster	= null;
			String[] countryMasterId	= new String[1];
			String[] countryName		= new String[1];
			String[] currencyId			= new String[1];
			String[] region				= new String[1];
			String area = "";
			try
			{
				String sqlQuery	=	"SELECT	* FROM FS_COUNTRYMASTER WHERE FS_COUNTRYMASTER.COUNTRYID = '"+countryId+"' AND INVALIDATE='F'";
				connection=operationsImpl.getConnection();
				stmt = connection.createStatement();
				rs = stmt.executeQuery(	sqlQuery );
				if(	rs.next() )
				{
					countryMasterId[0] = rs.getString( "countryId" );
					countryName[0]	 = rs.getString( "countryName" );
					currencyId[0]		 = rs.getString( "currencyId" );
					region[0]			 = rs.getString( "region" );
					area = rs.getString("AREA");
					countryMaster =	new	CountryMaster();
					countryMaster.setCountryId(countryMasterId);
					countryMaster.setCountryName(countryName);
					countryMaster.setCurrencyId(currencyId);
					countryMaster.setRegion(region);
					countryMaster.setArea(area);
				}
				else
					return	null;
			}
			catch(	SQLException sqle)
			{
				
				//Logger.error(FILE_NAME,"SQLException in	getCountryMasterDetails	of ETransHOSuperUserSetupSessionBean" +sqle.toString());
        logger.error(FILE_NAME+"SQLException in	getCountryMasterDetails	of ETransHOSuperUserSetupSessionBean" +sqle.toString());
				throw new EJBException(sqle.toString());
			}
			finally
			{
				try
				{
					if(	rs != null ){
						rs.close();}
					if(	stmt !=	null )
						{stmt.close();}
					if(	connection != null )
						{connection.close();}
				}
				catch(SQLException sqle)
				{
					//Logger.error(FILE_NAME,"SQLException in	getCountryMasterDetails	closing	of ETransHOSuperUserSetupSessionBean	"+sqle.toString());
          logger.error(FILE_NAME+"SQLException in	getCountryMasterDetails	closing	of ETransHOSuperUserSetupSessionBean	"+sqle.toString());
				}
			}
		return countryMaster;
	}

    		public	String getHcurrency(String companyid)
		{
			InitialContext ictx		=	null;
		// @@ 'connection' is replaced with 'connection1' by Suneetha for TogehterJ on 12 Jan 05
			Connection connection1	=	null;
			Statement stmt			=	null;
			ResultSet rs			=	null;
			String hCurrency		=	"";
			try
			{
				if(	dataSource == null )
				{
					ictx = new InitialContext();
					dataSource = ( DataSource )	ictx.lookup( "java:comp/env/jdbc/DB" );
				}
				String sqlQuery	= "SELECT HCURRENCY	FROM FS_COMPANYINFO WHERE COMPANYID='"+companyid+"' AND AGENTJVIND='C' ";
				connection1	=	dataSource.getConnection();
				stmt		=	connection1.createStatement();
				rs			=	stmt.executeQuery(	sqlQuery );
				if(	rs.next())
				{
					hCurrency  = rs.getString( "HCURRENCY" );
				}
				else
				{
					return null;
				}
			}
			catch(Exception	e)
			{
				
				//Logger.error(FILE_NAME,"CurrencyConversionSessionBean.java : getHCurrency()"+e);
        logger.error(FILE_NAME+"CurrencyConversionSessionBean.java : getHCurrency()"+e);
				throw new EJBException(e.toString());
			}
			finally
			{
				try
				{
					ictx = null;
					if(	rs != null )
          {
					rs.close();
          }
					if(	stmt !=	null )
          {
					stmt.close();
          }
					if(	connection1 != null )
          {
					connection1.close();
          }
				}
				catch(Exception	sqle)
				{
          sqle.printStackTrace();
				}
			}
			return hCurrency;
		}

	public Connection  getConnection() throws EJBException
	{
		Connection con = null;
 
		try
		{
			if(dataSource == null)
				{createDataSource();}
				 
			con	= dataSource.getConnection();
		}
		catch( Exception ex)
		{
		   throw new EJBException( "getConnection: "+ex.toString() );
		}
		return con;
	}
	public void	createDataSource() throws EJBException
	{
    InitialContext  initialContext  =   null;
		try
		{
			initialContext = new InitialContext();
			dataSource = (DataSource)initialContext.lookup("java:comp/env/jdbc/DB");
		}
		catch( Exception e )
		{
			throw new EJBException( "createDataSource:: "+e.toString() );
		}
	}	
    	private ArrayList	getCustomerMoreAddressIdsList(String customerId,String terminalId,Connection	connection){
			
				
			ArrayList	alistAddIds		=	new ArrayList();
		//	String		terminalId		=	esupplyGlobalParameters.getTerminalId();
			//Connection	connection		=	null;
			Statement	stmt			=	null;
			ResultSet	rs				=	null;
			String		sqlQry			=	"SELECT CUSTOMERADDRESSID,CONTACTNAME,DESIGNATION,DEL_FLAG,ADDRESS_TYPE FROM FS_FR_CUSTOMER_ADDRESS WHERE CUSTOMERID='"+customerId+"' AND TERMINALID='"+terminalId+"' AND DEL_FLAG='N'";
	//Logger.info(FILE_NAME," getCustomerMoreAddressIdsList() privt Method--> "+sqlQry);

			try{
				//connection	=	this.getConnection();
				stmt		=	connection.createStatement();
				rs			=	stmt.executeQuery(sqlQry);
				while(rs.next()){
					alistAddIds.add(new CustomerModel(customerId, terminalId,rs.getInt(1),rs.getString(2),rs.getString(3),rs.getString(4),rs.getString(5)));
				}

			}catch(Exception exp){
				
				//Logger.error(FILE_NAME," getCustomerMoreAddressIdsList() private Method--> "+exp.toString());
        logger.error(FILE_NAME+" getCustomerMoreAddressIdsList() private Method--> "+exp.toString());
				throw new EJBException(exp.toString());
			}
			finally{
				try{
					if(rs!=null){
						rs.close();
					}if(stmt!=null){
						stmt.close();
					}
				}catch(Exception exp){
					//Logger.error(FILE_NAME," getCustomerMoreAddressIdsList() private Method--> "+exp.toString());
          logger.error(FILE_NAME+" getCustomerMoreAddressIdsList() private Method--> "+exp.toString());
				}
			}
		return alistAddIds;

	}//End of the private Method
//JS Modified as 10/10/02 END

// @@ Suneetha Added on 20050308 for Bulk Invoicing
 private int createCustomerInvoiceInfo(String customerId, String terminalId, Timestamp invoiceFrequencyValidDate, String invoiceFrequencyFlag, String invoiceInfo,  Connection connection)
 {
		PreparedStatement		pstmt			= null;
		PreparedStatement		pstmt1			= null;
		ResultSet				rs				= null;
		int rowsAffected				= 0;
		String			sql				= "";
		int onceIn=0;
		try
		{	
				pstmt = connection.prepareStatement("SELECT CUSTOMERID FROM FS_FR_CUSTOMERINVOICEHDR WHERE CUSTOMERID=?");
				pstmt.setString(1,customerId);
				rs=pstmt.executeQuery();
				if(rs.next())
				{
					pstmt1 = connection.prepareStatement("DELETE FS_FR_CUSTOMERINVOICEHDR WHERE CUSTOMERID=?");
					pstmt1.setString(1,customerId);
					pstmt1.executeUpdate();
				}
        //Added By RajKumari on 30-10-2008 for Connection Leakages.
        if(pstmt !=null)
        {
          pstmt.close();
          pstmt = null;
        }

				if("D".equals(invoiceFrequencyFlag) || "M".equals(invoiceFrequencyFlag))
				{
					sql="INSERT INTO FS_FR_CUSTOMERINVOICEHDR(CUSTOMERID,TERMINALID,VALIDFROM,FREQUENCYTYPE) VALUES(?,?,?,?)";
					pstmt = connection.prepareStatement(sql); 
					pstmt.setString(1,customerId);
					pstmt.setString(2,terminalId);
					pstmt.setTimestamp(3,invoiceFrequencyValidDate);
					pstmt.setString(4,invoiceFrequencyFlag);			
				}
				else if("O".equals(invoiceFrequencyFlag) || "F".equals(invoiceFrequencyFlag))
				{
					onceIn = Integer.parseInt(invoiceInfo);		
					sql="INSERT INTO FS_FR_CUSTOMERINVOICEHDR(CUSTOMERID,TERMINALID,VALIDFROM,FREQUENCYTYPE,FREQUENCY) VALUES(?,?,?,?,?)";
					pstmt = connection.prepareStatement(sql); 
					pstmt.setString(1,customerId);
					pstmt.setString(2,terminalId);
					pstmt.setTimestamp(3,invoiceFrequencyValidDate);
					pstmt.setString(4,invoiceFrequencyFlag);
					pstmt.setInt(5,onceIn);
				}
				else if("W".equals(invoiceFrequencyFlag) || "S".equals(invoiceFrequencyFlag))
				{
					sql="INSERT INTO FS_FR_CUSTOMERINVOICEHDR(CUSTOMERID,TERMINALID,VALIDFROM,FREQUENCYTYPE,FREQUENCYINFO) VALUES(?,?,?,?,?)";   
					pstmt = connection.prepareStatement(sql); 
					pstmt.setString(1,customerId);
					pstmt.setString(2,terminalId);
					pstmt.setTimestamp(3,invoiceFrequencyValidDate);
					pstmt.setString(4,invoiceFrequencyFlag);
					pstmt.setString(5,invoiceInfo);
				}
				
				//Logger.info(FILE_NAME,"Sql in createCustomerInvoiceInfo()-------- " + sql);		
				rowsAffected = pstmt.executeUpdate();	
				//Logger.info(FILE_NAME,"Sql in createCustomerInvoiceInfo()---rowsAffected----- " + rowsAffected);		
				if(rowsAffected!=1)
				  {
					 throw new EJBException("Error While storing Customerinvoice Details");
				  }
			
		}catch(Exception ex)
	    {
	     //Logger.error(FILE_NAME,"Exception :: createCustomerInvoiceInfo() :::: " + ex.getMessage());
       logger.error(FILE_NAME+"Exception :: createCustomerInvoiceInfo() :::: " + ex.getMessage());
	     throw new EJBException(ex.toString());
	    }
      //Added By RajKumari on 30-10-2008 for Connection Leakages.
      finally
      {
        ConnectionUtil.closePreparedStatement(pstmt,rs);
        ConnectionUtil.closePreparedStatement(pstmt1);
      }
      return rowsAffected;
 }
    // Added By Prasada Reddy for Provide Prefered Service Level to Customer

  //For Create Customer and Service Level Map
  /**
   * 
   * @param custId
   * @param terId
   * @param serviceLevelId
   * @throws java.sql.SQLException
   */
 private void createCustServiceLevel(String custId,String terId,String serviceLevelId,Connection con) throws SQLException
   {
    // Connection con = null;
     Statement stmt = null;
     String sql  =  null;
     try
       {
       //  con=getConnection();
         stmt=con.createStatement();
         sql="INSERT INTO FS_FR_CUSTOMER_SRVC_LVL(TERMINALID,CUSTOMER_ID,SERVICELEVELID)VALUES ('"+terId+"','"+custId+"','"+serviceLevelId+"')";
         int i=stmt.executeUpdate(sql);
       }
      catch(SQLException sqlex)
          {
          //Logger.error(FILE_NAME,"ETransHOSuperUserSessionBean : SQLException in createCustServiceLevel() : "+sqlex.toString());
          logger.error(FILE_NAME+"ETransHOSuperUserSessionBean : SQLException in createCustServiceLevel() : "+sqlex.toString());
          throw new SQLException("Data Failed to Insert");
          }
       finally{
				try{
					
					if(stmt!=null){
						{stmt.close();}
					}

				}catch(Exception exp){
					//Logger.error(FILE_NAME," createCustServiceLevel() private Method--> "+exp.toString());
          logger.error(FILE_NAME+" createCustServiceLevel() private Method--> "+exp.toString());
			   	}
			  }   
   }//end for Create Customer and Service Level Map

// @@ 20050308 for Bulk Invoicing
 private void deleteCustServiceLevel(String custId,Connection con)throws SQLException   
   {
   //Connection con = null;
   Statement stmt = null;
   String sql  = null;
       try
         {
         sql="DELETE FROM FS_FR_CUSTOMER_SRVC_LVL WHERE CUSTOMER_ID='"+custId+"'";
        // con=getConnection();
         stmt=con.createStatement();
         boolean flag=stmt.execute(sql);
         }
      catch(SQLException sqlex)
           {
           //Logger.error(FILE_NAME,"ETransHOSuperUserSessionBean : SQLException in deleteCustServiceLevel() : "+sqlex.toString());
           logger.error(FILE_NAME+"ETransHOSuperUserSessionBean : SQLException in deleteCustServiceLevel() : "+sqlex.toString());
            throw new SQLException("Data Failed to Delete");
            }
       finally{
          try{
					
            if(stmt!=null){
              {stmt.close();}
            }

          }catch(Exception exp){
            //Logger.error(FILE_NAME," createCustServiceLevel() private Method--> "+exp.toString());
            logger.error(FILE_NAME+" createCustServiceLevel() private Method--> "+exp.toString());
            }
			  } 
   }
 
 	private String getShipmentModeValue(int shipMode)
	{
		String shipmentModevalue = "";

		switch(shipMode)
		{
			case 1:	shipmentModevalue = "A";
					break;
			case 2:	shipmentModevalue = "S";
					break;
			case 3:	shipmentModevalue = "A S";
					break;
			case 4:	shipmentModevalue = "T";
					break;
			case 5:	shipmentModevalue = "A T";
					break;
			case 6:	shipmentModevalue = "S T";
					break;
			case 7:	shipmentModevalue = "A S T";
					break;
		}
		return shipmentModevalue;
	}  
  private boolean isTaskExist(String servicelevelId,int eventId,String originLoc,String destiLoc,Connection connection)
    {
   // Connection connection =	null;
		Statement stmt 		  = null;
		ResultSet rs 		  = null;
		boolean  sMode			  = false;
    String sqlQuery    =null;
		try
		{
    // connection 	= 	getConnection();
	  	stmt = connection.createStatement();
     if(originLoc!=null)
        { sqlQuery	= "	SELECT * FROM FS_FR_LOC_TASK_PLAN WHERE SERVICELEVELID = '"+servicelevelId.trim()+"' AND EVNT_ID="+eventId+" AND ORIG_LOC_ID='"+originLoc+"' AND DEST_LOC_ID='"+destiLoc+"'";}
     else
        { sqlQuery	= "	SELECT * FROM FS_FR_SRVC_LVL_TASK_PLAN WHERE SERVICELEVELID = '"+servicelevelId.trim()+"' AND EVNT_ID="+eventId ;}
        
    	rs = stmt.executeQuery(	sqlQuery );
			if(	rs.next())
				{sMode	=	true;}
		}
		catch( SQLException	sqle)
		{
			
		  //Logger.error(FILE_NAME," ETransHOSuperUserSetupSessionBean : SQLException in isTaskExist() : "+sqle.toString());
      logger.error(FILE_NAME+" ETransHOSuperUserSetupSessionBean : SQLException in isTaskExist() : "+sqle.toString());
		  throw new EJBException(sqle.toString());
		}
		catch(Exception	e)
		{
			//Logger.error(FILE_NAME,"ETransHOSuperUserSetupSessionBean	: isTaskExist() :	"+e.toString());
      logger.error(FILE_NAME+"ETransHOSuperUserSetupSessionBean	: isTaskExist() :	"+e.toString());
			throw new EJBException("Operation Failed. Try Again.");
		}
		finally
		{
			ConnectionUtil.closeConnection(null,stmt,rs);
		}
		return sMode;
    }
      private  void  deleteEvntDetails(String serviceLevelId,Connection connection)	
  {
    //Connection connection=null;
    Statement stmt=null;
    Statement stmt1=null;
    try
    {
     // connection=this.getConnection();
      stmt=connection.createStatement();
      stmt1=connection.createStatement();      
      boolean flag=stmt.execute("DELETE FROM FS_FR_LOC_TASK_PLAN WHERE SERVICELEVELID='"+serviceLevelId.trim()+"'");
      flag=stmt1.execute("DELETE FROM FS_FR_SRVC_LVL_TASK_PLAN  WHERE SERVICELEVELID='"+serviceLevelId.trim()+"'");      
    }
    catch(SQLException exp)
        {
        //Logger.error(FILE_NAME,"Exception in deleteEvntDetails(String)");
        logger.error(FILE_NAME+"Exception in deleteEvntDetails(String)");
        throw new EJBException("**Operation Failed. Try Again.");
         } 
     finally
     {
      try{
       stmt.close();
       stmt1.close();
      
        }
      catch(Exception ex)
        { 
        //Logger.error(FILE_NAME,"Exception in deleteEvntDetails(String)","Connection not closed"); 
        logger.error(FILE_NAME+"Exception in deleteEvntDetails(String)"+"Connection not closed"); 
        }
     }
    
  }
    private void setLocationId( String terminalId , String[]  locationId,Connection connection )
  	{
  	    PreparedStatement stmt1= null;    // a PreparedStatement variable
  	    try
  	    {
 	       
 		 	String sqlInsert = "INSERT INTO FS_FR_TERMINALLOCATION VALUES('"+terminalId+"',"+"?)";    // a String having  an insertion statement
		    stmt1 = connection.prepareStatement(sqlInsert);
		    // setting different location ids from locationId array and executing the insertion operation
		    int locIdLen	=	locationId.length;
		    for( int i=0;i<locIdLen;i++)
			{
				if(!locationId[i].trim().equals("---"))
				{
   				   stmt1.setString(1,locationId[i].trim());
	 	    	   stmt1.executeUpdate();
				}
	 	    }
  	     }
	    catch( SQLException e )
		{
			//Logger.error(FILE_NAME, "Exception in  setLocationId() method of TerminalRegistrationSessionBean : "+e.toString() );
      logger.error(FILE_NAME+ "Exception in  setLocationId() method of TerminalRegistrationSessionBean : "+e.toString() );
			throw new EJBException( "Exception in  setLocationId() method of TerminalRegistrationSessionBean : "+e.toString() );
	    }
   		catch( Exception e )
   		{
   			//Logger.error(FILE_NAME,"Exception in  setLocationId() method of TerminalRegistrationSessionBean : "+e.toString() );
        logger.error(FILE_NAME+"Exception in  setLocationId() method of TerminalRegistrationSessionBean : "+e.toString() );
			throw new EJBException( "Exception in  setLocationId() method of TerminalRegistrationSessionBean : "+e.toString() );
   		}
	    finally
  		{
  		   	try
  		    {
  		    	if( stmt1!=null )
            {
	  	   	   	    stmt1.close();
            }
  		    
  	   	    }
  	   	   	catch( Exception e )
  		 	{
	   			//Logger.error(FILE_NAME,"Exception in  setLocationId() method of TerminalRegistrationSessionBean : "+e.toString() );
          logger.error(FILE_NAME+"Exception in  setLocationId() method of TerminalRegistrationSessionBean : "+e.toString() );
				throw new EJBException( "Exception in  setLocationId() method of TerminalRegistrationSessionBean : "+e.toString() );
  		 	}
   	   	}
    }
    	public  void setOperationTerminalIds( String terminalId , String[]  locationId , Vector  cbtLocationId, ESupplyGlobalParameters loginDetails) 
  	{
  	   //Logger.info(FILE_NAME,"Entered setOperationTerminalIds");
       logger.info(FILE_NAME+"Entered setOperationTerminalIds");
  	   Connection con   = null;
  	   PreparedStatement pStmt1 = null,pStmt2  = null;   // PreparedStatement variable
       Statement  stmt  = null;
       ResultSet  rs    = null;
       int        cnt   = 0; 
	// @@ Replaced by Suneetha for TogetherJ on 12 Jan 05
    //   String bookIdQry = null;
       StringBuffer bookIdQry = new StringBuffer();
	// @@
       long bookId[]  = null;
      
  	    try
  	    {
 	        con = operationsImpl.getConnection();
      	 	String sqlInsert = "INSERT INTO FS_FR_TERMINAL_REGN VALUES(?,?,?)";  // String containing an insertion statement
          pStmt1 = con.prepareStatement(sqlInsert);
            // inserting data in to the table for every location id
          int locIdLen	=	locationId.length;
  		    for( int i=0;i<locIdLen;i++)
      		{
            pStmt1.setString(1,terminalId);
            pStmt1.setString(2,locationId[i].trim());
            if ((cbtLocationId != null) && cbtLocationId.contains(locationId[i]))
            {
				      pStmt1.setString(3,"Y");
            }
            else
            {
				      pStmt1.setString(3,"N");
            }
             //Logger.info(FILE_NAME,terminalId + " " +locationId[i].trim());
	 	       pStmt1.executeUpdate();
          }
          
       		if( loginDetails.getAccessType().equals("ADMN_TERMINAL")) 
            {
              String sql1 = " SELECT COUNT(*) FROM FS_AC_VOUCHERTYPESMASTER WHERE GENERICTYPE IN ('CBR','CBP') ";
              stmt        = con.createStatement();              
              rs          = stmt.executeQuery(sql1);              
              if(rs.next())
              {
                cnt = rs.getInt(1);
              }
              if(stmt!=null)
                stmt.close();
              if(rs!=null)
              rs.close();
              
              if(cnt > 0)   
              {
              //Logger.info(FILE_NAME,"locationId:	"+locationId.length);	
              //Added by Rajkumari on 04-11-2008 for Connection Leakages in Loop
               pStmt2   = con.prepareStatement(
                  "INSERT INTO FS_AC_VOUCHERTYPESMASTER "+
                  "( BOOKID, VOUCHERID, VOUCHERTYPE, VOUCHERNUMBER, CORTERIND, GENERICTYPE) "+
                  "VALUES (?,?,?,?,?,?) "
                      );
                stmt  = con.createStatement();
                int locIdsLen	=	locationId.length;
               for( int j=0;j<locIdsLen;j++)
               {
				   //Logger.info(FILE_NAME,"Connection:	"+con);
    //Commented by Rajkumari on 04-11-2008 for Connection Leakages in Loop
                  /* pStmt2   = con.prepareStatement(
                  "INSERT INTO FS_AC_VOUCHERTYPESMASTER "+
                  "( BOOKID, VOUCHERID, VOUCHERTYPE, VOUCHERNUMBER, CORTERIND, GENERICTYPE) "+
                  "VALUES (?,?,?,?,?,?) "
                      );*/

				// @@ Replaced by Suneetha for TogetherJ on 12 Jan 05
				/*  bookIdQry = " SELECT MAX(BOOKID) FROM FS_AC_COMPANYINFO "+
								" WHERE  "+ 
								" COMPANYID='"+locationId[j]+"'";
				*/
				  bookIdQry.append(" SELECT MAX(BOOKID) FROM FS_AC_COMPANYINFO "+" WHERE  "+" COMPANYID='");
				  bookIdQry.append(locationId[j]);
				  bookIdQry.append("'");                              
				// @@

                  //stmt  = con.createStatement();   //Commented by Rajkumari on 04-11-2008 for Connection Leakages in Loop         
                  rs    = stmt.executeQuery(bookIdQry.toString());
                  bookId  = new long[locationId.length];
                  
                  while(rs.next())
                  {
                      bookId[j]  = rs.getLong(1);        
                  }
                  //Logger.info(FILE_NAME,"bookId[j]: "+bookId[j]);    
                  //pStmt2.setLong(1, bookId[i]);
                  pStmt2.setLong(1, bookId[j]);
                  pStmt2.setString(2, "CBR");
                  pStmt2.setString(3, "CENTRALISED BANKING RECEIPT");
                  pStmt2.setString(4, "1");
                  pStmt2.setString(5, "C");
                  pStmt2.setString(6, "CBR");
					
                  pStmt2.setLong(1, bookId[j]);
                  pStmt2.setString(2, "CBP");
                  pStmt2.setString(3, "CENTRALISED BANKING PAYMENT");
                  pStmt2.setString(4, "1");
                  pStmt2.setString(5, "C");
                  pStmt2.setString(6, "CBP");

                  int  row = pStmt2.executeUpdate(); 
                  if(rs!=null)
                    rs.close();
                  /*if(pStmt2!=null)
                    pStmt2.close();
                  if(stmt!=null)
                    stmt.close();*///Commented by Rajkumari on 04-11-2008 for Connection Leakages in Loop
                }
              }  
          }

  	}
	  catch( SQLException e )
		{
			//Logger.error(FILE_NAME, "Exception in setOperationTerminalIds() method of TerminalRegistrationSessionBean : "+e.toString() );
      logger.error(FILE_NAME+ "Exception in setOperationTerminalIds() method of TerminalRegistrationSessionBean : "+e.toString() );
			e.printStackTrace();
			throw new EJBException( "Exception in setOperationTerminalIds() method of TerminalRegistrationSessionBean : "+e.toString() );
	    }
   		catch( Exception e )
   		{
   			//Logger.error(FILE_NAME,"Exception in setOperationTerminalIds() method of TerminalRegistrationSessionBean : "+e.toString() );
        logger.error(FILE_NAME+"Exception in setOperationTerminalIds() method of TerminalRegistrationSessionBean : "+e.toString() );
			e.printStackTrace();
			throw new EJBException( "Exception in setOperationTerminalIds() method of TerminalRegistrationSessionBean : "+e.toString() );
   		}
	    finally
  		{
  		  ConnectionUtil.closeConnection(con, pStmt1);        
          ConnectionUtil.closeConnection(con, pStmt2);        
          ConnectionUtil.closeConnection(con, stmt,rs);        
   	   	}
    }
   private void insertGatewayDetails(	String	terminalId,
										int		shipMode,
										String	gatewayName,
										String	companyName,
										String	contactName,
										int		addressId,
										String	notes,
										String	timeZone,
										String	shipmentCollect,
										String	gatewayType,
                                        Connection 		 connection1 ) 
	 {
	  	 java.sql.PreparedStatement  pstmt	 		  = null;		// a PreparedStatement variable
		// @@ 'connection' is replaced with 'connection1' by Suneetha for TogehterJ on 12 Jan 05
	  	// java.sql.Connection 		 connection1   	  = null;		// a Connection variable
	  	 try
  	  	 {
  	  	   // connection1 = getConnection();
  	  	 	String str3 = "INSERT	INTO FS_FR_GATEWAYMASTER "+
									"(GATEWAYID,GATEWAYTYPE,GATEWAYNAME,		"+
				 					"COMPANYNAME,CONTACTNAME,CONTACTADDRESSID,	"+
									"NOTES,INDICATOR,TIME_ZONE,CC_SHIPMNT_FLAG, GATEWAY_TYPE) "+
									"VALUES(?,?,?,?,?,?,?,?,?,?,?)";		// a String having an insertion statement
  	  	 	pstmt = connection1.prepareStatement( str3 );
  	  		pstmt.setString(1,terminalId);
  	  		pstmt.setInt(2,shipMode);
  	  		pstmt.setString(3,gatewayName);
  	  		pstmt.setString(4,companyName);
  	  		pstmt.setString(5,contactName);
  	  		pstmt.setInt(6,addressId);
  	  		pstmt.setString(7,notes);
  	  		pstmt.setString(8,"S");
  	  		pstmt.setString(9,timeZone);
			    pstmt.setString(10,shipmentCollect);
  	  		pstmt.setString(11,gatewayType);
  	  		pstmt.executeUpdate();

  	    	String[] strGateway =	new String[1];		// creating a String array of size one
  	  		strGateway[0]		=	terminalId;
			setTerminalGatewayDB( terminalId,  strGateway,connection1 );
  	  	 }
	   	catch( Exception e )
   		{
			
   			//Logger.error(FILE_NAME,"Exception in  insertGatewayDetails() method of ETransHOAdminSetupSessionBean : ",e );
        logger.error(FILE_NAME+"Exception in  insertGatewayDetails() method of ETransHOAdminSetupSessionBean : "+e );
			throw new EJBException(e.toString());
   		}
        finally
        {
         	try
            {

            	if( pstmt!=null )
              {
	        	  	pstmt.close();
              }
          
        	}
        	catch( Exception e )
        	{
	   			//Logger.error(FILE_NAME,"Exception in  insertGatewayDetails() method of TerminalRegistrationEntityBean : ",e );
          logger.error(FILE_NAME+"Exception in  insertGatewayDetails() method of TerminalRegistrationEntityBean : "+e );
        	}
        }
   }

    private void insertCorpTxTypesAndChargeIds(long newBookId,String newTerminalId) 
 	{
		Connection con = null;    // a Connection variable
		PreparedStatement pstmtGetAcctMasterInfo=null;   // a PreparedStatement variable
        PreparedStatement pstmtSetAcctMasterInfo=null;   // a PreparedStatement variable
		PreparedStatement pstmtSetOpgBalInfo=null;    // a PreparedStatement variable
		PreparedStatement pstmtGetVoucherTypes=null;    // a PreparedStatement variable
		PreparedStatement pstmtSetVoucherTypes=null;    // a PreparedStatement variable
		ResultSet rsAcctMaster=null;    // a ResultSet variable
		ResultSet rsVoucherTypes = null;    // a ResultSet variable

		try
		{
           	con  = operationsImpl.getConnection();

			pstmtGetAcctMasterInfo = con.prepareStatement(
										"SELECT	DISTINCT ACCTCODE, "+
										"		ACCTNAME, "+
										//"		ACCTDESC, "+
										"		BAKCSHPTYIND, "+
										"		CONTACTADRESSID, "+
										"		ACCTID "+
										"FROM "+
										"	FS_AC_ACCTMASTER "+
										"WHERE "+
										"	CORTERIND='C' " );

			pstmtSetAcctMasterInfo = con.prepareStatement(
										"INSERT INTO FS_AC_ACCTMASTER VALUES (?,?,?,?,?,?,?,?,?)");
			pstmtSetOpgBalInfo = con.prepareStatement(
										"INSERT INTO FS_AC_OPENINGBAL VALUES (?,?,?,?)");

			//pstmtGetAcctMasterInfo.setLong(1, oldBookId);
			//pstmtGetAcctMasterInfo.setString(2, oldTerminalId);

			rsAcctMaster = pstmtGetAcctMasterInfo.executeQuery();
			int count = 0;   // an integer to store number of records
			java.sql.Timestamp tsAcctDate = new java.sql.Timestamp( new java.util.Date().getTime());

			while(rsAcctMaster!=null && rsAcctMaster.next())
			 {
				pstmtSetAcctMasterInfo.setString(1, rsAcctMaster.getString(1));		// ACCTCODE
				pstmtSetAcctMasterInfo.setString(2, rsAcctMaster.getString(2));		// ACCTNAME
				pstmtSetAcctMasterInfo.setString(3, newTerminalId);					// TERMINALID
				pstmtSetAcctMasterInfo.setTimestamp(4, tsAcctDate);					// ACCTDATE

				//if(rsAcctMaster.getString(3) != null)
					pstmtSetAcctMasterInfo.setString(5, "");						// ACCTDESC
				//else
					//pstmtSetAcctMasterInfo.setNull(5, Types.VARCHAR);

				if(rsAcctMaster.getString(3) != null)
					pstmtSetAcctMasterInfo.setString(6, rsAcctMaster.getString(3));	// BAKCSHPTYIND
				else
					pstmtSetAcctMasterInfo.setNull(6, Types.VARCHAR);

				if(rsAcctMaster.getLong(4) != 0.0)
					pstmtSetAcctMasterInfo.setLong(7, rsAcctMaster.getLong(4));	// CONTACTADRESSID
				else
					pstmtSetAcctMasterInfo.setNull(7, Types.NUMERIC);

				if(rsAcctMaster.getString(5) != null)
					pstmtSetAcctMasterInfo.setString(8, rsAcctMaster.getString(5));	// ACCTID
				else
					pstmtSetAcctMasterInfo.setNull(8, Types.VARCHAR);

				pstmtSetAcctMasterInfo.setString(9, "C");							// CORTERIND

				pstmtSetAcctMasterInfo.executeUpdate();

				pstmtSetOpgBalInfo.setLong(1, newBookId);
				pstmtSetOpgBalInfo.setString(2, rsAcctMaster.getString(1));
				pstmtSetOpgBalInfo.setDouble(3, 0.0);
				pstmtSetOpgBalInfo.setDouble(4, 0.0);

				pstmtSetOpgBalInfo.executeUpdate();

				count++;
			} // end while
			// if ResultSet is not null

			if(rsAcctMaster!=null)
				rsAcctMaster.close();


			pstmtGetVoucherTypes = con.prepareStatement(
									"SELECT DISTINCT "+		//	FOR MANUAL NUMBERED Voucher type
									"	VOUCHERTYPE, "+
									"	SUPERVOUCHERTYPE, "+
									"	MAX(VOUCHERNUMBER), "+
									"	GENERICTYPE, "+
									"	VOUCHERID "+
									"FROM  "+
									"	FS_AC_VOUCHERTYPESMASTER "+
									"WHERE  "+
									"	CORTERIND = 'C' AND GENERICTYPE IS NOT NULL AND "+
									"	VOUCHERNUMBER IS NULL "+
									"GROUP BY "+
									"	VOUCHERTYPE, SUPERVOUCHERTYPE, GENERICTYPE, VOUCHERID "+

									"UNION "+
									
									"SELECT DISTINCT "+		// FOR AUTO-NUMBERED Voucher type
									"	VOUCHERTYPE, "+
									"	SUPERVOUCHERTYPE, "+
									"	MAX(VOUCHERNUMBER) VN, "+ 
									"	GENERICTYPE, "+ 
									"	VOUCHERID "+ 
									"FROM "+ 
									"	FS_AC_VOUCHERTYPESMASTER "+ 
									"WHERE  "+
									"	CORTERIND = 'C'  AND GENERICTYPE IS NOT NULL AND "+ 
									"	VOUCHERNUMBER IS NOT NULL "+ 
									"	AND VOUCHERTYPE NOT IN (SELECT DISTINCT VOUCHERTYPE FROM FS_AC_VOUCHERTYPESMASTER "+
									"			WHERE VOUCHERNUMBER IS NULL AND CORTERIND='C' AND  "+
									"			GENERICTYPE IS NOT NULL ) "+
									"GROUP BY "+
									"	VOUCHERTYPE, SUPERVOUCHERTYPE, GENERICTYPE, VOUCHERID " );

			pstmtSetVoucherTypes = con.prepareStatement(
										"INSERT INTO FS_AC_VOUCHERTYPESMASTER ( "+
										"	VOUCHERTYPE, "+
										"	SUPERVOUCHERTYPE, "+
										"	VOUCHERNUMBER, "+
										"	BOOKID, "+
										"	VOUCHERID, "+
										"	CORTERIND, "+
										"	GENERICTYPE "+
										") VALUES (?,?,?,?,?,?,?) ");

			rsVoucherTypes = pstmtGetVoucherTypes.executeQuery();
			count = 0;

			String strStartingNumber = "";   // String to store starting number

			while(rsVoucherTypes!=null && rsVoucherTypes.next())
			{

				String	vType	=	rsVoucherTypes.getString(1);
				String	vSuType	=	rsVoucherTypes.getString(2);
				String	vNum	=	rsVoucherTypes.getString(3);
				String	genType	=	rsVoucherTypes.getString(4);
				String	vId		=	rsVoucherTypes.getString(5);
				strStartingNumber = "";

				pstmtSetVoucherTypes.setString(1, vType);					// VOUCHERTYPE

				if(vSuType != null) {
					pstmtSetVoucherTypes.setString(2, vSuType);				// SUPERVOUCHERTYPE
				} else {
					pstmtSetVoucherTypes.setNull(2, Types.VARCHAR);
				}

				/*	In case of a MANUALLY NUMBERED transaction type, the voucher number is put as NULL
					In case of a AUTO NUMBERED transaction type, the maximum number prevailing
					among the current numbers of the transaction type in different terminal books
					is taken and the stating number is generated like follow

					Ex 1:	If Max vou no.  = say 998 the starting no  = 001;
					Ex 2:	If Max vou no.  = say 4567 the starting no = 0001;

					and so on 9the width of the amx number is taken while calulating starting no.)
				*/

				if(vNum == null) {											// VOUCHERNUMBER
					pstmtSetVoucherTypes.setNull(3, Types.VARCHAR);			// Manual
				} else {
					int iWidth = vNum.length();
					for(int i=1; i < iWidth; i++) {
						strStartingNumber += "0";
					}
					strStartingNumber += "1";
					pstmtSetVoucherTypes.setString(3, strStartingNumber);	// Auto
				}

				pstmtSetVoucherTypes.setLong  (4, newBookId);				// BOOKID

				if(vId != null) {
					pstmtSetVoucherTypes.setString(5, vId);					// VOUCHERID
				} else {
					pstmtSetVoucherTypes.setNull(5, Types.VARCHAR);
				}

				pstmtSetVoucherTypes.setString(6, "C");						// CORTERIND

				if(genType != null) {
					pstmtSetVoucherTypes.setString(7, genType);				// GENERICTYPE
				} else {
					pstmtSetVoucherTypes.setNull(7, Types.VARCHAR);
				}

//System.out.println(rsVoucherTypes.getString("VOUCHERTYPE") +"  "+rsVoucherTypes.getString("SUPERVOUCHERTYPE") +"  "+rsVoucherTypes.getString("VOUCHERID") +"  "+rsVoucherTypes.getString("GENERICTYPE"));
				pstmtSetVoucherTypes.executeUpdate();

			} // end while


			if(rsVoucherTypes!=null)
				rsVoucherTypes.close();
		}
        catch( SQLException e )
        {
            e.printStackTrace();
           	throw new javax.ejb.EJBException(e.toString());
        }
	    catch( Exception e )
        {
          e.printStackTrace();
          throw new javax.ejb.EJBException(e.toString());
        }
       	finally
		{
			ConnectionUtil.closeConnection(con,pstmtGetAcctMasterInfo);
			ConnectionUtil.closeConnection(null,pstmtSetAcctMasterInfo);
			ConnectionUtil.closeConnection(null,pstmtSetOpgBalInfo);
			ConnectionUtil.closeConnection(null,pstmtGetVoucherTypes);
			ConnectionUtil.closeConnection(null,pstmtSetVoucherTypes);
		}	  

 	}
     	private void updateLocationId( String terminalId , String locationId[],Connection connection ) 
   	{
		Statement stmt = null;    // a Statement variable
		//ResultSet rs2  = null;    // a ResultSet variable //Commented By RajKumari on 30-10-2008 for Connection Leakages.
		//Connection connection =null;
   		try
   		{
  			//connection = operationsImpl.getConnection();
			String deleteQuery  = "DELETE FROM FS_FR_TERMINALLOCATION WHERE TERMINALID='"+ terminalId +"'" ;     // a String having  a delete statement
	   		stmt = connection.createStatement();
   			int noOfRecs = stmt.executeUpdate(deleteQuery);
	    	setLocationId( terminalId, locationId,connection );
   		}
	    catch( SQLException e )
		{
		   		//Logger.error(FILE_NAME, "Exception in  updateLocationId() method of TerminalRegistrationSessionBean : "+e.toString() );
          logger.error(FILE_NAME+ "Exception in  updateLocationId() method of TerminalRegistrationSessionBean : "+e.toString() );
		   		throw new EJBException( "Exception in  updateLocationId() method of TerminalRegistrationSessionBean : "+e.toString() );
        }
   		catch( Exception e )
   		{
			//Logger.error(FILE_NAME, "Exception in  updateLocationId() method of TerminalRegistrationSessionBean : "+e.toString() );
      logger.error(FILE_NAME+ "Exception in  updateLocationId() method of TerminalRegistrationSessionBean : "+e.toString() );
   			throw new EJBException( "Exception in  updateLocationId() method of TerminalRegistrationSessionBean : "+e.toString() );
   		}   
   		finally
   		{
			ConnectionUtil.closeConnection(connection, stmt,null);//Modified By RajKumari on 30-10-2008 for Connection Leakages.
   		}
    }
  /**
   * 
   * @param terminalId
   * @param locationId
   * @param sMode
   * @param indicator
   * @param cbtLocationId
   * @param loginDetails
   */
	private void updateOperationTerminalId( String terminalId , String locationId[],String sMode,String indicator ,Vector cbtLocationId,ESupplyGlobalParameters loginDetails,Connection connection) 
   	{
		Statement stmt = null;    // a Statement variable
		//ResultSet rs2  = null;    // a ResultSet variable//Commented By RajKumari on 30-10-2008 for Connection Leakages.
   		//Logger.info(FILE_NAME,"Entered updateOperationTerminalId........");
    
   		try
   		{
  			connection = operationsImpl.getConnection();
			String deleteQuery  = "DELETE FROM FS_FR_TERMINAL_REGN WHERE PARENT_TERMINAL_ID='"+ terminalId +"'" ;     // a String having  a delete statement
	   		stmt = connection.createStatement();
   			int noOfRecs = stmt.executeUpdate(deleteQuery);
	    	setOperationTerminalIds( terminalId, locationId,cbtLocationId,loginDetails );
			setGatewayDetail(terminalId,sMode,indicator,connection);
   		}
	    catch( SQLException e )
		{
		   		//Logger.error(FILE_NAME, "Exception in  updateOperationTerminalId() method of TerminalRegistrationSessionBean : "+e.toString() );
          logger.error(FILE_NAME+ "Exception in  updateOperationTerminalId() method of TerminalRegistrationSessionBean : "+e.toString() );
		   		throw new EJBException( "Exception in  updateOperationTerminalId() method of TerminalRegistrationSessionBean : "+e.toString() );
        }
   		catch( Exception e )
   		{
			//Logger.error(FILE_NAME, "Exception in  updateOperationTerminalId() method of TerminalRegistrationSessionBean : "+e.toString() );
      logger.error(FILE_NAME+ "Exception in  updateOperationTerminalId() method of TerminalRegistrationSessionBean : "+e.toString() );
   			throw new EJBException( "Exception in  updateOperationTerminalId() method of TerminalRegistrationSessionBean : "+e.toString() );
   		}
   		finally
   		{
			try
			{
				/*if( rs2!=null )
        {
					rs2.close();
        }*///Commented By RajKumari on 30-10-2008 for Connection Leakages.
				if( stmt!=null )
        {
					stmt.close();
        }
        	if( connection!=null )
        {
					connection.close();
        }
		
   			}
   			catch(Exception e )
   			{
				//Logger.error(FILE_NAME, "Exception in  updateOperationTerminalId() method of TerminalRegistrationSessionBean : "+e.toString() );
        logger.error(FILE_NAME+ "Exception in  updateOperationTerminalId() method of TerminalRegistrationSessionBean : "+e.toString() );
	   			throw new EJBException( "Exception in  updateOperationTerminalId() method of TerminalRegistrationSessionBean : "+e.toString() );
   			}
   		}
   	} 
   private void setGatewayDetailDB( String gatewayid, String gatewaytype, String indicator,Connection connection1 ) 
	 {
	  	 java.sql.PreparedStatement pstmt = null;		// a PreparedStatement variable
		// @@ 'connection' is replaced with 'connection1' by Suneetha for TogehterJ on 12 Jan 05
  		// java.sql.Connection connection1   = null; 		// a Connection variable
  		 
	  	 try
  	  	 {
  	  	  //  connection1 = getConnection();
  	  	 	pstmt = connection1.prepareStatement("UPDATE FS_FR_GATEWAYMASTER SET GATEWAYTYPE=?,INDICATOR=? WHERE GATEWAYID='"+gatewayid+"'");
  	  		pstmt.setString(1,gatewaytype);
  	  		pstmt.setString(2,indicator);
  	  		pstmt.executeUpdate();
  	  	 }
	    catch( SQLException e )
		{
			
			//Logger.error(FILE_NAME, "Exception in   setGatewayDetailDB() method of TerminalRegistrationEntityBean : ",e );
      logger.error(FILE_NAME+ "Exception in   setGatewayDetailDB() method of TerminalRegistrationEntityBean : "+e );
			throw new EJBException(e.toString());
	    }
   		catch( Exception e )
   		{
			
   			//Logger.error(FILE_NAME,"Exception in   setGatewayDetailDB() method of TerminalRegistrationEntityBean : ",e);
        logger.error(FILE_NAME+"Exception in   setGatewayDetailDB() method of TerminalRegistrationEntityBean : "+e);
			throw new EJBException(e.toString());
   		}
        finally
        {
         	try
            {
            	if( pstmt!=null )
              {
	        	  	pstmt.close();
              }
            
        	}
        	catch( Exception e )
        	{
	   			//Logger.error(FILE_NAME,"Exception in  setGatewayDetailDB() method of TerminalRegistrationEntityBean : ",e);
          logger.error(FILE_NAME+"Exception in  setGatewayDetailDB() method of TerminalRegistrationEntityBean : "+e);
        	}
        } 	   	
   }	   	  	   	 			 	  
 private boolean deleteLocationId( String terminalId ,String terminalType)
    {
   		Statement  stmt 	  = null; 		// a Statement variable
        Connection connection   =   null;
   		try
   		{
   			connection = getConnection();
			//Logger.info(FILE_NAME,"Entered deleteLocationId in EntityBean :"+terminalType);   
			
   			String updateQuery1 = "DELETE FROM FS_FR_TERMINALLOCATION WHERE TERMINALID = '"+terminalId+ "'"  ;
			
			if(terminalType!=null && !terminalType.equals("O"))
      {
				updateQuery1 = "DELETE FROM FS_FR_TERMINAL_REGN WHERE PARENT_TERMINAL_ID = '"+terminalId+ "'"  ;
      }
			
			//Logger.info(FILE_NAME,"SHERRY  : "+updateQuery1);
			stmt = connection.createStatement();
   			stmt.executeUpdate(updateQuery1);
     		return true;
   		}
	    catch( SQLException e )
		{
			//Logger.error(FILE_NAME, "Exception in deleteLocationId() method of TerminalRegistrationEntityBean : ",e);
      logger.error(FILE_NAME+ "Exception in deleteLocationId() method of TerminalRegistrationEntityBean : "+e);
			return false;
	    }
   		catch( Exception e )
   		{
   			//Logger.error(FILE_NAME,"Exception in deleteLocationId() method of TerminalRegistrationEntityBean : ",e );
        logger.error(FILE_NAME+"Exception in deleteLocationId() method of TerminalRegistrationEntityBean : "+e );
			return false;
  		}
   		finally
   		{
			try
			{
				if( stmt!=null )
        {
					stmt.close();
        }
				if( connection!=null )
        {
	   				connection.close();
        }
   			}
   			catch( Exception e )
   			{
	   			//Logger.error(FILE_NAME, "Exception in deleteLocationId() method of TerminalRegistrationEntityBean : ",e );
          logger.error(FILE_NAME+"Exception in deleteLocationId() method of TerminalRegistrationEntityBean : "+e );
   			}
   		}
    }
 	public boolean deleteGatewayId( String terminalId ) 
    {
   		Statement  stmt 	  = null;		// a Statement variable
         Connection connection   =   null;
   		try
   		{
   			connection = getConnection();
			String updateQuery1 = "DELETE FROM FS_FR_TERMINALGATEWAY WHERE TERMINALID = '"+terminalId+ "'"  ;
   			String updateQuery2 = "DELETE FROM FS_FR_GATEWAYMASTER WHERE GATEWAYID = '"+terminalId+ "'"  ;   
	   		stmt = connection.createStatement();
			stmt.executeUpdate(updateQuery1); 
   			stmt.executeUpdate(updateQuery2);   
     		return true;
   		}
	    catch( SQLException e )
		{
			//Logger.error(FILE_NAME, "Exception in deleteGatewayId() method of TerminalRegistrationEntityBean : ",e );
      logger.error(FILE_NAME+ "Exception in deleteGatewayId() method of TerminalRegistrationEntityBean : "+e );
			return false;
	    }
   		catch( Exception e )
   		{
   			//Logger.error(FILE_NAME,"Exception in deleteGatewayId() method of TerminalRegistrationEntityBean : ",e );
        logger.error(FILE_NAME+"Exception in deleteGatewayId() method of TerminalRegistrationEntityBean : "+e );
			return false;
   		}
   		finally
   		{
			try
			{
				if( stmt!=null )
        {
					stmt.close();
        }
				if( connection!=null )
        {
	   				connection.close();
        }
   			}
   			catch( Exception e )
   			{
	   			//Logger.error(FILE_NAME, "Exception in deleteGatewayId() method of TerminalRegistrationEntityBean : ",e);
          logger.error(FILE_NAME+ "Exception in deleteGatewayId() method of TerminalRegistrationEntityBean : "+e);
   			}
   		}
    }
 	private void setTerminalGatewayDB( String terminalid, String[] gatewayid,Connection connection1  ) 
     {
		// @@ 'connection' is replaced with 'connection1' by Suneetha for TogehterJ on 12 Jan 05
       // java.sql.Connection connection1    = null;		// a Connection variable
        java.sql.Statement  stmt          = null;		// a Statement variable
        java.sql.PreparedStatement pstmt  = null;		// a PreparedStatement variable
        try
        {
             // connection1 = getConnection();
              stmt       = connection1.createStatement();
              int row    = stmt.executeUpdate("DELETE FROM FS_FR_TERMINALGATEWAY WHERE TERMINALID='"+terminalid+"'");
              pstmt      = connection1.prepareStatement("INSERT INTO FS_FR_TERMINALGATEWAY(TERMINALID,GATEWAYID) VALUES(?,?)");
              // inserting data for all gateway ids
              int gateWayIdLen	=	gatewayid.length;
              for(int i=0;i<gateWayIdLen;i++)
              {
                   pstmt.setString(1,terminalid);
                   pstmt.setString(2,gatewayid[i]);
                   pstmt.executeUpdate();
              }
        }
   		catch( Exception e )
   		{
			
   			//Logger.error(FILE_NAME,"Exception in   setTerminalGatewayDB() method of TerminalRegistrationEntityBean : ",e );
        logger.error(FILE_NAME+"Exception in   setTerminalGatewayDB() method of TerminalRegistrationEntityBean : "+e );
			throw new EJBException(e.toString());
   		}
        finally
        {
         	try
            {
            	if( pstmt!=null )
              {
	        	  	pstmt.close();
              }
            	if( stmt!=null )
              {
	        	  	stmt.close();
              }
            
        	}
        	catch( Exception e )
        	{
	   			//Logger.error(FILE_NAME,"Exception in   setTerminalGatewayDB() method of TerminalRegistrationEntityBean : ",e );
          logger.error(FILE_NAME+"Exception in   setTerminalGatewayDB() method of TerminalRegistrationEntityBean : "+e );
        	}
        }
     } 
       private void setGatewayDetail( String gatewayid, String gatewaytype, String indicator,Connection connection1 ) 
	 {
	  	 java.sql.PreparedStatement pstmt = null;		// a PreparedStatement variable
		// @@ 'connection' is replaced with 'connection1' by Suneetha for TogehterJ on 12 Jan 05
  		// java.sql.Connection connection1   = null; 		// a Connection variable
  		 
	  	 try
  	  	 {
  	  	   // connection1 = getConnection();
  	  	 	pstmt = connection1.prepareStatement("UPDATE FS_FR_GATEWAYMASTER SET GATEWAYTYPE=?,INDICATOR=? WHERE GATEWAYID='"+gatewayid+"'");
  	  		pstmt.setString(1,gatewaytype);
  	  		pstmt.setString(2,indicator);
  	  		pstmt.executeUpdate();
  	  	 }
	    catch( SQLException e )
		{
			
			//Logger.error(FILE_NAME, "Exception in   setGatewayDetailDB() method of TerminalRegistrationEntityBean : ",e );
      logger.error(FILE_NAME+ "Exception in   setGatewayDetailDB() method of TerminalRegistrationEntityBean : "+e );
			throw new EJBException(e.toString());
	    }
   		catch( Exception e )
   		{
			
   			//Logger.error(FILE_NAME,"Exception in   setGatewayDetailDB() method of TerminalRegistrationEntityBean : ",e);
        logger.error(FILE_NAME+"Exception in   setGatewayDetailDB() method of TerminalRegistrationEntityBean : "+e);
			throw new EJBException(e.toString());
   		}
        finally
        {
         	try
            {
            	if( pstmt!=null )
              {
	        	  	pstmt.close();
              }
            
        	}
        	catch( Exception e )
        	{
	   			//Logger.error(FILE_NAME,"Exception in  setGatewayDetailDB() method of TerminalRegistrationEntityBean : ",e);
          logger.error(FILE_NAME+"Exception in  setGatewayDetailDB() method of TerminalRegistrationEntityBean : "+e);
        	}
        } 	   	
   }	   	  	   	 			 	  

    /**
	 
	 * @param housedocId
	 * @param shipmentMode
	 * @param terminalId
	 * @param boundIndex
	 * @return an String 
	 */


	public String getHouseDocId(String houseDocId,String shipmentMode,String terminalId,String boundIndex)
	{
		ResultSet 		rs   			= null;
		Statement		stmt			= null;
		Connection		connection		= null;
		String			sql				= null;
		String			houseDocumentId	= null;
		String			whereClause		= "";
		try
		{
			int shipCode = shipmentMode.equals("Air")?1:4;
			connection = getConnection();
			if(boundIndex.equals("Inbound"))
			{
				whereClause	=" AND DESTTERMINAL = '"+terminalId+"' ";
			}
			if(boundIndex.equals("Outbound"))
			{
				whereClause = " AND ORIGINTERMINAL = '"+terminalId+"' ";
			}
			if(boundIndex.equals("None"))
			{
				whereClause ="";
			}
			 sql	= "SELECT HOUSEDOCID FROM FS_FR_HOUSEDOCHDR WHERE HOUSEDOCID='"+houseDocId+"' AND SHIPMENTMODE="+shipCode+whereClause;
			 stmt	=	connection.createStatement();
			 rs		= 	stmt.executeQuery(sql);
			 if(rs.next())
			{
				houseDocumentId = rs.getString("HOUSEDOCID");
			}
		}
		catch(SQLException sqEx)
        {
			//Logger.error(FILE_NAME, "[getHouseDocId(String houseDocId,String shipmentMode,String terminalId,String boundIndex)] -> "+sqEx.toString());
      logger.error(FILE_NAME+ "[getHouseDocId(String houseDocId,String shipmentMode,String terminalId,String boundIndex)] -> "+sqEx.toString());
            throw new EJBException(sqEx.toString());
        }
        finally
        {
			ConnectionUtil.closeConnection(connection, stmt, rs);
        }
		return houseDocumentId;
	}
    
    /** 
  * This method is	used to	retreive the details about the company.
	* It	returns	HORegistrationJspBean,which	contains the company details.
	* @param	companyId  String  companyId
	* @returns HORegistrationJspBean,which contains the company details.
	* @exception				  
*/
	public HORegistrationJspBean getHORegistrationDetails(String companyId) 
	{
		Connection connection =	null;
		Statement  stmt		  =	null;
		ResultSet  rs		  =	null;
		HORegistrationJspBean company =	null;
		Address				  address =	null;
		try
		{
			connection		= operationsImpl.getConnection();
			String sqlQuery	= "	SELECT * FROM FS_COMPANYINFO, FS_ADDRESS WHERE FS_COMPANYINFO.COMPANYADDRESSID= FS_ADDRESS.ADDRESSID AND FS_COMPANYINFO.COMPANYID = '"+companyId+"'";
			stmt = connection.createStatement();
			rs	 = stmt.executeQuery( sqlQuery );
			if(	rs.next())
			{
				String companyMasterId	= rs.getString(	"COMPANYID"	);
				String companyName		= rs. getString( "CompanyName" );
				int	   companyAddressId	= rs.getInt( "COMPANYADDRESSID"	);
//				String dateFormat		= rs. getString( "DATEFORMAT" );
//				String timeZone			= rs. getString( "TIMEZONE"	);
				String hCurrency		= rs.getString(	"HCURRENCY"	);
				String dayLightSavings	= rs. getString( "DAYLIGHTSAVING" );
				String iataCode			= rs. getString( "IATACODE" );
				String contactName		= rs. getString( "CONTACTNAME" );
 //@@ Added By Ravi Kumar to Resolve the Issues SPETI-4168 & 4169 on 01-05-2005 
				String contactLastName		= rs. getString( "CONTACT_LASTNAME" );
				String companyEIN		= rs. getString( "COMPANY_EIN" );
//@@01-05-2005
				String designation		= rs. getString( "DESIGNATION" );
				String opEmailId		= rs. getString( "OPE_EMAILID" );
				company					= new HORegistrationJspBean();
				company.setCompanyId(companyMasterId);
				company.setCompanyName(companyName);
				company.setAddressId(companyAddressId);
//				company.setDateFormat(dateFormat);
//				company.setTimeZone(timeZone);
				company.setHCurrency(hCurrency);
				company.setDayLightSavings(dayLightSavings);
				company.setIataCode(iataCode);
				company.setContactName(contactName);
 //@@ Added By Ravi Kumar to Resolve the Issues SPETI-4168 & 4169 on 01-05-2005 
				company.setContactLastName(contactLastName);
				company.setCompanyEIN(companyEIN);
//@@ 01-05-2005
				company.setDesignation(designation);
				company.setOpEmailId(opEmailId);
				AddressDAO addressDAO= new AddressDAO();
				address	= addressDAO.load(companyAddressId);
				company.setAddress(address);
			}
			else
      {
				return null;
      }
		}
		catch( SQLException	sqle)
		{
			
			//Logger.error(FILE_NAME,"SQLException in	getHORegistrationDetails of	ETransHOSuperUserSetupSessionBean"+sqle.toString());
      logger.error(FILE_NAME+"SQLException in	getHORegistrationDetails of	ETransHOSuperUserSetupSessionBean"+sqle.toString());
			throw new EJBException(sqle.toString());
		}
		finally
		{
			try
			{
				if( rs	!= null	)
					{rs.close();}
				if( stmt != null )
					{stmt.close();}
				if( connection	!= null	)
					connection.close();
			}
			catch(SQLException	sqle)
			{
				//Logger.error(FILE_NAME,"finally block	SQLException in	getHORegistrationDetails of	ETransHOSuperUserSetupSessionBean"+sqle.toString());
        logger.error(FILE_NAME+"finally block	SQLException in	getHORegistrationDetails of	ETransHOSuperUserSetupSessionBean"+sqle.toString());
			}
		}//end of	finally.
		return company;
	} // End ofgetHORegistrationDetails
  private boolean isIdExist(String industryId,ESupplyGlobalParameters loginbean)
  {
    IndustryRegDOB   industryRegDOB = null;
    Connection       connection     = null;
    PreparedStatement pstmt         = null;
    ResultSet         rs            = null;
    String    selectQry = "SELECT COUNT(*) FROM QMS_INDUSTRY_REG WHERE INDUSTRY_ID=?";
    boolean   isExist               = false;
    try
    {
      connection  = operationsImpl.getConnection();
      pstmt = connection.prepareStatement(selectQry);
      pstmt.setString(1,industryId);
      rs    = pstmt.executeQuery();
      if(rs.next())
      {
        if(rs.getInt("COUNT(*)")>0)
        {
          isExist = true;
        }
      }
      
    }catch(SQLException se)
    {
        //Logger.error(FILE_NAME,"Exception in ------->isIdsExist()",se.toString());
        logger.error(FILE_NAME+"Exception in ------->isIdsExist()"+se.toString());
        throw new EJBException();      
    }catch(Exception e)
    {
        //Logger.error(FILE_NAME,"Exception in ------->isIdsExist()",e.toString());
        logger.error(FILE_NAME+"Exception in ------->isIdsExist()"+e.toString());
        throw new EJBException();      
    }finally
    {
      try
      {
        if(rs!=null)
        { rs.close();}
        if(pstmt!=null)
        { pstmt.close();}
        if(connection!=null)
        { connection.close();}
      }catch(SQLException sqx)
      {
       //Logger.error(FILE_NAME,"Error while closing the connection --- isIdExist()"+sqx.toString());
       logger.error(FILE_NAME+"Error while closing the connection --- isIdExist()"+sqx.toString());
       throw new EJBException();       
      }
    }
    return isExist;
  }
/**
   * 
   * @param industryList
   * @param loginbean
   * @return String
   * 
   */ 
  public Hashtable insertIndustryDetails(ArrayList industryList,ESupplyGlobalParameters loginbean)
  {
    String msg  =	"Records successfully inserted : ";
    InitialContext   initialContext = null;
    IndustryRegEntityLocal    local = null;
    IndustryRegEntityLocalHome home = null;
    IndustryRegDOB   industryRegDOB = null;
    boolean   success               = false;
    boolean   isExist               = false;
    ArrayList insetedList           = new ArrayList();
    ArrayList invalidList           = new ArrayList();
    Hashtable ht                    = new Hashtable();
    try
    {
      initialContext   = new InitialContext();
      home             = (IndustryRegEntityLocalHome)LookUpBean.getEJBLocalHome("java:comp/env/IndustryRegEntityBean");
      if(industryList!=null && industryList.size()>0)
      {
    	  int indusListSize	=	industryList.size();    	  
        for(int i=0;i<indusListSize;i++)
        {
          industryRegDOB  = (IndustryRegDOB)industryList.get(i);
          isExist         = isIdExist(industryRegDOB.getIndustry(),loginbean);
          if(!isExist)
          {
            insetedList.add(industryRegDOB.getIndustry());
            local           = (IndustryRegEntityLocal)home.create(industryRegDOB);
            success         = true;
          }
          else
          {
            invalidList.add(industryRegDOB.getIndustry());
          }
        }
      }
      ht.put("insetedList",insetedList);
      ht.put("invalidList",invalidList);
    }catch(NamingException nex)
    {
        msg  =	"Exception While inserting records : ";
        success = false;
        //Logger.error(FILE_NAME,"insertIndustryDetails()",nex.toString());
        logger.error(FILE_NAME+"insertIndustryDetails()"+nex.toString());
        throw new EJBException();
    }catch(Exception e)
    {
        msg  =	"Exception While inserting records : ";
        success = false;
        //Logger.error(FILE_NAME,"insertIndustryDetails()",e.toString());
        logger.error(FILE_NAME+"insertIndustryDetails()"+e.toString());
        throw new EJBException();      
    }
    return ht; 
  }
  
  public ArrayList getAllIndustryDetails(String searchStr,String operation,ESupplyGlobalParameters loginbean)
  {
    Connection connection         = null;
    //Statement         stmt     = null;//Commented By RajKumari on 30-10-2008 for Connection Leakages.
    ResultSet         rs       = null;
    ArrayList    indestryList  =  new ArrayList();
    IndustryRegDOB industryRegDOB = null;
    String industryId           = null;
    String industryDesc         = null;
    String invalidate           = null;
    String invalidateStr        = "";
    CallableStatement cstmt     = null;
    /*if(operation!=null && operation.equals("Invalidate"))
    {
      invalidateStr = "INVALIDATE LIKE '%'";
    }else
    {
      invalidateStr = "INVALIDATE LIKE 'F'";
    }
    */
    //String selectQry  = "SELECT INDUSTRY_ID,DESCRIPTION,INVALIDATE "
     //                   +" FROM "
       //                 +" QMS_INDUSTRY_REG "
      //                  +" WHERE "
       //                 +" INDUSTRY_ID LIKE '%"+searchStr+"' "
       //                 +" AND "+invalidateStr;
    try
    {
      connection  = operationsImpl.getConnection();
      //stmt        = connection.createStatement();
      //rs          = stmt.executeQuery(selectQry);
		  cstmt        = connection.prepareCall("{ ?= call QMS_SETUPS.GET_INDUSTRYREGISTRATION(?,?,?,?,?)}");
          cstmt.registerOutParameter(1,OracleTypes.CURSOR);
          cstmt.setString(2,loginbean.getTerminalId());
          cstmt.setString(3,(searchStr+"%"));
          cstmt.setString(4,operation);          
          cstmt.setString(5,operation);
          cstmt.setString(6,operation);
          cstmt.execute();
          rs  =  (ResultSet)cstmt.getObject(1);
      while(rs.next())
      {
        industryRegDOB  = new IndustryRegDOB();
        industryId      = rs.getString("INDUSTRY_ID");
        industryDesc    = rs.getString("DESCRIPTION");
        invalidate      = rs.getString("INVALIDATE");
        industryRegDOB.setIndustry((industryId!=null)?industryId:"");
        industryRegDOB.setDescription((industryDesc!=null)?industryDesc:"");
        industryRegDOB.setInvalidate((invalidate!=null)?invalidate:"");
        indestryList.add(industryRegDOB);
      }
      
    }catch(SQLException sqex)
    {sqex.printStackTrace();
      //Logger.error(FILE_NAME,"Error while retrieving the details --- getAllIndustryDetails()"+sqex.toString());
      logger.error(FILE_NAME+"Error while retrieving the details --- getAllIndustryDetails()"+sqex.toString());
      throw new EJBException();
    }
    catch(Exception e)
    {e.printStackTrace();
      //Logger.error(FILE_NAME,"Error while retrieving the details --- getAllIndustryDetails()"+e.toString());
      logger.error(FILE_NAME+"Error while retrieving the details --- getAllIndustryDetails()"+e.toString());
      throw new EJBException();     
    }finally
    {
      try
      {
        if(rs!=null)
        { rs.close();}
        if(cstmt!=null)
        { cstmt.close();}
        if(connection!=null)
        { connection.close();}
      }catch(SQLException sqx)
      {
       //Logger.error(FILE_NAME,"Error while closing the connection --- getAllIndustryDetails()"+sqx.toString());
       logger.error(FILE_NAME+"Error while closing the connection --- getAllIndustryDetails()"+sqx.toString());
      throw new EJBException();       
      }
    }
    
    return indestryList;
  }
  
  public IndustryRegDOB getIndustryDetails(String industryId,ESupplyGlobalParameters loginbean)throws ObjectNotFoundException
  {
    InitialContext   initialContext = null;
    IndustryRegEntityLocal    local = null;
    IndustryRegEntityLocalHome home = null;
    IndustryRegEntityPK       pkObj = new IndustryRegEntityPK();
    IndustryRegDOB   industryRegDOB = null;
    
    try
    {
      pkObj.industry  = industryId;
      initialContext  = new InitialContext();
      home            = (IndustryRegEntityLocalHome)LookUpBean.getEJBLocalHome("java:comp/env/IndustryRegEntityBean");
      local           = (IndustryRegEntityLocal)home.findByPrimaryKey(pkObj);
      industryRegDOB  = local.getIndustryRegDOB();
      
    }catch(ObjectNotFoundException e)
    {
        //Logger.error(FILE_NAME,"getIndustryDetails()",e.toString());
        logger.error(FILE_NAME+"getIndustryDetails()"+e.toString());
        throw new ObjectNotFoundException();     
    }
    catch(NamingException nex)
    {
        //Logger.error(FILE_NAME,"getIndustryDetails()",nex.toString());
        logger.error(FILE_NAME+"getIndustryDetails()"+nex.toString());
        throw new EJBException();
    }catch(javax.ejb.FinderException nex)
    {
        //Logger.error(FILE_NAME,"getIndustryDetails()",nex.toString());
        logger.error(FILE_NAME+"getIndustryDetails()"+nex.toString());
        throw new EJBException();
    }
    catch(Exception e)
    {
      //Logger.error(FILE_NAME,"Error while retrieving the details --- getIndustryDetails()"+e.toString());
      logger.error(FILE_NAME+"Error while retrieving the details --- getIndustryDetails()"+e.toString());
      throw new EJBException();      
    }
    
    return industryRegDOB;
  }
  
  public boolean updateIndustryDetails(IndustryRegDOB industryRegDOB,ESupplyGlobalParameters loginbean)
  {
    InitialContext   initialContext = null;
    IndustryRegEntityLocal    local = null;
    IndustryRegEntityLocalHome home = null;
    IndustryRegEntityPK       pkObj = new IndustryRegEntityPK();
    boolean success                 = true;
    try
    {
      pkObj.industry  = industryRegDOB.getIndustry();
      home            = (IndustryRegEntityLocalHome)LookUpBean.getEJBLocalHome("java:comp/env/IndustryRegEntityBean");
      local           = (IndustryRegEntityLocal)home.findByPrimaryKey(pkObj);
      local.setIndustryRegDOB(industryRegDOB);
      
    }catch(NamingException nex)
    {
        success = false;
        //Logger.error(FILE_NAME,"updateIndustryDetails()",nex.toString());
        logger.error(FILE_NAME+"updateIndustryDetails()"+nex.toString());
        throw new EJBException();
    }catch(javax.ejb.FinderException nex)
    {
        success = false;
        //Logger.error(FILE_NAME,"updateIndustryDetails()",nex.toString());
        logger.error(FILE_NAME+"updateIndustryDetails()"+nex.toString());
        throw new EJBException();
    }
    catch(Exception e)
    {
      success = false;

      //Logger.error(FILE_NAME,"Error while updatating the details --- updateIndustryDetails()"+e.toString());
      logger.error(FILE_NAME+"Error while updatating the details --- updateIndustryDetails()"+e.toString());

      throw new EJBException();      
    }
    return success;
  }
  
  public boolean deleteIndustryDetails(IndustryRegDOB industryRegDOB,ESupplyGlobalParameters loginbean)
  {
    IndustryRegEntityLocal    local = null;
    IndustryRegEntityLocalHome home = null;
    IndustryRegEntityPK       pkObj = new IndustryRegEntityPK();
    boolean success                 = true;
    
    try
    {
      pkObj.industry  = industryRegDOB.getIndustry();
      home            = (IndustryRegEntityLocalHome)LookUpBean.getEJBLocalHome("java:comp/env/IndustryRegEntityBean");
      local           = (IndustryRegEntityLocal)home.findByPrimaryKey(pkObj);
      local.remove();
    }catch(NamingException nex)
    {
        success = false;
        //Logger.error(FILE_NAME,"updateIndustryDetails()",nex.toString());
        logger.error(FILE_NAME+"updateIndustryDetails()"+nex.toString());
        throw new EJBException();
    }catch(javax.ejb.FinderException nex)
    {
        success = false;
        //Logger.error(FILE_NAME,"updateIndustryDetails()",nex.toString());
        logger.error(FILE_NAME+"updateIndustryDetails()"+nex.toString());
        throw new EJBException();
    }
    catch(Exception e)
    {
      success = false;
      //Logger.error(FILE_NAME,"Error while updatating the details --- updateIndustryDetails()"+e.toString());
      logger.error(FILE_NAME+"Error while updatating the details --- updateIndustryDetails()"+e.toString());
      throw new EJBException();      
    }
    return success;
  }
  
  public boolean invalidateIndustryDetails(ArrayList industryList,ESupplyGlobalParameters loginbean)
  {
    InitialContext   initialContext = null;
    IndustryRegEntityLocal    local = null;
    IndustryRegEntityLocalHome home = null;
    IndustryRegEntityPK       pkObj = new IndustryRegEntityPK();
    boolean success                 = true;
    IndustryRegDOB industryRegDOB   = null; 
    IndustryRegDAO industryRegDAO   = new IndustryRegDAO();
    
    try
    {
      if(industryList!=null && industryList.size()>0)
      {
/*        industryRegDOB  = (IndustryRegDOB)industryList.get(0);
        pkObj.industry  = industryRegDOB.getIndustry();
        initialContext  = new InitialContext();
        home            = (IndustryRegEntityLocalHome)LookUpBean.getEJBLocalHome("java:comp/env/IndustryRegEntityBean");
        local           = (IndustryRegEntityLocal)home.findByPrimaryKey(pkObj);
        local.invalidateIndustryDtls(industryList);
        */
        industryRegDAO.invalidateIndustryId(industryList,loginbean);
        
      }      
    }catch(Exception e)
    {
      success = false;
      //Logger.error(FILE_NAME,"Error while updatating the details --- invalidateIndustryDetails()"+e.toString());
      logger.error(FILE_NAME+"Error while updatating the details --- invalidateIndustryDetails()"+e.toString());
      throw new EJBException();      
    }
    return success;
    
  }
 private boolean isValidMarginLimitsData(MarginLimitMasterDOB marginLimitMasterDOB,ESupplyGlobalParameters loginbean)
  {
    PreparedStatement pstmt     = null;
    ResultSet         rs        = null;
    Connection  connection      = null;
    String      marginExistQry  = "";
    boolean     isValid         = false;
    String      levelQry        = null;
    String      sLevelQry       = null;
    String      shipmentMode    = null;
    try
    {
        if("1".equals(marginLimitMasterDOB.getShipmentMode()))
        {
          shipmentMode  ="1,3,5,7";
        }
        else if("2".equals(marginLimitMasterDOB.getShipmentMode()))
        {
          shipmentMode  ="2,3,6,7";
        }
        else if("4".equals(marginLimitMasterDOB.getShipmentMode()))
        {
          shipmentMode  ="4,5,6,7";
        }
        levelQry  = "SELECT LEVEL_NO FROM QMS_DESIGNATION WHERE LEVEL_NO=? AND INVALIDATE ='F'";
        sLevelQry = "SELECT SERVICELEVELID FROM FS_FR_SERVICELEVELMASTER WHERE SERVICELEVELID=? AND SHIPMENTMODE IN ("+shipmentMode+")";
        //String        sLevelQry = "SELECT SERVICELEVELID FROM FS_FR_SERVICELEVELMASTER WHERE SERVICELEVELID=? AND INVALIDATE ='F'";
        if(marginLimitMasterDOB.getChargeType().equals("FREIGHT"))
        {
            marginExistQry = "SELECT MARGIN_ID FROM QMS_MARGIN_LIMIT_DTL WHERE MARGIN_ID=? AND LEVELNO=? AND CHARGETYPE=? AND SERVICE_LEVEL=? ";
        }
        else
        {
            marginExistQry = "SELECT MARGIN_ID FROM QMS_MARGIN_LIMIT_DTL WHERE MARGIN_ID=? AND LEVELNO=? AND CHARGETYPE=? ";
        }  
        connection  = operationsImpl.getConnection();
        if(marginLimitMasterDOB.getChargeType().equals("FREIGHT"))
        {
          pstmt = connection.prepareStatement(sLevelQry);
          pstmt.setString(1,marginLimitMasterDOB.getServiceLevel());
          
          rs  = pstmt.executeQuery();
          if(rs.next())
          {
            isValid = true;
          }else
          {
            isValid = false;
            return isValid;
          }
        }
        if(rs!=null)
          { rs.close();}
        if(pstmt!=null)
          { pstmt.close();}     
        pstmt = connection.prepareStatement(levelQry);
        pstmt.setString(1,marginLimitMasterDOB.getLevelId());
      
        rs  = pstmt.executeQuery();
        if(rs.next())
        {
          isValid = true;
        }else
        {
          isValid = false;
          return isValid;
        }
        if(rs!=null)
          { rs.close();}
        if(pstmt!=null)
          { pstmt.close();} 
        pstmt = connection.prepareStatement(marginExistQry);
        pstmt.setString(1,marginLimitMasterDOB.getMarginId());
        pstmt.setString(2,marginLimitMasterDOB.getLevelId());
        pstmt.setString(3,marginLimitMasterDOB.getChargeType());
        if(marginLimitMasterDOB.getChargeType().equals("FREIGHT"))
        pstmt.setString(4,marginLimitMasterDOB.getServiceLevel());
      
        rs = pstmt.executeQuery();
        if(rs.next())
        {
          isValid = false;
          return isValid;
        }else
        {
          isValid = true;       
        }
        if(rs!=null)
          { rs.close();}
        if(pstmt!=null)
          { pstmt.close();}
        
    }catch(SQLException e)
    {
      isValid = false;
      //Logger.error(FILE_NAME,"Error while updatating the details --- isValidMarginLimitsData()"+e.toString());
      logger.error(FILE_NAME+"Error while updatating the details --- isValidMarginLimitsData()"+e.toString());
      throw new EJBException();      
    }catch(Exception e)
    {
      isValid = false;
      //Logger.error(FILE_NAME,"Error while updatating the details --- isValidMarginLimitsData()"+e.toString());
      logger.error(FILE_NAME+"Error while updatating the details --- isValidMarginLimitsData()"+e.toString());
      throw new EJBException();     
    }finally
    {
      try
      {
        if(rs!=null)
          { rs.close();}
        if(pstmt!=null)
          { pstmt.close();}
        if(connection!=null)
          { connection.close();}
      }catch(SQLException e)
      {
        isValid = false;
        //Logger.error(FILE_NAME,"Error while updatating the details --- isValidMarginLimitsData()"+e.toString());
        logger.error(FILE_NAME+"Error while updatating the details --- isValidMarginLimitsData()"+e.toString());
        throw new EJBException();      
      }
    }
    return isValid;
  }
  /**
   * 
   * @throws javax.ejb.EJBException
   * @return 
   * @param loginbean
   * @param dataList
   */
  public ArrayList insertMarginListDtls(ArrayList dataList,ESupplyGlobalParameters loginbean)throws EJBException
  {
    ArrayList insertedList    = new ArrayList();
    ArrayList invalidList     = new ArrayList();
    ArrayList returnList      = new ArrayList();
    MarginLimitMasterBeanLocalHome  home  = null;
    MarginLimitMasterBeanLocal      local = null;
    MarginLimitMasterDOB marginLimitMasterDOB =null;
    try
    {
    	int dataListSize	=	dataList.size();
      for(int i=0;i<dataListSize;i++)
      {
        marginLimitMasterDOB  = (MarginLimitMasterDOB)dataList.get(i);
        //Logger.info("","in Session Bean "+marginLimitMasterDOB.getChargeType());
        if(isValidMarginLimitsData(marginLimitMasterDOB,loginbean))
        {
          insertedList.add(marginLimitMasterDOB);
        }else
        {
          invalidList.add(marginLimitMasterDOB);
        }
      }
      returnList.add(insertedList);
      returnList.add(invalidList);
      if(insertedList!=null && insertedList.size()>0)
      {
        home  = (MarginLimitMasterBeanLocalHome)LookUpBean.getEJBLocalHome("java:comp/env/MarginLimitMasterBean");
        local = (MarginLimitMasterBeanLocal)home.create(insertedList);
      }     
    }catch(NamingException nex)
    {
        //Logger.error(FILE_NAME,"insertMarginListDtls()",nex.toString());
        logger.error(FILE_NAME+"insertMarginListDtls()"+nex.toString());
        throw new EJBException();
    }catch(Exception e)
    {
        //Logger.error(FILE_NAME,"insertMarginListDtls()",e.toString());
        logger.error(FILE_NAME+"insertMarginListDtls()"+e.toString());
        throw new EJBException();      
    }
      return returnList;
  }
  
  /**
   * 
   * @throws javax.ejb.ObjectNotFoundException
   * @throws javax.ejb.EJBException
   * @return 
   * @param loginbean
   * @param sLevelId
   * @param levelId
   * @param shipmentType
   */
  public MarginLimitMasterDOB getMarginLimitDetails(String shipmentType,String levelId,String sLevelId,ESupplyGlobalParameters loginbean,String chargeType,String operation)throws EJBException,ObjectNotFoundException
  {
    MarginLimitMasterDAO marginLimitMasterDAO  =new MarginLimitMasterDAO();
    MarginLimitMasterDOB marginLimitMasterDOB  =new MarginLimitMasterDOB();
    try{
//@@ Commented & Added by subrahmanyam for the pbn id: 203354 on 22-APR-010    	
    //marginLimitMasterDOB=marginLimitMasterDAO.getMarginDetails(shipmentType,levelId,sLevelId,chargeType);
    	marginLimitMasterDOB=marginLimitMasterDAO.getMarginDetails(shipmentType,levelId,sLevelId,chargeType,loginbean,operation);
      
    }catch(ObjectNotFoundException e)
    {
        //Logger.error(FILE_NAME,"getMarginLimitDetails()",e.toString());
        logger.error(FILE_NAME+"getMarginLimitDetails()"+e.toString());
        throw new ObjectNotFoundException();      
    }catch(FinderException e)
    {
        //Logger.error(FILE_NAME,"getMarginLimitDetails()",e.toString());
        logger.error(FILE_NAME+"getMarginLimitDetails()"+e.toString());
        throw new EJBException();      
    }catch(Exception e)
    {
        //Logger.error(FILE_NAME,"getMarginLimitDetails()",e.toString());
        logger.error(FILE_NAME+"getMarginLimitDetails()"+e.toString());
        throw new EJBException();      
    }
    return marginLimitMasterDOB;
  }
  
  /**
   * 
   * @throws javax.ejb.ObjectNotFoundException
   * @throws javax.ejb.EJBException
   * @return 
   * @param loginbean
   * @param marginLimitMasterDOB
   */
  public boolean updateMarginLimitDetails(MarginLimitMasterDOB marginLimitMasterDOB,ESupplyGlobalParameters loginbean)throws EJBException,ObjectNotFoundException
  {
    MarginLimitMasterDAO marginLimitMasterDAO  =new MarginLimitMasterDAO();
    boolean   success                     = false;
    try
    {
      marginLimitMasterDAO.updateMarginLimitDetail(marginLimitMasterDOB);
      success = true;
    }catch(Exception e)
    {
        success = false;    
        //Logger.error(FILE_NAME,"updateMarginLimitDetails()",e.toString());
        logger.error(FILE_NAME+"updateMarginLimitDetails()"+e.toString());
        throw new EJBException();     
    }
    return success;
  }
  /**
   * 
   * @throws javax.ejb.ObjectNotFoundException
   * @throws javax.ejb.EJBException
   * @return 
   * @param loginbean
   * @param marginLimitMasterDOB
   */
  public boolean deleteMariginLimitMasterDtls(MarginLimitMasterDOB marginLimitMasterDOB)throws EJBException,ObjectNotFoundException
  {
    MarginLimitMasterDAO marginLimitMasterDAO  =new MarginLimitMasterDAO();
    boolean   success                   = false;
    String	  freightRatesQry			=	" SELECT COUNT(*)  FROM QMS_QUOTE_RATES  WHERE QUOTE_ID IN (SELECT ID"+
    										" FROM QMS_QUOTE_MASTER WHERE SALES_PERSON IN(SELECT UM.EMPID"+
    										" FROM FS_USERMASTER UM, QMS_DESIGNATION QD WHERE UM.DESIGNATION_ID = QD.DESIGNATION_ID"+
    										" AND QD.LEVEL_NO = ?) AND ((ACTIVE_FLAG = 'A' AND QUOTE_STATUS NOT IN ('ACC', 'NAC')) OR"+
    										" (ACTIVE_FLAG IN ('A', 'I') AND QUOTE_STATUS IN ('ACC', 'NAC')))) AND SELL_BUY_FLAG IN ('BR', 'RSR')"+
    										" AND LINE_NO = 0 AND (BUYRATE_ID, RATE_LANE_NO) IN (SELECT  BUYRATEID, LANE_NO FROM QMS_BUYRATES_DTL "+
    									    " WHERE ACTIVEINACTIVE IS NULL AND LINE_NO = 0 AND SERVICE_LEVEL = ?)";
    										 
    String	  freightSpotRatesQry		=	" SELECT COUNT(*)  FROM QMS_QUOTE_RATES  WHERE QUOTE_ID IN (SELECT ID"+
    	   									" FROM QMS_QUOTE_MASTER WHERE SALES_PERSON IN(SELECT UM.EMPID"+
    	   									" FROM FS_USERMASTER UM, QMS_DESIGNATION QD WHERE UM.DESIGNATION_ID = QD.DESIGNATION_ID"+
    	   									" AND QD.LEVEL_NO = ?) AND ((ACTIVE_FLAG = 'A' AND QUOTE_STATUS NOT IN ('ACC', 'NAC')) OR"+
    	   									" (ACTIVE_FLAG IN ('A', 'I') AND QUOTE_STATUS IN ('ACC', 'NAC')))) AND SELL_BUY_FLAG IN ('SBR')"+
    	   									" AND LINE_NO = 0 AND (QUOTE_ID) IN (SELECT DISTINCT SR.QUOTE_ID FROM QMS_QUOTE_SPOTRATES SR "+
    	   									" WHERE SR.QUOTE_ID = QUOTE_ID AND SR.SERVICELEVEL = ? AND SR.LINE_NO = 0)";

    String	  charagesQry				=	" SELECT COUNT(*)  FROM QMS_QUOTE_RATES  WHERE QUOTE_ID IN (SELECT ID"+
    										" FROM QMS_QUOTE_MASTER WHERE SALES_PERSON IN(SELECT UM.EMPID"+
    										" FROM FS_USERMASTER UM, QMS_DESIGNATION QD WHERE UM.DESIGNATION_ID = QD.DESIGNATION_ID"+
    										" AND QD.LEVEL_NO = ?) AND ((ACTIVE_FLAG = 'A' AND QUOTE_STATUS NOT IN ('ACC', 'NAC')) OR"+
    										" (ACTIVE_FLAG IN ('A', 'I') AND QUOTE_STATUS IN ('ACC', 'NAC')))) AND SELL_BUY_FLAG IN ('B','S')";

    String	  cartagesQry				=	" SELECT COUNT(*)  FROM QMS_QUOTE_RATES  WHERE QUOTE_ID IN (SELECT ID"+
    										" FROM QMS_QUOTE_MASTER WHERE SALES_PERSON IN(SELECT UM.EMPID"+
    										" FROM FS_USERMASTER UM, QMS_DESIGNATION QD WHERE UM.DESIGNATION_ID = QD.DESIGNATION_ID"+
    										" AND QD.LEVEL_NO = ?) AND ((ACTIVE_FLAG = 'A' AND QUOTE_STATUS NOT IN ('ACC', 'NAC')) OR"+
    										" (ACTIVE_FLAG IN ('A', 'I') AND QUOTE_STATUS IN ('ACC', 'NAC')))) AND SELL_BUY_FLAG IN ('B','S')";
    int 	count						=	0;
    PreparedStatement pstmt     = null;
    ResultSet         rs        = null;
    Connection  connection      = null;
    

    
    try
    {
    	//@@Commented by subrahmanyam for the wpbn id:198576 on 25/Feb/10
    	/*
    	 * *
 				marginLimitMasterDAO.deleteMarginLimitDetail(marginLimitMasterDOB);
      			success = true;

    	 */
    	//@@Added by subrahmanyam for the wpbn id:198576 on 25/Feb/10    	
    	connection = operationsImpl.getConnection();
    	
    	if("FREIGHT".equalsIgnoreCase(marginLimitMasterDOB.getChargeType()))
    	{
    		pstmt 	= connection.prepareStatement(freightRatesQry);
    		pstmt.setString(1,marginLimitMasterDOB.getLevelId() );
    		pstmt.setString(2, marginLimitMasterDOB.getServiceLevel());
    		rs	= pstmt.executeQuery();
    		if(rs.next())
    			count = rs.getInt(1);
    		if(count==0)
    		{
    	   		pstmt 	= connection.prepareStatement(freightSpotRatesQry);
        		pstmt.setString(1,marginLimitMasterDOB.getLevelId() );
        		pstmt.setString(2, marginLimitMasterDOB.getServiceLevel());
        		rs	= pstmt.executeQuery();
        		if(rs.next())
        			count = rs.getInt(1);

    		}
    	}
    	else if("CARTAGES".equalsIgnoreCase(marginLimitMasterDOB.getChargeType()))
    	{
   	   		pstmt 	= connection.prepareStatement(cartagesQry);
    		pstmt.setString(1,marginLimitMasterDOB.getLevelId() );
    		rs	= pstmt.executeQuery();
    		if(rs.next())
    			count = rs.getInt(1);

    	}
    	else
    	{
   	   		pstmt 	= connection.prepareStatement(charagesQry);
    		pstmt.setString(1,marginLimitMasterDOB.getLevelId() );
    		rs	= pstmt.executeQuery();
    		if(rs.next())
    			count = rs.getInt(1);

    	}
    	if(count==0){
      marginLimitMasterDAO.deleteMarginLimitDetail(marginLimitMasterDOB);
      success = true;
    	}
    	else
    		success = false;
//@@Ended by subrahmanyam for the wpbn id:198576 on 25/Feb/10
    }catch(Exception e)
    {
        success = false;    
        //Logger.error(FILE_NAME,"deleteMariginLimitMasterDtls()",e.toString());
        logger.error(FILE_NAME+"deleteMariginLimitMasterDtls()"+e.toString());
        throw new EJBException();     
    }
    finally
    {
    	try{
    		if(connection !=null)
    			connection.close();
    		if(pstmt !=null)
    			pstmt.close();
    		if(rs !=null)
    			rs.close();
    	}catch(java.sql.SQLException e)
    	{
    		logger.error("Error while closing the connection");
    	}
    	
    }
    return success;
  }

  /**
   * 
   * @return 
   * @param loginbean
   * @param marginList
   */
  public boolean invalidateMarginLimitDetails(ArrayList marginList,ESupplyGlobalParameters loginbean)
  {
    InitialContext   initialContext = null;
    MarginLimitMasterBeanLocalHome  home  = null;
    MarginLimitMasterBeanLocal      local = null;
    MarginLimitMasterBeanPK         pkObj = new MarginLimitMasterBeanPK();
    boolean success                 = true;
    MarginLimitMasterDAO marginLimitMasterDAO   = new MarginLimitMasterDAO();
    
    try
    {
      if(marginList!=null && marginList.size()>0)
      {
/*        industryRegDOB  = (IndustryRegDOB)industryList.get(0);
        pkObj.industry  = industryRegDOB.getIndustry();
        initialContext  = new InitialContext();
        home            = (IndustryRegEntityLocalHome)LookUpBean.getEJBLocalHome("java:comp/env/IndustryRegEntityBean");
        local           = (IndustryRegEntityLocal)home.findByPrimaryKey(pkObj);
        local.invalidateIndustryDtls(industryList);
        */
        marginLimitMasterDAO.invalidateMarginDtl(marginList,loginbean);
        
      }      
    }catch(Exception e)
    {
      success = false;
      //Logger.error(FILE_NAME,"Error while updatating the details --- invalidateIndustryDetails()"+e.toString());
      logger.error(FILE_NAME+"Error while updatating the details --- invalidateIndustryDetails()"+e.toString());
      throw new EJBException();      
    }
    return success;
    
  }  
  /**
   * 
   * @return 
   * @param loginbean
   * @param levelId
   * @param consoletype
   * @param shipmentMode
   * @param searchStr
   * @param operation
   */
  public ArrayList getServiceLevelIds(String operation,String searchStr,String shipmentMode,String consoletype,String levelId,ESupplyGlobalParameters loginbean)
  {
    PreparedStatement pstmt = null;
    ResultSet         rs    = null;
    Connection  connection  = null;
    int shipmentType        = 0;
    StringTokenizer levelIds = null;
    String tempLevelId  = "";
    String searchStrLevel = "";
    String marginId       = "";
    String servId         = "";
    String name           = "";
    String sMode          = "";
		StringBuffer   servId1           = new StringBuffer();
		StringBuffer   sMode1            = new StringBuffer();   
    StringBuffer searchStrLevelB     = new StringBuffer();
    ArrayList serviceLevelIds        = new ArrayList();
    
    try{
           if(shipmentMode!=null && !shipmentMode.equals(""))
          {
            if(shipmentMode.equals("1"))
            {
              shipmentType	=	FoursoftConfig.AIR;
              marginId      = "'"+shipmentType+"'";
            }else if(shipmentMode.equals("2"))
            {
            
              if(consoletype!=null && consoletype.equals("LCL"))
              {
                shipmentType	=	FoursoftConfig.SEA_LCL;
                marginId      = "'"+shipmentType+"'";
              }else if(consoletype!=null && consoletype.equals("FCL"))
              {
                shipmentType	=	FoursoftConfig.SEA_FCL;
                marginId      = "'"+shipmentType+"'";
              }else
              {
                shipmentType	=	FoursoftConfig.SEA_LCL;             
                marginId      = "'"+shipmentType+"'";
                shipmentType	=	FoursoftConfig.SEA_FCL;                
                marginId      += ",'"+shipmentType+"'";
              }
              
            }else if(shipmentMode.equals("4"))
            {
              if(consoletype!=null && consoletype.equals("LTL"))
              {
                shipmentType	=	FoursoftConfig.TRUCK_LTL;
                marginId      = "'"+shipmentType+"'";                
              }else if(consoletype!=null && consoletype.equals("FTL"))
              {
                shipmentType	=	FoursoftConfig.TRUCK_FTL;
                marginId      = "'"+shipmentType+"'";                
              }else
              {
                shipmentType	=	FoursoftConfig.TRUCK_LTL;             
                marginId      = "'"+shipmentType+"'";
                shipmentType	=	FoursoftConfig.TRUCK_FTL;                
                marginId      += ",'"+shipmentType+"'";                    
              }
            }
          }
        if(levelId!=null && !levelId.trim().equals("")) 
        {
          searchStrLevelB.append(" AND LEVELNO IN (");
          levelIds  = new StringTokenizer(levelId,"$");
          while(levelIds.hasMoreTokens())
          {
            tempLevelId = levelIds.nextToken();
            searchStrLevelB.append("'");
            searchStrLevelB.append(tempLevelId);
            searchStrLevelB.append("'");
            if(levelIds.hasMoreTokens())
              searchStrLevelB.append(",");
          }
          searchStrLevelB.append(")");
          searchStrLevel  = searchStrLevelB.toString();
        }else if(levelId.equals(""))
        {
          searchStrLevel  = "";
        }
        
        if(searchStr==null || searchStr.equals(""))
        {
          searchStr = "";
        }
         
        StringBuffer selectQ          = new StringBuffer();
        selectQ.append("SELECT DISTINCT ML.SERVICE_LEVEL,SL.SERVICELEVELDESC,SL.SHIPMENTMODE ");
        selectQ.append("FROM QMS_MARGIN_LIMIT_DTL ML,FS_FR_SERVICELEVELMASTER SL ");
        selectQ.append("WHERE ML.SERVICE_LEVEL=SL.SERVICELEVELID ");
        selectQ.append(" AND ML.SERVICE_LEVEL LIKE '");
        selectQ.append(searchStr);
        selectQ.append("%'");
//@@ Added by subrahmanyam for the pbn id:203354 on 22-APR-010
        if(!"view".equalsIgnoreCase(operation) && !"viewAll".equalsIgnoreCase(operation) )
        {
        if("ADMN_TERMINAL".equalsIgnoreCase(loginbean.getAccessType()))
        {
        	selectQ.append(" AND ML.TERMINALID IN(SELECT CHILD_TERMINAL_ID FROM FS_FR_TERMINAL_REGN ");
        	selectQ.append(" WHERE PARENT_TERMINAL_ID='");
        	selectQ.append(loginbean.getTerminalId());
        	selectQ.append("'");
        	selectQ.append(" UNION SELECT TERMINALID FROM FS_FR_TERMINALMASTER WHERE TERMINALID='");
        	selectQ.append(loginbean.getTerminalId());
        	selectQ.append("')");
        }
        else if("OPER_TERMINAL".equalsIgnoreCase(loginbean.getAccessType()))
        {
        	selectQ.append(" AND ML.TERMINALID IN( SELECT TERMINALID FROM FS_FR_TERMINALMASTER WHERE TERMINALID='");
        	selectQ.append(loginbean.getTerminalId());
        	selectQ.append("')");
        }
        }
 //@@ Ended by subrahmanyam for the pbn id:203354 on 22-APR-010        
         if(!marginId.equals(""))
          {
            selectQ.append(" AND MARGIN_ID IN (");
            selectQ.append(marginId);
            selectQ.append(")");
          }   
         
        selectQ.append(searchStrLevel);
        if(operation!=null && !operation.equals("Invalidate"))
        {
            selectQ.append(" AND ML.INVALIDATE='F'");
        }
        
        String selectQry        = selectQ.toString();
        //Logger.info(FILE_NAME,"selectQry::>::"+selectQry+"::");
        
        connection  = operationsImpl.getConnection();
        pstmt = connection.prepareStatement(selectQry);
        rs  = pstmt.executeQuery();
        while ( rs.next() )
        {
          servId1.delete(0,servId1.length());
          sMode1.delete(0,sMode1.length());
          servId = rs.getString("SERVICE_LEVEL");
          name = (rs.getString("SERVICELEVELDESC")!=null)?rs.getString("SERVICELEVELDESC"):"";
          
          if(servId.length() == 4)	
            {
                servId1.append(servId);
                servId1.append(" ");
                servId = servId1.toString();
            }
          if(servId.length() == 3)
            {
                servId1.append(servId);
                servId1.append("  ");
                servId = servId1.toString();
            }
          if(servId.length() == 2)	
            {
                servId1.append(servId);
                servId1.append("   ");
                servId = servId1.toString();
            }
          if(servId.length() == 1)
            {
                servId1.append(servId);
                servId1.append(" ");
                servId = servId1.toString();
            }
          
          sMode = (new String()).valueOf(rs.getInt("SHIPMENTMODE"));
           
          if(sMode!=null)
          { 
            if( sMode.equals("7"))   
            {sMode="AST";}
            if(sMode.equals("1"))
            {sMode="A  ";}
            if(sMode.equals("2"))
            {sMode=" S ";}		
            if(sMode.equals("3"))
            {sMode="AS ";}
            if(sMode.equals("4"))
            {sMode="  T";}
            if(sMode.equals("5"))
            {sMode="A T";}
            if(sMode.equals("6"))
            {sMode=" ST";}
          }	
            sMode1.append(servId);
            sMode1.append("[" );
            sMode1.append(sMode);
            sMode1.append( "]");
            sMode1.append("[" );
            sMode1.append(name);
            sMode1.append( "]");
            sMode = sMode1.toString();
            
            serviceLevelIds.add(sMode);
        }
    }catch(SQLException e)
    {
        //Logger.error(FILE_NAME,"------->getServiceLevelIds()",e.toString());
        logger.error(FILE_NAME+"------->getServiceLevelIds()"+e.toString());
        throw new EJBException();         
    }catch(Exception e)
    {
        //Logger.error(FILE_NAME,"------->getServiceLevelIds()",e.toString());
        logger.error(FILE_NAME+"------->getServiceLevelIds()"+e.toString());
        throw new EJBException();         
    }finally
    {
      try
      {
        if(rs!=null)
          { rs.close();}
        if(pstmt!=null)
          { pstmt.close();}
        if(connection!=null)
          { connection.close();}
      }catch(SQLException e)
      {
        //Logger.error(FILE_NAME,"------->getServiceLevelIds()",e.toString());
        logger.error(FILE_NAME+"------->getServiceLevelIds()"+e.toString());
        throw new EJBException();
      }
    }
    return serviceLevelIds;
  }
  /**
   * 
   * @return 
   * @param loginbean
   * @param searchStr
   */
  public ArrayList getLevelIds(String searchStr,ESupplyGlobalParameters loginbean,String operation)
  {
    //PreparedStatement pstmt = null;//Commented By RajKumari on 30-10-2008 for Connection Leakages.
    ResultSet         rs    = null;
    Connection  connection  = null;
    //String      selectQry   = "SELECT DISTINCT LEVEL_NO FROM QMS_DESIGNATION WHERE LEVEL_NO LIKE '"+searchStr+"%' AND INVALIDATE='F'";
    String      levelId     = "";
    ArrayList   levelIdList  = new ArrayList();
	CallableStatement cstmt  = null;
    try
    {
      connection  = operationsImpl.getConnection();
      //pstmt       = connection.prepareStatement(selectQry);
      //rs          = pstmt.executeQuery();
			  cstmt        = connection.prepareCall("{ ?= call QMS_SETUPS.GET_MARGINLIMITMASTER_2(?,?,?,?)}");
              cstmt.registerOutParameter(1,OracleTypes.CURSOR);
              cstmt.setString(2,loginbean.getTerminalId());
              cstmt.setString(3,(searchStr+"%"));
              cstmt.setString(4,operation);
              cstmt.setString(5,(searchStr+"%"));          
              
              cstmt.execute();
              rs  =  (ResultSet)cstmt.getObject(1);
      while(rs.next())
      {
        levelId = rs.getString("LEVELNO");
        if(levelId!=null && !levelId.equals(""))
          levelIdList.add(levelId);
      }
    }catch(SQLException e)
    {e.printStackTrace();
        //Logger.error(FILE_NAME,"------->getServiceLevelIds()",e.toString());
        logger.error(FILE_NAME+"------->getServiceLevelIds()"+e.toString());
        throw new EJBException();         
    }catch(Exception e)
    {e.printStackTrace();
        //Logger.error(FILE_NAME,"------->getServiceLevelIds()",e.toString());
        logger.error(FILE_NAME+"------->getServiceLevelIds()"+e.toString());
        throw new EJBException();         
    }finally
    {
      try
      {
        if(rs!=null)
          { rs.close();}
        if(cstmt!=null)
          { cstmt.close();}
        if(connection!=null)
          { connection.close();}
      }catch(SQLException e)
      {
        //Logger.error(FILE_NAME,"------->getServiceLevelIds()",e.toString());
        logger.error(FILE_NAME+"------->getServiceLevelIds()"+e.toString());
        throw new EJBException();
      }
    }
    return levelIdList;
  }
  /**
   * 
   * @return 
   * @param loginbean
   * @param sLevelIds
   * @param consoletype
   * @param shipmentMode
   * @param searchStr
   * @param operation
   */
  public ArrayList getMarginLimitsLevelIds(String operation,String searchStr,String shipmentMode,String consoletype,String sLevelIds,ESupplyGlobalParameters loginbean)
  {
    //PreparedStatement pstmt = null;//Commented By RajKumari on 30-10-2008 for Connection Leakages.
    ResultSet         rs    = null;
    Connection  connection  = null;
    int shipmentType        = 0;  
    String levelNo          = "";
    ArrayList    levelNoList= new ArrayList();
    String selectQry        = "";
    String searchServiceLevel = "";
    StringBuffer searchServiceLevelB =  new StringBuffer();
    StringBuffer  selectQryB= new StringBuffer();
    StringTokenizer serviceLevelIds  = null;
    String marginId         = "";
    String validData        = "";
	CallableStatement cstmt  = null;
    try
    {
          if(shipmentMode!=null && !shipmentMode.equals(""))
          {
            if(shipmentMode.equals("1"))
            {
              shipmentType	=	FoursoftConfig.AIR;
              marginId      = "'"+shipmentType+"'";
            }
            else if(shipmentMode.equals("0"))
            {
              shipmentType	=	0;
              marginId      = "'"+shipmentType+"'";
            }
            else if(shipmentMode.equals("2"))
            {
            
              if(consoletype!=null && consoletype.equals("LCL"))
              {
                shipmentType	=	FoursoftConfig.SEA_LCL;
                marginId      = "'"+shipmentType+"'";
              }else if(consoletype!=null && consoletype.equals("FCL"))
              {
                shipmentType	=	FoursoftConfig.SEA_FCL;
                marginId      = "'"+shipmentType+"'";
              }else
              {
                shipmentType	=	FoursoftConfig.SEA_LCL;             
                marginId      = "'"+shipmentType+"'";
                shipmentType	=	FoursoftConfig.SEA_FCL;                
                marginId      += ",'"+shipmentType+"'";
              }
              
            }else if(shipmentMode.equals("4"))
            {
              if(consoletype!=null && consoletype.equals("LTL"))
              {
                shipmentType	=	FoursoftConfig.TRUCK_LTL;
                marginId      = "'"+shipmentType+"'";                
              }else if(consoletype!=null && consoletype.equals("FTL"))
              {
                shipmentType	=	FoursoftConfig.TRUCK_FTL;
                marginId      = "'"+shipmentType+"'";                
              }else
              {
                shipmentType	=	FoursoftConfig.TRUCK_LTL;             
                marginId      = "'"+shipmentType+"'";
                shipmentType	=	FoursoftConfig.TRUCK_FTL;                
                marginId      += ",'"+shipmentType+"'";                    
              }
            }
            else
            {
              marginId      =  " '1','2','4','7','15' ";
            }
          }else
            {
              marginId      =  " '1','2','4','7','15' ";
            }
        //System.out.println("marginId****************"+marginId);
          if(sLevelIds!=null && !sLevelIds.trim().equals(""))
          {
            searchServiceLevelB.append(" AND SERVICE_LEVEL IN (");
            serviceLevelIds = new StringTokenizer(sLevelIds,"$");
            while(serviceLevelIds.hasMoreTokens())
            {
              searchServiceLevelB.append("'");
              searchServiceLevelB.append(serviceLevelIds.nextToken());
              searchServiceLevelB.append("'");
              if(serviceLevelIds.hasMoreTokens())
                searchServiceLevelB.append(",");
            }
            searchServiceLevelB.append(")");
            searchServiceLevel  = searchServiceLevelB.toString();
          }else
          {
            searchServiceLevel  = "";
          }
          if(searchStr==null || searchStr.equals(""))
            {
              searchStr = "";
            }
        /*    
          selectQryB.append("SELECT DISTINCT LEVELNO FROM QMS_MARGIN_LIMIT_DTL WHERE ");
          selectQryB.append("LEVELNO LIKE '");
          selectQryB.append(searchStr);
          selectQryB.append("%'");
          if(!marginId.equals(""))
          {
            selectQryB.append(" AND MARGIN_ID IN (");
            selectQryB.append(marginId);
            selectQryB.append(")");
          }
          selectQryB.append(searchServiceLevel);
         /* if(operation!=null && !operation.equals("Invalidate"))
          {
            selectQryB.append(" AND INVALIDATE='F'");
          }*/
          selectQry = selectQryB.toString();
          //Logger.info(FILE_NAME,"selectQry::>::"+selectQry+"::");          
          connection =  operationsImpl.getConnection();
          //pstmt      =  connection.prepareStatement(selectQry);
          //rs         =  pstmt.executeQuery();
          
			  cstmt        = connection.prepareCall("{ ?= call QMS_SETUPS.GET_MARGINLIMITMASTER_1(?,?,?,?)}");
              cstmt.registerOutParameter(1,OracleTypes.CURSOR);
              cstmt.setString(2,loginbean.getTerminalId());
              cstmt.setString(3,(searchStr+"%"));                   
              cstmt.setString(4,operation);
              cstmt.setString(5,marginId);  
              cstmt.execute();
              rs  =  (ResultSet)cstmt.getObject(1);
          while(rs.next())
          {
            levelNo = (rs.getString("LEVELNO")!=null)?rs.getString("LEVELNO"):"";
            levelNoList.add(levelNo);
          }
    }catch(SQLException e)
    {e.printStackTrace();
        //Logger.error(FILE_NAME,"------->getServiceLevelIds()",e.toString());
        logger.error(FILE_NAME+"------->getServiceLevelIds()"+e.toString());
        throw new EJBException();         
    }catch(Exception e)
    {e.printStackTrace();
        //Logger.error(FILE_NAME,"------->getServiceLevelIds()",e.toString());
        logger.error(FILE_NAME+"------->getServiceLevelIds()"+e.toString());
        throw new EJBException();         
    }finally
    {
      try
      {
        if(rs!=null)
          { rs.close();}
       /* if(pstmt!=null)
          { pstmt.close();}*///Commented By RajKumari on 30-10-2008 for Connection Leakages.
        if(connection!=null)
          { connection.close();}
      }catch(SQLException e)
      {
        //Logger.error(FILE_NAME,"------->getServiceLevelIds()",e.toString());
        logger.error(FILE_NAME+"------->getServiceLevelIds()"+e.toString());
        throw new EJBException();
      }
    }
    return levelNoList;
  }
    
   /**
   * 
   * @return 
   * @param loginbean
   * @param sLevelId
   * @param levelId
   * @param chargetype
   * @param operation
   */
  public ArrayList getMarginLimitList1(String operation,String chargeType,String[] levelId,ESupplyGlobalParameters loginbean)
  {
      PreparedStatement pstmt = null;
      ResultSet         rs    = null;
      Connection    connection  = null;
      ArrayList   marginList    = new ArrayList();
      StringBuffer  selectQryB  = new StringBuffer();
      String        selectQry   = "";
      String      marginId      = "";
      String      chType        = "";
      String   validData        = "";
      MarginLimitMasterDOB marginLimitMasterDOB = null;
      try{
                   
          chargeType  = "'"+chargeType.toUpperCase()+"'";
          if(operation!=null && !operation.equals("Invalidate"))
          {
            validData   = " INVALIDATE='F'";
          }
          selectQryB.append("SELECT MARGIN_ID,LEVELNO,SERVICE_LEVEL,MAXDISCOUNT,MINMARGINS,INVALIDATE,CHARGETYPE FROM QMS_MARGIN_LIMIT_DTL WHERE CHARGETYPE=");
          selectQryB.append(chargeType);
         if((levelId!=null && levelId.length>0))
         {         
            selectQryB.append(" AND LEVELNO IN (");
            int levelIdsLen	=	levelId.length;
            for(int i=0;i<levelIdsLen;i++)
            {
              selectQryB.append("'");
              selectQryB.append(levelId[i]);
              selectQryB.append("'");
              if(i+1<levelId.length)
              {
                selectQryB.append(",");
              }
            }
            selectQryB.append(")");
         }
          if(operation!=null && !operation.equals("Invalidate"))
          {
            selectQryB.append(" AND ");
            selectQryB.append(validData);
          }
        //@@Added by subrahmanyam for the pbn id: 203354 on 23-APR-10          
          if("Invalidate".equalsIgnoreCase(operation))
          {
          	if("ADMN_TERMINAL".equalsIgnoreCase(loginbean.getAccessType()))
          	{
          		selectQryB.append(" AND TERMINALID IN (SELECT CHILD_TERMINAL_ID FROM FS_FR_TERMINAL_REGN ");
          		selectQryB.append(" WHERE PARENT_TERMINAL_ID='"+loginbean.getTerminalId()+"' UNION SELECT ");
          		selectQryB.append(" TERMINALID FROM FS_FR_TERMINALMASTER WHERE TERMINALID='"+loginbean.getTerminalId()+"')");
          		
          	}
          	else if("OPER_TERMINAL".equalsIgnoreCase(loginbean.getAccessType()))
          	{
          		selectQryB.append(" AND TERMINALID IN (SELECT TERMINALID FROM FS_FR_TERMINALMASTER WHERE TERMINALID='"+loginbean.getTerminalId()+"')");
          		
          	}
          	
          }
//@@Ended for 203354

            selectQryB.append(" ORDER BY TO_NUMBER(MARGIN_ID),LEVELNO,SERVICE_LEVEL");
            selectQry = selectQryB.toString();
            
            connection  = operationsImpl.getConnection();
            //System.out.println("selectQryselectQry :: "+selectQry);
            pstmt       = connection.prepareStatement(selectQry);
            rs          = pstmt.executeQuery();
            while(rs.next())
            {
              marginLimitMasterDOB  = new MarginLimitMasterDOB();
              marginLimitMasterDOB.setMarginId(rs.getString("MARGIN_ID"));
              marginLimitMasterDOB.setLevelId(rs.getString("LEVELNO"));
              marginLimitMasterDOB.setServiceLevel(rs.getString("SERVICE_LEVEL"));
              marginLimitMasterDOB.setMaxDiscount(rs.getString("MAXDISCOUNT"));
              marginLimitMasterDOB.setMinMargin(rs.getString("MINMARGINS"));
              marginLimitMasterDOB.setInvalidate(rs.getString("INVALIDATE"));
              marginLimitMasterDOB.setChargeType(rs.getString("CHARGETYPE"));
              marginList.add(marginLimitMasterDOB);
            }
      }catch(SQLException e)
    {
        //Logger.error(FILE_NAME,"------->getMarginLimitList1()",e.toString());
        logger.error(FILE_NAME+"------->getMarginLimitList1()"+e.toString());
        throw new EJBException();         
    }catch(Exception e)
    {
        //Logger.error(FILE_NAME,"------->getMarginLimitList1()",e.toString());
        logger.error(FILE_NAME+"------->getMarginLimitList1()"+e.toString());
        throw new EJBException();         
    }finally
    {
      try
      {
        if(rs!=null)
          { rs.close();}
        if(pstmt!=null)
          { pstmt.close();}
        if(connection!=null)
          { connection.close();}
      }catch(SQLException e)
      {
        //Logger.error(FILE_NAME,"------->getMarginLimitList1()",e.toString());
        logger.error(FILE_NAME+"------->getMarginLimitList1()"+e.toString());
        throw new EJBException();
      }
    }
    return marginList;
  }
  
  /**
   * 
   * @return 
   * @param loginbean
   * @param sLevelId
   * @param levelId
   * @param consoletype
   * @param shipmentMode
   * @param operation
   */
  public ArrayList getMarginLimitList(String operation,String shipmentMode,String consoletype,String[] levelId,String[] sLevelId,ESupplyGlobalParameters loginbean)
  {
      PreparedStatement pstmt = null;
      ResultSet         rs    = null;
      Connection    connection  = null;
      ArrayList   marginList    = new ArrayList();
      StringBuffer  selectQryB  = new StringBuffer();
      String        selectQry   = "";
      String      marginId      = "";
      int      shipmentType     = 0;
      String   validData        = "";
      MarginLimitMasterDOB marginLimitMasterDOB = null;
      try{
          if(shipmentMode!=null && !shipmentMode.equals(""))
          {
            if(shipmentMode.equals("1"))
            {
              shipmentType	=	FoursoftConfig.AIR;
              marginId      = "'"+shipmentType+"'";
            }else if(shipmentMode.equals("2"))
            {
            
              if(consoletype!=null && consoletype.equals("LCL"))
              {
                shipmentType	=	FoursoftConfig.SEA_LCL;
                marginId      = "'"+shipmentType+"'";
              }else if(consoletype!=null && consoletype.equals("FCL"))
              {
                shipmentType	=	FoursoftConfig.SEA_FCL;
                marginId      = "'"+shipmentType+"'";
              }else
              {
                shipmentType	=	FoursoftConfig.SEA_LCL;             
                marginId      = "'"+shipmentType+"'";
                shipmentType	=	FoursoftConfig.SEA_FCL;                
                marginId      += ",'"+shipmentType+"'";
              }
              
            }else if(shipmentMode.equals("4"))
            {
              if(consoletype!=null && consoletype.equals("LTL"))
              {
                shipmentType	=	FoursoftConfig.TRUCK_LTL;
                marginId      = "'"+shipmentType+"'";                
              }else if(consoletype!=null && consoletype.equals("FTL"))
              {
                shipmentType	=	FoursoftConfig.TRUCK_FTL;
                marginId      = "'"+shipmentType+"'";                
              }else
              {
                shipmentType	=	FoursoftConfig.TRUCK_LTL;             
                marginId      = "'"+shipmentType+"'";
                shipmentType	=	FoursoftConfig.TRUCK_FTL;                
                marginId      += ",'"+shipmentType+"'";                    
              }
            }
          }
          if(operation!=null && !operation.equals("Invalidate"))
          {
            validData   = " CHARGETYPE='FREIGHT' AND INVALIDATE='F'";
          }
          else
          {
            validData   = " CHARGETYPE='FREIGHT'";
          }
          selectQryB.append("SELECT MARGIN_ID,LEVELNO,SERVICE_LEVEL,MAXDISCOUNT,MINMARGINS,INVALIDATE,CHARGETYPE FROM QMS_MARGIN_LIMIT_DTL");
          
          //String selectQry  = "SELECT MARGIN_ID,LEVELNO,SERVICE_LEVEL,MAXDISCOUNT,MINMARGINS FROM QMS_MARGIN_LIMIT_DTL";
          
          if((marginId!=null && !marginId.equals("")) 
            || (levelId!=null && levelId.length>0) 
            || (sLevelId!=null && sLevelId.length>0))
            {
                selectQryB.append(" WHERE ");
                if(marginId !=null && !marginId.equals(""))
                {
                  selectQryB.append("MARGIN_ID IN (");
                  selectQryB.append(marginId);
                  selectQryB.append(")");                 
                }
                if((levelId!=null && levelId.length>0))
                {
                  if(marginId !=null && !marginId.equals(""))
                  {
                    selectQryB.append(" AND ");
                  }
                  selectQryB.append("LEVELNO IN (");
                  int levelIdsLen	=	levelId.length;
                  for(int i=0;i<levelIdsLen;i++)
                  {
                    selectQryB.append("'");
                    selectQryB.append(levelId[i]);
                    selectQryB.append("'");
                    if(i+1<levelId.length)
                    {
                      selectQryB.append(",");
                    }
                  }
                  selectQryB.append(")");
                }
                if(sLevelId!=null && sLevelId.length>0)
                {
                  if((marginId!=null && !marginId.equals("")) 
                    || (levelId!=null && levelId.length>0))
                    {
                       selectQryB.append(" AND ");                     
                    }
                  selectQryB.append("SERVICE_LEVEL IN (");
                  int sLevelIdsLen	=	sLevelId.length;
                  for(int i=0;i<sLevelIdsLen;i++)
                  {
                    selectQryB.append("'");
                    selectQryB.append(sLevelId[i]);
                    selectQryB.append("'");
                    if(i+1<sLevelId.length)
                    {
                      selectQryB.append(",");
                    }
                  }
                  selectQryB.append(")");                   
                }
                
                if(operation!=null && !operation.equals("Invalidate"))
                {
                  selectQryB.append(" AND ");
                  selectQryB.append(validData);
                }
                else
                {
                  selectQryB.append(" AND ");
                  selectQryB.append(validData);
                }
            }else
            {
              if(operation!=null && !operation.equals("Invalidate"))
                {
                  selectQryB.append(" WHERE ");
                  selectQryB.append(validData);
                }
                else
                {
                  selectQryB.append(" WHERE ");
                  selectQryB.append(validData);
                }
            }
//@@Added by subrahmanyam for the pbn id: 203354 on 23-APR-10          
            if("Invalidate".equalsIgnoreCase(operation))
            {
            	if("ADMN_TERMINAL".equalsIgnoreCase(loginbean.getAccessType()))
            	{
            		selectQryB.append(" AND TERMINALID IN (SELECT CHILD_TERMINAL_ID FROM FS_FR_TERMINAL_REGN ");
            		selectQryB.append(" WHERE PARENT_TERMINAL_ID='"+loginbean.getTerminalId()+"' UNION SELECT ");
            		selectQryB.append(" TERMINALID FROM FS_FR_TERMINALMASTER WHERE TERMINALID='"+loginbean.getTerminalId()+"')");
            		
            	}
            	else if("OPER_TERMINAL".equalsIgnoreCase(loginbean.getAccessType()))
            	{
            		selectQryB.append(" AND TERMINALID IN (SELECT TERMINALID FROM FS_FR_TERMINALMASTER WHERE TERMINALID='"+loginbean.getTerminalId()+"')");
            		
            	}
            	
            }
//@@Ended for 203354
            selectQryB.append(" ORDER BY TO_NUMBER(MARGIN_ID),LEVELNO,SERVICE_LEVEL");
            selectQry = selectQryB.toString();
            
            connection  = operationsImpl.getConnection();
            pstmt       = connection.prepareStatement(selectQry);
            rs          = pstmt.executeQuery();
            while(rs.next())
            {
              marginLimitMasterDOB  = new MarginLimitMasterDOB();
              marginLimitMasterDOB.setMarginId(rs.getString("MARGIN_ID"));
              marginLimitMasterDOB.setLevelId(rs.getString("LEVELNO"));
              marginLimitMasterDOB.setServiceLevel(rs.getString("SERVICE_LEVEL"));
              marginLimitMasterDOB.setMaxDiscount(rs.getString("MAXDISCOUNT"));
              marginLimitMasterDOB.setMinMargin(rs.getString("MINMARGINS"));
              marginLimitMasterDOB.setInvalidate(rs.getString("INVALIDATE"));
              marginLimitMasterDOB.setChargeType(rs.getString("CHARGETYPE"));
              marginList.add(marginLimitMasterDOB);
            }
      }catch(SQLException e)
    {
        //Logger.error(FILE_NAME,"------->getMarginLimitList()",e.toString());
        logger.error(FILE_NAME+"------->getMarginLimitList()"+e.toString());
        throw new EJBException();         
    }catch(Exception e)
    {
        //Logger.error(FILE_NAME,"------->getMarginLimitList()",e.toString());
        logger.error(FILE_NAME+"------->getMarginLimitList()"+e.toString());
        throw new EJBException();         
    }finally
    {
      try
      {
        if(rs!=null)
          { rs.close();}
        if(pstmt!=null)
          { pstmt.close();}
        if(connection!=null)
          { connection.close();}
      }catch(SQLException e)
      {
        //Logger.error(FILE_NAME,"------->getMarginLimitList()",e.toString());
        logger.error(FILE_NAME+"------->getMarginLimitList()"+e.toString());
        throw new EJBException();
      }
    }
    return marginList;
  }
  
    /**
   * 
   * @throws java.sql.SQLException
   * @return 
   * @param loginbean
   * @param chargesMasterDOB
   */
  private boolean isValidChargeId(ChargesMasterDOB chargesMasterDOB,ESupplyGlobalParameters loginbean)throws SQLException
  {
    PreparedStatement pstmt   = null;
    Connection connection     = null;
    ResultSet         rs      = null;
    boolean    isValid        = true;
    String selectQry          = "SELECT CHARGE_ID FROM QMS_CHARGESMASTER WHERE CHARGE_ID =?";
    try
    {
      connection    = operationsImpl.getConnection();
      pstmt         = connection.prepareStatement(selectQry);
      if(chargesMasterDOB!=null)
      {
        if(chargesMasterDOB.getChargeId().equals("1") || chargesMasterDOB.getChargeId().equals("2") || chargesMasterDOB.getChargeId().equals("3"))
        {
          isValid = false;
          return isValid;
        }
        
        pstmt.setString(1,chargesMasterDOB.getChargeId());
        rs  = pstmt.executeQuery();
        if(rs.next())
        {
          isValid = false;
        }

      }
    }catch(SQLException e)
    {
        //Logger.error(FILE_NAME,"------->isValidChargeId()",e.toString());
        logger.error(FILE_NAME+"------->isValidChargeId()"+e.toString());
        throw new EJBException();         
    }catch(Exception e)
    {
        //Logger.error(FILE_NAME,"------->isValidChargeId()",e.toString());
        logger.error(FILE_NAME+"------->isValidChargeId()"+e.toString());
        throw new EJBException();         
    }finally
    {
      try
      {
        if(rs!=null)
          { rs.close();}
        if(pstmt!=null)
          { pstmt.close();}
        if(connection!=null)
          { connection.close();}
      }catch(SQLException e)
      {
        //Logger.error(FILE_NAME,"------->isValidChargeId()",e.toString());
        logger.error(FILE_NAME+"------->isValidChargeId()"+e.toString());
        throw new EJBException();
      }
    }
    return isValid;
  }
  /**
   * 
   * @throws javax.ejb.EJBException
   * @return 
   * @param loginbean
   * @param dataList
   */
  public ArrayList insertChargesMasterDtls(ArrayList dataList,ESupplyGlobalParameters loginbean)throws EJBException
  {
    ArrayList insertedList    = new ArrayList();
    ArrayList invalidList     = new ArrayList();
    ArrayList returnList      = new ArrayList();
    ChargesMasterEntityBeanLocalHome home  = null;
    ChargesMasterEntityBeanLocal      local = null;
    ChargesMasterDOB chargesMasterDOB =null;
    try
    {
    	int dataListSize	=	dataList.size();
      for(int i=0;i<dataListSize;i++)
      {
        chargesMasterDOB  = (ChargesMasterDOB)dataList.get(i);
        if(isValidChargeId(chargesMasterDOB,loginbean))
        {
          insertedList.add(chargesMasterDOB);
        }else
        {
          invalidList.add(chargesMasterDOB);
        }
      }
      returnList.add(insertedList);
      returnList.add(invalidList);
      if(insertedList!=null && insertedList.size()>0)
      {
        home  = (ChargesMasterEntityBeanLocalHome)LookUpBean.getEJBLocalHome("java:comp/env/ChargesMasterEntityBean");
        local = (ChargesMasterEntityBeanLocal)home.create(insertedList);
      }     
    }catch(NamingException nex)
    {
        //Logger.error(FILE_NAME,"insertChargesMasterDtls()",nex.toString());
        logger.error(FILE_NAME+"insertChargesMasterDtls()"+nex.toString());
        throw new EJBException();
    }catch(Exception e)
    {
        //Logger.error(FILE_NAME,"insertChargesMasterDtls()",e.toString());
        logger.error(FILE_NAME+"insertChargesMasterDtls()"+e.toString());
        throw new EJBException();      
    }
      return returnList;
  }

  private boolean isValidForModify(String chargeId ,ESupplyGlobalParameters loginbean,String operation)
  {
    PreparedStatement pstmt   = null;
    Connection connection     = null;
    ResultSet         rs      = null;
    boolean    isValid        = true;
    String selectQry          = " SELECT CHARGE_ID FROM QMS_CHARGESMASTER WHERE CHARGE_ID =? AND (INVALIDATE='F' OR  INVALIDATE IS NULL)";
    
    String selectDescQry      = " SELECT CHARGEDESCID  FROM QMS_CHARGEDESCMASTER WHERE CHARGEID=? AND INACTIVATE='N'";
        
    
    try
    {
      connection    = operationsImpl.getConnection();
      pstmt         = connection.prepareStatement(selectQry);
      if(chargeId!=null)
      {
        if(chargeId.equals("1") || chargeId.equals("2") || chargeId.equals("3"))
        {
          isValid = false;
          return isValid;
        }
        
        pstmt.setString(1,chargeId.trim());
        rs  = pstmt.executeQuery();
        if(rs.next())
        {
          isValid = true;
        }
        
        if(rs!=null)
          { rs.close();}
        if(pstmt!=null)
          { pstmt.close();}
        
        if("Delete".equalsIgnoreCase(operation) || "Modify".equalsIgnoreCase(operation))  
        {
            pstmt = connection.prepareStatement(selectDescQry);
            pstmt.setString(1,chargeId);
            
            rs = pstmt.executeQuery();
            
            if(rs.next())
            {
                isValid = false;
            }
            if(rs!=null)
              { rs.close();}
            if(pstmt!=null)
              { pstmt.close();}           
        }
      }
    }catch(SQLException e)
    {
        //Logger.error(FILE_NAME,"------->isValidForModify()",e.toString());
        logger.error(FILE_NAME+"------->isValidForModify()"+e.toString());
        throw new EJBException();         
    }catch(Exception e)
    {
        //Logger.error(FILE_NAME,"------->isValidForModify()",e.toString());
        logger.error(FILE_NAME+"------->isValidForModify()"+e.toString());
        throw new EJBException();         
    }finally
    {
      try
      {
        if(rs!=null)
          { rs.close();}
        if(pstmt!=null)
          { pstmt.close();}
        if(connection!=null)
          { connection.close();}
      }catch(SQLException e)
      {
        //Logger.error(FILE_NAME,"------->isValidForModify()",e.toString());
        logger.error(FILE_NAME+"------->isValidForModify()"+e.toString());
        throw new EJBException();
      }
    }
    return isValid;    
    
  }

    /**
     * 
     * @throws javax.ejb.EJBException
     * @throws javax.ejb.ObjectNotFoundException
     * @return 
     * @param loginbean
     * @param chargeId
     */
  public ChargesMasterDOB getChargesMasterDtl(String chargeId,ESupplyGlobalParameters loginbean,String operation)throws ObjectNotFoundException,EJBException
  {
    ChargesMasterEntityBeanLocalHome home  = null;
    ChargesMasterEntityBeanLocal      local = null;
    ChargesMasterEntityBeanPK       pkObj   = new ChargesMasterEntityBeanPK();
    ChargesMasterDOB chargesMasterDOB =null;
    try
    {
        if(isValidForModify(chargeId,loginbean,operation))
        {
            pkObj.chargeId      = chargeId;
            home  = (ChargesMasterEntityBeanLocalHome)LookUpBean.getEJBLocalHome("java:comp/env/ChargesMasterEntityBean");
            local = (ChargesMasterEntityBeanLocal)home.findByPrimaryKey(pkObj);
            chargesMasterDOB  = local.getChagesMasterDOB();
        }else
        {
          throw new ObjectNotFoundException("Bean could not find");
        }
        
    }catch(ObjectNotFoundException e)
    {
        //Logger.error(FILE_NAME,"getChargesMasterDtl()",e.toString());
        logger.error(FILE_NAME+"getChargesMasterDtl()"+e.toString());
        throw new ObjectNotFoundException("Bean could not find");      
    }catch(NamingException nex)
    {
        //Logger.error(FILE_NAME,"getChargesMasterDtl()",nex.toString());
        logger.error(FILE_NAME+"getChargesMasterDtl()"+nex.toString());
        throw new EJBException();
    }catch(FinderException e)
    {
         //Logger.error(FILE_NAME,"getChargesMasterDtl()",e.toString());
         logger.error(FILE_NAME+"getChargesMasterDtl()"+e.toString());
        throw new EJBException();     
    }
    catch(Exception e)
    {
        //Logger.error(FILE_NAME,"getChargesMasterDtl()",e.toString());
        logger.error(FILE_NAME+"getChargesMasterDtl()"+e.toString());
        throw new EJBException();      
    }
    
    return chargesMasterDOB;
  }

  /**
   * 
   * @throws javax.ejb.ObjectNotFoundException
   * @throws javax.ejb.EJBException
   * @return 
   * @param loginbean
   * @param chargesMasterDOB
   */
 public boolean updateChargesMasterDetails(ChargesMasterDOB chargesMasterDOB,ESupplyGlobalParameters loginbean)throws EJBException,ObjectNotFoundException
  {
    ChargesMasterEntityBeanLocalHome home  = null;
    ChargesMasterEntityBeanLocal      local = null;
    ChargesMasterEntityBeanPK       pkObj   = new ChargesMasterEntityBeanPK();
    boolean   success                     = false;
    try
    {
      pkObj.chargeId  = chargesMasterDOB.getChargeId();
      home  = (ChargesMasterEntityBeanLocalHome)LookUpBean.getEJBLocalHome("java:comp/env/ChargesMasterEntityBean");
      local = (ChargesMasterEntityBeanLocal)home.findByPrimaryKey(pkObj);
      local.setChargesMasterDOB(chargesMasterDOB);
      success = true;
    }catch(ObjectNotFoundException e)
    {
        success = false;    
        //Logger.error(FILE_NAME,"updateChargesMasterDetails()",e.toString());
        logger.error(FILE_NAME+"updateChargesMasterDetails()"+e.toString());
        throw new ObjectNotFoundException("Bean could not find");      
    }catch(NamingException e)
    {
        success = false;
        //Logger.error(FILE_NAME,"updateChargesMasterDetails()",e.toString());
        logger.error(FILE_NAME+"updateChargesMasterDetails()"+e.toString());
        throw new EJBException();      
    }catch(FinderException e)
    {
        success = false;    
        //Logger.error(FILE_NAME,"updateChargesMasterDetails()",e.toString());
        logger.error(FILE_NAME+"updateChargesMasterDetails()"+e.toString());
        throw new EJBException();      
    }catch(Exception e)
    {
        success = false;    
        //Logger.error(FILE_NAME,"updateChargesMasterDetails()",e.toString());
        logger.error(FILE_NAME+"updateChargesMasterDetails()"+e.toString());
        throw new EJBException();     
    }
    return success;
  }  
  /**
   * 
   * @throws javax.ejb.ObjectNotFoundException
   * @throws javax.ejb.EJBException
   * @return 
   * @param loginbean
   * @param chargesMasterDOB
   */
   public boolean deleteChargesMasterDtls(ChargesMasterDOB chargesMasterDOB,ESupplyGlobalParameters loginbean)throws EJBException,ObjectNotFoundException
  {
    ChargesMasterEntityBeanLocalHome home  = null;
    ChargesMasterEntityBeanLocal      local = null;
    ChargesMasterEntityBeanPK       pkObj   = new ChargesMasterEntityBeanPK();
    boolean   success                     = false;
    try
    {
      pkObj.chargeId       = chargesMasterDOB.getChargeId();
      home  = (ChargesMasterEntityBeanLocalHome)LookUpBean.getEJBLocalHome("java:comp/env/ChargesMasterEntityBean");
      local = (ChargesMasterEntityBeanLocal)home.findByPrimaryKey(pkObj);
      local.remove();
      success = true;
    }catch(NamingException e)
    {
        success = false;
        //Logger.error(FILE_NAME,"deleteChargesMasterDtls()",e.toString());
        logger.error(FILE_NAME+"deleteChargesMasterDtls()"+e.toString());
        throw new EJBException();      
    }catch(ObjectNotFoundException e)
    {
        success = false;    
        //Logger.error(FILE_NAME,"deleteChargesMasterDtls()",e.toString());
        logger.error(FILE_NAME+"deleteChargesMasterDtls()"+e.toString());
        throw new ObjectNotFoundException("Bean could not find");      
    }catch(FinderException e)
    {
        success = false;    
        //Logger.error(FILE_NAME,"deleteChargesMasterDtls()",e.toString());
        logger.error(FILE_NAME+"deleteChargesMasterDtls()"+e.toString());
        throw new EJBException();      
    }catch(Exception e)
    {
        success = false;    
        //Logger.error(FILE_NAME,"updateMarginLimitDetails()",e.toString());
        logger.error(FILE_NAME+"updateMarginLimitDetails()"+e.toString());
        throw new EJBException();     
    }
    return success;
  }
  

    /**
     * 
     * @throws javax.ejb.EJBException
     * @return 
     * @param loginbean
     * @param shipModeStr
     * @param fromWhere
     * @param searchStr
     *///modified by VLAKSHMI for CR #170761 on 20090626
  public ArrayList getAllChargeIds(String searchStr,String fromWhere,String shipModeStr,String operation,ESupplyGlobalParameters loginbean,String chargeGroupId)throws EJBException
  {  
    PreparedStatement pstmt     = null;
    Connection    connection    = null;
    ResultSet         rs        = null;
    ArrayList     chargeList    = new ArrayList();
    String        chargeId      = "";
    StringBuffer  chargeIdBuf   = null;
    String        shipMode      = "";
    StringBuffer  selectAllQuery =null;//added by VLAKSHMI for CR #170761 on 20090626
    try
    {
    StringBuffer  selectQry     = new StringBuffer("SELECT CHARGE_ID ||'--'|| CHARGE_DESCRIPTION FROM QMS_CHARGESMASTER WHERE CHARGE_ID LIKE '");
                  selectQry.append(searchStr);
                  selectQry.append("%'");
                  selectQry.append(" AND (INVALIDATE ='F' OR INVALIDATE is null )");
                
                  if(operation!=null && !operation.equals(""))
                  {
                    if(operation.equals("Modify") || operation.equals("Delete"))
                    {
                      selectQry.append(" AND CHARGE_ID NOT IN ( SELECT DISTINCT CHARGEID FROM QMS_CHARGEDESCMASTER WHERE INACTIVATE = 'N') ");
                    }
                  }
                  selectQry.append(" ORDER BY CHARGE_ID ");
                  
                  //Logger.info(FILE_NAME,"selectQryselectQry::"+selectQry);
                  
    StringBuffer  selectAllQry  = new StringBuffer("SELECT CHARGE_ID ||'('|| COST_INCURREDAT || ')' ||'--'|| CHARGE_DESCRIPTION FROM QMS_CHARGESMASTER WHERE CHARGE_ID LIKE '");
                  selectAllQry.append(searchStr);
                  selectAllQry.append("%' AND SHIPMENT_MODE IN (");
                  selectAllQry.append(shipModeStr);
                  selectAllQry.append(")");
                  selectAllQry.append(" AND (INVALIDATE ='F' OR INVALIDATE is null)");
                  selectAllQry.append(" ORDER BY CHARGE_ID ");
                  ////added by VLAKSHMI for CR #170761 on 20090626
                  if(chargeGroupId.length()>0)
                  {
                      selectAllQuery= new StringBuffer("SELECT  distinct BM.CHARGE_ID ||'('|| BM.COST_INCURREDAT || ')' ||'--'|| BM.CHARGE_DESCRIPTION CHARGEID, BM.SHIPMENT_MODE FROM QMS_CHARGESMASTER BM ,QMS_CHARGE_GROUPSMASTER  GM WHERE BM.CHARGE_ID LIKE '");
                  selectAllQuery.append(searchStr);
                  selectAllQuery.append("%'");
                  selectAllQuery.append(" AND (BM.INVALIDATE ='F' OR BM.INVALIDATE is null) ");
                   selectAllQuery.append(" AND GM.Charge_Id = BM.Charge_Id " );
                    selectAllQuery.append(" AND GM.CHARGEGROUP_ID='"+chargeGroupId+"' " );
   
                  selectAllQuery.append(" ORDER BY CHARGEID,BM.SHIPMENT_MODE");
                  }
                  else{
     selectAllQuery= new StringBuffer("SELECT CHARGE_ID ||'('|| COST_INCURREDAT || ')' ||'--'|| CHARGE_DESCRIPTION, SHIPMENT_MODE FROM QMS_CHARGESMASTER WHERE CHARGE_ID LIKE '");
                  selectAllQuery.append(searchStr);
                  selectAllQuery.append("%'");
                  selectAllQuery.append(" AND (INVALIDATE ='F' OR INVALIDATE is null) ");
                  selectAllQuery.append(" ORDER BY CHARGE_ID,COST_INCURREDAT,SHIPMENT_MODE");
                  }
    
    //end for CR #170761
      connection  =   operationsImpl.getConnection();
     // Logger.info(FILE_NAME,"fromWhere::"+fromWhere);
      if(fromWhere.equals("chargemaster"))
        { pstmt       =   connection.prepareStatement(selectQry.toString());}
      else if(fromWhere.equals("chargegroup") || fromWhere.equals("chargeDescription"))
        { pstmt       =   connection.prepareStatement(selectAllQry.toString());}
      else if(fromWhere.equals("buycharges") || fromWhere.equals("sellcharges"))
        { pstmt       =   connection.prepareStatement(selectAllQuery.toString());}
      rs          =   pstmt.executeQuery();
      while(rs.next())
      {
        chargeId  = rs.getString(1);
        if(chargeId.trim().endsWith("--"))
          {  chargeId  = chargeId.substring(0,chargeId.indexOf("--"));}
        if(fromWhere.equals("buycharges") || fromWhere.equals("sellcharges"))
          {  shipMode = rs.getString(2);}
        if(!"".equals(shipMode))
          {
              if("1".equals(shipMode))
              { shipMode  = "[A]";}
              if("2".equals(shipMode))
              { shipMode  = "[S]";}
              if("3".equals(shipMode))
              { shipMode  = "[AS]";}
              if("4".equals(shipMode))
              { shipMode  = "[T]";}
              if("5".equals(shipMode))
              { shipMode  = "[AT]";}
              if("6".equals(shipMode))
              { shipMode  = "[ST]";}
              if("7".equals(shipMode))
              { shipMode  = "[AST]";}
              
              chargeIdBuf = new StringBuffer(chargeId);
              chargeId = chargeIdBuf.insert(chargeIdBuf.indexOf("("),shipMode).toString();
              
          }
        chargeList.add(chargeId);
      }
    }catch(SQLException e)
    {
        //Logger.error(FILE_NAME,"getAllChargeIds()",e.toString());
        logger.error(FILE_NAME+"getAllChargeIds()"+e.toString());
        throw new EJBException();       
    }catch(Exception e)
    {
        //Logger.error(FILE_NAME,"getAllChargeIds()",e.toString());
        logger.error(FILE_NAME+"getAllChargeIds()"+e.toString());
        throw new EJBException();        
    }finally
    {
      try
      {
        if(rs!=null)
          { rs.close();}
        if(pstmt!=null)
          { pstmt.close();}
        if(connection!=null)
          { connection.close();}
      }catch(SQLException e)
      {
        //Logger.error(FILE_NAME,"getAllChargeIds()",e.toString());
        logger.error(FILE_NAME+"getAllChargeIds()"+e.toString());
        throw new EJBException();           
      }
    }
    return chargeList;
  }


  /**
   * 
   * @throws java.sql.SQLException
   * @return 
   * @param loginbean
   * @param chargeGroupingDOB
   */
  private boolean isValidChargeGroupId(ArrayList dataList,ESupplyGlobalParameters loginbean)throws SQLException
  {
    Connection connection     = null;
    PreparedStatement pstmt   = null;
    ResultSet         rs      = null;
    PreparedStatement pstmt1   = null;
    
    PreparedStatement pstmt2   = null;
    PreparedStatement pstmt3   = null;//Added by Anil.k for CR 231214 on 25Jan2011
    
    PreparedStatement pstmt4   = null;//Added by Anil.k for CR 231214 on 25Jan2011
   
    boolean    isValid        = true;
    int        dataListSize   = 0;
    ChargeGroupingDOB chargeGroupingDOB = null;
    boolean   chargeGroupId   = false;
    boolean   chargeId        = false;
    boolean   chargeDescId    = false;
    boolean   orgCountryId	  = false;//Added by Anil.k for CR 231214 on 25Jan2011
    boolean	  destCountryId	  = false;//Added by Anil.k for CR 231214 on 25Jan2011
    int    shipmentMode       = 0;
    String shipmodeStr        = "";
    
    
    try
    {
    
      StringBuffer       higherTerminals = new StringBuffer("");
      
          higherTerminals.append(" SELECT TERMINALID FROM FS_FR_TERMINALMASTER WHERE TERMINALID = ?");
          higherTerminals.append(" UNION");
          higherTerminals.append(" SELECT PARENT_TERMINAL_ID TERMINALID from fs_fr_terminal_regn");
          higherTerminals.append(" connect by prior PARENT_TERMINAL_ID=CHILD_TERMINAL_ID start with CHILD_TERMINAL_ID=?");
          higherTerminals.append(" UNION");
          higherTerminals.append(" SELECT TERMINALID FROM FS_FR_TERMINALMASTER WHERE OPER_ADMIN_FLAG='H'");

      StringBuffer      lowerTerminals  = new StringBuffer("");

      if("HO_TERMINAL".equals(loginbean.getAccessType()))
      {
      
          lowerTerminals.append(" SELECT TERMINALID  FROM FS_FR_TERMINALMASTER WHERE ACTV_FLAG='A' ");
          
      }else
      {
          lowerTerminals.append(" select CHILD_TERMINAL_ID TERMINALID from fs_fr_terminal_regn ");
          lowerTerminals.append(" connect by prior CHILD_TERMINAL_ID=PARENT_TERMINAL_ID start with PARENT_TERMINAL_ID=?");     
      }


      StringBuffer selectQry          = new StringBuffer("SELECT CHARGEGROUP_ID FROM QMS_CHARGE_GROUPSMASTER WHERE CHARGEGROUP_ID =? AND INACTIVATE='N' AND TERMINALID IN (");
                   selectQry.append(higherTerminals.toString());
                   selectQry.append(")");
                         
      
      
      StringBuffer updateChidData = new StringBuffer("  UPDATE QMS_CHARGE_GROUPSMASTER SET INACTIVATE = 'Y' WHERE CHARGEGROUP_ID = ? AND TERMINALID IN (");
                   updateChidData.append(lowerTerminals.toString());
                   updateChidData.append(") AND INACTIVATE = 'N' ");
                   
      connection    = operationsImpl.getConnection();
      
      dataListSize = dataList.size();
      chargeGroupingDOB = (ChargeGroupingDOB)dataList.get(0);
      shipmentMode = chargeGroupingDOB.getShipmentMode();
             
      if(shipmentMode==1)
      { shipmodeStr = "1,3,5,7";}
      else if(shipmentMode==2)
      { shipmodeStr = "2,3,6,7";}
      else if(shipmentMode==3)
      { shipmodeStr = "3,7";}
      else if(shipmentMode==4)
      { shipmodeStr = "4,5,6,7";}
      else if(shipmentMode==5)
      { shipmodeStr = "5,7";}
      else if(shipmentMode==6)
      { shipmodeStr = "6,7";}
      else if(shipmentMode==7)
      { shipmodeStr = "7";}      
      
      String selectChargeID     = "SELECT CHARGE_ID FROM QMS_CHARGESMASTER WHERE CHARGE_ID = ? AND SHIPMENT_MODE IN ("+shipmodeStr+") AND INVALIDATE='F'";
      
      StringBuffer selectChargeDescId = new StringBuffer("SELECT  CHARGEDESCID   FROM QMS_CHARGEDESCMASTER  WHERE CHARGEID =? AND CHARGEDESCID=? AND SHIPMENTMODE IN("+shipmodeStr+") AND TERMINALID IN (");
                   selectChargeDescId.append(higherTerminals);
                   selectChargeDescId.append(")");

      //String orgCountry = "SELECT * FROM FS_COUNTRYMASTER FC WHERE FC.COUNTRYID='"+chargeGroupingDOB.getOriginCountry()+"'";//Added by Anil.k for CR 231214 on 25Jan2011
      //String destCountry = "SELECT * FROM FS_COUNTRYMASTER FC WHERE FC.COUNTRYID='"+chargeGroupingDOB.getDestinationCountry()+"'";//Added by Anil.k for CR 231214 on 25Jan2011

      pstmt         = connection.prepareStatement(selectQry.toString());
      String chargeGroupIdStr  = chargeGroupingDOB.getChargeGroup();
      String terminalId     = chargeGroupingDOB.getTerminalId();
      
      pstmt.setString(1,chargeGroupIdStr);
      pstmt.setString(2,terminalId);
      pstmt.setString(3,terminalId);
      
      rs  = pstmt.executeQuery();
      if(rs.next())
      {
        chargeGroupId = false;
      }else
      { chargeGroupId = true;}

       if(rs!=null)
        { rs.close();}     
      if(pstmt!=null)
        { pstmt.close();}      
        
      if(chargeGroupId)
      {   
          pstmt1         = connection.prepareStatement(selectChargeID);
          pstmt2         = connection.prepareStatement(selectChargeDescId.toString());
          
          for(int i=0;i<dataListSize;i++)
          {
            chargeGroupingDOB = (ChargeGroupingDOB)dataList.get(i);
            if(chargeGroupingDOB!=null)
            {
              pstmt1.clearParameters();
              pstmt1.setString(1,chargeGroupingDOB.getChargeIds());
              rs = pstmt1.executeQuery();
              if(rs.next())
              { chargeId = true;}
              else
              { chargeId = false;}
              
              if(rs!=null)
              { rs.close();}
              
              pstmt2.clearParameters();
              pstmt2.setString(1,chargeGroupingDOB.getChargeIds());
              pstmt2.setString(2,chargeGroupingDOB.getChargeDescId());
              pstmt2.setString(3,chargeGroupingDOB.getTerminalId());
              pstmt2.setString(4,chargeGroupingDOB.getTerminalId());
              rs = pstmt2.executeQuery();
              if(rs.next())
              { chargeDescId = true;}
              else
              { chargeDescId = false;}
              
              if(rs!=null)
              { rs.close();}
            }
            
            if(!chargeId || !chargeDescId )
            { break;}
          }
      }
      //Added by Anil.k for CR 231214 on 25Jan2011
      /*if(chargeGroupId && chargeId && chargeDescId )
      { 
    	  pstmt3         = connection.prepareStatement(orgCountry);
          pstmt4         = connection.prepareStatement(destCountry);                
          rs = pstmt3.executeQuery();
          if(rs.next())
          { orgCountryId = true;}
          else
          { orgCountryId = false;}
                             
          rs = pstmt4.executeQuery();
          if(rs.next())
          { destCountryId = true;}
          else
          { destCountryId = false;}
          
          if(rs!=null)
          { rs.close();}        
    	  
      }*///Ended by Anil.k for CR 231214 on 25Jan2011
      if(chargeGroupId && chargeId && chargeDescId)
      { 
        if(pstmt!=null)
          { pstmt.close();}
        

        pstmt = connection.prepareStatement(updateChidData.toString());
        pstmt.setString(1,chargeGroupIdStr);
        if(!"HO_TERMINAL".equals(loginbean.getAccessType()))
        {
          pstmt.setString(2,terminalId);
        }
        pstmt.executeUpdate();
        
        isValid = true;
      }
      else
      { isValid = false;}
      
    }catch(SQLException e)
    {
        //Logger.error(FILE_NAME,"------->isValidChargeGroupId()",e.toString());
        logger.error(FILE_NAME+"------->isValidChargeGroupId()"+e.toString());
        throw new EJBException();         
    }catch(Exception e)
    {
        //Logger.error(FILE_NAME,"------->isValidChargeGroupId()",e.toString());
        logger.error(FILE_NAME+"------->isValidChargeGroupId()"+e.toString());
        throw new EJBException();         
    }finally
    {
      try
      {
        if(rs!=null)
          { rs.close();}
        if(pstmt!=null)
          { pstmt.close();}
        if(pstmt1!=null)
          { pstmt1.close();}
        if(pstmt2!=null)
          { pstmt2.close();}
        if(pstmt3!=null)
        {pstmt3.close();} // Added by Gowtham on 17Feb2011 for connection Leaks.
        if(pstmt4!=null)
        {pstmt4.close();}// Added by Gowtham on 17Feb2011 for connection Leaks.
        if(connection!=null)
          { connection.close();}
      }catch(SQLException e)
      {
        //Logger.error(FILE_NAME,"------->isValidChargeGroupId()",e.toString());
        logger.error(FILE_NAME+"------->isValidChargeGroupId()"+e.toString());
        throw new EJBException();
      }
    }
    return isValid;    
  }
 
  /**
   * 
   * @throws javax.ejb.EJBException
   * @return 
   * @param loginbean
   * @param dataList
   */
  public boolean insertChargesGroupDtls(ArrayList dataList,ESupplyGlobalParameters loginbean)throws EJBException
  {
    /*ArrayList insertedList    = new ArrayList();
    ArrayList invalidList     = new ArrayList();
    ArrayList returnList      = new ArrayList();*/
    //ChargeGroupEntityBeanLocalHome home  = null;
    //ChargeGroupEntityBeanLocal      local = null;
    ChargeGroupingDAO chargeGroupingDAO       = null;
    ChargeGroupingDOB chargeGroupingDOB =null;
    boolean   success  = false;
    try
    {
 /*        chargeGroupingDOB  = (ChargeGroupingDOB)dataList.get(0);
       if(isValidChargeGroupId(dataList,loginbean))
        {
          insertedList.add(chargeGroupingDOB);
        }else
        {
          invalidList.add(chargeGroupingDOB);
        }*/
      //returnList.add(insertedList);
      //returnList.add(invalidList);
      if(dataList!=null && dataList.size()>0)
      {
        if(isValidChargeGroupId(dataList,loginbean))
        {      
          //home  = (ChargeGroupEntityBeanLocalHome)LookUpBean.getEJBLocalHome("java:comp/env/ChargeGroupEntityBean");
          //local = (ChargeGroupEntityBeanLocal)home.create(insertedList);
          chargeGroupingDAO = new ChargeGroupingDAO();
          chargeGroupingDAO.create(dataList);
          success = true;
        }
      }     
    }
    catch(Exception e)
    {
        //Logger.error(FILE_NAME,"insertChargesGroupDtls()",e.toString());
        logger.error(FILE_NAME+"insertChargesGroupDtls()"+e.toString());
        throw new EJBException();      
    }
      return success;
  }

  // to validate the teminal ids weather it is in that hirarchy or not.
  // Modify -- list of all the child terminals.
  // View   -- list of all the teminal list in the hirarchy.
  /**
   * 
   * @throws javax.ejb.EJBException
   * @return 
   * @param currTerminalId
   * @param successList
   */
  public HashMap uploadChargeGroupDetails (ArrayList successList,String process, String terminalId) throws EJBException
  {
    HashMap map   =   null;
    try
    {
      if("Add".equalsIgnoreCase(process))
          map = addChargeGroupDetails(successList,terminalId);
      else
          map = updateChargeGroupDetails(successList,terminalId);
    }
    catch (EJBException ejb)
    {
      ejb.printStackTrace();
      //Logger.error(FILE_NAME,"EJBException in uploadChargeGroupDetails "+ejb);
      logger.error(FILE_NAME+"EJBException in uploadChargeGroupDetails "+ejb);
      throw new EJBException(ejb);
    }
    catch (Exception e)
    {
      e.printStackTrace();
      //Logger.error(FILE_NAME,"Exception in uploadChargeGroupDetails "+e);
      logger.error(FILE_NAME+"Exception in uploadChargeGroupDetails "+e);
      throw new EJBException(e);
    }
    return map;
  }
  private HashMap addChargeGroupDetails (ArrayList successList, String currentTerminalId) throws EJBException
  {
    HashMap             map           =   new HashMap();
    Connection          conn          =   null;
    PreparedStatement   pstmt         =   null;
    CallableStatement   cstmt         =   null;
    ResultSet           rs            =   null;
    ResultSet           rs1           =   null;
    int                 size;
    ChargeGroupingDOB   chargeGroupDOB=   null;
    ArrayList           insertedList  =   new ArrayList();
    ArrayList           failedList    =   new ArrayList();
    
    //String              truncateQry       =   "TRUNCATE TABLE TEMP_CHARGES";//@@Global Temp Table
    //String              insertQry         =   "INSERT INTO TEMP_CHARGES (CHARGE_GROUP_ID,CHARGE_ID,CHARGEDESCID,SHIPMENTMODE,TERMINALID,SHMODE)VALUES (?,?,?,?,?,?)";
    String              insertQry         =   "INSERT INTO TEMP_CHARGES_GROUP (CHARGE_GROUP_ID,CHARGE_ID,CHARGEDESCID,SHIPMENTMODE,TERMINALID,SHMODE,ORIGINCOUNTRY,DESTINATIONCOUNTRY)VALUES (?,?,?,?,?,?,?,?)";//Modified by Anil.k for Enhancement 231214 on 25Jan2011
    String              chargeGrpInsert   =   "INSERT INTO QMS_CHARGE_GROUPSMASTER (CHARGEGROUP_ID,CHARGE_ID,SHIPMENT_MODE,CHARGEDESCID,TERMINALID,INVALIDATE,INACTIVATE,ID,ORIGINCOUNTRY,DESTINATIONCOUNTRY) VALUES (?,?,?,?,?,'F','N',CHARGE_GROUPSMASTER_SEQ.NEXTVAL,?,?)";//Modified by Anil.k for Enhancement 231214 on 25Jan2011
    
    try
    {
      if(successList!=null && successList.size()>0)
      {
        conn    =   getConnection();
        size    =   successList.size();
        /*pstmt   =   conn.prepareStatement(truncateQry);
        pstmt.execute();
      
        if(pstmt != null)
          pstmt.close();*/
          
        cstmt   =   conn.prepareCall("{CALL TRUNCATE_PROC()} "); 
          
        cstmt.execute();
        
        if(cstmt!=null)
          cstmt.close();
          
        pstmt  =  conn.prepareStatement(insertQry);
        for(int i=0;i<size;i++)
        {
          chargeGroupDOB = (ChargeGroupingDOB)successList.get(i);
          if(chargeGroupDOB!=null)
          {
            pstmt.clearParameters();
            pstmt.setString(1,chargeGroupDOB.getChargeGroup());
            pstmt.setString(2,chargeGroupDOB.getChargeIds());
            pstmt.setString(3,chargeGroupDOB.getChargeDescId());
            pstmt.setString(4,""+chargeGroupDOB.getShipmentMode());
            pstmt.setString(5,chargeGroupDOB.getTerminalId());
            pstmt.setString(6,chargeGroupDOB.getShipModeString());
            pstmt.setString(7, chargeGroupDOB.getOriginCountry());//Added by Anil.k for Enhancement 231214 on 25Jan2011
            pstmt.setString(8, chargeGroupDOB.getDestinationCountry());//Added by Anil.k for Enhancement 231214 on 25Jan2011
           // pstmt.executeUpdate();
            pstmt.addBatch();
          }
        }
       pstmt.executeBatch();
        
      if(pstmt!=null) 
      pstmt.close();
        
        cstmt   =   conn.prepareCall("{call PKG_QMS_CHARGES.CHARGEGROUP_DTL_PROC(?,?,?,?)}");
        cstmt.setString(1,"Add");
        cstmt.setString(2,currentTerminalId);
        cstmt.registerOutParameter(3,OracleTypes.CURSOR);
        cstmt.registerOutParameter(4,OracleTypes.CURSOR);
        
        cstmt.execute();
        
        rs  =   (ResultSet)cstmt.getObject(3);
        rs1 =   (ResultSet)cstmt.getObject(4);
        
        while (rs.next())
        {
          chargeGroupDOB    =   new ChargeGroupingDOB();
          chargeGroupDOB.setChargeGroup(rs.getString("CHARGE_GROUP_ID"));
          chargeGroupDOB.setChargeIds(rs.getString("CHARGE_ID"));
          chargeGroupDOB.setChargeDescId(rs.getString("CHARGEDESCID"));
          chargeGroupDOB.setShipmentMode(rs.getInt("SHIPMENTMODE"));
          chargeGroupDOB.setTerminalId(rs.getString("TERMINALID"));
          chargeGroupDOB.setShipModeString(rs.getString("SHMODE"));
          chargeGroupDOB.setOriginCountry(rs.getString("ORIGINCOUNTRY"));//Added by Anil.k for Enhancement 231214 on 25Jan2011
          chargeGroupDOB.setDestinationCountry(rs.getString("DESTINATIONCOUNTRY"));//Added by Anil.k for Enhancement 231214 on 25Jan2011
          insertedList.add(chargeGroupDOB);
        }
        
        while (rs1.next())
        {
          chargeGroupDOB    =   new ChargeGroupingDOB();
          chargeGroupDOB.setChargeGroup(rs1.getString("CHARGE_GROUP_ID"));
          chargeGroupDOB.setChargeIds(rs1.getString("CHARGE_ID"));
          chargeGroupDOB.setChargeDescId(rs1.getString("CHARGEDESCID"));
          chargeGroupDOB.setShipmentMode(rs1.getInt("SHIPMENTMODE"));
          chargeGroupDOB.setTerminalId(rs1.getString("TERMINALID"));
          chargeGroupDOB.setShipModeString(rs1.getString("SHMODE"));
          chargeGroupDOB.setRemarks(rs1.getString("NOTES"));
          chargeGroupDOB.setOriginCountry(rs1.getString("ORIGINCOUNTRY"));//Added by Anil.k for Enhancement 231214 on 25Jan2011
          chargeGroupDOB.setDestinationCountry(rs1.getString("DESTINATIONCOUNTRY"));//Added by Anil.k for Enhancement 231214 on 25Jan2011
          failedList.add(chargeGroupDOB);
        }
        
          if(insertedList.size()>0)
          {
            size  =  insertedList.size();
            pstmt =  conn.prepareStatement(chargeGrpInsert);
            for(int i=0;i<size;i++)
            {
              chargeGroupDOB    =   (ChargeGroupingDOB)insertedList.get(i);
              pstmt.clearParameters();
              pstmt.setString(1,chargeGroupDOB.getChargeGroup());
              pstmt.setString(2,chargeGroupDOB.getChargeIds());
              pstmt.setInt(3,chargeGroupDOB.getShipmentMode());
              pstmt.setString(4,chargeGroupDOB.getChargeDescId());
              pstmt.setString(5,chargeGroupDOB.getTerminalId());
              pstmt.setString(6,chargeGroupDOB.getOriginCountry());//Added by Anil.k for Enhancement 231214 on 25Jan2011
              pstmt.setString(7,chargeGroupDOB.getDestinationCountry());//Added by Anil.k for Enhancement 231214 on 25Jan2011
              pstmt.addBatch();
            }
            pstmt.executeBatch();
          }
          map.put("NONEXISTS",insertedList);
          map.put("EXISTS",failedList);
      }
    }
    catch (EJBException e)
    {
      e.printStackTrace();
      //Logger.error(FILE_NAME,"EJBException in addChargeGroupDetails "+e);
      logger.error(FILE_NAME+"EJBException in addChargeGroupDetails "+e);
      throw new EJBException(e);
    }
    catch (Exception e)
    {
      e.printStackTrace();
      //Logger.error(FILE_NAME,"Exception in addChargeGroupDetails "+e);
      logger.error(FILE_NAME+"Exception in addChargeGroupDetails "+e);
      throw new EJBException(e);
    }
    finally
    {
      try
      {
      //conn.commit();
         if(pstmt != null)
          pstmt.close();
         if(rs !=null)
          rs.close();
         if(rs1 != null)
          rs1.close();
         if(cstmt!=null)
          cstmt.close();
        if(conn!=null)
          conn.close();
          //conn.commit();
      }
      catch(Exception e)
      {
        e.printStackTrace();
        //Logger.error(FILE_NAME,"Error While Closing Opened Resources. "+e);
        logger.error(FILE_NAME+"Error While Closing Opened Resources. "+e);
      }
    }
    return map;
  }
  private HashMap updateChargeGroupDetails(ArrayList successList, String currentTerminalId) throws EJBException
  {
    HashMap             map           =   new HashMap();
    Connection          conn          =   null;
    PreparedStatement   pstmt         =   null;
    PreparedStatement   pstmt1        =   null;
    CallableStatement   cstmt         =   null;
    ResultSet           rs            =   null;
    ResultSet           rs1           =   null;
    int                 size;
    ChargeGroupingDOB   chargeGroupDOB=   null;
    ArrayList           insertedList  =   new ArrayList();
    ArrayList           failedList    =   new ArrayList();
    HashMap             checkMap      =   new HashMap();
    
    //String              truncateQry       =   "TRUNCATE TABLE TEMP_CHARGES";//@@Global Temp Table
    //String              insertQry         =   "INSERT INTO TEMP_CHARGES (CHARGE_GROUP_ID,CHARGE_ID,CHARGEDESCID,TERMINALID)VALUES (?,?,?,?)";
    String              insertQry         =   "INSERT INTO TEMP_CHARGES_GROUP (CHARGE_GROUP_ID,CHARGE_ID,CHARGEDESCID,TERMINALID,ORIGINCOUNTRY,DESTINATIONCOUNTRY)VALUES (?,?,?,?,?,?)";//Modified by Anil.k for Enhancement 231214 on 25Jan2011
    String              updateQry         =   "UPDATE QMS_CHARGE_GROUPSMASTER SET INACTIVATE='Y' WHERE CHARGEGROUP_ID=? AND TERMINALID=? AND INACTIVATE = 'N' ";
    String              chargeGrpInsert   =   "INSERT INTO QMS_CHARGE_GROUPSMASTER (CHARGEGROUP_ID,CHARGE_ID,SHIPMENT_MODE,CHARGEDESCID,TERMINALID,INVALIDATE,INACTIVATE,ID,ORIGINCOUNTRY,DESTINATIONCOUNTRY) VALUES (?,?,?,?,?,'F','N',CHARGE_GROUPSMASTER_SEQ.NEXTVAL,?,?)";
    
    try
    {
      if(successList!=null && successList.size()>0)
      {
        conn    =   getConnection();
        size    =   successList.size();
        /*pstmt   =   conn.prepareStatement(truncateQry);
        pstmt.execute();
      
        if(pstmt != null)
          pstmt.close();*/
        
        cstmt   =   conn.prepareCall("{CALL TRUNCATE_PROC()} "); 
          
        cstmt.execute();
        
        if(cstmt!=null)
          cstmt.close();
          
        pstmt  =  conn.prepareStatement(insertQry);
        for(int i=0;i<size;i++)
        {
          chargeGroupDOB = (ChargeGroupingDOB)successList.get(i);
          if(chargeGroupDOB!=null)
          {
            pstmt.clearParameters();
            pstmt.setString(1,chargeGroupDOB.getChargeGroup());
            pstmt.setString(2,chargeGroupDOB.getChargeIds());
            pstmt.setString(3,chargeGroupDOB.getChargeDescId());
            pstmt.setString(4,chargeGroupDOB.getTerminalId());
            pstmt.setString(5, chargeGroupDOB.getOriginCountry());//Added by Anil.k for Enhancement 231214 on 25Jan2011
            pstmt.setString(6, chargeGroupDOB.getDestinationCountry());//Added by Anil.k for Enhancement 231214 on 25Jan2011
            pstmt.addBatch();
          }
        }
        pstmt.executeBatch();
        
        if(pstmt!=null)
          pstmt.close();
        
        cstmt   =   conn.prepareCall("{call PKG_QMS_CHARGES.CHARGEGROUP_DTL_PROC(?,?,?,?)}");
        cstmt.setString(1,"Modify");
        cstmt.setString(2,currentTerminalId);
        cstmt.registerOutParameter(3,OracleTypes.CURSOR);
        cstmt.registerOutParameter(4,OracleTypes.CURSOR);
        
        cstmt.execute();
        
        rs  =   (ResultSet)cstmt.getObject(3);
        rs1 =   (ResultSet)cstmt.getObject(4);
        
        while (rs.next())//@@Valid List
        {
          chargeGroupDOB    =   new ChargeGroupingDOB();
          chargeGroupDOB.setChargeGroup(rs.getString("CHARGE_GROUP_ID"));
          chargeGroupDOB.setChargeIds(rs.getString("CHARGE_ID"));
          chargeGroupDOB.setChargeDescId(rs.getString("CHARGEDESCID"));
          chargeGroupDOB.setTerminalId(rs.getString("TERMINALID"));
          chargeGroupDOB.setShipmentMode(rs.getInt("SHIPMENTMODE"));
          chargeGroupDOB.setOriginCountry(rs.getString("ORIGINCOUNTRY"));//Added by Anil.k for Enhancement 231214 on 25Jan2011
          chargeGroupDOB.setDestinationCountry(rs.getString("DESTINATIONCOUNTRY"));//Added by Anil.k for Enhancement 231214 on 25Jan2011
          insertedList.add(chargeGroupDOB);
        }
        while (rs1.next())//@@Invalid List, to be displayed back to the user.
        {
          chargeGroupDOB    =   new ChargeGroupingDOB();
          chargeGroupDOB.setChargeGroup(rs1.getString("CHARGE_GROUP_ID"));
          chargeGroupDOB.setChargeIds(rs1.getString("CHARGE_ID"));
          chargeGroupDOB.setChargeDescId(rs1.getString("CHARGEDESCID"));
          chargeGroupDOB.setTerminalId(rs1.getString("TERMINALID"));
          chargeGroupDOB.setRemarks(rs1.getString("NOTES"));
          chargeGroupDOB.setOriginCountry(rs1.getString("ORIGINCOUNTRY"));//Added by Anil.k for Enhancement 231214 on 25Jan2011
          chargeGroupDOB.setDestinationCountry(rs1.getString("DESTINATIONCOUNTRY"));//Added by Anil.k for Enhancement 231214 on 25Jan2011
          failedList.add(chargeGroupDOB);
        }
        if(insertedList.size()>0)
        {
          size    =  insertedList.size();
          pstmt1  =  conn.prepareStatement(updateQry);
          pstmt   =  conn.prepareStatement(chargeGrpInsert);
          for(int i=0;i<size;i++)
          {
            chargeGroupDOB    =   (ChargeGroupingDOB)insertedList.get(i);
            
            if(!checkMap.containsKey(chargeGroupDOB.getChargeGroup()+chargeGroupDOB.getTerminalId()))
            {
              checkMap.put(chargeGroupDOB.getChargeGroup()+chargeGroupDOB.getTerminalId(),"");
              pstmt1.clearParameters();
              pstmt1.setString(1,chargeGroupDOB.getChargeGroup());
              pstmt1.setString(2,chargeGroupDOB.getTerminalId());
              pstmt1.addBatch();
            }
            
            pstmt.clearParameters();
            pstmt.setString(1,chargeGroupDOB.getChargeGroup());
            pstmt.setString(2,chargeGroupDOB.getChargeIds());
            pstmt.setInt(3,chargeGroupDOB.getShipmentMode());
            pstmt.setString(4,chargeGroupDOB.getChargeDescId());
            pstmt.setString(5,chargeGroupDOB.getTerminalId());
            pstmt.setString(6,chargeGroupDOB.getOriginCountry());//Added by Anil.k for Enhancement 231214 on 25Jan2011
            pstmt.setString(7,chargeGroupDOB.getDestinationCountry());//Added by Anil.k for Enhancement 231214 on 25Jan2011
            pstmt.addBatch();
          }
          pstmt1.executeBatch();
          pstmt.executeBatch();
        }
          map.put("NONEXISTS",insertedList);
          map.put("EXISTS",failedList);
      }
    }
    catch(Exception e)
    {
      e.printStackTrace();
      //Logger.error(FILE_NAME,"Exception in updateChargeGroupDetails "+e);
      logger.error(FILE_NAME+"Exception in updateChargeGroupDetails "+e);
      throw new EJBException(e);
    }
    finally
    {
      try
      {
         if(pstmt != null)
          pstmt.close();
        if(pstmt1!=null)
          pstmt1.close();
         if(rs !=null)
          rs.close();
         if(rs1 != null)
          rs1.close();
         if(cstmt!=null)
          cstmt.close();
        if(conn!=null)
          conn.close();
      }
      catch(Exception e)
      {
        e.printStackTrace();
        //Logger.error(FILE_NAME,"Error While Closing Opened Resources. "+e);
        logger.error(FILE_NAME+"Error While Closing Opened Resources. "+e);
      }
    }
    return map;
  }
  private boolean validateLoadDetails(BuySellChargesEnterIdDOB buySellChargesEnterIdDOB,String currentTerminalId)
  { 
    boolean   flag    = false;
    Connection    connection  = null;
    CallableStatement       cstmt = null;
   // ResultSet               rs  = null;
   String rs = null;
    try
    {
          connection  = operationsImpl.getConnection();
          cstmt   = connection.prepareCall("{ call PKG_QMS_CHARGES.ISEXISTINTHEHIRARCHY(?,?,?,?)}");
          cstmt.setString(1,buySellChargesEnterIdDOB.getOperation());
          cstmt.setString(2,buySellChargesEnterIdDOB.getTerminalId());
          cstmt.setString(3,currentTerminalId);
          cstmt.registerOutParameter(4,Types.VARCHAR);
          cstmt.execute();
          
          rs  = (String)cstmt.getObject(4);
          
          if("1".equals(rs))
          { flag= true;}
          else
          { flag=false;}

    }catch(Exception e)
    {
      //Logger.info(FILE_NAME,"exceptione in validateLoadDetails()"+e.toString());
      logger.info(FILE_NAME+"exceptione in validateLoadDetails()"+e.toString());
    }finally
    {
      try{
          if(cstmt!=null)
          { cstmt.close();}
          if(connection!=null)
          { connection.close();}
      }catch(SQLException e)
      {
        //Logger.info(FILE_NAME,"exceptione in validateLoadDetails()"+e);
        logger.info(FILE_NAME+"exceptione in validateLoadDetails()"+e);
      }
    }
    return flag;
  }  
  /**
   * 
   * @throws javax.ejb.EJBException
   * @throws javax.ejb.ObjectNotFoundException
   * @return 
   * @param loginbean
   * @param chargeGroupId
   */
 // public ArrayList getChargeGroupDtl(String chargeGroupId,ESupplyGlobalParameters loginbean)throws ObjectNotFoundException,EJBException
  public ArrayList getChargeGroupDtl(BuySellChargesEnterIdDOB buySellChargesEnterIdDOB,ESupplyGlobalParameters loginbean)throws ObjectNotFoundException,EJBException
  {
    //ChargeGroupEntityBeanLocalHome home  = null;
    //ChargeGroupEntityBeanLocal      local = null;
    ChargeGroupingDOB chargeGroupingDOB =null;
    ChargeGroupingDAO chargeGroupingDAO =new ChargeGroupingDAO();
    //ChargeGroupEntityBeanPK       pkObj   = new ChargeGroupEntityBeanPK();
    ArrayList dataList  = null;

    try
    {
        //pkObj.chargeGroup      = chargeGroupId;
       // home  = (ChargeGroupEntityBeanLocalHome)LookUpBean.getEJBLocalHome("java:comp/env/ChargeGroupEntityBean");
        //local = (ChargeGroupEntityBeanLocal)home.findByPrimaryKey(pkObj);
        //chargeGroupingDOB  = local.getChagesGroupDOB();
        String chargeGroupId  = buySellChargesEnterIdDOB.getChargeGroupId();
        String terminalId     = buySellChargesEnterIdDOB.getTerminalId();
        String fromWhere      = buySellChargesEnterIdDOB.getFromWhere();
        if(chargeGroupId!=null &&  !"".equals(chargeGroupId))
        {
            if(validateLoadDetails(buySellChargesEnterIdDOB,loginbean.getTerminalId()))
            {
              
              dataList = chargeGroupingDAO.load(chargeGroupId,terminalId,fromWhere,loginbean.getAccessType());
              if(dataList==null || dataList.size()<=0)
              {
                  throw new ObjectNotFoundException();
              }
            }else
            {
              throw new ObjectNotFoundException();
            }
        }
        
    }catch(ObjectNotFoundException e)
    {
        //Logger.error(FILE_NAME,"getChargeGroupDtl()",e.toString());
        logger.error(FILE_NAME+"getChargeGroupDtl()"+e.toString());
        throw new ObjectNotFoundException("Bean could not find");      
    }catch(Exception e)
    {
        //Logger.error(FILE_NAME,"getChargeGroupDtl()",e.toString());
        logger.error(FILE_NAME+"getChargeGroupDtl()"+e.toString());
        throw new EJBException();      
    }
    
    return dataList;
  }
  /**
   * 
   * @throws javax.ejb.ObjectNotFoundException
   * @throws javax.ejb.EJBException
   * @return 
   * @param loginbean
   * @param chargeGroupingDOB
   */
  public boolean updateChargesGroupDetails(ArrayList dataList,ESupplyGlobalParameters loginbean)throws EJBException,ObjectNotFoundException
  {
    /*ChargeGroupEntityBeanLocalHome home  = null;
    ChargeGroupEntityBeanLocal      local = null;
    ChargeGroupEntityBeanPK       pkObj   = new ChargeGroupEntityBeanPK();*/
    boolean   success                     = false;
    ChargeGroupingDAO  chargeGroupingDAO  = new ChargeGroupingDAO();
    try
    {
      /*pkObj.chargeGroup  = chargeGroupingDOB.getChargeGroup();
      home  = (ChargeGroupEntityBeanLocalHome)LookUpBean.getEJBLocalHome("java:comp/env/ChargeGroupEntityBean");
      local = (ChargeGroupEntityBeanLocal)home.findByPrimaryKey(pkObj);
      local.setChargeGroupDOB(chargeGroupingDOB);*/
      if(dataList!=null && dataList.size()>0)
      {
    	  if(isValidChargeGroupId(dataList,loginbean,"Modify"))//Added by Anil.k for Cr 231214 on 25Jan2011
          {
        chargeGroupingDAO.store(dataList);
        success = true;
      }
    	  else
    		  success = false;//Added by Anil.k for Cr 231214 on 25Jan2011
      }
     // success = true;
    }catch(ObjectNotFoundException e)
    {
        success = false;    
        //Logger.error(FILE_NAME,"updateChargesGroupDetails()",e.toString());
        logger.error(FILE_NAME+"updateChargesGroupDetails()"+e.toString());
        throw new ObjectNotFoundException("Bean could not find");      
    }catch(Exception e)
    {
        success = false;    
        //Logger.error(FILE_NAME,"updateChargesGroupDetails()",e.toString());
        logger.error(FILE_NAME+"updateChargesGroupDetails()"+e.toString());
        throw new EJBException();     
    }
    return success;
  }  
  /**
   * 
   * @throws javax.ejb.ObjectNotFoundException
   * @throws javax.ejb.EJBException
   * @return 
   * @param loginbean
   * @param chargeGroupingDOB
   */
  //@@Commented & Added the signature by subrahmanyam for the pbn id: 201931 on 05-04-2010
//public boolean deleteChargesGroupDtls(String chargeGroupId,ESupplyGlobalParameters loginbean)throws EJBException,ObjectNotFoundException  
   public ArrayList deleteChargesGroupDtls(String chargeGroupId,ESupplyGlobalParameters loginbean)throws EJBException,ObjectNotFoundException
  {
    boolean   success                     = false;
    ArrayList	delStatus				  = new ArrayList(2);//@@Added by subrahmanyam for the pbn id: 201931 on 05-04-2010
    ChargeGroupingDAO  chargeGroupingDAO  = new ChargeGroupingDAO();
    int removed = 0;
    try
    {
      removed = chargeGroupingDAO.remove(chargeGroupId,loginbean.getTerminalId());
      if(removed>0)
      { success = true;
    //@@Added by subrahmanyam for the pbn id: 201931 on 05-04-2010
      	delStatus.add(success);
      	delStatus.add(removed);
        //@@Added by subrahmanyam for the pbn id: 201931 on 05-04-2010      	
      }
      else
      { throw new ObjectNotFoundException();}
    }catch(ObjectNotFoundException e)
    {
        success = false;    
        //Logger.error(FILE_NAME,"deleteChargesGroupDtls()",e.toString());
        logger.error(FILE_NAME+"deleteChargesGroupDtls()"+e.toString());
        throw new ObjectNotFoundException("Bean could not find");      
    }catch(Exception e)
    {
        success = false;    
        //Logger.error(FILE_NAME,"deleteChargesGroupDtls()",e.toString());
        logger.error(FILE_NAME+"deleteChargesGroupDtls()"+e.toString());
        throw new EJBException();     
    }
  //@@Commented & Added by subrahmanyam for the pbn id: 201931 on 05-04-2010
   //return success;
   return delStatus;
  }  
  /**
   * 
   * @throws java.sql.SQLException
   * @return 
   * @param loginbean
   * @param searchStr
   */
  public ArrayList  getAllChargeGroupIds(String searchStr, String terminalId, String shipmentMode, String accessLevel)throws SQLException
  {
    PreparedStatement pstmt = null;
    Connection  connection  = null;
    ResultSet   rs          = null;
    ArrayList   chargeGroupList = new ArrayList();
    StringBuffer      terminalQry = new StringBuffer();
    String selectQry        = "";
    String  shipment  = "";
    
    
    try
    {
      if("H".equalsIgnoreCase(accessLevel))
      {
        terminalQry.append( " (SELECT terminalid term_id FROM FS_FR_TERMINALMASTER)");
      }
      else
      {
        terminalQry.append( "(SELECT parent_terminal_id term_id FROM FS_FR_TERMINAL_REGN CONNECT BY child_terminal_id = PRIOR parent_terminal_id START WITH child_terminal_id = ?")
                   .append( " UNION ")
                   .append( " SELECT terminalid term_id FROM FS_FR_TERMINALMASTER WHERE oper_admin_flag = 'H' ")
                   .append( " UNION ")
                   .append( " SELECT ? term_id FROM DUAL ")
                   .append( " UNION ")
                   .append( " SELECT child_terminal_id term_id FROM FS_FR_TERMINAL_REGN CONNECT BY PRIOR child_terminal_id = parent_terminal_id START WITH parent_terminal_id = ?)");
      }
      
      if("Truck".equalsIgnoreCase(shipmentMode)){
        shipment = " AND SHIPMENT_MODE IN (4,5,6,7) ";
      }else if("Air".equalsIgnoreCase(shipmentMode)){
        shipment = " AND SHIPMENT_MODE IN (1,3,5,7) ";
      }else if("Sea".equalsIgnoreCase(shipmentMode)){
        shipment = " AND SHIPMENT_MODE IN (2,3,6,7) ";
      }
      
//@@ Modified by subrahmanyam for the pbn id: 210495 on 12-Jul-10
      selectQry        = "SELECT DISTINCT CHARGEGROUP_ID FROM QMS_CHARGE_GROUPSMASTER WHERE CHARGEGROUP_ID LIKE '"+searchStr+"%' "+shipment+" AND INVALIDATE='F' AND INACTIVATE='N' AND TERMINALID IN "+terminalQry.toString()+" ORDER BY CHARGEGROUP_ID";
      
      connection  = operationsImpl.getConnection();
      pstmt       = connection.prepareStatement(selectQry);
      if(!"H".equalsIgnoreCase(accessLevel))
      {
        pstmt.setString(1,terminalId);
        pstmt.setString(2,terminalId);
        pstmt.setString(3,terminalId);
      }
      rs          = pstmt.executeQuery();
      while(rs.next())
      {
       chargeGroupList.add(rs.getString("CHARGEGROUP_ID"));
      }
    }catch(SQLException e)
    {
        //Logger.error(FILE_NAME,"------->getAllChargeGroupIds()",e.toString());
        logger.error(FILE_NAME+"------->getAllChargeGroupIds()"+e.toString());
        throw new EJBException();         
    }catch(Exception e)
    {
        //Logger.error(FILE_NAME,"------->getAllChargeGroupIds()",e.toString());
        logger.error(FILE_NAME+"------->getAllChargeGroupIds()"+e.toString());
        throw new EJBException();         
    }finally
    {
      try
      {
        if(rs!=null)
          { rs.close();}
        if(pstmt!=null)
          { pstmt.close();}
        if(connection!=null)
          { connection.close();}
      }catch(SQLException e)
      {
        //Logger.error(FILE_NAME,"------->getAllChargeGroupIds()",e.toString());
        logger.error(FILE_NAME+"------->getAllChargeGroupIds()"+e.toString());
        throw new SQLException();
      }
    }
    return chargeGroupList;
  }
 public ArrayList  getAllChargeGroupIds(BuySellChargesEnterIdDOB buySellChargesEnterIdDOB,ESupplyGlobalParameters loginbean)throws SQLException
  {
    PreparedStatement pstmt = null;
    Connection  connection  = null;
    ResultSet   rs          = null;
    ArrayList   chargeGroupList = new ArrayList();
   try 
    {
 StringBuffer  selectQry =null; //Chnged by VLAKSHMI for CR #170761 on 20090626
    //Chnged by VLAKSHMI for CR #170761 on 20090626
   if(buySellChargesEnterIdDOB.getChargeId().length()>0 && buySellChargesEnterIdDOB.getChargeDescId().length()>0)
    {
    
       selectQry        = new StringBuffer("SELECT DISTINCT CHARGEGROUP_ID FROM QMS_CHARGE_GROUPSMASTER WHERE INACTIVATE='N' AND CHARGE_ID='"+buySellChargesEnterIdDOB.getChargeId()+"' AND CHARGEDESCID='"+buySellChargesEnterIdDOB.getChargeDescId()+"' AND CHARGEGROUP_ID LIKE '");
                 selectQry.append(buySellChargesEnterIdDOB.getChargeGroupId());
                 selectQry.append("%' ");
   
    }
  else if(buySellChargesEnterIdDOB.getChargeId().length()>0)
    {
       selectQry        = new StringBuffer("SELECT DISTINCT CHARGEGROUP_ID FROM QMS_CHARGE_GROUPSMASTER WHERE INACTIVATE='N' AND CHARGE_ID='"+buySellChargesEnterIdDOB.getChargeId()+"' AND CHARGEGROUP_ID LIKE '");

    //  StringBuffer   selectQry        = new StringBuffer("SELECT DISTINCT CHARGEGROUP_ID FROM QMS_CHARGE_GROUPSMASTER WHERE INACTIVATE='N' AND CHARGEGROUP_ID LIKE '");

                 selectQry.append(buySellChargesEnterIdDOB.getChargeGroupId());
                 selectQry.append("%' ");

    } 
    else{
     selectQry        = new StringBuffer("SELECT DISTINCT CHARGEGROUP_ID FROM QMS_CHARGE_GROUPSMASTER WHERE INACTIVATE='N' AND CHARGEGROUP_ID LIKE '");
                 selectQry.append(buySellChargesEnterIdDOB.getChargeGroupId());
                 selectQry.append("%' ");
    }
 //end for CR #170761
    String       operation = buySellChargesEnterIdDOB.getOperation();
                 if(operation!=null)
                 {
                   if(operation.equals("Modify") || operation.equals("View") || operation.equals("Delete"))
                   {
                     selectQry.append(" AND INVALIDATE ='F' ");
                     selectQry.append(" AND TERMINALID = '");
                     selectQry.append(buySellChargesEnterIdDOB.getTerminalId());
                     selectQry.append("' ORDER BY CHARGEGROUP_ID");//@@ Added by subrahmanyam for the pbn id: 210495 on 12-Jul-10
                   }
                 }else
                 {
                   return chargeGroupList;
                 }

      connection  = operationsImpl.getConnection();
      pstmt       = connection.prepareStatement(selectQry.toString());
      rs          = pstmt.executeQuery();
      while(rs.next())
      {
       chargeGroupList.add(rs.getString("CHARGEGROUP_ID"));
      }
    }catch(SQLException e)
    {
        //Logger.error(FILE_NAME,"------->getAllChargeGroupIds()",e.toString());
        logger.error(FILE_NAME+"------->getAllChargeGroupIds()"+e.toString());
        throw new EJBException();         
    }catch(Exception e)
    {
        //Logger.error(FILE_NAME,"------->getAllChargeGroupIds()",e.toString());
        logger.error(FILE_NAME+"------->getAllChargeGroupIds()"+e.toString());
        throw new EJBException();         
    }finally
    {
      try
      {
        if(rs!=null)
          { rs.close();}
        if(pstmt!=null)
          { pstmt.close();}
        if(connection!=null)
          { connection.close();}
      }catch(SQLException e)
      {
        //Logger.error(FILE_NAME,"------->getAllChargeGroupIds()",e.toString());
        logger.error(FILE_NAME+"------->getAllChargeGroupIds()"+e.toString());
        throw new SQLException();
      }
    }
    return chargeGroupList;
  }
  
 
  
  private boolean isValidChargeBasisId(ChargeBasisMasterDOB chargeBasisMasterDOB,ESupplyGlobalParameters loginbean)throws SQLException
  {
    PreparedStatement pstmt   = null;
    Connection connection     = null;
    ResultSet         rs      = null;
    boolean    isValid        = true;
    String selectQry          = "SELECT CHARGEBASIS FROM QMS_CHARGE_BASISMASTER WHERE CHARGEBASIS =?";
    try
    {
      connection    = operationsImpl.getConnection();
      pstmt         = connection.prepareStatement(selectQry);
      if(chargeBasisMasterDOB!=null)
      {
        pstmt.setString(1,chargeBasisMasterDOB.getChargeBasis());
        rs  = pstmt.executeQuery();
        if(rs.next())
        {
          isValid = false;
        }
      }
    }catch(SQLException e)
    {
        //Logger.error(FILE_NAME,"------->isValidChargeBasisId()",e.toString());
        logger.error(FILE_NAME+"------->isValidChargeBasisId()"+e.toString());
        throw new EJBException();         
    }catch(Exception e)
    {
        //Logger.error(FILE_NAME,"------->isValidChargeBasisId()",e.toString());
        logger.error(FILE_NAME+"------->isValidChargeBasisId()"+e.toString());
        throw new EJBException();         
    }finally
    {
      try
      {
        if(rs!=null)
          { rs.close();}
        if(pstmt!=null)
          { pstmt.close();}
        if(connection!=null)
          { connection.close();}
      }catch(SQLException e)
      {
        //Logger.error(FILE_NAME,"------->isValidChargeBasisId()",e.toString());
        logger.error(FILE_NAME+"------->isValidChargeBasisId()"+e.toString());
        throw new EJBException();
      }
    }
    return isValid;    
  }

  public ArrayList insertChargesBasisDtls(ArrayList dataList,ESupplyGlobalParameters loginbean)throws EJBException
  {
    ArrayList insertedList    = new ArrayList();
    ArrayList invalidList     = new ArrayList();
    ArrayList returnList      = new ArrayList();
    ChargeBasisMasterEntityBeanLocalHome  home  = null;
    ChargeBasisMasterEntityBeanLocal      local = null;
    ChargeBasisMasterDOB chargeBasisMasterDOB   = null;
    try
    {
    	int dListSize	=	dataList.size();
      for(int i=0;i<dListSize;i++)
      {
        chargeBasisMasterDOB  = (ChargeBasisMasterDOB)dataList.get(i);
        if(isValidChargeBasisId(chargeBasisMasterDOB,loginbean))
        {
          insertedList.add(chargeBasisMasterDOB);
        }else
        {
          invalidList.add(chargeBasisMasterDOB);
        }
      }
      returnList.add(insertedList);
      returnList.add(invalidList);
      if(insertedList!=null && insertedList.size()>0)
      {

        home  = (ChargeBasisMasterEntityBeanLocalHome)LookUpBean.getEJBLocalHome("java:comp/env/ChargeBasisMasterEntityBean");
        local = (ChargeBasisMasterEntityBeanLocal)home.create(insertedList);

      }     
    }catch(NamingException nex)
    {
        //Logger.error(FILE_NAME,"insertChargesBasisDtls()",nex.toString());
        logger.error(FILE_NAME+"insertChargesBasisDtls()"+nex.toString());
        throw new EJBException();
    }catch(Exception e)
    {
        //Logger.error(FILE_NAME,"insertChargesBasisDtls()",e.toString());
        logger.error(FILE_NAME+"insertChargesBasisDtls()"+e.toString());
        throw new EJBException();      
    }
      return returnList;
  }
  public ChargeBasisMasterDOB getChargeBasisDtl(String chargeBasisId,String operation,ESupplyGlobalParameters loginbean)throws ObjectNotFoundException,EJBException
  {
    ChargeBasisMasterEntityBeanLocalHome  home  = null;
    ChargeBasisMasterEntityBeanLocal      local = null;
    ChargeBasisMasterDOB chargeBasisMasterDOB   = null;
    ChargeBasisMasterEntityBeanPK       pkObj   = new ChargeBasisMasterEntityBeanPK();
    
    Connection  connection  = null;
    PreparedStatement pstmt = null;
    ResultSet         rs    = null;
    
    
    String selectQry   = " SELECT BUYSELLCHARGEID FROM QMS_BUYSELLCHARGESMASTER WHERE CHARGEBASIS = ? "+
                         " UNION SELECT CARTAGE_ID FROM QMS_CARTAGE_BUYDTL WHERE CHARGE_BASIS = ?";
    boolean isvalid    = true;    
    try
    {
    
        if(!"View".equalsIgnoreCase(operation))
        {
           connection=getConnection();
           
           pstmt = connection.prepareStatement(selectQry);
           
           pstmt.setString(1,chargeBasisId);
           pstmt.setString(2,chargeBasisId);
           
           rs = pstmt.executeQuery();
           
           if(rs.next())
           {  isvalid = false;}
           
           if(rs!=null)
           {  rs.close();}
           if(pstmt!=null)
           {  pstmt.close();}
        }
       if(isvalid)
       {
        pkObj.chargeBasis      = chargeBasisId;
        home  = (ChargeBasisMasterEntityBeanLocalHome)LookUpBean.getEJBLocalHome("java:comp/env/ChargeBasisMasterEntityBean");
        local = (ChargeBasisMasterEntityBeanLocal)home.findByPrimaryKey(pkObj);
        chargeBasisMasterDOB  = local.getChagesBasisMasterDOB();
       }else
       {
         throw new ObjectNotFoundException();
       }
        
    }catch(ObjectNotFoundException e)
    {
        //Logger.error(FILE_NAME,"getChargeBasisDtl()",e.toString());
        logger.error(FILE_NAME+"getChargeBasisDtl()"+e.toString());
        throw new ObjectNotFoundException("Bean could not find");      
    }catch(NamingException nex)
    {
        //Logger.error(FILE_NAME,"getChargeBasisDtl()",nex.toString());
        logger.error(FILE_NAME+"getChargeBasisDtl()"+nex.toString());
        throw new EJBException();
    }catch(FinderException e)
    {
         //Logger.error(FILE_NAME,"getChargeBasisDtl()",e.toString());
         logger.error(FILE_NAME+"getChargeBasisDtl()"+e.toString());
        throw new EJBException();     
    }
    catch(Exception e)
    {
        e.printStackTrace();
        //Logger.error(FILE_NAME,"getChargeBasisDtl()",e.toString());
        logger.error(FILE_NAME+"getChargeBasisDtl()"+e.toString());
        throw new EJBException();      
    }finally
    {
      try
      {
       if(rs!=null)
       {  rs.close();}
       if(pstmt!=null)
       {  pstmt.close();}        
       if(connection!=null)
       {  connection.close();}        
      }catch(Exception e)
      {
        e.printStackTrace();
      }
    }
    
    return chargeBasisMasterDOB;
  } 
  public boolean updateChargesBasisDetails(ChargeBasisMasterDOB chargeBasisMasterDOB,ESupplyGlobalParameters loginbean)throws EJBException,ObjectNotFoundException
  {
    ChargeBasisMasterEntityBeanLocalHome  home  = null;
    ChargeBasisMasterEntityBeanLocal      local = null;
    ChargeBasisMasterEntityBeanPK       pkObj   = new ChargeBasisMasterEntityBeanPK();
    boolean   success                     = false;
    try
    {
      pkObj.chargeBasis  = chargeBasisMasterDOB.getChargeBasis();
      home  = (ChargeBasisMasterEntityBeanLocalHome)LookUpBean.getEJBLocalHome("java:comp/env/ChargeBasisMasterEntityBean");
      local = (ChargeBasisMasterEntityBeanLocal)home.findByPrimaryKey(pkObj);
      local.setChargeBasisMasterDOB(chargeBasisMasterDOB);
      success = true;
    }catch(ObjectNotFoundException e)
    {
        success = false;    
        //Logger.error(FILE_NAME,"updateChargesBasisDetails()",e.toString());
        logger.error(FILE_NAME+"updateChargesBasisDetails()"+e.toString());
        throw new ObjectNotFoundException("Bean could not find");      
    }catch(NamingException e)
    {
        success = false;
        //Logger.error(FILE_NAME,"updateChargesBasisDetails()",e.toString());
        logger.error(FILE_NAME+"updateChargesBasisDetails()"+e.toString());
        throw new EJBException();      
    }catch(FinderException e)
    {
        success = false;    
        //Logger.error(FILE_NAME,"updateChargesBasisDetails()",e.toString());
        logger.error(FILE_NAME+"updateChargesBasisDetails()"+e.toString());
        throw new EJBException();      
    }catch(Exception e)
    {
        success = false;    
        //Logger.error(FILE_NAME,"updateChargesBasisDetails()",e.toString());
        logger.error(FILE_NAME+"updateChargesBasisDetails()"+e.toString());
        throw new EJBException();     
    }
    return success;
  }
  public boolean deleteChargesBasisDtls(ChargeBasisMasterDOB chargeBasisMasterDOB,ESupplyGlobalParameters loginbean)throws EJBException,ObjectNotFoundException
  {
    ChargeBasisMasterEntityBeanLocalHome  home  = null;
    ChargeBasisMasterEntityBeanLocal      local = null;
    ChargeBasisMasterEntityBeanPK       pkObj   = new ChargeBasisMasterEntityBeanPK();
    boolean   success                     = false;
    try
    {
      pkObj.chargeBasis       = chargeBasisMasterDOB.getChargeBasis();
      home  = (ChargeBasisMasterEntityBeanLocalHome)LookUpBean.getEJBLocalHome("java:comp/env/ChargeBasisMasterEntityBean");
      local = (ChargeBasisMasterEntityBeanLocal)home.findByPrimaryKey(pkObj);
      local.remove();
      success = true;
    }catch(NamingException e)
    {
        success = false;
        //Logger.error(FILE_NAME,"deleteChargesBasisDtls()",e.toString());
        logger.error(FILE_NAME+"deleteChargesBasisDtls()"+e.toString());
        throw new EJBException();      
    }catch(ObjectNotFoundException e)
    {
        success = false;    
        //Logger.error(FILE_NAME,"deleteChargesBasisDtls()",e.toString());
        logger.error(FILE_NAME+"deleteChargesBasisDtls()"+e.toString());
        throw new ObjectNotFoundException("Bean could not find");      
    }catch(FinderException e)
    {
        success = false;    
        //Logger.error(FILE_NAME,"deleteChargesBasisDtls()",e.toString());
        logger.error(FILE_NAME+"deleteChargesBasisDtls()"+e.toString());
        throw new EJBException();      
    }catch(Exception e)
    {
        success = false;    
        //Logger.error(FILE_NAME,"deleteChargesBasisDtls()",e.toString());
        logger.error(FILE_NAME+"deleteChargesBasisDtls()"+e.toString());
        throw new EJBException();     
    }
    return success;
  }
  public ArrayList  getAllChargeBasisIds(String searchStr,String fromWhere,String operation,ESupplyGlobalParameters loginbean)throws SQLException
  {
    PreparedStatement pstmt = null;
    Connection  connection  = null;
    ResultSet   rs          = null;
    ArrayList   chargeBasisList = new ArrayList();
    
    String selectDescQry    = "SELECT CHARGEBASIS ||'--'|| BASIS_DESCRIPTION ||'@'|| PRIMARY_BASIS ||'@'|| SECONDARY_BASIS   FROM QMS_CHARGE_BASISMASTER WHERE CHARGEBASIS LIKE '"+searchStr+"%' AND INVALIDATE ='F' ORDER BY CHARGEBASIS";
    try
    {
      StringBuffer selectQry        = new StringBuffer("SELECT CHARGEBASIS FROM QMS_CHARGE_BASISMASTER WHERE CHARGEBASIS LIKE '");
                   selectQry.append(searchStr);
                   selectQry.append("%' AND INVALIDATE ='F' ");
                   if(operation!=null && !operation.equals(""))
                   {
                    if(operation.equals("Modify") || operation.equals("Delete"))
                     {              
                        selectQry.append(" AND CHARGEBASIS NOT IN ( SELECT DISTINCT CHARGEBASIS FROM QMS_BUYSELLCHARGESMASTER)");
                        selectQry.append(" AND CHARGEBASIS NOT IN ( SELECT DISTINCT CHARGEBASIS FROM QMS_SELLCHARGESMASTER)");
                     }
                   }
                   
      connection  = operationsImpl.getConnection();
      if("buycharges".equals(fromWhere) || "sellcharges".equals(fromWhere))
        { pstmt       = connection.prepareStatement(selectDescQry);}
      else
        { pstmt       = connection.prepareStatement(selectQry.toString());}
      rs          = pstmt.executeQuery();
      while(rs.next())
      {
       chargeBasisList.add(rs.getString(1));
      }
    }catch(SQLException e)
    {
        //Logger.error(FILE_NAME,"------->getAllChargeBasisIds()",e.toString());
        logger.error(FILE_NAME+"------->getAllChargeBasisIds()"+e.toString());
        throw new EJBException();         
    }catch(Exception e)
    {
        //Logger.error(FILE_NAME,"------->getAllChargeBasisIds()",e.toString());
        logger.error(FILE_NAME+"------->getAllChargeBasisIds()"+e.toString());
        throw new EJBException();         
    }finally
    {
      try
      {
        if(rs!=null)
          { rs.close();}
        if(pstmt!=null)
          { pstmt.close();}
        if(connection!=null)
          { connection.close();}
      }catch(SQLException e)
      {
        //Logger.error(FILE_NAME,"------->getAllChargeBasisIds()",e.toString());
        logger.error(FILE_NAME+"------->getAllChargeBasisIds()"+e.toString());
        throw new SQLException();
      }
    }
    return chargeBasisList;
  }
      

  
    public boolean invalidateCountry(ArrayList dobList)
  {
    Connection connection=null;
    PreparedStatement pstmt=null;
    CountryMasterDOB countryDob=null;
	connection=operationsImpl.getConnection();	
    String invalidateQuery="UPDATE FS_COUNTRYMASTER SET INVALIDATE=?,CURRENCY_INVALIDATE=? WHERE COUNTRYID=?";
   

    try
    {
    pstmt=connection.prepareStatement(invalidateQuery);
    int dobListSize	=	dobList.size();
	  for(int i=0;i<dobListSize;++i)
	 { 
		  countryDob =(CountryMasterDOB)dobList.get(i);
      pstmt.clearParameters();
		  
		  pstmt.setString(1,countryDob.getInvalidate());
		  pstmt.setString(2,countryDob.getCurrencyInvalidate());
          pstmt.setString(3,countryDob.getCountryId());
		  pstmt.addBatch();
	 }
    pstmt.executeBatch();
		 return true;
         
    }catch(SQLException exct)
    {
      exct.printStackTrace();
      return false;
    }
    finally
    {
      ConnectionUtil.closeConnection(connection,pstmt);
    }
  }
  public java.util.ArrayList getInvalidateCountryDetails()
  {
    Connection connection=null;
    Statement stmt=null;
    ResultSet countryRs=null;
    ArrayList countryDeatils=new ArrayList();
    //String countryQuery="SELECT COUNTRYID,COUNTRYNAME,CURRENCYID,REGION,AREA,INVALIDATE,CURRENCY_INVALIDATE   FROM FS_COUNTRYMASTER ORDER BY COUNTRYID";
    String countryQuery= " SELECT COUNTRYID,COUNTRYNAME,CURRENCYID,REGION,AREA,INVALIDATE,CURRENCY_INVALIDATE   FROM FS_COUNTRYMASTER WHERE COUNTRYID NOT IN(SELECT COUNTRYID FROM FS_FR_LOCATIONMASTER UNION SELECT COUNTRYID FROM FS_FRS_PORTMASTER)  ORDER BY COUNTRYID";
    
    try
    {
      connection=operationsImpl.getConnection();
      stmt=connection.createStatement();
      countryRs=stmt.executeQuery(countryQuery);
      while(countryRs.next())
      {
        CountryMasterDOB countryDOB=new CountryMasterDOB(countryRs.getString("COUNTRYID"),countryRs.getString("COUNTRYNAME"),countryRs.getString("CURRENCYID"),countryRs.getString("REGION"),countryRs.getString("AREA"),countryRs.getString("INVALIDATE"),countryRs.getString("CURRENCY_INVALIDATE"));
        countryDeatils.add(countryDOB);
        
      }
    }catch(SQLException excep)
    {
        excep.printStackTrace();
	  		throw new EJBException(excep.toString());

    }
    finally
    {
      ConnectionUtil.closeConnection(connection,stmt,countryRs);
    }
  return countryDeatils;
  }
  public java.util.ArrayList getLocationDetails()
  
  {
    Connection connection=null;
    Statement stmt=null;
    ResultSet locationRs=null;
    ArrayList locationList=new ArrayList();
    String locationQuery="SELECT LOCATIONID,LOCATIONNAME,COUNTRYID,CITY,ZIPCODE,SHIPMENTMODE,INVALIDATE  FROM FS_FR_LOCATIONMASTER ORDER BY LOCATIONID"; 
    try
    {
      connection=operationsImpl.getConnection();
      stmt=connection.createStatement();
      locationRs=stmt.executeQuery(locationQuery);
      while(locationRs.next())
      {
        LocationMasterJspBean locationBean=new LocationMasterJspBean();
        locationBean.setLocationId(locationRs.getString("LOCATIONID"));
        locationBean.setLocationName(locationRs.getString("LOCATIONNAME"));
        locationBean.setCountryId(locationRs.getString("COUNTRYID"));
        locationBean.setCity(locationRs.getString("CITY"));
        locationBean.setZipCode(locationRs.getString("ZIPCODE"));
        locationBean.setShipmentMode(locationRs.getString("SHIPMENTMODE"));
        locationBean.setInvalidate(locationRs.getString("INVALIDATE"));
        locationList.add(locationBean);
      }
    }catch(SQLException excep)
    {
      excep.printStackTrace();
 	throw new EJBException(excep.toString());

    }
    finally
    {
      ConnectionUtil.closeConnection(connection,stmt,locationRs);
    }
  return locationList;

  }
  public boolean invalidateLocation(ArrayList dobList)
  {
   
    Connection connection=null;
    PreparedStatement pstmt=null;
    LocationMasterJspBean locationBean=null;
	  connection=operationsImpl.getConnection();	
    String invalidateQuery="UPDATE FS_FR_LOCATIONMASTER SET INVALIDATE=? WHERE LOCATIONID=?";
   

    try
    {
    pstmt=connection.prepareStatement(invalidateQuery);
    int dobListSize	=	dobList.size();
	  for(int i=0;i<dobListSize;++i)
	 { 
		  locationBean =(LocationMasterJspBean)dobList.get(i);
      pstmt.clearParameters();
		  pstmt.setString(1,locationBean.getInvalidate());
		  pstmt.setString(2,locationBean.getLocationId());
		  pstmt.addBatch();
	 }
     pstmt.executeBatch();
		 return true;
         
    }catch(SQLException exct)
    {
      exct.printStackTrace();
      return false;
    }
    finally
    {
      ConnectionUtil.closeConnection(connection,pstmt);
    }
  }
  public java.util.ArrayList getInvalidatePortMasterDetails()
  
  {
    Connection connection=null;
    Statement stmt=null;
    ResultSet portRs=null;
    ArrayList locationList=new ArrayList();
    String locationQuery="SELECT PORTID,PORTNAME,COUNTRYID,DESCRIPTION,INVALIDATE  FROM FS_FRS_PORTMASTER ORDER BY PORTID"; 
    try
    {
      connection=operationsImpl.getConnection();
      stmt=connection.createStatement();
      portRs=stmt.executeQuery(locationQuery);
      while(portRs.next())
      {
        PortMasterJSPBean portBean=new PortMasterJSPBean();
        portBean.setPortId(portRs.getString("PORTID"));
        portBean.setPortName(portRs.getString("PORTNAME"));
        portBean.setCountryId(portRs.getString("COUNTRYID"));
        portBean.setDescription(portRs.getString("DESCRIPTION"));
        portBean.setInvalidate(portRs.getString("INVALIDATE"));
        
        locationList.add(portBean);
      }
    }catch(SQLException excep)
    {
		
      excep.printStackTrace();
	  throw new EJBException(excep.toString());
    }
    finally
    {
      ConnectionUtil.closeConnection(connection,stmt,portRs);
    }
  return locationList;

  }
  public boolean invalidatePortMaster(ArrayList dobList)
  {
    
    Connection connection=null;
    PreparedStatement pstmt=null;
    PortMasterJSPBean portDOb=null;
   	connection=operationsImpl.getConnection();	
    String invalidateQuery="UPDATE FS_FRS_PORTMASTER SET INVALIDATE=? WHERE PORTID=?";
   
   try
    {
    pstmt=connection.prepareStatement(invalidateQuery);
    int dobListSize	=	dobList.size();
	  for(int i=0;i<dobListSize;++i)
	 { 
		  portDOb =(PortMasterJSPBean)dobList.get(i);
       pstmt.clearParameters();
		  
		  pstmt.setString(1,portDOb.getInvalidate());
		  pstmt.setString(2,portDOb.getPortId());
		  pstmt.addBatch();
	 }
     pstmt.executeBatch();
		 return true;
         
    }catch(SQLException exct)
    {
      exct.printStackTrace();
      return false;
    }
    finally
    {
      ConnectionUtil.closeConnection(connection,pstmt);
    }
  }
   public java.util.ArrayList getInvalidateCustomerMasterDetails()
  
  {
    Connection connection=null;
    Statement stmt=null;
    ResultSet custRs=null;//Modified By RajKumari on 30-10-2008 for Connection Leakages.
    ArrayList customerList=new ArrayList();
    String custQuery="SELECT CUSTOMERID,COMPANYNAME,CONTACTNAME,REGISTERED,TERMINALID,CITY,INVALIDATE FROM FS_FR_CUSTOMERMASTER C, FS_ADDRESS A WHERE C.CUSTOMERADDRESSID= A.ADDRESSID   AND C.TERMINALID LIKE 'DHL%'    ORDER BY C.TERMINALID"; 
    
    try
    {
      connection=operationsImpl.getConnection();
      stmt=connection.createStatement();
      custRs=stmt.executeQuery(custQuery);
      while(custRs.next())
      {
        CustomerModel custBean=new CustomerModel();
        custBean.setCustomerId(custRs.getString("CUSTOMERID"));
        custBean.setCompanyName(custRs.getString("COMPANYNAME"));
        custBean.setContactName(custRs.getString("CONTACTNAME"));
        custBean.setRegistered(custRs.getString("REGISTERED"));
        custBean.setTerminalId(custRs.getString("TERMINALID"));
        custBean.setCity(custRs.getString("CITY"));
        custBean.setInvalidate(custRs.getString("INVALIDATE"));
        
        customerList.add(custBean);
      }
    }catch(SQLException excep)
    {
		
      excep.printStackTrace();
	  throw new EJBException(excep.toString());
    }
    finally
    {
      ConnectionUtil.closeConnection(connection,stmt,custRs);
    }
  return customerList;

  }
  public boolean invalidateCustomerMaster(ArrayList dobList)
  {
   
    Connection connection=null;
    PreparedStatement pstmt=null;
    CustomerModel custBean=null;
	connection=operationsImpl.getConnection();	
    String invalidateQuery="UPDATE FS_FR_CUSTOMERMASTER SET INVALIDATE=? WHERE CUSTOMERID=?";
   

    try
    {
    pstmt=connection.prepareStatement(invalidateQuery);
    int dobListSize	=	dobList.size();
	  for(int i=0;i<dobListSize;++i)
	 { 
		  custBean =(CustomerModel)dobList.get(i);
       pstmt.clearParameters();
		  
		  pstmt.setString(1,custBean.getInvalidate());
		  pstmt.setString(2,custBean.getCustomerId());
		  pstmt.addBatch();
	 }
     pstmt.executeBatch();
		 return true;
         
    }catch(SQLException exct)
    {
      exct.printStackTrace();
      return false;
    }
    finally
    {
      ConnectionUtil.closeConnection(connection,pstmt);
    }
  }
  public boolean invalidateTerminalMaster(ArrayList dobList)
  {
    Connection connection=null;
    PreparedStatement pstmt=null;
    TerminalRegJspBean terminalBean=null;
	connection=operationsImpl.getConnection();	
    String invalidateQuery="UPDATE FS_FR_TERMINALMASTER SET INVALIDATE=? WHERE TERMINALID=?";
   

    try
    {
    pstmt=connection.prepareStatement(invalidateQuery);
    int dobListSize	=	dobList.size();
	  for(int i=0;i<dobListSize;++i)
	 { 
		  terminalBean =(TerminalRegJspBean)dobList.get(i);
       pstmt.clearParameters();
		  
		  pstmt.setString(1,terminalBean.getInvalidate());
		  pstmt.setString(2,terminalBean.getTerminalId());
		  pstmt.addBatch();
	 }
     pstmt.executeBatch();
		 return true;
         
    }catch(SQLException exct)
    {
      exct.printStackTrace();
      return false;
    }
    finally
    {
      ConnectionUtil.closeConnection(connection,pstmt);
    }
  }
  
  public java.util.ArrayList getInvalidateCarrierDetails()
  
  {
    Connection connection=null;
    Statement stmt=null;
    ResultSet carrierRs=null;
    ArrayList carrierList=new ArrayList();
    String carrierQuery="SELECT CARRIERID,SHIPMENTMODE,CARRIERNAME,CITY,INVALIDATE FROM  FS_FR_CAMASTER A , FS_ADDRESS B WHERE A.ADDRESSID = B.ADDRESSID ORDER BY A.CARRIERID"; 
    
    try
    {
      connection=operationsImpl.getConnection();
      stmt=connection.createStatement();
      carrierRs=stmt.executeQuery(carrierQuery);
      
      while(carrierRs.next())
      {
        CarrierDetail carrierBean=new CarrierDetail();
        carrierBean.setCarrierId(carrierRs.getString("CARRIERID"));
        carrierBean.setShipmentMode(carrierRs.getString("SHIPMENTMODE"));
        carrierBean.setCarrierName(carrierRs.getString("CARRIERNAME"));
        carrierBean.setCity(carrierRs.getString("CITY"));
        carrierBean.setInvalidate(carrierRs.getString("INVALIDATE"));
        
        carrierList.add(carrierBean);
      }
    }catch(SQLException excep)
    {
		
      excep.printStackTrace();
	  throw new EJBException(excep.toString());
    }
    finally
    {
      ConnectionUtil.closeConnection(connection,stmt,carrierRs);
    }
  return carrierList;

  }
  public boolean invalidateCarrierMaster(ArrayList dobList)
  {
	  Connection connection=null;
    PreparedStatement pstmt=null;
    CarrierDetail carrierDtl=null;
	connection=operationsImpl.getConnection();	
    String invalidateQuery="UPDATE FS_FR_CAMASTER SET INVALIDATE=? WHERE CARRIERID=?";
   

    try
    {
    pstmt=connection.prepareStatement(invalidateQuery);
    int dobListSize	=	dobList.size();
	  for(int i=0;i<dobListSize;++i)
	 { 
		  carrierDtl =(CarrierDetail)dobList.get(i);
       pstmt.clearParameters();
		  
		  pstmt.setString(1,carrierDtl.getInvalidate());
		  pstmt.setString(2,carrierDtl.getCarrierId());
		  pstmt.addBatch();
	 }
     pstmt.executeBatch();
		 return true;
         
    }catch(SQLException exct)
    {
      exct.printStackTrace();
      return false;
    }
    finally
    {
      ConnectionUtil.closeConnection(connection,pstmt);
    }
   
  }
  
   //public java.util.ArrayList getInvalidateCommodityDetails()
   public java.util.ArrayList getInvalidateCommodityDetails(int noOfRecs,int pageNo,String sortBy,String sortOrder)//shyam
  
  {
    Connection connection=null;
    Statement stmt=null;
    ResultSet commodityRs=null;
    ArrayList commodityList=new ArrayList();

	ResultSet rs1          = null;//shyam
	String orderClause	  = "";//Shyam
	String row_Query	  = "";//Shyam
	int		noPages		  = 0 ;//Shyam
	int		no_of_recs		  = 0 ;//Shyam
	int		noOfrows = 0;

	ArrayList	mainList	= new ArrayList();//shyam

		 try
		{
		  connection=operationsImpl.getConnection();
		  stmt=connection.createStatement();
			
		//shyam starts here
		row_Query = ")T1  WHERE   ROWNUM <= (("+pageNo+"- 1) * "+noOfRecs+") + "+noOfRecs+") WHERE   RN > (("+pageNo+"- 1) * "+noOfRecs+")";
		
		if(sortBy.equalsIgnoreCase("Commodity Id"))
			orderClause ="ORDER BY COMODITYID "+sortOrder;
		else if(sortBy.equalsIgnoreCase("Commodity Description"))
			orderClause ="ORDER BY COMODITYDESCRIPTION "+sortOrder;
		else if(sortBy.equalsIgnoreCase("Handling Info"))
			orderClause ="ORDER BY HANDLINGINFO "+sortOrder;
		else if(sortBy.equalsIgnoreCase("Commodity Type"))
			orderClause ="ORDER BY COMODITYTYPE "+sortOrder;
		else if(sortBy.equalsIgnoreCase("INVALIDATE"))
			orderClause ="ORDER BY INVALIDATE "+sortOrder;
		String str = "";
		str	=	"SELECT COUNT(*) NO_ROWS FROM FS_FR_COMODITYMASTER";
		rs1	=	stmt.executeQuery(str);
		
		if(rs1.next())
		{
			noOfrows=rs1.getInt("NO_ROWS");
		}

		noPages = noOfrows/noOfRecs; 
			
			int extraPages=noOfrows%noOfRecs;

			if(extraPages>0)
				noPages++;

			if(noPages==0) 
				noPages = 1;

			//int extraRecs = noOfrows%noOfRecs;

			/*if(extraRecs==0)
				no_of_recs=noOfRecs;
			else
				no_of_recs=extraRecs;*/

			if(noPages==pageNo)
				no_of_recs = noOfrows%noOfRecs;
			else
				no_of_recs = noOfRecs;		

		//shyam ends here

	
    //String commodityQuery="SELECT COMODITYID ,COMODITYTYPE ,COMODITYDESCRIPTION ,HANDLINGINFO,INVALIDATE FROM FS_FR_COMODITYMASTER"; 
    String commodityQuery="SELECT * FROM (SELECT T1.*, ROWNUM RN FROM (SELECT COMODITYID ,COMODITYTYPE ,COMODITYDESCRIPTION ,HANDLINGINFO,INVALIDATE FROM FS_FR_COMODITYMASTER "+ orderClause + row_Query; //shyam

	  commodityRs=stmt.executeQuery(commodityQuery);
      while(commodityRs.next())
      {
        CommodityJspBean commodityBean=new CommodityJspBean();
        commodityBean.setCommodityId(commodityRs.getString("COMODITYID"));
        commodityBean.setCommodityType(commodityRs.getString("COMODITYTYPE"));
        commodityBean.setCommodityDescription(commodityRs.getString("COMODITYDESCRIPTION"));
        commodityBean.setCommodityHandlingInfo(commodityRs.getString("HANDLINGINFO"));
        commodityBean.setInvalidate(commodityRs.getString("INVALIDATE"));
        
        commodityList.add(commodityBean);
      }
	  	
		mainList.add(new Integer(no_of_recs));//shyam
		mainList.add(new Integer(noPages));//shyam
		mainList.add(commodityList);//shyam
		

    }catch(SQLException excep)
    {
		
      excep.printStackTrace();
	  throw new EJBException(excep.toString());
    }
    finally
    {
      ConnectionUtil.closeConnection(connection,stmt,commodityRs);
	  ConnectionUtil.closeConnection(null, null, rs1);//shyam
    }
  //return commodityList;
  return mainList;//shyam

  }
  public boolean invalidateCommodityMaster(ArrayList dobList)
  {
    
    Connection connection=null;
    PreparedStatement pstmt=null;
    CommodityJspBean commodityDOb=null;
	connection=operationsImpl.getConnection();	
    String invalidateQuery="UPDATE FS_FR_COMODITYMASTER SET INVALIDATE=? WHERE COMODITYID=?";
   

    try
    {
    pstmt=connection.prepareStatement(invalidateQuery);
    int dobListSize	=	dobList.size();
	  for(int i=0;i<dobListSize;++i)
	 { 
		  commodityDOb =(CommodityJspBean)dobList.get(i);
       pstmt.clearParameters();
		  
		  pstmt.setString(1,commodityDOb.getInvalidate());
		  pstmt.setString(2,commodityDOb.getCommodityId());
		  pstmt.addBatch();
	 }
     pstmt.executeBatch();
		 return true;
         
    }catch(SQLException exct)
    {
      exct.printStackTrace();
      return false;
    }
    finally
    {
      ConnectionUtil.closeConnection(connection,pstmt);
    }
   
  }
    public java.util.ArrayList getInvalidateServiceLevelDetails()
  
  {
    Connection connection=null;
    Statement stmt=null;
    ResultSet serviceLevelRs=null;
    ArrayList serviceLevelList=new ArrayList();
    String serviceLevelQuery="SELECT SERVICELEVELID,SERVICELEVELDESC,REMARKS,INVALIDATE FROM FS_FR_SERVICELEVELMASTER"; 
    try
    {
      connection=operationsImpl.getConnection();
      stmt=connection.createStatement();
      serviceLevelRs=stmt.executeQuery(serviceLevelQuery);
      while(serviceLevelRs.next())
      {
        ServiceLevelJspBean serviceBean=new ServiceLevelJspBean();
        serviceBean.setServiceLevelId(serviceLevelRs.getString("SERVICELEVELID"));
        serviceBean.setServiceLevelDescription(serviceLevelRs.getString("SERVICELEVELDESC"));
        serviceBean.setRemarks(serviceLevelRs.getString("REMARKS"));
        serviceBean.setInvalidate(serviceLevelRs.getString("INVALIDATE"));
        
        serviceLevelList.add(serviceBean);
      }
    }catch(SQLException excep)
    {
		
      excep.printStackTrace();
	  throw new EJBException(excep.toString());
    }
    finally
    {
      ConnectionUtil.closeConnection(connection,stmt,serviceLevelRs);
    }
  return serviceLevelList;

  }
  public boolean invalidateServiceLevelMaster(ArrayList dobList)
  {
    Connection connection=null;
    PreparedStatement pstmt=null;
    ServiceLevelJspBean serviceLevelDob=null;
	  connection=operationsImpl.getConnection();	
    String invalidateQuery="UPDATE FS_FR_SERVICELEVELMASTER SET INVALIDATE=? WHERE SERVICELEVELID=?";
    

    try
    {
    pstmt=connection.prepareStatement(invalidateQuery);
    int dobListSize	=	dobList.size();
     for(int i=0;i<dobListSize;++i)
	 { 
		  serviceLevelDob =(ServiceLevelJspBean)dobList.get(i);
       pstmt.clearParameters();
		  
		  pstmt.setString(1,serviceLevelDob.getInvalidate());
		  pstmt.setString(2,serviceLevelDob.getServiceLevelId());
		  pstmt.addBatch();
	 }
     pstmt.executeBatch();
		 return true;
         
    }catch(SQLException exct)
    {
      exct.printStackTrace();
      return false;
    }
    finally
    {
      ConnectionUtil.closeConnection(connection,pstmt);
    }
  }
   public java.util.ArrayList getInvalidateTerminalDetails()
  
  {
    Connection connection=null;
    Statement stmt=null;
    ResultSet terminalRs=null;
    ArrayList terminalList=new ArrayList();
    String terminalQuery="SELECT TERMINALID,CITY,CONTACTNAME,DESIGNATION,INVALIDATE FROM FS_FR_TERMINALMASTER T, FS_ADDRESS A WHERE T.CONTACTADDRESSID= A.ADDRESSID  ORDER BY T.TERMINALID"; 
    try
    {
      connection=operationsImpl.getConnection();
      stmt=connection.createStatement();
      terminalRs=stmt.executeQuery(terminalQuery);
      while(terminalRs.next())
      {
        TerminalRegJspBean terminalBean= new TerminalRegJspBean();
        terminalBean.setTerminalId(terminalRs.getString("TERMINALID"));
        terminalBean.setCity(terminalRs.getString("CITY"));
        terminalBean.setContactName(terminalRs.getString("CONTACTNAME"));
        terminalBean.setDesignation(terminalRs.getString("DESIGNATION"));
        terminalBean.setInvalidate(terminalRs.getString("INVALIDATE"));
        
        terminalList.add(terminalBean);
      }
    }catch(SQLException excep)
    {
		
      excep.printStackTrace();
	  throw new EJBException(excep.toString());
    }
    finally
    {
      ConnectionUtil.closeConnection(connection,stmt,terminalRs);
    }
  return terminalList;

  }
 
  /**
	* Method to Add/Modify All the Country Ids Provided thru Upload File
	* @ param countryList ArrayList	
	* @ param loginDetails ESupplyGlobalParameters
    * @ return String
    * Exceptions SQL Exception
    * Added by: Ravi         Date 07 - Jul -2005   Reason : To perform upload opertion for Country Master
	*/
	public	HashMap uploadCountryMasterDetails( ArrayList	countryList,boolean addModFlag){
		Connection connection =	null;
		PreparedStatement pStmt =	null;
        String  sqlStr = "";
        CountryMasterDOB countryMasterDOB = null;
        ArrayList   existingCountryList = new ArrayList(5);
        ArrayList   nonExistingCountryList = new ArrayList(5);
        HashMap totalMap = new HashMap(2,2);
		try{
            connection=operationsImpl.getConnection();
            totalMap = validateUploadData(countryList,addModFlag,connection);
			if(addModFlag){
                nonExistingCountryList = (ArrayList)totalMap.get("NONEXISTS");
                sqlStr = "INSERT INTO FS_COUNTRYMASTER(COUNTRYID , COUNTRYNAME , CURRENCYID, REGION, AREA, INVALIDATE)  VALUES( ?,  ?,  ?,  ?,  ?, ?)";
                pStmt= connection.prepareStatement(sqlStr);
                int nonExistCountrListSize	=	nonExistingCountryList.size();
                for(int i = 0;i<nonExistCountrListSize;i++){
                    countryMasterDOB = (CountryMasterDOB)nonExistingCountryList.get(i);
                    pStmt.clearParameters();
                    pStmt.setString(1,countryMasterDOB.getCountryId());
                    pStmt.setString(2,countryMasterDOB.getCountryName());
                    pStmt.setString(3,countryMasterDOB.getCurrencyId());
                    pStmt.setString(4,countryMasterDOB.getRegion());
                    pStmt.setString(5,countryMasterDOB.getArea());
                    pStmt.setString(6,"F");
                    pStmt.addBatch();
                }
                int count[] = pStmt.executeBatch();
                if(count.length == nonExistingCountryList.size())
                    connection.commit();
                else
                    throw new java.sql.SQLException("Problem In Performing Upload Opertion.");
            }else{
                existingCountryList = (ArrayList)totalMap.get("EXISTS");
                sqlStr = "UPDATE FS_COUNTRYMASTER SET COUNTRYNAME = ?, CURRENCYID = ?, REGION = ?, AREA = ? WHERE COUNTRYID = ?";
                pStmt= connection.prepareStatement(sqlStr);
                int existgContryListSize	=	existingCountryList.size();
                for(int i = 0;i<existgContryListSize;i++){
                    countryMasterDOB = (CountryMasterDOB)existingCountryList.get(i);
                    pStmt.clearParameters();
                    pStmt.setString(1,countryMasterDOB.getCountryName());
                    pStmt.setString(2,countryMasterDOB.getCurrencyId());
                    pStmt.setString(3,countryMasterDOB.getRegion());
                    pStmt.setString(4,countryMasterDOB.getArea());
                    pStmt.setString(5,countryMasterDOB.getCountryId());
                    pStmt.addBatch();
                }
                int count[] = pStmt.executeBatch();
                if(count.length == existingCountryList.size())
                    connection.commit();
                else
                    throw new java.sql.SQLException("Problem In Performing Upload Opertion.");
                }
                
            
		}catch(SQLException sqle){
    sqle.printStackTrace();
			throw new EJBException("SQLException	in setCountryMaster	ETransHOSuperUserSetupSessionBean "+sqle.toString());
		}catch(Exception	cnfe){
    cnfe.printStackTrace();
			throw new javax.ejb.EJBException("Exception in	setCountryMaster ETransHOSuperUserSetupSessionBean "+cnfe.toString());
		}finally{
			ConnectionUtil.closeConnection(connection,pStmt);
		}
        return totalMap;
	}//end	of method.

  /**
	* Method to Validate The Data Provided thru Upload File
	* @ param countryList ArrayList	
	* @ return String
    * Exceptions SQL Exception
    * Added by: Ravi         Date 07 - Jul -2005   Reason : To perform validations for Country Master
	*/
	private HashMap validateUploadData(ArrayList countryList,boolean flag,Connection connection) throws java.sql.SQLException{
        PreparedStatement pStmt = null;
        ResultSet   rs  = null;
        CountryMasterDOB countryMasterDOB = null;
        HashMap     hashMap = new HashMap(2,2);
        ArrayList   existingCountryList = new ArrayList(5);
        ArrayList   nonExistingCountryList = new ArrayList(5);
       
        try	{
         //Added by Rajkumari on 04-11-2008 for Connection Leakages in Loop
        pStmt = connection.prepareStatement("SELECT COUNTRYID FROM FS_COUNTRYMASTER WHERE COUNTRYID=?");
        int contryListSize	=	countryList.size();
            for(int i=0;i<contryListSize;i++){

                countryMasterDOB = (CountryMasterDOB)countryList.get(i);
                //Commented by Rajkumari on 04-11-2008 for Connection Leakages in Loop
                //pStmt = connection.prepareStatement("SELECT COUNTRYID FROM FS_COUNTRYMASTER WHERE COUNTRYID=?");
                pStmt.setString(1,countryMasterDOB.getCountryId());
                rs = pStmt.executeQuery();
                if(flag){
                    if(rs.next()){
                        countryMasterDOB.setRemarks(countryMasterDOB.getRemarks() + " Country Id Already Exists ");
                        existingCountryList.add(countryMasterDOB);
                    }else{
                        nonExistingCountryList.add(countryMasterDOB);
                    }
                }else{
                    if(!rs.next()){
                        countryMasterDOB.setRemarks(countryMasterDOB.getRemarks() + " Country Id Doesn't Exists ");
                        nonExistingCountryList.add(countryMasterDOB);
                    }else{
                        existingCountryList.add(countryMasterDOB);
                    }
                }
                if(rs!=null)
                  rs.close();
                /*if(pStmt!=null)
                  pStmt.close();*///Commented by Rajkumari on 04-11-2008 for Connection Leakages in Loop
            }
            hashMap.put("EXISTS",existingCountryList);
            hashMap.put("NONEXISTS",nonExistingCountryList);
		}catch (SQLException sqle){
      sqle.printStackTrace();
			//Logger.error(FILE_NAME,"SQLException in validateUploadData of SetupSessionBean "+sqle.toString());
      logger.error(FILE_NAME+"SQLException in validateUploadData of SetupSessionBean "+sqle.toString());
			error =	sqle.toString();
			throw new EJBException(sqle.toString());
		}catch( Exception	cnfe ){
    cnfe.printStackTrace();
			throw new java.sql.SQLException("Exception	in validateUploadData of	SetupSessionBean "	+cnfe.toString());
		}finally{
            nonExistingCountryList = null;
            existingCountryList = null;
            countryMasterDOB = null;
			ConnectionUtil.closePreparedStatement(pStmt,rs);	
		}
		return hashMap;
	}
    
    /**
	* Method to Add/Modify All the Location Ids Provided thru Upload File
	* @ param countryList ArrayList	
	* @ param boolean addModFlag
    * @ return HashMap
    * Exceptions SQL Exception
    * Added by: Ravi         Date 07 - Jul -2005   Reason : To perform upload opertion for Country Master
	*/
	public	HashMap uploadLocationMasterDetails( ArrayList	locationList,boolean addModFlag){
		Connection connection =	null;
		PreparedStatement pStmt =	null;
        String  sqlStr = "";
        LocationMasterJspBean locationMasterDOB = null;
        ArrayList   existingCountryList = new ArrayList(5);
        ArrayList   nonExistingCountryList = new ArrayList(5);
        HashMap totalMap = new HashMap(2,2);
		try{
            connection=operationsImpl.getConnection();
            totalMap = validateLocationUploadData(locationList,addModFlag,connection);
			if(addModFlag){
                nonExistingCountryList = (ArrayList)totalMap.get("NONEXISTS");
                sqlStr = "INSERT INTO FS_FR_LOCATIONMASTER(LOCATIONID,LOCATIONNAME,CITY,ZIPCODE,COUNTRYID,SHIPMENTMODE) VALUES(?,?,?,?,?,?)";
                pStmt= connection.prepareStatement(sqlStr);
                int nonExistngContryListSize	=	nonExistingCountryList.size();
                for(int i = 0;i<nonExistngContryListSize;i++){
                    locationMasterDOB = (LocationMasterJspBean)nonExistingCountryList.get(i);
                    pStmt.clearParameters();
                    pStmt.setString(1,locationMasterDOB.getLocationId());
                    pStmt.setString(2,locationMasterDOB.getLocationName());
                    pStmt.setString(3,locationMasterDOB.getCity());
                    pStmt.setString(4,locationMasterDOB.getZipCode());
                    pStmt.setString(5,locationMasterDOB.getCountryId());
                    pStmt.setString(6,locationMasterDOB.getShipmentMode());
                    pStmt.addBatch();
                }
                int count[] = pStmt.executeBatch();
                if(count.length == nonExistingCountryList.size())
                    connection.commit();
                else
                    throw new java.sql.SQLException("Problem In Performing Upload Opertion.");
            }else{
                existingCountryList = (ArrayList)totalMap.get("EXISTS");
                sqlStr = "UPDATE FS_FR_LOCATIONMASTER SET LOCATIONNAME =?,CITY=?, ZIPCODE=?,COUNTRYID =?,SHIPMENTMODE=?	WHERE LOCATIONID=?";                pStmt= connection.prepareStatement(sqlStr);
                int existngContryListSize	=	existingCountryList.size();
                for(int i = 0;i<existngContryListSize;i++){
                    locationMasterDOB = (LocationMasterJspBean)existingCountryList.get(i);
                    pStmt.clearParameters();
                    pStmt.setString(1,locationMasterDOB.getLocationName());
                    pStmt.setString(2,locationMasterDOB.getCity());
                    pStmt.setString(3,locationMasterDOB.getZipCode());
                    pStmt.setString(4,locationMasterDOB.getCountryId());
                    pStmt.setString(5,locationMasterDOB.getShipmentMode());
                    pStmt.setString(6,locationMasterDOB.getLocationId());
                    pStmt.addBatch();
                }
                int count[] = pStmt.executeBatch();
                if(count.length == existingCountryList.size())
                    connection.commit();
                else
                    throw new java.sql.SQLException("Problem In Performing Upload Opertion.");
                }
                
            
		}catch(SQLException sqle){
			throw new EJBException("SQLException	in setCountryMaster	ETransHOSuperUserSetupSessionBean "+sqle.toString());
		}catch(Exception	cnfe){
			throw new javax.ejb.EJBException("Exception in	setCountryMaster ETransHOSuperUserSetupSessionBean "+cnfe.toString());
		}finally{
			ConnectionUtil.closeConnection(connection,pStmt);
		}
        return totalMap;
	}//end	of method.

  /**
	* Method to Validate The Data Provided thru Upload File
	* @ param locationList ArrayList	
	* @ return String
    * Exceptions SQL Exception
    * Added by: Ravi         Date 07 - Jul -2005   Reason : To perform validations for Country Master
	*/
	private HashMap validateLocationUploadData(ArrayList locationList,boolean flag,Connection connection) throws java.sql.SQLException{
        PreparedStatement pStmt = null;
        ResultSet   rs  = null;
        PreparedStatement pStmt1 = null;
        ResultSet   rs1  = null;
        LocationMasterJspBean locationMasterDOB = null;
        HashMap     hashMap = new HashMap(2,2);
        ArrayList   existingLocationList = new ArrayList(5);
        ArrayList   nonExistingLocationList = new ArrayList(5);
        try	{
        //Added by Rajkumari on 04-11-2008 for Connection Leakages in Loop
        pStmt = connection.prepareStatement("SELECT LOCATIONID FROM FS_FR_LOCATIONMASTER WHERE LOCATIONID=?");
        pStmt1 = connection.prepareStatement("SELECT COUNTRYID FROM FS_COUNTRYMASTER WHERE COUNTRYID=?");
        int locIdsListSize	=	locationList.size();    
        for(int i=0;i<locIdsListSize;i++){
                locationMasterDOB = (LocationMasterJspBean)locationList.get(i);
                //Commented by Rajkumari on 04-11-2008 for Connection Leakages in Loop
                /*pStmt = connection.prepareStatement("SELECT LOCATIONID FROM FS_FR_LOCATIONMASTER WHERE LOCATIONID=?");
                pStmt1 = connection.prepareStatement("SELECT COUNTRYID FROM FS_COUNTRYMASTER WHERE COUNTRYID=?");*/
                pStmt.setString(1,locationMasterDOB.getLocationId());
                rs = pStmt.executeQuery();
                pStmt1.clearParameters();
                pStmt1.setString(1,locationMasterDOB.getCountryId());
                if(flag){
                    if(rs.next()){
                        locationMasterDOB.setRemarks(locationMasterDOB.getRemarks() + " Location Id Already Exists ");
                        existingLocationList.add(locationMasterDOB);
                    }else{
                        
                        rs1 = pStmt1.executeQuery();
                        if(rs1.next()){
                            nonExistingLocationList.add(locationMasterDOB);
                        }else{
                            locationMasterDOB.setRemarks(locationMasterDOB.getRemarks() + " Country Id Doesn't Exists. ");
                            existingLocationList.add(locationMasterDOB);
                        }
                    }
                }else{
                    if(!rs.next()){
                        locationMasterDOB.setRemarks(locationMasterDOB.getRemarks() + " Location Id Doesn't Exists. ");
                        nonExistingLocationList.add(locationMasterDOB);
                    }else{
                        //existingCountryList.add(locationMasterDOB);
                        rs1 = pStmt1.executeQuery();
                        if(!rs1.next()){
                            locationMasterDOB.setRemarks(locationMasterDOB.getRemarks() + " Country Id Doesn't Exists. ");
                            nonExistingLocationList.add(locationMasterDOB);
                        }else{
                            existingLocationList.add(locationMasterDOB);
                        }
                    }
                }
                if(rs!=null)
                  rs.close();
                if(rs1!=null)
                  rs1.close();
              /*  if(pStmt!=null)
                  pStmt.close();
                 if(pStmt1!=null)
                  pStmt1.close();*///Commented by Rajkumari on 04-11-2008 for Connection Leakages in Loop
            }
            hashMap.put("EXISTS",existingLocationList);
            hashMap.put("NONEXISTS",nonExistingLocationList);
		}catch (SQLException sqle){
			
			//Logger.error(FILE_NAME,"SQLException in validateUploadData of SetupSessionBean "+sqle.toString());
      logger.error(FILE_NAME+"SQLException in validateUploadData of SetupSessionBean "+sqle.toString());
			error =	sqle.toString();
			throw new EJBException(sqle.toString());
		}catch( Exception	cnfe ){
			throw new java.sql.SQLException("Exception	in validateUploadData of	SetupSessionBean "	+cnfe.toString());
		}finally{
            nonExistingLocationList = null;
            existingLocationList = null;
            locationMasterDOB = null;
			ConnectionUtil.closePreparedStatement(pStmt,rs);
      ConnectionUtil.closePreparedStatement(pStmt1,rs1);
		}
		return hashMap;
	}
    
    
    /**
	* Method to Add/Modify All the Port Ids Provided thru Upload File
	* @ param countryList ArrayList	
	* @ param boolean addModFlag
    * @ return HashMap
    * Exceptions SQL Exception
    * Added by: Ravi         Date 07 - Jul -2005   Reason : To perform upload opertion for Port Master
	*/
	public	HashMap uploadPortMasterDetails( ArrayList	portList,boolean addModFlag){
		Connection connection =	null;
		PreparedStatement pStmt =	null;
        String  sqlStr = "";
        PortMasterJSPBean portMasterDOB = null;
        ArrayList   existingPortList = new ArrayList(5);
        ArrayList   nonExistingPortList = new ArrayList(5);
        HashMap totalMap = new HashMap(2,2);
		try{
            connection=operationsImpl.getConnection();
            totalMap = validatePortUploadData(portList,addModFlag,connection);
			if(addModFlag){
                nonExistingPortList = (ArrayList)totalMap.get("NONEXISTS");
                sqlStr = "INSERT INTO	FS_FRS_PORTMASTER(PORTID,PORTNAME,COUNTRYID,DESCRIPTION) VALUES(?,?,?,?)";
                pStmt= connection.prepareStatement(sqlStr);
                int nonExistngProtListSize	=	nonExistingPortList.size();
                for(int i = 0;i<nonExistngProtListSize;i++){
                    portMasterDOB = (PortMasterJSPBean)nonExistingPortList.get(i);
                    pStmt.clearParameters();
                    pStmt.setString(1,portMasterDOB.getPortId());
                    pStmt.setString(2,portMasterDOB.getPortName());
                    pStmt.setString(3,portMasterDOB.getCountryId());
                    pStmt.setString(4,portMasterDOB.getDescription());
                    pStmt.addBatch();
                }
                int count[] = pStmt.executeBatch();
                if(count.length == nonExistingPortList.size())
                    connection.commit();
                else
                    throw new java.sql.SQLException("Problem In Performing Upload Opertion.");
            }else{
                existingPortList = (ArrayList)totalMap.get("EXISTS");
                sqlStr = "UPDATE	FS_FRS_PORTMASTER SET PORTNAME =?,COUNTRYID =?,DESCRIPTION=? WHERE PORTID=?";
                pStmt= connection.prepareStatement(sqlStr);
                int existngPortListSize		=	existingPortList.size();
                for(int i = 0;i<existngPortListSize;i++){
                    portMasterDOB = (PortMasterJSPBean)existingPortList.get(i);
                    pStmt.clearParameters();
                    pStmt.setString(1,portMasterDOB.getPortName());
                    pStmt.setString(2,portMasterDOB.getCountryId());
                    pStmt.setString(3,portMasterDOB.getDescription());
                    pStmt.setString(4,portMasterDOB.getPortId());
                    pStmt.addBatch();
                }
                int count[] = pStmt.executeBatch();
                if(count.length == existingPortList.size())
                    connection.commit();
                else
                    throw new java.sql.SQLException("Problem In Performing Upload Opertion.");
                }
                
            
		}catch(SQLException sqle){
			throw new EJBException("SQLException	in setCountryMaster	ETransHOSuperUserSetupSessionBean "+sqle.toString());
		}catch(Exception	cnfe){
			throw new javax.ejb.EJBException("Exception in	setCountryMaster ETransHOSuperUserSetupSessionBean "+cnfe.toString());
		}finally{
			ConnectionUtil.closeConnection(connection,pStmt);
		}
        return totalMap;
	}//end	of method.

  /**
	* Method to Validate The Data Provided thru Upload File
	* @ param locationList ArrayList	
	* @ return String
    * Exceptions SQL Exception
    * Added by: Ravi         Date 07 - Jul -2005   Reason : To perform validations for Country Master
	*/
	private HashMap validatePortUploadData(ArrayList portList,boolean flag,Connection connection) throws java.sql.SQLException{
        PreparedStatement pStmt = null;
        ResultSet   rs  = null;
        PreparedStatement pStmt1 = null;
        ResultSet   rs1  = null;
        PortMasterJSPBean portMasterDOB = null;
        HashMap     hashMap = new HashMap(2,2);
        ArrayList   existingPortList = new ArrayList(5);
        ArrayList   nonExistingPortList = new ArrayList(5);
        try	{
        //Added by Rajkumari on 04-11-2008 for Connection Leakages in Loop
        pStmt = connection.prepareStatement("SELECT PORTID FROM FS_FRS_PORTMASTER WHERE PORTID=?");
        pStmt1 = connection.prepareStatement("SELECT COUNTRYID FROM FS_COUNTRYMASTER WHERE COUNTRYID=?");
        int portListSize	=	portList.size();  
        for(int i=0;i<portListSize;i++){
                portMasterDOB = (PortMasterJSPBean)portList.get(i);
                //Commented by Rajkumari on 04-11-2008 for Connection Leakages in Loop
               /* pStmt = connection.prepareStatement("SELECT PORTID FROM FS_FRS_PORTMASTER WHERE PORTID=?");
                pStmt1 = connection.prepareStatement("SELECT COUNTRYID FROM FS_COUNTRYMASTER WHERE COUNTRYID=?");*/
                pStmt.setString(1,portMasterDOB.getPortId());
                rs = pStmt.executeQuery();
                pStmt1.clearParameters();
                pStmt1.setString(1,portMasterDOB.getCountryId());
                if(flag){
                    if(rs.next()){
                        portMasterDOB.setRemarks(portMasterDOB.getRemarks() + " Port Id Already Exists ");
                        existingPortList.add(portMasterDOB);
                    }else{
                        
                        rs1 = pStmt1.executeQuery();
                        if(rs1.next()){
                            nonExistingPortList.add(portMasterDOB);
                        }else{
                            portMasterDOB.setRemarks(portMasterDOB.getRemarks() + " Country Id Doesn't Exists. ");
                            existingPortList.add(portMasterDOB);
                        }
                    }
                }else{
                    if(!rs.next()){
                        portMasterDOB.setRemarks(portMasterDOB.getRemarks() + " Port Id Doesn't Exists. ");
                        nonExistingPortList.add(portMasterDOB);
                    }else{
                        //existingCountryList.add(locationMasterDOB);
                        rs1 = pStmt1.executeQuery();
                        if(!rs1.next()){
                            portMasterDOB.setRemarks(portMasterDOB.getRemarks() + " Country Id Doesn't Exists. ");
                            nonExistingPortList.add(portMasterDOB);
                        }else{
                            existingPortList.add(portMasterDOB);
                        }
                    }
                }
                if(rs!=null)
                  rs.close();
                if(rs1!=null)
                  rs1.close();
               /* if(pStmt!=null)
                  pStmt.close();
                 if(pStmt1!=null)
                  pStmt1.close();*///Commented by Rajkumari on 04-11-2008 for Connection Leakages in Loop
            }
            hashMap.put("EXISTS",existingPortList);
            hashMap.put("NONEXISTS",nonExistingPortList);
		}catch (SQLException sqle){
			
			//Logger.error(FILE_NAME,"SQLException in validateUploadData of SetupSessionBean "+sqle.toString());
      logger.error(FILE_NAME+"SQLException in validateUploadData of SetupSessionBean "+sqle.toString());
			error =	sqle.toString();
			throw new EJBException(sqle.toString());
		}catch( Exception	cnfe ){
			throw new java.sql.SQLException("Exception	in validateUploadData of	SetupSessionBean "	+cnfe.toString());
		}finally{
            nonExistingPortList = null;
            existingPortList = null;
            portMasterDOB = null;
			ConnectionUtil.closePreparedStatement(pStmt,rs);
      ConnectionUtil.closePreparedStatement(pStmt1,rs1);	
		}
		return hashMap;
	}
    
    /**
	* Method to Add/Modify All the Commodity Ids Provided thru Upload File
	* @ param countryList ArrayList	
	* @ param boolean addModFlag
    * @ return HashMap
    * Exceptions SQL Exception
    * Added by: Ravi         Date 07 - Jul -2005   Reason : To perform upload opertion for Commodity Master
	*/
	public	HashMap uploadCommodityMasterDetails( ArrayList	commodityList,boolean addModFlag){
		Connection connection =	null;
		PreparedStatement pStmt =	null;
        String  sqlStr = "";
        CommodityJspBean commodityMasterDOB = null;
        ArrayList   existingCommodityList = new ArrayList(5);
        ArrayList   nonExistingCommodityList = new ArrayList(5);
        HashMap totalMap = new HashMap(2,2);
		try{
            connection=operationsImpl.getConnection();
            totalMap = validateCommodityUploadData(commodityList,addModFlag,connection);
			if(addModFlag){
                nonExistingCommodityList = (ArrayList)totalMap.get("NONEXISTS");
                sqlStr = "INSERT	INTO FS_FR_COMODITYMASTER(COMODITYID , COMODITYDESCRIPTION , HANDLINGINFO ,COMODITYTYPE,HAZARD_INDICATOR,INVALIDATE,SUBCLASS ,UNNUMBER ,CLASSTYPE ,TERMINALID  ) VALUES (?,?,?,?,?,?,?,?,?,?)";
                pStmt= connection.prepareStatement(sqlStr);
                int nonExistngCommListSize	=	nonExistingCommodityList.size();
                for(int i = 0;i<nonExistngCommListSize;i++){
                    commodityMasterDOB = (CommodityJspBean)nonExistingCommodityList.get(i);
                    pStmt.clearParameters();
                    pStmt.setString(1,commodityMasterDOB.getCommodityId());
                    pStmt.setString(2,commodityMasterDOB.getCommodityDescription());
                    pStmt.setString(3,commodityMasterDOB.getCommodityHandlingInfo());
                    pStmt.setString(4,commodityMasterDOB.getCommodityType());
                    pStmt.setString(5,commodityMasterDOB.getHazardIndicator());
                    pStmt.setString(6,"F");
                    pStmt.setString(7,commodityMasterDOB.getSubClass());
                    pStmt.setString(8,commodityMasterDOB.getUnNumber());
                    pStmt.setString(9,commodityMasterDOB.getClassType());
                    pStmt.setString(10,commodityMasterDOB.getTerminalId());
                    pStmt.addBatch();
                }
                int count[] = pStmt.executeBatch();
                if(count.length == nonExistingCommodityList.size())
                    connection.commit();
                else
                    throw new java.sql.SQLException("Problem In Performing Upload Opertion.");
            }else{
                existingCommodityList = (ArrayList)totalMap.get("EXISTS");
                sqlStr = "UPDATE FS_FR_COMODITYMASTER SET COMODITYDESCRIPTION =?,HANDLINGINFO =?,COMODITYTYPE =?,HAZARD_INDICATOR=?,INVALIDATE=?,SUBCLASS=?,UNNUMBER=?,CLASSTYPE=?,TERMINALID=? WHERE	COMODITYID=?";
                pStmt= connection.prepareStatement(sqlStr);
                int existngComListSize	=	existingCommodityList.size();
                for(int i = 0;i<existngComListSize;i++){
                    commodityMasterDOB = (CommodityJspBean)existingCommodityList.get(i);
                    pStmt.clearParameters();
                    pStmt.setString(1,commodityMasterDOB.getCommodityDescription());
                    pStmt.setString(2,commodityMasterDOB.getCommodityHandlingInfo());
                    pStmt.setString(3,commodityMasterDOB.getCommodityType());
                    pStmt.setString(4,commodityMasterDOB.getHazardIndicator());
                    pStmt.setString(5,"F");
                    pStmt.setString(6,commodityMasterDOB.getSubClass());
                    pStmt.setString(7,commodityMasterDOB.getUnNumber());
                    pStmt.setString(8,commodityMasterDOB.getClassType());
                    pStmt.setString(9,commodityMasterDOB.getTerminalId());
                    pStmt.setString(10,commodityMasterDOB.getCommodityId());
                    pStmt.addBatch();
                }
                int count[] = pStmt.executeBatch();
                if(count.length == existingCommodityList.size())
                    connection.commit();
                else
                    throw new java.sql.SQLException("Problem In Performing Upload Opertion.");
                }
                
            
		}catch(SQLException sqle){
			throw new EJBException("SQLException	in setCountryMaster	ETransHOSuperUserSetupSessionBean "+sqle.toString());
		}catch(Exception	cnfe){
			throw new javax.ejb.EJBException("Exception in	setCountryMaster ETransHOSuperUserSetupSessionBean "+cnfe.toString());
		}finally{
			ConnectionUtil.closeConnection(connection,pStmt);
		}
        return totalMap;
	}//end	of method.

  /**
	* Method to Validate The Data Provided thru Upload File
	* @ param commodityList ArrayList	
	* @ return String
    * Exceptions SQL Exception
    * Added by: Ravi         Date 07 - Jul -2005   Reason : To perform validations for Commodity Master
	*/
	private HashMap validateCommodityUploadData(ArrayList commodityList,boolean flag,Connection connection) throws java.sql.SQLException{
        PreparedStatement pStmt = null;
        ResultSet   rs  = null;
       // ResultSet   rs1  = null;//Commented By RajKumari on 30-10-2008 for Connection Leakages.
        CommodityJspBean commodityMasterDOB = null;
        HashMap     hashMap = new HashMap(2,2);
        ArrayList   existingCommodityList = new ArrayList(5);
        ArrayList   nonExistingCommodityList = new ArrayList(5);
        try	{
        pStmt = connection.prepareStatement("SELECT COMODITYID FROM FS_FR_COMODITYMASTER WHERE COMODITYID=?");
        int commListSize	=	commodityList.size();
        for(int i=0;i<commListSize;i++){
                commodityMasterDOB = (CommodityJspBean)commodityList.get(i);
                pStmt.setString(1,commodityMasterDOB.getCommodityId());
                rs = pStmt.executeQuery();
                if(flag){
                    if(rs.next()){
                        commodityMasterDOB.setRemarks(commodityMasterDOB.getRemarks() + " Commodity Id Already Exists ");
                        existingCommodityList.add(commodityMasterDOB);
                    }else{
                        
                        nonExistingCommodityList.add(commodityMasterDOB);
                        
                    }
                }else{
                    if(!rs.next()){
                        commodityMasterDOB.setRemarks(commodityMasterDOB.getRemarks() + " Commodity Id Doesn't Exists. ");
                        nonExistingCommodityList.add(commodityMasterDOB);
                    }else{
                        existingCommodityList.add(commodityMasterDOB);
                        
                    }
                }
                if(rs!=null)
                  rs.close();
            }
            hashMap.put("EXISTS",existingCommodityList);
            hashMap.put("NONEXISTS",nonExistingCommodityList);
		}catch (SQLException sqle){
			
			//Logger.error(FILE_NAME,"SQLException in validateUploadData of SetupSessionBean "+sqle.toString());
      logger.error(FILE_NAME+"SQLException in validateUploadData of SetupSessionBean "+sqle.toString());
			error =	sqle.toString();
			throw new EJBException(sqle.toString());
		}catch( Exception	cnfe ){
			throw new java.sql.SQLException("Exception	in validateUploadData of	SetupSessionBean "	+cnfe.toString());
		}finally{
            nonExistingCommodityList = null;
            existingCommodityList = null;
            commodityMasterDOB = null;
			ConnectionUtil.closePreparedStatement(pStmt,rs);	
		}
		return hashMap;
	}
    
      //Added by rk for DHL QMS salesPerson module
public ArrayList getTerminalIdsforThirdStation(String locationId,String searchString)   
{
		Connection	connection	= null;
		Statement	stmt		= null;
        ResultSet	rs			= null;
		ArrayList terminalIds = new ArrayList();
		String sqlTerminalType	= "";
		CallableStatement cstmt = null;
		if(searchString==null)
     searchString = "";
		/*if(((searchString!=null) && !(searchString.equals("")))){
				searchString	=	" AND TERMINALID LIKE '"+searchString+"%' ";
		}else{
			searchString	=	"";
		}*/
		
		
		String		sqlQry		=	"SELECT TERMINALID FROM FS_FR_TERMINALLOCATION WHERE LOCATIONID ='"+locationId+"' AND TERMINALID LIKE '"+searchString+"%' ORDER BY TERMINALID";
        
		try
        {
              connection = this.getConnection();
            stmt = connection.createStatement();
            rs =  stmt.executeQuery(sqlQry);
			        //cstmt        = connection.prepareCall("{ ?= call QMS_SETUPS.GET_TERMINAL_2(?,?)}");
              //cstmt.registerOutParameter(1,OracleTypes.CURSOR);
              //System.out.println("locationId "+locationId);
              //System.out.println("searchString "+searchString);
              //cstmt.setString(2,locationId);
              //cstmt.setString(3,searchString+"%");
              //cstmt.execute();
              //rs  =  (ResultSet)cstmt.getObject(1);              
              while(rs.next())
              {
                    terminalIds.add(rs.getString(1));
              }
        }
        catch(SQLException sqEx)
        {
		sqEx.printStackTrace();
			//Logger.error(FILE_NAME, "[getTerminalIds(whereclause)] -> "+sqEx.toString());
      logger.error(FILE_NAME+ "[getTerminalIds(whereclause)] -> "+sqEx.toString());
            throw new EJBException(sqEx.toString());
        }
        catch(Exception sqEx)
        {
			sqEx.printStackTrace();
			//Logger.error(FILE_NAME, "[getTerminalIds(whereclause)] -> "+sqEx.toString());
      logger.error(FILE_NAME+ "[getTerminalIds(whereclause)] -> "+sqEx.toString());
            throw new EJBException(sqEx.toString());
        }
        finally
        {
          ConnectionUtil.closeConnection(connection, cstmt, rs);
          ConnectionUtil.closeConnection(null, stmt, null);
        }
        return terminalIds;
    }
//**************


// This is added by subbareddy on 18-07-2005
public TaxMaster getTaxDetails(String taxId,com.foursoft.esupply.common.bean.ESupplyGlobalParameters esupplyGlobalParameters,String termId,String operation) 
	{
			Statement st = null;		// Creating	Statement Object for Executing Query in	this method
			ResultSet rs = null;		// ResultSet Object	for	SELECT_TAXDETAILS Query
			Connection connection	=	null; // Connection	Object
			TaxMaster taxMaster		=	null; // for storing taxmaster Details
			String REMARKS			=	null;
			//String SELECT_TAXDETAILS = " SELECT	* FROM FS_FR_TAXMASTER WHERE TAXID = '"+ taxId +"' AND TRML_ID='"+termId+"'";
			// @@ Anup added for SPETI-3716  on 20050124
			String SELECT_TAXDETAILS = "";
			if (operation.equals("View"))
			{
				 SELECT_TAXDETAILS = "SELECT	* FROM FS_FR_TAXMASTER A,FS_FR_TERMINALMASTER B WHERE "+
					                  " TRML_ID IN ('"+termId+"',B.TERMINALID) AND "+
					                  " B.OPER_ADMIN_FLAG ='H' AND A.TAXID ='"+ taxId +"'";
			}
			else
		     {
			 SELECT_TAXDETAILS = " SELECT	* FROM FS_FR_TAXMASTER WHERE TAXID = '"+ taxId +"' AND TRML_ID='"+termId+"'";
			 }
			 // @@ 20050124
			try
			{
				connection	= operationsImpl.getConnection();
				st			= connection.createStatement();
				rs			= st.executeQuery(SELECT_TAXDETAILS);
				taxMaster	= new TaxMaster();
				if(rs.next())
				{
					taxMaster.setTaxId(rs.getString("TAXID"));
					taxMaster.setDesc(rs.getString("TAXDESC"));
					REMARKS	= rs.getString("REMARKS");
					if(REMARKS==null) {REMARKS="";}
					taxMaster.setRemarks(REMARKS);
					taxMaster.setTermId(rs.getString("TRML_ID"));
					taxMaster.setTaxType(rs.getString("TAX_TYPE_ID"));
					taxMaster.setTaxPer(rs.getDouble("TAX_PERCENT"));
					taxMaster.setSurchargeId(rs.getString("SURCHARGE_TAX_ID"));
					taxMaster.setChargeId(rs.getString("CHARGEID"));
					
				}
				else
					{return null;}
					//taxMaster =	null;
					
				if(esupplyGlobalParameters.getAccessType()!= null && esupplyGlobalParameters.getAccessType().equals("HO_TERMINAL"))
	   			{
					
					
					rs=null;
          //Added By RajKumari on 30-10-2008 for Connection Leakages.
					if(st != null){
          st.close();
          st=null;
          }
					ArrayList countList = new ArrayList();					
					String sqlCountry = " SELECT TRML_ID,TAXID,COUNTRYID FROM FS_FR_COUNTRY_TAX WHERE TAXID='"+taxId+"'"+
										//@@  G.Srinivas added on 20050211 SPETI-3903,3910,3911,3906
										" ORDER BY COUNTRYID" ;				
										//@@ 20050211 SPETI-3903,3910,3911,3906
					
					st			= connection.createStatement();
					rs			= st.executeQuery(sqlCountry);
					
					while(rs.next())
					{
						
						countList.add(rs.getString(3));
						
					}	
					
					taxMaster.setCountryList(countList);
	   			}		
				
			}
			catch( SQLException	sqle )
			{
				
				//Logger.error(FILE_NAME," Tax Details not found in getTaxDetails() :"+sqle.toString() );
        logger.error(FILE_NAME+" Tax Details not found in getTaxDetails() :"+sqle.toString() );
				throw new EJBException(sqle.toString());
			}
			catch( javax.ejb.EJBException	rme	)
			{
				
				//Logger.error(FILE_NAME,"Error while	Creating DataConection in getTaxDetails() :	"+rme.toString());
        logger.error(FILE_NAME+"Error while	Creating DataConection in getTaxDetails() :	"+rme.toString());
				throw new EJBException(rme.toString());
			}
			finally
			{
				try
				{
					if(	st != null)
						{st.close();}
					if(	rs != null)
						{rs.close();}
					if(	connection != null)
						{connection.close();}
				}catch(	SQLException sqle )
				{
					//Logger.error(FILE_NAME,"Error while	closing	Connections	in getTaxDetails() : "+sqle.toString());
          logger.error(FILE_NAME+"Error while	closing	Connections	in getTaxDetails() : "+sqle.toString());
				}
			}
			return taxMaster;
	}
	public String isTaxMasterTaxIdExists(String taxId,com.foursoft.esupply.common.bean.ESupplyGlobalParameters esupplyGlobalParameters,TaxMaster taxMaster) 
	{
			Statement st				= null;	 //	 Statement Object for executing	SELECT_TAXDETAILS Query
			ResultSet rs				= null;	  // ResultSet Object  for SELECT_TAXDETAILS Query
			Connection connection		=	null;  //  Connection Object
			String SELECT_TAXDETAILS	= "	SELECT TAXID FROM FS_FR_TAXMASTER WHERE	TAXID =	'"+	taxId +"'";
			String errors				= "";
			String ho_terminalId        = "";
			try
			{
				
				
				connection	= operationsImpl.getConnection();
			//	added by Srivegi-- HO_terminal condition is added in SELECT_TAXDETAILS Query
             	
			String  	sq1	= " SELECT TERMINALID FROM FS_FR_TERMINALMASTER WHERE OPER_ADMIN_FLAG ='H' " ;
			st	   		= connection.createStatement();
			rs  			= st.executeQuery(sq1);
			while(rs.next())
			{
				ho_terminalId = rs.getString(1);
			}	
			if(rs!=null)
      rs.close();
			if(st!=null)
        st.close();
			
				
				if(esupplyGlobalParameters.getAccessType()!= null && esupplyGlobalParameters.getAccessType().equals("OPER_TERMINAL"))
	   			{	
				
				 	 SELECT_TAXDETAILS	= "	SELECT TAXID FROM FS_FR_TAXMASTER WHERE	TAXID =	'"+	taxId +"' AND TRML_ID IN('"+esupplyGlobalParameters.getTerminalId()+"','"+ho_terminalId+"')";
	   			}//this is end of HO LEVEL adding.
				
				
				st			= connection.createStatement();
				rs			= st.executeQuery(SELECT_TAXDETAILS);
				int taxCount=0;
				if(rs.next())
				{
					taxCount++;
				}	
				if(taxCount >0)
				{	
					errors="Wrong_TaxId";
					return errors ;
				}
				else if(taxCount ==0)
				errors="Correct";
				
				//return true;
				
				String checkData =validate_TaxData(taxMaster,esupplyGlobalParameters);
			    
				if(checkData != null && checkData.equals("Correct") && errors.equals("Correct"))
				{
				   //errors = errors+checkData;
				   errors="CorrectData" ;
				  
				}
				else
				{
				 errors="";	
				 errors = errors+checkData ;
				}
			
				
			}
			catch( SQLException	sqle )
			{
				
				//Logger.error(FILE_NAME," Tax Details not found in isTaxIdExists() :"+sqle.toString() );
        logger.error(FILE_NAME+" Tax Details not found in isTaxIdExists() :"+sqle.toString() );
				throw new EJBException(sqle.toString());
			}
			catch( javax.ejb.EJBException	rme	)
			{
				
				//Logger.error(FILE_NAME,"Error while	Creating DataConection in isTaxIdExists() :	"+rme.toString());
        logger.error(FILE_NAME+"Error while	Creating DataConection in isTaxIdExists() :	"+rme.toString());
				throw new EJBException(rme.toString());
			}
			finally
			{
				try
				{
					if(	st != null)
						st.close();
					if(	rs != null)
						rs.close();
					if(	connection != null)
						connection.close();
				}catch(	SQLException sqle )
				{
					//Logger.error(FILE_NAME,"Error while	closing	Connections	in getTaxDetails() : "+sqle.toString());
          logger.error(FILE_NAME+"Error while	closing	Connections	in getTaxDetails() : "+sqle.toString());
				}
			}
			//return false;
			 return 	errors;
	}
    /**
   * 
   * @param taxMaster
   * @param esupplyGlobalParameters
   * @return 
   */
	private  String validate_TaxData( TaxMaster taxMaster,com.foursoft.esupply.common.bean.ESupplyGlobalParameters esupplyGlobalParameters)
	{
			
		////System.out.println("validate_TaxData..1");
		
		boolean	flag			= false;
		
		Connection connection	= null;		  
		PreparedStatement psmt	= null;		  
		ResultSet		  rs	= null;
		
		String	chargeId		= "";
		String  surchargeId		= "";
		String	errorFields		= "";
		chargeId				= taxMaster.getChargeId();
		surchargeId				= taxMaster.getSurchargeId();				
		////System.out.println("The chargeId-->"+chargeId+"SDurchargeId-->"+surchargeId+"TaxType-->"+taxMaster.getTaxType());
		//esupplyGlobalParameters.getTerminalId()
		String	sql1= " SELECT TAX_TYPE_ID,TAXID FROM FS_FR_TAXMASTER WHERE  TAX_TYPE_ID !='SURCHARGE' AND TRML_ID= ? AND TAXID=? "; 
		String	sql2= " SELECT CHARGEID FROM FS_FR_CHARGESMASTER WHERE CHARGEID=? ";
		try
		{
			connection	= operationsImpl.getConnection();
			psmt		= connection.prepareStatement(sql1);
			////System.out.println("new...0.1");
			int sur_count=0;
			int charge_count=0;
			
			if(taxMaster.getTaxType() != null && taxMaster.getTaxType().equals("FREIGHT AMOUNT")&& taxMaster.getTaxType().equals("INVOICE VALUE") && taxMaster.getTaxType().equals("EXCEPT FREIGHT") && taxMaster.getTaxType().equals("MANUAL"))
			{
				errorFields ="Correct" ;
				return errorFields; 
			}	
			if(taxMaster.getTaxType() != null && taxMaster.getTaxType().equals("SURCHARGE"))
			{
				psmt.setString(1,esupplyGlobalParameters.getTerminalId());
				psmt.setString(2,surchargeId);
				//@@ Srivegi Commented on 20050525 for 6542 Issue
				//	psmt.executeUpdate();
				//@@ 20050525 for 6542 Issue
				rs = psmt.executeQuery();
				////System.out.println("new...0.2");
				
				while(rs.next())
				{
				 	sur_count++;
	         	}
				////System.out.println("new...0.3");
        //Added By RajKumari on 30-10-2008 for Connection Leakages.
        if(psmt != null)
        {
          psmt.close();
          psmt=null;
        }
			  rs=null;
			}	
			////System.out.println("new...1");
			//SURCHARGE
			if(taxMaster.getTaxType() != null && taxMaster.getTaxType().equals("PER CHARGE"))
			{	
				psmt		= connection.prepareStatement(sql2);
				psmt.setString(1,chargeId);
				psmt.executeUpdate();
				rs = psmt.executeQuery();
				////System.out.println("new...2");
				while(rs.next())
				{
				 	charge_count++;
	      }
				////System.out.println("new...3"+charge_count);
				if(charge_count ==0 && taxMaster.getTaxType() != null && taxMaster.getTaxType().equals("PER CHARGE"))
				{
					errorFields = errorFields+"Wrong_ChargeId";
					return errorFields;
				}	
				else
				errorFields ="Correct" ;
				
			 
			}
			
			if(sur_count ==0 && taxMaster.getTaxType() != null && taxMaster.getTaxType().equals("SURCHARGE"))
			{errorFields = errorFields+"Wrong SurchargeId " ;}
			
			
			////System.out.println("new...4"+errorFields);
			if(sur_count >0 && charge_count >0)
			{errorFields ="Correct" ;}
			////System.out.println("new...5");	
			
		}
		catch(SQLException sqle	)
		{
			
			//Logger.error(FILE_NAME,"Error while	Inserting  in validate_TaxData : "+sqle.toString());
      logger.error(FILE_NAME+"Error while	Inserting  in validate_TaxData : "+sqle.toString());
			throw new EJBException(sqle.toString());
		}
		catch(javax.ejb.EJBException rme )
		{
			
			throw new EJBException("EJBException in validate_TaxData : "+rme.toString());
		}
		finally
		{
			try
			{
				if(	psmt !=	null)
					{psmt.close();}
//Connection Leakage -------------- 7-DEC-2004 ------------- Santhosam.P
				if(	rs !=	null)
					{rs.close();}
					
				if(	connection != null)
					{connection.close();}
			}
			catch( SQLException	sqle )
			{
				//Logger.error(FILE_NAME,"Error while	closing	Connections	in validate_TaxData :" +sqle.toString());
        logger.error(FILE_NAME+"Error while	closing	Connections	in validate_TaxData :" +sqle.toString());
			}
		}
		return	errorFields;
	}//	End	of insertTaxDetails()	
  
  public boolean addTaxMasterDetails( TaxMaster taxMaster,com.foursoft.esupply.common.bean.ESupplyGlobalParameters esupplyGlobalParameters,ArrayList countryList )
	{
			
			////System.out.println("this is inside addTaxMasterDetails...1");
			String taxId	=	null;					  // for storing taxId
			String desc		=	null;					   // for storing desc
			String remarks	=	null;						 //	for	storing	remarks
			boolean	flag			= false;
			
			Connection connection	= null;		  // Connection	object
			PreparedStatement psmt	= null;		  // preparedStatment object for INSERT_TAXDETAILS Query
			ResultSet		  rs	= null;
			taxId	= taxMaster.getTaxId();
			desc	= taxMaster.getDesc();
			remarks	= taxMaster.getRemarks();
			
			//////System.out.println("the chargeid-->"+taxMaster.getChargeId()+":taxType-->"+taxMaster.getTaxType()+":surchageID-->"+taxMaster.getSurchargeId()+"taxper-->"+taxMaster.getTaxPer());
			
			
			
			String taxTypeId ="";
			
			String sql1 = "SELECT TAX_TYPE_ID,TAX_TYPE_NAME FROM FS_FR_TAX_TYPE WHERE TAX_TYPE_NAME=? ";
			
			if(remarks == null ||remarks.equals("null"))
			{remarks	= "	";}
			 String	INSERT_TAXDETAILS =	"INSERT	INTO FS_FR_TAXMASTER VALUES(?,?,?,?,?,?,?,?)";
			try
			{
				connection	= operationsImpl.getConnection();
				
				psmt		= connection.prepareStatement(sql1);
				psmt.setString(1,taxMaster.getTaxType());
				rs = psmt.executeQuery();
				while(rs.next())
				{
					 taxTypeId            = rs.getString(1) ;
				}
				////System.out.println("this is inside addTaxMasterDetails...2 ::taxTypeId-->"+taxTypeId);
				psmt.clearParameters();
        //Added By RajKumari on 30-10-2008 for Connection Leakages.
				if(psmt != null)
        {
          psmt.close();
          psmt = null;
        }
				psmt		= connection.prepareStatement(INSERT_TAXDETAILS);
				psmt.setString(1,taxId);
				psmt.setString(2,desc);
				psmt.setString(3,remarks);
				psmt.setString(4,esupplyGlobalParameters.getTerminalId());
				psmt.setString(5,taxTypeId);
				psmt.setDouble(6,taxMaster.getTaxPer());
				psmt.setString(7,taxMaster.getSurchargeId());
				psmt.setString(8,taxMaster.getChargeId());
				
				psmt.executeUpdate();
				////System.out.println("this is inside addTaxMasterDetails...3");
				operationsImpl.setTransactionDetails(esupplyGlobalParameters.getTerminalId(),
										   esupplyGlobalParameters.getUserId(),
											   "Tax",
											   taxId,
											   esupplyGlobalParameters.getLocalTime(),
											   "Add" );
											   
				if(esupplyGlobalParameters.getAccessType()!= null && esupplyGlobalParameters.getAccessType().equals("HO_TERMINAL"))
	   			{
					
					try
					{
						////System.out.println("this is inside the addcountry taxes..");
						add_CountryTaxes(taxMaster,esupplyGlobalParameters,countryList);	
						
					}
					catch(Exception e)
					{
          //Logger.error(FILE_NAME,"Exception while adding in country tax details :" +e.toString());
          logger.error(FILE_NAME+"xception while adding in country tax details :" +e.toString());
						////System.out.println("Exception while adding in country tax details"+e.toString());
            throw new EJBException(e.toString());
					}			
						
				
	   			}//this is end of HO LEVEL adding.
			   
			   							   
											   
				flag = true;
				////System.out.println("this is inside addTaxMasterDetails...4");
			}
			catch(SQLException sqle	)
			{
				
				//Logger.error(FILE_NAME,"Error while	Inserting  in ETransHOSuperUserSetupSessionBean : "+sqle.toString());
        logger.error(FILE_NAME+"Error while	Inserting  in ETransHOSuperUserSetupSessionBean : "+sqle.toString());
				throw new EJBException(sqle.toString());
			}
			catch(javax.ejb.EJBException rme )
			{
				throw new EJBException("EJBException in insertTaxDetails : "+rme.toString());
			}
			finally
			{
				try
				{
					if(	psmt !=	null)
						{psmt.close();}
//Connection Leakage -------------- 7-DEC-2004 ------------- Santhosam.P
					if(	rs !=	null)
						{rs.close();}
						
					if(	connection != null)
						{connection.close();}
				}
				catch( SQLException	sqle )
				{
					//Logger.error(FILE_NAME,"Error while	closing	Connections	in insertTaxDetails() :" +sqle.toString());
          logger.error(FILE_NAME+"Error while	closing	Connections	in insertTaxDetails() :" +sqle.toString());
				}
			}
			return	flag;
	}//	End	of insertTaxDetails()
    
    /**
   * 
   * @param taxMaster
   * @param esupplyGlobalParameters
   * @param countryList
   * @return 
   */
	private  boolean add_CountryTaxes( TaxMaster taxMaster,com.foursoft.esupply.common.bean.ESupplyGlobalParameters esupplyGlobalParameters,ArrayList countryList )
	{
			
			////System.out.println("add_CountryTaxes..1");
			
			String taxId	=	null;					 
			String desc		=	null;					  
			String remarks	=	null;						
			boolean	flag			= false;
			
			Connection connection	= null;		  
			PreparedStatement psmt	= null;		  
			//ResultSet		  rs	= null;//Commented By RajKumari on 30-10-2008 for Connection Leakages.
			
			taxId	= taxMaster.getTaxId();
			desc	= taxMaster.getDesc();
			remarks	= taxMaster.getRemarks();
			
			String countryName ="";
			////System.out.println("the contry arraylist is -->"+countryList);
			if(countryList !=null)
			{
				int contryListSize	=	countryList.size();
				for(int i=0;i<contryListSize;i++)
				{	
					countryName = (String)countryList.get(i);
					////System.out.println("the Country Name-->"+countryName);
				}
			}	


			
			String sql1 = "INSERT INTO FS_FR_COUNTRY_TAX VALUES(?,?,?)";
			try
			{
				connection	= operationsImpl.getConnection();
				psmt		= connection.prepareStatement(sql1);
				if(countryList !=null)
				{
					int contryListSize	=	countryList.size();
					for(int i=0;i<contryListSize;i++)
					{	
						countryName = (String)countryList.get(i);
						////System.out.println("the Country Name-->"+countryName);
					 	psmt.setString(1,esupplyGlobalParameters.getTerminalId());
						psmt.setString(2,taxId);
						psmt.setString(3,countryName);				
						psmt.executeUpdate();
					}
				}
				////System.out.println("this is inside add_CountryTaxes...3");
											   
				flag = true;
				////System.out.println("this is inside add_CountryTaxes...4");
			}
			catch(SQLException sqle	)
			{
				
				//Logger.error(FILE_NAME,"Error while	Inserting  in ETransHOSuperUserSetupSessionBean : "+sqle.toString());
        logger.error(FILE_NAME+"Error while	Inserting  in ETransHOSuperUserSetupSessionBean : "+sqle.toString());
				throw new EJBException(sqle.toString());
			}
			catch(javax.ejb.EJBException rme )
			{
				
				throw new EJBException("EJBException in insertTaxDetails : "+rme.toString());
			}
			finally
			{
				try
				{
					if(	psmt !=	null)
						{psmt.close();}
//Connection Leakage -------------- 7-DEC-2004 ------------- Santhosam.P
					/*if(	rs !=	null)
						{rs.close();}*///Commented By RajKumari on 30-10-2008 for Connection Leakages.
						
					if(	connection != null)
						{connection.close();}
				}
				catch( SQLException	sqle )
				{
					//Logger.error(FILE_NAME,"Error while	closing	Connections	in insertTaxDetails() :" +sqle.toString());
          logger.error(FILE_NAME+"Error while	closing	Connections	in insertTaxDetails() :" +sqle.toString());
				}
			}
			return	flag;
	}//	End	of insertTaxDetails()	
    
    /**
   * 
   * @param taxMaster
   * @param esupplyGlobalParameters
   * @param countryList
   * @return 
   */
	public boolean updateTaxMasterDetails( TaxMaster taxMaster,com.foursoft.esupply.common.bean.ESupplyGlobalParameters esupplyGlobalParameters,ArrayList countryList )
	{
		
		////System.out.println("THIS IS INSIDE UpadeTaxMasterDetails....1");
		String taxId	=	null;					  // for storing taxId
		String desc		=	null;					   // for storing desc
		String remarks	=	null;						 //	for	storing	remarks
		boolean	flag		  =	false;
		boolean fla			  = false;
		Connection connection =	null;	// Connection Object
		Statement st		  =	null;	// Statement Object	for	executing UPDATE_TAXDETAILS	query
		PreparedStatement psmt	= null;		  // preparedStatment object for INSERT_TAXDETAILS Query
		ResultSet		  rs	= null;
		taxId	= taxMaster.getTaxId();
		taxMaster.setTaxId(taxId);
		desc	= taxMaster.getDesc();
		taxMaster.setDesc(desc);
		remarks	= taxMaster.getRemarks();
		if(remarks == null)	{remarks	= "";}
		taxMaster.setRemarks(remarks);
		String taxTypeId="";
		////System.out.println("THIS IS INSIDE UpadeTaxMasterDetails....2");
		////System.out.println("the chargeid-->"+taxMaster.getChargeId()+":taxType-->"+taxMaster.getTaxType()+":surchageID-->"+taxMaster.getSurchargeId()+"taxper-->"+taxMaster.getTaxPer());
			
		////System.out.println("THIS IS INSIDE UpadeTaxMasterDetails....3");
		String sql1 = "SELECT TAX_TYPE_ID,TAX_TYPE_NAME FROM FS_FR_TAX_TYPE WHERE TAX_TYPE_NAME=? ";
		
		//String UPDATE_TAXDETAILS= "UPDATE FS_FR_TAXMASTER SET TAXDESC ='"+desc+"', REMARKS ='"+remarks+"' ,TRML_ID='"+esupplyGlobalParameters.getTerminalId()+"', TAX_TYPE_ID='"+taxTypeId+"',TAX_PERCENT='"+taxMaster.getTaxPer()+"',SURCHARGE_TAX_ID='"+taxMaster.getSurchargeId()+"',CHARGEID='"+taxMaster.getChargeId()+"'  WHERE	TAXID =	'"+taxId+"'" ;
		try
		{
			connection = operationsImpl.getConnection();
			////System.out.println("THIS IS INSIDE UpadeTaxMasterDetails....4");
			psmt		= connection.prepareStatement(sql1);
			psmt.setString(1,taxMaster.getTaxType());
			rs = psmt.executeQuery();
			while(rs.next())
			{
			  taxTypeId            = rs.getString(1) ;
         	}
			////System.out.println("THIS IS INSIDE UpadeTaxMasterDetails....5");
			////System.out.println("this is inside UpadeTaxMasterDetails...6 ::taxTypeId-->"+taxTypeId);
			
			if(esupplyGlobalParameters.getAccessType()!= null && esupplyGlobalParameters.getAccessType().equals("OPER_TERMINAL"))
			{
				////System.out.println("THIS IS USED FOR CHECKING THE AMD CREATE THE DETAILS");
				fla=create_update_taxId(taxMaster,esupplyGlobalParameters);
			}	
			
			psmt.clearParameters();
			String surch_id  = taxMaster.getSurchargeId() ;
			String charge_id = taxMaster.getChargeId(); 
			if(surch_id == null || surch_id.equals("null"))
			{surch_id="";}
			
			if(charge_id == null || charge_id.equals("null"))
			{charge_id ="";	}
			String UPDATE_TAXDETAILS ="";
			if(fla == false)
			{UPDATE_TAXDETAILS= "UPDATE FS_FR_TAXMASTER SET TAXDESC ='"+desc+"', REMARKS ='"+remarks+"' , TAX_TYPE_ID='"+taxTypeId+"',TAX_PERCENT='"+taxMaster.getTaxPer()+"',SURCHARGE_TAX_ID='"+surch_id+"',CHARGEID='"+charge_id+"'  WHERE	TAXID =	'"+taxId+"' AND TRML_ID='"+esupplyGlobalParameters.getTerminalId()+"'";}
			
			st		= connection.createStatement();
			st.executeUpdate(UPDATE_TAXDETAILS);
			operationsImpl.setTransactionDetails(  esupplyGlobalParameters.getTerminalId(),
											   esupplyGlobalParameters.getUserId(),
										   "Tax",
										   taxId,
										  esupplyGlobalParameters.getLocalTime(),
									   "Modify"	);
									   
										   
			if(esupplyGlobalParameters.getAccessType()!= null && esupplyGlobalParameters.getAccessType().equals("HO_TERMINAL"))
			   {
				
				try
				{
					////System.out.println("this is inside the updateTaxMasterDetails taxes..");
					delete_CountryTaxes(taxId);
					add_CountryTaxes(taxMaster,esupplyGlobalParameters,countryList);	
					
				}
				catch(Exception e)
				{
        //Logger.error(FILE_NAME," Exception while adding in country tax details : "+e.toString());
        logger.error(FILE_NAME+" Exception while adding in country tax details : "+e.toString());
					////System.out.println("Exception while adding in country tax details"+e.toString());
				}			
					
			
			   }						   
			flag = true;
		}
		catch(SQLException sqle	)
		{
			
			//Logger.error(FILE_NAME,"SQLException while Updating	Tax	Details	"+sqle.toString());
      logger.error(FILE_NAME+"SQLException while Updating	Tax	Details	"+sqle.toString());
			throw new EJBException(sqle.toString());
		}
		catch(javax.ejb.EJBException rme )
		{
			throw new EJBException("EJBException while updating TaxDetails :"+rme.toString());
		}
		finally
		{
			try
			{
				if(	st != null)
					{st.close();}
				if(	psmt != null)
					{psmt.close();}
				if(	rs != null)
					{rs.close();}
				if(	connection != null)
					{connection.close();}
			}
			catch(Exception	sqle )
			{
				sqle.printStackTrace();
			}
		}
		return	flag;
	} // End of	updateTaxDetails()
		
    //new method is added
  /**
   * 
   * @param taxMaster
   * @param esupplyGlobalParameters
   * @return 
   */
private  boolean create_update_taxId(TaxMaster taxMaster,com.foursoft.esupply.common.bean.ESupplyGlobalParameters esupplyGlobalParameters)
{
			
	////System.out.println("create_update_taxId..1");
	
	boolean	flag			= false;
	
	Connection connection	= null;		  
	PreparedStatement psmt	= null;		  
	ResultSet		  rs	= null;
	
	String sql1 = " SELECT TAXID,TRML_ID FROM FS_FR_TAXMASTER WHERE TAXID=? AND TRML_ID=? " ;
	try
	{
		connection	= operationsImpl.getConnection();
		psmt		= connection.prepareStatement(sql1);
		psmt.setString(1,taxMaster.getTaxId());
		psmt.setString(2,esupplyGlobalParameters.getTerminalId());
		rs = psmt.executeQuery();
		int records =0;
		while(rs.next())
		{
			records++;
			flag=true;
		}
		
		if(records ==0)
		{addTaxMasterDetails(taxMaster,esupplyGlobalParameters,null);}
		else
		{flag = false;}							   
		//flag = true;
		
	}
	catch(SQLException sqle	)
	{
		//Logger.error(FILE_NAME,"Error while	create_update taxes in ETransHOSuperUserSetupSessionBean : "+sqle.toString());
    logger.error(FILE_NAME+"Error while	create_update taxes in ETransHOSuperUserSetupSessionBean : "+sqle.toString());
		throw new EJBException(sqle.toString());

	}
	catch(javax.ejb.EJBException rme )
	{

		throw new EJBException("EJBException in insertTaxDetails : "+rme.toString());
	}
	finally
	{
		try
		{
			if(	psmt !=	null)
				{psmt.close();}
//Connection Leakage -------------- 7-DEC-2004 ------------- Santhosam.P
			if(	rs !=	null)
				{rs.close();}
				
			if(	connection != null)
				{connection.close();}
		}
		catch( SQLException	sqle )
		{
			//Logger.error(FILE_NAME,"Error while	closing	Connections	in insertTaxDetails() :" +sqle.toString());
      logger.error(FILE_NAME+"Error while	closing	Connections	in insertTaxDetails() :" +sqle.toString());
		}
	}
		return	flag;
	}
 //end of new private method
 
 /**
   * 
   * @param taxId
   * @return 
   */
	private  boolean delete_CountryTaxes(String taxId)
	{
			
			////System.out.println("delete_CountryTaxes..1");
			
			boolean	flag			= false;
			
			Connection connection	= null;		  
			PreparedStatement psmt	= null;		  
			//ResultSet		  rs	= null;//Commented By RajKumari on 30-10-2008 for Connection Leakages.
			
			String sql1 = "DELETE  FROM  FS_FR_COUNTRY_TAX WHERE TAXID=?";
			try
			{
				connection	= operationsImpl.getConnection();
				psmt		= connection.prepareStatement(sql1);
				psmt.setString(1,taxId);
				//@@ Avinash replaced on 20041212 
				//psmt.executeQuery();
				 psmt.executeUpdate();
				//@@ 20041212
				
				////System.out.println("this is inside delete_CountryTaxes...2");
											   
				flag = true;
				////System.out.println("this is inside delete_CountryTaxes...3");
			}
			catch(SQLException sqle	)
			{
				
				//Logger.error(FILE_NAME,"Error while	delte country taxes in ETransHOSuperUserSetupSessionBean : "+sqle.toString());
        logger.error(FILE_NAME+"Error while	delte country taxes in ETransHOSuperUserSetupSessionBean : "+sqle.toString());
				throw new EJBException(sqle.toString());
			}
			catch(javax.ejb.EJBException rme )
			{
				throw new EJBException("EJBException in insertTaxDetails : "+rme.toString());
			}
			finally
			{
				try
				{
					if(	psmt !=	null)
						{psmt.close();}
					/*if(	rs !=	null)
						{rs.close();}*///Commented By RajKumari on 30-10-2008 for Connection Leakages.
						
					if(	connection != null)
						{connection.close();}
				}
				catch( SQLException	sqle )
				{
					//Logger.error(FILE_NAME,"Error while	closing	Connections	in insertTaxDetails() :" +sqle.toString());
          logger.error(FILE_NAME+"Error while	closing	Connections	in insertTaxDetails() :" +sqle.toString());
				}
			}
			return	flag;
	}//	End	of delete_country_taxes()	
	//###################### end of new method ##########################
    
    /**
   * 
   * @param taxMaster
   * @param esupplyGlobalParameters
   * @return 
   */

	public boolean deleteTaxMasterDetails( TaxMaster taxMaster,com.foursoft.esupply.common.bean.ESupplyGlobalParameters esupplyGlobalParameters)
	{
		String taxId	=	null;					  // for storing taxId
		String desc		=	null;					   // for storing desc
		String remarks	=	null;						 //	for	storing	remarks
		boolean	flag			= false;
		Connection connection	= null;		// Connection Object
		Statement st			= null;		// Statement Object	for	DELETE_TAXDETAILS Query
		taxId					= taxMaster.getTaxId();
		taxMaster.setTaxId(taxId);
		desc					= taxMaster.getDesc();
		taxMaster.setDesc(taxId);
		remarks					= taxMaster.getRemarks();
		taxMaster.setRemarks(remarks);
		// @@ Added by Anup on 20050127
		Statement            stmt			=   null;
		ResultSet			 rs				=	null;
		int	                 count		    =	0;
		   // @@ 20050127
		//String DELETE_TAXDETAILS= "DELETE FROM FS_FR_TAXMASTER WHERE TAXID = '"+taxId+"'";
		String DELETE_TAXDETAILS= "DELETE FROM FS_FR_TAXMASTER WHERE TAXID = '"+taxId+"'  AND TRML_ID ='"+esupplyGlobalParameters.getTerminalId()+"' ";
		// @@ Added by Anup
		  String sql_count = "SELECT COUNT(*) NO_ROWS FROM FS_FR_FRTINVOICETAXES WHERE TAXID= '"+taxId+"' ";
           // @@
		try
		{
			 // @@ Anup added on 20050127
			connection = operationsImpl.getConnection();
			stmt= connection.createStatement();	
			rs 	= stmt.executeQuery(sql_count );
			
			if(rs.next())
				count = rs.getInt("NO_ROWS"); // getting count from the database.
			if(count >= 1)
				return false;
			else
			  {   // @@ 20050127
			if(esupplyGlobalParameters.getAccessType()!= null && esupplyGlobalParameters.getAccessType().equals("HO_TERMINAL"))
	   		{		
				try
				{
					////System.out.println("this is inside the DELTE tAX MASTER..");
					delete_CountryTaxes(taxId);	
						
				}
				catch(Exception e)
				{
            //Logger.error(FILE_NAME," Exception while adding in country tax details : "+e.toString());
            logger.error(FILE_NAME+" Exception while adding in country tax details : "+e.toString());
						////System.out.println("Exception while adding in country tax details"+e.toString());
            throw new EJBException(e.toString());
				}			
					
	   		}//this is end of HO LEVEL adding.	
			
			
			connection = operationsImpl.getConnection();
			st		= connection.createStatement();
			st.executeUpdate(DELETE_TAXDETAILS);
			operationsImpl.setTransactionDetails(  esupplyGlobalParameters.getTerminalId(),
										   esupplyGlobalParameters.getUserId(),
										   "Tax",
										   taxId,
										   esupplyGlobalParameters.getLocalTime(),
										   "Delete"	);
										   
										   
										   
			flag = true;
		}
		}//Ends here
		catch(SQLException sqle	)
		{
			//Logger.error(FILE_NAME,"SQLException while deleting	Tax	Details	:"+sqle.toString());
      logger.error(FILE_NAME+"SQLException while deleting	Tax	Details	:"+sqle.toString());
			throw new EJBException(sqle.toString());

		}
		catch(javax.ejb.EJBException rme )
		{

			throw new EJBException("EJBException while deleting Tax Details "+rme.toString());
		}
		finally
		{
			try
			{
        if(rs!=null)
        {rs.close();}
        if(stmt!=null)
        {stmt.close();}
				if(	st != null)
					{st.close();}
				if(	connection != null)
					{connection.close();}
			}
			catch( SQLException	sqle )
			{
				//Logger.error(FILE_NAME,"Error while	closing	Connections	in deleteTaxDetails() :	"+sqle.toString());
        logger.error(FILE_NAME+"Error while	closing	Connections	in deleteTaxDetails() :	"+sqle.toString());
			}
		}
		return	flag;
	}//	End	of deleteTaxDetails()
    
    public ArrayList getTaxMasterTaxIds(String searchString,com.foursoft.esupply.common.bean.ESupplyGlobalParameters esupplyGlobalParameters,String ForsurChargeId,String tax_id,String operation) 
  {
		Statement  stmt					= null;						 //	Statement Object  for executing	this query
		ResultSet  resultset			= null;						 //	ResultSet Object for this query
		Connection connection			= null;						//	Connection object
		ArrayList  taxMaster			=  new ArrayList();		
	   
		try
		{
			connection = operationsImpl.getConnection();
			String queryCond ="";
			if(operation != null && operation.equals("Modify"))
			{queryCond = "AND TAXID !='"+tax_id+"'" ;	}
			
			String sql = "SELECT TAXID,TAXDESC,TRML_ID FROM FS_FR_TAXMASTER WHERE  TRML_ID='"+esupplyGlobalParameters.getTerminalId()+"' AND TAXID LIKE '"+searchString+"%' ORDER BY TAXID " ;
			if(esupplyGlobalParameters.getAccessType()!= null && esupplyGlobalParameters.getAccessType().equals("OPER_TERMINAL"))
			{  
				//@@ Avinash replaced on 20050301
				//if (operation.equals("View"))
				if ("View".equals(operation))
				//@@ 20050301
				{
					sql	  =	" SELECT TM.TAXID,TM.TAXDESC,TM.TRML_ID FROM FS_FR_TAXMASTER TM "+
						" WHERE TAXID LIKE '"+searchString+"%'  AND TM.TRML_ID ='"+esupplyGlobalParameters.getTerminalId()+"'"+
						" UNION "+
						" SELECT CT.TAXID,TN.TAXDESC,CT.TRML_ID FROM FS_FR_COUNTRY_TAX CT,"+
						" FS_FR_TAXMASTER TN WHERE COUNTRYID IN (SELECT COUNTRYID "+
						" FROM FS_FR_TERMINALMASTER T,FS_ADDRESS WHERE "+
						" CONTACTADDRESSID=ADDRESSID AND TERMINALID='"+esupplyGlobalParameters.getTerminalId()+"') "+
						" AND TN.TAXID=CT.TAXID	  AND TN.TRML_ID=CT.TRML_ID "+
						" AND TN.TAXID NOT IN(SELECT TAXID FROM FS_FR_TAXMASTER WHERE TRML_ID ='"+esupplyGlobalParameters.getTerminalId()+"')"+ 
						" AND CT.TAXID LIKE '"+searchString+"%'  " ;
				}else // @@ Anup Added for SPETI-3718 on 20050124
				{
					sql = "SELECT TAXID,TAXDESC,TRML_ID FROM FS_FR_TAXMASTER WHERE  TRML_ID='"+esupplyGlobalParameters.getTerminalId()+"' AND TAXID LIKE '"+searchString+"%' ORDER BY TAXID " ;

				} // @@ 20050124
				
		   
			}//this is end of HO LEVEL adding.
			////System.out.println("the value of forsurchargeis is --->"+ForsurChargeId);
			if(ForsurChargeId !=null && ForsurChargeId.equals("yes"))
			{
				////System.out.println("this is inside");
				//sql =  "SELECT TAXID,TAXDESC,TRML_ID FROM FS_FR_TAXMASTER WHERE TAXID LIKE '"+searchString+"%'   AND TRML_ID ='"+esupplyGlobalParameters.getTerminalId()+"' AND  TAX_TYPE_ID !='SURCHARGE' ORDER BY TAXID " ;
				
				  sql =  " SELECT TM.TAXID,TM.TAX_TYPE_ID ,TM.TAXDESC,TM.TRML_ID FROM FS_FR_TAXMASTER TM "+
						 " WHERE TAXID LIKE '"+searchString+"%'   AND  TM.TRML_ID ='"+esupplyGlobalParameters.getTerminalId()+"' AND  TM.TAX_TYPE_ID  !='SURCHARGE'   "+
						 " UNION "+
						 " SELECT CT.TAXID,TN.TAX_TYPE_ID ,TN.TAXDESC,CT.TRML_ID FROM FS_FR_COUNTRY_TAX CT, "+
						 " FS_FR_TAXMASTER TN WHERE COUNTRYID IN (SELECT COUNTRYID "+ 
						 " FROM FS_FR_TERMINALMASTER T,FS_ADDRESS WHERE "+
						 " CONTACTADDRESSID=ADDRESSID AND TERMINALID='"+esupplyGlobalParameters.getTerminalId()+"')  "+
						 " AND TN.TAXID=CT.TAXID	  AND TN.TRML_ID=CT.TRML_ID AND TN.TAXID NOT IN(SELECT TAXID FROM FS_FR_TAXMASTER WHERE TRML_ID ='"+esupplyGlobalParameters.getTerminalId()+"') "+
						 " AND CT.TAXID!='SURCHARGE'  AND TN.TAX_TYPE_ID !='SURCHARGE' AND "+
						 " CT.TAXID LIKE '"+searchString+"%'  " 	   ;	 
			}		
			stmt	   = connection.createStatement();
			////System.out.println("the sql is-->"+sql);
			resultset  = stmt.executeQuery(sql);
			//int noIds =0;
		/*	while(resultset.next())
			{
				noIds++ ;
			}
			////System.out.println("The no ids are --->"+noIds);
			taxMaster    = new TaxMaster[noIds];
			noIds =0;
			resultset  = stmt.executeQuery(sql);
	 */
			while(resultset.next())
			{
				//String temp	= (resultset.getString(1)).concat("	 ["+resultset.getString(2)+"]");
				String temp	= resultset.getString(1) + " [" + resultset.getString(2) + "]" ;
				taxMaster.add(temp);
				/*taxIds.put(resultset.getString(3),temp);
				taxMaster[noIds] = new TaxMaster();
				taxMaster[noIds].setDesc(temp);
				taxMaster[noIds].setTermId(resultset.getString(3));
				noIds++;*/
			}
		}
		catch(Exception	sqle ){
			sqle.printStackTrace();
			//Logger.error(FILE_NAME,"Error in getTaxIds Method :	"+sqle.toString());
      logger.error(FILE_NAME+"Error in getTaxIds Method :	"+sqle.toString());
			throw new EJBException(sqle.toString());
		}
		finally{
			try{
				if(	resultset != null){
					resultset.close();
				}
				if(	stmt !=	null){
					stmt.close();
				}
				if(	connection != null){
					connection.close();
				}
			}catch(	SQLException sqle ){
				//Logger.error(FILE_NAME,"Error while	closing	Connections	in getTaxIds() :" +sqle.toString());
        logger.error(FILE_NAME+"Error while	closing	Connections	in getTaxIds() :" +sqle.toString());
			}
		}
		return taxMaster;
	}	

//@@ Added By Naresh  8/19/2005
//ListTypeIds   searchString  shipmentMode        ArrayList
public java.util.ArrayList getListDetails()
  
  {
    Connection connection=null;
    Statement stmt=null;
    ResultSet listRs=null;
    ArrayList airListmaster=new ArrayList();
    ArrayList seaListmaster=new ArrayList();
    ArrayList truckListmaster=new ArrayList();
    ArrayList entireList=new ArrayList();
    String listQuery="SELECT SHIPMENT_MODE,UOV,UOM,LIST_TYPE,LIST_DESCRIPTION,VOLUME,PIOVT_UNLADEN_WEIGHT,OVER_PIVOT_TARE_WEIGHT,INVALIDATE  FROM QMS_LISTMASTER"; 
    try
    {
      connection=operationsImpl.getConnection();
      stmt=connection.createStatement();
      listRs=stmt.executeQuery(listQuery);
      while(listRs.next())
      {
        ListMasterDOB listMasterDOB = new ListMasterDOB();
        String shipmentMode=listRs.getString("SHIPMENT_MODE");
        
		    listMasterDOB.setShimpmentMode(shipmentMode);
        listMasterDOB.setUov(listRs.getString("UOV"));
        listMasterDOB.setUom(listRs.getString("UOM"));
        listMasterDOB.setUldType(listRs.getString("LIST_TYPE"));
        listMasterDOB.setDescription(listRs.getString("LIST_DESCRIPTION"));
        listMasterDOB.setVolume(listRs.getString("VOLUME"));
        listMasterDOB.setPivoteUladenWeight(listRs.getString("PIOVT_UNLADEN_WEIGHT"));
        listMasterDOB.setOverPivoteTareWeight(listRs.getString("OVER_PIVOT_TARE_WEIGHT"));
		listMasterDOB.setInvalidate(listRs.getString("INVALIDATE"));
        if(shipmentMode.equals("1"))
        {
          airListmaster.add(listMasterDOB);
        }
        else if(shipmentMode.equals("2"))
        {
          seaListmaster.add(listMasterDOB);
        }
        else if(shipmentMode.equals("4"))
        {
          truckListmaster.add(listMasterDOB);
        }
        entireList.add(airListmaster);
        entireList.add(seaListmaster);
        entireList.add(truckListmaster);
      }
    }catch(SQLException excep)
    {
		
      System.out.println("exception caught at getListDetails():"+excep);
	  throw new EJBException(excep.toString());
    }
    finally
    {
      ConnectionUtil.closeConnection(connection,stmt,listRs);
    }
  return entireList;

  }
  public boolean invalidateListMaster(ArrayList dobList)
  {
    ListMasterDAO listmasterDAO= new ListMasterDAO();
     try
    {
      listmasterDAO.invalidateListMaster(dobList);
      
    }catch(Exception exct)
    {
	  exct.printStackTrace();
      return false;
    }
    finally
    {
      
    }
    return true;
  }

   public ArrayList insertListMasterDetails(ArrayList dataList)
   {
      ArrayList insertedList    = new ArrayList();
    ArrayList invalidList     = new ArrayList();
    ArrayList returnList      = new ArrayList();
    ListMasterEntityBeanLocalHome home=null;
    ListMasterEntityBeanLocal local=null;
    ListMasterDOB listMasterDOB=null;
    try
    {
    	int dataListSize	=	dataList.size();
      for(int i=0;i<dataListSize;i++)
      {
        listMasterDOB=(ListMasterDOB)dataList.get(i);
        if(isValidListMasterData(listMasterDOB))
        {
          insertedList.add(listMasterDOB);
        }else
        {
          invalidList.add(listMasterDOB);
        }
      }
      returnList.add(insertedList);
      returnList.add(invalidList);
      if(insertedList!=null && insertedList.size()>0)
      {
        home  = (ListMasterEntityBeanLocalHome)LookUpBean.getEJBLocalHome("java:comp/env/ListMasterEntityBean");
        local = (ListMasterEntityBeanLocal)home.create(insertedList);
      }  
     
    }catch(NamingException nex)
    {
        //Logger.error(FILE_NAME,"insertListMasterDetails()",nex.toString());
        logger.error(FILE_NAME+"insertListMasterDetails()"+nex.toString());
        throw new EJBException();
    }catch(Exception e)
    {
        //Logger.error(FILE_NAME,"insertListMasterDetails()",e.toString());
        logger.error(FILE_NAME+"insertListMasterDetails()"+e.toString());
        throw new EJBException();      
    }
      return returnList;
     
    
     
   }
   public boolean updateListMasterDetails(ListMasterDOB listMasterDOB)throws EJBException,ObjectNotFoundException
  {
    ListMasterEntityBeanLocalHome  home  = null;
    ListMasterEntityBeanLocal      local = null;
    ListMasterEntityBeanPK        pkObj = new ListMasterEntityBeanPK();
    boolean   success                     = false;
    try
    {
      pkObj.shipmentMode  = listMasterDOB.getShipmentMode();
      pkObj.listType   = listMasterDOB.getUldType();
      
      home  = (ListMasterEntityBeanLocalHome)LookUpBean.getEJBLocalHome("java:comp/env/ListMasterEntityBean");
      local = (ListMasterEntityBeanLocal)home.findByPrimaryKey(pkObj);
      local.setListMasterDOB(listMasterDOB);
      success = true;
    }catch(ObjectNotFoundException e)
    {
        success = false;    
        //Logger.error(FILE_NAME,"updateListMasterDetails()",e.toString());
        logger.error(FILE_NAME+"updateListMasterDetails()"+e.toString());
        throw new ObjectNotFoundException("Bean could not find");      
    }catch(NamingException e)
    {
        success = false;
        //Logger.error(FILE_NAME,"updateListMasterDetails()",e.toString());
        logger.error(FILE_NAME+"updateListMasterDetails()"+e.toString());
        throw new EJBException();      
    }catch(FinderException e)
    {
        success = false;    
        //Logger.error(FILE_NAME,"updateListMasterDetails()",e.toString());
        logger.error(FILE_NAME+"updateListMasterDetails()"+e.toString());
        throw new EJBException();      
    }catch(Exception e)
    {
        success = false;    
        //Logger.error(FILE_NAME,"updateListMasterDetails()",e.toString());
        logger.error(FILE_NAME+"updateListMasterDetails()"+e.toString());
        throw new EJBException();     
    }
    return success;
  }
   public boolean deleteListMasterDtls(ListMasterDOB listMasterDOB)throws EJBException,ObjectNotFoundException
  {
    ListMasterEntityBeanLocalHome  home  = null;
    ListMasterEntityBeanLocal      local = null;
    ListMasterEntityBeanPK         pkObj = new ListMasterEntityBeanPK();
    boolean   success                     = false;
    try
    {
      pkObj.shipmentMode  = listMasterDOB.getShipmentMode();
      pkObj.listType   = listMasterDOB.getUldType();
      home  = (ListMasterEntityBeanLocalHome)LookUpBean.getEJBLocalHome("java:comp/env/ListMasterEntityBean");
      local = (ListMasterEntityBeanLocal)home.findByPrimaryKey(pkObj);
      local.remove();
      success = true;
    }catch(NamingException e)
    {
        success = false;
        //Logger.error(FILE_NAME,"deleteListMasterDtls()",e.toString());
        logger.error(FILE_NAME+"deleteListMasterDtls()"+e.toString());
        throw new EJBException();      
    }catch(ObjectNotFoundException e)
    {
        success = false;    
        //Logger.error(FILE_NAME,"deleteListMasterDtls()",e.toString());
        logger.error(FILE_NAME+"deleteListMasterDtls()"+e.toString());
        throw new ObjectNotFoundException("Bean could not find");      
    }catch(FinderException e)
    {
        success = false;    
        //Logger.error(FILE_NAME,"deleteListMasterDtls()",e.toString());
        logger.error(FILE_NAME+"deleteListMasterDtls()"+e.toString());
        throw new EJBException();      
    }catch(Exception e)
    {
        success = false;    
        //Logger.error(FILE_NAME,"deleteListMasterDtls()",e.toString());
        logger.error(FILE_NAME+"deleteListMasterDtls()"+e.toString());
        throw new EJBException();     
    }
    return success;
  }
   private boolean isValidListMasterData(ListMasterDOB listMasterDOB)
  {
    PreparedStatement pstmt = null;
    ResultSet         rs    = null;
    Connection  connection  = null;
     String   listExisQuery = "SELECT LIST_TYPE  FROM QMS_LISTMASTER WHERE LIST_TYPE=? AND SHIPMENT_MODE =?";
    boolean   isValid       = false;
    try
    {
      connection  = operationsImpl.getConnection();
      pstmt = connection.prepareStatement(listExisQuery);
      pstmt.setString(1,listMasterDOB.getUldType());
      pstmt.setString(2,listMasterDOB.getShipmentMode());
      rs  = pstmt.executeQuery();
      if(rs.next())
      {
       isValid = false;
       
        return isValid;
       
		
      }else
      {
        isValid = true;
      }
     
    }catch(SQLException e)
    {
      isValid = false;
      //Logger.error(FILE_NAME,"Error while updatating the details --- isValidListMasterData()"+e.toString());
      logger.error(FILE_NAME+"Error while updatating the details --- isValidListMasterData()"+e.toString());
      throw new EJBException();      
    }catch(Exception e)
    {
      isValid = false;
      //Logger.error(FILE_NAME,"Error while updatating the details --- isValidListMasterData()"+e.toString());
      logger.error(FILE_NAME+"Error while updatating the details --- isValidListMasterData()"+e.toString());
      throw new EJBException();     
    }finally
    {
      try
      {
        if(rs!=null)
          { rs.close();}
        if(pstmt!=null)
          { pstmt.close();}
        if(connection!=null)
          { connection.close();}
      }catch(SQLException e)
      {
        isValid = false;
        //Logger.error(FILE_NAME,"Error while updatating the details --- isValidListMasterData()"+e.toString());
        logger.error(FILE_NAME+"Error while updatating the details --- isValidListMasterData()"+e.toString());
        throw new EJBException();      
      }
    }
    
    return isValid;
    
  }
  public ArrayList getListTypeIds(String searchString,String shipmentMode,String operation,String terminalId)
	{
		Connection	connection	= null;
		//Statement	desiStmt	=null;//Commented By RajKumari on 30-10-2008 for Connection Leakages.
		ResultSet	desiResutltSet=null;
		ArrayList	listTypeIds= new ArrayList();
		CallableStatement cstmt  = null;
    if(shipmentMode.equals("Air"))
    {
      shipmentMode="1";
    }
    else if(shipmentMode.equals("Sea"))
    {
      shipmentMode="2";
    }
    else if(shipmentMode.equals("Truck"))
    {
      shipmentMode="4";
    }/*
		if(searchString!=null && !(searchString.equals("")))
		{
			searchString = "AND LIST_TYPE LIKE '"+searchString+"%'";
		}
		else
		{
			searchString = "";
		}*/
 		if(searchString==null)
           searchString ="";
		//String sql = "SELECT LIST_TYPE FROM QMS_LISTMASTER WHERE SHIPMENT_MODE='"+shipmentMode+"' "+searchString;

	    //Logger.info(FILE_NAME,"  getListTypeIds(String searchString,String shipmentMode)"+sql);
	    try
        {
			connection = this.getConnection();
            //desiStmt = connection.createStatement();
            //desiResutltSet = desiStmt.executeQuery(sql);
		  cstmt        = connection.prepareCall("{ ?= call QMS_SETUPS.GET_LISTMASTER(?,?,?,?)}");
          cstmt.registerOutParameter(1,OracleTypes.CURSOR);
          cstmt.setString(2,terminalId);
          cstmt.setString(3,operation);
          cstmt.setString(4,shipmentMode);
          cstmt.setString(5,searchString+"%");
          cstmt.execute();
          desiResutltSet  =  (ResultSet)cstmt.getObject(1);
            while(desiResutltSet.next())
			{
				listTypeIds.add(desiResutltSet.getString(1));
			}
        }
        catch(SQLException sqEx)
        {sqEx.printStackTrace();
			//Logger.error(FILE_NAME, "[getDesignationIs(whereclause)] -> "+sqEx.toString());
      logger.error(FILE_NAME+ "[getDesignationIs(whereclause)] -> "+sqEx.toString());
            throw new EJBException(sqEx.toString());
        }
        catch(Exception sqEx)
        {sqEx.printStackTrace();
			//Logger.error(FILE_NAME, "[getDesignationIs(whereclause)] -> "+sqEx.toString());
      logger.error(FILE_NAME+ "[getDesignationIs(whereclause)] -> "+sqEx.toString());
            throw new EJBException(sqEx.toString());
        }
        finally
        {
			ConnectionUtil.closeConnection(connection, cstmt, desiResutltSet);
        }
        return listTypeIds;
	}
  public ListMasterDOB getListMasterDetails(String shipmentType,String listType)throws EJBException,ObjectNotFoundException
  {
    ListMasterEntityBeanLocalHome  home  = null;
    ListMasterEntityBeanLocal      local = null;
    ListMasterEntityBeanPK         pkObj = new ListMasterEntityBeanPK();
    ListMasterDOB listMasterDOB = null;
    try
    {
		if(shipmentType.equals("Air"))
			shipmentType="1";
		else if(shipmentType.equals("Sea"))
			shipmentType="2";
		else if(shipmentType.equals("Truck"))
			shipmentType="4";
      pkObj.shipmentMode = shipmentType; 
      pkObj.listType   = listType;
            
      home  = (ListMasterEntityBeanLocalHome)LookUpBean.getEJBLocalHome("java:comp/env/ListMasterEntityBean");     
      local = (ListMasterEntityBeanLocal)home.findByPrimaryKey(pkObj);
      listMasterDOB = local.getListeMasterDOB();
    }catch(ObjectNotFoundException e)
    {
        //Logger.error(FILE_NAME,"getListMasterDetails()",e.toString());
        logger.error(FILE_NAME+"getListMasterDetails()"+e.toString());
        throw new ObjectNotFoundException();      
    }catch(NamingException nex)
    {
        //Logger.error(FILE_NAME,"getListMasterDetails()",nex.toString());
        logger.error(FILE_NAME+"getListMasterDetails()"+nex.toString());
        throw new EJBException();
    }catch(FinderException e)
    {
        //Logger.error(FILE_NAME,"getListMasterDetails()",e.toString());
        logger.error(FILE_NAME+"getListMasterDetails()"+e.toString());
        throw new EJBException();      
    }catch(Exception e)
    {
        //Logger.error(FILE_NAME,"getListMasterDetails()",e.toString());
        logger.error(FILE_NAME+"getListMasterDetails()"+e.toString());
        throw new EJBException();      
    }
    return listMasterDOB;
  }

public ArrayList getLocIds(String searchString,String searchString2,String terminalId,String operation,String shipmentMode)
{
        ArrayList      vec              = new ArrayList();
        Connection     connection       = null;
        //Statement      stmt             = null;//Commented By RajKumari on 30-10-2008 for Connection Leakages.
        ResultSet      rs               = null;
		String		   sMode			= "";	
		String		   temp				= "";	
		String		   locId			= "";
		String		   locations		="";
		String			countrys		="";
		CallableStatement cstmt  = null;
    String   locationIds    = "";

		
        try
        {
              countrys     =  searchString2.replaceAll(",","','");
              locationIds  =  searchString.replaceAll(",","','");
              //	searchString2 = " AND CON.COUNTRYID IN('"+countrys+"') ";
			
              connection = this.getConnection();
              //stmt = connection.createStatement();
              //String sqlQuery = "SELECT DISTINCT LOCATIONID,LOCATIONNAME,SHIPMENTMODE FROM   FS_FR_LOCATIONMASTER LOC, FS_COUNTRYMASTER CON WHERE LOC.COUNTRYID=CON.COUNTRYID  "+searchString2 +" ORDER BY LOCATIONID ";
              //rs = stmt.executeQuery(sqlQuery);
              cstmt        = connection.prepareCall("{ ?= call QMS_SETUPS.GET_LOCIDS(?,?,?,?,?)}");
              cstmt.registerOutParameter(1,OracleTypes.CURSOR);
              cstmt.setString(2,terminalId);
              cstmt.setString(3,operation);
              cstmt.setString(4,(locationIds));
              cstmt.setString(5,countrys); 
              cstmt.setString(6,shipmentMode);          
              //System.out.println("countrys   "+countrys);
             // System.out.println("locationIds   "+locationIds);
              cstmt.execute();
              rs  =  (ResultSet)cstmt.getObject(1);
            while(rs.next())
            {

              // @@ Added by G.Srinivas for TogetherJ FIX on 20050117
              StringBuffer   sMode1            = new StringBuffer();
              StringBuffer   locId1           = new StringBuffer();
              // @@
      
              locId = rs.getString(1);	
                      temp = rs.getString(2);
              sMode = rs.getString(3);
                      if(sMode==null)
        
              {  sMode="   ";}
                          
              if(sMode!=null)
              { 
                if(sMode.equals("7"))	{sMode="AST";}
                if(sMode.equals("1"))	{sMode="A  ";}
                if(sMode.equals("2"))	{sMode=" S ";}		
                if(sMode.equals("3"))	{sMode="AS ";}
                if(sMode.equals("4"))	{sMode="  T";}
                if(sMode.equals("5"))	{sMode="A T";}
                if(sMode.equals("6"))	{sMode=" ST";}
              }	
                
              if(locId.length() == 2)	
              //{locId = locId+" ";	}
      
              // @@ Replaced by G.Srinivas for TogetherJ on 20050105
                       {locId1.append(locId);
                locId1.append(" ");
                locId = locId1.toString();}
              // @@
      
              if(locId.length() == 1)
      
              //{locId = locId+"  ";	}
              // @@ Replaced by G.Srinivas for TogetherJ on 20050105
                 {locId1.append(locId);
                locId1.append(" ");
                locId = locId1.toString();}	
              // @@
      
              //sMode = locId+"[" +sMode+ "]"+"[" +temp+"]"; 
              // @@ Replaced by G.Srinivas for TogetherJ on 20050105
                sMode1.append(locId);
                sMode1.append("[" );
                sMode1.append(sMode);
                sMode1.append( "]");
                sMode1.append("[" );
                sMode1.append(temp);
                sMode1.append( "]");
                sMode = sMode1.toString();
                      // @@
                      vec.add(sMode);
          }
        }
        catch(SQLException sqEx)
        {sqEx.printStackTrace();
			//Logger.error(FILE_NAME, "[getLocationIds(whereclause)] -> "+sqEx.toString());
      logger.error(FILE_NAME+ "[getLocationIds(whereclause)] -> "+sqEx.toString());
            throw new EJBException(sqEx.toString());
        }
        catch(Exception sqEx)
        {sqEx.printStackTrace();
			//Logger.error(FILE_NAME, "[getLocationIds(whereclause)]   -> "+sqEx.toString());
      logger.error(FILE_NAME+ "[getLocationIds(whereclause)]   -> "+sqEx.toString());
            throw new EJBException(sqEx.toString());
        }
        finally
        {
			ConnectionUtil.closeConnection(connection, cstmt, rs);
        }
        return vec;
    }

     public ArrayList getCountryIds1(String searchString,String locationId,String shipmentMode)
    {
        Connection	connection	= null;
        //Statement   stmt		= null;//Commented By RajKumari on 30-10-2008 for Connection Leakages.
         ResultSet   rs			= null;
          String		locationIds	= null;
          String		countryId	="";
          String		searchString2="";
          CallableStatement cstmt = null;
          ArrayList countryIds = new ArrayList();
            if(searchString!=null && !(searchString.equals(""))){
            countryId=searchString.replaceAll(",","','");
            //searchString = " AND CON.COUNTRYID IN ('"+countryId+"')";
          }else{
            //searchString = "";
          }
          if(!(locationId.equals("")))
          {
            
            locationIds=locationId.replaceAll(",","','");
            //searchString2=" AND  LOC.LOCATIONID IN ('"+locationIds+"')";
          }
          else
          {
          //	searchString2="";
          }
         
          
          
        
          //String sql = "SELECT DISTINCT CON.COUNTRYID,COUNTRYNAME FROM FS_COUNTRYMASTER CON,FS_FR_LOCATIONMASTER LOC "+
                //        " WHERE LOC.COUNTRYID=CON.COUNTRYID  " +searchString2+searchString+
                 //      " ORDER BY COUNTRYNAME";
        //System.out.println("locationIds     "+locationIds);
       // System.out.println("countryId   "+countryId);
        try
        {
              connection = this.getConnection();
              //stmt = connection.createStatement();
              //rs = stmt.executeQuery(sql);
              cstmt        = connection.prepareCall("{ ?= call QMS_SETUPS.GET_COUNTRY_1(?,?,?)}");
              cstmt.registerOutParameter(1,OracleTypes.CURSOR);
             
              cstmt.setString(2,locationIds);
              cstmt.setString(3,countryId);
              cstmt.setString(4,shipmentMode);
              cstmt.execute();
              rs  =  (ResultSet)cstmt.getObject(1);
            while(rs.next())
            {
               countryIds.add(rs.getString(2)+" ["+rs.getString(1)+"]");
            }
        }
        catch(SQLException sqEx)
        {
          sqEx.printStackTrace();
			    //Logger.error(FILE_NAME, "[getCountryIds(whereclause)] -> "+sqEx.toString());
          logger.error(FILE_NAME+ "[getCountryIds(whereclause)] -> "+sqEx.toString());
            throw new EJBException(sqEx.toString());
        }
        catch(Exception sqEx)
        {
          sqEx.printStackTrace();
			    //Logger.error(FILE_NAME, "[getCountryIds(whereclause)] -> "+sqEx.toString());            
          logger.error(FILE_NAME+ "[getCountryIds(whereclause)] -> "+sqEx.toString());            
            throw new EJBException(sqEx.toString());
        }
        finally
        {
          ConnectionUtil.closeConnection(connection, cstmt, rs);
        }
        return countryIds;
    }
//ListTypeIds   searchString  shipmentMode        ArrayList
/** Added by Anil for cr-1007
	*The below method is used to get the Zone Codes
    *This method takes  sMode as argument and returns a vector
	*@param String sMode
	*@return Vector records	 
	*/
   public ArrayList getZoneCodes(String locationId, String zipCode,String zoneCode,String shipmentMode,String consoleType) 
  {
    String             sqlQuery         = null;
    String             alpha            = null;
     String             countryId            = null;
    String             whereCondition   = "";
     Connection connection   = null;
    PreparedStatement  stmt = null;
    ResultSet  rs           = null;
    ArrayList  records      = new ArrayList();
    int count ;
    String             whereCondition2   = "";//Added by RajKumari on 10-11-2008 for 143511 ..
    String             countryIdQuery   = "SELECT DISTINCT COUNTRYID FROM FS_FR_LOCATIONMASTER WHERE LOCATIONID=?";
    /*   if(zipCode!=null && zipCode.indexOf("-")!=-1)
    {
        alpha   =  zipCode.substring(0,zipCode.indexOf("-"));
        zipCode =  zipCode.substring((zipCode.indexOf("-")+1),zipCode.trim().length());
    }
    
    if(alpha != null)
        whereCondition  = " AND D.ALPHANUMERIC= ?";//alpha
    
    if(zipCode!=null && zipCode.length()!=0)
    {
      sqlQuery =  " SELECT D.ZONE_CODE, DECODE(D.ALPHANUMERIC,NULL,D.FROM_ZIPCODE,D.ALPHANUMERIC||'-'||D.FROM_ZIPCODE)FROM_ZIPCODE, D.TO_ZIPCODE, D.ZONE "+ 
                  " FROM QMS_ZONE_CODE_MASTER M, QMS_ZONE_CODE_DTL D "+
                  " WHERE D.INVALIDATE='F' AND D.ZONE_CODE=M.ZONE_CODE AND M.ORIGIN_LOCATION=? AND M.SHIPMENT_MODE = ? "+
                  " AND NVL(M.CONSOLE_TYPE,'~')= ?"+
                  " AND TO_NUMBER(?) BETWEEN TO_NUMBER(D.FROM_ZIPCODE) AND TO_NUMBER(D.TO_ZIPCODE) "+whereCondition+" ORDER BY D.ZONE";
    }
    else
    {
      sqlQuery =  " SELECT D.ZONE_CODE, DECODE(D.ALPHANUMERIC,NULL,D.FROM_ZIPCODE,D.ALPHANUMERIC||'-'||D.FROM_ZIPCODE)FROM_ZIPCODE, D.TO_ZIPCODE, D.ZONE "+ 
                  " FROM QMS_ZONE_CODE_MASTER M, QMS_ZONE_CODE_DTL D "+
                  " WHERE D.INVALIDATE='F' AND D.ZONE_CODE=M.ZONE_CODE AND M.ORIGIN_LOCATION=? AND M.SHIPMENT_MODE = ? AND NVL(M.CONSOLE_TYPE,'~')= ? "+whereCondition+" ORDER BY D.ZONE";
    }*/
                 
   
    try
    {
      connection =  this.getConnection();
       stmt       =  connection.prepareStatement(countryIdQuery);
       stmt.setString(1,locationId);
      rs          =  stmt.executeQuery();
      while(rs.next())
      {
         countryId     =  rs.getString("COUNTRYID");
      }
      if(rs!=null)
          rs.close();
     if(stmt!=null)
           stmt.close();
      if(zipCode!=null )

    {
       if(zipCode.indexOf("-")!=-1)
      {
        alpha   =  zipCode.substring(0,zipCode.indexOf("-"));
        zipCode =  zipCode.substring((zipCode.indexOf("-")+1),zipCode.trim().length());
       }
      else
       {
         count = 0;
       // int i  = Integer.parseInt(shipperZipCode);
         int zipCodeLen	=	zipCode.length();
       for (int j = 0;j < zipCodeLen;j++) 
       {
	         	if (!(Character.isDigit(zipCode.charAt(j))))
            {
                  count++;
                  break;
            }
         }
         if(count>0)
         {
          alpha = zipCode;          
          zipCode = "";
         }
       }
    }    
    if(alpha != null)
        whereCondition  = " AND D.ALPHANUMERIC LIKE ?";//alpha
    
    //Added by RajKumari on 10-11-2008 for 143511 starts..
    if(zoneCode!=null && zoneCode.length()!=0)
    {
        // Added By Kishore Podili For Multi Zone Codes
    	if(zoneCode.indexOf(',')!=-1){
    		whereCondition2 = "and ( ";
    		String[] zones = zoneCode.split(",");
    		for(String x: zones)
    			whereCondition2 += "D.ZONE LIKE '"+x+"%' or " ;
    		
    		whereCondition2 = whereCondition2.substring(0, whereCondition2.length()-3);
    		whereCondition2+=")";
    	}
    	else
      whereCondition2 = "AND D.ZONE LIKE '"+zoneCode+"%'" ;
    }
    //Added by RajKumari on 10-11-2008 for 143511 ends..
    if(zipCode!=null && zipCode.length()!=0)
    {
      if(countryId!=null&&"CA".equalsIgnoreCase(countryId))
      {
       	 
         sqlQuery =  " SELECT D.ZONE_CODE,D.FROM_ZIPCODE, D.TO_ZIPCODE, D.ZONE "+ 
                  " FROM QMS_ZONE_CODE_MASTER_CA M, QMS_ZONE_CODE_DTL_CA D "+
                  " WHERE D.INVALIDATE='F' AND D.ZONE_CODE=M.ZONE_CODE AND M.LOCATION_ID=? AND M.SHIPMENT_MODE = ? "+
                  " AND NVL(M.CONSOLE_TYPE,'~')= ?"+
                  " AND  ? BETWEEN D.FROM_ZIPCODE AND  D.TO_ZIPCODE "+whereCondition2+" ORDER BY D.ZONE";
      }
      else
      {
         sqlQuery =  " SELECT D.ZONE_CODE, DECODE(D.ALPHANUMERIC,NULL,D.FROM_ZIPCODE,D.ALPHANUMERIC||'-'||D.FROM_ZIPCODE)FROM_ZIPCODE, D.TO_ZIPCODE, D.ZONE "+ 
              " FROM QMS_ZONE_CODE_MASTER M, QMS_ZONE_CODE_DTL D "+
              " WHERE D.INVALIDATE='F' AND D.ZONE_CODE=M.ZONE_CODE AND M.ORIGIN_LOCATION=? AND M.SHIPMENT_MODE = ? "+
              " AND NVL(M.CONSOLE_TYPE,'~')= ?"+
              " AND TO_NUMBER(?) BETWEEN TO_NUMBER(D.FROM_ZIPCODE) AND TO_NUMBER(D.TO_ZIPCODE) "+whereCondition+ whereCondition2+"  ORDER BY D.ZONE";
 
      }
    }
    else
    {
        if(countryId!=null&&"CA".equalsIgnoreCase(countryId))
      {
        	//@@Added by subrahmanyam for the pbn id: 195650 on 29/Jan/10
        	String whereCondition3 ="";
      	   if(alpha!=null && alpha.length()!=0)
   	    {
      		   if(alpha.length()>3)
      			   whereCondition3 = " AND D.FROM_ZIPCODE LIKE  '"+alpha.substring(0,3)+"%'" ;
      		   else
      			 whereCondition3 = " AND D.FROM_ZIPCODE LIKE  '"+alpha.substring(0,alpha.length())+"%'" ;
   	    }
      	//@@ended by subrahmanyam for the pbn id: 195650 on 29/Jan/10
         sqlQuery =  " SELECT D.ZONE_CODE,D.FROM_ZIPCODE, D.TO_ZIPCODE, D.ZONE "+ 
                  " FROM QMS_ZONE_CODE_MASTER_CA M, QMS_ZONE_CODE_DTL_CA D "+
                  " WHERE D.INVALIDATE='F' AND D.ZONE_CODE=M.ZONE_CODE AND M.LOCATION_ID=? AND M.SHIPMENT_MODE = ? AND NVL(M.CONSOLE_TYPE,'~')= ?  "+whereCondition3+" ORDER BY D.ZONE";
     
      }
      else
      {
        sqlQuery =  " SELECT D.ZONE_CODE, DECODE(D.ALPHANUMERIC,NULL,D.FROM_ZIPCODE,D.ALPHANUMERIC||'-'||D.FROM_ZIPCODE)FROM_ZIPCODE, D.TO_ZIPCODE, D.ZONE "+ 
                  " FROM QMS_ZONE_CODE_MASTER M, QMS_ZONE_CODE_DTL D "+
                 " WHERE D.INVALIDATE='F' AND D.ZONE_CODE=M.ZONE_CODE AND M.ORIGIN_LOCATION=? AND M.SHIPMENT_MODE = ? AND NVL(M.CONSOLE_TYPE,'~')= ? "+whereCondition+whereCondition2+" ORDER BY D.ZONE";
  }
    }
         
                stmt       =  connection.prepareStatement(sqlQuery);
      stmt.setString(1,locationId);
      stmt.setString(2,shipmentMode);
      if("1".equalsIgnoreCase(shipmentMode))
          stmt.setString(3,"~");
      else
          stmt.setString(3,consoleType);
          
        if(zipCode!=null && zipCode.length()!=0)
        {
                if(countryId!=null&&"CA".equalsIgnoreCase(countryId))
             {
               stmt.setString(4,zipCode);
                   }
            else
           {
              stmt.setString(4,zipCode);
              if(countryId==null||!("CA".equalsIgnoreCase(countryId)))
              {
              if(alpha != null)
                stmt.setString(5,alpha+"%");
              }
           }
      }
      else
      {
       if(countryId==null||!("CA".equalsIgnoreCase(countryId)))
       {
        if(alpha != null)
          stmt.setString(4,alpha+"%");
      }
      }
       
      rs         =  stmt.executeQuery();
  
      while(rs.next())
      {
        records.add(rs.getString("FROM_ZIPCODE")+"-"+rs.getString("TO_ZIPCODE")+"["+rs.getString("ZONE")+"]["+"]");
      }
    }
    catch(SQLException sqEx)
    {
        //Logger.error(FILE_NAME, "[getServiceLevelDetails(String sMode) ] -> "+sqEx.toString());
        logger.error(FILE_NAME+ "[getServiceLevelDetails(String sMode) ] -> "+sqEx.toString());
        throw new EJBException(sqEx.toString());
    }
    finally
    {
        ConnectionUtil.closeConnection(connection, stmt, rs);
    }
    return records;
  }

  /** Added by Yuvraj for cr-1005
	*The below method is used to get the ZoneCodes
	*@param String locationId
	*@return ArrayList records	 
	*/
  public ArrayList getZoneCodes(String locationId,String shipmentMode,String consoleType) 
  {
    
    String sqlQuery =  " SELECT DISTINCT D.ZONE FROM QMS_ZONE_CODE_MASTER M, QMS_ZONE_CODE_DTL D "+
                       " WHERE D.ZONE_CODE=M.ZONE_CODE AND M.ORIGIN_LOCATION=? AND M.SHIPMENT_MODE = ? "+
                       " AND NVL(M.CONSOLE_TYPE,'~') = ? AND D.INVALIDATE='F' AND ZONE IS NOT NULL "+
                       " ORDER BY D.ZONE ";
     //@@Added by Kameswari for the WPBN issue-86294
     String  query  =   " SELECT DISTINCT C.ZONE FROM QMS_ZONE_CODE_MASTER_CA M,QMS_ZONE_CODE_DTL_CA C "+
                       " WHERE C.ZONE_CODE=M.ZONE_CODE AND M.LOCATION_ID=? AND M.SHIPMENT_MODE = ? "+
                       " AND NVL(M.CONSOLE_TYPE,'~') = ? AND C.INVALIDATE='F' AND ZONE IS NOT NULL "+
                       " ORDER BY C.ZONE ";    
     //@@Added by subrahmanyam for the pbn id: 202447 on 09-Apr-010
     String             countryIdQuery   = "SELECT DISTINCT COUNTRYID FROM FS_FR_LOCATIONMASTER WHERE LOCATIONID=?";
     String				countryId		 =	"";
     PreparedStatement  pstmt1  = null;
     ResultSet  rs1             = null;    
     //@@Ended by subrahmanyam for the pbn id: 202447 on 09-Apr-010  
   //@@WPBN issue-86294
    Connection connection     = null;
    PreparedStatement  pstmt  = null;
    ResultSet  rs             = null;
    ArrayList  records        = new ArrayList();
    int count =0;
    try
    {
      connection =  this.getConnection();
      //@@Added by subrahmanyam for the pbn id: 202447 on 09-Apr-010      
      try
      {
    	  pstmt1       =  connection.prepareStatement(countryIdQuery);
    	  pstmt1.setString(1,locationId);
    	  rs1         =  pstmt1.executeQuery();
    	  if(rs1.next())
    		  countryId = rs1.getString("COUNTRYID");
      }catch(SQLException e)
      {
          logger.error(FILE_NAME+ " Getting the problem while getting the Country id: "+e.toString());
          throw new EJBException(e.toString());

      }
      if(countryId !=null && !"CA".equalsIgnoreCase(countryId))
      {     //@@ended by subrahmanyam for the pbn id: 202447 on 09-Apr-010
      pstmt       =  connection.prepareStatement(sqlQuery);
      pstmt.setString(1,locationId);
      pstmt.setString(2,shipmentMode);
      
      if("1".equalsIgnoreCase(shipmentMode))
          pstmt.setString(3,"~");
      else
          pstmt.setString(3,consoleType);
          
      rs         =  pstmt.executeQuery();
  
      while(rs.next())
      {
        records.add(rs.getString("ZONE"));
      }
       //@@Added by Kameswari for the WPBN issue-86294
      if(pstmt!=null)
         pstmt.close();
      if(rs!=null)
         rs.close();
      }
      else
      {
      pstmt       =  connection.prepareStatement(query);
      pstmt.setString(1,locationId);
      pstmt.setString(2,shipmentMode);
      
      if("1".equalsIgnoreCase(shipmentMode))
          pstmt.setString(3,"~");
      else
          pstmt.setString(3,consoleType);
          
      rs         =  pstmt.executeQuery();
  
      while(rs.next())
      {
    	     //@@Commented by subrahmanyam for the pbn id: 202447 on 09-Apr-010
        /*for(int i=0;i<records.size();i++)
        {
          if(rs.getString("ZONE").equalsIgnoreCase((String)records.get(i)))
          {
             count++;
          }
        }
          if(count==0)*/
            records.add(rs.getString("ZONE"));
        
      }
       //@@WPBN issue-86294
    }     //@@Ended by subrahmanyam for the pbn id: 202447 on 09-Apr-010
    }
    catch(SQLException sqEx)
    {
        //Logger.error(FILE_NAME, "[getServiceLevelDetails(String sMode) ] -> "+sqEx.toString());
        logger.error(FILE_NAME+ "[getServiceLevelDetails(String sMode) ] -> "+sqEx.toString());
        throw new EJBException(sqEx.toString());
    }
    finally
    {
        ConnectionUtil.closeConnection(connection, pstmt, rs);
        ConnectionUtil.closePreparedStatement(pstmt1, rs1);
    }
    return records;
  }
  
  
   public HashMap addContentDiscriptionDtls(ArrayList contentDataList)
  {
     QMSContentDOB  contentDOB  =  null;
    Connection        connection =  null;
    PreparedStatement pStmt      =  null;
    PreparedStatement pStmtContent =  null;
    //ResultSet         rs          =  null;//Commented By RajKumari on 30-10-2008 for Connection Leakages.
    ResultSet         rsContent   =  null;
    StringBuffer      sqlContent = new StringBuffer("SELECT CONTENTID    FROM QMS_CONTENTDTLS WHERE CONTENTID   =? AND ACTIVEINACTIVE ='N' AND TEMINALID  IN (");
                      sqlContent.append("SELECT TERMINALID FROM FS_FR_TERMINALMASTER WHERE TERMINALID = ?");
                      sqlContent.append(" UNION");
                      sqlContent.append(" SELECT PARENT_TERMINAL_ID TERMINALID from fs_fr_terminal_regn ");
                      sqlContent.append(" connect by prior PARENT_TERMINAL_ID=CHILD_TERMINAL_ID start with CHILD_TERMINAL_ID= ?");
                      sqlContent.append(" UNION");
                      sqlContent.append(" SELECT TERMINALID FROM FS_FR_TERMINALMASTER WHERE OPER_ADMIN_FLAG='H')");
                      
    StringBuffer  updateChildData = new StringBuffer(" UPDATE QMS_CONTENTDTLS SET INACTIVATE ='Y' WHERE CHARGEDESCID =? AND TERMINALID IN (");
                  updateChildData.append(" select CHILD_TERMINAL_ID TERMINALID from fs_fr_terminal_regn");
                  updateChildData.append(" connect by prior CHILD_TERMINAL_ID=PARENT_TERMINAL_ID start with PARENT_TERMINAL_ID=? )");
      
    String            sql       = "INSERT INTO QMS_CONTENTDTLS (SHIPMENTMODE ,HEADERFOOTER ,CONTENTID ,DESCRIPTION ,FLAG,TEMINALID ,ACCESSLEVEL,INVALIDATE,ACTIVEINACTIVE   )VALUES(?,?,?,?,?,?,?,'F','N')";
        ArrayList         existingList   = new ArrayList();
    ArrayList         nonExistingList   = new ArrayList();
    HashMap           map               = new HashMap();
    String            accessLevel       ="";
    try
    {
      connection     =   this.getConnection();
      pStmt          =   connection.prepareStatement(sql);
      pStmtContent    =   connection.prepareStatement(sqlContent.toString());
      int contentDataListSize	=	contentDataList.size();
      for(int i=0;i<contentDataListSize;i++)
      {
        contentDOB  =  (QMSContentDOB)contentDataList.get(i);
        pStmtContent.clearParameters();
        pStmtContent.setString(1,contentDOB.getContentId());  
        pStmtContent.setString(2,contentDOB.getTerminalId());  
        pStmtContent.setString(3,contentDOB.getTerminalId()); 
        rsContent = pStmtContent.executeQuery();
        if(rsContent.next() ){
           existingList.add(contentDOB);           
        }
        else {
        pStmt.clearParameters();
        //Logger.info(FILE_NAME,"contentDOB.getShipmentMode()  "+contentDOB.getShipmentMode());
        if("OPER_TERMINAL".equalsIgnoreCase(contentDOB.getAccessLevel()))
        {
          accessLevel="O";
        }else if("HO_TERMINAL".equalsIgnoreCase(contentDOB.getAccessLevel()))
        {
            accessLevel ="H";
        }else if("ADMN_TERMINAL".equalsIgnoreCase(contentDOB.getAccessLevel()))
        {
            accessLevel ="A";
        }
        pStmt.setInt(1,contentDOB.getShipmentMode());
        pStmt.setString(2,contentDOB.getHeaderFooter());
        pStmt.setString(3,contentDOB.getContentId());
        pStmt.setString(4,contentDOB.getContentDescription());
        pStmt.setString(5,contentDOB.getDefaultFlag());        
        pStmt.setString(6,contentDOB.getTerminalId());  
        pStmt.setString(7,accessLevel);  
        pStmt.executeUpdate();
        nonExistingList.add(contentDOB);
        }
       
       // if(rs!=null)rs.close();//Commented By RajKumari on 30-10-2008 for Connection Leakages.
        if(rsContent!=null)rsContent.close();
      }
      map.put("EXISTS",existingList);
      map.put("NONEXISTS",nonExistingList);
    }
    catch(Exception e)
    {
      e.printStackTrace();
      //Logger.info(FILE_NAME,"Error while adding"+e.toString());
      logger.info(FILE_NAME+"Error while adding"+e.toString());
      return map;
    }
    finally
    {
      ConnectionUtil.closeConnection(connection,pStmt,null);//Modified By RajKumari on 30-10-2008 for Connection Leakages.
      ConnectionUtil.closeConnection(connection,pStmtContent,rsContent);
    }
    return map;
  }   
  
  public boolean modifyContentDescription(QMSContentDOB contentDOB)
	{
	   Connection        connection =  null;
     PreparedStatement pStmt      =  null;
	   String            sql       ="UPDATE QMS_CONTENTDTLS SET SHIPMENTMODE=?,HEADERFOOTER=?,DESCRIPTION=?,FLAG=? WHERE CONTENTID=?";
	try
		{
		  connection     =   this.getConnection();
      pStmt        =   connection.prepareStatement(sql);
		pStmt.setInt(1,contentDOB.getShipmentMode());
		pStmt.setString(2,contentDOB.getHeaderFooter());
		pStmt.setString(3,contentDOB.getContentDescription());
		pStmt.setString(4,contentDOB.getDefaultFlag());
		pStmt.setString(5,contentDOB.getContentId());
		pStmt.executeUpdate();

		}
		catch(Exception e)
    {
      e.printStackTrace();
	  //Logger.info(FILE_NAME,"Error while adding"+e.toString());
    logger.info(FILE_NAME+"Error while adding"+e.toString());
      return false;
    }
    finally
    {
      ConnectionUtil.closeConnection(connection,pStmt);
    }
		return true;
	}
  public boolean deleteContentDetails(String contentId)
  {
    Connection        connection =  null;
    PreparedStatement pStmt      =  null;
	  String            sql       ="DELETE QMS_CONTENTDTLS  WHERE CONTENTID=?";
	try
		{
		  connection     =   this.getConnection();
      pStmt        =   connection.prepareStatement(sql);
	    pStmt.setString(1,contentId);
      
		  pStmt.executeUpdate();
    }catch(Exception e)
    {
      e.printStackTrace();
      //Logger.info(FILE_NAME,"Error while modifying"+e.toString());
      logger.info(FILE_NAME+"Error while modifying"+e.toString());
      return false;
    }
    finally
    {
      ConnectionUtil.closeConnection(connection,pStmt);
    }
		return true;
    
  }
  public QMSContentDOB getContentDetails(String contentId,String terminalId)
	{
	  Connection        connection =  null;
    PreparedStatement pStmt      =  null;
	  ResultSet         rs          =  null;
    QMSContentDOB  contentDOB=null;
	  String            sql       ="SELECT SHIPMENTMODE ,HEADERFOOTER ,CONTENTID ,DESCRIPTION ,FLAG FROM QMS_CONTENTDTLS WHERE CONTENTID=? AND INVALIDATE='F'";
	try
		{
		connection     =   this.getConnection();
		pStmt          =   connection.prepareStatement(sql);
		pStmt.setString(1,contentId);
    //pStmt.setString(2,terminalId);
		rs=pStmt.executeQuery();
		while(rs.next())
		{
			contentDOB=new QMSContentDOB();
			contentDOB.setShipmentMode(rs.getInt("SHIPMENTMODE"));
			contentDOB.setHeaderFooter(rs.getString("HEADERFOOTER"));
			contentDOB.setContentId(contentId);
			contentDOB.setContentDescription(rs.getString("DESCRIPTION"));
			contentDOB.setDefaultFlag(rs.getString("FLAG"));
		}
		}catch(Exception e)
		{
		e.printStackTrace();
      //Logger.info(FILE_NAME,"Error while retrieving"+e.toString());
      logger.info(FILE_NAME+"Error while retrieving"+e.toString());
		return contentDOB;

		}
		 finally
    {
      ConnectionUtil.closeConnection(connection,pStmt,rs);
    }
		return contentDOB;
	}
	public ArrayList getContentIds(String shipmentMode,String searchString,String operation,String terminalId)
	{
			Connection        connection  =  null;
     // PreparedStatement pStmt       =  null;//Commented By RajKumari on 30-10-2008 for Connection Leakages.
      ResultSet         rs          =  null;
      ArrayList         contentIds  =  new ArrayList();
      CallableStatement cstmt       =  null;
    
      try
      {
        //Logger.info(FILE_NAME,"inside getContent Ids "+operation);
        //Logger.info(FILE_NAME,"inside getContent Ids "+shipmentMode);
        connection     =   this.getConnection();
        cstmt        = connection.prepareCall("{ ?= call QMS_SETUPS.GET_CONTENTDESCRIPTION(?,?,?,?)}");
        cstmt.registerOutParameter(1,OracleTypes.CURSOR);
        cstmt.setString(2,terminalId);
        cstmt.setString(3,(searchString+"%"));
        cstmt.setString(4,operation);
        cstmt.setString(5,shipmentMode);
        cstmt.execute();
        rs  =  (ResultSet)cstmt.getObject(1);
        while(rs.next())
        {
          contentIds.add(rs.getString("CONTENTID"));
        }
      }
      catch(Exception e)
      {
        e.printStackTrace();		
        //Logger.info(FILE_NAME,"Error while retrieving contentIds"+e.toString());
        logger.info(FILE_NAME+"Error while retrieving contentIds"+e.toString());
        contentIds  =  null;
      }
       finally
      {
        ConnectionUtil.closeConnection(connection,cstmt,rs);//Modified By RajKumari on 30-10-2008 for Connection Leakages.
      }
      return contentIds;
	}
   public ArrayList getChargeMasterDetails()
    {                 
         ArrayList                    chargeList         =                  null;
         Connection                   connection      =                  null;
         PreparedStatement            pStmt           =                  null;
         ResultSet                    resultSet       =                  null;
         String                       selectQuery     =                  null; 
         ChargesMasterDOB             chargeDOB       =                  null;
         
      try
      {              
         chargeList         =    new ArrayList(5);
         operationsImpl  =    new OperationsImpl();
         connection      =    operationsImpl.getConnection();
         selectQuery     =    "SELECT CHARGE_ID,CHARGE_DESCRIPTION,SHIPMENT_MODE,COST_INCURREDAT,INVALIDATE FROM QMS_CHARGESMASTER ";         
         pStmt           =    connection.prepareStatement(selectQuery);
         resultSet       =    pStmt.executeQuery(); 
         
        
          while(resultSet.next())
          { 
           chargeDOB           =      new ChargesMasterDOB();
           chargeDOB.setChargeId(resultSet.getString(1)!=null?resultSet.getString(1):"");
           chargeDOB.setChargeDesc(resultSet.getString(2)!=null?resultSet.getString(2):"");
           chargeDOB.setShipmentMode(resultSet.getInt(3));
           chargeDOB.setCostIncurr(resultSet.getString(4)!=null?resultSet.getString(4):"");
           chargeDOB.setInvalidate(resultSet.getString(5)!=null?resultSet.getString(5):"");
           chargeList.add(chargeDOB);
         }
      }
      catch(Exception e)
      {
         e.printStackTrace();
		
         //Logger.error(FILE_NAME,"Error in getDensityGroupCodeDetails module",e.toString()); 
         logger.error(FILE_NAME+"Error in getDensityGroupCodeDetails module"+e.toString()); 
		  throw new EJBException(e.toString());
      }    
      finally
      {
        chargeDOB    =    null;
        ConnectionUtil.closeConnection(connection,pStmt,resultSet);
      }
      return chargeList;
    }
    
    public ArrayList getChargeBasisDetails()
    {                 
         ArrayList                    chargeList         =                  null;
         Connection                   connection      =                  null;
         PreparedStatement            pStmt           =                  null;
         ResultSet                    resultSet       =                  null;
         String                       selectQuery     =                  null; 
         ChargeBasisMasterDOB            chargeDOB        =                  null;
         
      try
      {              
         chargeList         =    new ArrayList(5);
         operationsImpl  =    new OperationsImpl();
         connection      =    operationsImpl.getConnection();
         selectQuery     =    "SELECT CHARGEBASIS,BASIS_DESCRIPTION,BLOCK,PRIMARY_BASIS,SECONDARY_BASIS,TERTIARY_BASIS,INVALIDATE FROM QMS_CHARGE_BASISMASTER";         
         pStmt           =    connection.prepareStatement(selectQuery);
         resultSet       =    pStmt.executeQuery(); 
         
        
          while(resultSet.next())
          { 
           chargeDOB           =      new ChargeBasisMasterDOB();
           chargeDOB.setChargeBasis(resultSet.getString(1)!=null?resultSet.getString(1):"");
           chargeDOB.setChargeDesc(resultSet.getString(2)!=null?resultSet.getString(2):"");
           chargeDOB.setBlock(resultSet.getString(3)!=null?resultSet.getString(3):"");
           chargeDOB.setPrimaryBasis(resultSet.getString(4)!=null?resultSet.getString(4):"");
           chargeDOB.setSecondaryBasis(resultSet.getString(5)!=null?resultSet.getString(5):"");
           chargeDOB.setTertiaryBasis(resultSet.getString(6)!=null?resultSet.getString(6):"");
           chargeDOB.setCalculation("");  //removed calculation for issue 12182 rk
           chargeDOB.setInvalidate(resultSet.getString(7)!=null?resultSet.getString(7):"");  
           chargeList.add(chargeDOB);
         }
      }
      catch(Exception e)
      {
         e.printStackTrace();
		 //Logger.error(FILE_NAME,"Error in getDensityGroupCodeDetails module",e.toString());  
     logger.error(FILE_NAME+"Error in getDensityGroupCodeDetails module"+e.toString());  
		 throw new EJBException(e.toString());
      }    
      finally
      {
        chargeDOB    =    null;
        ConnectionUtil.closeConnection(connection,pStmt,resultSet);
      }
      return chargeList;
    }
    
   
    
    public void invalidateChargeMasterId(ArrayList chargeList)//throws SQLException,EJBException
    {
      Connection connection  = null;
      OperationsImpl operationsImpl = new OperationsImpl();
      ChargesMasterDOB  chargeDOB  = null;
      PreparedStatement pstmt = null;
      String updateQry  = "UPDATE QMS_CHARGESMASTER   SET INVALIDATE =? WHERE CHARGE_ID =?";
      try
      {
        
         connection      =    operationsImpl.getConnection();
        pstmt = connection.prepareStatement(updateQry);
        if(chargeList!=null && chargeList.size()>0)
        {
        	int chargeListSize	=	chargeList.size();
          for(int i=0;i<chargeListSize;i++)
          {
            chargeDOB  = (ChargesMasterDOB)chargeList.get(i);
            pstmt.setString(1,chargeDOB.getInvalidate());
            pstmt.setString(2,chargeDOB.getChargeId());
            pstmt.executeUpdate();
          }
         }
          
       }catch(SQLException e)
       {
         e.printStackTrace();
		 
         //Logger.error(FILE_NAME,"SQLException in invalidateIndustryId(ArrayList param0) method"+e.toString());
         logger.error(FILE_NAME+"SQLException in invalidateIndustryId(ArrayList param0) method"+e.toString());
		 throw new EJBException(e.toString());
        //throw new SQLException();                  
       }catch(Exception e)
       {
       e.printStackTrace();

        //Logger.error(FILE_NAME,"EJBException in invalidateIndustryId(ArrayList param0) method"+e.toString());
        logger.error(FILE_NAME+"EJBException in invalidateIndustryId(ArrayList param0) method"+e.toString());
       // throw new EJBException();            
	   	   throw new EJBException(e.toString());
       }finally
       {
        try{
           if(pstmt!=null)
            { pstmt.close();}
           if(connection!=null)
            { connection.close();}
        }catch(SQLException e)
        {
         //Logger.error(FILE_NAME,"SQLException in invalidateIndustryId(ArrayList param0) method"+e.toString());
         logger.error(FILE_NAME+"SQLException in invalidateIndustryId(ArrayList param0) method"+e.toString());
         //throw new SQLException();            
        }
       }
    }
    
    
    public void invalidateChargeBasisId(ArrayList chargeList)//throws SQLException,EJBException
    {
      Connection connection  = null;
      OperationsImpl operationsImpl = new OperationsImpl();
      ChargeBasisMasterDOB  chargeDOB  = null;
      PreparedStatement pstmt = null;
      String updateQry  = "UPDATE QMS_CHARGE_BASISMASTER    SET INVALIDATE =? WHERE CHARGEBASIS  =?";
      try
      {
        
         connection      =    operationsImpl.getConnection();
        pstmt = connection.prepareStatement(updateQry);
        if(chargeList!=null && chargeList.size()>0)
        {
        	int chargeListSize	=	chargeList.size();
          for(int i=0;i<chargeListSize;i++)
          {
            chargeDOB  = (ChargeBasisMasterDOB)chargeList.get(i);
            pstmt.setString(1,chargeDOB.getInvalidate());
            pstmt.setString(2,chargeDOB.getChargeBasis());
            pstmt.executeUpdate();
          }
         }
          
       }catch(SQLException e)
       {
        //Logger.error(FILE_NAME,"SQLException in invalidateIndustryId(ArrayList param0) method"+e.toString());
        logger.error(FILE_NAME+"SQLException in invalidateIndustryId(ArrayList param0) method"+e.toString());
        throw new EJBException(e.toString());
       }catch(Exception e)
       {
        //Logger.error(FILE_NAME,"EJBException in invalidateIndustryId(ArrayList param0) method"+e.toString());
        logger.error(FILE_NAME+"EJBException in invalidateIndustryId(ArrayList param0) method"+e.toString());
        throw new EJBException();            
       }finally
       {
        try{
           if(pstmt!=null)
            { pstmt.close();}
           if(connection!=null)
            { connection.close();}
        }catch(SQLException e)
        {
         //Logger.error(FILE_NAME,"SQLException in invalidateIndustryId(ArrayList param0) method"+e.toString());
         logger.error(FILE_NAME+"SQLException in invalidateIndustryId(ArrayList param0) method"+e.toString());
        // throw new SQLException();            
        }
       }
    }
    
    
     public ArrayList getChargeGroupMasterDetails(String operation,String terminalId,String accessLevel)
    {                 
         ArrayList                    chargeList      =                  null;
         Connection                   connection      =                  null;
         PreparedStatement            pStmt           =                  null;
         PreparedStatement            pStmtsub        =                  null;
         ResultSet                    resultSet       =                  null;
         ResultSet                    subResultSet    =                  null;
         String                       selectQuery     =                  null; 
         ChargeGroupingDOB             chargeDOB      =                  null;
         String                        subQuery       =                 null;
         ArrayList                    chargeIdList    =                 null;
         ArrayList                    descriptionIdList =               null;
         StringBuffer                 terminalList      =               new StringBuffer("");
      try
      {              
           
        if("HO_TERMINAL".equals(accessLevel))
        {
            terminalList.append(" SELECT DISTINCT terminalid FROM FS_FR_TERMINALMASTER WHERE actv_flag = 'A' ");
        
        }else
        {
            if("ViewAll".equals(operation))
             {  
                terminalList.append(" SELECT CHILD_TERMINAL_ID TERMINALID FROM FS_FR_TERMINAL_REGN");
                terminalList.append(" CONNECT BY PRIOR CHILD_TERMINAL_ID=PARENT_TERMINAL_ID START WITH PARENT_TERMINAL_ID='"+terminalId+"' ");
                terminalList.append(" UNION ");
                terminalList.append(" SELECT TERMINALID FROM FS_FR_TERMINALMASTER WHERE TERMINALID = '"+terminalId+"'  ");
                terminalList.append(" UNION");
                terminalList.append(" SELECT PARENT_TERMINAL_ID TERMINALID FROM FS_FR_TERMINAL_REGN ");
                terminalList.append(" CONNECT BY PRIOR PARENT_TERMINAL_ID=CHILD_TERMINAL_ID START WITH CHILD_TERMINAL_ID= '"+terminalId+"'");
                terminalList.append(" UNION");
                terminalList.append(" SELECT TERMINALID FROM FS_FR_TERMINALMASTER WHERE OPER_ADMIN_FLAG='H'");
             }else
             {
                terminalList.append(" SELECT TERMINALID FROM FS_FR_TERMINALMASTER WHERE TERMINALID = '"+terminalId+"'  ");
                terminalList.append(" UNION ");
                terminalList.append(" SELECT CHILD_TERMINAL_ID TERMINALID FROM FS_FR_TERMINAL_REGN");
                terminalList.append(" CONNECT BY PRIOR CHILD_TERMINAL_ID=PARENT_TERMINAL_ID START WITH PARENT_TERMINAL_ID='"+terminalId+"' ");
             }      
        }
         chargeList      =    new ArrayList(5);
         operationsImpl  =    new OperationsImpl();
         connection      =    operationsImpl.getConnection();
         if("ViewAll".equalsIgnoreCase(operation))
            selectQuery     =    "SELECT DISTINCT (CHARGEGROUP_ID),SHIPMENT_MODE,INVALIDATE,TERMINALID FROM QMS_CHARGE_GROUPSMASTER WHERE TERMINALID IN ("+terminalList.toString()+") AND INACTIVATE='N' AND ( INVALIDATE='F' OR INVALIDTE IS NULL) ORDER BY CHARGEGROUP_ID ";         
          else
           selectQuery     =    "SELECT DISTINCT (CHARGEGROUP_ID),SHIPMENT_MODE,INVALIDATE,TERMINALID FROM QMS_CHARGE_GROUPSMASTER  WHERE TERMINALID IN ("+terminalList.toString()+") AND INACTIVATE='N'  ORDER BY CHARGEGROUP_ID";         
           
         subQuery        =    "SELECT CHARGE_ID,CHARGEDESCID FROM QMS_CHARGE_GROUPSMASTER WHERE CHARGEGROUP_ID=? AND SHIPMENT_MODE=?  AND INACTIVATE='N'";         
         pStmt           =    connection.prepareStatement(selectQuery);
         pStmtsub        =    connection.prepareStatement(subQuery);
         resultSet       =    pStmt.executeQuery(); 
         
        
          while(resultSet.next())
          { 
           chargeDOB           =      new ChargeGroupingDOB();
           chargeDOB.setChargeGroup(resultSet.getString(1)!=null?resultSet.getString(1):"");           
           chargeDOB.setShipmentMode(resultSet.getInt(2));
           chargeDOB.setInvalidate(resultSet.getString(3));
           chargeDOB.setTerminalId(resultSet.getString(4));
           chargeIdList        =    new ArrayList();
           descriptionIdList   =    new ArrayList();
           pStmtsub.clearParameters();
           
           pStmtsub.setString(1,resultSet.getString("CHARGEGROUP_ID"));
           pStmtsub.setInt(2,resultSet.getInt("SHIPMENT_MODE"));
           subResultSet = pStmtsub.executeQuery();
           while(subResultSet.next())
           {
             chargeIdList.add(subResultSet.getString(1));
             descriptionIdList.add(subResultSet.getString(2));
             //Logger.info(FILE_NAME,"subResultSet.getString(2).size()   "+subResultSet.getString(2));
           }
           chargeDOB.setChargeIdList(chargeIdList);
           //Logger.info(FILE_NAME,"chargeIdList.size()   "+chargeIdList.size());
           //Logger.info(FILE_NAME,"descriptionIdList.size()   "+descriptionIdList.size());
           chargeDOB.setDescriptionIdList(descriptionIdList);
           if(subResultSet!=null)subResultSet.close();
           chargeList.add(chargeDOB);
         }
      }
      catch(Exception e)
      {
         e.printStackTrace();
         //Logger.error(FILE_NAME,"Error in getDensityGroupCodeDetails module",e.toString());       
         logger.error(FILE_NAME+"Error in getDensityGroupCodeDetails module"+e.toString());       
         return null;
      }    
      finally
      {
        chargeDOB    =    null;
        ConnectionUtil.closeConnection(connection,pStmt,resultSet);
        ConnectionUtil.closeConnection(null,pStmtsub,null);
        
      }
      return chargeList;
    }
    
    public void invalidateChargeGroupId(ArrayList contentList)//throws SQLException,EJBException
    {
      Connection connection  = null;
      OperationsImpl operationsImpl = new OperationsImpl();
      ChargeGroupingDOB  chargeDOB  = null;
      PreparedStatement pstmt = null;
      String updateQry  = "UPDATE QMS_CHARGE_GROUPSMASTER    SET INVALIDATE =? WHERE CHARGEGROUP_ID  =? AND TERMINALID=? AND INACTIVATE='N' ";
      try
      {
        
         connection      =    operationsImpl.getConnection();
        pstmt = connection.prepareStatement(updateQry);
        if(contentList!=null && contentList.size()>0)
        {
        	int contentListSize	=	contentList.size();
          for(int i=0;i<contentListSize;i++)
          {
            chargeDOB  = (ChargeGroupingDOB)contentList.get(i);
            pstmt.setString(1,chargeDOB.getInvalidate());
            pstmt.setString(2,chargeDOB.getChargeGroup());
            pstmt.setString(3,chargeDOB.getTerminalId());
            pstmt.executeUpdate();
          }
         }
          
       }catch(SQLException e)
       {e.printStackTrace();
        //Logger.error(FILE_NAME,"SQLException in invalidateIndustryId(ArrayList param0) method"+e.toString());
        logger.error(FILE_NAME+"SQLException in invalidateIndustryId(ArrayList param0) method"+e.toString());
       // throw new SQLException();                  
       }catch(Exception e)
       {e.printStackTrace();
        //Logger.error(FILE_NAME,"EJBException in invalidateIndustryId(ArrayList param0) method"+e.toString());
        logger.error(FILE_NAME+"EJBException in invalidateIndustryId(ArrayList param0) method"+e.toString());
        throw new EJBException();            
       }finally
       {
        try{
           if(pstmt!=null)
            { pstmt.close();}
           if(connection!=null)
            { connection.close();}
        }catch(SQLException e)
        {
         //Logger.error(FILE_NAME,"SQLException in invalidateIndustryId(ArrayList param0) method"+e.toString());
         logger.error(FILE_NAME+"SQLException in invalidateIndustryId(ArrayList param0) method"+e.toString());
        // throw new SQLException();            
        }
       }
    }
    //@@ Commented & Added by subrahmanyam for the pbn id: 203873 on 26-APR-10     
     //public ArrayList getAllContentDetails(String operation,String terminalId)
    public ArrayList getAllContentDetails(String operation,String terminalId,String loginAccesType)    
    {                 
         ArrayList                    contentList      =                  null;
         Connection                   connection      =                  null;
         PreparedStatement            pStmt           =                  null;
        
         ResultSet                    resultSet       =                  null;
         String                       selectQuery     =                  null; 
         QMSContentDOB             contentDOB      =                  null;
         String                        subQuery       =                 null;
         ArrayList                    chargeIdList    =                 null;
         ArrayList                    descriptionIdList =               null;
         StringBuffer                 terminalList      =               new StringBuffer("");
      try
      {              
      
            if("ViewAll".equals(operation))
             {  
                terminalList.append(" SELECT CHILD_TERMINAL_ID TERMINALID FROM FS_FR_TERMINAL_REGN");
                terminalList.append(" CONNECT BY PRIOR CHILD_TERMINAL_ID=PARENT_TERMINAL_ID START WITH PARENT_TERMINAL_ID='"+terminalId+"' ");
                terminalList.append(" UNION ");
                terminalList.append(" SELECT TERMINALID FROM FS_FR_TERMINALMASTER WHERE TERMINALID = '"+terminalId+"'  ");
                terminalList.append(" UNION");
                terminalList.append(" SELECT PARENT_TERMINAL_ID TERMINALID FROM FS_FR_TERMINAL_REGN ");
                terminalList.append(" CONNECT BY PRIOR PARENT_TERMINAL_ID=CHILD_TERMINAL_ID START WITH CHILD_TERMINAL_ID= '"+terminalId+"'");
                terminalList.append(" UNION");
                terminalList.append(" SELECT TERMINALID FROM FS_FR_TERMINALMASTER WHERE OPER_ADMIN_FLAG='H'");
             }else
             {
                terminalList.append(" SELECT TERMINALID FROM FS_FR_TERMINALMASTER WHERE TERMINALID = '"+terminalId+"'  ");
                terminalList.append(" UNION ");
                terminalList.append(" SELECT CHILD_TERMINAL_ID TERMINALID FROM FS_FR_TERMINAL_REGN");
                terminalList.append(" CONNECT BY PRIOR CHILD_TERMINAL_ID=PARENT_TERMINAL_ID START WITH PARENT_TERMINAL_ID='"+terminalId+"' ");
             }      
         contentList      =    new ArrayList();
         operationsImpl  =    new OperationsImpl();
         connection      =    operationsImpl.getConnection();
         //@@ Commented & Added by subrahmanyam for the pbn id: 203873 on 26-APR-10  
         //selectQuery     =    "SELECT SHIPMENTMODE ,HEADERFOOTER ,CONTENTID ,FLAG ,TEMINALID,DESCRIPTION,INVALIDATE   FROM QMS_CONTENTDTLS WHERE TEMINALID  IN ("+terminalList.toString()+") ORDER BY CONTENTID ";
         if("HO_TERMINAL".equalsIgnoreCase(loginAccesType))
        	 selectQuery     =    "SELECT SHIPMENTMODE ,HEADERFOOTER ,CONTENTID ,FLAG ,TEMINALID,DESCRIPTION,INVALIDATE   FROM QMS_CONTENTDTLS  ORDER BY CONTENTID ";
         else
        	 selectQuery     =    "SELECT SHIPMENTMODE ,HEADERFOOTER ,CONTENTID ,FLAG ,TEMINALID,DESCRIPTION,INVALIDATE   FROM QMS_CONTENTDTLS WHERE TEMINALID  IN ("+terminalList.toString()+") ORDER BY CONTENTID ";
         //Ended for 203873
         //ACTIVEINACTIVE ='N' AND 
         pStmt           =    connection.prepareStatement(selectQuery);
         resultSet       =    pStmt.executeQuery(); 
         
        
          while(resultSet.next())
          { 
           contentDOB           =      new QMSContentDOB();
           contentDOB.setShipmentMode(resultSet.getInt(1));           
           contentDOB.setHeaderFooter(resultSet.getString(2));                      
           contentDOB.setContentId(resultSet.getString(3));
           contentDOB.setDefaultFlag(resultSet.getString(4));
           contentDOB.setTerminalId(resultSet.getString(5));
           contentDOB.setContentDescription(resultSet.getString(6));
           contentDOB.setInvalidate(resultSet.getString(7));
           contentList.add(contentDOB);
         }
      }
      catch(Exception e)
      {
         e.printStackTrace();
         //Logger.error(FILE_NAME,"Error in getDensityGroupCodeDetails module",e.toString());       
         logger.error(FILE_NAME+"Error in getDensityGroupCodeDetails module"+e.toString());       
         return null;
      }    
      finally
      {
        contentDOB    =    null;
        ConnectionUtil.closeConnection(connection,pStmt,resultSet);
        
      }
      return contentList;
    }
    
    public void invalidateContentDtls(ArrayList contentList)throws SQLException,EJBException
    {
      Connection connection  = null;
      OperationsImpl operationsImpl = new OperationsImpl();
      QMSContentDOB  contentDOB  = null;
      PreparedStatement pstmt = null;
      String updateQry  = "UPDATE QMS_CONTENTDTLS    SET INVALIDATE =? WHERE CONTENTID   =? AND TEMINALID=?";
      try
      {
        
         connection      =    operationsImpl.getConnection();
        pstmt = connection.prepareStatement(updateQry);
        if(contentList!=null && contentList.size()>0)
        {
        	int contListSize	=	contentList.size();
          for(int i=0;i<contListSize;i++)
          {
            contentDOB  = (QMSContentDOB)contentList.get(i);
            pstmt.setString(1,contentDOB.getInvalidate());
            pstmt.setString(2,contentDOB.getContentId());
            pstmt.setString(3,contentDOB.getTerminalId());
            pstmt.executeUpdate();
          }
         }
          
       }catch(SQLException e)
       {e.printStackTrace();
        //Logger.error(FILE_NAME,"SQLException in invalidateIndustryId(ArrayList param0) method"+e.toString());
        logger.error(FILE_NAME+"SQLException in invalidateIndustryId(ArrayList param0) method"+e.toString());
        throw new SQLException();                  
       }catch(Exception e)
       {e.printStackTrace();
        //Logger.error(FILE_NAME,"EJBException in invalidateIndustryId(ArrayList param0) method"+e.toString());
        logger.error(FILE_NAME+"EJBException in invalidateIndustryId(ArrayList param0) method"+e.toString());
        throw new EJBException();            
       }finally
       {
        try{
           if(pstmt!=null)
            { pstmt.close();}
           if(connection!=null)
            { connection.close();}
        }catch(SQLException e)
        {
         //Logger.error(FILE_NAME,"SQLException in invalidateIndustryId(ArrayList param0) method"+e.toString());
         logger.error(FILE_NAME+"SQLException in invalidateIndustryId(ArrayList param0) method"+e.toString());
         throw new SQLException();            
        }
       }
    }
  public String insertCustContactDetial(ArrayList list,String custId,String TerminalId)throws SQLException,RemoteException
 {
         PreparedStatement		pStmt			      = null;	
         PreparedStatement		pStmt1			    = null;	
			   StringBuffer				  insQuery		    = new StringBuffer();
			   Connection				    connection		  = null;
	       ResultSet            rs              = null;    
         int                  prev_sl_no      = 0;
			   CustContactDtl       custDtl         = null;
				String                contactIdQry    = "SELECT NVL(MAX(SL_NO),-1)SL_NO FROM QMS_CUST_CONTACTDTL WHERE CUSTOMERID=?";
			   try
			   {
				  connection	=	getConnection();
				  insQuery.append("INSERT INTO QMS_CUST_CONTACTDTL ( CUSTOMERID ,TERMINALID ,ADDRTYPE ,CONTACTPERSON ,");
				  insQuery.append("DESIGNATION ,DEPT ,ZIPCODE ,CONTACTNO ,FAX ,EMAILID,CONTACTID,SL_NO) ");//contact id added by rk
				   insQuery.append(" VALUES(?,?,?,?,?,?,?,?,?,?,QMS_CUSTCONTACTIDS.NEXTVAL,?) ");

					 pStmt1       = connection.prepareStatement(contactIdQry);	
			 								
			      pStmt				=		connection.prepareStatement(insQuery.toString());
			      int listSize	=	list.size();
					 for(int i=0;i<listSize;i++)
					{ 
					  custDtl=(CustContactDtl)list.get(i);
					 
					  if(custDtl!=null)
					  {            
             pStmt1.setString(1,custId);
             rs          = pStmt1.executeQuery();                
            if(i==0){
             if(rs.next())
               prev_sl_no   = rs.getInt("SL_NO");	
            }
             if(rs!=null) rs.close();
					  pStmt.setString(1,custId);
					  pStmt.setString(2,TerminalId);
					  pStmt.setString(3,custDtl.getAddrType());
					  pStmt.setString(4,custDtl.getContactPerson());
					  pStmt.setString(5,custDtl.getDesignation());
					  pStmt.setString(6,custDtl.getDept());
					  pStmt.setString(7,custDtl.getZipCode());
					  pStmt.setString(8,custDtl.getContact());
					  pStmt.setString(9,custDtl.getFax());
					  pStmt.setString(10,custDtl.getEmail());
                     // pStmt.setInt(11,prev_sl_no+);
					  //@@Modified by kiran.v on 04/08/2011 for Wpbn Issue - 262471
					  prev_sl_no=prev_sl_no+1;
					  pStmt.setInt(11,prev_sl_no);
					  //@@Ended by kiran.v
				    pStmt.addBatch();
					}
			  }
       // Logger.info(FILE_NAME,"list.size()  "+list.size());
        if(list.size()>0)
           pStmt.executeBatch();
				  
			   
			   }
			   catch(SQLException sqlEx)
				{
				   //Logger.error(FILE_NAME,"insertCustContactDetial","SQLException While Inserting Customer Contact Details Record",sqlEx);
           logger.error(FILE_NAME+"insertCustContactDetial"+"SQLException While Inserting Customer Contact Details Record"+sqlEx);
				   throw new SQLException("Error while insering Contact Details(SQLException)");
				}
				catch(Exception e)
				{
					//Logger.error(FILE_NAME,"insertCustContactDetial","Exception ",e);
          logger.error(FILE_NAME+"insertCustContactDetial"+"Exception "+e);
					throw new EJBException("Error while insering Contact Details(SQLException)");
				}
				finally
			 	{
					ConnectionUtil.closeConnection(connection,pStmt);
          ConnectionUtil.closeConnection(connection,pStmt1);
			 	}
        return "success";
 }
 /* added by naresh for quote*/
  public StringBuffer insertCustAddrDetails(String custId,String terminalId,CustContactDtl custcontactDtl,Address address) throws EJBException
 {
 StringBuffer straddr       = new StringBuffer();
 Connection con             = null;
 PreparedStatement pStmt		=  null;
 ResultSet rs               =  null;
 try
 {
   
 
    InitialContext	jndiContext  = new InitialContext();
    OIDSessionHome	oidHome      = (OIDSessionHome)jndiContext.lookup("OIDSessionBean");
    OIDSession		oidRemote	 = (OIDSession)oidHome.create();	
    
    
    
    
    int 	addressId  =  oidRemote.getAddressOID();
    con=getConnection();
    String query2="INSERT INTO FS_ADDRESS(ADDRESSID,ADDRESSLINE1,ADDRESSLINE2,CITY,STATE,ZIPCODE,COUNTRYID,PHONENO,EMAILID ) VALUES(?,?,?,?,?,?,?,?,?)";
    
    
    pStmt=con.prepareStatement(query2);
    pStmt.setInt(1,addressId);
    pStmt.setString(2,address.getAddressLine1());
    pStmt.setString(3,address.getAddressLine2());
    pStmt.setString(4,address.getCity());
    pStmt.setString(5,address.getState());
    pStmt.setString(6,address.getZipCode());
    pStmt.setString(7,address.getCountryId());
    pStmt.setString(8,address.getPhoneNo());
    pStmt.setString(9,address.getEmailId());
    
    pStmt.executeUpdate();
    
    //Modified By RajKumari on 30-10-2008 for Connection Leakages.
    if(pStmt!=null){
      pStmt.close();
      pStmt=null;}
    String query="INSERT INTO FS_FR_CUSTOMER_ADDRESS( CUSTOMERID,TERMINALID,CUSTOMERADDRESSID,CONTACTNAME,DESIGNATION,DEL_FLAG,ADDRESS_TYPE ) VALUES(?,?,?,?,?,?,?)";
    pStmt=con.prepareStatement(query);
    pStmt.setString(1,custId);
    pStmt.setString(2,terminalId);
    pStmt.setInt(3,addressId);
    pStmt.setString(4,custcontactDtl.getContactPerson());
    pStmt.setString(5,custcontactDtl.getDesignation());
    pStmt.setString(6,custcontactDtl.getdeleteOption());
    pStmt.setString(7,custcontactDtl.getAddrType());
    pStmt.executeUpdate();
    
    //Modified By RajKumari on 30-10-2008 for Connection Leakages.
    if(pStmt!=null){
      pStmt.close();
      pStmt=null;}
    String query3="SELECT COUNTRYNAME FROM FS_COUNTRYMASTER  WHERE COUNTRYID=?";
    String countryName="";
    pStmt=con.prepareStatement(query3);
    pStmt.setString(1,address.getCountryId());
    rs=pStmt.executeQuery();
    
    if(rs.next())
      countryName=rs.getString(1);
    
    straddr.append(address.getAddressLine1()).append(" ");
    straddr.append(address.getAddressLine2()).append(" ");
    straddr.append(address.getCity()).append(" ");
    straddr.append(address.getState()).append(" ");
    straddr.append(countryName).append(" ");
    straddr.append(address.getZipCode());
    straddr.append(",").append(addressId);

 }
 catch(EJBException ejb)
 {
	ejb.printStackTrace(); 
   
   throw new EJBException(ejb);
 }
 catch(Exception e)
 {
   e.printStackTrace();
   throw new EJBException(e);
 }finally
 {
   ConnectionUtil.closeConnection(con,pStmt,rs);
 }
 
 return straddr; 
 }
 public ArrayList getContactNames(String customerId) throws SQLException,RemoteException
{
	 
    String sql1="SELECT CONTACTID,CONTACTPERSON,EMAILID FROM QMS_CUST_CONTACTDTL WHERE CUSTOMERID='"+customerId+"' AND  CONTACTPERSON IS NOT NULL ";//UNION ALL SELECT CONTACTNAME FROM FS_FR_CUSTOMERMASTER WHERE CUSTOMERID='"+customerId+"'  AND CONTACTNAME IS NOT NULL
   //contact id added by rk
   Connection connection = null;
    Statement stmt        = null;
    ResultSet rs          = null;
    ArrayList records = new ArrayList();
    try
    {
      connection     =this.getConnection();
      stmt       =connection.createStatement();
      rs         =stmt.executeQuery(sql1);
  
      while(rs.next())
      {
        //records.add(rs.getString(2)+"["+rs.getString(3)+"]"+"("+rs.getString(1)+")");
        records.add(rs.getString(2)+","+rs.getString(3)+","+rs.getString(1));//Commented and Modified by Kameswari for the WPBN issue-133569
       
      }
	  
	 
	  
    }
    catch(SQLException sqEx)
    {
        //Logger.error(FILE_NAME, "[getContactNames(String custId) ] -> "+sqEx.toString());
        logger.error(FILE_NAME+ "[getContactNames(String custId) ] -> "+sqEx.toString());
        throw new EJBException(sqEx.toString());
    }
    finally
    {
        ConnectionUtil.closeConnection(connection,stmt,rs);
    }
    return records;

}
//@@Commented and Modified by Kameswari for LOV issue
/*public ArrayList getDensityRatioIds(String dgcCode,ESupplyGlobalParameters loginbean)throws SQLException,EJBException
   {*/
   public ArrayList getDensityRatioIds(String dgcCode,ESupplyGlobalParameters loginbean,String searchString)throws SQLException,EJBException
   {
      Connection        connection      = null;
      OperationsImpl    operationsImpl  = new OperationsImpl();
      QMSContentDOB     contentDOB      = null;
      PreparedStatement pstmt           = null;
      ResultSet         rs              = null;
      String            sql             = null;
      String            terminalQry     = null;
      ArrayList         list            = new ArrayList();
     try
     {
       if(loginbean.getAccessType()!=null && "HO_TERMINAL".equalsIgnoreCase(loginbean.getAccessType()))
      {
        terminalQry  = "  AND TERMINALID IN(SELECT DISTINCT terminalid FROM FS_FR_TERMINALMASTER  WHERE oper_admin_flag <> 'H' UNION"+
                       " SELECT terminalid term_id  FROM FS_FR_TERMINALMASTER WHERE oper_admin_flag = 'H')";
      }
      else
      {
        terminalQry  =  " AND TERMINALID IN(SELECT TERMINALID FROM FS_FR_TERMINALMASTER WHERE TERMINALID = '"+loginbean.getTerminalId()+"' "+
                        " UNION SELECT CHILD_TERMINAL_ID TERMINALID FROM FS_FR_TERMINAL_REGN CONNECT BY PRIOR CHILD_TERMINAL_ID=PARENT_TERMINAL_ID START WITH PARENT_TERMINAL_ID='"+loginbean.getTerminalId()+"' "+
                        " UNION SELECT PARENT_TERMINAL_ID TERMINALID FROM FS_FR_TERMINAL_REGN CONNECT BY PRIOR PARENT_TERMINAL_ID=CHILD_TERMINAL_ID START WITH CHILD_TERMINAL_ID='"+loginbean.getTerminalId()+"')";
      }
	  if(dgcCode!=null && !"null".equals(dgcCode) && dgcCode.trim().length()!=0)
		  dgcCode=" AND DGCCODE  = '"+dgcCode+"' ";
    else
      dgcCode="";
	    
      
      
      //@@Commented and Modified by Kameswari for LOV issue
      // sql   = "SELECT KG_PER_M3,LB_PER_F3 FROM QMS_DENSITY_GROUP_CODE WHERE  INVALIDATE='F'  "+dgcCode+"  ORDER BY KG_PER_M3";
       sql   = "SELECT KG_PER_M3,LB_PER_F3 FROM QMS_DENSITY_GROUP_CODE WHERE  INVALIDATE='F'  AND KG_PER_M3 like '"+searchString+"%'"+dgcCode+"  ORDER BY KG_PER_M3";

       connection = operationsImpl.getConnection();
      // System.out.println("in deg LOV::"+sql);
       
       pstmt      = connection.prepareStatement(sql);
       rs         = pstmt.executeQuery();
       while(rs.next())
       {
         list.add(rs.getDouble(1)+":"+rs.getDouble(2));
       }
     }
     catch(SQLException e)
     {
       e.printStackTrace();
       throw new SQLException("Error while getting the Details");
     }
     catch(Exception e)
     {
       e.printStackTrace();
       throw new EJBException("Error while getting the Details");
     }
     finally
       {
        try{
           if(rs!=null)
            { rs.close();}
           if(pstmt!=null)
            { pstmt.close();}
           if(connection!=null)
            { connection.close();}
        }catch(SQLException e)
        {
         //Logger.error(FILE_NAME,"SQLException in invalidateIndustryId(ArrayList param0) method"+e.toString());
         logger.error(FILE_NAME+"SQLException in invalidateIndustryId(ArrayList param0) method"+e.toString());
         throw new SQLException();            
        }
       }return list;
   }
   //@@Commented and Modified by Kameswari for LOV issue
/*public ArrayList getDensityRatioIds(String dgcCode,String uom, ESupplyGlobalParameters loginbean)throws SQLException,EJBException
   {*/
  public ArrayList getDensityRatioIds(String dgcCode,String uom, ESupplyGlobalParameters loginbean,String searchStr)throws SQLException,EJBException
   {
      Connection        connection      = null;
      OperationsImpl    operationsImpl  = new OperationsImpl();
      QMSContentDOB     contentDOB      = null;
      PreparedStatement pstmt           = null;
      ResultSet         rs              = null;
      String            sql             = null;
      String            terminalQry     = null;
      ArrayList         list            = new ArrayList();
     try
     {
       if(loginbean.getAccessType()!=null && "HO_TERMINAL".equalsIgnoreCase(loginbean.getAccessType()))
      {
        terminalQry  = "  AND TERMINALID IN(SELECT DISTINCT terminalid FROM FS_FR_TERMINALMASTER)";
      }
      else
      {
        terminalQry  =  " AND TERMINALID IN(SELECT TERMINALID FROM FS_FR_TERMINALMASTER WHERE TERMINALID = '"+loginbean.getTerminalId()+"' "+
                        " UNION SELECT CHILD_TERMINAL_ID TERMINALID FROM FS_FR_TERMINAL_REGN CONNECT BY PRIOR CHILD_TERMINAL_ID=PARENT_TERMINAL_ID START WITH PARENT_TERMINAL_ID='"+loginbean.getTerminalId()+"' "+
                        " UNION SELECT PARENT_TERMINAL_ID TERMINALID FROM FS_FR_TERMINAL_REGN CONNECT BY PRIOR PARENT_TERMINAL_ID=CHILD_TERMINAL_ID START WITH CHILD_TERMINAL_ID='"+loginbean.getTerminalId()+"' "+
                        " UNION SELECT terminalid term_id  FROM FS_FR_TERMINALMASTER WHERE oper_admin_flag = 'H' )";
      }
	  if(dgcCode!=null && !"null".equals(dgcCode) && dgcCode.trim().length()!=0)
		  dgcCode=" AND DGCCODE  = '"+dgcCode+"' ";
    else
      dgcCode="";
	    
      /*sql = " SELECT DECODE(DGCCODE,'1','Air','2','Sea','4','Truck') shmode, DECODE((SELECT PRIMARY_BASIS FROM QMS_CHARGE_BASISMASTER WHERE CHARGEBASIS='"+uom+"'),'KG',KG_PER_M3||' Kg/M3','CBM',KG_PER_M3||' Kg/M3','LB',LB_PER_F3||' Lb/Ft3','CFT',LB_PER_F3||' Lb/Ft3','XX') DESNSITY_RATIO FROM QMS_DENSITY_GROUP_CODE WHERE  INVALIDATE='F'  "+dgcCode;
      */
      
     /* if("KG".equalsIgnoreCase(uom) || "CBM".equalsIgnoreCase(uom))
      {
        sql   = "SELECT KG_PER_M3 FROM QMS_DENSITY_GROUP_CODE WHERE  INVALIDATE='F'  "+dgcCode+"  ORDER BY KG_PER_M3";
        
      }else if("LB".equalsIgnoreCase(uom) || "CFT".equalsIgnoreCase(uom))
      {
        sql   = "SELECT LB_PER_F3 FROM QMS_DENSITY_GROUP_CODE WHERE  INVALIDATE='F'  "+dgcCode+"  ORDER BY LB_PER_F3";
      }
      */
      //@@Commented and Modified by Kameswari for LOV issue
      sql = " SELECT DECODE(DGCCODE,'1','Air','2','Sea','4','Truck') shmode, DECODE((SELECT PRIMARY_BASIS FROM QMS_CHARGE_BASISMASTER WHERE CHARGEBASIS='"+uom+"'),'KG',KG_PER_M3||' Kg/M3','CBM',KG_PER_M3||' Kg/M3','LB',LB_PER_F3||' Lb/Ft3','CFT',LB_PER_F3||' Lb/Ft3','XX') DESNSITY_RATIO FROM QMS_DENSITY_GROUP_CODE WHERE  KG_PER_M3 LIKE '"+searchStr+"%' AND INVALIDATE='F'  "+dgcCode;

       connection = operationsImpl.getConnection();
      // System.out.println("in deg LOV::"+sql);
       
       pstmt      = connection.prepareStatement(sql);
       rs         = pstmt.executeQuery();
       while(rs.next())
       {
         if(rs.getString("DESNSITY_RATIO").equalsIgnoreCase("XX"))
          break;
         else
          list.add(rs.getString("DESNSITY_RATIO")+"--"+rs.getString("shmode"));
       }
     }
     catch(SQLException e)
     {
       e.printStackTrace();
       throw new SQLException("Error while getting the Details");
     }
     catch(Exception e)
     {
       e.printStackTrace();
       throw new EJBException("Error while getting the Details");
     }
     finally
       {
        try{
           if(rs!=null)
            { rs.close();}
           if(pstmt!=null)
            { pstmt.close();}
           if(connection!=null)
            { connection.close();}
        }catch(SQLException e)
        {
         //Logger.error(FILE_NAME,"SQLException in getDensityRatioIds(ArrayList param0) method"+e.toString());
         logger.error(FILE_NAME+"SQLException in getDensityRatioIds(ArrayList param0) method"+e.toString());
         throw new SQLException();            
        }
       }return list;
   }   
   //@@Commented and Modified by Kameswari for LOV issue
//   public ArrayList getDensityRatioIdsForRates(String dgcCode,String uom, ESupplyGlobalParameters loginbean)throws SQLException,EJBException
   public ArrayList getDensityRatioIdsForRates(String dgcCode,String uom, ESupplyGlobalParameters loginbean,String searchString)throws SQLException,EJBException

   {
      Connection        connection      = null;
      OperationsImpl    operationsImpl  = new OperationsImpl();
      QMSContentDOB     contentDOB      = null;
      PreparedStatement pstmt           = null;
      ResultSet         rs              = null;
      String            sql             = null;
      String            terminalQry     = null;
      ArrayList         list            = new ArrayList();
     try
     {
       if(loginbean.getAccessType()!=null && "HO_TERMINAL".equalsIgnoreCase(loginbean.getAccessType()))
      {
        terminalQry  = "  AND TERMINALID IN(SELECT DISTINCT terminalid FROM FS_FR_TERMINALMASTER)";
      }
      else
      {
        terminalQry  =  " AND TERMINALID IN(SELECT TERMINALID FROM FS_FR_TERMINALMASTER WHERE TERMINALID = '"+loginbean.getTerminalId()+"' "+
                        " UNION SELECT CHILD_TERMINAL_ID TERMINALID FROM FS_FR_TERMINAL_REGN CONNECT BY PRIOR CHILD_TERMINAL_ID=PARENT_TERMINAL_ID START WITH PARENT_TERMINAL_ID='"+loginbean.getTerminalId()+"' "+
                        " UNION SELECT PARENT_TERMINAL_ID TERMINALID FROM FS_FR_TERMINAL_REGN CONNECT BY PRIOR PARENT_TERMINAL_ID=CHILD_TERMINAL_ID START WITH CHILD_TERMINAL_ID='"+loginbean.getTerminalId()+"' "+
                        " UNION SELECT terminalid term_id  FROM FS_FR_TERMINALMASTER WHERE oper_admin_flag = 'H' )";
      }
	  if(dgcCode!=null && !"null".equals(dgcCode) && dgcCode.trim().length()!=0)
		  dgcCode=" AND DGCCODE  = '"+dgcCode+"' ";
    else
      dgcCode="";
	    
      //sql = " SELECT DECODE(DGCCODE,'1','Air','2','Sea','4','Truck') shmode, DECODE((SELECT PRIMARY_BASIS FROM QMS_CHARGE_BASISMASTER WHERE CHARGEBASIS='"+uom+"'),'KG',KG_PER_M3||' Kg/M3','CBM',KG_PER_M3||' Kg/M3','LB',LB_PER_F3||' Lb/Ft3','CFT',LB_PER_F3||' Lb/Ft3','XX') DESNSITY_RATIO FROM QMS_DENSITY_GROUP_CODE WHERE  INVALIDATE='F'  "+dgcCode;
      
      if("KG".equalsIgnoreCase(uom) || "CBM".equalsIgnoreCase(uom))
      {
       //@@Commented and Modified by Kameswari for LOV issue
        //sql   = "SELECT KG_PER_M3 ||' Kg/M3' DESNSITY_RATIO FROM QMS_DENSITY_GROUP_CODE WHERE  INVALIDATE='F'  "+dgcCode+"  ORDER BY KG_PER_M3";
      sql   = "SELECT KG_PER_M3 ||' Kg/M3' DESNSITY_RATIO FROM QMS_DENSITY_GROUP_CODE WHERE  INVALIDATE='F'  AND KG_PER_M3 like '"+searchString+"%'"+dgcCode+"  ORDER BY KG_PER_M3";
  
      }else if("LB".equalsIgnoreCase(uom) || "CFT".equalsIgnoreCase(uom))
      {
       //@@Commented and Modified by Kameswari for LOV issue
       //sql   = "SELECT LB_PER_F3 ||' Lb/Ft3' DESNSITY_RATIO FROM  QMS_DENSITY_GROUP_CODE WHERE  INVALIDATE='F' +dgcCode+"  ORDER BY LB_PER_F3";
        sql   = "SELECT LB_PER_F3 ||' Lb/Ft3' DESNSITY_RATIO FROM  QMS_DENSITY_GROUP_CODE WHERE  INVALIDATE='F' AND KG_PER_M3 like '"+searchString+"%'"+dgcCode+"  ORDER BY LB_PER_F3";
      }
      
       
       
       connection = operationsImpl.getConnection();
      // System.out.println("in deg LOV::"+sql);
       
       pstmt      = connection.prepareStatement(sql);
       rs         = pstmt.executeQuery();
       while(rs.next())
       {
         if(rs.getString("DESNSITY_RATIO").equalsIgnoreCase("XX"))
          break;
         else
          list.add(rs.getString("DESNSITY_RATIO"));
       }
     }
     catch(SQLException e)
     {
       e.printStackTrace();
       throw new SQLException("Error while getting the Details");
     }
     catch(Exception e)
     {
       e.printStackTrace();
       throw new EJBException("Error while getting the Details");
     }
     finally
       {
        try{
           if(rs!=null)
            { rs.close();}
           if(pstmt!=null)
            { pstmt.close();}
           if(connection!=null)
            { connection.close();}
        }catch(SQLException e)
        {
         //Logger.error(FILE_NAME,"SQLException in getDensityRatioIds(ArrayList param0) method"+e.toString());
         logger.error(FILE_NAME+"SQLException in getDensityRatioIds(ArrayList param0) method"+e.toString());
         throw new SQLException();            
        }
       }return list;
   }   
//@@ Commented and added by subrahmanyam for the pbn id: 203354  on 23-APR-10
   //public ArrayList getMarginLimitLevelIds(String searchStr,String shipmentMode,String consoletype,String chargeType)
   public ArrayList getMarginLimitLevelIds(String searchStr,String shipmentMode,String consoletype,String chargeType,ESupplyGlobalParameters loginbean,String operation)   
  {
      PreparedStatement pstmt = null;
      ResultSet         rs    = null;
      Connection  connection  = null;
      int shipmentType        = 0;  
      String levelNo          = "";
      ArrayList    levelNoList= new ArrayList();
      String marginId         = "";
    
	
    try
    {
          if(shipmentMode!=null && !shipmentMode.equals(""))
          {
                if(shipmentMode.equals("1"))
                {
                  shipmentType	=	FoursoftConfig.AIR;
                  marginId      = new Integer(shipmentType).toString();
                }
                else if(shipmentMode.equals("0"))
                {
                  shipmentType	=	0;
                  marginId      = new Integer(shipmentType).toString();
                }
            else if(shipmentMode.equals("2"))
            {
            
                if(consoletype!=null && consoletype.equals("LCL"))
                {
                  shipmentType	=	FoursoftConfig.SEA_LCL;
                  marginId      = new Integer(shipmentType).toString();
                }else if(consoletype!=null && consoletype.equals("FCL"))
                {
                  shipmentType	=	FoursoftConfig.SEA_FCL;
                  marginId      = new Integer(shipmentType).toString();
                }else
                {
                  shipmentType	=	FoursoftConfig.SEA_LCL;             
                  marginId      = "'"+shipmentType+"'";
                  shipmentType	=	FoursoftConfig.SEA_FCL;                
                  marginId      += ",'"+shipmentType+"'";
                }
              
            }else if(shipmentMode.equals("4"))
            {
                if(consoletype!=null && consoletype.equals("LTL"))
                {
                  shipmentType	=	FoursoftConfig.TRUCK_LTL;
                  marginId      = new Integer(shipmentType).toString();                
                }else if(consoletype!=null && consoletype.equals("FTL"))
                {
                  shipmentType	=	FoursoftConfig.TRUCK_FTL;
                  marginId      = new Integer(shipmentType).toString();                
                }else
                {
                  shipmentType	=	FoursoftConfig.TRUCK_LTL;             
                  marginId      = "'"+shipmentType+"'";
                  shipmentType	=	FoursoftConfig.TRUCK_FTL;                
                  marginId      += ",'"+shipmentType+"'";                    
                }
            }
            
          }
                 
          if(searchStr==null || searchStr.equals(""))
          {
              searchStr = "";
          }
        
          connection = operationsImpl.getConnection();

          //@@ Commented and added by subrahmanyam for the pbn id: 203354  on 23-APR-10
			    //pstmt      = connection.prepareStatement("SELECT distinct LEVELNO FROM QMS_MARGIN_LIMIT_DTL WHERE INVALIDATE='F' AND MARGIN_ID=? AND CHARGETYPE=? AND LEVELNO LIKE ?");
          if("view".equalsIgnoreCase(operation) || "viewAll".equalsIgnoreCase(operation) || "HO_TERMINAL".equalsIgnoreCase(loginbean.getAccessType()))
        	  pstmt      = connection.prepareStatement("SELECT distinct LEVELNO FROM QMS_MARGIN_LIMIT_DTL WHERE INVALIDATE='F' AND MARGIN_ID=? AND CHARGETYPE=? AND LEVELNO LIKE ?");
          else if("ADMN_TERMINAL".equalsIgnoreCase(loginbean.getAccessType()))
        	  	pstmt      = connection.prepareStatement("SELECT distinct LEVELNO FROM QMS_MARGIN_LIMIT_DTL WHERE INVALIDATE='F' AND MARGIN_ID=? AND CHARGETYPE=? AND LEVELNO LIKE ?"+
        	  											" AND TERMINALID IN(SELECT CHILD_TERMINAL_ID FROM FS_FR_TERMINAL_REGN WHERE PARENT_TERMINAL_ID='"+loginbean.getTerminalId()+"'"+
        	  											" UNION SELECT TERMINALID FROM FS_FR_TERMINALMASTER WHERE TERMINALID='"+loginbean.getTerminalId()+"')");
          else if("OPER_TERMINAL".equalsIgnoreCase(loginbean.getAccessType()))
      	  	pstmt      = connection.prepareStatement("SELECT distinct LEVELNO FROM QMS_MARGIN_LIMIT_DTL WHERE INVALIDATE='F' AND MARGIN_ID=? AND CHARGETYPE=? AND LEVELNO LIKE ?"+
						" AND TERMINALID IN(SELECT TERMINALID FROM FS_FR_TERMINALMASTER WHERE TERMINALID='"+loginbean.getTerminalId()+"')");
        	  
          pstmt.setString(1,marginId);
          pstmt.setString(2,chargeType); 
          pstmt.setString(3,(searchStr+"%")); 
          rs = pstmt.executeQuery();
                  
          while(rs.next())
          {
            levelNo = (rs.getString("LEVELNO")!=null)?rs.getString("LEVELNO"):"";
            levelNoList.add(levelNo);
          }
    }catch(SQLException e)
    {e.printStackTrace();
        //Logger.error(FILE_NAME,"------->getMarginLimitLevelIds()",e.toString());
        logger.error(FILE_NAME+"------->getMarginLimitLevelIds()"+e.toString());
        throw new EJBException();         
    }catch(Exception e)
    {e.printStackTrace();
        //Logger.error(FILE_NAME,"------->getMarginLimitLevelIds()",e.toString());
        logger.error(FILE_NAME+"------->getMarginLimitLevelIds()"+e.toString());
        throw new EJBException();         
    }finally
    {
      try
      {
        if(rs!=null)
          { rs.close();}
        if(pstmt!=null)
          { pstmt.close();}
        if(connection!=null)
          { connection.close();}
      }catch(SQLException e)
      {
        //Logger.error(FILE_NAME,"------->getMarginLimitLevelIds()",e.toString());
        logger.error(FILE_NAME+"------->getMarginLimitLevelIds()"+e.toString());
        throw new EJBException();
      }
    }
    return levelNoList;
  }

   
   public ArrayList getCustomerIds(ESupplyGlobalParameters loginBean)
   {
      PreparedStatement pstmt         = null;
      ResultSet         rs            = null;
      Connection        connection    = null;
      ArrayList         customerIds   = new ArrayList();
      StringBuffer      terminalQuery = new StringBuffer("");
      String            terminalId    = null;
     try
     {
          connection = operationsImpl.getConnection();
          terminalId = loginBean.getTerminalId();
          if("HO_TERMINAL".equals(loginBean.getAccessType()))
          {
            terminalQuery.append("(SELECT TERMINALID FROM FS_FR_TERMINALMASTER)");
          }
          else
          {
            terminalQuery.append( "(SELECT PARENT_TERMINAL_ID TERM_ID FROM FS_FR_TERMINAL_REGN CONNECT BY CHILD_TERMINAL_ID = PRIOR PARENT_TERMINAL_ID START WITH CHILD_TERMINAL_ID = '").append(terminalId).append("'")
                   .append( " UNION ")
                   .append( " SELECT TERMINALID TERM_ID FROM FS_FR_TERMINALMASTER WHERE OPER_ADMIN_FLAG = 'H' ")
                   .append( " UNION ")
                   .append( " SELECT '").append(terminalId).append("' TERM_ID FROM DUAL ")
                   .append( " UNION ")
                   .append( " SELECT CHILD_TERMINAL_ID TERM_ID FROM FS_FR_TERMINAL_REGN CONNECT BY PRIOR CHILD_TERMINAL_ID = PARENT_TERMINAL_ID START WITH PARENT_TERMINAL_ID = '").append(terminalId).append("')");
          }
			    pstmt      = connection.prepareStatement("SELECT  DISTINCT CUSTOMER.CUSTOMERID FROM FS_FR_CUSTOMERMASTER CUSTOMER,FS_ADDRESS ADDRESS   WHERE CUSTOMER.CUSTOMERADDRESSID=ADDRESS.ADDRESSID  AND CUSTOMER.CUSTOMERADDRESSID=ADDRESS.ADDRESSID AND TERMINALID IN "+terminalQuery+" ORDER BY CUSTOMER.CUSTOMERID DESC");
         // pstmt.setString(1,loginBean.getTerminalId());
                  
          rs = pstmt.executeQuery();
                  
          while(rs.next())
              customerIds.add(rs.getString("CUSTOMERID"));
          
     }catch(SQLException e)
    {e.printStackTrace();
        //Logger.error(FILE_NAME,"------->getCustomerIds()",e.toString());
        logger.error(FILE_NAME+"------>getCustomerIds()"+e.toString());
        throw new EJBException();         
    }catch(Exception e)
    {e.printStackTrace();
        //Logger.error(FILE_NAME,"------->getCustomerIds()",e.toString());
        logger.error(FILE_NAME+"------->getCustomerIds()"+e.toString());
        throw new EJBException();         
    }finally
    {
      try
      {
        if(rs!=null)
          { rs.close();}
        if(pstmt!=null)
          { pstmt.close();}
        if(connection!=null)
          { connection.close();}
      }catch(SQLException e)
      {
        //Logger.error(FILE_NAME,"------->getCustomerIds()"+e.toString());
        logger.error(FILE_NAME+"------->getCustomerIds()"+e.toString());
        throw new EJBException();
      }
    }
    return customerIds;
  } 
   
   public ArrayList getLoctIds(ESupplyGlobalParameters loginBean,String searchString)
   {
      PreparedStatement pstmt = null;
      ResultSet         rs    = null;
      Connection  connection  = null;
      ArrayList  locationIds  = new ArrayList();
     try
     {
          connection = operationsImpl.getConnection();
			    pstmt      = connection.prepareStatement("SELECT LOCATIONID	FROM FS_FR_LOCATIONMASTER WHERE	LOCATIONID LIKE ?");
          pstmt.setString(1,(searchString+"%"));
                  
          rs = pstmt.executeQuery();
                  
          while(rs.next())
              locationIds.add(rs.getString("LOCATIONID"));
          
     }catch(SQLException e)
    {e.printStackTrace();
        //Logger.error(FILE_NAME,"------->getLoctIds()",e.toString());
        logger.error(FILE_NAME+"------->getLoctIds()"+e.toString());
        throw new EJBException();         
    }catch(Exception e)
    {e.printStackTrace();
        //Logger.error(FILE_NAME,"------->getLoctIds()",e.toString());
        logger.error(FILE_NAME+"------->getLoctIds()"+e.toString());
        throw new EJBException();         
    }finally
    {
      try
      {
        if(rs!=null)
          { rs.close();}
        if(pstmt!=null)
          { pstmt.close();}
        if(connection!=null)
          { connection.close();}
      }catch(SQLException e)
      {
        //Logger.error(FILE_NAME,"------->getLoctIds()",e.toString());
        logger.error(FILE_NAME+"------->getLoctIds()"+e.toString());
        throw new EJBException();
      }
    }
    return locationIds;
  } 
 



 public ArrayList getServiceLevelIdsHirarchy(String searchString,String shipmentMode,String terminalId,String operation)
    {
		Connection	connection	= null;
       // Statement	stmt 		= null;//Commented By RajKumari on 30-10-2008 for Connection Leakages.
        ResultSet	rs   		= null;
		String		whereclause = null;
		CallableStatement cstmt = null;

		// @@ Added by G.Srinivas for TogetherJ on 20050105
		//StringBuffer   servId1           = new StringBuffer();
		//StringBuffer   sMode1            = new StringBuffer();
		// @@

		if(searchString==null){
			searchString= "";
		}
		
		if(shipmentMode!=null){			
				
				if(shipmentMode.equals("4")){
					whereclause = "WHERE SERVICELEVELID LIKE '"+searchString+"%' AND SHIPMENTMODE IN (4,5,6,7)";
				}else 	if(shipmentMode.equals("1")){
					whereclause = "WHERE SERVICELEVELID LIKE '"+searchString+"%' AND SHIPMENTMODE IN (1,3,5,7) ";
				}else 	if(shipmentMode.equals("All")){
					whereclause = "WHERE SERVICELEVELID LIKE '"+searchString+"%'  ";
				}else{
					whereclause = "WHERE SERVICELEVELID LIKE '"+searchString+"%' AND SHIPMENTMODE IN (2,3,6,7) ";
				}
		
		}
		
	/*	if(searchString!=null){
			whereclause = " WHERE SERVICELEVELID LIKE '"+searchString+"%' AND SHIPMENTMODE IN (1,3,5,7)";
		}else{
			whereclause = " WHERE SERVICELEVELID LIKE '%' AND SHIPMENTMODE IN (1,3,5,7)";
		} */

		//WHERE SERVICELEVELID LIKE '"+searchString+"' AND SHIPMENTMODE IN (1,3,5,7)


		String sMode = "";	
		//String sql= "SELECT SERVICELEVELID,SERVICELEVELDESC,SHIPMENTMODE FROM FS_FR_SERVICELEVELMASTER "+whereclause+" ORDER BY SERVICELEVELID";
		ArrayList serviceLevelIds = new ArrayList();

		try
        {
			connection = this.getConnection();
			//stmt = connection.createStatement();
			//rs = stmt.executeQuery(sql);
		cstmt        = connection.prepareCall("{ ?= call QMS_SETUPS.GET_SERVICELEVEL_HIRARCHY(?,?,?,?)}");
      cstmt.registerOutParameter(1,OracleTypes.CURSOR);
      cstmt.setString(2,terminalId);
      cstmt.setString(3,shipmentMode);
      cstmt.setString(4,(searchString+"%"));
      cstmt.setString(5,operation);
      cstmt.execute();
      rs  =  (ResultSet)cstmt.getObject(1);
			//connection = this.getConnection();
			//stmt = connection.createStatement();
			//rs = stmt.executeQuery(sql);

			while ( rs.next() )
			{
				// @@ Added by G.Srinivas for TogetherJ Fix on 20050105
		StringBuffer   servId1           = new StringBuffer();
		StringBuffer   sMode1            = new StringBuffer();
		// @@

				String servId = rs.getString(1);
				String name = rs.getString(2);
				if (name==null)
				{name="";}
									
				if(servId.length() == 4)	
				//{servId = servId+" ";	}
				// @@ Replaced by G.Srinivas for TogetherJ on 20050105
                 {servId1.append(servId);
				  servId1.append(" ");
				  servId = servId1.toString();}
				// @@
				
				if(servId.length() == 3)
				//{servId = servId+"   ";}		
				// @@ Replaced by G.Srinivas for TogetherJ on 20050105
                 {servId1.append(servId);
				  servId1.append(" ");
				  servId = servId1.toString();}
				// @@
			
				if(servId.length() == 2)	
				//{servId = servId+"    ";}	
				// @@ Replaced by G.Srinivas for TogetherJ on 20050105
                 {servId1.append(servId);
				  servId1.append(" ");
				  servId = servId1.toString();}
				// @@
			
				if(servId.length() == 1)
				//{servId = servId+"     ";}	
				// @@ Replaced by G.Srinivas for TogetherJ on 20050105
                 {servId1.append(servId);
				  servId1.append(" ");
				  servId = servId1.toString();}
				// @@
			
				sMode = (new String()).valueOf(rs.getInt(3));
				 
				if(sMode!=null)
				{ 
					if( sMode.equals("7"))   
					{sMode="AST";}
					if(sMode.equals("1"))
					{sMode="A  ";}
					if(sMode.equals("2"))
					{sMode=" S ";}		
					if(sMode.equals("3"))
					{sMode="AS ";}
					if(sMode.equals("4"))
					{sMode="  T";}
					if(sMode.equals("5"))
					{sMode="A T";}
					if(sMode.equals("6"))
					{sMode=" ST";}
				}	
				
				//sMode = servId+"[" +sMode+ "]"+"[" +name+ "]";
				// @@ Replaced by G.Srinivas for TogetherJ on 20050105
				  sMode1.append(servId);
				  sMode1.append("[" );
				  sMode1.append(sMode);
				  sMode1.append( "]");
				  sMode1.append("[" );
				  sMode1.append(name);
				  sMode1.append( "]");
				  sMode = sMode1.toString();
                // @@

				serviceLevelIds.add(sMode);
			}
		}
		catch(SQLException sqEx)
        {sqEx.printStackTrace();
			//Logger.error(FILE_NAME, "[getServiceLevelIds(whereclause)] -> "+sqEx.toString());
      logger.error(FILE_NAME+ "[getServiceLevelIds(whereclause)] -> "+sqEx.toString());
            throw new EJBException(sqEx.toString());
        }
		catch(Exception e)
        {
          e.printStackTrace();
           throw new EJBException(e.toString());
        }
        finally
        {
			ConnectionUtil.closeConnection(connection, cstmt, rs);
        }
        return serviceLevelIds;
    }
    
   public ArrayList getSalesPersonIds(ESupplyGlobalParameters loginBean,String terminalIds)
   {
      Statement pstmt = null;
      ResultSet         rs    = null;
      Connection  connection  = null;
      ArrayList  salesPersonIds  = new ArrayList();
      String   termialId     = null;
     try
     {
          terminalIds  = terminalIds.replaceAll(",","','");
          
          if(terminalIds.length()>0)
           termialId="WHERE LOCATIONID IN ('"+terminalIds+"')";
          else
           termialId="";
           
          connection = operationsImpl.getConnection();
			    pstmt      = connection.createStatement();
          rs         = pstmt.executeQuery("SELECT EMPID FROM FS_USERMASTER "+termialId+" ORDER BY EMPID");
                          
          while(rs.next())
              salesPersonIds.add(rs.getString("EMPID"));
          
     }catch(SQLException e)
    {e.printStackTrace();
        //Logger.error(FILE_NAME,"------->getSalesPersonIds()",e.toString());
        logger.error(FILE_NAME+"------->getSalesPersonIds()"+e.toString());
        throw new EJBException();         
    }catch(Exception e)
    {e.printStackTrace();
        //Logger.error(FILE_NAME,"------->getSalesPersonIds()",e.toString());
        logger.error(FILE_NAME+"------->getSalesPersonIds()"+e.toString());
        throw new EJBException();         
    }finally
    {
      try
      {
        if(rs!=null)
          { rs.close();}
        if(pstmt!=null)
          { pstmt.close();}
        if(connection!=null)
          { connection.close();}
      }catch(SQLException e)
      {
        //Logger.error(FILE_NAME,"------->getSalesPersonIds()",e.toString());
        logger.error(FILE_NAME+"------->getSalesPersonIds()"+e.toString());
        throw new EJBException();
      }
    }
    return salesPersonIds;
  } 
  public ArrayList getContactNames(String customerId,String addressType) 
  {
    String  addressCheck    = "";
    if(addressType.length()>0)
    {
      if(addressType.equalsIgnoreCase("P"))
       addressCheck=" AND ADDRTYPE='P' ";
      else if(addressType.equalsIgnoreCase("D"))
       addressCheck=" AND ADDRTYPE='D' ";
      else if(addressType.equalsIgnoreCase("B"))
       addressCheck=" AND ADDRTYPE='B' ";
      else
       addressCheck=" ";
    }
    
    String sql1=  "SELECT SL_NO,CONTACTPERSON,NVL(EMAILID,FAX) FROM QMS_CUST_CONTACTDTL WHERE CUSTOMERID=? "+
                  "AND CONTACTPERSON IS NOT NULL "+addressCheck+" ORDER BY SL_NO";//UNION ALL SELECT CONTACTNAME FROM FS_FR_CUSTOMERMASTER WHERE CUSTOMERID='"+customerId+"'  AND CONTACTNAME IS NOT NULL
   //contact id added by rk
	 
	 
    Connection connection         = null;
    PreparedStatement stmt        = null;
    ResultSet rs                  = null;
    ArrayList records             = new ArrayList();
    try
    {
      connection     =  this.getConnection();
      stmt           =  connection.prepareStatement(sql1);
      stmt.setString(1,customerId);
      rs             =  stmt.executeQuery();
  
      while(rs.next())
      {
      // records.add(rs.getString(2)+"["+rs.getString(3)+"]"+"("+rs.getString(1)+")");//Commented and Modified by Kameswari for the WPBN issue-133569
      records.add(rs.getString(2)+","+rs.getString(3)+","+rs.getString(1));
    
        
      }
    }
    catch(SQLException sqEx)
    {
        //Logger.error(FILE_NAME, "[getContactNames(String custId) ] -> "+sqEx.toString());
        logger.error(FILE_NAME+ "[getContactNames(String custId) ] -> "+sqEx.toString());
        throw new EJBException(sqEx.toString());
    }
    finally
    {
        ConnectionUtil.closeConnection(connection,stmt,rs);
    }
    return records;

   } 
 // public ArrayList getCostingContactNames(String customerId,long quoteId) throws EJBException 
  public ArrayList getCostingContactNames(String customerId,String quoteId) throws EJBException //@@modified by kameswari on 11/02/09
  {
    Connection        connection          = null;
    PreparedStatement pStmt               = null;
    ResultSet         rs                  = null;
    ArrayList         records             = new ArrayList();
    
    String            addressCheck        = "";
    String            addressType         = null;
    
    String addressTypeQry =  "SELECT ADDRESS_TYPE FROM FS_FR_CUSTOMER_ADDRESS WHERE CUSTOMERADDRESSID = "+
                            "(SELECT CUSTOMER_ADDRESSID FROM QMS_QUOTE_MASTER WHERE CUSTOMER_ID = ? AND QUOTE_ID = ? AND ACTIVE_FLAG = 'A')";
    
	
    try
    {
      connection     =  this.getConnection();
      pStmt          =  connection.prepareStatement(addressTypeQry);
      pStmt.setString(1,customerId);
      pStmt.setString(2,quoteId);
      
      rs             =  pStmt.executeQuery();
      
      if(rs.next())
      {
        addressType  =  rs.getString("ADDRESS_TYPE");
      }
      
      if(rs!=null)
          rs.close();
      if(pStmt!=null)
          pStmt.close();
      
      if(addressType!=null && addressType.trim().length()>0)
      {
        if(addressType.equalsIgnoreCase("P"))
         addressCheck=" AND ADDRTYPE='P' ";
        else if(addressType.equalsIgnoreCase("D"))
         addressCheck=" AND ADDRTYPE='D' ";
        else if(addressType.equalsIgnoreCase("B"))
         addressCheck=" AND ADDRTYPE='B' ";
        else
         addressCheck=" ";
      }
      
      String sql1         =  "SELECT SL_NO,CONTACTPERSON,NVL(EMAILID,FAX) FROM QMS_CUST_CONTACTDTL WHERE CUSTOMERID=? "+
                             "AND CONTACTPERSON IS NOT NULL "+addressCheck+" ORDER BY SL_NO";
      
      pStmt          =  connection.prepareStatement(sql1);
      pStmt.setString(1,customerId);
      rs             =  pStmt.executeQuery();
  
      while(rs.next())
      {
         //  records.add(rs.getString(2)+"["+rs.getString(3)+"]"+"("+rs.getString(1)+")");
             records.add(rs.getString(2)+","+rs.getString(3)+","+rs.getString(1));//Commented and Modified by Kameswari for the WPBN issue-133569,143522

      }
    }
    catch(SQLException sqEx)
    {
        logger.error(FILE_NAME+ "[getContactNames(String custId) ] -> "+sqEx.toString());
        throw new EJBException(sqEx.toString());
    }
    finally
    {
        ConnectionUtil.closeConnection(connection,pStmt,rs);
    }
    return records;

   } 
  public ArrayList getAdvSerchLov(QMSAdvSearchLOVDOB advSearchLovDOB) throws EJBException
  {
    
    StringBuffer            sb            = null; // content stringbuffer variable which has to be displayed
    MarginLimitMasterDAO    dao           = null;
    QMSAdvSearchHelperObj   helperObj     = null;
		ArrayList               listValues    = null;
	//Added by Rakesh for Issue:          on 04-03-2011
	Connection        connection          = null; 
    PreparedStatement pStmt               = null;
    ResultSet         rs                  = null;
	String			  sql				  = null;//Added by Rakesh for Issue:          on 04-03-2011
		try
		{
      helperObj = new QMSAdvSearchHelperObj();
        helperObj.setOperation(advSearchLovDOB.getOperation());
      helperObj.setTerminalId(advSearchLovDOB.getTerminalId());
      helperObj.setWhereCondition(advSearchLovDOB.getLovWhere());
     helperObj.setDesignationId(advSearchLovDOB.getDesignationId());
      helperObj.setShipmentMode(advSearchLovDOB.getShipmentMode());
      helperObj.setAccessLevel(advSearchLovDOB.getAccessLevel());
        //@@Added by Kameswari for the WPBN issue-26514
      helperObj.setEmpId(advSearchLovDOB.getEmpId());
      helperObj.setBuyRatesPermission(advSearchLovDOB.getBuyRatesPermission());
      //@@End-26514
      helperObj.setLocalAcceslevel(advSearchLovDOB.getLocalAcceslevel());//added by VLAKSHMI on 22/05/2009
      helperObj.setLocalTerminal(advSearchLovDOB.getLocalTerminal());//added by VLAKSHMI on 22/05/2009
      helperObj.setMultiQuote(advSearchLovDOB.getMultiQuote());//added by Rakesh on 16-03-2011
      //Added by Rakesh for Issue:       on 04-03-2011
      sql			 = "SELECT DISTINCT COUNTRYID FROM FS_ADDRESS WHERE ADDRESSID IN(SELECT TM.CONTACTADDRESSID  FROM FS_FR_TERMINALMASTER TM WHERE TERMINALID =?)";
      connection     =  this.getConnection();
      pStmt          =  connection.prepareStatement(sql);
      pStmt.setString(1,advSearchLovDOB.getTerminalId());
      rs             =  pStmt.executeQuery();
      while(rs.next())
      {
    	  helperObj.setCountryId(rs.getString(1));  
      }
    //Ended by Rakesh for Issue:       on 04-03-2011
   advSearchLovDOB.setWhereCondition(helperObj.getWhereCondition());
       
       dao = new MarginLimitMasterDAO();
  		listValues  = dao.getAdvSerchLov(advSearchLovDOB);
 		}
    catch(SQLException sqle)
    {
      sqle.printStackTrace();
      //Logger.error(FILE_NAME, "Error occured in getAdvSerchLov:"+sqle);
      logger.error(FILE_NAME+ "Error occured in getAdvSerchLov:"+sqle);
      throw new EJBException(sqle.toString());
    }
    catch(Exception mainEx)
		{
      mainEx.printStackTrace();
      //Logger.error(FILE_NAME, "Error occured in getAdvSerchLov:"+mainEx);
      logger.error(FILE_NAME+ "Error occured in getAdvSerchLov:"+mainEx);
      throw new EJBException(mainEx.toString());
    }
    finally
    {
    	ConnectionUtil.closeConnection(connection, pStmt, rs); // Added by Gowtham for Connection Leaks on 25Mar2011
    }
			 
    //return value
    if(listValues!=null)
    {
      return listValues;
    }
    else
    {
      return null;
    }	
    
	}//getSearchData ends here
  //@@Added by Kameswari for the WPBN issue-61295
   public int addEmailTextDtls(QMSEmailTextDOB emailTextDOB)throws EJBException
   {
      Connection          connection  = null;
      PreparedStatement   pst         = null;
      ResultSet           rs          = null;
      String  terminalidquery         = "SELECT COUNT(*)CNT FROM QMS_EMAIL_TEXT_MASTER WHERE TERMINAL_ID=? AND QUOTE_TYPE=?";
      String addEmailtextquery        = "INSERT INTO QMS_EMAIL_TEXT_MASTER(ID,TERMINAL_ID,TEXT,CREATED_DATE,CREATED_BY,QUOTE_TYPE) VALUES(SEQ_EMAIL_TEXT.NEXTVAL,?,?,?,?,?)";
      int i =0 ,count = 0;
      try
      {
          connection  = getConnection();
          pst         = connection.prepareStatement(terminalidquery);
          pst.setString(1,emailTextDOB.getTerminalId());
          pst.setString(2,emailTextDOB.getQuoteType());
          rs          = pst.executeQuery();
          if(rs.next())
          {
            count = rs.getInt("CNT");
          }
          if(rs!=null)
              rs.close();
            if(pst!=null)
              pst.close();
          
          if(count>0)
          {           
               i = -1;
          }
          else
          {
             StringReader  reader  = new StringReader(emailTextDOB.getEmailText());
             pst         = connection.prepareStatement(addEmailtextquery); 
             pst.setString(1,emailTextDOB.getTerminalId());
             pst.setCharacterStream(2,reader,emailTextDOB.getEmailText().length());
             pst.setTimestamp(3,new java.sql.Timestamp((new java.util.Date()).getTime()));
             pst.setString(4,emailTextDOB.getCreatedBy());
             pst.setString(5,emailTextDOB.getQuoteType());
             
             i = pst.executeUpdate();
             operationsImpl.setTransactionDetails(emailTextDOB.getTerminalId(),emailTextDOB.getCreatedBy(),"Email Text Master","",new java.sql.Timestamp((new java.util.Date()).getTime()),"Add");
          } 
      }
      
      catch(Exception e)
      {
          e.printStackTrace();
          i = 0;
          logger.error(FILE_NAME+"Exception in adding EmailText");
          throw  new EJBException(e.toString());
      }
      finally
      {
         ConnectionUtil.closeConnection(connection,pst,rs);   
      }
      return  i;
   }
   public  QMSEmailTextDOB  viewEmailTextDtls(QMSEmailTextDOB dob) throws EJBException
   {
      QMSEmailTextDOB    emailTextDOB = null; 
      Connection          connection  = null;
      PreparedStatement   pst         = null;
      ResultSet           rs          = null;
      boolean  flag = false;
     
      String      viewEmailtextquery  = "SELECT ID,TEXT FROM QMS_EMAIL_TEXT_MASTER WHERE TERMINAL_ID=? AND QUOTE_TYPE=?";
      String      viewEmailtextqueryAdm  = "SELECT ID,TEXT FROM QMS_EMAIL_TEXT_MASTER WHERE TERMINAL_ID=? AND TERMINAL_ID IN(SELECT TERMINALID FROM FS_FR_TERMINALMASTER WHERE TERMINALID=?) AND QUOTE_TYPE=?";
      String      viewEmailtextqueryOpr  = "SELECT ID,TEXT FROM QMS_EMAIL_TEXT_MASTER WHERE TERMINAL_ID=? AND TERMINAL_ID IN(SELECT CHILD_TERMINAL_ID FROM FS_FR_TERMINAL_REGN WHERE PARENT_TERMINAL_ID=? ) AND QUOTE_TYPE=?";
      try
      {
         flag        =    validateTerminalId(dob);
          if(flag)
         {
            connection  =    getConnection();
            /*
            pst         =    connection.prepareStatement(viewEmailtextquery);
            pst.setString(1,dob.getTerminalId());
            pst.setString(2,dob.getQuoteType());
            */
            if("ADMN_TERMINAL".equalsIgnoreCase(dob.getLoginAccessType()))
            {
            if("A".equalsIgnoreCase(dob.getAccessType()))
            	pst         =    connection.prepareStatement(viewEmailtextqueryAdm);
            else if("O".equalsIgnoreCase(dob.getAccessType()))
            	pst         =    connection.prepareStatement(viewEmailtextqueryOpr);
            }
            else if("OPER_TERMINAL".equalsIgnoreCase(dob.getLoginAccessType()))
            {
            	pst         =    connection.prepareStatement(viewEmailtextqueryAdm);
            }

            else
                pst         =    connection.prepareStatement(viewEmailtextquery);
            
            if((("ADMN_TERMINAL".equalsIgnoreCase(dob.getLoginAccessType())) && ("A".equalsIgnoreCase(dob.getAccessType()) || "O".equalsIgnoreCase(dob.getAccessType()))) || ("OPER_TERMINAL".equalsIgnoreCase(dob.getLoginAccessType())))
            {
            	pst.setString(1,dob.getTerminalId());
            	pst.setString(2,dob.getLoginTerminal());
            	pst.setString(3,dob.getQuoteType());
            }
            else
            {
                pst.setString(1,dob.getTerminalId());
                pst.setString(2,dob.getQuoteType());

            }
            
            rs          = pst.executeQuery();
             if(rs.next())
            {            
              emailTextDOB     =  new QMSEmailTextDOB();
              emailTextDOB.setEmailText(rs.getString("TEXT"));
              emailTextDOB.setId(rs.getLong("ID"));
              emailTextDOB.setQuoteType(dob.getQuoteType());
              emailTextDOB.setModifiedBy(dob.getModifiedBy());
              
           }   
              emailTextDOB.setFlag(flag);
         }
     }
      catch(Exception e)
      {
          e.printStackTrace();
          logger.error(FILE_NAME+"Exception in adding EmailText");
          throw  new EJBException(e.toString());
      }
      finally
      {
         ConnectionUtil.closeConnection(connection,pst,rs);   
      }
      return emailTextDOB;
   }
    public int updateEmailTextDtls(QMSEmailTextDOB emailTextDOB)throws EJBException
   {
      Connection          connection  = null;
      PreparedStatement   pst         = null;
      String  updateEmailtextquery    = "UPDATE QMS_EMAIL_TEXT_MASTER SET TEXT=?,MODIFIED_DATE=?,MODIFIED_BY  = ? WHERE  ID = ?";
      int i =0 ;
      try
      {
          StringReader  reader  = new StringReader(emailTextDOB.getEmailText());
          connection           = getConnection();
          pst                  = connection.prepareStatement(updateEmailtextquery); 
          pst.setCharacterStream(1,reader,emailTextDOB.getEmailText().length());
          pst.setTimestamp(2,new java.sql.Timestamp((new java.util.Date()).getTime()));
          pst.setString(3,emailTextDOB.getModifiedBy());
          pst.setLong(4,emailTextDOB.getId());
         
          i = pst.executeUpdate();
          operationsImpl.setTransactionDetails(emailTextDOB.getTerminalId(),emailTextDOB.getModifiedBy(),"Email Text Master","",new java.sql.Timestamp((new java.util.Date()).getTime()),"Modify");
          
    }
      
      catch(Exception e)
      {
          e.printStackTrace();
          i = 0;
          logger.error(FILE_NAME+"Exception in updating the values");
          throw  new EJBException(e.toString());
      }
      finally
      {
         ConnectionUtil.closeConnection(connection,pst);   
      }
      return  i;
  }
    public int deleteEmailTextDtls(QMSEmailTextDOB emailTextDOB)throws RemoteException,EJBException
    {
        Connection          connection            = null;
        PreparedStatement   pst                   = null;
        String              deleteEmailtextquery  = "DELETE FROM QMS_EMAIL_TEXT_MASTER WHERE ID = ?";
        int i = 0;
        try
        {
            connection  = getConnection();
            pst         = connection.prepareStatement(deleteEmailtextquery);
            pst.setLong(1,emailTextDOB.getId());
            i          = pst.executeUpdate();
        }
       catch(Exception e)
      {
          e.printStackTrace();
          i = 0;
          logger.error(FILE_NAME+"Exception in deleting  the record");
          throw  new EJBException(e.toString());
      }
      finally
      {
          ConnectionUtil.closeConnection(connection,pst);
      }
      return  i;
    }
   private boolean  validateTerminalId(QMSEmailTextDOB dob)throws RemoteException,EJBException
   {
       Connection          connection  = null;
       PreparedStatement   pst         = null; 
       ResultSet           rs          = null;
       boolean      flag = false ;
       String       validateTerminalidquery =  "SELECT COUNT(*)  FROM FS_FR_TERMINALMASTER WHERE OPER_ADMIN_FLAG =? AND INVALIDATE='F' AND TERMINALID = ?";
         try
       {
           connection   =   getConnection();
           pst          =   connection.prepareStatement(validateTerminalidquery);
           pst.setString(1,dob.getAccessType());
           pst.setString(2, dob.getTerminalId());
           rs     =   pst.executeQuery();
           if(rs.next())
           {
            if(rs.getInt("COUNT(*)")>0)
            {
              flag  = true;
            }
           }
       }
        catch(Exception e)
      {
          e.printStackTrace();
          logger.error(FILE_NAME+"Exception in validating  the terminalId");
          throw  new EJBException(e.toString());
      }
      finally
      {
          ConnectionUtil.closeConnection(connection,pst,rs);
      }
          return flag;
    }
 //@@WPBN issue-61295
 //@@Added by Kameswari for the WPBN issue-61289
   public int addAttachmentDtls(QMSAttachmentDOB attachmentDOB)throws EJBException
   {
      Connection                connection          = null;
      PreparedStatement         pst                 = null; 
      //PreparedStatement         pst1                 = null; //Commented By RajKumari on 30-10-2008 for Connection Leakages.
     // PreparedStatement         pst2                 = null; //Commented By RajKumari on 30-10-2008 for Connection Leakages.
     // ResultSet                 rs                  = null;//Commented By RajKumari on 30-10-2008 for Connection Leakages.
    //  ResultSet                 rs1                  = null;//Commented By RajKumari on 30-10-2008 for Connection Leakages.
      QMSAttachmentDetailDOB    detailDOB           = null;
      QMSAttachmentFileDOB      fileDOB             = null; 
      CallableStatement         cstmt               = null;
      ArrayList           fieldsList                = new ArrayList(); 
      ArrayList           fileData                  = new ArrayList(); 
      String              addAttachmentIdQuery      = "INSERT INTO QMS_ATTACHMENT_MASTER(ATTACHMENT_ID,ACCESS_TYPE,TERMINAL_ID,INVALIDATE) VALUES(?,?,?,?)";
      String              addAttachmentDtlsQuery    = "INSERT INTO QMS_ATTACHMENT_DTL(ID,ATTACHMENT_ID ,DEFAULT_FLAG,SHIPMENT_MODE,CONSOLE_TYPE,QUOTE_TYPE,FROM_COUNTRY,FROM_LOCATION,TO_COUNTRY,TO_LOCATION,CARRIER_ID,SERVICE_LEVEL_ID,INDUSTRY_ID) VALUES(SEQ_ATTACHMENT_DTL_ID.NEXTVAL,?,?,?,?,?,?,?,?,?,?,?,?)";
      String              updateBlobQuery           = "UPDATE  QMS_ATTACHMENT_FILEDTL SET PDF_FILE=UTL_COMPRESS.LZ_COMPRESS(?)  WHERE ID=?";
      int i,p=0;
      Blob        blob   = null;
      byte[]      fileBytes = null;
      ArrayList   idList        = new ArrayList();
      long  id;
      String  longid = null;
       try
      {
          fieldsList      =  (ArrayList)attachmentDOB.getQmsAttachmentDetailDOBList();
          fileData        =  (ArrayList)attachmentDOB.getQmsAttachmentFileDOBList();
          connection      =   getConnection();
          pst             =   connection.prepareStatement(addAttachmentIdQuery);
          pst.setString(1,attachmentDOB.getAttachmentId());
          pst.setString(2,attachmentDOB.getAccessType());
          pst.setString(3,attachmentDOB.getTerminalId());
          pst.setString(4,attachmentDOB.getInvalidate());
          
          i = pst.executeUpdate();
          if(pst!=null)
            pst.close();
            
          pst       = connection.prepareStatement(addAttachmentDtlsQuery);
          int fldListSize	=	fieldsList.size();
           for(int k=0;k<fldListSize;k++)
          {
              detailDOB   = (QMSAttachmentDetailDOB)fieldsList.get(k);  
              if(detailDOB!=null)
              {
                  pst.setString(1,detailDOB.getAttachmentId());
                  pst.setString(2,detailDOB.getDefaultFlag());
                  pst.setString(3,detailDOB.getShipmentMode());
                  pst.setString(4,detailDOB.getConsoleType());
                  pst.setString(5,detailDOB.getQuoteType());
                  pst.setString(6,detailDOB.getFromCountry());
                  pst.setString(7,detailDOB.getFromLocation());
                  pst.setString(8,detailDOB.getToCountry());
                  pst.setString(9,detailDOB.getToLocation());
                  pst.setString(10,detailDOB.getCarrierId());
                  pst.setString(11,detailDOB.getServiceLevelId());
                  pst.setString(12,detailDOB.getIndustry());
                  pst.addBatch();
                }
          }
          pst.executeBatch();
           if(pst!=null)
               pst.close();
          
          
         cstmt =    connection.prepareCall ("BEGIN INSERT INTO QMS_ATTACHMENT_FILEDTL(ID,ATTACHMENT_ID ,PDF_FILE,PDF_FILENAME) "+
                                           "VALUES (SEQ_ATTACHMENT_FILEDTL_ID.NEXTVAL,?,EMPTY_BLOB(),?) RETURNING PDF_FILE,ID INTO ?,?; END;");
           pst       = connection.prepareStatement(updateBlobQuery);
           int fldDataSize	=	fileData.size();
          for(int j=0;j<fldDataSize;j++)
          {
              fileDOB   =(QMSAttachmentFileDOB)fileData.get(j);
              if(fileDOB!=null)
              {
                 
               cstmt.setString(1, fileDOB.getAttachmentId());
               cstmt.setString(2, fileDOB.getFileName());
               cstmt.registerOutParameter(3, Types.BLOB);
               cstmt.registerOutParameter(4, Types.BIGINT);
               cstmt.execute();
               blob  = cstmt.getBlob(3);
               id    = cstmt.getLong(4);
               FileInputStream  fileInputStream  = new FileInputStream(fileDOB.getPdfFile());
               int noofBytes   =  fileInputStream.available();
               byte[] buffer = new byte[noofBytes];
               fileInputStream.read(buffer);
               blob.setBytes(1,buffer);
               pst.setBlob(1,blob);
               pst.setLong(2,id);
               pst.executeUpdate();
               fileInputStream.close();
              }
          }
          if(cstmt!=null)
            cstmt.close();
      }
      catch(Exception e)
      {
          e.printStackTrace();
          logger.error(FILE_NAME+"Exception in inserting the values");
          throw  new EJBException(e.toString());
      }
      finally
      {
          ConnectionUtil.closeConnection(connection,pst,null);//Modified By RajKumari on 30-10-2008 for Connection Leakages.
      }
            return i;  
  }
 public int validateFields(QMSAttachmentDOB attachmentDOB)throws EJBException
 {
   Connection       connection   = null;
    PreparedStatement       pst   = null;
    ResultSet               rs    = null;
    String    terminalIdQuery     = "SELECT COUNT(*) FROM FS_FR_TERMINALMASTER WHERE OPER_ADMIN_FLAG =? AND INVALIDATE='F' AND TERMINALID = ?";
    String    attachmentIdQuery   = "SELECT COUNT(*) CNT FROM QMS_ATTACHMENT_MASTER WHERE ATTACHMENT_ID=?";
    int i = 0;
    try
    {
        connection  = getConnection();
        pst         = connection.prepareStatement(terminalIdQuery);
        pst.setString(1,attachmentDOB.getAccessType());
        pst.setString(2,attachmentDOB.getTerminalId());
        rs          = pst.executeQuery();
        if(rs.next())
        {
          if(rs.getInt("COUNT(*)")>0) //@@Added by Kameswari for the WPBN issue-145843
              i++;
        }
        if(rs!=null)
           rs.close();
        if(pst!=null)
           pst.close();
        pst         = connection.prepareStatement(attachmentIdQuery);
        pst.setString(1,attachmentDOB.getAttachmentId());
        rs          = pst.executeQuery();
        if(rs.next())
        {
           if(rs.getInt("CNT")==0)
           i = i+2;
          }    
    }
     catch(Exception e)
      {
          e.printStackTrace();
          logger.error(FILE_NAME+"Exception in validating termiinalId and attachmentId");
          throw  new EJBException(e.toString());
      }
      finally
      {
          ConnectionUtil.closeConnection(connection,pst,rs);
      }
               return i;  
   }
 public QMSAttachmentDOB viewAttachmentDtls(QMSAttachmentDOB attachmentDOB)throws EJBException 
 {
    Connection         connection      = null;
    PreparedStatement       pst        = null;
    ResultSet               rs         = null; 
    ResultSetMetaData       rmd        = null;
    QMSAttachmentDOB         dob       = null;
    QMSAttachmentDetailDOB   detailDOB = null;
    QMSAttachmentFileDOB     fileDOB   = null;
    ArrayList               fieldsList = new ArrayList();
    ArrayList               filesList  = new ArrayList();
    ArrayList               file       = new ArrayList();  
    int noofcols;
    String     viewAttachmentDtlQuery  = "SELECT DEFAULT_FLAG,SHIPMENT_MODE,CONSOLE_TYPE,QUOTE_TYPE,FROM_COUNTRY,FROM_LOCATION,TO_COUNTRY,TO_LOCATION,CARRIER_ID,SERVICE_LEVEL_ID,INDUSTRY_ID FROM QMS_ATTACHMENT_DTL WHERE ATTACHMENT_ID=?";
    //COMMENTED AND ADDED BY SUBRAHMANYAM FOR 188000 ON 29/10/09
    //String     viewAttachmentFileQuery = "SELECT PDF_FILENAME ,ID FROM QMS_ATTACHMENT_FILEDTL WHERE ATTACHMENT_ID=?"; 
    String     viewAttachmentFileQuery = "SELECT AD.PDF_FILENAME ,AD.ID FROM QMS_ATTACHMENT_FILEDTL AD,QMS_ATTACHMENT_MASTER AM  WHERE AD.ATTACHMENT_ID=AM.ATTACHMENT_ID AND AM.INVALIDATE='F' AND AD.ATTACHMENT_ID=?"; 
      try
    {
        dob          =   new QMSAttachmentDOB();
        connection  = getConnection();
        pst         = connection.prepareStatement(viewAttachmentDtlQuery);
        pst.setString(1,attachmentDOB.getAttachmentId());
        rs    =   pst.executeQuery();
        while(rs.next())
        {
           detailDOB    =   new QMSAttachmentDetailDOB();
           detailDOB.setDefaultFlag(rs.getString("DEFAULT_FLAG"));
           detailDOB.setShipmentMode(rs.getString("SHIPMENT_MODE"));
           detailDOB.setConsoleType(rs.getString("CONSOLE_TYPE"));
           detailDOB.setQuoteType(rs.getString("QUOTE_TYPE"));
           detailDOB.setFromCountry(rs.getString("FROM_COUNTRY"));
           detailDOB.setFromLocation(rs.getString("FROM_LOCATION"));
           detailDOB.setToCountry(rs.getString("TO_COUNTRY"));
           detailDOB.setToLocation(rs.getString("TO_LOCATION"));
           detailDOB.setCarrierId(rs.getString("CARRIER_ID"));
           detailDOB.setServiceLevelId(rs.getString("SERVICE_LEVEL_ID"));
           detailDOB.setIndustry(rs.getString("INDUSTRY_ID"));
      
           fieldsList.add(detailDOB);
         }
          if(rs!=null)
             rs.close();
          if(pst!=null)
             pst.close();
            pst         =   connection.prepareStatement(viewAttachmentFileQuery);
            pst.setString(1,attachmentDOB.getAttachmentId());
            rs          =   pst.executeQuery();
            while(rs.next())
            {
                 fileDOB      =   new QMSAttachmentFileDOB();
                 fileDOB.setFileName(rs.getString("PDF_FILENAME"));
                 fileDOB.setId(rs.getLong("ID"));
                  filesList.add(fileDOB);
            } 
            dob.setQmsAttachmentFileDOBList(filesList);
            dob.setAccessType(attachmentDOB.getAccessType()); 
            dob.setTerminalId(attachmentDOB.getTerminalId());
            dob.setAttachmentId(attachmentDOB.getAttachmentId()); 
            dob.setQmsAttachmentDetailDOBList(fieldsList);
   
      }
    catch(Exception e)
    {
        e.printStackTrace();
        logger.error(FILE_NAME+"Exception in retrieving the values");
        throw  new EJBException(e.toString());
    }
    finally
    {
          ConnectionUtil.closeConnection(connection,pst,rs);
    }
      return dob;
 }
  public byte[] viewPDFFile(long uniqueId)throws EJBException
  {
      Connection              connection             = null;
      PreparedStatement       pst                    = null; 
      CallableStatement       cstmt                  = null;
      ResultSet               rs                     = null;  
      String                  viewPDFFilequery       = "SELECT PDF_FILE FROM QMS_ATTACHMENT_FILEDTL WHERE ID=?";
      Blob                    blob                   = null;
      byte[]                  buffer                 = null;
      String                  uncompressedFilequery  = "{?=call UTL_COMPRESS.LZ_UNCOMPRESS(?)}";
     try
      {
         connection     =   getConnection();
         pst            =   connection.prepareStatement(viewPDFFilequery);
         pst.setLong(1,uniqueId);
       
          rs             =   pst.executeQuery();
           if(rs.next())     
         {
             blob  =  rs.getBlob("PDF_FILE");
         }
          cstmt         = connection.prepareCall(uncompressedFilequery);
          cstmt.registerOutParameter(1,OracleTypes.BLOB);
          cstmt.setBlob(2,blob);      
          cstmt.execute();
          blob  =  cstmt.getBlob(1);
        
          buffer  =  blob.getBytes(1,(int)blob.length());
          if(cstmt!=null)
            cstmt.close();
      }
      catch(Exception e)
     {
        e.printStackTrace();
        logger.error(FILE_NAME+"Exception in retrieving the pdf file");
        throw  new EJBException(e.toString());
    }
    finally
    {
          ConnectionUtil.closeConnection(connection,pst,rs);
    }
      return buffer;
     
 }
 public int updateAttachmentDtls(QMSAttachmentDOB attachmentDOB)throws EJBException
 {
      Connection                connection                    = null;
      PreparedStatement         pst                           = null;  
      QMSAttachmentDetailDOB    detailDOB                     = null;
      String                    deleteAttachmentdtlquery      = "DELETE FROM QMS_ATTACHMENT_DTL WHERE ATTACHMENT_ID=?";
      String                    updateAttachmentMasterQuery   = "UPDATE  QMS_ATTACHMENT_MASTER SET ACCESS_TYPE= ?,TERMINAL_ID=? WHERE ATTACHMENT_ID=?";
      String                    addAttachmentDtlsQuery        = "INSERT INTO QMS_ATTACHMENT_DTL(ID,ATTACHMENT_ID ,DEFAULT_FLAG,SHIPMENT_MODE,CONSOLE_TYPE,QUOTE_TYPE,FROM_COUNTRY,FROM_LOCATION,TO_COUNTRY,TO_LOCATION,CARRIER_ID,SERVICE_LEVEL_ID,INDUSTRY_ID) VALUES(SEQ_ATTACHMENT_DTL_ID.NEXTVAL,?,?,?,?,?,?,?,?,?,?,?,?)";
       ArrayList                fieldsList                    =  new ArrayList(); 
        int i = 0;
       try
      {
          connection      =  getConnection();
          fieldsList      =  (ArrayList)attachmentDOB.getQmsAttachmentDetailDOBList();
          pst              =    connection.prepareStatement(deleteAttachmentdtlquery);
          pst.setString(1,attachmentDOB.getAttachmentId());
          i                =    pst.executeUpdate();
          if(pst!=null)
             pst.close();
         
           connection      =   getConnection();
           pst             =   connection.prepareStatement(updateAttachmentMasterQuery);
           pst.setString(1,attachmentDOB.getAccessType());
           pst.setString(2,attachmentDOB.getTerminalId());
            pst.setString(3,attachmentDOB.getAttachmentId());
           i = pst.executeUpdate();
           if(pst!=null)
             pst.close();
          
           pst       = connection.prepareStatement(addAttachmentDtlsQuery);
           int fldListSize	=	fieldsList.size();
          for(int k=0;k<fldListSize;k++)
           {
              detailDOB   = (QMSAttachmentDetailDOB)fieldsList.get(k);  
              if(detailDOB!=null)
              {
                  pst.setString(1,detailDOB.getAttachmentId());
                  pst.setString(2,detailDOB.getDefaultFlag());
                  pst.setString(3,detailDOB.getShipmentMode());
                  pst.setString(4,detailDOB.getConsoleType());
                  pst.setString(5,detailDOB.getQuoteType());
                  pst.setString(6,detailDOB.getFromCountry());
                  pst.setString(7,detailDOB.getFromLocation());
                  pst.setString(8,detailDOB.getToCountry());
                  pst.setString(9,detailDOB.getToLocation());
                  pst.setString(10,detailDOB.getCarrierId());
                  pst.setString(11,detailDOB.getServiceLevelId());
                  pst.setString(12,detailDOB.getIndustry());
                  pst.addBatch();
               }
            }
            pst.executeBatch();
        
     }
      catch(Exception e)
      {
          e.printStackTrace();
          logger.error(FILE_NAME+"Exception in updating the values");
          throw  new EJBException(e.toString());
      }
      finally
      {
          ConnectionUtil.closeConnection(connection,pst);
      }
            return i;  
   }
  
public int deleteAttachmentDtls(QMSAttachmentDOB attachmentDOB)throws EJBException
{
      Connection                connection                    = null;
      PreparedStatement         pst                           = null;  
      String                    deleteAttachmentmasterquery   = "DELETE FROM QMS_ATTACHMENT_MASTER WHERE ATTACHMENT_ID=?";
      String                    deleteAttachmentdtlquery      = "DELETE FROM QMS_ATTACHMENT_DTL WHERE ATTACHMENT_ID=?";
      String                    deleteAttachedFilequery       = "DELETE FROM QMS_ATTACHMENT_FILEDTL WHERE ATTACHMENT_ID=?";
      String                    deleteAttachmentIdquery        = "DELETE FROM QMS_QUOTE_ATTACHMENTDTL WHERE ATTACHMENT_ID=?";   
      int i = 0;
      try
      {
          connection       =    getConnection();
          pst              =    connection.prepareStatement(deleteAttachedFilequery);
          pst.setString(1,attachmentDOB.getAttachmentId());
          pst.executeUpdate();
          if(pst!=null)
             pst.close();
          pst              =    connection.prepareStatement(deleteAttachmentdtlquery);
          pst.setString(1,attachmentDOB.getAttachmentId());
           pst.executeUpdate();
          if(pst!=null)
             pst.close();
             
           pst              =    connection.prepareStatement(deleteAttachmentIdquery);   
           pst.setString(1,attachmentDOB.getAttachmentId());
            pst.executeUpdate();
           
           pst              =    connection.prepareStatement(deleteAttachmentmasterquery);   
           pst.setString(1,attachmentDOB.getAttachmentId());
           i                =    pst.executeUpdate();
           
            if(pst!=null)
             pst.close();
      }
      catch(Exception e)
      {
          e.printStackTrace();
          logger.error(FILE_NAME+"Exception in deleting the values");
          throw  new EJBException(e.toString());
      }
      finally
      {
          ConnectionUtil.closeConnection(connection,pst);
      }
            return i;  
  }
//Commented & Added by subrahmanyam for the pbn id:203350
//  public ArrayList viewAllAttachmentDtls()throws EJBException
  public ArrayList viewAllAttachmentDtls(String loginAccessType,String loginTerminal)throws EJBException
  {
      QMSAttachmentDOB          attachmentDOB  = null;
      QMSAttachmentDetailDOB    detailDOB      = null;
      QMSAttachmentFileDOB      fileDOB        = null;
      Connection                connection     = null;
      PreparedStatement         pst            = null;  
      ResultSet                 rs             = null;
      ResultSet                 rsdtls         = null;
      ResultSet                 rsfile         = null;
      ArrayList                 filelist       = null;
      ArrayList                 viewList       = new ArrayList();
      ArrayList                 detailList     = new ArrayList();
      String                    fromCountry    = null;
      String                    fromLocation   = null;
      String                    toCountry      = null;
      String                    toLocation     = null;
      String                    carrierId      = null;
      String                    serviceLevelId = null;
      String                    industryId     = null;
      String                    fileName       = "";
      String                    uniqueId       = "";
      String                    viewAllQuery   = "SELECT ATTACHMENT_ID, TERMINAL_ID, INVALIDATE, CURSOR (SELECT ID, FROM_COUNTRY, FROM_LOCATION ,TO_COUNTRY ,TO_LOCATION,"+ 
                                                 "CARRIER_ID, SERVICE_LEVEL_ID , INDUSTRY_ID  FROM QMS_ATTACHMENT_DTL  WHERE ATTACHMENT_ID = MAS.ATTACHMENT_ID) DETAILS"+
                                                 ",CURSOR (SELECT ID,PDF_FILENAME  FROM QMS_ATTACHMENT_FILEDTL WHERE ATTACHMENT_ID = MAS.ATTACHMENT_ID) FILE_DTL FROM QMS_ATTACHMENT_MASTER MAS";
      
      String                    viewAllQueryAdm   = "SELECT ATTACHMENT_ID, TERMINAL_ID, INVALIDATE, CURSOR (SELECT ID, FROM_COUNTRY, FROM_LOCATION ,TO_COUNTRY ,TO_LOCATION,"+ 
      											 "CARRIER_ID, SERVICE_LEVEL_ID , INDUSTRY_ID  FROM QMS_ATTACHMENT_DTL  WHERE ATTACHMENT_ID = MAS.ATTACHMENT_ID) DETAILS"+
      											 ",CURSOR (SELECT ID,PDF_FILENAME  FROM QMS_ATTACHMENT_FILEDTL WHERE ATTACHMENT_ID = MAS.ATTACHMENT_ID) FILE_DTL FROM QMS_ATTACHMENT_MASTER MAS "+
      											 " WHERE MAS.TERMINAL_ID IN (SELECT CHILD_TERMINAL_ID FROM FS_FR_TERMINAL_REGN WHERE PARENT_TERMINAL_ID = ?  UNION "+
      											 " SELECT TERMINALID  FROM FS_FR_TERMINALMASTER  WHERE TERMINALID = ?)";
      
      String                    viewAllQueryOpr   = "SELECT ATTACHMENT_ID, TERMINAL_ID, INVALIDATE, CURSOR (SELECT ID, FROM_COUNTRY, FROM_LOCATION ,TO_COUNTRY ,TO_LOCATION,"+ 
      												"CARRIER_ID, SERVICE_LEVEL_ID , INDUSTRY_ID  FROM QMS_ATTACHMENT_DTL  WHERE ATTACHMENT_ID = MAS.ATTACHMENT_ID) DETAILS"+
      												",CURSOR (SELECT ID,PDF_FILENAME  FROM QMS_ATTACHMENT_FILEDTL WHERE ATTACHMENT_ID = MAS.ATTACHMENT_ID) FILE_DTL FROM QMS_ATTACHMENT_MASTER MAS "+
      												" WHERE MAS.TERMINAL_ID IN (SELECT TERMINALID  FROM FS_FR_TERMINALMASTER WHERE TERMINALID = ?)";

      
     try
      {
            connection          =  getConnection();
          //Commented & Added by subrahmanyam for the pbn id:203350            
            //pst                 =  connection.prepareStatement(viewAllQuery);
            // rs                  =  pst.executeQuery();
            if("ADMN_TERMINAL".equalsIgnoreCase(loginAccessType))
            {
          	  pst                 =  connection.prepareStatement(viewAllQueryAdm);  
          	  pst.setString(1, loginTerminal);
          	  pst.setString(2, loginTerminal);
            }
            else if("OPER_TERMINAL".equalsIgnoreCase(loginAccessType))
            {
          	  pst                 =  connection.prepareStatement(viewAllQueryOpr);
          	  pst.setString(1, loginTerminal);
            }
            else
            	  pst                 =  connection.prepareStatement(viewAllQuery);
          //ended 203350
          rs                  =  pst.executeQuery();
          while(rs.next())
          {
              attachmentDOB    =  new QMSAttachmentDOB();
             attachmentDOB.setAttachmentId(rs.getString("ATTACHMENT_ID"));
             attachmentDOB.setTerminalId(rs.getString("TERMINAL_ID"));
             attachmentDOB.setInvalidate(rs.getString("INVALIDATE"));
             rsdtls          = (ResultSet)rs.getObject("DETAILS");
            while(rsdtls.next())
            {
            
             if(rsdtls.getString("FROM_COUNTRY")!=null)
            {
             if(fromCountry!=null)
                fromCountry  = fromCountry+","+rsdtls.getString("FROM_COUNTRY");
              else 
                fromCountry = rsdtls.getString("FROM_COUNTRY");
                }
            if(rsdtls.getString("FROM_LOCATION")!=null)
            {
              if(fromLocation!=null)
                fromLocation  = fromLocation+","+rsdtls.getString("FROM_LOCATION");
              else 
                fromLocation = rsdtls.getString("FROM_LOCATION");
            }
            if(rsdtls.getString("TO_COUNTRY")!=null)
            {
              if(toCountry!=null)
                toCountry  = toCountry+","+rsdtls.getString("TO_COUNTRY");
              else 
                toCountry = rsdtls.getString("TO_COUNTRY");  
            }
            if(rsdtls.getString("TO_LOCATION")!=null)
            {
               if(toLocation!=null)
                toLocation  = toLocation+","+rsdtls.getString("TO_LOCATION");
             else 
                toLocation = rsdtls.getString("TO_LOCATION");
            }
            if(rsdtls.getString("CARRIER_ID")!=null)
            {
                if(carrierId!=null)
                carrierId  = carrierId+","+rsdtls.getString("CARRIER_ID");
              else 
                carrierId = rsdtls.getString("CARRIER_ID");
            }
            if(rsdtls.getString("SERVICE_LEVEL_ID")!=null)
            {
              if(serviceLevelId!=null)
                 serviceLevelId  = serviceLevelId+","+rsdtls.getString("SERVICE_LEVEL_ID");
              else 
                 serviceLevelId = rsdtls.getString("SERVICE_LEVEL_ID");
            }
            if(rsdtls.getString("INDUSTRY_ID")!=null)
            {
              if(industryId!=null)
                 industryId  = industryId+","+rsdtls.getString("INDUSTRY_ID");
              else 
                 industryId = rsdtls.getString("INDUSTRY_ID");
             } 
            }
              detailDOB         = new QMSAttachmentDetailDOB();
               detailDOB.setFromCountry(fromCountry);
              fromCountry=null;
              detailDOB.setFromLocation(fromLocation);
               fromLocation=null;
              detailDOB.setToCountry(toCountry);
               toCountry=null;
              detailDOB.setToLocation(toLocation);
               toLocation=null;
              detailDOB.setCarrierId(carrierId);
               carrierId=null;
              detailDOB.setServiceLevelId(serviceLevelId);
               serviceLevelId=null;
              detailDOB.setIndustry(industryId);
               industryId=null;
              detailList.add(detailDOB);
              attachmentDOB.setQmsAttachmentDetailDOBList(detailList);
             rsfile = (ResultSet)rs.getObject("FILE_DTL");
            filelist          = new ArrayList();
          while(rsfile.next())
          {
                 fileDOB           = new QMSAttachmentFileDOB();
                 fileDOB.setFileName(rsfile.getString("PDF_FILENAME"));
                 fileDOB.setId(rsfile.getLong("ID"));
                 filelist.add(fileDOB);
          }
           attachmentDOB.setQmsAttachmentFileDOBList(filelist);
           viewList.add(attachmentDOB);
        }
      }
       catch(Exception e)
      {
          e.printStackTrace();
          logger.error(FILE_NAME+"Exception in retrieving the values");
          throw  new EJBException(e.toString());
      }
      finally
      {
          ConnectionUtil.closeConnection(connection,pst,rs);//Modified By RajKumari on 30-10-2008 for Connection Leakages.
          ConnectionUtil.closeStatement(null,rsdtls);//Added By RajKumari on 30-10-2008 for Connection Leakages.
          ConnectionUtil.closeStatement(null,rsfile);//Added By RajKumari on 30-10-2008 for Connection Leakages.
      }
      return viewList;
  }
  public ArrayList attachmentIdList(QMSAttachmentDOB dob)throws EJBException
  {
      Connection                connection                    = null;
      PreparedStatement         pst                           = null;  
      ResultSet                 rs                            = null;
      ArrayList                 attachmentIdList              = new ArrayList();
      //@@ COMMENTED AND MODIFIED BY SUBRAHMANYAM FOR THE PBN ID: 188000 ON 29/OCT/09
     // String                    attachmentIdListQuery         = "SELECT ATTACHMENT_ID FROM QMS_ATTACHMENT_MASTER WHERE ACCESS_TYPE=? AND TERMINAL_ID=? AND ATTACHMENT_ID LIKE '"+dob.getAttachmentId()+"%'"; 
     String                    attachmentIdListQuery         = "SELECT ATTACHMENT_ID FROM QMS_ATTACHMENT_MASTER WHERE INVALIDATE='F' AND  ACCESS_TYPE=? AND TERMINAL_ID=? AND ATTACHMENT_ID LIKE '"+dob.getAttachmentId()+"%'";
     //@@Added by subrahmanyam for the pbn id: 203350 on 21-APR-10
     String                    attachmentIdListQueryOpr         = "SELECT ATTACHMENT_ID FROM QMS_ATTACHMENT_MASTER WHERE INVALIDATE='F' AND  ACCESS_TYPE=? AND TERMINAL_ID=? AND TERMINAL_ID IN(SELECT CHILD_TERMINAL_ID FROM FS_FR_TERMINAL_REGN WHERE PARENT_TERMINAL_ID=?)AND ATTACHMENT_ID LIKE '"+dob.getAttachmentId()+"%'";
     String                    attachmentIdListQueryAdm         = "SELECT ATTACHMENT_ID FROM QMS_ATTACHMENT_MASTER WHERE INVALIDATE='F' AND  ACCESS_TYPE=? AND TERMINAL_ID=? AND TERMINAL_ID IN(SELECT TERMINALID FROM FS_FR_TERMINALMASTER WHERE TERMINALID=?) AND ATTACHMENT_ID LIKE '"+dob.getAttachmentId()+"%'";
        try
      {
          connection  = getConnection();
      //@@Commented & Added by subrahmanyam for the pbn id: 203350 on 21-APR-10          
          /*
				pst         = connection.prepareStatement(attachmentIdListQuery);
				pst.setString(1,dob.getAccessType());
        	    pst.setString(2,dob.getTerminalId());
           */
          System.out.println("loginTerminal..."+dob.getLoginTerminal());
          System.out.println("LoginAccess..."+dob.getLoginAccessType());
          System.out.println("accessType.."+dob.getAccessType());
          if("ADMN_TERMINAL".equalsIgnoreCase(dob.getLoginAccessType())){
          if("O".equalsIgnoreCase(dob.getAccessType()))
              pst         = connection.prepareStatement(attachmentIdListQueryOpr);
          else
        	  pst         = connection.prepareStatement(attachmentIdListQueryAdm);
          }
          else if("OPER_TERMINAL".equalsIgnoreCase(dob.getLoginAccessType()))
        	  pst         = connection.prepareStatement(attachmentIdListQueryAdm);
          else
        	  pst         = connection.prepareStatement(attachmentIdListQuery);
          
          if("ADMN_TERMINAL".equalsIgnoreCase(dob.getLoginAccessType()) || "OPER_TERMINAL".equalsIgnoreCase(dob.getLoginAccessType()))
          {
        	  	pst.setString(1,dob.getAccessType());
          		pst.setString(2,dob.getTerminalId());
          		pst.setString(3,dob.getLoginTerminal());
          }
          else{
        	      pst.setString(1,dob.getAccessType());
        	      pst.setString(2,dob.getTerminalId());
          }
          //@@Ended by subrahmanyam for the pbn id: 203350 on 21-APR-10          
          rs    =   pst.executeQuery();
          while(rs.next())
              attachmentIdList.add(rs.getString("ATTACHMENT_ID"));  
      }
      catch(Exception e)
     {
          e.printStackTrace();
          logger.error(FILE_NAME+"Exception in retreiving the attachmentIdList");
          throw  new EJBException(e.toString());
      }
      finally
      {
          ConnectionUtil.closeConnection(connection,pst,rs);
      }
      return attachmentIdList;
  }
   public ArrayList attachFile(QMSAttachmentFileDOB dob)throws EJBException
   {
       Connection                connection                = null;
       PreparedStatement         pst                       = null;  
       CallableStatement         cstmt                     = null;
       //ResultSet                 rs                        = null;//Commented By RajKumari on 30-10-2008 for Connection Leakages.
       String                    attachFileQuery           ="INSERT INTO QMS_ATTACHMENT_FILEDTL(ID,ATTACHMENT_ID,PDF_FILENAME,PDF_FILE) VALUES(SEQ_ATTACHMENT_FILEDTL_ID.NEXTVAL,?,?,?)"; 
       ArrayList                 filesList                 = new ArrayList(); 
      // QMSAttachmentFileDOB      fileDOB                   = null;
       String                   updateBlobQuery            ="UPDATE  QMS_ATTACHMENT_FILEDTL SET PDF_FILE=UTL_COMPRESS.LZ_COMPRESS(?)  WHERE ID=?";
       int i = 0;
       Blob         blob    = null;
       long id;
       try
       {
          //FileInputStream  fileInputStream  = new FileInputStream(dob.getPdfFile());
          connection  = getConnection();
          /*pst         = connection.prepareStatement(attachFileQuery);
          pst.setString(1,dob.getAttachmentId());
          pst.setString(2,dob.getFileName());
          pst.setBinaryStream(3,fileInputStream,fileInputStream.available());
          i = pst.executeUpdate();*/
            cstmt =    connection.prepareCall ("BEGIN INSERT INTO QMS_ATTACHMENT_FILEDTL(ID,ATTACHMENT_ID ,PDF_FILE,PDF_FILENAME) "+
                                           "VALUES (SEQ_ATTACHMENT_FILEDTL_ID.NEXTVAL,?,EMPTY_BLOB(),?) RETURNING PDF_FILE,ID INTO ?,?; END;");
           pst       = connection.prepareStatement(updateBlobQuery);
         /* for(int j=0;j<fileData.size();j++)
          {
              fileDOB   =(QMSAttachmentFileDOB)fileData.get(j);
              if(fileDOB!=null)
              {*/
                 
               cstmt.setString(1, dob.getAttachmentId());
               cstmt.setString(2, dob.getFileName());
               cstmt.registerOutParameter(3, Types.BLOB);
               cstmt.registerOutParameter(4, Types.BIGINT);
               cstmt.execute();
               blob  = cstmt.getBlob(3);
               id    = cstmt.getLong(4);
               FileInputStream  fileInputStream  = new FileInputStream(dob.getPdfFile());
               int noofBytes   =  fileInputStream.available();
               byte[] buffer = new byte[noofBytes];
               fileInputStream.read(buffer);
               blob.setBytes(1,buffer);
               pst.setBlob(1,blob);
               pst.setLong(2,id);
               i=pst.executeUpdate();
               fileInputStream.close();
              //}
          //}
         if(cstmt!=null)
            cstmt.close();
          if(i>0)
            filesList     =   viewFile(dob.getAttachmentId());
          }
        catch(Exception e)
     {
          e.printStackTrace();
          logger.error(FILE_NAME+"Exception in inserting the file");
          throw  new EJBException(e.toString());
      }
      finally
      {
          ConnectionUtil.closeConnection(connection,pst);
      }
      return filesList;
   }
  public ArrayList deleteFile(ArrayList dobList)throws EJBException
  {
       Connection                connection                    = null;
       PreparedStatement         pst                           = null;  
       //ResultSet                 rs                            = null;//Commented By RajKumari on 30-10-2008 for Connection Leakages.
       QMSAttachmentFileDOB      fileDOB                       = null; 
       ArrayList                 filelist                      = new ArrayList(); 
       String                    attachmentId                  = null;
       //String                     getPDFile                    ="SELECT PDF_FILE FROM QMS_ATTACHMENT_FILEDTL WHERE ID=?";
       String                    deleteFileQuery               ="DELETE FROM QMS_ATTACHMENT_FILEDTL WHERE ID=?"; 
       int i = 0;
       try
       {
          connection      = getConnection();
          pst             = connection.prepareStatement(deleteFileQuery);
          int dobListSize	=	dobList.size();
          for(int j=0;j<dobListSize;j++)
          {
             fileDOB       = (QMSAttachmentFileDOB)dobList.get(j);
             attachmentId  = fileDOB.getAttachmentId();
               pst.setLong(1,fileDOB.getId());
              i = pst.executeUpdate();
          }
          if(i>0)
             filelist     =   viewFile(attachmentId);
         }
        catch(Exception e)
     {
          e.printStackTrace();
          logger.error(FILE_NAME+"Exception in deleting the file");
          throw  new EJBException(e.toString());
      }
      finally
      {
          ConnectionUtil.closeConnection(connection,pst,null);//Modified By RajKumari on 30-10-2008 for Connection Leakages.
      }
      return filelist;
  }
   public ArrayList viewFile(String attachmentId)throws EJBException
   { 
       Connection                connection                    = null;
       PreparedStatement         pst                           = null;  
       ResultSet                 rs                            = null;
       QMSAttachmentFileDOB      fileDOB                       = null; 
       //String                    viewFileQuery                 ="SELECT ID,PDF_FILENAME FROM QMS_ATTACHMENT_FILEDTL WHERE ATTACHMENT_ID=?"; 
         String                    viewFileQuery                 ="SELECT ID,PDF_FILENAME FROM QMS_ATTACHMENT_FILEDTL WHERE ATTACHMENT_ID=?"; 
        String                    fileName                      = null;
       ArrayList                 filelist                     = new ArrayList(); 
        try
       {
          connection  = getConnection();
          pst         = connection.prepareStatement(viewFileQuery);
          pst.setString(1,attachmentId);
          rs    =   pst.executeQuery();
          while(rs.next())
          {
              fileDOB   =   new QMSAttachmentFileDOB();
              fileDOB.setId(rs.getLong("ID"));
              fileDOB.setFileName(rs.getString("PDF_FILENAME"));
              filelist.add(fileDOB);
          }
          }
        catch(Exception e)
       {
          e.printStackTrace();
          logger.error(FILE_NAME+"Exception in retreiving the file");
          throw  new EJBException(e.toString());
      }
      finally
      {
          ConnectionUtil.closeConnection(connection,pst,rs);
      }
      return filelist;
   }
   public int invalidateAttachmentId(ArrayList dobList)throws EJBException
   {
       Connection                connection                    = null;
       PreparedStatement         pst                           = null;  
       QMSAttachmentDOB          dob                           = null;
       String                    invalidateMasterQuery         ="UPDATE QMS_ATTACHMENT_MASTER SET INVALIDATE=? WHERE ATTACHMENT_ID=?"; 
       Blob b=null;
        int i = 0;
        try
       {
          connection        = getConnection();
          pst                 = connection.prepareStatement(invalidateMasterQuery);
          int dobListSize	=	dobList.size();
          for(int k=0;k<dobListSize;k++)
          {
             dob = (QMSAttachmentDOB)dobList.get(k);
             pst.setString(1,dob.getInvalidate());
             pst.setString(2,dob.getAttachmentId());
             i = pst.executeUpdate();
          }
         }
        catch(Exception e)
       {
          e.printStackTrace();
          logger.error(FILE_NAME+"Exception in invalidating  the attachmentId");
          throw  new EJBException(e.toString());
      }
      finally
      {
          ConnectionUtil.closeConnection(connection,pst);
      }
      return i;
   }
   public ArrayList  quoteAttachmentIdList()throws EJBException
   {
       Connection           connection              = null;
       PreparedStatement    pst                     = null;  
       ResultSet            rs                      = null;   
       ArrayList            quoteAttachmentIdList   = new ArrayList();
       String               quoteAttachmentIdQuery  = "SELECT ATTACHMENT_ID FROM QMS_QUOTE_ATTACHMENTDTL"; 
        try
        {
          connection    = getConnection();
          pst           = connection.prepareStatement(quoteAttachmentIdQuery);
          rs            = pst.executeQuery();
          while(rs.next())
          {
            quoteAttachmentIdList.add(rs.getString("ATTACHMENT_ID"));
          }
       }
       catch(Exception e)
       {
          e.printStackTrace();
          logger.error(FILE_NAME+"Exception in retreiving the attachmentIds in quote");
          throw  new EJBException(e.toString());
      }
      finally
      {
          ConnectionUtil.closeConnection(connection,pst,rs);
      }
      return quoteAttachmentIdList; 
    }
 //@@ WPBN issue-61289
 //@@Added by Kameswari for the WPBN issue-61314
 public ArrayList getCustomerAddress()throws EJBException
 {
    Connection         connection             = null;
    PreparedStatement  pst                    = null;
    ResultSet          rs                     = null;
    ArrayList          addressList            = new ArrayList();
   // String             customerAddressQuery   = "SELECT CUSTOMERID,CUSTOMERADDRESSID,ADDRESSLINE1,ADDRESSLINE2,STATE,ZIPCODE  FROM V_CUSTOMER_MASTER ";
    String             customerAddressQuery   = "SELECT CUSTOMERID,CUSTOMERADDRESSID,ADDRESSLINE1,ADDRESSLINE2,CITY,STATE,FS.COUNTRYNAME,ZIPCODE  FROM V_CUSTOMER_MASTER V,FS_COUNTRYMASTER FS WHERE FS.COUNTRYID=V.COUNTRYID ";
    try
    {
        connection        =  getConnection();
        pst               =  connection.prepareStatement(customerAddressQuery);
        rs                =  pst.executeQuery();
        while(rs.next())
        {
          
          addressList.add(rs.getString("CUSTOMERID"));
          addressList.add(rs.getString("CUSTOMERADDRESSID"));
          addressList.add(rs.getString("ADDRESSLINE1"));
          addressList.add(rs.getString("ADDRESSLINE2"));
          addressList.add(rs.getString("CITY"));
          addressList.add(rs.getString("STATE"));
          addressList.add(rs.getString("COUNTRYNAME"));
          addressList.add(rs.getString("ZIPCODE"));
          
        }
        
    }
    catch(Exception e)
    {
        e.printStackTrace();
        logger.error(FILE_NAME+"Exception in retreiving the customerAddress");
        throw  new EJBException(e.toString());
    }
    finally
    {
        ConnectionUtil.closeConnection(connection,pst,rs);
    }
      return addressList; 
 }
 //@@WPBN issue-61314
  //@@Added by Kameswari for the WPBN issue-66410
 public ArrayList getTerminalIdsforAttachmentMaster(String terminalType,String terminalId,String searchString)throws EJBException
 {
     ArrayList          terminalIdList         = new ArrayList();
     Connection         connection             = null;
     PreparedStatement  pst                    = null;
     ResultSet          rs                     = null;
     String             terminalIdQuery        = null;
     try
     {
         if("DHLCORP".equalsIgnoreCase(terminalId))
         //@@Commented and Modified by Sunil for LOV issue
         // terminalIdQuery  = "SELECT TERMINALID FROM FS_FR_TERMINALMASTER WHERE OPER_ADMIN_FLAG='"+terminalType+"'";
              terminalIdQuery  = "SELECT TERMINALID FROM FS_FR_TERMINALMASTER WHERE OPER_ADMIN_FLAG='"+terminalType+"' AND TERMINALID LIKE '"+searchString+"%' "; 
        else
        {
        	//@@ Modified by subrahmanyam for the pbn id: 203350 on 21-Apr-10
          if("O".equalsIgnoreCase(terminalType))
          /* terminalIdQuery  = "SELECT CHILD_TERMINAL_ID FROM FS_FR_TERMINAL_REGN WHERE PARENT_TERMINAL_ID='"+terminalId
                              +"' AND CHILD_TERMINAL_ID LIKE'"+searchString+"%' UNION SELECT TERMINALID FROM FS_FR_TERMINALMASTER WHERE TERMINALID='"+terminalId+"'";
          */
      	  terminalIdQuery  = "SELECT CHILD_TERMINAL_ID FROM FS_FR_TERMINAL_REGN WHERE PARENT_TERMINAL_ID='"+terminalId
              +"' AND CHILD_TERMINAL_ID LIKE'"+searchString+"%'";

          else
          /* terminalIdQuery = "SELECT TR.CHILD_TERMINAL_ID FROM FS_FR_TERMINAL_REGN TR,FS_FR_TERMINALMASTER TM WHERE"+ 
                              " TR.PARENT_TERMINAL_ID=(SELECT PARENT_TERMINAL_ID FROM FS_FR_TERMINAL_REGN WHERE"+
                              " CHILD_TERMINAL_ID='"+terminalId+"')AND TM.OPER_ADMIN_FLAG='A' AND TR.CHILD_TERMINAL_ID="+
                              " TM.TERMINALID  AND TR.CHILD_TERMINAL_ID LIKE'"+searchString+"%' UNION SELECT TERMINALID FROM FS_FR_TERMINALMASTER WHERE TERMINALID='"+terminalId+"'";
          
        */
        	  terminalIdQuery = "SELECT TERMINALID FROM FS_FR_TERMINALMASTER WHERE TERMINALID='"+terminalId+"' AND TERMINALID LIKE '"+searchString+"%'";
          //@@ Ended by subrahmanyam for the pbn id: 203350 
        }
        connection        =  getConnection();
        pst               =  connection.prepareStatement(terminalIdQuery);
        rs                =  pst.executeQuery();
        while(rs.next())
        {
          terminalIdList.add(rs.getString(1));
        }
         
     }
     catch(Exception e)
     {
        e.printStackTrace();
        logger.error(FILE_NAME+"Exception in retreiving the terminalIdlist");
        throw  new EJBException(e.toString());
     }
     finally
      {
          ConnectionUtil.closeConnection(connection,pst,rs);
      }
      return terminalIdList; 
 }
 

//@@Added by Kameswari for chargebasis enhancement
 public ArrayList getChargeDescList()throws EJBException
 {
     ArrayList          chargeDescList         = new ArrayList();
     Connection         connection             = null;
     PreparedStatement  pst                    = null;
     ResultSet          rs                     = null;
     String             chargeDescQuery        = "SELECT CHARGEDESCID FROM QMS_CHARGEDESCMASTER";
     try
     {
       connection        =  getConnection();
        pst               =  connection.prepareStatement(chargeDescQuery);
        rs                =  pst.executeQuery();
        while(rs.next())
        {
          chargeDescList.add(rs.getString(1));
        }  
     }
     catch(Exception e)
     {
        e.printStackTrace();
        logger.error(FILE_NAME+"Exception in retreiving the chargeDescList");
        throw  new EJBException(e.toString());
     }
     finally
      {
          ConnectionUtil.closeConnection(connection,pst,rs);
      }
      return chargeDescList; 
  }
  
  //Added by ashlesh for DHL CR 154393 on 23/01/2009
public StringBuffer uploadCustomerDetails(java.util.ArrayList customerUploadList,ESupplyGlobalParameters loginbean)
		throws com.foursoft.esupply.common.exception.FoursoftException,CodeCustNotDoneException{
    String         customerId = null;
    StringBuffer   customerIds= new StringBuffer();
  int custUpldListSize	=	customerUploadList.size();
  for(int i=0;i<custUpldListSize;i++){
      ArrayList list= (ArrayList)customerUploadList.get(i);
      CustomerModel customerModel=new CustomerModel();
      Address address  =new Address(); 
      customerModel=(CustomerModel)list.get(0);
      address       =(Address)list.get(1);
      ArrayList li=  (ArrayList)list.get(2);
      ArrayList customerList = new ArrayList();
      loginbean.setLocationId(customerModel.getTerminalId());//@@Added by Kameswari on 13/04/09
      customerId= createCustomerDetails(customerModel,address,loginbean,customerList);
      customerIds.append(customerId+",");
    }   
    return customerIds;

}
//@@ Added by subrahmanyam for Enhancement 167669 on 27/04/09
public ArrayList getTerminalList(String terminalId)throws EJBException
{
    ArrayList terminalList = new ArrayList();
    Connection         connection             = null;
    PreparedStatement  pst                    = null;
    ResultSet          rs                     = null;
    String             terminalQry            ="SELECT CHILD_TERMINAL_ID FROM FS_FR_TERMINAL_REGN TR WHERE TR.PARENT_TERMINAL_ID "+
                                                "IN (SELECT PARENT_TERMINAL_ID FROM FS_FR_TERMINAL_REGN WHERE CHILD_TERMINAL_ID=?) ORDER BY CHILD_TERMINAL_ID";
    String            accessLevelQry          = "SELECT OPER_ADMIN_FLAG FROM FS_FR_TERMINALMASTER  WHERE TERMINALID=?";
    String            adminCustomerQry        = "SELECT CHILD_TERMINAL_ID FROM FS_FR_TERMINAL_REGN WHERE PARENT_TERMINAL_ID=? ORDER BY CHILD_TERMINAL_ID";
    String            accessLevel             = "";
    try
     {
       connection        =  getConnection();
        pst               =  connection.prepareStatement(accessLevelQry);
        pst.setString(1,terminalId);
        rs                =  pst.executeQuery();
        if(rs.next())
        {
             accessLevel=rs.getString("OPER_ADMIN_FLAG");
        } 
        if(pst!=null)
        pst=null;
        if(rs!=null)
        rs=null;
        if("O".equalsIgnoreCase(accessLevel))
          pst               =  connection.prepareStatement(terminalQry);
        else
          pst               =  connection.prepareStatement(adminCustomerQry);
        pst.setString(1,terminalId);
        rs                =  pst.executeQuery();
        while(rs.next())
        {
             terminalList.add(rs.getString("CHILD_TERMINAL_ID"));
        }  
     }
     catch(Exception e)
     {
        e.printStackTrace();
        logger.error(FILE_NAME+"Exception While Getting  The Terminals");
        throw  new EJBException(e.toString());
     }
     finally
      {
          ConnectionUtil.closeConnection(connection,pst,rs);
      }
     
      return terminalList;
   }
//@@ Ended by subrahmanyam for Enhancement 167669 on 27/04/09

//added by phani sekhar for wpbn 167678
       public ArrayList getContactNamesforAttentionLOV(String customerId,String addressType,String quoteId,String flag) 
  {
    String  addressCheck    = "";
     Connection connection         = null;
    PreparedStatement stmt        = null;
    ResultSet rs                  = null;
    ArrayList records             = new ArrayList();
     String addressTypeQry =  "SELECT ADDRESS_TYPE FROM FS_FR_CUSTOMER_ADDRESS WHERE CUSTOMERADDRESSID = "+
                            "(SELECT CUSTOMER_ADDRESSID FROM QMS_QUOTE_MASTER WHERE CUSTOMER_ID = ? AND QUOTE_ID = ? AND ACTIVE_FLAG = 'A')";

    try
    {
     connection     =  this.getConnection();
     if("Costing".equals(flag))
     {
      stmt          =  connection.prepareStatement(addressTypeQry);
      stmt.setString(1,customerId);
      stmt.setString(2,quoteId);
      
      rs             =  stmt.executeQuery();
      
      if(rs.next())
      {
        addressType  =  rs.getString("ADDRESS_TYPE");
      }
      
      if(rs!=null)
          rs.close();
      if(stmt!=null)
          stmt.close();
     }
    if(addressType!=null && addressType.length()>0)
    {
      if(addressType.equalsIgnoreCase("P"))
       addressCheck=" AND ADDRTYPE='P' ";
      else if(addressType.equalsIgnoreCase("D"))
       addressCheck=" AND ADDRTYPE='D' ";
      else if(addressType.equalsIgnoreCase("B"))
       addressCheck=" AND ADDRTYPE='B' ";
      else
       addressCheck=" ";
    }
    //@@Added by kiran.v on 02/08/2011 for Wpbn Issue 262471
    String sql1=  "SELECT SL_NO,CONTACTPERSON,NVL(EMAILID,FAX),FAX,CUSTOMERID,CONTACTNO FROM QMS_CUST_CONTACTDTL WHERE CUSTOMERID=? "+
                  "AND CONTACTPERSON IS NOT NULL "+addressCheck+" AND ACTIVE_STATUS = 'A' ORDER BY SL_NO";
 	 
	 
   
    
     
      stmt           =  connection.prepareStatement(sql1);
      stmt.setString(1,customerId);
      rs             =  stmt.executeQuery();
  
      while(rs.next())
      {
      records.add(rs.getString(2)+","+rs.getString(3)+","+StringUtility.noNull(rs.getString(6))+","+StringUtility.noNull(rs.getString(4))+","+rs.getString(5)+","+rs.getString(1));
    
        
      }
    }
    catch(SQLException sqEx)
    {
        logger.error(FILE_NAME+ "[getContactNamesforAttentionLOV(String customerId,String addressType,String quoteId,String flag) ] -> "+sqEx.toString());
        throw new EJBException(sqEx.toString());
    }catch(Exception Ex)
    {
        logger.error(FILE_NAME+ "[getContactNamesforAttentionLOV(String customerId,String addressType,String quoteId,String flag) ] -> "+Ex.toString());
        throw new EJBException(Ex.toString());
    }
    finally
    {
        ConnectionUtil.closeConnection(connection,stmt,rs);
    }
    return records;

   } 
   //ends 167768
   //added by phani sekhar for wpbn 171213 on 20090615
    public ArrayList getRegionIds(String searchString,String countryId,String locationId,String sMode)
    {
        Connection	connection	= null;   
        ResultSet   rs			= null;
	CallableStatement cstmt =null;
	String		locationIds	= null;
        String		countryIds	= null;
	String	        regionIds	= null;

		ArrayList regionsList = new ArrayList();
   try
        {
	     if(!(locationId.equals("")))
          {
            
            locationIds=locationId.replaceAll(",","','");
           
          }
	     if(!(countryId.equals("")))
          {
            
            countryIds=countryId.replaceAll(",","','");
           
          }
	     if(!(searchString.equals("")))
          {
            
            regionIds=searchString.replaceAll(",","','");
           
          }
	    connection = this.getConnection();
            cstmt        = connection.prepareCall("{ ?= call QMS_SETUPS.Get_Regions(?,?,?,?)}");
           cstmt.registerOutParameter(1,OracleTypes.CURSOR);
            cstmt.setString(2,regionIds);            
            cstmt.setString(3,locationIds);
	     cstmt.setString(4,countryIds);
	      cstmt.setString(5,sMode);
          
            cstmt.execute();
            rs  =  (ResultSet)cstmt.getObject(1);
            while(rs.next())
			{regionsList.add(rs.getString(1)+" ["+rs.getString(1)+"]");}
        }
        catch(SQLException sqEx)
        {sqEx.printStackTrace();
			
      logger.error(FILE_NAME+ "[getRegionIds(whereclause)] -> "+sqEx.toString());
            throw new EJBException(sqEx.toString());
        }
		catch(Exception e)
        {
          e.printStackTrace();
          throw new EJBException(e.toString());
        }
        finally
        {
			ConnectionUtil.closeConnection(connection, cstmt, rs);
        }
        return regionsList;
    }
    
 public ArrayList getRegionalCountryIds(String searchString,String locationId,String regionId, String sMode)
    {
      Connection	connection	= null;   
      ResultSet   rs			= null;
      CallableStatement cstmt =null;
      String		locationIds	= null;
      String		countryIds	= null;
      String	  regionIds	= null;

		ArrayList countrysList = new ArrayList();
   try
        {
	     if(!(locationId.equals("")))
          {
            
            locationIds=locationId.replaceAll(",","','");
           
          }
	     if(!(regionId.equals("")))
          {
            
            regionIds=regionId.replaceAll(",","','");
           
          }
	     if(!(searchString.equals("")))
          {
            
            countryIds=searchString.replaceAll(",","','");
           
          }
	    connection = this.getConnection();
            cstmt        = connection.prepareCall("{ ?= call QMS_SETUPS.Get_Region_countryIds(?,?,?,?)}");
            cstmt.registerOutParameter(1,OracleTypes.CURSOR);
            cstmt.setString(2,locationIds);
            cstmt.setString(3,countryIds);
            cstmt.setString(4,regionIds);          
            cstmt.setString(5,sMode);
          
            cstmt.execute();
            rs  =  (ResultSet)cstmt.getObject(1);
            while(rs.next())
        {
      countrysList.add(rs.getString(2)+" ["+rs.getString(1)+"]");      
        }
        
        
        }  catch(SQLException sqEx)
        {sqEx.printStackTrace();
			
      logger.error(FILE_NAME+ "[getRegionIds(whereclause)] -> "+sqEx.toString());
            throw new EJBException(sqEx.toString());
        }
       
		catch(Exception e)
        {
          e.printStackTrace();
          throw new EJBException(e.toString());
        }
        finally
        {
			ConnectionUtil.closeConnection(connection, cstmt, rs);
        }
        return countrysList;
    }
     
    public ArrayList getRegionalLocIds(String regionId,String terminald,String operation,String shipMentMode)
    {
       Connection	connection	= null;   
      ResultSet   rs			= null;
      CallableStatement cstmt =null;
      String		locationIds	= null;
      String		countryIds	= null;
      String	  regionIds	= null;
      String locId=null;
      String sMode,temp= null;
      	ArrayList locationsList = new ArrayList();
        try
        {
            if(!(regionId.equals("")))
          {
            
            regionIds=regionId.replaceAll(",","','");
           
          }
          connection = this.getConnection();
            cstmt        = connection.prepareCall("{ ?= call QMS_SETUPS.Get_Region_LocationIds(?,?)}");
            cstmt.registerOutParameter(1,OracleTypes.CURSOR);
           
            cstmt.setString(2,regionIds);          
            cstmt.setString(3,shipMentMode);
          
            cstmt.execute();
            rs  =  (ResultSet)cstmt.getObject(1);
        while(rs.next())
            {
             StringBuffer   sMode1            = new StringBuffer();
              StringBuffer   locId1           = new StringBuffer();
              locId = rs.getString(1);	
                      temp = rs.getString(2);
              sMode = rs.getString(3);
                      if(sMode==null)
              {  sMode="   ";}
              if(sMode!=null)
              { 
                if(sMode.equals("7"))	{sMode="AST";}
                if(sMode.equals("1"))	{sMode="A  ";}
                if(sMode.equals("2"))	{sMode=" S ";}		
                if(sMode.equals("3"))	{sMode="AS ";}
                if(sMode.equals("4"))	{sMode="  T";}
                if(sMode.equals("5"))	{sMode="A T";}
                if(sMode.equals("6"))	{sMode=" ST";}
              }	
              if(locId.length() == 2)	       
                       {locId1.append(locId);
                locId1.append(" ");
                locId = locId1.toString();}         
              if(locId.length() == 1)            
                 {locId1.append(locId);
                locId1.append(" ");
                locId = locId1.toString();}	
                sMode1.append(locId);
                sMode1.append("[" );
                sMode1.append(sMode);
                sMode1.append( "]");
                sMode1.append("[" );
                sMode1.append(temp);
                sMode1.append( "]");
                sMode = sMode1.toString();
                locationsList.add(sMode);
          }
        
        }  catch(SQLException sqEx)
        {sqEx.printStackTrace();
			
      logger.error(FILE_NAME+ "[getRegionIds(whereclause)] -> "+sqEx.toString());
            throw new EJBException(sqEx.toString());
        }
       
		catch(Exception e)
        {
          e.printStackTrace();
          throw new EJBException(e.toString());
        }
        finally
        {
			ConnectionUtil.closeConnection(connection, cstmt, rs);
        }
        return locationsList;
        
    }
     //ends 171213   
    //Added by Anil.k for Enhancement 231214 on 25Jan2011
    /**
     * 
     * @throws java.sql.SQLException
     * @return      
     * @param searchStr
     * @param terminalId
     * @param shipmentMode
     * @param accessLevel
     * @param originLocation
     * @param destLocation
     */
    public ArrayList  getAllChargeGroupIds(String searchStr, String terminalId, String shipmentMode, String accessLevel, String originLocation, String destLocation)throws SQLException
    {
      PreparedStatement pstmt = null;
      Connection  connection  = null;
      ResultSet   rs          = null;
      ArrayList   chargeGroupList = new ArrayList();
      StringBuffer      terminalQry = new StringBuffer();
      String selectQry        = "";
      String  shipment  = "";
      String  orgLoc	= "";
      String  destLoc	= "";
      
      
      try
      {
        if("H".equalsIgnoreCase(accessLevel))
        {
          terminalQry.append( " (SELECT terminalid term_id FROM FS_FR_TERMINALMASTER)");
        }
        else
        {
          terminalQry.append( "(SELECT parent_terminal_id term_id FROM FS_FR_TERMINAL_REGN CONNECT BY child_terminal_id = PRIOR parent_terminal_id START WITH child_terminal_id = ?")
                     .append( " UNION ")
                     .append( " SELECT terminalid term_id FROM FS_FR_TERMINALMASTER WHERE oper_admin_flag = 'H' ")
                     .append( " UNION ")
                     .append( " SELECT ? term_id FROM DUAL ")
                     .append( " UNION ")
                     .append( " SELECT child_terminal_id term_id FROM FS_FR_TERMINAL_REGN CONNECT BY PRIOR child_terminal_id = parent_terminal_id START WITH parent_terminal_id = ?)");
        }
        
        if("Truck".equalsIgnoreCase(shipmentMode)){
          shipment = " AND SHIPMENT_MODE IN (4,5,6,7) ";
        }else if("Air".equalsIgnoreCase(shipmentMode)){
          shipment = " AND SHIPMENT_MODE IN (1,3,5,7) ";
        }else if("Sea".equalsIgnoreCase(shipmentMode)){
          shipment = " AND SHIPMENT_MODE IN (2,3,6,7) ";
        }        
        if(originLocation!=null && !"".equals(originLocation))
        	orgLoc = "AND (INSTR(ORIGINCOUNTRY , (SELECT FFL.COUNTRYID FROM FS_FR_LOCATIONMASTER FFL WHERE FFL.LOCATIONID = '"+originLocation+"'))<>0 OR INSTR(DESTINATIONCOUNTRY , (SELECT FFL.COUNTRYID FROM FS_FR_LOCATIONMASTER FFL WHERE FFL.LOCATIONID = '"+originLocation+"'))<>0)";
        	//orgLoc = "AND ORIGINCOUNTRY IN (SELECT FFL.COUNTRYID FROM FS_FR_LOCATIONMASTER FFL WHERE FFL.LOCATIONID = '"+originLocation+"')";
        if(destLocation!=null && !"".equals(destLocation))
        	destLoc = "AND (INSTR(ORIGINCOUNTRY , (SELECT FFL.COUNTRYID FROM FS_FR_LOCATIONMASTER FFL WHERE FFL.LOCATIONID = '"+destLocation+"'))<>0 OR INSTR(DESTINATIONCOUNTRY , (SELECT FFL.COUNTRYID FROM FS_FR_LOCATIONMASTER FFL WHERE FFL.LOCATIONID = '"+destLocation+"'))<>0)";
        	//destLoc = "AND DESTINATIONCOUNTRY IN (SELECT FFL.COUNTRYID FROM FS_FR_LOCATIONMASTER FFL WHERE FFL.LOCATIONID = '"+destLocation+"')";
        selectQry  = "SELECT DISTINCT CHARGEGROUP_ID FROM QMS_CHARGE_GROUPSMASTER WHERE CHARGEGROUP_ID LIKE '"+
        			// Commented by Govind for getting All App charge Groups
                    //  searchStr+"%' "+shipment+" AND INVALIDATE='F' AND INACTIVATE='N' "+  orgLoc+destLoc+" AND TERMINALID IN "+ 
        searchStr+"%' "+shipment+" AND INVALIDATE='F' AND INACTIVATE='N' AND TERMINALID IN "+
        			  terminalQry.toString()+" ORDER BY CHARGEGROUP_ID";
        
        connection  = operationsImpl.getConnection();
        pstmt       = connection.prepareStatement(selectQry);
        if(!"H".equalsIgnoreCase(accessLevel))
        {
          pstmt.setString(1,terminalId);
          pstmt.setString(2,terminalId);
          pstmt.setString(3,terminalId);
        }
        rs          = pstmt.executeQuery();
        if(rs.next())
        {
        	do{
         chargeGroupList.add(rs.getString("CHARGEGROUP_ID"));
        	}while(rs.next());
        }
        else//Added to get Values of all ChargeGroup if no origin and destination
        {
        	orgLoc = "";
        	destLoc = "";
        	selectQry  = "SELECT DISTINCT CHARGEGROUP_ID FROM QMS_CHARGE_GROUPSMASTER WHERE CHARGEGROUP_ID LIKE '"+
			  searchStr+"%' "+shipment+" AND INVALIDATE='F' AND INACTIVATE='N' "+  orgLoc+destLoc+" AND TERMINALID IN "+
			  terminalQry.toString()+" ORDER BY CHARGEGROUP_ID";
        	 connection  = operationsImpl.getConnection();
             pstmt       = connection.prepareStatement(selectQry);
             if(!"H".equalsIgnoreCase(accessLevel))
             {
               pstmt.setString(1,terminalId);
               pstmt.setString(2,terminalId);
               pstmt.setString(3,terminalId);
             }
             rs          = pstmt.executeQuery();
        while(rs.next())
        {
         chargeGroupList.add(rs.getString("CHARGEGROUP_ID"));
        }
        }//Ended
      }catch(SQLException e)
      {          
          logger.error(FILE_NAME+"------->getAllChargeGroupIds()"+e.toString());
          throw new EJBException();         
      }catch(Exception e)
      {          
          logger.error(FILE_NAME+"------->getAllChargeGroupIds()"+e.toString());
          throw new EJBException();         
      }finally
      {
        try
        {
          if(rs!=null)
            { rs.close();}
          if(pstmt!=null)
            { pstmt.close();}
          if(connection!=null)
            { connection.close();}
        }catch(SQLException e)
        {          
          logger.error(FILE_NAME+"------->getAllChargeGroupIds()"+e.toString());
          throw new SQLException();
        }
      }
      return chargeGroupList;
    }
    /**
     * 
     * @throws java.sql.SQLException
     * @return 
     * @param loginbean
     * @param chargeGroupingDOB
     * @param Operation
     */
    private boolean isValidChargeGroupId(ArrayList dataList,ESupplyGlobalParameters loginbean,String Operation)throws SQLException
    {
      Connection connection     = null;
      PreparedStatement pstmt   = null;
      ResultSet         rs      = null;
      PreparedStatement pstmt1   = null;
      
      PreparedStatement pstmt2   = null;
      PreparedStatement pstmt3   = null;
      
      PreparedStatement pstmt4   = null;
     
      boolean    isValid        = true;
      int        dataListSize   = 0;
      ChargeGroupingDOB chargeGroupingDOB = null;
      boolean   chargeGroupId   = false;
      boolean   chargeId        = false;
      boolean   chargeDescId    = false;
      boolean   orgCountryId	  = false;
      boolean	  destCountryId	  = false;
      int    shipmentMode       = 0;
      String shipmodeStr        = "";
      
      
      try
      {
      
        StringBuffer       higherTerminals = new StringBuffer("");
        
            higherTerminals.append(" SELECT TERMINALID FROM FS_FR_TERMINALMASTER WHERE TERMINALID = ?");
            higherTerminals.append(" UNION");
            higherTerminals.append(" SELECT PARENT_TERMINAL_ID TERMINALID from fs_fr_terminal_regn");
            higherTerminals.append(" connect by prior PARENT_TERMINAL_ID=CHILD_TERMINAL_ID start with CHILD_TERMINAL_ID=?");
            higherTerminals.append(" UNION");
            higherTerminals.append(" SELECT TERMINALID FROM FS_FR_TERMINALMASTER WHERE OPER_ADMIN_FLAG='H'");

        StringBuffer      lowerTerminals  = new StringBuffer("");

        if("HO_TERMINAL".equals(loginbean.getAccessType()))
        {
        
            lowerTerminals.append(" SELECT TERMINALID  FROM FS_FR_TERMINALMASTER WHERE ACTV_FLAG='A' ");
            
        }else
        {
            lowerTerminals.append(" select CHILD_TERMINAL_ID TERMINALID from fs_fr_terminal_regn ");
            lowerTerminals.append(" connect by prior CHILD_TERMINAL_ID=PARENT_TERMINAL_ID start with PARENT_TERMINAL_ID=?");     
        }


        StringBuffer selectQry          = new StringBuffer("SELECT CHARGEGROUP_ID FROM QMS_CHARGE_GROUPSMASTER WHERE CHARGEGROUP_ID =? AND INACTIVATE='N' AND TERMINALID IN (");
                     selectQry.append(higherTerminals.toString());
                     selectQry.append(")");
                           
        
        
        /*StringBuffer updateChidData = new StringBuffer("  UPDATE QMS_CHARGE_GROUPSMASTER SET INACTIVATE = 'Y' WHERE CHARGEGROUP_ID = ? AND TERMINALID IN (");
                     updateChidData.append(lowerTerminals.toString());
                     updateChidData.append(") AND INACTIVATE = 'N' ");*/
                     
        connection    = operationsImpl.getConnection();
        
        dataListSize = dataList.size();
        chargeGroupingDOB = (ChargeGroupingDOB)dataList.get(0);
        shipmentMode = chargeGroupingDOB.getShipmentMode();
               
        if(shipmentMode==1)
        { shipmodeStr = "1,3,5,7";}
        else if(shipmentMode==2)
        { shipmodeStr = "2,3,6,7";}
        else if(shipmentMode==3)
        { shipmodeStr = "3,7";}
        else if(shipmentMode==4)
        { shipmodeStr = "4,5,6,7";}
        else if(shipmentMode==5)
        { shipmodeStr = "5,7";}
        else if(shipmentMode==6)
        { shipmodeStr = "6,7";}
        else if(shipmentMode==7)
        { shipmodeStr = "7";}      
        
        String selectChargeID     = "SELECT CHARGE_ID FROM QMS_CHARGESMASTER WHERE CHARGE_ID = ? AND SHIPMENT_MODE IN ("+shipmodeStr+") AND INVALIDATE='F'";
        
        StringBuffer selectChargeDescId = new StringBuffer("SELECT  CHARGEDESCID   FROM QMS_CHARGEDESCMASTER  WHERE CHARGEID =? AND CHARGEDESCID=? AND SHIPMENTMODE IN("+shipmodeStr+") AND TERMINALID IN (");
                     selectChargeDescId.append(higherTerminals);
                     selectChargeDescId.append(")");
                     
        //String orgCountry = "SELECT * FROM FS_COUNTRYMASTER FC WHERE FC.COUNTRYID='"+chargeGroupingDOB.getOriginCountry()+"'";//Added by Anil.k for CR 231214 on 25Jan2011
       // String destCountry = "SELECT * FROM FS_COUNTRYMASTER FC WHERE FC.COUNTRYID='"+chargeGroupingDOB.getDestinationCountry()+"'";//Added by Anil.k for CR 231214 on 25Jan2011

        //pstmt         = connection.prepareStatement(selectQry.toString());
        String chargeGroupIdStr  = chargeGroupingDOB.getChargeGroup();
        String terminalId     = chargeGroupingDOB.getTerminalId();
        
        /*pstmt.setString(1,chargeGroupIdStr);
        pstmt.setString(2,terminalId);
        pstmt.setString(3,terminalId);
        
        rs  = pstmt.executeQuery();
        if(rs.next())
        {
          chargeGroupId = false;
        }else
        { chargeGroupId = true;}

         if(rs!=null)
          { rs.close();}     
        if(pstmt!=null)
          { pstmt.close();}*/
        
        if(Operation!=null && "Modify".equalsIgnoreCase(Operation))
        	chargeGroupId = true;
          
        if(chargeGroupId)
        {   
            pstmt1         = connection.prepareStatement(selectChargeID);
            pstmt2         = connection.prepareStatement(selectChargeDescId.toString());
            
            for(int i=0;i<dataListSize;i++)
            {
              chargeGroupingDOB = (ChargeGroupingDOB)dataList.get(i);
              if(chargeGroupingDOB!=null)
              {
                pstmt1.clearParameters();
                pstmt1.setString(1,chargeGroupingDOB.getChargeIds());
                rs = pstmt1.executeQuery();
                if(rs.next())
                { chargeId = true;}
                else
                { chargeId = false;}
                
                if(rs!=null)
                { rs.close();}
                
                pstmt2.clearParameters();
                pstmt2.setString(1,chargeGroupingDOB.getChargeIds());
                pstmt2.setString(2,chargeGroupingDOB.getChargeDescId());
                pstmt2.setString(3,chargeGroupingDOB.getTerminalId());
                pstmt2.setString(4,chargeGroupingDOB.getTerminalId());
                rs = pstmt2.executeQuery();
                if(rs.next())
                { chargeDescId = true;}
                else
                { chargeDescId = false;}
                
                if(rs!=null)
                { rs.close();}
              }
              
              if(!chargeId || !chargeDescId )
              { break;}
            }
        }
       
       /* if(chargeGroupId && chargeId && chargeDescId)
        {
      	  pstmt3         = connection.prepareStatement(orgCountry);
            pstmt4         = connection.prepareStatement(destCountry);                
            rs = pstmt3.executeQuery();
            if(rs.next())
            { orgCountryId = true;}
            else
            { orgCountryId = false;}
                               
            rs = pstmt4.executeQuery();
            if(rs.next())
            { destCountryId = true;}
            else
            { destCountryId = false;}
            
            if(rs!=null)
            { rs.close();}        
      	  
        }*/
        if(chargeGroupId && chargeId && chargeDescId)
        { 
          if(pstmt!=null)
            { pstmt.close();}
          

         /* pstmt = connection.prepareStatement(updateChidData.toString());
          pstmt.setString(1,chargeGroupIdStr);
          if(!"HO_TERMINAL".equals(loginbean.getAccessType()))
          {
            pstmt.setString(2,terminalId);
          }
          pstmt.executeUpdate();*/
          
          isValid = true;
        }
        else
        { isValid = false;}
        
      }catch(SQLException e)
      {
          //Logger.error(FILE_NAME,"------->isValidChargeGroupId()",e.toString());
          logger.error(FILE_NAME+"------->isValidChargeGroupId()"+e.toString());
          throw new EJBException();         
      }catch(Exception e)
      {
          //Logger.error(FILE_NAME,"------->isValidChargeGroupId()",e.toString());
          logger.error(FILE_NAME+"------->isValidChargeGroupId()"+e.toString());
          throw new EJBException();         
      }finally
      {
        try
        {
          if(rs!=null)
            { rs.close();}
          if(pstmt!=null)
            { pstmt.close();}
          if(pstmt1!=null)
            { pstmt1.close();}
          if(pstmt2!=null)
            { pstmt2.close();}
          if(pstmt3!=null)
          { pstmt3.close();}// Added by Gowtham on 17Feb2011 for connection Leaks.
          if(pstmt4!=null)
          { pstmt4.close();}// Added by Gowtham on 17Feb2011 for connection Leaks.
          if(connection!=null)
            { connection.close();}
        }catch(SQLException e)
        {
          //Logger.error(FILE_NAME,"------->isValidChargeGroupId()",e.toString());
          logger.error(FILE_NAME+"------->isValidChargeGroupId()"+e.toString());
          throw new EJBException();
        }
      }
      return isValid;    
    }//Ended by Anil.k for CR 231214 on 25Jan2011
    //Added by Anil.k for issue 236357 on 22Feb2011
    public ArrayList getChargeGroupDtl(String opertaion,BuySellChargesEnterIdDOB buySellChargesEnterIdDOB,ESupplyGlobalParameters loginbean)throws ObjectNotFoundException,EJBException
    {
      //ChargeGroupEntityBeanLocalHome home  = null;
      //ChargeGroupEntityBeanLocal      local = null;
      ChargeGroupingDOB chargeGroupingDOB =null;
      ChargeGroupingDAO chargeGroupingDAO =new ChargeGroupingDAO();
      //ChargeGroupEntityBeanPK       pkObj   = new ChargeGroupEntityBeanPK();
      ArrayList dataList  = null;

      try
      {
          //pkObj.chargeGroup      = chargeGroupId;
         // home  = (ChargeGroupEntityBeanLocalHome)LookUpBean.getEJBLocalHome("java:comp/env/ChargeGroupEntityBean");
          //local = (ChargeGroupEntityBeanLocal)home.findByPrimaryKey(pkObj);
          //chargeGroupingDOB  = local.getChagesGroupDOB();
          String chargeGroupId  = buySellChargesEnterIdDOB.getChargeGroupId();
          String terminalId     = buySellChargesEnterIdDOB.getTerminalId();
          String fromWhere      = buySellChargesEnterIdDOB.getFromWhere();
          if(chargeGroupId!=null &&  !"".equals(chargeGroupId))
          {
              if(validateLoadDetails(buySellChargesEnterIdDOB,loginbean.getTerminalId()))
              {
                
                dataList = chargeGroupingDAO.load(opertaion,chargeGroupId,terminalId,fromWhere,loginbean.getAccessType());
                if(dataList==null || dataList.size()<=0)
                {
                    throw new ObjectNotFoundException();
                }
              }else
              {
                throw new ObjectNotFoundException();
              }
          }
          
      }catch(ObjectNotFoundException e)
      {
          //Logger.error(FILE_NAME,"getChargeGroupDtl()",e.toString());
          logger.error(FILE_NAME+"getChargeGroupDtl()"+e.toString());
          throw new ObjectNotFoundException("Bean could not find");      
      }catch(Exception e)
      {
          //Logger.error(FILE_NAME,"getChargeGroupDtl()",e.toString());
          logger.error(FILE_NAME+"getChargeGroupDtl()"+e.toString());
          throw new EJBException();      
      }
      
      return dataList;
    }
    //Ended by Anil.k for issue 236357 on 22Feb2011
   }
//@@WPBN issue-66410

//@@WPBN issue-66410