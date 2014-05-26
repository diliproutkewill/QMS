/**
 * @ (#) IndustryRegDAO.java
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
 * File       : QMSSellRatesDAO.java
 * Sub-Module : Sell Rates
 * Module     : QMS
 * @author    : Yuvraj Waghray, Madhu Y.
 * * @date 
 * Modified by      Date     Reason
 */
 
package com.qms.operations.sellrates.dao;

import com.foursoft.esupply.common.bean.ESupplyGlobalParameters;
import com.foursoft.esupply.common.util.ConnectionUtil;
//import com.foursoft.esupply.common.util.Logger;
import org.apache.log4j.Logger;
import com.foursoft.etrans.common.util.java.OperationsImpl;
import com.qms.operations.sellrates.ejb.bmp.QMSSellRatesEntityPK;
import com.qms.operations.sellrates.java.QMSBoundryDOB;
import com.qms.operations.sellrates.java.QMSSellRatesDOB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.CallableStatement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import javax.ejb.EJBException;
import javax.ejb.ObjectNotFoundException;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import oracle.jdbc.OracleTypes;


public class QMSSellRatesDAO 
{
    static final String FILE_NAME="QMSSellRatesDAO.java";
    private transient DataSource  dataSource=null;
    private static Logger logger = null;
    Connection  connection=null;
  
  private static final String sqlQuery1            = "SELECT QMS_SELLRATES.NEXTVAL FROM DUAL";
  private static final String sqlQuery2            = "SELECT QMS_SELLRATES.NEXTVAL SELLRATESID FROM DUAL";
  private static final String sqlQueryUserlog      = "INSERT INTO FS_USERLOG(LOCATIONID,USERID,DOCTYPE,DOCREFNO,DOCDATE,TRANSACTIONTYPE )VALUES (?,?,?,?,?,?)";  
  private static final String sqlQuerySellMaster   = "INSERT INTO QMS_REC_CON_SELLRATESMASTER(REC_CON_ID,RC_FLAG,SHIPMENT_MODE,WEIGHT_BREAK,RATE_TYPE,CURRENCY,WEIGHT_CLASS,OVERALL_MARGIN,MARGIN_TYPE,MARGIN_BASIS,CREATED_BY,CREATED_TSTMP,LAST_UPDATED_BY,LAST_UPDATED_TSTMP,AI_FLAG,ACCESSLEVEL,TERMINALID) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
//  private static final String sqlQuerySellDtl      = "INSERT INTO QMS_REC_CON_SELLRATESDTL(REC_CON_ID,CARRIER_ID,SERVICELEVEL_ID,CHARGERATE,PALLET_CAPACITY,PALLET_BYRATE,AVEREAGE_UNIT,LOOSE_SPACE,UPPER_BOUND,LOWRER_BOUND,BUY_RATE_AMT,MARGIN_PERC,AI_FLAG,NOTES,WEIGHTBREAKSLAB,FREQUENCY,CHARGERATE_INDICATOR,LANE_NO,LINE_NO,BUYRATEID,TRANSIT_TIME,ORIGIN,DESTINATION,ORIGIN_COUNTRY,DESTI_COUNTRY,CONSOLE_TYPE,INVALIDATE,ID) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,SEQ_CON_SELLRATESDTL.NEXTVAL)";
 //private static final String sqlQuerySellDtl      = "INSERT INTO QMS_REC_CON_SELLRATESDTL(REC_CON_ID,CARRIER_ID,SERVICELEVEL_ID,CHARGERATE,PALLET_CAPACITY,PALLET_BYRATE,AVEREAGE_UNIT,LOOSE_SPACE,UPPER_BOUND,LOWRER_BOUND,BUY_RATE_AMT,MARGIN_PERC,AI_FLAG,NOTES,WEIGHTBREAKSLAB,FREQUENCY,CHARGERATE_INDICATOR,LANE_NO,LINE_NO,BUYRATEID,TRANSIT_TIME,ORIGIN,DESTINATION,ORIGIN_COUNTRY,DESTI_COUNTRY,CONSOLE_TYPE,INVALIDATE,RATE_DESCRIPTION,ID) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,SEQ_CON_SELLRATESDTL.NEXTVAL)";
 
  //@@Modified by Kameswari for Surcharge Enhancements 
 private static final String sqlQuerySellDtl      = "INSERT INTO QMS_REC_CON_SELLRATESDTL(REC_CON_ID,CARRIER_ID,SERVICELEVEL_ID,CHARGERATE,PALLET_CAPACITY,PALLET_BYRATE,AVEREAGE_UNIT,LOOSE_SPACE,UPPER_BOUND,LOWRER_BOUND,BUY_RATE_AMT,MARGIN_PERC,AI_FLAG,NOTES,WEIGHTBREAKSLAB,FREQUENCY,CHARGERATE_INDICATOR,LANE_NO,LINE_NO,BUYRATEID,TRANSIT_TIME,ORIGIN,DESTINATION,ORIGIN_COUNTRY,DESTI_COUNTRY,CONSOLE_TYPE,INVALIDATE,RATE_DESCRIPTION,ID,VERSION_NO,EXTERNAL_NOTES)" // //Modified by Mohan for Issue No.219976 on 08-10-2010
 +"VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,SEQ_CON_SELLRATESDTL.NEXTVAL,?,?)";//@@Modified by Kameswari for the WPBN issue-146448 on 23/12/08
  private static final String originQuery           = " SELECT LOCATIONID FROM  FS_FR_LOCATIONMASTER WHERE LOCATIONID=? ";
  private static final String carrierQuery          = " SELECT CARRIERID FROM FS_FR_CAMASTER WHERE CARRIERID=?";
  private static final String serviceQuery          = " SELECT SERVICELEVELID FROM FS_FR_SERVICELEVELMASTER WHERE SERVICELEVELID=?";
  private static final String currencyQuery         = " SELECT CURRENCYID FROM	FS_COUNTRYMASTER WHERE CURRENCYID=?";
//  private static final String changeStatusQuery     = " UPDATE QMS_REC_CON_SELLRATESDTL SET INVALIDATE=? WHERE REC_CON_ID=? AND BUYRATEID=? AND ORIGIN=? AND DESTINATION=? AND CARRIER_ID=? AND SERVICELEVEL_ID=? AND FREQUENCY=? AND LANE_NO=?";
   //private static final String changeStatusQuery     = " UPDATE QMS_REC_CON_SELLRATESDTL SET INVALIDATE=? WHERE REC_CON_ID=? AND BUYRATEID=? AND ORIGIN=? AND DESTINATION=? AND CARRIER_ID=? AND SERVICELEVEL_ID IN (?,'SCH')  AND FREQUENCY=? AND LANE_NO=?";
    private static final String changeStatusQuery     = " UPDATE QMS_REC_CON_SELLRATESDTL SET INVALIDATE=? WHERE REC_CON_ID=? AND BUYRATEID=?  AND ORIGIN=? AND DESTINATION=? AND CARRIER_ID=? AND SERVICELEVEL_ID IN (?,'SCH')  AND FREQUENCY=? AND LANE_NO=? AND VERSION_NO=?";  //@@Added by Kameswari for the WPBN issue -146448 on 23/12/08
   
     private static final String checkValuesQuery      = " SELECT BUYRATEID FROM QMS_REC_CON_SELLRATESDTL SD,QMS_REC_CON_SELLRATESMASTER SM WHERE SM.REC_CON_ID=SD.REC_CON_ID AND SD.BUYRATEID=? AND SM.RC_FLAG=?";
 // private static final String checkUpdateQuery      = " UPDATE QMS_REC_CON_SELLRATESDTL SET AI_FLAG=? WHERE BUYRATEID=? AND ORIGIN=? AND DESTINATION=? AND SERVICELEVEL_ID=? AND CARRIER_ID=? AND FREQUENCY=? AND EXISTS (SELECT 'X' FROM QMS_REC_CON_SELLRATESMASTER MAS WHERE MAS.RC_FLAG='R' AND MAS.REC_CON_ID=DTL.REC_CON_ID)"; 
  //private static final String checkUpdateQuery      = " UPDATE QMS_REC_CON_SELLRATESDTL SET AI_FLAG=? WHERE BUYRATEID=? AND ORIGIN=? AND DESTINATION=? AND SERVICELEVEL_ID IN (?,'SCH') AND CARRIER_ID=? AND FREQUENCY=? AND EXISTS (SELECT 'X' FROM QMS_REC_CON_SELLRATESMASTER MAS WHERE MAS.RC_FLAG='R' AND MAS.REC_CON_ID=DTL.REC_CON_ID)"; 
   private static final String checkUpdateQuery      = " UPDATE QMS_REC_CON_SELLRATESDTL SET AI_FLAG=? WHERE BUYRATEID=? AND ORIGIN=? AND DESTINATION=? AND SERVICELEVEL_ID IN (?,'SCH') AND CARRIER_ID=? AND FREQUENCY=? AND EXISTS (SELECT 'X' FROM QMS_REC_CON_SELLRATESMASTER MAS WHERE MAS.RC_FLAG='R' AND MAS.REC_CON_ID=DTL.REC_CON_ID)AND VERSION_NO=?"; ////@@Added by Kameswari for the WPBN issue -146448 on 23/12/08
  //private static final String invalidateExisting    = " UPDATE QMS_REC_CON_SELLRATESDTL SET AI_FLAG=? WHERE BUYRATEID=? AND  REC_CON_ID IN (SELECT DTL.REC_CON_ID FROM QMS_REC_CON_SELLRATESMASTER MAS,QMS_REC_CON_SELLRATESDTL DTL WHERE MAS.RC_FLAG='C' AND MAS.REC_CON_ID=DTL.REC_CON_ID)";
  private static final String invalidateExisting    = " UPDATE QMS_REC_CON_SELLRATESDTL SET AI_FLAG=? WHERE BUYRATEID=? AND  REC_CON_ID IN (SELECT DTL.REC_CON_ID FROM QMS_REC_CON_SELLRATESMASTER MAS,QMS_REC_CON_SELLRATESDTL DTL WHERE MAS.RC_FLAG='C' AND MAS.REC_CON_ID=DTL.REC_CON_ID) AND VERSION_NO=?";//@@Added by Kameswari for the WPBN issue -146448 on 23/12/08
 // private static final String UpdateBuyRtFlagQuery  = " UPDATE QMS_BUYRATES_DTL SET GENERATED_FLAG =? WHERE BUYRATEID=? AND ORIGIN=? AND DESTINATION=? AND SERVICE_LEVEL=? AND CARRIER_ID=? AND FREQUENCY=?";
 // private static final String deleteBuyRtQuery      = " DELETE FROM QMS_REC_CON_SELLRATESDTL_ACC WHERE REC_CON_ID=? AND BUYRATEID=? AND LANE_NO=?";
  private static final String deleteBuyRtQuery      = " DELETE FROM QMS_REC_CON_SELLRATESDTL_ACC WHERE REC_CON_ID=? AND BUYRATEID=? AND LANE_NO=? AND VERSION_NO=?"; ////@@Added by Kameswari for the WPBN issue -146448 on 23/12/08
  // private static final String updateBuyRtQuery      = " UPDATE QMS_BUYRATES_DTL  SET ACC_FLAG=NULL WHERE  BUYRATEID=? AND LANE_NO=?"; //@@Added by Kameswari for the WPBN issue-139041
   private static final String updateBuyRtQuery      = " UPDATE QMS_BUYRATES_DTL  SET ACC_FLAG=NULL WHERE  BUYRATEID=? AND LANE_NO=? AND VERSION_NO=?"; ////@@Added by Kameswari for the WPBN issue -146448 on 23/12/08
   private static final String portmaterQuery        = " SELECT PORTID FROM FS_FRS_PORTMASTER WHERE PORTID=? ";
  public QMSSellRatesDAO()
  {
    logger  = Logger.getLogger(QMSSellRatesDAO.class);
    try
      {
        InitialContext ic =new InitialContext();
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
 public void create(QMSSellRatesDOB sellRateDob,ArrayList listValue,ESupplyGlobalParameters loginBean,String operation)throws SQLException
 {
        PreparedStatement		pStmt		 	      =   null;
        //PreparedStatement		pStmt2			    =   null;
        Statement    			  stmt          	= 	null;
        ResultSet    			  rs            	= 	null;
        CallableStatement		csmt            =   null;
     //   ResultSet				    rs1             =   null;
        QMSSellRatesDOB			sellDob         =   null;
        int						      sellRatesId     =   0;
        String              terminalId      =   null;      
        ArrayList           dobList          = null;
   
        try
        {
            
              getConnection();
              if("Add".equalsIgnoreCase(operation))
                terminalId  = loginBean.getTerminalId();
              else if("Modify".equalsIgnoreCase(operation))
                terminalId  = sellRateDob.getTerminalId();
                
              //System.out.println("terminalIdterminalId :: in Dao : "+terminalId);
              int   sizeVal =   listValue.size();
                 stmt        =   connection.createStatement();
              rs          =   stmt.executeQuery(sqlQuery2);
              while(rs.next())
                sellRatesId   =   rs.getInt("SELLRATESID");
               
              pStmt           =   connection.prepareStatement(sqlQueryUserlog);
              pStmt.setString(1,terminalId);
              pStmt.setString(2,loginBean.getUserId());
              pStmt.setString(3,"rSellRates");
              pStmt.setString(4,"DHLEXHO.HOROLE1");
              pStmt.setTimestamp(5,new java.sql.Timestamp((new java.util.Date()).getTime()));
              pStmt.setString(6,operation);
              pStmt.executeUpdate(); 
              
              if(pStmt!=null)
                pStmt.close();
              
              pStmt           =   connection.prepareStatement(sqlQuerySellMaster);
              pStmt.setInt(1,sellRatesId);
              pStmt.setString(2,"R");
              pStmt.setInt(3,Integer.parseInt(sellRateDob.getShipmentMode()));
              pStmt.setString(4,sellRateDob.getWeightBreak());
              pStmt.setString(5,sellRateDob.getRateType());
              pStmt.setString(6,sellRateDob.getCurrencyId());
              pStmt.setString(7,sellRateDob.getWeightClass());
              pStmt.setString(8,sellRateDob.getOverAllMargin());
              pStmt.setString(9,sellRateDob.getMarginType());
              pStmt.setString(10,sellRateDob.getMarginBasis());
              pStmt.setString(11,loginBean.getUserId());
              pStmt.setTimestamp(12,new java.sql.Timestamp((new java.util.Date()).getTime()));
              pStmt.setString(13,loginBean.getUserId());
              pStmt.setTimestamp(14,new java.sql.Timestamp((new java.util.Date()).getTime()));
              pStmt.setString(15,"A");
              pStmt.setString(16,sellRateDob.getAccessLevel());
              pStmt.setString(17,terminalId);
              pStmt.executeUpdate(); 
              
              if(pStmt!=null)
                  pStmt.close();
                  
              pStmt           =   connection.prepareStatement(sqlQuerySellDtl); 
             // pStmt2          =   connection.prepareStatement(UpdateBuyRtFlagQuery);
              int   sizeValue =   listValue.size();
              String  returnValue = null;
              csmt = connection.prepareCall("{ ?= call Qms_rsr_rates_Pkg.QMS_SELL_RATE_VALIDATION(?,?,?,?,?,?,?,?,?,?,?,?,?,?)} ");
              for(int j=0;j<sizeValue;j++)
             {
                                    
                    dobList  = (ArrayList)listValue.get(j);//@@Modified by Kameswari for the WPBN issue-126038
                    //sellDob   = (QMSSellRatesDOB)listValue.get(0);
                    sellDob   = (QMSSellRatesDOB)dobList.get(0);
                    
                    //if(i==0)
                    //{
                      //System.out.println("Modify Status 2222222 ");
                      csmt.clearParameters();
                      csmt.registerOutParameter(1,Types.VARCHAR);
                      csmt.setString(2,sellDob.getOrigin());
                      csmt.setString(3,sellDob.getDestination());
                      csmt.setString(4,sellDob.getServiceLevel());
                      csmt.setString(5,sellDob.getCarrier_id());
                      csmt.setString(6,sellDob.getFrequency());
                      csmt.setString(7,terminalId);
                      csmt.setString(8,sellRateDob.getWeightBreak());
                      csmt.setString(9,sellRateDob.getRateType());
                      csmt.setString(10,sellRateDob.getShipmentMode());
                      csmt.setString(11,"INS");
                      csmt.setLong(12,sellRatesId);
                      csmt.setString(13,sellDob.getBuyRateId());
                      csmt.setInt(14,sellDob.getLanNumber());
                      csmt.setString(15,operation); //@@Added for teh WPBN issue-146968 on 11/01/08
            
                      csmt.execute(); 
                      returnValue = csmt.getString(1); 
                    //}
                  //System.out.println("returnValuereturnValuereturnValue :: "+returnValue);
                  if("6".equals(returnValue))
                  {
                 //  for(int i=0;i<sizeValue;i++)
                	  int dobListSize	=	dobList.size();
                   for(int i=0;i<dobListSize;i++)//@@Modified by Kameswari for the WPBN issue-
                   {
                   // sellDob   = (QMSSellRatesDOB)listValue.get(i);
                   sellDob   = (QMSSellRatesDOB)dobList.get(i);//@@Modified by Kameswari for the WPBN issue-126038
           
                    pStmt.setInt(1,sellRatesId);
                    pStmt.setString(2,sellDob.getCarrier_id());
                     pStmt.setString(3,sellDob.getServiceLevel());
                    pStmt.setDouble(4,sellDob.getChargeRate());
                    pStmt.setDouble(5,0.0);
                    pStmt.setDouble(6,0.0);
                    pStmt.setDouble(7,0.0);
                    pStmt.setDouble(8,0.0);
                    pStmt.setDouble(9,sellDob.getUpperBd());//Modified by Mohan for issue on 12112010
                    pStmt.setDouble(10,sellDob.getLowerBd());//Modified by Mohan for issue on 12112010
                   // System.out.println("sellDob.getBuyRate() in DAO : "+sellDob.getBuyRate());
                    pStmt.setDouble(11,sellDob.getBuyRate());
                    pStmt.setDouble(12,sellDob.getMarginPer());
                    pStmt.setString(13,"A");
                    pStmt.setString(14,sellDob.getNoteValue());
                    pStmt.setString(15,sellDob.getMinFlat());
                    pStmt.setString(16,sellDob.getFrequency());
                    pStmt.setString(17,sellDob.getChargerateIndicator());
                    //pStmt.setString(17,"");
                    pStmt.setInt(18,sellDob.getLanNumber());
                    pStmt.setInt(19,sellDob.getLineNumber());
                    pStmt.setString(20,sellDob.getBuyRateId());
                    pStmt.setString(21,sellDob.getTransitTime());
                    pStmt.setString(22,sellDob.getOrigin());
                    pStmt.setString(23,sellDob.getDestination());
                    pStmt.setString(24,sellDob.getOriginCountry());
                    pStmt.setString(25,sellDob.getDestinationCountry());
                    pStmt.setString(26,sellRateDob.getConsoleType());
                    pStmt.setString(27,"F");
                    /* if("FSBASIC".equalsIgnoreCase(sellDob.getMinFlat())||"FSMIN".equalsIgnoreCase(sellDob.getMinFlat())
                       ||"FSKG".equalsIgnoreCase(sellDob.getMinFlat()))
                       
                           pStmt.setString(28,"FUEL SURCHARGE");
                       
                    else if("SSBASIC".equalsIgnoreCase(sellDob.getMinFlat())||"SSMIN".equalsIgnoreCase(sellDob.getMinFlat())
                       ||"SSKG".equalsIgnoreCase(sellDob.getMinFlat()))
                        pStmt.setString(28,"SECURITY SURCHARGE");
                    else if("BAFMIN".equalsIgnoreCase(sellDob.getMinFlat())||"BAFM3".equalsIgnoreCase(sellDob.getMinFlat()))
                        pStmt.setString(28,"B.A.F");
                    else if("CAFMIN".equalsIgnoreCase(sellDob.getMinFlat())||"CAF%".equalsIgnoreCase(sellDob.getMinFlat()))
                        pStmt.setString(28,"C.A.F%");
                    else if("PSSMIN".equalsIgnoreCase(sellDob.getMinFlat())||"PSSM3".equalsIgnoreCase(sellDob.getMinFlat()))
                        pStmt.setString(28,"P.S.S");
                    else if("CSF".equalsIgnoreCase(sellDob.getMinFlat()))
                        pStmt.setString(28,"C.S.F");
                    else if("SURCHARGE".equalsIgnoreCase(sellDob.getMinFlat()))
                        pStmt.setString(28,"SURCHARGE");
                    else if(sellDob.getMinFlat().endsWith("BAF"))
                       pStmt.setString(28,"BAF");
                   else if(sellDob.getMinFlat().endsWith("CAF"))
                        pStmt.setString(28,"CAF");
                    else if(sellDob.getMinFlat().endsWith("CSS"))
                        pStmt.setString(28,"CSS");
                    else if(sellDob.getMinFlat().endsWith("PSS"))
                        pStmt.setString(28,"PSS");
                    else
                        pStmt.setString(28,"A FREIGHT RATE");*/
                        /*if(sellDob.getMinFlat().startsWith("FS"))
                          pStmt.setString(28,"FUEL SURCHARGE");
                       else if(sellDob.getMinFlat().startsWith("SS"))
                          pStmt.setString(28,"SECURITY SURCHARGE");
                       else if(sellDob.getMinFlat().startsWith("BAF"))     
                         pStmt.setString(28,"B.A.F");
                       else if(sellDob.getMinFlat().startsWith("CAF"))     
                         pStmt.setString(28,"C.A.F%");
                          else if(sellDob.getMinFlat().startsWith("PSS"))     
                         pStmt.setString(28,"C.P.S.S");
                          else if(sellDob.getMinFlat().startsWith("CSF"))     
                         pStmt.setString(28,"C.S.F");
                          else if(sellDob.getMinFlat().startsWith("SURCHARGE"))     
                         pStmt.setString(28,"SURCHARGE");
                          else if(sellDob.getMinFlat().endsWith("BAF"))     
                         pStmt.setString(28,"BAF");
                          else if(sellDob.getMinFlat().endsWith("CAF"))     
                         pStmt.setString(28,"CAF%");
                          else if(sellDob.getMinFlat().endsWith("CSF"))     
                         pStmt.setString(28,"CSF");
                          else if(sellDob.getMinFlat().endsWith("PSS"))     
                         pStmt.setString(28,"PSS");
                          else
                           pStmt.setString(28,"A FREIGHT RATE");*/
                    pStmt.setString(28, sellDob.getSurChargeDescription());
                       
                       pStmt.setString(29,sellDob.getVersionNo());   //@@Added for the WPBN issue-146448 on 23/12/08
                       pStmt.setString(30,sellDob.getExtNotes()); //Modified by Mohan for Issue No.219976 on 08-10-2010 
                pStmt.addBatch(); 
                   /* pStmt2.setString(1,"Y");
                    pStmt2.setString(2,sellDob.getBuyRateId());
                    pStmt2.setString(3,sellDob.getOrigin());
                    pStmt2.setString(4,sellDob.getDestination());
                    pStmt2.setString(5,sellDob.getServiceLevel());
                    pStmt2.setString(6,sellDob.getCarrier_id());
                    pStmt2.setString(7,sellDob.getFrequency());
                    pStmt2.addBatch();*/ 
              }
                  }
                 else
                  {

                    throw new Exception("Error while Inserting into  QMS_REC_CON_SELLRATESDTL "+returnValue);    
                    //System.out.println("returnValuereturnValuereturnValue dao in else :: "+returnValue);

                 //System.out.println("returnValuereturnValuereturnValue dao in else :: "+returnValue);

                     // System.out.println("dao in else :: "+loginBean.getTerminalId());
                  }
             
             
              pStmt.executeBatch();
             }
             // pStmt2.executeBatch();
              
              System.out.println("******Calling DAO*********");
              //System.out.println("dao 66666666666666"+loginBean.getTerminalId());
        
        }
        catch(SQLException sqle)
        {
            //Logger.error(FILE_NAME,"SQLEXception in create(1)-->"+sqle.toString());
            logger.error(FILE_NAME+"SQLEXception in create(1)-->"+sqle.toString());
            sqle.printStackTrace();
            throw new EJBException(sqle.toString());
        }
        catch(Exception e)
        {
          //Logger.error(FILE_NAME,"EXception in create(1)-->"+e.toString());
          logger.error(FILE_NAME+"EXception in create(1)-->"+e.toString());
          e.printStackTrace();
          throw new EJBException(e.toString());
        }
        finally
        {
            try
            {
                if(rs!=null)
                  {rs.close();}
              /*  if(rs1!=null)
                  {rs1.close();}*///Commented by govind on 16-02-2010 for Connection leakages
                if(csmt!=null)
                  {csmt.close();}
                if(stmt!=null)
                  {stmt.close();}
                if(pStmt!=null)
                  {pStmt.close();}
                //if(pStmt2!=null)
                //  {pStmt2.close();}
                if(connection!=null)
                  {connection.close();}
                sellRateDob=null;
            }
            catch(Exception ex)
            {
                //Logger.error(FILE_NAME,"Exception caught :: finally :: insertRecords() " + ex.toString() );
                logger.error(FILE_NAME+"Exception caught :: finally :: insertRecords() " + ex.toString() );
            }
        }
  }
public void create(ArrayList sellRateList,ArrayList listValue,ESupplyGlobalParameters loginBean,String operation)throws SQLException
{
    PreparedStatement		pStmt		 	        =   null;
    Statement    			  stmt          	  = 	null;
    ResultSet    			  rs            	  = 	null;
    QMSSellRatesDOB			hedarDob          =   null;
    ArrayList           flatListArray          =   null;
    ArrayList           slabListArray          =   null;
    ArrayList           listListArray          =   null;
    ArrayList           fclListArray           =   null;
    ArrayList           flatList          =   null;
    ArrayList           slabList          =   null;
    ArrayList           listList          =   null;
    ArrayList           fclList           =   null;
    int						      sellRatesId       =   0;
    int                 c1                   =0,c2=0,c3=0,c4=0;
    try
    {
          getConnection();
          int sellRateListSize  = sellRateList.size();
          /*flatList  = (ArrayList)listValue.get(0);
          slabList  = (ArrayList)listValue.get(1);
          listList  = (ArrayList)listValue.get(2);
          fclList   = (ArrayList)listValue.get(3);*/
          
          
         if(listValue!=null)
         {
          flatListArray  = (ArrayList)listValue.get(0);
           slabListArray  = (ArrayList)listValue.get(1);
          listListArray  = (ArrayList)listValue.get(2);
          fclListArray   = (ArrayList)listValue.get(3);
         }
          //System.out.println("sellRateListSizesellRateListSizesellRateListSize in DAO :: "+sellRateListSize);
          for(int k=0;k<sellRateListSize;k++)
          {
              hedarDob      =   (QMSSellRatesDOB)sellRateList.get(k);
              //System.out.println("WeightBreakWeightBreakWeightBreak in DAO :: "+hedarDob.getWeightBreak());
              
              stmt            =   connection.createStatement();
              rs          =   stmt.executeQuery(sqlQuery2);
              while(rs.next())
              sellRatesId   =   rs.getInt("SELLRATESID");
              
              if(rs!=null)
                rs.close();
              if(stmt!=null)
                stmt.close();
                    
              pStmt           =   connection.prepareStatement(sqlQueryUserlog);
              pStmt.setString(1,loginBean.getTerminalId());
              pStmt.setString(2,loginBean.getUserId());
              pStmt.setString(3,"rSellRates");
              pStmt.setString(4,"DHLEXHO.HOROLE1");
              pStmt.setTimestamp(5,new java.sql.Timestamp((new java.util.Date()).getTime()));
              pStmt.setString(6,operation);
              pStmt.executeUpdate(); 
      
              if(pStmt!=null)
                pStmt.close();
              //System.out.println("sellRatesIdsellRatesIdsellRatesIdsellRatesIdsellRatesId :: in dao "+sellRatesId);
              
              pStmt           =   connection.prepareStatement(sqlQuerySellMaster);

              pStmt.setInt(1,sellRatesId);
              pStmt.setString(2,"R");
              pStmt.setInt(3,Integer.parseInt(hedarDob.getShipmentMode()));
              pStmt.setString(4,hedarDob.getWeightBreak());
              pStmt.setString(5,hedarDob.getRateType());
              //hedarDob.getCurrencyId()
              pStmt.setString(6,hedarDob.getCurrencyId());
              pStmt.setString(7,hedarDob.getWeightClass());
              pStmt.setString(8,hedarDob.getOverAllMargin());
              pStmt.setString(9,hedarDob.getMarginType());
              pStmt.setString(10,hedarDob.getMarginBasis());
              pStmt.setString(11,loginBean.getUserId());
              pStmt.setTimestamp(12,new java.sql.Timestamp((new java.util.Date()).getTime()));
              pStmt.setString(13,loginBean.getUserId());
              pStmt.setTimestamp(14,new java.sql.Timestamp((new java.util.Date()).getTime()));
              pStmt.setString(15,"A");
             // hedarDob.getAccessLevel()
              pStmt.setString(16,hedarDob.getAccessLevel());
              pStmt.setString(17,loginBean.getTerminalId());
              pStmt.executeUpdate(); 
              if(pStmt!=null)
                  pStmt.close(); 
                 
           
             if("1".equals(hedarDob.getShipmentMode()) || ("2".equals(hedarDob.getShipmentMode()) && "LCL".equals(hedarDob.getConsoleType())) || ("4".equals(hedarDob.getShipmentMode()) && "LTL".equals(hedarDob.getConsoleType())))
              {
                 
                  if("FLAT".equals(hedarDob.getWeightBreak()))
                  {
                    if(flatListArray!=null&&c1<flatListArray.size())
                    {
                    flatList  = (ArrayList)flatListArray.get(c1);
                     
                        insertFlatValues(sellRatesId,operation,flatList,loginBean,connection,hedarDob);
                      
                        c1++;
                    }
                  }
                  if("SLAB".equals(hedarDob.getWeightBreak()))
                  {
                     if(slabListArray!=null&&c2<slabListArray.size())
                     {
                       slabList  = (ArrayList)slabListArray.get(c2);
                    
                        insertSlabValues(sellRatesId,operation,slabList,loginBean,connection,hedarDob);
                      c2++;
                     }
                
                   }
                  if("LIST".equals(hedarDob.getWeightBreak()))
                  {
                    
                   if(listListArray!=null&&c3<listListArray.size())
                   {
                      listList  = (ArrayList)listListArray.get(c3);
                     insertListValues(sellRatesId,operation,listList,loginBean,connection,hedarDob);
                     c3++;
                   }
                  
                  }
              }
              else
              {
                  if("LIST".equals(hedarDob.getWeightBreak()))
                  {
                     if(fclListArray!=null&&c4<fclListArray.size())
                   {
                       fclList  = (ArrayList)fclListArray.get(c4);
                      
                      insertFclValues(sellRatesId,operation,fclList,loginBean,connection,hedarDob);
                      c4++;
                   }
                  }
                
              }
        
           }
    }
    catch(SQLException sqle)
    {
        //Logger.error(FILE_NAME,"SQLEXception in create(2)-->"+sqle.toString());
        logger.error(FILE_NAME+"SQLEXception in create(2)-->"+sqle.toString());
        sqle.printStackTrace();
        throw new EJBException(sqle.toString());
    }
    catch(Exception e)
    {
      //Logger.error(FILE_NAME,"EXception in create(2)-->"+e.toString());
      logger.error(FILE_NAME+"EXception in create(2)-->"+e.toString());
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
            if(pStmt!=null)
              {pStmt.close();}
           if(connection!=null)
              {connection.close();}
        }
        catch(Exception ex)
        {
            //Logger.error(FILE_NAME,"Exception caught :: finally :: insertRecords() " + ex.toString() );
            logger.error(FILE_NAME+"Exception caught :: finally :: insertRecords() " + ex.toString() );
        }
    }
}
private void insertFlatValues(int sellRatesId,String operation,ArrayList listValue,ESupplyGlobalParameters loginBean,Connection connection,QMSSellRatesDOB	hedarDob)throws SQLException
{
    PreparedStatement		pStmt		 	      =   null;
    PreparedStatement		pStmt1			    =   null;//@@Added by Kameswari for the WPBN issue-139041
    PreparedStatement   pStmt3          =   null;
    CallableStatement		csmt            =   null;
    QMSSellRatesDOB		  sellDob         =   null;
       try
    {
          pStmt           =   connection.prepareStatement(sqlQuerySellDtl); 
         // pStmt1          =   connection.prepareStatement(UpdateBuyRtFlagQuery);
          int   sizeValue =   listValue.size();
          //System.out.println("sizeValuesizeValuesizeValuesizeValuesizeValuesizeValuesizeValue :: in dao of insertFlatValues "+sizeValue);
          String  returnValue = null;
          csmt = connection.prepareCall("{ ?= call Qms_rsr_rates_Pkg.QMS_SELL_RATE_VALIDATION(?,?,?,?,?,?,?,?,?,?,?,?,?,?)} ");
          /*for(int i=0;i<sizeValue;i++)
          {*/
        
              sellDob   = (QMSSellRatesDOB)listValue.get(0);
             // if(i==0)
             // {
                //System.out.println("Modify Status 2222222 Flat");
                csmt.clearParameters();
                csmt.registerOutParameter(1,Types.VARCHAR);
                csmt.setString(2,sellDob.getOrigin());
                csmt.setString(3,sellDob.getDestination());
                csmt.setString(4,sellDob.getServiceLevel());
                csmt.setString(5,sellDob.getCarrier_id());
                csmt.setString(6,sellDob.getFrequency());
                csmt.setString(7,loginBean.getTerminalId());
                csmt.setString(8,hedarDob.getWeightBreak());
                csmt.setString(9,hedarDob.getRateType());
                csmt.setString(10,hedarDob.getShipmentMode());
                csmt.setString(11,"ACC");
                csmt.setLong(12,sellRatesId);
                csmt.setString(13,sellDob.getBuyRateId());
                csmt.setInt(14,sellDob.getLanNumber());
                csmt.setString(15,operation); //@@Added for teh WPBN issue-146968 on 11/01/08
                csmt.execute(); 
                returnValue = csmt.getString(1); 
             // }
              //System.out.println("returnValuereturnValuereturnValue :: "+returnValue);
              if("6".equals(returnValue))
              {
                for(int i=0;i<sizeValue;i++)
                {
              sellDob   = (QMSSellRatesDOB)listValue.get(i);
                pStmt.setInt(1,sellRatesId);
                pStmt.setString(2,sellDob.getCarrier_id());
                pStmt.setString(3,sellDob.getServiceLevel());
                pStmt.setDouble(4,sellDob.getChargeRate());
                pStmt.setDouble(5,0.0);
                pStmt.setDouble(6,0.0);
                pStmt.setDouble(7,0.0);
                pStmt.setDouble(8,0.0);
                pStmt.setDouble(9,sellDob.getUpperBd());//Modified by Mohan for issue on 12112010
                pStmt.setDouble(10,sellDob.getLowerBd());//Modified by Mohan for issue on 12112010
                //System.out.println("sellDob.getBuyRate() in DAO : "+sellDob.getBuyRate());
                pStmt.setDouble(11,sellDob.getBuyRate());
                pStmt.setDouble(12,sellDob.getMarginPer());
                pStmt.setString(13,"A");
                pStmt.setString(14,sellDob.getNoteValue());
                pStmt.setString(15,sellDob.getMinFlat());
                pStmt.setString(16,sellDob.getFrequency());
                pStmt.setString(17,sellDob.getChargerateIndicator());
                //pStmt.setString(17,"");
                pStmt.setInt(18,sellDob.getLanNumber());
                pStmt.setInt(19,sellDob.getLineNumber());
                pStmt.setString(20,sellDob.getBuyRateId());
                pStmt.setString(21,sellDob.getTransitTime());
                pStmt.setString(22,sellDob.getOrigin());
                pStmt.setString(23,sellDob.getDestination());
                pStmt.setString(24,sellDob.getOriginCountry());
                pStmt.setString(25,sellDob.getDestinationCountry());
                pStmt.setString(26,hedarDob.getConsoleType());
                pStmt.setString(27,"F");
 /*                if("FSBASIC".equalsIgnoreCase(sellDob.getMinFlat())||"FSMIN".equalsIgnoreCase(sellDob.getMinFlat())
                       ||"FSKG".equalsIgnoreCase(sellDob.getMinFlat()))
                    pStmt.setString(28,"FUEL SURCHARGE");
                    else if("SSBASIC".equalsIgnoreCase(sellDob.getMinFlat())||"SSMIN".equalsIgnoreCase(sellDob.getMinFlat())
                       ||"SSKG".equalsIgnoreCase(sellDob.getMinFlat()))
                        pStmt.setString(28,"SECURITY SURCHARGE");
                    else if("BAFMIN".equalsIgnoreCase(sellDob.getMinFlat())||"BAFM3".equalsIgnoreCase(sellDob.getMinFlat()))
                        pStmt.setString(28,"B.A.F");
                    else if("CAFMIN".equalsIgnoreCase(sellDob.getMinFlat())||"CAF%".equalsIgnoreCase(sellDob.getMinFlat()))
                        pStmt.setString(28,"C.A.F%");
                    else if("PSSMIN".equalsIgnoreCase(sellDob.getMinFlat())||"PSSM3".equalsIgnoreCase(sellDob.getMinFlat()))
                        pStmt.setString(28,"C.P.S.S");
                   else if("CSF".equalsIgnoreCase(sellDob.getMinFlat()))
                        pStmt.setString(28,"C.S.F");
                    else if("SURCHARGE".equalsIgnoreCase(sellDob.getMinFlat()))
                        pStmt.setString(28,"SURCHARGE");
                   else 
                          pStmt.setString(28,"A FREIGHT RATE");
                          
*/                        
                pStmt.setString(28, sellDob.getSurChargeDescription());
                           pStmt.setString(29,sellDob.getVersionNo());   //@@Added for the WPBN issue-146448 on 23/12/08
                           pStmt.setString(30,sellDob.getExtNotes());  //Modified by Mohan for Issue No.219976 on 08-10-2010
                pStmt.addBatch(); 
               /* pStmt1.setString(1,"Y");
                pStmt1.setString(2,sellDob.getBuyRateId());
                pStmt1.setString(3,sellDob.getOrigin());
                pStmt1.setString(4,sellDob.getDestination());
                pStmt1.setString(5,sellDob.getServiceLevel());
                pStmt1.setString(6,sellDob.getCarrier_id());
                pStmt1.setString(7,sellDob.getFrequency());
                pStmt1.addBatch(); */
                 }
                    pStmt.executeBatch();
              }
              else
              {

             
              //logger.info("returnValue"+returnValue);
              throw new Exception("Error while Inserting into  QMS_REC_CON_SELLRATESDTL "+returnValue);
                  //System.out.println("returnValuereturnValuereturnValue dao in else :: "+returnValue);
                  //System.out.println("dao in else :: "+loginBean.getTerminalId());
            
                //System.out.println("returnValuereturnValuereturnValue dao in else :: "+returnValue);
                  //System.out.println("dao in else :: "+loginBean.getTerminalId());
              }
              
          
       
          //pStmt1.executeBatch();
          
          pStmt3           =   connection.prepareStatement(deleteBuyRtQuery);
          //System.out.println("Rec_buyrate_idRec_buyrate_idRec_buyrate_idRec_buyrate_idRec_buyrate_id :: "+hedarDob.getRec_buyrate_id());
          pStmt3.setString(1,hedarDob.getRec_buyrate_id());
          pStmt3.setString(2,sellDob.getBuyRateId());
       
          pStmt3.setInt(3,sellDob.getLanNumber());
          pStmt3.setString(4,sellDob.getVersionNo());//@@added for the WPBN issue-146448 on 23/12/08
          pStmt3.executeUpdate();
         //@@Added by Kameswari for the WPBN issue-139041
          pStmt1         =   connection.prepareStatement(updateBuyRtQuery);
          pStmt1.setString(1,sellDob.getBuyRateId());
       
          pStmt1.setInt(2,sellDob.getLanNumber());
          pStmt1.setString(3,sellDob.getVersionNo());//@@Added for the WPBN issue-146448 on 23/12/08
          pStmt1.executeUpdate();
          //@@WPBN issue-139041
          System.out.println("******Calling DAO*********");
          //System.out.println("dao 66666666666666"+loginBean.getTerminalId());
    }
    catch(SQLException sqle)
    {
        //Logger.error(FILE_NAME,"SQLEXception in insertFlatValues(2)-->"+sqle.toString());
        logger.error(FILE_NAME+"SQLEXception in insertFlatValues(2)-->"+sqle.toString());
        sqle.printStackTrace();
        throw new EJBException(sqle.toString());
    }
    catch(Exception e)
    {
      //Logger.error(FILE_NAME,"EXception in insertFlatValues(2)-->"+e.toString());
      logger.error(FILE_NAME+"EXception in insertFlatValues(2)-->"+e.toString());
      e.printStackTrace();
      throw new EJBException(e.toString());
    }
    finally
    {
        try
        {
            if(pStmt!=null)
              {pStmt.close();}
            if(pStmt1!=null)
             {pStmt1.close();}
            if(pStmt3!=null)
              {pStmt3.close();}
            if(csmt!=null)
              {csmt.close();}
            hedarDob=null;
        }
        catch(Exception ex)
        {
            //Logger.error(FILE_NAME,"Exception caught :: finally :: insertFlatValues() " + ex.toString() );
            logger.error(FILE_NAME+"Exception caught :: finally :: insertFlatValues() " + ex.toString() );
        }
    }
}
private void insertSlabValues(int sellRatesId,String operation,ArrayList listValue,ESupplyGlobalParameters loginBean,Connection connection,QMSSellRatesDOB	hedarDob)throws SQLException
{
    PreparedStatement		pStmt		 	      =   null;
    PreparedStatement		pStmt1			    =   null;//@@Added by Kameswari for the WPBN issue-139041
    PreparedStatement   pStmt3          =   null;
    CallableStatement		csmt            =   null;
    QMSSellRatesDOB		  sellDob         =   null;
    
    try
    {
          pStmt           =   connection.prepareStatement(sqlQuerySellDtl); 
       
          //pStmt1          =   connection.prepareStatement(UpdateBuyRtFlagQuery);
          int   sizeValue =   listValue.size();
          //System.out.println("sizeValuesizeValuesizeValuesizeValuesizeValuesizeValuesizeValue :: in dao of insertSlabValues "+sizeValue);
          String  returnValue = null;
          csmt = connection.prepareCall("{ ?= call Qms_rsr_rates_Pkg.QMS_SELL_RATE_VALIDATION(?,?,?,?,?,?,?,?,?,?,?,?,?,?)} ");
         /* for(int i=0;i<sizeValue;i++)
          {*/
           
             
              sellDob   = (QMSSellRatesDOB)listValue.get(0);
            // sellDob   = (QMSSellRatesDOB)listValue.get(1);
               //System.out.println("sellDob.getRec_buyrate_id() ::"+sellDob.getRec_buyrate_id());
    
             
              //if(i==0)
              //{
                //System.out.println("Modify Status 2222222 Slab");
                  
                csmt.clearParameters();
                csmt.registerOutParameter(1,Types.VARCHAR);
                csmt.setString(2,sellDob.getOrigin());
                csmt.setString(3,sellDob.getDestination());
                csmt.setString(4,sellDob.getServiceLevel());
                csmt.setString(5,sellDob.getCarrier_id());
                csmt.setString(6,sellDob.getFrequency());
                csmt.setString(7,loginBean.getTerminalId());
                csmt.setString(8,hedarDob.getWeightBreak());
                csmt.setString(9,hedarDob.getRateType());
                csmt.setString(10,hedarDob.getShipmentMode());
                csmt.setString(11,"ACC");
                csmt.setLong(12,sellRatesId);
                csmt.setString(13,sellDob.getBuyRateId());
                csmt.setInt(14,sellDob.getLanNumber());
                csmt.setString(15,operation); //@@Added for teh WPBN issue-146968 on 11/01/08
            
                csmt.execute(); 
                returnValue = csmt.getString(1); 
             // }
              //System.out.println("returnValuereturnValuereturnValue :: "+returnValue);
              if("6".equals(returnValue))
              {
                for(int i=0;i<sizeValue;i++)
               {
                sellDob   = (QMSSellRatesDOB)listValue.get(i);
               
                pStmt.setInt(1,sellRatesId);
                pStmt.setString(2,sellDob.getCarrier_id());
                pStmt.setString(3,sellDob.getServiceLevel());
                pStmt.setDouble(4,sellDob.getChargeRate());
                pStmt.setDouble(5,0.0);
                pStmt.setDouble(6,0.0);
                pStmt.setDouble(7,0.0);
                pStmt.setDouble(8,0.0);
                pStmt.setDouble(9,sellDob.getUpperBd());//Modified by Mohan for issue on 12112010
                pStmt.setDouble(10,sellDob.getLowerBd());//Modified by Mohan for issue on 12112010
                //System.out.println("sellDob.getBuyRate() in DAO : "+sellDob.getBuyRate());
                pStmt.setDouble(11,sellDob.getBuyRate());
                pStmt.setDouble(12,sellDob.getMarginPer());
                pStmt.setString(13,"A");
                pStmt.setString(14,sellDob.getNoteValue());
                pStmt.setString(15,sellDob.getMinFlat());
                pStmt.setString(16,sellDob.getFrequency());
                pStmt.setString(17,sellDob.getChargerateIndicator());
                //pStmt.setString(17,"");
                pStmt.setInt(18,sellDob.getLanNumber());
                pStmt.setInt(19,sellDob.getLineNumber());
                pStmt.setString(20,sellDob.getBuyRateId());
                pStmt.setString(21,sellDob.getTransitTime());
                pStmt.setString(22,sellDob.getOrigin());
                pStmt.setString(23,sellDob.getDestination());
                pStmt.setString(24,sellDob.getOriginCountry());
                pStmt.setString(25,sellDob.getDestinationCountry());
                pStmt.setString(26,hedarDob.getConsoleType());
                pStmt.setString(27,"F");
               /*  if("FSBASIC".equalsIgnoreCase(sellDob.getMinFlat())||"FSMIN".equalsIgnoreCase(sellDob.getMinFlat())
                       ||"FSKG".equalsIgnoreCase(sellDob.getMinFlat()))
                    pStmt.setString(28,"FUEL SURCHARGE");
                    else if("SSBASIC".equalsIgnoreCase(sellDob.getMinFlat())||"SSMIN".equalsIgnoreCase(sellDob.getMinFlat())
                       ||"SSKG".equalsIgnoreCase(sellDob.getMinFlat()))
                        pStmt.setString(28,"SECURITY SURCHARGE");
                    else if("BAFMIN".equalsIgnoreCase(sellDob.getMinFlat())||"BAFM3".equalsIgnoreCase(sellDob.getMinFlat()))
                        pStmt.setString(28,"B.A.F");
                    else if("CAFMIN".equalsIgnoreCase(sellDob.getMinFlat())||"CAF%".equalsIgnoreCase(sellDob.getMinFlat()))
                        pStmt.setString(28,"C.A.F%");
                    else if("PSSMIN".equalsIgnoreCase(sellDob.getMinFlat())||"PSSM3".equalsIgnoreCase(sellDob.getMinFlat()))
                        pStmt.setString(28,"C.P.S.S");
                   else if("CSF".equalsIgnoreCase(sellDob.getMinFlat()))
                        pStmt.setString(28,"C.S.F");
                    else if("SURCHARGE".equalsIgnoreCase(sellDob.getMinFlat()))
                        pStmt.setString(28,"SURCHARGE");
                   else 
                          pStmt.setString(28,"A FREIGHT RATE");
                          */
                pStmt.setString(28,sellDob.getSurChargeDescription());
                           pStmt.setString(29,sellDob.getVersionNo());   //@@Added for the WPBN issue-146448 on 23/12/08
                           pStmt.setString(30,sellDob.getExtNotes());  //Modified by Mohan for Issue No.219976 on 08-10-2010
                pStmt.addBatch(); 
              /*  pStmt1.setString(1,"Y");
                pStmt1.setString(2,sellDob.getBuyRateId());
                pStmt1.setString(3,sellDob.getOrigin());
                pStmt1.setString(4,sellDob.getDestination());
                pStmt1.setString(5,sellDob.getServiceLevel());
                pStmt1.setString(6,sellDob.getCarrier_id());
                pStmt1.setString(7,sellDob.getFrequency());
                pStmt1.addBatch(); */
              }
              }
             else
              {

                throw new Exception("Error while Inserting into  QMS_REC_CON_SELLRATESDTL "+returnValue);
                 //System.out.println("returnValuereturnValuereturnValue dao in else :: "+returnValue);
                  //System.out.println("dao in else :: "+loginBean.getTerminalId());
              }
              
          
          pStmt.executeBatch();
          //pStmt1.executeBatch();
          pStmt3           =   connection.prepareStatement(deleteBuyRtQuery);
          //System.out.println("Rec_buyrate_idRec_buyrate_idRec_buyrate_idRec_buyrate_idRec_buyrate_id :: "+hedarDob.getRec_buyrate_id());
          pStmt3.setString(1,hedarDob.getRec_buyrate_id());
          pStmt3.setString(2,sellDob.getBuyRateId());
         
          pStmt3.setInt(3,sellDob.getLanNumber());
           pStmt3.setString(4,sellDob.getVersionNo());   //@@Added for the WPBN issue-146448 on 23/12/08
          pStmt3.executeUpdate();
           //@@Added by Kameswari for the WPBN issue-139041
          pStmt1         =   connection.prepareStatement(updateBuyRtQuery);
          pStmt1.setString(1,sellDob.getBuyRateId());
       
          pStmt1.setInt(2,sellDob.getLanNumber());
           pStmt1.setString(3,sellDob.getVersionNo());
          pStmt1.executeUpdate();
          System.out.println("******Calling DAO*********");
          //System.out.println("dao 66666666666666"+loginBean.getTerminalId());
    }
    catch(SQLException sqle)
    {
        //Logger.error(FILE_NAME,"SQLEXception in insertSlabValues(2)-->"+sqle.toString());
        logger.error(FILE_NAME+"SQLEXception in insertSlabValues(2)-->"+sqle.toString());
        sqle.printStackTrace();
        throw new EJBException(sqle.toString());
    }
    catch(Exception e)
    {
      //Logger.error(FILE_NAME,"EXception in insertSlabValues(2)-->"+e.toString());
      logger.error(FILE_NAME+"EXception in insertSlabValues(2)-->"+e.toString());
      e.printStackTrace();
      throw new EJBException(e.toString());
    }
    finally
    {
        try
        {
            if(pStmt!=null)
              {pStmt.close();}
           if(pStmt1!=null)// un commented for by VLAKSHMI for connection leakage.
             {pStmt1.close();}
            if(pStmt3!=null)
              {pStmt3.close();}
            if(csmt!=null)
              {csmt.close();}
            hedarDob=null;
        }
        catch(Exception ex)
        {
            //Logger.error(FILE_NAME,"Exception caught :: finally :: insertSlabValues() " + ex.toString() );
            logger.error(FILE_NAME+"Exception caught :: finally :: insertSlabValues() " + ex.toString() );
        }
    }
}
private void insertListValues(int sellRatesId,String operation,ArrayList listValue,ESupplyGlobalParameters loginBean,Connection connection,QMSSellRatesDOB	hedarDob)throws SQLException
{
    PreparedStatement		pStmt		 	      =   null;
    PreparedStatement		pStmt1			    =   null; //@@Added by Kameswari for the WPBN issue-139041
    PreparedStatement   pStmt3          =   null;
    CallableStatement		csmt            =   null;
    QMSSellRatesDOB		  sellDob         =   null;
    try
    {
          pStmt           =   connection.prepareStatement(sqlQuerySellDtl); 
          //pStmt1          =   connection.prepareStatement(UpdateBuyRtFlagQuery);
          int   sizeValue =   listValue.size();
          //System.out.println("sizeValuesizeValuesizeValuesizeValuesizeValuesizeValuesizeValue :: in dao of insertListValues "+sizeValue);
          String  returnValue = null;
          csmt = connection.prepareCall("{ ?= call Qms_rsr_rates_Pkg.QMS_SELL_RATE_VALIDATION(?,?,?,?,?,?,?,?,?,?,?,?,?,?)} ");
         /* for(int i=0;i<sizeValue;i++)
          {*/
              sellDob   = (QMSSellRatesDOB)listValue.get(0);
             // if(i==0)
             // {
                //System.out.println("Modify Status 2222222 List");
                csmt.clearParameters();
                csmt.registerOutParameter(1,Types.VARCHAR);
                csmt.setString(2,sellDob.getOrigin());
                csmt.setString(3,sellDob.getDestination());
                csmt.setString(4,sellDob.getServiceLevel());
                csmt.setString(5,sellDob.getCarrier_id());
                csmt.setString(6,sellDob.getFrequency());
                csmt.setString(7,loginBean.getTerminalId());
                csmt.setString(8,hedarDob.getWeightBreak());
                csmt.setString(9,hedarDob.getRateType());
                csmt.setString(10,hedarDob.getShipmentMode());
                csmt.setString(11,"ACC");
                csmt.setLong(12,sellRatesId);
                csmt.setString(13,sellDob.getBuyRateId());
                csmt.setInt(14,sellDob.getLanNumber());
               csmt.setString(15,operation); //@@Added for teh WPBN issue-146968 on 11/01/08
            
                csmt.execute(); 
                returnValue = csmt.getString(1); 
             // }
              //System.out.println("returnValuereturnValuereturnValue :: "+returnValue);
              if("6".equals(returnValue))
              {
               for(int i=0;i<sizeValue;i++)
             {
                sellDob   = (QMSSellRatesDOB)listValue.get(i);
                pStmt.setInt(1,sellRatesId);
                pStmt.setString(2,sellDob.getCarrier_id());
                pStmt.setString(3,sellDob.getServiceLevel());
                pStmt.setDouble(4,sellDob.getChargeRate());
                pStmt.setDouble(5,0.0);
                pStmt.setDouble(6,0.0);
                pStmt.setDouble(7,0.0);
                pStmt.setDouble(8,0.0);
                pStmt.setDouble(9,sellDob.getUpperBd());//Modified by Mohan for issue on 12112010
                pStmt.setDouble(10,sellDob.getLowerBd());//Modified by Mohan for issue on 12112010
                //System.out.println("sellDob.getBuyRate() in DAO : "+sellDob.getBuyRate());
                pStmt.setDouble(11,sellDob.getBuyRate());
                pStmt.setDouble(12,sellDob.getMarginPer());
                pStmt.setString(13,"A");
                pStmt.setString(14,sellDob.getNoteValue());
                pStmt.setString(15,sellDob.getMinFlat());
                pStmt.setString(16,sellDob.getFrequency());
                pStmt.setString(17,sellDob.getChargerateIndicator());
                //pStmt.setString(17,"");
                pStmt.setInt(18,sellDob.getLanNumber());
                pStmt.setInt(19,sellDob.getLineNumber());
                pStmt.setString(20,sellDob.getBuyRateId());
                pStmt.setString(21,sellDob.getTransitTime());
                pStmt.setString(22,sellDob.getOrigin());
                pStmt.setString(23,sellDob.getDestination());
                pStmt.setString(24,sellDob.getOriginCountry());
                pStmt.setString(25,sellDob.getDestinationCountry());
                pStmt.setString(26,hedarDob.getConsoleType());
                pStmt.setString(27,"F");
 /*                if("FSBASIC".equalsIgnoreCase(sellDob.getMinFlat())||"FSMIN".equalsIgnoreCase(sellDob.getMinFlat())
                       ||"FSKG".equalsIgnoreCase(sellDob.getMinFlat()))
                    pStmt.setString(28,"FUEL SURCHARGE");
                    else if("SSBASIC".equalsIgnoreCase(sellDob.getMinFlat())||"SSMIN".equalsIgnoreCase(sellDob.getMinFlat())
                       ||"SSKG".equalsIgnoreCase(sellDob.getMinFlat()))
                        pStmt.setString(28,"SECURITY SURCHARGE");
                    else if("BAFMIN".equalsIgnoreCase(sellDob.getMinFlat())||"BAFM3".equalsIgnoreCase(sellDob.getMinFlat()))
                        pStmt.setString(28,"B.A.F");
                    else if("CAFMIN".equalsIgnoreCase(sellDob.getMinFlat())||"CAF%".equalsIgnoreCase(sellDob.getMinFlat()))
                        pStmt.setString(28,"C.A.F%");
                    else if("PSSMIN".equalsIgnoreCase(sellDob.getMinFlat())||"PSSM3".equalsIgnoreCase(sellDob.getMinFlat()))
                        pStmt.setString(28,"C.P.S.S");
                    else if("CSF".equalsIgnoreCase(sellDob.getMinFlat()))
                        pStmt.setString(28,"C.S.F");
                   else if("SURCHARGE".equalsIgnoreCase(sellDob.getMinFlat()))
                        pStmt.setString(28,"SURCHARGE");
                   else 
                          pStmt.setString(28,"A FREIGHT RATE");
   */                       
                pStmt.setString(28,sellDob.getSurChargeDescription());
                           pStmt.setString(29,sellDob.getVersionNo());   //@@Added for the WPBN issue-146448 on 23/12/08
                           pStmt.setString(30,sellDob.getExtNotes());  //Modified by Mohan for Issue No.219976 on 08-10-2010
                pStmt.addBatch(); 
                /*pStmt1.setString(1,"Y");
                pStmt1.setString(2,sellDob.getBuyRateId());
                pStmt1.setString(3,sellDob.getOrigin());
                pStmt1.setString(4,sellDob.getDestination());
                pStmt1.setString(5,sellDob.getServiceLevel());
                pStmt1.setString(6,sellDob.getCarrier_id());
                pStmt1.setString(7,sellDob.getFrequency());
                pStmt1.addBatch(); */
              }
              }
             else
              {

              throw new Exception("Error while Inserting into  QMS_REC_CON_SELLRATESDTL "+returnValue);
                //System.out.println("returnValuereturnValuereturnValue dao in else :: "+returnValue);
                  //System.out.println("dao in else :: "+loginBean.getTerminalId());
              }
         
          pStmt.executeBatch();
          //pStmt1.executeBatch();
          pStmt3           =   connection.prepareStatement(deleteBuyRtQuery);
          //System.out.println("Rec_buyrate_idRec_buyrate_idRec_buyrate_idRec_buyrate_idRec_buyrate_id :: "+hedarDob.getRec_buyrate_id());
          pStmt3.setString(1,hedarDob.getRec_buyrate_id());
          pStmt3.setString(2,sellDob.getBuyRateId());
          pStmt3.setInt(3,sellDob.getLanNumber());
          pStmt3.setString(4,sellDob.getVersionNo());   //@@Added for the WPBN issue-146448 on 23/12/08
          pStmt3.executeUpdate();
           //@@Added by Kameswari for the WPBN issue-139041
          pStmt1         =   connection.prepareStatement(updateBuyRtQuery);
          pStmt1.setString(1,sellDob.getBuyRateId());
       
          pStmt1.setInt(2,sellDob.getLanNumber());
           pStmt1.setString(3,sellDob.getVersionNo());//@@Added for the WPBN issue-146448 on 23/12/08
           pStmt1.executeUpdate();
   
          //System.out.println("dao 66666666666666"+loginBean.getTerminalId());
    }
    catch(SQLException sqle)
    {
        //Logger.error(FILE_NAME,"SQLEXception in insertListValues(2)-->"+sqle.toString());
        logger.error(FILE_NAME+"SQLEXception in insertListValues(2)-->"+sqle.toString());
        sqle.printStackTrace();
        throw new EJBException(sqle.toString());
    }
    catch(Exception e)
    {
      //Logger.error(FILE_NAME,"EXception in insertListValues(2)-->"+e.toString());
      logger.error(FILE_NAME+"EXception in insertListValues(2)-->"+e.toString());
      e.printStackTrace();
      throw new EJBException(e.toString());
    }
    finally
    {
        try
        {
            if(pStmt!=null)
              {pStmt.close();}
            if(pStmt1!=null)
            {pStmt1.close();}// un commented by VLAKSHMI for  connection leakage
            if(pStmt3!=null)
              {pStmt3.close();}
            if(csmt!=null)
              {csmt.close();}
            hedarDob=null;
        }
        catch(Exception ex)
        {
            //Logger.error(FILE_NAME,"Exception caught :: finally :: insertListValues() " + ex.toString() );
            logger.error(FILE_NAME+"Exception caught :: finally :: insertListValues() " + ex.toString() );
        }
    }
}
private void insertFclValues(int sellRatesId,String operation,ArrayList listValue,ESupplyGlobalParameters loginBean,Connection connection,QMSSellRatesDOB	hedarDob)throws SQLException
{
    PreparedStatement		pStmt		 	      =   null;
    PreparedStatement		pStmt1			    =   null; //@@Added by Kameswari for the WPBN issue-139041
    PreparedStatement   pStmt3          =   null;
    CallableStatement		csmt            =   null;
    QMSSellRatesDOB		  sellDob         =   null;
    try
    {
          pStmt           =   connection.prepareStatement(sqlQuerySellDtl); 
          //pStmt1          =   connection.prepareStatement(UpdateBuyRtFlagQuery);
          int   sizeValue =   listValue.size();
          //System.out.println("sizeValuesizeValuesizeValuesizeValuesizeValuesizeValuesizeValue :: in dao of insertFclValues "+sizeValue);
          String  returnValue = null;
          csmt = connection.prepareCall("{ ?= call Qms_rsr_rates_Pkg.QMS_SELL_RATE_VALIDATION(?,?,?,?,?,?,?,?,?,?,?,?,?,?)} ");
         /* for(int i=0;i<sizeValue;i++)
          {*/
              sellDob   = (QMSSellRatesDOB)listValue.get(0);
             // if(i==0)
             // {
                //System.out.println("Modify Status 2222222 Fcl");
                csmt.clearParameters();
                csmt.registerOutParameter(1,Types.VARCHAR);
                csmt.setString(2,sellDob.getOrigin());
                csmt.setString(3,sellDob.getDestination());
                csmt.setString(4,sellDob.getServiceLevel());
                csmt.setString(5,sellDob.getCarrier_id());
                csmt.setString(6,sellDob.getFrequency());
                csmt.setString(7,loginBean.getTerminalId());
                csmt.setString(8,hedarDob.getWeightBreak());
                csmt.setString(9,hedarDob.getRateType());
                csmt.setString(10,hedarDob.getShipmentMode());
                csmt.setString(11,"ACC");
                csmt.setLong(12,sellRatesId);
                csmt.setString(13,sellDob.getBuyRateId());
                csmt.setInt(14,sellDob.getLanNumber());
                csmt.setString(15,operation); //@@Added for teh WPBN issue-146968 on 11/01/08
            
                csmt.execute(); 
                returnValue = csmt.getString(1); 
              //}
    
              if("6".equals(returnValue))
              {
              for(int i=0;i<sizeValue;i++)
            {
                sellDob   = (QMSSellRatesDOB)listValue.get(i);
                pStmt.setInt(1,sellRatesId);
                pStmt.setString(2,sellDob.getCarrier_id());
                pStmt.setString(3,sellDob.getServiceLevel());
                pStmt.setDouble(4,sellDob.getChargeRate());
                pStmt.setDouble(5,0.0);
                pStmt.setDouble(6,0.0);
                pStmt.setDouble(7,0.0);
                pStmt.setDouble(8,0.0);
                pStmt.setDouble(9,sellDob.getUpperBd());//Modified by Mohan for issue on 12112010
                pStmt.setDouble(10,sellDob.getLowerBd());//Modified by Mohan for issue on 12112010
                //System.out.println("sellDob.getBuyRate() in DAO : "+sellDob.getBuyRate());
                pStmt.setDouble(11,sellDob.getBuyRate());
                pStmt.setDouble(12,sellDob.getMarginPer());
                pStmt.setString(13,"A");
                pStmt.setString(14,sellDob.getNoteValue());
                pStmt.setString(15,sellDob.getMinFlat());
                pStmt.setString(16,sellDob.getFrequency());
                pStmt.setString(17,sellDob.getChargerateIndicator());
                //pStmt.setString(17,"");
                pStmt.setInt(18,sellDob.getLanNumber());
                pStmt.setInt(19,sellDob.getLineNumber());
                pStmt.setString(20,sellDob.getBuyRateId());
                pStmt.setString(21,sellDob.getTransitTime());
                pStmt.setString(22,sellDob.getOrigin());
                pStmt.setString(23,sellDob.getDestination());
                pStmt.setString(24,sellDob.getOriginCountry());
                pStmt.setString(25,sellDob.getDestinationCountry());
                pStmt.setString(26,hedarDob.getConsoleType());
                pStmt.setString(27,"F");
                
                if(sellDob.getMinFlat().endsWith("BAF"))
                     pStmt.setString(28,"BAF");
                 else if(sellDob.getMinFlat().endsWith("CAF"))
                      pStmt.setString(28,"CAF%");
                  else if(sellDob.getMinFlat().endsWith("CSF"))
                      pStmt.setString(28,"CSF");
                  else if(sellDob.getMinFlat().endsWith("PSS"))
                      pStmt.setString(28,"PSS");
                  else
                      pStmt.setString(28,sellDob.getSurChargeDescription()!=null?sellDob.getSurChargeDescription():"A FREIGHT RATE");
                      
                       pStmt.setString(29,sellDob.getVersionNo());   //@@Added for the WPBN issue-146448 on 23/12/08
                       pStmt.setString(30,sellDob.getExtNotes());  //Modified by Mohan for Issue No.219976 on 08-10-2010
               pStmt.addBatch(); 
               /* pStmt1.setString(1,"Y");
                pStmt1.setString(2,sellDob.getBuyRateId());
                pStmt1.setString(3,sellDob.getOrigin());
                pStmt1.setString(4,sellDob.getDestination());
                pStmt1.setString(5,sellDob.getServiceLevel());
                pStmt1.setString(6,sellDob.getCarrier_id());
                pStmt1.setString(7,sellDob.getFrequency());
                pStmt1.addBatch(); */
                }
              }
              else
              {

              
              throw new Exception("Error while Inserting into  QMS_REC_CON_SELLRATESDTL "+returnValue);
          //System.out.println("returnValuereturnValuereturnValue dao in else :: "+returnValue);
                  //System.out.println("dao in else :: "+loginBean.getTerminalId());
              }
              
          
          pStmt.executeBatch();
          //pStmt1.executeBatch();
          pStmt3           =   connection.prepareStatement(deleteBuyRtQuery);
          //System.out.println("Rec_buyrate_idRec_buyrate_idRec_buyrate_idRec_buyrate_idRec_buyrate_id :: "+hedarDob.getRec_buyrate_id());
          pStmt3.setString(1,hedarDob.getRec_buyrate_id());
          pStmt3.setString(2,sellDob.getBuyRateId());
          pStmt3.setInt(3,sellDob.getLanNumber());
         pStmt3.setString(4,sellDob.getVersionNo());   //@@Added for the WPBN issue-146448 on 23/12/08
          pStmt3.executeUpdate();
           //@@Added by Kameswari for the WPBN issue-139041
          pStmt1         =   connection.prepareStatement(updateBuyRtQuery);
          pStmt1.setString(1,sellDob.getBuyRateId());
       
          pStmt1.setInt(2,sellDob.getLanNumber());
          pStmt1.setString(3,sellDob.getVersionNo());//@@Added for the WPBN issue-146448 on 23/12/08
           pStmt1.executeUpdate();
          System.out.println("******Calling DAO*********");
          //System.out.println("dao 66666666666666"+loginBean.getTerminalId());
    }
    catch(SQLException sqle)
    {
        //Logger.error(FILE_NAME,"SQLEXception in insertFclValues(2)-->"+sqle.toString());
        logger.error(FILE_NAME+"SQLEXception in insertFclValues(2)-->"+sqle.toString());
        sqle.printStackTrace();
        throw new EJBException(sqle.toString());
    }
    catch(Exception e)
    {
      //Logger.error(FILE_NAME,"EXception in insertFclValues(2)-->"+e.toString());
      logger.error(FILE_NAME+"EXception in insertFclValues(2)-->"+e.toString());
      e.printStackTrace();
      throw new EJBException(e.toString());
    }
    finally
    {
        try
        {
            if(pStmt!=null)
              {pStmt.close();}
            if(pStmt1!=null)// un commented by VLAKSHMI for connection leakage
            {pStmt1.close();}
            if(pStmt3!=null)
              {pStmt3.close();}
            if(csmt!=null)
              {csmt.close();}
            hedarDob=null;
        }
        catch(Exception ex)
        {
            //Logger.error(FILE_NAME,"Exception caught :: finally :: insertFclValues() " + ex.toString() );
            logger.error(FILE_NAME+"Exception caught :: finally :: insertFclValues() " + ex.toString() );
        }
    }
}
   /**
   * 
   * @throws java.sql.SQLException
   * @param dataList
   */
    public void create(ArrayList dataList)throws SQLException
    {
      try
      {
        insertSellRateDetails(dataList);
      }
      catch(SQLException e)
      {
        //Logger.error(FILE_NAME,"SQLException in create(ArrayList param0) method"+e.toString());
        logger.error(FILE_NAME+"SQLException in create(ArrayList param0) method"+e.toString());
        throw new SQLException(e.toString());         
      }
      catch(Exception e)
      {
        //Logger.error(FILE_NAME,"Exception in create(ArrayList param0) method"+e.toString());
        logger.error(FILE_NAME+"Exception in create(ArrayList param0) method"+e.toString());
        throw new SQLException(e.toString());         
      }
    }
    
  public ArrayList load(QMSSellRatesDOB sellRateDetails)
  {
      ArrayList buyRateDetails  =  null;
      try
      {
        buyRateDetails  = loadRateDetails(sellRateDetails);
      }
      catch(Exception e)
      {
        //Logger.error(FILE_NAME,"Exception in QMSSellRatesDAO.load(param0) method"+e.toString());
        logger.error(FILE_NAME+"Exception in QMSSellRatesDAO.load(param0) method"+e.toString());
        e.printStackTrace();
        throw new EJBException();         
      }
    return buyRateDetails;
  }
  
  public void store(QMSSellRatesDOB sellRateDetails)
  {
    
  }
  
  private void insertSellRateDetails(ArrayList dataList) throws SQLException
  {
    PreparedStatement[] pStmt         = null;
    CallableStatement cstmt           = null;
    CallableStatement cstmt1          = null;
    Statement         stmt            = null;
    Statement         stmt1           = null;
    ResultSet         rs1             = null;
    ResultSet         rs              = null;
    QMSSellRatesDOB   sellRates       = null;
    QMSSellRatesDOB   sellRatesMaster = null;
    long              csr_Id          = 0;
    HashMap           slabRates       = null;
    QMSSellRatesDOB   sellRatesDtl    = null;
    
    String            sqlQuery        = "";
    String            inactivateQuery = "";
    String            whereCondition  = "";
    String            terminalQry     = "";
    String            terminalWhere   = "";
    String            changeDesc      = "";
    String            shipmentModeStr = "";
    String            frequencyQry    = "";
    String            quoteUpdateQry  = "";
    
    try
    {
      getConnection();
      pStmt    = new PreparedStatement[4];
      pStmt[0] = connection.prepareStatement(sqlQuerySellMaster);
      pStmt[1] = connection.prepareStatement(sqlQuerySellDtl);
      pStmt[2] = connection.prepareStatement(sqlQueryUserlog);
      //pStmt[4] = connection.prepareStatement(UpdateBuyRtFlagQuery);
      
      
      sellRatesMaster = (QMSSellRatesDOB)dataList.get(0);
      stmt            = connection.createStatement();
      rs              = stmt.executeQuery(sqlQuery1);
        
      if(rs.next())
          csr_Id = rs.getInt("NEXTVAL");
      
        pStmt[0].setLong(1,csr_Id);
        pStmt[0].setString(2,sellRatesMaster.getRec_cons_flag());
        pStmt[0].setString(3,sellRatesMaster.getShipmentMode());
        pStmt[0].setString(4,sellRatesMaster.getWeightBreak());
        pStmt[0].setString(5,sellRatesMaster.getRateType());
        pStmt[0].setString(6,sellRatesMaster.getCurrencyId());
        pStmt[0].setString(7,sellRatesMaster.getWeightClass());
        pStmt[0].setString(8,sellRatesMaster.getOverAllMargin());
        pStmt[0].setString(9,sellRatesMaster.getMarginType());
        pStmt[0].setString(10,sellRatesMaster.getMarginBasis());
        pStmt[0].setString(11,sellRatesMaster.getCreatedBy());
        pStmt[0].setTimestamp(12,sellRatesMaster.getCreatedTimestamp());
        pStmt[0].setString(13,sellRatesMaster.getLastUpdatedBy());
        pStmt[0].setTimestamp(14,sellRatesMaster.getLastUpdatedTimestamp());
        pStmt[0].setString(15,sellRatesMaster.getActiveInactiveFlag());
        pStmt[0].setString(16,sellRatesMaster.getAccessLevel());
        pStmt[0].setString(17,sellRatesMaster.getTerminalId());

        pStmt[0].executeUpdate();
        
        if("1".equalsIgnoreCase(sellRatesMaster.getShipmentMode()))
            shipmentModeStr = "Air";
        else if("2".equalsIgnoreCase(sellRatesMaster.getShipmentMode()))
            shipmentModeStr = "Sea";
        else
            shipmentModeStr = "Truck";
        
        
        if("H".equalsIgnoreCase(sellRatesMaster.getAccessLevel()))
            terminalQry   = " AND MAS.TERMINALID IN (SELECT TERMINALID FROM FS_FR_TERMINALMASTER) ";
        else
          terminalQry =   " AND MAS.TERMINALID IN (SELECT CHILD_TERMINAL_ID TERM_ID FROM FS_FR_TERMINAL_REGN CONNECT BY "+
                          "PRIOR CHILD_TERMINAL_ID=PARENT_TERMINAL_ID START WITH PARENT_TERMINAL_ID ='"+
                          sellRatesMaster.getTerminalId()+"' "+
                          "UNION SELECT '"+sellRatesMaster.getTerminalId()+"' FROM DUAL) ";
        
        if("Add".equalsIgnoreCase(sellRatesMaster.getOperation()))
        {
          inactivateQuery = " UPDATE QMS_REC_CON_SELLRATESDTL DTL SET AI_FLAG=? WHERE BUYRATEID=? "+
                          " AND REC_CON_ID IN (SELECT DTL.REC_CON_ID FROM QMS_REC_CON_SELLRATESMASTER MAS "+
                          " WHERE MAS.RC_FLAG='C' AND MAS.REC_CON_ID=DTL.REC_CON_ID"+
                          " AND DTL.BUYRATEID=? AND DTL.LANE_NO=? AND DTL.AI_FLAG='A' "+terminalQry+")";
        }
        else if("Modify".equalsIgnoreCase(sellRatesMaster.getOperation()))
        {
          inactivateQuery = " UPDATE QMS_REC_CON_SELLRATESDTL DTL SET AI_FLAG=? WHERE BUYRATEID=? "+
                          " AND REC_CON_ID IN (SELECT DTL.REC_CON_ID FROM QMS_REC_CON_SELLRATESMASTER MAS "+
                          " WHERE MAS.RC_FLAG='C' AND MAS.REC_CON_ID=DTL.REC_CON_ID"+
                          " AND DTL.BUYRATEID=? AND DTL.LANE_NO=? AND DTL.REC_CON_ID=? AND DTL.AI_FLAG='A' "+terminalQry+")";
        }
      
 
      pStmt[3] = connection.prepareStatement(inactivateQuery);
      cstmt    = connection.prepareCall("call QMS_QUOTEPACK_NEW.qms_quote_update(?,?,?,?,?,?,?,?,?,?,?,?)");
      int dListSize	= dataList.size();
      for(int i=1;i<=(dListSize-1);i++)
      {        
        sellRates = (QMSSellRatesDOB)dataList.get(i);//@@This DOB Contains details other than Slab Values
        slabRates = (HashMap)sellRates.getSellRates();
        
        Collection values = slabRates.values();  
        Iterator itr = values.iterator();
        //@@Invalidating Existing Record(s) if existing
        if("Add".equalsIgnoreCase(sellRatesMaster.getOperation()))
        {
          cstmt1        = connection.prepareCall("{ ?= call Pkg_Qms_Buyrates.comb_freq_func(?) }");
          cstmt1.registerOutParameter(1,OracleTypes.VARCHAR);
        
            
          if(sellRates.getFrequency().indexOf(",")!=-1)
            cstmt1.setString(2,sellRates.getFrequency().replaceAll(",","~"));
          else
            cstmt1.setString(2,sellRates.getFrequency());
          
          cstmt1.execute();
          
          frequencyQry  = cstmt1.getString(1);
          
          if(cstmt1!=null)
              cstmt1.close();
          
          if("H".equalsIgnoreCase(sellRatesMaster.getAccessLevel()))
              terminalWhere = " AND ID = (SELECT MAX(ID) FROM QMS_REC_CON_SELLRATESMASTER SUBMAS WHERE SUBMAS.REC_CON_ID = DTL.REC_CON_ID "+
                              " AND MAS.TERMINALID = SUBMAS.TERMINALID )";
          else
              terminalWhere = "AND MAS.TERMINALID IN (SELECT CHILD_TERMINAL_ID TERM_ID FROM FS_FR_TERMINAL_REGN CONNECT BY "+
                              "PRIOR CHILD_TERMINAL_ID=PARENT_TERMINAL_ID START WITH PARENT_TERMINAL_ID ='"+
                              sellRatesMaster.getTerminalId()+"' "+
                              "UNION SELECT '"+sellRatesMaster.getTerminalId()+"' FROM DUAL UNION ALL SELECT TERMINALID FROM FS_FR_TERMINALMASTER WHERE OPER_ADMIN_FLAG = 'H')";
          
          quoteUpdateQry  = " SELECT DISTINCT DTL.REC_CON_ID, BUYRATEID, LANE_NO ,VERSION_NO FROM QMS_REC_CON_SELLRATESDTL DTL, QMS_REC_CON_SELLRATESMASTER MAS "+
                            " WHERE MAS.REC_CON_ID = DTL.REC_CON_ID AND ORIGIN = '"+sellRatesMaster.getOrigin()+"' AND "+
                            " DESTINATION = '"+sellRatesMaster.getDestination()+"' AND "+
                            frequencyQry+ " AND SERVICELEVEL_ID IN '"+sellRates.getServiceLevel()+"' AND CARRIER_ID='"+sellRates.getCarrier_id()+"' "+
                            terminalWhere+" AND (DTL.REC_CON_ID, BUYRATEID, LANE_NO) IN ( SELECT NEW_SELLCHARGE_ID, NEW_BUYCHARGE_ID, NEW_LANE_NO "+
                            " FROM QMS_QUOTES_UPDATED WHERE SELL_BUY_FLAG = 'CSR')";
                            
          stmt1           =  connection.createStatement();
          rs1             =  stmt1.executeQuery(quoteUpdateQry);
          
          while(rs1.next())
          {
            changeDesc  = sellRatesMaster.getOrigin()+"-"+sellRatesMaster.getDestination()+","+shipmentModeStr+" Freight Rates and Surcharges";
            cstmt.setLong(1,rs1.getLong("REC_CON_ID"));
            cstmt.setLong(2,rs1.getLong("BUYRATEID"));
            cstmt.setInt(3,rs1.getInt("LANE_NO"));
             cstmt.setInt(4,rs1.getInt("VERSION_NO"));
            cstmt.setInt(5,Integer.parseInt(sellRates.getVersionNo()));
            cstmt.setLong(6,csr_Id);
            cstmt.setLong(7,Long.parseLong(sellRates.getBuyRateId()));
            cstmt.setInt(8,sellRates.getLaneNumber());
            cstmt.setString(9,"CSR");
            cstmt.setNull(10,Types.VARCHAR);
            cstmt.setNull(11,Types.VARCHAR);
            cstmt.setString(12,changeDesc);
            
            cstmt.addBatch();
          }
          
          if(rs1 != null)
            rs1.close();
          if(stmt1!=null)
            stmt1.close();
          
        }
        else if("Modify".equalsIgnoreCase(sellRatesMaster.getOperation()))
        {
          changeDesc  = sellRatesMaster.getOrigin()+"-"+sellRatesMaster.getDestination()+","+shipmentModeStr+" Freight Rates";
          cstmt.setLong(1,sellRates.getSellRateId());
          cstmt.setLong(2,Long.parseLong(sellRates.getBuyRateId()));
          cstmt.setInt(3,sellRates.getLaneNumber());
           cstmt.setString(4,sellRates.getVersionNo());
            cstmt.setString(5,sellRates.getVersionNo());
          cstmt.setLong(6,csr_Id);
          cstmt.setLong(7,Long.parseLong(sellRates.getBuyRateId()));
          cstmt.setInt(8,sellRates.getLaneNumber());
          cstmt.setString(9,"CSR");
          cstmt.setNull(10,Types.VARCHAR);
          cstmt.setNull(11,Types.VARCHAR);
          cstmt.setString(12,changeDesc);
          
          cstmt.addBatch();
        }
        //@@
        pStmt[3].setString(1,"I");
        pStmt[3].setString(2,sellRates.getBuyRateId());
        pStmt[3].setString(3,sellRates.getBuyRateId());
        pStmt[3].setInt(4,sellRates.getLaneNumber());
        
        if("Modify".equalsIgnoreCase(sellRatesMaster.getOperation()))
            pStmt[3].setLong(5,sellRates.getSellRateId());
          
        pStmt[3].addBatch();
        
        pStmt[3].clearParameters();
        
        while(itr.hasNext())
        {          
          sellRatesDtl  = (QMSSellRatesDOB)itr.next();//@@This DOB Contains Slab Values
          
          pStmt[1].setLong(1,csr_Id);
          pStmt[1].setString(2,sellRates.getCarrier_id());
          //pStmt[1].setString(3,sellRates.getServiceLevel());
          pStmt[1].setString(3,sellRatesDtl.getServiceLevel());
          pStmt[1].setDouble(4,sellRatesDtl.getChargeRate());
          pStmt[1].setDouble(5,sellRates.getPalletCapacity());
          pStmt[1].setDouble(6,sellRates.getPalletBuyRate());
          pStmt[1].setDouble(7,sellRates.getAverageUplift());
          pStmt[1].setDouble(8,sellRates.getLoseSpace());
          pStmt[1].setLong(9,sellRatesDtl.getUpperBound());
          pStmt[1].setLong(10,sellRatesDtl.getLowerBound());
          pStmt[1].setDouble(11,sellRatesDtl.getBuyRate());
          pStmt[1].setDouble(12,sellRatesDtl.getMarginPer());
          pStmt[1].setString(13,"A");
          pStmt[1].setString(14,sellRates.getNotes());
          pStmt[1].setString(15,sellRatesDtl.getWeightBreakSlab());
          pStmt[1].setString(16,sellRates.getFrequency());
          pStmt[1].setString(17,sellRatesDtl.getChargeRateInd());
          pStmt[1].setInt(18,sellRates.getLaneNumber());
          pStmt[1].setInt(19,sellRatesDtl.getLineNumber());
          pStmt[1].setString(20,sellRates.getBuyRateId());
          pStmt[1].setString(21,sellRates.getTransitTime());
          pStmt[1].setString(22,sellRatesMaster.getOrigin());
          pStmt[1].setString(23,sellRatesMaster.getDestination());
          pStmt[1].setString(24,sellRatesMaster.getOriginCountry());
          pStmt[1].setString(25,sellRatesMaster.getDestinationCountry());
          pStmt[1].setString(26,sellRates.getConsoleType());
          pStmt[1].setString(27,"");
          if("FSBASIC".equalsIgnoreCase(sellRatesDtl.getWeightBreakSlab())||"FSMIN".equalsIgnoreCase(sellRatesDtl.getWeightBreakSlab())
               ||"FSKG".equalsIgnoreCase(sellRatesDtl.getWeightBreakSlab()))
               pStmt[1].setString(28,"FUEL SURCHARGE");
            else if("SSBASIC".equalsIgnoreCase(sellRatesDtl.getWeightBreakSlab())||"SSMIN".equalsIgnoreCase(sellRatesDtl.getWeightBreakSlab())
               ||"SSKG".equalsIgnoreCase(sellRatesDtl.getWeightBreakSlab()))
                pStmt[1].setString(28,"SECURITY SURCHARGE");
            else if("BAFMIN".equalsIgnoreCase(sellRatesDtl.getWeightBreakSlab())||"BAFM3".equalsIgnoreCase(sellRatesDtl.getWeightBreakSlab()))
                pStmt[1].setString(28,"B.A.F");
            else if("CAFMIN".equalsIgnoreCase(sellRatesDtl.getWeightBreakSlab())||"CAF%".equalsIgnoreCase(sellRatesDtl.getWeightBreakSlab()))
                pStmt[1].setString(28,"C.A.F%");
            else if("PSSMIN".equalsIgnoreCase(sellRatesDtl.getWeightBreakSlab())||"PSSM3".equalsIgnoreCase(sellRatesDtl.getWeightBreakSlab()))
                pStmt[1].setString(28,"C.P.S.S");
            else if("CSF".equalsIgnoreCase(sellRatesDtl.getWeightBreakSlab()))
                pStmt[1].setString(28,"C.S.F");
            else if("SURCHARGE".equalsIgnoreCase(sellRatesDtl.getWeightBreakSlab()))
                pStmt[1].setString(28,"SURCHARGE");
            else if(sellRatesDtl.getWeightBreakSlab().endsWith("BAF"))
               pStmt[1].setString(28,"BAF");
           else if(sellRatesDtl.getWeightBreakSlab().endsWith("CAF"))
                pStmt[1].setString(28,"CAF%");
            else if(sellRatesDtl.getWeightBreakSlab().endsWith("CSF"))
                pStmt[1].setString(28,"CSF");
            else if(sellRatesDtl.getWeightBreakSlab().endsWith("PSS"))
                pStmt[1].setString(28,"PSS");
            else
                pStmt[1].setString(28,"A FREIGHT RATE");
                 pStmt[1].setString(29,sellRates.getVersionNo());//@@Added by Kameswari for the PWBN issue-146448 on 03/01/09
                 pStmt[1].setString(30,sellRates.getExtNotes());  //Modified by Mohan for Issue No.219976 on 08-10-2010
          pStmt[1].addBatch();
          
          pStmt[1].clearParameters();
        }
        
        //pStmt[1].executeBatch();

		  //@@Setting a flag in buy rates dtl to indicate sell rates have been generated.
		  /*pStmt[4].setString(1,"Y");
		  pStmt[4].setString(2,sellRates.getBuyRateId());
		  pStmt[4].setString(3,sellRatesMaster.getOrigin());
		  pStmt[4].setString(4,sellRatesMaster.getDestination());
		  pStmt[4].setString(5,sellRatesMaster.getServiceLevel());
		  pStmt[4].setString(6,sellRates.getCarrier_id());
		  pStmt[4].setString(7,sellRates.getFrequency());
		  pStmt[4].addBatch();
      pStmt[4].clearParameters();*/
		  //@@End
      }
      
      cstmt.executeBatch();//@@First Update Quote
      pStmt[3].executeBatch();//@@Next Inactivate Previous Rates
      pStmt[1].executeBatch();//@@Then Insert New Rates
     // pStmt[4].executeBatch();
      
      
      pStmt[2].setString(1,sellRatesMaster.getTerminalId());
      pStmt[2].setString(2,sellRatesMaster.getCreatedBy());//@@Same as loginbean.getUserId();
      pStmt[2].setString(3,"Consolidated Sell Rates");
      pStmt[2].setString(4,""+csr_Id);
      pStmt[2].setTimestamp(5,sellRatesMaster.getCreatedTimestamp());
      pStmt[2].setString(6,sellRatesMaster.getOperation());
      
      pStmt[2].executeUpdate(); 
      
    }
    catch (SQLException sql)
    {
      //Logger.error(FILE_NAME,"SQLException while inserting the records "+sql);
      logger.error(FILE_NAME+"SQLException while inserting the records "+sql);
      sql.printStackTrace();
      throw new SQLException(sql.toString());
    }
    catch(Exception e)
    {
      //Logger.error(FILE_NAME,"Error while inserting records"+e.toString());
      logger.error(FILE_NAME+"Error while inserting records"+e.toString());
      e.printStackTrace();
      throw new SQLException(e.toString());
    }
    finally
    {
      try
      {
          if(rs!=null)
            { rs.close();}
          if(rs1!=null)
            { rs1.close();}
          if(stmt!=null)
          {stmt.close();}
          if(stmt1!=null)
          {stmt1.close();}
          if(cstmt!=null)
            cstmt.close();
          if(cstmt1!=null)
            cstmt1.close();
          if(pStmt[0]!=null)
            { pStmt[0].close();}
          if(pStmt[1]!=null)
          { pStmt[1].close();}
          if(pStmt[2]!=null)
          { pStmt[2].close();}
          if(pStmt[3]!=null)
          { pStmt[3].close();}
          ConnectionUtil.closeConnection(connection, pStmt);
          if(connection!=null)
            { connection.close();}
          
          sellRatesMaster = null;
          sellRatesDtl    = null;
          sellRates       = null;
          slabRates       = null;
          dataList        = null;
      }
      catch(Exception e)
      {
         //Logger.error(FILE_NAME,"Exception in closing resources (1 param) method"+e.toString());
         logger.error(FILE_NAME+"Exception in closing resources (1 param) method"+e.toString());
      }
    }
  }
  private ArrayList loadRateDetails(QMSSellRatesDOB sellRateDetails)
  {
   
    return null;
  }
public ArrayList getAcceptanceRateDetails(QMSSellRatesDOB sellRatesDob,ESupplyGlobalParameters loginBean,String operation)throws EJBException
{
      CallableStatement csmt              =   null;
      ResultSet         rs                =   null;
      ArrayList         dataList          =   new ArrayList();
      ArrayList         finalList         =   new ArrayList();
      String            nameValue         =   "MADHU";
      String            chargerates       =   null;
      String            lbound1            =   null;
      String            ubound1           =   null;
      String            chargeIn          =   null;
      String            weightBreak       =   null;
      String            persentage        =   null;
      QMSSellRatesDOB   qmsSellRatesDob   =   null;
      String[]          weigthBreaksArray =   null;
      String[]          chargeRatesArray  =   null;
      String[]          lowerBoundArry    =   null;
      String[]          upperBoundArry    =   null;
      String[]          chargeIndicator   =   null;
      String[]          persentValuesArry =   null;
      String[]            rateDescription   = null;   
       String             rateDesc          = null;  
       String[]           charge          = null;
      try
      {
      
          getConnection();
          csmt = connection.prepareCall("{CALL Qms_Rsr_Rates_Pkg_acceptance.comman_proc(?,?,?,?,?,?,?,?,?,?,?)} ");//modified  by silpa for rsr origin filter for 
          csmt.setString(1,sellRatesDob.getShipmentMode());
          csmt.setString(2,sellRatesDob.getConsoleType());
          csmt.setString(3,loginBean.getTerminalId());
          csmt.setInt(4,Integer.parseInt(sellRatesDob.getPageNo()));
          //csmt.setInt(5,40);
        csmt.setInt(5,100);//@@Modfied for the WPBN issue-120266
          csmt.setString(6,sellRatesDob.getSortBy());
          csmt.setString(7,sellRatesDob.getSortOrder());
          csmt.registerOutParameter(8,OracleTypes.INTEGER);
          csmt.registerOutParameter(9,OracleTypes.INTEGER);
          csmt.setString(10,sellRatesDob.getOrigin());
          csmt.registerOutParameter(11,OracleTypes.CURSOR);
          
          csmt.execute();
          rs = (ResultSet)csmt.getObject(11);
          System.out.println("rs-----"+rs);
          while(rs.next())
          {
            qmsSellRatesDob = new QMSSellRatesDOB();
            //qmsSellRatesDob.setWeightBreak("SLAB");
            qmsSellRatesDob.setWeightBreak(rs.getString("WEIGHT_BREAK"));
            //qmsSellRatesDob.setRateType("FLAT");
            qmsSellRatesDob.setRateType(rs.getString("RATE_TYPE"));
            //qmsSellRatesDob.setShipmentMode(rs.getString(""));
            //qmsSellRatesDob.setWeightClass("G");
            qmsSellRatesDob.setWeightClass(rs.getString("WT_CLASS"));
            //qmsSellRatesDob.setBuyRateId("2685");
            qmsSellRatesDob.setBuyRateId(rs.getString("BUYRATEID"));
            qmsSellRatesDob.setVersionNo(rs.getString("VERSION_NO"));//@@Added for the WPBN issue-146448 on 23/12/08
            
            qmsSellRatesDob.setVersionNo(rs.getString("VERSION_NO"));//@@Added for the WPBN issue-146448 on 23/12/08
            //qmsSellRatesDob.setCarrier_id("C2");
            qmsSellRatesDob.setCarrier_id(rs.getString("CARRIER_ID"));
            //qmsSellRatesDob.setCurrencyId("ALL");
            qmsSellRatesDob.setCurrencyId(rs.getString("CURRENCY"));
            //qmsSellRatesDob.setOrigin("ACA");
            qmsSellRatesDob.setOrigin(rs.getString("ORIGIN"));
            //qmsSellRatesDob.setOriginCountry("NZ");
            qmsSellRatesDob.setOriginCountry(rs.getString("ORG_COUNTRYID"));
            //qmsSellRatesDob.setDestination("ALY");
            qmsSellRatesDob.setDestination(rs.getString("DESTINATION"));
            //qmsSellRatesDob.setDestinationCountry("SG");
            qmsSellRatesDob.setDestinationCountry(rs.getString("DEST_COUNTRYID"));
            //qmsSellRatesDob.setServiceLevel("IE");
            qmsSellRatesDob.setServiceLevel(rs.getString("SERVICE_LEVEL"));
            //qmsSellRatesDob.setTransitTime("12:00");
            qmsSellRatesDob.setTransitTime(rs.getString("TRANSIT_TIME"));
            //qmsSellRatesDob.setFrequency("1,2,3");
            qmsSellRatesDob.setFrequency(rs.getString("FREQUENCY"));
            //qmsSellRatesDob.setLanNumber(0);
            qmsSellRatesDob.setLanNumber(rs.getInt("LANE_NO"));
            qmsSellRatesDob.setRec_buyrate_id(rs.getString("REC_BUYRATE_ID"));
                qmsSellRatesDob.setNotes(rs.getString("NOTES"));//@@ Added by subrahmanyam for the wpbn id: 179985 on 19-aug-09
                qmsSellRatesDob.setExtNotes(rs.getString("EXTERNAL_NOTES")); //Modified by Mohan for Issue No.219976 on 08-10-2010
           /*  int count=1 ,count1=0,count2=0;
              String s1        =  "";
              String s2        =  "";
              String s3        =  "";
            chargerates      = rs.getString("CHARGERATE");
             chargeRatesArray = chargerates.split(",");
            chargeIn        = rs.getString("C_INDICATOR");
            chargeIndicator = chargeIn.split(",");
            qmsSellRatesDob.setChargeInr(chargeIndicator);  
           rateDesc        = rs.getString("RATE_DESCRIPTION");
            if(rateDesc!=null)
            {
            rateDescription = rateDesc.split(",");
            }
            for(int k=0;k<rateDescription.length;k++)
            {
              if("C.P.S.S".equalsIgnoreCase(rateDescription[k]))
              {
                rateDescription[k]  = "P.S.S";
              }
            }
        //  qmsSellRatesDob.setRateDescription(rateDescription);
          weightBreak      = rs.getString("WEIGHT_BREAK_SLAB");
          weigthBreaksArray = weightBreak.split(",");*/
              int count=0 ,count1=0,icount = 0,count2=0 ,count4=0;
              String s1        =  "";
              String s2        =  "";
              String s3        =  "";
              String s4 	   =  "";
              String s5		   = "";
              String s6		   = "";
              String s7		   = "";
              String temp      =  "";
          //weightBreaks = rs.getString("WEIGHTBREAKS");
             weightBreak          =   rs.getString("WEIGHT_BREAK_SLAB");
             weigthBreaksArray     =   weightBreak.split(",");
             chargerates           =   rs.getString("CHARGERATE");
             chargeRatesArray      =   chargerates.split(",");
             chargeIn              =   rs.getString("C_INDICATOR");
             chargeIndicator       = new String[weigthBreaksArray.length];
             if(chargeIn!=null)
             {
                 charge      =   chargeIn.split(",");
             }
             if(charge!=null)
             {
            	 int chargLen	=	charge.length;
               for(int r=0;r<chargLen;r++)
               {
                 if(charge[r]!=null)
                 {
                 count4++;
                 chargeIndicator[r] =charge[r];
                 }
               }
             }
             int wBrkArrLen	=	weigthBreaksArray.length;
             for(int j=count4;j<wBrkArrLen;j++)
             {
                chargeIndicator[j]  = "";
             
             }
              //String temp      =  "";
              String[]  weightbreaks          =   new String[weigthBreaksArray.length];  
             String[]  ratebreaks             =   new String[weigthBreaksArray.length];  
             String[]  chargeindicators       =   new String[weigthBreaksArray.length];  
             String[]  breaks                 =   new String[weigthBreaksArray.length];   
             String[]  rates                  =   new String[weigthBreaksArray.length];   
             String[]  indicators             =   new String[weigthBreaksArray.length];   
             String[]  ratedescs              =   new String[weigthBreaksArray.length]; 
             String[]  ratedescription        =   new String[weigthBreaksArray.length]; 
             String[]  lbound                 =   new String[weigthBreaksArray.length]; 
             String[]   ubound                =   new String[weigthBreaksArray.length]; 
             String[]  lboundArr                 =   new String[weigthBreaksArray.length]; 
             String[]   uboundArr                =   new String[weigthBreaksArray.length];
          rateDesc        = rs.getString("RATE_DESCRIPTION");
          
            
            if(rateDesc!=null)
            {
            rateDescription = rateDesc.split(",");
            }
            if(rateDescription!=null)
            {
            	int rtDesLen	=	rateDescription.length;
              for(int k=0;k<rtDesLen;k++)
              {
                if("C.P.S.S".equalsIgnoreCase(rateDescription[k]))
                {
                  rateDescription[k]  = "P.S.S";
                }
              }
            }
            lbound1          = rs.getString("LBOUND");
            //lbound            = "0,0,20,30";
            lowerBoundArry  = lbound1.split(",");
            qmsSellRatesDob.setLBound(lowerBoundArry);
           ubound1          = rs.getString("UBOUND");
           //ubound            = "0,19,29,1000";
            upperBoundArry  = ubound1.split(",");
            qmsSellRatesDob.setUBound(upperBoundArry);
      if("SLAB".equalsIgnoreCase(sellRatesDob.getWeightBreak()))
      {
             for(int n=0;n<wBrkArrLen;n++)
             {
                if(weigthBreaksArray[n]!=null)
                {
//                  if(weigthBreaksArray[n].startsWith("M"))
                if("BASIC".equalsIgnoreCase(weigthBreaksArray[n]) && ("A FREIGHT RATE".equalsIgnoreCase(rateDescription[n])||"-".equalsIgnoreCase(rateDescription[n])))
                {
                    s6   =   weigthBreaksArray[n];
                    s7 =     chargeRatesArray[n];

                	count++;
                }
                else if("MIN".equalsIgnoreCase(weigthBreaksArray[n]) && ("A FREIGHT RATE".equalsIgnoreCase(rateDescription[n])||"-".equalsIgnoreCase(rateDescription[n])))
                {
                     s1   =   weigthBreaksArray[n];
                     s2 =     chargeRatesArray[n];
                     s3 =     chargeIndicator[n];
                     s4 =     lowerBoundArry[n];
                     s5 =     upperBoundArry[n];
                     count++;
                }
 /*               else if(weigthBreaksArray[n].startsWith("S")||weigthBreaksArray[n].startsWith("F")||
                weigthBreaksArray[n].startsWith("B")||weigthBreaksArray[n].startsWith("C")||weigthBreaksArray[n].startsWith("P"))
*/				
                else if(!"A FREIGH RATE".equalsIgnoreCase(rateDescription[n]))
                {
                   weightbreaks[count2]       =   weigthBreaksArray[n];
                   lbound[count2]             =   lowerBoundArry[n];
                    ubound[count2]            =   upperBoundArry[n];
                  
                     ratedescs[count2]        =   rateDescription[n];
                  if(chargeRatesArray[n]!=null)
                   {
                        ratebreaks[count2]          =   chargeRatesArray[n];
                   }
                   if(chargeIndicator[n]!=null)
                   {
                      chargeindicators[count2]    =   chargeIndicator[n];
                   }
                  count2++;
                }
                 else
                  {
                     breaks[count1]           =   weigthBreaksArray[n];
                     
                     if(chargeRatesArray[n]!=null)
                     {
                        rates[count1]         =   chargeRatesArray[n];
                     }
                     if(chargeIndicator[n]!=null)
                     {
                        indicators[count1]    =   chargeIndicator[n];
                     }
                     else
                     {
                        indicators[count1]    = "";
                     }
                     
                     lboundArr[count1]        =   lowerBoundArry[n];
                     uboundArr[count1]        =   upperBoundArry[n];
                     
                     count1++;
                  }
                }
            }
            if(weightbreaks!=null)
            {
               for(int j=0;j<weightbreaks.length-1;j++)
             {
                if(("FSKG".equalsIgnoreCase(weightbreaks[j])&&"FSMIN".equalsIgnoreCase(weightbreaks[j+1]))||("SSKG".equalsIgnoreCase(weightbreaks[j])&&"SSMIN".equalsIgnoreCase(weightbreaks[j+1]))
                ||("CAF%".equalsIgnoreCase(weightbreaks[j])&&"CAFMIN".equalsIgnoreCase(weightbreaks[j+1]))||"BAFM3".equalsIgnoreCase(weightbreaks[j])&&"BAFMIN".equalsIgnoreCase(weightbreaks[j+1])
                ||"PSSM3".equalsIgnoreCase(weightbreaks[j])&&"PSSMIN".equalsIgnoreCase(weightbreaks[j+1]))
                {
                    temp                   =    weightbreaks[j];
                    weightbreaks[j]        =    weightbreaks[j+1];
                    weightbreaks[j+1]      =     temp;
                    temp                   =     ratebreaks[j];
                    ratebreaks[j]          =     ratebreaks[j+1];
                    ratebreaks[j+1]        =     temp;
                    temp                   =     chargeindicators[j];
                    chargeindicators[j]    =     chargeindicators[j+1];
                    chargeindicators[j+1]  =     temp;
                    temp                   =     lbound[j];
                    lbound[j]              =     lbound[j+1];
                    lbound[j+1]            =     temp;
                    temp                   =     ubound[j];
                    ubound[j]              =     ubound[j+1];
                    ubound[j+1]            =     temp;
                }
            
             }
            }
            if(breaks!=null)
            {
            	int brkLen	=	breaks.length;
             for(int s=0;s<brkLen;s++)
             {
              for(int r=0;r<brkLen-1;r++)
              {
                if(breaks[r]!=null&&breaks[r+1]!=null)
                {
                if(Integer.parseInt(breaks[r])>Integer.parseInt(breaks[r+1]))
                 {
                        temp             =     breaks[r];
                        breaks[r]        =     breaks[r+1];
                        breaks[r+1]      =     temp;
                        temp             =     rates[r];
                        rates[r]         =     rates[r+1];
                        rates[r+1]       =     temp;
                        temp             =     indicators[r];
                        indicators[r]    =     indicators[r+1];
                        indicators[r+1]  =     temp;
                        temp             =     lboundArr[r];
                        lboundArr[r]     =     lboundArr[r+1];
                        lboundArr[r+1]   =     temp;
                        temp             =     uboundArr[r];
                        uboundArr[r]     =     uboundArr[r+1];
                        uboundArr[r+1]   =     temp;
                  }
                }
                }
              }
            }
           //  Collections.sort(breaks);
            if(count == 2){
            	
            	weigthBreaksArray[0] = s6;
                chargeRatesArray[0] = s7;
                chargeIndicator[0]  = s3;
                lowerBoundArry[0]   = s4;
                upperBoundArry[0]   = s5;
                rateDescription[0] ="A FREIGHT RATE";
                
                weigthBreaksArray[1] = s1;
                chargeRatesArray[1] = s2;
                chargeIndicator[1]  = s3;
                lowerBoundArry[1]   = s4;
                upperBoundArry[1]   = s5;
                rateDescription[1] ="A FREIGHT RATE";
            }
            else{
            	weigthBreaksArray[0] = s1;
                chargeRatesArray[0] = s2;
                chargeIndicator[0]  = s3;
                lowerBoundArry[0]   = s4;
                upperBoundArry[0]   = s5;
                rateDescription[0] ="A FREIGHT RATE";
            }
              /*weigthBreaksArray[0] = s1;
              chargeRatesArray[0] = s2;
              chargeIndicator[0]  = s3;
              lowerBoundArry[0]   = s4;
              upperBoundArry[0]   = s5;
              rateDescription[0] ="A FREIGHT RATE";*/
              if(breaks!=null)
              {
            	  int brksLen	=	breaks.length;
             for(int s=0;s<brksLen;s++)
             {
                 if(breaks[s]!=null)
                {
               
                  weigthBreaksArray[count]   =  breaks[s];
                  chargeRatesArray[count]    =  rates[s];
                  chargeIndicator[count]     =  indicators[s];
                   lowerBoundArry[count]     =  lboundArr[s];
                   upperBoundArry[count]     =  uboundArr[s];
                  rateDescription[count]     =  "A FREIGHT RATE";
                  count++;
                }
             }
              }
             if(weightbreaks!=null)
             {
            	 int wtBrkLen	=	weightbreaks.length;
              for(int t=0;t<wtBrkLen;t++)
               {
                 if(weightbreaks[t]!=null)
                {
                  weigthBreaksArray[count]   =   weightbreaks[t];
                 chargeRatesArray[count]     =   ratebreaks[t];
                  chargeIndicator[count]     =   chargeindicators[t];
                   lowerBoundArry[count]     =  lbound[t];
                   upperBoundArry[count]     =  ubound[t];
                    rateDescription[count]   = ratedescs[t];
                   count++;
                }
               }
             }
          }
         else   if("FLAT".equalsIgnoreCase(qmsSellRatesDob.getWeightBreak()))
            {
            count = 0;count1=0;count2=0;
            int wtBrkArrLen	=	weigthBreaksArray.length;
             for(int n=0;n<wtBrkArrLen;n++)
             {
                if(weigthBreaksArray[n]!=null)
                {
                // Added By Kishore For RSR Acceptence Modify Screen
                  if( ("BASIC".equalsIgnoreCase(weigthBreaksArray[n]) || "MIN".equalsIgnoreCase(weigthBreaksArray[n])||"FLAT".equalsIgnoreCase(weigthBreaksArray[n])) && ("A FREIGHT RATE".equalsIgnoreCase(rateDescription[n]) ))
                  {
                     breaks[count1]                  =   weigthBreaksArray[n];
                       ratedescription[count1]        =   rateDescription[n];
                     if(chargeRatesArray[n]!=null)
                     {
                        rates[count1]         =   chargeRatesArray[n];
                     }
                     if(chargeIndicator[n]!=null)
                     {
                        indicators[count1]    =   chargeIndicator[n];
                     }
                     else
                     {
                        indicators[count1]    = "";
                     }
                     count1++;
                  }
                  else
                  {
                     weightbreaks[count2]        =   weigthBreaksArray[n];
                      ratedescs[count2]        =   rateDescription[n];
                    if(chargeRatesArray[n]!=null)
                     {
                    ratebreaks[count2]          =   chargeRatesArray[n];
                     }
                     if(chargeIndicator[n]!=null)
                     {
                        chargeindicators[count2]    =   chargeIndicator[n];
                     }
                    count2++;
                    
                  }
                }
              } 
               if(weightbreaks!=null)
            {
               for(int j=0;j<weightbreaks.length-1;j++)
             {
                if(("FSKG".equalsIgnoreCase(weightbreaks[j])&&"FSMIN".equalsIgnoreCase(weightbreaks[j+1]))||("SSKG".equalsIgnoreCase(weightbreaks[j])&&"SSMIN".equalsIgnoreCase(weightbreaks[j+1]))
                ||("CAF%".equalsIgnoreCase(weightbreaks[j])&&"CAFMIN".equalsIgnoreCase(weightbreaks[j+1]))||"BAFM3".equalsIgnoreCase(weightbreaks[j])&&"BAFMIN".equalsIgnoreCase(weightbreaks[j+1])
                ||"PSSM3".equalsIgnoreCase(weightbreaks[j])&&"PSSMIN".equalsIgnoreCase(weightbreaks[j+1]))
                {
                    temp                   =     weightbreaks[j];
                    weightbreaks[j]        =     weightbreaks[j+1];
                    weightbreaks[j+1]      =     temp;
                    temp                   =     ratebreaks[j];
                    ratebreaks[j]          =     ratebreaks[j+1];
                    ratebreaks[j+1]        =     temp;
                    temp                   =     chargeindicators[j];
                    chargeindicators[j]    =     chargeindicators[j+1];
                    chargeindicators[j+1]  =     temp;
                }
              }
            }
              if(breaks!=null)
              {
            	  int brksLen	=	breaks.length;
             for(int s=0;s<brksLen;s++)
             {
                 if(breaks[s]!=null)
                {
                  weigthBreaksArray[count]   =  breaks[s];
                  chargeRatesArray[count]    =  rates[s];
                  if(indicators!=null)
                  {
                      chargeIndicator[count]     =  indicators[s];
                  }
                   rateDescription[count]     = "A FREIGHT RATE";
                   count++;
                }
               }
              }
             if(weightbreaks!=null)
             {
            	 int wTbrkLen	=	weightbreaks.length;            	 
              for(int t=0;t<wTbrkLen;t++)
               {
                 if(weightbreaks[t]!=null)
                {
                  weigthBreaksArray[count]  =   weightbreaks[t];
                  chargeRatesArray[count]   =   ratebreaks[t];
                  if(chargeindicators!=null)
                  {
                      chargeIndicator[count]    =   chargeindicators[t];
                  }
                   rateDescription[count]     =  ratedescs[t];
                   count++;
                }
               }
             }
            }
            
             else  if("LIST".equalsIgnoreCase(qmsSellRatesDob.getWeightBreak())&&!("FCL".equalsIgnoreCase(sellRatesDob.getConsoleType())))
              {
                     count = 0;count1=0;count2=0;
                     int wtBrksArrLen	=	weigthBreaksArray.length;
             for(int n=0;n<wtBrksArrLen;n++)
             {
          
                if(weigthBreaksArray[n]!=null)
                {
 /*                 if(!("FSBASIC".equalsIgnoreCase(weigthBreaksArray[n])||"FSMIN".equalsIgnoreCase(weigthBreaksArray[n])||"FSKG".equalsIgnoreCase(weigthBreaksArray[n])
                     ||"SSBASIC".equalsIgnoreCase(weigthBreaksArray[n])||"SSMIN".equalsIgnoreCase(weigthBreaksArray[n])||"SSKG".equalsIgnoreCase(weigthBreaksArray[n])||"SURCHARGE".equalsIgnoreCase(weigthBreaksArray[n])))
*/                  if("A FREIGHT RATE".equalsIgnoreCase(rateDescription[n]))  
                	{
                     breaks[count1]                  =   weigthBreaksArray[n];
                      ratedescription[count1]        =   rateDescription[n];
                     if(chargeRatesArray[n]!=null)
                     {
                        rates[count1]         =   chargeRatesArray[n];
                     }
                    
                         if(chargeIndicator!=null)
                         {
                         if(chargeIndicator[n]!=null)
                         {
                     
                         indicators[count1]    =   chargeIndicator[n];
                         }
                     }
                     else
                     {
                        indicators[count1]    = "";
                     }
                     count1++;
                  }
                  else
                  {
                     weightbreaks[count2]        =   weigthBreaksArray[n];
                      ratedescs[count2]        =   rateDescription[n];
                    if(chargeRatesArray[n]!=null)
                     {
                    ratebreaks[count2]          =   chargeRatesArray[n];
                     }
                     if(chargeIndicator[n]!=null)
                     {
                        chargeindicators[count2]    =   chargeIndicator[n];
                     }
                    count2++;
                    
                  }
                }
              } 
             if(weightbreaks!=null)
            {
              for(int n=0;n<weightbreaks.length-1;n++)
             {
                 /* if(!("FSBASIC".equalsIgnoreCase(weightbreaks[n])||"FSMIN".equalsIgnoreCase(weightbreaks[n])||"FSKG".equalsIgnoreCase(weightbreaks[n])
                     ||"SSBASIC".equalsIgnoreCase(weightbreaks[n])||"SSMIN".equalsIgnoreCase(weightbreaks[n])||"SSKG".equalsIgnoreCase(weightbreaks[n])))
                     {
            
                         rateDescription[n] = "A FREIGHT RATE";
                     }
                     else
                     {*/
                     
                        if(("FSKG".equalsIgnoreCase(weightbreaks[n])&&"FSMIN".equalsIgnoreCase(weightbreaks[n+1]))||("SSKG".equalsIgnoreCase(weightbreaks[n])&&"SSMIN".equalsIgnoreCase(weightbreaks[n+1])))
                         {
                           temp                    =  weightbreaks[n];
                           weightbreaks[n]         =  weightbreaks[n+1];
                           weightbreaks[n+1]       =  temp;
                      // }
                    }
                   }
                 }
           if(breaks!=null)
              {
        	   int brkLen	=	breaks.length;
             for(int s=0;s<brkLen;s++)
             {
                 if(breaks[s]!=null)
                {
                  weigthBreaksArray[count]   =  breaks[s];
                  chargeRatesArray[count]    =  rates[s];
                  if(indicators!=null)
                  {
                   chargeIndicator[count]     =  indicators[s];
                  }
                   rateDescription[count]     = "A FREIGHT RATE";
                   count++;
                }
               }
              }
             if(weightbreaks!=null)
             {
            	 int wtBrekLen	=	weightbreaks.length;
              for(int t=0;t<wtBrekLen;t++)
               {
                 if(weightbreaks[t]!=null)
                {
                  weigthBreaksArray[count]  =   weightbreaks[t];
                  chargeRatesArray[count]   =   ratebreaks[t];
                  if(chargeindicators[t]!=null)
                  {
                      chargeIndicator[count]    =   chargeindicators[t];
                  }
                   rateDescription[count]     =  ratedescs[t];
                   count++;
                }
               }
             }
          }
              
          else  if("LIST".equalsIgnoreCase(qmsSellRatesDob.getWeightBreak())&&"FCL".equalsIgnoreCase(qmsSellRatesDob.getConsoleType()))
          {
        	int wBrkLen	=	weigthBreaksArray.length;  
           for(int n=0;n<wBrkLen;n++)
             {
                if(weigthBreaksArray[n]!=null)
                {
 /*                  if(!(weigthBreaksArray[n].endsWith("CAF")||weigthBreaksArray[n].endsWith("BAF")
                   ||weigthBreaksArray[n].endsWith("CSF")||weigthBreaksArray[n].endsWith("PSS")))
 */ 				if("A FREIGHT RATE".equalsIgnoreCase(rateDescription[n]))
                	{
                       weightbreaks[count1] = weigthBreaksArray[n];
                       ratedescs[count1]    = "A FREIGHT RATE";
                         if(chargeRatesArray[n]!=null)
                       {
                          ratebreaks[count1]          =   chargeRatesArray[n];
                       }
                       if(chargeIndicator[n]!=null)
                       {
                          chargeindicators[count1]    =   chargeIndicator[n];
                       }
//                     count1++;
                   }
 				else 
				{
                    weightbreaks[count1] = weigthBreaksArray[n];
                    ratedescs[count1]    = rateDescription[n];
                     if(chargeRatesArray[n]!=null)
                   {
                      ratebreaks[count1]          =   chargeRatesArray[n];
                   }
                   if(chargeIndicator[n]!=null)
                   {
                      chargeindicators[count1]    =   chargeIndicator[n];
                   }
			   }
 			count1++;
                }
             }
     /*      for(int n=0;n<wBrkLen;n++)
             {
                if(weigthBreaksArray[n]!=null)
                {
                   if(weigthBreaksArray[n].endsWith("BAF"))
                   {
                     weightbreaks[count1] = weigthBreaksArray[n];
                      ratedescs[count1]    = "BAF";
                       if(chargeRatesArray[n]!=null)
                     {
                        ratebreaks[count1]          =   chargeRatesArray[n];
                     }
                     if(chargeIndicator[n]!=null)
                     {
                        chargeindicators[count1]    =   chargeIndicator[n];
                     }
                     count1++;
                   }
                }
             }
             for(int n=0;n<wBrkLen;n++)
             {
                if(weigthBreaksArray[n]!=null)
                {
                   if(weigthBreaksArray[n].endsWith("CAF"))
                   {
                     weightbreaks[count1] = weigthBreaksArray[n];
                      ratedescs[count1]    = "CAF%";
                       if(chargeRatesArray[n]!=null)
                       {
                          ratebreaks[count1]          =   chargeRatesArray[n];
                       }
                       if(chargeIndicator[n]!=null)
                       {
                          chargeindicators[count1]    =   chargeIndicator[n];
                       }
                     count1++;
                   }
                }
             }
              for(int n=0;n<wBrkLen;n++)
             {
                if(weigthBreaksArray[n]!=null)
                {
                   if(weigthBreaksArray[n].endsWith("CSF"))
                   {
                     weightbreaks[count1] = weigthBreaksArray[n];
                      ratedescs[count1]    = "CSF";
                     
                          if(chargeRatesArray[n]!=null)
                       {
                          ratebreaks[count1]          =   chargeRatesArray[n];
                       }
                       if(chargeIndicator[n]!=null)
                       {
                          chargeindicators[count1]    =   chargeIndicator[n];
                       }
                     count1++;
                   }
                }
             }
              for(int n=0;n<wBrkLen;n++)
             {
                if(weigthBreaksArray[n]!=null)
                {
                   if(weigthBreaksArray[n].endsWith("PSS"))
                   {
                     weightbreaks[count1] = weigthBreaksArray[n];
                      ratedescs[count1]    = "PSS";
                      if(chargeRatesArray[n]!=null)
                       {
                         ratebreaks[count1]          =   chargeRatesArray[n];
                       }
                       if(chargeIndicator[n]!=null)
                       {
                          chargeindicators[count1]    =   chargeIndicator[n];
                       }
                     count1++;
                   }
                }
             }*/
              
//              if(icount==0)
               qmsSellRatesDob.setWeightBreaks(weightbreaks);
            qmsSellRatesDob.setAllWeightBreaks(weightbreaks);
            qmsSellRatesDob.setChargeInr(chargeindicators);
            qmsSellRatesDob.setChargeRates(ratebreaks);
            qmsSellRatesDob.setRateDescription(ratedescs);
           
          }
           /*    if(icount==0)
          {*/
            if("FLAT".equalsIgnoreCase(qmsSellRatesDob.getWeightBreak())||"SLAB".equalsIgnoreCase(qmsSellRatesDob.getWeightBreak())||("LIST".equalsIgnoreCase(qmsSellRatesDob.getWeightBreak())&&!("FCL".equalsIgnoreCase(qmsSellRatesDob.getConsoleType()))))
           {
            qmsSellRatesDob.setWeightBreaks(weigthBreaksArray);
             }
         // }
           if("FLAT".equalsIgnoreCase(qmsSellRatesDob.getWeightBreak())||"SLAB".equalsIgnoreCase(qmsSellRatesDob.getWeightBreak())||("LIST".equalsIgnoreCase(qmsSellRatesDob.getWeightBreak())&&!("FCL".equalsIgnoreCase(qmsSellRatesDob.getConsoleType()))))
           {
            //qmsSellRatesDob.setWeightBreaks(weigthBreaksArray);
             qmsSellRatesDob.setChargeInr(chargeIndicator);
             qmsSellRatesDob.setAllWeightBreaks(weigthBreaksArray);
            qmsSellRatesDob.setChargeRates(chargeRatesArray);
             qmsSellRatesDob.setRateDescription(rateDescription);
          }
           
            /*weightBreak = rs.getString("WEIGHT_BREAK_SLAB");
            //weightBreak = "MIN,-20,20,30";
            weigthBreaksArray = weightBreak.split(",");
             if(icount==0)
            qmsSellRatesDob.setWeightBreaks(weigthBreaksArray);
            qmsSellRatesDob.setAllWeightBreaks(weigthBreaksArray);
            chargerates      = rs.getString("CHARGERATE");
            //chargerates       = "20,60,30,20";
            //System.out.println("chargeRateschargeRateschargeRateschargeRates :: in dao "+chargeRates);
            chargeRatesArray = chargerates.split(",");
            qmsSellRatesDob.setChargeRates(chargeRatesArray);*/
            //lbound          = rs.getString("LBOUND");
            //lbound            = "0,0,20,30";
           // lowerBoundArry  = lbound.split(",");
            qmsSellRatesDob.setLBound(lowerBoundArry);
          // ubound          = rs.getString("UBOUND");
           //ubound            = "0,19,29,1000";
            //upperBoundArry  = ubound.split(",");
            qmsSellRatesDob.setUBound(upperBoundArry);
            chargeIn        = rs.getString("C_INDICATOR");
            //chargeIn          = "-,-,-,-";
           /* chargeIndicator = chargeIn.split(",");
            qmsSellRatesDob.setChargeInr(chargeIndicator);*/
            //persentage  = "4,5,5,5";
            persentage  = rs.getString("MARGINPERC");
            persentValuesArry = persentage.split(",");
            qmsSellRatesDob.setMarginValues(persentValuesArry);
            qmsSellRatesDob.setOverAllMargin(rs.getString("OVERALL_MARGIN"));
            qmsSellRatesDob.setMarginType(rs.getString("MARGIN_TYPE"));
            //System.out.println("currencyFactorcurrencyFactorcurrencyFactor3333333333 :: "+chargeIndicator);
            
            dataList.add(qmsSellRatesDob);
            //System.out.println("TerminalIdTerminalIdTerminalIdTerminalId ::: "+loginBean.getTerminalId());
          }
          finalList.add(dataList);
          finalList.add(new Integer(csmt.getInt(8)));
          finalList.add(new Integer(csmt.getInt(9)));
          //finalList.add(new Integer(1));
          //finalList.add(new Integer(1));
      }
      catch(SQLException se)
      {
        se.printStackTrace();
        //Logger.error(FILE_NAME,"error at getAcceptanceRateDetails()"+se.toString());
        logger.error(FILE_NAME+"error at getAcceptanceRateDetails()"+se.toString());
        throw new EJBException(se.toString());
      }
      catch(Exception e)
      {
        e.printStackTrace();
        //Logger.error(FILE_NAME,"error at getAcceptanceRateDetails()"+e.toString());
        logger.error(FILE_NAME+"error at getAcceptanceRateDetails()"+e.toString());
        throw new EJBException(e.toString());
      }finally
      {
        try
        {
          if(rs!=null)
            { rs.close();}
          if(csmt!=null)
            { csmt.close();}
          if(connection!=null)
            { connection.close();}
        }catch(SQLException e)
        {
          e.printStackTrace();
          //Logger.error(FILE_NAME,"error at getAcceptanceRateDetails()"+e.toString());
          logger.error(FILE_NAME+"error at getAcceptanceRateDetails()"+e.toString());
          throw new EJBException();         
        }
      }
     return finalList;
  }
 public ArrayList getRateDetails(QMSSellRatesDOB sellRatesDob,ESupplyGlobalParameters loginBean,String operation)throws EJBException
  {
      CallableStatement csmt  = null;
      ResultSet         rs    = null;
      
      ArrayList         dataList      =   new ArrayList();
      ArrayList         finalList     =   new ArrayList();
      Hashtable           map         =   new Hashtable();
      QMSSellRatesDOB   qmsSellRatesDob = null;
      String            weightBreaks    =  "";
      String            chargeRates     =  "";
      String            lBound          =  "";
      String            uBound          =  "";
      String            chargeIn        =  "";
      String            rateDesc        =  "";
      String[]          weigthBreaksArray = null;
      String[]          chargeRatesArray  = null;
      String[]          lowerBoundArry    = null;
      String[]          upperBoundArry    = null;
      String[]          chargeIndicator   = null;
      String[]          rateDescription   = null;
      String                orign             =   null;
      String                destination       =   null;
      String                carrier           =   null;
      String                serviceLevel      =   null;
      String                orignStr          =   null;
      String                destinationStr    =   null;
      String                serviceStr        =   null;
      String                carrierStr        =   null;
      String                currencyKey       =   null;
      double                currencyFactor    =   0.0;
      OperationsImpl        operationImpl     =   null;
      String                terminalId        =   null;
      
      //Added By Kishore Podili For Surcharge Currency
      String            schCurr        =  "";
      String[]          surChargeCurrency   = null;
      
      
 //@@ Added by subrahmanyam for the pbn id: 211196 on 20-Jul-10      
	 PreparedStatement pstmt1 = null;
	 ResultSet rs1 =null;
	 String lovSize	= "SELECT PARAM_VALUE FROM FS_USER_PREFERENCES WHERE USERID='"+loginBean.getUserId()+"'"+" AND LOCATIONID='"+loginBean.getTerminalId()+"' AND  PARAM_NAME='segmentSize'";
//@@ Ended by subrahmanyam for the pbn id: 211196 on 20-Jul-10
      try
      {
          operationImpl       =   new OperationsImpl();
          orignStr            =   sellRatesDob.getOrigin();
          destinationStr      =   sellRatesDob.getDestination();
          serviceStr          =   sellRatesDob.getServiceLevel();
          carrierStr          =   sellRatesDob.getCarrier_id();
          orign               =   orignStr.replaceAll(",","~");
          destination         =   destinationStr.replaceAll(",","~");
          serviceLevel        =   serviceStr.replaceAll(",","~");
          carrier             =   carrierStr.replaceAll(",","~");
          if("Add".equalsIgnoreCase(operation))
            terminalId  = loginBean.getTerminalId();
          else if("Modify".equalsIgnoreCase(operation))
            terminalId  = sellRatesDob.getTerminalId();
          //System.out.println("terminalIdterminalId in Dao to Get(): "+terminalId);
          getConnection();
          //@@ Added by subrahmanyam for the pbn id: 211196 on 20-Jul-10       
          pstmt1 = connection.prepareStatement(lovSize);
           rs1 =pstmt1.executeQuery();
          int lovsizeVal=0;
		if(rs1.next())
        	  lovsizeVal = rs1.getInt(1);
			
          csmt = connection.prepareCall("{CALL Qms_Rsr_Rates_Pkg.buy_sell_rates(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)} ");
          csmt.setString(1,orign);
          csmt.setString(2,destination);
          csmt.setString(3,terminalId);
          csmt.setString(4,sellRatesDob.getAccessLevel());
          csmt.setString(5,sellRatesDob.getRateType());
          csmt.setString(6,sellRatesDob.getWeightBreak());
          csmt.setString(7,serviceLevel);
          csmt.setString(8,carrier);
          csmt.setString(9,sellRatesDob.getCurrencyId());
          csmt.setString(10,sellRatesDob.getShipmentMode());
          csmt.setString(11,operation);
         // System.out.println("getPageNogetPageNogetPageNo IN dao :: "+sellRatesDob.getPageNo());
          csmt.setInt(12,Integer.parseInt(sellRatesDob.getPageNo()));
        // csmt.setInt(13,10);
       /* csmt.setInt(13,10);*/  //@@Modified by Kameswari for enhancement..
       
      // csmt.setInt(13,100);
         csmt.setInt(13,lovsizeVal);
         //added n modified by phani sekhar for wpbn 171213 on 20090609
        csmt.setString(14,sellRatesDob.getOriginCountry());
        csmt.setString(15,sellRatesDob.getDestinationCountry());
        csmt.setString(16,sellRatesDob.getOriginRegions());
        csmt.setString(17,sellRatesDob.getDestRegions());
        
          csmt.registerOutParameter(18,OracleTypes.INTEGER);
          csmt.registerOutParameter(19,OracleTypes.INTEGER);
          csmt.registerOutParameter(20,OracleTypes.CURSOR);
       /*   csmt.registerOutParameter(14,OracleTypes.INTEGER);
          csmt.registerOutParameter(15,OracleTypes.INTEGER);
          csmt.registerOutParameter(16,OracleTypes.CURSOR);*/
         csmt.execute();
      rs = (ResultSet)csmt.getObject(20);// changed index frm 16 to 20 by phani sekhar for wpbn 171213 on 20090609
        int icount = 0;
        while(rs.next())
        {
          qmsSellRatesDob = new QMSSellRatesDOB();
          //System.out.println("BUYRATEIDBUYRATEIDBUYRATEID :: in dao "+rs.getString("BUYRATEID"));
          qmsSellRatesDob.setBuyRateId(rs.getString("BUYRATEID"));
          qmsSellRatesDob.setVersionNo(rs.getString("VERSION_NO"));//@@Added for the WPBN issue-146448 on 23/12/08
          //System.out.println("CARRIER_IDCARRIER_IDCARRIER_IDCARRIER_ID :: in dao "+rs.getString("CARRIER_ID"));
          //System.out.println("rs.getString(CARRIER_ID)"+rs.getString("CARRIER_ID"));
          qmsSellRatesDob.setCarrier_id(rs.getString("CARRIER_ID"));
          qmsSellRatesDob.setOrigin(rs.getString("ORIGIN"));
          //System.out.println("1111 orign "+rs.getString("ORIGIN"));
          qmsSellRatesDob.setOriginCountry(rs.getString("ORG_COUNTRYID"));
          qmsSellRatesDob.setDestination(rs.getString("DESTINATION"));
          //System.out.println("2222 DESTINATION "+rs.getString("DESTINATION"));
          qmsSellRatesDob.setDestinationCountry(rs.getString("DEST_COUNTRYID"));
          
          qmsSellRatesDob.setServiceLevel(rs.getString("SERVICE_LEVEL"));
          qmsSellRatesDob.setTransitTime(rs.getString("TRANSIT_TIME"));
          qmsSellRatesDob.setFrequency(rs.getString("FREQUENCY"));
          qmsSellRatesDob.setLanNumber(rs.getInt("LANE_NO"));
          qmsSellRatesDob.setDensityRatio(rs.getString("DENSITY_CODE"));
          //System.out.println("NOTESNOTESNOTESNOTESNOTESNOTESNOTESNOTESNOTES :: "+rs.getString("NOTES"));
          //weightBreaks = rs.getString("WEIGHTBREAKS");
          qmsSellRatesDob.setNotes(rs.getString("NOTES"));
          qmsSellRatesDob.setExtNotes(rs.getString("EXTERNAL_NOTES")); //Modified by Mohan for Issue No.219976 on 08-10-2010
            int count=1 ,count1=0,count2=0;
              String s1        =  "";
              String s2        =  "";
              String s3        =  "";
              String s4        =  "";
              String s5        =  "";
              String s6        =  "";
              String s7        =  "";
              chargeRates      = rs.getString("CHARGERATE");
             chargeRatesArray = chargeRates.split(",");
              weightBreaks      = rs.getString("WEIGHT_BREAK_SLAB");
              weigthBreaksArray = weightBreaks.split(",");
              chargeIndicator = new String[weigthBreaksArray.length];
             chargeIn        = rs.getString("C_INDICATOR");
                
         lBound          = rs.getString("LBOUND");
          //System.out.println("lBoundlBoundlBoundlBoundlBoundlBoundlBound :: in dao "+lBound);
          lowerBoundArry  = lBound.split(",");
         // qmsSellRatesDob.setLBound(lowerBoundArry);
          uBound          = rs.getString("UBOUND");
          //System.out.println("lBoundlBoundlBoundlBoundlBoundlBoundlBound :: in dao "+lBound);
          upperBoundArry  = uBound.split(",");
          //qmsSellRatesDob.setUBound(upperBoundArry);
            if(chargeIn!=null)
            {
                chargeIndicator = chargeIn.split(",");
            }
            qmsSellRatesDob.setChargeInr(chargeIndicator);  
           rateDesc        = rs.getString("RATE_DESCRIPTION");
           schCurr		   = rs.getString("SUR_CHARGE_CURRENCY"); //Added by kishore For SCH Currency
          
            
            if(rateDesc!=null)
            {
            rateDescription = rateDesc.split(",");
            }
            if(rateDescription!=null)
            {
            	int rtDesLen	=	rateDescription.length;
              for(int k=0;k<rtDesLen;k++)
              {
                if("C.P.S.S".equalsIgnoreCase(rateDescription[k]))
                {
                  rateDescription[k]  = "P.S.S";
                }
              }
            }
            
          //Added by kishore For SCH Currency
            if(schCurr!=null){
            	surChargeCurrency = schCurr.split(",");
            }
            
        //  qmsSellRatesDob.setRateDescription(rateDescription);
            int process_count	=	0;
        for(int process=0;process<weigthBreaksArray.length;process++)
        {
        	if("**".equalsIgnoreCase(rateDescription[process])){
        		
        				weigthBreaksArray[process]=null;
						chargeRatesArray[process]=null;
						chargeIndicator[process]=null;
						lowerBoundArry[process]=null;
						upperBoundArry[process]=null;
						rateDescription[process]=null;
						surChargeCurrency[process]=null;
						process_count ++;

        	}
        }
        String[] weigthBreaksArray_process = new String[weigthBreaksArray.length-process_count];
        String[] chargeRatesArray_process = new String[weigthBreaksArray.length-process_count];
        String[] chargeIndicator_process = new String[weigthBreaksArray.length-process_count];
        String[] lowerBoundArry_process = new String[weigthBreaksArray.length-process_count];
        String[] upperBoundArry_process = new String[weigthBreaksArray.length-process_count];
        String[] rateDescription_process = new String[weigthBreaksArray.length-process_count];
        String[] surChargeCurrency_process = new String[weigthBreaksArray.length-process_count]; //Added by kishore For SCH Currency
        int process_count1	=	0;
        for(int process=0;process<weigthBreaksArray.length;process++)
        {
        	if(rateDescription[process]!=null){
        		
        		weigthBreaksArray_process[process_count1]	=	weigthBreaksArray[process];
        		chargeRatesArray_process[process_count1]	=	chargeRatesArray[process];
        		chargeIndicator_process[process_count1]	=	chargeIndicator[process];
        		lowerBoundArry_process[process_count1]		=	lowerBoundArry[process];
        		upperBoundArry_process[process_count1]		=	upperBoundArry[process];
        		rateDescription_process[process_count1]	=	rateDescription[process];
        		surChargeCurrency_process[process_count1] = surChargeCurrency[process];//Added by kishore For SCH Currency
        		process_count1 ++;

        	}
        }
        weigthBreaksArray	=	weigthBreaksArray_process;
        chargeRatesArray	=	chargeRatesArray_process;
        chargeIndicator		=	chargeIndicator_process;
        lowerBoundArry		=	lowerBoundArry_process;
        upperBoundArry		=	upperBoundArry_process;
        rateDescription		=	rateDescription_process;
        surChargeCurrency	=	surChargeCurrency_process; //Added by kishore For SCH Currency
          
              String temp      =  "";
              String[]  weightbreaks          =   new String[weigthBreaksArray.length];  
             String[]  ratebreaks             =   new String[weigthBreaksArray.length];  
             String[]  chargeindicators       =   new String[weigthBreaksArray.length];  
             String[]  breaks                 =   new String[weigthBreaksArray.length];   
             String[]  rates                  =   new String[weigthBreaksArray.length];   
             String[]  indicators             =   new String[weigthBreaksArray.length];   
             String[]  ratedescs              =   new String[weigthBreaksArray.length]; 
             String[]  ratedescription        =   new String[weigthBreaksArray.length]; 
             String[]  lbound                 =   new String[weigthBreaksArray.length]; 
             String[]   ubound                =   new String[weigthBreaksArray.length]; 
             String[]  lboundArr              =   new String[weigthBreaksArray.length]; 
             String[]   uboundArr              =   new String[weigthBreaksArray.length];
           //Added by kishore For SCH Currency
             String[]  schcurrs              =   new String[weigthBreaksArray.length]; 
             String[]  surchargecurrency        =   new String[weigthBreaksArray.length]; 
          
             int wtBrkArrLen	=	weigthBreaksArray.length;
      if("SLAB".equalsIgnoreCase(sellRatesDob.getWeightBreak()))
      {
    	   count=0 ; // Added By Kishore For Slab - Slab case RSR Add  
             for(int n=0;n<wtBrkArrLen;n++)
             {
                if(weigthBreaksArray[n]!=null)
                {
                	//@@ Commented and Added by subrahmanyam for the CR-219773
//                	if(weigthBreaksArray[n].startsWith("M"))
                if("BASIC".equalsIgnoreCase(weigthBreaksArray[n]) && ( "A FREIGHT RATE".equalsIgnoreCase(rateDescription[n]) || "-".equalsIgnoreCase(rateDescription[n]))){
                    s6   =   weigthBreaksArray[n];
                    s7 =     chargeRatesArray[n];
                    count++;
                }
                else if("MIN".equalsIgnoreCase(weigthBreaksArray[n]) && "A FREIGHT RATE".equalsIgnoreCase(rateDescription[n]) )
                {
                     s1   =   weigthBreaksArray[n];
                     s2 =     chargeRatesArray[n];
                     s3 =     chargeIndicator[n];
                     s4 =     lowerBoundArry[n];
                     s5 =     upperBoundArry[n];
                     count++;
                }
 /*               else if(weigthBreaksArray[n].startsWith("S")||weigthBreaksArray[n].startsWith("F")||
                weigthBreaksArray[n].startsWith("B")||weigthBreaksArray[n].startsWith("C")||weigthBreaksArray[n].startsWith("P") && (!"A FREIGHT RATE".equalsIgnoreCase(rateDescription[n]) && !"-".equalsIgnoreCase(rateDescription[n])))
 */               else if(!"A FREIGHT RATE".equalsIgnoreCase(rateDescription[n]) && !"**".equalsIgnoreCase(rateDescription[n])  && !"-".equalsIgnoreCase(rateDescription[n]))
                  {
            
                   weightbreaks[count2]       =   weigthBreaksArray[n];
                   lbound[count2]             =   lowerBoundArry[n];
                    ubound[count2]            =   upperBoundArry[n];
                  
                     ratedescs[count2]        =   rateDescription[n];
                     schcurrs[count2]		  =   surChargeCurrency[n]; //Added by kishore For SCH Currency
                     
                  if(chargeRatesArray[n]!=null)
                   {
                        ratebreaks[count2]          =   chargeRatesArray[n];
                   }
                   if(chargeIndicator[n]!=null)
                   {
                      chargeindicators[count2]    =   chargeIndicator[n];
                   }
                  count2++;
                }
                 else if("A FREIGHT RATE".equalsIgnoreCase(rateDescription[n]) || "-".equalsIgnoreCase(rateDescription[n]))
                  {
                     breaks[count1]           =   weigthBreaksArray[n];
                     
                     if(chargeRatesArray[n]!=null)
                     {
                        rates[count1]         =   chargeRatesArray[n];
                     }
                     if(chargeIndicator[n]!=null)
                     {
                        indicators[count1]    =   chargeIndicator[n];
                     }
                     else
                     {
                        indicators[count1]    = "";
                     }
                     
                     lboundArr[count1]        =   lowerBoundArry[n];
                     uboundArr[count1]        =   upperBoundArry[n];
                     
                     count1++;
                  }
                }
            }
            if(weightbreaks!=null)
            {
            	int wtBrkLen	=	weightbreaks.length;
               for(int j=0;j<wtBrkLen-1;j++)
             {
            	   //@@ Modified by subrahmanyam for the CR-219973
                if(("FSKG".equalsIgnoreCase(weightbreaks[j])&&"FSMIN".equalsIgnoreCase(weightbreaks[j+1]))||("SSKG".equalsIgnoreCase(weightbreaks[j])&&"SSMIN".equalsIgnoreCase(weightbreaks[j+1]))
                ||("CAF%".equalsIgnoreCase(weightbreaks[j])&&"CAFMIN".equalsIgnoreCase(weightbreaks[j+1]))||"BAFM3".equalsIgnoreCase(weightbreaks[j])&&"BAFMIN".equalsIgnoreCase(weightbreaks[j+1])
                ||"PSSM3".equalsIgnoreCase(weightbreaks[j])&&"PSSMIN".equalsIgnoreCase(weightbreaks[j+1]) || (  weightbreaks[j] !=null && weightbreaks[j+1] !=null && weightbreaks[j].endsWith("FLAT")&& weightbreaks[j+1].endsWith("MIN") && weightbreaks[j+1].length()>3)  )
                {
                    temp                   =    weightbreaks[j];
                    weightbreaks[j]        =    weightbreaks[j+1];
                    weightbreaks[j+1]      =     temp;
                    temp                   =     ratebreaks[j];
                    ratebreaks[j]          =     ratebreaks[j+1];
                    ratebreaks[j+1]        =     temp;
                    temp                   =     chargeindicators[j];
                    chargeindicators[j]    =     chargeindicators[j+1];
                    chargeindicators[j+1]  =     temp;
                    temp                   =     lbound[j];
                    lbound[j]              =     lbound[j+1];
                    lbound[j+1]            =     temp;
                    temp                   =     ubound[j];
                    ubound[j]              =     ubound[j+1];
                    ubound[j+1]            =     temp;
                }
            
             }
            }
            if(breaks!=null)
            {
            	int brkLen	=	breaks.length;
             for(int s=0;s<brkLen;s++)
             {
              for(int r=0;r<brkLen-1;r++)
              {
                if(breaks[r]!=null&&breaks[r+1]!=null)
                {
                if(Double.parseDouble(breaks[r])>Double.parseDouble(breaks[r+1]))//Modified by Mohan For Issue No.219976 on 10112010
                 {
                        temp             =     breaks[r];
                        breaks[r]        =     breaks[r+1];
                        breaks[r+1]      =     temp;
                        temp             =     rates[r];
                        rates[r]         =     rates[r+1];
                        rates[r+1]       =     temp;
                        temp             =     indicators[r];
                        indicators[r]    =     indicators[r+1];
                        indicators[r+1]  =     temp;
                        temp             =     lboundArr[r];
                        lboundArr[r]     =     lboundArr[r+1];
                        lboundArr[r+1]   =     temp;
                        temp             =     uboundArr[r];
                        uboundArr[r]     =     uboundArr[r+1];
                        uboundArr[r+1]   =     temp;
                  }
                }
                }
              }
            }
            if(count==2){
           //  Collections.sort(breaks);
            	//This is for basic
              weigthBreaksArray[0] = s6;
              chargeRatesArray[0] = s7;
              chargeIndicator[0]  = s3;
              lowerBoundArry[0]   = s4;
              upperBoundArry[0]   = s5;
              rateDescription[0] ="A FREIGHT RATE";
              surChargeCurrency[0] = ""; //Added by kishore For SCH Currency
//              THis is for MIN
              weigthBreaksArray[1] = s1;
              chargeRatesArray[1] = s2;
              chargeIndicator[1]  = s3;
              lowerBoundArry[1]   = s4;
              upperBoundArry[1]   = s5;
              rateDescription[1] ="A FREIGHT RATE";
              surChargeCurrency[1] = "";
            }
            else{
//            	This Is For Min
                weigthBreaksArray[0] = s1;
                chargeRatesArray[0] = s2;
                chargeIndicator[0]  = s3;
                lowerBoundArry[0]   = s4;
                upperBoundArry[0]   = s5;
                rateDescription[0] ="A FREIGHT RATE";
                surChargeCurrency[0] = "";//Added by kishore For SCH Currency

            }

              if(breaks!=null)
              {
            	  int brkLen	= breaks.length;
             for(int s=0;s<brkLen;s++)
             {
                 if(breaks[s]!=null)
                {
               
                  weigthBreaksArray[count]   =  breaks[s];
                  chargeRatesArray[count]    =  rates[s];
                  chargeIndicator[count]     =  indicators[s];
                   lowerBoundArry[count]     =  lboundArr[s];
                   upperBoundArry[count]     =  uboundArr[s];
                  rateDescription[count]     =  "A FREIGHT RATE";
                  surChargeCurrency[count] 		 = 	"";//Added by kishore For SCH Currency
                  count++;
                }
             }
              }
             if(weightbreaks!=null)
             {
            	 int wBrkLen	=	weightbreaks.length;
              for(int t=0;t<wBrkLen;t++)
               {
                 if(weightbreaks[t]!=null)
                {
                  weigthBreaksArray[count]   =   weightbreaks[t];
                 chargeRatesArray[count]     =   ratebreaks[t];
                  chargeIndicator[count]     =   chargeindicators[t];
                   lowerBoundArry[count]     =  lbound[t];
                   upperBoundArry[count]     =  ubound[t];
                    rateDescription[count]   = ratedescs[t];
                    surChargeCurrency[count] 	 = 	schcurrs[t];//Added by kishore For SCH Currency
                   count++;
                }
               }
             }
            
          }
          else if("FLAT".equalsIgnoreCase(sellRatesDob.getWeightBreak()))
            {int wtBrkArrLen1	=	weigthBreaksArray.length;
            count = 0;count1=0;count2=0;
             for(int n=0;n<wtBrkArrLen1;n++)
             {
                if(weigthBreaksArray[n]!=null)
                {
                	//@@ Commented & Added by subrahmanyam for the CR-219773
                	//if("MIN".equalsIgnoreCase(weigthBreaksArray[n])||"FLAT".equalsIgnoreCase(weigthBreaksArray[n]))
                	if( "BASIC".equalsIgnoreCase(weigthBreaksArray[n]) || ("MIN".equalsIgnoreCase(weigthBreaksArray[n])||"FLAT".equalsIgnoreCase(weigthBreaksArray[n]) ) && ("A FREIGHT RATE".equalsIgnoreCase(rateDescription[n]) ))
                  {
                     breaks[count1]                  =   weigthBreaksArray[n];
                      ratedescription[count1]        =   rateDescription[n];
                      surchargecurrency[count1]		 = 	 surChargeCurrency[n]; //Added by kishore For SCH Currency
                     if(chargeRatesArray[n]!=null)
                     {
                        rates[count1]         =   chargeRatesArray[n];
                     }
                     if(chargeIndicator[n]!=null)
                     {
                        indicators[count1]    =   chargeIndicator[n];
                     }
                     else
                     {
                        indicators[count1]    = "";
                     }
                     count1++;
                  }
                  else if(!"A FREIGHT RATE".equalsIgnoreCase(rateDescription[n]) && !"**".equalsIgnoreCase(rateDescription[n]))
                  {
                     weightbreaks[count2]        =   weigthBreaksArray[n];
                      ratedescs[count2]        =   rateDescription[n];
                      schcurrs[count2]			 =   surChargeCurrency[n]; //Added by kishore For SCH Currency
                    if(chargeRatesArray[n]!=null)
                     {
                    ratebreaks[count2]          =   chargeRatesArray[n];
                     }
                     if(chargeIndicator[n]!=null)
                     {
                        chargeindicators[count2]    =   chargeIndicator[n];
                     }
                    count2++;
                    
                  }
                }
              } 
               if(weightbreaks!=null)
              {
            	   int wBrkLen	=	weightbreaks.length;
               for(int j=0;j<wBrkLen-1;j++)
              {
                if(("FSKG".equalsIgnoreCase(weightbreaks[j])&&"FSMIN".equalsIgnoreCase(weightbreaks[j+1]))||("SSKG".equalsIgnoreCase(weightbreaks[j])&&"SSMIN".equalsIgnoreCase(weightbreaks[j+1]))
                ||("CAF%".equalsIgnoreCase(weightbreaks[j])&&"CAFMIN".equalsIgnoreCase(weightbreaks[j+1]))||"BAFM3".equalsIgnoreCase(weightbreaks[j])&&"BAFMIN".equalsIgnoreCase(weightbreaks[j+1])
                ||"PSSM3".equalsIgnoreCase(weightbreaks[j])&&"PSSMIN".equalsIgnoreCase(weightbreaks[j+1]) || ( weightbreaks[j]!=null && weightbreaks[j+1]!=null && weightbreaks[j].endsWith("FLAT")&& weightbreaks[j+1].endsWith("MIN")&& rateDescription[j].equalsIgnoreCase(rateDescription[j+1]) && weightbreaks[j+1].length()>3)  )
                //Added By Kishore for Mis Match Of Breaks for two diff Sur Charges(Min, Flat)
                {
                    temp                   =     weightbreaks[j];
                    weightbreaks[j]        =     weightbreaks[j+1];
                    weightbreaks[j+1]      =     temp;
                    temp                   =     ratebreaks[j];
                    ratebreaks[j]          =     ratebreaks[j+1];
                    ratebreaks[j+1]        =     temp;
                    temp                   =     chargeindicators[j];
                    chargeindicators[j]    =     chargeindicators[j+1];
                    chargeindicators[j+1]  =     temp;
                }
               }
            }
              if(breaks!=null)
                {
            	  int brkLen	=	breaks.length;
               for(int s=0;s<brkLen;s++)
               {
                   if(breaks[s]!=null)
                  {
                    weigthBreaksArray[count]   =  breaks[s];
                    chargeRatesArray[count]    =  rates[s];
                    chargeIndicator[count]     =  indicators[s];
                     rateDescription[count]     =  ratedescription[s];
                     surChargeCurrency[count]  =   surchargecurrency[s]; //Added by kishore For SCH Currency
                     count++;
                  }
                 }
                }
                 if(weightbreaks!=null)
                 {
                	 int wtBrkLen	=	weightbreaks.length;
                  for(int t=0;t<wtBrkLen;t++)
                   {
                     if(weightbreaks[t]!=null)
                    {
                      weigthBreaksArray[count]  =   weightbreaks[t];
                      chargeRatesArray[count]   =   ratebreaks[t];
                      chargeIndicator[count]    =   chargeIndicator[t];
                       rateDescription[count]     =  ratedescs[t];
                     surChargeCurrency[count]	=	schcurrs[t];//Added by kishore For SCH Currency
                       count++;
                    }
                   }
                 }
                    
            }
            else  if("LIST".equalsIgnoreCase(sellRatesDob.getWeightBreak())&&!("FCL".equalsIgnoreCase(sellRatesDob.getConsoleType())))
              {
                      count = 0;count1=0;count2=0;
                      int wBArrLen	=	weigthBreaksArray.length;
             for(int n=0;n<wBArrLen;n++)
             {
                if(weigthBreaksArray[n]!=null)
                {
 /*                 if(!("FSBASIC".equalsIgnoreCase(weigthBreaksArray[n])||"FSMIN".equalsIgnoreCase(weigthBreaksArray[n])||"FSKG".equalsIgnoreCase(weigthBreaksArray[n])
                     ||"SSBASIC".equalsIgnoreCase(weigthBreaksArray[n])||"SSMIN".equalsIgnoreCase(weigthBreaksArray[n])||"SSKG".equalsIgnoreCase(weigthBreaksArray[n])||"SURCHARGE".equalsIgnoreCase(weigthBreaksArray[n])) || !"A FREIGHT RATE".equalsIgnoreCase(rateDescription[n]))
 */					if("A FREIGHT RATE".equalsIgnoreCase(rateDescription[n]) || "-".equalsIgnoreCase(rateDescription[n]) )
                	{
                     breaks[count1]                  =   weigthBreaksArray[n];
              
                      ratedescription[count1]        =   rateDescription[n];
                      surchargecurrency[count1]		 = 	 surChargeCurrency[n]; //Added by kishore For SCH Currency
                      
                     if(chargeRatesArray[n]!=null)
                     {
                        rates[count1]         =   chargeRatesArray[n];
                     }
                     if(chargeIndicator[n]!=null)
                     {
                        indicators[count1]    =   chargeIndicator[n];
                     }
                     else
                     {
                        indicators[count1]    = "";
                     }
                     count1++;
                  }
                  else
                  {
                     weightbreaks[count2]        =   weigthBreaksArray[n];
                      ratedescs[count2]        =   rateDescription[n];
                      schcurrs[count2]			 =   surChargeCurrency[n]; //Added by kishore For SCH Currency
                    if(chargeRatesArray[n]!=null)
                     {
                    ratebreaks[count2]          =   chargeRatesArray[n];
                     }
                     if(chargeIndicator[n]!=null)
                     {
                        chargeindicators[count2]    =   chargeIndicator[n];
                     }
                    count2++;
                    
                  }
                }
              } 
             if(weightbreaks!=null)
            {
            	 int wBrkLen	=	weightbreaks.length;
              for(int n=0;n<wBrkLen-1;n++)
             {
                        if(("FSKG".equalsIgnoreCase(weightbreaks[n])&&"FSMIN".equalsIgnoreCase(weightbreaks[n+1]))||("SSKG".equalsIgnoreCase(weightbreaks[n])&&"SSMIN".equalsIgnoreCase(weightbreaks[n+1]))
                        		)
                         {
                           temp                    =  weightbreaks[n];
                           weightbreaks[n]         =  weightbreaks[n+1];
                           weightbreaks[n+1]       =  temp;
                     }
                   }
                 }
           if(breaks!=null)
              {
        	   int brkLen	=	breaks.length;
             for(int s=0;s<brkLen;s++)
             {
                 if(breaks[s]!=null)
                {
                  weigthBreaksArray[count]   =  breaks[s];
                  chargeRatesArray[count]    =  rates[s];
                  chargeIndicator[count]     =  indicators[s];
                   rateDescription[count]     = "A FREIGHT RATE";
                   surChargeCurrency[count]	 =  "-"; //Added by kishore For SCH Currency
                   count++;
                }
               }
              }
             if(weightbreaks!=null)
             {
            	 int wBrkLen	=	weightbreaks.length;
              for(int t=0;t<wBrkLen;t++)
               {
                 if(weightbreaks[t]!=null)
                {
                  weigthBreaksArray[count]  =   weightbreaks[t];
                  chargeRatesArray[count]   =   ratebreaks[t];
                  chargeIndicator[count]    =   chargeIndicator[t];
                   rateDescription[count]     =  ratedescs[t];
                   surChargeCurrency[count] =  schcurrs[t]; //Added by kishore For SCH Currency
                   count++;
                }
               }
             }
          }
              
          else if("LIST".equalsIgnoreCase(sellRatesDob.getWeightBreak())&&"FCL".equalsIgnoreCase(sellRatesDob.getConsoleType()))
          {
           count1 = 0;
           int wtBkArrLen	=	weigthBreaksArray.length;
           for(int n=0;n<wtBkArrLen;n++)
             {
                if(weigthBreaksArray[n]!=null)
                {
                  /* if(!(weigthBreaksArray[n].endsWith("CAF")||weigthBreaksArray[n].endsWith("BAF")
                   ||weigthBreaksArray[n].endsWith("CSF")||weigthBreaksArray[n].endsWith("PSS"))
                   || !"A FREIGHT RATE".equalsIgnoreCase(rateDescription[n]))*/
                	if("A FREIGHT RATE".equalsIgnoreCase(rateDescription[n]) || "-".equalsIgnoreCase(rateDescription[n]))
                   {
                       weightbreaks[count1] = weigthBreaksArray[n];
                       ratedescs[count1]    = "A FREIGHT RATE";
                       schcurrs[count1]		= "-"; //Added by kishore For SCH Currency
                         if(chargeRatesArray[n]!=null)
                       {
                          ratebreaks[count1]          =   chargeRatesArray[n];
                       }
                       if(chargeIndicator[n]!=null)
                       {
                          chargeindicators[count1]    =   chargeIndicator[n];
                       }
//                     count1++;
                  
                   }
                	else{
	                        weightbreaks[count1] = weigthBreaksArray[n];
	                        ratedescs[count1]    = rateDescription[n];
	                        schcurrs[count1]	 = surChargeCurrency[n]; //Added by kishore For SCH Currency
	                          if(chargeRatesArray[n]!=null)
	                        {
	                           ratebreaks[count1]          =   chargeRatesArray[n];
	                        }
	                        if(chargeIndicator[n]!=null)
	                        {
	                           chargeindicators[count1]    =   chargeIndicator[n];
	                        }
                	}
                	count1++;
                }
             }
           
           
           /* for(int n=0;n<wtBkArrLen;n++)
             {
                if(weigthBreaksArray[n]!=null)
                {
                   if(weigthBreaksArray[n].endsWith("BAF"))
                   {
                     weightbreaks[count1] = weigthBreaksArray[n];
                      ratedescs[count1]    = "BAF";
                       if(chargeRatesArray[n]!=null)
                     {
                        ratebreaks[count1]          =   chargeRatesArray[n];
                     }
                     if(chargeIndicator[n]!=null)
                     {
                        chargeindicators[count1]    =   chargeIndicator[n];
                     }
                     count1++;
                   }
                }
             }
             for(int n=0;n<wtBkArrLen;n++)
             {
                if(weigthBreaksArray[n]!=null)
                {
                   if(weigthBreaksArray[n].endsWith("CAF"))
                   {
                     weightbreaks[count1] = weigthBreaksArray[n];
                      ratedescs[count1]    = "CAF%";
                       if(chargeRatesArray[n]!=null)
                       {
                          ratebreaks[count1]          =   chargeRatesArray[n];
                       }
                       if(chargeIndicator[n]!=null)
                       {
                          chargeindicators[count1]    =   chargeIndicator[n];
                       }
                     count1++;
                   }
                }
             }
              for(int n=0;n<wtBkArrLen;n++)
             {
                if(weigthBreaksArray[n]!=null)
                {
                   if(weigthBreaksArray[n].endsWith("CSF"))
                   {
                     weightbreaks[count1] = weigthBreaksArray[n];
                      ratedescs[count1]    = "CSF";
                     
                          if(chargeRatesArray[n]!=null)
                       {
                          ratebreaks[count1]          =   chargeRatesArray[n];
                       }
                       if(chargeIndicator[n]!=null)
                       {
                          chargeindicators[count1]    =   chargeIndicator[n];
                       }
                     count1++;
                   }
                }
             }
              for(int n=0;n<wtBkArrLen;n++)
             {
                if(weigthBreaksArray[n]!=null)
                {
                   if(weigthBreaksArray[n].endsWith("PSS"))
                   {
                     weightbreaks[count1] = weigthBreaksArray[n];
                      ratedescs[count1]    = "PSS";
                     if(chargeRatesArray[n]!=null)
                       {
                         ratebreaks[count1]          =   chargeRatesArray[n];
                       }
                       if(chargeIndicator[n]!=null)
                       {
                          chargeindicators[count1]    =   chargeIndicator[n];
                       }
                     count1++;
                   }
                }
             }*/
             for(int i=0;i<wtBkArrLen;i++)
             {
                chargeRatesArray[i] = ratebreaks[i];
             
             }
              
             // if(icount==0)
     
               qmsSellRatesDob.setWeightBreaks(weightbreaks);               
            qmsSellRatesDob.setAllWeightBreaks(weightbreaks);
            qmsSellRatesDob.setChargeInr(chargeindicators);
            qmsSellRatesDob.setChargeRates(ratebreaks);
            qmsSellRatesDob.setRateDescription(ratedescs);
        }
     /*      if(icount==0)
          {*/
            if("FLAT".equalsIgnoreCase(sellRatesDob.getWeightBreak())||"SLAB".equalsIgnoreCase(sellRatesDob.getWeightBreak())||("LIST".equalsIgnoreCase(sellRatesDob.getWeightBreak())&&!("FCL".equalsIgnoreCase(sellRatesDob.getConsoleType()))))
           {
            qmsSellRatesDob.setWeightBreaks(weigthBreaksArray);
             }
         // }
           if("FLAT".equalsIgnoreCase(sellRatesDob.getWeightBreak())||"SLAB".equalsIgnoreCase(sellRatesDob.getWeightBreak())||("LIST".equalsIgnoreCase(sellRatesDob.getWeightBreak())&&!("FCL".equalsIgnoreCase(sellRatesDob.getConsoleType()))))
           {
            //qmsSellRatesDob.setWeightBreaks(weigthBreaksArray);
              qmsSellRatesDob.setChargeInr(chargeIndicator);
              qmsSellRatesDob.setAllWeightBreaks(weigthBreaksArray);
              qmsSellRatesDob.setChargeRates(chargeRatesArray);
              qmsSellRatesDob.setRateDescription(rateDescription);
              qmsSellRatesDob.setSurChargeCurency(surChargeCurrency); //Added by kishore For SCH Currency
           }
           if("LIST".equalsIgnoreCase(sellRatesDob.getWeightBreak())&&("FCL".equalsIgnoreCase(sellRatesDob.getConsoleType())))
        	   qmsSellRatesDob.setSurChargeCurency(surChargeCurrency); //Added by kishore For SCH Currency  
        /*  if(icount==0)
            qmsSellRatesDob.setWeightBreaks(weigthBreaksArray);
            
          qmsSellRatesDob.setAllWeightBreaks(weigthBreaksArray);*/
          
          //qmsSellRatesDob.setChargeRates(chargeRatesArray);
       
          qmsSellRatesDob.setLBound(lowerBoundArry);
                  //System.out.println("lBoundlBoundlBoundlBoundlBoundlBoundlBound :: in dao "+lBound);
          qmsSellRatesDob.setUBound(upperBoundArry);
          
          currencyKey    =  sellRatesDob.getCurrencyId()+"&"+rs.getString("CURRENCY");
         // System.out.println("currencyKeycurrencyKeycurrencyKeycurrencyKeycurrencyKey :: "+currencyKey);
         // System.out.println("containsKeycontainsKeycontainsKeycontainsKey :: "+map.containsKey(currencyKey));
          if(map.containsKey(currencyKey))
          {
                currencyFactor  = Double.parseDouble((String)map.get(currencyKey));
                //System.out.println("currencyFactorcurrencyFactorcurrencyFactor111111111111 :: "+currencyFactor);
          }
          else
          {
              currencyFactor  = operationImpl.getConvertionFactor(rs.getString("CURRENCY"),sellRatesDob.getCurrencyId());
              //System.out.println("currencyFactorcurrencyFactorcurrencyFactor222222222 :: "+currencyFactor);
              map.put(currencyKey,String.valueOf(currencyFactor));
          }
          int chargeRatesArraySize  = chargeRatesArray.length;
          String []  chargeRts       = new String[chargeRatesArraySize];
         // double[]  chargeRts       = new double[chargeRatesArraySize];
          for(int i=0;i<chargeRatesArraySize;i++)
          {
            if("-".equals(chargeRatesArray[i]))
            {
              chargeRts[i] = "-";
            }
            else
            {
              if(currencyFactor>0)
                chargeRts[i]  = ""+operationImpl.getConvertedAmt(Double.parseDouble(chargeRatesArray[i]),currencyFactor);
              else
                chargeRts[i]  = chargeRatesArray[i];
            }
            //System.out.println("chargeRtschargeRtschargeRts in DAO: "+chargeRts[i]);
          }
          qmsSellRatesDob.setChargeRates(chargeRts);
          //qmsSellRatesDob.setChargeRatesValues(chargeRts);
         // System.out.println("currencyFactorcurrencyFactorcurrencyFactor3333333333 :: "+currencyFactor);
          dataList.add(qmsSellRatesDob);
          icount++;
          
        }
        finalList.add(dataList);
        //modified by phani sekhar for wpbn 171213 changed indexes frm 14 to 18 , 15 to 19
        finalList.add(new Integer(csmt.getInt(18)));
        finalList.add(new Integer(csmt.getInt(19)));
      }catch(SQLException se)
      {
        se.printStackTrace();
        //Logger.error(FILE_NAME,"error at getRateDetails()"+se.toString());
        logger.error(FILE_NAME+"error at getRateDetails()"+se.toString());
        throw new EJBException(se.toString());
      }catch(Exception e)
      {
        e.printStackTrace();
        //Logger.error(FILE_NAME,"error at getRateDetails()"+e.toString());
        logger.error(FILE_NAME+"error at getRateDetails()"+e.toString());
        throw new EJBException(e.toString());
      }finally
      {
        try
        {
          if(rs!=null)
            { rs.close();}
          if(rs1!=null)
          { rs1.close();}
          if(pstmt1!=null)
          { pstmt1.close();}
          if(csmt!=null)
            { csmt.close();}
          if(connection!=null)
            { connection.close();}
           operationImpl=null; 
        }catch(SQLException e)
        {
          e.printStackTrace();
          //Logger.error(FILE_NAME,"error at getRateDetails()"+e.toString());
          logger.error(FILE_NAME+"error at getRateDetails()"+e.toString());
          throw new EJBException();         
        }
      }
     return finalList;
  }
public String updateInvalidateStatus(ArrayList valueList)throws SQLException
{
      PreparedStatement     pStmt		 		      =   null;
      String                message           =   null; 
      QMSSellRatesDOB       qmsSellRatesDob   =   null;
      int                   value             =   0;
      try
      {
         getConnection(); 
         pStmt       =   connection.prepareStatement(changeStatusQuery);
         int dobSize =  valueList.size();
         //System.out.println("dobSizedobSizedobSizedobSize in DAO : "+dobSize);
         for(int i=0;i<dobSize;i++)
         {
           qmsSellRatesDob  = (QMSSellRatesDOB)valueList.get(i);
           pStmt.setString(1,qmsSellRatesDob.getInvalidate());
           pStmt.setString(2,qmsSellRatesDob.getRec_buyrate_id());
           pStmt.setString(3,qmsSellRatesDob.getBuyRateId());
           pStmt.setString(4,qmsSellRatesDob.getOrigin());
           pStmt.setString(5,qmsSellRatesDob.getDestination());
           pStmt.setString(6,qmsSellRatesDob.getCarrier_id());
           pStmt.setString(7,qmsSellRatesDob.getServiceLevel());
           pStmt.setString(8,qmsSellRatesDob.getFrequency());
           pStmt.setInt(9,qmsSellRatesDob.getLanNumber());
            pStmt.setString(10, qmsSellRatesDob.getVersionNo());//ADDED BY Phani sekhar for wpbn 180219 on 20090821
              value  = pStmt.executeUpdate();
         }
         //System.out.println("valuevaluevaluevaluevaluevaluevalue ::: "+value);
        if(value==0)
          message = null;
        else
          message = "Updated";
      }                             
      catch(SQLException sqle)
      {
          //Logger.error(FILE_NAME,"SQLEXception in updateInvalidateStatus()-->"+sqle.toString());
          logger.error(FILE_NAME+"SQLEXception in updateInvalidateStatus()-->"+sqle.toString());
          sqle.printStackTrace();
          throw new EJBException(sqle.toString());
      }
      catch(Exception e)
      {
        //Logger.error(FILE_NAME,"EXception in updateInvalidateStatus()-->"+e.toString());
        logger.error(FILE_NAME+"EXception in updateInvalidateStatus()-->"+e.toString());
        e.printStackTrace();
        throw new EJBException(e.toString());
      }
      finally
		  {
		      try
		      {
              if(pStmt!=null)
                {pStmt.close();}
              if(connection!=null)
                {connection.close();}
          }
          catch(Exception ex)
          {
              //Logger.error(FILE_NAME,"Exception caught :: finally :: updateInvalidateStatus() " + ex.toString() );
              logger.error(FILE_NAME+"Exception caught :: finally :: updateInvalidateStatus() " + ex.toString() );
          }
		  
		  }
      return message;
  
}
public ArrayList getSellRatesValesOfView(QMSSellRatesDOB sellRatesDob,ESupplyGlobalParameters loginBean,String operation)throws SQLException
{
  CallableStatement     csmt              =   null;
  ResultSet             rs                =   null;
  String                weightBreaks      =   "";
  String                chargeRates       =   "";
  String                orign             =   null;
  String                destination       =   null;
  String                carrier           =   null;
  String                serviceLevel      =   null;
  String                orignStr          =   null;
  String                destinationStr    =   null;
  String                serviceStr        =   null;
  String                carrierStr        =   null;
  String                rateDesc        =   null;
  String[]              rateDescription        =   null;
  QMSSellRatesDOB       qmsSellRatesDob   =   null;
  String[]              weigthBreaksArray =   null;
  String[]              chargeRatesArray  =   null;
  String                currencyKey       =   null;
  double                currencyFactor    =   0.0;
  String                chargeIn          =  null;
  String[]              chargeIndicator   =  null;
  OperationsImpl        operationImpl     =   null;
  ArrayList             dataList          =   new ArrayList();
  ArrayList             finalList         =   new ArrayList();
  Hashtable             map               =   new Hashtable();
  
 //Added By Kishore Podili For SurCharge Currency
  String[]              surChargeCurrency =   null; 
  String				surChargeCurr     =   null;
  
 PreparedStatement pstmt1 = null;
 ResultSet 				rs1= null;		
 String lovSize	= "SELECT PARAM_VALUE FROM FS_USER_PREFERENCES WHERE USERID='"+loginBean.getUserId()+"'"+" AND LOCATIONID='"+loginBean.getTerminalId()+"' AND  PARAM_NAME='segmentSize'";

  try
  {
      operationImpl       =   new OperationsImpl();
      orignStr            =   sellRatesDob.getOrigin();
      destinationStr      =   sellRatesDob.getDestination();
      serviceStr          =   sellRatesDob.getServiceLevel();
      carrierStr          =   sellRatesDob.getCarrier_id();
      orign               =   orignStr.replaceAll(",","~");
      destination         =   destinationStr.replaceAll(",","~");
      serviceLevel        =   serviceStr.replaceAll(",","~");
      carrier             =   carrierStr.replaceAll(",","~");
      getConnection();
      pstmt1 = connection.prepareStatement(lovSize);
       rs1 =pstmt1.executeQuery();
      int lovsizeVal=0;
	if(rs1.next())
    	  lovsizeVal = rs1.getInt(1);

      csmt = connection.prepareCall("{CALL Qms_Rsr_Rates_Pkg.buy_sell_rates(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)} ");
      csmt.setString(1,orign);
      csmt.setString(2,destination);
      csmt.setString(3,loginBean.getTerminalId());
      csmt.setString(4,sellRatesDob.getAccessLevel());
      csmt.setString(5,sellRatesDob.getRateType());
      csmt.setString(6,sellRatesDob.getWeightBreak());
      csmt.setString(7,serviceLevel);
      csmt.setString(8,carrier);
      csmt.setString(9,sellRatesDob.getCurrencyId());
      csmt.setString(10,sellRatesDob.getShipmentMode());
      csmt.setString(11,operation);
      //System.out.println("getPageNogetPageNogetPageNo IN dao :: "+sellRatesDob.getPageNo());
      csmt.setInt(12,Integer.parseInt(sellRatesDob.getPageNo()));
    // csmt.setInt(13,10);
       csmt.setInt(13,lovsizeVal);
     //csmt.setInt(13,100);
     //added n modified by phani sekhar for wpbn 171213 on 20090609
        csmt.setString(14,sellRatesDob.getOriginCountry());
        csmt.setString(15,sellRatesDob.getDestinationCountry());
        csmt.setString(16,sellRatesDob.getOriginRegions());
        csmt.setString(17,sellRatesDob.getDestRegions());
        
          csmt.registerOutParameter(18,OracleTypes.INTEGER);
          csmt.registerOutParameter(19,OracleTypes.INTEGER);
          csmt.registerOutParameter(20,OracleTypes.CURSOR);
       /*   csmt.registerOutParameter(14,OracleTypes.INTEGER);
          csmt.registerOutParameter(15,OracleTypes.INTEGER);
          csmt.registerOutParameter(16,OracleTypes.CURSOR);*/
          //ends 171213
           csmt.execute();
      int icount    = 0;  
      rs = (ResultSet)csmt.getObject(20);//modified by phani sekhar for wpbn 171213 changed parameter index frm 16 to 20
      //System.out.println("ttttttttttttttttttttttttttttttttttt:: "+rs.next());
      while(rs.next())
      {
          qmsSellRatesDob = new QMSSellRatesDOB();
          //System.out.println("REC_BUYRATE_IDREC_BUYRATE_IDREC_BUYRATE_ID :: "+rs.getString("REC_BUYRATE_ID"));
          qmsSellRatesDob.setBuyRateId(rs.getString("REC_BUYRATE_ID"));
          qmsSellRatesDob.setRec_buyrate_id(rs.getString("BUYRATEID"));
          qmsSellRatesDob.setVersionNo(rs.getString("VERSION_NO"));//@@Added for the WPBN issue-146448 on 23/12/08
          qmsSellRatesDob.setCarrier_id(rs.getString("CARRIER_ID"));
          qmsSellRatesDob.setOrigin(rs.getString("ORIGIN"));
          qmsSellRatesDob.setOriginCountry(rs.getString("ORG_COUNTRYID"));
          qmsSellRatesDob.setDestination(rs.getString("DESTINATION"));
          qmsSellRatesDob.setDestinationCountry(rs.getString("DEST_COUNTRYID"));
          qmsSellRatesDob.setServiceLevel(rs.getString("SERVICE_LEVEL"));
          qmsSellRatesDob.setTransitTime(rs.getString("TRANSIT_TIME"));
          qmsSellRatesDob.setFrequency(rs.getString("FREQUENCY"));
          qmsSellRatesDob.setLanNumber(rs.getInt("LANE_NO"));
          qmsSellRatesDob.setNotes(rs.getString("NOTES"));
          qmsSellRatesDob.setExtNotes(rs.getString("EXTERNAL_NOTES")); //Modified by Mohan for Issue No.219976 on 08-10-2010
          //System.out.println("INVALIDATEINVALIDATEINVALIDATEINVALIDATEINVALIDATE:: "+rs.getString("INVALIDATE"));
          qmsSellRatesDob.setInvalidate(rs.getString("INVALIDATE"));
        int count=0 ,count1=0,count2=0;
              String s1        =  "";
              String s2        =  "";
              String s3        =  "";
              String s4        =  "";
              String s5        =  "";
           chargeRates      = rs.getString("CHARGERATE");
             chargeRatesArray = chargeRates.split(",");
            chargeIn        = rs.getString("C_INDICATOR");
             weightBreaks      = rs.getString("WEIGHT_BREAK_SLAB");
            weigthBreaksArray = weightBreaks.split(",");
            chargeIndicator = new String[weigthBreaksArray.length];
            if(chargeIn!=null)
            {
                chargeIndicator = chargeIn.split(",");
            }
            qmsSellRatesDob.setChargeInr(chargeIndicator);  
           rateDesc        = rs.getString("RATE_DESCRIPTION");
          
            
            if(rateDesc!=null)
            {
            rateDescription = rateDesc.split(",");
            }
            if(rateDescription!=null)
            {
            	int rtDesLen	=	rateDescription.length;
              for(int k=0;k<rtDesLen;k++)
              {
                if("C.P.S.S".equalsIgnoreCase(rateDescription[k]))
                {
                  rateDescription[k]  = "P.S.S";
                }
              }
            }
            
          //Added By Kishore Podili For SurCharge Currency
            surChargeCurr   = rs.getString("SUR_CHARGE_CURRENCY");
            
            if(surChargeCurr!=null)
            {
            surChargeCurrency = surChargeCurr.split(",");
            }
            
        //  qmsSellRatesDob.setRateDescription(rateDescription);
            int process_count	=	0;
            for(int process=0;process<weigthBreaksArray.length;process++)
            {
            	if("**".equalsIgnoreCase(rateDescription[process])){
            		
            				weigthBreaksArray[process]=null;
    						chargeRatesArray[process]=null;
    						chargeIndicator[process]=null;
    						rateDescription[process]=null;
    						surChargeCurrency[process]=null; //Added By Kishore Podili For SurCharge Currency
    						process_count ++;

            	}
            }
            String[] weigthBreaksArray_process = new String[weigthBreaksArray.length-process_count];
            String[] chargeRatesArray_process = new String[weigthBreaksArray.length-process_count];
            String[] chargeIndicator_process = new String[weigthBreaksArray.length-process_count];
            String[] rateDescription_process = new String[weigthBreaksArray.length-process_count];
            String[] surChargeCurrency_process = new String[weigthBreaksArray.length-process_count]; //Added By Kishore Podili For SurCharge Currency
            int process_count1	=	0;
            for(int process=0;process<weigthBreaksArray.length;process++)
            {
            	if(rateDescription[process]!=null){
            		
            		weigthBreaksArray_process[process_count1]	=	weigthBreaksArray[process];
            		chargeRatesArray_process[process_count1]	=	chargeRatesArray[process];
            		chargeIndicator_process[process_count1]	=	chargeIndicator[process];
            		rateDescription_process[process_count1]	=	rateDescription[process];
            		surChargeCurrency_process[process_count1]	=	surChargeCurrency[process];//Added By Kishore Podili For SurCharge Currency
            		process_count1 ++;

            	}
            }
            weigthBreaksArray	=	weigthBreaksArray_process;
            chargeRatesArray	=	chargeRatesArray_process;
            chargeIndicator		=	chargeIndicator_process;
             rateDescription		=	rateDescription_process;
             surChargeCurrency	=	surChargeCurrency_process;	//Added By Kishore Podili For SurCharge Currency
         
              String temp      =  "";
              String[]  weightbreaks          =   new String[weigthBreaksArray.length];  
             String[]  ratebreaks             =   new String[weigthBreaksArray.length];  
             String[]  chargeindicators       =   new String[weigthBreaksArray.length];  
             String[]  breaks                 =   new String[weigthBreaksArray.length];   
             String[]  rates                  =   new String[weigthBreaksArray.length];   
             String[]  indicators             =   new String[weigthBreaksArray.length];   
             String[]  ratedescs              =   new String[weigthBreaksArray.length]; 
            String[]  ratedescription         =   new String[weigthBreaksArray.length]; 
           
            //Added By Kishore Podili For SurCharge Currency
            String[]  surchargecurrs              =   new String[weigthBreaksArray.length]; 
            String[]  surchargecurrency         =   new String[weigthBreaksArray.length]; 
            
          int wtBrkArrLen	=	weigthBreaksArray.length;
      if("SLAB".equalsIgnoreCase(sellRatesDob.getWeightBreak()))
      {
    	  
             for(int n=0;n<wtBrkArrLen;n++)
             {
                if(weigthBreaksArray[n]!=null)
                {
                    if("BASIC".equalsIgnoreCase(weigthBreaksArray[n]) && ( "A FREIGHT RATE".equalsIgnoreCase(rateDescription[n]) || "-".equalsIgnoreCase(rateDescription[n])) )
                    {
                         s4   =   weigthBreaksArray[n];
                         s5 =     chargeRatesArray[n];
                         count++;
                    }
                	
                    else if("MIN".equalsIgnoreCase(weigthBreaksArray[n]) && "A FREIGHT RATE".equalsIgnoreCase(rateDescription[n]))
                {
                     s1   =   weigthBreaksArray[n];
                     s2 =     chargeRatesArray[n];
                     s3 =     chargeIndicator[n];
                     count++;
                }
 /*               else if(weigthBreaksArray[n].startsWith("S")||weigthBreaksArray[n].startsWith("F")||
                weigthBreaksArray[n].startsWith("B")||weigthBreaksArray[n].startsWith("C")||weigthBreaksArray[n].startsWith("P") || !"A FREIGHT RATE".equalsIgnoreCase(rateDescription[n]))
 */               else if(!"A FREIGHT RATE".equalsIgnoreCase(rateDescription[n]) && !"**".equalsIgnoreCase(rateDescription[n]) && !"-".equalsIgnoreCase(rateDescription[n]))
                  {
                  weightbreaks[count2]        =   weigthBreaksArray[n];
                  if(chargeRatesArray[n]!=null)
                   {
                  ratebreaks[count2]          =   chargeRatesArray[n];
                   }
                   if(chargeIndicator[n]!=null)
                   {
                      chargeindicators[count2]    =   chargeIndicator[n];
                   }
                  count2++;
                }
                 else if("A FREIGHT RATE".equalsIgnoreCase(rateDescription[n]) || "-".equalsIgnoreCase(rateDescription[n]))
                  {
                     breaks[count1]        =   weigthBreaksArray[n];
                     if(chargeRatesArray[n]!=null)
                     {
                        rates[count1]         =   chargeRatesArray[n];
                     }
                     if(chargeIndicator[n]!=null)
                     {
                        indicators[count1]    =   chargeIndicator[n];
                     }
                     else
                     {
                        indicators[count1]    = "";
                     }
                     count1++;
                  }
                }
            }
            if(weightbreaks!=null)
            {
            	int wtBrksLen	=	weightbreaks.length;
               for(int j=0;j<wtBrksLen-1;j++)
             {
                if(("FSKG".equalsIgnoreCase(weightbreaks[j])&&"FSMIN".equalsIgnoreCase(weightbreaks[j+1]))||("SSKG".equalsIgnoreCase(weightbreaks[j])&&"SSMIN".equalsIgnoreCase(weightbreaks[j+1]))
                ||("CAF%".equalsIgnoreCase(weightbreaks[j])&&"CAFMIN".equalsIgnoreCase(weightbreaks[j+1]))||"BAFM3".equalsIgnoreCase(weightbreaks[j])&&"BAFMIN".equalsIgnoreCase(weightbreaks[j+1])
                ||"PSSM3".equalsIgnoreCase(weightbreaks[j])&&"PSSMIN".equalsIgnoreCase(weightbreaks[j+1]))
                {
                    temp                   =    weightbreaks[j];
                    weightbreaks[j]        =    weightbreaks[j+1];
                    weightbreaks[j+1]      =     temp;
                    temp                   =     ratebreaks[j];
                    ratebreaks[j]          =     ratebreaks[j+1];
                    ratebreaks[j+1]        =     temp;
                    if(chargeIndicator[j]!=null)
                     {
                        temp                   =     chargeindicators[j];
                        chargeindicators[j]    =     chargeindicators[j+1];
                        chargeindicators[j+1]  =     temp;
                     }
                }
            
             }
            }
            if(breaks!=null)
            {
            	int brksLen	=	breaks.length;
             for(int s=0;s<brksLen;s++)
             {
              for(int r=0;r<brksLen-1;r++)
              {
                if(breaks[r]!=null&&breaks[r+1]!=null)
                {
                if(Double.parseDouble(breaks[r])>Double.parseDouble(breaks[r+1]))//Modified by Mohan for Issue No on 12112010
                 {
                        temp             =     breaks[r];
                        breaks[r]        =     breaks[r+1];
                        breaks[r+1]      =     temp;
                        temp             =     rates[r];
                        rates[r]         =     rates[r+1];
                        rates[r+1]       =     temp;
                        if(indicators[r]!=null)
                       {
                          temp             =     indicators[r];
                          indicators[r]    =     indicators[r+1];
                          indicators[r+1]  =     temp;
                       }
                  }
                }
                }
              }
            }
           //  Collections.sort(breaks);
            if(count==2){
//            	This is FOr BASIC
              weigthBreaksArray[0] = s4;
              chargeRatesArray[0] = s5;
              if(s3!=null)
              {
                chargeIndicator[0]  = s3;
              }
              rateDescription[0] ="A FREIGHT RATE";
              surChargeCurrency[0] = "-"; //Added By Kishore Podili For SurCharge Currency
//              This is for MIN
              weigthBreaksArray[1] = s1;
              chargeRatesArray[1] = s2;
	              if(s3!=null)
              {
                chargeIndicator[1]  = s3;
              }
              rateDescription[1] ="A FREIGHT RATE";
              surChargeCurrency[1] = "-"; //Added By Kishore Podili For SurCharge Currency

            }
            else{
//              This is for MIN
                weigthBreaksArray[0] = s1;
                chargeRatesArray[0] = s2;
                if(s3!=null)
                {
                  chargeIndicator[0]  = s3;
                }
                rateDescription[0] ="A FREIGHT RATE";
                surChargeCurrency[0] = "-"; //Added By Kishore Podili For SurCharge Currency


            }
              if(breaks!=null)
              {
            	  int brksLen	= breaks.length;
             for(int s=0;s<brksLen;s++)
             {
                 if(breaks[s]!=null)
                {
                  weigthBreaksArray[count]   =  breaks[s];
                  chargeRatesArray[count]    =  rates[s];
                  if(indicators[s]!=null)
                  {
                    chargeIndicator[count]     =  indicators[s];
                  }
                  rateDescription[count]     =  "A FREIGHT RATE";
                  surChargeCurrency[count] = "-"; //Added By Kishore Podili For SurCharge Currency
                  count++;
                }
             }
              }
             if(weightbreaks!=null)
             {
            	 int wtBrksLen	=	weightbreaks.length;	
              for(int t=0;t<wtBrksLen;t++)
               {
                 if(weightbreaks[t]!=null)
                {
                  weigthBreaksArray[count]  =   weightbreaks[t];
                 chargeRatesArray[count]   =   ratebreaks[t];
                  if(chargeIndicator[t]!=null)
                  {
                    chargeIndicator[count]    =   chargeIndicator[t];
                  }
                   count++;
                }
               }
             }
          }
          else if("FLAT".equalsIgnoreCase(sellRatesDob.getWeightBreak()))
            {
        	  count = 0;count1=0;count2=0;
             for(int n=0;n<wtBrkArrLen;n++)
             {
                if(weigthBreaksArray[n]!=null)
                {
                	//Added By Kishore Podili for RSR - View Header
                  if( ( "BASIC".equalsIgnoreCase(weigthBreaksArray[n]) || "MIN".equalsIgnoreCase(weigthBreaksArray[n])||"FLAT".equalsIgnoreCase(weigthBreaksArray[n]) ) && "A FREIGHT RATE".equalsIgnoreCase(rateDescription[n]))
                  {
                     breaks[count1]                  =   weigthBreaksArray[n];
                      ratedescription[count1]        =   rateDescription[n];
                      surchargecurrency[count1]		 =	 surChargeCurrency[n]; //Added By Kishore Podili For SurCharge Currency
                     if(chargeRatesArray[n]!=null)
                     {
                        rates[count1]         =   chargeRatesArray[n];
                     }
                     if(chargeIndicator[n]!=null)
                     {
                        indicators[count1]    =   chargeIndicator[n];
                     }
                     else
                     {
                        indicators[count1]    = "";
                     }
                     count1++;
                  }
                  else
                  {
                     weightbreaks[count2]        =   weigthBreaksArray[n];
                      ratedescs[count2]        =   rateDescription[n];
                      surchargecurrs[count2]	=   surChargeCurrency[n]; //Added By Kishore Podili For SurCharge Currency
                    if(chargeRatesArray[n]!=null)
                     {
                    ratebreaks[count2]          =   chargeRatesArray[n];
                     }
                     if(chargeIndicator[n]!=null)
                     {
                        chargeindicators[count2]    =   chargeIndicator[n];
                     }
                    count2++;
                    
                  }
                }
              } 
               if(weightbreaks!=null)
              {
            	   int wtBrksLen	=	weightbreaks.length;
               for(int j=0;j<wtBrksLen-1;j++)
              {
                if(("FSKG".equalsIgnoreCase(weightbreaks[j])&&"FSMIN".equalsIgnoreCase(weightbreaks[j+1]))||("SSKG".equalsIgnoreCase(weightbreaks[j])&&"SSMIN".equalsIgnoreCase(weightbreaks[j+1]))
                ||("CAF%".equalsIgnoreCase(weightbreaks[j])&&"CAFMIN".equalsIgnoreCase(weightbreaks[j+1]))||"BAFM3".equalsIgnoreCase(weightbreaks[j])&&"BAFMIN".equalsIgnoreCase(weightbreaks[j+1])
                ||"PSSM3".equalsIgnoreCase(weightbreaks[j])&&"PSSMIN".equalsIgnoreCase(weightbreaks[j+1]) )
                {
                    temp                   =     weightbreaks[j];
                    weightbreaks[j]        =     weightbreaks[j+1];
                    weightbreaks[j+1]      =     temp;
                    temp                   =     ratebreaks[j];
                    ratebreaks[j]          =     ratebreaks[j+1];
                    ratebreaks[j+1]        =     temp;
                    if(chargeindicators[j]!=null)
                    {
                      temp                   =     chargeindicators[j];
                      chargeindicators[j]    =     chargeindicators[j+1];
                      chargeindicators[j+1]  =     temp;  
                    }
                }
               }
            }
              if(breaks!=null)
                {
            	  int brksLen	=	breaks.length;
               for(int s=0;s<brksLen;s++)
               {
                   if(breaks[s]!=null)
                  {
                    weigthBreaksArray[count]   =  breaks[s];
                    chargeRatesArray[count]    =  rates[s];
                     if(indicators[s]!=null)
                     {
                       chargeIndicator[count]     =  indicators[s];
                     }
                     rateDescription[count]     =  ratedescription[s];
                     surChargeCurrency[count]  = surchargecurrency[s]; //Added By Kishore Podili For SurCharge Currency
                     count++;
                  }
                 }
                }
                 if(weightbreaks!=null)
                 {
                	 int wtBrksLen	=	weightbreaks.length;
                  for(int t=0;t<wtBrksLen;t++)
                   {	
                     if(weightbreaks[t]!=null)
                    {
                      weigthBreaksArray[count]  =   weightbreaks[t];
                      chargeRatesArray[count]   =   ratebreaks[t];
                       if(chargeindicators[t]!=null)
                       {
                          chargeIndicator[count]    =   chargeindicators[t];
                       }
                       rateDescription[count]     =  ratedescs[t];
                       surChargeCurrency[count]   =  surchargecurrs[t]; //Added By Kishore Podili For SurCharge Currency
                       count++;
                    }
                   }
                 }
                    
            }
            else  if("LIST".equalsIgnoreCase(sellRatesDob.getWeightBreak())&&!("FCL".equalsIgnoreCase(sellRatesDob.getConsoleType())))
              {
                      count = 0;count1=0;count2=0;
             for(int n=0;n<wtBrkArrLen;n++)
             {
                if(weigthBreaksArray[n]!=null)
                {
                  if(!("FSBASIC".equalsIgnoreCase(weigthBreaksArray[n])||"FSMIN".equalsIgnoreCase(weigthBreaksArray[n])||"FSKG".equalsIgnoreCase(weigthBreaksArray[n])
                     ||"SSBASIC".equalsIgnoreCase(weigthBreaksArray[n])||"SSMIN".equalsIgnoreCase(weigthBreaksArray[n])||"SSKG".equalsIgnoreCase(weigthBreaksArray[n])||"SURCHARGE".equalsIgnoreCase(weigthBreaksArray[n]) || !"A FREIGHT RATE".equalsIgnoreCase(rateDescription[n])))
                     {
                     breaks[count1]                  =   weigthBreaksArray[n];
                      ratedescription[count1]        =   rateDescription[n];
                     if(chargeRatesArray[n]!=null)
                     {
                        rates[count1]         =   chargeRatesArray[n];
                     }
                     if(chargeIndicator[n]!=null)
                     {
                        indicators[count1]    =   chargeIndicator[n];
                     }
                     else
                     {
                        indicators[count1]    = "";
                     }
                     count1++;
                  }
                  else
                  {
                     weightbreaks[count2]        =   weigthBreaksArray[n];
                      ratedescs[count2]        =   rateDescription[n];
                      surchargecurrs[count2]	=   surChargeCurrency[n]; //Added By Kishore Podili For SurCharge Currency
                    if(chargeRatesArray[n]!=null)
                     {
                    ratebreaks[count2]          =   chargeRatesArray[n];
                     }
                     if(chargeIndicator[n]!=null)
                     {
                        chargeindicators[count2]    =   chargeIndicator[n];
                     }
                    count2++;
                    
                  }
                }
              } 
             if(weightbreaks!=null)
            {
            	 int wtBrksLen	=	weightbreaks.length;
              for(int n=0;n<wtBrksLen-1;n++)
             {
                          if(("FSKG".equalsIgnoreCase(weightbreaks[n])&&"FSMIN".equalsIgnoreCase(weightbreaks[n+1]))||("SSKG".equalsIgnoreCase(weightbreaks[n])&&"SSMIN".equalsIgnoreCase(weightbreaks[n+1])))
                         {
                           temp                    =  weightbreaks[n];
                           weightbreaks[n]         =  weightbreaks[n+1];
                           weightbreaks[n+1]       =  temp;
                    }
                   }
                 }
           if(breaks!=null)
              {
        	   int brksLen	=	 breaks.length;
             for(int s=0;s<brksLen;s++)
             {
                 if(breaks[s]!=null)
                {
                  weigthBreaksArray[count]   =  breaks[s];
                  chargeRatesArray[count]    =  rates[s];
                  if(indicators[s]!=null)
                  {
                    chargeIndicator[count]     =  indicators[s];
                  }
                   rateDescription[count]     = "A FREIGHT RATE";
                   surChargeCurrency[count]   = "-"; //Added By Kishore Podili For SurCharge Currency
                   count++;
                }
               }
              }
             if(weightbreaks!=null)
             {
            	 int wtBrkLen	=	weightbreaks.length;
              for(int t=0;t<wtBrkLen;t++)
               {
                 if(weightbreaks[t]!=null)
                {
                  weigthBreaksArray[count]  =   weightbreaks[t];
                  chargeRatesArray[count]   =   ratebreaks[t];
                  if(chargeindicators[t]!=null)
                  {
                    chargeIndicator[count]    =   chargeindicators[t];
                  }
                   rateDescription[count]     =  ratedescs[t];
                   surChargeCurrency[count]   = surchargecurrs[t]; //Added By Kishore Podili For SurCharge Currency
                   count++;
                }
               }
             }
          }
              
          else if("LIST".equalsIgnoreCase(sellRatesDob.getWeightBreak())&&"FCL".equalsIgnoreCase(sellRatesDob.getConsoleType()))
          {
           for(int n=0;n<wtBrkArrLen;n++)
             {
                if(weigthBreaksArray[n]!=null)
                {
       /*            if(!(weigthBreaksArray[n].endsWith("CAF")||weigthBreaksArray[n].endsWith("BAF")
                   ||weigthBreaksArray[n].endsWith("CSF")||weigthBreaksArray[n].endsWith("PSS") || !"A FREIGHT RATE".equalsIgnoreCase(rateDescription[n])) )
      */            if("A FREIGHT RATE".equalsIgnoreCase(rateDescription[n]) || "-".equalsIgnoreCase(rateDescription[n]) ) 
                	{
                       weightbreaks[count1] = weigthBreaksArray[n];
                       ratedescs[count1]    = "A FREIGHT RATE";
                       surchargecurrs[count1]="-"; //Added By Kishore Podili For SurCharge Currency
                      
                         if(chargeRatesArray[n]!=null)
                       {
                          ratebreaks[count1]          =   chargeRatesArray[n];
                       }
                 
                       if(chargeIndicator[n]!=null)
                       {
                          chargeindicators[count1]    =   chargeIndicator[n];
                       }
//                     count1++;
                   }
      			else{
                    weightbreaks[count1] = weigthBreaksArray[n];
                    ratedescs[count1]    = rateDescription[n];
                    surchargecurrs[count1] = surChargeCurrency[n]; //Added By Kishore Podili For SurCharge Currency
                   
                      if(chargeRatesArray[n]!=null)
                    {
                       ratebreaks[count1]          =   chargeRatesArray[n];
                    }
              
                    if(chargeIndicator[n]!=null)
                    {
                       chargeindicators[count1]    =   chargeIndicator[n];
                    }

      			}
      		count1++;
             
                }
             }
       /*     for(int n=0;n<wtBrkArrLen;n++)
             {
                if(weigthBreaksArray[n]!=null)
                {
                   if(weigthBreaksArray[n].endsWith("BAF"))
                   {
                     weightbreaks[count1] = weigthBreaksArray[n];
                      ratedescs[count1]    = "BAF";
                       if(chargeRatesArray[n]!=null)
                     {
                        ratebreaks[count1]          =   chargeRatesArray[n];
                     }
                     if(chargeIndicator[n]!=null)
                     {
                        chargeindicators[count1]    =   chargeIndicator[n];
                     }
                     count1++;
                   }
                }
             }
             for(int n=0;n<wtBrkArrLen;n++)
             {
                if(weigthBreaksArray[n]!=null)
                {
                   if(weigthBreaksArray[n].endsWith("CAF"))
                   {
                     weightbreaks[count1] = weigthBreaksArray[n];
                      ratedescs[count1]    = "CAF%";
                       if(chargeRatesArray[n]!=null)
                       {
                          ratebreaks[count1]          =   chargeRatesArray[n];
                       }
                       if(chargeIndicator[n]!=null)
                       {
                          chargeindicators[count1]    =   chargeIndicator[n];
                       }
                     count1++;
                   }
                }
             }
              for(int n=0;n<wtBrkArrLen;n++)
             {
                if(weigthBreaksArray[n]!=null)
                {
                   if(weigthBreaksArray[n].endsWith("CSF"))
                   {
                     weightbreaks[count1] = weigthBreaksArray[n];
                      ratedescs[count1]    = "CSF";
                     
                          if(chargeRatesArray[n]!=null)
                       {
                          ratebreaks[count1]          =   chargeRatesArray[n];
                       }
                       if(chargeIndicator[n]!=null)
                       {
                          chargeindicators[count1]    =   chargeIndicator[n];
                       }
                     count1++;
                   }
                }
             }
              for(int n=0;n<wtBrkArrLen;n++)
             {
                if(weigthBreaksArray[n]!=null)
                {
                   if(weigthBreaksArray[n].endsWith("PSS"))
                   {
                     weightbreaks[count1] = weigthBreaksArray[n];
                      ratedescs[count1]    = "PSS";
                     if(chargeRatesArray[n]!=null)
                       {
                         ratebreaks[count1]          =   chargeRatesArray[n];
                       }
                       if(chargeIndicator[n]!=null)
                       {
                          chargeindicators[count1]    =   chargeIndicator[n];
                       }
                     count1++;
                   }
                }
             }
              */
              int rtBreksLen	=	ratebreaks.length;
             for(int i=0;i<rtBreksLen;i++)
             {
                chargeRatesArray[i] = ratebreaks[i];
             
             }
             // if(icount==0)
     
               qmsSellRatesDob.setWeightBreaks(weightbreaks);
            qmsSellRatesDob.setAllWeightBreaks(weightbreaks);
            qmsSellRatesDob.setChargeInr(chargeindicators);
            qmsSellRatesDob.setChargeRates(ratebreaks);
            qmsSellRatesDob.setRateDescription(ratedescs);
            qmsSellRatesDob.setSurChargeCurency(surchargecurrs); //Added By Kishore Podili For SurCharge Currency
        }
       
          /* if(icount==0)
          {*/
            if("FLAT".equalsIgnoreCase(sellRatesDob.getWeightBreak())||"SLAB".equalsIgnoreCase(sellRatesDob.getWeightBreak())||("LIST".equalsIgnoreCase(sellRatesDob.getWeightBreak())&&!("FCL".equalsIgnoreCase(sellRatesDob.getConsoleType()))))
           {
            qmsSellRatesDob.setWeightBreaks(weigthBreaksArray);
             }
        //  }
           if("FLAT".equalsIgnoreCase(sellRatesDob.getWeightBreak())||"SLAB".equalsIgnoreCase(sellRatesDob.getWeightBreak())||("LIST".equalsIgnoreCase(sellRatesDob.getWeightBreak())&&!("FCL".equalsIgnoreCase(sellRatesDob.getConsoleType()))))
           {
            //qmsSellRatesDob.setWeightBreaks(weigthBreaksArray);
              qmsSellRatesDob.setChargeInr(chargeIndicator);
              qmsSellRatesDob.setAllWeightBreaks(weigthBreaksArray);
              qmsSellRatesDob.setChargeRates(chargeRatesArray);
              qmsSellRatesDob.setRateDescription(rateDescription);
              qmsSellRatesDob.setSurChargeCurency(surChargeCurrency); //Added By Kishore Podili For SurCharge Currency
           }
        /*  weightBreaks      = rs.getString("WEIGHT_BREAK_SLAB");
          weigthBreaksArray = weightBreaks.split(",");
          rateDesc         = rs.getString("RATE_DESCRIPTION");//@@Added by kameswari for Surhcharge Enhancements
          rateDescription = rateDesc.split(",");
            qmsSellRatesDob.setRateDescription(rateDescription);
    
          if(icount==0)
            qmsSellRatesDob.setWeightBreaks(weigthBreaksArray);
          qmsSellRatesDob.setAllWeightBreaks(weigthBreaksArray);
          chargeRates      = rs.getString("CHARGERATE");
          //System.out.println("chargeRateschargeRateschargeRateschargeRates :: in dao "+chargeRates);
          chargeRatesArray = chargeRates.split(",");

          //qmsSellRatesDob.setChargeRates(chargeRatesArray);*/
          
           currencyKey    =  sellRatesDob.getCurrencyId()+"&"+rs.getString("CURRENCY");
         //System.out.println("currencyKeycurrencyKeycurrencyKeycurrencyKeycurrencyKey :: "+currencyKey);
         //System.out.println("containsKeycontainsKeycontainsKeycontainsKey :: "+map.containsKey(currencyKey));
          if(map.containsKey(currencyKey))
          {
                currencyFactor  = Double.parseDouble((String)map.get(currencyKey));
                //System.out.println("currencyFactorcurrencyFactorcurrencyFactor111111111111 :: "+currencyFactor);
          }
          else
          {
              currencyFactor  = operationImpl.getConvertionFactor(rs.getString("CURRENCY"),sellRatesDob.getCurrencyId());
              //System.out.println("currencyFactorcurrencyFactorcurrencyFactor222222222 :: "+currencyFactor);
              map.put(currencyKey,String.valueOf(currencyFactor));
          }
          int chargeRatesArraySize  = chargeRatesArray.length;
          double[]  chargeRts       = new double[chargeRatesArraySize];
          for(int i=0;i<chargeRatesArraySize;i++)
          {
          if(currencyFactor>0)
              chargeRts[i]  = operationImpl.getConvertedAmt(Double.parseDouble(chargeRatesArray[i]),currencyFactor);
            else
              chargeRts[i]  = Double.parseDouble(chargeRatesArray[i]);
                           
          }
          qmsSellRatesDob.setChargeRatesValues(chargeRts);
          
          dataList.add(qmsSellRatesDob);
          icount++;
      }
      finalList.add(dataList);
       //modified by phani sekhar for wpbn 171213 changed parameter index frm 14 to 18,15 to 19
      finalList.add(new Integer(csmt.getInt(18)));
      finalList.add(new Integer(csmt.getInt(19)));
  }
  catch(SQLException se)
  {
    se.printStackTrace();
    //Logger.error(FILE_NAME,"error at getSellRatesValesOfView()"+se.toString());
    logger.error(FILE_NAME+"error at getSellRatesValesOfView()"+se.toString());
    throw new EJBException(se.toString());
  }
  catch(Exception e)
  {
    e.printStackTrace();
    //Logger.error(FILE_NAME,"error at getSellRatesValesOfView()"+e.toString());
    logger.error(FILE_NAME+"error at getSellRatesValesOfView()"+e.toString());
    throw new EJBException(e.toString());
  }
  finally
  {
      try
      {
          if(rs!=null)
            { rs.close();}
          if(rs1!=null)
          { rs1.close();}

          if(csmt!=null)
            { csmt.close();}
          if(connection!=null)
            { connection.close();}
          if(pstmt1!=null)
        	  pstmt1.close();
            operationImpl=null; 
      }
      catch(SQLException e)
      {
          e.printStackTrace();
          //Logger.error(FILE_NAME,"error at getRateDetails()"+e.toString());
          logger.error(FILE_NAME+"error at getRateDetails()"+e.toString());
          throw new EJBException();         
      }
    }
  return finalList;
}
 public StringBuffer isExetIds(QMSSellRatesDOB sellDob)throws SQLException
{
      PreparedStatement     pStmt		 		      =   null;
      ResultSet    					rs            	  = 	null;
          
      StringBuffer          errorMassege      =   null;
      String                message           =   ""; 
      
      String                orgValues         =   null;
      String                destValues        =   null;
      String                carrValues        =   null;
      String                serValues         =   null;
      
      String[]              oValues           =   null;
      String[]              dValues           =   null;
      String[]              cValues           =   null;
      String[]              sValues           =   null;
      
      String                currValues        =   null;
      boolean               oFlag             =   false;
      String                currency          =   null;
      String 				regionsQuery = null;//added by phani sekhar for wpbn 179350
      try
      {
         
          errorMassege  = new StringBuffer();
          getConnection(); 
          
          //System.out.println("sellDob.getOrigin()sellDob.getOrigin() ::: in dao : "+sellDob.getOrigin());
          if(sellDob.getOrigin()!=null && sellDob.getOrigin().length()>0)
          {
            //System.out.println("getShipmentModegetShipmentMode in dao:: "+sellDob.getShipmentMode());
              if("2".equals(sellDob.getShipmentMode()))
                pStmt             =   connection.prepareStatement(portmaterQuery);
              else
                pStmt             =   connection.prepareStatement(originQuery);
              orgValues         =   sellDob.getOrigin(); 
              oValues           =   orgValues.split(",");
              int originIdsSize =   oValues.length;
              //System.out.println("originIdsSize in dao :: "+originIdsSize);
              for(int i=0;i<originIdsSize;i++)
              {
                //orgValues = (String)originIds.get(i);
                //System.out.println("originIds in dao :: "+oValues[i]);
                pStmt.setString(1,oValues[i]);
                rs = pStmt.executeQuery();
                if(!rs.next())
                {
                  message += oValues[i]+" ";
                  oFlag  = true;
                }
                if(rs!=null)rs.close();
              }
              if(oFlag || message!="")
                errorMassege.append(message+" :Orign Ids are not valid .<br>");
              else
                errorMassege.append(message);
                
            if(rs!=null)rs.close();
            if(pStmt!=null)pStmt.close();
          }
          if(sellDob.getDestination()!=null && sellDob.getDestination().length()>0)
          {
              destValues      =   sellDob.getDestination();  
              if("2".equals(sellDob.getShipmentMode()))
                pStmt             =   connection.prepareStatement(portmaterQuery);
              else
                pStmt             =   connection.prepareStatement(originQuery);
              oFlag = false;
              message = "";
              dValues     =   destValues.split(",");
              int destIdsSize = dValues.length;
              //System.out.println("destIds.size()destIds.size()destIds.size()  :: "+destIdsSize);
              //System.out.println("destIdsSize in dao :: "+destIdsSize);
              for(int i=0;i<destIdsSize;i++)
              {
                //destValues = (String)destIds.get(i);
                //System.out.println("destValues in dao :: "+dValues[i]);
                pStmt.setString(1,dValues[i]);
                rs = pStmt.executeQuery();
                if(!rs.next())
                {
                  message += dValues[i]+" ";
                  oFlag  = true;
                }
                if(rs!=null)rs.close();
          }
          if(oFlag || message!="")
            errorMassege.append(message+" :Destination Ids are not valid .<br>");
          else
            errorMassege.append(message);
            
          if(rs!=null)rs.close();
          if(pStmt!=null)pStmt.close();
        }
        if(sellDob.getCarrier_id()!=null && sellDob.getCarrier_id().length()>0) 
        {  
          carrValues    =   sellDob.getCarrier_id();
          pStmt         =   connection.prepareStatement(carrierQuery);
          message = ""; 
          oFlag = false;
          cValues   =   carrValues.split(",");
          int carrierIdsSize = cValues.length;
          //System.out.println("carrierIdsSize in dao :: "+carrierIdsSize);
          for(int i=0;i<carrierIdsSize;i++)
          {
            //carrValues = (String)carrierIds.get(i);
            //System.out.println("carrValues in dao :: "+cValues[i]);
            pStmt.setString(1,cValues[i]);
            rs = pStmt.executeQuery();
            if(!rs.next())
            {
              message += cValues[i]+" ";
              oFlag  = true;
            }
          
            if(rs!=null)rs.close();
          }
          if(oFlag || message!="")
            errorMassege.append(message+" :Carriers Ids are not valid .<br>");
          else
            errorMassege.append(message);
          
          if(rs!=null)rs.close();
          if(pStmt!=null)pStmt.close();
        }
        if(sellDob.getServiceLevel()!=null && sellDob.getServiceLevel().length()>0) 
        { 
          serValues   =     sellDob.getServiceLevel();
          pStmt       =     connection.prepareStatement(serviceQuery);
          message = "";
          oFlag = false;
          sValues   =   serValues.split(",");
          int serviceIdsSize = sValues.length;
          //System.out.println("serviceIdsSize in dao :: "+serviceIdsSize);
          for(int i=0;i<serviceIdsSize;i++)
          {
            //serValues = (String)serviceIds.get(i);
           // System.out.println("serValues in dao :: "+sValues[i]);
            pStmt.setString(1,sValues[i]);
            rs = pStmt.executeQuery();
            if(!rs.next())
            {
              message += sValues[i]+" ";
              oFlag  = true;
            }
          
            if(rs!=null)rs.close();
          }
          if(oFlag || message!="")
            errorMassege.append(message+" :ServiceLevel Ids are not valid .<br>");
          else
            errorMassege.append(message);
          
          if(rs!=null)rs.close();
          if(pStmt!=null)pStmt.close();
        } 
        if(sellDob.getCurrencyId()!=null && sellDob.getCurrencyId().length()>0)
        {
          pStmt       =   connection.prepareStatement(currencyQuery);
          message = ""; 
          oFlag = false;
          pStmt.setString(1,sellDob.getCurrencyId());
          rs = pStmt.executeQuery();
          if(!rs.next())
          {
            message += sellDob.getCurrencyId();
            oFlag  = true;
          }
         
          if(oFlag || message!="")
            errorMassege.append(message+" :Currency Id is not valid .");
          else
            errorMassege.append(message);
        }
          // added by phani sekhar for wpbn 179530 on 20090813
        regionsQuery = "SELECT * FROM FS_COUNTRYMASTER CM WHERE CM.REGION=? ";
        if(rs!=null)rs.close();
        if(pStmt!=null)pStmt.close();
        pStmt       =   connection.prepareStatement(regionsQuery);
        
        if((sellDob.getOriginRegions()!=null && sellDob.getOriginRegions().length()>0))
        {
        	message = ""; 
        	 pStmt.setString(1,sellDob.getOriginRegions());
             rs = pStmt.executeQuery();
             if(!rs.next())
             {
            	 message += sellDob.getOriginRegions();
                 oFlag  = true;
             }
             if(oFlag || message!="")
                 errorMassege.append(message+" :Origin Region is not valid .");
               else
                 errorMassege.append(message);	
             if(rs!=null)rs.close();
             
        }
        if(sellDob.getDestRegions()!=null && sellDob.getDestRegions().length()>0)
        {
        	if(pStmt!=null)pStmt.clearParameters();
        	message = ""; 
        	 pStmt.setString(1,sellDob.getDestRegions());
             rs = pStmt.executeQuery();
             if(!rs.next())
             {
            	 message += sellDob.getDestRegions();
                 oFlag  = true;
             }
             if(oFlag || message!="")
                 errorMassege.append(message+" :Destination Region is not valid .");
               else
                 errorMassege.append(message);	 
        }
        //ends 179530
      }
      catch(SQLException sqle)
      {
          //Logger.error(FILE_NAME,"SQLEXception in isExetIds()-->"+sqle.toString());
          logger.error(FILE_NAME+"SQLEXception in isExetIds()-->"+sqle.toString());
          sqle.printStackTrace();
          throw new EJBException(sqle.toString());
      }
      catch(Exception e)
      {
        //Logger.error(FILE_NAME,"EXception in isExetIds()-->"+e.toString());
        logger.error(FILE_NAME+"EXception in isExetIds()-->"+e.toString());
        e.printStackTrace();
        throw new EJBException(e.toString());
      }
      finally
		  {
		      try
		      {
             if(rs!=null)
                {rs.close();}
              if(pStmt!=null)
                {pStmt.close();}
              if(connection!=null)
                {connection.close();}
          }
          catch(Exception ex)
          {
              //Logger.error(FILE_NAME,"Exception caught :: finally :: isExetIds() " + ex.toString() );
              logger.error(FILE_NAME+"Exception caught :: finally :: isExetIds() " + ex.toString() );
          }
		  
		  }
      return errorMassege;
 }

public ArrayList getMarginOfValues(QMSSellRatesDOB sellRatesDob,String operation)throws SQLException
{
      PreparedStatement     pStmt		 		      =   null;
      ResultSet    					rs            	  = 	null;
      ArrayList             valueList         =   null;
      String                displayQuery      =   null;
      QMSSellRatesDOB       perSellDob        =   null;
      try
      {
          valueList   = new ArrayList();
        if("Acceptance".equals(operation))
        {
          //@@Commented and Modified by Kameswari for Surcharge Enhancements
          /*displayQuery        =   " SELECT SD.WEIGHTBREAKSLAB WEIGHTBREAKSLAB,SM.WEIGHT_BREAK WEIGHT_BREAK,SM.OVERALL_MARGIN OVERALL_MARGIN,SM.MARGIN_TYPE MARGIN_TYPE,SD.ORIGIN ORIGIN,"+
                                  " SD.DESTINATION DESTINATION,SD.CARRIER_ID CARRIER_ID,SD.SERVICELEVEL_ID SERVICELEVEL_ID,SD.TRANSIT_TIME TRANSIT_TIME,SD.CHARGERATE CHARGERATE,"+
                                  " SD.MARGIN_PERC MARGIN_PERC FROM QMS_REC_CON_SELLRATESMSTR_ACC SM,QMS_REC_CON_SELLRATESDTL_ACC SD WHERE SM.REC_CON_ID=SD.REC_CON_ID AND"+ 
                                  " SM.SHIPMENT_MODE=? AND SM.WEIGHT_BREAK =? AND SM.RATE_TYPE=? AND SM.RC_FLAG=? AND SD.ORIGIN=? AND SD.DESTINATION=? AND"+ 
                                  " SD.CARRIER_ID=? AND SD.SERVICELEVEL_ID=? AND SD.AI_FLAG<>'I' AND SD.BUYRATEID=? AND SD.FREQUENCY=?";*/
        displayQuery        =   " SELECT SD.WEIGHTBREAKSLAB WEIGHTBREAKSLAB,SM.WEIGHT_BREAK WEIGHT_BREAK,SM.OVERALL_MARGIN OVERALL_MARGIN,SM.MARGIN_TYPE MARGIN_TYPE,SD.ORIGIN ORIGIN,"+
                                  " SD.DESTINATION DESTINATION,SD.CARRIER_ID CARRIER_ID,SD.SERVICELEVEL_ID SERVICELEVEL_ID,SD.TRANSIT_TIME TRANSIT_TIME,SD.CHARGERATE CHARGERATE,"+
                                  " SD.MARGIN_PERC MARGIN_PERC FROM QMS_REC_CON_SELLRATESMSTR_ACC SM,QMS_REC_CON_SELLRATESDTL_ACC SD WHERE SM.REC_CON_ID=SD.REC_CON_ID AND"+ 
                                  " SM.SHIPMENT_MODE=? AND SM.WEIGHT_BREAK =? AND SM.RATE_TYPE=? AND SM.RC_FLAG=? AND SD.ORIGIN=? AND SD.DESTINATION=? AND"+ 
                                  " SD.CARRIER_ID=? AND (SD.SERVICELEVEL_ID=? or SD.SERVICELEVEL_ID='SCH') AND SD.AI_FLAG<>'I' AND SD.BUYRATEID=? AND SD.FREQUENCY=?";
        }
        else
        {
           //@@Commented and Modified by Kameswari for Surcharge Enhancements
          /*displayQuery        =   " SELECT SD.WEIGHTBREAKSLAB WEIGHTBREAKSLAB,SM.WEIGHT_BREAK WEIGHT_BREAK,SM.OVERALL_MARGIN OVERALL_MARGIN,SM.MARGIN_TYPE MARGIN_TYPE,SD.ORIGIN ORIGIN,"+
                                  " SD.DESTINATION DESTINATION,SD.CARRIER_ID CARRIER_ID,SD.SERVICELEVEL_ID SERVICELEVEL_ID,SD.TRANSIT_TIME TRANSIT_TIME,SD.CHARGERATE CHARGERATE,"+
                                  " SD.MARGIN_PERC MARGIN_PERC,SD.LINE_NO LINE_NO FROM QMS_REC_CON_SELLRATESMASTER SM,QMS_REC_CON_SELLRATESDTL SD WHERE SM.REC_CON_ID=SD.REC_CON_ID AND"+ 
                                  " SM.TERMINALID=? AND SM.SHIPMENT_MODE=? AND SM.WEIGHT_BREAK =? AND SM.RATE_TYPE=? AND SM.RC_FLAG=? AND SD.ORIGIN=? AND SD.DESTINATION=? AND"+ 
                                  " SD.CARRIER_ID=? AND SD.SERVICELEVEL_ID=? AND SD.AI_FLAG<>'I' AND SD.BUYRATEID=? AND SD.FREQUENCY=? ORDER BY SD.LINE_NO";*/
          displayQuery        =   " SELECT SD.WEIGHTBREAKSLAB WEIGHTBREAKSLAB,SM.WEIGHT_BREAK WEIGHT_BREAK,SM.OVERALL_MARGIN OVERALL_MARGIN,SM.MARGIN_TYPE MARGIN_TYPE,SD.ORIGIN ORIGIN,"+
                                  " SD.DESTINATION DESTINATION,SD.CARRIER_ID CARRIER_ID,SD.SERVICELEVEL_ID SERVICELEVEL_ID,SD.TRANSIT_TIME TRANSIT_TIME,SD.CHARGERATE CHARGERATE,"+
                                  " SD.MARGIN_PERC MARGIN_PERC,SD.LINE_NO LINE_NO FROM QMS_REC_CON_SELLRATESMASTER SM,QMS_REC_CON_SELLRATESDTL SD WHERE SM.REC_CON_ID=SD.REC_CON_ID AND"+ 
                                  " SM.TERMINALID=? AND SM.SHIPMENT_MODE=? AND SM.WEIGHT_BREAK =? AND SM.RATE_TYPE=? AND SM.RC_FLAG=? AND SD.ORIGIN=? AND SD.DESTINATION=? AND"+ 
                                  " SD.CARRIER_ID=? AND (SD.SERVICELEVEL_ID=? or SD.SERVICELEVEL_ID='SCH') AND SD.AI_FLAG<>'I' AND SD.BUYRATEID=? AND SD.FREQUENCY=? ORDER BY SD.LINE_NO";
      
        }
         getConnection(); 
         
         pStmt       =   connection.prepareStatement(displayQuery);
         
         pStmt.setString(1,sellRatesDob.getTerminalId());
         pStmt.setString(2,sellRatesDob.getShipmentMode());
         pStmt.setString(3,sellRatesDob.getWeightBreak());
         pStmt.setString(4,sellRatesDob.getRateType());
         pStmt.setString(5,"R");
         pStmt.setString(6,sellRatesDob.getOrigin());
         pStmt.setString(7,sellRatesDob.getDestination());
         pStmt.setString(8,sellRatesDob.getCarrier_id());
         pStmt.setString(9,sellRatesDob.getServiceLevel());
         pStmt.setString(10,sellRatesDob.getBuyRateId());
         pStmt.setString(11,sellRatesDob.getFrequency());
         rs = pStmt.executeQuery();
         while(rs.next())
         {
            perSellDob  = new QMSSellRatesDOB();
            perSellDob.setWeightBreakSlab(rs.getString("WEIGHTBREAKSLAB"));
            perSellDob.setWeightBreak(rs.getString("WEIGHT_BREAK"));
            perSellDob.setOverAllMargin(rs.getString("OVERALL_MARGIN"));
            perSellDob.setMarginType(rs.getString("MARGIN_TYPE"));
            perSellDob.setOrigin(rs.getString("ORIGIN"));
            perSellDob.setDestination(rs.getString("DESTINATION"));
            perSellDob.setCarrier_id(rs.getString("CARRIER_ID"));
            perSellDob.setServiceLevel(rs.getString("SERVICELEVEL_ID"));
            perSellDob.setTransitTime(rs.getString("TRANSIT_TIME"));
            perSellDob.setMinimumRate(rs.getDouble("CHARGERATE"));
            perSellDob.setMarginPer(rs.getDouble("MARGIN_PERC"));
            valueList.add(perSellDob);
         }
          
      }                             
      catch(SQLException sqle)
      {
          //Logger.error(FILE_NAME,"SQLEXception in getMarginOfValues()-->"+sqle.toString());
          logger.error(FILE_NAME+"SQLEXception in getMarginOfValues()-->"+sqle.toString());
          sqle.printStackTrace();
          throw new EJBException(sqle.toString());
      }
      catch(Exception e)
      {
        //Logger.error(FILE_NAME,"EXception in getMarginOfValues()-->"+e.toString());
        logger.error(FILE_NAME+"EXception in getMarginOfValues()-->"+e.toString());
        e.printStackTrace();
        throw new EJBException(e.toString());
      }
      finally
		  {
		      try
		      {
             if(rs!=null)
                {rs.close();}
              if(pStmt!=null)
                {pStmt.close();}
              if(connection!=null)
                {connection.close();}
          }
          catch(Exception ex)
          {
              //Logger.error(FILE_NAME,"Exception caught :: finally :: getMarginOfValues() " + ex.toString() );
              logger.error(FILE_NAME+"Exception caught :: finally :: getMarginOfValues() " + ex.toString() );
          }
      }
      return valueList;
}
public ArrayList getTerminalIds(ESupplyGlobalParameters loginBean,String operation)throws SQLException
{
      PreparedStatement     pStmt		 		      =   null;
      ResultSet             rs                =   null;
      QMSSellRatesDOB       qmsSellRatesDob   =   null;
      int                   value             =   0;
      ArrayList             terminalList      =   null;
      String                accessLevel       =   null;
      String                terminalId        =   null;
      String                terminalQuery     =   null;
      try
      {
          getConnection(); 
          accessLevel       =	loginBean.getUserLevel();
          terminalId        = loginBean.getTerminalId();
          terminalList      = new ArrayList();
          //System.out.println("accessLevelaccessLevelaccessLevel in Dao :: "+accessLevel);
          if("HO_TERMINAL".equalsIgnoreCase(accessLevel))
          {
            terminalQuery = "SELECT DISTINCT TERMINALID FROM FS_FR_TERMINALMASTER";
          }
          else
          { 
            terminalQuery = "SELECT '"+terminalId+"' TERMINALID FROM DUAL UNION SELECT CHILD_TERMINAL_ID TERMINALID FROM FS_FR_TERMINAL_REGN CONNECT BY PRIOR CHILD_TERMINAL_ID = PARENT_TERMINAL_ID START WITH PARENT_TERMINAL_ID ='"+terminalId+"'";
          }
          pStmt             =   connection.prepareStatement(terminalQuery);
          rs = pStmt.executeQuery();
          while(rs.next())
          {
            terminalList.add(rs.getString("TERMINALID"));
          }
      }                             
      catch(SQLException sqle)
      {
          //Logger.error(FILE_NAME,"SQLEXception in getTerminalIds() DAO-->"+sqle.toString());
          logger.error(FILE_NAME+"SQLEXception in getTerminalIds() DAO-->"+sqle.toString());
          sqle.printStackTrace();
          throw new EJBException(sqle.toString());
      }
      catch(Exception e)
      {
        //Logger.error(FILE_NAME,"EXception in getTerminalIds()DAO-->"+e.toString());
        logger.error(FILE_NAME+"EXception in getTerminalIds()DAO-->"+e.toString());
        e.printStackTrace();
        throw new EJBException(e.toString());
      }
      finally
		  {
		      try
		      {
              if(rs!=null)
                {rs.close();}
              if(pStmt!=null)
                {pStmt.close();}
              if(connection!=null)
                {connection.close();}
          }
          catch(Exception ex)
          {
              //Logger.error(FILE_NAME,"Exception caught :: finally :: getTerminalIds() " + ex.toString() );
              logger.error(FILE_NAME+"Exception caught :: finally :: getTerminalIds() " + ex.toString() );
          }
		  
		  }
      return terminalList;
  }
}