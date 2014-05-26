package com.foursoft.etrans.setup.vendorregistration.ejb.bmp;
import javax.ejb.EJBHome;
import java.rmi.RemoteException;
import javax.ejb.CreateException;
import javax.ejb.FinderException;

/**
 * 
 * This class will be useful to .
 * 
 * File		  : VendorRegistrationEnitityHome.java
 * @version	: etrans 1.6
 * @author	: Srinivasa Rao Koppurauri 
 * 
 * @date	  : 2002-03-24
 *
 */
public interface VendorRegistrationEnitityHome extends EJBHome 
{
  /**
   * 
   * @param param0
   * @return VendorRegistrationEnitity
   * @throws java.rmi.RemoteException
   * @throws javax.ejb.CreateException
   */
  // @@ Modified by G.Srinivas for TogetherArchitect on 20050111 
 com.foursoft.etrans.setup.vendorregistration.ejb.bmp.VendorRegistrationEnitity create(com.foursoft.etrans.setup.vendorregistration.java.VendorRegistrationJava param0) throws RemoteException, CreateException;
  /**
   * 
   * @param primaryKey
   * @return VendorRegistrationEnitity
   * @throws java.rmi.RemoteException
   * @throws javax.ejb.FinderException
   */
   com.foursoft.etrans.setup.vendorregistration.ejb.bmp.VendorRegistrationEnitity findByPrimaryKey(com.foursoft.etrans.setup.vendorregistration.ejb.bmp.VendorRegistrationEnitityPK primaryKey) throws RemoteException, FinderException;
}