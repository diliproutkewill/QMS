package com.foursoft.etrans.setup.vendorregistration.ejb.sls;
import javax.ejb.EJBHome;
import java.rmi.RemoteException;
import javax.ejb.CreateException;

/**
 * 
 * This class will be useful to .
 * 
 * File		  : VendorRegistrationSessionHome.java
 * @version	: etrans 1.6
 * @author	: Srinivasa Rao Koppurauri 
 * 
 * @date	  : 2002-03-24
 *
 */


public interface VendorRegistrationSessionHome extends EJBHome 
{
  /**
   * 
   * @return VendorRegistrationSession
   * @throws java.rmi.RemoteException
   * @throws javax.ejb.CreateException
   */
	com.foursoft.etrans.setup.vendorregistration.ejb.sls.VendorRegistrationSession create() throws RemoteException, CreateException;
}