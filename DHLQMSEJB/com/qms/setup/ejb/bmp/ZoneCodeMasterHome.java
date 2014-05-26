package com.qms.setup.ejb.bmp;
import javax.ejb.EJBHome;
import java.rmi.RemoteException;
import javax.ejb.CreateException;
import javax.ejb.FinderException;
import com.qms.setup.java.ZoneCodeMasterDOB;
public interface ZoneCodeMasterHome extends EJBHome 
{
  ZoneCodeMaster create() throws RemoteException, CreateException;
  
  ZoneCodeMaster create(ZoneCodeMasterDOB dob) throws CreateException,RemoteException;
  
  ZoneCodeMaster findByPrimaryKey(ZoneCodeMasterPK primaryKey) throws RemoteException, FinderException;
}