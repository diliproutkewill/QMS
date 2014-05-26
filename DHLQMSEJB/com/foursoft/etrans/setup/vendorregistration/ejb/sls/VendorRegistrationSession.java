package com.foursoft.etrans.setup.vendorregistration.ejb.sls;
import com.foursoft.etrans.setup.codecust.exception.CodeCustNotDoneException;

import javax.ejb.EJBObject;
import javax.ejb.ObjectNotFoundException;

/**
 * 
 * This class will be useful to .
 * 
 * File		  : VendorRegistrationSession.java
 * @version	: etrans 1.6
 * @author	: Srinivasa Rao Koppurauri 
 * 
 * @date	  : 2002-03-24
 *
 */

public interface VendorRegistrationSession extends EJBObject 
{
  /**
   * 
   * @param param0
   * @param param1
   * @return String
   * @throws java.rmi.RemoteException
   * @throws com.foursoft.etrans.setup.codecust.exception.CodeCustNotDoneException
   */
   // @@ Modified by G.Srinivas for TogetherArchitect on 20050111 

	 String insertVendorDetails(com.foursoft.etrans.setup.vendorregistration.java.VendorRegistrationJava param0,com.foursoft.esupply.common.bean.ESupplyGlobalParameters param1)throws java.rmi.RemoteException,CodeCustNotDoneException;

  /**
   * 
   * @param param0
   * @param param1
   * @param param2
   * @return VendorRegistrationJava
   * @throws javax.ejb.ObjectNotFoundException
   * @throws java.rmi.RemoteException
   */
//@@ Modified By Ravi Kumar on 26-04-2005
	 com.foursoft.etrans.setup.vendorregistration.java.VendorRegistrationJava isValidIdGetData(String param0,String param1,java.lang.String param2,java.lang.String param3)throws ObjectNotFoundException,java.rmi.RemoteException;
//@@ 	26-04-2005
  /**
   * 
   * @param param0
   * @return String
   * @throws java.rmi.RemoteException
   */
	 String updateVendorDetails(com.foursoft.etrans.setup.vendorregistration.java.VendorRegistrationJava param0)throws java.rmi.RemoteException;

  /**
   * 
   * @param param0
   * @return String
   * @throws java.rmi.RemoteException
   */
	 String	deleteVendorDetails(String param0)throws java.rmi.RemoteException;
	
  /**
   * 
   * @param param0
   * @param param1
   * @return StringBuffer
   * @throws java.rmi.RemoteException
   */
	 StringBuffer getValidityFields(String param0,String param1)throws java.rmi.RemoteException;
}