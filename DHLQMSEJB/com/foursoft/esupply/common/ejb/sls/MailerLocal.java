package com.foursoft.esupply.common.ejb.sls;

/* **********************************************
* Generated by Pramati Technologies, EJBWizard 
* RemoteInterface Class of the com.foursoft.esupply.common.ejb.sls.MailerBean *
* Mon Oct 21 16:05:05 IST 2002
* ***********************************************/

import javax.ejb.EJBLocalObject;
import com.foursoft.esupply.common.java.EMailMessage;

public interface MailerLocal extends EJBLocalObject
{
	public void sendMail(EMailMessage param0) ;
}
