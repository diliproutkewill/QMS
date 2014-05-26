package com.foursoft.esupply.accesscontrol.java;

import com.foursoft.esupply.common.java.FoursoftConfig;
import com.foursoft.esupply.accesscontrol.java.UserAccessConfig;

/**
 * @author  madhu
 */
public class UserAccessConfigFactory 
{
	public static UserAccessConfig getUserAccessConfig()
	{
		UserAccessConfig	userAccessConfig	=	null;
		
		String module = FoursoftConfig.MODULE;
		//System.out.println("Module : "+module);
		
		if(module.equalsIgnoreCase("ETRANS") ) {
			userAccessConfig = new ETransUserAccessConfig();
		}
		/*if(module.equalsIgnoreCase("ELOG") ) {
			userAccessConfig = new ELogUserAccessConfig();
		}
		if(module.equalsIgnoreCase("EP") ) {
			userAccessConfig = new EPUserAccessConfig();
		}
		if(module.equalsIgnoreCase("SP") ) {
			userAccessConfig = new SPUserAccessConfig();
		}*/
		return userAccessConfig;
	}
	
}
