package com.qms.operations.rates.ejb.sls;
/**
 * @ (#) BuyRatesSessionBean.java
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
 * File       : BuyRatesSessionBean.java
 * Sub-Module : Buy Rates
 * Module     : QMS
 * @author    : 
 * @date      : 17-08-2005
 * Modified by: Date     Reason
 */




import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Set;
import java.util.TreeSet;

import javax.ejb.EJBException;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.foursoft.esupply.common.bean.ESupplyGlobalParameters;
import com.foursoft.esupply.common.util.ConnectionUtil;
import com.foursoft.esupply.common.util.ESupplyDateUtility;
import com.foursoft.etrans.common.util.java.OperationsImpl;
import com.qms.operations.rates.dao.RatesDao;
import com.qms.operations.rates.dob.FlatRatesDOB;
import com.qms.operations.rates.dob.RateDOB;
import com.qms.operations.rates.ejb.bmp.BuyRatesEntityLocal;
import com.qms.operations.rates.ejb.bmp.BuyRatesEntityLocalHome;
import com.qms.operations.sellrates.java.QMSBoundryDOB;
import com.qms.operations.sellrates.java.QMSSellRatesDOB;
public class BuyRatesSessionBean implements SessionBean 
{

private static final String         FILE_NAME      = "BuyRatesSessionBean.java";
private	             SessionContext	sessionContext = null;
private	             String	        error = null;
                     OperationsImpl operationsImpl  = null;
private static Logger logger = null;

public BuyRatesSessionBean()
{
  logger  = Logger.getLogger(BuyRatesSessionBean.class);
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
  }
  
   public ArrayList crateBuyFlatRate(RateDOB rateDOB,ArrayList list)throws EJBException
   {
   BuyRatesEntityLocal      local  = null;
   BuyRatesEntityLocalHome  home   = null;
 //  String                   msg    = null;   
   
   	 RatesDao dao	=	null;
		 String		 msg	=	null;
     ArrayList           list_errorList = null;
   
    try
    {
  //  home  =  (BuyRatesEntityLocalHome)LookUpBean.getEJBLocalHome("java:comp/env/BuyRatesEntityBean");
  //  local =  (BuyRatesEntityLocal)home.create();   
  //  msg   =  local.crateBuyFlatRate(rateDOB,list);
  
  
  
			dao = new RatesDao();
      list_errorList=	dao.crateBuyFlatRate(rateDOB,list);
      
    }//    catch(NamingException e)
//    {
//      System.out.println("in naming exception for entity ");
//      e.printStackTrace();
//    }catch(CreateException e)
//    {
//    System.out.println("in create exception for entity ");
//      e.printStackTrace();
//    }
    catch(Exception e)
    {
    System.out.println("in normal exception for entity ");
      e.printStackTrace();
      throw new EJBException();
    }finally
    {
    local  = null;
    home   = null;
      
    }
   return list_errorList;
   }
   
  public String validateRateDOB(String carrier,String currency,String shipmentMode,String terminalId)
  {
   BuyRatesEntityLocal      local  = null;
   BuyRatesEntityLocalHome  home   = null;
   String                   msg    = null;   
   
   
     RatesDao dao	=	null;
		/// String		 msg	=	null;
   
   
    try
    {
    
    	dao = new RatesDao();
			msg=	dao.validateRateDOB(carrier,currency,shipmentMode,terminalId);
    
    
  //  home  =  (BuyRatesEntityLocalHome)LookUpBean.getEJBLocalHome("java:comp/env/BuyRatesEntityBean");
  //  local =  (BuyRatesEntityLocal)home.create(); 
  //  msg   =   local.validateRateDOB(carrier,currency);
  
     }//    catch(NamingException e)
//    {
//      0intln("in naming exception for entity ");
//      e.printStackTrace();
//    }catch(CreateException e)
//    {
//    System.out.println("in create exception for entity ");
//      e.printStackTrace();
//    }
    catch(Exception e)
    {
    System.out.println("in normal exception for entity ");
      e.printStackTrace();
      throw new EJBException();
    }finally
    {
      local  = null;
      home   = null;
   }
   return msg;
    
  }
  public String validateDetailRateDOB(ArrayList lanes,RateDOB rateDOB)
  {
   BuyRatesEntityLocal      local  = null;
   BuyRatesEntityLocalHome  home   = null;
   String                   msg    = null;   
   
   
    RatesDao dao	=	null;
	//	 String		 msg	=	null;
    try
    {
  //  home  =  (BuyRatesEntityLocalHome)LookUpBean.getEJBLocalHome("java:comp/env/BuyRatesEntityBean");
  //  local =  (BuyRatesEntityLocal)home.create(); 
  //  msg   =   local.validateDetailRateDOB(lanes,rateDOB);
  
      dao = new RatesDao();
			msg =	dao.validateDetailRateDOB(lanes,rateDOB);

  
  
  
  
     }
//    catch(NamingException e)
//    {
//      System.out.println("in naming exception for entity ");
//      e.printStackTrace();
//    }catch(CreateException e)
//    {
//    System.out.println("in create exception for entity ");
//      e.printStackTrace();
//    }
    catch(Exception e)
    {
    System.out.println("in normal exception for entity ");
      e.printStackTrace();
      throw new EJBException();
    }finally
    {
      local  = null;
      home   = null;
   }
   return msg;
    
    
  }
  
  public ArrayList getListRatesVales(QMSSellRatesDOB sellRatesDob,ESupplyGlobalParameters loginBean,String operation)
  {
   RatesDao     dao	  =	null;
   ArrayList    list  = null;
    try
    {
     dao = new RatesDao();
			list= dao.getListRatesVales(sellRatesDob,loginBean,operation);
      
    }catch(Exception e)
    {
     throw new EJBException(); 
    }
    return list;
  }
  
   public ArrayList getSlabRatesVales(QMSSellRatesDOB sellRatesDob,ESupplyGlobalParameters loginBean,String operation)
   {
   RatesDao     dao	  =	null;
   ArrayList    list  = null;
    try
    {
     dao = new RatesDao();
			list= dao.getSlabRatesVales(sellRatesDob,loginBean,operation);
      
    }catch(Exception e)
    {
      
    }
    return list;
     
   }
   public HashMap getFlatRatesVales(QMSSellRatesDOB sellRatesDob,ESupplyGlobalParameters loginBean,String operation)
   {
    RatesDao     dao	  =	null;
    HashMap       map  = null;
    try
    {
     dao = new RatesDao();
			map= dao.getFlatRatesVales(sellRatesDob,loginBean,operation);
      
    }catch(Exception e)
    {
      
    }
    return map;
     
   }
  /* 
   public ArrayList getSellRatesValues(QMSSellRatesDOB sellRatesDob,ESupplyGlobalParameters loginBean,String operation)
  {
      ArrayList                   arrayList             =   null;
      HashMap                     flatRatesMap          =   null;
       ArrayList                  headerList            =   null;
      RatesDao             sellRatesDao          =   new RatesDao();
      try
      {
         
          arrayList                     =   new ArrayList();
          
          if(sellRatesDob!=null)
          {
            
            if("1".equals(sellRatesDob.getShipmentMode()) || ("2".equals(sellRatesDob.getShipmentMode()) && "LCL".equals(sellRatesDob.getConsoleType())) || ("4".equals(sellRatesDob.getShipmentMode()) && "LTL".equals(sellRatesDob.getConsoleType())))
            {
              
              if("Flat".equals(sellRatesDob.getWeightBreak()))
              {
                  arrayList.add(sellRatesDob);
                  flatRatesMap = sellRatesDao.getFlatRatesVales(sellRatesDob,loginBean,operation);
                  arrayList.add(flatRatesMap);
              }
              else if("Slab".equals(sellRatesDob.getWeightBreak()))
              {
                  arrayList.add(sellRatesDob);
                  headerList = sellRatesDao.getSlabRatesVales(sellRatesDob,loginBean,operation);
                  arrayList.add(headerList);
              }
              else if("List".equals(sellRatesDob.getWeightBreak()))
              {
                  arrayList.add(sellRatesDob);
                  headerList = sellRatesDao.getListRatesVales(sellRatesDob,loginBean,operation);
                  arrayList.add(headerList);
              }
            }
            else
            {
                  arrayList.add(sellRatesDob);
                  headerList = sellRatesDao.getFCLRatesVales(sellRatesDob,loginBean,operation);
                  arrayList.add(headerList);
            }
          }     
          
      }
      catch(SQLException sqle)
      {
          Logger.error(FILE_NAME,"SQLEXception in getSellRatesValues()-->"+sqle.toString());
          sqle.printStackTrace();
          throw new EJBException(sqle.toString());
      }
      catch(Exception e)
      {
        Logger.error(FILE_NAME,"EXception in getSellRatesValues()-->"+e.toString());
        e.printStackTrace();
        throw new EJBException(e.toString());
      }
    return arrayList;
  }
   */
  public StringBuffer validateSellRatesHdrData(QMSSellRatesDOB sellDob)
  {
       RatesDao             sellRatesDao                 =   new RatesDao();
      StringBuffer                errorMassege          =   null;
      try
      {
          
          errorMassege = sellRatesDao.isExetIds(sellDob);   
          
      }
      catch(SQLException sqle)
      {
          //Logger.error(FILE_NAME,"SQLEXception in validateSellRatesHdrData()-->"+sqle.toString());
          logger.error(FILE_NAME+"SQLEXception in validateSellRatesHdrData()-->"+sqle.toString());
          sqle.printStackTrace();
          throw new EJBException(sqle.toString());
      }
      catch(Exception e)
      {
        //Logger.error(FILE_NAME,"EXception in validateSellRatesHdrData()-->"+e.toString());
        logger.error(FILE_NAME+"EXception in validateSellRatesHdrData()-->"+e.toString());
        e.printStackTrace();
        throw new EJBException(e.toString());
      }
      return errorMassege;
  }
   
 /*public StringBuffer validateSellRatesHdrData(QMSSellRatesDOB sellDob)
  {
      RatesDao             sellRatesDao                 =   new RatesDao();
      StringBuffer                errorMassege          =   null;
      
      String                      originValue           =   null;
      String                      destinationValue      =   null;
      String                      carrierValue          =   null;
      String                      serviceLevelValue     =   null;
      String                      currencyValue         =   null;
      
      ArrayList                   commenList            =   null;
      ArrayList                   orginIds              =   null;
      ArrayList                   destIds               =   null;
      ArrayList                   carrierIds            =   null;
      ArrayList                   serviceIds            =   null;
      try
      {
          originValue         =    sellDob.getOrigin();
          destinationValue    =    sellDob.getDestination();
          carrierValue        =    sellDob.getCarrier_id();
          serviceLevelValue   =    sellDob.getServiceLevel();
          currencyValue       =    sellDob.getCurrencyId();
          commenList    =   new ArrayList();
          orginIds      =   new ArrayList();
          destIds       =   new ArrayList();
          carrierIds    =   new ArrayList();
          serviceIds    =   new ArrayList();
          
          if(originValue.indexOf(",")!=-1 ){
              StringTokenizer orStr = new StringTokenizer(originValue,",");
              int i=0;
              while(orStr.hasMoreTokens()){
                 orginIds.add(orStr.nextToken());
                 i++;
              }
          }
          else{
            orginIds.add(originValue);
          }
          if(destinationValue.indexOf(",")!=-1){
              StringTokenizer destStr = new StringTokenizer(destinationValue,",");
              int i=0;
              while(destStr.hasMoreTokens()){
                 destIds.add(destStr.nextToken());
                 i++;
              }
          }
          else{
            destIds.add(destinationValue);
          }
          if(carrierValue.indexOf(",")!=-1){
              StringTokenizer carrierStr = new StringTokenizer(carrierValue,",");
              int i=0;
              while(carrierStr.hasMoreTokens()){
                 carrierIds.add(carrierStr.nextToken());
                 i++;
              }
          }
          else{
            carrierIds.add(carrierValue);
          }
          if(serviceLevelValue.indexOf(",")!=-1){
              StringTokenizer serviceStr = new StringTokenizer(serviceLevelValue,",");
              int i=0;
              while(serviceStr.hasMoreTokens()){
                 serviceIds.add(serviceStr.nextToken());
                 i++;
              }
          }
          else{
            serviceIds.add(serviceLevelValue);
          }
          commenList.add(orginIds);
          commenList.add(destIds);
          commenList.add(carrierIds);
          commenList.add(serviceIds);
          commenList.add(currencyValue);
          errorMassege = sellRatesDao.isExetIds(commenList);   
          
      }
      catch(SQLException sqle)
      {
          Logger.error(FILE_NAME,"SQLEXception in validateSellRatesHdrData()-->"+sqle.toString());
          sqle.printStackTrace();
          throw new EJBException(sqle.toString());
      }
      catch(Exception e)
      {
        Logger.error(FILE_NAME,"EXception in validateSellRatesHdrData()-->"+e.toString());
        e.printStackTrace();
        throw new EJBException(e.toString());
      }
      return errorMassege;
  } 
  */
 public ArrayList getFCLRatesVales(QMSSellRatesDOB sellRatesDob,ESupplyGlobalParameters loginBean,String operation)throws SQLException
  {
      Connection            connection		    =	  null;
      PreparedStatement     pStmt		 		      =   null;
      ResultSet    					rs            	  = 	null;
      PreparedStatement     pStmt1		 		    =   null;
      ResultSet    					rs1            	  = 	null;
      QMSSellRatesDOB       qmsSellRatesDob   =   null;
      QMSBoundryDOB         boundryDOB        =   null;
      ArrayList             slabValues        =   null;
      ArrayList             boundryList       =   null;
      ArrayList             boundryValueList  =   null;
      String                key               =   null;
      String                sqlQueryListWt    =   null;
      String                sqlQueryList      =   null;
      
      String                orgCountryQuery   =   null;
      String                destCountryQuery  =   null;
      Hashtable             originTable       =   new Hashtable();
      Hashtable             destTable         =   new Hashtable();
      HashMap               map               =   new HashMap();
      
      String                orignStr          =   null;
      String                orign             =   null;
      String                destination       =   null;
      String                carrier           =   null;
      String                serviceLevel      =   null;
      String                destinationStr    =   null;
      String                serviceStr        =   null;
      String                carrierStr        =   null;        
      try
      {
      
          slabValues  =   new ArrayList();
          boundryList =   new ArrayList();
          connection           =    operationsImpl.getConnection();
                                               
          orignStr            =   sellRatesDob.getOrigin();
          destinationStr      =   sellRatesDob.getDestination();
          serviceStr          =   sellRatesDob.getServiceLevel();
          carrierStr          =   sellRatesDob.getCarrier_id();
          orign               =   orignStr.replaceAll(",","','");
          destination         =   destinationStr.replaceAll(",","','");
          serviceLevel        =   serviceStr.replaceAll(",","','");
          carrier             =   carrierStr.replaceAll(",","','");
          
          orignStr            =   " AND QBD.ORIGIN IN('"+orign+"') AND QBD.DESTINATION IN('"+destination+"') AND QBD.SERVICE_LEVEL IN('"+serviceLevel+"')";
          carrierStr          =   " AND QBM.CARRIER_ID IN('"+carrier+"')";
          
          sqlQueryListWt           =    " SELECT QBD.WEIGHT_BREAK_SLAB WEIGHT_BREAK_SLAB FROM QMS_BUYRATES_DTL QBD,QMS_BUYRATES_MASTER QBM WHERE QBD.BUYRATEID=QBM.BUYRATEID "+orignStr+" AND QBM.CURRENCY=? AND QBM.SHIPMENT_MODE=? AND "+
                                        " QBM.WEIGHT_BREAK=? AND QBM.RATE_TYPE=? "+carrierStr+" AND QBM.TERMINALID=? ORDER BY WEIGHT_BREAK_SLAB";
    
          sqlQueryList             =    " SELECT QBD.BUYRATEID BUYRATEID,QBM.CARRIER_ID CARRIER_ID,QBD.ORIGIN ORIGIN,QBD.DESTINATION DESTINATION,QBD.SERVICE_LEVEL SERVICE_LEVEL,QBD.WEIGHT_BREAK_SLAB WEIGHT_BREAK_SLAB, "+
                                        " QBD.TRANSIT_TIME TRANSIT_TIME,QBD.FREQUENCY FREQUENCY,QBD.CHARGERATE CHARGERATE,QBD.LANE_NO LANE_NO,QBD.LOWERBOUND LOWERBOUND,QBD.UPPERBOUND UPPERBOUND,INVALIDATE  FROM QMS_BUYRATES_DTL QBD,QMS_BUYRATES_MASTER QBM "+   
                                        " WHERE QBD.BUYRATEID=QBM.BUYRATEID "+orignStr+" AND QBM.CURRENCY=? AND QBM.SHIPMENT_MODE=? AND QBM.WEIGHT_BREAK=? AND QBM.RATE_TYPE=? "+carrierStr+" AND QBM.TERMINALID=? ORDER BY BUYRATEID,LANE_NO";
                                        
          orgCountryQuery          =  " SELECT LOCATIONID,COUNTRYID FROM  FS_FR_LOCATIONMASTER WHERE LOCATIONID IN('"+orign+"')";
          destCountryQuery         =  " SELECT LOCATIONID,COUNTRYID FROM  FS_FR_LOCATIONMASTER WHERE LOCATIONID IN('"+destination+"') ";
          
          pStmt1       =   connection.prepareStatement(orgCountryQuery);
          rs1 = pStmt1.executeQuery();
          while(rs1.next())
          {
            originTable.put(rs1.getString("LOCATIONID"),rs1.getString("COUNTRYID"));
          }
          if(rs1!=null)rs1.close();
          if(pStmt1!=null)pStmt1.close();
          
          pStmt1       =   connection.prepareStatement(destCountryQuery);
          rs1 = pStmt1.executeQuery();
          while(rs1.next())
          {
            destTable.put(rs1.getString("LOCATIONID"),rs1.getString("COUNTRYID"));
          }
          pStmt       =   connection.prepareStatement(sqlQueryListWt);
          
          pStmt.setString(1,sellRatesDob.getCurrencyId());
          pStmt.setString(2,sellRatesDob.getShipmentMode());
          pStmt.setString(3,sellRatesDob.getWeightBreak());
          pStmt.setString(4,sellRatesDob.getRateType());
          pStmt.setString(5,loginBean.getTerminalId());
          rs = pStmt.executeQuery();
          while(rs.next())
          {
            String boundryValues  = rs.getString("WEIGHT_BREAK_SLAB"); 
            if(!boundryList.contains(boundryValues))
            {
                
                boundryList.add(boundryValues);
            }
          }
          slabValues.add(boundryList);
          if(rs!=null)
            rs.close();
          if(pStmt!=null)
            pStmt.close();
            
          pStmt       =   connection.prepareStatement(sqlQueryList);
      
          pStmt.setString(1,sellRatesDob.getCurrencyId());
          pStmt.setString(2,sellRatesDob.getShipmentMode());
          pStmt.setString(3,sellRatesDob.getWeightBreak());
          pStmt.setString(4,sellRatesDob.getRateType());
          pStmt.setString(5,loginBean.getTerminalId());
          rs = pStmt.executeQuery();
          while(rs.next())
          {
              qmsSellRatesDob = new QMSSellRatesDOB();
              qmsSellRatesDob.setBuyRateId(rs.getString("BUYRATEID"));
              qmsSellRatesDob.setCarrier_id(rs.getString("CARRIER_ID"));
              qmsSellRatesDob.setOrigin(rs.getString("ORIGIN"));
              
              qmsSellRatesDob.setOriginCountry((String)originTable.get(rs.getString("ORIGIN")));
              qmsSellRatesDob.setDestination(rs.getString("DESTINATION"));
              
              qmsSellRatesDob.setDestinationCountry((String)destTable.get(rs.getString("DESTINATION")));
              qmsSellRatesDob.setServiceLevel(rs.getString("SERVICE_LEVEL"));
              qmsSellRatesDob.setTransitTime(rs.getString("TRANSIT_TIME"));
              qmsSellRatesDob.setFrequency(rs.getString("FREQUENCY"));
              qmsSellRatesDob.setLanNumber(rs.getInt("LANE_NO"));
              qmsSellRatesDob.setInvalidate("INVALIDATE");
              key=rs.getString("BUYRATEID")+rs.getString("LANE_NO");
              
              
              if(map.containsKey(key))
              {
                  qmsSellRatesDob     = (QMSSellRatesDOB)map.get(key);
                  ArrayList tempList  = qmsSellRatesDob.getBoundryList();
                  boundryDOB          = new QMSBoundryDOB();
                  boundryDOB.setChargeRate(rs.getDouble("CHARGERATE"));
                  boundryDOB.setWeightBreak(rs.getString("WEIGHT_BREAK_SLAB"));
                  boundryDOB.setLowerBound(rs.getLong("LOWERBOUND"));
                  boundryDOB.setUperBound(rs.getLong("UPPERBOUND"));
                  tempList.add(boundryDOB);
                  qmsSellRatesDob.setBoundryList(tempList);
                  map.put(key,qmsSellRatesDob);
              }
              else
              {
                  boundryDOB  = new QMSBoundryDOB();
                  boundryDOB.setChargeRate(rs.getDouble("CHARGERATE"));
                  boundryDOB.setWeightBreak(rs.getString("WEIGHT_BREAK_SLAB"));
                  boundryDOB.setLowerBound(rs.getLong("LOWERBOUND"));
                  boundryDOB.setUperBound(rs.getLong("UPPERBOUND"));
                  boundryValueList =  new ArrayList();
                  boundryValueList.add(boundryDOB);
                  qmsSellRatesDob.setBoundryList(boundryValueList);
                  map.put(key,qmsSellRatesDob);
              }             
          }
          slabValues.add(map);
      }
      catch(SQLException sqle)
      {
          //Logger.error(FILE_NAME,"SQLEXception in getFCLRatesVales()-->"+sqle.toString());
          logger.error(FILE_NAME+"SQLEXception in getFCLRatesVales()-->"+sqle.toString());
          sqle.printStackTrace();
          throw new EJBException(sqle.toString());
      }
      catch(Exception e)
      {
        //Logger.error(FILE_NAME,"EXception in getFCLRatesVales()-->"+e.toString());
        logger.error(FILE_NAME+"EXception in getFCLRatesVales()-->"+e.toString());
        e.printStackTrace();
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
              if(pStmt!=null)
                {pStmt.close();}
              if(pStmt1!=null)
                {pStmt1.close();}
              if(connection!=null)
                {connection.close();}
          }
          catch(Exception ex)
          {
              //Logger.error(FILE_NAME,"Exception caught :: finally :: getFCLRatesVales() " + ex.toString() );
              logger.error(FILE_NAME+"Exception caught :: finally :: getFCLRatesVales() " + ex.toString() );
          }
		  
		  }
      return slabValues;
  }
 
  public void invalidateBuyRateDtls(ArrayList list)
	{
		RatesDao dao	=	null;

	  try{

		dao		=	new		RatesDao();
   	dao.invalidateBuyRateDtls( list);

	  }catch(Exception e)
		{
      e.printStackTrace();
      throw new EJBException();
		}finally{

			

		}

	}
	public void modifyFlatRates(ArrayList list,ESupplyGlobalParameters loginBean)
	{
			RatesDao dao	=	null;
     
		  try{
			  	dao	=	new	RatesDao();
		
				dao.modifyFlatRates( list,loginBean);

		  }catch(Exception e)
		  {
    			e.printStackTrace();
          throw new EJBException();
		  }


		
	}
  
   private HashMap validateFlatDetailDOB(RateDOB rateDOB,Connection connection,ESupplyGlobalParameters loginbean)
   {
     CallableStatement		cStmt			      = null;
     PreparedStatement    pStmt           = null;
     ResultSet            rs              = null;
     ArrayList            flatRateList    = null;
     String               shipmentMode    = null;
     String               weightBreak     = null;
     String               rateType        = null;
     int                  flatRateListSize= 0;
     FlatRatesDOB         flatRatesDOB    = null;
     String               terminalId      = null;
     String               returnValue     = null;
     StringBuffer         sb_errorMsg     = null;
     HashMap             map             = null;
     ArrayList           list_valid      = null;
     ArrayList           list_invalid    = null;     
     String              frequency       = null;
     String              uom             = null;
     String[]			 tmpDensRation	=null; // Added by Gowtham.	
     
     try
     {

          map             = new HashMap();
          list_valid    = new ArrayList();
          list_invalid = new ArrayList();      
          sb_errorMsg  = new StringBuffer("");
          terminalId   = loginbean.getTerminalId();
          shipmentMode = rateDOB.getShipmentMode();
          weightBreak  = rateDOB.getWeightBreak();
          rateType     = rateDOB.getRateType();
          flatRateList = rateDOB.getRateDtls();
          flatRateListSize = flatRateList.size();
          uom          = rateDOB.getUom();
          
          cStmt   =   connection.prepareCall("{CALL TRUNCATE_PROC()} "); 
          cStmt.execute();
            
          if(cStmt!=null)
            cStmt.close();
          
          cStmt  = connection.prepareCall("{ call PKG_QMS_BUYRATES.QMS_VALIDATE_BUYRATE(?,?,?,?,?,?,?,?,?,?,?,?,?)}");
          pStmt  = connection.prepareStatement("SELECT NOTES FROM Temp_Charges");
          //logger.info("flatRateListSize::"+flatRateListSize);
          
          for(int i=0;i<flatRateListSize;i++)
          {
          
            flatRatesDOB = (FlatRatesDOB)flatRateList.get(i);
            
            tmpDensRation = flatRatesDOB.getDensityRatio().split(","); // Added by Gowtham
            cStmt.clearParameters();
            
            //cStmt.registerOutParameter(1,java.sql.Types.VARCHAR);
            cStmt.setString(1,shipmentMode);
            cStmt.setString(2,flatRatesDOB.getOrigin());
            cStmt.setString(3,flatRatesDOB.getDestination());
            cStmt.setString(4,flatRatesDOB.getServiceLevel());
            cStmt.setString(5,flatRatesDOB.getCarrierId());
            cStmt.setTimestamp(6,flatRatesDOB.getEffDate());
            
            if(flatRatesDOB.getValidUpto()!=null)
              cStmt.setTimestamp(7,flatRatesDOB.getValidUpto());
            else
              cStmt.setNull(7,Types.DATE);
              
            cStmt.setString(8,weightBreak);
            cStmt.setString(9,rateType);
            cStmt.setString(10,terminalId);
            
            frequency = flatRatesDOB.getFrequency();
            frequency = frequency.replace(',','~');
            cStmt.setString(11,frequency);
           // cStmt.setString(12,flatRatesDOB.getDensityRatio());
            cStmt.setString(12, tmpDensRation[i]); // Added by Gowtham for Buyrate Upload Issue.
            cStmt.setString(13,uom);
            //cStmt.execute();
            cStmt.addBatch();
            
            //returnValue = cStmt.getString(1);
            
            //System.out.println("returnValue"+returnValue);
            
              /*if("1".equals(returnValue))
                { sb_errorMsg.append("Origin Location is Not Valid \n");}
              else if("2".equals(returnValue))
                { sb_errorMsg.append("Destination Location is Not Valid \n");}
              else if("3".equals(returnValue))
                { sb_errorMsg.append("Carrier Id is Not Valid\n");}
              else if("4".equals(returnValue))
                { sb_errorMsg.append("ServiceLevel is Not Valid\n");}
              else if("5".equals(returnValue))
                { sb_errorMsg.append("Data Already defined at HigherLevel\n");}
              else if("7".equals(returnValue))
                { sb_errorMsg.append("Density is Not Valid\n");}
              else if("0".equals(returnValue))
                { sb_errorMsg.append("Error while processing the data\n");}
                
              flatRatesDOB.setSlNo(i+1);  */
                
              //Logger.info(FILE_NAME,"returnValue:"+returnValue);  
                
             /*if("6".equals(returnValue))
              {
               flatRatesDOB.setRemarks("");
               list_valid.add(flatRatesDOB);                
              }else
              {
               flatRatesDOB.setRemarks(sb_errorMsg.toString());
               list_invalid.add(flatRatesDOB);              
              }
            
            sb_errorMsg.delete(0,sb_errorMsg.length());*/
          }
        cStmt.executeBatch();
        
        flatRatesDOB  = null;
        rs = pStmt.executeQuery();
        int count = 0;
        
        while(rs.next())
        {
          returnValue = rs.getString("NOTES");
          
          flatRatesDOB = (FlatRatesDOB)flatRateList.get(count);
          
          if("1".equals(returnValue))
            { sb_errorMsg.append("Origin Location is Not Valid \n");}
          else if("2".equals(returnValue))
            { sb_errorMsg.append("Destination Location is Not Valid \n");}
          else if("3".equals(returnValue))
            { sb_errorMsg.append("Carrier Id is Not Valid\n");}
          else if("4".equals(returnValue))
            { sb_errorMsg.append("ServiceLevel is Not Valid\n");}
          else if("5".equals(returnValue))
            { sb_errorMsg.append("Data Already defined at HigherLevel\n");}
          else if("7".equals(returnValue))
            { sb_errorMsg.append("Density is Not Valid\n");}
          else if("0".equals(returnValue))
            { sb_errorMsg.append("Error while processing the data\n");}
          
          if("6".equals(returnValue))
          {
           flatRatesDOB.setRemarks("");
           list_valid.add(flatRatesDOB);                
          }else
          {
           flatRatesDOB.setRemarks(sb_errorMsg.toString());
           list_invalid.add(flatRatesDOB);              
          }
            
          sb_errorMsg.delete(0,sb_errorMsg.length());
            
          flatRatesDOB.setSlNo(count+1);
          count++;
        }
         
        map.put("VALID",list_valid);
        map.put("INVALID",list_invalid);
       
       
     }catch(SQLException e)
     {
       e.printStackTrace();
       //Logger.info(FILE_NAME,"in validateFlatDetailDOB()"+e);
       logger.info(FILE_NAME+"in validateFlatDetailDOB()"+e);
       throw new EJBException();
     }finally
     {
        try{
           if(cStmt!=null)
            { cStmt.close();}
          if(rs!=null)
            rs.close();
          if(pStmt!=null)
            pStmt.close();
        }catch(SQLException e)
        {
          //Logger.info(FILE_NAME," in finally block validateFlatDetailDOB()"+e);
          logger.info(FILE_NAME+" in finally block validateFlatDetailDOB()"+e);
          throw new EJBException();
        }
     }
     return map;
   }
  
  
  
  
  private HashMap validateFlatDetailDOB(ArrayList lanes,Connection connection)
   {
      PreparedStatement		pStmt			      = null;	
      PreparedStatement		pStmt2		      = null;	
      PreparedStatement		pStmt3		      = null;	
      ResultSet           rs              = null;
      ResultSet           rs2              = null;
      ResultSet           rs3              = null;
      String              loc_qry         = null;
      String              date_qry        = null;
      String              ser_qry         = null;
      StringBuffer        sb_errorMsg     = null;
      FlatRatesDOB        flatRatesDOB    = null;
      boolean             flag            = false;
      String              car_qry         = null;  
      HashMap             map             = null;
      ArrayList           existingList    = null;
      ArrayList           nonExistingList = null;
      int 					laneSize	= 0;
    try
    {
      map             = new HashMap();
      existingList    = new ArrayList();
      nonExistingList = new ArrayList();   
      laneSize		=	lanes.size();

     sb_errorMsg = new StringBuffer();
    
    loc_qry = "  SELECT LOCATIONID FROM FS_FR_LOCATIONMASTER WHERE LOCATIONID=? ";
    
    ser_qry = "  SELECT SERVICELEVELID FROM FS_FR_SERVICELEVELMASTER WHERE SERVICELEVELID=? ";
    
    car_qry = "  SELECT DISTINCT CARRIERID FROM FS_FR_CAMASTER WHERE SHIPMENTMODE IN (1,2,3,4,5,6,7) and  CARRIERID=? ";
    
    
    
    
    
    
    
    pStmt       = connection.prepareStatement(loc_qry);
    pStmt2      = connection.prepareStatement(ser_qry);
    pStmt3      = connection.prepareStatement(car_qry);
     
     for(int i=0;i<laneSize;i++)
     {
        sb_errorMsg.delete(0,sb_errorMsg.length());
        flag            = false;
        flatRatesDOB = (FlatRatesDOB)lanes.get(i);
          
     
        pStmt.setString(1,flatRatesDOB.getOrigin());       
        pStmt2.setString(1,flatRatesDOB.getServiceLevel()); 
        pStmt3.setString(1,flatRatesDOB.getCarrierId()); 
      
        
        rs =    pStmt.executeQuery();      
        if(!rs.next())
        {
          sb_errorMsg.append("Origin Location is Not Valid ");
          flag            = true;
        }
        
        if(rs!=null)
          { rs.close();}
        
        pStmt.clearParameters();
        pStmt.setString(1,flatRatesDOB.getDestination()); 
        
        rs =    pStmt.executeQuery();
        if(!rs.next())
        {
          sb_errorMsg.append("Destination Location is Not Valid ");
           flag            = true;
        }

        rs2 =   pStmt2.executeQuery();
        if(!rs2.next())
        {
          sb_errorMsg.append("ServiceLevel is Not Valid ");
          flag            = true;
        }
        
        rs3 =   pStmt3.executeQuery();        
        if(!rs3.next())
        {
           sb_errorMsg.append("Carrier Id is Not Valid ");
           flag            = true;
        }      
      
        flatRatesDOB.setSlNo(i+1);
        
        if(flag)  {
           flatRatesDOB.setRemarks(sb_errorMsg.toString());
           nonExistingList.add(flatRatesDOB);        
        }
        else{
          flatRatesDOB.setRemarks("");
           existingList.add(flatRatesDOB);        
        }
      
        if(rs!=null)
          rs.close();
         if(rs2!=null)
          rs2.close();  
        if(rs3!=null)
          rs3.close();      
       
     }
     map.put("EXISTS",existingList);
     map.put("NONEXISTS",nonExistingList);

  
    }catch(SQLException e)
    {
      e.printStackTrace();
      throw new EJBException ("Problem while fetching the Details <BR>");
    }
    catch(Exception e)
    {
      e.printStackTrace();
      throw new EJBException ("Problem while fetching the Details /n");
    }finally
    {
     try{
        if(rs!=null)
          rs.close();
        if(rs2!=null)
          rs2.close();  
        if(rs3!=null)
          rs3.close();     
        if(pStmt!=null)
          pStmt.close();
        if(pStmt2!=null)
          pStmt2.close();  
        if(pStmt3!=null)
          pStmt3.close();                  
          
         }catch(SQLException e)
         {
           e.printStackTrace(); 
           throw new EJBException(e.toString());
         }
      
    }
    return map;     
   }
  
  
  public String  validateWBreaks (ArrayList wtbreakList,String shipmentMode)
  {
    PreparedStatement pstmt = null;
    ResultSet         rs    = null;
    Connection    connection= null;
    
    StringBuffer  sb_remarks =null;         
    String sql   = null;
    String  s1 = null;
    try
    {
    
      sql = "SELECT   LIST_TYPE FROM QMS_LISTMASTER WHERE LIST_TYPE=? AND SHIPMENT_MODE=? ";
     
      connection           =    operationsImpl.getConnection();  
      sb_remarks = new StringBuffer();
      
      pstmt = connection.prepareStatement(sql);
      
      int wtbreakListSize = wtbreakList.size();
        for(int i=0;i<wtbreakListSize ; i++)
      {
        String s=(String)wtbreakList.get(i);
  
          //@@Added by kameswari for Surcharge Enhancement
        if(s.endsWith("F")||s.endsWith("S"))
        {
         
            s1 =  s.substring(s.length()-3,s.length());
          s = s.substring(0,s.length()-3);
        
        
        }
       
        if(s1!=null)
        {
          if(!(s1.equalsIgnoreCase("BAF")||s1.equalsIgnoreCase("CAF")||s1.equalsIgnoreCase("PSS")
          ||s1.equalsIgnoreCase("CSF")))
          {
             sb_remarks.append((String)wtbreakList.get(i)).append(" ");
          }
        }
        //@@Added by kameswari for Surcharge Enhancement
     //   logger.info("s"+s);
         //if(!"OVERPIVOT".equalsIgnoreCase((String)wtbreakList.get(i)))//@@Modified by kameswari for Surcharge Enhancement
         if(!"OVERPIVOT".equalsIgnoreCase(s))
         {
           pstmt.clearParameters();
           pstmt.setString(1,s);
           pstmt.setString(2,shipmentMode);
           rs = pstmt.executeQuery();
           
           if(!rs.next())
           {
             sb_remarks.append((String)wtbreakList.get(i)).append(" ");
             
           }
           
           if(rs!=null)
            { rs.close();}
         }
      }
      
      
      if(sb_remarks!=null && sb_remarks.length()>0)
          return " ContainersTypes : "+sb_remarks.toString()+" are Invalid";
      else
         return "";
      
      
    }catch(Exception e)
    {
      e.printStackTrace();
      throw new EJBException();
    }finally
    {
      try
      {
        if(rs!=null )
          { rs.close();}
        if(pstmt!=null )
          { pstmt.close();}
        if(connection!=null)
          { connection.close();}
          
      }catch(SQLException e)
      {
        e.printStackTrace();
      }
    }
  }
//Added by Mohan for issue no.219973 on 01122010 
  public HashMap getFlatSurchargeList(String shipmentMode)throws EJBException
  {
    PreparedStatement pstmt = null;
    ResultSet         rs    = null;
    Connection    connection= null;
    //ArrayList	alist = null;
    HashMap	map = null;
    String sql   = null;
    try
    {
      sql = "SELECT DISTINCT SURCHARGE_ID,SURCHARGE_DESC FROM QMS_SURCHARGE_MASTER X WHERE X.SHIPMENT_MODE=? ";
     
      connection           =    operationsImpl.getConnection();  
      pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, shipmentMode);
      rs = pstmt.executeQuery();
      map =  new HashMap();
       while(rs.next())
       {
    	   map.put(rs.getString(1),rs.getString(2));
       }
       
    }catch(Exception e)
    {
      e.printStackTrace();
      throw new EJBException();
    }finally
    {
      try
      {
        if(rs!=null )
          { rs.close();}
        if(pstmt!=null )
          { pstmt.close();}
        if(connection!=null)
          { connection.close();}
          
      }catch(SQLException e)
      {
        e.printStackTrace();
      }
    }
    return map; 
  }
  public TreeSet getListSurchargeList(String shipmentMode)throws EJBException
  {
    PreparedStatement pstmt = null;
    ResultSet         rs    = null;
    Connection    connection= null;
    //ArrayList	alist = null;
    TreeSet	tSet = null;
    String sql   = null;
    try
    {
      sql = "SELECT   LIST_TYPE FROM QMS_LISTMASTER WHERE SHIPMENT_MODE=? ";
     
      connection           =    operationsImpl.getConnection();  
      
      pstmt = connection.prepareStatement(sql);
      pstmt.setString(1, shipmentMode);
      rs = pstmt.executeQuery();
      tSet =  new TreeSet();
      while(rs.next())
      {
    	  tSet.add(rs.getString(1));
      }
       
    }catch(Exception e)
    {
      e.printStackTrace();
      throw new EJBException();
    }finally
    {
      try
      {
        if(rs!=null )
          { rs.close();}
        if(pstmt!=null )
          { pstmt.close();}
        if(connection!=null)
          { connection.close();}
          
      }catch(SQLException e)
      {
        e.printStackTrace();
      }
    }
    return tSet; 
  }
  //End by Mohan for issue no.219973 on 01122010 
  public RateDOB upLoadBuyRates(RateDOB rateDOB,ESupplyGlobalParameters loginBean)
  {
    Connection        connection  = null;
    StringBuffer      remarks     = null;
    HashMap           hashMap     = null;
    ArrayList         flatList    = null;
    ArrayList         invalidList = null;
    RatesDao          ratesDao    = null;
    String[]		  frCurrency  = null; // Added by Gowtham.
    int				  tempFrcCnt  = 0;
    boolean		      flag = false ;
    
    try
    {

        remarks   = new StringBuffer("");
        connection = operationsImpl.getConnection();
        // Added by Gowtham for Buyrates Upload Issue.
        frCurrency = rateDOB.getCurrency().split(","); 
        tempFrcCnt = frCurrency.length;
        
        for(int i=0;i<tempFrcCnt;i++)
        {
        	 if(validateCurrency(frCurrency[i],connection))
        		 flag = true;
        	 else 
        		 flag = false;
        }
        if(flag)
        {
          
          //hashMap = validateFlatDetailDOB(rateDOB.getRateDtls(),connection);
          hashMap = validateFlatDetailDOB(rateDOB,connection,loginBean); 
          
          flatList   = (ArrayList)hashMap.get("VALID");
          invalidList= (ArrayList)hashMap.get("INVALID");
//          System.out.println(""+((invalidList!=null)?invalidList.size():99999));
         
          if((invalidList==null || invalidList.size()<=0 ) &&  flatList!=null && flatList.size()>0)
          {
             
              ratesDao  = new RatesDao();
              ratesDao.upLoadcreateBuyFlatRate(rateDOB,flatList,connection);
          }
          
          rateDOB.setRateDtls(flatList);            
          rateDOB.setInvalidList(invalidList);
            
        }else
        {
          rateDOB.setRemarks("Invalid currency Id");
        }
        
    }catch(SQLException e)
    {
        //Logger.info(FILE_NAME," in upLoadBuyRates(RateDOB rateDOB,ESupplyGlobalParameters loginBean)"+e);
        logger.info(FILE_NAME+" in upLoadBuyRates(RateDOB rateDOB,ESupplyGlobalParameters loginBean)"+e);
        throw new EJBException();
    }
    catch(Exception e)
    {
      e.printStackTrace();
      //Logger.info(FILE_NAME," in upLoadBuyRates(RateDOB rateDOB,ESupplyGlobalParameters loginBean)"+e);
      logger.info(FILE_NAME+" in upLoadBuyRates(RateDOB rateDOB,ESupplyGlobalParameters loginBean)"+e);
      throw new EJBException();
    }finally
    {
      try
      {
        if(connection!=null)
          { connection.close();}
      }catch(SQLException e)
      {
        //Logger.info(FILE_NAME," in upLoadBuyRates(RateDOB rateDOB,ESupplyGlobalParameters loginBean)"+e);
        logger.info(FILE_NAME+" in upLoadBuyRates(RateDOB rateDOB,ESupplyGlobalParameters loginBean)"+e);
        throw new EJBException();
      }
    }
    
    return rateDOB;
  }
  
  
  
/*  public HashMap upLoadBuyRates(ArrayList rateList ,boolean addModFlag,ESupplyGlobalParameters loginBean)
  {                
                String                      msg               =   "";
                RateDOB                     rateDOB           =   null;
                FlatRatesDOB                flatRatesDOB      =   null;
                ArrayList                existingBuyRateList  =   new ArrayList(5);
                ArrayList             nonExistingBuyRateList  =   new ArrayList(5);
                HashMap                     totalMap          =   new HashMap(2,2);
                ArrayList                  flatList           =   null;
                String                     sqlStrDtl          =   null;
                InitialContext             ictxt              =   null;
                ArrayList                  list               =   null;
                HashMap                hashMap                =   new HashMap(2,2);
                RatesDao               dao                    =   new RatesDao();
            try{
                        Logger.info(FILE_NAME," Inside UploadBuyRates"+addModFlag);                   
                        rateDOB     =     (RateDOB)rateList.get(0);
                        //hashMap     =     validateFlatDetailDOB(rateDOB.getRateDtls(),rateDOB);
                        flatList = (ArrayList)hashMap.get("EXISTS");                        
                        rateDOB.setRateDtls(flatList);                        
                        if(flatList.size()>0){
                        msg         =     validateDetailRateDOB(rateDOB.getRateDtls(),rateDOB);
                        if((msg !=null) && !"".equals(msg))
                           existingBuyRateList.add(rateDOB);
                        else
                           nonExistingBuyRateList.add(rateDOB);
                           rateDOB.setRemarks(msg);
                           Logger.info(FILE_NAME,"nonExistingBuyRateList     "+nonExistingBuyRateList.size());
                           Logger.info(FILE_NAME,"existingBuyRateList     "+existingBuyRateList.size());
                           Logger.info(FILE_NAME,"msg 1234    "+msg);                          
                    if(addModFlag)
                    {                   
                        for(int i=0;i<nonExistingBuyRateList.size();i++){
                           rateDOB     =     (RateDOB)nonExistingBuyRateList.get(i);                            
                           msg         =     crateBuyFlatRate(rateDOB,rateDOB.getRateDtls());                         
                           Logger.info(FILE_NAME,"msg     "+msg);                        
                        }
                    }
                    else
                    {
                      Logger.info(FILE_NAME,"Inside else block"+addModFlag);
                      Logger.info(FILE_NAME,"existingBuyRateList.size()    "+existingBuyRateList.size());
                      for(int i=0;i<existingBuyRateList.size();i++)
                      {
                           rateDOB     =     (RateDOB)existingBuyRateList.get(i);   
                           list     =     validateUploadList(rateDOB.getRateDtls(),rateDOB);
                           Logger.info(FILE_NAME,"After Validate    "+list.size());
                           if(list.size()>0)
                              modifyFlatRates(list,loginBean);                         
                           Logger.info(FILE_NAME,"msg     "+msg);
                      }
                      for(int i=0;i<nonExistingBuyRateList.size();i++){
                           rateDOB     =     (RateDOB)nonExistingBuyRateList.get(i);                            
                           msg         =     dao.upLoadcreateBuyFlatRate(rateDOB,rateDOB.getRateDtls());                         
                           Logger.info(FILE_NAME,"msg     "+msg);                        
                      }
                    }  
                    }
                        nonExistingBuyRateList = (ArrayList)hashMap.get("NONEXISTS");
                        existingBuyRateList    = (ArrayList)hashMap.get("EXISTS");
                        hashMap.put("EXISTS",existingBuyRateList);
                        hashMap.put("NONEXISTS",nonExistingBuyRateList);                    
            }
            catch(Exception	cnfe){
              cnfe.printStackTrace();              
              throw new javax.ejb.EJBException("Exception in	uploadBuyRateDetails QMSSETUP SESSION BEAN  "+cnfe.toString());              
            }
            finally{
              
            }
                return hashMap;               
  }
   
  */
 /* private ArrayList validateUploadList(ArrayList list,RateDOB rateDOB)
  {
                OperationsImpl              operationsImpl    =   new OperationsImpl();
                Connection                  connection        =	  null;
                PreparedStatement           pStmt             =	  null;
                ResultSet                   rs                =   null;
                PreparedStatement           pStmtDtl          =	  null;
                FlatRatesDOB                flatRatesDOB      =   null;
                String                      sqlDtl            =   "SELECT A.BUYRATEID FROM QMS_BUYRATES_MASTER B, QMS_BUYRATES_DTL A WHERE "+
                                                                  " B.BUYRATEID = A.BUYRATEID AND A.ORIGIN=? AND A.DESTINATION =? AND A.SERVICE_LEVEL = ? "+
                                                                  " AND B.CARRIER_ID=? AND SHIPMENT_MODE =? AND WEIGHT_BREAK=? AND RATE_TYPE=?  AND "+
                                                                  " ((A.EFFECTIVE_FROM BETWEEN ? AND ?) OR(A.VALID_UPTO BETWEEN ? AND ?) )";
                ArrayList                   tempList          =   new ArrayList();
                String                      buyRateId         =   null;
                ArrayList                   chargeList        =   new ArrayList();
                Hashtable                   flatTable         =   new Hashtable();                                
                ArrayList                   chargeRateList    =   new ArrayList();    
                ESupplyDateUtility utility        =  new ESupplyDateUtility();
    try
    {
        
          connection      =  operationsImpl.getConnection();
          pStmt           =  connection.prepareStatement(sqlDtl); 
          sqlDtl          =  "SELECT CHARGERATE FROM QMS_BUYRATES_DTL WHERE BUYRATEID = ? AND LANE_NO = ? ORDER BY LINE_NO";
          pStmtDtl           =  connection.prepareStatement(sqlDtl);
          for(int k=0;k<list.size();k++)
          {
              Logger.info(FILE_NAME,"Inside validate   "+list.get(k));
              flatRatesDOB   =  (FlatRatesDOB)list.get(k);          
              pStmt.setString(1,flatRatesDOB.getOrigin()); 
              pStmt.setString(2,flatRatesDOB.getDestination()); 
              pStmt.setString(3,flatRatesDOB.getServiceLevel()); 
              pStmt.setString(4,flatRatesDOB.getCarrierId()); 
              pStmt.setString(5,rateDOB.getShipmentMode()); 
              pStmt.setString(6,rateDOB.getWeightBreak()); 
              pStmt.setString(7,rateDOB.getRateType()); 
              pStmt.setTimestamp(8,flatRatesDOB.getEffDate());
              pStmt.setTimestamp(9,flatRatesDOB.getValidUpto());
              pStmt.setTimestamp(10,flatRatesDOB.getEffDate());
              pStmt.setTimestamp(11,flatRatesDOB.getValidUpto());
              rs   =  pStmt.executeQuery();
              
              if(rs.next())      buyRateId    =  rs.getString("BUYRATEID");                        
              
              Logger.info(FILE_NAME,"Inside validate buyRateID  "+buyRateId);
              
              if(rs!=null)rs.close();
              chargeRateList        =    flatRatesDOB.getChargeRateList();
                
                  Logger.info(FILE_NAME,"Inside validate frtList.size()  "+chargeRateList.size());
                  pStmtDtl.clearParameters();
                  pStmtDtl.setString(1,buyRateId);                  
                  pStmtDtl.setInt(2,flatRatesDOB.getLaneNo());                  
                  rs   =  pStmtDtl.executeQuery();
                for(int j=0;j<chargeRateList.size();j++)
                { 
                  rs.next();
                  Logger.info(FILE_NAME,"Inside validate rs.getDouble(CHARGERATE)  "+rs.getDouble("CHARGERATE"));
                  Logger.info(FILE_NAME,"Inside validate ((Double)frtHash.get(frtList.get(i))).toString())  "+(new Double((String)chargeRateList.get(j)).doubleValue()));
                    if(!( rs.getString("CHARGERATE").equalsIgnoreCase((String)chargeRateList.get(j))))
                    {   
                          Logger.info(FILE_NAME,"buyRateId 1234 "+buyRateId);                  
                          flatRatesDOB.setBuyrateId(buyRateId);                      
                          tempList.add(flatRatesDOB);                
                          break;
                    }                    
              }
              if(rs!=null)rs.close();
      }            
          Logger.info(FILE_NAME,"Inside validate tempList  "+tempList.size());
       rateDOB.setRateDtls(tempList);
    }
    catch(Exception e)
    {
      e.printStackTrace();
      Logger.error(FILE_NAME,"Error in validate UpLoad List"+e.toString());
    }
    finally
    {
      ConnectionUtil.closeConnection(connection,pStmt,rs);
    }
  
    return tempList;
  }
  */
  
  /**
   * 
   * @param sellRatesDob
   * @param loginBean
   * @param operation
   * @return 
   */
 public ArrayList getSellRatesValues(QMSSellRatesDOB sellRatesDob,ESupplyGlobalParameters loginBean,String operation)
  {
      ArrayList                   arrayList             =   null;
      //HashMap                     flatRatesMap          =   null;
      ArrayList                   flatRateList             =   null;
      RatesDao             rateDAO          =   new RatesDao();
      try
      {
         
          arrayList                     =   new ArrayList();
          //System.out.println("6666666222223333333 :: "+sellRatesDob.getConsoleType());
          if(sellRatesDob!=null)
          {   
            arrayList.add(sellRatesDob);
            flatRateList = rateDAO.getRateDetails(sellRatesDob,loginBean,operation);
            
            arrayList.add(flatRateList);
            
            /*if("1".equals(sellRatesDob.getShipmentMode()) || ("2".equals(sellRatesDob.getShipmentMode()) && "LCL".equals(sellRatesDob.getConsoleType())) || ("4".equals(sellRatesDob.getShipmentMode()) && "LTL".equals(sellRatesDob.getConsoleType())))
            {
              System.out.println("3333333333333 :: "+sellRatesDob.getWeightBreak());
              if("Flat".equalsIgnoreCase(sellRatesDob.getWeightBreak()))
              {
                  arrayList.add(sellRatesDob);
                  flatRatesMap = rateDAO.getFlatRatesVales(sellRatesDob,loginBean,operation);
                  arrayList.add(flatRatesMap);
              }
              else if("Slab".equalsIgnoreCase(sellRatesDob.getWeightBreak()))
              {
                  arrayList.add(sellRatesDob);
                  headerList = rateDAO.getSlabRatesVales(sellRatesDob,loginBean,operation);
                  arrayList.add(headerList);
              }
              else if("List".equalsIgnoreCase(sellRatesDob.getWeightBreak()))
              {
                  arrayList.add(sellRatesDob);
                  headerList = rateDAO.getListRatesVales(sellRatesDob,loginBean,operation);
                  arrayList.add(headerList);
              }
            }
            else
            {
                  arrayList.add(sellRatesDob);
                  headerList = rateDAO.getFCLRatesVales(sellRatesDob,loginBean,operation);
                  arrayList.add(headerList);
            }*/
          }     
          
      }
      catch(SQLException sqle)
      {
          //Logger.error(FILE_NAME,"SQLEXception in getSellRatesValues()-->"+sqle.toString());
          logger.error(FILE_NAME+"SQLEXception in getSellRatesValues()-->"+sqle.toString());
          sqle.printStackTrace();
          throw new EJBException(sqle.toString());
      }
      catch(Exception e)
      {
        //Logger.error(FILE_NAME,"EXception in getSellRatesValues()-->"+e.toString());
        logger.error(FILE_NAME+"EXception in getSellRatesValues()-->"+e.toString());
        e.printStackTrace();
        throw new EJBException(e.toString());
      }
    return arrayList;
  }
  
  private boolean validateCurrency(String currency,Connection connection)throws SQLException
  {
     
      PreparedStatement		pStmt1		      = null;	
      ResultSet           rs1              = null;
      
      boolean             flag            = false;
      String              cur_qry         = "SELECT	CURRENCYID FROM	FS_COUNTRYMASTER WHERE CURRENCYID = ?";  
    try
    {
      pStmt1      = connection.prepareStatement(cur_qry);  
      pStmt1.setString(1,currency); 
      rs1 =   pStmt1.executeQuery();
      if(rs1.next())
      {        
        flag            = true;
      }
    }
    catch(Exception e)
    {
      e.printStackTrace();
      flag = false;
      throw new SQLException();
    }
    finally
    {
      try{
          if(rs1!=null)
            { rs1.close();}
          if(pStmt1!=null)
            { pStmt1.close();}
       
      }catch(SQLException e)
      {
        e.printStackTrace();
        //Logger.info(FILE_NAME," in validateCurrency(String currency,Connection connection)"+e);
        logger.info(FILE_NAME+" in validateCurrency(String currency,Connection connection)"+e);
        flag = false;
        throw new SQLException();
      }
    }
    return flag;
  }
 
  public boolean validateCurrency(String currency)
  {
     
      PreparedStatement		pStmt1		      = null;	
      ResultSet           rs1              = null;
      Connection				  connection		  = null;
      OperationsImpl      impl            = null;
      boolean             flag            = false;
      String              cur_qry         = "SELECT	CURRENCYID FROM	FS_COUNTRYMASTER WHERE CURRENCYID = ?";  
    try
    {
      impl       = new OperationsImpl();
      connection	=	impl.getConnection();
      pStmt1      = connection.prepareStatement(cur_qry);  
      pStmt1.setString(1,currency); 
      rs1 =   pStmt1.executeQuery();
      if(rs1.next())
      {        
        flag            = true;
      }
    }
    catch(Exception e)
    {
      e.printStackTrace();
      return false;
    }
    finally
    {
      ConnectionUtil.closeConnection(connection,pStmt1,rs1);
    }
    return flag;
  } 
  
  
  
public String processExcel(String fileName,ESupplyGlobalParameters loginBean) throws EJBException
{
    long                startTime           = System.currentTimeMillis(); 
		String              s1                  = "";
    PreparedStatement   pstmtMainHeader     = null;
  	PreparedStatement   pstmtDetails        = null;
		PreparedStatement   pstmtDetailsData    = null;
    PreparedStatement   MainHeader          = null;
  	PreparedStatement   Details             = null;
		PreparedStatement   DetailsData         = null;
		Connection          connection          = null;
    OperationsImpl      operationsImpl      = null;
    String              dateFormat          = null;
    String              deleteDtldata       = "DELETE FROM QMS_STG_BUYRATES_DETAILS_DATA";
    String              deleteDtl           = "DELETE FROM QMS_STG_BUYRATES_DETAILS";
    String              deleteHeader        = "DELETE FROM QMS_STG_BUYRATES_MAIN_HEADER";
    String              selectMainHeader    = "SELECT WEIGHT_BREAK FROM QMS_STG_BUYRATES_MAIN_HEADER";
    ResultSet           rs                  = null;
    PreparedStatement   pstmtHeader         = null; //@@Added by Kameswari on 15/04/09
	  String              wtbreak             = null;
    String              returnstr           = null;
    CallableStatement   cstmt               = null;
    double              time                = 0.0;
    String              data                = null;
    try
		{
			String s="";
			String mainHeaderData[] =new String[8];			 
			int rows=0;
			int cols=0;
			int notesIndex=0;
			int batchCount=0;
			HSSFRow row = null;
			HSSFCell cell=null;
      
   		InputStream myxls = new FileInputStream(fileName);
   		HSSFWorkbook wb     = new HSSFWorkbook(myxls);
			HSSFSheet sheet = wb.getSheetAt(0);   
			rows=sheet.getPhysicalNumberOfRows();
			dateFormat  = loginBean.getUserPreferences().getDateFormat();
		  String queryMainHeader   = "INSERT INTO QMS_STG_BUYRATES_MAIN_HEADER (SHIPMENT_MODE,CURRENCY,WEIGHT_BREAK,WEIGHT_CLASS,RATE_TYPE,UOM,CONSOLETYPE,DENSITY_RATIO,TERMINALID) VALUES (?,?,?,?,?,?,?,?,?)";			
		//	String queryDetails      = "INSERT INTO QMS_STG_BUYRATES_DETAILS (ROW_ID,ORIGIN,DESTINATION,CARRIER_ID,SERVICELEVEL,FREQUENCY,TRANSIT,EFFECTIVE_FROM,VALID_UPTO,NOTES) VALUES (?,?,?,?,?,?,?,?,?,?)";			
			String queryDetails      = "INSERT INTO QMS_STG_BUYRATES_DETAILS (ROW_ID,ORIGIN,DESTINATION,CARRIER_ID,SERVICELEVEL,FREQUENCY,TRANSIT,EFFECTIVE_FROM,VALID_UPTO,NOTES) VALUES (?,?,?,?,?,?,?,TO_DATE(?),TO_DATE(?),?)";			
    	String queryDetailsData  = "INSERT INTO QMS_STG_BUYRATES_DETAILS_DATA (ROW_ID,LINE_NO,CONTAINER_NO,CONTAINERS_VALUE,LOWER_BOUND,UPPER_BOUND,RATE_DESCRIPTION) VALUES (?,?,?,?,?,?,?)";
			String        msgquery   = "SELECT ERROR_MSG FROM QMS_STG_BUYRATES_DETAILS";
		//	connection=ConnectionUtil.getConnection();
    	operationsImpl    = new OperationsImpl();
      connection        = operationsImpl.getConnection();
      DetailsData = connection.prepareStatement(deleteDtldata);
      Details = connection.prepareStatement(deleteDtl);
      MainHeader = connection.prepareStatement(deleteHeader);
      DetailsData.execute();
      Details.execute();
      MainHeader.execute();
			// long startTime1=System.currentTimeMillis(); 
		//For MainHeader
			row=sheet.getRow(1);
			for(int k=0;k<8;k++)
			{
			    cell=row.getCell((short)k);			  
			    mainHeaderData[k]=getCellData(cell,0);			  
				}
			pstmtMainHeader=connection.prepareStatement(queryMainHeader);
			
      insertMainHeaderData(mainHeaderData,pstmtMainHeader,loginBean);
		//End
		     pstmtHeader   =   connection.prepareStatement(selectMainHeader);
         rs            =   pstmtHeader.executeQuery(); 
      if(rs.next())
      {
          wtbreak   = rs.getString("WEIGHT_BREAK");
      }
		//To get columns count , column index for NOTES and containers ids
			 row=sheet.getRow(3);
   
			 cols=row.getPhysicalNumberOfCells();
   
			 int l=cols-1;
     
			 for(;l>=0;l--)
			 {
				 cell=row.getCell((short)l);				
				if("NOTES".equalsIgnoreCase(getCellData(cell,l)))
					break;
			  
			 }	
   
			 notesIndex=l+1;
   
			 String detailsData[] =new String[notesIndex];
			 String containersData[] =new String[notesIndex-9];
			 for(l=8;l<notesIndex-1;l++)
			 {
				 cell=row.getCell((short)l);
				 containersData[l-8]=getCellData(cell,l);
			 }
		//End
			
		//For Details	 
	          pstmtDetails= connection.prepareStatement(queryDetails);;
			  pstmtDetailsData=connection.prepareStatement(queryDetailsData); 
			 for (int i=4;i<=rows;i++) 
			 {
				 
				row=sheet.getRow(i);				
				//cols=row.getPhysicalNumberOfCells();
				//s=row.getRowNum() + " : ";			 
			    for (int j=0;j<notesIndex;j++ ) 
			    {
					     if(row!=null)
               {
                cell=row.getCell((short)j);
             
					      detailsData[j]=getCellData(cell,j);
               }
					     // s = s+ " -- " + detailsData[j];					 
         
                 if(j==6&&(cell.CELL_TYPE_NUMERIC==cell.getCellType())&&(detailsData[j].indexOf(".")!=-1)&&(Double.parseDouble(detailsData[j].substring(0, detailsData[j].indexOf(".")))<1))
                {
                   time =1/Double.parseDouble(detailsData[j]);
                   Double  t =new Double(time);
                   if(t.toString().indexOf(".")!=-1)
                   {
                        data =t.toString().substring(0, t.toString().indexOf("."));
                   }
                    if(data!=null)
                    {
                         detailsData[j] = data+":"+"00";
                    }
                }
                  if(j==6&&(cell.CELL_TYPE_NUMERIC==cell.getCellType())&&(detailsData[j].indexOf(".")!=-1)&&(!(Double.parseDouble(detailsData[j].substring(0, detailsData[j].indexOf(".")))<1)))
                 {
                       if(detailsData[j].indexOf(".")!=-1)
                       {
                         detailsData[j] =detailsData[j].substring(0, detailsData[j].indexOf("."));
                        }
                  }
                  if(j==5&&detailsData[j].indexOf(".")!=-1)
                  {
                     detailsData[j] =detailsData[j].substring(0, detailsData[j].indexOf("."));
                    }
                }
				    
				 //long EndTime1=System.currentTimeMillis();
				 //System.out.println("\n------->>>> M.SEC : "+(EndTime1-startTime1) +"  SEC : " + (EndTime1-startTime1)/(1000) + " MIN :  "+(EndTime1-startTime1)/(1000*60));
			
			    batchCount++;
       
			    insertDetails(detailsData,containersData,pstmtDetails ,pstmtDetailsData,notesIndex,i,dateFormat,wtbreak);
			    if(batchCount == 150)
			    {
			    	pstmtDetails.executeBatch();
			    	pstmtDetailsData.executeBatch();
			    	pstmtDetails.clearBatch();
			    	pstmtDetailsData.clearBatch();
			    	batchCount=0;
			    }
			   // s1=s1+s + "\n";			
			 }
				
			 if(batchCount >0)
			 {
				    pstmtDetails.executeBatch();
			    	pstmtDetailsData.executeBatch();
			    	pstmtDetails.clearBatch();
			    	pstmtDetailsData.clearBatch();
			    	batchCount=0;
			 }
       
          cstmt  = connection.prepareCall("{ ?=call QMS_BUY_RATES_UPLOAD_PKG.BUY_RATES_PROC(?)}");

           cstmt.registerOutParameter(1,Types.VARCHAR);
          cstmt.setString(2,loginBean.getTerminalId());
          cstmt.execute();
          returnstr = (String)cstmt.getString(1);
  
		 //End
     return returnstr;
		}
		catch(Exception e)
		{		
			logger.error("Exception");
			e.printStackTrace();
		}
		finally
		{
		  try
		  {			
				if(pstmtMainHeader!=null)
					pstmtMainHeader.close();
				if(pstmtDetails!=null)
					pstmtDetails.close();
				if(pstmtDetailsData!=null)
					pstmtDetailsData.close();
          	if(cstmt!=null)
					cstmt.close();
        if(connection!=null)
				connection.close();
        if(rs!=null)//Added by govind on 16-02-2010 for Connection Leakages
        	rs.close();
        if(pstmtHeader!= null)//Added by govind on 16-02-2010 for Connection Leakages
        	pstmtHeader.close();
        if (MainHeader!=null)//Added by govind on 16-02-2010 for Connection Leakages
        	MainHeader.close();
        if (DetailsData!=null) //Added by govind on 16-02-2010 for Connection Leakages
        	DetailsData.close();
        if (Details!=null)//Added by govind on 16-02-2010 for Connection Leakages
        	Details.close();
		  }
		  catch(Exception e)
		   {	
				logger.error("Error Occured  while closing Resources" + e);
			}
		}
		
		 long EndTime=System.currentTimeMillis(); 	
		 s1=" M.SEC : "+(EndTime-startTime) +"  SEC : " + (EndTime-startTime)/(1000) + " MIN :  "+(EndTime-startTime)/(1000*60);
return s1;
}
private String getCellData(HSSFCell cell,int j) throws Exception
{
//	String format =  loginBean.getUserPreferences().getDateFormat();
  String s="";
   ESupplyDateUtility        fomater           = null;
	try
	{	
		fomater = new ESupplyDateUtility();

 
    if(cell!=null)
		{
		    if(cell!=null )
		{
		  
        if (cell.CELL_TYPE_STRING == cell.getCellType()) 
        {
		         s = cell.getStringCellValue();
          }
        else if(cell.CELL_TYPE_NUMERIC== cell.getCellType())		 
        {
  // if( HSSFDateUtil.isCellDateFormatted(cell))
           if(j==6 || j==7)
           {
                    SimpleDateFormat format = new SimpleDateFormat();
                    format.applyPattern("dd/MMM/yy");
                   
                    s = format.format(cell.getDateCellValue());
             
                    // s =  cell.getDateCellValue();
           }
           else
           {
                 s= String.valueOf(cell.getNumericCellValue()).trim();
            }
           
        }
		   	   // 
		    else  if(cell.CELL_TYPE_BLANK== cell.getCellType())
				 s ="";		
		    else if(cell.CELL_TYPE_ERROR == cell.getCellType())
			     s ="";
		     else
			     s ="";
	    }
	    }
	}
	catch(Exception e)
	{
		e.printStackTrace();
		throw e;
	}
return s;
}

private void insertMainHeaderData(String[] records,PreparedStatement pstmt,ESupplyGlobalParameters loginBean) throws Exception
{
	try
	{
   
		pstmt.clearParameters();
		if(records !=null)
		{			
			pstmt.setString(1,records[0].trim());
			pstmt.setString(2,records[1].trim());
			pstmt.setString(3,records[2].trim());
			pstmt.setString(4,records[3].trim());
			pstmt.setString(5,records[4].trim());
			pstmt.setString(6,records[5].trim());
			pstmt.setString(7,records[6].trim());
      if(records[7]!=null&&records[7].trim().length()>0)
      {
        pstmt.setString(8,records[7].trim());
      }
      else
      {
        pstmt.setNull(8,Types.DATE);
      }
      pstmt.setString(9,loginBean.getTerminalId());//@@Added by Kameswari on 14/04/09
	     pstmt.execute();
		}	
	}
	catch(Exception e)
	{
		e.printStackTrace();
		throw e;
	}

}

private void insertDetails(String[] records,String[] containersData,PreparedStatement pstmt,PreparedStatement pstmt1,int notesIndex,int rowIndex,String dateFormat,String wtbreak) throws Exception
{
	    java.sql.Timestamp				validupTo          = null;
      java.sql.Timestamp				effectiveFrom      = null;
      ESupplyDateUtility        fomater            = null;




       int                      temp               = 0;   
 	try
	{
		   fomater			      =   new ESupplyDateUtility();
    pstmt.clearParameters();		
		if(records !=null)
		{			
		  pstmt.setString(1,String.valueOf(rowIndex));
			pstmt.setString(2,records[0].trim());
			pstmt.setString(3,records[1].trim());
			pstmt.setString(4,records[2].trim());
			pstmt.setString(5,records[3].trim());
			pstmt.setString(6,records[4].trim().toUpperCase());
			pstmt.setString(7,records[5].trim());
    
    	pstmt.setString(8,records[6].trim());

    if(records[7]!=null)
    {
        pstmt.setString(9,records[7].trim());
    }
    
			pstmt.setString(10,records[notesIndex-1].trim());
			pstmt.addBatch();
			//pstmt.execute();
			  for(int j=0;j<notesIndex-9;j++)
			{
          if(!(containersData[j].startsWith("FS")||containersData[j].startsWith("SS")
         ||containersData[j].startsWith("BAF")||containersData[j].startsWith("CAF")
         ||containersData[j].startsWith("CSF")||containersData[j].startsWith("SURCHARGE")
         ||containersData[j].startsWith("PSS")))
         {
            temp++;             
         }
      }
			for(int i=0;i<notesIndex-9;i++)
			{
				pstmt1.clearParameters();
				pstmt1.setString(1,String.valueOf(rowIndex));
				pstmt1.setInt(2,i);
      	pstmt1.setString(3,containersData[i].trim());
				pstmt1.setString(4,records[i+8].trim());	
        if("SLAB".equalsIgnoreCase(wtbreak))
        {
          if(i==1 || i==0)
         {
                 pstmt1.setInt(5,0);
             if(i==1)
                 pstmt1.setString(6, containersData[i+1]);  
             else
                 pstmt1.setInt(6,0);
                 
                 pstmt1.setString(7,"A FREIGHT RATE");
           }
           else
                    {
                     if(i==temp-1)
                      {
                   
                         pstmt1.setString(5,containersData[i]);  
                         pstmt1.setInt(6,100000);
                         pstmt1.setString(7,"A FREIGHT RATE");
                      }  
                      else
                      {
                       /*  if("FSBASIC".equalsIgnoreCase((String)wtBreakList.get(k))||"FSMIN".equalsIgnoreCase((String)wtBreakList.get(k))
                        ||"FSKG".equalsIgnoreCase((String)wtBreakList.get(k))||"SSBASIC".equalsIgnoreCase((String)wtBreakList.get(k))
                        ||"SSMIN".equalsIgnoreCase((String)wtBreakList.get(k))||"SSKG".equalsIgnoreCase((String)wtBreakList.get(k))
                        ||"CAFMIN".equalsIgnoreCase((String)wtBreakList.get(k))||"CAF%".equalsIgnoreCase((String)wtBreakList.get(k))
                        ||"BAFMIN".equalsIgnoreCase((String)wtBreakList.get(k))||"BAFM3".equalsIgnoreCase((String)wtBreakList.get(k))
                        ||"CSF".equalsIgnoreCase((String)wtBreakList.get(k))||"SURCHARGE".equalsIgnoreCase((String)wtBreakList.get(k)))
                        {*/
                         if(containersData[i].startsWith("FS")||containersData[i].startsWith("SS")
                         ||containersData[i].startsWith("BAF")||containersData[i].startsWith("CAF")
                         ||containersData[i].startsWith("CSF")||containersData[i].startsWith("SURCHARGE")||containersData[i].startsWith("PSS"))
                         {
                            pstmt1.setInt          (5,0);                              
                            pstmt1.setInt          (6,0);  
                             if(containersData[i].startsWith("FS"))
                                pstmt1.setString(7,"FUEL SURCHARGE");
                             if(containersData[i].startsWith("SS"))
                              pstmt1.setString(7,"SECURITY SURCHARGE");
                             if(containersData[i].startsWith("BAF"))
                               pstmt1.setString(7,"B.A.F");
                             if(containersData[i].startsWith("CAF"))
                               pstmt1.setString(7,"C.A.F");
                             if(containersData[i].startsWith("CSF"))
                                pstmt1.setString(7,"C.S.F");
                             if(containersData[i].startsWith("PSS"))
                                pstmt1.setString(7,"P.S.S");
                        }
                        else
                        {
                            pstmt1.setString(5,containersData[i]);
                            pstmt1.setString(6,containersData[i+1]);
                             pstmt1.setString(7,"A FREIGHT RATE");
                        }
                      }
                    }
                 
                }
        else
        {
                    pstmt1.setInt(5,0);
                    pstmt1.setInt(6,0);
                  if(containersData[i].startsWith("FS"))
                    pstmt1.setString(7,"FUEL SURCHARGE");
                    else if(containersData[i].startsWith("SS"))
                    pstmt1.setString(7,"SECURITY SURCHARGE");
                   else if(containersData[i].startsWith("BAF"))
                     pstmt1.setString(7,"B.A.F");
                   else if(containersData[i].startsWith("CAF"))
                     pstmt1.setString(7,"C.A.F");
                   else if(containersData[i].startsWith("CSF"))
                      pstmt1.setString(7,"C.S.F");
                   else if(containersData[i].startsWith("PSS"))
                      pstmt1.setString(7,"P.S.S");
                  else  if(containersData[i].endsWith("BAF"))
                     pstmt1.setString(7,"BAF");
                  else if(containersData[i].endsWith("CAF"))
                     pstmt1.setString(7,"CAF");
                  else if(containersData[i].endsWith("CSF"))
                      pstmt1.setString(7,"CSF");
                   else  if(containersData[i].endsWith("PSS"))
                      pstmt1.setString(7,"PSS");
                   else
                      pstmt1.setString(7,"A FREIGHT RATE");
        }
		
				pstmt1.addBatch();
				//pstmt1.execute();
				
			}
			
		}	
	}
	catch(Exception e)
	{
		e.printStackTrace();
		throw e;
	}

}
 public ArrayList getErrorMsg(ESupplyGlobalParameters loginbean) throws javax.ejb.EJBException
 {
    String                   mainhdrQuery        = "SELECT QSM.SHIPMENT_MODE SHIPMENT_MODE,QSM.CURRENCY CURRENCY,QSM.WEIGHT_BREAK WEIGHT_BREAK,QSM.WEIGHT_CLASS WEIGHT_CLASS,QSM.RATE_TYPE RATE_TYPE,QSM.UOM UOM,QSM.CONSOLETYPE CONSOLETYPE,QSM.ERROR_MSG ERROR_MSG FROM  QMS_STG_BUYRATES_MAIN_HEADER QSM";
    String                   dtlQuery            = "SELECT QSB.ROW_ID ROW_ID,QSB.ORIGIN ORIGIN,QSB.DESTINATION DESTINATION,QSB.CARRIER_ID CARRIER_ID,QSB.SERVICELEVEL SERVICELEVEL,QSB.FREQUENCY FREQUENCY,QSB.TRANSIT TRANSIT,QSB.EFFECTIVE_FROM EFFECTIVE_FROM,QSB.VALID_UPTO VALID_UPTO,QSB.ERROR_MSG  ERROR_MSG FROM QMS_STG_BUYRATES_DETAILS QSB ORDER BY to_number(QSB.ROW_ID)";
    Connection               connection          = null;                      
    PreparedStatement        pstmt               = null;
    PreparedStatement        pstmt1              = null;
    ResultSet                rs                  = null;
    ResultSet                rs1                 = null;
    OperationsImpl           operationsImpl      = null;
    FlatRatesDOB             flatRatesDOB        = null; 
    java.sql.Timestamp				validupTo          = null;
    java.sql.Timestamp				effectiveFrom      = null;
     ESupplyDateUtility        fomater           = null;
    ArrayList                errorList           = new ArrayList();
    RateDOB                  ratedob             = null;
   // ESupplyGlobalParameters    loginBean         = null;
    String                    dateFormat         = null; 
    try
    {
     
      fomater			      =  new ESupplyDateUtility();
      operationsImpl    =  new OperationsImpl();
        dateFormat        =   loginbean.getUserPreferences().getDateFormat();
      connection        =  operationsImpl.getConnection();
      pstmt1            =  connection.prepareStatement(mainhdrQuery);
      rs1               =  pstmt1.executeQuery();
      while(rs1.next())
      {
         ratedob        = new  RateDOB();
         ratedob.setShipmentMode(rs1.getString("SHIPMENT_MODE"));
         ratedob.setCurrency(rs1.getString("CURRENCY"));
         ratedob.setWeightBreak(rs1.getString("WEIGHT_BREAK"));
         ratedob.setWeightClass(rs1.getString("WEIGHT_CLASS"));
         ratedob.setRateType(rs1.getString("RATE_TYPE"));
         ratedob.setUom(rs1.getString("UOM"));
         ratedob.setConsoleType(rs1.getString("CONSOLETYPE"));
         ratedob.setRemarks(rs1.getString("ERROR_MSG"));
         errorList.add(ratedob);
      }
      
      pstmt             =  connection.prepareStatement(dtlQuery);
      rs                =  pstmt.executeQuery();
      while(rs.next())
        {
   
          flatRatesDOB  = new FlatRatesDOB();
          flatRatesDOB.setOrigin(rs.getString("ORIGIN"));
          flatRatesDOB.setDestination(rs.getString("DESTINATION"));
          flatRatesDOB.setCarrierId(rs.getString("CARRIER_ID"));
          flatRatesDOB.setServiceLevel(rs.getString("SERVICELEVEL"));
          flatRatesDOB.setFrequency(rs.getString("FREQUENCY"));
          flatRatesDOB.setTransittime(rs.getString("TRANSIT"));
            
          flatRatesDOB.setEffDate(rs.getTimestamp("EFFECTIVE_FROM"));
          if(rs.getTimestamp("VALID_UPTO")!=null)
          {
                flatRatesDOB.setValidUpto(rs.getTimestamp("VALID_UPTO"));
          }
            flatRatesDOB.setRemarks((rs.getString("ERROR_MSG")!=null)?rs.getString("ERROR_MSG"):"");
          flatRatesDOB.setSlNo(rs.getInt(1));
          errorList.add(flatRatesDOB);
      }
    
    }
    catch(Exception e)
    {
      	e.printStackTrace();

    }
    finally
		{
		  try
		  {			
				if(rs!=null)
					rs.close();
				if(rs1!=null)
					rs1.close();
				if(pstmt!=null)
					pstmt.close();
       	if(pstmt1!=null)
					pstmt1.close();
        if(connection!=null)
				  connection.close();
		  }
		  catch(Exception e)
		   {	
				logger.error("Error Occured  while closing Resources" + e);
			}
		}
    return errorList;
 }
 //@@Added by Kameswari for the WPBN issue-171210
  public String processExcelDelete(String fileName) throws EJBException
{
  	String            s1                ="";
  	PreparedStatement pstmtDetails      = null;
   // PreparedStatement pstmtDetails1      = null;Commented by govind on 16-02-2010 for connectionLeakages
   	PreparedStatement Details           = null;
   // PreparedStatement Details1           = null;Commented by govind on 16-02-2010 for connectionLeakages
	  Connection        connection        = null;
    OperationsImpl    operationsImpl    = null;
    String            deleteDtl         = "TRUNCATE TABLE QMS_BUYRATES_DELETE_DETAILS";
 //   String            deleteDtl1         = "DELETE FROM QMS_BUYRATES_DELETE_DATA";
    String            queryDetails      = "INSERT INTO QMS_BUYRATES_DELETE_DETAILS (ROW_ID,ORIGIN,DESTINATION,CARRIER_ID,SERVICE_LEVEL,FREQUENCY,TRANSIT_TIME,CURRENCY,TERMINAL_ID,DENSITY_CODE) VALUES (?,?,?,?,?,?,?,?,?,?)";			
	//	String            queryDetails1     = "INSERT INTO QMS_BUYRATES_DELETE_DATA (ROW_ID,LINE_NO,CONTAINER_NO,CONTAINER_VALUE) VALUES (?,?,?,?)";			
		
    String             msgquery         = "SELECT ERROR_MSG FROM QMS_BUYRATES_DELETE_DETAILS";
	//   ResultSet         rs               = null;Commeneted by Govind on 16-02-2010 for Connection Leakages
     String            returnstr        = null;
     CallableStatement  cstmt           = null;
    try
		{
    	String s="";
			String mainHeaderData[] =new String[8];			 
			int rows=0;
			int cols=0;
			int notesIndex=0;
			int batchCount=0;
			HSSFRow row = null;
			HSSFCell cell=null;
      InputStream myxls   = new FileInputStream(fileName);
      HSSFWorkbook wb     = new HSSFWorkbook(myxls);
      HSSFSheet sheet     = wb.getSheetAt(0);   
      rows                =sheet.getPhysicalNumberOfRows();
      //	connection=ConnectionUtil.getConnection();
    	operationsImpl    = new OperationsImpl();
      connection        = operationsImpl.getConnection();
      Details = connection.prepareStatement(deleteDtl);
     
       Details.execute();
       
     //  Details1 = connection.prepareStatement(deleteDtl1);
     //  Details1.execute();
	  	 row=sheet.getRow(0);

			 cols=row.getPhysicalNumberOfCells();
    
			 int l=cols-1;
     
			 for(;l>=0;l--)
			 {
				 cell=row.getCell((short)l);	
       
				if("TERMINALID:".equalsIgnoreCase(getCellData(cell,0)))
					break;
			  
			 }	
     
			 notesIndex=l+1;
    //  notesIndex=cols;
 
			 String detailsData[] =new String[notesIndex];
			 /*String containersData[] =new String[notesIndex-7];
			 for(l=7;l<notesIndex-2;l++)
			 {
				 cell=row.getCell((short)l);
				 containersData[l-7]=getCellData(cell);
			 }*/
	
	          pstmtDetails= connection.prepareStatement(queryDetails);
	          
          //  pstmtDetails1= connection.prepareStatement(queryDetails1);
		
      	 for (int i=1;i<rows;i++) 
			 {
				 
				row=sheet.getRow(i);				
      double time = 0.0;
      String data = null;
			    for (int j=0;j<notesIndex;j++ ) 
			    {
					     if(row!=null)
               {
                cell=row.getCell((short)j);
                 
               /* {
                cell.setCellType(cell.CELL_TYPE_STRING);
               
                }*/
					      detailsData[j]=getCellData(cell,0);
					      if(j==4) // Added by Gowtham.
					      {
					    	  if(detailsData[j].toString().indexOf(".")!= -1)
					    		  detailsData[j] = detailsData[j].substring(0,detailsData[j].toString().indexOf("."));
					      }
                if(j==6&&(cell.CELL_TYPE_NUMERIC==cell.getCellType())&&(detailsData[j].indexOf(".")!=-1)&&(Double.parseDouble(detailsData[j].substring(0, detailsData[j].indexOf(".")))<1))
                {
                   time =1/Double.parseDouble(detailsData[j]);
                   Double  t =new Double(time);
                   if(t.toString().indexOf(".")!=-1)
                   {
                        data =t.toString().substring(0, t.toString().indexOf("."));
                   }
                    if(data!=null)
                    {
                         detailsData[j] = data+":"+"00";
                    }
                }
                  if(j==6&&(cell.CELL_TYPE_NUMERIC==cell.getCellType())&&(detailsData[j].indexOf(".")!=-1)&&(!(Double.parseDouble(detailsData[j].substring(0, detailsData[j].indexOf(".")))<1)))
                   {
                   if(detailsData[j].indexOf(".")!=-1)
                   {
                     detailsData[j] =detailsData[j].substring(0, detailsData[j].indexOf("."));
                    }
                  
               }
                  if(j==5&&detailsData[j].indexOf(".")!=-1)
                  {
                     detailsData[j] =detailsData[j].substring(0, detailsData[j].indexOf("."));
                    }
                  /*if(j==8&&detailsData[j].indexOf(".")!=-1)
                  {
                     detailsData[j] =detailsData[j].substring(0, detailsData[j].indexOf("."));
                    }*/
                     }
				    }
				    batchCount++;

			    insertDeleteDetails(detailsData,pstmtDetails ,notesIndex,i);
			    
			    
			    if(batchCount == 150)
			    {
			    	pstmtDetails.executeBatch();
		       	pstmtDetails.clearBatch();
           
		      
		    	  batchCount=0;
          
		    
			    }
			 }
				
			 if(batchCount >0)
			 {
				    pstmtDetails.executeBatch();
				   	pstmtDetails.clearBatch();
           
		      
	        	batchCount=0;
			 }
       
          cstmt  = connection.prepareCall("{ ?=call QMS_BUY_RATES_UPLOAD_PKG.BUY_RATES_DELETE_PROC}");

           cstmt.registerOutParameter(1,Types.VARCHAR);
           cstmt.execute();
          returnstr = (String)cstmt.getString(1);
  
		 //End
     return returnstr;
		}
		catch(Exception e)
		{		
			logger.error("Exception");
			e.printStackTrace();
		}
		finally
		{
		  try
		  {			
     /* 	if(rs!=null)
        rs.close();-*///Commented by govind  on 16-02-2010 fro connection leakages.
       
				if(pstmtDetails!=null)
					pstmtDetails.close();
       if(Details!=null)
       Details.close();
       	if(cstmt!=null)
					cstmt.close();
        if(connection!=null)
				connection.close();
		  }
		  catch(Exception e)
		   {	
				logger.error("Error Occured  while closing Resources" + e);
			}
		}
		
return s1;
}
private void insertDeleteDetails(String[] records,PreparedStatement pstmt,int notesIndex,int rowIndex) throws Exception
{
	    java.sql.Timestamp				validupTo          = null;
      java.sql.Timestamp				effectiveFrom      = null;
      ESupplyDateUtility        fomater            = null;
     
       int                      temp               = 0;   
 	try
	{
	  fomater			      =   new ESupplyDateUtility();
   // pstmt.clearParameters();	
  
    if(records !=null)
		{			
			pstmt.setString(1,String.valueOf(rowIndex));
			pstmt.setString(2,records[0].trim());
			pstmt.setString(3,records[1].trim());
			pstmt.setString(4,records[2].trim());
			pstmt.setString(5,records[3].trim());
			pstmt.setString(6,records[4].trim());
			pstmt.setString(7,records[5].trim());
 			pstmt.setString(8,records[10].trim());
 			pstmt.setString(9,records[notesIndex-1].trim());
 			pstmt.setString(10,records[8].trim());
			pstmt.addBatch();
        
		
			//pstmt.execute();
			
		}	
	}
	catch(Exception e)
	{
		e.printStackTrace();
		throw e;
	}

}
  public ArrayList getDeleteErrorMsg() throws javax.ejb.EJBException
  {
    Connection          conn           =  null;
    PreparedStatement   pstmt          =  null;
    ResultSet           rs             =  null;  
    ArrayList          quotesList      =  new ArrayList();
    //@@Commented and added by subrahmanyam for the Buyrates delete issues for displaying only rates having the quotes.
    //String             errorMsgQuery   =  "SELECT ROW_ID,ORIGIN,DESTINATION,CARRIER_ID,SERVICE_LEVEL,FREQUENCY,TRANSIT_TIME,ERROR_MSG FROM QMS_BUYRATES_DELETE_DETAILS";
    String             errorMsgQuery   =  "SELECT ROW_ID,ORIGIN,DESTINATION,CARRIER_ID,SERVICE_LEVEL,FREQUENCY,TRANSIT_TIME,ERROR_MSG FROM QMS_BUYRATES_DELETE_DETAILS WHERE ERROR_MSG IS NOT NULL";
    FlatRatesDOB       flatRatesDOB    = null; 
     OperationsImpl    operationsImpl  = null;
    try
    {
       operationsImpl    =  new OperationsImpl();
       conn              =  operationsImpl.getConnection();
       pstmt             =  conn.prepareStatement(errorMsgQuery);
       rs                =  pstmt.executeQuery();
       while(rs.next())
       {
             flatRatesDOB   = new  FlatRatesDOB();
             flatRatesDOB.setSlNo(rs.getInt("ROW_ID"));
             flatRatesDOB.setOrigin(rs.getString("ORIGIN"));
             flatRatesDOB.setDestination(rs.getString("DESTINATION"));
              flatRatesDOB.setCarrierId(rs.getString("CARRIER_ID"));
             flatRatesDOB.setServiceLevel(rs.getString("SERVICE_LEVEL"));
             flatRatesDOB.setFrequency(rs.getString("FREQUENCY"));
             flatRatesDOB.setTransittime(rs.getString("TRANSIT_TIME"));
             flatRatesDOB.setRemarks(rs.getString("ERROR_MSG"));
             quotesList.add(flatRatesDOB);
       }
    }
    catch(Exception e)
    {
        e.printStackTrace();
    
    }
    finally
    {
        try
        {
           if(rs!=null)
           {
              rs.close();
           }
            if(pstmt!=null)
           {
              pstmt.close();
           }
            if(conn!=null)
           {
              conn.close();
           }
        }
        catch(Exception e)
        {
          	e.printStackTrace();
	       
        }
    }
    return quotesList;
  }
  //@@WPBN issue-171210
}
