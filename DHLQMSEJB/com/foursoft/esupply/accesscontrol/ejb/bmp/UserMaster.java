package com.foursoft.esupply.accesscontrol.ejb.bmp;

/* **********************************************
* Generated by Pramati Technologies, EJBWizard 
* RemoteInterface Class of the com.foursoft.esupply.accesscontrol.ejb.bmp.UserMasterBean *
* Sun Feb 16 12:46:59 IST 2003
* ***********************************************/

import java.rmi.RemoteException;
import javax.ejb.*;
import com.foursoft.esupply.accesscontrol.exception.DataIntegrityViolationException;

public interface UserMaster extends javax.ejb.EJBObject
{
	public  com.foursoft.esupply.accesscontrol.java.UserModel getUserModel()
		throws java.rmi.RemoteException;
	public  void setUserModel(com.foursoft.esupply.accesscontrol.java.UserModel param0)
		throws DataIntegrityViolationException, java.rmi.RemoteException;
}
