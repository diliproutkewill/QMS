/**
 * 
 * Copyright (c) 2000-2001 by FourSoft, Inc. All Rights Reserved.
 * This software is the proprietary information of FourSoft, Pvt Ltd.
 * Use is subject to license terms.
 *
 * esupply - v 1.x 
 *
 */
package com.foursoft.esupply.accesscontrol.java;

import com.foursoft.esupply.common.util.ArraySupport;
/**
 * File			: UserAccessConfig
 * sub-module	: AccessControl
 * module		: esupply
 * 
 * This is an Class is used to configure the user access levels, like 
 * what are the accesses allowed for a guy who is in a perticular level
 * 
 * If you want ship out access control this the file you need to change
 *
 * @author Madhu. P, 
 * @date   28-08-2001
 */
public final class ETransUserAccessConfig extends UserAccessConfig
{
	// what are different modules it is supporting	
// for ETRans and Accounts 	

	public static final String[] 	MODULES 		= {"ADMIN", "ETRANS"};
	public static final int[] 		MODULE_INDEXES 	= {1,2};
	
	public final String	getETrans() {
		return MODULES[1];
	}
	
	public static final String	ETRANS = MODULES[1];

// allowed modules for ETrans access level
	//public static final String[] ESUPPLY_MODULES = {"ADMIN", "ETRANS", "EACCOUNTS"};
	//public static final String[] ETRANS_MODULES = {"ADMIN", "ETRANS", "EACCOUNTS"};
	//public static final String[] EACCOUNTS_MODULES = {"ADMIN", "EACCOUNTS"};
	//public static final String[] ADMIN_MODULES = {"ADMIN"};
	//public static final String[] ETCRM_MODULES = {"ETRANS"};	

	// what are the different type of access level the system will provide
	public static final String[] ACCESS_TYPES = {"LICENSEE", "HO_TERMINAL", "ADMN_TERMINAL", "OPER_TERMINAL", "ETVRM", "ETCRM"};

	// what are the allowed AccessLevels for each Access Level
	public static final String[] LICENSEE_ACCESS_TYPES 		= {"LICENSEE", "HO_TERMINAL", "ADMN_TERMINAL"};
	public static final String[] HO_ACCESS_TYPES 			= {"HO_TERMINAL", "ADMN_TERMINAL", "OPER_TERMINAL", "ETVRM", "ETCRM"};
	public static final String[] ADMIN_ACCESS_TYPES 		= {"ADMN_TERMINAL", "OPER_TERMINAL", "ETVRM", "ETCRM"};
	public static final String[] OPER_ACCESS_TYPES 			= {"OPER_TERMINAL", "ETCRM"};
	public static final String[] AGENT_ACCESS_TYPES 		= {"AGENT_TERMINAL"};	
	public static final String[] GATEWAY_ACCESS_TYPES 		= {"GATEWAY"};
	public static final String[] ETCRM_ACCESS_TYPES 		= {"ETCRM"};

	/*
	* this used to retrieve the allowed access types for allowed for a specified access level
	* @ param - accessType
	*/
	public String[] getAccessTypes( String accessType )
	{
		String[] allowedAccessTypes = null;
		if( accessType.equalsIgnoreCase( "LICENSEE" ) )
		{
			allowedAccessTypes = LICENSEE_ACCESS_TYPES;
		}
		else if( accessType.equalsIgnoreCase( "HO_TERMINAL" ) )
		{
			allowedAccessTypes = HO_ACCESS_TYPES;
		}
		else if( accessType.equalsIgnoreCase( "ADMN_TERMINAL" ) )
		{
			allowedAccessTypes = ADMIN_ACCESS_TYPES;
		}
		else if( accessType.equalsIgnoreCase( "OPER_TERMINAL" ) )
		{
			allowedAccessTypes = OPER_ACCESS_TYPES;
		}
		else if( accessType.equalsIgnoreCase( "AGENT_TERMINAL" ) )
		{
			allowedAccessTypes = AGENT_ACCESS_TYPES;
		}
		else if( accessType.equalsIgnoreCase( "GATEWAY" ) )
		{
			allowedAccessTypes = GATEWAY_ACCESS_TYPES;
		}		
		else if( accessType.equalsIgnoreCase( "ETCRM" ) )
		{
			allowedAccessTypes = ETCRM_ACCESS_TYPES;
		}
		return allowedAccessTypes;
	}
	
	public String[] getModules()
	{
		return MODULES;
	}
	public int[] getModuleIndexes()
	{
		return MODULE_INDEXES;
	}
	
}
