package com.foursoft.esupply.accesscontrol.ejb.bmp;

/* **********************************************
* Generated by Pramati Technologies, EJBWizard 
* RemoteInterface Class of the com.foursoft.esupply.accesscontrol.ejb.bmp.RoleMasterBean *
* Sun Feb 16 12:46:21 IST 2003
* ***********************************************/

import javax.ejb.*;
import com.foursoft.esupply.accesscontrol.java.RoleModel;
import com.foursoft.esupply.accesscontrol.exception.DataIntegrityViolationException;

public interface RoleMasterLocal extends EJBLocalObject
{
	public RoleModel getRoleModel();
	public void setRoleModel(RoleModel param0)throws DataIntegrityViolationException;
}