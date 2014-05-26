/**
 * @ (#) ChargeMasterSessionBean.java
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
 * File       : ChargeMasterSessionBean.java
 * Sub-Module : Buy charges
 * Module     : QMS
 * @author    : I.V.Sekhar Merrinti
 * @date      : 25-06-2005
 * Modified by: Date     Reason
 */
package com.qms.operations.charges.ejb.sls;
import com.foursoft.esupply.common.exception.FoursoftException;
import com.qms.operations.charges.buycharges.dao.BuyChargesDAO;
import com.qms.operations.charges.java.BuychargesDtlDOB;
import com.qms.operations.charges.java.QMSCartageBuyDtlDOB;
import com.qms.operations.charges.java.QMSCartageMasterDOB;
import com.qms.operations.charges.java.QMSCartageSellDtlDOB;
import com.qms.operations.charges.sellcharges.dao.SellChargesDAO;
import com.qms.operations.sellrates.java.QMSSellRatesDOB;
import java.sql.CallableStatement;
import java.util.HashMap;
import java.sql.Timestamp;
import java.sql.Types;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;
import javax.naming.InitialContext;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.sql.SQLException;
import javax.ejb.EJBException;
import javax.ejb.ObjectNotFoundException;

import com.foursoft.esupply.common.bean.ESupplyGlobalParameters;
import com.foursoft.esupply.common.util.ConnectionUtil;
//import com.foursoft.esupply.common.util.Logger;
import org.apache.log4j.Logger;
import com.foursoft.esupply.common.java.LookUpBean;
import com.foursoft.etrans.common.util.java.OperationsImpl;
import com.foursoft.etrans.common.util.ejb.sls.OIDSession;
import com.foursoft.etrans.common.util.ejb.sls.OIDSessionHome;

import com.qms.operations.charges.buycharges.ejb.bmp.BuyChargesEntityBeanPK;
import com.qms.operations.charges.buycharges.ejb.bmp.BuyChargesEntityBeanLocalHome;
import com.qms.operations.charges.buycharges.ejb.bmp.BuyChargesEntityBeanLocal;
import com.qms.operations.charges.java.BuychargesHDRDOB;
import com.qms.operations.charges.java.BuySellChargesEnterIdDOB;
import com.qms.operations.charges.sellcharges.ejb.bmp.SellChargesEntityBeanPK;
import com.qms.operations.charges.sellcharges.ejb.bmp.SellChargesEntityBeanLocalHome;
import com.qms.operations.charges.sellcharges.ejb.bmp.SellChargesEntityLocal;
import oracle.jdbc.OracleTypes;

public class ChargeMasterSessionBean implements SessionBean 
{
 	private static final String FILE_NAME = "ChargeMasterSessionBean.java";
	private	 SessionContext	sessionContext = null;
	private	 String	 error = null;
	private	 OperationsImpl	operationsImpl = null;
  private	 DataSource		dataSource	   = null;
  private InitialContext  initialContext	= null; 
  private static Logger logger = null;
  
  public ChargeMasterSessionBean()
  {
    logger  = Logger.getLogger(ChargeMasterSessionBean.class);
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
    operationsImpl  = null;
  }

  public void setSessionContext(SessionContext ctx)
  {
  
	  this.sessionContext	= ctx;
		operationsImpl  = new OperationsImpl();  
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
      logger.error(FILE_NAME+" Exception in getDataSource() method of QMSQuoteSessionBean.java: "+e.toString());
    }
  }
  
  /**
   * 
   * @throws java.sql.SQLException
   * @return 
   * @param loginbean
   * @param buychargesHDRDOB
   */
  private boolean isValidBuyCharges(BuychargesHDRDOB buychargesHDRDOB,ESupplyGlobalParameters loginbean)throws EJBException
  {
    PreparedStatement pstmt   = null;
    Connection connection     = null;
    ResultSet         rs      = null;
    boolean    isValid        = true;
    int        cnt            = 0;
    String selectChargeID     = "SELECT COUNT(*) COUNT FROM QMS_CHARGESMASTER WHERE CHARGE_ID = ?";
    String selectChargeDescId = "SELECT COUNT(*) COUNT FROM QMS_CHARGEDESCMASTER  WHERE CHARGEID =? AND CHARGEDESCID=? AND TERMINALID=?";    
    String selectChargeBasis  = "SELECT COUNT(*) COUNT FROM QMS_CHARGE_BASISMASTER WHERE CHARGEBASIS = ?";     
    //String selectQry          = "SELECT COUNT(*) COUNT FROM QMS_BUYSELLCHARGESMASTER WHERE CHARGE_ID = ? AND CHARGEBASIS = ? AND TERMINALID = ?";
    String selectQry          = "SELECT COUNT(*) COUNT FROM QMS_BUYSELLCHARGESMASTER WHERE CHARGEDESCID=? AND TERMINALID = ?";
    try
    {
      connection    = operationsImpl.getConnection();
      if(buychargesHDRDOB!=null)
      {
        pstmt         = connection.prepareStatement(selectChargeID);
        pstmt.setString(1,buychargesHDRDOB.getChargeId());
        rs = pstmt.executeQuery();
        rs.next();
        cnt  = rs.getInt("COUNT");
        if(cnt==0)
        {
          isValid = false;
          return isValid;
        }
        if(rs!=null)
          { rs.close();}
        if(pstmt!=null)
          { pstmt.close();}
        pstmt = connection.prepareStatement(selectChargeBasis);
        pstmt.setString(1,buychargesHDRDOB.getChargeBasisId());
        rs = pstmt.executeQuery();
        rs.next();
        cnt = rs.getInt("COUNT");
        if(cnt==0)
        { 
          isValid = false;
          return isValid;
        }
        if(rs!=null)
          { rs.close();}
        if(pstmt!=null)
          { pstmt.close();} 
        if(buychargesHDRDOB.getChargeId().equals("1") || buychargesHDRDOB.getChargeId().equals("2") || buychargesHDRDOB.getChargeId().equals("3"))
        {
          isValid = false;
          return isValid;
        }
        pstmt         = connection.prepareStatement(selectChargeDescId);
        pstmt.setString(1,buychargesHDRDOB.getChargeId());
        pstmt.setString(2,buychargesHDRDOB.getChargeDescId());
        pstmt.setString(3,loginbean.getTerminalId());
        rs  = pstmt.executeQuery();
        rs.next();
        cnt   = rs.getInt("COUNT");
        
        if(cnt>0)
        {
          isValid = false;
        }
        if(rs!=null)
          { rs.close();}
        if(pstmt!=null)
          { pstmt.close();}         
        pstmt         = connection.prepareStatement(selectQry);
        pstmt.setString(1,buychargesHDRDOB.getChargeDescId());
        //pstmt.setString(2,buychargesHDRDOB.getChargeBasisId());
        pstmt.setString(2,loginbean.getTerminalId());
        rs  = pstmt.executeQuery();
        rs.next();
        cnt   = rs.getInt("COUNT");
        
        if(cnt>0)
        {
          isValid = false;
        }

      }else
      {
        isValid = false;
      }
    }catch(SQLException e)
    {
        //Logger.error(FILE_NAME,"------->isValidBuyCharges()",e.toString());
        logger.error(FILE_NAME+"------->isValidBuyCharges()"+e.toString());
        throw new EJBException();         
    }catch(Exception e)
    {
        //Logger.error(FILE_NAME,"------->isValidBuyCharges()",e.toString());
        logger.error(FILE_NAME+"------->isValidBuyCharges()"+e.toString());
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
        //Logger.error(FILE_NAME,"------->isValidBuyCharges()",e.toString());
        logger.error(FILE_NAME+"------->isValidBuyCharges()"+e.toString());
        throw new EJBException();
      }
    }
    return isValid;
  }
  
  /**
   * 
   * @param dataList
   * @param loginbean
   * @return 
   * @throws javax.ejb.EJBException
   */
  public ArrayList  insertBuyChargesDetails(ArrayList dataList,ESupplyGlobalParameters loginbean)throws EJBException
  {
    ArrayList insertedList    = null;
    ArrayList invalidList     = null;
    ArrayList dataAtHiger     = null;
    ArrayList returnList      = null;
    BuyChargesEntityBeanLocalHome home    = null;
    BuyChargesEntityBeanLocal     local   = null;
    OIDSessionHome               oidHome  = null;
    OIDSession	                 oidRemote= null;
    BuychargesHDRDOB buychargesHDRDOB     = null;
    double  buySellChargeId               = 1;
    
    
    try
    {
      insertedList    = new ArrayList();
      invalidList     = new ArrayList();
      dataAtHiger     = new ArrayList();
      returnList      = new ArrayList();
       //Logger.info(FILE_NAME,"in session bean ------>");
     
		   oidHome      =(OIDSessionHome)LookUpBean.getEJBHome("OIDSessionBean");
		   oidRemote	  =(OIDSession)oidHome.create();
       int dataListSize = dataList.size();
       home  = (BuyChargesEntityBeanLocalHome)LookUpBean.getEJBLocalHome("java:comp/env/BuyChargesEntityBean");
      for(int i=0;i<dataListSize;i++)
      {
        buychargesHDRDOB  = (BuychargesHDRDOB)dataList.get(i);
       /* if(isValidBuyCharges(buychargesHDRDOB,loginbean))
        {*/
        //generate unique buysellchargeId call OIDSessionbean.getBuySellChargesOID(loginbean)
        try{
          buySellChargeId = oidRemote.getBuySellChargesOID(loginbean,"Buy");
          buychargesHDRDOB.setBuySellChargeId(buySellChargeId);
          local = (BuyChargesEntityBeanLocal)home.create(buychargesHDRDOB);
          insertedList.add(buychargesHDRDOB);
        }catch(FoursoftException e) 
        {
            String errorMsg = e.getMessage();
            if("Invalid data".equals(errorMsg) || "Exception while inserting".equals(errorMsg))
            {
              invalidList.add(buychargesHDRDOB);
            }else if(errorMsg!=null && errorMsg.startsWith("Data exist at higer levels"))
            {
              buychargesHDRDOB.setDataAtHigher(errorMsg.substring(errorMsg.indexOf("H_")+2));
              dataAtHiger.add(buychargesHDRDOB);
            }
        }
        /*}else
        {
          invalidList.add(buychargesHDRDOB);
        }*/
      }
      returnList.add(insertedList);
      returnList.add(invalidList);
      returnList.add(dataAtHiger);
      if(insertedList!=null && insertedList.size()>0)
      {
//       home  = (BuyChargesEntityBeanLocalHome)LookUpBean.getEJBLocalHome("java:comp/env/BuyChargesEntityBean");
//       local = (BuyChargesEntityBeanLocal)home.create(insertedList);
        operationsImpl.setTransactionDetails(loginbean.getTerminalId(),loginbean.getUserId(),"BuyChargesMaster","",loginbean.getLocalTime(),"ADD");
      }     
    }catch(NamingException nex)
    {
        //Logger.error(FILE_NAME,"insertBuyChargesDetails()",nex.toString());
        logger.error(FILE_NAME+"insertBuyChargesDetails()"+nex.toString());
        nex.printStackTrace();
        throw new EJBException();
    }catch(SQLException e)
    {
        //Logger.error(FILE_NAME,"insertBuyChargesDetails()",e.toString());
        logger.error(FILE_NAME+"insertBuyChargesDetails()"+e.toString());
        e.printStackTrace();
        throw new EJBException();      
    }catch(Exception e)
    {
        //Logger.error(FILE_NAME,"insertBuyChargesDetails()",e.toString());
        logger.error(FILE_NAME+"insertBuyChargesDetails()"+e.toString());
        e.printStackTrace();
        throw new EJBException();      
    }
      return returnList;    
  }

  // to validate the teminal ids weather it is in that hirarchy or not.
  // Modify -- list of all the child terminals.
  // View   -- list of all the teminal list in the hirarchy.
  
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
          //logger.info("buySellChargesEnterIdDOB.getOperation()"+buySellChargesEnterIdDOB.getOperation());
          if("Modify".equalsIgnoreCase(buySellChargesEnterIdDOB.getOperation()))
          {
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
          }
          else
          {
       
             flag=true;
          }

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

  //public BuychargesHDRDOB loadBuychargeDetails(String chargeId,String chargeBasis,ESupplyGlobalParameters loginbean)throws ObjectNotFoundException,SQLException
 // public BuychargesHDRDOB loadBuychargeDetails(String chargeId,String chargeDescId,ESupplyGlobalParameters loginbean)throws ObjectNotFoundException,SQLException
  public BuychargesHDRDOB loadBuychargeDetails(BuySellChargesEnterIdDOB buySellChargesEnterIdDOB,ESupplyGlobalParameters loginbean)throws FoursoftException,ObjectNotFoundException,SQLException
  {
        BuychargesHDRDOB buychargesHDRDOB     = null;
        BuyChargesEntityBeanLocalHome home    = null;
        BuyChargesEntityBeanLocal     local   = null;        
        BuyChargesEntityBeanPK        pkObj   = null;
        
        try
        {
          pkObj   = new BuyChargesEntityBeanPK();
          if(validateLoadDetails(buySellChargesEnterIdDOB,loginbean.getTerminalId()))
          {
              pkObj.chargeId      = buySellChargesEnterIdDOB.getChargeId();
              /*pkObj.chargeBasisId = chargeBasis;*/
              pkObj.chargeDescId  = buySellChargesEnterIdDOB.getChargeDescId();
              pkObj.terminalId    = buySellChargesEnterIdDOB.getTerminalId();
              
              home                = (BuyChargesEntityBeanLocalHome)LookUpBean.getEJBLocalHome("java:comp/env/BuyChargesEntityBean");
              local               = (BuyChargesEntityBeanLocal)home.findByPrimaryKey(pkObj);
              buychargesHDRDOB    = local.getBuychargesHDRDOB();
              operationsImpl.setTransactionDetails(loginbean.getTerminalId(),loginbean.getUserId(),"BuyChargesMaster","",loginbean.getLocalTime(),"VIEW");
          }else
          {
            throw new FoursoftException("InvalidAccess");
          }
        }catch(FoursoftException e)
        {
          //Logger.info(FILE_NAME,"Exception in loadBuychargeDetails-->"+e);
          logger.info(FILE_NAME+"Exception in loadBuychargeDetails-->"+e);
          throw new FoursoftException("InvalidAccess");
        }catch(ObjectNotFoundException e)
        {
          //Logger.info(FILE_NAME,"Exception in loadBuychargeDetails-->"+e);
          logger.info(FILE_NAME+"Exception in loadBuychargeDetails-->"+e);
          throw new ObjectNotFoundException("bean couldnot Found----->");
        }catch(NameNotFoundException e)
        {
          //Logger.info(FILE_NAME,"Exception in loadBuychargeDetails-->"+e);
          logger.info(FILE_NAME+"Exception in loadBuychargeDetails-->"+e);
          throw new EJBException();
        }catch(NamingException e)
        {
          //Logger.info(FILE_NAME,"Exception in loadBuychargeDetails-->"+e);
          logger.info(FILE_NAME+"Exception in loadBuychargeDetails-->"+e);
          throw new EJBException();
        }catch(Exception e)
        {
          //Logger.info(FILE_NAME,"Exception in loadBuychargeDetails-->"+e);
          logger.info(FILE_NAME+"Exception in loadBuychargeDetails-->"+e);
          throw new EJBException();
        }
        return buychargesHDRDOB;
  }
  
  public boolean modifyBuyChargesDetails(BuychargesHDRDOB  buychargesHDRDOB,ESupplyGlobalParameters loginbean)throws ObjectNotFoundException,SQLException
  {
        BuyChargesEntityBeanLocalHome home    = null;
        BuyChargesEntityBeanLocal     local   = null;        
        BuyChargesEntityBeanPK        pkObj   = null;
        OIDSessionHome               oidHome  = null;
        OIDSession	                 oidRemote= null;
        double  buySellChargeId               = 1;
        boolean flag  = false;
        try
        {
          pkObj   = new BuyChargesEntityBeanPK();
          
          pkObj.chargeId      = buychargesHDRDOB.getChargeId();
          /*pkObj.chargeBasisId = buychargesHDRDOB.getChargeBasisId();*/
          pkObj.chargeDescId  = buychargesHDRDOB.getChargeDescId();
          pkObj.terminalId    = buychargesHDRDOB.getTerminalId();
          oidHome      =(OIDSessionHome)LookUpBean.getEJBHome("OIDSessionBean");
          oidRemote	  =(OIDSession)oidHome.create();          
          buySellChargeId = oidRemote.getBuySellChargesOID(loginbean,"Buy");
          buychargesHDRDOB.setBuySellChargeId(buySellChargeId);
          home                = (BuyChargesEntityBeanLocalHome)LookUpBean.getEJBLocalHome("java:comp/env/BuyChargesEntityBean");
          local               = (BuyChargesEntityBeanLocal)home.findByPrimaryKey(pkObj);
          local.setBuychargesHDRDOB(buychargesHDRDOB);
          flag  = true;
          if(flag)
            operationsImpl.setTransactionDetails(loginbean.getTerminalId(),loginbean.getUserId(),"BuyChargesMaster","",loginbean.getLocalTime(),"MODIFY");
        }catch(ObjectNotFoundException e)
        {
          //Logger.info(FILE_NAME,"Exception in modifyBuyChargesDetails-->"+e);
          logger.info(FILE_NAME+"Exception in modifyBuyChargesDetails-->"+e);
          throw new ObjectNotFoundException("bean couldnot Found----->");
        }catch(NameNotFoundException e)
        {
          //Logger.info(FILE_NAME,"Exception in modifyBuyChargesDetails-->"+e);
          logger.info(FILE_NAME+"Exception in modifyBuyChargesDetails-->"+e);
          throw new EJBException();
        }catch(NamingException e)
        {
          //Logger.info(FILE_NAME,"Exception in modifyBuyChargesDetails-->"+e);
          logger.info(FILE_NAME+"Exception in modifyBuyChargesDetails-->"+e);
          throw new EJBException();
        }catch(Exception e)
        {
          //Logger.info(FILE_NAME,"Exception in modifyBuyChargesDetails-->"+e);
          logger.info(FILE_NAME+"Exception in modifyBuyChargesDetails-->"+e);
          throw new EJBException();
        }
        
        return flag;
  }
  
  public ArrayList getTerminalListForBuyRSellCharges(BuySellChargesEnterIdDOB buySellChargesEnterIdDOB,ESupplyGlobalParameters loginbean)throws EJBException
  {
    ArrayList               terminalList  = null;        
    Connection    connection  = null;
    CallableStatement       cstmt = null;
    ResultSet               rs  = null;
    try
    {
          terminalList  = new ArrayList();
          connection  = operationsImpl.getConnection();
          cstmt   = connection.prepareCall("{ ? = call PKG_QMS_CHARGES.GETTERMINALLISTBRSCHARGES(?,?,?)}");
          cstmt.registerOutParameter(1,OracleTypes.CURSOR);
          cstmt.setString(2,buySellChargesEnterIdDOB.getOperation());
          cstmt.setString(3,buySellChargesEnterIdDOB.getTerminalId());
          cstmt.setString(4,loginbean.getTerminalId());
          //cstmt.registerOutParameter(3,OracleTypes.CURSOR);
          cstmt.execute();
          
          rs  = (ResultSet)cstmt.getObject(1);
          
          while(rs.next())
          {
            terminalList.add(rs.getString(1));
          }
    }catch(Exception e)
    {
      //Logger.info(FILE_NAME,"exceptione in getTerminalList()"+e.toString());
      logger.info(FILE_NAME+"exceptione in getTerminalList()"+e.toString());
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
        //Logger.error(FILE_NAME,"SQLException in load(BuyChargesEntityBeanPK param0) method"+e.toString());
        logger.error(FILE_NAME+"SQLException in load(BuyChargesEntityBeanPK param0) method"+e.toString());
        throw new EJBException();        
      }
      }
    return terminalList;
  }
  
 public ArrayList getBuySellChargeIds(BuySellChargesEnterIdDOB buySellChargesEnterIdDOB,ESupplyGlobalParameters loginbean)throws EJBException
  {
    //PreparedStatement pstmt     = null;
    CallableStatement       cstmt = null;
    Connection    connection    = null;
    ResultSet         rs        = null;
    ArrayList     chargeList    = null;

    

    try
    {
      chargeList    = new ArrayList();
      
    if(buySellChargesEnterIdDOB!=null)
    {
        String        chargeId      = buySellChargesEnterIdDOB.getChargeId();
        String        chargeDescId  = buySellChargesEnterIdDOB.getChargeDescId();
        String        fromWhere     = buySellChargesEnterIdDOB.getFromWhere();
        String        terminalId    = buySellChargesEnterIdDOB.getTerminalId();
          connection  =   operationsImpl.getConnection();
          if(buySellChargesEnterIdDOB.getFromWhere().equals("buychargesenterid") || buySellChargesEnterIdDOB.getFromWhere().equals("sellchargesenterid"))
            { //pstmt       =   connection.prepareStatement(selBuycharges.toString());
            //Chnged by VLAKSHMI for CR #170761 on 20090626
               cstmt   = connection.prepareCall("{?= call PKG_QMS_CHARGES.GETBUYSELLCHARGEIDS(?,?,?,?,?)}");
               cstmt.registerOutParameter(1,OracleTypes.CURSOR);
               cstmt.setString(2,(chargeId!=null?chargeId:""));
               cstmt.setString(3,(fromWhere!=null)?fromWhere:"");
               cstmt.setString(4,(chargeDescId!=null)?chargeDescId:"");
               cstmt.setString(5,(terminalId==null || terminalId.equals(""))?loginbean.getTerminalId():terminalId);
                cstmt.setString(6,(buySellChargesEnterIdDOB.getChargeGroupId()!=null)?buySellChargesEnterIdDOB.getChargeGroupId():"");
              //Logger.info(FILE_NAME,"chargeId"+chargeId+"fromWhere"+fromWhere+"chargeDescId"+chargeDescId+"terminalId"+terminalId);
          //rs          =   pstmt.executeQuery();
              cstmt.execute();
              rs = (ResultSet)cstmt.getObject(1);
              while(rs.next())
              {
                chargeId  = rs.getString(1);
                if(chargeId.trim().endsWith("--"))
                  {  chargeId  = chargeId.substring(0,chargeId.indexOf("--"));}
                chargeList.add(chargeId);
             }
          }
    }
    }catch(SQLException e)
    {
        //Logger.error(FILE_NAME,"getBuySellChargeIds()",e.toString());
        logger.error(FILE_NAME+"getBuySellChargeIds()"+e.toString());
        throw new EJBException();       
    }catch(Exception e)
    {
        //Logger.error(FILE_NAME,"getBuySellChargeIds()",e.toString());
        
        logger.error(FILE_NAME+"getBuySellChargeIds()"+e.toString());
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
        //Logger.error(FILE_NAME,"getBuyChargeIds()",e.toString());
        logger.error(FILE_NAME+"getBuyChargeIds()"+e.toString());
        throw new EJBException();           
      }
    }
    return chargeList;
  }
  
  public ArrayList getBuySellChargeDescIds(BuySellChargesEnterIdDOB buySellChargesEnterIdDOB,ESupplyGlobalParameters loginbean)throws EJBException
  {
    //PreparedStatement pstmt     = null;
    CallableStatement       cstmt = null;
    Connection    connection    = null;
    ResultSet         rs        = null;
    ArrayList     chargeDescList    = null;

    

    try
    {
     chargeDescList    = new ArrayList();
    if(buySellChargesEnterIdDOB!=null)
    {
        String        chargeId      = buySellChargesEnterIdDOB.getChargeId();
        String        chargeDescId  = buySellChargesEnterIdDOB.getChargeDescId();
        String        fromWhere     = buySellChargesEnterIdDOB.getFromWhere();
        String        terminalId    = buySellChargesEnterIdDOB.getTerminalId();
        String 		  chargeGroupId	= buySellChargesEnterIdDOB.getChargeGroupId();//@@Added by subbu for 195270 on 20-Jan-10
          connection  =   operationsImpl.getConnection();
          if(buySellChargesEnterIdDOB.getFromWhere().equals("buychargesenterid") || buySellChargesEnterIdDOB.getFromWhere().equals("sellchargesenterid"))
            { //pstmt       =   connection.prepareStatement(selBuycharges.toString());
        	  //@@ Comment & Added by subbu for the pbn id: 195270 on 20-Jan-10
               //cstmt   = connection.prepareCall("{?= call PKG_QMS_CHARGES.GETBUYSELLCHARGEDESCIDS(?,?,?,?)}");
        	  cstmt   = connection.prepareCall("{?= call PKG_QMS_CHARGES.GETBUYSELLCHARGEDESCIDS(?,?,?,?,?)}");
               cstmt.registerOutParameter(1,OracleTypes.CURSOR);
               cstmt.setString(4,(chargeId!=null?chargeId:""));
               cstmt.setString(3,(fromWhere!=null)?fromWhere:"");
               cstmt.setString(2,(chargeDescId!=null)?chargeDescId:"");
               cstmt.setString(5,(terminalId==null || terminalId.equals(""))?loginbean.getTerminalId():terminalId);
               cstmt.setString(6,(chargeGroupId!=null)?chargeGroupId:"");//@@Added by subbu for 195270 on 20-Jan-10
             // Logger.info(FILE_NAME,"chargeId"+chargeId+"fromWhere"+fromWhere+"chargeDescId"+chargeDescId+"terminalId"+terminalId);
          //rs          =   pstmt.executeQuery();
              cstmt.execute();
              rs = (ResultSet)cstmt.getObject(1);
              while(rs.next())
              {
                chargeId  = rs.getString(1);
                if(chargeId.trim().endsWith("--"))
                  {  chargeId  = chargeId.substring(0,chargeId.indexOf("--"));}
                chargeDescList.add(chargeId);
              }
          }
    }
    }catch(SQLException e)
    {
        //Logger.error(FILE_NAME,"getBuySellChargeDescIds()",e.toString());
        logger.error(FILE_NAME+"getBuySellChargeDescIds()"+e.toString());
        throw new EJBException();       
    }catch(Exception e)
    {
        //Logger.error(FILE_NAME,"getBuySellChargeDescIds()",e.toString());
        logger.error(FILE_NAME+"getBuySellChargeDescIds()"+e.toString());
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
        //Logger.error(FILE_NAME,"getBuySellChargeDescIds()",e.toString());
        logger.error(FILE_NAME+"getBuySellChargeDescIds()"+e.toString());
        throw new EJBException();           
      }
    }
    return chargeDescList;
  }  
 public ArrayList getBuyChargeIds(String searchStr,String fromWhere,String chargeDescId,ESupplyGlobalParameters loginbean)throws EJBException
  {
    PreparedStatement pstmt     = null;
    Connection    connection    = null;
    ResultSet         rs        = null;
    ArrayList     chargeList    = null;
    String        chargeId      = "";
    String        shipMode      = "";
    String        tabName       = "";
   try
    {
        chargeList    = new ArrayList();
        
        if("buychargesenterid".equals(fromWhere))
          { tabName = "QMS_BUYSELLCHARGESMASTER";}
        else if("sellchargesenterid".equals(fromWhere))
          { tabName = "QMS_SELLCHARGESMASTER";}
        else
          { return chargeList;}
          
        StringBuffer  selBuycharges = new StringBuffer("SELECT DISTINCT BM.CHARGE_ID ||'--'|| CM.CHARGE_DESCRIPTION FROM QMS_CHARGESMASTER CM,");
                  selBuycharges.append(tabName);
                  selBuycharges.append(" BM");
                  if(!"".equals(chargeDescId))
                    {  selBuycharges.append(",QMS_CHARGEDESCMASTER CBM");}
                  selBuycharges.append(" WHERE BM.CHARGE_ID = CM.CHARGE_ID AND BM.TERMINALID='");
                  selBuycharges.append(loginbean.getTerminalId());
                  if(!"".equals(chargeDescId))
                  {
                    selBuycharges.append("' AND BM.CHARGEDESCID='");
                    selBuycharges.append(chargeDescId);
                    selBuycharges.append("' AND CBM.CHARGEDESCID='");
                    selBuycharges.append(chargeDescId);
                  }
                  selBuycharges.append("' AND BM.CHARGE_ID LIKE '");
                  selBuycharges.append(searchStr);
                  selBuycharges.append("%'");
                  if("sellchargesenterid".equals(fromWhere))
                  {
                    selBuycharges.append(" AND BM.IE_FLAG ='A'"); 
                  }else if("buychargesenterid".equals(fromWhere))
                  {
                    selBuycharges.append(" AND BM.DEL_FLAG='N'");
                  }

      
      connection  =   operationsImpl.getConnection();
      if(fromWhere.equals("buychargesenterid") || fromWhere.equals("sellchargesenterid"))
        { pstmt       =   connection.prepareStatement(selBuycharges.toString());}
      rs          =   pstmt.executeQuery();
      while(rs.next())
      {
        chargeId  = rs.getString(1);
        if(chargeId.trim().endsWith("--"))
          {  chargeId  = chargeId.substring(0,chargeId.indexOf("--"));}
        chargeList.add(chargeId);
      }
    }catch(SQLException e)
    {
        //Logger.error(FILE_NAME,"getBuyChargeIds()",e.toString());
        logger.error(FILE_NAME+"getBuyChargeIds()"+e.toString());
        throw new EJBException();       
    }catch(Exception e)
    {
        //Logger.error(FILE_NAME,"getBuyChargeIds()",e.toString());
        logger.error(FILE_NAME+"getBuyChargeIds()"+e.toString());
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
        //Logger.error(FILE_NAME,"getBuyChargeIds()",e.toString());
        logger.error(FILE_NAME+"getBuyChargeIds()"+e.toString());
        throw new EJBException();           
      }
    }
    return chargeList;
  }
  public ArrayList getBuyChargeDescIds(String searchStr,String fromWhere,String chargeId,ESupplyGlobalParameters loginbean)throws EJBException
  {
    PreparedStatement pstmt       = null;
    Connection    connection      = null;
    ResultSet         rs          = null;
    ArrayList     chargeBasisList = null;
    String        chargeBasisId   = "";
    String        shipMode        = "";
    String        tabName         = "";
    try
    {
        chargeBasisList = new ArrayList();
        if("buychargesenterid".equals(fromWhere))
          { tabName = "QMS_BUYSELLCHARGESMASTER";}
        else if("sellchargesenterid".equals(fromWhere))
          { tabName = "QMS_SELLCHARGESMASTER";}
        else
          { return chargeBasisList;}
        StringBuffer  selBuycharges = new StringBuffer("SELECT DISTINCT BM.CHARGEDESCID FROM QMS_CHARGEDESCMASTER CBM,");
                  selBuycharges.append(tabName);
                  selBuycharges.append(" BM");
                  if(!"".equals(chargeId))
                    {  selBuycharges.append(",QMS_CHARGESMASTER CM");}
                  selBuycharges.append(" WHERE BM.CHARGEDESCID = CBM.CHARGEDESCID AND BM.TERMINALID='");
                  selBuycharges.append(loginbean.getTerminalId());
                  if(!"".equals(chargeId))
                  {
                    selBuycharges.append("' AND BM.CHARGE_ID ='");
                    selBuycharges.append(chargeId);
                    selBuycharges.append("' AND CM.CHARGE_ID ='");
                    selBuycharges.append(chargeId);
                  }                  
                  selBuycharges.append("' AND BM.CHARGEDESCID LIKE '");
                  selBuycharges.append(searchStr);
                  selBuycharges.append("%'");
                  if("sellchargesenterid".equals(fromWhere))
                  {
                    selBuycharges.append(" AND BM.IE_FLAG ='A'"); 
                  }else if("buychargesenterid".equals(fromWhere))
                  {
                    selBuycharges.append("AND BM.DEL_FLAG ='N'");
                  }

    
      connection  =   operationsImpl.getConnection();
      if(fromWhere.equals("buychargesenterid") || fromWhere.equals("sellchargesenterid"))
        { pstmt       =   connection.prepareStatement(selBuycharges.toString());}
      rs          =   pstmt.executeQuery();
      while(rs.next())
      {
        chargeBasisId  = rs.getString(1);
        if(chargeId.trim().endsWith("--"))
          {  chargeBasisId  = chargeBasisId.substring(0,chargeId.indexOf("--"));}
        chargeBasisList.add(chargeBasisId);
      }
    }catch(SQLException e)
    {
        //Logger.error(FILE_NAME,"getBuyChargeBasisIds()",e.toString());
        logger.error(FILE_NAME+"getBuyChargeBasisIds()"+e.toString());
        throw new EJBException();       
    }catch(Exception e)
    {
        //Logger.error(FILE_NAME,"getBuyChargeBasisIds()",e.toString());
        logger.error(FILE_NAME+"getBuyChargeBasisIds()"+e.toString());
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
        //Logger.error(FILE_NAME,"getBuyChargeBasisIds()",e.toString());
        logger.error(FILE_NAME+"getBuyChargeBasisIds()"+e.toString());
        throw new EJBException();           
      }
    }
    return chargeBasisList;
  } 
  public ArrayList getBuyChargeBasisIds(String searchStr,String fromWhere,String chargeId,ESupplyGlobalParameters loginbean)throws EJBException
  {
    PreparedStatement pstmt       = null;
    Connection    connection      = null;
    ResultSet         rs          = null;
    ArrayList     chargeBasisList = null;
    String        chargeBasisId   = "";
    String        shipMode        = "";
    String        tabName         = "";
    try
    {
      chargeBasisList = new ArrayList();
        if("buychargesenterid".equals(fromWhere))
          { tabName = "QMS_BUYSELLCHARGESMASTER";}
        else if("sellchargesenterid".equals(fromWhere))
          { tabName = "QMS_SELLCHARGESMASTER";}
        else
          { return chargeBasisList;}
        StringBuffer  selBuycharges = new StringBuffer("SELECT DISTINCT BM.CHARGEBASIS ||'--'|| CBM.BASIS_DESCRIPTION FROM QMS_CHARGE_BASISMASTER CBM,");
                  selBuycharges.append(tabName);
                  selBuycharges.append(" BM");
                  if(!"".equals(chargeId))
                    {  selBuycharges.append(",QMS_CHARGESMASTER CM");}
                  selBuycharges.append(" WHERE BM.CHARGEBASIS = CBM.CHARGEBASIS AND BM.TERMINALID='");
                  selBuycharges.append(loginbean.getTerminalId());
                  if(!"".equals(chargeId))
                  {
                    selBuycharges.append("' AND BM.CHARGE_ID ='");
                    selBuycharges.append(chargeId);
                    selBuycharges.append("' AND CM.CHARGE_ID ='");
                    selBuycharges.append(chargeId);
                  }                  
                  selBuycharges.append("' AND BM.CHARGEBASIS LIKE '");
                  selBuycharges.append(searchStr);
                  selBuycharges.append("%'");
                  if("sellchargesenterid".equals(fromWhere))
                  {
                    selBuycharges.append(" AND BM.IE_FLAG ='A'"); 
                  }                  

      connection  =   operationsImpl.getConnection();
      if(fromWhere.equals("buychargesenterid") || fromWhere.equals("sellchargesenterid"))
        { pstmt       =   connection.prepareStatement(selBuycharges.toString());}
      rs          =   pstmt.executeQuery();
      while(rs.next())
      {
        chargeBasisId  = rs.getString(1);
        if(chargeId.trim().endsWith("--"))
          {  chargeBasisId  = chargeBasisId.substring(0,chargeId.indexOf("--"));}
        chargeBasisList.add(chargeBasisId);
      }
    }catch(SQLException e)
    {
        //Logger.error(FILE_NAME,"getBuyChargeBasisIds()",e.toString());
        logger.error(FILE_NAME+"getBuyChargeBasisIds()"+e.toString());
        throw new EJBException();       
    }catch(Exception e)
    {
        //Logger.error(FILE_NAME,"getBuyChargeBasisIds()",e.toString());
        logger.error(FILE_NAME+"getBuyChargeBasisIds()"+e.toString());
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
        //Logger.error(FILE_NAME,"getBuyChargeBasisIds()",e.toString());
        logger.error(FILE_NAME+"getBuyChargeBasisIds()"+e.toString());
        throw new EJBException();           
      }
    }
    return chargeBasisList;
  }  
  
  public String validateHeaderData(BuychargesHDRDOB buychargesHDRDOB)
  {
    SellChargesDAO sellChargesDAO = null;
    String errorMsg  = null;
    try
    {
      sellChargesDAO = new SellChargesDAO();
      errorMsg  = sellChargesDAO.validateHeaderData(buychargesHDRDOB);
      
    }catch(Exception e)
    {
      e.printStackTrace();
      throw new EJBException();
    }
    return errorMsg;
  }
  
  
  
 //public BuychargesHDRDOB loadBuychargeDetailsForSellCharges(String chargeId,String chargeBasis,ESupplyGlobalParameters loginbean)throws ObjectNotFoundException,SQLException  
  public BuychargesHDRDOB loadBuychargeDetailsForSellCharges(String chargeId,String chargeDescId,String currencyId,ESupplyGlobalParameters loginbean)throws ObjectNotFoundException,SQLException
  {
        Connection connection = null;
        CallableStatement csmt = null;
        ResultSet         rs1  = null;
        ResultSet         rs2  = null;
        BuychargesHDRDOB buychargesHDRDOB     = null;
        BuychargesDtlDOB buychargesDtlDOB     = null;
        ArrayList        dtlList    = null;
        double  buySellChargeId      =  0;
        double  convFactor           = 1;
        /*BuyChargesEntityBeanLocalHome home    = null;
        BuyChargesEntityBeanLocal     local   = null;        
        BuyChargesEntityBeanPK        pkObj   = new BuyChargesEntityBeanPK();*/
        
        try
        {
          //validate chargeId and ChargeBasisId before going for buycharges entity[pending]//
          /*pkObj.chargeId      = chargeId;
          /*pkObj.chargeBasisId = chargeBasis;
          pkObj.chargeDescId  = chargeDescId;
          pkObj.terminalId    = loginbean.getTerminalId();*/
          /**
           * Get the Terminal Ids in an hirarchi which followed like 
           * OPERATIONAL_TERMINAL
           * ADMIN_TERMINAL
           * --
           * --
           * ADMIN_TERMINAL
           * HO_TERMINAL
           * then call for the Buy charges entity from current level,
           * if the buy charges not found then call the Buycharges for higher level
           * --
           * --
           * upto HO_TERMINAL ,even in HO_TERMINAL also doesnt exit then return "Record doesnt exit"
           */
          /*home                = (BuyChargesEntityBeanLocalHome)LookUpBean.getEJBLocalHome("java:comp/env/BuyChargesEntityBean");
          local               = (BuyChargesEntityBeanLocal)home.findByPrimaryKey(pkObj);
          buychargesHDRDOB    = local.getBuychargesHDRDOB();*/
          dtlList    = new ArrayList();
          connection = operationsImpl.getConnection();
          csmt        = connection.prepareCall("{ call PKG_QMS_CHARGES.LOADBUYCHARGEDTLSFORSELL(?,?,?,?,?)}");
          csmt.clearParameters();
          csmt.setString(1,chargeId);
          csmt.setString(2,chargeDescId);
          csmt.setString(3,loginbean.getTerminalId());
          csmt.registerOutParameter(4,OracleTypes.CURSOR);
          csmt.registerOutParameter(5,OracleTypes.CURSOR);
          csmt.execute();
          rs1 = (ResultSet)csmt.getObject(4);
          rs2 = (ResultSet)csmt.getObject(5);
          if(rs1==null && rs2==null)
          {
            throw new ObjectNotFoundException("Object Not Found----->");
          }
          else if(rs1.next())
          {
            
            
          //BUYSELLCHARGEID,CHARGE_ID,CHARGE_DESCRIPTION,CHARGEBASIS,BASIS_DESCRIPTION,RATE_BREAK,RATE_TYPE,CURRENCY,WEIGHT_CLASS 
            buySellChargeId   = Double.parseDouble(rs1.getString("BUYSELLCHARGEID"));
            buychargesHDRDOB  = new BuychargesHDRDOB();
            buychargesHDRDOB.setBuySellChargeId(buySellChargeId);
            buychargesHDRDOB.setChargeId(rs1.getString("CHARGE_ID"));
            buychargesHDRDOB.setChargeDescId(rs1.getString("CHARGEDESCID"));
            buychargesHDRDOB.setChargeBasisId(rs1.getString("CHARGEBASIS"));
            buychargesHDRDOB.setChargeBasisDesc(rs1.getString("BASIS_DESCRIPTION"));
            buychargesHDRDOB.setCurrencyId(rs1.getString("CURRENCY"));
            buychargesHDRDOB.setRateBreak(rs1.getString("RATE_BREAK"));
            buychargesHDRDOB.setRateType(rs1.getString("RATE_TYPE"));
            buychargesHDRDOB.setWeightClass(rs1.getString("WEIGHT_CLASS"));
            buychargesHDRDOB.setDensityGrpCode(rs1.getString("DENSITY_CODE"));
            buychargesHDRDOB.setPrimaryUnit(rs1.getString("PRIMARY_BASIS"));
            
            
            if(buySellChargeId>0)
            {
	            convFactor = operationsImpl.getConvertionFactor(buychargesHDRDOB.getCurrencyId(),currencyId);
              //SELECT BUYSELLCHAEGEID,BASE,MIN,MAX,FLAT,CHARGERATE,CHARGESLAB,"
                //+"LOWERBOUND,UPPERBOUND,CHARGERATE_INDICATOR FROM QMS_BUYCHARGESDTL WHERE BUYSELLCHAEGEID=? ORDER BY LANE_NO";
              dtlList   = new ArrayList();
              while(rs2.next())
              {
                buychargesDtlDOB  = new BuychargesDtlDOB();
                buychargesDtlDOB.setChargeSlab((rs2.getString("CHARGESLAB")!=null)?rs2.getString("CHARGESLAB"):"");
              //@@ Commented by subrahmanyam for the Enhancement 180161  on 24/Aug/09
              //buychargesDtlDOB.setChargeRate(operationsImpl.getConvertedAmt(rs2.getDouble("CHARGERATE"),convFactor));
              //@@ Added by subrahmanyam for the Enhancement 180161  on 24/Aug/09
                buychargesDtlDOB.setChargeRate(operationsImpl.getConvertedAmtDecimal(rs2.getDouble("CHARGERATE"),convFactor));
              // ended for 180161
                buychargesDtlDOB.setLowerBound(rs2.getDouble("LOWERBOUND"));
                buychargesDtlDOB.setUpperBound(rs2.getDouble("UPPERBOUND"));
                buychargesDtlDOB.setChargeRate_indicator((rs2.getString("CHARGERATE_INDICATOR")!=null)?rs2.getString("CHARGERATE_INDICATOR"):"");
                dtlList.add(buychargesDtlDOB);    
              }
            }else
            {
              throw new ObjectNotFoundException("Object Not Found----->");
            }
            buychargesHDRDOB.setBuyChargeDtlList(dtlList);
          }else
          {
            throw new ObjectNotFoundException("Object Not Found----->");
          }
          //operationsImpl.setTransactionDetails(loginbean.getTerminalId(),loginbean.getUserId(),"BuyChargesMaster","",loginbean.getLocalTime(),"View");
        }catch(ObjectNotFoundException e)
        {
          //Logger.info(FILE_NAME,"Exception in loadBuychargeDetailsForSellCharges-->"+e);
          logger.info(FILE_NAME+"Exception in loadBuychargeDetailsForSellCharges-->"+e);
          throw new ObjectNotFoundException("bean couldnot Found----->");
        }catch(Exception e)
        {
          e.printStackTrace();
          //Logger.info(FILE_NAME,"Exception in loadBuychargeDetailsForSellCharges-->"+e);
          logger.info(FILE_NAME+"Exception in loadBuychargeDetailsForSellCharges-->"+e);
          throw new EJBException();
        }finally
        {
          try
          {
            if(rs1!=null)
            { rs1.close();}
            if(rs2!=null)
            { rs2.close();}            
            if(csmt!=null)
            { csmt.close();}
            if(connection!=null)
            { connection.close();}
          }catch(SQLException e)
          {
            //Logger.info(FILE_NAME,"SQLException "+e);
            logger.info(FILE_NAME+"SQLException "+e);
          }
        }
        return buychargesHDRDOB;
  }
  private boolean isValidSellCharges(BuychargesHDRDOB sellchargesHDRDOB,ESupplyGlobalParameters loginbean)throws SQLException
  {
    PreparedStatement pstmt   = null;
    Connection connection     = null;
    ResultSet         rs      = null;
    boolean    isValid        = true;
    String selectQry          = "SELECT COUNT(*) COUNT FROM QMS_SELLCHARGESMASTER WHERE CHARGE_ID = ? AND CHARGEBASIS = ? AND RATE_BREAK=? AND RATE_TYPE=? AND TERMINALID = ?";
    String selectChargeID     = "SELECT COUNT(*) COUNT FROM QMS_CHARGESMASTER WHERE CHARGE_ID = ?";
    String selectChargeBasis  = "SELECT COUNT(*) COUNT FROM QMS_CHARGE_BASISMASTER WHERE CHARGEBASIS = ?";
    int cnt                   = 0;
    try
    {
      connection    = operationsImpl.getConnection();
      
      if(sellchargesHDRDOB!=null)
      {
        pstmt         = connection.prepareStatement(selectChargeID);
        pstmt.setString(1,sellchargesHDRDOB.getChargeId());
        rs = pstmt.executeQuery();
        rs.next();
        cnt  = rs.getInt("COUNT");
        if(cnt==0)
        {
          isValid = false;
          return isValid;
        }
        if(rs!=null)
          { rs.close();}
        if(pstmt!=null)
          { pstmt.close();}
        pstmt = connection.prepareStatement(selectChargeBasis);
        pstmt.setString(1,sellchargesHDRDOB.getChargeBasisId());
        rs = pstmt.executeQuery();
        rs.next();
        cnt = rs.getInt("COUNT");
        if(cnt==0)
        { 
          isValid = false;
          return isValid;
        }
        if(rs!=null)
          { rs.close();}
        if(pstmt!=null)
          { pstmt.close();}        
        if(sellchargesHDRDOB.getChargeId().equals("1") || sellchargesHDRDOB.getChargeId().equals("2") || sellchargesHDRDOB.getChargeId().equals("3"))
        {
          isValid = false;
          return isValid;
        }
        
        pstmt         = connection.prepareStatement(selectQry);
        pstmt.setString(1,sellchargesHDRDOB.getChargeId());
        pstmt.setString(2,sellchargesHDRDOB.getChargeBasisId());
        pstmt.setString(3,sellchargesHDRDOB.getRateBreak());
        pstmt.setString(4,sellchargesHDRDOB.getRateType());
        pstmt.setString(5,loginbean.getTerminalId());
        rs  = pstmt.executeQuery();
        rs.next();
        cnt = rs.getInt("COUNT");
        if(cnt>0)
        {
          isValid = false;
        }

      }
    }catch(SQLException e)
    {
        //Logger.error(FILE_NAME,"------->isValidSellCharges()",e.toString());
        logger.error(FILE_NAME+"------->isValidSellCharges()"+e.toString());
        throw new EJBException();         
    }catch(Exception e)
    {
        //Logger.error(FILE_NAME,"------->isValidSellCharges()",e.toString());
        logger.error(FILE_NAME+"------->isValidSellCharges()"+e.toString());
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
        //Logger.error(FILE_NAME,"------->isValidSellCharges()",e.toString());
        logger.error(FILE_NAME+"------->isValidSellCharges()"+e.toString());
        throw new EJBException();
      }
    }
    return isValid;
  }
  public String  insertSellChargesDetails(BuychargesHDRDOB sellchargesHDRDOB,ESupplyGlobalParameters loginbean)throws EJBException
  {
    SellChargesEntityBeanLocalHome home    = null;
    SellChargesEntityLocal         local   = null;
    OIDSessionHome               oidHome   = null;
    OIDSession	                 oidRemote = null;
    double  buySellChargeId               = 1;
    String returnMsg          = "";
    
    
    try
    {
       //Logger.info(FILE_NAME,"in session bean ------>");
       logger.info(FILE_NAME+"in session bean ------>");
		   oidHome      =(OIDSessionHome)LookUpBean.getEJBHome("OIDSessionBean");
		   oidRemote	  =(OIDSession)oidHome.create();
       home  = (SellChargesEntityBeanLocalHome)LookUpBean.getEJBLocalHome("java:comp/env/SellChargesEntityBean");
        /*if(isValidSellCharges(sellchargesHDRDOB,loginbean))
        {*/
        //generate unique buysellchargeId call OIDSessionbean.getBuySellChargesOID(loginbean)
          buySellChargeId = oidRemote.getBuySellChargesOID(loginbean,"Sell");
          sellchargesHDRDOB.setBuySellChargeId(buySellChargeId);
          try{
          local = (SellChargesEntityLocal)home.create(sellchargesHDRDOB);
          operationsImpl.setTransactionDetails(loginbean.getTerminalId(),loginbean.getUserId(),"SellChargesMaster","",loginbean.getLocalTime(),"Add");
          returnMsg = "success";
          }catch(FoursoftException e) 
          {
              String errorMsg = e.getMessage();
              returnMsg = errorMsg;
          }
          //insertedList.add(sellchargesHDRDOB);
       // }
    
    }catch(NamingException nex)
    {
        //Logger.error(FILE_NAME,"insertSellChargesDetails()",nex.toString());
        logger.error(FILE_NAME+"insertSellChargesDetails()"+nex.toString());
        throw new EJBException();
    }catch(SQLException e)
    {
        //Logger.error(FILE_NAME,"insertSellChargesDetails()",e.toString());
        logger.error(FILE_NAME+"insertSellChargesDetails()"+e.toString());
        throw new EJBException();      
    }catch(Exception e)
    {
        //Logger.error(FILE_NAME,"insertSellChargesDetails()",e.toString());
        logger.error(FILE_NAME+"insertSellChargesDetails()"+e.toString());
        throw new EJBException();      
    }
      return returnMsg;    
  }
  public BuychargesHDRDOB loadSellchargeDetails(BuySellChargesEnterIdDOB buySellChargesEnterIdDOB,ESupplyGlobalParameters loginbean)throws FoursoftException,ObjectNotFoundException,SQLException
  {
        BuychargesHDRDOB sellchargesHDRDOB     = null;
        SellChargesEntityBeanLocalHome home    = null;
        SellChargesEntityLocal     local   = null;        
        SellChargesEntityBeanPK        pkObj   = null;
        
        try
        {
          pkObj   = new SellChargesEntityBeanPK();
          //Logger.info(FILE_NAME,"inLoadsell");
        
          if(validateLoadDetails(buySellChargesEnterIdDOB,loginbean.getTerminalId()))
          {
              //validate chargeId and ChargeBasisId before going for buycharges entity[pending]//
              pkObj.chargeId      = buySellChargesEnterIdDOB.getChargeId();
              /*pkObj.chargeBasisId = chargeBasis;
              pkObj.rateBreak     = rateBreak;
              pkObj.rateType      = rateType;*/
              pkObj.chargeDescId  = buySellChargesEnterIdDOB.getChargeDescId();
              pkObj.terminalId    = buySellChargesEnterIdDOB.getTerminalId();
              pkObj.operation     = buySellChargesEnterIdDOB.getOperation();//@@Added by Kameswari for the WPBN Issue-154398 on 24/02/09
              /**
               * Get the Terminal Ids in an hirarchi which followed like 
               * OPERATIONAL_TERMINAL
               * ADMIN_TERMINAL
               * --
               * --
               * ADMIN_TERMINAL
               * HO_TERMINAL
               * then call for the Buy charges entity from current level,
               * if the buy charges not found then call the Buycharges for higher level
               * --
               * --
               * upto HO_TERMINAL ,even in HO_TERMINAL also doesnt exit then return "Record doesnt exit"
               */
              home                = (SellChargesEntityBeanLocalHome)LookUpBean.getEJBLocalHome("java:comp/env/SellChargesEntityBean");
              local               = (SellChargesEntityLocal)home.findByPrimaryKey(pkObj);
              sellchargesHDRDOB    = local.getSellchargesHDRDOB();
                //operationsImpl.setTransactionDetails(loginbean.getTerminalId(),loginbean.getUserId(),"SellChargesMaster","",loginbean.getLocalTime(),"View");
          }else
          {
            throw new FoursoftException("InvalidAccess");
          }
        }catch(FoursoftException e)
        {
          //Logger.info(FILE_NAME,"Exception in loadBuychargeDetails-->"+e);
          logger.info(FILE_NAME+"Exception in loadBuychargeDetails-->"+e);
          throw new FoursoftException("InvalidAccess");
        }
        catch(ObjectNotFoundException e)
        {
          //Logger.info(FILE_NAME,"Exception in loadSellchargeDetails-->"+e);
          logger.info(FILE_NAME+"Exception in loadSellchargeDetails-->"+e);
          throw new ObjectNotFoundException("bean couldnot Found----->");
        }catch(NameNotFoundException e)
        {
          //Logger.info(FILE_NAME,"Exception in loadSellchargeDetails-->"+e);
          logger.info(FILE_NAME+"Exception in loadSellchargeDetails-->"+e);
          throw new EJBException();
        }catch(NamingException e)
        {
          //Logger.info(FILE_NAME,"Exception in loadSellchargeDetails-->"+e);
          logger.info(FILE_NAME+"Exception in loadSellchargeDetails-->"+e);
          throw new EJBException();
        }catch(Exception e)
        {
          //Logger.info(FILE_NAME,"Exception in loadSellchargeDetails-->"+e);
          logger.info(FILE_NAME+"Exception in loadSellchargeDetails-->"+e);
          throw new EJBException();
        }
        return sellchargesHDRDOB;
  }
  public boolean modifySellChargesDetails(BuychargesHDRDOB  sellchargesHDRDOB,ESupplyGlobalParameters loginbean)throws ObjectNotFoundException,SQLException
  {
        SellChargesEntityBeanLocalHome home    = null;
        SellChargesEntityLocal     local   = null;        
        SellChargesEntityBeanPK        pkObj   = null;
        OIDSessionHome               oidHome  = null;
        OIDSession	                 oidRemote= null;
        double  sellChargeId               = 1;
        boolean flag  = false;
        try
        {
          pkObj   = new SellChargesEntityBeanPK();
          pkObj.chargeId      = sellchargesHDRDOB.getChargeId();
          /*pkObj.chargeBasisId = sellchargesHDRDOB.getChargeBasisId();
          pkObj.rateBreak     = sellchargesHDRDOB.getRateBreak();
          pkObj.rateType      = sellchargesHDRDOB.getRateType();*/
          pkObj.chargeDescId  = sellchargesHDRDOB.getChargeDescId();
          pkObj.terminalId    = sellchargesHDRDOB.getTerminalId();
          pkObj.operation     = sellchargesHDRDOB.getOperation();//@@Added by Kameswari for the WPBN issue-154398
          oidHome      =(OIDSessionHome)LookUpBean.getEJBHome("OIDSessionBean");
          oidRemote	  =(OIDSession)oidHome.create();          
          sellChargeId = oidRemote.getBuySellChargesOID(loginbean,"Sell");
          sellchargesHDRDOB.setBuySellChargeId(sellChargeId);
          home                = (SellChargesEntityBeanLocalHome)LookUpBean.getEJBLocalHome("java:comp/env/SellChargesEntityBean");
          local               = (SellChargesEntityLocal)home.findByPrimaryKey(pkObj);
          local.setSellchargesHDRDOB(sellchargesHDRDOB);
          operationsImpl.setTransactionDetails(loginbean.getTerminalId(),loginbean.getUserId(),"SellChargesMaster","",loginbean.getLocalTime(),"Modify");
          flag  = true;
        }catch(ObjectNotFoundException e)
        {
          //Logger.info(FILE_NAME,"Exception in modifySellChargesDetails-->"+e);
          logger.info(FILE_NAME+"Exception in modifySellChargesDetails-->"+e);
          throw new ObjectNotFoundException("bean couldnot Found----->");
        }catch(NameNotFoundException e)
        {
          //Logger.info(FILE_NAME,"Exception in modifySellChargesDetails-->"+e);
          logger.info(FILE_NAME+"Exception in modifySellChargesDetails-->"+e);
          throw new EJBException();
        }catch(NamingException e)
        {
          //Logger.info(FILE_NAME,"Exception in modifySellChargesDetails-->"+e);
          logger.info(FILE_NAME+"Exception in modifySellChargesDetails-->"+e);
          throw new EJBException();
        }catch(Exception e)
        {
          //Logger.info(FILE_NAME,"Exception in modifySellChargesDetails-->"+e);
          logger.info(FILE_NAME+"Exception in modifySellChargesDetails-->"+e);
          throw new EJBException();
        }
        
        return flag;
  }
  
   public StringBuffer validateCartageBuyChargesHdr(QMSCartageMasterDOB cartageMaster) throws EJBException
  {
   
    Connection        connection      = null;
    PreparedStatement pStmt           = null;
    ResultSet         rs              = null;
    StringBuffer      errors          = null;
    
    String            zones           = "";
    String[]          zoneCodes       = null;
    String            locationId      = null;
    String            currencyId      = null;
    String            vendorId        = null;
    Timestamp         effectiveFrom   = null;
    Timestamp         validUpto       = null;
    String            terminalId      = null;
    String            accessLevel     = null;
    
    String            currency_sql    = "";
    String            location_sql    = "";
    String            locationAccess_sql="";
    String            locationSubQuery= "";
    String            zoneCodeSql     = "";
    String            zonecodesql     = "";//@@Added by Kameswari for the WPBN issue-83894.
    String            dates_sql       = "";
    String            chargeType      = null;
    String            chargeTypeStr   = "";
    
    int               count           = 0;
    
    boolean           flag            = false;
    int 			  zoneCodeLen		= 0;
    
    try
    {
      connection      =   operationsImpl.getConnection();
      errors          =  new StringBuffer();
      
      zoneCodes       = cartageMaster.getZoneCodes();
      locationId      = cartageMaster.getLocationId();
      currencyId      = cartageMaster.getCurrencyId();
      vendorId        = cartageMaster.getVendorId();
      effectiveFrom   = cartageMaster.getEffectiveFrom();
      validUpto       = cartageMaster.getValidUpto();
      
      chargeType      = cartageMaster.getChargeType();
      terminalId      = cartageMaster.getTerminalId();
      
      accessLevel     = cartageMaster.getAccessLevel();
      
      if (zoneCodes !=null)
      {
    	  zoneCodeLen	=	zoneCodes.length;
      for(int i=0;i<zoneCodeLen;i++)
      {
        if((i+1)==zoneCodeLen)
            zones  = zones + "'"+zoneCodes[i]+"'";
        else        
            zones  = zones + "'"+zoneCodes[i]+"',";
      }
      }
      
      if("Both".equals(chargeType))
          chargeTypeStr = "'Pickup','Delivery'";
      else
          chargeTypeStr = "'"+chargeType+"'";
          
      if("H".equalsIgnoreCase(accessLevel))
        locationSubQuery     =    "SELECT TERMINALID FROM FS_FR_TERMINALMASTER";
      else
        locationSubQuery     =    "SELECT CHILD_TERMINAL_ID TERM_ID FROM FS_FR_TERMINAL_REGN CONNECT BY PRIOR CHILD_TERMINAL_ID= "+
                                  "PARENT_TERMINAL_ID START WITH PARENT_TERMINAL_ID='"+terminalId+"' UNION SELECT TERMINALID FROM FS_FR_TERMINALMASTER WHERE TERMINALID='"+terminalId+"'";
      
      location_sql         =    "SELECT COUNT(*) NO_ROWS FROM FS_FR_LOCATIONMASTER WHERE LOCATIONID=?";
      locationAccess_sql   =    "SELECT LOCATIONID FROM FS_FR_TERMINALLOCATION WHERE TERMINALID IN("+locationSubQuery+")";
      
      zoneCodeSql          =    "SELECT COUNT(DISTINCT D.ZONE) NO_ROWS FROM QMS_ZONE_CODE_MASTER M, QMS_ZONE_CODE_DTL D "+
                                "WHERE D.ZONE_CODE=M.ZONE_CODE AND M.SHIPMENT_MODE = ? AND NVL(M.CONSOLE_TYPE,'~') = ? AND M.ORIGIN_LOCATION=?  "+
                                "AND D.ZONE IN ("+zones+")";
      
     zonecodesql          =    "SELECT COUNT(DISTINCT C.ZONE) NO_ROWS FROM QMS_ZONE_CODE_MASTER_CA M, QMS_ZONE_CODE_DTL_CA C "+
                                "WHERE C.ZONE_CODE=M.ZONE_CODE AND M.SHIPMENT_MODE = ? AND NVL(M.CONSOLE_TYPE,'~') = ? AND M.LOCATION_ID=?  "+
                                "AND C.ZONE IN ("+zones+")";
      currency_sql         =    "SELECT COUNT(*) NO_ROWS FROM FS_COUNTRYMASTER WHERE CURRENCYID=?";
      
      dates_sql            =    "SELECT COUNT(*)NO_ROWS FROM QMS_CARTAGE_BUYSELLCHARGES MAS,QMS_CARTAGE_BUYDTL DTL WHERE ((EFFECTIVE_FROM BETWEEN ? AND "+
                                "?) OR (VALID_UPTO BETWEEN ? AND ? )) AND MAS.CARTAGE_ID=DTL.CARTAGE_ID AND MAS.LOCATION_ID=? "+
                                "AND DTL.CHARGE_TYPE IN("+chargeTypeStr+") AND DTL.ZONE_CODE IN ("+zones+")";
      
      pStmt                =     connection.prepareStatement(location_sql);
      pStmt.setString(1,locationId);
      rs                   =     pStmt.executeQuery();
      
      while(rs.next())
        count = rs.getInt("NO_ROWS");
        
      if(count == 0)
        errors.append("Location Id is Invalid.<br>");
      else
      {
        if(rs!=null)
          rs.close();
        if(pStmt!=null)
          pStmt.close();
        
        pStmt                =     connection.prepareStatement(locationAccess_sql);
        rs                   =     pStmt.executeQuery();
        
        while(rs.next())
        {
          if(rs.getString("LOCATIONID").equalsIgnoreCase(locationId))
            flag  = true;
        }
        
        if(!flag)
          errors.append("Location Id ").append(locationId).append(" Does Not Fall Under Your Jurisdiction.<br>");
      }
      
      if(rs!=null)
        rs.close();
      if(pStmt!=null)
        pStmt.close();
        
      count = 0;
      
      pStmt                =     connection.prepareStatement(zoneCodeSql);
      pStmt.setString(1,cartageMaster.getShipmentMode());
      if("1".equalsIgnoreCase(cartageMaster.getShipmentMode()))
          pStmt.setString(2,"~");
      else
          pStmt.setString(2,cartageMaster.getConsoleType());
      pStmt.setString(3,locationId);
      rs                   =     pStmt.executeQuery();
      
      while(rs.next())
        count = rs.getInt("NO_ROWS");
        //@@Added by Kameswari for the WPBN issue-83894
      if(rs!=null)
        rs.close();
      if(pStmt!=null)
        pStmt.close();    
      if(count != zoneCodes.length)
      {
          pStmt                =     connection.prepareStatement(zonecodesql);
          pStmt.setString(1,cartageMaster.getShipmentMode());
          if("1".equalsIgnoreCase(cartageMaster.getShipmentMode()))
              pStmt.setString(2,"~");
          else
              pStmt.setString(2,cartageMaster.getConsoleType());
          pStmt.setString(3,locationId);
          rs                   =     pStmt.executeQuery();
          
          while(rs.next())
            count = rs.getInt("NO_ROWS");
      }
      //@@WPBN issue-83894
       if(count != zoneCodes.length)
      {
        errors.append("The Selected Zone Code(s) are not defined for the selected Shipment Mode, Console Type & Location Id. <br>");
      }
      if(rs!=null)
        rs.close();
      if(pStmt!=null)
        pStmt.close();
        
      count = 0;
      
      pStmt                =     connection.prepareStatement(currency_sql);
      pStmt.setString(1,currencyId);
      rs                   =     pStmt.executeQuery();
      
      while(rs.next())
        count = rs.getInt("NO_ROWS");
        
      if(count == 0)
        errors.append("Currency Id is Invalid.<br>");
      
      if(rs!=null)
        rs.close();
      if(pStmt!=null)
        pStmt.close();
        
      count = 0;
      
     /* pStmt                =     connection.prepareStatement(dates_sql);
      pStmt.setTimestamp(1,effectiveFrom);
      pStmt.setTimestamp(2,validUpto);
      pStmt.setTimestamp(3,effectiveFrom);
      pStmt.setTimestamp(4,validUpto);
      pStmt.setString(5,cartageMaster.getLocationId());
      //pStmt.setString(6,cartageMaster.getWeightBreak());//@@Since the user will define either flat or slab rate 
     // pStmt.setString(7,cartageMaster.getRateType());       for that combination in that period.
      //pStmt.setString(7,cartageMaster.getChargeType());
     // rs                   =     pStmt.executeQuery();
      
     // while(rs.next())
        //count = rs.getInt("NO_ROWS");
        
      //if(count > 0)
       // errors.append("Cartage Buy Charges are Already Defined Between the Two Dates.<br>");
      
      if(rs!=null)
        rs.close();
      if(pStmt!=null)
        pStmt.close();*/
        
    }
    catch(Exception e)
    {
      //Logger.error(FILE_NAME,"Error while validating Cartage Charges."+e.toString());
      logger.error(FILE_NAME+"Error while validating Cartage Charges."+e.toString());
      e.printStackTrace();
      throw new EJBException(e.toString());
    }
    finally
    {
      ConnectionUtil.closeConnection(connection,pStmt,rs);
    }
    return errors;
    
  }
  public StringBuffer validateCartageSellChargesHdr(QMSCartageMasterDOB cartageMaster) throws EJBException
  {
    Connection        connection      = null;
    PreparedStatement pStmt           = null;
    ResultSet         rs              = null;
    StringBuffer      errors          = null;
    
    String            locationId      = null;
    String[]            zoneCodes        ;
    String            currencyId      = null;
    
    String            chargeType      = null;
    String            chargeTypeStr   = "";
    
    String            currency_sql    = "";
    String            location_sql    = "";
    String            zoneCodeSql     = "";
    String            locationAccess_sql="";
    String            locationSubQuery= "";
    
    String            terminalId      = null;
    String            accessLevel     = null;
    
    int               count           = 0;
    
    boolean           flag            = false;
//added by subrahmanyam for CartageChargesNot Coming To view
    String            zoneCodeCaSql    = "";
    PreparedStatement pStmt1           = null;
    PreparedStatement pStmt2           = null;
    ResultSet         rs1              = null;
    ResultSet         rs2              = null;
    String            country_loc_sql  = "";
    String            country          = "";
 //ended by subrahmanyam for CartageChargesNot Coming To view
    try
    {
      connection      =   operationsImpl.getConnection();
      errors          =   new StringBuffer();
      
      zoneCodes        =   cartageMaster.getZoneCodes();
      locationId      =   cartageMaster.getLocationId();
      currencyId      =   cartageMaster.getCurrencyId();
      
      chargeType      =   cartageMaster.getChargeType();
      
      if("Both".equals(chargeType))
          chargeTypeStr = "'Pickup','Delivery'";
      else
          chargeTypeStr = "'"+chargeType+"'";
          
      terminalId      = cartageMaster.getTerminalId();
      
      accessLevel     = cartageMaster.getAccessLevel();
      
            
      location_sql         =    "SELECT COUNT(*) NO_ROWS FROM FS_FR_LOCATIONMASTER WHERE LOCATIONID=?";
      currency_sql         =    "SELECT COUNT(*) NO_ROWS FROM FS_COUNTRYMASTER WHERE CURRENCYID=?";
      
      zoneCodeSql          =    "SELECT COUNT(*) NO_ROWS FROM QMS_ZONE_CODE_MASTER M, QMS_ZONE_CODE_DTL D "+
                                "WHERE D.ZONE_CODE=M.ZONE_CODE AND M.SHIPMENT_MODE = ? AND NVL(M.CONSOLE_TYPE,'~')= ? "+
                                "AND M.ORIGIN_LOCATION=? AND D.ZONE=?";
//Added by subrahmanyam for CartageCharges not coming to view
      zoneCodeCaSql        =    "SELECT COUNT(*) NO_ROWS FROM QMS_ZONE_CODE_MASTER_CA CM, QMS_ZONE_CODE_DTL_CA CD "+
                                "WHERE CD.ZONE_CODE=CM.ZONE_CODE AND CM.SHIPMENT_MODE = ? AND NVL(CM.CONSOLE_TYPE,'~')= ? "+
                                "AND CM.LOCATION_ID=? AND CD.ZONE=?";
    
    country_loc_sql        =  "SELECT COUNTRYID FROM FS_FR_LOCATIONMASTER WHERE LOCATIONID= ? ";
//Ended by subrahmanyam for Cartage Charges not coming to view
                                
      if("H".equalsIgnoreCase(accessLevel))
        locationSubQuery     =    "SELECT TERMINALID FROM FS_FR_TERMINALMASTER";
      else
        locationSubQuery     =    "SELECT CHILD_TERMINAL_ID TERM_ID FROM FS_FR_TERMINAL_REGN CONNECT BY PRIOR CHILD_TERMINAL_ID= "+
                                  "PARENT_TERMINAL_ID START WITH PARENT_TERMINAL_ID='"+terminalId+"' UNION SELECT TERMINALID FROM FS_FR_TERMINALMASTER WHERE TERMINALID='"+terminalId+"'";
      
      location_sql         =    "SELECT COUNT(*) NO_ROWS FROM FS_FR_LOCATIONMASTER WHERE LOCATIONID=?";
      locationAccess_sql   =    "SELECT LOCATIONID FROM FS_FR_TERMINALLOCATION WHERE TERMINALID IN("+locationSubQuery+")";
                                
      
      pStmt                =     connection.prepareStatement(location_sql);
      pStmt.setString(1,locationId);
      rs                   =     pStmt.executeQuery();
      
      while(rs.next())
        count = rs.getInt("NO_ROWS");
        
      if(count == 0)
        errors.append("Location Id is Invalid.<br>");
      else if(!("buyView".equalsIgnoreCase(cartageMaster.getOperation()) || "sellView".equalsIgnoreCase(cartageMaster.getOperation())))
      {
        if(rs!=null)
          rs.close();
        if(pStmt!=null)
          pStmt.close();
        
        pStmt                =     connection.prepareStatement(locationAccess_sql);
        rs                   =     pStmt.executeQuery();
        
        while(rs.next())
        {
          if(rs.getString("LOCATIONID").equalsIgnoreCase(locationId))
            flag  = true;
        }
        
        if(!flag)
          errors.append("Location Id ").append(locationId).append(" Does Not Fall Under Your Jurisdiction.<br>");
      }
      
      if(rs!=null)
        rs.close();
      if(pStmt!=null)
        pStmt.close();
        
      count = 0;
      
      pStmt                =     connection.prepareStatement(zoneCodeSql);
//Added by subrahmanyam for cartageCharge view      
      pStmt1                =     connection.prepareStatement(country_loc_sql);
       pStmt2                =     connection.prepareStatement(zoneCodeCaSql);
       pStmt1.setString(1,locationId);
      rs1                   =     pStmt1.executeQuery();
      
      while(rs1.next())
        country = rs1.getString("COUNTRYID");
     
  //Ended by subrahmanyam for Cartage Charges View
  //Commented by subrahmanyam for cartageCharges issue

     /* for(int i=0;i<zoneCodes.length;i++)
      {
        pStmt.setString(1,cartageMaster.getShipmentMode());
        
        if("1".equalsIgnoreCase(cartageMaster.getShipmentMode()))
            pStmt.setString(2,"~");
        else
            pStmt.setString(2,cartageMaster.getConsoleType());
            
        pStmt.setString(3,locationId);
        pStmt.setString(4,zoneCodes[i]);
        rs                   =     pStmt.executeQuery();      
        if(rs.next())
          count = rs.getInt("NO_ROWS"); 
                 if(count == 0)
                {
                  errors.append("The Selected Zone Code(s) are not defined for the selected Shipment Mode, Console Type & Location Id. <br>");
                  break;
                }
        }
                */
//Added by subrahmanyam for cartage charges view issue
      int zoneCodeLen	= zoneCodes.length;
        for(int i=0;i<zoneCodeLen;i++)
      {    
          if("CA".equalsIgnoreCase(country))
          {
                         pStmt2.setString(1,cartageMaster.getShipmentMode());
                        
                        if("1".equalsIgnoreCase(cartageMaster.getShipmentMode()))
                            pStmt2.setString(2,"~");
                        else
                            pStmt2.setString(2,cartageMaster.getConsoleType());
                            
                        pStmt2.setString(3,locationId);
                        pStmt2.setString(4,zoneCodes[i]);
                        rs2                   =     pStmt2.executeQuery();
                        if(rs2.next())
                        count = rs2.getInt("NO_ROWS");  
               
       
                if(count == 0)
                {
                  errors.append("The Selected Zone Code(s) are not defined for the selected Shipment Mode, Console Type & Location Id. <br>");
                  break;
                }
          }
      
          else
          {
               pStmt.setString(1,cartageMaster.getShipmentMode());
            
            if("1".equalsIgnoreCase(cartageMaster.getShipmentMode()))
                pStmt.setString(2,"~");
            else
                pStmt.setString(2,cartageMaster.getConsoleType());
                
            pStmt.setString(3,locationId);
            pStmt.setString(4,zoneCodes[i]);
            rs                   =     pStmt.executeQuery();      
            if(rs.next())
              count = rs.getInt("NO_ROWS");
             if(count == 0)
              {
                errors.append("The Selected Zone Code(s) are not defined for the selected Shipment Mode, Console Type & Location Id. <br>");
                break;
              }
          }
           //Ended by subrahmanyam for cartage charges view issue
      }
      
      if(rs!=null)
        rs.close();
      if(pStmt!=null)
        pStmt.close();
        //added by subrahmanyam
         if(rs1!=null)
        rs1.close();
      if(pStmt1!=null)
        pStmt1.close();
        if(rs2!=null)
        rs2.close();
      if(pStmt2!=null)
        pStmt2.close();   

        //ended by subrahmanyam
        
      count = 0;
      
      pStmt                =     connection.prepareStatement(currency_sql);
      pStmt.setString(1,currencyId);
      rs                   =     pStmt.executeQuery();
      
      while(rs.next())
        count = rs.getInt("NO_ROWS");
        
      if(count == 0)
        errors.append("Currency Id is Invalid.<br>");
      
      if(rs!=null)
        rs.close();
      if(pStmt!=null)
        pStmt.close();
    }
    catch(Exception e)
    {
      //Logger.error(FILE_NAME,"Error while validating Cartage Sell Charges."+e.toString());
      logger.error(FILE_NAME+"Error while validating Cartage Sell Charges."+e.toString());
      e.printStackTrace();
      throw new EJBException(e.toString());
    }
    finally
    {
      ConnectionUtil.closeConnection(connection,pStmt,rs);
    }
    return errors;
  }
  public ArrayList insertCartageBuyCharges(ArrayList cartageBuyCharges) throws EJBException
  {
    //Logger.info(FILE_NAME,"inside insertCartageBuyCharges");
    Connection              connection    =  null;
    PreparedStatement       pStmt0        =  null;
    PreparedStatement       pStmt1        =  null;
    ResultSet               rs            =  null;
    ResultSet               termRs        =  null;
    ArrayList               validList     =  null;
    ArrayList               inValidList   =  null;
    QMSCartageMasterDOB     cartageMaster =  null;
    QMSCartageBuyDtlDOB     buyCartageDOB =  null;
    
    boolean                 flag          =  false;
    
    int                     count         =  0;
    
    String                  accessLevel   =  null;
    
    String   countQuery       =    "SELECT COUNT(*)NO_ROWS FROM QMS_CARTAGE_BUYSELLCHARGES MAS,"+
                                   "QMS_CARTAGE_BUYDTL DTL WHERE MAS.CARTAGE_ID=DTL.CARTAGE_ID AND MAS.LOCATION_ID=? "+
                                   "AND DTL.ZONE_CODE=? AND DTL.CHARGE_TYPE=? AND MAS.SHIPMENT_MODE= ? AND NVL(MAS.CONSOLE_TYPE,'~')= ? "+
                                   "AND DTL.ACTIVEINACTIVE='A'";
    
    
    String   selectQuery       =   "SELECT DISTINCT MAS.TERMINALID FROM QMS_CARTAGE_BUYSELLCHARGES MAS,"+
                                   "QMS_CARTAGE_BUYDTL DTL WHERE MAS.CARTAGE_ID=DTL.CARTAGE_ID AND MAS.LOCATION_ID=? "+
                                   "AND DTL.ZONE_CODE=? AND DTL.CHARGE_TYPE=? AND MAS.SHIPMENT_MODE= ? AND NVL(MAS.CONSOLE_TYPE,'~')= ? "+
                                   "AND DTL.ACTIVEINACTIVE='A' AND TERMINALID IN "+
                                   "(SELECT CHILD_TERMINAL_ID FROM FS_FR_TERMINAL_REGN CONNECT BY PRIOR CHILD_TERMINAL_ID = PARENT_TERMINAL_ID "+
                                   "START WITH PARENT_TERMINAL_ID =? UNION SELECT TERMINALID FROM FS_FR_TERMINALMASTER WHERE TERMINALID=?)";
    
    BuyChargesDAO  dao          =  new BuyChargesDAO();
    int				cartageBuyChargeSize	=	cartageBuyCharges.size();
    
    
    try
    {      
      connection                 =  this.getConnection();
      //pStmt                      =  new PreparedStatement[2];
      cartageMaster              =  (QMSCartageMasterDOB)cartageBuyCharges.get(0);
      accessLevel                =  cartageMaster.getAccessLevel();
      validList                  =  new ArrayList();
      inValidList                =  new ArrayList();
      
      
      pStmt0                   =  connection.prepareStatement(countQuery);
      pStmt1                   =  connection.prepareStatement(selectQuery);
      
      if("H".equalsIgnoreCase(accessLevel))
          validList = cartageBuyCharges;
      else
      {
        for(int i=1;i<=(cartageBuyChargeSize-1);i++)
        {
            buyCartageDOB = (QMSCartageBuyDtlDOB)cartageBuyCharges.get(i);
            
            if(buyCartageDOB.getLineNumber()==0)
            {
              pStmt0.clearParameters();
              
              pStmt0.setString(1,cartageMaster.getLocationId());
              pStmt0.setString(2,buyCartageDOB.getZoneCode());
              pStmt0.setString(3,buyCartageDOB.getChargeType());
              pStmt0.setString(4,cartageMaster.getShipmentMode());
              if("1".equalsIgnoreCase(cartageMaster.getShipmentMode()))
                  pStmt0.setString(5,"~");
              else
                  pStmt0.setString(5,cartageMaster.getConsoleType());
              
              rs  = pStmt0.executeQuery();
              
              if(rs.next())
              {
                count = rs.getInt("NO_ROWS");
              }
              else count  = 0;
              
              if(rs!=null)
                rs.close();
              
              pStmt1.clearParameters();
              
              pStmt1.setString(1,cartageMaster.getLocationId());
              pStmt1.setString(2,buyCartageDOB.getZoneCode());
              pStmt1.setString(3,buyCartageDOB.getChargeType());
              pStmt1.setString(4,cartageMaster.getShipmentMode());
              if("1".equalsIgnoreCase(cartageMaster.getShipmentMode()))
                  pStmt1.setString(5,"~");
              else
                  pStmt1.setString(5,cartageMaster.getConsoleType());
              pStmt1.setString(6,cartageMaster.getTerminalId());
              pStmt1.setString(7,cartageMaster.getTerminalId());
              
              termRs  = pStmt1.executeQuery();
              
              if(termRs.next() || count==0)
                flag    = true;
              else
                flag    = false;
                
              if(termRs!=null)
                termRs.close();
            }
            if(flag)
              validList.add(buyCartageDOB);
            else
              inValidList.add(buyCartageDOB);
            
        }
      }
      if(validList.size()>0)
      {
        if(!"H".equalsIgnoreCase(accessLevel))
          validList.add(0,cartageMaster);
        dao.insertCartageBuyCharges(validList);
      }
    }
    catch(EJBException sql)
    {
      //Logger.error(FILE_NAME,"SQL Exception in insertCartageBuyCharges"+sql);
      logger.error(FILE_NAME+"SQL Exception in insertCartageBuyCharges"+sql);
      sql.printStackTrace();
      throw new EJBException(sql);
    }
    catch(Exception e)
    {
      //Logger.error(FILE_NAME,"Exception in insertCartageBuyCharges"+e);
      logger.error(FILE_NAME+"Exception in insertCartageBuyCharges"+e);
      e.printStackTrace();
      throw new EJBException(e);
    }
    finally
    {
        try
        {                      
            if(rs!=null)
              rs.close();
            if(termRs!=null)
              termRs.close();
            if(pStmt0!=null)
              pStmt0.close();
            if(pStmt1!=null)
              pStmt1.close();
            if(connection!=null)
              connection.close();
        }
        catch (Exception e)
        {
          //Logger.error(FILE_NAME,"Error while Closing Resources"+e);
          logger.error(FILE_NAME+"Error while Closing Resources"+e);
          e.printStackTrace();
        }
    }
    return inValidList;
  }
  
  public ArrayList getBuyCartageChargesFlat(QMSCartageMasterDOB cartageMaster) throws EJBException
  {
    
    Connection        connection      = null;
    //PreparedStatement pStmt           = null;//Commented By RajKumari on 24-10-2008 for Connection Leakages.
    ResultSet         rs              = null;
    
    String            selectQuery     = "";
    
    QMSCartageBuyDtlDOB cartageBuyDtl = null;
    QMSCartageBuyDtlDOB tempDtlDOB    = null;
    
    ArrayList         pickUpList      = new ArrayList();
    ArrayList         delList         = new ArrayList();
    ArrayList         buyChargesList  = new ArrayList();
    
    
    String  locationId                = null;
    String[]  zoneCodes               = null;
    String[]vendorIds                 = null;
    String  currencyId                = null;
    String  chargeType                = null;
    String  chargeBasis               = null;
    String  unitofMeasure             = null;
    String  weightBreak               = null;
    String  rateType                  = null;
    String  shipmentMode              = null;
    
    String  vendors                   = "";
    String  chargeTypeStr             = "";
    
    double  minRate                   = 0;
    double  flatRate                  = 0;
    double  maxRate                   = 0;
    
    double chargeRate                 = 0;
    boolean isPickup                  = true;
    String key                        = "";
    HashMap      hMap                 = null;
    CallableStatement cstmt           = null;
    double conversionFactor           = 0.0;
    boolean addToList                 = true;
    boolean checkFlag                 = false;
    
    try
    {
      connection        =   operationsImpl.getConnection();
      
      locationId         =  cartageMaster.getLocationId();
      zoneCodes           =  cartageMaster.getZoneCodes();
      currencyId         =  cartageMaster.getCurrencyId();
      chargeType         =  cartageMaster.getChargeType();
     /* chargeBasis        =  cartageMaster.getChargeBasis();
      unitofMeasure      =  cartageMaster.getUom();*/
      shipmentMode       =  cartageMaster.getShipmentMode();
      weightBreak        =  cartageMaster.getWeightBreak();
      rateType           =  cartageMaster.getRateType();
      StringBuffer zoneCode    =  new StringBuffer();
      int zoneCodeLen		=	zoneCodes.length;
      for(int i=0;i<zoneCodeLen;i++)
      {
        zoneCode.append(zoneCodes[i]+"~");
      }
      String temp  =  zoneCode.substring(0,zoneCode.length()-1);
      
     /* if("Both".equals(chargeType))
          chargeTypeStr = "'Pickup','Delivery'";
      else
          chargeTypeStr = "'"+chargeType+"'";
          
      String addQuery    = "";
      System.out.print("temp  "+temp);
      if("sellAdd".equalsIgnoreCase(cartageMaster.getOperation()))
         addQuery  =  "AND MAS.CARTAGE_ID NOT IN (SELECT S.CARTAGE_ID FROM QMS_CARTAGE_SELLDTL S,QMS_CARTAGE_BUYDTL D WHERE S.ZONE_CODE IN ("+temp+") AND S.CHARGE_TYPE IN ("+chargeTypeStr+") AND S.CARTAGE_ID=D.CARTAGE_ID AND D.SELLCHARGE_FLAG ='Y' )";
      else if("sellModify".equals(cartageMaster.getOperation())||"sellView".equals(cartageMaster.getOperation()))
         addQuery  =  "AND MAS.CARTAGE_ID IN (SELECT S.CARTAGE_ID FROM QMS_CARTAGE_SELLDTL S,QMS_CARTAGE_BUYDTL D WHERE S.ZONE_CODE IN ("+temp+") AND S.CHARGE_TYPE IN ("+chargeTypeStr+") AND S.CARTAGE_ID=D.CARTAGE_ID AND D.SELLCHARGE_FLAG ='Y' )";
      else if("buyModify".equals(cartageMaster.getOperation())||"buyView".equals(cartageMaster.getOperation())){
			addQuery="";
         }
      selectQuery        =  "SELECT MAS.CARTAGE_ID,MAS.MAX_CHARGEPER_TRUCKLOAD,DTL.ZONE_CODE,"+
                            "DTL.CHARGESLAB,DTL.CHARGERATE,DTL.CHARGE_TYPE,MAS.UOM,MAS.EFFECTIVE_FROM,MAS.VALID_UPTO,DENSITY_CODE FROM "+
                            "QMS_CARTAGE_BUYDTL DTL,QMS_CARTAGE_BUYSELLCHARGES MAS WHERE MAS.CARTAGE_ID=DTL.CARTAGE_ID AND "+
                            "MAS.WEIGHT_BREAK=? AND MAS.RATE_TYPE=? AND MAS.LOCATION_ID=? AND ACTIVEINACTIVE = 'A' "+
                            "AND DTL.ZONE_CODE IN ("+temp+") AND MAS.CHARGE_BASIS =? AND DTL.CHARGE_TYPE IN("+chargeTypeStr+") "+
                            " AND CHARGE_TYPE IN("+chargeTypeStr+") "+addQuery+" "+
                            "ORDER BY CARTAGE_ID ";
                            
                            
      pStmt             =   connection.prepareStatement(selectQuery);*/
      cstmt             =   connection.prepareCall("{call PKG_QMS_CARTAGE_ACC.cartage_buycharges(?,?,?,?,?,?,?,?,?,?)}");
      //System.out.println(cartageMaster.getOperation()+"-"+cartageMaster.getTerminalId()+"-"+chargeType+"-"+rateType+"-"+locationId+"-"+chargeBasis+"-"+weightBreak+"-"+temp);
      cstmt.setString(1,cartageMaster.getOperation());
      cstmt.setString(2,cartageMaster.getTerminalId());
      cstmt.setString(3,chargeType);
      cstmt.setString(4,rateType);
      cstmt.setString(5,locationId);
      cstmt.setString(6,shipmentMode);
      cstmt.setString(7,temp);
      cstmt.setString(8,weightBreak);
      cstmt.registerOutParameter(9,OracleTypes.CURSOR);
      cstmt.registerOutParameter(10,OracleTypes.CURSOR);      
      cstmt.execute();
      //rs                =   pStmt.executeQuery();
      
      rs                  =   (ResultSet)cstmt.getObject(9);      
      
      int i=0;
      while(rs.next())
      {   
      
          conversionFactor = operationsImpl.getConvertionFactor(rs.getString("CURRENCY"),cartageMaster.getCurrencyId());
          
          key = rs.getString("CARTAGE_ID")+rs.getString("ZONE_CODE")+rs.getString("CHARGE_TYPE");
          chargeRate  = operationsImpl.getConvertedAmt(rs.getDouble("CHARGERATE"),conversionFactor);
            
          if(i!=0 && hMap.containsKey(key))
          {
            cartageBuyDtl  =    (QMSCartageBuyDtlDOB)hMap.get(key);
            if("MIN".equalsIgnoreCase(rs.getString("CHARGESLAB")))
                cartageBuyDtl.setMinRate(chargeRate);
            else if("FLAT".equalsIgnoreCase(rs.getString("CHARGESLAB")))
                cartageBuyDtl.setFlatRate(chargeRate);
            else if("MAX".equalsIgnoreCase(rs.getString("CHARGESLAB")))
                cartageBuyDtl.setMaxRate(chargeRate); 
//@@Added by subrahmanyam for the Enhancement 170759 on 01/06/09                
            else if("BASE".equalsIgnoreCase(rs.getString("CHARGESLAB")))
                cartageBuyDtl.setBaseRate(chargeRate);
//@@Ended by subrahmanyam for the Enhancement 170759 on 01/06/09                                
            //cartageBuyDtl.setLineNumber(rs.getInt("LINE_NO"));
            addToList      =  false;
          }
          else
          {             
            cartageBuyDtl  =    new QMSCartageBuyDtlDOB();
            cartageBuyDtl.setCartageId(rs.getLong("CARTAGE_ID"));
            cartageBuyDtl.setMaxChargeFlag(rs.getString("MAX_CHARGEPER_TRUCKLOAD"));
            cartageBuyDtl.setZoneCode(rs.getString("ZONE_CODE"));
            cartageBuyDtl.setChargeType(rs.getString("CHARGE_TYPE"));
            cartageBuyDtl.setEffectiveFrom(rs.getTimestamp("EFFECTIVE_FROM"));
            cartageBuyDtl.setValidUpto(rs.getTimestamp("VALID_UPTO"));            
            cartageBuyDtl.setDensityRatio(rs.getString("DENSITY_CODE"));
            cartageBuyDtl.setChargeBasis(rs.getString("CHARGE_BASIS"));
            if("MIN".equalsIgnoreCase(rs.getString("CHARGESLAB")))
                cartageBuyDtl.setMinRate(chargeRate);
            else if("FLAT".equalsIgnoreCase(rs.getString("CHARGESLAB")))
                cartageBuyDtl.setFlatRate(chargeRate);
            else if("MAX".equalsIgnoreCase(rs.getString("CHARGESLAB")))
                cartageBuyDtl.setMaxRate(chargeRate); 
//@@Added by subrahmanyam for the Enhancement 170759 on 01/06/09                
            else if("BASE".equalsIgnoreCase(rs.getString("CHARGESLAB")))
                cartageBuyDtl.setBaseRate(chargeRate);
//@@Ended by subrahmanyam for the Enhancement 170759 on 01/06/09                   
            //cartageBuyDtl.setLineNumber(rs.getInt("LINE_NO"));
            //cartageBuyDtl.setFlatRate(round(chargeRate));  
            
            hMap           =    new HashMap();
            hMap.put(key,cartageBuyDtl);
            addToList      =  true;
            
            /*if("Pickup".equals(rs.getString("CHARGE_TYPE")))
              pickUpList.add(cartageBuyDtl);
            else if("Delivery".equals(rs.getString("CHARGE_TYPE")))
              delList.add(cartageBuyDtl);*/
          }
          i++;
          
          if(addToList)
          {
            cartageBuyDtl.setSlabRates(hMap);
            if("Pickup".equals(rs.getString("CHARGE_TYPE")))
            {
                tempDtlDOB  = null;
                checkFlag   = false;
                int delListSize	=	delList.size();
                for(int j=0;j<delListSize;j++)
                {
                  tempDtlDOB  = (QMSCartageBuyDtlDOB)delList.get(j);
                  if(tempDtlDOB!=null && tempDtlDOB.getZoneCode().equalsIgnoreCase(rs.getString("ZONE_CODE")))
                  {
                    pickUpList.remove(j);
                    pickUpList.add(cartageBuyDtl);
                    checkFlag = true;
                  }
                }
                if(!checkFlag)
                {
                  pickUpList.add(cartageBuyDtl);
                  delList.add(null);
                }
            }
            else
            {
                tempDtlDOB  = null;
                checkFlag   = false;
                int pickUpListSize	=	pickUpList.size();
                for(int j=0;j<pickUpListSize;j++)
                {
                  tempDtlDOB  = (QMSCartageBuyDtlDOB)pickUpList.get(j);
                  if(tempDtlDOB!=null && tempDtlDOB.getZoneCode().equalsIgnoreCase(rs.getString("ZONE_CODE")))
                  {
                    delList.remove(j);
                    delList.add(cartageBuyDtl);
                    checkFlag = true;
                  }
                }
                if(!checkFlag)
                {
                  pickUpList.add(null);
                  delList.add(cartageBuyDtl);
                }
                /*pickUpList.add(null);
                delList.add(cartageBuyDtl);*/
            }
          }
      }
      buyChargesList.add(pickUpList);
      buyChargesList.add(delList);

    }
    catch(Exception e)
    {
      //Logger.error(FILE_NAME,"Error while retreiving Buy Charges"+e);
      logger.error(FILE_NAME+"Error while retreiving Buy Charges"+e);
      e.printStackTrace();
      throw new EJBException(e);
    }
    finally
    {
      ConnectionUtil.closeConnection(connection,cstmt,rs);
    }
    return buyChargesList;
  }
 
  
   public ArrayList getSellCartageChargesFlat(QMSCartageMasterDOB cartageMaster) throws EJBException,SQLException
  {
    
    Connection        connection      = null;
    PreparedStatement pStmt           = null;
    ResultSet         rs              = null;    
    String            selectQuery     = "";    
    QMSCartageSellDtlDOB cartageSellDtl = null;
    
    ArrayList         sellDtlList      = new ArrayList();
    String            chargeTypeStr    = "";
    double            conversionFactor = 0.0;
    try
    {
      connection        =   operationsImpl.getConnection();
      
      if("Both".equals(cartageMaster.getChargeType()))
          chargeTypeStr = "'Pickup','Delivery'";
      else
          chargeTypeStr = "'"+cartageMaster.getChargeType()+"'";
      
      selectQuery        =  "SELECT MAS.CARTAGE_ID CARTAGE_ID,MAS.MAX_CHARGEPER_TRUCKLOAD MAX_CHARGEPER_TRUCKLOAD,DTL.ZONE_CODE ZONE_CODE,DTL.CHARGESLAB CHARGESLAB,DTL.CHARGERATE CHARGERATE,DTL.CURRENCY CURRENCY, "+
                            "DTL.CHARGE_TYPE CHARGE_TYPE,MAS.UOM UOM,DTL.EFFECTIVE_FROM EFFECTIVE_FROM,DTL.VALID_UPTO VALID_UPTO,OVERALL_MARGIN,MARGIN_TYPE ,MARGIN_BASIS, "+
                            "MARGIN  FROM QMS_CARTAGE_SELLDTL DTL,QMS_CARTAGE_BUYSELLCHARGES MAS  "+
                            "WHERE MAS.CARTAGE_ID=DTL.CARTAGE_ID AND MAS.WEIGHT_BREAK=? AND MAS.RATE_TYPE=?  AND MAS.LOCATION_ID=?  "+//AND MAS.ZONE_CODE=?
                            "AND DTL.CHARGE_BASIS =? AND DTL.ZONE_CODE=? AND MAS.CARTAGE_ID = ? AND ACTIVEINACTIVE='A' AND DTL.CHARGE_TYPE = ("+chargeTypeStr+") ORDER BY MAS.CARTAGE_ID";
      pStmt = connection.prepareStatement(selectQuery);                      
      pStmt.setString(1,cartageMaster.getWeightBreak());
      pStmt.setString(2,cartageMaster.getRateType());
      //pStmt.setString(3,cartageMaster.getZoneCode());
      pStmt.setString(3,cartageMaster.getLocationId());
      pStmt.setString(4,cartageMaster.getChargeBasis());                      
      pStmt.setString(5,cartageMaster.getZoneCode());
      pStmt.setLong(6,cartageMaster.getCartageId());    
      
      rs   =  pStmt.executeQuery();
      
      while(rs.next())
      {
        cartageSellDtl  =  new QMSCartageSellDtlDOB();
        cartageSellDtl.setOverallMargin(rs.getString("OVERALL_MARGIN"));
        cartageSellDtl.setMargin(rs.getDouble("MARGIN"));
        cartageSellDtl.setMarginBasis("MARGIN_BASIS");
        cartageSellDtl.setMarginType(rs.getString("MARGIN_TYPE"));
        cartageSellDtl.setChargeSlab(rs.getString("CHARGESLAB"));        
        conversionFactor = operationsImpl.getConvertionFactor(rs.getString("CURRENCY"),cartageMaster.getCurrencyId());
        cartageSellDtl.setChargeRate(operationsImpl.getConvertedAmt(rs.getDouble("CHARGERATE"),conversionFactor));
        cartageSellDtl.setChargeType(rs.getString("CHARGE_TYPE"));
        cartageSellDtl.setCartageId(rs.getLong("CARTAGE_ID"));
        sellDtlList.add(cartageSellDtl);
      }
      
    }
     catch(SQLException e)
    {
      //Logger.error(FILE_NAME,"Error while retreiving Buy Charges(SQLException)"+e);
      logger.error(FILE_NAME+"Error while retreiving Buy Charges(SQLException)"+e);
      e.printStackTrace();
      throw new EJBException(e);
    }
    catch(Exception e)
    {
      //Logger.error(FILE_NAME,"Error while retreiving Buy Charges"+e);
      logger.error(FILE_NAME+"Error while retreiving Buy Charges"+e);
      e.printStackTrace();
      throw new EJBException(e);
    }
    finally
    {
      ConnectionUtil.closeConnection(connection,pStmt,rs);
    }
    return sellDtlList;
  }
  
 public ArrayList getBuyCartageChargesSlab(QMSCartageMasterDOB cartageMaster) throws EJBException
  {
    Connection        connection      = null;
    //PreparedStatement pStmt           = null;//Commented By RajKumari on 24-10-2008 for Connection Leakages.
    ResultSet         rs              = null;
    
    String            slabQuery       = "";
    String            selectQuery     = "";
    
    QMSCartageBuyDtlDOB cartageBuyDtl = null;
    
    ArrayList         pickUpList      = new ArrayList();
    ArrayList         delList         = new ArrayList();
    ArrayList         buyChargesList  = new ArrayList();
    
    ArrayList         slabList        = new ArrayList();
    
    HashMap           hMap            = null; 
    
    
    String  locationId                = null;
    String[]  zoneCodes               = null;
    //String[]vendorIds                 = null;
    String  currencyId                = null;
    String  chargeType                = null;
    String  chargeBasis               = null;
    String  unitofMeasure             = null;
    String  weightBreak               = null;
    String  rateType                  = null;
    String  shipmentMode              = null;
    
    String[] slabBreak                = null;
    
    //String  vendors                   = "";
    String  chargeTypeStr             = "";
    
    String  key                       = "";
    double  chargeRate                = 0;
    boolean addToList                 = true;
    CallableStatement cstmt           = null;
    double conversionFactor           = 0.0;
    try
    {
      connection        =   operationsImpl.getConnection();
      
      locationId         =  cartageMaster.getLocationId();
      zoneCodes           =  cartageMaster.getZoneCodes();
      currencyId         =  cartageMaster.getCurrencyId();
      chargeType         =  cartageMaster.getChargeType();
      /*chargeBasis        =  cartageMaster.getChargeBasis();
      unitofMeasure      =  cartageMaster.getUom();*/
      shipmentMode       =  cartageMaster.getShipmentMode();
      weightBreak        =  cartageMaster.getWeightBreak();
      rateType           =  cartageMaster.getRateType();
      
      StringBuffer zoneCode    =  new StringBuffer(); 
      int zoneCodesLen		=	zoneCodes.length;
      for(int i=0;i<zoneCodesLen;i++)
      {
        zoneCode.append(zoneCodes[i]+"~");
      }
      String temp  =  zoneCode.substring(0,zoneCode.length()-1);
      
    /*  String   addQuery  =  "";
      if("Both".equals(chargeType))
          chargeTypeStr = "'Pickup','Delivery'";
      else
          chargeTypeStr = "'"+chargeType+"'";
          System.out.println("temp  "+temp);
      if("sellAdd".equalsIgnoreCase(cartageMaster.getOperation()))
         addQuery  =  "AND MAS.CARTAGE_ID NOT IN (SELECT S.CARTAGE_ID FROM QMS_CARTAGE_SELLDTL S,QMS_CARTAGE_BUYDTL D WHERE S.ZONE_CODE IN ("+temp+") AND S.CHARGE_TYPE IN ("+chargeTypeStr+") AND S.CARTAGE_ID=D.CARTAGE_ID AND D.SELLCHARGE_FLAG ='Y' )";
      else if("buyModify".equals(cartageMaster.getOperation())||"buyView".equals(cartageMaster.getOperation()))
      {
       addQuery=" ";
      }
      else
         addQuery  =  "AND MAS.CARTAGE_ID IN (SELECT S.CARTAGE_ID FROM QMS_CARTAGE_SELLDTL S,QMS_CARTAGE_BUYDTL D WHERE S.ZONE_CODE IN ("+temp+") AND S.CHARGE_TYPE IN ("+chargeTypeStr+") AND S.CARTAGE_ID=D.CARTAGE_ID AND D.SELLCHARGE_FLAG ='Y' )";
       
                 System.out.println("cartageMaster.getOperation()  "+cartageMaster.getOperation());
      slabQuery = "SELECT DISTINCT DTL.CHARGESLAB FROM QMS_CARTAGE_BUYDTL DTL,QMS_CARTAGE_BUYSELLCHARGES MAS WHERE "+
                  "MAS.CARTAGE_ID=DTL.CARTAGE_ID AND MAS.WEIGHT_BREAK=? AND MAS.RATE_TYPE=? AND MAS.LOCATION_ID=? "+
                  "AND DTL.ZONE_CODE IN ("+temp+") AND MAS.CHARGE_BASIS =? AND DTL.CHARGE_TYPE IN("+chargeTypeStr+") AND ACTIVEINACTIVE ='A' "+
                  " AND CHARGE_TYPE IN("+chargeTypeStr+")"+
                  "AND DTL.CHARGESLAB NOT IN ('MIN','MAX') "+addQuery+" ORDER BY TO_NUMBER(DTL.CHARGESLAB)";//AND MAS.CARTAGE_ID NOT IN (SELECT CARTAGE_ID FROM QMS_CARTAGE_SELLDTL WHERE ZONE_CODE=? )
                  
      pStmt           =   connection.prepareStatement(slabQuery);
      
      pStmt.setString(1,weightBreak);
      pStmt.setString(2,rateType);
      pStmt.setString(3,locationId);
      //pStmt.setString(4,zoneCode);
      pStmt.setString(4,chargeBasis);
     // pStmt.setString(6,zoneCode);
      
      rs              =   pStmt.executeQuery();*/
      
      cstmt             =   connection.prepareCall("{call PKG_QMS_CARTAGE_ACC.cartage_buycharges(?,?,?,?,?,?,?,?,?,?)}");
      //System.out.println(cartageMaster.getOperation()+"-"+cartageMaster.getTerminalId()+"-"+chargeType+"-"+rateType+"-"+locationId+"-"+chargeBasis+"-"+weightBreak+"-"+temp);
      cstmt.setString(1,cartageMaster.getOperation());
      cstmt.setString(2,cartageMaster.getTerminalId());
      cstmt.setString(3,chargeType);
      cstmt.setString(4,rateType);
      cstmt.setString(5,locationId);
     // cstmt.setString(6,chargeBasis);
      cstmt.setString(6,shipmentMode);
      cstmt.setString(7,temp);
      cstmt.setString(8,weightBreak);
      cstmt.registerOutParameter(9,OracleTypes.CURSOR);
      cstmt.registerOutParameter(10,OracleTypes.CURSOR);      
      cstmt.execute();
      rs   = (ResultSet)cstmt.getObject(10);
      slabList.add("BASE");//@@ Added by subrahmanyam for the Enhancement 170759 on 01/06/09
      slabList.add("MIN");

      
      while(rs.next())
      {
 
        slabList.add(rs.getString("CHARGESLAB"));
      }
      slabList.add("MAX");
      
      
    /*  selectQuery =  "SELECT MAS.CARTAGE_ID,MAS.MAX_CHARGEPER_TRUCKLOAD,DTL.ZONE_CODE,"+
                     "DTL.CHARGERATE,DTL.CHARGESLAB,DTL.CHARGE_TYPE,MAS.UOM,MAS.EFFECTIVE_FROM,MAS.VALID_UPTO,DTL.CHARGERATE_INDICATOR,"+
                     "DTL.LOWERBOUND,DTL.UPPERBOUND,DENSITY_CODE "+
                     "FROM QMS_CARTAGE_BUYDTL DTL,QMS_CARTAGE_BUYSELLCHARGES MAS WHERE MAS.CARTAGE_ID=DTL.CARTAGE_ID AND MAS.WEIGHT_BREAK=? "+
                     "AND MAS.RATE_TYPE=? AND MAS.LOCATION_ID=? "+
                     "AND DTL.ZONE_CODE IN ("+temp+") AND MAS.CHARGE_BASIS =? AND DTL.CHARGE_TYPE IN("+chargeTypeStr+") AND ACTIVEINACTIVE ='A' "+
                     " AND CHARGE_TYPE IN("+chargeTypeStr+") "+//AND MAS.CARTAGE_ID NOT IN (SELECT CARTAGE_ID FROM QMS_CARTAGE_SELLDTL WHERE ZONE_CODE=? )
                     ""+addQuery+"ORDER BY CARTAGE_ID,CHARGE_TYPE DESC";
                        */    
      if(rs!=null)
          rs.close();
     /* if(pStmt!=null)
          pStmt.close();*///Commented By RajKumari on 24-10-2008 for Connection Leakages.
      rs   = (ResultSet)cstmt.getObject(9);
          
    /*  pStmt             =   connection.prepareStatement(selectQuery);
      pStmt.setString(1,weightBreak);
      pStmt.setString(2,rateType);
      pStmt.setString(3,locationId);
      //pStmt.setString(4,zoneCode);
      pStmt.setString(4,chargeBasis);
      //pStmt.setString(6,zoneCode);
      rs                =   pStmt.executeQuery();*/
      
      for(int i=0;rs.next();i++)
      {
        key            =    rs.getString("CARTAGE_ID")+rs.getString("CHARGE_TYPE")+rs.getString("ZONE_CODE");
        conversionFactor = operationsImpl.getConvertionFactor(rs.getString("CURRENCY"),cartageMaster.getCurrencyId());
        
        chargeRate = operationsImpl.getConvertedAmt(rs.getDouble("CHARGERATE"),conversionFactor);
        
        if(i!=0 && hMap.containsKey(key))
        {
          hMap.put(rs.getString("CHARGESLAB"),Double.toString(chargeRate));
          hMap.put("Ind"+rs.getString("CHARGESLAB"),rs.getString("CHARGERATE_INDICATOR"));
          hMap.put("LB"+rs.getString("CHARGESLAB"),rs.getString("LOWERBOUND"));
          hMap.put("UB"+rs.getString("CHARGESLAB"),rs.getString("UPPERBOUND"));
          hMap.put("LNO"+rs.getString("CHARGESLAB"),rs.getString("LINE_NO"));
          addToList   = false;
        }
        else
        {
          cartageBuyDtl  =  new QMSCartageBuyDtlDOB();
          cartageBuyDtl.setCartageId(rs.getLong("CARTAGE_ID"));
          cartageBuyDtl.setMaxChargeFlag(rs.getString("MAX_CHARGEPER_TRUCKLOAD"));
          cartageBuyDtl.setZoneCode(rs.getString("ZONE_CODE"));
          cartageBuyDtl.setChargeType(rs.getString("CHARGE_TYPE"));
          cartageBuyDtl.setEffectiveFrom(rs.getTimestamp("EFFECTIVE_FROM"));
          cartageBuyDtl.setValidUpto(rs.getTimestamp("VALID_UPTO"));
          cartageBuyDtl.setDensityRatio(rs.getString("DENSITY_CODE"));
          cartageBuyDtl.setChargeBasis(rs.getString("CHARGE_BASIS"));
          hMap           =  new HashMap();
          hMap.put(rs.getString("CHARGESLAB"),Double.toString(chargeRate));
          hMap.put("Ind"+rs.getString("CHARGESLAB"),rs.getString("CHARGERATE_INDICATOR"));
          hMap.put("LB"+rs.getString("CHARGESLAB"),rs.getString("LOWERBOUND"));
          hMap.put("UB"+rs.getString("CHARGESLAB"),rs.getString("UPPERBOUND"));
          hMap.put("LNO"+rs.getString("CHARGESLAB"),rs.getString("LINE_NO"));
          hMap.put(key,"");
          addToList      =  true;
        }
        
        if(addToList)
        {
          cartageBuyDtl.setSlabRates(hMap);
          if("Pickup".equals(rs.getString("CHARGE_TYPE")))
              pickUpList.add(cartageBuyDtl);
          else
              delList.add(cartageBuyDtl);
        }
      }
      buyChargesList.add(pickUpList);
      buyChargesList.add(delList);
      buyChargesList.add(slabList);
    }
    catch(Exception e)
    {
      //Logger.error(FILE_NAME,"Error while retrieving Buy Charges Slab"+e);
      logger.error(FILE_NAME+"Error while retrieving Buy Charges Slab"+e);
      e.printStackTrace();
      throw new EJBException(e);
    }
    finally
    {
      ConnectionUtil.closeConnection(connection,cstmt,rs);
    }
    return buyChargesList;
  }
  public ArrayList getBuyCartageChargesList(QMSCartageMasterDOB cartageMaster) throws EJBException
  {
    Connection        connection      = null;
    CallableStatement cstmt           = null;
    ResultSet         rs              = null;
    
    QMSCartageBuyDtlDOB cartageBuyDtl = null;
    
    ArrayList         pickUpList      = new ArrayList();
    ArrayList         delList         = new ArrayList();
    ArrayList         buyChargesList  = new ArrayList();
    
    ArrayList        slabList         = new ArrayList();
    
    HashMap           hMap            = null; 
    
    
    String  locationId                = null;
    String[]  zoneCodes               = null;
    String  currencyId                = null;
    String  chargeType                = null;
    String  chargeBasis               = null;
    String  unitofMeasure             = null;
    String  weightBreak               = null;
    String  rateType                  = null;
    String  shipmentMode              = null;
    
    StringBuffer  zoneCode            = new StringBuffer();
    String  zones                     = "";
    
    String  key                       = "";
    double  chargeRate                = 0;
    boolean addToList                 = true;
    double conversionFactor           = 0.0;
    int     noOfZones                 = 0;
    try
    {
      connection         =  getConnection();
      locationId         =  cartageMaster.getLocationId();
      zoneCodes          =  cartageMaster.getZoneCodes();
      currencyId         =  cartageMaster.getCurrencyId();
      chargeType         =  cartageMaster.getChargeType();
      shipmentMode       =  cartageMaster.getShipmentMode();
      weightBreak        =  cartageMaster.getWeightBreak();
      rateType           =  cartageMaster.getRateType();
      
      if(zoneCodes!=null)
        noOfZones = zoneCodes.length;
        
      for(int i=0;i<noOfZones;i++)
      {
        zoneCode.append(zoneCodes[i]+"~");
      }
      
      zones  =  zoneCode.substring(0,zoneCode.length()-1);
      
      cstmt             =   connection.prepareCall("{call PKG_QMS_CARTAGE_ACC.cartage_buycharges(?,?,?,?,?,?,?,?,?,?)}");
      cstmt.setString(1,cartageMaster.getOperation());
      cstmt.setString(2,cartageMaster.getTerminalId());
      cstmt.setString(3,chargeType);
      cstmt.setString(4,rateType);
      cstmt.setString(5,locationId);
      cstmt.setString(6,shipmentMode);
      cstmt.setString(7,zones);
      cstmt.setString(8,weightBreak);
      cstmt.registerOutParameter(9,OracleTypes.CURSOR);
      cstmt.registerOutParameter(10,OracleTypes.CURSOR);      
      cstmt.execute();
      rs   = (ResultSet)cstmt.getObject(10);
      
      while(rs.next())
      {
        slabList.add(rs.getString("CHARGESLAB"));
      }
      
      if(rs!=null)
          rs.close();
          
      rs   = (ResultSet)cstmt.getObject(9);
      
      for(int i=0;rs.next();i++)
      {
        key            =    rs.getString("CARTAGE_ID")+rs.getString("CHARGE_TYPE")+rs.getString("ZONE_CODE");
        conversionFactor = operationsImpl.getConvertionFactor(rs.getString("CURRENCY"),cartageMaster.getCurrencyId());
        chargeRate = operationsImpl.getConvertedAmt(rs.getDouble("CHARGERATE"),conversionFactor);
        
        if(i!=0 && hMap.containsKey(key))
        {
          hMap.put(rs.getString("CHARGESLAB"),""+chargeRate);
          addToList   = false;
        }
        else
        {
          cartageBuyDtl  =  new QMSCartageBuyDtlDOB();
          cartageBuyDtl.setCartageId(rs.getLong("CARTAGE_ID"));
          cartageBuyDtl.setZoneCode(rs.getString("ZONE_CODE"));
          cartageBuyDtl.setChargeType(rs.getString("CHARGE_TYPE"));
          cartageBuyDtl.setEffectiveFrom(rs.getTimestamp("EFFECTIVE_FROM"));
          cartageBuyDtl.setValidUpto(rs.getTimestamp("VALID_UPTO"));
          cartageBuyDtl.setDensityRatio(rs.getString("DENSITY_CODE"));
          cartageBuyDtl.setChargeBasis(rs.getString("CHARGE_BASIS"));
          hMap           =  new HashMap();
          hMap.put(rs.getString("CHARGESLAB"),""+chargeRate);
          hMap.put(key,"");
          addToList      =  true;
        }
        
        if(addToList)
        {
          cartageBuyDtl.setSlabRates(hMap);
          if("Pickup".equals(rs.getString("CHARGE_TYPE")))
              pickUpList.add(cartageBuyDtl);
          else
              delList.add(cartageBuyDtl);
        }
      }
      
      buyChargesList.add(pickUpList);
      buyChargesList.add(delList);
      buyChargesList.add(slabList);
   }
    catch(Exception e)
    {
      e.printStackTrace();
    }
    finally
    {
      ConnectionUtil.closeConnection(connection,cstmt,rs);
    }
    return buyChargesList;
  }
  public void insertCartageSellCharges(ArrayList cartageSellChargesList) throws EJBException
  {
    SellChargesDAO  dao   =  new SellChargesDAO();
    try
    {      
      dao.insertCartageSellCharges(cartageSellChargesList);
    }
    catch(Exception e)
    {
      //Logger.error(FILE_NAME,"error in insertCartageSellCharges");
      logger.error(FILE_NAME+"error in insertCartageSellCharges");
      e.printStackTrace();
      throw new EJBException(e);
    }
    finally
    {
      dao = null;
    }
  }
  private double round(double number)
  {
    
    int d = 2;
    double f = Math.pow(10, d);
    number += Math.pow(10, - (d + 1)); 
    number = Math.round(number * f) / f;
    number += Math.pow(10, - (d + 1)); 
    String value  = Double.toString(number);
    return Double.parseDouble(value.substring(0, value.indexOf('.') + d + 1));
  }
  public HashMap addChargeDiscriptionDtls(ArrayList chargeDescList,ESupplyGlobalParameters loginbean)
  { 
    BuychargesHDRDOB  chargeDOB  =  null;
    Connection        connection =  null;
    PreparedStatement pStmt      =  null;
    PreparedStatement pStmt1      =  null;
    PreparedStatement pStmtCharge =  null;
    PreparedStatement pStmtUpdate =  null;
    ResultSet         rs          =  null;
    ResultSet         rsCharge    =  null;
//    String            sqlDescId = "SELECT CHARGEDESCID  FROM QMS_CHARGEDESCMASTER WHERE CHARGEDESCID =? AND TERMINALID =? ";
    StringBuffer            sqlDescId = new StringBuffer("SELECT CHARGEDESCID  FROM QMS_CHARGEDESCMASTER WHERE CHARGEDESCID =? AND INACTIVATE='N' AND TERMINALID IN ( ");
    sqlDescId.append(" SELECT TERMINALID FROM FS_FR_TERMINALMASTER WHERE TERMINALID = ?  ");
    sqlDescId.append(" UNION");
    sqlDescId.append(" SELECT PARENT_TERMINAL_ID TERMINALID from fs_fr_terminal_regn ");
    sqlDescId.append(" connect by prior PARENT_TERMINAL_ID=CHILD_TERMINAL_ID start with CHILD_TERMINAL_ID= ?");
    sqlDescId.append(" UNION");
    sqlDescId.append(" SELECT TERMINALID FROM FS_FR_TERMINALMASTER WHERE OPER_ADMIN_FLAG='H')");
    
    StringBuffer  updateChildData = new StringBuffer(" UPDATE QMS_CHARGEDESCMASTER SET INACTIVATE ='Y' WHERE CHARGEDESCID =? AND TERMINALID IN (");
                  updateChildData.append(" select CHILD_TERMINAL_ID TERMINALID from fs_fr_terminal_regn");
                  updateChildData.append(" connect by prior CHILD_TERMINAL_ID=PARENT_TERMINAL_ID start with PARENT_TERMINAL_ID=? )");
                  
    StringBuffer  selectAllchild = new StringBuffer(" UPDATE QMS_CHARGEDESCMASTER SET INACTIVATE ='Y' WHERE CHARGEDESCID =? AND TERMINALID IN (");
                  selectAllchild.append(" SELECT TERMINALID  FROM FS_FR_TERMINALMASTER WHERE ACTV_FLAG='A' ) ");

    
    
    
    
    String            sqlCharge  = " SELECT CHARGE_ID   FROM QMS_CHARGESMASTER WHERE CHARGE_ID  =? AND SHIPMENT_MODE IN (?,?,?,?) AND  ( INVALIDATE ='F' or INVALIDATE is null)";

    String            sql       = " INSERT INTO QMS_CHARGEDESCMASTER (CHARGEID,CHARGEDESCID,REMARKS,TERMINALID,SHIPMENTMODE,INVALIDATE,INACTIVATE,ID,EXT_CHARGE_NAME )VALUES(?,?,?,?,?,'F','N',CHARGE_DESCMASTER_SEQ.NEXTVAL,?)";
    OperationsImpl    operationsImpl = new OperationsImpl();
    ArrayList         existingList   = new ArrayList();
    ArrayList         nonExistingList   = new ArrayList();
    HashMap           map               = new HashMap();
    String            shipMode      = null;
    try
    {
      connection     =   operationsImpl.getConnection();
      pStmt          =   connection.prepareStatement(sql);
      pStmt1         =   connection.prepareStatement(sqlDescId.toString());
      pStmtCharge    =   connection.prepareStatement(sqlCharge);
      if("HO_TERMINAL".equals(loginbean.getAccessType()))
        pStmtUpdate    =   connection.prepareStatement(selectAllchild.toString());
      else
        pStmtUpdate    =   connection.prepareStatement(updateChildData.toString());
      int chargeDescListSize	=	chargeDescList.size();
      for(int i=0;i<chargeDescListSize;i++)
      {
        chargeDOB  =  (BuychargesHDRDOB)chargeDescList.get(i);
        //Logger.info(FILE_NAME,"chargeDOB.getChargeId()   "+chargeDOB.getChargeId());
        //Logger.info(FILE_NAME,"i   "+i);
        shipMode = chargeDOB.getShipmentMode()+"";
        
        pStmtCharge.clearParameters();
        pStmtCharge.setString(1,chargeDOB.getChargeId()); 
        
        if("1".equals(shipMode))
        { 
          pStmtCharge.setString(2,shipMode);
          pStmtCharge.setString(3,"3");
          pStmtCharge.setString(4,"5");
          pStmtCharge.setString(5,"7");
        }else if("2".equals(shipMode))
        { 
          pStmtCharge.setString(2,shipMode);
          pStmtCharge.setString(3,"3");
          pStmtCharge.setString(4,"6");
          pStmtCharge.setString(5,"7");
        }else if("4".equals(shipMode))
        { 
          pStmtCharge.setString(2,shipMode);
          pStmtCharge.setString(3,"5");
          pStmtCharge.setString(4,"6");
          pStmtCharge.setString(5,"7");
        }
        else if("3".equals(shipMode) || "5".equals(shipMode) || "6".equals(shipMode) )
        { 
          pStmtCharge.setString(2,shipMode);
          pStmtCharge.setString(3,"7");
          pStmtCharge.setString(4,"");
          pStmtCharge.setString(5,"");
        }else if("7".equals(shipMode) )
        { 
          pStmtCharge.setString(2,shipMode);
          pStmtCharge.setString(3,"");
          pStmtCharge.setString(4,"");
          pStmtCharge.setString(5,"");
        }
        
        
        
          
        //rs  = pStmt1.executeQuery();
        rsCharge = pStmtCharge.executeQuery();
        
        if(rsCharge.next())
        {
          pStmt1.clearParameters();
          pStmt1.setString(1,chargeDOB.getChargeDescId());  
          pStmt1.setString(2,chargeDOB.getTerminalId());          
          pStmt1.setString(3,chargeDOB.getTerminalId());
          rs  = pStmt1.executeQuery();
          if(rs.next())
          {
              chargeDOB.setChargeDesc("The Charge Description Id "+chargeDOB.getChargeDescId()+" Already Exists.");
              existingList.add(chargeDOB);            
          }
          else
          {
            pStmtUpdate.clearParameters();
            pStmtUpdate.setString(1,chargeDOB.getChargeDescId());
            
            if(!"HO_TERMINAL".equals(loginbean.getAccessType()))
            {
                pStmtUpdate.setString(2,chargeDOB.getTerminalId());
            }
            pStmtUpdate.executeUpdate();
                
            pStmt.clearParameters();
            pStmt.setString(1,chargeDOB.getChargeId());
            pStmt.setString(2,chargeDOB.getChargeDescId());
            pStmt.setString(3,chargeDOB.getRemarks());
            pStmt.setString(4,chargeDOB.getTerminalId());
            pStmt.setInt(5,chargeDOB.getShipmentMode()); 
            pStmt.setString(6,chargeDOB.getExternalChargeName());
            pStmt.executeUpdate();
            nonExistingList.add(chargeDOB);            
            
          }
        }else
        {
              chargeDOB.setChargeDesc("The Charge Id "+chargeDOB.getChargeId()+" is Invalid.");
              existingList.add(chargeDOB);            
        }
        
       /* 
        if(rs.next() ){
           chargeDOB.setChargeDesc("This Description Id is already Exist");
           existingList.add(chargeDOB);           
        }
        else if(rsCharge.next()){
        pStmt.clearParameters();
        pStmt.setString(1,chargeDOB.getChargeId());
        pStmt.setString(2,chargeDOB.getChargeDescId());
        pStmt.setString(3,chargeDOB.getRemarks());
        pStmt.setString(4,chargeDOB.getTerminalId());
        pStmt.setInt(5,chargeDOB.getShipmentMode());        
        pStmt.executeUpdate();
        nonExistingList.add(chargeDOB);
        }
        else
        {
          chargeDOB.setChargeDesc("This Charge Id  doesn't Exist in Charge Master");
          existingList.add(chargeDOB);
        }*/
        if(rs!=null)rs.close();
        if(rsCharge!=null)rsCharge.close();
      }
      map.put("EXISTS",existingList);
      map.put("NONEXISTS",nonExistingList);
    }
    catch(Exception e)
    {
      e.printStackTrace();
      //Logger.info(FILE_NAME,"Error while adding"+e.toString());
      logger.info(FILE_NAME+"Error while adding"+e.toString());
      throw new EJBException();
    }
    finally
    {
      ConnectionUtil.closeConnection(connection,pStmt,rs);
      ConnectionUtil.closeConnection(null,pStmt1,rsCharge);
      ConnectionUtil.closeConnection(null,pStmtCharge);
      ConnectionUtil.closeConnection(null,pStmtUpdate);
      
    }
    return map;
  }
  
  public boolean deleteChargeDiscriptionDtls(String chargeId,String descId,String terminalId)
  { 
  
    BuychargesHDRDOB  chargeDOB  =  null;
    Connection        connection =  null;
    PreparedStatement pStmt      =  null;
    //String            sql        =  "DELETE QMS_CHARGEDESCMASTER  WHERE CHARGEID =? AND CHARGEDESCID =? AND TERMINALID=?";
    
    
        
    String            sql        =  "UPDATE QMS_CHARGEDESCMASTER SET INACTIVATE='Y'  WHERE CHARGEID =? AND CHARGEDESCID =? AND TERMINALID=? AND INACTIVATE='N' ";
    //OperationsImpl    operationsImpl = new OperationsImpl();
    try
    {
      connection     =   operationsImpl.getConnection();
      pStmt          =   connection.prepareStatement(sql);
      
        pStmt.clearParameters();
        pStmt.setString(1,chargeId);
        pStmt.setString(2,descId);
        pStmt.setString(3,terminalId);
        pStmt.executeUpdate();
     
    }
    catch(Exception e)
    {
      e.printStackTrace();
      //Logger.info(FILE_NAME,"Error while adding"+e.toString());
      logger.info(FILE_NAME+"Error while adding"+e.toString());
      throw new EJBException();
     
    }
    finally
    {
      ConnectionUtil.closeConnection(connection,pStmt);
    }return true;
  }
  
  //public BuychargesHDRDOB selectChargeDiscriptionDtls(String chargeId,String descId ,String terminalId)
  public BuychargesHDRDOB selectChargeDiscriptionDtls(BuySellChargesEnterIdDOB buySellChargesEnterIdDOB,ESupplyGlobalParameters loginbean)throws FoursoftException
  {
  
    BuychargesHDRDOB  chargeDOB  =  null;
    Connection        connection =  null;
    PreparedStatement pStmt      =  null;
    String            sql        =  "SELECT  SHIPMENTMODE ,REMARKS,EXT_CHARGE_NAME  FROM QMS_CHARGEDESCMASTER  WHERE CHARGEID =? AND CHARGEDESCID =? AND INVALIDATE='F' AND INACTIVATE='N' AND TERMINALID=?";
    //OperationsImpl    operationsImpl = new OperationsImpl();
    ResultSet         rs             =  null;
    //BuySellChargesEnterIdDOB buySellChargesEnterIdDOB  = new BuySellChargesEnterIdDOB();
    StringBuffer      sb_lowerTermilas = null;
    String            selectQry   = null;
    boolean           validFlag   = true;
    String            selectGroup = null;
    try
    {
      sb_lowerTermilas = new StringBuffer();
      
      if(validateLoadDetails(buySellChargesEnterIdDOB,loginbean.getTerminalId()))
      {
        connection     =   operationsImpl.getConnection();
        
        //@@Modified By Yuvraj on 20060504 for External Charge Name Enhancement
        
        if(!("View".equalsIgnoreCase(buySellChargesEnterIdDOB.getOperation()) || "Modify".equalsIgnoreCase(buySellChargesEnterIdDOB.getOperation())))//@@Yuvraj
        {
           if("HO_TERMINAL".equals(loginbean.getAccessType()))
          {
          
              sb_lowerTermilas.append(" SELECT TERMINALID  FROM FS_FR_TERMINALMASTER WHERE ACTV_FLAG='A' ");
              
          }else
          {
              sb_lowerTermilas.append(" select CHILD_TERMINAL_ID TERMINALID from fs_fr_terminal_regn ");
              sb_lowerTermilas.append(" connect by prior CHILD_TERMINAL_ID=PARENT_TERMINAL_ID start with PARENT_TERMINAL_ID='");     
              sb_lowerTermilas.append(buySellChargesEnterIdDOB.getTerminalId());
              sb_lowerTermilas.append("' UNION SELECT TERMINALID FROM FS_FR_TERMINALMASTER WHERE TERMINALID='");
              sb_lowerTermilas.append(buySellChargesEnterIdDOB.getTerminalId());
              sb_lowerTermilas.append("'");
              
          }         
          
          selectGroup = " SELECT CHARGEGROUP_ID  FROM QMS_CHARGE_GROUPSMASTER WHERE CHARGEDESCID = ? AND INACTIVATE= 'N' AND TERMINALID IN ( "+sb_lowerTermilas.toString()+" )";
          selectQry   = " SELECT BUYSELLCHARGEID   FROM QMS_BUYSELLCHARGESMASTER WHERE CHARGE_ID=? AND CHARGEDESCID=? AND TERMINALID IN ( "+sb_lowerTermilas.toString()+" )";
          pStmt          =   connection.prepareStatement(selectQry);
          
          pStmt.setString(1,buySellChargesEnterIdDOB.getChargeId());
          pStmt.setString(2,buySellChargesEnterIdDOB.getChargeDescId());
          
          rs = pStmt.executeQuery();
          
          if(rs.next())
          { validFlag = false;}
          
         if(rs!=null)
          { rs.close();}        
         if(pStmt!=null)
          { pStmt.close();}   
          
          if(validFlag)
          {
              pStmt          =   connection.prepareStatement(selectGroup);
              
              pStmt.setString(1,buySellChargesEnterIdDOB.getChargeDescId());         
              rs = pStmt.executeQuery();
              
              if(rs.next())
              { validFlag = false;}
              
             if(rs!=null)
              { rs.close();}        
             if(pStmt!=null)
              { pStmt.close();}          
          }
          
        }
        

        if(validFlag)
        {
        
            pStmt          =   connection.prepareStatement(sql);
            pStmt.clearParameters();
            /*pStmt.setString(1,chargeId);
            pStmt.setString(2,descId);
            pStmt.setString(3,terminalId);*/
            pStmt.setString(1,buySellChargesEnterIdDOB.getChargeId());
            pStmt.setString(2,buySellChargesEnterIdDOB.getChargeDescId());
            pStmt.setString(3,buySellChargesEnterIdDOB.getTerminalId());
            
            rs  =  pStmt.executeQuery();
            
            if(rs.next())
            {
              chargeDOB  =  new BuychargesHDRDOB();
              chargeDOB.setShipmentMode(rs.getInt("SHIPMENTMODE"));
              chargeDOB.setRemarks(rs.getString("REMARKS"));
              chargeDOB.setExternalChargeName(rs.getString("EXT_CHARGE_NAME"));
            }
         }
       else
        {
          throw new FoursoftException();
        }
      }    
      
    }
    catch(FoursoftException e)
    {
      throw new FoursoftException("Already in use");
    }
    catch(Exception e)
    {
      e.printStackTrace();
      //Logger.info(FILE_NAME,"Error while adding"+e.toString());
      logger.info(FILE_NAME+"Error while adding"+e.toString());
      throw new EJBException();
    }
    finally
    {
      ConnectionUtil.closeConnection(connection,pStmt,rs);
    }return chargeDOB;
  }
  
  public boolean modifyChargeDiscriptionDtls(BuychargesHDRDOB  chargeDOB)
  {  
    
    Connection        connection =  null;
    PreparedStatement pStmt      =  null;
    String            sql        =  "UPDATE QMS_CHARGEDESCMASTER SET REMARKS =?,SHIPMENTMODE=?,EXT_CHARGE_NAME=? WHERE CHARGEID =? AND CHARGEDESCID =? AND TERMINALID=?  AND INACTIVATE='N' ";
    //OperationsImpl    operationsImpl = new OperationsImpl();
    try
    {
      connection     =   operationsImpl.getConnection();
      pStmt          =   connection.prepareStatement(sql);
        pStmt.clearParameters();
        pStmt.setString(1,chargeDOB.getRemarks());
        pStmt.setInt(2,chargeDOB.getShipmentMode());
        pStmt.setString(3,chargeDOB.getExternalChargeName());
        pStmt.setString(4,chargeDOB.getChargeId());
        pStmt.setString(5,chargeDOB.getChargeDescId());
        pStmt.setString(6,chargeDOB.getTerminalId());
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

  
  public ArrayList getChargeIdsForChargeDesc(String searchString,String terminalId)
  {
    Connection        connection =  null;
    PreparedStatement pStmt      =  null;
    String            searchQry  =  "";
    /*if(searchString!=null)
      searchQry  = "AND CHARGEDESCID LIKE '"+searchString+"%'";
    String            sql        =  "SELECT  CHARGEDESCID   FROM QMS_CHARGEDESCMASTER  WHERE CHARGEID =? "+searchQry+" AND TERMINALID=? AND INVALIDATE='F' AND INACTIVATE='N'";
    //  String            sql        =  "SELECT  CHARGEDESCID   FROM QMS_CHARGEDESCMASTER  WHERE CHARGEID =? "+searchQry+" AND TERMINALID  IN ( ";
    */
    String            sql = "SELECT DISTINCT  CM.CHARGE_ID ||'('|| COST_INCURREDAT || ')'||'--'|| CM.CHARGE_DESCRIPTION FROM QMS_CHARGESMASTER CM,QMS_CHARGEDESCMASTER CDM WHERE CM.CHARGE_ID LIKE '"+searchString+"%' AND CM.CHARGE_ID=CDM.CHARGEID AND CDM.INVALIDATE ='F'  AND CDM.INACTIVATE='N' AND CDM.TERMINALID='"+terminalId+"'";
    OperationsImpl    operationsImpl = new OperationsImpl();
    ResultSet         rs             =  null;
    ArrayList         descriptionIds =  new ArrayList();
    try
    {
      connection     =   operationsImpl.getConnection();
      pStmt          =   connection.prepareStatement(sql);
      
        
        pStmt.clearParameters();

        rs = pStmt.executeQuery();
        while(rs.next())
        {
          descriptionIds.add(rs.getString(1));
        }
     
    }
    catch(Exception e)
    {
      e.printStackTrace();
      //Logger.error(FILE_NAME,"Error in getDescriptionIds"+e.toString());
      logger.error(FILE_NAME+"Error in getDescriptionIds"+e.toString());
      throw new EJBException();
    }
    finally
    {
      ConnectionUtil.closeConnection(connection,pStmt,rs);
    }
    return descriptionIds;
  }
  
  public ArrayList getDesriptionIds(BuySellChargesEnterIdDOB buySellChargesEnterIdDOB)
  {
    BuychargesHDRDOB  chargeDOB   =  null;
    Connection        connection  =  null;
    PreparedStatement pStmt       =  null;
    String            searchString=  buySellChargesEnterIdDOB.getChargeDescId();
    String            chargeId    =  buySellChargesEnterIdDOB.getChargeId();
    String            terminalId  =  buySellChargesEnterIdDOB.getTerminalId();
    String            operation   =  buySellChargesEnterIdDOB.getOperation();
    String            chkQry      =  "";
    String             sqlQuery=  "";
    String             sql=  "";
    if(searchString == null || (searchString!=null && searchString.trim().length()==0))
        searchString  = "";
    
    if("Delete".equalsIgnoreCase(operation))
        chkQry  = " AND (CHARGEID,CHARGEDESCID)NOT IN (SELECT CHARGE_ID,CHARGEDESCID FROM QMS_BUYSELLCHARGESMASTER) ";
        //added by VLAKSHMI for CR #170761 on 20090626
    if(searchString.trim().length()==0)
    {
                  sqlQuery        =  "SELECT  CHARGEDESCID   FROM QMS_CHARGEDESCMASTER  WHERE CHARGEID =? AND TERMINALID=? AND INVALIDATE='F' AND INACTIVATE='N' "+
                                    chkQry+"ORDER BY CHARGEDESCID"; 
    }

    /*if(searchString!=null)
    {*/
    else{
    searchString  = searchString+"%";
    //}
                sql        =  "SELECT  CHARGEDESCID   FROM QMS_CHARGEDESCMASTER  WHERE CHARGEID =? AND TERMINALID=? AND CHARGEDESCID LIKE ? AND INVALIDATE='F' AND INACTIVATE='N' "+
                                    chkQry+"ORDER BY CHARGEDESCID";
    }
    //  String            sql        =  "SELECT  CHARGEDESCID   FROM QMS_CHARGEDESCMASTER  WHERE CHARGEID =? "+searchQry+" AND TERMINALID  IN ( ";
   // OperationsImpl    operationsImpl = new OperationsImpl();
    ResultSet         rs             =  null;
    ArrayList         descriptionIds =  new ArrayList();
    try
    {
      connection     =   operationsImpl.getConnection();
       //added by VLAKSHMI for CR #170761 on 20090626
      if(searchString.trim().length()==0){
      pStmt          =   connection.prepareStatement(sqlQuery);
      
        
        pStmt.clearParameters();
        pStmt.setString(1,chargeId);        
        pStmt.setString(2,terminalId);
        rs = pStmt.executeQuery();
        while(rs.next())
        {
          descriptionIds.add(rs.getString("CHARGEDESCID"));
        }
      }
      else
      {
       pStmt          =   connection.prepareStatement(sql);
      
        
        pStmt.clearParameters();
        pStmt.setString(1,chargeId);        
        pStmt.setString(2,terminalId);
        pStmt.setString(3,searchString);
        rs = pStmt.executeQuery();
        while(rs.next())
        {
          descriptionIds.add(rs.getString("CHARGEDESCID"));
        } 
      }
     
    }
    catch(Exception e)
    {
      e.printStackTrace();
      //Logger.error(FILE_NAME,"Error in getDescriptionIds"+e.toString());
      logger.error(FILE_NAME+"Error in getDescriptionIds"+e.toString());
      throw new EJBException();
      
    }
    finally
    {
      ConnectionUtil.closeConnection(connection,pStmt,rs);
    }
    return descriptionIds;
  }
   public ArrayList getChargeDescDetails(String operation,ESupplyGlobalParameters loginbean)
    {                 
         ArrayList                    chargeList         =                  null;
         Connection                   connection      =                  null;
         PreparedStatement            pStmt           =                  null;
         ResultSet                    resultSet       =                  null;
         String                       selectQuery     =                  null; 
         BuychargesHDRDOB             chargeDOB        =                  null;
         StringBuffer terminalList        = new StringBuffer("");;
         String terminalId = "";
      try
      {  
        terminalId = loginbean.getTerminalId(); 
        
        if(loginbean.getAccessType().equals("HO_TERMINAL"))
        {
            terminalList.append(" SELECT DISTINCT terminalid FROM FS_FR_TERMINALMASTER WHERE actv_flag = 'A' ");
        
        }else
        {
           if("ViewAll".equals(operation))
           {  
              terminalList.append(" select CHILD_TERMINAL_ID TERMINALID from fs_fr_terminal_regn");
              terminalList.append(" connect by prior CHILD_TERMINAL_ID=PARENT_TERMINAL_ID start with PARENT_TERMINAL_ID='"+terminalId+"' ");
              terminalList.append(" UNION ");
              terminalList.append(" SELECT TERMINALID FROM FS_FR_TERMINALMASTER WHERE TERMINALID = '"+terminalId+"'  ");
              terminalList.append(" UNION");
              terminalList.append(" SELECT PARENT_TERMINAL_ID TERMINALID from fs_fr_terminal_regn ");
              terminalList.append(" connect by prior PARENT_TERMINAL_ID=CHILD_TERMINAL_ID start with CHILD_TERMINAL_ID= '"+terminalId+"'");
              terminalList.append(" UNION");
              terminalList.append(" SELECT TERMINALID FROM FS_FR_TERMINALMASTER WHERE OPER_ADMIN_FLAG='H'");
           }else
           {
              terminalList.append(" SELECT TERMINALID FROM FS_FR_TERMINALMASTER WHERE TERMINALID = '"+terminalId+"'  ");
              terminalList.append(" UNION ");
              terminalList.append(" select CHILD_TERMINAL_ID TERMINALID from fs_fr_terminal_regn");
              terminalList.append(" connect by prior CHILD_TERMINAL_ID=PARENT_TERMINAL_ID start with PARENT_TERMINAL_ID='"+terminalId+"' ");
           }
        }
         chargeList         =    new ArrayList(5);
         operationsImpl  =    new OperationsImpl();
         connection      =    operationsImpl.getConnection();
         selectQuery     =    "SELECT CHARGEID,CHARGEDESCID,REMARKS,SHIPMENTMODE,INVALIDATE,TERMINALID,EXT_CHARGE_NAME FROM QMS_CHARGEDESCMASTER WHERE   INACTIVATE='N' ";         
         if(terminalList.length()>0)
         {
            selectQuery = selectQuery+"  AND TERMINALID IN ( "+terminalList+")";
         }
         
         pStmt           =    connection.prepareStatement(selectQuery);

         
         resultSet       =    pStmt.executeQuery(); 
         
        
          while(resultSet.next())
          { 
           chargeDOB           =      new BuychargesHDRDOB();
           
           chargeDOB.setChargeId(resultSet.getString(1)!=null?resultSet.getString(1):"");
           chargeDOB.setChargeDescId(resultSet.getString(2)!=null?resultSet.getString(2):"");
           chargeDOB.setRemarks(resultSet.getString(3)!=null?resultSet.getString(3):"");
           chargeDOB.setShipmentMode(resultSet.getInt(4));
           chargeDOB.setInvalidate(resultSet.getString(5)!=null?resultSet.getString(5):"");
           chargeDOB.setTerminalId(resultSet.getString(6)!=null?resultSet.getString(6):"");
           chargeDOB.setExternalChargeName(resultSet.getString("EXT_CHARGE_NAME")!=null?resultSet.getString("EXT_CHARGE_NAME"):"");
           chargeList.add(chargeDOB);
         }
      }
      catch(Exception e)
      {
         e.printStackTrace();
         //Logger.error(FILE_NAME,"Error in getDensityGroupCodeDetails module",e.toString());         
         logger.error(FILE_NAME+"Error in getDensityGroupCodeDetails module"+e.toString());         
         throw new EJBException();
      }    
      finally
      {
        chargeDOB    =    null;
        ConnectionUtil.closeConnection(connection,pStmt,resultSet);
      }
      return chargeList;
    }
    
    public void invalidateChargeDescId(ArrayList chargeList)
    {
      Connection connection  = null;
     // OperationsImpl operationsImpl = new OperationsImpl();
      BuychargesHDRDOB  chargeDOB  = null;
      PreparedStatement pstmt = null;
      String updateQry  = "UPDATE QMS_CHARGEDESCMASTER  SET INVALIDATE =? WHERE CHARGEDESCID  =? AND TERMINALID = ? AND INACTIVATE='N' ";
      try
      {
         connection      =    operationsImpl.getConnection();
        pstmt = connection.prepareStatement(updateQry);
        if(chargeList!=null && chargeList.size()>0)
        {
        	int chargeListSize	=	chargeList.size();
          for(int i=0;i<chargeListSize;i++)
          {
            chargeDOB  = (BuychargesHDRDOB)chargeList.get(i);
            pstmt.setString(1,chargeDOB.getInvalidate());
            pstmt.setString(2,chargeDOB.getChargeDescId());
            pstmt.setString(3,chargeDOB.getTerminalId());
            pstmt.executeUpdate();
          }
         }
          
       }catch(SQLException e)
       {
        //Logger.error(FILE_NAME,"SQLException in invalidateIndustryId(ArrayList param0) method"+e.toString());
        logger.error(FILE_NAME+"SQLException in invalidateIndustryId(ArrayList param0) method"+e.toString());
        //throw new SQLException();                  
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
         throw new EJBException();            
        }
       }
    }
    //@@ Comment & added by subrahmanyam for the pbn id: 195270 on 20-Jan-10
  // public ArrayList getDesriptionIds(String chargeId,String searchString,String shipmentMode,String terminalId)
    public ArrayList getDesriptionIds(String chargeId,String searchString,String shipmentMode,String terminalId,String chargeGroupId)
  {
    
    Connection        connection =  null;
    CallableStatement csmt       =  null;
    /*PreparedStatement pStmt      =  null;
    String            searchQry  =  "";
    if(searchString!=null)
      searchQry  = "AND CHARGEDESCID LIKE '"+searchString+"%'";
    String            sql        =  "SELECT  CHARGEDESCID   FROM QMS_CHARGEDESCMASTER  WHERE CHARGEID =? "+searchQry+" AND TERMINALID=? AND SHIPMENTMODE IN("+shipmentMode+")";*/
    //OperationsImpl    operationsImpl = new OperationsImpl();
    ResultSet         rs             =  null;
    ArrayList         descriptionIds =  new ArrayList();
    try
    {
      connection     =   operationsImpl.getConnection();
      //@@ Comment & added by subrahmanyam for the pbn id: 195270 on 20-Jan-10      
      //csmt           =   connection.prepareCall("{ ?= call PKG_QMS_CHARGES.GETCHARGEDESCIDSALLLEVELS(?,?,?,?) }");
      csmt           =   connection.prepareCall("{ ?= call PKG_QMS_CHARGES.GETCHARGEDESCIDSALLLEVELS(?,?,?,?,?) }");
      csmt.registerOutParameter(1,OracleTypes.CURSOR);
      csmt.setString(2,searchString);
      csmt.setString(3,chargeId);
      csmt.setString(4,shipmentMode);
      csmt.setString(5,terminalId);
      csmt.setString(6,(chargeGroupId!=null)?chargeGroupId:"");//@@ added by subrahmanyam for the pbn id: 195270 on 20-Jan-10
      csmt.execute();
      rs  = (ResultSet)csmt.getObject(1);
      
/*      pStmt          =   connection.prepareStatement(sql);
      
        
        pStmt.clearParameters();
        pStmt.setString(1,chargeId);        
        pStmt.setString(2,terminalId);
        rs = pStmt.executeQuery();*/
        while(rs.next())
        {
          descriptionIds.add(rs.getString(1));
        }
     
    }
    catch(Exception e)
    {
      e.printStackTrace();
      //Logger.error(FILE_NAME,"Error in getDescriptionIds"+e.toString());
      logger.error(FILE_NAME+"Error in getDescriptionIds"+e.toString());
      return null;
    }
    finally
    {
      ConnectionUtil.closeConnection(connection,csmt);
      ConnectionUtil.closeStatement(null,rs);//Added By RajKumari on 24-10-2008 for Connection Leakages.
    }
    return descriptionIds;
  }    
  
  public ArrayList getUpdatedBuyCharges(QMSCartageMasterDOB masterDOB)//throws SQLException
  {
    
    ArrayList         list           = null; 
    SellChargesDAO    dao            =  new SellChargesDAO();
    try
    {    
      
      list       = dao.getUpdatedBuyCharges(masterDOB);
      
    }
    /*catch(SQLException e)
    {
      e.printStackTrace();
      throw new SQLException("Error while getting the details");
    }*/
    catch(Exception e)
    {
      e.printStackTrace();
      throw new EJBException("Error while getting the details");
    }
    return list;
  }
  //For Flat
  public QMSCartageSellDtlDOB getSellChargesDtls(String cartageId,String zoneCode,String chargeType)//throws SQLException
  {
    
    QMSCartageSellDtlDOB         dob           = null; 
    SellChargesDAO    dao            =  new SellChargesDAO();
    try
    {    
      
      dob       = dao.getSellChargesDtls(cartageId,zoneCode,chargeType);      
      
    }
    /*catch(SQLException e)
    {
      e.printStackTrace();
      throw new SQLException("Error while getting the details");
    }*/
    catch(Exception e)
    {
      e.printStackTrace();
      throw new EJBException("Error while getting the details");
    }
    return dob;
  }
  
   public QMSCartageSellDtlDOB getSellChargesDtlsSlab(String cartageId,String zoneCode,String chargeType,String chargeBasis,String unitofMeasure)//throws SQLException
  {

    
    QMSCartageSellDtlDOB         dob           = null; 
    SellChargesDAO    dao            =  new SellChargesDAO();
    try
    {    
      
      dob       = dao.getSellChargesDtls(cartageId,zoneCode,chargeType,chargeBasis,unitofMeasure);      
      
    }
    /*catch(SQLException e)
    {
      e.printStackTrace();
      throw new SQLException("Error while getting the details");
    }*/
    catch(Exception e)
    {
      e.printStackTrace();
      throw new EJBException("Error while getting the details");
    }
    return dob;
  }
  public QMSCartageSellDtlDOB getListUpdatedCharges(String cartageId,String zoneCode,String chargeType)
  {
    QMSCartageSellDtlDOB         dob           = null; 
    SellChargesDAO    dao            =  new SellChargesDAO();
    try
    {    
      
      dob       = dao.getListUpdatedCharges(cartageId,zoneCode,chargeType);
      
    }
    catch(Exception e)
    {
      e.printStackTrace();
      throw new EJBException("Error while getting the details");
    }
    return dob;
  }
  
  //For Flat
   public void updateSellDtls(ArrayList list)
   {
     SellChargesDAO    dao            =  new SellChargesDAO();
      try
      {    
       dao.updateSellDtls(list);
       
     }
     catch(Exception e)
     {
       e.printStackTrace();
       //Logger.error(FILE_NAME,"Error while updating details"+e.toString());
       logger.error(FILE_NAME+"Error while updating details"+e.toString());
       throw new EJBException("Error while updating details");
     }
   }
  public String insertNewCartageSellDtl(ArrayList list)//throws SQLException
  {
    SellChargesDAO    dao            =  new SellChargesDAO();

    //Connection connection            = null;   //Commented By RajKumari on 24-10-2008 for Connection Leakages.  
   // ResultSet         rs             = null;   //Commented By RajKumari on 24-10-2008 for Connection Leakages.
   // OperationsImpl    operationsImpl = new OperationsImpl();
    CallableStatement cstmt          = null;

    try
    {    
     dao.insertNewCartageSellDtl(list);
    }
    /*catch(SQLException e)
    {
      e.printStackTrace();
      throw new SQLException("Error while getting the details");
    }*/
    catch(Exception e)
    {
      e.printStackTrace();
      throw new EJBException("Error while updating the SellCharges");
    }
    return "Successfully Updated";
   }
   /**
   * Public Method to Call either add or Modify methods based on the process selected by the user.
   * @throws javax.ejb.EJBException
   * @return HashMap
   * @param loginbean
   * @param process
   * @param successList
   */
   public HashMap uploadChargeDescriptionDetails(ArrayList successList,String process, ESupplyGlobalParameters loginbean) throws EJBException
   {
     HashMap  map  =    new HashMap();
     try
     {
       if("Add".equalsIgnoreCase(process))
          map = insertChargeDescDtls(successList,loginbean);
       if("Modify".equalsIgnoreCase(process))
          map = updateChargeDescriptionDtls(successList,loginbean);
     }
     catch (Exception e)
     {
       e.printStackTrace();
       //Logger.error(FILE_NAME,"Exception in uploadChargeDescriptionDetails "+e);
       logger.error(FILE_NAME+"Exception in uploadChargeDescriptionDetails "+e);
       throw new EJBException(e);
     }
     return map;
   }
   /**
   * This Method is used to Update Charge Description Ids in case of Charge Description- Upload, process- Modify.
   * Inserts the Data in a GTT using Batch Update and calls the procedure to get 2 resultsets , updated data & not updated data.
   * @throws javax.ejb.EJBException
   * @return HashMap
   * @param loginbean
   * @param successList
   */
   private HashMap updateChargeDescriptionDtls(ArrayList successList,ESupplyGlobalParameters loginbean) throws EJBException
   {
     Connection        conn           =   null;
     PreparedStatement pstmt          =   null;
     CallableStatement cstmt          =   null;
     ResultSet         rs             =   null;
     ResultSet         rs1            =   null;
     ArrayList         updatedList    =   new ArrayList();
     ArrayList         failedList     =   new ArrayList();
     HashMap           map            =   new HashMap();
     int               size;
     
     BuychargesHDRDOB  headerDOB=   null;
     
     //String            truncateQry  =   "TRUNCATE TABLE TEMP_CHARGES";//@@Global Temp Table
     String            insertQry    =   "INSERT INTO TEMP_CHARGES (CHARGE_ID,CHARGEDESCID,TERMINALID,INT_CHARGE_NAME,EXT_CHARGE_NAME)VALUES (?,?,?,?,?)";
     
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
            headerDOB = (BuychargesHDRDOB)successList.get(i);
            if(headerDOB!=null)
            {
              pstmt.clearParameters();
              pstmt.setString(1,headerDOB.getChargeId());
              pstmt.setString(2,headerDOB.getChargeDescId());
              pstmt.setString(3,headerDOB.getTerminalId());
              pstmt.setString(4,headerDOB.getRemarks());
              pstmt.setString(5,headerDOB.getExternalChargeName());
              pstmt.addBatch();
            }
          }
          pstmt.executeBatch();
          
          cstmt   =   conn.prepareCall("{CALL PKG_QMS_CHARGES.UPDATE_CHARGEDESC_DTLS(?,?,?)} ");
          cstmt.setString(1,loginbean.getTerminalId());
          cstmt.registerOutParameter(2,OracleTypes.CURSOR);
          cstmt.registerOutParameter(3,OracleTypes.CURSOR);
          cstmt.execute();
          
          rs    =   (ResultSet)cstmt.getObject(2);//@@Updated List
          rs1   =   (ResultSet)cstmt.getObject(3);//@@Failed List
          
          while (rs.next())
          {
            headerDOB   =   new BuychargesHDRDOB();
            headerDOB.setChargeId(rs.getString("CHARGE_ID"));
            headerDOB.setChargeDescId(rs.getString("CHARGEDESCID"));
            headerDOB.setTerminalId(rs.getString("TERMINALID"));
            headerDOB.setRemarks(rs.getString("INT_CHARGE_NAME"));
            headerDOB.setExternalChargeName(rs.getString("EXT_CHARGE_NAME"));
            updatedList.add(headerDOB);
          }
          
          while (rs1.next())
          {
            headerDOB   =   new BuychargesHDRDOB();
            headerDOB.setChargeId(rs1.getString("CHARGE_ID"));
            headerDOB.setChargeDescId(rs1.getString("CHARGEDESCID"));
            headerDOB.setTerminalId(rs1.getString("TERMINALID"));
            headerDOB.setRemarks(rs1.getString("INT_CHARGE_NAME"));
            headerDOB.setExternalChargeName(rs1.getString("EXT_CHARGE_NAME"));
            headerDOB.setChargeDesc(rs1.getString("NOTES"));
            failedList.add(headerDOB);
          }
          
          map.put("NONEXISTS",updatedList);
          map.put("EXISTS",failedList);
        }    
     }
     catch (SQLException sql)
     {
       sql.printStackTrace();
       //Logger.error(FILE_NAME,"SQL Exception in updateChargeDescriptionDtls "+sql);
       logger.error(FILE_NAME+"SQL Exception in updateChargeDescriptionDtls "+sql);
       //Logger.error(FILE_NAME,"SQL Error Code "+sql.getErrorCode());
       logger.error(FILE_NAME+"SQL Error Code "+sql.getErrorCode());
       throw new EJBException(sql);
     }
     catch(Exception e)
     {
       e.printStackTrace();
       //Logger.error(FILE_NAME,"Error in updateChargeDescriptionDtls "+e);
       logger.error(FILE_NAME+"Error in updateChargeDescriptionDtls "+e);
       throw new EJBException(e);
     }
     finally
     {
       try
       {
         if(pstmt != null)
            pstmt.close();
         if(rs != null)
            rs.close();
         if(rs1!=null)
            rs1.close();
         if(cstmt != null)
          cstmt.close();
         if(conn != null)
            conn.close();
       }
       catch (SQLException sql)
       {
         sql.printStackTrace();
         //Logger.error(FILE_NAME,"Error While Closing Used Resources "+sql);
         logger.error(FILE_NAME+"Error While Closing Used Resources "+sql);
       }
     }
     return map;
   }
   private HashMap insertChargeDescDtls(ArrayList successList,ESupplyGlobalParameters loginbean) throws EJBException 
   {
     Connection        conn           =   null;
     PreparedStatement pstmt          =   null;
     CallableStatement cstmt          =   null;
     ResultSet         rs             =   null;
     ResultSet         rs1            =   null;
     ArrayList         insertedList   =   new ArrayList();
     ArrayList         failedList     =   new ArrayList();
     HashMap           map            =   new HashMap();
     String            terminalId     =   loginbean.getTerminalId();
     int               size;
     
     BuychargesHDRDOB  headerDOB=   null;
     
     //String            truncateQry  =   "TRUNCATE TABLE TEMP_CHARGES";//@@Global Temp Table
     String            insertQry    =   "INSERT INTO TEMP_CHARGES (CHARGE_ID,CHARGEDESCID,SHIPMENTMODE,INT_CHARGE_NAME,EXT_CHARGE_NAME,SHMODE)VALUES (?,?,?,?,?,?)";
     String            sql          =   "INSERT INTO QMS_CHARGEDESCMASTER (CHARGEID,CHARGEDESCID,REMARKS,TERMINALID,SHIPMENTMODE,INVALIDATE,INACTIVATE,ID,EXT_CHARGE_NAME )VALUES(?,?,?,?,?,'F','N',CHARGE_DESCMASTER_SEQ.NEXTVAL,?)";
     
     try
     {
          if(successList!=null && successList.size()>0)
          {
            conn    =   getConnection();
            size    =   successList.size();
            /*pstmt   =   conn.prepareStatement(truncateQry);
            int  trunc  = pstmt.executeUpdate();
            
            Logger.info(FILE_NAME,"trunctrunc::"+trunc);
          
            if(pstmt != null)
              pstmt.close();*/
            
            cstmt   =   conn.prepareCall("{CALL TRUNCATE_PROC()} "); 
            
            cstmt.execute();
            
            if(cstmt!=null)
              cstmt.close();
            
            pstmt  =  conn.prepareStatement(insertQry);
            for(int i=0;i<size;i++)
            {
              headerDOB = (BuychargesHDRDOB)successList.get(i);
              if(headerDOB!=null)
              {
                pstmt.clearParameters();
                pstmt.setString(1,headerDOB.getChargeId());
                pstmt.setString(2,headerDOB.getChargeDescId());
                pstmt.setInt(3,headerDOB.getShipmentMode());
                pstmt.setString(4,headerDOB.getRemarks());
                pstmt.setString(5,headerDOB.getExternalChargeName());
                pstmt.setString(6,headerDOB.getShipModeString());
                pstmt.addBatch();
              }
            }
            pstmt.executeBatch();
            
            if(pstmt!=null)
                pstmt.close();
            
            cstmt   =   conn.prepareCall("{CALL PKG_QMS_CHARGES.VALIDATE_CHARGE_DESC(?,?,?)} ");
            cstmt.setString(1,loginbean.getTerminalId());
            cstmt.registerOutParameter(2,OracleTypes.CURSOR);
            cstmt.registerOutParameter(3,OracleTypes.CURSOR);
            cstmt.execute();
            
            rs    =   (ResultSet)cstmt.getObject(2);//@@Updated List
            rs1   =   (ResultSet)cstmt.getObject(3);//@@Failed List
            
            while (rs.next())
            {
              headerDOB   =   new BuychargesHDRDOB();
              headerDOB.setChargeId(rs.getString("CHARGE_ID"));
              headerDOB.setChargeDescId(rs.getString("CHARGEDESCID"));
              headerDOB.setShipmentMode(rs.getInt("SHIPMENTMODE"));
              headerDOB.setRemarks(rs.getString("INT_CHARGE_NAME"));
              headerDOB.setExternalChargeName(rs.getString("EXT_CHARGE_NAME"));
              headerDOB.setShipModeString(rs.getString("SHMODE"));
              insertedList.add(headerDOB);
            }
            
            while (rs1.next())
            {
              headerDOB   =   new BuychargesHDRDOB();
              headerDOB.setChargeId(rs1.getString("CHARGE_ID"));
              headerDOB.setChargeDescId(rs1.getString("CHARGEDESCID"));
              headerDOB.setShipmentMode(rs1.getInt("SHIPMENTMODE"));
              headerDOB.setRemarks(rs1.getString("INT_CHARGE_NAME"));
              headerDOB.setExternalChargeName(rs1.getString("EXT_CHARGE_NAME"));
              headerDOB.setChargeDesc(rs1.getString("NOTES"));
              headerDOB.setShipModeString(rs1.getString("SHMODE"));
              failedList.add(headerDOB);
            }
            size      =   insertedList.size();
            
            pstmt     =   conn.prepareStatement(sql);
            
            for(int i=0;i<size;i++)
            {
              headerDOB   =   (BuychargesHDRDOB)insertedList.get(i);
              pstmt.clearParameters();
              pstmt.setString(1,headerDOB.getChargeId());
              pstmt.setString(2,headerDOB.getChargeDescId());
              pstmt.setString(3,headerDOB.getRemarks());
              pstmt.setString(4,terminalId);
              pstmt.setInt(5,headerDOB.getShipmentMode()); 
              pstmt.setString(6,headerDOB.getExternalChargeName());
              pstmt.addBatch();
            }
            pstmt.executeBatch();
            
            map.put("NONEXISTS",insertedList);
            map.put("EXISTS",failedList);
        }
     }
     catch (SQLException e)
     {
       e.printStackTrace();
       //Logger.error(FILE_NAME,"SQLException in insertChargeDescDtls "+e);
       logger.error(FILE_NAME+"SQLException in insertChargeDescDtls "+e);
       throw new EJBException(e);
     }
     catch(Exception e)
     {
       e.printStackTrace();
       //Logger.error(FILE_NAME,"Error in insertChargeDescDtls "+e);
       logger.error(FILE_NAME+"Error in insertChargeDescDtls "+e);
       throw new EJBException(e);
     }
     finally
     {
       try
       {
         if(pstmt != null)
            pstmt.close();
         if(rs != null)
            rs.close();
         if(rs1!=null)
            rs1.close();
         if(cstmt != null)
          cstmt.close();
         if(conn != null)
            conn.close();
       }
       catch (SQLException e)
       {
         e.printStackTrace();
         //Logger.error(FILE_NAME,"Error While Closing Used Resources "+sql);
         logger.error(FILE_NAME+"Error While Closing Used Resources "+sql);
       }
     }
     return map;
   }
   //@@Added by Kameswari for the WPBBN issue-54554
   public ArrayList getDensityGroupCodeList()throws EJBException
   {
      Connection          conn              =   null;
      PreparedStatement    pstmt             =   null;
      ResultSet           rs                =   null; 
      String              viewdensityGroupListQuery = "SELECT KG_PER_M3,LB_PER_F3 FROM QMS_DENSITY_GROUP_CODE";
      ArrayList           densityGroupList = new ArrayList();    
      try
      {
         conn        =  getConnection();
         pstmt       =  conn.prepareStatement(viewdensityGroupListQuery);
         rs          =  pstmt.executeQuery();
         while(rs.next())
         {
          densityGroupList.add(rs.getString("KG_PER_M3"));
          densityGroupList.add(rs.getString("LB_PER_F3"));
         }
       }
      catch (Exception e)
       {
         e.printStackTrace();
         //Logger.error(FILE_NAME,"Error While Closing Used Resources "+sql);
         logger.error(FILE_NAME+"Error Occured While retrieving densityGroupCodeList "+e);
       }
        finally
     {
       try
       {
         if(rs != null)
            rs.close();
         if(pstmt != null)
           pstmt.close();
         if(conn != null)
           conn.close();
       }
        catch (SQLException e)
       {
         e.printStackTrace();
         //Logger.error(FILE_NAME,"Error While Closing Used Resources "+sql);
         logger.error(FILE_NAME+"Error Occured While Closing Used Resources ");
       }
     }
    return densityGroupList;
   }
   
     //@@WPBBN issue-54554
     
      //@@ Added by subrahmanyam for the WPBN issue-145057 on 13/11/08
      //@@Modified by subrahmanyam for the  pbn id: 186783 on 22/oct/09
    //public ArrayList getDensityGroupCodesList(String shipmentMode)throws EJBException
   public ArrayList getDensityGroupCodesList(String shipmentMode ,String charageBasis)throws EJBException

   {
      Connection          conn              =   null;
      PreparedStatement    pstmt             =   null;
      ResultSet           rs                =   null; 
      String              viewdensityGroupListQuery = "SELECT KG_PER_M3,LB_PER_F3 FROM QMS_DENSITY_GROUP_CODE WHERE DGCCODE='"+shipmentMode+"'";
      ArrayList           densityGroupList = new ArrayList();    
//@@Added by subrahmanyam for the pbn id: 186783 on 22/oct/09
      PreparedStatement    pstmt1             =   null;
      ResultSet           rs1                =   null; 
      String              primaryBasisQry     = "SELECT PRIMARY_BASIS FROM Qms_Charge_Basismaster WHERE CHARGEBASIS='"+charageBasis+"'" ;
//@@ Ended by subrahmanyam for the pbn id: 186783       on 22/oct/09

      try
      {
         conn        =  getConnection();
         pstmt       =  conn.prepareStatement(viewdensityGroupListQuery);
         rs          =  pstmt.executeQuery();
         while(rs.next())
         {
          densityGroupList.add(rs.getString("KG_PER_M3"));
          densityGroupList.add(rs.getString("LB_PER_F3"));
         }
//@@ Added by subrahmanyam for the  pbn id: 186783 on 22/oct/09     
   
           pstmt1       =  conn.prepareStatement(primaryBasisQry);
           rs1          =  pstmt1.executeQuery();
          while(rs1.next())
         {
              densityGroupList.add(rs1.getString("PRIMARY_BASIS"));
         }
//@@ Ended by subrahmanyam for the  pbn id: 186783   on 22/oct/09

       }
      catch (Exception e)
       {
         e.printStackTrace();
         //Logger.error(FILE_NAME,"Error While Closing Used Resources "+sql);
         logger.error(FILE_NAME+"Error Occured While retrieving densityGroupCodeList "+e);
       }
        finally
     {
       try
       {
         if(rs != null)
            rs.close();
         if(pstmt != null)
           pstmt.close();
//@@ Added by subrahmanyam for the pbn id: 186783 on 22/oct/09
           if(rs1 != null)
            rs1.close();
         if(pstmt1 != null)
           pstmt1.close();
          
//Ended for pbn id: 186783 on 22/oct/09

         if(conn != null)
           conn.close();
       }
        catch (SQLException e)
       {
         e.printStackTrace();
         //Logger.error(FILE_NAME,"Error While Closing Used Resources "+sql);
         logger.error(FILE_NAME+"Error Occured While Closing Used Resources ");
       }
     }
    return densityGroupList;
   }
     //@@WPBBN issue-145057
  
  //@@Added by Kameswari for the WPBN issue-106698
 public ArrayList  getChargeBasisList()throws EJBException  
{
  
       Connection          conn              =   null;
      PreparedStatement    pstmt             =   null;
      ResultSet           rs                =   null; 
      String              viewdensityGroupListQuery = "SELECT DISTINCT CHARGEBASIS,PRIMARY_BASIS FROM QMS_CHARGE_BASISMASTER";
      ArrayList           getChargeBasisList = new ArrayList();    
      try
      {
         conn        =  getConnection();
         pstmt       =  conn.prepareStatement(viewdensityGroupListQuery);
         rs          =  pstmt.executeQuery();
         while(rs.next())
         {
          getChargeBasisList.add(rs.getString("CHARGEBASIS"));
          getChargeBasisList.add(rs.getString("PRIMARY_BASIS"));
         }
       }
      catch (Exception e)
       {
         e.printStackTrace();
         //Logger.error(FILE_NAME,"Error While Closing Used Resources "+sql);
         logger.error(FILE_NAME+"Error Occured While retrieving densityGroupCodeList "+e);
       }
        finally
     {
       try
       {
         if(rs != null)
            rs.close();
         if(pstmt != null)
           pstmt.close();
         if(conn != null)
           conn.close();
       }
        catch (SQLException e)
       {
         e.printStackTrace();
         //Logger.error(FILE_NAME,"Error While Closing Used Resources "+sql);
         logger.error(FILE_NAME+"Error Occured While Closing Used Resources ");
       }
     }
    return getChargeBasisList;
   }
//@@WPBBN issue-106698
   private Connection getConnection() throws SQLException
	{
		if(dataSource== null)
        getDataSource();
    return dataSource.getConnection();
	}
  //@@Added by Kameswari for the WPBN issue- on 31/03/09
   public ArrayList getAcceptanceDetails()   throws javax.ejb.EJBException
   {
      Connection           conn                        =   null;
      PreparedStatement    pstmt                       =   null;
      ResultSet            rs                          =   null; 
      String               viewSellChargeAccListQuery  =   "SELECT SELLCHARGEID,TERMINALID,CHARGE_ID,CHARGEDESCID FROM QMS_SELLCHARGESMASTER_ACC";
      ArrayList            getSellChargeIdList         =   new ArrayList();  
      ArrayList            getTerminalIdList           =   new ArrayList();  
      ArrayList            getChargeIdList             =   new ArrayList();
      ArrayList            getChargeDescList           =   new ArrayList();
      ArrayList            getAccList                  =   new ArrayList();
      try
      {
         conn        =  getConnection();
         pstmt       =  conn.prepareStatement(viewSellChargeAccListQuery);
         rs          =  pstmt.executeQuery();
         while(rs.next())
         {
             getSellChargeIdList.add(rs.getString("SELLCHARGEID"));
             getTerminalIdList.add(rs.getString("TERMINALID"));
             getChargeIdList.add(rs.getString("CHARGE_ID"));
             getChargeDescList.add(rs.getString("CHARGEDESCID"));
         }
         if(getTerminalIdList!=null&&getTerminalIdList.size()>0)
         {
              getAccList.add(getSellChargeIdList);
              getAccList.add(getTerminalIdList);
              getAccList.add(getChargeIdList);
              getAccList.add(getChargeDescList);
         }
       }
      catch (Exception e)
       {
         e.printStackTrace();
         //Logger.error(FILE_NAME,"Error While Closing Used Resources "+sql);
         logger.error(FILE_NAME+"getAcceptanceDetails()..Error Occured While retrieving Sellcharge AccList "+e);
       }
        finally
     {
       try
       {
         if(rs != null)
            rs.close();
         if(pstmt != null)
           pstmt.close();
         if(conn != null)
           conn.close();
       }
        catch (SQLException e)
       {
         e.printStackTrace();
         //Logger.error(FILE_NAME,"Error While Closing Used Resources "+sql);
         logger.error(FILE_NAME+"Error Occured While Closing Used Resources ");
       }
     }
    return getAccList;

   }
  public int insertSellChargeAccDtls(ArrayList chargesList) throws javax.ejb.EJBException
  {
      Connection           conn                        =   null;
      CallableStatement    cstmt                       =   null;
      PreparedStatement    pstmt                       =   null;
      PreparedStatement    pstmt1                       =   null;
      PreparedStatement    pstmt2                       =   null;
      ResultSet            rs                          =   null; 
     // String               viewSellChargeAccListQuery  =   "SELECT TERMINALID,CHARGE_ID,CHARGEDESCID,CHARGEBASIS,RATE_BREAK,RATE_TYPE,CURRENCY,WEIGHT_CLASS,OVERALL_MARGIN,MARGIN_TYPE,MARGIN_BASIS,CREATED_BY,ACCESSLEVEL FROM QMS_SELLCHARGESMASTER_ACC QSM WHERE QSM.CHARGE_ID=? AND QSM.CHARGEDESCID=? AND QSM.TERMINALID=?";
    //  String               viewSellChargeAccListQuery  =   "";
      String               selectqry                   =  "SELECT IE_FLAG FROM QMS_SELLCHARGESMASTER WHERE SELLCHARGEID = ?";
      String              deleteDtlrqry                =  "DELETE FROM QMS_SELLCHARGESDTL_ACC WHERE SELLCHARGEID = ?";
      String              deleteMstrqry                 =  "DELETE FROM QMS_SELLCHARGESMASTER_ACC WHERE SELLCHARGEID = ?";
      String[]            getTerminalIdList            =   null; 
      String[]            getChargeIdList              =   null; 
      String[]            getChargeDescList            =   null; 
      String[]            getSellChargeIdList          =   null; 
     // ArrayList            getAccList                  =   new ArrayList();
      String               active_flag                 =   null;  
      int                  count                      = 0;
      try
      {
         conn        =  getConnection();
         if(chargesList!=null)
         {
               getSellChargeIdList  = (String[])chargesList.get(0);
               getChargeIdList   = (String[])chargesList.get(1);
               getChargeDescList     = (String[])chargesList.get(2);
               getTerminalIdList   = (String[])chargesList.get(3);
            
               if(getTerminalIdList!=null)
               {
            	   int terminalListLen	= getTerminalIdList.length;
            	   pstmt = conn.prepareStatement(selectqry); // Added by Gowtham on 04Feb2011 for Loop Leakages
            	   pstmt1 = conn.prepareStatement(deleteDtlrqry); // Added by Gowtham on 04Feb2011 for Loop Leakages
            	   pstmt2 = conn.prepareStatement(deleteMstrqry); // Added by Gowtham on 04Feb2011 for Loop Leakages
                  for(int i =0;i<terminalListLen;i++)
                 {
                  // pstmt = conn.prepareStatement(selectqry); // Commented by Gowtham on 04Feb2011 for Loop Leakages
                   pstmt.setString(1,getSellChargeIdList[i]);
                   rs   = pstmt.executeQuery();
                   if(rs.next())
                   {
                      active_flag   =   rs.getString("IE_FLAG");
                      count++;
                   }
                   if(active_flag!=null&&"A".equalsIgnoreCase(active_flag))
                   {
                     cstmt  =conn.prepareCall("{ ?=call PKG_QMS_CHARGES.sellcharges_insert(?,?,?) }");
                 
                     cstmt.registerOutParameter(1,Types.VARCHAR);
                     cstmt.setString(2,getChargeIdList[i]);
                     cstmt.setString(3,getChargeDescList[i]);
                     cstmt.setString(4,getTerminalIdList[i]);
             
                     cstmt.execute();
                 
                     count  = cstmt.getInt(1);
                   }
                   else if(active_flag!=null&&"I".equalsIgnoreCase(active_flag))
                   {
                     // pstmt1 = conn.prepareStatement(deleteDtlrqry); Commented by Gowtham on 04Feb2011 for Loop Leakages
                     // pstmt2 = conn.prepareStatement(deleteMstrqry); Commented by Gowtham on 04Feb2011 for Loop Leakages
                      pstmt1.setString(1,getSellChargeIdList[i]);
                      pstmt1.execute();
                      pstmt2.setString(1,getSellChargeIdList[i]);
                      pstmt2.execute();
                   }
                   pstmt.clearParameters();// Added by Gowtham on 04Feb2011 for Loop Leakages
                   pstmt1.clearParameters();// Added by Gowtham on 04Feb2011 for Loop Leakages
                   pstmt2.clearParameters();// Added by Gowtham on 04Feb2011 for Loop Leakages
                 }
               }   
        }
      
       }
      catch (Exception e)
       {
         e.printStackTrace();
         //Logger.error(FILE_NAME,"Error While Closing Used Resources "+sql);
         logger.error(FILE_NAME+"getAcceptanceDetails()..Error Occured While retrieving Sellcharge AccList "+e);
       }
        finally
     {
       try
       {
         if(rs != null)
            rs.close();
         if(cstmt != null)
           cstmt.close();
         if(conn != null)
           conn.close();
         if(pstmt!= null)//@@ Added by Govind for Me,ory leakages on 15-02-2010
        	 pstmt.close();
         if(pstmt1!= null)//@@ Added by Govind for Me,ory leakages on 15-02-2010
        	 pstmt1.close();
         if(pstmt2!= null)//@@ Added by Govind for Me,ory leakages on 15-02-2010
        	 pstmt2.close();
       }
        catch (SQLException e)
       {
         e.printStackTrace();
         //Logger.error(FILE_NAME,"Error While Closing Used Resources "+sql);
         logger.error(FILE_NAME+"Error Occured While Closing Used Resources ");
       }
     }
    return count;

  
  }
}