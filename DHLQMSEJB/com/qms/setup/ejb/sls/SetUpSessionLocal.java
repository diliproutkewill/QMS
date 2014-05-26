package com.qms.setup.ejb.sls;
import com.foursoft.esupply.common.bean.ESupplyGlobalParameters;
import com.foursoft.esupply.common.exception.FoursoftException;
import com.foursoft.etrans.setup.codecust.exception.CodeCustNotDoneException;
import com.qms.setup.java.ListMasterDOB;
import com.qms.setup.java.QMSContentDOB;
import java.util.ArrayList;
import java.util.HashMap;
import javax.ejb.EJBLocalObject;
import javax.ejb.ObjectNotFoundException;

public interface SetUpSessionLocal extends EJBLocalObject 
{
	java.util.ArrayList getAllCustomerDetails(String terminalId);

	java.util.ArrayList getCustomerDetail(java.lang.String param0, com.foursoft.esupply.common.bean.ESupplyGlobalParameters param1, java.lang.String param2, java.lang.String param3, java.lang.String param4);

	java.lang.String createCustomerDetails(com.foursoft.etrans.setup.customer.java.CustomerModel param0, com.foursoft.etrans.common.bean.Address param1, com.foursoft.esupply.common.bean.ESupplyGlobalParameters param2, java.util.ArrayList param3)
  throws FoursoftException,CodeCustNotDoneException;

	boolean updateCustomerDetails(com.foursoft.etrans.setup.customer.java.CustomerModel param0, com.foursoft.etrans.common.bean.Address param1, com.foursoft.esupply.common.bean.ESupplyGlobalParameters param2, java.util.ArrayList param3, java.util.ArrayList param4);

	boolean deleteCustomerDetail(com.foursoft.etrans.setup.customer.java.CustomerModel param0, com.foursoft.etrans.common.bean.Address param1, com.foursoft.esupply.common.bean.ESupplyGlobalParameters param2);


	boolean upgradeCustomerStatus(com.foursoft.etrans.setup.customer.java.CustomerModel param0);

	java.util.ArrayList getShipConsDtls(java.lang.String param0, java.lang.String param1, java.lang.String param2, java.lang.String param3);

	java.lang.String validateCustomer(java.lang.String param0, java.lang.String param1, java.util.ArrayList param2);

	boolean mapShipOrConsDtls(java.util.ArrayList param0);

	java.util.Vector getTerminals(java.lang.String param0);

	java.util.ArrayList getCurrencyIds(java.lang.String param0);

	java.util.ArrayList getCustomerIds(com.foursoft.esupply.common.bean.ESupplyGlobalParameters param0, java.lang.String param1);

	java.util.ArrayList getCustomerIds(java.lang.String param0, java.lang.String param1, java.lang.String param2, java.lang.String param3, java.lang.String param4, java.lang.String param5);

	java.util.ArrayList getCustomerIds(java.lang.String param0, java.lang.String param1, java.lang.String param2, java.lang.String param3,String operation);

	java.util.ArrayList getCountryIds(java.lang.String param0,String terminalId,String operation);

	java.util.ArrayList getTerminalIdsforThirdStation(java.lang.String param0, java.lang.String param1, java.lang.String param2, java.lang.String param3, java.lang.String param4, java.lang.String param5, java.lang.String param6);

	public ArrayList getLocations(String searchString,String lovType,String module,String origin,String dest,String serviceLevel) ;

	java.util.ArrayList getTerminalIds(java.lang.String param0, java.lang.String param1, java.lang.String param2, java.lang.String param3, java.lang.String param4,java.lang.String param5);

	java.util.ArrayList getTerminalIds(java.lang.String param0);

//	java.lang.String[][] getViewCustomerRegistrationReports(java.lang.String param0, int param1, java.lang.String param2, java.lang.String param3);
java.util.ArrayList getViewCustomerRegistrationReports(java.lang.String param0, int param1, java.lang.String param2, java.lang.String param3,int param4,int param5,String param6,String param7);

	java.lang.String[][] getViewCarrierRegistration(java.lang.String param0, int param1);

	java.lang.String[][] getViewTerminalReports(java.lang.String param0, int param1, java.lang.String param2);

	java.lang.String[][] getViewRevenueSharingReport(java.lang.String param0, int param1);

	boolean isCarrierExists(java.lang.String param0, java.lang.String param1);

	boolean setCarrierDetail(com.foursoft.etrans.setup.carrier.bean.CarrierDetail param0, com.foursoft.etrans.common.bean.Address param1, com.foursoft.esupply.common.bean.ESupplyGlobalParameters param2);

	int updateCarrierDetail(com.foursoft.etrans.setup.carrier.bean.CarrierDetail param0, com.foursoft.etrans.common.bean.Address param1, com.foursoft.esupply.common.bean.ESupplyGlobalParameters param2, java.lang.String param3);

	 boolean deleteCarrierDetail(com.foursoft.etrans.setup.carrier.bean.CarrierDetail param0, com.foursoft.etrans.common.bean.Address param1, com.foursoft.esupply.common.bean.ESupplyGlobalParameters param2, java.lang.String param3);

	 boolean checkCarrier(java.lang.String param0, java.lang.String param1);

	 com.foursoft.etrans.setup.carrier.bean.CarrierDetail getCarrierDetail(java.lang.String param0, java.lang.String param1);
	
	 com.foursoft.etrans.common.bean.Address getAddressDetails(int param0);

	 java.util.ArrayList getSpcificCarrierIds(java.lang.String param0,java.lang.String param1);

	 java.util.ArrayList getCarrierIds(java.lang.String param0, java.lang.String param1, java.lang.String param2,String operation,String terminalId);
   java.util.ArrayList getCarrierIds1(java.lang.String param0, java.lang.String param1, java.lang.String param2,String operation,String terminalId);

	 java.util.ArrayList getEventData();

	 int isServiceLevelIdExists(java.lang.String param0);

	 boolean addServiceLevelDetails(com.foursoft.etrans.setup.servicelevel.bean.ServiceLevelJspBean param0, com.foursoft.esupply.common.bean.ESupplyGlobalParameters param1);

	 boolean updateServiceLevelDetails(com.foursoft.etrans.setup.servicelevel.bean.ServiceLevelJspBean param0, com.foursoft.esupply.common.bean.ESupplyGlobalParameters param1, int param2);

	 boolean deleteServiceLevelDetails(java.lang.String param0, com.foursoft.esupply.common.bean.ESupplyGlobalParameters param1, int param2);

	 //com.foursoft.etrans.setup.servicelevel.bean.ServiceLevelJspBean getServiceLevelDetails(java.lang.String param0,java.lang.String param1);
	 

	 java.util.ArrayList getServiceLevelIds(java.lang.String param0, java.lang.String param1,String param2,String param3);
   
   public ArrayList getServiceLevelIds1(String searchString,String shipmentMode,String terminalId,String operation);

	 java.util.Vector getCountryDetails();

	 java.util.Vector getCompanyInfo();

	 java.util.Vector getAgentJVDetails();

	 java.util.Vector getChargeDetails();

	 java.util.Vector getServiceLevelDetails(java.lang.String param0,java.lang.String param1);

	 //java.util.Vector getViewCommodityReports();
   java.util.ArrayList getViewCommodityReports(int param0,int param1,java.lang.String param2,java.lang.String param3);

	 java.util.Vector getTaxDetails(java.lang.String param0);

	 java.lang.String[][] getLocationMaster(java.lang.String param0, int param1);

	boolean addCommodityDetails(com.foursoft.etrans.setup.commodity.bean.CommodityJspBean param0, com.foursoft.esupply.common.bean.ESupplyGlobalParameters param1);

	boolean updateCommodityMasterDetails(com.foursoft.etrans.setup.commodity.bean.CommodityJspBean param0, com.foursoft.esupply.common.bean.ESupplyGlobalParameters param1);

	boolean deleteCommodityMasterDetails(java.lang.String param0, com.foursoft.esupply.common.bean.ESupplyGlobalParameters param1);

	com.foursoft.etrans.setup.commodity.bean.CommodityJspBean getCommodityDetails(java.lang.String param0);

	java.util.ArrayList getCommodityIds(java.lang.String param0,String terminalId,String operation);

	java.util.ArrayList getAllCountryIds();

	java.util.ArrayList getVendorDetails(java.lang.String param0) throws java.sql.SQLException;

	java.util.ArrayList getVendorAddressDetails(java.lang.String param0) throws java.sql.SQLException;

	int setAddVendorDetails(com.foursoft.etrans.truck.setup.bean.ETransTruckingVendor param0, java.lang.String param1, java.lang.String param2) throws java.sql.SQLException;

	int setModifyVendorDetails(com.foursoft.etrans.truck.setup.bean.ETransTruckingVendor param0) throws java.sql.SQLException;

	boolean deleteVendorDetails(java.lang.String param0, java.lang.String param1) throws java.sql.SQLException;

	java.util.ArrayList getAllVendorIds();

	java.util.ArrayList getVendorIds(java.lang.String param0,java.lang.String param1,String param2,String param3,String param4);

	java.util.ArrayList getVendorIdsForAll(java.lang.String param0,java.lang.String param1,String param2,String param3,String param4);

	StringBuffer getValidityFields(String param0,String param1);

	String insertVendorDetails(com.foursoft.etrans.setup.vendorregistration.java.VendorRegistrationJava param0,com.foursoft.esupply.common.bean.ESupplyGlobalParameters param1)throws CodeCustNotDoneException;

	com.foursoft.etrans.setup.vendorregistration.java.VendorRegistrationJava isValidIdGetData(String param0,String param1,java.lang.String param2,java.lang.String param3)throws ObjectNotFoundException;

	String updateVendorDetails(com.foursoft.etrans.setup.vendorregistration.java.VendorRegistrationJava param0);

	String	deleteVendorDetails(String param0);

	boolean isLocationMasterLocationIdExists(java.lang.String param0);

	boolean addLocationMasterDetails(com.foursoft.etrans.setup.location.bean.LocationMasterJspBean param0, com.foursoft.esupply.common.bean.ESupplyGlobalParameters param1);

	boolean updateLocationMasterDetails(com.foursoft.etrans.setup.location.bean.LocationMasterJspBean param0, com.foursoft.esupply.common.bean.ESupplyGlobalParameters param1);

	
	boolean deleteLocationMasterDetails(java.lang.String param0, com.foursoft.esupply.common.bean.ESupplyGlobalParameters param1);

	com.foursoft.etrans.setup.location.bean.LocationMasterJspBean getLocationMasterDetails(java.lang.String param0);

	java.util.ArrayList getLocationIds(java.lang.String param0,String operation,String terminalId,String param1);
  java.util.ArrayList getLocationIds1(java.lang.String param0,String operation,String terminalId,String param1);

	com.foursoft.etrans.sea.setup.port.bean.PortMasterJSPBean getPortMasterDetails(java.lang.String param0);

	boolean addPortMasterDetails(com.foursoft.etrans.sea.setup.port.bean.PortMasterJSPBean param0, com.foursoft.esupply.common.bean.ESupplyGlobalParameters param1);

	boolean updatePortMasterDetails(com.foursoft.etrans.sea.setup.port.bean.PortMasterJSPBean param0, com.foursoft.esupply.common.bean.ESupplyGlobalParameters param1);

	boolean deletePortMasterDetails(java.lang.String param0, com.foursoft.esupply.common.bean.ESupplyGlobalParameters param1);

	java.util.ArrayList getPortIds(java.lang.String param0, java.lang.String param1, java.lang.String param2, java.lang.String param3,String operation,String terminalId);

	com.foursoft.etrans.setup.codecust.bean.CodeCustomisationDOB getCodeCustDetails(com.foursoft.esupply.common.bean.ESupplyGlobalParameters param0);

	java.util.ArrayList getCodeCustomizationCodeType(java.lang.String param0,int param1,java.lang.String param2,java.lang.String param3);

	com.foursoft.etrans.setup.codecust.bean.CodeCustomiseJSPBean getCodeCustomisationDetails(java.lang.String param0,java.lang.String param1);

	int updateCodeCustomisationDetails(com.foursoft.etrans.setup.codecust.bean.CodeCustomiseJSPBean param0, com.foursoft.esupply.common.bean.ESupplyGlobalParameters param1);

	boolean addCodeCustomisationDetails(com.foursoft.etrans.setup.codecust.bean.CodeCustomiseJSPBean param0, com.foursoft.esupply.common.bean.ESupplyGlobalParameters param1);

	java.util.ArrayList getReportFormatNames(java.lang.String param0,java.lang.String param1);

	public String validateInfo(String originId,String destinationId,String serviceLevelId,String currencyId,String operation);

	public com.foursoft.etrans.setup.IATARateMaster.java.IATADtlModel getIATADtls(String originTerminalId,String destinationTerminalId,String serviceLevelId,String operation,int masterId);

	public String addIATADtls(com.foursoft.etrans.setup.IATARateMaster.java.IATADtlModel  rateDtlModel) ;

	public String addIATADtls(com.foursoft.etrans.setup.IATARateMaster.java.IATADtlModel  rateDtlModel,ArrayList chargeValues);

	public String setIATADtls(com.foursoft.etrans.setup.IATARateMaster.java.IATADtlModel  rateDtlModel) ;

	public String updateIATADtls(com.foursoft.etrans.setup.IATARateMaster.java.IATADtlModel  rateDtlModel,ArrayList chargeValues);

	public String  removeIATADtls(int IATAmasterId);

	boolean isHORegistrationCompanyExists(java.lang.String param0);

	boolean addHORegistrationDetails(com.foursoft.etrans.setup.company.bean.HORegistrationJspBean param0, com.foursoft.etrans.common.bean.Address param1, com.foursoft.esupply.common.bean.ESupplyGlobalParameters param2);

	boolean updateHORegistrationDetails(com.foursoft.etrans.setup.company.bean.HORegistrationJspBean param0, com.foursoft.esupply.common.bean.ESupplyGlobalParameters param1, int param2, com.foursoft.etrans.common.bean.Address param3);

	boolean deleteHORegistrationDetails(java.lang.String param0, com.foursoft.esupply.common.bean.ESupplyGlobalParameters param1, int param2);

	java.util.ArrayList getCompanyIds(java.lang.String param0, java.lang.String param1,String operation,String terminalId);

	java.util.ArrayList getCurrencyList2(java.lang.String param0, java.lang.String param1,String terminalId,String operation);

	java.util.ArrayList getCurrencyList(java.lang.String param0, java.lang.String param1);

	boolean updateCurrencyConversion(java.lang.String param0, java.lang.String[] param1, java.lang.String[] param2, java.lang.String[] param3,java.lang.String[] param4,java.lang.String param5);

	boolean addConversionFactor(java.lang.String param0, java.lang.String[] param1, java.lang.String[] param2, java.lang.String[] param3,java.lang.String[] param4,java.lang.String param5);

	java.lang.String[][] getConversionFactor(java.lang.String param0, java.lang.String[] param1, java.lang.String param2);

	java.util.Vector getCFactorViewAll(java.lang.String param0, java.lang.String param1);

	java.lang.String[][] getConversionFactor1(java.lang.String[] param0, java.lang.String[] param1, java.lang.String param2);

	java.lang.String checkValidOrNot1(java.lang.String[] param0);

	java.util.Vector getCurrencyView(java.lang.String param0,String terminalId,String operation);

	java.util.Vector getSelectedCurrency(java.lang.String param0, java.lang.String param1,String terminalId,String operation);

	java.util.Vector getModifiedCurrencyList(java.lang.String param0, java.lang.String param1,String terminalId,String operation);

	java.util.Vector getCurrencyList1(java.lang.String param0);

	java.lang.String getUserTerminalType() throws java.sql.SQLException;

	java.lang.String[] getCompanyIds();

	boolean checkHOTerminal() throws java.sql.SQLException;

	java.util.Vector getLocationInfo(java.lang.String param0,java.lang.String param1,java.lang.String param2);

	boolean isExists(java.lang.String param0) throws java.sql.SQLException;

	java.lang.String setTerminalRegDetails(com.foursoft.etrans.setup.terminal.bean.TerminalRegJspBean param0, com.foursoft.etrans.common.bean.Address param1, com.foursoft.esupply.common.bean.ESupplyGlobalParameters param2);

	java.util.Vector getOperationTerminalInfo(java.lang.String param0, com.foursoft.esupply.common.bean.ESupplyGlobalParameters param1, java.lang.String param2, java.lang.String param3);
//loginbean is added by VLAKSHMI for  issue 173655 on 20090629
/*	com.foursoft.etrans.setup.terminal.bean.TerminalRegJspBean getTerminalRegDetails(java.lang.String param0, java.lang.String param1);*/
com.foursoft.etrans.setup.terminal.bean.TerminalRegJspBean getTerminalRegDetails(java.lang.String param0, java.lang.String param1,com.foursoft.esupply.common.bean.ESupplyGlobalParameters loginbean);

	boolean updateTerminalReg(com.foursoft.etrans.setup.terminal.bean.TerminalRegJspBean param0, com.foursoft.etrans.common.bean.Address param1, java.lang.String param2, com.foursoft.esupply.common.bean.ESupplyGlobalParameters param3);


	boolean deleteTerminalReg(java.lang.String param0, int param1, java.lang.String param2, java.lang.String param3, com.foursoft.esupply.common.bean.ESupplyGlobalParameters param4);

	boolean checkGatewayId(java.lang.String param0);

	int selectId() ;

	void insGatewayDB(java.lang.String param0, java.lang.String param1, java.lang.String param2, java.lang.String param3, java.lang.String param4, int param5, java.lang.String param6, java.lang.String param7, com.foursoft.etrans.setup.gateway.bean.GatewayJSPBean param8, com.foursoft.etrans.common.bean.Address param9, java.lang.String[] param10, com.foursoft.esupply.common.bean.ESupplyGlobalParameters param11, int param12) ;

	java.lang.String[] loadGatewayId() ;

	com.foursoft.etrans.setup.gateway.bean.GatewayJSPBean viewGatewayDB(java.lang.String param0) ;

	java.lang.String[] getTerminalIds(java.lang.String param0,String param1) ;
			
	//java.lang.String[] loadLocationName();

	boolean removeGateway(java.lang.String param0, com.foursoft.esupply.common.bean.ESupplyGlobalParameters param1) ;

	boolean updateGatewayDB(java.lang.String param0, java.lang.String param1, java.lang.String param2, java.lang.String param3, java.lang.String param4, java.lang.String param5, com.foursoft.etrans.setup.gateway.bean.GatewayJSPBean param6, com.foursoft.etrans.common.bean.Address param7, java.lang.String[] param8, com.foursoft.esupply.common.bean.ESupplyGlobalParameters param9) ;

	java.util.ArrayList getGatewayIds(java.lang.String param0, java.lang.String param1, java.lang.String param2, java.lang.String param3) ;

	java.lang.String[][] getCustomsMaster(java.lang.String param0, java.lang.String param1, int param2) ;

	java.lang.String[][] getGWCarrierContracts(java.lang.String param0, java.lang.String param1, int param2) ;

	java.lang.String[][] getCustomerContracts(java.lang.String param0, java.lang.String param1, int param2)	;

	java.lang.String[][] getCustomerRegistration(java.lang.String param0, java.lang.String param1, int param2)	;

	java.lang.String[][] getGatewayDetails(java.lang.String param0, int param1) ;

	boolean isCountryMasterCountryAlreadyExists(java.lang.String param0) ;

	void addCountryMasterDetails(com.foursoft.etrans.setup.country.bean.CountryMaster param0, com.foursoft.esupply.common.bean.ESupplyGlobalParameters param1) ;

	boolean updateCountryMasterDetails(com.foursoft.etrans.setup.country.bean.CountryMaster param0, com.foursoft.esupply.common.bean.ESupplyGlobalParameters param1) ;

	boolean deleteCountryMasterDetails(java.lang.String param0, com.foursoft.esupply.common.bean.ESupplyGlobalParameters param1) ;

	com.foursoft.etrans.setup.country.bean.CountryMaster getCountryMasterDetails(java.lang.String param0) ;

    java.lang.String[][] getTerminalMaster(java.lang.String param0, int param1, java.lang.String param2);
    
    public  java.lang.String getHouseDocId(java.lang.String param0, java.lang.String param1, java.lang.String param2, java.lang.String param3);

    com.foursoft.etrans.setup.company.bean.HORegistrationJspBean getHORegistrationDetails(java.lang.String param0);	

    com.foursoft.etrans.setup.servicelevel.bean.ServiceLevelJspBean getServiceLevelDetails(java.lang.String param0);
    
	public java.util.ArrayList getInvalidateCountryDetails();
	 public boolean invalidateCountry(ArrayList dobList);
	public boolean invalidateLocation(ArrayList dobList);
	public java.util.ArrayList getLocationDetails();
	public java.util.ArrayList getInvalidatePortMasterDetails();
	public boolean invalidatePortMaster(ArrayList dobList);
	public boolean invalidateCustomerMaster(ArrayList dobList);
	public java.util.ArrayList getInvalidateCustomerMasterDetails();
	public java.util.ArrayList getInvalidateCarrierDetails();
	public boolean invalidateCarrierMaster(ArrayList dobList);
//	public java.util.ArrayList getInvalidateCommodityDetails();
  public java.util.ArrayList getInvalidateCommodityDetails(int param0,int param1,String param2,String param3);
	public boolean invalidateCommodityMaster(ArrayList dobList);
	public boolean invalidateTerminalMaster(ArrayList dobList);
	public java.util.ArrayList getInvalidateTerminalDetails();
	public java.util.ArrayList getInvalidateServiceLevelDetails();
	public boolean invalidateServiceLevelMaster(ArrayList dobList);
  public	java.util.HashMap uploadCountryMasterDetails( java.util.ArrayList	countryList,boolean addModFlag) throws java.sql.SQLException;

  public	java.util.HashMap uploadLocationMasterDetails( ArrayList	locationList,boolean addModFlag) throws java.sql.SQLException;
    
  public	java.util.HashMap uploadPortMasterDetails( ArrayList	portList,boolean addModFlag) throws java.sql.SQLException;

  public	java.util.HashMap uploadCommodityMasterDetails( ArrayList	commodityList,boolean addModFlag) throws java.sql.SQLException;
    public ArrayList getTerminalIdsforThirdStation(String locationId,String searchString)  ;  

     public ArrayList insertListMasterDetails(ArrayList dataList);
     public ArrayList getListTypeIds(String searchString,String shipmentMode,String operation,String terminalId);
     public ListMasterDOB getListMasterDetails(String shipmentType,String listType) throws javax.ejb.ObjectNotFoundException;
      public boolean updateListMasterDetails(ListMasterDOB listMasterDOB)throws ObjectNotFoundException;
      public boolean deleteListMasterDtls(ListMasterDOB listMasterDOB)throws ObjectNotFoundException;
      
 public java.util.ArrayList getListDetails() ;
     public boolean invalidateListMaster(ArrayList dobList) ;
     public ArrayList getLocIds(String searchString,String searchString2,String terminalId,String operation,String shipmode);
      public ArrayList getCountryIds1(String searchString,String locationId,String shipmentMode);
      
      
      public HashMap addContentDiscriptionDtls(ArrayList contentDataList);
   
   public boolean modifyContentDescription(QMSContentDOB contentDOB);
   
   public boolean deleteContentDetails(String contentId);
   
   public QMSContentDOB getContentDetails(String contentId,String terminalId);
  
   public ArrayList getContentIds(String shipmentMode,String searchString,String operation,String terminalId);
   
   public void invalidateChargeBasisId(ArrayList chargeList);
   
   public void invalidateChargeMasterId(ArrayList chargeList);
   
   
   
   public ArrayList getChargeBasisDetails() ;
   
   public ArrayList getChargeMasterDetails() ;

    public void invalidateChargeGroupId(ArrayList chargeList);
    
     public ArrayList getChargeGroupMasterDetails(String operation,String terminalId ,String param1);
     //@@ Commented & Added by subrahmanyam for the pbn id: 203873 on 26-APR-10     
     //public ArrayList getAllContentDetails(String operation,String terminalId);
     public ArrayList getAllContentDetails(String operation,String terminalId,String loginAccessType);
   
   public void invalidateContentDtls(ArrayList chargeList) throws java.sql.SQLException;
 

}