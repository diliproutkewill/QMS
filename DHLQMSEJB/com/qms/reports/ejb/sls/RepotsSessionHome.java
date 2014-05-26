package com.qms.reports.ejb.sls;
import javax.ejb.EJBHome;
import java.rmi.RemoteException;
import javax.ejb.CreateException;

public interface RepotsSessionHome extends EJBHome 
{
  RepotsSession create() throws RemoteException, CreateException;
}