package com.foursoft.esupply.accesscontrol.ejb.bmp;

/* **********************************************
* Generated by Pramati Technologies, EJBWizard 
* RemoteInterface Class of the com.foursoft.esupply.accesscontrol.ejb.bmp.RoleMasterBean *
* Sun Feb 16 12:46:21 IST 2003
* ***********************************************/

import java.rmi.RemoteException;
import javax.ejb.*;
import com.foursoft.esupply.accesscontrol.exception.DataIntegrityViolationException;

public interface RoleMaster extends javax.ejb.EJBObject
{


	public  com.foursoft.esupply.accesscontrol.java.RoleModel getRoleModel()
		throws java.rmi.RemoteException;
	public  void setRoleModel(com.foursoft.esupply.accesscontrol.java.RoleModel param0)
		throws DataIntegrityViolationException, java.rmi.RemoteException;
}
