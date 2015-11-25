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
 * File : BuyChargesDAO.java
 * Sub-Module : Buy charges master
 * Module : QMS
 * @author : I.V.Sekhar Merrinti
 * * @date 25-06-2005
 * Modified by      Date     Reason
 */
package com.qms.operations.charges.buycharges.dao;

import com.foursoft.esupply.common.exception.FoursoftException;
import com.foursoft.esupply.common.java.LookUpBean;
import com.foursoft.etrans.common.util.ejb.sls.OIDSession;
import com.foursoft.etrans.common.util.ejb.sls.OIDSessionHome;
import com.foursoft.etrans.common.util.java.OperationsImpl;
import com.qms.operations.charges.java.QMSCartageBuyDtlDOB;
import com.qms.operations.charges.java.QMSCartageMasterDOB;
import com.qms.operations.charges.java.QMSCartageSellDtlDOB;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import javax.ejb.CreateException;
import javax.sql.DataSource;
import java.sql.Connection;
import javax.ejb.EJBException;
import javax.naming.NamingException;
import javax.naming.InitialContext;
import javax.ejb.ObjectNotFoundException;
import javax.naming.NameNotFoundException;
import java.util.ArrayList;

import com.foursoft.esupply.common.util.ConnectionUtil;
//import com.foursoft.esupply.common.util.Logger;
import org.apache.log4j.Logger;
import com.qms.operations.charges.buycharges.ejb.bmp.BuyChargesEntityBeanPK;
import com.qms.operations.charges.java.BuychargesHDRDOB;
import com.qms.operations.charges.java.BuychargesDtlDOB;
import oracle.jdbc.OracleTypes;

public class BuyChargesDAO 
{
    static final String FILE_NAME="BuyChargesDAO.java";
    DataSource  dataSource=null;
    Connection  connection=null;
    private static Logger logger = null;

    private static final String insertHdrQry = "INSERT INTO QMS_BUYSELLCHARGESMASTER (BUYSELLCHARGEID,CHARGE_ID,CHARGEDESCID,CHARGEBASIS,RATE_BREAK,RATE_TYPE,CURRENCY,WEIGHT_CLASS,"
                                              +"CREATED_BY,CREATED_TSTMP,LAST_UPDATED_BY,LAST_UPDATED_TSTMP,DEL_FLAG,ACCESSLEVEL,TERMINALID,DENSITY_CODE)"
                                              +" VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
                                                  
   /* private static final String selectHdrQry = "SELECT BSM.BUYSELLCHARGEID,BSM.CHARGE_ID,BSM.CHARGEDESCID,BSM.CHARGEBASIS,BM.BASIS_DESCRIPTION,BSM.RATE_BREAK,BSM.RATE_TYPE,BSM.CURRENCY,BSM.WEIGHT_CLASS "
                                              +" FROM QMS_BUYSELLCHARGESMASTER BSM,QMS_CHARGE_BASISMASTER BM WHERE BSM.CHARGE_ID=? AND BSM.CHARGEBASIS=? AND BSM.TERMINALID=? AND "
                                              +" BSM.CHARGEBASIS=BM.CHARGEBASIS AND BSM.DEL_FLAG='N'";
    */
private static final String selectHdrQry = "SELECT BSM.BUYSELLCHARGEID,BSM.CHARGE_ID,BSM.CHARGEDESCID,BSM.CHARGEBASIS,BM.BASIS_DESCRIPTION,BSM.RATE_BREAK,BSM.RATE_TYPE,BSM.CURRENCY,BSM.WEIGHT_CLASS,DENSITY_CODE "
                                              +" FROM QMS_BUYSELLCHARGESMASTER BSM,QMS_CHARGE_BASISMASTER BM WHERE BSM.CHARGE_ID=? AND BSM.CHARGEDESCID=? AND BSM.TERMINALID=? AND "
                                              +" BSM.CHARGEBASIS=BM.CHARGEBASIS AND BSM.DEL_FLAG='N'";
    
    private static final String insertDtlQry = "INSERT INTO QMS_BUYCHARGESDTL (BUYSELLCHAEGEID,CHARGERATE,CHARGESLAB,"
                                              +"LOWERBOUND,UPPERBOUND,CHARGERATE_INDICATOR,LANE_NO,ID) VALUES (?,?,?,?,?,?,?,SEQ_BUYCHARGESDTL.NEXTVAL)";
    
    private static final String selectDtlQry = "SELECT BUYSELLCHAEGEID,CHARGERATE,CHARGESLAB,"
                                              +"LOWERBOUND,UPPERBOUND,CHARGERATE_INDICATOR FROM QMS_BUYCHARGESDTL WHERE BUYSELLCHAEGEID=? ORDER BY LANE_NO";                                              
    
   // private static final String updateQry    = "UPDATE QMS_BUYSELLCHARGESMASTER SET DEL_FLAG='Y' WHERE CHARGE_ID=? AND CHARGEBASIS=? AND TERMINALID=?";
    //private static final String deleteQry    = "UPDATE QMS_BUYSELLCHARGESMASTER SET DEL_FLAG='Y' WHERE CHARGE_ID=? AND CHARGEBASIS=? AND TERMINALID=?";

    private static final String updateQry    = "UPDATE QMS_BUYSELLCHARGESMASTER SET DEL_FLAG='Y' WHERE CHARGE_ID=? AND CHARGEDESCID=?  AND TERMINALID=? AND DEL_FLAG='N' ";
    private static final String deleteQry    = "UPDATE QMS_BUYSELLCHARGESMASTER SET DEL_FLAG='Y' WHERE CHARGE_ID=? AND CHARGEDESCID=?  AND TERMINALID=? AND DEL_FLAG='N' ";
    
    private static final String updateDtlQry       =  "UPDATE  QMS_CARTAGE_BUYDTL SET ACTIVEINACTIVE = 'I' WHERE CARTAGE_ID IN (SELECT dtl.CARTAGE_ID FROM QMS_CARTAGE_BUYDTL dtl,QMS_CARTAGE_BUYSELLCHARGES mas WHERE mas.CARTAGE_ID=dtl.cartage_id  AND ZONE_CODE=? AND CHARGE_TYPE = ? and LOCATION_ID=? and activeinactive='A') and ZONE_CODE=? AND CHARGE_TYPE =?";
   // private static final String updateSellDtlQry   =  "UPDATE  QMS_CARTAGE_SELLDTL SET ACTIVEINACTIVE = 'I' WHERE CARTAGE_ID IN (SELECT dtl.CARTAGE_ID FROM QMS_CARTAGE_SELLDTL dtl,QMS_CARTAGE_BUYSELLCHARGES mas WHERE mas.CARTAGE_ID=dtl.CARTAGE_ID  AND ZONE_CODE=? AND CHARGE_TYPE = ? and LOCATION_ID=? and activeinactive='A') and ZONE_CODE=? AND CHARGE_TYPE =?";
    
    private static final String selectCartageIdQry =  "SELECT dtl.CARTAGE_ID FROM QMS_CARTAGE_BUYDTL dtl,QMS_CARTAGE_BUYSELLCHARGES mas WHERE mas.CARTAGE_ID=dtl.cartage_id  AND ZONE_CODE=? AND CHARGE_TYPE =? and LOCATION_ID=? and activeinactive='A'";
    
    private static final String masterQuery =  "INSERT INTO QMS_CARTAGE_BUYSELLCHARGES (CARTAGE_ID,LOCATION_ID,WEIGHT_BREAK,RATE_TYPE,"+
                                               "CURRENCY,UOM,MAX_CHARGEPER_TRUCKLOAD,CREATED_BY,"+
                                               "CREATED_TSTMP,ACCESSLEVEL,TERMINALID,SHIPMENT_MODE,CONSOLE_TYPE) "+
                                               "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?)";
    private static final String buyDtlQuery =  "INSERT INTO QMS_CARTAGE_BUYDTL (CARTAGE_ID,ZONE_CODE,CHARGERATE,"+
                                               "CHARGESLAB,LOWERBOUND,UPPERBOUND,CHARGERATE_INDICATOR,CHARGE_TYPE,ACTIVEINACTIVE,ID,SELLCHARGE_FLAG,DENSITY_CODE,LINE_NO,CHARGE_BASIS,EFFECTIVE_FROM,VALID_UPTO) "+
                                               "VALUES (?,?,?,?,?,?,?,?,'A',SEQ_CARTAGE_BUYDTL.NEXTVAL,'N',?,?,?,?,?)";//MODIFIED BY RK FOR SELLCHARGE_FLAG&DENSITY_CODE
   private static final String checkDensityRatio = "SELECT * FROM QMS_DENSITY_GROUP_CODE WHERE KG_PER_M3=? AND LB_PER_F3=? AND DGCCODE=?";



    //private static final String updateQry    = "UPDATE QMS_BUYSELLCHARGESMASTER SET DEL_FLAG='Y' WHERE CHARGE_ID=? AND CHARGEDESCID=?  AND TERMINALID=? AND DEL_FLAG='N'";
   // private static final String deleteQry    = "UPDATE QMS_BUYSELLCHARGESMASTER SET DEL_FLAG='Y' WHERE CHARGE_ID=? AND CHARGEDESCID=?  AND TERMINALID=? AND DEL_FLAG='N'";

    private static final String selectQry    = "SELECT BUYSELLCHARGEID FROM QMS_BUYSELLCHARGESMASTER  WHERE CHARGE_ID=? AND CHARGEDESCID=?  AND TERMINALID=? AND DEL_FLAG='N' ";

  /**
   * 
   */
    public BuyChargesDAO()
    {
          logger  = Logger.getLogger(BuyChargesDAO.class);
          try
          {
            InitialContext ic =new InitialContext();
            dataSource        =(DataSource)ic.lookup("java:comp/env/jdbc/DB");
            
          }catch(NamingException nex)
          {
            throw new EJBException(nex.toString());
          }catch(Exception e)
          {
            throw new EJBException(e.toString());        
          }    
    }
  /**
   * 
   */
    protected void getConnection()
    {         
      try 
      {
           connection = dataSource.getConnection();
      } 
      catch (SQLException se) 
      {
        throw new EJBException(se.toString());
      }
      catch (Exception e) 
      {
              throw new EJBException(e.toString());
      }	   
  
    }

    public BuyChargesEntityBeanPK findByPrimariKey(BuyChargesEntityBeanPK pkObj)throws ObjectNotFoundException,EJBException,SQLException
    {
      PreparedStatement pstmt = null;
      ResultSet         rs    = null;
      boolean     buyChargesRow  = false;
      try
      {
        getConnection();
        pstmt = connection.prepareStatement(selectHdrQry);
        pstmt.setString(1,pkObj.chargeId);
        /*pstmt.setString(2,pkObj.chargeBasisId);*/
        pstmt.setString(2,pkObj.chargeDescId);
        pstmt.setString(3,pkObj.terminalId);
        rs    = pstmt.executeQuery();
        if(rs.next())
        {
          buyChargesRow  = true;
        }
      }catch(SQLException e)
      {
        //Logger.error(FILE_NAME,"SQLException in findByPrimariKey(BuyChargesEntityBeanPK param0) method"+e.toString());
        logger.error(FILE_NAME+"SQLException in findByPrimariKey(BuyChargesEntityBeanPK param0) method"+e.toString());
        throw new SQLException();         
      }catch(Exception e)
      {
         //Logger.error(FILE_NAME,"Exception in findByPrimariKey(BuyChargesEntityBeanPK param0) method"+e.toString());
         logger.error(FILE_NAME+"Exception in findByPrimariKey(BuyChargesEntityBeanPK param0) method"+e.toString());
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
          //Logger.error(FILE_NAME,"SQLException in findByPrimariKey(BuyChargesEntityBeanPK param0) method"+e.toString());
          logger.error(FILE_NAME+"SQLException in findByPrimariKey(BuyChargesEntityBeanPK param0) method"+e.toString());
          throw new SQLException();          
        }
      }
      
      if(buyChargesRow)
      {
        return pkObj;
      }
      else
      {
        throw new ObjectNotFoundException();
      }    
    }
    public void create(BuychargesHDRDOB buychargesHDRDOB)throws FoursoftException,EJBException
    {
      CallableStatement  cstmt = null;
      String returnValue      = "";
      ResultSet         rs   = null;
      ArrayList         oldBuychargeIds = null;
      try
      {
        
        getConnection();
        cstmt = connection.prepareCall("{ ?= call PKG_QMS_CHARGES.BUYCHARGESADD(?,?,?,?,?,?,?,?,?,?)}");
        
       /* System.out.println("buychargesHDRDOB.getChargeDescId():: "+buychargesHDRDOB.getChargeDescId()+" ::buychargesHDRDOB.getChargeDescId()");
        System.out.println("getTerminalId() "+buychargesHDRDOB.getTerminalId()+" getTerminalId()");
        System.out.println("getChargeBasisId() "+buychargesHDRDOB.getChargeBasisId()+" getChargeBasisId()");
        //System.out.println();*/
        cstmt.registerOutParameter(1, Types.VARCHAR);
        cstmt.setString(2,buychargesHDRDOB.getChargeId());
        cstmt.setString(3,buychargesHDRDOB.getChargeDescId());
        cstmt.setString(4,buychargesHDRDOB.getChargeBasisId());
        cstmt.setString(5,buychargesHDRDOB.getRateBreak());
        cstmt.setString(6,buychargesHDRDOB.getRateType());
        cstmt.setString(7,buychargesHDRDOB.getTerminalId());
        cstmt.setString(8,buychargesHDRDOB.getDensityGrpCode());
        cstmt.setString(9,buychargesHDRDOB.getCurrencyId());
        cstmt.setDouble(10,buychargesHDRDOB.getBuySellChargeId());
        cstmt.registerOutParameter(11, OracleTypes.CURSOR);
        
        cstmt.execute();
        
        returnValue = cstmt.getString(1);
        
        
        //Logger.info(FILE_NAME,"returnValue:"+returnValue);

        
        if(returnValue.equals("1") || returnValue.equals("2") || 
        returnValue.equals("3") || returnValue.equals("4") 
        || returnValue.equals("7") || returnValue.equals("8") )
        { throw new FoursoftException("Invalid data");}
        else if(returnValue.equals("100"))
        { throw new EJBException("Exception while inserting");}
        else if(returnValue.startsWith("H_"))
        { throw new FoursoftException("Data exist at higer levels"+returnValue);}
        else if(returnValue.startsWith("6"))
        { 
          rs          = (ResultSet)cstmt.getObject(11);
          
          oldBuychargeIds = new ArrayList();
          //System.out.println("rs::"+rs);
          while(rs.next())
          {
            oldBuychargeIds.add(rs.getString("BUYSELLCHARGEID"));
          }
         // System.out.println("insert kkkkkk:"+oldBuychargeIds);
          
          insertBuyChargeDetails(buychargesHDRDOB,oldBuychargeIds);
          
        }
        else
        { throw new FoursoftException("Exception while inserting");}
      }catch(FoursoftException e)
      {
         e.printStackTrace();
        //Logger.error(FILE_NAME,"SQLException in create(ArrayList param0) method"+e.toString());
        logger.error(FILE_NAME+"SQLException in create(ArrayList param0) method"+e.toString());
        throw new FoursoftException(e.getMessage());        
      }
      catch(SQLException e)
      {
        e.printStackTrace();
        //Logger.error(FILE_NAME,"SQLException in create(ArrayList param0) method"+e.toString());
        logger.error(FILE_NAME+"SQLException in create(ArrayList param0) method"+e.toString());
        throw new EJBException();        
      }catch(EJBException e)
      {
      e.printStackTrace();
        //Logger.error(FILE_NAME,"EJBException in create(ArrayList param0) method"+e.toString());
        logger.error(FILE_NAME+"EJBException in create(ArrayList param0) method"+e.toString());
        throw new EJBException();         
      }catch(Exception e)
      {
      e.printStackTrace();
        //Logger.error(FILE_NAME,"Exception in create(ArrayList param0) method"+e.toString());
        logger.error(FILE_NAME+"Exception in create(ArrayList param0) method"+e.toString());
        throw new EJBException();         
      }finally
      {
        try
        {
        //Added By RajKumari on 24-10-2008 for Connection Leakages.
          if(rs!=null)
          {
            rs.close();
          }
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
    }

   public BuychargesHDRDOB load(BuyChargesEntityBeanPK pkObj)throws EJBException
    {
      BuychargesHDRDOB  buychargesHDRDOB  = null;
      try
      {
        buychargesHDRDOB  = loadBuyChargesMasterDOB(pkObj);
      }catch(SQLException e)
      {
        //Logger.error(FILE_NAME,"SQLException in load(BuyChargesEntityBeanPK param0) method"+e.toString());\
        logger.error(FILE_NAME+"SQLException in load(BuyChargesEntityBeanPK param0) method"+e.toString());
        throw new EJBException();        
      }catch(EJBException e)
      {
        //Logger.error(FILE_NAME,"EJBException in load(BuyChargesEntityBeanPK param0) method"+e.toString());
        logger.error(FILE_NAME+"EJBException in load(BuyChargesEntityBeanPK param0) method"+e.toString());
        throw new EJBException();         
      }catch(Exception e)
      {
        //Logger.error(FILE_NAME,"SQLException in load(BuyChargesEntityBeanPK param0) method"+e.toString());
        logger.error(FILE_NAME+"SQLException in load(BuyChargesEntityBeanPK param0) method"+e.toString());
        throw new EJBException();         
      }
      return buychargesHDRDOB;        
    }    
    
   public void store(BuyChargesEntityBeanPK pkObj,BuychargesHDRDOB buychargesHDRDOB)throws SQLException,EJBException
    {
      try
      {
         modifyBuyChargeDetails(pkObj,buychargesHDRDOB);
      }catch(SQLException e)
      {
        //Logger.error(FILE_NAME,"SQLException in store(BuyChargesEntityBeanPK param0) method"+e.toString());
        logger.error(FILE_NAME+"SQLException in store(BuyChargesEntityBeanPK param0) method"+e.toString());
        throw new EJBException();         
      }catch(EJBException e)
      {
        //Logger.error(FILE_NAME,"EJBException in store(BuyChargesEntityBeanPK param0) method"+e.toString());
        logger.error(FILE_NAME+"EJBException in store(BuyChargesEntityBeanPK param0) method"+e.toString());
        throw new EJBException();         
      }catch(Exception e)
      {
         //Logger.error(FILE_NAME,"SQLException in store(BuyChargesEntityBeanPK param0) method"+e.toString());
         logger.error(FILE_NAME+"SQLException in store(BuyChargesEntityBeanPK param0) method"+e.toString());
        throw new EJBException();        
      }      
    }

   /* public void remove(BuyChargesEntityBeanPK pkObj)throws SQLException,EJBException
    {
      try
      {
//        deleteBuyChargeMasterDetails(pkObj);
      }catch(SQLException e)
      {
        Logger.error(FILE_NAME,"SQLException in remove(BuyChargesEntityBeanPK param0) method"+e.toString());
        throw new EJBException();           
      }catch(EJBException e)
      {
        Logger.error(FILE_NAME,"EJBException in remove(BuyChargesEntityBeanPK param0) method"+e.toString());
        throw new EJBException();         
      }catch(Exception e)
      {
        Logger.error(FILE_NAME,"SQLException in remove(BuyChargesEntityBeanPK param0) method"+e.toString());
        throw new EJBException();            
      }
    }
*/
    private BuychargesHDRDOB loadBuyChargesMasterDOB(BuyChargesEntityBeanPK pkObj)throws EJBException,SQLException
    {
      PreparedStatement pstmt1               = null;
      PreparedStatement pstmt2               = null;
      ResultSet         rs1                  = null;
      ResultSet         rs2                  = null;
      BuychargesHDRDOB  buychargesHDRDOB     = null;
      BuychargesDtlDOB  buychargesDtlDOB     = null;
      ArrayList         dtlList              = null;
      String            chargeId             = null;
      String            charegBasisId        = null;
      String            chargeDescId         = null;
      String            terminalId           = null;
      double            buySellChargeId      = 0.0;
      try
      {
        getConnection();
       pstmt1  = connection.prepareStatement(selectHdrQry);
        pstmt2  = connection.prepareStatement(selectDtlQry);        
        if(pkObj!=null)
        {
          chargeId        = pkObj.chargeId;
          /*charegBasisId   = pkObj.chargeBasisId;*/
          chargeDescId    = pkObj.chargeDescId;
          terminalId      = pkObj.terminalId;
          pstmt1.setString(1,chargeId);
          /*pstmt1.setString(2,charegBasisId);*/
          pstmt1.setString(2,chargeDescId);
          pstmt1.setString(3,terminalId);
          rs1 = pstmt1.executeQuery();
          if(rs1.next())
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
            if(buySellChargeId>0)
            {
              //SELECT BUYSELLCHAEGEID,BASE,MIN,MAX,FLAT,CHARGERATE,CHARGESLAB,"
                //+"LOWERBOUND,UPPERBOUND,CHARGERATE_INDICATOR FROM QMS_BUYCHARGESDTL WHERE BUYSELLCHAEGEID=? ORDER BY LANE_NO";            
              pstmt2.setDouble(1,buySellChargeId);
              rs2 = pstmt2.executeQuery();
            
              dtlList   = new ArrayList();
             
               
               
           
                 while(rs2.next())
              {
                // logger.info("rs2..while"+rs2);
                buychargesDtlDOB  = new BuychargesDtlDOB();
                buychargesDtlDOB.setChargeSlab((rs2.getString("CHARGESLAB")!=null)?rs2.getString("CHARGESLAB"):"");
                buychargesDtlDOB.setChargeRate(rs2.getDouble("CHARGERATE"));
                buychargesDtlDOB.setLowerBound(rs2.getDouble("LOWERBOUND"));
                buychargesDtlDOB.setUpperBound(rs2.getDouble("UPPERBOUND"));
                buychargesDtlDOB.setChargeRate_indicator((rs2.getString("CHARGERATE_INDICATOR")!=null)?rs2.getString("CHARGERATE_INDICATOR"):"");
                dtlList.add(buychargesDtlDOB); 
                //logger.info("buychargesDtlDOB"+buychargesDtlDOB);
                // logger.info("dtlList"+dtlList);
            
                
               }
            }else
            {
              throw new ObjectNotFoundException("Object Not Found----->");
            }
            buychargesHDRDOB.setBuyChargeDtlList(dtlList);
          }
        }else
        {
          throw new ObjectNotFoundException("Object Not Found----->");
        }
      }catch(SQLException e)
      {
        //Logger.error(FILE_NAME,"SQLException in loadBuyChargesMasterDOB(ArrayList param0) method"+e.toString());
        logger.error(FILE_NAME+"SQLException in loadBuyChargesMasterDOB(ArrayList param0) method"+e.toString());
        throw new EJBException();        
      }catch(Exception e)
      {
        //Logger.error(FILE_NAME,"Exception in loadBuyChargesMasterDOB(ArrayList param0) method"+e.toString());
        logger.error(FILE_NAME+"Exception in loadBuyChargesMasterDOB(ArrayList param0) method"+e.toString());
        throw new EJBException();
      }finally
      {
        try
        {
          if(rs1!=null)
            { rs1.close();}
          if(rs2!=null)
            { rs2.close();}  
          if(pstmt1!=null)
            { pstmt1.close();}
          if(pstmt2!=null)
            { pstmt2.close();}          
          if(connection!=null)
            { connection.close();}
        }catch(SQLException e)
        {
          //Logger.error(FILE_NAME,"SQLException in loadBuyChargesMasterDOB(ArrayList param0) method"+e.toString());
          logger.error(FILE_NAME+"SQLException in loadBuyChargesMasterDOB(ArrayList param0) method"+e.toString());
          throw new EJBException();
        }
      }
      return buychargesHDRDOB;
    }
    private void insertBuyChargeDetails(BuychargesHDRDOB buychargesHDRDOB,ArrayList oldBuyChargeIds)throws EJBException,SQLException
    {
      PreparedStatement pstmt1               = null;
      PreparedStatement pstmt2               = null;
      CallableStatement cstmt                = null;
      //BuychargesHDRDOB  buychargesHDRDOB     = null;
      BuychargesDtlDOB  buychargesDtlDOB     = null;
      int dataListSize                       = 0;
      int dtlListSize                        = 0;
      int hdrInserted                        = 0;
      ArrayList         dtlList              = null;
      int               index                = 0;
      String            rateBreak            = "";
      String            chargeSb             = "";
      int               returnStatus            = 0;
      String            terminalId           = null;
      String            rateType             = null;
      try
      {
//      "BUYSELLCHARGEID,CHARGE_ID,CHARGEBASIS,RATE_BREAK,RATE_TYPE,CURRENCY,WEIGHT_CLASS,"
//     +"CREATED_BY,CREATED_TSTMP,LAST_UPDATED_BY,LAST_UPDATED_TSTMP,DEL_FLAG,ACCESSLEVEL,TERMINALID";
       
        getConnection();
        pstmt1   = connection.prepareStatement(insertHdrQry);
        pstmt2   = connection.prepareStatement(insertDtlQry);
        //if(dataList!=null && dataList.size()>0)
        //  dataListSize  = dataList.size();
        
        /*for(int i=0;i<dataListSize;i++)
        {
          buychargesHDRDOB  = (BuychargesHDRDOB)dataList.get(i);*/
        if(buychargesHDRDOB!=null)
        {
        
          terminalId = buychargesHDRDOB.getTerminalId();
          
          pstmt1.clearParameters();
            pstmt1.setDouble(1,buychargesHDRDOB.getBuySellChargeId());
            pstmt1.setString(2,buychargesHDRDOB.getChargeId());
            pstmt1.setString(3,buychargesHDRDOB.getChargeDescId());
            pstmt1.setString(4,buychargesHDRDOB.getChargeBasisId());
            pstmt1.setString(5,buychargesHDRDOB.getRateBreak());
            pstmt1.setString(6,buychargesHDRDOB.getRateType());
            pstmt1.setString(7,buychargesHDRDOB.getCurrencyId());
            pstmt1.setString(8,buychargesHDRDOB.getWeightClass());
            pstmt1.setString(9,buychargesHDRDOB.getUserId());
            pstmt1.setTimestamp(10,buychargesHDRDOB.getCreate_Time());
            pstmt1.setNull(11,Types.VARCHAR);
            pstmt1.setNull(12,Types.DATE);
            pstmt1.setString(13,"N");
            pstmt1.setString(14,buychargesHDRDOB.getAccessLevel());
            pstmt1.setString(15,buychargesHDRDOB.getTerminalId());
            pstmt1.setString(16,buychargesHDRDOB.getDensityGrpCode());
            hdrInserted = pstmt1.executeUpdate();

 //           "BUYSELLCHAEGEID,BASE,MIN,MAX,FLAT,CHARGERATE,CHARGESLAB,"
  //          +"LOWERBOUND,UPPERBOUND,CHARGERATE_INDICATOR,LANE_NO";
            rateBreak  = buychargesHDRDOB.getRateBreak();
            rateType   = buychargesHDRDOB.getRateType();
            if(hdrInserted>0)
            {
              dtlList = buychargesHDRDOB.getBuyChargeDtlList();
              double buysellID = buychargesHDRDOB.getBuySellChargeId();
              if(dtlList!=null && dtlList.size()>0)
              {
                 dtlListSize = dtlList.size();
                for(index = 0;index<dtlListSize;index++)
                {
                  pstmt2.clearParameters();
                  buychargesDtlDOB = (BuychargesDtlDOB)dtlList.get(index);
                  pstmt2.setDouble(1,buysellID);
                    chargeSb = buychargesDtlDOB.getChargeSlab();
                    if(chargeSb!=null && !chargeSb.equals("") && (chargeSb.equals("BASE") ||
                    chargeSb.equals("MIN") || chargeSb.equals("MAX") || chargeSb.equals("Flat")))
                    {
                      
                      pstmt2.setDouble(2,buychargesDtlDOB.getChargeRate());
                      pstmt2.setString(3,buychargesDtlDOB.getChargeSlab());
                      pstmt2.setDouble(4,0);
                      pstmt2.setDouble(5,0);              
                    }else if(chargeSb.equals("AbsRPersent"))
                    {                      
                      pstmt2.setDouble(2,buychargesDtlDOB.getChargeRate());
                      pstmt2.setString(3,rateBreak);
                      pstmt2.setDouble(4,0);
                      pstmt2.setDouble(5,0);
                    }
                    else
                    {
                     
                      pstmt2.setDouble(2,buychargesDtlDOB.getChargeRate());
                      pstmt2.setString(3,buychargesDtlDOB.getChargeSlab());
                      pstmt2.setDouble(4,buychargesDtlDOB.getLowerBound());
                      pstmt2.setDouble(5,buychargesDtlDOB.getUpperBound());                    
                    }
                  pstmt2.setString(6,(buychargesDtlDOB.getChargeRate_indicator()!=null)?buychargesDtlDOB.getChargeRate_indicator():"");
                  pstmt2.setInt(7,index+1);
                  pstmt2.executeUpdate();
                }
              }
                     if(pstmt1!=null)
                    { pstmt1.close();}
                    if(pstmt2!=null)
                    { pstmt2.close();}                
                    
                    
                    cstmt = connection.prepareCall("{ call PKG_QMS_CHARGES.buy_chages_bkup_proc(?,?,?,?,?,?)}");
                    
                    if(oldBuyChargeIds!=null && oldBuyChargeIds.size()>0)
                    {
                        /*System.out.println("oldBuychargeId"+oldBuyChargeIds);
                        System.out.println("new id::"+buychargesHDRDOB.getBuySellChargeId());
                        
                        System.out.println("pkObj.terminalId::"+terminalId);
                        */
                    	int oldBuyChargeIdsSize	=	oldBuyChargeIds.size();
                        for(int k=0;k<oldBuyChargeIdsSize;k++)
                        {
                            cstmt.clearParameters();
                            cstmt.setString(1,(String)oldBuyChargeIds.get(k));
                            cstmt.setString(2,(buychargesHDRDOB.getBuySellChargeId()+""));
                            cstmt.setString(3,rateBreak);
                            cstmt.setString(4,rateType);
                            cstmt.setString(5,terminalId);
                            cstmt.registerOutParameter(6,Types.INTEGER);
                            
                            
                            cstmt.execute();
                            
                            returnStatus = cstmt.getInt(6);
                            //@@Added by kameswari for the WPBN issue -173772 on 18-06-09 
                            if(returnStatus==0)
                            {
                              throw new Exception("Exception while Updating the Sell charges");
                            }  
                            //@@WPBN issue-173772 on 18-06-09 
                        }
                    }
                    if(cstmt!=null)
                    { cstmt.close();}   
                    //@@Commented by kameswari for the WPBN issue -173772 on 18-06-09 
                    /*
                      if(returnStatus==0)
                            {
                              throw new Exception("Exception while Updating the Sell charges");
                            }    
                     */
            }
            
            
        }
        
      }catch(SQLException e)
      {e.printStackTrace();
        //Logger.error(FILE_NAME,"SQLException in insertBuyChargeDetails(ArrayList param0) method"+e.toString());
        logger.error(FILE_NAME+"SQLException in insertBuyChargeDetails(ArrayList param0) method"+e.toString());
        throw new EJBException();        
      }catch(Exception e)
      {e.printStackTrace();
        //Logger.error(FILE_NAME,"Exception in insertBuyChargeDetails(ArrayList param0) method"+e.toString());
        logger.error(FILE_NAME+"Exception in insertBuyChargeDetails(ArrayList param0) method"+e.toString());
        throw new EJBException();
      }finally
      {
        try
        {
          if(pstmt1!=null)
            { pstmt1.close();}
          if(pstmt2!=null)
            { pstmt2.close();}            
          if(connection!=null)
            { connection.close();}
        }catch(SQLException e)
        {
          //Logger.error(FILE_NAME,"SQLException in insertBuyChargeDetails(ArrayList param0) method"+e.toString());
          logger.error(FILE_NAME+"SQLException in insertBuyChargeDetails(ArrayList param0) method"+e.toString());
          throw new EJBException();
        }
      }
    }
    private void modifyBuyChargeDetails(BuyChargesEntityBeanPK pkObj,BuychargesHDRDOB buychargesHDRDOB)throws EJBException,SQLException
    {
      PreparedStatement pstmt1               = null;
      PreparedStatement pstmt2               = null;
      CallableStatement cstmt                = null;
      ResultSet         rs                   = null;
      BuychargesDtlDOB  buychargesDtlDOB     = null;
      int dtlListSize                        = 0;
      int hdrInserted                        = 0;
      ArrayList         dtlList              = null;
      int               index                = 0;
      String            rateBreak            = "";
      String            chargeSb             = "";
      int               deleted              = 0;
      String            oldBuychargeId       = "";
      int               returnStatus         = 0;
      String            rateType             = null;
      try
      {
        getConnection();
        pstmt1   = connection.prepareStatement(selectQry);
        pstmt1.setString(1,pkObj.chargeId);
        pstmt1.setString(2,pkObj.chargeDescId);
        pstmt1.setString(3,pkObj.terminalId);
        rs = pstmt1.executeQuery();
        
        if(rs.next())
        {
            oldBuychargeId = rs.getString("BUYSELLCHARGEID");
        }
        
        if(rs!=null)
        { rs.close();}
        if(pstmt1!=null)
        { pstmt1.close();}
        
        pstmt1   = connection.prepareStatement(insertHdrQry);
        pstmt2   = connection.prepareStatement(insertDtlQry);
        
       // System.out.println("oldBuychargeIdin modify"+oldBuychargeId);
        
        if(buychargesHDRDOB!=null)
        {
            if(updateDeleteFlag(pkObj))
            {
               //System.out.println("updated --------------");
                pstmt1.setDouble(1,buychargesHDRDOB.getBuySellChargeId());
                pstmt1.setString(2,buychargesHDRDOB.getChargeId());
                pstmt1.setString(3,buychargesHDRDOB.getChargeDescId());
                pstmt1.setString(4,buychargesHDRDOB.getChargeBasisId());
                pstmt1.setString(5,buychargesHDRDOB.getRateBreak());
                pstmt1.setString(6,buychargesHDRDOB.getRateType());
                pstmt1.setString(7,buychargesHDRDOB.getCurrencyId());
                pstmt1.setString(8,buychargesHDRDOB.getWeightClass());
                pstmt1.setNull(9,Types.VARCHAR);
                pstmt1.setNull(10,Types.DATE);
                pstmt1.setString(11,buychargesHDRDOB.getUserId());
                pstmt1.setTimestamp(12,buychargesHDRDOB.getCreate_Time());
                pstmt1.setString(13,"N");
                pstmt1.setString(14,buychargesHDRDOB.getAccessLevel());
                pstmt1.setString(15,buychargesHDRDOB.getTerminalId());
                pstmt1.setString(16,buychargesHDRDOB.getDensityGrpCode());
                hdrInserted = pstmt1.executeUpdate();
     //           "BUYSELLCHAEGEID,BASE,MIN,MAX,FLAT,CHARGERATE,CHARGESLAB,"
      //          +"LOWERBOUND,UPPERBOUND,CHARGERATE_INDICATOR,LANE_NO";
                rateBreak  = buychargesHDRDOB.getRateBreak();
                rateType   = buychargesHDRDOB.getRateType();
                if(hdrInserted>0)
                {
                  dtlList = buychargesHDRDOB.getBuyChargeDtlList();
                  double buysellID = buychargesHDRDOB.getBuySellChargeId();
                  if(dtlList!=null && dtlList.size()>0)
                  {
                     dtlListSize = dtlList.size();
                    for(index = 0;index<dtlListSize;index++)
                    {
                      pstmt2.clearParameters();
                      buychargesDtlDOB = (BuychargesDtlDOB)dtlList.get(index);
                      pstmt2.setDouble(1,buysellID);
                        chargeSb = buychargesDtlDOB.getChargeSlab();
                        if(chargeSb!=null && !chargeSb.equals("") && (chargeSb.equals("BASE") ||
                        chargeSb.equals("MIN") || chargeSb.equals("MAX") || chargeSb.equals("Flat")))
                        {

                          pstmt2.setDouble(2,buychargesDtlDOB.getChargeRate());
                          pstmt2.setString(3,buychargesDtlDOB.getChargeSlab());
                          pstmt2.setNull(4,0);
                          pstmt2.setNull(5,0);              
                        }else if(chargeSb.equals("AbsRPersent"))
                        {

                          pstmt2.setDouble(2,buychargesDtlDOB.getChargeRate());
                          pstmt2.setString(3,rateBreak);

                          pstmt2.setNull(4,Types.DOUBLE);
                          pstmt2.setNull(5,Types.DOUBLE);
                        }
                        else
                        {

                           pstmt2.setDouble(2,buychargesDtlDOB.getChargeRate());
                          pstmt2.setString(3,buychargesDtlDOB.getChargeSlab());
                          pstmt2.setDouble(4,buychargesDtlDOB.getLowerBound());
                          pstmt2.setDouble(5,buychargesDtlDOB.getUpperBound());                    

                        }
                      pstmt2.setString(6,(buychargesDtlDOB.getChargeRate_indicator()!=null)?buychargesDtlDOB.getChargeRate_indicator():"");
                      pstmt2.setInt(7,index+1);
                      pstmt2.executeUpdate();
                    }
                  }
                
                
                
                    if(pstmt1!=null)
                    { pstmt1.close();}
                    if(pstmt2!=null)
                    { pstmt2.close();}                
                    
                    //System.out.println("oldBuychargeIdbefor quote"+oldBuychargeId);
                   // cstmt = connection.prepareCall("call QMS_QUOTEPACK_NEW.qms_quote_update(?,?,?,?,?,?,?,?,?,?)");
                   cstmt = connection.prepareCall("call QMS_QUOTEPACK_NEW.qms_quote_update(?,?,?,?,?,?,?,?,?,?,?,?)");//@@Modified by Kameswari for the WPBN issue-146448 on 04/01/09
                    cstmt.setNull(1,Types.DOUBLE);
                    cstmt.setString(2,oldBuychargeId);
                    cstmt.setNull(3,Types.DOUBLE);
                    cstmt.setNull(4,Types.DOUBLE);
                     cstmt.setNull(5,Types.DOUBLE);
                      cstmt.setNull(6,Types.DOUBLE);
                    cstmt.setString(7,(buychargesHDRDOB.getBuySellChargeId()+""));
                    cstmt.setNull(8,Types.DOUBLE);
                    cstmt.setString(9,"B");
                    cstmt.setNull(10,Types.VARCHAR);
                    cstmt.setNull(11,Types.VARCHAR);
                    cstmt.setString(12,buychargesHDRDOB.getChargeDescId());
                    
                    cstmt.execute();
                    
                    if(cstmt!=null)
                     { cstmt.close();}
                     
                                        
                    
                    
                    cstmt = connection.prepareCall("{ call PKG_QMS_CHARGES.buy_chages_bkup_proc(?,?,?,?,?,?)}");
                    
                    /*System.out.println("oldBuychargeId"+oldBuychargeId);
                    System.out.println("new id::"+buychargesHDRDOB.getBuySellChargeId());
                    System.out.println("pkObj.terminalId::"+pkObj.terminalId);
                    System.out.println("rateBreak:::"+rateBreak);
                    */
                    cstmt.setString(1,oldBuychargeId);
                    cstmt.setString(2,(buychargesHDRDOB.getBuySellChargeId()+""));
                    cstmt.setString(3,rateBreak);
                    cstmt.setString(4,rateType);
                    cstmt.setString(5,pkObj.terminalId);
                    cstmt.registerOutParameter(6,Types.INTEGER);
                    
                    cstmt.execute();
                    
                    returnStatus = cstmt.getInt(6);
                    if(cstmt!=null)
                      { cstmt.close();}                    
                    //Logger.info("","returnStatus"+returnStatus);
                    if(returnStatus==0)
                    {
                      throw new Exception("Exception while Updating the Sell charges");
                    }
                
                }
              }
          }
      }catch(SQLException e)
      {
        //Logger.error(FILE_NAME,"SQLException in modifyBuyChargeDetails(BuychargesHDRDOB param0) method"+e.toString());
        logger.error(FILE_NAME+"SQLException in modifyBuyChargeDetails(BuychargesHDRDOB param0) method"+e.toString());
        e.printStackTrace();
        throw new EJBException();        
      }catch(Exception e)
      {
        //Logger.error(FILE_NAME,"Exception in modifyBuyChargeDetails(BuychargesHDRDOB param0) method"+e.toString());
        logger.error(FILE_NAME+"Exception in modifyBuyChargeDetails(BuychargesHDRDOB param0) method"+e.toString());
        e.printStackTrace();
        throw new EJBException();
      }finally
      {
        try
        {
          // Added by Dilip for PMD Correction on 22/09/2015
           if(rs!=null){ 
             rs.close();
          	 rs=null;
            }
          if(cstmt!=null)
            { cstmt.close();}
          if(pstmt1!=null)
            { pstmt1.close();}
          if(pstmt2!=null)
            { pstmt2.close();}
          if(connection!=null)
            { connection.close();}
        }catch(SQLException e)
        {
          //Logger.error(FILE_NAME,"SQLException in modifyBuyChargeDetails(BuychargesHDRDOB param0) method"+e.toString());
          logger.error(FILE_NAME+"SQLException in modifyBuyChargeDetails(BuychargesHDRDOB param0) method"+e.toString());
          throw new EJBException();
        }
      }
      
    }
    
    private boolean updateDeleteFlag(BuyChargesEntityBeanPK pkObj)throws SQLException,EJBException
    {
      PreparedStatement pstmt1               = null;
      int               deleted              = 0;
      try
      {
        pstmt1   = connection.prepareStatement(updateQry); 
        pstmt1.setString(1,pkObj.chargeId);
        /*pstmt1.setString(2,pkObj.chargeBasisId);*/
        pstmt1.setString(2,pkObj.chargeDescId);
        pstmt1.setString(3,pkObj.terminalId);
        deleted = pstmt1.executeUpdate();

      }catch(SQLException e)
      {
        //Logger.error(FILE_NAME,"SQLException in updateDeleteFlag(BuyChargesEntityBeanPK param0) method"+e.toString());
        logger.error(FILE_NAME+"SQLException in updateDeleteFlag(BuyChargesEntityBeanPK param0) method"+e.toString());
        throw new EJBException();        
      }catch(Exception e)
      {
        //Logger.error(FILE_NAME,"Exception in updateDeleteFlag(BuyChargesEntityBeanPK param0) method"+e.toString());
        logger.error(FILE_NAME+"Exception in updateDeleteFlag(BuyChargesEntityBeanPK param0) method"+e.toString());
        throw new EJBException();
      }finally
      {
        try
        {
          if(pstmt1!=null)
            { pstmt1.close();}
        }catch(SQLException e)
        {
          //Logger.error(FILE_NAME,"SQLException in updateDeleteFlag(BuyChargesEntityBeanPK param0) method"+e.toString());
          logger.error(FILE_NAME+"SQLException in updateDeleteFlag(BuyChargesEntityBeanPK param0) method"+e.toString());
          throw new EJBException();
        }
      }
      
      if(deleted>0)
        return true;
      else
        return false;      
      
    }
    
  public void insertCartageBuyCharges(ArrayList cartageBuyCharges) throws EJBException
  {
    //Logger.info(FILE_NAME,"inside insert cartage::");
 
    PreparedStatement pstmt0         = null;
    PreparedStatement pstmt1         = null;
    //Connection        connection      = null;
    
    OIDSessionHome    oidHome         = null;
    OIDSession	      oidRemote       = null;
    CallableStatement cstmt           = null;
    
    double            buyCartageId    = 0;
    int               count           = 0;
    
    QMSCartageMasterDOB cartageMaster = null;
    QMSCartageBuyDtlDOB buyCartageDtl = null;
    OperationsImpl     operationsImpl = new OperationsImpl();
    long               cartageId      = 0;
    int 				cartageBuyChargesSize	=	cartageBuyCharges.size();//@@ Added by subrahmanyam for Loop Performance 
    
    
   // String seqQuery     = "SELECT SEQ_CARTAGE_BUYDTL.NEXTVAL FROM DUAL";

    
    //ResultSet  rs  =  null;//Commented By RajKumari on 24-10-2008 for Connection Leakages.
    //CallableStatement cstmt = null;
    String result  = "";
    
    try
    {
      getConnection();
      cartageMaster= (QMSCartageMasterDOB)cartageBuyCharges.get(0); 
      /*cstmt   =  connection.prepareCall("{call PKG_QMS_CARTAGE_ACC.cartage_check_for_buycharges(?,?,?,?,?,?)}");
      
      cstmt.setString(1,cartageMaster.getOperation());
      cstmt.setString(2,cartageMaster.getTerminalId());
      cstmt.setString(3,cartageMaster.getChargeType());
      cstmt.setString(4,cartageMaster.getLocationId());
      cstmt.setString(5,cartageMaster.getZoneCodes()[0]);
      cstmt.registerOutParameter(6,Types.VARCHAR);
      cstmt.execute(); 
       
      result  = (String)cstmt.getObject(6);          
      
        if(cstmt!=null)
          cstmt.close();*/
        
      //if(!result.equals("3"))
     // {
     
      cstmt       = connection.prepareCall("{call PKG_QMS_CARTAGE_ACC.cartage_activeinactive(?,?,?,?,?,?,?,?,?,?,?)}");
      oidHome     =(OIDSessionHome)LookUpBean.getEJBHome("OIDSessionBean");
      oidRemote	  =(OIDSession)oidHome.create();      
      buyCartageId= oidRemote.getBuyCartageOID();
      
      //pstmt       =   new PreparedStatement[3];
      
      pstmt0    =   connection.prepareStatement(masterQuery);
      pstmt1    =   connection.prepareStatement(buyDtlQuery);
      pstmt0.setDouble(1,buyCartageId);
      pstmt0.setString(2,cartageMaster.getLocationId());
      pstmt0.setString(3,cartageMaster.getWeightBreak());

      pstmt0.setString(4,cartageMaster.getRateType());
      /*pstmt0.setTimestamp(5,cartageMaster.getEffectiveFrom());
      pstmt0.setTimestamp(6,cartageMaster.getValidUpto());*/
      pstmt0.setString(5,cartageMaster.getCurrencyId());
      //pstmt0.setString(8,cartageMaster.getChargeBasis());
      pstmt0.setString(6,cartageMaster.getUom());
      pstmt0.setString(7,cartageMaster.getMaxChargeFlag());
      pstmt0.setString(8,cartageMaster.getCreatedBy()); 
      pstmt0.setTimestamp(9,cartageMaster.getCreatedTimestamp());
      pstmt0.setString(10,cartageMaster.getAccessLevel());
      pstmt0.setString(11,cartageMaster.getTerminalId());
      pstmt0.setString(12,cartageMaster.getShipmentMode());
      if("1".equalsIgnoreCase(cartageMaster.getShipmentMode()))
          pstmt0.setNull(13,Types.VARCHAR);
      else
          pstmt0.setString(13,cartageMaster.getConsoleType());
      pstmt0.executeUpdate();
      
      for(int i=1;i<=(cartageBuyChargesSize-1);i++)
      {
        buyCartageDtl = (QMSCartageBuyDtlDOB)cartageBuyCharges.get(i);
        
        if(buyCartageDtl.getLineNumber()==0)
            inactiveCartageBuyCharge(cartageMaster.getLocationId(),buyCartageDtl.getZoneCode(),buyCartageDtl.getChargeType(),cartageMaster.getTerminalId(),buyCartageId,cartageMaster.getShipmentMode(),cartageMaster.getConsoleType());
        
        pstmt1.setDouble(1,buyCartageId);
        // logger.info("buyCartageId"+buyCartageId);
        pstmt1.setString(2,buyCartageDtl.getZoneCode());
       // logger.info("buyCartageDtl.getZoneCode()"+buyCartageDtl.getZoneCode());
        pstmt1.setDouble(3,buyCartageDtl.getChargeRate());
        pstmt1.setString(4,buyCartageDtl.getWeightBreakSlab());
        pstmt1.setString(5,buyCartageDtl.getLowerBound());
        pstmt1.setString(6,buyCartageDtl.getUpperBound());
        pstmt1.setString(7,buyCartageDtl.getChargeRateIndicator());
        pstmt1.setString(8,buyCartageDtl.getChargeType());//ADDED BY RK
        pstmt1.setString(9,buyCartageDtl.getDensityRatio());
        pstmt1.setInt(10,buyCartageDtl.getLineNumber());
        pstmt1.setString(11,buyCartageDtl.getChargeBasis());
        pstmt1.setTimestamp(12,buyCartageDtl.getEffectiveFrom());
        pstmt1.setTimestamp(13,buyCartageDtl.getValidUpto());
        pstmt1.addBatch();
       // logger.info("buyCartageDtl.getLineNumber()"+buyCartageDtl.getLineNumber());
         if(buyCartageDtl.getLineNumber()==0)//@@for calling the procedure only once for one record
        {
          cstmt.clearParameters();
          cstmt.setString(1,""+cartageId);
          cstmt.setString(2,""+buyCartageId);
          cstmt.setString(3,cartageMaster.getWeightBreak());
          cstmt.setString(4,cartageMaster.getRateType());
          cstmt.setString(5,cartageMaster.getLocationId());
          cstmt.setString(6,buyCartageDtl.getZoneCode());
          cstmt.setString(7,cartageMaster.getChargeBasis());
          cstmt.setString(8,buyCartageDtl.getChargeType());
          cstmt.setString(9,cartageMaster.getShipmentMode());
          if("1".equalsIgnoreCase(cartageMaster.getShipmentMode()))
              cstmt.setNull(10,Types.VARCHAR);
          else
              cstmt.setString(10,cartageMaster.getConsoleType());
          cstmt.setInt(11,count);
          cstmt.addBatch();
          count++;
     
        }
      }
      pstmt1.executeBatch();
      cstmt.executeBatch();
      
      operationsImpl.setTransactionDetails(cartageMaster.getTerminalId(),cartageMaster.getCreatedBy(),
                                            "Cartage Buy Charges",Double.toString(buyCartageId),
                                            cartageMaster.getCreatedTimestamp(),cartageMaster.getOperation());
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
          /* if(rs!=null)
            { rs.close();}*///Commented By RajKumari on 24-10-2008 for Connection Leakages.
          if(pstmt0!=null)
            { pstmt0.close();}
          if(pstmt1!=null)
            { pstmt1.close();}
            if(cstmt!=null)cstmt.close();
          if(connection!=null)
            { connection.close();}
            
            cartageMaster     = null;
            cartageBuyCharges = null;
        }
        catch (Exception e)
        {
          //Logger.error(FILE_NAME,"Error while Closing Resources"+e);
          logger.error(FILE_NAME+"Error while Closing Resources"+e);
          e.printStackTrace();
        }
    }
                          
  }

  private void inactiveCartageBuyCharge(String locationId,String zoneCode,String chargeType,String terminalId,double cartageId,String shipMode,String consoleType)
  {
    CallableStatement cstmt       =  null;
    //ResultSet         rs          =  null;
    
    
    try
    { 
    
      //Logger.info(FILE_NAME,"inactiveCartageBuyCharge::"+cartageId);
      
      //Logger.info(FILE_NAME,"zoneCodezoneCode::"+zoneCode);
      //Logger.info(FILE_NAME,"chargeType::"+chargeType);
      //Logger.info(FILE_NAME,"locationId::"+locationId);
      
      cstmt  = connection.prepareCall("{call PKG_QMS_CARTAGE_ACC.buy_cartage_update_quote(?,?,?,?,?,?,?,?)}");
      
      cstmt.setString(1,terminalId);
      cstmt.setString(2,chargeType);
      cstmt.setString(3,locationId);
      cstmt.setString(4,zoneCode);
      cstmt.setString(5,shipMode);
      if("1".equalsIgnoreCase(shipMode))
          cstmt.setNull(6,Types.VARCHAR);
      else
          cstmt.setString(6,consoleType);
      cstmt.setString(7,""+cartageId);
      cstmt.registerOutParameter(8,Types.VARCHAR);
      
      cstmt.execute();
      
      //Logger.info(FILE_NAME,"cstmt..."+ cstmt.getString(6));
      
     /* if(rs.next())
      {
        cartageId = rs.getLong(1);
      }
        
      if(pStmt!=null)
        pStmt.close();
        
      pStmt   =  connection.prepareStatement(updateDtlQry);
      
      pStmt.setString(1,zoneCode);
      pStmt.setString(2,chargeType);
      pStmt.setString(3,locationId);
      pStmt.setString(4,zoneCode);
      pStmt.setString(5,chargeType);
      pStmt.executeUpdate();
      
      if(pStmt!=null)
        pStmt.close();
        
      /*pStmt   =  connection.prepareStatement(updateSellDtlQry);//@@Inactivating Corresponding Sell Charges
      
      pStmt.setString(1,zoneCode);
      pStmt.setString(2,chargeType);
      pStmt.setString(3,locationId);
      pStmt.setString(4,zoneCode);
      pStmt.setString(5,chargeType);
      pStmt.executeUpdate();*/
      
      
    }   
    catch(SQLException e)
    {
      e.printStackTrace();
      //Logger.error(FILE_NAME,"Error while inactivating the record(SQL Exception) "+e.toString());
      logger.error(FILE_NAME+"Error while inactivating the record(SQL Exception) "+e.toString());
      throw new EJBException("Error while inactivating the record(SQL Exception) ");
    }
    catch(Exception e)
    {
      e.printStackTrace();
      //Logger.error(FILE_NAME,"Error while inactivating the record "+e.toString());
      logger.error(FILE_NAME+"Error while inactivating the record "+e.toString());
      throw new EJBException("Error while inactivating the record ");
    }
    finally
    {
      ConnectionUtil.closeConnection(null,cstmt,null);
    }
  }
  
  private boolean validateDensityRatio(String densityRatio)throws SQLException
    {
      
      PreparedStatement pStmt  = null;
      ResultSet         rs     = null;
      try
      {
        this.getConnection();
        pStmt    = connection.prepareStatement(checkDensityRatio);
        
        String[] values = densityRatio.split(":");
        pStmt.setString(1,values[0]);
        pStmt.setString(2,values[1]);
        pStmt.setString(3,"4");
        rs  = pStmt.executeQuery();
        if(rs.next())
          return true;
        
      }
      catch(SQLException e)
      {
        e.printStackTrace();
        throw new SQLException("Error while checking density ratio");
      }
      catch(Exception e)
      {
        e.printStackTrace();
        throw new EJBException("Error while checking density ratio");
      }
      finally
      {
        ConnectionUtil.closeConnection(connection,pStmt,rs);
      }
      return false;
    }
  
  
  
}