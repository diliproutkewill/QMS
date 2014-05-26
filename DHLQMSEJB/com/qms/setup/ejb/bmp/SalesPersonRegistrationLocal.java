/*
	Program Name	:SalesPersonRegistrationLocal.java
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

import javax.ejb.EJBLocalObject;

public interface SalesPersonRegistrationLocal extends EJBLocalObject 
{
    public void setSalesPersonRegistrationDOB(SalesPersonRegistrationDOB dob);
    
    public SalesPersonRegistrationDOB getSalesPersonRegistrationDOB();
}