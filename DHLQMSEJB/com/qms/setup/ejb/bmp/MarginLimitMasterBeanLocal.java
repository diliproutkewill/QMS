package com.qms.setup.ejb.bmp;
import com.qms.setup.java.MarginLimitMasterDOB;
import java.rmi.RemoteException;
import javax.ejb.EJBLocalObject;

public interface MarginLimitMasterBeanLocal extends EJBLocalObject 
{
  /**
   * 
   * @throws java.rmi.RemoteException
   * @param param0
   */
   public void setMarginLimitDOB(MarginLimitMasterDOB param0);
  /**
   * 
   * @throws java.rmi.RemoteException
   * @return 
   */
   public MarginLimitMasterDOB getMarginLimitDOB();
}