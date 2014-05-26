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
/**
 * File			: SPUserAccessConfig
 * sub-module	: AccessControl
 * module		: esupply
 * 
 * This is an Class is used to configure the user access levels, like 
 * what are the accesses allowed for a guy who is in a perticular level
 * 
 * If you want ship out access control this the file you need to change
 *
 * @author Madhu. V, 
 * @date   22-01-2003
 */
 
public class SPUserAccessConfig extends UserAccessConfig 
{
  public static final String[] 	MODULES 		= {"ADMIN", "ETRANS", "ELOG","EACCOUNTS"};
	public static final int[] 		MODULE_INDEXES 	= {1,2,4,8};

/////////////////////////////////////ELOG
	public static final String[] ACCESS_TYPES = {"LICENSEE", "HO", "HO_TERMINAL", "COMPANY", "WAREHOUSE", "PROJECTLOG", "CUSTWH", "ESCRM", "ESVRM", "ELCRM", "ELVRM"};

	public static final String[] LICENSEE_ACCESS_TYPES 		= {"LICENSEE","HO", "HO_TERMINAL", "ADMN_TERMINAL"};
	public static final String[] HO_ACCESS_TYPES 			= {"HO_TERMINAL", "ADMN_TERMINAL", "OPER_TERMINAL", "AGENT_TERMINAL", "GATEWAY", "ESCRM","HO", "COMPANY", "WAREHOUSE", "PROJECTLOG", "CUSTWH"};
	public static final String[] ADMIN_ACCESS_TYPES 		= {"ADMN_TERMINAL", "OPER_TERMINAL", "AGENT_TERMINAL", "ESCRM","ETCRM","WAREHOUSE", "CUSTWH"};
	public static final String[] OPER_ACCESS_TYPES 			= {"OPER_TERMINAL", "ESCRM", "ETCRM"};
	public static final String[] AGENT_ACCESS_TYPES 		= {"AGENT_TERMINAL"};	
	public static final String[] GATEWAY_ACCESS_TYPES 		= {"GATEWAY"};
	public static final String[] ETCRM_ACCESS_TYPES 		= {"ETCRM"};
	public static final String[] COMPANY_ACCESS_TYPES 		= {"COMPANY", "WAREHOUSE", "PROJECTLOG", "CUSTWH"};
	public static final String[] PROJECTLOG_ACCESS_TYPES 	= {"PROJECTLOG", "CUSTWH"};
	public static final String[] WAREHOUSE_ACCESS_TYPES 	= {"WAREHOUSE", "CUSTWH"};
	public static final String[] CUSTWH_ACCESS_TYPES 		= {"CUSTWH", "ESCRM", "ESVRM", "ELCRM", "ELVRM"};
	public static final String[] ESCRM_ACCESS_TYPES 		= {"ESCRM"};
	public static final String[] ESVRM_ACCESS_TYPES 		= {"ESVRM"};


	public String[] getModules()
	{
		return MODULES;
	}
	public int[] getModuleIndexes()
	{
		return MODULE_INDEXES;
	}

  public final String	getETrans() 
  {	// This method is just for compilation
		return MODULES[1];
	}

	public String[] getAccessTypes( String accessType )
	{
		String[] allowedAccessTypes = null;
		if( accessType.equalsIgnoreCase( "LICENSEE" ) )		
		{
			allowedAccessTypes = LICENSEE_ACCESS_TYPES;		
		}
		else if( accessType.equalsIgnoreCase( "HO_TERMINAL" ) || accessType.equalsIgnoreCase( "HO" ) )		
		{
			allowedAccessTypes = HO_ACCESS_TYPES;		
		}
		else if( accessType.equalsIgnoreCase( "ADMN_TERMINAL" ) )		
		{
			allowedAccessTypes = ADMIN_ACCESS_TYPES;		
		}
		else if( accessType.equalsIgnoreCase( "OPER_TERMINAL" ) )		
		{
			allowedAccessTypes = OPER_ACCESS_TYPES;		}
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
		else if( accessType.equalsIgnoreCase( "ESVRM" ) )
		{
			allowedAccessTypes = ESVRM_ACCESS_TYPES;
		}
		else if( accessType.equalsIgnoreCase( "ESCRM" ) )
		{
			allowedAccessTypes = ESCRM_ACCESS_TYPES;
		}
		return allowedAccessTypes;
	}  
  
}