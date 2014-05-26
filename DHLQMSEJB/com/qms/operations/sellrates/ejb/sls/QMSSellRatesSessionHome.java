package com.qms.operations.sellrates.ejb.sls;
import javax.ejb.EJBHome;
import java.rmi.RemoteException;
import javax.ejb.CreateException;

public interface QMSSellRatesSessionHome extends EJBHome 
{
  QMSSellRatesSession create() throws RemoteException, CreateException;
}