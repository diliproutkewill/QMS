package com.foursoft.etrans.common.util.ejb.sls;
import java.rmi.RemoteException;

/* **********************************************
* Generated by Pramati Technologies, EJBWizard 
* RemoteInterface Class of the com.foursoft.etrans.common.util.ejb.sls.OIDSessionBean *
* Wed Oct 02 16:22:56 IST 2002
* ***********************************************/

/**
 * interface name :OIDSessionHome
 * @author :
 * @version  :1.6
 */
public interface OIDSession extends javax.ejb.EJBObject
{
	 // @@ Sreelakshmi KVA removed all declarations as public for TogetherJ
	  int getAccountsBookOID()
		throws java.rmi.RemoteException;
	  int getAddressOID()
		throws java.rmi.RemoteException;
	  java.lang.String getBankOID()
		throws java.rmi.RemoteException;
	  java.lang.String getDamageAndShortageOID(java.lang.String param0)
		throws java.rmi.RemoteException;
	  java.lang.String getInventoryOID()
		throws java.rmi.RemoteException;
	  java.lang.String getLocationOID()
		throws java.rmi.RemoteException;
	  java.lang.String getManufactureOID(java.lang.String param0)
		throws java.rmi.RemoteException;
	  java.lang.String getMasterdocOID(java.lang.String param0, java.lang.String param1)
		throws java.rmi.RemoteException;
	  java.lang.String getModelOID(java.lang.String param0)
		throws java.rmi.RemoteException;
	  java.lang.String getPartOID()
		throws java.rmi.RemoteException;
	  java.lang.String getPreventiveMntcOID()
		throws java.rmi.RemoteException;
	  java.lang.String getProjectOID(java.lang.String param0, java.lang.String param1)
		throws java.rmi.RemoteException;
	  java.lang.String getRouteOID()
		throws java.rmi.RemoteException, com.foursoft.etrans.setup.codecust.exception.CodeCustNotDoneException;
	  long getRoutePlanId()
		throws java.rmi.RemoteException;
	 long getShpmntPlanId()
  throws java.rmi.RemoteException;
	  java.lang.String getTrailerOID()
		throws java.rmi.RemoteException;
	 java.lang.String getCodeCustomisationId(java.lang.String param0,com.foursoft.etrans.setup.codecust.bean.CodeCustModelDOB param1)   
    throws java.rmi.RemoteException, com.foursoft.etrans.setup.codecust.exception.CodeCustNotDoneException; 
	  java.lang.String getTruckTrailerInventoryIssueId()
		throws java.rmi.RemoteException;
	  int getTruckTrailerMaintanenceOID()
		throws java.rmi.RemoteException;
    java.lang.String getRunsheetOID()
		throws java.rmi.RemoteException;  
	  java.lang.String getTruckTyreOID()
		throws java.rmi.RemoteException;
	  java.lang.String getTyreMaintenenceOID()
		throws java.rmi.RemoteException;
	  java.lang.String getVehicleOID()
		throws java.rmi.RemoteException;
	  java.lang.String getSubAgentOID()
		throws java.rmi.RemoteException;
	  java.lang.String getCodeCustId(com.foursoft.esupply.common.bean.ESupplyGlobalParameters param0, java.lang.String param1)
		throws java.rmi.RemoteException,com.foursoft.etrans.setup.codecust.exception.CodeCustNotDoneException;  
	  //@@
	public long getZoneCodeId() throws RemoteException;
    
    public double getBuySellChargesOID(com.foursoft.esupply.common.bean.ESupplyGlobalParameters param0,String param1)
    throws java.rmi.RemoteException,java.sql.SQLException;

	public double getBuyCartageOID() throws java.rmi.RemoteException,java.sql.SQLException;
	public String getCustomerOID( String terminalId, String custAbbvName)  throws java.rmi.RemoteException,java.sql.SQLException;
}