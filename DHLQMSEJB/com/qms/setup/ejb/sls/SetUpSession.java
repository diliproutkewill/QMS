package com.qms.setup.ejb.sls;
import com.foursoft.esupply.common.bean.ESupplyGlobalParameters;
import com.foursoft.etrans.common.bean.Address;
import com.foursoft.etrans.setup.codecust.exception.CodeCustNotDoneException;
import com.foursoft.etrans.setup.customer.java.CustContactDtl;
import com.qms.operations.charges.java.BuySellChargesEnterIdDOB;
import com.qms.operations.quote.dob.QuoteFinalDOB;
import com.qms.operations.quote.dob.QuoteMasterDOB;
import com.qms.setup.java.ListMasterDOB;
import com.qms.setup.java.QMSAdvSearchLOVDOB;
import com.qms.setup.java.QMSAttachmentDOB;
import com.qms.setup.java.QMSAttachmentFileDOB;
import com.qms.setup.java.QMSContentDOB;
import com.qms.setup.java.QMSEmailTextDOB;
import java.rmi.RemoteException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import javax.ejb.EJBException;
import javax.ejb.EJBObject;
import javax.ejb.ObjectNotFoundException;
import com.qms.setup.java.ChargeGroupingDOB;
import com.qms.setup.java.ChargesMasterDOB;
import com.qms.setup.java.ChargeBasisMasterDOB;
public interface SetUpSession extends EJBObject 
{
  public java.util.Vector[] getChargeIds_Taxes(String searchId)throws java.rmi.RemoteException;
	java.util.ArrayList getAllCustomerDetails(String terminalId) throws java.rmi.RemoteException;
	
	java.util.ArrayList getCustomerDetail(java.lang.String param0, com.foursoft.esupply.common.bean.ESupplyGlobalParameters param1, java.lang.String param2, java.lang.String param3, java.lang.String param4)
		throws java.rmi.RemoteException;

	java.lang.String createCustomerDetails(com.foursoft.etrans.setup.customer.java.CustomerModel param0, com.foursoft.etrans.common.bean.Address param1, com.foursoft.esupply.common.bean.ESupplyGlobalParameters param2, java.util.ArrayList param3)
		throws com.foursoft.esupply.common.exception.FoursoftException,java.rmi.RemoteException,CodeCustNotDoneException;

  //Added by ashlesh for DHL CR 154393 on 23/01/2009  
  java.lang.StringBuffer uploadCustomerDetails(java.util.ArrayList param1, ESupplyGlobalParameters eSupplyGlobalParameters)
		throws com.foursoft.esupply.common.exception.FoursoftException,java.rmi.RemoteException,CodeCustNotDoneException;

	boolean updateCustomerDetails(com.foursoft.etrans.setup.customer.java.CustomerModel param0, com.foursoft.etrans.common.bean.Address param1, com.foursoft.esupply.common.bean.ESupplyGlobalParameters param2, java.util.ArrayList param3, java.util.ArrayList param4)
		throws java.rmi.RemoteException;

	boolean deleteCustomerDetail(com.foursoft.etrans.setup.customer.java.CustomerModel param0, com.foursoft.etrans.common.bean.Address param1, com.foursoft.esupply.common.bean.ESupplyGlobalParameters param2)
		throws java.rmi.RemoteException;

	boolean upgradeCustomerStatus(com.foursoft.etrans.setup.customer.java.CustomerModel param0)
		throws java.rmi.RemoteException;

	java.util.ArrayList getShipConsDtls(java.lang.String param0, java.lang.String param1, java.lang.String param2, java.lang.String param3)
		throws java.rmi.RemoteException;

	java.lang.String validateCustomer(java.lang.String param0, java.lang.String param1, java.util.ArrayList param2)
		throws java.rmi.RemoteException;

	boolean mapShipOrConsDtls(java.util.ArrayList param0) throws java.rmi.RemoteException;

	java.util.Vector getTerminals(java.lang.String param0)	throws java.rmi.RemoteException;

	java.util.ArrayList getCurrencyIds(java.lang.String param0)	throws java.rmi.RemoteException;

	java.util.ArrayList getCustomerIds(com.foursoft.esupply.common.bean.ESupplyGlobalParameters param0, java.lang.String param1)
		throws java.rmi.RemoteException;

	java.util.ArrayList getCustomerIds(java.lang.String param0, java.lang.String param1, java.lang.String param2, java.lang.String param3, java.lang.String param4, java.lang.String param5)
		throws java.rmi.RemoteException;

	java.util.ArrayList getCustomerIds(java.lang.String param0, java.lang.String param1, java.lang.String param2, java.lang.String param3,String operation)
		throws java.rmi.RemoteException;

	java.util.ArrayList getCountryIds(java.lang.String param0,String terminalId,String operation)	throws java.rmi.RemoteException;
  //@@Added by Yuvraj for AJAX 
  String getCurrencyId(String countryId) throws java.rmi.RemoteException;
     //@@Added by Yuvraj for CR_DHLQMS_1006
  java.util.ArrayList getCountryIds(java.lang.String param0,java.lang.String param1)	throws java.rmi.RemoteException;
  //@@Yuvraj

	java.util.ArrayList getTerminalIdsforThirdStation(java.lang.String param0, java.lang.String param1, java.lang.String param2, java.lang.String param3, java.lang.String param4, java.lang.String param5, java.lang.String param6)
		throws java.rmi.RemoteException;

	public ArrayList getLocations(String searchString,String lovType,String module,String origin,String dest,String serviceLevel) throws java.rmi.RemoteException;

	java.util.ArrayList getTerminalIds(java.lang.String param0, java.lang.String param1, java.lang.String param2, java.lang.String param3, java.lang.String param4,java.lang.String param5)
		throws java.rmi.RemoteException;

	java.util.ArrayList getTerminalIds(java.lang.String param0)
		throws java.rmi.RemoteException;

	java.util.ArrayList getViewCustomerRegistrationReports(java.lang.String param0, int param1, java.lang.String param2, java.lang.String param3,int param4,int param5,String param6,String param7)
		throws java.rmi.RemoteException;

	java.lang.String[][] getViewCarrierRegistration(java.lang.String param0, int param1)
		throws java.rmi.RemoteException;

	java.lang.String[][] getViewTerminalReports(java.lang.String param0, int param1, java.lang.String param2)
		throws java.rmi.RemoteException;

	java.lang.String[][] getViewRevenueSharingReport(java.lang.String param0, int param1)
		throws java.rmi.RemoteException;

	boolean isCarrierExists(java.lang.String param0, java.lang.String param1)
		throws java.rmi.RemoteException;

	boolean setCarrierDetail(com.foursoft.etrans.setup.carrier.bean.CarrierDetail param0, com.foursoft.etrans.common.bean.Address param1, com.foursoft.esupply.common.bean.ESupplyGlobalParameters param2)
		throws java.rmi.RemoteException;

	int updateCarrierDetail(com.foursoft.etrans.setup.carrier.bean.CarrierDetail param0, com.foursoft.etrans.common.bean.Address param1, com.foursoft.esupply.common.bean.ESupplyGlobalParameters param2, java.lang.String param3)
		throws java.rmi.RemoteException;

	 boolean deleteCarrierDetail(com.foursoft.etrans.setup.carrier.bean.CarrierDetail param0, com.foursoft.etrans.common.bean.Address param1, com.foursoft.esupply.common.bean.ESupplyGlobalParameters param2, java.lang.String param3)
		throws java.rmi.RemoteException;

	 boolean checkCarrier(java.lang.String param0, java.lang.String param1)
		throws java.rmi.RemoteException;

	 com.foursoft.etrans.setup.carrier.bean.CarrierDetail getCarrierDetail(java.lang.String param0, java.lang.String param1)
		throws java.rmi.RemoteException;

	 com.foursoft.etrans.common.bean.Address getAddressDetails(int param0)
		throws java.rmi.RemoteException;

	 java.util.ArrayList getSpcificCarrierIds(java.lang.String param0,java.lang.String param1) throws java.rmi.RemoteException;

	 java.util.ArrayList getCarrierIds(java.lang.String param0, java.lang.String param1, java.lang.String param2,String operation,String terminalId)
		throws java.rmi.RemoteException;
   java.util.ArrayList getCarrierIds1(java.lang.String param0, java.lang.String param1, java.lang.String param2,String operation,String terminalId)
		throws java.rmi.RemoteException;
   public ArrayList getCarriersForCSR(String shipmentMode,String originLoc,String destLoc,String terminalId) throws java.rmi.RemoteException;
	 java.util.ArrayList getEventData() throws java.rmi.RemoteException;

	 //java.lang.String[] getLocationIds(String searchString) throws RemoteException;

	 int isServiceLevelIdExists(java.lang.String param0) throws java.rmi.RemoteException;

	 boolean addServiceLevelDetails(com.foursoft.etrans.setup.servicelevel.bean.ServiceLevelJspBean param0, com.foursoft.esupply.common.bean.ESupplyGlobalParameters param1)
		throws java.rmi.RemoteException;

	 boolean updateServiceLevelDetails(com.foursoft.etrans.setup.servicelevel.bean.ServiceLevelJspBean param0, com.foursoft.esupply.common.bean.ESupplyGlobalParameters param1, int param2)
		throws java.rmi.RemoteException;

	 boolean deleteServiceLevelDetails(java.lang.String param0, com.foursoft.esupply.common.bean.ESupplyGlobalParameters param1, int param2)
		throws java.rmi.RemoteException;

	 com.foursoft.etrans.setup.servicelevel.bean.ServiceLevelJspBean getServiceLevelDetails(java.lang.String param0)
		throws java.rmi.RemoteException;

	 java.util.ArrayList getServiceLevelIds(java.lang.String param0, java.lang.String param1,String param2,String param3)
		throws java.rmi.RemoteException;
    	 java.util.ArrayList getServiceLevelIdsHirarchy(java.lang.String param0, java.lang.String param1,String param2,String param3)
		throws java.rmi.RemoteException;

	 java.util.Vector getCountryDetails()	throws java.rmi.RemoteException;

	 java.util.Vector getCompanyInfo()	throws java.rmi.RemoteException;

	java.util.Vector getAgentJVDetails() throws java.rmi.RemoteException;

	java.util.Vector getChargeDetails()	throws java.rmi.RemoteException;

	java.util.Vector getServiceLevelDetails(java.lang.String param0,java.lang.String param1)
		throws java.rmi.RemoteException;

	//java.util.Vector getViewCommodityReports()	throws java.rmi.RemoteException;
  java.util.ArrayList getViewCommodityReports(int param0,int param1,java.lang.String param2,java.lang.String param3)	throws java.rmi.RemoteException;//shyam

	java.util.Vector getTaxDetails(java.lang.String param0)	throws java.rmi.RemoteException;

	java.lang.String[][] getLocationMaster(java.lang.String param0, int param1)
		throws java.rmi.RemoteException;

	boolean addCommodityDetails(com.foursoft.etrans.setup.commodity.bean.CommodityJspBean param0, com.foursoft.esupply.common.bean.ESupplyGlobalParameters param1)
		throws java.rmi.RemoteException;

	boolean updateCommodityMasterDetails(com.foursoft.etrans.setup.commodity.bean.CommodityJspBean param0, com.foursoft.esupply.common.bean.ESupplyGlobalParameters param1)
		throws java.rmi.RemoteException;

	boolean deleteCommodityMasterDetails(java.lang.String param0, com.foursoft.esupply.common.bean.ESupplyGlobalParameters param1)
		throws java.rmi.RemoteException;

	com.foursoft.etrans.setup.commodity.bean.CommodityJspBean getCommodityDetails(java.lang.String param0)
		throws java.rmi.RemoteException;

	java.util.ArrayList getCommodityIds(java.lang.String param0,String terminalId,String operation)
		throws java.rmi.RemoteException;

	java.util.ArrayList getAllCountryIds()
		throws java.rmi.RemoteException;

	java.util.ArrayList getVendorDetails(java.lang.String param0)
		throws java.rmi.RemoteException, java.sql.SQLException;

	java.util.ArrayList getVendorAddressDetails(java.lang.String param0)
		throws java.rmi.RemoteException,
			java.sql.SQLException;

	int setAddVendorDetails(com.foursoft.etrans.truck.setup.bean.ETransTruckingVendor param0, java.lang.String param1, java.lang.String param2)
		throws java.rmi.RemoteException, java.sql.SQLException;

	int setModifyVendorDetails(com.foursoft.etrans.truck.setup.bean.ETransTruckingVendor param0)
		throws java.rmi.RemoteException, java.sql.SQLException;

	boolean deleteVendorDetails(java.lang.String param0, java.lang.String param1)
		throws java.rmi.RemoteException,
			java.sql.SQLException;

	java.util.ArrayList getAllVendorIds()	throws java.rmi.RemoteException;

	java.util.ArrayList getVendorIds(java.lang.String param0,java.lang.String param1,String param2,String param3,String param4)
		throws java.rmi.RemoteException;

	java.util.ArrayList getVendorIdsForAll(java.lang.String param0,java.lang.String param1,String param2,String param3,String param4)
		throws java.rmi.RemoteException;

	StringBuffer getValidityFields(String param0,String param1)throws java.rmi.RemoteException;

	String insertVendorDetails(com.foursoft.etrans.setup.vendorregistration.java.VendorRegistrationJava param0,com.foursoft.esupply.common.bean.ESupplyGlobalParameters param1)throws java.rmi.RemoteException,CodeCustNotDoneException;

	com.foursoft.etrans.setup.vendorregistration.java.VendorRegistrationJava isValidIdGetData(String param0,String param1,java.lang.String param2,java.lang.String param3)throws ObjectNotFoundException,java.rmi.RemoteException;

	String updateVendorDetails(com.foursoft.etrans.setup.vendorregistration.java.VendorRegistrationJava param0)throws java.rmi.RemoteException;

	String	deleteVendorDetails(String param0)throws java.rmi.RemoteException;

	boolean isLocationMasterLocationIdExists(java.lang.String param0) throws java.rmi.RemoteException;

	boolean addLocationMasterDetails(com.foursoft.etrans.setup.location.bean.LocationMasterJspBean param0, com.foursoft.esupply.common.bean.ESupplyGlobalParameters param1)
		throws java.rmi.RemoteException;

	boolean updateLocationMasterDetails(com.foursoft.etrans.setup.location.bean.LocationMasterJspBean param0, com.foursoft.esupply.common.bean.ESupplyGlobalParameters param1)
		throws java.rmi.RemoteException;

	boolean deleteLocationMasterDetails(java.lang.String param0, com.foursoft.esupply.common.bean.ESupplyGlobalParameters param1)
		throws java.rmi.RemoteException;

	com.foursoft.etrans.setup.location.bean.LocationMasterJspBean getLocationMasterDetails(java.lang.String param0)
		throws java.rmi.RemoteException;

	java.util.ArrayList getLocationIds(java.lang.String param0,String operation,String terminalId,String pram1)
		throws java.rmi.RemoteException;
    java.util.ArrayList getLocationIds1(java.lang.String param0,String operation,String terminalId,String pram1)
		throws java.rmi.RemoteException;
    //@@Added by Yuvraj for CR_DHLQMS_1006    
    java.util.ArrayList getLocationIdsForCountry(java.lang.String param0,java.lang.String param1,String terminalId,String shipMode)
    throws java.rmi.RemoteException;//@@Yuvraj
    
	com.foursoft.etrans.sea.setup.port.bean.PortMasterJSPBean getPortMasterDetails(java.lang.String param0)
		throws java.rmi.RemoteException;

	boolean addPortMasterDetails(com.foursoft.etrans.sea.setup.port.bean.PortMasterJSPBean param0, com.foursoft.esupply.common.bean.ESupplyGlobalParameters param1)
		throws java.rmi.RemoteException;

	boolean updatePortMasterDetails(com.foursoft.etrans.sea.setup.port.bean.PortMasterJSPBean param0, com.foursoft.esupply.common.bean.ESupplyGlobalParameters param1)
		throws java.rmi.RemoteException;

	boolean deletePortMasterDetails(java.lang.String param0, com.foursoft.esupply.common.bean.ESupplyGlobalParameters param1)
		throws java.rmi.RemoteException;

	java.util.ArrayList getPortIds(java.lang.String param0, java.lang.String param1, java.lang.String param2, java.lang.String param3,String operation,String terminalId)
		throws java.rmi.RemoteException;

	com.foursoft.etrans.setup.codecust.bean.CodeCustomisationDOB getCodeCustDetails(com.foursoft.esupply.common.bean.ESupplyGlobalParameters param0)
		throws java.rmi.RemoteException;

	java.util.ArrayList getCodeCustomizationCodeType(java.lang.String param0,int param1,java.lang.String param2,java.lang.String param3)//new
		throws java.rmi.RemoteException;

	com.foursoft.etrans.setup.codecust.bean.CodeCustomiseJSPBean getCodeCustomisationDetails(java.lang.String param0,java.lang.String param1)
		throws java.rmi.RemoteException;

	int updateCodeCustomisationDetails(com.foursoft.etrans.setup.codecust.bean.CodeCustomiseJSPBean param0, com.foursoft.esupply.common.bean.ESupplyGlobalParameters param1)
		throws java.rmi.RemoteException;

	boolean addCodeCustomisationDetails(com.foursoft.etrans.setup.codecust.bean.CodeCustomiseJSPBean param0, com.foursoft.esupply.common.bean.ESupplyGlobalParameters param1)
		throws java.rmi.RemoteException;

	java.util.ArrayList getReportFormatNames(java.lang.String param0,java.lang.String param1)
		throws java.rmi.RemoteException;

	public String validateInfo(String originId,String destinationId,String serviceLevelId,String currencyId,String operation)throws java.rmi.RemoteException;

	public com.foursoft.etrans.setup.IATARateMaster.java.IATADtlModel getIATADtls(String originTerminalId,String destinationTerminalId,String serviceLevelId,String operation,int masterId) throws java.rmi.RemoteException;

	public ArrayList  getIATADtls(int masterId)throws java.rmi.RemoteException;

	public String addIATADtls(com.foursoft.etrans.setup.IATARateMaster.java.IATADtlModel  rateDtlModel) throws java.rmi.RemoteException;

	public String addIATADtls(com.foursoft.etrans.setup.IATARateMaster.java.IATADtlModel  rateDtlModel,ArrayList chargeValues)throws java.rmi.RemoteException;

	public String setIATADtls(com.foursoft.etrans.setup.IATARateMaster.java.IATADtlModel  rateDtlModel) throws java.rmi.RemoteException;

	public String updateIATADtls(com.foursoft.etrans.setup.IATARateMaster.java.IATADtlModel  rateDtlModel,ArrayList chargeValues)throws java.rmi.RemoteException;

	public String  removeIATADtls(int IATAmasterId)throws java.rmi.RemoteException;
	
	//public String  removeIATADtls(int IATAmasterId)throws java.rmi.RemoteException;
	
	boolean isHORegistrationCompanyExists(java.lang.String param0) throws java.rmi.RemoteException;

	boolean addHORegistrationDetails(com.foursoft.etrans.setup.company.bean.HORegistrationJspBean param0, com.foursoft.etrans.common.bean.Address param1, com.foursoft.esupply.common.bean.ESupplyGlobalParameters param2)
		throws java.rmi.RemoteException;

	boolean updateHORegistrationDetails(com.foursoft.etrans.setup.company.bean.HORegistrationJspBean param0, com.foursoft.esupply.common.bean.ESupplyGlobalParameters param1, int param2, com.foursoft.etrans.common.bean.Address param3)
		throws java.rmi.RemoteException;

	boolean deleteHORegistrationDetails(java.lang.String param0, com.foursoft.esupply.common.bean.ESupplyGlobalParameters param1, int param2)
		throws java.rmi.RemoteException;

	java.util.ArrayList getCompanyIds(java.lang.String param0, java.lang.String param1,String operation,String terminalId)
		throws java.rmi.RemoteException;

	java.util.ArrayList getCurrencyList2(java.lang.String param0, java.lang.String param1,String terminalId,String operation)
		throws java.rmi.RemoteException;

	java.util.ArrayList getCurrencyList(java.lang.String param0, java.lang.String param1)
		throws java.rmi.RemoteException;

	boolean updateCurrencyConversion(java.lang.String param0, java.lang.String[] param1, java.lang.String[] param2, java.lang.String[] param3,java.lang.String[] param4,java.lang.String param5)
		throws java.rmi.RemoteException;

	boolean addConversionFactor(java.lang.String param0, java.lang.String[] param1, java.lang.String[] param2, java.lang.String[] param3,java.lang.String[] param4,java.lang.String param5)
		throws java.rmi.RemoteException;

	java.lang.String[][] getConversionFactor(java.lang.String param0, java.lang.String[] param1, java.lang.String param2)
		throws java.rmi.RemoteException;

	java.util.Vector getCFactorViewAll(java.lang.String param0, java.lang.String param1)
		throws java.rmi.RemoteException;

	java.lang.String[][] getConversionFactor1(java.lang.String[] param0, java.lang.String[] param1, java.lang.String param2)
		throws java.rmi.RemoteException;

	java.lang.String checkValidOrNot1(java.lang.String[] param0)
		throws java.rmi.RemoteException;

	java.util.Vector getCurrencyView(java.lang.String param0,String terminalId,String operation)
		throws java.rmi.RemoteException;

	java.util.Vector getSelectedCurrency(java.lang.String param0, java.lang.String param1,String terminalId,String operation)
		throws java.rmi.RemoteException;

	java.util.Vector getModifiedCurrencyList(java.lang.String param0, java.lang.String param1,String terminalId,String operation)
		throws java.rmi.RemoteException;

	java.util.Vector getCurrencyList1(java.lang.String param0)
		throws java.rmi.RemoteException;

	java.lang.String getUserTerminalType()
		throws java.rmi.RemoteException, java.sql.SQLException;

	java.lang.String[] getCompanyIds()
		throws java.rmi.RemoteException;

	boolean checkHOTerminal()
		throws java.rmi.RemoteException, java.sql.SQLException;

	java.util.Vector getLocationInfo(java.lang.String param0,java.lang.String param1,java.lang.String param2)
		throws java.rmi.RemoteException;

	boolean isExists(java.lang.String param0)
		throws java.rmi.RemoteException, java.sql.SQLException;

	java.lang.String setTerminalRegDetails(com.foursoft.etrans.setup.terminal.bean.TerminalRegJspBean param0, com.foursoft.etrans.common.bean.Address param1, com.foursoft.esupply.common.bean.ESupplyGlobalParameters param2)
		throws java.rmi.RemoteException;

	java.util.Vector getOperationTerminalInfo(java.lang.String param0, com.foursoft.esupply.common.bean.ESupplyGlobalParameters param1, java.lang.String param2, java.lang.String param3)
		throws java.rmi.RemoteException;
    
	java.util.Vector getOperationTerminalInfo(java.lang.String param0, com.foursoft.esupply.common.bean.ESupplyGlobalParameters param1, java.lang.String param2, java.lang.String param3,java.lang.String param4)
		throws java.rmi.RemoteException;
    //loginbean is added by VLAKSHMI for  issue 173655 on 20090629
	com.foursoft.etrans.setup.terminal.bean.TerminalRegJspBean getTerminalRegDetails(java.lang.String param0, java.lang.String param1,com.foursoft.esupply.common.bean.ESupplyGlobalParameters loginbean)
		throws java.rmi.RemoteException;

	boolean updateTerminalReg(com.foursoft.etrans.setup.terminal.bean.TerminalRegJspBean param0, com.foursoft.etrans.common.bean.Address param1, java.lang.String param2, com.foursoft.esupply.common.bean.ESupplyGlobalParameters param3)
	  throws java.rmi.RemoteException;

	boolean deleteTerminalReg(java.lang.String param0, int param1, java.lang.String param2, java.lang.String param3, com.foursoft.esupply.common.bean.ESupplyGlobalParameters param4)
		throws java.rmi.RemoteException;

	boolean checkGatewayId(java.lang.String param0)throws java.rmi.RemoteException;

	int selectId() throws java.rmi.RemoteException;

	void insGatewayDB(java.lang.String param0, java.lang.String param1, java.lang.String param2, java.lang.String param3, java.lang.String param4, int param5, java.lang.String param6, java.lang.String param7, com.foursoft.etrans.setup.gateway.bean.GatewayJSPBean param8, com.foursoft.etrans.common.bean.Address param9, java.lang.String[] param10, com.foursoft.esupply.common.bean.ESupplyGlobalParameters param11, int param12) throws java.rmi.RemoteException;

	java.lang.String[] loadGatewayId() throws java.rmi.RemoteException;

	com.foursoft.etrans.setup.gateway.bean.GatewayJSPBean viewGatewayDB(java.lang.String param0) throws java.rmi.RemoteException;

	//java.lang.String[] getTerminalIds(java.lang.String param0) throws java.rmi.RemoteException;
			
	//java.lang.String[] loadLocationName()throws java.rmi.RemoteException;

	boolean removeGateway(java.lang.String param0, com.foursoft.esupply.common.bean.ESupplyGlobalParameters param1) throws java.rmi.RemoteException;

	boolean updateGatewayDB(java.lang.String param0, java.lang.String param1, java.lang.String param2, java.lang.String param3, java.lang.String param4, java.lang.String param5, com.foursoft.etrans.setup.gateway.bean.GatewayJSPBean param6, com.foursoft.etrans.common.bean.Address param7, java.lang.String[] param8, com.foursoft.esupply.common.bean.ESupplyGlobalParameters param9) throws java.rmi.RemoteException;

	java.util.ArrayList getGatewayIds(java.lang.String param0, java.lang.String param1, java.lang.String param2, java.lang.String param3) throws java.rmi.RemoteException;

	java.lang.String[][] getCustomsMaster(java.lang.String param0, java.lang.String param1, int param2) throws java.rmi.RemoteException;

	java.lang.String[][] getGWCarrierContracts(java.lang.String param0, java.lang.String param1, int param2) throws java.rmi.RemoteException;

	java.lang.String[][] getCustomerContracts(java.lang.String param0, java.lang.String param1, int param2)	throws java.rmi.RemoteException;

	java.lang.String[][] getCustomerRegistration(java.lang.String param0, java.lang.String param1, int param2)	throws java.rmi.RemoteException;

	java.lang.String[][] getGatewayDetails(java.lang.String param0, int param1) throws java.rmi.RemoteException;

	boolean isCountryMasterCountryAlreadyExists(java.lang.String param0) throws java.rmi.RemoteException;

	void addCountryMasterDetails(com.foursoft.etrans.setup.country.bean.CountryMaster param0, com.foursoft.esupply.common.bean.ESupplyGlobalParameters param1) throws java.rmi.RemoteException;

	boolean updateCountryMasterDetails(com.foursoft.etrans.setup.country.bean.CountryMaster param0, com.foursoft.esupply.common.bean.ESupplyGlobalParameters param1) throws java.rmi.RemoteException;

	boolean deleteCountryMasterDetails(java.lang.String param0, com.foursoft.esupply.common.bean.ESupplyGlobalParameters param1) throws java.rmi.RemoteException;

	com.foursoft.etrans.setup.country.bean.CountryMaster getCountryMasterDetails(java.lang.String param0) throws java.rmi.RemoteException;

    java.lang.String[][] getTerminalMaster(java.lang.String param0, int param1, java.lang.String param2)
		throws java.rmi.RemoteException;
        
    public  java.lang.String getHouseDocId(java.lang.String param0, java.lang.String param1, java.lang.String param2, java.lang.String param3)
		throws java.rmi.RemoteException;
    
    com.foursoft.etrans.setup.company.bean.HORegistrationJspBean getHORegistrationDetails(java.lang.String param0)
		throws java.rmi.RemoteException;
    
        public java.util.Hashtable insertIndustryDetails(java.util.ArrayList param0,com.foursoft.esupply.common.bean.ESupplyGlobalParameters param1)
    throws java.rmi.RemoteException;
    
    public ArrayList getAllIndustryDetails(String param0,String param1,com.foursoft.esupply.common.bean.ESupplyGlobalParameters param2) 
    throws java.rmi.RemoteException;
    
    public com.qms.setup.java.IndustryRegDOB getIndustryDetails(String param0,com.foursoft.esupply.common.bean.ESupplyGlobalParameters loginbean)
    throws java.rmi.RemoteException,javax.ejb.ObjectNotFoundException;
    
    public boolean updateIndustryDetails(com.qms.setup.java.IndustryRegDOB param0,com.foursoft.esupply.common.bean.ESupplyGlobalParameters param1)
    throws java.rmi.RemoteException;
    
    public boolean deleteIndustryDetails(com.qms.setup.java.IndustryRegDOB param0,com.foursoft.esupply.common.bean.ESupplyGlobalParameters param1)
    throws java.rmi.RemoteException;
    
    public boolean invalidateIndustryDetails(ArrayList param0,com.foursoft.esupply.common.bean.ESupplyGlobalParameters param1)    
    throws java.rmi.RemoteException;
    
    public ArrayList insertMarginListDtls(ArrayList param0,com.foursoft.esupply.common.bean.ESupplyGlobalParameters param1)
    throws java.rmi.RemoteException;
  //@@ Commented & Added by subrahmanyam for the pbn id: 203354 on 22-APR-010 
    /*
    public com.qms.setup.java.MarginLimitMasterDOB getMarginLimitDetails(String param0,String param1,String param2,com.foursoft.esupply.common.bean.ESupplyGlobalParameters param3,String param4)
    throws javax.ejb.EJBException,ObjectNotFoundException,java.rmi.RemoteException;
    */
    public com.qms.setup.java.MarginLimitMasterDOB getMarginLimitDetails(String param0,String param1,String param2,com.foursoft.esupply.common.bean.ESupplyGlobalParameters param3,String param4,String param5)
    throws javax.ejb.EJBException,ObjectNotFoundException,java.rmi.RemoteException;
    //203354
    
    public boolean updateMarginLimitDetails(com.qms.setup.java.MarginLimitMasterDOB param0,com.foursoft.esupply.common.bean.ESupplyGlobalParameters param1)
    throws javax.ejb.EJBException,ObjectNotFoundException,java.rmi.RemoteException;

    public boolean deleteMariginLimitMasterDtls(com.qms.setup.java.MarginLimitMasterDOB param0)
    throws javax.ejb.EJBException,ObjectNotFoundException,java.rmi.RemoteException;
    
    public boolean invalidateMarginLimitDetails(ArrayList param0,com.foursoft.esupply.common.bean.ESupplyGlobalParameters loginbean)
    throws javax.ejb.EJBException,ObjectNotFoundException,java.rmi.RemoteException;
    
    public ArrayList getServiceLevelIds(String param0,String param1,String param2,String param3,String param4,com.foursoft.esupply.common.bean.ESupplyGlobalParameters param5)
    throws javax.ejb.EJBException,java.rmi.RemoteException;
    
    public ArrayList getServiceLevelIds1(String searchString,String shipmentMode,String terminalId,String operation)
    throws javax.ejb.EJBException,java.rmi.RemoteException;
    
    public ArrayList getMarginLimitsLevelIds(String param0,String param1,String param2,String param3,String param4,com.foursoft.esupply.common.bean.ESupplyGlobalParameters param5)
    throws javax.ejb.EJBException,java.rmi.RemoteException;

    //@@ Commented and added by subrahmanyam for the pbn id: 203354  on 23-APR-10
  /*  public ArrayList getMarginLimitLevelIds(String param0,String param1,String param2,String param3)
    throws javax.ejb.EJBException,java.rmi.RemoteException;
*/
    public ArrayList getMarginLimitLevelIds(String param0,String param1,String param2,String param3,ESupplyGlobalParameters loginbean,String operation)
    throws javax.ejb.EJBException,java.rmi.RemoteException;
//ended for 203354
    public ArrayList getLevelIds(String param0,com.foursoft.esupply.common.bean.ESupplyGlobalParameters param2,String operation)
    throws javax.ejb.EJBException,java.rmi.RemoteException;
  
    public ArrayList getMarginLimitList(String param0,String param1,String param2,String[] param3,String[] param4,com.foursoft.esupply.common.bean.ESupplyGlobalParameters param5)
    throws javax.ejb.EJBException,java.rmi.RemoteException;
    
     public ArrayList getMarginLimitList1(String param0,String param1,String[] param2,com.foursoft.esupply.common.bean.ESupplyGlobalParameters param4)
    throws javax.ejb.EJBException,java.rmi.RemoteException;
    
    public ArrayList insertChargesMasterDtls(ArrayList param0,com.foursoft.esupply.common.bean.ESupplyGlobalParameters param1)
    throws javax.ejb.EJBException,java.rmi.RemoteException;

    public ChargesMasterDOB getChargesMasterDtl(String param0,com.foursoft.esupply.common.bean.ESupplyGlobalParameters param2,String para3)
    throws ObjectNotFoundException,javax.ejb.EJBException,java.rmi.RemoteException;
    
    public boolean updateChargesMasterDetails(ChargesMasterDOB param0,com.foursoft.esupply.common.bean.ESupplyGlobalParameters param2)
    throws ObjectNotFoundException,javax.ejb.EJBException,java.rmi.RemoteException;

    public boolean deleteChargesMasterDtls(ChargesMasterDOB param0,com.foursoft.esupply.common.bean.ESupplyGlobalParameters param1)
    throws ObjectNotFoundException,javax.ejb.EJBException,java.rmi.RemoteException;
    

   public boolean insertChargesGroupDtls(ArrayList dataList,com.foursoft.esupply.common.bean.ESupplyGlobalParameters loginbean)
   throws javax.ejb.EJBException,java.rmi.RemoteException;
    
   public ArrayList getChargeGroupDtl(BuySellChargesEnterIdDOB param0,com.foursoft.esupply.common.bean.ESupplyGlobalParameters param1)
   throws ObjectNotFoundException,javax.ejb.EJBException,java.rmi.RemoteException;
    
   /*public ArrayList getChargeGroupDtl(String chargeGroupId,com.foursoft.esupply.common.bean.ESupplyGlobalParameters loginbean)
   throws ObjectNotFoundException,javax.ejb.EJBException,java.rmi.RemoteException;
   */
   
   public boolean updateChargesGroupDetails(ArrayList param0,com.foursoft.esupply.common.bean.ESupplyGlobalParameters loginbean)
   throws ObjectNotFoundException,javax.ejb.EJBException,java.rmi.RemoteException;
   //@@Commented & Added the signature by subrahmanyam for the pbn id: 201931 on 05-04-2010
  /* public boolean deleteChargesGroupDtls(String param0,com.foursoft.esupply.common.bean.ESupplyGlobalParameters loginbean)
   throws ObjectNotFoundException,javax.ejb.EJBException,java.rmi.RemoteException;
  */
   public ArrayList deleteChargesGroupDtls(String param0,com.foursoft.esupply.common.bean.ESupplyGlobalParameters loginbean)
   throws ObjectNotFoundException,javax.ejb.EJBException,java.rmi.RemoteException;

   public ArrayList  getAllChargeGroupIds(BuySellChargesEnterIdDOB param0,com.foursoft.esupply.common.bean.ESupplyGlobalParameters param1)
   throws javax.ejb.EJBException,java.rmi.RemoteException,java.sql.SQLException;
  
   public ArrayList  getAllChargeGroupIds(String param0, String param1, String param2, String param3)
   throws javax.ejb.EJBException,java.rmi.RemoteException,java.sql.SQLException;

   public ArrayList insertChargesBasisDtls(ArrayList param0,com.foursoft.esupply.common.bean.ESupplyGlobalParameters param1)
   throws javax.ejb.EJBException,java.rmi.RemoteException;
   
   public ChargeBasisMasterDOB getChargeBasisDtl(String param0,String param3,com.foursoft.esupply.common.bean.ESupplyGlobalParameters param1)
   throws ObjectNotFoundException,javax.ejb.EJBException,java.rmi.RemoteException;
   
   public boolean updateChargesBasisDetails(ChargeBasisMasterDOB param0,com.foursoft.esupply.common.bean.ESupplyGlobalParameters param1)
   throws ObjectNotFoundException,javax.ejb.EJBException,java.rmi.RemoteException;
   
   public boolean deleteChargesBasisDtls(ChargeBasisMasterDOB param0,com.foursoft.esupply.common.bean.ESupplyGlobalParameters param1)
   throws ObjectNotFoundException,javax.ejb.EJBException,java.rmi.RemoteException;
   
   public ArrayList  getAllChargeBasisIds(String param0,String param1,String param4,com.foursoft.esupply.common.bean.ESupplyGlobalParameters param2)
   throws javax.ejb.EJBException,java.rmi.RemoteException,java.sql.SQLException;
   
    public java.util.ArrayList getInvalidateCountryDetails() throws java.rmi.RemoteException;
     public boolean invalidateCountry(ArrayList dobList) throws RemoteException;
     public boolean invalidateLocation(ArrayList dobList) throws RemoteException;
    public java.util.ArrayList getLocationDetails() throws RemoteException;
    public java.util.ArrayList getInvalidatePortMasterDetails() throws RemoteException;
    public boolean invalidatePortMaster(ArrayList dobList) throws RemoteException;
    public boolean invalidateCustomerMaster(ArrayList dobList) throws RemoteException;
    public boolean invalidateTerminalMaster(ArrayList dobList) throws RemoteException;
    public java.util.ArrayList getInvalidateCustomerMasterDetails() throws RemoteException;
    public java.util.ArrayList getInvalidateCarrierDetails() throws RemoteException;
    public boolean invalidateCarrierMaster(ArrayList dobList) throws RemoteException;
    //public java.util.ArrayList getInvalidateCommodityDetails() throws RemoteException;
    public java.util.ArrayList getInvalidateCommodityDetails(int param0,int param1,String param2,String param3) throws RemoteException;
    public boolean invalidateCommodityMaster(ArrayList dobList) throws RemoteException;
    public java.util.ArrayList getInvalidateTerminalDetails() throws RemoteException;
    public java.util.ArrayList getInvalidateServiceLevelDetails() throws RemoteException;
   public boolean invalidateServiceLevelMaster(ArrayList dobList) throws RemoteException;
    public	java.util.HashMap uploadCountryMasterDetails( java.util.ArrayList	countryList,boolean addModFlag) throws java.sql.SQLException,java.rmi.RemoteException;
    
    public	java.util.HashMap uploadLocationMasterDetails( ArrayList	locationList,boolean addModFlag) throws java.rmi.RemoteException,java.sql.SQLException;
    
    public	java.util.HashMap uploadPortMasterDetails( ArrayList	portList,boolean addModFlag) throws java.rmi.RemoteException,java.sql.SQLException;

    public	java.util.HashMap uploadCommodityMasterDetails( ArrayList	commodityList,boolean addModFlag) throws java.rmi.RemoteException,java.sql.SQLException;
    public ArrayList getTerminalIdsforThirdStation(String locationId,String searchString) throws RemoteException;
    com.foursoft.etrans.setup.taxes.bean.TaxMaster getTaxDetails(java.lang.String param0, com.foursoft.esupply.common.bean.ESupplyGlobalParameters param1, java.lang.String param2,java.lang.String param3)
    throws java.rmi.RemoteException;
    java.lang.String isTaxMasterTaxIdExists(java.lang.String param0, com.foursoft.esupply.common.bean.ESupplyGlobalParameters param1, com.foursoft.etrans.setup.taxes.bean.TaxMaster param2)
    throws java.rmi.RemoteException;
    boolean addTaxMasterDetails(com.foursoft.etrans.setup.taxes.bean.TaxMaster param0, com.foursoft.esupply.common.bean.ESupplyGlobalParameters param1,java.util.ArrayList param2)
    throws java.rmi.RemoteException;
    boolean updateTaxMasterDetails(com.foursoft.etrans.setup.taxes.bean.TaxMaster param0, com.foursoft.esupply.common.bean.ESupplyGlobalParameters param1,java.util.ArrayList param2)
    throws java.rmi.RemoteException;
    boolean deleteTaxMasterDetails(com.foursoft.etrans.setup.taxes.bean.TaxMaster param0, com.foursoft.esupply.common.bean.ESupplyGlobalParameters param1)
    throws java.rmi.RemoteException;
 
	java.util.ArrayList getTaxMasterTaxIds(java.lang.String param0, com.foursoft.esupply.common.bean.ESupplyGlobalParameters param1, java.lang.String param2, java.lang.String param3, java.lang.String param4)
    throws java.rmi.RemoteException;


    public ArrayList insertListMasterDetails(ArrayList dataList) throws RemoteException;
    public ArrayList getListTypeIds(String searchString,String shipmentMode,String operation,String terminalId) throws RemoteException;
    public ListMasterDOB getListMasterDetails(String shipmentType,String listType)  throws javax.ejb.ObjectNotFoundException,java.rmi.RemoteException;
     public boolean updateListMasterDetails(ListMasterDOB listMasterDOB)throws RemoteException,ObjectNotFoundException;
     public boolean deleteListMasterDtls(ListMasterDOB listMasterDOB)throws RemoteException,ObjectNotFoundException;
     public java.util.ArrayList getListDetails() throws RemoteException;
     public boolean invalidateListMaster(ArrayList dobList) throws RemoteException;
     public ArrayList getLocIds(String searchString,String searchString2,String terminalId,String operation,String shipmode) throws RemoteException;
      public ArrayList getCountryIds1(String searchString,String locationId,String shipmentMode) throws RemoteException;
     public ArrayList getZoneCodes(String locationId,String shipmentMode,String consoleType) throws RemoteException;
    //Modified by RajKumari on 10-11-2008 for 143511 ..
	 public ArrayList getZoneCodes(String locationId, String zipCode,String zoneCode,String shipmentMode,String consoleType) throws RemoteException;   
   public String insertCustContactDetial(ArrayList list,String custId,String TerminalId)throws RemoteException,SQLException;
   public StringBuffer insertCustAddrDetails(String custId,String terminalId,CustContactDtl custcontactDtl,Address address) throws RemoteException,EJBException;
   public ArrayList getContactNames(String customerId) throws SQLException,RemoteException;
   public HashMap addContentDiscriptionDtls(ArrayList contentDataList) throws RemoteException;
   
   public boolean modifyContentDescription(QMSContentDOB contentDOB)throws RemoteException;
   
   public boolean deleteContentDetails(String contentId)throws RemoteException;
   
   public QMSContentDOB getContentDetails(String contentId,String terminalId)throws RemoteException;
   
   public ArrayList getContentIds(String shipmentMode,String searchString,String operation,String terminalId)throws RemoteException;
   
   public void invalidateChargeBasisId(ArrayList chargeList)throws EJBException,RemoteException;
   
   public void invalidateChargeMasterId(ArrayList chargeList)throws EJBException,RemoteException;
   
   public ArrayList getChargeBasisDetails() throws RemoteException;
   
   public ArrayList getChargeMasterDetails() throws RemoteException;
   
    public void invalidateChargeGroupId(ArrayList chargeList)throws RemoteException;
    
     public ArrayList getChargeGroupMasterDetails(String operation,String terminalId ,String param1) throws RemoteException;
   //@@ Commented & Added by subrahmanyam for the pbn id: 203873 on 26-APR-10  
   //public ArrayList getAllContentDetails(String operation,String terminalId)throws EJBException,RemoteException;
     public ArrayList getAllContentDetails(String operation,String terminalId,String logingAccessType)throws EJBException,RemoteException;
   
   public void invalidateContentDtls(ArrayList chargeList)throws EJBException,RemoteException,SQLException;
   
   public ArrayList getCommodityIds(String searchString,String comodityType,ESupplyGlobalParameters loginbean) throws EJBException,RemoteException;
   
   public ArrayList getLocationIdsAndPorts(String terminalId,String operation,String searchString,String shipmentMode)throws EJBException,RemoteException;
   
  // public ArrayList getDensityRatioIds(String dgcCode,ESupplyGlobalParameters loginbean)throws RemoteException,EJBException,SQLException;
  public ArrayList getDensityRatioIds(String dgcCode,ESupplyGlobalParameters loginbean,String searchString)throws RemoteException,EJBException,SQLException;//@@Commented and Modified by Kameswari for LOV issue

   public ArrayList getLocationsUnderTerminal(String terminalId,String accessLevel,String searchString,String shipmentMode)throws RemoteException,EJBException; 
   
   public ArrayList getCustomerIds(ESupplyGlobalParameters loginBean)throws RemoteException,EJBException;
   public ArrayList getLoctIds(ESupplyGlobalParameters loginBean,String searchString)throws RemoteException,EJBException;
   public ArrayList getSalesPersonIds(ESupplyGlobalParameters loginBean,String terminalIds)throws RemoteException,EJBException;
   public ArrayList getContactNames(String customerId,String addressType) throws RemoteException,EJBException;
  // public ArrayList getCostingContactNames(String customerId,long quoteId) throws RemoteException,EJBException;
  public ArrayList getCostingContactNames(String customerId,String quoteId) throws RemoteException,EJBException;//@@modified by kameswari on 11/02/09
    // public ArrayList getDensityRatioIds(String dgcCode,String uom, ESupplyGlobalParameters loginbean)throws RemoteException,EJBException,SQLException;
   public ArrayList getDensityRatioIds(String dgcCode,String uom, ESupplyGlobalParameters loginbean,String searchStr)throws RemoteException,EJBException,SQLException;//@@Commented and Modified by Sunil for LOV issue

   //public ArrayList getDensityRatioIdsForRates(String dgcCode,String uom, ESupplyGlobalParameters loginbean)throws RemoteException,EJBException,SQLException;
    public ArrayList getDensityRatioIdsForRates(String dgcCode,String uom, ESupplyGlobalParameters loginbean,String searchString)throws RemoteException,EJBException,SQLException;//@@Commented and Modified by Kameswari for LOV issue
  
   public ArrayList getAdvSerchLov(QMSAdvSearchLOVDOB advSearchLovDOB)throws RemoteException,EJBException;
   public HashMap uploadChargeGroupDetails (ArrayList successList,String process,String terminalId) throws RemoteException,EJBException;
   //@@Added by Kameswari for the WPBN issue-61295
   public int addEmailTextDtls(QMSEmailTextDOB emailTextDOB)throws RemoteException,EJBException;
   public QMSEmailTextDOB viewEmailTextDtls(QMSEmailTextDOB dob)throws RemoteException,EJBException;
   public int updateEmailTextDtls(QMSEmailTextDOB emailTextDOB)throws RemoteException,EJBException;
   public int deleteEmailTextDtls(QMSEmailTextDOB emailTextDOB)throws RemoteException,EJBException;
 //@@WPBN issue-61295
  //@@Added by Kameswari for the WPBN issue-61289
   public int addAttachmentDtls(QMSAttachmentDOB attachmentDOB)throws RemoteException,EJBException;
   public int validateFields(QMSAttachmentDOB attachmentDOB)throws RemoteException,EJBException;
   public QMSAttachmentDOB viewAttachmentDtls(QMSAttachmentDOB attachmentDOB)throws RemoteException,EJBException;
   public int updateAttachmentDtls(QMSAttachmentDOB attachmentDOB)throws RemoteException,EJBException;
   public int deleteAttachmentDtls(QMSAttachmentDOB attachmentDOB)throws RemoteException,EJBException;
 //Commented & Added by subrahmanyam for the pbn id:203350
   // public ArrayList viewAllAttachmentDtls()throws RemoteException,EJBException;
   public ArrayList viewAllAttachmentDtls(String loginAccessType,String loginTerminal)throws RemoteException,EJBException;   
   public byte[] viewPDFFile(long uniqueId)throws RemoteException,EJBException;
   public ArrayList attachmentIdList(QMSAttachmentDOB dob)throws RemoteException,EJBException;
   public ArrayList attachFile(QMSAttachmentFileDOB dob)throws RemoteException,EJBException;
   public ArrayList deleteFile(ArrayList dobList)throws RemoteException,EJBException;
   public ArrayList viewFile(String attachmentId)throws RemoteException,EJBException; 
   public int invalidateAttachmentId(ArrayList dobList)throws RemoteException,EJBException; 
   public ArrayList  quoteAttachmentIdList()throws RemoteException,EJBException;
    //@@WPBN issue-61289
   public ArrayList getCustomerAddress()throws RemoteException,EJBException;//@@Added by Kameswari for the WPBN issue-61314
   public ArrayList getTerminalIdsforAttachmentMaster(String terminalType,String terminalId,String searchString)throws RemoteException,EJBException;//@@Added by Kameswari for the WPBN issue-66410
   public ArrayList getChargeDescList()throws RemoteException,EJBException;//@@Added by Kameswari for chargebasis enhancement
   public ArrayList getTerminalList(String TerminalId)throws RemoteException,EJBException;//@@ Added by subrahmanyam for Enhancement 167669 on 27/04/09
    //added by phani sekhar for wpbn 167678 on 
    public ArrayList getContactNamesforAttentionLOV(String customerId,String addressType,String quoteId,String flag)  throws SQLException,RemoteException;
   //added by phani sekhar for wpbn 171213 on 20090615
    public ArrayList getRegionIds(String searchString,String countryId,String locationId,String sMode)throws RemoteException,EJBException;
     public ArrayList getRegionalCountryIds(String searchString,String locationId,String regionId, String sMode)throws RemoteException,EJBException;
     public ArrayList getRegionalLocIds(String regionId,String terminald,String operation,String shipMentMode)throws RemoteException,EJBException;
//ends 171213
/*    public ArrayList getAllChargeIds(String para0,String param3,String param4,String param5,com.foursoft.esupply.common.bean.ESupplyGlobalParameters param2)
    throws javax.ejb.EJBException,java.rmi.RemoteException;
    *///modfied by VLAKSHMI for CR #170761 on 20090626
   public ArrayList getAllChargeIds(String para0,String param3,String param4,String param5,com.foursoft.esupply.common.bean.ESupplyGlobalParameters param2,String param6 )
    throws javax.ejb.EJBException,java.rmi.RemoteException;
   //Added by Anil.k for Enhancement 231214 on 25Jan2011 
   public ArrayList  getAllChargeGroupIds(String searchStr, String terminalId, String shipmentMode, String accessLevel, String originLocation, String destLocation)throws javax.ejb.EJBException,java.rmi.RemoteException,java.sql.SQLException;
   //Added by Anil.k for Issue 236357 on 22Feb2011
   public ArrayList getChargeGroupDtl(String operation,BuySellChargesEnterIdDOB param0,com.foursoft.esupply.common.bean.ESupplyGlobalParameters param1)
   throws ObjectNotFoundException,javax.ejb.EJBException,java.rmi.RemoteException;
   
}