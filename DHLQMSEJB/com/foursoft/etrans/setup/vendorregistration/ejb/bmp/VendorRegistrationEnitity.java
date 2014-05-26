package com.foursoft.etrans.setup.vendorregistration.ejb.bmp;
import javax.ejb.EJBObject;
import com.foursoft.etrans.setup.vendorregistration.java.VendorRegistrationJava;
import java.rmi.*;

/**
 * 
 * This class will be useful to .
 * 
 * File		  : VendorRegistrationEnitity.java
 * @author 	: Srinivasa Rao Koppurauri 
 *  
 * @version 	: etrans 1.6
 * @date 	  : 2002-03-24
 * 
 */


public interface VendorRegistrationEnitity extends EJBObject 
{
  /**
   * 
   * @return VendorRegistrationJava
   * @throws java.rmi.RemoteException
   */
  // @@ Modified by G.Srinivas for TogetherArchitect on 20050111 
  VendorRegistrationJava getVendorRegistrationJava() throws RemoteException;
  /**
   * 
   * @param param0
   * @throws java.rmi.RemoteException
   */
   void setVendorRegistrationJava(com.foursoft.etrans.setup.vendorregistration.java.VendorRegistrationJava param0)throws RemoteException;
}