/*
	Program Name	:SalesPersonRegistrationLocalHome.java
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

import javax.ejb.EJBLocalHome;

import javax.ejb.CreateException;

import javax.ejb.FinderException;

public interface SalesPersonRegistrationLocalHome extends EJBLocalHome 
{
  SalesPersonRegistrationLocal create() throws CreateException;
  
  SalesPersonRegistrationLocal create(SalesPersonRegistrationDOB dob) throws CreateException;

  SalesPersonRegistrationLocal findByPrimaryKey(SalesPersonRegistrationPK primaryKey) throws FinderException;
}