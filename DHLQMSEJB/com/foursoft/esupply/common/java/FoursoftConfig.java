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
import com.foursoft.esupply.common.bean.ESupplyGlobalParameters;

/**
 * File			: FoursoftConfig.java
 * sub-module 	: Common
 * module 		: esupply
 * 
 * This is used to configure the runtime characteritics of the product / module
 * 
 * @author	Madhusudhan Rao. P, 
 * @date	2002-04-25
 * Modification History
 * Amit		23.09.2002	Other configuration parameters like concurrent login and
 * 						Shipment Mode number are moved to this file for
 *                      centralized reference.
 * Amit		26.09.2002	Flag put for enabling/disabling mouse events is GUI rendering JSP
 * Amit		09.10.2002	Selector configuration for Hot-Key modifier for the Right Panel Menus
 * Amit		11.10.2002	Selector configuration for the Cascaded Style Sheet (CSS) to be used for the module ported
 * Amit		16.10.2002	The expiry date for he product is now to be set in this class in MM/DD/YYYY format and not
 * 						in the Rawdata.java file in the Web module.
 * Amit		21/10/2002	The global datasource name 'jdbc/DB' or otherwise is now
 *						maintained globally in this file.
 *                      Also the global mail source name 'mail/MS' or otherwise is now
 *						maintained globally in this file.
 *                      The sendMail addresses (from / to) are maintained here.
 *                      The product build purpose can now be selected.
 *                      The Integrator's name, EAR name and integration date can also be
 *                      specified here.
 *                      The name of the Client can also be speicfied here. hese will be used
 *                      in the send Malil function when the application is started at the client's
 *                      setup
 * Amit		12/11/2002	Defined Module names as constants so they can be used throught out the application
 * 						and even if the label changes, the code is not affected.
 * Amit		19/11/2002	The Concurrent login flag is phased out (removed). A User will NOT be able to login with
 * 						concurrently with the same set of credentials. This preference is now the default.
 *                      The entry of shipment modes and menu hotkey modifier has been made stricter.
 *                      Failing to configure tis file correctly will result in the failure of the
 *                      application build as many access control parameters rely heavily on the setting in this
 *                      file
 * Amit		25/04/2003	The Licensee name was changed to "Four Soft Limited"
 */

public interface FoursoftConfig
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
	public static final String[] PRODUCTS = {	FoursoftConfig.ETRANS,	// 0
												};		// 3


	/**This number is used to select the ported product (module) from the PRODUCTS array
	 * instead of typing / commenting each time
	 */
	public static final int PORTED_MODULE_INDEX = 0;

	/**	This is an indirect reference to the internal ID of the ported Product / Module
	 *	That is being ported / packaged for deployment 
	 */
	public static final String MODULE = PRODUCTS[ PORTED_MODULE_INDEX ];

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

	/**	Licensee Id
	 */
	public static final String LICENSEE_ID = "FOURSOFT";
	

	/**	Name of the Licensee
	 */
	public static final String LICENSEE_NAME = "Four Soft Limiteds";


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

	/**This number is used to select the CSS corresponding to the look-and-feel preferred by the
	 * Customer / Licensee
	 */
	public static final int CSS_INDEX = 0;

	/**	These will enable to select the CSS needed for the Look-And-Feel for the
	  * ported application
	  */
	public static final String	CSSID	= CSSIDS[ CSS_INDEX ];


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

    /**
     * Here are the static integers mapped to shipment modes including console type
     * 
     * Air    --------  1
     * Sea(LCL)-------  2
     * Sea(FCL)-------  4
     * Truck(LTL)-----  7
     * Truck(FTL)-----  15
     *
     * These values can be used in the application for the corresponding combinations
     * also,combination of modes can use...
     */
     
     public static final int AIR = 1;
     public static final int SEA_LCL = 2;
     public static final int SEA_FCL = 4;
     public static final int TRUCK_LTL  = 7;
     public static final int TRUCK_FTL  =  15;
     
     //public static final String[] shipmentmode_console  ={0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15};    

}