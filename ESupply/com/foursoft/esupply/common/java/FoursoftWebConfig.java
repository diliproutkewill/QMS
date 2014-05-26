/**
 * 
 * Copyright (c) 2000-2001 by FourSoft, Inc. All Rights Reserved.
 * This software is the proprietary information of FourSoft, Pvt Ltd.
 * Use is subject to license terms.
 *
 * esupply - v 1.x 
 *
 */

package com.foursoft.esupply.common.java;

/**
 * File			: FoursoftWebConfig.java
 * sub-module 	: Common
 * module 		: esupply
 * 
 * This is used to configure the runtime characteritics of the product / module at Web Side
 * 
 * @author	Madhusudhan Rao. V, 
 * @date	2003-10-20
 */

public interface FoursoftWebConfig 
{
	/**
	 * NOTES: For the Configurator who modifies this file
	 * 
	 * As the configurator of this file an individual is supposed to configure only the variables
	 * listed below. Wherever these variables are declared, please read the respective comments
	 * and set the values of these variables accordingly.
	 * 
	 * PORTED_MODULE_INDEX		(Application build specific)
	 * MAX_USERS_LIMIT			(Application build specific)
	 * 
	 * SHIPMENT_MODE_INDEX		(Applicable to products ETRANS and SP only. Values should be between 0 and 7)
	 *  
	 * EAR_NAME					(Application build specific)
	 * INT_NAME					(Application build specific)
	 * INT_DATE					(Application build specific)
	 * EXP_DATE					(Application build specific)
	 * PURPOSE_INDEX			(Application build specific)
	 *
	 * CLIENT_NAME					(Client specific)
	 * CSS_INDEX					(Client specific)
	 * 	MENU_HOTKEY_MODIFIERS_INDEX	(Client specific)
	 * 
	 * IMPORTANT: After configuration please review, your changes at least 2 times as a small
	 * mistake in configuring this file may prove costly in terms of time and quality
	 * of the product ported. It is recommended that he Configrator/Integrator dicuss the configuration
	 * and walk-through the file configuration with the Team Leader (and/or Project Manager and/or Any Authorised Personnel)
	 * responsible for porting the build.
	 * 
	 */

	/**These are the Pre-defined identifiers for the Modules/Products
	 * application
	 */
	public static final String ETRANS	=	"ETRANS";
	//public static final String ELOG		=	"ELOG";
	//public static final String EP		=	"EP";
	//public static final String SP		=	"SP";
	

	/**These are the possible Modules / Products that can be ported as an
	 * application
	 */
	public static final String[] PRODUCTS = {	FoursoftWebConfig.ETRANS};		// 3


	/**This number is used to select the ported product (module) from the PRODUCTS array
	 * instead of typing / commenting each time
	 */
	public static final int PORTED_MODULE_INDEX = 0;

	/**	This is an indirect reference to the internal ID of the ported Product / Module
	 *	That is being ported / packaged for deployment 
	 */
	public static final String MODULE = PRODUCTS[ PORTED_MODULE_INDEX ];

	/** Two possible values to this variable are "PRODUCTION" and "NON-PRODUCTION".
	 *	This varibale will be refered in ESupplyMessagePage to disply the component details
	 *	where the error was raised.
	 */
	public static final String	APPLICATION_MODE =	"PRODUCTION";   


	/**Enables to configure the maximum number of concurrent Users that the application
	 * is licensed for
	 */
	public static final int MAX_USERS_LIMIT = 2000;

	/**Enables to configure the date of expiry of the User's license
	 * To make life difficult for a cracker, we have put two values for the date
	 * One will be in the actual date format MM/DD/YYYY
	 * If no date is specified please put and empty String "" instead of null
	 */

	public static final String	EXP_DATE = "";	//"10/15/2002";	// MM/DD/YYYY Format

	/**	BUILD Info like Name of EAR, the date on which this build was made and by whom
	 */

	public static final String	EAR_NAME = "BUILT_EAR_APPLICATIION_NAME"+".ear";	//"10/15/2002";	// MM/DD/YYYY Format
	
	public static final String	INT_DATE = "06/10/2005";	//"10/15/2002";	// MM/DD/YYYY Format

	public static final String	INT_NAME = "DHL Admin";	//"10/15/2002";	// MM/DD/YYYY Format	

	/**	Name of the Licensee
	 */
	public static final String LICENSEE_NAME = "Four Soft Limiteds";

	/**	Client's Name e.g. S-Net Freight Holdings Pte. ltd.
	 */
	public static final String CLIENT_NAME = "";


	/**	These are the Tree's Top Label configured for the different Modules / Products
	 *	It is automatically selected when a product is select by its index
	 */
	public static final String[] TREE_TOP_LABELS	= {	"DHL QuoteShop"	};


	/**	This will allow selection of the Tree's Top Label for the ported Product / Module
	 *	It is automatically select when a product is select by its index
	 */
	public static final String	TREE_TOP_LABEL		=	TREE_TOP_LABELS[ PORTED_MODULE_INDEX ];


	/**	These are a few purpose templates available as per the purpose with which
	 * the current build of the application is given
	 */
	 
	public static final String[] MODULE_PURPOSES	= {	"Demo",			// 0
														"Trial",		// 1
														"Pilot Run",	// 2
														"Licensed Use"	// 3
													  };

	/**	Selected the purpose for which the current build of the application is given for
	 *	by specifying an index into the above array
	 */

	public static final int	PURPOSE_INDEX	=	0;

	public static final String MODULE_PURPOSE	=	MODULE_PURPOSES[ PURPOSE_INDEX ];

	
	/**	Selected the purpose for which the current build of the application is given for
	 *	in the MESSAGE_TEMPLATES by specifying an index and need not be  edited every time. 
	 */
													  
	public static final String[] MESSAGE_TEMPLATES	= {	"This product is a Demo version of : ",		// 0
														"This product is a Trial version of : ",	// 1
														"This product is a Pilot version of : ",	// 2
														"This product is a Production version of : "// 3
													  };

	/**	Message to be shown in the top frame of the browser. Its selected automatoccaly from those available
	 *	when the purpose is  selected.
	 */
	public static final String MESSAGE_TEMPLATE = MESSAGE_TEMPLATES[ PURPOSE_INDEX ];


	/**	For different Combination of Modes (Air, Sea, Truck) We will maintain different Intergers 
	  * as follows:
	  *
	  * Value	Mode Combinations
	  * -----	----------------- 
	  * 1		AIR
	  * 2		SEA
	  * 4		TRUCK
	  * 3		AIR-SEA
	  * 5		AIR-TRUCK
	  * 6		SEA-TRUCK
	  * 7		AIR-SEA-TRUCK
	  * 
	  * At the time of packaging the application for deployment, the MODE final variable will 
	  * be set to the Shipment Mode modules provided under Etrans and using this, the application
	  * can introspect this info and find out which modules are ported in it.
	  * 
	  */

	/**
	 * All the possible shipment modes (as integers) are stored in this array of integers
	 */
	public static final int[]	SHIPMENT_MODES	=	{0,1,2,3,4,5,6,7};
	//										Index =  0,1,2,3,4,5,6,7

	/**
	 * Enter a value for SHIPMENT_MODE_INDEX to select a SHIPMENT MODE from the ones
	 * defined in the above array by specifying the index at which the shipment mode occurs.
	 * 
	 * HINT: Give the same number as the shipment mode you want to select
	 * 
	 * WARNING: Giving a number other than the ones between 0 and 7 will not allow the application to
	 * run. The Login process will give errors because this class will simply not be loaded
	 * by the JVM's class loader as the static variable initializer will throw an
	 * ArrayIndexOutOfBoundsexception at login time.
	 */
	public static final int SHIPMENT_MODE_INDEX = 7;
	
	public static final int SHIPMENT_MODE = SHIPMENT_MODES[ SHIPMENT_MODE_INDEX ];


	/**	This is to enable/disable mouse events in GUI rendering JSPs
	  *
	  */
	public static final boolean	HANDLE_MOUSE_EVENTS = true;


	/**	These are the available Hot-Key modifiers to be used for the Right Frame Menus
	  */
	public static final String[] MENU_HOTKEY_MODIFIERS	= {	"SHIFT",	// 0
															"ALT",		// 1
															""			// 2
															};

	/**	Select a MENU_HOTKEY_MODIFIER by specifying a value for the index at which
	 *	the modifier occurs in the ones available in the array above.
	 */
	 
	public static final int MENU_HOTKEY_MODIFIERS_INDEX	=	0;

	/**	These will enable to select the Hot-Key modifier to be used for the Right Frame Menus
	  * from those available
	  */
	public static final String	MENU_HOTKEY_MODIFIER = MENU_HOTKEY_MODIFIERS[ MENU_HOTKEY_MODIFIERS_INDEX ];


	/**	These are the available CSS numbers as in the Foursoft_css.jsp
	  */
	public static final String[] CSSIDS	= {	"0",	// 0
											"1",	// 1
											"2",	// 2
											"3",	// 3
											"4",	// 4
											"5"		// 5
										};


	/**	These are the Strings to be used while looking up the data source (database)
	  * or the mail resource from the application's EJB or WEB module. Using this
	  * these strings need not be hard coded and can be maintained centrally.
	  * This enables us to change the datasource lookup reference name for each
	  * deploymwent.
	  * 
	  * CAUTION :-
	  * This should match with the actual resource reference name as in the
	  * ejb-jar.xml, web.xml and application.xml in the EJB-JAR, WAR and EAR
	  * respectively and as set by the deployer. The deployer should be aware
	  * of the values set in this file.
	  * 
	  */
	public static final String	DATA_SOURCE		=	"java:comp/env/oraclePool";

	public static final String	MAIL_SOURCE 	=	"java:comp/env/mail/MS";


	/**	These are the email addresses to be used by the sendMail functonality in the
	  * index servlet / JSP to send a mail to foursoft admin when the application is
	  * first accessed on a production deployment at customer or when the application
	  * is started after deployment.
	  */
	public static final String	FROM_EMAIL_ADDR =	"webmaster@four-soft.com";

	public static final String	TO_EMAIL_ADDR 	=	"webmaster@four-soft.com";

	/**This defines whether optional roles will be available to Users of the application
	 */
//	public static final boolean[] PROCESS_OPTIONAL_ROLES = {false, true, true, false};
	// Changed for SP
	public static final boolean[] PROCESS_OPTIONAL_ROLES = {false, true, true, true};

	/**	This is an indirect reference to the internal ID of the ported Product / Module
	 *	That is being ported / packaged for deployment 
	 */
	public static final boolean PROCESS_OPTIONAL_ROLE = PROCESS_OPTIONAL_ROLES[ PORTED_MODULE_INDEX ];


	public int DEFAULT_DECIMAL_PRECISION		= 2;

	public int LENGTH_DECIMAL_PRECISION			= 2;
	public int VOLUME_DECIMAL_PRECISION			= 2;
	public int WEIGHT_DECIMAL_PRECISION			= 2;

	public int INTERNAL_CALCULATION_PRECISION	= 10;


	/**
     *  These are the parameters used for displaying no of records per page and
     *  number of segments in the LOV Page.
     */ 

    public static final int NOOFRECORDSPERPAGE = 10;
    public static final int NOOFSEGMENTS       = 10;
}