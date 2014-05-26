package com.foursoft.etrans.setup.currency.ejb.sls;
import javax.ejb.EJBHome;
import java.rmi.RemoteException;
import javax.ejb.CreateException;

public interface CurrencySessionHome extends EJBHome 
{
  CurrencySession create() throws RemoteException, CreateException;
}