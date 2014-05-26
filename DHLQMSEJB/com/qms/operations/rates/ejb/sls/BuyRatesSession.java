package com.qms.operations.rates.ejb.sls;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;

import javax.ejb.EJBObject;

import com.foursoft.esupply.common.bean.ESupplyGlobalParameters;
import com.qms.operations.rates.dob.RateDOB;
import com.qms.operations.sellrates.java.QMSSellRatesDOB;

public interface BuyRatesSession extends EJBObject
{
 public ArrayList crateBuyFlatRate(RateDOB rateDOB,ArrayList list)
  throws javax.ejb.EJBException,java.rmi.RemoteException;
 public String validateRateDOB(String carrier,String currency,String param3,String param4)
  throws javax.ejb.EJBException,java.rmi.RemoteException;
  public String validateDetailRateDOB(ArrayList lanes,RateDOB rateDOB)
  throws javax.ejb.EJBException,java.rmi.RemoteException;

  public HashMap getFlatRatesVales(QMSSellRatesDOB sellRatesDob,ESupplyGlobalParameters loginBean,String operation)
     throws javax.ejb.EJBException,java.rmi.RemoteException;

  public ArrayList getListRatesVales(QMSSellRatesDOB sellRatesDob,ESupplyGlobalParameters loginBean,String operation)
	throws javax.ejb.EJBException,java.rmi.RemoteException;

   public ArrayList getSlabRatesVales(QMSSellRatesDOB sellRatesDob,ESupplyGlobalParameters loginBean,String operation)	
	throws javax.ejb.EJBException,java.rmi.RemoteException;
    public StringBuffer validateSellRatesHdrData(QMSSellRatesDOB sellDob)
			throws javax.ejb.EJBException,java.rmi.RemoteException;
	public ArrayList getSellRatesValues(QMSSellRatesDOB sellRatesDob,ESupplyGlobalParameters loginBean,String operation)
		throws javax.ejb.EJBException,java.rmi.RemoteException;

	public void invalidateBuyRateDtls(ArrayList list)
 		throws javax.ejb.EJBException,java.rmi.RemoteException;

	public void modifyFlatRates(ArrayList list,ESupplyGlobalParameters loginBean)
 		throws javax.ejb.EJBException,java.rmi.RemoteException;
    
//     public HashMap upLoadBuyRates(ArrayList rateList ,boolean addModFlag,ESupplyGlobalParameters loginBean )throws EJBException,RemoteException ;   
  
  	public RateDOB upLoadBuyRates(RateDOB param0,ESupplyGlobalParameters param1)
    	throws javax.ejb.EJBException,java.rmi.RemoteException;

	public boolean validateCurrency(String currency) 		throws javax.ejb.EJBException,java.rmi.RemoteException;
	public String  validateWBreaks (ArrayList wtbreakList,String shipmentMode) 
  throws javax.ejb.EJBException,java.rmi.RemoteException;

  
  //@@Added for rates upload performance issue on 03/03/09
  public String processExcel(String fileName,ESupplyGlobalParameters loginBean)  throws javax.ejb.EJBException,java.rmi.RemoteException;

   public ArrayList getErrorMsg(ESupplyGlobalParameters loginbean) throws javax.ejb.EJBException,java.rmi.RemoteException;
  //@@Added by Kameswari for the WPBN issue-171210
 public String processExcelDelete(String fileName)  throws javax.ejb.EJBException,java.rmi.RemoteException;
  public ArrayList getDeleteErrorMsg() throws javax.ejb.EJBException,java.rmi.RemoteException;
  //Added by Mohan for issue no.219973 on 02122010 
  public HashMap getFlatSurchargeList(String shipmentMode) throws javax.ejb.EJBException,java.rmi.RemoteException;
  public TreeSet getListSurchargeList(String shipmentMode) throws javax.ejb.EJBException,java.rmi.RemoteException;
//@@WPBN issue-171210
} 