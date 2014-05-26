package com.foursoft.esupply.accesscontrol.util;

/**
 * @author  madhu
 * 
 * Modification History
 * Modified By		Modified On			Remarks
 * Amit Parekh		11-12-2002			Added the HO access type details to the class
 */
public class UserAccessUtility 
{
	public static String getRoleModule(int moduleIndex)
	{
		String module = null;
		switch(moduleIndex)
		{
			case 1 :
			{
				module = "ADMIN";
				break;
			}
			case 2 :
			{
				module = "ETRANS";
				break;
			}
			case 4 :
			{
				module = "ELOG";
				break;				
			}
			case 8 :
			{
				module = "EACCOUNTS";
				break;				
			}
		}
		return module;
	}

	public static String convertIntoModuleString(int moduleIndex)
	{
		String module = null;
		switch(moduleIndex)
		{
			case 1 :
			{
				module = "AD";
				break;
			}
			case 2 :
			{
				module = "QS";
				break;
			}
			case 3 :
			{
				module = "AD-QS";
				break;
			}
			case 4 :
			{
				module = "EL";
				break;				
			}
			case 5 :
			{
				module = "AD-QS";
				break;
			}
			case 6 :
			{
				module = "QS-EL";
				break;
			}
			case 7 :
			{
				module = "AD-QS-EL";
				break;
			}
			case 8 :
			{
				module = "EA";
				break;				
			}
			case 9 :
			{
				module = "AD-EA";
				break;
			}
			case 10 :
			{
				module = "QS-EA";
				break;
			}
			case 11 :
			{
				module = "AD-QS-EA";
				break;
			}
			case 12 :
			{
				module = "EL-EA";
				break;				
			}
			case 13 :
			{
				module = "AD-EL-EA";
				break;
			}
			case 14 :
			{
				module = "QS-EL-EA";
				break;
			}
			case 15 :
			{
				module = "AD-QS-EL-EA";
				break;
			}
		}
		return module;
	}
	
	/*
	* this used to retrieve the Access Type Lable
	* @ param - accessType
	*/
	public static final String getLocationLable( String accessType )
	{
		String 	locationLable = null;
		if( accessType.equalsIgnoreCase( "LICENSEE" ) )
		{
			locationLable = "Licensee Id";
		}
		else if( accessType.equalsIgnoreCase( "HO" ) )
		{
			locationLable = "Head Office Id";
		}
		else if( accessType.equalsIgnoreCase( "COMPANY" ) )
		{
			locationLable = "Company Id";
		}
		else if( accessType.equalsIgnoreCase( "PROJECTLOG" ) )
		{
			locationLable = "Project Id";
		}
		else if( accessType.equalsIgnoreCase( "WAREHOUSE" ) )
		{
			locationLable = "Warehouse Id";
		}
		else if( accessType.equalsIgnoreCase( "CUSTWH" ) )
		{
			locationLable = "Warehouse Customer Id";
		}		
		else if( accessType.equalsIgnoreCase( "HO_TERMINAL" ) )
		{
			locationLable = "Corporate Terminal Id";
		}
		else if( accessType.equalsIgnoreCase( "ADMN_TERMINAL" ) )
		{
			locationLable = "Admin Terminal Id";
		}
		else if( accessType.equalsIgnoreCase( "OPER_TERMINAL" ) )
		{
			locationLable = "Operation Terminal Id";
		}
		else if( accessType.equalsIgnoreCase( "AGENT_TERMINAL" ) )
		{
			locationLable = "Agent Terminal Id";
		}
		else if( accessType.equalsIgnoreCase( "GATEWAY" ) )
		{
			locationLable = "Gateway Id";
		}
		else if( accessType.equalsIgnoreCase("ETCRM") || accessType.equalsIgnoreCase("EPCRM") || accessType.equalsIgnoreCase("ELCRM") || accessType.equalsIgnoreCase("ESCRM"))
		{
			locationLable = "Customer Id";
		}	
		else if( accessType.equalsIgnoreCase("ELVRM") || accessType.equalsIgnoreCase("EPVRM") || accessType.equalsIgnoreCase("ESVRM"))
		{
			locationLable = "Vendor Id";
		}
		else if( accessType.equalsIgnoreCase("EPTRM")){
			locationLable = "Transporter Id";
		}
		else if( accessType.equalsIgnoreCase("ELTRM"))
		{
			locationLable = "Transporter Id";
		}
		return locationLable;
	}
	/*
	* this used to retrieve the Access Type Lable
	* @ param - accessType
	*/
	public static final String getAccessTypeLable( String accessType )
	{
		String 	locationLable = null;
		if( accessType.equalsIgnoreCase( "LICENSEE" ) )
		{
			locationLable = "LICENSEE";
		}
		else if( accessType.equalsIgnoreCase( "HO" ) )
		{
			locationLable = "HEAD-OFFICE";
		}
		else if( accessType.equalsIgnoreCase( "COMPANY" ) )
		{
			locationLable = "COMPANY";
		}
		else if( accessType.equalsIgnoreCase( "PROJECTLOG" ) )
		{
			locationLable = "ELOG-PROJECT ";
		}
		else if( accessType.equalsIgnoreCase( "WAREHOUSE" ) )
		{
			locationLable = "WAREHOUSE";
		}
		else if( accessType.equalsIgnoreCase( "CUSTWH" ) )
		{
			locationLable = "WH-CUSTOMER";
		}		
		else if( accessType.equalsIgnoreCase( "HO_TERMINAL" ) )
		{
			locationLable = "HO TERMINAL";
		}
		else if( accessType.equalsIgnoreCase( "ADMN_TERMINAL" ) )
		{
			locationLable = "ADMIN TERMINAL";
		}
		else if( accessType.equalsIgnoreCase( "OPER_TERMINAL" ) )
		{
			locationLable = "OPERATIONS TERMINAL";
		}
		else if( accessType.equalsIgnoreCase( "AGENT_TERMINAL" ) )
		{
			locationLable = "AGENT ACCESS";
		}
		else if( accessType.equalsIgnoreCase( "ETVRM" ) )
		{
			locationLable = "SUPPLIER";
		}
		else if( accessType.equalsIgnoreCase( "ETCRM" ) )
		{
			locationLable = "CUSTOMER";
		}
		else if( accessType.equalsIgnoreCase( "ELCRM" ) )
		{
			locationLable = "ELOG-CUSTOMER";
		}	
		else if( accessType.equalsIgnoreCase( "ELVRM" ) )
		{
			locationLable = "ELOG-VENDOR";
		}
		else if( accessType.equalsIgnoreCase( "ESCRM" ) )
		{
			locationLable = "SP-CUSTOMER";
		}	
		else if( accessType.equalsIgnoreCase( "ESVRM" ) )
		{
			locationLable = "SP-VENDOR";
		}
		else if( accessType.equalsIgnoreCase( "EPCRM" ) )
		{
			locationLable = "EP-CUSTOMER";
		}	
		else if( accessType.equalsIgnoreCase( "EPVRM" ) )
		{
			locationLable = "EP-VENDOR";
		}
		else if( accessType.equalsIgnoreCase( "EPTRM" ) )
		{
			locationLable = "EP-TRANSPORTER";
		}
		else if( accessType.equalsIgnoreCase("ELTRM") )
		{
            locationLable = "ELOG-TRANSPORTER";
		}
		return locationLable;
	}
	
}
