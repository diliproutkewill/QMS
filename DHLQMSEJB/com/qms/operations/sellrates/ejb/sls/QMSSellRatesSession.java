package com.qms.operations.sellrates.ejb.sls;
import com.qms.operations.sellrates.java.QMSSellRatesDOB;
import javax.ejb.EJBObject;

public interface QMSSellRatesSession extends EJBObject 
{
  public StringBuffer validateSellRateFormHdr(QMSSellRatesDOB param0) throws java.rmi.RemoteException;

  public boolean insertSellRateDetails(java.util.ArrayList param0) throws java.rmi.RemoteException;

  public java.util.ArrayList getBuySellRateDetails(QMSSellRatesDOB param0) throws java.rmi.RemoteException,javax.ejb.ObjectNotFoundException;

  public java.util.ArrayList getSellRatesValues(QMSSellRatesDOB param0,com.foursoft.esupply.common.bean.ESupplyGlobalParameters param1,String param2)throws java.rmi.RemoteException;

  //public java.util.ArrayList getCalSellRates(QMSSellRatesDOB param0,String param1)throws java.rmi.RemoteException;

  public String insertSellRates(java.util.ArrayList param0,com.foursoft.esupply.common.bean.ESupplyGlobalParameters param1,String param2)throws java.rmi.RemoteException;

  public StringBuffer validateSellRatesHdrData(QMSSellRatesDOB param0)throws java.rmi.RemoteException;
  public java.util.ArrayList getSellRatesOfValues(QMSSellRatesDOB param0,com.foursoft.esupply.common.bean.ESupplyGlobalParameters param1,String param2)throws java.rmi.RemoteException;
  
  public String updateInvalidate(java.util.ArrayList param0)throws java.rmi.RemoteException;
  public java.util.ArrayList getMarginValues(QMSSellRatesDOB param0,String param1)throws java.rmi.RemoteException;
  
  public java.util.ArrayList getAcceptanceSellRatesValues(QMSSellRatesDOB param0,com.foursoft.esupply.common.bean.ESupplyGlobalParameters param1,String param2)throws java.rmi.RemoteException;
  public String insertAcceptanceSellRates(java.util.ArrayList param0,com.foursoft.esupply.common.bean.ESupplyGlobalParameters param1,String param2)throws java.rmi.RemoteException;
  public java.util.ArrayList getTerminalIds(com.foursoft.esupply.common.bean.ESupplyGlobalParameters param1,String param2)throws java.rmi.RemoteException;

}