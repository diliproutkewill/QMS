package com.qms.setup.ejb.bmp;
import com.qms.setup.java.ZoneCodeMasterDOB;
import java.rmi.RemoteException;
import javax.ejb.EJBException;
import javax.ejb.EJBObject;

public interface ZoneCodeMaster extends EJBObject 
{
   public void setZoneCodeMasterDOB(ZoneCodeMasterDOB dob) throws RemoteException;
   
   public ZoneCodeMasterDOB getZoneCodeMasterDOB() throws RemoteException;
}