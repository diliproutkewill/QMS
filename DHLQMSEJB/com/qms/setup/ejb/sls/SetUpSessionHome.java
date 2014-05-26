package com.qms.setup.ejb.sls;
import javax.ejb.EJBHome;
import java.rmi.RemoteException;
import javax.ejb.CreateException;

public interface SetUpSessionHome extends EJBHome 
{
  com.qms.setup.ejb.sls.SetUpSession create() throws RemoteException, CreateException;
}