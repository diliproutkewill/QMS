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
 * @author : I.V.Sekhar MerrintiINSERT INTO QMS_SELLCHARGESDTL
 * * @date 25-06-2005
 * Modified by      Date     Reason
 */
 package com.qms.operations.charges.sellcharges.dao;
import com.foursoft.esupply.common.exception.FoursoftException;
import com.foursoft.etrans.common.util.java.OperationsImpl;
import com.qms.operations.charges.java.QMSCartageMasterDOB;
import com.qms.operations.charges.java.QMSCartageSellDtlDOB;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.HashMap;
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

import com.qms.operations.charges.sellcharges.ejb.bmp.SellChargesEntityBeanPK;
import com.qms.operations.charges.java.BuychargesHDRDOB;
import com.qms.operations.charges.java.BuychargesDtlDOB;
import oracle.jdbc.OracleTypes;
public class SellChargesDAO
{
    static final String FILE_NAME="SellChargesDAO.java";
    DataSource  dataSource=null;
    Connection  connection=null;
   /* private static final String insertHdrQry = "INSERT INTO QMS_SELLCHARGESMASTER (SELLCHARGEID,CHARGE_ID,CHARGEBASIS,RATE_BREAK,RATE_TYPE,CURRENCY,WEIGHT_CLASS,"
                                              +"OVERALL_MARGIN,MARGIN_TYPE,MARGIN_BASIS,CREATED_BY,CREATED_TSTMP,LAST_UPDATED_BY,LAST_UPDATED_TSTMP,IE_FLAG,ACCESSLEVEL,TERMINALID,DUMMY_SELL_CHARGES_FLAG)"
                                              +" VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
*/


    private static final String insertHdrQry = "INSERT INTO QMS_SELLCHARGESMASTER (SELLCHARGEID,CHARGE_ID,CHARGEBASIS,RATE_BREAK,RATE_TYPE,CURRENCY,WEIGHT_CLASS,"
                                              +"OVERALL_MARGIN,MARGIN_TYPE,MARGIN_BASIS,CREATED_BY,CREATED_TSTMP,LAST_UPDATED_BY,LAST_UPDATED_TSTMP,IE_FLAG,ACCESSLEVEL,TERMINALID,DUMMY_SELL_CHARGES_FLAG,CHARGEDESCID,BUYCHARGEID)"
                                              +" VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
                                              
/*    private static final String selectHdrQry = "SELECT SSM.SELLCHARGEID,SSM.CHARGE_ID,SSM.CHARGEDESCID,SSM.CHARGEBASIS,BM.BASIS_DESCRIPTION,SSM.RATE_BREAK,SSM.RATE_TYPE,SSM.CURRENCY,SSM.WEIGHT_CLASS,"
                                              +" SSM.OVERALL_MARGIN,SSM.MARGIN_TYPE,SSM.MARGIN_BASIS,SSM.DUMMY_SELL_CHARGES_FLAG "
                                              +" FROM QMS_SELLCHARGESMASTER SSM,QMS_CHARGE_BASISMASTER BM WHERE SSM.CHARGE_ID=? AND SSM.CHARGEBASIS=? AND "
                                              +" SSM.RATE_BREAK=? AND SSM.RATE_TYPE=? AND SSM.TERMINALID=? AND "
                                              +" SSM.CHARGEBASIS=BM.CHARGEBASIS AND SSM.IE_FLAG='A'";
*/
    private static final String selectHdrQry = "SELECT SSM.SELLCHARGEID,SSM.CHARGE_ID,SSM.CHARGEDESCID,SSM.CHARGEBASIS,BM.BASIS_DESCRIPTION,SSM.RATE_BREAK,SSM.RATE_TYPE,SSM.CURRENCY,SSM.WEIGHT_CLASS,"
                                              +" SSM.OVERALL_MARGIN,SSM.MARGIN_TYPE,SSM.MARGIN_BASIS,SSM.DUMMY_SELL_CHARGES_FLAG,SSM.BUYCHARGEID "
                                              +" FROM QMS_SELLCHARGESMASTER SSM,QMS_CHARGE_BASISMASTER BM WHERE SSM.CHARGE_ID=? AND SSM.CHARGEDESCID=? AND "
                                              +" SSM.TERMINALID=? AND "
                                              +" SSM.CHARGEBASIS=BM.CHARGEBASIS AND SSM.IE_FLAG='A'";
                                              
   //@@Added by Kameswari for the WPBN issue-154398 on 15/02/09
     private static final String selectHdrAccQry = "SELECT SSM.SELLCHARGEID,SSM.CHARGE_ID,SSM.CHARGEDESCID,SSM.CHARGEBASIS,BM.BASIS_DESCRIPTION,SSM.RATE_BREAK,SSM.RATE_TYPE,SSM.CURRENCY,SSM.WEIGHT_CLASS,"
                                              +" SSM.OVERALL_MARGIN,SSM.MARGIN_TYPE,SSM.MARGIN_BASIS,SSM.BUYCHARGEID "
                                              +" FROM QMS_SELLCHARGESMASTER_ACC SSM,QMS_CHARGE_BASISMASTER BM WHERE SSM.CHARGE_ID=? AND SSM.CHARGEDESCID=? AND "
                                              +" SSM.TERMINALID=? AND "
                                              +" SSM.CHARGEBASIS=BM.CHARGEBASIS AND SSM.IE_FLAG='A'";

    private static final String insertDtlQry = "INSERT INTO QMS_SELLCHARGESDTL (SELLCHARGEID,CHARGESLAB,CHARGERATE,MARGINVALUE,"
                                              +"LOWERBOUND,UPPERBOUND,CHARGERATE_INDICATOR,LANE_NO,ID) VALUES (?,?,?,?,?,?,?,?,SEQ_SELLCHARGESDTL.nextval)";

    private static final String selectDtlQry = "SELECT SELLCHARGEID,CHARGESLAB,CHARGERATE,MARGINVALUE,"
                                              +"LOWERBOUND,UPPERBOUND,CHARGERATE_INDICATOR FROM QMS_SELLCHARGESDTL WHERE SELLCHARGEID=? ORDER BY LANE_NO";                                              
    
      //@@Added by Kameswari for the WPBN issue-154398 on 15/02/09
    private static final String selectDtlAccQry = "SELECT SELLCHARGEID,CHARGESLAB,CHARGERATE,MARGINVALUE,"
                                              +"LOWERBOUND,UPPERBOUND,CHARGERATE_INDICATOR FROM QMS_SELLCHARGESDTL_ACC WHERE SELLCHARGEID=? ORDER BY LANE_NO";                                              
  
//    private static final String updateQry    = "UPDATE QMS_SELLCHARGESMASTER SET IE_FLAG='I' WHERE CHARGE_ID=? AND CHARGEBASIS=? AND RATE_BREAK=? AND RATE_TYPE=? AND TERMINALID=?";
//    private static final String deleteQry    = "UPDATE QMS_SELLCHARGESMASTER SET IE_FLAG='I' WHERE CHARGE_ID=? AND CHARGEBASIS=? AND RATE_BREAK=? AND RATE_TYPE=? AND TERMINALID=?";                                              

      private static final String updateQry    = "UPDATE QMS_SELLCHARGESMASTER SET IE_FLAG='I' WHERE CHARGE_ID=? AND CHARGEDESCID=? AND TERMINALID=? AND  IE_FLAG='A'";
      private static final String deleteQry    = "UPDATE QMS_SELLCHARGESMASTER SET IE_FLAG='I' WHERE CHARGE_ID=? AND CHARGEDESCID=? AND TERMINALID=? AND  IE_FLAG='A'";                                              

                                 
      private static final String sqlGetSellAccDtls = " SELECT DISTINCT SELL.CARTAGE_ID,SELL.MARGIN_TYPE,SELL.OVERALL_MARGIN,SELL.MARGIN ,SELL.BUYRATE_AMT CHARGERATE ,"+
                                                      " SELL.CHARGESLAB,SELL.ZONE_CODE,SELL.CHARGE_TYPE,SELL.LINE_NO FROM QMS_CARTAGE_BUYSELLCHARGES_A MAS,QMS_CARTAGE_SELLDTL_A SELL "+
                                                      " WHERE MAS.CARTAGE_ID=SELL.CARTAGE_ID AND SELL.CARTAGE_ID=? AND SELL.ZONE_CODE=? AND SELL.CHARGE_TYPE=? ORDER BY LINE_NO ";
                                                      
     private static final String sqlUpdateSellAcceptModify = " UPDATE qms_cartage_selldtl_a SET CHARGERATE =?,MARGIN =?,OVERALL_MARGIN =? ,MARGIN_TYPE =?"+
                                                            "  WHERE CARTAGE_ID =? AND ZONE_CODE =? AND CHARGE_TYPE =? AND LINE_NO=?";
                                         
      private static final String insertSellDtlQuery = "INSERT INTO QMS_CARTAGE_SELLDTL (CARTAGE_ID,ZONE_CODE,OVERALL_MARGIN,MARGIN_TYPE,MARGIN_BASIS,MARGIN,"+
                                                      "CHARGERATE,CHARGESLAB,LOWERBOUND,UPPERBOUND,CHARGE_TYPE,CURRENCY,BUYRATE_AMT,ACTIVEINACTIVE,ID,LINE_NO,SELL_CARTAGE_ID,CHARGE_BASIS,EFFECTIVE_FROM,VALID_UPTO,CHARGERATE_INDICATOR) "+
                                                      "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,'A',SEQ_CARTAGE_SELLDTL.NEXTVAL,?,?,?,?,?,?)"; 
      
      private static final String  updateDtlQry   =  "update QMS_CARTAGE_sellDTL  set activeinactive ='I' WHERE CARTAGE_ID IN "+
                                                     " (SELECT dtl.CARTAGE_ID FROM QMS_CARTAGE_sellDTL dtl,QMS_CARTAGE_BUYSELLCHARGES mas "+
                                                     " WHERE mas.CARTAGE_ID=dtl.cartage_id  AND ZONE_CODE=? AND CHARGE_TYPE = ? "+
                                                     " and LOCATION_ID=? and activeinactive='A') and ZONE_CODE=? AND CHARGE_TYPE =?";
      
      private static final String  updateBuyDtlQry =  "UPDATE QMS_CARTAGE_BUYDTL  SET SELLCHARGE_FLAG = 'Y' WHERE  CARTAGE_ID =? AND ZONE_CODE=? AND CHARGE_TYPE=?";
      
//@@Commented by subrahmanyam for the Enhancement 170759 on 02/06/09
      /*
      private static final String  sqlGetSlabValues = " select distinct(CHARGESLAB) from qms_cartage_buysellcharges_a mas,qms_cartage_selldtl_a sell "+
                                                      " where mas.cartage_id=sell.cartage_id  and sell.cartage_id=? and sell.zone_code=? and "+
                                                      " sell.charge_type=?  AND CHARGESLAB NOT IN ('MIN','MAX') order by TO_NUMBER(CHARGESLAB)";
    */                                                      
//@@Added by subrahmanyam for the Enhancement 170759 on 02/06/09
      private static final String  sqlGetSlabValues = " select distinct(CHARGESLAB) from qms_cartage_buysellcharges_a mas,qms_cartage_selldtl_a sell "+
                                                      " where mas.cartage_id=sell.cartage_id  and sell.cartage_id=? and sell.zone_code=? and "+
                                                      " sell.charge_type=?  AND CHARGESLAB NOT IN ('BASE','MIN','MAX') order by TO_NUMBER(CHARGESLAB)";
//@@ Ended for the Enhancement 170759 on 02/06/09                                                      
      private static final String  chargeSlabQry   = " SELECT DISTINCT(CHARGESLAB) FROM QMS_CARTAGE_BUYSELLCHARGES_A MAS,QMS_CARTAGE_SELLDTL_A SELL "+
                                                      " WHERE MAS.CARTAGE_ID=SELL.CARTAGE_ID  AND SELL.CARTAGE_ID=? AND SELL.ZONE_CODE=? AND "+
                                                      " SELL.CHARGE_TYPE=? ORDER BY CHARGESLAB";
                                          
      private static final String  sqlGetSlabDtls   = " SELECT DISTINCT SELL.CARTAGE_ID ,SELL.CHARGE_TYPE,SELL.ZONE_CODE,MARGIN_TYPE,OVERALL_MARGIN,MARGIN ,SELL.BUYRATE_AMT CHARGERATE  ,"+
                                                      " SELL.CHARGESLAB,SELL.LOWERBOUND,SELL.UPPERBOUND,SELL.LINE_NO FROM QMS_CARTAGE_BUYSELLCHARGES_A MAS,QMS_CARTAGE_SELLDTL_A SELL  "+
                                                      " WHERE MAS.CARTAGE_ID=SELL.CARTAGE_ID AND SELL.CARTAGE_ID=? AND SELL.ZONE_CODE=? AND SELL.CHARGE_TYPE=? "+
                                                      " ORDER BY LINE_NO ";

                                              
    private static final String insertBuyHdrQry = "INSERT INTO QMS_BUYSELLCHARGESMASTER (BUYSELLCHARGEID,CHARGE_ID,CHARGEDESCID,CHARGEBASIS,RATE_BREAK,RATE_TYPE,CURRENCY,WEIGHT_CLASS,"
                                              +"CREATED_BY,CREATED_TSTMP,LAST_UPDATED_BY,LAST_UPDATED_TSTMP,DEL_FLAG,ACCESSLEVEL,TERMINALID,DENSITY_CODE)"
                                              +" VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";


    private static final String insertBuyDtlQry = "INSERT INTO QMS_BUYCHARGESDTL (BUYSELLCHAEGEID,CHARGERATE,CHARGESLAB,"
                                              +"LOWERBOUND,UPPERBOUND,CHARGERATE_INDICATOR,LANE_NO,ID) VALUES (?,?,?,?,?,?,?,SEQ_BUYCHARGESDTL.NEXTVAL)";
                                              
                                              

   // private static final String insertBuyHdrQry = "INSERT INTO QMS_BUYSELLCHARGESMASTER (BUYSELLCHARGEID,CHARGE_ID,CHARGEDESCID,CHARGEBASIS,RATE_BREAK,RATE_TYPE,CURRENCY,WEIGHT_CLASS,"
                                             // +"CREATED_BY,CREATED_TSTMP,LAST_UPDATED_BY,LAST_UPDATED_TSTMP,DEL_FLAG,ACCESSLEVEL,TERMINALID,DENSITY_CODE)"
                                              //+" VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";


    //private static final String insertBuyDtlQry = "INSERT INTO QMS_BUYCHARGESDTL (BUYSELLCHAEGEID,CHARGERATE,CHARGESLAB,"
                                             // +"LOWERBOUND,UPPERBOUND,CHARGERATE_INDICATOR,LANE_NO,ID) VALUES (?,?,?,?,?,?,?,SEQ_BUYCHARGESDTL.NEXTVAL)";
                                              

    private static Logger logger = null;

  /**
   * 
   */
    public SellChargesDAO()
    {
          logger  = Logger.getLogger(SellChargesDAO.class);
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
  /**
   * 
   * @throws java.sql.SQLException
   * @throws javax.ejb.EJBException
   * @throws javax.ejb.ObjectNotFoundException
   * @return 
   * @param pkObj
   */
    public SellChargesEntityBeanPK findByPrimariKey(SellChargesEntityBeanPK pkObj)throws ObjectNotFoundException,EJBException,SQLException
    {
      PreparedStatement pstmt = null;
      ResultSet         rs    = null;
      boolean     sellChargesRow  = false;
      try
      {
        getConnection();
        pstmt = connection.prepareStatement(selectHdrQry);
        pstmt.setString(1,pkObj.chargeId);
        pstmt.setString(2,pkObj.chargeDescId);
       // pstmt.setString(3,pkObj.rateBreak);
       // pstmt.setString(4,pkObj.rateType);
        pstmt.setString(3,pkObj.terminalId);
        rs    = pstmt.executeQuery();
        if(rs.next())
        {
          sellChargesRow  = true;
        }
      }catch(SQLException e)
      {
        //Logger.error(FILE_NAME,"SQLException in findByPrimariKey(SellChargesEntityBeanPK param0) method"+e.toString());
        logger.error(FILE_NAME+"SQLException in findByPrimariKey(SellChargesEntityBeanPK param0) method"+e.toString());
        throw new SQLException();         
      }catch(Exception e)
      {
         //Logger.error(FILE_NAME,"Exception in findByPrimariKey(SellChargesEntityBeanPK param0) method"+e.toString());
         logger.error(FILE_NAME+"Exception in findByPrimariKey(SellChargesEntityBeanPK param0) method"+e.toString());
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
          //Logger.error(FILE_NAME,"SQLException in findByPrimariKey(SellChargesEntityBeanPK param0) method"+e.toString());
          logger.error(FILE_NAME+"SQLException in findByPrimariKey(SellChargesEntityBeanPK param0) method"+e.toString());
          throw new SQLException();          
        }
      }
      
      if(sellChargesRow)
      {
        return pkObj;
      }
      else
      {
        throw new ObjectNotFoundException();
      }    
    }    
  /**
   * 
   * @throws javax.ejb.EJBException
   * @param buychargesHDRDOB
   */
    public void create(BuychargesHDRDOB buychargesHDRDOB)throws FoursoftException,EJBException
    {
      CallableStatement  cstmt = null;
      String returnValue      = "";    
      PreparedStatement    pstmt = null;
      ResultSet            rs    = null;
      double newBuyChargeId   = 0.0;
      String selectSeq = " SELECT BUYSELL_SEQ.NEXTVAL BUYCHARGEID FROM DUAL"; 
      
      try
      {
        getConnection();
        
            if("T".equals(buychargesHDRDOB.getDummyBuychargesFlag()))
            {
                pstmt = connection.prepareStatement(selectSeq);
                
                rs = pstmt.executeQuery();
                
                if(rs.next())
                {
                  newBuyChargeId = rs.getDouble("BUYCHARGEID");
                }
                buychargesHDRDOB.setBuychargeId(newBuyChargeId); 
                
                if(rs!=null)
                  { rs.close();}
                if(pstmt!=null)
                  { pstmt.close();}              
            }
        
        //System.out.println("buychargesHDRDOB.getBuychargeId()"+buychargesHDRDOB.getBuychargeId());
        //System.out.println("buychargesHDRDOB.getDummyBuychargesFlag()"+buychargesHDRDOB.getDummyBuychargesFlag());
        
        cstmt = connection.prepareCall("{? = call PKG_QMS_CHARGES.SELLCHARGESADD(?,?,?,?,?,?,?,?,?)}");
        cstmt.registerOutParameter(1, Types.VARCHAR);
        cstmt.setString(2,buychargesHDRDOB.getChargeId());
        cstmt.setString(3,buychargesHDRDOB.getChargeDescId());
        cstmt.setString(4,buychargesHDRDOB.getChargeBasisId());
        cstmt.setString(5,buychargesHDRDOB.getRateBreak());
        cstmt.setDouble(6,buychargesHDRDOB.getBuySellChargeId());
        cstmt.setDouble(7,buychargesHDRDOB.getBuychargeId());
        cstmt.setString(8,buychargesHDRDOB.getTerminalId());
        cstmt.setString(9,(buychargesHDRDOB.getDummyBuychargesFlag()!=null)?buychargesHDRDOB.getDummyBuychargesFlag():"");
        cstmt.setString(10,buychargesHDRDOB.getDensityGrpCode());
        
        cstmt.execute();
        returnValue = cstmt.getString(1);
        
          if(connection!=null)
          { connection.close();}        
        if(returnValue.equals("1") || returnValue.equals("2") || 
        returnValue.equals("3") || returnValue.equals("4") || returnValue.equals("7"))
        { throw new FoursoftException("Invalid data");}
        else if(returnValue.equals("100"))
        { throw new EJBException("Exception while inserting");}
        else if(returnValue.startsWith("H_"))
        { throw new FoursoftException("Data exist at higer levels"+returnValue);}
        else if(returnValue.startsWith("6"))
        {
            insertSellChargeDetails(buychargesHDRDOB,"create");
            if("T".equals(buychargesHDRDOB.getDummyBuychargesFlag()))
            {
              insertBuyChargeDetails(buychargesHDRDOB);
            }
            
        }
        else
        { throw new FoursoftException("Exception while inserting");}
        
      }catch(FoursoftException e)
      {
         e.printStackTrace();
        //Logger.error(FILE_NAME,"SQLException in create(BuychargesHDRDOB param0) method"+e.toString());
        logger.error(FILE_NAME+"SQLException in create(BuychargesHDRDOB param0) method"+e.toString());
        throw new FoursoftException(e.getMessage());        
      }catch(SQLException e)
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
          if(cstmt!=null)
          { cstmt.close();}
          ConnectionUtil.closePreparedStatement(pstmt,rs);// Added by Dilip for PMD Correction on 22/09/2015
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
  /**
   * 
   * @throws javax.ejb.EJBException
   * @return 
   * @param pkObj
   */
   public BuychargesHDRDOB load(SellChargesEntityBeanPK pkObj)throws EJBException
    {
      BuychargesHDRDOB  sellchargesHDRDOB  = null;
      try
      {
        sellchargesHDRDOB  = loadSellChargesMasterDOB(pkObj);
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
      return sellchargesHDRDOB;        
    }
  /**
   * 
   * @throws javax.ejb.EJBException
   * @throws java.sql.SQLException
   * @param sellchargesHDRDOB
   * @param pkObj
   */
    public void store(SellChargesEntityBeanPK pkObj,BuychargesHDRDOB sellchargesHDRDOB)throws SQLException,EJBException
    {
      try
      {
         modifySellChargeDetails(pkObj,sellchargesHDRDOB);
      }catch(SQLException e)
      {
        //Logger.error(FILE_NAME,"SQLException in store(SellChargesEntityBeanPK param0) method"+e.toString());
        logger.error(FILE_NAME+"SQLException in store(SellChargesEntityBeanPK param0) method"+e.toString());
        throw new EJBException();         
      }catch(EJBException e)
      {
        //Logger.error(FILE_NAME,"EJBException in store(SellChargesEntityBeanPK param0) method"+e.toString());
        logger.error(FILE_NAME+"EJBException in store(SellChargesEntityBeanPK param0) method"+e.toString());
        throw new EJBException();         
      }catch(Exception e)
      {
         //Logger.error(FILE_NAME,"SQLException in store(SellChargesEntityBeanPK param0) method"+e.toString());
         logger.error(FILE_NAME+"SQLException in store(SellChargesEntityBeanPK param0) method"+e.toString());
        throw new EJBException();        
      }     
    }
  /**
   * 
   * @throws java.sql.SQLException
   * @throws javax.ejb.EJBException
   * @param sellchargesHDRDOB
   * @param pkObj
   */
    private void modifySellChargeDetails(SellChargesEntityBeanPK pkObj,BuychargesHDRDOB sellchargesHDRDOB)throws EJBException,SQLException
    {
    
      String selectQry            = "SELECT SELLCHARGEID FROM QMS_SELLCHARGESMASTER WHERE CHARGE_ID=? AND CHARGEDESCID=? AND TERMINALID=? AND IE_FLAG='A'";
      String  buychargeIdQuery    = "SELECT QSM.BUYCHARGEID BUYCHARGEID FROM QMS_SELLCHARGESMASTER QSM ,QMS_SELLCHARGESMASTER_ACC QSA WHERE QSA.SELLCHARGEID=QSM.SELLCHARGEID AND QSA.SELLCHARGEID=?";
      PreparedStatement pstmt1    = null;
      ResultSet         rs        = null;
      PreparedStatement pstmt2    = null;
      ResultSet         rs1       = null;
      double           oldSellChargeId = 0.0;
      double           oldBuyChargeId = 0.0;
      CallableStatement   csmt   = null;
      
      try
      {
        getConnection();
        if(sellchargesHDRDOB!=null)
        {
        
            pstmt1   = connection.prepareStatement(selectQry); 
            pstmt1.setString(1,pkObj.chargeId);
            /*pstmt1.setString(2,pkObj.chargeBasisId);
            pstmt1.setString(3,pkObj.rateBreak);
            pstmt1.setString(4,pkObj.rateType);*/
            pstmt1.setString(2,pkObj.chargeDescId);
            pstmt1.setString(3,pkObj.terminalId);
            rs = pstmt1.executeQuery();  
            
            if(rs.next())
            { 
              oldSellChargeId      = rs.getDouble("SELLCHARGEID");
            }
            
            if(rs!=null)
              { rs.close();}
            if(pstmt1!=null)
              {pstmt1.close();}
            
           if("Accept".equalsIgnoreCase(sellchargesHDRDOB.getOperation()))
           {
              pstmt2    = connection.prepareStatement(buychargeIdQuery); 
              pstmt2.setDouble(1,oldSellChargeId);
              rs1       = pstmt2.executeQuery();  
              
              if(rs1.next())
              { 
                 oldBuyChargeId = rs1.getDouble("BUYCHARGEID");
              }
           } 
       
            //sellchargesHDRDOB.setOldBuyChargeId(oldSellChargeId);
           
            if(updateDeleteFlag(pkObj))
            {
              // csmt = connection.prepareCall("{ call QMS_QUOTEPACK_NEW.qms_quote_update(?,?,?,?,?,?,?,?,?,?) }");
              csmt = connection.prepareCall("{ call QMS_QUOTEPACK_NEW.qms_quote_update(?,?,?,?,?,?,?,?,?,?,?,?) }");//@@Modified by Kameswari for the WPBN issue-146448 on 04/01/09
              csmt.setDouble(1,oldSellChargeId);
              if("Accept".equalsIgnoreCase(sellchargesHDRDOB.getOperation()))
              {
                  csmt.setDouble(2,oldBuyChargeId);
              }
              else
              {
                  csmt.setDouble(2,sellchargesHDRDOB.getBuychargeId());
              }
              csmt.setNull(3,Types.DOUBLE);
              csmt.setNull(4,Types.DOUBLE);
              csmt.setNull(5,Types.DOUBLE);
              csmt.setDouble(6,sellchargesHDRDOB.getBuySellChargeId());
              csmt.setDouble(7,sellchargesHDRDOB.getBuychargeId());
              csmt.setNull(8,Types.DOUBLE);
              csmt.setString(9,"S");
              csmt.setNull(10,Types.VARCHAR);
              csmt.setNull(11,Types.VARCHAR);
              csmt.setString(12,sellchargesHDRDOB.getChargeDescId());
              
              csmt.execute();
      
              if(connection!=null)
              { connection.close();}
              
              insertSellChargeDetails(sellchargesHDRDOB,"modify");
              
              
            }else
            {
              throw new Exception();
            }
        }else
        {
          throw new Exception();
        }
      }catch(SQLException e)
      {
       e.printStackTrace();
        //Logger.error(FILE_NAME,"SQLException in modifyBuyChargeDetails(BuychargesHDRDOB param0) method"+e.toString());
        logger.error(FILE_NAME+"SQLException in modifyBuyChargeDetails(BuychargesHDRDOB param0) method"+e.toString());
        throw new EJBException();        
      }catch(Exception e)
      {
        e.printStackTrace();
        //Logger.error(FILE_NAME,"Exception in modifyBuyChargeDetails(BuychargesHDRDOB param0) method"+e.toString());
        logger.error(FILE_NAME+"Exception in modifyBuyChargeDetails(BuychargesHDRDOB param0) method"+e.toString());
        throw new EJBException();
      }finally
      {
        try{
            if(rs!=null)
              { rs.close();}
            if(pstmt1!=null)
              {pstmt1.close();}     
            if(rs1!=null)
              { rs1.close();}
            if(pstmt2!=null)
              {pstmt2.close();}    
            if(connection!=null)
            {  connection.close();}
        }catch(SQLException e)
        {
          //Logger.error(FILE_NAME,"SQLException in modifyBuyChargeDetails(BuychargesHDRDOB param0) method"+e.toString());
          logger.error(FILE_NAME+"SQLException in modifyBuyChargeDetails(BuychargesHDRDOB param0) method"+e.toString());
          throw new EJBException();        
        }
      }
    }
  /**
   * 
   * @throws javax.ejb.EJBException
   * @return 
   * @param pkObj
   */
    private BuychargesHDRDOB loadSellChargesMasterDOB(SellChargesEntityBeanPK pkObj)throws EJBException
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
      String            rateBreak            = null;
      String            rateType             = null;
      String            chargeDescId         = null;
      String            terminalId           = null;
      String            operation            = null;//@@Addded by Kameswari for the WPBN issue-154398 on 15/02/09
      double            buySellChargeId      = 0.0;
      try
      {
        getConnection();
        pstmt1  = connection.prepareStatement(selectHdrQry);
        pstmt2  = connection.prepareStatement(selectDtlQry);        
        if(pkObj!=null)
        {
          chargeId        = pkObj.chargeId;
         /* charegBasisId   = pkObj.chargeBasisId;
          rateBreak       = pkObj.rateBreak;
          rateType        = pkObj.rateType;*/
          chargeDescId    = pkObj.chargeDescId;
          terminalId      = pkObj.terminalId;
          operation        = pkObj.operation;
          //@@Addded by Kameswari for the WPBN issue-154398 on 15/02/09
          if("Accept".equalsIgnoreCase(operation))
          {
             pstmt1  = connection.prepareStatement(selectHdrAccQry);
             pstmt2  = connection.prepareStatement(selectDtlAccQry);
          }
          else
          {
             pstmt1  = connection.prepareStatement(selectHdrQry);
            pstmt2  = connection.prepareStatement(selectDtlQry);
          }
          pstmt1.setString(1,chargeId);
          /*pstmt1.setString(2,charegBasisId);
          pstmt1.setString(3,rateBreak);
          pstmt1.setString(4,rateType);*/
          pstmt1.setString(2,chargeDescId);
          pstmt1.setString(3,terminalId);
          rs1 = pstmt1.executeQuery();
          if(rs1.next())
          {
          //BUYSELLCHARGEID,CHARGE_ID,CHARGE_DESCRIPTION,CHARGEBASIS,BASIS_DESCRIPTION,RATE_BREAK,RATE_TYPE,CURRENCY,WEIGHT_CLASS 
            buySellChargeId   = Double.parseDouble(rs1.getString("SELLCHARGEID"));
            buychargesHDRDOB  = new BuychargesHDRDOB();
            buychargesHDRDOB.setBuychargeId(rs1.getDouble("BUYCHARGEID"));//Modified
            buychargesHDRDOB.setBuySellChargeId(buySellChargeId);
            buychargesHDRDOB.setChargeId(rs1.getString("CHARGE_ID"));
            buychargesHDRDOB.setChargeDescId(rs1.getString("CHARGEDESCID"));
            buychargesHDRDOB.setChargeBasisId(rs1.getString("CHARGEBASIS"));
            buychargesHDRDOB.setChargeBasisDesc(rs1.getString("BASIS_DESCRIPTION"));
            buychargesHDRDOB.setCurrencyId(rs1.getString("CURRENCY"));
            buychargesHDRDOB.setRateBreak(rs1.getString("RATE_BREAK"));
            buychargesHDRDOB.setRateType(rs1.getString("RATE_TYPE"));
            buychargesHDRDOB.setWeightClass(rs1.getString("WEIGHT_CLASS"));
            buychargesHDRDOB.setOverallMargin(rs1.getString("OVERALL_MARGIN"));
            buychargesHDRDOB.setMarginType(rs1.getString("MARGIN_TYPE"));
            buychargesHDRDOB.setMarginBasis(rs1.getString("MARGIN_BASIS"));
             if(!("Accept".equalsIgnoreCase(operation)))
            {
                buychargesHDRDOB.setDummyBuychargesFlag(rs1.getString("DUMMY_SELL_CHARGES_FLAG"));
            }
            if(buySellChargeId>0)
            {
              //SELECT BUYSELLCHAEGEID,BASE,MIN,MAX,FLAT,CHARGERATE,CHARGESLAB,"
                //+"LOWERBOUND,UPPERBOUND,CHARGERATE_INDICATOR FROM QMS_BUYCHARGESDTL WHERE BUYSELLCHAEGEID=? ORDER BY LANE_NO";            
              pstmt2.setDouble(1,buySellChargeId);
              rs2 = pstmt2.executeQuery();
              dtlList   = new ArrayList();
              while(rs2.next())
              {
                buychargesDtlDOB  = new BuychargesDtlDOB();
                buychargesDtlDOB.setChargeSlab((rs2.getString("CHARGESLAB")!=null)?rs2.getString("CHARGESLAB"):"");
                buychargesDtlDOB.setChargeRate(rs2.getDouble("CHARGERATE"));
                buychargesDtlDOB.setMarginValue(rs2.getString("MARGINVALUE"));
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
          }
        }else
        {
          throw new ObjectNotFoundException("Object Not Found----->");
        }
      }catch(SQLException e)
      {
        //Logger.error(FILE_NAME,"SQLException in loadSellChargesMasterDOB(ArrayList param0) method"+e.toString());
        logger.error(FILE_NAME+"SQLException in loadSellChargesMasterDOB(ArrayList param0) method"+e.toString());
        throw new EJBException();        
      }catch(Exception e)
      {
        //Logger.error(FILE_NAME,"Exception in loadSellChargesMasterDOB(ArrayList param0) method"+e.toString());
        logger.error(FILE_NAME+"Exception in loadSellChargesMasterDOB(ArrayList param0) method"+e.toString());
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
          //Logger.error(FILE_NAME,"SQLException in loadSellChargesMasterDOB(ArrayList param0) method"+e.toString());
          logger.error(FILE_NAME+"SQLException in loadSellChargesMasterDOB(ArrayList param0) method"+e.toString());
          throw new EJBException();
        }
      }
      return buychargesHDRDOB;      
    }
  /**
   * 
   * @throws java.sql.SQLException
   * @throws javax.ejb.EJBException
   * @param operation
   * @param buychargesHDRDOB
   */
    private void insertSellChargeDetails(BuychargesHDRDOB buychargesHDRDOB,String operation)throws EJBException,SQLException
    {
      PreparedStatement pstmt1               = null;
      PreparedStatement pstmt2               = null;
      //BuychargesHDRDOB  buychargesHDRDOB     = null;
      BuychargesDtlDOB  buychargesDtlDOB     = null;
      int dataListSize                       = 0;
      int dtlListSize                        = 0;
      int hdrInserted                        = 0;
      ArrayList         dtlList              = null;
      int               index                = 0;
      String            rateBreak            = "";
      String            chargeSb             = "";
      try
      {
      //"SELLCHARGEID,CHARGE_ID,CHARGEBASIS,RATE_BREAK,RATE_TYPE,CURRENCY,WEIGHT_CLASS,"
      //+"OVERALL_MARGIN,MARGIN_TYPE,MARGIN_BASIS,CREATED_BY,CREATED_TSTMP,LAST_UPDATED_BY,LAST_UPDATED_TSTMP,IE_FLAG,ACCESSLEVEL,TERMINALID,DUMMY_SELL_CHARGES_FLAG)"
      
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
          pstmt1.clearParameters();
          if(buychargesHDRDOB!=null) 
          {
            pstmt1.setDouble(1,buychargesHDRDOB.getBuySellChargeId());
            pstmt1.setString(2,buychargesHDRDOB.getChargeId());
            pstmt1.setString(3,buychargesHDRDOB.getChargeBasisId());
            pstmt1.setString(4,buychargesHDRDOB.getRateBreak());
            pstmt1.setString(5,buychargesHDRDOB.getRateType());
            pstmt1.setString(6,buychargesHDRDOB.getCurrencyId());
            pstmt1.setString(7,buychargesHDRDOB.getWeightClass());
            pstmt1.setString(8,buychargesHDRDOB.getOverallMargin());
            pstmt1.setString(9,buychargesHDRDOB.getMarginType());
            pstmt1.setString(10,buychargesHDRDOB.getMarginBasis());
            if("modify".equals(operation))
            {
              pstmt1.setNull(11,Types.VARCHAR);
              pstmt1.setNull(12,Types.DATE);
              pstmt1.setString(13,buychargesHDRDOB.getUserId());
              pstmt1.setTimestamp(14,buychargesHDRDOB.getCreate_Time());
            }else
            {
              pstmt1.setString(11,buychargesHDRDOB.getUserId());
              pstmt1.setTimestamp(12,buychargesHDRDOB.getCreate_Time());
              pstmt1.setNull(13,Types.VARCHAR);
              pstmt1.setNull(14,Types.DATE);
            }
            pstmt1.setString(15,"A");
            pstmt1.setString(16,buychargesHDRDOB.getAccessLevel());
            pstmt1.setString(17,buychargesHDRDOB.getTerminalId());
            pstmt1.setString(18,buychargesHDRDOB.getDummyBuychargesFlag());
            pstmt1.setString(19,buychargesHDRDOB.getChargeDescId());
            pstmt1.setDouble(20,buychargesHDRDOB.getBuychargeId());
            
            hdrInserted = pstmt1.executeUpdate();
            rateBreak  = buychargesHDRDOB.getRateBreak();
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
                      pstmt2.setString(2,buychargesDtlDOB.getChargeSlab());
                      pstmt2.setDouble(3,buychargesDtlDOB.getChargeRate());
                      pstmt2.setString(4,buychargesDtlDOB.getMarginValue());
                      pstmt2.setDouble(5,0);
                      pstmt2.setDouble(6,0);
                    }else if(chargeSb.equals("AbsRPersent"))
                    {
                      pstmt2.setString(2,rateBreak);
                      pstmt2.setDouble(3,buychargesDtlDOB.getChargeRate());
                      pstmt2.setString(4,buychargesDtlDOB.getMarginValue());
                      pstmt2.setDouble(5,0);
                      pstmt2.setDouble(6,0);
                    }
                    else
                    {
                      pstmt2.setString(2,buychargesDtlDOB.getChargeSlab());
                      pstmt2.setDouble(3,buychargesDtlDOB.getChargeRate());
                      pstmt2.setString(4,buychargesDtlDOB.getMarginValue());
                      pstmt2.setDouble(5,buychargesDtlDOB.getLowerBound());
                      pstmt2.setDouble(6,buychargesDtlDOB.getUpperBound());
                    }
                  pstmt2.setString(7,(buychargesDtlDOB.getChargeRate_indicator()!=null)?buychargesDtlDOB.getChargeRate_indicator():"");
                  pstmt2.setInt(8,index+1);
                  //pstmt2.executeUpdate();
                  pstmt2.addBatch();// added by VLAKSHMI for memmory leakage 
                }
                pstmt2.executeBatch();
              }
            }else
            {
              throw new Exception();
            }
          }
        }
        
      }catch(SQLException e)
      {
        //Logger.error(FILE_NAME,"SQLException in insertSellChargeDetails(ArrayList param0) method"+e.toString());
        logger.error(FILE_NAME+"SQLException in insertSellChargeDetails(ArrayList param0) method"+e.toString());
        throw new EJBException();        
      }catch(Exception e)
      {
        //Logger.error(FILE_NAME,"Exception in insertSellChargeDetails(ArrayList param0) method"+e.toString());
        logger.error(FILE_NAME+"Exception in insertSellChargeDetails(ArrayList param0) method"+e.toString());
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
          //Logger.error(FILE_NAME,"SQLException in insertSellChargeDetails(ArrayList param0) method"+e.toString());
          logger.error(FILE_NAME+"SQLException in insertSellChargeDetails(ArrayList param0) method"+e.toString());
          throw new EJBException();
        }
      }      
    }
  /**
   * 
   * @throws javax.ejb.EJBException
   * @throws java.sql.SQLException
   * @return 
   * @param pkObj
   */
    private boolean updateDeleteFlag(SellChargesEntityBeanPK pkObj)throws SQLException,EJBException
    {
      PreparedStatement pstmt1               = null;
      int               deleted              = 0;
      try
      {
        pstmt1   = connection.prepareStatement(updateQry); 
        pstmt1.setString(1,pkObj.chargeId);
        /*pstmt1.setString(2,pkObj.chargeBasisId);
        pstmt1.setString(3,pkObj.rateBreak);
        pstmt1.setString(4,pkObj.rateType);*/
        pstmt1.setString(2,pkObj.chargeDescId);
        pstmt1.setString(3,pkObj.terminalId);
        deleted = pstmt1.executeUpdate();
        if(deleted>0)
          return true;
        else
          return false;
      }catch(SQLException e)
      {
        //Logger.error(FILE_NAME,"SQLException in updateDeleteFlag(SellChargesEntityBeanPK param0) method"+e.toString());
        logger.error(FILE_NAME+"SQLException in updateDeleteFlag(SellChargesEntityBeanPK param0) method"+e.toString());
        throw new EJBException();        
      }catch(Exception e)
      {
        //Logger.error(FILE_NAME,"Exception in updateDeleteFlag(SellChargesEntityBeanPK param0) method"+e.toString());
        logger.error(FILE_NAME+"Exception in updateDeleteFlag(SellChargesEntityBeanPK param0) method"+e.toString());
        throw new EJBException();
      }finally
      {
        try
        {
          if(pstmt1!=null)
            { pstmt1.close();}
        }catch(SQLException e)
        {
          //Logger.error(FILE_NAME,"SQLException in updateDeleteFlag(SellChargesEntityBeanPK param0) method"+e.toString());
          logger.error(FILE_NAME+"SQLException in updateDeleteFlag(SellChargesEntityBeanPK param0) method"+e.toString());
          throw new EJBException();
        }
      }
    }
    public ArrayList getUpdatedBuyCharges(QMSCartageMasterDOB masterDOB)throws SQLException
    {
      QMSCartageMasterDOB  dob         = null;
      ArrayList         list           = new ArrayList();      
      ArrayList         slabList       = new ArrayList();      
      
      ResultSet         rs             = null;      
      CallableStatement cstmt          = null;
      PreparedStatement pStmt          = null;
      
      String         terminalId        = masterDOB.getTerminalId();
      String         accessType        = masterDOB.getAccessLevel();
      String         chargeType        = masterDOB.getChargeType();
      String         locationId        = masterDOB.getLocationId();
      String         shipmentMode      = masterDOB.getShipmentMode();
      String         consoleType       = masterDOB.getConsoleType();
      
      String         branchTerminals   = "";
      String         locationQry       = "";
      String         chargeTypeQry     = "";
      String[]       tempLocation      = null;
      String         tempLocationId    = "";
      
      locationId  = locationId+",";
      
      if(locationId!=null)
      {
         tempLocation  = locationId.split(",");
         int tempLocLen	=	tempLocation.length;
         for(int i=0;i<tempLocLen;i++)
            tempLocationId =tempLocationId+"'"+tempLocation[i]+"',";       
         locationQry  = "AND LOCATION_ID IN ("+tempLocationId.substring(0,tempLocationId.length()-1)+")";
      }
      
      if(!"Both".equalsIgnoreCase(chargeType))
         chargeTypeQry ="AND CHARGE_TYPE='"+chargeType+"'";
       
      if("HO_TERMINAL".equalsIgnoreCase(accessType))
         branchTerminals    = " SELECT DISTINCT terminalid term_id"+
                              " FROM FS_FR_TERMINALMASTER ";
      else  
         branchTerminals   =             " SELECT child_terminal_id term_id"+
                                         " FROM FS_FR_TERMINAL_REGN"+
                                         " CONNECT BY PRIOR child_terminal_id = parent_terminal_id"+
                                         " START WITH parent_terminal_id = '"+terminalId+"'  UNION "+
                                         " SELECT '"+terminalId+"' term_id FROM DUAL ";
                                         
       String            sql            = " SELECT DISTINCT(MAS.CARTAGE_ID) CARTAGE_ID,LOCATION_ID,WEIGHT_BREAK ,RATE_TYPE ,SELL.EFFECTIVE_FROM,SELL.VALID_UPTO "+
                                         " ,MAS.CURRENCY CURRENCY ,SELL.CHARGE_BASIS,SELL.ZONE_CODE,SELL.CHARGE_TYPE ,TERMINALID  "+
                                         " FROM QMS_CARTAGE_BUYSELLCHARGES_A MAS,QMS_CARTAGE_SELLDTL_A SELL "+
                                         " WHERE MAS.CARTAGE_ID=SELL.CARTAGE_ID "+chargeTypeQry+" "+locationQry+" AND MAS.SHIPMENT_MODE=? AND NVL(MAS.CONSOLE_TYPE,'~')=? "+
                                         " AND TERMINALID IN ("+branchTerminals+") ORDER BY CARTAGE_ID,ZONE_CODE";
                                         
      try
      {
        list   =  new ArrayList();
        getConnection();       
        
        pStmt  = connection.prepareStatement(sql);
        pStmt.setString(1,shipmentMode);
        if("1".equalsIgnoreCase(shipmentMode))
            pStmt.setString(2,"~");
        else
            pStmt.setString(2,consoleType);
        rs     = pStmt.executeQuery();
        while(rs.next())
        {           
          dob      =  new QMSCartageMasterDOB();
          dob.setLocationId(rs.getString("LOCATION_ID"));
          dob.setTerminalId(rs.getString("TERMINALID"));
          dob.setZoneCode(rs.getString("ZONE_CODE"));
          dob.setChargeType(rs.getString("CHARGE_TYPE"));  
          dob.setCartageId(rs.getLong("CARTAGE_ID"));  
          dob.setWeightBreak(rs.getString("WEIGHT_BREAK"));  
          dob.setRateType(rs.getString("RATE_TYPE"));  
          dob.setCurrencyId(rs.getString("CURRENCY"));
          dob.setChargeBasis(rs.getString("CHARGE_BASIS"));  
          //dob.setUom(rs.getString("UOM"));  
          dob.setEffectiveFrom(rs.getTimestamp("EFFECTIVE_FROM"));  
          dob.setValidUpto(rs.getTimestamp("VALID_UPTO"));
          dob.setShipmentMode(masterDOB.getShipmentMode());
          dob.setConsoleType(masterDOB.getConsoleType());
          list.add(dob);
        }
      }
      catch(SQLException e)
      {
        e.printStackTrace();
        //Logger.error(FILE_NAME,"Error while getting the details"+e.toString());
        logger.error(FILE_NAME+"Error while getting the details"+e.toString());
        throw new SQLException("Error while getting the details (SQLException)");
      }
      catch(Exception e)
      {
        e.printStackTrace();
        //Logger.error(FILE_NAME,"Error while getting the details"+e.toString());
        logger.error(FILE_NAME+"Error while getting the details"+e.toString());
        throw new EJBException("Error while getting the details");
      }
      finally
      {
        ConnectionUtil.closeConnection(connection,pStmt,rs);
      }
      return list;
    }
    //For Slab
    public QMSCartageSellDtlDOB getSellChargesDtls(String cartageId,String zoneCode,String chargeType,String chargeBasis,String unitofMeasure)throws SQLException
    {
      QMSCartageSellDtlDOB  dob        = null;
      ArrayList         list           = null;      
      ResultSet         rs             = null;      
      CallableStatement cstmt          = null;
      PreparedStatement pStmt          = null;
      HashMap           hMap           = null;
      String            key            = null;
      ArrayList         slabList       = new ArrayList(); 
      int               count          = 0;
      double            chargeRate     = 0;
      
      try
      {
        dob  =  new QMSCartageSellDtlDOB();
        list   =  new ArrayList();
        getConnection();
        pStmt  = connection.prepareStatement(sqlGetSlabValues);
        pStmt.setString(1,cartageId);
        pStmt.setString(2,zoneCode);
        pStmt.setString(3,chargeType);
        rs     = pStmt.executeQuery();
        slabList.add("BASE");//@@Added by subrahmanyam for the Enhancement 170759 on 02/06/09
        slabList.add("MIN");
        
        while(rs.next())
        {
            slabList.add(rs.getString(1));
        }
        slabList.add("MAX");
        
        if(pStmt!=null)
          pStmt.close();
        if(rs!=null)
          rs.close();
        
        dob.setSlabList(slabList); 
          
        pStmt  = connection.prepareStatement(sqlGetSlabDtls);
        pStmt.setString(1,cartageId);
        pStmt.setString(2,zoneCode);
        pStmt.setString(3,chargeType);
        rs     = pStmt.executeQuery();
        hMap           =  new HashMap();//@@Added by subrahmanyam for the Enhancement 170759 on 03/06/09
        for(int i=0;rs.next();i++)
        {
          key            =    rs.getString("CARTAGE_ID")+rs.getString("CHARGE_TYPE")+rs.getString("ZONE_CODE");          
          chargeRate = rs.getDouble("CHARGERATE");
//@@ if Condition Commented & Added  by subrahmanyam for the Enhancement 170759 on 03/06/09          
          //if(i!=0 && hMap.containsKey(key))
          if(i!=0 && hMap.containsKey(key) && i!=1)
          {
            ArrayList  values = (ArrayList)hMap.get(rs.getString("CHARGESLAB"));
            if(values==null)
                  hMap.put(rs.getString("CHARGESLAB"),values=new ArrayList());       
            values.add(0,Double.toString(chargeRate));
            values.add(1,""+rs.getDouble("MARGIN"));            
            if("MAX".equalsIgnoreCase(rs.getString("CHARGESLAB")))
                dob.setMaxMargin(rs.getDouble("MARGIN"));
            else if (dob.getMargin()==0)
                dob.setMargin(rs.getDouble("MARGIN"));
            hMap.put("LB"+rs.getString("CHARGESLAB"),rs.getString("LOWERBOUND"));
            hMap.put("UB"+rs.getString("CHARGESLAB"),rs.getString("UPPERBOUND"));
            hMap.put("LNO"+rs.getString("CHARGESLAB"),rs.getString("LINE_NO"));         
          }
          else
          {            
            dob.setCartageId(rs.getLong("CARTAGE_ID"));
            dob.setZoneCode(rs.getString("ZONE_CODE"));
            dob.setChargeType(rs.getString("CHARGE_TYPE"));  
            dob.setMarginType(rs.getString("MARGIN_TYPE"));
            dob.setOverallMargin(rs.getString("OVERALL_MARGIN"));
            dob.setChargeRate(rs.getDouble("CHARGERATE"));
            dob.setChargeSlab(rs.getString("CHARGESLAB"));  
            dob.setCartageId(rs.getLong("CARTAGE_ID")); 
            dob.setZoneCode(rs.getString("ZONE_CODE")); 
            //dob.setMargin(rs.getDouble("MARGIN"));
            dob.setChargeType(rs.getString("CHARGE_TYPE"));
            //hMap           =  new HashMap();//Commented by subrahmanyam for the Enhancement 170759 on 03/06/09
            
            ArrayList  values = (ArrayList)hMap.get(rs.getString("CHARGESLAB"));
            if(values==null)
                  hMap.put(rs.getString("CHARGESLAB"),values=new ArrayList());       
            values.add(0,Double.toString(chargeRate));
            values.add(1,""+rs.getDouble("MARGIN"));
//@@Added by subrahmanyam for the Enhancement 170759 on 02/06/09            
            if("BASE".equalsIgnoreCase(rs.getString("CHARGESLAB")))
                dob.setBaseMargin(rs.getDouble("MARGIN"));
//@@ Ended by subrahmanyam for the Enhancement 170759 on 02/06/*09                
            if("MIN".equalsIgnoreCase(rs.getString("CHARGESLAB")))
                dob.setMinMargin(rs.getDouble("MARGIN"));
            hMap.put("LB"+rs.getString("CHARGESLAB"),rs.getString("LOWERBOUND"));
            hMap.put("UB"+rs.getString("CHARGESLAB"),rs.getString("UPPERBOUND"));
            hMap.put("LNO"+rs.getString("CHARGESLAB"),rs.getString("LINE_NO"));
            hMap.put(key,"");
          }
        }
        dob.setSlabRates(hMap);
      }
      catch(SQLException e)
      {
        e.printStackTrace();
        logger.error(FILE_NAME+"Error while getting the details"+e.toString());
        throw new SQLException("Error while getting the details (SQLException)");
      }
      catch(Exception e)
      {
        e.printStackTrace();
        logger.error(FILE_NAME+"Error while getting the details"+e.toString());
        throw new EJBException("Error while getting the details");
      }
      finally
      {
        ConnectionUtil.closeConnection(connection,pStmt,rs);
      }
      return dob;
    }
   //For Flat 
    public QMSCartageSellDtlDOB getSellChargesDtls(String cartageId,String zoneCode,String chargeType)throws SQLException
    {
      QMSCartageSellDtlDOB  dob        = null;
      ArrayList         list           = null;      
      ResultSet         rs             = null;      
      CallableStatement cstmt          = null;
      PreparedStatement pStmt          = null;
      int               count          = 0;
      double            chargeRate     = 0;
      
      
      try
      {
        list   =  new ArrayList();
        getConnection();
        pStmt  = connection.prepareStatement(sqlGetSellAccDtls);
        pStmt.setString(1,cartageId);
        pStmt.setString(2,zoneCode);
        pStmt.setString(3,chargeType);
        rs     = pStmt.executeQuery();
        while(rs.next())
        {         
          //chargeRate  = rs.getDouble("CHARGERATE");
          if(count == 0)
          {
            dob      =  new QMSCartageSellDtlDOB();
            dob.setMarginType(rs.getString("MARGIN_TYPE"));
            dob.setOverallMargin(rs.getString("OVERALL_MARGIN"));
            dob.setChargeRate(rs.getDouble("CHARGERATE"));
            dob.setChargeSlab(rs.getString("CHARGESLAB"));  
            dob.setCartageId(rs.getLong("CARTAGE_ID")); 
            dob.setZoneCode(rs.getString("ZONE_CODE"));             
            dob.setChargeType(rs.getString("CHARGE_TYPE"));
            
            if("Yes".equalsIgnoreCase(dob.getOverallMargin()) && "P".equalsIgnoreCase(dob.getMarginType()))
                dob.setMargin(rs.getDouble("MARGIN")); 
          }
          if("MIN".equalsIgnoreCase(rs.getString("CHARGESLAB")))
          {
              dob.setMinRate((rs.getDouble("CHARGERATE")));
              dob.setMinMargin(rs.getDouble("MARGIN")); 
          }
          else if("FLAT".equalsIgnoreCase(rs.getString("CHARGESLAB")))
          {
              dob.setFlatRate((rs.getDouble("CHARGERATE")));
              dob.setFlatMargin(rs.getDouble("MARGIN")); 
          }
          else if("MAX".equalsIgnoreCase(rs.getString("CHARGESLAB")))
          {
              dob.setMaxRate((rs.getDouble("CHARGERATE")));   
              dob.setMaxMargin(rs.getDouble("MARGIN")); 
          }
//@@Added by subrahmanyam for the Enhancement 170759 on 02/06/09          
          else if("BASE".equalsIgnoreCase(rs.getString("CHARGESLAB")))
          {
              dob.setBaseRate((rs.getDouble("CHARGERATE")));   
              dob.setBaseMargin(rs.getDouble("MARGIN")); 
          }
//@@ Ended by subrahmanyam for 170759          
          //list.add(dob);
          count++;
        }
      }
      catch(SQLException e)
      {
        e.printStackTrace();
        //Logger.error(FILE_NAME,"Error while getting the details"+e.toString());
        logger.error(FILE_NAME+"Error while getting the details"+e.toString());
        throw new SQLException("Error while getting the details (SQLException)");
      }
      catch(Exception e)
      {
        e.printStackTrace();
        //Logger.error(FILE_NAME,"Error while getting the details"+e.toString());
        logger.error(FILE_NAME+"Error while getting the details"+e.toString());
        throw new EJBException("Error while getting the details");
      }
      finally
      {
        ConnectionUtil.closeConnection(connection,pStmt,rs);
      }
      return dob;
    }
    public QMSCartageSellDtlDOB getListUpdatedCharges(String cartageId,String zoneCode,String chargeType)throws SQLException
    {
      QMSCartageSellDtlDOB  dob        = null;
      ArrayList         list           = null;      
      ResultSet         rs             = null;      
      CallableStatement cstmt          = null;
      PreparedStatement pStmt          = null;
      HashMap           hMap           = null;
      String            key            = null;
      ArrayList         slabList       = new ArrayList(); 
      int               count          = 0;
      double            chargeRate     = 0;
      
      try
      {
        dob  =  new QMSCartageSellDtlDOB();
        list   =  new ArrayList();
        getConnection();
        pStmt  = connection.prepareStatement(chargeSlabQry);
        pStmt.setString(1,cartageId);
        pStmt.setString(2,zoneCode);
        pStmt.setString(3,chargeType);
        rs     = pStmt.executeQuery();
        
        while(rs.next())
        {
            slabList.add(rs.getString(1));
        }
        
        if(pStmt!=null)
          pStmt.close();
        if(rs!=null)
          rs.close();
        
        dob.setSlabList(slabList); 
          
        pStmt  = connection.prepareStatement(sqlGetSlabDtls);
        pStmt.setString(1,cartageId);
        pStmt.setString(2,zoneCode);
        pStmt.setString(3,chargeType);
        rs     = pStmt.executeQuery();
        
        for(int i=0;rs.next();i++)
        {
          key            =    rs.getString("CARTAGE_ID")+rs.getString("CHARGE_TYPE")+rs.getString("ZONE_CODE");          
          chargeRate = rs.getDouble("CHARGERATE");
          
          if(i!=0 && hMap.containsKey(key))
          {
            ArrayList  values = (ArrayList)hMap.get(rs.getString("CHARGESLAB"));
            if(values==null)
                  hMap.put(rs.getString("CHARGESLAB"),values=new ArrayList());       
            values.add(0,Double.toString(chargeRate));
            values.add(1,""+rs.getDouble("MARGIN"));
          }
          else
          {            
            dob.setCartageId(rs.getLong("CARTAGE_ID"));
            dob.setZoneCode(rs.getString("ZONE_CODE"));
            dob.setChargeType(rs.getString("CHARGE_TYPE"));  
            dob.setMarginType(rs.getString("MARGIN_TYPE"));
            dob.setOverallMargin(rs.getString("OVERALL_MARGIN"));
            dob.setChargeRate(rs.getDouble("CHARGERATE"));
            dob.setChargeSlab(rs.getString("CHARGESLAB"));  
            dob.setCartageId(rs.getLong("CARTAGE_ID")); 
            dob.setZoneCode(rs.getString("ZONE_CODE")); 
            dob.setMargin(rs.getDouble("MARGIN"));
            dob.setChargeType(rs.getString("CHARGE_TYPE"));
            hMap           =  new HashMap();
            
            ArrayList  values = (ArrayList)hMap.get(rs.getString("CHARGESLAB"));
            if(values==null)
                  hMap.put(rs.getString("CHARGESLAB"),values=new ArrayList());       
            values.add(0,Double.toString(chargeRate));
            values.add(1,""+rs.getDouble("MARGIN"));
            hMap.put(key,"");
          }
        }
        dob.setSlabRates(hMap);
      }
      catch(SQLException e)
      {
        e.printStackTrace();
        logger.error(FILE_NAME+"Error while getting the details"+e.toString());
        throw new SQLException("Error while getting the details (SQLException)");
      }
      catch(Exception e)
      {
        e.printStackTrace();
        logger.error(FILE_NAME+"Error while getting the details"+e.toString());
        throw new EJBException("Error while getting the details");
      }
      finally
      {
        ConnectionUtil.closeConnection(connection,pStmt,rs);
      }
      return dob;
    }
    public void updateSellDtls(ArrayList list)
    {
      QMSCartageSellDtlDOB  dob         = null;
      //ResultSet         rs             = null;   //Commented By RajKumari on 24-10-2008 for Connection Leakages.   
      CallableStatement cstmt          = null;
      PreparedStatement pStmt          = null;
      int               count          =  0;
      double            chargeRate     = 0;
      
      try
      {
        getConnection();
        pStmt   =  connection.prepareStatement(sqlUpdateSellAcceptModify);
        int listSize	=	list.size();
        for(int i=0;i<listSize;i++)
        {
              dob       = (QMSCartageSellDtlDOB)list.get(i);
              
              pStmt.clearParameters();
            
              pStmt.setDouble(1,dob.getChargeRate());              
              pStmt.setDouble(2,dob.getMargin());
              pStmt.setString(3,dob.getOverallMargin());
              pStmt.setString(4,dob.getMarginType());
              pStmt.setLong(5,dob.getCartageId());
              pStmt.setString(6,dob.getZoneCode());
              pStmt.setString(7,dob.getChargeType());
              pStmt.setInt(8,i);
              pStmt.addBatch();
              //pStmt.executeUpdate();
        }
        pStmt.executeBatch();
      }
       catch(Exception e)
       {
         e.printStackTrace();
         //Logger.error(FILE_NAME,"Error while updating details"+e.toString());
         logger.error(FILE_NAME+"Error while updating details"+e.toString());
         throw new EJBException("Error while updating details");
       }finally
      {
        ConnectionUtil.closeConnection(connection,pStmt,null);//Modified By RajKumari on 24-10-2008 for Connection Leakages.
      }
    }
    public void insertNewCartageSellDtl(ArrayList list)throws SQLException
    {
      PreparedStatement pStmt = null;   
      PreparedStatement pStmt1 = null;  
      PreparedStatement pStmt2 = null;  
      PreparedStatement pStmt3 = null;  
      PreparedStatement pStmt4 = null;
      PreparedStatement pStmt5 = null;
      //CallableStatement csmt   = null;
      ResultSet         rs     = null;
      
      long              oldId  = 0;
      long              newId  = 0;
      
      OperationsImpl   operationsImpl     = new OperationsImpl();
      QMSCartageMasterDOB   dob           = null;
      String            insertQuery       = "INSERT INTO QMS_CARTAGE_SELLDTL(CARTAGE_ID,ZONE_CODE,OVERALL_MARGIN,MARGIN_TYPE,MARGIN_BASIS,MARGIN,CHARGERATE,"+
                                            "CHARGESLAB,LOWERBOUND,UPPERBOUND,CHARGE_TYPE,UOM,CURRENCY,BUYRATE_AMT,ACTIVEINACTIVE,ID,LINE_NO,SELL_CARTAGE_ID,EFFECTIVE_FROM,VALID_UPTO,CHARGE_BASIS,CHARGERATE_INDICATOR) "+
                                            "SELECT CARTAGE_ID,ZONE_CODE,OVERALL_MARGIN,MARGIN_TYPE,MARGIN_BASIS,MARGIN,CHARGERATE,"+
                                            "CHARGESLAB,LOWERBOUND,UPPERBOUND,CHARGE_TYPE,UOM,CURRENCY,BUYRATE_AMT,ACTIVEINACTIVE,ID,LINE_NO,?,EFFECTIVE_FROM,VALID_UPTO,CHARGE_BASIS,CHARGERATE_INDICATOR "+
                                            "FROM QMS_CARTAGE_SELLDTL_A WHERE CARTAGE_ID=? AND ZONE_CODE=? AND CHARGE_TYPE=?";
                                            
      //String           selectQuery        =  "SELECT DISTINCT CARTAGE_ID,OLD_SELL_ID FROM QMS_CARTAGE_SELLDTL_A WHERE CARTAGE_ID=? AND ZONE_CODE=? AND CHARGE_TYPE=?";
      String           selectQuery        =  " SELECT SELLCARTAGE_SEQ.NEXTVAL FROM DUAL";                                      
      String           updateBuyDtlQry    =  "UPDATE QMS_CARTAGE_BUYDTL  SET SELLCHARGE_FLAG = 'Y' WHERE  CARTAGE_ID =? AND ZONE_CODE=? AND CHARGE_TYPE=?";
      String           deleteQueryBuyDtl  =  "DELETE FROM QMS_CARTAGE_BUYDTL_A WHERE CARTAGE_ID=? AND ZONE_CODE=? AND CHARGE_TYPE=?";
      String           deleteQuerySellDtl =  "DELETE FROM QMS_CARTAGE_SELLDTL_A WHERE CARTAGE_ID=? AND ZONE_CODE=? AND CHARGE_TYPE=?";
      String           deleteQueryMaster  =  "DELETE FROM  qms_cartage_buysellcharges_a WHERE CARTAGE_ID not in (select cartage_id from qms_cartage_selldtl_a)  AND LOCATION_ID=? AND SHIPMENT_MODE=? AND NVL(CONSOLE_TYPE,'~')=?";
      try
      {
        getConnection();  
        pStmt  = connection.prepareStatement(insertQuery);
        pStmt1 = connection.prepareStatement(updateBuyDtlQry);
        pStmt2 = connection.prepareStatement(deleteQueryBuyDtl);
        pStmt3 = connection.prepareStatement(deleteQuerySellDtl);
        pStmt4 = connection.prepareStatement(deleteQueryMaster);
        pStmt5 = connection.prepareStatement(selectQuery);
        
        rs = pStmt5.executeQuery();
          
        if(rs.next())
          newId = rs.getLong(1);
        
        if(rs!=null)
          rs.close();
        
        //csmt  =   connection.prepareCall("{call QMS_QUOTEPACK_NEW.qms_quote_update(?,?,?,?,?,?,?,?,?,?)}");
        int listSize	=	list.size();
        for(int i=0;i<listSize;i++)
        {
          dob  = (QMSCartageMasterDOB)list.get(i);
          inactiveCartageSellCharge(dob.getZoneCode(),connection,dob.getChargeType(),dob,newId,dob.getCartageId());
          pStmt.clearParameters();
          pStmt.setLong(1,newId);
          pStmt.setLong(2,dob.getCartageId());
          pStmt.setString(3,dob.getZoneCode());
          pStmt.setString(4,dob.getChargeType());
          pStmt.executeUpdate();
          
          /*pStmt5.clearParameters();
          pStmt5.setLong(1,dob.getCartageId());
          pStmt5.setString(2,dob.getZoneCode());
          pStmt5.setString(3,dob.getChargeType());*/
          
          /*csmt.setString(1,""+oldId);
          csmt.setString(2,""+oldId);
          csmt.setNull(3,Types.NULL);
          csmt.setString(4,""+newId);
          csmt.setString(5,""+newId);
          csmt.setNull(6,Types.NULL);
          csmt.setString(7,"SC");
          csmt.setString(8,dob.getChargeType());
          csmt.setString(9,dob.getZoneCode());
          csmt.setString(10,dob.getLocationId());
          
          csmt.addBatch(); */        
          
          pStmt1.clearParameters();
          pStmt1.setLong(1,dob.getCartageId());
          pStmt1.setString(2,dob.getZoneCode());
          pStmt1.setString(3,dob.getChargeType());
          pStmt1.executeUpdate();
          
          pStmt2.clearParameters();
          pStmt2.setLong(1,dob.getCartageId());
          pStmt2.setString(2,dob.getZoneCode());
          pStmt2.setString(3,dob.getChargeType());
          pStmt2.executeUpdate();
          
          pStmt3.clearParameters();
          pStmt3.setLong(1,dob.getCartageId());
          pStmt3.setString(2,dob.getZoneCode());
          pStmt3.setString(3,dob.getChargeType());
          pStmt3.executeUpdate();
          
          pStmt4.clearParameters();          
          pStmt4.setString(1,dob.getLocationId());
          pStmt4.setString(2,dob.getShipmentMode());
          if("1".equalsIgnoreCase(dob.getShipmentMode()))
              pStmt4.setString(3,"~");
          else
              pStmt4.setString(3,dob.getConsoleType());
          pStmt4.executeUpdate();
          //pStmt.addBatch();    
          operationsImpl.setTransactionDetails(dob.getTerminalId(),dob.getCreatedBy(),
                                            "Cartage Sell Charges",Double.toString(dob.getCartageId()),
                                            dob.getCreatedTimestamp(),"Cartage Accept");
        }        
        //pStmt.executeBatch();
        //csmt.executeBatch();
        
        
      }
      catch(SQLException e)
      {
        e.printStackTrace();
        //Logger.error(FILE_NAME,"Error while inserting details"+e.toString());
        logger.error(FILE_NAME+"Error while inserting details"+e.toString());
        throw new EJBException(e);
        
      }
      catch(Exception e)
      {
        e.printStackTrace();
        logger.error(FILE_NAME+"Error while inserting details"+e.toString());
        throw new EJBException(e);
        //Logger.error(FILE_NAME,"Error while inserting details"+e.toString());
        
      }finally
      {
        ConnectionUtil.closeConnection(connection,pStmt,rs);
        if(pStmt5!=null)
          pStmt5.close();
        ConnectionUtil.closeConnection(null,pStmt1);
        ConnectionUtil.closeConnection(null,pStmt2);
        ConnectionUtil.closeConnection(null,pStmt3);
        ConnectionUtil.closeConnection(null,pStmt4);
      }
    }
    
  public void insertCartageSellCharges(ArrayList cartageSellChargesList) throws EJBException
  {
    PreparedStatement pstmt           = null;
    PreparedStatement pstmt1          = null;
    PreparedStatement pstmt2          = null;
    ResultSet         rs              = null;
    //Connection        connection      = null;
    CallableStatement cstmt           = null;
    QMSCartageSellDtlDOB cartageSellDtl = null;
    QMSCartageMasterDOB   masterDOB   = null;
    long              sellCartageId   = 0L;
    int               listSize        = 0;
       
    String result       = "";
    
    try
    {
     this.getConnection();     
     //cstmt       =   connection.prepareCall("{call PKG_QMS_CARTAGE_ACC.cartage_check_for_sellcharges(?,?,?,?,?,?)}");
     pstmt       =   connection.prepareStatement(insertSellDtlQuery);
     pstmt1      =   connection.prepareStatement(updateBuyDtlQry);
     pstmt2      =   connection.prepareStatement("SELECT SELLCARTAGE_SEQ.NEXTVAL FROM DUAL ");
     
     rs          =   pstmt2.executeQuery();
     
     if(rs.next())
        sellCartageId = rs.getLong(1);
     
     if(cartageSellChargesList!=null)
        listSize   =  cartageSellChargesList.size();
    
    if(listSize > 0)
    {
        listSize    =    listSize-1;
        masterDOB   =   (QMSCartageMasterDOB)cartageSellChargesList.get(listSize);
    }
     
     for(int i=0;i<listSize;i++)
     {
       cartageSellDtl   = (QMSCartageSellDtlDOB)cartageSellChargesList.get(i);  
       
       if(cartageSellDtl.getLineNumber() == 0) //@@For updating & calling the procedure only once per record.
       {
         pstmt1.clearParameters();
         pstmt1.setLong(1,cartageSellDtl.getCartageId());
         pstmt1.setString(2,cartageSellDtl.getZoneCode());
         pstmt1.setString(3,cartageSellDtl.getChargeType());
         pstmt1.executeUpdate();
         
         /*if("sellAdd".equalsIgnoreCase(cartageSellDtl.getOperation()))
         {
          cstmt.clearParameters();
          cstmt.setString(1,cartageSellDtl.getOperation());
          cstmt.setString(2,cartageSellDtl.getTerminalId());
          cstmt.setString(3,cartageSellDtl.getChargeType());
          cstmt.setString(4,cartageSellDtl.getLocationId());
          cstmt.setString(5,cartageSellDtl.getZoneCode());
          cstmt.registerOutParameter(6,Types.VARCHAR);
          cstmt.execute();          
          result  = (String)cstmt.getObject(6); 
          System.out.println("result  "+result);
          if(result.equals("4") || result.equals("0")|| result.equals("2"))
            inactiveCartageSellCharge(cartageSellDtl.getZoneCode(),connection,cartageSellDtl.getChargeType(),cartageSellDtl.getLocationId());
          if(result.equals("3"))
            break;
         }
         else
         {*/
           inactiveCartageSellCharge(cartageSellDtl.getZoneCode(),connection,cartageSellDtl.getChargeType(),masterDOB,sellCartageId,cartageSellDtl.getCartageId());
         //}
       }
          
       pstmt.setLong(1,cartageSellDtl.getCartageId());
       pstmt.setString(2,cartageSellDtl.getZoneCode());
       pstmt.setString(3,cartageSellDtl.getOverallMargin());
       pstmt.setString(4,cartageSellDtl.getMarginType());
       pstmt.setString(5,"");
       pstmt.setDouble(6,cartageSellDtl.getMargin());
       pstmt.setDouble(7,cartageSellDtl.getChargeRate());
       pstmt.setString(8,cartageSellDtl.getChargeSlab());
       pstmt.setString(9,cartageSellDtl.getLowerBound());
       pstmt.setString(10,cartageSellDtl.getUpperBound());
       pstmt.setString(11,cartageSellDtl.getChargeType());
       //pstmt.setString(12,cartageSellDtl.getUom());
       pstmt.setString(12,cartageSellDtl.getCurrencyId());
       pstmt.setDouble(13,cartageSellDtl.getBuyChargeAmount());   
       pstmt.setInt(14,cartageSellDtl.getLineNumber());
       pstmt.setLong(15,sellCartageId);
       pstmt.setString(16,cartageSellDtl.getChargeBasis());
       pstmt.setTimestamp(17,cartageSellDtl.getEffectiveFrom());
       pstmt.setTimestamp(18,cartageSellDtl.getValidUpto());
       pstmt.setString(19,cartageSellDtl.getChargeRateIndicator());
       pstmt.addBatch();
       
       if(rs!=null)
        rs.close();
     }
     
     pstmt.executeBatch();
    }
    catch(Exception e)
    {
      //Logger.error(FILE_NAME,"error in insertCartageSellCharges    ");
      logger.error(FILE_NAME+"error in insertCartageSellCharges    ");
      e.printStackTrace();
      throw new EJBException(e);
    }
    finally
    {
      ConnectionUtil.closeStatement(pstmt,rs);
      ConnectionUtil.closeConnection(connection,pstmt1);
      ConnectionUtil.closePreparedStatement(pstmt2);//Added By RajKumari on 24-10-2008 for Connection Leakages.
    }
  }
  
   private void inactiveCartageSellCharge(String zoneCode,Connection connection,String chargeType,QMSCartageMasterDOB masterDOB,long cartageId, long newBuyCartageId)throws SQLException
  {
    CallableStatement cstmt       =  null;
    
    
    try
    { 
          
      cstmt  = connection.prepareCall("{call PKG_QMS_CARTAGE_ACC.sell_cartage_update_quote(?,?,?,?,?,?,?,?,?)}");
      
      cstmt.setString(1,"");
      cstmt.setString(2,chargeType);
      cstmt.setString(3,masterDOB.getLocationId());
      cstmt.setString(4,zoneCode);
      cstmt.setString(5,masterDOB.getShipmentMode());
      if("1".equalsIgnoreCase(masterDOB.getShipmentMode()))          
          cstmt.setNull(6,Types.VARCHAR);
      else
          cstmt.setString(6,masterDOB.getConsoleType());
      cstmt.setString(7,""+cartageId);
      cstmt.setString(8,""+newBuyCartageId);
      cstmt.registerOutParameter(9,Types.VARCHAR);
      
      cstmt.execute();
     /* pStmt   =  connection.prepareStatement(updateDtlQry);
      pStmt.setString(1,zoneCode);
      pStmt.setString(2,chargeType);
      pStmt.setString(3,locationId);
      pStmt.setString(4,zoneCode);
      pStmt.setString(5,chargeType);      
      pStmt.executeUpdate();
      if(pStmt!=null)pStmt.close();*/
      
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
      if(cstmt!=null)cstmt.close();
    }
  }
  
    private void insertBuyChargeDetails(BuychargesHDRDOB buychargesHDRDOB)throws EJBException,SQLException
    {
      PreparedStatement pstmt1               = null;
      PreparedStatement pstmt2               = null;
      PreparedStatement pstmt3               = null;
      //ResultSet         rs                   =  null;//Commented By RajKumari on 24-10-2008 for Connection Leakages.
      //BuychargesHDRDOB  buychargesHDRDOB     = null;
      BuychargesDtlDOB  buychargesDtlDOB     = null;
      int dataListSize                       = 0;
      int dtlListSize                        = 0;
      int hdrInserted                        = 0;
      ArrayList         dtlList              = null;
      int               index                = 0;
      String            rateBreak            = "";
      String            chargeSb             = "";
      String            selectSeq            = null;
      String            buyChargeId          =  null;
      String            updateSell           =  null;
      try
      {
//      "BUYSELLCHARGEID,CHARGE_ID,CHARGEBASIS,RATE_BREAK,RATE_TYPE,CURRENCY,WEIGHT_CLASS,"
//     +"CREATED_BY,CREATED_TSTMP,LAST_UPDATED_BY,LAST_UPDATED_TSTMP,DEL_FLAG,ACCESSLEVEL,TERMINALID";
       
        selectSeq = " SELECT BUYSELL_SEQ.NEXTVAL BUYCHARGEID FROM DUAL"; 
        updateSell = "UPDATE QMS_SELLCHARGESMASTER SET BUYCHARGEID = ? WHERE SELLCHARGEID = ?";
        
        getConnection();
        
        /*pstmt3 = connection.prepareStatement(selectSeq);
        
        rs = pstmt3.executeQuery();
        
        if(rs.next())
        {
          buyChargeId = rs.getString("BUYCHARGEID");
        }
        
        if(rs!=null)
          { rs.close();}
        if(pstmt3!=null)
          { pstmt3.close();}
        */
        pstmt1   = connection.prepareStatement(insertBuyHdrQry);
        pstmt2   = connection.prepareStatement(insertBuyDtlQry);
        
       
        //if(dataList!=null && dataList.size()>0)
        //  dataListSize  = dataList.size();
        
        /*for(int i=0;i<dataListSize;i++)
        {
          buychargesHDRDOB  = (BuychargesHDRDOB)dataList.get(i);*/
        
          
          
        if(buychargesHDRDOB!=null)
        {
          pstmt1.clearParameters();
            //pstmt1.setString(1,buyChargeId);
            pstmt1.setDouble(1,buychargesHDRDOB.getBuychargeId());
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
            if(hdrInserted>0)
            {
              dtlList = buychargesHDRDOB.getBuyChargeDtlList();
              //double buysellID = buychargesHDRDOB.getBuySellChargeId();
              if(dtlList!=null && dtlList.size()>0)
              {
                 dtlListSize = dtlList.size();
                for(index = 0;index<dtlListSize;index++)
                {
                  pstmt2.clearParameters();
                  buychargesDtlDOB = (BuychargesDtlDOB)dtlList.get(index);
                  pstmt2.setDouble(1,buychargesHDRDOB.getBuychargeId());
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
                    else if(!(rateBreak.equals("Flat") || rateBreak.equals("Flat%")))
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
              
              //update sellcharges table with the buycharge id;
              pstmt3 = connection.prepareStatement(updateSell);
              pstmt3.clearParameters();
              pstmt3.setDouble(1,buychargesHDRDOB.getBuychargeId());
              pstmt3.setDouble(2,buychargesHDRDOB.getBuySellChargeId());
              pstmt3.executeUpdate();
              
              
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
          /*if(rs!=null)
            { rs.close();}*///Commented By RajKumari on 24-10-2008 for Connection Leakages.
          if(pstmt3!=null)
            { pstmt3.close();}            
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

  public String validateHeaderData(BuychargesHDRDOB buychargesHDRDOB)throws Exception
  {
     
     CallableStatement  cstmt = null;
     String returnValue      = "";
    try
    {
      getConnection();
      
      cstmt = connection.prepareCall("{? = call PKG_QMS_CHARGES.VALIDATE_SELLCHARGESADD(?,?,?,?,?,?)}");
      
      cstmt.registerOutParameter(1,Types.VARCHAR);
      cstmt.setString(2,buychargesHDRDOB.getChargeId());
      cstmt.setString(3,buychargesHDRDOB.getChargeDescId());
      cstmt.setString(4,buychargesHDRDOB.getChargeBasisId());
      cstmt.setString(5,buychargesHDRDOB.getDensityGrpCode());
      cstmt.setString(6,buychargesHDRDOB.getCurrencyId());
      cstmt.setString(7,buychargesHDRDOB.getTerminalId());
      
      cstmt.execute();
      
      returnValue = cstmt.getString(1);
      
      //System.out.println("returnValue:::"+returnValue ); 
      
    }catch(Exception e)
    {
      e.printStackTrace();
      throw new Exception();
    }finally
    {
      try
      {
        if(cstmt!=null)
        { cstmt.close();}
        if(connection!=null)
        { connection.close();}
        
      }catch(Exception e)
      {
        
      }
    }
    return returnValue;
  }
  
}
