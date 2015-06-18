package com.foursoft.etrans.setup.currency.ejb.sls;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import javax.ejb.EJBException;
import javax.ejb.EJBObject;

public interface CurrencySession extends EJBObject 
{
  public void insertCurrencyMasterDetails(ArrayList currConvDOBs) throws	EJBException,RemoteException;
  public HashMap getUrlDetails()throws EJBException,RemoteException;/*@@Modified by Anusha for DHL-4S-CR in Kewill Time & Expense*/
  public boolean checkCurrencyUpdation()throws EJBException,RemoteException;
}