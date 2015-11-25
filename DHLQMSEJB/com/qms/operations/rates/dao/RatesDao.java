//package com.qms.operations.rates.dao;
package com.qms.operations.rates.dao;
import com.foursoft.esupply.common.java.LookUpBean;
import com.foursoft.esupply.common.util.ConnectionUtil;
import com.foursoft.esupply.common.util.ESupplyDateUtility;
//import com.foursoft.esupply.common.util.Logger;
import org.apache.log4j.Logger;
import com.foursoft.etrans.common.util.java.OperationsImpl;

import com.qms.operations.rates.dob.FlatRatesDOB;
import com.qms.operations.rates.dob.RateDOB;

import com.qms.operations.sellrates.java.QMSBoundryDOB;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

import com.foursoft.esupply.common.bean.ESupplyGlobalParameters;
import com.qms.operations.sellrates.java.QMSSellRatesDOB;
import java.sql.Types;
import java.util.*;

import javax.ejb.EJBException;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import javax.sql.DataSource;
import oracle.jdbc.OracleTypes;

public class RatesDao 
{

  private static final 	String 		FILE_NAME		= "RatesDao.java";
  private static Logger logger = null;
   private DataSource dataSource = null;
   
     
	 public RatesDao()  
	 {
   logger  = Logger.getLogger(RatesDao.class);
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
   
  // public ArrayList crateBuyFlatRate(RateDOB rateDOB,ArrayList list)throws Exception
   public ArrayList crateBuyFlatRate(RateDOB rateDOB,ArrayList laneslist)throws Exception
   {
	   System.out.println("IN crateBuyFlatRate method");
      PreparedStatement		pStmt			      = null;
      PreparedStatement		pStmt1			      = null;//@@Added by Kameswari for the WPBN issue-146448 
      PreparedStatement   pFrqStmt        = null;
      CallableStatement   cStmt           = null;
      CallableStatement   cStmt1           = null;//@@Added by Kameswari for the WPBN issue-146448 
       CallableStatement   cStmt2           = null;
      PreparedStatement     pStmtQuote           =    null;// commented by govind on 16-02-2010 for Connection Leakages //added by VLAKSHMI for issue 146968 on 23/12/2008
      PreparedStatement     pstmtQmsQuoteInsert =    null;//commented by govind on 16-02-2010 for Connection Leakages //added by VLAKSHMI for issue 146968 on 23/12/2008
      PreparedStatement     pstmtSelectQuoteId   =    null;//commented by govind on 16-02-2010 for Connection Leakages //added by VLAKSHMI for issue 146968 on 23/12/2008
    //  PreparedStatement     pStmtQuoteBreak         =    null;//commented by govind on 16-02-2010 for Connection Leakages //added by VLAKSHMI for issue 146968 on 23/12/2008
      PreparedStatement     pstmtQmsQuoteupdate  =    null; //added by VLAKSHMI for issue 146968 on 23/12/2008
      PreparedStatement    pStmtCount     = null;   
      Connection				  connection		  = null;
      String              master_qry      = null;
      String              seq_qry         = null;
      String              update_qry      = null;
      String              log_qry         = null;
      String              dtl_qry         = null;
      String              freq_qry        = null;
      FlatRatesDOB        flatRatesDOB    = null;
      OperationsImpl      impl            = null;
      ResultSet           rs              = null;
      ResultSet           rs1             = null;//@@Added by Kameswari for the WPBN issue-146448 
      ResultSet				rsQuote					     = 	null;// added by VLAKSHMI for issue 146968 on 23/12/2008
    //ResultSet				rsQmsQuoteInsert		 = 	null;//commented by govind on 16-02-2010 for Connection Leakages // added by VLAKSHMI for issue 146968 on 23/12/2008
      ResultSet				rsSelectQuoteId			 = 	null;// added by VLAKSHMI for issue 146968 on 23/12/2008
      //ResultSet			  rsQuoteBreak		     = 	null;//commented by govind on 16-02-2010 for Connection Leakages // added by VLAKSHMI for issue 146968 on 23/12/2008
      //ResultSet				rsQmsQuoteupdate	 = 	null;//commented by govind on 16-02-2010 for Connection Leakages // added by VLAKSHMI for issue 146968 on 23/12/2008
      ResultSet				rsCount		          = 	null;
      String              buyRateID       = null;
      String              proc_inactivate = null;
      String              shipmentMode    = null;
      String              terminalId      = null;
      String              weightBreak     = null;
      String              rateType        = null;
      //String              returnStr       = null;
      String[]              returnStr       = null;//@@Modified for the WPBN issues-146448,146968 on 18/12/08      
      int                 lineNo          = 0;
      int                 count           = 0;
      int                 count1           = 0;//@@Added by Kameswari for the WPBN issue-146448 
      int                 laneNo          = -1;
      boolean             flagDtl            = false;
      String              str_freq        =  null;
      StringBuffer        sb_successErrorMsg     = null;
      StringBuffer        sb_failErrorMsg  = null;
      String[]            freqArray        = null;
      String              frequery         = null;//@@Added by Kameswari for the WPBN issue-146448 
      String              quotefrequery    = null;//@@Added by Kameswari for the WPBN issue-146448 
      String              slabquery         = null;//@@Added by Kameswari for the WPBN issue-146448 
     String               versionQuery     = null;
      String              updatequery       = null;
      String              selectquery       = null;
      String              buyrate_id        = null;
      String              lane_no          = null;
      ArrayList           buyrateid         = new ArrayList();
      ArrayList          laneno             = new ArrayList();
      ArrayList           quoteid           = new ArrayList();
      ArrayList           id                = new ArrayList();
       ArrayList          quoterateid       = new ArrayList();
      ArrayList       ratelaneno            = new ArrayList();
      String          breakslab              = "";
      String          chargerate             = "";
      ArrayList       breakslab1             = new ArrayList();
      ArrayList       chargerate1            = new ArrayList();
      ArrayList       list                   = new ArrayList();
      String          returnStr1             = null;//@@Added for the WPBN issues-146448,146968 on 18/12/08    
      String          quote_qry              = null;// added by VLAKSHMI for issue 146968 on 25/12/2008
      String          oldbuyrateid           = null;
      String          oldlaneno              = null;
      int             versionno              = 1;
      int             temp = 0;
     String           quoteId =null;// added by VLAKSHMI for issue 146968 on 23/12/2008
     String           qmsQuoteInsert         = null;// added by VLAKSHMI for issue 146968 on 23/12/2008
     String           shipmodeStr            = null;// added by VLAKSHMI for issue 146968 on 23/12/2008
     String           selectQuoteIdFlag      = null;// added by VLAKSHMI for issue 146968 on 23/12/2008
     String           quote_Break_qry        = null;// added by VLAKSHMI for issue 146968 on 23/12/2008
     String           qmsQuoteupdate         = null;// added by VLAKSHMI for issue 146968 on 23/12/2008
     String           selectquote_qry        = null;
     String           updatequote_qry        = null;
     String           countqry               = null;
     String           servicelevel           = null;    
     String           effectiveFrom[]         = null;  
      String           validUpto[]            = null;    
      ArrayList        list_errorList        = null;
      ArrayList       tempbuyrateid          = new ArrayList();
      int           buyratesize             = 0;
      int           temp1                   = 0;  
      int           count2                  = 0;
        try
			   {
          list_errorList = new ArrayList();
          
          
          sb_successErrorMsg = new StringBuffer();
          sb_failErrorMsg = new StringBuffer();
          
          list_errorList.add(sb_successErrorMsg);
          list_errorList.add(sb_failErrorMsg);
          
         impl = new OperationsImpl();
         
        //seq_qry     = " SELECT BUYRATE_SEQ.NEXTVAL BUYRATEID FROM DUAL  "; 
        seq_qry     = " SELECT BUYRATE_SEQ.NEXTVAL BUYRATEID FROM DUAL  "; 
        /*master_qry =  " INSERT INTO QMS_BUYRATES_MASTER (SHIPMENT_MODE,  CURRENCY,  UOM, WEIGHT_BREAK,  "+
                      " RATE_TYPE, CONSOLE_TYPE, CREATED_BY, DEL_FLAG, BUYRATEID, WEIGHT_CLASS, ACCESSLEVEL, TERMINALID  )"+
                      " VALUES (?,?,?,?,?,?,?,?,?,?,?,?) ";
                      
       dtl_qry    = "     INSERT INTO QMS_BUYRATES_DTL (BUYRATEID ,ORIGIN, DESTINATION, SERVICE_LEVEL,  "
                      +"  FREQUENCY, TRANSIT_TIME, CHARGERATE, WEIGHT_BREAK_SLAB, LANE_NO, LOWERBOUND ,UPPERBOUND  ,"
                      +"   NOTES, LINE_NO,CARRIER_ID,CHARGERATE_INDICATOR,CREATEDTIME ,OVER_PIVOT,EFFECTIVE_FROM, VALID_UPTO,ID,DENSITY_CODE,RATE_DESCRIPTION) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,SEQ_BUYRATES_DTL.nextval,?,?) ";*/

      master_qry =  " INSERT INTO QMS_BUYRATES_MASTER (SHIPMENT_MODE,  CURRENCY,  UOM, WEIGHT_BREAK,  "+
                      " RATE_TYPE, CONSOLE_TYPE, CREATED_BY, DEL_FLAG, BUYRATEID, WEIGHT_CLASS, ACCESSLEVEL, TERMINALID,VERSION_NO,LANE_NO)"+
                      " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?) ";
                      
       dtl_qry    = "     INSERT INTO QMS_BUYRATES_DTL (BUYRATEID ,ORIGIN, DESTINATION, SERVICE_LEVEL,  "
                      +"  FREQUENCY, TRANSIT_TIME, CHARGERATE, WEIGHT_BREAK_SLAB, LANE_NO, LOWERBOUND ,UPPERBOUND  ,"
                      +"   NOTES, LINE_NO,CARRIER_ID,CHARGERATE_INDICATOR,CREATEDTIME ,OVER_PIVOT,EFFECTIVE_FROM, VALID_UPTO,ID,DENSITY_CODE,RATE_DESCRIPTION,VERSION_NO,SUR_CHARGE_CURRENCY,SURCHARGE_ID,EXTERNAL_NOTES) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,SEQ_BUYRATES_DTL.nextval,?,?,?,?,?,?) ";//MODIFIED BY MOHAN/GOVIND FOR THE cr-219973
     //  freq_qry    = " INSERT INTO QMS_BUYRATES_FREQ VALUES (?,?,?) ";
		      freq_qry    = " INSERT INTO QMS_BUYRATES_FREQ VALUES (?,?,?,?) "; //@@Modified by Kameswari for the WPBN issue-146448 on 22/12/08
		                      
       log_qry     =" INSERT INTO FS_USERLOG (LOCATIONID ,USERID ,DOCTYPE ,DOCREFNO ,DOCDATE ,TRANSACTIONTYPE ) VALUES(?,?,?,?,?,?)    "          ;
             
      // proc_inactivate = "{ ?= call PKG_QMS_BUYRATES.validate_upd_insert_buyrate(?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
     // proc_inactivate = "{ ?= call PKG_QMS_BUYRATES.validate_upd_insert_buyrate(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
       proc_inactivate = "{ ?= call PKG_QMS_BUYRATES.validate_upd_insert_buyrate(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
      versionQuery    = "SELECT MAX(VERSION_NO) VERSION_NO FROM QMS_BUYRATES_DTL WHERE BUYRATEID=? AND LANE_NO=?";
      // added by VLAKSHMI for issue 146968 on 23/12/2008
      qmsQuoteInsert= " INSERT INTO QMS_QUOTES_UPDATED (QUOTE_ID, "+
           " NEW_BUYCHARGE_ID, NEW_LANE_NO, SELL_BUY_FLAG, CHANGEDESC, OLD_BUYCHARGE_ID, OLD_LANE_NO,OLD_VERSION_NO,NEW_VERSION_NO) "+
        " Values(?,?,?,?,?,?,?,?,?) ";
    quote_qry="SELECT  QR.QUOTE_ID QUOTEID"
                   +" From Qms_Quote_Rates Qr,QMS_QUOTE_MASTER QM WHERE QM.ACTIVE_FLAG='A' AND QM.ID=QR.QUOTE_ID AND Qr.Sell_Buy_Flag ='BR' And Qr.Buyrate_Id = ?"
                   
                   +" And Qr.Rate_Lane_No = ? AND QR.BREAK_POINT =?  AND QR.BUY_RATE NOT IN (?)";

         countqry = "SELECT COUNT(*) FROM QMS_QUOTES_UPDATED WHERE QUOTE_ID =? AND SELL_BUY_FLAG='BR' AND CONFIRM_FLAG IS NULL";
    selectquote_qry  = "SELECT QUOTE_ID,CHANGEDESC FROM QMS_QUOTES_UPDATED WHERE QUOTE_ID IN (SELECT QR.QUOTE_ID FROM QMS_QUOTE_RATES QR,QMS_QUOTE_MASTER QM WHERE QM.ACTIVE_FLAG='A' AND QM.ID=QR.QUOTE_ID AND SELL_BUY_FLAG='BR' AND BUYRATE_ID=? AND RATE_LANE_NO=? AND BREAK_POINT =?  AND BUY_RATE NOT IN (?) ) AND CONFIRM_FLAG IS NULL";               
   
    updatequote_qry ="UPDATE QMS_QUOTES_UPDATED SET CHANGEDESC= ?,NEW_VERSION_NO=? WHERE QUOTE_ID=? AND SELL_BUY_FLAG='BR'";
      shipmentMode    = rateDOB.getShipmentMode();
      terminalId      = rateDOB.getTerminalId();
      weightBreak     = rateDOB.getWeightBreak().toUpperCase();
      rateType        = rateDOB.getRateType().toUpperCase();
			//effectiveFrom	  = eSupplyDateUtility.getDisplayStringArray(rateDOB.getEffectiveFrom());
      //	validUpto	  = eSupplyDateUtility.getDisplayStringArray(rateDOB.getValidUpto());
           if ("1".equalsIgnoreCase(shipmentMode))
                      { shipmodeStr = "Air";}
                      else if("2".equalsIgnoreCase(shipmentMode))
                      { shipmodeStr = "Sea";}
                      else
                      { shipmodeStr = "Truck";}
      //System.out.println("in dao::::list:::::"+list+":::listsize:::"+list.size());
      
      
      	  connection	=	impl.getConnection();
          
          //inactivateRateDOB(rateDOB,list);
          
          pStmt       = connection.prepareStatement(seq_qry);
       
          pStmt1      = connection.prepareStatement(versionQuery);
          rs          = pStmt.executeQuery();   
          cStmt       = connection.prepareCall(proc_inactivate); 
          cStmt2 = connection.prepareCall("{ call qms_buy_rates_pkg.sellratesmstr_acc_proc(?,?,?)}");
        // cStmt1  = connection.prepareCall("{ call pkg_qms_buyrates.qms_update_new_quote(?,?,?,?,?,?,?,?,?,?,?,?,?)}");
        cStmt1  = connection.prepareCall("{ call pkg_qms_buyrates.qms_update_new_quote(?,?,?,?,?,?,?,?,?,?,?,?,?,?)}"); //@@Commented and Modified by subrahmanyam for the WPBN issue-167860
                           

           
             if(rs.next())
              buyRateID   = rs.getString("BUYRATEID");
                if(pStmt!= null )
              pStmt.close();
                int lanesListSize	=	laneslist.size();
                pStmtCount = connection.prepareStatement(countqry); //Added by Gowtham on 04Feb2011 for Loop Leakages
          for(int m=0;m<lanesListSize;m++)
          {
             list  =  (ArrayList)laneslist.get(m);
           
          
    
           flatRatesDOB =  (FlatRatesDOB)list.get(0);
             servicelevel  = flatRatesDOB.getServiceLevel();
             cStmt.clearParameters();
             cStmt.registerOutParameter(1,Types.VARCHAR);
          cStmt.setString(2,shipmentMode);
          cStmt.setString(3,flatRatesDOB.getOrigin());
          cStmt.setString(4,flatRatesDOB.getDestination());
          cStmt.setString(5,flatRatesDOB.getServiceLevel());
          cStmt.setString(6,rateDOB.getCarrierId());
          cStmt.setTimestamp(7,rateDOB.getEffectiveFrom());//@Modified by kameswari on 05/02/09
          if(rateDOB.getValidUpto()!=null && !"".equals(rateDOB.getValidUpto()))
            cStmt.setTimestamp(8,rateDOB.getValidUpto());                          
          else
            cStmt.setNull(8,Types.DATE);
            
            
          cStmt.setString(9,weightBreak);
          cStmt.setString(10,rateType);
          cStmt.setString(11,terminalId);
    
          str_freq  = flatRatesDOB.getFrequency();
          str_freq = str_freq.replace(',','~');
          cStmt.setString(12,str_freq);
          
          //System.out.println("flatRatesDOB.getDensityRatio()"+flatRatesDOB.getDensityRatio());
          if(flatRatesDOB.getDensityRatio()==null || "".equals(flatRatesDOB.getDensityRatio())
           || "null".equals(flatRatesDOB.getDensityRatio()))
            cStmt.setNull(13,Types.VARCHAR);
          else
            cStmt.setString(13,flatRatesDOB.getDensityRatio());  
          
          cStmt.setString(14,buyRateID);
          cStmt.setInt(15,temp);
          cStmt.setString(16,flatRatesDOB.getTransittime()); //@@Added by Kameswari for the WPBN sisue-146448 on 20/12/08
           cStmt.setString(17,"NEW");
           cStmt.setString(18,rateDOB.getCurrency()!= null?rateDOB.getCurrency():"");
          cStmt.execute();
           
           
          
           returnStr1      = cStmt.getString(1);
           returnStr      = returnStr1.split(",");//@@Added for the WPBN issues-146448,146968 on 18/12/08
   
           if(returnStr!=null&&returnStr.length>1)
           {
             oldbuyrateid   = returnStr[1];
             oldlaneno      = returnStr[2];
           }
                               
             
                  
                 if(oldbuyrateid!=null&&oldbuyrateid.trim().length()>0)
                 {
                   pStmt1.setString(1,oldbuyrateid);
                   pStmt1.setString(2,oldlaneno);
                   rs1     =  pStmt1.executeQuery();
                   if(rs1.next())
                   {
                      versionno   =   rs1.getInt("VERSION_NO");
                   }
                 }
                     
                     
                      
                                         
                  
                      
                    
                      
                       
                       /*if("List".equalsIgnoreCase(weightBreak) && "1".equals(shipmentMode))
                       {  lineNo = 1;}*/
                       
                       
                       
                      /* for(int i=0;i<list.size();i++)
                       {
                          flatRatesDOB =  (FlatRatesDOB)list.get(i);
                                                    //System.out.println("in looop "+flatRatesDOB.getLineNo());
                          //System.out.println("in looop "+flatRatesDOB.getLaneNo());
                        if(flatRatesDOB.getWtBreakSlab()!=null)
                           {
                          if(flatRatesDOB.getLaneNo() != laneNo)
                          {
                             laneNo= flatRatesDOB.getLaneNo();*/
                             
                             /*cStmt.clearParameters();
                              
                              cStmt.registerOutParameter(1,Types.VARCHAR);
                              cStmt.setString(2,shipmentMode);
                              cStmt.setString(3,flatRatesDOB.getOrigin());
                              cStmt.setString(4,flatRatesDOB.getDestination());
                              cStmt.setString(5,flatRatesDOB.getServiceLevel());
                              cStmt.setString(6,rateDOB.getCarrierId());
                              cStmt.setTimestamp(7,rateDOB.getEffectiveFrom());
                              
                              if(rateDOB.getValidUpto()!=null && !"".equals(rateDOB.getValidUpto()))
                                cStmt.setTimestamp(8,rateDOB.getValidUpto());                          
                              else
                                cStmt.setNull(8,Types.DATE);
                                
                                
                              cStmt.setString(9,weightBreak);
                              cStmt.setString(10,rateType);
                              cStmt.setString(11,terminalId);
                              
                              str_freq  = flatRatesDOB.getFrequency();
                              str_freq = str_freq.replace(',','~');
                              cStmt.setString(12,str_freq);
                              
                              //System.out.println("flatRatesDOB.getDensityRatio()"+flatRatesDOB.getDensityRatio());
                              
                              if(flatRatesDOB.getDensityRatio()==null || "".equals(flatRatesDOB.getDensityRatio())
                               || "null".equals(flatRatesDOB.getDensityRatio()))
                                cStmt.setNull(13,Types.VARCHAR);
                              else
                                cStmt.setString(13,flatRatesDOB.getDensityRatio());  
                              
                              cStmt.setString(14,buyRateID);
                              cStmt.setInt(15,flatRatesDOB.getLaneNo());
							  
                              cStmt.execute();
                              
                               returnStr1      = cStmt.getString(1);
                               returnStr      = returnStr1.split(",");//@@Added for the WPBN issues-146448,146968 on 18/12/08
                               if(returnStr!=null)
                               {
                                 oldbuyrateid   = returnStr[1];
                                 oldlaneno      = returnStr[2];
                               }
                               */
                                   
                               // if("1".equals(returnStr))//@@Modified for the WPBN issues-146448,146968 on 18/12/08
                                if(returnStr!=null&&"1".equals(returnStr[0]))
                                {
                                  sb_failErrorMsg.append(flatRatesDOB.getOrigin());
                                  sb_failErrorMsg.append(",\t");
                                  sb_failErrorMsg.append(flatRatesDOB.getDestination());
                                  sb_failErrorMsg.append(",\t");
                                  sb_failErrorMsg.append(flatRatesDOB.getServiceLevel());
                                  sb_failErrorMsg.append(",\t");
                                  sb_failErrorMsg.append(rateDOB.getCarrierId());
                                  sb_failErrorMsg.append(",\t");
                                  sb_failErrorMsg.append(flatRatesDOB.getFrequency());
                                  sb_failErrorMsg.append("\n");
                                }
                                     // if("2".equals(returnStr))//@@Modified for the WPBN issues-146448,146968 on 18/12/08
                                //if(returnStr!=null&&"2".equals(returnStr[0]))
                               else  if(returnStr!=null&&("2".equals(returnStr[0])||"3".equals(returnStr[0])))
                                {
                                    sb_successErrorMsg.append(flatRatesDOB.getOrigin());
                                    sb_successErrorMsg.append(",\t");
                                    sb_successErrorMsg.append(flatRatesDOB.getDestination());
                                    sb_successErrorMsg.append(",\t");
                                    sb_successErrorMsg.append(flatRatesDOB.getServiceLevel());
                                    sb_successErrorMsg.append(",\t");
                                    sb_successErrorMsg.append(rateDOB.getCarrierId());
                                    sb_successErrorMsg.append(",\t");
                                    sb_successErrorMsg.append(flatRatesDOB.getFrequency());
                                    sb_successErrorMsg.append("\n");
                                    
                                    if(!"2".equalsIgnoreCase(shipmentMode))
                                    {
                                      freqArray   =  flatRatesDOB.getFrequency().split(",");
                                                pFrqStmt    = connection.prepareStatement(freq_qry);
                                      if(freqArray!=null)
                                      {
                                    	  int freqArrLen	=	freqArray.length;
                                        for(int k=0;k<freqArrLen;k++)
                                        {
                                          if(oldbuyrateid!=null)
                                          {
                                               pFrqStmt.setString(1,oldbuyrateid);
                                        //  pFrqStmt.setInt(2,flatRatesDOB.getLaneNo());
                                              pFrqStmt.setInt(2,Integer.parseInt(oldlaneno)); //@@Modified by Kameswari for the WPBN issue-146448 on 22/12/08
                                          
                                             pFrqStmt.setInt(4,versionno+1);
                                          }
                                          else
                                          {
                                            pFrqStmt.setLong(1,Long.parseLong(buyRateID));
                                        //  pFrqStmt.setInt(2,flatRatesDOB.getLaneNo());
                                            pFrqStmt.setInt(2,temp); //@@Modified by Kameswari for the WPBN issue-146448 on 22/12/08
                                             pFrqStmt.setInt(4,1);
                                          }
                                                          pFrqStmt.setInt(3,Integer.parseInt(freqArray[k]));
                                                pFrqStmt.addBatch();
                                        }
                                          pFrqStmt.executeBatch();
                                      }
                                    }
                                }
                          
                                 
                              // if("2".equals(returnStr))//@@Modified for the WPBN issues-146448,146968 on 18/12/08
                             if(returnStr!=null&&("2".equals(returnStr[0])||"3".equals(returnStr[0])))
                                {
                          
                               pStmt       = connection.prepareStatement(master_qry);
                      
					
                      
                              /*  pStmt.setString(1,shipmentMode);
                                pStmt.setString(2,rateDOB.getCurrency());
                                //pStmt.setString(3,rateDOB.getCarrierId());
                                pStmt.setString(3,rateDOB.getUom());
                                pStmt.setString(4,weightBreak);
                                pStmt.setString(5,rateType);
                                pStmt.setString(6,rateDOB.getConsoleType());
                                pStmt.setString(7,rateDOB.getUser());
                                pStmt.setString(8,"N");
                                if(oldbuyrateid!=null&&oldbuyrateid.trim().length()>0)
                               {
                                  pStmt.setString(9,oldbuyrateid);
                                   pStmt.setInt(13,versionno+1); 
                               }
                               else
                               {
                                  pStmt.setString(9,	buyRateID);
                                  pStmt.setInt(13,versionno); 
                               }
                                pStmt.setString(10,rateDOB.getWeightClass()); 
                                pStmt.setString(11,rateDOB.getAccessLevel()); 
                                pStmt.setString(12,terminalId); */
              if(oldbuyrateid!=null&&oldbuyrateid.trim().length()>0)
              {
                 /* if(tempbuyrateid.size()==0)
                 {*/
                      pStmt.clearParameters();
                      pStmt.setString(1,rateDOB.getShipmentMode());
                      pStmt.setString(2,rateDOB.getCurrency());
                      pStmt.setString(3,rateDOB.getUom());
                      pStmt.setString(4,weightBreak);
                      pStmt.setString(5,rateType);
                      pStmt.setString(6,rateDOB.getConsoleType());
                      pStmt.setString(7,rateDOB.getUser());                      
                      pStmt.setString(8,"N"); 
                      pStmt.setString(9,oldbuyrateid); 
                      pStmt.setInt(13,versionno+1);
                      pStmt.setString(14,oldlaneno);//@@Added by kameswari on 04/02/09
                      pStmt.setString(10,rateDOB.getWeightClass()); 
                      pStmt.setString(11,rateDOB.getAccessLevel()); 
                      pStmt.setString(12,terminalId); 
                        pStmt.executeUpdate();
                       //tempbuyrateid.add(oldbuyrateid);
                 /* }
                 
               else
               {
                 for( int z=0;z<tempbuyrateid.size();z++)
                 {
                   if(!tempbuyrateid.get(z).equals(oldbuyrateid))
                   {   buyratesize++;
                   }
                 }
              
               if(buyratesize==tempbuyrateid.size())
               {
                  pStmt.clearParameters();
                  pStmt.setString(1,rateDOB.getShipmentMode());
                  pStmt.setString(2,rateDOB.getCurrency());
                  pStmt.setString(3,rateDOB.getUom());
                  pStmt.setString(4,weightBreak);
                  pStmt.setString(5,rateType);
                  pStmt.setString(6,rateDOB.getConsoleType());
                  pStmt.setString(7,rateDOB.getUser());                      
                  pStmt.setString(8,"N"); 
                   pStmt.setString(9,oldbuyrateid); 
                   pStmt.setInt(13,versionno+1);
                    pStmt.setString(14,oldlaneno);//@@Added by kameswari on 04/02/09
                   pStmt.setString(10,rateDOB.getWeightClass()); 
                  pStmt.setString(11,rateDOB.getAccessLevel()); 
                   pStmt.setString(12,terminalId); 
                   pStmt.executeUpdate();
                 tempbuyrateid.add(oldbuyrateid);
                 
                   } 
                 } */
                
              }
              else
              {
                 if(temp1==0)
                 {
                    pStmt.clearParameters();
                    pStmt.setString(1,rateDOB.getShipmentMode());
                    pStmt.setString(2,rateDOB.getCurrency());
                    pStmt.setString(3,rateDOB.getUom());
                    pStmt.setString(4,weightBreak);
                    pStmt.setString(5,rateType);
                    pStmt.setString(6,rateDOB.getConsoleType());
                    pStmt.setString(7,rateDOB.getUser());                      
                    pStmt.setString(8,"N"); 
                    pStmt.setString(9,buyRateID); 
                     pStmt.setNull(14,Types.NULL);//@@Added by kameswari on 04/02/09
                    pStmt.setInt(13,1); 
                     pStmt.setString(10,rateDOB.getWeightClass()); 
                    pStmt.setString(11,rateDOB.getAccessLevel()); 
                      pStmt.setString(12,terminalId); 
                        pStmt.executeUpdate();
                   temp1++;
                 }
              }
          
           
                                
                       

                   

                          if(pStmt!= null )
                            pStmt.close();
                      
                       pStmt       = connection.prepareStatement(dtl_qry);
                    
                       		int listSize	=	list.size();
                           for(int i=0;i<listSize;i++)
                           {
                            
                            flatRatesDOB =  (FlatRatesDOB)list.get(i);
                                                      //System.out.println("in looop "+flatRatesDOB.getLineNo());
                            //System.out.println("in looop "+flatRatesDOB.getLaneNo());
                                                     
                             // pStmt.setString		(1,	buyRateID);
                             //starting
                             
                         if(oldbuyrateid!=null&&oldbuyrateid.trim().length()>0)
                            {
                                                  // added by VLAKSHMI for issue 146968 on 23/12/2008
                                               ///end for issue 146968 on 23/12/2008
                                pStmt.setString	(1,	oldbuyrateid);
                                  pStmt.setInt  (9,	Integer.parseInt(oldlaneno));
                                 pStmt.setInt(22,versionno+1);//@@Added for the WPBN issues-146448,146968 on 18/12/08
                             }
                             else
                             {
                                pStmt.setString	(1,	buyRateID);
                                 pStmt.setInt  (9,temp);
                                 pStmt.setInt(22,versionno);//@@Added for the WPBN issues-146448,146968 on 18/12/08
                  
                             }
                              pStmt.setString		(2,	flatRatesDOB.getOrigin());
                              pStmt.setString		(3,	flatRatesDOB.getDestination());
                              pStmt.setString		(4,	flatRatesDOB.getServiceLevel());
                              pStmt.setString		(5,	flatRatesDOB.getFrequency());
                              pStmt.setString		(6,	flatRatesDOB.getTransittime());
                              pStmt.setDouble		(7,	flatRatesDOB.getChargeRate());
                              pStmt.setString		(8,	flatRatesDOB.getWtBreakSlab());
                             
                              pStmt.setDouble          (10,flatRatesDOB.getLowerBound());
                              pStmt.setDouble          (11,flatRatesDOB.getUpperBound());
                              pStmt.setString		(12,flatRatesDOB.getNotes());
                              pStmt.setInt		( 13,flatRatesDOB.getLineNo());
                              pStmt.setString		(14,rateDOB.getCarrierId());
                              pStmt.setString       (15,flatRatesDOB.getChargeRateIndicator());
                              pStmt.setTimestamp    (16,rateDOB.getCreatedTime());
                              pStmt.setString		(17,flatRatesDOB.getOverPivot());
                              pStmt.setTimestamp(18,rateDOB.getEffectiveFrom());
                              pStmt.setString(23,flatRatesDOB.getSurchargeCurrency());//Added by govind for the CR-219973
                              pStmt.setString(24,flatRatesDOB.getSurchargeId());//Added by govind for the CR-219973
                              pStmt.setString(25,flatRatesDOB.getExtNotes());//Added by govind for the CR-219973
                              if(rateDOB.getValidUpto()!=null && !"".equals(rateDOB.getValidUpto()))
                                pStmt.setTimestamp(19,rateDOB.getValidUpto());
                              else
                                pStmt.setNull(19,Types.DATE);
                              pStmt.setString		(20,flatRatesDOB.getDensityRatio());
                              pStmt.setString		(21,flatRatesDOB.getTypeofcharge());
                             
                              
                              pStmt.executeUpdate();
                 
                  if(oldbuyrateid!=null&&oldbuyrateid.trim().length()>0&&"2".equalsIgnoreCase(rateDOB.getShipmentMode())  && "LIST".equalsIgnoreCase(weightBreak))
                  {
                     pStmtQuote      = connection.prepareStatement(quote_qry);// added by VLAKSHMI for issue 146968 on 10/12/2008
                     pstmtQmsQuoteInsert = connection.prepareStatement(qmsQuoteInsert);// added by VLAKSHMI for issue 146968 on 10/12/2008
                     pstmtSelectQuoteId=connection.prepareStatement(selectquote_qry);
                     pstmtQmsQuoteupdate = connection.prepareStatement(updatequote_qry);
                  
                  
                   pstmtSelectQuoteId.setString(1,flatRatesDOB.getBuyrateId());
                     pstmtSelectQuoteId.setInt(2,Integer.parseInt(oldlaneno));
                      pstmtSelectQuoteId.setString(3,flatRatesDOB.getWtBreakSlab());
                     pstmtSelectQuoteId.setDouble(4,flatRatesDOB.getChargeRate());
                     // pstmtSelectQuoteId.setNull(3,Types.INTEGER);
                      rsSelectQuoteId   = pstmtSelectQuoteId.executeQuery();
			              String changedesc = flatRatesDOB.getWtBreakSlab()+","+flatRatesDOB.getOrigin()+"-"+flatRatesDOB.getDestination()+", Sea Freight Rates and Surcharges";
                   while(rsSelectQuoteId.next())
                   {
                     // COMMENTED& ADDED BY SUBRAHMANYAM FOR 187878 ON NOV-4-09
                 //     if(rsSelectQuoteId.getString("CHANGEDESC").equalsIgnoreCase(changedesc))
                      if(rsSelectQuoteId.getString("CHANGEDESC").indexOf(flatRatesDOB.getWtBreakSlab())!=-1)

                     {
                        pstmtQmsQuoteupdate.setString(1,changedesc);
                        pstmtQmsQuoteupdate.setInt(2,versionno+1);
                        pstmtQmsQuoteupdate.setString(3,rsSelectQuoteId.getString("QUOTE_ID"));
                     }
                     else
                     {
                        pstmtQmsQuoteupdate.setString(1,flatRatesDOB.getWtBreakSlab()+","+rsSelectQuoteId.getString("CHANGEDESC"));
                        pstmtQmsQuoteupdate.setInt(2,versionno+1);
                        pstmtQmsQuoteupdate.setString(3,rsSelectQuoteId.getString("QUOTE_ID"));
                   }
                     pstmtQmsQuoteupdate.executeUpdate();
                 
                     }
                  
                     pStmtQuote.clearParameters();
                     pStmtQuote.setString(1,oldbuyrateid);
                     pStmtQuote.setInt(2,Integer.parseInt(oldlaneno));
                     pStmtQuote.setString(3,flatRatesDOB.getWtBreakSlab());
                     pStmtQuote.setDouble(4,flatRatesDOB.getChargeRate());
                    // pStmtQuote.setInt(6,prevoiusQuotid);
                   rsQuote= pStmtQuote.executeQuery();
                 
                    while(rsQuote.next())
                    {
                           //String quoteWtBrk=rsQuote.getString("BREAK_POINT");
                         // int QuoteId=rsQuote.getInt("QUOTEID");
                       //pStmtCount = connection.prepareStatement(countqry); Commented by Gowtham on 04Feb2011 for Loop Leakages
                       pStmtCount.clearParameters();
                       pStmtCount.setString(1,rsQuote.getString("QUOTEID"));
                       rsCount  = pStmtCount.executeQuery();
                       
                       if(rsCount.next()&&rsCount.getInt(1)==0)
                       {
                         pstmtQmsQuoteInsert.clearParameters();
                         pstmtQmsQuoteInsert.setInt(1,Integer.parseInt(rsQuote.getString("QUOTEID")));
                      //pstmtQmsQuoteInsert.setInt(2,Integer.parseInt(buyRateID));
                         pstmtQmsQuoteInsert.setInt(2,Integer.parseInt(oldbuyrateid));
                       // pstmtQmsQuoteInsert.setInt(3,i); commented by VALSKHMI for issue 146968 
                         pstmtQmsQuoteInsert.setInt(3,Integer.parseInt(oldlaneno));
                         pstmtQmsQuoteInsert.setString(4,"BR");
                         pstmtQmsQuoteInsert.setString(5,flatRatesDOB.getWtBreakSlab()+","+flatRatesDOB.getOrigin()+"-"+flatRatesDOB.getDestination()+","+" Sea Freight Rates and Surcharges");
                         pstmtQmsQuoteInsert.setInt(6,Integer.parseInt(oldbuyrateid));
                         pstmtQmsQuoteInsert.setInt(7,Integer.parseInt(oldlaneno));
                         pstmtQmsQuoteInsert.setInt(8,versionno);
                         pstmtQmsQuoteInsert.setInt(9,versionno+1);
                         pstmtQmsQuoteInsert.executeUpdate();
                             count2++;
                       }    
                    }
                     
                    
                    }
                               flagDtl = true;
                              //pStmt.addBatch();
                   }
                }
                          else     // if("0".equals(returnStr))//@@Modified for the WPBN issues-146448,146968 on 18/12/08
                                if(returnStr!=null&&"0".equals(returnStr[0]))
                          {
                            throw new Exception();
                          }
                      
                     
                       
                     //pStmt.executeBatch();
                      if(rs!= null )
                        rs.close();
          
                     if(pStmt!= null )
                        pStmt.close();
          
          //pStmt       = connection.prepareStatement(log_qry);      
                   /* if(pFrqStmt!=null)
                       pFrqStmt.close();*/
                   /* if(cStmt!=null)
                    { cStmt.close();}*/
                        flatRatesDOB =  (FlatRatesDOB)list.get(0);
                    //To Update the buyrates and sellrates into RSR_Acceptence module
                    ////condition added  by VLAKSHMI for issue 146968 on 23/12/2008
                     if("2".equalsIgnoreCase(shipmentMode) && "FCL".equalsIgnoreCase(rateDOB.getConsoleType()) && "LIST".equalsIgnoreCase(weightBreak))
                     {
                        if(flagDtl&&count2!=0&&oldbuyrateid!=null&&oldbuyrateid.trim().length()>0 )
                        {
                         // cStmt2 = connection.modprepareCall("{ call qms_buy_rates_pkg.sellratesmstr_acc_proc(?,?,?)}");
                          //cStmt.setDouble(1,Double.parseDouble(buyRateID)); //commented by VLAKSHMI for issue 146968 on 23/12/2008
                     
                         cStmt2.setDouble(1,Double.parseDouble(oldbuyrateid)); //added by VLAKSHMI for issue 146968 on 23/12/2008
                         cStmt2.setInt(2,versionno+1);
                            cStmt2.setString(3,oldlaneno);
                       /*else
                         {
                           cStmt2.setDouble(1,Double.parseDouble(buyRateID));//added by VLAKSHMI for issue 146968 on 23/12/2008
                           cStmt2.setInt(2,1);
                         }*/
                         cStmt2.execute();
                       }
                     } 
                     ////condition added  by VLAKSHMI for issue 146968 on 23/12/2008 becoz in sellratesmstr_acc_proc(?)} 
                     //To Update the buyrates and sellrates into RSR_Acceptence module   
                     //in up we are modifying  and inserting the qms_update table for fcl,2,list
                     //in qms_update_new_quote pkage both insertin,updating the qms_update table and Update the buyrates and sellrates into RSR_Acceptence module    both is happeneing...so no need
                     else
                     {
                    if("2".equalsIgnoreCase(returnStr[0])&&oldbuyrateid!=null&&oldbuyrateid.trim().length()>0)
                    {
                     
                             cStmt2.setDouble(1,Double.parseDouble(oldbuyrateid));//added by VLAKSHMI for issue 146968 on 23/12/2008
                             cStmt2.setInt(2,versionno+1);//added by VLAKSHMI for issue 146968 on 23/12/2008 
                             cStmt2.setString(3,oldlaneno);
                       
                       /* else
                        {
                             cStmt2.setDouble(1,Double.parseDouble(buyRateID));//added by VLAKSHMI for issue 146968 on 23/12/2008
                             cStmt2.setInt(2,1);//added by VLAKSHMI for issue 146968 on 23/12/2008 
                           }*/
                        cStmt2.execute();
                    }
                    else if("3".equalsIgnoreCase(returnStr[0])&&oldbuyrateid!=null&&oldbuyrateid.trim().length()>0)
                    {
                         cStmt1.setString(1,"BR");
                         cStmt1.setString(2,flatRatesDOB.getOrigin());
                         cStmt1.setString(3,flatRatesDOB.getDestination());
                         cStmt1.setString(4,flatRatesDOB.getServiceLevel());
                         cStmt1.setString(5,rateDOB.getCarrierId());
                         cStmt1.setTimestamp(6,rateDOB.getValidUpto());
                         cStmt1.setString(7,flatRatesDOB.getFrequency());
                         cStmt1.setString(8,flatRatesDOB.getTransittime());
                         cStmt1.setString(9,oldbuyrateid);
                          cStmt1.setNull(10,Types.VARCHAR);
                          cStmt1.setNull(11,Types.VARCHAR);
                         cStmt1.setInt(12,versionno+1);
                         cStmt1.setInt(13,Integer.parseInt(oldlaneno));
                         cStmt1.setString(14,terminalId);//@@Added  by subrahmanyam for the WPBN issue-167860    
                         cStmt1.execute();
                    }
              }
                                 temp++;
             
             if("2".equalsIgnoreCase(shipmentMode) && "FCL".equalsIgnoreCase(rateDOB.getConsoleType()) && "LIST".equalsIgnoreCase(weightBreak))
            {
                  
                   //cStmt1  = connection.prepareCall("{ call pkg_qms_buyrates.qms_update_new_quote(?,?,?,?,?,?,?,?,?,?,?)}");
                   if(count2==0&&("3".equalsIgnoreCase(returnStr[0])&&oldbuyrateid!=null&&oldbuyrateid.trim().length()>0))
                   {
                   cStmt1.setString(1,"BR");
                   cStmt1.setString(2,flatRatesDOB.getOrigin());
                   cStmt1.setString(3,flatRatesDOB.getDestination());
                   cStmt1.setString(4,flatRatesDOB.getServiceLevel());
                   cStmt1.setString(5,rateDOB.getCarrierId());
                   cStmt1.setTimestamp(6,rateDOB.getValidUpto());
                   cStmt1.setString(7,flatRatesDOB.getFrequency());
                   cStmt1.setString(8,flatRatesDOB.getTransittime());
                   cStmt1.setString(9,oldbuyrateid);
                   cStmt1.setNull(10,Types.VARCHAR);
                   cStmt1.setNull(11,Types.VARCHAR);
                   cStmt1.setInt(12,versionno+1);
                   cStmt1.setInt(13,Integer.parseInt(oldlaneno));
                   cStmt1.setString(14,terminalId);//@@Added  by subrahmanyam for the WPBN issue-167860    
                   cStmt1.execute();
                   }
                   else if (count2==0&&"2".equalsIgnoreCase(returnStr[0])&&oldbuyrateid!=null&&oldbuyrateid.trim().length()>0)
                       {
                            cStmt2.setDouble(1,Double.parseDouble(oldbuyrateid)); //added by VLAKSHMI for issue 146968 on 23/12/2008
                            cStmt2.setInt(2,versionno+1);
                            cStmt2.setString(3,oldlaneno);
                            cStmt2.execute();
                       }
             }
          }                   
                        
                    //end @@sekhar
             //@@Added by Kameswari for the WPBN issue-146448 on 09/12/08
      /*if(rateDOB.getValidUpto()!=null && !"".equals(rateDOB.getValidUpto()))
       {
           frequery    ="SELECT BUYRATEID,LANE_NO FROM QMS_BUYRATES_DTL WHERE ORIGIN=? AND DESTINATION=? AND CARRIER_ID=?  AND SERVICE_LEVEL =? AND TRANSIT_TIME=? AND VALID_UPTO = ?  AND ACTIVEINACTIVE IS NULL AND (INVALIDATE='F' OR INVALIDATE IS NULL) AND LINE_NO='0'";  
       }
       else
       {
           frequery    ="SELECT BUYRATEID,LANE_NO FROM QMS_BUYRATES_DTL WHERE ORIGIN=? AND DESTINATION=? AND CARRIER_ID=?  AND SERVICE_LEVEL =? AND TRANSIT_TIME=?   AND ACTIVEINACTIVE IS NULL AND (INVALIDATE='F' OR INVALIDATE IS NULL) AND LINE_NO='0'";   
       }
           slabquery    ="SELECT BUYRATEID,LANE_NO, WEIGHT_BREAK_SLAB,CHARGERATE FROM QMS_BUYRATES_DTL WHERE BUYRATEID =? AND LANE_NO =?"; 
      
        if(rateDOB.getValidUpto()!=null && !"".equals(rateDOB.getValidUpto()))
       {
          quotefrequery    ="SELECT COUNT(*) FROM QMS_QUOTE_RATES QR ,QMS_QUOTE_MASTER QM WHERE QM.ID =QR.QUOTE_ID AND QM.ORIGIN_LOCATION=? AND QM.DEST_LOCATION=? AND QR.SELL_BUY_FLAG = 'BR' AND QR.CARRIER=?  AND QR.SRVLEVEL=? AND QR.TRANSIT_TIME=? AND QR.RATE_VALIDITY=? AND (QR.FREQUENCY_CHECKED='Y' or QR.FREQUENCY_CHECKED='on') AND QR.LINE_NO='0'";  
       }
       else
       {
          quotefrequery    ="SELECT QM.QUOTE_ID QUOTE_ID,QM.ID ID,QR.BUYRATE_ID BUYRATE_ID, QR.RATE_LANE_NO RATE_LANE_NO FROM QMS_QUOTE_RATES QR ,QMS_QUOTE_MASTER QM WHERE QM.ID =QR.QUOTE_ID AND QM.ORIGIN_LOCATION=? AND QM.DEST_LOCATION=? AND QR.SELL_BUY_FLAG = 'BR' AND QR.CARRIER=?  AND QR.SRVLEVEL=? AND QR.TRANSIT_TIME=?  AND (QR.FREQUENCY_CHECKED='Y' or QR.FREQUENCY_CHECKED='on') AND QR.LINE_NO='0'";  
       }
          updatequery   = "INSERT INTO QMS_QUOTES_UPDATED(QUOTE_ID,NEW_BUYCHARGE_ID,NEW_LANE_NO,SELL_BUY_FLAG,CHARGE_AT,CHANGEDESC,OLD_BUYCHARGE_ID,OLD_LANE_NO) VALUES(?,?,?,?,?,?,?,?)";
           
          selectquery  = "SELECT DISTINCT QUOTE_ID FROM QMS_QUOTE_RATES WHERE BUYRATE_ID=? AND RATE_LANE_NO=? AND SELL_BUY_FLAG='BR'"; 
              pStmt             = connection.prepareStatement(frequery);
              flatRatesDOB      =  (FlatRatesDOB)list.get(0);
              pStmt.setString(1,flatRatesDOB.getOrigin());
              pStmt.setString(2,flatRatesDOB.getDestination());
              pStmt.setString(3,rateDOB.getCarrierId());
              pStmt.setString(4,flatRatesDOB.getServiceLevel());
              pStmt.setString(5,flatRatesDOB.getTransittime());
             
               if(rateDOB.getValidUpto()!=null && !"".equals(rateDOB.getValidUpto()))
               {
                           pStmt.setTimestamp(6,rateDOB.getValidUpto());
               }
        
             rs   =  pStmt.executeQuery();
             while(rs.next())
             {
                if(rs.getString("BUYRATEID")!=null)
                {
                    buyrateid.add(rs.getString("BUYRATEID"));
                }
                 if(rs.getString("LANE_NO")!=null)
                {
                   laneno.add(rs.getString("LANE_NO"));
                }
             }
             if(buyrateid!=null&&buyrateid.size()>0)
             {
                pFrqStmt = connection.prepareStatement(quotefrequery);
                pFrqStmt.setString(1,flatRatesDOB.getOrigin());
                pFrqStmt.setString(2,flatRatesDOB.getDestination());
                pFrqStmt.setString(3,rateDOB.getCarrierId());
                pFrqStmt.setString(4,flatRatesDOB.getServiceLevel());
                pFrqStmt.setString(5,flatRatesDOB.getTransittime());
                if(rateDOB.getValidUpto()!=null && !"".equals(rateDOB.getValidUpto()))
               {
                 pFrqStmt.setTimestamp(6,rateDOB.getValidUpto());
               }
             
                
                rs1   =  pFrqStmt.executeQuery();
                 if(rs1.next())
                 {
                   count1   =   rs1.getInt(1);
                  
                 }
             }
                if(rs!= null )
                   rs.close();
               if(rs1!= null )
                   rs1.close();
               if(pStmt!= null )
                  pStmt.close();
               if(pFrqStmt!=null)
                  pFrqStmt.close();
             
             if(count1>0)
             { 
                 pStmt             = connection.prepareStatement(slabquery);
                if(buyrateid!=null)
                {
                   for(int i=0;i<buyrateid.size();i++)
                   {
                      pStmt.setString(1,(String)buyrateid.get(i)); 
                      pStmt.setString(2,(String)laneno.get(i)); 
                      rs  = pStmt.executeQuery();
                      breakslab  = new ArrayList();
                       chargerate  = new ArrayList();
                      while(rs.next())
                      {
                       
                       if(rs.getString("WEIGHT_BREAK_SLAB")!=null)
                         {
                            breakslab.add(rs.getString("WEIGHT_BREAK_SLAB"));
                         }
                         if(rs.getString("CHARGERATE")!=null)
                         {
                            chargerate.add(rs.getString("CHARGERATE"));
                         }
                      }
                        breakslab1.add(breakslab);
                          chargerate1.add(chargerate);
                   }
                }
              
             }
             
             if(breakslab1!=null)
             {
              for(int k=0;k<breakslab1.size();k++)
              {
                count  = 0;
                breakslab   =    (ArrayList)breakslab1.get(k);
                chargerate  =    (ArrayList)chargerate1.get(k);
                buyrate_id   =    (String)buyrateid.get(k);
                 lane_no     =    (String)laneno.get(k);
                             
                 if(breakslab!=null&&list!=null&&breakslab.size()==list.size())
                 {
                    for(int i =0;i<list.size();i++)
                    {
                        flatRatesDOB      =  (FlatRatesDOB)list.get(i);
                        for(int j=0;j<breakslab.size();j++)
                        {
                            if(flatRatesDOB.getWtBreakSlab().equalsIgnoreCase((String)breakslab.get(j)))
                            {
                              
                              if(flatRatesDOB.getChargeRate()==Double.parseDouble((String)chargerate.get(j)))
                                {
                                
                                    count++;
                                }
                                
                            }
                        }
                    }
                 }
             
                 flatRatesDOB      =  (FlatRatesDOB)list.get(0);
             if(list!=null&&count==list.size())
             {
                  
                    pStmt1    =  connection.prepareStatement(selectquery);
                    
                    pStmt1.setString(1,buyrate_id);
                     pStmt1.setString(2,lane_no);
                     rs1 = pStmt1.executeQuery();
                     pFrqStmt  =  connection.prepareStatement(updatequery);
                     while(rs1.next())
                     {
                         
                        pFrqStmt.setLong(1,rs1.getLong("QUOTE_ID"));
                        pFrqStmt.setLong(2,Long.parseLong(buyRateID));
                        pFrqStmt.setInt(3,flatRatesDOB.getLaneNo());
                        pFrqStmt.setString(4,"BR");
                        pFrqStmt.setString(5,"Carrier");
                        pFrqStmt.setString(6,flatRatesDOB.getOrigin()+"-"+flatRatesDOB.getDestination()+" Frequency");
                        pFrqStmt.setLong(7,Long.parseLong(buyrate_id));
                        pFrqStmt.setLong(8,Long.parseLong(lane_no));
                        pFrqStmt.execute();
                     }
                  
                    
                  }
              }
           }
             if(rateDOB.getValidUpto()!=null && !"".equals(rateDOB.getValidUpto()))
       {
           frequery    ="SELECT BUYRATEID,LANE_NO FROM QMS_BUYRATES_DTL WHERE ORIGIN=? AND DESTINATION=? AND CARRIER_ID=?  AND SERVICE_LEVEL =? AND TRANSIT_TIME=? AND VALID_UPTO = ?  AND ACTIVEINACTIVE IS NULL AND (INVALIDATE='F' OR INVALIDATE IS NULL) AND LINE_NO='0'";  
       }
       else
       {
           frequery    ="SELECT BUYRATEID,LANE_NO FROM QMS_BUYRATES_DTL WHERE ORIGIN=? AND DESTINATION=? AND CARRIER_ID=?  AND SERVICE_LEVEL =? AND TRANSIT_TIME=?   AND ACTIVEINACTIVE IS NULL AND (INVALIDATE='F' OR INVALIDATE IS NULL) AND LINE_NO='0'";   
       }
           slabquery    ="SELECT BUYRATEID,LANE_NO, WEIGHT_BREAK_SLAB,CHARGERATE FROM QMS_BUYRATES_DTL WHERE BUYRATEID =? AND LANE_NO =?"; 
      
        if(rateDOB.getValidUpto()!=null && !"".equals(rateDOB.getValidUpto()))
       {
          quotefrequery    ="SELECT COUNT(*) FROM QMS_QUOTE_RATES QR ,QMS_QUOTE_MASTER QM WHERE QM.ID =QR.QUOTE_ID AND QM.ORIGIN_LOCATION=? AND QM.DEST_LOCATION=? AND QR.SELL_BUY_FLAG = 'BR' AND QR.CARRIER=?  AND QR.SRVLEVEL=? AND QR.TRANSIT_TIME=? AND QR.RATE_VALIDITY=? AND (QR.FREQUENCY_CHECKED='Y' or QR.FREQUENCY_CHECKED='on') AND QR.LINE_NO='0'";  
       }
       else
       {
          quotefrequery    ="SELECT QM.QUOTE_ID QUOTE_ID,QM.ID ID,QR.BUYRATE_ID BUYRATE_ID, QR.RATE_LANE_NO RATE_LANE_NO FROM QMS_QUOTE_RATES QR ,QMS_QUOTE_MASTER QM WHERE QM.ID =QR.QUOTE_ID AND QM.ORIGIN_LOCATION=? AND QM.DEST_LOCATION=? AND QR.SELL_BUY_FLAG = 'BR' AND QR.CARRIER=?  AND QR.SRVLEVEL=? AND QR.TRANSIT_TIME=?  AND (QR.FREQUENCY_CHECKED='Y' or QR.FREQUENCY_CHECKED='on') AND QR.LINE_NO='0'";  
       }
          updatequery   = "INSERT INTO QMS_QUOTES_UPDATED(QUOTE_ID,NEW_BUYCHARGE_ID,NEW_LANE_NO,SELL_BUY_FLAG,CHARGE_AT,CHANGEDESC,OLD_BUYCHARGE_ID,OLD_LANE_NO) VALUES(?,?,?,?,?,?,?,?)";
           
          selectquery  = "SELECT DISTINCT QUOTE_ID FROM QMS_QUOTE_RATES WHERE BUYRATE_ID=? AND RATE_LANE_NO=? AND SELL_BUY_FLAG='BR'"; 
              pStmt             = connection.prepareStatement(frequery);
              flatRatesDOB      =  (FlatRatesDOB)list.get(0);
              pStmt.setString(1,flatRatesDOB.getOrigin());
              pStmt.setString(2,flatRatesDOB.getDestination());
              pStmt.setString(3,rateDOB.getCarrierId());
              pStmt.setString(4,flatRatesDOB.getServiceLevel());
              pStmt.setString(5,flatRatesDOB.getTransittime());
             
               if(rateDOB.getValidUpto()!=null && !"".equals(rateDOB.getValidUpto()))
               {
                           pStmt.setTimestamp(6,rateDOB.getValidUpto());
               }
        
             rs   =  pStmt.executeQuery();
             while(rs.next())
             {
                if(rs.getString("BUYRATEID")!=null)
                {
                    buyrateid.add(rs.getString("BUYRATEID"));
                }
                 if(rs.getString("LANE_NO")!=null)
                {
                   laneno.add(rs.getString("LANE_NO"));
                }
             }
             if(buyrateid!=null&&buyrateid.size()>0)
             {
                pFrqStmt = connection.prepareStatement(quotefrequery);
                pFrqStmt.setString(1,flatRatesDOB.getOrigin());
                pFrqStmt.setString(2,flatRatesDOB.getDestination());
                pFrqStmt.setString(3,rateDOB.getCarrierId());
                pFrqStmt.setString(4,flatRatesDOB.getServiceLevel());
                pFrqStmt.setString(5,flatRatesDOB.getTransittime());
                if(rateDOB.getValidUpto()!=null && !"".equals(rateDOB.getValidUpto()))
               {
                 pFrqStmt.setTimestamp(6,rateDOB.getValidUpto());
               }
             
                
                rs1   =  pFrqStmt.executeQuery();
                 if(rs1.next())
                 {
                   count1   =   rs1.getInt(1);
                  
                 }
             }
                if(rs!= null )
                   rs.close();
               if(rs1!= null )
                   rs1.close();
               if(pStmt!= null )
                  pStmt.close();
               if(pFrqStmt!=null)
                  pFrqStmt.close();
             
             if(count1>0)
             { 
                 pStmt             = connection.prepareStatement(slabquery);
                if(buyrateid!=null)
                {
                   for(int i=0;i<buyrateid.size();i++)
                   {
                      pStmt.setString(1,(String)buyrateid.get(i)); 
                      pStmt.setString(2,(String)laneno.get(i)); 
                      rs  = pStmt.executeQuery();
                      breakslab  = new ArrayList();
                       chargerate  = new ArrayList();
                      while(rs.next())
                      {
                       
                       if(rs.getString("WEIGHT_BREAK_SLAB")!=null)
                         {
                            breakslab.add(rs.getString("WEIGHT_BREAK_SLAB"));
                         }
                         if(rs.getString("CHARGERATE")!=null)
                         {
                            chargerate.add(rs.getString("CHARGERATE"));
                         }
                      }
                        breakslab1.add(breakslab);
                          chargerate1.add(chargerate);
                   }
                }
              
             }
             
             if(breakslab1!=null)
             {
              for(int k=0;k<breakslab1.size();k++)
              {
                count  = 0;
                breakslab   =    (ArrayList)breakslab1.get(k);
                chargerate  =    (ArrayList)chargerate1.get(k);
                buyrate_id   =    (String)buyrateid.get(k);
                 lane_no     =    (String)laneno.get(k);
                             
                 if(breakslab!=null&&list!=null&&breakslab.size()==list.size())
                 {
                    for(int i =0;i<list.size();i++)
                    {
                        flatRatesDOB      =  (FlatRatesDOB)list.get(i);
                        for(int j=0;j<breakslab.size();j++)
                        {
                            if(flatRatesDOB.getWtBreakSlab().equalsIgnoreCase((String)breakslab.get(j)))
                            {
                              
                              if(flatRatesDOB.getChargeRate()==Double.parseDouble((String)chargerate.get(j)))
                                {
                                
                                    count++;
                                }
                                
                            }
                        }
                    }
                 }
             
                 flatRatesDOB      =  (FlatRatesDOB)list.get(0);
             if(list!=null&&count==list.size())
             {
                  
                    pStmt1    =  connection.prepareStatement(selectquery);
                    
                    pStmt1.setString(1,buyrate_id);
                     pStmt1.setString(2,lane_no);
                     rs1 = pStmt1.executeQuery();
                     pFrqStmt  =  connection.prepareStatement(updatequery);
                     while(rs1.next())
                     {
                         
                        pFrqStmt.setLong(1,rs1.getLong("QUOTE_ID"));
                        pFrqStmt.setLong(2,Long.parseLong(buyRateID));
                        pFrqStmt.setInt(3,flatRatesDOB.getLaneNo());
                        pFrqStmt.setString(4,"BR");
                        pFrqStmt.setString(5,"Carrier");
                        pFrqStmt.setString(6,flatRatesDOB.getOrigin()+"-"+flatRatesDOB.getDestination()+" Frequency");
                        pFrqStmt.setLong(7,Long.parseLong(buyrate_id));
                        pFrqStmt.setLong(8,Long.parseLong(lane_no));
                        pFrqStmt.execute();
                     }
                  
                    
                  }
              }
           }*/
           
         }catch(SQLException sq)
         {
           sq.printStackTrace();

	      throw new Exception ("Problem while fetching the Details <BR>");
         }catch(Exception e)
         {
           e.printStackTrace();
		         throw new Exception ("Problem while fetching the Details <BR>");
         }
         finally
         {
         try{
         if(rs!=null)
          rs.close();
           if(rs1!=null)
          //rs.close();
          rs1.close(); // changed by VLAKSHMI for connection leakage
          if(rsSelectQuoteId!=null)//added by VLAKSHMI for connection leakage
          rsSelectQuoteId.close();
           if(rsQuote!=null)//added by VLAKSHMI for connection leakage
          rsQuote.close();
          if(rsCount!=null)//added by VLAKSHMI for connection leakage
          rsCount.close();
           if(pStmt1!=null)
          pStmt1.close();
         if(pStmt!=null)
           pStmt.close();
           if(pStmtQuote!=null)// added by VLAKSHMI for connection leakage
           pStmtQuote.close();
             if(pstmtQmsQuoteInsert!=null)// added by VLAKSHMI for connection leakage
           pstmtQmsQuoteInsert.close();
              if(pstmtSelectQuoteId!=null)// added by VLAKSHMI for connection leakage
           pstmtSelectQuoteId.close();
            if(pstmtQmsQuoteupdate!=null)// added by VLAKSHMI for connection leakage
           pstmtQmsQuoteupdate.close();
           if(pStmtCount!=null)// added by VLAKSHMI for connection leakage
           pStmtCount.close();
             if(pFrqStmt!=null)
            pFrqStmt.close();
            if(cStmt1!=null)
            cStmt1.close();
         if(cStmt!=null)  
          { cStmt.close();}
         if(connection!=null)
           connection.close();
         }catch(Exception e)
         {
                 throw new Exception ("Problem while fetching the Details <BR>");
         }
         }
        // return "1";
         return list_errorList;
   }         
     /**
   * 
   * @return Connection
   * @throws java.sql.SQLException
   */
    private Connection getConnection() throws SQLException
    {
    
      return dataSource.getConnection();
    }

   private void inactivateRateDOB(RateDOB rateDOB,ArrayList list)	throws Exception
	{

      PreparedStatement	  pStmt3	      = null;	
      PreparedStatement	  pStmt		      = null;	
      Connection		  connection	  = null;
      String              sel_qry		  = null;
      String              update_qry      = null;
      FlatRatesDOB        flatRatesDOB    = null;
      OperationsImpl      impl            = null;
      ResultSet           rs              = null;
      String              buyRateID       = null;

	  try{

			 impl       = new OperationsImpl();
			 connection	 =	impl.getConnection();
			 

			 sel_qry	= " SELECT A.BUYRATEID BUYRATEID,A.LANE_NO LANE_NO FROM QMS_BUYRATES_MASTER B, QMS_BUYRATES_DTL A WHERE "+
						  " B.BUYRATEID = A.BUYRATEID AND A.ORIGIN=? AND A.DESTINATION =? AND A.SERVICE_LEVEL = ? "+
					      "  AND A.CARRIER_ID=? AND SHIPMENT_MODE =? AND WEIGHT_BREAK=? AND RATE_TYPE=? AND   "+
     					  " ((A.EFFECTIVE_FROM BETWEEN ? AND ?) OR(A.VALID_UPTO BETWEEN ? AND ?) )";

			update_qry =  "   UPDATE   QMS_BUYRATES_DTL  SET ACTIVEINACTIVE ='I' WHERE BUYRATEID=? AND LANE_NO=? ";

				 pStmt3	= connection.prepareStatement(sel_qry);
				 pStmt	= connection.prepareStatement(update_qry);
					int listSize	=	list.size();
			  for (int j=0;j<listSize ;j++ )
			  {
					  
					  flatRatesDOB =  (FlatRatesDOB)list.get(j);
					  pStmt3.clearParameters();
					  pStmt3.setString	(1,flatRatesDOB.getOrigin()); 
					  pStmt3.setString	(2,flatRatesDOB.getDestination()); 
					  pStmt3.setString	(3,flatRatesDOB.getServiceLevel()); 
					  pStmt3.setString	(4,rateDOB.getCarrierId()); 
					  pStmt3.setString	(5,rateDOB.getShipmentMode()); 
					  pStmt3.setString	(6,rateDOB.getWeightBreak()); 
					  pStmt3.setString	(7,rateDOB.getRateType()); 
					  pStmt3.setTimestamp(8,rateDOB.getEffectiveFrom());
					  pStmt3.setTimestamp(9,rateDOB.getValidUpto());
					  pStmt3.setTimestamp(10,rateDOB.getEffectiveFrom());
					  pStmt3.setTimestamp(11,rateDOB.getValidUpto());

					  rs	=	pStmt3.executeQuery();

					  while(rs.next())
					  {
							pStmt.clearParameters();
							pStmt.setString(1,rs.getString("BUYRATEID"));
							pStmt.setString(2,rs.getString("LANE_NO"));
							pStmt.executeUpdate();
					  }
//					  pStmt.executeBatch();

				if(rs!=null)
					rs.close();
			  }
			
			
			
			
	  }catch(SQLException e)
		{

				e.printStackTrace();
			 throw new Exception ("Problem while fetching the Details");
		}catch(Exception e)
		{
				e.printStackTrace();
			 throw new Exception ("Problem while fetching the Details");
		}finally{
        try{
        	if(rs!=null)
				rs.close();
			if(pStmt3!=null)
				pStmt3.close();
			if(pStmt!=null)
				pStmt.close();
			if(connection!=null)
				connection.close();
			}catch(Exception e)
			{
				 throw new Exception ("Problem while fetching the Details");
			}
		}

	}
     
   private void inactivateddRateDOB(RateDOB rateDOB,ArrayList list)	throws Exception
	{

      PreparedStatement	  pStmt3	      = null;	
      PreparedStatement	  pStmt		      = null;	
      Connection		  connection	  = null;
      String              sel_qry		  = null;
      String              update_qry      = null;
      FlatRatesDOB        flatRatesDOB    = null;
      OperationsImpl      impl            = null;
      ResultSet           rs              = null;
      String              buyRateID       = "";

	  try{

			 impl       = new OperationsImpl();
			 connection	 =	impl.getConnection();
			 

			 sel_qry	= " SELECT A.BUYRATEID BUYRATEID,A.LANE_NO LANE_NO FROM QMS_BUYRATES_MASTER B, QMS_BUYRATES_DTL A WHERE "+
						  " B.BUYRATEID = A.BUYRATEID AND A.SERVICE_LEVEL = ? "+
					      "  AND A.CARRIER_ID=? AND SHIPMENT_MODE =? AND WEIGHT_BREAK=?  AND   "+
     					  " ((A.EFFECTIVE_FROM BETWEEN ? AND ?) OR(A.VALID_UPTO BETWEEN ? AND ?) )"+
                " AND RATE_TYPE=?    AND ACTIVEINACTIVE IS NULL AND A.ORIGIN=? AND A.DESTINATION =?  ";

			update_qry =  "   UPDATE   QMS_BUYRATES_DTL  SET ACTIVEINACTIVE ='I' WHERE BUYRATEID=? AND LANE_NO=?";

				 pStmt3	= connection.prepareStatement(sel_qry);
				 pStmt	= connection.prepareStatement(update_qry);
					boolean rsE  =false;
				int listSize	=	list.size();
			  for (int j=0;j<listSize ;j++ )
			  {
					  
					  flatRatesDOB =  (FlatRatesDOB)list.get(j);
					  pStmt3.clearParameters();
			
					  pStmt3.setString	(1,flatRatesDOB.getServiceLevel()); 
					  pStmt3.setString	(2,rateDOB.getCarrierId()); 
					  pStmt3.setString	(3,rateDOB.getShipmentMode()); 
					  pStmt3.setString	(4,rateDOB.getWeightBreak()); 
					  pStmt3.setString	(9,rateDOB.getRateType()); 
					  pStmt3.setTimestamp(5,flatRatesDOB.getEffDate());
					  pStmt3.setTimestamp(6,flatRatesDOB.getValidUpto());
					  pStmt3.setTimestamp(7,flatRatesDOB.getEffDate());
					  pStmt3.setTimestamp(8,flatRatesDOB.getValidUpto());
		        pStmt3.setString	(10,flatRatesDOB.getOrigin()); 
					  pStmt3.setString	(11,flatRatesDOB.getDestination()); 
            
            
					  rs	=	pStmt3.executeQuery();
					  if(rs.next())
					  {

							pStmt.clearParameters();
							pStmt.setString(1,rs.getString("BUYRATEID"));
							pStmt.setString(2,rs.getString("LANE_NO"));
              //pStmt.executeUpdate();
              pStmt.addBatch();

					  }
            
					 

				if(rs!=null)
					rs.close();
			  }
        
			
			pStmt.executeBatch();
			
			//Logger.info(FILE_NAME,"after batch"+System.currentTimeMillis());
	  }catch(SQLException e)
		{
//Logger.info(FILE_NAME,"after batch"+System.currentTimeMillis());
				e.printStackTrace();
			 throw new Exception ("Problem while fetching the Details");
		}catch(Exception e)
		{
    //Logger.info(FILE_NAME,"after batch"+System.currentTimeMillis());
				e.printStackTrace();
			 throw new EJBException ("Problem while fetching the Details");
		}finally{
        try{
        	if(rs!=null)
				rs.close();
			if(pStmt3!=null)
				pStmt3.close();
			if(pStmt!=null)
				pStmt.close();
			if(connection!=null)
				connection.close();
			}catch(Exception e)
			{
				 throw new Exception ("Problem while fetching the Details");
			}
		}






	}

  public String validateRateDOB(String carrier,String currency,String shipmentMode,String terminalId)throws Exception
   {
      Connection				  connection		  = null;
      CallableStatement   cStmt           = null;
      String              errorMsg     = null;
      OperationsImpl      impl            = null;
      try
      {
         impl       = new OperationsImpl();
         connection	=	impl.getConnection();
          
          
         if("1".equals(shipmentMode))
          { shipmentMode = "('1','3','5','7')";}
         else if("2".equals(shipmentMode))
          { shipmentMode = "('2','3','6','7')";}  
         else if("4".equals(shipmentMode))
          { shipmentMode = "('4','5','6','7')";}
         else
          { shipmentMode = "('')";}
         
         cStmt  = connection.prepareCall("{ ?= call PKG_QMS_BUYRATES.VALIDATE_CURRENCY_CARRIER(?,?,?,?)}");
         
         cStmt.registerOutParameter(1,Types.VARCHAR);
         cStmt.setString(2,terminalId);
         cStmt.setString(3,shipmentMode);
         cStmt.setString(4,carrier);
         cStmt.setString(5,currency);
         
         cStmt.execute();
         
         errorMsg = cStmt.getString(1);
         if("1".equals(errorMsg))
            errorMsg = "";
          
      }catch(SQLException e)
      {
        e.printStackTrace();
        throw new Exception ("Problem while fetching the Details <BR>");
      }
      catch(Exception e)
      {
        e.printStackTrace();
        throw new Exception ("Problem while fetching the Details /n");
      }finally
      {
       try{
  
           if(cStmt!=null)
             cStmt.close(); 
           if(connection!=null)
             connection.close();
           }catch(Exception e)
           {
             
           }
        
      }
      return errorMsg;
   }


   public String validateRateDOB(String carrier,String currency)throws Exception
   {
      PreparedStatement		pStmt			      = null;	
      Connection				  connection		  = null;
      ResultSet           rs              = null;
      OperationsImpl      impl            = null;
      
      String              car_qry         = null;
      String              cur_qry         = null;
      StringBuffer        sb_errorMsg     = null;
    try
    {
     impl       = new OperationsImpl();
     connection	=	impl.getConnection();
     sb_errorMsg = new StringBuffer();
    
    car_qry = "  SELECT DISTINCT CARRIERID FROM FS_FR_CAMASTER WHERE SHIPMENTMODE IN (1,2,3,4,5,6,7) and  CARRIERID=? ";
    
    pStmt       = connection.prepareStatement(car_qry);
    pStmt.setString(1,carrier);
    rs = pStmt.executeQuery();
    if(!rs.next())
    {
      sb_errorMsg.append("Plese Enter Valid Carrier ID");
    }
    
         if(rs!=null)
           rs.close();
         if(pStmt!=null)
           pStmt.close(); 
           
   cur_qry=  "  SELECT 	CURRENCYID FROM	FS_COUNTRYMASTER WHERE CURRENCYID = ?    "        ;
     
    pStmt       = connection.prepareStatement(cur_qry);
    pStmt.setString(1,currency);
    rs = pStmt.executeQuery();
    
     if(!rs.next())
    {
      sb_errorMsg.append("Plese Enter Valid Currency ID");
    }
      
    }catch(SQLException e)
    {
      e.printStackTrace();
      throw new Exception ("Problem while fetching the Details <BR>");
    }
    catch(Exception e)
    {
      e.printStackTrace();
      throw new Exception ("Problem while fetching the Details /n");
    }finally
    {
     try{
         if(rs!=null)
          rs.close();
         if(pStmt!=null)
           pStmt.close(); 
         if(connection!=null)
           connection.close();
         }catch(Exception e)
         {
           
         }
      
    }
    return sb_errorMsg.toString();
     
   }
 
 public String validateDetailRateDOB(ArrayList lanes,RateDOB rateDOB)throws Exception
   {
      CallableStatement cStmt = null;
      Connection        connection = null;
      OperationsImpl      impl            = null;
      StringBuffer      sb_errorMsg = null;
      int lanesSize = 0;
      FlatRatesDOB flatRatesDOB   = null;
      String terminalId       = null;
      String shipmentMode     = null;
      String shipMode         = null;
      String returnStr        = null;
      String uom = null;
      try
      {
        terminalId    = rateDOB.getTerminalId();
        shipMode  = rateDOB.getShipmentMode();
        uom       = rateDOB.getUom();
        
        impl = new OperationsImpl();
        sb_errorMsg = new StringBuffer();
        connection = impl.getConnection();
        
         if("1".equals(shipMode))
          { shipmentMode = "('1','3','5','7')";}
         else if("2".equals(shipMode))
          { shipmentMode = "('2','3','6','7')";}  
         else if("4".equals(shipMode))
          { shipmentMode = "('4','5','6','7')";}
         else
          { shipmentMode = "('')";}
        
        cStmt = connection.prepareCall("{ ?= call PKG_QMS_BUYRATES.VALIDATE_ORG_DEST_SL(?,?,?,?,?,?,?,?)}");
        lanesSize = lanes.size();
        for(int i=0;i<lanesSize;i++)
        {
          flatRatesDOB = (FlatRatesDOB)lanes.get(i);
          
          cStmt.clearParameters();
          
          cStmt.registerOutParameter(1,Types.VARCHAR);
          cStmt.setString(2,flatRatesDOB.getOrigin());
          cStmt.setString(3,flatRatesDOB.getDestination());
          cStmt.setString(4,terminalId);
          cStmt.setString(5,shipmentMode);
          cStmt.setString(6,shipMode);
          cStmt.setString(7,flatRatesDOB.getServiceLevel());
          cStmt.setString(8,flatRatesDOB.getDensityRatio());
          cStmt.setString(9,uom);
          
          cStmt.execute();
          
          returnStr = cStmt.getString(1);
          
          if(returnStr!=null && !"".equals(returnStr.trim()) && !"1".equals(returnStr.trim()))
          {
            sb_errorMsg.append(returnStr);
            sb_errorMsg.append(" At LaneNo");
            sb_errorMsg.append(i+1);
            sb_errorMsg.append("<BR>");
          }
          
          
        }
        
      }catch(SQLException e)
      {
        e.printStackTrace();
        throw new Exception ("Problem while fetching the Details <BR>");
      }
      catch(Exception e)
      {
        e.printStackTrace();
        throw new Exception ("Problem while fetching the Details /n");
      }finally
      {
         try{
            if(cStmt!=null)
              { cStmt.close();}
            if(connection!=null)
              { connection.close();}
         }catch(Exception e)
         {
           
         }
      }
      
      return sb_errorMsg.toString();
      
   }
 
 /*
 public String validateDetailRateDOB(ArrayList lanes,RateDOB rateDOB)throws Exception
   {
      PreparedStatement		pStmt			      = null;	
      PreparedStatement		pStmt1		      = null;	
      PreparedStatement		pStmt2		      = null;	
      PreparedStatement		pStmt3		      = null;	
      Connection				  connection		  = null;
      ResultSet           rs              = null;
      ResultSet           rs1              = null;
      ResultSet           rs2              = null;
      ResultSet           rs3              = null;
      OperationsImpl      impl            = null;
      
      String              loc_qry         = null;
      String              date_qry        = null;
      String              ser_qry         = null;
      StringBuffer        sb_errorMsg     = null;
      FlatRatesDOB        flatRatesDOB    = null;
      boolean             flag            = false;
      
    try
    {
     impl       = new OperationsImpl();
     connection	=	impl.getConnection();
     sb_errorMsg = new StringBuffer();
    
    loc_qry = "  SELECT LOCATIONID FROM FS_FR_LOCATIONMASTER WHERE LOCATIONID=? ";
    
    ser_qry = "  SELECT SERVICELEVELID FROM FS_FR_SERVICELEVELMASTER WHERE SERVICELEVELID=? ";
    
    date_qry= " SELECT A.BUYRATEID FROM QMS_BUYRATES_MASTER B, QMS_BUYRATES_DTL A WHERE "+
              " B.BUYRATEID = A.BUYRATEID AND A.ORIGIN=? AND A.DESTINATION =? AND A.SERVICE_LEVEL = ? "+
              "  AND B.CARRIER_ID=? AND SHIPMENT_MODE =? AND WEIGHT_BREAK=? AND RATE_TYPE=?   ";
//              " ((A.EFFECTIVE_FROM BETWEEN ? AND ?) OR(A.VALID_UPTO BETWEEN ? AND ?) )";
    
    
    pStmt       = connection.prepareStatement(loc_qry);
    pStmt1      = connection.prepareStatement(loc_qry);
    pStmt2      = connection.prepareStatement(ser_qry);
//    pStmt3      = connection.prepareStatement(date_qry);
    
   
    
     
     for(int i=0;i<lanes.size();i++)
     {
     
      flag            = false;
      flatRatesDOB = (FlatRatesDOB)lanes.get(i);
      
      if(rs!=null)
        rs.close();
      if(rs1!=null)
        rs1.close();
       if(rs2!=null)
        rs2.close();  
      if(rs3!=null)
        rs3.close();          
     
      pStmt.setString(1,flatRatesDOB.getOrigin()); 
      pStmt1.setString(1,flatRatesDOB.getDestination()); 
      pStmt2.setString(1,flatRatesDOB.getServiceLevel()); 
      
//      pStmt3.setString(1,flatRatesDOB.getOrigin()); 
//      pStmt3.setString(2,flatRatesDOB.getDestination()); 
//      pStmt3.setString(3,flatRatesDOB.getServiceLevel()); 
//      pStmt3.setString(4,rateDOB.getCarrierId()); 
//      pStmt3.setString(5,rateDOB.getShipmentMode()); 
//      pStmt3.setString(6,rateDOB.getWeightBreak()); 
//      pStmt3.setString(7,rateDOB.getRateType())\; 
//      pStmt3.setTimestamp(8,rateDOB.getEffectiveFrom());
//      pStmt3.setTimestamp(9,rateDOB.getValidUpto());
//      pStmt3.setTimestamp(10,rateDOB.getEffectiveFrom());
//      pStmt3.setTimestamp(11,rateDOB.getValidUpto());
        
      rs =    pStmt.executeQuery();
      rs1 =   pStmt1.executeQuery();
      rs2 =   pStmt2.executeQuery();
//      rs3 =   pStmt3.executeQuery();
//      if(rs3.next())
//      {
//        sb_errorMsg.append("Origin "+flatRatesDOB.getOrigin()+",Destination "+flatRatesDOB.getDestination()+",ServiceLevel "+flatRatesDOB.getServiceLevel()+",Carrier "+rateDOB.getCarrierId()+",Effective From  "+rateDOB.getEffectiveFrom()+",ValidUpto "+rateDOB.getValidUpto()+" are Already Defined");
//         flag            = true;
//      }
      if(!rs.next())
      {
        sb_errorMsg.append("Origin  ");
         flag            = true;
      }
      if(!rs1.next())
      {
        sb_errorMsg.append("Destination ");
        flag            = true;
      }
      if(!rs2.next())
      {
        sb_errorMsg.append("ServiceLevel ");
        flag            = true;
      }
      if(flag)
      {
        sb_errorMsg.append("are Invalid At Line"+(i+1)+" <BR>");
      }
      
      
       
     }
    
   

  
    }catch(SQLException e)
    {
      e.printStackTrace();
      throw new Exception ("Problem while fetching the Details <BR>");
    }
    catch(Exception e)
    {
      e.printStackTrace();
      throw new Exception ("Problem while fetching the Details /n");
    }finally
    {
     try{
          if(rs!=null)
            rs.close();
          if(rs1!=null)
            rs1.close();
          if(rs2!=null)
            rs2.close();  
          if(rs3!=null)
            rs3.close();     
          if(pStmt!=null)
             pStmt.close(); 
          if(pStmt1!=null)
             pStmt1.close();
          if(pStmt2!=null)
            pStmt2.close();    
          if(pStmt3!=null)
            pStmt3.close();      
          if(connection!=null)
           connection.close();
         }catch(Exception e)
         {
           
         }
      
    }
    return sb_errorMsg.toString();
     
   }
 */
 
 
  public ArrayList getRateDetails(QMSSellRatesDOB sellRatesDob,ESupplyGlobalParameters loginBean,String operation)throws SQLException,Exception
  {
      CallableStatement csmt  = null;
      ResultSet         rs    = null;
      Statement         srchargeStmt = null;//Added by Govind for the cr-219973
      ResultSet         srRs 	=	null;//Added by Govind for the cr-219973
      
      ArrayList         dataList = null;
      QMSSellRatesDOB   qmsSellRatesDob = null;
      String            weightBreaks =  "";
      String            chargeRates  =  "";
      String            lBound       =  "";
      String            uBound       =  "";
      String            chargeIn     =  "";
      String            ratedesc     =  "";
      String[]          weigthBreaksArray = null;
      String[]          chargeRatesArray  = null;
      String[]          lowerBoundArry    = null;
      String[]          upperBoundArry    = null;
      String[]          chargeIndicator   = null;
      String[]            rateDescription     =  null;
      String                orign             =   null;
      String                destination       =   null;
      String                carrier           =   null;
      String                serviceLevel      =   null;
      String                orignStr          =   null;
      String                destinationStr    =   null;
      String                serviceStr        =   null;
      String                carrierStr        =   null;
      OperationsImpl        impl              =   null;
      Connection            connection        =   null;
      Connection            srConnection	  =	  null;//@@Added By Govind for the CR-219973	
      ESupplyDateUtility		fomater					=	 null;
      String                currencyKey     = null;
      HashMap               map             = null;
      HashMap               surchargeMap    = null;//Added by Govind for the cr-219973
      double                currencyFactor  = 0.0;
      double[]  chargeRts                   = null;
	  ArrayList         finalList         =   null;
	  String[] 		    surChargeCurrency = null; // Added by Gowtham  for Surcharge Currency
	  String 			surChargeCurr      = null; // Added by Gowtham for surcharge currency
	  String			currencyDesc	=	null;// Added by Gowtham for surcharge currency
	  String[]		surChrgCurrArray = null ; // Added by Gowtham for surcharge currency
	  
//@@ Added by subrahmanyam for the pbn id: 211196 on 20-Jul-10	  
	  PreparedStatement pstmt1 = null;
	  ResultSet rs1= null;// Added by Dilip for PMD Correction on 22/09/2015
	 String lovSize	= "SELECT PARAM_VALUE FROM FS_USER_PREFERENCES WHERE USERID='"+loginBean.getUserId()+"'"+" AND LOCATIONID='"+loginBean.getTerminalId()+"' AND  PARAM_NAME='segmentSize'";
//@@ Ended by subrahmanyam for the pbn id: 211196 on 20-Jul-10
	
	 try
      {
          
		  map             =   new HashMap();
          finalList         =   new ArrayList();
          fomater			    =   new ESupplyDateUtility();
          dataList            =   new ArrayList();
          orignStr            =   sellRatesDob.getOrigin();
          destinationStr      =   sellRatesDob.getDestination();
          serviceStr          =   sellRatesDob.getServiceLevel();
          carrierStr          =   sellRatesDob.getCarrier_id();
          orign               =   orignStr!= null?orignStr.replaceAll(",","~"):"";
          destination         =   destinationStr!= null?destinationStr.replaceAll(",","~"):"";
          serviceLevel        =   serviceStr!= null?serviceStr.replaceAll(",","~"):"";
          carrier             =   carrierStr.replaceAll(",","~"); 
          impl                =   new OperationsImpl();
          connection          =   impl.getConnection();
         // csmt = connection.prepareCall("{CALL qms_buy_rates_pkg.buy_rates_proc(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)} ");
       // csmt = connection.prepareCall("{CALL qms_buy_rates_pkg.buy_rates_proc(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)} ");
          //csmt = connection.prepareCall("{CALL qms_buy_rates_pkg.buy_rates_proc(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)} ");
        // csmt = connection.prepareCall("{CALL qms_buy_rates_pkg.buy_rates_proc(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)} ");
 //@@ Added by subrahmanyam for the pbn id: 211196 on 20-Jul-10        
          pstmt1 = connection.prepareStatement(lovSize);
          //ResultSet rs1 =pstmt1.executeQuery();
          rs1 =pstmt1.executeQuery();
          int lovsizeVal=0;
		if(rs1.next())
        	  lovsizeVal = rs1.getInt(1);
//@@ Ended by subrahmanyam for the pbn id: 211196 on 20-Jul-10		
		//@@ Added By Govind for the CR-219973 
		if("DOWNLOAD".equalsIgnoreCase(operation)){
			srConnection = impl.getConnection();
		    srchargeStmt = srConnection.createStatement();
		    srRs = srchargeStmt.executeQuery("select DISTINCT srm.surcharge_desc,srm.surcharge_id from qms_surcharge_master srm where srm.rate_break like 'Slab' or srm.rate_break like 'List'");
		    surchargeMap = new HashMap();
		    while(srRs.next())
		    {
		    	surchargeMap.put(srRs.getString("surcharge_desc"),srRs.getString("surcharge_id"));
		    }
		  //  sellRatesDob.setSurchargesIds(surchargeMap)  ;	
		    srRs.close();
		    srchargeStmt.close();
		    srConnection.close();
		}//@@Ended by Govind for the CR-219973
		//System.out.println("In DAO surchargeMap-->"+surchargeMap);
		
		
		
		if("buyratesexpiry".equalsIgnoreCase(operation))
			csmt = connection.prepareCall("{CALL qms_buy_rates_pkg.buy_rates_Expiry_proc(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)} ");//modified by phani sekhar  on 20090605 for wpbn 171213		
	   else
          csmt = connection.prepareCall("{CALL qms_buy_rates_pkg.buy_rates_proc(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)} ");//modified by phani sekhar  on 20090605 for wpbn 171213
         
          csmt.setString(1,orign);
          csmt.setString(2,destination);
          csmt.setString(3,loginBean.getTerminalId());
          csmt.setString(4,sellRatesDob.getRateType().toUpperCase());
          csmt.setString(5,sellRatesDob.getWeightBreak().toUpperCase());
          csmt.setString(6,serviceLevel);
          csmt.setString(7,carrier);
          csmt.setString(8,sellRatesDob.getShipmentMode());
          csmt.setString(9,"buyratesexpiry".equalsIgnoreCase(operation)?"Download":operation);
     if(pstmt1!=null)
        	  pstmt1.close();
          if(rs1 !=null)
        	  rs1.close();
         // csmt.setTimestamp(10,fomater.getTimestampWithTimeAndSeconds("DD-MM-YY",loginBean.getCurrentDateString(),"00:00:00"));
          
		
         /* if(!"DOWNLOAD".equalsIgnoreCase(operation))
            { csmt.setInt(11,Integer.parseInt(sellRatesDob.getPageNo()));}
          else
            { csmt.setNull(11,Types.INTEGER);}
            
          csmt.setInt(12,10);
          
          csmt.registerOutParameter(13,OracleTypes.INTEGER);
          csmt.registerOutParameter(14,OracleTypes.INTEGER);          
          csmt.registerOutParameter(15,OracleTypes.CURSOR);*/
           if(!"DOWNLOAD".equalsIgnoreCase("buyratesexpiry".equalsIgnoreCase(operation)?"Download":operation))
            { csmt.setInt(10,Integer.parseInt(sellRatesDob.getPageNo()));}
          else
            { csmt.setNull(10,Types.INTEGER);}
  //@@ Commented & Added by subrahmanyam for the pbn id: 211196 on 20-Jul-10   		
         //csmt.setInt(11,10);
           csmt.setInt(11,lovsizeVal);
//@@ Ended by subrahmanyam for the pbn id: 211196 on 20-Jul-10          
            //added and modified by phani sekhar for  wpbn 171213 on 20090605
           csmt.setString(12,sellRatesDob.getOriginCountry());
            csmt.setString(13,sellRatesDob.getDestinationCountry());
             csmt.setString(14,sellRatesDob.getOriginRegions());
             if("buyratesexpiry".equalsIgnoreCase(operation)){
          csmt.setString(15,sellRatesDob.getDestRegions());
          csmt.setTimestamp(16,sellRatesDob.getFromdate());
          csmt.setTimestamp(17,sellRatesDob.getToDate());
          csmt.registerOutParameter(18,OracleTypes.INTEGER);
          csmt.registerOutParameter(19,OracleTypes.INTEGER);          
          csmt.registerOutParameter(20,OracleTypes.CURSOR);
          }else
             {
            	  csmt.setTimestamp(15,sellRatesDob.getToDate());
                  csmt.registerOutParameter(16,OracleTypes.INTEGER);
                  csmt.registerOutParameter(17,OracleTypes.INTEGER);          
                  csmt.registerOutParameter(18,OracleTypes.CURSOR);
             }
             
        // @@ ends phani 171213

          csmt.execute();
          if("buyratesexpiry".equalsIgnoreCase(operation))   
        rs = (ResultSet)csmt.getObject(20);
        else
        	  rs = (ResultSet)csmt.getObject(18);      	
  
        int icount = 0;
        while(rs.next())
        {
          qmsSellRatesDob = new QMSSellRatesDOB();
          if(icount==0)
        	  qmsSellRatesDob.setSurchargesIds(surchargeMap)  ;	//Added by Govind
          
          System.out.println("qmsSellRatesDob"+qmsSellRatesDob);
          qmsSellRatesDob.setBuyRateId(rs.getString("BUYRATEID"));
          qmsSellRatesDob.setCarrier_id(rs.getString("CARRIER_ID"));
          qmsSellRatesDob.setOrigin(rs.getString("ORIGIN"));
          qmsSellRatesDob.setOriginCountry(rs.getString("ORG_COUNTRYID"));
          qmsSellRatesDob.setDestination(rs.getString("DESTINATION"));
           qmsSellRatesDob.setDestinationCountry(rs.getString("DEST_COUNTRYID"));
          
          qmsSellRatesDob.setServiceLevel(rs.getString("SERVICE_LEVEL"));
          qmsSellRatesDob.setTransitTime(rs.getString("TRANSIT_TIME"));
          qmsSellRatesDob.setFrequency(rs.getString("FREQUENCY"));
          qmsSellRatesDob.setLanNumber(rs.getInt("LANE_NO"));
          qmsSellRatesDob.setDensityRatio(rs.getString("DENSITY_CODE"));
               
              int count=0 ,count1=0,count2=0; // count=2 KISHORE FOR Buy Rate Modify 
              String s1        =  "";
              String s2        =  "";
              String s3        =  "";
              String s4        =  "";
              String s5        =  "";
              String s6        =  "";
              String temp      =  "";
          //weightBreaks = rs.getString("WEIGHTBREAKS");
             weightBreaks          =   rs.getString("WEIGHT_BREAK_SLAB");
             weigthBreaksArray     =   weightBreaks.split(",");
             chargeRates           =   rs.getString("CHARGERATE");
             chargeRatesArray      =   chargeRates.split(",");
             chargeIn              =   rs.getString("C_INDICATOR");
             chargeIndicator       = new String[weigthBreaksArray.length];
          
             //Modified By Kishore Podili For Buyrates Download
             
             /* currencyDesc 			=	rs.getString("SUR_CHARGE_CURRENCY");// Added by Gowtham for surchrg Desc
             surChrgCurrArray		=	currencyDesc.split(",");// Added by Gowtham for surchrg Desc
             */
             
             surChargeCurr		=  rs.getString("SUR_CHARGE_CURRENCY"); // Added by Gowtham for surcharge description
             surChargeCurrency = surChargeCurr.split(",");
             
             if("DOWNLOAD".equalsIgnoreCase(operation)){
            	 ratedesc        =  rs.getString("RATE_DESCRIPTION");
            	 rateDescription =ratedesc.split(",");
             }
             
             // End Of Kishore Podili For BuyRates DownLoad 
             
             if(chargeIn!=null)
             {
                 chargeIndicator       =   chargeIn.split(",");
             }
             lBound          = rs.getString("LBOUND");
             lowerBoundArry  = lBound.split(",");
             uBound          = rs.getString("UBOUND");
             upperBoundArry  = uBound.split(",");
             
               if(!"DOWNLOAD".equalsIgnoreCase(operation)||("DOWNLOAD".equalsIgnoreCase(operation)&&(!"LIST".equalsIgnoreCase(sellRatesDob.getWeightBreak())||("LIST".equalsIgnoreCase(sellRatesDob.getWeightBreak())&&!"FCL".equalsIgnoreCase(sellRatesDob.getConsoleType())))))
               {
             String[]  weightbreaks           =  new String[weigthBreaksArray.length];  
             String[]  ratebreaks             =   new String[weigthBreaksArray.length];  
             String[]  chargeindicators       =   new String[weigthBreaksArray.length];  
             String[]  breaks                 =  new String[weigthBreaksArray.length];   
             String[]  rates                  =  new String[weigthBreaksArray.length];   
             String[]  indicators             =  new String[weigthBreaksArray.length];   
             String[]  ratedescs             =  new String[weigthBreaksArray.length]; 
             String[]  ratedescription        =  new String[weigthBreaksArray.length]; 
             String[]  surchrgcurrency 	      = new String[weigthBreaksArray.length]; // Added by Gowtham for surchrg desc
             String[]  surchrgdescs			=	new String[weigthBreaksArray.length]; // Added by Gowtham for surchrg desc
             
             // Added By Kishore Podili For BuyRates DownLoad 
             if(!"DOWNLOAD".equalsIgnoreCase(operation)){
            ratedesc        =  rs.getString("RATE_DESCRIPTION");
            rateDescription =ratedesc.split(",");
             }
             	
          //Commented By Kishore Podili for BR Download Issue   
          /*surChargeCurr		=  rs.getString("SUR_CHARGE_CURRENCY"); // Added by Gowtham for surcharge description
            surChargeCurrency = surChargeCurr.split(","); // Added by Gowtham for surchrge description
          */
          
            
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
    						surChargeCurrency[process]=null; // Added by Gowtham for surcharge description
    						//surChrgCurrArray[process] = null;// Added by Gowtham for surcharge description
    						process_count ++;

            	}
            }
            String[] weigthBreaksArray_process = new String[weigthBreaksArray.length-process_count];
            String[] chargeRatesArray_process = new String[weigthBreaksArray.length-process_count];
            String[] chargeIndicator_process = new String[weigthBreaksArray.length-process_count];
            String[] lowerBoundArry_process = new String[weigthBreaksArray.length-process_count];
            String[] upperBoundArry_process = new String[weigthBreaksArray.length-process_count];
            String[] rateDescription_process = new String[weigthBreaksArray.length-process_count];
            String[] surChrgCurrDesc_process = new String[weigthBreaksArray.length-process_count]; // Added by Gowtham for surcharge Desc
            
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
            		surChrgCurrDesc_process[process_count1] =   surChargeCurrency[process]; // Added by Gowtham for surchrg Desc
            		//surChrgCurrArray_process[process_count1]	=	surChrgCurrArray[process];//Added by Gowtham for surchrg Desc
            		process_count1 ++;

            	}
            }
            weigthBreaksArray	=	weigthBreaksArray_process;
            chargeRatesArray	=	chargeRatesArray_process;
            chargeIndicator		=	chargeIndicator_process;
            lowerBoundArry		=	lowerBoundArry_process;
            upperBoundArry		=	upperBoundArry_process;
            rateDescription		=	rateDescription_process;
            surChargeCurrency	=	  surChrgCurrDesc_process; // Added by Gowtham for surchrg Desc
          //  surChrgCurrArray	=	surChrgCurrDesc_process ; // Added by Gowtham for surchrg Desc
           
            
            int wtBrkArrLen	=	weigthBreaksArray.length;
             if("SLAB".equalsIgnoreCase(sellRatesDob.getWeightBreak()))
            {
            	 //count=2;count1=0;count2=0;
             for(int n=0;n<wtBrkArrLen;n++)
             {
                if(weigthBreaksArray[n]!=null)
                {     //@@ Modified by subrahmanyam for the CR-219973 to filter MIN from the surcharges.....
                	  if(weigthBreaksArray[n].equalsIgnoreCase("Basic") && "A FREIGHT RATE".equalsIgnoreCase(rateDescription[n]))
                      {
                           s1   =   weigthBreaksArray[n];
                           s2 =     chargeRatesArray[n];
                           s3 =     chargeIndicator[n];
                      }
                	//@@ Modified by subrahmanyam for the CR-219973 to filter MIN from the surcharges.....
               	  else  if(weigthBreaksArray[n].startsWith("M") && "A FREIGHT RATE".equalsIgnoreCase(rateDescription[n]))
                {
                     s4   =   weigthBreaksArray[n];
                     s5 =     chargeRatesArray[n];
                     s6 =     chargeIndicator[n];
                }
               /*else if((weigthBreaksArray[n].startsWith("S")||weigthBreaksArray[n].startsWith("F")||
               weigthBreaksArray[n].startsWith("B")||weigthBreaksArray[n].startsWith("C")||weigthBreaksArray[n].startsWith("P"))&& !"A FREIGHT RATE".equalsIgnoreCase(rateDescription[n]))*/
               	 else if(!"A FREIGHT RATE".equalsIgnoreCase(rateDescription[n]) && !"**".equalsIgnoreCase(rateDescription[n])  && !"-".equalsIgnoreCase(rateDescription[n]))
                 {
                 
                  weightbreaks[count2]        =   weigthBreaksArray[n];
                  ratedescs[count2]            =   rateDescription[n];
                  surchrgdescs[count2]			=	surChargeCurrency[n];// Added by Gowtham for surchrg desc
                 
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
              	//@@ Added by subrahmanyam for the CR-219973 to Handle slab the surcharges.....
               	else if("A FREIGHT RATE".equalsIgnoreCase(rateDescription[n]) || "-".equalsIgnoreCase(rateDescription[n]))
               {
                     breaks[count1]        =   weigthBreaksArray[n];
                      ratedescription[count1]        =   rateDescription[n];
                      surchrgcurrency[count1]		= 	surChargeCurrency[n];// Added by Gowtham for surchrg desc
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
                ||"PSSM3".equalsIgnoreCase(weightbreaks[j])&&"PSSMIN".equalsIgnoreCase(weightbreaks[j+1]) 
                || (weightbreaks[j] !=null && weightbreaks[j+1] !=null && ratedescs[j] == ratedescs[j+1]  
                      && weightbreaks[j].endsWith("FLAT")&& weightbreaks[j+1].endsWith("MIN") && weightbreaks[j+1].length()>3)) //Added by kishore for continuos surcharge Breaks FLAT(sch1), MIN, FLAT(sch2)
                {
                    temp                   =    weightbreaks[j];
                    weightbreaks[j]        =    weightbreaks[j+1];
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
              /* if("SSKG".equalsIgnoreCase(weightbreaks[j])&&"SSMIN".equalsIgnoreCase(weightbreaks[j+1]))
                {
                    temp                   =    weightbreaks[j];
                    weightbreaks[j]        =    weightbreaks[j+1];
                    weightbreaks[j+1]      =    temp;
                    temp                   =     ratebreaks[j];
                    ratebreaks[j]          =     ratebreaks[j+1];
                    ratebreaks[j+1]        =     temp;
                    temp                   =     chargeindicators[j];
                    chargeindicators[j]    =     chargeindicators[j+1];
                    chargeindicators[j+1]  =     temp;
                }*/
             }
            }
            if(breaks!=null)
            {
            	int brksLen	=	breaks.length;
             for(int s=0;s<brksLen;s++)
             {
              for(int r=0;r<brksLen-1;r++)
              {
                if( breaks[r]!=null&&breaks[r+1]!=null)
                {
        if(Double.parseDouble(breaks[r])>Double.parseDouble(breaks[r+1]))
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
            
//@@ Added for surcharge sorting
   /*         Set surChargeSet	= new LinkedHashSet();
            for(String rdes: rateDescription)
            {
            	if(!"A FREIGHT RATE".equalsIgnoreCase(rdes) && !"-".equalsIgnoreCase(rdes))
            		surChargeSet.add(rdes);
            }
            Iterator surChargeItr = surChargeSet.iterator();
            String   tempSurCharge	=	"";
            if(weightbreaks!=null)
            {
            	int weightbreaksLen	=	weightbreaks.length;
            	while(surChargeItr.hasNext()){
            	            	tempSurCharge	= (String)surChargeItr.next();
             for(int s=0;s<weightbreaksLen;s++)
             {
              for(int r=0;r<weightbreaksLen;r++)
              {
                if( weightbreaks[s]!=null&&weightbreaks[r]!=null &&  tempSurCharge.equalsIgnoreCase(rateDescription[s])&& tempSurCharge.equalsIgnoreCase(rateDescription[r]) && isNumber(weightbreaks[s]) &&  isNumber(weightbreaks[r]) )
                {
                if(Integer.parseInt(weightbreaks[r])>Integer.parseInt(weightbreaks[s]))
                 {
                        temp             =     weightbreaks[r];
                        weightbreaks[r]        =     weightbreaks[s];
                        weightbreaks[s]      =     temp;
                        temp          	   =     ratebreaks[r];
                        ratebreaks[r]         =     ratebreaks[s];
                        ratebreaks[s]       =     temp;
                         if(chargeindicators[r]!=null)
                         {
                            temp             =     chargeindicators[r];
                            chargeindicators[r]    =     chargeindicators[s];
                            chargeindicators[s]  =     temp;
                         }
                  }
                }
                }
              }
             }//End while
            }
            */
//@@ ended for surcharges sorting            
           //  Collections.sort(breaks);
            if(s1!= null && !"".equals(s1)){
            weigthBreaksArray[0] = s1;
            chargeRatesArray[0] = s2;
            chargeIndicator[0]  = s3;
            rateDescription[0] ="A FREIGHT RATE";
            count++;
            }
            if(s4!= null && !"".equals(s4)){
              weigthBreaksArray[1] = s4;
              chargeRatesArray[1] = s5;
              chargeIndicator[1]  = s6;
              rateDescription[1] ="A FREIGHT RATE";
              count++;
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
                  rateDescription[count]     =  "A FREIGHT RATE";
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
                  chargeIndicator[count]    =   chargeindicators[t];
                   rateDescription[count]     =  ratedescs[t];
                   surChargeCurrency[count]     =  surchrgdescs[t]; // Added By Gowtham For Surcharge Currency 
                   count++;
                }
               }
             }
          }
         else   if("FLAT".equalsIgnoreCase(sellRatesDob.getWeightBreak()))
            {
            count = 0;count1=0;count2=0;
             for(int n=0;n<wtBrkArrLen;n++)
             {
                if(weigthBreaksArray[n]!=null)
                {      //gOVIND
                  if( ( "BASIC".equalsIgnoreCase(weigthBreaksArray[n]) ||"MIN".equalsIgnoreCase(weigthBreaksArray[n])||"FLAT".equalsIgnoreCase(weigthBreaksArray[n]) ) && "A FREIGHT RATE".equalsIgnoreCase(rateDescription[n]) )
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
                      surchrgdescs[count2]		= surChargeCurrency[n]; // Added By Gowtham For Surcharge Currency 
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
            	   int wtBrkLen	=	weightbreaks.length;
               for(int j=0;j<wtBrkLen-1;j++)
             {
                if(("FSKG".equalsIgnoreCase(weightbreaks[j])&&"FSMIN".equalsIgnoreCase(weightbreaks[j+1]))||("SSKG".equalsIgnoreCase(weightbreaks[j])&&"SSMIN".equalsIgnoreCase(weightbreaks[j+1]))
                ||("CAF%".equalsIgnoreCase(weightbreaks[j])&&"CAFMIN".equalsIgnoreCase(weightbreaks[j+1]))||"BAFM3".equalsIgnoreCase(weightbreaks[j])&&"BAFMIN".equalsIgnoreCase(weightbreaks[j+1])
                ||"PSSM3".equalsIgnoreCase(weightbreaks[j])&&"PSSMIN".equalsIgnoreCase(weightbreaks[j+1])
                || ( weightbreaks[j]!=null && weightbreaks[j+1]!=null && ratedescs[j] == ratedescs[j+1]
                		&& weightbreaks[j].endsWith("FLAT")&& weightbreaks[j+1].endsWith("MIN") && weightbreaks[j+1].length()>3) ) //Added by kishore for continuos surcharge Breaks FLAT(sch1), MIN, FLAT(sch2)
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
            	 int wtBrksLen	=	weightbreaks.length;
              for(int t=0;t<wtBrksLen;t++)
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
                   surChargeCurrency[count]	  = surchrgdescs[t]; // Added by Gowtham For Surcharge Currency 
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
              /*    if(!("FSBASIC".equalsIgnoreCase(weigthBreaksArray[n])||"FSMIN".equalsIgnoreCase(weigthBreaksArray[n])||"FSKG".equalsIgnoreCase(weigthBreaksArray[n])
                     ||"SSBASIC".equalsIgnoreCase(weigthBreaksArray[n])||"SSMIN".equalsIgnoreCase(weigthBreaksArray[n])||"SSKG".equalsIgnoreCase(weigthBreaksArray[n])||"SURCHARGE".equalsIgnoreCase(weigthBreaksArray[n]))|| !"A FREIGHT RATE".equalsIgnoreCase(rateDescription[n]))*/
                    if("A FREIGHT RATE".equalsIgnoreCase(rateDescription[n]) || "-".equalsIgnoreCase(rateDescription[n])) 
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
                      surchrgdescs[count2] = 	surChargeCurrency[n]; // added by Gowtham For Surcharge Currency 
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
        
             
          }
              
          else  if("LIST".equalsIgnoreCase(sellRatesDob.getWeightBreak())&&"FCL".equalsIgnoreCase(sellRatesDob.getConsoleType()))
          {
           for(int n=0;n<wtBrkArrLen;n++)
             {
                if(weigthBreaksArray[n]!=null)
                {
 /*                  if(!(weigthBreaksArray[n].endsWith("CAF")||weigthBreaksArray[n].endsWith("BAF")
                   ||weigthBreaksArray[n].endsWith("CSF")||weigthBreaksArray[n].endsWith("PSS")))
*/                  if("A FREIGHT RATE".equalsIgnoreCase(rateDescription[n]) || "-".equalsIgnoreCase(rateDescription[n])) 
                	{
                       weightbreaks[count1] = weigthBreaksArray[n];
                       ratedescs[count1]    = "A FREIGHT RATE";
                       surchrgdescs[count1] = "-";  // Added By Gowtham For Surcharge Currency 
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
                    surchrgdescs[count1] = surChargeCurrency[n];  // Added By Gowtham  For Surcharge Currency 
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
    /*       for(int n=0;n<wtBrkArrLen;n++)
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
             }*/
              
             // if(icount==0)
               qmsSellRatesDob.setWeightBreaks(weightbreaks);
            qmsSellRatesDob.setAllWeightBreaks(weightbreaks);
            qmsSellRatesDob.setChargeInr(chargeindicators);
            qmsSellRatesDob.setChargeRates(ratebreaks);
            qmsSellRatesDob.setRateDescription(ratedescs);
            qmsSellRatesDob.setSurChargeCurency(surchrgdescs);// Added by Gowtham For Surcharge Currency 
           
          }
              /* if(icount==0)
          {*/
            if("FLAT".equalsIgnoreCase(sellRatesDob.getWeightBreak())||"SLAB".equalsIgnoreCase(sellRatesDob.getWeightBreak())||("LIST".equalsIgnoreCase(sellRatesDob.getWeightBreak())&&!("FCL".equalsIgnoreCase(sellRatesDob.getConsoleType()))))
           {
            qmsSellRatesDob.setWeightBreaks(weigthBreaksArray);
             }
          //}
           if("FLAT".equalsIgnoreCase(sellRatesDob.getWeightBreak())||"SLAB".equalsIgnoreCase(sellRatesDob.getWeightBreak())||("LIST".equalsIgnoreCase(sellRatesDob.getWeightBreak())&&!("FCL".equalsIgnoreCase(sellRatesDob.getConsoleType()))))
           {
            //qmsSellRatesDob.setWeightBreaks(weigthBreaksArray);
             qmsSellRatesDob.setChargeInr(chargeIndicator);
             qmsSellRatesDob.setAllWeightBreaks(weigthBreaksArray);
            qmsSellRatesDob.setChargeRates(chargeRatesArray);
             qmsSellRatesDob.setRateDescription(rateDescription);
             qmsSellRatesDob.setSurChargeCurency(surChargeCurrency); // Added by Gowtham for Surcharge Currency
          }
        }
               else
               {
                 //govind if(icount==0)
               qmsSellRatesDob.setWeightBreaks(weigthBreaksArray);
          qmsSellRatesDob.setChargeInr(chargeIndicator);
             qmsSellRatesDob.setAllWeightBreaks(weigthBreaksArray);
            qmsSellRatesDob.setChargeRates(chargeRatesArray);
             qmsSellRatesDob.setRateDescription(rateDescription);
             qmsSellRatesDob.setSurChargeCurency(surChargeCurrency); // Added by Gowtham for Surcharge Currency
               
               
               }
             
          /*currencyKey    =  sellRatesDob.getCurrencyId()+"&"+rs.getString("CURRENCY");
          if(map.containsKey(currencyKey))
          {
                currencyFactor  = Double.parseDouble((String)map.get(currencyKey));
           }
          else
          {
              currencyFactor  = impl.getConvertionFactor(rs.getString("CURRENCY"),sellRatesDob.getCurrencyId());
              map.put(currencyKey,String.valueOf(currencyFactor));
          }
          int chargeRatesArraySize  = chargeRatesArray.length;
          chargeRts       = new double[chargeRatesArraySize];
          for(int i=0;i<chargeRatesArraySize;i++)
          {
            if(currencyFactor>0)
              chargeRts[i]  = impl.getConvertedAmt(Double.parseDouble(chargeRatesArray[i]),currencyFactor);
            else
              chargeRts[i]  = Double.parseDouble(chargeRatesArray[i]);
          }*/
          
          /*int chargeRatesArraySize  = chargeRatesArray.length;
          chargeRts       = new double[chargeRatesArraySize];
          for(int i=0;i<chargeRatesArraySize;i++)
          {
              chargeRts[i]  = Double.parseDouble(chargeRatesArray[i]);
          }     */     
          // qmsSellRatesDob.setChargeRates(chargeRatesArray);
          
          
          qmsSellRatesDob.setLBound(lowerBoundArry);
          qmsSellRatesDob.setUBound(upperBoundArry);
          qmsSellRatesDob.setInvalidate(rs.getString("INVALIDATE"));
          qmsSellRatesDob.setTerminalId(rs.getString("TERMINALID"));
          qmsSellRatesDob.setNotes(rs.getString("NOTES")); 
           //Modified by Mohan for Issue No.219976 on 08-10-2010
          qmsSellRatesDob.setExtNotes(rs.getString("EXTERNAL_NOTES"));
          qmsSellRatesDob.setEffectiveFrom(rs.getTimestamp("EFROM"));
          qmsSellRatesDob.setValidUpto(rs.getTimestamp("VALIDUPTO"));
           qmsSellRatesDob.setCurrencyId(rs.getString("CURRENCY"));
          
          dataList.add(qmsSellRatesDob);
          icount++;
          
        }
          finalList.add(dataList);
          /*finalList.add(new Integer(csmt.getInt(13)));
          finalList.add(new Integer(csmt.getInt(14)));*/
          
       //modified by phani sekhar for 171213 
          if("buyratesexpiry".equalsIgnoreCase(operation))  {
        	  finalList.add(new Integer(csmt.getInt(18)));
              finalList.add(new Integer(csmt.getInt(19)));
        	  
          }else{
          finalList.add(new Integer(csmt.getInt(16)));
          finalList.add(new Integer(csmt.getInt(17)));
          }
          //ends 171213
      }catch(SQLException se)
      {
        se.printStackTrace();
        //Logger.error(FILE_NAME,"error at getRateDetails()"+se.toString());
        logger.error(FILE_NAME+"error at getRateDetails()"+se.toString());
        throw new SQLException(se.toString());
      }catch(Exception e)
      {
        e.printStackTrace();
        //Logger.error(FILE_NAME,"error at getRateDetails()"+e.toString());
        logger.error(FILE_NAME+"error at getRateDetails()"+e.toString());
        throw new Exception(e.toString());
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
          if(srRs != null)
          srRs.close();
          if(srchargeStmt != null)
		    srchargeStmt.close();
          ConnectionUtil.closePreparedStatement(pstmt1,rs1);// Added by Dilip for PMD Correction on 22/09/2015
         if(srConnection != null)
          srConnection.close();
            
        }catch(SQLException e)
        {
          e.printStackTrace();
          //Logger.error(FILE_NAME,"error at getRateDetails()"+e.toString());
          logger.error(FILE_NAME+"error at getRateDetails()"+e.toString());
          throw new SQLException();         
        }
      }
    // return dataList;
    return finalList;
  } 
 
 
 
 public HashMap getFlatRatesVales(QMSSellRatesDOB sellRatesDob,ESupplyGlobalParameters loginBean,String operation)throws SQLException
  {
  
      Connection            connection		    =	  null;
      PreparedStatement     pStmt		 		      =   null;
      ResultSet    					rs            	  = 	null;
      PreparedStatement     pStmt1		 		    =   null;
      ResultSet    					rs1            	  = 	null;
      QMSSellRatesDOB       qmsSellRatesDob   =   null;
      ArrayList             flatValues        =   null;
      String                key               =   null;
      String                sqlQueryFlat      =   null;
      
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
      String                invalidate        =   null;
      String                accessLevel       =   null;
      OperationsImpl        impl              =   null;
       String               currencyQry       =   "";                                  
      try
      {

	   accessLevel        =	 loginBean.getUserLevel();
      
      
      if("OPER_TERMINAL".equalsIgnoreCase(accessLevel))
      {
       accessLevel= " AND QBM.ACCESSLEVEL= 'O'";
      }else if("HO_TERMINAL".equalsIgnoreCase(accessLevel))
      {
		  accessLevel= " AND QBM.ACCESSLEVEL= 'H'";

      }else if("ADMN_TERMINAL".equalsIgnoreCase(accessLevel))
      {
  		  accessLevel= " AND QBM.ACCESSLEVEL= 'A'";
          
      }
              impl            =   new OperationsImpl();
				if(!"DownLoad".equalsIgnoreCase(operation))
        {
          currencyQry = "AND CURRENCY ='"+sellRatesDob.getCurrencyId()+"'";
        }

			  if(!"InValide".equalsIgnoreCase(operation))
			  {
				invalidate =" AND INVALIDATE   IS NULL  ";
			  }else
			  {
				invalidate =" ";
			  }

          connection	        =	  impl.getConnection();
          orignStr            =   sellRatesDob.getOrigin();
          destinationStr      =   sellRatesDob.getDestination();
          serviceStr          =   sellRatesDob.getServiceLevel();
          carrierStr          =   sellRatesDob.getCarrier_id();
          orign               =   orignStr.replaceAll(",","','");
          destination         =   destinationStr.replaceAll(",","','");
          serviceLevel        =   serviceStr.replaceAll(",","','");
          carrier             =   carrierStr.replaceAll(",","','");          

          orignStr        =   " AND QBD.ORIGIN IN('"+orign+"') AND QBD.DESTINATION IN('"+destination+"') AND QBD.SERVICE_LEVEL IN('"+serviceLevel+"')";
          carrierStr      =   " AND QBM.CARRIER_ID IN('"+carrier+"')";
                   
          sqlQueryFlat             =  " SELECT QBD.BUYRATEID BUYRATEID,QBM.CARRIER_ID CARRIER_ID,QBD.ORIGIN ORIGIN,QBD.DESTINATION DESTINATION,QBD.SERVICE_LEVEL SERVICE_LEVEL,QBD.WEIGHT_BREAK_SLAB WEIGHT_BREAK_SLAB, "+
                                      " QBD.TRANSIT_TIME TRANSIT_TIME,QBD.FREQUENCY FREQUENCY,QBD.CHARGERATE CHARGERATE,QBD.LANE_NO LANE_NO,INVALIDATE,DENSITY_CODE FROM QMS_BUYRATES_DTL QBD,QMS_BUYRATES_MASTER QBM "+   
                                      " WHERE QBD.BUYRATEID=QBM.BUYRATEID "+orignStr+" "+currencyQry+" AND QBD.ACTIVEINACTIVE IS NULL     "+ invalidate+
                                      " AND QBM.SHIPMENT_MODE=? AND QBM.WEIGHT_BREAK=? AND QBM.RATE_TYPE=? "+carrierStr+" "+accessLevel+" ORDER BY BUYRATEID,LANE_NO";
          orgCountryQuery          =  " SELECT LOCATIONID,COUNTRYID FROM  FS_FR_LOCATIONMASTER WHERE LOCATIONID IN('"+orign+"')";
          destCountryQuery         =  " SELECT LOCATIONID,COUNTRYID FROM  FS_FR_LOCATIONMASTER WHERE LOCATIONID IN('"+destination+"') ";
          flatValues  =   new ArrayList();
          //connection  =   this.getConnection(); 

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
          pStmt       =   connection.prepareStatement(sqlQueryFlat);
          
          //pStmt.setString(1,sellRatesDob.getCurrencyId());
          pStmt.setString(1,sellRatesDob.getShipmentMode());
          pStmt.setString(2,sellRatesDob.getWeightBreak());
          pStmt.setString(3,sellRatesDob.getRateType());
          //pStmt.setString(5,loginBean.getTerminalId());
         
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
              
              qmsSellRatesDob.setLaneNumber(rs.getInt("LANE_NO"));
              qmsSellRatesDob.setInvalidate(rs.getString("INVALIDATE"));
              qmsSellRatesDob.setDensityRatio(rs.getString("DENSITY_CODE"));
              key=rs.getString("BUYRATEID")+rs.getString("LANE_NO");
              
              if(map.containsKey(key))
              {
                qmsSellRatesDob = (QMSSellRatesDOB)map.get(key);
				
                qmsSellRatesDob.setFlatRate(rs.getDouble("CHARGERATE"));
                map.put(key,qmsSellRatesDob);
              }
              else
              {
                qmsSellRatesDob.setMinimumRate(rs.getDouble("CHARGERATE"));
				
                map.put(key,qmsSellRatesDob);
              }             
          }
      }
      catch(SQLException sqle)
      {
          //Logger.error(FILE_NAME,"SQLEXception in getFlatRatesVales()-->"+sqle.toString());
          logger.error(FILE_NAME+"SQLEXception in getFlatRatesVales()-->"+sqle.toString());
          sqle.printStackTrace();
          throw new EJBException(sqle.toString());
      }
      catch(Exception e)
      {
        //Logger.error(FILE_NAME,"EXception in getFlatRatesVales()-->"+e.toString());
        logger.error(FILE_NAME+"EXception in getFlatRatesVales()-->"+e.toString());
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
              //Logger.error(FILE_NAME,"Exception caught :: finally :: getFlatRatesVales() " + ex.toString() );
              logger.error(FILE_NAME+"Exception caught :: finally :: getFlatRatesVales() " + ex.toString() );
          }
		  
		  }
      return map;
  }
 public ArrayList getSlabRatesVales(QMSSellRatesDOB sellRatesDob,ESupplyGlobalParameters loginBean,String operation)throws SQLException
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
      String                sqlQuerySlabWt    =   null;
      String                sqlQuerySlab      =   null;
      String                orgCountryQuery   =   null;
      String                destCountryQuery  =   null;
      Hashtable             originTable       =   new Hashtable();
      Hashtable             destTable         =   new Hashtable();
      HashMap               map               =   new HashMap();
      OperationsImpl        impl              = null;
      
      String                orignStr          =   null;
      String                orign             =   null;
      String                destination       =   null;
      String                carrier           =   null;
      String                serviceLevel      =   null;
      String                destinationStr    =   null;
      String                serviceStr        =   null;
      String                carrierStr        =   null;
      String                invalidate        =   null;
      String                accessLevel       =   null;
      String                currencyQry       =   "";         
      try
      {
          impl        = new OperationsImpl();

          connection	=	impl.getConnection();
          slabValues  =   new ArrayList();
          boundryList =   new ArrayList();

     accessLevel        =	 loginBean.getUserLevel();
      if("OPER_TERMINAL".equalsIgnoreCase(accessLevel))
      {
       accessLevel= " AND QBM.ACCESSLEVEL= 'O'";
      }else if("HO_TERMINAL".equalsIgnoreCase(accessLevel))
      {
		  accessLevel= " AND QBM.ACCESSLEVEL= 'H'";

      }else if("ADMN_TERMINAL".equalsIgnoreCase(accessLevel))
      {
  		  accessLevel= " AND QBM.ACCESSLEVEL= 'A'";
          
      }

		  	  if(!"InValide".equalsIgnoreCase(operation))
			  {
				invalidate =" AND INVALIDATE   IS NULL  ";
			  }else
			  {
				invalidate =" ";
			  }
        if(!"DownLoad".equalsIgnoreCase(operation))
        {
          currencyQry = "AND CURRENCY='"+sellRatesDob.getCurrencyId()+"'";
        }
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
          
          sqlQuerySlabWt           =    " SELECT QBD.WEIGHT_BREAK_SLAB WEIGHT_BREAK_SLAB FROM QMS_BUYRATES_DTL QBD,QMS_BUYRATES_MASTER QBM WHERE QBD.BUYRATEID=QBM.BUYRATEID "+orignStr+" AND  QBD.ACTIVEINACTIVE IS NULL "+  invalidate +"AND  LINE_NO>0 "+currencyQry+" AND QBM.SHIPMENT_MODE=? AND "+
                                        " QBM.WEIGHT_BREAK=? AND QBM.RATE_TYPE=? "+ carrierStr +" "+ accessLevel +" ORDER BY TO_NUMBER(WEIGHT_BREAK_SLAB)  ";
    
          sqlQuerySlab             =    " SELECT QBD.BUYRATEID BUYRATEID,QBM.CARRIER_ID CARRIER_ID,QBD.ORIGIN ORIGIN,QBD.DESTINATION DESTINATION,QBD.SERVICE_LEVEL SERVICE_LEVEL,QBD.WEIGHT_BREAK_SLAB WEIGHT_BREAK_SLAB, "+
                                        " QBD.TRANSIT_TIME TRANSIT_TIME,QBD.FREQUENCY FREQUENCY,QBD.CHARGERATE CHARGERATE,QBD.LANE_NO LANE_NO,QBD.LOWERBOUND LOWERBOUND,QBD.UPPERBOUND UPPERBOUND,INVALIDATE,CHARGERATE_INDICATOR,DENSITY_CODE  FROM QMS_BUYRATES_DTL QBD,QMS_BUYRATES_MASTER QBM "+   
                                        " WHERE QBD.BUYRATEID=QBM.BUYRATEID "+orignStr+" "+currencyQry+" AND QBM.SHIPMENT_MODE=? AND QBM.WEIGHT_BREAK=? AND QBM.RATE_TYPE=? "+carrierStr+" "+accessLevel+ "  "+ invalidate +"  AND QBD.ACTIVEINACTIVE IS NULL   ORDER BY BUYRATEID,LANE_NO";
                                        
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
          
          pStmt       =   connection.prepareStatement(sqlQuerySlabWt);
          
          //pStmt.setString(1,sellRatesDob.getCurrencyId());
          pStmt.setString(1,sellRatesDob.getShipmentMode());
          pStmt.setString(2,sellRatesDob.getWeightBreak());
          pStmt.setString(3,sellRatesDob.getRateType());
          //pStmt.setString(5,loginBean.getTerminalId());
          rs = pStmt.executeQuery();
          boundryList.add("MIN");
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
            
          pStmt       =   connection.prepareStatement(sqlQuerySlab);
      
         // pStmt.setString(1,sellRatesDob.getCurrencyId());
          pStmt.setString(1,sellRatesDob.getShipmentMode());
          pStmt.setString(2,sellRatesDob.getWeightBreak());
          pStmt.setString(3,sellRatesDob.getRateType());
          //pStmt.setString(5,loginBean.getTerminalId());
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
              qmsSellRatesDob.setInvalidate(rs.getString("INVALIDATE"));
              qmsSellRatesDob.setDensityRatio(rs.getString("DENSITY_CODE"));
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
                  boundryDOB.setChargerateIndicator(rs.getString("CHARGERATE_INDICATOR"));
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
          //Logger.error(FILE_NAME,"SQLEXception in getSlabRatesVales()-->"+sqle.toString());
          logger.error(FILE_NAME+"SQLEXception in getSlabRatesVales()-->"+sqle.toString());
          sqle.printStackTrace();
          throw new EJBException(sqle.toString());
      }
      catch(Exception e)
      {
        //Logger.error(FILE_NAME,"EXception in getSlabRatesVales()-->"+e.toString());
        logger.error(FILE_NAME+"EXception in getSlabRatesVales()-->"+e.toString());
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
              //Logger.error(FILE_NAME,"Exception caught :: finally :: getSlabRatesVales() " + ex.toString() );
              logger.error(FILE_NAME+"Exception caught :: finally :: getSlabRatesVales() " + ex.toString() );
          }
		  
		  }
      return slabValues;
  }

  public ArrayList getListRatesVales(QMSSellRatesDOB sellRatesDob,ESupplyGlobalParameters loginBean,String operation)throws SQLException
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
      OperationsImpl        impl              = null;
      
      String                orignStr          =   null;
      String                orign             =   null;
      String                destination       =   null;
      String                carrier           =   null;
      String                serviceLevel      =   null;
      String                destinationStr    =   null;
      String                serviceStr        =   null;
      String                carrierStr        =   null;        
      String                invalidate        =   null;        
      String                accessLevel        =   null;        
      String                currencyQry       =   "";
      try
      {
      
		
	  accessLevel        =	 loginBean.getUserLevel();

      if("OPER_TERMINAL".equalsIgnoreCase(accessLevel))
      {
       accessLevel= " AND QBM.ACCESSLEVEL= 'O'";
      }else if("HO_TERMINAL".equalsIgnoreCase(accessLevel))
      {
		  accessLevel= " AND QBM.ACCESSLEVEL= 'H'";

      }else if("ADMN_TERMINAL".equalsIgnoreCase(accessLevel))
      {
  		  accessLevel= " AND QBM.ACCESSLEVEL= 'A'";
          
      }


                  impl = new OperationsImpl();

          connection	=	impl.getConnection();
      
          slabValues  =   new ArrayList();
          boundryList =   new ArrayList();

			 if(!"InValide".equalsIgnoreCase(operation))
			  {
				invalidate =" AND INVALIDATE   IS NULL  ";
			  }else
			  {
				invalidate =" ";
			  }
        if(!"DownLoad".equalsIgnoreCase(operation))
        {
          currencyQry = "AND CURRENCY='"+sellRatesDob.getCurrencyId()+"'";
        }
          
                                               
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
          
          sqlQueryListWt           =    " SELECT QBD.WEIGHT_BREAK_SLAB WEIGHT_BREAK_SLAB FROM QMS_BUYRATES_DTL QBD,QMS_BUYRATES_MASTER QBM WHERE QBD.BUYRATEID=QBM.BUYRATEID "+orignStr+" AND  QBD.ACTIVEINACTIVE IS NULL "+ invalidate +"  "+currencyQry+" AND QBM.SHIPMENT_MODE=? AND "+
                                        " QBM.WEIGHT_BREAK=? AND QBM.RATE_TYPE=? "+carrierStr+" "+ accessLevel +" ORDER BY LINE_NO ";
    
          sqlQueryList             =    " SELECT QBD.BUYRATEID BUYRATEID,QBM.CARRIER_ID CARRIER_ID,QBD.ORIGIN ORIGIN,QBD.DESTINATION DESTINATION,QBD.SERVICE_LEVEL SERVICE_LEVEL,QBD.WEIGHT_BREAK_SLAB WEIGHT_BREAK_SLAB, "+
                                        " QBD.TRANSIT_TIME TRANSIT_TIME,QBD.FREQUENCY FREQUENCY,QBD.CHARGERATE CHARGERATE,QBD.LANE_NO LANE_NO,QBD.LOWERBOUND LOWERBOUND,QBD.UPPERBOUND UPPERBOUND,INVALIDATE,DENSITY_CODE FROM QMS_BUYRATES_DTL QBD,QMS_BUYRATES_MASTER QBM "+   
                                        " WHERE QBD.BUYRATEID=QBM.BUYRATEID "+orignStr+" "+currencyQry+" AND QBM.SHIPMENT_MODE=? AND QBM.WEIGHT_BREAK=? AND QBM.RATE_TYPE=? "+carrierStr+" " + accessLevel+  " AND QBD.ACTIVEINACTIVE IS NULL "+ invalidate +"  ORDER BY BUYRATEID,LANE_NO";
                                        
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
          
          //pStmt.setString(1,sellRatesDob.getCurrencyId());
          pStmt.setString(1,sellRatesDob.getShipmentMode());
          pStmt.setString(2,sellRatesDob.getWeightBreak());
          pStmt.setString(3,sellRatesDob.getRateType());
          //pStmt.setString(5,loginBean.getTerminalId());
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
      
         // pStmt.setString(1,sellRatesDob.getCurrencyId());
          pStmt.setString(1,sellRatesDob.getShipmentMode());
          pStmt.setString(2,sellRatesDob.getWeightBreak());
          pStmt.setString(3,sellRatesDob.getRateType());
          //pStmt.setString(5,loginBean.getTerminalId());
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
              qmsSellRatesDob.setInvalidate(rs.getString("INVALIDATE"));
              qmsSellRatesDob.setDensityRatio(rs.getString("DENSITY_CODE"));
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
          //Logger.error(FILE_NAME,"SQLEXception in getListRatesVales()-->"+sqle.toString());
          logger.error(FILE_NAME+"SQLEXception in getListRatesVales()-->"+sqle.toString());
          sqle.printStackTrace();
          throw new EJBException(sqle.toString());
      }
      catch(Exception e)
      {
        //Logger.error(FILE_NAME,"EXception in getListRatesVales()-->"+e.toString());
        logger.error(FILE_NAME+"EXception in getListRatesVales()-->"+e.toString());
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
              //Logger.error(FILE_NAME,"Exception caught :: finally :: getListRatesVales() " + ex.toString() );
              logger.error(FILE_NAME+"Exception caught :: finally :: getListRatesVales() " + ex.toString() );
          }
		  
		  }
      return slabValues;
  }
 
  public ArrayList getFCLRatesVales(QMSSellRatesDOB sellRatesDob,ESupplyGlobalParameters loginBean,String operation)throws SQLException
  {
      Connection            connection		    =	  null;
       OperationsImpl        impl              = null;
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
      String                accessLevel        =   null;      
      String                currencyQry       =   "";
      try
      {
      
      impl = new OperationsImpl();


	  	accessLevel        =	 loginBean.getUserLevel();

      if("OPER_TERMINAL".equalsIgnoreCase(accessLevel))
      {
       accessLevel= " AND QBM.ACCESSLEVEL= 'O'";
      }else if("HO_TERMINAL".equalsIgnoreCase(accessLevel))
      {
		  accessLevel= " AND QBM.ACCESSLEVEL= 'H'";

      }else if("ADMN_TERMINAL".equalsIgnoreCase(accessLevel))
      {
  		  accessLevel= " AND QBM.ACCESSLEVEL= 'A'";
          
      }
        if(!"DownLoad".equalsIgnoreCase(operation))
        {
          currencyQry = "AND CURRENCY='"+sellRatesDob.getCurrencyId()+"'";
        }	
          connection	=	impl.getConnection();
      
          slabValues  =   new ArrayList();
          boundryList =   new ArrayList();
          
                                               
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
          
          sqlQueryListWt           =    " SELECT QBD.WEIGHT_BREAK_SLAB WEIGHT_BREAK_SLAB FROM QMS_BUYRATES_DTL QBD,QMS_BUYRATES_MASTER QBM WHERE QBD.BUYRATEID=QBM.BUYRATEID "+orignStr+" "+currencyQry+" AND QBM.SHIPMENT_MODE=? AND "+
                                        " QBM.WEIGHT_BREAK=? AND QBM.RATE_TYPE=? "+carrierStr+" "+ accessLevel +" ORDER BY WEIGHT_BREAK_SLAB";
    
          sqlQueryList             =    " SELECT QBD.BUYRATEID BUYRATEID,QBM.CARRIER_ID CARRIER_ID,QBD.ORIGIN ORIGIN,QBD.DESTINATION DESTINATION,QBD.SERVICE_LEVEL SERVICE_LEVEL,QBD.WEIGHT_BREAK_SLAB WEIGHT_BREAK_SLAB, "+
                                        " QBD.TRANSIT_TIME TRANSIT_TIME,QBD.FREQUENCY FREQUENCY,QBD.CHARGERATE CHARGERATE,QBD.LANE_NO LANE_NO,QBD.LOWERBOUND LOWERBOUND,QBD.UPPERBOUND UPPERBOUND,INVALIDATE,DENSITY_CODE FROM QMS_BUYRATES_DTL QBD,QMS_BUYRATES_MASTER QBM "+   
                                        " WHERE QBD.BUYRATEID=QBM.BUYRATEID "+orignStr+" "+currencyQry+" AND QBM.SHIPMENT_MODE=? AND QBM.WEIGHT_BREAK=? AND QBM.RATE_TYPE=? "+carrierStr+" " + accessLevel+" ORDER BY BUYRATEID,LANE_NO";
                                        
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
          
         // pStmt.setString(1,sellRatesDob.getCurrencyId());
          pStmt.setString(1,sellRatesDob.getShipmentMode());
          pStmt.setString(2,sellRatesDob.getWeightBreak());
          pStmt.setString(3,sellRatesDob.getRateType());
          //pStmt.setString(5,loginBean.getTerminalId());
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
      
          //pStmt.setString(1,sellRatesDob.getCurrencyId());
          pStmt.setString(1,sellRatesDob.getShipmentMode());
          pStmt.setString(2,sellRatesDob.getWeightBreak());
          pStmt.setString(3,sellRatesDob.getRateType());
          //pStmt.setString(5,loginBean.getTerminalId());
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
              qmsSellRatesDob.setDensityRatio(rs.getString("DENSITY_CODE"));
              qmsSellRatesDob.setLanNumber(rs.getInt("LANE_NO"));
              qmsSellRatesDob.setInvalidate(rs.getString("INVALIDATE"));
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
  public StringBuffer isExetIds(QMSSellRatesDOB sellDob)throws SQLException
{
      PreparedStatement     pStmt		 		      =   null;
      ResultSet    					rs            	  = 	null;
      Connection            connection        =   null;    
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
      OperationsImpl        impl              =   null;
      String                currValues        =   null;
      boolean               oFlag             =   false;
      String                currency          =   null;
      String originQuery           = " SELECT LOCATIONID FROM  FS_FR_LOCATIONMASTER WHERE LOCATIONID=? ";
      String carrierQuery          = " SELECT CARRIERID FROM FS_FR_CAMASTER WHERE CARRIERID=?";
      String serviceQuery          = " SELECT SERVICELEVELID FROM FS_FR_SERVICELEVELMASTER WHERE SERVICELEVELID=?";
      String currencyQuery         = " SELECT CURRENCYID FROM	FS_COUNTRYMASTER WHERE CURRENCYID=?";  
      String portmaterQuery        = " SELECT PORTID FROM FS_FRS_PORTMASTER WHERE PORTID=? ";
        String regionsQuery 			= "SELECT * FROM FS_COUNTRYMASTER CM WHERE CM.REGION=? ";//added by phani sekhar for wpbn 179351
      try
      {
         
          errorMassege  = new StringBuffer();
          impl = new OperationsImpl();
          connection	=	impl.getConnection();
          
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
        // added by phani sekhar for wpbn 179351 on 20090813
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
        //ends 179351
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
 
/* public StringBuffer isExetIds(ArrayList commenList)throws SQLException
 {
 
      OperationsImpl        impl              = null;
      Connection            connection		    =	  null;
      PreparedStatement     pStmt		 		      =   null;
      ResultSet    					rs            	  = 	null;
          
      StringBuffer          errorMassege      =   null;
      String                message           =   ""; 
      
      String                orgValues         =   null;
      String                destValues        =   null;
      String                carrValues        =   null;
      String                serValues         =   null;
      String                currValues        =   null;
      boolean               oFlag             =   false;
      
      ArrayList             originIds         =   null;
      ArrayList             destIds           =   null;
      ArrayList             carrierIds        =   null;
      ArrayList             serviceIds        =   null;
      String                currency          =   null;
      try
      {
      String originQuery           = " SELECT LOCATIONID FROM  FS_FR_LOCATIONMASTER WHERE LOCATIONID=? ";
      String carrierQuery          = " SELECT CARRIERID FROM FS_FR_CAMASTER WHERE CARRIERID=?";
      String serviceQuery          = " SELECT SERVICELEVELID FROM FS_FR_SERVICELEVELMASTER WHERE SERVICELEVELID=?";
      String currencyQuery         = " SELECT CURRENCYID FROM	FS_COUNTRYMASTER WHERE CURRENCYID=?";  
      
      
         impl = new OperationsImpl();

          connection	=	impl.getConnection();
          originIds   = (ArrayList)commenList.get(0);
          destIds     = (ArrayList)commenList.get(1);
          carrierIds  = (ArrayList)commenList.get(2);
          serviceIds  = (ArrayList)commenList.get(3);
          currency    = (String)commenList.get(4);
                    
          //connection  =   this.

          pStmt       =   connection.prepareStatement(originQuery);
          errorMassege  = new StringBuffer();
          int originIdsSize = originIds.size();
          
          for(int i=0;i<originIdsSize;i++)
          {
            orgValues = (String)originIds.get(i);
            
            pStmt.setString(1,orgValues);
            rs = pStmt.executeQuery();
            if(!rs.next())
            {
              message += orgValues+" ";
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
          
          pStmt       =   connection.prepareStatement(originQuery);
          
          oFlag = false;
          message = "";
           int destIdsSize = destIds.size();
          
          for(int i=0;i<destIdsSize;i++)
          {
            destValues = (String)destIds.get(i);
            
            pStmt.setString(1,destValues);
            rs = pStmt.executeQuery();
            if(!rs.next())
            {
              message += destValues+" ";
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
          
          pStmt       =   connection.prepareStatement(carrierQuery);
          message = ""; 
          oFlag = false;
           int carrierIdsSize = carrierIds.size();
          
          for(int i=0;i<carrierIdsSize;i++)
          {
            carrValues = (String)carrierIds.get(i);
            
            pStmt.setString(1,carrValues);
            rs = pStmt.executeQuery();
            if(!rs.next())
            {
              message += carrValues+" ";
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
          
          pStmt       =   connection.prepareStatement(serviceQuery);
          message = "";
          oFlag = false;
          int serviceIdsSize = serviceIds.size();
          
          for(int i=0;i<serviceIdsSize;i++)
          {
            serValues = (String)serviceIds.get(i);
            
            pStmt.setString(1,serValues);
            rs = pStmt.executeQuery();
            if(!rs.next())
            {
              message += serValues+" ";
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
          
          pStmt       =   connection.prepareStatement(currencyQuery);
          message = ""; 
          oFlag = false;
          pStmt.setString(1,currency);
          rs = pStmt.executeQuery();
          if(!rs.next())
          {
            message += currency;
            oFlag  = true;
          }
          if(oFlag || message!="")
            errorMassege.append(message+" :Currency Id is not valid .");
          else
            errorMassege.append(message);
      }
      catch(SQLException sqle)
      {
          Logger.error(FILE_NAME,"SQLEXception in isExetIds()-->"+sqle.toString());
          sqle.printStackTrace();
          throw new EJBException(sqle.toString());
      }
      catch(Exception e)
      {
        Logger.error(FILE_NAME,"EXception in isExetIds()-->"+e.toString());
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
              Logger.error(FILE_NAME,"Exception caught :: finally :: isExetIds() " + ex.toString() );
          }
		  
		  }
      return errorMassege;
 }*/
 public void invalidateBuyRateDtls(ArrayList list)throws Exception
	{
	  OperationsImpl        impl              =		null;
      Connection            connection		  =		null;
      PreparedStatement     pStmt		 	  =		null;
      PreparedStatement     pStmt1		 	  =		null;
      PreparedStatement     pStmt2		 	  =		null;
     // ResultSet				rs				  = 	null;//Commented By RajKumari on 27-10-2008 for Connection Leakages.
	  String				sql_query		  =		null;	
    String				sql_query_rsr		  =		null;	
    String				sql_query_rsr_acc		  =		null;	
	  FlatRatesDOB			flatRatesDOB	  =		null;  	



	  try{
      
       sql_query          =	"UPDATE QMS_BUYRATES_DTL SET INVALIDATE =? WHERE BUYRATEID =? AND  LANE_NO =?";	
	     sql_query_rsr      =	"UPDATE QMS_REC_CON_SELLRATESDTL SET INVALIDATE =? WHERE BUYRATEID =? AND  LANE_NO =?";	
	     sql_query_rsr_acc  =	"UPDATE QMS_REC_CON_SELLRATESDTL_ACC SET INVALIDATE =? WHERE BUYRATEID =? AND  LANE_NO =?";	
	     impl		=	new OperationsImpl();
			 connection	=	impl.getConnection();
             pStmt      = connection.prepareStatement(sql_query);
            pStmt1      = connection.prepareStatement(sql_query_rsr);
            pStmt2      = connection.prepareStatement(sql_query_rsr_acc);
            int listSize	=	list.size();
			  for(int i=0;i<listSize;i++)
              {
                    pStmt.clearParameters() ;
                    pStmt1.clearParameters() ;
                    pStmt2.clearParameters() ;
                 flatRatesDOB =  (FlatRatesDOB)list.get(i);	
                 
                 if(!"T".equals(flatRatesDOB.getInvalidate()))
                 {
                   pStmt.setNull(1,Types.VARCHAR);
                  // pStmt1.setNull(1,Types.VARCHAR);
                   pStmt1.setString(1, "F"); // Added by Kishore for BR - RSR Invalidate - Validate Scenario
                   pStmt2.setNull(1,Types.VARCHAR);
                 }else
                 {
                    pStmt.setString(1,flatRatesDOB.getInvalidate()); 
                    pStmt1.setString(1,flatRatesDOB.getInvalidate()); 
                    pStmt2.setString(1,flatRatesDOB.getInvalidate()); 
                 }
                 pStmt.setString(2,	flatRatesDOB.getBuyrateId());
                 pStmt.setInt	  (3,	flatRatesDOB.getLaneNo());	
                  pStmt.addBatch();
		             pStmt1.setString(2,	flatRatesDOB.getBuyrateId());
                 pStmt1.setInt	  (3,	flatRatesDOB.getLaneNo());	
                  pStmt1.addBatch();
                   pStmt2.setString(2,	flatRatesDOB.getBuyrateId());
                   pStmt2.setInt	  (3,	flatRatesDOB.getLaneNo());	
                  pStmt2.addBatch();
    	  }
			  pStmt.executeBatch();
       pStmt1.executeBatch();
       pStmt2.executeBatch();

	  }catch(SQLException e)
		{
			  e.printStackTrace();
						throw new Exception ("Problem while updating the Details <BR>");
		}catch(Exception e)
		{
			 e.printStackTrace();

		}finally{

			try{
			 if(pStmt!=null)
				 pStmt.close();
       if(pStmt1!=null)
				 pStmt1.close();
          if(pStmt2!=null)
				 pStmt2.close();
         if(connection!=null)
					connection.close();



			}catch(SQLException e)
			{
				      throw new Exception ("Problem while UPDATING the Details <BR>");
			}catch(Exception e)
			{
				      throw new Exception ("Problem while UPDATING the Details <BR>");

			}

		}

	}
public void  modifyFlatRates(ArrayList list,ESupplyGlobalParameters loginBean)throws Exception
	{
 
    OperationsImpl        impl				         =		null;
    Connection            connection	         =		null;
    PreparedStatement     pStmt			           =		null;
    PreparedStatement     pStmt1		           =		null;
    PreparedStatement     pStmt2		           =		null;
    PreparedStatement     pStmt3		           =		null;
   // PreparedStatement     pStmt4               =    null;Commented by govind on 16-02-2010 for Connection leakages
  //  PreparedStatement     pStmt5               =    null;Commented by govind on 16-02-2010 for Connection leakages
//    PreparedStatement     pStmt6               =    null;Commented by govind on 16-02-2010 for Connection leakages
    PreparedStatement     pStmtFreq            =    null;
    PreparedStatement     pStmtQuote           =    null;// added by VLAKSHMI for issue 146968 on 10/12/2008
    PreparedStatement     pstmtSelectQuoteId   =    null;// added by VLAKSHMI for issue 146968 on 10/12/2008
    PreparedStatement     pstmtQmsQuoteupdate  =    null;// added by VLAKSHMI for issue 146968 on 10/12/2008
    PreparedStatement     pstmtQmsQuoteInsert =    null;// added by VLAKSHMI for issue 146968 on 10/12/2008
    PreparedStatement     pStmtVersion         =    null;// added by VLAKSHMI for issue 146968 on 10/12/2008
 //   PreparedStatement     pStmtQuoteBreak         =    null;// added by VLAKSHMI for issue 146968 on 10/12/2008
   PreparedStatement     pStmtCount         =    null;
    ResultSet				rs					          = 	null;
    ResultSet				rsQuote					      = 	null;// added by VLAKSHMI for issue 146968 on 10/12/2008
    ResultSet				rsVersion				  = 	null;// added by VLAKSHMI for issue 146968 on 10/12/2008
    ResultSet				rsSelectQuoteId			  = 	null;// added by VLAKSHMI for issue 146968 on 10/12/2008
  //ResultSet				rsQmsQuoteupdate		  = 	null;// added by VLAKSHMI for issue 146968 on 10/12/2008//Commented by govind on 16-02-2010 for Connection Leakage
  //ResultSet				rsQmsQuoteInsert		  = 	null;// added by VLAKSHMI for issue 146968 on 10/12/2008//Commented by govind on 16-02-2010 for Connection Leakage
  //  ResultSet			  rsQuoteBreak		     = 	null;// added by VLAKSHMI for issue 146968 on 10/12/2008//Commented by govind on 16-02-2010 for Connection Leakage
	   ResultSet			rsCount     		     = 	null;
    String				sql_query			      =		null;	
	  String				update_qry			    =		null;
    String        updateSellRatesQry  =   "";
    String        update_csr_qry      =   "";
	  String				sql_query1			    =		null;	
	  String				seq_qry				      =		null;	
	  String				master_qry			    =		null;	
	  String				dtl_qry				      =		null;
    String        freq_qry            =   null;
	  String				log_qry				      =		null;	
	  FlatRatesDOB	flatRatesDOB		    =		null;  
	  String				shipmentmode		    =		null;
	  String				currency			      =		null;
	  String				uom					        =		null;
	  String				weightbreak			    =		null;
	  String				weightclass			    =		null;
	  String				ratetype			      =		null;
	  String				consoletype			    =		null;
	  String				accesslevel			    =		null;
	  String				origin				      =		null;
	  String				destination			    =		null;
	  String				servicelevel		    =		null;
	  String				frequency			      =		null;
	  String				transit_time		    =		null;
	  String				weightbreakslab		  =		null;
	  int					  lowerbound			    =		0;
	  int					  upperbound			    =		0;		
	  String				chargerateIndicator	=		null;
	  String				notes				        =		null;
	  String				chargeslab			    =		null;
	  String				carrierID			      =		null;
	  String				containertype		    =		null;
	  String				buyRateID			      =		null;
	  String				accessLevel			    =		null;
	  Timestamp				effectiveFrom		  =		null;
	  Timestamp				validUpto			    =		null;
    boolean       flagDtl             =   false;
    CallableStatement cStmt           =   null;
     CallableStatement cStmt1           =   null;
    String        genaratedFlag       =   null;
    String        densityRatio        =   null;//added by rk
    String        shipmodeStr         =   null;
    String        terminalId          =   null;
    String        createdBy           =   null;
    String[]      freqArray           =   null;
    
    String        rateDescription     =   null;//@@Added by Kameswari for the Surcharge Enhancement
    String        updateRsrAccQry     =   null;
    String        quote_qry           =   null;// added by VLAKSHMI for issue 146968 on 10/12/2008
    String        selectQuoteId       =   null;// added by VLAKSHMI for issue 146968 on 10/12/2008
    String        qmsQuoteupdate      =   null;// added by VLAKSHMI for issue 146968 on 10/12/2008
   String         qmsQuoteInsert      =   null;// added by VLAKSHMI for issue 146968 on 10/12/2008
   String        selectQuoteIdFlag    =   null;// added by VLAKSHMI for issue 146968 on 10/12/2008
   String        selectMaxVersionNo   =   null;// added by VLAKSHMI for issue 146968 on 10/12/2008
   String        service_level        =   null; // Added by Kameswari for the WPBN issue-146448 on 29/12/08
    int           charge              =    0;
    boolean        falgQuoteUpdate    = false;
    int prevoiusQuotid=0;// added by VLAKSHMI for issue 146968 on 10/12/2008
    int old_versionNo=0;// added by VLAKSHMI for issue 146968 on 10/12/2008
     int new_versionNo=0;// added by VLAKSHMI for issue 146968 on 10/12/2008
    //int count=0;
    String count_qry =null;// added by VLAKSHMI for issue 146968 on 10/12/2008
   // int qoutCount=0;
    String quoteId =null;// added by VLAKSHMI for issue 146968 on 10/12/2008
    String quote_Break_qry=null;// added by VLAKSHMI for issue 146968 on 10/12/2008
	  //HashMap   quoteslablist  = new HashMap();
    String  updatequote_qry = null;
    String   selectquote_qry = null;
    int   lane_no = 0;
    String countqry =null;
    ArrayList   tempbuyrateid  = new ArrayList();
    int        buyratesize     = 0;
    int        temp1           = 0;
    try{
    
			 /*sql_query  =	" SELECT  BUYRATEID,SHIPMENT_MODE ,CURRENCY,CARRIER_ID,UOM ,WEIGHT_BREAK,WEIGHT_CLASS,RATE_TYPE,CONSOLE_TYPE,CREATED_BY , "
							+" LAST_UPDATED_BY,LAST_UPDATED_TSTMP ,DEL_FLAG ,ACCESSLEVEL,TERMINALID FROM QMS_BUYRATES_MASTER WHERE BUYRATEID =? ";	
*/
      sql_query  =	" SELECT  BUYRATEID,SHIPMENT_MODE ,CURRENCY,UOM ,WEIGHT_BREAK,WEIGHT_CLASS,RATE_TYPE,CONSOLE_TYPE,CREATED_BY , "
                  +" LAST_UPDATED_BY,LAST_UPDATED_TSTMP ,DEL_FLAG ,ACCESSLEVEL,TERMINALID FROM QMS_BUYRATES_MASTER WHERE BUYRATEID =? ";	
              
			seq_qry     = " SELECT BUYRATE_SEQ.NEXTVAL BUYRATEID FROM DUAL  "; 

			/*update_qry      =  "   UPDATE   QMS_BUYRATES_DTL  SET ACTIVEINACTIVE ='I' WHERE BUYRATEID=? AND LANE_NO=?       "	;*/
      update_qry      =  "   UPDATE   QMS_BUYRATES_DTL  SET ACTIVEINACTIVE ='I' WHERE BUYRATEID=? AND LANE_NO=?  and  VERSION_NO=? "	;
  //@@Added by Kameswari for the WPBN issue-122521
     updateRsrAccQry   = "UPDATE QMS_REC_CON_SELLRATESDTL_ACC SET AI_FLAG='I' WHERE BUYRATEID=? AND LANE_NO = ? AND VERSION_NO=? ";
  
   updateSellRatesQry = "UPDATE QMS_REC_CON_SELLRATESDTL SET ACCEPTANCE_FLAG='Y' WHERE BUYRATEID=? AND LANE_NO =?  AND VERSION_NO=?"+ 
                          " AND AI_FLAG='A' "; 
      
     update_csr_qry  = " UPDATE QMS_REC_CON_SELLRATESDTL DTL SET DTL.AI_FLAG='I' WHERE DTL.BUYRATEID=? AND DTL.LANE_NO =? "+ 
                        " AND DTL.AI_FLAG='A' AND VERSION_NO=? AND REC_CON_ID IN (SELECT REC_CON_ID FROM QMS_REC_CON_SELLRATESMASTER MAS WHERE "+
                        " MAS.REC_CON_ID=DTL.REC_CON_ID AND MAS.RC_FLAG='C')";
			
		/*	master_qry =  " INSERT INTO QMS_BUYRATES_MASTER (SHIPMENT_MODE,  CURRENCY, UOM, WEIGHT_BREAK,  "+
                    " RATE_TYPE, CONSOLE_TYPE, CREATED_BY,LAST_UPDATED_BY, LAST_UPDATED_TSTMP, BUYRATEID, WEIGHT_CLASS, ACCESSLEVEL, TERMINALID  )"+
                    " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?) ";*/
  /* master_qry =  " INSERT INTO QMS_BUYRATES_MASTER (SHIPMENT_MODE,  CURRENCY, UOM, WEIGHT_BREAK,  "+
                    " RATE_TYPE, CONSOLE_TYPE, CREATED_BY,LAST_UPDATED_BY, LAST_UPDATED_TSTMP, BUYRATEID, WEIGHT_CLASS, ACCESSLEVEL, TERMINALID  )"+
                    " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?) ";*/ //commented  VLAKSHMI for issue 146968 on 10/12/2008
     master_qry =  " INSERT INTO QMS_BUYRATES_MASTER (SHIPMENT_MODE,  CURRENCY, UOM, WEIGHT_BREAK,  "+
                    " RATE_TYPE, CONSOLE_TYPE, CREATED_BY,LAST_UPDATED_BY, LAST_UPDATED_TSTMP, BUYRATEID, WEIGHT_CLASS, ACCESSLEVEL, TERMINALID,VERSION_NO,LANE_NO)"+
                    " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ";
		                 
						  
			/*dtl_qry    = " INSERT INTO QMS_BUYRATES_DTL (BUYRATEID ,ORIGIN, DESTINATION, SERVICE_LEVEL,  "
                   +"  FREQUENCY, TRANSIT_TIME, CHARGERATE, WEIGHT_BREAK_SLAB, LANE_NO, LOWERBOUND ,UPPERBOUND  ,"
                  +"   NOTES, LINE_NO,CARRIER_ID,CHARGERATE_INDICATOR,CREATEDTIME ,OVER_PIVOT,EFFECTIVE_FROM, VALID_UPTO,ID,GENERATED_FLAG,DENSITY_CODE,RATE_DESCRIPTION) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,seq_BUYRATES_DTL.nextval,?,?,?) ";*///commented by VLAKSHMI for issue 146968 on 10/12/2008
     dtl_qry    = " INSERT INTO QMS_BUYRATES_DTL (BUYRATEID ,ORIGIN, DESTINATION, SERVICE_LEVEL,  "
                   +"  FREQUENCY, TRANSIT_TIME, CHARGERATE, WEIGHT_BREAK_SLAB, LANE_NO, LOWERBOUND ,UPPERBOUND  ,"
                   //Modified by Mohan for Issue No.219976 on 08-10-2010
                  +"   NOTES, LINE_NO,CARRIER_ID,CHARGERATE_INDICATOR,CREATEDTIME ,OVER_PIVOT,EFFECTIVE_FROM, VALID_UPTO,ID,GENERATED_FLAG,DENSITY_CODE,RATE_DESCRIPTION ,VERSION_NO,EXTERNAL_NOTES,SUR_CHARGE_CURRENCY,SURCHARGE_ID)"
                  +"VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,seq_BUYRATES_DTL.nextval,?,?,?,?,?,?,?) "; // Added By Kishore to Update SurCharge Id, SurCharge Currency For Buy rates updates
        
     // freq_qry   =  " INSERT INTO QMS_BUYRATES_FREQ VALUES (?,?,?) ";  
     freq_qry   =  " INSERT INTO QMS_BUYRATES_FREQ VALUES (?,?,?,?) ";    // added by VLAKSHMI for issue 146968 on 10/12/2008
						  
			log_qry     =" INSERT INTO FS_USERLOG (LOCATIONID ,USERID ,DOCTYPE ,DOCREFNO ,DOCDATE ,TRANSACTIONTYPE ) VALUES(?,?,?,?,?,?)    "          ;
// added by VLAKSHMI for issue 146968 on 10/12/2008
			/*quote_qry="Select Distinct (Qr.Break_Point) BREAK_POINT ,(qr.quote_id)QUOTEID"
                   +" From Qms_Quote_Rates Qr, Qms_Quote_Master Qm Where Qr.Sell_Buy_Flag = ?"
                    +"  And   Nvl(Qr.Margin_Discount_Flag, 'M') = 'M' And Qr.Buyrate_Id = ?"
                   +" And Qr.Rate_Lane_No = ? And Qr.Quote_Id = Qm.Id"
                    +" And Qm.Id = (Select Id From Qms_Quote_Master Where Quote_Id = Qm.Quote_Id And Version_No In"
                                        +" (Select Max(Version_No) From Qms_Quote_Master Where Id = Qr.Quote_Id))"
                     +"And Qm.Complete_Flag = 'C' And (Qm.Active_Flag = 'A' Or Qm.Active_Flag Is Null) order by QUOTEID";*/
    // added by VLAKSHMI for issue 146968 on 10/12/2008
     
     quote_qry="SELECT  QR.QUOTE_ID QUOTEID"
                   +" From Qms_Quote_Rates Qr,QMS_QUOTE_MASTER QM WHERE QM.ACTIVE_FLAG='A' AND QM.ID=QR.QUOTE_ID AND Qr.Sell_Buy_Flag ='BR' And Qr.Buyrate_Id = ?"
                   
                   +" And Qr.Rate_Lane_No = ? AND QR.BREAK_POINT =?  AND QR.BUY_RATE NOT IN (?)";
       
    countqry = "SELECT COUNT(*) FROM QMS_QUOTES_UPDATED WHERE QUOTE_ID =? AND SELL_BUY_FLAG='BR' AND CONFIRM_FLAG IS NULL";
    selectquote_qry  = "SELECT QUOTE_ID,CHANGEDESC FROM QMS_QUOTES_UPDATED WHERE QUOTE_ID IN (SELECT QR.QUOTE_ID FROM QMS_QUOTE_RATES QR,QMS_QUOTE_MASTER QM WHERE QM.ACTIVE_FLAG='A' AND QM.ID=QR.QUOTE_ID AND SELL_BUY_FLAG='BR' AND BUYRATE_ID=? AND RATE_LANE_NO=? AND BREAK_POINT =?  AND BUY_RATE NOT IN (?) ) AND CONFIRM_FLAG IS NULL";               
   
    updatequote_qry ="UPDATE QMS_QUOTES_UPDATED SET CHANGEDESC= ?,NEW_VERSION_NO=? WHERE QUOTE_ID=? AND SELL_BUY_FLAG='BR'";
     
     /*quote_Break_qry =   "Select Distinct (Qr.Break_Point) BREAK_POINT , QR.quote_id QUOTEID from  Qms_Quote_Rates Qr where Qr.QUOTE_ID=? and Qr.Sell_Buy_Flag = 'BR'  order by QUOTEID" ;  
     
      count_qry=" select Distinct count (*)COUNT from Qms_Quote_Rates qr, Qms_Quote_Master Qm"+
                          " Where Qr.Sell_Buy_Flag = 'BR'"+
                           " And Nvl(Qr.Margin_Discount_Flag, 'M') = 'M'"+
                            " And Qr.Buyrate_Id = ?"+
                                " And Qr.Rate_Lane_No = ?"+
                                 " And Qr.Quote_Id = Qm.Id"+
                                 " And Qm.Id = (Select Id From Qms_Quote_Master Where Quote_Id = Qm.Quote_Id"+
                   " And Version_No In (Select Max(Version_No)From Qms_Quote_Master Where Id = Qr.Quote_Id))"+
  " And Qm.Complete_Flag = 'C'And (Qm.Active_Flag = 'A' Or Qm.Active_Flag Is Null) and qr.line_no='0'"  ;  
 // added by VLAKSHMI for issue 146968 on 10/12/2008 
 selectQuoteId=" Select Qr.Quote_Id QUOTEID From Qms_Quotes_Updated Qr Where Qr.Sell_Buy_Flag = 'BR' And Qr.New_Buycharge_Id = ? "+
                   "  And Qr.New_Lane_No = ? "+
                   "  And Qr.Quote_Id =? "+
                    " And Confirm_Flag Is Null  order by QUOTEID " ;
// added by VLAKSHMI for issue 146968 on 10/12/2008                    
    qmsQuoteupdate= " Update Qms_Quotes_Updated Qu Set New_Buycharge_Id = ?, New_Lane_No = ?, Changedesc = ?,NEW_VERSION_NO=? "+
          " Where Qu.Quote_Id = ? "+
         "  And Qu.Sell_Buy_Flag = 'BR' "+
         "  And Qu.New_Buycharge_Id = ? "+
         "  And Qu.New_Lane_No = ? "+
         "  And Confirm_Flag Is Null ";
// added by VLAKSHMI for issue 146968 on 10/12/2008         
         selectQuoteIdFlag= " Select Qr.Quote_Id QUOTEID From Qms_Quotes_Updated Qr Where Qr.Sell_Buy_Flag = 'BR' And Qr.New_Buycharge_Id = ? "+
                   "  And Qr.New_Lane_No = ? "+
                    " And Confirm_Flag Is Null " ;*/
   // added by VLAKSHMI for issue 146968 on 10/12/2008      
   qmsQuoteInsert= " INSERT INTO QMS_QUOTES_UPDATED (QUOTE_ID, "+
           " NEW_BUYCHARGE_ID, NEW_LANE_NO, SELL_BUY_FLAG, CHANGEDESC, OLD_BUYCHARGE_ID, OLD_LANE_NO,OLD_VERSION_NO,NEW_VERSION_NO) "+
        " Values(?,?,?,?,?,?,?,?,?) ";
  // added by VLAKSHMI for issue 146968 on 10/12/2008
    selectMaxVersionNo= "select MAX(VERSION_NO)VERSION_NO from QMS_BUYRATES_DTL dtl where dtl.BUYRATEID=? AND dtl.LANE_NO=? AND ACTIVEINACTIVE IS NULL";
   // selectoldVersionNo= "select VERSION_NO VERSION_NO from QMS_BUYRATES_DTL dtl where dtl.BUYRATEID=? AND dtl.LANE_NO=? AND ACTIVEINACTIVE ='A'";
      impl		    =	new OperationsImpl();
      connection	=	impl.getConnection();
      pStmt       = connection.prepareStatement(seq_qry);

			 rs         = pStmt.executeQuery();
			           
      if(rs.next())
          buyRateID   = rs.getString("BUYRATEID");
        
		    if(rs!= null )
              rs.close();
          if(pStmt!= null )
              pStmt.close();
        cStmt1 = connection.prepareCall("{ call qms_buy_rates_pkg.sellratesmstr_acc_proc(?,?,?)}");
   	   pStmt           = connection.prepareStatement(sql_query);
		   pStmt1          = connection.prepareStatement(master_qry);
		   pStmt2          = connection.prepareStatement(dtl_qry);
       pStmtFreq       = connection.prepareStatement(freq_qry);
       pStmtVersion    = connection.prepareStatement(selectMaxVersionNo);// added by VLAKSHMI for issue 146968 on 10/12/2008
     //  pStmtQuoteBreak = connection.prepareStatement(quote_Break_qry);// added by VLAKSHMI for issue 146968 on 10/12/2008
      // pStmtCount=connection.prepareStatement(count_qry);
      // pstmtSelectQuoteId  = connection.prepareStatement(selectQuoteId);// added by VLAKSHMI for issue 146968 on 10/12/2008
      // pstmtQmsQuoteupdate = connection.prepareStatement(qmsQuoteupdate);// added by VLAKSHMI for issue 146968 on 10/12/2008
      // 
      flatRatesDOB = (FlatRatesDOB)list.get(0);  
		
		  pStmt.setString(1,flatRatesDOB.getBuyrateId());

		  rs= pStmt.executeQuery();

		 if(rs.next())
		  {
	        shipmentmode  =  rs.getString("SHIPMENT_MODE") ;
          currency	  =  rs.getString("CURRENCY") ;
          uom			  =  rs.getString("UOM") ;
          weightbreak	  =  rs.getString("WEIGHT_BREAK") ;
          weightclass	  =  rs.getString("WEIGHT_CLASS") ;
          ratetype	  =  rs.getString("RATE_TYPE") ;
          consoletype   =    rs.getString("CONSOLE_TYPE") ;
          accessLevel  =  rs.getString("ACCESSLEVEL");
          terminalId   =  rs.getString("TERMINALID");
          createdBy    =  rs.getString("CREATED_BY");
			//carrierID	  =  rs.getString("CARRIER_ID") ;
			

		  }
		    if(rs!= null )
              rs.close();
            if(pStmt!= null )
              pStmt.close();

			//pStmt      = connection.prepareStatement(update_qry);
     // pStmt6      =  connection.prepareStatement(updateRsrAccQry);//@@Added by Kameswari for the WPBN issue
      //pStmt4     = connection.prepareStatement(updateSellRatesQry);
     // pStmt5     = connection.prepareStatement(update_csr_qry);
    if("List".equalsIgnoreCase(weightbreak)&&!("1".equalsIgnoreCase(shipmentmode)))
    {
    /*	sql_query1 =  " SELECT  ORIGIN,DESTINATION,SERVICE_LEVEL,FREQUENCY,TRANSIT_TIME,WEIGHT_BREAK_SLAB,LOWERBOUND,UPPERBOUND,CHARGERATE_INDICATOR, "+  
                    " NOTES,CHARGESLAB,CARRIER_ID,CONTAINERTYPE,CREATEDTIME,ACTIVEINACTIVE,EFFECTIVE_FROM,VALID_UPTO,INVALIDATE,ACCESSLEVEL,CHARGERATE,GENERATED_FLAG ,DENSITY_CODE,RATE_DESCRIPTION,VERSION_NO VERSION_NO "+
			              " FROM QMS_BUYRATES_DTL  WHERE BUYRATEID=? AND LANE_NO=? ORDER BY RATE_DESCRIPTION,WEIGHT_BREAK_SLAB";*/
      sql_query1 =  " SELECT  LANE_NO ,ORIGIN,DESTINATION,SERVICE_LEVEL,FREQUENCY,TRANSIT_TIME,WEIGHT_BREAK_SLAB,LOWERBOUND,UPPERBOUND,CHARGERATE_INDICATOR, "+  
                    " NOTES,CHARGESLAB,CARRIER_ID,CONTAINERTYPE,CREATEDTIME,ACTIVEINACTIVE,EFFECTIVE_FROM,VALID_UPTO,INVALIDATE,ACCESSLEVEL,CHARGERATE,GENERATED_FLAG ,DENSITY_CODE,RATE_DESCRIPTION,VERSION_NO, SURCHARGE_ID  "+
			              " FROM QMS_BUYRATES_DTL  WHERE BUYRATEID=? AND LANE_NO=? AND  ACTIVEINACTIVE IS NULL ORDER BY RATE_DESCRIPTION,WEIGHT_BREAK_SLAB";
                                  
                   
	
    }
    else
    {
    	sql_query1 =  " SELECT  LANE_NO,ORIGIN,DESTINATION,SERVICE_LEVEL,FREQUENCY,TRANSIT_TIME,WEIGHT_BREAK_SLAB,LOWERBOUND,UPPERBOUND,CHARGERATE_INDICATOR, "+  
                    " NOTES,CHARGESLAB,CARRIER_ID,CONTAINERTYPE,CREATEDTIME,ACTIVEINACTIVE,EFFECTIVE_FROM,VALID_UPTO,INVALIDATE,ACCESSLEVEL,CHARGERATE,GENERATED_FLAG ,DENSITY_CODE,RATE_DESCRIPTION,VERSION_NO, SURCHARGE_ID  "+
			              " FROM QMS_BUYRATES_DTL  WHERE BUYRATEID=? AND LANE_NO=? AND  ACTIVEINACTIVE IS NULL ORDER BY RATE_DESCRIPTION,LINE_NO";
		
    
    }
    pStmt3      = connection.prepareStatement(sql_query1);
    // cStmt  = connection.prepareCall("{ call pkg_qms_buyrates.qms_update_new_quote(?,?,?,?,?,?,?,?,?,?,?)}");
     // added by VLAKSHMI for issue 146968 on 10/12/2008
             // end  for issue 146968 on 10/12/2008
	  /* if("OPER_TERMINAL".equalsIgnoreCase(loginBean.getUserLevel()))
      {
        accessLevel="O";
      }else if("HO_TERMINAL".equalsIgnoreCase(loginBean.getUserLevel()))
      {
          accessLevel ="H";
      }else if("ADMN_TERMINAL".equalsIgnoreCase(loginBean.getUserLevel()))
      {
          accessLevel ="A";
      }*/
                      
                      if ("1".equalsIgnoreCase(shipmentmode))
                      { shipmodeStr = "Air";}
                      else if("2".equalsIgnoreCase(shipmentmode))
                      { shipmodeStr = "Sea";}
                      else
                      { shipmodeStr = "Truck";}
                      
                      
                      
                     /* pStmt1.clearParameters();
                      pStmt1.setString(1,shipmentmode);
                      pStmt1.setString(2,currency);
                      //pStmt1.setString(3,carrierID);
                      pStmt1.setString(3,uom);
                      pStmt1.setString(4,weightbreak);
                      pStmt1.setString(5,ratetype);
                      pStmt1.setString(6,consoletype);
                      pStmt1.setString(7,createdBy);
                      pStmt1.setString(8,loginBean.getUserId());
                      pStmt1.setTimestamp(9,loginBean.getLocalTime());
                   // pStmt1.setString(10,buyRateID); commented by VLAKSHMI  for issue 146968 on 10/12/2008
                      pStmt1.setString(10,flatRatesDOB.getBuyrateId());
                      pStmt1.setString(11,weightclass); 
                      pStmt1.setString(12,accessLevel); 
                      pStmt1.setString(13,terminalId); 
                      pStmt1.setInt(14,new_versionNo+1); 
                      pStmt1.executeUpdate() ;
					             if(pStmt1!= null )
			                 pStmt1.close();*/

					 
				
					cStmt = connection.prepareCall("{ call QMS_QUOTEPACK_NEW.qms_quote_update(?,?,?,?,?,?,?,?,?,?,?,?)}");
					int listSize	=	list.size();
					pstmtQmsQuoteInsert = connection.prepareStatement(qmsQuoteInsert); // Added by Gowtham on 04Feb2011 for Loop Leakages.
					pstmtQmsQuoteupdate = connection.prepareStatement(updatequote_qry);//Added by Gowtham on 04Feb2011 for Loop Leakages.
					for(int i=0;i<listSize;i++)
					{
							flatRatesDOB = (FlatRatesDOB)list.get(i); 
                pStmtVersion.setInt(1,Integer.parseInt(flatRatesDOB.getBuyrateId()));
                pStmtVersion.setInt(2,flatRatesDOB.getLaneNo());
                rsVersion=pStmtVersion.executeQuery();
                if(rsVersion.next())
                {
                  new_versionNo=rsVersion.getInt("VERSION_NO");
                }
         //commented by phani for wpbn 176431  on 20090714
          /*        if(tempbuyrateid.size()==0)
                 {
                    pStmt1.clearParameters();
                      pStmt1.setString(1,shipmentmode);
                      pStmt1.setString(2,currency);
                      //pStmt1.setString(3,carrierID);
                      pStmt1.setString(3,uom);
                      pStmt1.setString(4,weightbreak);
                      pStmt1.setString(5,ratetype);
                      pStmt1.setString(6,consoletype);
                      pStmt1.setString(7,createdBy);
                      pStmt1.setString(8,loginBean.getUserId());
                      pStmt1.setTimestamp(9,loginBean.getLocalTime());
                   // pStmt1.setString(10,buyRateID); commented by VLAKSHMI  for issue 146968 on 10/12/2008
                      pStmt1.setString(10,flatRatesDOB.getBuyrateId());
                      pStmt1.setString(11,weightclass); 
                      pStmt1.setString(12,accessLevel); 
                      pStmt1.setString(13,terminalId); 
                      pStmt1.setInt(14,new_versionNo+1); 
                       pStmt1.setInt(15,flatRatesDOB.getLaneNo()); //@@Added by kameswari on 04/02/09
                      pStmt1.executeUpdate() ;
                     tempbuyrateid.add(flatRatesDOB.getBuyrateId());
                 }
                 
               else
               {
                 for( int z=0;z<tempbuyrateid.size();z++)
                 {
                   if(!tempbuyrateid.get(z).equals(flatRatesDOB.getBuyrateId()))
                   {
                        buyratesize++;
                   }
                 }
             
               if(buyratesize==tempbuyrateid.size())
               {*///ends 176431
                     pStmt1.clearParameters();
                      pStmt1.setString(1,shipmentmode);
                      pStmt1.setString(2,currency);
                      //pStmt1.setString(3,carrierID);
                      pStmt1.setString(3,uom);
                      pStmt1.setString(4,weightbreak);
                      pStmt1.setString(5,ratetype);
                      pStmt1.setString(6,consoletype);
                      pStmt1.setString(7,createdBy);
                      pStmt1.setString(8,loginBean.getUserId());
                      pStmt1.setTimestamp(9,loginBean.getLocalTime());
                   // pStmt1.setString(10,buyRateID); commented by VLAKSHMI  for issue 146968 on 10/12/2008
                      pStmt1.setString(10,flatRatesDOB.getBuyrateId());
                      pStmt1.setString(11,weightclass); 
                      pStmt1.setString(12,accessLevel); 
                      pStmt1.setString(13,terminalId); 
                      pStmt1.setInt(14,new_versionNo+1); 
                        pStmt1.setInt(15,flatRatesDOB.getLaneNo()); //@@Added by kameswari on 04/02/09
                      pStmt1.executeUpdate() ;
                 tempbuyrateid.add(flatRatesDOB.getBuyrateId());
                  //commented by phani sekhar for wpbn 176431 on 20090714
                 //  } 
                // } 
                
               
              
           

              
							pStmt3.setString(1,flatRatesDOB.getBuyrateId());
							pStmt3.setInt(2,flatRatesDOB.getLaneNo());
							rs= pStmt3.executeQuery();
             	int j=0;	
             	// Added by Gowtham for schCurrecny Updation
             	int schCount=0;
             	String tempRateDesc="A FREIGHT RATE";
             	int arrayCount=-1;
             	String surChargeId = "";
             // End Of Gowtham for schCurrecny Updation
             	pStmtCount = connection.prepareStatement(countqry); // Added by Gowtham on 04Feb2011 for Loop Leakages.
              while(rs.next())
              {
                prevoiusQuotid=0;
                lane_no        =	rs.getInt("LANE_NO");
								origin				 =	rs.getString("ORIGIN");
								destination			=	rs.getString("DESTINATION");	
								servicelevel		=	rs.getString("SERVICE_LEVEL");	
								frequency			  =	rs.getString("FREQUENCY");
								transit_time		=	rs.getString("TRANSIT_TIME");
								weightbreakslab		=	rs.getString("WEIGHT_BREAK_SLAB");
								lowerbound			=	rs.getInt	("LOWERBOUND");
								upperbound			=	rs.getInt	("UPPERBOUND");
								chargerateIndicator	=	rs.getString("CHARGERATE_INDICATOR");
								notes				     =	rs.getString("NOTES");
								chargeslab			 =	rs.getString("CHARGESLAB");
								carrierID			   =	rs.getString("CARRIER_ID");
								containertype		 =	rs.getString("CONTAINERTYPE");	
								effectiveFrom		 =	rs.getTimestamp("EFFECTIVE_FROM");
								validUpto			   =	rs.getTimestamp("VALID_UPTO");	
								genaratedFlag    = rs.getString("GENERATED_FLAG");
								densityRatio     = rs.getString("DENSITY_CODE");
                rateDescription  = rs.getString("RATE_DESCRIPTION");
                charge           = rs.getInt("CHARGERATE");
                old_versionNo    = rs.getInt("VERSION_NO");
                surChargeId		 = rs.getString("SURCHARGE_ID"); // Added by Gowtham for schCurrecny Updation			
              if(j==0)
              {
                 service_level = servicelevel;
              }   
                  // added by VLAKSHMI for issue 146968 on 12/12/2008
             
									pStmt2.clearParameters() ;
                  if(flatRatesDOB.getRates()[j]!=null&&!("".equalsIgnoreCase(flatRatesDOB.getRates()[j]))&&flatRatesDOB.getRates()[j].trim().length()>0)
                  { 
								 // pStmt2.setString		(1,	buyRateID);// commented by VALSKHMI for issue 146968 on 10/12/2008
                   pStmt2.setString		 (1,	flatRatesDOB.getBuyrateId());
								   pStmt2.setString		(2,	origin);
								   pStmt2.setString		(3,	destination);
                  if(rateDescription==null||"A FREIGHT RATE".equalsIgnoreCase(rateDescription)||rateDescription.trim().length()==0)
                    pStmt2.setString		(4,	servicelevel);
                  else
                    pStmt2.setString		(4,	"SCH");
								    pStmt2.setString		(5,	frequency);
								    pStmt2.setString		(6,	transit_time);
                 pStmt2.setDouble(7,Double.parseDouble(flatRatesDOB.getRates()[j]));
                 
								  pStmt2.setString		(8,weightbreakslab);
                   pStmt2.setInt(9,lane_no);//@@Modified by Kameswari for the WPBN issues-146448,146968 on 01/12/09
								 // pStmt2.setInt(9,i);
								  pStmt2.setInt(10,lowerbound);
								  pStmt2.setInt(11,upperbound);
								  pStmt2.setString(12,flatRatesDOB.getRemarks());
          
                   pStmt2.setInt(13,j);
                  
               
								  pStmt2.setString(14,carrierID);
       
							if("Both".equalsIgnoreCase(ratetype))	  
								  pStmt2.setString(15,flatRatesDOB.getSlabValues()[j]);
							else
                  pStmt2.setString(15,("-".equals(flatRatesDOB.getSlabValues()[j]))?"":("S".equals(flatRatesDOB.getSlabValues()[j])?"SLAB":"FLAT"));//Govind
								  pStmt2.setTimestamp(16,new java.sql.Timestamp((new java.util.Date()).getTime()));
								  pStmt2.setString(17,"");
								  pStmt2.setTimestamp(18,effectiveFrom);
								  pStmt2.setTimestamp(19,validUpto);
                  pStmt2.setString(20,genaratedFlag);
                  pStmt2.setString(21,densityRatio);//ADDED BY RK
								  pStmt2.setString(22,rateDescription);//@@Added by Kameswari for Surcharge Enhancement
                  pStmt2.setInt(23,new_versionNo+1);
            		  pStmt2.setString(24,flatRatesDOB.getExtNotes());//Added By Mohan For Issue No. on 29102010
            		// Added by Gowtham for schCurrecny Updation
            	
            	if(!tempRateDesc.equals(rateDescription)){
                    		tempRateDesc = rateDescription;
                    		arrayCount = arrayCount+1;
                    }            	 
            		  
                 if("A FREIGHT RATE".equalsIgnoreCase(rateDescription)){
            		  pStmt2.setString(25,"");
            	  }
               else{
                   String sch = ""; // Added By Kishore
            	   if(flatRatesDOB.getSchCurr()!= null)
            	   sch =(flatRatesDOB.getSchCurr()[arrayCount]== "-")? "" : flatRatesDOB.getSchCurr()[arrayCount];  
                   pStmt2.setString(25, sch);
            		  
            	  }
            	 
            	  
            	   //End of Gowtham for schCurrecny Updation
            	   
            	  //pStmt2.setString(25,flatRatesDOB.getSurchargeCurrency());// Added By Kishore to Update SurCharge Id, SurCharge Currency For Buy rates updates 
            	  pStmt2.setString(26,surChargeId);// Added By Kishore to Update SurCharge Id, SurCharge Currency For Buy rates updates
            		  
                  pStmt2.executeUpdate();
             
                 pStmtQuote      = connection.prepareStatement(quote_qry);// added by VLAKSHMI for issue 146968 on 10/12/2008
                // pstmtQmsQuoteInsert = connection.prepareStatement(qmsQuoteInsert);// added by VLAKSHMI for issue 146968 on 10/12/2008 Commented by Gowtham on 04Feb2011 for loop Leakages.
                 pstmtSelectQuoteId=connection.prepareStatement(selectquote_qry);
                // pstmtQmsQuoteupdate = connection.prepareStatement(updatequote_qry); Commented by Gowtham on 04Feb2011 for Loop Leakages.
                
                     if("2".equalsIgnoreCase(shipmentmode) && "FCL".equalsIgnoreCase(consoletype) && "LIST".equalsIgnoreCase(weightbreak)  )
                  {
                  
                  
                   pstmtSelectQuoteId.setString(1,flatRatesDOB.getBuyrateId());
                     pstmtSelectQuoteId.setInt(2,flatRatesDOB.getLaneNo());
                      pstmtSelectQuoteId.setString(3,weightbreakslab);
                     pstmtSelectQuoteId.setString(4,flatRatesDOB.getRates()[j]);
                     // pstmtSelectQuoteId.setNull(3,Types.INTEGER);
                      rsSelectQuoteId   = pstmtSelectQuoteId.executeQuery();
			              String changedesc = weightbreakslab+","+origin+"-"+destination+", Sea Freight Rates and Surcharges";
                   while(rsSelectQuoteId.next())
                   {
                     // COMMENTED& ADDED BY SUBRAHMANYAM FOR 187878 ON NOV-4-09
                 //    if(rsSelectQuoteId.getString("CHANGEDESC").equalsIgnoreCase(changedesc))
                      if(rsSelectQuoteId.getString("CHANGEDESC").indexOf(weightbreakslab)!=-1)

                     {
                        pstmtQmsQuoteupdate.setString(1,changedesc);
                        pstmtQmsQuoteupdate.setInt(2,new_versionNo+1);
                        pstmtQmsQuoteupdate.setString(3,rsSelectQuoteId.getString("QUOTE_ID"));
                     }
                     else
                     {
                        pstmtQmsQuoteupdate.setString(1,weightbreakslab+","+rsSelectQuoteId.getString("CHANGEDESC"));
                        pstmtQmsQuoteupdate.setInt(2,new_versionNo+1);
                        pstmtQmsQuoteupdate.setString(3,rsSelectQuoteId.getString("QUOTE_ID"));

                     }
                     pstmtQmsQuoteupdate.executeUpdate();
                   }
                   pStmtQuote.clearParameters();
                  pStmtQuote.setString(1,flatRatesDOB.getBuyrateId());
                   pStmtQuote.setInt(2,flatRatesDOB.getLaneNo());
                    pStmtQuote.setString(3,weightbreakslab);
                     pStmtQuote.setString(4,flatRatesDOB.getRates()[j]);
                    // pStmtQuote.setInt(6,prevoiusQuotid);
                   rsQuote= pStmtQuote.executeQuery();
                 
                    while(rsQuote.next())
                    {
                           //String quoteWtBrk=rsQuote.getString("BREAK_POINT");
                         // int QuoteId=rsQuote.getInt("QUOTEID");
                       //pStmtCount = connection.prepareStatement(countqry); Commented by Gowtham on 04Feb2011 for Loop Leakages.
                       pStmtCount.clearParameters();
                       pStmtCount.setString(1,rsQuote.getString("QUOTEID"));
                       rsCount  = pStmtCount.executeQuery();
                       
                       if(rsCount.next()&&rsCount.getInt(1)==0)
                       {
                         pstmtQmsQuoteInsert.clearParameters();
                        pstmtQmsQuoteInsert.setInt(1,Integer.parseInt(rsQuote.getString("QUOTEID")));
                      //pstmtQmsQuoteInsert.setInt(2,Integer.parseInt(buyRateID));
                        pstmtQmsQuoteInsert.setInt(2,Integer.parseInt(flatRatesDOB.getBuyrateId()));
                       // pstmtQmsQuoteInsert.setInt(3,i); commented by VALSKHMI for issue 146968 
                        pstmtQmsQuoteInsert.setInt(3,flatRatesDOB.getLaneNo());
                        pstmtQmsQuoteInsert.setString(4,"BR");
                        pstmtQmsQuoteInsert.setString(5,weightbreakslab+","+origin+"-"+destination+", Sea Freight Rates and Surcharges");
                        pstmtQmsQuoteInsert.setInt(6,Integer.parseInt(flatRatesDOB.getBuyrateId()));
                        pstmtQmsQuoteInsert.setInt(7,flatRatesDOB.getLaneNo());
                        pstmtQmsQuoteInsert.setInt(8,old_versionNo);
                        pstmtQmsQuoteInsert.setInt(9,new_versionNo+1);
                        pstmtQmsQuoteInsert.executeUpdate();
                       }    
                    }
                     
                                  }
                  
                
               if(j==0&&!("2".equalsIgnoreCase(shipmentmode)&&"FCL".equalsIgnoreCase(consoletype)))
              {
              cStmt.clearParameters();
              cStmt.setNull(1,Types.DOUBLE);
              cStmt.setLong(2,Long.parseLong(flatRatesDOB.getBuyrateId()));
              cStmt.setInt(3,flatRatesDOB.getLaneNo());
              cStmt.setInt(4,old_versionNo);// aded by VLAKSHMI
              cStmt.setInt(5,new_versionNo+1);// aded by VLAKSHMI
              cStmt.setNull(6,Types.DOUBLE);
            //cStmt.setLong(7,Long.parseLong(buyRateID)); commented by VALSKHMI
              cStmt.setLong(7,Long.parseLong(flatRatesDOB.getBuyrateId()));
              //cStmt.setInt(8,i);// commented by VLAKSHMI for issue on 20-01-2009
              cStmt.setInt(8,flatRatesDOB.getLaneNo());
              cStmt.setString(9,"BR");
              cStmt.setNull(10,Types.VARCHAR);
              cStmt.setNull(11,Types.VARCHAR);
             cStmt.setString(12,origin+"-"+destination+","+shipmodeStr+" Freight Rates and Surcharges");
             cStmt.execute();
                        
              }
                  if(j==0 && !"2".equalsIgnoreCase(shipmentmode))
                  {
                    freqArray = frequency.split(",");
                    
                    if(freqArray!=null)
                    {
                    	int freqArrLen	=	freqArray.length;
                      for(int z=0;z<freqArrLen;z++)
                      {
                       // pStmtFreq.setLong(1,Long.parseLong(buyRateID)); commented by VALKSHMI for issue 146968 on 10/12/2008
                        pStmtFreq.setLong(1,Long.parseLong(flatRatesDOB.getBuyrateId()));
                        //pStmtFreq.setInt(2,i);
                         pStmtFreq.setInt(2,flatRatesDOB.getLaneNo());
                        pStmtFreq.setInt(3,Integer.parseInt(freqArray[z]));
                        pStmtFreq.setInt(4,new_versionNo+1);//added by VALKSHMI for issue 146968 on 10/12/2008
                        pStmtFreq.addBatch();
                      }
                    }
                    
                  }

								  flagDtl = true;
                  
								
							  } 
                	j++;
                
   }
              
                  if(flagDtl)
                  {
                   //  cStmt = connection.prepareCall("{ call qms_buy_rates_pkg.sellratesmstr_acc_proc(?)}");
                // cStmt.setDouble(1,Double.parseDouble(buyRateID));commented by VALSKHMI
                  cStmt1.setInt(1,Integer.parseInt(flatRatesDOB.getBuyrateId()));
                  cStmt1.setInt(2,new_versionNo+1);
                   cStmt1.setInt(3,flatRatesDOB.getLaneNo());
                    cStmt1.addBatch();
                 }
    			  }
           /* pStmt.executeBatch();
            pStmt6.executeBatch();
					  pStmt4.executeBatch();
            pStmt5.executeBatch();*/
					 // pStmt2.executeBatch();//commented by phani sekhar for wpbn 176431 on 20090714 
           pStmtFreq.executeBatch();
           cStmt1.executeBatch();
            // cStmt1.executeBatch();
            // cStmt.executeBatch();
        
           /* if(cStmt!=null)
              { cStmt.close();}  */

                   //To Update the buyrates and sellrates into RSR_Acceptence module
                  
                    //end @@sekhar


	}catch(SQLException e)
		{
			  e.printStackTrace();
			  				      throw new Exception ("Problem while UPDATING the Details <BR>");
		}catch(Exception e)
		{
			 e.printStackTrace();
			 				      throw new Exception ("Problem while UPDATING the Details <BR>");

		}finally{

			try{
			 if(rs!=null)
				 rs.close();
         if(rsVersion!=null)// added by VLAKSHMI for connection leakage
         rsVersion.close();
         if(rsCount!=null)// added by VLAKSHMI for connection leakage
         rsCount.close();
          if(rsSelectQuoteId!=null)// added by VLAKSHMI for connection leakage
         rsSelectQuoteId.close();
           if(rsQuote!=null)// added by VLAKSHMI for connection leakage
         rsQuote.close();
    	 if(pStmt!=null)
				 pStmt.close();
			 if(pStmt1!=null)
				 pStmt1.close();
          /*if(pStmt6!=null)
				 pStmt6.close();*///Commented by govind on 16-02-2010 for Connectiobn leakage
			 if(pStmt2!=null)
				 pStmt2.close();
			 if(pStmt3!=null)
				 pStmt3.close();
    /*   if(pStmt4!=null)
          pStmt4.close();*///Commented by Govind on 16-02-2010 for Connection Leakages
//         if(pStmt5!=null)//@@Added now by Kameswari //Commented by Govind on 16-02-2010 for Connection Leakages
          //pStmt5.close();
       if(pStmtFreq!=null)
          pStmtFreq.close();
           if(pStmtVersion!=null)// added by VLAKSHMI for connection leakage
          pStmtVersion.close();
          if(pStmtQuote!=null)// added by VLAKSHMI for connection leakage
          pStmtQuote.close();
          if(pstmtQmsQuoteInsert!=null)// added by VLAKSHMI for connection leakage
          pstmtQmsQuoteInsert.close();
          if(pstmtSelectQuoteId!=null)// added by VLAKSHMI for connection leakage
          pstmtSelectQuoteId.close();
           if(pstmtQmsQuoteupdate!=null)// added by VLAKSHMI for connection leakage
          pstmtQmsQuoteupdate.close();
          if(pStmtCount!=null)// added by VLAKSHMI for connection leakage
          pStmtCount.close();
            if(connection!=null)
					connection.close();
          if(cStmt!=null)
          cStmt.close();
            if(cStmt1!=null)
          cStmt1.close();
          



			}catch(SQLException e)
			{
				e.printStackTrace();
								      throw new Exception ("Problem while UPDATING the Details <BR>");
			}catch(Exception e)
			{
				      throw new Exception ("Problem while UPDATING the Details <BR>");
			}
	}

}

public String upLoadcreateBuyFlatRate(RateDOB rateDOB,ArrayList list,Connection connection)throws Exception
   {
      PreparedStatement		pStmt			      = null;
      PreparedStatement		pStmtb				  = null; 	
      PreparedStatement   pStmtFreq       = null;
      //Connection				  connection		  = null;
      String              master_qry      = null;
      String              seq_qry         = null;
      String              update_qry      = null;
      String              log_qry         = null;
      String              dtl_qry         = null;
      FlatRatesDOB        flatRatesDOB    = null;
      OperationsImpl      impl            = null;
      ResultSet           rs              = null;
      String              buyRateID       = null;
      ArrayList           wtList          = null;
      ArrayList           chargeList      = null;
      ArrayList           wtBreakList     = null;
      ArrayList           chargeRateList  = null;
	  //Added by Mohan for issue no.219973 on 01122010
      ArrayList           wtDescList      = null;
      ArrayList           surChargeCurrList= null;
      ArrayList           wtBrkTypeList	  = null;
      ArrayList           rateTypeList	  = null;
      String			  tt1			  =  null;
      String			  frgtWtBreak	  = null;
      int				  wtTypeIndex	  = 0;
      String			  wtTypeValue	  = null;
      String			  wtTypeValueTemp = null;
      String 			  wtBreakType     = null;
      String 			  surChargeId 	  = null;
      String 			  temppp 		  = "";
	//End by Mohan for issue no.219973 on 01122010      
      String              accessLevel     = "";
      ArrayList           slabFlatList    = null;
      int                 chargeRateListSize = 0;
      String              wtBreak         = null;
      String              rateType        = null;
      boolean             flagDtl         = false;
      CallableStatement   cStmt           = null;
      String              freqStr         = null;
      String              freqQry         = null;
      String[]            freqArray       = null;
      int laneNo = -1;
       String            shipmentMode     = null;
       String            terminalId       = null;
       //@@addeed by Kameswari for the WPBN issue-146448 on 20/12/08
      String              proc_inactivate = null; 
      
      String             returnStr1       = null;
      String             oldbuyrateid     = null;
      //ArrayList          tempbuyrateid     = new ArrayList();
      String             oldlaneno        = null;
      String[]           returnStr        = null;
      String              versionQuery   = null;
      int versionno = 1;
      int temp1      = 0;
      String[]			tmpDensRatio		=	null; // Added by Gowtham.
      PreparedStatement   pStmt1         = null;
       PreparedStatement   pStmt2         = null;
      ResultSet           rs1            = null;
      CallableStatement   cStmt1          = null;
       CallableStatement   cStmt3          = null;
      // added by VLAKSHMI for issue 146968 on 23/12/2008
      PreparedStatement     pStmtQuote           =    null;
      PreparedStatement     pstmtQmsQuoteInsert =    null;
      PreparedStatement     pstmtSelectQuoteId   =    null;
     // PreparedStatement     pStmtQuoteBreak       =    null; //Commented by govind on 16-02-2010 for Connection leakages
      PreparedStatement     pstmtQmsQuoteupdate  =    null;
      PreparedStatement    pStmtCount           = null; //@@Added by Kameswari for the WPBN issue-146968 on 01/01/09  
     // ResultSet				rsQmsQuoteInsert		  = 	null;//Commented by govind on 16-02-2010 for Connection leakages
      ResultSet				rsSelectQuoteId			  = 	null;
//      ResultSet			  rsQuoteBreak		      = 	null;//Commented by govind on 16-02-2010 for Connection leakages
    //  ResultSet				rsQmsQuoteupdate		  = 	null;//Commented by govind on 16-02-2010 for Connection leakages
      ResultSet				    rsQuote				  = 	null;
     String           quoteId =null;// added by VLAKSHMI for issue 146968 on 23/12/2008
     String           qmsQuoteInsert         = null;
     String           shipmodeStr            = null;
     String           selectQuoteIdFlag      = null;
     String           quote_Break_qry        = null;
     String           qmsQuoteupdate         = null;
      String          quote_qry              = null;
      // String          servicelevel          = null;
      //String          effectiveFrom[]        = null;
       //String          validUpto[]           = null;
     //  issue 146968 on 23/12/2008
      
       ResultSet				rsCount		          = 	null;
        String           selectquote_qry        = null;//@@Added by Kameswari for the WPBN issue-146968 on 01/01/09
     String           updatequote_qry        = null;//@@Added by Kameswari for the WPBN issue-146968 on 01/01/09
     String           countqry               = null;//@@Added by Kameswari for the WPBN issue-146968 on 01/01/09
      int laneno = 0;
      int  buyratesize = 0;

      int finallaneno = 0;
      int  count2 = 0;
       int  newversionno = 0;
       //@@ Added by subrahmanyam for the wpbn id: 196113 on 01/Feb/10
       String noChangeRateQry1 		= "SELECT count(*)  FROM QMS_BUYRATES_DTL BD1 WHERE "+
       									" BD1.BUYRATEID = ?   AND BD1.LANE_NO = ? AND  BD1.VERSION_NO = ?"+
       									" AND (BD1.WEIGHT_BREAK_SLAB) NOT IN (SELECT BD.WEIGHT_BREAK_SLAB "+
       									" FROM QMS_BUYRATES_DTL BD WHERE BD.BUYRATEID = BD1.BUYRATEID "+
       									" AND BD.LANE_NO = BD1.LANE_NO AND BD.VERSION_NO = ? )";
       int updateQuoteCount								  =	    0;
       PreparedStatement    pStmtNoChangeRate             = 	null;   
       ResultSet			rsNoChangeRate 		     	  = 	null;
       PreparedStatement    pStmtNoChangeRate1            = 	null;   
       ResultSet			rsNoChangeRate1 		      = 	null;
       PreparedStatement    pStmtNoChangeRateQuotes       = 	null;   
       ResultSet			rsNoChangeRateQuotes 		  = 	null;
       PreparedStatement    pStmtNoChangeRateQuotes1      = 	null;   
       ResultSet			rsNoChangeRateQuotes1	      = 	null;
       PreparedStatement    pStmtNoChangeRateQuotes2      = 	null;   
       PreparedStatement    pStmtNoChangeUpdInsert        = 	null;   
       int					tmpSchCurrCnt				  = 0; // Added by Gowtham .
       int					tmpSchCurrCnt1				  = 0;	 // Added by Gowtham .
       String				fr_Currency					  = null; // Added by Gowtham.	
       String[]				tmpDescRatio				  = null ; 	
       
       //@@ Ended by subrahmanyam for the wpbn id: 196113 on 01/Feb/10
   
      try
       {
         
         seq_qry     = " SELECT BUYRATE_SEQ.NEXTVAL BUYRATEID FROM DUAL  ";         
         /*master_qry  =  " INSERT INTO QMS_BUYRATES_MASTER (SHIPMENT_MODE,  CURRENCY, UOM, WEIGHT_BREAK,  "+
                      " RATE_TYPE, CONSOLE_TYPE, CREATED_BY, DEL_FLAG, BUYRATEID, WEIGHT_CLASS, ACCESSLEVEL, TERMINALID  )"+
                      " VALUES (?,?,?,?,?,?,?,?,?,?,?,?) ";  */                    
       //@@Modified by Kameswari for the WPBN issue-146448 on 20/12/08
       
        master_qry  =  " INSERT INTO QMS_BUYRATES_MASTER (SHIPMENT_MODE,  CURRENCY, UOM, WEIGHT_BREAK,  "+
                      " RATE_TYPE, CONSOLE_TYPE, CREATED_BY, DEL_FLAG, BUYRATEID, WEIGHT_CLASS, ACCESSLEVEL, TERMINALID,VERSION_NO,LANE_NO)"+
                      " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?) "; 
        
        /*dtl_qry      = "     INSERT INTO QMS_BUYRATES_DTL (BUYRATEID ,ORIGIN, DESTINATION, SERVICE_LEVEL,  "
                      +"  FREQUENCY, TRANSIT_TIME, CHARGERATE, WEIGHT_BREAK_SLAB, LANE_NO, LOWERBOUND ,UPPERBOUND  ,"
                      +"   NOTES, LINE_NO,CARRIER_ID,CHARGERATE_INDICATOR,CREATEDTIME ,OVER_PIVOT,EFFECTIVE_FROM, VALID_UPTO,ID,DENSITY_CODE,RATE_DESCRIPTION ) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,seq_BUYRATES_DTL.nextval,?,?) ";*/
      
       //@@Modified by Kameswari for the WPBN issue-146448 on 20/12/08
       dtl_qry      = "     INSERT INTO QMS_BUYRATES_DTL (BUYRATEID ,ORIGIN, DESTINATION, SERVICE_LEVEL,  "
                      +"  FREQUENCY, TRANSIT_TIME, CHARGERATE, WEIGHT_BREAK_SLAB, LANE_NO, LOWERBOUND ,UPPERBOUND  ,"
                       //Modified by Mohan for Issue No.219976 on 08-10-2010
                      +"   NOTES, LINE_NO,CARRIER_ID,CHARGERATE_INDICATOR,CREATEDTIME ,OVER_PIVOT,EFFECTIVE_FROM, VALID_UPTO,ID,DENSITY_CODE,RATE_DESCRIPTION,VERSION_NO,EXTERNAL_NOTES,SUR_CHARGE_CURRENCY,SURCHARGE_ID) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,seq_BUYRATES_DTL.nextval,?,?,?,?,?,?) ";
					 	//Modified by Mohan for issue no.219973 on 01122010
        //@@Modified by Kameswari for the WPBN issue-146448 on 24/12/08
       // freqQry      =  " INSERT INTO QMS_BUYRATES_FREQ VALUES (?,?,?)";
       freqQry      =  " INSERT INTO QMS_BUYRATES_FREQ VALUES (?,?,?,?)";
                     
        log_qry      = " INSERT INTO FS_USERLOG (LOCATIONID ,USERID ,DOCTYPE ,DOCREFNO ,DOCDATE ,TRANSACTIONTYPE ) VALUES(?,?,?,?,?,?)    "          ;
            
    //  proc_inactivate = "{ ?= call PKG_QMS_BUYRATES.validate_upd_insert_buyrate(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";//@@Added by Kameswari for the WPBN issue-146448 on 22/12/08
        proc_inactivate = "{ ?= call PKG_QMS_BUYRATES.validate_upd_insert_buyrate(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";//@@Added by Govind for the keep checking when we add/upload rate 
          versionQuery    = "SELECT MAX(VERSION_NO) VERSION_NO FROM QMS_BUYRATES_DTL WHERE BUYRATEID=? AND LANE_NO=?";

       qmsQuoteInsert= " INSERT INTO QMS_QUOTES_UPDATED (QUOTE_ID, "+
           " NEW_BUYCHARGE_ID, NEW_LANE_NO, SELL_BUY_FLAG, CHANGEDESC, OLD_BUYCHARGE_ID, OLD_LANE_NO,OLD_VERSION_NO,NEW_VERSION_NO) "+
        " Values(?,?,?,?,?,?,?,?,?) ";
    quote_qry="SELECT  QR.QUOTE_ID QUOTEID"
                   +" From Qms_Quote_Rates Qr,QMS_QUOTE_MASTER QM WHERE QM.ACTIVE_FLAG='A' AND QM.ID=QR.QUOTE_ID AND Qr.Sell_Buy_Flag ='BR' And Qr.Buyrate_Id = ?"
                   
                   +" And Qr.Rate_Lane_No = ? AND QR.BREAK_POINT =?  AND QR.BUY_RATE NOT IN (?)";


  
         countqry = "SELECT COUNT(*) FROM QMS_QUOTES_UPDATED WHERE QUOTE_ID =? AND SELL_BUY_FLAG='BR' AND CONFIRM_FLAG IS NULL";
    selectquote_qry  = "SELECT QUOTE_ID,CHANGEDESC FROM QMS_QUOTES_UPDATED WHERE QUOTE_ID IN (SELECT QR.QUOTE_ID FROM QMS_QUOTE_RATES QR,QMS_QUOTE_MASTER QM WHERE QM.ACTIVE_FLAG='A' AND QM.ID=QR.QUOTE_ID AND SELL_BUY_FLAG='BR' AND BUYRATE_ID=? AND RATE_LANE_NO=? AND BREAK_POINT =?  AND BUY_RATE NOT IN (?) ) AND CONFIRM_FLAG IS NULL";               
   
    updatequote_qry ="UPDATE QMS_QUOTES_UPDATED SET CHANGEDESC= ?,NEW_VERSION_NO=? WHERE QUOTE_ID=? AND SELL_BUY_FLAG='BR'";
   
     
       // pStmt       = connection.prepareStatement(seq_qry);
          
        pStmt1       = connection.prepareStatement(versionQuery);
        //rs          = pStmt.executeQuery();   
      
        //if(rs.next()) buyRateID   = rs.getString("BUYRATEID");          
        
       // ConnectionUtil.closeStatement(pStmt,rs);  
        pStmt2       = connection.prepareStatement(master_qry);
              
   
              
       /* pStmt.clearParameters();
        pStmt.setString(1,rateDOB.getShipmentMode());
        pStmt.setString(2,rateDOB.getCurrency());
        pStmt.setString(3,rateDOB.getUom());
        pStmt.setString(4,rateDOB.getWeightBreak());
        pStmt.setString(5,rateDOB.getRateType());
        pStmt.setString(6,rateDOB.getConsoleType());
       pStmt.setString(7,rateDOB.getUser());                      
        pStmt.setString(8,"N");                      
        pStmt.setString(9,buyRateID); 
        pStmt.setString(10,rateDOB.getWeightClass()); */
        
        wtBreak      =   rateDOB.getWeightBreak();
        rateType     =   rateDOB.getRateType();
        shipmentMode =   rateDOB.getShipmentMode();
        terminalId   =   rateDOB.getTerminalId();
      
          
        if("OPER_TERMINAL".equalsIgnoreCase(rateDOB.getAccessLevel()))
        {
          accessLevel="O";
        }else if("HO_TERMINAL".equalsIgnoreCase(rateDOB.getAccessLevel()))
        {
            accessLevel ="H";
        }else if("ADMN_TERMINAL".equalsIgnoreCase(rateDOB.getAccessLevel()))
        {
            accessLevel ="A";
        }
      /*  pStmt.setString(11,accessLevel); 
        pStmt.setString(12,terminalId); 
                      
        pStmt.executeUpdate();
                      
        ConnectionUtil.closeStatement(pStmt);*/
       //  ESupplyDateUtility  eSupplyDateUtility=	new ESupplyDateUtility(); //@@Added by Kameswari on 05/02/09
      // eSupplyDateUtility.setPatternWithTime("DD-MONTH-YYYY");
         pStmt       = connection.prepareStatement(dtl_qry);
         pStmtFreq   = connection.prepareStatement(freqQry);
         //wtBreakList =  rateDOB.getWtBreakList();
          wtList       		 = rateDOB.getWtBreakList();
          wtDescList		 = rateDOB.getWtBreakDescList();//Added by Mohan 
          surChargeCurrList  = rateDOB.getSurChargeCurrList();//Added by Mohan
          wtBrkTypeList		 = rateDOB.getWtBreakTypesList();//Added by Mohan
          rateTypeList		 = rateDOB.getRateTypeList();//Added by Mohan
          System.out.println("wtList Size--->"+wtList.size());
          System.out.println("wtDescList Size--->"+wtDescList.size());
          System.out.println("surChargeCurrList Size--->"+surChargeCurrList.size());
          System.out.println("wtBrkTypeList Size--->"+wtBrkTypeList.size());
          System.out.println("rateTypeList Size--->"+rateTypeList.size());
          int listSize    = list.size();
         //int temp=0;
          int count = 1;
        
         //cStmt = connection.prepareCall("{ call pkg_qms_buyrates.Qms_INACTIVATE_Buyrate(?,?,?,?,?,?,?,?,?,?,?,?,?)}");//@@Modified by Kameswarifor the WPBN issue-146448 on 20/12/08
        cStmt = connection.prepareCall(proc_inactivate);
           // cStmt1  = connection.prepareCall("{ call pkg_qms_buyrates.qms_update_new_quote(?,?,?,?,?,?,?,?,?,?,?,?,?)}");
              cStmt1  = connection.prepareCall("{ call pkg_qms_buyrates.qms_update_new_quote(?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");
           cStmt3 = connection.prepareCall("{ call qms_buy_rates_pkg.sellratesmstr_acc_proc(?,?,?)}");
        
                     
         //System.out.println("after calling invactive");
           
         for(int i=0;i<listSize;i++)
         { 
            buyratesize = 0;
             oldbuyrateid = "";
             oldlaneno = "";

             returnStr1 = "";

              finallaneno = laneno;

    
            //temp=0;
            flatRatesDOB =  (FlatRatesDOB)list.get(i);
          
            tmpDescRatio = flatRatesDOB.getDensityRatio().split(","); // Added by Gowtham for Buyrate Upload Issue.
            //chargeRateList   =  flatRatesDOB.getChargeRateList();
             chargeList   =  flatRatesDOB.getChargeRateList();
            slabFlatList      =  flatRatesDOB.getSlabFlatList();
               //@@Added by kameswari for the WPBN issue-59354
            chargeRateList   = new ArrayList();
            wtBreakList   = new ArrayList();
            // Added by Gowtham for Buyrate Upload Issue.
            tmpSchCurrCnt1 = tmpSchCurrCnt;
            if(i==0)
            fr_Currency = surChargeCurrList.get(0).toString();
            else
            fr_Currency = surChargeCurrList.get(tmpSchCurrCnt1+1).toString();
            
            
            
            rateDOB.getCurrency();
            cStmt.clearParameters();
            //servicelevel = flatRatesDOB.getServiceLevel();
            /*cStmt.setString(1,shipmentMode);
            cStmt.setString(2,flatRatesDOB.getOrigin());
            cStmt.setString(3,flatRatesDOB.getDestination());
            cStmt.setString(4,flatRatesDOB.getServiceLevel());
            cStmt.setString(5,flatRatesDOB.getCarrierId());
            cStmt.setTimestamp(6,flatRatesDOB.getEffDate());
            
            if(flatRatesDOB.getValidUpto()!=null)
              cStmt.setTimestamp(7,flatRatesDOB.getValidUpto());
            else
              cStmt.setNull(7,Types.DATE);
           
            cStmt.setString(8,wtBreak);
            cStmt.setString(9,rateType);
            cStmt.setString(10,terminalId);
            freqStr = flatRatesDOB.getFrequency();
            freqStr = freqStr.replace(',','~');
            cStmt.setString(11,freqStr);
            cStmt.setString(12,buyRateID);          
            cStmt.setInt(13,flatRatesDOB.getLaneNo());
            cStmt.addBatch();*/
         //  effectiveFrom	  = eSupplyDateUtility.getDisplayStringArray(flatRatesDOB.getEffDate());
      	 // validUpto	  = eSupplyDateUtility.getDisplayStringArray(flatRatesDOB.getValidUpto());
          cStmt.registerOutParameter(1,Types.VARCHAR);
          cStmt.setString(2,shipmentMode);
          cStmt.setString(3,flatRatesDOB.getOrigin());
          cStmt.setString(4,flatRatesDOB.getDestination());
          cStmt.setString(5,flatRatesDOB.getServiceLevel());
          cStmt.setString(6,flatRatesDOB.getCarrierId());
          cStmt.setTimestamp(7,flatRatesDOB.getEffDate());
//          cStmt.setString(7,effectiveFrom[0]);
         if(flatRatesDOB.getValidUpto()!=null )
            cStmt.setTimestamp(8,flatRatesDOB.getValidUpto());
          /* if(flatRatesDOB.getValidUpto()!=null )
            cStmt.setString(8,validUpto[0]);  */ //@@Modified by kameswari on 05/02/09
          else
            cStmt.setNull(8,Types.DATE);
            
            
          cStmt.setString(9,wtBreak.toUpperCase());
          cStmt.setString(10,rateType.toUpperCase());
          cStmt.setString(11,terminalId);
        
          freqStr  = flatRatesDOB.getFrequency();
          freqStr = freqStr.replace(',','~');
          cStmt.setString(12,freqStr);
          
          //System.out.println("flatRatesDOB.getDensityRatio()"+flatRatesDOB.getDensityRatio());
          
          if(flatRatesDOB.getDensityRatio()==null || "".equals(flatRatesDOB.getDensityRatio())
           || "null".equals(flatRatesDOB.getDensityRatio()))
          cStmt.setNull(13,Types.VARCHAR);
          else if(tmpDescRatio[i]== null || "".equals(tmpDescRatio[i])||"null".equals(tmpDescRatio[i]))
        	  cStmt.setNull(13,Types.VARCHAR);
          else
            cStmt.setString(13,tmpDescRatio[i]);  // Added by Gowtham.
          
          cStmt.setNull(14,Types.INTEGER);
          cStmt.setNull(15,Types.INTEGER);
          cStmt.setString(16,flatRatesDOB.getTransittime()); //@@Added by Kameswari for the WPBN sisue-146448 on 20/12/08
          cStmt.setString(17,"UPLOAD");
         // cStmt.setString(18,rateDOB.getCurrency()!= null?rateDOB.getCurrency():"");
           cStmt.setString(18,fr_Currency!=null?fr_Currency:rateDOB.getCurrency()); // Added by Gowtham for Buyrate Upload Issue,
          cStmt.execute();
          returnStr1      = cStmt.getString(1);
          
           returnStr      = returnStr1.split(",");//@@Added for the WPBN issues-146448,146968 on 18/12/08
     
           if(returnStr!=null&&returnStr.length>1)
           {
             oldbuyrateid   = returnStr[1];
             oldlaneno      = returnStr[2];
           }
                  if(oldbuyrateid!=null&&oldbuyrateid.trim().length()>0)
                 {
                   pStmt1.setString(1,oldbuyrateid);
                   pStmt1.setString(2,oldlaneno);
                   rs1     =  pStmt1.executeQuery();
                   if(rs1.next())
                   {
                      versionno   =   rs1.getInt("VERSION_NO");
                   }
                 }
         
                 newversionno = versionno+1;
                 int chargeListSize	=	chargeList.size();
            for(int j=0;j<chargeListSize;j++)
            {
             
               if(chargeList.get(j)!=null)
               {
                 chargeRateList.add(chargeList.get(j));
                 wtBreakList.add(wtList.get(j));
                 
               }
            }     pStmt2.clearParameters();
          
                if(oldbuyrateid!=null&&oldbuyrateid.trim().length()>0)
              {
                pStmt2.setString(1,rateDOB.getShipmentMode());
               // pStmt2.setString(2,rateDOB.getCurrency());
                pStmt2.setString(2,fr_Currency!=null?fr_Currency:rateDOB.getCurrency() ); // Added by Gowtham for Buyrate Upload Issue.
                pStmt2.setString(3,rateDOB.getUom());
                pStmt2.setString(4,rateDOB.getWeightBreak().toUpperCase());
                pStmt2.setString(5,rateDOB.getRateType().toUpperCase());
                pStmt2.setString(6,rateDOB.getConsoleType());
                pStmt2.setString(7,rateDOB.getUser());                      
                pStmt2.setString(8,"N"); 
                pStmt2.setString(9,oldbuyrateid); 
                pStmt2.setInt(13,(versionno+1));
              pStmt2.setString(14,oldlaneno);//@@Added by kameswari on 04/02/09
                pStmt2.setString(10,rateDOB.getWeightClass()); 
                pStmt2.setString(11,accessLevel); 
                pStmt2.setString(12,terminalId); 
                // pStmt2.addBatch();
                 pStmt2.execute();
               /*   if(tempbuyrateid.size()==0)
                 {
              //  pStmt2.clearParameters();
            
                pStmt2.setString(1,rateDOB.getShipmentMode());
                pStmt2.setString(2,rateDOB.getCurrency());
                pStmt2.setString(3,rateDOB.getUom());
                pStmt2.setString(4,rateDOB.getWeightBreak().toUpperCase());
                pStmt2.setString(5,rateDOB.getRateType().toUpperCase());
                pStmt2.setString(6,rateDOB.getConsoleType());
                pStmt2.setString(7,rateDOB.getUser());                      
                pStmt2.setString(8,"N"); 
                pStmt2.setString(9,oldbuyrateid); 
                pStmt2.setInt(13,versionno+1);
              pStmt2.setString(14,oldlaneno);//@@Added by kameswari on 04/02/09
                pStmt2.setString(10,rateDOB.getWeightClass()); 
                pStmt2.setString(11,accessLevel); 
                pStmt2.setString(12,terminalId); 
                // pStmt2.addBatch();
                 pStmt2.execute();
                 tempbuyrateid.add(oldbuyrateid);
                 }
                 
               else
               {
                 for( int z=0;z<tempbuyrateid.size();z++)
                 {
                   if(!tempbuyrateid.get(z).equals(oldbuyrateid))
                   {
                        buyratesize++;
                   }
                 }
             
               if(buyratesize==tempbuyrateid.size())
               {
                pStmt2.setString(1,rateDOB.getShipmentMode());
                pStmt2.setString(2,rateDOB.getCurrency());
                pStmt2.setString(3,rateDOB.getUom());
                pStmt2.setString(4,rateDOB.getWeightBreak().toUpperCase());
                pStmt2.setString(5,rateDOB.getRateType().toUpperCase());
                pStmt2.setString(6,rateDOB.getConsoleType());
                pStmt2.setString(7,rateDOB.getUser());                      
                pStmt2.setString(8,"N"); 
                 pStmt2.setString(9,oldbuyrateid); 
                 pStmt2.setInt(13,versionno+1);
                 pStmt2.setString(14,oldlaneno);//@@Added by kameswari on 04/02/09
                 pStmt2.setString(10,rateDOB.getWeightClass()); 
                  pStmt2.setString(11,accessLevel); 
                 pStmt2.setString(12,terminalId); 
                   //pStmt2.addBatch();
                  pStmt2.execute();
                 tempbuyrateid.add(oldbuyrateid);
                 
                   } 
                 } */
                
              }
              else
              {
                    // if(temp1==0) 
            	  
            	  pStmtb = connection.prepareStatement(seq_qry);
            	  
            	  rs          = pStmtb.executeQuery();   
                  
                  if(rs.next()) buyRateID   = rs.getString("BUYRATEID");          
                  
                 
            	  
                 {
                  pStmt2.setString(1,rateDOB.getShipmentMode());
                  //pStmt2.setString(2,rateDOB.getCurrency());
                  pStmt2.setString(2,fr_Currency!=null?fr_Currency:rateDOB.getCurrency()); // Added by Gowtham for Buyrate Upload Issue.
                  pStmt2.setString(3,rateDOB.getUom());
                  pStmt2.setString(4,rateDOB.getWeightBreak().toUpperCase());
                  pStmt2.setString(5,rateDOB.getRateType().toUpperCase());
                  pStmt2.setString(6,rateDOB.getConsoleType());
                  pStmt2.setString(7,rateDOB.getUser());                      
                  pStmt2.setString(8,"N"); 
                  pStmt2.setString(9,buyRateID); 
                  pStmt2.setInt(13,1); 
                   pStmt2.setNull(14,Types.NULL);
                   pStmt2.setString(10,rateDOB.getWeightClass()); 
                  pStmt2.setString(11,accessLevel); 
                    pStmt2.setString(12,terminalId); 
                     // pStmt2.addBatch();
                      pStmt2.execute();
                  // temp1++;
                 }
              }
          
              
              wtBreak   =   rateDOB.getWeightBreak();
              rateType  =   rateDOB.getRateType();
              shipmentMode = rateDOB.getShipmentMode();
              terminalId =rateDOB.getTerminalId();
              if("OPER_TERMINAL".equalsIgnoreCase(rateDOB.getAccessLevel()))
              {
                accessLevel="O";
              }else if("HO_TERMINAL".equalsIgnoreCase(rateDOB.getAccessLevel()))
              {
                  accessLevel ="H";
              }else if("ADMN_TERMINAL".equalsIgnoreCase(rateDOB.getAccessLevel()))
              {
                  accessLevel ="A";
              }
            
                            
            
                  
         // ConnectionUtil.closeStatement(pStmt);
              int wtBrkListSize	=	wtBreakList.size();
              /* for(int k=0;k<wtBrkListSize;k++)
            {
      
              /* if(!("FSBASIC".equalsIgnoreCase((String)wtBreakList.get(k))||"FSMIN".equalsIgnoreCase((String)wtBreakList.get(k))
                        ||"FSKG".equalsIgnoreCase((String)wtBreakList.get(k))||"SSBASIC".equalsIgnoreCase((String)wtBreakList.get(k))
                        ||"SSMIN".equalsIgnoreCase((String)wtBreakList.get(k))||"SSKG".equalsIgnoreCase((String)wtBreakList.get(k))
                        ||"CAFMIN".equalsIgnoreCase((String)wtBreakList.get(k))||"CAF%".equalsIgnoreCase((String)wtBreakList.get(k))
                        ||"BAFMIN".equalsIgnoreCase((String)wtBreakList.get(k))||"BAFM3".equalsIgnoreCase((String)wtBreakList.get(k))
                     
                        ||"CSF".equalsIgnoreCase((String)wtBreakList.get(k))||"SURCHARGE".equalsIgnoreCase((String)wtBreakList.get(k))))*/
             
          /* if(!(((String)wtBreakList.get(k)).startsWith("FS")||((String)wtBreakList.get(k)).startsWith("SS")
           ||((String)wtBreakList.get(k)).startsWith("BAF")||((String)wtBreakList.get(k)).startsWith("CAF")
           ||((String)wtBreakList.get(k)).startsWith("CSF")||((String)wtBreakList.get(k)).startsWith("SURCHARGE")
           ||((String)wtBreakList.get(k)).startsWith("PSS")))
             {* /
            	
            	if("A FREIGHT RATE".equals((String)wtDescList.get(k)))
            	{
                   temp++;
            	}
            }*/
             //@@ WPBN issue-59354
            chargeRateListSize = chargeRateList.size();
             
            //cStmt.execute();
            //System.out.println("before inserting:::"+i);
         
            for(int k=0;k<chargeRateListSize;k++)
            {			if(i>0)
            {
            		tmpSchCurrCnt++;
            	}
            if(i==0)
            	tmpSchCurrCnt = chargeRateListSize-1;
          
              //  if(chargeRateList.get(k)!=null)//@@Commented by Kameswari for the WPBN issue-59354
             //   {
            	if(!"NA".equalsIgnoreCase(chargeRateList.get(k)+""))	  //Added by Mohan for issue no.219973 on 01122010
            	{
            		pStmt.clearParameters() ;//@@Added by Kameswari for the WPBN issue-146448 on 20/12/08
                  if(oldbuyrateid!=null&&oldbuyrateid.trim().length()>0)
                {
                       pStmt.setString(1,oldbuyrateid);
                      pStmt.setInt(9,Integer.parseInt(oldlaneno));
                     pStmt.setInt(22,versionno+1);
                }
                else
                {
                      pStmt.setString(1,buyRateID);
                      pStmt.setInt(9,finallaneno);
                      pStmt.setInt(22,1);
                       if(k==0)
                     {
                       laneno++;
                     }
                     
               
                }
                  pStmt.setString		(2,	flatRatesDOB.getOrigin());
                  pStmt.setString		(3,	flatRatesDOB.getDestination());
                  
                  pStmt.setString		(5,	flatRatesDOB.getFrequency());
                  pStmt.setString		(6,	flatRatesDOB.getTransittime());
                  pStmt.setDouble		(7,	Double.parseDouble(chargeRateList.get(k)+""));
                  
                  wtBreakType =  (String)wtBreakList.get(k);
                  if(wtBreakType!=null && wtBreakType.contains("~"))
                  {
                	  surChargeId = wtBreakType.substring(0,wtBreakType.indexOf("~"));
                	  wtBreakType = wtBreakType.substring(wtBreakType.indexOf("~")+1, wtBreakType.length());
                	  
                	//  wtBreakType = wtBreakType.substring(wtBreakType.indexOf("~")+1, wtBreakType.length())+surChargeId;	  
                  }else
                	  surChargeId = "";
                  //System.out.println("TT --->"+wtBreakType);
                  pStmt.setString		(8,	wtBreakType);

                  if(k!=wtBrkListSize-1)
                	  tt1 =  (String)wtBreakList.get(k+1);

                  if(tt1!=null && tt1.contains("~"))
                	  tt1 = tt1.substring(tt1.indexOf("~")+1, tt1.length());
                  //System.out.println("TT --->"+tt1);
                  
                  wtTypeValueTemp = wtBrkTypeList.get(k)+"";
                 // System.out.println("Weight Break Types-1111-->"+wtTypeValueTemp);
                  wtTypeValue = wtTypeValueTemp.substring(0,wtTypeValueTemp.indexOf("~"));
                  //System.out.println("Weight Break Types--->"+wtTypeValue);
                 
                  if(k==0)
                	  frgtWtBreak=wtTypeValue;
                  
                  if( ! "slab".equalsIgnoreCase(wtTypeValue))
                  {
                
                    pStmt.setInt          (10,0);                              
                    pStmt.setInt          (11,0);  
                  }
                  else{ 
                     
                      wtTypeIndex = Integer.parseInt(wtTypeValueTemp.substring(wtTypeValueTemp.indexOf("~")+1,wtTypeValueTemp.length()));
                      //System.out.println("Weight Break Index-->"+wtTypeIndex);
                
                 if(wtTypeIndex==1 || wtTypeIndex==0)
                    {
                       pStmt.setInt(10,0);
                       if(wtTypeIndex==1)
                       {
                    	   if(!"MIN".equalsIgnoreCase(wtBreakType))
                    		   pStmt.setDouble(11,Double.parseDouble(tt1));
                    	   else
                    		   pStmt.setInt(11,0);
                       }else
                           pStmt.setInt(11,0);
                  }else
                    {
                        
               
                     if(wtTypeIndex==-1)
                     {
                   
                        pStmt.setDouble(10,Double.parseDouble(wtBreakType));  
                        pStmt.setInt(11,1000000);
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
                        /* if(((String)wtBreakList.get(k)).startsWith("FS")||((String)wtBreakList.get(k)).startsWith("SS")
                         ||((String)wtBreakList.get(k)).startsWith("BAF")||((String)wtBreakList.get(k)).startsWith("CAF")
                         ||((String)wtBreakList.get(k)).startsWith("CSF")||((String)wtBreakList.get(k)).startsWith("SURCHARGE")||((String)wtBreakList.get(k)).startsWith("PSS"))
                         {*/
                    	/*if(!("A FREIGHT RATE".equals((String)wtDescList.get(k))))
                        {
                          pStmt.setInt          (10,0);                              
                          pStmt.setInt          (11,0);  
                        }
                        else
                        {*/
                          //pStmt.setInt(10,new Integer((String)wtBreakList.get(k)).intValue());
                          //pStmt.setDouble(11,new Integer((String)wtBreakList.get(k+1)).intValue());
                    	  
                    	  if("SLAB".equalsIgnoreCase(frgtWtBreak)&& wtTypeIndex==2)
                    	  {	
                    		  pStmt.setInt(10,0);
                    		  pStmt.setDouble(11,Double.parseDouble(tt1));
                    	  }else{
                    		  pStmt.setDouble(10,Double.parseDouble(wtBreakType));
                          	  pStmt.setDouble(11,Double.parseDouble(tt1));
                        }
                    	  
                       // }
                      }
                    }
                 
                }
           
                  pStmt.setString		(12,flatRatesDOB.getNotes());
                  //pStmt.setInt		( 13,k);
                  //@@Modified by Kameswari for the issue
                 /* if(  "LIST".equalsIgnoreCase(wtBreak)&&"1".equalsIgnoreCase(shipmentMode))
                  {
                  if(k!=chargeRateListSize-1)
                  pStmt.setInt		( 13,k+1);
                  else
                   pStmt.setInt		( 13,0);
                  }
                  else*/
                  pStmt.setInt		( 13,k);
                  //@@issue
                  pStmt.setString		(14,flatRatesDOB.getCarrierId());
                 if("Both".equalsIgnoreCase(rateTypeList.get(k)+""))
                 {
                    pStmt.setString       (15,(String)slabFlatList.get(k));
                 }
                  else
                    pStmt.setString       (15,flatRatesDOB.getChargeRateIndicator());
                    
                  pStmt.setTimestamp    (16,rateDOB.getCreatedTime());
                  pStmt.setString		(17,flatRatesDOB.getOverPivot());
                  pStmt.setTimestamp(18,flatRatesDOB.getEffDate());
                  if(flatRatesDOB.getValidUpto()!=null )
                    pStmt.setTimestamp(19,flatRatesDOB.getValidUpto());
                  else
                    pStmt.setNull(19,Types.DATE);
                  tmpDensRatio = flatRatesDOB.getDensityRatio().split(",");
                  //pStmt.setString(20,flatRatesDOB.getDensityRatio());
                  pStmt.setString(20,tmpDensRatio[i]); // Added by Gowtham.
                /*  if("FSBASIC".equalsIgnoreCase((String)wtBreakList.get(k))||"FSMIN".equalsIgnoreCase((String)wtBreakList.get(k))
                  ||"FSKG".equalsIgnoreCase((String)wtBreakList.get(k)))*/
              
                  /* if(((String)wtBreakList.get(k)).startsWith("FS"))
                  {
                     pStmt.setString(4,	"SCH");
                     pStmt.setString(21,"FUEL SURCHARGE");
                  }
                  else  if("SSBASIC".equalsIgnoreCase((String)wtBreakList.get(k))||"SSMIN".equalsIgnoreCase((String)wtBreakList.get(k))
                  ||"SSKG".equalsIgnoreCase((String)wtBreakList.get(k)))
                  else if(((String)wtBreakList.get(k)).startsWith("SS"))
                  {
                     pStmt.setString(4,	"SCH");
                     pStmt.setString(21,"SECURITY SURCHARGE");
                  }
                  else  if(((String)wtBreakList.get(k)).startsWith("BAF"))
                 {
                   pStmt.setString(4,	"SCH");
                   pStmt.setString(21,"B.A.F");
                 }
                  // else  if("CAFMIN".equalsIgnoreCase((String)wtBreakList.get(k))||"CAF%".equalsIgnoreCase((String)wtBreakList.get(k)))
                   else  if(((String)wtBreakList.get(k)).startsWith("CAF"))
                   {
                     pStmt.setString(4,	"SCH");
                     pStmt.setString(21,"C.A.F%");
                   }
                   //else  if("CSF".equalsIgnoreCase((String)wtBreakList.get(k)))
                    else  if(((String)wtBreakList.get(k)).startsWith("CSF"))
                   {
                     pStmt.setString(4,	"SCH");
                     pStmt.setString(21,"C.S.F");
                   }
                   //else  if("SURCHARGE".equalsIgnoreCase((String)wtBreakList.get(k)))
                    else  if(((String)wtBreakList.get(k)).startsWith("SURCHARGE"))
                   {
                     pStmt.setString(4,	"SCH");
                     pStmt.setString(21,"SURCHARGE");
                   }
                    else  if(((String)wtBreakList.get(k)).startsWith("PSS"))
                   {
                     pStmt.setString(4,	"SCH");
                     pStmt.setString(21,"C.P.S.S");
                   }
                   else if (((String)wtBreakList.get(k)).endsWith("BAF"))
                   {
                      pStmt.setString(4,	"SCH");
                      pStmt.setString(21,"BAF");
                   }
                   else if (((String)wtBreakList.get(k)).endsWith("CAF"))
                   {
                     pStmt.setString(4,	"SCH");
                      pStmt.setString(21,"CAF%");
                   }
                  else if (((String)wtBreakList.get(k)).endsWith("CSF"))
                  {
                     
                     pStmt.setString(4,	"SCH");
                     pStmt.setString(21,"CSF");
                  }
                  else if (((String)wtBreakList.get(k)).endsWith("PSS"))
                  {
                     pStmt.setString(4,	"SCH");
                      pStmt.setString(21,"PSS");
                  }*/
                 //System.out.println("wtDescList values in DAO--->"+wtDescList.get(k));
                 //System.out.println("wtBreakList values in DAO--->"+wtBreakList.get(k));
                 if(!("A FREIGHT RATE".equals((String)wtDescList.get(k))))
                 {
                	 pStmt.setString(4,	"SCH");
                	 pStmt.setString(21,((String)wtDescList.get(k)));
                	
                 }else 
                 {
                   pStmt.setString(4,flatRatesDOB.getServiceLevel());
                   pStmt.setString(21,"A FREIGHT RATE");
                 }
                   pStmt.setString(23,flatRatesDOB.getExtNotes()); //Modified by Mohan for Issue No.219976 on 08-10-2010
                   if(i>0)
                   pStmt.setString(24,(surChargeCurrList.get(tmpSchCurrCnt)).toString()); // Added by Gowtham for Buyrate Upload Issue. 
                   else
                   pStmt.setString(24,(surChargeCurrList.get(k)).toString());
                   if(!("A FREIGHT RATE".equals((String)wtDescList.get(k))))
                   {
                	   pStmt.setString(25,surChargeId);
                   }else
                	   pStmt.setString(25,"");

                   pStmt.addBatch();
                 // pStmt.execute();
                  if(oldbuyrateid!=null&&oldbuyrateid.trim().length()>0&&"2".equalsIgnoreCase(rateDOB.getShipmentMode())  && "LIST".equalsIgnoreCase(wtBreak))
                  {
                     pStmtQuote      = connection.prepareStatement(quote_qry);// added by VLAKSHMI for issue 146968 on 10/12/2008
                     pstmtQmsQuoteInsert = connection.prepareStatement(qmsQuoteInsert);// added by VLAKSHMI for issue 146968 on 10/12/2008
                     pstmtSelectQuoteId=connection.prepareStatement(selectquote_qry);
                     pstmtQmsQuoteupdate = connection.prepareStatement(updatequote_qry);
                  //   pstmtQmsQuoteupdate.clearParameters();
                    //  pStmtQuote.clearParameters();
                   //  pstmtSelectQuoteId.clearParameters();
                    //  pstmtQmsQuoteInsert.clearParameters();
                     pstmtSelectQuoteId.setString(1,oldbuyrateid);
                     pstmtSelectQuoteId.setInt(2,Integer.parseInt(oldlaneno));
                     // pstmtSelectQuoteId.setString(3,flatRatesDOB.getWtBreakSlab());
                    // pstmtSelectQuoteId.setDouble(4,flatRatesDOB.getChargeRate());
                     // pstmtSelectQuoteId.setNull(3,Types.INTEGER);
                      pstmtSelectQuoteId.setString(3,(String)wtBreakList.get(k));
                     pstmtSelectQuoteId.setDouble(4,Double.parseDouble((String)chargeRateList.get(k)));

                      rsSelectQuoteId   = pstmtSelectQuoteId.executeQuery();
			              String changedesc = (String)wtBreakList.get(k)+","+flatRatesDOB.getOrigin()+"-"+flatRatesDOB.getDestination()+", Sea Freight Rates and Surcharges";
                   while(rsSelectQuoteId.next())
                   {
                    pstmtQmsQuoteupdate.clearParameters();
                     if(rsSelectQuoteId.getString("CHANGEDESC").equalsIgnoreCase(changedesc))
                     {
                        pstmtQmsQuoteupdate.setString(1,changedesc);
                        pstmtQmsQuoteupdate.setInt(2,versionno+1);
                        pstmtQmsQuoteupdate.setString(3,rsSelectQuoteId.getString("QUOTE_ID"));
           
                     
                     
                     }
                     else
                     {
                       // pstmtQmsQuoteupdate.setString(1,flatRatesDOB.getWtBreakSlab()+","+changedesc);
                       pstmtQmsQuoteupdate.setString(1,(String)wtBreakList.get(k)+","+changedesc);
                        pstmtQmsQuoteupdate.setInt(2,versionno+1);
                        pstmtQmsQuoteupdate.setString(3,rsSelectQuoteId.getString("QUOTE_ID"));
              
                   }
                    // pstmtQmsQuoteupdate.addBatch();
                    pstmtQmsQuoteupdate.execute();
                     }
                   
                     pStmtQuote.setString(1,oldbuyrateid);
                     pStmtQuote.setInt(2,Integer.parseInt(oldlaneno));
                     //pStmtQuote.setString(3,flatRatesDOB.getWtBreakSlab());
                     //pStmtQuote.setDouble(4,flatRatesDOB.getChargeRate());
                      pStmtQuote.setString(3,(String)wtBreakList.get(k));
                     pStmtQuote.setDouble(4,Double.parseDouble((String)chargeRateList.get(k)));

                    // pStmtQuote.setInt(6,prevoiusQuotid);
                   rsQuote= pStmtQuote.executeQuery();
                   pStmtCount = connection.prepareStatement(countqry); // Added by Gowtham on 04Feb2011 for Loop Leakages.  
                   while(rsQuote.next())
                    {
                           //String quoteWtBrk=rsQuote.getString("BREAK_POINT");
                         // int QuoteId=rsQuote.getInt("QUOTEID");
                       //pStmtCount = connection.prepareStatement(countqry); //Commented by Gowtham on 04Feb2011 for Loop Leakages.
                    	 pStmtCount.setString(1,rsQuote.getString("QUOTEID"));
                      rsCount  = pStmtCount.executeQuery();
                      
                     pstmtQmsQuoteInsert.clearParameters();
                       if(rsCount.next()&&rsCount.getInt(1)==0)
                       {
                  
                        
                         pstmtQmsQuoteInsert.setInt(1,Integer.parseInt(rsQuote.getString("QUOTEID")));
                      //pstmtQmsQuoteInsert.setInt(2,Integer.parseInt(buyRateID));
                         pstmtQmsQuoteInsert.setInt(2,Integer.parseInt(oldbuyrateid));
                       // pstmtQmsQuoteInsert.setInt(3,i); commented by VALSKHMI for issue 146968 
                         pstmtQmsQuoteInsert.setInt(3,Integer.parseInt(oldlaneno));
                         pstmtQmsQuoteInsert.setString(4,"BR");
                         pstmtQmsQuoteInsert.setString(5,(String)wtBreakList.get(k)+","+flatRatesDOB.getOrigin()+"-"+flatRatesDOB.getDestination()+","+" Sea Freight Rates and Surcharges");
                         pstmtQmsQuoteInsert.setInt(6,Integer.parseInt(oldbuyrateid));
                         pstmtQmsQuoteInsert.setInt(7,Integer.parseInt(oldlaneno));
                         pstmtQmsQuoteInsert.setInt(8,versionno);
                         pstmtQmsQuoteInsert.setInt(9,versionno+1);
                        /// pstmtQmsQuoteInsert.addBatch();
                         count2++;
                         pstmtQmsQuoteInsert.execute();
                         updateQuoteCount++;
                       } 
                       pStmtCount.clearParameters();
                    }
                     
                  /* pstmtSelectQuoteId.clearParameters();;
                     pstmtSelectQuoteId.setString(1,flatRatesDOB.getBuyrateId());
                     pstmtSelectQuoteId.setInt(2,Integer.parseInt(oldlaneno));
                      pstmtSelectQuoteId.setString(3,(String)wtBreakList.get(k));
                       pstmtSelectQuoteId.setDouble(4,Double.parseDouble((String)chargeRateList.get(k)));

                     // pstmtSelectQuoteId.setNull(3,Types.INTEGER);
                      rsSelectQuoteId   = pstmtSelectQuoteId.executeQuery();
			               changedesc = (String)wtBreakList.get(k)+","+flatRatesDOB.getOrigin()+"-"+flatRatesDOB.getDestination()+", Sea Freight Rates and Surcharges";
                   while(rsSelectQuoteId.next())
                   {
                    pstmtQmsQuoteupdate.clearParameters();
                     if(rsSelectQuoteId.getString("CHANGEDESC").equalsIgnoreCase(changedesc))
                     {
                        pstmtQmsQuoteupdate.setString(1,changedesc);
                        pstmtQmsQuoteupdate.setInt(2,versionno+1);
                        pstmtQmsQuoteupdate.setString(3,rsSelectQuoteId.getString("QUOTE_ID"));
                     }
                     else
                     {
                        pstmtQmsQuoteupdate.setString(1,(String)wtBreakList.get(k)+","+changedesc);
                        pstmtQmsQuoteupdate.setInt(2,versionno+1);
                        pstmtQmsQuoteupdate.setString(3,rsSelectQuoteId.getString("QUOTE_ID"));
                   }
                     pstmtQmsQuoteupdate.addBatch();
                      }*/
     }

                  flagDtl = true;
                  
                  if(k==0 && !"2".equalsIgnoreCase(shipmentMode))
                  {
                    freqArray   =   flatRatesDOB.getFrequency().split(",");
                   
                    if(freqArray!=null)
                    {
                     int freqArrLen	=	freqArray.length;
                      for(int z=0;z<freqArrLen;z++)
                      {
                      pStmtFreq.clearParameters();
                       if(oldbuyrateid!=null&&oldbuyrateid.trim().length()>0)
                       {
                          pStmtFreq.setLong(1,Long.parseLong(oldbuyrateid));
                          pStmtFreq.setString(2,oldlaneno);
                          pStmtFreq.setInt(3,Integer.parseInt(freqArray[z]));
                           pStmtFreq.setInt(4,versionno+1);
                       }
                       else
                       {
                          pStmtFreq.setLong(1,Long.parseLong(buyRateID));
                          pStmtFreq.setInt(2,laneno);
                           pStmtFreq.setInt(3,Integer.parseInt(freqArray[z]));
                          pStmtFreq.setInt(4,1);
                        
                       }
                        //pStmtFreq.addBatch();
                        pStmtFreq.execute();
                      }
                    }
                  }
                  if(rsSelectQuoteId!=null)
                  {
                    rsSelectQuoteId.close();
                  }
                   if(pstmtSelectQuoteId!=null)
                  {
                    pstmtSelectQuoteId.close();
                  }
                  
                  if(rsQuote!=null)
                  {
                     rsQuote.close();
                  }
                   if(pStmtQuote!=null)
                  {
                     pStmtQuote.close();
                  }
                  
                   if(rsCount!=null)
                  {
                     rsCount.close();
                  }
                  
                   if(pStmtCount!=null)
                  {
                     pStmtCount.close();
                  }
              }
            }          cStmt1.clearParameters();
        
                 
                         //cStmt1.execute();
                          cStmt3.clearParameters();
                
                        //  flatRatesDOB =  (FlatRatesDOB)list.get(i);
                    //To Update the buyrates and sellrates into RSR_Acceptence module
                    ////condition added  by VLAKSHMI for issue 146968 on 23/12/2008
                     if("2".equalsIgnoreCase(shipmentMode) && "FCL".equalsIgnoreCase(rateDOB.getConsoleType()) && "LIST".equalsIgnoreCase(rateDOB.getWeightBreak().toUpperCase()))
                     {
                           if(flagDtl&&count2!=0&&oldbuyrateid!=null&&oldbuyrateid.trim().length()>0 )
                        {
                            cStmt3.setDouble(1,Double.parseDouble(oldbuyrateid)); //added by VLAKSHMI for issue 146968 on 23/12/2008
                            cStmt3.setInt(2,versionno+1);
                            cStmt3.setString(3,oldlaneno);
                            cStmt3.addBatch();
                       }
                      
                       else if (count2==0&&"2".equalsIgnoreCase(returnStr[0])&&oldbuyrateid!=null&&oldbuyrateid.trim().length()>0)
                       {
                            cStmt3.setDouble(1,Double.parseDouble(oldbuyrateid)); //added by VLAKSHMI for issue 146968 on 23/12/2008
                            cStmt3.setInt(2,newversionno);
                            cStmt3.setString(3,oldlaneno);
                            cStmt3.addBatch();
                       }
                      else  if("3".equalsIgnoreCase(returnStr[0])&&oldbuyrateid!=null&&oldbuyrateid.trim().length()>0)
                    {
                        if(count2==0)
                        {
                         cStmt1.setString(1,"BR");
                         cStmt1.setString(2,flatRatesDOB.getOrigin());
                         cStmt1.setString(3,flatRatesDOB.getDestination());
                         cStmt1.setString(4,flatRatesDOB.getServiceLevel());
                         cStmt1.setString(5,flatRatesDOB.getCarrierId());
                         cStmt1.setTimestamp(6,flatRatesDOB.getValidUpto());
                         cStmt1.setString(7,freqStr);
                         cStmt1.setString(8,flatRatesDOB.getTransittime());
                         cStmt1.setString(9,oldbuyrateid);
                         cStmt1.setNull(10,Types.VARCHAR);
                         cStmt1.setNull(11,Types.VARCHAR);
                         cStmt1.setInt(12,versionno+1);
                         cStmt1.setInt(13,Integer.parseInt(oldlaneno));
                           cStmt1.setString(14,terminalId);
                          cStmt1.addBatch();
                        }
                      }
                     } 
                     ////condition added  by VLAKSHMI for issue 146968 on 23/12/2008 becoz in sellratesmstr_acc_proc(?)} 
                     //To Update the buyrates and sellrates into RSR_Acceptence module   
                     //in up we are modifying  and inserting the qms_update table for fcl,2,list
                     //in qms_update_new_quote pkage both insertin,updating the qms_update table and Update the buyrates and sellrates into RSR_Acceptence module    both is happeneing...so no need
                     else
                     {
                    if("2".equalsIgnoreCase(returnStr[0])&&oldbuyrateid!=null&&oldbuyrateid.trim().length()>0)
                    {
                           
                             cStmt3.setDouble(1,Double.parseDouble(oldbuyrateid));//added by VLAKSHMI for issue 146968 on 23/12/2008
                             cStmt3.setInt(2,newversionno);//added by VLAKSHMI for issue 146968 on 23/12/2008 
                             cStmt3.setString(3,oldlaneno);
                             cStmt3.addBatch();
                    }
                    else if("3".equalsIgnoreCase(returnStr[0])&&oldbuyrateid!=null&&oldbuyrateid.trim().length()>0)
                    {
                         cStmt1.setString(1,"BR");
                         cStmt1.setString(2,flatRatesDOB.getOrigin());
                         cStmt1.setString(3,flatRatesDOB.getDestination());
                         cStmt1.setString(4,flatRatesDOB.getServiceLevel());
                         cStmt1.setString(5,flatRatesDOB.getCarrierId());
                         cStmt1.setTimestamp(6,flatRatesDOB.getValidUpto());
                         cStmt1.setString(7,flatRatesDOB.getFrequency());
                         cStmt1.setString(8,flatRatesDOB.getTransittime());
                         cStmt1.setString(9,oldbuyrateid);
                          cStmt1.setNull(10,Types.VARCHAR);
                          cStmt1.setNull(11,Types.VARCHAR);
                         cStmt1.setInt(12,versionno+1);
                         cStmt1.setInt(13,Integer.parseInt(oldlaneno));
                           cStmt1.setString(14,terminalId);
                              
                         cStmt1.addBatch();
                    }
              }
                            
           if(i==50||i==(listSize-1)||i==50*count)
           {
         
            
              if(pStmt2!=null)
              {
                pStmt2.executeBatch();
           
              }
         if(pStmt!=null)
              {
               pStmt.executeBatch();
              }
             /* if(pStmtFreq!=null)
              {
               pStmtFreq.executeBatch();
              }*/
           /*  if(pstmtQmsQuoteupdate!=null)
               {
                    pstmtQmsQuoteupdate.executeBatch();

               }*/
              /* if(pstmtQmsQuoteInsert!=null)
               {
                  pstmtQmsQuoteInsert.executeBatch();

               } */
              
                 if(cStmt1!=null)
               {
                 cStmt1.executeBatch();
               }
                 if(cStmt3!=null)
               {
                 cStmt3.executeBatch();
               }
              
                /* if(cStmt3!=null)
               {
                 cStmt3.executeBatch();
               }*/
                count++;
              
           }
                /* if(rsSelectQuoteId!=null)
                 {
                   rsSelectQuoteId.close();
                 }
                 if(rsQuote!=null)
                 {
                   rsQuote.close();
                 }*/
           //@@ Added by subrahmanyam for the wpbn id: 196113 on 01/Feb/10           
          // logger.info("updateCount: "+updateQuoteCount);
           String 	noRatesChangeQuotes	=	 " SELECT DISTINCT QR.QUOTE_ID QUOTEID FROM QMS_QUOTE_RATES QR WHERE QR.BUYRATE_ID=? "+
           				 " AND QR.RATE_LANE_NO=? AND QR.SELL_BUY_FLAG='BR' AND  QR.QUOTE_ID IN(SELECT QM.ID "+
           				 " FROM QMS_QUOTE_MASTER QM WHERE  QM.ID=QR.QUOTE_ID AND QM.ACTIVE_FLAG='A')";
           String noRatesChangeUpdQry	=	"SELECT COUNT(*) FROM QMS_QUOTES_UPDATED WHERE QUOTE_ID=?"+
           									" AND SELL_BUY_FLAG='BR' AND CONFIRM_FLAG IS NULL";
           String noRateUpdQuote		=	"UPDATE QMS_QUOTES_UPDATED SET CHANGEDESC= ?,NEW_VERSION_NO=? WHERE QUOTE_ID=? AND SELL_BUY_FLAG='BR'";
       
           String   noChangeUpdQuoteInsert= " INSERT INTO QMS_QUOTES_UPDATED (QUOTE_ID,NEW_BUYCHARGE_ID, "+
           								  "  NEW_LANE_NO, SELL_BUY_FLAG, CHANGEDESC, OLD_BUYCHARGE_ID, "+
           								  " OLD_LANE_NO,OLD_VERSION_NO,NEW_VERSION_NO) "+
           								  " Values(?,?,?,?,?,?,?,?,?) ";
 
           if(updateQuoteCount==0 && (returnStr!=null&&returnStr.length>1 && "2".equalsIgnoreCase(returnStr[0])))
           {
        	   try
        	   {
        	   //logger.info("Entered in the loop for the norates change....");
        	      pStmtNoChangeRate       = connection.prepareStatement(noChangeRateQry1);
        	      pStmtNoChangeRate.setInt(1, Integer.parseInt(oldbuyrateid));
        	      pStmtNoChangeRate.setInt(2, Integer.parseInt(oldlaneno));
        	      pStmtNoChangeRate.setInt(3, versionno+1);
        	      pStmtNoChangeRate.setInt(4, versionno);
        	      rsNoChangeRate	=	 pStmtNoChangeRate.executeQuery();
        	      if(rsNoChangeRate.next() && rsNoChangeRate.getInt(1)>0)
        	      {
        	    	 // logger.info("Entered in the loop for the norates change & Adding extra charges....");
        	    	  pStmtNoChangeRateQuotes	=	connection.prepareStatement(noRatesChangeQuotes);
        	    	  
        	    	  pStmtNoChangeRateQuotes.setInt(1, Integer.parseInt(oldbuyrateid));
        	    	  pStmtNoChangeRateQuotes.setInt(2, Integer.parseInt(oldlaneno));
        	    	  rsNoChangeRateQuotes	=	pStmtNoChangeRateQuotes.executeQuery();
        	    	  pStmtNoChangeRateQuotes1	= connection.prepareStatement(noRatesChangeUpdQry); // Added by Gowtham on 04Feb2011 for Loop Leakages.
        	    	  while(rsNoChangeRateQuotes.next())
        	    	  {
        	    		  //pStmtNoChangeRateQuotes1	= connection.prepareStatement(noRatesChangeUpdQry); Commented by Gowtham on 04Feb2011 for Loop Leakages.
        	    		  pStmtNoChangeRateQuotes1.setInt(1, rsNoChangeRateQuotes.getInt("QUOTEID"));
        	    		  rsNoChangeRateQuotes1	=	pStmtNoChangeRateQuotes1.executeQuery();
        	    		  if(rsNoChangeRateQuotes1.next() && rsNoChangeRateQuotes1.getInt(1)!=0)
        	    		  {
        	    			  pStmtNoChangeRateQuotes2 =	connection.prepareStatement(noRateUpdQuote);
        	    			  pStmtNoChangeRateQuotes2.setString(1, flatRatesDOB.getOrigin()+"-"+flatRatesDOB.getDestination()+",FreightRate And SurCharges");
        	    			  pStmtNoChangeRateQuotes2.setInt(2, versionno+1);
        	    			  pStmtNoChangeRateQuotes2.setInt(3, rsNoChangeRateQuotes.getInt("QUOTEID"));
        	    			  pStmtNoChangeRateQuotes2.executeUpdate();
        	    			  
        	    		  }
        	    		  else
        	    		  {
        	    			  pStmtNoChangeUpdInsert	=	connection.prepareStatement(noChangeUpdQuoteInsert);
        	    			  pStmtNoChangeUpdInsert.setInt(1,rsNoChangeRateQuotes.getInt("QUOTEID"));
        	    			  pStmtNoChangeUpdInsert.setInt(2, Integer.parseInt(oldbuyrateid));
        	    			  pStmtNoChangeUpdInsert.setInt(3, Integer.parseInt(oldlaneno));
        	    			  pStmtNoChangeUpdInsert.setString(4, "BR");
        	    			  pStmtNoChangeUpdInsert.setString(5, flatRatesDOB.getOrigin()+"-"+flatRatesDOB.getDestination()+",FreightRate And SurCharges");
           	    			  pStmtNoChangeUpdInsert.setInt(6, Integer.parseInt(oldbuyrateid));
        	    			  pStmtNoChangeUpdInsert.setInt(7, Integer.parseInt(oldlaneno));
        	    			  pStmtNoChangeUpdInsert.setInt(8, versionno);
        	    			  pStmtNoChangeUpdInsert.setInt(9, versionno+1);
        	    			  	
        	    			  pStmtNoChangeUpdInsert.execute();
        	    			 
        	    		  }
        	    		  
        	    	  }
        	      }
        	      else
        	      {
        	    	 // logger.info("Entered in the loop for the norates change in else....");
            	      pStmtNoChangeRate1       = connection.prepareStatement(noChangeRateQry1);
            	      pStmtNoChangeRate1.setInt(1, Integer.parseInt(oldbuyrateid));
            	      pStmtNoChangeRate1.setInt(2, Integer.parseInt(oldlaneno));
            	      pStmtNoChangeRate1.setInt(3, versionno);
            	      pStmtNoChangeRate1.setInt(4, versionno+1);
            	      rsNoChangeRate1	=	 pStmtNoChangeRate1.executeQuery();
            	      if(rsNoChangeRate1.next() && rsNoChangeRate1.getInt(1)>0)
            	      {
            	    	  //logger.info("Entered in the loop for the norates change & removing extra charges....");
            	    	  pStmtNoChangeRateQuotes	=	connection.prepareStatement(noRatesChangeQuotes);
            	    	  
            	    	  pStmtNoChangeRateQuotes.setInt(1, Integer.parseInt(oldbuyrateid));
            	    	  pStmtNoChangeRateQuotes.setInt(2, Integer.parseInt(oldlaneno));
            	    	  rsNoChangeRateQuotes	=	pStmtNoChangeRateQuotes.executeQuery();
            	    	  pStmtNoChangeRateQuotes1	= connection.prepareStatement(noRatesChangeUpdQry); // Added by Gowtham on 04Feb2011 for Loop Leakages.
            	    	  while(rsNoChangeRateQuotes.next())
            	    	  {
            	    		 // pStmtNoChangeRateQuotes1	= connection.prepareStatement(noRatesChangeUpdQry); Commented by Gowtham on 04Feb2011 for Loop Leakages.
            	    		  pStmtNoChangeRateQuotes1.setInt(1, rsNoChangeRateQuotes.getInt("QUOTEID"));
            	    		  rsNoChangeRateQuotes1	=	pStmtNoChangeRateQuotes1.executeQuery();
            	    		  if(rsNoChangeRateQuotes1.next() && rsNoChangeRateQuotes1.getInt(1)!=0)
            	    		  {
            	    			  pStmtNoChangeRateQuotes2 =	connection.prepareStatement(noRateUpdQuote);
            	    			  pStmtNoChangeRateQuotes2.setString(1, flatRatesDOB.getOrigin()+"-"+flatRatesDOB.getDestination()+",FreightRate And SurCharges");
            	    			  pStmtNoChangeRateQuotes2.setInt(2, versionno+1);
            	    			  pStmtNoChangeRateQuotes2.setInt(3, rsNoChangeRateQuotes.getInt("QUOTEID"));
            	    			  pStmtNoChangeRateQuotes2.executeUpdate();
            	    			  
            	    		  }
            	    		  else
            	    		  {
            	    			  pStmtNoChangeUpdInsert	=	connection.prepareStatement(noChangeUpdQuoteInsert);
            	    			  pStmtNoChangeUpdInsert.setInt(1,rsNoChangeRateQuotes.getInt("QUOTEID"));
            	    			  pStmtNoChangeUpdInsert.setInt(2, Integer.parseInt(oldbuyrateid));
            	    			  pStmtNoChangeUpdInsert.setInt(3, Integer.parseInt(oldlaneno));
            	    			  pStmtNoChangeUpdInsert.setString(4, "BR");
            	    			  pStmtNoChangeUpdInsert.setString(5, flatRatesDOB.getOrigin()+"-"+flatRatesDOB.getDestination()+",FreightRate And SurCharges");
               	    			  pStmtNoChangeUpdInsert.setInt(6, Integer.parseInt(oldbuyrateid));
            	    			  pStmtNoChangeUpdInsert.setInt(7, Integer.parseInt(oldlaneno));
            	    			  pStmtNoChangeUpdInsert.setInt(8, versionno);
            	    			  pStmtNoChangeUpdInsert.setInt(9, versionno+1);
            	    			  	
            	    			  pStmtNoChangeUpdInsert.execute();
            	    			 
            	    		  }
            	    		  
            	    	  }
            	      }
            	      
        	      }
        	   }catch(SQLException e)
        	   {
        	          e.printStackTrace();
        	          logger.error("Error in Rates DAO While updating the quotes for the unChangedRates and extra charges:  "+ e.getMessage());           
        	    	  throw new Exception ("Error in Rates DAO While updating the quotes for the unChangedRates and extra charges:");

        	   }finally
        	   {
        		   if(pStmtNoChangeRate!=null)
        			   pStmtNoChangeRate.close();
        		   if(rsNoChangeRate!=null)
        			   rsNoChangeRate.close();
        		   if(pStmtNoChangeRateQuotes!=null)
        			   pStmtNoChangeRateQuotes.close();
        		   if(rsNoChangeRateQuotes!=null)
        			   rsNoChangeRateQuotes.close();
        		   if(pStmtNoChangeRateQuotes1!=null)
        			   pStmtNoChangeRateQuotes1.close();
        		   if(rsNoChangeRateQuotes1!=null)
        			   rsNoChangeRateQuotes1.close();
        		   if(pStmtNoChangeRateQuotes2!=null)
        			   pStmtNoChangeRateQuotes2.close();
        		   if(pStmtNoChangeUpdInsert!=null)
        			   pStmtNoChangeUpdInsert.close();
        		   if(pStmtNoChangeRate1!=null)
        			   pStmtNoChangeRate1.close();
        		   if(rsNoChangeRate1!=null)
        			   rsNoChangeRate1.close();
        		   
        		   
        		   
        		   
        		   
        		   
        	   }
           }
           //@@ Ended by subrahmanyam for the wpbn id: 196113 on 01/Feb/10           
         }
       
         /*cStmt.executeBatch();
         pStmt.executeBatch();
         pStmtFreq.executeBatch();*/
                  //System.out.println("after inserting::::");
                
         ConnectionUtil.closeStatement(pStmt);
          
          pStmt       = connection.prepareStatement(log_qry);      
          
          if(cStmt!=null)
          {cStmt.close();}
          
                    //To Update the buyrates and sellrates into RSR_Acceptence module
        
       
                    //end @@sekhar
        
          if(pStmt!=null)
            pStmt.close();
          //System.out.println("end create");
         }catch(SQLException sq)
         {
           sq.printStackTrace();
            logger.error("Error in Rates DAO dhl "+ sq.getMessage());           
    	      throw new Exception ("Problem while fetching the Details <BR>");
         }catch(Exception e)
         {
           e.printStackTrace();
		         throw new Exception ("Problem while fetching the Details <BR>");
         }
         finally
         {
           try{
           if(rs!=null)
            {rs.close();}
             if(rs1!=null)// added by VALKSHMI for connection leakage
            {rs1.close();}
            if(cStmt!=null)
            {cStmt.close();}   
               if(cStmt3!=null)
            {cStmt3.close();}     
               if(cStmt1!=null)
            {cStmt1.close();}    
     
           if(pStmt!=null)
             pStmt.close(); 
               if(pStmt2!=null)// added by VALKSHMI for connection leakage
             pStmt2.close(); 
              if(pStmt1!=null)// added by VALKSHMI for connection leakage
             pStmt1.close(); 
              if(pstmtQmsQuoteupdate!=null)// added by VALKSHMI for connection leakage
             pstmtQmsQuoteupdate.close(); 
              if(pstmtQmsQuoteInsert!=null)// added by VALKSHMI for connection leakage
             pstmtQmsQuoteInsert.close(); 
              if(pStmtFreq!=null)//@@Added now by Kameswari
             pStmtFreq.close(); 
              ConnectionUtil.closeStatement(pStmtb,rs);  // Added by Gowtham
              ConnectionUtil.closePreparedStatement(pStmtCount,rsCount);// Added by Dilip for PMD Correction on 22/09/2015
              ConnectionUtil.closePreparedStatement(pStmtQuote,rsQuote);// Added by Dilip for PMD Correction on 22/09/2015
              ConnectionUtil.closePreparedStatement(pstmtSelectQuoteId,rsSelectQuoteId);// Added by Dilip for PMD Correction on 22/09/2015
             /*if(connection!=null)
             connection.close();*/
           }catch(SQLException e)
           {
                   throw new Exception ("Problem while fetching the Details <BR>");
           }
           wtDescList      		= null;
           surChargeCurrList	= null;
           wtBrkTypeList	  	= null;
           rateTypeList	  		= null;
           wtList          		= null;
           chargeList      		= null;
           wtBreakList     		= null;
           chargeRateList  		= null;
           slabFlatList    		= null;
           freqArray       		= null;
           returnStr       		= null;
         }
         return "1";
   }  
   
   public void  upLoadModifyFlatRates(ArrayList list,ESupplyGlobalParameters loginBean)throws Exception
	{

	    OperationsImpl        impl				    =		null;
      Connection            connection			=		null;
      PreparedStatement     pStmt				    =		null;
      PreparedStatement     pStmt1				  =		null;
      PreparedStatement     pStmt2				  =		null;
      PreparedStatement     pStmt3				  =		null;
      ResultSet				      rs    					= 	null;
      String				        sql_query			  =		null;	
      String				        update_qry			=		null;	
      String				        sql_query1			=		null;	
      String				        seq_qry				  =		null;	
      String				        master_qry			=		null;	
      String				        dtl_qry				  =		null;
      String				        log_qry				  =		null;	
      FlatRatesDOB	flatRatesDOB		=		null;  
      String				        shipmentmode		=		null;
      String				        currency			  =		null;
      String				        uom					    =		null;
      String				        weightbreak			=		null;
      String				        weightclass			=		null;
      String				        ratetype			  =		null;
      String				        consoletype			=		null;
      String				        accesslevel			=		null;
      String				        origin				  =		null;
      String				        destination			=		null;
      String				        servicelevel		=		null;
      String				        frequency			  =		null;
      String				        transit_time		=		null;
      String				        weightbreakslab	=		null;
      String                densityRatio    =   null;
      int					          lowerbound			=		0;
      int					          upperbound			=		0;		
      String				    chargerateIndicator	=		null;
      String				        notes				    =		null;
      String				        chargeslab			=		null;
      String				        carrierID			  =		null;
      String				        containertype		=		null;
      String				        buyRateID			  =		null;
      String				        accessLevel			=		null;
      String				        rateDescription =		null;
      Timestamp				      effectiveFrom		=		null;
      Timestamp				      validUpto			  =		null;
      ArrayList             chargeRateList  =   null;
      ArrayList             slabFlatList    = null;


	  try{
      
			 sql_query  =	" SELECT  BUYRATEID,SHIPMENT_MODE ,CURRENCY,CARRIER_ID,UOM ,WEIGHT_BREAK,WEIGHT_CLASS,RATE_TYPE,CONSOLE_TYPE,CREATED_BY , "
							+" LAST_UPDATED_BY,LAST_UPDATED_TSTMP ,DEL_FLAG ,ACCESSLEVEL,TERMINALID FROM QMS_BUYRATES_MASTER WHERE BUYRATEID =? ";	

			seq_qry     = " SELECT BUYRATE_SEQ.NEXTVAL BUYRATEID FROM DUAL  "; 

			update_qry =  "   UPDATE   QMS_BUYRATES_DTL  SET ACTIVEINACTIVE ='I' WHERE BUYRATEID=? AND LANE_NO=?       "	;
			
	
  		master_qry =  " INSERT INTO QMS_BUYRATES_MASTER (SHIPMENT_MODE,  CURRENCY, CARRIER_ID, UOM, WEIGHT_BREAK,  "+
						  " RATE_TYPE, CONSOLE_TYPE, CREATED_BY, LAST_UPDATED_TSTMP, BUYRATEID, WEIGHT_CLASS, ACCESSLEVEL, TERMINALID  )"+
						  " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?) ";

			sql_query1 =  " SELECT  ORIGIN,DESTINATION,SERVICE_LEVEL,FREQUENCY,TRANSIT_TIME,WEIGHT_BREAK_SLAB,LOWERBOUND,UPPERBOUND,CHARGERATE_INDICATOR, "+  
						  " NOTES,CHARGESLAB,CARRIER_ID,CONTAINERTYPE,CREATEDTIME,ACTIVEINACTIVE,EFFECTIVE_FROM,VALID_UPTO,INVALIDATE,ACCESSLEVEL,CHARGERATE,DENSITY_CODE,RATE_DESCRIPTION "+
			              " FROM QMS_BUYRATES_DTL  WHERE BUYRATEID=? AND LANE_NO=? ORDER BY LINE_NO";
						  
			dtl_qry    = " INSERT INTO QMS_BUYRATES_DTL (BUYRATEID ,ORIGIN, DESTINATION, SERVICE_LEVEL,  "
						  +"  FREQUENCY, TRANSIT_TIME, CHARGERATE, WEIGHT_BREAK_SLAB, LANE_NO, LOWERBOUND ,UPPERBOUND  ,"
						  +"   NOTES, LINE_NO,CARRIER_ID,CHARGERATE_INDICATOR,CREATEDTIME ,OVER_PIVOT,EFFECTIVE_FROM, VALID_UPTO,ID,DENSITY_CODE,RATE_DESCRIPTION) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,seq_BUYRATES_DTL.nextval,?,?) ";
						  
			log_qry     =" INSERT INTO FS_USERLOG (LOCATIONID ,USERID ,DOCTYPE ,DOCREFNO ,DOCDATE ,TRANSACTIONTYPE ) VALUES(?,?,?,?,?,?)    "          ;

			 
	         impl		    =	        new OperationsImpl();
           connection	=	        impl.getConnection();
           pStmt      =         connection.prepareStatement(seq_qry);
           rs         =         pStmt.executeQuery();   
           
          if(rs.next())          buyRateID   = rs.getString("BUYRATEID");
 
		      ConnectionUtil.closeStatement(pStmt,rs);
        
          pStmt       = connection.prepareStatement(sql_query);
          pStmt1      = connection.prepareStatement(master_qry);
          pStmt2      = connection.prepareStatement(dtl_qry);
          pStmt3      = connection.prepareStatement(sql_query1);
        
          flatRatesDOB = (FlatRatesDOB)list.get(0);  
        
          pStmt.setString(1,flatRatesDOB.getBuyrateId());
      
    		  rs= pStmt.executeQuery();

         if(rs.next())
         {
            shipmentmode  =  rs.getString("SHIPMENT_MODE") ;
            currency	  =  rs.getString("CURRENCY") ;
            uom			  =  rs.getString("UOM") ;
            weightbreak	  =  rs.getString("WEIGHT_BREAK") ;
            weightclass	  =  rs.getString("WEIGHT_CLASS") ;
            ratetype	  =  rs.getString("RATE_TYPE") ;
            consoletype   =  rs.getString("CONSOLE_TYPE") ;
         }
         
         ConnectionUtil.closeStatement(pStmt,rs);
         
         pStmt      = connection.prepareStatement(update_qry);

        if("OPER_TERMINAL".equalsIgnoreCase(loginBean.getUserLevel()))
        {
          accessLevel="O";
        }else if("HO_TERMINAL".equalsIgnoreCase(loginBean.getUserLevel()))
        {
            accessLevel ="H";
        }else if("ADMN_TERMINAL".equalsIgnoreCase(loginBean.getUserLevel()))
        {
            accessLevel ="A";
        }
			
          pStmt1.setString(1,shipmentmode);
          pStmt1.setString(2,currency);
          pStmt1.setString(3,carrierID);
          pStmt1.setString(4,uom);
          pStmt1.setString(5,weightbreak);
          pStmt1.setString(6,ratetype);
          pStmt1.setString(7,consoletype);
          pStmt1.setString(8,loginBean.getUserId());
          pStmt1.setTimestamp(9,new java.sql.Timestamp((new java.util.Date()).getTime()));
          pStmt1.setString(10,buyRateID); 
          pStmt1.setString(11,weightclass); 
          pStmt1.setString(12,accessLevel); 
          pStmt1.setString(13,loginBean.getTerminalId()); 
					pStmt1.executeUpdate() ;

				  if(pStmt1!= null ) pStmt1.close();
				
					
				  	int listSize	= list.size();
					for(int i=0;i<listSize;i++)
					{
							flatRatesDOB = (FlatRatesDOB)list.get(i); 
              chargeRateList      =  flatRatesDOB.getChargeRateList();  
              slabFlatList        =  flatRatesDOB.getSlabFlatList();
							pStmt.clearParameters() ;					
				      
							pStmt.setString(1,flatRatesDOB.getBuyrateId());
							pStmt.setInt(2,flatRatesDOB.getLaneNo());
							pStmt.executeUpdate();		
              
            
							pStmt3.setString(1,flatRatesDOB.getBuyrateId());
							pStmt3.setInt(2,flatRatesDOB.getLaneNo());
							rs= pStmt3.executeQuery();
							int j=0;	
			        while(rs.next())
							{
								origin				      =	rs.getString("ORIGIN");
								destination			    =	rs.getString("DESTINATION");	
								servicelevel		    =	rs.getString("SERVICE_LEVEL");	
								frequency			      =	rs.getString("FREQUENCY");
								transit_time		    =	rs.getString("TRANSIT_TIME");
								weightbreakslab		  =	rs.getString("WEIGHT_BREAK_SLAB");
								lowerbound			    =	rs.getInt	("LOWERBOUND");
								upperbound			    =	rs.getInt	("UPPERBOUND");
								chargerateIndicator	=	rs.getString("CHARGERATE_INDICATOR");
								notes				        =	rs.getString("NOTES");
								chargeslab			    =	rs.getString("CHARGESLAB");
								carrierID			      =	rs.getString("CARRIER_ID");
								containertype		    =	rs.getString("CONTAINERTYPE");	
								effectiveFrom		    =	rs.getTimestamp("EFFECTIVE_FROM");
								validUpto			      =	rs.getTimestamp("VALID_UPTO");	
                densityRatio        = rs.getString("DENSITY_CODE");	
                rateDescription     = rs.getString("RATE_DESCRIPTION");	//@@Added by Kameswari for Surcharge Enhancement         
									pStmt2.clearParameters() ;
								  pStmt2.setString		(1,	buyRateID);
								  pStmt2.setString		(2,	origin);
								  pStmt2.setString		(3,	destination);
                  if("A FREIGHT RATE".equalsIgnoreCase(rateDescription)||rateDescription==null||rateDescription.trim().length()>0)
                    pStmt2.setString		(4,	servicelevel);
                  else
                    pStmt2.setString		(4,	"SCH");
								  pStmt2.setString		(5,	frequency);
								  pStmt2.setString		(6,	transit_time);
              	  pStmt2.setDouble		(7,	new Double((String)chargeRateList.get(j)).doubleValue());
								  pStmt2.setString		(8,weightbreakslab);
								  pStmt2.setInt       (9,	i);
								  pStmt2.setInt       (10,lowerbound);
								  pStmt2.setInt       (11,upperbound);
								  pStmt2.setString		(12,notes);
								  pStmt2.setInt			  ( 13,j);
								  pStmt2.setString		(14,carrierID);
                  //ss.info(FILE_NAME,"(String)frtHash.get(frtList.get(j))  "+(String)frtHash.get(frtList.get(j)));
							if("Both".equalsIgnoreCase(ratetype))	  
								  pStmt2.setString       (15,(String)slabFlatList.get(j));
							else
                  pStmt2.setString       (15,chargerateIndicator);
								  pStmt2.setTimestamp (16,new java.sql.Timestamp((new java.util.Date()).getTime()));
								  pStmt2.setString		(17,"");
								  pStmt2.setTimestamp	(18,effectiveFrom);
								  pStmt2.setTimestamp	(19,validUpto);
                  pStmt2.setString(20,densityRatio);
                  pStmt2.setString(21,rateDescription);//@@Added by Kameswari for Surcharge Enhancement                 
								  pStmt2.executeUpdate();
									j++;
						  }
							if(rs!=null)rs.close();
					  }					  
					 // pStmt2.executeBatch();
					  //pStmt.executeBatch();
	}catch(SQLException e)
		{
			  e.printStackTrace();
			  				      throw new Exception ("Problem while UPDATING the Details <BR>");
		}catch(Exception e)
		{
			 e.printStackTrace();
			 				      throw new Exception ("Problem while UPDATING the Details <BR>");

		}finally{

			try{
			 if(rs!=null)
				 rs.close();
			 if(pStmt!=null)
				 pStmt.close();
			 if(pStmt1!=null)
				 pStmt1.close();
			 if(pStmt2!=null)
				 pStmt2.close();
			 if(pStmt3!=null)
				 pStmt3.close();
              if(connection!=null)
					connection.close();



			}catch(SQLException e)
			{
				e.printStackTrace();
								      throw new Exception ("Problem while UPDATING the Details <BR>");
			}catch(Exception e)
			{
				      throw new Exception ("Problem while UPDATING the Details <BR>");
			}
	}

}
   public boolean isNumber(String value){
	   
	   try{

		   double d = Double.parseDouble(value);
		   System.out.println("value....."+value);
		   System.out.println("after check."+d);
	   }catch(java.lang.NumberFormatException e){
		   return false;
	   }
	   return true;
   }
}