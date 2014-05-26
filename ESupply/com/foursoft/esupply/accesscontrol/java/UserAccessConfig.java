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
import com.foursoft.esupply.common.java.FoursoftConfig;
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
 * Date				Modified By			Reason
 * 04-10-2002		Amit Parekh			The hypens in the module name labels have been removed
 */
public abstract class UserAccessConfig
{

	public static final int ADMIN		= 1;
	public static final int ETRANS		= 2;
	//public static final int ELOG		= 4;
	//public static final int ACCOUNTS	= 8;


	/*
	* this used to retrieve the allowed access types for allowed for a specified access level
	* @ param - accessType
	*/
	public abstract String[] getAccessTypes( String accessType );

	public abstract String[] getModules();

	public abstract int[] getModuleIndexes();

	public abstract String	getETrans();

	public static final synchronized String getModuleLabel(String module) {
		
		String	moduleLabel = "";
		
		if(module.equals("ADMIN")) {
			moduleLabel = "Administration";
		} else if(module.equals("QMS")) {
			moduleLabel = "QMS";
		} else {
			moduleLabel = module;
		}
		return moduleLabel;
	}

}
