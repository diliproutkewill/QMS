package com.qms.setup.ejb.bmp;
import com.qms.setup.java.ListMasterDOB;
import java.rmi.RemoteException;
import javax.ejb.EJBLocalObject;

public interface ListMasterEntityBeanLocal extends EJBLocalObject 
{
  public void setListMasterDOB(ListMasterDOB listmasterDOB);
  public ListMasterDOB getListeMasterDOB() ;
}