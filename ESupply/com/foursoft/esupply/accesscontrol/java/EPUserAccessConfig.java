/**
 * 
 * Copyright (c) 2000-2001 by FourSoft, Inc. All Rights Reserved.
 * This software is the proprietary information of FourSoft Pvt. Ltd.
 * Use is subject to license terms.
 *
 * esupply - v 1.x 
 *
 */
package com.foursoft.esupply.accesscontrol.java;

/**
 * File			: EPUserAccessConfig
 * sub-module	: AccessControl
 * module		: esupply
 * 
 * This is an Class is used to configure the user access levels, like 
 * what are the accesses allowed for a person who is in a particular level
 * 
 * If you want ship out access control this the file you need to change according to the module being ported
 *
 * @author Amit Parekh, 
 * @date   04-10-2002
 */

public class EPUserAccessConfig extends UserAccessConfig  {

	// what are different modules it is supporting	
	// for ELog and Accounts 	

	public static final String[] 	MODULES 		= {"ADMIN", "ELOG", "EACCOUNTS"};
	public static final int[] 		MODULE_INDEXES 	= {1,4,8};
	
	// what are the different type of access level the system will provide
	public static final String[] ACCESS_TYPES = {"LICENSEE", "COMPANY", "WAREHOUSE", "EPCRM", "EPVRM", "EPTRM"};

	// what are the allowed AccessLevels for each Access Level
	public static final String[] LICENSEE_ACCESS_TYPES 		= {"LICENSEE", "COMPANY"};
	public static final String[] COMPANY_ACCESS_TYPES 		= {"COMPANY", "WAREHOUSE", "EPCRM", "EPVRM", "EPTRM"};
	public static final String[] WAREHOUSE_ACCESS_TYPES 	= {"WAREHOUSE", "EPCRM", "EPVRM"};
	public static final String[] EPCRM_ACCESS_TYPES 		= {"EPCRM"};
	public static final String[] EPVRM_ACCESS_TYPES 		= {"EPVRM"};
	public static final String[] EPTRM_ACCESS_TYPES 		= {"EPTRM"};


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
		else if( accessType.equalsIgnoreCase( "COMPANY" ) )
		{
			allowedAccessTypes = COMPANY_ACCESS_TYPES;
		}
		else if( accessType.equalsIgnoreCase( "WAREHOUSE" ) )
		{
			allowedAccessTypes = WAREHOUSE_ACCESS_TYPES;
		}
		else if( accessType.equalsIgnoreCase( "EPCRM" ) )
		{
			allowedAccessTypes = EPCRM_ACCESS_TYPES;
		}
		else if( accessType.equalsIgnoreCase( "EPVRM" ) )
		{
			allowedAccessTypes = EPVRM_ACCESS_TYPES;
		}
		else if( accessType.equalsIgnoreCase( "EPTRM" ) )
		{
			allowedAccessTypes = EPTRM_ACCESS_TYPES;
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
	
	public final String	getETrans() {	// This method is just for compilation
		return "";
	}

}