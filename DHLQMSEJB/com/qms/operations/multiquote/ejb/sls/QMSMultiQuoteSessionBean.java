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
 

 * File					: QMSMultiQuoteSessionBean
 * @author				: Govind
 * @date				: 
 *CR-                   :CR-DHLQMS-CR-219979&80


 *	This Controller is used to control the flow in the quote module
 */






package com.qms.operations.multiquote.ejb.sls;


import com.foursoft.esupply.common.bean.ESupplyGlobalParameters;
import com.foursoft.esupply.common.java.LookUpBean;
import com.foursoft.esupply.common.util.ConnectionUtil;
import com.foursoft.esupply.common.util.ESupplyDateUtility;
//import com.foursoft.esupply.common.util.Logger;
import com.qms.operations.quote.dob.QuoteAttachmentDOB;
import com.qms.operations.quote.dob.QuoteFinalDOB;
import com.qms.operations.quote.dob.QuoteMasterDOB;
import com.qms.operations.multiquote.dob.MultiQuoteFreightLegSellRates;

import java.sql.Blob;
import org.apache.log4j.Logger;
import com.foursoft.etrans.common.util.java.OperationsImpl;
import com.qms.operations.costing.dob.CostingHDRDOB;
import com.qms.operations.costing.dob.CostingMasterDOB;
import com.qms.operations.multiquote.dao.QMSMultiQuoteDAO;
import com.qms.operations.multiquote.dob.MultiQuoteAttachmentDOB;
import com.qms.operations.multiquote.dob.MultiQuoteChargeInfo;
import com.qms.operations.multiquote.dob.MultiQuoteFreightLegSellRates;
import com.qms.operations.multiquote.dob.MultiQuoteFinalDOB;
import com.qms.operations.multiquote.dob.MultiQuoteFlagsDOB;
import com.qms.operations.multiquote.dob.MultiQuoteHeader;
import com.qms.operations.multiquote.dob.MultiQuoteMasterDOB;
import com.qms.operations.multiquote.dob.MultiQuoteTiedCustomerInfo;
import com.qms.operations.multiquote.dob.MultiQuoteFreightRSRCSRDOB;
import com.qms.operations.multiquote.dob.MultiQuoteChargeInfo;
import com.qms.operations.multiquote.dob.MultiQuoteCharges;
import com.qms.operations.multiquote.ejb.bmp.QMSMultiQuoteEntityLocal;
import com.qms.operations.multiquote.ejb.bmp.QMSMultiQuoteEntityLocalHome;
import com.qms.reports.java.ReportDetailsDOB;
import com.qms.reports.java.UpdatedQuotesFinalDOB;
import com.qms.reports.java.UpdatedQuotesReportDOB;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import javax.ejb.EJBException;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import java.util.ArrayList;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Map.Entry;

import oracle.jdbc.driver.OracleTypes;
import oracle.jdbc.driver.OracleCallableStatement;

public class QMSMultiQuoteSessionBean implements SessionBean 
{
  private InitialContext  initialContext	= null; 
	private DataSource		  dataSource		  = null;
  //private LookUpBean      lookUpBean      =   null;
	private static String   FILE_NAME				= "QMSMultiQuoteSessionBean.java";
  private static Logger logger = null;
  
  
  public QMSMultiQuoteSessionBean()
  {
    logger  = Logger.getLogger(QMSMultiQuoteSessionBean.class);
  }
  public void ejbCreate()
  {
  }

  public void ejbActivate()
  {
  }

  public void ejbPassivate()
  {
  }

  public void ejbRemove()
  {
  }

  public void setSessionContext(SessionContext ctx)
  {
  }
  
  //Serialization of BeanObject is to be done here.....//
  private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException
  {
    //write non-serializable attributes here
    out.defaultWriteObject();
  }

  private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException
  {
    //read non-serializable attributes here
    in.defaultReadObject();
  }
  
  private void getDataSource() throws EJBException
  {
    try
    {
      initialContext = new InitialContext();
      dataSource = (DataSource)initialContext.lookup("java:comp/env/jdbc/DB");
    }
    catch( Exception e )
    {
      e.printStackTrace();
      //Logger.error(FILE_NAME,"Exception in getDataSource() method of QMSQuoteSessionBean.java: "+e.toString());
      logger.error(FILE_NAME+"Exception in getDataSource() method of QMSMultiQuoteSessionBean.java: "+e.toString());
    }
  }
  
  private Connection  getConnection() throws EJBException
  {
    Connection con = null;
    try
    {
      if(dataSource== null)
        getDataSource();
      con = dataSource.getConnection();
    }
    catch( Exception e )
    {
      e.printStackTrace();
      //Logger.error(FILE_NAME,"Exception in getConnectin() method of QMSQuoteSessionBean.java: "+e.toString());
      logger.error(FILE_NAME+"Exception in getConnectin() method of QMSMultiQuoteSessionBean.java: "+e.toString());
    }
    return con;
  
  }
  
 /**
	 * This method helps in validating the quote master details 
   * 
	 * @param masterDOB 	an QuoteMasterDOB Object that contains all the Quote Master information
	 * 
	 * @exception EJBException 
	 */
  public StringBuffer validateQuoteMaster(MultiQuoteFinalDOB finalDOB) throws	EJBException
  {
    Connection        connection      = null;
    PreparedStatement psmt            = null;
    //Statement         stmt            = null; //Commented By RajKumari on 27-10-2008 for Connection Leakages.
    ResultSet         rs              = null;
    StringBuffer      errors          = null;
  //  StringBuffer      terminalQry     = new StringBuffer();
    String            shipperAlpha    = null;
    String            consigneeAlpha  = null;
    String            shipperZipCode  = null;
    String            consigneeZipCode= null;
    String			  consigneeConsoleType = null;
    String			  shipperConsoleType   = null;	
    String            whereCondition  = "";
    MultiQuoteMasterDOB    masterDOB       = null;  
   // MultiQuoteFreightLegSellRates  legDOB  = null;
   // ArrayList         legDetails      = null;
  //  String            currency        = null;
  //  String            error           = null;
    String           originCountryId  = null;
    String           destCountryId    = null;
   // StringBuffer      terminalQrySales     = new StringBuffer();
    StringBuffer      terminalQryCustQuote     = new StringBuffer();//@@Added by subrahmanyam for the CR_Enhancement_167669 on 26/may/09
    PreparedStatement psmtSub            = null;
    ResultSet         rsSub              = null;
	String fromZipCode 	=	"";
	String toZipCode	=	"";
    
    try
    {
     
      masterDOB     =   finalDOB.getMasterDOB();
    //  legDetails    =   finalDOB.getLegDetails();
    //  int legSize   =   legDetails.size();
      
      if("H".equalsIgnoreCase(masterDOB.getAccessLevel()))
      {
       
        terminalQryCustQuote.append( " (SELECT terminalid term_id FROM FS_FR_TERMINALMASTER)");//@@ Added by subrahmanyam for the CR_Enhancement_167669 on 26/May/09

      }
      else
      {
       
      
             if("A".equalsIgnoreCase(masterDOB.getAccessLevel()))
             {
               terminalQryCustQuote.append( "(SELECT parent_terminal_id term_id FROM FS_FR_TERMINAL_REGN CONNECT BY child_terminal_id = PRIOR parent_terminal_id START WITH child_terminal_id = ?")
                   .append( " UNION ")
                   .append( " SELECT terminalid term_id FROM FS_FR_TERMINALMASTER WHERE oper_admin_flag = 'H' ")
                   .append( " UNION ")
                   .append( " SELECT ? term_id FROM DUAL ")
                   .append( " UNION ")
                   .append( " SELECT child_terminal_id term_id FROM FS_FR_TERMINAL_REGN CONNECT BY PRIOR child_terminal_id = parent_terminal_id START WITH parent_terminal_id = ?)");
             
             }
             else
             {
               terminalQryCustQuote.append( "(SELECT parent_terminal_id term_id FROM FS_FR_TERMINAL_REGN CONNECT BY child_terminal_id = PRIOR parent_terminal_id START WITH child_terminal_id = ?")
                   .append( " UNION ")
                   .append( " SELECT terminalid term_id FROM FS_FR_TERMINALMASTER WHERE oper_admin_flag = 'H' ")
                   .append( " UNION ")
                   .append( " SELECT ? term_id FROM DUAL ")
                   .append( " UNION ")
                   .append( " SELECT child_terminal_id term_id FROM FS_FR_TERMINAL_REGN CONNECT BY PRIOR child_terminal_id = parent_terminal_id START WITH parent_terminal_id = ? ")
                    .append(" union ")
                    .append("  select child_terminal_id from fs_fr_terminal_regn  ")
                   .append(" where parent_terminal_id in(select parent_terminal_id from fs_fr_terminal_regn fr1")
                  .append(" where fr1.child_terminal_id= ? ))");
            
             }
   
}
          
      errors  = new StringBuffer();
      connection  = this.getConnection();
      int  shipmentMode  = masterDOB.getShipmentMode();
      String  shipment      = "";
      String  shipment1     = "";
      // String  shipmModeStr  = "";
      //  String  whereclause = null;
     //  String  inClause = null;
      
      if(shipmentMode==4){
        shipment = " AND SHIPMENTMODE IN (4,5,6,7) ";
        shipment1 = " AND SHIPMENT_MODE IN (4,5,6,7) ";
      }else if(shipmentMode==1){
        shipment = " AND SHIPMENTMODE IN (1,3,5,7) ";
        shipment1 = " AND SHIPMENT_MODE IN (1,3,5,7) ";
      }else if(shipmentMode==2){
        shipment = " AND SHIPMENTMODE IN (2,3,6,7) ";
        shipment1 = " AND SHIPMENT_MODE IN (2,3,6,7) ";
        consigneeConsoleType = ("LIST".equalsIgnoreCase(masterDOB.getMultiquoteweightBrake()))?"FCL":"LCL";
        shipperConsoleType = ("LIST".equalsIgnoreCase(masterDOB.getMultiquoteweightBrake()))?"FCL":"LCL";
      }
      
      //if(masterDOB.getPreQuoteId()>0) //Commented by subrahmanyam for the enhancement #146971 on 03/12/2008
      if(masterDOB.getPreQuoteId()!=null) //Commented by subrahmanyam for the enhancement #146971 on 03/12/2008
      {
          psmt  = connection.prepareStatement("SELECT QUOTE_ID FROM QMS_QUOTE_MASTER WHERE QUOTE_ID=? AND TERMINAL_ID IN "+terminalQryCustQuote.toString());
          psmt.setString(1,masterDOB.getPreQuoteId());  
        if(!"H".equalsIgnoreCase(masterDOB.getAccessLevel()))
        {
          if("A".equalsIgnoreCase(masterDOB.getAccessLevel()))
          {
            psmt.setString(2,masterDOB.getTerminalId());
            psmt.setString(3,masterDOB.getTerminalId());
            psmt.setString(4,masterDOB.getTerminalId());
          }
          else
          {
            psmt.setString(2,masterDOB.getTerminalId());
            psmt.setString(3,masterDOB.getTerminalId());
            psmt.setString(4,masterDOB.getTerminalId());
            psmt.setString(5,masterDOB.getTerminalId());
          }

        }
        rs  = psmt.executeQuery();
        if(!rs.next())
          errors.append("Previous Quote Id is Invalid Or Does not exist.<br>");
          
        if(rs!=null)
          rs.close();
        if(psmt!=null)
          psmt.close();
        
      }
      
       
int no_of_lanes = masterDOB.getOriginLocation().length;
for(int lane =0;lane<no_of_lanes;lane++)
{
	String s = "SELECT LOCATIONID,COUNTRYID FROM  FS_FR_LOCATIONMASTER WHERE LOCATIONID =? AND (INVALIDATE ='F' OR INVALIDATE IS NULL) "+shipment;
	psmt  = connection.prepareStatement(s);
	if(masterDOB.getOriginLocation()[lane]!=null && (masterDOB.getOriginLocation()[lane].trim()).length()!=0)
    {	
		psmt.clearParameters();
        psmt.setString(1,masterDOB.getOriginLocation()[lane]);
        rs  = psmt.executeQuery();
	    if(rs.next())
	    	originCountryId =rs.getString("COUNTRYID");
       psmt.close();
       rs.close();
    }
	 if(masterDOB.getDestLocation()[lane]!=null && (masterDOB.getDestLocation()[lane].trim()).length()!=0)
     {
		 psmt  = connection.prepareStatement(s); 
	  psmt.setString(1,masterDOB.getDestLocation()[lane]);
       rs  = psmt.executeQuery();
       if(rs.next())
    	   destCountryId = rs.getString("COUNTRYID");
       psmt.close();
       rs.close();
     }
	
     if(masterDOB.getShipperZipCode()[lane]!=null && (masterDOB.getShipperZipCode()[lane].trim()).length()!=0)
      {
        if(masterDOB.getShipperZones()!=null && masterDOB.getShipperZones()[lane].indexOf(",") > -1)
            errors.append("Please Select a Single Shipper Zone Code Matching the Zip Code "+masterDOB.getShipperZipCode()[lane]+"<br>");
            
        shipperZipCode  = masterDOB.getShipperZipCode()[lane];
          if(shipperZipCode.indexOf("-")!=-1)
        {
          shipperAlpha    =  shipperZipCode.substring(0,shipperZipCode.indexOf("-"));
          shipperZipCode  =  shipperZipCode.substring((shipperZipCode.indexOf("-")+1),shipperZipCode.trim().length());
        }
       if(shipperAlpha != null)
            whereCondition  = " AND D.ALPHANUMERIC = ? ";
    
      
      
      
     if("CA".equalsIgnoreCase(masterDOB.getOriginLocation()[lane]))
       {
     
          psmt  = connection.prepareStatement(" SELECT D.ZONE_CODE "+ 
                                          " FROM QMS_ZONE_CODE_MASTER_CA M, QMS_ZONE_CODE_DTL_CA D "+
                                          " WHERE D.ZONE_CODE=M.ZONE_CODE AND M.LOCATION_ID= ? AND M.SHIPMENT_MODE = ? AND NVL(M.CONSOLE_TYPE,'~')= ? "+ 
                                          " AND D.INVALIDATE='F' AND  ? IN( D.FROM_ZIPCODE ,   D.TO_ZIPCODE )");//@@ Modified for wpbn 185101 on 01-10-09
       }
       else
       {
          psmt  = connection.prepareStatement(" SELECT D.ZONE_CODE "+ 
                                  " FROM QMS_ZONE_CODE_MASTER M, QMS_ZONE_CODE_DTL D "+
                                  " WHERE D.ZONE_CODE=M.ZONE_CODE AND M.ORIGIN_LOCATION= ? AND M.SHIPMENT_MODE = ? AND NVL(M.CONSOLE_TYPE,'~')= ? "+ 
                                  " AND D.INVALIDATE='F' AND TO_NUMBER (?) BETWEEN D.FROM_ZIPCODE AND D.TO_ZIPCODE "+whereCondition);
       }

        psmt.setString(1,masterDOB.getOriginLocation()[lane]);
        psmt.setString(2,Integer.toString(masterDOB.getShipmentMode()));
        if("1".equalsIgnoreCase(Integer.toString(masterDOB.getShipmentMode())))
            psmt.setString(3,"~");
        else
            psmt.setString(3,shipperConsoleType);
        
         if("CA".equalsIgnoreCase(originCountryId))
       {
           psmt.setString(4,shipperZipCode);
          
       }
       else
       {
        psmt.setString(4,shipperZipCode);
        
         if(shipperAlpha != null)
            psmt.setString(5,shipperAlpha);
       }
         rs  = psmt.executeQuery();
        if(!rs.next())
        {
        	if("CA".equalsIgnoreCase(originCountryId) && shipperZipCode.length()>3)
        	{
        		String shipperZipCodeSub	= shipperZipCode.substring(0,3);
        		shipperZipCodeSub	= 	shipperZipCodeSub+'%';
        		boolean zipFlag		=	false;
        		psmtSub		=	connection.prepareStatement(" SELECT D.ZONE_CODE,D.FROM_ZIPCODE,D.TO_ZIPCODE "+
        											" FROM QMS_ZONE_CODE_MASTER_CA M, QMS_ZONE_CODE_DTL_CA D "+
        											" WHERE D.ZONE_CODE = M.ZONE_CODE    AND M.LOCATION_ID = ? "+
        											" AND M.SHIPMENT_MODE = ?  AND NVL(M.CONSOLE_TYPE, '~') = ? "+
        											" AND D.INVALIDATE = 'F'  AND D.ZONE=? AND D.FROM_ZIPCODE LIKE ? ");
        	
        		psmtSub.setString(1,masterDOB.getOriginLocation()[lane]);
        		psmtSub.setString(2,masterDOB.getShipperMode());
        	        if("1".equalsIgnoreCase(masterDOB.getShipperMode()))
        	        	psmtSub.setString(3,"~");
        	        else
        	        	psmtSub.setString(3,shipperConsoleType);
        	       
        	        psmtSub.setString(4,masterDOB.getShipperZones()[lane].trim());
        	        psmtSub.setString(5,shipperZipCodeSub);
        	        rsSub  = psmtSub.executeQuery();
        	        while(rsSub.next())
        	        {
        	        	fromZipCode	=	rsSub.getString("FROM_ZIPCODE");
        	        	toZipCode	=	rsSub.getString("TO_ZIPCODE");
        	        	
        	        	zipFlag	=	getZipCode(fromZipCode,toZipCode,shipperZipCode);
        	        	if(zipFlag)
        	        		break;
        	        	else{
        	        		fromZipCode	=	"";
        	        		toZipCode	=	"";
        	        	}
        	        }
        	        if(!zipFlag)
        	        	errors.append("Shipper Zip Code is not defined in the Zone code master or an invalid one for the Origin Location & selected Shipper Mode.<br>");
        	}
        	else
        		errors.append("Shipper Zip Code is not defined in the Zone code master or an invalid one for the Origin Location & selected Shipper Mode.<br>");
        
        }
        else if(masterDOB.getShipperZones()[lane]!=null && masterDOB.getShipperZones()[lane].indexOf(",")==-1)
        {
          if(rs!=null)
            rs.close();
          if(psmt!=null)
            psmt.close();
          if(rsSub!=null)
        	  rsSub.close();
          if(psmtSub!=null)
        	  psmtSub.close();
               
          if("CA".equalsIgnoreCase(originCountryId))
       {
           psmt  = connection.prepareStatement(" SELECT D.ZONE_CODE "+ 
                                              " FROM QMS_ZONE_CODE_MASTER_CA M, QMS_ZONE_CODE_DTL_CA D "+
                                              " WHERE D.ZONE_CODE=M.ZONE_CODE AND M.LOCATION_ID= ? AND M.SHIPMENT_MODE = ? AND NVL(M.CONSOLE_TYPE,'~')= ?"+ 
                                         " AND D.INVALIDATE='F' AND  ? IN ( D.FROM_ZIPCODE , D.TO_ZIPCODE)  AND D.ZONE= ?");//@@ Modified for wpbn 185101 on 01-10-09
       }
       else
       {
         psmt  = connection.prepareStatement(" SELECT D.ZONE_CODE "+ 
                                              " FROM QMS_ZONE_CODE_MASTER M, QMS_ZONE_CODE_DTL D "+
                                              " WHERE D.ZONE_CODE=M.ZONE_CODE AND M.ORIGIN_LOCATION= ? AND M.SHIPMENT_MODE = ? AND NVL(M.CONSOLE_TYPE,'~')= ?"+ 
                                              " AND D.INVALIDATE='F' AND TO_NUMBER (?) BETWEEN D.FROM_ZIPCODE AND D.TO_ZIPCODE AND D.ZONE= ? "+whereCondition);
       }
    
          
          psmt.setString(1,masterDOB.getOriginLocation()[lane]);
          //Modified by Rakesh for CR:231217
          // psmt.setString(2,masterDOB.getShipperMode());       
          psmt.setString(2,Integer.toString(masterDOB.getShipmentMode()));
        //Modified by Rakesh for CR:231217
          //if("1".equalsIgnoreCase(masterDOB.getShipperMode()))
          if(masterDOB.getShipmentMode()==1)
              psmt.setString(3,"~");
          else
              psmt.setString(3,shipperConsoleType);
        
         if("CA".equalsIgnoreCase(originCountryId))
       {
           psmt.setString(4,shipperZipCode);
           psmt.setString(5,masterDOB.getShipperZones()[lane].trim());
       }
       else
       {
          psmt.setString(4,shipperZipCode);
          psmt.setString(5,masterDOB.getShipperZones()[lane].trim());
          if(shipperAlpha != null)
            psmt.setString(6,shipperAlpha);
       }  
          rs  = psmt.executeQuery();
          if(!rs.next())
          {
        	  if("CA".equalsIgnoreCase(originCountryId))
        	  {
        		  if("".equalsIgnoreCase(fromZipCode))
        			  errors.append("The Shipper Zip Code "+masterDOB.getShipperZipCode()[lane]+" Does Not Match the Shipper Zone Code "+masterDOB.getShipperZones()[lane]+".<br>");
        	  }
        	  else
        		  errors.append("The Shipper Zip Code "+masterDOB.getShipperZipCode()[lane]+" Does Not Match the Shipper Zone Code "+masterDOB.getShipperZones()[lane]+".<br>");
          }
        }
       

           if(whereCondition.trim().length()==0)
    {
      if(rs!=null)
        rs.close();
      if(psmt!=null)
        psmt.close();

        if("CA".equalsIgnoreCase(originCountryId))
        {
          psmt  = connection.prepareStatement("  SELECT COUNT(*) COUNT  FROM QMS_ZONE_CODE_MASTER_CA M, QMS_ZONE_CODE_DTL_CA D "+
                                              " WHERE D.ZONE_CODE = M.ZONE_CODE  AND M.LOCATION_ID = ? AND ? "+
                                              " IN (D.FROM_ZIPCODE,D.TO_ZIPCODE)  AND M.SHIPMENT_MODE = ?  AND D.ZONE IN (?) AND NVL(M.CONSOLE_TYPE, '~') = ? ") ;//@@ Modified for wpbn 185101 on 01-10-09

        }
        else
        {
          psmt  = connection.prepareStatement("SELECT COUNT(*) COUNT FROM QMS_ZONE_CODE_MASTER M,QMS_ZONE_CODE_DTL D  "+
                            "WHERE D.ZONE_CODE=M.ZONE_CODE AND M.ORIGIN_LOCATION=? AND TO_NUMBER(?) BETWEEN "+
                            "D.FROM_ZIPCODE AND D.TO_ZIPCODE AND M.SHIPMENT_MODE = ? AND D.ZONE IN (?) AND NVL(M.CONSOLE_TYPE, '~') = ? ") ;

        }
		
                            
          psmt.setString(1,masterDOB.getOriginLocation()[lane]);
          psmt.setString(2,shipperZipCode);
          psmt.setString(3,Integer.toString(masterDOB.getShipmentMode()));          
          psmt.setString(4,masterDOB.getShipperZones()[lane]);   
          if("1".equalsIgnoreCase(masterDOB.getShipperMode()))
              psmt.setString(5,"~");
          else 
              psmt.setString(5,shipperConsoleType);
          
          rs    =   psmt.executeQuery();
          
          while(rs.next())
          {
          if(rs.getInt(1)>1)
          {
       errors.append("More Than One Alpha Numeric is there for The Shipper Zip Code "+masterDOB.getShipperZipCode()[lane]+"Enter Valid Alpha Numeric.<br>");
          }
          }
      }

        if(rs!=null)
          rs.close();
        if(psmt!=null)
          psmt.close();
      }
      if(masterDOB.getConsigneeZipCode()[lane]!=null && (masterDOB.getConsigneeZipCode()[lane].trim()).length()!=0)
      {
        if(masterDOB.getConsigneeZones()!=null && masterDOB.getConsigneeZones()[lane].indexOf(",") > -1)
            errors.append("Please Select a Single Consignee Zone Code Matching the Zip Code "+masterDOB.getConsigneeZipCode()[lane]+"<br>");
        whereCondition    = "";
        consigneeZipCode  = masterDOB.getConsigneeZipCode()[lane];
          if(consigneeZipCode.indexOf("-")!=-1)
        {
          consigneeAlpha   =  consigneeZipCode.substring(0,consigneeZipCode.indexOf("-"));
          consigneeZipCode =  consigneeZipCode.substring((consigneeZipCode.indexOf("-")+1),consigneeZipCode.trim().length());
        }

        if(consigneeAlpha != null)
            whereCondition  = " AND D.ALPHANUMERIC = ?";
      
        if("CA".equalsIgnoreCase(destCountryId))
       {
     
          psmt  = connection.prepareStatement(" SELECT D.ZONE_CODE "+ 
                                          " FROM QMS_ZONE_CODE_MASTER_CA M, QMS_ZONE_CODE_DTL_CA D "+
                                          " WHERE D.ZONE_CODE=M.ZONE_CODE AND M.LOCATION_ID= ? AND M.SHIPMENT_MODE = ? AND NVL(M.CONSOLE_TYPE,'~')= ? "+ 
                                          " AND D.INVALIDATE='F' AND  ? IN( D.FROM_ZIPCODE ,  D.TO_ZIPCODE )");//@@ Modified for wpbn 185101 on 01-10-09
       }
       else
       {
          psmt  = connection.prepareStatement(" SELECT D.ZONE_CODE "+ 
                                  " FROM QMS_ZONE_CODE_MASTER M, QMS_ZONE_CODE_DTL D "+
                                  " WHERE D.ZONE_CODE=M.ZONE_CODE AND M.ORIGIN_LOCATION= ? AND M.SHIPMENT_MODE = ? AND NVL(M.CONSOLE_TYPE,'~')= ? "+ 
                                  " AND D.INVALIDATE='F' AND TO_NUMBER (?) BETWEEN D.FROM_ZIPCODE AND D.TO_ZIPCODE "+whereCondition);
       }
       psmt.setString(1,masterDOB.getDestLocation()[lane]);
        psmt.setString(2,Integer.toString(masterDOB.getShipmentMode()));
        if("1".equalsIgnoreCase(Integer.toString(masterDOB.getShipmentMode())))
            psmt.setString(3,"~");
        else
            psmt.setString(3,consigneeConsoleType);
          if("CA".equalsIgnoreCase(destCountryId))
       {
            psmt.setString(4,consigneeZipCode);     
       }
       else
       {
        psmt.setString(4,consigneeZipCode);        
        
        if(consigneeAlpha != null)
            psmt.setString(5,consigneeAlpha);
       }     
        rs  = psmt.executeQuery();
             
        if(!rs.next())
        {
          
        	if("CA".equalsIgnoreCase(destCountryId) && consigneeZipCode.length()>3)
        	{
             		String consigneeZipCodeSub	= consigneeZipCode.substring(0,3);
             		consigneeZipCodeSub	= 	consigneeZipCodeSub+'%';
            		boolean zipFlag		=	false;
            		psmtSub		=	connection.prepareStatement(" SELECT D.ZONE_CODE,D.FROM_ZIPCODE,D.TO_ZIPCODE "+
            											" FROM QMS_ZONE_CODE_MASTER_CA M, QMS_ZONE_CODE_DTL_CA D "+
            											" WHERE D.ZONE_CODE = M.ZONE_CODE    AND M.LOCATION_ID = ? "+
            											" AND M.SHIPMENT_MODE = ?  AND NVL(M.CONSOLE_TYPE, '~') = ? "+
            											" AND D.INVALIDATE = 'F'  AND D.ZONE=? AND D.FROM_ZIPCODE LIKE ? ");
            	
            		psmtSub.setString(1,masterDOB.getDestLocation()[lane]);
            		psmtSub.setString(2,Integer.toString(masterDOB.getShipmentMode()));
            	        if("1".equalsIgnoreCase(Integer.toString(masterDOB.getShipmentMode())))
            	        	psmtSub.setString(3,"~");
            	        else
            	        	psmtSub.setString(3,consigneeConsoleType);
            	       
            	        psmtSub.setString(4,masterDOB.getConsigneeZones()[lane].trim());
            	        psmtSub.setString(5,consigneeZipCodeSub);
            	        rsSub  = psmtSub.executeQuery();
            	        while(rsSub.next())
            	        {
            	        	fromZipCode	=	rsSub.getString("FROM_ZIPCODE");
            	        	toZipCode	=	rsSub.getString("TO_ZIPCODE");
            	        	
            	        	zipFlag	=	getZipCode(fromZipCode,toZipCode,consigneeZipCode);
            	        	if(zipFlag)
            	        		break;
            	        	else{
            	        		fromZipCode	=	"";
            	        		toZipCode	=	"";
            	        	}
            	        }
            	        if(!zipFlag)
            	        	errors.append("Consignee Zip Code is not defined in the Zone code master or an invalid one for the Destination Location & selected Consignee Mode.<br>");
        	}
        	else
                errors.append("Consignee Zip Code is not defined in the Zone code master or an invalid one for the Destination Location & selected Consignee Mode.<br>");
            
        }
        //@@ Ended by subrahmanyam for the wpbn id: 195650 on 29/Jan/10        
        else if(masterDOB.getConsigneeZones()[lane]!=null && masterDOB.getConsigneeZones()[lane].indexOf(",")==-1)
        {
          if(rs!=null) 
            rs.close();
          if(psmt!=null)
            psmt.close();
          if(rsSub!=null) 
        	  rsSub.close();
            if(psmtSub!=null)
            	psmtSub.close();
            

        if("CA".equalsIgnoreCase(destCountryId))
       {
           psmt  = connection.prepareStatement(" SELECT D.ZONE_CODE "+ 
                                              " FROM QMS_ZONE_CODE_MASTER_CA M, QMS_ZONE_CODE_DTL_CA D "+
                                              " WHERE D.ZONE_CODE=M.ZONE_CODE AND M.LOCATION_ID= ? AND M.SHIPMENT_MODE = ? AND NVL(M.CONSOLE_TYPE,'~')= ?"+ 
                                         " AND D.INVALIDATE='F' AND  ? IN( D.FROM_ZIPCODE , D.TO_ZIPCODE)  AND D.ZONE= ?");//@@ Modified for wpbn 185101 on 01-10-09
       }
       else
       {
         psmt  = connection.prepareStatement(" SELECT D.ZONE_CODE "+ 
                                              " FROM QMS_ZONE_CODE_MASTER M, QMS_ZONE_CODE_DTL D "+
                                              " WHERE D.ZONE_CODE=M.ZONE_CODE AND M.ORIGIN_LOCATION= ? AND M.SHIPMENT_MODE = ? AND NVL(M.CONSOLE_TYPE,'~')= ?"+ 
                                              " AND D.INVALIDATE='F' AND TO_NUMBER (?) BETWEEN D.FROM_ZIPCODE AND D.TO_ZIPCODE AND D.ZONE= ? "+whereCondition);
       }  
          psmt.setString(1,masterDOB.getDestLocation()[lane]);
          psmt.setString(2,Integer.toString(masterDOB.getShipmentMode()));
          if("1".equalsIgnoreCase(Integer.toString(masterDOB.getShipmentMode())))
            psmt.setString(3,"~");
          else
            psmt.setString(3,consigneeConsoleType);    
           if("CA".equalsIgnoreCase(destCountryId))
        {
            psmt.setString(4,consigneeZipCode);
             psmt.setString(5,masterDOB.getConsigneeZones()[lane].trim()); 
       }
       else
       {
        psmt.setString(4,consigneeZipCode);
          psmt.setString(5,masterDOB.getConsigneeZones()[lane].trim());
          
          if(consigneeAlpha != null)
            psmt.setString(6,consigneeAlpha);
          
       }
          rs  = psmt.executeQuery();

          if(!rs.next())
          {
        	  if("CA".equalsIgnoreCase(destCountryId))
        	  {
        		  if("".equalsIgnoreCase(fromZipCode))
        			  errors.append("The Consignee Zip Code "+masterDOB.getConsigneeZipCode()[lane]+" Does Not Match the Consignee Zone Code "+masterDOB.getConsigneeZones()[lane]+".<br>");
        	  }
        	  else
        		  errors.append("The Consignee Zip Code "+masterDOB.getConsigneeZipCode()[lane]+" Does Not Match the Consignee Zone Code "+masterDOB.getConsigneeZones()[lane]+".<br>");
          }
     //@@ Ended by subrahmanyam for the wpbn id: 195650 on 29/Jan/10
        }
        //Added on 0406
       if(whereCondition.trim().length()==0){
        if(rs!=null)
        rs.close();
      if(psmt!=null)
        psmt.close();
        
 //added for 185101
         if("CA".equalsIgnoreCase(destCountryId))
        {
          psmt  = connection.prepareStatement("  SELECT COUNT(*) COUNT  FROM QMS_ZONE_CODE_MASTER_CA M, QMS_ZONE_CODE_DTL_CA D "+
                                              " WHERE D.ZONE_CODE = M.ZONE_CODE  AND M.LOCATION_ID = ? AND ? "+
                                              " IN (D.FROM_ZIPCODE,D.TO_ZIPCODE)  AND M.SHIPMENT_MODE = ?  AND D.ZONE IN (?) AND NVL(M.CONSOLE_TYPE, '~') = ? ") ;//@@ Modified for wpbn 185101 on 01-10-09

        }
        else
        {
          psmt  = connection.prepareStatement("SELECT COUNT(*) COUNT FROM QMS_ZONE_CODE_MASTER M,QMS_ZONE_CODE_DTL D  "+
                            "WHERE D.ZONE_CODE=M.ZONE_CODE AND M.ORIGIN_LOCATION=? AND TO_NUMBER(?) BETWEEN "+
                            "D.FROM_ZIPCODE AND D.TO_ZIPCODE AND M.SHIPMENT_MODE = ? AND D.ZONE IN (?) AND NVL(M.CONSOLE_TYPE, '~') = ? ") ;

        }
		                
          psmt.setString(1,masterDOB.getDestLocation()[lane]);
          psmt.setString(2,consigneeZipCode);
          psmt.setString(3,Integer.toString(masterDOB.getShipmentMode()));          
          psmt.setString(4,masterDOB.getConsigneeZones()[lane]);   
          if("1".equalsIgnoreCase(Integer.toString(masterDOB.getShipmentMode())))
              psmt.setString(5,"~");
          else 
              psmt.setString(5,consigneeConsoleType);
          
          rs    =   psmt.executeQuery();
          
          while(rs.next())
          {
          if(rs.getInt(1)>1)
          {
       errors.append("More Than One Alpha Numeric is there for The Consignee Zip Code "+masterDOB.getConsigneeZipCode()[lane]+"Enter Valid Alpha Numeric.<br>");
          }
          }
      }
        //Added end
      }
      
      if(rs!=null)
        rs.close();
      if(psmt!=null)
        psmt.close();
        
      if(masterDOB.getShipperZones()[lane]!=null && (masterDOB.getShipperZones()[lane].trim()).length()!=0)
      {
        StringBuffer shZones        =  new StringBuffer();
        int          shZonesLength  =  0;
        String[]     shZoneArray    =  null;
        
        if(masterDOB.getShipperZones()[lane].split(",").length > 0)
        {
           shZonesLength  =  masterDOB.getShipperZones()[lane].split(",").length;
           shZoneArray    =  masterDOB.getShipperZones()[lane].split(",");
            
            for(int k=0;k<shZonesLength;k++)
            {
                if(k==(shZonesLength-1))
                    shZones.append("?");
                else
                    shZones.append("?,");
            }
        }
        
       /* psmt = connection.prepareStatement(" SELECT COUNT(DISTINCT D.ZONE) NO_ROWS "+ 
                                           " FROM QMS_ZONE_CODE_MASTER M, QMS_ZONE_CODE_DTL D "+
                                           " WHERE D.ZONE_CODE=M.ZONE_CODE AND M.ORIGIN_LOCATION=? AND M.SHIPMENT_MODE = ? "+ 
                                           " AND NVL(M.CONSOLE_TYPE,'~')= ? AND D.INVALIDATE='F' AND D.ZONE IN ("+shZones+")");*/
         if("CA".equalsIgnoreCase(originCountryId))
       {
          psmt = connection.prepareStatement(" SELECT COUNT(DISTINCT D.ZONE) NO_ROWS "+ 
                                           " FROM QMS_ZONE_CODE_MASTER_CA M, QMS_ZONE_CODE_DTL_CA D "+
                                           " WHERE D.ZONE_CODE=M.ZONE_CODE AND M.LOCATION_ID=? AND M.SHIPMENT_MODE = ? "+ 
                                           " AND NVL(M.CONSOLE_TYPE,'~')= ? AND D.INVALIDATE='F' AND D.ZONE IN ("+shZones+")");
       }
       else
       {
         psmt = connection.prepareStatement(" SELECT COUNT(DISTINCT D.ZONE) NO_ROWS "+ 
                                       " FROM QMS_ZONE_CODE_MASTER M, QMS_ZONE_CODE_DTL D "+
                                       " WHERE D.ZONE_CODE=M.ZONE_CODE AND M.ORIGIN_LOCATION=? AND M.SHIPMENT_MODE = ? "+ 
                                       " AND NVL(M.CONSOLE_TYPE,'~')= ? AND D.INVALIDATE='F' AND D.ZONE IN ("+shZones+")");

       }
        psmt.setString(1,masterDOB.getOriginLocation()[lane]);
        psmt.setString(2,Integer.toString(masterDOB.getShipmentMode()));
        if("1".equalsIgnoreCase(Integer.toString(masterDOB.getShipmentMode())))
            psmt.setString(3,"~");
        else
            psmt.setString(3,shipperConsoleType);
        
        if(shZoneArray!=null && shZoneArray.length>0)
        {
        	int shZoneArrLen	=	shZoneArray.length;
          for(int k=0;k<shZoneArrLen;k++)
              psmt.setString(k+4,shZoneArray[k]);
        }
        
        rs  = psmt.executeQuery();                                
        
         
        while(rs.next())
        {
          if(rs.getInt("NO_ROWS")!=shZonesLength)
            errors.append("Shipper Zone is not defined in the Zone code master or an invalid one for the Origin Location & selected Shipper Mode.<br>");
        }
        if(rs!=null)
          rs.close();
        if(psmt!=null)
          psmt.close();
      }
      if(masterDOB.getConsigneeZones()[lane]!=null && (masterDOB.getConsigneeZones()[lane].trim()).length()!=0)
      {
        
        StringBuffer consigneeZones       =  new StringBuffer();
        int          consigneeZonesLength =  0;
        String[]     consigneeZoneArray   =  null;
        
        if(masterDOB.getConsigneeZones()[lane].split(",").length > 0)
        {
            consigneeZonesLength  =  masterDOB.getConsigneeZones()[lane].split(",").length;
            consigneeZoneArray    =  masterDOB.getConsigneeZones()[lane].split(",");
            
            for(int k=0;k<consigneeZonesLength;k++)
            {
                if(k==(consigneeZonesLength-1))
                    consigneeZones.append("?");
                else
                    consigneeZones.append("?,");
            }
        }
        
       
         if("CA".equalsIgnoreCase(destCountryId))
       {
           psmt  = connection.prepareStatement(" SELECT COUNT(DISTINCT D.ZONE) NO_ROWS "+ 
                                            " FROM QMS_ZONE_CODE_MASTER_CA M, QMS_ZONE_CODE_DTL_CA D "+
                                            " WHERE D.ZONE_CODE=M.ZONE_CODE AND M.LOCATION_ID= ?  AND M.SHIPMENT_MODE = ? "+ 
                                            " AND NVL(M.CONSOLE_TYPE,'~')= ? AND D.INVALIDATE='F' AND D.ZONE IN ("+consigneeZones+")");
       }
       else
       {
           psmt  = connection.prepareStatement(" SELECT COUNT(DISTINCT D.ZONE) NO_ROWS "+ 
                                            " FROM QMS_ZONE_CODE_MASTER M, QMS_ZONE_CODE_DTL D "+
                                            " WHERE D.ZONE_CODE=M.ZONE_CODE AND M.ORIGIN_LOCATION= ?  AND M.SHIPMENT_MODE = ? "+ 
                                            " AND NVL(M.CONSOLE_TYPE,'~')= ? AND D.INVALIDATE='F' AND D.ZONE IN ("+consigneeZones+")");
    
       }
        
        
        psmt.setString(1,masterDOB.getDestLocation()[lane]);
        psmt.setString(2,Integer.toString(masterDOB.getShipmentMode()));
        if("1".equalsIgnoreCase(Integer.toString(masterDOB.getShipmentMode())))
            psmt.setString(3,"~");
        else
            psmt.setString(3,consigneeConsoleType);
        
        if(consigneeZoneArray!=null && consigneeZoneArray.length>0)
        {
        	int consZoneArrLen	=	consigneeZoneArray.length;
          for(int k=0;k<consZoneArrLen;k++)
              psmt.setString(k+4,consigneeZoneArray[k]);
        }
        
        rs  = psmt.executeQuery();        
        
        while(rs.next())
        {
          if(rs.getInt("NO_ROWS")!=consigneeZonesLength)
            errors.append("Consignee Zone is not defined in the Zone code master or an invalid one for the Destination Location & selected Consignee Mode.<br>");
        }
        if(rs!=null)
          rs.close();
      }
      
      if(rs!=null)
        rs.close();
      if(psmt!=null)
        psmt.close();
}
     

    }catch(SQLException sqEx)
		{
      sqEx.printStackTrace();
			//Logger.error(FILE_NAME,"QMSQuoteSessionBean[validateQuoteMaster(masterDOB)] -> "+sqEx.toString());
      logger.error(FILE_NAME+"QMSMultiQuoteSessionBean[validateQuoteMaster(masterDOB)] -> "+sqEx.toString());
			throw new EJBException(sqEx.toString());
		}
    catch(Exception e)
		{
      e.printStackTrace();
			//Logger.error(FILE_NAME,"QMSQuoteSessionBean[validateQuoteMaster(masterDOB)] -> "+e.toString());
      logger.error(FILE_NAME+"QMSMultiQuoteSessionBean[validateQuoteMaster(masterDOB)] -> "+e.toString());
			throw new EJBException(e.toString());
		}
		finally
		{
			try
			{
				ConnectionUtil.closeConnection(connection,psmt,rs);
				ConnectionUtil.closeConnection(null,psmtSub,rsSub);//@@Added by Kameswari for the WPBN issue - on 02/02/2012
      
       // ConnectionUtil.closeConnection(null,stmt,null); //Commented By RajKumari on 27-10-2008 for Connection Leakages.
			}
			catch(EJBException ex)
			{
				//Logger.error(FILE_NAME,"Finally : QMSQuoteSessionBean[insertQuoteMasterDtls(masterDOB)]-> "+ex.toString());
        logger.error(FILE_NAME+"Finally : QMSMultiQuoteSessionBean[insertQuoteMasterDtls(masterDOB)]-> "+ex.toString());
				throw new EJBException(ex.toString());
			}
		}
    return errors;
  }
  
  public boolean getZipCode(String fromZipCode,String toZipCode, String givenZipCode) throws EJBException
  {
 	 /**
 	  * For Example:
 	  * 
 	  * fromZipCode 	=	"M6L 1A1";
 	  * toZipCode		=	"M6L 3G5";
 	  * givenZipCode	=	"M6L 3F9";
 	  * zone1Sub		= 	"1A1";
 	  * zone2Sub		=	"3G5";
 	  * zoneReqSub		=	"3F9";
 	  * zone1Sub1		=	1;
 	  * zone1Sub2		=	65;(HERE ALL ARE CAPITAL LETTERS ONLY)(ASCII VALUE)
 	  * zone1Sub3		=	1;
 	  * zone2Sub1		=	3;
 	  * zone2Sub2		=	71;
 	  * zone2Sub3		=	5;
 	  * zoneReqSub1		=	3;
 	  * zoneReqSub2		=	70;
 	  * zoneReqSub3		=	9;
 	  */
 		if(givenZipCode.length()>6)
 		{
 				 String zone1 		= 	fromZipCode;
 				 String zone2 		= 	toZipCode;
 				 String zoneReq 	= 	givenZipCode;
 			     String zone1Sub 	= 	zone1.substring(4,7);
 				 String zone2Sub 	= 	zone2.substring(4,7);
 			     String zoneReqSub 	= 	zoneReq.substring(4,7);
 			     int 	zone1Sub1 	= 	Integer.parseInt(zone1Sub.substring(0,1));
 			     int 	zone1Sub2 	= 	zone1Sub.substring(1,2).codePointAt(0);
 			     int 	zone1Sub3 	= 	Integer.parseInt(zone1Sub.substring(2,3));
 			     int 	zone2Sub1   = 	Integer.parseInt(zone2Sub.substring(0,1));
 			     int 	zone2Sub2 	= 	zone2Sub.substring(1,2).codePointAt(0);
 			     int 	zone2Sub3 	= 	Integer.parseInt(zone2Sub.substring(2,3));
 			     int 	zoneReqSub1 = 	Integer.parseInt(zoneReqSub.substring(0,1));
 			     int 	zoneReqSub2 = zoneReqSub.substring(1,2).codePointAt(0);
 			     int 	zoneReqSub3 = 	Integer.parseInt(zoneReqSub.substring(2,3));
 			     boolean zipCodeFlag = false;
 			     
 					if(zoneReqSub1>=zone1Sub1 && zoneReqSub1 <=zone2Sub1 )
 					{
 						if(zoneReqSub2>=zone1Sub2 && zoneReqSub2 <=zone2Sub2 )
 						{
 							if(zoneReqSub2>=zone1Sub2 && zoneReqSub2 <zone2Sub2 )
 							{
 								if(zoneReqSub3>=zone1Sub3 && zoneReqSub3 <10 )
 								{
 									zipCodeFlag	=	true;
 								}
 								else
 								{
 									zipCodeFlag	= false;
 								}					
 							}
 							else
 							{
 								if(zoneReqSub3>=zone1Sub3 && zoneReqSub3 <=zone2Sub3 )
 								{
 									zipCodeFlag	=	true;
 								}
 								else
 								{
 									zipCodeFlag	=	false;
 								}
 							}
 			
 						}
 						else
 						{
 							zipCodeFlag	=	false;
 						}
 					}
 					else
 					{
 						zipCodeFlag	=	false;
 					}
 				 return zipCodeFlag ;
 		}
 		else
 			return false;
 		
  }

  public void  updateAttentionToContacts(HashMap attentionDetails) throws EJBException
  {
	Connection                connection              = null;
	PreparedStatement         pstmt                    = null;
	String query="UPDATE QMS_CUST_CONTACTDTL DTL SET DTL.EMAILID=?,DTL.FAX=?,CONTACTNO=? WHERE DTL.CUSTOMERID=? AND DTL.SL_NO=?";
	//HashMap attentionDetails = null;
  String customerIds[] = null;
  String slNos[] = null;
  String emailIds[] = null;
  String faxNos[] = null;
  String contactNos[] = null;
	try
	{
	 connection  = this.getConnection();
	 pstmt  = connection.prepareStatement(query);
	// attentionDetails = masterDOB.getAttentionToDetails();
	 if(attentionDetails.get("customerId")!=null)
    customerIds =(String[]) attentionDetails.get("customerId");
    if(attentionDetails.get("slNo")!=null)
    slNos =(String[]) attentionDetails.get("slNo");
    if(attentionDetails.get("emailId")!=null)
    emailIds =(String[]) attentionDetails.get("emailId");
    if(attentionDetails.get("faxNo")!=null)
    faxNos =(String[]) attentionDetails.get("faxNo");
      if(attentionDetails.get("contactNo")!=null)
    contactNos =(String[]) attentionDetails.get("contactNo");
    if(customerIds!=null && customerIds.length>0)
    {
    	int custIdsLen	=	customerIds.length;
      for(int i=0;i<custIdsLen;i++)
      {
        pstmt.clearParameters();        
        if(emailIds[i]!=null)
              pstmt.setString(1,emailIds[i]);
            else
                pstmt.setNull(1,Types.VARCHAR);
       if(faxNos!=null && i<faxNos.length)
       {
          if(faxNos[i]!=null)
              pstmt.setString(2,faxNos[i]);
            else
                pstmt.setNull(2,Types.VARCHAR);
       }
       else pstmt.setNull(2,Types.VARCHAR);
        if(contactNos!=null && i<contactNos.length)
       {
          if(contactNos[i]!=null)
              pstmt.setString(3,contactNos[i]);
            else
                pstmt.setNull(3,Types.VARCHAR);
       }
       else pstmt.setNull(3,Types.VARCHAR);
        pstmt.setString(4,customerIds[i]);
        pstmt.setInt(5,Integer.parseInt(slNos[i]));
       
        pstmt.executeUpdate();
      }
    }
    
	} catch(SQLException sqEx)
		{
      sqEx.printStackTrace();			
      logger.error(FILE_NAME+"QMSQuoteSessionBean[updateAttentionToContacts(headerDOB)] -> "+sqEx.toString());
			throw new EJBException(sqEx.toString());
		}
   catch(Exception e)
		{
      e.printStackTrace();	
      logger.error(FILE_NAME+"QMSQuoteSessionBean[updateAttentionToContacts(headerDOB)] -> "+e.toString());
			throw new EJBException(e.toString());
		}
	finally {	ConnectionUtil.closeConnection(connection,pstmt);}

  }
  
  /**
	 * Method Added to Fetch Shipper & Consignee Zip Codes
 * if not Selected in Master Page.
	 * @param masterDOB, QuoteMasterDOB Object that contains all the Quote Master Page information
	 * 
	 * @exception EJBException 
	 */
public MultiQuoteMasterDOB getShipperConsigneeZones(MultiQuoteMasterDOB masterDOB) throws EJBException
{
  Connection                connection              = null;
  PreparedStatement         psmt                    = null;
  ResultSet                 rs                      = null;
  String                    query                   = null;
  String                    shipperAlpha            = null;
  String                    shipperZipCode          = null;
  String                    consigneeAlpha          = null;
  String                    consigneeZipCode        = null;
  String                    whereCondition          = "";
  String[]                  tempShipperZipCode      = masterDOB.getShipperZipCode();
  String[]                  tempConsigneeZipCode	= masterDOB.getConsigneeZipCode();
  int 						no_0f_zipcode			= masterDOB.getShipperZipCode().length;
  
  try
  {
    
    connection        =     this.getConnection();
    
    /*query             =   "SELECT D.ZONE ZONE_CODE FROM QMS_ZONE_CODE_MASTER M,QMS_ZONE_CODE_DTL D  "+
                          "WHERE D.ZONE_CODE=M.ZONE_CODE AND M.ORIGIN_LOCATION=? AND TO_NUMBER(?) BETWEEN "+
                          "D.FROM_ZIPCODE AND D.TO_ZIPCODE ";
                  
    psmt              =     connection.prepareStatement(query);*/
    
  for(int zip=0;zip<no_0f_zipcode;zip++)
  {
    if(masterDOB.getShipperZipCode()!=null && masterDOB.getShipperZipCode()[zip].trim().length()!=0)
    {
      shipperZipCode  = masterDOB.getShipperZipCode()[zip];
      if(masterDOB.getShipperZones()[zip]==null)
      {
        if(shipperZipCode.indexOf("-")!=-1)
        {
          shipperAlpha    =  shipperZipCode.substring(0,shipperZipCode.indexOf("-"));
          shipperZipCode  =  shipperZipCode.substring((shipperZipCode.indexOf("-")+1),shipperZipCode.trim().length());
        }
      
        if(shipperAlpha != null)
            whereCondition  = " AND D.ALPHANUMERIC= '"+shipperAlpha+"'";
      //  else
       //     whereCondition  = " AND D.ALPHANUMERIC IS NULL ";
        
        query         =   "SELECT D.ZONE ZONE_CODE FROM QMS_ZONE_CODE_MASTER M,QMS_ZONE_CODE_DTL D  "+
                          "WHERE D.ZONE_CODE=M.ZONE_CODE AND M.ORIGIN_LOCATION=? AND TO_NUMBER(?) BETWEEN "+
                          "D.FROM_ZIPCODE AND D.TO_ZIPCODE AND M.SHIPMENT_MODE = ? AND NVL(M.CONSOLE_TYPE, '~') = ? "+
                          whereCondition;
        
        psmt          =     connection.prepareStatement(query);
        psmt.setString(1,masterDOB.getOriginLocation()[zip]);
        psmt.setString(2,shipperZipCode);
        psmt.setString(3,Integer.toString(masterDOB.getShipmentMode()));          
        if("1".equalsIgnoreCase(Integer.toString(masterDOB.getShipmentMode())))
            psmt.setString(4,"~");
        else 
            psmt.setString(4,"List".equalsIgnoreCase(masterDOB.getMultiquoteweightBrake())?"FCL":"LCL");
        
        rs    =   psmt.executeQuery();
        
        while(rs.next())
        	tempShipperZipCode[zip] = rs.getString("ZONE_CODE");
        
        if(psmt!=null)
          psmt.close();
        if(rs!=null)
          rs.close();
      }
    }
    
    if(masterDOB.getConsigneeZipCode()[zip]!= null && masterDOB.getConsigneeZipCode()[zip].trim().length()!=0)
    {
      whereCondition    = "";
      consigneeZipCode  = masterDOB.getConsigneeZipCode()[zip];
      if(masterDOB.getConsigneeZones()[zip]==null)
      {
        if(consigneeZipCode.indexOf("-")!=-1)
        {
          consigneeAlpha    =  consigneeZipCode.substring(0,consigneeZipCode.indexOf("-"));
          consigneeZipCode  =  consigneeZipCode.substring((consigneeZipCode.indexOf("-")+1),consigneeZipCode.trim().length());
        }
        
        if(consigneeAlpha != null)
            whereCondition  = " AND D.ALPHANUMERIC= '"+consigneeAlpha+"'";
      //  else
      //      whereCondition  = " AND D.ALPHANUMERIC IS NULL ";
        
        query         =   "SELECT DISTINCT D.ZONE ZONE_CODE FROM QMS_ZONE_CODE_MASTER M,QMS_ZONE_CODE_DTL D  "+
                          "WHERE D.ZONE_CODE=M.ZONE_CODE AND M.ORIGIN_LOCATION=? AND TO_NUMBER(?) BETWEEN "+
                          "D.FROM_ZIPCODE AND D.TO_ZIPCODE AND M.SHIPMENT_MODE = ? AND NVL(M.CONSOLE_TYPE, '~') = ? "+
                          whereCondition;
        
        psmt          =     connection.prepareStatement(query);
        psmt.setString(1,masterDOB.getDestLocation()[zip]);
        psmt.setString(2,consigneeZipCode);
        psmt.setString(3,Integer.toString(masterDOB.getShipmentMode()));          
        if("1".equalsIgnoreCase(Integer.toString(masterDOB.getShipmentMode())))
            psmt.setString(4,"~");
        else 
            psmt.setString(4,"List".equalsIgnoreCase(masterDOB.getMultiquoteweightBrake())?"FCL":"LCL");
        
        rs    =   psmt.executeQuery();
        
        while(rs.next())
            tempConsigneeZipCode[zip] =rs.getString("ZONE_CODE");
      }                 
    }
  }
  masterDOB.setShipperZones(tempShipperZipCode);
  masterDOB.setConsigneeZones(tempConsigneeZipCode);
  
   }
  
  catch(Exception e)
  {
    //Logger.error(FILE_NAME,"Error in getShipperConsigneeZones"+e);
    logger.error(FILE_NAME+"Error in getShipperConsigneeZones"+e);
    e.printStackTrace();
    throw new EJBException(e.toString());
  }
  finally
  {
    ConnectionUtil.closeConnection(connection,psmt,rs);
  }
  return masterDOB;
}//@@ 
  
  

/**
 * This method helps in getting the sell rate details of 
* all the legs that are involved if a route is specifies
* or the sell rates between a particular origin and destination
* 
 * @param finalDOB 	an QuoteFinalDOB Object that contains all the Quote information
 * 
 * @exception EJBException 
 */
public MultiQuoteFinalDOB  getFreightSellRates(MultiQuoteFinalDOB finalDOB) throws EJBException
{
ArrayList                 freightRates            = null;//this is used to maintain info of all the legs
MultiQuoteFreightLegSellRates  legRateDetails          = null;//to maintain the info of each leg
MultiQuoteMasterDOB            masterDOB               = null;
QMSMultiQuoteDAO               quoteDAO                = null;
MultiQuoteFreightLegSellRates  legDOB                  = null;
MultiQuoteFreightLegSellRates  tiedCustLegDOB          = null;
String                    serviceLevel            = null;
ArrayList                 legDetails              = null;
ArrayList                 frtTiedCustInfoList     = null;
int                       legSize                 = 0;
    
try
{
 if(finalDOB!=null)//@@Added by Kameswari for the WPBN issue-141961 on 21/10/2008
 {
  legDetails    = finalDOB.getLegDetails();
 }
  if(legDetails!=null)
    legSize       = legDetails.size();
    
 if(finalDOB!=null)//@@Added by Kameswari for the WPBN issue-141961 on 21/10/2008
 {
    masterDOB           = finalDOB.getMasterDOB();
    frtTiedCustInfoList = finalDOB.getTiedCustomerInfoFreightList();
 }
  quoteDAO      = new QMSMultiQuoteDAO();
  
  serviceLevel            = masterDOB.getServiceLevelId()!=null?masterDOB.getServiceLevelId():"";
  
  freightRates  = new ArrayList();

  for(int i=0;i<legSize;i++)
  {
    legDOB          = (MultiQuoteFreightLegSellRates)legDetails.get(i);
    if(masterDOB.getSpotRatesFlag()!=null && "N".equalsIgnoreCase(masterDOB.getSpotRatesFlag()[i])) {//Added by Anil.k for Spot Rates
    if(frtTiedCustInfoList!=null)
      tiedCustLegDOB  = (MultiQuoteFreightLegSellRates)frtTiedCustInfoList.get(i);
    
    //Logger.info(FILE_NAME,"legDOB.getOrigin()::"+legDOB.getOrigin());
    //Logger.info(FILE_NAME,"legDOB.getDestination()::"+legDOB.getDestination());
    //Logger.info(FILE_NAME,"legDOB.getShipmentMode()::"+legDOB.getShipmentMode());
    
    if(tiedCustLegDOB==null)
//      legRateDetails  = quoteDAO.getFrtLegSellRates(legDOB.getOrigin(), legDOB.getDestination(), serviceLevel, legDOB.getShipmentMode(), masterDOB.getTerminalId(),masterDOB.getBuyRatesPermission(),masterDOB.getOperation(),masterDOB.getQuoteId(),masterDOB.getIncoTermsId()[i]);
    	legRateDetails  = quoteDAO.getFrtLegSellRates(legDOB.getOrigin(), legDOB.getDestination(), serviceLevel, legDOB.getShipmentMode(), masterDOB.getTerminalId(),masterDOB.getBuyRatesPermission(),masterDOB.getOperation(),masterDOB.getQuoteId(),masterDOB.getIncoTermsId()[i],masterDOB.getMultiquoteweightBrake());
    
    if(legRateDetails!=null)
    {
      legDOB.setRates(legRateDetails.getRates());
      legDOB.setSlabWeightBreaks(legRateDetails.getSlabWeightBreaks());
      /// legDOB.setFlatWeightBreaks(legRateDetails.getFlatWeightBreaks());
      legDOB.setListWeightBreaks(legRateDetails.getListWeightBreaks());
      legDetails.remove(i);
      legDetails.add(i,legDOB);
    }
  }
    else if(masterDOB.getSpotRatesFlag()!=null && "Y".equalsIgnoreCase(masterDOB.getSpotRatesFlag()[i]) && ("View".equalsIgnoreCase(masterDOB.getOperation()) || "Modify".equalsIgnoreCase(masterDOB.getOperation()))){//Added by Anil.k for Spot Rates
    	    	
       	legRateDetails  = quoteDAO.getSpotRates(legDOB.getOrigin(), legDOB.getDestination(), serviceLevel, legDOB.getShipmentMode(), masterDOB.getTerminalId(),masterDOB.getBuyRatesPermission(),masterDOB.getOperation(),masterDOB.getQuoteId(),masterDOB.getIncoTermsId()[i],masterDOB.getMultiquoteweightBrake(),i);
        
        if(legRateDetails!=null)
        {
          legDOB.setRates(legRateDetails.getRates());
          legDOB.setSlabWeightBreaks(legRateDetails.getSlabWeightBreaks());         
          legDOB.setListWeightBreaks(legRateDetails.getListWeightBreaks());
          legDOB.setSpotRateDescription(legRateDetails.getSpotRateDescription());
          legDOB.setSpotRateDetails(legRateDetails.getSpotRateDetails());          
          legDOB.setSpotRatesType(legRateDetails.getSpotRatesType());
          legDOB.setCarrier(legRateDetails.getCarrier());
          legDOB.setChargeRateIndicator(legRateDetails.getChargeRateIndicator());
          legDOB.setCurrency(legRateDetails.getCurrency());
          legDOB.setDensityRatio(legRateDetails.getDensityRatio());
          legDOB.setFrequency(legRateDetails.getFrequency());
          legDOB.setServiceLevel(legRateDetails.getServiceLevel());
          legDOB.setSurchargeId(legRateDetails.getSurchargeId());
          legDOB.setTransitTime(legRateDetails.getTransitTime());
          legDOB.setUom(legRateDetails.getUom());
          legDOB.setWeightBreak(legRateDetails.getWeightBreak());
          legDOB.setWeightBreaks(legRateDetails.getWeightBreaks());
          legDOB.setSurCurrency(legRateDetails.getSurCurrency());
          legDOB.setCheckedFlag(legRateDetails.getCheckedFlag());
          legDOB.setMarginType(legRateDetails.getMarginType());
          legDOB.setMarginValue(legRateDetails.getMarginValue());
          legDOB.setSpotrateSurchargeCount(Integer.parseInt(masterDOB.getSpotrateSurchargeCount()[i]));
          legDetails.remove(i);
          legDetails.add(i,legDOB);
        }
    }
  }//Ended by Anil.k for Spot Rates

  //end
  
  finalDOB.setLegDetails(legDetails);
  
}
catch(Exception e)
	{
  e.printStackTrace();
		//Logger.error(FILE_NAME,"QMSQuoteSessionBean[getFreightSellRates(masterDOB)] -> "+e.toString());
  logger.error(FILE_NAME+"QMSMultiQuoteSessionBean[getFreightSellRates(masterDOB)] -> "+e.toString());
		throw new EJBException(e.toString());
	}
	finally
	{
  legRateDetails  = null;
	}
return finalDOB;
}
public MultiQuoteFinalDOB getMarginLimit(MultiQuoteFinalDOB finalDOB) throws EJBException
{
  Connection                connection              = null;
  PreparedStatement         psmt                    = null;
  ResultSet                 rs                      = null;
  String                    frtQuery                = null;
  String                    chargesQuery            = null;
  String                    marginId                = "";
  String                    whereCondition          = "";
  ArrayList                 freightRates            = null;
  int                       noOfLegs                = 0;
  int                       selectedIndex           = 0;
  int 						selectedIndexsize		= 0;
  MultiQuoteFreightLegSellRates  legDOB                  = null;
  MultiQuoteMasterDOB            masterDOB               = null;
  String                    weightBreakType         = "";
  String                    legServiceLevel         = "";
  
  boolean                   marginLimitFlag         = false;
  boolean                   cartageMarginFlag       = false;
  boolean                   chargesMarginFlag       = false;
  
  try
  {          
        connection      =   this.getConnection();
        
        freightRates    =   finalDOB.getLegDetails();
        masterDOB       =   finalDOB.getMasterDOB();
    
        if(freightRates!=null)
          noOfLegs  = freightRates.size();
          
      
		   frtQuery = "SELECT MINMARGINS,MAXDISCOUNT,CHARGETYPE FROM QMS_MARGIN_LIMIT_DTL WHERE INVALIDATE='F' "+
					  "AND MARGIN_ID = ? AND SERVICE_LEVEL=? AND LEVELNO  = (SELECT LEVEL_NO "+
					  "FROM QMS_DESIGNATION WHERE DESIGNATION_ID=(SELECT DESIGNATION_ID FROM FS_USERMASTER WHERE EMPID=?)) "+
					  "AND CHARGETYPE='FREIGHT'";
     chargesQuery = "SELECT MINMARGINS,MAXDISCOUNT,CHARGETYPE FROM QMS_MARGIN_LIMIT_DTL WHERE INVALIDATE='F' "+
					  "AND LEVELNO  = (SELECT LEVEL_NO "+
					  "FROM QMS_DESIGNATION WHERE DESIGNATION_ID=(SELECT DESIGNATION_ID FROM FS_USERMASTER WHERE EMPID=?))"+
					  "AND CHARGETYPE <> 'FREIGHT'";      
        psmt  = connection.prepareStatement(frtQuery);
        
        for(int i=0;i<noOfLegs;i++)
        {
          marginLimitFlag =     false;
          legDOB          =     (MultiQuoteFreightLegSellRates)freightRates.get(i);
          selectedIndexsize	=   "N".equalsIgnoreCase(masterDOB.getSpotRatesFlag()[i])?legDOB.getSelectedFreightSellRateIndex().length:1;//Modified by Anil.k for Spot Rates
          for(int selind=0;selind<selectedIndexsize;selind++){
          selectedIndex   =   selind;
          
          if(legDOB.isSpotRatesFlag() || legDOB.isTiedCustInfoFlag())
          {
            //Logger.info(FILE_NAME,"inside spot rates for the leg :"+i);
            weightBreakType = legDOB.getSpotRatesType();
            legServiceLevel = legDOB.getServiceLevel();
          }
          else
          {
           
            //Logger.info(FILE_NAME,"in else with selected index::"+selectedIndex);
            if(selectedIndex!=-1){
            weightBreakType = ((MultiQuoteFreightRSRCSRDOB)legDOB.getRates().get(selectedIndex)).getWeightBreakType()!=null?
            ((MultiQuoteFreightRSRCSRDOB)legDOB.getRates().get(selectedIndex)).getWeightBreakType():"";

            legServiceLevel = (((MultiQuoteFreightRSRCSRDOB)legDOB.getRates().get(selectedIndex)).getServiceLevelId())!=null?
            ((MultiQuoteFreightRSRCSRDOB)legDOB.getRates().get(selectedIndex)).getServiceLevelId():"";
			  }             
          } 
          
          if(legDOB.getShipmentMode()==1)
            marginId = "1";
          else if(legDOB.getShipmentMode()==2 && "LIST".equals(weightBreakType))
            marginId = "4";
          else if(legDOB.getShipmentMode()==2 && !"LIST".equals(weightBreakType))
            marginId = "2";
          else if(legDOB.getShipmentMode()==4 && "LIST".equals(weightBreakType))
            marginId = "15";
          else if(legDOB.getShipmentMode()==4 && !"LIST".equals(weightBreakType))
            marginId = "7";
            
           psmt.setString(1,marginId);
           psmt.setString(2,legServiceLevel);
           psmt.setString(3,masterDOB.getSalesPersonCode());
           
           rs   =   psmt.executeQuery();
           
           if(rs.next())
           {
              //Logger.info(FILE_NAME,"inside rs for margin test:");
              marginLimitFlag = true;//@@This flag is checked only in case of freight rates.
            
              legDOB.setMarginLimit(rs.getDouble("MINMARGINS"));
              
              legDOB.setDiscountLimit(rs.getDouble("MAXDISCOUNT"));
              legDOB.setMarginFlag(true);
             
           }
          else legDOB.setMarginFlag(false);
          
          //Logger.info(FILE_NAME,"legDOB.isMarginFlag():"+legDOB.isMarginFlag());
          
          //Logger.info(FILE_NAME,"legDOBlegDOB::"+legDOB);
              
          if(rs!=null)
            rs.close();
          
          psmt.clearParameters();
              
          freightRates.remove(i);
          freightRates.add(i,legDOB);    
        }
        //}
        } //Added by Anil.k for Spot Rates
        
        if(rs!=null)
            rs.close();
        if(psmt!=null)
          psmt.close();
        
        psmt  = connection.prepareStatement(chargesQuery);
        
        psmt.setString(1,masterDOB.getSalesPersonCode());
        
        rs    = psmt.executeQuery();
        
        while(rs.next())
        {
          if("CHARGES".equalsIgnoreCase(rs.getString("CHARGETYPE")))
          {
              chargesMarginFlag = true;
              finalDOB.setChargesMargin(rs.getDouble("MINMARGINS"));
             finalDOB.setChargesDiscount(rs.getDouble("MAXDISCOUNT"));
          }
          else if("CARTAGES".equalsIgnoreCase(rs.getString("CHARGETYPE")))
          {
              cartageMarginFlag = true;
              finalDOB.setCartageMargin(rs.getDouble("MINMARGINS"));
             finalDOB.setCartageDiscount(rs.getDouble("MAXDISCOUNT"));
          }
        }
        finalDOB.setChargesMarginDefined(chargesMarginFlag);
        finalDOB.setCartageMarginDefined(cartageMarginFlag);
        finalDOB.setLegDetails(freightRates);        
  }
  catch(Exception e) 
		{
    e.printStackTrace();
			//Logger.error(FILE_NAME,"QMSQuoteSessionBean[getFreightSellRates(masterDOB)] -> "+e.toString());
    logger.error(FILE_NAME+"QMSQuoteSessionBean[getFreightSellRates(masterDOB)] -> "+e.toString());
			throw new EJBException(e.toString());
		}
  finally
  {
    ConnectionUtil.closeConnection(connection,psmt,rs);
  }
  return finalDOB;
}
 
/**
 * This method helps in getting the header and charhes information that are to be displayed on the quote 
* 
 * @param finalDOB 	an QuoteFinalDOB Object that contains all the Quote information
 * 
 * @exception EJBException 
 */
public MultiQuoteFinalDOB  getChargesAndHeader(MultiQuoteFinalDOB finalDOB) throws EJBException
{
ArrayList                 chargesList   = null;
MultiQuoteMasterDOB            masterDOB     = finalDOB.getMasterDOB();
MultiQuoteHeader               headerDOB     = null;
MultiQuoteCharges              chargesDOB    = null;
    
try
{
  finalDOB = getQuoteHeader(finalDOB);
 
  //finalDOB.setHeaderDOB(headerDOB);\
  if("View".equalsIgnoreCase(masterDOB.getOperation()))
  {
	  //Modified by Rakesh  on 08-01-2010
  //   finalDOB  = getRatesChargesInfo(""+masterDOB.getQuoteId(),finalDOB,null);
		  finalDOB  = getCharges(finalDOB);
   
  }
  else
    finalDOB  = getCharges(finalDOB);
}
catch(Exception e)
	{
  e.printStackTrace();
		//Logger.error(FILE_NAME,"QMSQuoteSessionBean[getChargesAndHeader(masterDOB)] -> "+e.toString());
  logger.error(FILE_NAME+"QMSMultiQuoteSessionBean[getChargesAndHeader(masterDOB)] -> "+e.toString());
		throw new EJBException(e.toString());
	}
	finally
	{
	}
return finalDOB;
}  
 
/**
 * This method helps in getting the header information that is to be displayed on the quote 
* 
 * @param finalDOB 	an QuoteFinalDOB Object that contains all the Quote information
 * 
 * @exception EJBException 
 */
public MultiQuoteFinalDOB getQuoteHeader(MultiQuoteFinalDOB finalDOB) throws EJBException
{
MultiQuoteMasterDOB            masterDOB               = finalDOB.getMasterDOB();
MultiQuoteHeader               headerDOB               = null;
ArrayList                 legRateDetails          = null;
MultiQuoteFreightRSRCSRDOB     ratesDOB                = null;
Connection                connection              = null;
PreparedStatement         psmt                    = null;
PreparedStatement         psmt1                   = null;
ResultSet                 rs                      = null;
String                    query                   = null;
String                    locationQry             = null;
ArrayList                 toEmailIds              = null;
ArrayList                 toFaxIds                = null;
ArrayList                 toContactNo             = null;//Added by Anil.k for Back Button With Charges
String[]                  contactPersonsEmails    = null;
String[]                  contactPersonsFax       = null;
String[]                  contactNo			      = null;//Added by Anil.k for Back Button With Charges
String[]				  tempOriginCountryId     = null;
String[]				  tempDestCountryId       = null;
String[]				  tempOriginLocName		  = null;
String[]				  tempDestLocName		  = null;
String[]				  tempOrgCountry		  = null;
String[]				  tempDestCountry		  = null;
String[]				  tempOriginPortName	  = null;
String[]                  tempDestPortName		  = null;
String[]                  tempOriginPortCountry   = null;
String[]                  tempDestPortCountry	  = null;
String[]                  tempCargoAcceptancePlace= null;
 //ResultSet                 rsdtls                      = null;    //Commented By RajKumari on 27-10-2008 for Connection Leakages.
 String                    department            = "";
 String                     description          = "";
//@@ Added by subrahmanyam for Enhancement 167668 on 29/04/09     
 String                     custAddress1         =  null;
 String                     custAddress2         =  null;
 String                     custAddress3         =  null;
 String                     custAddrQry          =  null;
 String                     queryNoCustAddr      =  null;

 int						len					 = 0;
//@@ Ended by subrahmanyam for Enhancement 167668 on 29/04/09     
try
{

  connection  = this.getConnection();
  headerDOB   = new MultiQuoteHeader();
  StringBuffer sBuffer   = new StringBuffer("");
  
 // locationQry = "SELECT CM.COUNTRYID,COUNTRYNAME,LOCATIONNAME FROM FS_FR_LOCATIONMASTER LM, FS_COUNTRYMASTER CM WHERE CM.COUNTRYID = LM.COUNTRYID AND LOCATIONID = ?";
  // locationQry = "SELECT CM.COUNTRYID,COUNTRYNAME,LOCATIONNAME FROM FS_FR_LOCATIONMASTER LM, FS_COUNTRYMASTER CM WHERE CM.COUNTRYID = LM.COUNTRYID AND LOCATIONID = ?";
   locationQry = "SELECT CM.COUNTRYID,COUNTRYNAME,LOCATIONNAME FROM FS_FR_LOCATIONMASTER LM, FS_COUNTRYMASTER CM WHERE CM.COUNTRYID = LM.COUNTRYID AND LOCATIONID = ?";
  psmt1  = connection.prepareStatement(locationQry);
  
  if(masterDOB.getOriginLocation()!=null)
  {
    len = masterDOB.getOriginLocation().length;
	tempOriginCountryId = new String[len];
	tempOriginLocName   = new String[len];
    tempOrgCountry		= new String[len];
	for(int i=0;i<len;i++){
    psmt1.setString(1,masterDOB.getOriginLocation()[i]);
    rs    = psmt1.executeQuery();
    if(rs.next())
    {
		tempOriginCountryId[i] = rs.getString("COUNTRYID");
        tempOriginLocName[i]   = rs.getString("LOCATIONNAME");
        tempOrgCountry[i]	   = rs.getString("COUNTRYNAME");
    }
          psmt1.clearParameters();
			rs.close();
    }

	  headerDOB.setOriginCountryId(tempOriginCountryId);
      headerDOB.setOriginLocName(tempOriginLocName);
      headerDOB.setOriginCountry(tempOrgCountry);
	}
   
  psmt1.clearParameters();
  
  if(masterDOB.getDestLocation()!=null)
  {
    len = masterDOB.getDestLocation().length;
    tempDestCountryId = new String[len];
    tempDestLocName   = new String[len];
    tempDestCountry		= new String[len];
	for(int i=0;i<len;i++){
    psmt1.setString(1,masterDOB.getDestLocation()[i]);
    rs    = psmt1.executeQuery();
    if(rs.next())
    {
      tempDestCountryId[i] = rs.getString("COUNTRYID");
      tempDestLocName[i]   = rs.getString("LOCATIONNAME");
      tempDestCountry[i]   = rs.getString("COUNTRYNAME");
	  
    }
    psmt1.clearParameters();
	rs.close();
	}
	headerDOB.setDestinationCountryId(tempDestCountryId);
    headerDOB.setDestLocName(tempDestLocName);
    headerDOB.setDestinationCountry(tempDestCountry);
  }
  
  if(rs!=null)
    rs.close();

  query = "SELECT CM.COMPANYNAME,CM.OPERATIONS_EMAILID,AD.FAX,AD.COUNTRYID,CM.PAYMENTTERMS FROM FS_FR_CUSTOMERMASTER CM,FS_ADDRESS AD"+
          " WHERE CM.CUSTOMERID=? AND CM.CUSTOMERADDRESSID=AD.ADDRESSID";


  psmt  = connection.prepareStatement(query);
  psmt.setString(1,masterDOB.getCustomerId());
  //psmt.setString(2,masterDOB.getTerminalId());
  rs    = psmt.executeQuery();
 
  if(rs.next())
  {
   
    headerDOB.setCustomerName(rs.getString("COMPANYNAME"));
    headerDOB.setCustEmailId(rs.getString("OPERATIONS_EMAILID"));
    headerDOB.setCustFaxNo(rs.getString("FAX"));
    headerDOB.setCustCountyCode(rs.getString("COUNTRYID"));
    headerDOB.setPaymentTerms(rs.getString("PAYMENTTERMS"));
  }
  if(rs!=null)
    rs.close();
  if(psmt!=null)
    psmt.close();
  query = null;
  //Added by Anil.k for Back Button Quote With Charges
  query = "SELECT EMAILID,FAX,CONTACTNO FROM QMS_CUST_CONTACTDTL WHERE CUSTOMERID=? AND SL_NO=? ORDER BY SL_NO";

  if(masterDOB.getCustomerContacts()!=null)
  {
    toEmailIds  = new ArrayList();
    toFaxIds    = new ArrayList();
    toContactNo = new ArrayList();
    psmt  = connection.prepareStatement(query);
   
       
    for(int i=0;i<masterDOB.getCustomerContacts().length;i++)
    {
    
      if(masterDOB.getCustomerContacts()[i]!=null && masterDOB.getCustomerContacts()[i].trim().length()!=0)
      {
        psmt.clearParameters();
        psmt.setString(1,masterDOB.getCustomerId());
       
        psmt.setInt(2,Integer.parseInt(masterDOB.getCustomerContacts()[i]));
        rs    = psmt.executeQuery();
        
        if(rs.next())
        {
          toEmailIds.add(rs.getString("EMAILID"));
         
          toFaxIds.add(rs.getString("FAX"));
          
          toContactNo.add(rs.getString("CONTACTNO"));//Added by Anil.k for Back Button With Charges
        }
      }
      
      if(rs!=null)
        rs.close();
    }
    if(toEmailIds!=null && toEmailIds.size() > 0)
      contactPersonsEmails  = new String[toEmailIds.size()];
    if(toFaxIds!=null && toFaxIds.size() > 0)
      contactPersonsFax     = new String[toFaxIds.size()];
    if(toFaxIds!=null && toFaxIds.size() > 0)//Added by Anil.k for Back Button With Charges
      contactNo				= new String[toContactNo.size()];
      
    int mailIdsSize	=	toEmailIds.size();
    for(int i=0;i<mailIdsSize;i++)
    {
      contactPersonsEmails[i]   =   (String)toEmailIds.get(i);
    
    }
    int faxIdsSize	= toFaxIds.size();
    for(int i=0;i<faxIdsSize;i++)
      contactPersonsFax[i]      =    (String)toFaxIds.get(i);
    for(int i=0;i<faxIdsSize;i++)//Added by Anil.k for Back Button With Charges
    	contactNo[i]      =    (String)toContactNo.get(i);
      //adeed by phani sekhar for wpbn 167678 on 20090415
     if(masterDOB.getCustomerContactsEmailIds()==null )
    masterDOB.setCustomerContactsEmailIds(contactPersonsEmails);
    masterDOB.setCustomerContactsFax(contactPersonsFax);
    masterDOB.setCustomerContactNo(contactNo);//Added by Anil.k for Back Button With Charges
  }
  
  if(psmt!=null)
    psmt.close();
  
  
  if(masterDOB.getCustomerContacts()!=null && masterDOB.getCustomerContacts().length!=0)
    headerDOB.setAttentionTo(masterDOB.getCustomerContacts());
  
  headerDOB.setDateOfQuotation(masterDOB.getModifiedDate()!=null?masterDOB.getModifiedDate():masterDOB.getCreatedDate());
  headerDOB.setAgent("DHL Global Forwarding");//hard coded
  query = null;
  
  query = "SELECT PM.PORTNAME, CM.COUNTRYNAME FROM FS_FRS_PORTMASTER PM, FS_COUNTRYMASTER CM WHERE CM.COUNTRYID=PM.COUNTRYID AND PORTID=?";
  
  psmt  = connection.prepareStatement(query);
  
  //String portName = null;
  
  if(masterDOB.getOriginPort()!=null)
  {
	len = masterDOB.getOriginPort().length;
    tempOriginPortName = new String[len];
    tempOriginPortCountry = new String[len];
	for(int i=0;i<len;i++){
    psmt.setString(1,masterDOB.getOriginPort()[i]);
    rs    = psmt.executeQuery();
    if(rs.next())
    {
      //portName  =  rs.getString("PORTNAME");
      tempOriginPortName[i] = rs.getString("PORTNAME");
      tempOriginPortCountry[i] = rs.getString("COUNTRYNAME");
      
	 }
    else
    {
     if(rs!=null)
        rs.close();
      psmt1.clearParameters();
      psmt1.setString(1,masterDOB.getOriginPort()[i]);
      rs      =   psmt1.executeQuery();
      if(rs.next())
      {
        tempOriginPortName[i] = rs.getString("LOCATIONNAME");
        tempOriginPortCountry[i] = rs.getString("COUNTRYNAME");
      }
    }
    psmt.clearParameters();
	rs.close();
	}
	headerDOB.setOriginPortName(tempOriginPortName);
    headerDOB.setOriginPortCountry(tempOriginPortCountry);
  }
  else
  {
    headerDOB.setOriginPortName(headerDOB.getOriginLocName());
    headerDOB.setOriginPortCountry(headerDOB.getOriginCountry());
  }
  psmt.clearParameters();
  
  
  if(masterDOB.getDestPort()!=null)
  {
    len = masterDOB.getDestPort().length;
    tempDestPortName = new String[len];
    tempDestPortCountry = new String[len];
	for(int i=0;i<len;i++){
    psmt.setString(1,masterDOB.getDestPort()[i]);
    rs    = psmt.executeQuery();
    if(rs.next())
    {
      tempDestPortName[i] = rs.getString("PORTNAME");
      tempDestPortCountry[i] = rs.getString("COUNTRYNAME");
	  
	  }
    else
    {
       if(rs!=null)
        rs.close();
      psmt1.clearParameters();
      psmt1.setString(1,masterDOB.getDestPort()[i]);
      rs      =   psmt1.executeQuery();
      
      if(rs.next())
      {
        tempDestPortName[i] = rs.getString("LOCATIONNAME");
		tempDestPortCountry[i] = rs.getString("COUNTRYNAME");
      
		
      }
    }
	psmt.clearParameters();
	rs.close();
	}
	headerDOB.setDestPortName(tempDestPortName);
    headerDOB.setDestPortCountry(tempDestPortCountry);
  }
  else
  {
    headerDOB.setDestPortName(headerDOB.getDestLocName());
    headerDOB.setDestPortCountry(headerDOB.getDestinationCountry());
  }
  
  if(rs!=null)
    rs.close();
  if(psmt!=null)
    psmt.close();
  if(psmt1!=null)
    psmt1.close();

  //Added by Anil.k
if(!("Charges".equalsIgnoreCase(masterDOB.getQuoteWith())))
{//END
  String[]       incoTermsId     = masterDOB.getIncoTermsId();
  len  = incoTermsId.length;
  tempCargoAcceptancePlace = new String[len];
  for(int i=0;i<len;i++){
  StringBuffer cargoAcceptance = new StringBuffer(incoTermsId[i]+" ");
  
  if("other".equalsIgnoreCase(masterDOB.getCargoAcceptance()[i]))
  {
    tempCargoAcceptancePlace[i] = masterDOB.getCargoAccPlace()[i];
  }
  else
  {
    if("EXW".equalsIgnoreCase(incoTermsId[i]) || "FCA".equalsIgnoreCase(incoTermsId[i]) || "FAS".equalsIgnoreCase(incoTermsId[i]) || "FOB".equalsIgnoreCase(incoTermsId[i]))
    {
      if("ddao".equalsIgnoreCase(masterDOB.getCargoAcceptance()[i]))
       cargoAcceptance.append("DGF TERMINAL ").append(headerDOB.getOriginLocName()[i]);//@@Modified by Yuvraj for WPBN-DHLQMS-22531
      else if("port".equalsIgnoreCase(masterDOB.getCargoAcceptance()[i]))
        cargoAcceptance.append(headerDOB.getOriginPortName()[i]); 
      else if("ZIPCODE".equalsIgnoreCase(masterDOB.getCargoAcceptance()[i]))
       cargoAcceptance.append(masterDOB.getShipperZipCode()[i]).append(" ").append(headerDOB.getOriginLocName()[i]);
      else if("ZONECODE".equalsIgnoreCase(masterDOB.getCargoAcceptance()[i]))
        cargoAcceptance.append("Zone ").append(masterDOB.getShipperZones()[i]).append(" ").append(headerDOB.getOriginLocName()[i]).append(" (Refer Attachment)");
    }
    else
    {
      if("ddao".equalsIgnoreCase(masterDOB.getCargoAcceptance()[i]))
         cargoAcceptance.append("DGF TERMINAL ").append(headerDOB.getDestLocName()[i]);//@@Modified by Yuvraj for WPBN-DHLQMS-22531
      else if("port".equalsIgnoreCase(masterDOB.getCargoAcceptance()[i]))
        cargoAcceptance.append(headerDOB.getDestPortName()[i]); 
      else if("ZIPCODE".equalsIgnoreCase(masterDOB.getCargoAcceptance()[i]))
        cargoAcceptance.append(masterDOB.getConsigneeZipCode()[i]).append(" ").append(headerDOB.getDestLocName()[i]);
      else if("ZONECODE".equalsIgnoreCase(masterDOB.getCargoAcceptance()[i]))
        cargoAcceptance.append(masterDOB.getConsigneeZones()[i]).append(" ").append(headerDOB.getDestLocName()[i]).append("(Refer Attachment)");
    }
	tempCargoAcceptancePlace[i] = cargoAcceptance.toString();
  }

}
headerDOB.setCargoAcceptancePlace(tempCargoAcceptancePlace);
}//Added by Anil.k
headerDOB.setRouting("Direct"); 
headerDOB.setTypeOfService("Multi-Lane/Multi-Carrier");

  
if(masterDOB.getCommodityId()!=null)
  {
    query = null;
    query = "SELECT COMODITYDESCRIPTION FROM FS_FR_COMODITYMASTER WHERE COMODITYID =?";
    psmt  = connection.prepareStatement(query);
    psmt.setString(1,masterDOB.getCommodityId());
    rs    = psmt.executeQuery();
    if(rs.next())
    {
      headerDOB.setCommodity(rs.getString("COMODITYDESCRIPTION"));
    }
  }
  else
  {
	//Modified By Kishore Podili For Hazardous Checking 
    //headerDOB.setCommodity("General Cargo-Non Hazardous");
	  if(masterDOB.isHazardousInd())
		  headerDOB.setCommodity("Cargo-Hazardous");//modified by silpa on 5-04-11
	  else
		  headerDOB.setCommodity("Cargo-Non Hazardous");//modified by silpa on 5-04-11
  }
  
  if(rs!=null)
    rs.close();
  if(psmt!=null)
    psmt.close();
  
  headerDOB.setIncoTerms(masterDOB.getIncoTermsId());
  headerDOB.setNotes("Based On Over Length Cargo: "+masterDOB.getOverLengthCargoNotes()!=null?masterDOB.getOverLengthCargoNotes():"");
  headerDOB.setEffDate(masterDOB.getEffDate());
  
  if(masterDOB.getValidTo()!=null)
  {
    headerDOB.setValidUpto(masterDOB.getValidTo());
  }
  
  //psmt  = connection.prepareStatement(query);
  finalDOB.setHeaderDOB(headerDOB);
  
 // added by VLAKSHMI for WPBN issue 167659 (CR) on 22/04/2009  
  if(finalDOB.getMasterDOB().getSalesPersonCode()!=null)
  {
       int shipmentMode=masterDOB.getShipmentMode();

   String  shipment      = "";   
    if(shipmentMode==4){
     shipment = " 4,5,6,7 ";
       }else if(shipmentMode==1){
    shipment = " 1,3,5,7 ";
       }else if(shipmentMode==2){
    shipment = " 2,3,6,7 ";
         }
         
         query = null;
         
     query = "SELECT ss.rep_officers_id, (select emailid from FS_USERMASTER where empid=ss.rep_officers_id)emailid, SS.ALLOTED_TIME FROM FS_USERMASTER RW, fs_rep_officers_master SS WHERE RW.EMPID =  ? and SS.shipment_mode in("+shipment+") and RW.userid = SS.userid  and RW.locationid = SS.locationid" ;//@@Commented and Modified by Kameswari for the WPBN issue - 179220 on 12/08/09
      psmt  = connection.prepareStatement(query);

    psmt.setString(1,finalDOB.getMasterDOB().getSalesPersonCode());
    rs    = psmt.executeQuery();
  
    if(rs.next())
    {  
     finalDOB.setReportingOfficer(rs.getString("rep_officers_id"));
     finalDOB.setReportingOfficerEmail(rs.getString("EMAILID"));
      finalDOB.setAllottedTime(rs.getString("ALLOTED_TIME"));
    }
     if(rs!=null)
      rs.close();
    if(psmt!=null)
      psmt.close();
  }
if(masterDOB.getEmpId()!=null)
  {
     query = null;
 
     query  = "SELECT U.USERNAME, U.DEPARTMENT, U.EMAILID,U.PHONE_NO,U.FAX_NO,U.MOBILE_NO, FSC.COMPANYNAME FROM "+
               "  FS_USERMASTER U,FS_COMPANYINFO FSC WHERE U.EMPID = ?   AND FSC.COMPANYID = ?";   //@@Modified for the WPBN issue-70355                  
 

     psmt  = connection.prepareStatement(query);
    psmt.setString(1,masterDOB.getEmpId());
    psmt.setString(2,masterDOB.getCompanyId());
 
     rs    = psmt.executeQuery();
     
    while(rs.next())
    {
          masterDOB.setUserEmailId(rs.getString("EMAILID"));
               if(rs.getString("DEPARTMENT")!=null)   
              department = rs.getString("DEPARTMENT");
     masterDOB.setCreatorDetails(rs.getString("USERNAME")+"\n"+department);//@@Modified for the WPBN issue-70355  
     masterDOB.setPhoneNo((rs.getString("PHONE_NO")!=null)?rs.getString("PHONE_NO"):"");
     masterDOB.setFaxNo((rs.getString("FAX_NO")!=null)?rs.getString("FAX_NO"):"");
     masterDOB.setMobileNo((rs.getString("MOBILE_NO")!=null)?rs.getString("MOBILE_NO"):"");
     masterDOB.setCompanyName((rs.getString("COMPANYNAME")!=null)?rs.getString("COMPANYNAME"):"");
    
     }
    if(rs!=null)
      rs.close();
    if(psmt!=null)
      psmt.close();
  }
  if(masterDOB.getTerminalId()!=null)
  {
  
   
 query =   "SELECT (AD.ADDRESSLINE1||'\n\n'||DECODE(AD.ADDRESSLINE2,null,'',AD.ADDRESSLINE2||'\n\n')||DECODE(AD.ADDRESSLINE3,NULL,'',AD.ADDRESSLINE3||'\n\n')||AD.CITY||'\n'||DECODE(AD.STATE,NULL,'',AD.STATE||'\n')"+
            "||DECODE(AD.ZIPCODE,NULL,'',AD.ZIPCODE||'\n')||UPPER(CO.COUNTRYNAME)||'\n')ADDRESS FROM FS_FR_TERMINALMASTER TM,FS_ADDRESS AD "+
            ",FS_COUNTRYMASTER CO WHERE TM.CONTACTADDRESSID=AD.ADDRESSID AND AD.COUNTRYID=CO.COUNTRYID AND TM.TERMINALID=?";

  custAddrQry = " SELECT  CUST_ADDRLINE1 ,CUST_ADDRLINE2 ,CUST_ADDRLINE3  FROM FS_USERMASTER WHERE USERID=? AND LOCATIONID=? ";
  queryNoCustAddr = "SELECT AD.CITY||'\n'||DECODE(AD.STATE,NULL,'',AD.STATE||'\n')"+
            "||DECODE(AD.ZIPCODE,NULL,'',AD.ZIPCODE||'\n')||UPPER(CO.COUNTRYNAME)ADDRESS FROM FS_FR_TERMINALMASTER TM,FS_ADDRESS AD "+
            ",FS_COUNTRYMASTER CO WHERE TM.CONTACTADDRESSID=AD.ADDRESSID AND AD.COUNTRYID=CO.COUNTRYID AND TM.TERMINALID=?";
psmt  = connection.prepareStatement(custAddrQry);  
psmt.setString(1,masterDOB.getCreatedBy());
psmt.setString(2,masterDOB.getTerminalId());
rs    = psmt.executeQuery();
if(rs.next())
{
  custAddress1=rs.getString("CUST_ADDRLINE1");
  custAddress2=rs.getString("CUST_ADDRLINE2");
  custAddress3=rs.getString("CUST_ADDRLINE3");
}
    if(rs!=null)
      rs.close();
    if(psmt!=null)
      psmt.close();
if(custAddress1==null && custAddress2==null && custAddress3==null)
{
     psmt  = connection.prepareStatement(query);
    psmt.setString(1,masterDOB.getTerminalId());
    rs    = psmt.executeQuery();
    
    if(rs.next())
    {
      masterDOB.setTerminalAddress(rs.getString("ADDRESS"));
    }
    if(rs!=null)
      rs.close();
    if(psmt!=null)
      psmt.close();
}
else
  {
        custAddress1=(custAddress1!=null?custAddress1+"\n\n":"")+(custAddress2!=null?custAddress2+"\n\n":"")+(custAddress3!=null?custAddress3+"\n\n":"");
        psmt  = connection.prepareStatement(queryNoCustAddr);
    psmt.setString(1,masterDOB.getTerminalId());
    rs    = psmt.executeQuery();
    
    if(rs.next())
    {
      masterDOB.setTerminalAddress(custAddress1+rs.getString("ADDRESS"));
    }
    if(rs!=null)
      rs.close();
    if(psmt!=null)
      psmt.close();
  }
//@@ Ended by subrahmanyam for Enhancement 167668 on 29/04/09    
  }
  
    
  if(masterDOB.getSalesPersonCode()!=null)
  {
    query = null;
    query = "SELECT USERNAME,EMAILID FROM FS_USERMASTER WHERE EMPID=?";
    psmt  = connection.prepareStatement(query);
    psmt.setString(1,masterDOB.getSalesPersonCode());
    rs    = psmt.executeQuery();
    if(rs.next())
    {
      masterDOB.setSalesPersonName(rs.getString("USERNAME"));
      masterDOB.setSalesPersonEmail(rs.getString("EMAILID"));
      headerDOB.setPreparedBy(rs.getString("USERNAME"));
    }
     if(rs!=null)
      rs.close();
    if(psmt!=null)
      psmt.close();
  }
  finalDOB.setMasterDOB(masterDOB);
}
catch(SQLException sqEx)
	{
  sqEx.printStackTrace();
		//Logger.error(FILE_NAME,"QMSQuoteSessionBean[getQuoteHeader(finalDOB)] -> "+sqEx.toString());
  logger.error(FILE_NAME+"QMSMultiQuoteSessionBean[getQuoteHeader(finalDOB)] -> "+sqEx.toString());
		throw new EJBException(sqEx.toString());
	}
catch(Exception e)
	{
  e.printStackTrace();
		//Logger.error(FILE_NAME,"QMSQuoteSessionBean[getQuoteHeader(finalDOB)] -> "+e.toString());
  logger.error(FILE_NAME+"QMSMultiQuoteSessionBean[getQuoteHeader(finalDOB)] -> "+e.toString());
		throw new EJBException(e.toString());
	}
	finally
	{
		try
		{
			ConnectionUtil.closeConnection(connection,psmt,rs);
		}
		catch(EJBException ex)
		{
			//Logger.error(FILE_NAME,"Finally : QMSQuoteSessionBean[getQuoteHeader(finalDOB)]-> "+ex.toString());
    logger.error(FILE_NAME+"Finally : QMSMultiQuoteSessionBean[getQuoteHeader(finalDOB)]-> "+ex.toString());
			throw new EJBException(ex.toString());
		}
	}
return finalDOB;
}

public MultiQuoteFinalDOB  getCharges(MultiQuoteFinalDOB finalDOB) throws EJBException
{
  ArrayList                 charges                 = null;//this is used to maintain info of all the legs
  ArrayList                 freightRates            = null;
  ArrayList                 rates                   = null;
  int                       freightRatesSize        = 0;
  
  MultiQuoteFreightLegSellRates  legRateDetails          = null;//to maintain the info of each leg
  MultiQuoteFreightLegSellRates  legChargeDetails        = null;//to maintain the list of charge info of each leg
  MultiQuoteFreightLegSellRates  tiedCustInfoLeg         = null;//to maintain the list of charge info of each leg
  MultiQuoteFreightRSRCSRDOB     ratesDOB                = null;
  MultiQuoteMasterDOB            masterDOB               = null;
  MultiQuoteCharges              chargesDOB              = null;
  MultiQuoteChargeInfo           chargeInfo              = null;
  ArrayList                 chargeInfoList          = null;
  ArrayList                 freightChargesList      = null;
  ArrayList                 tiedCustInfoList        = null;
   
  QMSMultiQuoteDAO               quoteDAO                = null;
  
  ArrayList                 tiedCustInfoFrtList     = null;
  MultiQuoteCharges              tiedCustInfoChargesDOB  = null;
  double                    marginLimit             = 0;
//@@ Added by subrahmanyam for the wpbn id 199797 on 17-Mar-010    
  MultiQuoteFreightLegSellRates  legChargeDetailsSpotModify        = null;
	ArrayList     freightChargesSpotModify          	= null;
	ArrayList     freightChargesSpotModifyOld           = null;
	MultiQuoteCharges  chargesDOBSpotModify			  		= null;
	MultiQuoteCharges  chargesDOBSpotModifyOld			 	= null;
	ArrayList     freightChargeInfoSpotModify       	= null;
	ArrayList     freightChargeInfoSpotModifyOld       	= null;
	ArrayList     originChargesList						= new ArrayList();
	ArrayList     destChargesList						= new ArrayList();
	int 		  spotRateOldSize						=	0;
	int 		  spotRateNewSize						=	0;
	int selectedRateIndexSize			=0;
	MultiQuoteChargeInfo           chargeInfoSpotModify             = new MultiQuoteChargeInfo();
	ArrayList                 chargeInfoListSpotModify          = null;
	
//@@ Ended by subrahmanyam for the wpbn id 199797 on 17-Mar-010	
  try
  {
    charges  = new ArrayList();
      if(finalDOB!=null)
    {
       masterDOB       = finalDOB.getMasterDOB();
       tiedCustInfoList= finalDOB.getTiedCustomerInfoFreightList();
    }
     quoteDAO        = new QMSMultiQuoteDAO();
  
    
    if((masterDOB.getShipperZones()!=null && masterDOB.getShipperZones().length>0 )
      ||(masterDOB.getConsigneeZones()!=null && masterDOB.getConsigneeZones().length>0) &&!"charges".equalsIgnoreCase(masterDOB.getQuoteWith()))
       finalDOB        = quoteDAO.getMultiQuoteCartagesForAdd(finalDOB); 
  //if(!"Freight".equalsIgnoreCase(masterDOB.getQuoteWith()))  
    finalDOB        = quoteDAO.getCharges(finalDOB);
    
    if(finalDOB!=null)
    {
      freightRates  = finalDOB.getLegDetails();
    }
   if(freightRates!=null)
   {
      freightRatesSize  = freightRates.size();//get the no fo legs since the freight rates size gives the no of legs
   }
    Hashtable spotRateDetails = null;
    ArrayList weightBreakSlabs = null;
    Iterator  spotRatesItr  = null;
    
    
    String weightBreak  = null;
    double[] rateDetail = null;
    
    int     spotRatesSize;
    int     weightBreakSlabSize;
    
    //get the charges for different legs
    //Added by Anil.k
    if(!"Charges".equalsIgnoreCase(masterDOB.getQuoteWith()))
     {//END
   	finalDOB  = quoteDAO.getLegRates(finalDOB);
      } //Added by Anil.k

    }
  catch(Exception e)
		{
    e.printStackTrace();
			//Logger.error(FILE_NAME,"QMSQuoteSessionBean[getFreightSellRates(masterDOB)] -> "+e.toString());
    logger.error(FILE_NAME+"QMSQuoteSessionBean[getFreightSellRates(masterDOB)] -> "+e.toString());
			throw new EJBException(e.toString());
		}
		finally
		{
		}
  return finalDOB;
}

public ArrayList getAttachmentDtls(MultiQuoteFinalDOB finalDOB)throws EJBException
{
    MultiQuoteAttachmentDOB attachmentDOB          = null;
    MultiQuoteMasterDOB     masterDOB              = null;   
    Connection         connection             = null;
    PreparedStatement  pst                    = null;
    ResultSet          rs                     = null;
    ArrayList          attachmentIdList       = new ArrayList();    
    String              quoteId              = null;
    String             viewAttachmentListQry  = "SELECT DISTINCT ATTACHMENT_ID FROM QMS_QUOTE_ATTACHMENTDTL WHERE QUOTE_ID IN (SELECT ID FROM QMS_QUOTE_MASTER QMS WHERE QMS.QUOTE_ID=? AND QMS.ACTIVE_FLAG='A')";
    try
    {
        masterDOB    = (MultiQuoteMasterDOB)finalDOB.getMasterDOB();
       // quoteId      = masterDOB.getUniqueId();
       // quoteId      = masterDOB.getUniqueIds()!= null ?masterDOB.getUniqueIds()[0]:0;@@ Commented by govind for not fetching the quoteid
        quoteId      = masterDOB.getQuoteId()!= null ?masterDOB.getQuoteId():"0";
        
        connection   = getConnection();
        pst          = connection.prepareStatement(viewAttachmentListQry);
        pst.setString(1,quoteId);
        rs          = pst.executeQuery();
        while(rs.next())
        {
            attachmentDOB  = new MultiQuoteAttachmentDOB();
            attachmentDOB.setAttachmentId(rs.getString("ATTACHMENT_ID"));
            attachmentIdList.add(attachmentDOB);
        }
    }
     catch(Exception e)
    {
         e.printStackTrace();
         logger.error("Exception in retreiving attachmentDOBList "+e);
         throw new EJBException(e);
    }
    finally
    {
         ConnectionUtil.closeConnection(connection,pst,rs);   
    }
    return attachmentIdList;
}

public ArrayList getAttachmentIdList(MultiQuoteFinalDOB finalDOB, String attachmentId)throws EJBException
{
   Connection                 connection             = null;
   PreparedStatement          pst                    = null;
   ResultSet                  rs                     = null;
   MultiQuoteMasterDOB             masterDOB         = null;
   MultiQuoteHeader                headerDOB         = null;
   MultiQuoteFreightLegSellRates   legRates          = null;
   MultiQuoteFreightRSRCSRDOB      freightDOB        = null;
   ArrayList                  legDetails             = new ArrayList();
   ArrayList                  freightRates           = null;
   ArrayList                  attachmentIdList       = new ArrayList();
   String                     quoteType              = null;
   String                     carrierId              = null;
   String                     orgLocation            = null;
   String                     orgCountryId           = null;
   String                     destCountryId          = null;
   String                     destLocation           = null;
   String                     attachmentIdListQuery  = null;

 
    try
   {
       masterDOB       = finalDOB.getMasterDOB();
       legDetails      = finalDOB.getLegDetails();
       headerDOB       = finalDOB.getHeaderDOB();
       if(finalDOB.getUpdatedReportDOB()!= null)
            quoteType = "U";
       else
            quoteType = "N";    
           int legDtlSize	=	legDetails.size();
              for(int i=0;i<legDtlSize;i++)
              {
                  legRates  = (MultiQuoteFreightLegSellRates)legDetails.get(i);
                  if(legRates!=null)
                     freightRates = (ArrayList)legRates.getRates();
              }
        if(freightRates!=null)
        {
          freightDOB = (MultiQuoteFreightRSRCSRDOB)freightRates.get(0);
          carrierId  =(String)freightDOB.getCarrierId();
            
        
        }
         connection  = getConnection();
         orgLocation  =  masterDOB.getOriginLocation()!= null?multiStringAppend(masterDOB.getOriginLocation()).trim():"";
         destLocation =  masterDOB.getDestLocation()!= null?multiStringAppend(masterDOB.getDestLocation()).trim():"";
         orgCountryId =  headerDOB.getOriginCountryId()!= null?multiStringAppend(headerDOB.getOriginCountryId()).trim():"";
         destCountryId=  headerDOB.getDestinationCountryId()!= null?multiStringAppend(headerDOB.getDestinationCountryId()).trim():"";
   attachmentIdListQuery  =   " SELECT DISTINCT QAD.ATTACHMENT_ID FROM QMS_ATTACHMENT_MASTER  QAM,QMS_ATTACHMENT_DTL QAD"+
		                      " WHERE  QAM.ATTACHMENT_ID=QAD.ATTACHMENT_ID AND QAM.INVALIDATE='F' AND QAM.TERMINAL_ID IN"+
		                      " ( SELECT ? TERM_ID FROM DUAL   UNION ALL"+
		                      " SELECT PARENT_TERMINAL_ID TERM_ID FROM FS_FR_TERMINAL_REGN CONNECT BY  CHILD_TERMINAL_ID="+ 
		                      " PRIOR PARENT_TERMINAL_ID START WITH CHILD_TERMINAL_ID=? UNION ALL SELECT TERMINALID"+
		                      " FROM FS_FR_TERMINALMASTER WHERE OPER_ADMIN_FLAG='H') AND QAD.DEFAULT_FLAG=? AND QAD.ATTACHMENT_ID IN"+
		                      " ( SELECT ATTACHMENT_ID FROM QMS_ATTACHMENT_DTL  WHERE FROM_LOCATION"+
		                      " IN ("+orgLocation+") OR FROM_LOCATION IS NULL INTERSECT SELECT ATTACHMENT_ID FROM"+
		                      " QMS_ATTACHMENT_DTL  WHERE TO_LOCATION IN ("+destLocation+") OR TO_LOCATION IS NULL"+
		                      " INTERSECT SELECT ATTACHMENT_ID  FROM QMS_ATTACHMENT_DTL WHERE FROM_COUNTRY IN ("+orgCountryId+")"+
		                      " OR FROM_COUNTRY IS NULL INTERSECT SELECT ATTACHMENT_ID FROM QMS_ATTACHMENT_DTL WHERE"+
		                      " TO_COUNTRY IN ("+destCountryId+") OR QAD.TO_COUNTRY IS NULL INTERSECT SELECT ATTACHMENT_ID FROM QMS_ATTACHMENT_DTL"+
		                      " WHERE CARRIER_ID=? OR CARRIER_ID IS NULL INTERSECT SELECT ATTACHMENT_ID FROM"+
		                      " QMS_ATTACHMENT_DTL WHERE SERVICE_LEVEL_ID=? OR SERVICE_LEVEL_ID IS NULL"+
		                      " INTERSECT SELECT ATTACHMENT_ID FROM QMS_ATTACHMENT_DTL WHERE QUOTE_TYPE=?"+
		                      " OR QUOTE_TYPE IS NULL INTERSECT SELECT ATTACHMENT_ID FROM QMS_ATTACHMENT_DTL"+
		                      " WHERE SHIPMENT_MODE =? OR SHIPMENT_MODE IS NULL INTERSECT SELECT ATTACHMENT_ID "+
		                      " FROM QMS_ATTACHMENT_DTL WHERE INDUSTRY_ID =? OR INDUSTRY_ID IS NULL) AND QAD.ATTACHMENT_ID LIKE ?";

         
         pst         = connection.prepareStatement(attachmentIdListQuery);
        
      
         pst.setString(1,masterDOB.getTerminalId()!= null?masterDOB.getTerminalId().trim():"");
         pst.setString(2,masterDOB.getTerminalId()!= null?masterDOB.getTerminalId().trim():"");
         pst.setString(3,finalDOB.getDefaultFlag()!= null?finalDOB.getDefaultFlag().trim():"");
        // pst.setString(4,masterDOB.getOriginLocation()!= null?multiStringAppend(masterDOB.getOriginLocation()).trim():"");
        // pst.setString(5,masterDOB.getDestLocation()!= null?multiStringAppend(masterDOB.getDestLocation()).trim():"");
        // pst.setString(6,headerDOB.getOriginCountryId()!= null?multiStringAppend(headerDOB.getOriginCountryId()).trim():"");
        // pst.setString(7,headerDOB.getDestinationCountryId()!= null?multiStringAppend(headerDOB.getDestinationCountryId()).trim():"");
         
         pst.setString(4,carrierId!=null?carrierId.trim():"");
         pst.setString(5,masterDOB.getServiceLevelId()!= null?masterDOB.getServiceLevelId().trim():"");
         pst.setString(6,quoteType!=null?quoteType.trim():"");
         pst.setInt(7,masterDOB.getShipmentMode());
         pst.setString(8,masterDOB.getIndustryId()!= null?masterDOB.getIndustryId().trim():"");
         pst.setString(9,attachmentId+"%");
         rs = pst.executeQuery();
         while(rs.next())
         {
            attachmentIdList.add((String)rs.getString("ATTACHMENT_ID"));
         }
   }  
    catch(Exception e)
    {
         
         e.printStackTrace();
         logger.error("Exception in retreiving attachmentIdList "+e);
         throw new EJBException(e);
   }
    finally
   {
         ConnectionUtil.closeConnection(connection,pst,rs);   
    }
    return attachmentIdList;
}
 

public String insertQuoteMasterDtls(MultiQuoteFinalDOB finalDOB) throws	EJBException //@@ Added by subrahmanyam for the enhancement #146971 on 03/12/2008
{
  Connection connection = null;
  PreparedStatement psmt  = null;
  ResultSet rs= null;
  MultiQuoteMasterDOB masterDOB  = null;
 
  String location = null;
  String quoteId = null;
  int i1=0;
  Integer I1=null;
  String[] quoteIds  = null;
  String[]                   quotes=null;
  int                        quoteLen=0;
  String                     quoteSub=null; 
  String                     quoteSub1=null;
  String                     quoteSub2=null; 
  try
  {
      
    masterDOB   = finalDOB.getMasterDOB();
    if(finalDOB!=null&& finalDOB.getUpdatedReportDOB()!=null)
    {
     quoteId = finalDOB.getUpdatedReportDOB().getQuoteId();
    }

   location   =   masterDOB.getTerminalId();
   connection  = this.getConnection();
   
    if(rs!=null)
      rs.close();
    if(psmt!=null)
      psmt.close();
   
    psmt  = connection.prepareStatement("SELECT QUOTE_MASTER_SEQ.NEXTVAL FROM DUAL");
    rs  = psmt.executeQuery();
    if(rs.next())
      masterDOB.setUniqueId(rs.getInt(1));
    

    
    finalDOB.setMasterDOB(masterDOB);
   

    QMSMultiQuoteEntityLocalHome localHome   = (QMSMultiQuoteEntityLocalHome)LookUpBean.getEJBLocalHome("java:comp/env/QMSMultiQuoteEntityBean");
    QMSMultiQuoteEntityLocal localRemote = (QMSMultiQuoteEntityLocal)localHome.create(finalDOB);
  }
  catch(SQLException sqEx)
		{
    sqEx.printStackTrace();
			//Logger.error(FILE_NAME,"QMSQuoteSessionBean[insertQuoteMasterDtls(masterDOB)] -> "+sqEx.toString());
    logger.error(FILE_NAME+"QMSMultiQuoteSessionBean[insertQuoteMasterDtls(masterDOB)] -> "+sqEx.toString());
			throw new EJBException(sqEx.toString());
		}
  catch(Exception e)
		{
    e.printStackTrace();
			//Logger.error(FILE_NAME,"QMSQuoteSessionBean[insertQuoteMasterDtls(masterDOB)] -> "+e.toString());
    logger.error(FILE_NAME+"QMSMultiQuoteSessionBean[insertQuoteMasterDtls(masterDOB)] -> "+e.toString());
			throw new EJBException(e.toString());
		}
		finally
		{
			try
			{
				ConnectionUtil.closeConnection(connection,psmt,rs);
			}
			catch(EJBException ex)
			{
				//Logger.error(FILE_NAME,"Finally : QMSQuoteSessionBean[insertQuoteMasterDtls(masterDOB)]-> "+ex.toString());
      logger.error(FILE_NAME+"Finally : QMSMultiQuoteSessionBean[insertQuoteMasterDtls(masterDOB)]-> "+ex.toString());
				throw new EJBException(ex.toString());
			}
		}
  return masterDOB.getQuoteId();
}

public MultiQuoteFinalDOB getQuoteContentDtl(MultiQuoteFinalDOB finalDOB) throws EJBException{


    Connection          conn        =   null;
    PreparedStatement   pstmt       =   null;
    ResultSet           rs          =   null;
    PreparedStatement   pstmt1       =   null;
    ResultSet           rs1          =   null;
    ArrayList           descList          =   null;
    ArrayList           hdrFtrList        =   null;
    ArrayList           alignList         =   null;
    ArrayList           defaultDescList   =   null;
    ArrayList           defaultHdrFtrList =   null;
    StringBuffer        terminalQry       =   null;
    String              quoteQry          =   "";
    String       salesPersonEmailQuery    =   "";//@@Added by kameswari for enhancements
    StringBuffer        contentQry        =   null;
    MultiQuoteMasterDOB      masterDOB         =   null;
    String              shipModeStr       =   "";
    String              operation         =   "";
     
    String[]            contentArray        = null;
    String[]            headerFooterArray   = null;
    String[]            alignArray          = null;
    
    String[]            defaultContentArray  = null;
    String[]            defaultHdrFooterArray= null;
    String                terminalIdQuery     ="SELECT TERMINAL_ID FROM QMS_QUOTE_MASTER WHERE QUOTE_ID=?";
    try
    {
      conn              =     this.getConnection();
      terminalQry       =     new StringBuffer("");
      contentQry        =     new StringBuffer("");
      descList          =     new ArrayList();
      hdrFtrList        =     new ArrayList();
      alignList         =     new ArrayList();
      defaultDescList   =     new ArrayList();
      defaultHdrFtrList =     new ArrayList(); 
      masterDOB         =     finalDOB.getMasterDOB();
      operation         =     masterDOB.getOperation();
     
      if("View".equalsIgnoreCase(operation))
      {
            pstmt1      =     conn.prepareStatement(terminalIdQuery);
            pstmt1.setString(1,masterDOB.getQuoteId());
            rs1         =     pstmt1.executeQuery();
            while(rs1.next())
            {
                  masterDOB.setTerminalId(rs1.getString("TERMINAL_ID"));   
            }
      }  
      quoteQry          =     "SELECT CM.DESCRIPTION,QCD.HEADER,QCD.ALIGN FROM QMS_CONTENTDTLS CM,QMS_QUOTE_HF_DTL QCD "+
                          "WHERE CM.CONTENTID=QCD.CONTENT AND QCD.QUOTE_ID = ? AND CM.FLAG ='F' ORDER BY HEADER DESC,CLEVEL";
      


      salesPersonEmailQuery ="SELECT EMAILID FROM FS_USERMASTER WHERE EMPID=?";
      
    
     // if("H".equalsIgnoreCase(masterDOB.getAccessLevel()))
      //@@ Commented & Added by subrahmanyam for the pbn id: 210886 on 12-Jul-10
      //if("DHLCORP".equalsIgnoreCase(masterDOB.getTerminalId()))
      if("DHLCORP".equalsIgnoreCase(masterDOB.getTerminalId()) || "DHLASPA".equalsIgnoreCase(masterDOB.getTerminalId()))
      {
      //  terminalQry.append( " (SELECT TERMINALID TERM_ID FROM FS_FR_TERMINALMASTER)");
      terminalQry.append( " (SELECT TERMINALID TERM_ID FROM FS_FR_TERMINALMASTER WHERE TERMINALID='"+masterDOB.getTerminalId()+"')");
      }
      else
      {
        terminalQry.append( "(SELECT PARENT_TERMINAL_ID TERM_ID FROM FS_FR_TERMINAL_REGN CONNECT BY CHILD_TERMINAL_ID = PRIOR PARENT_TERMINAL_ID START WITH CHILD_TERMINAL_ID = '")
                   .append(masterDOB.getTerminalId()).append("'")
                   .append( " UNION ")
                   .append( " SELECT TERMINALID TERM_ID FROM FS_FR_TERMINALMASTER WHERE OPER_ADMIN_FLAG = 'H' ")
                   .append( " UNION ")
                   .append( " SELECT '").append(masterDOB.getTerminalId()).append("' TERM_ID FROM DUAL ")
                   .append( " UNION ")
                   .append( " SELECT CHILD_TERMINAL_ID TERM_ID FROM FS_FR_TERMINAL_REGN CONNECT BY PRIOR CHILD_TERMINAL_ID = PARENT_TERMINAL_ID START WITH PARENT_TERMINAL_ID = '")
                   .append(masterDOB.getTerminalId()).append("')");
      }
      if(!finalDOB.isMultiModalQuote())
      {
        if(masterDOB.getShipmentMode()==1)
          shipModeStr = " AND SHIPMENTMODE IN (1,3,5,7)";
        else if(masterDOB.getShipmentMode()==2)
          shipModeStr = " AND SHIPMENTMODE IN (2,3,6,7)";
        else if(masterDOB.getShipmentMode()==4)
          shipModeStr = " AND SHIPMENTMODE IN (4,5,6,7)";
      }
      
      contentQry.append("SELECT DESCRIPTION,HEADERFOOTER,'L' ALIGN,(DECODE(ACCESSLEVEL,'H','A','A','H','O'))ACCESSLEVEL,FLAG FROM QMS_CONTENTDTLS WHERE ACTIVEINACTIVE='N' AND INVALIDATE='F' ")
                .append(shipModeStr).append("AND FLAG ='T' AND TEMINALID IN ").append(terminalQry)
               // .append(" ORDER  BY ACCESSLEVEL,CONTENTID"); commented by VLAKSHMI for issue 172986 on 10/6/2009
               .append(" ORDER  BY CONTENTID");
      
      pstmt   =   conn.prepareStatement(contentQry.toString());
      
      rs      =   pstmt.executeQuery();
      
      if("QuoteGrouping".equalsIgnoreCase(operation))
      {
        while(rs.next())
        {
          defaultDescList.add(rs.getString("DESCRIPTION"));
          defaultHdrFtrList.add(rs.getString("HEADERFOOTER"));
        }
      }
      else
      {
        while(rs.next())
        {
          descList.add(rs.getString("DESCRIPTION"));
          hdrFtrList.add(rs.getString("HEADERFOOTER"));
          alignList.add(rs.getString("ALIGN"));//@@Default Left Aligned
        }
      }
      
      if(rs!=null)
        rs.close();
      if(pstmt!=null)
        pstmt.close();
      
      pstmt         =     conn.prepareStatement(quoteQry);
      
      pstmt.setLong(1,masterDOB.getUniqueId());
      
      rs            =     pstmt.executeQuery();
      
      while(rs.next())
      {
        descList.add(rs.getString("DESCRIPTION"));
        hdrFtrList.add(rs.getString("HEADER"));
        alignList.add(rs.getString("ALIGN"));
      }
      
      if(descList.size()>0)
      {
        contentArray            = new String[descList.size()];
        headerFooterArray       = new String[hdrFtrList.size()];
        alignArray              = new String[alignList.size()];
      }
     int desListSize	=	descList.size();
      for(int i=0;i<desListSize;i++)
      {
        contentArray[i]       = (String)descList.get(i);
        headerFooterArray[i]  = (String)hdrFtrList.get(i);
        alignArray[i]         = (String)alignList.get(i);
      }
      
      int defaultContentSize  = defaultDescList.size();
      
      if(defaultContentSize >0)
      {
        defaultContentArray   =   new String[defaultContentSize];
        defaultHdrFooterArray =   new String[defaultContentSize];
      }
      
      for(int i=0;i<defaultContentSize;i++)
      {
        defaultContentArray[i]   =   (String)defaultDescList.get(i);
        defaultHdrFooterArray[i] =   (String)defaultHdrFtrList.get(i);
      }
       if(rs!=null)
        rs.close();
      if(pstmt!=null)
        pstmt.close();
      if("Y".equalsIgnoreCase(masterDOB.getSalesPersonFlag()))
       {
          pstmt = conn.prepareStatement(salesPersonEmailQuery);
          pstmt.setString(1,masterDOB.getSalesPersonCode());
          rs  = pstmt.executeQuery();
          if(rs.next())
          {
              masterDOB.setSalesPersonEmail(rs.getString("EMAILID"));
          }
       }   
      masterDOB.setHeaderFooter(headerFooterArray); 
      masterDOB.setContentOnQuote(contentArray);
      masterDOB.setAlign(alignArray);
      
      masterDOB.setDefaultContent(defaultContentArray);
      masterDOB.setDefaultHeaderFooter(defaultHdrFooterArray);
      
      finalDOB.setMasterDOB(masterDOB);
    
      
   
 
    }
    catch(EJBException ejb)
    {
      //Logger.error(FILE_NAME,"EJBException while fetching Quote Content Header Footer Details "+ejb);
      logger.error(FILE_NAME+"EJBException while fetching Quote Content Header Footer Details "+ejb);
      ejb.printStackTrace();
      throw new EJBException(ejb);
    }
    catch(Exception e)
    {
      //Logger.error(FILE_NAME,"Exception while fetching Quote Content Header Footer Details "+e);
      logger.error(FILE_NAME+"Exception while fetching Quote Content Header Footer Details "+e);
      e.printStackTrace();
      throw new EJBException(e);
    }
    finally
    {
      ConnectionUtil.closeConnection(conn,pstmt,rs);
      ConnectionUtil.closeConnection(conn,pstmt1,rs1);
    }
    return finalDOB;
  

}

public ArrayList getQuoteAttachmentDtls(MultiQuoteFinalDOB finalDOB)throws EJBException
{

	
    Connection                 conn                 = null;
    PreparedStatement          pst                  = null;
    CallableStatement       cstmt                  = null;
    ResultSet                  rs                   = null;
    MultiQuoteAttachmentDOB         attachmentDOB        = null;
    MultiQuoteMasterDOB             masterDOB            = null;
    MultiQuoteHeader                headerDOB            = null;
    MultiQuoteFreightLegSellRates   legRates             = null;
    MultiQuoteFreightRSRCSRDOB      freightDOB           = null;
    MultiQuoteChargeInfo                chargesDOB          = null;
    ArrayList                  defaultIdList        = new ArrayList();
    ArrayList                  attachmentIdList     = null;
    ArrayList                  legDetails           = new ArrayList();
    ArrayList                  freightRates         = null;
     ArrayList                  chargesList         = null;
    ArrayList                  attachmentDOBList    = new ArrayList();
     byte                      fileBytes[]          = null; 
    String                     filebyte             = null;
    Blob                       fileBlob             = null;
    String                     quoteType            = null;
    String                     carrierId            = null;
    String                     org                  = null;
    String                     dest                 = null;
    String                     consoleType          = null;
    String                     service_Level        = null;
    String                     shipment_mode        = null;
    long                       quoteId;
    String                     nondefaultFileQry    = "SELECT PDF_FILENAME,PDF_FILE FROM QMS_ATTACHMENT_FILEDTL WHERE ATTACHMENT_ID=? ORDER BY PDF_FILENAME";
 
		  String                    defaultFileQry  =  "SELECT PDF_FILE ,PDF_FILENAME FROM QMS_ATTACHMENT_FILEDTL WHERE ATTACHMENT_ID IN"+ 
											          "(SELECT QAM.ATTACHMENT_ID FROM QMS_ATTACHMENT_MASTER  QAM,"+
											          " QMS_ATTACHMENT_DTL QAD,QMS_ATTACHMENT_FILEDTL QAF WHERE  QAM.ATTACHMENT_ID="+
											          " QAD.ATTACHMENT_ID AND QAM.ATTACHMENT_ID=QAF.ATTACHMENT_ID "+
											          " AND QAM.INVALIDATE='F' AND QAM.TERMINAL_ID IN ("+
											          " SELECT ? TERM_ID FROM DUAL  UNION ALL"+
											          " SELECT PARENT_TERMINAL_ID TERM_ID FROM FS_FR_TERMINAL_REGN CONNECT BY  CHILD_TERMINAL_ID="+
											          " PRIOR PARENT_TERMINAL_ID START WITH CHILD_TERMINAL_ID=? UNION ALL SELECT TERMINALID"+
											          " FROM FS_FR_TERMINALMASTER WHERE OPER_ADMIN_FLAG='H') AND QAD.DEFAULT_FLAG=? AND QAD.ATTACHMENT_ID"+
											          " IN ( SELECT ATTACHMENT_ID FROM QMS_ATTACHMENT_DTL WHERE FROM_LOCATION  IN ?"+
											          " OR FROM_LOCATION IS NULL INTERSECT SELECT ATTACHMENT_ID FROM QMS_ATTACHMENT_DTL"+
											          " WHERE TO_LOCATION IN ? OR QAD.TO_LOCATION IS NULL INTERSECT SELECT ATTACHMENT_ID FROM"+
											          " QMS_ATTACHMENT_DTL WHERE FROM_COUNTRY IN (?) OR FROM_COUNTRY IS NULL INTERSECT SELECT ATTACHMENT_ID"+
											          " FROM QMS_ATTACHMENT_DTL WHERE TO_COUNTRY IN (?) OR TO_COUNTRY IS NULL INTERSECT SELECT ATTACHMENT_ID"+
											          " FROM QMS_ATTACHMENT_DTL WHERE CARRIER_ID IN ? OR CARRIER_ID IS NULL INTERSECT SELECT ATTACHMENT_ID"+
											          " FROM QMS_ATTACHMENT_DTL WHERE SERVICE_LEVEL_ID  IN ? OR SERVICE_LEVEL_ID IS NULL INTERSECT"+
											          " SELECT ATTACHMENT_ID FROM QMS_ATTACHMENT_DTL WHERE QUOTE_TYPE=? OR QUOTE_TYPE IS NULL INTERSECT"+
											          " SELECT ATTACHMENT_ID FROM QMS_ATTACHMENT_DTL WHERE SHIPMENT_MODE  IN ? OR SHIPMENT_MODE IS NULL"+
											          " INTERSECT SELECT ATTACHMENT_ID  FROM QMS_ATTACHMENT_DTL WHERE INDUSTRY_ID =? OR INDUSTRY_ID IS NULL"+
											         " INTERSECT SELECT ATTACHMENT_ID  FROM QMS_ATTACHMENT_DTL WHERE CONSOLE_TYPE IN ? OR CONSOLE_TYPE IS NULL)) ORDER BY PDF_FILENAME";  //@@Modified  by kameswari on 19/02/09
											          

 
   String                  uncompressedFilequery  = "{?=call UTL_COMPRESS.LZ_UNCOMPRESS(?)}";
    try
    {
      conn     = getConnection();
      masterDOB  = finalDOB.getMasterDOB();
      headerDOB  = finalDOB.getHeaderDOB();
      legDetails  = finalDOB.getLegDetails();
   
      attachmentIdList = (ArrayList)finalDOB.getAttachmentDOBList();
       finalDOB.setDefaultFlag("Y");
      if(finalDOB.getUpdatedReportDOB()!= null)
           quoteType = "U";
      else
           quoteType = "N";    
          
      if(legDetails!=null)
      {
     	 int legDtlSize	=	legDetails.size();
       for(int j=0;j<legDtlSize;j++)
       {
           legRates  = (MultiQuoteFreightLegSellRates)legDetails.get(j);
              if(legRates.getShipmentMode()==2)
          {
                 if("List".equalsIgnoreCase(legRates.getSpotRatesType()))
                 {
                   legRates.setConsoleType("FCL");
                 }
                 else
                 {
                   legRates.setConsoleType("LCL");
                 }
         }

           if(legRates!=null)
           {
              freightRates = legRates.getRates();
              chargesList  =  legRates.getFreightChargesList();

                if(chargesList!=null && chargesList.size()>0)
                {
                     chargesDOB  =  (MultiQuoteChargeInfo)chargesList.get(0);
                }
           }
           if(freightRates!=null && freightRates.size()>0)
           {
             freightDOB = (MultiQuoteFreightRSRCSRDOB)freightRates.get(0);


             carrierId  = (String)freightDOB.getCarrierId();
           }
         //@@ Added by govind for the issue 262166  
          if(j==0){
           org =  (legRates.getOrigin()!= null?"'"+legRates.getOrigin()+"',":"");
           dest = (legRates.getDestination()!= null?"'"+legRates.getDestination()+"',":"");
           consoleType = (legRates.getConsoleType()!=null?"'"+legRates.getConsoleType()+"',":"");
           service_Level = (legRates.getServiceLevel()!=null?"'"+legRates.getServiceLevel()+"',":"");
          // shipment_mode = (legRates.getShipmentMode()+",");
          }
          else if(j==legDtlSize-1){
           org =  (legRates.getOrigin()!= null?org+"'"+legRates.getOrigin()+"'":"");
           dest = (legRates.getDestination()!= null?dest+"'"+legRates.getDestination()+"'":"");
           consoleType = (legRates.getConsoleType()!=null?consoleType+"'"+legRates.getConsoleType()+"'":"");
           service_Level = (legRates.getServiceLevel()!=null?service_Level+"'"+legRates.getServiceLevel()+"'":"");
         //  shipment_mode = (shipment_mode+legRates.getShipmentMode());
          }
          else{
           org =  (legRates.getOrigin()!= null?org+"'"+legRates.getOrigin()+"',":"");
           dest = (legRates.getDestination()!= null?dest+"'"+legRates.getDestination()+"',":"");
           consoleType = (legRates.getConsoleType()!=null?consoleType+"'"+legRates.getConsoleType()+"',":"");
           service_Level = (legRates.getServiceLevel()!=null?service_Level+"'"+legRates.getServiceLevel()+"',":"");
          // shipment_mode = (shipment_mode+legRates.getShipmentMode()+",");
           
          }
       }
      // conn     = getConnection();
       pst      = conn.prepareStatement(nondefaultFileQry);
      if(attachmentIdList!=null)
     {
     	 int attachMentListSize	=	attachmentIdList.size();
       for(int i=0;i<attachMentListSize;i++)
      {
         attachmentDOB   = (MultiQuoteAttachmentDOB)attachmentIdList.get(i);
         pst.setString(1,attachmentDOB.getAttachmentId());
         rs = pst.executeQuery();
         while(rs.next())
         {

           attachmentDOB   = new MultiQuoteAttachmentDOB();
           fileBlob        =  rs.getBlob("PDF_FILE");
             cstmt         = conn.prepareCall(uncompressedFilequery);
             cstmt.registerOutParameter(1,OracleTypes.BLOB);
             cstmt.setBlob(2,fileBlob);
             cstmt.execute();
            fileBlob  =  cstmt.getBlob(1);
            fileBytes  =  fileBlob.getBytes(1,(int)fileBlob.length());
           attachmentDOB.setPdfFile(fileBytes);
           attachmentDOB.setFileName(rs.getString("PDF_FILENAME"));
           attachmentDOB.setTerminalId(masterDOB.getTerminalId());
           attachmentDOB.setUserId(masterDOB.getUserId());
           attachmentDOBList.add(attachmentDOB);
         }
      }
     }
        if(rs!=null)
         rs.close();
        if(pst!=null)
         pst.close();
        if(cstmt!=null)
         cstmt.close();
        pst         = conn.prepareStatement(defaultFileQry);

        pst.setString(1,masterDOB.getTerminalId());
        pst.setString(2,masterDOB.getTerminalId());
        pst.setString(3,finalDOB.getDefaultFlag());
        pst.setString(4,org); //@@ Added by govind for the issue 262166  
        pst.setString(5,dest); //@@ Added by govind for the issue 262166  
        pst.setString(6,multiStringAppend(headerDOB.getOriginCountryId()));
        pst.setString(7,multiStringAppend(headerDOB.getDestinationCountryId()));
        pst.setString(8,carrierId); //@@ Added by govind for the issue 262166  
        pst.setString(9,service_Level); //@@ Added by govind for the issue 262166  
        pst.setString(10,quoteType);
        pst.setInt(11,masterDOB.getShipmentMode()); //@@ Added by govind for the issue 262166  
        pst.setString(12,masterDOB.getIndustryId());
          if("Y".equalsIgnoreCase(finalDOB.getSpotRatesFlag()))
        {
              pst.setString(13,consoleType);
        }
        else
        {
        	if(chargesDOB!=null)
        		pst.setString(13,chargesDOB.getConsoleType()!=null?chargesDOB.getConsoleType():"");
        	else
        	pst.setString(13,"");
        }


          rs = pst.executeQuery();
        while(rs.next())
        {
	        	String pdfFileName = attachmentDOB!=null?attachmentDOB.getFileName():"";
	        	if(!"General Conditions.pdf".equalsIgnoreCase(pdfFileName ))
	        	{
           attachmentDOB   = new MultiQuoteAttachmentDOB();
           fileBlob        =  rs.getBlob("PDF_FILE");
            cstmt         = conn.prepareCall(uncompressedFilequery);
            cstmt.registerOutParameter(1,OracleTypes.BLOB);
            cstmt.setBlob(2,fileBlob);
            cstmt.execute();
            fileBlob  =  cstmt.getBlob(1);
           fileBytes       =  fileBlob.getBytes(1,(int)fileBlob.length());
           attachmentDOB.setPdfFile(fileBytes);
           attachmentDOB.setFileName(rs.getString("PDF_FILENAME"));
           attachmentDOB.setTerminalId(masterDOB.getTerminalId());
           attachmentDOB.setUserId(masterDOB.getUserId());
           attachmentDOBList.add(attachmentDOB);
        }
	        }

     
      }
    }

    catch(Exception e)
   {
        e.printStackTrace();
        logger.error("Exception in retreiving attachmentDOBList "+e);
        throw new EJBException(e);
   }
   finally
   {
        ConnectionUtil.closeConnection(conn,pst,rs);   
		  if(cstmt!=null){  //@@Added by Kameswari for the WPBN issue - on 02/02/2012
          try{
			  cstmt.close();
          }catch(Exception e){e.printStackTrace();}
		  }
   }
   return attachmentDOBList;

}

public StringBuffer validateQuoteId(MultiQuoteMasterDOB masterDOB) throws	EJBException
{ 
	  
  Connection        connection   = null;
  PreparedStatement pstmt        = null;
	ResultSet         rs           = null;
	boolean           validFlag    = false;
  StringBuffer      errorMessage = new StringBuffer("");
  int               count        = 0;
  StringBuffer      sql          = new StringBuffer("");
  StringBuffer      terminalQuery= new StringBuffer("");
  String            quoteId      = null;    //@@ Added by subrahmanyam for the enhancement #146971 on 03/12/2008
  String            customerId   = null;
  String            origin       = null;  
  String            destination  = null;
  String            terminalId   = null;
  String            empId        = null;
  String            basisFlag    = null;
  String            operation    = null;
  String            accessLevel  = null;
  String            sqlQuery     = null;
  String            subQuery     = "";
  String            locSubQuery  = "";
  boolean           isOrigin     = false;
  boolean           isCustomer   = false;
  int               shipmentMode = 0;
  String			  customerName = "";
  String			  quoteStatus  = "";
  String			  quoteActive  = "";
  String			  shipModeStr  = null;
  
		try
		{
			
    quoteId       =     masterDOB.getQuoteId().trim();//Added by Rakesh on 25-02-2011 for Issue:236363 
    customerId    =     masterDOB.getCustomerId();
    origin        =     masterDOB.getOriginLocation()[0].trim();//Added by Rakesh on 25-02-2011 for Issue:236363 
    destination   =     masterDOB.getDestLocation()[0].trim();//Added by Rakesh on 25-02-2011 for Issue:236363 
    terminalId    =     masterDOB.getTerminalId();
    accessLevel   =     masterDOB.getAccessLevel();
    empId         =     masterDOB.getUserId();
    basisFlag     =     masterDOB.getBuyRatesPermission();
    operation     =     masterDOB.getOperation();
    customerName  =     masterDOB.getCompanyName();
    quoteStatus   =     masterDOB.getQuoteStatus();
    quoteActive =  masterDOB.getActiveFlag();
    if(masterDOB.getShipmentMode()==1)
        shipModeStr = "1,3,5,7";
      else if(masterDOB.getShipmentMode()==2)
        shipModeStr = "2,3,6,7";
      else if(masterDOB.getShipmentMode()==4)
        shipModeStr = "4,5,6,7";
    if("H".equalsIgnoreCase(accessLevel))
    {
        sql.append("(SELECT TERMINALID FROM FS_FR_TERMINALMASTER)");
        terminalQuery.append("(SELECT TERMINALID FROM FS_FR_TERMINALMASTER)");
    }
    else
    {
        if(!"Modify".equalsIgnoreCase(operation) && !"Copy".equalsIgnoreCase(operation))
        {
         


          sql.append( "(SELECT CHILD_TERMINAL_ID FROM FS_FR_TERMINAL_REGN WHERE  PARENT_TERMINAL_ID =(SELECT PARENT_TERMINAL_ID FROM FS_FR_TERMINAL_REGN WHERE CHILD_TERMINAL_ID='")
                    .append(terminalId).append("' )UNION SELECT PARENT_TERMINAL_ID TERM_ID FROM FS_FR_TERMINAL_REGN CONNECT BY CHILD_TERMINAL_ID = PRIOR PARENT_TERMINAL_ID START WITH CHILD_TERMINAL_ID = '")
                    .append(terminalId).append("'")
                    .append( " UNION ")
                    .append( " SELECT TERMINALID TERM_ID FROM FS_FR_TERMINALMASTER WHERE OPER_ADMIN_FLAG = 'H' ")
                    .append( " UNION ");
        }

        else
          sql.append("(");
          
   if("A".equalsIgnoreCase(accessLevel))
    {
        sql.append(" SELECT CHILD_TERMINAL_ID TERM_ID FROM FS_FR_TERMINAL_REGN CONNECT BY PRIOR CHILD_TERMINAL_ID = PARENT_TERMINAL_ID ");
        sql.append("START WITH PARENT_TERMINAL_ID = '").append(terminalId).append("' UNION SELECT TERMINALID FROM FS_FR_TERMINALMASTER WHERE TERMINALID='").append(terminalId).append("')");
    }
    else
    {
          sql.append(" SELECT CHILD_TERMINAL_ID TERM_ID FROM FS_FR_TERMINAL_REGN CONNECT BY PRIOR CHILD_TERMINAL_ID = PARENT_TERMINAL_ID ");
          sql.append("START WITH PARENT_TERMINAL_ID = '").append(terminalId).append("' UNION SELECT TERMINALID FROM FS_FR_TERMINALMASTER WHERE TERMINALID='").append(terminalId).append("'");
          sql.append("UNION SELECT CHILD_TERMINAL_ID FROM FS_FR_TERMINAL_REGN FRT WHERE PARENT_TERMINAL_ID IN (SELECT PARENT_TERMINAL_ID FROM FS_FR_TERMINAL_REGN FRT WHERE FRT.CHILD_TERMINAL_ID='").append(terminalId).append("'))");
        
    }
        
        terminalQuery.append( "(SELECT PARENT_TERMINAL_ID TERM_ID FROM FS_FR_TERMINAL_REGN CONNECT BY CHILD_TERMINAL_ID = PRIOR PARENT_TERMINAL_ID START WITH CHILD_TERMINAL_ID = '").append(terminalId).append("'")
                 .append( " UNION ")
                 .append( " SELECT TERMINALID TERM_ID FROM FS_FR_TERMINALMASTER WHERE OPER_ADMIN_FLAG = 'H' ")
                 .append( " UNION ")
                 .append( " SELECT '").append(terminalId).append("' TERM_ID FROM DUAL ")
                 .append( " UNION ")
                 .append( " SELECT CHILD_TERMINAL_ID TERM_ID FROM FS_FR_TERMINAL_REGN CONNECT BY PRIOR CHILD_TERMINAL_ID = PARENT_TERMINAL_ID START WITH PARENT_TERMINAL_ID = '").append(terminalId).append("')");
    }
    
    if("Modify".equalsIgnoreCase(operation))
    {
        sqlQuery      = "SELECT COUNT(*)NO_ROWS FROM QMS_QUOTE_MASTER WHERE QUOTE_ID=? AND ACTIVE_FLAG='A' AND( TERMINAL_ID IN "+sql.toString()+"  or sales_person ='"+empId+"')";
   
    }
    else if("Copy".equalsIgnoreCase(operation) || "View".equalsIgnoreCase(operation))
    {
      sqlQuery      = "SELECT COUNT(*)NO_ROWS FROM QMS_QUOTE_MASTER WHERE QUOTE_ID=? AND ((QUOTE_STATUS IN ('ACC', 'NAC') AND ACTIVE_FLAG IN ('A', 'I')) OR " +
                "(QUOTE_STATUS NOT IN ('ACC', 'NAC') AND ACTIVE_FLAG = 'A'))  AND TERMINAL_ID IN "+sql.toString();
    }
    
    
    connection    =   this.getConnection();
          
    pstmt         = connection.prepareStatement(sqlQuery);
    pstmt.setString(1,quoteId.trim());  //Added by Rakesh on 25-02-2011 for Issue:236363 
    rs            = pstmt.executeQuery();
    
    if(rs.next())
        count = rs.getInt("NO_ROWS");
    
    if(count==0)
        errorMessage.append("Quote Id is Invalid. Please Enter a Valid & Active Quote Id to Continue.<BR>");
    else
    {
      if(rs!=null)
        rs.close();
      if(pstmt!=null)
        pstmt.close();
      
      count = 0;
      
      if("View".equalsIgnoreCase(operation))
      {
        
    sqlQuery      =  "SELECT COUNT(*)ROWS_NO FROM QMS_QUOTE_MASTER WHERE QUOTE_ID=? AND ((QUOTE_STATUS IN ('ACC', 'NAC') AND ACTIVE_FLAG IN ('A', 'I')) OR " +
                            "(QUOTE_STATUS NOT IN ('ACC', 'NAC') AND ACTIVE_FLAG = 'A')) AND COMPLETE_FLAG='C'";

          pstmt         = connection.prepareStatement(sqlQuery);
          pstmt.setString(1,quoteId);   
          rs            = pstmt.executeQuery();
          if(rs.next())
          count = rs.getInt("ROWS_NO");
          if(count==0)
          errorMessage.append("The Selected Quote Is Incomplete. Please Enter/Select a Valid Quote Id.<BR>");
      }
      if(rs!=null)
        rs.close();
      if(pstmt!=null)
        pstmt.close();
      count = 0;
      
     if((!"".equals(shipModeStr) && shipModeStr!=null))
      {
          sqlQuery   = " SELECT COUNT(*)NO_ROWS FROM FS_RT_PLAN A,FS_RT_LEG B WHERE A.QUOTE_ID= ? AND "+
                       " A.RT_PLAN_ID=B.RT_PLAN_ID AND B.SHPMNT_MODE IN("+shipModeStr+") ";
          
          pstmt     = connection.prepareStatement(sqlQuery);
          pstmt.setString(1,quoteId);
          rs        = pstmt.executeQuery();
          if(rs.next())
              count = rs.getInt("NO_ROWS");
          
          if(count==0)
            errorMessage.append("Please Enter A Valid Quote Id For the Selected Shipment Mode.<BR>");
          
          if(rs!=null)
            rs.close();
          if(pstmt!=null)
            pstmt.close();
          count = 0;
      }
      
             if(customerName!=null && customerName.trim().length()!=0)
      {
          isCustomer  = true;
         sqlQuery  = "SELECT COUNT(*)NO_ROWS FROM FS_FR_CUSTOMERMASTER WHERE COMPANYNAME=? AND CUSTOMERID=?";
          pstmt     = connection.prepareStatement(sqlQuery);
          pstmt.setString(1,customerName);
          pstmt.setString(2,customerId);
          rs        = pstmt.executeQuery();
          if(rs.next())
              count = rs.getInt("NO_ROWS");
          
          if(count==0)
            errorMessage.append("COMPANYNAME is Invalid.<BR>");          
          else
          {
            if(rs!=null)
              rs.close();
            if(pstmt!=null)
              pstmt.close();
            count = 0;
         if("NAC".equalsIgnoreCase(quoteStatus) || "ACC".equalsIgnoreCase(quoteStatus))  
         {
             sqlQuery  = "SELECT COUNT(*)NO_ROWS FROM QMS_QUOTE_MASTER WHERE QUOTE_ID=? AND CUSTOMER_ID=?  AND TERMINAL_ID IN "+sql.toString();
            pstmt     = connection.prepareStatement(sqlQuery);
            pstmt.setString(1,quoteId);
            pstmt.setString(2,customerId);
            rs        = pstmt.executeQuery();
         }
         else{
            sqlQuery  = "SELECT COUNT(*)NO_ROWS FROM QMS_QUOTE_MASTER WHERE QUOTE_ID=? AND CUSTOMER_ID=? AND ACTIVE_FLAG='A' AND TERMINAL_ID IN "+sql.toString();
            pstmt     = connection.prepareStatement(sqlQuery);
            pstmt.setString(1,quoteId);
            pstmt.setString(2,customerId);
            rs        = pstmt.executeQuery();
         }
            if(rs.next())
              count = rs.getInt("NO_ROWS");
          
            if(count==0)
              errorMessage.append("Quote Id ").append(quoteId).append(" & COMPANYNAME ").append(customerName).append(" Do Not Match.<BR>");
          }
      }
              if(quoteStatus!=null && quoteStatus.trim().length()!=0)
      {
       if("NAC".equalsIgnoreCase(quoteStatus) || "ACC".equalsIgnoreCase(quoteStatus))  
         {
           sqlQuery  = "SELECT COUNT(*)NO_ROWS FROM QMS_QUOTE_MASTER WHERE QUOTE_ID=? AND QUOTE_STATUS=?  AND TERMINAL_ID IN "+sql.toString();
            pstmt     = connection.prepareStatement(sqlQuery);
            pstmt.setString(1,quoteId);
            pstmt.setString(2,quoteStatus);
            rs        = pstmt.executeQuery();
         }
         else{
          sqlQuery  = "SELECT COUNT(*)NO_ROWS FROM QMS_QUOTE_MASTER WHERE QUOTE_ID=? AND QUOTE_STATUS=? AND ACTIVE_FLAG='A' AND TERMINAL_ID IN "+sql.toString();
            pstmt     = connection.prepareStatement(sqlQuery);
            pstmt.setString(1,quoteId);
            pstmt.setString(2,quoteStatus);
            rs        = pstmt.executeQuery();
         }
            if(rs.next())
              count = rs.getInt("NO_ROWS");
          
            if(count==0)
              errorMessage.append("Quote Id ").append(quoteId).append(" &STATUS ").append(quoteStatus).append(" Do Not Match.<BR>");
        
      }
      
        if(quoteActive!=null && quoteActive.trim().length()!=0)
      {
      
          sqlQuery  = "SELECT COUNT(*)NO_ROWS FROM QMS_QUOTE_MASTER WHERE QUOTE_ID=? AND ACTIVE_FLAG=? AND TERMINAL_ID IN "+sql.toString();
            pstmt     = connection.prepareStatement(sqlQuery);
            pstmt.setString(1,quoteId);
            pstmt.setString(2,quoteActive);
            rs        = pstmt.executeQuery();
            if(rs.next())
              count = rs.getInt("NO_ROWS");
          
            if(count==0)
              errorMessage.append("Quote Id ").append(quoteId).append(" &ACTIVEFLAG ").append(quoteActive).append(" Do Not Match.<BR>");
        
      }
   
   
      
      if(origin!=null && origin.trim().length()!=0 )
      {
          if(rs!=null)
              rs.close();
          if(pstmt!=null)
              pstmt.close();
          count = 0;
          isOrigin  = true;
          sqlQuery  = "SELECT COUNT(*)NO_ROWS FROM  FS_FR_LOCATIONMASTER WHERE LOCATIONID =? AND (INVALIDATE ='F' OR INVALIDATE IS NULL) ";
          pstmt     = connection.prepareStatement(sqlQuery);
          pstmt.setString(1,origin);
          rs        = pstmt.executeQuery();
          
          if(rs.next())
            count = rs.getInt("NO_ROWS");
          if(count  == 0)
            errorMessage.append("Origin Location is Invalid.<BR>");
          else
          {
            if(rs!=null)
              rs.close();
            if(pstmt!=null)
              pstmt.close();
            count = 0;
            if("NAC".equalsIgnoreCase(quoteStatus) || "ACC".equalsIgnoreCase(quoteStatus))  
         {
          sqlQuery  = "SELECT COUNT(*)NO_ROWS FROM QMS_QUOTE_MASTER WHERE QUOTE_ID=?  AND ORIGIN_LOCATION=? AND TERMINAL_ID IN "+sql.toString();
            pstmt     =  connection.prepareStatement(sqlQuery);
            pstmt.setString(1,quoteId);
            pstmt.setString(2,origin);
            rs  = pstmt.executeQuery();
         }else{
            sqlQuery  = "SELECT COUNT(*)NO_ROWS FROM QMS_QUOTE_MASTER WHERE QUOTE_ID=? AND ACTIVE_FLAG='A' AND ORIGIN_LOCATION=? AND TERMINAL_ID IN "+sql.toString();
            pstmt     =  connection.prepareStatement(sqlQuery);
            pstmt.setString(1,quoteId);
            pstmt.setString(2,origin);
            rs  = pstmt.executeQuery();
         }  
            if(rs.next())
              count = rs.getInt("NO_ROWS");
            
            if(count==0)
              errorMessage.append("Quote Id ").append(quoteId).append(" & Origin Location ").append(origin).append(" Do Not Match.<BR>");
          }
      }
      
      if(destination!=null && destination.trim().length()!=0)
      {
          if(rs!=null)
            rs.close();
          if(pstmt!=null)
            pstmt.close();
          count = 0;
            
          sqlQuery  = "SELECT COUNT(*)NO_ROWS FROM  FS_FR_LOCATIONMASTER WHERE LOCATIONID =? AND (INVALIDATE ='F' OR INVALIDATE IS NULL) ";
          pstmt     = connection.prepareStatement(sqlQuery);
          pstmt.setString(1,destination);
          rs        = pstmt.executeQuery();
          
          if(rs.next())
            count = rs.getInt("NO_ROWS");
          if(count  == 0)
            errorMessage.append("Destination Location is Invalid.<BR>");
          else
          {
            if(rs!=null)
              rs.close();
            if(pstmt!=null)
              pstmt.close();
            count = 0;
               if("NAC".equalsIgnoreCase(quoteStatus) || "ACC".equalsIgnoreCase(quoteStatus))  
         {
           sqlQuery  = "SELECT COUNT(*)NO_ROWS FROM QMS_QUOTE_MASTER WHERE QUOTE_ID=?  AND DEST_LOCATION=? AND TERMINAL_ID IN "+sql.toString();
            pstmt     =  connection.prepareStatement(sqlQuery);
            pstmt.setString(1,quoteId);
            pstmt.setString(2,destination);              
            rs  = pstmt.executeQuery();
         }else{
            sqlQuery  = "SELECT COUNT(*)NO_ROWS FROM QMS_QUOTE_MASTER WHERE QUOTE_ID=? AND ACTIVE_FLAG='A' AND DEST_LOCATION=? AND TERMINAL_ID IN "+sql.toString();
            pstmt     =  connection.prepareStatement(sqlQuery);
            pstmt.setString(1,quoteId);
            pstmt.setString(2,destination);              
            rs  = pstmt.executeQuery();
         }  
            if(rs.next())
              count = rs.getInt("NO_ROWS");
            
            if(count==0)
              errorMessage.append("Quote Id ").append(quoteId).append(" & Destination Location ").append(destination).append(" Do Not Match.<BR>");
          }
      }
     
      
      if("Modify".equalsIgnoreCase(operation))
      {
        
        if(rs!=null)
          rs.close();
        if(pstmt!=null)
          pstmt.close();
        count = 0;
        
     
            sqlQuery  = "SELECT COUNT(*)NO_ROWS FROM QMS_QUOTE_MASTER QM,QMS_QUOTES_UPDATED QU WHERE QM.QUOTE_ID=? AND QM.ID=QU.QUOTE_ID AND QU.CONFIRM_FLAG IS NULL";
         pstmt     = connection.prepareStatement(sqlQuery);
         pstmt.setString(1,quoteId); 
         rs        = pstmt.executeQuery();
        
        if(rs.next())
          count = rs.getInt("NO_ROWS");
        if(count > 0)
          errorMessage.append("This Quote Has Been Updated. Please Confirm this Quote Using the Updated Quotes Report.<BR>");
        
        if(rs!=null)
          rs.close();
        if(pstmt!=null)
          pstmt.close();
          

        sqlQuery  = "SELECT UM.USERNAME FROM QMS_QUOTE_MASTER QM,FS_USERMASTER UM WHERE QUOTE_ID=? AND QM.ESCALATED_TO =UM.EMPID AND  ACTIVE_FLAG='A' AND ESCALATION_FLAG='Y' AND ESCALATED_TO <> ?";
        pstmt     = connection.prepareStatement(sqlQuery);
        pstmt.setString(1,quoteId);
        pstmt.setString(2,empId);
        rs        = pstmt.executeQuery();
        
        if(rs.next())
          errorMessage.append("This Quote has been Escalated to "+rs.getString("USERNAME")+".<BR>");
        
        if(rs!=null)
          rs.close();
        if(pstmt!=null)
          pstmt.close();
        
        count = 0;
          
        sqlQuery  = " SELECT COUNT(*)NO_ROWS FROM QMS_QUOTE_MASTER WHERE QUOTE_ID = ? AND ACTIVE_FLAG='A' AND QUOTE_STATUS IN ('ACC','NAC')";
        pstmt     = connection.prepareStatement(sqlQuery);
        pstmt.setString(1,quoteId); 
        rs        = pstmt.executeQuery();
        
        if(rs.next())
          count   = rs.getInt("NO_ROWS");
        
        if(count > 0)
          errorMessage.append(" Positive/Negative Quotes Cannot Be Modified.<BR> ");
        
        if(rs!=null)
          rs.close();
        if(pstmt!=null)
          pstmt.close();
      }
      
     
      if("Modify".equalsIgnoreCase(operation))
    {
        sqlQuery      = "SELECT BASIS FROM QMS_QUOTE_MASTER WHERE QUOTE_ID=? AND ACTIVE_FLAG='A' AND TERMINAL_ID IN "+terminalQuery;
      
    }
    else if("Copy".equalsIgnoreCase(operation) || "View".equalsIgnoreCase(operation))
    {
      sqlQuery      = "SELECT BASIS FROM QMS_QUOTE_MASTER WHERE QUOTE_ID=? AND ((QUOTE_STATUS IN ('ACC', 'NAC') AND ACTIVE_FLAG IN ('A', 'I')) OR " +
                      "(QUOTE_STATUS NOT IN ('ACC', 'NAC') AND ACTIVE_FLAG = 'A'))  AND TERMINAL_ID IN "+terminalQuery;
    }
      
      pstmt     = connection.prepareStatement(sqlQuery);
        pstmt.setString(1,quoteId); 
      rs        =  pstmt.executeQuery();
      if(rs.next())
      {
        if("N".equalsIgnoreCase(basisFlag) && "Y".equalsIgnoreCase(rs.getString("BASIS")))
          errorMessage.append("You Do Not Have Sufficient Privileges to "+masterDOB.getOperation()+" this Quote.<BR>");
      }
      
      sqlQuery =  "  ";
    }
  }
  catch(SQLException se)
		{
    se.printStackTrace();
    throw new EJBException();
		}
  catch(Exception e)
  {
    e.printStackTrace();
    throw new EJBException();
  }
  finally
  {
	ConnectionUtil.closePreparedStatement(pstmt,rs);// call order changed by Dilip for PMD Correction
    ConnectionUtil.closeConnection(connection,null,null); 
    //ConnectionUtil.closePreparedStatement(pstmt,rs);   
  }
  return errorMessage;
}

public MultiQuoteFinalDOB getMasterInfo(String quoteId,ESupplyGlobalParameters loginbean) throws EJBException,SQLException
{

	System.out.println("QUOTE ID IN GET MASTER INFO: "+quoteId);
	Connection                connection      = null;
	PreparedStatement         pStmt		  	  = null; 
	ResultSet                 singleReturn    = null;
	ResultSet                 count           = null;
	ResultSet                 multipleReturn  = null;
	PreparedStatement         hStmt		  = null;
	CallableStatement         mstrProc        = null;
	ResultSet                 hRs             = null;
	MultiQuoteFinalDOB             finalDOB   = null;
	MultiQuoteMasterDOB            masterDOB  = null;
	MultiQuoteFreightLegSellRates  legDOB     = null;
	MultiQuoteFreightLegSellRates  tempDOB    = null;
	MultiQuoteFlagsDOB             flagsDOB   = null;
	ESupplyDateUtility        eSupplyDateUtility  = new ESupplyDateUtility();
	String                    accessLevel         = "";
	ArrayList                 legDetails          = null;
	ArrayList                  keyList             = new ArrayList();
	String[]				  orgLoc			  = null;
	String[]				  destLoc			  = null;  
	String[]				  orgPort			  = null;
	String[]				  destPort		      = null;  
    String[]				  shipp_zone		  = null;
	String[]				  shipp_zip		      = null;  
	String[]				  consignee_zone	  = null;
	String[]				  consignee_zip	      = null;
	String[]				  INCO_TERMS_ID	      = null;
	String[]				  CARGO_ACC_TYPE	  = null;
    String[]				  CARGO_ACC_Place	  = null;
	int                       keySize             = 0;
	String[]  				  spotRatesFlag		  = null;//Added by Anil.k for Spot Rates
	String[]                spotRateSurchargeCount=null;
	ArrayList				  uniqueIds			  = new ArrayList();//Added by Anil.k for Spot Rates
	long[]					  uniqueIdsList	      = null;//Added by Anil.k for Spot Rates
	ResultSet                 idReturn 			  = null;//Added by Anil.k for Spot Rates
	PreparedStatement         idStmt			  = null;//Added by Anil.k for Spot Rates
	boolean                   isMultiModal        = false;
	boolean                   incomplete_screen        = false;
	



String accessSql    = "SELECT OPER_ADMIN_FLAG FROM FS_FR_TERMINALMASTER WHERE TERMINALID=?";

String headerQry    = "SELECT * FROM QMS_QUOTE_HF_DTL WHERE QUOTE_ID= (SELECT MAX(QMS.ID) FROM QMS_QUOTE_MASTER QMS WHERE QMS.QUOTE_ID=?)";

String chargeQry    = 
"SELECT CHARGEGROUPID FROM QMS_QUOTE_CHARGEGROUPDTL WHERE QUOTE_ID = (SELECT MAX(QMS.ID) FROM QMS_QUOTE_MASTER QMS WHERE QMS.QUOTE_ID = ? AND QMS.ORIGIN_LOCATION = ? AND QMS.DEST_LOCATION = ?)";

//Modifed to get Attention To values in Modify Case
//MODIFIED BY SILPA.P ON 31-05-11
String contactsQry  =    "SELECT DISTINCT CD.SL_NO, CD.CONTACTPERSON," + 
						"  CD.CUSTOMERID, CD.CONTACTNO, CD.FAX, CD.EMAILID " +
						"  FROM QMS_CUST_CONTACTDTL CD, QMS_QUOTE_CONTACTDTL QC" + 
						" WHERE QC.CUSTOMERID = CD.CUSTOMERID" + 
						"   AND QC.SL_NO = CD.SL_NO" + 
						"   AND QC.QUOTE_ID =" + 
						"       (SELECT MAX(QMS.ID) FROM QMS_QUOTE_MASTER QMS WHERE QMS.QUOTE_ID = ?)" + 
						" AND CD.ACTIVE_STATUS ='A'" +
						" ORDER BY SL_NO";



String addressQry   = "SELECT ADDRESSLINE1||' '||ADDRESSLINE2||' '||ADDRESSLINE3||' '||CITY||' '||STATE||' '||ZIPCODE||' '||COUNTRYID ADDRESS FROM FS_ADDRESS WHERE ADDRESSID= ?";
// String legsQry      = "SELECT RL.RT_PLAN_ID,RL.ORIG_LOC,RL.DEST_LOC,RL.SHPMNT_MODE FROM FS_RT_PLAN RP,FS_RT_LEG RL WHERE RP.QUOTE_ID=? AND RP.RT_PLAN_ID=RL.RT_PLAN_ID ORDER BY RL.SERIAL_NO ";
//String spotRatesQry = "SELECT * FROM QMS_QUOTE_SPOTRATES WHERE QUOTE_ID = ? ORDER BY LINE_NO";
//Modified by Rakesh on 18-02-2011
String notesQry     =  "SELECT INTERNAL_NOTES,EXTERNAL_NOTES FROM QMS_QUOTE_NOTES WHERE QUOTE_ID IN (SELECT MAX(QMS.ID) FROM QMS_QUOTE_MASTER QMS WHERE QMS.QUOTE_ID =?)";
String commodityQry = "SELECT COMODITYTYPE FROM FS_FR_COMODITYMASTER WHERE COMODITYID=?";


//Added by Anil.k for Spot Rates
String getQuoteIdsQuery = 
	"SELECT QMS.ID FROM QMS_QUOTE_MASTER QMS WHERE QMS.QUOTE_ID=? AND QMS.ACTIVE_FLAG='A'";
//Ended by Anil.k for Spot Rates

	try
	{		
		connection        =   this.getConnection();
		mstrProc = connection.prepareCall("{call getMultiQuoteMasterData(?,?,?,?)}");
		mstrProc.setString(1,quoteId);
		mstrProc.registerOutParameter(2,OracleTypes.CURSOR);
		mstrProc.registerOutParameter(3,OracleTypes.CURSOR);
		mstrProc.registerOutParameter(4,OracleTypes.CURSOR);
     	mstrProc.execute();
	       singleReturn    = (ResultSet) mstrProc.getObject(2);
   		    count          = (ResultSet) mstrProc.getObject(3);
	       multipleReturn  = (ResultSet) mstrProc.getObject(4);
	      

		if(singleReturn.next())
		{
    
	finalDOB  = new MultiQuoteFinalDOB();
	masterDOB = new MultiQuoteMasterDOB();
    
    
    masterDOB.setQuoteId(singleReturn.getString("QUOTE_ID")); 
   /* masterDOB.setUniqueId(singleReturn.getInt("ID"));//Added by Rakesh for Issue:240224
*/    masterDOB.setVersionNo(singleReturn.getLong("VERSION_NO"));
    masterDOB.setShipmentMode(singleReturn.getInt("SHIPMENT_MODE"));
    masterDOB.setShipperMode(singleReturn.getInt("SHIPMENT_MODE")+"");
    masterDOB.setConsigneeMode(singleReturn.getInt("SHIPMENT_MODE")+"");
    masterDOB.setPreQuoteId(singleReturn.getString("PREQUOTE_ID"));  
    masterDOB.setQuoteStatus(singleReturn.getString("QUOTE_STATUS"));
    if(singleReturn.getString("IU_FLAG")!=null && "U".equalsIgnoreCase(singleReturn.getString("IU_FLAG")))
     masterDOB.setImpFlag(false);
    else if(singleReturn.getString("IU_FLAG")!=null && "I".equalsIgnoreCase(singleReturn.getString("IU_FLAG")))
     masterDOB.setImpFlag(true);
    masterDOB.setEffDate(singleReturn.getTimestamp("EFFECTIVE_DATE"));  
    
    if(loginbean!=null)
    {
      masterDOB.setEmpId(loginbean.getEmpId());
      masterDOB.setTerminalCurrency(loginbean.getCurrencyId());
    }
   
      masterDOB.setValidTo(singleReturn.getTimestamp("VALID_TO"));
    
  
     masterDOB.setCreatedDate(singleReturn.getTimestamp("CREATED_DATE"));
  
    masterDOB.setModifiedDate(singleReturn.getTimestamp("MODIFIED_DATE"));
    masterDOB.setCustDate(singleReturn.getTimestamp("CUST_REQUESTED_DATE"));//Added by Rakesh on 23-02-2011 for  Issue:236359
    masterDOB.setCustTime(singleReturn.getString("CUST_REQUESTED_TIME"));//Added by Rakesh on 23-02-2011 for  Issue:
    masterDOB.setAccValidityPeriod(singleReturn.getInt("ACCEPT_VALIDITYPERIOD"));
  
    if(singleReturn.getString("CUSTOMER_ID")!=null && singleReturn.getString("CUSTOMER_ID").trim().length()!=0)
      masterDOB.setCustomerId(singleReturn.getString("CUSTOMER_ID"));        
    
      masterDOB.setCustomerAddressId(singleReturn.getInt("CUSTOMER_ADDRESSID"));        
  
  if(singleReturn.getString("CREATED_BY")!=null && singleReturn.getString("CREATED_BY").trim().length()!=0)
    masterDOB.setCreatedBy(singleReturn.getString("CREATED_BY"));      
  
  if(singleReturn.getString("TERMINAL_ID")!=null && singleReturn.getString("TERMINAL_ID").trim().length()!=0)
    masterDOB.setTerminalId(singleReturn.getString("TERMINAL_ID"));
    
  if(singleReturn.getString("SALES_PERSON")!=null && singleReturn.getString("SALES_PERSON").trim().length()!=0)
    masterDOB.setSalesPersonCode(singleReturn.getString("SALES_PERSON"));
  
  if(singleReturn.getString("SALES_PERSON_EMAIL_FLAG")!=null && singleReturn.getString("SALES_PERSON_EMAIL_FLAG").trim().length()!=0)
    masterDOB.setSalesPersonFlag(singleReturn.getString("SALES_PERSON_EMAIL_FLAG"));
  
  if(singleReturn.getString("OVERLENGTH_CARGONOTES")!=null && singleReturn.getString("OVERLENGTH_CARGONOTES").trim().length()!=0)
    masterDOB.setOverLengthCargoNotes(singleReturn.getString("OVERLENGTH_CARGONOTES"));
    
  
  if(singleReturn.getString("INDUSTRY_ID")!=null && singleReturn.getString("INDUSTRY_ID").trim().length()!=0)
    masterDOB.setIndustryId(singleReturn.getString("INDUSTRY_ID"));
  
  if(singleReturn.getString("COMMODITY_ID")!=null && singleReturn.getString("COMMODITY_ID").trim().length()!=0)
    masterDOB.setCommodityId(singleReturn.getString("COMMODITY_ID"));
  
  if("Y".equalsIgnoreCase(singleReturn.getString("HAZARDOUS_IND")))
    masterDOB.setHazardousInd(true);
  else
    masterDOB.setHazardousInd(false);        
 
    masterDOB.setUnNumber(singleReturn.getString("UN_NUMBER"));      
  
    masterDOB.setCommodityClass(singleReturn.getString("CLASS"));
  
  if(singleReturn.getString("SERVICE_LEVEL_ID")!=null && singleReturn.getString("SERVICE_LEVEL_ID").trim().length()!=0)
    masterDOB.setServiceLevelId(singleReturn.getString("SERVICE_LEVEL_ID"));
  
 
  
  if(singleReturn.getString("QUOTING_STATION")!=null && singleReturn.getString("QUOTING_STATION").trim().length()!=0)
    masterDOB.setQuotingStation(singleReturn.getString("QUOTING_STATION"));
  
    if(singleReturn.getString("SHIPPER_CONSOLE_TYPE")!=null && singleReturn.getString("SHIPPER_CONSOLE_TYPE").trim().length()!=0)
    masterDOB.setShipperConsoleType(singleReturn.getString("SHIPPER_CONSOLE_TYPE"));
        

  if(singleReturn.getString("CONSIGNEE_CONSOLE_TYPE")!=null && singleReturn.getString("CONSIGNEE_CONSOLE_TYPE").trim().length()!=0)
    masterDOB.setConsigneeConsoleType(singleReturn.getString("CONSIGNEE_CONSOLE_TYPE"));
  
 
  
  if(singleReturn.getString("OVERLENGTH_CARGONOTES")!=null  && singleReturn.getString("OVERLENGTH_CARGONOTES").trim().length()!=0)
    masterDOB.setOverLengthCargoNotes(singleReturn.getString("OVERLENGTH_CARGONOTES"));
         
 //   masterDOB.setRouteId(Integer.toString(singleReturn.getInt("ROUTING_ID")));
  
  if(singleReturn.getString("MULTI_QUOTE_WEIGHT_BREAK")!=null  && singleReturn.getString("MULTI_QUOTE_WEIGHT_BREAK").trim().length()!=0)
	    masterDOB.setMultiquoteweightBrake(singleReturn.getString("MULTI_QUOTE_WEIGHT_BREAK")) ;
  
  //Added by Anil.k
  	if(singleReturn.getString("MULTI_QUOTE_WITH")!=null && singleReturn.getString("MULTI_QUOTE_WITH").trim().length()!=0)
  	masterDOB.setQuoteWith(singleReturn.getString("MULTI_QUOTE_WITH"));
  
  flagsDOB = new MultiQuoteFlagsDOB();
  
  if(singleReturn.getString("PN_FLAG")!=null && singleReturn.getString("PN_FLAG").trim().length()!=0)
    flagsDOB.setPNFlag(singleReturn.getString("PN_FLAG"));
  else
    flagsDOB.setPNFlag("");
    
  if(singleReturn.getString("ACTIVE_FLAG")!=null && singleReturn.getString("ACTIVE_FLAG").trim().length()!=0)
    flagsDOB.setActiveFlag(singleReturn.getString("ACTIVE_FLAG"));
  else
    flagsDOB.setActiveFlag("");
    
  if(singleReturn.getString("COMPLETE_FLAG")!=null && singleReturn.getString("COMPLETE_FLAG").trim().length()!=0)
    flagsDOB.setCompleteFlag(singleReturn.getString("COMPLETE_FLAG"));
  else
    flagsDOB.setCompleteFlag(""); 
    
  if(singleReturn.getString("ESCALATION_FLAG")!=null && singleReturn.getString("ESCALATION_FLAG").trim().length()!=0)
    flagsDOB.setEscalationFlag(singleReturn.getString("ESCALATION_FLAG"));
  else
    flagsDOB.setEscalationFlag("");
    
  if(singleReturn.getString("UPDATE_FLAG")!=null && singleReturn.getString("UPDATE_FLAG").trim().length()!=0)
    flagsDOB.setUpdateFlag(singleReturn.getString("UPDATE_FLAG"));
  else
    flagsDOB.setUpdateFlag("");
    
  if(singleReturn.getString("QUOTE_STATUS")!=null && singleReturn.getString("QUOTE_STATUS").trim().length()!=0)
    flagsDOB.setQuoteStatusFlag(singleReturn.getString("QUOTE_STATUS"));
  else
    flagsDOB.setQuoteStatusFlag("");
    
  if(singleReturn.getString("IE_FLAG")!=null && singleReturn.getString("IE_FLAG").trim().length()!=0)
    flagsDOB.setInternalExternalFlag(singleReturn.getString("IE_FLAG"));
  else
    flagsDOB.setInternalExternalFlag("");
  
  if(singleReturn.getString("SENT_FLAG")!=null && singleReturn.getString("SENT_FLAG").trim().length()!=0)
    flagsDOB.setSentFlag(singleReturn.getString("SENT_FLAG"));
  else
    flagsDOB.setSentFlag("");
    
  if(singleReturn.getString("EMAIL_FLAG")!=null && singleReturn.getString("EMAIL_FLAG").trim().length()!=0)
    flagsDOB.setEmailFlag(singleReturn.getString("EMAIL_FLAG"));
  else
    flagsDOB.setEmailFlag("");
    
  if(singleReturn.getString("FAX_FLAG")!=null && singleReturn.getString("FAX_FLAG").trim().length()!=0)
    flagsDOB.setFaxFlag(singleReturn.getString("FAX_FLAG"));
  else
    flagsDOB.setFaxFlag("");
  
  if(singleReturn.getString("PRINT_FLAG")!=null && singleReturn.getString("PRINT_FLAG").trim().length()!=0)
    flagsDOB.setPrintFlag(singleReturn.getString("PRINT_FLAG"));
  else
    flagsDOB.setPrintFlag("");
  
  finalDOB.setReportingOfficer(singleReturn.getString("ESCALATED_TO"));
  
  if(finalDOB.getReportingOfficer()!=null && finalDOB.getReportingOfficer().trim().length()!=0)
    finalDOB.setEscalatedTo(finalDOB.getReportingOfficer());
  
  finalDOB.setFlagsDOB(flagsDOB);
  
  hStmt =  connection.prepareStatement(accessSql);
  hStmt.setString(1,masterDOB.getTerminalId());
  hRs   =  hStmt.executeQuery();
  
  if(hRs.next())
  {
    masterDOB.setAccessLevel(hRs.getString("OPER_ADMIN_FLAG"));
  }
  
  if(hRs != null)
    hRs.close();
  if(hStmt!=null)
    hStmt.close();

}	//if end

int count1 = 0;
if(count.next())
   count1    = count.getInt(1);
 int  rowcount =0;

      orgLoc			= new String[count1];  
	  destLoc			= new String[count1];
	  orgPort			= new String[count1];
	  destPort		    = new String[count1];
	  shipp_zone		= new String[count1];
	  shipp_zip		    = new String[count1];
	  consignee_zone	= new String[count1];
	  consignee_zip	    = new String[count1];
	  INCO_TERMS_ID	    = new String[count1];
	  CARGO_ACC_TYPE	= new String[count1];
	  CARGO_ACC_Place   = new String[count1];
      legDetails		= new ArrayList();
      spotRatesFlag		= new String[count1];//Added by Anil.k for Spot Rates
 spotRateSurchargeCount = new String[count1];	  

while(multipleReturn.next())
{
legDOB			= new MultiQuoteFreightLegSellRates();
if(multipleReturn.getString("ORIGIN_LOCATION")!=null && multipleReturn.getString("ORIGIN_LOCATION").trim().length()!=0)
{
 orgLoc[rowcount]  = multipleReturn.getString("ORIGIN_LOCATION");
 
}
  
if(multipleReturn.getString("SHIPPER_ZIPCODE")!=null && multipleReturn.getString("SHIPPER_ZIPCODE").trim().length()!=0)
{
  shipp_zip[rowcount] = multipleReturn.getString("SHIPPER_ZIPCODE"); // Added by Kishore 
}
    
if(multipleReturn.getString("SHIPPERZONES")!=null && multipleReturn.getString("SHIPPERZONES").trim().length()!=0)
{
     shipp_zone[rowcount] = multipleReturn.getString("SHIPPERZONES");
}

if(multipleReturn.getString("CONSIGNEEZONES")!=null && multipleReturn.getString("CONSIGNEEZONES").trim().length()!=0)
{
       consignee_zone[rowcount] = multipleReturn.getString("CONSIGNEEZONES");
}

if(multipleReturn.getString("CONSIGNEE_ZIPCODE")!=null && multipleReturn.getString("CONSIGNEE_ZIPCODE").trim().length()!=0)
{
     consignee_zip[rowcount] = multipleReturn.getString("CONSIGNEE_ZIPCODE");
}
  
if(multipleReturn.getString("ORIGIN_PORT")!=null &&multipleReturn.getString("ORIGIN_PORT").trim().length()!=0)
{
        orgPort[rowcount] = multipleReturn.getString("ORIGIN_PORT");
		legDOB.setOrigin(multipleReturn.getString("ORIGIN_PORT")); 
}

if(multipleReturn.getString("DEST_LOCATION")!=null && multipleReturn.getString("DEST_LOCATION").trim().length()!=0)
{
         destLoc[rowcount] = multipleReturn.getString("DEST_LOCATION");
}
  
if(multipleReturn.getString("DESTIONATION_PORT")!=null && multipleReturn.getString("DESTIONATION_PORT").trim().length()!=0)
{
           destPort[rowcount] = multipleReturn.getString("DESTIONATION_PORT");
		   legDOB.setDestination(multipleReturn.getString("DESTIONATION_PORT"));
}
if(legDOB.getOrigin()!=null && legDOB.getDestination()!=null){
	legDOB=this.getLocFullNames(legDOB,legDOB.getOrigin(),legDOB.getDestination(),orgLoc[rowcount],destLoc[rowcount]);
}
if(multipleReturn.getString("CARGO_ACC_TYPE")!=null && multipleReturn.getString("CARGO_ACC_TYPE").trim().length()!=0)
{
             CARGO_ACC_TYPE[rowcount] = multipleReturn.getString("CARGO_ACC_TYPE");
}
  
if(multipleReturn.getString("CARGO_ACC_PLACE")!=null && multipleReturn.getString("CARGO_ACC_PLACE").trim().length()!=0)
{
                CARGO_ACC_Place[rowcount] = multipleReturn.getString("CARGO_ACC_PLACE");
}
if(multipleReturn.getString("INCO_TERMS_ID") != null && multipleReturn.getString("INCO_TERMS_ID").trim().length() !=0 )
{  
	                  INCO_TERMS_ID[rowcount]  = multipleReturn.getString("INCO_TERMS_ID");
}
//Added by Anil.k for Spot Rates
if(multipleReturn.getString("SPOT_RATES_FLAG")!=null && multipleReturn.getString("SPOT_RATES_FLAG").trim().length()!=0)
{
	spotRatesFlag[rowcount] = multipleReturn.getString("SPOT_RATES_FLAG");
}
if(multipleReturn.getString("SPOTRATESURCHARGECOUNT")!=null && multipleReturn.getString("SPOTRATESURCHARGECOUNT").trim().length()!=0)
{
	spotRateSurchargeCount[rowcount] = multipleReturn.getString("SPOTRATESURCHARGECOUNT");
}

if(multipleReturn.getInt("INCOMPLETE_SCREEN")>0)
	incomplete_screen = true;

if(!"".equals(Integer.toString(multipleReturn.getInt("MULTI_LANE_ORDER"))))
{
	legDOB.setMultiQuote_SerialNo(multipleReturn.getInt("MULTI_LANE_ORDER"));
}else{
	legDOB.setMultiQuote_SerialNo(rowcount);
}
legDOB.setSpotRatesFlag("Y".equalsIgnoreCase(multipleReturn.getString("SPOT_RATES_FLAG"))?true:false);//Ended by Anil.k for Spot Rates
legDOB.setShipmentMode(masterDOB.getShipmentMode());
hStmt  = connection.prepareStatement(chargeQry);
hStmt.clearParameters();
hStmt.setString(1,masterDOB.getQuoteId());
hStmt.setString(2,orgLoc[rowcount]);
hStmt.setString(3,destLoc[rowcount]);
hRs = hStmt.executeQuery();
ArrayList chargeGrps = new ArrayList();
  
  while(hRs.next())
  {
   chargeGrps.add(hRs.getString("CHARGEGROUPID"));
  }
  String[] chargeGrp = null;
  
  if(chargeGrps.size()>0)
    chargeGrp = new String[chargeGrps.size()];
 int chargGrpSize	=	chargeGrps.size();
  for(int i=0;i<chargGrpSize;i++)
  {
    chargeGrp[i] = (String)chargeGrps.get(i);
  }

   legDOB.setChargeGroupIds(chargeGrp);
if(hRs!= null)
hRs.close();
 legDetails.add(legDOB); 
rowcount++;
}

   //Added by Anil.k for Spot Rates	
   idStmt = connection.prepareStatement(getQuoteIdsQuery);
   idStmt.setString(1, quoteId);
   idReturn = idStmt.executeQuery();
   while(idReturn.next())
   {
	   uniqueIds.add(idReturn.getLong("ID"));
   }
   //Ended by Anil.k for Spot Rates
   masterDOB.setIncoTermsId(INCO_TERMS_ID);
   masterDOB.setOriginLocation(orgLoc);
   masterDOB.setOriginPort(orgPort);
   masterDOB.setDestLocation(destLoc);
   masterDOB.setDestPort(destPort);
   masterDOB.setShipperZipCode(shipp_zip);
   masterDOB.setShipperZones(shipp_zone);
   masterDOB.setConsigneeZones(consignee_zone);
   masterDOB.setConsigneeZipCode(consignee_zip);
   masterDOB.setCargoAccPlace(CARGO_ACC_Place);
   masterDOB.setCargoAcceptance(CARGO_ACC_TYPE);
   masterDOB.setSpotRatesFlag(spotRatesFlag);//Added by Anil.k for Spot Rates
   masterDOB.setSpotrateSurchargeCount(spotRateSurchargeCount);
   masterDOB.setIncomplete_screen(incomplete_screen);
   //Added by Anil.k for Spot Rates      
   uniqueIdsList = new long[uniqueIds.size()];  
   /* for(int i=0;i<uniqueIds.size();i++)
   {
	   uniqueIdsList[i] = Long.parseLong(uniqueIds.toString());
   }*/  
   Object obj[]=null;
   int len=0;
   obj = uniqueIds.toArray();
   for(Object a:obj){
   	   uniqueIdsList[len] = Long.parseLong(a.toString());
   	   len++;
   }
   masterDOB.setUniqueIds(uniqueIdsList);
   //Ended by Anil.k for Spot Rates

if(multipleReturn != null || singleReturn != null || mstrProc != null )
	{
 multipleReturn.close();
 singleReturn.close();
 mstrProc.close();
 }




  hStmt  = connection.prepareStatement(headerQry);
  hStmt.clearParameters();      
  hStmt.setString(1,masterDOB.getQuoteId());
  hRs = hStmt.executeQuery();
  
  ArrayList headerFooter = new ArrayList();
  ArrayList content      = new ArrayList();
  ArrayList level        = new ArrayList();
  ArrayList align        = new ArrayList();
  
  while(hRs.next())
  {      
    headerFooter.add(hRs.getString("HEADER")!=null?hRs.getString("HEADER"):"");       
    content.add(hRs.getString("CONTENT")!=null?hRs.getString("CONTENT"):"");       
    level.add(hRs.getString("CLEVEL")!=null?hRs.getString("CLEVEL"):"");      
    align.add(hRs.getString("ALIGN")!=null?hRs.getString("ALIGN"):"");
  }
  if(hRs!=null)hRs.close();
  if(hStmt!=null)hStmt.close();
  
  String[] contentArray            = null;
  String[] headerFooterArray       = null;
  String[] levelArray              = null;
  String[] alignArray              = null;
  
  if(content.size()>0)
  {
    contentArray            = new String[content.size()];
    headerFooterArray       = new String[headerFooter.size()];
    levelArray              = new String[level.size()];
    alignArray              = new String[align.size()];
  }
 int contentSize	=	content.size();
  for(int i=0;i<contentSize;i++)
  {
    contentArray[i]       = (String)content.get(i);
    headerFooterArray[i]  = (String)headerFooter.get(i);
    levelArray[i]         = (String)level.get(i);
    alignArray[i]         = (String)align.get(i);
  }
  
  masterDOB.setHeaderFooter(headerFooterArray); 
  masterDOB.setContentOnQuote(contentArray);
  masterDOB.setLevels(levelArray);
  masterDOB.setAlign(alignArray);
  

hStmt  = connection.prepareStatement(contactsQry);
  hStmt.clearParameters();     
  hStmt.setString(1,masterDOB.getQuoteId());
  hRs = hStmt.executeQuery();
  ArrayList contactIds   = new ArrayList();
  ArrayList contactNames = new ArrayList();
//Added to get Attention To values in Modify Case
  ArrayList contactNo = new ArrayList();
  ArrayList fax = new ArrayList();
  ArrayList emailId = new ArrayList();
//Ended to get Attention To values in Modify Case
  while(hRs.next())
  {
   contactIds.add(hRs.getString("SL_NO"));
   contactNames.add(hRs.getString("CONTACTPERSON"));
 //Added to get Attention To values in Modify Case
   contactNo.add(hRs.getString("CONTACTNO"));
   fax.add(hRs.getString("FAX"));
   emailId.add(hRs.getString("EMAILID"));
 //Ended to get Attention To values in Modify Case
  }
  
  String[] contactIdsArray    =   null;
  String[] contactNamesArray    =   null;
//Added to get Attention To values in Modify Case
  String[] contactNoArray    =   null;
  String[] faxArray    =   null;
  String[] emailIdArray    =   null;
//Ended to get Attention To values in Modify Case
  
  if(contactIds.size()>0)
  {
      contactIdsArray   = new String[contactIds.size()];
      contactNamesArray = new String[contactIds.size()];
    //Added to get Attention To values in Modify Case
      contactNoArray 	= new String[contactIds.size()];
      faxArray 			= new String[contactIds.size()];
      emailIdArray 		= new String[contactIds.size()];
    //Ended to get Attention To values in Modify Case
  }
  int contactIdsSize	=	contactIds.size();
  for(int i=0;i<contactIdsSize;i++)
  {
    contactIdsArray[i]    = (String)contactIds.get(i);
    contactNamesArray[i]  = (String)contactNames.get(i);
  //Added to get Attention To values in Modify Case
    contactNoArray[i] 	  = (String)contactNo.get(i);
    faxArray[i] 		  = (String)fax.get(i);
    emailIdArray[i]  	  = (String)emailId.get(i);
  //Ended to get Attention To values in Modify Case
  }
  masterDOB.setCustomerContacts(contactIdsArray);     
  masterDOB.setCustContactNames(contactNamesArray);
//Added to get Attention To values in Modify Case
  masterDOB.setCustomerContactsEmailIds(emailIdArray);
  masterDOB.setCustomerContactsFax(faxArray);
  masterDOB.setCustomerContactNo(contactNoArray);
//Ended to get Attention To values in Modify Case
  
  if(hRs!=null)hRs.close();
  if(hStmt!=null)hStmt.close();   


    
  hStmt  = connection.prepareStatement(addressQry);
  hStmt.clearParameters();
  hStmt.setInt(1,masterDOB.getCustomerAddressId());     
  hRs = hStmt.executeQuery();      
  
  while(hRs.next())
  {
    masterDOB.setCustomerAddress(hRs.getString("ADDRESS"));
  }
  if(hRs!=null)hRs.close();
  if(hStmt!=null)hStmt.close(); 
  
 
          
 
 if(hRs!=null)hRs.close();
  if(hStmt!=null)hStmt.close(); 
  
       
  hStmt  = connection.prepareStatement(notesQry);
  hStmt.clearParameters();     
  hStmt.setString(1,masterDOB.getQuoteId());
  hRs = hStmt.executeQuery();
  
  ArrayList internal  = new ArrayList();
  ArrayList external  = new ArrayList();
  
    String[]  internalArr = null;
  String[]  externalArr = null;
  String[]  arr = null;
  String[]  intarr = null;
  String intarr1 = "";
   String  arr1 = "";

  

    while(hRs.next())
    {
      internal.add((hRs.getString("INTERNAL_NOTES")!=null)?hRs.getString("INTERNAL_NOTES"):"");
      external.add((hRs.getString("EXTERNAL_NOTES")!=null)?hRs.getString("EXTERNAL_NOTES"):"");
    }
  
    if(internal.size()>0)
    {
      internalArr = new String[internal.size()];
      externalArr = new String[external.size()];

    }
    
    if(externalArr!=null)
    {
    	int extArrLen	=	externalArr.length;
      for(int i=0;i<extArrLen;i++)
      {
        internalArr[i]  = (String)internal.get(i);
        internalArr[i] =  internalArr[i] .trim();
        if(internalArr[i].length()>0)
        {
        intarr = internalArr[i].split("");
        for( int j=0;j<intarr.length-1;j++)
        {
         
             if((intarr[j].trim()).length()>0)
            {
              intarr1 =intarr1.concat(intarr[j].trim());
            }
         else
         {
           if((intarr[j+1].trim()).length()>0)
           {
              intarr1 =intarr1.concat(" ");
           }
         }
        }
              intarr1 =intarr1.concat(intarr[intarr.length-1]);
              internalArr[i] = intarr1;
        }
        //Added by Rakesh on 18-02-2011
        intarr1="";
        arr1 =  "";
        externalArr[i]  = (String)external.get(i);
        externalArr[i]  = externalArr[i].trim();
        if(  externalArr[i].length()>0)
        {
        arr = externalArr[i].split("");
        for( int j=0;j<arr.length-1;j++)
        {
         
             if((arr[j].trim()).length()>0)
            {
              arr1 =arr1.concat(arr[j].trim());
            }
         else
         {
           if((arr[j+1].trim()).length()>0)
           {
              arr1 =arr1.concat(" ");
           }
         }
        }
              arr1 =arr1.concat(arr[arr.length-1]);
              externalArr[i] = arr1;
        arr1 =  "";
        }
       }
    }

    
    finalDOB.setExternalNotes(externalArr);
    finalDOB.setInternalNotes(internalArr);
    //Added by Rakesh on 18-02-2011
    finalDOB.setINotes(internalArr);
    finalDOB.setENotes(externalArr);
    
    
    if(hRs!=null)hRs.close();
    if(hStmt!=null)hStmt.close(); 
    
    hStmt   =   connection.prepareStatement(commodityQry);
    hStmt.setString(1,masterDOB.getCommodityId());
    
    hRs     =   hStmt.executeQuery();
    
    while(hRs.next())
    {
      masterDOB.setCommodityType(hRs.getString("COMODITYTYPE"));
    }

  finalDOB.setLegDetails(legDetails);
  finalDOB.setMasterDOB(masterDOB);
//   finalDOB.setMultiModalQuote(isMultiModal);
  




  
}
	catch(SQLException se)
	{
		se.printStackTrace();
		throw new EJBException();
	}
	catch(Exception e)
	{
		e.printStackTrace();
		throw new EJBException();
	}
finally
{
	// Added by Dilip for PMD Correction on 22/09/2015
	  if(multipleReturn!=null){
		  multipleReturn.close();
		  multipleReturn=null;
	  }
	  if(singleReturn!=null){
		  singleReturn.close();
		  singleReturn=null;
	  }
	  if(count!=null)
		  count.close(); // Added by Gowtham on 04Feb2011 for Connection Leaks.
  ConnectionUtil.closePreparedStatement(idStmt,idReturn);// Added by Dilip for PMD Correction on 22/09/2015
  ConnectionUtil.closeConnection(connection,pStmt,hRs);
  ConnectionUtil.closeConnection(connection,hStmt);
}
return finalDOB;


}

public MultiQuoteFinalDOB getQuoteDetails(String quoteId,String buyRatesFlag,ESupplyGlobalParameters loginbean) throws EJBException
{
    MultiQuoteFinalDOB           finalDOB       = null;
    MultiQuoteMasterDOB          masterDOB      = null;
    MultiQuoteAttachmentDOB      attachmentDOB  = null;
    ArrayList               attachmentIdList = new  ArrayList();       
   
   try
   {
        finalDOB = getMasterInfo(quoteId,loginbean);
        masterDOB = finalDOB.getMasterDOB();
        masterDOB.setUserId(loginbean.getUserId());
        masterDOB.setBuyRatesPermission(buyRatesFlag);
        masterDOB.setCompanyId(loginbean.getCompanyId());
        finalDOB.setMasterDOB(masterDOB);
        finalDOB = getQuoteHeader(finalDOB);
        finalDOB = getMultiQuoteRatesChargesInfo(quoteId,finalDOB,null);
        finalDOB = getQuoteContentDtl(finalDOB);
     
   }
   catch(Exception e)
   {
     //Logger.error(FILE_NAME,"Error in getUpdatedQuoteDetails"+e);
     logger.error(FILE_NAME+"Error in getUpdatedQuoteDetails"+e);
     e.printStackTrace();
     throw new EJBException(e.toString());
   }  
   return finalDOB;
  }








public String getEmailText(String terminalId,String quoteType) throws EJBException
{

	
        Connection connection = null;
        PreparedStatement pst = null;
        ResultSet   rs        = null;
        String      emailText = null;
        
                  
        String      emailTextQuery  ="SELECT TEXT FROM QMS_EMAIL_TEXT_MASTER"+ 
          " WHERE TERMINAL_ID=((SELECT DISTINCT TERMINAL_ID FROM QMS_EMAIL_TEXT_MASTER WHERE"+
          " TERMINAL_ID=(SELECT TERMINAL_ID FROM(SELECT T1.TERMINAL_ID ,ROWNUM FROM(SELECT "+
          " T.TERMINAL_ID,DECODE(TM.OPER_ADMIN_FLAG,'O',1,'A',2,'H',3)OPER_ADMIN_FLAG FROM("+
          " SELECT TERMINAL_ID,COUNT(*)NO_ROWS FROM QMS_EMAIL_TEXT_MASTER WHERE TERMINAL_ID"+ 
          " IN(SELECT ? TERM_ID FROM DUAL UNION ALL SELECT PARENT_TERMINAL_ID TERM_ID"+ 
          " FROM FS_FR_TERMINAL_REGN CONNECT BY CHILD_TERMINAL_ID=PRIOR PARENT_TERMINAL_ID"+ 
          " START WITH CHILD_TERMINAL_ID=? UNION ALL SELECT TERMINALID TERM_ID FROM "+
          " FS_FR_TERMINALMASTER WHERE OPER_ADMIN_FLAG='H')GROUP BY TERMINAL_ID)T,"+
          " FS_FR_TERMINALMASTER TM WHERE T.NO_ROWS>0 AND T.TERMINAL_ID=TM.TERMINALID ORDER BY"+
          " OPER_ADMIN_FLAG)T1 WHERE ROWNUM=1)))) AND QUOTE_TYPE=?";
        try
        {
          connection  =   getConnection();
          pst         =   connection.prepareStatement(emailTextQuery);
          pst.setString(1,terminalId);
          pst.setString(2,terminalId);
          pst.setString(3,quoteType);
          rs        =   pst.executeQuery();
          if(rs.next())
             emailText  = rs.getString("TEXT");
        }
        catch(Exception e)
        {
           e.printStackTrace();
           logger.error("Exception in getting EmailText "+e);
           throw new EJBException(e);
       }
        finally
        {
           ConnectionUtil.closeConnection(connection,pst,rs);   
        }
             return emailText;
    
}

/**
 * This method helps in modifying the quote master details by calling the entity bean
* 
 * @param masterDOB 	an MultiQuoteMasterDOB Object that contains all the Quote Master information
 * 
 * @exception EJBException 
 */
//public long modifyQuoteMasterDtls(MultiQuoteFinalDOB finalDOB) throws	EJBException  //@@ Commented by subrahmanyam for the Enhacnement #146971 on 03/12/2008
public String modifyQuoteMasterDtls(MultiQuoteFinalDOB finalDOB) throws	EJBException //@@ Added by subrahmanyam for the enhancement #146971 on 03/12/2008
{
Connection connection = null;

MultiQuoteMasterDOB   masterDOB  = null;
QMSMultiQuoteDAO       quoteDAO  = null;
try
{
  masterDOB   = finalDOB.getMasterDOB();
  connection  = this.getConnection(); 
  quoteDAO  = new QMSMultiQuoteDAO();
  quoteDAO.store(finalDOB); 
}
catch(SQLException sqEx)
	{
  sqEx.printStackTrace();

  logger.error(FILE_NAME+"QMSMultiQuoteSessionBean[insertQuoteMasterDtls(masterDOB)] -> "+sqEx.toString());
		throw new EJBException(sqEx.toString());
	}
catch(Exception e)
	{
  e.printStackTrace();

  logger.error(FILE_NAME+"QMSMultiQuoteSessionBean[insertQuoteMasterDtls(masterDOB)] -> "+e.toString());
		throw new EJBException(e.toString());
	}
	finally
	{
		try
		{
			ConnectionUtil.closeConnection(connection,null,null);
		}
		catch(EJBException ex)
		{

    logger.error(FILE_NAME+"Finally : QMSQuoteSessionBean[insertQuoteMasterDtls(masterDOB)]-> "+ex.toString());
			throw new EJBException(ex.toString());
		}
	}
return masterDOB.getQuoteId();
}




public String  updateSendMailFlag(String quoteId,String userId,String operation,boolean compareFlag,int mailStatus) throws EJBException
{
	  
  Connection                connection              = null;
  PreparedStatement         psmt                    = null;
  String                    inactivateQuery         = "";
  String                    query                   = null;    
  try
  {      
    connection        =    this.getConnection();
    inactivateQuery   =   "UPDATE QMS_QUOTE_MASTER SET ACTIVE_FLAG=?,MODIFIED_DATE=?,MODIFIED_BY=? WHERE QUOTE_ID=? AND VERSION_NO <> (SELECT MAX(VERSION_NO) FROM QMS_QUOTE_MASTER WHERE QUOTE_ID=?)";
    query             =   "UPDATE QMS_QUOTE_MASTER SET SENT_FLAG=?,IE_FLAG=?,QUOTE_STATUS=?,MODIFIED_DATE=?,MODIFIED_BY=? WHERE QUOTE_ID=? AND VERSION_NO = (SELECT MAX(VERSION_NO) FROM QMS_QUOTE_MASTER WHERE QUOTE_ID=?)";    
    
    //if(!compareFlag && "Modify".equalsIgnoreCase(operation))
   // if(!compareFlag && "Modify".equalsIgnoreCase(operation))//Modified by Anil.k for updating the active flag of multiquote
    if( "Modify".equalsIgnoreCase(operation))//Modified by Anil.k for updating the active flag of multiquote
    {
      psmt              =   connection.prepareStatement(inactivateQuery);
      psmt.setString(1,"I");   
      psmt.setTimestamp(2,new java.sql.Timestamp((new java.util.Date()).getTime()));
      psmt.setString(3,userId);
      psmt.setString(4,quoteId);
      psmt.setString(5,quoteId);
      psmt.executeUpdate();
      
      if(psmt!=null)
        psmt.close();
    }
      
    if(mailStatus != 0)
    {
      psmt              =   connection.prepareStatement(query);
      psmt.clearParameters();
      psmt.setString(1,"S");
      psmt.setString(2,"E");
      psmt.setString(3,"PEN");
      psmt.setTimestamp(4,new java.sql.Timestamp((new java.util.Date()).getTime()));
      psmt.setString(5,userId);
      psmt.setString(6,quoteId);
      psmt.setString(7,quoteId);
      psmt.executeUpdate();
     }
  }
  catch(Exception e)
  {
 
    logger.error(FILE_NAME+"Error in getShipperConsigneeZones"+e);
    e.printStackTrace();
    throw new EJBException(e.toString());
  }
  finally
  {
    ConnectionUtil.closeConnection(connection,psmt);
  }
  return "Updated" ;

}


private String multiStringAppend(String[] str)
{
	String st="";
	int strlen = str.length;
	
	for(int i=0;i<strlen;i++){
		if(i!= strlen-1)
			st= st+"'"+str[i]+"',";
		else
			 st= st+"'"+str[i]+"'";
	} 
	
return st.trim();
		
	
}

public String getCountryId(int addressId)throws EJBException
{
 
    Connection         connection             = null;
    PreparedStatement  pst                    = null;
    ResultSet          rs                     = null;
    String             countryId              = null;
    String             countryIdQuery         = "SELECT COUNTRYID FROM FS_ADDRESS WHERE ADDRESSID=?";
   try
   {
     
     connection = getConnection();
      pst  = connection.prepareStatement(countryIdQuery);
      pst.setInt(1,addressId);
      rs = pst.executeQuery();
     while(rs.next())
     {
        countryId = (String)rs.getString("COUNTRYID");
     }
    
   }
    catch(Exception e)
   {
        e.printStackTrace();
        logger.error("Exception in retreiving countryid "+e);
        throw new EJBException(e);
   }
   finally
   {
        ConnectionUtil.closeConnection(connection,pst,rs);   
   }
   return countryId;
}

private MultiQuoteFinalDOB getMultiQuoteRatesChargesInfo(String quoteId,MultiQuoteFinalDOB  finalDOB,UpdatedQuotesReportDOB reportDOB) throws EJBException
{
    Connection                 connection = null;
    CallableStatement          cStmt      = null;
    ResultSet                  rs2        = null,rs3 = null,rs4 = null,rs5 = null;    
    ArrayList                  quoteList  = null;
    ArrayList                  originChargesList       = null;
    ArrayList                  destChargesList         = null;
    MultiQuoteFreightLegSellRates   legDOB                  = null;
    MultiQuoteFreightLegSellRates   spotLegDOB              = null;
    MultiQuoteCharges               chargesDOB              = null;
    MultiQuoteCharges               deliveryChargesDOB      = null;
    MultiQuoteChargeInfo            chargeInfo              = null;
    ArrayList                  chargeInfoList          = null;
    ArrayList                  legDetails              = null;
    ArrayList                  spotRateDetails         = null;
    String                     flag                    = null;
    double                     sellRate                = 0;
    String                     weightBreak             = null;
    String                     rateType                = null;
    String                     rateIndicator           = null;
    ArrayList                  list_exNotes            = null;
    ArrayList                  freightChargesList      = null;
    MultiQuoteMasterDOB             masterDOB               = null;
    MultiQuoteFinalDOB              tmpFinalDOB             = null;
    QMSMultiQuoteDAO                quoteDAO                = null;
    int[]                      selectedFrtIndices      = null;
    boolean[]                    isShipperZipCode        = null;
    boolean[]                    isConsigneeZipCode      = null;
    boolean[]                    isSingleShipperZone     = null;
    boolean[]                    isSingleConsigneeZone   = null;
    int         n;
      String      breakPoint    = null;
	  MultiQuoteFreightLegSellRates legRatesDetails = null;
	  int chrgSize = 0;
	  MultiQuoteCharges     chargesDOBs              = null;
	  ArrayList finalOrginChrgs =  null;
  	  ArrayList finalDestChrgs =  null;
    try
    {      
      connection                =         this.getConnection();
      
      masterDOB = finalDOB.getMasterDOB();
     

      cStmt                     =         connection.prepareCall("{ call qms_quote_pack.multi_quote_view_proc(?,?,?,?,?) }");
      cStmt.setString(1,quoteId);
      cStmt.registerOutParameter(2,OracleTypes.CURSOR);
      cStmt.registerOutParameter(3,OracleTypes.CURSOR);
      cStmt.registerOutParameter(4,OracleTypes.CURSOR);
      cStmt.registerOutParameter(5,OracleTypes.CURSOR);
      cStmt.execute();
      rs2  = (ResultSet) cStmt.getObject(2);
      rs3  = (ResultSet) cStmt.getObject(3);
      rs4  = (ResultSet) cStmt.getObject(4);
      rs5  = (ResultSet) cStmt.getObject(5);

      quoteDAO  =   new QMSMultiQuoteDAO();
      
 
 
   if(freightChargesList==null)
	  freightChargesList  = new ArrayList();
    
	legDetails = finalDOB.getLegDetails();
    legDOB =(MultiQuoteFreightLegSellRates)legDetails.get(0);

	 while(rs2.next())
	 {
		  flag = rs2.getString("SEL_BUY_FLAG");
		  chargeInfo  = new MultiQuoteChargeInfo();
		  chargeInfo.setBreakPoint(rs2.getString("CHARGESLAB"));
		//@@Added by kiran.v on 08/08/2011 for Wpbn Issue 258778
		  chargeInfo.setFrequencyChecked(rs2.getString("FREQUENCY_CHECKED"));
	      chargeInfo.setTransitTimeChecked(rs2.getString("TRANSITTIME_CHECKED"));
	      chargeInfo.setCarrierChecked(rs2.getString("CARRIER_CHECKED"));
	      chargeInfo.setRateValidityChecked(rs2.getString("RATEVALIDITY_CHECKED"));
	      chargeInfo.setServiceChecked("on");
	      chargeInfo.setServiceLevel(rs2.getString("SRV_LEVEL"));
	      chargeInfo.setCurrency(rs2.getString("CURRENCY"));
	      chargeInfo.setCarrier(rs2.getString("CURRENCY"));
	      chargeInfo.setRatio(rs2.getString("DENSITY_RATIO"));
		  if(rs2.getString("CURRENCY")!=null && rs2.getString("CURRENCY").trim().length()!=0)
			  chargeInfo.setCurrency(rs2.getString("CURRENCY"));
		  else
			  chargeInfo.setCurrency(masterDOB.getTerminalCurrency());

		  chargeInfo.setOriginPort(rs2.getString("ORG"));
		  chargeInfo.setDestPort(rs2.getString("DEST"));
		  chargeInfo.setCarrier(rs2.getString("CARRIER"));
		  chargeInfo.setServiceLevel(rs2.getString("SRV_LEVEL"));
		  chargeInfo.setFrequency(rs2.getString("FREQUENCY"));
		  //@@Added by kiran.v on 23/09/2011 for Wpbn Issue 272712
		  chargeInfo.setTransitTime(rs2.getString("TRANSITTIME"));
		  chargeInfo.setBuyRate(rs2.getDouble("BUYRATE"));
		  chargeInfo.setRecOrConSellRrate(rs2.getDouble("SELLRATE"));
		  chargeInfo.setSellChargeMargin(rs2.getDouble("MARGINVALUE"));
		  chargeInfo.setSellChargeMarginType(rs2.getString("MARGIN_TYPE"));
		  chargeInfo.setRateIndicator(rs2.getString("RATE_INDICATOR"));
		  chargeInfo.setLegNumber(Integer.parseInt(rs2.getString("LEG_SL_NO"))); // Added By Gowtham For PDF View Issue
		 
		  if("C.P.S.S".equalsIgnoreCase(rs2.getString("RATE_DESCRIPTION")))
			  chargeInfo.setRateDescription("P.S.S");
		  else
			  chargeInfo.setRateDescription(rs2.getString("RATE_DESCRIPTION")!=null?rs2.getString("RATE_DESCRIPTION"):"FREIGHT RATE");
		  if("M".equalsIgnoreCase(chargeInfo.getMarginDiscountFlag()) || chargeInfo.getMarginDiscountFlag()==null)
		  {  
			  chargeInfo.setMargin(rs2.getDouble("MARGINVALUE"));
			  chargeInfo.setMarginType(rs2.getString("MARGIN_TYPE"));

			  if("A".equalsIgnoreCase(rs2.getString("MARGIN_TYPE")))
				  sellRate  = rs2.getDouble("BUYRATE")+rs2.getDouble("MARGINVALUE");
			  else if("P".equalsIgnoreCase(rs2.getString("MARGIN_TYPE")))
				  sellRate  = rs2.getDouble("BUYRATE")+(rs2.getDouble("BUYRATE")*rs2.getDouble("MARGINVALUE")/100);

		  }
		  else
		  {
			  chargeInfo.setDiscount(rs2.getDouble("MARGINVALUE"));
			  chargeInfo.setDiscountType(rs2.getString("MARGIN_TYPE"));

			  if("A".equalsIgnoreCase(rs2.getString("MARGIN_TYPE")))
				  sellRate  = rs2.getDouble("SELLRATE")-rs2.getDouble("MARGINVALUE");
			  else if("P".equalsIgnoreCase(rs2.getString("MARGIN_TYPE")))
				  sellRate  = rs2.getDouble("SELLRATE")-(rs2.getDouble("SELLRATE")*rs2.getDouble("MARGINVALUE")/100);
		  }

		  weightBreak   =   rs2.getString("WEIGHT_BREAK");
		  rateType      =   rs2.getString("RATE_TYPE");
		  rateIndicator	= 	rs2.getString("RATE_INDICATOR");
		  if(!(("FREIGHT RATE".equalsIgnoreCase(chargeInfo.getRateDescription()))|| ("A FREIGHT RATE".equalsIgnoreCase(chargeInfo.getRateDescription())))) // Added by Gowtham to show the Basis Description in PDF
		  {
			  if("CSF".equalsIgnoreCase(chargeInfo.getBreakPoint()))
			  {
				  chargeInfo.setBasis("Per Shipment");
			  }

			  else if(chargeInfo.getBreakPoint().endsWith("CAF")||chargeInfo.getBreakPoint().endsWith("CAFLF")||chargeInfo.getBreakPoint().endsWith("BAF")||chargeInfo.getBreakPoint().endsWith("BAFLF")
					  ||chargeInfo.getBreakPoint().endsWith("CSF")||chargeInfo.getBreakPoint().endsWith("CSFLF")||chargeInfo.getBreakPoint().endsWith("PSS")||chargeInfo.getBreakPoint().endsWith("PSSLF")||chargeInfo.getBreakPoint().endsWith("caf")||chargeInfo.getBreakPoint().endsWith("caflf")
					  ||chargeInfo.getBreakPoint().endsWith("baf")||chargeInfo.getBreakPoint().endsWith("baflf")||chargeInfo.getBreakPoint().endsWith("csf")||chargeInfo.getBreakPoint().endsWith("csflf")||chargeInfo.getBreakPoint().endsWith("pss")||chargeInfo.getBreakPoint().endsWith("psslf")) // Added by Gowtham to show the Basis Description in PDF
			  {
				  if(chargeInfo.getBreakPoint().endsWith("CAF")||chargeInfo.getBreakPoint().endsWith("CAFLF")||chargeInfo.getBreakPoint().endsWith("caf")||chargeInfo.getBreakPoint().endsWith("caflf"))
				  {
					  chargeInfo.setBasis("Percent of Freight");
				  }
				  else
				  {
					  chargeInfo.setBasis("Per Container");
				  }
			  }
			  else if("FSBASIC".equalsIgnoreCase(chargeInfo.getBreakPoint())||"FSMIN".equalsIgnoreCase(chargeInfo.getBreakPoint())||
					  "SSBASIC".equalsIgnoreCase(chargeInfo.getBreakPoint())||"SSMIN".equalsIgnoreCase(chargeInfo.getBreakPoint())||"BAFMIN".equalsIgnoreCase(chargeInfo.getBreakPoint())||
					  "PSSMIN".equalsIgnoreCase(chargeInfo.getBreakPoint())||"CAFMIN".equalsIgnoreCase(chargeInfo.getBreakPoint())|| chargeInfo.getBreakPoint().toUpperCase().endsWith("BASIC")||chargeInfo.getBreakPoint().toUpperCase().endsWith("MIN")) // Added by Gowtham to show the Basis Description in PDF.
			  {

				  chargeInfo.setBasis("Per Shipment");
			  }
			  else if("FSKG".equalsIgnoreCase(chargeInfo.getBreakPoint())|| "SSKG".equalsIgnoreCase(chargeInfo.getBreakPoint())||chargeInfo.getBreakPoint().toUpperCase().endsWith("FLAT")) // Added by Gowtham to show the Basis Description in PDF
			  {
				  chargeInfo.setBasis("Per Kilogram");
			  }
			  else if("CAF%".equalsIgnoreCase(chargeInfo.getBreakPoint())||"SURCHARGE".equalsIgnoreCase(chargeInfo.getBreakPoint())||chargeInfo.getBreakPoint().toUpperCase().endsWith("PERCENT"))// Added by Gowtham to show the Basis Description in PDF
			  {

				  chargeInfo.setBasis("Percent of Freight");
			  }

			  else if("BAFM3".equalsIgnoreCase(chargeInfo.getBreakPoint())||"PSSM3".equalsIgnoreCase(chargeInfo.getBreakPoint()))
			  {
				  chargeInfo.setBasis("per Cubic Meter");
			  }
			  else if("Per Kg".equalsIgnoreCase(rs2.getString("CHARGEBASIS")))// Added by Gowtham on 22Feb2011
			  {
				  chargeInfo.setBasis("Per Kilogram");
			  }
			  else
				  chargeInfo.setBasis(rs2.getString("CHARGEBASIS")); // Added by Gowtham on 22Feb2011

		  }
		  else
		  {
			                   
			  if("BASE".equalsIgnoreCase(chargeInfo.getBreakPoint()) || "MIN".equalsIgnoreCase(chargeInfo.getBreakPoint()) || "MAX".equalsIgnoreCase(chargeInfo.getBreakPoint()) || ("SLAB".equalsIgnoreCase(weightBreak) && "FLAT".equalsIgnoreCase(rateType)) || ("BOTH".equalsIgnoreCase(rateType) && "FLAT".equalsIgnoreCase(rateIndicator)))
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

					  chargeInfo.setBasis("Per Container");

				  }
				  else if(("2".equalsIgnoreCase(rs2.getString("SHMODE"))&& "LCL".equalsIgnoreCase(rs2.getString("CONSOLE_TYPE")))&&("FLAT".equalsIgnoreCase(rs2.getString("WEIGHT_BREAK"))||"SLAB".equalsIgnoreCase(rs2.getString("WEIGHT_BREAK")))) // Added by Gowtham for LCL case.
				  {
					  chargeInfo.setBasis("Per Weight Measurement");
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
		  if(rs2.getString("margin_test_flag")!=null)
		  {
			  if(rs2.getString("margin_test_flag").equals("Y"))
					  chargeInfo.setMarginTestFailed(true);
				  else 
					  chargeInfo.setMarginTestFailed(false);
			  }
			  else
				  chargeInfo.setMarginTestFailed(false);

			  freightChargesList.add(chargeInfo);
	}
  legDetails.remove(0);
  legDOB.setFreightChargesList(freightChargesList);
  legDetails.add(0,legDOB);

      
 if(legDetails!=null)
    finalDOB.setLegDetails(legDetails);
     
      chargesDOB  = null;
      //rs4 ResultSet is used for Cartage Charges
      while(rs4.next())
      {
		  
     if("Pickup".equalsIgnoreCase(rs4.getString("COST_INCURREDAT")))
    {
    if( (masterDOB.getShipperZipCode()!=null && masterDOB.getShipperZipCode().length!=0)
        ||(masterDOB.getShipperZones()!=null && masterDOB.getShipperZones().length!=0) 
      )
    {
     // isSingleShipperZone = true;
    	// Modifed by Kishore Podili For Duplicates in MultiQuote View PDF
    	if(chargesDOB!=null && 
    	             (((chargesDOB.getSellChargeId()!=null && chargesDOB.getSellChargeId().equals(rs4.getString("SELLCHARGEID"))) || 
    	                 (chargesDOB.getBuyChargeId()!=null && chargesDOB.getBuyChargeId().equals(rs4.getString("BUY_CHARGE_ID")))
    	             )
    	            &&(chargesDOB.getChargeOrgin()!=null && chargesDOB.getChargeOrgin().equalsIgnoreCase(rs4.getString("ORG")) ) 
    	            &&(chargesDOB.getChargeDestination()!=null && chargesDOB.getChargeDestination().equalsIgnoreCase(rs4.getString("DEST")))
    	            &&(rs4.getString("ZONE")!=null && rs4.getString("ZONE").equals(chargesDOB.getZoneCode()))
    	            
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
        if("BASE".equalsIgnoreCase(chargeInfo.getBreakPoint()) || "MIN".equalsIgnoreCase(chargeInfo.getBreakPoint()) || "MAX".equalsIgnoreCase(chargeInfo.getBreakPoint()) || ("SLAB".equalsIgnoreCase(weightBreak) && "FLAT".equalsIgnoreCase(rateType)) || ("BOTH".equalsIgnoreCase(rateType) && "F".equalsIgnoreCase(chargeInfo.getRateIndicator())))
        {
          chargeInfo.setBasis("Per Shipment");
        }
        else
        {
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
          
          if("A".equalsIgnoreCase(rs4.getString("MARGIN_TYPE")))
          {
            sellRate  = rs4.getDouble("BUYRATE")+rs4.getDouble("MARGINVALUE");
         
          
          }
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
      
        chargeInfo.setSellRate(sellRate);
      if(rs4.getString("margin_test_flag")!=null)
      {
        if(rs4.getString("margin_test_flag").equals("Y"))
        chargeInfo.setMarginTestFailed(true);
        else 
        chargeInfo.setMarginTestFailed(false);
      }
      else
        chargeInfo.setMarginTestFailed(false);
  
     }
     else
     {         
        chargesDOB  = new MultiQuoteCharges();
        
        chargesDOB.setSellChargeId(rs4.getString("SELLCHARGEID"));
        chargesDOB.setBuyChargeId(rs4.getString("BUY_CHARGE_ID"));
                 
        chargesDOB.setSellBuyFlag(rs4.getString("SEL_BUY_FLAG"));
        chargesDOB.setChargeOrgin(rs4.getString("ORG"));
        chargesDOB.setChargeDestination(rs4.getString("DEST"));
        //Modifed By Kishore Podili For Multiple Zone Codes
        chargesDOB.setChargeDescriptionId(rs4.getString("RATE_DESCRIPTION"));
        chargesDOB.setZoneCode(rs4.getString("zone"));
               
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
        //chargeInfo.setBasis(rs4.getString("CHARGEBASIS"));
        weightBreak   =   rs4.getString("WEIGHT_BREAK");
        rateType      =   rs4.getString("RATE_TYPE");
        if("BASE".equalsIgnoreCase(chargeInfo.getBreakPoint()) || "MIN".equalsIgnoreCase(chargeInfo.getBreakPoint()) || "MAX".equalsIgnoreCase(chargeInfo.getBreakPoint()) || ("SLAB".equalsIgnoreCase(weightBreak) && "FLAT".equalsIgnoreCase(rateType)) || ("BOTH".equalsIgnoreCase(rateType) && "F".equalsIgnoreCase(chargeInfo.getRateIndicator())))
        {
          chargeInfo.setBasis("Per Shipment");
        }
        else
        {
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
          
          if("A".equalsIgnoreCase(rs4.getString("MARGIN_TYPE")))
          {
            sellRate  = rs4.getDouble("BUYRATE")+rs4.getDouble("MARGINVALUE");
               }
          else if("P".equalsIgnoreCase(rs4.getString("MARGIN_TYPE")))
            sellRate  = rs4.getDouble("BUYRATE")+(rs4.getDouble("BUYRATE")*rs4.getDouble("MARGINVALUE")/100);
        }
        else
        {
          chargeInfo.setDiscount(rs4.getDouble("MARGINVALUE"));
          chargeInfo.setDiscountType(rs4.getString("MARGIN_TYPE"));
          
          if("A".equalsIgnoreCase(rs4.getString("MARGIN_TYPE")))
          {
            sellRate  = rs4.getDouble("SELLRATE")-rs4.getDouble("MARGINVALUE");
            }
          else if("P".equalsIgnoreCase(rs4.getString("MARGIN_TYPE")))
            sellRate  = rs4.getDouble("SELLRATE")-(rs4.getDouble("SELLRATE")*rs4.getDouble("MARGINVALUE")/100);
        }
          
        chargeInfo.setSellRate(sellRate);
        if(rs4.getString("margin_test_flag")!=null)
        {
          if(rs4.getString("margin_test_flag").equals("Y"))
            chargeInfo.setMarginTestFailed(true);
          else 
            chargeInfo.setMarginTestFailed(false);
        }
        else
          chargeInfo.setMarginTestFailed(false);
        
        if("Pickup".equalsIgnoreCase(rs4.getString("COST_INCURREDAT")))
        {
          if(originChargesList==null)
            originChargesList = new ArrayList();
          originChargesList.add(chargesDOB);
        }
     }
    }
    /*else
    {
      tmpFinalDOB = new QuoteFinalDOB();
      tmpFinalDOB.setMasterDOB(masterDOB);
      tmpFinalDOB.setOriginChargesList(originChargesList);
      Logger.info(FILE_NAME,"masterDOB.getShipperZones()::"+masterDOB.getShipperZones());
      //Logger.info(FILE_NAME,"masterDOB.getConsigneeZones()::"+masterDOB.getConsigneeZones());
      if((masterDOB.getShipperZones()!=null && masterDOB.getShipperZones().trim().length()!=0))
          tmpFinalDOB = quoteDAO.getCartages(tmpFinalDOB);
   
      //finalDOB.setOriginChargesList(tmpFinalDOB.getOriginChargesList());
      originChargesList = tmpFinalDOB.getOriginChargesList();
      
    //  Logger.info(FILE_NAME,"tmpFinalDOB.getPickUpCartageRatesList()"+tmpFinalDOB.getPickUpCartageRatesList());
      
      if(tmpFinalDOB.getPickUpCartageRatesList()!=null)
        finalDOB.setPickUpCartageRatesList(tmpFinalDOB.getPickUpCartageRatesList());
      
      finalDOB.setPickZoneZipMap(tmpFinalDOB.getPickZoneZipMap());          
    }*/
  }
  else if("Delivery".equalsIgnoreCase(rs4.getString("COST_INCURREDAT")))
   { 
    if((masterDOB.getConsigneeZipCode()!=null && masterDOB.getConsigneeZipCode().length!=0)
        ||(masterDOB.getConsigneeZones()!=null && masterDOB.getConsigneeZones().length!=0))
    {
     // isSingleConsigneeZone = true; 
    	// Modifed by Kishore Podili For Duplicates in MultiQuote View PDF
   if(deliveryChargesDOB!=null && 
       	           (
       	             (((deliveryChargesDOB.getSellChargeId()!=null && deliveryChargesDOB.getSellChargeId().equals(rs4.getString("SELLCHARGEID"))) || 
       	                  (deliveryChargesDOB.getBuyChargeId()!=null && deliveryChargesDOB.getBuyChargeId().equals(rs4.getString("BUY_CHARGE_ID")))
       	             )
       	             &&
       	              (deliveryChargesDOB.getChargeOrgin()!=null && deliveryChargesDOB.getChargeOrgin().equalsIgnoreCase(rs4.getString("ORG"))) 
       	             &&
       	              deliveryChargesDOB.getChargeDestination()!=null && deliveryChargesDOB.getChargeDestination().equalsIgnoreCase(rs4.getString("DEST")))
       	             &&
       	              (rs4.getString("ZONE")!=null && rs4.getString("ZONE").equals(deliveryChargesDOB.getZoneCode()))
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
        if("BASE".equalsIgnoreCase(chargeInfo.getBreakPoint()) || "MIN".equalsIgnoreCase(chargeInfo.getBreakPoint()) || "MAX".equalsIgnoreCase(chargeInfo.getBreakPoint()) || ("SLAB".equalsIgnoreCase(weightBreak) && "FLAT".equalsIgnoreCase(rateType)) || ("BOTH".equalsIgnoreCase(rateType) && "F".equalsIgnoreCase(chargeInfo.getRateIndicator())))
        {
          chargeInfo.setBasis("Per Shipment");
        }
        else
        {
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
          
          if("A".equalsIgnoreCase(rs4.getString("MARGIN_TYPE")))
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
      
        chargeInfo.setSellRate(sellRate);
        if(rs4.getString("margin_test_flag")!=null)
        {
          if(rs4.getString("margin_test_flag").equals("Y"))
            chargeInfo.setMarginTestFailed(true);
          else 
           chargeInfo.setMarginTestFailed(false);
        }
        else
          chargeInfo.setMarginTestFailed(false);
     }
    else
     {         
        deliveryChargesDOB  = new MultiQuoteCharges();
        
        deliveryChargesDOB.setSellChargeId(rs4.getString("SELLCHARGEID"));
        deliveryChargesDOB.setBuyChargeId(rs4.getString("BUY_CHARGE_ID"));
                 
        deliveryChargesDOB.setSellBuyFlag(rs4.getString("SEL_BUY_FLAG"));
        deliveryChargesDOB.setChargeOrgin(rs4.getString("ORG"));
        deliveryChargesDOB.setChargeDestination(rs4.getString("DEST"));
        //Added By Kishore Podili for Multiple Zone Codes
        deliveryChargesDOB.setChargeDescriptionId(rs4.getString("RATE_DESCRIPTION"));
        deliveryChargesDOB.setZoneCode(rs4.getString("ZONE"));
        
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
        //chargeInfo.setBasis(rs4.getString("CHARGEBASIS"));
        weightBreak = rs4.getString("WEIGHT_BREAK");
        rateType    = rs4.getString("RATE_TYPE");
        
        if("BASE".equalsIgnoreCase(chargeInfo.getBreakPoint()) || "MIN".equalsIgnoreCase(chargeInfo.getBreakPoint()) || "MAX".equalsIgnoreCase(chargeInfo.getBreakPoint()) || ("SLAB".equalsIgnoreCase(weightBreak) && "FLAT".equalsIgnoreCase(rateType)) || ("BOTH".equalsIgnoreCase(rateType) && "F".equalsIgnoreCase(chargeInfo.getRateIndicator())))
        {
          chargeInfo.setBasis("Per Shipment");
        }
        else
        {
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
          else if("Per CFT".equalsIgnoreCase(rs4.getString("CHARGEBASIS")))
          {
            chargeInfo.setBasis("Per Cubic Feet");
          }
          else if("Per CBM".equalsIgnoreCase(rs4.getString("CHARGEBASIS")))
          {
            chargeInfo.setBasis("Per Cubic Meter");
          }
          else
            chargeInfo.setBasis(rs4.getString("CHARGEBASIS"));
        }
        
        chargeInfo.setRatio(rs4.getString("DENSITY_RATIO"));
        
        if("M".equalsIgnoreCase(deliveryChargesDOB.getMarginDiscountFlag()) || deliveryChargesDOB.getMarginDiscountFlag()==null)
        {  
          chargeInfo.setMargin(rs4.getDouble("MARGINVALUE"));
          chargeInfo.setMarginType(rs4.getString("MARGIN_TYPE"));
          
          if("A".equalsIgnoreCase(rs4.getString("MARGIN_TYPE")))
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
          
        chargeInfo.setSellRate(sellRate);
        if(rs4.getString("margin_test_flag")!=null)
        {
          if(rs4.getString("margin_test_flag").equals("Y"))
          chargeInfo.setMarginTestFailed(true);
          else 
          chargeInfo.setMarginTestFailed(false);
        }
        else
          chargeInfo.setMarginTestFailed(false);
          
       if("Delivery".equalsIgnoreCase(rs4.getString("COST_INCURREDAT")))
        {
          if(destChargesList==null)
            destChargesList = new ArrayList();
          destChargesList.add(deliveryChargesDOB);
        }
     }
   //@@ For putting the delivery charge at the end of the list, if it exists.
   
    }
  }
    
	  }
      
      /*String tempShipperCode    =   null;
      String tempShipperZones   =   null;
      String tempConsigneeCode  =   null;
      String tempConsigneeZones =   null;
      boolean isShipperFetched  =   false;
      boolean isConsigneeFetched=   false;
       
      if(masterDOB.getShipperZipCode()!=null && masterDOB.getShipperZipCode().trim().length()!=0)
          isShipperZipCode        =  true;
      if(masterDOB.getShipperZones()!=null && masterDOB.getShipperZones().indexOf(",")==-1)
          isSingleShipperZone = true;
      if(masterDOB.getConsigneeZipCode()!=null && masterDOB.getConsigneeZipCode().trim().length()!=0)
          isConsigneeZipCode      =  true;
      if(masterDOB.getConsigneeZones()!=null && masterDOB.getConsigneeZones().indexOf(",")==-1)
          isSingleConsigneeZone   =  true;
      
      /*if((isSingleShipperZone && ! isShipperZipCode)||(isSingleConsigneeZone && !isConsigneeZipCode))
      {
        finalDOB    =  quoteDAO.getZipZoneMapping(finalDOB);//@@So fetch the Zip Zone Mapping
      }
      else*/
      //{
     /* if((masterDOB.getShipperZones()!=null && masterDOB.getShipperZones().trim().length()!=0)
            ||(masterDOB.getConsigneeZones()!=null && masterDOB.getConsigneeZones().trim().length()!=0))
        {
          if(isShipperZipCode || isSingleShipperZone)
          {
             tempShipperCode  = masterDOB.getShipperZipCode();
             tempShipperZones = masterDOB.getShipperZones();
             masterDOB.setShipperZipCode(null);
             masterDOB.setShipperZones(null);
             isShipperFetched = true;
          }
          if(isConsigneeZipCode || isSingleConsigneeZone)
          {
             tempConsigneeCode  = masterDOB.getConsigneeZipCode();
             tempConsigneeZones = masterDOB.getConsigneeZones();
             masterDOB.setConsigneeZipCode(null);
             masterDOB.setConsigneeZones(null);
             isConsigneeFetched = true;
          }

          if(!(isShipperFetched && isConsigneeFetched))
          {
              tmpFinalDOB = new MultiQuoteFinalDOB();
              tmpFinalDOB.setMasterDOB(masterDOB);
              tmpFinalDOB.setOriginChargesList(originChargesList);
              tmpFinalDOB.setDestChargesList(destChargesList);
              logger.info("tmpFinalDOB ::"+tmpFinalDOB.getMasterDOB().getBuyRatesPermission());
              tmpFinalDOB = quoteDAO.getCartages(tmpFinalDOB);
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
            finalDOB    =  quoteDAO.getZipZoneMapping(finalDOB);
          }         
          
          //originChargesList = tmpFinalDOB.getOriginChargesList();
          //destChargesList = tmpFinalDOB.getDestChargesList();
          
          /*if(tmpFinalDOB.getPickUpCartageRatesList()!=null)
            finalDOB.setPickUpCartageRatesList(tmpFinalDOB.getPickUpCartageRatesList());
          if(tmpFinalDOB.getDeliveryCartageRatesList()!=null)
            finalDOB.setDeliveryCartageRatesList(tmpFinalDOB.getDeliveryCartageRatesList());
          
          finalDOB.setPickZoneZipMap(tmpFinalDOB.getPickZoneZipMap());
          finalDOB.setDeliveryZoneZipMap(tmpFinalDOB.getDeliveryZoneZipMap());*/
        /*}*/
     // }
      //@@Moved after the cartage charges rs by Yuvraj.
      chargesDOB  = null;
      //rs3 ResultSet is used for getting Charges  
      while(rs3.next())
      {
      if(chargesDOB!=null && 
       (
         (((chargesDOB.getSellChargeId()!=null && chargesDOB.getSellChargeId().equals(rs3.getString("SELLCHARGEID"))) || 
                 (chargesDOB.getBuyChargeId()!=null && chargesDOB.getBuyChargeId().equals(rs3.getString("BUY_CHARGE_ID"))))
        		 &&(chargesDOB.getChargeOrgin()!=null && chargesDOB.getChargeOrgin().equalsIgnoreCase(rs3.getString("ORG"))) &&
        		 (chargesDOB.getChargeDestination()!=null && chargesDOB.getChargeDestination().equalsIgnoreCase(rs3.getString("DEST"))))))
  
        {
          chargeInfo  = new MultiQuoteChargeInfo();
          
          chargeInfoList.add(chargeInfo);
          
          chargeInfo.setBreakPoint(rs3.getString("CHARGESLAB"));
          
          if(rs3.getString("CURRENCY")!=null && rs3.getString("CURRENCY").trim().length()!=0)
              chargeInfo.setCurrency(rs3.getString("CURRENCY"));
          else
              chargeInfo.setCurrency(masterDOB.getTerminalCurrency());
          chargeInfo.setOrginLoc(rs3.getString("ORG"));
          chargeInfo.setDestLoc(rs3.getString("DEST"));
          
          chargeInfo.setBuyRate(rs3.getDouble("BUYRATE"));
          chargeInfo.setRecOrConSellRrate(rs3.getDouble("SELLRATE"));
          chargeInfo.setSellChargeMargin(rs3.getDouble("MARGINVALUE"));
          chargeInfo.setSellChargeMarginType(rs3.getString("MARGIN_TYPE"));
          chargeInfo.setRateIndicator(rs3.getString("RATE_INDICATOR"));
          if("M".equalsIgnoreCase(chargesDOB.getMarginDiscountFlag()) || chargesDOB.getMarginDiscountFlag()==null)
          {  
            chargeInfo.setMargin(rs3.getDouble("MARGINVALUE"));
            chargeInfo.setMarginType(rs3.getString("MARGIN_TYPE"));
            
            if("A".equalsIgnoreCase(rs3.getString("MARGIN_TYPE")))
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
          weightBreak   = rs3.getString("WEIGHT_BREAK");
          rateType      = rs3.getString("RATE_TYPE");
          //chargeInfo.setBasis(rs3.getString("CHARGEBASIS"));
          if("BASE".equalsIgnoreCase(chargeInfo.getBreakPoint()) || "MIN".equalsIgnoreCase(chargeInfo.getBreakPoint()) || "MAX".equalsIgnoreCase(chargeInfo.getBreakPoint()) || ("SLAB".equalsIgnoreCase(weightBreak) && "FLAT".equalsIgnoreCase(rateType)) || ("BOTH".equalsIgnoreCase(rateType) && "F".equalsIgnoreCase(chargeInfo.getRateIndicator())))
          {
            chargeInfo.setBasis("Per Shipment");
          }
          else
          {
            chargeInfo.setBasis(rs3.getString("CHARGEBASIS"));
            
            //weightBreak   = rs3.getString("WEIGHT_BREAK");
          
            if(weightBreak!=null && ("Percent".equalsIgnoreCase(weightBreak) || weightBreak.endsWith("%")))
              chargeInfo.setPercentValue(true);
          }
          chargeInfo.setRatio(rs3.getString("DENSITY_RATIO"));
              
          chargeInfo.setSellRate(sellRate);
         if(rs3.getString("margin_test_flag")!=null)
          {
              if(rs3.getString("margin_test_flag").equals("Y"))
                chargeInfo.setMarginTestFailed(true);
              else 
                chargeInfo.setMarginTestFailed(false);
          }
        else
          chargeInfo.setMarginTestFailed(false);
        }
        else
        {        
          chargesDOB  = new MultiQuoteCharges();
          
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
          chargesDOB.setChargeOrgin(rs3.getString("ORG"));//Added by Mohan
		 chargesDOB.setChargeDestination(rs3.getString("DEST"));//Added by Mohan

          chargesDOB.setChargeId(rs3.getString("CHARGE_ID"));
          chargesDOB.setTerminalId(rs3.getString("TERMINALID"));
          chargesDOB.setMarginDiscountFlag(rs3.getString("MARGIN_DISCOUNT_FLAG"));
          chargesDOB.setChargeDescriptionId(rs3.getString("CHARGEDESCID"));
          chargesDOB.setInternalName(rs3.getString("INT_CHARGE_NAME"));
          chargesDOB.setExternalName(rs3.getString("EXT_CHARGE_NAME"));
          chargesDOB.setCostIncurredAt(rs3.getString("COST_INCURREDAT"));
          chargesDOB.setSelectedFlag(rs3.getString("SELECTED_FLAG"));
          
          if(reportDOB!=null && reportDOB.getChangeDesc().equals(chargesDOB.getChargeDescriptionId()))
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
          chargeInfo.setOrginLoc(rs3.getString("ORG"));
          chargeInfo.setDestLoc(rs3.getString("DEST"));
          chargeInfo.setBuyRate(rs3.getDouble("BUYRATE"));
          chargeInfo.setRecOrConSellRrate(rs3.getDouble("SELLRATE"));
          chargeInfo.setSellChargeMargin(rs3.getDouble("MARGINVALUE"));
          chargeInfo.setSellChargeMarginType(rs3.getString("MARGIN_TYPE"));
          chargeInfo.setRateIndicator(rs3.getString("RATE_INDICATOR"));
          weightBreak     =   rs3.getString("WEIGHT_BREAK");
          rateType        =   rs3.getString("RATE_TYPE");
          //chargeInfo.setBasis(rs3.getString("CHARGEBASIS"));
          if("BASE".equalsIgnoreCase(chargeInfo.getBreakPoint()) || "MIN".equalsIgnoreCase(chargeInfo.getBreakPoint()) || "MAX".equalsIgnoreCase(chargeInfo.getBreakPoint()) || ("SLAB".equalsIgnoreCase(weightBreak) && "FLAT".equalsIgnoreCase(rateType)) || ("BOTH".equalsIgnoreCase(rateType) && "F".equalsIgnoreCase(chargeInfo.getRateIndicator())))
          {
            chargeInfo.setBasis("Per Shipment");
          }
          else
          {
            chargeInfo.setBasis(rs3.getString("CHARGEBASIS"));
            
            //weightBreak   = rs3.getString("WEIGHT_BREAK");
          
            if(weightBreak!=null && ("Percent".equalsIgnoreCase(weightBreak) || weightBreak.endsWith("%")))
              chargeInfo.setPercentValue(true);
            
          }
          chargeInfo.setRatio(rs3.getString("DENSITY_RATIO"));
          
         if("M".equalsIgnoreCase(chargesDOB.getMarginDiscountFlag()) || chargesDOB.getMarginDiscountFlag()==null)
          {  
            chargeInfo.setMargin(rs3.getDouble("MARGINVALUE"));
            chargeInfo.setMarginType(rs3.getString("MARGIN_TYPE"));
            
            if("A".equalsIgnoreCase(rs3.getString("MARGIN_TYPE")))
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
              
          chargeInfo.setSellRate(sellRate);
          if(rs3.getString("margin_test_flag")!=null)
          {
              if(rs3.getString("margin_test_flag").equals("Y"))
              chargeInfo.setMarginTestFailed(true);
              else 
              chargeInfo.setMarginTestFailed(false);
          }
          else
            chargeInfo.setMarginTestFailed(false);
        }            
      }
	  //Setting charges to respective lanes
	  ArrayList laneDOB = finalDOB.getLegDetails();
	  ArrayList multiLegDetails =  new ArrayList();
	  int laneFinalSize = laneDOB.size();
	  int orgChrgSize=0;
	  int destChrgSize= 0;
	  for(int lane=0;lane<laneFinalSize;lane++)
	  {
			legRatesDetails= (MultiQuoteFreightLegSellRates)laneDOB.get(lane);
			
			if(originChargesList!=null)
				orgChrgSize = originChargesList.size();
			else
				orgChrgSize =0;
			
			if(orgChrgSize>0)
					finalOrginChrgs =  new ArrayList();

			for(int chrg=0;chrg<orgChrgSize;chrg++)
			{
				chargesDOBs = (MultiQuoteCharges)originChargesList.get(chrg);
				//System.out.println("Leg Org and Charge Org--->"+chargesDOBs.getChargeOrgin()+"<-->"+legRatesDetails.getOrigin()+"<--");
				//System.out.println("Leg Dest and Charge Dest--->"+ chargesDOBs.getChargeDestination()+"<-->"+legRatesDetails.getDestination()+"<--");
				if(chargesDOBs.getChargeOrgin().equalsIgnoreCase(legRatesDetails.getOrigin()) && chargesDOBs.getChargeDestination().equalsIgnoreCase(legRatesDetails.getDestination()))
				//if(legRatesDetails.getOrigin().endsWith(chargesDOBs.getChargeOrgin()) && legRatesDetails.getDestination().endsWith(chargesDOBs.getChargeDestination())) // Added by Gowtham to show origin and destination charges in PDF View
				{
					finalOrginChrgs.add(chargesDOBs);
				}
				chargesDOBs = null;
			}
			if(destChargesList!=null)
				destChrgSize = destChargesList.size();
			else
				destChrgSize=0;
			
			if(destChrgSize>0)
				finalDestChrgs = new ArrayList();

			for(int chrg=0;chrg<destChrgSize;chrg++)
			{
				chargesDOBs = (MultiQuoteCharges)destChargesList.get(chrg);
				if(chargesDOBs.getChargeOrgin().equalsIgnoreCase(legRatesDetails.getOrigin()) && chargesDOBs.getChargeDestination().equalsIgnoreCase(legRatesDetails.getDestination()))
				//if(legRatesDetails.getOrigin().endsWith(chargesDOBs.getChargeOrgin()) && legRatesDetails.getDestination().endsWith(chargesDOBs.getChargeDestination())) // Added by Gowtham to show origin and destination charges in PDF View
				{
					finalDestChrgs.add(chargesDOBs);

				}
				chargesDOBs = null;
			}
			if(finalDestChrgs!=null)
				legRatesDetails.setDestChargesList(finalDestChrgs);
			if(finalOrginChrgs!=null)
			legRatesDetails.setOriginChargesList(finalOrginChrgs);
			multiLegDetails.add(legRatesDetails);
		}
	
	if(legRatesDetails!=null)
	  finalDOB.setLegDetails(multiLegDetails);//Lane Wise Freight and Cartages,Chages

	  //End of chras
	/* --------- Commented By Kishore for Multiple Zone Codes ---------
      String tempShipperCode    =   null;
      String tempShipperZones   =   null;
      String tempConsigneeCode  =   null;
      String tempConsigneeZones =   null;
      
      String[] dumShipperCode    =   null;
      String[] dumShipperZones   =   null;
      String[] dumConsigneeCode  =   null;
      String[] dumConsigneeZones =   null;

	  
      boolean isShipperFetched[]  =   new boolean[laneFinalSize];
      boolean isConsigneeFetched[]=   new boolean[laneFinalSize];
      //ArrayList  lanDOB = finalDOB.getLegDetails();
	  //int laneFinalSize = lanDOB.size();

	 dumShipperCode =  new String[laneFinalSize];
	 dumShipperZones =  new String[laneFinalSize];
	 dumConsigneeCode =  new String[laneFinalSize];
	 dumConsigneeZones =  new String[laneFinalSize];
	 isShipperZipCode =  new boolean[laneFinalSize];
	 isConsigneeZipCode =  new boolean[laneFinalSize];
	 isSingleShipperZone=  new boolean[laneFinalSize];
	 isSingleConsigneeZone =  new boolean[laneFinalSize];
	 
	 --------- Commented By Kishore for Multiple Zone Codes --------- */
	 
	 ArrayList finalLegDetails = new ArrayList();

	  for(int zip=0;zip<laneFinalSize;zip++)
	  {
		  legRatesDetails  = (MultiQuoteFreightLegSellRates) laneDOB.get(zip);

		  /*
		   *  --------- Commented By Kishore for Multiple Zone Codes ---------
		   *  if(masterDOB.getShipperZipCode()!=null && masterDOB.getShipperZipCode().length!=0 && masterDOB.getShipperZipCode()[zip]!= null && !"".equals(masterDOB.getShipperZipCode()[zip]) )
					isShipperZipCode[zip]  =  true;

		  //Modified By Kishore For multiple Zone Codes 
		  if(masterDOB.getShipperZones()!=null && masterDOB.getShipperZones().length!=0  && masterDOB.getShipperZones()[zip]!= null && !"".equals(masterDOB.getShipperZones()[zip]) && masterDOB.getShipperZones()[zip].indexOf(",")==-1 ) // Modified By Kishore for Multiple Zone Codes
					isSingleShipperZone[zip]  =  true;
		   
		  if(masterDOB.getConsigneeZipCode()!=null && masterDOB.getConsigneeZipCode().length!=0 && masterDOB.getConsigneeZipCode()[zip]!= null && !"".equals(masterDOB.getConsigneeZipCode()[zip]) )
			  isConsigneeZipCode[zip]      =  true;
		  //Modified By Kishore For multiple Zone Codes
		  if(masterDOB.getConsigneeZones()!=null && masterDOB.getConsigneeZones().length!=0 && masterDOB.getConsigneeZones()[zip]!= null && !"".equals(masterDOB.getConsigneeZones()[zip]) && masterDOB.getConsigneeZones()[zip].indexOf(",")==-1 ) // Modified By Kishore for Multiple Zone Codes
			  isSingleConsigneeZone[zip]   =  true;
		  --------- Commented By Kishore for Multiple Zone Codes ---------
		 */
		
     
      if((masterDOB.getShipperZones()!=null && masterDOB.getShipperZones()[zip]!=null )
            ||(masterDOB.getConsigneeZones()!=null && masterDOB.getConsigneeZones()[zip]!=null))
        {
          /* --------- Commented By Kishore for Multiple Zone Codes ---------
           * if(isShipperZipCode[zip] || isSingleShipperZone[zip])
          {
             tempShipperCode  = masterDOB.getShipperZipCode()[zip];
             tempShipperZones = masterDOB.getShipperZones()[zip];
             dumShipperCode[zip]=null;
             dumShipperZones[zip]=null;
             masterDOB.setShipperZipCode(dumShipperCode);
             masterDOB.setShipperZones(dumShipperZones);
             isShipperFetched[zip]= true;
          }
          if(isConsigneeZipCode[zip] || isSingleConsigneeZone[zip])
          {
             tempConsigneeCode  = masterDOB.getConsigneeZipCode()[zip];
             tempConsigneeZones = masterDOB.getConsigneeZones()[zip];
             dumConsigneeCode[zip]= null;
             dumConsigneeZones[zip]= null;
             masterDOB.setConsigneeZipCode(dumConsigneeCode);
             masterDOB.setConsigneeZones(dumConsigneeZones);
             isConsigneeFetched[zip] = true;
          }
         // Changed By Kishore
          if(!(isShipperFetched[zip]) && !(isConsigneeFetched[zip]))
          {
           --------- Commented By Kishore for Multiple Zone Codes --------- 
          */
             if(masterDOB.getShipperZipCode()[zip]!=null || masterDOB.getShipperZones()[zip]!=null || masterDOB.getConsigneeZipCode()[zip]!=null || masterDOB.getConsigneeZones()[zip]!=null)
              { 
                tmpFinalDOB = new MultiQuoteFinalDOB();
				MultiQuoteFreightLegSellRates tempLegRateDetials = null;
				masterDOB.setOperation("View");//@@Added by kiran.v on 21/02/2012
                tmpFinalDOB.setMasterDOB(masterDOB);
                //tmpFinalDOB.setLegDetails(finalDOB.getLegDetails()); //Added by kishore
                //tmpFinalDOB.setOriginChargesList(originChargesList);
               // tmpFinalDOB.setDestChargesList(destChargesList);
                //tempLegRateDetials = quoteDAO.getMultiQuoteCartages(tmpFinalDOB,zip,getOriginChargesList,destChargesList);//888888888888
                tempLegRateDetials = quoteDAO.getMultiQuoteCartages(tmpFinalDOB,zip);//888888888888
                
                //commented for not using here and move to set selected indeces logic
               // originChargesList = tempLegRateDetials.getOriginChargesList();
                //destChargesList = tempLegRateDetials.getDestChargesList();
                
                if(tempLegRateDetials.getPickUpCartageRatesList()!=null)
                  legRatesDetails.setPickUpCartageRatesList(tempLegRateDetials.getPickUpCartageRatesList());
                if(tempLegRateDetials.getDeliveryCartageRatesList()!=null)
                  legRatesDetails.setDeliveryCartageRatesList(tempLegRateDetials.getDeliveryCartageRatesList());
                if(tempLegRateDetials.getPickupWeightBreaks()!=null)
                  legRatesDetails.setPickupWeightBreaks(tempLegRateDetials.getPickupWeightBreaks());
                if(tempLegRateDetials.getDeliveryWeightBreaks()!=null)
                  legRatesDetails.setDeliveryWeightBreaks(tempLegRateDetials.getDeliveryWeightBreaks());
              //Added By Kishore For the ChargeBasis in the Annexure PDF on 26-May-11
                if(tempLegRateDetials.getPickupChargeBasisList()!=null)
                    legRatesDetails.setPickupChargeBasisList(tempLegRateDetials.getPickupChargeBasisList());
                
                if(tempLegRateDetials.getDelChargeBasisList()!=null)
                    legRatesDetails.setDelChargeBasisList(tempLegRateDetials.getDelChargeBasisList());
                
                legRatesDetails.setPickZoneZipMap(tempLegRateDetials.getPickZoneZipMap());
                legRatesDetails.setDeliveryZoneZipMap(tempLegRateDetials.getDeliveryZoneZipMap()); 
              }
          }
         /*
          *  --------- Commented By Kishore for Multiple Zone Codes ---------
          *   if(isShipperFetched[zip])
          {
			  dumShipperCode[zip]=tempShipperCode;
			  dumShipperZones[zip] = tempShipperZones;
             // legRatesDetails.setShipperZipCode(tempShipperCode);
             // legRatesDetails.setShipperZones(tempShipperZones);
			  masterDOB.setShipperZipCode(dumShipperCode);
              masterDOB.setShipperZones(dumShipperZones);

          }
          if(isConsigneeFetched[zip])
          {
			   dumConsigneeCode[zip]  =tempConsigneeCode;
			   dumConsigneeZones[zip] =tempConsigneeZones;
			  // legRatesDetails.setConsigneeZipCode(tempConsigneeCode);
			  // legRatesDetails.setConsigneeZones(tempConsigneeZones);
			   masterDOB.setConsigneeZipCode(dumConsigneeCode);
			   masterDOB.setConsigneeZones(dumConsigneeZones);
		              
          }
          
        //  finalDOB.setMasterDOB(masterDOB);
          
          if(!isShipperFetched[zip] || !isConsigneeFetched[zip] || !isShipperZipCode[zip] || !isConsigneeZipCode[zip])
          {
            finalDOB    =  quoteDAO.getMultiQuoteZipZoneMapping(finalDOB,zip);//8888888
			//zipMap    =  quoteDAO.getZipZoneMapping(finalDOB);
          }  */       
          
          //originChargesList = tmpFinalDOB.getOriginChargesList();
          //destChargesList = tmpFinalDOB.getDestChargesList();
          
          /*if(tmpFinalDOB.getPickUpCartageRatesList()!=null)
            finalDOB.setPickUpCartageRatesList(tmpFinalDOB.getPickUpCartageRatesList());
          if(tmpFinalDOB.getDeliveryCartageRatesList()!=null)
            finalDOB.setDeliveryCartageRatesList(tmpFinalDOB.getDeliveryCartageRatesList());
          
          finalDOB.setPickZoneZipMap(tmpFinalDOB.getPickZoneZipMap());
          finalDOB.setDeliveryZoneZipMap(tmpFinalDOB.getDeliveryZoneZipMap());
           --------- Commented By Kishore for Multiple Zone Codes ---------
           
        }*/
      
      originChargesList = legRatesDetails.getOriginChargesList();
      destChargesList = legRatesDetails.getDestChargesList();
      
      if(originChargesList!=null || destChargesList!=null)
	  {
        if(originChargesList!=null)
        {
          int originChargesSize = 0;
          originChargesSize     = originChargesList.size();

          int[] originSelectedIndices = new int[originChargesSize];
          
          for(int i=0;i<originChargesSize;i++)
          {
            originSelectedIndices[i]  = i;
          }
          legRatesDetails.setOriginChargesList(originChargesList);
          legRatesDetails.setSelectedOriginChargesListIndices(originSelectedIndices);
        }
        if(destChargesList!=null)
        {
          int destChargesSize = 0;
          destChargesSize     = destChargesList.size();
          int[] destSelectedIndices = new int[destChargesSize];
              
          for(int i=0;i<destChargesSize;i++)
          {
            destSelectedIndices[i]  = i;
          }
          legRatesDetails.setSelctedDestChargesListIndices(destSelectedIndices);
          legRatesDetails.setDestChargesList(destChargesList);
        }
      }
	  finalLegDetails.add(legRatesDetails);
	  } 
	  /* --------- Commented By Kishore for Multiple Zone Codes --------- 
	   * masterDOB.setConsigneeZipCode(dumConsigneeCode);
	  masterDOB.setConsigneeZones(dumConsigneeZones);
	  masterDOB.setShipperZipCode(dumShipperCode);
	  masterDOB.setShipperZones(dumShipperZones);
	  --------- Commented By Kishore for Multiple Zone Codes --------- 
	  */
	 finalDOB.setOperation("View");
	 finalDOB.setMasterDOB(masterDOB);
	 finalDOB.setLegDetails(finalLegDetails);
	//Added by Mohan for Issue No.219976 on 04-11-2010
      String[]  inotes = null;
      String[]  enotes = null;
      ArrayList iNotes = new ArrayList();
      ArrayList eNotes =  new ArrayList();
      int notesCount  = 0 ;  
      while(rs5.next())
      {
    	  iNotes.add((rs5.getString("INTERNAL_NOTES")!=null)?rs5.getString("INTERNAL_NOTES"):"");
    	  eNotes.add((rs5.getString("EXTERNAL_NOTES")!=null)?rs5.getString("EXTERNAL_NOTES"):"");
      }
      notesCount = iNotes.size();      
      inotes = new String[notesCount];
      enotes = new String[notesCount];
      for( int i=0; i<notesCount;i++)
      {
    	  inotes[i] = (String)iNotes.get(i);
    	  enotes[i] = (String)eNotes.get(i);
      }
     

      finalDOB.setInternalNotes(inotes);//Modified by Mohan for Issue No.219976 on 30102010
      finalDOB.setExternalNotes(enotes);//Modified by Mohan for Issue No.219976 on 30102010
    }    
    catch(Exception e)
    {
      e.printStackTrace();
      logger.error(FILE_NAME+"getQuoteIds6) "+e.toString());
      logger.error(FILE_NAME+"QMSQuoteSessionBean[getRatesChargesInfo(quoteId)] -> "+e.toString());
			throw new EJBException(e.toString());    
    }
    finally
    {
      try
      {
        if(rs5!=null)rs5.close();
        if(rs4!=null)rs4.close();
        if(rs3!=null)rs3.close();
        if(rs2!=null)rs2.close();
        if(cStmt!=null)cStmt.close();
      }
      catch(SQLException se)
      {
        se.printStackTrace();
        //Logger.error(FILE_NAME,"QMSQuoteSessionBean[getRatesChargesInfo(quoteId)] -> "+se.toString());
        logger.error(FILE_NAME+"QMSQuoteSessionBean[getRatesChargesInfo(quoteId)] -> "+se.toString());
			  throw new EJBException(se.toString());      
      }
      catch(Exception e)
      {
        e.printStackTrace();
        //Logger.error(FILE_NAME,"QMSQuoteSessionBean[getRatesChargesInfo(quoteId)] -> "+e.toString());
        logger.error(FILE_NAME+"QMSQuoteSessionBean[getRatesChargesInfo(quoteId)] -> "+e.toString());
        throw new EJBException(e.toString());        
      }
      ConnectionUtil.closeConnection(connection);      
    }
    return finalDOB;
  }   
public String getServiceLevelDesc(String QuoteId) throws EJBException
{
    Connection                connection              = null;
    ResultSet                 rs                      = null;
    ResultSet                 rs1                     = null;
    ResultSet                 rs2                     = null;
    ResultSet                 rs3                     = null;
    PreparedStatement         pstmt                   = null;
    PreparedStatement         pstmt1                  = null;
    PreparedStatement         pstmt2                  = null;
    PreparedStatement         pstmt3                  = null;
    String                    idSpotFlag              = " SELECT ID,SPOT_RATES_FLAG  FROM QMS_QUOTE_MASTER "+
                                                        " WHERE QUOTE_ID = ? AND (QUOTE_STATUS IN ('ACC', 'NAC') AND " +
                                                        " ACTIVE_FLAG IN ('A', 'I')  OR (QUOTE_STATUS NOT IN('ACC','NAC') AND ACTIVE_FLAG IN('A') )) "+
                                                        " AND VERSION_NO = (SELECT MAX(VERSION_NO) FROM QMS_QUOTE_MASTER WHERE QUOTE_ID = ? ) ";
   String                    spotFlagYes              = " SELECT DISTINCT SR.SERVICELEVEL FROM QMS_QUOTE_SPOTRATES SR WHERE SR.QUOTE_ID= ? ";
   String                    bRLnVnId                 = " SELECT DISTINCT BUYRATE_ID, RATE_LANE_NO, VERSION_NO  FROM QMS_QUOTE_RATES "+
                                                        " WHERE QUOTE_ID = ? AND SELL_BUY_FLAG IN ('BR','RSR')";
   String                   spotFlagNo                = " SELECT DISTINCT BD.SERVICE_LEVEL    FROM QMS_BUYRATES_DTL BD "+
                                                        " WHERE BUYRATEID = ? AND LANE_NO = ? AND VERSION_NO = ? "+
                                                        " AND BD.SERVICE_LEVEL NOT IN ('SCH')";
   String                    serviceLevelDesc         = " SELECT SERVICELEVELDESC FROM FS_FR_SERVICELEVELMASTER WHERE SERVICELEVELID = ? ";
   long                      id                       = 0;
   String                    spotRateFlag             = "";
   long                      buyrateid                = 0;
   int                       laneno                   = 0;
   int                       versionno                = 0;
   String                    serviceLevelId           = "";
   String                    serviceLevelDes          = "";
  try
  {
    connection  = this.getConnection();
    pstmt  = connection.prepareStatement(idSpotFlag);
    pstmt.setString(1,QuoteId);
    pstmt.setString(2,QuoteId);
    rs  = pstmt.executeQuery();
    if(rs.next())
    {
      id           = rs.getLong("ID");
      spotRateFlag = rs.getString("SPOT_RATES_FLAG");
    }
    if("Y".equalsIgnoreCase(spotRateFlag))
    {
      pstmt1  = connection.prepareStatement(spotFlagYes);
      pstmt1.setLong(1,id);
      rs1 = pstmt1.executeQuery();
      if(rs1.next())
      {
        serviceLevelId =  rs1.getString("SERVICELEVEL");
      }
   }
   else
   {
     pstmt1  = connection.prepareStatement(bRLnVnId);
     pstmt1.setLong(1,id);
     rs1 = pstmt1.executeQuery();
     if(rs1.next())
     {
       buyrateid = rs1.getLong("BUYRATE_ID");
       laneno    = rs1.getInt("RATE_LANE_NO");
       versionno = rs1.getInt("VERSION_NO");
       pstmt2  = connection.prepareStatement(spotFlagNo);
       pstmt2.setLong(1,buyrateid);
       pstmt2.setInt(2,laneno);
       pstmt2.setInt(3,versionno);
       rs2 = pstmt2.executeQuery();
       if(rs2.next())
       {
         serviceLevelId =  rs2.getString("SERVICE_LEVEL");
       }
     }
   }
   pstmt3  = connection.prepareStatement(serviceLevelDesc);
   pstmt3.setString(1,serviceLevelId);
   rs3 = pstmt3.executeQuery();
   if(rs3.next())
   {
     serviceLevelDes = rs3.getString("SERVICELEVELDESC");
   }
  }
  catch(SQLException sqEx)
  {
        sqEx.printStackTrace();
        logger.error(FILE_NAME+"QMSQuoteSessionBean[getServiceLevelDesc(QuoteId)] ->while viewing the Quote "+sqEx.toString());
        throw new EJBException(sqEx.toString());

  }
  catch(Exception e)
  {
      e.printStackTrace();
      logger.error(FILE_NAME+"QMSQuoteSessionBean[getServiceLevelDesc(QuoteId)] ->while viewing the Quote  "+e.toString());
      throw new EJBException(e.toString());

  }
  finally
  {
    ConnectionUtil.closeConnection(connection,pstmt,rs);
    ConnectionUtil.closePreparedStatement(pstmt1,rs1);
    ConnectionUtil.closePreparedStatement(pstmt2,rs2);
    ConnectionUtil.closePreparedStatement(pstmt3,rs3);
  }
  return serviceLevelDes;
}

 //Added by Rakesh on 11-01-2010
public ArrayList getQuoteGroupIds(QuoteMasterDOB masterDOB) throws EJBException
{
 Connection                connection              = null;
 Statement                 stmt                    = null;
 StringBuffer              query                   = new StringBuffer("");    
 ResultSet                 rs                      = null; 
 ArrayList                 quoteIds                = new ArrayList();
 String                    origins                 = "";
 String                    destinations            = "";
 try
 {      
      String[] destLocIds   = masterDOB.getDestLocation().split(","); 
      String[] originLocIds = masterDOB.getOriginLocation().split(",");
      int orgLocIdLen	=	originLocIds.length;
      for(int i=0;i<orgLocIdLen;i++)
     {
       if((i+1)==originLocIds.length)
           origins  = origins + "'"+originLocIds[i]+"'";
       else        
           origins  = origins + "'"+originLocIds[i]+"',";
     }
     int destLocIdsLen	=	destLocIds.length;
     for(int i=0;i<destLocIdsLen;i++)
     {
       if((i+1)==destLocIds.length)
           destinations  = destinations + "'"+destLocIds[i]+"'";
       else        
           destinations  = destinations + "'"+destLocIds[i]+"',";
     }
      
      connection        =    this.getConnection();
      //query             =   "SELECT DISTINCT QUOTE_ID FROM QMS_QUOTE_MASTER WHERE CUSTOMER_ID=? AND ORIGIN_LOCATION IN(?) AND DEST_LOCATION IN(?)";
      query.append("SELECT DISTINCT QUOTE_ID FROM QMS_QUOTE_MASTER WHERE ACTIVE_FLAG='A' AND SHIPMENT_MODE=").append(masterDOB.getShipmentMode()).append(" ");
      query.append(" AND QUOTE_STATUS NOT IN ('QUE') AND ESCALATION_FLAG='N' AND IS_MULTI_QUOTE='Y' ");
      if(masterDOB.getCustomerId()!=null && masterDOB.getCustomerId().trim().length()!=0)
         query.append("AND ").append("CUSTOMER_ID='").append(masterDOB.getCustomerId()).append("' ");
      if(masterDOB.getOriginLocation()!=null && masterDOB.getOriginLocation().trim().length()!=0)
         query.append("AND ").append("ORIGIN_LOCATION IN(").append(origins).append(") ");
      if(masterDOB.getDestLocation()!=null && masterDOB.getDestLocation().trim().length()!=0)
         query.append("AND ").append("DEST_LOCATION IN(").append(destinations).append(")");
      
       stmt           =   connection.createStatement();
       
       rs  = stmt.executeQuery(query.toString());
       
       while(rs.next())
          quoteIds.add(rs.getString(1));
   
   }
 catch(Exception e)
 {
   //Logger.error(FILE_NAME,"Error in getQuoteGroups"+e);
   logger.error(FILE_NAME+"Error in getQuoteGroups"+e);
   e.printStackTrace();
   throw new EJBException(e.toString());
 }
 finally
 {
   ConnectionUtil.closeConnection(connection,stmt,rs);
 }
 return  quoteIds;
}
//Added by Rakesh on 12-01-2010
public ArrayList  getQuoteGroups(String[] quoteIds,ESupplyGlobalParameters loginbean) throws EJBException
{
 Connection                connection              = null;
 PreparedStatement         psmt                    = null;
 String                    query                   = null;    
 ResultSet                 rs                      = null; 
 ArrayList                 chargeGroups            = new ArrayList();
 try
 {      
    connection        =    this.getConnection();
    //Commented by Anusha V
    //query             =   "SELECT  M.QUOTE_ID,C.CHARGEGROUPID  FROM QMS_QUOTE_MASTER M,QMS_QUOTE_CHARGEGROUPDTL C WHERE M.QUOTE_ID=? AND M.VERSION_NO=(SELECT MAX(VERSION_NO) FROM QMS_QUOTE_MASTER WHERE QUOTE_ID=?) AND M.ID=C.ID AND IS_MULTI_QUOTE='Y'";
  //Added by Anusha V
    query             =   "SELECT  M.QUOTE_ID,C.CHARGEGROUPID  FROM QMS_QUOTE_MASTER M,QMS_QUOTE_CHARGEGROUPDTL C WHERE M.QUOTE_ID=? AND M.VERSION_NO=(SELECT MAX(VERSION_NO) FROM QMS_QUOTE_MASTER WHERE QUOTE_ID=?) AND M.ID=C.QUOTE_ID AND IS_MULTI_QUOTE='Y'";
    psmt              =   connection.prepareStatement(query);
    int quoteIdLen	=	quoteIds.length;
    for(int m=0;m<quoteIdLen;m++)
    {          
       psmt.clearParameters();
      //@@ Commented by subrahmanyam for the enhancement #146971 on 10/12/2008          
       /*psmt.setInt(1,Integer.parseInt(quoteIds[m]));
       psmt.setInt(2,Integer.parseInt(quoteIds[m]));*/
      //@@ Added by subrahmanyam for the enhancement #146971 on 10/12/2008          
       psmt.setString(1,quoteIds[m]);
       psmt.setString(2,quoteIds[m]);

       
       if(rs!=null)
         rs.close();
       
       rs  = psmt.executeQuery();
       
       while(rs.next())
        chargeGroups.add(rs.getString(1)+","+rs.getString(2)); 
    } 
   
   }
 catch(Exception e)
 {
   //Logger.error(FILE_NAME,"Error in getQuoteGroups"+e);
   logger.error(FILE_NAME+"Error in getQuoteGroups"+e);
   e.printStackTrace();
   throw new EJBException(e.toString());
 }
 finally
 {
   ConnectionUtil.closeConnection(connection,psmt,rs);
 }
 return  chargeGroups;
}
//Added by Rakesh on 12-01-2011
public CostingMasterDOB getQuoteRateInfo(CostingHDRDOB costingHDRDOB,ESupplyGlobalParameters loginbean) 
{
  QMSMultiQuoteDAO  qMSMultiQuoteDAO = null;
  CostingMasterDOB costingMasterDOB = null;
  try
  {
	  qMSMultiQuoteDAO = new QMSMultiQuoteDAO();
      
      costingMasterDOB = qMSMultiQuoteDAO.getQuoteRateInfo(costingHDRDOB,loginbean);
      
      
  }catch(Exception e)
  {
    e.printStackTrace();
    throw new EJBException();
  }

  return costingMasterDOB;
}

//Added for the Issue 234719
public MultiQuoteFinalDOB getUpdatedQuoteInfo(long uniqueId,String changeDesc,String sellBuyFlag,String buyRatesFlag,ESupplyGlobalParameters loginbean,String quoteType) throws EJBException
{
  MultiQuoteFinalDOB      finalDOB        =     null;
  MultiQuoteMasterDOB     masterDOB       =     null;
  QMSMultiQuoteDAO        quoteDAO        =     null;
  Connection         conn            =     null;
  PreparedStatement  pStmt           =     null;
  ResultSet          rs              =     null;
  //long               QuoteId         =     0;  //@@ Commented by subrahmanyam for the enhancement #146971 on 03/12/2008
  String               QuoteId       =null;    //@@ Added by subrahmanyam for teh enhancement #146971 on 03/12/2008
  
   
  try
  {
    conn           =   this.getConnection();
    pStmt          =   conn.prepareStatement("SELECT QUOTE_ID FROM QMS_QUOTE_MASTER WHERE ID=?");
    pStmt.setLong(1,uniqueId);
    
    rs             =    pStmt.executeQuery();
    
    if(rs.next())
       //QuoteId     =   rs.getLong("QUOTE_ID");  //@@ Commented by subrahmanyam for the enhancement #146971 on 03/12/2008
       QuoteId     =   rs.getString("QUOTE_ID");  //@@ Added by subrahmanyam for the enhancement #146971 on 03/12/2008
    
    quoteDAO       =   new QMSMultiQuoteDAO();
    finalDOB       =   getMasterInfo(""+QuoteId,loginbean);
    masterDOB      =   finalDOB.getMasterDOB();
    masterDOB.setUserId(loginbean.getUserId());
    masterDOB.setBuyRatesPermission(buyRatesFlag);
 
      masterDOB.setCompanyId(loginbean.getCompanyId());
    finalDOB.setMasterDOB(masterDOB);
    finalDOB       =   getQuoteHeader(finalDOB);
    finalDOB       =   quoteDAO.getUpdatedQuoteInfo(uniqueId,changeDesc,sellBuyFlag,finalDOB,quoteType);

    finalDOB       =   getQuoteContentDtl(finalDOB);
 }
  catch(EJBException ejb)
  {
    //Logger.error(FILE_NAME,"EJBException While Fetching Updated Quotes Data"+ejb);
    logger.error(FILE_NAME+"EJBException While Fetching Updated Quotes Data"+ejb);
    ejb.printStackTrace();
    throw new EJBException(ejb);
  }
  catch(SQLException sql)
  {
    //Logger.error(FILE_NAME,"SQLException While Fetching Updated Quotes Data"+sql);
    logger.error(FILE_NAME+"SQLException While Fetching Updated Quotes Data"+sql);
    sql.printStackTrace();
    throw new EJBException(sql);
  }
  catch(Exception e)
  {
    //Logger.error(FILE_NAME,"Exception While Fetching Updated Quotes Data"+e);
    logger.error(FILE_NAME+"Exception While Fetching Updated Quotes Data"+e);
    e.printStackTrace();
    throw new EJBException(e);
  }
  finally
  {
    ConnectionUtil.closeConnection(conn,pStmt,rs);
  }
  return finalDOB;
}//Ended for the issue 234719
  // Added by Gowtham for Origin/Destination LocationName in Pdf Land Scape Issue. 
public String getLocationName(String locName) throws EJBException
{
	   Connection                connection              = null;
	   PreparedStatement         pstmt                   = null;
	   String					 locname				 = null;
	   ResultSet				 rs						 = null; 	
	   try
	   {
		   connection = this.getConnection();
		   pstmt	  = connection.prepareStatement("SELECT LOCATIONNAME FROM FS_FR_LOCATIONMASTER WHERE LOCATIONID = ?");
		   pstmt.setString(1,locName);
		   rs 			= pstmt.executeQuery();
		   if(rs.next())
			   locname = rs.getString("LOCATIONNAME");
		   else
			   locname = "";
	   }
	   catch(SQLException sql)
	   {
	     logger.error(FILE_NAME+"SQLException While Fetching Location Name"+sql);
	     sql.printStackTrace();
	     throw new EJBException(sql);
	   }
	   catch(Exception e)
	   {
	     logger.error(FILE_NAME+"Exception While Fetching Location Name"+e);
	     e.printStackTrace();
	     throw new EJBException(e);
	   }
	   finally
	   {
	     ConnectionUtil.closeConnection(connection,pstmt,rs);
	   }
	  return locname;
   
}

public String getPortName(String portName) throws EJBException
{
	   Connection                connection              = null;
	   PreparedStatement         pstmt                   = null;
	   String					 portname				 = null;
	   ResultSet				 rs						 = null; 	
	   try
	   {
		   connection = this.getConnection();
		   pstmt	  = connection.prepareStatement("SELECT PORTNAME FROM FS_FRS_PORTMASTER WHERE PORTID = ?");
		   pstmt.setString(1,portName);
		   rs 			= pstmt.executeQuery();
		   if(rs.next())
			   portname = rs.getString("PORTNAME");
		   else
			   portname = "";
	   }
	   catch(SQLException sql)
	   {
	     logger.error(FILE_NAME+"SQLException While Fetching Location Name"+sql);
	     sql.printStackTrace();
	     throw new EJBException(sql);
	   }
	   catch(Exception e)
	   {
	     logger.error(FILE_NAME+"Exception While Fetching Location Name"+e);
	     e.printStackTrace();
	     throw new EJBException(e);
	   }
	   finally
	   {
	     ConnectionUtil.closeConnection(connection,pstmt,rs);
	   }
	  return portname;
   
}
// Added by Gowtham for ServiceLevel Description.
public String getServiceLevelName(String ServLevelName) throws EJBException
{
	   Connection                connection              = null;
	   PreparedStatement         pstmt                   = null;
	   String					 ServLevelname			 = null;
	   ResultSet				 rs						 = null; 	
	   try
	   {
		   connection = this.getConnection();
		   pstmt	  = connection.prepareStatement("SELECT SERVICELEVELDESC FROM FS_FR_SERVICELEVELMASTER WHERE SERVICELEVELID = ?");
		   pstmt.setString(1,ServLevelName);
		   rs 			= pstmt.executeQuery();
		   if(rs.next())
			   ServLevelname = rs.getString("SERVICELEVELDESC");
		   else
			   ServLevelname = "";
	   }
	   catch(SQLException sql)
	   {
	     logger.error(FILE_NAME+"SQLException While Fetching Location Name"+sql);
	     sql.printStackTrace();
	     throw new EJBException(sql);
	   }
	   catch(Exception e)
	   {
	     logger.error(FILE_NAME+"Exception While Fetching Location Name"+e);
	     e.printStackTrace();
	     throw new EJBException(e);
	   }
	   finally
	   {
	     ConnectionUtil.closeConnection(connection,pstmt,rs);
	   }
	  return ServLevelname;
   
}
	// // Added by Gowtham for ServiceLevel Description. in Pdf Land Scape Issue. 


//@@ Added by Gowtham for PDF  LandScape Issue in View Case.

public ArrayList getChargeInfoDetailsforView(String quoteId)
{
		Connection                	connection              = null;
		PreparedStatement         	pstmt                   = null;
		PreparedStatement         	pstmt1                  = null;
		PreparedStatement         	pstmt2                  = null;
		String					 	locname				 	= null;
		ResultSet				 	rs						= null; 
		ResultSet				 	rs1						= null; 
		ResultSet				 	rs2						= null; 
		MultiQuoteChargeInfo 		viewDOB					= null;
		String						multiBreakPts			= "";
		String[]					multiBreakPts1			= null;
		String						multiBuyRates			= "";
		String[]					multiBuyRates1			= null;
		String						frequency				="";
		String						carrier					="";	
		String						Currency 				= null;
		String						origin					="";
		String						destination				="";
		String						serviceLevel			="";
		String						incoTerms				="";
		String						mWeightBrk				="";
		ArrayList					chargesInfoList			= new ArrayList();
		int 						i 						=0;
		int							brkSize					=0;
		String						basis					=	"";
		String						basis1					=	"";
		String						brkPt					= null;
		double                      sellrate                = 0.00;
		double                      buyrate                 = 0.00;
		String                      density_Code            = null;
		DecimalFormat               deciFormat              = new DecimalFormat("#0.00");
		String               densityQryForSPR   = 	"SELECT DISTINCT SPR.DENSITY_CODE FROM QMS_QUOTE_SPOTRATES SPR WHERE SPR.QUOTE_ID IN (SELECT QMS.ID FROM QMS_QUOTE_MASTER QMS WHERE QMS.QUOTE_ID =? AND QMS.ACTIVE_FLAG ='A' AND QMS.SPOT_RATES_FLAG LIKE 'Y')";
		String               densityQryForBRorRSR   = "SELECT DISTINCT DENSITY_CODE FROM QMS_BUYRATES_DTL WHERE BUYRATEID =?";
		//Added by kiran.v on 26/09/2011 for Wpbn Issue 272712
		String                      transitTime            ="";
		String                      freightValidity        ="";
		 try
		   {
			   connection = this.getConnection();
			   pstmt = connection.prepareStatement("SELECT SHIPMENT_MODE,ID,ORIGIN_LOCATION,DEST_LOCATION,MULTI_QUOTE_SERVICE_LEVEL,INCO_TERMS_ID,MULTI_QUOTE_WEIGHT_BREAK FROM QMS_QUOTE_MASTER WHERE QUOTE_ID = ? And VERSION_NO = (SELECT MAX(VERSION_NO) FROM QMS_QUOTE_MASTER QMS WHERE QMS.QUOTE_ID =?)");
			   pstmt.setString(1,quoteId);
			   pstmt.setString(2,quoteId);
			   rs = pstmt.executeQuery();
			   Map<String, String> map = new HashMap<String, String>();
			  
			   while(rs.next())
			   {	
				   
				   viewDOB = new MultiQuoteChargeInfo();
				   multiBreakPts = "";
				   multiBuyRates = "";
				   carrier="";
				   frequency = "";
				   i=0;
				   //carrier = carrier+rs.getString(""))
				   origin = rs.getString("ORIGIN_LOCATION");
				   destination= rs.getString("DEST_LOCATION");
				   serviceLevel =rs.getString("MULTI_QUOTE_SERVICE_LEVEL");
				   incoTerms = rs.getString("INCO_TERMS_ID");
				   mWeightBrk = rs.getString("MULTI_QUOTE_WEIGHT_BREAK");
				   //Modified by kiran.v on 26/09/2011 for Wpbn Issue 272712
				   // 07/11/2011
				   pstmt1 = connection.prepareStatement("SELECT BREAK_POINT,BUY_RATE,R_SELL_RATE,FREQUENCY,CARRIER,SELL_BUY_FLAG,TRANSIT_TIME TRANSITTIME,"+
						                                "to_char(RATE_VALIDITY,'dd-Mon-yyyy') FREIGHTValidity,DECODE(SELL_BUY_FLAG,'BR',(SELECT BM.CURRENCY "+
						                                " FROM QMS_BUYRATES_MASTER BM WHERE BM.BUYRATEID = QR.BUYRATE_ID"+
						                                " AND QR.RATE_LANE_NO = BM.LANE_NO "+
						                                " AND QR.VERSION_NO = BM.VERSION_NO),'RSR',(SELECT BM.CURRENCY "+
						                                " FROM QMS_BUYRATES_MASTER BM "+
						                                " WHERE BM.BUYRATEID = QR.BUYRATE_ID "+
						                                " AND QR.RATE_LANE_NO = BM.LANE_NO "+
						                                " AND QR.VERSION_NO = BM.VERSION_NO),'SBR',"+
						                                " (SELECT SP.CURRENCYID FROM QMS_QUOTE_SPOTRATES SP  WHERE SP.QUOTE_ID=?"+
						                                " AND SP.WEIGHT_BREAK_SLAB = QR.BREAK_POINT)) CURRENCY,BUYRATE_ID,"+ 
						   								"  DECODE(SELL_BUY_FLAG,'SBR'," +
						   								"(SELECT DISTINCT SPR.DENSITY_CODE FROM QMS_QUOTE_SPOTRATES SPR WHERE SPR.QUOTE_ID IN (?))/*SELECT QMS.ID FROM QMS_QUOTE_MASTER QMS WHERE QMS.QUOTE_ID = AND QMS.ACTIVE_FLAG ='A' AND QMS.SPOT_RATES_FLAG LIKE 'Y')*/," +
						   								"(SELECT DISTINCT DENSITY_CODE FROM QMS_BUYRATES_DTL WHERE BUYRATEID = BUYRATE_ID  AND ACTIVEINACTIVE IS NULL )  )DENSITY_RATIO,"+ 
						   								" MARGIN,DISCOUNT,MARGIN_TYPE,DISCOUNT_TYPE,MARGIN_DISCOUNT_FLAG, CHARGE_DESCRIPTION FROM QMS_QUOTE_RATES QR WHERE QUOTE_ID =? AND SELL_BUY_FLAG IN('BR','RSR','SBR')"); 
				   pstmt1.setString(1,rs.getString("ID"));
				   pstmt1.setString(2,rs.getString("ID"));
				   pstmt1.setString(3,rs.getString("ID"));
				   rs1 = pstmt1.executeQuery();
				   while(rs1.next())
				   {
					  /* if(!"SBR".equalsIgnoreCase(rs1.getString("SELL_BUY_FLAG"))){
					   pstmt2 = connection.prepareStatement(densityQryForBRorRSR);
					   pstmt2.setString(1,rs1.getString("BUYRATE_ID"));
					   }else{
						   pstmt2 = connection.prepareStatement(densityQryForSPR);
						   pstmt2.setString(1,quoteId);   
					   }
					   rs2 = pstmt2.executeQuery();*/
					   density_Code = rs1.getString("DENSITY_RATIO");
					   multiBreakPts = multiBreakPts+rs1.getString("BREAK_POINT")+",";
					   if("M".equalsIgnoreCase(rs1.getString("MARGIN_DISCOUNT_FLAG")))
						   {
							   if("A".equalsIgnoreCase(rs1.getString("MARGIN_TYPE")))

        					buyrate	= Double.parseDouble(deciFormat.format((rs1.getDouble("BUY_RATE"))+(rs1.getDouble("MARGIN"))));
        				else if("P".equalsIgnoreCase(rs1.getString("MARGIN_TYPE")))
        				    buyrate= Double.parseDouble(deciFormat.format((rs1.getDouble("BUY_RATE"))+((rs1.getDouble("BUY_RATE")*rs1.getDouble("MARGIN"))/100)));
						     
								  multiBuyRates = multiBuyRates+buyrate+",";
							 
						}	else if("D".equalsIgnoreCase(rs1.getString("MARGIN_DISCOUNT_FLAG")))
						   {
	
	                          if("A".equalsIgnoreCase(rs1.getString("MARGIN_TYPE")))
        					sellrate = Double.parseDouble(deciFormat.format((rs1.getDouble("R_SELL_RATE"))-(rs1.getDouble("MARGIN"))));
        				 else if("P".equalsIgnoreCase(rs1.getString("MARGIN_TYPE")))
        					sellrate= Double.parseDouble(deciFormat.format((rs1.getDouble("R_SELL_RATE"))-((rs1.getDouble("R_SELL_RATE")*(rs1.getDouble("MARGIN")))/100)));
						          multiBuyRates = multiBuyRates+sellrate+",";
						 }else{
                             if(rs1.getString("SELL_BUY_FLAG").toUpperCase().equalsIgnoreCase("RSR"))
					   {
							 sellrate =     Double.parseDouble(deciFormat.format(rs1.getDouble("R_SELL_RATE")));
						      multiBuyRates = multiBuyRates+sellrate+",";
					   }else{
						   buyrate =       Double.parseDouble(deciFormat.format(rs1.getDouble("BUY_RATE")));
						   multiBuyRates = multiBuyRates+buyrate+",";
					   }
							  
						 }
				      if(i==0)
					   {
					   carrier		 = rs1.getString("CARRIER");
					   frequency	 = rs1.getString("FREQUENCY");
					   Currency      = rs1.getString("CURRENCY");
					 //Added by kiran.v on 26/09/2011 for Wpbn Issue 272712
					   transitTime   = rs1.getString("TRANSITTIME");
					   freightValidity=rs1.getString("FREIGHTValidity");
					   i++;
					   }
				      // Added By Kishroe For Surcharge in PDF 
				      String chargeDesc = rs1.getString("CHARGE_DESCRIPTION");
				      String breakPoint = rs1.getString("BREAK_POINT");
				      
				      /*if(chargeDesc!=null && !"-".equals(chargeDesc) && !"A FREIGHT RATE".equals(chargeDesc)){
				  		  map.put(breakPoint.length()>8?breakPoint.substring(0, 3):breakPoint,chargeDesc.substring(0, chargeDesc.length()-3) );
				  	  }*/
                                     if(!"List".equalsIgnoreCase(mWeightBrk)){
	                	   if(chargeDesc!=null && !"-".equals(chargeDesc) && !"A FREIGHT RATE".equals(chargeDesc)){
	                		   if(breakPoint.length()>=7)
		                		 map.put(breakPoint.substring(0, 3),chargeDesc.substring(0,chargeDesc.length()-3) );
		                	 }
	                  	}
	                	
	                   if("List".equalsIgnoreCase(mWeightBrk)){
				      if(chargeDesc!=null && !"-".equals(chargeDesc) && !"A FREIGHT RATE".equals(chargeDesc)){
	                		   	if(breakPoint.length()>4 && breakPoint.length()<10)
		                		 map.put(breakPoint.substring(4,7),chargeDesc.substring(0,chargeDesc.length()-3) );
		                	 }
				  	  }
				   }
				  
				   /*Iterator<Entry<String, String>> it = map.entrySet().iterator();
				   System.out.println("--- From Sessionbean---");
				    while (it.hasNext()) {
				        Map.Entry<String, String> pairs = it.next();
				        System.out.println(pairs.getKey() + " - " + pairs.getValue());
				    }*/
				    
				    
				   
				   viewDOB.setSurChragesMap(map);
				   
				   /*if(rs2!=null && rs2.next())
					   viewDOB.setRatio(rs2.getString("DENSITY_CODE"));
				   else
				   viewDOB.setRatio("");*/
				   viewDOB.setRatio(density_Code);
				   multiBreakPts1 = multiBreakPts.split(",");
				   multiBuyRates1 = multiBuyRates.split(",");
				   viewDOB.setMultiBreakPoints(multiBreakPts1);
				   viewDOB.setMultiBuyRates(multiBuyRates1);
				   viewDOB.setCarrier(carrier);
				   viewDOB.setOrginLoc(origin);
				   viewDOB.setDestLoc(destination);
				   viewDOB.setServiceLevel(serviceLevel);
				   viewDOB.setIncoTerms(incoTerms);
				   viewDOB.setFrequency(frequency);
				   viewDOB.setCurrency(Currency);
				 //Added by kiran.v on 26/09/2011 for Wpbn Issue 272712
				   viewDOB.setTransitTime(transitTime);
				   viewDOB.setRateValidity(freightValidity);	
				   
				   brkSize = multiBreakPts1.length;
				   for(int j=0;j<brkSize;j++)
				   {	
					   if("LIST".equalsIgnoreCase(mWeightBrk)&& "2".equals(rs.getString("SHIPMENT_MODE")))
					   {
						   //basis = "Per Container";//commented by silpa.p on 5-07-11
						   basis = "Per Ctr";//modified by silpa.p on 5-07-11
						   basis1= basis1+basis+",";
					   }
					else if("2".equals(rs.getString("SHIPMENT_MODE")))
					   {
						   if(multiBreakPts1[j].toUpperCase().endsWith("MIN")||multiBreakPts1[j].toUpperCase().endsWith("BASIC")||multiBreakPts1[j].equalsIgnoreCase("ABSOLUTE")||multiBreakPts1[j].equalsIgnoreCase("PERCENT"))
							   // basis = "Per Shipment";
								  basis = "Per Shpt";
						  else if(multiBreakPts1[j].length()>7)
						  {		
							  brkPt= multiBreakPts1[j].substring(3,5);
							  if(brkPt.equalsIgnoreCase("FF")|| brkPt.equalsIgnoreCase("SB")||brkPt.equalsIgnoreCase("SS"))
							 //  basis = "Per Weight Measurement";
								  basis = "Per W/M";  
							  else if(brkPt.equalsIgnoreCase("SF")||brkPt.equalsIgnoreCase("AF"))
								 // basis = "Per Shipment";
								  basis = "Per Shpt";
							  
							  else if(brkPt.equalsIgnoreCase("FP")||brkPt.equalsIgnoreCase("SP"))
								//  basis = "Percent of Freight rates";
								  basis = "% of freight rates";
							
							  else
								// basis = "Per Weight Measurement";
								  basis = "Per W/M";
								  
							}
						  else
							 // basis = "Per Weight Measurement";
							  basis = "Per W/M";
						   basis1=basis1+basis+",";
					   }
					   else if("1".equals(rs.getString("SHIPMENT_MODE")))
					   {
						   if(multiBreakPts1[j].toUpperCase().endsWith("MIN")||multiBreakPts1[j].toUpperCase().endsWith("BASIC")||multiBreakPts1[j].equalsIgnoreCase("ABSOLUTE")||multiBreakPts1[j].equalsIgnoreCase("PERCENT"))
							//   basis = "Per Shipment";	
							   basis = "Per Shpt";
						   else if(multiBreakPts1[j].length()>7)
						   {
							   brkPt= multiBreakPts1[j].substring(3,5);
							   if(brkPt.equalsIgnoreCase("FF")|| brkPt.equalsIgnoreCase("SB")||brkPt.equalsIgnoreCase("SS"))
								  // basis = "Per Kilogram";
								   basis = "Per Kg";
								  else if(brkPt.equalsIgnoreCase("SF"))
									  //  basis = "Per Shipment";
									  basis = "Per Shpt";
								  else if(brkPt.equalsIgnoreCase("LP"))
									  basis = "Per ULD Container";
							          /*else if (brkPt.equalsIgnoreCase("AF"))
								   basis = "Per Kilogram";*/
								  else
									// basis = "Per Shipment";
							       basis = "Per Shpt";
						   }
						   else
							// basis = "Per Kilogram";
							   basis = "Per Kg";
						   basis1=basis1+basis+",";
					   }
				   }
				   viewDOB.setBasis(basis1);
				   
				   chargesInfoList.add(viewDOB);
			   }
			   	
			   	
			  
			   
		}
		 catch(SQLException sql)
		   {
		     logger.error(FILE_NAME+"SQLException While Fetching Location Name"+sql);
		     sql.printStackTrace();
		     throw new EJBException(sql);
		   }
		   catch(Exception e)
		   {
		     logger.error(FILE_NAME+"Exception While Fetching Location Name"+e);
		     e.printStackTrace();
		     throw new EJBException(e);
		   }
		   finally
		   {
		     ConnectionUtil.closeConnection(connection,pstmt,rs);
		     ConnectionUtil.closeConnection(connection,pstmt1,rs1);
		     ConnectionUtil.closeConnection(connection,pstmt2,rs2);
		   }
		   return chargesInfoList;
	}

// @@ Added by Gowtham for PDF  LandScape Issue in View Case.

  public MultiQuoteFreightLegSellRates getLocFullNames(MultiQuoteFreightLegSellRates legDOB,String org,String dest,String orgLoc,String destLoc){

		Connection                	connection              = null;
		PreparedStatement         	pstmt                   = null;
		String					 	locname				 	= null;
		ResultSet				 	rs						= null; 
		ArrayList					chargesInfoList			= new ArrayList();
		 try
		   {
			   connection = this.getConnection();
			   pstmt = connection.prepareStatement("SELECT DISTINCT LOC.LOCATIONNAME FROM FS_FR_LOCATIONMASTER LOC WHERE LOC.LOCATIONID =?   OR LOC.LOCATIONID = ?");
			   pstmt.setString(1,org);
			   pstmt.setString(2,orgLoc);
			   rs = pstmt.executeQuery();
			   while(rs.next())
			   {
				   legDOB.setOrgFullName(rs.getString(1));   
			   }
			   pstmt.clearParameters();
			   rs=null;
			   pstmt = connection.prepareStatement("SELECT DISTINCT LOC.LOCATIONNAME FROM FS_FR_LOCATIONMASTER LOC WHERE LOC.LOCATIONID=?  OR LOC.LOCATIONID = ?");
			   pstmt.setString(1,dest);
			   pstmt.setString(2,destLoc);
			   rs = pstmt.executeQuery();
			   while(rs.next())
			   {
				   legDOB.setDestFullName(rs.getString(1));
			   }
			   pstmt.clearParameters();	
			  
			   
		}
		 catch(SQLException sql)
		   {
		     logger.error(FILE_NAME+"SQLException While Fetching Location Name"+sql);
		     sql.printStackTrace();
		     throw new EJBException(sql);
		   }
		   catch(Exception e)
		   {
		     logger.error(FILE_NAME+"Exception While Fetching Location Name"+e);
		     e.printStackTrace();
		     throw new EJBException(e);
		   }
		   finally
		   {
		     ConnectionUtil.closeConnection(connection,pstmt,rs);
		    
		   }
		   return legDOB;
	
	  
  }

//added by silpa.p on 16-05-11
public String getCarrierName(String CarrierDesc) throws EJBException
{
	   Connection                connection              = null;
	   PreparedStatement         pstmt                   = null;
	   String					 carrierName			 = null;
	   ResultSet				 rs						 = null; 	
	   try
	   {
		   connection = this.getConnection();
		   pstmt	  = connection.prepareStatement("SELECT CARRIERNAME FROM FS_FR_CAMASTER WHERE CARRIERID = ?");
		  // pstmt.setString(1,carrierName);
		 //@@Modified by kiran.v on 09/08/2011 for Wpbn Issue 258778
		   pstmt.setString(1,CarrierDesc);
		   rs 			= pstmt.executeQuery();
		   if(rs.next())
			   carrierName = rs.getString("CARRIERNAME");
		   else
			   carrierName = "";
	   }
	   catch(SQLException sql)
	   {
	     logger.error(FILE_NAME+"SQLException While Fetching Location Name"+sql);
	     sql.printStackTrace();
	     throw new EJBException(sql);
	   }
	   catch(Exception e)
	   {
	     logger.error(FILE_NAME+"Exception While Fetching Location Name"+e);
	     e.printStackTrace();
	     throw new EJBException(e);
}
	   finally
	   {
	     ConnectionUtil.closeConnection(connection,pstmt,rs);
	   }
	  return carrierName;
   
}
}