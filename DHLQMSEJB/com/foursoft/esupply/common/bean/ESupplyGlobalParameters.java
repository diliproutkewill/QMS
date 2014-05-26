/**
*
* Copyright (c) 2000-2001 by FourSoft, Inc. All Rights Reserved.
* This software is the proprietary information of FourSoft, Pvt Ltd.
* Use is subject to license terms.
*
* esupply - v 1.x
*
*/
package com.foursoft.esupply.common.bean;

import com.foursoft.esupply.common.bean.UserPreferences;
//import com.foursoft.esupply.common.util.Logger;
import com.foursoft.eaccounts.common.java.AccountsCredentials;
import com.foursoft.elog.common.java.ELogCredentials;
import org.apache.log4j.Logger;


import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.HashMap;
import java.util.Date;
import java.text.DateFormat;
import java.util.Locale;
import com.foursoft.esupply.common.java.LookUpBean;
import javax.naming.*;
import javax.ejb.*;
import com.foursoft.esupply.common.java.FoursoftConfig;
import com.foursoft.esupply.common.exception.FoursoftException;

/**
* File			: ESupplyGlobalParameters.java
* sub-module	: common
* module		: esupply
*
* This is used to hold the user credentials
* which also have the elog and eaccounts specific credentials
*
* @author	Madhu. P,
* @date	28-08-2001
* Modification History
* Amit	23.09.2002	The Module Indicator and Shipment mode are referring the FoursoftConfig class
* 					This is for easy configuration of production releases as its in only one place
*                   along with other information related to the ported product.
* Amit	10.10.2002	The Component was made compliant with all modules like Etrans, eLog, eSupply(EP), etc.
* 					Common methods needed by eLog and eSupply(EP) have been added along with
*                   necessary members. Accounts Compatibility is already present.
* Amit	11.10.2002	The CSS settings now refer to FoursoftConfig.java file for settings.
* 					Also a check for maximum concurrent users has been put here in case
*                   concurrent logging is disabled.
*/
public class ESupplyGlobalParameters implements java.io.Serializable
{
	private static Logger logger = null;

	public ESupplyGlobalParameters()
	{
		loginTime	= new Timestamp(new java.util.Date().getTime() );	
    logger  = Logger.getLogger(ESupplyGlobalParameters.class);
	}

	public ESupplyGlobalParameters(String userId,String locationId,String userType,String licenseeId)
	{
		loginTime	= new Timestamp(new java.util.Date().getTime() );
		
		this.userId = userId;
		this.locationId = locationId;
		this.userType = userType;
		this.licenseeId = licenseeId;
	}

	public String getUserId()				{	return this.userId;				}
  public String getEmpId()        { return this.empId;        }
	public String getLocationId()			{	return this.locationId;			}
	public String getRoleId()				{	return this.roleId;				}
	public String getRoleLocationId()		{	return this.roleLocationId;		}
	public String getAccessType()			{	return this.accessType;			}
	public String getUserLevel()			{	return this.userLevel;			}
	public String getUserType()				{	return this.userType;			}

	public String getLicenseeId()			{	return this.licenseeId;			}
	public String getCompanyId()			{	return this.companyId;			}

	public String getTerminalId()			{	return this.terminalId;			}	
	public String getUserTerminalType()		{	return this.userTerminalType;	}	
	public int    getShipmentMode()			{   return this.shipmentMode;		}

	public String getProjectId()			{	return this.projectId;			}
	public String getWareHouseId()			{	return this.wareHouseId;		}
	public String getCustWHId()				{	return this.custWHId;			}

	public String getCustomerId()			{	return this.customerId;			}

	public Timestamp getLoginTime()			{ 	return loginTime;				}
	public String getCurrencyId()			{	return this.currencyId;			}
	public Locale getLocale()				{	return this.locale;				}
	public String getCSSId()				{	return this.CSSId;				}
	public String getHOId()					{	return this.hoId;				}
	

	// added for accounts Integration
	public String getModuleIndicator()		{	return this.moduleIndicator;	}

	public AccountsCredentials	getAccountsCredentials() {	return this.accountsCredentials;	}
	public ELogCredentials		getELogCredentials() {		return this.eLogCredentials;		}
	public UserPreferences		getUserPreferences() {		return this.userPreferences;		}
	
	public EJBHome getEjbHome(String jndiNameOfEjb) {
		
		EJBHome ejbHome = null;
		try {
			ejbHome = LookUpBean.getEJBHome( jndiNameOfEjb );
		} catch(NamingException e) {
			System.out.println("ERROR while looking up EJBHome from loginbean:\n"+e);
		}
		
		return ejbHome;
	}


	
	
	public void setUserId( String userId )					{	this.userId = userId; this.ESupplyLoginId = userId;				}
  public void setEmpId( String empId )			      {	this.empId = empId;		}
	public void setLocationId( String locationId )			{	this.locationId = locationId;		}
	public void setRoleId( String roleId )					{	this.roleId = roleId;				}
	public void setRoleLocationId( String roleLocationId )	{	this.roleLocationId = roleLocationId;	}
	public void setAccessType( String accessType )			{	this.accessType = accessType;		}
	public void setUserType( String userType )				{	this.userType = userType;			}
	public void setUserLevel( String userLevel )			{	this.userLevel = userLevel;			}

	public void setLicenseeId( String licenseeId )			{	this.licenseeId = licenseeId;		}
	public void setCompanyId( String companyId )			{	this.companyId = companyId;			}

	public void setTerminalId( String terminalId )			{	this.terminalId = terminalId;		}
	public void setUserTerminalType(String userTerminalType){	this.userTerminalType = userTerminalType;	}	

	public void setProjectId( String projectId )			{	this.projectId = projectId;			}
	public void setWareHouseId( String wareHouseId )		{	this.wareHouseId = wareHouseId;		}
	public void setCustWHId( String custWHId )				{	this.custWHId = custWHId;			}

	
	public void setCustomerId( String customerId )			{	this.customerId = customerId;		}
	public void setCurrencyId( String currencyId )			{	this.currencyId = currencyId;		}
	public void setLocale( Locale locale )					{	this.locale = locale;				}
	public void setCSSId( String CSSId )					{	this.CSSId = CSSId;					}
	public void setHOId(String hoId)						{	this.hoId = hoId;					}

	// added for accounts Integration
	public void setModuleIndicator(String moduleIndicator )	{	this.moduleIndicator = moduleIndicator;		}

	public void setAccountsCredentials(AccountsCredentials accountsCredentials) {
		this.accountsCredentials	= accountsCredentials;
	}
	
	public void setELogCredentials(ELogCredentials eLogCredentials) {
		this.eLogCredentials	= eLogCredentials;
	}
	
	public void setUserPreferences(UserPreferences userPreferences) {
		this.userPreferences	=	userPreferences;
	}
	
	public void setESupplyGlobalParameters(ESupplyGlobalParameters eSupplyGlobalParameters)
	{
		this.userId 			= eSupplyGlobalParameters.getUserId();
		this.locationId			= eSupplyGlobalParameters.getLocationId();
		this.roleId 			= eSupplyGlobalParameters.getRoleId();
		this.roleLocationId		= eSupplyGlobalParameters.getRoleLocationId();
		this.userLevel			= eSupplyGlobalParameters.getUserLevel();
		this.accessType			= eSupplyGlobalParameters.getAccessType();
		this.userType			= eSupplyGlobalParameters.getUserType();
		this.licenseeId			= eSupplyGlobalParameters.getLicenseeId();
		this.companyId			= eSupplyGlobalParameters.getCompanyId();
		this.terminalId			= eSupplyGlobalParameters.getTerminalId();
		this.userTerminalType	= eSupplyGlobalParameters.getUserTerminalType();		
		this.shipmentMode		= eSupplyGlobalParameters.getShipmentMode();
		this.projectId			= eSupplyGlobalParameters.getProjectId();
		this.wareHouseId		= eSupplyGlobalParameters.getWareHouseId();
		this.custWHId			= eSupplyGlobalParameters.getCustWHId();
		this.customerId			= eSupplyGlobalParameters.getCustomerId();
		this.currencyId			= eSupplyGlobalParameters.getCurrencyId();
		this.moduleIndicator	= eSupplyGlobalParameters.getModuleIndicator();
		this.timeZone			= eSupplyGlobalParameters.getTimeZone();
		this.relativeOffset		= eSupplyGlobalParameters.getRelativeOffset();
		this.hoId				= eSupplyGlobalParameters.getHOId();
		this.eLogCredentials	= eSupplyGlobalParameters.getELogCredentials();
		this.accountsCredentials	= eSupplyGlobalParameters.getAccountsCredentials();
	}
	
	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		sb.append("User and Role :"+locationId+"/"+userId+" - "+roleLocationId+"/"+roleId+"\n");
		sb.append("\tUserType : AccessType : userLevel : Module Indicator : Timezone :: "+userType+"-"+accessType+"-"+userLevel+"-"+moduleIndicator+"-"+timeZone+"\n");
		sb.append("\tTerminal-TerminalType-Project-shipmentMode-currencyId :: "+terminalId+"-"+userTerminalType+"-"+projectTransId+"-"+shipmentMode+"-"+currencyId+"\n");
		sb.append("\tLicensee/HOId/Company/Warehouse/Project/CustWh :: "+licenseeId+"/"+hoId+"/"+companyId+"/"+wareHouseId+"/"+projectId+"/"+custWHId+"\n");
		sb.append("\tAccounting Credentials :: "+accountsCredentials+"\n");
		sb.append("\tELog Credentials :: "+eLogCredentials+"\n");
		return sb.toString();
	}

	// for time zones handling
	public String getTimeZone()	{				return timeZone;				}

	public void setTimeZone(String timeZone) {
		this.timeZone	= timeZone;
	}

	public int getRelativeOffset() {			return relativeOffset;			}
	
	public void setRelativeOffset(int relativeOffset) {
		this.relativeOffset	= relativeOffset;
	}
	
	public Timestamp getLocalTime() {
		Timestamp currentTime = new Timestamp((new java.util.Date()).getTime() + relativeOffset);
		return currentTime;
	}
	

	// ELOG / EP SPECIFIC METHODS START

	public ArrayList getCustWHIdInfo()		{	return custWHIdInfo;			}

	public void setCustWHIdInfo( ArrayList custWHIdInfo ) {
		this.custWHIdInfo = custWHIdInfo;
	}

	public ArrayList getWHIdInfo()		{	return custWHIdInfo;			}

	public void setWHIdInfo( ArrayList custWHIdInfo ) {
		this.custWHIdInfo = custWHIdInfo;
	}

	public String getWareHouseName()	{		return wareHouseName;			}
	
	public void setWareHouseName( String wareHouseName ) {
		this.wareHouseName = wareHouseName;
	}

	public void setLoginTime( Timestamp loginTime ) {
		this.loginTime = loginTime;
	}

	// this is added from taking ETrans
	public void setESupplyLoginId( String ESupplyLoginId ) {
		this.ESupplyLoginId = ESupplyLoginId;
		this.userId = ESupplyLoginId;
	}

	public void setESupplyTerminalId( String ESupplyTerminalId ) {
		this.ESupplyTerminalId	= ESupplyTerminalId;
		this.locationId			= ESupplyTerminalId;
	}

	public String getESupplyLoginId() {
		return userId;
	}
	
	public String convertToString( Timestamp timestamp ) {
		return df.format( timestamp );
	}

	/*
	* this is used to convert the String into TimeStamp
	*/
	public Timestamp convertToTimestamp( String strDate ) {
		Timestamp date = null;
		try {
			Date dt = df.parse( strDate );
			date = new Timestamp( dt.getTime() );
		} catch( java.text.ParseException e ) {}
		return date;
	}

	/*
	* this returns the current date string
	*/
	public String getCurrentDateString() {
		Date dt = new Date();
  	return df.format( dt );
	}

	public String	getEPShipmentMode() {
		return this.shipmentModeEP;
	}

	public void	setEPShipmentMode(String shipmentMode) {
		this.shipmentModeEP = shipmentMode;
	}

	// ELOG / EP SPECIFIC METHODS END

	//ESupply Data Members
	
	private String		userId				= null;		//	user's Id
  private String		empId				  = null;		//	EMP Id
	private String		locationId			= null;		//	location of the user can be terminal, custWHId, wh, project, company or can be Licensee
	private String		roleId				= null;		//	user's role
	private String		roleLocationId		= null;		// location of the role belongs to (combination of roleId and roleLocationId becomes the unique identity)

	private String		userLevel			= null;		// user's level
	private String		accessType			= null;		// access type of the user (COMPANY, WAREHOUSE, or TERMINAL
	private String		userType			= null;		// user type

	private String		licenseeId			= null;		//	licensee of the user belongs to
	private String		companyId			= null;		//	company of the user belongs to

	private String		terminalId			= null;		// terminal Id
	private String		userTerminalType	= "";		// User Terminal Type(H/A/O)
	private String		projectTransId		= null;		// project trans id
	
	
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
	  */
	private int			shipmentMode		= FoursoftConfig.SHIPMENT_MODE;	// For ETRANS & eSupply(SP
	private String		shipmentModeEP		= "Air";	// For eSupply(EP)

	private String		projectId			= null;		//	project Id
	private String		wareHouseId			= null;		//	WareHouse Id
	private String		custWHId			= null;		//	CustomerWareHouse Id
	private ArrayList	custWHIdInfo		= null;		//	List of customer warehouses specific for eLog only
	private String		wareHouseName		= ""; 		//	Warehouse Name

	private String		customerId			= null;		//	customerId

	private String		currencyId			= null;						//	currencyId of the user's location
	private Locale		locale				= Locale.UK;				//	Locale of the user's location
	private DateFormat	df					= DateFormat.getDateInstance( DateFormat.SHORT, locale );;				//  Date Format instance of the user's location
	private Timestamp	loginTime			= null;						//	Login Time	
	private String		moduleIndicator		= FoursoftConfig.MODULE;	// The ported product

	private String		timeZone			= null;
	private int			relativeOffset		= 0;	   // relative offset time (localOffset - defaultOffset)

	private String 		ESupplyLoginId		= null;
	private String		ESupplyTerminalId	= null;

	private String		CSSId				= FoursoftConfig.CSSID;
	
	private	UserPreferences		userPreferences		= null;	// User Preferences
	private AccountsCredentials accountsCredentials	= null;	// accounting credentials
	private ELogCredentials		eLogCredentials		= null;	// elog credentials
	private String 				hoId				= null; // for hoId								 
	private HashMap     codeCustProjectId = null;


 //Code Customization
 /* public void setCodeCustCustWHId(ArrayList codeCustCustWHId)
  {
    this.codeCustCustWHId = codeCustCustWHId;
  }
  public ArrayList  getCodeCustCustWHId()
  {
    return codeCustCustWHId;
  } */
  public void setCodeCustStatus(HashMap codeCustProjectId)
  {
    this.codeCustProjectId = codeCustProjectId;
  }
  public HashMap  getCodeCustStatus()
  {
    return codeCustProjectId;
  }


	public static final void setTerminalUserId(java.util.Hashtable current_users) throws FoursoftException{

		int licensedNoOfUsers		=	FoursoftConfig.MAX_USERS_LIMIT;
		int	currentUsersConnected	=	current_users.size();

		if(currentUsersConnected == licensedNoOfUsers) {
			//Logger.warning("ESMenuIndex.jsp","M.U.L.E. : "+new java.util.Date());
      logger.warn("ESMenuIndex.jsp"+"M.U.L.E. : "+new java.util.Date());
				
			throw new FoursoftException(
						"The Limit of Maximum Licensed Concurrent Users "+
						"is being exceeded. Please try again after other User(s) Logout."
						);
		}
	}

	//These Three Methods are Used For Generating a Unique Id in Each Screen of the Application.
	public String generateUniqueId(String filename, String operation)
	{
		StringBuffer returnStrBuff = new StringBuffer();
		if (filename.indexOf(".") != -1)
			returnStrBuff.append(filename.substring(0,filename.indexOf(".")));
		returnStrBuff.append(operation);
		//System.out.println(returnStrBuff.toString());
		return (String)UniqueIDList.uniqueIdList.get(returnStrBuff.toString());
	}
  
	public String generateUniqueId(String filename, String operation, String shipmentMode)
	{
		StringBuffer returnStrBuff = new StringBuffer();
		if (filename.indexOf(".") != -1)
			returnStrBuff.append(filename.substring(0,filename.indexOf(".")));
		returnStrBuff.append(operation);
		returnStrBuff.append(shipmentMode);
		return (String)UniqueIDList.uniqueIdList.get(returnStrBuff.toString());
	}
  
	public String generateUniqueId(String filename, String operation, String shipmentMode, String terminalType)
	{
		StringBuffer returnStrBuff = new StringBuffer();
		if (filename.indexOf(".") != -1)
			returnStrBuff.append(filename.substring(0,filename.indexOf(".")));
		returnStrBuff.append(operation);
		returnStrBuff.append(shipmentMode);
		returnStrBuff.append(terminalType);
		return (String)UniqueIDList.uniqueIdList.get(returnStrBuff.toString());
	}

	// For ELog Files, generating Unique Id in Each Screen of the Application.
	public String getUniqueId(String filename, String process)
	{
		StringBuffer returnStrBuff = new StringBuffer();
		if(filename.indexOf(".") != -1)
			returnStrBuff.append(filename.substring(0, filename.indexOf(".")));
		returnStrBuff.append(process);
		return (String)UniqueIDList.uniqueIdList.get(returnStrBuff.toString().toUpperCase());
	}

}