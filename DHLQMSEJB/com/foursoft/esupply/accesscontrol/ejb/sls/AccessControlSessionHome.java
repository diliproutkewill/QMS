package com.foursoft.esupply.accesscontrol.ejb.sls;

/* **********************************************
* Generated by Pramati Technologies, EJBWizard 
* HomeInterface Class of the com.foursoft.esupply.accesscontrol.ejb.sls.AccessControlSessionBean *
* Thu Mar 06 19:00:15 IST 2003
* ***********************************************/

import java.rmi.RemoteException;
import javax.ejb.EJBHome;
import javax.ejb.CreateException;

public interface AccessControlSessionHome extends EJBHome
{
	public com.foursoft.esupply.accesscontrol.ejb.sls.AccessControlSession create ()
		throws CreateException, RemoteException;
}