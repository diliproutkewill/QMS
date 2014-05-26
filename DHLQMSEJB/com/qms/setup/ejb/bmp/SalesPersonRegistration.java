/*
	Program Name	:SalesPersonRegistration.java
	Module Name		:QMSSetup
	Task			    :SalesPersonRegistration EntityBean
	Sub Task		  :
	Author Name		:RamaKrishna Y
	Date Started	:June 28,2001
	Date Completed:
	Date Modified	:
	Description		:
*/

package com.qms.setup.ejb.bmp;

import com.qms.setup.java.SalesPersonRegistrationDOB;

import java.rmi.RemoteException;

import javax.ejb.EJBObject;

public interface SalesPersonRegistration extends EJBObject 
{
  public void setSalesPersonRegistrationDOB(SalesPersonRegistrationDOB dob) throws RemoteException;
  
  public SalesPersonRegistrationDOB getSalesPersonRegistrationDOB()  throws RemoteException;
  
}