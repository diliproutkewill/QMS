/**
 * 
 * Copyright (c) 2000-2001 by FourSoft, Inc. All Rights Reserved.
 * This software is the proprietary information of FourSoft. Pvt Ltd.
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
 * 
 * Modification History
 * Modified By		Modified On			Remarks
 * Amit Parekh		11-12-2002			Added the HO access type to the class
 */
public final class ELogUserAccessConfig extends UserAccessConfig
{

	// what are different modules it is supporting	
	// for ELog and Accounts 	

	public static final String[] 	MODULES 		= {"ADMIN", "ELOG", "EACCOUNTS"};
	public static final int[] 		MODULE_INDEXES 	= {1,4,8};

	// what are the different type of access level the system will provide
	public static final String[] ACCESS_TYPES = {"LICENSEE", "HO", "COMPANY", "WAREHOUSE", "PROJECTLOG", "CUSTWH", "ELCRM", "ELVRM", "ELTRM"};

	// what are the allowed AccessLevels for each Access Level
	public static final String[] LICENSEE_ACCESS_TYPES 		= {"LICENSEE", "HO" };
	public static final String[] HO_ACCESS_TYPES 			= {"HO", "COMPANY", "WAREHOUSE", "PROJECTLOG", "CUSTWH"};
	public static final String[] COMPANY_ACCESS_TYPES 		= {"COMPANY", "WAREHOUSE", "PROJECTLOG", "CUSTWH"};
	public static final String[] PROJECTLOG_ACCESS_TYPES 	= {"PROJECTLOG", "CUSTWH"};
	public static final String[] WAREHOUSE_ACCESS_TYPES 	= {"WAREHOUSE", "CUSTWH","ELTRM"};
	public static final String[] CUSTWH_ACCESS_TYPES 		= {"CUSTWH", "ELCRM", "ELVRM"};
	public static final String[] ELCRM_ACCESS_TYPES 		= {"ELCRM"};
	public static final String[] ELVRM_ACCESS_TYPES 		= {"ELVRM"};
	public static final String[] ELTRM_ACCESS_TYPES 		= {"ELTRM"};

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
		else if( accessType.equalsIgnoreCase( "HO" ) || accessType.equalsIgnoreCase( "HO_TERMINAL" ))
		{
			allowedAccessTypes = HO_ACCESS_TYPES;
		}
		else if( accessType.equalsIgnoreCase( "COMPANY" ) )
		{
			allowedAccessTypes = COMPANY_ACCESS_TYPES;
		}
		else if( accessType.equalsIgnoreCase( "WAREHOUSE" ) )
		{
			allowedAccessTypes = WAREHOUSE_ACCESS_TYPES;
		}
		else if( accessType.equalsIgnoreCase( "PROJECTLOG" ) )
		{
			allowedAccessTypes = PROJECTLOG_ACCESS_TYPES;
		}
		else if( accessType.equalsIgnoreCase( "CUSTWH" ) )
		{
			allowedAccessTypes = CUSTWH_ACCESS_TYPES;
		}
		else if( accessType.equalsIgnoreCase( "ELVRM" ) )
		{
			allowedAccessTypes = ELVRM_ACCESS_TYPES;
		}
		else if( accessType.equalsIgnoreCase( "ELCRM" ) )
		{
			allowedAccessTypes = ELCRM_ACCESS_TYPES;
		}
		else if( accessType.equalsIgnoreCase( "ELTRM" ) )
		{
			allowedAccessTypes = ELTRM_ACCESS_TYPES;
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
