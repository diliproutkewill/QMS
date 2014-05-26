package com.foursoft.etrans.setup.customer.dao;

import com.foursoft.etrans.setup.customer.java.CustContactDtl;
import javax.ejb.EJBException;
import javax.ejb.ObjectNotFoundException;
import javax.naming.NamingException;
import javax.naming.InitialContext;
import java.sql.SQLException;
import com.foursoft.esupply.common.util.ConnectionUtil;
//import com.foursoft.esupply.common.util.Logger;
import org.apache.log4j.Logger;
import com.foursoft.etrans.setup.customer.java.CustomerModel;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import javax.sql.DataSource;   
import java.util.ArrayList;
import com.foursoft.etrans.common.bean.Address; 

/*
 * @author 
 * @version : 1.6
 */
public class CustomerDAO 
{

     private static final  String FILE_NAME     =	"CustomerDAO";
	 private DataSource dataSource = null;
   private static Logger logger = null;
     
	 public CustomerDAO()  
	 {
   logger  = Logger.getLogger(CustomerDAO.class);
		try
		{
			InitialContext ic	= new InitialContext();
			dataSource			=(DataSource) ic.lookup("java:comp/env/jdbc/DB");
		}
		catch(NamingException nmEx)
		{
			//Logger.error(FILE_NAME, "CustomerDAO", nmEx.toString());
      logger.error(FILE_NAME+ "CustomerDAO"+ nmEx.toString());
			throw new EJBException(nmEx.toString());
		}
	    
	 }
   

  /**
   * 
   * @param customerModel
   * @throws javax.ejb.EJBException
   */
     public  void  create(CustomerModel customerModel)  throws SQLException
	 {
		insertCustomerDetails(customerModel);
		//insertCustomerNotifDetails(customerModel);
		insertCustomerContactDetails(customerModel);
	 }//end of create method
 
 //JS
  /**
   * 
   * @param custMoreAddrList
   * @throws javax.ejb.EJBException
   */
    public  void  create(ArrayList custMoreAddrList)  throws SQLException
	 {
		
//Logger.info(FILE_NAME, " Starting of the Dao Creat Method alist size --> "+custMoreAddrList.size());
		if((custMoreAddrList!=null) && (custMoreAddrList.size() > 0)){
			int custAddrListSize	=	custMoreAddrList.size();
			for(int i=0;i<custAddrListSize;i++){
				CustomerModel	custMoreAddObj	=	(CustomerModel)custMoreAddrList.get(i);
				insertintoCustomerAddressTable(custMoreAddObj);
			}
		}

	 }//end of create method
//JS
  /**
   * 
   * @param customerModel
   * @throws javax.ejb.EJBException
   */
	 private void  insertintoCustomerAddressTable(CustomerModel customerModel) throws SQLException
	 {
	           PreparedStatement		pStmt			  = null;	
			   String					sqlQry			  = "INSERT INTO FS_FR_CUSTOMER_ADDRESS ( CUSTOMERID,TERMINALID,CUSTOMERADDRESSID,CONTACTNAME, DESIGNATION,DEL_FLAG,ADDRESS_TYPE) VALUES(?,?,?,?,?,?,?)";
			   Connection				connection		  = null;

//Logger.info(FILE_NAME," insertintoCustomerAddressTable Method sqlQry is---> "+sqlQry);
			   try
			   {
             
				  connection	=	getConnection();
				  pStmt			=		connection.prepareStatement(sqlQry);

				  pStmt.setString(1,customerModel.getCustomerId());
				  pStmt.setString(2,customerModel.getTerminalId());
				  pStmt.setInt(3,customerModel.getCustomerAddressId());
				  pStmt.setString(4,customerModel.getContactName());
				  pStmt.setString(5,customerModel.getDesignation());
				  pStmt.setString(6,customerModel.getDelFlag());
				  pStmt.setString(7,customerModel.getAddressType());
				  
			      pStmt.executeUpdate();
			   
			   }
			   catch(SQLException sqlEx)
				{
				   //Logger.error(FILE_NAME,"insertintoCustomerAddressTable()","SQLException While Inserting Customer Details Record",sqlEx);
           logger.error(FILE_NAME+"insertintoCustomerAddressTable()"+"SQLException While Inserting Customer Details Record"+sqlEx);
				   sqlEx.printStackTrace();
				   throw new SQLException("Error in Inserting Customer master Addresss Table   ");
				}
				finally
			 	{
					ConnectionUtil.closeConnection(connection,pStmt);
			 	}//end of finally block
	  }//end of method




  /**
   * 
   * @param customerModel
   * @throws javax.ejb.EJBException
   */
     private void  insertCustomerDetails(CustomerModel customerModel) throws SQLException
	 {
	           PreparedStatement		pStmt			  = null;	
			   StringBuffer				insQuery		  = new StringBuffer();
			   Connection				connection		  = null;

			   try
			   {
				  connection	=	getConnection();
				  insQuery.append("INSERT INTO FS_FR_CUSTOMERMASTER ( CUSTOMERID,COMPANYNAME,TERMINALID,CONTACTNAME,");
				  //insQuery.append("INSERT INTO FS_FR_CUSTOMERMASTER ( CUSTOMERID,COMPANYNAME,TERMINALID,CONTACTPERSON_FIRSTNAME,CONTACTPERSON_LASTNAME,CUSTOMER_STATUS,EISSSN");
				  insQuery.append("DESIGNATION,CUSTOMERADDRESSID,NOTES,ABBRNAME,REGISTERED,CURRENCY,CREDITDAYS,CREDITLIMIT,");
				  insQuery.append("INDICATOR,CUSTOMERTYPE,OPERATIONS_EMAILID,CORPCUSTOMERID,PAYMENTTERMS,DEVISION, "); // Added by Gowtham for Customer Salesperson
				 
				  //@@ Suneetha added on 20050309 for Bulk Invoicing
				  //@@ Srivegi added on 20050224 (AES-SED)
                  // insQuery.append("CONTACTPERSONLASTNAME ,EISSSN ,KNOWNSHIPPER ) ");
                  //@@ 20050224  
				  insQuery.append("CONTACTPERSONLASTNAME ,EISSSN ,KNOWNSHIPPER, ");
                  insQuery.append("BULKINVOICEREQUIRED,SALESPERSON ) ");
                  //@@ 20050309 for Bulk Invoicing

				  //insQuery.append("SALESMANID,INDICATOR,CUSTOMERTYPE,OPERATIONS_EMAILID,CORPCUSTOMERID)  VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");
				   insQuery.append(" VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");
     //Logger.info(FILE_NAME, " Dao 2222 insert quer "+insQuery.toString());            
			 								
			      pStmt				=		connection.prepareStatement(insQuery.toString());
            
            			 pStmt.setString(1,customerModel.getCustomerId());
				  pStmt.setString(2,customerModel.getCompanyName());
				  pStmt.setString(3,customerModel.getTerminalId());
				  pStmt.setString(4,customerModel.getContactName());
				  pStmt.setString(5,customerModel.getDesignation());
				  pStmt.setInt(6,customerModel.getCustomerAddressId());
				  pStmt.setString(7,customerModel.getNotes());
				  pStmt.setString(8,customerModel.getAbbrName());
				  pStmt.setString(9,customerModel.getRegistered());
				  pStmt.setString(10,customerModel.getCurrency());
				  pStmt.setInt(11,customerModel.getCreditDays());
				  pStmt.setDouble(12,customerModel.getCreditLimit());

				  pStmt.setString(13,customerModel.getIndicator());
				  pStmt.setString(14,customerModel.getTypeOfCustomer());
				  pStmt.setString(15,customerModel.getOpEmailId());
				  pStmt.setString(16,customerModel.getCorpCustomerId());
                 //@@ Srivegi added on 20050224 (AES-SED)
				  pStmt.setString(17,customerModel.getPaymentTerms());
				  pStmt.setString(18,customerModel.getDivision());
                  pStmt.setString(19,customerModel.getContactLastName());

				  pStmt.setString(20,customerModel.getEINSSNNo());
				  pStmt.setString(21,customerModel.getcustType());
                 //@@ 20050224
				 //@@ Suneetha added on 20050309 for Bulk Invoicing
				  pStmt.setString(22,customerModel.getBulkInvoiceRequired());
				  pStmt.setString(23,customerModel.getSalesPersonCode()); // Added by Gowtham
          
          //System.out.println("customerModel.getPaymentTerms() "+customerModel.getPaymentTerms());
          //				  pStmt.setString(1,customerModel.getCustomerId());
//				  pStmt.setString(2,customerModel.getCompanyName());
//				  pStmt.setString(3,customerModel.getTerminalId());
//				  pStmt.setString(4,customerModel.getContactName());
//				  pStmt.setString(5,customerModel.getDesignation());
//				  pStmt.setInt(6,customerModel.getCustomerAddressId());
//				  pStmt.setString(7,customerModel.getNotes());
//				  pStmt.setString(8,customerModel.getAbbrName());
//				  pStmt.setString(9,customerModel.getRegistered());
//				  pStmt.setString(10,customerModel.getCurrency());
//				  pStmt.setInt(11,customerModel.getCreditDays());
//				  pStmt.setDouble(12,customerModel.getCreditLimit());
//				  //pStmt.setString(13,customerModel.getSCode());
//				  pStmt.setString(13,customerModel.getIndicator());
//				  pStmt.setString(14,customerModel.getTypeOfCustomer());
//				  pStmt.setString(15,customerModel.getOpEmailId());
//				  pStmt.setString(16,customerModel.getCorpCustomerId());
//                 //@@ Srivegi added on 20050224 (AES-SED)
//                  pStmt.setString(17,customerModel.getContactLastName());
//				  pStmt.setString(18,customerModel.getEINSSNNo());
//				  pStmt.setString(19,customerModel.getcustType());
//                 //@@ 20050224
//				 //@@ Suneetha added on 20050309 for Bulk Invoicing
//				  pStmt.setString(20,customerModel.getBulkInvoiceRequired());
//          System.out.println("customerModel.getPaymentTerms() "+customerModel.getPaymentTerms());
//				  pStmt.setString(21,customerModel.getPaymentTerms());
//				  pStmt.setString(22,customerModel.getDivision());
//				 //@@ 20050309 for Bulk Invoicing
//				  

				  
			      pStmt.executeUpdate();
			   
			   }
			   catch(SQLException sqlEx)
				{
				   //Logger.error(FILE_NAME,"insertcustomerModels()","SQLException While Inserting Customer Details Record",sqlEx);
           logger.error(FILE_NAME+"insertcustomerModels()"+"SQLException While Inserting Customer Details Record"+sqlEx);
				   sqlEx.printStackTrace();
				   throw new SQLException("Error in Inserting Customer master  ");
				}
				finally
			 	{
					ConnectionUtil.closeConnection(connection,pStmt);
			 	}//end of finally block
	  }//end of method

 /**
   * 
   * @param customerModel
   * @throws javax.ejb.EJBException
   */
     private void  insertCustomerContactDetails(CustomerModel customerModel) throws SQLException
	  {
	           PreparedStatement		pStmt			  = null;	
			       StringBuffer				insQuery		  = new StringBuffer();
			       Connection				connection		  = null;
             ResultSet        rs              = null;
	           ArrayList custdtls=new ArrayList();
			       CustContactDtl custDtl  =  null;
             String  contactId       =  null;
             String  contactIdQry    =  "SELECT QMS_CUSTCONTACTIDS.NEXTVAL CONTACTID FROM DUAL";

			   try
			   {
				  connection	=	getConnection();
				  insQuery.append("INSERT INTO QMS_CUST_CONTACTDTL ( CUSTOMERID ,TERMINALID ,ADDRTYPE ,CONTACTPERSON ,");
				  insQuery.append("DESIGNATION ,DEPT ,ZIPCODE ,CONTACTNO ,FAX ,EMAILID,CONTACTID,SL_NO) ");//contact id added by rk
				  insQuery.append(" VALUES(?,?,?,?,?,?,?,?,?,?,?,?) ");
          
          pStmt       = connection.prepareStatement(contactIdQry);
          rs          = pStmt.executeQuery();   
          
          if(rs.next())
             contactId   = rs.getString("CONTACTID");	
             
          if(pStmt!=null) pStmt.close();
          if(rs!=null) rs.close();
             
			    pStmt				=		connection.prepareStatement(insQuery.toString());
				  custdtls=customerModel.getContactDtl();
				 int custdtlsSize	=	custdtls.size();
					 for(int i=0;i<custdtlsSize;i++)
					 { 
					  custDtl=(CustContactDtl)custdtls.get(i);
					 // System.out.println("custDtl:"+custDtl);
					  if(custDtl!=null)
					  {
              pStmt.setString(1,customerModel.getCustomerId());
              pStmt.setString(2,customerModel.getTerminalId());
              pStmt.setString(3,custDtl.getAddrType());
              pStmt.setString(4,custDtl.getContactPerson());
              pStmt.setString(5,custDtl.getDesignation());
              pStmt.setString(6,custDtl.getDept());
              pStmt.setString(7,custDtl.getZipCode());
              pStmt.setString(8,custDtl.getContact());
              pStmt.setString(9,custDtl.getFax());
              pStmt.setString(10,custDtl.getEmail());
              pStmt.setString(11,contactId);
              pStmt.setInt(12,i);
				      pStmt.addBatch();
					}
			    }
          //Logger.info(FILE_NAME,"list.size()  "+custdtls.size());
          if(custdtls.size()>0)
             pStmt.executeBatch();	   
			   }
			   catch(SQLException sqlEx)
				 {
				   //Logger.error(FILE_NAME,"insertCustomerContactDetails()","SQLException While Inserting Customer Contact Details Record",sqlEx);
           logger.error(FILE_NAME+"insertCustomerContactDetails()"+"SQLException While Inserting Customer Contact Details Record"+sqlEx);
				   sqlEx.printStackTrace();
				   throw new SQLException("Error in Inserting Customer dtl  ");
				 }
          finally
          {
            ConnectionUtil.closeConnection(connection,pStmt);
          }//end of finally block
	   }
  /**
   * 
   * @param customerModel
   * @throws javax.ejb.EJBException
   */
      public void insertCustomerNotifDetails(CustomerModel customerModel) throws SQLException
	  {
	          PreparedStatement			pStmt			  = null;	
		      StringBuffer				insQuery		  = new StringBuffer();
			  Connection				connection		  = null;

			  try
			 {
		              
                       connection = getConnection();
                       insQuery.append("INSERT INTO FS_FR_CUSTOMER_NOTIF (TERMINAL_ID,CUSTOMER_ID,PRQ_CREATE_FLAG,PRQ_MODIFY_FLAG,");
					   insQuery.append("PRQ_DELETE_FLAG,HOUSE_CREATE_FLAG,HOUSE_MODIFY_FLAG,HOUSE_DELETE_FLAG,MASTER_CREATE_FLAG,");
					   insQuery.append("MASTER_MODIFY_FLAG,MASTER_DELETE_FLAG,DO_CREATE_FLAG,CREDIT_CHECK_FLAG,BB_FLAG,BB_MODIFY_FLAG,");
					   insQuery.append("MASTER_CLOSE_FLAG)  VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");
					   

					   pStmt	 =  connection.prepareStatement(insQuery.toString());
					   
					   pStmt.setString(1,customerModel.getTerminalId());
					   pStmt.setString(2,customerModel.getCustomerId());
					   pStmt.setString(3,customerModel.getPrqCreateFlag());
					   pStmt.setString(4,customerModel.getPrqModifyFlag());
					   pStmt.setString(5,customerModel.getPrqDeleteFlag());
					   pStmt.setString(6,customerModel.getHouseCreateFlag());
					   pStmt.setString(7,customerModel.getHouseModifyFlag());
					   pStmt.setString(8,customerModel.getHouseDeleteFlag());
					   pStmt.setString(9,customerModel.getMasterCreateFlag());
					   pStmt.setString(10,customerModel.getMasterModifyFlag());
					   pStmt.setString(11,customerModel.getMasterDeleteFlag());
					   pStmt.setString(12,customerModel.getDoCreateFlag());
					   pStmt.setString(13,customerModel.getCreditFlag());
					   pStmt.setString(14,customerModel.getBBFlag());
					   pStmt.setString(15,customerModel.getBBModifyFlag());
					   pStmt.setString(16,customerModel.getMasterCloseFlag());
 //pStmt.executeQuery();
              //For 10g server implementation
              pStmt.executeUpdate();

			 
			 }
			 catch(SQLException sqlEx)
			 {
				   //Logger.error(FILE_NAME,"insertcustomerModels()----->","SQLException While Inserting Customer Details Record",sqlEx);
           logger.error(FILE_NAME+"insertcustomerModels()----->"+"SQLException While Inserting Customer Details Record"+sqlEx);
				   throw new SQLException("Error in Inserting Customer master  ");
			 }//end of try block
			finally
			 {
					ConnectionUtil.closeConnection(connection,pStmt);
			 }
	  }//end of method

      
  /**
   * 
   * @param customerId
   * @param terminalId
   * @return boolean
   * @throws javax.ejb.ObjectNotFoundException
   */
	  public  boolean findByPrimaryKey(String customerId,String terminalId) throws ObjectNotFoundException
	  {
	          PreparedStatement	pStmt						=	null;	
		      StringBuffer				insQuery			= 	new StringBuffer();
			  ResultSet					rs					= 	null;	
			  boolean					isCustomerExists 	= 	false;
			  Connection 				connection			=	null;
			  try
			  {
			      connection=getConnection();
				  
				  insQuery.append("SELECT COUNT(*) FROM FS_FR_CUSTOMERMASTER WHERE CUSTOMERID=? AND TERMINALID=?");
				  
				  pStmt =  connection.prepareStatement(insQuery.toString());

				  pStmt.setString(1,customerId);
				  pStmt.setString(2,terminalId);

				  rs   =   pStmt.executeQuery();
			    
			     if(rs.next())
           {
            isCustomerExists  =  true;
           }

			     rs.close();
			  }
			  catch(SQLException sqlEx)
			  {
				 //Logger.error(FILE_NAME,"findByPrimaryKey(CustomerId,terminalId)","Error in Finding CustomerId ",sqlEx);
         logger.error(FILE_NAME+"findByPrimaryKey(CustomerId,terminalId)"+"Error in Finding CustomerId "+sqlEx);
			  }
			  finally
		      {
		        
				ConnectionUtil.closeConnection(connection,pStmt);
			  }
				
			     if(isCustomerExists)
           {
            return true;
           }
				 else
         {
					 throw new ObjectNotFoundException("Unable to Find Customer Bean"); 
         }

     }//end of method


  /**
   * 
   * @param customerId
   * @param terminalId
   * @return CustomerModel
   * @throws javax.ejb.EJBException
   */
	 public CustomerModel   load(String customerId,String terminalId)  throws EJBException,EJBException
	 {
		CustomerModel  customerModel =  null;
		 customerModel = new CustomerModel();
		 System.out.println("executing at load");
		 customerModel.setCustomerId(customerId);
		 customerModel.setTerminalId(terminalId);
		 customerModel  =   loadCustomerMaster(customerId,terminalId);
		 System.out.println("executing at method load");
		 //customerModel	=	loadCustomerNotify(customerModel);
     // customerModel   = loadServiceLevel(customerModel); //K.N.V.Prasada Reddy
		return customerModel;
	  
	  }//end of method
	    
	  
  /**
   * 
   * @param customerId
   * @param terminalId
   * @return CustomerModel
   * @throws javax.ejb.EJBException
   */
	  private  CustomerModel loadCustomerMaster(String customerId,String terminalId)  throws EJBException,EJBException
	  {
	        PreparedStatement		pStmt				= 	null;	
            StringBuffer			loadQuery			= 	new StringBuffer();    
            String loadQuery1="";
            ResultSet				rs					= 	null;	
			CustomerModel			customerModel    	= 	null;
			Connection				connection			=	null;
			ArrayList custdtls=new ArrayList();
			CustContactDtl custDtl=null;
			 try
		     {
		          connection	= getConnection();
		          loadQuery.append("SELECT CUSTOMERID,COMPANYNAME,TERMINALID,CONTACTNAME,DESIGNATION,CUSTOMERADDRESSID,");
				  //loadQuery.append("NOTES,ABBRNAME,REGISTERED,CURRENCY,CREDITDAYS,CREDITLIMIT,SALESMANID,INDICATOR,CUSTOMERTYPE,");
				  loadQuery.append("NOTES,ABBRNAME,REGISTERED,CURRENCY,CREDITDAYS,CREDITLIMIT,INDICATOR,CUSTOMERTYPE,");
				  loadQuery.append("OPERATIONS_EMAILID,CORPCUSTOMERID,"); 
                  //@@ Srivegi added on 20050224 (AES-SED)
                  loadQuery.append("CONTACTPERSONLASTNAME ,EISSSN ,KNOWNSHIPPER "); 
                  //@@ 20050224
				  // @@ Suneetha added on 20050309 for Bulk Invoicing
				  loadQuery.append(",BULKINVOICEREQUIRED,PAYMENTTERMS,DEVISION,SALESPERSON ");  // salesperson Added by Gowtham 
				  // @@ 20050309 for Bulk Invoicing
				  loadQuery.append(" FROM FS_FR_CUSTOMERMASTER  WHERE ");
				  loadQuery.append(" CUSTOMERID=? AND TERMINALID=?");  
			      
				  pStmt				=	connection.prepareStatement(loadQuery.toString());

				  customerModel   	= 	new CustomerModel();

				  pStmt.setString(1,customerId);
				  pStmt.setString(2,terminalId);
				  rs  =  pStmt.executeQuery();
				  while(rs.next())
				  {
				      customerModel.setCustomerId(rs.getString(1));
					  customerModel.setCompanyName(rs.getString(2));
					  customerModel.setTerminalId(rs.getString(3));
					  customerModel.setContactName(rs.getString(4));
					  customerModel.setDesignation(rs.getString(5));
					  customerModel.setCustomerAddressId(rs.getInt(6));
					  customerModel.setNotes(rs.getString(7));
					  customerModel.setAbbrName(rs.getString(8));
					  customerModel.setRegistered(rs.getString(9));
					  customerModel.setCurrencyId(rs.getString(10));
					  customerModel.setCreditDays(rs.getInt(11));
					  customerModel.setCreditLimit(rs.getDouble(12));
					  //customerModel.setSCode(rs.getString(13));
					  customerModel.setIndicator(rs.getString(13));
					  customerModel.setTypeOfCustomer(rs.getString(14));
					  customerModel.setOpEmailId(rs.getString(15));
					  customerModel.setCorpCustomerId(rs.getString(16));
					  //@@ Srivegi added on 20050224 (AES-SED)
                      customerModel.setContactLastName(rs.getString(17));
					  customerModel.setEINSSNNo(rs.getString(18));
					  customerModel.setcustType(rs.getString(19));
                      //@@ 20050224	          
					  // @@ Suneetha added on 20050309 for Bulk Invoicing
	  				  customerModel.setBulkInvoiceRequired(rs.getString(20));
              customerModel.setPaymentTerms(rs.getString(21));
              customerModel.setDivision(rs.getString(22));
					  // @@ 20050309 for Bulk Invoicing
	  				  customerModel.setSalesPersonCode(rs.getString(23)); // Added by  Gowtham to show salesperson code in modify case.
			      }
					loadQuery1 ="SELECT SL_NO,TERMINALID,ADDRTYPE,CONTACTPERSON ,DESIGNATION ,DEPT ,ZIPCODE,CONTACTNO,FAX,EMAILID FROM QMS_CUST_CONTACTDTL WHERE CUSTOMERID=? AND TERMINALID=?";
				 // @@ Suneetha Added on 20050308 for Bulk Invoicing
				 //loadQuery1 = "SELECT VALIDFROM,FREQUENCYTYPE,FREQUENCY,FREQUENCYINFO FROM FS_FR_CUSTOMERINVOICEHDR WHERE CUSTOMERID=? AND TERMINALID=?";
         //Added By RajKumari on 23-10-2008 for Connection Leakages.
         if(pStmt!=null)
         {
          pStmt.close();
          pStmt = null;
         }
				pStmt				=	connection.prepareStatement(loadQuery1);		   
				// Logger.info(FILE_NAME,"loadQuery  OF FS_FR_CUSTOMERINVOICEHDR " + loadQuery1);			  
				 pStmt.setString(1,customerId);
				pStmt.setString(2,terminalId);
				 rs  =  pStmt.executeQuery();
			    while(rs.next())
			    {
					custDtl=new CustContactDtl();
          custDtl.setContactId(rs.getInt("SL_NO"));
					custDtl.setAddrType(rs.getString("ADDRTYPE"));
		            custDtl.setContactPerson(rs.getString("CONTACTPERSON"));
					custDtl.setDesignation(rs.getString("DESIGNATION"));
	                custDtl.setDept(rs.getString("DEPT"));
					custDtl.setZipCode(rs.getString("ZIPCODE"));
					custDtl.setContact(rs.getString("CONTACTNO"));
					custDtl.setFax(rs.getString("FAX"));
					custDtl.setEmail(rs.getString("EMAILID"));
					custdtls.add(custDtl);
				   //customerModel.setInvoiceFrequencyValidDate(rs.getTimestamp(1));
				   //customerModel.setInvoiceFrequencyFlag(rs.getString(2));
				  //if("D".equals(rs.getString(2)) || "M".equals(rs.getString(2))) 
					// customerModel.setInvoiceInfo("");
				  //if("O".equals(rs.getString(2)) || "F".equals(rs.getString(2))) 
					// customerModel.setInvoiceInfo(rs.getString(3));
				  //if("W".equals(rs.getString(2)) || "S".equals(rs.getString(2))) 
				    // customerModel.setInvoiceInfo(rs.getString(4));
			 }	
				customerModel.setContactDtl(custdtls);
			  // @@ 20050308 for Bulk Invoicing
		
                   if(rs != null)
                   {               
                    rs.close();
                   }
				   	return customerModel;
			}
	         catch(SQLException sqlEx)
		     {
		        throw new EJBException("Exception while loading Customer Details \n   "  + sqlEx);
         } 
	         finally
		     {
			   ConnectionUtil.closeConnection(connection,pStmt);
		    }
	}//end of method
	
		
	//Added by K.N.V.Prasada Reddy
  /**
   * 
   * @param customerModel
   * @return CustomerModel
   */
  private CustomerModel loadServiceLevel(CustomerModel customerModel)
     {
       Connection con=null;
       Statement stmt = null;
       ResultSet rs  = null;
       String sql  = null;
       try
       {
         sql="SELECT SERVICELEVELID  FROM FS_FR_CUSTOMER_SRVC_LVL WHERE  CUSTOMER_ID ='"+customerModel.getCustomerId()+"'";
         //System.out.println("Query for getting Service Level : "+sql);
         con=getConnection();
         stmt = con.createStatement();
         rs = stmt.executeQuery(sql);
         if (rs.next())
          {
            customerModel.setServiceLevelId(rs.getString(1));
          }
        return   customerModel;
       }
       catch(SQLException sqlEx)
         {
            throw new EJBException("Exception while loading Customer Details \n   "  + sqlEx);
         }
        finally
		     {
			   ConnectionUtil.closeConnection(con,stmt,rs);
		    }   
        
     }
  /**
   * 
   * @param customerModel
   * @return CustomerModel
   * @throws javax.ejb.EJBException
   */
	 private  CustomerModel loadCustomerNotify(CustomerModel customerModel)  throws EJBException,EJBException
	  {
	         PreparedStatement	pStmt					= null;	
	         StringBuffer		loadQuery				= new StringBuffer();    
             ResultSet			rs						= null;
           Connection			connection				= null;	
			 
			 try
		     {
		          connection	= getConnection();
		          loadQuery.append("SELECT PRQ_CREATE_FLAG,PRQ_MODIFY_FLAG,PRQ_DELETE_FLAG,");
				  loadQuery.append("HOUSE_CREATE_FLAG,HOUSE_MODIFY_FLAG,HOUSE_DELETE_FLAG,MASTER_CREATE_FLAG,");
				  loadQuery.append("MASTER_MODIFY_FLAG,MASTER_DELETE_FLAG,DO_CREATE_FLAG,CREDIT_CHECK_FLAG,BB_FLAG,");
				  loadQuery.append("BB_MODIFY_FLAG,MASTER_CLOSE_FLAG FROM FS_FR_CUSTOMER_NOTIF WHERE");
				  loadQuery.append(" CUSTOMER_ID=? AND TERMINAL_ID=?");
			      
				  pStmt		=		connection.prepareStatement(loadQuery.toString());

				  pStmt.setString(1,customerModel.getCustomerId());
				  pStmt.setString(2,customerModel.getTerminalId());

                  rs  =  pStmt.executeQuery();

				  while(rs.next())
				  {
				      customerModel.setPrqCreateFlag(rs.getString(1));
                      customerModel.setPrqModifyFlag(rs.getString(2));
					  customerModel.setPrqDeleteFlag(rs.getString(3));
					  customerModel.setHouseCreateFlag(rs.getString(4));
					  customerModel.setHouseModifyFlag(rs.getString(5));
					  customerModel.setHouseDeleteFlag(rs.getString(6));
					  customerModel.setMasterCreateFlag(rs.getString(7));
					  customerModel.setMasterModifyFlag(rs.getString(8));
					  customerModel.setMasterDeleteFlag(rs.getString(9));
					  customerModel.setDoCreateFlag(rs.getString(10));
					  customerModel.setCreditFlag(rs.getString(11));
					  customerModel.setBBFlag(rs.getString(12));
					  customerModel.setBBModifyFlag(rs.getString(13));
					  customerModel.setMasterCloseFlag(rs.getString(14));
				      

			          
			      }
                   if(rs != null) 
                   {
                   	rs.close();
                   }
				   return customerModel;
			}
	         catch(SQLException sqlEx)
		     {
		        throw new EJBException("Exception while loading Customer Notify Details \n   "  + sqlEx);
         } 
	         finally
		     {
			   ConnectionUtil.closeConnection(connection,pStmt);	
		    }
	} //end of method 
	  
  /**
   * 
   * @param customerModel
   * @throws javax.ejb.EJBException
   */
	public void store(CustomerModel customerModel) throws SQLException,EJBException 
	{
		updateCustomerMaster(customerModel);
		/*if(customerModel.getUpgradeFlag())
		{  
		   removeCustomerNotify(customerModel.getCustomerId(),customerModel.getTerminalId());
		   insertCustomerNotifDetails(customerModel);
		}
		else 
    {
		   updateCustomerNotify(customerModel); 
    }*/
	
	}//end of method


	//JS
  /**
   * 
   * @param costAddressListME
   * @param costAddressListM
   * @param customerId
   * @param terminalId
   * @throws javax.ejb.EJBException
   */
	public void store(ArrayList costAddressListME,ArrayList costAddressListM,String customerId,String terminalId) throws EJBException
	{
		
		try{
				
				
				  if(costAddressListME!=null && costAddressListME.size() > 0){
					  int costAddrMESize	=	costAddressListME.size();
					 for(int i=0;i<costAddrMESize;i++){
						Address addObj	=	(Address)costAddressListME.get(i);
						
							if(addObj.getDelFlag().equals("Y")){
								updateDeleteFlagStatusifdeleteFlagN(addObj,customerId,terminalId);
							}else{
								updateDeleteFlagStatusifdeleteFlagY(addObj,customerId,terminalId);
								updateDeleteFlagStatusifdeleteFlagAY(addObj,customerId,terminalId);
							} 
						}
					}		
				

			}catch(Exception exp){
				//Logger.error(FILE_NAME," store(ArrayList costAddressListME)--> "+exp.toString());
        logger.error(FILE_NAME+" store(ArrayList costAddressListME)--> "+exp.toString());
				throw new EJBException(" store(ArrayList costAddressListME)--> "+exp);
			}

	
	}//end of method

  /**
   * 
   * @param addObj
   * @param customerId
   * @param terminalId
   * @throws javax.ejb.EJBException
   */
	private void updateDeleteFlagStatusifdeleteFlagN(Address addObj,String customerId,String terminalId) throws SQLException{
			
				Statement		stmt		=	null;
				//ResultSet		rs			=	null;//Commented By RajKumari on 23-10-2008 for Connection Leakages.
				String			sqlQry		=	"UPDATE FS_FR_CUSTOMER_ADDRESS SET DEL_FLAG='Y' WHERE CUSTOMERID= '"+customerId +"' AND TERMINALID='"+terminalId +"' AND CUSTOMERADDRESSID="+ addObj.getAddressId() +" ";

	//Logger.info(FILE_NAME," updateDeleteFlagStatusifdeleteFlagN(Address addObj) sqlQry--> "+sqlQry);		
				Connection		connection	=	null;
				
				try{
					connection	=	this.getConnection();
					stmt		=	connection.createStatement();
					stmt.executeUpdate(sqlQry);

				}catch(Exception exp){
					//Logger.error(FILE_NAME," updateDeleteFlagStatusifdeleteFlagN(Address addObj)-->"+exp.toString());
          logger.error(FILE_NAME+" updateDeleteFlagStatusifdeleteFlagN(Address addObj)-->"+exp.toString());
						throw new EJBException(" updateDeleteFlagStatusifdeleteFlagN(Address addObj)-->"+exp);
				}finally{
					try{
					/*	if(rs!=null){
							rs.close();
						}*///Commented By RajKumari on 23-10-2008 for Connection Leakages.
            if(stmt!=null){
							stmt.close();
						}if(connection!=null){
							connection.close();
						}
					}catch(Exception exp){
						//Logger.error(FILE_NAME," finally block updateDeleteFlagStatusifdeleteFlagN(Address addObj)-->"+exp.toString());
            logger.error(FILE_NAME+" finally block updateDeleteFlagStatusifdeleteFlagN(Address addObj)-->"+exp.toString());
						throw new SQLException(" finally block updateDeleteFlagStatusifdeleteFlagN(Address addObj)-->"+exp);
					}
				}
	
	}

  /**
   * 
   * @param addObj
   * @param customerId
   * @param terminalId
   * @throws javax.ejb.EJBException
   */
	private void updateDeleteFlagStatusifdeleteFlagY(Address addObj,String customerId,String terminalId) throws SQLException{
			
		//Logger.info(FILE_NAME," 	customerId-->"+customerId);
				Statement		stmt		=	null;
				//ResultSet		rs			=	null;//Commented By RajKumari on 23-10-2008 for Connection Leakages.
				String			sqlQry		=	"UPDATE FS_FR_CUSTOMER_ADDRESS SET CONTACTNAME='"+addObj.getContactName()+"',DESIGNATION='"+addObj.getDesignation()+"',DEL_FLAG='"+addObj.getDelFlag()+"',ADDRESS_TYPE='"+addObj.getAdddressType()+"' WHERE CUSTOMERID= '"+customerId +"' AND TERMINALID='"+terminalId +"' AND CUSTOMERADDRESSID="+ addObj.getAddressId() +" ";
		
		//		String				sqlQryStatusAY	=	"UPDATE FS_ADDRESS SET ADDRESSLINE1=?,ADDRESSLINE2=?,CITY=?,STATE=?,ZIPCODE=?,COUNTRYID=?,PHONENO=?,EMAILID=? WHERE ADDRESSID=?";//"UPDATE FS_ADDRESS SET DEL_FLAG=? WHERE CUSTOMERID=? AND TERMINALID=? AND CUSTOMERADDRESSID=?";

	//Logger.info(FILE_NAME," updateDeleteFlagStatusifdeleteFlagY(Address addObj) sqlQry--> "+sqlQry);		
				Connection		connection	=	null;
				
				try{
					connection	=	this.getConnection();
					stmt		=	connection.createStatement();
					stmt.executeUpdate(sqlQry);

				}catch(Exception exp){
					//Logger.error(FILE_NAME," updateDeleteFlagStatusifdeleteFlagY(Address addObj)-->"+exp.toString());
          logger.error(FILE_NAME+" updateDeleteFlagStatusifdeleteFlagY(Address addObj)-->"+exp.toString());
						throw new SQLException(" updateDeleteFlagStatusifdeleteFlagY(Address addObj)-->"+exp);
				}finally{
					try{
						/*if(rs!=null){
							rs.close();
						}*///Commented By RajKumari on 23-10-2008 for Connection Leakages.
            if(stmt!=null){
							stmt.close();
						}if(connection!=null){
							connection.close();
						}
					}catch(Exception exp){
						//Logger.error(FILE_NAME," finally block updateDeleteFlagStatusifdeleteFlagY(Address addObj)-->"+exp.toString());
            logger.error(FILE_NAME+" finally block updateDeleteFlagStatusifdeleteFlagY(Address addObj)-->"+exp.toString());
						throw new EJBException(" finally block updateDeleteFlagStatusifdeleteFlagY(Address addObj)-->"+exp);
					}
				}
	
	}
  /**
   * 
   * @param addObj
   * @param customerId
   * @param terminalId
   * @throws javax.ejb.EJBException
   */
	private void updateDeleteFlagStatusifdeleteFlagAY(Address addObj,String customerId,String terminalId) throws SQLException{
			
				Statement		stmt		=	null;
				//ResultSet		rs			=	null;//Commented By RajKumari on 23-10-2008 for Connection Leakages.
				String			sqlQry		=	"UPDATE FS_ADDRESS SET ADDRESSLINE1='"+addObj.getAddressLine1()+"',ADDRESSLINE2='"+addObj.getAddressLine2()+"',CITY='"+addObj.getCity()+"',STATE='"+addObj.getState()+"',ZIPCODE='"+addObj.getZipCode()+"',COUNTRYID='"+addObj.getCountryId()+"',PHONENO='"+addObj.getPhoneNo()+"',EMAILID='"+addObj.getEmailId()+"' WHERE ADDRESSID="+ addObj.getAddressId() +" ";		
				Connection		connection	=	null;
				
		//Logger.info(FILE_NAME," updateDeleteFlagStatusifdeleteFlagN(Address addObj) sqlQry--> "+sqlQry);		
				
				
				try{
					connection	=	this.getConnection();
					stmt		=	connection.createStatement();
					stmt.executeUpdate(sqlQry);

				}catch(Exception exp){
					//Logger.error(FILE_NAME," updateDeleteFlagStatusifdeleteFlagAY(Address addObj)-->"+exp.toString());
          logger.error(FILE_NAME+" updateDeleteFlagStatusifdeleteFlagAY(Address addObj)-->"+exp.toString());
						throw new SQLException(" updateDeleteFlagStatusifdeleteFlagAY(Address addObj)-->"+exp);
				}finally{
					try{
					/*	if(rs!=null){
							rs.close();
						}*///Commented By RajKumari on 23-10-2008 for Connection Leakages.
            if(stmt!=null){
							stmt.close();
						}if(connection!=null){
							connection.close();
						}
					}catch(Exception exp){
						//Logger.error(FILE_NAME," finally block updateDeleteFlagStatusifdeleteFlagN(Address addObj)-->"+exp.toString());
            logger.error(FILE_NAME+" finally block updateDeleteFlagStatusifdeleteFlagN(Address addObj)-->"+exp.toString());
						throw new EJBException(" finally block updateDeleteFlagStatusifdeleteFlagN(Address addObj)-->"+exp);
					}
				}
	
	}

  /**
   * 
   * @param addressId
   * @param customerId
   * @param terminalId
   * @param customerModel
   * @throws javax.ejb.EJBException
   */
	public void create(int addressId,String customerId,String terminalId,Address customerModel)throws SQLException{
			   
			   PreparedStatement		pStmt			  = null;	
			   String					sqlQry			  = "INSERT INTO FS_FR_CUSTOMER_ADDRESS ( CUSTOMERID,TERMINALID,CUSTOMERADDRESSID,CONTACTNAME, DESIGNATION,DEL_FLAG,ADDRESS_TYPE) VALUES(?,?,?,?,?,?,?)";
			   Connection				connection		  = null;

//Logger.info(FILE_NAME," create(int addressId,String customerId,CustomerModel custModelObj) Method sqlQry is---> "+sqlQry);
//Logger.info(FILE_NAME,"  customerModel.getDelFlag "+customerModel.getDelFlag());			 
			   try
			   {
             
				  connection	=	getConnection();
					if(customerModel.getDelFlag().equals("N")){
					  pStmt			=	connection.prepareStatement(sqlQry);
					  pStmt.setString(1,customerId);
					  pStmt.setString(2,terminalId);
					  pStmt.setInt(3,addressId);
					  pStmt.setString(4,customerModel.getContactName());
					  pStmt.setString(5,customerModel.getDesignation());
					  pStmt.setString(6,customerModel.getDelFlag());
					  pStmt.setString(7,customerModel.getAdddressType());
					  
					  pStmt.executeUpdate();
					}
			   }catch(SQLException sqlEx){
					throw new SQLException("SQLException while updating Customer Details : \n"+ sqlEx);
			   }finally{
						ConnectionUtil.closeConnection(connection,pStmt);
			   }
	
	}

//End of Modify JS


   private void updateCustomerMaster(CustomerModel customerModel)  throws SQLException,EJBException
	{
	      PreparedStatement			pStmt			= null;	
	      StringBuffer				storeQuery		= new StringBuffer();
		  ResultSet					rs				= null;	
		  Connection				connection		= null;
		  String					query			= null;
       String countSlNo = null;
      ArrayList       custDtls=new ArrayList();
       int k =0 ;

		 try
		 {
		      connection	=	getConnection();
		      storeQuery.append("UPDATE FS_FR_CUSTOMERMASTER SET CUSTOMERID=?,COMPANYNAME=?,TERMINALID=?,CONTACTNAME=?,");
			  storeQuery.append("DESIGNATION=?,CUSTOMERADDRESSID=?,NOTES=?,ABBRNAME=?,REGISTERED=?,CURRENCY=?,CREDITDAYS=?,");
			  //storeQuery.append("CREDITLIMIT=?,SALESMANID=?,INDICATOR=?,CUSTOMERTYPE=?,OPERATIONS_EMAILID=?,CORPCUSTOMERID=?");
			  storeQuery.append("CREDITLIMIT=?,INDICATOR=?,CUSTOMERTYPE=?,OPERATIONS_EMAILID=?,CORPCUSTOMERID=?,");
              //@@ Srivegi added on 20050224 (AES-SED)
              storeQuery.append("CONTACTPERSONLASTNAME =?,EISSSN =?,KNOWNSHIPPER =? ");
			  //@@ 20050224
			  //@@ Suneetha added on 20050309 for Bulk Invoicing
			  storeQuery.append(",BULKINVOICEREQUIRED =?,PAYMENTTERMS=?,DEVISION=? ,SALESPERSON=?"); // Salesperson Added by Gowtham 	
			  //@@ 20050309 for Bulk Invoicing
			  storeQuery.append(" WHERE CUSTOMERID=? AND TERMINALID=? ");
			  pStmt   =   connection.prepareStatement(storeQuery.toString());

			  pStmt.setString(1,customerModel.getCustomerId());
			  pStmt.setString(2,customerModel.getCompanyName());
			  pStmt.setString(3,customerModel.getTerminalId());
			  pStmt.setString(4,customerModel.getContactName());
			  pStmt.setString(5,customerModel.getDesignation());
			  pStmt.setInt(6,customerModel.getCustomerAddressId());
			  pStmt.setString(7,customerModel.getNotes());
			  pStmt.setString(8,customerModel.getAbbrName());
			  pStmt.setString(9,customerModel.getRegistered());
			  pStmt.setString(10,customerModel.getCurrency());
			  pStmt.setInt(11,customerModel.getCreditDays());
			  pStmt.setDouble(12,customerModel.getCreditLimit());
			 // pStmt.setString(13,customerModel.getSCode());
			  pStmt.setString(13,customerModel.getIndicator());
			  pStmt.setString(14,customerModel.getTypeOfCustomer());
			  pStmt.setString(15,customerModel.getOpEmailId());
			  pStmt.setString(16,customerModel.getCorpCustomerId());
              //@@ Srivegi added on 20050224 (AES-SED)
              pStmt.setString(17,customerModel.getContactLastName());
			  pStmt.setString(18,customerModel.getEINSSNNo());
			  pStmt.setString(19,customerModel.getcustType());
              //@@ 20050224

			  //@@ Suneetha replaced on 20050309 for Bulk Invoicing
	  			  //pStmt.setString(20,customerModel.getCustomerId());
				  //pStmt.setString(21,customerModel.getTerminalId());
			  pStmt.setString(20,customerModel.getBulkInvoiceRequired());
        pStmt.setString(21,customerModel.getPaymentTerms());
        pStmt.setString(22,customerModel.getDivision());
        pStmt.setString(23,customerModel.getSalesPersonCode()); // Added by Gowtham for salesperson code
        
			  pStmt.setString(24,customerModel.getCustomerId());
			  pStmt.setString(25,customerModel.getTerminalId());
		  	  //@@ 20050309 for Bulk Invoicing

			  if(pStmt.executeUpdate()!=1)
			  {
				  throw new EJBException("Error While Updating Customer Details");
			  }
        //Added By RajKumari on 23-10-2008 for Connection Leakages.
        if(pStmt!=null)
        {
          pStmt.close();
          pStmt = null;
        }
        countSlNo = "SELECT COUNT(*)  FROM QMS_CUST_CONTACTDTL WHERE CUSTOMERID=? AND TERMINALID=? " ;
        pStmt=connection.prepareStatement(countSlNo);
        
        pStmt.setString(1,customerModel.getCustomerId());
        logger.info("pStmt"+pStmt);
         pStmt.setString(2,customerModel.getTerminalId());
        rs = pStmt.executeQuery();
       
       if(rs.next())
       {
           //logger.info("rs"+rs);
          k  =  rs.getInt(1);
           // logger.info("inrs k"+k);
       }  
        // logger.info("k"+k);
         if(rs!=null)
          rs.close();
          if(pStmt!=null)
          pStmt.close();
          
			  query="DELETE FROM QMS_CUST_CONTACTDTL WHERE CUSTOMERID=? AND TERMINALID=?";
			  if(pStmt!=null)
				  pStmt=null;
        
        pStmt=connection.prepareStatement(query);
        pStmt.setString(1,customerModel.getCustomerId());
        pStmt.setString(2,customerModel.getTerminalId());
        pStmt.executeUpdate();
        
      if(rs!=null)
      rs.close();
        if(pStmt!=null)
          pStmt.close();
        custDtls=customerModel.getContactDtl();
       
        query="INSERT INTO QMS_CUST_CONTACTDTL(CUSTOMERID,TERMINALID,ADDRTYPE,CONTACTPERSON,DESIGNATION,DEPT,ZIPCODE,CONTACTNO,FAX,EMAILID,CONTACTID,SL_NO)  VALUES(?,?,?,?,?,?,?,?,?,?,QMS_CUSTCONTACTIDS.NEXTVAL,?)";
        
        pStmt=connection.prepareStatement(query);
        int custDtlSize		=	custDtls.size();
        for(int i=0;i<custDtlSize;++i)
        {
           CustContactDtl custDtl=(CustContactDtl)custDtls.get(i);
           pStmt.setString(1,customerModel.getCustomerId());
           pStmt.setString(2,customerModel.getTerminalId());
           pStmt.setString(3,custDtl.getAddrType());
           pStmt.setString(4,custDtl.getContactPerson());
           pStmt.setString(5,custDtl.getDesignation());
           pStmt.setString(6,custDtl.getDept());
           pStmt.setString(7,custDtl.getZipCode());
           pStmt.setString(8,custDtl.getContact());
           pStmt.setString(9,custDtl.getFax());
           pStmt.setString(10,custDtl.getEmail());
           //logger.info("custDtl.getContactId()"+custDtl.getContactId());
          // pStmt.setInt(11,custDtl.getContactId()!=-1?custDtl.getContactId():i);//@@Modified by Kameswari for the WPBN issue-87145
          // pStmt.setInt(11,custDtl.getContactId()!=-1?custDtl.getContactId():k++);
           //@@Modified by kiran.v on 29/07/2011 for Wpbn Issue-258036
           pStmt.setInt(11,i);
           pStmt.executeUpdate();
        }
        

		 }
		 catch(SQLException sqlEx)
		 {
		      throw new SQLException("SQLException while updating Customer Details : \n"+ sqlEx);
		 }
		 finally
		 {
			ConnectionUtil.closeConnection(connection,pStmt);
		 }
   }//end of method

  /**
   * 
   * @param customerModel
   * @throws javax.ejb.EJBException
   */
   private void updateCustomerNotify(CustomerModel customerModel)   throws EJBException,EJBException
   {
        PreparedStatement	pStmt				= null;	
	    StringBuffer		storeQuery			= new StringBuffer();    
        ResultSet			rs					= null;
		Connection			connection			= null;

	
		boolean				isCustomerExists	= false;	
		try
	    {
			 connection	=	getConnection();

			 String query	=	"SELECT CUSTOMER_ID FROM FS_FR_CUSTOMER_NOTIF WHERE CUSTOMER_ID=? AND TERMINAL_ID=? " ;
			 pStmt   =   connection.prepareStatement(query); 

			 pStmt.setString(1,customerModel.getCustomerId());
			 pStmt.setString(2,customerModel.getTerminalId());
			  rs   =   pStmt.executeQuery();
			    
			     if(rs.next())
           {
            isCustomerExists  =  true;
           }
			 //Added By RajKumari on 23-10-2008 for Connection Leakages.
       if(pStmt!=null)
       {
         pStmt.close();
       }
			 pStmt				= null;	
	         rs					= null;

			if(isCustomerExists)
			{

			 storeQuery.append("UPDATE FS_FR_CUSTOMER_NOTIF SET CUSTOMER_ID=?,TERMINAL_iD=?,PRQ_CREATE_FLAG=?,PRQ_MODIFY_FLAG=?,");
			 storeQuery.append("PRQ_DELETE_FLAG=?,HOUSE_CREATE_FLAG=?,HOUSE_MODIFY_FLAG=?,HOUSE_DELETE_FLAG=?,MASTER_CREATE_FLAG=?,");
			 storeQuery.append("MASTER_MODIFY_FLAG=?,MASTER_DELETE_FLAG=?,DO_CREATE_FLAG=?,CREDIT_CHECK_FLAG=?,BB_FLAG=?,");
			 storeQuery.append("BB_MODIFY_FLAG=?,MASTER_CLOSE_FLAG=?  WHERE CUSTOMER_ID=? AND TERMINAL_ID=? " );

        pStmt   =   connection.prepareStatement(storeQuery.toString()); 
			  pStmt.setString(1,customerModel.getCustomerId());
			  pStmt.setString(2,customerModel.getTerminalId());
			  pStmt.setString(3,customerModel.getPrqCreateFlag());
			  pStmt.setString(4,customerModel.getPrqModifyFlag());
			  pStmt.setString(5,customerModel.getPrqDeleteFlag());
			  pStmt.setString(6,customerModel.getHouseCreateFlag());
			  pStmt.setString(7,customerModel.getHouseModifyFlag());
			  pStmt.setString(8,customerModel.getHouseDeleteFlag());
			  pStmt.setString(9,customerModel.getMasterCreateFlag());
			  pStmt.setString(10,customerModel.getMasterModifyFlag());
			  pStmt.setString(11,customerModel.getMasterDeleteFlag());
			  pStmt.setString(12,customerModel.getDoCreateFlag());
			  pStmt.setString(13,customerModel.getCreditFlag());
			  pStmt.setString(14,customerModel.getBBFlag());
			  pStmt.setString(15,customerModel.getBBModifyFlag());
			  pStmt.setString(16,customerModel.getMasterCloseFlag());
			  pStmt.setString(17,customerModel.getCustomerId());
			  pStmt.setString(18,customerModel.getTerminalId());
      
              pStmt.executeUpdate(); 
			}
			else
			{
				insertCustomerNotifDetails(customerModel);
			}
/**	        if(pStmt.executeUpdate()!=1)
				    throw new EJBException("Error While Updating Customer Details"); */
		}catch(SQLException sqlEx)
		 {
		    throw new EJBException("SQLException while updating Customer Details : \n"+ sqlEx);
		 }finally
		{
			 ConnectionUtil.closeConnection(connection,pStmt,rs);
		}
   }//end of method
		  
  /**
   * 
   * @param customerId
   * @param terminalId
   * @throws javax.ejb.EJBException
   */
	public void remove(String customerId,String terminalId) throws SQLException,EJBException
	{
			
			//removeCustomerMapping(customerId, terminalId);
			//removeCustomerNotify(customerId,terminalId);
			//JS
			//removeCustomerMoreDetails(customerId,terminalId);
			removeCustomerDetails(customerId,terminalId);
			
			

	}//end of method

//js
  /**
   * 
   * @param customerId
   * @param terminalId
   * @throws javax.ejb.EJBException
   */
	private void removeCustomerNotify(String customerId,String terminalId) throws EJBException,EJBException
	{
	    PreparedStatement	pStmt			= null;	
	    StringBuffer		removeQuery		= new StringBuffer();    
        Connection 		connection		= null;    
		 
		 
		 try
		 {
		       connection = getConnection();
		       removeQuery.append("DELETE FS_FR_CUSTOMER_NOTIF WHERE CUSTOMER_ID=? AND TERMINAL_ID=?");
			//Logger.info(FILE_NAME," removeCustomerNotify() removeQuery.toString() -->"+removeQuery.toString());			   
            pStmt  =   connection.prepareStatement(removeQuery.toString());
		       pStmt.setString(1,customerId);
			   pStmt.setString(2,terminalId);
			   pStmt.executeUpdate();	
/**		       if(pStmt.executeUpdate()!=1)
				     throw new EJBException("Unable to Delete Customer Notif Details");  */
		 }catch(SQLException sqlEx)
		  {
		     throw new EJBException(sqlEx.toString());
		  }
		  finally
		 {
			  ConnectionUtil.closeConnection(connection,pStmt);
		 }
	}//end of method

/*
	private void  removeCustomerDetails(String customerId,String terminalId)  throws EJBException,EJBException
    {
	     PreparedStatement	pStmt			= null;	
	     StringBuffer		removeQuery		= new StringBuffer();
		 Connection 		connection		= null;    
        
		 
		 
		 try
		 {
		       connection	=	getConnection();
		       removeQuery.append("DELETE FS_FR_CUSTOMERMASTER  WHERE CUSTOMERID=? AND TERMINALID=?");
	Logger.info(FILE_NAME," removeCustomerDetails() removeQuery.toString() -->"+removeQuery.toString());			   
		   
               pStmt  =   connection.prepareStatement(removeQuery.toString());
		       pStmt.setString(1,customerId);
			   pStmt.setString(2,terminalId);
		       
			   if(pStmt.executeUpdate()!=1)
				     throw new EJBException("Unable to Delete Customer Notif Details");
		 }catch(SQLException sqlEx)
		  {
		     throw new EJBException(sqlEx.toString());
		  }
		  finally
		{
			  ConnectionUtil.closeConnection(connection,pStmt);
		}
	}//end of method
*/
  /**
   * 
   * @param customerId
   * @param terminalId
   * @throws javax.ejb.EJBException
   */
	private void  removeCustomerDetails(String customerId,String terminalId)  throws SQLException,EJBException
    {
	     Statement			stmt			= null;	
	     String				removeQuery		= " DELETE FS_FR_CUSTOMERMASTER  WHERE CUSTOMERID='"+customerId+"' AND TERMINALID='"+terminalId+"'";
		 String				removeCustDtls	= "	DELETE QMS_CUST_CONTACTDTL	WHERE CUSTOMERID='"+customerId+"' AND TERMINALID='"+terminalId+"'";
		 Connection 		connection		= null;
    	
  
		 
		 
		 try
		 {
		       connection	=	getConnection();
			   //Logger.info(FILE_NAME," removeCustomerDetails() removeCustDtls -->"+removeCustDtls);
			   stmt	=	connection.createStatement();
			   stmt.executeUpdate(removeCustDtls);

	           
			   if(stmt!=null)
         {
           stmt.close();//Added By RajKumari on 23-10-2008 for Connection Leakages.
           stmt=null;
         }
				   
			   //Logger.info(FILE_NAME," removeCustomerDetails() removeQuery -->"+removeQuery);			   
			   
               stmt  =   connection.createStatement();
			   int  rs	=	stmt.executeUpdate(removeQuery);

				
		 }catch(SQLException sqlEx)
		  {
		     throw new EJBException(sqlEx.toString());
		  }
		  finally
		{
			  ConnectionUtil.closeConnection(connection,stmt);
		}
	}//end of  removeCustomerDetails method

  /**
   * 
   * @param customerId
   * @param terminalId
   * @throws javax.ejb.EJBException
   */
private void removeCustomerMapping(String customerId,String terminalId) throws EJBException,EJBException
{
		PreparedStatement	pStmt			= null;	
	    Connection 			connection		= null;    
		 
		 
	try
	{
		//System.out.println("This is inside the RemoveCustomerMapping method.... ");
		String sql ="DELETE FS_FR_CUSTOMERS_MAPPING  WHERE CUSTOMER_ID1=? AND TERMINAL_ID1=?" ;
		connection = getConnection();
		pStmt  =   connection.prepareStatement(sql);
		pStmt.setString(1,customerId);
		pStmt.setString(2,terminalId);
		pStmt.executeUpdate();	
		//System.out.println("This is inside the RemoveCustomerMapping method....1 ");
	}
	catch(SQLException sqlEx)
	{
		
		throw new EJBException(sqlEx.toString());
		
	}
	finally
	{
		ConnectionUtil.closeConnection(connection,pStmt);
	}
	//System.out.println("This is inside the RemoveCustomerMapping method....2 ");
}//end of method


//JS remove Customer more Address details removeCustomerMoreDetails(customerId,terminalId);
	
/*	private void  removeCustomerMoreDetails(String customerId,String terminalId)  throws EJBException,EJBException
    {
	     PreparedStatement	pStmt			= null;	
	     StringBuffer		removeQuery		= new StringBuffer();
		 Connection 		connection		= null;    
        
		 
		 
		 try
		 {
		       connection	=	getConnection();
		       removeQuery.append("DELETE FS_FR_CUSTOMER_ADDRESS  WHERE CUSTOMERID=? AND TERMINALID=?");
	Logger.info(FILE_NAME," removeCustomerMoreDetails() removeQuery.toString() -->"+removeQuery.toString());			   
			   
               pStmt  =   connection.prepareStatement(removeQuery.toString());
		       pStmt.setString(1,customerId);
			   pStmt.setString(2,terminalId);
		      
			   if(pStmt.executeUpdate()!=1)
				     throw new EJBException("Unable to Delete Customer More Address details ");
		 }catch(SQLException sqlEx)
		  {
		     throw new EJBException(sqlEx.toString());
		  }
		  finally
		{
			  ConnectionUtil.closeConnection(connection,pStmt);
		}
	}//end of  removeCustomerMoreDetails method
*/
		
  /**
   * 
   * @param customerId
   * @param terminalId
   * @throws javax.ejb.EJBException
   */
	private void  removeCustomerMoreDetails(String customerId,String terminalId)  throws EJBException,EJBException
    {
	     Statement			stmt			= null;	
	     String				removeQuery		= " DELETE FS_FR_CUSTOMER_ADDRESS  WHERE CUSTOMERID='"+customerId+"' AND TERMINALID='"+terminalId+"'";
		 Connection 		connection		= null;
    	
  
		 
		 
		 try
		 {
		       connection	=	getConnection();
	          // Logger.info(FILE_NAME," removeCustomerMoreDetails() removeQuery -->"+removeQuery);			   
			   
               stmt  =   connection.createStatement();
			   int  rs	=	stmt.executeUpdate(removeQuery);

		 }catch(SQLException sqlEx)
		  {
		     throw new EJBException(sqlEx.toString());
		  }
		  finally
		{
			  ConnectionUtil.closeConnection(connection,stmt);
		}
	}//end of  removeCustomerMoreDetails method


  /**
   * 
   * @return Connection
   * @throws java.sql.SQLException
   */
	private Connection getConnection() throws SQLException
	{
		return dataSource.getConnection();
	}

}
