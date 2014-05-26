/*
	Program Name	:SalesPersonRegistrationHome.java
	Module Name		:QMSSetup
	Task			    :SalesPersonRegistration EntityBean
	Sub Task		  :
	Author Name		:RamaKrishna Y
	Date Started	:June 17,2001
	Date Completed:
	Date Modified	:
	Description		:
*/

package com.qms.setup.ejb.bmp;

import com.qms.setup.java.SalesPersonRegistrationDOB;

import javax.ejb.EJBHome;

import java.rmi.RemoteException;

import javax.ejb.CreateException;

import javax.ejb.FinderException;

public interface SalesPersonRegistrationHome extends EJBHome 
{
  SalesPersonRegistration create() throws RemoteException, CreateException;
  
  SalesPersonRegistration create(SalesPersonRegistrationDOB   dob) throws RemoteException, CreateException;

  SalesPersonRegistration findByPrimaryKey(SalesPersonRegistrationPK primaryKey) throws RemoteException, FinderException;
}