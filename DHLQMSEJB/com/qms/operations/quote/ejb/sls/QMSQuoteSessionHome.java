package com.qms.operations.quote.ejb.sls;
import javax.ejb.EJBHome;
import java.rmi.RemoteException;
import javax.ejb.CreateException;

public interface QMSQuoteSessionHome extends EJBHome 
{
  QMSQuoteSession create() throws RemoteException, CreateException;
}