package com.foursoft.etrans.setup.terminal.ejb.bmp;
import com.foursoft.etrans.common.bean.Address;
import com.foursoft.etrans.common.dao.AddressDAO;
import com.foursoft.etrans.setup.terminal.bean.TerminalRegJspBean;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
//import com.foursoft.esupply.common.util.Logger;
import org.apache.log4j.Logger;
import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ejb.EntityBean;
import javax.ejb.EntityContext;
import javax.ejb.FinderException;
import javax.ejb.ObjectNotFoundException;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import javax.sql.DataSource;
/**
 * @author Avinash P
 * @version 1.6
 * @date 20041216
 */
public class TerminalRegistrationEntityBean implements EntityBean 
{
// @@ Commented by G.Srinivas for TogetherArchitect on 20050111
 // private static final String FILE_NAME = "TerminalRegistrationEntityBean.java";
  private transient DataSource dataSource = null;
  

  private	EntityContext		context;
  private 	TerminalRegJspBean	terminalRegObj	=	null;
  private   String				terminalId		=   null;
  
  /**
   * 
   */
   //@@ Srivegi Modified on 20050419 (Invoice-PR) - USESTOCKEDINVOICE  field is added int he below query
  private static final String sqlCreateTerminal		=	" INSERT INTO FS_FR_TERMINALMASTER"+
														" ("+
														" TERMINALID, COMPANYID, CONTACTNAME, DESIGNATION,"+
														" NOTES, CONTACTADDRESSID, AGENTIND, IATACODE,"+
														" ACCOUNTNO, TAXREGNO, BANKNAME, BRANCHNAME,"+
														" BANKACCTNO, BANKADDRESS, BANKCITY, DISCREPANCY,"+
														" INTERESTRATE, INVOICECATEGORY, TERMINALTYPE, OPER_ADMIN_FLAG,"+
														" EMAIL_ACTVE_FLAG, TIME_ZONE, CC_SHIPMNT_FLAG, LAST_UPDTD_TIMESTMP,"+
														" SHIPMENTMODE, SERVER_TIME_DIFF, CONV_WGT_SCAL,"+
														" USESTOCKEDINVOICE,"+
                            " CHILDTERMINAL_FLAG,"+//added by I.V.Sekhar
							 // modified by phani sekhar for wpbn 170758 on 20090626
                            " FREQUENCY,CARRIER,TRANSITTIME,RATEVALIDITY,INVALIDATE,MARGIN_TYPE,DISCOUNT_TYPE)"+//Added By RajKumari on 11/28/2008 for 146448
														" VALUES"+
														" ("+
														" ?, ?, ?, ?,"+
														" ?, ?, ?, ?,"+
														" ?, ?, ?, ?,"+
														" ?, ?, ?, ?,"+
														" ?, ?, ?, ?,"+
														" ?, ?, ?, ?,"+
														" ?, ?, ?,?,?,"+
                            " ?, ?, ?, ? ,'F'"+//Added By RajKumari on 11/28/2008 for 146448
														",?,? )";// Modified by phani sekhar  for wpbn 170758 on 20090626


  /**
   * 
   */
  private static final String sqlFindTerminalByPK	=	" SELECT COUNT(*) FROM FS_FR_TERMINALMASTER WHERE TERMINALID = ?";


  /**
   * 
   */
 //@@ Srivegi Modified on 20050419 (Invoice-PR) - USESTOCKEDINVOICE  field is added int he below query
  private static final String sqlLoadTerminal		=	" SELECT"+
														" TERMINALID, COMPANYID, CONTACTNAME, DESIGNATION,"+
														" NOTES, CONTACTADDRESSID, AGENTIND, IATACODE,"+
														" ACCOUNTNO, TAXREGNO, BANKNAME, BRANCHNAME,"+
														" BANKACCTNO, BANKADDRESS, BANKCITY, DISCREPANCY,"+
														" INTERESTRATE, INVOICECATEGORY, TERMINALTYPE, OPER_ADMIN_FLAG,"+
														" EMAIL_ACTVE_FLAG, TIME_ZONE, CC_SHIPMNT_FLAG, LAST_UPDTD_TIMESTMP,"+
                            " SHIPMENTMODE, SERVER_TIME_DIFF, CONV_WGT_SCAL ,USESTOCKEDINVOICE,CHILDTERMINAL_FLAG "+
														" FROM FS_FR_TERMINALMASTER WHERE  INVALIDATE='F' AND TERMINALID = ?  ";


  /**
   * 
   */
   //@@ Srivegi Modified on 20050419 (Invoice-PR) - USESTOCKEDINVOICE  field is added int he below query
  private static final String sqlStoreTerminal		=	" UPDATE FS_FR_TERMINALMASTER SET"+
														" COMPANYID = ?, CONTACTNAME = ?, DESIGNATION = ?,"+
														" NOTES = ?, CONTACTADDRESSID = ?, AGENTIND = ?, IATACODE= ?,"+
														" ACCOUNTNO = ?, TAXREGNO = ?, BANKNAME = ?, BRANCHNAME= ?,"+
														" BANKACCTNO = ?, BANKADDRESS = ?, BANKCITY = ?, DISCREPANCY = ?,"+
														" INTERESTRATE = ?, INVOICECATEGORY = ?, TERMINALTYPE = ?, OPER_ADMIN_FLAG = ?,"+
														" EMAIL_ACTVE_FLAG = ?, TIME_ZONE = ?, CC_SHIPMNT_FLAG = ?,"+
														" SHIPMENTMODE = ?, SERVER_TIME_DIFF= ?, CONV_WGT_SCAL= ?,USESTOCKEDINVOICE= ?,CHILDTERMINAL_FLAG=?"+
														" WHERE TERMINALID = ?";

  /**
   * 
   */
  private static final String sqlRemoveTerminal	=	" DELETE FS_FR_TERMINALMASTER WHERE TERMINALID = ?";


  /**
   * 
   * @param terminalRegObj
   * @return 
   * @throws javax.ejb.CreateException
   */
  public String ejbCreate(TerminalRegJspBean terminalRegObj)throws CreateException{
    terminalId			= terminalRegObj.getTerminalId();
    this.terminalRegObj = terminalRegObj;
	createTerminal();
	return terminalId;
  }

  public void ejbPostCreate(TerminalRegJspBean terminalRegObj){
  }

  /**
   * 
   * @param terminalId
   * @return 
   * @throws javax.ejb.FinderException
   */
  public String ejbFindByPrimaryKey(String terminalId)throws FinderException{
    findTerminal(terminalId);
    return terminalId;
  }

  /**
   * 
   */
  public void ejbLoad(){
	   loadTerminal();
  }

  /**
   * 
   */
  public void ejbStore(){
	  storeTerminal();
  }

  /**
   * 
   */
  public void ejbRemove(){
	  removeTerminal();
  }

  /**
   * 
   */
  public void ejbActivate(){
  }

  /**
   * 
   */
  public void ejbPassivate(){
  }

  /**
   * 
   * @param ctx
   */
  public void setEntityContext(EntityContext ctx){
    this.context = ctx;
  }

  /**
   * 
   */
  public void unsetEntityContext(){
    this.context = null;
  }

  /**
   * 
   * @return 
   */
public int getContactAddressId()
{

	return terminalRegObj.getAddress().getAddressId();
}
/**
   * 
   * @throws javax.ejb.CreateException
   */
  private void createTerminal()throws CreateException{

	  Connection			connection			= null;
	  PreparedStatement		psTerminalCreate	= null;
	//@@ Added by subrahmanyam for the pbn id: 207312 on 03-Jun-10
	  Statement		sTCreateTerminalSeq	= null;
	  String 				terminalSeqQry			=	"create sequence "+terminalId+"_SEQ minvalue 1"+
	  													" maxvalue 999999999 start with 1 increment by 1"+
	  													" nocache";
	  //@@Ended by subrahmanyam
	  try{
		 connection		= this.getConnection();
		 psTerminalCreate	= connection.prepareStatement(sqlCreateTerminal);
		 sTCreateTerminalSeq	= connection.createStatement();//@@ Added by subrahmanyam for the pbn id: 207312 on 03-Jun-10
		 psTerminalCreate.setString(1, terminalId);			// creating Address Object
		psTerminalCreate.setString(2, terminalRegObj.getCompanyId());		// String to store companyid
		psTerminalCreate.setString(3, terminalRegObj.getContactName());		// String to store contact name
		psTerminalCreate.setString(4, terminalRegObj.getDesignation());		// String to store designation

		psTerminalCreate.setString(5, terminalRegObj.getNotes());			// String to store notes
		psTerminalCreate.setInt(6, createAddress(terminalRegObj.getAddress()));			// address id
		
		// String to store agent ind.
		if("COMPANY".equals(terminalRegObj.getAgentInd())){
			psTerminalCreate.setString(7,  "c");		
		}
		else
		if("AGENT".equals(terminalRegObj.getAgentInd())){
			psTerminalCreate.setString(7,  "a");		
		}
		else
		if("JOINT VENTURE".equals(terminalRegObj.getAgentInd())){
			psTerminalCreate.setString(7, "j" );		
		}
		else{
			psTerminalCreate.setString(7, terminalRegObj.getAgentInd() );
		}

		psTerminalCreate.setString(8, terminalRegObj.getIataCode());		// String to store IATA code

		psTerminalCreate.setString(9, terminalRegObj.getAccountCode());		// String to store account code
		psTerminalCreate.setString(10, terminalRegObj.getTaxRegNo());		// String to store tax registration number
		psTerminalCreate.setString(11, terminalRegObj.getBankName());		// String to store bank Name
		psTerminalCreate.setString(12, terminalRegObj.getBranchName());		// String to store branch Name

		psTerminalCreate.setString(13, terminalRegObj.getAccountNumber());	// String to store bank Account Number
		psTerminalCreate.setString(14, terminalRegObj.getBankAddress());		// String to store bank Address
		psTerminalCreate.setString(15, terminalRegObj.getBankCity());		// String to store bank city
		psTerminalCreate.setInt(16, terminalRegObj.getDiscrepancy());		// int to store discrepancy
		
		psTerminalCreate.setDouble(17, terminalRegObj.getInterestRate());	// double to store interestRate
		psTerminalCreate.setString(18, terminalRegObj.getInvoiceCategory());	// string to store invoicecategory
		psTerminalCreate.setString(19, terminalRegObj.getTerminalType());	// string to store terminalType

		psTerminalCreate.setString(20, terminalRegObj.getOperationTerminalType());

		psTerminalCreate.setString(21, terminalRegObj.getEmailStatus());
		psTerminalCreate.setString(22, terminalRegObj.getTimeZone());
		psTerminalCreate.setString(23, terminalRegObj.getCollectShipment());
		psTerminalCreate.setTimestamp(24, new java.sql.Timestamp( (new java.util.Date()).getTime()));


		psTerminalCreate.setInt(25, terminalRegObj.getShipmentMode() == null ? 0 
									: Integer.parseInt(terminalRegObj.getShipmentMode()) );

		psTerminalCreate.setString(26, terminalRegObj.getServerTimeDiff());
		psTerminalCreate.setInt(27, terminalRegObj.getWeightScale());
		//@@ Srivegi Added on 20050419 (Invoice-PR)
	    psTerminalCreate.setString(28, terminalRegObj.getStockedInvoiceIdsCheck());
        //@@ 20050419 (Invoice-PR)
    //Added by I.V.Sekhar for Terminal registration
     psTerminalCreate.setString(29,terminalRegObj.getChildTerminalFlag());
    //end//
    
    //Added By RajKumari on 11/28/2008 for 146448 starts
    psTerminalCreate.setString(30,terminalRegObj.getFrequency());
    psTerminalCreate.setString(31,terminalRegObj.getCarrier());
    psTerminalCreate.setString(32,terminalRegObj.getTransitTime());
    psTerminalCreate.setString(33,terminalRegObj.getRateValidity());
   //Added By RajKumari on 11/28/2008 for 146448 ends
    psTerminalCreate.setString(34,terminalRegObj.getMarginType());// added by phani sekhar for wpbn 170758 on 20090626
    psTerminalCreate.setString(35,terminalRegObj.getDiscountType());// added by phani sekhar for wpbn 170758 20090626
		psTerminalCreate.executeUpdate();	
		sTCreateTerminalSeq.executeUpdate(terminalSeqQry);//@@ Added by subrahmanyam for the pbn id: 207312 on 03-Jun-10
	  }
	  catch(Exception e){
		  e.printStackTrace();
		  throw new CreateException(e.getMessage());
	  }
	  finally{
		  try{
        if(psTerminalCreate!=null)
          psTerminalCreate.close();
        if(sTCreateTerminalSeq!=null)
        	sTCreateTerminalSeq.close();

			  if(connection!=null){
				  connection.close();
			  }
		  }
		  catch(SQLException e){
			  throw new EJBException();
		  }
	  }
  }


  /**
   * 
   * @param terminalId
   * @throws javax.ejb.FinderException
   */
  private String findTerminal(String terminalId)throws FinderException{
	  Connection			connection			= null;
	  PreparedStatement		psFindTerminal		= null;
	  ResultSet				rsFindTerminal		= null;
	  boolean				terminalExists		= false;  

	  try{
		connection		= this.getConnection();
		psFindTerminal	= connection.prepareStatement(sqlFindTerminalByPK);

		psFindTerminal.setString(1, terminalId);	

		rsFindTerminal = psFindTerminal.executeQuery();

		if(rsFindTerminal.next())
		{
			if(rsFindTerminal.getInt(1)>0){
				terminalExists = true;
			}

		}
		if(!terminalExists){
			throw new ObjectNotFoundException("Terminal "+terminalId+" not found");
		}
		else{
			 return terminalId;
		}

	  }
	  catch(Exception e){
		  e.printStackTrace();
		  throw new FinderException(e.getMessage());
	  }
	  finally{
		  try{
        if(rsFindTerminal!=null)
          rsFindTerminal.close();
        if(psFindTerminal!=null)
          psFindTerminal.close();
			  if(connection!=null){
				  connection.close();
			  }
		  }
		  catch(SQLException e){
			  throw new EJBException();
		  }
	  }
  }


  /**
   * 
   */
  private void loadTerminal(){
	  Connection			connection			= null;
	  PreparedStatement		psLoadTerminal		= null;
	  ResultSet				rsLoadTerminal		= null;

	  try{
		terminalId = (String)context.getPrimaryKey();
		connection		= this.getConnection();

		psLoadTerminal	= connection.prepareStatement(sqlLoadTerminal);
		
		psLoadTerminal.setString(1, terminalId);	

		rsLoadTerminal = psLoadTerminal.executeQuery();

		if(rsLoadTerminal.next()){
			terminalRegObj = new TerminalRegJspBean();
			terminalRegObj.setTerminalId(terminalId);

			terminalRegObj.setCompanyId(rsLoadTerminal.getString("COMPANYID"));		// String to store companyid
			terminalRegObj.setContactName(rsLoadTerminal.getString("CONTACTNAME"));		// String to store contact name
			terminalRegObj.setDesignation(rsLoadTerminal.getString("DESIGNATION"));		// String to store designation

			terminalRegObj.setNotes(rsLoadTerminal.getString("NOTES"));			// String to store notes
			terminalRegObj.setAddress(loadAddress(rsLoadTerminal.getInt("CONTACTADDRESSID")));
			terminalRegObj.setAgentInd(rsLoadTerminal.getString("AGENTIND"));		// String to store agent ind.
			terminalRegObj.setIataCode(rsLoadTerminal.getString("IATACODE"));		// String to store IATA code

			terminalRegObj.setAccountCode(rsLoadTerminal.getString("ACCOUNTNO"));		// String to store account code
			terminalRegObj.setTaxRegNo(rsLoadTerminal.getString("TAXREGNO"));		// String to store tax registration number
			terminalRegObj.setBankName(rsLoadTerminal.getString("BANKNAME"));		// String to store bank Name
			terminalRegObj.setBranchName(rsLoadTerminal.getString("BRANCHNAME"));		// String to store branch Name

			terminalRegObj.setAccountNumber(rsLoadTerminal.getString("BANKACCTNO"));	// String to store bank Account Number
			terminalRegObj.setBankAddress(rsLoadTerminal.getString("BANKADDRESS"));		// String to store bank Address
			terminalRegObj.setBankCity(rsLoadTerminal.getString("BANKCITY"));		// String to store bank city
			terminalRegObj.setDiscrepancy(rsLoadTerminal.getInt("DISCREPANCY"));		// int to store discrepancy
			terminalRegObj.setIntrestRate(rsLoadTerminal.getDouble("INTERESTRATE"));
			terminalRegObj.setInvoiceCategory(rsLoadTerminal.getString("INVOICECATEGORY"));	// string to store invoicecategory			
			terminalRegObj.setTerminalType(rsLoadTerminal.getString("TERMINALTYPE"));	// string to store terminalType

			terminalRegObj.setOperationTerminalType(rsLoadTerminal.getString("OPER_ADMIN_FLAG"));
			terminalRegObj.setEmailStatus(rsLoadTerminal.getString("EMAIL_ACTVE_FLAG"));
			terminalRegObj.setTimeZone(rsLoadTerminal.getString("TIME_ZONE"));
			terminalRegObj.setCollectShipment(rsLoadTerminal.getString("CC_SHIPMNT_FLAG"));

			terminalRegObj.setShipmentMode(rsLoadTerminal.getString("SHIPMENTMODE"));
			terminalRegObj.setServerTimeDiff(rsLoadTerminal.getString("SERVER_TIME_DIFF"));						
			terminalRegObj.setWeightScale(rsLoadTerminal.getInt("CONV_WGT_SCAL"));	
			//@@ Srivegi Added on 20050419 (Invoice-PR)
			terminalRegObj.setStockedInvoiceIdsCheck(rsLoadTerminal.getString("USESTOCKEDINVOICE"));
      //@@ 20050419 (Invoice-PR) 
      terminalRegObj.setChildTerminalFlag("CHILDTERMINAL_FLAG");//Added By I.V.Sekhar      
		}
	  }
	  catch(Exception e){
		  e.printStackTrace();
		  throw new EJBException(e.getMessage());
	  }
	  finally{
		  try{
        if(rsLoadTerminal!=null)
          rsLoadTerminal.close();
        if(psLoadTerminal!=null)
          psLoadTerminal.close();
			  if(connection!=null){
				  connection.close();
			  }
		  }
		  catch(SQLException e){
			  throw new EJBException();
		  }
	  }
  }


  /**
   * 
   */
  private void storeTerminal(){
		Connection			connection			= null;
		PreparedStatement	psStoreTerminal		= null;
	  try{
		connection			= this.getConnection();
		psStoreTerminal		= connection.prepareStatement(sqlStoreTerminal);

		storeAddress(terminalRegObj.getAddress());
		psStoreTerminal.setString(1, terminalRegObj.getCompanyId());		// String to store companyid
		psStoreTerminal.setString(2, terminalRegObj.getContactName());		// String to store contact name
		psStoreTerminal.setString(3, terminalRegObj.getDesignation());		// String to store designation

		psStoreTerminal.setString(4, terminalRegObj.getNotes());			// String to store notes
		psStoreTerminal.setInt(5, terminalRegObj.getAddress().getAddressId());			// address id		

		if("COMPANY".equals(terminalRegObj.getAgentInd())){
			psStoreTerminal.setString(6,  "c");		
		}
		else
		if("AGENT".equals(terminalRegObj.getAgentInd())){
			psStoreTerminal.setString(6,  "a");		
		}
		else
		if("JOINT VENTURE".equals(terminalRegObj.getAgentInd())){
			psStoreTerminal.setString(6, "j" );		
		}
		else{
			psStoreTerminal.setString(6, terminalRegObj.getAgentInd() );
		}

		psStoreTerminal.setString(7, terminalRegObj.getIataCode());		// String to store IATA code

		psStoreTerminal.setString(8, terminalRegObj.getAccountCode());		// String to store account code
		psStoreTerminal.setString(9, terminalRegObj.getTaxRegNo());		// String to store tax registration number
		psStoreTerminal.setString(10, terminalRegObj.getBankName());		// String to store bank Name
		psStoreTerminal.setString(11, terminalRegObj.getBranchName());		// String to store branch Name

		psStoreTerminal.setString(12, terminalRegObj.getAccountNumber());	// String to store bank Account Number
		psStoreTerminal.setString(13, terminalRegObj.getBankAddress());		// String to store bank Address
		psStoreTerminal.setString(14, terminalRegObj.getBankCity());		// String to store bank city
		psStoreTerminal.setInt(15, terminalRegObj.getDiscrepancy());		// int to store discrepancy
		
		psStoreTerminal.setDouble(16, terminalRegObj.getInterestRate());	// double to store interestRate
		psStoreTerminal.setString(17, terminalRegObj.getInvoiceCategory());	// string to store invoicecategory
		psStoreTerminal.setString(18, terminalRegObj.getTerminalType());	// string to store terminalType
		psStoreTerminal.setString(19, terminalRegObj.getOperationTerminalType());

		psStoreTerminal.setString(20, terminalRegObj.getEmailStatus());
		psStoreTerminal.setString(21, terminalRegObj.getTimeZone());
		psStoreTerminal.setString(22, terminalRegObj.getCollectShipment());

		psStoreTerminal.setInt(23, terminalRegObj.getShipmentMode() == null ? 0 
									: Integer.parseInt(terminalRegObj.getShipmentMode()) );

		psStoreTerminal.setString(24, terminalRegObj.getServerTimeDiff());
		psStoreTerminal.setInt(25, terminalRegObj.getWeightScale());
		//@@ Srivegi Added on 20050419 (Invoice-PR)
		psStoreTerminal.setString(26, terminalRegObj.getStockedInvoiceIdsCheck());
        //@@ 20050419 (Invoice-PR) 
    psStoreTerminal.setString(27,terminalRegObj.getChildTerminalFlag());//Added I.V.Sekhar
		psStoreTerminal.setString(28, terminalId);        
		psStoreTerminal.executeUpdate();	
	  }
	  catch(Exception e){
  		  e.printStackTrace();
		  throw new EJBException(e.getMessage());
	  }
	  finally{
		  try{
        if(psStoreTerminal!=null)
          psStoreTerminal.close();
			  if(connection!=null){
				  connection.close();
			  }
		  }
		  catch(SQLException e){
			  throw new EJBException();
		  }
	  }
  }


  /**
   * 
   */
  private void removeTerminal(){
	Connection			connection			= null;
	PreparedStatement	psRemoveTerminal	= null;

	try{
		connection			= this.getConnection();
		psRemoveTerminal	= connection.prepareStatement(sqlRemoveTerminal);
		psRemoveTerminal.setString(1, terminalId);	
		
		psRemoveTerminal.executeUpdate();
		removeAddress();
	  }
	  catch(Exception e){
		  e.printStackTrace();
		  throw new EJBException(e.getMessage());
	  }
	  finally{
		  try{
        if(psRemoveTerminal!=null)
          psRemoveTerminal.close();
			  if(connection!=null){
				  connection.close();
			  }
		  }
		  catch(SQLException e){
			  throw new EJBException();
		  }
	  }
  }

  /**
   * 
   * @param address
   * @return 
   */
	public int createAddress( com.foursoft.etrans.common.bean.Address address ){  	      
		   AddressDAO addressDAO= new AddressDAO();
		   int addressId = addressDAO.create(address);
		   address.setAddressId(addressId);
		   return addressId;	  
    }	  	
	
  /**
   * 
   * @param addressId
   * @return 
   * @throws java.sql.SQLException
   */
	public Address loadAddress( int addressId )throws SQLException{  	      
		   AddressDAO addressDAO= new AddressDAO();
		   return addressDAO.load(addressId);	  
    }

  /**
   * 
   * @param address
   * @throws java.sql.SQLException
   */
	public void storeAddress( com.foursoft.etrans.common.bean.Address address )throws SQLException{  	      
		   AddressDAO addressDAO= new AddressDAO();
		   addressDAO.store(address);	  
    }

  /**
   * 
   * @param addressId
   * @throws java.sql.SQLException
   */
	public void removeAddress( )throws SQLException{  	      
	   
	Connection			connection	= null;
	Statement			stm			= null;
	ResultSet			rs			= null;
	try{
		connection			= this.getConnection();
		stm					= connection.createStatement();
		
		rs = stm.executeQuery("SELECT CONTACTADDRESSID FROM FS_FR_TERMINALMASTER WHERE TERMINALID = '"+terminalId+"'");

		if(rs.next()){
		   AddressDAO addressDAO= new AddressDAO();
		   addressDAO.removeAddress(rs.getInt("CONTACTADDRESSID"));	
		}
	  }
	  catch(Exception e){
		  e.printStackTrace();
		  throw new EJBException(e.getMessage());
	  }
	  finally{
		  try{
        if(rs!=null)
          rs.close();
        if(stm!=null)
          stm.close();
			  if(connection!=null){
				  connection.close();
			  }
		  }
		  catch(SQLException e){
			  throw new EJBException();
		  }
	  }
	}
  /**
   * 
   * @return 
   * @throws java.sql.SQLException
   * @throws javax.naming.NamingException
   */
	private Connection getConnection() throws SQLException, NamingException {

		if(dataSource==null){
			InitialContext ic = new InitialContext();
			dataSource = ((DataSource) ic.lookup("java:comp/env/jdbc/DB"));
		}
		return dataSource.getConnection();
	}
}