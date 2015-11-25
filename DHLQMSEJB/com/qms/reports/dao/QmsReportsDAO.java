/**
 * @ (#) BuyChargesDAO.java
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
 * File       : BuyChargesDAO.java
 * Sub-Module : Buy charges master
 * Module     : QMS
 * @author    : Venkatasatish
 * * @date 26-10-2005
 * Modified by      Date     Reason
 */

package com.qms.reports.dao;

import com.foursoft.esupply.common.exception.FoursoftException;
import com.foursoft.esupply.common.java.LookUpBean;
import com.foursoft.esupply.common.util.ESupplyDateUtility;
import com.foursoft.etrans.common.util.java.OperationsImpl;
import com.qms.reports.java.QMSChargesReportDOB;
import com.qms.reports.java.QMSRatesReportDOB;
import com.qms.reports.java.UpdatedQuotesDOB;
import com.qms.reports.java.UpdatedQuotesReportDOB;
import com.qms.reports.java.YieldDetailsDOB;
import java.sql.CallableStatement;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import javax.ejb.CreateException;
import javax.sql.DataSource;
import java.sql.Connection;
import javax.ejb.EJBException;
import javax.naming.NamingException;
import javax.naming.InitialContext;
import javax.ejb.ObjectNotFoundException;
import javax.naming.NameNotFoundException;
import java.util.ArrayList;
import com.foursoft.esupply.common.bean.ESupplyGlobalParameters;

import com.foursoft.esupply.common.util.ConnectionUtil;
//import com.foursoft.esupply.common.util.Logger;
import org.apache.log4j.Logger;

import com.qms.reports.java.ReportDetailsDOB;
import com.qms.reports.java.ReportsEnterIdDOB;

import oracle.jdbc.OracleTypes;

public class QmsReportsDAO 
{
    static final String FILE_NAME="QmsReportsDAO.java";
    private transient DataSource  dataSource=null;
    private static Logger logger = null;
   
  public QmsReportsDAO()
  {
      logger  = Logger.getLogger(QmsReportsDAO.class);
      try
      {
        InitialContext ic =new InitialContext();
        //dataSource        =(DataSource)ic.lookup("java:comp/env/jdbc/DB");
        dataSource        =(DataSource)ic.lookup("java:comp/env/jdbc/DB");
      }
      catch(NamingException nex)
      {
        throw new EJBException(nex.toString());
      }catch(Exception e)
      {
        throw new EJBException(e.toString());        
      }  
  }
  /**
   * This getBuyRatesExpiryReportData() fetches the values from the DB by procedure QMS_REPORT_PKG.QMS_BYRATE_EXPIRY_REPORT_TEMP
   * for the buy rate expiry. 
   * @throws javax.ejb.EJBException
   * @return 
   * @param reportsEnterIdDOB
   */
    public ArrayList getBuyRatesExpiryReportData(ReportsEnterIdDOB reportenterDob)throws EJBException
    {
      Connection  connection=null;
      CallableStatement  cstmt          = null;
      ResultSet           rs            = null;      
      long              expiryinDays     =  0;
      Timestamp        validuptodate    =  null;
      Timestamp        currentdate      =  null;
      Timestamp        effectivefromdate=  null;
      DataSource  dataSource            = null;
      ReportDetailsDOB reportDetailsDOB = null;
      OperationsImpl   oimp             = null;
    
      ArrayList        dataList         = null;    
      ArrayList        mainDataList     = null;
      ArrayList        pageIterList     = null;

      try
      {
        oimp=new OperationsImpl();
        connection= oimp.getConnection();
        dataList         = new ArrayList();
        mainDataList     = new ArrayList();
        pageIterList     = new ArrayList();
        
        cstmt = connection.prepareCall("{call QMS_REPORT_PKG.QMS_BYRATE_EXPIRY_REPORT(?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");
        cstmt.clearParameters();
        cstmt.setString(1,reportenterDob.getShipmentMode());
       //@@ Commented by subrahmanyam for the Enhancement 146451  on 15/02/09
        //cstmt.setString(2,reportenterDob.getCarrierId().replace(',','~'));
        //cstmt.setString(3,reportenterDob.getServiceLevel().replace(',','~'));
//@@ Added by subrahmanyam for the Enhancement 146451 on 15/02/09        
        if(reportenterDob.getCarrierId()!=null)
        cstmt.setString(2,reportenterDob.getCarrierId().replace(',','~'));
        else
        cstmt.setString(2,null);
        if(reportenterDob.getServiceLevel()!=null)
        cstmt.setString(3,reportenterDob.getServiceLevel().replace(',','~'));
          else
        cstmt.setString(3,null);
//@@ Ended by subrahmanyam for the enhancement 146451 on 15/02/09        
        cstmt.setString(4,reportenterDob.getBasis());
        cstmt.setString(5,reportenterDob.getExpiryActiveIndicator());
        cstmt.setTimestamp(6,reportenterDob.getFromDate1());        
        cstmt.setTimestamp(7,reportenterDob.getToDate1());
        cstmt.setInt(8,reportenterDob.getPageNo());
        cstmt.setInt(9,reportenterDob.getNoOfRecords());        
        cstmt.setString(10,reportenterDob.getSortBy());
        cstmt.setString(11,reportenterDob.getSortOrder());
        cstmt.registerOutParameter(12, OracleTypes.INTEGER);
        cstmt.registerOutParameter(13, OracleTypes.INTEGER);        
        
        cstmt.registerOutParameter(14, OracleTypes.CURSOR);
        cstmt.execute();       

        rs = (ResultSet)cstmt.getObject(14);
        String currdate   =new  com.foursoft.esupply.common.util.ESupplyDateUtility().getCurrentDateStringWithTime("DD-MM-YYYY");
        currentdate         = new ESupplyDateUtility().getTimestamp("DD/MM/YY",currdate);
          while(rs.next())
          {   
              reportDetailsDOB = new ReportDetailsDOB();
              reportDetailsDOB.setFromCountry(rs.getString("COUNTRYID")!=null?rs.getString("COUNTRYID"):"");
              reportDetailsDOB.setFromLocation(rs.getString("ORIGIN")!=null?rs.getString("ORIGIN"):"");
              reportDetailsDOB.setToCountry(rs.getString("DESTCOUNTRYID")!=null?rs.getString("DESTCOUNTRYID"):"");
              reportDetailsDOB.setToLocation(rs.getString("DESTINATION")!=null?rs.getString("DESTINATION"):"");
              reportDetailsDOB.setCarrierId(rs.getString("CARRIER_ID")!=null?rs.getString("CARRIER_ID"):"");
              reportDetailsDOB.setServiceLevel(rs.getString("SERVICE_LEVEL")!=null?rs.getString("SERVICE_LEVEL"):"");
              reportDetailsDOB.setCreateDate(rs.getTimestamp("CREATEDTIME"));
              reportDetailsDOB.setEffectiveFrom(rs.getTimestamp("EFFECTIVE_FROM"));
              reportDetailsDOB.setValidUpto(rs.getTimestamp("VALID_UPTO"));
              reportDetailsDOB.setShipmentMode(rs.getString("SHIPMENT_MODE")!=null?rs.getString("SHIPMENT_MODE"):""); 
              long expiry =0L;
              if(rs.getTimestamp("VALID_UPTO")!=null &&  reportenterDob.getExpiryActiveIndicator().equals("Y"))
                { 
                   validuptodate   = rs.getTimestamp("VALID_UPTO");
                   expiry = validuptodate.getTime()-currentdate.getTime();
                }
              else
              {
                   effectivefromdate    = rs.getTimestamp("EFFECTIVE_FROM");
                   expiry = currentdate.getTime()-effectivefromdate.getTime();
              }
              if(expiry>0)
                { expiry = expiry/(24*60*60*1000);}
              
              reportDetailsDOB.setExpiryinDays(expiry);
              dataList.add(reportDetailsDOB);
          }
           pageIterList.add(new Integer(cstmt.getInt(12)));   
           pageIterList.add(new Integer(cstmt.getInt(13)));
           mainDataList.add(pageIterList);
           mainDataList.add(dataList);           
      }
      catch(SQLException e)
      {
        e.printStackTrace();
        //Logger.error(FILE_NAME,"SQLException in getBuyRatesExpiryReportData( ) method"+e.toString());
        logger.error(FILE_NAME+"SQLException in getBuyRatesExpiryReportData( ) method"+e.toString());
        throw new EJBException();        
      }catch(EJBException e)
      {
      e.printStackTrace();
        //Logger.error(FILE_NAME,"EJBException in getBuyRatesExpiryReportData() method"+e.toString());
        logger.error(FILE_NAME+"EJBException in getBuyRatesExpiryReportData() method"+e.toString());
        throw new EJBException();         
      }catch(Exception e)
      {
      e.printStackTrace();
        //Logger.error(FILE_NAME,"Exception in getBuyRatesExpiryReportData() method"+e.toString());
        logger.error(FILE_NAME+"Exception in getBuyRatesExpiryReportData() method"+e.toString());
        throw new EJBException();         
      }finally
      {
        try
        {
          if(rs!=null)
          {rs.close();}
          if(cstmt!=null)
          { cstmt.close();}
          if(connection!=null)
          { connection.close();}
        }catch(SQLException e)
        {
          //Logger.error(FILE_NAME,"SQLException in getBuyRatesExpiryReportData( ) method"+e.toString());
          logger.error(FILE_NAME+"SQLException in getBuyRatesExpiryReportData( ) method"+e.toString());
          throw new EJBException();        
        }
      }
       return mainDataList;
    }
    
/**
   * This getBuyRatesExpiryReportDataForExcl() fetches the values from the DB by procedure QMS_REPORT_PKG.QMS_BYRATE_EXPIRY_REPORT_TEMP
   * for the buy rate expiry. 
   * @throws javax.ejb.EJBException
   * @return 
   * @param reportsEnterIdDOB
   */
    public ArrayList getBuyRatesExpiryReportDataForExcl(ReportsEnterIdDOB reportenterDob)throws EJBException
    {
      Connection  connection=null;
      CallableStatement  cstmt          = null;
      ResultSet           rs            = null;      
      long              expiryinDays     =  0;
      Timestamp        validuptodate    =  null;
      Timestamp        currentdate      =  null;
      Timestamp        effectivefromdate=  null;
      DataSource  dataSource            = null;
      ReportDetailsDOB reportDetailsDOB = null;
      OperationsImpl   oimp             = null;
    
      ArrayList        dataList         = null;    
      ArrayList        mainDataList     = null;
      ArrayList        pageIterList     = null;

      try
      {
        oimp=new OperationsImpl();
        connection= oimp.getConnection();
        dataList         = new ArrayList();
        mainDataList     = new ArrayList();
        pageIterList     = new ArrayList();
       System.out.println("dao");
        cstmt = connection.prepareCall("{call QMS_REPORT_PKG.QMS_BYRATE_EXPIRY_REPORT_EXCEL(?,?,?,?,?,?,?,?,?,?)}");
        cstmt.clearParameters();
        cstmt.setString(1,reportenterDob.getShipmentMode());
        //Commented by Rakesh for Issue:             on 09-03-2011
/*       //@@ Commented by subrahmanyam for the Enhancement 146451 on 15/02/09        
        //cstmt.setString(2,reportenterDob.getCarrierId().replace(',','~'));
        //cstmt.setString(3,reportenterDob.getServiceLevel().replace(',','~'));
//@@ Added by subrahmanyam for the enhancement 146451 on 15/02/09        
        if(reportenterDob.getCarrierId()!=null)
        cstmt.setString(2,reportenterDob.getCarrierId().replace(',','~'));
        else
          cstmt.setString(2,null);
        if(reportenterDob.getServiceLevel()!=null)
        cstmt.setString(3,reportenterDob.getServiceLevel().replace(',','~'));
        else
          cstmt.setString(3,null);
//@@ Ended by subrahmanyam for the enhancement 146451 on 15/02/09          
        cstmt.setString(4,reportenterDob.getBasis());
        cstmt.setString(5,reportenterDob.getExpiryActiveIndicator());  */
        //Comment Ended by Rakesh for Issue:             on 09-03-2011
        cstmt.setString(2,"");
        cstmt.setString(3,"");
        cstmt.setString(4,"");
        cstmt.setString(5,"");
        cstmt.setTimestamp(6,reportenterDob.getFromDate1());        
        cstmt.setTimestamp(7,reportenterDob.getToDate1());
        cstmt.setString(8,reportenterDob.getWeight_break());
        cstmt.setString(9,reportenterDob.getRate_type());
        cstmt.registerOutParameter(10, OracleTypes.CURSOR);
        cstmt.execute();       

        rs = (ResultSet)cstmt.getObject(10);
        String currdate   =new  com.foursoft.esupply.common.util.ESupplyDateUtility().getCurrentDateStringWithTime("DD-MM-YYYY");
        currentdate         = new ESupplyDateUtility().getTimestamp("DD/MM/YY",currdate);
          while(rs.next())
          {   
              reportDetailsDOB = new ReportDetailsDOB();
              //reportDetailsDOB.setFromCountry(rs.getString("COUNTRYID")!=null?rs.getString("COUNTRYID"):"");
              reportDetailsDOB.setFromLocation(rs.getString("ORIGIN")!=null?rs.getString("ORIGIN"):"");
              //reportDetailsDOB.setToCountry(rs.getString("DESTCOUNTRYID")!=null?rs.getString("DESTCOUNTRYID"):"");
              reportDetailsDOB.setToLocation(rs.getString("DESTINATION")!=null?rs.getString("DESTINATION"):"");
              reportDetailsDOB.setCarrierId(rs.getString("CARRIER_ID")!=null?rs.getString("CARRIER_ID"):"");
              reportDetailsDOB.setServiceLevel(rs.getString("SERVICE_LEVEL")!=null?rs.getString("SERVICE_LEVEL"):"");
              reportDetailsDOB.setCreateDate(rs.getTimestamp("CREATEDTIME"));
              reportDetailsDOB.setEffectiveFrom(rs.getTimestamp("EFFECTIVE_FROM"));
              reportDetailsDOB.setValidUpto(rs.getTimestamp("VALID_UPTO"));
              //Added by Rakesh for Issue:              on 09-03-2011
              reportDetailsDOB.setCurrency(rs.getString("CURRENCY")!=null?rs.getString("CURRENCY"):"");
              reportDetailsDOB.setFrequency(rs.getString("FREQUENCY")!=null?rs.getString("FREQUENCY"):"");
              reportDetailsDOB.setTerminalId(rs.getString("TERMINALID")!=null?rs.getString("TERMINALID"):"");
              reportDetailsDOB.setNotes(rs.getString("NOTES")!=null?rs.getString("NOTES"):"");
              //Ended by Rakesh for Issue:              on 09-03-2011
              reportDetailsDOB.setShipmentMode(rs.getString("SHIPMENT_MODE")!=null?rs.getString("SHIPMENT_MODE"):""); 
           //   reportDetailsDOB.setWeight_break(rs.getString("WEIGHT_BREAK")!=null?rs.getString("WEIGHT_BREAK"):""); 
           //   reportDetailsDOB.setRate_type(rs.getString("RATE_TYPE")!=null?rs.getString("RATE_TYPE"):""); 
              long expiry =0L;
              if(rs.getTimestamp("VALID_UPTO")!=null &&  reportenterDob.getExpiryActiveIndicator().equals("Y"))
                { 
                   validuptodate   = rs.getTimestamp("VALID_UPTO");
                   expiry = validuptodate.getTime()-currentdate.getTime();
                }
              else
              {
                   effectivefromdate    = rs.getTimestamp("EFFECTIVE_FROM");
                   expiry = currentdate.getTime()-effectivefromdate.getTime();
              }
              if(expiry>0)
                { expiry = expiry/(24*60*60*1000);}
              
              reportDetailsDOB.setExpiryinDays(expiry);
              dataList.add(reportDetailsDOB);
          }
           mainDataList.add(pageIterList);
           mainDataList.add(dataList);           
      }
      catch(SQLException e)
      {
        e.printStackTrace();
        //Logger.error(FILE_NAME,"SQLException in getBuyRatesExpiryReportDataForExcl( ) method"+e.toString());
        logger.error(FILE_NAME+"SQLException in getBuyRatesExpiryReportDataForExcl( ) method"+e.toString());
        throw new EJBException();        
      }catch(EJBException e)
      {
      e.printStackTrace();
        //Logger.error(FILE_NAME,"EJBException in getBuyRatesExpiryReportDataForExcl() method"+e.toString());
        logger.error(FILE_NAME+"EJBException in getBuyRatesExpiryReportDataForExcl() method"+e.toString());
        throw new EJBException();         
      }catch(Exception e)
      {
      e.printStackTrace();
        //Logger.error(FILE_NAME,"Exception in getBuyRatesExpiryReportDataForExcl() method"+e.toString());
        logger.error(FILE_NAME+"Exception in getBuyRatesExpiryReportDataForExcl() method"+e.toString());
        throw new EJBException();         
      }finally
      {
        try
        {
          if(rs!=null)
          {rs.close();}
          if(cstmt!=null)
          { cstmt.close();}
          if(connection!=null)
          { connection.close();}
        }catch(SQLException e)
        {
          //Logger.error(FILE_NAME,"SQLException in getBuyRatesExpiryReportData( ) method"+e.toString());
          logger.error(FILE_NAME+"SQLException in getBuyRatesExpiryReportData( ) method"+e.toString());
          throw new EJBException();        
        }
      }
       return mainDataList;
    }
    
/**
   * This getQuoteExpiryReportData() get the report from Db by procedure QMS_REPORT_PKG.QMS_QUOTE_EXPIRY_REPORT for quite expiry
   * @throws javax.ejb.EJBException
   * @return 
   * @param reportsEnterIdDOB
   */
  public ArrayList getQuoteExpiryReportData(ReportsEnterIdDOB reportenterDob)throws EJBException
    {
      Connection  connection            = null;
      CallableStatement  cstmt          = null;
      ResultSet           rs            = null;
      OperationsImpl    oimpl           = null; 
      ReportDetailsDOB reportDetailsDOB = null;
      int              expiryinDays     =  0;
      DataSource  dataSource            = null;
      OperationsImpl   oimp             = null;
    
      ArrayList        dataList         = null;    
      ArrayList        mainDataList     = null;
      ArrayList        pageIterList     = null;

      try
      {
         oimpl=new OperationsImpl();
        connection= oimpl.getConnection();
        dataList         = new ArrayList();
        mainDataList     = new ArrayList();
        pageIterList     = new ArrayList();
        cstmt = connection.prepareCall("{call QMS_REPORT_PKG.QMS_QUOTE_EXPIRY_REPORT(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");
        cstmt.clearParameters();
        cstmt.setString(1,reportenterDob.getShipmentMode());
        cstmt.setString(2,reportenterDob.getCustomerId().replace(',','~'));
        cstmt.setString(3,reportenterDob.getServiceLevel().replace(',','~'));
        cstmt.setString(4,reportenterDob.getTerminalId());
        cstmt.setString(5,reportenterDob.getBasis());
        cstmt.setString(6,reportenterDob.getExpiryActiveIndicator());        
        cstmt.setString(7,reportenterDob.getFromDate());
        cstmt.setString(8,reportenterDob.getToDate());
        cstmt.setString(9,reportenterDob.getDateFormat());
        cstmt.setInt(10,reportenterDob.getPageNo());
        cstmt.setInt(11,reportenterDob.getNoOfRecords());        
        cstmt.setString(12,reportenterDob.getSortBy());
        cstmt.setString(13,reportenterDob.getSortOrder());
		cstmt.setString(14, reportenterDob.getSalesPersonId());//Included by Shyam for DHL on 3/7/2006
		cstmt.setString(15, reportenterDob.getUserId());//Included by Shyam for DHL on 3/7/2006
        cstmt.registerOutParameter(16, OracleTypes.INTEGER);
        cstmt.registerOutParameter(17, OracleTypes.INTEGER);        
        cstmt.registerOutParameter(18, OracleTypes.CURSOR);
        cstmt.execute();       
        rs = (ResultSet)cstmt.getObject(18);
          while(rs.next())  
          {
              reportDetailsDOB = new ReportDetailsDOB();
              reportDetailsDOB.setImportant(rs.getString("IU_FLAG")!=null?rs.getString("IU_FLAG"):"");   
              reportDetailsDOB.setCustomerId(rs.getString("CUSTOMER_ID")!=null?rs.getString("CUSTOMER_ID"):"");
              reportDetailsDOB.setCustomerName(rs.getString("COMPANYNAME")!=null?rs.getString("COMPANYNAME"):"");//@@added by kameswari for the WPBN issue-30313
              reportDetailsDOB.setQuoteId(rs.getString("QUOTE_ID")!=null?rs.getString("QUOTE_ID"):"");
              reportDetailsDOB.setServiceLevel(rs.getString("SERVICE_LEVEL_ID")!=null?rs.getString("SERVICE_LEVEL_ID"):"");  
              reportDetailsDOB.setFromCountry(rs.getString("ORIGINCOUNTRYID")!=null?rs.getString("ORIGINCOUNTRYID"):"");
              reportDetailsDOB.setFromLocation(rs.getString("ORIGIN_LOCATION")!=null?rs.getString("ORIGIN_LOCATION"):"");
              reportDetailsDOB.setToCountry(rs.getString("DESTCOUNTRYID")!=null?rs.getString("DESTCOUNTRYID"):"");
              reportDetailsDOB.setToLocation(rs.getString("DEST_LOCATION")!=null?rs.getString("DEST_LOCATION"):"");
              reportDetailsDOB.setValidUpto(rs.getTimestamp("VALID_TO"));
              reportDetailsDOB.setShipmentMode(rs.getString("SHIPMENT_MODE")!=null?rs.getString("SHIPMENT_MODE"):"");
              reportDetailsDOB.setIsMultiQuote(rs.getString("IS_MULTI_QUOTE")!=null?rs.getString("IS_MULTI_QUOTE"):"");//Added by Anil.k on 18-Jan-2011
              reportDetailsDOB.setUpdate_flag(rs.getString("UPDATE_FLAG")!=null?rs.getString("UPDATE_FLAG"):"");//aDDED BY GOVIND FOR MAKING UPDAED QUOTES INACTIVE WHEN QUOTE IS INACTIVE
              dataList.add(reportDetailsDOB);
          }
           pageIterList.add(new Integer(cstmt.getInt(16)));
           pageIterList.add(new Integer(cstmt.getInt(17)));
           mainDataList.add(pageIterList);
           mainDataList.add(dataList);           
      }
      catch(SQLException e)
      {
        e.printStackTrace();
        //Logger.error(FILE_NAME,"SQLException in getQuoteExpiryReportData( ) method"+e.toString());
        logger.error(FILE_NAME+"SQLException in getQuoteExpiryReportData( ) method"+e.toString());
        throw new EJBException();        
      }catch(EJBException e)
      {
      e.printStackTrace();
        //Logger.error(FILE_NAME,"EJBException in getQuoteExpiryReportData() method"+e.toString());
        logger.error(FILE_NAME+"EJBException in getQuoteExpiryReportData() method"+e.toString());
        throw new EJBException();         
      }catch(Exception e)
      {
      e.printStackTrace();
        //Logger.error(FILE_NAME,"Exception in getQuoteExpiryReportData() method"+e.toString());
        logger.error(FILE_NAME+"Exception in getQuoteExpiryReportData() method"+e.toString());
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
          //Logger.error(FILE_NAME,"SQLException in getQuoteExpiryReportData( ) method"+e.toString());
          logger.error(FILE_NAME+"SQLException in getQuoteExpiryReportData( ) method"+e.toString());
          throw new EJBException();        
        }
      }
       return mainDataList;
    }
    /**
   * This getQuoteExpiryReportDataExcl() get the report from Db by procedure QMS_REPORT_PKG.QMS_QUOTE_EXPIRY_REPORT for quite expiry
   * @throws javax.ejb.EJBException
   * @return 
   * @param reportsEnterIdDOB
   */
  public ArrayList getQuoteExpiryReportDataExcl(ReportsEnterIdDOB reportenterDob)throws EJBException
    {
      Connection  connection            = null;
      CallableStatement  cstmt          = null;
      ResultSet           rs            = null;
      OperationsImpl    oimpl           = null; 
      ReportDetailsDOB reportDetailsDOB = null;
      int              expiryinDays     =  0;
      DataSource  dataSource            = null;
      OperationsImpl   oimp             = null;
    
      ArrayList        dataList         = null;    
      ArrayList        mainDataList     = null;
      ArrayList        pageIterList     = null;

      try
      {
        oimpl=new OperationsImpl();
        connection= oimpl.getConnection();
        dataList         = new ArrayList();
        mainDataList     = new ArrayList();
        pageIterList     = new ArrayList();
        cstmt = connection.prepareCall("{call QMS_REPORT_PKG.qms_quote_expiry_report_excl(?,?,?,?,?,?,?,?,?,?)}");
        cstmt.clearParameters();
        cstmt.setString(1,reportenterDob.getShipmentMode());
        cstmt.setString(2,reportenterDob.getCustomerId().replace(',','~'));
        cstmt.setString(3,reportenterDob.getServiceLevel().replace(',','~'));
        cstmt.setString(4,reportenterDob.getTerminalId());
        cstmt.setString(5,reportenterDob.getBasis());
        cstmt.setString(6,reportenterDob.getExpiryActiveIndicator());        
        cstmt.setString(7,reportenterDob.getFromDate());
        cstmt.setString(8,reportenterDob.getToDate());
        cstmt.setString(9,reportenterDob.getDateFormat());
        cstmt.registerOutParameter(10, OracleTypes.CURSOR);
        cstmt.execute();       
        rs = (ResultSet)cstmt.getObject(10);
          while(rs.next())  
          {
              reportDetailsDOB = new ReportDetailsDOB();
              reportDetailsDOB.setImportant(rs.getString("IU_FLAG")!=null?rs.getString("IU_FLAG"):"");   
              reportDetailsDOB.setCustomerId(rs.getString("CUSTOMER_ID")!=null?rs.getString("CUSTOMER_ID"):"");
              reportDetailsDOB.setCustomerName(rs.getString("COMPANYNAME")!=null?rs.getString("COMPANYNAME"):"");//@@added by kameswari for the WPBN issue-30313
              reportDetailsDOB.setQuoteId(rs.getString("QUOTE_ID")!=null?rs.getString("QUOTE_ID"):"");
              reportDetailsDOB.setServiceLevel(rs.getString("SERVICE_LEVEL_ID")!=null?rs.getString("SERVICE_LEVEL_ID"):"");  
              reportDetailsDOB.setFromCountry(rs.getString("ORIGINCOUNTRYID")!=null?rs.getString("ORIGINCOUNTRYID"):"");
              reportDetailsDOB.setFromLocation(rs.getString("ORIGIN_LOCATION")!=null?rs.getString("ORIGIN_LOCATION"):"");
              reportDetailsDOB.setToCountry(rs.getString("DESTCOUNTRYID")!=null?rs.getString("DESTCOUNTRYID"):"");
              reportDetailsDOB.setToLocation(rs.getString("DEST_LOCATION")!=null?rs.getString("DEST_LOCATION"):"");
              reportDetailsDOB.setValidUpto(rs.getTimestamp("VALID_TO"));
              reportDetailsDOB.setShipmentMode(rs.getString("SHIPMENT_MODE")!=null?rs.getString("SHIPMENT_MODE"):"");
             reportDetailsDOB.setCreateDateStr(rs.getString("CREATED_DATE"));//added by silpa 
             reportDetailsDOB.setQuoteStatus(rs.getString("QUOTE_STATUS"));//added by silpa
             
              dataList.add(reportDetailsDOB);
          }
           mainDataList.add(pageIterList);
           mainDataList.add(dataList);           
      }
      catch(SQLException e)
      {
        e.printStackTrace();
        //Logger.error(FILE_NAME,"SQLException in getQuoteExpiryReportDataExcl( ) method"+e.toString());
        logger.error(FILE_NAME+"SQLException in getQuoteExpiryReportDataExcl( ) method"+e.toString());
        throw new EJBException();        
      }catch(EJBException e)
      {
      e.printStackTrace();
        //Logger.error(FILE_NAME,"EJBException in getQuoteExpiryReportDataExcl() method"+e.toString());
        logger.error(FILE_NAME+"EJBException in getQuoteExpiryReportDataExcl() method"+e.toString());
        throw new EJBException();         
      }catch(Exception e)
      {
      e.printStackTrace();
        //Logger.error(FILE_NAME,"Exception in getQuoteExpiryReportDataExcl() method"+e.toString());
        logger.error(FILE_NAME+"Exception in getQuoteExpiryReportDataExcl() method"+e.toString());
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
          //Logger.error(FILE_NAME,"SQLException in getQuoteExpiryReportDataExcl( ) method"+e.toString());
          logger.error(FILE_NAME+"SQLException in getQuoteExpiryReportDataExcl( ) method"+e.toString());
          throw new EJBException();        
        }
      }
       return mainDataList;
    }
 /**
   * This updateQuoteExpiryReportData() updates the quote status
   * @throws javax.ejb.EJBException
   * @return 
   * @param updatedDataList   
   */
  public String updateQuoteExpiryReportData(ArrayList updatedDataList)throws EJBException
    {
      Connection  connection            = null;
      PreparedStatement  pstmt          = null;
      PreparedStatement  pstmt1          = null;
      OperationsImpl     oimpl          = null;
      ReportDetailsDOB reportDetailsDOB = null;
      String              updated       = null;
      int                 modify        = 0;     
      Timestamp        currDate      = null;
      String[]            currDateString  = null;
      Timestamp         currentdate       = null; 
      String              userDateFormat  = null;
      ESupplyDateUtility  dateFormatter   = new ESupplyDateUtility();
      DataSource  dataSource            = null;
      OperationsImpl   oimp             = null;
      try
      {
        oimpl            =new OperationsImpl();
        connection       = oimpl.getConnection();
        currDate        = new java.sql.Timestamp((new java.util.Date()).getTime());
        
        if(updatedDataList!=null && updatedDataList.size()>0)
             userDateFormat = ((ReportDetailsDOB)updatedDataList.get(0)).getDateFormat();
        
        if(userDateFormat!=null)    
            dateFormatter.setPatternWithTime(userDateFormat);
        else
            dateFormatter.setPatternWithTime("DD-MM-YY");
        
        currDateString  = dateFormatter.getDisplayStringArray(currDate);
        //String currdate  = new  com.foursoft.esupply.common.util.ESupplyDateUtility().getCurrentDateStringWithTime("DD-MM-YYYY");
        currentdate      = dateFormatter.getTimestampWithTime(userDateFormat,currDateString[0],currDateString[1]);
        //String updateqry=" UPDATE QMS_QUOTE_MASTER SET VALID_TO =?,EXPIRED_FLAG=? WHERE QUOTE_ID=? AND VERSION_NO=(SELECT MAX(VERSION_NO) FROM QMS_QUOTE_MASTER WHERE QUOTE_ID=?) ";
        //@@Modified by Kameswari for the WPBN issue-140717
        String updateqry=" UPDATE QMS_QUOTE_MASTER SET VALID_TO =?,EXPIRED_FLAG=? WHERE QUOTE_ID=? AND VERSION_NO=(SELECT MAX(VERSION_NO) FROM QMS_QUOTE_MASTER WHERE QUOTE_ID=? AND ACTIVE_FLAG=?) ";
        //Added by Anil.k on 28Feb2011
        String updateInActive=" UPDATE QMS_QUOTE_MASTER SET VALID_TO =?,EXPIRED_FLAG=?,ACTIVE_FLAG=?,quote_status='NAC' WHERE QUOTE_ID=? AND VERSION_NO=(SELECT MAX(VERSION_NO) FROM QMS_QUOTE_MASTER WHERE QUOTE_ID=? AND ACTIVE_FLAG=?) ";
        
        String Inactive_del_update = "DELETE FROM QMS_QUOTES_UPDATED UP WHERE UP.QUOTE_ID IN (SELECT ID FROM QMS_QUOTE_MASTER WHERE  QUOTE_ID = ?)";//Added by govind for inactivating the quote in update when quote is inactvating
        
        //pstmt=connection.prepareStatement(updateqry);
        if(updatedDataList!=null && updatedDataList.size()>0)
        {
        	int updDataListSize	=	updatedDataList.size();
        for(int i=0;i<updDataListSize;i++)
           {
            reportDetailsDOB=(ReportDetailsDOB)updatedDataList.get(i);
          //Added by Anil.k on 28Feb2011
            if(reportDetailsDOB.getInActive()!=null && "inActive".equalsIgnoreCase(reportDetailsDOB.getInActive()))
            {
            	pstmt=connection.prepareStatement(updateInActive);//Ended by Anil.k on 28Feb2011
            	if(reportDetailsDOB.getValidUpto()!=null)
            	  pstmt.setTimestamp(1,reportDetailsDOB.getValidUpto());
            	else
                  pstmt.setNull(1,Types.DATE);
            
            	if(reportDetailsDOB.getValidUpto()!=null && currentdate.before(reportDetailsDOB.getValidUpto()))
                  pstmt.setString(2,"N");
            	else
                  pstmt.setString(2,"Y");
            	pstmt.setString(3,"I");
            	pstmt.setString(4,reportDetailsDOB.getQuoteId());
            	pstmt.setString(5,reportDetailsDOB.getQuoteId());
            	pstmt.setString(6,"A");
            	modify=pstmt.executeUpdate(); 
            	
            	
            	if(Integer.parseInt(reportDetailsDOB.getUpdate_flag())>0)
                {
            		pstmt1 = connection.prepareStatement(Inactive_del_update);
            		pstmt1.setString(1, reportDetailsDOB.getQuoteId());
            		pstmt1.execute();
                }
            }
            else
            {
            	pstmt=connection.prepareStatement(updateqry);//Ended by Anil.k on 28Feb2011
            if(reportDetailsDOB.getValidUpto()!=null)
                  pstmt.setTimestamp(1,reportDetailsDOB.getValidUpto());
            else
                  pstmt.setNull(1,Types.DATE);
            
            if(reportDetailsDOB.getValidUpto()!=null && currentdate.before(reportDetailsDOB.getValidUpto()))
                  pstmt.setString(2,"N");
            else
                  pstmt.setString(2,"Y");
            
            pstmt.setString(3,reportDetailsDOB.getQuoteId());
            pstmt.setString(4,reportDetailsDOB.getQuoteId());
             pstmt.setString(5,"A");
            modify=pstmt.executeUpdate();    
            }//Added by Anil.k on 28Feb2011
            
            
           }
      
        
        }
        if(modify==0)
            updated =null;
        else
            updated="modified";        
      }
      catch(SQLException e)
      {
        e.printStackTrace();
        //Logger.error(FILE_NAME,"SQLException in updateQuoteExpiryReportData( ) method"+e.toString());
        logger.error(FILE_NAME+"SQLException in updateQuoteExpiryReportData( ) method"+e.toString());
        throw new EJBException();        
      }catch(EJBException e)
      {
      e.printStackTrace();
        //Logger.error(FILE_NAME,"EJBException in updateQuoteExpiryReportData() method"+e.toString());
        logger.error(FILE_NAME+"EJBException in updateQuoteExpiryReportData() method"+e.toString());
        throw new EJBException();         
      }catch(Exception e)
      {
      e.printStackTrace();
        //Logger.error(FILE_NAME,"Exception in updateQuoteExpiryReportData() method"+e.toString());
        logger.error(FILE_NAME+"Exception in updateQuoteExpiryReportData() method"+e.toString());
        throw new EJBException();         
      }finally
      {
        try
        {
          
          if(pstmt!=null )
          { pstmt.close();}
          if(connection!=null)
          { connection.close();}
          if(pstmt1!= null)
        	  pstmt1.close();
        }catch(SQLException e)
        {
          //Logger.error(FILE_NAME,"SQLException in updateQuoteExpiryReportData( ) method"+e.toString());
          logger.error(FILE_NAME+"SQLException in updateQuoteExpiryReportData( ) method"+e.toString());
          throw new EJBException();        
        }
      }
      return updated;
    }
    
   /**
  * this to get getAproveRRejectQuoteDetailData() for approved report
  */
  public ArrayList getAproveRRejectQuoteDetailData(ReportsEnterIdDOB reportsEnterIdDOB)throws EJBException
    {
      Connection  connection=null;
      CallableStatement  cstmt          = null;
      ResultSet           rs            = null;
      DataSource  dataSource            = null;
      ReportDetailsDOB reportDetailsDOB = null;
      OperationsImpl   oimp             = null;
      
      ArrayList        dataList         = null;    
      ArrayList        mainDataList     = null;
      ArrayList        pageIterList     = null;
      StringBuffer     sendOptions      = null;
      String           prevOptions      = "";

      try
      {
    
        oimp=new OperationsImpl();
        connection= oimp.getConnection();
        dataList         = new ArrayList();
        mainDataList     = new ArrayList();
        pageIterList     = new ArrayList();
        //cstmt = connection.prepareCall("{call QMS_REPORT_PKG.qms_app_rej_report(?,?,?,?,?,?,?,?,?,?,?)}");
        cstmt = connection.prepareCall("{call QMS_REPORT_PKG.qms_app_rej_report(?,?,?,?,?,?,?,?,?,?,?)}");
        cstmt.setString(1,reportsEnterIdDOB.getUserId());
        cstmt.setString(2,reportsEnterIdDOB.getTerminalId());
   
        cstmt.setString(3,reportsEnterIdDOB.getQuoteStatus());
        cstmt.setString(4,reportsEnterIdDOB.getApprovedFlag());
        cstmt.setInt(5,reportsEnterIdDOB.getPageNo());
        cstmt.setInt(6,reportsEnterIdDOB.getNoOfRecords()); 
        cstmt.setString(7,reportsEnterIdDOB.getSortBy());

        cstmt.setString(8,reportsEnterIdDOB.getSortOrder());
        cstmt.registerOutParameter(9, OracleTypes.INTEGER);
        cstmt.registerOutParameter(10, OracleTypes.INTEGER);        
        cstmt.registerOutParameter(11, OracleTypes.CURSOR);
        
       
        cstmt.execute();      
       
        rs = (ResultSet)cstmt.getObject(11);
      
        while(rs.next())
          {
              
              reportDetailsDOB = new ReportDetailsDOB();
              reportDetailsDOB.setQuoteId(rs.getString("QUOTE_ID")!=null?rs.getString("QUOTE_ID"):"");
              reportDetailsDOB.setShipmentMode(rs.getString("SHIPMENT_MODE")!=null?rs.getString("SHIPMENT_MODE"):"");
              reportDetailsDOB.setImportant(rs.getString("IU_FLAG")!=null?rs.getString("IU_FLAG"):"");
              reportDetailsDOB.setCustomerId(rs.getString("CUSTOMER_ID")!=null?rs.getString("CUSTOMER_ID"):"");
              reportDetailsDOB.setCustomerName(rs.getString("COMPANYNAME")!=null?rs.getString("COMPANYNAME"):"");//@@added by kameswari for the WPBN isue-30313
              reportDetailsDOB.setFromCountry(rs.getString("org_country")!=null?rs.getString("org_country"):"");
              reportDetailsDOB.setFromLocation(rs.getString("origin_location")!=null?rs.getString("origin_location"):"");
              reportDetailsDOB.setApprovedRrejectedBy(rs.getString("ESCALATED_TO")!=null?rs.getString("ESCALATED_TO"):"");
              reportDetailsDOB.setToCountry(rs.getString("dest_country")!=null?rs.getString("dest_country"):"");
              reportDetailsDOB.setToLocation(rs.getString("dest_location")!=null?rs.getString("dest_location"):"");
              reportDetailsDOB.setServiceLevel(rs.getString("SERVICE_LEVEL_ID")!=null?rs.getString("SERVICE_LEVEL_ID"):"");
              reportDetailsDOB.setOperEmailId(rs.getString("OPERATIONS_EMAILID")!=null?rs.getString("OPERATIONS_EMAILID"):"");

              //reportDetailsDOB.setApprovedRrejectedDtNtime(rs.getString("APP_REJ_TSTMP")!=null?rs.getString("APP_REJ_TSTMP"):"");
              reportDetailsDOB.setApprovedRejTstmp(rs.getTimestamp("APP_REJ_TSTMP"));
              
              reportDetailsDOB.setDueDateTmstmp(rs.getTimestamp("DUE_DATE"));
              
       
              reportDetailsDOB.setInternalRemarks(rs.getString("INTERNAL_NOTES")!=null?rs.getString("INTERNAL_NOTES"):"");
              reportDetailsDOB.setExternalRemarks(rs.getString("EXTERNAL_NOTES")!=null?rs.getString("EXTERNAL_NOTES"):"");
              
              sendOptions = new StringBuffer("");
              sendOptions.append(rs.getString("EMAIL_FLAG")!=null?rs.getString("EMAIL_FLAG"):"")
              .append(rs.getString("FAX_FLAG")!=null?rs.getString("FAX_FLAG"):"")
              .append(rs.getString("PRINT_FLAG")!=null?rs.getString("PRINT_FLAG"):"");
              reportDetailsDOB.setIsMultiQuote(rs.getString("IS_MULTI_QUOTE"));//Added by Anil.k
              
              prevOptions = sendOptions.toString().trim();
              
              if (prevOptions.indexOf(",")!=-1)
              {
               if(prevOptions.indexOf(",") == prevOptions.length()-1)
                  prevOptions = prevOptions.substring(0,prevOptions.length()-1);
              }
              
              reportDetailsDOB.setSendOptions(prevOptions);
              dataList.add(reportDetailsDOB);
          }
           pageIterList.add(new Integer(cstmt.getInt(9)));
           pageIterList.add(new Integer(cstmt.getInt(10)));
           mainDataList.add(pageIterList);
           mainDataList.add(dataList);           
        
      }
      catch(SQLException e)
      {
        e.printStackTrace();
        //Logger.error(FILE_NAME,"SQLException in getAproveRRejectQuoteDetailData( ) method"+e.toString());
        logger.error(FILE_NAME+"SQLException in getAproveRRejectQuoteDetailData( ) method"+e.toString());
        throw new EJBException();        
      }catch(EJBException e)
      {
      e.printStackTrace();
        //Logger.error(FILE_NAME,"EJBException in getAproveRRejectQuoteDetailData() method"+e.toString());
        logger.error(FILE_NAME+"EJBException in getAproveRRejectQuoteDetailData() method"+e.toString());
        throw new EJBException();         
      }catch(Exception e)
      {
      e.printStackTrace();
        //Logger.error(FILE_NAME,"Exception in getAproveRRejectQuoteDetailData() method"+e.toString());
        logger.error(FILE_NAME+"Exception in getAproveRRejectQuoteDetailData() method"+e.toString());
        throw new EJBException();         
      }finally
      {
        try
        {
          if(rs!=null)
          {rs.close();}
          if(cstmt!=null)
          { cstmt.close();}
          if(connection!=null)
          { connection.close();}
        }catch(SQLException e)
        {
          //Logger.error(FILE_NAME,"SQLException in getAproveRRejectQuoteDetailData( ) method"+e.toString());
          logger.error(FILE_NAME+"SQLException in getAproveRRejectQuoteDetailData( ) method"+e.toString());
          throw new EJBException();        
        }
      }
       return mainDataList;
    }
 /**
  * this to get getAproveRRejectQuoteDetailDataForExcl() for approved report
  */
  public ArrayList getAproveRRejectQuoteDetailDataForExcl(ReportsEnterIdDOB reportsEnterIdDOB)throws EJBException
    {
      Connection  connection=null;
      CallableStatement  cstmt          = null;
      ResultSet           rs            = null;
      DataSource  dataSource            = null;
      ReportDetailsDOB reportDetailsDOB = null;
      OperationsImpl   oimp             = null;
      
      ArrayList        dataList         = null;    
      ArrayList        mainDataList     = null;
      ArrayList        pageIterList     = null;

      try
      {
        oimp=new OperationsImpl();
        connection= oimp.getConnection();
        dataList         = new ArrayList();
        mainDataList     = new ArrayList();
        pageIterList     = new ArrayList();
        cstmt = connection.prepareCall("{call QMS_REPORT_PKG.qms_app_rej_report_excel(?,?,?,?,?)}");
        cstmt.setString(1,reportsEnterIdDOB.getUserId());
        cstmt.setString(2,reportsEnterIdDOB.getTerminalId());
        cstmt.setString(3,reportsEnterIdDOB.getQuoteStatus());
        cstmt.setString(4,reportsEnterIdDOB.getApprovedFlag());
        cstmt.registerOutParameter(5, OracleTypes.CURSOR);
        
        cstmt.execute();      
        rs = (ResultSet)cstmt.getObject(5);
        while(rs.next())
          {
              reportDetailsDOB = new ReportDetailsDOB();
              reportDetailsDOB.setQuoteId(rs.getString("QUOTE_ID")!=null?rs.getString("QUOTE_ID"):"");
              reportDetailsDOB.setShipmentMode(rs.getString("SHIPMENT_MODE")!=null?rs.getString("SHIPMENT_MODE"):"");
              reportDetailsDOB.setImportant(rs.getString("IU_FLAG")!=null?rs.getString("IU_FLAG"):"");
              reportDetailsDOB.setCustomerId(rs.getString("CUSTOMER_ID")!=null?rs.getString("CUSTOMER_ID"):"");
              reportDetailsDOB.setCustomerName(rs.getString("COMPANYNAME")!=null?rs.getString("COMPANYNAME"):"");//@@added by kameswari for the WPBN issue-30313
              reportDetailsDOB.setFromCountry(rs.getString("org_country")!=null?rs.getString("org_country"):"");
              reportDetailsDOB.setFromLocation(rs.getString("origin_location")!=null?rs.getString("origin_location"):"");
              reportDetailsDOB.setApprovedRrejectedBy(rs.getString("ESCALATED_TO")!=null?rs.getString("ESCALATED_TO"):"");
              reportDetailsDOB.setToCountry(rs.getString("dest_country")!=null?rs.getString("dest_country"):"");
              reportDetailsDOB.setToLocation(rs.getString("dest_location")!=null?rs.getString("dest_location"):"");
              reportDetailsDOB.setServiceLevel(rs.getString("SERVICE_LEVEL_ID")!=null?rs.getString("SERVICE_LEVEL_ID"):"");
              reportDetailsDOB.setOperEmailId(rs.getString("OPERATIONS_EMAILID")!=null?rs.getString("OPERATIONS_EMAILID"):"");

              //reportDetailsDOB.setApprovedRrejectedDtNtime(rs.getString("APP_REJ_TSTMP")!=null?rs.getString("APP_REJ_TSTMP"):"");
              reportDetailsDOB.setApprovedRejTstmp(rs.getTimestamp("APP_REJ_TSTMP"));
              
              reportDetailsDOB.setDueDateTmstmp(rs.getTimestamp("DUE_DATE"));
              

              reportDetailsDOB.setInternalRemarks(rs.getString("INTERNAL_NOTES")!=null?rs.getString("INTERNAL_NOTES"):"");
              reportDetailsDOB.setExternalRemarks(rs.getString("EXTERNAL_NOTES")!=null?rs.getString("EXTERNAL_NOTES"):"");
                   
              dataList.add(reportDetailsDOB);
          }
           mainDataList.add(pageIterList);
           mainDataList.add(dataList);           
        
      }
      catch(SQLException e)
      {
        e.printStackTrace();
        //Logger.error(FILE_NAME,"SQLException in getAproveRRejectQuoteDetailDataForExcl( ) method"+e.toString());
        logger.error(FILE_NAME+"SQLException in getAproveRRejectQuoteDetailDataForExcl( ) method"+e.toString());
        throw new EJBException();        
      }catch(EJBException e)
      {
      e.printStackTrace();
        //Logger.error(FILE_NAME,"EJBException in getAproveRRejectQuoteDetailDataForExcl() method"+e.toString());
        logger.error(FILE_NAME+"EJBException in getAproveRRejectQuoteDetailDataForExcl() method"+e.toString());
        throw new EJBException();         
      }catch(Exception e)
      {
      e.printStackTrace();
        //Logger.error(FILE_NAME,"Exception in getAproveRRejectQuoteDetailDataForExcl() method"+e.toString());
        logger.error(FILE_NAME+"Exception in getAproveRRejectQuoteDetailDataForExcl() method"+e.toString());
        throw new EJBException();         
      }finally
      {
        try
        {
          if(rs!=null)
          {rs.close();}
          if(cstmt!=null)
          { cstmt.close();}
          if(connection!=null)
          { connection.close();}
        }catch(SQLException e)
        {
          //Logger.error(FILE_NAME,"SQLException in getAproveRRejectQuoteDetailDataForExcl( ) method"+e.toString());
          logger.error(FILE_NAME+"SQLException in getAproveRRejectQuoteDetailDataForExcl( ) method"+e.toString());
          throw new EJBException();        
        }
      }
       return mainDataList;
    }
  /**
   * This updateAproveRRejectQuoteDetailData() updates the quote status
   * @throws javax.ejb.EJBException
   * @return 
   * @param updatedDataList   
   */
  public String updateAproveRRejectQuoteDetailData(ArrayList updatedDataList)throws EJBException
    {
      Connection  connection            = null;
      PreparedStatement  pstmt          = null;
      OperationsImpl     oimpl          = null;
      ReportDetailsDOB reportDetailsDOB = null;
      String              updated       = null;
      int                 modify        = 0;     
      Timestamp        currentdate      = null;
      DataSource  dataSource            = null;
      OperationsImpl   oimp             = null;
      try
      {
        oimpl            = new OperationsImpl();
        connection       = oimpl.getConnection();
        String currdate  = new  com.foursoft.esupply.common.util.ESupplyDateUtility().getCurrentDateStringWithTime("DD-MM-YYYY");
        currentdate      = new ESupplyDateUtility().getTimestamp("DD/MM/YY",currdate);
        String updateqry=" UPDATE QMS_QUOTE_MASTER SET VALID_TO =?,EXPIRED_FLAG=? WHERE QUOTE_ID=? AND VERSION_NO=(SELECT MAX(VERSION_NO) FROM QMS_QUOTE_MASTER WHERE QUOTE_ID=?) ";
        pstmt=connection.prepareStatement(updateqry);
        if(updatedDataList!=null && updatedDataList.size()>0)
        {
        	int updDataListSize	= updatedDataList.size();
        for(int i=0;i<updDataListSize;i++)
           {
            reportDetailsDOB=(ReportDetailsDOB)updatedDataList.get(i);
            if(reportDetailsDOB.getValidUpto()!=null)
                  pstmt.setTimestamp(1,reportDetailsDOB.getValidUpto());
            else
                  pstmt.setNull(1,Types.DATE);
            
            if(reportDetailsDOB.getValidUpto()!=null && currentdate.before(reportDetailsDOB.getValidUpto()))
                  pstmt.setString(2,"N");
            else
                  pstmt.setString(2,"Y");
            
            pstmt.setString(3,reportDetailsDOB.getQuoteId());
            pstmt.setString(4,reportDetailsDOB.getQuoteId());
            modify=pstmt.executeUpdate();    
           }
        }
        if(modify==0)
            updated =null;
        else
            updated="modified";        
      }
      catch(SQLException e)
      {
        e.printStackTrace();
        //Logger.error(FILE_NAME,"SQLException in updateAproveRRejectQuoteDetailData( ) method"+e.toString());
        logger.error(FILE_NAME+"SQLException in updateAproveRRejectQuoteDetailData( ) method"+e.toString());
        throw new EJBException();        
      }catch(EJBException e)
      {
      e.printStackTrace();
        //Logger.error(FILE_NAME,"EJBException in updateAproveRRejectQuoteDetailData() method"+e.toString());
        logger.error(FILE_NAME+"EJBException in updateAproveRRejectQuoteDetailData() method"+e.toString());
        throw new EJBException();         
      }catch(Exception e)
      {
      e.printStackTrace();
        //Logger.error(FILE_NAME,"Exception in updateAproveRRejectQuoteDetailData() method"+e.toString());
        logger.error(FILE_NAME+"Exception in updateAproveRRejectQuoteDetailData() method"+e.toString());
        throw new EJBException();         
      }finally
      {
        try
        {
          
          if(pstmt!=null)
          { pstmt.close();}
          if(connection!=null)
          { connection.close();}
        }catch(SQLException e)
        {
          //Logger.error(FILE_NAME,"SQLException in updateAproveRRejectQuoteDetailData( ) method"+e.toString());
          logger.error(FILE_NAME+"SQLException in updateAproveRRejectQuoteDetailData( ) method"+e.toString());
          throw new EJBException();        
        }
      }
      return updated;
    }    
 
 /**
   * This updateEscalatedQuoteReportDetailsData() updates the quote status
   * @throws javax.ejb.EJBException
   * @return 
   * @param updatedDataList   
   */
  public String updateEscalatedQuoteReportDetailsData(ArrayList updatedDataList)throws EJBException
    {
      Connection  connection            = null;
      PreparedStatement  pstmt          = null;
      OperationsImpl     oimpl          = null;
      ReportDetailsDOB reportDetailsDOB = null;
      String              updated       = null;
      int[]                 modify      = null;     
      Timestamp        currentdate      = null;
      DataSource  dataSource            = null;
      OperationsImpl   oimp             = null;
      int             updatesize        = 0;
      ArrayList      tempList           = new ArrayList();
      String          updateqry           = "";        
      try
      {
        oimpl            =new OperationsImpl();
        connection       = oimpl.getConnection();
        String currdate  = new  com.foursoft.esupply.common.util.ESupplyDateUtility().getCurrentDateStringWithTime("DD-MM-YYYY");
        currentdate      = new ESupplyDateUtility().getTimestamp("DD/MM/YY",currdate);
        
        if(updatedDataList!=null && updatedDataList.size()>0)
        {
          updateqry=" UPDATE QMS_QUOTE_MASTER SET QUOTE_STATUS =?, APP_REJ_TSTMP =?,ESCALATION_FLAG=?, INTERNAL_NOTES=?, EXTERNAL_NOTES=? WHERE QUOTE_ID=? AND VERSION_NO=(SELECT MAX(VERSION_NO) FROM QMS_QUOTE_MASTER WHERE QUOTE_ID=?) ";
          pstmt=connection.prepareStatement(updateqry);
           if(updatedDataList!=null && updatedDataList.size()>0)
          {
             updatesize=updatedDataList.size();
             for(int j=0;j<updatesize;j++)
             {
                reportDetailsDOB=(ReportDetailsDOB)updatedDataList.get(j);
                pstmt.setString(1,reportDetailsDOB.getQuoteStatus());
                pstmt.setTimestamp(2,new java.sql.Timestamp((new java.util.Date()).getTime()));
                pstmt.setString(3,"N");
                pstmt.setString(4,reportDetailsDOB.getInternalRemarks());
                pstmt.setString(5,reportDetailsDOB.getExternalRemarks());
                //@@Added by Kameswari for the WPBN issue-71660
               /* if("App".equalsIgnoreCase(reportDetailsDOB.getQuoteStatus()))
                  pstmt.setString(6,"E");
                else
                  pstmt.setString(6,"I");*/
                //@@WPBN issue-71660
                pstmt.setString(6,reportDetailsDOB.getQuoteId());
                pstmt.setString(7,reportDetailsDOB.getQuoteId());
                
                  
                pstmt.addBatch();   
             }
             modify = pstmt.executeBatch();
          }
        }
        
        if(modify!=null)
          updated = "modified";    
      }
      catch(SQLException e)
      {
        e.printStackTrace();
        updated =null;
        //Logger.error(FILE_NAME,"SQLException in updateEscalatedQuoteReportDetailsData( ) method"+e.toString());
        logger.error(FILE_NAME+"SQLException in updateEscalatedQuoteReportDetailsData( ) method"+e.toString());
        throw new EJBException();        
      }catch(EJBException e)
      {
      e.printStackTrace();
        //Logger.error(FILE_NAME,"EJBException in updateEscalatedQuoteReportDetailsData() method"+e.toString());
        logger.error(FILE_NAME+"EJBException in updateEscalatedQuoteReportDetailsData() method"+e.toString());
        updated =null;
        throw new EJBException();         
      }catch(Exception e)
      {
        e.printStackTrace();
        updated =null;
        //Logger.error(FILE_NAME,"Exception in updateEscalatedQuoteReportDetailsData() method"+e.toString());
        logger.error(FILE_NAME+"Exception in updateEscalatedQuoteReportDetailsData() method"+e.toString());
        throw new EJBException();         
      }finally
      {
        try
        {
          
          if(pstmt!=null)
          { pstmt.close();}
          if(connection!=null)
          { connection.close();}
        }catch(SQLException e)
        {
          //Logger.error(FILE_NAME,"SQLException in updateEscalatedQuoteReportDetailsData( ) method"+e.toString());
          logger.error(FILE_NAME+"SQLException in updateEscalatedQuoteReportDetailsData( ) method"+e.toString());
          throw new EJBException();        
        }
      }
      return updated;
    }    
 
 
//Included by Shyam for DHL Issue No: 14048
 public HashMap updateActivityFlag(HashMap checkMap) throws EJBException
 {
      Connection  connection            = null;
      PreparedStatement  pstmt          = null;
      int[]                 modify      = null;   
      OperationsImpl     oimpl          = null;
      String          updateqry         = "";    
      Iterator        quoteIterator     = null;
      String          key               = "";
      String          activeFlag      = "";
    HashMap finalMap =null;//added by phani sekhar for wpbn 181670 on 20090909
    HashMap higherVersionMap = null;
    HashMap updateReportMap = null;
    HashMap resultMap = null;//ends 181670
        try
      {
        oimpl            =new OperationsImpl();
        connection       = oimpl.getConnection();
         finalMap = new HashMap();
         higherVersionMap = new HashMap();
         updateReportMap = new HashMap();
          resultMap = new HashMap();
          if(checkMap!=null && checkMap.size()>0)
          {

            updateqry="UPDATE QMS_QUOTE_MASTER SET ACTIVE_FLAG=?,status_reason=? WHERE QUOTE_ID = ? AND VERSION_NO=(SELECT MAX(VERSION_NO) FROM QMS_QUOTE_MASTER WHERE QUOTE_ID = ?)";
            pstmt=connection.prepareStatement(updateqry);
           checkStatusofQuote(connection,checkMap,finalMap,higherVersionMap,updateReportMap);//added by phani sekhar for wpbn 181670 on 20090909
            quoteIterator  = finalMap.keySet().iterator(); //commented and modified by phani sekhar for wpbn 181670 on 20090909
           // quoteIterator  = checkMap.keySet().iterator();

            while(quoteIterator.hasNext())
            {
            
              key = (String)quoteIterator.next();

              activeFlag  = (String)checkMap.get(key);
              pstmt.setString(1,activeFlag.substring(0,1));  //Added By Kishore Podili For Status Reason
              pstmt.setString(2,activeFlag.length()==1?"":activeFlag.substring(1)); //Added By Kishore Podili For Status Reason
              pstmt.setString(3,key);
              pstmt.setString(4,key);
              
              pstmt.addBatch();
            }
            if(finalMap.size()>0)
             modify = pstmt.executeBatch();
          }
          resultMap.put("succes",finalMap);//added by phani sekhar for wpbn 181670 on 20090909
          resultMap.put("fail1",higherVersionMap);
          resultMap.put("fail2",updateReportMap);//ends 181670
          
      }
       catch(SQLException e)
      {
        e.printStackTrace();
        //Logger.error(FILE_NAME,"SQLException in updateActivityFlag( ) method"+e.toString());
        logger.error(FILE_NAME+"SQLException in updateActivityFlag( ) method"+e.toString());
        throw new EJBException();        
      }catch(EJBException e)
      {
      e.printStackTrace();
        //Logger.error(FILE_NAME,"EJBException in updateActivityFlag() method"+e.toString());\
        logger.error(FILE_NAME+"EJBException in updateActivityFlag() method"+e.toString());
        throw new EJBException();         
      }catch(Exception e)
      {
        e.printStackTrace();
        //Logger.error(FILE_NAME,"Exception in updateActivityFlag() method"+e.toString());
        logger.error(FILE_NAME+"Exception in updateActivityFlag() method"+e.toString());
        throw new EJBException();         
      }finally
      {
        try
        {
         
          if(pstmt!=null)
          { pstmt.close();}
          if(connection!=null)
          { connection.close();}
        }catch(SQLException e)
        {
          //Logger.error(FILE_NAME,"SQLException in updateActivityFlag( ) method"+e.toString());
          logger.error(FILE_NAME+"SQLException in updateActivityFlag( ) method"+e.toString());
          throw new EJBException();        
        }
      }
     return resultMap; 
 }
//Included by Shyam for DHL
//added by phani sekhar for wpbn 181670 on 20090909
private void checkStatusofQuote(Connection connection,HashMap checkedMap,HashMap finalMap,HashMap higherVersionMap,HashMap updateReportMap)
{
//HashMap finalmap =null;
//HashMap higherVersionMap = null;
//HashMap updateReportMap = null;
//Connection  connection            = null;
PreparedStatement  pstmt          = null;
ResultSet rs					  = null;
Iterator        quoteIterator     = null;
//String checkPNQuery =" SELECT PN_FLAG,ACTIVE_FLAG FROM QMS_QUOTE_MASTER WHERE QUOTE_ID=? "; // commented by subrahmanyam for 186630 on 15-oct-09
String checkPNQuery =" SELECT PN_FLAG,ACTIVE_FLAG FROM QMS_QUOTE_MASTER WHERE QUOTE_ID=? AND VERSION_NO =("+
                      " SELECT MAX(VERSION_NO) FROM  QMS_QUOTE_MASTER WHERE QUOTE_ID=? )";//added by subrahmanyam for 186630 on 15-oct-09

String currentStatus= null;
String changedStatus= null;
String pnFlag = null;
String key;
//Added By Kishore Podili For Status Reason
String value=null;
  try
  {
    
	//	connection       = oimpl.getConnection();
		quoteIterator  = checkedMap.keySet().iterator();
	//	finalMap =  new HashMap();
		 pstmt=connection.prepareStatement(checkPNQuery);
            while(quoteIterator.hasNext())
            {
              pstmt.clearParameters();
              key = (String)quoteIterator.next();
            //Added By Kishore Podili For Status Reason
			  value = (String)checkedMap.get(key);
			  changedStatus = value.substring(0,1);
			 //End Of Kishore Podili For Status Reason
			  pstmt.setString(1,key);
        pstmt.setString(2,key);//added by subrahmanyam for 186630 on 15-oct-09
			  rs = pstmt.executeQuery();
			  if(rs.next())
				{
				 pnFlag = rs.getString(1);
          currentStatus = rs.getString(2);
				}

				if("N".equals(pnFlag))
					finalMap.put(key,changedStatus);
				else if("P".equals(pnFlag))
				{
					if("I".equals(currentStatus) && "A".equals(changedStatus))
					{
						if(!checkForHigherVersionQuotes(key,connection))
						  finalMap.put(key,changedStatus);
						 else
						  higherVersionMap.put(key,changedStatus);
					}
					
				else	if("A".equals(currentStatus) && "I".equals(changedStatus))
					{
						if(!checkInUpdateQuotes(key,connection))
						  finalMap.put(key,changedStatus);
						 else
						  updateReportMap.put(key,changedStatus);
					}
          else
					 finalMap.put(key,changedStatus);
				}            
      if(rs!=null)
      rs.close();
            }

  }catch(Exception e){e.printStackTrace();}
  finally{
	  ConnectionUtil.closePreparedStatement(pstmt,rs);
	  //ConnectionUtil.closeConnection(connection);
  }
}

private boolean checkInUpdateQuotes(String key,Connection con)
{
	boolean checkUpdateaval = false;
	PreparedStatement pstmt2 = null;
	ResultSet rst2 = null;
	String quoteId = null;
  int cnt =0;
	String countQuery = " SELECT count(*) FROM QMS_QUOTES_UPDATED QU WHERE QU.QUOTE_ID  IN ( SELECT ID FROM QMS_QUOTE_MASTER WHERE QUOTE_ID=? )";
	try{
	pstmt2=con.prepareStatement(countQuery);
	
	if(key!=null)
		{
		  pstmt2.setString(1,key);
		  rst2= pstmt2.executeQuery();
		  if(rst2.next())
      {
        cnt = rst2.getInt(1);
      }
        if(cnt>0)
				checkUpdateaval = true;
		
		}

	}catch(Exception e){e.printStackTrace();}
	finally{ ConnectionUtil.closePreparedStatement(pstmt2,rst2);}
	return checkUpdateaval;
}

private boolean checkForHigherVersionQuotes(String key,Connection con)
{
	boolean higerQuoteavl = false;
	PreparedStatement pstmt1 = null;
	ResultSet rst = null;
	String quoteId = null;
	String countQuery = " SELECT * FROM QMS_QUOTE_MASTER WHERE   QUOTE_ID =? ";
	try{
	pstmt1=con.prepareStatement(countQuery);
	quoteId=getNextVersionQuote(key);
	if(quoteId!=null)
		{
		  pstmt1.setString(1,quoteId);
		  rst= pstmt1.executeQuery();
		  if(rst.next())
				higerQuoteavl = true;
		
		}

	}catch(Exception e){e.printStackTrace();}
	finally{ ConnectionUtil.closePreparedStatement(pstmt1,rst);}
	return higerQuoteavl;
}
private String getNextVersionQuote(String quoteId)
{
	//  String                      quoteId  = null;
      String[]                   quotes=null;
      int                        quoteLen=0;
      String                     quoteSub=null; 
      String                     quoteSub1=null;
      String                     quoteSub2=null; 
	try{
	
	    if(quoteId.indexOf("_")==-1)
		  {
				quoteId=quoteId+"_001";
			
		  }
						
		else
		{
						quotes= quoteId.split("_");
						quoteSub=quotes[quotes.length-2];//first part of the quoteId
						
						if(quotes.length==2 && quoteSub.matches("[a-zA-Z]*"))
						{
                    quoteId=quoteId+"_001";
                 
						}
						else if(quotes.length==2 && !quoteSub.matches("[a-zA-Z]*"))
						{
								quoteLen = Integer.parseInt(quotes[quotes.length-1]);//2nd part of the quoteId
								quoteLen++;
								if(quoteLen<10)
								{
										quoteSub = Integer.toString(quoteLen);
	             			quoteSub1 ="00"+quoteSub;
										quoteId=quotes[quotes.length-2]+"_"+quoteSub1;
									
								}
								else if(quoteLen<100)
								{
									
									  quoteSub = Integer.toString(quoteLen);
										quoteSub="0"+quoteSub;
										quoteId=quotes[quotes.length-2]+"_"+quoteSub;
										
								}
								else
								{
										quoteSub = Integer.toString(quoteLen);
										quoteId=quotes[quotes.length-2]+"_"+quoteSub;
									
								}
						}
						else
						{
							
							quoteLen = Integer.parseInt(quotes[quotes.length-1]);//3rd part of the quoteId
							quoteLen++;
						
								if(quoteLen<10)
								{
									 quoteSub = Integer.toString(quoteLen);
										quoteSub1 ="00"+quoteSub;
										quoteId=quotes[quotes.length-3]+"_"+quotes[quotes.length-2]+"_"+quoteSub1;
									
								}
								else if(quoteLen<100)
								{
									
									 quoteSub = Integer.toString(quoteLen);
										quoteSub="0"+quoteSub;
										quoteId=quotes[quotes.length-3]+"_"+quotes[quotes.length-2]+"_"+quoteSub;
									
								}
								else
								{
										quoteSub = Integer.toString(quoteLen);
										quoteId=quotes[quotes.length-3]+"_"+quotes[quotes.length-2]+"_"+quoteSub;
									
								}
						}
		  
		}

	}catch(Exception e){e.printStackTrace();}
  return quoteId;
}
//ends 181670
  /**
   * This getYieldReportDetailsData() get the report for average yield of the quote 
   * @throws javax.ejb.EJBException
   * @return 
   * @param reportsEnterIdDOB
   */
    public ArrayList getYieldReportDetailsData(ReportsEnterIdDOB reportsEnterIdDOB)throws EJBException
    {
       Connection  connection=null;
      CallableStatement  cstmt          = null;
      ResultSet           rs            = null;
      ReportDetailsDOB reportDetailsDOB = null;
      ArrayList        dataList         = null;
      OperationsImpl   oimp             = null;
      ArrayList        mainDataList     = null;
      ArrayList        pageIterList     = null;
      
      long              expiryinDays     =  0;
      Timestamp        validuptodate    =  null;
      Timestamp        currentdate      =  null;
      Timestamp        effectivefromdate=  null;
      
      DecimalFormat    formater = null;
      try
      {
        formater = new DecimalFormat("##0.00");
       // getConnection();
        oimp=new OperationsImpl();
        connection= oimp.getConnection();
        dataList         = new ArrayList();
        mainDataList     = new ArrayList();
        pageIterList     = new ArrayList();
        
         //cstmt = connection.prepareCall("{call QMS_REPORT_PKG.ACTIVITY_REPORT(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");
        //cstmt = connection.prepareCall("{call QMS_REPORT_PKG.ACTIVITY_REPORT(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");//@@Modified by Kameswari for the WPBN issue-71825
       cstmt = connection.prepareCall("{call QMS_REPORT_PKG.ACTIVITY_REPORT(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");//@@Modified by Kameswari for the WPBN issue-154390
        cstmt.clearParameters();
        cstmt.setString(1,reportsEnterIdDOB.getFromDate());        
        cstmt.setString(2,reportsEnterIdDOB.getToDate());
        cstmt.setString(3,reportsEnterIdDOB.getDateFormat());
        cstmt.setString(4,reportsEnterIdDOB.getShipmentMode());
        cstmt.setString(5,reportsEnterIdDOB.getSalesPersonId());
        cstmt.setString(6,reportsEnterIdDOB.getCustomerId());
        cstmt.setString(7,reportsEnterIdDOB.getQuoteStatus());
        cstmt.setString(8,reportsEnterIdDOB.getOrginLocation());
        cstmt.setString(9,reportsEnterIdDOB.getDestLocation());
        cstmt.setString(10,reportsEnterIdDOB.getServiceLevel());   
         cstmt.setString(11,reportsEnterIdDOB.getTerminalId());  //@@Added by Kameswari for the WPBN issue-71825
        cstmt.setString(12,reportsEnterIdDOB.getLoginTerminal()); //@@Added by Kameswari for the WPBN issue-71825 
            cstmt.setString(13,reportsEnterIdDOB.getFromCountry());  //@@Added by RajKumari for the WPBN issue-143517
        cstmt.setString(14,reportsEnterIdDOB.getToCountry()); //@@Added by RajKumari for the WPBN issue-143517
        cstmt.setInt(15,reportsEnterIdDOB.getPageNo());
        cstmt.setInt(16,reportsEnterIdDOB.getNoOfRecords());
        cstmt.setString(17,reportsEnterIdDOB.getSortBy());
        cstmt.setString(18,reportsEnterIdDOB.getSortOrder());
        cstmt.setString(19,reportsEnterIdDOB.getAutoUpdated()); //@@Added by Kameswari for the WPBN issue-154390 on 21/02/08
        cstmt.registerOutParameter(20,Types.INTEGER);
        cstmt.registerOutParameter(21,Types.INTEGER);
        cstmt.registerOutParameter(22, OracleTypes.CURSOR);

        cstmt.execute();       
        rs = (ResultSet)cstmt.getObject(22);
        String currdate   =new  com.foursoft.esupply.common.util.ESupplyDateUtility().getCurrentDateStringWithTime("DD-MM-YYYY");
        currentdate         = new ESupplyDateUtility().getTimestamp("DD/MM/YY",currdate);
          while(rs.next())
          {         
              reportDetailsDOB = new ReportDetailsDOB();
              reportDetailsDOB.setSalesPerson(rs.getString("SALES_PERSON"));
              reportDetailsDOB.setQuoteDateTstmp(rs.getTimestamp("CREATED_DATE"));
              reportDetailsDOB.setQuoteId(rs.getString("QUOTE_ID"));
              reportDetailsDOB.setCustomerId(rs.getString("CUSTOMER_ID"));
              reportDetailsDOB.setCustomerName(rs.getString("COMPANYNAME"));//@@added by kameswari for teh WPBN issue-30313
              reportDetailsDOB.setQuoteStatus(rs.getString("QUOTE_STATUS"));
              if("1".equalsIgnoreCase(rs.getString("SMODE")))
              {
                reportDetailsDOB.setShipmentMode("Air");
              }
              else if("2".equalsIgnoreCase(rs.getString("SMODE")))
              {
                reportDetailsDOB.setShipmentMode("Sea");
              }
              else if("4".equalsIgnoreCase(rs.getString("SMODE")))
              {
                reportDetailsDOB.setShipmentMode("Truck");
              }
              else if("100".equalsIgnoreCase(rs.getString("SMODE")))
              {
                reportDetailsDOB.setShipmentMode("Multi-Modal");
              }
              //reportDetailsDOB.setShipmentMode(rs.getString("SMODE"));
              reportDetailsDOB.setServiceLevel(rs.getString("SERVICE"));
              reportDetailsDOB.setFromCountry(rs.getString("ORG_COUNTRY"));
              reportDetailsDOB.setFromLocation(rs.getString("ORIGIN_LOCATION"));
              reportDetailsDOB.setToCountry(rs.getString("DEST_COUNTRY"));
              reportDetailsDOB.setToLocation(rs.getString("DEST_LOCATION"));
              reportDetailsDOB.setAverageYield(formater.format(rs.getDouble("AVG_YIELD")));
              
              
              dataList.add(reportDetailsDOB);
              
         }
           pageIterList.add(new Integer(cstmt.getInt(20)));
           pageIterList.add(new Integer(cstmt.getInt(21  )));
           mainDataList.add(pageIterList);
           mainDataList.add(dataList);           
      }
      catch(SQLException e)
      {
        e.printStackTrace();
        //Logger.error(FILE_NAME,"SQLException in getYieldReportDetailsData( ) method"+e.toString());
        logger.error(FILE_NAME+"SQLException in getYieldReportDetailsData( ) method"+e.toString());
        throw new EJBException();        
      }catch(EJBException e)
      {
      e.printStackTrace();
        //Logger.error(FILE_NAME,"EJBException in getYieldReportDetailsData() method"+e.toString());
        logger.error(FILE_NAME+"EJBException in getYieldReportDetailsData() method"+e.toString());
        throw new EJBException();         
      }catch(Exception e)
      {
      e.printStackTrace();
        //Logger.error(FILE_NAME,"Exception in getYieldReportDetailsData() method"+e.toString());
        logger.error(FILE_NAME+"Exception in getYieldReportDetailsData() method"+e.toString());
        throw new EJBException();         
      }finally
      {
        try
        {
          if(rs!=null)
           rs.close();
          if(cstmt!=null)
          { cstmt.close();}
          if(connection!=null)
          { connection.close();}
        }catch(SQLException e) 
        {
          //Logger.error(FILE_NAME,"SQLException in getYieldReportDetailsData( ) method"+e.toString());
          logger.error(FILE_NAME+"SQLException in getYieldReportDetailsData( ) method"+e.toString());
          throw new EJBException();        
        }
      }
       return mainDataList;
    }
    
    /**
   * This getYieldReportDetailsDataForExcl() get the report for average yield of the quote 
   * @throws javax.ejb.EJBException
   * @return 
   * @param reportsEnterIdDOB
   */
    public ArrayList getYieldReportDetailsDataForExcl(ReportsEnterIdDOB reportsEnterIdDOB)throws EJBException
    {
       Connection  connection=null;
      CallableStatement  cstmt          = null;
      ResultSet           rs            = null;
      ReportDetailsDOB reportDetailsDOB = null;
      ArrayList        dataList         = null;
      OperationsImpl   oimp             = null;
      ArrayList        mainDataList     = null;
      ArrayList        pageIterList     = null;
      
      long              expiryinDays     =  0;
      Timestamp        validuptodate    =  null;
      Timestamp        currentdate      =  null;
      Timestamp        effectivefromdate=  null;
      
      DecimalFormat    formater = null;
      try
      {
         formater = new DecimalFormat("##0.00");
       // getConnection();
        oimp=new OperationsImpl();
        connection= oimp.getConnection();
        dataList         = new ArrayList();
        mainDataList     = new ArrayList();
        pageIterList     = new ArrayList();
        
       // cstmt = connection.prepareCall("{call QMS_REPORT_PKG.ACTIVITY_REPORT_EXCEL(?,?,?,?,?,?,?,?,?,?,?)}");
       //cstmt = connection.prepareCall("{call QMS_REPORT_PKG.ACTIVITY_REPORT_EXCEL(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");//@@Modified by Kameswari for the WPBN issue-71825
       cstmt = connection.prepareCall("{call QMS_REPORT_PKG.ACTIVITY_REPORT_EXCEL(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");//@@Modified by Kameswari for the WPBN issue-154390
        cstmt.clearParameters();
        cstmt.setString(1,reportsEnterIdDOB.getFromDate());        
        cstmt.setString(2,reportsEnterIdDOB.getToDate());
        cstmt.setString(3,reportsEnterIdDOB.getDateFormat());
        cstmt.setString(4,reportsEnterIdDOB.getShipmentMode());
        cstmt.setString(5,reportsEnterIdDOB.getSalesPersonId());
        cstmt.setString(6,reportsEnterIdDOB.getCustomerId());
        cstmt.setString(7,reportsEnterIdDOB.getQuoteStatus());
        cstmt.setString(8,reportsEnterIdDOB.getOrginLocation());
        cstmt.setString(9,reportsEnterIdDOB.getDestLocation());
        cstmt.setString(10,reportsEnterIdDOB.getServiceLevel());  
        cstmt.setString(11,reportsEnterIdDOB.getTerminalId());  //@@Added by Kameswari for the WPBN issue-71825
        cstmt.setString(12,reportsEnterIdDOB.getLoginTerminal()); //@@Added by Kameswari for the WPBN issue-71825 
        cstmt.setString(13,reportsEnterIdDOB.getFromCountry());  //@@Added by RajKumari for the WPBN issue-143517
        cstmt.setString(14,reportsEnterIdDOB.getToCountry()); //@@Added by RajKumari for the WPBN issue-143517
        cstmt.setString(15,reportsEnterIdDOB.getAutoUpdated()); //@@Added by Kameswari for the WPBN issue-154390 on 21/02/09
        cstmt.registerOutParameter(16, OracleTypes.CURSOR);
        cstmt.execute();       
        rs = (ResultSet)cstmt.getObject(16);
        String currdate   =new  com.foursoft.esupply.common.util.ESupplyDateUtility().getCurrentDateStringWithTime("DD-MM-YYYY");
        currentdate         = new ESupplyDateUtility().getTimestamp("DD/MM/YY",currdate);
          while(rs.next())
          {         
              reportDetailsDOB = new ReportDetailsDOB();
              reportDetailsDOB.setSalesPerson(rs.getString("SALES_PERSON"));
              reportDetailsDOB.setQuoteDateTstmp(rs.getTimestamp("CREATED_DATE"));
              reportDetailsDOB.setQuoteId(rs.getString("QUOTE_ID"));
              reportDetailsDOB.setCustomerId(rs.getString("CUSTOMER_ID"));
              reportDetailsDOB.setCustomerName(rs.getString("COMPANYNAME"));
              reportDetailsDOB.setQuoteStatus(rs.getString("QUOTE_STATUS"));
              if("1".equalsIgnoreCase(rs.getString("SMODE")))
              {
                reportDetailsDOB.setShipmentMode("Air");
              }
              else if("2".equalsIgnoreCase(rs.getString("SMODE")))
              {
                reportDetailsDOB.setShipmentMode("Sea");
              }
              else if("4".equalsIgnoreCase(rs.getString("SMODE")))
              {
                reportDetailsDOB.setShipmentMode("Truck");
              }
              else if("100".equalsIgnoreCase(rs.getString("SMODE")))
              {
                reportDetailsDOB.setShipmentMode("Multi-Modal");
              }
              //reportDetailsDOB.setShipmentMode(rs.getString("SMODE"));
              reportDetailsDOB.setServiceLevel(rs.getString("SERVICE"));
              reportDetailsDOB.setFromCountry(rs.getString("ORG_COUNTRY"));
              reportDetailsDOB.setFromLocation(rs.getString("ORIGIN_LOCATION"));
              reportDetailsDOB.setToCountry(rs.getString("DEST_COUNTRY"));
              reportDetailsDOB.setToLocation(rs.getString("DEST_LOCATION"));
              reportDetailsDOB.setAverageYield(formater.format(rs.getDouble("AVG_YIELD")));
              
              
              dataList.add(reportDetailsDOB);
              
         }
           mainDataList.add(pageIterList);
           mainDataList.add(dataList);           
      }
      catch(SQLException e)
      {
        e.printStackTrace();
        //Logger.error(FILE_NAME,"SQLException in getYieldReportDetailsDataForExcl( ) method"+e.toString());
        logger.error(FILE_NAME+"SQLException in getYieldReportDetailsDataForExcl( ) method"+e.toString());
        throw new EJBException();        
      }catch(EJBException e)
      {
      e.printStackTrace();
        //Logger.error(FILE_NAME,"EJBException in getYieldReportDetailsDataForExcl() method"+e.toString());
        logger.error(FILE_NAME+"EJBException in getYieldReportDetailsDataForExcl() method"+e.toString());
        throw new EJBException();         
      }catch(Exception e)
      {
      e.printStackTrace();
        //Logger.error(FILE_NAME,"Exception in getYieldReportDetailsDataForExcl() method"+e.toString());
        logger.error(FILE_NAME+"Exception in getYieldReportDetailsDataForExcl() method"+e.toString());
        throw new EJBException();         
      }finally
      {
        try
        {
          if(rs!=null)
           rs.close();
          if(cstmt!=null)
          { cstmt.close();}
          if(connection!=null)
          { connection.close();}
        }catch(SQLException e) 
        {
          //Logger.error(FILE_NAME,"SQLException in getYieldReportDetailsDataForExcl( ) method"+e.toString());
          logger.error(FILE_NAME+"SQLException in getYieldReportDetailsDataForExcl( ) method"+e.toString());
          throw new EJBException();        
        }
      }
       return mainDataList;
    }
    
    public ArrayList getYieldReportDetailsLegs(String quoteId)throws Exception
    {
       Connection  connection=null;
      CallableStatement  cstmt          = null;
      ResultSet           rs            = null;
      ArrayList    list_data    = null;
      OperationsImpl   oimp             = null;
      YieldDetailsDOB  yieldDetailsDOB  = null;
      ArrayList     brkList  = null;
      ArrayList     yieldlist = null;
      DecimalFormat    formater = null;
      try
      {
       
       formater = new DecimalFormat("##0.00");
        list_data = new ArrayList();
        oimp=new OperationsImpl();
        connection= oimp.getConnection();   
        
        cstmt = connection.prepareCall("{call QMS_REPORT_PKG.yeild_dtls_proc(?,?)}");
        cstmt.setString(1,quoteId);
        
        cstmt.registerOutParameter(2,OracleTypes.CURSOR);
        cstmt.execute();
        rs = (ResultSet)cstmt.getObject(2);
        
        
        
        while(rs.next())
        {
          

            
            if( yieldDetailsDOB !=null && yieldDetailsDOB.getLegSLNo().equals(rs.getString("serial_no") ))
            {
              brkList.add(rs.getString("break_point"));
              yieldlist.add(rs.getString("YEILD"));           
            }else
            {
            
             
             yieldDetailsDOB = new YieldDetailsDOB();
              
             list_data.add(yieldDetailsDOB);
              
             brkList   =  new ArrayList();
             yieldlist =  new ArrayList();
            
            yieldDetailsDOB.setQuoteId(quoteId);  
            yieldDetailsDOB.setCustomerId(rs.getString("customer_id"));
            yieldDetailsDOB.setCustomerName(rs.getString("companyname"));//@@added by kameswari for the WPBN issue-30313
            yieldDetailsDOB.setShipmentMode(rs.getString("shpmnt_mode"));
            yieldDetailsDOB.setServiveLevel(rs.getString("servicelevel"));
            yieldDetailsDOB.setOrgCountry(rs.getString("org_country"));
            yieldDetailsDOB.setDestCountry(rs.getString("dest_country"));
            
            yieldDetailsDOB.setOrgLocation(rs.getString("orig_loc"));
            yieldDetailsDOB.setDestLocation(rs.getString("dest_loc"));
            yieldDetailsDOB.setLegSLNo(rs.getString("serial_no"));
            
            yieldDetailsDOB.setBrkPoint(brkList);
            yieldDetailsDOB.setYieldValue(yieldlist);
            
            brkList.add(rs.getString("break_point"));
            yieldlist.add(formater.format(rs.getDouble("YEILD")));
            

            }
          
        }
         }catch(Exception e)
      {
        logger.info(FILE_NAME+"exception in getYieldReportDetailsLegs()--"+e);
        throw new Exception();
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
          
        }
      }
      
      return list_data;
    }
    
  /** This getActivityReportDetailsData() get the data from the procedure for activity report
   * 
   */
  public ArrayList getActivityReportDetailsData(ReportsEnterIdDOB reportsEnterIdDOB)throws EJBException
    {
      Connection  connection=null;
      CallableStatement  cstmt          = null;
      ResultSet           rs            = null;
      ReportDetailsDOB reportDetailsDOB = null;
      ArrayList        dataList         = null;
      OperationsImpl   oimp             = null;
      ArrayList        mainDataList     = null;
      ArrayList        pageIterList     = null;
      
      long              expiryinDays     =  0;
      Timestamp        validuptodate    =  null;
      Timestamp        currentdate      =  null;
      Timestamp        effectivefromdate=  null;
      
      //Added By Kishroe Podili For StatusReason
      String		   selectStatusReasonQuery	= "SELECT id,status_reason FROM QMS_STATUS_REASON  WHERE INVALID = 'F'"; 
      Statement	statusReasonstmt				= null;
      ResultSet			statusReasonRs			= null;
      HashMap<Integer, String> 		statusReasonMap		= null;
      //End Of Kishore Podili For StatusReason
      
      try
      {
       // getConnection();
        oimp=new OperationsImpl();
        connection= oimp.getConnection();
        dataList         = new ArrayList();
        mainDataList     = new ArrayList();
        pageIterList     = new ArrayList();
        
        //cstmt = connection.prepareCall("{call QMS_REPORT_PKG.ACTIVITY_REPORT(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");
        //cstmt = connection.prepareCall("{call QMS_REPORT_PKG.ACTIVITY_REPORT(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");//@@Modified by Kameswari for the WPBN issue-71825
        //cstmt = connection.prepareCall("{call QMS_REPORT_PKG.ACTIVITY_REPORT(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");//@@Modified by Rajkumai for the WPBN issue-143517
        cstmt = connection.prepareCall("{call QMS_REPORT_PKG.ACTIVITY_REPORT(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");//@@Modified by Kameswari for the WPBN issue-154390
        cstmt.clearParameters();
        cstmt.setString(1,reportsEnterIdDOB.getFromDate());        
        cstmt.setString(2,reportsEnterIdDOB.getToDate());
        cstmt.setString(3,reportsEnterIdDOB.getDateFormat());
        cstmt.setString(4,reportsEnterIdDOB.getShipmentMode());
        cstmt.setString(5,reportsEnterIdDOB.getSalesPersonId());
        cstmt.setString(6,reportsEnterIdDOB.getCustomerId());
        cstmt.setString(7,reportsEnterIdDOB.getQuoteStatus());
        cstmt.setString(8,reportsEnterIdDOB.getOrginLocation());
        cstmt.setString(9,reportsEnterIdDOB.getDestLocation());
        cstmt.setString(10,reportsEnterIdDOB.getServiceLevel());  
        cstmt.setString(11,reportsEnterIdDOB.getTerminalId());  //@@Added by Kameswari for the WPBN issue-71825
        cstmt.setString(12,reportsEnterIdDOB.getLoginTerminal()); //@@Added by Kameswari for the WPBN issue-71825 
        cstmt.setString(13,reportsEnterIdDOB.getFromCountry());  //@@Added by RajKumari for the WPBN issue-143517
        cstmt.setString(14,reportsEnterIdDOB.getToCountry()); //@@Added by RajKumari for the WPBN issue-143517
        cstmt.setInt(15,reportsEnterIdDOB.getPageNo());
        cstmt.setInt(16,reportsEnterIdDOB.getNoOfRecords());
        cstmt.setString(17,reportsEnterIdDOB.getSortBy());
        cstmt.setString(18,reportsEnterIdDOB.getSortOrder());
        cstmt.setString(19,reportsEnterIdDOB.getAutoUpdated()); //@@Added by Kameswari for the WPBN issue-154390 on 21/02/09
        cstmt.registerOutParameter(20,Types.INTEGER);
        cstmt.registerOutParameter(21,Types.INTEGER);
        cstmt.registerOutParameter(22, OracleTypes.CURSOR);
        cstmt.execute();       
        rs = (ResultSet)cstmt.getObject(22);
        String currdate   =new  com.foursoft.esupply.common.util.ESupplyDateUtility().getCurrentDateStringWithTime("DD-MM-YYYY");
        currentdate         = new ESupplyDateUtility().getTimestamp("DD/MM/YY",currdate);
          while(rs.next())
          {         
              reportDetailsDOB = new ReportDetailsDOB();
              reportDetailsDOB.setSalesPerson(rs.getString("SALES_PERSON"));
              reportDetailsDOB.setQuoteDateTstmp(rs.getTimestamp("CREATED_DATE"));
              reportDetailsDOB.setQuoteId(rs.getString("QUOTE_ID"));
			  reportDetailsDOB.setCreatedBy(rs.getString("CREATED_BY"));//Added by subrahmanyam for wpbn:173831
              reportDetailsDOB.setCustomerId(rs.getString("CUSTOMER_ID"));
              reportDetailsDOB.setCustomerName(rs.getString("COMPANYNAME"));//@@added by kameswari for the WPBN issue-30313 
              reportDetailsDOB.setQuoteStatus(rs.getString("QUOTE_STATUS"));
              if("1".equalsIgnoreCase(rs.getString("SMODE")))
              {
                reportDetailsDOB.setShipmentMode("Air");
              }
              else if("2".equalsIgnoreCase(rs.getString("SMODE")))
              {
                reportDetailsDOB.setShipmentMode("Sea");
              }
              else if("4".equalsIgnoreCase(rs.getString("SMODE")))
              {
                reportDetailsDOB.setShipmentMode("Truck");
              }
              else if("100".equalsIgnoreCase(rs.getString("SMODE")))
              {
                reportDetailsDOB.setShipmentMode("Multi-Modal");
              }
              //reportDetailsDOB.setShipmentMode(rs.getString("SMODE"));
              reportDetailsDOB.setServiceLevel(rs.getString("SERVICE"));
              reportDetailsDOB.setFromCountry(rs.getString("ORG_COUNTRY"));
              reportDetailsDOB.setFromLocation(rs.getString("ORIGIN_LOCATION"));
              reportDetailsDOB.setToCountry(rs.getString("DEST_COUNTRY"));
              reportDetailsDOB.setToLocation(rs.getString("DEST_LOCATION"));
              reportDetailsDOB.setActiveFlag(rs.getString("ACTIVE_FLAG"));
              reportDetailsDOB.setIsMultiQuote(rs.getString("IS_MULTI_QUOTE"));//Added by Anil.k to get Multi Quote or Not
              reportDetailsDOB.setStatusReason(rs.getString("STATUS_REASON"));//Added by Kishore Podili For StatusReason
              
              dataList.add(reportDetailsDOB);
              
         }
           pageIterList.add(new Integer(cstmt.getInt(20)));
           pageIterList.add(new Integer(cstmt.getInt(21)));
           mainDataList.add(pageIterList);
           mainDataList.add(dataList);           
           
           
           
         //Added By Kishore For The Quote StatusReason
           statusReasonMap = new HashMap<Integer,String>();
           statusReasonstmt = connection.createStatement();
           statusReasonRs = statusReasonstmt.executeQuery(selectStatusReasonQuery);
           while(statusReasonRs.next()){
        	   
        	   statusReasonMap.put(statusReasonRs.getInt("id"),statusReasonRs.getString("STATUS_REASON"));
           }
           mainDataList.add(statusReasonMap);
           
          //End Of Kishore For The Quote StatusReason
      }
      catch(SQLException e)
      {
        e.printStackTrace();
        //Logger.error(FILE_NAME,"SQLException in getActivityReportDetailsData( ) method"+e.toString());
        logger.error(FILE_NAME+"SQLException in getActivityReportDetailsData( ) method"+e.toString());
        throw new EJBException();        
      }catch(EJBException e)
      {
      e.printStackTrace();
        //Logger.error(FILE_NAME,"EJBException in getActivityReportDetailsData() method"+e.toString());
        logger.error(FILE_NAME+"EJBException in getActivityReportDetailsData() method"+e.toString());
        throw new EJBException();         
      }catch(Exception e)
      {
      e.printStackTrace();
        //Logger.error(FILE_NAME,"Exception in getActivityReportDetailsData() method"+e.toString());
        logger.error(FILE_NAME+"Exception in getActivityReportDetailsData() method"+e.toString());
        throw new EJBException();         
      }finally
      {
        try
        {
          if(rs!=null)
            rs.close();
          if(cstmt!=null)
          { cstmt.close();}
          ConnectionUtil.closeStatement(statusReasonstmt,statusReasonRs);// Added by Dilip for PMD Correction on 22/09/2015
          if(connection!=null)
          { connection.close();}
        }catch(SQLException e) 
        {
          //Logger.error(FILE_NAME,"SQLException in getActivityReportDetailsData( ) method"+e.toString());
          logger.error(FILE_NAME+"SQLException in getActivityReportDetailsData( ) method"+e.toString());
          throw new EJBException();        
        }
      }
       return mainDataList;
    }
 /** This getActivityReportDetailsDataForExcl() get the data from the procedure for activity report
   * 
   */
  public ArrayList getActivityReportDetailsDataForExcl(ReportsEnterIdDOB reportsEnterIdDOB)throws EJBException
  {
      Connection  connection=null;
      CallableStatement  cstmt          = null;
      ResultSet           rs            = null;
      ReportDetailsDOB reportDetailsDOB = null;
      ArrayList        dataList         = null;
      OperationsImpl   oimp             = null;
      ArrayList        mainDataList     = null;
      ArrayList        pageIterList     = null;
      
      long              expiryinDays     =  0;
      Timestamp        validuptodate    =  null;
      Timestamp        currentdate      =  null;
      Timestamp        effectivefromdate=  null;
      try
      {
       // getConnection();
         oimp=new OperationsImpl();
        connection= oimp.getConnection();
        dataList         = new ArrayList();
        mainDataList     = new ArrayList();
        pageIterList     = new ArrayList();
        
        //cstmt = connection.prepareCall("{call QMS_REPORT_PKG.ACTIVITY_REPORT_EXCEL(?,?,?,?,?,?,?,?,?,?,?)}");
        //cstmt = connection.prepareCall("{call QMS_REPORT_PKG.ACTIVITY_REPORT_EXCEL(?,?,?,?,?,?,?,?,?,?,?,?,?)}");//@@Modified by Kameswari for the WPBN issue-71825 
       // cstmt = connection.prepareCall("{call QMS_REPORT_PKG.ACTIVITY_REPORT_EXCEL(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");//@@Modified by Rajkumari for the WPBN issue-143517 
        cstmt = connection.prepareCall("{call QMS_REPORT_PKG.ACTIVITY_REPORT_EXCEL(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");//@@Modified by Kameswari for the WPBN issue-154390 
        cstmt.clearParameters();
        cstmt.setString(1,reportsEnterIdDOB.getFromDate());        
        cstmt.setString(2,reportsEnterIdDOB.getToDate());
        cstmt.setString(3,reportsEnterIdDOB.getDateFormat());
        cstmt.setString(4,reportsEnterIdDOB.getShipmentMode());
        cstmt.setString(5,reportsEnterIdDOB.getSalesPersonId());
        cstmt.setString(6,reportsEnterIdDOB.getCustomerId());
        cstmt.setString(7,reportsEnterIdDOB.getQuoteStatus());
        cstmt.setString(8,reportsEnterIdDOB.getOrginLocation());
        cstmt.setString(9,reportsEnterIdDOB.getDestLocation());
        cstmt.setString(10,reportsEnterIdDOB.getServiceLevel());
        cstmt.setString(11,reportsEnterIdDOB.getTerminalId());  //@@Added by Kameswari for the WPBN issue-71825
        cstmt.setString(12,reportsEnterIdDOB.getLoginTerminal()); //@@Added by Kameswari for the WPBN issue-71825 
        cstmt.setString(13,reportsEnterIdDOB.getFromCountry());  //@@Added by RajKumari for the WPBN issue-143517
        cstmt.setString(14,reportsEnterIdDOB.getToCountry()); //@@Added by RajKumari for the WPBN issue-143517
        cstmt.setString(15,reportsEnterIdDOB.getAutoUpdated()); //@@Added by Kameswari for the WPBN issue-154390 on 21/02/09
        cstmt.registerOutParameter(16, OracleTypes.CURSOR);
        cstmt.execute();       
        rs = (ResultSet)cstmt.getObject(16);
        String currdate   =new  com.foursoft.esupply.common.util.ESupplyDateUtility().getCurrentDateStringWithTime("DD-MM-YYYY");
        currentdate         = new ESupplyDateUtility().getTimestamp("DD/MM/YY",currdate);
          while(rs.next())
          {         
              reportDetailsDOB = new ReportDetailsDOB();
              reportDetailsDOB.setSalesPerson(rs.getString("SALES_PERSON"));
              reportDetailsDOB.setQuoteDateTstmp(rs.getTimestamp("CREATED_DATE"));
              reportDetailsDOB.setQuoteId(rs.getString("QUOTE_ID"));
              reportDetailsDOB.setCreatedBy(rs.getString("CREATED_BY"));//Added by subrahmanyam for wpbn:173831
              reportDetailsDOB.setCustomerId(rs.getString("CUSTOMER_ID"));
              reportDetailsDOB.setCustomerName(rs.getString("COMPANYNAME"));//@@added by kameswari for the WPBN issue-30313
              reportDetailsDOB.setQuoteStatus(rs.getString("QUOTE_STATUS"));
              if("1".equalsIgnoreCase(rs.getString("SMODE")))
              {
                reportDetailsDOB.setShipmentMode("Air");
              }
              else if("2".equalsIgnoreCase(rs.getString("SMODE")))
              {
                reportDetailsDOB.setShipmentMode("Sea");
              }
              else if("4".equalsIgnoreCase(rs.getString("SMODE")))
              {
                reportDetailsDOB.setShipmentMode("Truck");
              }
              else if("100".equalsIgnoreCase(rs.getString("SMODE")))
              {
                reportDetailsDOB.setShipmentMode("Multi-Modal");
              }
              //reportDetailsDOB.setShipmentMode(rs.getString("SMODE"));
              reportDetailsDOB.setServiceLevel(rs.getString("SERVICE"));
              reportDetailsDOB.setFromCountry(rs.getString("ORG_COUNTRY"));
              reportDetailsDOB.setFromLocation(rs.getString("ORIGIN_LOCATION"));
              reportDetailsDOB.setToCountry(rs.getString("DEST_COUNTRY"));
              reportDetailsDOB.setToLocation(rs.getString("DEST_LOCATION"));
              reportDetailsDOB.setStatusReason(rs.getString("STATUS_REASON")); //Added By Kishore Podili for Status Reason
              //Added by Rakesh on 22-02-2011 for Issue:236359
              reportDetailsDOB.setModifyDate(rs.getTimestamp("MODIFIED_DATE"));
              reportDetailsDOB.setIncoTerms(rs.getString("INCO_TERMS_ID"));
              reportDetailsDOB.setTerminalId(rs.getString("TERMINAL_ID"));
              reportDetailsDOB.setCountry(rs.getString("COUNTRY_ID"));
              reportDetailsDOB.setCustDate(rs.getTimestamp("CUST_REQUESTED_DATE"));
              reportDetailsDOB.setValidTo(rs.getTimestamp("VALID_TO"));
              reportDetailsDOB.setQuoteStatus(rs.getString("QUOTE_STATUS")!=null?rs.getString("QUOTE_STATUS"):"");
              reportDetailsDOB.setActiveFlag(rs.getString("ACTIVE_FLAG")!=null?rs.getString("ACTIVE_FLAG"):"");
              reportDetailsDOB.setVersionNo(rs.getInt("VERSION_NO"));//Added by Rakesh on 21-03-2011
              reportDetailsDOB.setMaxVersionNo(rs.getInt("MAXVERSIONNO"));//Added by Rakesh on 21-03-2011
              reportDetailsDOB.setCustReqTime(rs.getString("CUST_REQUESTED_TIME")!=null?rs.getString("CUST_REQUESTED_TIME"):"");
              reportDetailsDOB.setIs_MultiQuote(rs.getString("IS_MULTI_QUOTE")!= null?rs.getString("IS_MULTI_QUOTE"):"N");
              
              //Added by Rakesh on 22-02-2011 for Issue:236359
              dataList.add(reportDetailsDOB);
              
         }
           mainDataList.add(pageIterList);
           mainDataList.add(dataList);           
      }
      catch(SQLException e)
      {
        e.printStackTrace();
        //Logger.error(FILE_NAME,"SQLException in getActivityReportDetailsDataForExcl( ) method"+e.toString());
        logger.error(FILE_NAME+"SQLException in getActivityReportDetailsDataForExcl( ) method"+e.toString());
        throw new EJBException();        
      }catch(EJBException e)
      {
      e.printStackTrace();
        //Logger.error(FILE_NAME,"EJBException in getActivityReportDetailsDataForExcl() method"+e.toString());
        logger.error(FILE_NAME+"EJBException in getActivityReportDetailsDataForExcl() method"+e.toString());
        throw new EJBException();         
      }catch(Exception e)
      {
      e.printStackTrace();
        //Logger.error(FILE_NAME,"Exception in getActivityReportDetailsDataForExcl() method"+e.toString());
        logger.error(FILE_NAME+"Exception in getActivityReportDetailsDataForExcl() method"+e.toString());
        throw new EJBException();         
      }finally
      {
        try
        {
          if(rs!=null)
            rs.close();
          if(cstmt!=null)
          { cstmt.close();}
          if(connection!=null)
          { connection.close();}
        }catch(SQLException e) 
        {
          //Logger.error(FILE_NAME,"SQLException in getActivityReportDetailsDataForExcl( ) method"+e.toString());
          logger.error(FILE_NAME+"SQLException in getActivityReportDetailsDataForExcl( ) method"+e.toString());
          throw new EJBException();        
        }
      }
       return mainDataList;
    }
    
/** this method getPendingQuoteReportData() get the pending quotes data
 * 
 * */
 public ArrayList getPendingQuoteReportData(ReportsEnterIdDOB reportsEnterIdDOB)throws EJBException
    {
      Connection  cconnection=null;
      CallableStatement  cstmt          = null;
      ResultSet           rs            = null;
      ReportDetailsDOB reportDetailsDOB = null;
      ArrayList        dataList         = null;
      OperationsImpl   oimp             = null;
      ArrayList        mainDataList     = null;
      ArrayList        pageIterList     = new ArrayList();
      
      long             expiryinDays       =   0;
      Timestamp        validuptodate      =   null;
      Timestamp        currentdate        =   null;
      Timestamp        effectivefromdate  =   null;
      String           fromDate           =   null;
      String           toDate             =   null;
      String		   selectStatusReasonQuery	= "SELECT id,status_reason FROM QMS_STATUS_REASON  WHERE INVALID = 'F'"; 
      Statement	statusReasonstmt				= null;
      ResultSet			statusReasonRs			= null;
      HashMap<Integer, String> 		statusReasonMap		= null;	
      try
      {
        
        oimp        = new OperationsImpl();
        cconnection = oimp.getConnection();
        dataList          =   new ArrayList();
        mainDataList      =   new ArrayList();
        pageIterList      =   new ArrayList();
        if(reportsEnterIdDOB.getFromDate()!=null && reportsEnterIdDOB.getFromDate().length()>0)
          fromDate          =   reportsEnterIdDOB.getFromDate().replaceAll(",","~");
        if(reportsEnterIdDOB.getToDate()!=null && reportsEnterIdDOB.getToDate().length()>0)
          toDate            =   reportsEnterIdDOB.getToDate().replaceAll(",","~");
      
        cstmt = cconnection.prepareCall("{call QMS_REPORT_PKG.QMS_PENDING_REPORT(?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");
          cstmt.setString(1,reportsEnterIdDOB.getShipmentMode());
        cstmt.setString(2,reportsEnterIdDOB.getConsoleType());
        cstmt.setString(3,reportsEnterIdDOB.getPrimaryOption());
        cstmt.setString(4,fromDate);
        cstmt.setString(5,toDate);
        cstmt.setString(6,reportsEnterIdDOB.getTerminalId());
        cstmt.setString(7,reportsEnterIdDOB.getUserDateFormat());
        cstmt.setInt(8,reportsEnterIdDOB.getPageNo());
        cstmt.setInt(9,reportsEnterIdDOB.getNoOfRecords());
         cstmt.setString(10,reportsEnterIdDOB.getSortBy());
        cstmt.setString(11,reportsEnterIdDOB.getSortOrder());
        cstmt.registerOutParameter(12, OracleTypes.INTEGER);
        cstmt.registerOutParameter(13, OracleTypes.INTEGER);        
        cstmt.registerOutParameter(14, OracleTypes.CURSOR);
        cstmt.execute();  
        
        
        rs = (ResultSet)cstmt.getObject(14);
        while(rs.next())
          {
              reportDetailsDOB = new ReportDetailsDOB();
              
              reportDetailsDOB.setQuoteId(rs.getString("QUOTE_ID"));
              reportDetailsDOB.setCreateDate(rs.getTimestamp("CREATED_DATE"));//@@added by kameswari for the WPBN issue-61310
              reportDetailsDOB.setImportant(rs.getString("IU_FLAG"));
              reportDetailsDOB.setCustomerId(rs.getString("CUSTOMER_ID"));
              reportDetailsDOB.setCustomerName(rs.getString("COMPANYNAME"));//@@added by kameswari for the WPBN issue-30313
              reportDetailsDOB.setServiceLevel(rs.getString("SERVICE_LEVEL_ID"));
              reportDetailsDOB.setFromCountry(rs.getString("org_country"));
              reportDetailsDOB.setFromLocation(rs.getString("ORIGIN_LOCATION"));
              reportDetailsDOB.setToLocation(rs.getString("DEST_LOCATION"));
              reportDetailsDOB.setToCountry(rs.getString("dest_country"));
              reportDetailsDOB.setIsMultiQuote(rs.getString("IS_MULTI_QUOTE"));//Added by Anil.k to get ismultiquote value 
              
              
              dataList.add(reportDetailsDOB);
         
         }
           pageIterList.add(new Integer(cstmt.getInt(12)));   
           pageIterList.add(new Integer(cstmt.getInt(13)));
           mainDataList.add(pageIterList);
           mainDataList.add(dataList);           
           
         //Added By Kishore For The Quote StatusReason
           statusReasonMap = new HashMap<Integer,String>();
           statusReasonstmt = cconnection.createStatement();
           statusReasonRs = statusReasonstmt.executeQuery(selectStatusReasonQuery);
           while(statusReasonRs.next()){
        	   
        	   statusReasonMap.put(statusReasonRs.getInt("id"),statusReasonRs.getString("STATUS_REASON"));
           }
           mainDataList.add(statusReasonMap);
           
          //End Of Kishore For The Quote StatusReason
        	   
        	   
           
           
           
      }
      catch(SQLException e) 
      {
        e.printStackTrace();
        //Logger.error(FILE_NAME,"SQLException in getPendingQuoteReportData( ) method"+e.toString());
        logger.error(FILE_NAME+"SQLException in getPendingQuoteReportData( ) method"+e.toString());
        throw new EJBException();        
      }
      catch(EJBException e)
      {
        e.printStackTrace();
        //Logger.error(FILE_NAME,"EJBException in getPendingQuoteReportData() method"+e.toString());
        logger.error(FILE_NAME+"EJBException in getPendingQuoteReportData() method"+e.toString());
        throw new EJBException();         
      }catch(Exception e)
      {
      e.printStackTrace();
        //Logger.error(FILE_NAME,"Exception in getPendingQuoteReportData() method"+e.toString());
        logger.error(FILE_NAME+"Exception in getPendingQuoteReportData() method"+e.toString());
        throw new EJBException();         
      }finally
      {
        try
        {
          if(rs!=null)
          rs.close();
          if(cstmt!=null)
        	  cstmt.close();      ConnectionUtil.closeStatement(statusReasonstmt, statusReasonRs);          
          if(cconnection!=null)
          { cconnection.close();}
        }catch(SQLException e)
        {
          //Logger.error(FILE_NAME,"SQLException in getPendingQuoteReportData( ) method"+e.toString());
          logger.error(FILE_NAME+"SQLException in getPendingQuoteReportData( ) method"+e.toString());
          throw new EJBException();        
        }
      }
       return mainDataList;
    }
    
 /** this method getPendingQuoteReportDataForExcl() get the pending quotes data
 * 
 * */
 public ArrayList getPendingQuoteReportDataForExcl(ReportsEnterIdDOB reportsEnterIdDOB)throws EJBException
    {
      Connection  cconnection=null;
      CallableStatement  cstmt          = null;
      ResultSet           rs            = null;
      ReportDetailsDOB reportDetailsDOB = null;
      ArrayList        dataList         = null;
      OperationsImpl   oimp             = null;
      ArrayList        mainDataList     = null;
      ArrayList        pageIterList     = new ArrayList();
      
      long             expiryinDays       =   0;
      Timestamp        validuptodate      =   null;
      Timestamp        currentdate        =   null;
      Timestamp        effectivefromdate  =   null;
      String           fromDate           =   null;
      String           toDate             =   null;
      try
      {
        oimp        = new OperationsImpl();
        cconnection = oimp.getConnection();
        dataList          =   new ArrayList();
        mainDataList      =   new ArrayList();
        pageIterList      =   new ArrayList();
        if(reportsEnterIdDOB.getFromDate()!=null && reportsEnterIdDOB.getFromDate().length()>0)
          fromDate          =   reportsEnterIdDOB.getFromDate().replaceAll(",","~");
        if(reportsEnterIdDOB.getToDate()!=null && reportsEnterIdDOB.getToDate().length()>0)
          toDate            =   reportsEnterIdDOB.getToDate().replaceAll(",","~");
          
        cstmt = cconnection.prepareCall("{call QMS_REPORT_PKG.QMS_PENDING_REPORT_EXCEL(?,?,?,?,?,?,?,?)}");
        
        cstmt.setString(1,reportsEnterIdDOB.getShipmentMode());
        cstmt.setString(2,reportsEnterIdDOB.getConsoleType());
        cstmt.setString(3,reportsEnterIdDOB.getPrimaryOption());
        cstmt.setString(4,fromDate);
        cstmt.setString(5,toDate);
        cstmt.setString(6,reportsEnterIdDOB.getTerminalId());
        cstmt.setString(7,reportsEnterIdDOB.getUserDateFormat());    
        cstmt.registerOutParameter(8, OracleTypes.CURSOR);
        cstmt.execute();  
        
        
        rs = (ResultSet)cstmt.getObject(8);
        
        while(rs.next())
          {
              reportDetailsDOB = new ReportDetailsDOB();
              
              reportDetailsDOB.setQuoteId(rs.getString("QUOTE_ID"));
              reportDetailsDOB.setCreateDate(rs.getTimestamp("CREATED_DATE"));//@@Added by kameswari for the WPBN issue-61310
              reportDetailsDOB.setImportant(rs.getString("IU_FLAG"));
              reportDetailsDOB.setCustomerId(rs.getString("CUSTOMER_ID"));
              reportDetailsDOB.setCustomerName(rs.getString("COMPANYNAME"));//@@added by kameswari for the WPBN issue-30313
              reportDetailsDOB.setServiceLevel(rs.getString("SERVICE_LEVEL_ID"));
              reportDetailsDOB.setFromCountry(rs.getString("org_country"));
              reportDetailsDOB.setFromLocation(rs.getString("ORIGIN_LOCATION"));
              reportDetailsDOB.setToLocation(rs.getString("DEST_LOCATION"));
              reportDetailsDOB.setToCountry(rs.getString("dest_country"));
              
              
              dataList.add(reportDetailsDOB);
         
         }
           mainDataList.add(pageIterList);
           mainDataList.add(dataList);           
      }
      catch(SQLException e) 
      {
        e.printStackTrace();
        //Logger.error(FILE_NAME,"SQLException in getPendingQuoteReportDataForExcl( ) method"+e.toString());
        logger.error(FILE_NAME+"SQLException in getPendingQuoteReportDataForExcl( ) method"+e.toString());
        throw new EJBException();        
      }
      catch(EJBException e)
      {
        e.printStackTrace();
        //Logger.error(FILE_NAME,"EJBException in getPendingQuoteReportDataForExcl() method"+e.toString());
        logger.error(FILE_NAME+"EJBException in getPendingQuoteReportDataForExcl() method"+e.toString());
        throw new EJBException();         
      }catch(Exception e)
      {
      e.printStackTrace();
        //Logger.error(FILE_NAME,"Exception in getPendingQuoteReportDataForExcl() method"+e.toString());
        logger.error(FILE_NAME+"Exception in getPendingQuoteReportDataForExcl() method"+e.toString());
        throw new EJBException();         
      }finally
      {
        try
        {
          if(rs!=null)
          rs.close();
          if(cstmt!=null)
          { cstmt.close();}
          if(cconnection!=null)
          { cconnection.close();}
        }catch(SQLException e)
        {
          //Logger.error(FILE_NAME,"SQLException in getPendingQuoteReportDataForExcl( ) method"+e.toString());
          logger.error(FILE_NAME+"SQLException in getPendingQuoteReportDataForExcl( ) method"+e.toString());
          throw new EJBException();        
        }
      }
       return mainDataList;
    }
  /**
   * This updateQuoteExpiryReportData() updates the quote status
   * @throws javax.ejb.EJBException
   * @return 
   * @param updatedDataList   
   */
  public String updatePendingQuoteReportData(ArrayList updatedDataList)throws EJBException
    {
      Connection  connection            = null;
      PreparedStatement  pstmt          = null;
      OperationsImpl     oimpl          = null;
      ReportDetailsDOB reportDetailsDOB = null;
      String              updated       = null;
      int                 modify        = 0;     
      Timestamp        currentdate      = null;
      DataSource  dataSource            = null;
      OperationsImpl   oimp             = null;
      String             userid         = "";
      int             updatesize        = 0;
      ArrayList       tempList          = null;
    
      try
      {
        oimpl            = new OperationsImpl();
        connection       = oimpl.getConnection();
        
        String currdate  = new  com.foursoft.esupply.common.util.ESupplyDateUtility().getCurrentDateStringWithTime("DD/MM/YY");
        currentdate      = new ESupplyDateUtility().getTimestamp("DD/MM/YY",currdate);
        String updateqry=" UPDATE QMS_QUOTE_MASTER SET QUOTE_STATUS =?,PN_FLAG=?, ACTIVE_FLAG=?,MODIFIED_BY=?,STATUS_REASON=? " +
        				 " WHERE QUOTE_ID=? AND VERSION_NO=(SELECT MAX(VERSION_NO) FROM QMS_QUOTE_MASTER WHERE QUOTE_ID=?) "; //Added by Kishore For statusReason
        pstmt=connection.prepareStatement(updateqry);
        if(updatedDataList!=null && updatedDataList.size()>0)
        {
        updatesize=updatedDataList.size();
           for(int j=0;j<updatesize;j++)
           {
           reportDetailsDOB=(ReportDetailsDOB)updatedDataList.get(j);
            if(reportDetailsDOB.getQuoteStatus()!=null && (reportDetailsDOB.getQuoteStatus()).equalsIgnoreCase("APP"))
                  {
                  pstmt.setString(1,"ACC");
                  pstmt.setString(2,"P");
                  pstmt.setString(3,"A");
                  }
            else
                  {
                  pstmt.setString(1,"NAC");
                  pstmt.setString(2,"N");
                  pstmt.setString(3,"I");
                  }
            pstmt.setString(4,reportDetailsDOB.getUserId());
           // pstmt.setTimestamp(5,currentdate);
            pstmt.setString(5,reportDetailsDOB.getStatusReason()); //Added by Kishore For statusReason
            pstmt.setString(6,reportDetailsDOB.getQuoteId());
            pstmt.setString(7,reportDetailsDOB.getQuoteId());
            modify=pstmt.executeUpdate();    
           }
           
        }
        if(modify==0)
            updated =null;
        else
            updated="modified";        
      }
      catch(SQLException e)
      {
        e.printStackTrace();
        //Logger.error(FILE_NAME,"SQLException in updatePendingQuoteReportData( ) method"+e.toString());
        logger.error(FILE_NAME+"SQLException in updatePendingQuoteReportData( ) method"+e.toString());
        throw new EJBException();        
      }catch(EJBException e)
      {
      e.printStackTrace();
        //Logger.error(FILE_NAME,"EJBException in updatePendingQuoteReportData() method"+e.toString());
        logger.error(FILE_NAME+"EJBException in updatePendingQuoteReportData() method"+e.toString());
        throw new EJBException();         
      }catch(Exception e)
      {
      e.printStackTrace();
        //Logger.error(FILE_NAME,"Exception in updatePendingQuoteReportData() method"+e.toString());
        logger.error(FILE_NAME+"Exception in updatePendingQuoteReportData() method"+e.toString());
        throw new EJBException();         
      }finally
      {
        try
        {
          
          if(pstmt!=null)
          { pstmt.close();}
          if(connection!=null)
          { connection.close();}
        }catch(SQLException e)
        {
          //Logger.error(FILE_NAME,"SQLException in updatePendingQuoteReportData( ) method"+e.toString());
          logger.error(FILE_NAME+"SQLException in updatePendingQuoteReportData( ) method"+e.toString());
          throw new EJBException();        
        }
      }
      return updated;
    }
    
    public ArrayList getUpdatedQuotes(String terminalId,String userId,String empId) throws SQLException
    {
      Connection          connection            = null;
      CallableStatement   csmt                  = null;
      ResultSet           rs                    = null;
      ArrayList           list                  = null;
      UpdatedQuotesDOB    updatedQuotes         = null;
      
      try
      {
        connection      = getConnection();
        list            = new ArrayList();
        
        
        csmt        = connection.prepareCall("{call QMS_QUOTE_PACK.qms_updated_quotes_count(?,?,?,?)}");
        csmt.setString(1,terminalId);
        csmt.setString(2,userId);
        csmt.setString(3,empId);
        csmt.registerOutParameter(4,OracleTypes.CURSOR);
        
        csmt.execute();
        
        rs    =   (ResultSet)csmt.getObject(4);
        
        while(rs.next())
        {
          updatedQuotes   = new UpdatedQuotesDOB();
          updatedQuotes.setChangeDesc(rs.getString("CHANGEDESC"));
          updatedQuotes.setUpdatedQuotes(rs.getInt("UPDATED_QUOTES"));
          updatedQuotes.setConfirmedQuotes(rs.getInt("CONFIRMED_QUOTES"));
          updatedQuotes.setUnconfirmedQuotes(rs.getInt("UPDATED_QUOTES")-rs.getInt("CONFIRMED_QUOTES"));
          list.add(updatedQuotes);
        }
      }
      catch(SQLException sql)
      {
        //Logger.error(FILE_NAME,"SQL Exception in getUpdatedQuotes::"+sql);
        logger.error(FILE_NAME+"SQL Exception in getUpdatedQuotes::"+sql);
        sql.printStackTrace();
        throw new SQLException(sql.toString(),sql.getSQLState(),sql.getErrorCode());
      }
      catch(Exception e)
      {
        //Logger.error(FILE_NAME,"Exception in getUpdatedQuotes::"+e);
        logger.error(FILE_NAME+"Exception in getUpdatedQuotes::"+e);
        e.printStackTrace();
        throw new SQLException(e.getMessage());
      }
      finally
      {
        if(rs!=null)
          rs.close();
        if(csmt!=null)
          csmt.close();
        if(connection!=null)
          connection.close();
      }
      return list;
    }
    
    public ArrayList getUpdatedQuotesReportDetails(String changeDesc,int pageNo,String sortBy,String sortOrder,String terminalId,String userId,String empId) throws SQLException
    {
      Connection              connection            = null;
      CallableStatement       csmt                  = null;
      ResultSet               rs                    = null;
      ArrayList               list                  = null;
      ArrayList               finalList             = null;
      UpdatedQuotesReportDOB  reportDOB             = null;
      
      String                  shipMode              = null;
      
      try
      {
        connection      = getConnection();
        list            = new ArrayList();
        finalList       = new ArrayList();
        String faxEmailPrint         = "";//Included by Shyam for DHL
        
        csmt        = connection.prepareCall("{call QMS_QUOTE_PACK.qms_updated_quotes_info(?,?,?,?,?,?,?,?,?,?,?)}");
        csmt.setString(1,changeDesc);
        csmt.setString(2,terminalId);
        csmt.setInt(3,pageNo);
        //csmt.setInt(4,10);
        csmt.setInt(4,100);//@@Commented and Modified by Kameswari for the WPBN issue-178973 on 12/08/09
        csmt.setString(5,sortBy);
        csmt.setString(6,sortOrder);
        csmt.setString(7,userId);
        csmt.setString(8,empId);
        csmt.registerOutParameter(9,OracleTypes.NUMBER);
        csmt.registerOutParameter(10,OracleTypes.NUMBER);
        csmt.registerOutParameter(11,OracleTypes.CURSOR);
        
        csmt.execute();
        if(csmt.getInt(9) != 0)
         finalList.add(new Integer(csmt.getInt(9)));
        else
         finalList.add(new Integer(0));
        if(csmt.getInt(10) != 0)
         finalList.add(new Integer(csmt.getInt(10)));
        else
         finalList.add(new Integer(0));
         
        rs    =   (ResultSet)csmt.getObject(11);
        
        while(rs.next())
        {
		  
          reportDOB   =   new UpdatedQuotesReportDOB();
          reportDOB.setImpFlag(rs.getString("IU_FLAG"));
          reportDOB.setCustomerId(rs.getString("CUSTOMER_ID"));
          reportDOB.setCustomerName(rs.getString("COMPANYNAME"));//@@added by kameswari for the WPBN issue-30313
          //reportDOB.setQuoteId(rs.getLong("QUOTE_ID"));//@@ commented by subrahmanyam for the enhancement #146971 on 03/12/08
          reportDOB.setQuoteId(rs.getString("QUOTE_ID"));//@@ Added by subrahmanyam for the enhancement #146971 on 03/12/08
          reportDOB.setUniqueId(rs.getLong("ID"));
          
          if(rs.getInt("SMODE")==1)
            shipMode  = "Air";
          else if(rs.getInt("SMODE")==2)
            shipMode  = "Sea";
          else if(rs.getInt("SMODE")==4)
            shipMode  = "Truck";
          else if(rs.getInt("SMODE")==100)//@@Returns 100 if Shipment is Multi-Modal.
            shipMode  = "Multi-Modal";
            
          reportDOB.setShipmentMode(shipMode);
          reportDOB.setServiceLevel(rs.getString("SERVICE"));
          reportDOB.setOriginLocation(rs.getString("ORIGIN_LOCATION"));
          reportDOB.setOriginCountry(rs.getString("ORIGIN_COUNTRY"));
          reportDOB.setDestLocation(rs.getString("DEST_LOCATION"));
          reportDOB.setDestCountry(rs.getString("DEST_COUNTRY"));
          reportDOB.setSellBuyFlag(rs.getString("SELL_BUY_FLAG"));
          reportDOB.setChangeDesc(changeDesc);
          reportDOB.setIsMultiQuote(rs.getString("IS_MULTI_QUOTE"));//Added by Anil.k for CR 231104 on 31Jan2011
          
		 //Included by Shyam for DHL starts here
		  faxEmailPrint	=	"";

          if("Y".equalsIgnoreCase(rs.getString("EMAIL_FLAG")))
              faxEmailPrint	=	"EMAIL";
              
          if("Y".equalsIgnoreCase(rs.getString("FAX_FLAG")))
            {
              if(faxEmailPrint.equals("") || faxEmailPrint==null)
                faxEmailPrint	=	"FAX";
              else
                faxEmailPrint	=	faxEmailPrint+",FAX";
            }
            
          if("Y".equalsIgnoreCase(rs.getString("PRINT_FLAG")))
            {
                if(faxEmailPrint.equals("") || faxEmailPrint==null)
                faxEmailPrint	=	"PRINT";
              else
                faxEmailPrint	=	faxEmailPrint+",PRINT";
            }
           
          reportDOB.setFaxEmailPrintFlag(faxEmailPrint);
		 //Included by Shyam for DHL ends here

		  list.add(reportDOB);
        }
        finalList.add(list);
      }
      catch(SQLException sql)
      {
        //Logger.error(FILE_NAME,"SQL Exception in getUpdatedQuotesReportDetails::"+sql);
        logger.error(FILE_NAME+"SQL Exception in getUpdatedQuotesReportDetails::"+sql);
        sql.printStackTrace();
        throw new SQLException(sql.toString(),sql.getSQLState(),sql.getErrorCode());
      }
      catch(Exception e)
      {
        //Logger.error(FILE_NAME,"Exception in getUpdatedQuotesReportDetails::"+e);
        logger.error(FILE_NAME+"Exception in getUpdatedQuotesReportDetails::"+e);
        e.printStackTrace();
        throw new SQLException(e.getMessage());
      }
      finally
      {
        if(rs!=null)
          rs.close();
        if(csmt!=null)
          csmt.close();
        if(connection!=null)
          connection.close();
      }
      return finalList;
    }
     public ArrayList getUpdatedQuotesReportDetailsExcl(String changeDesc,int pageNo,String sortBy,String sortOrder,String terminalId,String userId,String empId) throws SQLException
    {
      Connection              connection            = null;
      CallableStatement       csmt                  = null;
      ResultSet               rs                    = null;
      ArrayList               list                  = null;
      ArrayList               finalList             = null;
      UpdatedQuotesReportDOB  reportDOB             = null;
      String                  shipMode              = null;
      try
      {
        connection      = getConnection();
        list            = new ArrayList();
        finalList       = new ArrayList();
        String faxEmailPrint         = "";//Included by Shyam for DHL

        csmt        = connection.prepareCall("{call QMS_QUOTE_PACK.qms_updated_quotes_info_Excel(?,?,?,?,?)}");
        csmt.setString(1,changeDesc);
        csmt.setString(2,terminalId);
        csmt.setString(3,userId);
        csmt.setString(4,empId);
        //csmt.setInt(3,pageNo);
        //csmt.setInt(4,10);
        //csmt.setString(5,sortBy);
        //csmt.setString(6,sortOrder);
        //csmt.registerOutParameter(7,OracleTypes.NUMBER);
        //csmt.registerOutParameter(8,OracleTypes.NUMBER);
        csmt.registerOutParameter(5,OracleTypes.CURSOR);
        
        csmt.execute();
        finalList.add(new Integer(0));
        finalList.add(new Integer(0));
         
        rs    =   (ResultSet)csmt.getObject(5);
        
        while(rs.next())
        {
          reportDOB   =   new UpdatedQuotesReportDOB();
          reportDOB.setImpFlag(rs.getString("IU_FLAG"));
          reportDOB.setCustomerId(rs.getString("CUSTOMER_ID"));
          reportDOB.setCustomerName(rs.getString("COMPANYNAME"));//@@added by kameswari for the WPBN issue-30313
          //reportDOB.setQuoteId(rs.getLong("QUOTE_ID"));  //Commented by subrahmanyam for the enhancement #146971 on 03/12/08
          reportDOB.setQuoteId(rs.getString("QUOTE_ID"));  //Added by subrahmanyam for the enhancement #146971 on 03/12/08
          reportDOB.setUniqueId(rs.getLong("ID"));
          
          if(rs.getInt("SMODE")==1)
            shipMode  = "Air";
          else if(rs.getInt("SMODE")==2)
            shipMode  = "Sea";
          else if(rs.getInt("SMODE")==4)
            shipMode  = "Truck";
          else if(rs.getInt("SMODE")==100)//@@Returns 100 if Shipment is Multi-Modal.
            shipMode  = "Multi-Modal";
            
          reportDOB.setShipmentMode(shipMode);
          reportDOB.setServiceLevel(rs.getString("SERVICE"));
          reportDOB.setOriginLocation(rs.getString("ORIGIN_LOCATION"));
          reportDOB.setOriginCountry(rs.getString("ORIGIN_COUNTRY"));
          reportDOB.setDestLocation(rs.getString("DEST_LOCATION"));
          reportDOB.setDestCountry(rs.getString("DEST_COUNTRY"));
          reportDOB.setSellBuyFlag(rs.getString("SELL_BUY_FLAG"));
          reportDOB.setChangeDesc(changeDesc);
          reportDOB.setCreatedBy(rs.getString("CREATED_BY"));//Added by Anil.k for Excel Page
          //reportDOB.setCreatedDate(rs.getTimestamp(("CREATED_DATE")));
          reportDOB.setCreatedDateStr(rs.getString(("CREATED_DATE")));
          
          //System.out.println("createddate-----------"+rs.getTimestamp("CREATED_DATE"));
          //Added by Anil.k for Excel Page
          reportDOB.setSalesPerson(rs.getString("SALES_PERSON"));//Added by Anil.k for Excel Page
          
		 //Included by Shyam for DHL starts here
		  faxEmailPrint	=	"";

          if("Y".equalsIgnoreCase(rs.getString("EMAIL_FLAG")))
              faxEmailPrint	=	"EMAIL";
              
          if("Y".equalsIgnoreCase(rs.getString("FAX_FLAG")))
            {
              if(faxEmailPrint.equals("") || faxEmailPrint==null)
                faxEmailPrint	=	"FAX";
              else
                faxEmailPrint	=	faxEmailPrint+",FAX";
            }
            
          if("Y".equalsIgnoreCase(rs.getString("PRINT_FLAG")))
            {
                if(faxEmailPrint.equals("") || faxEmailPrint==null)
                faxEmailPrint	=	"PRINT";
              else
                faxEmailPrint	=	faxEmailPrint+",PRINT";
            }
           
          reportDOB.setFaxEmailPrintFlag(faxEmailPrint);
		 //Included by Shyam for DHL ends here

          list.add(reportDOB);
        }
        finalList.add(list);
      }
      catch(SQLException sql)
      {
        //Logger.error(FILE_NAME,"SQL Exception in getUpdatedQuotesReportDetailsExcl::"+sql);
        logger.error(FILE_NAME+"SQL Exception in getUpdatedQuotesReportDetailsExcl::"+sql);
        sql.printStackTrace();
        throw new SQLException(sql.toString(),sql.getSQLState(),sql.getErrorCode());
      }
      catch(Exception e)
      {
        //Logger.error(FILE_NAME,"Exception in getUpdatedQuotesReportDetailsExcl::"+e);
        logger.error(FILE_NAME+"Exception in getUpdatedQuotesReportDetailsExcl::"+e);
        e.printStackTrace();
        throw new SQLException(e.getMessage());
      }
      finally
      {
        if(rs!=null)
          rs.close();
        if(csmt!=null)
          csmt.close();
        if(connection!=null)
          connection.close();
      }
      return finalList;
    }
    protected Connection getConnection() throws SQLException
    {         
      Connection    connection  = null;
      try 
      {
           connection = dataSource.getConnection();
      } 
      catch (SQLException se) 
      {
        //Logger.error(FILE_NAME,"Error while getting Connection:"+se.toString());
        logger.error(FILE_NAME+"Error while getting Connection:"+se.toString());
        se.printStackTrace();
        throw new SQLException(se.toString(),se.getSQLState(),se.getErrorCode());
      }
      catch (Exception e) 
      {
        //Logger.error(FILE_NAME,"Error while getting Connection:"+e.toString());
        logger.error(FILE_NAME+"Error while getting Connection:"+e.toString());
        e.printStackTrace();
        throw new SQLException(e.toString());
      }	   
      return connection;
    }
 public HashMap getActivitySummaryReportDetailsData(ESupplyGlobalParameters loginbean)throws EJBException
    {
      Connection          connection      = null;
      ResultSet           rs              = null;
      OperationsImpl      oimp            = null;
      PreparedStatement   pStmt           = null;
      HashMap             summaryDetails  = new HashMap();
      ESupplyDateUtility  dateFormatter   = new ESupplyDateUtility();
      Timestamp           currDate        = new java.sql.Timestamp((new java.util.Date()).getTime());
      String[]            currDateString  = null;
      //String              currTimeString  = null;
      String              terminalQry     = "";
	  
      try
      {
        oimp=new OperationsImpl();
        dateFormatter.setPatternWithTime(loginbean.getUserPreferences().getDateFormat());
        currDateString  = dateFormatter.getDisplayStringArray(currDate);
        connection  = oimp.getConnection();
         if(loginbean.getAccessType().equalsIgnoreCase("HO_TERMINAL"))
            terminalQry = "SELECT DISTINCT terminalid term_id FROM fs_fr_terminalmaster";
        else
            terminalQry = "SELECT '"+loginbean.getTerminalId()+"' FROM DUAL  UNION "+
                          " SELECT  child_terminal_id term_id FROM fs_fr_terminal_regn "+
                          " CONNECT BY PRIOR child_terminal_id = parent_terminal_id START WITH parent_terminal_id = '"+loginbean.getTerminalId()+"'";
       /*   pStmt = connection.prepareStatement("SELECT COUNT(*) PENDING_QUOTES FROM QMS_QUOTE_MASTER WHERE ACTIVE_FLAG='A' AND QUOTE_STATUS='PEN' AND TERMINAL_ID IN ("+terminalQry+") AND "+
                                            "(SALES_PERSON=(SELECT EMPID FROM FS_USERMASTER WHERE USERID=? AND LOCATIONID=?) OR (CREATED_BY=? AND TERMINAL_ID=?))");*/
                                          //@@Modified for the WPBN issue-125496
                                            
                                            
                                            
                                            
                                            
       /* pStmt = connection.prepareStatement("SELECT COUNT(*) PENDING_QUOTES FROM QMS_QUOTE_MASTER WHERE ACTIVE_FLAG='A' AND QUOTE_STATUS='PEN' AND ID NOT IN (SELECT DISTINCT QUOTE_ID FROM QMS_QUOTES_UPDATED) AND TERMINAL_ID IN ("+terminalQry+") AND "+
                                            "(SALES_PERSON=(SELECT EMPID FROM FS_USERMASTER WHERE USERID=? AND LOCATIONID=?) OR (CREATED_BY=? AND TERMINAL_ID=?))");*/
         //Modified by Anil.k on 10-JAN-2011
         /*pStmt = connection.prepareStatement("SELECT COUNT(*) PENDING_QUOTES FROM QMS_QUOTE_MASTER WHERE ACTIVE_FLAG='A' AND QUOTE_STATUS='PEN' AND ID NOT IN (SELECT DISTINCT QUOTE_ID FROM QMS_QUOTES_UPDATED WHERE CONFIRM_FLAG IS NULL) AND "+
                                            "(SALES_PERSON=(SELECT EMPID FROM FS_USERMASTER WHERE USERID=? AND LOCATIONID=?) OR (CREATED_BY=? AND TERMINAL_ID=?))");*/
         pStmt = connection.prepareStatement(" SELECT COUNT(*) PENDING_QUOTES FROM (SELECT  DISTINCT QUOTE_ID FROM QMS_QUOTE_MASTER WHERE ACTIVE_FLAG='A' AND QUOTE_STATUS='PEN' AND ID NOT IN (SELECT DISTINCT QUOTE_ID FROM QMS_QUOTES_UPDATED WHERE CONFIRM_FLAG IS NULL) AND "+
         "(SALES_PERSON=(SELECT EMPID FROM FS_USERMASTER WHERE USERID=? AND LOCATIONID=?) OR (CREATED_BY=? AND TERMINAL_ID=?)) AND VALID_TO IS NOT NULL AND VALID_TO >= TO_DATE(?,?))");                                   
        //Ended by Anil.k                            
        pStmt.clearParameters();
        pStmt.setString(1,loginbean.getUserId());
        pStmt.setString(2,loginbean.getTerminalId());
        pStmt.setString(3,loginbean.getUserId());
        pStmt.setString(4,loginbean.getTerminalId());
        pStmt.setString(5,currDateString[0]+" "+currDateString[1]); //@@Modified by kiran.v for expire quotes not appear in pending report.
        pStmt.setString(6,loginbean.getUserPreferences().getDateFormat()+" HH24:MI");
         rs=pStmt.executeQuery();       
        if(rs.next())
          summaryDetails.put("pending",rs.getString("PENDING_QUOTES"));               
        else
          summaryDetails.put("pending","0");               
        
        if(rs!=null)
            rs.close();
        if(pStmt!=null)
            pStmt.close();
      //Modified by Anil.k on 10-JAN-2011
        //pStmt = connection.prepareStatement("SELECT COUNT(*) SELF_APPROVAL FROM QMS_QUOTE_MASTER WHERE ACTIVE_FLAG='A' AND ESCALATION_FLAG='Y' AND ESCALATED_TO=(SELECT EMPID FROM FS_USERMASTER WHERE USERID=? AND LOCATIONID=?)");
        pStmt = connection.prepareStatement("SELECT COUNT(*) SELF_APPROVAL FROM (SELECT DISTINCT QUOTE_ID FROM QMS_QUOTE_MASTER WHERE ACTIVE_FLAG='A' AND ESCALATION_FLAG='Y' AND ESCALATED_TO=(SELECT EMPID FROM FS_USERMASTER WHERE USERID=? AND LOCATIONID=?))");
        //Ended by Anil.k
        pStmt.clearParameters();
        pStmt.setString(1,loginbean.getUserId());
        pStmt.setString(2,loginbean.getTerminalId());
        rs=pStmt.executeQuery();
       
       if(rs.next())
          summaryDetails.put("self",rs.getString("SELF_APPROVAL"));               
        else
          summaryDetails.put("self","0"); 
         
        if(rs!=null)
            rs.close();
        if(pStmt!=null)
            pStmt.close();
        //Modified by Anil.k on 10-JAN-2011   
        /*pStmt = connection.prepareStatement("SELECT COUNT(*) OTHERS_APPROVAL FROM QMS_QUOTE_MASTER WHERE ACTIVE_FLAG='A' AND ESCALATION_FLAG='Y' AND "+
        "(SALES_PERSON=(SELECT EMPID FROM FS_USERMASTER WHERE USERID=? AND LOCATIONID=?) OR (CREATED_BY=? AND TERMINAL_ID=?))");*/
        pStmt = connection.prepareStatement("SELECT COUNT(*) OTHERS_APPROVAL FROM (SELECT DISTINCT QUOTE_ID FROM QMS_QUOTE_MASTER WHERE ACTIVE_FLAG='A' AND ESCALATION_FLAG='Y' AND "+
                                            "(SALES_PERSON=(SELECT EMPID FROM FS_USERMASTER WHERE USERID=? AND LOCATIONID=?) OR (CREATED_BY=? AND TERMINAL_ID=?)))");
        //Ended by Anil.k
        pStmt.clearParameters();
        pStmt.setString(1,loginbean.getUserId());
        pStmt.setString(2,loginbean.getTerminalId());
        pStmt.setString(3,loginbean.getUserId());
        pStmt.setString(4,loginbean.getTerminalId());
        rs=pStmt.executeQuery();
       
        if(rs.next())
          summaryDetails.put("others",rs.getString("OTHERS_APPROVAL"));               
        else
          summaryDetails.put("others","0"); 
          
          
        if(rs!=null)
            rs.close();
        if(pStmt!=null)
            pStmt.close();
        //Modified by Anil.k on 10-JAN-2011
        /*pStmt = connection.prepareStatement("SELECT COUNT(*) APPROVED_QUOTES_NOTSENT FROM QMS_QUOTE_MASTER WHERE ACTIVE_FLAG='A' AND QUOTE_STATUS='APP' AND SENT_FLAG='U' AND "+
                                            "(SALES_PERSON=(SELECT EMPID FROM FS_USERMASTER WHERE USERID=? AND LOCATIONID=?) OR (CREATED_BY=? AND TERMINAL_ID=?))");*/
        pStmt = connection.prepareStatement("SELECT COUNT(*) APPROVED_QUOTES_NOTSENT FROM (SELECT DISTINCT QUOTE_ID FROM QMS_QUOTE_MASTER WHERE ACTIVE_FLAG='A' AND QUOTE_STATUS='APP' AND SENT_FLAG='U' AND "+
        "(SALES_PERSON=(SELECT EMPID FROM FS_USERMASTER WHERE USERID=? AND LOCATIONID=?) OR (CREATED_BY=? AND TERMINAL_ID=?)))");
        //Ended by Anil.k
        pStmt.clearParameters();
        pStmt.setString(1,loginbean.getUserId());
        pStmt.setString(2,loginbean.getTerminalId());
        pStmt.setString(3,loginbean.getUserId());
        pStmt.setString(4,loginbean.getTerminalId());
        rs=pStmt.executeQuery();
       
        if(rs.next())
          summaryDetails.put("approved",rs.getString("APPROVED_QUOTES_NOTSENT"));               
        else
          summaryDetails.put("approved","0");   
     
        if(rs!=null)
            rs.close();
        if(pStmt!=null)
            pStmt.close();
      //Modified by Anil.k on 10-JAN-2011
        /*pStmt = connection.prepareStatement("SELECT COUNT(*) REJECTED_QUOTES FROM QMS_QUOTE_MASTER WHERE ACTIVE_FLAG = 'A' AND QUOTE_STATUS = 'REJ' AND "
                                            +"(SALES_PERSON = ? OR (CREATED_BY = ? AND TERMINAL_ID = ?))");*/
        pStmt = connection.prepareStatement("SELECT COUNT(*) REJECTED_QUOTES FROM (SELECT DISTINCT QUOTE_ID FROM QMS_QUOTE_MASTER WHERE ACTIVE_FLAG = 'A' AND QUOTE_STATUS = 'REJ' AND "
                +"(SALES_PERSON = ? OR (CREATED_BY = ? AND TERMINAL_ID = ?)))");
        //Ended by Anil.k
                                            
        pStmt.setString(1,loginbean.getEmpId()); 
        pStmt.setString(2,loginbean.getUserId());
        pStmt.setString(3,loginbean.getTerminalId());
        
        rs=pStmt.executeQuery();
        
        if(rs.next())
          summaryDetails.put("rejected",rs.getString("REJECTED_QUOTES"));               
        else
          summaryDetails.put("rejected","0"); 
     
        if(rs!=null)
            rs.close();
        if(pStmt!=null)
            pStmt.close();
      //Modified by Anil.k on 10-JAN-2011
        /*pStmt = connection.prepareStatement("SELECT COUNT(*) EXPIRED_NOTUPDATED FROM QMS_QUOTE_MASTER WHERE ACTIVE_FLAG='A' AND COMPLETE_FLAG='C' AND VALID_TO IS NOT NULL AND VALID_TO < TO_DATE(?,?)  AND "+
                                            "(SALES_PERSON=(SELECT EMPID FROM FS_USERMASTER WHERE USERID=? AND LOCATIONID=?) OR (CREATED_BY=? AND TERMINAL_ID=?))");*/
        pStmt = connection.prepareStatement("SELECT COUNT(*) EXPIRED_NOTUPDATED FROM (SELECT DISTINCT QUOTE_ID FROM QMS_QUOTE_MASTER WHERE ACTIVE_FLAG='A' AND COMPLETE_FLAG='C' AND VALID_TO IS NOT NULL AND VALID_TO < TO_DATE(?,?)  AND "+
        "(SALES_PERSON=(SELECT EMPID FROM FS_USERMASTER WHERE USERID=? AND LOCATIONID=?) OR (CREATED_BY=? AND TERMINAL_ID=?)))");
        //Ended by Anil.k
		//Curr date included and AND SENT_FLAG='U' condition Removed  by shyam
           pStmt.clearParameters();
        pStmt.setString(1,currDateString[0]+" "+currDateString[1]);
        pStmt.setString(2,loginbean.getUserPreferences().getDateFormat()+" HH24:MI");
        pStmt.setString(3,loginbean.getUserId());
        pStmt.setString(4,loginbean.getTerminalId());
        pStmt.setString(5,loginbean.getUserId());
        pStmt.setString(6,loginbean.getTerminalId());
        rs=pStmt.executeQuery();
       
        if(rs.next())
          summaryDetails.put("expired",rs.getString("EXPIRED_NOTUPDATED"));               
        else
          summaryDetails.put("expired","0"); 
     
        if(rs!=null)
            rs.close();
        if(pStmt!=null)
            pStmt.close();
        
      /*     if(loginbean.getAccessType().equalsIgnoreCase("HO_TERMINAL"))
            terminalQry = "SELECT DISTINCT terminalid term_id FROM fs_fr_terminalmaster";
        else
            terminalQry = "SELECT '"+loginbean.getTerminalId()+"' FROM DUAL  UNION "+
                          " SELECT  child_terminal_id term_id FROM fs_fr_terminal_regn "+
                          " CONNECT BY PRIOR child_terminal_id = parent_terminal_id START WITH parent_terminal_id = '"+loginbean.getTerminalId()+"'";*/
        
        //@@Modified by Yuvraj for WPBN-DHLQMS-23408
        //pStmt = connection.prepareStatement("SELECT COUNT(*) UPDATEDQUOTES_NOTCONFIRMED FROM QMS_QUOTES_UPDATED qu, QMS_QUOTE_MASTER B WHERE B.ID=QU.QUOTE_ID and qu.confirm_flag is null and QU.QUOTE_ID IN (SELECT DISTINCT QU.QUOTE_ID FROM QMS_QUOTE_MASTER QM,QMS_QUOTES_UPDATED QU WHERE QU.QUOTE_ID=QM.ID AND QM.TERMINAL_ID IN ("+terminalQry+"))");
        pStmt = connection.prepareStatement("SELECT COUNT(*) UPDATEDQUOTES_NOTCONFIRMED FROM QMS_QUOTES_UPDATED qu, QMS_QUOTE_MASTER B WHERE B.ID=QU.QUOTE_ID AND QU.CONFIRM_FLAG IS NULL "+
                                            "AND QU.QUOTE_ID IN (SELECT QU.QUOTE_ID FROM QMS_QUOTE_MASTER QM,QMS_QUOTES_UPDATED QU WHERE QU.QUOTE_ID=QM.ID "+
                                            "AND ((QM.CREATED_BY = ? AND QM.TERMINAL_ID = ?) OR QM.SALES_PERSON = ?)) ");
        //@@        
        pStmt.clearParameters();
        pStmt.setString(1,loginbean.getUserId());
        pStmt.setString(2,loginbean.getTerminalId());
        pStmt.setString(3,loginbean.getEmpId());
        rs=pStmt.executeQuery();
       
        if(rs.next())
          summaryDetails.put("updated",rs.getString("UPDATEDQUOTES_NOTCONFIRMED"));               
        else
          summaryDetails.put("updated","0");
          
     }
      catch(SQLException e)
      {
        e.printStackTrace();
        //Logger.error(FILE_NAME,"SQLException in getActivitySummaryReportDetailsData( ) method"+e.toString());
        logger.error(FILE_NAME+"SQLException in getActivitySummaryReportDetailsData( ) method"+e.toString());
        throw new EJBException();        
      }catch(EJBException e)
      {
      e.printStackTrace();
        //Logger.error(FILE_NAME,"EJBException in getActivitySummaryReportDetailsData() method"+e.toString());
        logger.error(FILE_NAME+"EJBException in getActivitySummaryReportDetailsData() method"+e.toString());
        throw new EJBException();         
      }catch(Exception e)
      {
      e.printStackTrace();
        //Logger.error(FILE_NAME,"Exception in getActivitySummaryReportDetailsData() method"+e.toString());
        logger.error(FILE_NAME+"Exception in getActivitySummaryReportDetailsData() method"+e.toString());
        throw new EJBException();         
      }finally
      {
        try
        {
          if(rs!=null)
            rs.close();
          if(pStmt!=null)
          { pStmt.close();}
          if(connection!=null)
          { connection.close();}
        }catch(SQLException e) 
        {
          //Logger.error(FILE_NAME,"SQLException in getActivitySummaryReportDetailsData( ) method"+e.toString());
          logger.error(FILE_NAME+"SQLException in getActivitySummaryReportDetailsData( ) method"+e.toString());
          throw new EJBException();        
        }
      }
       return summaryDetails;
    }
 public HashMap getGroupingExcelDetails(HashMap<String,String> paramsMap,ESupplyGlobalParameters loginbean)
 {
	 //Modified By Kishore Podili For QuoteGroupExcel Report with Charges on 03-Feb-11
	  String			  customerId		= null;
	  String			  originCountry		= null;
	  String			  originCity		= null;
	  String			  destCountry		= null;
	  String			  destCity			= null;
	  String			  shipmentMode		= null;
	  String			  consoleType		= null;
	  String			  spotRateFlag		= null;
	  String			  weightBreak		= null;
	  HashSet             Quote_id          = null; 
	  HashMap             Quote_idmAp       = new HashMap();
      ArrayList           ratesList         = null;
      ArrayList           totalList         = new ArrayList();
      Connection          connection        = null;
      ResultSet           rs                = null;
      OperationsImpl      oimp              = null;
      PreparedStatement   pStmt             = null;
      PreparedStatement   pStmt4             = null;
      PreparedStatement   pStmt1            = null;
      QMSRatesReportDOB   ratesDOB          = null;
      CallableStatement   cstmt             = null;
      ResultSet           rs1               = null; 
       ResultSet           rs4               = null; 
      ResultSet           rs2               = null; 
      ResultSet           rs3               = null; 
     String[]            rateList          = null; 
      ArrayList           wtbrkList        = null;
     // LinkedList           rateLinkedList    = null; 
      // LinkedList           wtbrkList          = null; 
      ArrayList           modeList          = new ArrayList();
      String              customerNameQuery = "SELECT COMPANYNAME FROM FS_FR_CUSTOMERMASTER WHERE CUSTOMERID=?";   
   //  String              ratesexcelquery   = "SELECT QUOTE_ID,ORIGIN_COUNTRY,ORIGIN_LOCATION,DESTI_COUNTRY,DEST_LOCATION,CARRIER_ID,TRANSIT_TIME,SELL_BUY_FLAG,FREQUENCY,CURRENCY,SHIPMENT_MODE,VALID_UPTO,CHARGE_RATE,WEIGHTBREAKSLAB,DENSITY_RATIO,DENSITY_TYPE ,SELL_BUY_FLAG,SERVICELEVEL FROM RATES_EXCEL WHERE WEIGHT_BREAK=? AND SHIPMENT_MODE=? ORDER BY QUOTE_ID,WEIGHTBREAKSLAB";   
// commented by subrahmanyam for 181328
      //String              ratesexcelquery   = "SELECT QUOTE_ID,ORIGIN_COUNTRY,ORIGIN_LOCATION,DESTI_COUNTRY,DEST_LOCATION,CARRIER_ID,TRANSIT_TIME,SELL_BUY_FLAG,FREQUENCY,CURRENCY,SHIPMENT_MODE,VALID_UPTO,CHARGE_RATE,WEIGHTBREAKSLAB,DENSITY_RATIO,DENSITY_TYPE ,SELL_BUY_FLAG,SERVICELEVEL FROM RATES_EXCEL WHERE WEIGHT_BREAK=? AND SHIPMENT_MODE=? ORDER BY QUOTE_ID,WEIGHTBREAKSLAB";   //@@Commented and Modified by Kameswari for the WPBN issue-178965 on 12/08/09
      //Modified by Mohan for Issue No.219976 on 08-10-2010
      //String              ratesexcelquery   = "SELECT DISTINCT QUOTE_ID,QUOTE_VALID_UPTO,ORIGIN_COUNTRY,ORIGIN_LOCATION,DESTI_COUNTRY,DEST_LOCATION,CARRIER_ID,TRANSIT_TIME,SELL_BUY_FLAG,FREQUENCY,CURRENCY,SHIPMENT_MODE,VALID_UPTO,CHARGE_RATE,WEIGHTBREAKSLAB,DENSITY_RATIO,DENSITY_TYPE ,SELL_BUY_FLAG,SERVICELEVEL,INTERNAL_NOTES,EXTERNAL_NOTES FROM RATES_EXCEL WHERE WEIGHT_BREAK=? AND SHIPMENT_MODE=? ORDER BY QUOTE_ID,WEIGHTBREAKSLAB";//added by subrahmanyam for 181328
      //@@Modified by Kameswari for the WPBN issue - 267260 on 25/08/2011	
      	String               ratesexcelquery  ="SELECT DISTINCT QUOTE_ID,QUOTE_VALID_UPTO, (SELECT COUNTRYNAME FROM FS_COUNTRYMASTER  WHERE COUNTRYID =ORIGIN_COUNTRY )ORIGIN_COUNTRY,"+
      	                                       "DECODE(LENGTH(ORIGIN_LOCATION),5,(SELECT fp.portname FROM fs_frs_portmaster fp WHERE portid =ORIGIN_LOCATION),3,(SELECT LOCATIONNAME FROM FS_FR_LOCATIONMASTER WHERE LOCATIONID = ORIGIN_LOCATION))ORIGIN_LOCATION,"+
      	                                       "(SELECT COUNTRYNAME FROM FS_COUNTRYMASTER  WHERE COUNTRYID =DESTI_COUNTRY )DESTI_COUNTRY,"+
      	                                       "DECODE(LENGTH(DEST_LOCATION),5,(SELECT fp.portname FROM fs_frs_portmaster fp WHERE portid =DEST_LOCATION),3,(SELECT LOCATIONNAME FROM FS_FR_LOCATIONMASTER WHERE LOCATIONID = DEST_LOCATION))DEST_LOCATION,CARRIER_ID,TRANSIT_TIME,SELL_BUY_FLAG,FREQUENCY,CURRENCY,SHIPMENT_MODE,VALID_UPTO,CHARGE_RATE,DENSITY_RATIO,DENSITY_TYPE,SELL_BUY_FLAG,WEIGHTBREAKSLAB,  sm.serviceleveldesc SERVICELEVEL,INTERNAL_NOTES,EXTERNAL_NOTES  FROM RATES_EXCEL re, FS_FR_SERVICELEVELMASTER sm WHERE WEIGHT_BREAK =? AND SHIPMENT_MODE =?  and re.servicelevel = sm.servicelevelid ORDER BY QUOTE_ID, WEIGHTBREAKSLAB";//Modified by kishore on 3-05-11
     // String              brkListQuery      = "SELECT WEIGHTBREAKSLAB,ROW_ID FROM RATES_HEADER_EXCEL WHERE  WEIGHTBREAK=? AND SHIPMENT_MODE=? ORDER BY ROW_ID";
        String              brkListQuery      =  "SELECT decode(surcharge_desc,'A FREIGHT RATE',WEIGHTBREAKSLAB,substr(surcharge_desc, 0, length(surcharge_desc) - 3)) || ' ' ||" +
										    	"       case (substr('WEIGHTBREAKSLAB', 6))" + 
										    	"        when  'BASIC'  then  'Basic' " + 
										    	"        when 'MIN'     then  'Minimum'" + 
										    	"        when 'FLAT'    then	'Flat'" + 
										    	"        when 'PERCENT' then 'Percent'" + 
										    	"        when 'ABSOLUTE' then 'Absolute'" + 
										        "         else" + 
										    	"           Upper(substr(WEIGHTBREAKSLAB, 6))" + 
										    	"       end WEIGHTBREAKSLAB," + 
										    	"       ROW_ID" + 
										    	"  FROM RATES_HEADER_EXCEL RE" + 
										    	" WHERE WEIGHTBREAK = ?" + 
										    	"   AND RE.SHIPMENT_MODE = ?" + 
										    	" ORDER BY ROW_ID";

; 
    	  //"SELECT WEIGHTBREAKSLAB,ROW_ID FROM RATES_HEADER_EXCEL WHERE  WEIGHTBREAK=? AND RE.SHIPMENT_MODE=? ORDER BY ROW_ID ";
      String              notesQuery        = null;
      String              accesslevelQuery  = "SELECT OPER_ADMIN_FLAG FROM FS_FR_TERMINALMASTER WHERE TERMINALID=?";
      String              wtBreak           = null;
      String              accessLevel       = null;
      String              mode              = null;
      String              terminalId        = null; 
      String              terminalQry       = null; 
     ArrayList           notes             = new ArrayList();
      ArrayList            shmode           = new ArrayList();
      String[]            notesArray         = null;
      int                 count             = 0;
      String             terminal           = null;
      int                  counttemp       = 0;
      
      //Added By Kishore Podili For QuoteGroupExcel Report with Charges on 08-Feb-11
      
      ArrayList<ArrayList>	returningList = null;
      ArrayList<QMSChargesReportDOB>	chargesList	 = new ArrayList<QMSChargesReportDOB>();
      QMSChargesReportDOB chargesDOB = null;
       
          
    //End Of Kishore Podili For QuoteGroupExcel Report with Charges on 08-Feb-11
      
   try  
   {
       oimp         = new OperationsImpl();
       
      
       //Added By Kishore Podili For QuoteGroupExcel Report with Charges on 03-Feb-11

         if(paramsMap!= null){
    	   customerId 			= paramsMap.get("customerId");
    	   originCountry		= paramsMap.get("originCountry");
    	   originCity			= paramsMap.get("originCity");
    	   destCountry			= paramsMap.get("destCountry");
    	   destCity				= paramsMap.get("destCity");
    	   shipmentMode			= paramsMap.get("shipmentMode");
    	   consoleType			= paramsMap.get("consoleType");
    	   spotRateFlag			= paramsMap.get("spotRateFlag");
    	   weightBreak			= paramsMap.get("weightBreak");
     	   
       }
       //End Of Kishore Podili For QuoteGroupExcel Report with Charges on 03-Feb-11
       
       
       
       
       terminalId   = loginbean.getTerminalId();
       connection   = oimp.getConnection();
       pStmt        = connection.prepareStatement(accesslevelQuery);
       pStmt.setString(1,terminalId);
       rs           = pStmt.executeQuery();
      
   
       while(rs.next())
       {
            accessLevel  = rs.getString("OPER_ADMIN_FLAG");
       }
        if(customerId!=null&&customerId.length()>3&&customerId.trim().length()>0&&"H".equalsIgnoreCase(accessLevel))
       {
          terminalId ="DHL"+customerId.substring(0,3);
         
       }
       if(rs!=null)
       {
         rs.close();
       }
       if(pStmt!=null)
       {
         pStmt.close();
        }
            terminalQry  = "SELECT PARENT_TERMINAL_ID FROM FS_FR_TERMINAL_REGN CONNECT BY CHILD_TERMINAL_ID = PRIOR PARENT_TERMINAL_ID START WITH CHILD_TERMINAL_ID = ? UNION SELECT ? FROM DUAL";
   
       notesQuery        = "SELECT DESCRIPTION ,SHIPMENTMODE ,CONTENTID FROM QMS_CONTENTDTLS WHERE TEMINALID IN ("+terminalQry+") AND SHIPMENTMODE IN (SELECT DISTINCT SHIPMENT_MODE FROM RATES_MODE) AND HEADERFOOTER='F' AND FLAG='T' ORDER BY SHIPMENTMODE,CONTENTID";
      
     
              pStmt1 = connection.prepareStatement(notesQuery);
           
              pStmt1.setString(1,terminalId);
              pStmt1.setString(2,terminalId);
             
            
       pStmt4     =  connection.prepareStatement(customerNameQuery);
       pStmt4.setString(1,customerId);
       //Modified By Kishore Podili For QuoteGroupExcel Report with Charges on 03-Feb-11
       cstmt             = connection.prepareCall("CALL qms_rates_excel.qms_ratesreport_excel(?,?,?,?,?,?,?,?,?,?,?)");
       cstmt.setString(1,customerId);
       cstmt.setString(2,originCountry);
       cstmt.setString(3,originCity);
       cstmt.setString(4,destCountry);
       cstmt.setString(5,destCity);
       cstmt.setString(6,shipmentMode);
       cstmt.setString(7,consoleType);
        cstmt.setString(8,spotRateFlag);
       //Modified By Kishore Podili For QuoteGroupExcel Report with Charges on 03-Feb-11
       cstmt.setString(9,weightBreak);
       cstmt.registerOutParameter(10, OracleTypes.CURSOR);  
       cstmt.registerOutParameter(11, OracleTypes.CURSOR); 
       cstmt.execute();
       rs = (ResultSet)cstmt.getObject(10);
  
        while(rs.next())
       {
          ratesList = new ArrayList();
           if(counttemp==0)
           {
              rs3  = pStmt1.executeQuery();
       
            while(rs3.next())
            {
               notes.add((String)rs3.getString("DESCRIPTION"));
             
                shmode.add((String)rs3.getString("SHIPMENTMODE"));
            }
          
            notesArray = new String[notes.size()];
            int notsSize	=	notes.size();
          for(int i=0;i<notsSize;i++)
          {
              notesArray[i] = (String)notes.get(i);
          }

          totalList.add(notesArray);
          totalList.add(shmode);
           rs4        = pStmt4.executeQuery();
       while (rs4.next())
       {
          totalList.add(rs4.getString("COMPANYNAME"));
       }
       if(rs4!=null)
       {
            rs4.close();
       }
       if(pStmt4!=null)
       {
          pStmt4.close();
       }
      
          counttemp++;
           if(rs3!=null)
         {
           rs3.close();
         }
         if(pStmt1!=null)
         {
           pStmt1.close();
         }
    }
          
          if(mode!=null)
          {
              mode    = mode+"','"+rs.getString("SHIPMENT_MODE");
          }
          else
          {
             mode =rs.getString("SHIPMENT_MODE");
          }
          pStmt = connection.prepareStatement(ratesexcelquery);
          pStmt.setString(1,rs.getString("WEIGHTBREAK"));
          wtBreak = rs.getString("WEIGHTBREAK");
          pStmt.setString(2,rs.getString("SHIPMENT_MODE"));
          rs1 = pStmt.executeQuery();
          Quote_id  = new HashSet();
          pStmt1 = connection.prepareStatement(brkListQuery); // Added by Gowtham on 04Feb2011 for Loop Leakages.
          while(rs1.next())
          {
             ratesDOB  = new QMSRatesReportDOB();
             wtbrkList = new ArrayList();
            // rateLinkedList =new LinkedList();
             ratesDOB.setQuoteId(rs1.getString("QUOTE_ID"));
             Quote_id.add(rs1.getString("QUOTE_ID"));
             ratesDOB.setQuoteValidUpto(rs1.getTimestamp("QUOTE_VALID_UPTO"));// added by subrahmanyam for 181328
             ratesDOB.setOriginCountry(rs1.getString("ORIGIN_COUNTRY"));
             ratesDOB.setOrigin(rs1.getString("ORIGIN_LOCATION"));
             ratesDOB.setDestination(rs1.getString("DEST_LOCATION"));
             ratesDOB.setDestCountry(rs1.getString("DESTI_COUNTRY"));
              ratesDOB.setDensityCode(rs1.getString("DENSITY_RATIO"));
             ratesDOB.setCarrierId(rs1.getString("CARRIER_ID"));
             ratesDOB.setTransitTime(rs1.getString("TRANSIT_TIME"));
             ratesDOB.setSellBuyFlag(rs1.getString("SELL_BUY_FLAG"));
             ratesDOB.setFrequency(rs1.getString("FREQUENCY"));
             ratesDOB.setCurrency(rs1.getString("CURRENCY"));
             ratesDOB.setShipmentMode(rs1.getString("SHIPMENT_MODE"));
              ratesDOB.setValidUpto(rs1.getTimestamp("VALID_UPTO"));
              ratesDOB.setDensityType(rs1.getString("DENSITY_TYPE"));
              ratesDOB.setSellBuyFlag(rs1.getString("SELL_BUY_FLAG"));
              ratesDOB.setInternalNotes(rs1.getString("INTERNAL_NOTES")!=null?rs1.getString("INTERNAL_NOTES"):"");//Added by Mohan for issue 219976 on 01112010
              ratesDOB.setExternalNotes(rs1.getString("EXTERNAL_NOTES")!=null?rs1.getString("EXTERNAL_NOTES"):"");//Added by Mohan for issue 219976 on 01112010    
               ratesDOB.setServiceLevelId(rs1.getString("SERVICELEVEL"));//@@Added by Kameswari for the WPBN issue-178965 on 12/08/09
               if(rs1.getString("CHARGE_RATE")!=null&&rs1.getString("CHARGE_RATE").trim().length()>0)
             {
                  rateList =rs1.getString("CHARGE_RATE").split(",");
            
                 /* for(int i=0;i<rateList.length;i++)
                  {
                      logger.info("rateList:"+rateList[i]);
                     rateLinkedList.add(rateList[i]);
                  }*/
                  ratesDOB.setRateList(rateList);
                //  ratesDOB.setRateLinkedList(rateLinkedList);
                  
             }
              ratesDOB.setWtBreak(wtBreak);
              ratesDOB.setWtBreakSlab(rs1.getString("WEIGHTBREAKSLAB"));
             // pStmt1 = connection.prepareStatement(brkListQuery); // Commented by Gowtham on 04Feb2011 for Loop Leakages.
              pStmt1.setString(1,rs.getString("WEIGHTBREAK"));
              pStmt1.setString(2,rs.getString("SHIPMENT_MODE"));

              rs3 = pStmt1.executeQuery();
               pStmt1.clearParameters();
               while(rs3.next())
               {
                    
                  wtbrkList.add(rs3.getString("WEIGHTBREAKSLAB"));
                
               }
               ratesDOB.setWtBreakList(wtbrkList);
                  
               ratesList.add(ratesDOB);
          }
       
        if(ratesList.size()>0)
        {
            totalList.add(ratesList); 
        }
        Quote_idmAp.put(wtBreak+rs.getString("SHIPMENT_MODE"), Quote_id);
      }
     
        //Added By Kishore Podili For QuoteGroupExcel Report with Charges on 03-Feb-11
        returningList = new ArrayList<ArrayList>();
        returningList.add(totalList);
        ConnectionUtil.closePreparedStatement(pStmt1, rs);
        

       
        	//pStmt1 = connection.prepareStatement(chargeQuery);
        	//rs = pStmt.executeQuery();
        
        	rs = (ResultSet)cstmt.getObject(11);
        	
        	while(rs.next()){
       
        		chargesDOB = new QMSChargesReportDOB();
        		
        		chargesDOB.setBreakPoint(rs.getString("CHARGESLAB"));
        		chargesDOB.setBuyRate(rs.getString("BUYRATE"));
        		chargesDOB.setBasis(rs.getString("CHARGEBASIS"));
        		chargesDOB.setChargeName(rs.getString("CHARGEDESCID"));
        		chargesDOB.setCurrency(rs.getString("currency"));
        		chargesDOB.setChargeAt(rs.getString("Cost_Incurredat"));
        		chargesDOB.setDefinedBy(rs.getString("TERMINALID"));
        	//	chargesDOB.setDestination(getLocationName(connection, rs.getString("DEST")));@@ Commented and modified by govind for the performance issue
        		chargesDOB.setDestination(rs.getString("DEST"));
        		chargesDOB.setMarginDiscountFlag(rs.getString("MARGIN_DISCOUNT_FLAG"));
        	//	chargesDOB.setOrigin(getLocationName(connection, rs.getString("ORG")));@@ Commented and modified by govind for the performance issue
        		chargesDOB.setOrigin(rs.getString("ORG"));
        		chargesDOB.setQuoteId(rs.getString("QUOTE_ID"));
        		chargesDOB.setRatio(rs.getString("DENSITY_RATIO"));
        		chargesDOB.setRsr(rs.getString("RSR"));
        		chargesDOB.setSellRate(rs.getString("Sellrate"));
        		chargesDOB.setExternalChargeName(rs.getString("ext_charge_name"));
        		
        		chargesList.add(chargesDOB);
        		
        	}
        	
        	returningList.add(chargesList);
        	Quote_idmAp.put("returnList",returningList);
        	
        //Added By Kishore Podili For QuoteGroupExcel Report with Charges on 03-Feb-11
         
        
    }
   catch(Exception e)
   {
      e.printStackTrace();
   }finally
      {
        try
        {
          if(rs!=null)
            rs.close();
         if(rs1!=null)
            rs1.close();
          if(rs2!=null)
            rs2.close();
          if(rs3!=null)
            rs3.close();
          if(pStmt1!=null)
            pStmt1.close();
          if(cstmt!=null)
            cstmt.close();
          if(pStmt!=null)
          { pStmt.close();}
          ConnectionUtil.closePreparedStatement(pStmt4,rs4);// Added by Dilip for PMD Correction on 22/09/2015
          if(connection!=null)
          { connection.close();}
        }catch(SQLException e) 
        {
          //Logger.error(FILE_NAME,"SQLException in getActivitySummaryReportDetailsData( ) method"+e.toString());
          logger.error(FILE_NAME+"SQLException in getActivitySummaryReportDetailsData( ) method"+e.toString());
          throw new EJBException();        
        }
      }
    return Quote_idmAp;
 }
 //added by phani sekhar for CR 167656    
 public StringBuffer validateFreightReportData(QMSRatesReportDOB ratesReportDOB)throws EJBException 
 {
     Connection          connection      = null;
      ResultSet           rs              = null;
       OperationsImpl      oimp            = null;
       PreparedStatement pstmt = null;
       StringBuffer      terminalQry     = new StringBuffer();
       StringBuffer errors = new StringBuffer();
       String locationQry="";
       String  shipment      = "";
   try
   {
     oimp=new OperationsImpl();
     connection = oimp.getConnection();
     int  shipmentMode  = Integer.parseInt(ratesReportDOB.getShipmentMode());
      if(shipmentMode==4){
        shipment = " AND SHIPMENTMODE IN (4,5,6,7) ";
       
      }else if(shipmentMode==1){
        shipment = " AND SHIPMENTMODE IN (1,3,5,7) ";
        
      }else if(shipmentMode==2){
        shipment = " AND SHIPMENTMODE IN (2,3,6,7) ";
        
      }
      if("H".equalsIgnoreCase(ratesReportDOB.getAcessLevel()))
       terminalQry.append( " (SELECT terminalid term_id FROM FS_FR_TERMINALMASTER)");
       else
             terminalQry.append( "(SELECT parent_terminal_id term_id FROM FS_FR_TERMINAL_REGN CONNECT BY child_terminal_id = PRIOR parent_terminal_id START WITH child_terminal_id = ?")
                   .append( " UNION ")
                   .append( " SELECT terminalid term_id FROM FS_FR_TERMINALMASTER WHERE oper_admin_flag = 'H' ")
                   .append( " UNION ")
                   .append( " SELECT ? term_id FROM DUAL ")
                   .append( " UNION ")
                   .append( " SELECT child_terminal_id term_id FROM FS_FR_TERMINAL_REGN CONNECT BY PRIOR child_terminal_id = parent_terminal_id START WITH parent_terminal_id = ?)");

     
     
           //customer
        if(ratesReportDOB.getCustomerId()!=null && (ratesReportDOB.getCustomerId().trim()).length()!=0)
      {      
     String s ="SELECT CUSTOMERID FROM FS_FR_CUSTOMERMASTER WHERE CUSTOMERID=? AND TERMINALID IN "+terminalQry.toString();
         pstmt  = connection.prepareStatement(s);
        pstmt.setString(1,ratesReportDOB.getCustomerId());
        if(!"H".equalsIgnoreCase(ratesReportDOB.getAcessLevel()))
        {
          pstmt.setString(2,ratesReportDOB.getTerminalId());
          pstmt.setString(3,ratesReportDOB.getTerminalId());
          pstmt.setString(4,ratesReportDOB.getTerminalId());
        }
        rs  = pstmt.executeQuery();
        if(!rs.next())
          errors.append("Customer is Invalid Or Does not exist.<br>");
          
       ConnectionUtil.closePreparedStatement(pstmt,rs);
          
      }
  //service Level id
   if(ratesReportDOB.getServiceLevelId()!=null && (ratesReportDOB.getServiceLevelId().trim()).length()!=0)
      {
               
        pstmt  = connection.prepareStatement("SELECT SERVICELEVELID FROM FS_FR_SERVICELEVELMASTER WHERE SERVICELEVELID = ? AND INVALIDATE='F' AND TERMINALID IN "+terminalQry.toString());
        pstmt.setString(1,ratesReportDOB.getServiceLevelId());
        if(!"H".equalsIgnoreCase(ratesReportDOB.getAcessLevel()))
        {
          pstmt.setString(2,ratesReportDOB.getTerminalId());
          pstmt.setString(3,ratesReportDOB.getTerminalId());
          pstmt.setString(4,ratesReportDOB.getTerminalId());
        }
        
        rs  = pstmt.executeQuery();
        if(!rs.next())
          errors.append("Service Level Id is In Correct Or Does not exist Or does not exist for this Shipment Mode.<br>");
        
         ConnectionUtil.closePreparedStatement(pstmt,rs);
        
      }
     if("1".equals(ratesReportDOB.getShipmentMode()))
     {
        locationQry ="SELECT LOCATIONID FROM  FS_FR_LOCATIONMASTER WHERE LOCATIONID =? AND (INVALIDATE ='F' OR INVALIDATE IS NULL) "+shipment;

       pstmt  = connection.prepareStatement(locationQry);      
         if(ratesReportDOB.getOrigin()!=null && (ratesReportDOB.getOrigin().trim()).length()!=0)
        {
        pstmt.clearParameters();
          pstmt.setString(1,ratesReportDOB.getOrigin());
          rs  = pstmt.executeQuery();
          if(!rs.next())
            errors.append("Origin Location is not valid.<br>");
            if(rs!=null)
            rs.close();
        }
        if(ratesReportDOB.getDestination()!=null && (ratesReportDOB.getDestination().trim()).length()!=0)
        {
          pstmt.clearParameters();
          pstmt.setString(1,ratesReportDOB.getDestination());
          rs  = pstmt.executeQuery();
          if(!rs.next())
            errors.append("Destination Location is not valid.<br>");
        }
        ConnectionUtil.closePreparedStatement(pstmt,rs);
     }
    else
    {
        locationQry="SELECT PORTID FROM FS_FRS_PORTMASTER WHERE PORTID = ? AND (INVALIDATE ='F' OR INVALIDATE IS NULL)";
         pstmt  = connection.prepareStatement(locationQry);      
         if(ratesReportDOB.getOrigin()!=null && (ratesReportDOB.getOrigin().trim()).length()!=0)
        {
        pstmt.clearParameters();
          pstmt.setString(1,ratesReportDOB.getOrigin());
          rs  = pstmt.executeQuery();
          if(!rs.next())
            errors.append("Origin Port is not valid.<br>");
            if(rs!=null)
            rs.close();
        }
        if(ratesReportDOB.getDestination()!=null && (ratesReportDOB.getDestination().trim()).length()!=0)
        {
          pstmt.clearParameters();
          pstmt.setString(1,ratesReportDOB.getDestination());
          rs  = pstmt.executeQuery();
          if(!rs.next())
            errors.append("Destination Port is not valid.<br>");
        }
        ConnectionUtil.closePreparedStatement(pstmt,rs);
        
    }

    //Added By Kishore For SalesMan Validation
   //Salesman
     if(ratesReportDOB.getSalesPerson()!=null && (ratesReportDOB.getSalesPerson().trim()).length()!=0)
        {
                 
          pstmt  = connection.prepareStatement(	"Select EMPID, USERID, USERNAME" +
        		  								" from V_REPOFFICE_MASTER\n" + 
        		  								" where locationid in" + 
        		  								" (select terminalid term_id from fs_fr_terminalmaster)" + 
        		  								"  and  EMPID LIKE ?");
          
          pstmt.setString(1,ratesReportDOB.getSalesPerson()+"%");
                
          rs  = pstmt.executeQuery();
          if(!rs.next())
            errors.append("Invalid SalesPerson: "+ratesReportDOB.getSalesPerson()+"<br>");
          
           ConnectionUtil.closePreparedStatement(pstmt,rs);
          
        }

   }catch(Exception e)
   {
   e.printStackTrace();
 throw new EJBException();
   }
    finally
 {
   ConnectionUtil.closeConnection(connection,pstmt,rs);
 }
 return errors;
 }
 public ArrayList getFreightRatesExcelDetails(QMSRatesReportDOB ratesReportDOB)throws  EJBException 
 {
      Connection          connection      = null;
      ResultSet           rs              = null;
      OperationsImpl      oimp            = null;
      CallableStatement   cStmt           = null;
      ArrayList headerList,dataList = null;
      ArrayList excelDetails = null;
      ESupplyDateUtility dateUtility = new ESupplyDateUtility(); // Added by gowtham
   try{
   
   oimp=new OperationsImpl();
   connection = oimp.getConnection();
   cStmt  =  connection.prepareCall("{ call Get_Rates_header_proc(?,?,?,?,?,?,?,?,?,?,?,?,?)}");
   
      cStmt.setString(1,ratesReportDOB.getOrigin());
      cStmt.setString(2,ratesReportDOB.getDestination());
      cStmt.setString(3,ratesReportDOB.getRateType());//rate type
      cStmt.setString(4,ratesReportDOB.getWeightBreak());//wgthbreak
      cStmt.setString(5,ratesReportDOB.getServiceLevelId());
      cStmt.setString(6,ratesReportDOB.getShipmentMode());
      cStmt.setString(7,ratesReportDOB.getCustomerId());   
      cStmt.setTimestamp(8,ratesReportDOB.getFrmDate());
      cStmt.setTimestamp(9,ratesReportDOB.getToDate());  
      cStmt.setString(10,ratesReportDOB.getTerminalId()); 
       cStmt.setString(11,ratesReportDOB.getOriginCountry()); 
      cStmt.setString(12,ratesReportDOB.getDestCountry()); 
       cStmt.registerOutParameter(13,OracleTypes.CURSOR);
       
       
      cStmt.execute();
      rs  = (ResultSet) cStmt.getObject(13);
      headerList = new ArrayList();
      while(rs.next())
      {
        if(rs.getString("WEIGHT_BREAK_SLAB")!=null)
        headerList.add(rs.getString("WEIGHT_BREAK_SLAB"));      
      }
     dataList=getFreightRatesExcelDetailsData(headerList,ratesReportDOB,connection);
     excelDetails = new ArrayList ();
     excelDetails.add(headerList);
     excelDetails.add(dataList);
      
 }catch(Exception e)
 {
 e.printStackTrace();
 throw new EJBException(); 
 }
 finally
 {
   ConnectionUtil.closeConnection(connection,cStmt,rs);
 }
 return excelDetails;
 }
 private ArrayList getFreightRatesExcelDetailsData(ArrayList headers,QMSRatesReportDOB ratesReportDOB,Connection con)throws  EJBException
 {
  
  ResultSet           rst              = null;
  PreparedStatement pStmt = null;
  ArrayList details=null,frghtRates = null;
  StringBuffer strQry = null;
  String frmClause = null;
  StringBuffer whrClause = null;
  StringBuffer dynamicQry = null;
  QMSRatesReportDOB ratesDOB = null;
  int headerListSize = 0;
  String data[] = null;
  String terminalQry = "";
  ESupplyDateUtility dateUtility = new ESupplyDateUtility();// Added by Gowtham

   try
   {
      if(headers!=null && headers.size()>0)
         headerListSize = headers.size();
     whrClause = new StringBuffer();
     strQry = new StringBuffer();
     details = new ArrayList();
    strQry.append("SELECT DISTINCT QM.QUOTE_ID,QM.CUSTOMER_ID,(SELECT COMPANYNAME FROM FS_FR_CUSTOMERMASTER CM WHERE CM.CUSTOMERID = QM.CUSTOMER_ID) CUSTOMERNAME,");
    strQry.append(" DECODE(QM.SHIPMENT_MODE,'1',QM.ORIGIN_LOCATION,QM.ORIGIN_PORT) ORIGINLOCATION, ");
    strQry.append(" DECODE(QM.SHIPMENT_MODE, '1',(SELECT LM.COUNTRYID FROM FS_FR_LOCATIONMASTER LM WHERE LM.LOCATIONID = QM.ORIGIN_LOCATION),");
    strQry.append(" (SELECT PM.COUNTRYID FROM FS_FRS_PORTMASTER PM WHERE PM.PORTID = QM.ORIGIN_PORT)) ORIGINCOUNTRY, ");
    strQry.append(" DECODE(QM.SHIPMENT_MODE, '1', QM.DEST_LOCATION, QM.DESTIONATION_PORT) DESTLOCATION,DECODE(QM.SHIPMENT_MODE, '1', ");
    strQry.append(" (SELECT LM.COUNTRYID FROM FS_FR_LOCATIONMASTER LM WHERE LM.LOCATIONID = QM.DEST_LOCATION),(SELECT PM.COUNTRYID " );   
    strQry.append(" FROM FS_FRS_PORTMASTER PM WHERE PM.PORTID =QM.DESTIONATION_PORT)) DESTCOUNTRY, ");
    //Modified by Mohan for Issue No.219976 on 30102010
    //Modified by Anil.k for Issue No. 236362 on 24Feb2011
     frmClause=  " QM.SHIPMENT_MODE SMODE,BRM.CURRENCY CURRENCY, QM.CREATED_TSTMP CDATE ,QM.VALID_TO QVALIDITY,GET_FREIGHT_RATE_VALIDITY(QRTS.BUYRATE_ID,QRTS.RATE_LANE_NO,QRTS.VERSION_NO)FVALIDITY,GET_FREIGHT_RATE_DENSITY(QRTS.BUYRATE_ID,QRTS.RATE_LANE_NO,QRTS.VERSION_NO)DENSITY_CODE " +
     			 ", X.INTERNAL_NOTES,X.EXTERNAL_NOTES,QM.MODIFIED_DATE,QM.CREATED_BY,QM.SALES_PERSON,QM.CUST_REQUESTED_DATE ,QM.IS_MULTI_QUOTE" +
     			 " FROM QMS_QUOTE_MASTER QM, QMS_QUOTE_RATES QRTS, QMS_BUYRATES_MASTER BRM " +
     			 " ,QMS_QUOTE_NOTES X " +
     			 " WHERE QRTS.QUOTE_ID = QM.ID AND BRM.BUYRATEID = QRTS.BUYRATE_ID AND QM.ACTIVE_FLAG = 'A'   AND QRTS.LINE_NO = '0' ";
     
     whrClause .append( " AND QRTS.SELL_BUY_FLAG IN ('RSR', 'BR', 'CSR') AND QRTS.VERSION_NO = BRM.VERSION_NO  AND (QRTS.RATE_LANE_NO = BRM.LANE_NO OR BRM.LANE_NO IS NULL) ");
     whrClause .append( " AND X.QUOTE_ID =  QM.ID ");    //Added by Mohan for issue no.219976 on 09112010
     
    if(ratesReportDOB.getShipmentMode()!=null)
    whrClause.append(" AND QM.SHIPMENT_MODE='"+ratesReportDOB.getShipmentMode()+"'");
    
    if(ratesReportDOB.getCustomerId()!=null && !"".equals(ratesReportDOB.getCustomerId()))
     whrClause.append(" AND QM.CUSTOMER_ID like '"+ratesReportDOB.getCustomerId()+"%'");
     if(ratesReportDOB.getRateType()!=null)
     whrClause.append(" AND BRM.RATE_TYPE = '"+ratesReportDOB.getRateType()+"'");
     if(ratesReportDOB.getWeightBreak()!=null)
     whrClause.append(" AND BRM.WEIGHT_BREAK = '"+ratesReportDOB.getWeightBreak()+"'");
     if(ratesReportDOB.getFrmDate()!=null)     
     whrClause.append(" AND QM.CREATED_TSTMP >=? ");
     if(ratesReportDOB.getToDate()!=null)     
     whrClause.append(" AND QM.CREATED_TSTMP <=? ");
     
     if(ratesReportDOB.getOrigin()!=null && !"".equals(ratesReportDOB.getOrigin()))
     {
       if("1".equals(ratesReportDOB.getShipmentMode()))
       whrClause.append(" AND QM.ORIGIN_LOCATION='"+ratesReportDOB.getOrigin()+"'");
       else
       whrClause.append(" AND QM.Origin_Port='"+ratesReportDOB.getOrigin()+"'");
     }
     
     if(ratesReportDOB.getDestination()!=null && !"".equals(ratesReportDOB.getDestination()))
     {
       if("1".equals(ratesReportDOB.getShipmentMode()))
       whrClause.append(" AND QM.Dest_Location='"+ratesReportDOB.getDestination()+"'");
       else
       whrClause.append(" AND QM.Destionation_Port='"+ratesReportDOB.getDestination()+"'");
     }
      if(ratesReportDOB.getOriginCountry()!=null && !"".equals(ratesReportDOB.getOriginCountry()))
     {
     if("1".equals(ratesReportDOB.getShipmentMode()))
    whrClause.append(" AND QM.ORIGIN_LOCATION IN( select locationid from fs_fr_locationmaster where countryid ='"+ratesReportDOB.getOriginCountry()+"' ) ");
    else
    whrClause.append(" AND QM.ORIGIN_PORT IN( SELECT portid FROM fs_frs_portmaster where countryid ='"+ratesReportDOB.getOriginCountry()+"' ) "); 
     }
    if(ratesReportDOB.getDestCountry()!=null && !"".equals(ratesReportDOB.getDestCountry()))
    {
      if("1".equals(ratesReportDOB.getShipmentMode()))
      whrClause.append(" AND QM.DEST_LOCATION IN( select locationid from fs_fr_locationmaster where countryid ='"+ratesReportDOB.getDestCountry()+"' ) ");
      else
      whrClause.append(" AND QM.DESTIONATION_PORT IN( SELECT portid FROM fs_frs_portmaster where countryid ='"+ratesReportDOB.getDestCountry()+"' ) "); 
    }
     if("H".equals(ratesReportDOB.getAcessLevel()))
     terminalQry = " SELECT terminalid FROM FS_FR_TERMINALMASTER "  ;
     else
      terminalQry=" SELECT '"+ratesReportDOB.getTerminalId()+"' FROM DUAL UNION SELECT child_terminal_id term_id FROM FS_FR_TERMINAL_REGN  CONNECT BY PRIOR child_terminal_id = parent_terminal_id  START WITH  parent_terminal_id ='"+ratesReportDOB.getTerminalId()+"'";
     
       whrClause.append(" AND QM.TERMINAL_ID in ("+terminalQry+")");
     //Added by Anil.k for Issue No. 236362 on 24Feb2011
       if(ratesReportDOB.getSalesPerson()!=null && !"".equals(ratesReportDOB.getSalesPerson()))
    	   whrClause.append(" AND QM.SALES_PERSON = '"+ratesReportDOB.getSalesPerson()+"' ");
     //Ended by Anil.k for Issue No. 236362 on 24Feb2011
       whrClause.append(" ORDER BY QM.QUOTE_ID "); //Add Mohan for issue No.219976 on 10112010
      dynamicQry= getDynamicQry(headers);
     
      pStmt=con.prepareStatement(strQry.toString()+dynamicQry.toString()+frmClause+whrClause.toString());
      //	pStmt.setTimestamp(1,ratesReportDOB.getFrmDate());
      	pStmt.setString(1, dateUtility.getDisplayDateStringWithTime(ratesReportDOB.getFrmDate(), "DD-MON-YY").substring(0,9) ); // Added by Gowtham)
      	//pStmt.setTimestamp(2,ratesReportDOB.getToDate());
      	pStmt.setString(2,dateUtility.getDisplayDateStringWithTime(ratesReportDOB.getToDate(), "DD-MON-YY").substring(0,9) ); // Added by Gowtham)
   
      rst=pStmt.executeQuery();
      
     // rst=stmt.executeQuery(strQry+dynamicQry.toString()+frmClause+whrClause.toString());
      
      while(rst.next())
      {
        ratesDOB= new QMSRatesReportDOB();
        ratesDOB.setQuoteId(rst.getString(1));
        ratesDOB.setCustomerId(rst.getString(2));
        ratesDOB.setCustomerName(rst.getString(3));
        ratesDOB.setShipmentMode(rst.getString("SMODE"));
        ratesDOB.setOrigin(rst.getString("ORIGINLOCATION"));
        ratesDOB.setOriginCountry(rst.getString("ORIGINCOUNTRY"));
        ratesDOB.setDestination(rst.getString("DESTLOCATION"));
        ratesDOB.setDestCountry(rst.getString("DESTCOUNTRY"));
        ratesDOB.setCurrency(rst.getString("CURRENCY"));
        ratesDOB.setCreationDate(rst.getTimestamp("CDATE"));
        ratesDOB.setQuoteValidDate(rst.getTimestamp("QVALIDITY"));
        ratesDOB.setFreightValidDate(rst.getTimestamp("FVALIDITY"));
       //Modified by Mohan for Issue No on 08112010
       ratesDOB.setInternalNotes(rst.getString("INTERNAL_NOTES")!=null?rst.getString("INTERNAL_NOTES"):"");
       ratesDOB.setExternalNotes(rst.getString("EXTERNAL_NOTES")!=null?rst.getString("EXTERNAL_NOTES"):"");
     //Added by Anil.k for Issue No. 236362 on 24Feb2011
       ratesDOB.setCustomerReqDate(rst.getTimestamp("CUST_REQUESTED_DATE"));
       ratesDOB.setCreatedBy(rst.getString("CREATED_BY")!=null?rst.getString("CREATED_BY"):"");
       ratesDOB.setModifiedDate(rst.getTimestamp("MODIFIED_DATE"));
       ratesDOB.setSalesPerson(rst.getString("SALES_PERSON")!=null?rst.getString("SALES_PERSON"):"");
       ratesDOB.setMultiQuote(rst.getString("IS_MULTI_QUOTE")!= null?rst.getString("IS_MULTI_QUOTE"):"");
     //Ended by Anil.k for Issue No. 236362 on 24Feb2011
       if(rst.getString("DENSITY_CODE")!=null)
       {
         data = rst.getString("DENSITY_CODE").split("#");
         if(data!=null && data[0]!=null)
         ratesDOB.setDensityCode(data[0]);   
         ratesDOB.setDensityType(data[1]);
       }
       
        frghtRates = new ArrayList();
        
        for(int i=0;i<headerListSize;i++)
        {
        
        data=rst.getString(8+i).split("#");
       
        if(data[0]!=null)
        frghtRates.add(data[0]);
        
          if(data[1]!=null && !"NA".equals(data[1]) && ratesDOB.getCarrierId()==null)
          ratesDOB.setCarrierId(data[1]);
          if(data[2]!=null && !"NA".equals(data[2]) && ratesDOB.getServiceLevelId()==null)
          ratesDOB.setServiceLevelId(data[2]);
       
        }
        ratesDOB.setChargeRateList(frghtRates);
        if(ratesReportDOB.getServiceLevelId()!=null && !"".equals(ratesReportDOB.getServiceLevelId()))
        {
          if(ratesDOB.getServiceLevelId()!=null && !"".equals(ratesDOB.getServiceLevelId()))
          {
            if(ratesReportDOB.getServiceLevelId().equals(ratesDOB.getServiceLevelId()) ||"SCH".equals(ratesDOB.getServiceLevelId()))
            details.add(ratesDOB);
          }
          else
          System.out.println("search value is entered but no matching record exists in details table");
          
        }
        else
        {
        details.add(ratesDOB);
        }
      }
      
   }catch(Exception e){
   e.printStackTrace();
   throw new EJBException();
   }
   finally
   {
   ConnectionUtil.closeStatement(pStmt,rst);
   }
   return details;
 }
 private StringBuffer getDynamicQry (ArrayList headers)throws  EJBException
 {
  StringBuffer qry = null;
   try
   {
     
     qry = new StringBuffer();
     if(headers!=null && headers.size()>0)
     {
    	 int headerSize	= headers.size();
       for(int i=0;i<headerSize;i++)
       { 
         qry.append("GETFREIGHTRATE(QRTS.BUYRATE_ID,'"+(String)headers.get(i)+"',QRTS.RATE_LANE_NO,QRTS.SELL_BUY_FLAG,QRTS.VERSION_NO,QRTS.SELLRATE_ID,QRTS.QUOTE_ID,QRTS.SERIAL_NO),");//modified by phani sekhar for wpbn 176475 on 200907
       }
     }
   }catch(Exception e){
   e.printStackTrace();
   throw new EJBException();
   }
   return qry;
   
 }//ends 167656




	public String getLocationName(Connection connection, String locName) throws EJBException
	{
	//	   Connection                connection              = null;
		   PreparedStatement         pstmt                   = null;
		   String					 locname				 = null;
		   ResultSet				 rs						 = null; 	
		   try
		   {
			   //connection = this.getConnection();
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
		     ConnectionUtil.closeConnection(null,pstmt,rs);
		   }
		  return locname;
	   
		}
	
	public String getCountryName(Connection connection, String countryId) throws EJBException
	{
		   //Connection                connection              = null;
		   PreparedStatement         pstmt                   = null;
		   String					 countryName				 = null;
		   ResultSet				 rs						 = null; 	
		   try
		   {
			  // connection = this.getConnection();
			   pstmt	  = connection.prepareStatement("SELECT countryname FROM fs_countrymaster  WHERE countryid = ?");
			   pstmt.setString(1,countryId);
			   rs 			= pstmt.executeQuery();
			   if(rs.next())
				   countryName = rs.getString("countryname");
			   else
				   countryName = "";
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
		     ConnectionUtil.closeConnection(null,pstmt,rs);
		   }
		  return countryName;
	   
		}

}