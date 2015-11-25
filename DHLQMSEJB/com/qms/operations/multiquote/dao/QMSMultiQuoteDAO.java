/**
 * Copyright (c) 2000-2001 Four-Soft Pvt Ltd,
 * 5Q1A3, Hi-Tech City, Madhapur, Hyderabad-33, India.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of  Four-Soft Pvt Ltd,
 * ("Confidential Information").  You shall not disclose such Confidential Information
 * and shall use it only in accordance with the terms of the license agreement you entered
 * into with Four-Soft. For more information on the Four Soft Pvt Ltd
 *
 

 * File					: QMSMultiQuoteDAO.java
 * @author				: Govind
 * @date				: 
 *CR-                   :CR-DHLQMS-CR-219979&80


 *	This Controller is used to control the flow in the quote module
 */
 
package com.qms.operations.multiquote.dao;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedHashSet;
import javax.ejb.ObjectNotFoundException;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import oracle.jdbc.OracleTypes;
import org.apache.log4j.Logger;
import com.foursoft.esupply.common.bean.ESupplyGlobalParameters;
import com.foursoft.esupply.common.util.ConnectionUtil;
import com.foursoft.esupply.common.util.ESupplyDateUtility;	  //@@Added by kiran.v on 26/09/2011 for Wpbn Issue   272712 
import com.foursoft.etrans.common.util.java.OperationsImpl;
import com.qms.operations.costing.dob.CostingChargeDetailsDOB;
import com.qms.operations.costing.dob.CostingHDRDOB;
import com.qms.operations.costing.dob.CostingLegDetailsDOB;
import com.qms.operations.costing.dob.CostingMasterDOB;
import com.qms.operations.costing.dob.CostingRateInfoDOB;
import com.qms.operations.multiquote.dob.MultiQuoteAttachmentDOB;
import com.qms.operations.multiquote.dob.MultiQuoteCartageRates;
import com.qms.operations.multiquote.dob.MultiQuoteChargeInfo;
import com.qms.operations.multiquote.dob.MultiQuoteCharges;
import com.qms.operations.multiquote.dob.MultiQuoteFinalDOB;
import com.qms.operations.multiquote.dob.MultiQuoteFlagsDOB;
import com.qms.operations.multiquote.dob.MultiQuoteFreightLegSellRates;
import com.qms.operations.multiquote.dob.MultiQuoteFreightRSRCSRDOB;
import com.qms.operations.multiquote.dob.MultiQuoteMasterDOB;
import com.qms.reports.java.UpdatedQuotesReportDOB;

public class QMSMultiQuoteDAO 
{
  private transient DataSource dataSource = null;
	private static final String FILE_NAME		= "QMSQuoteDAO.java";
  private static Logger logger = null;

  /**
   * Queries
   */
  private static final String masterInsQuery		=	" INSERT INTO QMS_QUOTE_MASTER "+
														  " (QUOTE_ID, SHIPMENT_MODE, PREQUOTE_ID, IU_FLAG, EFFECTIVE_DATE, VALID_TO, ACCEPT_VALIDITYPERIOD,"+
														  " CUSTOMER_ID, CUSTOMER_ADDRESSID, CREATED_DATE, CREATED_BY, SALES_PERSON, INDUSTRY_ID, COMMODITY_ID,"+
														  " HAZARDOUS_IND, UN_NUMBER, CLASS, SERVICE_LEVEL_ID, INCO_TERMS_ID, QUOTING_STATION, ORIGIN_LOCATION,"+
														  " SHIPPER_ZIPCODE, ORIGIN_PORT, OVERLENGTH_CARGONOTES, ROUTING_ID, DEST_LOCATION, CONSIGNEE_ZIPCODE,"+
														  " DESTIONATION_PORT,ESCALATED_TO,MODIFIED_DATE,MODIFIED_BY, TERMINAL_ID, VERSION_NO,BASIS,SHIPPERZONES,"+
														  " CONSIGNEEZONES,ID,PN_FLAG, UPDATE_FLAG, ACTIVE_FLAG, SENT_FLAG, COMPLETE_FLAG, QUOTE_STATUS,"+
														  " ESCALATION_FLAG,IE_FLAG,EMAIL_FLAG, FAX_FLAG, PRINT_FLAG,CREATED_TSTMP,SPOT_RATES_FLAG,CARGO_ACC_TYPE,CARGO_ACC_PLACE,"+
														  " SHIPPER_MODE,CONSIGNEE_MODE,SHIPPER_CONSOLE_TYPE,CONSIGNEE_CONSOLE_TYPE,SALES_PERSON_EMAIL_FLAG,APP_REJ_TSTMP,IS_MULTI_QUOTE,"+
														  " MULTI_QUOTE_CARRIER_ID,MULTI_QUOTE_LANE_NO,MULTI_QUOTE_SERVICE_LEVEL,MULTI_QUOTE_WEIGHT_BREAK,MULTI_QUOTE_WITH,CUST_REQUESTED_DATE,"+
														  " CUST_REQUESTED_TIME,MULTI_LANE_ORDER,SPOT_SUR_COUNT) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,"+
														  " ?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"; //Modified by Anil.k  Modified by Rakesh for Issue:236359
														  
  private static final String masterContactPersonInsQuery = " INSERT INTO QMS_QUOTE_CONTACTDTL (QUOTE_ID, CUSTOMERID,SL_NO,ID) VALUES (?,?,?,SEQ_QUOTE_CONTACTDTL_ID.NEXTVAL)";
  
  private static final String masterChargeGroupsInsQuery  = " INSERT INTO QMS_QUOTE_CHARGEGROUPDTL (QUOTE_ID, CHARGEGROUPID,ID) VALUES (?,?,SEQ_CHARGEGROUPDTL_ID.NEXTVAL)";
  
  private static final String masterHeaderFooterInsQuery  = " INSERT INTO QMS_QUOTE_HF_DTL (QUOTE_ID, HEADER, CONTENT, CLEVEL, ALIGN,ID) VALUES (?,?,?,?,?,SEQ_QMS_QUOTE_HF_DTL.NEXTVAL)";
  
  private static final String notesInsertQuery            = "INSERT INTO QMS_QUOTE_NOTES (QUOTE_ID,INTERNAL_NOTES,EXTERNAL_NOTES,ID) VALUES (?,?,?,SEQ_QMS_QUOTE_NOTES.NEXTVAL)";
  
  private static final String selectedRatesInsertQuery    = "INSERT INTO QMS_QUOTE_RATES(QUOTE_ID,SELL_BUY_FLAG,BUYRATE_ID,SELLRATE_ID,RATE_LANE_NO,CHARGE_ID,CHARGE_DESCRIPTION,MARGIN_DISCOUNT_FLAG,MARGIN_TYPE,"+
															  "MARGIN,DISCOUNT_TYPE,DISCOUNT,NOTES,QUOTE_REFNO,BREAK_POINT,CHARGE_AT,BUY_RATE,R_SELL_RATE,	RT_PLAN_ID,	SERIAL_NO,ID,LINE_NO,MARGIN_TEST_FLAG,MULTI_QUOTE_LANE_NO,ZONE_CODE)"+ 
															  " VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,SEQ_QMS_QUOTE_RATES.NEXTVAL,?,?,?,?)";
  
 private static final String selectedRatesInsertQuery1    = "INSERT INTO QMS_QUOTE_RATES(QUOTE_ID,SELL_BUY_FLAG,BUYRATE_ID,SELLRATE_ID,RATE_LANE_NO,CHARGE_ID,CHARGE_DESCRIPTION,MARGIN_DISCOUNT_FLAG,MARGIN_TYPE,"+
																  "MARGIN,DISCOUNT_TYPE,DISCOUNT,NOTES,QUOTE_REFNO,BREAK_POINT,CHARGE_AT,BUY_RATE,R_SELL_RATE,	RT_PLAN_ID,	SERIAL_NO,ID,LINE_NO,MARGIN_TEST_FLAG,SRVLEVEL,FREQUENCY,TRANSIT_TIME,CARRIER,RATE_VALIDITY,FREQUENCY_CHECKED,TRANSIT_CHECKED,CARRIER_CHECKED,VALIDITY_CHECKED,VERSION_NO,MULTI_QUOTE_LANE_NO)"+ 
																  " VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,SEQ_QMS_QUOTE_RATES.NEXTVAL,?,?,?,?,?,?,?,?,?,?,?,?,?)";

 private static final String routePlanInsertQuery        = "INSERT INTO FS_RT_PLAN (RT_PLAN_ID,QUOTE_ID,ORIG_TRML_ID,DEST_TRML_ID,ORIG_LOC_ID,DEST_LOC_ID,SHIPPER_ID,PRMY_MODE,CRTD_TIMESTMP,LAST_UPDTD_TIMESTMP) VALUES (?,?,?,?,?,?,?,?,?,?)";
 
 private static final String routeLegInsertQuery         = "INSERT INTO FS_RT_LEG (RT_PLAN_ID,SERIAL_NO,LEG_TYPE,ORIG_LOC,DEST_LOC,SHPMNT_MODE,LEG_VALID_FLAG,ORIG_TRML_ID,DEST_TRML_ID) VALUES (?,?,?,?,?,?,?,?,?)";
 private static final String terminalQuery     = "SELECT TERMINALID FROM FS_FR_TERMINALLOCATION WHERE LOCATIONID=?";
 
 //Added by Anil.k for Spot Rates
 private static final String selectedSpotRatesInsertQuery = "INSERT INTO QMS_QUOTE_SPOTRATES (QUOTE_ID,LANE_NO,UPPER_BOUND,LOWER_BOUND,CHARGE_RATE,WEIGHT_BREAK_SLAB,LINE_NO,SHIPMENT_MODE,SERVICELEVEL,ID,UOM,DENSITY_CODE,WEIGHT_BREAK,CURRENCYID,RATE_DESCRIPTION,SURCHARGE_ID,CHARGERATE_INDICATOR) VALUES(?,?,?,?,?,?,?,?,?,SEQ_QUOTE_SPOTRATES.NEXTVAL,?,?,?,?,?,?,?)";
 
 
 
 private static final String getIdQry            ="  SELECT ID" +
                                            	  "  FROM QMS_QUOTE_MASTER" + 
                                            	  "  WHERE QUOTE_ID = ?" + 
                                            	  "   AND VERSION_NO =" + 
                                            	  "       (SELECT MAX(VERSION_NO) FROM QMS_QUOTE_MASTER WHERE QUOTE_ID = ?) and MULTI_QUOTE_LANE_NO =?";
 
 private static final String masterUpdateQry		=	" UPDATE QMS_QUOTE_MASTER "+
														 " SET QUOTE_ID=?,SHIPMENT_MODE=?,PREQUOTE_ID=?,IU_FLAG=?,EFFECTIVE_DATE=?,VALID_TO=?,ACCEPT_VALIDITYPERIOD=?,"+
														 " CUSTOMER_ID=?,CUSTOMER_ADDRESSID=?,CREATED_DATE=?,CREATED_BY=?,SALES_PERSON=?,INDUSTRY_ID=?,COMMODITY_ID=?,"+
														 " HAZARDOUS_IND=?,UN_NUMBER=?,CLASS=?, SERVICE_LEVEL_ID=?, INCO_TERMS_ID=?, QUOTING_STATION=?, ORIGIN_LOCATION=?,"+
														 " SHIPPER_ZIPCODE=?, ORIGIN_PORT=?, OVERLENGTH_CARGONOTES=?, ROUTING_ID=?, DEST_LOCATION=?, CONSIGNEE_ZIPCODE=?,"+
														 " DESTIONATION_PORT=?, ESCALATED_TO=?, MODIFIED_DATE=?, MODIFIED_BY=?,  TERMINAL_ID=?, VERSION_NO=?, BASIS=?, SHIPPERZONES=?,"+
														 " CONSIGNEEZONES=?, PN_FLAG=?, UPDATE_FLAG=?, ACTIVE_FLAG=?, SENT_FLAG=?, COMPLETE_FLAG=?, QUOTE_STATUS=?,"+
														 " ESCALATION_FLAG=?, IE_FLAG=?, EMAIL_FLAG=?, FAX_FLAG=?, PRINT_FLAG=?,SHIPPER_MODE=?,CONSIGNEE_MODE=?,SHIPPER_CONSOLE_TYPE=?,CONSIGNEE_CONSOLE_TYPE=? ,SALES_PERSON_EMAIL_FLAG=?,CUST_REQUESTED_DATE=?,CUST_REQUESTED_TIME=?   WHERE ID=?";//Modified by Rakesh on 23-02-2011 for  Issue:236359
 
 private static final String masterContactPersonDelQry  = "DELETE FROM QMS_QUOTE_CONTACTDTL WHERE QUOTE_ID = ?";
 private static final String masterChargeGroupsDelQry   = "DELETE FROM QMS_QUOTE_CHARGEGROUPDTL WHERE QUOTE_ID= ?";
 private static final String masterHeaderFooterDelQry   = "DELETE FROM QMS_QUOTE_HF_DTL WHERE QUOTE_ID=?";
 private static final String selectedRatesDelQry        = "DELETE FROM QMS_QUOTE_RATES WHERE QUOTE_ID=?";
 private static final String notesDelQry                = "DELETE FROM QMS_QUOTE_NOTES WHERE QUOTE_ID=?";

  /**
	*	Default Contructor which Initializes
	*	InitialContext	DataSource
	*/
  public QMSMultiQuoteDAO()
  {
    logger  = Logger.getLogger(QMSMultiQuoteDAO.class);
    try
    {
      InitialContext ic	= new InitialContext();
      dataSource			=(DataSource) ic.lookup("java:comp/env/jdbc/DB");
    }
    catch(NamingException nmEx)
    {
      //Logger.error(FILE_NAME,"QMSQuoteDAO(QMSQuoteDAO()) naming exception "+nmEx.toString());
      logger.error(FILE_NAME+"QMSQuoteDAO(QMSQuoteDAO()) naming exception "+nmEx.toString());
    }
  }
  
  /**
	*@return Connection
	*gets the connection from the pool
	*/
  private Connection getConnection() throws SQLException
	{
		return dataSource.getConnection();
	}
  
 
 
  
   /**
	 * This method is used to insert the master info from QuoteMasterDOB to QuoteMaster table
   * 
	 * @param masterDOB 	an QuoteMasterDOB object that contains the master information of the quote
   * 
	 * @exception SQLException 
	 */
 public MultiQuoteFreightLegSellRates getFrtLegSellRates(String origin, String destination, String serviceLevelId, int shipmentMode, String terminalId,String permissionFlag,String operation,String quoteId,String incoTerms, String wtBrk) throws SQLException
  {
    Connection                connection              = null;
    CallableStatement         csmt                    = null;
   // PreparedStatement         psmt                    = null;//Commented By RajKumari on 24-10-2008 for Connection Leakages.
    ResultSet                 rs                      = null;
    MultiQuoteFreightLegSellRates  legRateDetails          = null;//to maintain the info of each leg
    String[]                  slabWeightBreaks        = null;//to maintain the slab weight breaks in order
    String[]                  listWeightBreaks        = null;//to maintain the list weight breaks in order
    String[]                  flatWeightBreaks        = null;                          
    ArrayList                 sellRates               = null;//to maintain the list of  all rate dobs
    MultiQuoteFreightRSRCSRDOB     sellRatesDOB            = null;//to maintain one record that is to be displayed
    String[]                  rateDescriptions        = null;    
    PreparedStatement 			pstmt				  = null;//Added for the issue 234719
    ResultSet 					rs1					  = null;
    ArrayList 						buyRateId 		  = null;
    ArrayList						sellRateId		  = null;
  
    String operationQuote						  = "";
    //int i = 0; //Ended for the Issue 234719
    try
    {
    long start = System.currentTimeMillis();
    connection  = this.getConnection();
    //Added for the issue 234719 on 09Feb2011
      if("View/Print".equalsIgnoreCase(operation))
      {
    	  buyRateId   = new ArrayList();
    	  sellRateId  = new ArrayList();
    	
    	  String reportQuery = "SELECT DISTINCT QR.SELL_BUY_FLAG ,QR.BUYRATE_ID,QR.SELLRATE_ID FROM QMS_QUOTE_MASTER QM,QMS_QUOTE_RATES QR WHERE QM.QUOTE_ID=? AND QM.ID=QR.QUOTE_ID";
    	  pstmt = connection.prepareStatement(reportQuery);
    	  pstmt.setString(1, quoteId);
    	  rs1 = pstmt.executeQuery();
      
    	  while(rs1.next())
    	  {
    		  if("BR".equalsIgnoreCase(rs1.getString("SELL_BUY_FLAG"))){
        		         		  buyRateId.add(rs1.getInt("BUYRATE_ID"));
        		
        		  }else if("RSR".equalsIgnoreCase(rs1.getString("SELL_BUY_FLAG"))){
        			  buyRateId.add(rs1.getInt("BUYRATE_ID"));
            		  sellRateId.add(rs1.getInt("SELLRATE_ID"));
        		  }
    		//  i++;
    	  }
    	  operation = "View";
    	  operationQuote = "View/Print";
    	  csmt  = connection.prepareCall("{CALL QMS_QUOTE_PACK.MULTI_QUOTE_SEL_BUY_RTE_PROC(?,?,?,?,?,?,?,?,?,?)}");
      }
      else if("Modify/Print".equalsIgnoreCase(operation))
      {
    	  buyRateId     = new ArrayList();
    	  sellRateId	= new ArrayList();
   
    	  String reportQuery = "SELECT DISTINCT QR.SELL_BUY_FLAG,QR.BUYRATE_ID,QR.SELLRATE_ID FROM QMS_QUOTE_MASTER QM,QMS_QUOTE_RATES QR WHERE QM.QUOTE_ID=? AND QM.ID=QR.QUOTE_ID";
    	  pstmt = connection.prepareStatement(reportQuery);
    	  pstmt.setString(1, quoteId);
    	  rs1 = pstmt.executeQuery();
      
    	  while(rs1.next())
    	  {
    		 
    		  if("BR".equalsIgnoreCase(rs1.getString("SELL_BUY_FLAG"))){
    		  buyRateId.add(rs1.getInt("BUYRATE_ID"));
  
    		  }else if("RSR".equalsIgnoreCase(rs1.getString("SELL_BUY_FLAG"))){
    			  
        		  sellRateId.add(rs1.getInt("SELLRATE_ID"));
        		  buyRateId.add(rs1.getInt("BUYRATE_ID"));
    		  }
    		//  i++;
    	  }
    	  operation = "Modify";
    	  operationQuote = "View/Print";
    	  csmt  = connection.prepareCall("{CALL QMS_QUOTE_PACK.MULTI_QUOTE_SEL_BUY_RTE_PROC(?,?,?,?,?,?,?,?,?,?)}");
      }
      else
      {
    	  csmt  = connection.prepareCall("{CALL QMS_QUOTE_PACK.MULTI_QUOTE_SEL_BUY_RATE_PROC(?,?,?,?,?,?,?,?,?,?)}");
      }//Ended for the Issue 234719
    
      
       csmt.setString(1,origin);
      csmt.setString(2,destination);
      csmt.setString(3,terminalId);
      csmt.setString(4,serviceLevelId);
      csmt.setInt(5,shipmentMode);
    
      
      //this is the whether the user has the buy rates permission or not 
      //this paticular flag should be retrived from ? and is to be set in the master dob 
      //so that it can be used here for now it is defaulted
      csmt.setString(6,permissionFlag);
      csmt.setString(7,operation);	   //Modified by Rakesh on 08-01-2010
      
      csmt.setString(8,quoteId!=null?""+quoteId:"");
      csmt.setString(9, wtBrk.toUpperCase());
      csmt.registerOutParameter(10,OracleTypes.CURSOR);
            
      csmt.execute();
      
      //get the freight rates
      rs  = (ResultSet)csmt.getObject(10);     
      logger.info("Time Taken for DB procedure in seconds for 2nd screen (Quote_Sell_Buy_Rates_Proc)  :  " + ((System.currentTimeMillis()) - start)  + "   Origin ::"+ origin + " Destination::"+destination+" TerminalId :: "+terminalId);
      sellRates = new ArrayList();
      
      while(rs.next())
      {
    	  //Added for the Issue 234719
    	  if( "View/Print".equalsIgnoreCase(operationQuote))
    	  {
    	 /* for(int j=0;j<i;j++)
    	  {*/
    	//  if(((Integer)buyRateId.get(j)!=0 && (Integer)buyRateId.get(j) == rs.getInt("BUYRATEID")) || ((Integer)sellRateId.get(j)!=0 && (Integer)sellRateId.get(j) == rs.getInt("REC_BUYRATE_ID"))){
    		  //Commented by govind for the issue hyperlink not working for Quote update
         if(("BR".equalsIgnoreCase(rs.getString("RCB_FLAG")) && buyRateId.contains(rs.getInt("BUYRATEID"))) ||
    		  ("RSR".equalsIgnoreCase(rs.getString("RCB_FLAG")) && sellRateId.contains(rs.getInt("BUYRATEID"))&& buyRateId.contains(rs.getInt("REC_BUYRATE_ID")))){         
    	        //Create a new dob to set the details of the new record  
    	        sellRatesDOB  = new MultiQuoteFreightRSRCSRDOB();
    	        
    	        sellRates.add(sellRatesDOB);
    	      
    	        //now set the dob with the data
    	      
    	        if(!"BR".equalsIgnoreCase(rs.getString("RCB_FLAG")))//only the RSR or CSR rates will have a sell rate id
    	          sellRatesDOB.setSellRateId(rs.getInt("BUYRATEID"));
    	          
    	        sellRatesDOB.setRsrOrCsrFlag(rs.getString("RCB_FLAG"));
    	        sellRatesDOB.setOrigin(rs.getString("ORIGIN"));
    	        sellRatesDOB.setDestination(rs.getString("DESTINATION"));
    	        sellRatesDOB.setCarrierId(rs.getString("CARRIER_ID"));
    	        sellRatesDOB.setServiceLevelId(rs.getString("SERVICE_LEVEL"));
    	        sellRatesDOB.setConsoleType(rs.getString("CONSOLE_TYPE"));//added by VLAKSHMI for issue 146968 on 5/12/2008
    	        sellRatesDOB.setServiceLevelDesc(rs.getString("SERVICE_LEVEL_DESC"));//@@Added by Kameswari for the WPBN issue-31330
    	        sellRatesDOB.setFrequency(rs.getString("FREQUENCY")!=null?rs.getString("FREQUENCY"):"");
    	        sellRatesDOB.setTransitTime(rs.getString("TRANSIT_TIME")!=null?rs.getString("TRANSIT_TIME"):"");
    	        if(!"BR".equalsIgnoreCase(rs.getString("RCB_FLAG")))//if it is a RSR or CSR 
    	          sellRatesDOB.setBuyRateId(rs.getInt("REC_BUYRATE_ID"));
    	        else
    	          sellRatesDOB.setBuyRateId(rs.getInt("BUYRATEID"));//if it is a buy rate
    	         
    	        sellRatesDOB.setLaneNo(rs.getInt("LANE_NO"));
    	        sellRatesDOB.setNotes(rs.getString("NOTES")!=null?rs.getString("NOTES"):"");
    	        sellRatesDOB.setExtNotes(rs.getString("EXTERNAL_NOTES")!=null?rs.getString("EXTERNAL_NOTES"):"");//Added by Mohan for Issue No.219976 on 04-11-2010
    	        sellRatesDOB.setWeightBreakType(rs.getString("WEIGHT_BREAK"));
    	        sellRatesDOB.setDensityRatio(rs.getString("DENSITY_CODE")!= null?rs.getString("DENSITY_CODE"):"");
    	        
    	        if("G".equalsIgnoreCase(rs.getString("WT_CLASS")))
    	          sellRatesDOB.setWeightClass("General");
    	        else
    	          sellRatesDOB.setWeightClass("WeightScale");
    	     
    	        sellRatesDOB.setCreatedTerminalId(rs.getString("TERMINALID"));
    	        sellRatesDOB.setCurrency(rs.getString("CURRENCY"));
    	        sellRatesDOB.setSelectedFlag(rs.getString("SELECTED_FLAG"));
    	        sellRatesDOB.setEffDate(rs.getTimestamp("EFROM"));
    	        sellRatesDOB.setValidUpTo(rs.getTimestamp("VALIDUPTO"));
    	        sellRatesDOB.setIncoTerms(incoTerms);
    	        rateDescriptions	=	rs.getString("RATE_DESCRIPTION").split(",");
//    	        if(slabWeightBreaks==null && "SLAB".equalsIgnoreCase(rs.getString("WEIGHT_BREAK")))
    	        if( "SLAB".equalsIgnoreCase(rs.getString("WEIGHT_BREAK")))
    	        {
    	          slabWeightBreaks  = rs.getString("WEIGHT_BREAK_SLAB").split(",");
    	        }
//    	        else if(listWeightBreaks==null && "LIST".equalsIgnoreCase(rs.getString("WEIGHT_BREAK")))
    	        else if( "LIST".equalsIgnoreCase(rs.getString("WEIGHT_BREAK")))
    	        {
    	          listWeightBreaks  = rs.getString("WEIGHT_BREAK_SLAB").split(",");
    	        }
//    	            ADDED BY SUBRAHMANYAM FOR CR-219973
//    	        else if(flatWeightBreaks==null && "FLAT".equalsIgnoreCase(rs.getString("WEIGHT_BREAK")))
    	        else if( "FLAT".equalsIgnoreCase(rs.getString("WEIGHT_BREAK")))
    	        {
    	        	flatWeightBreaks  = rs.getString("WEIGHT_BREAK_SLAB").split(",");
    	        }        
    	            sellRatesDOB.setFlatWeightBreaks(flatWeightBreaks);
    	            sellRatesDOB.setSlabWeightBreaks(slabWeightBreaks);
    	            sellRatesDOB.setListWeightBreaks(listWeightBreaks);
    	            sellRatesDOB.setRateDescriptions(rateDescriptions);            
    	      
    	        sellRatesDOB.setChargeRates(rs.getString("CHARGERATE").split(","));
    	      
    	        if(rs.getString("CHECKED_FLAG")!=null && "FCL".equals(rs.getString("CONSOLE_TYPE")))
    	        {
    	        sellRatesDOB.setCheckedFalg(rs.getString("CHECKED_FLAG").split(","));
    	        }
    	      }
    	  //}
    	  } //Ended for the Issue 234719
    	  else if( "View".equalsIgnoreCase(operation) && "Y".equalsIgnoreCase(rs.getString("SELECTED_FLAG"))){
        //if the sellRate ArrayList is not initialised then initialise it
         
        //Create a new dob to set the details of the new record  
        sellRatesDOB  = new MultiQuoteFreightRSRCSRDOB();
        
        sellRates.add(sellRatesDOB);
      
        //now set the dob with the data
      
        if(!"BR".equalsIgnoreCase(rs.getString("RCB_FLAG")))//only the RSR or CSR rates will have a sell rate id
          sellRatesDOB.setSellRateId(rs.getInt("BUYRATEID"));
          
        sellRatesDOB.setRsrOrCsrFlag(rs.getString("RCB_FLAG"));
        sellRatesDOB.setOrigin(rs.getString("ORIGIN"));
        sellRatesDOB.setDestination(rs.getString("DESTINATION"));
        sellRatesDOB.setCarrierId(rs.getString("CARRIER_ID"));
        sellRatesDOB.setServiceLevelId(rs.getString("SERVICE_LEVEL"));
        sellRatesDOB.setConsoleType(rs.getString("CONSOLE_TYPE"));//added by VLAKSHMI for issue 146968 on 5/12/2008
        sellRatesDOB.setServiceLevelDesc(rs.getString("SERVICE_LEVEL_DESC"));//@@Added by Kameswari for the WPBN issue-31330
        sellRatesDOB.setFrequency(rs.getString("FREQUENCY")!=null?rs.getString("FREQUENCY"):"");
        sellRatesDOB.setTransitTime(rs.getString("TRANSIT_TIME")!=null?rs.getString("TRANSIT_TIME"):"");
        if(!"BR".equalsIgnoreCase(rs.getString("RCB_FLAG")))//if it is a RSR or CSR 
          sellRatesDOB.setBuyRateId(rs.getInt("REC_BUYRATE_ID"));
        else
          sellRatesDOB.setBuyRateId(rs.getInt("BUYRATEID"));//if it is a buy rate
         
        sellRatesDOB.setLaneNo(rs.getInt("LANE_NO"));
        sellRatesDOB.setNotes(rs.getString("NOTES")!=null?rs.getString("NOTES"):"");
        sellRatesDOB.setExtNotes(rs.getString("EXTERNAL_NOTES")!=null?rs.getString("EXTERNAL_NOTES"):"");//Added by Mohan for Issue No.219976 on 04-11-2010
        sellRatesDOB.setWeightBreakType(rs.getString("WEIGHT_BREAK"));
        
        if("G".equalsIgnoreCase(rs.getString("WT_CLASS")))
          sellRatesDOB.setWeightClass("General");
        else
          sellRatesDOB.setWeightClass("WeightScale");
     
        sellRatesDOB.setCreatedTerminalId(rs.getString("TERMINALID"));
        sellRatesDOB.setCurrency(rs.getString("CURRENCY"));
        sellRatesDOB.setSelectedFlag(rs.getString("SELECTED_FLAG"));
        sellRatesDOB.setEffDate(rs.getTimestamp("EFROM"));
        sellRatesDOB.setValidUpTo(rs.getTimestamp("VALIDUPTO"));
        sellRatesDOB.setDensityRatio(rs.getString("DENSITY_CODE")!= null?rs.getString("DENSITY_CODE"):"");
        sellRatesDOB.setIncoTerms(incoTerms);
        rateDescriptions	=	rs.getString("RATE_DESCRIPTION").split(",");
//        if(slabWeightBreaks==null && "SLAB".equalsIgnoreCase(rs.getString("WEIGHT_BREAK")))
        if( "SLAB".equalsIgnoreCase(rs.getString("WEIGHT_BREAK")))
        {
          slabWeightBreaks  = rs.getString("WEIGHT_BREAK_SLAB").split(",");
        }
//        else if(listWeightBreaks==null && "LIST".equalsIgnoreCase(rs.getString("WEIGHT_BREAK")))
        else if( "LIST".equalsIgnoreCase(rs.getString("WEIGHT_BREAK")))
        {
          listWeightBreaks  = rs.getString("WEIGHT_BREAK_SLAB").split(",");
        }
//            ADDED BY SUBRAHMANYAM FOR CR-219973
//        else if(flatWeightBreaks==null && "FLAT".equalsIgnoreCase(rs.getString("WEIGHT_BREAK")))
        else if( "FLAT".equalsIgnoreCase(rs.getString("WEIGHT_BREAK")))
        {
        	flatWeightBreaks  = rs.getString("WEIGHT_BREAK_SLAB").split(",");
        }        
            sellRatesDOB.setFlatWeightBreaks(flatWeightBreaks);
            sellRatesDOB.setSlabWeightBreaks(slabWeightBreaks);
            sellRatesDOB.setListWeightBreaks(listWeightBreaks);
            sellRatesDOB.setRateDescriptions(rateDescriptions);            
       /* //@@Added by Kameswari fro the CR-
        else
        {
          flatWeightBreaks  = rs.getString("WEIGHT_BREAK_SLAB").split(",");
        }*/
        sellRatesDOB.setChargeRates(rs.getString("CHARGERATE").split(","));
        // added by VLAKSHMI for issue 146968 on 5/12/2008
        if(rs.getString("CHECKED_FLAG")!=null && "FCL".equals(rs.getString("CONSOLE_TYPE")))
        {
        sellRatesDOB.setCheckedFalg(rs.getString("CHECKED_FLAG").split(","));
        }//end for issue 146968 on 5/12/2008
      }else if(!("View".equalsIgnoreCase(operation))){//Added by Rakesh on 08-01-2010

          //if the sellRate ArrayList is not initialised then initialise it
           
          //Create a new dob to set the details of the new record  
          sellRatesDOB  = new MultiQuoteFreightRSRCSRDOB();
          
          sellRates.add(sellRatesDOB);
        
          //now set the dob with the data
        
          if(!"BR".equalsIgnoreCase(rs.getString("RCB_FLAG")))//only the RSR or CSR rates will have a sell rate id
            sellRatesDOB.setSellRateId(rs.getInt("BUYRATEID"));
            
          sellRatesDOB.setRsrOrCsrFlag(rs.getString("RCB_FLAG"));
          sellRatesDOB.setOrigin(rs.getString("ORIGIN"));
          sellRatesDOB.setDestination(rs.getString("DESTINATION"));
          sellRatesDOB.setCarrierId(rs.getString("CARRIER_ID"));
          sellRatesDOB.setServiceLevelId(rs.getString("SERVICE_LEVEL"));
          sellRatesDOB.setConsoleType(rs.getString("CONSOLE_TYPE"));//added by VLAKSHMI for issue 146968 on 5/12/2008
          sellRatesDOB.setServiceLevelDesc(rs.getString("SERVICE_LEVEL_DESC"));//@@Added by Kameswari for the WPBN issue-31330
          sellRatesDOB.setFrequency(rs.getString("FREQUENCY")!=null?rs.getString("FREQUENCY"):"");
          sellRatesDOB.setTransitTime(rs.getString("TRANSIT_TIME")!=null?rs.getString("TRANSIT_TIME"):"");
          sellRatesDOB.setDensityRatio(rs.getString("DENSITY_CODE")!= null?rs.getString("DENSITY_CODE"):"");
          if(!"BR".equalsIgnoreCase(rs.getString("RCB_FLAG")))//if it is a RSR or CSR 
            sellRatesDOB.setBuyRateId(rs.getInt("REC_BUYRATE_ID"));
          else
            sellRatesDOB.setBuyRateId(rs.getInt("BUYRATEID"));//if it is a buy rate
           
          sellRatesDOB.setLaneNo(rs.getInt("LANE_NO"));
          sellRatesDOB.setNotes(rs.getString("NOTES")!=null?rs.getString("NOTES"):"");
          sellRatesDOB.setExtNotes(rs.getString("EXTERNAL_NOTES")!=null?rs.getString("EXTERNAL_NOTES"):"");//Added by Mohan for Issue No.219976 on 04-11-2010
          sellRatesDOB.setWeightBreakType(rs.getString("WEIGHT_BREAK"));
          
          if("G".equalsIgnoreCase(rs.getString("WT_CLASS")))
            sellRatesDOB.setWeightClass("General");
          else
            sellRatesDOB.setWeightClass("WeightScale");
       
          sellRatesDOB.setCreatedTerminalId(rs.getString("TERMINALID"));
          sellRatesDOB.setCurrency(rs.getString("CURRENCY"));
          sellRatesDOB.setSelectedFlag(rs.getString("SELECTED_FLAG"));
          sellRatesDOB.setEffDate(rs.getTimestamp("EFROM"));
          sellRatesDOB.setValidUpTo(rs.getTimestamp("VALIDUPTO"));
          sellRatesDOB.setIncoTerms(incoTerms);
          rateDescriptions	=	rs.getString("RATE_DESCRIPTION").split(",");
//          if(slabWeightBreaks==null && "SLAB".equalsIgnoreCase(rs.getString("WEIGHT_BREAK")))
          if( "SLAB".equalsIgnoreCase(rs.getString("WEIGHT_BREAK")))
          {
            slabWeightBreaks  = rs.getString("WEIGHT_BREAK_SLAB").split(",");
          }
//          else if(listWeightBreaks==null && "LIST".equalsIgnoreCase(rs.getString("WEIGHT_BREAK")))
          else if( "LIST".equalsIgnoreCase(rs.getString("WEIGHT_BREAK")))
          {
            listWeightBreaks  = rs.getString("WEIGHT_BREAK_SLAB").split(",");
          }
//              ADDED BY SUBRAHMANYAM FOR CR-219973
//          else if(flatWeightBreaks==null && "FLAT".equalsIgnoreCase(rs.getString("WEIGHT_BREAK")))
          else if( "FLAT".equalsIgnoreCase(rs.getString("WEIGHT_BREAK")))
          {
          	flatWeightBreaks  = rs.getString("WEIGHT_BREAK_SLAB").split(",");
          }        
              sellRatesDOB.setFlatWeightBreaks(flatWeightBreaks);
              sellRatesDOB.setSlabWeightBreaks(slabWeightBreaks);
              sellRatesDOB.setListWeightBreaks(listWeightBreaks);
              sellRatesDOB.setRateDescriptions(rateDescriptions);            
         /* //@@Added by Kameswari fro the CR-
          else
          {
            flatWeightBreaks  = rs.getString("WEIGHT_BREAK_SLAB").split(",");
          }*/
          sellRatesDOB.setChargeRates(rs.getString("CHARGERATE").split(","));
          // added by VLAKSHMI for issue 146968 on 5/12/2008
          if(rs.getString("CHECKED_FLAG")!=null && "FCL".equals(rs.getString("CONSOLE_TYPE")))
          {
          sellRatesDOB.setCheckedFalg(rs.getString("CHECKED_FLAG").split(","));
          }//end for issue 146968 on 5/12/2008
          
      }
    	  
      }
      //getting the freight rates ends here
     
      //only if the sell rates are there initialize the QuoteFreightLegSellRates dob and assign the data to it
    
      if(sellRates!=null && sellRates.size() > 0)
      {

        legRateDetails  = new MultiQuoteFreightLegSellRates();
           
        legRateDetails.setSlabWeightBreaks(slabWeightBreaks);
        legRateDetails.setListWeightBreaks(listWeightBreaks);
        legRateDetails.setFlatWeightBreaks(flatWeightBreaks);
//        legRateDetails.setRateDescriptions(rateDescriptions);
        legRateDetails.setRates(sellRates);
      }
    }
    catch(SQLException sqEx)
		{
      sqEx.printStackTrace();
			//Logger.error(FILE_NAME,"QMSQuoteDAO[getFrtLegSellRates(origin,dest,servicelevel,shipmentMode,terminalid)] -> "+sqEx.toString());
      logger.error(FILE_NAME+"QMSQuoteDAO[getFrtLegSellRates(origin,dest,servicelevel,shipmentMode,terminalid)] -> "+sqEx.toString());
      throw new SQLException(sqEx.toString());
		}
    catch(Exception e)
		{
      e.printStackTrace();
			//Logger.error(FILE_NAME,"QMSQuoteDAO[getFrtLegSellRates(origin,dest,servicelevel,shipmentMode,terminalid)] -> "+e.toString());
      logger.error(FILE_NAME+"QMSQuoteDAO[getFrtLegSellRates(origin,dest,servicelevel,shipmentMode,terminalid)] -> "+e.toString());
      throw new SQLException(e.toString());
		}
		finally
		{
			try
			{
				ConnectionUtil.closeConnection(connection,csmt,rs);
				ConnectionUtil.closePreparedStatement(pstmt, rs1); // Added by Gowtham on 17Feb2011 for Connectio Leaks.
			}
			catch(Exception ex)
			{
				//Logger.error(FILE_NAME,"Finally : QMSQuoteDAO[getFreightLegSellRates(origin,dest,servicelevel,shipmentMode,terminalid)]-> "+ex.toString());
        logger.error(FILE_NAME+"Finally : QMSQuoteDAO[getFreightLegSellRates(origin,dest,servicelevel,shipmentMode,terminalid)]-> "+ex.toString());
        throw new SQLException(ex.toString());
			}
		}
    return legRateDetails;
  }
 /**
	 * This method helps in getting the charge details for a specified terminal and shipment mode
* 
	 * MultiQuoteFinalDOB  finalDOB
	 * @exception Exception 
	 */
public MultiQuoteFinalDOB getCharges(MultiQuoteFinalDOB finalDOB) throws SQLException
{
 Connection               connection              = null;
 CallableStatement        csmt                    = null;
 ResultSet                rs                      = null;
 ArrayList                originChargesList       = null;//to maintain the list of  all origin charge dobs
 ArrayList                destChargesList         = null;//to maintain the list of  all origin charge dobs
 ArrayList                tempdestChargesList         = null;
 ArrayList				  finalLegDetails 		   = new ArrayList();
 String[]  				  inotes = null;
 String[]     			  enotes = null;
 ArrayList 				  iNotes = new ArrayList();
 ArrayList                eNotes =  new ArrayList();
 int  					  notesCount        = 0;
 int                      chargeGroupIdslen             =0;
 MultiQuoteCharges              delChargesDOB           = null;//@@ To get the Delivery Charge DOB, so that it can be Placed at the end of the list
 MultiQuoteCharges              orgChargesDOB           = null;
 MultiQuoteCharges              chargesDOB              = null;//to maintain one record that is to be displayed
 MultiQuoteChargeInfo           chargeInfo              = null;
 MultiQuoteFreightLegSellRates legRateDetails		   = null;
 ArrayList                 chargeInfoList          = null;
 String                    flag                    = null;
 MultiQuoteMasterDOB            masterDOB               = null;

 double                    sellRate                = 0;
 String                    weightBreak             = null;
 String                    rateType                = null;
	int						  frtsize				  = 0;
   //added by phani sekhar for wpbn 170758 on 20090626   
 String mType = null;
 String dType = null;
 String terminalFlag = null;
 PreparedStatement pstmt = null;
 PreparedStatement notesPstmt = null;////Added by Mohan for Issue No.219976 on 04-11-2010
 ResultSet notesRs = null;    
// PreparedStatement pstmt1 = null;//@@ Commented by Govind for the ConnectionLeaks
 String query = "SELECT MARGIN_TYPE,DISCOUNT_TYPE FROM FS_FR_TERMINALMASTER TM WHERE TM.TERMINALID IN(SELECT RG.PARENT_TERMINAL_ID FROM FS_FR_TERMINAL_REGN RG WHERE RG.CHILD_TERMINAL_ID = ?)";
 String query1 = "SELECT STM.OPER_ADMIN_FLAG FROM Fs_Fr_Terminalmaster STM WHERE STM.TERMINALID =? ";
 String query2 = "SELECT MARGIN_TYPE,DISCOUNT_TYPE FROM FS_FR_TERMINALMASTER TM WHERE TM.TERMINALID =? ";
 String notesQry = "SELECT QN.INTERNAL_NOTES, QN.EXTERNAL_NOTES FROM QMS_QUOTE_NOTES QN WHERE QN.QUOTE_ID IN (SELECT MAX(QMS.ID) FROM QMS_QUOTE_MASTER QMS WHERE QMS.QUOTE_ID = ? AND QMS.ORIGIN_LOCATION = ? AND QMS.DEST_LOCATION = ? AND QMS.ACTIVE_FLAG = 'A')";//Added by Mohan for Issue No.219976 on 04-11-2010  Modified by Rakesh on 21-03-2010
 //ends 170758     
 try
 {
   masterDOB = finalDOB.getMasterDOB();
   long start = System.currentTimeMillis();
   connection  = this.getConnection();
   terminalFlag=masterDOB.getAccessLevel();
  // if(!"H".equals(terminalFlag))
 //  {
   if("A".equals(terminalFlag)|| "H".equals(terminalFlag))
   pstmt= connection.prepareStatement(query2);
   else
   pstmt= connection.prepareStatement(query);
  // pstmt.setString(1,masterDOB.getQuotingStation());
  pstmt.setString(1,masterDOB.getTerminalId());
   rs=pstmt.executeQuery();
   if(rs.next())
   {
   mType = rs.getString("MARGIN_TYPE");
   dType = rs.getString("DISCOUNT_TYPE");
   }
   ConnectionUtil.closePreparedStatement(pstmt,rs);
   connection.setAutoCommit(false);
   csmt  = connection.prepareCall("{CALL QMS_QUOTE_PACK.quote_sell_buy_charges_proc(?,?,?,?,?,?,?,?,?,?,?,?,?)}");
   frtsize = finalDOB.getLegDetails().size();
   notesPstmt= connection.prepareStatement(notesQry); // Added by Gowtham on 04Feb2011 for Loop Leakages.
   for(int fr=0;fr<frtsize;fr++){
	   if(!"Freight".equalsIgnoreCase(masterDOB.getQuoteWith()) || (masterDOB.getSpotRatesFlag()!=null && "N".equalsIgnoreCase(masterDOB.getSpotRatesFlag()[fr]))){
	   legRateDetails		=	(MultiQuoteFreightLegSellRates)finalDOB.getLegDetails().get(fr);
	   if(       (masterDOB.getShipperZipCode()[fr]!=null && !"".equals(masterDOB.getShipperZipCode()[fr]))||
		         (masterDOB.getShipperZones()[fr]!=null  && !"".equals(masterDOB.getShipperZones()[fr]))    ||
		         (masterDOB.getConsigneeZipCode()[fr]!= null && !"".equals(masterDOB.getConsigneeZipCode()[fr]))||
		         (masterDOB.getConsigneeZones()[fr]!=null && !"".equals(masterDOB.getConsigneeZones()[fr])))
	   {
	    originChargesList = legRateDetails.getOriginChargesList();
	    destChargesList   = legRateDetails.getDestChargesList();
	   }
	   if(!"Freight".equalsIgnoreCase(masterDOB.getQuoteWith()))
	   {   
   //@@Fetching the delivery Charge & Removing it from destination list.
	   /*if(originChargesList!=null && originChargesList.size()==1)
	   {
		   orgChargesDOB = (MultiQuoteCharges)originChargesList.get(0);
	   originChargesList.remove(0);
	   }
	   	   
    if(destChargesList!=null && destChargesList.size()==1)
   {
     delChargesDOB = (MultiQuoteCharges)destChargesList.get(0);
     destChargesList.remove(0);
   }*/
   
   String[] chargeGroupIds = legRateDetails.getChargeGroupIds();
   StringBuffer   strChargeGroups  = new StringBuffer();
   chargeGroupIdslen = chargeGroupIds!=null?chargeGroupIds.length-1:0;
   if(chargeGroupIds!=null)
   {
   for(int i=0;i<chargeGroupIdslen;i++)
   {
	   if(chargeGroupIds[i]!= null && !"".equals(chargeGroupIds[i]))
     strChargeGroups = strChargeGroups.append(chargeGroupIds[i]).append("~");
   }
   strChargeGroups.append(chargeGroupIds[chargeGroupIds.length-1]);
   }  
   if(strChargeGroups != null && !"".equals(strChargeGroups.toString()))
   {
	   
	   /*if(originChargesList!=null && originChargesList.size()==1)
	   {
		   orgChargesDOB = (MultiQuoteCharges)originChargesList.get(0);
	   originChargesList.remove(0);
	   }
	   	   
    if(destChargesList!=null && destChargesList.size()==1)
   {
     delChargesDOB = (MultiQuoteCharges)destChargesList.get(0);
     destChargesList.remove(0);
   }*/
	   
	   
   csmt.setString(1,strChargeGroups.toString());
   csmt.setString(2,masterDOB.getSalesPersonCode());
   csmt.setString(3,masterDOB.getTerminalId());
   csmt.setString(4,masterDOB.getBuyRatesPermission());
   csmt.setDouble(5,finalDOB.getChargesMargin());
   csmt.setDouble(6,finalDOB.getChargesDiscount());
   csmt.setString(7,masterDOB.getOriginLocation()[fr]);
   csmt.setString(8,masterDOB.getDestLocation()[fr]);
   csmt.setString(9,""+masterDOB.getShipmentMode());
   csmt.setString(10,masterDOB.getCustomerId());
   csmt.setString(11,("View".equalsIgnoreCase(masterDOB.getOperation())?"Modify":masterDOB.getOperation()));//Modified by Rakesh
   csmt.setString(12,masterDOB.getQuoteId()!=null?""+masterDOB.getQuoteId():"");  //@@ Commented by subrahmanyam  for the enhancement 146971 on 1/12/08
   
   csmt.registerOutParameter(13,OracleTypes.CURSOR);
   
   csmt.execute();
   
   
   rs  = (ResultSet)csmt.getObject(13);
   logger.info("Time Taken for DB procedure in milli seconds for 3rd screen (quote_sell_buy_charges_proc) :  " + ((System.currentTimeMillis()) - start) + "   UserId ::"+ masterDOB.getUserId() + " Origin :: "+ masterDOB.getOriginLocation() + " Destination::"+masterDOB.getDestLocation()+" TerminalId :: "+ masterDOB.getTerminalId());
   //get the Charges
   while(rs.next())
   {
     flag = rs.getString("SEL_BUY_FLAG");
   
     if(originChargesList==null)
     {
       originChargesList = new ArrayList();
     }
     if(tempdestChargesList==null)
     {
    	 tempdestChargesList = new ArrayList();
     }
 
     
     if(chargesDOB!=null && (rs.getString("SELLCHARGEID").equalsIgnoreCase(chargesDOB.getSellChargeId()) || rs.getString("SELLCHARGEID").equalsIgnoreCase(chargesDOB.getBuyChargeId())))
     {
       chargeInfo  = new MultiQuoteChargeInfo();
       
       
       chargeInfoList.add(chargeInfo);
 
       chargeInfo.setBreakPoint(rs.getString("CHARGESLAB"));
       
       if(rs.getString("CURRENCY")!=null && rs.getString("CURRENCY").trim().length()!=0)
           chargeInfo.setCurrency(rs.getString("CURRENCY"));
       else
            chargeInfo.setCurrency(masterDOB.getTerminalCurrency());
            
       chargeInfo.setBuyRate(rs.getDouble("BUYRATE"));
       chargeInfo.setRecOrConSellRrate(rs.getDouble("SELLRATE"));
       chargeInfo.setSellChargeMargin(rs.getDouble("MARGINVALUE"));
       chargeInfo.setSellChargeMarginType(rs.getString("MARGIN_TYPE"));
       chargeInfo.setRateIndicator(rs.getString("RATE_INDICATOR"));
      // if("M".equalsIgnoreCase(chargesDOB.getMarginDiscountFlag()) || chargesDOB.getMarginDiscountFlag()==null)
       if("B".equalsIgnoreCase(flag)||"M".equalsIgnoreCase(chargesDOB.getMarginDiscountFlag()))
       {  
         chargeInfo.setMargin(rs.getDouble("MARGINVALUE"));
         chargeInfo.setTieMarginDiscountValue(rs.getDouble("MARGINVALUE"));
         //modified by phani sekhar for wpbn 170758 on 20090626  
         if(!"Y".equals(chargesDOB.getSelectedFlag()))
         chargeInfo.setMarginType(mType);
         else
         chargeInfo.setMarginType(rs.getString("MARGIN_TYPE"));
           
           if(rs.getDouble("MARGINVALUE")>0 || "Y".equalsIgnoreCase(rs.getString("SELECTED_FLAG")))
           {
         if("A".equalsIgnoreCase(chargeInfo.getMarginType()))
           sellRate  = rs.getDouble("BUYRATE")+rs.getDouble("MARGINVALUE");
         else if("P".equalsIgnoreCase(chargeInfo.getMarginType()))
           sellRate  = rs.getDouble("BUYRATE")+(rs.getDouble("BUYRATE")*rs.getDouble("MARGINVALUE")/100);
         else if(chargeInfo.getMarginType() == null || "".equals(chargeInfo.getMarginType()))
         sellRate  = rs.getDouble("BUYRATE");
       }
           else
             sellRate  = rs.getDouble("BUYRATE");
          
       }
      else
       {
         
         chargeInfo.setDiscount(rs.getDouble("MARGINVALUE"));
         chargeInfo.setTieMarginDiscountValue(rs.getDouble("MARGINVALUE")); //@@Added by subrahmanyam for the wpbn id: 196745 on 02/Feb/10
         if(!"Y".equals(chargesDOB.getSelectedFlag()))
         chargeInfo.setDiscountType(dType);
         else
         chargeInfo.setDiscountType(rs.getString("MARGIN_TYPE"));
          if("A".equalsIgnoreCase(chargeInfo.getDiscountType()))
           sellRate  = rs.getDouble("SELLRATE")-rs.getDouble("MARGINVALUE");
         else if("P".equalsIgnoreCase(chargeInfo.getDiscountType()))
           sellRate  = rs.getDouble("SELLRATE")-(rs.getDouble("SELLRATE")*rs.getDouble("MARGINVALUE")/100);
         else if(chargeInfo.getMarginType() == null || "".equals(chargeInfo.getMarginType()))
         sellRate  = rs.getDouble("SELLRATE");
       }
       weightBreak   = rs.getString("WEIGHT_BREAK");
       rateType      = rs.getString("RATE_TYPE");
       if("BASE".equalsIgnoreCase(chargeInfo.getBreakPoint()) || "MIN".equalsIgnoreCase(chargeInfo.getBreakPoint()) || "MAX".equalsIgnoreCase(chargeInfo.getBreakPoint()) || ("SLAB".equalsIgnoreCase(weightBreak) && "FLAT".equalsIgnoreCase(rateType)) || ("BOTH".equalsIgnoreCase(rateType) && "F".equalsIgnoreCase(chargeInfo.getRateIndicator()))) // MODIFIED FOR 183812 & 195552 ON 25-JAN-10 BY SUBRAHMANYAM
       {
         chargeInfo.setBasis("Per Shipment");
       }
       else
       {
           if("Per Kg".equalsIgnoreCase(rs.getString("CHARGEBASIS")))
           {
             chargeInfo.setBasis("Per Kilogram");
           }
           else if("Per Lb".equalsIgnoreCase(rs.getString("CHARGEBASIS")))
           {
             chargeInfo.setBasis("Per Pound");
           }
           else if("Per CFT".equalsIgnoreCase(rs.getString("CHARGEBASIS")))
           {
             chargeInfo.setBasis("Per Cubic Feet");
           }
           else if("Per CBM".equalsIgnoreCase(rs.getString("CHARGEBASIS")))
           {
             chargeInfo.setBasis("Per Cubic Meter");
           }
           else
             chargeInfo.setBasis(rs.getString("CHARGEBASIS"));
         
         if(weightBreak!=null && ("Percent".equalsIgnoreCase(weightBreak) || weightBreak.endsWith("%")))
           chargeInfo.setPercentValue(true);
         
       }
       chargeInfo.setRatio(rs.getString("DENSITY_RATIO"));
       chargeInfo.setLineNumber(rs.getInt("LINE_NO"));
       chargeInfo.setSellRate(sellRate);
      chargeInfo.setTieSellRateValue(sellRate);
       chargeInfo.setSelectedFlag(rs.getString("SELECTED_FLAG"));

       
     }
     else
     {
       chargesDOB  = new MultiQuoteCharges();

     
       if("Origin".equalsIgnoreCase(rs.getString("COST_INCURREDAT")))
         originChargesList.add(chargesDOB);
       else if("Destination".equalsIgnoreCase(rs.getString("COST_INCURREDAT")))
    	   tempdestChargesList.add(chargesDOB);
       
       //if it is a sell charge/rate
       if("S".equalsIgnoreCase(flag))
       {
         chargesDOB.setSellChargeId(rs.getString("SELLCHARGEID"));
         chargesDOB.setBuyChargeId(rs.getString("BUY_CHARGE_ID"));
       }
       else if("B".equalsIgnoreCase(flag))
       {
         chargesDOB.setBuyChargeId(rs.getString("SELLCHARGEID"));
       }
                       
       chargesDOB.setSellBuyFlag(flag);
     
       chargesDOB.setChargeId(rs.getString("CHARGE_ID"));
       chargesDOB.setTerminalId(rs.getString("TERMINALID"));
       chargesDOB.setMarginDiscountFlag(rs.getString("MARGIN_DISCOUNT_FLAG"));
       chargesDOB.setChargeDescriptionId(rs.getString("CHARGEDESCID"));
       chargesDOB.setInternalName(rs.getString("INT_CHARGE_NAME"));
       chargesDOB.setExternalName(rs.getString("EXT_CHARGE_NAME"));
       chargesDOB.setCostIncurredAt(rs.getString("COST_INCURREDAT"));
       chargesDOB.setSelectedFlag(rs.getString("SELECTED_FLAG"));
       
       chargeInfoList  = new ArrayList();
       chargeInfo  = new MultiQuoteChargeInfo();
       chargeInfoList.add(chargeInfo);
       
       chargesDOB.setChargeInfoList(chargeInfoList);
       
       chargeInfo.setBreakPoint(rs.getString("CHARGESLAB"));
       if(rs.getString("CURRENCY")!=null && rs.getString("CURRENCY").trim().length()!=0)
           chargeInfo.setCurrency(rs.getString("CURRENCY"));
       else
            chargeInfo.setCurrency(masterDOB.getTerminalCurrency());
            
       chargeInfo.setBuyRate(rs.getDouble("BUYRATE"));
       chargeInfo.setRecOrConSellRrate(rs.getDouble("SELLRATE"));
       chargeInfo.setSellChargeMargin(rs.getDouble("MARGINVALUE"));
       chargeInfo.setSellChargeMarginType(rs.getString("MARGIN_TYPE"));
       chargeInfo.setRateIndicator(rs.getString("RATE_INDICATOR"));
       weightBreak   = rs.getString("WEIGHT_BREAK");
       rateType      = rs.getString("RATE_TYPE");
       if("BASE".equalsIgnoreCase(chargeInfo.getBreakPoint()) || "MIN".equalsIgnoreCase(chargeInfo.getBreakPoint()) || "MAX".equalsIgnoreCase(chargeInfo.getBreakPoint()) || ("SLAB".equalsIgnoreCase(weightBreak) && "FLAT".equalsIgnoreCase(rateType))  || ("BOTH".equalsIgnoreCase(rateType) && "F".equalsIgnoreCase(chargeInfo.getRateIndicator()))) // MODIFIED FOR 183812 & 195552 ON 25-JAN-10 BY SUBRAHMANYAM 
       {
         chargeInfo.setBasis("Per Shipment");
       }
       else
       {
           if("Per Kg".equalsIgnoreCase(rs.getString("CHARGEBASIS")))
           {
             chargeInfo.setBasis("Per Kilogram");
           }
           else if("Per Lb".equalsIgnoreCase(rs.getString("CHARGEBASIS")))
           {
             chargeInfo.setBasis("Per Pound");
           }
           else if("Per CFT".equalsIgnoreCase(rs.getString("CHARGEBASIS")))
           {
             chargeInfo.setBasis("Per Cubic Feet");
           }
           else if("Per CBM".equalsIgnoreCase(rs.getString("CHARGEBASIS")))
           {
             chargeInfo.setBasis("Per Cubic Meter");
           }
           else
             chargeInfo.setBasis(rs.getString("CHARGEBASIS"));
         
         if(weightBreak!=null && ("Percent".equalsIgnoreCase(weightBreak) || weightBreak.endsWith("%")))
           chargeInfo.setPercentValue(true);            
       }
       chargeInfo.setRatio(rs.getString("DENSITY_RATIO"));
       chargeInfo.setLineNumber(rs.getInt("LINE_NO"));
       if("B".equalsIgnoreCase(flag)||"M".equalsIgnoreCase(chargesDOB.getMarginDiscountFlag()))
       {  
         chargeInfo.setMargin(rs.getDouble("MARGINVALUE"));
         chargeInfo.setTieMarginDiscountValue(rs.getDouble("MARGINVALUE"));
           if(!"Y".equals(chargesDOB.getSelectedFlag()))
           chargeInfo.setMarginType(mType);
           else
           chargeInfo.setMarginType(rs.getString("MARGIN_TYPE")!=null?rs.getString("MARGIN_TYPE"):mType);
       if(rs.getDouble("MARGINVALUE")>0 || "Y".equalsIgnoreCase(rs.getString("SELECTED_FLAG")))
       {
       if("A".equalsIgnoreCase(chargeInfo.getMarginType()))
           sellRate  = rs.getDouble("BUYRATE")+rs.getDouble("MARGINVALUE");
      else if("P".equalsIgnoreCase(chargeInfo.getMarginType()))
           sellRate  = rs.getDouble("BUYRATE")+(rs.getDouble("BUYRATE")*rs.getDouble("MARGINVALUE")/100);
      else if(chargeInfo.getMarginType() == null || "".equals(chargeInfo.getMarginType()))
      sellRate  = rs.getDouble("BUYRATE");
       
       }
      else
        sellRate  = rs.getDouble("BUYRATE");
      }
      else
        {
         chargeInfo.setDiscount(rs.getDouble("MARGINVALUE"));
         chargeInfo.setTieMarginDiscountValue(rs.getDouble("MARGINVALUE"));
          if(!"Y".equals(chargesDOB.getSelectedFlag()))
         chargeInfo.setDiscountType(dType);
         else
         chargeInfo.setDiscountType(rs.getString("MARGIN_TYPE"));
         if("A".equalsIgnoreCase(chargeInfo.getDiscountType()))
           sellRate  = rs.getDouble("SELLRATE")-rs.getDouble("MARGINVALUE");
        else if("P".equalsIgnoreCase(chargeInfo.getDiscountType()))
           sellRate  = rs.getDouble("SELLRATE")-(rs.getDouble("SELLRATE")*rs.getDouble("MARGINVALUE")/100);
        else if(chargeInfo.getMarginType() == null || "".equals(chargeInfo.getMarginType()))
        sellRate  = rs.getDouble("SELLRATE");
       }
      chargeInfo.setSellRate(sellRate);
       chargeInfo.setSelectedFlag(rs.getString("SELECTED_FLAG"));
       chargeInfo.setTieSellRateValue(sellRate);

     }
   }
       if(delChargesDOB != null)
    	   tempdestChargesList.add(delChargesDOB);
       if(orgChargesDOB != null)
    	   originChargesList.add(orgChargesDOB);
         //@@          
	      //Added by Mohan for Issue No.219976 on 04-11-2010
       /*if(masterDOB.getOperation()!=null && "Modify".equals(masterDOB.getOperation()))
       {
     	  //notesPstmt= connection.prepareStatement(notesQry);Added by Gowtham for Connection leaks
     	  notesPstmt.setString(1, masterDOB.getQuoteId());
     	  notesPstmt.setString(2,masterDOB.getOriginLocation()[fr]);
     	  notesPstmt.setString(3,masterDOB.getDestLocation()[fr]);
     	  notesRs  = notesPstmt.executeQuery();
          notesPstmt.clearParameters();//Added by Gowtham for Connection leaks
//   	  Modified by Mohan on 30102010
     	  
       	  while(notesRs.next())
     	  {
     		  iNotes.add((notesRs.getString("INTERNAL_NOTES")!=null)?notesRs.getString("INTERNAL_NOTES"):"");
     		  eNotes.add((notesRs.getString("EXTERNAL_NOTES")!=null)?notesRs.getString("EXTERNAL_NOTES"):"");
     	  }
     	 
       }*/
       legRateDetails.setOriginChargesList(originChargesList);
       if(destChargesList != null && destChargesList.size()>0){//Added by govind for getting the deatination charges at end
    	   for(int sz=0;sz<destChargesList.size();sz++)
    	   {
    		   tempdestChargesList.add(destChargesList.get(sz));
    	   }
       }
       
       legRateDetails.setDestChargesList(tempdestChargesList); 
       finalLegDetails.add(legRateDetails);
       csmt.clearParameters();
       rs.close();
       delChargesDOB = null;
       orgChargesDOB = null;
   }else{
	   legRateDetails.setOriginChargesList(originChargesList);
	   
	   if(destChargesList != null && destChargesList.size()>0){//Added by govind for getting the destination charges at end
		   if(tempdestChargesList==null)
		     {
		    	 tempdestChargesList = new ArrayList();
		     }
    	   for(int sz=0;sz<destChargesList.size();sz++)
    	   {
    		   tempdestChargesList.add(destChargesList.get(sz));
    	   }
       }
       legRateDetails.setDestChargesList(tempdestChargesList); 
      
       finalLegDetails.add(legRateDetails);
       csmt.clearParameters();
       rs.close();
       delChargesDOB = null;
       orgChargesDOB = null;
   }
   
   
}
	   }
   if(masterDOB.getOperation()!=null && fr==0 && ("Modify".equals(masterDOB.getOperation()) || "View".equalsIgnoreCase(masterDOB.getOperation())))
   {
 	  //notesPstmt= connection.prepareStatement(notesQry);Added by Gowtham for Connection leaks
 	  notesPstmt.setString(1, masterDOB.getQuoteId());
 	  notesPstmt.setString(2,masterDOB.getOriginLocation()[fr]);
 	  notesPstmt.setString(3,masterDOB.getDestLocation()[fr]);
 	  notesRs  = notesPstmt.executeQuery();
      notesPstmt.clearParameters();//Added by Gowtham for Connection leaks
//	  Modified by Mohan on 30102010
 	  
   	  while(notesRs.next())
 	  {
 		  iNotes.add((notesRs.getString("INTERNAL_NOTES")!=null)?notesRs.getString("INTERNAL_NOTES"):"");
 		  eNotes.add((notesRs.getString("EXTERNAL_NOTES")!=null)?notesRs.getString("EXTERNAL_NOTES"):"");
 	  }
 	 
   }
 
   originChargesList = null;
   tempdestChargesList = null;
   }
      notesCount = iNotes.size();

	 if(iNotes != null && iNotes.size()>0)
	 {
      inotes = new String[notesCount];
	  enotes = new String[notesCount];

	  for( int i=0; i<notesCount;i++)
	  {
		  inotes[i] = (String)iNotes.get(i);
		  enotes[i] = (String)eNotes.get(i);
	  }
	  if(masterDOB.getOperation()!=null && ("Modify".equals(masterDOB.getOperation()) || "View".equals(masterDOB.getOperation())))//Modified by Rakesh for Issue:          on 17-03-2011
      {
	  finalDOB.setInternalNotes(inotes);//Modified by Mohan on 30102010
	  finalDOB.setExternalNotes(enotes);//Modified by Mohan on 30102010
      }
	 }
	 if(finalLegDetails != null && finalLegDetails.size()>0)
       finalDOB.setLegDetails(finalLegDetails);
 }
 catch(SQLException sqEx)
		{
   sqEx.printStackTrace();
			//Logger.error(FILE_NAME,"QMSMultiQuoteDAO[getLegCharges(masterDOB,sellBuyRate,sellBuyFlag,laneNo,origin,dest)] -> "+sqEx.toString());
   logger.error(FILE_NAME+"QMSMultiQuoteDAO[getLegCharges(masterDOB,sellBuyRate,sellBuyFlag,laneNo,origin,dest)] -> "+sqEx.toString());
   throw new SQLException(sqEx.toString());
		}
 catch(Exception e)
		{
   e.printStackTrace();
			//Logger.error(FILE_NAME,"QMSMultiQuoteDAO[getLegCharges(masterDOB,sellBuyRate,sellBuyFlag,laneNo,origin,dest)] -> "+e.toString());
   logger.error(FILE_NAME+"QMSMultiQuoteDAO[getLegCharges(masterDOB,sellBuyRate,sellBuyFlag,laneNo,origin,dest)] -> "+e.toString());
   throw new SQLException(e.toString());
		}
		finally
		{
			try
			{
				ConnectionUtil.closeConnection(connection,csmt,rs);
				ConnectionUtil.closePreparedStatement(notesPstmt,notesRs);//Added by Mohan for Issue No.219976 on 04-11-2010
				if(pstmt!=null)
				{
					pstmt.close(); //@@Added by Kameswari for the WPBN issue - on 02/02/2012
				}
			}
			catch(Exception ex)
			{
				//Logger.error(FILE_NAME,"Finally : QMSMultiQuoteDAO[getLegCharges(masterDOB,sellBuyRate,sellBuyFlag,laneNo,origin,dest)]-> "+ex.toString());
     logger.error(FILE_NAME+"Finally : QMSMultiQuoteDAO[getLegCharges(masterDOB,sellBuyRate,sellBuyFlag,laneNo,origin,dest)]-> "+ex.toString());
     throw new SQLException(ex.toString());
			}
		}
 return finalDOB;
}

//getLegrates
//public MultiQuoteFreightLegSellRates getLegRates(MultiQuoteMasterDOB masterDOB,MultiQuoteFreightRSRCSRDOB ratesDOB,String legOrigin,String legDest,String sellBuyRateId,String buyRateId,String laneNo,String sellBuyFlag,double marginLimit,String shipmentMode,String currency,String densityRatio,String[] container) throws SQLException
public MultiQuoteFinalDOB getLegRates(MultiQuoteFinalDOB finalDOB) throws SQLException
{
  Connection                connection                   = null;
  CallableStatement         csmt                         = null;
  ResultSet                 rs                      	 = null;
  ArrayList                 freightChargesList      	 = null;//to maintain the list of  all origin charge dobs
  MultiQuoteFreightLegSellRates  legChargesDetails       = null;//to maintain the charges info of each leg
  MultiQuoteChargeInfo           chargeInfo              = null;
  HashMap 					     rateLane				 = new HashMap();
  ArrayList                 	 chargeInfoList          = null;
  ArrayList 					 list44                  = null;
  ArrayList 					 legDetails              = null;
  String                         flag                    = null;
  String                         weightBreak             = null;
  String                         rateType                = null;
  double                         sellRate                = 0;
  int                            i 						 = 0;
  int 							 selectedRateIndex		 = 0;
  int                            list44Size              = 0;
  int                            uniqueIdCount           = 0;
  int                         multiQuote_SerialNo     	 = 0;//@Added by govind for maintaining order for the lanes While Quote Add
  String                         breakPoint              = null;
  String  						 margin_type   			 = null;//@Added by govind for getting default margin/discount type when Quote Add
  String  						 discount_type   		 = null;//@Added by govind for getting default margin/discount type when Quote Add
  
  MultiQuoteFreightLegSellRates legRateDetails = null;
  ArrayList	rates	= new ArrayList();
  ArrayList freightRates	= new ArrayList();
  MultiQuoteFreightRSRCSRDOB ratesDOB	=	 null;
  MultiQuoteMasterDOB masterDOB	= finalDOB.getMasterDOB();
  String mType = null;
  String dType = null;
  String terminalFlag = null;
  PreparedStatement pstmt = null;
  PreparedStatement pstmt1 = null;
  PreparedStatement pstmt2 = null;//Added by Anil.k for Spot Rates
  ResultSet         rs1    = null;//Added by Anil.k for Spot Rates
  //Statement truncStmt = null;
  String query = "SELECT MARGIN_TYPE,DISCOUNT_TYPE FROM FS_FR_TERMINALMASTER TM WHERE TM.TERMINALID IN(SELECT RG.PARENT_TERMINAL_ID FROM FS_FR_TERMINAL_REGN RG WHERE RG.CHILD_TERMINAL_ID = ?)";
  String query1 = "SELECT STM.OPER_ADMIN_FLAG FROM Fs_Fr_Terminalmaster STM WHERE STM.TERMINALID =? ";
  String query2 = "SELECT MARGIN_TYPE,DISCOUNT_TYPE FROM FS_FR_TERMINALMASTER TM WHERE TM.TERMINALID =? ";
  
  final String tempDataQry	=	" insert into TEMP_BUYRATESDTL_PROC_TABLE (ORIGIN_LOCATION, DEST_LOCATION, SELL_BUY_RATE_ID, BUYRATE_ID, LANE_NO, SALES_PERSON,"+
  								" TERMINAL_ID, PERMISSION, SELLRATE_FLAG, MARGIN, OPERATION, QUOTE_ID, CUSTOMER_ID, QUOTE_ORIGIN, QUOTE_DEST, CARRIER, SERVICE_LEVEL,"+
  								" FREQUENCY, FREIGHT_TERMINAL, SHIMPMENT_MODE,INCO_TERMS,FREIGHT_RATE_VALIDITY,TRANSIT_TIME,MULTIQUOTE_SERIALNO) values (?, ?, ?,?,?, ?, ?, ?,?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
 
  //final String tempDtataTruncQry = "truncate table temp_buyratesdtl_proc_table";
  
  //Added by Anil.k for Spot Rates
  HashMap			spotRateFlag = new HashMap();
  final String spotRateTempQry = " insert into SPOTRATE_CHARGES_PROC_TABLE(SELLCHARGEID,CHARGE_ID,CHARGEDESCID,COST_INCURREDAT,CHARGESLAB,CURRENCY,BUYRATE,SELLRATE,MARGIN_TYPE,"+
  								 " MARGINVALUE,CHARGEBASIS,SEL_BUY_FLAG,BUY_CHARGE_ID,ZONE,EFROM,VALIDUPTO,TERMINALID,WEIGHT_BREAK,PRIMARY_BASIS,SECONDARY_BASIS,TERTIARY_BASIS,"+
  								 " BLOCK,RATE_TYPE,WEIGHT_SCALE,NOTES,FREQUENCY,TRANSITTIME,ORG,DEST,SHMODE,SRV_LEVEL,LEG_SL_NO,DENSITY_RATIO,SELECTED_FLAG,LBOUND,UBOUND,RATE_INDICATOR,"+
  								 " MARGIN_DISCOUNT_FLAG,LANE_NO,LINE_NO,INT_CHARGE_NAME,EXT_CHARGE_NAME,MARGIN_TEST_FLAG,CHARGE_GROUP_ID,SHIPMENTMODE,RATE_DESCRIPTION,CONSOLE_TYPE,"+
  								 " CARRIER,FREQUENCY_CHECKED,TRANSITTIME_CHECKED,CARRIER_CHECKED,RATEVALIDITY_CHECKED,VERSION_NO,CHANGE_FLAG,CHECKED_FLAG,QUOTE_ID,QUOTE_UNIQUE_ID,ORIGIN_PORT,"+
  								 " DEST_PORT,INCO_TERMS,CARRIER_ID,SERVICE_LEVEL_ID,MULTIQUOTE_SERIALNO,FREIGHT_RATE_VALIDITY) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
  // @@Modified by kiran.v 02/11/2011
  Hashtable spotRateDetails    = null;
	  //@@Added by kiran.v on 26/09/2011 for Wpbn Issue   272712 
  ESupplyDateUtility      dateFormatter       =  new ESupplyDateUtility();

  
  //ends 170758
  try
  {
	  
	if(finalDOB!=null)
	    {
	      freightRates  = finalDOB.getLegDetails();
	    }
	   int freightRatesSize=0;
	if(freightRates!=null)
	   {
	      freightRatesSize  = freightRates.size();//get the no fo legs since the freight rates size gives the no of legs
	   }
		    //get the charges for different legs
	    connection  = this.getConnection();
	  /*  truncStmt = connection.createStatement();
	     int row = truncStmt.executeUpdate(tempDtataTruncQry);
	     System.out.println("row----------"+row);
	    if(truncStmt != null)
	     truncStmt.close();*/
	    pstmt1= connection.prepareStatement(query1); // Added by Gowtham on 04Feb2011 for Loop Leakages.
	    for(int j=0;j<freightRatesSize;j++)
	    {
	      if("View".equalsIgnoreCase(masterDOB.getOperation()) || (masterDOB.getSpotRatesFlag()!=null && "N".equalsIgnoreCase(masterDOB.getSpotRatesFlag()[j]))){//Added by Anilk for Spot Rates
	       legRateDetails = (MultiQuoteFreightLegSellRates)freightRates.get(j);
	       selectedRateIndex = "N".equalsIgnoreCase(masterDOB.getSpotRatesFlag()[j])?legRateDetails.getSelectedFreightSellRateIndex().length:1;//Modified by Anil.k for Spot Rates
	       uniqueIdCount  += selectedRateIndex;//Added by govind for the issue escalated quotes error 
	      //if(tiedCustInfoList!=null)
	      //tiedCustInfoLeg = (MultiQuoteFreightLegSellRates)tiedCustInfoList.get(i);
	      rates = "N".equalsIgnoreCase(masterDOB.getSpotRatesFlag()[j])?legRateDetails.getRates():null;//Modified by Anil.k for Spot Rates
	     for(int srats=0;srats<selectedRateIndex;srats++) {
	     if(!legRateDetails.isSpotRatesFlag())// && tiedCustInfoLeg==null
	      {
	        ratesDOB  = (MultiQuoteFreightRSRCSRDOB)rates.get( legRateDetails.getSelectedFreightSellRateIndex()[srats].intValue());
	      }
	      
	      String  sellBuyRateId = "";
	      String  buyRateId     = "";
	      String  laneNo        = "";  
	      String  sellOrBuyFlag = "";
	      String  origin        = "";
	      String  destination   = "";
	      String  currency      = null;//@@added by kameswari for the issue WPBN-30908
	     
	           
	      //get the sell rate or the buy rate index and lane no that are selected
	      if(!legRateDetails.isSpotRatesFlag())
	      {
	        double marginLimit;
			//if(tiedCustInfoLeg==null)
	        //{
	          if("BR".equalsIgnoreCase(ratesDOB.getRsrOrCsrFlag()))
	          {
	            sellOrBuyFlag = "N";
	            sellBuyRateId = ""+ratesDOB.getBuyRateId();
	            marginLimit     =  legRateDetails.getMarginLimit();
	         
	          }
	          else
	          {
	            sellOrBuyFlag = "Y";
	            sellBuyRateId = ""+ratesDOB.getSellRateId();
	            buyRateId     = ""+ratesDOB.getBuyRateId();
	            marginLimit     =  legRateDetails.getDiscountLimit();
	          }
	          
	          laneNo          = ""+ratesDOB.getLaneNo();
	         // marginLimit     =  legRateDetails.getMarginLimit();
	          
	        //}
	      }
	      else
	      {
	    	sellBuyRateId = ""+masterDOB.getUniqueIds()[uniqueIdCount-1];
	        sellOrBuyFlag = "SBR";
	        laneNo        = ""+i;
	       }
	      origin      			= legRateDetails.getOrigin();
	      destination 			= legRateDetails.getDestination();
	      currency    			= legRateDetails.getCurrency(); 
	      multiQuote_SerialNo   = legRateDetails.getMultiQuote_SerialNo();
//	    }
	      if(!"SBR".equalsIgnoreCase(sellOrBuyFlag))
	      rateLane.put(sellBuyRateId+"-"+laneNo,new Integer(j)); // laneNo Added by Gowtham to show origin and Destination charges in pdf view correctly
	      else
	    	  rateLane.put(sellBuyRateId+"-"+j,new Integer(j));	      
	      spotRateFlag.put(sellBuyRateId, masterDOB.getSpotRatesFlag()[j]);//Added by Anil.k for Spot Rates
	      //added by phani sekhar for wpbn 170758 on 20090626    
     //pstmt1= connection.prepareStatement(query1); Commented by Gowtham on 04Feb2011 for Loop Leakages.
   pstmt1.setString(1,masterDOB.getQuotingStation());
    rs=pstmt1.executeQuery();
    pstmt1.clearParameters();
    if(rs.next())
    {
    terminalFlag=rs.getString("OPER_ADMIN_FLAG");
    }
    /*ConnectionUtil.closePreparedStatement(pstmt1,rs);
    if(!"H".equals(terminalFlag))
    {*/
    if("A".equals(terminalFlag)||"H".equals(terminalFlag))
    pstmt= connection.prepareStatement(query2);
    else
    pstmt= connection.prepareStatement(query);
  //  pstmt.setString(1,masterDOB.getQuotingStation());
  pstmt.setString(1,masterDOB.getTerminalId());
    rs=pstmt.executeQuery();
    if(rs.next())
    {
    mType = rs.getString("MARGIN_TYPE");
    dType = rs.getString("DISCOUNT_TYPE");
    }
    ConnectionUtil.closePreparedStatement(pstmt,rs);
   // }
    //ENDS 170758
   // connection.setAutoCommit(false);
    pstmt	= connection.prepareStatement(tempDataQry);
    //csmt  = connection.prepareCall("{CALL QMS_QUOTE_PACK.quote_sell_buy_ratesdtl_proc(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");
//    csmt  = connection.prepareCall("{CALL QMS_QUOTE_PACK.quote_sell_buy_ratesdtl_proc(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");
    
    pstmt.setString(1,origin);
    pstmt.setString(2,destination);
    pstmt.setString(3,sellBuyRateId);
    pstmt.setString(4,buyRateId);
    pstmt.setString(5,"SBR".equalsIgnoreCase(sellOrBuyFlag)?j+"":laneNo);
    pstmt.setString(6,masterDOB.getSalesPersonCode());
    pstmt.setString(7,masterDOB.getTerminalId());
    pstmt.setString(8,masterDOB.getBuyRatesPermission());
    pstmt.setString(9,sellOrBuyFlag);
    pstmt.setDouble(10,legRateDetails.getMarginLimit());
    pstmt.setString(11,masterDOB.getOperation());//("View".equalsIgnoreCase(masterDOB.getOperation())?"Modify":masterDOB.getOperation()));//Modified by Rakesh on 10-01-2011
    pstmt.setString(12,""+masterDOB.getQuoteId());
    pstmt.setString(13,masterDOB.getCustomerId());
    pstmt.setString(14,masterDOB.getOriginLocation()[j]);
    pstmt.setString(15,masterDOB.getDestLocation()[j]);
       //csmt.set
    if("SBR".equalsIgnoreCase(sellOrBuyFlag))
    {
    	//Modified by Anil.k for Spot Rates
    	/*pstmt.setNull(16,Types.VARCHAR);
    	pstmt.setNull(17,Types.VARCHAR);
    	pstmt.setNull(18,Types.VARCHAR);
    	pstmt.setNull(19,Types.VARCHAR);
    	pstmt.setNull(20,Types.VARCHAR);*/
    	pstmt.setString(16,legRateDetails.getCarrier());
    	pstmt.setString(17,legRateDetails.getServiceLevel());
    	pstmt.setString(18,legRateDetails.getFrequency());
    	pstmt.setString(19,masterDOB.getTerminalId());
    	pstmt.setString(20,""+masterDOB.getShipmentMode());
    	pstmt.setString(21,masterDOB.getIncoTermsId()[j]);
    	pstmt.setTimestamp(22,masterDOB.getValidTo());
    	pstmt.setString(23, legRateDetails.getTransitTime());
    	pstmt.setInt(24, multiQuote_SerialNo);
    	//Ended by Anil.k for Spot Rates
    }
    else
    {
    	pstmt.setString(16,ratesDOB.getCarrierId());
    	pstmt.setString(17,ratesDOB.getServiceLevelId());
    	pstmt.setString(18,ratesDOB.getFrequency());
    	pstmt.setString(19,ratesDOB.getCreatedTerminalId());
    	pstmt.setString(20,""+masterDOB.getShipmentMode());
    	pstmt.setString(21,ratesDOB.getIncoTerms());
    	pstmt.setTimestamp(22,ratesDOB.getValidUpTo());
    	pstmt.setString(23, ratesDOB.getTransitTime());
    	pstmt.setInt(24, multiQuote_SerialNo);
    }
//    csmt.registerOutParameter(21,OracleTypes.CURSOR);
    
    pstmt.executeUpdate();
    pstmt.clearParameters();
	    }
  }
  else{
	  legRateDetails = (MultiQuoteFreightLegSellRates)freightRates.get(j);
	  spotRateDetails = legRateDetails.getSpotRateDetails();
	  ArrayList spotRateDesc = legRateDetails.getSpotRateDescription();
	  ArrayList checkedFlag  = legRateDetails.getCheckedFlag();
	  ArrayList marginType   = legRateDetails.getMarginType();
	  ArrayList marginValue  = legRateDetails.getMarginValue();
	  double[] rate = null;
	  double spotRateId = 0;
	  String[] currencyId = legRateDetails.getCurrency().split(",");
	  String   spotDesc   = "";
	  for(int sp=0;sp<spotRateDetails.size();sp++)
	  {
		  /*pstmt2 = connection.prepareStatement("SELECT QUOTE_SPOT_RATES_SEQ.NEXTVAL ID FROM DUAL");
		  rs1 = pstmt2.executeQuery();
		  if(rs1.next())
		  {
			  spotRateId = rs1.getDouble("ID");
		  }*/
	  String wtBreak = (String)legRateDetails.getWeightBreaks().get(sp);
	  rate = (double[])spotRateDetails.get(wtBreak);
	  pstmt	= connection.prepareStatement(spotRateTempQry);
	  pstmt.setString(1,"X"+j);
	  pstmt.setString(2,"");
	  pstmt.setString(3,"Freight Rate");
	  pstmt.setString(4,"Carrier");
	  pstmt.setString(5,wtBreak.toUpperCase());//Weight Break
	  //pstmt.setString(6,currencyId[0]);
	  //@@Added by kiran.v on 01/08/2011 for Wpbn Issue 257513
	  pstmt.setString(6,legRateDetails.getCurrency());
	  pstmt.setDouble(7,rate[2]);
	  pstmt.setDouble(8,0.0);//Sell Rate
	 
	  pstmt.setString(9,marginType!=null?(marginType.size()>0 &&(String)marginType.get(sp)!= null)?(String)marginType.get(sp):"A":"A");//Margin Type
	  pstmt.setDouble(10,marginValue!=null?(marginValue.size()>0 && (Double)marginValue.get(sp)!= null)?(Double)marginValue.get(sp):0.0:0.0);//Margin Value
	  pstmt.setString(11,legRateDetails.getUom());//Charge Basis EX:Per Kg
	  pstmt.setString(12,"SBR");//Sell Buy Flag is always "SBR" for Spot Rates
	  pstmt.setString(13,"");//Buy Charge Id
	  pstmt.setString(14,"");//Zone
	  pstmt.setTimestamp(15,masterDOB.getCreatedDate());//From Date
	  pstmt.setTimestamp(16,masterDOB.getValidTo());//Valid To Date
	  pstmt.setString(17,masterDOB.getTerminalId());//Terminal Id
	  pstmt.setString(18,"Flat".equalsIgnoreCase(legRateDetails.getSpotRatesType())?"FLAT":legRateDetails.getSpotRatesType());//Weight Break
	  pstmt.setString(19,"");//Primary Basis
	  pstmt.setString(20,"");//Secondary Basis
	  pstmt.setString(21,"");//Teritory Basis
	  pstmt.setString(22,"");//Block
	  pstmt.setString(23,"Flat".equalsIgnoreCase(legRateDetails.getSpotRatesType())?"FLAT":legRateDetails.getSpotRatesType());//Rate Type
	  pstmt.setString(24,"");//Weight Scale
	  pstmt.setString(25,"");//Note
	  pstmt.setString(26,legRateDetails.getFrequency());//Frequency
	  pstmt.setString(27,legRateDetails.getTransitTime());//Transit Time
	  pstmt.setString(28,masterDOB.getOriginLocation()[j]);//Origin Location
	  pstmt.setString(29,masterDOB.getDestLocation()[j]);//Destination Location
	  pstmt.setInt(30,masterDOB.getShipmentMode());//Shipment Mode
	  pstmt.setString(31,legRateDetails.getServiceLevel());//Service Level
	  pstmt.setString(32,"");//Leg Serial No
	  pstmt.setString(33,legRateDetails.getDensityRatio());//Density Ratio
	  pstmt.setString(34,checkedFlag!=null?"Y":"N");//Selected Flag
	  pstmt.setDouble(35,rate[1]);//Lower Bound
	  pstmt.setDouble(36,rate[0]);//Upper Bound
	  pstmt.setString(37,"");//Rate Indicatoer
	  pstmt.setString(38,"");//Margin Discount Flag
	  pstmt.setInt(39,j);//Lane Number
	  pstmt.setInt(40,sp);//Line Number
	  pstmt.setString(41,"");//Internal Charge Name
	  pstmt.setString(42,"");//External Charge Name
	  pstmt.setString(43,"");//Margin Test Flag
	  pstmt.setString(44,"");//Charge Group Id
	  pstmt.setString(45,masterDOB.getShipmentMode()+"");//Shipper Mode
	  //pstmt.setDouble(46,spotRateId);//Unique ID
	  pstmt.setString(46,(String)spotRateDesc.get(sp));//Rate Description
	  if(legRateDetails.getConsoleType()!= null && !"".equals(legRateDetails.getConsoleType()))
	  pstmt.setString(47,legRateDetails.getConsoleType());//Console Type
	  else if( masterDOB.getShipmentMode() == 2 && "LIST".equalsIgnoreCase(legRateDetails.getSpotRatesType()))
	  pstmt.setString(47,"FCL");//Console Type
	  else if(masterDOB.getShipmentMode() == 2 && !"LIST".equalsIgnoreCase(legRateDetails.getSpotRatesType()))
	  pstmt.setString(47,"LCL");//Console Type
	  else
	  pstmt.setString(47,"");//Console Type  
	  pstmt.setString(48,legRateDetails.getCarrier());//Carrier 
	  pstmt.setString(49,"on");//Frequency Checked
	  pstmt.setString(50,"on");//Transit Time Checked
	  pstmt.setString(51,"on");//Carrier Checked
	  pstmt.setString(52,"");//Rate Validity Ratio
	  pstmt.setInt(53,1);//Version No
	  pstmt.setString(54,"N");//Changed Flag
	  pstmt.setString(55,checkedFlag!=null?(String)checkedFlag.get(sp):"");//Checked Flag
	  pstmt.setString(56,masterDOB.getQuoteId()!=null?masterDOB.getQuoteId():"");//Quote ID
	  pstmt.setLong(57,masterDOB.getUniqueId()!=0?masterDOB.getUniqueId():0);//Quote Unique ID
	  pstmt.setString(58,masterDOB.getOriginPort()[j]);//Origin Port
	  pstmt.setString(59,masterDOB.getDestPort()[j]);//Destination Port
	  pstmt.setString(60,masterDOB.getIncoTermsId()[j]);//Inco Terms
	  pstmt.setString(61,legRateDetails.getCarrier());//Carrier 
	  pstmt.setString(62,legRateDetails.getServiceLevel());//Service Level
	  pstmt.setInt(63,legRateDetails.getMultiQuote_SerialNo());
	  pstmt.setTimestamp(64,masterDOB.getValidTo());// @@Added by kiran.v 02/11/2011
	  pstmt.executeUpdate();
	  pstmt.clearParameters();
	  spotRateFlag.put("X"+j, masterDOB.getSpotRatesFlag()[j]);
	  rateLane.put("X"+j+"-"+j,new Integer(j));
  	}
  }
  }
	    
	    long start = System.currentTimeMillis();
	    csmt  = connection.prepareCall("{CALL QMS_QUOTE_PACK.MultiLane_Buryates_dtl_Proc(?)}");
	    csmt.registerOutParameter(1,OracleTypes.CURSOR);
	    csmt.execute();
    rs  = (ResultSet)csmt.getObject(1);
logger.info("Time Taken for DB procedure in milli seconds for 3rd screen (quote_sell_buy_ratesdtl_proc) :  " + ((System.currentTimeMillis()) - start) + "    UserId ::"+ masterDOB.getUserId() + " Origin :: "+ masterDOB.getOriginLocation() + " Destination::"+masterDOB.getDestLocation()+" TerminalId :: "+ masterDOB.getTerminalId());
chargeInfoList = new ArrayList();
	while(rs.next())
    {
    // added by VLAKSHMI for issue 146968 on 5/12/2008
    /*int count =0;
    String[] container = legRateDetails.getContainerTypes();
   if(!"SBR".equalsIgnoreCase(rs.getString("SEL_BUY_FLAG"))&&"LIST".equalsIgnoreCase(ratesDOB.getWeightBreakType()) && "FCL".equalsIgnoreCase(ratesDOB.getConsoleType()) && container!=null)
   {
 int containerLen	=	container.length;
  for(int k=0;k<containerLen;k++)
    {
      if(container[k]!=null && container[k].trim().length()>0 && (rs.getString("CHARGESLAB").startsWith(container[k])))
      {
        
        count++;
       break;
      }
   
 }
     } else 
   {
     count=1;
   }*/// end of  for issue 146968 on 5/12/2008
      flag = rs.getString("SEL_BUY_FLAG");
   
      if(freightChargesList==null)
      {
        freightChargesList = new ArrayList();
      }
  
      // added by VLAKSHMI for issue 146968 on 5/12/2008
/*if(count >0)   
      { */      
/*      if(chargesDOB!=null && (rs.getString("SELLCHARGEID").equalsIgnoreCase(chargesDOB.getSellChargeId()) || rs.getString("SELLCHARGEID").equalsIgnoreCase(chargesDOB.getBuyChargeId())))
      {*/
        chargeInfo  = new MultiQuoteChargeInfo();
        
        chargeInfoList.add(chargeInfo);
     
//        chargesDOB  = new MultiQuoteCharges();
        
    /*    if("Carrier".equalsIgnoreCase(rs.getString("COST_INCURREDAT")))
          freightChargesList.add(chargesDOB);*/
        
        chargeInfo.setSellBuyFlag(flag);
                     //if it is a sell charge/rate
         if("RSR".equalsIgnoreCase(chargeInfo.getSellBuyFlag()) || "CSR".equalsIgnoreCase(chargeInfo.getSellBuyFlag()))
        {  
        	 chargeInfo.setSellChargeId(rs.getString("SELLCHARGEID"));
        	 chargeInfo.setBuyChargeId(rs.getString("BUY_CHARGE_ID"));
        }
        else 
        {
        	chargeInfo.setBuyChargeId(rs.getString("SELLCHARGEID"));
        }
         chargeInfo.setSpotRatesFlag((String)spotRateFlag.get(rs.getString("SELLCHARGEID")));//Added by Anil.k for Spot Rates
         System.out.println("-sell-------"+rs.getString("SELLCHARGEID")+"buy----------------"+rs.getString("BUY_CHARGE_ID"));
         System.out.println(rateLane.get(rs.getString("SELLCHARGEID")));
         System.out.println(rateLane.get(rs.getString("BUY_CHARGE_ID")));
         if(rs.getString("SELLCHARGEID")!=null)
          chargeInfo.setSelectedLaneNum(((Integer)(rateLane.get(rs.getString("SELLCHARGEID")+"-"+rs.getString("LANE_NO")))).intValue()) ;
         else
          chargeInfo.setSelectedLaneNum(((Integer)(rateLane.get(rs.getString("BUY_CHARGE_ID")+"-"+rs.getString("LANE_NO")))).intValue())  ;
        //in case of freight rates
         chargeInfo.setVersionNo(rs.getString("VERSION_NO"));//@@Added for the WPBN issues-146448,146968 on 18/12/08
         chargeInfo.setBuyChargeLaneNo(rs.getString("LANE_NO"));
         chargeInfo.setTerminalId(rs.getString("TERMINALID"));
       //  System.out.println("margin_discount_flag-----------"+rs.getString("MARGIN_DISCOUNT_FLAG"));
         chargeInfo.setMarginDiscountFlag(rs.getString("MARGIN_DISCOUNT_FLAG"));
                  
         chargeInfo.setChargeDescriptionId(rs.getString("CHARGEDESCID"));
         //@@Added by Kameswari for the WPBN issue-146448 on 01/12/08  
         
         chargeInfo.setFrequencyChecked(rs.getString("FREQUENCY_CHECKED"));
         chargeInfo.setTransitTimeChecked(rs.getString("TRANSITTIME_CHECKED"));
         chargeInfo.setCarrierChecked(rs.getString("CARRIER_CHECKED"));
         chargeInfo.setRateValidityChecked(rs.getString("RATEVALIDITY_CHECKED"));
        //@@Added by kiran.v on 08/08/2011 for Wpbn Issue 258778
        chargeInfo.setServiceChecked("on");
         chargeInfo.setServicelevel(rs.getString("SRV_LEVEL"));
         chargeInfo.setFrequency(rs.getString("FREQUENCY"));
         chargeInfo.setTransitTime(rs.getString("TRANSITTIME"));
         //@@Added by kiran.v on 26/09/2011 for Wpbn Issue   272712 
         // 07/11/2011
         dateFormatter.setPattern("DD-MON-YYYY");
         String date=dateFormatter.getDisplayString(rs.getTimestamp("FREIGHT_RATE_VALIDITY"));
         chargeInfo.setRateValidity(date);
        // chargeInfo.setRateValidity(rs.getString("FREIGHT_RATE_VALIDITY"));
         chargeInfo.setCarrierName(rs.getString("CARRIER"));
         chargeInfo.setOrginLoc(rs.getString("org"));
         chargeInfo.setDestLoc(rs.getString("dest"));
         chargeInfo.setValidUpto(rs.getTimestamp("VALIDUPTO"));
        //@@WPBN issue-146448
         chargeInfo.setSelectedFlag(!"SBR".equalsIgnoreCase(rs.getString("SEL_BUY_FLAG"))?rs.getString("SELECTED_FLAG"):"Y");//@@Added by Kameswari for the WPBN issue-143250
         chargeInfo.setChecked_Flag(rs.getString("CHECKED_FLAG"));
         chargeInfo.setCostIncurredAt(rs.getString("COST_INCURREDAT"));
         chargeInfo.setConsoleType(rs.getString("CONSOLE_TYPE"));
      //   chargesDOB.setRateDescription(rs.getString("RATE_DESCRIPTION"));       
       
//        chargesDOB.setChargeInfoList(chargeInfoList);
        chargeInfo.setMultiBreakPoints(rs.getString("CHARGESLAB").split(","));
        chargeInfo.setCurrency(rs.getString("CURRENCY"));
        chargeInfo.setBasis(getBasisShortcut(rs.getString("CHARGEBASIS")));     
        chargeInfo.setMultiBuyRates(rs.getString("BUYRATE").split(","));
        chargeInfo.setMultiSellRates(rs.getString("SELLRATE").split(","));
        
        int marginLen	=	rs.getString("MARGINVALUE").split(",").length;
        boolean[]  freightBrekMarginTest = new boolean[marginLen];
        String[] 	calSellRate		= new String[marginLen];
        String[]	marginValues	= rs.getString("MARGINVALUE").split(",");
        String[]	buyRateValues	= rs.getString("BUYRATE").split(",");
        String[]	sellRateValues	= rs.getString("SELLRATE").split(",");
        String      temp_margin_type = null;
        String      temp_discount_type = null;
        for(int m=0;m<marginLen ;m++)
        {
        	freightBrekMarginTest[m] = false;
        	if(!("Y".equalsIgnoreCase(chargeInfo.getSelectedFlag())&& "Y".equalsIgnoreCase(chargeInfo.getChecked_Flag().split(",")[m]))) {//Modified by Anil.k on 18Jan2011
        		// Added By Kishore Podili for Govind
                marginValues[m] =  "-".equals(marginValues[m])?"0.00":(Double.parseDouble(marginValues[m])<0?"0.00":marginValues[m]);
        	}
        	
       }
        
        chargeInfo.setFreightBrekMarginTest(freightBrekMarginTest);
        chargeInfo.setMultiMargins(marginValues);
        
        
       
        chargeInfo.setRateIndicator(rs.getString("RATE_INDICATOR"));
        chargeInfo.setMultiRateDescriptions(rs.getString("RATE_DESCRIPTION").split(","));
        temp_margin_type = rs.getString("MARGIN_TYPE"); 
       if("BR".equalsIgnoreCase(flag)||"SBR".equalsIgnoreCase(flag)||"M".equalsIgnoreCase(chargeInfo.getMarginDiscountFlag()))
        {  
        
        	
          chargeInfo.setMultiMarginDiscount(marginValues);
          chargeInfo.setMultiTieMarginDiscountValue(marginValues);

        if(!"Y".equals(chargeInfo.getSelectedFlag()))
        {        	
        margin_type = temp_margin_type.contains("A")?temp_margin_type.replaceAll("A",mType):temp_margin_type;
        margin_type = temp_margin_type.contains("P")?temp_margin_type.replaceAll("P",mType):temp_margin_type;
        chargeInfo.setMarginType(mType);
        chargeInfo.setMultiMarginTypes(margin_type.split(","));
        }else{
        chargeInfo.setMarginType(rs.getString("MARGIN_TYPE"));
        chargeInfo.setMultiMarginTypes(temp_margin_type.split(","));
        }
         //ends 170758
   
        //for calaculated sell rate      
        	for (int j=0;j<marginLen; j++)
        	{
        		if("-".equals(marginValues[j]))
        			calSellRate[j]	=	marginValues[j];
        		else
        		{
        			if(Double.parseDouble(marginValues[j])>0 || "Y".equalsIgnoreCase(rs.getString("SELECTED_FLAG")))
        			{
        				if("A".equalsIgnoreCase(chargeInfo.getMultiMarginTypes()[j]))
        					calSellRate[j]	= new Double(Double.parseDouble(buyRateValues[j])+Double.parseDouble(marginValues[j])).toString();
        					
        				 else if("P".equalsIgnoreCase(chargeInfo.getMultiMarginTypes()[j])){
        					 double sellrate = Double.parseDouble(buyRateValues[j]) +( ( Double.parseDouble(buyRateValues[j]) * Double.parseDouble(marginValues[j]))/100);
        					 calSellRate[j]	= new Double( sellrate ).toString();
        							        					 
        				 }
        			}
        			else 
        				calSellRate[j]	=	rs.getString("BUYRATE").split(",")[j];
        		}
        		
        	}
       
        }
        else  if("RSR".equalsIgnoreCase(flag)||"CSR".equalsIgnoreCase(flag))
        {
        	temp_discount_type = rs.getString("MARGIN_TYPE");
        	chargeInfo.setMultiDiscountMargin(marginValues);// this Property (MultiDiscountMargin) for RSR
          chargeInfo.setMultiTieMarginDiscountValue(marginValues);
        //  chargeInfo.setMultiDiscountTypes(rs.getString("MARGIN_TYPE").split(","));
         if(!"Y".equals(chargeInfo.getSelectedFlag()))
         {
        	 discount_type = temp_discount_type.contains("A")?temp_discount_type.replaceAll("A",dType):temp_discount_type;
        	 discount_type = temp_discount_type.contains("P")?temp_discount_type.replaceAll("P",dType):temp_discount_type;
        chargeInfo.setDiscountType(dType);
        chargeInfo.setMultiDiscountTypes(discount_type.split(","));
         }else{
          chargeInfo.setDiscountType(rs.getString("MARGIN_TYPE"));
          chargeInfo.setMultiDiscountTypes(temp_discount_type.split(","));
         }
          //ends 170758
             
          	for (int j=0;j<marginLen; j++)
        	{
        		if("-".equals(marginValues[j]))
        			calSellRate[j]	=	marginValues[j];
        		else
        		{
        				if("A".equalsIgnoreCase(chargeInfo.getMultiDiscountTypes()[j]))
        					calSellRate[j]	= new Double(Double.parseDouble(sellRateValues[j])-Double.parseDouble(marginValues[j])).toString();
        					
        				 else if("P".equalsIgnoreCase(chargeInfo.getMultiDiscountTypes()[j])){
        					 double sellrate = Double.parseDouble(sellRateValues[j]) -( ( Double.parseDouble(sellRateValues[j]) * Double.parseDouble(marginValues[j]))/100);
        					 calSellRate[j]	= new Double( sellrate ).toString();
        							        					 
        				 }
        				 else
        					 calSellRate[j]	=	sellRateValues[j];
        		}
        		
        	}
         }
        
        chargeInfo.setSellRate(sellRate);
        chargeInfo.setMultiCalSellRates(calSellRate);
        chargeInfo.setMultiTieCalSellRateValue(calSellRate);
        chargeInfo.setTieSellRateValue(sellRate);
        chargeInfo.setSelectedFlag(rs.getString("SELECTED_FLAG"));
        weightBreak = rs.getString("WEIGHT_BREAK");
        rateType    = rs.getString("RATE_TYPE");
        chargeInfo.setRatio(rs.getString("DENSITY_RATIO"));
        chargeInfo.setMultiLineNo(rs.getString("LINE_NO").split(","));
        chargeInfo.setOriginPort(rs.getString("ORIGIN_PORT"));
        chargeInfo.setDestPort(rs.getString("DEST_PORT"));
        chargeInfo.setIncoTerms(rs.getString("INCO_TERMS"));
        chargeInfo.setFreightRateValidity(rs.getTimestamp("FREIGHT_RATE_VALIDITY"));
        chargeInfo.setCarrier(rs.getString("CARRIER_ID"));
        chargeInfo.setServiceLevel(rs.getString("SERVICE_LEVEL_ID"));
        chargeInfo.setMultiQuote_SerialNo(rs.getInt("MULTIQUOTE_SERIALNO"));
        
       

  
      
      //}
    }
           
    if(chargeInfoList!=null)
    {
      //legChargesDetails = (MultiQuoteFreightLegSellRates)
       list44 = (ArrayList)finalDOB.getLegDetails(); 
       legDetails = new ArrayList();
       list44Size  = list44.size();
      for(int j=0;j< list44Size;j++)
      {
    	  
    	  legChargesDetails = (MultiQuoteFreightLegSellRates)list44.get(j);
    	  if(j==0)
    		  legChargesDetails.setFreightChargesList(chargeInfoList);
    	  legDetails.add(legChargesDetails);
    	  
      }
        finalDOB.setLegDetails(legDetails);
        
    }
    
  }
  catch(SQLException sqEx)
		{
    sqEx.printStackTrace();
			//Logger.error(FILE_NAME,"QMSQuoteDAO[getLegCharges(masterDOB,sellBuyRate,sellBuyFlag,laneNo,origin,dest)] -> "+sqEx.toString());
    logger.error(FILE_NAME+"QMSMultiQuoteDAO[getLegRates(masterDOB,sellBuyRate,sellBuyFlag,laneNo,origin,dest)] -> "+sqEx.toString());
    throw new SQLException(sqEx.toString());
		}
  catch(Exception e)
		{
    e.printStackTrace();
			//Logger.error(FILE_NAME,"QMSQuoteDAO[getLegCharges(masterDOB,sellBuyRate,sellBuyFlag,laneNo,origin,dest)] -> "+e.toString());
    logger.error(FILE_NAME+"QMSMultiQuoteDAO[getLegRates(masterDOB,sellBuyRate,sellBuyFlag,laneNo,origin,dest)] -> "+e.toString());
    throw new SQLException(e.toString());
		}
		finally
		{
			try
			{   
				ConnectionUtil.closePreparedStatement( pstmt);//@@ Added by govind on 15-02-2010 for connection leakage
				ConnectionUtil.closePreparedStatement( pstmt1);//@@ Added by govind on 15-02-2010 for connection leakages
				ConnectionUtil.closeConnection(connection,csmt,rs);
			}
			catch(Exception ex)
			{
				//Logger.error(FILE_NAME,"Finally : QMSQuoteDAO[getLegCharges(masterDOB,sellBuyRate,sellBuyFlag,laneNo,origin,dest)]-> "+ex.toString());
      logger.error(FILE_NAME+"Finally : QMSQuoteDAO[getLegRates(masterDOB,sellBuyRate,sellBuyFlag,laneNo,origin,dest)]-> "+ex.toString());
      throw new SQLException(ex.toString());
			}
		}
  return finalDOB;
}

//getLegRates
private boolean isNumber(String s) {
	// TODO Auto-generated method stub
	try{
		Double d = Double.parseDouble(s);
		return true;
	}catch(Exception e){
		return false;
	}
	
}
public void create(MultiQuoteFinalDOB	finalDOB)throws  SQLException
{
	 
									 
		Connection connection		=	null;
		try
		{ 
			connection = this.getConnection(); 
     this.insertQuoteMasterDetails(finalDOB, connection);		
     
		}
		catch(SQLException sqEx)
		{
     sqEx.printStackTrace();
			//Logger.error(FILE_NAME,"QMSQuoteDAO[create(masterDOB)] -> "+sqEx.toString());
     logger.error(FILE_NAME+"QMSMultiQuoteDAO[create(masterDOB)] -> "+sqEx.toString());
     throw new SQLException(sqEx.toString());
		}
   catch(Exception e)
		{
     e.printStackTrace();
			//Logger.error(FILE_NAME,"QMSQuoteDAO[create(masterDOB)] -> "+e.toString());
     logger.error(FILE_NAME+"QMSMultiQuoteDAO[create(masterDOB)] -> "+e.toString());
     throw new SQLException(e.toString());
		}
		finally
		{
			try
			{
				ConnectionUtil.closeConnection(connection);
			}
			catch(Exception ex)
			{
				//Logger.error(FILE_NAME,"Finally : QMSQuoteDAO[create(masterDOB)]-> "+ex.toString());
       logger.error(FILE_NAME+"Finally : QMSMultiQuoteDAO[create(masterDOB)]-> "+ex.toString());
       throw new SQLException(ex.toString());
			}
		}
	
}

/**
*	This implements for the findByPrimaryKey()
*	of the Quote
*/
public long findByPrimaryKey(long	quoteId)throws  SQLException, ObjectNotFoundException
{
								 
	PreparedStatement pStmtFindPK 	= null;
	boolean	hasRows 				= false;
	Connection connection		=	null;
	ResultSet rs				=	null;
	try
	{
		
	}
	catch(Exception e)
	{
  e.printStackTrace();
		//Logger.error(FILE_NAME,"QMSQuoteDAO[findByPrimaryKey(quoteId)] -> "+e.toString());
  logger.error(FILE_NAME+"QMSQuoteDAO[findByPrimaryKey(quoteId)] -> "+e.toString());
	}
	finally
	{
		try
		{
			ConnectionUtil.closeConnection(connection, pStmtFindPK,rs);
		}
		catch(Exception ex)
		{
			//Logger.error(FILE_NAME,"Finally : QMSQuoteDAO[findByPrimaryKey(quoteId)]-> "+ex.toString());
    logger.error(FILE_NAME+"Finally : QMSQuoteDAO[findByPrimaryKey(quoteId)]-> "+ex.toString());
			//throw new Exception(ex.toString());
		}
	}
	if (hasRows)
		return quoteId;
	else
		throw new ObjectNotFoundException("Could not find bean	with Id "+quoteId);
}
/**
 * This method is used to insert the master info from MultiQuoteMasterDOB to QuoteMaster table
* 
 * @param masterDOB 	an MultiQuoteMasterDOB object that contains the master information of the quote
* 
 * @exception SQLException 
 */
private void insertQuoteMasterDetails(MultiQuoteFinalDOB	finalDOB, Connection connection) throws  SQLException
{
  
PreparedStatement pStmt	           = null;
PreparedStatement psmt             = null;
ResultSet         rs               = null;
MultiQuoteMasterDOB    masterDOB   = null;
MultiQuoteFlagsDOB     flagsDOB    = null;
MultiQuoteChargeInfo   freightRates = null;
ArrayList         attachmentIdList = null;
ArrayList		 chargeInfoList	   = null;
String location					   = null;
String quoteId					   = null;
int i1=0;
long maxvalue = 0;
String[]          quoteIds          = null;
String[]          quotes            = null;
int               quoteLen          = 0;
int               lanesize			= 0;
int 			  laneno			= 0;
long 			  quoteUniqueId		= 0;
//boolean			  spotRateFlag		= false;//Added by Anil.k for Spot Rates
//String			  spotRateFlagV		= "";//Added by Anil.k for Spot Rates
try
{

  masterDOB = (MultiQuoteMasterDOB)finalDOB.getMasterDOB();
  pStmt = connection.prepareStatement(masterInsQuery);
    if(finalDOB!=null&& finalDOB.getUpdatedReportDOB()!=null)
  {
   quoteId = finalDOB.getUpdatedReportDOB().getQuoteId();
  }

  //Added by Anil.k
  if(!"Charges".equalsIgnoreCase(masterDOB.getQuoteWith()))
  {
  chargeInfoList = ((MultiQuoteFreightLegSellRates)finalDOB.getLegDetails().get(0)).getFreightChargesList();
  lanesize = chargeInfoList!=null?chargeInfoList.size():finalDOB.getLegDetails().size();//Added by Anil.k for Save and Exit in 1st Screen
  }
  else
	  lanesize = 1;
  
  
    if("update".equalsIgnoreCase(finalDOB.getUpdate())&&quoteId!=null&&quoteId.trim().length()>0)
  {
  		quotes= quoteId.split("_");
         if(quotes.length==2)
         {
              quoteId=quoteId+ "_001" ;
         }
         else
         {
           quoteLen = Integer.parseInt(quotes[2]);//2nd part of the quoteId
							quoteLen++;

							if(quoteLen<10)
							{
								 quoteId=quotes[0]+"_"+quotes[1]+"_00"+new Integer(quoteLen).toString();
								
							}
							else if(quoteLen>=10&&quoteLen<100)
							{
				 				quoteId=quotes[0]+"_"+quotes[1]+"_0"+new Integer(quoteLen).toString();
									
							}
							else
							{
								quoteId=quotes[0]+"_"+quotes[1]+"_"+new Integer(quoteLen).toString();
								
							}

          if(quoteLen<10)
          {
          quoteId=quotes[0]+"_"+quotes[1]+"_00"+new Integer(quoteLen).toString();
          
          }
          else if(quoteLen>=10&&quoteLen<100)
          {
          quoteId=quotes[0]+"_"+quotes[1]+"_0"+new Integer(quoteLen).toString();
          
          }
          else
          {
          quoteId=quotes[0]+"_"+quotes[1]+"_"+new Integer(quoteLen).toString();
          
          }
      }
     masterDOB.setQuoteId(quoteId);
     for(int lane =0;lane< lanesize;lane++)
     {
    	 quoteUniqueId = getQuoteId(connection);
    	 masterDOB.setUniqueId(quoteUniqueId);
     finalDOB.setMasterDOB(masterDOB);
	 masterDOB = finalDOB.getMasterDOB();
	 
	 if(!"Charges".equalsIgnoreCase(masterDOB.getQuoteWith()))
	    {//END
		freightRates	=  (MultiQuoteChargeInfo)chargeInfoList.get(lane)  ;
		laneno	= freightRates.getSelectedLaneNum();	
	    }//Added by Anil.k
	    else
	    	laneno = 0;
   
	pStmt.setString(1,masterDOB.getQuoteId()); //@@ Added by subrahmanyam for the enhancement  146971 on 1/12/08
   pStmt.setInt(2,masterDOB.getShipmentMode());
  //pStmt.setLong(3,masterDOB.getPreQuoteId());  //@@ Commented by subrahmanyam  for the enhancement 146971 on 1/12/08
   pStmt.setString(3,masterDOB.getPreQuoteId());  //@@ Commented by subrahmanyam  for the enhancement 146971 on 1/12/08
 
  
  if(masterDOB.isImpFlag())
    pStmt.setString(4,"I");                             
  else
    pStmt.setString(4,"U");
 
  pStmt.setTimestamp(5,masterDOB.getEffDate());
  pStmt.setTimestamp(6,masterDOB.getValidTo());
  pStmt.setInt(7,masterDOB.getAccValidityPeriod());
  pStmt.setString(8,masterDOB.getCustomerId());
  pStmt.setInt(9,masterDOB.getCustomerAddressId());
  pStmt.setTimestamp(10,masterDOB.getCreatedDate());
  pStmt.setString(11,masterDOB.getCreatedBy());
  pStmt.setString(12,masterDOB.getSalesPersonCode());
  pStmt.setString(13,masterDOB.getIndustryId());
  pStmt.setString(14,masterDOB.getCommodityId());
  
  if(masterDOB.isHazardousInd())
    pStmt.setString(15,"Y");
  else
    pStmt.setString(15,"N");
    
  pStmt.setString(16,masterDOB.getUnNumber());
  pStmt.setString(17,masterDOB.getCommodityClass());
  pStmt.setString(18,masterDOB.getServiceLevelId());
  pStmt.setString(19,freightRates!=null?freightRates.getIncoTerms():masterDOB.getIncoTermsId()[0]);
  pStmt.setString(20,masterDOB.getQuotingStation());
  pStmt.setString(21,freightRates!=null?freightRates.getOrginLoc():masterDOB.getOriginLocation()[0]);
  pStmt.setString(22,freightRates!=null?masterDOB.getShipperZipCode()[laneno]:"");
  pStmt.setString(23,freightRates!=null?freightRates.getOriginPort():masterDOB.getOriginPort()[0]);
  pStmt.setString(24,masterDOB.getOverLengthCargoNotes());
  
  if(masterDOB.getRouteId()!=null)
    pStmt.setString(25,masterDOB.getRouteId());
  else
    pStmt.setNull(25,Types.VARCHAR);
    
  pStmt.setString(26,freightRates!=null?freightRates.getDestLoc():masterDOB.getDestLocation()[0]);
  pStmt.setString(27,freightRates!=null?masterDOB.getConsigneeZipCode()[laneno]:masterDOB.getConsigneeZipCode()[0]);
  pStmt.setString(28,freightRates!=null?freightRates.getDestPort():masterDOB.getDestPort()[0]);
  pStmt.setString(29,finalDOB.getEscalatedTo());
  pStmt.setTimestamp(30,masterDOB.getModifiedDate()!=null?masterDOB.getModifiedDate():(new java.sql.Timestamp((new java.util.Date()).getTime())));
  pStmt.setString(31,masterDOB.getModifiedBy());
  pStmt.setString(32,masterDOB.getTerminalId());
  pStmt.setLong(33,masterDOB.getVersionNo());
  pStmt.setString(34,masterDOB.getBuyRatesPermission());
  pStmt.setString(35,freightRates!=null?masterDOB.getShipperZones()[laneno]:masterDOB.getShipperZones()[0]);
  pStmt.setString(36,freightRates!=null?masterDOB.getConsigneeZones()[laneno]:masterDOB.getConsigneeZones()[0]);
  pStmt.setLong(37,masterDOB.getUniqueId());
  
  if(finalDOB.getFlagsDOB()!=null)
  {
    flagsDOB = finalDOB.getFlagsDOB();
    flagsDOB.setQuoteId(masterDOB.getUniqueId());
    
    if(flagsDOB.getPNFlag()!=null)
      pStmt.setString(38,flagsDOB.getPNFlag());
    else
      pStmt.setNull(38,Types.VARCHAR);
    
    if(flagsDOB.getUpdateFlag()!=null)
      pStmt.setString(39,flagsDOB.getUpdateFlag());
    else
      pStmt.setNull(39,Types.VARCHAR);
      
    if(flagsDOB.getActiveFlag()!=null)
      pStmt.setString(40,flagsDOB.getActiveFlag());
    else
      pStmt.setNull(40,Types.VARCHAR);
    
    if(flagsDOB.getSentFlag()!=null)
      pStmt.setString(41,flagsDOB.getSentFlag());
    else
      pStmt.setNull(41,Types.VARCHAR);
    
    if(flagsDOB.getCompleteFlag()!=null)
      pStmt.setString(42,flagsDOB.getCompleteFlag());
    else
      pStmt.setNull(42,Types.VARCHAR);
    
    if(flagsDOB.getQuoteStatusFlag()!=null)
      pStmt.setString(43,flagsDOB.getQuoteStatusFlag());
    else
      pStmt.setNull(43,Types.VARCHAR);
      
    pStmt.setString(44,flagsDOB.getEscalationFlag());
    pStmt.setString(45,flagsDOB.getInternalExternalFlag());
    pStmt.setString(46,flagsDOB.getEmailFlag());
    pStmt.setString(47,flagsDOB.getFaxFlag());
    pStmt.setString(48,flagsDOB.getPrintFlag());
    pStmt.setTimestamp(49,new java.sql.Timestamp((new java.util.Date()).getTime()));
      
  }
  pStmt.setString(50,finalDOB.getSpotRatesFlag());
  pStmt.setString(51,freightRates!=null?masterDOB.getCargoAcceptance()[laneno]:masterDOB.getCargoAcceptance()[0]);
  pStmt.setString(52,freightRates!=null?masterDOB.getCargoAccPlace()[laneno]:masterDOB.getCargoAccPlace()[0]);
  pStmt.setString(53,masterDOB.getShipperMode());
  pStmt.setString(54,masterDOB.getConsigneeMode());
  if("2".equalsIgnoreCase(masterDOB.getShipperMode()))
      pStmt.setString(55,masterDOB.getShipperConsoleType());
  else
      pStmt.setNull(55,Types.VARCHAR);
  
  if("2".equalsIgnoreCase(masterDOB.getConsigneeMode()))
      pStmt.setString(56,masterDOB.getConsigneeConsoleType());
  else
      pStmt.setNull(56,Types.VARCHAR);
      pStmt.setString(57,masterDOB.getSalesPersonFlag());
  if("APP".equalsIgnoreCase(flagsDOB.getQuoteStatusFlag()))  
  {
        pStmt.setTimestamp(58,new java.sql.Timestamp((new java.util.Date()).getTime()));
  }
  else
  {
        pStmt.setNull(58,Types.DATE);
  }
  pStmt.setString(59,"Y" );
  pStmt.setString(60,freightRates!=null?freightRates.getCarrier():"");//Modified by Anil.k
  pStmt.setInt(61,lane );
  pStmt.setString(62,freightRates!=null?freightRates.getServiceLevel():"");//Modified by Anil.k
  pStmt.setString(63,masterDOB.getMultiquoteweightBrake()!= null?masterDOB.getMultiquoteweightBrake():"");
  pStmt.setString(64, masterDOB.getQuoteWith());//Added by Anil.k
  pStmt.setTimestamp(65,masterDOB.getCustDate());//Added by Rakesh for Issue:236359
  pStmt.setString(66,masterDOB.getCustTime());//Added by Rakesh for Issue:
  pStmt.executeUpdate();
  
  if(pStmt!=null)
      pStmt.clearParameters();
  
  if(masterDOB.getCustomerContacts()!=null)
    insertQuoteContactPersons(masterDOB,connection);
    
   // insertQuoteSpotRates(finalDOB,connection);
     
  insertQuoteChargeGroups(finalDOB,connection,laneno); 
  
  if(masterDOB.getContentOnQuote()!=null)
    insertQuoteHeaderFooter(masterDOB,connection);
    
  if(!"Modify".equalsIgnoreCase(masterDOB.getOperation()))
     insertRoutePlanDetails(connection,finalDOB,freightRates!=null?freightRates.getOrginLoc():masterDOB.getOriginLocation()[0],freightRates!=null?freightRates.getDestLoc():masterDOB.getDestLocation()[0],freightRates!=null?freightRates.getOriginPort():masterDOB.getOriginPort()[0],freightRates!=null?freightRates.getDestPort():masterDOB.getDestPort()[0],lane);//Modified by Anil.k
  
 insertSelectedRates(finalDOB,connection,laneno,lane);
 
 if(finalDOB.getExternalNotes()!=null)     
    insertNotes(connection,finalDOB);
 
   if(finalDOB.getUpdatedReportDOB()!=null)
 {
    UpdatedQuotesReportDOB  reportDOB = finalDOB.getUpdatedReportDOB();
    reportDOB.setNewQuoteId(finalDOB.getMasterDOB().getUniqueId());
    setConfirmFlag(reportDOB); 
 }
 //@@Added by kameswari for the WPBN issue-    61289
if("Add".equalsIgnoreCase(masterDOB.getOperation()))
{
 if(finalDOB.getAttachmentDOBList()!=null)
 {
    insertAttachmentIdList(finalDOB);
 
 }
}
else if("Modify".equalsIgnoreCase(masterDOB.getOperation())) 
{
   if(finalDOB.getAttachmentDOBList()!=null)
 {
    updateAttachmentIdList(finalDOB);
 
 } 
}
  }
  }
else
  {
  	
    if(!("Modify".equalsIgnoreCase(masterDOB.getOperation())))
    {
     psmt  = connection.prepareStatement("SELECT "+masterDOB.getTerminalId()+"_SEQ.NEXTVAL FROM DUAL");
     rs  = psmt.executeQuery();
     if(rs.next())
     {
        maxvalue   = rs.getLong(1);
     } 
       location = masterDOB.getTerminalId().substring(3);
     
     quoteId  = location+"_"+new Long(maxvalue).toString();
      masterDOB.setQuoteId(quoteId);
    }
     for(int lane =0;lane< lanesize;lane++)
     {
    	 quoteUniqueId = getQuoteId(connection);
    	 masterDOB.setUniqueId(quoteUniqueId);
    	 finalDOB.setMasterDOB(masterDOB);
    	 masterDOB = finalDOB.getMasterDOB();
    	 //Added by Anil.k
    if(!"Charges".equalsIgnoreCase(masterDOB.getQuoteWith()))
    {//END
    	if(chargeInfoList!=null){
	freightRates	=  chargeInfoList!=null?((MultiQuoteChargeInfo)chargeInfoList.get(lane)) :null ;
	laneno	= freightRates!=null?freightRates.getSelectedLaneNum():0;//Added by Anil.k for Save and Exit in 1st Screen	
    	}
    	else
    		laneno=lane;
    }//Added by Anil.k
    else
    	laneno = 0;
    //spotRateFlagV = masterDOB.getSpotRatesFlag()[lane];
    //spotRateFlag = ((MultiQuoteFreightLegSellRates)finalDOB.getLegDetails().get(lane)).isSpotRatesFlag();//Added by Anil.k for Spot Rates
	pStmt.setString(1,masterDOB.getQuoteId());
    pStmt.setInt(2,masterDOB.getShipmentMode());
    pStmt.setString(3,masterDOB.getPreQuoteId());
    if(masterDOB.isImpFlag())
    pStmt.setString(4,"I");                             
  else
    pStmt.setString(4,"U");
 
  pStmt.setTimestamp(5,masterDOB.getEffDate());
  pStmt.setTimestamp(6,masterDOB.getValidTo());
  pStmt.setInt(7,masterDOB.getAccValidityPeriod());
  pStmt.setString(8,masterDOB.getCustomerId());
  pStmt.setInt(9,masterDOB.getCustomerAddressId());
  pStmt.setTimestamp(10,masterDOB.getCreatedDate());
  pStmt.setString(11,masterDOB.getCreatedBy());
  pStmt.setString(12,masterDOB.getSalesPersonCode());
  pStmt.setString(13,masterDOB.getIndustryId());
  pStmt.setString(14,masterDOB.getCommodityId());
  
  if(masterDOB.isHazardousInd())
    pStmt.setString(15,"Y");
  else
    pStmt.setString(15,"N");
    
  pStmt.setString(16,masterDOB.getUnNumber());
  pStmt.setString(17,masterDOB.getCommodityClass());
  pStmt.setString(18,masterDOB.getServiceLevelId());
  pStmt.setString(19,freightRates!=null?freightRates.getIncoTerms():masterDOB.getIncoTermsId()[laneno]);//Modified by Anil.k
  pStmt.setString(20,masterDOB.getQuotingStation());
  pStmt.setString(21,freightRates!=null?freightRates.getOrginLoc():masterDOB.getOriginLocation()[laneno]);//Modified by Anil.k
  pStmt.setString(22,freightRates!=null?masterDOB.getShipperZipCode()[laneno]:masterDOB.getShipperZipCode()[laneno]);//Modified by Anil.k
  pStmt.setString(23,freightRates!=null?freightRates.getOriginPort():masterDOB.getOriginPort()[laneno]);//Modified by Anil.k
  pStmt.setString(24,masterDOB.getOverLengthCargoNotes());
  pStmt.setNull(25,Types.VARCHAR);
/*   if(masterDOB.getRouteId()!=null)
    pStmt.setString(25,masterDOB.getRouteId());
  else
    pStmt.setNull(25,Types.VARCHAR);
 */   
  pStmt.setString(26,freightRates!=null?freightRates.getDestLoc():masterDOB.getDestLocation()[laneno]);//Modified by Anil.k
  pStmt.setString(27,freightRates!=null?masterDOB.getConsigneeZipCode()[laneno]:masterDOB.getConsigneeZipCode()[laneno]);//Modified by Anil.k
  pStmt.setString(28,freightRates!=null?freightRates.getDestPort():masterDOB.getDestPort()[laneno]);//Modified by Anil.k
  pStmt.setString(29,finalDOB.getEscalatedTo());
  pStmt.setTimestamp(30,masterDOB.getModifiedDate()!=null?masterDOB.getModifiedDate():(new java.sql.Timestamp((new java.util.Date()).getTime())));
  pStmt.setString(31,masterDOB.getModifiedBy());
  pStmt.setString(32,masterDOB.getTerminalId());
  pStmt.setLong(33,masterDOB.getVersionNo());
  pStmt.setString(34,masterDOB.getBuyRatesPermission());
  pStmt.setString(35,freightRates!=null?masterDOB.getShipperZones()[laneno]:masterDOB.getShipperZones()[laneno]);//Modified by Anil.k
  pStmt.setString(36,freightRates!=null?masterDOB.getConsigneeZones()[laneno]:masterDOB.getConsigneeZones()[laneno]);//Modified by Anil.k
  pStmt.setLong(37,masterDOB.getUniqueId());
  
  if(finalDOB.getFlagsDOB()!=null)
  {
    flagsDOB = finalDOB.getFlagsDOB();
    flagsDOB.setQuoteId(masterDOB.getUniqueId());
    
    if(flagsDOB.getPNFlag()!=null)
      pStmt.setString(38,flagsDOB.getPNFlag());
    else
      pStmt.setNull(38,Types.VARCHAR);
    
    if(flagsDOB.getUpdateFlag()!=null)
      pStmt.setString(39,flagsDOB.getUpdateFlag());
    else
      pStmt.setNull(39,Types.VARCHAR);
      
    if(flagsDOB.getActiveFlag()!=null)
      pStmt.setString(40,flagsDOB.getActiveFlag());
    else
      pStmt.setNull(40,Types.VARCHAR);
    
    if(flagsDOB.getSentFlag()!=null)
      pStmt.setString(41,flagsDOB.getSentFlag());
    else
      pStmt.setNull(41,Types.VARCHAR);
    
    if(flagsDOB.getCompleteFlag()!=null)
      pStmt.setString(42,flagsDOB.getCompleteFlag());
    else
      pStmt.setNull(42,Types.VARCHAR);
    
    if(flagsDOB.getQuoteStatusFlag()!=null)
      pStmt.setString(43,flagsDOB.getQuoteStatusFlag());
    else
      pStmt.setNull(43,Types.VARCHAR);
      
    pStmt.setString(44,flagsDOB.getEscalationFlag());
    pStmt.setString(45,flagsDOB.getInternalExternalFlag());
    pStmt.setString(46,flagsDOB.getEmailFlag());
    pStmt.setString(47,flagsDOB.getFaxFlag());
    pStmt.setString(48,flagsDOB.getPrintFlag());
    pStmt.setTimestamp(49,new java.sql.Timestamp((new java.util.Date()).getTime()));
      
  }
  //pStmt.setString(50,finalDOB.getSpotRatesFlag());
  pStmt.setString(50,freightRates!=null?freightRates.getSpotRatesFlag():(masterDOB.getSpotRatesFlag() != null && masterDOB.getSpotRatesFlag()[laneno]!= null)?masterDOB.getSpotRatesFlag()[laneno]:"N");//Added by Anil.k for Spot Rates//modified by govind on 25-05-11
  pStmt.setString(51,masterDOB.getCargoAcceptance()[laneno]);
  pStmt.setString(52,masterDOB.getCargoAccPlace()[laneno]);
  pStmt.setString(53,masterDOB.getShipmentMode()+"");
  pStmt.setString(54,masterDOB.getShipmentMode()+"");
  if("2".equalsIgnoreCase(masterDOB.getShipperMode()) || masterDOB.getShipmentMode()==2)
      //pStmt.setString(55,"List".equalsIgnoreCase(masterDOB.getQuoteWith())?"FCL":"LCL");
  pStmt.setString(55,"List".equalsIgnoreCase(masterDOB.getMultiquoteweightBrake())?"FCL":"LCL"); //@@Commented and Modified by Kameswari for the WPBN issue - 267535 on 25/08/2011
  else
      pStmt.setNull(55,Types.VARCHAR);
  
  if("2".equalsIgnoreCase(masterDOB.getConsigneeMode()) || masterDOB.getShipmentMode()==2)
    //  pStmt.setString(56,masterDOB.getConsigneeConsoleType());
	//  pStmt.setString(56,"List".equalsIgnoreCase(masterDOB.getQuoteWith())?"FCL":"LCL");
	  pStmt.setString(56,"List".equalsIgnoreCase(masterDOB.getMultiquoteweightBrake())?"FCL":"LCL"); //@@Commented and Modified by Kameswari for the WPBN issue - 267535 on 25/08/2011
  else
      pStmt.setNull(56,Types.VARCHAR);
      pStmt.setString(57,masterDOB.getSalesPersonFlag());
   if("APP".equalsIgnoreCase(flagsDOB.getQuoteStatusFlag()))  
  {
        pStmt.setTimestamp(58,new java.sql.Timestamp((new java.util.Date()).getTime()));
  }
  else
  {
        pStmt.setNull(58,Types.DATE);
  }
        pStmt.setString(59,"Y" );
        pStmt.setString(60,freightRates!=null?freightRates.getCarrier():"");//Modified by Anil.k
        pStmt.setInt(61,lane );
        pStmt.setString(62,freightRates!=null?freightRates.getServiceLevel():"");//Modified by Anil.k
        pStmt.setString(63,masterDOB.getMultiquoteweightBrake()!= null?masterDOB.getMultiquoteweightBrake():"");
        pStmt.setString(64, masterDOB.getQuoteWith());//Added by Anil.k
        pStmt.setTimestamp(65,masterDOB.getCustDate());//Added by Rakesh for Issue:236359
        pStmt.setString(66,masterDOB.getCustTime());//Added by Rakesh for Issue:
        pStmt.setInt(67,freightRates!=null?freightRates.getMultiQuote_SerialNo():lane);
        pStmt.setInt(68,(masterDOB != null && masterDOB.getSpotRatesFlag()!= null && masterDOB.getSpotRatesFlag()[laneno]!= null && masterDOB.getSpotrateSurchargeCount()[laneno]!= null )?Integer.parseInt(masterDOB.getSpotrateSurchargeCount()[laneno]):0);//Added by Anil.k for Spot Rates
        pStmt.executeUpdate();
        
 
  if(pStmt!=null)
      pStmt.clearParameters();
  
  if(masterDOB.getCustomerContacts()!=null)
    insertQuoteContactPersons(masterDOB,connection);
  
  if(freightRates != null && freightRates.getSpotRatesFlag()!=null && "Y".equalsIgnoreCase(freightRates.getSpotRatesFlag()))
    insertQuoteSpotRates(finalDOB,connection,laneno,freightRates.getSelectedLaneNum(),freightRates.getSpotRatesFlag());//Modified by Anil.k for Spot Rates
     
  insertQuoteChargeGroups(finalDOB,connection,laneno); 
  
  if(masterDOB.getContentOnQuote()!=null)
    insertQuoteHeaderFooter(masterDOB,connection);
    
  if(!"Modify".equalsIgnoreCase(masterDOB.getOperation()))
     insertRoutePlanDetails(connection,finalDOB,freightRates!=null?freightRates.getOrginLoc():masterDOB.getOriginLocation()[0],freightRates!=null?freightRates.getDestLoc():masterDOB.getDestLocation()[0],freightRates!=null?freightRates.getOriginPort():masterDOB.getOriginPort()[0],freightRates!=null?freightRates.getDestPort():masterDOB.getDestPort()[0],lane);//Modified by Anil.k
  
 insertSelectedRates(finalDOB,connection,laneno,lane);
 
 if(finalDOB.getExternalNotes()!=null)     
    insertNotes(connection,finalDOB);
 
   if(finalDOB.getUpdatedReportDOB()!=null)
 {
    UpdatedQuotesReportDOB  reportDOB = finalDOB.getUpdatedReportDOB();
    reportDOB.setNewQuoteId(finalDOB.getMasterDOB().getUniqueId());
    setConfirmFlag(reportDOB); 
 }
 //@@Added by kameswari for the WPBN issue-    61289
if("Add".equalsIgnoreCase(masterDOB.getOperation()))
{
 if(finalDOB.getAttachmentDOBList()!=null)
 {
    insertAttachmentIdList(finalDOB);
 
 }
}
else if("Modify".equalsIgnoreCase(masterDOB.getOperation())) 
{
   if(finalDOB.getAttachmentDOBList()!=null)
 {
    updateAttachmentIdList(finalDOB);
 
 } 
}

	 }  //for end
     
     setTransactionDetails(masterDOB);
       
     
  }
}

catch(SQLException sqEx)
	{
  sqEx.printStackTrace();
	
  logger.error(FILE_NAME+"QMSQuoteDAO[insertQuoteMasterDetails(masterDOB,connection)] -> "+sqEx.toString());
  throw new SQLException(sqEx.toString());
	}
catch(Exception e)
	{
	e.printStackTrace();
  logger.error(FILE_NAME+"QMSQuoteDAO[insertQuoteMasterDetails(masterDOB,connection)] -> "+e.toString());
  throw new SQLException(e.toString());
	}
	finally
	{
		try
		{
			//ConnectionUtil.closeConnection(null,pStmt,null);//Modified By RajKumari on 24-10-2008 for Connection Leakages.
			ConnectionUtil.closeConnection(null,pStmt,rs);//Commented and added by GOving for the connection Leakages
			ConnectionUtil.closePreparedStatement(psmt);
		}     
		catch(Exception ex)
		{
			//Logger.error(FILE_NAME,"Finally : QMSQuoteDAO[insertQuoteMasterDetails(masterDOB,connection)]-> "+ex.toString());
    logger.error(FILE_NAME+"Finally : QMSQuoteDAO[insertQuoteMasterDetails(masterDOB,connection)]-> "+ex.toString());
    throw new SQLException(ex.toString());
		}
}

}

/**
 * This method is used to insert the master info from MultiQuoteMasterDOB to Quote Contact dtl table
* 
 * @param masterDOB 	an MultiQuoteMasterDOB object that contains the master information of the quote
* 
 * @exception SQLException 
 */
private void insertQuoteContactPersons(MultiQuoteMasterDOB masterDOB, Connection connection) throws SQLException
{
PreparedStatement pStmt	= null;
	//ResultSet rs				=	null;//Commented By RajKumari on 24-10-2008 for Connection Leakages.
try
{
  pStmt = connection.prepareStatement(masterContactPersonInsQuery);
  int len = masterDOB.getCustomerContacts().length;
  for(int i=0;i<len;i++)
  {
    if(masterDOB.getCustomerContacts()[i]!=null && masterDOB.getCustomerContacts()[i].trim().length()!=0)
    {
      pStmt.setLong(1,masterDOB.getUniqueId());
      pStmt.setString(2,masterDOB.getCustomerId());
      pStmt.setInt(3,Integer.parseInt((masterDOB.getCustomerContacts())[i]));
      pStmt.addBatch();
    }
  }
  pStmt.executeBatch();
}
catch(SQLException sqEx)
	{
  sqEx.printStackTrace();
		//Logger.error(FILE_NAME,"QMSQuoteDAO[insertQuoteContactPersons(masterDOB,connection)] -> "+sqEx.toString());
  logger.error(FILE_NAME+"QMSQuoteDAO[insertMultiQuoteContactPersons(masterDOB,connection)] -> "+sqEx.toString());
  throw new SQLException(sqEx.toString());
		//throw new Exception(sqEx.toString());
	}
catch(Exception e)
	{
		//Logger.error(FILE_NAME,"QMSQuoteDAO[insertQuoteContactPersons(masterDOB,connection)] -> "+e.toString());
  logger.error(FILE_NAME+"QMSQuoteDAO[insertMultiQuoteContactPersons(masterDOB,connection)] -> "+e.toString());
		throw new SQLException(e.toString());
  //throw new Exception(e.toString());
	}
	finally
	{
		try
		{
			ConnectionUtil.closeConnection(null,pStmt,null);//Modified By RajKumari on 24-10-2008 for Connection Leakages.
		}
		catch(Exception ex)
		{
			//Logger.error(FILE_NAME,"Finally : QMSQuoteDAO[insertQuoteContactPersons(masterDOB,connection)]-> "+ex.toString());
    logger.error(FILE_NAME+"Finally : QMSMultiQuoteDAO[insertQuoteContactPersons(masterDOB,connection)]-> "+ex.toString());
		}
}
}

/**
 * This method is used to insert the master info from QuoteMasterDOB to Quote Charge Group dtl table
* 
 * @param masterDOB 	an QuoteMasterDOB object that contains the master information of the quote
* 
 * @exception SQLException 
 */
private void insertQuoteChargeGroups(MultiQuoteFinalDOB finalDOB ,Connection connection,int laneno) throws SQLException 
{
PreparedStatement pStmt	= null;
MultiQuoteFreightLegSellRates	legRateDetails = null;
MultiQuoteMasterDOB    masterDOB =null;
	//ResultSet rs				=	null;//Commented By RajKumari on 24-10-2008 for Connection Leakages.
try
{
  pStmt = connection.prepareStatement(masterChargeGroupsInsQuery);
legRateDetails   =   (MultiQuoteFreightLegSellRates)finalDOB.getLegDetails().get(laneno);
 //int len  = legRateDetails.getChargeGroupIds().length;
 masterDOB = (MultiQuoteMasterDOB)finalDOB.getMasterDOB(); 
if(!"Freight".equalsIgnoreCase(masterDOB.getQuoteWith()) && legRateDetails.getChargeGroupIds()!= null){//Added by Anil.k
 int len  = legRateDetails.getChargeGroupIds()!=null?legRateDetails.getChargeGroupIds().length:0;
 //masterDOB = (MultiQuoteMasterDOB)finalDOB.getMasterDOB(); 
  for(int i=0;i<len;i++)
  {
    pStmt.setLong(1,masterDOB.getUniqueId());
    pStmt.setString(2,(legRateDetails.getChargeGroupIds())[i]);
    pStmt.addBatch();
  }
  pStmt.executeBatch();
}//Added by Anil.k
}
catch(SQLException sqEx)
	{
  sqEx.printStackTrace();
		//Logger.error(FILE_NAME,"QMSQuoteDAO[insertQuoteChargeGroups(masterDOB,connection)] -> "+sqEx.toString());
  logger.error(FILE_NAME+"QMSMultiQuoteDAO[insertQuoteChargeGroups(masterDOB,connection)] -> "+sqEx.toString());
  throw new SQLException(sqEx.toString());
	}
catch(Exception e)
	{
		//Logger.error(FILE_NAME,"QMSQuoteDAO[insertQuoteChargeGroups(masterDOB,connection)] -> "+e.toString());
  logger.error(FILE_NAME+"QMSMultiQuoteDAO[insertQuoteChargeGroups(masterDOB,connection)] -> "+e.toString());
  throw new SQLException(e.toString());
	}
	finally
	{
		try
		{
			ConnectionUtil.closeConnection(null,pStmt,null);//Modified By RajKumari on 24-10-2008 for Connection Leakages.
		}
		catch(Exception ex)
		{
			//Logger.error(FILE_NAME,"Finally : QMSQuoteDAO[insertQuoteChargeGroups(masterDOB,connection)]-> "+ex.toString());
    logger.error(FILE_NAME+"Finally : QMSQuoteDAO[insertQuoteChargeGroups(masterDOB,connection)]-> "+ex.toString());
    throw new SQLException(ex.toString());
		}
}
}


/**
 * This method is used to insert the master info from QuoteMasterDOB to Quote Header footer table
* 
 * @param masterDOB 	an QuoteMasterDOB object that contains the master information of the quote
* 
 * @exception SQLException 
 */
private void insertQuoteHeaderFooter(MultiQuoteMasterDOB masterDOB, Connection connection) throws SQLException 
{
PreparedStatement pStmt	= null;
	//ResultSet         rs	  =	null;//Commented By RajKumari on 24-10-2008 for Connection Leakages.
try
{
  pStmt = connection.prepareStatement(masterHeaderFooterInsQuery);
  int len = masterDOB.getHeaderFooter()!= null?masterDOB.getHeaderFooter().length:0;
  for(int i=0;i<len;i++)
  {
    if(masterDOB.getContentOnQuote()[i]!=null && masterDOB.getContentOnQuote()[i].trim().length()!=0)
    {
      pStmt.setLong(1,masterDOB.getUniqueId());
      pStmt.setString(2,(masterDOB.getHeaderFooter())[i]);
      pStmt.setString(3,(masterDOB.getContentOnQuote())[i]);
      pStmt.setString(4,(masterDOB.getLevels()!=null ? masterDOB.getLevels()[i]!=null ? masterDOB.getLevels()[i]:"":"")); // Added by Gowtham.
      pStmt.setString(5,(masterDOB.getAlign())[i]);
      pStmt.addBatch();
    }
  }
  pStmt.executeBatch();
}
catch(SQLException sqEx)
	{
  sqEx.printStackTrace();
		//Logger.error(FILE_NAME,"QMSQuoteDAO[insertQuoteHeaderFooter(masterDOB,connection)] -> "+sqEx.toString());
  logger.error(FILE_NAME+"QMSMultiQuoteDAO[insertQuoteHeaderFooter(masterDOB,connection)] -> "+sqEx.toString());
  throw new SQLException(sqEx.toString());
	}
catch(Exception e)
	{
		e.printStackTrace();
  //Logger.error(FILE_NAME,"QMSQuoteDAO[insertQuoteHeaderFooter(masterDOB,connection)] -> "+e.toString());
  logger.error(FILE_NAME+"QMSMultiQuoteDAO[insertQuoteHeaderFooter(masterDOB,connection)] -> "+e.toString());
  throw new SQLException(e.toString());
	}
	finally
	{
		try
		{
			ConnectionUtil.closeConnection(null,pStmt,null);//Modified By RajKumari on 24-10-2008 for Connection Leakages.
		}
		catch(Exception ex)
		{
			//Logger.error(FILE_NAME,"Finally : QMSQuoteDAO[insertQuoteHeaderFooter(masterDOB,connection)]-> "+ex.toString());
    logger.error(FILE_NAME+"Finally : QMSQuoteDAO[insertQuoteHeaderFooter(masterDOB,connection)]-> "+ex.toString());
    throw new SQLException(ex.toString());
		}
}
}

private void insertNotes(Connection connection, MultiQuoteFinalDOB finalDOB) throws SQLException
{
  PreparedStatement     pstmt       = null;
  MultiQuoteMasterDOB        masterDOB   = null;
  try
  {
    pstmt     =     connection.prepareStatement(notesInsertQuery);
    masterDOB =     finalDOB.getMasterDOB();
    
    for(int i=0;i<finalDOB.getExternalNotes().length;i++)
    {
      pstmt.clearParameters();
      pstmt.setLong(1,masterDOB.getUniqueId());
      
      if(finalDOB.getInternalNotes()[i]!= null && finalDOB.getInternalNotes()[i].trim().length()!=0)
        pstmt.setString(2,finalDOB.getInternalNotes()[i]);
      else
        pstmt.setNull(2,Types.VARCHAR);
      
      if(finalDOB.getExternalNotes()[i]!=null && finalDOB.getExternalNotes()[i].trim().length()!=0)
        pstmt.setString(3,finalDOB.getExternalNotes()[i]);
      else
        pstmt.setNull(3,Types.VARCHAR);
        
      pstmt.addBatch();
    }
    pstmt.executeBatch();
  }
  catch(SQLException sql)
  {
    //Logger.error(FILE_NAM E,"SQLException in insertNotes::"+sql);
    logger.error(FILE_NAME+"SQLException in insertNotes::"+sql);
    sql.printStackTrace();
    throw new SQLException(sql.toString(),sql.getSQLState(),sql.getErrorCode());
  }
  catch(Exception e)
  {
    //Logger.error(FILE_NAME,"Error in insertNotes::"+e);
    logger.error(FILE_NAME+"Error in insertNotes::"+e);
    e.printStackTrace();
    throw new SQLException(e.toString());
  }
  finally
  {
    ConnectionUtil.closeConnection(null,pstmt,null);
  }
}

public void setTransactionDetails(MultiQuoteMasterDOB masterDOB)
{
  //@@This method does not throw any exception. If any error occurs, system will ignore it.
  OperationsImpl     operationsImpl = null;
  try
  {
    operationsImpl =  new OperationsImpl();
    operationsImpl.setTransactionDetails(masterDOB.getTerminalId(),masterDOB.getUserId(),
                                          "Quote",""+masterDOB.getQuoteId()+", Version:"+masterDOB.getVersionNo(),
                                          new java.sql.Timestamp((new java.util.Date()).getTime()),masterDOB.getOperation());
  }
  catch(Exception e)
  {
    //Logger.error(FILE_NAME,"Error while setting Transaction Details");
    logger.error(FILE_NAME+"Error while setting Transaction Details");
    e.printStackTrace();
  }
}

//@@Added by Kameswari for the WPBN issue-61289
public void insertAttachmentIdList(MultiQuoteFinalDOB finalDOB)throws Exception
{

   Connection           connection         = null;
   PreparedStatement    pst                = null;
   MultiQuoteMasterDOB       masterDOB          = null;
   MultiQuoteAttachmentDOB   attachmentDOB      = null;
   ArrayList            attachmentIdList   = null;
   long                 quoteId;  
   int k = 0;
   String               insertIdQuery      = "INSERT INTO QMS_QUOTE_ATTACHMENTDTL(ID,QUOTE_ID,ATTACHMENT_ID) VALUES(SEQ_QUOTE_ATTACHDTL_ID .NEXTVAL,?,?)"; 
   try
   {
       attachmentIdList    = finalDOB.getAttachmentDOBList();
       masterDOB           = finalDOB.getMasterDOB();
       quoteId             = masterDOB.getUniqueId();
       connection          = getConnection();
       pst                 = connection.prepareStatement(insertIdQuery);
       int attachmntIdSize	=	attachmentIdList.size();
       for(int i=0;i<attachmntIdSize;i++)
       {
            attachmentDOB  =  (MultiQuoteAttachmentDOB)attachmentIdList.get(i); 
             pst.setLong(1,quoteId);
             pst.setString(2,attachmentDOB.getAttachmentId());
             k = pst.executeUpdate();
      }
    }
   catch(Exception e)
   {
      e.printStackTrace();
      throw new Exception();
   }
   finally
   {
      if(pst!=null)
        pst.close();
      if(connection!=null)
        connection.close(); 
   }
}
public void updateAttachmentIdList(MultiQuoteFinalDOB finalDOB)throws Exception
{
   Connection          connection         = null;
   PreparedStatement    pst                = null;
   MultiQuoteMasterDOB       masterDOB          = null;
   long                 quoteId;          
   String               deleteIdQuery      = "DELETE  FROM QMS_QUOTE_ATTACHMENTDTL WHERE QUOTE_ID=?"; 
   int i = 0;
   try
   {
      masterDOB           = finalDOB.getMasterDOB();
      quoteId             = masterDOB.getUniqueId();
      connection         =   getConnection();
      pst                 = connection.prepareStatement(deleteIdQuery);
      pst.setLong(1,quoteId);
      
      i                   = pst.executeUpdate();
        insertAttachmentIdList(finalDOB);
      
   }
   catch(Exception e)
   {
      e.printStackTrace();
      throw new Exception();
   }
   finally
   {
      if(pst!=null)
        pst.close();
      if(connection!=null)
        connection.close(); 
   }
}

private void insertSelectedRates(MultiQuoteFinalDOB finalDOB,Connection connection,int laneno,int lane) throws SQLException
{
  
  PreparedStatement pStmt           = null;
  PreparedStatement pStmt1           = null;
  ArrayList legDetails              = null;
  ArrayList originChargesList       = null;
  ArrayList destChargesList         = null;
  ArrayList frtChargesList          = null;
  String Margin_discount_flag       = null;
  MultiQuoteMasterDOB  masterDOB         = null;
  MultiQuoteFreightLegSellRates  legDOB  = null;
  MultiQuoteCharges    chargesDOB        = null;
	MultiQuoteFreightLegSellRates legRateDetails = null;
  ArrayList       chargesList       = null;
  MultiQuoteChargeInfo chargeInfoDOB     = null;
  int[] originIndices               = null;
  int[] destIndices                 = null;
  int[] freightIndices              = null;
  
  int   noOfLegs                    = 0;
  int   size                        = 0;
  int   chargeSize                  = 0;
   String chargeDescription = null;
  try
  {
    pStmt             =        connection.prepareStatement(selectedRatesInsertQuery);
    
    masterDOB             =        finalDOB.getMasterDOB();
    legRateDetails		=	 (MultiQuoteFreightLegSellRates)finalDOB.getLegDetails().get(laneno);
	  originChargesList		=     legRateDetails.getOriginChargesList();
    originIndices         =     legRateDetails.getSelectedOriginChargesListIndices();
    logger.info("userid : "+masterDOB.getCreatedBy());
    if(originIndices!=null)
    {
      size              =   originIndices.length;
     if(size>1)
      {
        logger.info("originIndices[0] : "+originIndices[0]);
       
        logger.info("originIndices[1] : "+originIndices[1]);
      }
    }
    else size=0;
     chargeDescription = null;
    for(int i=0;i<size;i++)
    {
    
      if(originIndices[i]!=-1)
      {
      chargesDOB      =   (MultiQuoteCharges)originChargesList.get(originIndices[i]);
      logger.info("Origin insertSelectedRates::"+i+":"+chargesDOB); // newly added
      chargesList     =   chargesDOB.getChargeInfoList();
      chargeSize      =   chargesList.size();
     if(i<=1)
     {
         logger.info("chargesDOB.getChargeId : "+chargesDOB.getChargeId());
         logger.info("chargesDOB.getChargeDescriptionId : "+chargesDOB.getChargeDescriptionId());
         logger.info("chargeSize : "+chargeSize);
     }
      chargeSize      =   chargesList.size();
     logger.info("chargeSize : "+chargeSize);
      
    

      for(int j=0;j<chargeSize;j++)
      {
        chargeInfoDOB   =   (MultiQuoteChargeInfo)chargesList.get(j);
      
        pStmt.setLong(1,masterDOB.getUniqueId());
        pStmt.setString(2,chargesDOB.getSellBuyFlag());
        pStmt.setString(3,chargesDOB.getBuyChargeId());
        pStmt.setString(4,chargesDOB.getSellChargeId());
        pStmt.setString(5,chargesDOB.getBuyChargeLaneNo());
        pStmt.setString(6,chargesDOB.getChargeId());
        pStmt.setString(7,chargesDOB.getChargeDescriptionId());
        if(chargesDOB.getMarginDiscountFlag().split(",").length == chargesList.size()){
        Margin_discount_flag  = (chargesDOB.getMarginDiscountFlag().split(",")[j]);
        }
        //System.out.println("Margin_discount_flag---------2184"+Margin_discount_flag);
        Margin_discount_flag = Margin_discount_flag!= null ?Margin_discount_flag:"";
        if("-".equals(Margin_discount_flag))
        Margin_discount_flag = "BC".equalsIgnoreCase(chargesDOB.getSellBuyFlag())|| "B".equalsIgnoreCase(chargesDOB.getSellBuyFlag())?"M":"SC".equalsIgnoreCase(chargesDOB.getSellBuyFlag())|| "S".equalsIgnoreCase(chargesDOB.getSellBuyFlag())?"D":"";
       // System.out.println("Margin_discount_flag---------2186"+Margin_discount_flag);
        else if("".equals(Margin_discount_flag))
        	Margin_discount_flag=chargesDOB.getMarginDiscountFlag(); //@@Modified by kiran.v on 26/12/2011
        pStmt.setString(8,Margin_discount_flag);
        pStmt.setString(9,chargeInfoDOB.getMarginType());
        pStmt.setDouble(10,chargeInfoDOB.getMargin());
        pStmt.setString(11,chargeInfoDOB.getDiscountType());
        pStmt.setDouble(12,chargeInfoDOB.getDiscount());
        pStmt.setNull(13,Types.VARCHAR);
        pStmt.setNull(14,Types.INTEGER);
        pStmt.setString(15,chargeInfoDOB.getBreakPoint());
        pStmt.setString(16,chargesDOB.getCostIncurredAt());
        pStmt.setDouble(17,chargeInfoDOB.getBuyRate());
        pStmt.setDouble(18,chargeInfoDOB.getRecOrConSellRrate());
        pStmt.setNull(19,Types.INTEGER);
        pStmt.setNull(20,Types.INTEGER);
        pStmt.setInt(21,chargeInfoDOB.getLineNumber());
        pStmt.setString(22,chargeInfoDOB.isMarginTestFailed()?"Y":"N");
        pStmt.setInt(23,lane);
        pStmt.setString(24, chargesDOB.getZoneCode()!= null?chargesDOB.getZoneCode():"");
        pStmt.addBatch();
        Margin_discount_flag=null;
      }
 
    
    }
	  }
    destChargesList =   legRateDetails.getDestChargesList();
    destIndices     =   legRateDetails.getSelctedDestChargesListIndices();
    
    if(destIndices!=null)
    {
      size              =   destIndices.length;  
     
    }
    else size=0;
           chargeDescription = null;
   
    for(int i=0;i<size;i++)
    {
    
    
        if(destIndices[i] != -1)
        {
        chargesDOB      =   (MultiQuoteCharges)destChargesList.get(destIndices[i]);
        logger.info("Destination insertSelectedRates::"+i+":"+chargesDOB); // newly added          
      chargesList     =   chargesDOB.getChargeInfoList();
      chargeSize      =   chargesList.size();
   
      for(int j=0;j<chargeSize;j++)
      {
        chargeInfoDOB   =   (MultiQuoteChargeInfo)chargesList.get(j);
        pStmt.setLong(1,masterDOB.getUniqueId());
        pStmt.setString(2,chargesDOB.getSellBuyFlag());
        pStmt.setString(3,chargesDOB.getBuyChargeId());
        pStmt.setString(4,chargesDOB.getSellChargeId());
        pStmt.setString(5,chargesDOB.getBuyChargeLaneNo());
        pStmt.setString(6,chargesDOB.getChargeId());
        pStmt.setString(7,chargesDOB.getChargeDescriptionId());
        if(chargesDOB.getMarginDiscountFlag().split(",").length == chargesList.size()){
            Margin_discount_flag  = (chargesDOB.getMarginDiscountFlag().split(",")[j]);
            }
         //   System.out.println("Margin_discount_flag---------2244"+Margin_discount_flag);
            Margin_discount_flag = Margin_discount_flag!= null ?Margin_discount_flag:"";
         //   System.out.println("Margin_discount_flag---------2246"+Margin_discount_flag);
            if("-".equals(Margin_discount_flag)||"".equals(Margin_discount_flag))
        Margin_discount_flag = "BC".equalsIgnoreCase(chargesDOB.getSellBuyFlag())|| "B".equalsIgnoreCase(chargesDOB.getSellBuyFlag())?"M":"SC".equalsIgnoreCase(chargesDOB.getSellBuyFlag())|| "S".equalsIgnoreCase(chargesDOB.getSellBuyFlag())?"D":"";            
        pStmt.setString(8,Margin_discount_flag);
        pStmt.setString(9,chargeInfoDOB.getMarginType());
        pStmt.setDouble(10,chargeInfoDOB.getMargin());
        pStmt.setString(11,chargeInfoDOB.getDiscountType());
        pStmt.setDouble(12,chargeInfoDOB.getDiscount());
        pStmt.setNull(13,Types.VARCHAR);
        pStmt.setNull(14,Types.INTEGER);
        pStmt.setString(15,chargeInfoDOB.getBreakPoint());
        pStmt.setString(16,chargesDOB.getCostIncurredAt());
        pStmt.setDouble(17,chargeInfoDOB.getBuyRate());
        pStmt.setDouble(18,chargeInfoDOB.getRecOrConSellRrate());
        pStmt.setNull(19,Types.INTEGER);
        pStmt.setNull(20,Types.INTEGER);
        pStmt.setInt(21,chargeInfoDOB.getLineNumber());
        pStmt.setString(22,chargeInfoDOB.isMarginTestFailed()?"Y":"N");//included by shyam for DHL
        pStmt.setInt(23,lane);
        pStmt.setString(24, chargesDOB.getZoneCode()!= null?chargesDOB.getZoneCode():"");
        pStmt.addBatch();
        Margin_discount_flag=null;
      }
}    
    }
    
    if(!"Charges".equalsIgnoreCase(masterDOB.getQuoteWith()))//Added by Anil.k
    {
     ArrayList chargeInfoList = null;
	 MultiQuoteChargeInfo chargeInfo = null;
	 String[] temp_Marg_Dis_flag	= null;//Added by Anil.k for Save & Exit
	 chargeInfoList = ((MultiQuoteFreightLegSellRates)finalDOB.getLegDetails().get(0)).getFreightChargesList();
	 chargeInfo	=	chargeInfoList!=null?(MultiQuoteChargeInfo)chargeInfoList.get(lane):null;//Modified by Anil.k for Save & Exit 
	 String selectedBreaks  = finalDOB.getMultiQuoteSelectedBreaks();
	// System.out.println("selectedBreaks-------"+selectedBreaks);
	 String[]  selBreaks = selectedBreaks!=null?selectedBreaks.split(","):null;//Modified by Anil.k for Save & Exit
	 if(chargeInfo!=null){//Added by Anil.k for Save & Exit
		 temp_Marg_Dis_flag = (chargeInfo.getMarginDiscountFlag()!= null)?chargeInfo.getMarginDiscountFlag().split(","):null;
	 }
	 int breakssize = selBreaks!=null?selBreaks.length:0;//Modified by Anil.k for Save & Exit
	 int br=0;
	 pStmt1             =   connection.prepareStatement(selectedRatesInsertQuery1);
   
      
          
        for(int j=0;j<breakssize;j++)
        {
        	br=Integer.parseInt(selBreaks[j]);
   // System.out.println("br---------"+br);
          pStmt1.setLong(1,masterDOB.getUniqueId());
          pStmt1.setString(2,chargeInfo.getSellBuyFlag());
           if("SBR".equalsIgnoreCase(chargeInfo.getSellBuyFlag()))
            pStmt1.setNull(3,Types.VARCHAR);
          else
            pStmt1.setString(3,chargeInfo.getBuyChargeId());

          pStmt1.setString(4,chargeInfo.getSellChargeId());
          pStmt1.setString(5,chargeInfo.getBuyChargeLaneNo());
          pStmt1.setString(6,"");//
          pStmt1.setString(7,"FREIGHT RATE".equalsIgnoreCase(chargeInfo.getMultiRateDescriptions()[br])?"A FREIGHT RATE":chargeInfo.getMultiRateDescriptions()[br] );
         
             /* Margin_discount_flag  = temp_Marg_Dis_flag!= null?temp_Marg_Dis_flag[br]:"";
          
              System.out.println("Margin_discount_flag---------2295"+Margin_discount_flag);
              Margin_discount_flag = Margin_discount_flag!= null ?Margin_discount_flag:"";*/
          //    System.out.println("Margin_discount_flag---------300"+chargeInfo.getMarginDiscountFlag());
              if(chargeInfo.getMarginDiscountFlag().length()>1)
              {
            	  pStmt1.setString(8,temp_Marg_Dis_flag[br]);
              }
              else{
            	  pStmt1.setString(8,"BR".equalsIgnoreCase(chargeInfo.getSellBuyFlag())|| "SBR".equalsIgnoreCase(chargeInfo.getSellBuyFlag())?"M":"RSR".equalsIgnoreCase(chargeInfo.getSellBuyFlag())?"D":"");
              }
             
     
        //  pStmt1.setString(8,chargeInfo.getMarginDiscountFlag());
          
          if(chargeInfo.getMultiMarginTypes()!=null && chargeInfo.getMultiMarginTypes()[br]!=null)
        	  pStmt1.setString(9,chargeInfo.getMultiMarginTypes()[br]);
          else
        	  pStmt1.setString(9,"");
          
          if(chargeInfo.getMultiMargins()!= null && chargeInfo.getMultiMargins()[br]!=null)
        	  pStmt1.setDouble(10,Double.parseDouble("-".equals(chargeInfo.getMultiMargins()[br])?"0.00000":chargeInfo.getMultiMargins()[br]));
          else
        	  pStmt1.setDouble(10,0.0);

          if(chargeInfo.getMultiMarginTypes()!=null && chargeInfo.getMultiMarginTypes()[br]!=null)
        	  pStmt1.setString(11,chargeInfo.getMultiMarginTypes()[br]);
          else
        	  pStmt1.setString(11,"");

          if( chargeInfo.getMultiDiscountMargin() != null && chargeInfo.getMultiDiscountMargin()[br]!=null )
        	  pStmt1.setDouble(12,Double.parseDouble("-".equals(chargeInfo.getMultiDiscountMargin()[br])?"0.00000":chargeInfo.getMultiDiscountMargin()[br]));
          else
        	  pStmt1.setDouble(12,0.0);
          pStmt1.setNull(13,Types.VARCHAR);
          pStmt1.setNull(14,Types.INTEGER);
          pStmt1.setString(15, chargeInfo.getMultiBreakPoints()[br]);
          pStmt1.setString(16,chargeInfo.getCostIncurredAt());
          if(chargeInfo.getMultiBuyRates() != null && chargeInfo.getMultiBuyRates()[br]!=null)
        	  pStmt1.setDouble(17,Double.parseDouble("-".equals(chargeInfo.getMultiBuyRates()[br])?"0.00000":chargeInfo.getMultiBuyRates()[br]));
          else
        	  pStmt1.setDouble(17,0.0);
          
          if(chargeInfo.getMultiSellRates()!= null && chargeInfo.getMultiSellRates()[br]!=null)
        	  pStmt1.setDouble(18,Double.parseDouble("-".equals(chargeInfo.getMultiSellRates()[br])?"0.00000":chargeInfo.getMultiSellRates()[br]));
          else
        	  pStmt1.setDouble(18,0.0);
          
          pStmt1.setNull(19,Types.INTEGER);
		    pStmt1.setInt(20,lane);
          pStmt1.setInt(21,j);
          pStmt1.setString(22,chargeInfo.isMarginTestFailed()?"Y":"N");//included by shyam for DHL
          pStmt1.setString(23,chargeInfo.getServiceLevel());
          pStmt1.setString(24,chargeInfo.getFrequency());
          pStmt1.setString(25,chargeInfo.getTransitTime());
         // pStmt1.setString(26,chargeInfo.getCarrierName());
          //@@Modified by kiran.v on 08/08/2011 for Wpbn Issue 258778
          pStmt1.setString(26,chargeInfo.getCarrier());
          pStmt1.setTimestamp(27,chargeInfo.getValidUpto());
          //pStmt1.setString(28,chargeInfo.getFrequencyChecked());
          //pStmt1.setString(29,chargeInfo.getTransitTimeChecked());
          // pStmt1.setString(30,chargeInfo.getCarrierChecked());
          //pStmt1.setString(31,chargeInfo.getRateValidityChecked());
          //@@Modified by kiran.v on 08/08/2011 for Wpbn Issue 258778
          pStmt1.setString(28,masterDOB.getFrequencyChecked());
          pStmt1.setString(29,masterDOB.getTransitTimeChecked());
           pStmt1.setString(30,masterDOB.getCarrierChecked());
          pStmt1.setString(31,masterDOB.getFrequencyValidityChecked());
          pStmt1.setString(32,chargeInfo.getVersionNo());
          pStmt1.setInt(33,lane);
         //@@ kiran.v
         // pStmt1.setString(34,masterDOB.getServiceChecked());
          pStmt1.addBatch();
        }
        
        pStmt1.executeBatch();
    }//Added by Anil.k
        
    pStmt.executeBatch();
    
  }
  catch(Exception e)
  {
   
    logger.error(FILE_NAME+"Error in insertSelectedRates"+e);
    e.printStackTrace();
    throw new SQLException(e.toString());
  }
  finally
  {
    ConnectionUtil.closeConnection(null,pStmt);
    ConnectionUtil.closeConnection(null,pStmt1);
  }
} 
private void insertRoutePlanDetails(Connection connection,MultiQuoteFinalDOB finalDOB,String orgLoc,String destLoc,String orgPort,String destPort,int lane) throws SQLException
{
  PreparedStatement         pStmt	      = null;
  ResultSet                 rs          = null;

  MultiQuoteMasterDOB            masterDOB   = null;
  

  long              rt_plan_id          = 0;//RTPLAN_SEQ
  
  String            originTerminalId    = null;
  String            destTerminalId      = null;
  
  try
  {
    masterDOB     =     finalDOB.getMasterDOB();
    
    
    pStmt         =     connection.prepareStatement("SELECT RTPLAN_SEQ.NEXTVAL NEXTVAL FROM DUAL");
  
    rs            =     pStmt.executeQuery();
    
    while(rs.next())
      rt_plan_id  =     rs.getLong("NEXTVAL");
      
    if(pStmt!=null)
      pStmt.close();
    
    pStmt             =     connection.prepareStatement(routePlanInsertQuery);
    
    originTerminalId  =     getTerminalForLocation(connection,orgLoc);
    destTerminalId    =     getTerminalForLocation(connection,destLoc);
    
    pStmt.setLong(1,rt_plan_id);
    //pStmt.setLong(2,masterDOB.getQuoteId());//@@ Commented by subrahmanyam  for the enhancement 146971 on 1/12/08
    pStmt.setString(2,masterDOB.getQuoteId());//@@ Added by subrahmanyam  for the enhancement 146971 on 1/12/08
    pStmt.setString(3,originTerminalId);
    pStmt.setString(4,masterDOB.getDestinationTerminal());
    pStmt.setString(5,orgLoc);
    pStmt.setString(6,destLoc);
    pStmt.setString(7,masterDOB.getCustomerId());
    pStmt.setInt(8,masterDOB.getShipmentMode());
    pStmt.setTimestamp(9,masterDOB.getCreatedDate());
    pStmt.setTimestamp(10,masterDOB.getCreatedDate());
    
    pStmt.executeUpdate();
    
    if(pStmt!=null)
      pStmt.close();
    
    pStmt         =     connection.prepareStatement(routeLegInsertQuery);
    
   // for(int i=0;i<noOfLegs;i++)
  //  {
      
      
      originTerminalId  =     getTerminalForLocation(connection,orgPort);
      destTerminalId    =     getTerminalForLocation(connection,destPort);
      
      pStmt.setLong(1,rt_plan_id);
      pStmt.setInt(2,(lane));
      pStmt.setString(3,"LL");
      pStmt.setString(4,orgPort);
      pStmt.setString(5,destPort);
      pStmt.setInt(6,masterDOB.getShipmentMode());
      pStmt.setString(7,"Y");//@@Valid_invalid Flag(Y is for Valid)
      pStmt.setString(8,originTerminalId);
      pStmt.setString(9,destTerminalId);
      
      pStmt.addBatch();
   // }
    pStmt.executeBatch();
  }
  catch(Exception e)
  {
    //Logger.error(FILE_NAME,"Error while inserting into Route Plan:"+e);
    logger.error(FILE_NAME+"Error while inserting into Route Plan:"+e);
    e.printStackTrace();
    throw new SQLException(e.toString());
  }
  finally
		{
			try
			{
				ConnectionUtil.closeConnection(null,pStmt,rs);
			}
			catch(Exception ex)
			{
				//Logger.error(FILE_NAME,"Finally : QMSQuoteDAO[insertRoutePlanDetails(masterDOB,connection)]-> "+ex.toString());
      logger.error(FILE_NAME+"Finally : QMSQuoteDAO[insertRoutePlanDetails(masterDOB,connection)]-> "+ex.toString());
      throw new SQLException(ex.toString());
			}
  }
}


/**
 * This method is used to modify the master info from MultiQuoteMasterDOB to QuoteMaster and its child tables
* 
 * @param masterDOB 	an MultiQuoteMasterDOB object that contains the master information of the quote
* 
 * @exception SQLException 
 */
public void store(MultiQuoteFinalDOB	finalDOB)throws  SQLException
{
	MultiQuoteMasterDOB masterDOB = null;							 
	Connection connection		 =	null;
PreparedStatement psmt   =  null;
ResultSet         rs     =  null;
	try
	{
		connection = this.getConnection();
		masterDOB = finalDOB.getMasterDOB();
	     
	     masterDOB.setPreQuoteId(masterDOB.getQuoteId());
 
 /* if(finalDOB.isCompareFlag()){
   
	  this.modifyQuoteMasterDetails(finalDOB, connection);
  }
  else
  {
     psmt  = connection.prepareStatement("SELECT QUOTE_MASTER_SEQ.NEXTVAL FROM DUAL");
     rs  = psmt.executeQuery();
     masterDOB = finalDOB.getMasterDOB();
     
     masterDOB.setPreQuoteId(masterDOB.getQuoteId());
     
     if(rs.next())
      
     masterDOB.setUniqueId(rs.getInt(1));  */
     
     masterDOB.setVersionNo(masterDOB.getVersionNo()+1);
     finalDOB.setMasterDOB(masterDOB);
     this.insertQuoteMasterDetails(finalDOB,connection);
 // }      			
	}
	catch(SQLException sqEx)
	{
  sqEx.printStackTrace();
	
  logger.error(FILE_NAME+"QMSMultiQuoteDAO[create(masterDOB)] -> "+sqEx.toString());
  throw new SQLException(sqEx.toString());
	}
catch(Exception e)
	{
  e.printStackTrace();
	
  logger.error(FILE_NAME+"QMSMultiQuoteDAO[create(masterDOB)] -> "+e.toString());
  throw new SQLException(e.toString());
	}
	finally
	{
		try
		{
			ConnectionUtil.closeConnection(connection,psmt,rs);
		}
		catch(Exception ex)
		{
			
    logger.error(FILE_NAME+"Finally : QMSMultiQuoteDAO[create(masterDOB)]-> "+ex.toString());
    throw new SQLException(ex.toString());
		}
	}
}

/**
 * This method is used to updates the master info from MultiQuoteMasterDOB to QuoteMaster table
* 
 * @param masterDOB 	an MultiQuoteMasterDOB object that contains the master information of the quote
* 
 * @exception SQLException 
 */
private void modifyQuoteMasterDetails(MultiQuoteFinalDOB	finalDOB, Connection connection) throws  SQLException
{
PreparedStatement         pStmt	      =  null;
	ResultSet                 rs          =	null;
MultiQuoteMasterDOB            masterDOB   = null;
MultiQuoteFlagsDOB             flagsDOB    = null;
long                      id          = 0;
ArrayList                 legDetails  = null;
ArrayList		 chargeInfoList	   = null;
int               lanesize			= 0;
int 				laneno         =0;
int                       legSize     = 0;
boolean                   updateFlag  = true;
MultiQuoteFreightLegSellRates  legDOB      = null;
MultiQuoteFreightLegSellRates legRateDetails = null;
MultiQuoteChargeInfo        freightRates   = null;
try
{
 
chargeInfoList = ((MultiQuoteFreightLegSellRates)finalDOB.getLegDetails().get(0)).getFreightChargesList();
lanesize = chargeInfoList.size();  
  
  masterDOB = finalDOB.getMasterDOB();
for(int lane=0 ;lane<lanesize;lane++)  
	{

pStmt = connection.prepareStatement(getIdQry);
pStmt.setString(1,masterDOB.getQuoteId());
pStmt.setString(2,masterDOB.getQuoteId());
pStmt.setString(3,lane+"");
  rs    = pStmt.executeQuery();
  if(rs.next())
   id = rs.getLong("ID");
  
  if(rs!=null)rs.close();
  if(pStmt!=null)pStmt.close();
  freightRates	=  (MultiQuoteChargeInfo)chargeInfoList.get(lane)  ;
  laneno	= freightRates.getSelectedLaneNum();
  masterDOB = (MultiQuoteMasterDOB)finalDOB.getMasterDOB();
  masterDOB.setUniqueId(id);
  pStmt = connection.prepareStatement(masterUpdateQry);
  pStmt.clearParameters();
  pStmt.setString(1,masterDOB.getQuoteId());  
  pStmt.setInt(2,masterDOB.getShipmentMode());
  pStmt.setString(3,masterDOB.getPreQuoteId());
  
  if(masterDOB.isImpFlag())
    pStmt.setString(4,"I");
  else
    pStmt.setString(4,"U");
    
  pStmt.setTimestamp(5,masterDOB.getEffDate());
  pStmt.setTimestamp(6,masterDOB.getValidTo());
  pStmt.setInt(7,masterDOB.getAccValidityPeriod());
  pStmt.setString(8,masterDOB.getCustomerId());
  pStmt.setInt(9,masterDOB.getCustomerAddressId());
  pStmt.setTimestamp(10,masterDOB.getCreatedDate());
  pStmt.setString(11,masterDOB.getCreatedBy());
  pStmt.setString(12,masterDOB.getSalesPersonCode());
  pStmt.setString(13,masterDOB.getIndustryId());
  pStmt.setString(14,masterDOB.getCommodityId());
  
  if(masterDOB.isHazardousInd())
    pStmt.setString(15,"Y");
  else
    pStmt.setString(15,"N");
    
  if(masterDOB.getUnNumber()!=null)
    pStmt.setString(16,masterDOB.getUnNumber());
  else
   pStmt.setNull(16,Types.INTEGER);
  
  if(masterDOB.getCommodityClass()!=null)
   pStmt.setString(17,masterDOB.getCommodityClass());
  else
   pStmt.setNull(17,Types.INTEGER);
   
  
  pStmt.setString(18,masterDOB.getServiceLevelId());
  pStmt.setString(19,freightRates.getIncoTerms());
  pStmt.setString(20,masterDOB.getQuotingStation());
  pStmt.setString(21,freightRates.getOrginLoc());
  pStmt.setString(22,masterDOB.getShipperZipCode()[laneno]);
  pStmt.setString(23,freightRates.getOriginPort());
  pStmt.setString(24,masterDOB.getOverLengthCargoNotes());
  
  if(masterDOB.getRouteId()!=null)
    pStmt.setLong(25,Long.parseLong(masterDOB.getRouteId()));
  else
    pStmt.setNull(25,Types.INTEGER);
    
  pStmt.setString(26,freightRates.getDestLoc());
  pStmt.setString(27,masterDOB.getConsigneeZipCode()[laneno]);
  pStmt.setString(28,freightRates.getDestPort());
  

  
  pStmt.setString(29,finalDOB.getEscalatedTo());
  pStmt.setTimestamp(30,new java.sql.Timestamp((new java.util.Date()).getTime()));
  pStmt.setString(31,masterDOB.getUserId());
  
  pStmt.setString(32,masterDOB.getTerminalId());
  pStmt.setLong(33,masterDOB.getVersionNo());
  pStmt.setString(34,masterDOB.getBuyRatesPermission());
  pStmt.setString(35,masterDOB.getShipperZones()+"");
  pStmt.setString(36,masterDOB.getConsigneeZones()+"");

  
  if(finalDOB.getFlagsDOB()!=null)
  {
    
    flagsDOB = finalDOB.getFlagsDOB();
    
    
    if(flagsDOB.getPNFlag()!=null)
      pStmt.setString(37,flagsDOB.getPNFlag());
    else
      pStmt.setNull(37,Types.VARCHAR);
    
    if(flagsDOB.getUpdateFlag()!=null)
      pStmt.setString(38,flagsDOB.getUpdateFlag());
    else
      pStmt.setNull(38,Types.VARCHAR);
      
    if(flagsDOB.getActiveFlag()!=null)
      pStmt.setString(39,flagsDOB.getActiveFlag());
    else
      pStmt.setNull(39,Types.VARCHAR);
    
    if(flagsDOB.getSentFlag()!=null)
      pStmt.setString(40,flagsDOB.getSentFlag());
    else
      pStmt.setNull(40,Types.VARCHAR);
    
    if(flagsDOB.getCompleteFlag()!=null)
      pStmt.setString(41,flagsDOB.getCompleteFlag());
    else
      pStmt.setNull(41,Types.VARCHAR);
    
    if(flagsDOB.getQuoteStatusFlag()!=null)
      pStmt.setString(42,flagsDOB.getQuoteStatusFlag());
    else
      pStmt.setNull(42,Types.VARCHAR);
      
    pStmt.setString(43,flagsDOB.getEscalationFlag());
    pStmt.setString(44,flagsDOB.getInternalExternalFlag());
    pStmt.setString(45,flagsDOB.getEmailFlag());
    pStmt.setString(46,flagsDOB.getFaxFlag());
    pStmt.setString(47,flagsDOB.getPrintFlag());  
  }
  pStmt.setString(48,masterDOB.getShipperMode());
  pStmt.setString(49,masterDOB.getConsigneeMode());
  pStmt.setString(50,masterDOB.getShipperConsoleType());
  pStmt.setString(51,masterDOB.getConsigneeConsoleType());
   pStmt.setString(52,masterDOB.getSalesPersonFlag());
   pStmt.setTimestamp(53,masterDOB.getCustDate());//Added by Rakesh for Issue:236359
   pStmt.setString(54,masterDOB.getCustTime());//Added by Rakesh for Issue:
  pStmt.setLong(55,id); //Modified by Rakesh for Issue:236359
  int k = pStmt.executeUpdate();
  
  if(pStmt!=null)
    pStmt.close();
  
  finalDOB.setMasterDOB(masterDOB);
  
  if(masterDOB.getCustomerContacts()!=null)
    updateQuoteContactPersons(masterDOB,id,connection);
    
 // if(masterDOB.getSpotRatesFlag())
//   updateQuoteSpotRates(finalDOB,id,connection);
  legRateDetails =(MultiQuoteFreightLegSellRates)finalDOB.getLegDetails().get(laneno);
  if(legRateDetails.getChargeGroupIds()!=null)
    updateQuoteChargeGroups(finalDOB,id,connection,laneno);
  
  if(masterDOB.getHeaderFooter()!=null)
    updateQuoteHeaderFooter(masterDOB,id,connection);  
  
  legDetails = finalDOB.getLegDetails();
  legSize    = legDetails.size();
  
  for(int i=0;i<legSize;i++)
  {
      legDOB  = (MultiQuoteFreightLegSellRates)legDetails.get(i);
      if(legDOB.getSelectedFreightChargesListIndices()==null)
          updateFlag  = false;             
  }
  //if(updateFlag)
    updateSelectedRates(finalDOB,id,connection,laneno,lane);
  
  if(finalDOB.getExternalNotes()!=null)     
    updateNotes(connection,id,finalDOB);
 

 //@@Added by Kameswari for the WPBN issue-61289
 
  if(finalDOB.getAttachmentDOBList()!=null)
  {
      updateAttachmentIdList(finalDOB);
  }

}
   setTransactionDetails(masterDOB);
}
catch(SQLException sqEx)
	{
  sqEx.printStackTrace();
		//Logger.error(FILE_NAME,"QMSQuoteDAO[modifyQuoteMasterDetails(masterDOB,connection)] -> "+sqEx.toString());
  logger.error(FILE_NAME+"QMSMultiQuoteDAO[modifyQuoteMasterDetails(masterDOB,connection)] -> "+sqEx.toString());
  throw new SQLException(sqEx.toString());
	}
catch(Exception e)
	{
		e.printStackTrace();
  //Logger.error(FILE_NAME,"QMSQuoteDAO[modifyQuoteMasterDetails(masterDOB,connection)] -> "+e.toString());
  logger.error(FILE_NAME+"QMSMultiQuoteDAO[modifyQuoteMasterDetails(masterDOB,connection)] -> "+e.toString());
  throw new SQLException(e.toString());
	}
	finally
	{
		try
		{
			ConnectionUtil.closeConnection(null,pStmt,rs);
		}
		catch(Exception ex)
		{
			//Logger.error(FILE_NAME,"Finally : QMSQuoteDAO[insertQuoteMasterDetails(masterDOB,connection)]-> "+ex.toString());
    logger.error(FILE_NAME+"Finally : QMSQuoteDAO[insertQuoteMasterDetails(masterDOB,connection)]-> "+ex.toString());
    throw new SQLException(ex.toString());
		}
}
}    


public MultiQuoteFinalDOB getMultiQuoteZipZoneMapping(MultiQuoteFinalDOB finalDOB, int laneNo) throws SQLException
{
    Connection                  connection             = null;
    PreparedStatement           stmt                   = null;
    ResultSet                   rs                     = null;
    
    MultiQuoteMasterDOB              masterDOB              = null;
    
    String                      shipZoneQry            = "";
    String                      consZoneQry            = "";
    
    String                      sqlQuery1              = "";
    String                      sqlQuery2              = "";
    String                      sqlQuery               = "";
    
    ArrayList                   delivZipCodes          = null;
    ArrayList                   pickUpZipCodes         = null;
    HashMap                     pickUpZoneZipMap       = new HashMap();
    HashMap                     deliveryZoneZipMap     = new HashMap();
    //StringTokenizer             str                    = null;
    //String[]                    shipperZoneArray       = null;
    //String                      shipperZones           = "";
    //String[]                    consigneeZoneArray     = null;
    //String                      consigneeZones         = "";
    boolean                     executeQry             = true;
    String                      originCountryId        = null;
    String                      destinationCountryId   = null;
    StringBuffer                shZones                = new StringBuffer();
    int                         shZonesLength          = 0;
    String[]                    shZoneArray            = null;
    
    StringBuffer                consigneeZones         = new StringBuffer();
    int                         consigneeZonesLength   = 0;
    String[]                    consigneeZoneArray     = null;
        
    
    
    try
    {
      connection            =   this.getConnection();
      
      //stmt                  =   connection.prepareStatement();
      
      masterDOB             =   finalDOB.getMasterDOB();
      originCountryId       =   finalDOB.getHeaderDOB().getOriginCountryId()[laneNo];
      destinationCountryId  =   finalDOB.getHeaderDOB().getDestinationCountryId()[laneNo];
      
      if(masterDOB.getShipperZones()!=null && masterDOB.getShipperZones()[laneNo]!=null && !"".equals(masterDOB.getShipperZones()[laneNo])) // Added by Gowtham on 19Feb2011
      {
        /*str               =   new StringTokenizer(masterDOB.getShipperZones(),",");      
        shipperZoneArray  =   new String[str.countTokens()];
        
        for(int i=0;str.hasMoreTokens();i++)
        {
            shipperZoneArray[i] = str.nextToken();
        }*/
        if(masterDOB.getShipperZones()[laneNo].split(",").length > 0)
        {
           shZonesLength  =  masterDOB.getShipperZones()[laneNo].split(",").length;
           shZoneArray    =  masterDOB.getShipperZones()[laneNo].split(",");
            
            for(int k=0;k<shZonesLength;k++)
            {
                if(k==(shZonesLength-1))
                    shZones.append("?");
                else
                    shZones.append("?,");
            }
        }
      }
      
      /*if(shipperZoneArray!=null)
      {
        for(int i=0;i<shipperZoneArray.length;i++)
        {
          if((i+1)==shipperZoneArray.length)
              shipperZones  = shipperZones + "'"+shipperZoneArray[i]+"'";
          else        
              shipperZones  = shipperZones + "'"+shipperZoneArray[i]+"',";
        }
      }
      str = null;*/
      
      if(masterDOB.getConsigneeZones()!=null &&  masterDOB.getConsigneeZones()[laneNo]!=null && !"".equals(masterDOB.getConsigneeZones()[laneNo]) ) // Added by Gowtham on 19Feb2011
      {
        /*str                 = new StringTokenizer(masterDOB.getConsigneeZones(),",");
        consigneeZoneArray  = new String[str.countTokens()];
        
        for(int i=0;str.hasMoreTokens();i++)
        {
            consigneeZoneArray[i] = str.nextToken();
        }*/
        if(masterDOB.getConsigneeZones()[laneNo].split(",").length > 0)
        {
            consigneeZonesLength  =  masterDOB.getConsigneeZones()[laneNo].split(",").length;
            consigneeZoneArray    =  masterDOB.getConsigneeZones()[laneNo].split(",");
            
            for(int k=0;k<consigneeZonesLength;k++)
            {
                if(k==(consigneeZonesLength-1))
                    consigneeZones.append("?");
                else
                    consigneeZones.append("?,");
            }
        }
      }
      
      /*if(consigneeZoneArray!=null)
      {
        for(int i=0;i<consigneeZoneArray.length;i++)
        {
          if((i+1)==consigneeZoneArray.length)
              consigneeZones  = consigneeZones + "'"+consigneeZoneArray[i]+"'";
          else        
              consigneeZones  = consigneeZones + "'"+consigneeZoneArray[i]+"',";
        }
      }*/
      
      
      /*if(masterDOB.getShipperZones()!=null && masterDOB.getShipperZones().trim().length()!=0)
          shipZoneQry = " AND D.ZONE IN ("+shipperZones+") ";
      if(masterDOB.getConsigneeZones()!=null && masterDOB.getConsigneeZones().trim().length()!=0)
          consZoneQry = " AND D.ZONE IN ("+consigneeZones+") ";*/
          
     /* if("CA".equalsIgnoreCase(originCountryId))
      {
        sqlQuery1       = "SELECT D.ZONE, NULL ALPHANUMERIC, D.TO_ZIPCODE, D.FROM_ZIPCODE,'Pickup' CHARGE_TYPE "+
                          "FROM QMS_ZONE_CODE_MASTER_CA M, QMS_ZONE_CODE_DTL_CA D WHERE D.ZONE_CODE = M.ZONE_CODE AND M.LOCATION_ID = ?"+
                          " AND M.SHIPMENT_MODE = ? AND NVL(M.CONSOLE_TYPE,'~')= ? AND D.ZONE IN ("+shZones+")";
      }
      else
      {
        sqlQuery1       = "SELECT D.ZONE, D.ALPHANUMERIC ALPHANUMERIC, D.TO_ZIPCODE, D.FROM_ZIPCODE,'Pickup' CHARGE_TYPE "+
                          "FROM QMS_ZONE_CODE_MASTER M, QMS_ZONE_CODE_DTL D WHERE D.ZONE_CODE = M.ZONE_CODE AND M.ORIGIN_LOCATION = ? "+
                          "AND M.SHIPMENT_MODE = ? AND NVL(M.CONSOLE_TYPE,'~')= ? AND D.ZONE IN ("+shZones+")";
      }*/
    
      if("CA".equalsIgnoreCase(originCountryId))
      {
        sqlQuery1       = "SELECT D.ZONE, NULL ALPHANUMERIC, D.TO_ZIPCODE, D.FROM_ZIPCODE,'Pickup' CHARGE_TYPE "+
                          "FROM QMS_ZONE_CODE_MASTER_CA M, QMS_ZONE_CODE_DTL_CA D WHERE D.ZONE_CODE = M.ZONE_CODE AND M.LOCATION_ID = ?"+
                          " AND M.SHIPMENT_MODE = ? AND NVL(M.CONSOLE_TYPE,'~')= ? AND D.ZONE IN ("+shZones+")";
      }
      else
      {
        sqlQuery1       = "SELECT D.ZONE, D.ALPHANUMERIC ALPHANUMERIC, D.TO_ZIPCODE, D.FROM_ZIPCODE,'Pickup' CHARGE_TYPE "+
                          "FROM QMS_ZONE_CODE_MASTER M, QMS_ZONE_CODE_DTL D WHERE D.ZONE_CODE = M.ZONE_CODE AND M.ORIGIN_LOCATION = ? "+
                          "AND M.SHIPMENT_MODE = ? AND NVL(M.CONSOLE_TYPE,'~')= ? AND D.ZONE IN ("+shZones+")";
      }
      
      
      /*if("CA".equalsIgnoreCase(destinationCountryId))
      {
        sqlQuery2       = "SELECT D.ZONE, NULL ALPHANUMERIC, D.TO_ZIPCODE, D.FROM_ZIPCODE,'Delivery' CHARGE_TYPE "+
                          "FROM QMS_ZONE_CODE_MASTER_CA M,QMS_ZONE_CODE_DTL_CA D WHERE D.ZONE_CODE = M.ZONE_CODE AND M.LOCATION_ID = ? "+
                          "AND M.SHIPMENT_MODE = ? AND NVL(M.CONSOLE_TYPE,'~')= ? AND D.ZONE IN ("+consigneeZones+") ";
      }
      else
      {
        sqlQuery2       = "SELECT D.ZONE, D.ALPHANUMERIC ALPHANUMERIC, D.TO_ZIPCODE, D.FROM_ZIPCODE,'Delivery' CHARGE_TYPE "+
                          "FROM QMS_ZONE_CODE_MASTER M,QMS_ZONE_CODE_DTL D WHERE D.ZONE_CODE = M.ZONE_CODE AND M.ORIGIN_LOCATION = ? "+
                          "AND M.SHIPMENT_MODE = ? AND NVL(M.CONSOLE_TYPE,'~')= ? AND D.ZONE IN ("+consigneeZones+") ";
      }*/
       if("CA".equalsIgnoreCase(destinationCountryId))
      {
        sqlQuery2       = "SELECT D.ZONE, NULL ALPHANUMERIC, D.TO_ZIPCODE, D.FROM_ZIPCODE,'Delivery' CHARGE_TYPE "+
                          "FROM QMS_ZONE_CODE_MASTER_CA M,QMS_ZONE_CODE_DTL_CA D WHERE D.ZONE_CODE = M.ZONE_CODE AND M.LOCATION_ID = ? "+
                          "AND M.SHIPMENT_MODE = ? AND NVL(M.CONSOLE_TYPE,'~')= ? AND D.ZONE IN ("+consigneeZones+") ";
      }
      else
      {
        sqlQuery2       = "SELECT D.ZONE, D.ALPHANUMERIC ALPHANUMERIC, D.TO_ZIPCODE, D.FROM_ZIPCODE,'Delivery' CHARGE_TYPE "+
                          "FROM QMS_ZONE_CODE_MASTER M,QMS_ZONE_CODE_DTL D WHERE D.ZONE_CODE = M.ZONE_CODE AND M.ORIGIN_LOCATION = ? "+
                          "AND M.SHIPMENT_MODE = ? AND NVL(M.CONSOLE_TYPE,'~')= ? AND D.ZONE IN ("+consigneeZones+") ";
      }
      
      if(masterDOB.getShipperZones() !=null && masterDOB.getShipperZones()[laneNo] !=null && masterDOB.getConsigneeZones()!=null && masterDOB.getConsigneeZones()[laneNo]!=null )
      {
          if(masterDOB.getShipperZipCode()==null && masterDOB.getConsigneeZipCode()==null)
          {
            sqlQuery    =  sqlQuery1+" UNION "+sqlQuery2 +" Order By Zone,Alphanumeric, From_Zipcode";
            stmt        =  connection.prepareStatement(sqlQuery);
            stmt.setString(1,masterDOB.getOriginLocation()[laneNo]);
            stmt.setString(2,masterDOB.getShipperMode());
            if("1".equalsIgnoreCase(masterDOB.getShipperMode()))
                stmt.setString(3,"~");
            else
                stmt.setString(3,masterDOB.getShipperConsoleType());
            
            int k=0;
            if(shZoneArray!=null && shZoneArray.length>0)
            {
            	int shZoneArrLen	=	shZoneArray.length;
              for(k=0;k<shZoneArrLen;k++)
                  stmt.setString(k+4,shZoneArray[k]);
              k = k+3;
            }
            else
              k = k+2;
            
            
            stmt.setString(k+1,masterDOB.getDestLocation()[laneNo]);
            stmt.setString(k+2,masterDOB.getConsigneeMode());
            
            if("1".equalsIgnoreCase(masterDOB.getConsigneeMode()))
                stmt.setString(k+3,"~");
            else
                stmt.setString(k+3,masterDOB.getConsigneeConsoleType());
            
            if(consigneeZoneArray!=null && consigneeZoneArray.length>0)
            {
            	int conZoneArrLen	=	consigneeZoneArray.length;
              for(int j=0;j<conZoneArrLen;j++)
                  stmt.setString(j+k+4,consigneeZoneArray[j]);
            }
            rs = stmt.executeQuery();
            
          }
          else if (masterDOB.getShipperZipCode()==null && masterDOB.getConsigneeZipCode()!=null)
          {
            sqlQuery    =   sqlQuery1 + " Order By Zone,Alphanumeric, From_Zipcode";
            stmt        =  connection.prepareStatement(sqlQuery);
            stmt.setString(1,masterDOB.getOriginLocation()[laneNo]);
            stmt.setString(2,masterDOB.getShipperMode());
            if("1".equalsIgnoreCase(masterDOB.getShipperMode()))
                stmt.setString(3,"~");
            else
                stmt.setString(3,masterDOB.getShipperConsoleType());
                
            if(shZoneArray!=null && shZoneArray.length>0)
            {
            	int shZoneArrLen	=	shZoneArray.length;
              for(int k=0;k<shZoneArrLen;k++)
                  stmt.setString(k+4,shZoneArray[k]);
            }
            rs = stmt.executeQuery();
          }
          else if(masterDOB.getShipperZipCode()!=null && masterDOB.getConsigneeZipCode()==null)
          {
            sqlQuery    =   sqlQuery2 + "Order By Zone,Alphanumeric, From_Zipcode";
            stmt        =  connection.prepareStatement(sqlQuery);
            stmt.setString(1,masterDOB.getDestLocation()[laneNo]);
            stmt.setString(2,masterDOB.getConsigneeMode());
            if("1".equalsIgnoreCase(masterDOB.getConsigneeMode()))
                stmt.setString(3,"~");
            else
                stmt.setString(3,masterDOB.getConsigneeConsoleType());
                
            if(consigneeZoneArray!=null && consigneeZoneArray.length>0)
            {
            	int conZoneArrLen	=	consigneeZoneArray.length;
              for(int k=0;k<conZoneArrLen;k++)
                  stmt.setString(k+4,consigneeZoneArray[k]);
            }
            rs = stmt.executeQuery();
            
          }
      }
      else
      {
          if(masterDOB.getShipperZones()==null && masterDOB.getConsigneeZones()!=null && masterDOB.getConsigneeZipCode()==null)
          {
            sqlQuery    =  sqlQuery2 +" Order By Zone,Alphanumeric, From_Zipcode";
            stmt        =  connection.prepareStatement(sqlQuery);
            stmt.setString(1,masterDOB.getDestLocation()[laneNo]);
            stmt.setString(2,masterDOB.getConsigneeMode());
            if("1".equalsIgnoreCase(masterDOB.getConsigneeMode()))
                stmt.setString(3,"~");
            else
                stmt.setString(3,masterDOB.getConsigneeConsoleType());
                
            if(consigneeZoneArray!=null && consigneeZoneArray.length>0)
            {
            	int conZoneArrLen	=	consigneeZoneArray.length;
              for(int k=0;k<conZoneArrLen;k++)
                  stmt.setString(k+4,consigneeZoneArray[k]);
            }
            rs = stmt.executeQuery();            
          }
          else if(masterDOB.getShipperZones()!=null && masterDOB.getConsigneeZones()==null  && masterDOB.getShipperZipCode()==null)
          {
            sqlQuery    =   sqlQuery1 +" Order By Zone,Alphanumeric, From_Zipcode";
            stmt        =  connection.prepareStatement(sqlQuery);
            stmt.setString(1,masterDOB.getOriginLocation()[laneNo]);
            stmt.setString(2,masterDOB.getShipperMode());
            if("1".equalsIgnoreCase(masterDOB.getShipperMode()))
                stmt.setString(3,"~");
            else
                stmt.setString(3,masterDOB.getShipperConsoleType());
                
            if(shZoneArray!=null && shZoneArray.length>0)
            {
            	int shZoneArrLen	=	shZoneArray.length;
              for(int k=0;k<shZoneArrLen;k++)
                  stmt.setString(k+4,shZoneArray[k]);
            }
            rs = stmt.executeQuery();
          }
      }
          
      if(rs!=null)
      {
        while(rs.next())
        {
           if("Pickup".equalsIgnoreCase(rs.getString("CHARGE_TYPE")))
            {            
              String from_toZip = null;
              if(pickUpZoneZipMap.containsKey(rs.getString("ZONE")))
              {             
                pickUpZipCodes = (ArrayList) pickUpZoneZipMap.get(rs.getString("ZONE"));
                if(rs.getString("ALPHANUMERIC")!=null)
                 from_toZip = rs.getString("ALPHANUMERIC")+rs.getString("FROM_ZIPCODE")+" - "+rs.getString("ALPHANUMERIC")+rs.getString("TO_ZIPCODE");
                else
                 from_toZip = rs.getString("FROM_ZIPCODE")+" - "+rs.getString("TO_ZIPCODE");
                if(from_toZip!=null)
                  pickUpZipCodes.add(from_toZip);
                pickUpZoneZipMap.put(rs.getString("ZONE"),pickUpZipCodes);
              }
              else
              {
                pickUpZipCodes = new ArrayList();
                if(rs.getString("FROM_ZIPCODE")!=null)
                {
                  if(rs.getString("ALPHANUMERIC")!=null)
                   from_toZip = rs.getString("ALPHANUMERIC")+rs.getString("FROM_ZIPCODE")+" - "+rs.getString("ALPHANUMERIC")+rs.getString("TO_ZIPCODE");
                  else
                   from_toZip = rs.getString("FROM_ZIPCODE")+" - "+rs.getString("TO_ZIPCODE");
                  if(from_toZip!=null)
                    pickUpZipCodes.add(from_toZip);
                  pickUpZoneZipMap.put(rs.getString("ZONE"),pickUpZipCodes);
                }
              }
            }
            else if("Delivery".equalsIgnoreCase(rs.getString("CHARGE_TYPE")))
            {            
              String from_toZip = null;
              if(deliveryZoneZipMap.containsKey(rs.getString("ZONE")))
              {
                delivZipCodes = (ArrayList) deliveryZoneZipMap.get(rs.getString("ZONE"));
                if(rs.getString("ALPHANUMERIC")!=null)
                 from_toZip = rs.getString("ALPHANUMERIC")+rs.getString("FROM_ZIPCODE")+" - "+rs.getString("ALPHANUMERIC")+rs.getString("TO_ZIPCODE");
                else
                 from_toZip = rs.getString("FROM_ZIPCODE")+" - "+rs.getString("TO_ZIPCODE");
                if(from_toZip!=null)
                  delivZipCodes.add(from_toZip);
                deliveryZoneZipMap.put(rs.getString("ZONE"),delivZipCodes);
              }
              else
              { 
                delivZipCodes = new ArrayList();
                if(rs.getString("ALPHANUMERIC")!=null)
                 from_toZip = rs.getString("ALPHANUMERIC")+rs.getString("FROM_ZIPCODE")+" - "+rs.getString("ALPHANUMERIC")+rs.getString("TO_ZIPCODE");
                else
                 from_toZip = rs.getString("FROM_ZIPCODE")+" - "+rs.getString("TO_ZIPCODE");
                if(from_toZip!=null)
                  delivZipCodes.add(from_toZip);
                deliveryZoneZipMap.put(rs.getString("ZONE"),delivZipCodes);
              }
            }
        }
      }
      
      if(pickUpZoneZipMap.size() > 0)
         ( (MultiQuoteFreightLegSellRates)finalDOB.getLegDetails().get(laneNo)).setPickZoneZipMap(pickUpZoneZipMap);
      if(deliveryZoneZipMap.size() > 0)
         ((MultiQuoteFreightLegSellRates)finalDOB.getLegDetails().get(laneNo)).setDeliveryZoneZipMap(deliveryZoneZipMap);
      
    }
    catch(SQLException sql)
    {
      //Logger.error(FILE_NAME,"SQLException while getting the Zip Zone Code Mapping: "+sql);
      logger.error(FILE_NAME+"SQLException while getting the Zip Zone Code Mapping: "+sql);
      sql.printStackTrace();
      throw new SQLException(sql.toString());
    }
    catch(Exception e)
    {
      //Logger.error(FILE_NAME,"Error while getting the Zip Zone Code Mapping: "+e);
      logger.error(FILE_NAME+"Error while getting the Zip Zone Code Mapping: "+e);
      e.printStackTrace();
      throw new SQLException(e.toString());
    }
    finally
    {
      if(rs!=null)
          rs.close();
      if(stmt!=null)
          stmt.close();
      if(connection !=null)
          connection.close();
    }
    return finalDOB;
}

public MultiQuoteFinalDOB getMultiQuoteCartagesForAdd(MultiQuoteFinalDOB finalDOB) throws SQLException
{
    Connection                connection              = null;
    CallableStatement         csmt                    = null;
    ResultSet                 rs                      = null;
    ResultSet                 zoneRs                  = null;
    ResultSet                 pickWeightBreaksRS      = null;
    ResultSet                 delWeightBreaksRS       = null;
    /*ResultSet                 pickWeightBreaksRSList  = null;
    ResultSet                 delWeightBreaksRSList   = null;*/
   // ArrayList                 originChargesList       = null;//to maintain the list of  all origin charge dobs
  //  ArrayList                 destChargesList         = null;//to maintain the list of  all origin charge dobs
    MultiQuoteCharges              cartageChargesDOB       = null;//to maintain one record that is to be displayed
    MultiQuoteCharges              cartageDelChargesDOB    = null;//to maintain one record that is to be displayed
    MultiQuoteChargeInfo           chargeInfo              = null;
    ArrayList                 chargeInfoList          = null;
    ArrayList 					originChargesList     = null;
    ArrayList                 destChargesList         = null;
    String                    flag                    = null;
    String                    pickWeightBreak         = null;
    String                    pickRateType            = null;
    String                    delWeightBreak          = null;
    String                    delRateType             = null;
    MultiQuoteMasterDOB            masterDOB               = null;
    double                    sellRate                = 0;
    ArrayList                 pickUpQuoteCartageRates   = null;
    ArrayList                 finalLegDOB    = new ArrayList();
    ArrayList                 deliveryQuoteCartageRates = null;
    ArrayList                 pickupWeightBreaksList    = null;
    ArrayList                 delWeightBreaksList       = null;
    HashMap                   pickUpZoneZipMap          = null;
    HashMap                   deliveryZoneZipMap        = null;
    HashMap                   pickUpZoneCode            = null;
    HashMap                   deliveryZoneCode          = new HashMap();
    MultiQuoteCartageRates         pickQuoteCartageRates     = null;
    MultiQuoteCartageRates         delQuoteCartageRates      = null;
    boolean                   addToPickupList           = true;
    boolean                   addToDeliveryList         = true;
    boolean                   isPickupMin               = false;
    boolean                   isPickupFlat              = false;
    boolean                   isPickupMax               = false;
    boolean                   isDeliveryMin             = false;
    boolean                   isDeliveryFlat            = false;
    boolean                   isDeliveryMax             = false;
	ArrayList					legDetails					= null;
    MultiQuoteFreightLegSellRates   legDOB                  = null;
    int                        laneSize                      = 0;
    
  //Added By Kishore For the ChargeBasis in the Annexure PDF on 27-May-11
    ArrayList                 pickupChargeBasisList    = null;
    ArrayList                 delChargeBasisList       = null;
    String					  pickupMinChargeBasis	   = "";
    String					  pickupFlatChargeBasis	   = "";
    String					  pickupMaxChargeBasis	   = "";
    String					  deliveryFlatChargeBasis  = "";
    String					  deliveryMinChargeBasis   = "";
    String					  deliveryMaxChargeBasis   = "";
    
    try
    {
      //@@for re-initialising all the objects put in session previously.
      legDetails = finalDOB.getLegDetails();
      laneSize  = legDetails.size();
      connection  = this.getConnection();
      connection.setAutoCommit(false);
      csmt  = connection.prepareCall("{CALL QMS_QUOTE_PACK.quote_sell_buy_cartages_proc(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");
      for(int laneNo =0;laneNo <laneSize;laneNo++){
    	  
    	  //Added By Kishore Podili For Multiple Zone Codes
    	   pickUpQuoteCartageRates = new ArrayList();
    	 deliveryQuoteCartageRates = new ArrayList();
    	 pickupWeightBreaksList    = new ArrayList();
    	 delWeightBreaksList       = new ArrayList();
    	 pickUpZoneZipMap          = new HashMap();
    	 deliveryZoneZipMap        = new HashMap();
    	 pickUpZoneCode            = new HashMap();
    	 deliveryZoneCode          = new HashMap();
    	 originChargesList         = null;
    	 destChargesList           = null;
    	 chargeInfoList            = null;
    	 cartageChargesDOB         = null;
    	 cartageDelChargesDOB      = null;
    	 
    	 //Added By Kishore For the ChargeBasis in the Annexure PDF on 27-May-11
    	 pickupChargeBasisList	   = new ArrayList();
    	 delChargeBasisList		   = new ArrayList();
    	 
    	 
     legDOB = (MultiQuoteFreightLegSellRates)legDetails.get(laneNo);
     // originChargesList  = legDOB.getOriginChargesList();Commented by govind for Duplication of Charges in charges group
     // destChargesList    = legDOB.getDestChargesList();
      legDOB.setPickUpCartageRatesList(pickUpQuoteCartageRates);
      legDOB.setDeliveryCartageRatesList(deliveryQuoteCartageRates); 
      legDOB.setPickupWeightBreaks(pickupWeightBreaksList);
      legDOB.setDeliveryWeightBreaks(delWeightBreaksList);
      legDOB.setPickZoneZipMap(pickUpZoneZipMap);
      legDOB.setDeliveryZoneZipMap(deliveryZoneZipMap);
      
      //Added By Kishore For the ChargeBasis in the Annexure PDF on 27-May-11
      legDOB.setPickupChargeBasisList(pickupChargeBasisList);
      legDOB.setDelChargeBasisList(delChargeBasisList);
      
      masterDOB = finalDOB.getMasterDOB();
      long start = System.currentTimeMillis();
      //Modified By Kishore For Codereview FOr 236286 CR
      if((masterDOB.getShipperZipCode()[laneNo]!=null && !"".equals(masterDOB.getShipperZipCode()[laneNo]))||
         (masterDOB.getShipperZones()[laneNo]!=null  && !"".equals(masterDOB.getShipperZones()[laneNo]))||
         (masterDOB.getConsigneeZipCode()[laneNo]!= null && !"".equals(masterDOB.getConsigneeZipCode()[laneNo]))||
         (masterDOB.getConsigneeZones()[laneNo]!=null && !"".equals(masterDOB.getConsigneeZones()[laneNo]))){
     
      csmt.setString(1,"");//masterDOB.getShipperZipCode()[laneNo]!=null?masterDOB.getShipperZipCode()[laneNo]:""
      csmt.setString(2,(masterDOB.getShipperZones()[laneNo]!=null?masterDOB.getShipperZones()[laneNo]:"").replaceAll(",","~"));
      csmt.setString(3,"");//masterDOB.getConsigneeZipCode()[laneNo]!=null?masterDOB.getConsigneeZipCode()[laneNo]:""
      csmt.setString(4,(masterDOB.getConsigneeZones()[laneNo]!=null?masterDOB.getConsigneeZones()[laneNo]:"").replaceAll(",","~"));
      csmt.setString(5,masterDOB.getSalesPersonCode());
      csmt.setString(6,masterDOB.getTerminalId());
      csmt.setString(7,masterDOB.getBuyRatesPermission());
      csmt.setString(8,masterDOB.getOriginLocation()[laneNo]);
      csmt.setString(9,masterDOB.getDestLocation()[laneNo]);
      csmt.setDouble(10,finalDOB.getCartageMargin()); 
      csmt.setDouble(11,finalDOB.getCartageDiscount());
      csmt.setString(12,masterDOB.getCustomerId());
      csmt.setString(13,""+masterDOB.getShipmentMode());
      csmt.setString(14,((masterDOB.getShipperZipCode()[laneNo]!=null && !"".equals(masterDOB.getShipperZipCode()[laneNo])) || (masterDOB.getShipperZones()[laneNo]!=null && !"".equals(masterDOB.getShipperZones()[laneNo])))?""+masterDOB.getShipmentMode():""); //shipper mode
      if("2".equalsIgnoreCase(""+masterDOB.getShipmentMode()))
          csmt.setString(15,"list".equalsIgnoreCase(masterDOB.getMultiquoteweightBrake())?"FCL":"LCL");
      else
          csmt.setString(15,"~");
        
      csmt.setString(16,((masterDOB.getConsigneeZipCode()[laneNo]!=null && !"".equals(masterDOB.getConsigneeZipCode()[laneNo])) || (masterDOB.getConsigneeZones()[laneNo]!=null && !"".equals(masterDOB.getConsigneeZones()[laneNo])))?""+masterDOB.getShipmentMode():"");
      
      if("2".equalsIgnoreCase(""+masterDOB.getShipmentMode()))
          csmt.setString(17,"list".equalsIgnoreCase(masterDOB.getMultiquoteweightBrake())?"FCL":"LCL");
      else
          csmt.setString(17,"~");
          
      csmt.setString(18,masterDOB.getOperation());
      //csmt.setString(19,masterDOB.getQuoteId()!=0?masterDOB.getQuoteId()+"":"");  //@@ Commented by subrahmanyam  for the enhancement 146971 on 1/12/08
      csmt.setString(19,masterDOB.getQuoteId()!=null?masterDOB.getQuoteId()+"":"");  //@@ Added by subrahmanyam  for the enhancement 146971 on 1/12/08
      
      csmt.registerOutParameter(20,OracleTypes.CURSOR);
      csmt.registerOutParameter(21,OracleTypes.CURSOR);
      csmt.registerOutParameter(22,OracleTypes.CURSOR);//Distinct Pickup Charge slabs
      csmt.registerOutParameter(23,OracleTypes.CURSOR);//Distinct Delivery Charge slabs
      
      csmt.execute();
      
      
      rs  = (ResultSet)csmt.getObject(20);
      logger.info("Time Taken for DB procedure in milli seconds for 3rd screen (quote_sell_buy_cartages_proc) :  " + ((System.currentTimeMillis()) - start)  + "   UserId ::"+ masterDOB.getUserId() + " Origin :: "+ masterDOB.getOriginLocation()[laneNo] + " Destination::"+masterDOB.getDestLocation()[laneNo]+" TerminalId :: "+ masterDOB.getTerminalId());
      pickWeightBreaksRS      = (ResultSet)csmt.getObject(22);
      delWeightBreaksRS       = (ResultSet)csmt.getObject(23);
    
      while(rs.next())
      {
        if("Pickup".equalsIgnoreCase(rs.getString("COST_INCURREDAT")))
        {
        flag = rs.getString("SEL_BUY_FLAG");
       

        if( (masterDOB.getShipperZipCode()!=null && masterDOB.getShipperZipCode()[laneNo]!=null)
            ||(masterDOB.getShipperZones()!=null )) //&& masterDOB.getShipperZones()[laneNo].indexOf(",")==-1) 
        	//Commented By Kishore for multiple ZoneCodes
        {
          if(originChargesList==null)
          {
            originChargesList = new ArrayList();
          }
          //Added By Kishore for multiple ZoneCodes
          if(cartageChargesDOB!=null 
        		  && (rs.getString("SELLCHARGEID").equalsIgnoreCase(cartageChargesDOB.getSellChargeId()) || rs.getString("SELLCHARGEID").equalsIgnoreCase(cartageChargesDOB.getBuyChargeId()))
        		  && (rs.getString("ZONE")!=null && rs.getString("ZONE").equalsIgnoreCase(cartageChargesDOB.getZoneCode()))
             )
          {
            chargeInfo  = new MultiQuoteChargeInfo();
            
            chargeInfoList.add(chargeInfo);
            
            chargeInfo.setBreakPoint(rs.getString("CHARGESLAB"));
            
            if(rs.getString("CURRENCY")!=null && rs.getString("CURRENCY").trim().length()!=0)
              chargeInfo.setCurrency(rs.getString("CURRENCY"));
            else
               chargeInfo.setCurrency(masterDOB.getTerminalCurrency());
               
            chargeInfo.setBuyRate(rs.getDouble("BUYRATE"));
            chargeInfo.setRecOrConSellRrate(rs.getDouble("SELLRATE"));
            chargeInfo.setSellChargeMargin(rs.getDouble("MARGINVALUE"));
            chargeInfo.setSellChargeMarginType(rs.getString("MARGIN_TYPE"));
            chargeInfo.setRateIndicator(rs.getString("RATE_INDICATOR"));
            pickWeightBreak = rs.getString("WEIGHT_BREAK");
            pickRateType    = rs.getString("RATE_TYPE");
            
            if("BASE".equalsIgnoreCase(chargeInfo.getBreakPoint()) || "MIN".equalsIgnoreCase(chargeInfo.getBreakPoint()) || "MAX".equalsIgnoreCase(chargeInfo.getBreakPoint()) || ("SLAB".equalsIgnoreCase(pickWeightBreak) && "FLAT".equalsIgnoreCase(pickRateType))  || ("BOTH".equalsIgnoreCase(pickRateType) && "F".equalsIgnoreCase(chargeInfo.getRateIndicator())))
            {
              chargeInfo.setBasis("Per Shipment");
            }
            else
            {
              if("LIST".equalsIgnoreCase(rs.getString("WEIGHT_BREAK")))
              {
                chargeInfo.setBasis("Per Container");
              }
              else if("Per Kg".equalsIgnoreCase(rs.getString("CHARGEBASIS")))
              {
                chargeInfo.setBasis("Per Kilogram");
              }
              else if("Per Lb".equalsIgnoreCase(rs.getString("CHARGEBASIS")))
              {
                chargeInfo.setBasis("Per Pound");
              }
              else if("Per CFT".equalsIgnoreCase(rs.getString("CHARGEBASIS")))
              {
                chargeInfo.setBasis("Per Cubic Feet");
              }
              else if("Per CBM".equalsIgnoreCase(rs.getString("CHARGEBASIS")))
              {
                chargeInfo.setBasis("Per Cubic Meter");
              }
              else
                chargeInfo.setBasis(rs.getString("CHARGEBASIS"));
            }
            chargeInfo.setRatio(rs.getString("DENSITY_RATIO"));
            chargeInfo.setLineNumber(rs.getInt("LINE_NO"));
            chargeInfo.setSelectedFlag(rs.getString("SELECTED_FLAG"));
            if("BC".equalsIgnoreCase(flag)||"M".equalsIgnoreCase(cartageChargesDOB.getMarginDiscountFlag()) )
            {  
              chargeInfo.setMargin(rs.getDouble("MARGINVALUE"));
              chargeInfo.setTieMarginDiscountValue(rs.getDouble("MARGINVALUE"));
              chargeInfo.setMarginType(rs.getString("MARGIN_TYPE"));
              
                        if(rs.getDouble("MARGINVALUE")>0 || "Y".equalsIgnoreCase(rs.getString("SELECTED_FLAG")))
                        {
              if("A".equalsIgnoreCase(rs.getString("MARGIN_TYPE")))
                sellRate  = rs.getDouble("BUYRATE")+rs.getDouble("MARGINVALUE");
              else if("P".equalsIgnoreCase(rs.getString("MARGIN_TYPE")))
                sellRate  = rs.getDouble("BUYRATE")+(rs.getDouble("BUYRATE")*rs.getDouble("MARGINVALUE")/100);
            }
                        else
                          sellRate  = rs.getDouble("BUYRATE");
          //@@ Ended by subrahmanyam for the Enhancement 154381 on 28/01/09
            }
            else   if("SC".equalsIgnoreCase(flag))
            {
              chargeInfo.setDiscount(rs.getDouble("MARGINVALUE"));
              chargeInfo.setTieMarginDiscountValue(rs.getDouble("MARGINVALUE"));
              chargeInfo.setDiscountType(rs.getString("MARGIN_TYPE"));
              
              if("A".equalsIgnoreCase(rs.getString("MARGIN_TYPE")))
                sellRate  = rs.getDouble("SELLRATE")-rs.getDouble("MARGINVALUE");
              else if("P".equalsIgnoreCase(rs.getString("MARGIN_TYPE")))
                sellRate  = rs.getDouble("SELLRATE")-(rs.getDouble("SELLRATE")*rs.getDouble("MARGINVALUE")/100);
            }
          
            chargeInfo.setSellRate(sellRate);
            chargeInfo.setTieSellRateValue(sellRate);
                  
          }
          else
          {
           
            cartageChargesDOB  = new MultiQuoteCharges();
            
            //if it is a sell charge/rate
            if("SC".equalsIgnoreCase(flag))
            {
              cartageChargesDOB.setSellChargeId(rs.getString("SELLCHARGEID"));
              cartageChargesDOB.setBuyChargeId(rs.getString("BUY_CHARGE_ID"));
            }
            else if("BC".equalsIgnoreCase(flag))
            {
              cartageChargesDOB.setBuyChargeId(rs.getString("SELLCHARGEID"));
            }
                     
            cartageChargesDOB.setSellBuyFlag(flag);
            
            //cartageChargesDOB.setChargeDescriptionId("Pickup Charges");
          //Added By Kishore for multiple ZoneCodes
          //  Modified By Kishore For Codereview FOr 236286 CR
            cartageChargesDOB.setZoneCode(rs.getString("ZONE"));
            if(rs.getString("ZONE")!=null || !"".equalsIgnoreCase(rs.getString("ZONE")))
            	cartageChargesDOB.setChargeDescriptionId("Zone-"+rs.getString("ZONE")+" Pickup Charges");
            else
               cartageChargesDOB.setChargeDescriptionId("Pickup Charges");
            
            cartageChargesDOB.setCostIncurredAt(rs.getString("COST_INCURREDAT"));
            cartageChargesDOB.setTerminalId(rs.getString("TERMINALID"));
            cartageChargesDOB.setSelectedFlag(rs.getString("SELECTED_FLAG"));
            cartageChargesDOB.setMarginDiscountFlag(rs.getString("MARGIN_DISCOUNT_FLAG"));
            chargeInfoList  = new ArrayList();
            chargeInfo      = new MultiQuoteChargeInfo();
            chargeInfoList.add(chargeInfo);
            
            cartageChargesDOB.setChargeInfoList(chargeInfoList);
            
            chargeInfo.setBreakPoint(rs.getString("CHARGESLAB"));
            
            if(rs.getString("CURRENCY")!=null && rs.getString("CURRENCY").trim().length()!=0)
              chargeInfo.setCurrency(rs.getString("CURRENCY"));
            else
               chargeInfo.setCurrency(masterDOB.getTerminalCurrency());
               
            chargeInfo.setBuyRate(rs.getDouble("BUYRATE"));
            chargeInfo.setRecOrConSellRrate(rs.getDouble("SELLRATE"));
            chargeInfo.setSellChargeMargin(rs.getDouble("MARGINVALUE"));
            chargeInfo.setSellChargeMarginType(rs.getString("MARGIN_TYPE"));
            chargeInfo.setRateIndicator(rs.getString("RATE_INDICATOR"));
                 pickWeightBreak = rs.getString("WEIGHT_BREAK");
            pickRateType    = rs.getString("RATE_TYPE");
            if("BASE".equalsIgnoreCase(chargeInfo.getBreakPoint()) || "MIN".equalsIgnoreCase(chargeInfo.getBreakPoint()) || "MAX".equalsIgnoreCase(chargeInfo.getBreakPoint()) || ("SLAB".equalsIgnoreCase(pickWeightBreak) && "FLAT".equalsIgnoreCase(pickRateType)) || ("BOTH".equalsIgnoreCase(pickRateType) && "F".equalsIgnoreCase(chargeInfo.getRateIndicator())))
            {
              chargeInfo.setBasis("Per Shipment");
            }
            else
            {
              if("LIST".equalsIgnoreCase(rs.getString("WEIGHT_BREAK")))
              {
                chargeInfo.setBasis("Per Container");
              }
              else if("Per Kg".equalsIgnoreCase(rs.getString("CHARGEBASIS")))
              {
                chargeInfo.setBasis("Per Kilogram");
              }
              else if("Per Lb".equalsIgnoreCase(rs.getString("CHARGEBASIS")))
              {
                chargeInfo.setBasis("Per Pound");
              }
              else if("Per CFT".equalsIgnoreCase(rs.getString("CHARGEBASIS")))
              {
                chargeInfo.setBasis("Per Cubic Feet");
              }
              else if("Per CBM".equalsIgnoreCase(rs.getString("CHARGEBASIS")))
              {
                chargeInfo.setBasis("Per Cubic Meter");
              }
              else
                chargeInfo.setBasis(rs.getString("CHARGEBASIS"));
            }
            chargeInfo.setRatio(rs.getString("DENSITY_RATIO"));
            chargeInfo.setLineNumber(rs.getInt("LINE_NO"));
            chargeInfo.setSelectedFlag(rs.getString("SELECTED_FLAG"));
           // if("M".equalsIgnoreCase(cartageChargesDOB.getMarginDiscountFlag()) || cartageChargesDOB.getMarginDiscountFlag()==null)
            if("BC".equalsIgnoreCase(flag)||"M".equalsIgnoreCase(cartageChargesDOB.getMarginDiscountFlag()) )
            {  
              chargeInfo.setMargin(rs.getDouble("MARGINVALUE"));
              chargeInfo.setTieMarginDiscountValue(rs.getDouble("MARGINVALUE"));
              chargeInfo.setMarginType(rs.getString("MARGIN_TYPE"));
              
                      if(rs.getDouble("MARGINVALUE")>0 || "Y".equalsIgnoreCase(rs.getString("SELECTED_FLAG")))
                {
              if("A".equalsIgnoreCase(rs.getString("MARGIN_TYPE")))
                sellRate  = rs.getDouble("BUYRATE")+rs.getDouble("MARGINVALUE");
              else if("P".equalsIgnoreCase(rs.getString("MARGIN_TYPE")))
                sellRate  = rs.getDouble("BUYRATE")+(rs.getDouble("BUYRATE")*rs.getDouble("MARGINVALUE")/100);
            }
            else
                  sellRate  = rs.getDouble("BUYRATE");
//@@ Ended by subrahmanyam for the Enhancement 154381 on 28/01/09    
            }
            else
            {
              chargeInfo.setDiscount(rs.getDouble("MARGINVALUE"));
              chargeInfo.setTieMarginDiscountValue(rs.getDouble("MARGINVALUE"));
              chargeInfo.setDiscountType(rs.getString("MARGIN_TYPE"));
              
              if("A".equalsIgnoreCase(rs.getString("MARGIN_TYPE")))
                sellRate  = rs.getDouble("SELLRATE")-rs.getDouble("MARGINVALUE");
              else if("P".equalsIgnoreCase(rs.getString("MARGIN_TYPE")))
                sellRate  = rs.getDouble("SELLRATE")-(rs.getDouble("SELLRATE")*rs.getDouble("MARGINVALUE")/100);
            }
              
            chargeInfo.setSellRate(sellRate);
            chargeInfo.setTieSellRateValue(sellRate);
         // Added by kiran.v on 07/09/2011 for Wpbn Issue 266732
            if(rs.getString("margin_test_flag")!=null)
            {
              if(rs.getString("margin_test_flag").equals("Y"))
                chargeInfo.setMarginTestFailed(true);
              else 
                chargeInfo.setMarginTestFailed(false);
            }
            else
              chargeInfo.setMarginTestFailed(false);
           // if("Pickup".equalsIgnoreCase(rs.getString("COST_INCURREDAT")))
            //{
       
            
              if((masterDOB.getShipperZipCode()!=null && ( masterDOB.getShipperZipCode()[laneNo]!=null && masterDOB.getShipperZipCode()[laneNo].trim().length()!=0))||(masterDOB.getShipperZones()[laneNo]!=null))// && masterDOB.getShipperZones()[laneNo].indexOf(",")==-1))
              {
            	//Added By Kishore for multiple ZoneCodes
                originChargesList.add(cartageChargesDOB);  
              }
            //}            
          }
        }
        //Added By Kishore for multiple ZoneCodes
        if(masterDOB.getShipperZones()[laneNo].length()> 0)
        {//Written by Sanjay for Cartage Charges
          //get the pickup and delivery charges as seperate entities as required for the annexure
          HashMap charge = null;
       
          // Added by kiran.v on 07/09/2011 for Wpbn Issue 266732
          if(rs.getString("margin_test_flag")!=null)
          {
            if(rs.getString("margin_test_flag").equals("Y"))
              chargeInfo.setMarginTestFailed(true);
            else 
              chargeInfo.setMarginTestFailed(false);
          }
          else
            chargeInfo.setMarginTestFailed(false);
          if("Pickup".equalsIgnoreCase(rs.getString("COST_INCURREDAT")))
          {
            if(pickUpZoneCode.containsKey(rs.getString("ZONE")+rs.getString("SELLCHARGEID")))
            {
              if(rs.getString("CHARGESLAB")!=null)
              {
                 if("FLAT".equalsIgnoreCase(rs.getString("CHARGESLAB"))){
                      isPickupFlat  =  true;
                      pickupFlatChargeBasis=rs.getString("CHARGEBASIS");
                 }
                 
                 if("MAX".equalsIgnoreCase(rs.getString("CHARGESLAB"))){
                      isPickupMax   =  true;
                     pickupMaxChargeBasis = rs.getString("CHARGEBASIS");
                 }
                      
                 pickQuoteCartageRates = (MultiQuoteCartageRates)pickUpZoneCode.get(rs.getString("ZONE")+rs.getString("SELLCHARGEID"));
                 charge = pickQuoteCartageRates.getRates();
                 
                 if("SC".equalsIgnoreCase(flag))
                      charge.put(rs.getString("CHARGESLAB"),rs.getString("SELLRATE"));
                 else
                      charge.put(rs.getString("CHARGESLAB"),rs.getString("BUYRATE"));
                      
                 pickUpZoneCode.put(rs.getString("ZONE")+rs.getString("SELLCHARGEID"),pickQuoteCartageRates);  
              }
              addToPickupList = false;
            }
            else
            {
              pickQuoteCartageRates = new MultiQuoteCartageRates();
              pickQuoteCartageRates.setZone(rs.getString("ZONE"));
              pickQuoteCartageRates.setCurrency(rs.getString("CURRENCY"));
              pickQuoteCartageRates.setCartageId(rs.getString("SELLCHARGEID"));
              pickWeightBreak = rs.getString("WEIGHT_BREAK");
              charge = new HashMap();
              if(rs.getString("CHARGESLAB")!=null)
              {
                  if("MIN".equalsIgnoreCase(rs.getString("CHARGESLAB"))){
                      isPickupMin   =  true;                 
                      pickupMinChargeBasis = rs.getString("CHARGEBASIS");
                  }
                  if("SC".equalsIgnoreCase(flag))
                      charge.put(rs.getString("CHARGESLAB"),rs.getString("SELLRATE"));
                  else
                      charge.put(rs.getString("CHARGESLAB"),rs.getString("BUYRATE"));
                  //charge.put(rs.getString("CHARGESLAB"),rs.getString("SELLRATE"));
                  pickQuoteCartageRates.setRates(charge);
                  pickUpZoneCode.put(rs.getString("ZONE")+rs.getString("SELLCHARGEID"),pickQuoteCartageRates);
              }
              addToPickupList = true;
            }
           
            if(addToPickupList)
              pickUpQuoteCartageRates.add(pickQuoteCartageRates);
          }                     
        }//End
      }
      if("Delivery".equalsIgnoreCase(rs.getString("COST_INCURREDAT")))
      {
       flag = rs.getString("SEL_BUY_FLAG");
      
       if((masterDOB.getConsigneeZipCode()!=null &&  masterDOB.getConsigneeZipCode()[laneNo]!=null && masterDOB.getConsigneeZipCode()[laneNo].trim().length()!=0)
            ||(masterDOB.getConsigneeZones()!=null ))//&& masterDOB.getConsigneeZones()[laneNo].indexOf(",")==-1))
    	   //Commented By Kishore for multiple ZoneCodes
        {
          
          if(destChargesList==null)
          {
            destChargesList = new ArrayList();
          }
          
         
    
           if(cartageDelChargesDOB!=null 
        		   && (rs.getString("SELLCHARGEID").equalsIgnoreCase(cartageDelChargesDOB.getSellChargeId()) || rs.getString("SELLCHARGEID").equalsIgnoreCase(cartageDelChargesDOB.getBuyChargeId()))
        		    && (rs.getString("ZONE")!=null && rs.getString("ZONE").equalsIgnoreCase(cartageDelChargesDOB.getZoneCode()))
             )
          {
            chargeInfo  = new MultiQuoteChargeInfo();
            
            chargeInfoList.add(chargeInfo);
            
            chargeInfo.setBreakPoint(rs.getString("CHARGESLAB"));
            if(rs.getString("CURRENCY")!=null && rs.getString("CURRENCY").trim().length()!=0)
              chargeInfo.setCurrency(rs.getString("CURRENCY"));
            else
               chargeInfo.setCurrency(masterDOB.getTerminalCurrency());
               
            chargeInfo.setBuyRate(rs.getDouble("BUYRATE"));
            chargeInfo.setRecOrConSellRrate(rs.getDouble("SELLRATE"));
            chargeInfo.setSellChargeMargin(rs.getDouble("MARGINVALUE"));
            chargeInfo.setSellChargeMarginType(rs.getString("MARGIN_TYPE"));
            chargeInfo.setRateIndicator(rs.getString("RATE_INDICATOR"));
            delWeightBreak   =  rs.getString("WEIGHT_BREAK");
            delRateType      =  rs.getString("RATE_TYPE");
            //chargeInfo.setMargin(rs.getDouble("MARGINVALUE"));
           // chargeInfo.setMarginType(rs.getString("MARGIN_TYPE"));
            //chargeInfo.setBasis(rs.getString("CHARGEBASIS"));
            if("BASE".equalsIgnoreCase(chargeInfo.getBreakPoint()) || "MIN".equalsIgnoreCase(chargeInfo.getBreakPoint()) || "MAX".equalsIgnoreCase(chargeInfo.getBreakPoint()) || ("SLAB".equalsIgnoreCase(delWeightBreak) && "FLAT".equalsIgnoreCase(delRateType))  || ("BOTH".equalsIgnoreCase(delRateType) && "F".equalsIgnoreCase(chargeInfo.getRateIndicator())))
            {
              chargeInfo.setBasis("Per Shipment");
            }
            else
            {
              if("LIST".equalsIgnoreCase(rs.getString("WEIGHT_BREAK")))
              {
                chargeInfo.setBasis("Per Container");
              }
              else if("Per Kg".equalsIgnoreCase(rs.getString("CHARGEBASIS")))
              {
                chargeInfo.setBasis("Per Kilogram");
              }
              else if("Per Lb".equalsIgnoreCase(rs.getString("CHARGEBASIS")))
              {
                chargeInfo.setBasis("Per Pound");
              }
              else if("Per CFT".equalsIgnoreCase(rs.getString("CHARGEBASIS")))
              {
                chargeInfo.setBasis("Per Cubic Feet");
              }
              else if("Per CBM".equalsIgnoreCase(rs.getString("CHARGEBASIS")))
              {
                chargeInfo.setBasis("Per Cubic Meter");
              }
              else
                chargeInfo.setBasis(rs.getString("CHARGEBASIS"));
            }
            chargeInfo.setRatio(rs.getString("DENSITY_RATIO"));
            chargeInfo.setLineNumber(rs.getInt("LINE_NO"));
            chargeInfo.setSelectedFlag(rs.getString("SELECTED_FLAG"));
            if("BC".equalsIgnoreCase(flag)||"M".equalsIgnoreCase(cartageDelChargesDOB.getMarginDiscountFlag()))
            {  
              chargeInfo.setMargin(rs.getDouble("MARGINVALUE"));
              chargeInfo.setTieMarginDiscountValue(rs.getDouble("MARGINVALUE"));
              chargeInfo.setMarginType(rs.getString("MARGIN_TYPE"));
              
                if(rs.getDouble("MARGINVALUE")>0 || "Y".equalsIgnoreCase(rs.getString("SELECTED_FLAG")))
                {
              if("A".equalsIgnoreCase(rs.getString("MARGIN_TYPE")))
                sellRate  = rs.getDouble("BUYRATE")+rs.getDouble("MARGINVALUE");
              else if("P".equalsIgnoreCase(rs.getString("MARGIN_TYPE")))
                sellRate  = rs.getDouble("BUYRATE")+(rs.getDouble("BUYRATE")*rs.getDouble("MARGINVALUE")/100);
              else if(rs.getString("MARGIN_TYPE")== null || "".equals(rs.getString("MARGIN_TYPE")))
            	  sellRate  = rs.getDouble("BUYRATE");  
            }
            else
                  sellRate  = rs.getDouble("BUYRATE");
//@@ Ended by subrahmanyam for the Enhancement 154381 on 30/01/09  
            }
            else
            {
              chargeInfo.setDiscount(rs.getDouble("MARGINVALUE"));
              chargeInfo.setTieMarginDiscountValue(rs.getDouble("MARGINVALUE"));
              chargeInfo.setDiscountType(rs.getString("MARGIN_TYPE"));
              
              if("A".equalsIgnoreCase(rs.getString("MARGIN_TYPE")))
                sellRate  = rs.getDouble("SELLRATE")-rs.getDouble("MARGINVALUE");
              else if("P".equalsIgnoreCase(rs.getString("MARGIN_TYPE")))
                sellRate  = rs.getDouble("SELLRATE")-(rs.getDouble("SELLRATE")*rs.getDouble("MARGINVALUE")/100);
              else if(rs.getString("MARGIN_TYPE")== null || "".equals(rs.getString("MARGIN_TYPE")))
            	  sellRate  = rs.getDouble("SELLRATE");  
            }
          
            chargeInfo.setSellRate(sellRate);
            chargeInfo.setTieSellRateValue(sellRate);
            /*if("A".equalsIgnoreCase(rs.getString("MARGIN_TYPE")))
              sellRate  = rs.getDouble("BUYRATE")+rs.getDouble("MARGINVALUE");
            else if("P".equalsIgnoreCase(rs.getString("MARGIN_TYPE")))
              sellRate  = rs.getDouble("BUYRATE")+(rs.getDouble("BUYRATE")*rs.getDouble("MARGINVALUE")/100);*/
              
          }
          else
          {
            cartageDelChargesDOB  = new MultiQuoteCharges();
            
            //if it is a sell charge/rate
            if("SC".equalsIgnoreCase(flag))
            {
              cartageDelChargesDOB.setSellChargeId(rs.getString("SELLCHARGEID"));
              cartageDelChargesDOB.setBuyChargeId(rs.getString("BUY_CHARGE_ID"));
            }
            else if("BC".equalsIgnoreCase(flag))
            {
              cartageDelChargesDOB.setBuyChargeId(rs.getString("SELLCHARGEID"));
            }
            
                      
            cartageDelChargesDOB.setSellBuyFlag(flag);
            
            //cartageDelChargesDOB.setChargeDescriptionId("Delivery Charges");
          //Added By Kishore for multiple ZoneCodes
            cartageDelChargesDOB.setZoneCode(rs.getString("ZONE"));
            if(rs.getString("ZONE")!=null || !"".equalsIgnoreCase(rs.getString("ZONE")))
            	cartageDelChargesDOB.setChargeDescriptionId("Zone-"+rs.getString("ZONE")+" Delivery Charges");
            else
                cartageDelChargesDOB.setChargeDescriptionId("Delivery Charges");
            
            cartageDelChargesDOB.setCostIncurredAt(rs.getString("COST_INCURREDAT"));
            cartageDelChargesDOB.setTerminalId(rs.getString("TERMINALID"));
            cartageDelChargesDOB.setSelectedFlag(rs.getString("SELECTED_FLAG"));
            cartageDelChargesDOB.setMarginDiscountFlag(rs.getString("MARGIN_DISCOUNT_FLAG"));
            chargeInfoList  = new ArrayList();
            chargeInfo  = new MultiQuoteChargeInfo();
            chargeInfoList.add(chargeInfo);
           cartageDelChargesDOB.setChargeInfoList(chargeInfoList);
            
            chargeInfo.setBreakPoint(rs.getString("CHARGESLAB"));
             
            if(rs.getString("CURRENCY")!=null && rs.getString("CURRENCY").trim().length()!=0)
              chargeInfo.setCurrency(rs.getString("CURRENCY"));
            else
               chargeInfo.setCurrency(masterDOB.getTerminalCurrency());
               
            chargeInfo.setBuyRate(rs.getDouble("BUYRATE"));
          
            chargeInfo.setRecOrConSellRrate(rs.getDouble("SELLRATE"));
            chargeInfo.setSellChargeMargin(rs.getDouble("MARGINVALUE"));
            chargeInfo.setSellChargeMarginType(rs.getString("MARGIN_TYPE"));
            chargeInfo.setRateIndicator(rs.getString("RATE_INDICATOR"));
            delWeightBreak   =  rs.getString("WEIGHT_BREAK");
            delRateType      =  rs.getString("RATE_TYPE");
           // chargeInfo.setMargin(rs.getDouble("MARGINVALUE"));
          //  chargeInfo.setMarginType(rs.getString("MARGIN_TYPE"));
            //chargeInfo.setBasis(rs.getString("CHARGEBASIS"));
            if("BASE".equalsIgnoreCase(chargeInfo.getBreakPoint()) || "MIN".equalsIgnoreCase(chargeInfo.getBreakPoint()) || "MAX".equalsIgnoreCase(chargeInfo.getBreakPoint()) || ("SLAB".equalsIgnoreCase(delWeightBreak) && "FLAT".equalsIgnoreCase(delRateType)) || ("BOTH".equalsIgnoreCase(delRateType) && "F".equalsIgnoreCase(chargeInfo.getRateIndicator())))
            {
              chargeInfo.setBasis("Per Shipment");
            }
            else
            {
              if("LIST".equalsIgnoreCase(rs.getString("WEIGHT_BREAK")))
              {
                chargeInfo.setBasis("Per Container");
              }
              else if("Per Kg".equalsIgnoreCase(rs.getString("CHARGEBASIS")))
              {
                chargeInfo.setBasis("Per Kilogram");
              }
              else if("Per Lb".equalsIgnoreCase(rs.getString("CHARGEBASIS")))
              {
                chargeInfo.setBasis("Per Pound");
              }
              else if("Per CFT".equalsIgnoreCase(rs.getString("CHARGEBASIS")))
              {
                chargeInfo.setBasis("Per Cubic Feet");
              }
              else if("Per CBM".equalsIgnoreCase(rs.getString("CHARGEBASIS")))
              {
                chargeInfo.setBasis("Per Cubic Meter");
              }
              else
                chargeInfo.setBasis(rs.getString("CHARGEBASIS"));
            }
            chargeInfo.setRatio(rs.getString("DENSITY_RATIO"));
            chargeInfo.setLineNumber(rs.getInt("LINE_NO"));
            chargeInfo.setSelectedFlag(rs.getString("SELECTED_FLAG"));

           // if("M".equalsIgnoreCase(cartageDelChargesDOB.getMarginDiscountFlag()) || cartageDelChargesDOB.getMarginDiscountFlag()==null)
            if("BC".equalsIgnoreCase(flag)||"M".equalsIgnoreCase(cartageDelChargesDOB.getMarginDiscountFlag()))
            {   
              chargeInfo.setMargin(rs.getDouble("MARGINVALUE"));
              chargeInfo.setTieMarginDiscountValue(rs.getDouble("MARGINVALUE"));              
              chargeInfo.setMarginType(rs.getString("MARGIN_TYPE"));
              
            //@@ Commented by subramanyam for the enhancement 154381 on 05/02/09              
              /*if("A".equalsIgnoreCase(rs.getString("MARGIN_TYPE")))
                sellRate  = rs.getDouble("BUYRATE")+rs.getDouble("MARGINVALUE");
              else if("P".equalsIgnoreCase(rs.getString("MARGIN_TYPE")))
                sellRate  = rs.getDouble("BUYRATE")+(rs.getDouble("BUYRATE")*rs.getDouble("MARGINVALUE")/100);*/
//@@ Added by subrahmanyam for the Enhancement 154381 on 15/02/09             
              if(rs.getDouble("MARGINVALUE")>0 || "Y".equalsIgnoreCase(rs.getString("SELECTED_FLAG")))
              {
              if("A".equalsIgnoreCase(rs.getString("MARGIN_TYPE")))
                sellRate  = rs.getDouble("BUYRATE")+rs.getDouble("MARGINVALUE");
              else if("P".equalsIgnoreCase(rs.getString("MARGIN_TYPE")))
                sellRate  = rs.getDouble("BUYRATE")+(rs.getDouble("BUYRATE")*rs.getDouble("MARGINVALUE")/100);
              else if(rs.getString("MARGIN_TYPE")== null || "".equals(rs.getString("MARGIN_TYPE")))
            	  sellRate  = rs.getDouble("BUYRATE");  
            }
            else
                 sellRate = rs.getDouble("BUYRATE");
//@@ Ended by subrahmanyam for the Enhancement 154381 on 15/02/09 
            }
            else
            {
              chargeInfo.setDiscount(rs.getDouble("MARGINVALUE"));
              chargeInfo.setTieMarginDiscountValue(rs.getDouble("MARGINVALUE"));             
              chargeInfo.setDiscountType(rs.getString("MARGIN_TYPE"));
              
              if("A".equalsIgnoreCase(rs.getString("MARGIN_TYPE")))
                sellRate  = rs.getDouble("SELLRATE")-rs.getDouble("MARGINVALUE");
              else if("P".equalsIgnoreCase(rs.getString("MARGIN_TYPE")))
                sellRate  = rs.getDouble("SELLRATE")-(rs.getDouble("SELLRATE")*rs.getDouble("MARGINVALUE")/100);
              else if(rs.getString("MARGIN_TYPE")== null || "".equals(rs.getString("MARGIN_TYPE")))
            	  sellRate  = rs.getDouble("SELLRATE");  
            }
              
            chargeInfo.setSellRate(sellRate);
            chargeInfo.setTieSellRateValue(sellRate);           
            // Added by kiran.v on 07/09/2011 for Wpbn Issue 266732
            if(rs.getString("margin_test_flag")!=null)
            {
              if("y".equalsIgnoreCase(rs.getString("margin_test_flag")))
              chargeInfo.setMarginTestFailed(true);
              else 
              chargeInfo.setMarginTestFailed(false);
            }
            else
              chargeInfo.setMarginTestFailed(false);
            
            //if("Delivery".equalsIgnoreCase(rs.getString("COST_INCURREDAT")))
            //{
             
            
              if((masterDOB.getConsigneeZipCode()!=null && (masterDOB.getConsigneeZipCode()[laneNo]!=null &&  masterDOB.getConsigneeZipCode()[laneNo].trim().length()!=0))||(masterDOB.getConsigneeZones()!=null )) //&& masterDOB.getConsigneeZones()[laneNo].indexOf(",")==-1))
              {
            //Added By Kishore for multiple ZoneCodes
               destChargesList.add(cartageDelChargesDOB);  
              }
            //}
          }   
        }
     //Added By Kishore for multiple ZoneCodes
       if( masterDOB.getConsigneeZones()[laneNo].length()>0)
        {//Written by Sanjay for Cartage Charges
          //get the pickup and delivery charges as seperate entities as required for the annexure
          HashMap charge = null;
          // Added by kiran.v on 07/09/2011 for Wpbn Issue 266732
          if(rs.getString("margin_test_flag")!=null)
          {
            if(rs.getString("margin_test_flag").equals("Y"))
              chargeInfo.setMarginTestFailed(true);
            else 
              chargeInfo.setMarginTestFailed(false);
          }
          else
            chargeInfo.setMarginTestFailed(false);
          if("Delivery".equalsIgnoreCase(rs.getString("COST_INCURREDAT")))
          {
            if(deliveryZoneCode.containsKey(rs.getString("ZONE")+rs.getString("SELLCHARGEID")))
            {
              if(rs.getString("CHARGESLAB")!=null)
              {
                 //Modified By Kishore For the ChargeBasis in the Annexure PDF on 27-May-11
                 if("FLAT".equalsIgnoreCase(rs.getString("CHARGESLAB"))){
                      isDeliveryFlat  =  true;
                      deliveryFlatChargeBasis = rs.getString("CHARGEBASIS");
                 }
                 if("MAX".equalsIgnoreCase(rs.getString("CHARGESLAB"))){
                      isDeliveryMax  =  true;
                      deliveryMaxChargeBasis =  rs.getString("CHARGESLAB");
                 }
                 //End Of: Modified By Kishore For the ChargeBasis in the Annexure PDF on 27-May-11
                 
                 delQuoteCartageRates = (MultiQuoteCartageRates)deliveryZoneCode.get(rs.getString("ZONE")+rs.getString("SELLCHARGEID"));
                 charge = delQuoteCartageRates.getRates();
                //System.out.println("==rs.getString(marginvalue)=="+rs.getString("marginvalue"));
           	    // System.out.println("====type===="+rs.getString("margin_type"));
                 if("SC".equalsIgnoreCase(flag))
                      charge.put(rs.getString("CHARGESLAB"),rs.getString("SELLRATE"));
                  else{
                	  if("P".equalsIgnoreCase(rs.getString("margin_type")))
                		  sellRate  = rs.getDouble("BUYRATE")+(rs.getDouble("BUYRATE")*rs.getDouble("MARGINVALUE")/100);
                	  else if("A".equalsIgnoreCase(rs.getString("margin_type"))) 
                		  sellRate  = rs.getDouble("BUYRATE")+rs.getDouble("MARGINVALUE");  
                	  else
                		  sellRate  = rs.getDouble("BUYRATE");
                	  charge.put(rs.getString("CHARGESLAB"),Double.toString(sellRate));
                  }
                 //charge.put(rs.getString("CHARGESLAB"),rs.getString("SELLRATE"));
                 deliveryZoneCode.put(rs.getString("ZONE")+rs.getString("SELLCHARGEID"),delQuoteCartageRates);
              }
              addToDeliveryList = false;
            }
            else
            {
              delQuoteCartageRates = new MultiQuoteCartageRates();
              delWeightBreak       =  rs.getString("WEIGHT_BREAK");
              delQuoteCartageRates.setZone(rs.getString("ZONE"));
              delQuoteCartageRates.setCurrency(rs.getString("CURRENCY"));
              delQuoteCartageRates.setCartageId(rs.getString("SELLCHARGEID"));               
              charge = new HashMap();
              if(rs.getString("CHARGESLAB")!=null)
              {
            	//Modified By Kishore For the ChargeBasis in the Annexure PDF on 27-May-11
                if("MIN".equalsIgnoreCase(rs.getString("CHARGESLAB"))){
                    isDeliveryMin   =  true;                 
                    deliveryMinChargeBasis = rs.getString("CHARGEBASIS");
                }
                
                 if("SC".equalsIgnoreCase(flag))
                      charge.put(rs.getString("CHARGESLAB"),rs.getString("SELLRATE"));
                  else{
                	  if("P".equalsIgnoreCase(rs.getString("margin_type"))&& !"0".equalsIgnoreCase(rs.getString("marginvalue")))
                		  sellRate  = rs.getDouble("BUYRATE")+(rs.getDouble("BUYRATE")*rs.getDouble("MARGINVALUE")/100);
                	  else if("A".equalsIgnoreCase(rs.getString("margin_type"))) 
                		  sellRate  = rs.getDouble("BUYRATE")+rs.getDouble("MARGINVALUE"); 
                	  else
                		  sellRate  = rs.getDouble("BUYRATE");
                      charge.put(rs.getString("CHARGESLAB"),Double.toString(sellRate));
                  }
                 //charge.put(rs.getString("CHARGESLAB"),rs.getString("SELLRATE"));
                 delQuoteCartageRates.setRates(charge);        
                 deliveryZoneCode.put(rs.getString("ZONE")+rs.getString("SELLCHARGEID"),delQuoteCartageRates);
              }
              addToDeliveryList = true;
            }
              if(addToDeliveryList)
                  deliveryQuoteCartageRates.add(delQuoteCartageRates);
          }           
        }//End
      }
      }      
      
      if(originChargesList!=null || destChargesList!=null)
      {
        if(originChargesList!=null)
          legDOB.setOriginChargesList(originChargesList);
        if(destChargesList!=null)
          legDOB.setDestChargesList(destChargesList);
      }
      /*if(deliveryZoneCode.values()!=null)
      {
        deliveryQuoteCartageRates = new ArrayList();
        deliveryQuoteCartageRates.addAll(deliveryZoneCode.values());
      }
      if(pickUpZoneCode.values()!=null)
      {
        pickUpQuoteCartageRates = new ArrayList();
        pickUpQuoteCartageRates.addAll(pickUpZoneCode.values());
      }*/
      if(deliveryQuoteCartageRates!=null && deliveryQuoteCartageRates.size()>0)
      {
        //delWeightBreaksRS   =   (ResultSet)csmt.getObject(23);
        
    	 //Modified By Kishore For the ChargeBasis in the Annexure PDF on 27-May-11
        if(isDeliveryMin){
          delWeightBreaksList.add("MIN");
          delChargeBasisList.add(deliveryMinChargeBasis);
        }
        
        if(isDeliveryFlat){
            delWeightBreaksList.add("FLAT");
            delChargeBasisList.add(deliveryFlatChargeBasis);
        }
        while(delWeightBreaksRS.next())
        {
          delWeightBreaksList.add(delWeightBreaksRS.getString("CHARGESLAB"));
          delChargeBasisList.add(delWeightBreaksRS.getString("CHARGEBASIS"));
        }
        
        //delWeightBreaksRSList = (ResultSet)csmt.getObject(24);
        
        /*while(delWeightBreaksRSList.next())
        {
          delWeightBreaksList.add(delWeightBreaksRSList.getString("CHARGESLAB"));
        }*/
        
        if(isDeliveryMax){
            delWeightBreaksList.add("MAX");
            delChargeBasisList.add(deliveryMaxChargeBasis);
        }
     
        legDOB.setDeliveryCartageRatesList(deliveryQuoteCartageRates);
        legDOB.setDeliveryWeightBreaks(delWeightBreaksList);
        legDOB.setDelChargeBasisList(delChargeBasisList);
      }
      if(pickUpQuoteCartageRates!=null && pickUpQuoteCartageRates.size()>0)
      {
       // pickWeightBreaksRS          = (ResultSet)csmt.getObject(21);
        //pickWeightBreaksRSList  = 
          //Modified By Kishore For the ChargeBasis in the Annexure PDF on 27-May-11
    	  if(isPickupMin){
          pickupWeightBreaksList.add("MIN");
          pickupChargeBasisList.add(pickupMinChargeBasis);
        }
        
        if(isPickupFlat){
          pickupWeightBreaksList.add("FLAT");
          pickupChargeBasisList.add(pickupFlatChargeBasis);
        }
        
        while(pickWeightBreaksRS.next())
        {
          pickupWeightBreaksList.add(pickWeightBreaksRS.getString("CHARGESLAB"));
          pickupChargeBasisList.add(pickWeightBreaksRS.getString("CHARGEBASIS"));
        }
        
        //pickWeightBreaksRSList  = (ResultSet)csmt.getObject(22);
        
        /*while(pickWeightBreaksRSList.next())
        {
          pickupWeightBreaksList.add(pickWeightBreaksRSList.getString("CHARGESLAB"));
        }*/
        
       if(isPickupMax){
          pickupWeightBreaksList.add("MAX");
          pickupChargeBasisList.add(pickupMaxChargeBasis);
        }
       
     //End of: Modified By Kishore For the ChargeBasis in the Annexure PDF on 27-May-11
        
      
        
        legDOB.setPickUpCartageRatesList(pickUpQuoteCartageRates);
        legDOB.setPickupWeightBreaks(pickupWeightBreaksList);
        legDOB.setPickupChargeBasisList(pickupChargeBasisList); //Added By Kishore For the ChargeBasis in the Annexure PDF on 27-May-11
      }
        
      //The below code is to get the ZONE-ZIPCODE Mapping from the CURSOR from 20 OUTPARAMETER
      
        zoneRs  = (ResultSet)csmt.getObject(21);
        ArrayList delivZipCodes  = null;
        ArrayList pickUpZipCodes = null;
            
        while(zoneRs.next())
        {
          if("Pickup".equalsIgnoreCase(zoneRs.getString("charge_type")))
          {
           // if( !((masterDOB.getShipperZipCode()!=null && masterDOB.getShipperZipCode()[laneNo].trim().length()!=0)))
          //  {
              String from_toZip = null;
              if(pickUpZoneZipMap.containsKey(zoneRs.getString("ZONE")))
              {             
                pickUpZipCodes = (ArrayList) pickUpZoneZipMap.get(zoneRs.getString("ZONE"));
                if(zoneRs.getString("ALPHANUMERIC")!=null)
                 from_toZip = zoneRs.getString("ALPHANUMERIC")+zoneRs.getString("from_zipcode")+" - "+zoneRs.getString("ALPHANUMERIC")+zoneRs.getString("to_zipcode");
                else
                 from_toZip = zoneRs.getString("from_zipcode")+" - "+zoneRs.getString("to_zipcode");
                if(from_toZip!=null)
                  pickUpZipCodes.add(from_toZip);
                pickUpZoneZipMap.put(zoneRs.getString("ZONE"),pickUpZipCodes);
              }
              else
              {
                pickUpZipCodes = new ArrayList();
                if(zoneRs.getString("from_zipcode")!=null)
                {
                  if(zoneRs.getString("ALPHANUMERIC")!=null)
                   from_toZip = zoneRs.getString("ALPHANUMERIC")+zoneRs.getString("from_zipcode")+" - "+zoneRs.getString("ALPHANUMERIC")+zoneRs.getString("to_zipcode");
                  else
                   from_toZip = zoneRs.getString("from_zipcode")+" - "+zoneRs.getString("to_zipcode");
                  if(from_toZip!=null)
                    pickUpZipCodes.add(from_toZip);
                  pickUpZoneZipMap.put(zoneRs.getString("ZONE"),pickUpZipCodes);
                }
              }
            //}
          }
          else if("Delivery".equalsIgnoreCase(zoneRs.getString("charge_type")))
          {
           // if(!((masterDOB.getConsigneeZipCode()!=null && masterDOB.getConsigneeZipCode()[laneNo].trim().length()!=0)))
            //{
              String from_toZip = null;
              if(deliveryZoneZipMap.containsKey(zoneRs.getString("ZONE")))
              {
                delivZipCodes = (ArrayList) deliveryZoneZipMap.get(zoneRs.getString("ZONE"));
                if(zoneRs.getString("ALPHANUMERIC")!=null)
                 from_toZip = zoneRs.getString("ALPHANUMERIC")+zoneRs.getString("from_zipcode")+" - "+zoneRs.getString("ALPHANUMERIC")+zoneRs.getString("to_zipcode");
                else
                 from_toZip = zoneRs.getString("from_zipcode")+" - "+zoneRs.getString("to_zipcode");
                if(from_toZip!=null)
                  delivZipCodes.add(from_toZip);
                deliveryZoneZipMap.put(zoneRs.getString("ZONE"),delivZipCodes);
              }
              else
              { 
                delivZipCodes = new ArrayList();
                if(zoneRs.getString("ALPHANUMERIC")!=null)
                 from_toZip = zoneRs.getString("ALPHANUMERIC")+zoneRs.getString("from_zipcode")+" - "+zoneRs.getString("ALPHANUMERIC")+zoneRs.getString("to_zipcode");
                else
                 from_toZip = zoneRs.getString("from_zipcode")+" - "+zoneRs.getString("to_zipcode");
                if(from_toZip!=null)
                  delivZipCodes.add(from_toZip);
                deliveryZoneZipMap.put(zoneRs.getString("ZONE"),delivZipCodes);
              }
            }
          //}          
        }
        legDOB.setPickZoneZipMap(pickUpZoneZipMap);
        legDOB.setDeliveryZoneZipMap(deliveryZoneZipMap);
      //End
        finalLegDOB.add(legDOB);
    }else{
    	finalLegDOB.add(legDOB);
    }
      csmt.clearParameters();
    }
      if(finalLegDOB != null && finalLegDOB.size()>0)
      finalDOB.setLegDetails(finalLegDOB);
    }
    catch(SQLException sqEx)
		{
      sqEx.printStackTrace();
			//Logger.error(FILE_NAME,"QMSQuoteDAO[getLegCharges(masterDOB,sellBuyRate,sellBuyFlag,laneNo,origin,dest)] -> "+sqEx.toString());
      logger.error(FILE_NAME+"QMSQuoteDAO[getCartages(masterDOB,sellBuyRate,sellBuyFlag,laneNo,origin,dest)] -> "+sqEx.toString());
      throw new SQLException(sqEx.toString());
		}
    catch(Exception e)
		{
      e.printStackTrace();
			//Logger.error(FILE_NAME,"QMSQuoteDAO[getLegCharges(masterDOB,sellBuyRate,sellBuyFlag,laneNo,origin,dest)] -> "+e.toString());
      logger.error(FILE_NAME+"QMSQuoteDAO[getCartages(masterDOB,sellBuyRate,sellBuyFlag,laneNo,origin,dest)] -> "+e.toString());
      throw new SQLException(e.toString());
		}
		finally
		{
			try
			{				
        if(zoneRs!=null)
            zoneRs.close();
        if(pickWeightBreaksRS!=null)
          pickWeightBreaksRS.close();
        if(delWeightBreaksRS!=null)
          delWeightBreaksRS.close();
        if(rs!=null)
          rs.close();
        if(csmt!=null)
          csmt.close();
        if(connection!=null)
          connection.close();
        //ConnectionUtil.closeConnection(connection,csmt,rs);
			}
			catch(Exception ex)
			{
				//Logger.error(FILE_NAME,"Finally : QMSQuoteDAO[getLegCharges(masterDOB,sellBuyRate,sellBuyFlag,laneNo,origin,dest)]-> "+ex.toString());
        logger.error(FILE_NAME+"Finally : QMSQuoteDAO[getLegCharges(masterDOB,sellBuyRate,sellBuyFlag,laneNo,origin,dest)]-> "+ex.toString());
        throw new SQLException(ex.toString());
			}
		}
    return finalDOB;
    }

public MultiQuoteFreightLegSellRates getMultiQuoteCartages(MultiQuoteFinalDOB finalDOB,int laneNo) throws SQLException
{
    Connection                connection              = null;
    CallableStatement         csmt                    = null;
    ResultSet                 rs                      = null;
    ResultSet                 zoneRs                  = null;
    ResultSet                 pickWeightBreaksRS      = null;
    ResultSet                 delWeightBreaksRS       = null;
    /*ResultSet                 pickWeightBreaksRSList  = null;
    ResultSet                 delWeightBreaksRSList   = null;*/
    // Added By Kishore Podili For Multi Zone Codes    
    ArrayList                 originChargesList       = null;//to maintain the list of  all origin charge dobs
    ArrayList                 destChargesList         = null;//to maintain the list of  all origin charge dobs
    MultiQuoteCharges              cartageChargesDOB       = null;//to maintain one record that is to be displayed
    MultiQuoteCharges              cartageDelChargesDOB    = null;//to maintain one record that is to be displayed
    MultiQuoteChargeInfo           chargeInfo              = null;
    ArrayList                 chargeInfoList          = null;
    String                    flag                    = null;
    String                    pickWeightBreak         = null;
    String                    pickRateType            = null;
    String                    delWeightBreak          = null;
    String                    delRateType             = null;
    MultiQuoteMasterDOB            masterDOB               = null;
    double                    sellRate                = 0;
    ArrayList                 pickUpQuoteCartageRates   = new ArrayList();
    ArrayList                 deliveryQuoteCartageRates = new ArrayList();
    ArrayList                 pickupWeightBreaksList    = new ArrayList();
    ArrayList                 delWeightBreaksList       = new ArrayList();
    HashMap                   pickUpZoneZipMap          = new HashMap();//Added by Sanjay
    HashMap                   deliveryZoneZipMap        = new HashMap();//Added by Sanjay
    HashMap                   pickUpZoneCode            = new HashMap();//Added by Sanjay
    HashMap                   deliveryZoneCode          = new HashMap();//Added by Sanjay
    MultiQuoteCartageRates         pickQuoteCartageRates     = null;//Added by Sanjay
    MultiQuoteCartageRates         delQuoteCartageRates      = null;//Added by Sanjay
    boolean                   addToPickupList           = true;
    boolean                   addToDeliveryList         = true;
    boolean                   isPickupMin               = false;
    boolean                   isPickupFlat              = false;
    boolean                   isPickupMax               = false;
    boolean                   isDeliveryMin             = false;
    boolean                   isDeliveryFlat            = false;
    boolean                   isDeliveryMax             = false;
	ArrayList					legDetails					= null;
    MultiQuoteFreightLegSellRates   legDOB                  = null;
    //Added By Kishore For the ChargeBasis in the Annexure PDF on 27-May-11
    ArrayList                 pickupChargeBasisList    = new ArrayList();
    ArrayList                 delChargeBasisList       = new ArrayList();
    String					  pickupMinChargeBasis	   = "";
    String					  pickupFlatChargeBasis	   = "";
    String					  pickupMaxChargeBasis	   = "";
    String					  deliveryFlatChargeBasis  = "";
    String					  deliveryMinChargeBasis   = "";
    String					  deliveryMaxChargeBasis   = "";
    /*ArrayList				  pickupChargeBasisListForZone = null;
    ArrayList				  delChargeBasisListForZone = null;*/
    
    
    try
    {
      //@@for re-initialising all the objects put in session previously.
      legDetails = finalDOB.getLegDetails();
      
      //Added By Kishore Podili
      //legDOB = (MultiQuoteFreightLegSellRates)legDetails.get(laneNo);
      if(legDetails == null)
    	  legDOB = new MultiQuoteFreightLegSellRates();
      else
      legDOB = (MultiQuoteFreightLegSellRates)legDetails.get(laneNo);

      legDOB.setPickUpCartageRatesList(pickUpQuoteCartageRates);
      legDOB.setDeliveryCartageRatesList(deliveryQuoteCartageRates); 
      legDOB.setPickupWeightBreaks(pickupWeightBreaksList);
      legDOB.setDeliveryWeightBreaks(delWeightBreaksList);
      legDOB.setPickZoneZipMap(pickUpZoneZipMap);
      legDOB.setDeliveryZoneZipMap(deliveryZoneZipMap);
      //@@
      
      masterDOB = finalDOB.getMasterDOB();
      long start = System.currentTimeMillis();
      connection  = this.getConnection();
      connection.setAutoCommit(false);
      csmt  = connection.prepareCall("{CALL QMS_QUOTE_PACK.quote_sell_buy_cartages_proc(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");
      csmt.setString(1,""); //masterDOB.getShipperZipCode()[laneNo]!=null?masterDOB.getShipperZipCode()[laneNo]:""
      csmt.setString(2,(masterDOB.getShipperZones()[laneNo]!=null?masterDOB.getShipperZones()[laneNo]:"").replaceAll(",","~"));
      csmt.setString(3,"");//masterDOB.getConsigneeZipCode()[laneNo]!=null?masterDOB.getConsigneeZipCode()[laneNo]:""
      csmt.setString(4,(masterDOB.getConsigneeZones()[laneNo]!=null?masterDOB.getConsigneeZones()[laneNo]:"").replaceAll(",","~"));
      csmt.setString(5,masterDOB.getSalesPersonCode());
      csmt.setString(6,masterDOB.getTerminalId());
      csmt.setString(7,masterDOB.getBuyRatesPermission());
      csmt.setString(8,masterDOB.getOriginLocation()[laneNo]);
      csmt.setString(9,masterDOB.getDestLocation()[laneNo]);
      csmt.setDouble(10,finalDOB.getCartageMargin()); 
      csmt.setDouble(11,finalDOB.getCartageDiscount());
      csmt.setString(12,masterDOB.getCustomerId());
      csmt.setString(13,""+masterDOB.getShipmentMode());
      csmt.setString(14,masterDOB.getShipperMode());
      
      /*if("2".equalsIgnoreCase(masterDOB.getShipperMode()))
          csmt.setString(15,masterDOB.getShipperConsoleType());
      else
          csmt.setString(15,"~");
        
      csmt.setString(16,masterDOB.getConsigneeMode());
      
      if("2".equalsIgnoreCase(masterDOB.getConsigneeMode()))
          csmt.setString(17,masterDOB.getConsigneeConsoleType());
      else
	      csmt.setString(17,"~");*/
      if("2".equalsIgnoreCase(""+masterDOB.getShipmentMode()))
          csmt.setString(15,"list".equalsIgnoreCase(masterDOB.getMultiquoteweightBrake())?"FCL":"LCL");
      else
          csmt.setString(15,"~");
        
      csmt.setString(16,masterDOB.getConsigneeMode());
      
      if("2".equalsIgnoreCase(""+masterDOB.getShipmentMode()))
          csmt.setString(17,"list".equalsIgnoreCase(masterDOB.getMultiquoteweightBrake())?"FCL":"LCL");
      else
          csmt.setString(17,"~");
          
      
      
          
      csmt.setString(18,masterDOB.getOperation());
      //csmt.setString(19,masterDOB.getQuoteId()!=0?masterDOB.getQuoteId()+"":"");  //@@ Commented by subrahmanyam  for the enhancement 146971 on 1/12/08
      csmt.setString(19,masterDOB.getQuoteId()!=null?masterDOB.getQuoteId()+"":"");  //@@ Added by subrahmanyam  for the enhancement 146971 on 1/12/08
      
      csmt.registerOutParameter(20,OracleTypes.CURSOR);
      csmt.registerOutParameter(21,OracleTypes.CURSOR);
      csmt.registerOutParameter(22,OracleTypes.CURSOR);//Distinct Pickup Charge slabs
      csmt.registerOutParameter(23,OracleTypes.CURSOR);//Distinct Delivery Charge slabs
      
      csmt.execute();
      
      
      rs  = (ResultSet)csmt.getObject(20);
      logger.info("Time Taken for DB procedure in milli seconds for 3rd screen (quote_sell_buy_cartages_proc) :  " + ((System.currentTimeMillis()) - start)  + "   UserId ::"+ masterDOB.getUserId() + " Origin :: "+ masterDOB.getOriginLocation()[laneNo] + " Destination::"+masterDOB.getDestLocation()[laneNo]+" TerminalId :: "+ masterDOB.getTerminalId());
      pickWeightBreaksRS      = (ResultSet)csmt.getObject(22);
      delWeightBreaksRS       = (ResultSet)csmt.getObject(23);
      /*delWeightBreaksRS       = (ResultSet)csmt.getObject(23);
      delWeightBreaksRSList   = (ResultSet)csmt.getObject(24);*/
      
   //  originChargesList = legDOB.getOriginChargesList();
    // destChargesList   = legDOB.getDestChargesList();
    
      while(rs.next())
      {
        if("Pickup".equalsIgnoreCase(rs.getString("COST_INCURREDAT")))
        {
        flag = rs.getString("SEL_BUY_FLAG");
       

        if( (masterDOB.getShipperZipCode()!=null && masterDOB.getShipperZipCode()[laneNo]!=null&& masterDOB.getShipperZipCode()[laneNo]!=null)
            ||(masterDOB.getShipperZones()!=null)// && masterDOB.getShipperZones()[laneNo].indexOf(",")==-1) // Added By Kishore Podili For Multi Zone Codes
          )
        {
          if(originChargesList==null)
          {
            originChargesList = new ArrayList();
          }
        //Added By Kishore for multiple ZoneCodes
         //Modified By Kishore For Codereview FOr 236286 CR
          if(cartageChargesDOB!=null 
        		  && (rs.getString("SELLCHARGEID").equalsIgnoreCase(cartageChargesDOB.getSellChargeId()) || rs.getString("SELLCHARGEID").equalsIgnoreCase(cartageChargesDOB.getBuyChargeId()))
        		  && (rs.getString("ZONE")!=null && rs.getString("ZONE").equals(cartageChargesDOB.getZoneCode()))
        	)
          {
            chargeInfo  = new MultiQuoteChargeInfo();
            
            chargeInfoList.add(chargeInfo);
            
            chargeInfo.setBreakPoint(rs.getString("CHARGESLAB"));
            
            if(rs.getString("CURRENCY")!=null && rs.getString("CURRENCY").trim().length()!=0)
              chargeInfo.setCurrency(rs.getString("CURRENCY"));
            else
               chargeInfo.setCurrency(masterDOB.getTerminalCurrency());
               
            chargeInfo.setBuyRate(rs.getDouble("BUYRATE"));
            chargeInfo.setRecOrConSellRrate(rs.getDouble("SELLRATE"));
            chargeInfo.setSellChargeMargin(rs.getDouble("MARGINVALUE"));
            chargeInfo.setSellChargeMarginType(rs.getString("MARGIN_TYPE"));
            chargeInfo.setRateIndicator(rs.getString("RATE_INDICATOR"));
            pickWeightBreak = rs.getString("WEIGHT_BREAK");
            pickRateType    = rs.getString("RATE_TYPE");
            
            if("BASE".equalsIgnoreCase(chargeInfo.getBreakPoint()) || "MIN".equalsIgnoreCase(chargeInfo.getBreakPoint()) || "MAX".equalsIgnoreCase(chargeInfo.getBreakPoint()) || ("SLAB".equalsIgnoreCase(pickWeightBreak) && "FLAT".equalsIgnoreCase(pickRateType))  || ("BOTH".equalsIgnoreCase(pickRateType) && "F".equalsIgnoreCase(chargeInfo.getRateIndicator())))
            {
              chargeInfo.setBasis("Per Shipment");
            }
            else
            {
              if("LIST".equalsIgnoreCase(rs.getString("WEIGHT_BREAK")))
              {
                chargeInfo.setBasis("Per Container");
              }
              else if("Per Kg".equalsIgnoreCase(rs.getString("CHARGEBASIS")))
              {
                chargeInfo.setBasis("Per Kilogram");
              }
              else if("Per Lb".equalsIgnoreCase(rs.getString("CHARGEBASIS")))
              {
                chargeInfo.setBasis("Per Pound");
              }
              else if("Per CFT".equalsIgnoreCase(rs.getString("CHARGEBASIS")))
              {
                chargeInfo.setBasis("Per Cubic Feet");
              }
              else if("Per CBM".equalsIgnoreCase(rs.getString("CHARGEBASIS")))
              {
                chargeInfo.setBasis("Per Cubic Meter");
              }
              else
                chargeInfo.setBasis(rs.getString("CHARGEBASIS"));
            }
            chargeInfo.setRatio(rs.getString("DENSITY_RATIO"));
            chargeInfo.setLineNumber(rs.getInt("LINE_NO"));
            chargeInfo.setSelectedFlag(rs.getString("SELECTED_FLAG"));
            //if("M".equalsIgnoreCase(cartageChargesDOB.getMarginDiscountFlag()) || cartageChargesDOB.getMarginDiscountFlag()==null)
            if("BC".equalsIgnoreCase(flag)||"M".equalsIgnoreCase(cartageChargesDOB.getMarginDiscountFlag()) )
            {  
              chargeInfo.setMargin(rs.getDouble("MARGINVALUE"));
              chargeInfo.setTieMarginDiscountValue(rs.getDouble("MARGINVALUE"));
              chargeInfo.setMarginType(rs.getString("MARGIN_TYPE"));
              
              //@@ Commented by subrahmanyam for the Enhancement 154381 on 28/01/09              
              /*if("A".equalsIgnoreCase(rs.getString("MARGIN_TYPE")))
                sellRate  = rs.getDouble("BUYRATE")+rs.getDouble("MARGINVALUE");
              else if("P".equalsIgnoreCase(rs.getString("MARGIN_TYPE")))
                sellRate  = rs.getDouble("BUYRATE")+(rs.getDouble("BUYRATE")*rs.getDouble("MARGINVALUE")/100);*/
            //@@ Added by subrahmanyam for the Enhancement 154381 on 28/01/09
                        if(rs.getDouble("MARGINVALUE")>0 || "Y".equalsIgnoreCase(rs.getString("SELECTED_FLAG")))
                        {
              if("A".equalsIgnoreCase(rs.getString("MARGIN_TYPE")))
                sellRate  = rs.getDouble("BUYRATE")+rs.getDouble("MARGINVALUE");
              else if("P".equalsIgnoreCase(rs.getString("MARGIN_TYPE")))
                sellRate  = rs.getDouble("BUYRATE")+(rs.getDouble("BUYRATE")*rs.getDouble("MARGINVALUE")/100);
            }
                        else
                          sellRate  = rs.getDouble("BUYRATE");
          //@@ Ended by subrahmanyam for the Enhancement 154381 on 28/01/09
            }
            else   if("SC".equalsIgnoreCase(flag))
            {
              chargeInfo.setDiscount(rs.getDouble("MARGINVALUE"));
              chargeInfo.setTieMarginDiscountValue(rs.getDouble("MARGINVALUE"));
              chargeInfo.setDiscountType(rs.getString("MARGIN_TYPE"));
              
              if("A".equalsIgnoreCase(rs.getString("MARGIN_TYPE")))
                sellRate  = rs.getDouble("SELLRATE")-rs.getDouble("MARGINVALUE");
              else if("P".equalsIgnoreCase(rs.getString("MARGIN_TYPE")))
                sellRate  = rs.getDouble("SELLRATE")-(rs.getDouble("SELLRATE")*rs.getDouble("MARGINVALUE")/100);
            }
          
            chargeInfo.setSellRate(sellRate);
            chargeInfo.setTieSellRateValue(sellRate);
            /*if("A".equalsIgnoreCase(rs.getString("MARGIN_TYPE")))
              sellRate  = rs.getDouble("BUYRATE")+rs.getDouble("MARGINVALUE");
            else if("P".equalsIgnoreCase(rs.getString("MARGIN_TYPE")))
              sellRate  = rs.getDouble("BUYRATE")+(rs.getDouble("BUYRATE")*rs.getDouble("MARGINVALUE")/100);*/
              
          }
          else
          {
           
            cartageChargesDOB  = new MultiQuoteCharges();
            /*pickupChargeBasisListForZone = new ArrayList();
            pickupChargeBasisList.add(pickupChargeBasisListForZone);*/
            
            
            //if it is a sell charge/rate
            if("SC".equalsIgnoreCase(flag))
            {
              cartageChargesDOB.setSellChargeId(rs.getString("SELLCHARGEID"));
              cartageChargesDOB.setBuyChargeId(rs.getString("BUY_CHARGE_ID"));
            }
            else if("BC".equalsIgnoreCase(flag))
            {
              cartageChargesDOB.setBuyChargeId(rs.getString("SELLCHARGEID"));
            }
                     
            cartageChargesDOB.setSellBuyFlag(flag);
            
           //cartageChargesDOB.setChargeDescriptionId("Pickup Charges");
          //Added By Kishore for multiple ZoneCodes
            cartageChargesDOB.setZoneCode(rs.getString("ZONE"));
            if(rs.getString("ZONE")!=null || !"".equalsIgnoreCase(rs.getString("ZONE")))
            	cartageChargesDOB.setChargeDescriptionId("Zone-"+rs.getString("ZONE")+" Pickup Charges");
            else
                cartageChargesDOB.setChargeDescriptionId("Pickup Charges");
            
            cartageChargesDOB.setCostIncurredAt(rs.getString("COST_INCURREDAT"));
            cartageChargesDOB.setTerminalId(rs.getString("TERMINALID"));
            cartageChargesDOB.setSelectedFlag(rs.getString("SELECTED_FLAG"));
            cartageChargesDOB.setMarginDiscountFlag(rs.getString("MARGIN_DISCOUNT_FLAG"));
            chargeInfoList  = new ArrayList();
            chargeInfo      = new MultiQuoteChargeInfo();
            chargeInfoList.add(chargeInfo);
            
            cartageChargesDOB.setChargeInfoList(chargeInfoList);
            
            chargeInfo.setBreakPoint(rs.getString("CHARGESLAB"));
            
            if(rs.getString("CURRENCY")!=null && rs.getString("CURRENCY").trim().length()!=0)
              chargeInfo.setCurrency(rs.getString("CURRENCY"));
            else
               chargeInfo.setCurrency(masterDOB.getTerminalCurrency());
               
            chargeInfo.setBuyRate(rs.getDouble("BUYRATE"));
            chargeInfo.setRecOrConSellRrate(rs.getDouble("SELLRATE"));
            chargeInfo.setSellChargeMargin(rs.getDouble("MARGINVALUE"));
            chargeInfo.setSellChargeMarginType(rs.getString("MARGIN_TYPE"));
            chargeInfo.setRateIndicator(rs.getString("RATE_INDICATOR"));
           // chargeInfo.setMargin(rs.getDouble("MARGINVALUE"));
          //  chargeInfo.setMarginType(rs.getString("MARGIN_TYPE"));
            //chargeInfo.setBasis(rs.getString("CHARGEBASIS"));
            pickWeightBreak = rs.getString("WEIGHT_BREAK");
            pickRateType    = rs.getString("RATE_TYPE");
            if("BASE".equalsIgnoreCase(chargeInfo.getBreakPoint()) || "MIN".equalsIgnoreCase(chargeInfo.getBreakPoint()) || "MAX".equalsIgnoreCase(chargeInfo.getBreakPoint()) || ("SLAB".equalsIgnoreCase(pickWeightBreak) && "FLAT".equalsIgnoreCase(pickRateType)) || ("BOTH".equalsIgnoreCase(pickRateType) && "F".equalsIgnoreCase(chargeInfo.getRateIndicator())))
            {
              chargeInfo.setBasis("Per Shipment");
            }
            else
            {
              if("LIST".equalsIgnoreCase(rs.getString("WEIGHT_BREAK")))
              {
                chargeInfo.setBasis("Per Container");
              }
              else if("Per Kg".equalsIgnoreCase(rs.getString("CHARGEBASIS")))
              {
                chargeInfo.setBasis("Per Kilogram");
              }
              else if("Per Lb".equalsIgnoreCase(rs.getString("CHARGEBASIS")))
              {
                chargeInfo.setBasis("Per Pound");
              }
              else if("Per CFT".equalsIgnoreCase(rs.getString("CHARGEBASIS")))
              {
                chargeInfo.setBasis("Per Cubic Feet");
              }
              else if("Per CBM".equalsIgnoreCase(rs.getString("CHARGEBASIS")))
              {
                chargeInfo.setBasis("Per Cubic Meter");
              }
              else
                chargeInfo.setBasis(rs.getString("CHARGEBASIS"));
            }
            chargeInfo.setRatio(rs.getString("DENSITY_RATIO"));
            chargeInfo.setLineNumber(rs.getInt("LINE_NO"));
            chargeInfo.setSelectedFlag(rs.getString("SELECTED_FLAG"));
           // if("M".equalsIgnoreCase(cartageChargesDOB.getMarginDiscountFlag()) || cartageChargesDOB.getMarginDiscountFlag()==null)
            if("BC".equalsIgnoreCase(flag)||"M".equalsIgnoreCase(cartageChargesDOB.getMarginDiscountFlag()) )
            {  
              chargeInfo.setMargin(rs.getDouble("MARGINVALUE"));
              chargeInfo.setTieMarginDiscountValue(rs.getDouble("MARGINVALUE"));
              chargeInfo.setMarginType(rs.getString("MARGIN_TYPE"));
              
              //@@ Commented by subrahmanyam for the Enhancement 154381 on 28/01/09              
              /*if("A".equalsIgnoreCase(rs.getString("MARGIN_TYPE")))
                sellRate  = rs.getDouble("BUYRATE")+rs.getDouble("MARGINVALUE");
              else if("P".equalsIgnoreCase(rs.getString("MARGIN_TYPE")))
                sellRate  = rs.getDouble("BUYRATE")+(rs.getDouble("BUYRATE")*rs.getDouble("MARGINVALUE")/100);*/
//@@ Added by subrahmanyam for the Enhancement 154381 on 28/01/09                
                if(rs.getDouble("MARGINVALUE")>0 || "Y".equalsIgnoreCase(rs.getString("SELECTED_FLAG")))
                {
              if("A".equalsIgnoreCase(rs.getString("MARGIN_TYPE")))
                sellRate  = rs.getDouble("BUYRATE")+rs.getDouble("MARGINVALUE");
              else if("P".equalsIgnoreCase(rs.getString("MARGIN_TYPE")))
                sellRate  = rs.getDouble("BUYRATE")+(rs.getDouble("BUYRATE")*rs.getDouble("MARGINVALUE")/100);
            }
            else
                  sellRate  = rs.getDouble("BUYRATE");
//@@ Ended by subrahmanyam for the Enhancement 154381 on 28/01/09    
            }
            else
            {
              chargeInfo.setDiscount(rs.getDouble("MARGINVALUE"));
              chargeInfo.setTieMarginDiscountValue(rs.getDouble("MARGINVALUE"));
              chargeInfo.setDiscountType(rs.getString("MARGIN_TYPE"));
              
              if("A".equalsIgnoreCase(rs.getString("MARGIN_TYPE")))
                sellRate  = rs.getDouble("SELLRATE")-rs.getDouble("MARGINVALUE");
              else if("P".equalsIgnoreCase(rs.getString("MARGIN_TYPE")))
                sellRate  = rs.getDouble("SELLRATE")-(rs.getDouble("SELLRATE")*rs.getDouble("MARGINVALUE")/100);
            }
              
            chargeInfo.setSellRate(sellRate);
            chargeInfo.setTieSellRateValue(sellRate);
           // if("Pickup".equalsIgnoreCase(rs.getString("COST_INCURREDAT")))
            //{
       
            // Added By Kishore Podili For Multi Zone Codes
              if((masterDOB.getShipperZipCode()!=null && masterDOB.getShipperZipCode()[laneNo]!=null && masterDOB.getShipperZipCode()[laneNo].trim().length()!=0)||(masterDOB.getShipperZones()[laneNo]!=null )) //&& masterDOB.getShipperZones()[laneNo].indexOf(",")==-1))
              {
                
                originChargesList.add(cartageChargesDOB);  
              }
            //}            
          }
         /* pickupChargeBasisListForZone.add(chargeInfo.getBasis());*/
        }
        //Added By Kishore
        //if(masterDOB.getShipperZones()[laneNo].indexOf(",")!=-1)
      if(masterDOB.getShipperZones()[laneNo].length()>0)
        {//Written by Sanjay for Cartage Charges
          //get the pickup and delivery charges as seperate entities as required for the annexure
          HashMap charge = null;
       
          if("Pickup".equalsIgnoreCase(rs.getString("COST_INCURREDAT")))
          {
            if(pickUpZoneCode.containsKey(rs.getString("ZONE")+rs.getString("SELLCHARGEID")))
            {
              if(rs.getString("CHARGESLAB")!=null)
              {
            	//Modified By Kishore For the ChargeBasis in the Annexure PDF on 26-May-11
                 if("FLAT".equalsIgnoreCase(rs.getString("CHARGESLAB"))){
                      isPickupFlat  =  true;
                	 pickupFlatChargeBasis = rs.getString("CHARGEBASIS");
                 }
                 if("MAX".equalsIgnoreCase(rs.getString("CHARGESLAB"))){
                      isPickupMax   =  true;
                      pickupMaxChargeBasis = rs.getString("CHARGEBASIS");
                 }
                      
                 pickQuoteCartageRates = (MultiQuoteCartageRates)pickUpZoneCode.get(rs.getString("ZONE")+rs.getString("SELLCHARGEID"));
                 charge = pickQuoteCartageRates.getRates();
                 
                 if("SC".equalsIgnoreCase(flag))
                      charge.put(rs.getString("CHARGESLAB"),rs.getString("SELLRATE"));
                 else
                      charge.put(rs.getString("CHARGESLAB"),rs.getString("BUYRATE"));
                      
                 pickUpZoneCode.put(rs.getString("ZONE")+rs.getString("SELLCHARGEID"),pickQuoteCartageRates);  
              }
              addToPickupList = false;
            }
            else
            {
              pickQuoteCartageRates = new MultiQuoteCartageRates();
              pickQuoteCartageRates.setZone(rs.getString("ZONE"));
              pickQuoteCartageRates.setCurrency(rs.getString("CURRENCY"));
              pickQuoteCartageRates.setCartageId(rs.getString("SELLCHARGEID"));
              pickWeightBreak = rs.getString("WEIGHT_BREAK");
              charge = new HashMap();
              if(rs.getString("CHARGESLAB")!=null)
              {
            	//Added By Kishore For the ChargeBasis in the Annexure PDF on 26-May-11
            	  if("MIN".equalsIgnoreCase(rs.getString("CHARGESLAB"))){
                      isPickupMin   =  true;                 
                      pickupMinChargeBasis = rs.getString("CHARGEBASIS");
                  }
                  if("SC".equalsIgnoreCase(flag))
                      charge.put(rs.getString("CHARGESLAB"),rs.getString("SELLRATE"));
                  else
                      charge.put(rs.getString("CHARGESLAB"),rs.getString("BUYRATE"));
                  //charge.put(rs.getString("CHARGESLAB"),rs.getString("SELLRATE"));
                  pickQuoteCartageRates.setRates(charge);
                  pickUpZoneCode.put(rs.getString("ZONE")+rs.getString("SELLCHARGEID"),pickQuoteCartageRates);
              }
              addToPickupList = true;
            }
           
            if(addToPickupList)
              pickUpQuoteCartageRates.add(pickQuoteCartageRates);
          }                     
        }//End
      //pickupChargeBasisList.add(chargeInfo.getBasis()); //Added By Kishore For the ChargeBasis in the Annexure PDF on 26-May-11
      }
      if("Delivery".equalsIgnoreCase(rs.getString("COST_INCURREDAT")))
      {
       flag = rs.getString("SEL_BUY_FLAG");
        // Added By Kishore Podili For Multi Zone Codes
       //System.out.println(masterDOB.getConsigneeZipCode());
       if((masterDOB.getConsigneeZipCode()!=null&& masterDOB.getConsigneeZipCode()[laneNo]!=null && masterDOB.getConsigneeZipCode()[laneNo].trim().length()!=0)
            ||(masterDOB.getConsigneeZones()!=null )) //&& masterDOB.getConsigneeZones()[laneNo].indexOf(",")==-1)) KISHORE
        {
          
          if(destChargesList==null)
          {
            destChargesList = new ArrayList();
          }
          
         
        //Modified By Kishore for multiple ZoneCodes
           if(cartageDelChargesDOB!=null 
        		   && (rs.getString("SELLCHARGEID").equalsIgnoreCase(cartageDelChargesDOB.getSellChargeId()) || rs.getString("SELLCHARGEID").equalsIgnoreCase(cartageDelChargesDOB.getBuyChargeId()))
        		   && (rs.getString("ZONE")!=null && rs.getString("ZONE").equals(cartageDelChargesDOB.getZoneCode()))
             )
          {
            chargeInfo  = new MultiQuoteChargeInfo();
            
            chargeInfoList.add(chargeInfo);
            
            chargeInfo.setBreakPoint(rs.getString("CHARGESLAB"));
            if(rs.getString("CURRENCY")!=null && rs.getString("CURRENCY").trim().length()!=0)
              chargeInfo.setCurrency(rs.getString("CURRENCY"));
            else
               chargeInfo.setCurrency(masterDOB.getTerminalCurrency());
               
            chargeInfo.setBuyRate(rs.getDouble("BUYRATE"));
            chargeInfo.setRecOrConSellRrate(rs.getDouble("SELLRATE"));
            chargeInfo.setSellChargeMargin(rs.getDouble("MARGINVALUE"));
            chargeInfo.setSellChargeMarginType(rs.getString("MARGIN_TYPE"));
            chargeInfo.setRateIndicator(rs.getString("RATE_INDICATOR"));
            delWeightBreak   =  rs.getString("WEIGHT_BREAK");
            delRateType      =  rs.getString("RATE_TYPE");
            //chargeInfo.setMargin(rs.getDouble("MARGINVALUE"));
           // chargeInfo.setMarginType(rs.getString("MARGIN_TYPE"));
            //chargeInfo.setBasis(rs.getString("CHARGEBASIS"));
            if("BASE".equalsIgnoreCase(chargeInfo.getBreakPoint()) || "MIN".equalsIgnoreCase(chargeInfo.getBreakPoint()) || "MAX".equalsIgnoreCase(chargeInfo.getBreakPoint()) || ("SLAB".equalsIgnoreCase(delWeightBreak) && "FLAT".equalsIgnoreCase(delRateType))  || ("BOTH".equalsIgnoreCase(delRateType) && "F".equalsIgnoreCase(chargeInfo.getRateIndicator())))
            {
              chargeInfo.setBasis("Per Shipment");
            }
            else
            {
              if("LIST".equalsIgnoreCase(rs.getString("WEIGHT_BREAK")))
              {
                chargeInfo.setBasis("Per Container");
              }
              else if("Per Kg".equalsIgnoreCase(rs.getString("CHARGEBASIS")))
              {
                chargeInfo.setBasis("Per Kilogram");
              }
              else if("Per Lb".equalsIgnoreCase(rs.getString("CHARGEBASIS")))
              {
                chargeInfo.setBasis("Per Pound");
              }
              else if("Per CFT".equalsIgnoreCase(rs.getString("CHARGEBASIS")))
              {
                chargeInfo.setBasis("Per Cubic Feet");
              }
              else if("Per CBM".equalsIgnoreCase(rs.getString("CHARGEBASIS")))
              {
                chargeInfo.setBasis("Per Cubic Meter");
              }
              else
                chargeInfo.setBasis(rs.getString("CHARGEBASIS"));
            }
            chargeInfo.setRatio(rs.getString("DENSITY_RATIO"));
            chargeInfo.setLineNumber(rs.getInt("LINE_NO"));
            chargeInfo.setSelectedFlag(rs.getString("SELECTED_FLAG"));
           // if("M".equalsIgnoreCase(cartageDelChargesDOB.getMarginDiscountFlag()) || cartageDelChargesDOB.getMarginDiscountFlag()==null)
            if("BC".equalsIgnoreCase(flag)||"M".equalsIgnoreCase(cartageDelChargesDOB.getMarginDiscountFlag()))
            {  
              chargeInfo.setMargin(rs.getDouble("MARGINVALUE"));
              chargeInfo.setTieMarginDiscountValue(rs.getDouble("MARGINVALUE"));
              chargeInfo.setMarginType(rs.getString("MARGIN_TYPE"));
              
               //@@ Commented by subrahmanyam for the enhancement 154381 on 30/01/09              
              /*if("A".equalsIgnoreCase(rs.getString("MARGIN_TYPE")))
                sellRate  = rs.getDouble("BUYRATE")+rs.getDouble("MARGINVALUE");
              else if("P".equalsIgnoreCase(rs.getString("MARGIN_TYPE")))
                sellRate  = rs.getDouble("BUYRATE")+(rs.getDouble("BUYRATE")*rs.getDouble("MARGINVALUE")/100);*/
//@@ Added by subrahmanyam for the Enhancement 154381 on 30/01/09                
                if(rs.getDouble("MARGINVALUE")>0 || "Y".equalsIgnoreCase(rs.getString("SELECTED_FLAG")))
                {
              if("A".equalsIgnoreCase(rs.getString("MARGIN_TYPE")))
                sellRate  = rs.getDouble("BUYRATE")+rs.getDouble("MARGINVALUE");
              else if("P".equalsIgnoreCase(rs.getString("MARGIN_TYPE")))
                sellRate  = rs.getDouble("BUYRATE")+(rs.getDouble("BUYRATE")*rs.getDouble("MARGINVALUE")/100);
            }
            else
                  sellRate  = rs.getDouble("BUYRATE");
//@@ Ended by subrahmanyam for the Enhancement 154381 on 30/01/09  
            }
            else
            {
              chargeInfo.setDiscount(rs.getDouble("MARGINVALUE"));
              chargeInfo.setTieMarginDiscountValue(rs.getDouble("MARGINVALUE"));
              chargeInfo.setDiscountType(rs.getString("MARGIN_TYPE"));
              
              if("A".equalsIgnoreCase(rs.getString("MARGIN_TYPE")))
                sellRate  = rs.getDouble("SELLRATE")-rs.getDouble("MARGINVALUE");
              else if("P".equalsIgnoreCase(rs.getString("MARGIN_TYPE")))
                sellRate  = rs.getDouble("SELLRATE")-(rs.getDouble("SELLRATE")*rs.getDouble("MARGINVALUE")/100);
            }
          
            chargeInfo.setSellRate(sellRate);
            chargeInfo.setTieSellRateValue(sellRate);
            /*if("A".equalsIgnoreCase(rs.getString("MARGIN_TYPE")))
              sellRate  = rs.getDouble("BUYRATE")+rs.getDouble("MARGINVALUE");
            else if("P".equalsIgnoreCase(rs.getString("MARGIN_TYPE")))
              sellRate  = rs.getDouble("BUYRATE")+(rs.getDouble("BUYRATE")*rs.getDouble("MARGINVALUE")/100);*/
              
          }
          else
          {
            cartageDelChargesDOB  = new MultiQuoteCharges();
            /*delChargeBasisListForZone = new ArrayList();
            delChargeBasisList.add(delChargeBasisListForZone);*/
            //if it is a sell charge/rate
            if("SC".equalsIgnoreCase(flag))
            {
              cartageDelChargesDOB.setSellChargeId(rs.getString("SELLCHARGEID"));
              cartageDelChargesDOB.setBuyChargeId(rs.getString("BUY_CHARGE_ID"));
            }
            else if("BC".equalsIgnoreCase(flag))
            {
              cartageDelChargesDOB.setBuyChargeId(rs.getString("SELLCHARGEID"));
            }
            
                      
            cartageDelChargesDOB.setSellBuyFlag(flag);
            
           //cartageDelChargesDOB.setChargeDescriptionId("Delivery Charges");
          //Added By Kishore for multiple ZoneCodes
            cartageDelChargesDOB.setZoneCode(rs.getString("ZONE"));
            if(rs.getString("ZONE")!=null || !"".equalsIgnoreCase(rs.getString("ZONE")))
            	cartageDelChargesDOB.setChargeDescriptionId("Zone-"+rs.getString("ZONE")+" Delivery Charges");
            else
            cartageDelChargesDOB.setChargeDescriptionId("Delivery Charges");
            cartageDelChargesDOB.setCostIncurredAt(rs.getString("COST_INCURREDAT"));
            cartageDelChargesDOB.setTerminalId(rs.getString("TERMINALID"));
            cartageDelChargesDOB.setSelectedFlag(rs.getString("SELECTED_FLAG"));
            cartageDelChargesDOB.setMarginDiscountFlag(rs.getString("MARGIN_DISCOUNT_FLAG"));
            chargeInfoList  = new ArrayList();
            chargeInfo  = new MultiQuoteChargeInfo();
            chargeInfoList.add(chargeInfo);
           cartageDelChargesDOB.setChargeInfoList(chargeInfoList);
            
            chargeInfo.setBreakPoint(rs.getString("CHARGESLAB"));
             
            if(rs.getString("CURRENCY")!=null && rs.getString("CURRENCY").trim().length()!=0)
              chargeInfo.setCurrency(rs.getString("CURRENCY"));
            else
               chargeInfo.setCurrency(masterDOB.getTerminalCurrency());
               
            chargeInfo.setBuyRate(rs.getDouble("BUYRATE"));
          
            chargeInfo.setRecOrConSellRrate(rs.getDouble("SELLRATE"));
            chargeInfo.setSellChargeMargin(rs.getDouble("MARGINVALUE"));
            chargeInfo.setSellChargeMarginType(rs.getString("MARGIN_TYPE"));
            chargeInfo.setRateIndicator(rs.getString("RATE_INDICATOR"));
            delWeightBreak   =  rs.getString("WEIGHT_BREAK");
            delRateType      =  rs.getString("RATE_TYPE");
           // chargeInfo.setMargin(rs.getDouble("MARGINVALUE"));
          //  chargeInfo.setMarginType(rs.getString("MARGIN_TYPE"));
            //chargeInfo.setBasis(rs.getString("CHARGEBASIS"));
            if("BASE".equalsIgnoreCase(chargeInfo.getBreakPoint()) || "MIN".equalsIgnoreCase(chargeInfo.getBreakPoint()) || "MAX".equalsIgnoreCase(chargeInfo.getBreakPoint()) || ("SLAB".equalsIgnoreCase(delWeightBreak) && "FLAT".equalsIgnoreCase(delRateType)) || ("BOTH".equalsIgnoreCase(delRateType) && "F".equalsIgnoreCase(chargeInfo.getRateIndicator())))
            {
              chargeInfo.setBasis("Per Shipment");
            }
            else
            {
              if("LIST".equalsIgnoreCase(rs.getString("WEIGHT_BREAK")))
              {
                chargeInfo.setBasis("Per Container");
              }
              else if("Per Kg".equalsIgnoreCase(rs.getString("CHARGEBASIS")))
              {
                chargeInfo.setBasis("Per Kilogram");
              }
              else if("Per Lb".equalsIgnoreCase(rs.getString("CHARGEBASIS")))
              {
                chargeInfo.setBasis("Per Pound");
              }
              else if("Per CFT".equalsIgnoreCase(rs.getString("CHARGEBASIS")))
              {
                chargeInfo.setBasis("Per Cubic Feet");
              }
              else if("Per CBM".equalsIgnoreCase(rs.getString("CHARGEBASIS")))
              {
                chargeInfo.setBasis("Per Cubic Meter");
              }
              else
                chargeInfo.setBasis(rs.getString("CHARGEBASIS"));
            }
            chargeInfo.setRatio(rs.getString("DENSITY_RATIO"));
            chargeInfo.setLineNumber(rs.getInt("LINE_NO"));
            chargeInfo.setSelectedFlag(rs.getString("SELECTED_FLAG"));

           // if("M".equalsIgnoreCase(cartageDelChargesDOB.getMarginDiscountFlag()) || cartageDelChargesDOB.getMarginDiscountFlag()==null)
            if("BC".equalsIgnoreCase(flag)||"M".equalsIgnoreCase(cartageDelChargesDOB.getMarginDiscountFlag()))
            {   
              chargeInfo.setMargin(rs.getDouble("MARGINVALUE"));
              chargeInfo.setTieMarginDiscountValue(rs.getDouble("MARGINVALUE"));              
              chargeInfo.setMarginType(rs.getString("MARGIN_TYPE"));
              
            //@@ Commented by subramanyam for the enhancement 154381 on 05/02/09              
              /*if("A".equalsIgnoreCase(rs.getString("MARGIN_TYPE")))
                sellRate  = rs.getDouble("BUYRATE")+rs.getDouble("MARGINVALUE");
              else if("P".equalsIgnoreCase(rs.getString("MARGIN_TYPE")))
                sellRate  = rs.getDouble("BUYRATE")+(rs.getDouble("BUYRATE")*rs.getDouble("MARGINVALUE")/100);*/
//@@ Added by subrahmanyam for the Enhancement 154381 on 15/02/09             
              if(rs.getDouble("MARGINVALUE")>0 || "Y".equalsIgnoreCase(rs.getString("SELECTED_FLAG")))
              {
              if("A".equalsIgnoreCase(rs.getString("MARGIN_TYPE")))
                sellRate  = rs.getDouble("BUYRATE")+rs.getDouble("MARGINVALUE");
              else if("P".equalsIgnoreCase(rs.getString("MARGIN_TYPE")))
                sellRate  = rs.getDouble("BUYRATE")+(rs.getDouble("BUYRATE")*rs.getDouble("MARGINVALUE")/100);
            }
            else
                 sellRate = rs.getDouble("BUYRATE");
//@@ Ended by subrahmanyam for the Enhancement 154381 on 15/02/09 
            }
            else
            {
              chargeInfo.setDiscount(rs.getDouble("MARGINVALUE"));
              chargeInfo.setTieMarginDiscountValue(rs.getDouble("MARGINVALUE"));             
              chargeInfo.setDiscountType(rs.getString("MARGIN_TYPE"));
              
              if("A".equalsIgnoreCase(rs.getString("MARGIN_TYPE")))
                sellRate  = rs.getDouble("SELLRATE")-rs.getDouble("MARGINVALUE");
              else if("P".equalsIgnoreCase(rs.getString("MARGIN_TYPE")))
                sellRate  = rs.getDouble("SELLRATE")-(rs.getDouble("SELLRATE")*rs.getDouble("MARGINVALUE")/100);
            }
              
            chargeInfo.setSellRate(sellRate);
            chargeInfo.setTieSellRateValue(sellRate);           
            //if("Delivery".equalsIgnoreCase(rs.getString("COST_INCURREDAT")))
            //{
             
              // Added By Kishore Podili For Multi Zone Codes
              if((masterDOB.getConsigneeZipCode()!=null&& masterDOB.getConsigneeZipCode()[laneNo]!=null && masterDOB.getConsigneeZipCode()[laneNo].trim().length()!=0)||(masterDOB.getConsigneeZones()!=null && masterDOB.getConsigneeZones()[laneNo]!=null)) // &&masterDOB.getConsigneeZones()[laneNo].indexOf(",")==-1))
              {
               destChargesList.add(cartageDelChargesDOB);  
              }
            //}
          }   
           /*delChargeBasisListForZone.add(chargeInfo.getBasis());*/
        }
       // 	Added by Kishore
       //else
       //if( masterDOB.getConsigneeZones()[laneNo].indexOf(",")!=-1)
       if( masterDOB.getConsigneeZones()[laneNo].length()>0)
        {//Written by Sanjay for Cartage Charges
          //get the pickup and delivery charges as seperate entities as required for the annexure
          HashMap charge = null;
          if("Delivery".equalsIgnoreCase(rs.getString("COST_INCURREDAT")))
          {
            if(deliveryZoneCode.containsKey(rs.getString("ZONE")+rs.getString("SELLCHARGEID")))
            {
              if(rs.getString("CHARGESLAB")!=null)
              {
            	//Modified By Kishore For the ChargeBasis in the Annexure PDF on 26-May-11
                 if("FLAT".equalsIgnoreCase(rs.getString("CHARGESLAB"))){
                      isDeliveryFlat  =  true;
                      deliveryFlatChargeBasis = rs.getString("CHARGEBASIS");
                 }
                 if("MAX".equalsIgnoreCase(rs.getString("CHARGESLAB"))){
                      isDeliveryMax  =  true;
                      deliveryMaxChargeBasis = rs.getString("CHARGEBASIS");
                 }
               //Modified By Kishore For the ChargeBasis in the Annexure PDF on 26-May-11
                 delQuoteCartageRates = (MultiQuoteCartageRates)deliveryZoneCode.get(rs.getString("ZONE")+rs.getString("SELLCHARGEID"));
                 charge = delQuoteCartageRates.getRates();
                 if("SC".equalsIgnoreCase(flag))
                      charge.put(rs.getString("CHARGESLAB"),rs.getString("SELLRATE"));
                  else{
                	   if("P".equalsIgnoreCase(rs.getString("margin_type")))
                		  sellRate  = rs.getDouble("BUYRATE")+(rs.getDouble("BUYRATE")*rs.getDouble("MARGINVALUE")/100);
                	  else if("A".equalsIgnoreCase(rs.getString("margin_type"))) 
                		  sellRate  = rs.getDouble("BUYRATE")+rs.getDouble("MARGINVALUE"); 
                	  else
                		  sellRate  = rs.getDouble("BUYRATE");
                      charge.put(rs.getString("CHARGESLAB"),Double.toString(sellRate));
                      //charge.put(rs.getString("CHARGESLAB"),rs.getString("BUYRATE"));
                  }
                 //charge.put(rs.getString("CHARGESLAB"),rs.getString("SELLRATE"));
                 deliveryZoneCode.put(rs.getString("ZONE")+rs.getString("SELLCHARGEID"),delQuoteCartageRates);
              }
              addToDeliveryList = false;
            }
            else
            {
              delQuoteCartageRates = new MultiQuoteCartageRates();
              delWeightBreak       =  rs.getString("WEIGHT_BREAK");
              delQuoteCartageRates.setZone(rs.getString("ZONE"));
              delQuoteCartageRates.setCurrency(rs.getString("CURRENCY"));
              delQuoteCartageRates.setCartageId(rs.getString("SELLCHARGEID"));               
              charge = new HashMap();
              if(rs.getString("CHARGESLAB")!=null)
              {
            	//Modified  By Kishore For the ChargeBasis in the Annexure PDF on 26-May-11
                if("MIN".equalsIgnoreCase(rs.getString("CHARGESLAB"))){
                    isDeliveryMin   =  true;                 
                    deliveryMinChargeBasis = rs.getString("CHARGEBASIS");
                }
                 if("SC".equalsIgnoreCase(flag))
                      charge.put(rs.getString("CHARGESLAB"),rs.getString("SELLRATE"));
                  else{
                	  if("P".equalsIgnoreCase(rs.getString("margin_type")))
                		  sellRate  = rs.getDouble("BUYRATE")+(rs.getDouble("BUYRATE")*rs.getDouble("MARGINVALUE")/100);
                	  else if("A".equalsIgnoreCase(rs.getString("margin_type"))) 
                		  sellRate  = rs.getDouble("BUYRATE")+rs.getDouble("MARGINVALUE"); 
                	  else
                		  sellRate  = rs.getDouble("BUYRATE");
                      charge.put(rs.getString("CHARGESLAB"),Double.toString(sellRate));
                     // charge.put(rs.getString("CHARGESLAB"),rs.getString("BUYRATE"));
                  }
                 //charge.put(rs.getString("CHARGESLAB"),rs.getString("SELLRATE"));
                 delQuoteCartageRates.setRates(charge);
                 deliveryZoneCode.put(rs.getString("ZONE")+rs.getString("SELLCHARGEID"),delQuoteCartageRates);
              }
              addToDeliveryList = true;
            }
              if(addToDeliveryList)
                  deliveryQuoteCartageRates.add(delQuoteCartageRates);
          }           
        }//End
       //delChargeBasisList.add(chargeInfo.getBasis()); //Added By Kishore For the ChargeBasis in the Annexure PDF on 26-May-11
      }
      }      
      
      if(originChargesList!=null || destChargesList!=null)
      {
        if(originChargesList!=null)
          legDOB.setOriginChargesList(originChargesList);
        if(destChargesList!=null)
          legDOB.setDestChargesList(destChargesList);
      }
      /*if(deliveryZoneCode.values()!=null)
      {
        deliveryQuoteCartageRates = new ArrayList();
        deliveryQuoteCartageRates.addAll(deliveryZoneCode.values());
      }
      if(pickUpZoneCode.values()!=null)
      {
        pickUpQuoteCartageRates = new ArrayList();
        pickUpQuoteCartageRates.addAll(pickUpZoneCode.values());
      }*/
      if(deliveryQuoteCartageRates!=null && deliveryQuoteCartageRates.size()>0)
      {
        //delWeightBreaksRS   =   (ResultSet)csmt.getObject(23);
        
        if(isDeliveryMin){
          delWeightBreaksList.add("MIN");
          delChargeBasisList.add(deliveryMinChargeBasis);
        }
        
        if(isDeliveryFlat){
            delWeightBreaksList.add("FLAT");
            delChargeBasisList.add(deliveryFlatChargeBasis);
        }
        while(delWeightBreaksRS.next())
        {
          delWeightBreaksList.add(delWeightBreaksRS.getString("CHARGESLAB"));
          delChargeBasisList.add(delWeightBreaksRS.getString("CHARGEBASIS"));
        }
        
        //delWeightBreaksRSList = (ResultSet)csmt.getObject(24);
        
        /*while(delWeightBreaksRSList.next())
        {
          delWeightBreaksList.add(delWeightBreaksRSList.getString("CHARGESLAB"));
        }*/
        
        if(isDeliveryMax){
            delWeightBreaksList.add("MAX");
            delChargeBasisList.add(deliveryMaxChargeBasis);
        }
     
        legDOB.setDeliveryCartageRatesList(deliveryQuoteCartageRates);
        legDOB.setDeliveryWeightBreaks(delWeightBreaksList);
        legDOB.setDelChargeBasisList(delChargeBasisList); //Added By Kishore For the ChargeBasis in the Annexure PDF on 26-May-11
      }
      if(pickUpQuoteCartageRates!=null && pickUpQuoteCartageRates.size()>0)
      {
       // pickWeightBreaksRS          = (ResultSet)csmt.getObject(21);
        //pickWeightBreaksRSList  = 
        if(isPickupMin){
          pickupWeightBreaksList.add("MIN");
          pickupChargeBasisList.add(pickupMinChargeBasis);
        }
        
        if(isPickupFlat){
          pickupWeightBreaksList.add("FLAT");
          pickupChargeBasisList.add(pickupFlatChargeBasis);
        }
        
        while(pickWeightBreaksRS.next())
        {
          pickupWeightBreaksList.add(pickWeightBreaksRS.getString("CHARGESLAB"));
          pickupChargeBasisList.add(pickWeightBreaksRS.getString("CHARGEBASIS"));
        }
        
        //pickWeightBreaksRSList  = (ResultSet)csmt.getObject(22);
        
        /*while(pickWeightBreaksRSList.next())
        {
          pickupWeightBreaksList.add(pickWeightBreaksRSList.getString("CHARGESLAB"));
        }*/
        
        if(isPickupMax){
          pickupWeightBreaksList.add("MAX");
          pickupChargeBasisList.add(pickupMaxChargeBasis);
        }
        
      
        
        legDOB.setPickUpCartageRatesList(pickUpQuoteCartageRates);
        legDOB.setPickupWeightBreaks(pickupWeightBreaksList);
        legDOB.setPickupChargeBasisList(pickupChargeBasisList); //Added By Kishore For the ChargeBasis in the Annexure PDF on 26-May-11
      }
        
      //The below code is to get the ZONE-ZIPCODE Mapping from the CURSOR from 20 OUTPARAMETER
      
        zoneRs  = (ResultSet)csmt.getObject(21);
        ArrayList delivZipCodes  = null;
        ArrayList pickUpZipCodes = null;
            
        while(zoneRs.next())
        {
          if("Pickup".equalsIgnoreCase(zoneRs.getString("charge_type")))
          {
            // Added By Kishore Podili For Multi Zone Codes
            //if( !((masterDOB.getShipperZipCode()!=null && masterDOB.getShipperZipCode()[laneNo]!=null && masterDOB.getShipperZipCode()[laneNo].trim().length()!=0)))
            //{
              String from_toZip = null;
              if(pickUpZoneZipMap.containsKey(zoneRs.getString("ZONE")))
              {             
                pickUpZipCodes = (ArrayList) pickUpZoneZipMap.get(zoneRs.getString("ZONE"));
                if(zoneRs.getString("ALPHANUMERIC")!=null)
                 from_toZip = zoneRs.getString("ALPHANUMERIC")+zoneRs.getString("from_zipcode")+" - "+zoneRs.getString("ALPHANUMERIC")+zoneRs.getString("to_zipcode");
                else
                 from_toZip = zoneRs.getString("from_zipcode")+" - "+zoneRs.getString("to_zipcode");
                if(from_toZip!=null)
                  pickUpZipCodes.add(from_toZip);
                pickUpZoneZipMap.put(zoneRs.getString("ZONE"),pickUpZipCodes);
              }
              else
              {
                pickUpZipCodes = new ArrayList();
                if(zoneRs.getString("from_zipcode")!=null)
                {
                  if(zoneRs.getString("ALPHANUMERIC")!=null)
                   from_toZip = zoneRs.getString("ALPHANUMERIC")+zoneRs.getString("from_zipcode")+" - "+zoneRs.getString("ALPHANUMERIC")+zoneRs.getString("to_zipcode");
                  else
                   from_toZip = zoneRs.getString("from_zipcode")+" - "+zoneRs.getString("to_zipcode");
                  if(from_toZip!=null)
                    pickUpZipCodes.add(from_toZip);
                  pickUpZoneZipMap.put(zoneRs.getString("ZONE"),pickUpZipCodes);
                }
              }
           // }
          }
          else if("Delivery".equalsIgnoreCase(zoneRs.getString("charge_type")))
          {
            // Added By Kishore Podili For Multi Zone Codes
            //if(!((masterDOB.getConsigneeZipCode()!=null && masterDOB.getConsigneeZipCode()[laneNo]!=null &&masterDOB.getConsigneeZipCode()[laneNo].trim().length()!=0)))
            //{
              String from_toZip = null;
              if(deliveryZoneZipMap.containsKey(zoneRs.getString("ZONE")))
              {
                delivZipCodes = (ArrayList) deliveryZoneZipMap.get(zoneRs.getString("ZONE"));
                if(zoneRs.getString("ALPHANUMERIC")!=null)
                 from_toZip = zoneRs.getString("ALPHANUMERIC")+zoneRs.getString("from_zipcode")+" - "+zoneRs.getString("ALPHANUMERIC")+zoneRs.getString("to_zipcode");
                else
                 from_toZip = zoneRs.getString("from_zipcode")+" - "+zoneRs.getString("to_zipcode");
                if(from_toZip!=null)
                  delivZipCodes.add(from_toZip);
                deliveryZoneZipMap.put(zoneRs.getString("ZONE"),delivZipCodes);
              }
              else
              { 
                delivZipCodes = new ArrayList();
                if(zoneRs.getString("ALPHANUMERIC")!=null)
                 from_toZip = zoneRs.getString("ALPHANUMERIC")+zoneRs.getString("from_zipcode")+" - "+zoneRs.getString("ALPHANUMERIC")+zoneRs.getString("to_zipcode");
                else
                 from_toZip = zoneRs.getString("from_zipcode")+" - "+zoneRs.getString("to_zipcode");
                if(from_toZip!=null)
                  delivZipCodes.add(from_toZip);
                deliveryZoneZipMap.put(zoneRs.getString("ZONE"),delivZipCodes);
              }
            //}
          }          
        }
        legDOB.setPickZoneZipMap(pickUpZoneZipMap);
        legDOB.setDeliveryZoneZipMap(deliveryZoneZipMap);
      //End
      
    }
    catch(SQLException sqEx)
		{
      sqEx.printStackTrace();
			//Logger.error(FILE_NAME,"QMSQuoteDAO[getLegCharges(masterDOB,sellBuyRate,sellBuyFlag,laneNo,origin,dest)] -> "+sqEx.toString());
      logger.error(FILE_NAME+"QMSQuoteDAO[getCartages(masterDOB,sellBuyRate,sellBuyFlag,laneNo,origin,dest)] -> "+sqEx.toString());
      throw new SQLException(sqEx.toString());
		}
    catch(Exception e)
		{
      e.printStackTrace();
			//Logger.error(FILE_NAME,"QMSQuoteDAO[getLegCharges(masterDOB,sellBuyRate,sellBuyFlag,laneNo,origin,dest)] -> "+e.toString());
      logger.error(FILE_NAME+"QMSQuoteDAO[getCartages(masterDOB,sellBuyRate,sellBuyFlag,laneNo,origin,dest)] -> "+e.toString());
      throw new SQLException(e.toString());
		}
		finally
		{
			try
			{				
        if(zoneRs!=null)
            zoneRs.close();
        if(pickWeightBreaksRS!=null)
          pickWeightBreaksRS.close();
        if(delWeightBreaksRS!=null)
          delWeightBreaksRS.close();
        if(rs!=null)
          rs.close();
        if(csmt!=null)
          csmt.close();
        if(connection!=null)
          connection.close();
        //ConnectionUtil.closeConnection(connection,csmt,rs);
			}
			catch(Exception ex)
			{
				//Logger.error(FILE_NAME,"Finally : QMSQuoteDAO[getLegCharges(masterDOB,sellBuyRate,sellBuyFlag,laneNo,origin,dest)]-> "+ex.toString());
        logger.error(FILE_NAME+"Finally : QMSQuoteDAO[getLegCharges(masterDOB,sellBuyRate,sellBuyFlag,laneNo,origin,dest)]-> "+ex.toString());
        throw new SQLException(ex.toString());
			}
		}
    return legDOB;

}

private void updateQuoteContactPersons(MultiQuoteMasterDOB masterDOB, long id, Connection connection) throws SQLException
{
  PreparedStatement pStmt	= null;
		//ResultSet rs				=	null;//Commented By RajKumari on 24-10-2008 for Connection Leakages.
  try
  {
    pStmt = connection.prepareStatement(masterContactPersonDelQry);
    pStmt.clearParameters();
    pStmt.setLong(1,id);
    pStmt.executeUpdate();
    if(pStmt!=null)
     pStmt.close();
    
   if(masterDOB.getCustomerContacts()!=null)
      insertQuoteContactPersons(masterDOB,connection);     
  }
  catch(SQLException sqEx)
		{
    sqEx.printStackTrace();
		
    logger.error(FILE_NAME+"QMSMultiQuoteDAO[insertQuoteContactPersons(masterDOB,connection)] -> "+sqEx.toString());
    throw new SQLException(sqEx.toString());
		
		}
  catch(Exception e)
		{
		
    logger.error(FILE_NAME+"QMSMultiQuoteDAO[insertQuoteContactPersons(masterDOB,connection)] -> "+e.toString());
			throw new SQLException(e.toString());
    
		}
		finally
		{
			try
			{
				ConnectionUtil.closeConnection(null,pStmt,null);
			}
			catch(Exception ex)
			{

      logger.error(FILE_NAME+"Finally : QMSMultiQuoteDAO[insertQuoteContactPersons(masterDOB,connection)]-> "+ex.toString());
			}
  }
}

/**
 * This method is used to updates the master info from QuoteMasterDOB to Quote Charge Group dtl table
* 
 * @param masterDOB 	an QuoteMasterDOB object that contains the master information of the quote
* 
 * @exception SQLException 
 */
private void updateQuoteChargeGroups(MultiQuoteFinalDOB finalDOB, long id, Connection connection,int laneno) throws SQLException 
{
PreparedStatement pStmt	= null;
       MultiQuoteMasterDOB    masterDOB   = finalDOB.getMasterDOB();
      MultiQuoteFreightLegSellRates legRateDetails = null;
try
{
	 legRateDetails =(MultiQuoteFreightLegSellRates)finalDOB.getLegDetails().get(laneno);
  pStmt = connection.prepareStatement(masterChargeGroupsDelQry);
  pStmt.clearParameters();
  pStmt.setLong(1,id);
  pStmt.executeUpdate();
  if(pStmt!=null)
   pStmt.close();
  
   
  if(legRateDetails.getChargeGroupIds()!=null)
    insertQuoteChargeGroups(finalDOB,connection,laneno);
}
catch(SQLException sqEx)
	{
  sqEx.printStackTrace();
	
  logger.error(FILE_NAME+"QMSMultiQuoteDAO[insertQuoteChargeGroups(masterDOB,connection)] -> "+sqEx.toString());
  throw new SQLException(sqEx.toString());
	}
catch(Exception e)
	{
	
  	logger.error(FILE_NAME+"QMSMultiQuoteDAO[insertQuoteChargeGroups(masterDOB,connection)] -> "+e.toString());
  throw new SQLException(e.toString());
	}
	finally
	{
		try
		{
			ConnectionUtil.closeConnection(null,pStmt,null);
		}
		catch(Exception ex)
		{
		
    logger.error(FILE_NAME+"Finally : QMSMultiQuoteDAO[insertQuoteChargeGroups(masterDOB,connection)]-> "+ex.toString());
    throw new SQLException(ex.toString());
		}
}
}

/**
 * This method is used to updates the master info from MultiQuoteMasterDOB to Quote Header footer table
* 
 * @param masterDOB 	an MultiQuoteMasterDOB object that contains the master information of the quote
* 
 * @exception SQLException 
 */
private void updateQuoteHeaderFooter(MultiQuoteMasterDOB masterDOB,long id, Connection connection) throws SQLException 
{
PreparedStatement pStmt	= null;
	//ResultSet         rs	  =	null;//Commented By RajKumari on 24-10-2008 for Connection Leakages.
try
{
  pStmt = connection.prepareStatement(masterHeaderFooterDelQry);
  pStmt.clearParameters();
  pStmt.setLong(1,id);
  pStmt.executeUpdate();
  if(pStmt!=null)
   pStmt.close();
   
  if(masterDOB.getHeaderFooter()!=null)
    insertQuoteHeaderFooter(masterDOB,connection);
}
catch(SQLException sqEx)
	{
  sqEx.printStackTrace();
		//Logger.error(FILE_NAME,"QMSQuoteDAO[insertQuoteHeaderFooter(masterDOB,connection)] -> "+sqEx.toString());
  logger.error(FILE_NAME+"QMSMultiQuoteDAO[insertQuoteHeaderFooter(masterDOB,connection)] -> "+sqEx.toString());
  throw new SQLException(sqEx.toString());
	}
catch(Exception e)
	{
		//Logger.error(FILE_NAME,"QMSQuoteDAO[insertQuoteHeaderFooter(masterDOB,connection)] -> "+e.toString());
  logger.error(FILE_NAME+"QMSMultiQuoteDAO[insertQuoteHeaderFooter(masterDOB,connection)] -> "+e.toString());
  throw new SQLException(e.toString());
	}
	finally
	{
		try
		{
			ConnectionUtil.closeConnection(null,pStmt,null);//Modified By RajKumari on 24-10-2008 for Connection Leakages.
		}
		catch(Exception ex)
		{
			//Logger.error(FILE_NAME,"Finally : QMSQuoteDAO[insertQuoteHeaderFooter(masterDOB,connection)]-> "+ex.toString());
    logger.error(FILE_NAME+"Finally : QMSQuoteDAO[insertQuoteHeaderFooter(masterDOB,connection)]-> "+ex.toString());
    throw new SQLException(ex.toString());
		}
}
}

private void updateSelectedRates(MultiQuoteFinalDOB finalDOB,long id,Connection connection,int laneno,int lane) throws SQLException
{
  
  PreparedStatement pStmt           = null;
  
  try
  {
    pStmt = connection.prepareStatement(selectedRatesDelQry);
    pStmt.clearParameters();
    pStmt.setLong(1,id);
    pStmt.executeUpdate();
    if(pStmt!=null)
     pStmt.close();
     
    insertSelectedRates(finalDOB,connection,laneno,lane); 
  }
  catch(Exception e)
  {
    //Logger.error(FILE_NAME,"Error in insertSelectedRates"+e);
    logger.error(FILE_NAME+"Error in insertSelectedRates"+e);
    e.printStackTrace();
    throw new SQLException(e.toString());
  }
  finally
  {
    ConnectionUtil.closeConnection(null,pStmt);
  }
}

private void updateNotes(Connection connection,long id,MultiQuoteFinalDOB finalDOB) throws SQLException
{
  PreparedStatement pStmt           = null;
  
  try
  {
    pStmt = connection.prepareStatement(notesDelQry);
    
    pStmt.clearParameters();
    
    pStmt.setLong(1,id);
    pStmt.executeUpdate();
    
    if(pStmt!=null)
     pStmt.close();
    
    insertNotes(connection,finalDOB);
  }
  catch(SQLException sql)
  {
    
    logger.error(FILE_NAME+"SQLException in updateNotes"+sql);
    sql.printStackTrace();
    throw new SQLException(sql.toString(),sql.getSQLState(),sql.getErrorCode());
  }
  catch(Exception e)
  {

    logger.error(FILE_NAME+"Exception in updateNotes"+e);
    e.printStackTrace();
    throw new SQLException(e.toString());
  }
  finally
  {
    ConnectionUtil.closeConnection(null,pStmt);
  }
}



public long getQuoteId(Connection connection)throws Exception
{
   PreparedStatement    pst                = null;
   ResultSet			rs				   = null;
   long                 quoteId=0l;          
   String               quoteIdQuery      = "SELECT QUOTE_MASTER_SEQ.NEXTVAL QuotyeID FROM DUAL"; 
   try
   {
     // connection         =   getConnection();
      pst                = connection.prepareStatement(quoteIdQuery);
      rs                 = pst.executeQuery();
      if(rs.next())
    	  quoteId = rs.getLong("QuotyeID");
      
   }
   catch(Exception e)
   {
      e.printStackTrace();
      throw new Exception();
   }
   finally
   {
      if(pst!=null)
        pst.close();
      if(rs!=null)	// Added by Gowtham on 03Feb2011 ConnectionLeaks Checking
    	  rs.close();
      //if(connection!=null)
        //connection.close(); 
   }
   return quoteId;
} 









public void setConfirmFlag(UpdatedQuotesReportDOB reportDOB) throws SQLException
{
   Connection            conn            =   null;
  PreparedStatement     pstmt           =   null;
  ResultSet             rs              =   null;
  int                   totalUpdated    =   0;
  int                   confirmedQuotes =   -1;
  try
  {
    conn    =   this.getConnection();
    pstmt   =   conn.prepareStatement("UPDATE QMS_QUOTE_MASTER SET ACTIVE_FLAG='I' WHERE ID=?");//@@Inactivating the Old Id
    if(reportDOB!=null)
    {
      pstmt.setLong(1,reportDOB.getUniqueId());
      pstmt.executeUpdate();
      
      if(pstmt!=null)
        pstmt.close();
    }
    
    pstmt   =   conn.prepareStatement("UPDATE QMS_QUOTES_UPDATED SET CONFIRM_FLAG='C' WHERE QUOTE_ID=? AND CHANGEDESC=? AND SELL_BUY_FLAG=?");
    
    if(reportDOB!=null)
    {
      pstmt.setLong(1,reportDOB.getUniqueId());
      pstmt.setString(2,reportDOB.getChangeDesc());
      pstmt.setString(3,reportDOB.getSellBuyFlag());
      
      pstmt.executeUpdate();
      
      if(pstmt!=null)
        pstmt.close();
      
      pstmt   =   conn.prepareStatement("UPDATE QMS_QUOTES_UPDATED SET QUOTE_ID=? WHERE QUOTE_ID=? AND CONFIRM_FLAG IS NULL");
      pstmt.setLong(1,reportDOB.getNewQuoteId());
      pstmt.setLong(2,reportDOB.getUniqueId());
    
      pstmt.executeUpdate();
      
      if(pstmt!=null)
        pstmt.close();
      
      pstmt  = conn.prepareStatement("SELECT COUNT(*)TOT_UPDATED FROM QMS_QUOTES_UPDATED WHERE CHANGEDESC=? AND SELL_BUY_FLAG=?");
      
      pstmt.setString(1,reportDOB.getChangeDesc());
      pstmt.setString(2,reportDOB.getSellBuyFlag());
      
      rs  = pstmt.executeQuery();
      
      if(rs.next())
        totalUpdated  = rs.getInt("TOT_UPDATED");
        
      if(rs!=null)
        rs.close();
      if(pstmt!=null)
        pstmt.close();
      
      pstmt  = conn.prepareStatement("SELECT COUNT(*)CONFIRMED FROM QMS_QUOTES_UPDATED WHERE CHANGEDESC=? AND SELL_BUY_FLAG=? AND CONFIRM_FLAG ='C'");
      
      pstmt.setString(1,reportDOB.getChangeDesc());
      pstmt.setString(2,reportDOB.getSellBuyFlag());
      
      rs  = pstmt.executeQuery();        
      
      if(rs.next())
        confirmedQuotes  = rs.getInt("CONFIRMED");
        
      if(totalUpdated==confirmedQuotes)
      {
        if(pstmt!=null)
          pstmt.close();
        
        pstmt = conn.prepareStatement("DELETE FROM QMS_QUOTES_UPDATED WHERE CHANGEDESC=? AND SELL_BUY_FLAG=?");
        pstmt.setString(1,reportDOB.getChangeDesc());
        pstmt.setString(2,reportDOB.getSellBuyFlag());
        pstmt.executeUpdate();
      }
      
    }
  }
  catch(SQLException sql)
  {
    //Logger.error(FILE_NAME,"SQLException in setConfirmFlag"+sql);
    logger.error(FILE_NAME+"SQLException in setConfirmFlag"+sql);
    sql.printStackTrace();
    throw new SQLException(sql.toString());
  }
  catch(Exception e)
  {
    //Logger.error(FILE_NAME,"Exception in setConfirmFlag"+e);
    logger.error(FILE_NAME+"Exception in setConfirmFlag"+e);
    e.printStackTrace();
    throw new SQLException(e.toString());
  }
  finally
  {
    ConnectionUtil.closeConnection(conn,pstmt,rs);
  }
}



private String getTerminalForLocation(Connection conn,String locationId) throws SQLException
{
  PreparedStatement         pStmt	      = null;
  ResultSet                 rs          = null;
  String                    terminalId  = null;
  try
  {
    pStmt         =       conn.prepareStatement(terminalQuery);
    
    pStmt.setString(1,locationId);
    
    rs            =       pStmt.executeQuery();
    
    if(rs.next())
      terminalId  =   rs.getString("TERMINALID");
  }
  catch(Exception e)
  {
    //Logger.error(FILE_NAME,"Error while getting Terminal Ids"+e);
    logger.error(FILE_NAME+"Error while getting Terminal Ids"+e);
    e.printStackTrace();
    throw new SQLException(e.toString());
  }
  finally
  {
    ConnectionUtil.closeConnection(null,pStmt,rs);
  }
  return terminalId;
}
//Added by Rakesh on 12-01-2011
public CostingMasterDOB getQuoteRateInfo(CostingHDRDOB costingHDRDOB,ESupplyGlobalParameters loginbean)throws Exception
{
    
        CallableStatement cStmt     = null;
        ResultSet         rs        = null;
        ResultSet         rs2 = null;//,rs3   = null;//Commented By RajKumari on 24-10-2008 for Connection Leakages.
        
        String    weightBreak       = null;
        String    rateType          = null;
        
        ArrayList legDetails        = null; 
        ArrayList chargeList        = null;
        ArrayList destChargeList    = null;
        ArrayList rateList          = null;
        ArrayList list_exNotes      = null;
        ArrayList contactPersonsIds = new ArrayList();
        ArrayList contactPersonsList= new ArrayList();
        ArrayList contactsMailList  = new ArrayList();
        ArrayList contactsFaxList   = new ArrayList();
        
        CostingLegDetailsDOB costingLegDetailsDOB = null;
        CostingChargeDetailsDOB costingChargeDetailsDOB = null;
        CostingChargeDetailsDOB delChargeDetailsDOB     = null;
        CostingRateInfoDOB costingRateInfoDOB = null;
        CostingMasterDOB  costingMasterDOB = null;
        
        Connection connection           = null;
        PreparedStatement pstmt         = null;
        PreparedStatement pstmt1 = null;//,pstmt2 = null;//Commented By RajKumari on 24-10-2008 for Connection Leakages.
        ResultSet rs1  = null;
        String  department ="";
        String laneNo=null;  //Added by Rakesh on 11-01-2011
 //@@ Added by subrahmanyam for Enhancement 167668 on 29/04/09     
     String                     custAddress1         =  null;
     String                     custAddress2         =  null;
     String                     custAddress3         =  null;
     String                     custAddrQry          =  null;
     String                     queryNoCustAddr      =  null;
//@@ Ended by subrahmanyam for Enhancement 167668 on 29/04/09     
        String selectMailid = " SELECT EMAILID FROM fs_usermaster WHERE USERID=? AND LOCATIONID = ?"; 
        
        /*String selectQuote = "SELECT QM.ORIGIN_LOCATION,(SELECT COUNTRYID FROM FS_FR_LOCATIONMASTER WHERE LOCATIONID = QM.ORIGIN_LOCATION) ORG_COUNTRY ,QM.DEST_LOCATION, (SELECT COUNTRYID FROM FS_FR_LOCATIONMASTER WHERE LOCATIONID = QM.DEST_LOCATION) DEST_COUNTRY,QM.INCO_TERMS_ID, "
                             +" (SELECT COMODITYDESCRIPTION FROM FS_FR_COMODITYMASTER CM WHERE QM.COMMODITY_ID = CM.COMODITYID) COMODITYDESCRIPTION,QM.CUSTOMER_ID,C.OPERATIONS_EMAILID,AD.FAX,AD.COUNTRYID,QM.VERSION_NO,"
                             +" DECODE((SELECT COUNT(*) FROM FS_RT_LEG LG, FS_RT_PLAN RP WHERE RP.QUOTE_ID=QM.QUOTE_ID  AND LG.RT_PLAN_ID=RP.RT_PLAN_ID), 1, to_char(QM.SHIPMENT_MODE), 'Multi-Mode') SHIPMENTMODE, "
                             +" C.COMPANYNAME,QM.MODIFIED_DATE,QM.EMAIL_FLAG,QM.FAX_FLAG,QM.PRINT_FLAG FROM QMS_QUOTE_MASTER QM,FS_FR_CUSTOMERMASTER C,FS_ADDRESS AD "
                             +" WHERE ID IN "
                             +" (SELECT ID FROM QMS_QUOTE_MASTER WHERE VERSION_NO="
                             +" (SELECT MAX(VERSION_NO) FROM QMS_QUOTE_MASTER WHERE QUOTE_ID=?) AND QUOTE_ID=?) AND QM.CUSTOMER_ID=C.CUSTOMERID AND AD.ADDRESSID=C.CUSTOMERADDRESSID";*/
        
        
          String selectQuote = "SELECT QM.ORIGIN_LOCATION,(SELECT COUNTRYID FROM FS_FR_LOCATIONMASTER WHERE LOCATIONID = QM.ORIGIN_LOCATION) ORG_COUNTRY ,QM.DEST_LOCATION, (SELECT COUNTRYID FROM FS_FR_LOCATIONMASTER WHERE LOCATIONID = QM.DEST_LOCATION) DEST_COUNTRY,QM.INCO_TERMS_ID, "
                             +" (SELECT COMODITYDESCRIPTION FROM FS_FR_COMODITYMASTER CM WHERE QM.COMMODITY_ID = CM.COMODITYID) COMODITYDESCRIPTION,QM.CUSTOMER_ID,C.OPERATIONS_EMAILID,AD.FAX,AD.COUNTRYID,QM.VERSION_NO,"
                             +" DECODE((SELECT COUNT(*) FROM FS_RT_LEG LG, FS_RT_PLAN RP WHERE RP.QUOTE_ID=QM.QUOTE_ID  AND LG.RT_PLAN_ID=RP.RT_PLAN_ID), 1, to_char(QM.SHIPMENT_MODE), 'Multi-Mode') SHIPMENTMODE, "
                             +" C.COMPANYNAME,QM.CREATED_TSTMP,QM.EMAIL_FLAG,QM.FAX_FLAG,QM.PRINT_FLAG FROM QMS_QUOTE_MASTER QM,FS_FR_CUSTOMERMASTER C,FS_ADDRESS AD "
                         +" WHERE ID IN " //Added by Mohan for issue no.219979 on 10122010
                             +" (SELECT ID FROM QMS_QUOTE_MASTER WHERE VERSION_NO="
                         +" (SELECT MAX(VERSION_NO) FROM QMS_QUOTE_MASTER WHERE QUOTE_ID=? AND MULTI_QUOTE_LANE_NO=?) AND QUOTE_ID=? AND MULTI_QUOTE_LANE_NO=?) AND QM.CUSTOMER_ID=C.CUSTOMERID AND AD.ADDRESSID=C.CUSTOMERADDRESSID";
     
        String countryNamesSQL    = "SELECT UPPER(COUNTRYNAME) ORIGIN,(SELECT UPPER(COUNTRYNAME) FROM FS_COUNTRYMASTER WHERE COUNTRYID = ?)"+
                                  " DEST FROM FS_COUNTRYMASTER WHERE COUNTRYID =?";
        
        String contactsQuery      = "SELECT DISTINCT CD.SL_NO, CD.CONTACTPERSON, CC.EMAILID, CC.FAX " +
                                    "  FROM QMS_CUST_CONTACTDTL  CD, " + 
                                    "       QMS_QUOTE_CONTACTDTL QC, " + 
                                    "       QMS_CUST_CONTACTDTL  CC " + 
                                    " WHERE QC.CUSTOMERID = CD.CUSTOMERID " + 
                                    "   AND CC.CUSTOMERID = QC.CUSTOMERID " + 
                                    "   AND QC.SL_NO = CD.SL_NO " + 
                                    "   AND CC.SL_NO = CD.SL_NO " + 
                                    "   AND QC.QUOTE_ID IN (SELECT ID " + 
                                    "                        FROM QMS_QUOTE_MASTER " + 
                                    "                       WHERE QUOTE_ID = ? " + 
                                "                         AND ACTIVE_FLAG = 'A' AND MULTI_QUOTE_LANE_NO=?) " + //Added by Mohan for issue no.219979 on 10122010
                                    " ORDER BY SL_NO";
        
        //String contactsMailQuery  =  "SELECT EMAILID,FAX FROM QMS_CUST_CONTACTDTL WHERE CUSTOMERID=? AND SL_NO=? ORDER BY SL_NO";
      
        /*String creatorDtlsQry     =   "SELECT (UM.USERNAME||',\n'||DM.DESCRIPTION)CREATOR FROM "+
                                      "FS_USERMASTER UM,QMS_DESIGNATION DM WHERE UM.DESIGNATION_ID=DM.DESIGNATION_ID AND UM.USERID=? AND UM.LOCATIONID=?";*/
       /*    String creatorDtlsQry     =  "SELECT U.USERNAME, U.DEPARTMENT, U.PHONE_NO,U.FAX_NO,U.MOBILE_NO, FSC.COMPANYNAME FROM "+
                                        "  FS_USERMASTER U,FS_COMPANYINFO FSC WHERE  U.USERID=? AND U.LOCATIONID = ?  AND FSC.COMPANYID =U.COMPANYID";*/                     
        String creatorDtlsQry     =  "SELECT U.USERNAME, U.DEPARTMENT, U.PHONE_NO,U.FAX_NO,U.MOBILE_NO, FSC.COMPANYNAME FROM "+
                                        "  FS_USERMASTER U,FS_COMPANYINFO FSC WHERE  U.USERID=? AND U.LOCATIONID = ?  AND FSC.COMPANYID =U.COMPANYID";                     
        /*String addressQry         =   "SELECT (AD.ADDRESSLINE1||'\n'||DECODE(AD.ADDRESSLINE2,null,'',AD.ADDRESSLINE2||'\n')||DECODE(AD.ADDRESSLINE3,NULL,'',AD.ADDRESSLINE3||'\n')||AD.CITY||'\n'||DECODE(AD.STATE,NULL,'',AD.STATE||'\n')"+
                                      "||DECODE(AD.ZIPCODE,NULL,'',AD.ZIPCODE||'\n')||UPPER(CO.COUNTRYNAME)||'\n'||AD.PHONENO||'\n'||AD.FAX)ADDRESS FROM FS_FR_TERMINALMASTER TM,FS_ADDRESS AD "+
                                      ",FS_COUNTRYMASTER CO WHERE TM.CONTACTADDRESSID=AD.ADDRESSID AND AD.COUNTRYID=CO.COUNTRYID AND TM.TERMINALID=?";*/
        /*String addressQry         =   "SELECT (AD.ADDRESSLINE1||'\n'||DECODE(AD.ADDRESSLINE2,null,'',AD.ADDRESSLINE2||'\n')||DECODE(AD.ADDRESSLINE3,NULL,'',AD.ADDRESSLINE3||'\n')||AD.CITY||'\n'||DECODE(AD.STATE,NULL,'',AD.STATE||'\n')"+
                                      "||DECODE(AD.ZIPCODE,NULL,'',AD.ZIPCODE||'\n')||UPPER(CO.COUNTRYNAME))ADDRESS FROM FS_FR_TERMINALMASTER TM,FS_ADDRESS AD "+
                                      ",FS_COUNTRYMASTER CO WHERE TM.CONTACTADDRESSID=AD.ADDRESSID AND AD.COUNTRYID=CO.COUNTRYID AND TM.TERMINALID=?";*/
    /*                                  
       String addressQry         =   "SELECT (AD.ADDRESSLINE1||'\n'||DECODE(AD.ADDRESSLINE2,null,'',AD.ADDRESSLINE2||'\n')||DECODE(AD.ADDRESSLINE3,NULL,'',AD.ADDRESSLINE3||'\n')||AD.CITY||'\n'||DECODE(AD.STATE,NULL,'',AD.STATE||'\n')"+
                                      "||DECODE(AD.ZIPCODE,NULL,'',AD.ZIPCODE||'\n')||UPPER(CO.COUNTRYNAME))ADDRESS FROM FS_FR_TERMINALMASTER TM,FS_ADDRESS AD "+
                                      ",FS_COUNTRYMASTER CO WHERE TM.CONTACTADDRESSID=AD.ADDRESSID AND AD.COUNTRYID=CO.COUNTRYID AND TM.TERMINALID=?";*/
         
          String addressQry         =   "SELECT (AD.ADDRESSLINE1||'\n\n'||DECODE(AD.ADDRESSLINE2,null,'',AD.ADDRESSLINE2||'\n\n')||DECODE(AD.ADDRESSLINE3,NULL,'',AD.ADDRESSLINE3||'\n\n')||AD.CITY||'\n'||DECODE(AD.STATE,NULL,'',AD.STATE||'\n')"+
                                      "||DECODE(AD.ZIPCODE,NULL,'',AD.ZIPCODE||'\n')||UPPER(CO.COUNTRYNAME))ADDRESS FROM FS_FR_TERMINALMASTER TM,FS_ADDRESS AD "+
                                      ",FS_COUNTRYMASTER CO WHERE TM.CONTACTADDRESSID=AD.ADDRESSID AND AD.COUNTRYID=CO.COUNTRYID AND TM.TERMINALID=?";
        String fromMailId = null;
        double sellRate = 0.0;
        java.text.DecimalFormat   deciFormat     = null;
        
        String customerId         =   null;
        int    sl_no              =   0;
        
        String[] contactsEmailIds   =   null;
        String[] contactsFax        =   null;
        String[] contactPersonNames =   null;
        String[] contactPersonIds   =   null;
        int  n ;
        String       breakPoint      = null;
        try
        {
          deciFormat = new java.text.DecimalFormat("##0.00");
          connection  = this.getConnection();
          
          pstmt = connection.prepareStatement(selectMailid);
          pstmt.setString(1,loginbean.getUserId());
          pstmt.setString(2,loginbean.getTerminalId());
          
          rs1   = pstmt.executeQuery();
          
          if(rs1.next())
          {
            fromMailId = rs1.getString("EMAILID");
          }
          
          if(rs1!=null)
          { rs1.close();}
          if(pstmt!=null)
          { pstmt.close();}
          
        //Added by Rakesh on 11-01-2011
          if(costingHDRDOB.getLaneNo()!=null){
        	  laneNo=costingHDRDOB.getLaneNo();   
          }else{
        	  laneNo="1";
          }
          pstmt = connection.prepareStatement(selectQuote);
          
          pstmt.setString(1,costingHDRDOB.getQuoteid());
      pstmt.setString(2,laneNo);//Modified by Rakesh for issue no.219979 on 10122010
      pstmt.setString(3,costingHDRDOB.getQuoteid());
      pstmt.setString(4,laneNo);//Modified by Rakesh for issue no.219979 on 10122010
          rs1 = pstmt.executeQuery();
          if(rs1.next())
          {
                  costingMasterDOB = new CostingMasterDOB();
                  
                  costingMasterDOB.setOrigin(rs1.getString("ORIGIN_LOCATION"));
                  costingMasterDOB.setDestination(rs1.getString("DEST_LOCATION"));
                  costingMasterDOB.setCustomerid(rs1.getString("CUSTOMER_ID"));
                  costingMasterDOB.setIncoterms(rs1.getString("INCO_TERMS_ID"));
                  costingMasterDOB.setCommodityType(rs1.getString("COMODITYDESCRIPTION"));
                  costingMasterDOB.setVersionNo(rs1.getString("VERSION_NO"));
                  costingMasterDOB.setEmailId(rs1.getString("OPERATIONS_EMAILID"));
                  costingMasterDOB.setOrginCountry(rs1.getString("ORG_COUNTRY"));
                  costingMasterDOB.setDestCountry(rs1.getString("DEST_COUNTRY"));
                  costingMasterDOB.setCustomerName(rs1.getString("COMPANYNAME"));
                //  costingMasterDOB.setDateOfQuotation(rs1.getTimestamp("MODIFIED_DATE"));
                  costingMasterDOB.setDateOfQuotation(rs1.getTimestamp("CREATED_TSTMP"));
                  costingMasterDOB.setEmailFlag(rs1.getString("EMAIL_FLAG"));
                  costingMasterDOB.setFaxFlag(rs1.getString("FAX_FLAG"));
                  costingMasterDOB.setPrintFlag(rs1.getString("PRINT_FLAG"));
                  costingMasterDOB.setCustomerFax(rs1.getString("FAX"));
                  costingMasterDOB.setCustomerCountryId(rs1.getString("COUNTRYID"));
                  
                  if(rs1.getString("SHIPMENTMODE").equalsIgnoreCase("1"))
                    costingMasterDOB.setShipmentMode("AIR");
                  else if(rs1.getString("SHIPMENTMODE").equalsIgnoreCase("2"))
                    costingMasterDOB.setShipmentMode("SEA");
                  else if(rs1.getString("SHIPMENTMODE").equalsIgnoreCase("4"))
                    costingMasterDOB.setShipmentMode("TRUCK");
                  else
                    costingMasterDOB.setShipmentMode(rs1.getString("SHIPMENTMODE"));
                  
                  costingMasterDOB.setFromMailId(fromMailId);
                  
                  pstmt1    =   connection.prepareStatement(countryNamesSQL);
                  
                  pstmt1.setString(1,costingMasterDOB.getDestCountry());
                  pstmt1.setString(2,costingMasterDOB.getOrginCountry());
                  
                  rs2       =   pstmt1.executeQuery();
                  
                  if(rs2.next())
                  {
                    costingMasterDOB.setOriginCountryName(rs2.getString("ORIGIN"));
                    costingMasterDOB.setDestCountryName(rs2.getString("DEST"));
                  }
                  if(rs2 != null)
                    rs2.close();
                  if(pstmt1!=null)
                    pstmt1.close();
                    
                  pstmt1    =   connection.prepareStatement(contactsQuery);
                  //pstmt2    =   connection.prepareStatement(contactsMailQuery);
                  
                  pstmt1.setString(1,costingHDRDOB.getQuoteid());
              pstmt1.setString(2,laneNo);//Modified by Rakesh for issue no.219979 on 10122010
                  
                  rs2       =   pstmt1.executeQuery();
                  
                  while(rs2.next())
                  {
                    /*pstmt2.setString(1,costingMasterDOB.getCustomerid());
                    pstmt2.setString(2,rs2.getString("SL_NO"));
                    
                    rs3 = pstmt2.executeQuery();*/
                    
                    /*if(rs3.next())
                    {*/
                      contactPersonsIds.add(rs2.getString("SL_NO"));
                      contactPersonsList.add(rs2.getString("CONTACTPERSON"));
                      contactsMailList.add(rs2.getString("EMAILID"));
                      contactsFaxList.add(rs2.getString("FAX"));
                    //}
                    
                    /*if(rs3!=null)
                      rs3.close();
                    pstmt2.clearParameters();*/
                  }
                  
                /*  if(pstmt2!=null)
                    pstmt2.close();*///Commented By RajKumari on 24-10-2008 for Connection Leakages.
                    
                  if(rs2!=null)
                    rs2.close();
                  if(pstmt1!=null)
                    pstmt1.close();
                  
                  if(contactsMailList!=null && contactsMailList.size()>0)
                  {
                    contactsEmailIds  = new String[contactsMailList.size()];
                    contactPersonIds  = new String[contactPersonsIds.size()];
                    contactPersonNames= new String[contactsMailList.size()];
                    contactsFax       = new String[contactsFaxList.size()];
                    int contMailSize	=	contactsMailList.size();
                    for(int i=0;i<contMailSize;i++)
                    {
                      contactPersonIds[i]   = (String)contactPersonsIds.get(i);
                      contactsEmailIds[i]   = (String)contactsMailList.get(i);
                      contactsFax[i]        = (String)contactsFaxList.get(i);
                      contactPersonNames[i] = (String)contactPersonsList.get(i);
                    }
                    costingMasterDOB.setContactPersonIds(contactPersonIds);
                    costingMasterDOB.setContactPersonNames(contactPersonNames);
                    costingMasterDOB.setContactEmailIds(contactsEmailIds);
                    costingMasterDOB.setContactsFax(contactsFax);
                  }
                  
                  pstmt1  = connection.prepareStatement(creatorDtlsQry);

                  pstmt1.setString(1,costingHDRDOB.getUserId());
                  pstmt1.setString(2,costingHDRDOB.getTerminalId());
                  
                  rs2     =  pstmt1.executeQuery();
                  
                  if(rs2.next())
                  {
                    //@@Added for the WPBN issue-61303
                    if(rs2.getString("DEPARTMENT")!=null)
                      department = rs2.getString("DEPARTMENT");
                    costingMasterDOB.setCreatorDetails(rs2.getString("USERNAME")+"\n"+department);
                    costingMasterDOB.setPhoneNo(rs2.getString("PHONE_NO"));
                    costingMasterDOB.setMobileNo((rs2.getString("MOBILE_NO")!=null)?rs2.getString("MOBILE_NO"):"");
                    costingMasterDOB.setFaxNo((rs2.getString("FAX_NO")!=null)?rs2.getString("FAX_NO"):"");
                    costingMasterDOB.setCompanyName((rs2.getString("COMPANYNAME")!=null)?rs2.getString("FAX_NO"):"");
                  //@@WPBN issue-61303
                  }
                  
                  if(rs2!=null)
                    rs2.close();
                  if(pstmt1!=null)
                    pstmt1.close();
//@@Commented by subrahmanyam for the Enhancement 167668 on 29/04/09                     
                 /* pstmt1   =    connection.prepareStatement(addressQry);
                  
                  pstmt1.setString(1,costingHDRDOB.getTerminalId());
                  
                  rs2       =   pstmt1.executeQuery();
                  
                  if(rs2.next())
                    costingMasterDOB.setTerminalAddress(rs2.getString("ADDRESS"));
                    if(rs2!=null)
                    rs2.close();
                  if(pstmt1!=null)
                    pstmt1.close();
               */   
//@@ Added by subrahmanyam for Enhanement 167668  on 29/04/09
      custAddrQry = " SELECT  CUST_ADDRLINE1 ,CUST_ADDRLINE2 ,CUST_ADDRLINE3  FROM FS_USERMASTER WHERE USERID=? AND LOCATIONID=? ";
      queryNoCustAddr = "SELECT AD.CITY||'\n'||DECODE(AD.STATE,NULL,'',AD.STATE||'\n')"+
                "||DECODE(AD.ZIPCODE,NULL,'',AD.ZIPCODE||'\n')||UPPER(CO.COUNTRYNAME)ADDRESS FROM FS_FR_TERMINALMASTER TM,FS_ADDRESS AD "+
                ",FS_COUNTRYMASTER CO WHERE TM.CONTACTADDRESSID=AD.ADDRESSID AND AD.COUNTRYID=CO.COUNTRYID AND TM.TERMINALID=?";
    pstmt1  = connection.prepareStatement(custAddrQry);  
    pstmt1.setString(1,costingHDRDOB.getUserId());
    pstmt1.setString(2,costingHDRDOB.getTerminalId());
    rs2    = pstmt1.executeQuery();
    if(rs2.next())
    {
      custAddress1=rs2.getString("CUST_ADDRLINE1");
      custAddress2=rs2.getString("CUST_ADDRLINE2");
      custAddress3=rs2.getString("CUST_ADDRLINE3");
    }
        if(rs2!=null)
          rs2.close();
        if(pstmt1!=null)
          pstmt1.close();
    if(custAddress1==null && custAddress2==null && custAddress3==null)
    {
         pstmt1  = connection.prepareStatement(addressQry);
        pstmt1.setString(1,costingHDRDOB.getTerminalId());
        rs2    = pstmt1.executeQuery();
        
        if(rs2.next())
        {
          costingMasterDOB.setTerminalAddress(rs2.getString("ADDRESS"));
          
        }
        if(rs2!=null)
          rs2.close();
        if(pstmt1!=null)
          pstmt1.close();
    }
    else
      {
           //    custAddress1=(custAddress1!=null?custAddress1+"\n":"")+(custAddress2!=null?custAddress2+"\n":"")+(custAddress3!=null?custAddress3+"\n":"");
         custAddress1=(custAddress1!=null?custAddress1+"\n\n":"")+(custAddress2!=null?custAddress2+"\n\n":"")+(custAddress3!=null?custAddress3+"\n\n":"");
            pstmt1  = connection.prepareStatement(queryNoCustAddr);
        pstmt1.setString(1,costingHDRDOB.getTerminalId());
        rs2    = pstmt1.executeQuery();
        
        if(rs2.next())
        {
          costingMasterDOB.setTerminalAddress(custAddress1+rs2.getString("ADDRESS"));
        }
        if(rs2!=null)
          rs2.close();
        if(pstmt1!=null)
          pstmt1.close();
      }
//@@ Ended by subrahmanyam for Enhancement 167668 on 29/04/09                  
                  
              //Modified by Mohan for issue no.219979 on 10122010
              cStmt = connection.prepareCall("{ call Qms_Quote_Pack.quote_view_proc(?,?,?,?,?,?) }");
                   
                   cStmt.clearParameters();
                  //cStmt.setLong(1,Long.parseLong(costingHDRDOB.getQuoteid()));// @@ commented by subrahmanyam  for the enhancement 146971 on 1/12/08
                  cStmt.setString(1,costingHDRDOB.getQuoteid());// @@ Added by subrahmanyam  for the enhancement 146971 on 1/12/08
              cStmt.setString(2,laneNo);//Added/Modified by Mohan for issue no.219979 on 10122010
              cStmt.registerOutParameter(3,OracleTypes.CURSOR);//Modified by Mohan for issue no.219979 on 10122010
              cStmt.registerOutParameter(4,OracleTypes.CURSOR);//Modified by Mohan for issue no.219979 on 10122010
              cStmt.registerOutParameter(5,OracleTypes.CURSOR);//Modified by Mohan for issue no.219979 on 10122010
              cStmt.registerOutParameter(6,OracleTypes.CURSOR);//Modified by Mohan for issue no.219979 on 10122010
                  cStmt.execute();
                  
                
                  
              rs  = (ResultSet)cStmt.getObject(3);//Modified by Mohan for issue no.219979 on 10122010
                  
     
                  
                  legDetails = new ArrayList();
                  //to get buysell rates//
              int i =0;
                  while(rs.next())
                  {
        
                  
                    if(costingLegDetailsDOB!=null && costingLegDetailsDOB.getLegSerialNo()==rs.getInt("LEG_SL_NO"))
                    {
                  
                          if((costingRateInfoDOB.getSellRateId()!=null && costingRateInfoDOB.getSellRateId().equals(rs.getString("SELLCHARGEID"))) || 
                            (costingRateInfoDOB.getBuyRateId()!=null && costingRateInfoDOB.getBuyRateId().equals(rs.getString("BUY_CHARGE_ID"))))//for FSC charges
                          {
                               weightBreak = costingChargeDetailsDOB.getBrkPoint();
                              rateType    = costingChargeDetailsDOB.getRateType();
                               costingRateInfoDOB = new CostingRateInfoDOB();
                                  if("A FREIGHT RATE".equalsIgnoreCase(rs.getString("RATE_DESCRIPTION")))
                                  costingRateInfoDOB.setRateDescription("FREIGHT RATE");
                              else  if("C.P.S.S".equalsIgnoreCase(rs.getString("RATE_DESCRIPTION")))
                                   costingRateInfoDOB.setRateDescription("P.S.S");
                              else
                                  costingRateInfoDOB.setRateDescription(rs.getString("RATE_DESCRIPTION"));//@@Added by Kameswari for Surcharge Enhancements
                          
                               costingRateInfoDOB.setWeightBreakSlab(rs.getString("CHARGESLAB"));
                           if(!("FREIGHT RATE".equalsIgnoreCase(costingRateInfoDOB.getRateDescription())))
                           {
                                if(costingRateInfoDOB.getWeightBreakSlab().equalsIgnoreCase("CSF")
                                		|| costingRateInfoDOB.getWeightBreakSlab().contains("CSF"))
                               {
                                   costingChargeDetailsDOB.setChargeBasis("Per Shipment");
                               }
                              else if(costingRateInfoDOB.getWeightBreakSlab().endsWith("CAF")||costingRateInfoDOB.getWeightBreakSlab().endsWith("BAF")
                               ||costingRateInfoDOB.getWeightBreakSlab().endsWith("CSF")||costingRateInfoDOB.getWeightBreakSlab().endsWith("PSS")||costingRateInfoDOB.getWeightBreakSlab().endsWith("caf")
                               ||costingRateInfoDOB.getWeightBreakSlab().endsWith("baf")||costingRateInfoDOB.getWeightBreakSlab().endsWith("csf")||costingRateInfoDOB.getWeightBreakSlab().endsWith("pss")
                               || (costingRateInfoDOB.getWeightBreakSlab().length()>5  && "LF".equalsIgnoreCase(costingRateInfoDOB.getWeightBreakSlab().substring(3, 5))))
                               {
                                    if(costingRateInfoDOB.getWeightBreakSlab().endsWith("CAF")||costingRateInfoDOB.getWeightBreakSlab().endsWith("caf"))
                                   {
                                      costingChargeDetailsDOB.setChargeBasis("Percent of Freight");
                                   }
                                   else
                                   {
                                     costingChargeDetailsDOB.setChargeBasis("Per Container");
                                   }
                               }
                               else if(costingRateInfoDOB.getWeightBreakSlab().equalsIgnoreCase("BAFMIN")||costingRateInfoDOB.getWeightBreakSlab().equalsIgnoreCase("CAFMIN")
                               ||costingRateInfoDOB.getWeightBreakSlab().equalsIgnoreCase("PSSMIN")||costingRateInfoDOB.getWeightBreakSlab().equalsIgnoreCase("FSMIN")||costingRateInfoDOB.getWeightBreakSlab().equalsIgnoreCase("SSMIN")
                                || costingRateInfoDOB.getWeightBreakSlab().endsWith("MIN") || "MIN".equalsIgnoreCase(costingRateInfoDOB.getWeightBreakSlab()))
                               {
                                  costingChargeDetailsDOB.setChargeBasis("Per Shipment");
                               }
                               else if(costingRateInfoDOB.getWeightBreakSlab().equalsIgnoreCase("BAFM3")||costingRateInfoDOB.getWeightBreakSlab().equalsIgnoreCase("CAF%")
                                  ||costingRateInfoDOB.getWeightBreakSlab().equalsIgnoreCase("PSSM3") || costingRateInfoDOB.getWeightBreakSlab().endsWith("M3"))
                               {
                                  if(costingRateInfoDOB.getWeightBreakSlab().equalsIgnoreCase("BAFM3")||costingRateInfoDOB.getWeightBreakSlab().equalsIgnoreCase("PSSM3") 
                                		  || costingRateInfoDOB.getWeightBreakSlab().endsWith("M3"))
                                  {
                                       costingChargeDetailsDOB.setChargeBasis("per Cubic Meter");
                                  }
                                  else
                                  {
                                      costingChargeDetailsDOB.setChargeBasis("Percent of Freight");
                                  }
                                }
                              
                               else if(costingRateInfoDOB.getWeightBreakSlab().equalsIgnoreCase("FSBASIC")||costingRateInfoDOB.getWeightBreakSlab().equalsIgnoreCase("SSBASIC")
                            		   || costingRateInfoDOB.getWeightBreakSlab().endsWith("BASIC"))
                               {
                                   costingChargeDetailsDOB.setChargeBasis("Per Shipment");
                               }
                                else if(costingRateInfoDOB.getWeightBreakSlab().equalsIgnoreCase("FSKG")||costingRateInfoDOB.getWeightBreakSlab().equalsIgnoreCase("SSKG")
                                		|| costingRateInfoDOB.getWeightBreakSlab().equalsIgnoreCase("FLAT") || isNumber(costingRateInfoDOB.getWeightBreakSlab()))
                               {
                                   costingChargeDetailsDOB.setChargeBasis("Per Kg");
                               }
                               else if(costingRateInfoDOB.getWeightBreakSlab().equalsIgnoreCase("SURCHARGE"))
                               {
                                   costingChargeDetailsDOB.setChargeBasis("Percent of Freight");
                               }
                               else
                            	   costingChargeDetailsDOB.setChargeBasis("Per Kg");
                           }
                           else
                           {
                               if("SLAB".equalsIgnoreCase(weightBreak) && "FLAT".equalsIgnoreCase(rateType))
                                  costingChargeDetailsDOB.setChargeBasisDesc("Per Shipment");
                             
                              else
                              {
                                if("1".equalsIgnoreCase(costingChargeDetailsDOB.getShipmentMode()) && "LIST".equalsIgnoreCase(rs.getString("WEIGHT_BREAK")))
                                {
                                  costingChargeDetailsDOB.setChargeBasisDesc("Per ULD");
                                }
                                else if(("2".equalsIgnoreCase(costingChargeDetailsDOB.getShipmentMode())||"4".equalsIgnoreCase(costingChargeDetailsDOB.getShipmentMode())) && "LIST".equalsIgnoreCase(rs.getString("WEIGHT_BREAK")))
                                {
                                  costingChargeDetailsDOB.setChargeBasisDesc("Per Container");
                                }
                                else if("Per Kg".equalsIgnoreCase(rs.getString("CHARGEBASIS")))
                                {
                                  costingChargeDetailsDOB.setChargeBasisDesc("Per Kilogram");
                                }
                                else if("Per Lb".equalsIgnoreCase(rs.getString("CHARGEBASIS")))
                                {
                                  
                                  costingChargeDetailsDOB.setChargeBasisDesc("Per Pound");
                                }
                                else if("Per CBM".equalsIgnoreCase(rs.getString("CHARGEBASIS")))
                                {
                                  costingChargeDetailsDOB.setChargeBasisDesc("Per Cubic Meter");
                                }
                                else if("Per CFT".equalsIgnoreCase(rs.getString("CHARGEBASIS")))
                                {
                                  costingChargeDetailsDOB.setChargeBasisDesc("Per Cubic Feet");
                                }
                                else
                                  costingChargeDetailsDOB.setChargeBasisDesc(rs.getString("CHARGEBASIS"));
                              }
                            
                           }
                                costingRateInfoDOB.setSellRateId(rs.getString("SELLCHARGEID"));
                               costingRateInfoDOB.setBuyRateId(rs.getString("BUY_CHARGE_ID"));
                               
                               costingRateInfoDOB.setLowerBound(rs.getDouble("LBOUND"));
                               costingRateInfoDOB.setUpperBound(rs.getDouble("UBOUND"));

                         if("FREIGHT RATE".equalsIgnoreCase(costingRateInfoDOB.getRateDescription()))
                         
                               costingRateInfoDOB.setRateIndicator(rs.getString("RATE_INDICATOR"));
                        else
                              costingRateInfoDOB.setRateIndicator("");
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                //Logger.info(FILE_NAME,""+rs.getString("CHARGESLAB"));
                             if("M".equalsIgnoreCase(costingChargeDetailsDOB.getMarginDiscountType()) || costingChargeDetailsDOB.getMarginDiscountType()==null)
                              {  
                                costingRateInfoDOB.setMargin(rs.getDouble("MARGINVALUE"));
                                costingRateInfoDOB.setMarginType(rs.getString("MARGIN_TYPE"));
                                
                                if("A".equalsIgnoreCase(rs.getString("MARGIN_TYPE")))
                                  sellRate  = rs.getDouble("BUYRATE")+rs.getDouble("MARGINVALUE");
                                else if("P".equalsIgnoreCase(rs.getString("MARGIN_TYPE")))
                                  sellRate  = rs.getDouble("BUYRATE")+(rs.getDouble("BUYRATE")*rs.getDouble("MARGINVALUE")/100);
                              }
                              else
                              {
                                costingRateInfoDOB.setDiscount(rs.getDouble("MARGINVALUE"));
                                costingRateInfoDOB.setDiscountType(rs.getString("MARGIN_TYPE"));
                                
                                if("A".equalsIgnoreCase(rs.getString("MARGIN_TYPE")))
                                  sellRate  = rs.getDouble("SELLRATE")-rs.getDouble("MARGINVALUE");
                                else if("P".equalsIgnoreCase(rs.getString("MARGIN_TYPE")))
                                  sellRate  = rs.getDouble("SELLRATE")-(rs.getDouble("SELLRATE")*rs.getDouble("MARGINVALUE")/100);
                              }
                         
                              costingRateInfoDOB.setRate(Double.parseDouble( deciFormat.format(sellRate)));
                                  rateList.add(costingRateInfoDOB);             
                       
                          if(rs.getString("CURRENCY")!=null)
                        	  costingRateInfoDOB.setMutilQuoteCurrency(rs.getString("CURRENCY"));
                          else
                        	  costingRateInfoDOB.setMutilQuoteCurrency(loginbean.getCurrencyId());
                        }else
                        {
                              costingChargeDetailsDOB = new CostingChargeDetailsDOB();
                              chargeList.add(costingChargeDetailsDOB);
                              
                              if(rs.getString("CURRENCY")!=null)
                                costingChargeDetailsDOB.setCurrency(rs.getString("CURRENCY"));
                              else
                                costingChargeDetailsDOB.setCurrency(loginbean.getCurrencyId());
                              
                              costingChargeDetailsDOB.setWeightBreak(rs.getString("WEIGHT_BREAK"));
                              costingChargeDetailsDOB.setRateType((rs.getString("RATE_TYPE")!=null && !"".equals(rs.getString("RATE_TYPE")))?rs.getString("RATE_TYPE"):rs.getString("WEIGHT_BREAK"));
                              costingChargeDetailsDOB.setWeightClass(rs.getString("WEIGHT_SCALE"));
                              costingChargeDetailsDOB.setDensityRatio(rs.getString("DENSITY_RATIO"));
                              
                              costingChargeDetailsDOB.setShipmentMode(rs.getString("SHMODE"));
                              
                              weightBreak = costingChargeDetailsDOB.getWeightBreak();
                              rateType    = costingChargeDetailsDOB.getRateType();
                              String breakpoint = costingChargeDetailsDOB.getBrkPoint();
                            //@@Commented by Kameswari for Surcharge Enhancements
                        costingChargeDetailsDOB.setPrimaryBasis(rs.getString("PRIMARY_BASIS"));
                              
                              costingChargeDetailsDOB.setMarginDiscountType(rs.getString("MARGIN_DISCOUNT_FLAG"));
                               costingChargeDetailsDOB.setTertiaryBasis("Chargeable");
                              rateList = new ArrayList();
                              
                              costingRateInfoDOB = new CostingRateInfoDOB();
                          
                          if(rs.getString("CURRENCY")!=null)
                        	  costingRateInfoDOB.setMutilQuoteCurrency(rs.getString("CURRENCY"));
                          else
                        	  costingRateInfoDOB.setMutilQuoteCurrency(loginbean.getCurrencyId());
                          
                            if("A FREIGHT RATE".equalsIgnoreCase(rs.getString("RATE_DESCRIPTION"))
                            ||"".equalsIgnoreCase(rs.getString("RATE_DESCRIPTION")))
                                 costingRateInfoDOB.setRateDescription("FREIGHT RATE");
                           else   if("C.P.S.S".equalsIgnoreCase(rs.getString("RATE_DESCRIPTION")))
                                 costingRateInfoDOB.setRateDescription("P.S.S");
                            else
                                 costingRateInfoDOB.setRateDescription(rs.getString("RATE_DESCRIPTION"));//@@Added by Kameswari for Surcharge Enhancements
                
                              costingRateInfoDOB.setWeightBreakSlab(rs.getString("CHARGESLAB"));
                           if(!("FREIGHT RATE".equalsIgnoreCase(costingRateInfoDOB.getRateDescription())))
                           {
                               if(costingRateInfoDOB.getWeightBreakSlab().equalsIgnoreCase("CSF"))
                               {
                                   costingChargeDetailsDOB.setChargeBasis("Per Shipment");
                               }
                              else if(costingRateInfoDOB.getWeightBreakSlab().endsWith("CAF")||costingRateInfoDOB.getWeightBreakSlab().endsWith("BAF")
                               ||costingRateInfoDOB.getWeightBreakSlab().endsWith("CSF")||costingRateInfoDOB.getWeightBreakSlab().endsWith("PSS")||costingRateInfoDOB.getWeightBreakSlab().endsWith("caf")
                               ||costingRateInfoDOB.getWeightBreakSlab().endsWith("baf")||costingRateInfoDOB.getWeightBreakSlab().endsWith("csf")||costingRateInfoDOB.getWeightBreakSlab().endsWith("pss")
                               || (costingRateInfoDOB.getWeightBreakSlab().length()>5  && "LF".equalsIgnoreCase(costingRateInfoDOB.getWeightBreakSlab().substring(3, 5))))
                               {
                                    if(costingRateInfoDOB.getWeightBreakSlab().endsWith("CAF")||costingRateInfoDOB.getWeightBreakSlab().endsWith("caf"))
                                   {
                                      costingChargeDetailsDOB.setChargeBasis("Percent of Freight");
                                   }
                                   else
                                   {
                                     costingChargeDetailsDOB.setChargeBasis("Per Container");
                                   }
                                  }
                               else if(costingRateInfoDOB.getWeightBreakSlab().equalsIgnoreCase("BAFMIN")||costingRateInfoDOB.getWeightBreakSlab().equalsIgnoreCase("CAFMIN")
                               ||costingRateInfoDOB.getWeightBreakSlab().equalsIgnoreCase("PSSMIN")||costingRateInfoDOB.getWeightBreakSlab().equalsIgnoreCase("FSMIN")||costingRateInfoDOB.getWeightBreakSlab().equalsIgnoreCase("SSMIN")
                               || costingRateInfoDOB.getWeightBreakSlab().equalsIgnoreCase("MIN"))
                               {
                                   costingChargeDetailsDOB.setChargeBasis("Per Shipment");
                               }
                               else if(costingRateInfoDOB.getWeightBreakSlab().equalsIgnoreCase("BAFM3")||costingRateInfoDOB.getWeightBreakSlab().equalsIgnoreCase("CAF%")
                                  ||costingRateInfoDOB.getWeightBreakSlab().equalsIgnoreCase("PSSM3")
                                  || "2".equalsIgnoreCase(costingChargeDetailsDOB.getShipmentMode()) ||costingRateInfoDOB.getWeightBreakSlab().endsWith("FLAT") )
                               {
                                  if(costingRateInfoDOB.getWeightBreakSlab().equalsIgnoreCase("BAFM3")||costingRateInfoDOB.getWeightBreakSlab().equalsIgnoreCase("PSSM3")
                                		  || costingRateInfoDOB.getWeightBreakSlab().endsWith("FLAT"))
                                  {
                                       costingChargeDetailsDOB.setChargeBasis("per Cubic Meter");
                                  }
                                  else
                                  {
                                      costingChargeDetailsDOB.setChargeBasis("Percent of Freight");
                                  }
                               }
                              
                               else if(costingRateInfoDOB.getWeightBreakSlab().equalsIgnoreCase("FSBASIC")||costingRateInfoDOB.getWeightBreakSlab().equalsIgnoreCase("SSBASIC")
                            		   ||costingRateInfoDOB.getWeightBreakSlab().endsWith("BASIC"))
                               {
                                    costingChargeDetailsDOB.setChargeBasis("Per Shipment");
                               }
                                else if(costingRateInfoDOB.getWeightBreakSlab().equalsIgnoreCase("FSKG")||costingRateInfoDOB.getWeightBreakSlab().equalsIgnoreCase("SSKG")
                                		|| isNumber(costingRateInfoDOB.getWeightBreakSlab()))
                               {
                                   costingChargeDetailsDOB.setChargeBasis("Per Kg");
                               }
                               else if(costingRateInfoDOB.getWeightBreakSlab().equalsIgnoreCase("SURCHARGE"))
                               {
                                   costingChargeDetailsDOB.setChargeBasis("Percent of Freight");
                               }
                           }
                           else
                           {
                               if("SLAB".equalsIgnoreCase(weightBreak) && "FLAT".equalsIgnoreCase(rateType))
                                  costingChargeDetailsDOB.setChargeBasisDesc("Per Shipment");
                             
                              else
                              {
                                if("1".equalsIgnoreCase(costingChargeDetailsDOB.getShipmentMode()) && "LIST".equalsIgnoreCase(rs.getString("WEIGHT_BREAK")))
                                {
                                  costingChargeDetailsDOB.setChargeBasisDesc("Per ULD");
                                }
                                else if(("2".equalsIgnoreCase(costingChargeDetailsDOB.getShipmentMode())||"4".equalsIgnoreCase(costingChargeDetailsDOB.getShipmentMode())) && "LIST".equalsIgnoreCase(rs.getString("WEIGHT_BREAK")))
                                {
                                  costingChargeDetailsDOB.setChargeBasisDesc("Per Container");
                                }
                                else if("Per Kg".equalsIgnoreCase(rs.getString("CHARGEBASIS")))
                                {
                                  costingChargeDetailsDOB.setChargeBasisDesc("Per Kilogram");
                                }
                                else if("Per Lb".equalsIgnoreCase(rs.getString("CHARGEBASIS")))
                                {
                                  
                                  costingChargeDetailsDOB.setChargeBasisDesc("Per Pound");
                                }
                                else if("Per CBM".equalsIgnoreCase(rs.getString("CHARGEBASIS")))
                                {
                                  costingChargeDetailsDOB.setChargeBasisDesc("Per Cubic Meter");
                                }
                                else if("Per CFT".equalsIgnoreCase(rs.getString("CHARGEBASIS")))
                                {
                                  costingChargeDetailsDOB.setChargeBasisDesc("Per Cubic Feet");
                                }
                                else
                                  costingChargeDetailsDOB.setChargeBasisDesc(rs.getString("CHARGEBASIS"));
                              }
                            
                           }   
                               
                              //costingRateInfoDOB.setRate(rs.getDouble("SELLRATE"));
                               costingRateInfoDOB.setLowerBound(rs.getDouble("LBOUND"));
                              costingRateInfoDOB.setUpperBound(rs.getDouble("UBOUND"));
                             if("FREIGHT RATE".equalsIgnoreCase(costingRateInfoDOB.getRateDescription()))
                            costingRateInfoDOB.setRateIndicator(rs.getString("RATE_INDICATOR"));
                            else
                            costingRateInfoDOB.setRateIndicator("");
                                costingRateInfoDOB.setSellRateId(rs.getString("SELLCHARGEID"));
                              costingRateInfoDOB.setBuyRateId(rs.getString("BUY_CHARGE_ID"));
                              
                              if("M".equalsIgnoreCase(costingChargeDetailsDOB.getMarginDiscountType()) || costingChargeDetailsDOB.getMarginDiscountType()==null)
                              {  
                                costingRateInfoDOB.setMargin(rs.getDouble("MARGINVALUE"));
                                costingRateInfoDOB.setMarginType(rs.getString("MARGIN_TYPE"));
                                
                                if("A".equalsIgnoreCase(rs.getString("MARGIN_TYPE")))
                                  sellRate  = rs.getDouble("BUYRATE")+rs.getDouble("MARGINVALUE");
                                else if("P".equalsIgnoreCase(rs.getString("MARGIN_TYPE")))
                                  sellRate  = rs.getDouble("BUYRATE")+(rs.getDouble("BUYRATE")*rs.getDouble("MARGINVALUE")/100);
                              }
                             else
                              {
                                costingRateInfoDOB.setDiscount(rs.getDouble("MARGINVALUE"));
                                costingRateInfoDOB.setDiscountType(rs.getString("MARGIN_TYPE"));
                                
                                if("A".equalsIgnoreCase(rs.getString("MARGIN_TYPE")))
                                  sellRate  = rs.getDouble("SELLRATE")-rs.getDouble("MARGINVALUE");
                                else if("P".equalsIgnoreCase(rs.getString("MARGIN_TYPE")))
                                  sellRate  = rs.getDouble("SELLRATE")-(rs.getDouble("SELLRATE")*rs.getDouble("MARGINVALUE")/100);
                              }
                              costingRateInfoDOB.setRate(Double.parseDouble( deciFormat.format(sellRate)));
                              rateList.add(costingRateInfoDOB);
                              costingChargeDetailsDOB.setCostingRateInfoDOB(rateList);                 
                        }
                    }
                    else
                    {
                    costingLegDetailsDOB = new CostingLegDetailsDOB();
                      legDetails.add(costingLegDetailsDOB);
                      
                      chargeList           = new ArrayList();
                      
                      costingLegDetailsDOB.setCostingChargeDetailList(chargeList);
                      
                  
                      costingLegDetailsDOB.setOrigin(rs.getString("ORG"));
                      costingLegDetailsDOB.setDestination(rs.getString("DEST"));
                      costingLegDetailsDOB.setLegSerialNo(rs.getInt("LEG_SL_NO"));
                      costingLegDetailsDOB.setFrequency((rs.getString("FREQUENCY")!=null)?rs.getString("FREQUENCY"):"");//@@ MOdified by kameswari for the wpbn issue 174425 on 24-jun-09
                      costingLegDetailsDOB.setTransitTime((rs.getString("TRANSITTIME")!=null)?rs.getString("TRANSITTIME"):"");
                      costingLegDetailsDOB.setNotes(rs.getString("NOTES"));
                      costingLegDetailsDOB.setFrequencyChecked(rs.getString("FREQUENCY_CHECKED"));
                     costingLegDetailsDOB.setTransitChecked(rs.getString("TRANSITTIME_CHECKED"));
                    
                       if(rs.getString("CARRIER")!=null&&(rs.getString("CARRIER_CHECKED")!=null&&"on".equalsIgnoreCase(rs.getString("CARRIER_CHECKED"))))
                      {
                           costingLegDetailsDOB.setCarrierName(rs.getString("CARRIER"));
                      }
                     if(rs.getTimestamp("VALIDUPTO")!=null&&(rs.getString("RATEVALIDITY_CHECKED")!=null&&"on".equalsIgnoreCase(rs.getString("RATEVALIDITY_CHECKED"))))
                      {
                       costingLegDetailsDOB.setRateValidity(rs.getTimestamp("VALIDUPTO"));
                      }
                     /* if(rs.getTimestamp("VALIDUPTO")!=null)
                      {
                       costingLegDetailsDOB.setRateValidity(rs.getTimestamp("VALIDUPTO"));
                      }*/
                       costingChargeDetailsDOB = new CostingChargeDetailsDOB();
                      chargeList.add(costingChargeDetailsDOB);
                      if(rs.getString("CURRENCY")!=null)
                        costingChargeDetailsDOB.setCurrency(rs.getString("CURRENCY"));
                      else
                        costingChargeDetailsDOB.setCurrency(loginbean.getCurrencyId());
                        
                      costingChargeDetailsDOB.setWeightBreak(rs.getString("WEIGHT_BREAK"));
                      costingChargeDetailsDOB.setRateType((rs.getString("RATE_TYPE")!=null && !"".equals(rs.getString("RATE_TYPE")))?rs.getString("RATE_TYPE"):rs.getString("WEIGHT_BREAK"));
                      costingChargeDetailsDOB.setWeightClass(rs.getString("WEIGHT_SCALE"));
                      costingChargeDetailsDOB.setDensityRatio(rs.getString("DENSITY_RATIO"));
                      
                      costingChargeDetailsDOB.setShipmentMode(rs.getString("SHMODE"));
                               //costingChargeDetailsDOB.setRateDescription(rs.getString("RATE_DESCRIPTION"));//@@Added by kameswari for Surcharge Enhancements
                          //costingChargeDetailsDOB.setChargeBasisDesc(rs.getString("CHARGEBASIS"));
                      weightBreak = costingChargeDetailsDOB.getWeightBreak();
                      rateType    = costingChargeDetailsDOB.getRateType();
                        String breakpoint = costingChargeDetailsDOB.getBrkPoint();
                    
                       costingChargeDetailsDOB.setPrimaryBasis(rs.getString("PRIMARY_BASIS"));
                      
                      costingChargeDetailsDOB.setMarginDiscountType(rs.getString("MARGIN_DISCOUNT_FLAG"));
                      
                      costingChargeDetailsDOB.setTertiaryBasis("Chargeable");//Default is chargable incase of frieght
                      
                      rateList = new ArrayList();
        
                      costingRateInfoDOB = new CostingRateInfoDOB();
                      costingRateInfoDOB.setWeightBreakSlab(rs.getString("CHARGESLAB"));
                       if("A FREIGHT RATE".equalsIgnoreCase(rs.getString("RATE_DESCRIPTION"))
                            ||"".equalsIgnoreCase(rs.getString("RATE_DESCRIPTION")))
                          costingRateInfoDOB.setRateDescription("FREIGHT RATE");
                    else  if("C.P.S.S".equalsIgnoreCase(rs.getString("RATE_DESCRIPTION")))
                            costingRateInfoDOB.setRateDescription("P.S.S");
                     else
                         costingRateInfoDOB.setRateDescription(rs.getString("RATE_DESCRIPTION"));//@@Added by Kameswari for Surcharge Enhancements
             
                    if(!("FREIGHT RATE".equalsIgnoreCase(costingRateInfoDOB.getRateDescription())))
                           {
                     if(costingRateInfoDOB.getWeightBreakSlab().equalsIgnoreCase("CSF"))
                     {
                              //     costingRateInfoDOB.setWeightBreakSlab("Absolute");
                                   costingChargeDetailsDOB.setChargeBasis("Per Shipment");
                               }
                    else if(costingRateInfoDOB.getWeightBreakSlab().endsWith("CAF")||costingRateInfoDOB.getWeightBreakSlab().endsWith("BAF")
                     ||costingRateInfoDOB.getWeightBreakSlab().endsWith("CSF")||costingRateInfoDOB.getWeightBreakSlab().endsWith("PSS")||costingRateInfoDOB.getWeightBreakSlab().endsWith("caf")
                     ||costingRateInfoDOB.getWeightBreakSlab().endsWith("baf")||costingRateInfoDOB.getWeightBreakSlab().endsWith("csf")||costingRateInfoDOB.getWeightBreakSlab().endsWith("pss")
                     || (costingRateInfoDOB.getWeightBreakSlab().length() > 5 && "LF".equalsIgnoreCase(costingRateInfoDOB.getWeightBreakSlab().substring(3, 5)) ))
                     {
                          if(costingRateInfoDOB.getWeightBreakSlab().endsWith("CAF")||costingRateInfoDOB.getWeightBreakSlab().endsWith("caf"))
                         {
                            costingChargeDetailsDOB.setChargeBasis("Percent of Freight");
                         }
                         else
                         {
                           costingChargeDetailsDOB.setChargeBasis("Per Container");
                         }
                      }
                     else if(costingRateInfoDOB.getWeightBreakSlab().equalsIgnoreCase("BAFMIN")||costingRateInfoDOB.getWeightBreakSlab().equalsIgnoreCase("CAFMIN")
                    		 ||costingRateInfoDOB.getWeightBreakSlab().equalsIgnoreCase("PSSMIN")
                    		 ||costingRateInfoDOB.getWeightBreakSlab().equalsIgnoreCase("FSMIN")||
                    		 	costingRateInfoDOB.getWeightBreakSlab().equalsIgnoreCase("SSMIN") || costingRateInfoDOB.getWeightBreakSlab().endsWith("MIN")
                    		 	|| costingRateInfoDOB.getWeightBreakSlab().endsWith("MIN"))
                     {
                        //costingRateInfoDOB.setWeightBreakSlab("Minimum");
                        costingChargeDetailsDOB.setChargeBasis("Per Shipment");
                     }
                     else if(costingRateInfoDOB.getWeightBreakSlab().equalsIgnoreCase("BAFM3")||costingRateInfoDOB.getWeightBreakSlab().equalsIgnoreCase("CAF%")
                        ||costingRateInfoDOB.getWeightBreakSlab().equalsIgnoreCase("PSSM3") || costingRateInfoDOB.getWeightBreakSlab().endsWith("FLAT")
                        ||( "2".equalsIgnoreCase(costingChargeDetailsDOB.getShipmentMode()) && costingRateInfoDOB.getWeightBreakSlab().endsWith("FLAT")))
                     {
                        if(costingRateInfoDOB.getWeightBreakSlab().equalsIgnoreCase("BAFM3")||costingRateInfoDOB.getWeightBreakSlab().equalsIgnoreCase("PSSM3")
                        		|| costingRateInfoDOB.getWeightBreakSlab().equalsIgnoreCase("M3") || costingRateInfoDOB.getWeightBreakSlab().endsWith("FLAT"))
                        {
                             costingChargeDetailsDOB.setChargeBasis("per Cubic Meter");
                        }
                        else
                        {
                            costingChargeDetailsDOB.setChargeBasis("Percent of Freight");
                        }
                        //costingRateInfoDOB.setWeightBreakSlab("Or");
                     }
                       
                               else if(costingRateInfoDOB.getWeightBreakSlab().equalsIgnoreCase("FSBASIC")||costingRateInfoDOB.getWeightBreakSlab().equalsIgnoreCase("SSBASIC")
                            		   	|| costingRateInfoDOB.getWeightBreakSlab().endsWith("BASIC"))
                               {
                                  // costingRateInfoDOB.setWeightBreakSlab("Basic");
                                   costingChargeDetailsDOB.setChargeBasis("Per Shipment");
                               }
                                else if(costingRateInfoDOB.getWeightBreakSlab().equalsIgnoreCase("FSKG")||costingRateInfoDOB.getWeightBreakSlab().equalsIgnoreCase("SSKG")
                                		|| costingRateInfoDOB.getWeightBreakSlab().endsWith("FLAT") || isNumber(costingRateInfoDOB.getWeightBreakSlab()))
                               {
                                  // costingRateInfoDOB.setWeightBreakSlab("Flat");
                                   costingChargeDetailsDOB.setChargeBasis("Per Kg");
                               }
                               else if(costingRateInfoDOB.getWeightBreakSlab().equalsIgnoreCase("SURCHARGE"))
                               {
                                   costingChargeDetailsDOB.setChargeBasis("Percent of Freight");
                               }
                           }
                           else
                           {
                               if("SLAB".equalsIgnoreCase(weightBreak) && "FLAT".equalsIgnoreCase(rateType))
                                  costingChargeDetailsDOB.setChargeBasisDesc("Per Shipment");
                             
                              else
                              {
                                if("1".equalsIgnoreCase(costingChargeDetailsDOB.getShipmentMode()) && "LIST".equalsIgnoreCase(rs.getString("WEIGHT_BREAK")))
                                {
                                  costingChargeDetailsDOB.setChargeBasisDesc("Per ULD");
                                }
                                else if(("2".equalsIgnoreCase(costingChargeDetailsDOB.getShipmentMode())||"4".equalsIgnoreCase(costingChargeDetailsDOB.getShipmentMode())) && "LIST".equalsIgnoreCase(rs.getString("WEIGHT_BREAK")))
                                {
                                  costingChargeDetailsDOB.setChargeBasisDesc("Per Container");
                                }
                                else if("Per Kg".equalsIgnoreCase(rs.getString("CHARGEBASIS")))
                                {
                                  costingChargeDetailsDOB.setChargeBasisDesc("Per Kilogram");
                                }
                                else if("Per Lb".equalsIgnoreCase(rs.getString("CHARGEBASIS")))
                                {
                                  
                                  costingChargeDetailsDOB.setChargeBasisDesc("Per Pound");
                                }
                                else if("Per CBM".equalsIgnoreCase(rs.getString("CHARGEBASIS")))
                                {
                                  costingChargeDetailsDOB.setChargeBasisDesc("Per Cubic Meter");
                                }
                                else if("Per CFT".equalsIgnoreCase(rs.getString("CHARGEBASIS")))
                                {
                                  costingChargeDetailsDOB.setChargeBasisDesc("Per Cubic Feet");
                                }
                                else
                                  costingChargeDetailsDOB.setChargeBasisDesc(rs.getString("CHARGEBASIS"));
                              }
                            
                           }
                      if("M".equalsIgnoreCase(costingChargeDetailsDOB.getMarginDiscountType()) || costingChargeDetailsDOB.getMarginDiscountType()==null)
                      {  
                        costingRateInfoDOB.setMargin(rs.getDouble("MARGINVALUE"));
                        costingRateInfoDOB.setMarginType(rs.getString("MARGIN_TYPE"));
                        
                        if("A".equalsIgnoreCase(rs.getString("MARGIN_TYPE")))
                          sellRate  = rs.getDouble("BUYRATE")+rs.getDouble("MARGINVALUE");
                        else if("P".equalsIgnoreCase(rs.getString("MARGIN_TYPE")))
                          sellRate  = rs.getDouble("BUYRATE")+(rs.getDouble("BUYRATE")*rs.getDouble("MARGINVALUE")/100);
                      }
                      else
                      {
                        costingRateInfoDOB.setDiscount(rs.getDouble("MARGINVALUE"));
                        costingRateInfoDOB.setDiscountType(rs.getString("MARGIN_TYPE"));
                        
                        if("A".equalsIgnoreCase(rs.getString("MARGIN_TYPE")))
                          sellRate  = rs.getDouble("SELLRATE")-rs.getDouble("MARGINVALUE");
                        else if("P".equalsIgnoreCase(rs.getString("MARGIN_TYPE")))
                          sellRate  = rs.getDouble("SELLRATE")-(rs.getDouble("SELLRATE")*rs.getDouble("MARGINVALUE")/100);
                      }
                      
                      
                      
                      costingRateInfoDOB.setRate(Double.parseDouble( deciFormat.format(sellRate)));
                  
                      costingRateInfoDOB.setLowerBound(rs.getDouble("LBOUND"));
                      costingRateInfoDOB.setUpperBound(rs.getDouble("UBOUND"));
                      if("FREIGHT RATE".equalsIgnoreCase(costingRateInfoDOB.getRateDescription()))
                        costingRateInfoDOB.setRateIndicator(rs.getString("RATE_INDICATOR"));
                        else
                        costingRateInfoDOB.setRateIndicator("");
                            costingRateInfoDOB.setSellRateId(rs.getString("SELLCHARGEID"));
                      costingRateInfoDOB.setBuyRateId(rs.getString("BUY_CHARGE_ID"));              
				  //Added by Mohan for issue no.219979 on 10122010
                  if(rs.getString("CURRENCY")!=null)
                	  costingRateInfoDOB.setMutilQuoteCurrency(rs.getString("CURRENCY"));
                  else
                	  costingRateInfoDOB.setMutilQuoteCurrency(loginbean.getCurrencyId());
                      
                       rateList.add(costingRateInfoDOB);
                      costingChargeDetailsDOB.setCostingRateInfoDOB(rateList);
                      
                    }    
                i++;
                  }
                  
                  
                  costingMasterDOB.setCostingLegDetailsList(legDetails);
                  
                  if(rs!=null)
                    { rs.close();}
                  
                  /* To Order the charges.
                    rs = (ResultSet)cStmt.getObject(3);
                  */
                    
                  //to getBuysell charges          
                  
                  chargeList      = new ArrayList();
                  destChargeList  = new ArrayList();
                  
                  costingChargeDetailsDOB = null;
                  
                 /* while(rs.next())
                  {
                  
                      if(costingChargeDetailsDOB!=null && 
                        ((costingChargeDetailsDOB.getBuyChargeId()!=null && costingChargeDetailsDOB.getBuyChargeId().equals(rs.getString("BUY_CHARGE_ID")))
                       ||
                       (costingChargeDetailsDOB.getSellChargeId()!=null && costingChargeDetailsDOB.getSellChargeId().equals(rs.getString("SELLCHARGEID")))
                        ))
                      {
                            costingRateInfoDOB = new CostingRateInfoDOB();
                            costingRateInfoDOB.setWeightBreakSlab(rs.getString("CHARGESLAB"));
                            //costingRateInfoDOB.setRate(rs.getDouble("SELLRATE"));
                            costingRateInfoDOB.setLowerBound(rs.getDouble("LBOUND"));
                            costingRateInfoDOB.setUpperBound(rs.getDouble("UBOUND"));
                            costingRateInfoDOB.setRateIndicator(rs.getString("RATE_INDICATOR"));
                            
                            if("M".equalsIgnoreCase(costingChargeDetailsDOB.getMarginDiscountType()) || costingChargeDetailsDOB.getMarginDiscountType()==null)
                              {  
                                costingRateInfoDOB.setMargin(rs.getDouble("MARGINVALUE"));
                                costingRateInfoDOB.setMarginType(rs.getString("MARGIN_TYPE"));
                                
                                if("A".equalsIgnoreCase(rs.getString("MARGIN_TYPE")))
                                  sellRate  = rs.getDouble("BUYRATE")+rs.getDouble("MARGINVALUE");
                                else if("P".equalsIgnoreCase(rs.getString("MARGIN_TYPE")))
                                  sellRate  = rs.getDouble("BUYRATE")+(rs.getDouble("BUYRATE")*rs.getDouble("MARGINVALUE")/100);
                              }
                              else
                              {
                                costingRateInfoDOB.setDiscount(rs.getDouble("MARGINVALUE"));
                                costingRateInfoDOB.setDiscountType(rs.getString("MARGIN_TYPE"));
                                
                                if("A".equalsIgnoreCase(rs.getString("MARGIN_TYPE")))
                                  sellRate  = rs.getDouble("SELLRATE")-rs.getDouble("MARGINVALUE");
                                else if("P".equalsIgnoreCase(rs.getString("MARGIN_TYPE")))
                                  sellRate  = rs.getDouble("SELLRATE")-(rs.getDouble("SELLRATE")*rs.getDouble("MARGINVALUE")/100);
                              }
                              
                              
                              
                              costingRateInfoDOB.setRate(Double.parseDouble( deciFormat.format(sellRate)));
                      
                            
                            rateList.add(costingRateInfoDOB);        
                      }else
                      {
                        /*if(!costingChargeDetailsDOB.getCostIncurred().equals(rs.getString("COST_INCURREDAT")))
                        {
                          if(costingChargeDetailsDOB.getCostIncurred().equalsIgnoreCase("Origin"))
                            { costingMasterDOB.setOriginList(chargeList);}
                          else
                            { costingMasterDOB.setDestinationList(chargeList);}
                            
                          chargeList = new ArrayList();
                        }*/
                         /* costingChargeDetailsDOB = new CostingChargeDetailsDOB();
                          
                          if(rs.getString("COST_INCURREDAT").equalsIgnoreCase("Origin"))
                            { chargeList.add(costingChargeDetailsDOB);}
                          if(rs.getString("COST_INCURREDAT").equalsIgnoreCase("Destination"))
                            { destChargeList.add(costingChargeDetailsDOB);}
                          
                         costingChargeDetailsDOB.setBuyChargeId(rs.getString("BUY_CHARGE_ID"));
                          costingChargeDetailsDOB.setSellChargeId(rs.getString("SELLCHARGEID"));
                          
                          costingChargeDetailsDOB.setChargeId(rs.getString("CHARGE_ID"));
                          costingChargeDetailsDOB.setChargeDescId(rs.getString("CHARGEDESCID"));
                          costingChargeDetailsDOB.setInternalName(rs.getString("INT_CHARGE_NAME"));
                          costingChargeDetailsDOB.setExternalName(rs.getString("EXT_CHARGE_NAME"));
                          costingChargeDetailsDOB.setChargeBasisDesc(rs.getString("CHARGEBASIS"));
                          costingChargeDetailsDOB.setCostIncurred(rs.getString("COST_INCURREDAT"));
                          costingChargeDetailsDOB.setCurrency(rs.getString("CURRENCY"));
                          costingChargeDetailsDOB.setWeightBreak(rs.getString("WEIGHT_BREAK"));
                          costingChargeDetailsDOB.setRateType((rs.getString("RATE_TYPE")!=null && !"".equals(rs.getString("RATE_TYPE")))?rs.getString("RATE_TYPE"):rs.getString("WEIGHT_BREAK"));
                          costingChargeDetailsDOB.setWeightClass(rs.getString("WEIGHT_SCALE"));
                          costingChargeDetailsDOB.setDensityRatio(rs.getString("DENSITY_RATIO"));
                          costingChargeDetailsDOB.setChargeBasisDesc(rs.getString("CHARGEBASIS"));
                          costingChargeDetailsDOB.setPrimaryBasis(rs.getString("PRIMARY_BASIS"));
                          costingChargeDetailsDOB.setSecondaryBasis(rs.getString("SECONDARY_BASIS"));
                          costingChargeDetailsDOB.setTertiaryBasis(rs.getString("TERTIARY_BASIS"));
                          costingChargeDetailsDOB.setBlock(rs.getDouble("BLOCK"));
                          costingChargeDetailsDOB.setMarginDiscountType(rs.getString("MARGIN_DISCOUNT_FLAG"));
                          
                          //getConversionfactor
                          //costingChargeDetailsDOB.setConvfactor();
                          
                          rateList = new ArrayList();
            
                          costingRateInfoDOB = new CostingRateInfoDOB();
                          costingRateInfoDOB.setWeightBreakSlab(rs.getString("CHARGESLAB"));
                          //costingRateInfoDOB.setRate(rs.getDouble("SELLRATE"));
                          costingRateInfoDOB.setLowerBound(rs.getDouble("LBOUND"));
                          costingRateInfoDOB.setUpperBound(rs.getDouble("UBOUND"));
                          costingRateInfoDOB.setRateIndicator(rs.getString("RATE_INDICATOR"));
                          
                          if("M".equalsIgnoreCase(costingChargeDetailsDOB.getMarginDiscountType()) || costingChargeDetailsDOB.getMarginDiscountType()==null)
                            {  
                              costingRateInfoDOB.setMargin(rs.getDouble("MARGINVALUE"));
                              costingRateInfoDOB.setMarginType(rs.getString("MARGIN_TYPE"));
                              
                              if("A".equalsIgnoreCase(rs.getString("MARGIN_TYPE")))
                                sellRate  = rs.getDouble("BUYRATE")+rs.getDouble("MARGINVALUE");
                              else if("P".equalsIgnoreCase(rs.getString("MARGIN_TYPE")))
                                sellRate  = rs.getDouble("BUYRATE")+(rs.getDouble("BUYRATE")*rs.getDouble("MARGINVALUE")/100);
                            }
                            else
                            {
                              costingRateInfoDOB.setDiscount(rs.getDouble("MARGINVALUE"));
                              costingRateInfoDOB.setDiscountType(rs.getString("MARGIN_TYPE"));
                              
                              if("A".equalsIgnoreCase(rs.getString("MARGIN_TYPE")))
                                sellRate  = rs.getDouble("SELLRATE")-rs.getDouble("MARGINVALUE");
                              else if("P".equalsIgnoreCase(rs.getString("MARGIN_TYPE")))
                                sellRate  = rs.getDouble("SELLRATE")-(rs.getDouble("SELLRATE")*rs.getDouble("MARGINVALUE")/100);
                            }
                            costingRateInfoDOB.setRate(Double.parseDouble( deciFormat.format(sellRate)));
                          
                          rateList.add(costingRateInfoDOB);
                          costingChargeDetailsDOB.setCostingRateInfoDOB(rateList);      
                       
                      }
                  }
                  
                  //getcartage data
                  if(rs!=null)
                    { rs.close();}*/
                  
              rs = (ResultSet)cStmt.getObject(5);     //Modified by Mohan for issue no.219979 on 10122010     
                  
                  costingChargeDetailsDOB = null;
                  //to get Cartage info
                  
                  while(rs.next())
                  {
                	  //Added By Kishore Podili for Costing
                      if(costingChargeDetailsDOB!=null  
                         && ((costingChargeDetailsDOB.getBuyChargeId()!=null && costingChargeDetailsDOB.getBuyChargeId().equals(rs.getString("BUY_CHARGE_ID")))
                          || 
                          (costingChargeDetailsDOB.getSellChargeId()!=null && costingChargeDetailsDOB.getSellChargeId().equals(rs.getString("SELLCHARGEID"))))
                       && (rs.getString("RATE_DESCRIPTION")!= null && rs.getString("RATE_DESCRIPTION").equals(costingChargeDetailsDOB.getChargeDescId()))
                      )
                      {
                            costingRateInfoDOB = new CostingRateInfoDOB();
                            costingRateInfoDOB.setWeightBreakSlab(rs.getString("CHARGESLAB"));
                            //costingRateInfoDOB.setRate(rs.getDouble("SELLRATE"));
                            costingRateInfoDOB.setLowerBound(rs.getDouble("LBOUND"));
                            costingRateInfoDOB.setUpperBound(rs.getDouble("UBOUND"));
                            costingRateInfoDOB.setRateIndicator(rs.getString("RATE_INDICATOR"));
                             costingRateInfoDOB.setRateDescription(rs.getString("RATE_DESCRIPTION"));
                            if("M".equalsIgnoreCase(costingChargeDetailsDOB.getMarginDiscountType()) || costingChargeDetailsDOB.getMarginDiscountType()==null)
                              {  
                                costingRateInfoDOB.setMargin(rs.getDouble("MARGINVALUE"));
                                costingRateInfoDOB.setMarginType(rs.getString("MARGIN_TYPE"));
                                
                                if("A".equalsIgnoreCase(rs.getString("MARGIN_TYPE")))
                                  sellRate  = rs.getDouble("BUYRATE")+rs.getDouble("MARGINVALUE");
                                else if("P".equalsIgnoreCase(rs.getString("MARGIN_TYPE")))
                                  sellRate  = rs.getDouble("BUYRATE")+(rs.getDouble("BUYRATE")*rs.getDouble("MARGINVALUE")/100);
                              }
                              else
                              {
                                costingRateInfoDOB.setDiscount(rs.getDouble("MARGINVALUE"));
                                costingRateInfoDOB.setDiscountType(rs.getString("MARGIN_TYPE"));
                                
                                if("A".equalsIgnoreCase(rs.getString("MARGIN_TYPE")))
                                  sellRate  = rs.getDouble("SELLRATE")-rs.getDouble("MARGINVALUE");
                                else if("P".equalsIgnoreCase(rs.getString("MARGIN_TYPE")))
                                  sellRate  = rs.getDouble("SELLRATE")-(rs.getDouble("SELLRATE")*rs.getDouble("MARGINVALUE")/100);
                              }
                              costingRateInfoDOB.setRate(Double.parseDouble( deciFormat.format(sellRate)));
                            
                            
                            rateList.add(costingRateInfoDOB);               
                                  }
                      else
                      {
                        /*if(!costingChargeDetailsDOB.getCostIncurred().equals(rs.getString("COST_INCURREDAT")))
                        {
                          if(costingChargeDetailsDOB.getCostIncurred().equalsIgnoreCase("Origin"))
                            { costingMasterDOB.setOriginList(chargeList);}
                          else
                            { costingMasterDOB.setDestinationList(chargeList);}
                            
                          chargeList = new ArrayList();
                        }*/
                          costingChargeDetailsDOB = new CostingChargeDetailsDOB();
                          
                          if(rs.getString("COST_INCURREDAT").equalsIgnoreCase("Pickup"))
                          { 
                            chargeList.add(costingChargeDetailsDOB);
                            //Commented By Kishore for multiple ZoneCodes
                            // costingChargeDetailsDOB.setChargeDescId("Pickup Charge"); 
                              costingChargeDetailsDOB.setChargeDescId(rs.getString("RATE_DESCRIPTION"));
                          }
                          if(rs.getString("COST_INCURREDAT").equalsIgnoreCase("Delivery"))
                          { 
                            destChargeList.add(costingChargeDetailsDOB);
                            //Commented By Kishore for multiple ZoneCodes
                            //costingChargeDetailsDOB.setChargeDescId("Delivery Charge");
                            costingChargeDetailsDOB.setChargeDescId(rs.getString("RATE_DESCRIPTION"));
                          }
                          
                         
                          
                          costingChargeDetailsDOB.setBuyChargeId(rs.getString("BUY_CHARGE_ID"));
                          costingChargeDetailsDOB.setSellChargeId(rs.getString("SELLCHARGEID"));
                          costingChargeDetailsDOB.setInternalName(costingChargeDetailsDOB.getChargeDescId());
                          costingChargeDetailsDOB.setExternalName(costingChargeDetailsDOB.getChargeDescId());
                          //costingChargeDetailsDOB.setChargeId(rs.getString("CHARGE_ID"));
                          //costingChargeDetailsDOB.setChargeDescId(rs.getString("CHARGEDESCID"));
                          costingChargeDetailsDOB.setChargeId(rs.getString("COST_INCURREDAT"));
                          costingChargeDetailsDOB.setWeightBreak(rs.getString("WEIGHT_BREAK"));
                          costingChargeDetailsDOB.setRateType((rs.getString("RATE_TYPE")!=null && !"".equals(rs.getString("RATE_TYPE")))?rs.getString("RATE_TYPE"):rs.getString("WEIGHT_BREAK"));
                          //costingChargeDetailsDOB.setChargeBasisDesc(rs.getString("CHARGEBASIS"));
                          weightBreak = costingChargeDetailsDOB.getWeightBreak();
                          rateType    = costingChargeDetailsDOB.getRateType();
                         // costingChargeDetailsDOB.setRateDescription(rs.getString("RATE_DESCRIPTION"));//@@Added by kameswari for Surcharge Enhancements
                           
                          if("SLAB".equalsIgnoreCase(weightBreak) && "FLAT".equalsIgnoreCase(rateType))
                              costingChargeDetailsDOB.setChargeBasisDesc("Per Shipment");
                          else
                          {
                            if("LIST".equalsIgnoreCase(rs.getString("WEIGHT_BREAK")))
                            {
                              costingChargeDetailsDOB.setChargeBasisDesc("Per Container");
                            }
                            else if("Per Kg".equalsIgnoreCase(rs.getString("CHARGEBASIS")))
                            {
                              costingChargeDetailsDOB.setChargeBasisDesc("Per Kilogram");
                            }
                            else if("Per Lb".equalsIgnoreCase(rs.getString("CHARGEBASIS")))
                            {
                              costingChargeDetailsDOB.setChargeBasisDesc("Per Pound");
                            }
                            else if("Per CBM".equalsIgnoreCase(rs.getString("CHARGEBASIS")))
                            {
                              costingChargeDetailsDOB.setChargeBasisDesc("Per Cubic Meter");
                            }
                            else if("Per CFT".equalsIgnoreCase(rs.getString("CHARGEBASIS")))
                            {
                              costingChargeDetailsDOB.setChargeBasisDesc("Per Cubic Feet");
                            }
                            else
                              costingChargeDetailsDOB.setChargeBasisDesc(rs.getString("CHARGEBASIS"));
                          }
                        
                          costingChargeDetailsDOB.setCostIncurred(rs.getString("COST_INCURREDAT"));
                          costingChargeDetailsDOB.setCurrency(rs.getString("CURRENCY"));
                          costingChargeDetailsDOB.setWeightClass(rs.getString("WEIGHT_SCALE"));
                          costingChargeDetailsDOB.setDensityRatio(rs.getString("DENSITY_RATIO"));
                          costingChargeDetailsDOB.setPrimaryBasis(rs.getString("PRIMARY_BASIS"));
                          
                          costingChargeDetailsDOB.setShipmentMode(rs.getString("SHMODE"));
                          costingChargeDetailsDOB.setMarginDiscountType(rs.getString("MARGIN_DISCOUNT_FLAG"));
                          
                         /* if("KG".equalsIgnoreCase(costingChargeDetailsDOB.getPrimaryBasis()) || "LB".equalsIgnoreCase(costingChargeDetailsDOB.getPrimaryBasis()) )
                            costingChargeDetailsDOB.setTertiaryBasis("Actual");
                          else if("CBM".equalsIgnoreCase(costingChargeDetailsDOB.getPrimaryBasis()) || "CFT".equalsIgnoreCase(costingChargeDetailsDOB.getPrimaryBasis()))*/
                          costingChargeDetailsDOB.setSecondaryBasis(rs.getString("SECONDARY_BASIS"));
                          costingChargeDetailsDOB.setTertiaryBasis(rs.getString("TERTIARY_BASIS"));
                          costingChargeDetailsDOB.setBlock(rs.getDouble("BLOCK"));
                          //getConversionfactor
                          //costingChargeDetailsDOB.setConvfactor();
                          
                          rateList = new ArrayList();
            
                          costingRateInfoDOB = new CostingRateInfoDOB();
                          costingRateInfoDOB.setWeightBreakSlab(rs.getString("CHARGESLAB"));
                          //costingRateInfoDOB.setRate(rs.getDouble("SELLRATE"));
                          costingRateInfoDOB.setLowerBound(rs.getDouble("LBOUND"));
                          costingRateInfoDOB.setUpperBound(rs.getDouble("UBOUND"));
                          costingRateInfoDOB.setRateIndicator(rs.getString("RATE_INDICATOR"));
                          costingRateInfoDOB.setRateDescription(rs.getString("RATE_DESCRIPTION"));
                          if("M".equalsIgnoreCase(costingChargeDetailsDOB.getMarginDiscountType()) || costingChargeDetailsDOB.getMarginDiscountType()==null)
                            {  
                              costingRateInfoDOB.setMargin(rs.getDouble("MARGINVALUE"));
                              costingRateInfoDOB.setMarginType(rs.getString("MARGIN_TYPE"));
                              
                              if("A".equalsIgnoreCase(rs.getString("MARGIN_TYPE")))
                                sellRate  = rs.getDouble("BUYRATE")+rs.getDouble("MARGINVALUE");
                              else if("P".equalsIgnoreCase(rs.getString("MARGIN_TYPE")))
                                sellRate  = rs.getDouble("BUYRATE")+(rs.getDouble("BUYRATE")*rs.getDouble("MARGINVALUE")/100);
                            }
                            else
                            {
                              costingRateInfoDOB.setDiscount(rs.getDouble("MARGINVALUE"));
                              costingRateInfoDOB.setDiscountType(rs.getString("MARGIN_TYPE"));
                              
                              if("A".equalsIgnoreCase(rs.getString("MARGIN_TYPE")))
                                sellRate  = rs.getDouble("SELLRATE")-rs.getDouble("MARGINVALUE");
                              else if("P".equalsIgnoreCase(rs.getString("MARGIN_TYPE")))
                                sellRate  = rs.getDouble("SELLRATE")-(rs.getDouble("SELLRATE")*rs.getDouble("MARGINVALUE")/100);
                            }
                            costingRateInfoDOB.setRate(Double.parseDouble( deciFormat.format(sellRate)));
                          
                          rateList.add(costingRateInfoDOB);
                          costingChargeDetailsDOB.setCostingRateInfoDOB(rateList);      
                       
                      }           
                  }
                  
                   //get external notes data
                  if(rs!=null)
                    { rs.close();}
                  
                  if(destChargeList != null && destChargeList.size()==1)
                  {
                    delChargeDetailsDOB = (CostingChargeDetailsDOB)destChargeList.get(0);
                    destChargeList.remove(0);
                  }
                  
              rs = (ResultSet)cStmt.getObject(4);//Modified by Mohan for issue no.219979 on 10122010
                  while(rs.next())
                  {
                  
                      if(costingChargeDetailsDOB!=null && 
                        ((costingChargeDetailsDOB.getBuyChargeId()!=null && costingChargeDetailsDOB.getBuyChargeId().equals(rs.getString("BUY_CHARGE_ID")))
                       ||
                       (costingChargeDetailsDOB.getSellChargeId()!=null && costingChargeDetailsDOB.getSellChargeId().equals(rs.getString("SELLCHARGEID")))
                        ))
                      {
                            costingRateInfoDOB = new CostingRateInfoDOB();
                            costingRateInfoDOB.setWeightBreakSlab(rs.getString("CHARGESLAB"));
                            //costingRateInfoDOB.setRate(rs.getDouble("SELLRATE"));
                            costingRateInfoDOB.setLowerBound(rs.getDouble("LBOUND"));
                            costingRateInfoDOB.setUpperBound(rs.getDouble("UBOUND"));
                            costingRateInfoDOB.setRateIndicator(rs.getString("RATE_INDICATOR"));
                            costingRateInfoDOB.setRateDescription(rs.getString("RATE_DESCRIPTION"));
                            if("M".equalsIgnoreCase(costingChargeDetailsDOB.getMarginDiscountType()) || costingChargeDetailsDOB.getMarginDiscountType()==null)
                              {  
                                costingRateInfoDOB.setMargin(rs.getDouble("MARGINVALUE"));
                                costingRateInfoDOB.setMarginType(rs.getString("MARGIN_TYPE"));
                                
                                if("A".equalsIgnoreCase(rs.getString("MARGIN_TYPE")))
                                  sellRate  = rs.getDouble("BUYRATE")+rs.getDouble("MARGINVALUE");
                                else if("P".equalsIgnoreCase(rs.getString("MARGIN_TYPE")))
                                  sellRate  = rs.getDouble("BUYRATE")+(rs.getDouble("BUYRATE")*rs.getDouble("MARGINVALUE")/100);
                              }
                              else
                              {
                                costingRateInfoDOB.setDiscount(rs.getDouble("MARGINVALUE"));
                                costingRateInfoDOB.setDiscountType(rs.getString("MARGIN_TYPE"));
                                
                                if("A".equalsIgnoreCase(rs.getString("MARGIN_TYPE")))
                                  sellRate  = rs.getDouble("SELLRATE")-rs.getDouble("MARGINVALUE");
                                else if("P".equalsIgnoreCase(rs.getString("MARGIN_TYPE")))
                                  sellRate  = rs.getDouble("SELLRATE")-(rs.getDouble("SELLRATE")*rs.getDouble("MARGINVALUE")/100);
                              }
                              
                              
                              
                               //Commented and added by subrahmanyam for 180161 on 25-aug-09
                              //costingRateInfoDOB.setRate(Double.parseDouble( deciFormat.format(sellRate)));
                              costingRateInfoDOB.setRate(Double.parseDouble( roundDecimal(sellRate)));
                            //Ended for 180161
                      
                            
                            rateList.add(costingRateInfoDOB);        
                      }else
                      {
                        /*if(!costingChargeDetailsDOB.getCostIncurred().equals(rs.getString("COST_INCURREDAT")))
                        {
                          if(costingChargeDetailsDOB.getCostIncurred().equalsIgnoreCase("Origin"))
                            { costingMasterDOB.setOriginList(chargeList);}
                          else
                            { costingMasterDOB.setDestinationList(chargeList);}
                            
                          chargeList = new ArrayList();
                        }*/
                          costingChargeDetailsDOB = new CostingChargeDetailsDOB();
                          
                          if(rs.getString("COST_INCURREDAT").equalsIgnoreCase("Origin"))
                            { chargeList.add(costingChargeDetailsDOB);}
                          if(rs.getString("COST_INCURREDAT").equalsIgnoreCase("Destination"))
                            { destChargeList.add(costingChargeDetailsDOB);}
                          
                          costingChargeDetailsDOB.setBuyChargeId(rs.getString("BUY_CHARGE_ID"));
                          costingChargeDetailsDOB.setSellChargeId(rs.getString("SELLCHARGEID"));
                          
                          costingChargeDetailsDOB.setChargeId(rs.getString("CHARGE_ID"));
                          costingChargeDetailsDOB.setChargeDescId(rs.getString("CHARGEDESCID"));
                          costingChargeDetailsDOB.setInternalName(rs.getString("INT_CHARGE_NAME"));
                          costingChargeDetailsDOB.setExternalName(rs.getString("EXT_CHARGE_NAME"));
                          //costingChargeDetailsDOB.setChargeBasisDesc(rs.getString("CHARGEBASIS"));
                          costingChargeDetailsDOB.setCostIncurred(rs.getString("COST_INCURREDAT"));
                          costingChargeDetailsDOB.setCurrency(rs.getString("CURRENCY"));
                          costingChargeDetailsDOB.setWeightBreak(rs.getString("WEIGHT_BREAK"));
                          costingChargeDetailsDOB.setRateType((rs.getString("RATE_TYPE")!=null && !"".equals(rs.getString("RATE_TYPE")))?rs.getString("RATE_TYPE"):rs.getString("WEIGHT_BREAK"));
                           costingChargeDetailsDOB.setWeightClass(rs.getString("WEIGHT_SCALE"));
                          costingChargeDetailsDOB.setDensityRatio(rs.getString("DENSITY_RATIO"));
                          
                          weightBreak = costingChargeDetailsDOB.getWeightBreak();
                          rateType    = costingChargeDetailsDOB.getRateType();
                          
                          if("SLAB".equalsIgnoreCase(weightBreak) && "FLAT".equalsIgnoreCase(rateType))
                              costingChargeDetailsDOB.setChargeBasisDesc("Per Shipment");
                          else
                          {                          
                            if("Per Kg".equalsIgnoreCase(rs.getString("CHARGEBASIS")))
                            {
                              costingChargeDetailsDOB.setChargeBasisDesc("Per Kilogram");
                            }
                            else if("Per Lb".equalsIgnoreCase(rs.getString("CHARGEBASIS")))
                            {
                              costingChargeDetailsDOB.setChargeBasisDesc("Per Pound");
                            }
                            else if("Per CBM".equalsIgnoreCase(rs.getString("CHARGEBASIS")))
                            {
                              costingChargeDetailsDOB.setChargeBasisDesc("Per Cubic Meter");
                            }
                            else if("Per CFT".equalsIgnoreCase(rs.getString("CHARGEBASIS")))
                            {
                              costingChargeDetailsDOB.setChargeBasisDesc("Per Cubic Feet");
                            }
                            else
                              costingChargeDetailsDOB.setChargeBasisDesc(rs.getString("CHARGEBASIS"));
                          }
                          
                          //costingChargeDetailsDOB.setChargeBasisDesc(rs.getString("CHARGEBASIS"));
                          costingChargeDetailsDOB.setPrimaryBasis(rs.getString("PRIMARY_BASIS"));
                          costingChargeDetailsDOB.setSecondaryBasis(rs.getString("SECONDARY_BASIS"));
                          costingChargeDetailsDOB.setTertiaryBasis(rs.getString("TERTIARY_BASIS"));
                          costingChargeDetailsDOB.setBlock(rs.getDouble("BLOCK"));
                          costingChargeDetailsDOB.setMarginDiscountType(rs.getString("MARGIN_DISCOUNT_FLAG"));
                          
                          //getConversionfactor
                          //costingChargeDetailsDOB.setConvfactor();
                          
                          rateList = new ArrayList();
            
                          costingRateInfoDOB = new CostingRateInfoDOB();
                          costingRateInfoDOB.setWeightBreakSlab(rs.getString("CHARGESLAB"));
                          //costingRateInfoDOB.setRate(rs.getDouble("SELLRATE"));
                          costingRateInfoDOB.setLowerBound(rs.getDouble("LBOUND"));
                          costingRateInfoDOB.setUpperBound(rs.getDouble("UBOUND"));
                          costingRateInfoDOB.setRateIndicator(rs.getString("RATE_INDICATOR"));
                          costingRateInfoDOB.setRateDescription(rs.getString("RATE_DESCRIPTION"));
                          if("M".equalsIgnoreCase(costingChargeDetailsDOB.getMarginDiscountType()) || costingChargeDetailsDOB.getMarginDiscountType()==null)
                            {  
                              costingRateInfoDOB.setMargin(rs.getDouble("MARGINVALUE"));
                              costingRateInfoDOB.setMarginType(rs.getString("MARGIN_TYPE"));
                              
                              if("A".equalsIgnoreCase(rs.getString("MARGIN_TYPE")))
                                sellRate  = rs.getDouble("BUYRATE")+rs.getDouble("MARGINVALUE");
                              else if("P".equalsIgnoreCase(rs.getString("MARGIN_TYPE")))
                                sellRate  = rs.getDouble("BUYRATE")+(rs.getDouble("BUYRATE")*rs.getDouble("MARGINVALUE")/100);
                            }
                            else
                            {
                              costingRateInfoDOB.setDiscount(rs.getDouble("MARGINVALUE"));
                              costingRateInfoDOB.setDiscountType(rs.getString("MARGIN_TYPE"));
                              
                              if("A".equalsIgnoreCase(rs.getString("MARGIN_TYPE")))
                                sellRate  = rs.getDouble("SELLRATE")-rs.getDouble("MARGINVALUE");
                              else if("P".equalsIgnoreCase(rs.getString("MARGIN_TYPE")))
                                sellRate  = rs.getDouble("SELLRATE")-(rs.getDouble("SELLRATE")*rs.getDouble("MARGINVALUE")/100);
                            }
                            //@@ commented and added by subrahmanyam for 180161
                           // costingRateInfoDOB.setRate(Double.parseDouble( deciFormat.format(sellRate)));
                           costingRateInfoDOB.setRate(Double.parseDouble( roundDecimal(sellRate)));
                           //Ended for 180161
                                                     
                           rateList.add(costingRateInfoDOB);
                           costingChargeDetailsDOB.setCostingRateInfoDOB(rateList);      
                       
                      }
                  }
                  
                  //getcartage data
                  if(rs!=null)
                    { rs.close();}
                    
              rs = (ResultSet)cStmt.getObject(6);//Modified by Mohan for issue no.219979 on 10122010
                  
                  list_exNotes = new ArrayList();
                  
                  while(rs.next())
                  {
                    list_exNotes.add((rs.getString("EXTERNAL_NOTES")!=null)?rs.getString("EXTERNAL_NOTES").trim():"");
                  }
                  
                  costingMasterDOB.setExternalNotes(list_exNotes);
                  
                  costingMasterDOB.setOriginList(chargeList); 
                  if(delChargeDetailsDOB!=null)
                      destChargeList.add(delChargeDetailsDOB);
                  costingMasterDOB.setDestinationList(destChargeList); 
                  
                  if(rs!=null)
                    { rs.close();}
                  
                  legDetails = costingMasterDOB.getCostingLegDetailsList();                  
                              
          }else
          {
            throw new Exception("No data found");
          }
        }catch(Exception e)
        {
          e.printStackTrace();
          throw new Exception();
          
        }finally
        {
          try
          {
            if(rs1!=null)
              { rs1.close();}
            if(pstmt!=null)
              { pstmt.close();}
            if(rs!=null)
              { rs.close();}
            if(cStmt!=null)
              { cStmt.close();}
         // Added by Dilip for PMD Correction on 22/09/2015
            if(rs2!=null){ 
            	rs2.close();
            	rs2=null;
            }
            if(pstmt1!=null){ 
            	pstmt1.close();
            	pstmt1=null;
            }
            if(connection!=null)
              { connection.close();}
          }catch(SQLException e)
          {
            //Logger.error(FILE_NAME,"in finally"+e);
            logger.error(FILE_NAME+"in finally"+e);
          }
        }
        
        return costingMasterDOB; 
    }  
//Added by Rakesh on 12-01-2011
private String roundDecimal (double sellRate)
{

java.text.DecimalFormat deciFormat = new java.text.DecimalFormat("##0.00000");
  String rateString ="";
  int k = 0;
  int l = 0;
  int m = 0;
  rateString = Double.toString(sellRate);
  k = rateString.length();
  l = rateString.indexOf(".");
  m = (k - l)+1;
  if(m>5)
  rateString = deciFormat.format(sellRate);

 return rateString;

}  
//Added for the issue 234719
public MultiQuoteFinalDOB getUpdatedQuoteInfo(long quoteId,String changeDesc,String sellBuyFlag,MultiQuoteFinalDOB finalDOB,String quoteType) throws SQLException
{
Connection                  connection             = null;
CallableStatement           csmt                   = null;
PreparedStatement           pStmt                  = null;
 ResultSet                   rs2                    = null,rs3 = null,rs4 = null,rs5 = null,rs6=null;

ArrayList                  originChargesList       = null;//to maintain the list of  all origin charge dobs
ArrayList                  destChargesList         = null;//to maintain the list of  all origin charge dobs
MultiQuoteFreightLegSellRates   legDOB                  = null;
MultiQuoteFreightLegSellRates   spotLegDOB              = null;
MultiQuoteChargeInfo               chargesDOB              = null;//to maintain one record that is to be displayed
MultiQuoteCharges               deliveryChargesDOB      = null;
MultiQuoteChargeInfo            chargeInfo              = null;
ArrayList                  chargeInfoList          = null;
ArrayList                  legDetails              = null;
ArrayList                  spotRateDetails         = null;
double                     sellRate                = 0;
String                     weightBreak             = null;
String                     rateType                = null;
ArrayList                  list_exNotes            = null;
ArrayList                  freightChargesList      = null;
MultiQuoteMasterDOB             masterDOB               = null;
int[]                      selectedFrtIndices      = null;
Integer[]				   selectedFreightSellRateIndex = null;
MultiQuoteFinalDOB              tmpFinalDOB             = null;
String                     flag                    = null;
boolean                    isShipperZipCode[]        = null;
boolean                    isConsigneeZipCode[]      = null;
boolean                    isSingleShipperZone[]     = null;
boolean                    isSingleConsigneeZone[]   = null;
double                     marginValue             = 0;//@@Added by kameswari for the WPBN issue-61235
try
{
  masterDOB         =     finalDOB.getMasterDOB();
  connection        =     this.getConnection();
  csmt              =     connection.prepareCall("{ call qms_quotepack_new.qms_updated_modify_quote(?,?,?,?,?,?,?) }");
//  pStmt             =     connection.prepareStatement("SELECT BUY_RATE,R_SELL_RATE FROM QMS_QUOTE_RATES WHERE QUOTE_ID =? AND SELL_BUY_FLAG=?");//@@Added bby kameswari fro the WPBN issue-
//     pStmt             =     connection.prepareStatement("SELECT BUY_RATE,R_SELL_RATE FROM QMS_QUOTE_RATES WHERE QUOTE_ID =? AND SELL_BUY_FLAG=?");//@@Added bby kameswari fro the WPBN issue-
 // pStmt             =     connection.prepareStatement("SELECT BUY_RATE,R_SELL_RATE,SELL_BUY_FLAG,BREAK_POINT FROM QMS_QUOTE_RATES WHERE QUOTE_ID =? AND SELL_BUY_FLAG=?");//@@Added by kameswari for the WPBN issue-
// pStmt             =     connection.prepareStatement("select BUY_RATE,R_SELL_RATE,SELL_BUY_FLAG,BREAK_POINT ,charge_id,charge_description FROM QMS_QUOTE_RATES WHERE QUOTE_ID =? AND SELL_BUY_FLAG =? and (charge_id,charge_description) in (select charge_id,chargedescid from qms_charge_groupsmaster where id in (select id from qms_charge_groupsmaster where chargegroup_id in (select chargegroupid from qms_quote_chargegroupdtl where quote_id='45663')  group by id) ) ");//@@Added by kameswari for the WPBN issue-

   csmt.setLong(1,quoteId);
  csmt.setString(2,sellBuyFlag);
  csmt.setString(3,changeDesc);

  csmt.registerOutParameter(4,OracleTypes.CURSOR);
  csmt.registerOutParameter(5,OracleTypes.CURSOR);
  csmt.registerOutParameter(6,OracleTypes.CURSOR);
  csmt.registerOutParameter(7,OracleTypes.CURSOR);
  
  csmt.execute();

  rs2  = (ResultSet) csmt.getObject(4);
  rs3  = (ResultSet) csmt.getObject(5);
  rs4  = (ResultSet) csmt.getObject(6);
  rs5  = (ResultSet) csmt.getObject(7);
  //@@rs2 resultset fot fetching Freight Rates
//@@Added by kameswari for the WPBN issue-61235
//@@Added by Kameswari for the WPBN issue-13558
if("B".equalsIgnoreCase(sellBuyFlag)||"S".equalsIgnoreCase(sellBuyFlag))
{
//pStmt             =     connection.prepareStatement("SELECT BUY_RATE,R_SELL_RATE,SELL_BUY_FLAG,BREAK_POINT,CHARGE_DESCRIPTION FROM QMS_QUOTE_RATES WHERE QUOTE_ID =? AND SELL_BUY_FLAG=? AND (CHARGE_ID,CHARGE_DESCRIPTION) IN (SELECT CHARGE_ID,CHARGEDESCID FROM QMS_CHARGE_GROUPSMASTER WHERE ID IN (SELECT ID FROM QMS_CHARGE_GROUPSMASTER WHERE CHARGEGROUP_ID IN (SELECT CHARGEGROUPID FROM QMS_QUOTE_CHARGEGROUPDTL WHERE QUOTE_ID=?)  GROUP BY ID) )");
  pStmt             =     connection.prepareStatement("SELECT BUY_RATE,R_SELL_RATE,SELL_BUY_FLAG,BREAK_POINT,CHARGE_DESCRIPTION FROM QMS_QUOTE_RATES WHERE QUOTE_ID =? AND SELL_BUY_FLAG=? AND CHARGE_DESCRIPTION=?");
  pStmt.setLong(1,quoteId);
  pStmt.setString(2,sellBuyFlag);
   pStmt.setString(3,changeDesc);
  rs6 =  pStmt.executeQuery();
}
else if("BC".equalsIgnoreCase(sellBuyFlag)||"SC".equalsIgnoreCase(sellBuyFlag)) 
{
  pStmt             =     connection.prepareStatement("SELECT BUY_RATE,R_SELL_RATE,SELL_BUY_FLAG,BREAK_POINT,CHARGE_AT,LINE_NO FROM QMS_QUOTE_RATES WHERE QUOTE_ID =? AND SELL_BUY_FLAG=? ORDER BY CHARGE_AT,LINE_NO");//@@Added by kameswari for the WPBN issue-
  pStmt.setLong(1,quoteId);
  pStmt.setString(2,sellBuyFlag);
  rs6 =  pStmt.executeQuery();
}
else
{
  pStmt             =     connection.prepareStatement("SELECT BUY_RATE,R_SELL_RATE,SELL_BUY_FLAG,BREAK_POINT,CHARGE_AT,LINE_NO FROM QMS_QUOTE_RATES WHERE QUOTE_ID =? AND SELL_BUY_FLAG=? ORDER BY CHARGE_AT,LINE_NO");//@@Added by kameswari for the WPBN issue-
  pStmt.setLong(1,quoteId);
  pStmt.setString(2,sellBuyFlag);
  rs6 =  pStmt.executeQuery();
}
 //@@WPBN issue-13558
  int n = 0;
  String breakPoint = null;
  legDOB                  = null;
 //@@WPBN issue-61235

   while(rs2.next())
  {
    
       flag = rs2.getString("SEL_BUY_FLAG");
  
      if(legDOB!=null && legDOB.getLegSerialNo()==rs2.getInt("LEG_SL_NO"))
    {                  
      if((chargesDOB.getSellChargeId()!=null && chargesDOB.getSellChargeId().equals(rs2.getString("SELLCHARGEID"))) || 
         (chargesDOB.getBuyChargeId()!=null && chargesDOB.getBuyChargeId().equals(rs2.getString("BUY_CHARGE_ID"))))
      {
               chargeInfo  = new MultiQuoteChargeInfo();
               chargeInfoList.add(chargeInfo);
              chargeInfo.setBreakPoint(rs2.getString("CHARGESLAB"));
              
              if(rs2.getString("CURRENCY")!=null && rs2.getString("CURRENCY").trim().length()!=0)
                chargeInfo.setCurrency(rs2.getString("CURRENCY"));
              else
                chargeInfo.setCurrency(masterDOB.getTerminalCurrency());
                
              chargeInfo.setBuyRate(rs2.getDouble("BUYRATE"));
              chargeInfo.setRecOrConSellRrate(rs2.getDouble("SELLRATE"));
              chargeInfo.setSellChargeMargin(rs2.getDouble("MARGINVALUE"));
              chargeInfo.setSellChargeMarginType(rs2.getString("MARGIN_TYPE"));
              chargeInfo.setRateIndicator(rs2.getString("RATE_INDICATOR"));
              
              if("A FREIGHT RATE".equalsIgnoreCase(rs2.getString("RATE_DESCRIPTION")))
                 chargeInfo.setRateDescription("FREIGHT RATE");
              else   if("C.P.S.S".equalsIgnoreCase(rs2.getString("RATE_DESCRIPTION")))
                 chargeInfo.setRateDescription("P.S.S");
              else
                 chargeInfo.setRateDescription(rs2.getString("RATE_DESCRIPTION")!=null?rs2.getString("RATE_DESCRIPTION"):"FREIGHT RATE");//@@Added by Kameswari for Surcharge Enhancemenst
              if("M".equalsIgnoreCase(chargesDOB.getMarginDiscountFlag()) || chargesDOB.getMarginDiscountFlag()==null)
              {  
               
                chargeInfo.setMarginType(rs2.getString("MARGIN_TYPE"));
                
                if("A".equalsIgnoreCase(rs2.getString("MARGIN_TYPE")))
                {
                  //@@Modified by kameswari for the WPBN issue-61235
                  
               //   if(rs6.next()&&flag.equalsIgnoreCase(rs6.getString("SELL_BUY_FLAG"))&&rs2.getString("CHARGESLAB").equalsIgnoreCase(rs6.getString("BREAK_POINT")))
                  if(flag.equalsIgnoreCase(sellBuyFlag))
                    {
                    
                      if("modifiedQuote".equalsIgnoreCase(quoteType))
                      {
                        sellRate  = rs2.getDouble("BUYRATE")+rs2.getDouble("MARGINVALUE");
                         chargeInfo.setMargin(rs2.getDouble("MARGINVALUE"));
                        }
                      else
                      {
                        if(rs6.next())
                       {
                        	logger.info("buyrate M :"+rs6.getDouble("BUY_RATE"));
                        	logger.info("margin Value M: "+rs2.getDouble("MARGINVALUE"));
                        	
                        sellRate  = rs6.getDouble("BUY_RATE")+rs2.getDouble("MARGINVALUE");
                        marginValue = sellRate-rs2.getDouble("BUYRATE");
                        logger.info("sellRate M: "+sellRate);
                        logger.info("marginValue M: "+marginValue);
                         chargeInfo.setMargin(marginValue);
                       }
                      }
                     }
                                    
                  else
                  {
                       sellRate  = rs2.getDouble("BUYRATE")+rs2.getDouble("MARGINVALUE");
                       chargeInfo.setMargin(rs2.getDouble("MARGINVALUE"));
                  }
                  //@@WPBN issue-61235
                }
              
                else if("P".equalsIgnoreCase(rs2.getString("MARGIN_TYPE")))
                {
                 
                   //@@Modified by kameswari for the WPBN issue-61235
                 // if(rs6.next()&&flag.equalsIgnoreCase(rs6.getString("SELL_BUY_FLAG"))&&rs2.getString("CHARGESLAB").equalsIgnoreCase(rs6.getString("BREAK_POINT")))
                  if(flag.equalsIgnoreCase(sellBuyFlag))
                  {
                     if("modifiedQuote".equalsIgnoreCase(quoteType))
                    {
                     sellRate  = rs2.getDouble("BUYRATE")+(rs2.getDouble("BUYRATE")*rs2.getDouble("MARGINVALUE")/100);
                      chargeInfo.setMargin(rs2.getDouble("MARGINVALUE"));
                    }
                     else
                    {
                      if(rs6.next())
                       {
                         	logger.info("buyrate P :"+rs6.getDouble("BUY_RATE"));
                        	logger.info("margin Value P: "+rs2.getDouble("MARGINVALUE"));
                      	

                            sellRate  = rs6.getDouble("BUY_RATE")+(rs6.getDouble("BUY_RATE")*rs2.getDouble("MARGINVALUE")/100);
                            marginValue = (sellRate-rs2.getDouble("BUYRATE"))*100/rs2.getDouble("BUYRATE"); 
                            logger.info("sellRate P: "+sellRate);
                            logger.info("marginValue P: "+marginValue);
 
                            chargeInfo.setMargin(marginValue);
                        
                            }
                     }
                   }
                   else
                  {
                      sellRate  = rs2.getDouble("BUYRATE")+(rs2.getDouble("BUYRATE")*rs2.getDouble("MARGINVALUE")/100);
                      chargeInfo.setMargin(rs2.getDouble("MARGINVALUE"));
                  }
                   //@@WPBN issue-61235
                }
                
              }
              else
              {
                 chargeInfo.setDiscountType(rs2.getString("MARGIN_TYPE"));
                
                if("A".equalsIgnoreCase(rs2.getString("MARGIN_TYPE")))
                {
                    //@@Modified by kameswari for the WPBN issue-61235
                 // if(rs6.next()&&flag.equalsIgnoreCase(rs6.getString("SELL_BUY_FLAG"))&&rs2.getString("CHARGESLAB").equalsIgnoreCase(rs6.getString("BREAK_POINT")))
                   if(flag.equalsIgnoreCase(sellBuyFlag))
                  {
                     if("modifiedQuote".equalsIgnoreCase(quoteType))
                     {
                         sellRate  = rs2.getDouble("SELLRATE")-rs2.getDouble("MARGINVALUE");
                         chargeInfo.setDiscount(rs2.getDouble("MARGINVALUE"));
                     }
                    else
                    {
                       if(rs6.next())
                       {
                    	   logger.info("buyrate 1 :"+rs6.getDouble("R_SELL_RATE"));
                    	 logger.info("margin Value 1: "+rs2.getDouble("MARGINVALUE"));
                     	
                        sellRate  = rs6.getDouble("R_SELL_RATE")-rs2.getDouble("MARGINVALUE");
                        marginValue  =rs2.getDouble("SELLRATE") -sellRate;
                        
                        logger.info("sellRate 1: "+sellRate);
                        logger.info("marginValue 1: "+marginValue);

                        chargeInfo.setDiscount(marginValue);
                       }
                    }
                  }
                  else
                  {
                     
                     sellRate  = rs2.getDouble("SELLRATE")-rs2.getDouble("MARGINVALUE");
                     chargeInfo.setDiscount(rs2.getDouble("MARGINVALUE"));
                 }
                   //@@WPBN issue-61235
                }
                 
                else if("P".equalsIgnoreCase(rs2.getString("MARGIN_TYPE")))
                {
                   //@@Modified by kameswari for the WPBN issue-61235
                 // if(rs6.next()&&flag.equalsIgnoreCase(rs6.getString("SELL_BUY_FLAG"))&&rs2.getString("CHARGESLAB").equalsIgnoreCase(rs6.getString("BREAK_POINT")))
                   if(flag.equalsIgnoreCase(sellBuyFlag))
                  {
                      if("modifiedQuote".equalsIgnoreCase(quoteType))
                    {
                      sellRate  = rs2.getDouble("SELLRATE")-(rs2.getDouble("SELLRATE")*rs2.getDouble("MARGINVALUE")/100);
                     
                      chargeInfo.setDiscount(rs2.getDouble("MARGINVALUE"));
                    }
                    else
                    {
                       if(rs6.next())
                       {
                    	   
                    	   logger.info("buyrate 2 :"+rs6.getDouble("R_SELL_RATE"));
                      	 logger.info("margin Value 2: "+rs2.getDouble("MARGINVALUE"));
                       	
                        sellRate  = rs6.getDouble("R_SELL_RATE")-(rs6.getDouble("R_SELL_RATE")*rs2.getDouble("MARGINVALUE")/100);
                        marginValue = (rs2.getDouble("SELLRATE")-sellRate)*100/rs2.getDouble("SELLRATE");
                        logger.info("sellRate 2: "+sellRate);
                        logger.info("marginValue 2: "+marginValue);

                        chargeInfo.setDiscount(marginValue);
                       }
                    }
                  
                  }
                   else
                  {
                      
                       sellRate  = rs2.getDouble("SELLRATE")-(rs2.getDouble("SELLRATE")*rs2.getDouble("MARGINVALUE")/100);
                      chargeInfo.setDiscount(rs2.getDouble("MARGINVALUE"));
    
                 }
                   //@@WPBN issue-61235
                }
              }
              weightBreak = rs2.getString("WEIGHT_BREAK");
              rateType    = rs2.getString("RATE_TYPE");
              //chargeInfo.setBasis(rs2.getString("CHARGEBASIS"));
             /* if("BASE".equalsIgnoreCase(chargeInfo.getBreakPoint()) || "MIN".equalsIgnoreCase(chargeInfo.getBreakPoint()) || "MAX".equalsIgnoreCase(chargeInfo.getBreakPoint()) || ("SLAB".equalsIgnoreCase(weightBreak) && "FLAT".equalsIgnoreCase(rateType)) || ("BOTH".equalsIgnoreCase(rateType) && "F".equalsIgnoreCase(chargeInfo.getRateIndicator())))
              {
                chargeInfo.setBasis("Per Shipment");
              }
              else
              {
                //chargeInfo.setBasis(rs2.getString("CHARGEBASIS"));
                if("1".equalsIgnoreCase(rs2.getString("SHMODE")) && "LIST".equalsIgnoreCase(rs2.getString("WEIGHT_BREAK")))
                {
                  chargeInfo.setBasis("Per ULD");
                }
                else if(("2".equalsIgnoreCase(rs2.getString("SHMODE"))||"4".equalsIgnoreCase(rs2.getString("SHMODE"))) && "LIST".equalsIgnoreCase(rs2.getString("WEIGHT_BREAK")))
                {
                  chargeInfo.setBasis("Per Container");
                }
                else if("Per Kg".equalsIgnoreCase(rs2.getString("CHARGEBASIS")))
                {
                  chargeInfo.setBasis("Per Kilogram");
                }
                else if("Per Lb".equalsIgnoreCase(rs2.getString("CHARGEBASIS")))
                {
                  chargeInfo.setBasis("Per Pound");
                }
                else if("Per CBM".equalsIgnoreCase(rs2.getString("CHARGEBASIS")))
                {
                  chargeInfo.setBasis("Per Cubic Meter");
                }
                else if("Per CFT".equalsIgnoreCase(rs2.getString("CHARGEBASIS")))
                {
                  chargeInfo.setBasis("Per Cubic Feet");
                }
                else
                  chargeInfo.setBasis(rs2.getString("CHARGEBASIS"));
              }
              */
              
              if(!("FREIGHT RATE".equalsIgnoreCase(chargeInfo.getRateDescription())))
              {
                   if(chargeInfo.getBreakPoint().equalsIgnoreCase("CSF"))
             {
                  chargeInfo.setBasis("Per Shipment");
             }
                else  if(chargeInfo.getBreakPoint().endsWith("CAF")||chargeInfo.getBreakPoint().endsWith("caf")||chargeInfo.getBreakPoint().endsWith("BAF")
             ||chargeInfo.getBreakPoint().endsWith("baf")||chargeInfo.getBreakPoint().endsWith("CSF")||chargeInfo.getBreakPoint().endsWith("csf")||chargeInfo.getBreakPoint().endsWith("PSS")||chargeInfo.getBreakPoint().endsWith("pss"))
             {
                  // if(chargeInfo.getBreakPoint().endsWith("CAF") //@@Modified for the WPBN issue on 16/12/08
                   if(chargeInfo.getBreakPoint().endsWith("CAF")||chargeInfo.getBreakPoint().endsWith("caf"))
                 {
                    chargeInfo.setBasis("Percent of Freight");
                 }
                 else
                 {
                   chargeInfo.setBasis("Per Container");
                 }
           }
               else if(chargeInfo.getBreakPoint().equalsIgnoreCase("BAFMIN")||chargeInfo.getBreakPoint().equalsIgnoreCase("CAFMIN")
           ||chargeInfo.getBreakPoint().equalsIgnoreCase("PSSMIN")||chargeInfo.getBreakPoint().equalsIgnoreCase("FSMIN")||chargeInfo.getBreakPoint().equalsIgnoreCase("SSMIN"))
             {
                 chargeInfo.setBasis("Per Shipment");
             }
             else if(chargeInfo.getBreakPoint().equalsIgnoreCase("BAFM3")||chargeInfo.getBreakPoint().equalsIgnoreCase("CAF%")
                ||chargeInfo.getBreakPoint().equalsIgnoreCase("PSSM3"))
             {
                if(chargeInfo.getBreakPoint().equalsIgnoreCase("BAFM3")||chargeInfo.getBreakPoint().equalsIgnoreCase("PSSM3"))
                {
                      chargeInfo.setBasis("per Cubic Meter");
                }
                else
                {
                     chargeInfo.setBasis("Percent of Freight");
                }
             }
             
             else if(chargeInfo.getBreakPoint().equalsIgnoreCase("FSBASIC")||chargeInfo.getBreakPoint().equalsIgnoreCase("SSBASIC"))
             {
                   chargeInfo.setBasis("Per Shipment");
             }
              else if(chargeInfo.getBreakPoint().equalsIgnoreCase("FSKG")||chargeInfo.getBreakPoint().equalsIgnoreCase("SSKG"))
             {
                  chargeInfo.setBasis("Per Kg");
             }
             else if(chargeInfo.getBreakPoint().equalsIgnoreCase("SURCHARGE"))
             {
                  chargeInfo.setBasis("Percent of Freight");
             }
              }   //@@Surcharge Enhancements
              else
              {
                //chargeInfo.setBasis(rs2.getString("CHARGEBASIS"));
                if("BASE".equalsIgnoreCase(chargeInfo.getBreakPoint()) || "MIN".equalsIgnoreCase(chargeInfo.getBreakPoint()) || "MAX".equalsIgnoreCase(chargeInfo.getBreakPoint()) || ("SLAB".equalsIgnoreCase(weightBreak) && "FLAT".equalsIgnoreCase(rateType)) || ("BOTH".equalsIgnoreCase(rateType) && "FLAT".equalsIgnoreCase(chargeInfo.getRateIndicator()))) //MODIFIED FOR 183812
              {
                chargeInfo.setBasis("Per Shipment");
              }
               else
               {
                if("1".equalsIgnoreCase(rs2.getString("SHMODE")) && "LIST".equalsIgnoreCase(rs2.getString("WEIGHT_BREAK")))
                {
                  chargeInfo.setBasis("Per ULD");
                }
                else if(("2".equalsIgnoreCase(rs2.getString("SHMODE"))||"4".equalsIgnoreCase(rs2.getString("SHMODE"))) && "LIST".equalsIgnoreCase(rs2.getString("WEIGHT_BREAK")))
                {
                    //@@Modified by Kameswari for the WPBN issue on 16/12/08
                    // if(chargeInfo.getBreakPoint().endsWith("CAF"))
                    if(chargeInfo.getBreakPoint().endsWith("CAF")|| chargeInfo.getBreakPoint().endsWith("caf"))
                    {
                        chargeInfo.setBasis(" Percent of Freight");
                    }
                    else
                    {
                        chargeInfo.setBasis("Per Container");
                    }
                }
                else if("Per Kg".equalsIgnoreCase(rs2.getString("CHARGEBASIS")))
                {
                  chargeInfo.setBasis("Per Kilogram");
                }
                else if("Per Lb".equalsIgnoreCase(rs2.getString("CHARGEBASIS")))
                {
                  chargeInfo.setBasis("Per Pound");
                }
                else if("Per CBM".equalsIgnoreCase(rs2.getString("CHARGEBASIS")))
                {
                  chargeInfo.setBasis("Per Cubic Meter");
                }
                else if("Per CFT".equalsIgnoreCase(rs2.getString("CHARGEBASIS")))
                {
                  chargeInfo.setBasis("Per Cubic Feet");
                }
                else
                  chargeInfo.setBasis(rs2.getString("CHARGEBASIS"));
              }
              }
              chargeInfo.setRatio(rs2.getString("DENSITY_RATIO"));
                  
              chargeInfo.setSellRate(sellRate);
              chargeInfo.setLineNumber(rs2.getInt("LINE_NO"));
     }
     
      else
      {
          chargesDOB  = new MultiQuoteChargeInfo();
          freightChargesList.add(chargesDOB); 
                
              
          chargesDOB.setBuyChargeId(rs2.getString("BUY_CHARGE_ID"));
          chargesDOB.setSellChargeId(rs2.getString("SELLCHARGEID"));
          chargesDOB.setVersionNo(rs2.getString("VERSION_NO"));
         //@@Added by Kameswari for the WPBN issue-146448 on 24/12/08
            chargesDOB.setFrequency(rs2.getString("FREQUENCY"));
          chargesDOB.setCarrier(rs2.getString("CARRIER"));
          chargesDOB.setTransitTime(rs2.getString("TRANSITTIME"));
          chargesDOB.setValidUpto(rs2.getTimestamp("VALIDUPTO"));
          chargesDOB.setFrequencyChecked(rs2.getString("FREQUENCY_CHECKED"));
          chargesDOB.setCarrierChecked(rs2.getString("CARRIER_CHECKED"));
         chargesDOB.setTransitTimeChecked(rs2.getString("TRANSITTIME_CHECKED"));
         chargesDOB.setRateValidityChecked(rs2.getString("RATEVALIDITY_CHECKED"));
         //@@Added by kiran.v on 08/08/2011 for Wpbn Issue 258778
         chargesDOB.setServiceChecked("on");
         chargesDOB.setVersionNo(rs2.getString("VERSION_NO"));
          chargesDOB.setBuyChargeLaneNo(rs2.getString("LANE_NO"));
          chargesDOB.setTerminalId(rs2.getString("TERMINALID"));
          chargesDOB.setMarginDiscountFlag(rs2.getString("MARGIN_DISCOUNT_FLAG"));
           chargesDOB.setSelectedFlag(rs2.getString("SELECTED_FLAG"));            
          chargesDOB.setSellBuyFlag(flag);
              chargesDOB.setVersionNo(rs2.getString("VERSION_NO"));  
          chargesDOB.setChargeDescriptionId("Freight Rate");
          chargesDOB.setCostIncurredAt(rs2.getString("COST_INCURREDAT"));
           chargesDOB.setConsoleType(rs2.getString("CONSOLE_TYPE"));           
          chargeInfoList  = new ArrayList();
              chargeInfo  = new MultiQuoteChargeInfo();
              chargeInfoList.add(chargeInfo);
              
              chargesDOB.setChargeInfoList(chargeInfoList);
              
              chargeInfo.setBreakPoint(rs2.getString("CHARGESLAB"));
                if("A FREIGHT RATE".equalsIgnoreCase(rs2.getString("RATE_DESCRIPTION")))
                 chargeInfo.setRateDescription("FREIGHT RATE");
              else   if("C.P.S.S".equalsIgnoreCase(rs2.getString("RATE_DESCRIPTION")))
                 chargeInfo.setRateDescription("P.S.S");
              else
                 chargeInfo.setRateDescription(rs2.getString("RATE_DESCRIPTION")!=null?rs2.getString("RATE_DESCRIPTION"):"FREIGHT RATE");//@@Added by Kameswari for Surcharge Enhancemenst

              if(rs2.getString("CURRENCY")!=null && rs2.getString("CURRENCY").trim().length()!=0)
                chargeInfo.setCurrency(rs2.getString("CURRENCY"));
              else
                chargeInfo.setCurrency(masterDOB.getTerminalCurrency());
                
              chargeInfo.setBuyRate(rs2.getDouble("BUYRATE"));
              chargeInfo.setRecOrConSellRrate(rs2.getDouble("SELLRATE"));
              chargeInfo.setSellChargeMargin(rs2.getDouble("MARGINVALUE"));
              chargeInfo.setSellChargeMarginType(rs2.getString("MARGIN_TYPE"));
              chargeInfo.setRateIndicator(rs2.getString("RATE_INDICATOR"));
              
              weightBreak = rs2.getString("WEIGHT_BREAK");
              rateType    = rs2.getString("RATE_TYPE");
              //chargeInfo.setBasis(rs2.getString("CHARGEBASIS"));
            /*  if("BASE".equalsIgnoreCase(chargeInfo.getBreakPoint()) || "MIN".equalsIgnoreCase(chargeInfo.getBreakPoint()) || "MAX".equalsIgnoreCase(chargeInfo.getBreakPoint()) || ("SLAB".equalsIgnoreCase(weightBreak) && "FLAT".equalsIgnoreCase(rateType))  || ("BOTH".equalsIgnoreCase(rateType) && "F".equalsIgnoreCase(chargeInfo.getRateIndicator())))
              {
                chargeInfo.setBasis("Per Shipment");
              }
              else
              {
                //chargeInfo.setBasis(rs2.getString("CHARGEBASIS"));
                if("1".equalsIgnoreCase(rs2.getString("SHMODE")) && "LIST".equalsIgnoreCase(rs2.getString("WEIGHT_BREAK")))
                {
                  chargeInfo.setBasis("Per ULD");
                }
                else if(("2".equalsIgnoreCase(rs2.getString("SHMODE"))||"4".equalsIgnoreCase(rs2.getString("SHMODE"))) && "LIST".equalsIgnoreCase(rs2.getString("WEIGHT_BREAK")))
                {
                  chargeInfo.setBasis("Per Container");
                }
                else if("Per Kg".equalsIgnoreCase(rs2.getString("CHARGEBASIS")))
                {
                  chargeInfo.setBasis("Per Kilogram");
                }
                else if("Per Lb".equalsIgnoreCase(rs2.getString("CHARGEBASIS")))
                {
                  chargeInfo.setBasis("Per Pound");
                }
                else if("Per CBM".equalsIgnoreCase(rs2.getString("CHARGEBASIS")))
                {
                  chargeInfo.setBasis("Per Cubic Meter");
                }
                else if("Per CFT".equalsIgnoreCase(rs2.getString("CHARGEBASIS")))
                {
                  chargeInfo.setBasis("Per Cubic Feet");
                }
                else
                  chargeInfo.setBasis(rs2.getString("CHARGEBASIS"));
              }*/
                if(!("FREIGHT RATE".equalsIgnoreCase(chargeInfo.getRateDescription())))
              {
              /*  if(chargeInfo.getBreakPoint().endsWith("CAF")||chargeInfo.getBreakPoint().endsWith("BAF")
             ||chargeInfo.getBreakPoint().endsWith("CSF")||chargeInfo.getBreakPoint().endsWith("PSS"))*///@@Modified by Kameswari for the WPBN issue on 16/12/08
             if(chargeInfo.getBreakPoint().equalsIgnoreCase("CSF"))
             {
                  chargeInfo.setBasis("Per Shipment");
             }
                
             else  if(chargeInfo.getBreakPoint().endsWith("CAF")||chargeInfo.getBreakPoint().endsWith("BAF")
             ||chargeInfo.getBreakPoint().endsWith("CSF")||chargeInfo.getBreakPoint().endsWith("PSS")||chargeInfo.getBreakPoint().endsWith("caf")||chargeInfo.getBreakPoint().endsWith("baf")
             ||chargeInfo.getBreakPoint().endsWith("csf")||chargeInfo.getBreakPoint().endsWith("pss"))
             {
                 if(chargeInfo.getBreakPoint().endsWith("CAF")||chargeInfo.getBreakPoint().endsWith("caf"))
               {
                  chargeInfo.setBasis("Percent of Freight");
               }
               else
               {
                 chargeInfo.setBasis("Per Container");
               }
              }
               else if(chargeInfo.getBreakPoint().equalsIgnoreCase("BAFMIN")||chargeInfo.getBreakPoint().equalsIgnoreCase("CAFMIN")
           ||chargeInfo.getBreakPoint().equalsIgnoreCase("PSSMIN")||chargeInfo.getBreakPoint().equalsIgnoreCase("FSMIN")||chargeInfo.getBreakPoint().equalsIgnoreCase("SSMIN"))
             {
                 chargeInfo.setBasis("Per Shipment");
             }
             else if(chargeInfo.getBreakPoint().equalsIgnoreCase("BAFM3")||chargeInfo.getBreakPoint().equalsIgnoreCase("CAF%")
                ||chargeInfo.getBreakPoint().equalsIgnoreCase("PSSM3"))
             {
                if(chargeInfo.getBreakPoint().equalsIgnoreCase("BAFM3")||chargeInfo.getBreakPoint().equalsIgnoreCase("PSSM3"))
                {
                      chargeInfo.setBasis("per Cubic Meter");
                }
                else
                {
                     chargeInfo.setBasis("Percent of Freight");
                }
             }
             
             else if(chargeInfo.getBreakPoint().equalsIgnoreCase("FSBASIC")||chargeInfo.getBreakPoint().equalsIgnoreCase("SSBASIC"))
             {
                  chargeInfo.setBasis("Per Shipment");
             }
              else if(chargeInfo.getBreakPoint().equalsIgnoreCase("FSKG")||chargeInfo.getBreakPoint().equalsIgnoreCase("SSKG"))
             {
                   chargeInfo.setBasis("Per Kg");
             }
             else if(chargeInfo.getBreakPoint().equalsIgnoreCase("SURCHARGE"))
             {
                  chargeInfo.setBasis("Percent of Freight");
             }
              }   
               //@@Surcharge Enhancements
              else
              {
                //chargeInfo.setBasis(rs2.getString("CHARGEBASIS"));
                if("BASE".equalsIgnoreCase(chargeInfo.getBreakPoint()) || "MIN".equalsIgnoreCase(chargeInfo.getBreakPoint()) || "MAX".equalsIgnoreCase(chargeInfo.getBreakPoint()) || ("SLAB".equalsIgnoreCase(weightBreak) && "FLAT".equalsIgnoreCase(rateType)) || ("BOTH".equalsIgnoreCase(rateType) && "FLAT".equalsIgnoreCase(chargeInfo.getRateIndicator())))//MODIFIED FOR 183812
              {
                chargeInfo.setBasis("Per Shipment");
              }
              
              else
              {
                if("1".equalsIgnoreCase(rs2.getString("SHMODE")) && "LIST".equalsIgnoreCase(rs2.getString("WEIGHT_BREAK")))
                {
                  chargeInfo.setBasis("Per ULD");
                }
                else if(("2".equalsIgnoreCase(rs2.getString("SHMODE"))||"4".equalsIgnoreCase(rs2.getString("SHMODE"))) && "LIST".equalsIgnoreCase(rs2.getString("WEIGHT_BREAK")))
                {
                    if(chargeInfo.getBreakPoint().endsWith("CAF")||chargeInfo.getBreakPoint().endsWith("caf"))
                    {
                        chargeInfo.setBasis(" Percent of Freight");
                    }
                    else
                    {
                        chargeInfo.setBasis("Per Container");
                    }
                }
                else if("Per Kg".equalsIgnoreCase(rs2.getString("CHARGEBASIS")))
                {
                  chargeInfo.setBasis("Per Kilogram");
                }
                else if("Per Lb".equalsIgnoreCase(rs2.getString("CHARGEBASIS")))
                {
                  chargeInfo.setBasis("Per Pound");
                }
                else if("Per CBM".equalsIgnoreCase(rs2.getString("CHARGEBASIS")))
                {
                  chargeInfo.setBasis("Per Cubic Meter");
                }
                else if("Per CFT".equalsIgnoreCase(rs2.getString("CHARGEBASIS")))
                {
                  chargeInfo.setBasis("Per Cubic Feet");
                }
                else
                  chargeInfo.setBasis(rs2.getString("CHARGEBASIS"));
              }
              }
              chargeInfo.setRatio(rs2.getString("DENSITY_RATIO"));
              
               /*if("A FREIGHT RATE".equalsIgnoreCase(rs2.getString("RATE_DESCRIPTION")))
                 chargeInfo.setRateDescription("FREIGHT RATE");
              else   if("C.P.S.S".equalsIgnoreCase(rs2.getString("RATE_DESCRIPTION")))
                 chargeInfo.setRateDescription("P.S.S");
              else
                 chargeInfo.setRateDescription(rs2.getString("RATE_DESCRIPTION")!=null?rs2.getString("RATE_DESCRIPTION"):"FREIGHT RATE");//@@Added by Kameswari for Surcharge Enhancemenst*/
              if("M".equalsIgnoreCase(chargesDOB.getMarginDiscountFlag()) || chargesDOB.getMarginDiscountFlag()==null)
              {
                 chargeInfo.setMarginType(rs2.getString("MARGIN_TYPE"));
                 if("A".equalsIgnoreCase(rs2.getString("MARGIN_TYPE")))
               {
                
                   //@@Modified by kameswari for the WPBN issue-61235
                // if(rs6.next()&&flag.equalsIgnoreCase(rs6.getString("SELL_BUY_FLAG"))&&rs2.getString("CHARGESLAB").equalsIgnoreCase(rs6.getString("BREAK_POINT")))
                   if(flag.equalsIgnoreCase(sellBuyFlag))
                   {
                    if("modifiedQuote".equalsIgnoreCase(quoteType))
                    {
                     sellRate  = rs2.getDouble("BUYRATE")+rs2.getDouble("MARGINVALUE");
                     chargeInfo.setMargin(rs2.getDouble("MARGINVALUE"));
                    }
                    else
                    {
                      if(rs6.next())
                       {
                          sellRate  = rs6.getDouble("BUY_RATE")+rs2.getDouble("MARGINVALUE");
                          marginValue = sellRate-rs2.getDouble("BUYRATE"); 
                          chargeInfo.setMargin(marginValue);
                       }
                    } 
                 }
                 else
                 {
                   sellRate  = rs2.getDouble("BUYRATE")+rs2.getDouble("MARGINVALUE");
                   chargeInfo.setMargin(rs2.getDouble("MARGINVALUE"));
                  }
                   //@@WPBN issue-61235
              }
              else if("P".equalsIgnoreCase(rs2.getString("MARGIN_TYPE")))
              {
                
                  //@@Modified by kameswari for the WPBN issue-61235
               ///  if(rs6.next()&&flag.equalsIgnoreCase(rs6.getString("SELL_BUY_FLAG"))&&rs2.getString("CHARGESLAB").equalsIgnoreCase(rs6.getString("BREAK_POINT")))
                 if(flag.equalsIgnoreCase(sellBuyFlag))
                   {
                    if("modifiedQuote".equalsIgnoreCase(quoteType))
                    {
                      sellRate  = rs2.getDouble("BUYRATE")+(rs2.getDouble("BUYRATE")*rs2.getDouble("MARGINVALUE")/100);
                      chargeInfo.setMargin(rs2.getDouble("MARGINVALUE"));
                    }
                     else
                    {
                     if(rs6.next())
                       {
                      sellRate  = rs6.getDouble("BUY_RATE")+(rs6.getDouble("BUY_RATE")*rs2.getDouble("MARGINVALUE")/100);
                      marginValue = (sellRate-rs2.getDouble("BUYRATE"))*100/rs2.getDouble("BUYRATE"); 
                      chargeInfo.setMargin(marginValue);
                       }
                    }
                 }
                 else
                 {
                     sellRate  = rs2.getDouble("BUYRATE")+(rs2.getDouble("BUYRATE")*rs2.getDouble("MARGINVALUE")/100);
                     chargeInfo.setMargin(rs2.getDouble("MARGINVALUE"));
                 }
                   //@@WPBN issue-61235
                }
          
              }
              else
              {
                
                chargeInfo.setDiscountType(rs2.getString("MARGIN_TYPE"));
                
                if("A".equalsIgnoreCase(rs2.getString("MARGIN_TYPE")))
                {
                    //@@Modified by kameswari for the WPBN issue-61235
                 //  if(rs6.next()&&flag.equalsIgnoreCase(rs6.getString("SELL_BUY_FLAG"))&&rs2.getString("CHARGESLAB").equalsIgnoreCase(rs6.getString("BREAK_POINT")))
                  if(flag.equalsIgnoreCase(sellBuyFlag))
                    {
                    if("modifiedQuote".equalsIgnoreCase(quoteType))
                   {
                       sellRate  = rs2.getDouble("SELLRATE")-rs2.getDouble("MARGINVALUE");
                       chargeInfo.setDiscount(rs2.getDouble("MARGINVALUE"));
                  }
                  else
                  {
                      if(rs6.next())
                       {
                      sellRate  = rs6.getDouble("R_SELL_RATE")-rs2.getDouble("MARGINVALUE");
                      marginValue  =rs2.getDouble("SELLRATE") -sellRate;
                      chargeInfo.setDiscount(marginValue);
                       }
                  }
                  }
                 else
                 {
                       sellRate  = rs2.getDouble("SELLRATE")-rs2.getDouble("MARGINVALUE");
                       chargeInfo.setDiscount(rs2.getDouble("MARGINVALUE"));
                 }
                   //@@WPBN issue-61235
                }
                else if("P".equalsIgnoreCase(rs2.getString("MARGIN_TYPE")))
                {
                 
                  //@@Modified by kameswari for the WPBN issue-61235
              //   if(rs6.next()&&flag.equalsIgnoreCase(rs6.getString("SELL_BUY_FLAG"))&&rs2.getString("CHARGESLAB").equalsIgnoreCase(rs6.getString("BREAK_POINT")))
               if(flag.equalsIgnoreCase(sellBuyFlag))
                 {
                  if("modifiedQuote".equalsIgnoreCase(quoteType))
                  {
                      sellRate  = rs2.getDouble("SELLRATE")-(rs2.getDouble("SELLRATE")*rs2.getDouble("MARGINVALUE")/100);
                      chargeInfo.setDiscount(rs2.getDouble("MARGINVALUE"));
                   }
                  else
                  {
                     if(rs6.next())
                       {
                     sellRate  = rs6.getDouble("R_SELL_RATE")-(rs6.getDouble("R_SELL_RATE")*rs2.getDouble("MARGINVALUE")/100);
                     marginValue = (rs2.getDouble("SELLRATE")-sellRate)*100/rs2.getDouble("SELLRATE");
                     chargeInfo.setDiscount(marginValue);
                       }
                  }
                 }
                 else
                 {
                    sellRate  = rs2.getDouble("SELLRATE")-(rs2.getDouble("SELLRATE")*rs2.getDouble("MARGINVALUE")/100);
                    chargeInfo.setDiscount(rs2.getDouble("MARGINVALUE"));
                 }
                   //@@WPBN issue-61235
                 }
                }
               chargeInfo.setSellRate(sellRate);
              chargeInfo.setLineNumber(rs2.getInt("LINE_NO"));
         
      }
  }
    else
    {
      ArrayList               ratesList = new ArrayList();
      MultiQuoteFreightRSRCSRDOB   freightDOB= new MultiQuoteFreightRSRCSRDOB();
      
     
      if(legDetails==null)
        legDetails  = new ArrayList();
      
      legDOB = new MultiQuoteFreightLegSellRates();
      selectedFrtIndices  = new int[1];
      selectedFrtIndices[0] = 0;
      selectedFreightSellRateIndex = new Integer[1];
      selectedFreightSellRateIndex[0] = 0;
      
      legDetails.add(legDOB);
      
      freightChargesList           = new ArrayList();
      
      legDOB.setFreightChargesList(freightChargesList);
      legDOB.setSelectedFreightChargesListIndices(selectedFrtIndices);
      legDOB.setSelectedFreightSellRateIndex(selectedFreightSellRateIndex);
      legDOB.setOrigin(rs2.getString("ORG"));
      legDOB.setDestination(rs2.getString("DEST"));
      legDOB.setLegSerialNo(rs2.getInt("LEG_SL_NO"));
      legDOB.setShipmentMode(rs2.getInt("SHMODE"));
      legDOB.setServiceLevel(rs2.getString("SRV_LEVEL"));
  
      if("SBR".equalsIgnoreCase(flag) && finalDOB.getLegDetails()!=null)
      {
        spotRateDetails = finalDOB.getLegDetails();
        spotLegDOB      = (MultiQuoteFreightLegSellRates)spotRateDetails.get(rs2.getInt("LANE_NO"));//@@Refer getMasterInfo() in QMSQuoteSessionBean;
        legDOB.setDensityRatio(spotLegDOB.getDensityRatio());
        legDOB.setUom(spotLegDOB.getUom());
        legDOB.setSpotRatesType(spotLegDOB.getSpotRatesType());
        legDOB.setWeightBreaks(spotLegDOB.getWeightBreaks());
        legDOB.setCurrency(spotLegDOB.getCurrency());//@@added by kameswari for the WPBN issue-30908
        legDOB.setSpotRateDetails(spotLegDOB.getSpotRateDetails());
        legDOB.setSpotRatesFlag(spotLegDOB.isSpotRatesFlag());
      }
      
      
      freightDOB.setServiceLevelId(legDOB.getServiceLevel());
      freightDOB.setWeightBreakType(rs2.getString("WEIGHT_BREAK"));
      freightDOB.setBuyRateId(rs2.getInt("BUY_CHARGE_ID"));
      freightDOB.setRsrOrCsrFlag(rs2.getString("SEL_BUY_FLAG"));
      freightDOB.setLaneNo(rs2.getInt("LANE_NO"));
      
      ratesList.add(freightDOB);
      legDOB.setRates(ratesList);
      
      //legDOB.setSelectedFreightSellRateIndex(new Integer[0]);
      
      chargesDOB  = new MultiQuoteChargeInfo();
      
      freightChargesList.add(chargesDOB);
        
      chargesDOB.setChargeDescriptionId("Freight Rate");
      chargesDOB.setBuyChargeId(rs2.getString("BUY_CHARGE_ID"));
      chargesDOB.setSellChargeId(rs2.getString("SELLCHARGEID"));
      chargesDOB.setSelectedFlag(rs2.getString("SELECTED_FLAG"));
      chargesDOB.setBuyChargeLaneNo(rs2.getString("LANE_NO"));
      chargesDOB.setTerminalId(rs2.getString("TERMINALID"));
      chargesDOB.setMarginDiscountFlag(rs2.getString("MARGIN_DISCOUNT_FLAG"));
      //@@Added by Kameswari for the WPBN issue-146448 on 24/12/08
            chargesDOB.setFrequency(rs2.getString("FREQUENCY"));
          chargesDOB.setCarrier(rs2.getString("CARRIER"));
          chargesDOB.setTransitTime(rs2.getString("TRANSITTIME"));
          chargesDOB.setValidUpto(rs2.getTimestamp("VALIDUPTO"));
          chargesDOB.setFrequencyChecked(rs2.getString("FREQUENCY_CHECKED"));
          chargesDOB.setCarrierChecked(rs2.getString("CARRIER_CHECKED"));
         chargesDOB.setTransitTimeChecked(rs2.getString("TRANSITTIME_CHECKED"));
         chargesDOB.setRateValidityChecked(rs2.getString("RATEVALIDITY_CHECKED"));
       //@@Added by kiran.v on 08/08/2011 for Wpbn Issue 258778
         chargesDOB.setServiceChecked("on");
         chargesDOB.setVersionNo(rs2.getString("VERSION_NO"));
      chargesDOB.setSellBuyFlag(flag);
      
      chargesDOB.setCostIncurredAt(rs2.getString("COST_INCURREDAT"));
        chargesDOB.setConsoleType(rs2.getString("CONSOLE_TYPE"));    
         chargeInfoList  = new ArrayList();
      chargeInfo  = new MultiQuoteChargeInfo();
      chargeInfoList.add(chargeInfo);
      
      chargesDOB.setChargeInfoList(chargeInfoList);
      
      chargeInfo.setBreakPoint(rs2.getString("CHARGESLAB"));
      if(rs2.getString("CURRENCY")!=null && rs2.getString("CURRENCY").trim().length()!=0)
         chargeInfo.setCurrency(rs2.getString("CURRENCY"));
      else
         chargeInfo.setCurrency(masterDOB.getTerminalCurrency());
         
      chargeInfo.setBuyRate(rs2.getDouble("BUYRATE"));
      chargeInfo.setRecOrConSellRrate(rs2.getDouble("SELLRATE"));
      chargeInfo.setSellChargeMargin(rs2.getDouble("MARGINVALUE"));
      chargeInfo.setSellChargeMarginType(rs2.getString("MARGIN_TYPE"));
      chargeInfo.setRateIndicator(rs2.getString("RATE_INDICATOR"));
      weightBreak   = rs2.getString("WEIGHT_BREAK");
      rateType      = rs2.getString("RATE_TYPE");
      //chargeInfo.setBasis(rs2.getString("CHARGEBASIS"));
      /*if("BASE".equalsIgnoreCase(chargeInfo.getBreakPoint()) || "MIN".equalsIgnoreCase(chargeInfo.getBreakPoint()) || "MAX".equalsIgnoreCase(chargeInfo.getBreakPoint()) || ("SLAB".equalsIgnoreCase(weightBreak) && "FLAT".equalsIgnoreCase(rateType))  || ("BOTH".equalsIgnoreCase(rateType) && "F".equalsIgnoreCase(chargeInfo.getRateIndicator())))
      {
        chargeInfo.setBasis("Per Shipment");
      }
      else
      {
       //chargeInfo.setBasis(rs2.getString("CHARGEBASIS"));
        if("1".equalsIgnoreCase(rs2.getString("SHMODE")) && "LIST".equalsIgnoreCase(rs2.getString("WEIGHT_BREAK")))
        {
          chargeInfo.setBasis("Per ULD");
        }
        else if(("2".equalsIgnoreCase(rs2.getString("SHMODE"))||"4".equalsIgnoreCase(rs2.getString("SHMODE"))) && "LIST".equalsIgnoreCase(rs2.getString("WEIGHT_BREAK")))
        {
          chargeInfo.setBasis("Per Container");
        }
        else if("Per Kg".equalsIgnoreCase(rs2.getString("CHARGEBASIS")))
        {
          chargeInfo.setBasis("Per Kilogram");
        }
        else if("Per Lb".equalsIgnoreCase(rs2.getString("CHARGEBASIS")))
        {
          chargeInfo.setBasis("Per Pound");
        }
        else if("Per CBM".equalsIgnoreCase(rs2.getString("CHARGEBASIS")))
        {
          chargeInfo.setBasis("Per Cubic Meter");
        }
        else if("Per CFT".equalsIgnoreCase(rs2.getString("CHARGEBASIS")))
        {
          chargeInfo.setBasis("Per Cubic Feet");
        }
        else
          chargeInfo.setBasis(rs2.getString("CHARGEBASIS"));
      }*/
        if("A FREIGHT RATE".equalsIgnoreCase(rs2.getString("RATE_DESCRIPTION")))
                 chargeInfo.setRateDescription("FREIGHT RATE");
              else   if("C.P.S.S".equalsIgnoreCase(rs2.getString("RATE_DESCRIPTION")))
                 chargeInfo.setRateDescription("P.S.S");
              else
                 chargeInfo.setRateDescription(rs2.getString("RATE_DESCRIPTION")!=null?rs2.getString("RATE_DESCRIPTION"):"FREIGHT RATE");//@@Added by Kameswari for Surcharge Enhancemenst

          if(!("FREIGHT RATE".equalsIgnoreCase(chargeInfo.getRateDescription())))
              {
               
                 if(chargeInfo.getBreakPoint().equalsIgnoreCase("CSF"))
                 {
                    chargeInfo.setBasis("Per Shipment");
                 }                   
               else  if(chargeInfo.getBreakPoint().endsWith("CAF")||chargeInfo.getBreakPoint().endsWith("BAF")
             ||chargeInfo.getBreakPoint().endsWith("CSF")||chargeInfo.getBreakPoint().endsWith("PSS")||chargeInfo.getBreakPoint().endsWith("caf")||chargeInfo.getBreakPoint().endsWith("baf")
             ||chargeInfo.getBreakPoint().endsWith("csf")||chargeInfo.getBreakPoint().endsWith("pss"))
             {
                 if(chargeInfo.getBreakPoint().endsWith("CAF")||chargeInfo.getBreakPoint().endsWith("caf"))
               {
                  chargeInfo.setBasis("Percent of Freight");
               }
               else
               {
                 chargeInfo.setBasis("Per Container");
               }
            }
               else if(chargeInfo.getBreakPoint().equalsIgnoreCase("BAFMIN")||chargeInfo.getBreakPoint().equalsIgnoreCase("CAFMIN")
           ||chargeInfo.getBreakPoint().equalsIgnoreCase("PSSMIN")||chargeInfo.getBreakPoint().equalsIgnoreCase("FSMIN")||chargeInfo.getBreakPoint().equalsIgnoreCase("SSMIN"))
             {
                 chargeInfo.setBasis("Per Shipment");
             }
             else if(chargeInfo.getBreakPoint().equalsIgnoreCase("BAFM3")||chargeInfo.getBreakPoint().equalsIgnoreCase("CAF%")
                ||chargeInfo.getBreakPoint().equalsIgnoreCase("PSSM3"))
             {
                if(chargeInfo.getBreakPoint().equalsIgnoreCase("BAFM3")||chargeInfo.getBreakPoint().equalsIgnoreCase("PSSM3"))
                {
                      chargeInfo.setBasis("per Cubic Meter");
                }
                else
                {
                     chargeInfo.setBasis("Percent of Freight");
                }
              }
              
             else if(chargeInfo.getBreakPoint().equalsIgnoreCase("FSBASIC")||chargeInfo.getBreakPoint().equalsIgnoreCase("SSBASIC"))
             {
                  chargeInfo.setBasis("Per Shipment");
             }
              else if(chargeInfo.getBreakPoint().equalsIgnoreCase("FSKG")||chargeInfo.getBreakPoint().equalsIgnoreCase("SSKG"))
             {
                  chargeInfo.setBasis("Per Kg");
             }
             else if(chargeInfo.getBreakPoint().equalsIgnoreCase("SURCHARGE"))
             {
                  chargeInfo.setBasis("Percent of Freight");
             }
              }   
               //@@Surcharge Enhancements
              else
              {
                //chargeInfo.setBasis(rs2.getString("CHARGEBASIS"));
               if("BASE".equalsIgnoreCase(chargeInfo.getBreakPoint()) || "MIN".equalsIgnoreCase(chargeInfo.getBreakPoint()) || "MAX".equalsIgnoreCase(chargeInfo.getBreakPoint()) || ("SLAB".equalsIgnoreCase(weightBreak) && "FLAT".equalsIgnoreCase(rateType)) || ("BOTH".equalsIgnoreCase(rateType) && "FLAT".equalsIgnoreCase(chargeInfo.getRateIndicator())))//MODIFIED FOR 183812
              {
                chargeInfo.setBasis("Per Shipment");
              }
               else
               {
                if("1".equalsIgnoreCase(rs2.getString("SHMODE")) && "LIST".equalsIgnoreCase(rs2.getString("WEIGHT_BREAK")))
                {
                  chargeInfo.setBasis("Per ULD");
                }
                else if(("2".equalsIgnoreCase(rs2.getString("SHMODE"))||"4".equalsIgnoreCase(rs2.getString("SHMODE"))) && "LIST".equalsIgnoreCase(rs2.getString("WEIGHT_BREAK")))
                {
                    if(chargeInfo.getBreakPoint().endsWith("CAF")||chargeInfo.getBreakPoint().endsWith("caf"))
                    {
                        chargeInfo.setBasis("Percent of Freight");
                    }
                    else
                    {
                        chargeInfo.setBasis("Per Container");
                    }
                }
                else if("Per Kg".equalsIgnoreCase(rs2.getString("CHARGEBASIS")))
                {
                  chargeInfo.setBasis("Per Kilogram");
                }
                else if("Per Lb".equalsIgnoreCase(rs2.getString("CHARGEBASIS")))
                {
                  chargeInfo.setBasis("Per Pound");
                }
                else if("Per CBM".equalsIgnoreCase(rs2.getString("CHARGEBASIS")))
                {
                  chargeInfo.setBasis("Per Cubic Meter");
                }
                else if("Per CFT".equalsIgnoreCase(rs2.getString("CHARGEBASIS")))
                {
                  chargeInfo.setBasis("Per Cubic Feet");
                }
                else
                  chargeInfo.setBasis(rs2.getString("CHARGEBASIS"));
               }
              }
      chargeInfo.setRatio(rs2.getString("DENSITY_RATIO"));
      
     /* if("A FREIGHT RATE".equalsIgnoreCase(rs2.getString("RATE_DESCRIPTION")))
         chargeInfo.setRateDescription("FREIGHT RATE");
      else   if("C.P.S.S".equalsIgnoreCase(rs2.getString("RATE_DESCRIPTION")))
         chargeInfo.setRateDescription("P.S.S");
      else
         chargeInfo.setRateDescription(rs2.getString("RATE_DESCRIPTION")!=null?rs2.getString("RATE_DESCRIPTION"):"FREIGHT RATE");//@@Added by Kameswari for Surcharge Enhancemenst*/

     if("M".equalsIgnoreCase(chargesDOB.getMarginDiscountFlag()) || chargesDOB.getMarginDiscountFlag()==null)
      {  
       
        chargeInfo.setMarginType(rs2.getString("MARGIN_TYPE"));
        
        if("A".equalsIgnoreCase(rs2.getString("MARGIN_TYPE")))
        {
         
          //@@Modified by kameswari for the WPBN issue-61235
       // if(rs6.next()&&flag.equalsIgnoreCase(rs6.getString("SELL_BUY_FLAG"))&&rs2.getString("CHARGESLAB").equalsIgnoreCase(rs6.getString("BREAK_POINT")))
         if(flag.equalsIgnoreCase(sellBuyFlag))
          {
          if("modifiedQuote".equalsIgnoreCase(quoteType))
            {
                sellRate  = rs2.getDouble("BUYRATE")+rs2.getDouble("MARGINVALUE"); 
                chargeInfo.setMargin(rs2.getDouble("MARGINVALUE"));
          }
          else
          {
              if(rs6.next())
              {
               sellRate  = rs6.getDouble("BUY_RATE")+rs2.getDouble("MARGINVALUE");
               marginValue = sellRate-rs2.getDouble("BUYRATE"); 
               chargeInfo.setMargin(marginValue);
              }
          }
         }
          else
          {
            sellRate  = rs2.getDouble("BUYRATE")+rs2.getDouble("MARGINVALUE"); 
            chargeInfo.setMargin(rs2.getDouble("MARGINVALUE"));
          }
           //@@WPBN issue-61235
        }
        else if("P".equalsIgnoreCase(rs2.getString("MARGIN_TYPE")))
        {
            
           //@@Modified by kameswari for the WPBN issue-61235
         // if(rs6.next()&&flag.equalsIgnoreCase(rs6.getString("SELL_BUY_FLAG"))&&rs2.getString("CHARGESLAB").equalsIgnoreCase(rs6.getString("BREAK_POINT")))
          if(flag.equalsIgnoreCase(sellBuyFlag))
          {
           if("modifiedQuote".equalsIgnoreCase(quoteType))
            {
             sellRate  = rs2.getDouble("BUYRATE")+(rs2.getDouble("BUYRATE")*rs2.getDouble("MARGINVALUE")/100);
             chargeInfo.setMargin(rs2.getDouble("MARGINVALUE"));
            }
             else
            {
               if(rs6.next())
              {
               sellRate  = rs6.getDouble("BUY_RATE")+(rs6.getDouble("BUY_RATE")*rs2.getDouble("MARGINVALUE")/100);
               marginValue = (sellRate-rs2.getDouble("BUYRATE"))*100/rs2.getDouble("BUYRATE"); 
               chargeInfo.setMargin(marginValue);
              }
            }
          }
        else
        {
              sellRate  = rs2.getDouble("BUYRATE")+(rs2.getDouble("BUYRATE")*rs2.getDouble("MARGINVALUE")/100);
              chargeInfo.setMargin(rs2.getDouble("MARGINVALUE"));
          }
             //@@WPBN issue-61235
        }
         
      }
      else
      {
        chargeInfo.setDiscount(rs2.getDouble("MARGINVALUE"));
        chargeInfo.setDiscountType(rs2.getString("MARGIN_TYPE"));
        
        if("A".equalsIgnoreCase(rs2.getString("MARGIN_TYPE")))
        {
         
           //@@Modified by kameswari for the WPBN issue-61235
         // if(rs6.next()&&flag.equalsIgnoreCase(rs6.getString("SELL_BUY_FLAG"))&&rs2.getString("CHARGESLAB").equalsIgnoreCase(rs6.getString("BREAK_POINT")))
          if(flag.equalsIgnoreCase(sellBuyFlag))
            {
           if("modifiedQuote".equalsIgnoreCase(quoteType))
           {
               sellRate  = rs2.getDouble("SELLRATE")-rs2.getDouble("MARGINVALUE");
               chargeInfo.setDiscount(rs2.getDouble("MARGINVALUE"));
     }
            else
           {
               if(rs6.next())
              {
              sellRate  = rs6.getDouble("R_SELL_RATE")-rs2.getDouble("MARGINVALUE");
              marginValue  =rs2.getDouble("SELLRATE") -sellRate;
              chargeInfo.setDiscount(marginValue);
              }
           }
        }
        else
        {
            sellRate  = rs2.getDouble("SELLRATE")-rs2.getDouble("MARGINVALUE");
            chargeInfo.setDiscount(rs2.getDouble("MARGINVALUE"));
         }
            //@@WPBN issue-61235
        }
        else if("P".equalsIgnoreCase(rs2.getString("MARGIN_TYPE")))
        {
         
          //@@Modified by kameswari for the WPBN issue-61235
       //  if(rs6.next()&&flag.equalsIgnoreCase(rs6.getString("SELL_BUY_FLAG"))&&rs2.getString("CHARGESLAB").equalsIgnoreCase(rs6.getString("BREAK_POINT")))
          if(flag.equalsIgnoreCase(sellBuyFlag))
            {
          if("modifiedQuote".equalsIgnoreCase(quoteType))
          {
            sellRate  = rs2.getDouble("SELLRATE")-(rs2.getDouble("SELLRATE")*rs2.getDouble("MARGINVALUE")/100);
            chargeInfo.setDiscount(rs2.getDouble("MARGINVALUE"));
          }
          else
          {
              if(rs6.next())
              {
             sellRate  = rs6.getDouble("R_SELL_RATE")-(rs6.getDouble("R_SELL_RATE")*rs2.getDouble("MARGINVALUE")/100);
             marginValue = (rs2.getDouble("SELLRATE")-sellRate)*100/rs2.getDouble("SELLRATE");
            chargeInfo.setDiscount(marginValue);
              }
          } 
      }
      else
      {
          sellRate  = rs2.getDouble("SELLRATE")-(rs2.getDouble("SELLRATE")*rs2.getDouble("MARGINVALUE")/100);
          chargeInfo.setDiscount(rs2.getDouble("MARGINVALUE"));
         }
           //@@WPBN issue-61235
        }
       }
      
      chargeInfo.setSellRate(sellRate);
    
      chargeInfo.setLineNumber(rs2.getInt("LINE_NO"));
      
    }
  }
   if(legDetails!=null)
    finalDOB.setLegDetails(legDetails);
  
  
  chargesDOB  = null;
   rs6 =  pStmt.executeQuery();
  //rs4 ResultSet is used for Cartage Charges
  while(rs4.next())
  {
    flag  = rs4.getString("SEL_BUY_FLAG");

     if("Pickup".equalsIgnoreCase(rs4.getString("COST_INCURREDAT")))
    {
    	 for(int i=0;i<masterDOB.getShipperZipCode().length;i++)
    	 {
    if( (masterDOB.getShipperZipCode()!=null && masterDOB.getShipperZipCode()[i].trim().length()!=0)
        ||(masterDOB.getShipperZones()!=null && masterDOB.getShipperZones()[i].indexOf(",")==-1) 
      )
    {
      //isSingleShipperZone = true;
      if(chargesDOB!=null && 
       (
         (chargesDOB.getBuyChargeId()!=null && chargesDOB.getBuyChargeId().equals(rs4.getString("BUY_CHARGE_ID")))
         ||
         (chargesDOB.getSellChargeId()!=null && chargesDOB.getSellChargeId().equals(rs4.getString("SELLCHARGEID")))
       )
      )
     {
        chargeInfo  = new MultiQuoteChargeInfo();
        
        chargeInfoList.add(chargeInfo);
        
        chargeInfo.setBreakPoint(rs4.getString("CHARGESLAB"));
        if(rs4.getString("CURRENCY")!=null && rs4.getString("CURRENCY").trim().length()!=0)
          chargeInfo.setCurrency(rs4.getString("CURRENCY"));
        else
           chargeInfo.setCurrency(masterDOB.getTerminalCurrency());
           
        chargeInfo.setBuyRate(rs4.getDouble("BUYRATE"));
        chargeInfo.setRecOrConSellRrate(rs4.getDouble("SELLRATE"));
        chargeInfo.setSellChargeMargin(rs4.getDouble("MARGINVALUE"));
        chargeInfo.setSellChargeMarginType(rs4.getString("MARGIN_TYPE"));
        chargeInfo.setRateIndicator(rs4.getString("RATE_INDICATOR"));
        weightBreak = rs4.getString("WEIGHT_BREAK");
        rateType    = rs4.getString("RATE_TYPE");
        //chargeInfo.setBasis(rs4.getString("CHARGEBASIS"));
        if("BASE".equalsIgnoreCase(chargeInfo.getBreakPoint()) || "MIN".equalsIgnoreCase(chargeInfo.getBreakPoint()) || "MAX".equalsIgnoreCase(chargeInfo.getBreakPoint()) || ("SLAB".equalsIgnoreCase(weightBreak) && "FLAT".equalsIgnoreCase(rateType))  || ("BOTH".equalsIgnoreCase(rateType) && "F".equalsIgnoreCase(chargeInfo.getRateIndicator())))//MODIFIED FOR 183812
        {
          chargeInfo.setBasis("Per Shipment");
        }
        else
        {
          //chargeInfo.setBasis(rs4.getString("CHARGEBASIS"));
          if("LIST".equalsIgnoreCase(rs4.getString("WEIGHT_BREAK")))
          {
            chargeInfo.setBasis("Per Container");
          }
          else if("Per Kg".equalsIgnoreCase(rs4.getString("CHARGEBASIS")))
          {
            chargeInfo.setBasis("Per Kilogram");
          }
          else if("Per Lb".equalsIgnoreCase(rs4.getString("CHARGEBASIS")))
          {
            chargeInfo.setBasis("Per Pound");
          }
          else if("Per CBM".equalsIgnoreCase(rs4.getString("CHARGEBASIS")))
          {
            chargeInfo.setBasis("Per Cubic Meter");
          }
          else if("Per CFT".equalsIgnoreCase(rs4.getString("CHARGEBASIS")))
          {
            chargeInfo.setBasis("Per Cubic Feet");
          }
          else
            chargeInfo.setBasis(rs4.getString("CHARGEBASIS"));
        }
        chargeInfo.setRatio(rs4.getString("DENSITY_RATIO"));
        
        if("M".equalsIgnoreCase(chargesDOB.getMarginDiscountFlag()) || chargesDOB.getMarginDiscountFlag()==null)
        {  
          chargeInfo.setMargin(rs4.getDouble("MARGINVALUE"));
          chargeInfo.setMarginType(rs4.getString("MARGIN_TYPE"));
          
         /* if("A".equalsIgnoreCase(rs4.getString("MARGIN_TYPE")))
            sellRate  = rs4.getDouble("BUYRATE")+rs4.getDouble("MARGINVALUE");
          else if("P".equalsIgnoreCase(rs4.getString("MARGIN_TYPE")))
            sellRate  = rs4.getDouble("BUYRATE")+(rs4.getDouble("BUYRATE")*rs4.getDouble("MARGINVALUE")/100);*/
       if("A".equalsIgnoreCase(rs4.getString("MARGIN_TYPE")))
       {
       //  if(rs6.next()&&flag.equalsIgnoreCase(rs6.getString("SELL_BUY_FLAG"))&&rs4.getString("CHARGESLAB").equalsIgnoreCase(rs6.getString("BREAK_POINT")))
         if(flag.equalsIgnoreCase(sellBuyFlag))
            {
          if("modifiedQuote".equalsIgnoreCase(quoteType))
            {
                sellRate  = rs4.getDouble("BUYRATE")+rs4.getDouble("MARGINVALUE"); 
                chargeInfo.setMargin(rs4.getDouble("MARGINVALUE"));
          }
          else
          {
               if(rs6.next())
               {
               sellRate  = rs6.getDouble("BUY_RATE")+rs4.getDouble("MARGINVALUE");
               marginValue = sellRate-rs4.getDouble("BUYRATE"); 
               chargeInfo.setMargin(marginValue);
               }
          }
         }
         else
         {
             sellRate  = rs4.getDouble("BUYRATE")+rs4.getDouble("MARGINVALUE"); 
             chargeInfo.setMargin(rs4.getDouble("MARGINVALUE"));
         }
        }
        else if("P".equalsIgnoreCase(rs4.getString("MARGIN_TYPE")))
        {
           //if(rs6.next()&&flag.equalsIgnoreCase(rs6.getString("SELL_BUY_FLAG"))&&rs4.getString("CHARGESLAB").equalsIgnoreCase(rs6.getString("BREAK_POINT")))
          if(flag.equalsIgnoreCase(sellBuyFlag))
            {
            if("modifiedQuote".equalsIgnoreCase(quoteType))
            {
               sellRate  = rs4.getDouble("BUYRATE")+(rs4.getDouble("BUYRATE")*rs4.getDouble("MARGINVALUE")/100);
             chargeInfo.setMargin(rs4.getDouble("MARGINVALUE"));
            }
            else
            {
              if(rs6.next())
               {
               
        
               sellRate  = rs6.getDouble("BUY_RATE")-(rs6.getDouble("BUY_RATE")*rs4.getDouble("MARGINVALUE")/100);
               marginValue = (rs4.getDouble("BUYRATE")-sellRate)*100/rs4.getDouble("BUYRATE");
               chargeInfo.setMargin(marginValue);
               }
            } 
           }
          else
         {
              sellRate  = rs4.getDouble("BUYRATE")+(rs4.getDouble("BUYRATE")*rs4.getDouble("MARGINVALUE")/100);
             chargeInfo.setMargin(rs4.getDouble("MARGINVALUE"));
            }
        }
     }
        else
        {
          chargeInfo.setDiscount(rs4.getDouble("MARGINVALUE"));
          chargeInfo.setDiscountType(rs4.getString("MARGIN_TYPE"));
          
         if("A".equalsIgnoreCase(rs4.getString("MARGIN_TYPE")))
        {
         
           //@@Modified by kameswari for the WPBN issue-61235
         //   if(rs6.next()&&flag.equalsIgnoreCase(rs6.getString("SELL_BUY_FLAG"))&&rs4.getString("CHARGESLAB").equalsIgnoreCase(rs6.getString("BREAK_POINT")))
             if(flag.equalsIgnoreCase(sellBuyFlag))
            {
             if("modifiedQuote".equalsIgnoreCase(quoteType))
             {
                 sellRate  = rs4.getDouble("SELLRATE")-rs4.getDouble("MARGINVALUE");  
                 chargeInfo.setDiscount(rs4.getDouble("MARGINVALUE"));
               }
              else
             {
                if(rs6.next())
                {
                sellRate  = rs6.getDouble("R_SELL_RATE")-rs4.getDouble("MARGINVALUE");
                marginValue  =rs4.getDouble("SELLRATE") -sellRate;
                chargeInfo.setDiscount(marginValue);
                }
             }
           }
            else
            {
                sellRate  = rs4.getDouble("SELLRATE")-rs4.getDouble("MARGINVALUE");
                chargeInfo.setDiscount(rs4.getDouble("MARGINVALUE"));
             }
            //@@WPBN issue-61235
        }
        else if("P".equalsIgnoreCase(rs4.getString("MARGIN_TYPE")))
        {
         
              //@@Modified by kameswari for the WPBN issue-61235
            // if(rs6.next()&&flag.equalsIgnoreCase(rs6.getString("SELL_BUY_FLAG"))&&rs4.getString("CHARGESLAB").equalsIgnoreCase(rs6.getString("BREAK_POINT")))
             if(flag.equalsIgnoreCase(sellBuyFlag))
            {
              if("modifiedQuote".equalsIgnoreCase(quoteType))
              {
                sellRate  = rs4.getDouble("SELLRATE")-(rs4.getDouble("SELLRATE")*rs4.getDouble("MARGINVALUE")/100);
                chargeInfo.setDiscount(rs4.getDouble("MARGINVALUE"));
              }
              else
              {
                 if(rs6.next())
                {
                 sellRate  = rs6.getDouble("R_SELL_RATE")-(rs6.getDouble("R_SELL_RATE")*rs4.getDouble("MARGINVALUE")/100);
                 marginValue = (rs4.getDouble("SELLRATE")-sellRate)*100/rs4.getDouble("SELLRATE");
                chargeInfo.setDiscount(marginValue);
                }
              } 
            }
          else
          {
              sellRate  = rs4.getDouble("SELLRATE")-(rs4.getDouble("SELLRATE")*rs4.getDouble("MARGINVALUE")/100);
              chargeInfo.setDiscount(rs4.getDouble("MARGINVALUE"));
          }
           //@@WPBN issue-61235
         }
       }
      
        chargeInfo.setSellRate(sellRate);
        chargeInfo.setLineNumber(rs4.getInt("LINE_NO"));

     }
     else
     {         
        chargesDOB  = new MultiQuoteChargeInfo();
        
        chargesDOB.setSellChargeId(rs4.getString("SELLCHARGEID"));
        chargesDOB.setBuyChargeId(rs4.getString("BUY_CHARGE_ID"));
                 
        chargesDOB.setSellBuyFlag(rs4.getString("SEL_BUY_FLAG"));
        
        chargesDOB.setChargeDescriptionId("Pickup Charge");
        chargesDOB.setCostIncurredAt(rs4.getString("COST_INCURREDAT"));
        chargesDOB.setTerminalId(rs4.getString("TERMINALID"));
        chargesDOB.setSelectedFlag(rs4.getString("SELECTED_FLAG"));
        chargesDOB.setMarginDiscountFlag(rs4.getString("MARGIN_DISCOUNT_FLAG"));
        
        chargeInfoList  = new ArrayList();
        chargeInfo      = new MultiQuoteChargeInfo();
        
        chargeInfoList.add(chargeInfo);
        
        chargesDOB.setChargeInfoList(chargeInfoList);
        
        chargeInfo.setBreakPoint(rs4.getString("CHARGESLAB"));
        if(rs4.getString("CURRENCY")!=null && rs4.getString("CURRENCY").trim().length()!=0)
          chargeInfo.setCurrency(rs4.getString("CURRENCY"));
        else
           chargeInfo.setCurrency(masterDOB.getTerminalCurrency());
           
        chargeInfo.setBuyRate(rs4.getDouble("BUYRATE"));
        chargeInfo.setRecOrConSellRrate(rs4.getDouble("SELLRATE"));
        chargeInfo.setSellChargeMargin(rs4.getDouble("MARGINVALUE"));
        chargeInfo.setSellChargeMarginType(rs4.getString("MARGIN_TYPE"));
        chargeInfo.setRateIndicator(rs4.getString("RATE_INDICATOR"));
        weightBreak = rs4.getString("WEIGHT_BREAK");
        rateType    = rs4.getString("RATE_TYPE");
        //chargeInfo.setBasis(rs4.getString("CHARGEBASIS"));
        if("BASE".equalsIgnoreCase(chargeInfo.getBreakPoint()) || "MIN".equalsIgnoreCase(chargeInfo.getBreakPoint()) || "MAX".equalsIgnoreCase(chargeInfo.getBreakPoint()) || ("SLAB".equalsIgnoreCase(weightBreak) && "FLAT".equalsIgnoreCase(rateType))  || ("BOTH".equalsIgnoreCase(rateType) && "F".equalsIgnoreCase(chargeInfo.getRateIndicator())))//MODIFIED FOR 183812
        {
          chargeInfo.setBasis("Per Shipment");
        }
        else
        {
          //chargeInfo.setBasis(rs4.getString("CHARGEBASIS"));
          if("LIST".equalsIgnoreCase(rs4.getString("WEIGHT_BREAK")))
          {
            chargeInfo.setBasis("Per Container");
          }
          else if("Per Kg".equalsIgnoreCase(rs4.getString("CHARGEBASIS")))
          {
            chargeInfo.setBasis("Per Kilogram");
          }
          else if("Per Lb".equalsIgnoreCase(rs4.getString("CHARGEBASIS")))
          {
            chargeInfo.setBasis("Per Pound");
          }
          else if("Per CBM".equalsIgnoreCase(rs4.getString("CHARGEBASIS")))
          {
            chargeInfo.setBasis("Per Cubic Meter");
          }
          else if("Per CFT".equalsIgnoreCase(rs4.getString("CHARGEBASIS")))
          {
            chargeInfo.setBasis("Per Cubic Feet");
          }
          else
            chargeInfo.setBasis(rs4.getString("CHARGEBASIS"));
        }
        chargeInfo.setRatio(rs4.getString("DENSITY_RATIO"));
        
        if("M".equalsIgnoreCase(chargesDOB.getMarginDiscountFlag()) || chargesDOB.getMarginDiscountFlag()==null)
        {  
          chargeInfo.setMargin(rs4.getDouble("MARGINVALUE"));
          chargeInfo.setMarginType(rs4.getString("MARGIN_TYPE"));
          
         /* if("A".equalsIgnoreCase(rs4.getString("MARGIN_TYPE")))
            sellRate  = rs4.getDouble("BUYRATE")+rs4.getDouble("MARGINVALUE");
          else if("P".equalsIgnoreCase(rs4.getString("MARGIN_TYPE")))
            sellRate  = rs4.getDouble("BUYRATE")+(rs4.getDouble("BUYRATE")*rs4.getDouble("MARGINVALUE")/100);
        }
        else
        {
          chargeInfo.setDiscount(rs4.getDouble("MARGINVALUE"));
          chargeInfo.setDiscountType(rs4.getString("MARGIN_TYPE"));
          
          if("A".equalsIgnoreCase(rs4.getString("MARGIN_TYPE")))
            sellRate  = rs4.getDouble("SELLRATE")-rs4.getDouble("MARGINVALUE");
          else if("P".equalsIgnoreCase(rs4.getString("MARGIN_TYPE")))
            sellRate  = rs4.getDouble("SELLRATE")-(rs4.getDouble("SELLRATE")*rs4.getDouble("MARGINVALUE")/100);
        }
          */
              if("A".equalsIgnoreCase(rs4.getString("MARGIN_TYPE")))
       {
        // if(rs6.next()&&flag.equalsIgnoreCase(rs6.getString("SELL_BUY_FLAG"))&&rs4.getString("CHARGESLAB").equalsIgnoreCase(rs6.getString("BREAK_POINT")))
         if(flag.equalsIgnoreCase(sellBuyFlag))
            {
          if("modifiedQuote".equalsIgnoreCase(quoteType))
            {
                sellRate  = rs4.getDouble("BUYRATE")+rs4.getDouble("MARGINVALUE"); 
                chargeInfo.setMargin(rs4.getDouble("MARGINVALUE"));
          }
          else
          {
               if(rs6.next())
               {
               sellRate  = rs6.getDouble("BUY_RATE")+rs4.getDouble("MARGINVALUE");
               marginValue = sellRate-rs4.getDouble("BUYRATE"); 
               chargeInfo.setMargin(marginValue);
               }
          }
         }
         else
         {
             sellRate  = rs4.getDouble("BUYRATE")+rs4.getDouble("MARGINVALUE"); 
             chargeInfo.setMargin(rs4.getDouble("MARGINVALUE"));
         }
        }
        else if("P".equalsIgnoreCase(rs4.getString("MARGIN_TYPE")))
        {
         //  if(rs6.next()&&flag.equalsIgnoreCase(rs6.getString("SELL_BUY_FLAG"))&&rs4.getString("CHARGESLAB").equalsIgnoreCase(rs6.getString("BREAK_POINT")))
          if(flag.equalsIgnoreCase(sellBuyFlag))
            {
            if("modifiedQuote".equalsIgnoreCase(quoteType))
            {
               sellRate  = rs4.getDouble("BUYRATE")+(rs4.getDouble("BUYRATE")*rs4.getDouble("MARGINVALUE")/100);
             chargeInfo.setMargin(rs4.getDouble("MARGINVALUE"));
            }
            else
            {
               if(rs6.next())
               {
              
               sellRate  = rs6.getDouble("BUY_RATE")-(rs6.getDouble("BUY_RATE")*rs4.getDouble("MARGINVALUE")/100);
               marginValue = (rs4.getDouble("BUYRATE")-sellRate)*100/rs4.getDouble("BUYRATE");
               chargeInfo.setMargin(marginValue);
               }
            } 
           }
         else
         {
             sellRate  = rs4.getDouble("BUYRATE")+(rs4.getDouble("BUYRATE")*rs4.getDouble("MARGINVALUE")/100);
             chargeInfo.setMargin(rs4.getDouble("MARGINVALUE"));
           }
        }
     }
        else
        {
          chargeInfo.setDiscount(rs4.getDouble("MARGINVALUE"));
          chargeInfo.setDiscountType(rs4.getString("MARGIN_TYPE"));
          
         if("A".equalsIgnoreCase(rs4.getString("MARGIN_TYPE")))
        {
         
           //@@Modified by kameswari for the WPBN issue-61235
        // if(rs6.next()&&flag.equalsIgnoreCase(rs6.getString("SELL_BUY_FLAG"))&&rs4.getString("CHARGESLAB").equalsIgnoreCase(rs6.getString("BREAK_POINT")))
         if(flag.equalsIgnoreCase(sellBuyFlag))
          {
           if("modifiedQuote".equalsIgnoreCase(quoteType))
           {
               sellRate  = rs4.getDouble("SELLRATE")-rs4.getDouble("MARGINVALUE");
               chargeInfo.setDiscount(rs4.getDouble("MARGINVALUE"));
           }
            else
           {
              if(rs6.next())
               {
              sellRate  = rs6.getDouble("R_SELL_RATE")-rs4.getDouble("MARGINVALUE");
              marginValue  =rs4.getDouble("SELLRATE") -sellRate;
              chargeInfo.setDiscount(marginValue);
               }
           }
        }
        else
        {
            sellRate  = rs4.getDouble("SELLRATE")-rs4.getDouble("MARGINVALUE");
            chargeInfo.setDiscount(rs4.getDouble("MARGINVALUE"));
         }
            //@@WPBN issue-61235
        }
        else if("P".equalsIgnoreCase(rs4.getString("MARGIN_TYPE")))
        {
         
          //@@Modified by kameswari for the WPBN issue-61235
        // if(rs6.next()&&flag.equalsIgnoreCase(rs6.getString("SELL_BUY_FLAG"))&&rs4.getString("CHARGESLAB").equalsIgnoreCase(rs6.getString("BREAK_POINT")))
          if(flag.equalsIgnoreCase(sellBuyFlag))
          {
            if("modifiedQuote".equalsIgnoreCase(quoteType))
            {
              sellRate  = rs4.getDouble("SELLRATE")-(rs4.getDouble("SELLRATE")*rs4.getDouble("MARGINVALUE")/100);
              chargeInfo.setDiscount(rs4.getDouble("MARGINVALUE"));
            }
            else
            {
                if(rs6.next())
               {
               sellRate  = rs6.getDouble("R_SELL_RATE")-(rs6.getDouble("R_SELL_RATE")*rs4.getDouble("MARGINVALUE")/100);
               marginValue = (rs4.getDouble("SELLRATE")-sellRate)*100/rs4.getDouble("SELLRATE");
              chargeInfo.setDiscount(marginValue);
               }
            } 
      }
        else
        {
          sellRate  = rs4.getDouble("SELLRATE")-(rs4.getDouble("SELLRATE")*rs4.getDouble("MARGINVALUE")/100);
          chargeInfo.setDiscount(rs4.getDouble("MARGINVALUE"));
         }
           //@@WPBN issue-61235
        }
       }
        chargeInfo.setSellRate(sellRate);
        chargeInfo.setLineNumber(rs4.getInt("LINE_NO"));
        
        if("Pickup".equalsIgnoreCase(rs4.getString("COST_INCURREDAT")))
        {
          if(originChargesList==null)
            originChargesList = new ArrayList();
          originChargesList.add(chargesDOB);
        }
     }
    }
    }
    	 
    /*else
    {
      tmpFinalDOB = new QuoteFinalDOB();
      tmpFinalDOB.setMasterDOB(masterDOB);
      tmpFinalDOB.setOriginChargesList(originChargesList);
      
      if(masterDOB.getShipperZones()!=null && masterDOB.getShipperZones().trim().length()!=0)
          tmpFinalDOB = getCartages(tmpFinalDOB);
   
      //finalDOB.setOriginChargesList(tmpFinalDOB.getOriginChargesList());
      originChargesList = tmpFinalDOB.getOriginChargesList();
      
      if(tmpFinalDOB.getPickUpCartageRatesList()!=null)
        finalDOB.setPickUpCartageRatesList(tmpFinalDOB.getPickUpCartageRatesList());
      
      finalDOB.setPickZoneZipMap(tmpFinalDOB.getPickZoneZipMap());          
    }*/
  }
   if("Delivery".equalsIgnoreCase(rs4.getString("COST_INCURREDAT")))
   { 
	   for(int i=0;i<masterDOB.getConsigneeZipCode().length;i++)
	   {
    if((masterDOB.getConsigneeZipCode()!=null && masterDOB.getConsigneeZipCode()[i].trim().length()!=0)
        ||(masterDOB.getConsigneeZones()!=null && masterDOB.getConsigneeZones()[i].indexOf(",")==-1))
    {            
        //isSingleConsigneeZone   =  true;
      if(deliveryChargesDOB!=null && 
       (
         (deliveryChargesDOB.getBuyChargeId()!=null && deliveryChargesDOB.getBuyChargeId().equals(rs4.getString("BUY_CHARGE_ID")))
         ||
         (deliveryChargesDOB.getSellChargeId()!=null && deliveryChargesDOB.getSellChargeId().equals(rs4.getString("SELLCHARGEID")))
       )
      )
     {
        chargeInfo  = new MultiQuoteChargeInfo();
        
        chargeInfoList.add(chargeInfo);
        
        chargeInfo.setBreakPoint(rs4.getString("CHARGESLAB"));
        
        if(rs4.getString("CURRENCY")!=null && rs4.getString("CURRENCY").trim().length()!=0)
          chargeInfo.setCurrency(rs4.getString("CURRENCY"));
        else
           chargeInfo.setCurrency(masterDOB.getTerminalCurrency());
           
        chargeInfo.setBuyRate(rs4.getDouble("BUYRATE"));
        chargeInfo.setRecOrConSellRrate(rs4.getDouble("SELLRATE"));
        chargeInfo.setSellChargeMargin(rs4.getDouble("MARGINVALUE"));
        chargeInfo.setSellChargeMarginType(rs4.getString("MARGIN_TYPE"));
        chargeInfo.setRateIndicator(rs4.getString("RATE_INDICATOR"));
        //chargeInfo.setBasis(rs4.getString("CHARGEBASIS"));
        weightBreak   =   rs4.getString("WEIGHT_BREAK");
        rateType      =   rs4.getString("RATE_TYPE");
         if("BASE".equalsIgnoreCase(chargeInfo.getBreakPoint()) || "MIN".equalsIgnoreCase(chargeInfo.getBreakPoint()) || "MAX".equalsIgnoreCase(chargeInfo.getBreakPoint()) || ("SLAB".equalsIgnoreCase(weightBreak) && "FLAT".equalsIgnoreCase(rateType))  || ("BOTH".equalsIgnoreCase(rateType) && "F".equalsIgnoreCase(chargeInfo.getRateIndicator()))) //MODIFIED FOR 183182
        {
          chargeInfo.setBasis("Per Shipment");
        }
        else
        {
          //chargeInfo.setBasis(rs4.getString("CHARGEBASIS"));
          if("LIST".equalsIgnoreCase(rs4.getString("WEIGHT_BREAK")))
          {
            chargeInfo.setBasis("Per Container");
          }
          else if("Per Kg".equalsIgnoreCase(rs4.getString("CHARGEBASIS")))
          {
            chargeInfo.setBasis("Per Kilogram");
          }
          else if("Per Lb".equalsIgnoreCase(rs4.getString("CHARGEBASIS")))
          {
            chargeInfo.setBasis("Per Pound");
          }
          else if("Per CBM".equalsIgnoreCase(rs4.getString("CHARGEBASIS")))
          {
            chargeInfo.setBasis("Per Cubic Meter");
          }
          else if("Per CFT".equalsIgnoreCase(rs4.getString("CHARGEBASIS")))
          {
            chargeInfo.setBasis("Per Cubic Feet");
          }
          else
            chargeInfo.setBasis(rs4.getString("CHARGEBASIS"));
        }
        chargeInfo.setRatio(rs4.getString("DENSITY_RATIO"));
        
        if("M".equalsIgnoreCase(deliveryChargesDOB.getMarginDiscountFlag()) || deliveryChargesDOB.getMarginDiscountFlag()==null)
        {  
          chargeInfo.setMargin(rs4.getDouble("MARGINVALUE"));
          chargeInfo.setMarginType(rs4.getString("MARGIN_TYPE"));
          
         /* if("A".equalsIgnoreCase(rs4.getString("MARGIN_TYPE")))
            sellRate  = rs4.getDouble("BUYRATE")+rs4.getDouble("MARGINVALUE");
          else if("P".equalsIgnoreCase(rs4.getString("MARGIN_TYPE")))
            sellRate  = rs4.getDouble("BUYRATE")+(rs4.getDouble("BUYRATE")*rs4.getDouble("MARGINVALUE")/100);
        }
        else
        {
          chargeInfo.setDiscount(rs4.getDouble("MARGINVALUE"));
          chargeInfo.setDiscountType(rs4.getString("MARGIN_TYPE"));
          
          if("A".equalsIgnoreCase(rs4.getString("MARGIN_TYPE")))
            sellRate  = rs4.getDouble("SELLRATE")-rs4.getDouble("MARGINVALUE");
          else if("P".equalsIgnoreCase(rs4.getString("MARGIN_TYPE")))
            sellRate  = rs4.getDouble("SELLRATE")-(rs4.getDouble("SELLRATE")*rs4.getDouble("MARGINVALUE")/100);
        }*/
          if("A".equalsIgnoreCase(rs4.getString("MARGIN_TYPE")))
       {
         //if(rs6.next()&&flag.equalsIgnoreCase(rs6.getString("SELL_BUY_FLAG"))&&rs4.getString("CHARGESLAB").equalsIgnoreCase(rs6.getString("BREAK_POINT")))
        if(flag.equalsIgnoreCase(sellBuyFlag))
          {
          if("modifiedQuote".equalsIgnoreCase(quoteType))
            {
                sellRate  = rs4.getDouble("BUYRATE")+rs4.getDouble("MARGINVALUE"); 
                chargeInfo.setMargin(rs4.getDouble("MARGINVALUE"));
          }
          else
          {
               if(rs6.next())
               {
               sellRate  = rs6.getDouble("BUY_RATE")+rs4.getDouble("MARGINVALUE");
               marginValue = sellRate-rs4.getDouble("BUYRATE"); 
               chargeInfo.setMargin(marginValue);
             
               
               }
          }
         }
         else
         {
             sellRate  = rs4.getDouble("BUYRATE")+rs4.getDouble("MARGINVALUE"); 
             chargeInfo.setMargin(rs4.getDouble("MARGINVALUE"));
         }
        }
        else if("P".equalsIgnoreCase(rs4.getString("MARGIN_TYPE")))
        {
          // if(rs6.next()&&flag.equalsIgnoreCase(rs6.getString("SELL_BUY_FLAG"))&&rs4.getString("CHARGESLAB").equalsIgnoreCase(rs6.getString("BREAK_POINT")))
          if(flag.equalsIgnoreCase(sellBuyFlag))
          {
            if("modifiedQuote".equalsIgnoreCase(quoteType))
            {
               sellRate  = rs4.getDouble("BUYRATE")+(rs4.getDouble("BUYRATE")*rs4.getDouble("MARGINVALUE")/100);
             chargeInfo.setMargin(rs4.getDouble("MARGINVALUE"));
            }
            else
            {
              if(rs6.next())
               {
               sellRate  = rs6.getDouble("BUY_RATE")-(rs6.getDouble("BUY_RATE")*rs4.getDouble("MARGINVALUE")/100);
               marginValue = (rs4.getDouble("BUYRATE")-sellRate)*100/rs4.getDouble("BUYRATE");
               chargeInfo.setMargin(marginValue);
                
               }
            } 
           }
          else
         {
              sellRate  = rs4.getDouble("BUYRATE")+(rs4.getDouble("BUYRATE")*rs4.getDouble("MARGINVALUE")/100);
             chargeInfo.setMargin(rs4.getDouble("MARGINVALUE"));
        
         }
        }
     }
     else
     {
          chargeInfo.setDiscount(rs4.getDouble("MARGINVALUE"));
          chargeInfo.setDiscountType(rs4.getString("MARGIN_TYPE"));
          
         if("A".equalsIgnoreCase(rs4.getString("MARGIN_TYPE")))
        {
         
           //@@Modified by kameswari for the WPBN issue-61235
        //  if(rs6.next()&&flag.equalsIgnoreCase(rs6.getString("SELL_BUY_FLAG"))&&rs4.getString("CHARGESLAB").equalsIgnoreCase(rs6.getString("BREAK_POINT")))
         if(flag.equalsIgnoreCase(sellBuyFlag))
          {
           if("modifiedQuote".equalsIgnoreCase(quoteType))
           {
               sellRate  = rs4.getDouble("SELLRATE")-rs4.getDouble("MARGINVALUE");
               chargeInfo.setDiscount(rs4.getDouble("MARGINVALUE"));
            }
            else
           {
             if(rs6.next())
             {
              sellRate  = rs6.getDouble("R_SELL_RATE")-rs4.getDouble("MARGINVALUE");
              marginValue  =rs4.getDouble("SELLRATE") -sellRate;
              chargeInfo.setDiscount(marginValue);
             }
           }
        }
        else
        {
            sellRate  = rs4.getDouble("SELLRATE")-rs4.getDouble("MARGINVALUE");
            chargeInfo.setDiscount(rs4.getDouble("MARGINVALUE"));
         }
            //@@WPBN issue-61235
      }
      else if("P".equalsIgnoreCase(rs4.getString("MARGIN_TYPE")))
      {
     
          //@@Modified by kameswari for the WPBN issue-61235
      // if(rs6.next()&&flag.equalsIgnoreCase(rs6.getString("SELL_BUY_FLAG"))&&rs4.getString("CHARGESLAB").equalsIgnoreCase(rs6.getString("BREAK_POINT")))
        if(flag.equalsIgnoreCase(sellBuyFlag))
          {
          if("modifiedQuote".equalsIgnoreCase(quoteType))
          {
            sellRate  = rs4.getDouble("SELLRATE")-(rs4.getDouble("SELLRATE")*rs4.getDouble("MARGINVALUE")/100);
            chargeInfo.setDiscount(rs4.getDouble("MARGINVALUE"));
          }
          else
          {
            if(rs6.next())
             {
             sellRate  = rs6.getDouble("R_SELL_RATE")-(rs6.getDouble("R_SELL_RATE")*rs4.getDouble("MARGINVALUE")/100);
             marginValue = (rs4.getDouble("SELLRATE")-sellRate)*100/rs4.getDouble("SELLRATE");
            chargeInfo.setDiscount(marginValue);
             }
          } 
      }
      else
      {
          sellRate  = rs4.getDouble("SELLRATE")-(rs4.getDouble("SELLRATE")*rs4.getDouble("MARGINVALUE")/100);
          chargeInfo.setDiscount(rs4.getDouble("MARGINVALUE"));
       }
           //@@WPBN issue-61235
      }
     }
      
        chargeInfo.setSellRate(sellRate);
        chargeInfo.setLineNumber(rs4.getInt("LINE_NO"));
       
        
     }
    else
     {         
        deliveryChargesDOB  = new MultiQuoteCharges();
        
        deliveryChargesDOB.setSellChargeId(rs4.getString("SELLCHARGEID"));
        deliveryChargesDOB.setBuyChargeId(rs4.getString("BUY_CHARGE_ID"));
                 
        deliveryChargesDOB.setSellBuyFlag(rs4.getString("SEL_BUY_FLAG"));
        
        deliveryChargesDOB.setChargeDescriptionId("Delivery Charge");
        deliveryChargesDOB.setCostIncurredAt(rs4.getString("COST_INCURREDAT"));
        deliveryChargesDOB.setTerminalId(rs4.getString("TERMINALID"));
        deliveryChargesDOB.setSelectedFlag(rs4.getString("SELECTED_FLAG"));
        deliveryChargesDOB.setMarginDiscountFlag(rs4.getString("MARGIN_DISCOUNT_FLAG"));
        
        chargeInfoList  = new ArrayList();
        chargeInfo      = new MultiQuoteChargeInfo();
        
        chargeInfoList.add(chargeInfo);
        
        deliveryChargesDOB.setChargeInfoList(chargeInfoList);
        
        chargeInfo.setBreakPoint(rs4.getString("CHARGESLAB"));
        if(rs4.getString("CURRENCY")!=null && rs4.getString("CURRENCY").trim().length()!=0)
          chargeInfo.setCurrency(rs4.getString("CURRENCY"));
        else
           chargeInfo.setCurrency(masterDOB.getTerminalCurrency());
           
        chargeInfo.setBuyRate(rs4.getDouble("BUYRATE"));
        chargeInfo.setRecOrConSellRrate(rs4.getDouble("SELLRATE"));
        chargeInfo.setSellChargeMargin(rs4.getDouble("MARGINVALUE"));
        chargeInfo.setSellChargeMarginType(rs4.getString("MARGIN_TYPE"));
        chargeInfo.setRateIndicator(rs4.getString("RATE_INDICATOR"));
        weightBreak   = rs4.getString("WEIGHT_BREAK");
        rateType      = rs4.getString("RATE_TYPE");
        //chargeInfo.setBasis(rs4.getString("CHARGEBASIS"));
        if("BASE".equalsIgnoreCase(chargeInfo.getBreakPoint()) || "MIN".equalsIgnoreCase(chargeInfo.getBreakPoint()) || "MAX".equalsIgnoreCase(chargeInfo.getBreakPoint()) || ("SLAB".equalsIgnoreCase(weightBreak) && "FLAT".equalsIgnoreCase(rateType))  || ("BOTH".equalsIgnoreCase(rateType) && "F".equalsIgnoreCase(chargeInfo.getRateIndicator()))) //MODIFIED FOR 183812
        {
          chargeInfo.setBasis("Per Shipment");
        }
        else
        {
          //chargeInfo.setBasis(rs4.getString("CHARGEBASIS"));
          if("LIST".equalsIgnoreCase(rs4.getString("WEIGHT_BREAK")))
          {
            chargeInfo.setBasis("Per Container");
          }
          else if("Per Kg".equalsIgnoreCase(rs4.getString("CHARGEBASIS")))
          {
            chargeInfo.setBasis("Per Kilogram");
          }
          else if("Per Lb".equalsIgnoreCase(rs4.getString("CHARGEBASIS")))
          {
            chargeInfo.setBasis("Per Pound");
          }
          else if("Per CBM".equalsIgnoreCase(rs4.getString("CHARGEBASIS")))
          {
            chargeInfo.setBasis("Per Cubic Meter");
          }
          else if("Per CFT".equalsIgnoreCase(rs4.getString("CHARGEBASIS")))
          {
            chargeInfo.setBasis("Per Cubic Feet");
          }
          else
            chargeInfo.setBasis(rs4.getString("CHARGEBASIS"));
        }
        chargeInfo.setRatio(rs4.getString("DENSITY_RATIO"));
        
        if("M".equalsIgnoreCase(deliveryChargesDOB.getMarginDiscountFlag()) || deliveryChargesDOB.getMarginDiscountFlag()==null)
        {  
          chargeInfo.setMargin(rs4.getDouble("MARGINVALUE"));
          chargeInfo.setMarginType(rs4.getString("MARGIN_TYPE"));
          
      /*    if("A".equalsIgnoreCase(rs4.getString("MARGIN_TYPE")))
            sellRate  = rs4.getDouble("BUYRATE")+rs4.getDouble("MARGINVALUE");
          else if("P".equalsIgnoreCase(rs4.getString("MARGIN_TYPE")))
            sellRate  = rs4.getDouble("BUYRATE")+(rs4.getDouble("BUYRATE")*rs4.getDouble("MARGINVALUE")/100);
        }
        else
        {
          chargeInfo.setDiscount(rs4.getDouble("MARGINVALUE"));
          chargeInfo.setDiscountType(rs4.getString("MARGIN_TYPE"));
          
          if("A".equalsIgnoreCase(rs4.getString("MARGIN_TYPE")))
            sellRate  = rs4.getDouble("SELLRATE")-rs4.getDouble("MARGINVALUE");
          else if("P".equalsIgnoreCase(rs4.getString("MARGIN_TYPE")))
            sellRate  = rs4.getDouble("SELLRATE")-(rs4.getDouble("SELLRATE")*rs4.getDouble("MARGINVALUE")/100);
        }*/
          if("A".equalsIgnoreCase(rs4.getString("MARGIN_TYPE")))
       {
       //  if(rs6.next()&&flag.equalsIgnoreCase(rs6.getString("SELL_BUY_FLAG"))&&rs4.getString("CHARGESLAB").equalsIgnoreCase(rs6.getString("BREAK_POINT")))
         if(flag.equalsIgnoreCase(sellBuyFlag))
          {
            if("modifiedQuote".equalsIgnoreCase(quoteType))
              {
                  sellRate  = rs4.getDouble("BUYRATE")+rs4.getDouble("MARGINVALUE"); 
                  chargeInfo.setMargin(rs4.getDouble("MARGINVALUE"));
            }
            else
            {
                if(rs6.next())
                {
                 sellRate  = rs6.getDouble("BUY_RATE")+rs4.getDouble("MARGINVALUE");
                 marginValue = sellRate-rs4.getDouble("BUYRATE"); 
                  chargeInfo.setMargin(marginValue);
                }
            }
         }
           else
           {
               sellRate  = rs4.getDouble("BUYRATE")+rs4.getDouble("MARGINVALUE"); 
               chargeInfo.setMargin(rs4.getDouble("MARGINVALUE"));
           }
        }
        else if("P".equalsIgnoreCase(rs4.getString("MARGIN_TYPE")))
        {
          // if(rs6.next()&&flag.equalsIgnoreCase(rs6.getString("SELL_BUY_FLAG"))&&rs4.getString("CHARGESLAB").equalsIgnoreCase(rs6.getString("BREAK_POINT")))
          if(flag.equalsIgnoreCase(sellBuyFlag))
          {
            if("modifiedQuote".equalsIgnoreCase(quoteType))
            {
               sellRate  = rs4.getDouble("BUYRATE")+(rs4.getDouble("BUYRATE")*rs4.getDouble("MARGINVALUE")/100);
             chargeInfo.setMargin(rs4.getDouble("MARGINVALUE"));
            }
            else
            {
              if(rs6.next())
              {
               sellRate  = rs6.getDouble("BUY_RATE")-(rs6.getDouble("BUY_RATE")*rs4.getDouble("MARGINVALUE")/100);
               marginValue = (rs4.getDouble("BUYRATE")-sellRate)*100/rs4.getDouble("BUYRATE");
                  chargeInfo.setMargin(marginValue);
              }
            } 
           }
        else
         {
             sellRate  = rs4.getDouble("BUYRATE")+(rs4.getDouble("BUYRATE")*rs4.getDouble("MARGINVALUE")/100);
             chargeInfo.setMargin(rs4.getDouble("MARGINVALUE"));
          }
        }
     }
        else
        {
          chargeInfo.setDiscount(rs4.getDouble("MARGINVALUE"));
          chargeInfo.setDiscountType(rs4.getString("MARGIN_TYPE"));
          
         if("A".equalsIgnoreCase(rs4.getString("MARGIN_TYPE")))
        {
         
           //@@Modified by kameswari for the WPBN issue-61235
         // if(rs6.next()&&flag.equalsIgnoreCase(rs6.getString("SELL_BUY_FLAG"))&&rs4.getString("CHARGESLAB").equalsIgnoreCase(rs6.getString("BREAK_POINT")))
          if(flag.equalsIgnoreCase(sellBuyFlag))
          {
           if("modifiedQuote".equalsIgnoreCase(quoteType))
           {
               sellRate  = rs4.getDouble("SELLRATE")-rs4.getDouble("MARGINVALUE");
               chargeInfo.setDiscount(rs4.getDouble("MARGINVALUE"));
           }
            else
           {
             if(rs6.next())
              {
              sellRate  = rs6.getDouble("R_SELL_RATE")-rs4.getDouble("MARGINVALUE");
              marginValue  =rs4.getDouble("SELLRATE") -sellRate;
              chargeInfo.setDiscount(marginValue);
              }
           }
        }
        else
        {
            sellRate  = rs4.getDouble("SELLRATE")-rs4.getDouble("MARGINVALUE");
            chargeInfo.setDiscount(rs4.getDouble("MARGINVALUE"));
         }
            //@@WPBN issue-61235
        }
        else if("P".equalsIgnoreCase(rs4.getString("MARGIN_TYPE")))
        {
         
          //@@Modified by kameswari for the WPBN issue-61235
        // if(rs6.next()&&flag.equalsIgnoreCase(rs6.getString("SELL_BUY_FLAG"))&&rs4.getString("CHARGESLAB").equalsIgnoreCase(rs6.getString("BREAK_POINT")))
          if(flag.equalsIgnoreCase(sellBuyFlag))
          {
            if("modifiedQuote".equalsIgnoreCase(quoteType))
            {
              sellRate  = rs4.getDouble("SELLRATE")-(rs4.getDouble("SELLRATE")*rs4.getDouble("MARGINVALUE")/100);
              chargeInfo.setDiscount(rs4.getDouble("MARGINVALUE"));
            }
            else
            {
                if(rs6.next())
                {
               sellRate  = rs6.getDouble("R_SELL_RATE")-(rs6.getDouble("R_SELL_RATE")*rs4.getDouble("MARGINVALUE")/100);
               marginValue = (rs4.getDouble("SELLRATE")-sellRate)*100/rs4.getDouble("SELLRATE");
              chargeInfo.setDiscount(marginValue);
                }
            } 
        }
        else
        {
            sellRate  = rs4.getDouble("SELLRATE")-(rs4.getDouble("SELLRATE")*rs4.getDouble("MARGINVALUE")/100);
            chargeInfo.setDiscount(rs4.getDouble("MARGINVALUE"));
          }
           //@@WPBN issue-61235
        }
       }
          
        chargeInfo.setSellRate(sellRate);
        chargeInfo.setLineNumber(rs4.getInt("LINE_NO"));
       
        /*if("Delivery".equalsIgnoreCase(rs4.getString("COST_INCURREDAT")))
        {
          if(destChargesList==null)
            destChargesList = new ArrayList();
          destChargesList.add(deliveryChargesDOB);
        }*/
     }
    }
   }
    /*else
    {
      tmpFinalDOB = new QuoteFinalDOB();
      tmpFinalDOB.setMasterDOB(masterDOB);
      tmpFinalDOB.setDestChargesList(destChargesList);
      
      if((masterDOB.getShipperZones()!=null && masterDOB.getShipperZones().trim().length()!=0)
        ||(masterDOB.getConsigneeZones()!=null && masterDOB.getConsigneeZones().trim().length()!=0))
          tmpFinalDOB = getCartages(tmpFinalDOB);        
      
      destChargesList = tmpFinalDOB.getDestChargesList();
      
      
      if(tmpFinalDOB.getDeliveryCartageRatesList()!=null)
        finalDOB.setDeliveryCartageRatesList(tmpFinalDOB.getDeliveryCartageRatesList());           
      
      finalDOB.setDeliveryZoneZipMap(tmpFinalDOB.getDeliveryZoneZipMap());
    }*/
   }
  }
  String tempShipperCode[]    =   new String[masterDOB.getShipperZipCode()!= null?masterDOB.getShipperZipCode().length:0];
  String tempShipperZones[]   =   new String[masterDOB.getShipperZones()!= null?masterDOB.getShipperZones().length:0];
  String tempConsigneeCode[]  =   new String[masterDOB.getConsigneeZipCode()!= null?masterDOB.getConsigneeZipCode().length:0];
  String tempConsigneeZones[] =   new String[masterDOB.getConsigneeZones()!= null?masterDOB.getConsigneeZones().length:0];
  boolean isShipperFetched  =   false;
  boolean isConsigneeFetched=   false;
  isShipperZipCode        =    new boolean[masterDOB.getShipperZipCode()!= null?masterDOB.getShipperZipCode().length:0];
  isSingleShipperZone     =  new boolean[masterDOB.getShipperZones()!= null?masterDOB.getShipperZones().length:0];
  isConsigneeZipCode      = new boolean[masterDOB.getConsigneeZipCode()!= null?masterDOB.getConsigneeZipCode().length:0];
  isSingleConsigneeZone   = new boolean[masterDOB.getConsigneeZones()!= null?masterDOB.getConsigneeZones().length:0];
  for(int i=0;i<masterDOB.getShipperZipCode().length;i++)
  if(masterDOB.getShipperZipCode()[i]!=null && masterDOB.getShipperZipCode()[i].trim().length()!=0)
      isShipperZipCode[i]        =  true;
  for(int i=0;i<masterDOB.getShipperZones().length;i++)
  if(masterDOB.getShipperZones()[i]!=null && masterDOB.getShipperZones()[i].indexOf(",")==-1)
      isSingleShipperZone[i] = true;
  for(int i=0;i<masterDOB.getConsigneeZipCode().length;i++)
  if(masterDOB.getConsigneeZipCode()[i]!=null && masterDOB.getConsigneeZipCode()[i].trim().length()!=0)
      isConsigneeZipCode[i]      =  true;
  for(int i=0;i<masterDOB.getConsigneeZones().length;i++)
  if(masterDOB.getConsigneeZones()[i]!=null && masterDOB.getConsigneeZones()[i].indexOf(",")==-1)
      isSingleConsigneeZone[i]   =  true;
  
  /*if((isSingleShipperZone && ! isShipperZipCode)||(isSingleConsigneeZone && !isConsigneeZipCode))
  {
    finalDOB    =  getZipZoneMapping(finalDOB);//@@So fetch the Zip Zone Mapping
  }
  else*/
  //{
   /* if((masterDOB.getShipperZones()!=null && masterDOB.getShipperZones().trim().length()!=0)
        ||(masterDOB.getConsigneeZones()!=null && masterDOB.getConsigneeZones().trim().length()!=0))
    {
      
      if(isShipperZipCode)
      {
         tempShipperCode  = masterDOB.getShipperZipCode();
         tempShipperZones = masterDOB.getShipperZones();
         masterDOB.setShipperZipCode(null);
         masterDOB.setShipperZones(null);
         isShipperFetched = true;
      }
      if(isConsigneeZipCode)
      {
         tempConsigneeCode  = masterDOB.getConsigneeZipCode();
         tempConsigneeZones = masterDOB.getConsigneeZones();
         masterDOB.setConsigneeZipCode(null);
         masterDOB.setConsigneeZones(null);
         isConsigneeFetched = true;
      }
      
      if(!(isShipperFetched && isConsigneeFetched))
      {
          tmpFinalDOB = new QuoteFinalDOB();
          tmpFinalDOB.setMasterDOB(masterDOB);
          tmpFinalDOB.setOriginChargesList(originChargesList);
          tmpFinalDOB.setDestChargesList(destChargesList);
          tmpFinalDOB = getCartages(tmpFinalDOB); 
          originChargesList = tmpFinalDOB.getOriginChargesList();
          destChargesList = tmpFinalDOB.getDestChargesList();
          if(tmpFinalDOB.getPickUpCartageRatesList()!=null)
            finalDOB.setPickUpCartageRatesList(tmpFinalDOB.getPickUpCartageRatesList());
          if(tmpFinalDOB.getDeliveryCartageRatesList()!=null)
              finalDOB.setDeliveryCartageRatesList(tmpFinalDOB.getDeliveryCartageRatesList());
      
          finalDOB.setPickZoneZipMap(tmpFinalDOB.getPickZoneZipMap());
          finalDOB.setDeliveryZoneZipMap(tmpFinalDOB.getDeliveryZoneZipMap());
      }
      
      if(tempShipperCode!=null)
      {
          masterDOB.setShipperZipCode(tempShipperCode);
          masterDOB.setShipperZones(tempShipperZones);
      }
      if(tempConsigneeCode!=null)
      {
        masterDOB.setConsigneeZipCode(tempConsigneeCode);
        masterDOB.setConsigneeZones(tempConsigneeZones);
      }
      
      finalDOB.setMasterDOB(masterDOB);
      
      if(!isShipperFetched || !isConsigneeFetched)
      {
        finalDOB    =  getZipZoneMapping(finalDOB);
      }
      
      //originChargesList = tmpFinalDOB.getOriginChargesList();
      //destChargesList = tmpFinalDOB.getDestChargesList();
      
     /* if(tmpFinalDOB.getPickUpCartageRatesList()!=null)
        finalDOB.setPickUpCartageRatesList(tmpFinalDOB.getPickUpCartageRatesList());
      if(tmpFinalDOB.getDeliveryCartageRatesList()!=null)
        finalDOB.setDeliveryCartageRatesList(tmpFinalDOB.getDeliveryCartageRatesList());
      
      finalDOB.setPickZoneZipMap(tmpFinalDOB.getPickZoneZipMap());
      finalDOB.setDeliveryZoneZipMap(tmpFinalDOB.getDeliveryZoneZipMap());*/
    /*}*/
  //}
  
  chargesDOB  = null;
   rs6 =  pStmt.executeQuery();
  //rs3 ResultSet is used for getting Charges  
  while(rs3.next())
  {           
   flag = rs3.getString("SEL_BUY_FLAG");
     if(chargesDOB!=null && 
       (
         (chargesDOB.getBuyChargeId()!=null && chargesDOB.getBuyChargeId().equals(rs3.getString("BUY_CHARGE_ID")))
         ||
         (chargesDOB.getSellChargeId()!=null && chargesDOB.getSellChargeId().equals(rs3.getString("SELLCHARGEID")))
       )
      )
    {
      chargeInfo  = new MultiQuoteChargeInfo();
      
      chargeInfoList.add(chargeInfo);
      
      chargeInfo.setBreakPoint(rs3.getString("CHARGESLAB"));
      if(rs3.getString("CURRENCY")!=null && rs3.getString("CURRENCY").trim().length()!=0)
           chargeInfo.setCurrency(rs3.getString("CURRENCY"));
      else
           chargeInfo.setCurrency(masterDOB.getTerminalCurrency());
           
      chargeInfo.setBuyRate(rs3.getDouble("BUYRATE"));
      chargeInfo.setRecOrConSellRrate(rs3.getDouble("SELLRATE"));
      chargeInfo.setSellChargeMargin(rs3.getDouble("MARGINVALUE"));
      chargeInfo.setSellChargeMarginType(rs3.getString("MARGIN_TYPE"));
      chargeInfo.setRateIndicator(rs3.getString("RATE_INDICATOR"));
      if("M".equalsIgnoreCase(chargesDOB.getMarginDiscountFlag()) || chargesDOB.getMarginDiscountFlag()==null)
      {  
        chargeInfo.setMargin(rs3.getDouble("MARGINVALUE"));
        chargeInfo.setMarginType(rs3.getString("MARGIN_TYPE"));
        
       /* if("A".equalsIgnoreCase(rs3.getString("MARGIN_TYPE")))
          sellRate  = rs3.getDouble("BUYRATE")+rs3.getDouble("MARGINVALUE");
        else if("P".equalsIgnoreCase(rs3.getString("MARGIN_TYPE")))
          sellRate  = rs3.getDouble("BUYRATE")+(rs3.getDouble("BUYRATE")*rs3.getDouble("MARGINVALUE")/100);
      }
      else
      {
        chargeInfo.setDiscount(rs3.getDouble("MARGINVALUE"));
        chargeInfo.setDiscountType(rs3.getString("MARGIN_TYPE"));
        
        if("A".equalsIgnoreCase(rs3.getString("MARGIN_TYPE")))
          sellRate  = rs3.getDouble("SELLRATE")-rs3.getDouble("MARGINVALUE");
        else if("P".equalsIgnoreCase(rs3.getString("MARGIN_TYPE")))
          sellRate  = rs3.getDouble("SELLRATE")-(rs3.getDouble("SELLRATE")*rs3.getDouble("MARGINVALUE")/100);
      }*/
       if("A".equalsIgnoreCase(rs3.getString("MARGIN_TYPE")))
       {
         //if(rs6.next()&&flag.equalsIgnoreCase(rs6.getString("SELL_BUY_FLAG"))&&rs3.getString("CHARGESLAB").equalsIgnoreCase(rs6.getString("BREAK_POINT"))&&rs6.getString("CHARGE_DESCRIPTION").equalsIgnoreCase(chargesDOB.getChargeDescriptionId()))
       if(flag.equalsIgnoreCase(sellBuyFlag)&&changeDesc.equalsIgnoreCase(chargesDOB.getChargeDescriptionId()))
          {
            //if("modifiedQuote".equalsIgnoreCase(quoteType))//@@Commented and Modified by Kameswari for the WPBN issue-154398 on 21/02/09
              if("modifiedQuote".equalsIgnoreCase(quoteType)||"Y".equalsIgnoreCase(rs3.getString("CHANGE_FLAG")))
              {
                  sellRate  = rs3.getDouble("BUYRATE")+rs3.getDouble("MARGINVALUE"); 
                  chargeInfo.setMargin(rs3.getDouble("MARGINVALUE"));
            }
            else
            {
                if(rs6.next())
               {
                 sellRate  = rs6.getDouble("BUY_RATE")+rs3.getDouble("MARGINVALUE");
                 marginValue = sellRate-rs3.getDouble("BUYRATE"); 
                 chargeInfo.setMargin(marginValue);
               }
              
            }
         }
           else
           {
               sellRate  = rs3.getDouble("BUYRATE")+rs3.getDouble("MARGINVALUE"); 
               chargeInfo.setMargin(rs3.getDouble("MARGINVALUE"));
           }
        }
        else if("P".equalsIgnoreCase(rs3.getString("MARGIN_TYPE")))
        {
        //   if(rs6.next()&&flag.equalsIgnoreCase(rs6.getString("SELL_BUY_FLAG"))&&rs3.getString("CHARGESLAB").equalsIgnoreCase(rs6.getString("BREAK_POINT")))
          if(flag.equalsIgnoreCase(sellBuyFlag)&&changeDesc.equalsIgnoreCase(chargesDOB.getChargeDescriptionId()))
          {
            
             //if("modifiedQuote".equalsIgnoreCase(quoteType))//@@Commented and Modified by Kameswari for the WPBN issue-154398 on 21/02/09
              if("modifiedQuote".equalsIgnoreCase(quoteType)||"Y".equalsIgnoreCase(rs3.getString("CHANGE_FLAG")))
            {
               sellRate  = rs3.getDouble("BUYRATE")+(rs3.getDouble("BUYRATE")*rs3.getDouble("MARGINVALUE")/100);
             chargeInfo.setMargin(rs3.getDouble("MARGINVALUE"));
            }
            else
            {
                   if(rs6.next())
                   {
               sellRate  = rs6.getDouble("BUY_RATE")-(rs6.getDouble("BUY_RATE")*rs3.getDouble("MARGINVALUE")/100);
               marginValue = (rs3.getDouble("BUYRATE")-sellRate)*100/rs3.getDouble("BUYRATE");
               chargeInfo.setMargin(marginValue);
                   }
            } 
           }
        else
         {
              sellRate  = rs3.getDouble("BUYRATE")+(rs3.getDouble("BUYRATE")*rs3.getDouble("MARGINVALUE")/100);
             chargeInfo.setMargin(rs3.getDouble("MARGINVALUE"));
        
         }
        }
     }
        else
        {
          chargeInfo.setDiscount(rs3.getDouble("MARGINVALUE"));
          chargeInfo.setDiscountType(rs3.getString("MARGIN_TYPE"));
          
         if("A".equalsIgnoreCase(rs3.getString("MARGIN_TYPE")))
        {
         
           //@@Modified by kameswari for the WPBN issue-61235
        //  if(rs6.next()&&flag.equalsIgnoreCase(rs6.getString("SELL_BUY_FLAG"))&&rs3.getString("CHARGESLAB").equalsIgnoreCase(rs6.getString("BREAK_POINT")))
         if(flag.equalsIgnoreCase(sellBuyFlag)&&changeDesc.equalsIgnoreCase(chargesDOB.getChargeDescriptionId()))
          {
           //if("modifiedQuote".equalsIgnoreCase(quoteType))//@@Commented and Modified by Kameswari for the WPBN issue-154398 on 21/02/09
              if("modifiedQuote".equalsIgnoreCase(quoteType)||"Y".equalsIgnoreCase(rs3.getString("CHANGE_FLAG")))
           {
               sellRate  = rs3.getDouble("SELLRATE")-rs3.getDouble("MARGINVALUE");
               chargeInfo.setDiscount(rs3.getDouble("MARGINVALUE"));
           }
            else
           {
              if(rs6.next())
              {
              sellRate  = rs6.getDouble("R_SELL_RATE")-rs3.getDouble("MARGINVALUE");
              marginValue  =rs3.getDouble("SELLRATE") -sellRate;
              chargeInfo.setDiscount(marginValue);
              }
           }
        }
        else
        {
            sellRate  = rs3.getDouble("SELLRATE")-rs3.getDouble("MARGINVALUE");
            chargeInfo.setDiscount(rs3.getDouble("MARGINVALUE"));
         }
            //@@WPBN issue-61235
        }
        else if("P".equalsIgnoreCase(rs3.getString("MARGIN_TYPE")))
        {
         
          //@@Modified by kameswari for the WPBN issue-61235
        // if(rs6.next()&&flag.equalsIgnoreCase(rs6.getString("SELL_BUY_FLAG"))&&rs3.getString("CHARGESLAB").equalsIgnoreCase(rs6.getString("BREAK_POINT")))
          if(flag.equalsIgnoreCase(sellBuyFlag)&&changeDesc.equalsIgnoreCase(chargesDOB.getChargeDescriptionId()))
          {
           //if("modifiedQuote".equalsIgnoreCase(quoteType))//@@Commented and Modified by Kameswari for the WPBN issue-154398 on 21/02/09
              if("modifiedQuote".equalsIgnoreCase(quoteType)||"Y".equalsIgnoreCase(rs3.getString("CHANGE_FLAG")))
            {
              
              sellRate  = rs3.getDouble("SELLRATE")-(rs3.getDouble("SELLRATE")*rs3.getDouble("MARGINVALUE")/100);
              chargeInfo.setDiscount(rs3.getDouble("MARGINVALUE"));
            }
            else
            {
         if(rs6.next())
         {
               sellRate  = rs6.getDouble("R_SELL_RATE")-(rs6.getDouble("R_SELL_RATE")*rs3.getDouble("MARGINVALUE")/100);
               marginValue = (rs3.getDouble("SELLRATE")-sellRate)*100/rs3.getDouble("SELLRATE");
              chargeInfo.setDiscount(marginValue);
         }
            } 
        }
        else
        {
            sellRate  = rs3.getDouble("SELLRATE")-(rs3.getDouble("SELLRATE")*rs3.getDouble("MARGINVALUE")/100);
            chargeInfo.setDiscount(rs3.getDouble("MARGINVALUE"));
          }
           //@@WPBN issue-61235
        }
       }
          
      //chargeInfo.setBasis(rs3.getString("CHARGEBASIS"));
      weightBreak   =   rs3.getString("WEIGHT_BREAK");
      rateType      =   rs3.getString("RATE_TYPE");
      if("BASE".equalsIgnoreCase(chargeInfo.getBreakPoint()) || "MIN".equalsIgnoreCase(chargeInfo.getBreakPoint()) || "MAX".equalsIgnoreCase(chargeInfo.getBreakPoint()) || ("SLAB".equalsIgnoreCase(weightBreak) && "FLAT".equalsIgnoreCase(rateType))  || ("BOTH".equalsIgnoreCase(rateType) && "FLAT".equalsIgnoreCase(chargeInfo.getRateIndicator()))) //MODIFIED FOR 183812
      {
        chargeInfo.setBasis("Per Shipment");
      }
      else
      {
        /*if("1".equalsIgnoreCase(rs3.getString("SHMODE")) && "LIST".equalsIgnoreCase(rs3.getString("WEIGHT_BREAK")))
        {
          chargeInfo.setBasis("Per ULD");
        }
        else if(("2".equalsIgnoreCase(rs3.getString("SHMODE"))||"4".equalsIgnoreCase(rs3.getString("SHMODE"))) && "LIST".equalsIgnoreCase(rs3.getString("WEIGHT_BREAK")))
        {
          chargeInfo.setBasis("Per Container");
        }
        else */
       if("Per Kg".equalsIgnoreCase(rs3.getString("CHARGEBASIS")))
        {
          chargeInfo.setBasis("Per Kilogram");
        }
        else if("Per Lb".equalsIgnoreCase(rs3.getString("CHARGEBASIS")))
        {
          chargeInfo.setBasis("Per Pound");
        }
        else if("Per CBM".equalsIgnoreCase(rs3.getString("CHARGEBASIS")))
        {
          chargeInfo.setBasis("Per Cubic Meter");
        }
        else if("Per CFT".equalsIgnoreCase(rs3.getString("CHARGEBASIS")))
        {
          chargeInfo.setBasis("Per Cubic Feet");
        }
        else
          chargeInfo.setBasis(rs3.getString("CHARGEBASIS"));
          
          //weightBreak   = rs3.getString("WEIGHT_BREAK");
      
          if(weightBreak!=null && ("Percent".equalsIgnoreCase(weightBreak) || weightBreak.endsWith("%")))
            chargeInfo.setPercentValue(true);
      }
      chargeInfo.setRatio(rs3.getString("DENSITY_RATIO"));
          
      chargeInfo.setSellRate(sellRate);
      chargeInfo.setLineNumber(rs3.getInt("LINE_NO"));
     
    }
    else
    {        
      chargesDOB  = new MultiQuoteChargeInfo();
     
       
         if("Origin".equalsIgnoreCase(rs3.getString("COST_INCURREDAT")))
      {
        if(originChargesList==null)
          originChargesList = new ArrayList();
        originChargesList.add(chargesDOB);
      }
      else if("Destination".equalsIgnoreCase(rs3.getString("COST_INCURREDAT")))
      {
        if(destChargesList==null)
          destChargesList = new ArrayList();
        destChargesList.add(chargesDOB);
      }
      
      chargesDOB.setBuyChargeId(rs3.getString("BUY_CHARGE_ID"));
      chargesDOB.setSellChargeId(rs3.getString("SELLCHARGEID"));                
      chargesDOB.setSellBuyFlag(rs3.getString("SEL_BUY_FLAG"));
      
      chargesDOB.setChargeId(rs3.getString("CHARGE_ID"));
      chargesDOB.setTerminalId(rs3.getString("TERMINALID"));
      chargesDOB.setMarginDiscountFlag(rs3.getString("MARGIN_DISCOUNT_FLAG"));
      chargesDOB.setChargeDescriptionId(rs3.getString("CHARGEDESCID"));
      chargesDOB.setInternalName(rs3.getString("INT_CHARGE_NAME"));
      chargesDOB.setExternalName(rs3.getString("EXT_CHARGE_NAME"));
      chargesDOB.setCostIncurredAt(rs3.getString("COST_INCURREDAT"));
      chargesDOB.setSelectedFlag(rs3.getString("SELECTED_FLAG"));
      
      if(changeDesc.equalsIgnoreCase(chargesDOB.getChargeDescriptionId()))
            finalDOB.setEmailChargeName(chargesDOB.getExternalName());
              
      chargeInfoList  = new ArrayList();
      chargeInfo      = new MultiQuoteChargeInfo();
      chargeInfoList.add(chargeInfo);
      
      chargesDOB.setChargeInfoList(chargeInfoList);
      
      chargeInfo.setBreakPoint(rs3.getString("CHARGESLAB"));
      if(rs3.getString("CURRENCY")!=null && rs3.getString("CURRENCY").trim().length()!=0)
          chargeInfo.setCurrency(rs3.getString("CURRENCY"));
      else
           chargeInfo.setCurrency(masterDOB.getTerminalCurrency());
      chargeInfo.setBuyRate(rs3.getDouble("BUYRATE"));
      chargeInfo.setRecOrConSellRrate(rs3.getDouble("SELLRATE"));
      chargeInfo.setSellChargeMargin(rs3.getDouble("MARGINVALUE"));
      chargeInfo.setSellChargeMarginType(rs3.getString("MARGIN_TYPE"));
      chargeInfo.setRateIndicator(rs3.getString("RATE_INDICATOR"));
      weightBreak   =   rs3.getString("WEIGHT_BREAK");
      rateType      =   rs3.getString("RATE_TYPE");
      //chargeInfo.setBasis(rs3.getString("CHARGEBASIS"));
      if("BASE".equalsIgnoreCase(chargeInfo.getBreakPoint()) || "MIN".equalsIgnoreCase(chargeInfo.getBreakPoint()) || "MAX".equalsIgnoreCase(chargeInfo.getBreakPoint()) || ("SLAB".equalsIgnoreCase(weightBreak) && "FLAT".equalsIgnoreCase(rateType))  || ("BOTH".equalsIgnoreCase(rateType) && "FLAT".equalsIgnoreCase(chargeInfo.getRateIndicator()))) //MODIFIED FOR 183812
      {
        chargeInfo.setBasis("Per Shipment");
      }
      else
      {
        
        if(weightBreak!=null && ("Percent".equalsIgnoreCase(weightBreak) || weightBreak.endsWith("%")))
          chargeInfo.setPercentValue(true);
        
        if("Per Kg".equalsIgnoreCase(rs3.getString("CHARGEBASIS")))
        {
          chargeInfo.setBasis("Per Kilogram");
        }
        else if("Per Lb".equalsIgnoreCase(rs3.getString("CHARGEBASIS")))
        {
          chargeInfo.setBasis("Per Pound");
        }
        else if("Per CBM".equalsIgnoreCase(rs3.getString("CHARGEBASIS")))
        {
          chargeInfo.setBasis("Per Cubic Meter");
        }
        else if("Per CFT".equalsIgnoreCase(rs3.getString("CHARGEBASIS")))
        {
          chargeInfo.setBasis("Per Cubic Feet");
        }
        else
          chargeInfo.setBasis(rs3.getString("CHARGEBASIS"));
      }
      chargeInfo.setRatio(rs3.getString("DENSITY_RATIO"));
      
     if("M".equalsIgnoreCase(chargesDOB.getMarginDiscountFlag()) || chargesDOB.getMarginDiscountFlag()==null)
      {  
        chargeInfo.setMargin(rs3.getDouble("MARGINVALUE"));
        chargeInfo.setMarginType(rs3.getString("MARGIN_TYPE"));
        
      /*  if("A".equalsIgnoreCase(rs3.getString("MARGIN_TYPE")))
          sellRate  = rs3.getDouble("BUYRATE")+rs3.getDouble("MARGINVALUE");
        else if("P".equalsIgnoreCase(rs3.getString("MARGIN_TYPE")))
          sellRate  = rs3.getDouble("BUYRATE")+(rs3.getDouble("BUYRATE")*rs3.getDouble("MARGINVALUE")/100);
      }
      else
      {
        chargeInfo.setDiscount(rs3.getDouble("MARGINVALUE"));
        chargeInfo.setDiscountType(rs3.getString("MARGIN_TYPE"));
        
        if("A".equalsIgnoreCase(rs3.getString("MARGIN_TYPE")))
          sellRate  = rs3.getDouble("SELLRATE")-rs3.getDouble("MARGINVALUE");
        else if("P".equalsIgnoreCase(rs3.getString("MARGIN_TYPE")))
          sellRate  = rs3.getDouble("SELLRATE")-(rs3.getDouble("SELLRATE")*rs3.getDouble("MARGINVALUE")/100);
      }
          */
            if("A".equalsIgnoreCase(rs3.getString("MARGIN_TYPE")))
       {
      
       //  if(rs6.next()&&flag.equalsIgnoreCase(rs6.getString("SELL_BUY_FLAG"))&&rs3.getString("CHARGESLAB").equalsIgnoreCase(rs6.getString("BREAK_POINT")))
             if(flag.equalsIgnoreCase(sellBuyFlag)&&changeDesc.equalsIgnoreCase(chargesDOB.getChargeDescriptionId()))
          {
          //if("modifiedQuote".equalsIgnoreCase(quoteType))//@@Commented and Modified by Kameswari for the WPBN issue-154398 on 21/02/09
              if("modifiedQuote".equalsIgnoreCase(quoteType)||"Y".equalsIgnoreCase(rs3.getString("CHANGE_FLAG")))
              {
                  sellRate  = rs3.getDouble("BUYRATE")+rs3.getDouble("MARGINVALUE"); 
                  chargeInfo.setMargin(rs3.getDouble("MARGINVALUE"));
            }
            else
            {
                 if(rs6.next())
                 {
                 sellRate  = rs6.getDouble("BUY_RATE")+rs3.getDouble("MARGINVALUE");
                 marginValue = sellRate-rs3.getDouble("BUYRATE"); 
                 chargeInfo.setMargin(marginValue);
                 }
            }
         }
           else
           {
              
                 sellRate  = rs3.getDouble("BUYRATE")+rs3.getDouble("MARGINVALUE"); 
               chargeInfo.setMargin(rs3.getDouble("MARGINVALUE"));
           }
        }
        else if("P".equalsIgnoreCase(rs3.getString("MARGIN_TYPE")))
        {
         
         //  if(rs6.next()&&flag.equalsIgnoreCase(rs6.getString("SELL_BUY_FLAG"))&&rs3.getString("CHARGESLAB").equalsIgnoreCase(rs6.getString("BREAK_POINT")))
              if(flag.equalsIgnoreCase(sellBuyFlag)&&changeDesc.equalsIgnoreCase(chargesDOB.getChargeDescriptionId()))
          {
            //if("modifiedQuote".equalsIgnoreCase(quoteType))//@@Commented and Modified by Kameswari for the WPBN issue-154398 on 21/02/09
              if("modifiedQuote".equalsIgnoreCase(quoteType)||"Y".equalsIgnoreCase(rs3.getString("CHANGE_FLAG")))
            {
           
               sellRate  = rs3.getDouble("BUYRATE")+(rs3.getDouble("BUYRATE")*rs3.getDouble("MARGINVALUE")/100);
             chargeInfo.setMargin(rs3.getDouble("MARGINVALUE"));
            }
            else
            {
                
                   if(rs6.next())
                   {
               sellRate  = rs6.getDouble("BUY_RATE")-(rs6.getDouble("BUY_RATE")*rs3.getDouble("MARGINVALUE")/100);
               marginValue = (rs3.getDouble("BUYRATE")-sellRate)*100/rs3.getDouble("BUYRATE");
               chargeInfo.setMargin(marginValue);
              
                   }
                       } 
           }
        else
         {
            
                     sellRate  = rs3.getDouble("BUYRATE")+(rs3.getDouble("BUYRATE")*rs3.getDouble("MARGINVALUE")/100);
             chargeInfo.setMargin(rs3.getDouble("MARGINVALUE"));
         }
        }
     }
        else
        {
          chargeInfo.setDiscount(rs3.getDouble("MARGINVALUE"));
          chargeInfo.setDiscountType(rs3.getString("MARGIN_TYPE"));
         if("A".equalsIgnoreCase(rs3.getString("MARGIN_TYPE")))
        {
         
           //@@Modified by kameswari for the WPBN issue-61235
        //  if(rs6.next()&&flag.equalsIgnoreCase(rs6.getString("SELL_BUY_FLAG"))&&rs3.getString("CHARGESLAB").equalsIgnoreCase(rs6.getString("BREAK_POINT")))
              if(flag.equalsIgnoreCase(sellBuyFlag)&&changeDesc.equalsIgnoreCase(chargesDOB.getChargeDescriptionId()))
          {
           //if("modifiedQuote".equalsIgnoreCase(quoteType))//@@Commented and Modified by Kameswari for the WPBN issue-154398 on 21/02/09
              if("modifiedQuote".equalsIgnoreCase(quoteType)||"Y".equalsIgnoreCase(rs3.getString("CHANGE_FLAG")))
           {
               sellRate  = rs3.getDouble("SELLRATE")-rs3.getDouble("MARGINVALUE");
             
               chargeInfo.setDiscount(rs3.getDouble("MARGINVALUE"));
           }
            else
           {
              if(rs6.next())
              {
              sellRate  = rs6.getDouble("R_SELL_RATE")-rs3.getDouble("MARGINVALUE");
              marginValue  =rs3.getDouble("SELLRATE") -sellRate;
              chargeInfo.setDiscount(marginValue);
              }
           }
        }
        else
        {
            sellRate  = rs3.getDouble("SELLRATE")-rs3.getDouble("MARGINVALUE");
            chargeInfo.setDiscount(rs3.getDouble("MARGINVALUE"));
         }
            //@@WPBN issue-61235
        }
        else if("P".equalsIgnoreCase(rs3.getString("MARGIN_TYPE")))
        {
       
          //@@Modified by kameswari for the WPBN issue-61235
        // if(rs6.next()&&flag.equalsIgnoreCase(rs6.getString("SELL_BUY_FLAG"))&&rs3.getString("CHARGESLAB").equalsIgnoreCase(rs6.getString("BREAK_POINT")))
              if(flag.equalsIgnoreCase(sellBuyFlag)&&changeDesc.equalsIgnoreCase(chargesDOB.getChargeDescriptionId()))
          {
             //if("modifiedQuote".equalsIgnoreCase(quoteType))//@@Commented and Modified by Kameswari for the WPBN issue-154398 on 21/02/09
              if("modifiedQuote".equalsIgnoreCase(quoteType)||"Y".equalsIgnoreCase(rs3.getString("CHANGE_FLAG")))
            {
              sellRate  = rs3.getDouble("SELLRATE")-(rs3.getDouble("SELLRATE")*rs3.getDouble("MARGINVALUE")/100);
              chargeInfo.setDiscount(rs3.getDouble("MARGINVALUE"));
           
            }
            else
            {
               if(rs6.next())
               {
               sellRate  = rs6.getDouble("R_SELL_RATE")-(rs6.getDouble("R_SELL_RATE")*rs3.getDouble("MARGINVALUE")/100);
               marginValue = (rs3.getDouble("SELLRATE")-sellRate)*100/rs3.getDouble("SELLRATE");
              chargeInfo.setDiscount(marginValue);
               }
            } 
        }
        else
        {
             sellRate  = rs3.getDouble("SELLRATE")-(rs3.getDouble("SELLRATE")*rs3.getDouble("MARGINVALUE")/100);
            chargeInfo.setDiscount(rs3.getDouble("MARGINVALUE"));
          }
           //@@WPBN issue-61235
        }
       }
      chargeInfo.setSellRate(sellRate);
        
      chargeInfo.setLineNumber(rs3.getInt("LINE_NO"));
    }            
  }
  
  //@@ For putting the delivery charge at the end of the list, if it exists.
  if(deliveryChargesDOB!=null)
  {
      if(destChargesList==null)
            destChargesList = new ArrayList();
      destChargesList.add(deliveryChargesDOB);
  }
  //@@
  for(int i=0;i<masterDOB.getShipperZones().length;i++)
  {
  if((masterDOB.getShipperZones()[i]!=null && masterDOB.getShipperZones()[i].trim().length()!=0)
        ||(masterDOB.getConsigneeZones()[i]!=null && masterDOB.getConsigneeZones()[i].trim().length()!=0))
    {
      if(isShipperZipCode[i] || isSingleShipperZone[i])
      {
         tempShipperCode[i]  = masterDOB.getShipperZipCode()[i];
         tempShipperZones[i] = masterDOB.getShipperZones()[i];
         masterDOB.setShipperZipCode(null);
         masterDOB.setShipperZones(null);
         isShipperFetched = true;
      }
      if(isConsigneeZipCode[i] || isSingleConsigneeZone[i])
      {
         tempConsigneeCode[i]  = masterDOB.getConsigneeZipCode()[i];
         tempConsigneeZones[i] = masterDOB.getConsigneeZones()[i];
         masterDOB.setConsigneeZipCode(null);
         masterDOB.setConsigneeZones(null);
         isConsigneeFetched = true;
      }

      if(!(isShipperFetched && isConsigneeFetched))
      {
         if(masterDOB!= null && (masterDOB.getShipperZipCode()!= null && masterDOB.getShipperZipCode()[i]!=null) || (masterDOB.getShipperZones()!= null && masterDOB.getShipperZones()[i]!=null) || 
        (masterDOB.getConsigneeZipCode()!= null && masterDOB.getConsigneeZipCode()[i]!=null) || (masterDOB.getConsigneeZones()!= null && masterDOB.getConsigneeZones()[i]!=null))
          { 
            tmpFinalDOB = new MultiQuoteFinalDOB();
            tmpFinalDOB.setMasterDOB(masterDOB);
            tmpFinalDOB.setOriginChargesList(originChargesList);
            tmpFinalDOB.setDestChargesList(destChargesList);
               tmpFinalDOB = getCartages(tmpFinalDOB);
            originChargesList = tmpFinalDOB.getOriginChargesList();
            destChargesList = tmpFinalDOB.getDestChargesList();
            
            if(tmpFinalDOB.getPickUpCartageRatesList()!=null)
              finalDOB.setPickUpCartageRatesList(tmpFinalDOB.getPickUpCartageRatesList());
            if(tmpFinalDOB.getDeliveryCartageRatesList()!=null)
              finalDOB.setDeliveryCartageRatesList(tmpFinalDOB.getDeliveryCartageRatesList());
            if(tmpFinalDOB.getPickupWeightBreaks()!=null)
              finalDOB.setPickupWeightBreaks(tmpFinalDOB.getPickupWeightBreaks());
            if(tmpFinalDOB.getDeliveryWeightBreaks()!=null)
              finalDOB.setDeliveryWeightBreaks(tmpFinalDOB.getDeliveryWeightBreaks());
            
            finalDOB.setPickZoneZipMap(tmpFinalDOB.getPickZoneZipMap());
            finalDOB.setDeliveryZoneZipMap(tmpFinalDOB.getDeliveryZoneZipMap()); 
          }
      }
      if(isShipperFetched)
      {
          masterDOB.setShipperZipCode(tempShipperCode);
          masterDOB.setShipperZones(tempShipperZones);
      }
      if(isConsigneeFetched)
      {
        masterDOB.setConsigneeZipCode(tempConsigneeCode);
        masterDOB.setConsigneeZones(tempConsigneeZones);
      }
      
      finalDOB.setMasterDOB(masterDOB);
      
      if(!isShipperFetched || !isConsigneeFetched || !isShipperZipCode[i] || !isConsigneeZipCode[i])
      {
        finalDOB    =  getZipZoneMapping(finalDOB);
      }
      
      //originChargesList = tmpFinalDOB.getOriginChargesList();
      //destChargesList = tmpFinalDOB.getDestChargesList();
      
     /* if(tmpFinalDOB.getPickUpCartageRatesList()!=null)
        finalDOB.setPickUpCartageRatesList(tmpFinalDOB.getPickUpCartageRatesList());
      if(tmpFinalDOB.getDeliveryCartageRatesList()!=null)
        finalDOB.setDeliveryCartageRatesList(tmpFinalDOB.getDeliveryCartageRatesList());
      
      finalDOB.setPickZoneZipMap(tmpFinalDOB.getPickZoneZipMap());
      finalDOB.setDeliveryZoneZipMap(tmpFinalDOB.getDeliveryZoneZipMap());*/
    }
}
  
  if(originChargesList!=null || destChargesList!=null)
  {
    if(originChargesList!=null)
      finalDOB.setOriginChargesList(originChargesList);
    if(destChargesList!=null)
      finalDOB.setDestChargesList(destChargesList);
  }
  
  list_exNotes = new ArrayList();
  String[]  notes = null;
  while(rs5.next())
  {
    list_exNotes.add((rs5.getString("EXTERNAL_NOTES")!=null)?rs5.getString("EXTERNAL_NOTES"):"");
  }
  if(list_exNotes!=null)
  {
    notes   = new String[list_exNotes.size()];
  }
String[] arr = null;
  String arr1="";
  int j = 0;
  if(list_exNotes != null)
  {
	  int exNotesSize	=	list_exNotes.size();
    for(int i=0;i<exNotesSize;i++)
    {
     if(list_exNotes.get(i)!="")
     {
      notes[j]  = (String)list_exNotes.get(i);
      if((notes[j].trim()).length()>0)
      {
    arr = notes[j] .split("");
        for( int k=0;k<arr.length-1;k++)
        {
         
             if((arr[k].trim()).length()>0)
            {
              arr1 =arr1.concat(arr[k].trim());
            }
         else
         {
           if((arr[k+1].trim()).length()>0)
           {
              arr1 =arr1.concat(" ");
           }
         }
        }
           arr1 =arr1.concat(arr[arr.length-1]);
           notes[j] = arr1;
        arr1 =  "";
     
      j++;
      }
        
     }
    
    }
  }
  finalDOB.setExternalNotes(notes);

}

catch(SQLException sql)
 {
   //Logger.error(FILE_NAME,"SQLException While Fetching Updated Quotes Data"+sql);
   logger.error(FILE_NAME+"SQLException While Fetching Updated Quotes Data"+sql);
   //Logger.error(FILE_NAME,"Error Code: "+sql.getErrorCode());
   logger.error(FILE_NAME+"Error Code: "+sql.getErrorCode());
   //Logger.error(FILE_NAME,"SQL State: "+sql.getSQLState());
   logger.error(FILE_NAME+"SQL State: "+sql.getSQLState());
   
   sql.printStackTrace();
   throw new SQLException(sql.toString());
 }
catch(Exception e)
{
  //Logger.error(FILE_NAME,"Exception While Fetching Updated Quotes Data"+e);
  logger.error(FILE_NAME+"Exception While Fetching Updated Quotes Data"+e);
  e.printStackTrace();
  throw new SQLException(e.toString());
}
finally
{
  try
  {
    if(rs2!=null)
      rs2.close();
    if(rs3!=null)
      rs3.close();
    if(rs4!=null)
      rs4.close();
    if(rs5!=null)
      rs5.close();
    if(rs6!=null)
      rs6.close();//Added By RajKumari on 24-10-2008 for Connection Leakages.
    if(pStmt!=null)
      pStmt.close();
    if(csmt!=null)
      csmt.close();
    if(connection !=null)
      connection.close();
  }
  catch(Exception e)
  {
    e.printStackTrace();
  }
}
return finalDOB;
}
public MultiQuoteFinalDOB getCartages(MultiQuoteFinalDOB finalDOB) throws SQLException
{
  Connection                connection              = null;
  CallableStatement         csmt                    = null;
  ResultSet                 rs                      = null;
  ResultSet                 zoneRs                  = null;
  ResultSet                 pickWeightBreaksRS      = null;
  ResultSet                 delWeightBreaksRS       = null;
  /*ResultSet                 pickWeightBreaksRSList  = null;
  ResultSet                 delWeightBreaksRSList   = null;*/
  ArrayList                 originChargesList       = null;//to maintain the list of  all origin charge dobs
  ArrayList                 destChargesList         = null;//to maintain the list of  all origin charge dobs
  MultiQuoteCharges              cartageChargesDOB       = null;//to maintain one record that is to be displayed
  MultiQuoteCharges              cartageDelChargesDOB    = null;//to maintain one record that is to be displayed
  MultiQuoteChargeInfo           chargeInfo              = null;
  ArrayList                 chargeInfoList          = null;
  String                    flag                    = null;
  String                    pickWeightBreak         = null;
  String                    pickRateType            = null;
  String                    delWeightBreak          = null;
  String                    delRateType             = null;
  MultiQuoteMasterDOB            masterDOB               = null;
  double                    sellRate                = 0;
  ArrayList                 pickUpQuoteCartageRates   = new ArrayList();
  ArrayList                 deliveryQuoteCartageRates = new ArrayList();
  ArrayList                 pickupWeightBreaksList    = new ArrayList();
  ArrayList                 delWeightBreaksList       = new ArrayList();
  HashMap                   pickUpZoneZipMap          = new HashMap();//Added by Sanjay
  HashMap                   deliveryZoneZipMap        = new HashMap();//Added by Sanjay
  HashMap                   pickUpZoneCode            = new HashMap();//Added by Sanjay
  HashMap                   deliveryZoneCode          = new HashMap();//Added by Sanjay
  MultiQuoteCartageRates         pickQuoteCartageRates     = null;//Added by Sanjay
  MultiQuoteCartageRates         delQuoteCartageRates      = null;//Added by Sanjay
  boolean                   addToPickupList           = true;
  boolean                   addToDeliveryList         = true;
  boolean                   isPickupMin               = false;
  boolean                   isPickupFlat              = false;
  boolean                   isPickupMax               = false;
  boolean                   isDeliveryMin             = false;
  boolean                   isDeliveryFlat            = false;
  boolean                   isDeliveryMax             = false;
  
  try
  {
    //@@for re-initialising all the objects put in session previously.
   
    finalDOB.setPickUpCartageRatesList(pickUpQuoteCartageRates);
    finalDOB.setDeliveryCartageRatesList(deliveryQuoteCartageRates); 
    finalDOB.setPickupWeightBreaks(pickupWeightBreaksList);
    finalDOB.setDeliveryWeightBreaks(delWeightBreaksList);
    finalDOB.setPickZoneZipMap(pickUpZoneZipMap);
    finalDOB.setDeliveryZoneZipMap(deliveryZoneZipMap);
    //@@
    
    masterDOB = finalDOB.getMasterDOB();
    long start = System.currentTimeMillis();
    connection  = this.getConnection();
    connection.setAutoCommit(false);
    for(int i=0;i<masterDOB.getOriginLocation().length;i++)
    {
    csmt  = connection.prepareCall("{CALL QMS_QUOTE_PACK.quote_sell_buy_cartages_proc(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");
    csmt.setString(1,masterDOB.getShipperZipCode()!=null?masterDOB.getShipperZipCode()[i]:"");
    csmt.setString(2,(masterDOB.getShipperZones()!=null?masterDOB.getShipperZones()[i]:"").replaceAll(",","~"));
    csmt.setString(3,masterDOB.getConsigneeZipCode()!=null?masterDOB.getConsigneeZipCode()[i]:"");
    csmt.setString(4,(masterDOB.getConsigneeZones()!=null?masterDOB.getConsigneeZones()[i]:"").replaceAll(",","~"));
    csmt.setString(5,masterDOB.getSalesPersonCode());
    csmt.setString(6,masterDOB.getTerminalId());
    csmt.setString(7,masterDOB.getBuyRatesPermission());
    csmt.setString(8,masterDOB.getOriginLocation()[i]);
    csmt.setString(9,masterDOB.getDestLocation()[i]);
    csmt.setDouble(10,finalDOB.getCartageMargin()); 
    csmt.setDouble(11,finalDOB.getCartageDiscount());
    csmt.setString(12,masterDOB.getCustomerId());
    csmt.setString(13,""+masterDOB.getShipmentMode());
    csmt.setString(14,masterDOB.getShipperMode());
    if("2".equalsIgnoreCase(masterDOB.getShipperMode()))
        csmt.setString(15,masterDOB.getShipperConsoleType());
    else
        csmt.setString(15,"~");
      
    csmt.setString(16,masterDOB.getConsigneeMode());
    
    if("2".equalsIgnoreCase(masterDOB.getConsigneeMode()))
        csmt.setString(17,masterDOB.getConsigneeConsoleType());
    else
        csmt.setString(17,"~");
        
    csmt.setString(18,masterDOB.getOperation());
    //csmt.setString(19,masterDOB.getQuoteId()!=0?masterDOB.getQuoteId()+"":"");  //@@ Commented by subrahmanyam  for the enhancement 146971 on 1/12/08
    csmt.setString(19,masterDOB.getQuoteId()!=null?masterDOB.getQuoteId()+"":"");  //@@ Added by subrahmanyam  for the enhancement 146971 on 1/12/08
    
    csmt.registerOutParameter(20,OracleTypes.CURSOR);
    csmt.registerOutParameter(21,OracleTypes.CURSOR);
    csmt.registerOutParameter(22,OracleTypes.CURSOR);//Distinct Pickup Charge slabs
    csmt.registerOutParameter(23,OracleTypes.CURSOR);//Distinct Delivery Charge slabs
    
    csmt.execute();
    
    
    rs  = (ResultSet)csmt.getObject(20);
    logger.info("Time Taken for DB procedure in milli seconds for 3rd screen (quote_sell_buy_cartages_proc) :  " + ((System.currentTimeMillis()) - start)  + "   UserId ::"+ masterDOB.getUserId() + " Origin :: "+ masterDOB.getOriginLocation() + " Destination::"+masterDOB.getDestLocation()+" TerminalId :: "+ masterDOB.getTerminalId());
    pickWeightBreaksRS      = (ResultSet)csmt.getObject(22);
    delWeightBreaksRS       = (ResultSet)csmt.getObject(23);
    /*delWeightBreaksRS       = (ResultSet)csmt.getObject(23);
    delWeightBreaksRSList   = (ResultSet)csmt.getObject(24);*/
    
   originChargesList = finalDOB.getOriginChargesList();
   destChargesList   = finalDOB.getDestChargesList();
  
    while(rs.next())
    {
      if("Pickup".equalsIgnoreCase(rs.getString("COST_INCURREDAT")))
      {
      flag = rs.getString("SEL_BUY_FLAG");
     

      if( (masterDOB.getShipperZipCode()!=null && masterDOB.getShipperZipCode()[i].trim().length()!=0)
          ||(masterDOB.getShipperZones()!=null) //&& masterDOB.getShipperZones()[i].indexOf(",")==-1)  
        ) //Commented By Kishore for multiple ZoneCodes
      {
        if(originChargesList==null)
        {
          originChargesList = new ArrayList();
        }
        
        if(cartageChargesDOB!=null && (rs.getString("SELLCHARGEID").equalsIgnoreCase(cartageChargesDOB.getSellChargeId()) || rs.getString("SELLCHARGEID").equalsIgnoreCase(cartageChargesDOB.getBuyChargeId())))
        {
          chargeInfo  = new MultiQuoteChargeInfo();
          
          chargeInfoList.add(chargeInfo);
          
          chargeInfo.setBreakPoint(rs.getString("CHARGESLAB"));
          
          if(rs.getString("CURRENCY")!=null && rs.getString("CURRENCY").trim().length()!=0)
            chargeInfo.setCurrency(rs.getString("CURRENCY"));
          else
             chargeInfo.setCurrency(masterDOB.getTerminalCurrency());
             
          chargeInfo.setBuyRate(rs.getDouble("BUYRATE"));
          chargeInfo.setRecOrConSellRrate(rs.getDouble("SELLRATE"));
          chargeInfo.setSellChargeMargin(rs.getDouble("MARGINVALUE"));
          chargeInfo.setSellChargeMarginType(rs.getString("MARGIN_TYPE"));
          chargeInfo.setRateIndicator(rs.getString("RATE_INDICATOR"));
          pickWeightBreak = rs.getString("WEIGHT_BREAK");
          pickRateType    = rs.getString("RATE_TYPE");
          
          if("BASE".equalsIgnoreCase(chargeInfo.getBreakPoint()) || "MIN".equalsIgnoreCase(chargeInfo.getBreakPoint()) || "MAX".equalsIgnoreCase(chargeInfo.getBreakPoint()) || ("SLAB".equalsIgnoreCase(pickWeightBreak) && "FLAT".equalsIgnoreCase(pickRateType))  || ("BOTH".equalsIgnoreCase(pickRateType) && "F".equalsIgnoreCase(chargeInfo.getRateIndicator())))
          {
            chargeInfo.setBasis("Per Shipment");
          }
          else
          {
            if("LIST".equalsIgnoreCase(rs.getString("WEIGHT_BREAK")))
            {
              chargeInfo.setBasis("Per Container");
            }
            else if("Per Kg".equalsIgnoreCase(rs.getString("CHARGEBASIS")))
            {
              chargeInfo.setBasis("Per Kilogram");
            }
            else if("Per Lb".equalsIgnoreCase(rs.getString("CHARGEBASIS")))
            {
              chargeInfo.setBasis("Per Pound");
            }
            else if("Per CFT".equalsIgnoreCase(rs.getString("CHARGEBASIS")))
            {
              chargeInfo.setBasis("Per Cubic Feet");
            }
            else if("Per CBM".equalsIgnoreCase(rs.getString("CHARGEBASIS")))
            {
              chargeInfo.setBasis("Per Cubic Meter");
            }
            else
              chargeInfo.setBasis(rs.getString("CHARGEBASIS"));
          }
          chargeInfo.setRatio(rs.getString("DENSITY_RATIO"));
          chargeInfo.setLineNumber(rs.getInt("LINE_NO"));
          chargeInfo.setSelectedFlag(rs.getString("SELECTED_FLAG"));
          //if("M".equalsIgnoreCase(cartageChargesDOB.getMarginDiscountFlag()) || cartageChargesDOB.getMarginDiscountFlag()==null)
          if("BC".equalsIgnoreCase(flag)||"M".equalsIgnoreCase(cartageChargesDOB.getMarginDiscountFlag()) )
          {  
            chargeInfo.setMargin(rs.getDouble("MARGINVALUE"));
            chargeInfo.setTieMarginDiscountValue(rs.getDouble("MARGINVALUE"));
            chargeInfo.setMarginType(rs.getString("MARGIN_TYPE"));
            
            //@@ Commented by subrahmanyam for the Enhancement 154381 on 28/01/09              
            /*if("A".equalsIgnoreCase(rs.getString("MARGIN_TYPE")))
              sellRate  = rs.getDouble("BUYRATE")+rs.getDouble("MARGINVALUE");
            else if("P".equalsIgnoreCase(rs.getString("MARGIN_TYPE")))
              sellRate  = rs.getDouble("BUYRATE")+(rs.getDouble("BUYRATE")*rs.getDouble("MARGINVALUE")/100);*/
          //@@ Added by subrahmanyam for the Enhancement 154381 on 28/01/09
                      if(rs.getDouble("MARGINVALUE")>0 || "Y".equalsIgnoreCase(rs.getString("SELECTED_FLAG")))
                      {
            if("A".equalsIgnoreCase(rs.getString("MARGIN_TYPE")))
              sellRate  = rs.getDouble("BUYRATE")+rs.getDouble("MARGINVALUE");
            else if("P".equalsIgnoreCase(rs.getString("MARGIN_TYPE")))
              sellRate  = rs.getDouble("BUYRATE")+(rs.getDouble("BUYRATE")*rs.getDouble("MARGINVALUE")/100);
          }
                      else
                        sellRate  = rs.getDouble("BUYRATE");
        //@@ Ended by subrahmanyam for the Enhancement 154381 on 28/01/09
          }
          else   if("SC".equalsIgnoreCase(flag))
          {
            chargeInfo.setDiscount(rs.getDouble("MARGINVALUE"));
            chargeInfo.setTieMarginDiscountValue(rs.getDouble("MARGINVALUE"));
            chargeInfo.setDiscountType(rs.getString("MARGIN_TYPE"));
            
            if("A".equalsIgnoreCase(rs.getString("MARGIN_TYPE")))
              sellRate  = rs.getDouble("SELLRATE")-rs.getDouble("MARGINVALUE");
            else if("P".equalsIgnoreCase(rs.getString("MARGIN_TYPE")))
              sellRate  = rs.getDouble("SELLRATE")-(rs.getDouble("SELLRATE")*rs.getDouble("MARGINVALUE")/100);
          }
        
          chargeInfo.setSellRate(sellRate);
          chargeInfo.setTieSellRateValue(sellRate);
          /*if("A".equalsIgnoreCase(rs.getString("MARGIN_TYPE")))
            sellRate  = rs.getDouble("BUYRATE")+rs.getDouble("MARGINVALUE");
          else if("P".equalsIgnoreCase(rs.getString("MARGIN_TYPE")))
            sellRate  = rs.getDouble("BUYRATE")+(rs.getDouble("BUYRATE")*rs.getDouble("MARGINVALUE")/100);*/
            
        }
        else
        {
         
          cartageChargesDOB  = new MultiQuoteCharges();
          
          //if it is a sell charge/rate
          if("SC".equalsIgnoreCase(flag))
          {
            cartageChargesDOB.setSellChargeId(rs.getString("SELLCHARGEID"));
            cartageChargesDOB.setBuyChargeId(rs.getString("BUY_CHARGE_ID"));
          }
          else if("BC".equalsIgnoreCase(flag))
          {
            cartageChargesDOB.setBuyChargeId(rs.getString("SELLCHARGEID"));
          }
                   
          cartageChargesDOB.setSellBuyFlag(flag);
          
        //Added By Kishore for multiple ZoneCodes
          if(rs.getString("ZONE")!=null || !"".equals(rs.getString("ZONE")))
          	cartageChargesDOB.setChargeDescriptionId("Zone-"+rs.getString("ZONE")+" Pickup Charges");
          else
                cartageChargesDOB.setChargeDescriptionId("Pickup Charges");
          
          //cartageChargesDOB.setChargeDescriptionId("Pickup Charges");
          
          cartageChargesDOB.setCostIncurredAt(rs.getString("COST_INCURREDAT"));
          cartageChargesDOB.setTerminalId(rs.getString("TERMINALID"));
          cartageChargesDOB.setSelectedFlag(rs.getString("SELECTED_FLAG"));
          cartageChargesDOB.setMarginDiscountFlag(rs.getString("MARGIN_DISCOUNT_FLAG"));
          chargeInfoList  = new ArrayList();
          chargeInfo      = new MultiQuoteChargeInfo();
          chargeInfoList.add(chargeInfo);
          
          cartageChargesDOB.setChargeInfoList(chargeInfoList);
          
          chargeInfo.setBreakPoint(rs.getString("CHARGESLAB"));
          
          if(rs.getString("CURRENCY")!=null && rs.getString("CURRENCY").trim().length()!=0)
            chargeInfo.setCurrency(rs.getString("CURRENCY"));
          else
             chargeInfo.setCurrency(masterDOB.getTerminalCurrency());
             
          chargeInfo.setBuyRate(rs.getDouble("BUYRATE"));
          chargeInfo.setRecOrConSellRrate(rs.getDouble("SELLRATE"));
          chargeInfo.setSellChargeMargin(rs.getDouble("MARGINVALUE"));
          chargeInfo.setSellChargeMarginType(rs.getString("MARGIN_TYPE"));
          chargeInfo.setRateIndicator(rs.getString("RATE_INDICATOR"));
         // chargeInfo.setMargin(rs.getDouble("MARGINVALUE"));
        //  chargeInfo.setMarginType(rs.getString("MARGIN_TYPE"));
          //chargeInfo.setBasis(rs.getString("CHARGEBASIS"));
          pickWeightBreak = rs.getString("WEIGHT_BREAK");
          pickRateType    = rs.getString("RATE_TYPE");
          if("BASE".equalsIgnoreCase(chargeInfo.getBreakPoint()) || "MIN".equalsIgnoreCase(chargeInfo.getBreakPoint()) || "MAX".equalsIgnoreCase(chargeInfo.getBreakPoint()) || ("SLAB".equalsIgnoreCase(pickWeightBreak) && "FLAT".equalsIgnoreCase(pickRateType)) || ("BOTH".equalsIgnoreCase(pickRateType) && "F".equalsIgnoreCase(chargeInfo.getRateIndicator())))
          {
            chargeInfo.setBasis("Per Shipment");
          }
          else
          {
            if("LIST".equalsIgnoreCase(rs.getString("WEIGHT_BREAK")))
            {
              chargeInfo.setBasis("Per Container");
            }
            else if("Per Kg".equalsIgnoreCase(rs.getString("CHARGEBASIS")))
            {
              chargeInfo.setBasis("Per Kilogram");
            }
            else if("Per Lb".equalsIgnoreCase(rs.getString("CHARGEBASIS")))
            {
              chargeInfo.setBasis("Per Pound");
            }
            else if("Per CFT".equalsIgnoreCase(rs.getString("CHARGEBASIS")))
            {
              chargeInfo.setBasis("Per Cubic Feet");
            }
            else if("Per CBM".equalsIgnoreCase(rs.getString("CHARGEBASIS")))
            {
              chargeInfo.setBasis("Per Cubic Meter");
            }
            else
              chargeInfo.setBasis(rs.getString("CHARGEBASIS"));
          }
          chargeInfo.setRatio(rs.getString("DENSITY_RATIO"));
          chargeInfo.setLineNumber(rs.getInt("LINE_NO"));
          chargeInfo.setSelectedFlag(rs.getString("SELECTED_FLAG"));
         // if("M".equalsIgnoreCase(cartageChargesDOB.getMarginDiscountFlag()) || cartageChargesDOB.getMarginDiscountFlag()==null)
          if("BC".equalsIgnoreCase(flag)||"M".equalsIgnoreCase(cartageChargesDOB.getMarginDiscountFlag()) )
          {  
            chargeInfo.setMargin(rs.getDouble("MARGINVALUE"));
            chargeInfo.setTieMarginDiscountValue(rs.getDouble("MARGINVALUE"));
            chargeInfo.setMarginType(rs.getString("MARGIN_TYPE"));
            
            //@@ Commented by subrahmanyam for the Enhancement 154381 on 28/01/09              
            /*if("A".equalsIgnoreCase(rs.getString("MARGIN_TYPE")))
              sellRate  = rs.getDouble("BUYRATE")+rs.getDouble("MARGINVALUE");
            else if("P".equalsIgnoreCase(rs.getString("MARGIN_TYPE")))
              sellRate  = rs.getDouble("BUYRATE")+(rs.getDouble("BUYRATE")*rs.getDouble("MARGINVALUE")/100);*/
//@@ Added by subrahmanyam for the Enhancement 154381 on 28/01/09                
              if(rs.getDouble("MARGINVALUE")>0 || "Y".equalsIgnoreCase(rs.getString("SELECTED_FLAG")))
              {
            if("A".equalsIgnoreCase(rs.getString("MARGIN_TYPE")))
              sellRate  = rs.getDouble("BUYRATE")+rs.getDouble("MARGINVALUE");
            else if("P".equalsIgnoreCase(rs.getString("MARGIN_TYPE")))
              sellRate  = rs.getDouble("BUYRATE")+(rs.getDouble("BUYRATE")*rs.getDouble("MARGINVALUE")/100);
          }
          else
                sellRate  = rs.getDouble("BUYRATE");
//@@ Ended by subrahmanyam for the Enhancement 154381 on 28/01/09    
          }
          else
          {
            chargeInfo.setDiscount(rs.getDouble("MARGINVALUE"));
            chargeInfo.setTieMarginDiscountValue(rs.getDouble("MARGINVALUE"));
            chargeInfo.setDiscountType(rs.getString("MARGIN_TYPE"));
            
            if("A".equalsIgnoreCase(rs.getString("MARGIN_TYPE")))
              sellRate  = rs.getDouble("SELLRATE")-rs.getDouble("MARGINVALUE");
            else if("P".equalsIgnoreCase(rs.getString("MARGIN_TYPE")))
              sellRate  = rs.getDouble("SELLRATE")-(rs.getDouble("SELLRATE")*rs.getDouble("MARGINVALUE")/100);
          }
            
          chargeInfo.setSellRate(sellRate);
          chargeInfo.setTieSellRateValue(sellRate);
         // if("Pickup".equalsIgnoreCase(rs.getString("COST_INCURREDAT")))
          //{
     
          
            if((masterDOB.getShipperZipCode()!=null && masterDOB.getShipperZipCode()[i].trim().length()!=0)||(masterDOB.getShipperZones()!=null && masterDOB.getShipperZones()[i].indexOf(",")==-1))
            {
              
              originChargesList.add(cartageChargesDOB);  
            }
          //}            
        }
      }
      else
      {//Written by Sanjay for Cartage Charges
        //get the pickup and delivery charges as seperate entities as required for the annexure
        HashMap charge = null;
     
        if("Pickup".equalsIgnoreCase(rs.getString("COST_INCURREDAT")))
        {
          if(pickUpZoneCode.containsKey(rs.getString("ZONE")+rs.getString("SELLCHARGEID")))
          {
            if(rs.getString("CHARGESLAB")!=null)
            {
               if("FLAT".equalsIgnoreCase(rs.getString("CHARGESLAB")))
                    isPickupFlat  =  true;
               if("MAX".equalsIgnoreCase(rs.getString("CHARGESLAB")))
                    isPickupMax   =  true;
                    
               pickQuoteCartageRates = (MultiQuoteCartageRates)pickUpZoneCode.get(rs.getString("ZONE")+rs.getString("SELLCHARGEID"));
               charge = pickQuoteCartageRates.getRates();
               
               if("SC".equalsIgnoreCase(flag))
                    charge.put(rs.getString("CHARGESLAB"),rs.getString("SELLRATE"));
               else
                    charge.put(rs.getString("CHARGESLAB"),rs.getString("BUYRATE"));
                    
               pickUpZoneCode.put(rs.getString("ZONE")+rs.getString("SELLCHARGEID"),pickQuoteCartageRates);  
            }
            addToPickupList = false;
          }
          else
          {
            pickQuoteCartageRates = new MultiQuoteCartageRates();
            pickQuoteCartageRates.setZone(rs.getString("ZONE"));
            pickQuoteCartageRates.setCurrency(rs.getString("CURRENCY"));
            pickQuoteCartageRates.setCartageId(rs.getString("SELLCHARGEID"));
            pickWeightBreak = rs.getString("WEIGHT_BREAK");
            charge = new HashMap();
            if(rs.getString("CHARGESLAB")!=null)
            {
                if("MIN".equalsIgnoreCase(rs.getString("CHARGESLAB")))
                    isPickupMin   =  true;                 
                if("SC".equalsIgnoreCase(flag))
                    charge.put(rs.getString("CHARGESLAB"),rs.getString("SELLRATE"));
                else
                    charge.put(rs.getString("CHARGESLAB"),rs.getString("BUYRATE"));
                //charge.put(rs.getString("CHARGESLAB"),rs.getString("SELLRATE"));
                pickQuoteCartageRates.setRates(charge);
                pickUpZoneCode.put(rs.getString("ZONE")+rs.getString("SELLCHARGEID"),pickQuoteCartageRates);
            }
            addToPickupList = true;
          }
         
          if(addToPickupList)
            pickUpQuoteCartageRates.add(pickQuoteCartageRates);
        }                     
      }//End
    }
    if("Delivery".equalsIgnoreCase(rs.getString("COST_INCURREDAT")))
    {
     flag = rs.getString("SEL_BUY_FLAG");
    
     if((masterDOB.getConsigneeZipCode()!=null && masterDOB.getConsigneeZipCode()[i].trim().length()!=0)
          ||(masterDOB.getConsigneeZones()!=null))// && masterDOB.getConsigneeZones()[i].indexOf(",")==-1))
      {//Commented By Kishore for multiple ZoneCodes
        
        if(destChargesList==null)
        {
          destChargesList = new ArrayList();
        }
        
       
  
         if(cartageDelChargesDOB!=null && (rs.getString("SELLCHARGEID").equalsIgnoreCase(cartageDelChargesDOB.getSellChargeId()) || rs.getString("SELLCHARGEID").equalsIgnoreCase(cartageDelChargesDOB.getBuyChargeId())))
        {
          chargeInfo  = new MultiQuoteChargeInfo();
          
          chargeInfoList.add(chargeInfo);
          
          chargeInfo.setBreakPoint(rs.getString("CHARGESLAB"));
          if(rs.getString("CURRENCY")!=null && rs.getString("CURRENCY").trim().length()!=0)
            chargeInfo.setCurrency(rs.getString("CURRENCY"));
          else
             chargeInfo.setCurrency(masterDOB.getTerminalCurrency());
             
          chargeInfo.setBuyRate(rs.getDouble("BUYRATE"));
          chargeInfo.setRecOrConSellRrate(rs.getDouble("SELLRATE"));
          chargeInfo.setSellChargeMargin(rs.getDouble("MARGINVALUE"));
          chargeInfo.setSellChargeMarginType(rs.getString("MARGIN_TYPE"));
          chargeInfo.setRateIndicator(rs.getString("RATE_INDICATOR"));
          delWeightBreak   =  rs.getString("WEIGHT_BREAK");
          delRateType      =  rs.getString("RATE_TYPE");
          //chargeInfo.setMargin(rs.getDouble("MARGINVALUE"));
         // chargeInfo.setMarginType(rs.getString("MARGIN_TYPE"));
          //chargeInfo.setBasis(rs.getString("CHARGEBASIS"));
          if("BASE".equalsIgnoreCase(chargeInfo.getBreakPoint()) || "MIN".equalsIgnoreCase(chargeInfo.getBreakPoint()) || "MAX".equalsIgnoreCase(chargeInfo.getBreakPoint()) || ("SLAB".equalsIgnoreCase(delWeightBreak) && "FLAT".equalsIgnoreCase(delRateType))  || ("BOTH".equalsIgnoreCase(delRateType) && "F".equalsIgnoreCase(chargeInfo.getRateIndicator())))
          {
            chargeInfo.setBasis("Per Shipment");
          }
          else
          {
            if("LIST".equalsIgnoreCase(rs.getString("WEIGHT_BREAK")))
            {
              chargeInfo.setBasis("Per Container");
            }
            else if("Per Kg".equalsIgnoreCase(rs.getString("CHARGEBASIS")))
            {
              chargeInfo.setBasis("Per Kilogram");
            }
            else if("Per Lb".equalsIgnoreCase(rs.getString("CHARGEBASIS")))
            {
              chargeInfo.setBasis("Per Pound");
            }
            else if("Per CFT".equalsIgnoreCase(rs.getString("CHARGEBASIS")))
            {
              chargeInfo.setBasis("Per Cubic Feet");
            }
            else if("Per CBM".equalsIgnoreCase(rs.getString("CHARGEBASIS")))
            {
              chargeInfo.setBasis("Per Cubic Meter");
            }
            else
              chargeInfo.setBasis(rs.getString("CHARGEBASIS"));
          }
          chargeInfo.setRatio(rs.getString("DENSITY_RATIO"));
          chargeInfo.setLineNumber(rs.getInt("LINE_NO"));
          chargeInfo.setSelectedFlag(rs.getString("SELECTED_FLAG"));
         // if("M".equalsIgnoreCase(cartageDelChargesDOB.getMarginDiscountFlag()) || cartageDelChargesDOB.getMarginDiscountFlag()==null)
          if("BC".equalsIgnoreCase(flag)||"M".equalsIgnoreCase(cartageDelChargesDOB.getMarginDiscountFlag()))
          {  
            chargeInfo.setMargin(rs.getDouble("MARGINVALUE"));
            chargeInfo.setTieMarginDiscountValue(rs.getDouble("MARGINVALUE"));
            chargeInfo.setMarginType(rs.getString("MARGIN_TYPE"));
            
             //@@ Commented by subrahmanyam for the enhancement 154381 on 30/01/09              
            /*if("A".equalsIgnoreCase(rs.getString("MARGIN_TYPE")))
              sellRate  = rs.getDouble("BUYRATE")+rs.getDouble("MARGINVALUE");
            else if("P".equalsIgnoreCase(rs.getString("MARGIN_TYPE")))
              sellRate  = rs.getDouble("BUYRATE")+(rs.getDouble("BUYRATE")*rs.getDouble("MARGINVALUE")/100);*/
//@@ Added by subrahmanyam for the Enhancement 154381 on 30/01/09                
              if(rs.getDouble("MARGINVALUE")>0 || "Y".equalsIgnoreCase(rs.getString("SELECTED_FLAG")))
              {
            if("A".equalsIgnoreCase(rs.getString("MARGIN_TYPE")))
              sellRate  = rs.getDouble("BUYRATE")+rs.getDouble("MARGINVALUE");
            else if("P".equalsIgnoreCase(rs.getString("MARGIN_TYPE")))
              sellRate  = rs.getDouble("BUYRATE")+(rs.getDouble("BUYRATE")*rs.getDouble("MARGINVALUE")/100);
          }
          else
                sellRate  = rs.getDouble("BUYRATE");
//@@ Ended by subrahmanyam for the Enhancement 154381 on 30/01/09  
          }
          else
          {
            chargeInfo.setDiscount(rs.getDouble("MARGINVALUE"));
            chargeInfo.setTieMarginDiscountValue(rs.getDouble("MARGINVALUE"));
            chargeInfo.setDiscountType(rs.getString("MARGIN_TYPE"));
            
            if("A".equalsIgnoreCase(rs.getString("MARGIN_TYPE")))
              sellRate  = rs.getDouble("SELLRATE")-rs.getDouble("MARGINVALUE");
            else if("P".equalsIgnoreCase(rs.getString("MARGIN_TYPE")))
              sellRate  = rs.getDouble("SELLRATE")-(rs.getDouble("SELLRATE")*rs.getDouble("MARGINVALUE")/100);
          }
        
          chargeInfo.setSellRate(sellRate);
          chargeInfo.setTieSellRateValue(sellRate);
          /*if("A".equalsIgnoreCase(rs.getString("MARGIN_TYPE")))
            sellRate  = rs.getDouble("BUYRATE")+rs.getDouble("MARGINVALUE");
          else if("P".equalsIgnoreCase(rs.getString("MARGIN_TYPE")))
            sellRate  = rs.getDouble("BUYRATE")+(rs.getDouble("BUYRATE")*rs.getDouble("MARGINVALUE")/100);*/
            
        }
        else
        {
          cartageDelChargesDOB  = new MultiQuoteCharges();
          
          //if it is a sell charge/rate
          if("SC".equalsIgnoreCase(flag))
          {
            cartageDelChargesDOB.setSellChargeId(rs.getString("SELLCHARGEID"));
            cartageDelChargesDOB.setBuyChargeId(rs.getString("BUY_CHARGE_ID"));
          }
          else if("BC".equalsIgnoreCase(flag))
          {
            cartageDelChargesDOB.setBuyChargeId(rs.getString("SELLCHARGEID"));
          }
          
                    
          cartageDelChargesDOB.setSellBuyFlag(flag);
          
        //Added By Kishore for multiple ZoneCodes
          if(rs.getString("ZONE")!=null || !"".equals(rs.getString("ZONE")))
          	cartageDelChargesDOB.setChargeDescriptionId("Zone-"+rs.getString("ZONE")+" Delivery Charges");
          else
            cartageDelChargesDOB.setChargeDescriptionId("Delivery Charges");
          
          //cartageDelChargesDOB.setChargeDescriptionId("Delivery Charges");
          cartageDelChargesDOB.setCostIncurredAt(rs.getString("COST_INCURREDAT"));
          cartageDelChargesDOB.setTerminalId(rs.getString("TERMINALID"));
          cartageDelChargesDOB.setSelectedFlag(rs.getString("SELECTED_FLAG"));
          cartageDelChargesDOB.setMarginDiscountFlag(rs.getString("MARGIN_DISCOUNT_FLAG"));
          chargeInfoList  = new ArrayList();
          chargeInfo  = new MultiQuoteChargeInfo();
          chargeInfoList.add(chargeInfo);
         cartageDelChargesDOB.setChargeInfoList(chargeInfoList);
          
          chargeInfo.setBreakPoint(rs.getString("CHARGESLAB"));
           
          if(rs.getString("CURRENCY")!=null && rs.getString("CURRENCY").trim().length()!=0)
            chargeInfo.setCurrency(rs.getString("CURRENCY"));
          else
             chargeInfo.setCurrency(masterDOB.getTerminalCurrency());
             
          chargeInfo.setBuyRate(rs.getDouble("BUYRATE"));
        
          chargeInfo.setRecOrConSellRrate(rs.getDouble("SELLRATE"));
          chargeInfo.setSellChargeMargin(rs.getDouble("MARGINVALUE"));
          chargeInfo.setSellChargeMarginType(rs.getString("MARGIN_TYPE"));
          chargeInfo.setRateIndicator(rs.getString("RATE_INDICATOR"));
          delWeightBreak   =  rs.getString("WEIGHT_BREAK");
          delRateType      =  rs.getString("RATE_TYPE");
         // chargeInfo.setMargin(rs.getDouble("MARGINVALUE"));
        //  chargeInfo.setMarginType(rs.getString("MARGIN_TYPE"));
          //chargeInfo.setBasis(rs.getString("CHARGEBASIS"));
          if("BASE".equalsIgnoreCase(chargeInfo.getBreakPoint()) || "MIN".equalsIgnoreCase(chargeInfo.getBreakPoint()) || "MAX".equalsIgnoreCase(chargeInfo.getBreakPoint()) || ("SLAB".equalsIgnoreCase(delWeightBreak) && "FLAT".equalsIgnoreCase(delRateType)) || ("BOTH".equalsIgnoreCase(delRateType) && "F".equalsIgnoreCase(chargeInfo.getRateIndicator())))
          {
            chargeInfo.setBasis("Per Shipment");
          }
          else
          {
            if("LIST".equalsIgnoreCase(rs.getString("WEIGHT_BREAK")))
            {
              chargeInfo.setBasis("Per Container");
            }
            else if("Per Kg".equalsIgnoreCase(rs.getString("CHARGEBASIS")))
            {
              chargeInfo.setBasis("Per Kilogram");
            }
            else if("Per Lb".equalsIgnoreCase(rs.getString("CHARGEBASIS")))
            {
              chargeInfo.setBasis("Per Pound");
            }
            else if("Per CFT".equalsIgnoreCase(rs.getString("CHARGEBASIS")))
            {
              chargeInfo.setBasis("Per Cubic Feet");
            }
            else if("Per CBM".equalsIgnoreCase(rs.getString("CHARGEBASIS")))
            {
              chargeInfo.setBasis("Per Cubic Meter");
            }
            else
              chargeInfo.setBasis(rs.getString("CHARGEBASIS"));
          }
          chargeInfo.setRatio(rs.getString("DENSITY_RATIO"));
          chargeInfo.setLineNumber(rs.getInt("LINE_NO"));
          chargeInfo.setSelectedFlag(rs.getString("SELECTED_FLAG"));

         // if("M".equalsIgnoreCase(cartageDelChargesDOB.getMarginDiscountFlag()) || cartageDelChargesDOB.getMarginDiscountFlag()==null)
          if("BC".equalsIgnoreCase(flag)||"M".equalsIgnoreCase(cartageDelChargesDOB.getMarginDiscountFlag()))
          {   
            chargeInfo.setMargin(rs.getDouble("MARGINVALUE"));
            chargeInfo.setTieMarginDiscountValue(rs.getDouble("MARGINVALUE"));              
            chargeInfo.setMarginType(rs.getString("MARGIN_TYPE"));
            
          //@@ Commented by subramanyam for the enhancement 154381 on 05/02/09              
            /*if("A".equalsIgnoreCase(rs.getString("MARGIN_TYPE")))
              sellRate  = rs.getDouble("BUYRATE")+rs.getDouble("MARGINVALUE");
            else if("P".equalsIgnoreCase(rs.getString("MARGIN_TYPE")))
              sellRate  = rs.getDouble("BUYRATE")+(rs.getDouble("BUYRATE")*rs.getDouble("MARGINVALUE")/100);*/
//@@ Added by subrahmanyam for the Enhancement 154381 on 15/02/09             
            if(rs.getDouble("MARGINVALUE")>0 || "Y".equalsIgnoreCase(rs.getString("SELECTED_FLAG")))
            {
            if("A".equalsIgnoreCase(rs.getString("MARGIN_TYPE")))
              sellRate  = rs.getDouble("BUYRATE")+rs.getDouble("MARGINVALUE");
            else if("P".equalsIgnoreCase(rs.getString("MARGIN_TYPE")))
              sellRate  = rs.getDouble("BUYRATE")+(rs.getDouble("BUYRATE")*rs.getDouble("MARGINVALUE")/100);
          }
          else
               sellRate = rs.getDouble("BUYRATE");
//@@ Ended by subrahmanyam for the Enhancement 154381 on 15/02/09 
          }
          else
          {
            chargeInfo.setDiscount(rs.getDouble("MARGINVALUE"));
            chargeInfo.setTieMarginDiscountValue(rs.getDouble("MARGINVALUE"));             
            chargeInfo.setDiscountType(rs.getString("MARGIN_TYPE"));
            
            if("A".equalsIgnoreCase(rs.getString("MARGIN_TYPE")))
              sellRate  = rs.getDouble("SELLRATE")-rs.getDouble("MARGINVALUE");
            else if("P".equalsIgnoreCase(rs.getString("MARGIN_TYPE")))
              sellRate  = rs.getDouble("SELLRATE")-(rs.getDouble("SELLRATE")*rs.getDouble("MARGINVALUE")/100);
          }
            
          chargeInfo.setSellRate(sellRate);
          chargeInfo.setTieSellRateValue(sellRate);           
          //if("Delivery".equalsIgnoreCase(rs.getString("COST_INCURREDAT")))
          //{
           
          
            if((masterDOB.getConsigneeZipCode()!=null && masterDOB.getConsigneeZipCode()[i].trim().length()!=0)||(masterDOB.getConsigneeZones()!=null && masterDOB.getConsigneeZones()[i].indexOf(",")==-1))
            {
             destChargesList.add(cartageDelChargesDOB);  
            }
          //}
        }   
      }
     //Modified By Kishore Podili
      //else
     if(masterDOB.getConsigneeZones()[i].length()>0)
      {//Written by Sanjay for Cartage Charges
        //get the pickup and delivery charges as seperate entities as required for the annexure
        HashMap charge = null;
        if("Delivery".equalsIgnoreCase(rs.getString("COST_INCURREDAT")))
        {
          if(deliveryZoneCode.containsKey(rs.getString("ZONE")+rs.getString("SELLCHARGEID")))
          {
            if(rs.getString("CHARGESLAB")!=null)
            {
               if("FLAT".equalsIgnoreCase(rs.getString("CHARGESLAB")))
                    isDeliveryFlat  =  true;
               if("MAX".equalsIgnoreCase(rs.getString("CHARGESLAB")))
                    isDeliveryMax  =  true;
               delQuoteCartageRates = (MultiQuoteCartageRates)deliveryZoneCode.get(rs.getString("ZONE")+rs.getString("SELLCHARGEID"));
               charge = delQuoteCartageRates.getRates();
               if("SC".equalsIgnoreCase(flag))
                    charge.put(rs.getString("CHARGESLAB"),rs.getString("SELLRATE"));
                else{
                	 if("P".equalsIgnoreCase(rs.getString("margin_type"))&& !"0".equalsIgnoreCase(rs.getString("marginvalue")))
               		  sellRate  = rs.getDouble("BUYRATE")+(rs.getDouble("BUYRATE")*rs.getDouble("MARGINVALUE")/100);
               	  else if("A".equalsIgnoreCase(rs.getString("margin_type"))) 
               		  sellRate  = rs.getDouble("BUYRATE")+rs.getDouble("MARGINVALUE"); 
               	  else
               		  sellRate  = rs.getDouble("BUYRATE");
                     charge.put(rs.getString("CHARGESLAB"),Double.toString(sellRate));
                   // charge.put(rs.getString("CHARGESLAB"),rs.getString("BUYRATE"));
                }
               //charge.put(rs.getString("CHARGESLAB"),rs.getString("SELLRATE"));
               deliveryZoneCode.put(rs.getString("ZONE")+rs.getString("SELLCHARGEID"),delQuoteCartageRates);
            }
            addToDeliveryList = false;
          }
          else
          {
            delQuoteCartageRates = new MultiQuoteCartageRates();
            delWeightBreak       =  rs.getString("WEIGHT_BREAK");
            delQuoteCartageRates.setZone(rs.getString("ZONE"));
            delQuoteCartageRates.setCurrency(rs.getString("CURRENCY"));
            delQuoteCartageRates.setCartageId(rs.getString("SELLCHARGEID"));               
            charge = new HashMap();
            if(rs.getString("CHARGESLAB")!=null)
            {
              if("MIN".equalsIgnoreCase(rs.getString("CHARGESLAB")))
                  isDeliveryMin   =  true;                 
               if("SC".equalsIgnoreCase(flag))
                    charge.put(rs.getString("CHARGESLAB"),rs.getString("SELLRATE"));
                else{
                	 if("P".equalsIgnoreCase(rs.getString("margin_type"))&& !"0".equalsIgnoreCase(rs.getString("marginvalue")))
               		  sellRate  = rs.getDouble("BUYRATE")+(rs.getDouble("BUYRATE")*rs.getDouble("MARGINVALUE")/100);
               	  else if("A".equalsIgnoreCase(rs.getString("margin_type"))) 
               		  sellRate  = rs.getDouble("BUYRATE")+rs.getDouble("MARGINVALUE"); 
               	  else
               		  sellRate  = rs.getDouble("BUYRATE");
                     charge.put(rs.getString("CHARGESLAB"),Double.toString(sellRate));
                   // charge.put(rs.getString("CHARGESLAB"),rs.getString("BUYRATE"));
                }
               //charge.put(rs.getString("CHARGESLAB"),rs.getString("SELLRATE"));
               delQuoteCartageRates.setRates(charge);
               deliveryZoneCode.put(rs.getString("ZONE")+rs.getString("SELLCHARGEID"),delQuoteCartageRates);
            }
            addToDeliveryList = true;
          }
            if(addToDeliveryList)
                deliveryQuoteCartageRates.add(delQuoteCartageRates);
        }           
      }//End
    }
    } 
  
    
    if(originChargesList!=null || destChargesList!=null)
    {
      if(originChargesList!=null)
        finalDOB.setOriginChargesList(originChargesList);
      if(destChargesList!=null)
        finalDOB.setDestChargesList(destChargesList);
    }
    /*if(deliveryZoneCode.values()!=null)
    {
      deliveryQuoteCartageRates = new ArrayList();
      deliveryQuoteCartageRates.addAll(deliveryZoneCode.values());
    }
    if(pickUpZoneCode.values()!=null)
    {
      pickUpQuoteCartageRates = new ArrayList();
      pickUpQuoteCartageRates.addAll(pickUpZoneCode.values());
    }*/
    if(deliveryQuoteCartageRates!=null && deliveryQuoteCartageRates.size()>0)
    {
      //delWeightBreaksRS   =   (ResultSet)csmt.getObject(23);
      
      if(isDeliveryMin)
        delWeightBreaksList.add("MIN");
      
      if(isDeliveryFlat)
          delWeightBreaksList.add("FLAT");
      while(delWeightBreaksRS.next())
      {
        delWeightBreaksList.add(delWeightBreaksRS.getString("CHARGESLAB"));
      }
      
      //delWeightBreaksRSList = (ResultSet)csmt.getObject(24);
      
      /*while(delWeightBreaksRSList.next())
      {
        delWeightBreaksList.add(delWeightBreaksRSList.getString("CHARGESLAB"));
      }*/
      
      if(isDeliveryMax)
          delWeightBreaksList.add("MAX");
   
      finalDOB.setDeliveryCartageRatesList(deliveryQuoteCartageRates);
      finalDOB.setDeliveryWeightBreaks(delWeightBreaksList);
    }
    if(pickUpQuoteCartageRates!=null && pickUpQuoteCartageRates.size()>0)
    {
     // pickWeightBreaksRS          = (ResultSet)csmt.getObject(21);
      //pickWeightBreaksRSList  = 
      if(isPickupMin)
        pickupWeightBreaksList.add("MIN");
      
      if(isPickupFlat)
        pickupWeightBreaksList.add("FLAT");
      
      while(pickWeightBreaksRS.next())
      {
        pickupWeightBreaksList.add(pickWeightBreaksRS.getString("CHARGESLAB"));
      }
      
      //pickWeightBreaksRSList  = (ResultSet)csmt.getObject(22);
      
      /*while(pickWeightBreaksRSList.next())
      {
        pickupWeightBreaksList.add(pickWeightBreaksRSList.getString("CHARGESLAB"));
      }*/
      
      if(isPickupMax)
        pickupWeightBreaksList.add("MAX");
      
    
      
      finalDOB.setPickUpCartageRatesList(pickUpQuoteCartageRates);
      finalDOB.setPickupWeightBreaks(pickupWeightBreaksList);
    }
      
    //The below code is to get the ZONE-ZIPCODE Mapping from the CURSOR from 20 OUTPARAMETER
    
      zoneRs  = (ResultSet)csmt.getObject(21);
      ArrayList delivZipCodes  = null;
      ArrayList pickUpZipCodes = null;
          
      while(zoneRs.next())
      {
        if("Pickup".equalsIgnoreCase(zoneRs.getString("charge_type")))
        {
          if( !((masterDOB.getShipperZipCode()!=null && masterDOB.getShipperZipCode()[i].trim().length()!=0)))
          {
            String from_toZip = null;
            if(pickUpZoneZipMap.containsKey(zoneRs.getString("ZONE")))
            {             
              pickUpZipCodes = (ArrayList) pickUpZoneZipMap.get(zoneRs.getString("ZONE"));
              if(zoneRs.getString("ALPHANUMERIC")!=null)
               from_toZip = zoneRs.getString("ALPHANUMERIC")+zoneRs.getString("from_zipcode")+" - "+zoneRs.getString("ALPHANUMERIC")+zoneRs.getString("to_zipcode");
              else
               from_toZip = zoneRs.getString("from_zipcode")+" - "+zoneRs.getString("to_zipcode");
              if(from_toZip!=null)
                pickUpZipCodes.add(from_toZip);
              pickUpZoneZipMap.put(zoneRs.getString("ZONE"),pickUpZipCodes);
            }
            else
            {
              pickUpZipCodes = new ArrayList();
              if(zoneRs.getString("from_zipcode")!=null)
              {
                if(zoneRs.getString("ALPHANUMERIC")!=null)
                 from_toZip = zoneRs.getString("ALPHANUMERIC")+zoneRs.getString("from_zipcode")+" - "+zoneRs.getString("ALPHANUMERIC")+zoneRs.getString("to_zipcode");
                else
                 from_toZip = zoneRs.getString("from_zipcode")+" - "+zoneRs.getString("to_zipcode");
                if(from_toZip!=null)
                  pickUpZipCodes.add(from_toZip);
                pickUpZoneZipMap.put(zoneRs.getString("ZONE"),pickUpZipCodes);
              }
            }
          }
        }
        else if("Delivery".equalsIgnoreCase(zoneRs.getString("charge_type")))
        {
          if(!((masterDOB.getConsigneeZipCode()!=null && masterDOB.getConsigneeZipCode()[i].trim().length()!=0)))
          {
            String from_toZip = null;
            if(deliveryZoneZipMap.containsKey(zoneRs.getString("ZONE")))
            {
              delivZipCodes = (ArrayList) deliveryZoneZipMap.get(zoneRs.getString("ZONE"));
              if(zoneRs.getString("ALPHANUMERIC")!=null)
               from_toZip = zoneRs.getString("ALPHANUMERIC")+zoneRs.getString("from_zipcode")+" - "+zoneRs.getString("ALPHANUMERIC")+zoneRs.getString("to_zipcode");
              else
               from_toZip = zoneRs.getString("from_zipcode")+" - "+zoneRs.getString("to_zipcode");
              if(from_toZip!=null)
                delivZipCodes.add(from_toZip);
              deliveryZoneZipMap.put(zoneRs.getString("ZONE"),delivZipCodes);
            }
            else
            { 
              delivZipCodes = new ArrayList();
              if(zoneRs.getString("ALPHANUMERIC")!=null)
               from_toZip = zoneRs.getString("ALPHANUMERIC")+zoneRs.getString("from_zipcode")+" - "+zoneRs.getString("ALPHANUMERIC")+zoneRs.getString("to_zipcode");
              else
               from_toZip = zoneRs.getString("from_zipcode")+" - "+zoneRs.getString("to_zipcode");
              if(from_toZip!=null)
                delivZipCodes.add(from_toZip);
              deliveryZoneZipMap.put(zoneRs.getString("ZONE"),delivZipCodes);
            }
          }
        }          
      }
  }
      finalDOB.setPickZoneZipMap(pickUpZoneZipMap);
      finalDOB.setDeliveryZoneZipMap(deliveryZoneZipMap);
    //End
    
  }
  catch(SQLException sqEx)
		{
    sqEx.printStackTrace();
			//Logger.error(FILE_NAME,"QMSQuoteDAO[getLegCharges(masterDOB,sellBuyRate,sellBuyFlag,laneNo,origin,dest)] -> "+sqEx.toString());
    logger.error(FILE_NAME+"QMSQuoteDAO[getCartages(masterDOB,sellBuyRate,sellBuyFlag,laneNo,origin,dest)] -> "+sqEx.toString());
    throw new SQLException(sqEx.toString());
		}
  catch(Exception e)
		{
    e.printStackTrace();
			//Logger.error(FILE_NAME,"QMSQuoteDAO[getLegCharges(masterDOB,sellBuyRate,sellBuyFlag,laneNo,origin,dest)] -> "+e.toString());
    logger.error(FILE_NAME+"QMSQuoteDAO[getCartages(masterDOB,sellBuyRate,sellBuyFlag,laneNo,origin,dest)] -> "+e.toString());
    throw new SQLException(e.toString());
		}
		finally
		{
			try
			{				
      if(zoneRs!=null)
          zoneRs.close();
      if(pickWeightBreaksRS!=null)
        pickWeightBreaksRS.close();
      if(delWeightBreaksRS!=null)
        delWeightBreaksRS.close();
      if(rs!=null)
        rs.close();
      if(csmt!=null)
        csmt.close();
      if(connection!=null)
        connection.close();
      //ConnectionUtil.closeConnection(connection,csmt,rs);
			}
			catch(Exception ex)
			{
				//Logger.error(FILE_NAME,"Finally : QMSQuoteDAO[getLegCharges(masterDOB,sellBuyRate,sellBuyFlag,laneNo,origin,dest)]-> "+ex.toString());
      logger.error(FILE_NAME+"Finally : QMSQuoteDAO[getLegCharges(masterDOB,sellBuyRate,sellBuyFlag,laneNo,origin,dest)]-> "+ex.toString());
      throw new SQLException(ex.toString());
			}
		}
  return finalDOB;
}
public MultiQuoteFinalDOB getZipZoneMapping(MultiQuoteFinalDOB finalDOB) throws SQLException
{
Connection                  connection             = null;
PreparedStatement           stmt                   = null;
ResultSet                   rs                     = null;

MultiQuoteMasterDOB              masterDOB              = null;

String                      shipZoneQry            = "";
String                      consZoneQry            = "";

String                      sqlQuery1              = "";
String                      sqlQuery2              = "";
String                      sqlQuery               = "";

ArrayList                   delivZipCodes          = null;
ArrayList                   pickUpZipCodes         = null;
HashMap                     pickUpZoneZipMap       = new HashMap();
HashMap                     deliveryZoneZipMap     = new HashMap();
//StringTokenizer             str                    = null;
//String[]                    shipperZoneArray       = null;
//String                      shipperZones           = "";
//String[]                    consigneeZoneArray     = null;
//String                      consigneeZones         = "";
boolean                     executeQry             = true;
String                      originCountryId        = null;
String                      destinationCountryId   = null;
StringBuffer                shZones                = new StringBuffer();
int                         shZonesLength          = 0;
String[]                    shZoneArray            = null;

StringBuffer                consigneeZones         = new StringBuffer();
int                         consigneeZonesLength   = 0;
String[]                    consigneeZoneArray     = null;
    


try
{
  connection            =   this.getConnection();
  
  //stmt                  =   connection.prepareStatement();
  
  masterDOB             =   finalDOB.getMasterDOB();
  for(int i=0;i<finalDOB.getHeaderDOB().getOriginCountryId().length;i++)
  originCountryId       =   finalDOB.getHeaderDOB().getOriginCountryId()[i];
  for(int i=0;i<finalDOB.getHeaderDOB().getDestinationCountryId().length;i++)
  destinationCountryId  =   finalDOB.getHeaderDOB().getDestinationCountryId()[i];
  for(int i=0;i<masterDOB.getShipperZones().length;i++)
  {
  if((masterDOB.getShipperZones()!=null && masterDOB.getShipperZones()[i]!= null)&& masterDOB.getShipperZones()[i].trim().length()!=0)
  {
    /*str               =   new StringTokenizer(masterDOB.getShipperZones(),",");      
    shipperZoneArray  =   new String[str.countTokens()];
    
    for(int i=0;str.hasMoreTokens();i++)
    {
        shipperZoneArray[i] = str.nextToken();
    }*/
    if(masterDOB.getShipperZones()[i].split(",").length > 0)
    {
       shZonesLength  =  masterDOB.getShipperZones()[i].split(",").length;
       shZoneArray    =  masterDOB.getShipperZones()[i].split(",");
        
        for(int k=0;k<shZonesLength;k++)
        {
            if(k==(shZonesLength-1))
                shZones.append("?");
            else
                shZones.append("?,");
        }
    }
  }
  }
  
  /*if(shipperZoneArray!=null)
  {
    for(int i=0;i<shipperZoneArray.length;i++)
    {
      if((i+1)==shipperZoneArray.length)
          shipperZones  = shipperZones + "'"+shipperZoneArray[i]+"'";
      else        
          shipperZones  = shipperZones + "'"+shipperZoneArray[i]+"',";
    }
  }
  str = null;*/
  for(int i=0;i<masterDOB.getConsigneeZones().length;i++)
  {
  if((masterDOB.getConsigneeZones()!=null && masterDOB.getConsigneeZones()[i]!= null) && masterDOB.getConsigneeZones()[i].trim().length()!=0)
  {
    /*str                 = new StringTokenizer(masterDOB.getConsigneeZones(),",");
    consigneeZoneArray  = new String[str.countTokens()];
    
    for(int i=0;str.hasMoreTokens();i++)
    {
        consigneeZoneArray[i] = str.nextToken();
    }*/
    if(masterDOB.getConsigneeZones()[i].split(",").length > 0)
    {
        consigneeZonesLength  =  masterDOB.getConsigneeZones()[i].split(",").length;
        consigneeZoneArray    =  masterDOB.getConsigneeZones()[i].split(",");
        
        for(int k=0;k<consigneeZonesLength;k++)
        {
            if(k==(consigneeZonesLength-1))
                consigneeZones.append("?");
            else
                consigneeZones.append("?,");
        }
    }
  }
  }
  
  /*if(consigneeZoneArray!=null)
  {
    for(int i=0;i<consigneeZoneArray.length;i++)
    {
      if((i+1)==consigneeZoneArray.length)
          consigneeZones  = consigneeZones + "'"+consigneeZoneArray[i]+"'";
      else        
          consigneeZones  = consigneeZones + "'"+consigneeZoneArray[i]+"',";
    }
  }*/
  
  
  /*if(masterDOB.getShipperZones()!=null && masterDOB.getShipperZones().trim().length()!=0)
      shipZoneQry = " AND D.ZONE IN ("+shipperZones+") ";
  if(masterDOB.getConsigneeZones()!=null && masterDOB.getConsigneeZones().trim().length()!=0)
      consZoneQry = " AND D.ZONE IN ("+consigneeZones+") ";*/
      
 /* if("CA".equalsIgnoreCase(originCountryId))
  {
    sqlQuery1       = "SELECT D.ZONE, NULL ALPHANUMERIC, D.TO_ZIPCODE, D.FROM_ZIPCODE,'Pickup' CHARGE_TYPE "+
                      "FROM QMS_ZONE_CODE_MASTER_CA M, QMS_ZONE_CODE_DTL_CA D WHERE D.ZONE_CODE = M.ZONE_CODE AND M.LOCATION_ID = ?"+
                      " AND M.SHIPMENT_MODE = ? AND NVL(M.CONSOLE_TYPE,'~')= ? AND D.ZONE IN ("+shZones+")";
  }
  else
  {
    sqlQuery1       = "SELECT D.ZONE, D.ALPHANUMERIC ALPHANUMERIC, D.TO_ZIPCODE, D.FROM_ZIPCODE,'Pickup' CHARGE_TYPE "+
                      "FROM QMS_ZONE_CODE_MASTER M, QMS_ZONE_CODE_DTL D WHERE D.ZONE_CODE = M.ZONE_CODE AND M.ORIGIN_LOCATION = ? "+
                      "AND M.SHIPMENT_MODE = ? AND NVL(M.CONSOLE_TYPE,'~')= ? AND D.ZONE IN ("+shZones+")";
  }*/

  if("CA".equalsIgnoreCase(originCountryId))
  {
    sqlQuery1       = "SELECT D.ZONE, NULL ALPHANUMERIC, D.TO_ZIPCODE, D.FROM_ZIPCODE,'Pickup' CHARGE_TYPE "+
                      "FROM QMS_ZONE_CODE_MASTER_CA M, QMS_ZONE_CODE_DTL_CA D WHERE D.ZONE_CODE = M.ZONE_CODE AND M.LOCATION_ID = ?"+
                      " AND M.SHIPMENT_MODE = ? AND NVL(M.CONSOLE_TYPE,'~')= ? AND D.ZONE IN ("+shZones+")";
  }
  else
  {
    sqlQuery1       = "SELECT D.ZONE, D.ALPHANUMERIC ALPHANUMERIC, D.TO_ZIPCODE, D.FROM_ZIPCODE,'Pickup' CHARGE_TYPE "+
                      "FROM QMS_ZONE_CODE_MASTER M, QMS_ZONE_CODE_DTL D WHERE D.ZONE_CODE = M.ZONE_CODE AND M.ORIGIN_LOCATION = ? "+
                      "AND M.SHIPMENT_MODE = ? AND NVL(M.CONSOLE_TYPE,'~')= ? AND D.ZONE IN ("+shZones+")";
  }
  
  
  /*if("CA".equalsIgnoreCase(destinationCountryId))
  {
    sqlQuery2       = "SELECT D.ZONE, NULL ALPHANUMERIC, D.TO_ZIPCODE, D.FROM_ZIPCODE,'Delivery' CHARGE_TYPE "+
                      "FROM QMS_ZONE_CODE_MASTER_CA M,QMS_ZONE_CODE_DTL_CA D WHERE D.ZONE_CODE = M.ZONE_CODE AND M.LOCATION_ID = ? "+
                      "AND M.SHIPMENT_MODE = ? AND NVL(M.CONSOLE_TYPE,'~')= ? AND D.ZONE IN ("+consigneeZones+") ";
  }
  else
  {
    sqlQuery2       = "SELECT D.ZONE, D.ALPHANUMERIC ALPHANUMERIC, D.TO_ZIPCODE, D.FROM_ZIPCODE,'Delivery' CHARGE_TYPE "+
                      "FROM QMS_ZONE_CODE_MASTER M,QMS_ZONE_CODE_DTL D WHERE D.ZONE_CODE = M.ZONE_CODE AND M.ORIGIN_LOCATION = ? "+
                      "AND M.SHIPMENT_MODE = ? AND NVL(M.CONSOLE_TYPE,'~')= ? AND D.ZONE IN ("+consigneeZones+") ";
  }*/
   if("CA".equalsIgnoreCase(destinationCountryId))
  {
    sqlQuery2       = "SELECT D.ZONE, NULL ALPHANUMERIC, D.TO_ZIPCODE, D.FROM_ZIPCODE,'Delivery' CHARGE_TYPE "+
                      "FROM QMS_ZONE_CODE_MASTER_CA M,QMS_ZONE_CODE_DTL_CA D WHERE D.ZONE_CODE = M.ZONE_CODE AND M.LOCATION_ID = ? "+
                      "AND M.SHIPMENT_MODE = ? AND NVL(M.CONSOLE_TYPE,'~')= ? AND D.ZONE IN ("+consigneeZones+") ";
  }
  else
  {
    sqlQuery2       = "SELECT D.ZONE, D.ALPHANUMERIC ALPHANUMERIC, D.TO_ZIPCODE, D.FROM_ZIPCODE,'Delivery' CHARGE_TYPE "+
                      "FROM QMS_ZONE_CODE_MASTER M,QMS_ZONE_CODE_DTL D WHERE D.ZONE_CODE = M.ZONE_CODE AND M.ORIGIN_LOCATION = ? "+
                      "AND M.SHIPMENT_MODE = ? AND NVL(M.CONSOLE_TYPE,'~')= ? AND D.ZONE IN ("+consigneeZones+") ";
  }
   for(int i=0;i<masterDOB.getOriginLocation().length;i++)
   {
  if(masterDOB.getShipperZones() !=null && masterDOB.getConsigneeZones()!=null)
  {
      if(masterDOB.getShipperZipCode()==null && masterDOB.getConsigneeZipCode()==null)
      {
        sqlQuery    =  sqlQuery1+" UNION "+sqlQuery2 +" Order By Zone,Alphanumeric, From_Zipcode";
        stmt        =  connection.prepareStatement(sqlQuery);
        stmt.setString(1,masterDOB.getOriginLocation()[i]);
        stmt.setString(2,masterDOB.getShipperMode());
        if("1".equalsIgnoreCase(masterDOB.getShipperMode()))
            stmt.setString(3,"~");
        else
            stmt.setString(3,masterDOB.getShipperConsoleType());
        
        int k=0;
        if(shZoneArray!=null && shZoneArray.length>0)
        {
        	int shZoneArrLen	=	shZoneArray.length;
          for(k=0;k<shZoneArrLen;k++)
              stmt.setString(k+4,shZoneArray[k]);
          k = k+3;
        }
        else
          k = k+2;
        
        
        stmt.setString(k+1,masterDOB.getDestLocation()[i]);
        stmt.setString(k+2,masterDOB.getConsigneeMode());
        
        if("1".equalsIgnoreCase(masterDOB.getConsigneeMode()))
            stmt.setString(k+3,"~");
        else
            stmt.setString(k+3,masterDOB.getConsigneeConsoleType());
        
        if(consigneeZoneArray!=null && consigneeZoneArray.length>0)
        {
        	int conZoneArrLen	=	consigneeZoneArray.length;
          for(int j=0;j<conZoneArrLen;j++)
              stmt.setString(j+k+4,consigneeZoneArray[j]);
        }
        rs = stmt.executeQuery();
        
      }
      else if (masterDOB.getShipperZipCode()==null && masterDOB.getConsigneeZipCode()!=null)
      {
        sqlQuery    =   sqlQuery1 + " Order By Zone,Alphanumeric, From_Zipcode";
        stmt        =  connection.prepareStatement(sqlQuery);
        stmt.setString(1,masterDOB.getOriginLocation()[i]);
        stmt.setString(2,masterDOB.getShipperMode());
        if("1".equalsIgnoreCase(masterDOB.getShipperMode()))
            stmt.setString(3,"~");
        else
            stmt.setString(3,masterDOB.getShipperConsoleType());
            
        if(shZoneArray!=null && shZoneArray.length>0)
        {
        	int shZoneArrLen	=	shZoneArray.length;
          for(int k=0;k<shZoneArrLen;k++)
              stmt.setString(k+4,shZoneArray[k]);
        }
        rs = stmt.executeQuery();
      }
      else if(masterDOB.getShipperZipCode()!=null && masterDOB.getConsigneeZipCode()==null)
      {
        sqlQuery    =   sqlQuery2 + "Order By Zone,Alphanumeric, From_Zipcode";
        stmt        =  connection.prepareStatement(sqlQuery);
        stmt.setString(1,masterDOB.getDestLocation()[i]);
        stmt.setString(2,masterDOB.getConsigneeMode());
        if("1".equalsIgnoreCase(masterDOB.getConsigneeMode()))
            stmt.setString(3,"~");
        else
            stmt.setString(3,masterDOB.getConsigneeConsoleType());
            
        if(consigneeZoneArray!=null && consigneeZoneArray.length>0)
        {
        	int conZoneArrLen	=	consigneeZoneArray.length;
          for(int k=0;k<conZoneArrLen;k++)
              stmt.setString(k+4,consigneeZoneArray[k]);
        }
        rs = stmt.executeQuery();
        
      }
  }
  else
  {
      if(masterDOB.getShipperZones()==null && masterDOB.getConsigneeZones()!=null && masterDOB.getConsigneeZipCode()==null)
      {
        sqlQuery    =  sqlQuery2 +" Order By Zone,Alphanumeric, From_Zipcode";
        stmt        =  connection.prepareStatement(sqlQuery);
        stmt.setString(1,masterDOB.getDestLocation()[i]);
        stmt.setString(2,masterDOB.getConsigneeMode());
        if("1".equalsIgnoreCase(masterDOB.getConsigneeMode()))
            stmt.setString(3,"~");
        else
            stmt.setString(3,masterDOB.getConsigneeConsoleType());
            
        if(consigneeZoneArray!=null && consigneeZoneArray.length>0)
        {
        	int conZoneArrLen	=	consigneeZoneArray.length;
          for(int k=0;k<conZoneArrLen;k++)
              stmt.setString(k+4,consigneeZoneArray[k]);
        }
        rs = stmt.executeQuery();            
      }
      else if(masterDOB.getShipperZones()!=null && masterDOB.getConsigneeZones()==null  && masterDOB.getShipperZipCode()==null)
      {
        sqlQuery    =   sqlQuery1 +" Order By Zone,Alphanumeric, From_Zipcode";
        stmt        =  connection.prepareStatement(sqlQuery);
        stmt.setString(1,masterDOB.getOriginLocation()[i]);
        stmt.setString(2,masterDOB.getShipperMode());
        if("1".equalsIgnoreCase(masterDOB.getShipperMode()))
            stmt.setString(3,"~");
        else
            stmt.setString(3,masterDOB.getShipperConsoleType());
            
        if(shZoneArray!=null && shZoneArray.length>0)
        {
        	int shZoneArrLen	=	shZoneArray.length;
          for(int k=0;k<shZoneArrLen;k++)
              stmt.setString(k+4,shZoneArray[k]);
        }
        rs = stmt.executeQuery();
      }
  }
}  
  if(rs!=null)
  {
    while(rs.next())
    {
       if("Pickup".equalsIgnoreCase(rs.getString("CHARGE_TYPE")))
        {            
          String from_toZip = null;
          if(pickUpZoneZipMap.containsKey(rs.getString("ZONE")))
          {             
            pickUpZipCodes = (ArrayList) pickUpZoneZipMap.get(rs.getString("ZONE"));
            if(rs.getString("ALPHANUMERIC")!=null)
             from_toZip = rs.getString("ALPHANUMERIC")+rs.getString("FROM_ZIPCODE")+" - "+rs.getString("ALPHANUMERIC")+rs.getString("TO_ZIPCODE");
            else
             from_toZip = rs.getString("FROM_ZIPCODE")+" - "+rs.getString("TO_ZIPCODE");
            if(from_toZip!=null)
              pickUpZipCodes.add(from_toZip);
            pickUpZoneZipMap.put(rs.getString("ZONE"),pickUpZipCodes);
          }
          else
          {
            pickUpZipCodes = new ArrayList();
            if(rs.getString("FROM_ZIPCODE")!=null)
            {
              if(rs.getString("ALPHANUMERIC")!=null)
               from_toZip = rs.getString("ALPHANUMERIC")+rs.getString("FROM_ZIPCODE")+" - "+rs.getString("ALPHANUMERIC")+rs.getString("TO_ZIPCODE");
              else
               from_toZip = rs.getString("FROM_ZIPCODE")+" - "+rs.getString("TO_ZIPCODE");
              if(from_toZip!=null)
                pickUpZipCodes.add(from_toZip);
              pickUpZoneZipMap.put(rs.getString("ZONE"),pickUpZipCodes);
            }
          }
        }
        else if("Delivery".equalsIgnoreCase(rs.getString("CHARGE_TYPE")))
        {            
          String from_toZip = null;
          if(deliveryZoneZipMap.containsKey(rs.getString("ZONE")))
          {
            delivZipCodes = (ArrayList) deliveryZoneZipMap.get(rs.getString("ZONE"));
            if(rs.getString("ALPHANUMERIC")!=null)
             from_toZip = rs.getString("ALPHANUMERIC")+rs.getString("FROM_ZIPCODE")+" - "+rs.getString("ALPHANUMERIC")+rs.getString("TO_ZIPCODE");
            else
             from_toZip = rs.getString("FROM_ZIPCODE")+" - "+rs.getString("TO_ZIPCODE");
            if(from_toZip!=null)
              delivZipCodes.add(from_toZip);
            deliveryZoneZipMap.put(rs.getString("ZONE"),delivZipCodes);
          }
          else
          { 
            delivZipCodes = new ArrayList();
            if(rs.getString("ALPHANUMERIC")!=null)
             from_toZip = rs.getString("ALPHANUMERIC")+rs.getString("FROM_ZIPCODE")+" - "+rs.getString("ALPHANUMERIC")+rs.getString("TO_ZIPCODE");
            else
             from_toZip = rs.getString("FROM_ZIPCODE")+" - "+rs.getString("TO_ZIPCODE");
            if(from_toZip!=null)
              delivZipCodes.add(from_toZip);
            deliveryZoneZipMap.put(rs.getString("ZONE"),delivZipCodes);
          }
        }
    }
  }
  
  if(pickUpZoneZipMap.size() > 0)
      finalDOB.setPickZoneZipMap(pickUpZoneZipMap);
  if(deliveryZoneZipMap.size() > 0)
      finalDOB.setDeliveryZoneZipMap(deliveryZoneZipMap);
  
}
catch(SQLException sql)
{
  //Logger.error(FILE_NAME,"SQLException while getting the Zip Zone Code Mapping: "+sql);
  logger.error(FILE_NAME+"SQLException while getting the Zip Zone Code Mapping: "+sql);
  sql.printStackTrace();
  throw new SQLException(sql.toString());
}
catch(Exception e)
{
  //Logger.error(FILE_NAME,"Error while getting the Zip Zone Code Mapping: "+e);
  logger.error(FILE_NAME+"Error while getting the Zip Zone Code Mapping: "+e);
  e.printStackTrace();
  throw new SQLException(e.toString());
}
finally
{
  if(rs!=null)
      rs.close();
  if(stmt!=null)
      stmt.close();
  if(connection !=null)
      connection.close();
}
return finalDOB;
}
//Ended for the Issue 234719
//Added by Anil.k for Spot Rates
private void insertQuoteSpotRates(MultiQuoteFinalDOB finalDOB,Connection connection,int laneno,int lane,String spotRatesFlag) throws SQLException
{
  
  PreparedStatement pStmt           = null;
  PreparedStatement pStmt1          = null;
  ResultSet			rs1				= null; 
  ArrayList legDetails              = null;
  ArrayList originChargesList       = null;
  ArrayList destChargesList         = null;
  ArrayList frtChargesList          = null;
  String Margin_discount_flag       = null;
  MultiQuoteMasterDOB  masterDOB         = null;
  MultiQuoteFreightLegSellRates  legDOB  = null;
  MultiQuoteCharges    chargesDOB        = null;
	MultiQuoteFreightLegSellRates legRateDetails = null;
  ArrayList       chargesList       = null;
  MultiQuoteChargeInfo chargeInfoDOB     = null;
  int[] originIndices               = null;
  int[] destIndices                 = null;
  int[] freightIndices              = null;
  ArrayList freightRates	= new ArrayList();
  String selectedBreaks				=	null;
  String[]  selBreaks                = null;  
  String[] currencyId			  = null;
  String    spotDesc			  = "";  
  
  int   noOfLegs                    = 0;
  int   size                        = 0;
  int   chargeSize                  = 0;
   String chargeDescription = null;
  try
  {
    pStmt             =        connection.prepareStatement(selectedSpotRatesInsertQuery);
    
    masterDOB             =        finalDOB.getMasterDOB();    
    logger.info("userid : "+masterDOB.getCreatedBy());    
   
    
    if(!"Charges".equalsIgnoreCase(masterDOB.getQuoteWith()))
    {
      freightRates  = finalDOB.getLegDetails();
      Hashtable spotRateDetails = null;
      MultiQuoteChargeInfo chargeInfo = null;
      String[] temp_Marg_Dis_flag	= null;	
	  legRateDetails = (MultiQuoteFreightLegSellRates)freightRates.get(lane);
	  spotRateDetails = legRateDetails.getSpotRateDetails();
	  ArrayList spotRateDesc = legRateDetails.getSpotRateDescription();
	  ArrayList surChargeId  = legRateDetails.getSurchargeId();
	  ArrayList chargeRateIndicator = legRateDetails.getChargeRateIndicator();
	  currencyId = legRateDetails.getCurrency().split(",");
	  double[] rate = null;
	  double spotRateId = 0;
	  String weightBreak = "";
	  
	  selectedBreaks  = finalDOB.getMultiQuoteSelectedBreaks();
	  selBreaks = selectedBreaks.split(",");	
	  if(spotRatesFlag!=null && "Y".equalsIgnoreCase(spotRatesFlag)){
	  for(int sp=0;sp<spotRateDetails.size();sp++)
	  {	    
		  String wtBreak = (String)legRateDetails.getWeightBreaks().get(sp);
		  rate = (double[])spotRateDetails.get(wtBreak);		  
		  weightBreak = ("".equals((String)surChargeId.get(sp))?legRateDetails.getSpotRatesType():("List".equalsIgnoreCase(legRateDetails.getSpotRatesType())?"List":""));
		  pStmt.setLong(1, masterDOB.getUniqueId());
		  pStmt.setLong(2, lane);
		  pStmt.setDouble(3, rate[0]);
		  pStmt.setDouble(4, rate[1]);
		  pStmt.setDouble(5, rate[2]);
		  pStmt.setString(6, wtBreak.toUpperCase());
		  pStmt.setLong(7, sp);
		  pStmt.setInt(8, masterDOB.getShipmentMode());
		  pStmt.setString(9, legRateDetails.getServiceLevel());		  
		  pStmt.setString(10, legRateDetails.getUom());
		  pStmt.setString(11, legRateDetails.getDensityRatio());
		  pStmt.setString(12, legRateDetails.getSpotRatesType());
		  //pStmt.setString(13, currencyId[sp]);
		  //@@Added by kiran.v on 01/08/2011 for Wpbn Issue 257513
		  pStmt.setString(13, legRateDetails.getCurrency());		  
		  pStmt.setString(14, (String)spotRateDesc.get(sp));
		  pStmt.setString(15, (String)surChargeId.get(sp));
		  pStmt.setString(16, (String)chargeRateIndicator.get(sp));
		  pStmt.executeUpdate();
		  //pStmt.addBatch();		
		  spotDesc = (String)spotRateDesc.get(sp);
	  }
    }
    }
        
    //pStmt.executeBatch();
    
  }
  catch(Exception e)
  {
   
    logger.error(FILE_NAME+"Error in insertSelectedRates"+e);
    e.printStackTrace();
    throw new SQLException(e.toString());
  }
  finally
  {
    ConnectionUtil.closeConnection(null,pStmt);
    ConnectionUtil.closeConnection(null,pStmt1,rs1);
  }
} 
public MultiQuoteFreightLegSellRates getSpotRates(String origin, String destination, String serviceLevelId, int shipmentMode, String terminalId,String permissionFlag,String operation,String quoteId,String incoTerms, String wtBrk,int laneNo) throws SQLException
{	
	MultiQuoteFreightLegSellRates  legRateDetails = null;
	CallableStatement         csmt                = null;	
	ResultSet			singleReturn			  = null; 
	ResultSet			multipleReturn			  = null;
	Connection connection      = null;
	double[] rateDetail 	   = null;
	Hashtable spotRateDetails  = new Hashtable();
	ArrayList weightBreakSlabs = new ArrayList();
	LinkedHashSet weightBreak  = new LinkedHashSet();
	ArrayList rateDescription  = new ArrayList();
	ArrayList chargeRateIndicator = new ArrayList();
	ArrayList surChargeId  	   = new ArrayList();
	LinkedHashSet currencyId   = new LinkedHashSet();
	ArrayList checkedFlag  	   = new ArrayList();
	ArrayList marginType	   = new ArrayList();
	ArrayList marginValue	   = new ArrayList();
	String    surChrg          = "";
	String    surBreak		   = "";
	int       index			   = 0;
	try{
		connection    =   this.getConnection();
		csmt  = connection.prepareCall("{CALL QMS_QUOTE_PACK.MULTI_QUOTE_SPOT_RATE_PROC(?,?,?,?,?,?,?,?,?,?,?,?)}");
		csmt.setString(1,origin);
		csmt.setString(2,destination);
		csmt.setString(3,terminalId);
		csmt.setString(4,serviceLevelId);
		csmt.setInt(5,shipmentMode);
		csmt.setString(6,permissionFlag);
		csmt.setString(7,operation);	
		csmt.setString(8,quoteId!=null?""+quoteId:"");  
		csmt.setString(9,wtBrk.toUpperCase());
		csmt.setInt(10,laneNo);
		csmt.registerOutParameter(11,OracleTypes.CURSOR);
		csmt.registerOutParameter(12,OracleTypes.CURSOR);
		csmt.execute();
		singleReturn  = (ResultSet)csmt.getObject(11);  
		multipleReturn= (ResultSet)csmt.getObject(12);
		legRateDetails = new MultiQuoteFreightLegSellRates();
		while(singleReturn.next())
		{	
			legRateDetails.setServiceLevel(singleReturn.getString("SERVICELEVEL"));
			legRateDetails.setUom(singleReturn.getString("UOM"));
			legRateDetails.setDensityRatio(singleReturn.getString("DENSITY_CODE"));
			legRateDetails.setFrequency(singleReturn.getString("FREQUENCY"));
			legRateDetails.setTransitTime(singleReturn.getString("TRANSIT_TIME"));
			legRateDetails.setCurrency(singleReturn.getString("CURRENCYID"));
			legRateDetails.setCarrier(singleReturn.getString("CARRIER"));		
			legRateDetails.setSpotRatesType(singleReturn.getString("WEIGHT_BREAK"));
		}
		while(multipleReturn.next())
		{	
			if(multipleReturn.getString("SURCHARGE_ID")==null){			
				rateDetail = new double[3];//for storing upper bound,lower bound,rate respectively
				rateDetail[0] = multipleReturn.getDouble("UPPER_BOUND");//upper bound
				rateDetail[1] = multipleReturn.getDouble("LOWER_BOUND");//lower bound
				rateDetail[2] = multipleReturn.getDouble("CHARGE_RATE");//rate
				spotRateDetails.put(multipleReturn.getString("WEIGHT_BREAK_SLAB"),rateDetail);
				weightBreakSlabs.add(multipleReturn.getString("WEIGHT_BREAK_SLAB"));
				rateDescription.add(multipleReturn.getString("RATE_DESCRIPTION"));
				chargeRateIndicator.add(multipleReturn.getString("CHARGERATE_INDICATOR"));
				surChargeId.add(multipleReturn.getString("SURCHARGE_ID"));
				marginType.add(multipleReturn.getString("MARGIN_TYPE"));
				marginValue.add(multipleReturn.getDouble("MARGIN"));
				//weightBreak.add(multipleReturn.getString("WEIGHT_BREAKS"));
			}
			else if(multipleReturn.getString("SURCHARGE_ID")!=null){					   
					rateDetail = new double[3];//for storing upper bound,lower bound,rate respectively
					rateDetail[0] = multipleReturn.getDouble("UPPER_BOUND");
					rateDetail[1] = multipleReturn.getDouble("LOWER_BOUND");
					rateDetail[2] = multipleReturn.getDouble("CHARGE_RATE");
					spotRateDetails.put(multipleReturn.getString("WEIGHT_BREAK_SLAB"),rateDetail);
					weightBreakSlabs.add(multipleReturn.getString("WEIGHT_BREAK_SLAB"));
					rateDescription.add(multipleReturn.getString("RATE_DESCRIPTION").split(",")[0]);
					chargeRateIndicator.add(multipleReturn.getString("CHARGERATE_INDICATOR"));
					surChargeId.add(multipleReturn.getString("SURCHARGE_ID"));					
					currencyId.add(multipleReturn.getString("SURCHARGE_ID")+","+multipleReturn.getString("CURRENCYID"));	
					weightBreak.add(multipleReturn.getString("SURCHARGE_ID")+","+multipleReturn.getString("WEIGHT_BREAKS"));				
					marginType.add(multipleReturn.getString("MARGIN_TYPE"));
					marginValue.add(multipleReturn.getDouble("MARGIN"));
			}			
			checkedFlag.add(multipleReturn.getString("CHECKEDFLAG"));
		}
		legRateDetails.setMarginType(marginType);
		legRateDetails.setMarginValue(marginValue);
		legRateDetails.setCheckedFlag(checkedFlag);
		legRateDetails.setSpotRateDescription(rateDescription);
		legRateDetails.setWeightBreaks(weightBreakSlabs);
		legRateDetails.setWeightBreak(weightBreak);
		legRateDetails.setChargeRateIndicator(chargeRateIndicator);
		legRateDetails.setSurchargeId(surChargeId);
		legRateDetails.setSurCurrency(currencyId);
		legRateDetails.setSpotRateDetails(spotRateDetails);
	
	}
	catch(Exception e)
	{
		e.printStackTrace();
	}
	finally
	{
	    //ConnectionUtil.closeConnection(null,csmt,singleReturn);
		ConnectionUtil.closeConnection(connection,csmt,singleReturn);//@@Commented and Modified by Kameswari for the WPBN issue - on 02/02/2012
	    ConnectionUtil.closeConnection(null,null,multipleReturn);
	}
	return legRateDetails;
}
//Ended by Anil.k for Spot Rates

private String getBasisShortcut(String basis) {
	if(basis != null)
		basis = basis.replaceAll("per CBM","PerContainer");
		basis =	basis.replaceAll("Per Kilogram","Per KG");
	    basis = basis.replaceAll("Per Shipment","Per Shpt");
	    basis = basis.replaceAll("Per Weight Measurement","Per W/M");
	
	return basis;
}


}
//@@the WPBN issue-61289