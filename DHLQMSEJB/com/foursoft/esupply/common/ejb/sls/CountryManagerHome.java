package com.foursoft.esupply.common.ejb.sls;

/* **********************************************
* Generated by Pramati Technologies, EJBWizard 
* HomeInterface Class of the com.foursoft.esupply.common.ejb.sls.CountryManagerBean *
* Mon Oct 21 16:04:18 IST 2002
* ***********************************************/

import java.rmi.RemoteException;
import javax.ejb.CreateException;

public interface CountryManagerHome extends javax.ejb.EJBHome
{
	public CountryManager create() throws CreateException, RemoteException;
}
