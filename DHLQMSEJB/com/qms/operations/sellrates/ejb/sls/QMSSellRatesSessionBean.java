

/**
 * @ (#) QMSSellRatesSessionBean.java
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
 * File       : QMSSellRatesSessionBean.java
 * Sub-Module : 
 * Module     : QMS
 * @author    : Madhu.Y,Yuvraj Waghray
 * * @date    : 29-07-2005
 * Modified by      Date     Reason
 */

package com.qms.operations.sellrates.ejb.sls;
import com.foursoft.esupply.common.bean.ESupplyGlobalParameters;
import com.foursoft.esupply.common.java.LookUpBean;
import com.foursoft.esupply.common.util.ConnectionUtil;
//import com.foursoft.esupply.common.util.Logger;
import org.apache.log4j.Logger;
import com.foursoft.etrans.common.util.java.OperationsImpl;
import com.qms.operations.sellrates.dao.QMSSellRatesDAO;
import com.qms.operations.sellrates.ejb.bmp.QMSSellRatesEntityLocal;
import com.qms.operations.sellrates.ejb.bmp.QMSSellRatesEntityLocalHome;
import com.qms.operations.sellrates.ejb.bmp.QMSSellRatesEntityPK;
import com.qms.operations.sellrates.java.QMSBoundryDOB;
import com.qms.operations.sellrates.java.QMSSellRatesDOB;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;
import javax.ejb.EJBException;
import javax.ejb.ObjectNotFoundException;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;
import javax.naming.Context;
import javax.naming.InitialContext;
import java.sql.Connection;
import javax.naming.NamingException;
import javax.sql.DataSource;
import oracle.jdbc.OracleTypes;

public class QMSSellRatesSessionBean implements SessionBean 
{
    public  static final String FILE_NAME       =   "QMSSellRatesSessionBean.java";
    private SessionContext     sessionContext   =   null;
    private InitialContext     ictxt            =   null;    
    private	OperationsImpl	   operationsImpl   =   null;
    private LookUpBean         lookUpBean       =   null;
    private DataSource         dataSource       =   null;
    private static Logger logger = null;
    
    public QMSSellRatesSessionBean()
    {
      logger  = Logger.getLogger(QMSSellRatesSessionBean.class);
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
    operationsImpl = null;
  }

  public void setSessionContext(SessionContext ctx)
  {
    this.sessionContext      =     ctx;
  }
  
  //Serialization of BeanObject is to be done here.....//
    private void writeObject(java.io.ObjectOutputStream out)
      throws java.io.IOException
    {
      //write non-serializable attributes here
  
      out.defaultWriteObject();
    }

    private void readObject(java.io.ObjectInputStream in)
      throws java.io.IOException, ClassNotFoundException
    {
      //read non-serializable attributes here
  
      in.defaultReadObject();
    }
  public ArrayList getSellRatesOfValues(QMSSellRatesDOB sellRatesDob,ESupplyGlobalParameters loginBean,String operation)
  {
      ArrayList                   headerList            =   null;
      QMSSellRatesDAO             sellRatesDao          =   new QMSSellRatesDAO();
      try
      {
          headerList = sellRatesDao.getSellRatesValesOfView(sellRatesDob,loginBean,operation);
      }
      catch(SQLException sqle)
      {
          //Logger.error(FILE_NAME,"SQLEXception in getSellRatesOfValues()-->"+sqle.toString());
          logger.error(FILE_NAME+"SQLEXception in getSellRatesOfValues()-->"+sqle.toString());
          sqle.printStackTrace();
          throw new EJBException(sqle.toString());
      }
      catch(Exception e)
      {
        //Logger.error(FILE_NAME,"Exception in getSellRatesOfValues()-->"+e.toString());
        logger.error(FILE_NAME+"Exception in getSellRatesOfValues()-->"+e.toString());
        e.printStackTrace();
        throw new EJBException(e.toString());
      }
    return headerList;
  }
  public String updateInvalidate(ArrayList valueList)
  {
     QMSSellRatesDAO             sellRatesDao          =   new QMSSellRatesDAO();
     String                      massege               =    null;
     try
      {
          massege = sellRatesDao.updateInvalidateStatus(valueList);
      }
      catch(SQLException sqle)
      {
          //Logger.error(FILE_NAME,"SQLEXception in updateInvalidate()-->"+sqle.toString());
          logger.error(FILE_NAME+"SQLEXception in updateInvalidate()-->"+sqle.toString());
          sqle.printStackTrace();
          throw new EJBException(sqle.toString());
      }
      catch(Exception e)
      {
        //Logger.error(FILE_NAME,"EXception in updateInvalidate()-->"+e.toString());
        logger.error(FILE_NAME+"EXception in updateInvalidate()-->"+e.toString());
        e.printStackTrace();
        throw new EJBException(e.toString());
      }
    return massege;
  }
  /**
   * 
   * @param sellRatesDob
   * @param loginBean
   * @param operation
   */
  public ArrayList getAcceptanceSellRatesValues(QMSSellRatesDOB sellRatesDob,ESupplyGlobalParameters loginBean,String operation)
  {
      ArrayList                   arrayList             =   null;
      HashMap                     flatRatesMap          =   null;
      ArrayList                   headerList            =   null;
      ArrayList                   fslList               =   null;
      QMSSellRatesDAO             sellRatesDao          =   new QMSSellRatesDAO();
      try
      {
         
          arrayList                     =   new ArrayList();
          //System.out.println("6666666222223333333 :: "+sellRatesDob.getConsoleType());
          if(sellRatesDob!=null)
          {
            
              //System.out.println("3333333333333 :: "+sellRatesDob.getWeightBreak());
              arrayList.add(sellRatesDob);
              fslList   =  sellRatesDao.getAcceptanceRateDetails(sellRatesDob,loginBean,operation);
              arrayList.add(fslList);
              
          }     
          
      }
      catch(Exception e)
      {
        //Logger.error(FILE_NAME,"EXception in getAcceptanceSellRatesValues()-->"+e.toString());
        logger.error(FILE_NAME+"EXception in getAcceptanceSellRatesValues()-->"+e.toString());
        e.printStackTrace();
        throw new EJBException(e.toString());
      }
    return arrayList;
  }

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
      HashMap                     flatRatesMap          =   null;
      ArrayList                   headerList            =   null;
      ArrayList                   fslList               =   null;
      QMSSellRatesDAO             sellRatesDao          =   new QMSSellRatesDAO();
      try
      {
         
          arrayList                     =   new ArrayList();
          //System.out.println("6666666222223333333 :: "+sellRatesDob.getConsoleType());
          if(sellRatesDob!=null)
          {
            
              //System.out.println("3333333333333 :: "+sellRatesDob.getWeightBreak());
              arrayList.add(sellRatesDob);
              fslList   =  sellRatesDao.getRateDetails(sellRatesDob,loginBean,operation);
              // flatRatesMap = sellRatesDao.getFlatRatesVales(sellRatesDob,loginBean,operation);
              arrayList.add(fslList);
              
          }     
          
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
  public ArrayList getTerminalIds(ESupplyGlobalParameters loginBean,String operation)
  {
      ArrayList                   terminalList          =   null;
      QMSSellRatesDAO             sellRatesDao          =   new QMSSellRatesDAO();
      try
      {
              //System.out.println("operationoperation in Sls Bean:: "+operation);
              terminalList   =  sellRatesDao.getTerminalIds(loginBean,operation);   
      }
      catch(Exception e)
      {
        //Logger.error(FILE_NAME,"EXception in getTerminalIds()-->"+e.toString());
        logger.error(FILE_NAME+"EXception in getTerminalIds()-->"+e.toString());
        e.printStackTrace();
        throw new EJBException(e.toString());
      }
    return terminalList;
  }
  public String  insertAcceptanceSellRates(ArrayList valueList,ESupplyGlobalParameters loginBean,String operation)
  {
        QMSSellRatesEntityLocalHome sellRateHome        = null;
        QMSSellRatesEntityLocal     sellRateEntity      = null;
        ArrayList                   listFlatValues      = null;
        ArrayList                   listSlabValues      = null;
        ArrayList                   listListValues      = null;
        ArrayList                   listFCLValues       = null;
        ArrayList                   flatValues      = null;
        ArrayList                   slabValues      = null;
        ArrayList                   ListValues      = null;
        ArrayList                   FCLValues       = null;

        ArrayList                   listValues          = null;
        ArrayList               fslListValues   = null;
        ArrayList               list2           = null;
        ArrayList               herderList      = null;
        QMSSellRatesDOB         sellDob         = null;
        QMSSellRatesDOB         sellDob1        = null;
        QMSSellRatesDOB         sellDobvalues1  = null;
        QMSSellRatesDOB         sellDobvalues2  = null;
        String                  massage         = null;
        try
        {
            //System.out.println("****calling insertSellRates method*******");
            sellDob           = (QMSSellRatesDOB)valueList.get(0);
            
            fslListValues     = (ArrayList)valueList.get(1);
            if(fslListValues!=null)
            {
                int sizeValue     = fslListValues.size();
             
                flatValues                =  new ArrayList();
                slabValues                =  new ArrayList();
                ListValues                =  new ArrayList();
                FCLValues                 =  new ArrayList();
                herderList                    =  new ArrayList();
                listValues                    =  new ArrayList();
                //System.out.println("sizeValuesizeValuesizeValuesizeValue ::: "+sizeValue);
                for(int i=0;i<sizeValue;i++)
                {
                    sellDobvalues2  = new QMSSellRatesDOB();
                      listFlatValues                =  new ArrayList();
                      listSlabValues                =  new ArrayList();
                      listListValues                =  new ArrayList();
                      listFCLValues                 =  new ArrayList(); 
                    sellDob1        = (QMSSellRatesDOB)fslListValues.get(i);
             
                    sellDobvalues2.setShipmentMode(sellDob.getShipmentMode());
                    sellDobvalues2.setConsoleType(sellDob.getConsoleType());
                    sellDobvalues2.setWeightBreak(sellDob1.getWeightBreak());
                    sellDobvalues2.setOverAllMargin(sellDob1.getOverAllMargin());
                    sellDobvalues2.setMarginType(sellDob1.getMarginType());
                    sellDobvalues2.setRateType(sellDob1.getRateType());
                    sellDobvalues2.setWeightClass(sellDob1.getWeightClass());
                    sellDobvalues2.setAccessLevel(sellDob.getAccessLevel());
                    sellDobvalues2.setCurrencyId(sellDob1.getCurrencyId());
                    sellDobvalues2.setRec_buyrate_id(sellDob1.getRec_buyrate_id());
                    
                    if("1".equals(sellDob.getShipmentMode()) || ("2".equals(sellDob.getShipmentMode()) && "LCL".equals(sellDob.getConsoleType())) || ("4".equals(sellDob.getShipmentMode()) && "LTL".equals(sellDob.getConsoleType())))
                    {
                        if("FLAT".equalsIgnoreCase(sellDob1.getWeightBreak()))
                        {
                            double[] chargeRates      = sellDob1.getChargeRatesValues();
                            String[] buyChargeRates   = sellDob1.getChargeRates();
                            //double[] buyChargeRates   = sellDob1.getBuyChargeRates();
                            String[] weightBreaks     = sellDob1.getAllWeightBreaks();
                            String[] chargeIndicator  = sellDob1.getChargeInr();
                            int chargeRatesLen        = chargeRates.length;
                            String[] rateDescription	= sellDob1.getRateDescription();
                            int m =0;
                            for(int j=0;j<chargeRatesLen;j++)
                            {
                              sellDobvalues1  = new QMSSellRatesDOB();
                                //System.out.println("IN Flat chargeRates :: "+chargeRates[j]);
                          
                            /*  if(chargeRates[j]>0)
                              {*/
                                  //System.out.println("IN Flat weightBreaks :: "+weightBreaks[j]);
                                  if("BASIC".equalsIgnoreCase(weightBreaks[j].trim()) ||"MIN".equalsIgnoreCase(weightBreaks[j].trim()))
                                  {
                                      sellDobvalues1.setCarrier_id(sellDob1.getCarrier_id());
                                      sellDobvalues1.setOrigin(sellDob1.getOrigin());
                                      sellDobvalues1.setOriginCountry(sellDob1.getOriginCountry());
                                      sellDobvalues1.setDestination(sellDob1.getDestination());
                                      sellDobvalues1.setDestinationCountry(sellDob1.getDestinationCountry());
                                      sellDobvalues1.setServiceLevel(sellDob1.getServiceLevel());
                                      sellDobvalues1.setTransitTime(sellDob1.getTransitTime());
                                      sellDobvalues1.setFrequency(sellDob1.getFrequency());
                                      sellDobvalues1.setNoteValue(sellDob1.getNoteValue());
                                      sellDobvalues1.setExtNotes(sellDob1.getExtNotes());//Added by Mohan for Issue No.219976 on 08-10-2010
                                      sellDobvalues1.setBuyRateId(sellDob1.getBuyRateId());
                                      sellDobvalues1.setVersionNo(sellDob1.getVersionNo());//@@Added by Kameswari for the WPBN issue-146448 on 29/12/08
                                      sellDobvalues1.setLanNumber(sellDob1.getLanNumber());
                                      sellDobvalues1.setLineNumber(m);
                                      sellDobvalues1.setLowerBd(0);
                                      sellDobvalues1.setUpperBd(0);
                                      sellDobvalues1.setChargerateIndicator("");
                                      //System.out.println("chargeRates[j]chargeRates[j]chargeRates[j]:::: in sls bean "+chargeRates[j]);
                                      sellDobvalues1.setChargeRate(chargeRates[j]);
                                      sellDobvalues1.setMarginPer("BASIC".equalsIgnoreCase(weightBreaks[j].trim())?sellDob1.getAbpersentWithBasic():sellDob1.getAbpersentWithMin());
                                      sellDobvalues1.setMinFlat(weightBreaks[j].trim());
                                      sellDobvalues1.setBuyRate(Double.parseDouble(buyChargeRates[j]));
                                      sellDobvalues1.setSurChargeDescription(rateDescription[j]);
                                     // System.out.println("getLanNumber in Bean min : "+sellDob1.getLanNumber());
                                      listFlatValues.add(sellDobvalues1);
                             
                                      m++;
                                  }
                                  else
                                  {
                                      sellDobvalues1.setCarrier_id(sellDob1.getCarrier_id());
                                      sellDobvalues1.setOrigin(sellDob1.getOrigin());
                                      sellDobvalues1.setOriginCountry(sellDob1.getOriginCountry());
                                      sellDobvalues1.setDestination(sellDob1.getDestination());
                                      sellDobvalues1.setDestinationCountry(sellDob1.getDestinationCountry());
                                      sellDobvalues1.setServiceLevel(sellDob1.getServiceLevel());
                                      sellDobvalues1.setTransitTime(sellDob1.getTransitTime());
                                      sellDobvalues1.setFrequency(sellDob1.getFrequency());
                                      sellDobvalues1.setNoteValue(sellDob1.getNoteValue());
                                      sellDobvalues1.setExtNotes(sellDob1.getExtNotes()); //Added by Mohan for Issue No.219976 on 08-10-2010
                                      sellDobvalues1.setBuyRateId(sellDob1.getBuyRateId());
                                      sellDobvalues1.setLanNumber(sellDob1.getLanNumber());
                                       sellDobvalues1.setVersionNo(sellDob1.getVersionNo());//@@Added by Kameswari for the WPBN issue-146448 on 29/12/08
                                      sellDobvalues1.setLineNumber(m);
                                      sellDobvalues1.setLowerBd(0);
                                      sellDobvalues1.setUpperBd(0);
                                      sellDobvalues1.setChargerateIndicator("");
                                     // System.out.println("chargeRates[j]chargeRates[j]chargeRates[j]:::: in sls bean "+chargeRates[j]);
                                      sellDobvalues1.setChargeRate(chargeRates[j]);
/*                                       if("FSBASIC".equalsIgnoreCase(weightBreaks[j])||"FSMIN".equalsIgnoreCase(weightBreaks[j])
                                        ||"FSKG".equalsIgnoreCase(weightBreaks[j])||"SSBASIC".equalsIgnoreCase(weightBreaks[j])
                                        ||"SSMIN".equalsIgnoreCase(weightBreaks[j])||"SSKG".equalsIgnoreCase(weightBreaks[j])
                                        ||"CAFMIN".equalsIgnoreCase(weightBreaks[j])||"CAF%".equalsIgnoreCase(weightBreaks[j])
                                        ||"BAFMIN".equalsIgnoreCase(weightBreaks[j])||"BAFM3".equalsIgnoreCase(weightBreaks[j])
                                        ||"PSSMIN".equalsIgnoreCase(weightBreaks[j])||"PSSM3".equalsIgnoreCase(weightBreaks[j])            //@@Added by Kameswari for the internal issue on 27/05/09
                                        ||"SURCHARGE".equalsIgnoreCase(weightBreaks[j])||"CSF".equalsIgnoreCase(weightBreaks[j]))
*/                                    if(!"A FREIGHT RATE".equalsIgnoreCase(rateDescription[j]))    
                                      {
                                             sellDobvalues1.setServiceLevel("SCH");
                                           sellDobvalues1.setMarginPer(0.0); //@@Modified by Kameswari for the internal issue- on 03/06/09
                                           // sellDobvalues1.setMarginPer(sellDob1.getAbpersentWithFlat());
                                     }
                                        else
                                        {
                                            sellDobvalues1.setServiceLevel(sellDob1.getServiceLevel());
                                             sellDobvalues1.setMarginPer(sellDob1.getAbpersentWithFlat());
                                        }
                                           
                                       sellDobvalues1.setSurChargeDescription(rateDescription[j]);
                                      //sellDobvalues1.setMarginPer(sellDob1.getAbpersentWithFlat());
                                      sellDobvalues1.setMinFlat(weightBreaks[j].trim());
                                      sellDobvalues1.setBuyRate(Double.parseDouble(buyChargeRates[j]));
                                      //System.out.println("getLanNumber in Bean min : "+sellDob1.getLanNumber());
                                      listFlatValues.add(sellDobvalues1);  
                                
                                       m++;
                                  }
                              //}
                            }
                    
                        }
                        else if("SLAB".equalsIgnoreCase(sellDob1.getWeightBreak()))
                        {
                            String[] buyChargeRates   = sellDob1.getChargeRates();
                            double[] chargeRates      = sellDob1.getChargeRatesValues();
                            double[] ovarlMarign      = sellDob1.getSameOvrMargin();
                            String[] weightBreaks     = sellDob1.getAllWeightBreaks();
                            String[] lBound           = sellDob1.getLBound();
                            String[] uBound           = sellDob1.getUBound();
                            String[] chargeIndicator  = sellDob1.getChargeInr();
                            int chargeRatesLen        = chargeRates.length;
                            int m = 0;
                            String[] rateDescription	= sellDob1.getRateDescription();
                            //System.out.println("chargeRatesLenchargeRatesLen :: "+chargeRatesLen);
                            for(int j=0;j<chargeRatesLen;j++)
                            {
                                sellDobvalues1  = new QMSSellRatesDOB();
                                //System.out.println("IN Slab chargeRates :: "+chargeRates[j]);
                                if(chargeRates[j]>0)
                                {
                                    //System.out.println("IN Slab weightBreaks :: "+weightBreaks[j]);
                                    if("BASIC".equalsIgnoreCase(weightBreaks[j].trim()) || "MIN".equalsIgnoreCase(weightBreaks[j].trim()))
                                    {
                                        sellDobvalues1.setCarrier_id(sellDob1.getCarrier_id());
                                        sellDobvalues1.setOrigin(sellDob1.getOrigin());
                                        sellDobvalues1.setOriginCountry(sellDob1.getOriginCountry());
                                        sellDobvalues1.setDestination(sellDob1.getDestination());
                                        sellDobvalues1.setDestinationCountry(sellDob1.getDestinationCountry());
                                        sellDobvalues1.setServiceLevel(sellDob1.getServiceLevel());
                                        sellDobvalues1.setTransitTime(sellDob1.getTransitTime());
                                        sellDobvalues1.setFrequency(sellDob1.getFrequency());
                                        sellDobvalues1.setNoteValue(sellDob1.getNoteValue());
                                        sellDobvalues1.setExtNotes(sellDob1.getExtNotes()); //Added by Mohan for Issue No.219976 on 08-10-2010
                                        sellDobvalues1.setBuyRateId(sellDob1.getBuyRateId());
                                         sellDobvalues1.setVersionNo(sellDob1.getVersionNo());//@@Added by Kameswari for the WPBN issue-146448 on 29/12/08
                                        sellDobvalues1.setLanNumber(sellDob1.getLanNumber());
                                        sellDobvalues1.setLineNumber(m);
                                       // if("-".equalsIgnoreCase(chargeIndicator[j]))
                                        //  sellDobvalues1.setChargerateIndicator("");
                                       // else
                                         // sellDobvalues1.setChargerateIndicator(chargeIndicator[j]);
                                        if(lBound!=null)
                                          sellDobvalues1.setLowerBd(Long.parseLong(lBound[j].trim()));
                                        if(lBound!=null)
                                        sellDobvalues1.setUpperBd(Long.parseLong(uBound[j].trim()));
                                        
                                        //System.out.println("chargeRates[j]chargeRates[j]chargeRates[j]:::: in sls bean "+chargeRates[j]);
                                        sellDobvalues1.setChargeRate(chargeRates[j]);
                                       if("N".equals(sellDob1.getOverAllMargin()))
                                       {
                                          sellDobvalues1.setMarginPer(ovarlMarign[j]);
                                          //System.out.println("ovarlMarign[j] in SLS bean OF Min : "+ovarlMarign[j]);
                                       }
                                        else
                                        {
                                          sellDobvalues1.setMarginPer(sellDob1.getAbpersentWithMin());
                                        }
                                       sellDobvalues1.setSurChargeDescription(rateDescription[j]);
                                        sellDobvalues1.setMinFlat(weightBreaks[j].trim());
                                        sellDobvalues1.setBuyRate(Double.parseDouble(buyChargeRates[j]));
                                        //System.out.println("getLanNumber in Bean min : "+sellDob1.getLanNumber());
                                        listSlabValues.add(sellDobvalues1);
                                        m++;
                                    }
                                    else
                                    {
                                        //System.out.println("getCarriers FLAT in Bean Min: "+sellDob1.getCarrier_id());
                                        sellDobvalues1.setCarrier_id(sellDob1.getCarrier_id());
                                        sellDobvalues1.setOrigin(sellDob1.getOrigin());
                                        sellDobvalues1.setOriginCountry(sellDob1.getOriginCountry());
                                        sellDobvalues1.setDestination(sellDob1.getDestination());
                                        sellDobvalues1.setDestinationCountry(sellDob1.getDestinationCountry());
                                        sellDobvalues1.setServiceLevel(sellDob1.getServiceLevel());
                                        sellDobvalues1.setTransitTime(sellDob1.getTransitTime());
                                        sellDobvalues1.setFrequency(sellDob1.getFrequency());
                                        sellDobvalues1.setNoteValue(sellDob1.getNoteValue());
                                        sellDobvalues1.setExtNotes(sellDob1.getExtNotes()); //Added by Mohan for Issue No.219976 on 08-10-2010
                                        sellDobvalues1.setBuyRateId(sellDob1.getBuyRateId());
                                        sellDobvalues1.setLanNumber(sellDob1.getLanNumber());
                                         sellDobvalues1.setVersionNo(sellDob1.getVersionNo());//@@Added by Kameswari for the WPBN issue-146448 on 29/12/08
                                        sellDobvalues1.setLineNumber(m);
                                        sellDobvalues1.setSurChargeDescription(rateDescription[j]);
                                        //if("-".equalsIgnoreCase(chargeIndicator[j]))
                                        //  sellDobvalues1.setChargerateIndicator("");
                                       // else
                                        //  sellDobvalues1.setChargerateIndicator(chargeIndicator[j]);
                                       /*  if(lBound!=null)
                                          sellDobvalues1.setLowerBd(Long.parseLong(lBound[j].trim()));
                                        if(lBound!=null)
                                        sellDobvalues1.setUpperBd(Long.parseLong(uBound[j].trim()));*/
/*                                  if("FSBASIC".equalsIgnoreCase(weightBreaks[j])||"FSMIN".equalsIgnoreCase(weightBreaks[j])
                                    ||"FSKG".equalsIgnoreCase(weightBreaks[j])||"SSBASIC".equalsIgnoreCase(weightBreaks[j])
                                    ||"SSMIN".equalsIgnoreCase(weightBreaks[j])||"SSKG".equalsIgnoreCase(weightBreaks[j])
                                    ||"CAFMIN".equalsIgnoreCase(weightBreaks[j])||"CAF%".equalsIgnoreCase(weightBreaks[j])
                                    ||"BAFMIN".equalsIgnoreCase(weightBreaks[j])||"BAFM3".equalsIgnoreCase(weightBreaks[j])
                                    ||"PSSMIN".equalsIgnoreCase(weightBreaks[j])||"PSSM3".equalsIgnoreCase(weightBreaks[j])            //@@Added by Kameswari for the internal issue on 27/05/09
                                    ||"SURCHARGE".equalsIgnoreCase(weightBreaks[j])||"CSF".equalsIgnoreCase(weightBreaks[j]))
*/ 	
                                       if(!"A FREIGHT RATE".equalsIgnoreCase(rateDescription[j]))
                                        {
                                           sellDobvalues1.setMarginPer(0.0); //@@Modified by subrahmanyam for the wpbn issue: 172960- on 11/06/09
                                   /*  if("N".equals(sellDob.getOverAllMargin()))
                                        sellDobvalues1.setMarginPer(ovarlMarign[j]);
                                      else
                                        sellDobvalues1.setMarginPer(sellDob1.getAbpersentWithFlat());*/
                                        sellDobvalues1.setLowerBd(0);
                                         sellDobvalues1.setLowerBd(0);
                                         sellDobvalues1.setServiceLevel("SCH");
                                    }
                                    else
                                    {
                                       sellDobvalues1.setServiceLevel(sellDob1.getServiceLevel());
                                      if("N".equals(sellDob.getOverAllMargin()))
                                        sellDobvalues1.setMarginPer(ovarlMarign[j]);
                                      else
                                      {
/*                                    	  if("FSBASIC".equalsIgnoreCase(weightBreaks[j])||"FSMIN".equalsIgnoreCase(weightBreaks[j])
                                                  ||"FSKG".equalsIgnoreCase(weightBreaks[j])||"SSBASIC".equalsIgnoreCase(weightBreaks[j])
                                                  ||"SSMIN".equalsIgnoreCase(weightBreaks[j])||"SSKG".equalsIgnoreCase(weightBreaks[j])
                                                  ||"CAFMIN".equalsIgnoreCase(weightBreaks[j])||"CAF%".equalsIgnoreCase(weightBreaks[j])
                                                  ||"BAFMIN".equalsIgnoreCase(weightBreaks[j])||"BAFM3".equalsIgnoreCase(weightBreaks[j])
                                                  ||"PSSMIN".equalsIgnoreCase(weightBreaks[j])||"PSSM3".equalsIgnoreCase(weightBreaks[j])            //@@Added by Kameswari for the internal issue on 27/05/09
                                                  ||"SURCHARGE".equalsIgnoreCase(weightBreaks[j])||"CSF".equalsIgnoreCase(weightBreaks[j]))
*/
                                    	  if(!"A FREIGHT RATE".equalsIgnoreCase(rateDescription[j]))
                                    	  {
                                                         sellDobvalues1.setMarginPer(0.0); //@@Modified by subrahmanyam for the wpbn issue: 172960- on 11/06/09
                                                 /*  if("N".equals(sellDob.getOverAllMargin()))
                                                      sellDobvalues1.setMarginPer(ovarlMarign[j]);
                                                    else
                                                      sellDobvalues1.setMarginPer(sellDob1.getAbpersentWithFlat());*/
                                                      sellDobvalues1.setLowerBd(0);
                                                       sellDobvalues1.setLowerBd(0);
                                                       sellDobvalues1.setServiceLevel("SCH");
                                                  }
                                    	  		else                                    		  
                                    	  		sellDobvalues1.setMarginPer(sellDob1.getAbpersentWithFlat());
                                      }
                                  
                                       if(lBound!=null&&!("-".equalsIgnoreCase(lBound[j])))
                                        sellDobvalues1.setLowerBd(Long.parseLong(lBound[j]));
                                      if(uBound!=null&&!("-".equalsIgnoreCase(uBound[j])))
                                      sellDobvalues1.setUpperBd(Long.parseLong(uBound[j]));
                                  }
                                              // System.out.println("chargeRates[j]chargeRates[j]chargeRates[j]:::: in sls bean "+chargeRates[j]);
                                      sellDobvalues1.setChargeRate(chargeRates[j]);
                                     // System.out.println("sdsdfsdsddsdfs ::: "+sellDob.getOverAllMargin());
                                      if("N".equals(sellDob1.getOverAllMargin()))
                                      {
                                        sellDobvalues1.setMarginPer(ovarlMarign[j]);
                                        //System.out.println("ovarlMarign[j] in SLS bean : "+ovarlMarign[j]);
                                      }
                                      else
                                      {
/*                                      	  if("FSBASIC".equalsIgnoreCase(weightBreaks[j])||"FSMIN".equalsIgnoreCase(weightBreaks[j])
                                                  ||"FSKG".equalsIgnoreCase(weightBreaks[j])||"SSBASIC".equalsIgnoreCase(weightBreaks[j])
                                                  ||"SSMIN".equalsIgnoreCase(weightBreaks[j])||"SSKG".equalsIgnoreCase(weightBreaks[j])
                                                  ||"CAFMIN".equalsIgnoreCase(weightBreaks[j])||"CAF%".equalsIgnoreCase(weightBreaks[j])
                                                  ||"BAFMIN".equalsIgnoreCase(weightBreaks[j])||"BAFM3".equalsIgnoreCase(weightBreaks[j])
                                                  ||"PSSMIN".equalsIgnoreCase(weightBreaks[j])||"PSSM3".equalsIgnoreCase(weightBreaks[j])            //@@Added by Kameswari for the internal issue on 27/05/09
                                                  ||"SURCHARGE".equalsIgnoreCase(weightBreaks[j])||"CSF".equalsIgnoreCase(weightBreaks[j]))
*/
                                    	  if(!"A FREIGHT RATE".equalsIgnoreCase(rateDescription[j]))
                                    	  {
                                                         sellDobvalues1.setMarginPer(0.0); //@@Modified by subrahmanyam for the wpbn issue: 172960- on 11/06/09
                                                 /*  if("N".equals(sellDob.getOverAllMargin()))
                                                      sellDobvalues1.setMarginPer(ovarlMarign[j]);
                                                    else
                                                      sellDobvalues1.setMarginPer(sellDob1.getAbpersentWithFlat());*/
                                                      sellDobvalues1.setLowerBd(0);
                                                       sellDobvalues1.setLowerBd(0);
                                                       sellDobvalues1.setServiceLevel("SCH");
                                                  }
                                    	  		else                                    		  
                                    	  		sellDobvalues1.setMarginPer(sellDob1.getAbpersentWithFlat());

                                      }
                                      sellDobvalues1.setMinFlat(weightBreaks[j].trim());
                                       sellDobvalues1.setBuyRate(Double.parseDouble(buyChargeRates[j]));
                                    // System.out.println("getLanNumber in Bean min OF Flat : "+sellDob1.getLanNumber());
                                      listSlabValues.add(sellDobvalues1);
                                  
                                      m++;
                                    
                                  }
                                }
                            }
                        }
                        else if("LIST".equalsIgnoreCase(sellDob1.getWeightBreak()))
                        {
                            //System.out.println("getCarriers MIN in Bean Min getWeightBreak : "+sellDob1.getWeightBreak());
                            String[] buyChargeRates   = sellDob1.getChargeRates();
                            double[] chargeRates      = sellDob1.getChargeRatesValues();
                            double[] ovarlMarign      = sellDob1.getSameOvrMargin();
                            String[] weightBreaks     = sellDob1.getAllWeightBreaks();
                            String[] lBound           = sellDob1.getLBound();
                            String[] uBound           = sellDob1.getUBound();
                            String[] chargeIndicator  = sellDob1.getChargeInr();
                            int chargeRatesLen        = chargeRates.length;
                            String[] rateDescription  = sellDob1.getRateDescription();
                            int m = 0;
                            for(int j=0;j<chargeRatesLen;j++)
                            {
                                sellDobvalues1  = new QMSSellRatesDOB();
                                //System.out.println("IN List chargeRates :: "+chargeRates[j]);
                                if(chargeRates[j]>0)
                                {
                                   // System.out.println("IN List weightBreaks :: "+weightBreaks[j]);
                                    if("overPivot".equalsIgnoreCase(weightBreaks[j].trim()))
                                    {
                                        sellDobvalues1.setCarrier_id(sellDob1.getCarrier_id());
                                        sellDobvalues1.setOrigin(sellDob1.getOrigin());
                                        sellDobvalues1.setOriginCountry(sellDob1.getOriginCountry());
                                        sellDobvalues1.setDestination(sellDob1.getDestination());
                                        sellDobvalues1.setDestinationCountry(sellDob1.getDestinationCountry());
                                        sellDobvalues1.setServiceLevel(sellDob1.getServiceLevel());
                                        sellDobvalues1.setTransitTime(sellDob1.getTransitTime());
                                        sellDobvalues1.setFrequency(sellDob1.getFrequency());
                                        sellDobvalues1.setNoteValue(sellDob1.getNoteValue());
                                        sellDobvalues1.setExtNotes(sellDob1.getExtNotes()); //Added by Mohan for Issue No.219976 on 08-10-2010
                                        sellDobvalues1.setBuyRateId(sellDob1.getBuyRateId());
                                        sellDobvalues1.setLanNumber(sellDob1.getLanNumber());
                                        sellDobvalues1.setLineNumber(0);
                                        sellDobvalues1.setVersionNo(sellDob1.getVersionNo());//@@Added by Kameswari for the WPBN issue-146448 on 29/12/08
                                        sellDobvalues1.setLowerBd(0);
                                        sellDobvalues1.setUpperBd(0);
                                        sellDobvalues1.setChargerateIndicator("");
                                        //System.out.println("chargeRates[j]chargeRates[j]chargeRates[j]:::: in sls bean "+chargeRates[j]);
                                        sellDobvalues1.setChargeRate(chargeRates[j]);
                                       if("N".equals(sellDob1.getOverAllMargin()))
                                          sellDobvalues1.setMarginPer(ovarlMarign[j]);
                                        else
                                          sellDobvalues1.setMarginPer(sellDob1.getAbpersentWithFlat());
                                        sellDobvalues1.setMinFlat(weightBreaks[j].trim());
                                        sellDobvalues1.setBuyRate(Double.parseDouble(buyChargeRates[j]));
                                        sellDobvalues1.setSurChargeDescription(rateDescription[j]);
                                        //System.out.println("getLanNumber in Bean min : "+sellDob1.getLanNumber());
                                        listListValues.add(sellDobvalues1);
                                        m++;
                                    }
                                    else
                                    {
                                        sellDobvalues1.setCarrier_id(sellDob1.getCarrier_id());
                                        sellDobvalues1.setOrigin(sellDob1.getOrigin());
                                        sellDobvalues1.setOriginCountry(sellDob1.getOriginCountry());
                                        sellDobvalues1.setDestination(sellDob1.getDestination());
                                        sellDobvalues1.setDestinationCountry(sellDob1.getDestinationCountry());
                                        sellDobvalues1.setServiceLevel(sellDob1.getServiceLevel());
                                        sellDobvalues1.setTransitTime(sellDob1.getTransitTime());
                                        sellDobvalues1.setFrequency(sellDob1.getFrequency());
                                        sellDobvalues1.setNoteValue(sellDob1.getNoteValue());
                                        sellDobvalues1.setExtNotes(sellDob1.getExtNotes()); //Added by Mohan for Issue No.219976 on 08-10-2010
                                        sellDobvalues1.setBuyRateId(sellDob1.getBuyRateId());
                                        sellDobvalues1.setLanNumber(sellDob1.getLanNumber());
                                        sellDobvalues1.setVersionNo(sellDob1.getVersionNo());//@@Added by Kameswari for the WPBN issue-146448 on 29/12/08
                                        sellDobvalues1.setLineNumber(m+1);
                                        sellDobvalues1.setChargerateIndicator("");
                                        sellDobvalues1.setLowerBd(0);
                                        sellDobvalues1.setUpperBd(0);
                                        //System.out.println("chargeRates[j]chargeRates[j]chargeRates[j]:::: in sls bean "+chargeRates[j]);
                                        sellDobvalues1.setChargeRate(chargeRates[j]);
                                     /*   if("N".equals(sellDob1.getOverAllMargin()))
                                          sellDobvalues1.setMarginPer(ovarlMarign[j]);
                                        else
                                          sellDobvalues1.setMarginPer(sellDob1.getAbpersentWithMin());*/
/*                                       if("FSBASIC".equalsIgnoreCase(weightBreaks[j])||"FSMIN".equalsIgnoreCase(weightBreaks[j])
                                            ||"FSKG".equalsIgnoreCase(weightBreaks[j])||"SSBASIC".equalsIgnoreCase(weightBreaks[j])
                                            ||"SSMIN".equalsIgnoreCase(weightBreaks[j])||"SSKG".equalsIgnoreCase(weightBreaks[j])
                                            ||"CAFMIN".equalsIgnoreCase(weightBreaks[j])||"CAF%".equalsIgnoreCase(weightBreaks[j])
                                            ||"BAFMIN".equalsIgnoreCase(weightBreaks[j])||"BAFM3".equalsIgnoreCase(weightBreaks[j])
                                          ||"PSSMIN".equalsIgnoreCase(weightBreaks[j])||"PSSM3".equalsIgnoreCase(weightBreaks[j])            //@@Added by Kameswari for the internal issue on 27/05/09
                                          ||"SURCHARGE".equalsIgnoreCase(weightBreaks[j])||"CSF".equalsIgnoreCase(weightBreaks[j]))
*/										if(!"A FREIGHT RATE".equalsIgnoreCase(rateDescription[j]))
                                        {
                                                sellDobvalues1.setServiceLevel("SCH");
                                                 sellDobvalues1.setMarginPer(0.0); //@@Modified by Kameswari for the internal issue- on 03/06/09
                                             //   sellDobvalues1.setMarginPer(ovarlMarign[j]);
                                        }
                                         else
                                         {
                                               sellDobvalues1.setServiceLevel(sellDob1.getServiceLevel());
                                                 if("N".equals(sellDob.getOverAllMargin()))
                                              sellDobvalues1.setMarginPer(ovarlMarign[j]);
                                            else
                                              sellDobvalues1.setMarginPer(sellDob1.getAbpersentWithMin());
                                         }
                                          
                                      
                                          sellDobvalues1.setMinFlat(weightBreaks[j]);
                                        sellDobvalues1.setBuyRate(Double.parseDouble(buyChargeRates[j].trim()));
                                        sellDobvalues1.setSurChargeDescription(rateDescription[j]);
                                        //System.out.println("getLanNumber in Bean min : "+sellDob1.getLanNumber());
                                        listListValues.add(sellDobvalues1);
                                         m++;
                                    }
                                }
                            }
                        }
                    }
                    else
                    {
                        //System.out.println("IN FclList WeightBreak :: "+sellDob1.getWeightBreak());
                        if("LIST".equalsIgnoreCase(sellDob1.getWeightBreak()))
                        {
                            String[] buyChargeRates   = sellDob1.getChargeRates();
                            double[] chargeRates      = sellDob1.getChargeRatesValues();
                            double[] ovarlMarign      = sellDob1.getSameOvrMargin();
                            String[] weightBreaks     = sellDob1.getAllWeightBreaks();
                            String[] lBound           = sellDob1.getLBound();
                            String[] uBound           = sellDob1.getUBound();
                            String[] chargeIndicator  = sellDob1.getChargeInr();
                            int chargeRatesLen        = chargeRates.length;
                            int m = 0;
                            String[] rateDescription	= sellDob1.getRateDescription();
                            for(int j=0;j<chargeRatesLen;j++)
                            {
                                sellDobvalues1  = new QMSSellRatesDOB();
                                //System.out.println("IN FclList chargeRates :: "+chargeRates[j]);
                                //if(chargeRates[j]>0.0D)
                               // {
                                    sellDobvalues1.setCarrier_id(sellDob1.getCarrier_id());
                                    sellDobvalues1.setOrigin(sellDob1.getOrigin());
                                    sellDobvalues1.setOriginCountry(sellDob1.getOriginCountry());
                                    sellDobvalues1.setDestination(sellDob1.getDestination());
                                    sellDobvalues1.setDestinationCountry(sellDob1.getDestinationCountry());
                                    sellDobvalues1.setServiceLevel((rateDescription !=null && rateDescription[j]!= null && ("A FREIGHT RATE".equalsIgnoreCase(rateDescription[j])||"FREIGHT RATE".equalsIgnoreCase(rateDescription[j]))? sellDob1.getServiceLevel():(rateDescription !=null && rateDescription[j]!= null)?"SCH":""));
                                    sellDobvalues1.setTransitTime(sellDob1.getTransitTime());
                                    sellDobvalues1.setFrequency(sellDob1.getFrequency());
                                    sellDobvalues1.setNoteValue(sellDob1.getNoteValue());
                                    sellDobvalues1.setExtNotes(sellDob1.getExtNotes());//Added by Mohan for Issue No.219976 on 08-10-2010
                                    sellDobvalues1.setBuyRateId(sellDob1.getBuyRateId());
                                     sellDobvalues1.setVersionNo(sellDob1.getVersionNo());//@@Added by Kameswari for the WPBN issue-146448 on 29/12/08
                                    sellDobvalues1.setLanNumber(sellDob1.getLanNumber());
                                    sellDobvalues1.setLineNumber(m);
                                    sellDobvalues1.setLowerBd(0);
                                    sellDobvalues1.setUpperBd(0);
                                    sellDobvalues1.setChargerateIndicator("");
                                    //System.out.println("chargeRates[j]chargeRates[j]chargeRates[j]:::: in sls bean "+chargeRates[j]);
                                    sellDobvalues1.setChargeRate(chargeRates[j]);
                       
                                   /* if("N".equals(sellDob1.getOverAllMargin()))
                                      sellDobvalues1.setMarginPer(ovarlMarign[j]);
                                    else
                                      sellDobvalues1.setMarginPer(sellDob1.getAbpersentWithMin());*/
                                /* if("FSBASIC".equalsIgnoreCase(weightBreaks[j])||"FSMIN".equalsIgnoreCase(weightBreaks[j])
                                  ||"FSKG".equalsIgnoreCase(weightBreaks[j])||"SSBASIC".equalsIgnoreCase(weightBreaks[j])
                                  ||"SSMIN".equalsIgnoreCase(weightBreaks[j])||"SSKG".equalsIgnoreCase(weightBreaks[j])
                                  ||"CAFMIN".equalsIgnoreCase(weightBreaks[j])||"CAF%".equalsIgnoreCase(weightBreaks[j])
                                  ||"BAFMIN".equalsIgnoreCase(weightBreaks[j])||"BAFM3".equalsIgnoreCase(weightBreaks[j])
                                  ||"PSSMIN".equalsIgnoreCase(weightBreaks[j])||"PSSM3".equalsIgnoreCase(weightBreaks[j])            //@@Added by Kameswari for the internal issue on 27/05/09
                                   ||"SURCHARGE".equalsIgnoreCase(weightBreaks[j])||"CSF".equalsIgnoreCase(weightBreaks[j]))*/
                                //@@Commented and Modified by Kameswari for the internal issue
                                  //@@ Commented and Added by subrahmanyam for the pbn id: 206737 on 27-May-10
                                    //if("BAF".endsWith(weightBreaks[j])||"CAF".endsWith(weightBreaks[j])||"CSS".endsWith(weightBreaks[j])||"PSS".endsWith(weightBreaks[j]))
//                                  if(weightBreaks[j].endsWith("BAF")||weightBreaks[j].endsWith("CAF")||weightBreaks[j].endsWith("CSF")||weightBreaks[j].endsWith("PSS"))
                                    if(!"A FREIGHT RATE".equalsIgnoreCase(rateDescription[j]))
                                  {
                                       sellDobvalues1.setMarginPer(0.0); //@@Modified by Kameswari for the internal issue- on 03/06/09
                                    //  sellDobvalues1.setMarginPer(ovarlMarign[j]);
                                     // sellDobvalues1.setServiceLevel("SCH");
                                  }
                                  else
                                  {
                               
                                  sellDobvalues1.setServiceLevel(sellDob1.getServiceLevel());
                                 //  if("N".equals(sellDob.getOverAllMargin()))//@@Modified and Commented by Kameswari for the WPBN issue -172742 on 05/06/09
                                       if("N".equals(sellDob1.getOverAllMargin()))
                                    {
                                      sellDobvalues1.setMarginPer(ovarlMarign[j]);
                                     }
                                    else
                                      sellDobvalues1.setMarginPer(sellDob1.getAbpersentWithMin());
                                  }
                             
                                 
                                    sellDobvalues1.setMinFlat(weightBreaks[j].trim());
                                    sellDobvalues1.setBuyRate(Double.parseDouble(buyChargeRates[j]));
                                    sellDobvalues1.setSurChargeDescription(rateDescription[j]);
                                    listFCLValues.add(sellDobvalues1);
                                    m++;
                                //}
                            }
                      }
                        
                }
              herderList.add(sellDobvalues2); 
           
              if(listFlatValues!=null&&listFlatValues.size()>0)
                 flatValues.add(listFlatValues);
               if(listSlabValues!=null&&listSlabValues.size()>0)
              slabValues.add(listSlabValues);
                if(listListValues!=null&&listListValues.size()>0)
              ListValues.add(listListValues);
               if(listFCLValues!=null&&listFCLValues.size()>0)
              FCLValues.add(listFCLValues);
              }
            /*listValues.add(listFlatValues);
            listValues.add(listSlabValues);
            listValues.add(listListValues);
            listValues.add(listFCLValues);*/
            
          
            listValues.add(flatValues);
            listValues.add(slabValues);
            listValues.add(ListValues);
            listValues.add(FCLValues);
            //System.out.println("******Before*********");
            sellRateHome      = (QMSSellRatesEntityLocalHome)lookUpBean.getEJBLocalHome("java:comp/env/QMSSellRatesEntity");
            sellRateEntity    = sellRateHome.create(herderList,listValues,loginBean,operation);
            //System.out.println("******After*********");
            massage ="successfully";
            }
        }
        catch(Exception e)
        {
          //Logger.error(FILE_NAME,"EXception in insertAcceptanceSellRates()-->"+e.toString());
          logger.error(FILE_NAME+"EXception in insertAcceptanceSellRates()-->"+e.toString());
          e.printStackTrace();
          throw new EJBException(e.toString());
        }
      return massage;
   }
  /**
   * 
   * @param sellRatesDob
   * @param operation
   * @return 
   */
    public String insertSellRates(ArrayList valueList,ESupplyGlobalParameters loginBean,String operation)
   {
        QMSSellRatesEntityLocalHome sellRateHome  = null;
        QMSSellRatesEntityLocal     sellRateEntity = null;
        ArrayList               listValues      = null;
         ArrayList               lanesList      = null;
        ArrayList               fslListValues   = null;
        ArrayList               list2           = null;
        ArrayList               boundryList     = null;
        QMSSellRatesDOB         sellDob         = null;
        QMSSellRatesDOB         sellDob1        = null;
        QMSSellRatesDOB         sellDobvalues1  = null;
        String                  massage         = null;
        try
        {
          
            if(valueList!=null)
            {
            sellDob           = (QMSSellRatesDOB)valueList.get(0);
         
            fslListValues     = (ArrayList)valueList.get(1);
            }
          if(sellDob!=null)
          {
          
            if("1".equals(sellDob.getShipmentMode()) || ("2".equals(sellDob.getShipmentMode()) && "LCL".equals(sellDob.getConsoleType())) || ("4".equals(sellDob.getShipmentMode()) && "LTL".equals(sellDob.getConsoleType())))
            {
              if("FLAT".equalsIgnoreCase(sellDob.getWeightBreak()))
              {
                if(fslListValues!=null)
                {
                  int sizeValue     = fslListValues.size();
                   lanesList               =  new ArrayList();
                //  listValues                =  new ArrayList();//@@Modified by Kameswari for the WPBN issue-126038
                  for(int i=0;i<sizeValue;i++)
                  {
                    sellDob1                  = (QMSSellRatesDOB)fslListValues.get(i);
                     listValues                =  new ArrayList();//@@Added by Kameswari for the WPBN issue-126038
                    //double[] chargeRates      = sellDob1.getChargeRatesValues();
                    String[] chargeRates      = sellDob1.getChargeRates();
                    //double[] buyChargeRates   = sellDob1.getBuyChargeRates();
                    String[] buyChargeRates   = sellDob1.getBuyChrRates();
                    String[] weightBreaks     = sellDob1.getAllWeightBreaks();
                    String[] chargeIndicator  = sellDob1.getChargeInr();
                    String[] rateDescriptions	=	sellDob1.getRateDescription();//@@ Added by subrahmanyam for CR-219973
                    int chargeRatesLen        = chargeRates.length;
                    int m =0;
                    for(int j=0;j<chargeRatesLen;j++)
                    {
                      sellDobvalues1  = new QMSSellRatesDOB();
                      
                      if("MIN".equalsIgnoreCase(weightBreaks[j]) & "A FREIGHT RATE".equalsIgnoreCase(rateDescriptions[j]))
                      {
                        if(!("-".equalsIgnoreCase(chargeRates[j])))
                        {
                          //System.out.println("getCarriers MIN in Bean Min: "+sellDob1.getCarrier_id());
                          sellDobvalues1.setCarrier_id(sellDob1.getCarrier_id());
                          sellDobvalues1.setOrigin(sellDob1.getOrigin());
                          sellDobvalues1.setOriginCountry(sellDob1.getOriginCountry());
                          sellDobvalues1.setDestination(sellDob1.getDestination());
                          sellDobvalues1.setDestinationCountry(sellDob1.getDestinationCountry());
                          sellDobvalues1.setServiceLevel(sellDob1.getServiceLevel());
                          sellDobvalues1.setTransitTime(sellDob1.getTransitTime());
                          sellDobvalues1.setFrequency(sellDob1.getFrequency());
                          sellDobvalues1.setNoteValue(sellDob1.getNoteValue());
                          sellDobvalues1.setExtNotes(sellDob1.getExtNotes());//Added by Mohan for Issue No.219976 on 08-10-2010
                          sellDobvalues1.setBuyRateId(sellDob1.getBuyRateId());
                           sellDobvalues1.setVersionNo(sellDob1.getVersionNo());//@@Added by Kameswari for the WPBN issue-146448 on 29/12/08
                          sellDobvalues1.setLanNumber(sellDob1.getLanNumber());
                          sellDobvalues1.setVersionNo(sellDob1.getVersionNo());//@@Added for the WPBN issue-146448 on 24/12/08
                          sellDobvalues1.setLineNumber(m);
                          sellDobvalues1.setLowerBd(0);
                          sellDobvalues1.setUpperBd(0);
                          sellDobvalues1.setChargerateIndicator("");
                          
                          //System.out.println("chargeRates[j]chargeRates[j]chargeRates[j]:::: in sls bean "+chargeRates[j]);
                          sellDobvalues1.setChargeRate(Double.parseDouble(chargeRates[j]));
                          sellDobvalues1.setMarginPer(sellDob1.getAbpersentWithMin());
                          sellDobvalues1.setMinFlat(weightBreaks[j]);
                          sellDobvalues1.setBuyRate(Double.parseDouble(buyChargeRates[j]));
                          sellDobvalues1.setSurChargeDescription(rateDescriptions[j]);
                         // System.out.println("getLanNumber in Bean min : "+sellDob1.getLanNumber());
                         
                          listValues.add(sellDobvalues1);
                          m++;
                        }
                      }else if("BASIC".equalsIgnoreCase(weightBreaks[j]) & "A FREIGHT RATE".equalsIgnoreCase(rateDescriptions[j]))// Added by govind fro the RSR View issue
                      {
                          if(!("-".equalsIgnoreCase(chargeRates[j])))
                          {
                            //System.out.println("getCarriers MIN in Bean Min: "+sellDob1.getCarrier_id());
                            sellDobvalues1.setCarrier_id(sellDob1.getCarrier_id());
                            sellDobvalues1.setOrigin(sellDob1.getOrigin());
                            sellDobvalues1.setOriginCountry(sellDob1.getOriginCountry());
                            sellDobvalues1.setDestination(sellDob1.getDestination());
                            sellDobvalues1.setDestinationCountry(sellDob1.getDestinationCountry());
                            sellDobvalues1.setServiceLevel(sellDob1.getServiceLevel());
                            sellDobvalues1.setTransitTime(sellDob1.getTransitTime());
                            sellDobvalues1.setFrequency(sellDob1.getFrequency());
                            sellDobvalues1.setNoteValue(sellDob1.getNoteValue());
                            sellDobvalues1.setExtNotes(sellDob1.getExtNotes());//Added by Mohan for Issue No.219976 on 08-10-2010
                            sellDobvalues1.setBuyRateId(sellDob1.getBuyRateId());
                             sellDobvalues1.setVersionNo(sellDob1.getVersionNo());//@@Added by Kameswari for the WPBN issue-146448 on 29/12/08
                            sellDobvalues1.setLanNumber(sellDob1.getLanNumber());
                            sellDobvalues1.setVersionNo(sellDob1.getVersionNo());//@@Added for the WPBN issue-146448 on 24/12/08
                            sellDobvalues1.setLineNumber(m);
                            sellDobvalues1.setLowerBd(0);
                            sellDobvalues1.setUpperBd(0);
                            sellDobvalues1.setChargerateIndicator("");
                            
                            //System.out.println("chargeRates[j]chargeRates[j]chargeRates[j]:::: in sls bean "+chargeRates[j]);
                            sellDobvalues1.setChargeRate(Double.parseDouble(chargeRates[j]));
                            sellDobvalues1.setMarginPer(sellDob1.getAbpersentWithBasic());
                            sellDobvalues1.setMinFlat(weightBreaks[j]);
                            sellDobvalues1.setBuyRate(Double.parseDouble(buyChargeRates[j]));
                            sellDobvalues1.setSurChargeDescription(rateDescriptions[j]);
                           // System.out.println("getLanNumber in Bean min : "+sellDob1.getLanNumber());
                           
                            listValues.add(sellDobvalues1);
                            m++;
                          }
                        }
                      else
                      {
              
                        if(!("-".equalsIgnoreCase(chargeRates[j])))
                        {
                          //System.out.println("getCarriers FLAT in Bean Min: "+sellDob1.getCarrier_id());
                          sellDobvalues1.setCarrier_id(sellDob1.getCarrier_id());
                          sellDobvalues1.setOrigin(sellDob1.getOrigin());
                          sellDobvalues1.setOriginCountry(sellDob1.getOriginCountry());
                          sellDobvalues1.setDestination(sellDob1.getDestination());
                          sellDobvalues1.setDestinationCountry(sellDob1.getDestinationCountry());
                         
                          sellDobvalues1.setTransitTime(sellDob1.getTransitTime());
                          sellDobvalues1.setFrequency(sellDob1.getFrequency());
                          sellDobvalues1.setNoteValue(sellDob1.getNoteValue());
                          sellDobvalues1.setExtNotes(sellDob1.getExtNotes());//Added by Mohan for Issue No.219976 on 08-10-2010
                          sellDobvalues1.setBuyRateId(sellDob1.getBuyRateId());
                           sellDobvalues1.setVersionNo(sellDob1.getVersionNo());//@@Added by Kameswari for the WPBN issue-146448 on 29/12/08
                          sellDobvalues1.setLanNumber(sellDob1.getLanNumber());
                          sellDobvalues1.setVersionNo(sellDob1.getVersionNo());//@@Added for the WPBN issue-146448 on 24/12/08
                          sellDobvalues1.setLineNumber(m);
                          sellDobvalues1.setLowerBd(0);
                          sellDobvalues1.setUpperBd(0);
                          sellDobvalues1.setChargerateIndicator("");
                          sellDobvalues1.setSurChargeDescription(rateDescriptions[j]);
                         // System.out.println("chargeRates[j]chargeRates[j]chargeRates[j]:::: in sls bean "+chargeRates[j]);
                          sellDobvalues1.setChargeRate(Double.parseDouble(chargeRates[j]));
                           if("FSBASIC".equalsIgnoreCase(weightBreaks[j])||"FSMIN".equalsIgnoreCase(weightBreaks[j])
                          ||"FSKG".equalsIgnoreCase(weightBreaks[j])||"SSBASIC".equalsIgnoreCase(weightBreaks[j])
                          ||"SSMIN".equalsIgnoreCase(weightBreaks[j])||"SSKG".equalsIgnoreCase(weightBreaks[j])
                          ||"CAFMIN".equalsIgnoreCase(weightBreaks[j])||"CAF%".equalsIgnoreCase(weightBreaks[j])
                          ||"BAFMIN".equalsIgnoreCase(weightBreaks[j])||"BAFM3".equalsIgnoreCase(weightBreaks[j])
                          ||"PSSMIN".equalsIgnoreCase(weightBreaks[j]) ||"PSSM3".equalsIgnoreCase(weightBreaks[j])
                          ||"SURCHARGE".equalsIgnoreCase(weightBreaks[j])||"CSF".equalsIgnoreCase(weightBreaks[j])||
                          weightBreaks[j].endsWith("CAF")||weightBreaks[j].endsWith("BAF")||weightBreaks[j].endsWith("CSF")
                          ||weightBreaks[j].endsWith("PSS") || !"A FREIGHT RATE".equalsIgnoreCase(rateDescriptions[j]))
                          {
                               sellDobvalues1.setServiceLevel("SCH");
                               sellDobvalues1.setMarginPer(sellDob1.getAbperWithSurcharge());
                               
                          }
                          else
                          {
                                sellDobvalues1.setServiceLevel(sellDob1.getServiceLevel());
                               sellDobvalues1.setMarginPer(sellDob1.getAbpersentWithFlat());
                          }
                          sellDobvalues1.setMinFlat(weightBreaks[j]);
                          sellDobvalues1.setBuyRate(Double.parseDouble(buyChargeRates[j]));
                          //System.out.println("getLanNumber in Bean min : "+sellDob1.getLanNumber());
                          listValues.add(sellDobvalues1);
                          m++;
                        }
                      }
                    }
                    lanesList.add(listValues);
                  }
                }
              }
              else if("SLAB".equalsIgnoreCase(sellDob.getWeightBreak()))
              {
                if(fslListValues!=null)
                {
                  int sizeValue     = fslListValues.size();
                  lanesList          = new ArrayList();
                  //listValues        = new ArrayList();//@@Modified by Kameswari for the WPBN issue-126038
                  for(int i=0;i<sizeValue;i++)
                  {
                    sellDob1                  = (QMSSellRatesDOB)fslListValues.get(i);
                    listValues        = new ArrayList();//@@Added by Kameswari for the WPBN issue-126038
                    //double[] buyChargeRates   = sellDob1.getBuyChargeRates();
                    String[] buyChargeRates   = sellDob1.getBuyChrRates();
                    //double[] chargeRates      = sellDob1.getChargeRatesValues();
                    String[] chargeRates      = sellDob1.getChargeRates();
                    double[] ovarlMarign      = sellDob1.getSameOvrMargin();
                    String[] weightBreaks     = sellDob1.getAllWeightBreaks();
                    String[] lBound           = sellDob1.getLBound();
                    String[] uBound           = sellDob1.getUBound();
                    String[] chargeIndicator  = sellDob1.getChargeInr();
                    String[] rateDesc		  = sellDob1.getRateDescription();//@@ Added by subrahmanyam for CR-219973
                    int chargeRatesLen        = chargeRates.length;
                    int m = 0;
                   // System.out.println("chargeRatesLenchargeRatesLen :: "+chargeRatesLen);
                    for(int j=0;j<chargeRatesLen;j++)
                    {
                   
                     // System.out.println("jjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjj :: "+j);
                     // System.out.println("chargeRates[j]chargeRates[j]chargeRates[j] :: "+chargeRates[j]);
                      //System.out.println("weightBreaks[j]weightBreaks[j]weightBreaks[j] :: "+weightBreaks[j]);
                      sellDobvalues1  = new QMSSellRatesDOB();
                      if("MIN".equalsIgnoreCase(weightBreaks[j]) && "A FREIGHT RATE".equalsIgnoreCase(rateDesc[j]))
                      {
                        if(!("-".equalsIgnoreCase(chargeRates[j])))
                        {
                          //System.out.println("getCarriers MIN in Bean Min: "+sellDob1.getCarrier_id());
                          sellDobvalues1.setCarrier_id(sellDob1.getCarrier_id());
                          sellDobvalues1.setOrigin(sellDob1.getOrigin());
                          sellDobvalues1.setOriginCountry(sellDob1.getOriginCountry());
                          sellDobvalues1.setDestination(sellDob1.getDestination());
                          sellDobvalues1.setDestinationCountry(sellDob1.getDestinationCountry());
                          sellDobvalues1.setServiceLevel(sellDob1.getServiceLevel());
                          sellDobvalues1.setTransitTime(sellDob1.getTransitTime());
                          sellDobvalues1.setFrequency(sellDob1.getFrequency());
                          sellDobvalues1.setNoteValue(sellDob1.getNoteValue());
                          sellDobvalues1.setExtNotes(sellDob1.getExtNotes());//Added by Mohan for Issue No.219976 on 08-10-2010
                          sellDobvalues1.setBuyRateId(sellDob1.getBuyRateId());
                         sellDobvalues1.setVersionNo(sellDob1.getVersionNo());//@@Added by Kameswari for the WPBN issue-146448 on 29/12/08
                          sellDobvalues1.setLanNumber(sellDob1.getLanNumber());
                          sellDobvalues1.setVersionNo(sellDob1.getVersionNo());//@@Added for the WPBN issue-146448 on 24/12/08
                          sellDobvalues1.setLineNumber(m);
                          if("-".equalsIgnoreCase(chargeIndicator[j]))
                            sellDobvalues1.setChargerateIndicator("");
                          else
                            sellDobvalues1.setChargerateIndicator(chargeIndicator[j]);
                          if(lBound!=null)
                            sellDobvalues1.setLowerBd(Long.parseLong(lBound[j]));
                          if(lBound!=null)
                          sellDobvalues1.setUpperBd(Long.parseLong(uBound[j]));
                          
                          //System.out.println("chargeRates[j]chargeRates[j]chargeRates[j]:::: in sls bean "+chargeRates[j]);
                          sellDobvalues1.setChargeRate(Double.parseDouble(chargeRates[j]));
                         
                         if("N".equals(sellDob.getOverAllMargin()))
                            sellDobvalues1.setMarginPer(ovarlMarign[j]);
                          else
                            sellDobvalues1.setMarginPer(sellDob1.getAbpersentWithMin());
                          sellDobvalues1.setMinFlat(weightBreaks[j]);
                          sellDobvalues1.setBuyRate(Double.parseDouble(buyChargeRates[j]));
                          sellDobvalues1.setSurChargeDescription(rateDesc[j]);
                          //System.out.println("getLanNumber in Bean min : "+sellDob1.getLanNumber());
                          listValues.add(sellDobvalues1);
                          m++;
                        }
                      }
                      else  if("BASIC".equalsIgnoreCase(weightBreaks[j]) && "A FREIGHT RATE".equalsIgnoreCase(rateDesc[j]))
                      {
                          if(!("-".equalsIgnoreCase(chargeRates[j])))
                          {
                            //System.out.println("getCarriers MIN in Bean Min: "+sellDob1.getCarrier_id());
                            sellDobvalues1.setCarrier_id(sellDob1.getCarrier_id());
                            sellDobvalues1.setOrigin(sellDob1.getOrigin());
                            sellDobvalues1.setOriginCountry(sellDob1.getOriginCountry());
                            sellDobvalues1.setDestination(sellDob1.getDestination());
                            sellDobvalues1.setDestinationCountry(sellDob1.getDestinationCountry());
                            sellDobvalues1.setServiceLevel(sellDob1.getServiceLevel());
                            sellDobvalues1.setTransitTime(sellDob1.getTransitTime());
                            sellDobvalues1.setFrequency(sellDob1.getFrequency());
                            sellDobvalues1.setNoteValue(sellDob1.getNoteValue());
                            sellDobvalues1.setExtNotes(sellDob1.getExtNotes());//Added by Mohan for Issue No.219976 on 08-10-2010
                            sellDobvalues1.setBuyRateId(sellDob1.getBuyRateId());
                           sellDobvalues1.setVersionNo(sellDob1.getVersionNo());//@@Added by Kameswari for the WPBN issue-146448 on 29/12/08
                            sellDobvalues1.setLanNumber(sellDob1.getLanNumber());
                            sellDobvalues1.setVersionNo(sellDob1.getVersionNo());//@@Added for the WPBN issue-146448 on 24/12/08
                            sellDobvalues1.setLineNumber(m);
                            if("-".equalsIgnoreCase(chargeIndicator[j]))
                              sellDobvalues1.setChargerateIndicator("");
                            else
                              sellDobvalues1.setChargerateIndicator(chargeIndicator[j]);
                            if(lBound!=null)
                              sellDobvalues1.setLowerBd(Long.parseLong(lBound[j]));
                            if(lBound!=null)
                            sellDobvalues1.setUpperBd(Long.parseLong(uBound[j]));
                            
                            //System.out.println("chargeRates[j]chargeRates[j]chargeRates[j]:::: in sls bean "+chargeRates[j]);
                            sellDobvalues1.setChargeRate(Double.parseDouble(chargeRates[j]));
                           
                           if("N".equals(sellDob.getOverAllMargin()))
                              sellDobvalues1.setMarginPer(ovarlMarign[j]);
                            else
                            sellDobvalues1.setMarginPer(sellDob1.getAbpersentWithBasic());
                            sellDobvalues1.setMinFlat(weightBreaks[j]);
                            sellDobvalues1.setBuyRate(Double.parseDouble(buyChargeRates[j]));
                            sellDobvalues1.setSurChargeDescription(rateDesc[j]);
                            //System.out.println("getLanNumber in Bean min : "+sellDob1.getLanNumber());
                            listValues.add(sellDobvalues1);
                            m++;
                          }
                        }
                      else
                      {
                        if(!("-".equalsIgnoreCase(chargeRates[j])))
                        {
                          //System.out.println("getCarriers FLAT in Bean Min: "+sellDob1.getCarrier_id());
                          sellDobvalues1.setCarrier_id(sellDob1.getCarrier_id());
                          sellDobvalues1.setOrigin(sellDob1.getOrigin());
                          sellDobvalues1.setOriginCountry(sellDob1.getOriginCountry());
                          sellDobvalues1.setDestination(sellDob1.getDestination());
                          sellDobvalues1.setDestinationCountry(sellDob1.getDestinationCountry());
                         // sellDobvalues1.setServiceLevel(sellDob1.getServiceLevel());
                          sellDobvalues1.setTransitTime(sellDob1.getTransitTime());
                          sellDobvalues1.setFrequency(sellDob1.getFrequency());
                          sellDobvalues1.setNoteValue(sellDob1.getNoteValue());
                          sellDobvalues1.setExtNotes(sellDob1.getExtNotes());//Added by Mohan for Issue No.219976 on 08-10-2010
                          sellDobvalues1.setBuyRateId(sellDob1.getBuyRateId());
                          sellDobvalues1.setVersionNo(sellDob1.getVersionNo());//@@Added by Kameswari for the WPBN issue-146448 on 29/12/08
                          sellDobvalues1.setLanNumber(sellDob1.getLanNumber());
                          sellDobvalues1.setVersionNo(sellDob1.getVersionNo());//@@Added for the WPBN issue-146448 on 24/12/08
                          sellDobvalues1.setLineNumber(m);
                          if("-".equalsIgnoreCase(chargeIndicator[j]))
                            sellDobvalues1.setChargerateIndicator("");
                          else
                            sellDobvalues1.setChargerateIndicator(chargeIndicator[j]);
                          // System.out.println("chargeRates[j]chargeRates[j]chargeRates[j]:::: in sls bean "+chargeRates[j]);
                          sellDobvalues1.setChargeRate(Double.parseDouble(chargeRates[j]));
                           if("FSBASIC".equalsIgnoreCase(weightBreaks[j])||"FSMIN".equalsIgnoreCase(weightBreaks[j])
                          ||"FSKG".equalsIgnoreCase(weightBreaks[j])||"SSBASIC".equalsIgnoreCase(weightBreaks[j])
                          ||"SSMIN".equalsIgnoreCase(weightBreaks[j])||"SSKG".equalsIgnoreCase(weightBreaks[j])
                          ||"CAFMIN".equalsIgnoreCase(weightBreaks[j])||"CAF%".equalsIgnoreCase(weightBreaks[j])
                          ||"BAFMIN".equalsIgnoreCase(weightBreaks[j])||"BAFM3".equalsIgnoreCase(weightBreaks[j])
                           ||"PSSMIN".equalsIgnoreCase(weightBreaks[j]) ||"PSSM3".equalsIgnoreCase(weightBreaks[j])
                          ||"SURCHARGE".equalsIgnoreCase(weightBreaks[j])||"CSF".equalsIgnoreCase(weightBreaks[j])
                           || weightBreaks[j].endsWith("CAF")||weightBreaks[j].endsWith("BAF")||weightBreaks[j].endsWith("CSF")
                          ||weightBreaks[j].endsWith("PSS") || !"A FREIGHT RATE".equalsIgnoreCase(rateDesc[j]))
                     
                          {
                                sellDobvalues1.setMarginPer(sellDob1.getAbperWithSurcharge());
                               sellDobvalues1.setLowerBd(0);
                               sellDobvalues1.setLowerBd(0);
                               sellDobvalues1.setServiceLevel("SCH");
                              
                          }
                          else
                          {
                             sellDobvalues1.setServiceLevel(sellDob1.getServiceLevel());
                            if("N".equals(sellDob.getOverAllMargin()))
                              sellDobvalues1.setMarginPer(ovarlMarign[j]);
                            else
                              sellDobvalues1.setMarginPer(sellDob1.getAbpersentWithFlat());
                             if(lBound!=null&&!("-".equalsIgnoreCase(lBound[j])))
                              sellDobvalues1.setLowerBd(Double.parseDouble(lBound[j]));//Modified by Mohan for Issue
                            if(uBound!=null&&!("-".equalsIgnoreCase(uBound[j])))
                            sellDobvalues1.setUpperBd(Double.parseDouble(uBound[j]));//Modified by Mohan for Issue
                          }
                          sellDobvalues1.setMinFlat(weightBreaks[j]);
                          sellDobvalues1.setSurChargeDescription(rateDesc[j]);
                          sellDobvalues1.setBuyRate(Double.parseDouble(buyChargeRates[j]));
                         // System.out.println("getLanNumber in Bean min : "+sellDob1.getLanNumber());
                          listValues.add(sellDobvalues1);
                          m++;
                        }
                      }
                    }
                    lanesList.add(listValues);
                  }
                }
              }
              else if("LIST".equalsIgnoreCase(sellDob.getWeightBreak()))
              {
                if(fslListValues!=null)
                {
                  int sizeValue     = fslListValues.size();
                  lanesList          = new ArrayList();
                  //listValues        = new ArrayList();//@@Modified by Kameswari for the WPBN issue-126038
                   for(int i=0;i<sizeValue;i++)
                  {
                    sellDob1                  = (QMSSellRatesDOB)fslListValues.get(i);
                    //double[] buyChargeRates   = sellDob1.getBuyChargeRates();
                    listValues        = new ArrayList();//@@Added by Kameswari for the WPBN issue-126038
                     String[] buyChargeRates   = sellDob1.getBuyChrRates();
                    //double[] chargeRates      = sellDob1.getChargeRatesValues();
                    String[] chargeRates      = sellDob1.getChargeRates();
                    double[] ovarlMarign      = sellDob1.getSameOvrMargin();
                    String[] weightBreaks     = sellDob1.getAllWeightBreaks();
                    String[] lBound           = sellDob1.getLBound();
                    String[] uBound           = sellDob1.getUBound();
                    String[] chargeIndicator  = sellDob1.getChargeInr();
                    int chargeRatesLen        = chargeRates.length;
                    String[]	rateDesc	  = sellDob1.getRateDescription();
                    int m = 0;
                    for(int j=0;j<chargeRatesLen;j++)
                    {
                      sellDobvalues1  = new QMSSellRatesDOB();
                      
                      if("overPivot".equalsIgnoreCase(weightBreaks[j]) && "A FREIGHT RATE".equalsIgnoreCase(rateDesc[j]))
                      {
                        if(!("-".equalsIgnoreCase(chargeRates[j])))
                        {
                          //System.out.println("getCarriers MIN in Bean Min: "+sellDob1.getCarrier_id());
                          sellDobvalues1.setCarrier_id(sellDob1.getCarrier_id());
                          sellDobvalues1.setOrigin(sellDob1.getOrigin());
                          sellDobvalues1.setOriginCountry(sellDob1.getOriginCountry());
                          sellDobvalues1.setDestination(sellDob1.getDestination());
                          sellDobvalues1.setDestinationCountry(sellDob1.getDestinationCountry());
                          sellDobvalues1.setServiceLevel(sellDob1.getServiceLevel());
                          sellDobvalues1.setTransitTime(sellDob1.getTransitTime());
                          sellDobvalues1.setFrequency(sellDob1.getFrequency());
                          sellDobvalues1.setNoteValue(sellDob1.getNoteValue());
                          sellDobvalues1.setExtNotes(sellDob1.getExtNotes());//Added by Mohan for Issue No.219976 on 08-10-2010
                          sellDobvalues1.setBuyRateId(sellDob1.getBuyRateId());
                          sellDobvalues1.setVersionNo(sellDob1.getVersionNo());//@@Added by Kameswari for the WPBN issue-146448 on 29/12/08
                          sellDobvalues1.setLanNumber(sellDob1.getLanNumber());
                          sellDobvalues1.setVersionNo(sellDob1.getVersionNo());//@@Added for the WPBN issue-146448 on 24/12/08
                          sellDobvalues1.setLineNumber(0);
                          sellDobvalues1.setLowerBd(0);
                          sellDobvalues1.setUpperBd(0);
                          sellDobvalues1.setChargerateIndicator("");
                          //System.out.println("chargeRates[j]chargeRates[j]chargeRates[j]:::: in sls bean "+chargeRates[j]);
                          sellDobvalues1.setChargeRate(Double.parseDouble(chargeRates[j]));
                         
                           if("N".equals(sellDob.getOverAllMargin()))
                              sellDobvalues1.setMarginPer(ovarlMarign[j]);
                            else
                              sellDobvalues1.setMarginPer(sellDob1.getAbpersentWithFlat());
                            sellDobvalues1.setMinFlat(weightBreaks[j]);
                            
                          sellDobvalues1.setBuyRate(Double.parseDouble(buyChargeRates[j]));
                          sellDobvalues1.setSurChargeDescription(rateDesc[j]);
                          //System.out.println("getLanNumber in Bean min : "+sellDob1.getLanNumber());
                          listValues.add(sellDobvalues1);
                          m++;
                        }
                      }
                      else
                      {
                        if(!("-".equalsIgnoreCase(chargeRates[j])))
                        {
                          //System.out.println(" m++ m++ m++ m++ m++ m++ m++ m++ m++ : "+ m);
                          sellDobvalues1.setCarrier_id(sellDob1.getCarrier_id());
                          sellDobvalues1.setOrigin(sellDob1.getOrigin());
                          sellDobvalues1.setOriginCountry(sellDob1.getOriginCountry());
                          sellDobvalues1.setDestination(sellDob1.getDestination());
                          sellDobvalues1.setDestinationCountry(sellDob1.getDestinationCountry());
                          sellDobvalues1.setServiceLevel(sellDob1.getServiceLevel());
                          sellDobvalues1.setTransitTime(sellDob1.getTransitTime());
                          sellDobvalues1.setFrequency(sellDob1.getFrequency());
                          sellDobvalues1.setNoteValue(sellDob1.getNoteValue());
                          sellDobvalues1.setExtNotes(sellDob1.getExtNotes());//Added by Mohan for Issue No.219976 on 08-10-2010
                          sellDobvalues1.setBuyRateId(sellDob1.getBuyRateId());
                          sellDobvalues1.setVersionNo(sellDob1.getVersionNo());//@@Added by Kameswari for the WPBN issue-146448 on 29/12/08
                          sellDobvalues1.setLanNumber(sellDob1.getLanNumber());
                          sellDobvalues1.setVersionNo(sellDob1.getVersionNo());//@@Added for the WPBN issue-146448 on 24/12/08
                          sellDobvalues1.setLineNumber(m+1);
                          sellDobvalues1.setChargerateIndicator("");
                          sellDobvalues1.setLowerBd(0);
                          sellDobvalues1.setUpperBd(0);
                          //System.out.println("chargeRates[j]chargeRates[j]chargeRates[j]:::: in sls bean "+chargeRates[j]);
                          sellDobvalues1.setChargeRate(Double.parseDouble(chargeRates[j]));
                          sellDobvalues1.setSurChargeDescription(rateDesc[j]);
                           if("FSBASIC".equalsIgnoreCase(weightBreaks[j])||"FSMIN".equalsIgnoreCase(weightBreaks[j])
                          ||"FSKG".equalsIgnoreCase(weightBreaks[j])||"SSBASIC".equalsIgnoreCase(weightBreaks[j])
                          ||"SSMIN".equalsIgnoreCase(weightBreaks[j])||"SSKG".equalsIgnoreCase(weightBreaks[j])
                          ||"CAFMIN".equalsIgnoreCase(weightBreaks[j])||"CAF%".equalsIgnoreCase(weightBreaks[j])
                          ||"BAFMIN".equalsIgnoreCase(weightBreaks[j])||"BAFM3".equalsIgnoreCase(weightBreaks[j])
                          ||"PSSMIN".equalsIgnoreCase(weightBreaks[j]) ||"PSSM3".equalsIgnoreCase(weightBreaks[j])
                          ||"SURCHARGE".equalsIgnoreCase(weightBreaks[j])||"CSF".equalsIgnoreCase(weightBreaks[j])
                           || weightBreaks[j].endsWith("CAF")||weightBreaks[j].endsWith("BAF")||weightBreaks[j].endsWith("CSF")
                          ||weightBreaks[j].endsWith("PSS") || !"A FREIGHT RATE".equalsIgnoreCase(rateDesc[j]))
                          {
                              sellDobvalues1.setServiceLevel("SCH");
                               sellDobvalues1.setMarginPer(sellDob1.getAbperWithSurcharge());
                          }
                          else
                          {
                             sellDobvalues1.setServiceLevel(sellDob1.getServiceLevel());
                              if("N".equals(sellDob.getOverAllMargin()))
                                sellDobvalues1.setMarginPer(ovarlMarign[j]);
                              else
                                sellDobvalues1.setMarginPer(sellDob1.getAbpersentWithMin());
                         }
                          sellDobvalues1.setMinFlat(weightBreaks[j]);
                          sellDobvalues1.setBuyRate(Double.parseDouble(buyChargeRates[j]));
                          //System.out.println("getLanNumber in Bean min : "+sellDob1.getLanNumber());
                          listValues.add(sellDobvalues1);
                           m++;
                        }
                      }
                     
                    }
                    lanesList.add(listValues);
                  }
                }
              }
            }
            else
            {
              if("LIST".equalsIgnoreCase(sellDob.getWeightBreak()))
              {
                if(fslListValues!=null)
                {
                  int sizeValue     = fslListValues.size();
                 lanesList          = new ArrayList();
                  //listValues        = new ArrayList();//@@Modified by Kameswari for the WPBN issue-126038
                   for(int i=0;i<sizeValue;i++)
                  {
                    sellDob1                  = (QMSSellRatesDOB)fslListValues.get(i);
                     listValues        = new ArrayList();//@@Added by Kameswari for the WPBN issue-126038
                    //double[] buyChargeRates   = sellDob1.getBuyChargeRates();
                     String[] buyChargeRates   = sellDob1.getBuyChrRates();
                    //double[] chargeRates      = sellDob1.getChargeRatesValues();
                    String[] chargeRates      = sellDob1.getChargeRates();
                    double[] ovarlMarign      = sellDob1.getSameOvrMargin();
                    String[] weightBreaks     = sellDob1.getAllWeightBreaks();
                    String[] lBound           = sellDob1.getLBound();
                    String[] uBound           = sellDob1.getUBound();
                    String[] chargeIndicator  = sellDob1.getChargeInr();
                    int chargeRatesLen        = chargeRates.length;
                    String[] rateDesc		  = sellDob1.getRateDescription();
                    int m = 0;
                    for(int j=0;j<chargeRatesLen;j++)
                    {
                        sellDobvalues1  = new QMSSellRatesDOB();
                       
                        if(!("-".equalsIgnoreCase(chargeRates[j])))
                        {
                          //System.out.println("getCarriers FLAT in Bean Min: "+sellDob1.getCarrier_id());
                          sellDobvalues1.setCarrier_id(sellDob1.getCarrier_id());
                          sellDobvalues1.setOrigin(sellDob1.getOrigin());
                          sellDobvalues1.setOriginCountry(sellDob1.getOriginCountry());
                          sellDobvalues1.setDestination(sellDob1.getDestination());
                          sellDobvalues1.setDestinationCountry(sellDob1.getDestinationCountry());
                         // sellDobvalues1.setServiceLevel(sellDob1.getServiceLevel());
                          sellDobvalues1.setTransitTime(sellDob1.getTransitTime());
                          sellDobvalues1.setFrequency(sellDob1.getFrequency());
                          sellDobvalues1.setNoteValue(sellDob1.getNoteValue());
                          sellDobvalues1.setExtNotes(sellDob1.getExtNotes());//Added by Mohan for Issue No.219976 on 08-10-2010
                          sellDobvalues1.setBuyRateId(sellDob1.getBuyRateId());
                          sellDobvalues1.setVersionNo(sellDob1.getVersionNo());//@@Added by Kameswari for the WPBN issue-146448 on 29/12/08
                          sellDobvalues1.setLanNumber(sellDob1.getLanNumber());
                          sellDobvalues1.setVersionNo(sellDob1.getVersionNo());//@@Added for the WPBN issue-146448 on 24/12/08
                          sellDobvalues1.setLineNumber(m);
                          sellDobvalues1.setLowerBd(0);
                          sellDobvalues1.setUpperBd(0);
                          sellDobvalues1.setChargerateIndicator("");
                          sellDobvalues1.setSurChargeDescription(rateDesc[j]);
                          //System.out.println("chargeRates[j]chargeRates[j]chargeRates[j]:::: in sls bean "+chargeRates[j]);
                          sellDobvalues1.setChargeRate(Double.parseDouble(chargeRates[j]));
                         if("FSBASIC".equalsIgnoreCase(weightBreaks[j])||"FSMIN".equalsIgnoreCase(weightBreaks[j])
                          ||"FSKG".equalsIgnoreCase(weightBreaks[j])||"SSBASIC".equalsIgnoreCase(weightBreaks[j])
                          ||"SSMIN".equalsIgnoreCase(weightBreaks[j])||"SSKG".equalsIgnoreCase(weightBreaks[j])
                          ||"CAFMIN".equalsIgnoreCase(weightBreaks[j])||"CAF%".equalsIgnoreCase(weightBreaks[j])
                          ||"BAFMIN".equalsIgnoreCase(weightBreaks[j])||"BAFM3".equalsIgnoreCase(weightBreaks[j])
                          ||"PSSMIN".equalsIgnoreCase(weightBreaks[j]) ||"PSSM3".equalsIgnoreCase(weightBreaks[j])
                          ||"SURCHARGE".equalsIgnoreCase(weightBreaks[j])||"CSF".equalsIgnoreCase(weightBreaks[j])
                          || weightBreaks[j].endsWith("CAF")||weightBreaks[j].endsWith("BAF")||weightBreaks[j].endsWith("CSF")
                          ||weightBreaks[j].endsWith("PSS") || !"A FREIGHT RATE".equalsIgnoreCase(rateDesc[j]))
                          {
                              sellDobvalues1.setMarginPer(sellDob1.getAbperWithSurcharge());
                               sellDobvalues1.setServiceLevel("SCH");
                               sellDobvalues1.setRateDescription(sellDob1.getRateDescription());
                               
                          }
                          else
                          {
                            sellDobvalues1.setServiceLevel(sellDob1.getServiceLevel());
                            //sellDobvalues1.setRateDescription("A FREIGHT RATE");
                            if("N".equals(sellDob.getOverAllMargin()))
                              sellDobvalues1.setMarginPer(ovarlMarign[j]);
                            else
                              sellDobvalues1.setMarginPer(sellDob1.getAbpersentWithMin());
                          }
                          sellDobvalues1.setMinFlat(weightBreaks[j]);
                          sellDobvalues1.setBuyRate(Double.parseDouble(buyChargeRates[j]));
                          //System.out.println("getLanNumber in Bean min : "+sellDob1.getLanNumber());
                          listValues.add(sellDobvalues1);
                          m++;
                        }
                      }
                      lanesList.add(listValues);
                    }
                  }
                }
            }
          }
            
            //System.out.println("******Before*********");
            sellRateHome      = (QMSSellRatesEntityLocalHome)lookUpBean.getEJBLocalHome("java:comp/env/QMSSellRatesEntity");
           // sellRateEntity    = sellRateHome.create(sellDob,listValues,loginBean,operation);
             sellRateEntity    = sellRateHome.create(sellDob,lanesList,loginBean,operation);//@@Modified by Kameswari for the WPBN issue-126038
            //system.out.println("******After*********");
            massage ="successfully";
        }
        catch(Exception e)
        {
          //Logger.error(FILE_NAME,"EXception in insertSellRates()-->"+e.toString());
          logger.error(FILE_NAME+"EXception in insertSellRates()-->"+e.toString());
          e.printStackTrace();
          throw new EJBException(e.toString());
        }
      return massage;
   }
  public StringBuffer validateSellRatesHdrData(QMSSellRatesDOB sellDob)
  {
      QMSSellRatesDAO             sellRatesDao          =   new QMSSellRatesDAO();
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
    
  public StringBuffer validateSellRateFormHdr(QMSSellRatesDOB sellRateDetails) throws EJBException
  {
    StringBuffer                 errors             = null;
    Connection                   connection         = null;
    PreparedStatement            pStmt              = null;
    CallableStatement            csmt               = null;
    ResultSet                    rs                 = null;
    String                       shipmentMode       = null;
    String                       currencyId         = null;
    String                       origin             = null;
    String                       originCountry      = null;
    String                       destination        = null;
    String                       destinationCountry = null;
    String                       serviceLevel       = null;
    String                       operation          = null;
    String                       whereclause        = "";
    
    String                       currency_sql       = "";
    String                       location_sql       = "";
    String                       country_sql        = "";
    String                       serviceLevel_sql   = "";
    String                       terminalId_sql     = "";
    int                          count              = 0;
    
    try
    {
      connection           =    operationsImpl.getConnection();
      
      errors               =    new StringBuffer();
      shipmentMode         =    sellRateDetails.getShipmentMode();
      currencyId           =    sellRateDetails.getCurrencyId();
      origin               =    sellRateDetails.getOrigin();
      destination          =    sellRateDetails.getDestination();
      originCountry        =    sellRateDetails.getOriginCountry();
      destinationCountry   =    sellRateDetails.getDestinationCountry();
      serviceLevel         =    sellRateDetails.getServiceLevel();
      operation            =    sellRateDetails.getOperation();
      
      if(shipmentMode!=null)
      {			
				
				if(shipmentMode.equals("4")){
					whereclause = "WHERE SERVICELEVELID = ? AND SHIPMENTMODE IN (4,5,6,7)";
				}else 	if(shipmentMode.equals("1")){
					whereclause = "WHERE SERVICELEVELID = ?  AND SHIPMENTMODE IN (1,3,5,7) ";
				}else 	if(shipmentMode.equals("All")){
					whereclause = "WHERE SERVICELEVELID = ?  ";
				}else{
					whereclause = "WHERE SERVICELEVELID = ?  AND SHIPMENTMODE IN (2,3,6,7) ";
				}
      }
      
      currency_sql         =    "SELECT COUNT(*) NO_ROWS FROM FS_COUNTRYMASTER WHERE CURRENCYID=?";
      
      if("2".equals(shipmentMode))
        location_sql         =    "SELECT COUNT(*) NO_ROWS FROM FS_FRS_PORTMASTER WHERE PORTID=?";
      else
        location_sql         =    "SELECT COUNT(*) NO_ROWS FROM FS_FR_LOCATIONMASTER WHERE LOCATIONID=?";
    
      if("2".equals(shipmentMode))
        country_sql          =  "SELECT COUNT(*) NO_ROWS FROM FS_COUNTRYMASTER CON,FS_FRS_PORTMASTER PORT "+
                                "WHERE PORT.COUNTRYID=CON.COUNTRYID AND PORT.PORTID=? AND CON.COUNTRYID=? "+
                                "AND (CON.INVALIDATE='F' OR CON.INVALIDATE IS NULL)";
      else
        country_sql          =  "SELECT COUNT(*) NO_ROWS FROM FS_COUNTRYMASTER CON,FS_FR_LOCATIONMASTER LOC "+
                                "WHERE LOC.COUNTRYID=CON.COUNTRYID AND LOC.LOCATIONID=? AND CON.COUNTRYID=? "+
                                "AND (CON.INVALIDATE='F' OR CON.INVALIDATE IS NULL)";
                                
      serviceLevel_sql     =    "SELECT COUNT(*) NO_ROWS FROM FS_FR_SERVICELEVELMASTER "+whereclause;
      
      //terminalId_sql       =
      if("Modify".equalsIgnoreCase(operation))
      {
        String returnVal     =     null;
        csmt                 =     connection.prepareCall("{CALL PKG_QMS_CHARGES.ISEXISTINTHEHIRARCHY(?,?,?,?)}");
        
        csmt.setString(1,operation);
        csmt.setString(2,sellRateDetails.getTerminalId());
        csmt.setString(3,sellRateDetails.getLoginTerminalId());
        csmt.registerOutParameter(4,OracleTypes.VARCHAR);
        
        csmt.execute();
        
        returnVal             =    csmt.getString(4);
        
        if(!"1".equalsIgnoreCase(returnVal))
          errors.append("Terminal Id is Invalid or Does Not Fall Under Your Jurisdiction.<br>");
          
      }
      
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
      
      pStmt                =     connection.prepareStatement(location_sql);
      pStmt.setString(1,origin);
      rs                   =     pStmt.executeQuery();
      
      while(rs.next())
        count = rs.getInt("NO_ROWS");
        
      if(count == 0)
      {
        if("2".equals(shipmentMode))
          errors.append("Port of Loading is Invalid.<br>");
        else
          errors.append("Origin Location is Invalid.");
      }
      
      if(rs!=null)
        rs.close();
      if(pStmt!=null)
        pStmt.close();
        
      count = 0;
      
      pStmt                =     connection.prepareStatement(location_sql);
      pStmt.setString(1,destination);
      rs                   =     pStmt.executeQuery();
      
      while(rs.next())
        count = rs.getInt("NO_ROWS");
        
      if(count == 0)
      {
        if("2".equals(shipmentMode))
          errors.append("Port of Discharge is Invalid.<br>");
        else
          errors.append("Destination Location is Invalid.<br>");
      }
      
      if(rs!=null)
        rs.close();
      if(pStmt!=null)
        pStmt.close();
     
      if(originCountry!=null && !"".equals(originCountry))
      {
        count = 0;
        pStmt                =     connection.prepareStatement(country_sql);
        pStmt.setString(1,origin);
        pStmt.setString(2,originCountry);
        rs                   =     pStmt.executeQuery();
        
        while(rs.next())
          count = rs.getInt("NO_ROWS");
          
        if(count == 0)
          errors.append("Origin Country is Invalid.<br>");
        
        if(rs!=null)
          rs.close();
        if(pStmt!=null)
          pStmt.close();
      }
      
      if(destinationCountry!=null && !"".equals(destinationCountry))
      {
        count = 0;
        pStmt                =     connection.prepareStatement(country_sql);
        pStmt.setString(1,destination);
        pStmt.setString(2,destinationCountry);
        rs                   =     pStmt.executeQuery();
        
        while(rs.next())
          count = rs.getInt("NO_ROWS");
          
        if(count == 0)
          errors.append("Destination Country is Invalid.<br>");
        
        if(rs!=null)
          rs.close();
        if(pStmt!=null)
          pStmt.close();        
      }
      if(serviceLevel!=null && serviceLevel.trim().length()!=0)
      {
        count = 0;
        pStmt                =     connection.prepareStatement(serviceLevel_sql);
      
        pStmt.setString(1,serviceLevel);
        rs                   =     pStmt.executeQuery();
      
        while(rs.next())
          count = rs.getInt("NO_ROWS"); 
        
        if(count == 0)
          errors.append("Service Level is Invalid.<br>");
      }
      
    }
    catch(Exception e)
    {
      //Logger.error(FILE_NAME,"Error while validating Sell Rate Details."+e.toString());
      logger.error(FILE_NAME+"Error while validating Sell Rate Details."+e.toString());
      e.printStackTrace();
      throw new EJBException(e.toString());
    }
    finally
    {
      ConnectionUtil.closeConnection(connection,pStmt,rs);
    }
    return errors;
  }
  
  public boolean insertSellRateDetails(java.util.ArrayList dataList)
  {
      QMSSellRatesEntityLocalHome home  = null;
      QMSSellRatesEntityLocal     local = null;
      boolean flag                      = true;
   
      try
      {
         //ictxt         =          new InitialContext();
         home          =          (QMSSellRatesEntityLocalHome)lookUpBean.getEJBLocalHome("java:comp/env/QMSSellRatesEntity");
         local         =          home.create(dataList);
      }
      catch(Exception e)
      {
        flag = false;
        //Logger.error(FILE_NAME,"error while inserting records"+e.toString());
        logger.error(FILE_NAME+"error while inserting records"+e.toString());
        e.printStackTrace();
        throw new EJBException(e);
      }
      return flag;
  }
  
  public ArrayList getBuySellRateDetails(QMSSellRatesDOB sellRateDetails) throws ObjectNotFoundException
  {
    Connection connection               = null;
    PreparedStatement pStmt             = null;
    PreparedStatement pStmtWeightBreak  = null;
    ResultSet rs                        = null;
    ResultSet rsWeightBreak             = null;
    
    
    QMSSellRatesDOB buyRateDetails  = null;
    
    ArrayList buyRateDetailsArray   = new ArrayList();
    ArrayList weightBreakHdr        = null;
    ArrayList  rateDescHdr          = null;
    HashMap hMap                    = null;
    
    HashMap currencyMap             = new HashMap();
    
    String operation                = null;
    String shipmentMode             = null;
    String rateType                 = null;
    String weightBreak              = null;
    String[] carrier_ids            = null;
    String serviceLevel             = null;
    String originLocation           = null;
    String destinationLocation      = null;
    String currencyId               = null;
    String accessLevel              = null;
    String weightClass              = null;
    
    String terminalId               = null;
    
    String carriers                 = "";
    String carrierSubQry            = "";
    
    String selectQuery              = "";
    String weightBreakSlabQuery     = "";
    String dataAccessQuery          = "";
    String serviceLevelDtlQry       = "";
    String serviceLevelSubDtlQry    = "";
    int    count                    = 0;
    
    String  flatRate                = null;
    
    String key                      = null;
    String currencyKey              = null;
    boolean addToList               = true;
    double  currencyFactor          = 0.0;
    String chargeRate               = null;
    double convertedRate            = 0.0;
    String   weightBreakSurQuery    =  null;
    try
    {
      connection          = operationsImpl.getConnection();
      
      operation           = sellRateDetails.getOperation();
      shipmentMode        = sellRateDetails.getShipmentMode();
      rateType            = sellRateDetails.getRateType();
      weightBreak         = sellRateDetails.getWeightBreak();
      carrier_ids         = sellRateDetails.getCarriers();
      originLocation      = sellRateDetails.getOrigin();
      destinationLocation = sellRateDetails.getDestination();
      serviceLevel        = sellRateDetails.getServiceLevel();
      currencyId          = sellRateDetails.getCurrencyId();
      accessLevel         = sellRateDetails.getAccessLevel();
      weightClass         = sellRateDetails.getWeightClass();
      terminalId          = sellRateDetails.getTerminalId();
      
      if(carrier_ids!=null)
      {
    	  int carrIdsLen	=	carrier_ids.length;
        for(int i=0;i<carrIdsLen;i++)
        {
          if((i+1)==carrier_ids.length)
              carriers  = carriers + "'"+carrier_ids[i]+"'";
          else        
            carriers  = carriers + "'"+carrier_ids[i]+"',";
        }
        if(carriers!=null && carriers.trim().length()!=0)
          carrierSubQry = " AND DTL.CARRIER_ID IN ("+carriers+")  ";
      }
      /*if(serviceLevel!=null && serviceLevel.trim().length()!=0)
      {
        serviceLevelDtlQry      = " AND DTL.SERVICE_LEVEL= '"+serviceLevel+"' ";
        serviceLevelSubDtlQry   = "";
      }*/
      
      if("Add".equalsIgnoreCase(operation))
      {
          if(serviceLevel!=null && serviceLevel.trim().length()!=0)
            serviceLevelDtlQry   =  " AND DTL.SERVICE_LEVEL='"+serviceLevel+"' ";
          
          if("H".equalsIgnoreCase(accessLevel))
          {
            dataAccessQuery = "SELECT TERMINALID FROM FS_FR_TERMINALMASTER WHERE OPER_ADMIN_FLAG <> 'H'";
          }
          else
          {
            dataAccessQuery = "SELECT CHILD_TERMINAL_ID TERM_ID FROM FS_FR_TERMINAL_REGN CONNECT BY "+
                              "PRIOR CHILD_TERMINAL_ID=PARENT_TERMINAL_ID START WITH PARENT_TERMINAL_ID = MAS.TERMINALID";
          }
      
      /*selectQuery = "SELECT MAS.TERMINALID,MAS.BUYRATEID,DTL.CHARGERATE,DTL.WEIGHT_BREAK_SLAB,DTL.TRANSIT_TIME,DTL.LOWERBOUND,DTL.UPPERBOUND,DTL.LINE_NO,DTL.LANE_NO, "+
       "DTL.CARRIER_ID,DTL.FREQUENCY,DTL.EFFECTIVE_FROM, "+
                    "DTL.VALID_UPTO,DTL.CHARGERATE_INDICATOR,MAS.CONSOLE_TYPE,NOTES,MAS.CURRENCY,DTL.SERVICE_LEVEL SERVICE_LEVEL FROM QMS_BUYRATES_MASTER MAS,QMS_BUYRATES_DTL DTL WHERE MAS.WEIGHT_BREAK= ? AND "+
                    "MAS.RATE_TYPE= ? AND MAS.BUYRATEID=DTL.BUYRATEID AND MAS.VERSION_NO=DTL.VERSION_NO AND DTL.ORIGIN=? AND DTL.DESTINATION=? "+
                    "AND MAS.SHIPMENT_MODE=? "+serviceLevelDtlQry+carrierSubQry+
                    "AND (ACTIVEINACTIVE IS NULL OR ACTIVEINACTIVE='A') AND MAS.TERMINALID NOT IN("+dataAccessQuery+") AND (DTL.BUYRATEID,DTL.LINE_NO,DTL.LANE_NO,DTL.VERSION_NO) NOT IN"+
                    "(SELECT BUYRATEID,LINE_NO,LANE_NO FROM QMS_REC_CON_SELLRATESDTL SUBDTL,QMS_REC_CON_SELLRATESMASTER SUBMAS "+
                    "WHERE SUBDTL.BUYRATEID=MAS.BUYRATEID AND SUBDTL.LINE_NO=DTL.LINE_NO AND SUBDTL.LANE_NO = DTL.LANE_NO AND SUBMAS.RC_FLAG='C' "+
                    "AND SUBMAS.REC_CON_ID=SUBDTL.REC_CON_ID AND SUBDTL.AI_FLAG='A' AND SUBMAS.TERMINALID  IN (SELECT PARENT_TERMINAL_ID TERM_ID FROM FS_FR_TERMINAL_REGN "+ 
                    "CONNECT BY  CHILD_TERMINAL_ID=PRIOR PARENT_TERMINAL_ID START WITH CHILD_TERMINAL_ID=SUBMAS.TERMINALID UNION ALL "+
                    "SELECT '"+terminalId+"' TERM_ID FROM DUAL UNION SELECT TERMINALID  TERM_ID "+
                    "FROM FS_FR_TERMINALMASTER WHERE OPER_ADMIN_FLAG='H')) ORDER BY MAS.BUYRATEID,DTL.LANE_NO,DTL.LINE_NO";*/

       //@@Modified by Kameswari for the WPBN issue-146448 on 03/01/09
        selectQuery = "SELECT MAS.TERMINALID,MAS.BUYRATEID,MAS.VERSION_NO,DTL.CHARGERATE,DTL.WEIGHT_BREAK_SLAB,DTL.TRANSIT_TIME,DTL.LOWERBOUND,DTL.UPPERBOUND,DTL.LINE_NO,DTL.LANE_NO, "+
                    "DTL.CARRIER_ID,DTL.FREQUENCY,DTL.EFFECTIVE_FROM, "+
                    "DTL.VALID_UPTO,DTL.CHARGERATE_INDICATOR,MAS.CONSOLE_TYPE,NOTES,MAS.CURRENCY,DTL.SERVICE_LEVEL SERVICE_LEVEL FROM QMS_BUYRATES_MASTER MAS,QMS_BUYRATES_DTL DTL WHERE MAS.WEIGHT_BREAK= ? AND "+
                    "MAS.RATE_TYPE= ? AND MAS.BUYRATEID=DTL.BUYRATEID AND MAS.VERSION_NO=DTL.VERSION_NO AND DTL.ORIGIN=? AND DTL.DESTINATION=? "+
                    "AND MAS.SHIPMENT_MODE=? "+serviceLevelDtlQry+carrierSubQry+
                    "AND (ACTIVEINACTIVE IS NULL OR ACTIVEINACTIVE='A') AND MAS.TERMINALID NOT IN("+dataAccessQuery+") AND (DTL.BUYRATEID,DTL.LINE_NO,DTL.LANE_NO,DTL.VERSION_NO) NOT IN"+
                    "(SELECT BUYRATEID,LINE_NO,LANE_NO,VERSION_NO FROM QMS_REC_CON_SELLRATESDTL SUBDTL,QMS_REC_CON_SELLRATESMASTER SUBMAS "+
                    "WHERE SUBDTL.BUYRATEID=MAS.BUYRATEID AND SUBDTL.VERSION_NO=MAS.VERSION_NO  AND SUBDTL.LINE_NO=DTL.LINE_NO AND SUBDTL.LANE_NO = DTL.LANE_NO AND SUBMAS.RC_FLAG='C' "+
                    "AND SUBMAS.REC_CON_ID=SUBDTL.REC_CON_ID AND SUBDTL.AI_FLAG='A' AND SUBMAS.TERMINALID  IN (SELECT PARENT_TERMINAL_ID TERM_ID FROM FS_FR_TERMINAL_REGN "+ 
                    "CONNECT BY  CHILD_TERMINAL_ID=PRIOR PARENT_TERMINAL_ID START WITH CHILD_TERMINAL_ID=SUBMAS.TERMINALID UNION ALL "+
                    "SELECT '"+terminalId+"' TERM_ID FROM DUAL UNION SELECT TERMINALID  TERM_ID "+
                    "FROM FS_FR_TERMINALMASTER WHERE OPER_ADMIN_FLAG='H')) ORDER BY MAS.BUYRATEID,DTL.LANE_NO,DTL.LINE_NO";
                    
                    
      weightBreakSlabQuery=  "SELECT DTL.WEIGHT_BREAK_SLAB FROM QMS_BUYRATES_MASTER MAS,QMS_BUYRATES_DTL DTL WHERE MAS.WEIGHT_BREAK= ? AND "+
                    "MAS.RATE_TYPE= ? AND MAS.BUYRATEID=DTL.BUYRATEID AND DTL.ORIGIN=? AND DTL.DESTINATION=? "+
                    "AND MAS.SHIPMENT_MODE=? "+serviceLevelDtlQry+carrierSubQry+
                    "AND (ACTIVEINACTIVE IS NULL OR ACTIVEINACTIVE='A') AND MAS.TERMINALID NOT IN("+dataAccessQuery+") AND (DTL.BUYRATEID,DTL.LINE_NO,DTL.LANE_NO) NOT IN"+
                    "(SELECT BUYRATEID,LINE_NO,LANE_NO FROM QMS_REC_CON_SELLRATESDTL SUBDTL,QMS_REC_CON_SELLRATESMASTER SUBMAS "+
                    "WHERE SUBDTL.BUYRATEID=MAS.BUYRATEID AND SUBDTL.LINE_NO=DTL.LINE_NO AND SUBDTL.LANE_NO = DTL.LANE_NO AND SUBMAS.RC_FLAG='C' "+
                    "AND SUBMAS.REC_CON_ID=SUBDTL.REC_CON_ID AND SUBDTL.AI_FLAG='A' AND SUBMAS.TERMINALID IN(SELECT PARENT_TERMINAL_ID TERM_ID FROM FS_FR_TERMINAL_REGN "+
                    "CONNECT BY  CHILD_TERMINAL_ID=PRIOR PARENT_TERMINAL_ID START WITH CHILD_TERMINAL_ID=SUBMAS.TERMINALID UNION ALL "+
                    "SELECT '"+terminalId+"' TERM_ID FROM DUAL UNION ALL SELECT TERMINALID TERM_ID "+
                    "FROM FS_FR_TERMINALMASTER WHERE OPER_ADMIN_FLAG='H')) AND DTL.WEIGHT_BREAK_SLAB "+
                    "NOT IN ('MIN')  AND DTL.SERVICE_LEVEL NOT IN ('SCH') ORDER BY TO_NUMBER(DTL.WEIGHT_BREAK_SLAB)";
     
  weightBreakSurQuery=  "SELECT DTL.WEIGHT_BREAK_SLAB ,DTL.RATE_DESCRIPTION FROM QMS_BUYRATES_MASTER MAS,QMS_BUYRATES_DTL DTL WHERE MAS.WEIGHT_BREAK= ? AND "+
                    "MAS.RATE_TYPE= ? AND MAS.BUYRATEID=DTL.BUYRATEID AND DTL.ORIGIN=? AND DTL.DESTINATION=? "+
                    "AND MAS.SHIPMENT_MODE=? "+carrierSubQry+
                    "AND (ACTIVEINACTIVE IS NULL OR ACTIVEINACTIVE='A') AND MAS.TERMINALID NOT IN("+dataAccessQuery+") AND (DTL.BUYRATEID,DTL.LINE_NO,DTL.LANE_NO) NOT IN"+
                    "(SELECT BUYRATEID,LINE_NO,LANE_NO FROM QMS_REC_CON_SELLRATESDTL SUBDTL,QMS_REC_CON_SELLRATESMASTER SUBMAS "+
                    "WHERE SUBDTL.BUYRATEID=MAS.BUYRATEID AND SUBDTL.LINE_NO=DTL.LINE_NO AND SUBDTL.LANE_NO = DTL.LANE_NO AND SUBMAS.RC_FLAG='C' "+
                    "AND SUBMAS.REC_CON_ID=SUBDTL.REC_CON_ID AND SUBDTL.AI_FLAG='A' AND SUBMAS.TERMINALID IN(SELECT PARENT_TERMINAL_ID TERM_ID FROM FS_FR_TERMINAL_REGN "+
                    "CONNECT BY  CHILD_TERMINAL_ID=PRIOR PARENT_TERMINAL_ID START WITH CHILD_TERMINAL_ID=SUBMAS.TERMINALID UNION ALL "+
                    "SELECT '"+terminalId+"' TERM_ID FROM DUAL UNION ALL SELECT TERMINALID TERM_ID "+
                    "FROM FS_FR_TERMINALMASTER WHERE OPER_ADMIN_FLAG='H')) "+
                    "  AND DTL.SERVICE_LEVEL  IN ('SCH') ORDER BY DTL.WEIGHT_BREAK_SLAB";

      }
      else if("Modify".equalsIgnoreCase(operation))
      {
         
         if(serviceLevel!=null && serviceLevel.trim().length()!=0)
         {
            serviceLevelDtlQry     = " AND DTL.SERVICELEVEL_ID='"+serviceLevel+"' ";
            serviceLevelSubDtlQry  = " AND SUBDTL.SERVICELEVEL_ID='"+serviceLevel+"' ";
         }
      
     /* selectQuery  = "SELECT MAS.TERMINALID,MAS.ACCESSLEVEL,MAS.REC_CON_ID,BUYRATEID,DTL.CHARGERATE,DTL.BUY_RATE_AMT BUYRATE,DTL.WEIGHTBREAKSLAB WEIGHT_BREAK_SLAB,DTL.TRANSIT_TIME,DTL.LOWRER_BOUND LOWERBOUND,DTL.UPPER_BOUND UPPERBOUND,"+
                     "DTL.CARRIER_ID,DTL.FREQUENCY,DTL.LINE_NO,DTL.LANE_NO,MAS.CREATED_TSTMP,DTL.CHARGERATE_INDICATOR,DTL.CONSOLE_TYPE, "+
                     "PALLET_CAPACITY,PALLET_BYRATE,AVEREAGE_UNIT,LOOSE_SPACE,NOTES,MAS.CURRENCY,DTL.SERVICELEVEL_ID SERVICE_LEVEL "+
                     "FROM QMS_REC_CON_SELLRATESMASTER MAS,QMS_REC_CON_SELLRATESDTL DTL WHERE RC_FLAG='C' AND MAS.REC_CON_ID=DTL.REC_CON_ID AND WEIGHT_BREAK=? AND RATE_TYPE=? "+
                     "AND ORIGIN=? AND DESTINATION=? AND MAS.SHIPMENT_MODE=? "+serviceLevelDtlQry+carrierSubQry+
                     "AND MAS.TERMINALID=? AND (DTL.BUYRATEID,DTL.REC_CON_ID) IN (SELECT SUBDTL.BUYRATEID,SUBDTL.REC_CON_ID FROM QMS_REC_CON_SELLRATESMASTER SUBMAS,QMS_REC_CON_SELLRATESDTL SUBDTL WHERE "+
                     "RC_FLAG='C' "+serviceLevelSubDtlQry+" AND WEIGHT_BREAK=? AND RATE_TYPE=? AND ORIGIN=? AND DESTINATION=? AND SHIPMENT_MODE=? AND SUBMAS.TERMINALID=? AND SUBMAS.REC_CON_ID=SUBDTL.REC_CON_ID "+
                     "AND DTL.FREQUENCY=SUBDTL.FREQUENCY AND SUBDTL.CARRIER_ID = DTL.CARRIER_ID AND (SUBDTL.AI_FLAG='A' OR SUBDTL.AI_FLAG IS NULL) ) ORDER BY BUYRATEID,DTL.REC_CON_ID,LANE_NO,LINE_NO";*/
       //@@Modified by Kameswari for the WPBN issue-146448 on 03/01/09
       
        selectQuery  = "SELECT MAS.TERMINALID,MAS.ACCESSLEVEL,MAS.REC_CON_ID,BUYRATEID,DTL.CHARGERATE,DTL.BUY_RATE_AMT BUYRATE,DTL.WEIGHTBREAKSLAB WEIGHT_BREAK_SLAB,DTL.TRANSIT_TIME,DTL.LOWRER_BOUND LOWERBOUND,DTL.UPPER_BOUND UPPERBOUND,"+
                     "DTL.CARRIER_ID,DTL.FREQUENCY,DTL.LINE_NO,DTL.LANE_NO,DTL.VERSION_NO,MAS.CREATED_TSTMP,DTL.CHARGERATE_INDICATOR,DTL.CONSOLE_TYPE, "+
                     "PALLET_CAPACITY,PALLET_BYRATE,AVEREAGE_UNIT,LOOSE_SPACE,NOTES,MAS.CURRENCY,DTL.SERVICELEVEL_ID SERVICE_LEVEL "+
                     "FROM QMS_REC_CON_SELLRATESMASTER MAS,QMS_REC_CON_SELLRATESDTL DTL WHERE RC_FLAG='C' AND MAS.REC_CON_ID=DTL.REC_CON_ID AND WEIGHT_BREAK=? AND RATE_TYPE=? "+
                     "AND ORIGIN=? AND DESTINATION=? AND MAS.SHIPMENT_MODE=? "+serviceLevelDtlQry+carrierSubQry+
                     "AND MAS.TERMINALID=? AND (DTL.BUYRATEID,DTL.REC_CON_ID) IN (SELECT SUBDTL.BUYRATEID,SUBDTL.REC_CON_ID FROM QMS_REC_CON_SELLRATESMASTER SUBMAS,QMS_REC_CON_SELLRATESDTL SUBDTL WHERE "+
                     "RC_FLAG='C' "+serviceLevelSubDtlQry+" AND WEIGHT_BREAK=? AND RATE_TYPE=? AND ORIGIN=? AND DESTINATION=? AND SHIPMENT_MODE=? AND SUBMAS.TERMINALID=? AND SUBMAS.REC_CON_ID=SUBDTL.REC_CON_ID "+
                     "AND DTL.FREQUENCY=SUBDTL.FREQUENCY AND SUBDTL.CARRIER_ID = DTL.CARRIER_ID AND (SUBDTL.AI_FLAG='A' OR SUBDTL.AI_FLAG IS NULL) ) ORDER BY BUYRATEID,DTL.REC_CON_ID,LANE_NO,LINE_NO";

                    
      weightBreakSlabQuery=  "SELECT DTL.WEIGHTBREAKSLAB WEIGHT_BREAK_SLAB FROM QMS_REC_CON_SELLRATESMASTER MAS,QMS_REC_CON_SELLRATESDTL DTL WHERE "+
                     "RC_FLAG='C' AND MAS.REC_CON_ID=DTL.REC_CON_ID AND WEIGHT_BREAK=? AND RATE_TYPE=? "+
                     "AND ORIGIN=? AND DESTINATION=? AND MAS.SHIPMENT_MODE=? "+serviceLevelDtlQry+carrierSubQry+
                     "AND MAS.TERMINALID = ? AND (DTL.BUYRATEID,DTL.REC_CON_ID) IN (SELECT SUBDTL.BUYRATEID,SUBDTL.REC_CON_ID FROM QMS_REC_CON_SELLRATESMASTER SUBMAS,QMS_REC_CON_SELLRATESDTL SUBDTL WHERE "+
                     "RC_FLAG='C' "+serviceLevelSubDtlQry+" AND WEIGHT_BREAK=? AND RATE_TYPE=? AND ORIGIN=? AND DESTINATION=? AND SHIPMENT_MODE=? AND TERMINALID = ? AND SUBMAS.REC_CON_ID=SUBDTL.REC_CON_ID "+
                     "AND DTL.FREQUENCY=SUBDTL.FREQUENCY AND SUBDTL.CARRIER_ID = DTL.CARRIER_ID AND (SUBDTL.AI_FLAG='A' OR SUBDTL.AI_FLAG IS NULL)) AND DTL.WEIGHTBREAKSLAB NOT IN ('MIN') AND DTL.SERVICELEVEL_ID NOT IN ('SCH') ORDER BY TO_NUMBER(DTL.WEIGHTBREAKSLAB)";
                     
                     
    weightBreakSurQuery=  "SELECT DTL.WEIGHTBREAKSLAB WEIGHT_BREAK_SLAB,DTL.RATE_DESCRIPTION FROM QMS_REC_CON_SELLRATESMASTER MAS,QMS_REC_CON_SELLRATESDTL DTL WHERE "+
                     "RC_FLAG='C' AND MAS.REC_CON_ID=DTL.REC_CON_ID AND WEIGHT_BREAK=? AND RATE_TYPE=? "+
                     "AND ORIGIN=? AND DESTINATION=? AND MAS.SHIPMENT_MODE=? "+carrierSubQry+
                     "AND MAS.TERMINALID = ? AND (DTL.BUYRATEID,DTL.REC_CON_ID) IN (SELECT SUBDTL.BUYRATEID,SUBDTL.REC_CON_ID FROM QMS_REC_CON_SELLRATESMASTER SUBMAS,QMS_REC_CON_SELLRATESDTL SUBDTL WHERE "+
                     "RC_FLAG='C' "+" AND WEIGHT_BREAK=? AND RATE_TYPE=? AND ORIGIN=? AND DESTINATION=? AND SHIPMENT_MODE=? AND TERMINALID = ? AND SUBMAS.REC_CON_ID=SUBDTL.REC_CON_ID "+
                     "AND DTL.FREQUENCY=SUBDTL.FREQUENCY AND SUBDTL.CARRIER_ID = DTL.CARRIER_ID AND (SUBDTL.AI_FLAG='A' OR SUBDTL.AI_FLAG IS NULL))  AND DTL.SERVICELEVEL_ID  IN ('SCH') ORDER BY DTL.WEIGHTBREAKSLAB";

      }
           
      pStmt  = connection.prepareStatement(selectQuery);
      
      pStmt.setString(1,weightBreak.toUpperCase());
      pStmt.setString(2,rateType.toUpperCase());
      pStmt.setString(3,originLocation);
      pStmt.setString(4,destinationLocation);
      pStmt.setString(5,shipmentMode);
      //pStmt.setString(6,serviceLevel);
      
      if("Modify".equals(operation))
      {
        pStmt.setString(6,terminalId);
        //pStmt.setString(7,serviceLevel);
        pStmt.setString(7,weightBreak.toUpperCase());
        pStmt.setString(8,rateType.toUpperCase());
        pStmt.setString(9,originLocation);
        pStmt.setString(10,destinationLocation);
        pStmt.setString(11,shipmentMode);
        pStmt.setString(12,terminalId);
      }
      
      rs  =  pStmt.executeQuery();
      
      if("Flat".equalsIgnoreCase(weightBreak))
      {
        weightBreakHdr = new ArrayList();
         rateDescHdr = new ArrayList();
       /* pStmtWeightBreak = connection.prepareStatement(weightBreakSlabQuery);
      
        pStmtWeightBreak.setString(1,weightBreak.toUpperCase());
        pStmtWeightBreak.setString(2,rateType.toUpperCase());
        pStmtWeightBreak.setString(3,originLocation);
        pStmtWeightBreak.setString(4,destinationLocation);
        pStmtWeightBreak.setString(5,shipmentMode);
        //pStmtWeightBreak.setString(6,serviceLevel);
        
        if("Modify".equalsIgnoreCase(operation))
        {
          pStmtWeightBreak.setString(6,terminalId);
          //pStmtWeightBreak.setString(8,serviceLevel);
          pStmtWeightBreak.setString(7,weightBreak.toUpperCase());
          pStmtWeightBreak.setString(8,rateType.toUpperCase());
          pStmtWeightBreak.setString(9,originLocation);
          pStmtWeightBreak.setString(10,destinationLocation);
          pStmtWeightBreak.setString(11,shipmentMode);
          pStmtWeightBreak.setString(12,terminalId);
        }
        
        rsWeightBreak  =  pStmtWeightBreak.executeQuery();
        
        
     
        for(int j=0;rsWeightBreak.next();j++)
        {
         if(!weightBreakHdr.contains(rsWeightBreak.getString("WEIGHT_BREAK_SLAB")))
          {
            weightBreakHdr.add(rsWeightBreak.getString("WEIGHT_BREAK_SLAB"));
            rateDescHdr.add("A FREIGHT RATE");
          }
       }
        if(rsWeightBreak!=null)
          rsWeightBreak.close();
        if(pStmtWeightBreak!=null)
          pStmtWeightBreak.close();*/
          pStmtWeightBreak = connection.prepareStatement(weightBreakSurQuery);
      
        pStmtWeightBreak.setString(1,weightBreak.toUpperCase());
        pStmtWeightBreak.setString(2,rateType.toUpperCase());
        pStmtWeightBreak.setString(3,originLocation);
        pStmtWeightBreak.setString(4,destinationLocation);
        pStmtWeightBreak.setString(5,shipmentMode);
        //pStmtWeightBreak.setString(6,serviceLevel);
        
        if("Modify".equalsIgnoreCase(operation))
        {
          pStmtWeightBreak.setString(6,terminalId);
          //pStmtWeightBreak.setString(8,serviceLevel);
          pStmtWeightBreak.setString(7,weightBreak.toUpperCase());
          pStmtWeightBreak.setString(8,rateType.toUpperCase());
          pStmtWeightBreak.setString(9,originLocation);
          pStmtWeightBreak.setString(10,destinationLocation);
          pStmtWeightBreak.setString(11,shipmentMode);
          pStmtWeightBreak.setString(12,terminalId);
        }
        
        rsWeightBreak  =  pStmtWeightBreak.executeQuery();
         for(int j=0;rsWeightBreak.next();j++)
        {
         if(!weightBreakHdr.contains(rsWeightBreak.getString("WEIGHT_BREAK_SLAB")))
          {
            weightBreakHdr.add(rsWeightBreak.getString("WEIGHT_BREAK_SLAB"));
            // rateDescHdr.add(rsWeightBreak.getString("RATE_DESCRIPTION"));
          }
        }
       if(rsWeightBreak!=null)
          rsWeightBreak.close();
        if(pStmtWeightBreak!=null)
          pStmtWeightBreak.close();
        String weightBreakSlab = null;  
        buyRateDetailsArray.add(weightBreakHdr);//@@Adding form header values to arraylist
        //buyRateDetailsArray.add(rateDescHdr);
        for(int i=0;rs.next();i++)
        {
           weightBreakSlab = rs.getString("WEIGHT_BREAK_SLAB");
          currencyKey    = currencyId+"&"+rs.getString("CURRENCY");
          if("Add".equalsIgnoreCase(operation))
              key            = rs.getString("BUYRATEID")+rs.getString("LANE_NO");
          else if("Modify".equalsIgnoreCase(operation))
              key            = rs.getString("BUYRATEID")+rs.getString("REC_CON_ID")+rs.getString("LANE_NO");
              
              
          chargeRate     = rs.getString("CHARGERATE");
          if(currencyMap.containsKey(currencyKey))
          {
                currencyFactor  = Double.parseDouble((String)currencyMap.get(currencyKey));
                //System.out.println("currencyFactorcurrencyFactorcurrencyFactor111111111111 :: "+currencyFactor);
          }
          else
          {
              currencyFactor  = operationsImpl.getConvertionFactor(rs.getString("CURRENCY"),currencyId);
              //System.out.println("currencyFactorcurrencyFactorcurrencyFactor222222222 :: "+currencyFactor);
              currencyMap.put(currencyKey,String.valueOf(currencyFactor));
          }
          
          if(currencyFactor>0)
              convertedRate  = operationsImpl.getConvertedAmt(Double.parseDouble(chargeRate),currencyFactor);
            else
              convertedRate  = Double.parseDouble(chargeRate);
          
          if(i!=0 && hMap.containsKey(key))
          {
            flatRate = ""+convertedRate;
            if("FLAT".equalsIgnoreCase(weightBreakSlab))
            {
              hMap.put("FLATRATE",flatRate);
              hMap.put("FLATLINE_NO",rs.getString("LINE_NO"));
            }
           else
           {
              hMap.put(weightBreakSlab+"FLATRATE",flatRate);
              hMap.put(weightBreakSlab+"FLATLINE_NO",rs.getString("LINE_NO"));
           }
           // hMap.put("FLATLINE_NO",rs.getString("LINE_NO"));
            if("Modify".equals(operation))
            {
              if("FLAT".equalsIgnoreCase(weightBreakSlab))
            {
               hMap.put("FLATBUYRATE",rs.getString("BUYRATE"));
            }
            else
            {
              hMap.put(weightBreakSlab+"FLATBUYRATE",rs.getString("BUYRATE"));
            }
            }
            addToList = false;
          }
          else
          {
            hMap = new HashMap();
            hMap.put(key,"");
            hMap.put("BUYRATEID",rs.getString("BUYRATEID"));
            hMap.put("WEIGHT_BREAK_SLAB",rs.getString("WEIGHT_BREAK_SLAB"));
            hMap.put("MINRATE",""+convertedRate);
            hMap.put("LOWERBOUND",rs.getString("LOWERBOUND"));
            hMap.put("UPPERBOUND",rs.getString("UPPERBOUND"));
            hMap.put("MINLINE_NO",rs.getString("LINE_NO"));
            hMap.put("LANE_NO",rs.getString("LANE_NO"));
            hMap.put("CARRIER_ID",rs.getString("CARRIER_ID"));
            hMap.put("FREQUENCY",rs.getString("FREQUENCY"));
            hMap.put("CONSOLE_TYPE",rs.getString("CONSOLE_TYPE"));
            hMap.put("TRANSIT_TIME",rs.getString("TRANSIT_TIME"));
            hMap.put("BUYTERMINALID",rs.getString("TERMINALID"));
            hMap.put("NOTES",rs.getString("NOTES"));
            hMap.put("SERVICE_LEVEL",rs.getString("SERVICE_LEVEL"));
            hMap.put("VERSION_NO",rs.getString("VERSION_NO"));//@@Added by Kameswari for the WPBN issue-146448 on 03/01/09
            
            if("Modify".equalsIgnoreCase(operation))
            {
              hMap.put("TERMINALID",rs.getString("TERMINALID"));
              hMap.put("ACCESSLEVEL",rs.getString("ACCESSLEVEL"));
              hMap.put("SELLRATEID",rs.getString("REC_CON_ID"));
              hMap.put("PALLET_CAPACITY",rs.getString("PALLET_CAPACITY"));
              hMap.put("PALLET_BYRATE",rs.getString("PALLET_BYRATE"));
              hMap.put("AVEREAGE_UPLIFT",rs.getString("AVEREAGE_UNIT"));
              hMap.put("LOOSE_SPACE",rs.getString("LOOSE_SPACE"));
              hMap.put("MINBUYRATE",rs.getString("BUYRATE"));
            }
            addToList = true;
          }
          if(addToList)
            buyRateDetailsArray.add(hMap);
        }
      }
      else if("Slab".equalsIgnoreCase(weightBreak))
      {
        weightBreakHdr = new ArrayList();
         rateDescHdr = new ArrayList();
        pStmtWeightBreak = connection.prepareStatement(weightBreakSlabQuery);
      
        pStmtWeightBreak.setString(1,weightBreak.toUpperCase());
        pStmtWeightBreak.setString(2,rateType.toUpperCase());
        pStmtWeightBreak.setString(3,originLocation);
        pStmtWeightBreak.setString(4,destinationLocation);
        pStmtWeightBreak.setString(5,shipmentMode);
        //pStmtWeightBreak.setString(6,serviceLevel);
        
        if("Modify".equalsIgnoreCase(operation))
        {
          pStmtWeightBreak.setString(6,terminalId);
          //pStmtWeightBreak.setString(8,serviceLevel);
          pStmtWeightBreak.setString(7,weightBreak.toUpperCase());
          pStmtWeightBreak.setString(8,rateType.toUpperCase());
          pStmtWeightBreak.setString(9,originLocation);
          pStmtWeightBreak.setString(10,destinationLocation);
          pStmtWeightBreak.setString(11,shipmentMode);
          pStmtWeightBreak.setString(12,terminalId);
        }
        
        rsWeightBreak  =  pStmtWeightBreak.executeQuery();
        
        
        weightBreakHdr.add("MIN");
        rateDescHdr.add("A FREIGHT RATE");
        for(int j=0;rsWeightBreak.next();j++)
        {
         if(!weightBreakHdr.contains(rsWeightBreak.getString("WEIGHT_BREAK_SLAB")))
          {
            weightBreakHdr.add(rsWeightBreak.getString("WEIGHT_BREAK_SLAB"));
            rateDescHdr.add("A FREIGHT RATE");
          }
       }
        if(rsWeightBreak!=null)
          rsWeightBreak.close();
        if(pStmtWeightBreak!=null)
          pStmtWeightBreak.close();
          pStmtWeightBreak = connection.prepareStatement(weightBreakSurQuery);
      
        pStmtWeightBreak.setString(1,weightBreak.toUpperCase());
        pStmtWeightBreak.setString(2,rateType.toUpperCase());
        pStmtWeightBreak.setString(3,originLocation);
        pStmtWeightBreak.setString(4,destinationLocation);
        pStmtWeightBreak.setString(5,shipmentMode);
        //pStmtWeightBreak.setString(6,serviceLevel);
        
        if("Modify".equalsIgnoreCase(operation))
        {
          pStmtWeightBreak.setString(6,terminalId);
          //pStmtWeightBreak.setString(8,serviceLevel);
          pStmtWeightBreak.setString(7,weightBreak.toUpperCase());
          pStmtWeightBreak.setString(8,rateType.toUpperCase());
          pStmtWeightBreak.setString(9,originLocation);
          pStmtWeightBreak.setString(10,destinationLocation);
          pStmtWeightBreak.setString(11,shipmentMode);
          pStmtWeightBreak.setString(12,terminalId);
        }
        
        rsWeightBreak  =  pStmtWeightBreak.executeQuery();
         for(int j=0;rsWeightBreak.next();j++)
        {
         if(!weightBreakHdr.contains(rsWeightBreak.getString("WEIGHT_BREAK_SLAB")))
          {
            weightBreakHdr.add(rsWeightBreak.getString("WEIGHT_BREAK_SLAB"));
             rateDescHdr.add(rsWeightBreak.getString("RATE_DESCRIPTION"));
          }
        }
        
        buyRateDetailsArray.add(weightBreakHdr);//@@Adding form header values to arraylist
        buyRateDetailsArray.add(rateDescHdr);
       String weightBreakSlab  = null;
       
        for(int i=0;rs.next();i++)
        {
          currencyKey    = currencyId+"&"+rs.getString("CURRENCY");
          if("Add".equalsIgnoreCase(operation))
              key            = rs.getString("BUYRATEID")+rs.getString("LANE_NO");
          else if("Modify".equalsIgnoreCase(operation))
              key            = rs.getString("BUYRATEID")+rs.getString("REC_CON_ID")+rs.getString("LANE_NO");
              
          chargeRate     = rs.getString("CHARGERATE");
          weightBreakSlab = rs.getString("WEIGHT_BREAK_SLAB");
          
          if(currencyMap.containsKey(currencyKey))
          {
                currencyFactor  = Double.parseDouble((String)currencyMap.get(currencyKey));
                //System.out.println("currencyFactorcurrencyFactorcurrencyFactor111111111111 :: "+currencyFactor);
          }
          else
          {
              currencyFactor  = operationsImpl.getConvertionFactor(rs.getString("CURRENCY"),currencyId);
              //System.out.println("currencyFactorcurrencyFactorcurrencyFactor222222222 :: "+currencyFactor);
              currencyMap.put(currencyKey,String.valueOf(currencyFactor));
          }
          
          if(currencyFactor>0)
              convertedRate  = operationsImpl.getConvertedAmt(Double.parseDouble(chargeRate),currencyFactor);
            else
              convertedRate  = Double.parseDouble(chargeRate);
        
         if(i!=0 && hMap.containsKey(key))
          {
            hMap.put(weightBreakSlab,""+convertedRate);//@@(key is the weight break & value is the charge rate)
            hMap.put(weightBreakSlab+"LOWERBOUND",rs.getString("LOWERBOUND"));
            hMap.put(weightBreakSlab+"UPPERBOUND",rs.getString("UPPERBOUND"));
            hMap.put(weightBreakSlab+"LINE_NO",rs.getString("LINE_NO"));
            hMap.put(weightBreakSlab+"CHARGERATE_INDICATOR",rs.getString("CHARGERATE_INDICATOR"));
            if("Modify".equals(operation))
              hMap.put(weightBreakSlab+"BUYRATE",rs.getString("BUYRATE"));
            addToList = false;
          }
          else
          {
            hMap = new HashMap();//@@Creating a new instance only if either the buy rate id or the lane number changes. 
            hMap.put(key,"");
            hMap.put("BUYRATEID",rs.getString("BUYRATEID"));
            hMap.put(weightBreakSlab,""+convertedRate);
            hMap.put("TRANSIT_TIME",rs.getString("TRANSIT_TIME"));
            hMap.put(weightBreakSlab+"LOWERBOUND",rs.getString("LOWERBOUND"));
            hMap.put(weightBreakSlab+"UPPERBOUND",rs.getString("UPPERBOUND"));
            hMap.put(weightBreakSlab+"LINE_NO",rs.getString("LINE_NO"));
            hMap.put("LANE_NO",rs.getString("LANE_NO"));
            hMap.put(weightBreakSlab+"CHARGERATE_INDICATOR",rs.getString("CHARGERATE_INDICATOR"));
            hMap.put("CARRIER_ID",rs.getString("CARRIER_ID"));
            hMap.put("FREQUENCY",rs.getString("FREQUENCY"));
            hMap.put("CONSOLE_TYPE",rs.getString("CONSOLE_TYPE"));
            hMap.put("NOTES",rs.getString("NOTES"));
            hMap.put("BUYTERMINALID",rs.getString("TERMINALID"));
            hMap.put("SERVICE_LEVEL",rs.getString("SERVICE_LEVEL"));
           hMap.put("VERSION_NO",rs.getString("VERSION_NO"));//@@Added by Kameswari for the WPBN issue-146448 on 03/01/09
          if("Modify".equals(operation))
            {
              hMap.put("TERMINALID",rs.getString("TERMINALID"));
              hMap.put("ACCESSLEVEL",rs.getString("ACCESSLEVEL"));
              hMap.put("SELLRATEID",rs.getString("REC_CON_ID"));
              hMap.put(weightBreakSlab+"BUYRATE",rs.getString("BUYRATE"));
            }
            addToList = true;//@@Only when a new instance is created, the hMap will be added to arraylist.
          }
          if(addToList)
            buyRateDetailsArray.add(hMap);//@@Adding key value pairs to arraylist
        }
        
      }
    }
    catch(Exception e)
    {
      e.printStackTrace();
      throw new EJBException(e)   ;
    }
    finally
    {
      try
      {
          if(rs!=null)
            { rs.close();}
          if(rsWeightBreak!=null)
            { rsWeightBreak.close();}
          if(pStmt!=null)
            { pStmt.close();}
          if(pStmtWeightBreak!=null)
            { pStmtWeightBreak.close();}
          if(connection!=null)
            { connection.close();}
      }
      catch(Exception e)
      {
         //Logger.error(FILE_NAME,"Exception in closing connection loadRateDetails(3 params) method"+e.toString());
         logger.error(FILE_NAME+"Exception in closing connection loadRateDetails(3 params) method"+e.toString());
      }
    }
    return buyRateDetailsArray;
  }

  public ArrayList getMarginValues(QMSSellRatesDOB sellDob,String operation)
  {
      QMSSellRatesDAO             sellRatesDao          =   new QMSSellRatesDAO();
      ArrayList                   valueList             =   null;
      try
      {
          valueList = sellRatesDao.getMarginOfValues(sellDob,operation); 
      }
      catch(SQLException sqle)
      {
          //Logger.error(FILE_NAME,"SQLEXception in getMarginValues()-->"+sqle.toString());
          logger.error(FILE_NAME+"SQLEXception in getMarginValues()-->"+sqle.toString());
          sqle.printStackTrace();
          throw new EJBException(sqle.toString());
      }
      catch(Exception e)
      {
        //Logger.error(FILE_NAME,"EXception in getMarginValues()-->"+e.toString());
        logger.error(FILE_NAME+"EXception in getMarginValues()-->"+e.toString());
        e.printStackTrace();
        throw new EJBException(e.toString());
      }
      return valueList;
  }


  
}