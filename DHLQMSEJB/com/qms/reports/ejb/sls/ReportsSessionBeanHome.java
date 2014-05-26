package com.qms.reports.ejb.sls;
import javax.ejb.EJBHome;
import java.rmi.RemoteException;
import javax.ejb.CreateException;

public interface ReportsSessionBeanHome extends EJBHome 
{
  ReportsSession create() throws RemoteException, CreateException;
}