package com.foursoft.esupply.common.ejb.sls;

/* **********************************************
* Generated by Pramati Technologies, EJBWizard 
* HomeInterface Class of the com.foursoft.esupply.common.ejb.sls.CountryManagerBean *
* Mon Oct 21 16:04:18 IST 2002
* ***********************************************/

import javax.ejb.CreateException;
import javax.ejb.EJBLocalHome;

public interface CountryManagerLocalHome extends EJBLocalHome
{
	public CountryManagerLocal create() throws CreateException;
}
