package com.foursoft.esupply.common.ejb.sls;

/* **********************************************
* Generated by Pramati Technologies, EJBWizard 
* HomeInterface Class of the com.foursoft.esupply.common.ejb.sls.MailerBean *
* Mon Oct 21 16:05:05 IST 2002
* ***********************************************/

import java.rmi.RemoteException;
import javax.ejb.*;

public interface MailerHome extends javax.ejb.EJBHome
{
	public com.foursoft.esupply.common.ejb.sls.Mailer create ()
		throws javax.ejb.CreateException, java.rmi.RemoteException;
}
