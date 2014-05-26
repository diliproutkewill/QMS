
/**
 * 
 * Copyright (c) 2000-2001 by FourSoft, Inc. All Rights Reserved.
 * This software is the proprietary information of FourSoft, Pvt Ltd.
 * Use is subject to license terms.
 *
 * esupply - v 1.x sdfg
 *
 */
package com.foursoft.esupply.accesscontrol.bean;

import java.util.Hashtable;
import java.util.ArrayList;
import java.util.Iterator;
import com.foursoft.esupply.accesscontrol.ejb.sls.UserRoleRegistrationSession;
import com.foursoft.esupply.accesscontrol.ejb.sls.UserRoleRegistrationSessionHome;
import com.foursoft.esupply.common.util.Logger;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import com.foursoft.esupply.accesscontrol.java.TxDetailVOB;

/**
 * File			: RoleMasterDOB
 * sub-module	: AccessControl
 * module		: esupply
 * 
 * @author	Madhusudhan Rao. P
 * @date		17-08-01
 */
 
public class RolePermissionControllerJBean implements java.io.Serializable
{
	private Hashtable permissionsTable;
	
	public ArrayList getRolePermissionsList()
	{
		ArrayList permissionsList = new ArrayList();
		
		TxDetailVOB txDetailVOB = null;
		String txId = "";
		Iterator moduleItr = permissionsTable.keySet().iterator();
//		Logger.info("RolePermissionControllerJBean","Permissions Table : "+	permissionsTable);	
		
		Hashtable tempTable = new Hashtable();
		
		while(moduleItr.hasNext() )
		{
			String	strShipmentMode	=	"";
			int		shipmentMode	=	-1;
			String	moduleName		=	"";

			String	moduleNameKey	=	(String) moduleItr.next();
			
			if(moduleNameKey.indexOf("@") > -1) {
				// The the mdoule is ETRANS
				moduleName		= moduleNameKey.substring( 0, moduleNameKey.indexOf("@") );
				strShipmentMode	= moduleNameKey.substring( moduleNameKey.indexOf("@") );
				
				tempTable	= (Hashtable) permissionsTable.get( moduleNameKey );
				
			} else {
				moduleName	= moduleNameKey;
				tempTable	= (Hashtable) permissionsTable.get( moduleName );
			}

				
			
			Iterator tempModItr = tempTable.keySet().iterator();
			
			while( tempModItr.hasNext() )
			{
				txId = (String)tempModItr.next();
				
				txDetailVOB = new TxDetailVOB( txId, ( (Integer)tempTable.get( txId ) ).intValue() );
				
				permissionsList.add( txDetailVOB );
			}
		}
		return permissionsList;
	}
	public RolePermissionControllerJBean() 
	{
		permissionsTable = new Hashtable();
	}
	public void addPermissions( String module, Hashtable permissions )
	{
		permissionsTable.put( module, permissions );
	}
	
	public void removePermissions( String module, Hashtable permissions )
	{
		permissionsTable.remove( module );
	}
	public Hashtable getAllPermissions()
	{
		return permissionsTable;
	}
}
