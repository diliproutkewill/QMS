package com.foursoft.esupply.accesscontrol.ejb.sls;

/* **********************************************
* Generated by Pramati Technologies, EJBWizard 
* HomeInterface Class of the com.foursoft.esupply.accesscontrol.ejb.sls.UserRoleRegistrationSessionBean *
* Thu Mar 06 11:13:56 IST 2003
* ***********************************************/

import javax.ejb.EJBLocalHome;
import javax.ejb.CreateException;

public interface UserRoleRegistrationSessionLocalHome extends EJBLocalHome
{
	public UserRoleRegistrationSessionLocal create() throws CreateException;
}
