package com.foursoft.esupply.common.bean;
/**
 * Copyright (c) 2000-2001 Four-Soft Pvt Ltd,
 * 5Q1A3, Hi-Tech City, Madhapur, Hyderabad-33, India.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of  Four-Soft Pvt Ltd,
 * ("Confidential Information").  You shall not disclose such Confidential Information
 * and shall use it only in accordance with the terms of the license agreement you entered
 * into with Four-Soft. For more information on the Four Soft Pvt Ltd
 */

/**
 * File					: UniqueIDList.java
 *
 * @author				: Ramesh Kumar.P
 * @date				: 10-05-2003(DD/MM/YYYY)
 */
import java.util.HashMap;

public final class UniqueIDList
{
	public static HashMap uniqueIdList = new HashMap(); 
	
	public static String getValue(String key)
	{
		return (String) uniqueIdList.get(key);
	}
	
	static
	{

//*************************************ACCONTROL**************************************
		uniqueIdList.put("ESACRoleRegistrationAdd","QS0010111");
		uniqueIdList.put("ESACRolePermissionsadd", "QS0010112");
		uniqueIdList.put("ESACRolePermissionsConfirmadd", "QS0010113");

		uniqueIdList.put("ESACUserRoleEnterIdMODIFYRole", "QS0010121");
		uniqueIdList.put("ESACUserRoleEnterIdModifyRole", "QS0010121");
		uniqueIdList.put("ESACRolePermissionsMODIFY","QS0010122");
		uniqueIdList.put("ESACRolePermissionsModify","QS0010122");
		uniqueIdList.put("ESACRolePermissionsConfirmMODIFY","QS0010123");
		
		uniqueIdList.put("ESACUserRoleEnterIdVIEWRole", "QS0010131");
		uniqueIdList.put("ESACRolePermissionsViewVIEW","QS0010132");
		uniqueIdList.put("ESACRolePermissionsConfirmVIEW","QS0010133");

		uniqueIdList.put("ESACUserRoleEnterIdDELETERole", "QS0010141");		
		uniqueIdList.put("ESACUserRoleEnterIdDeleteRole", "QS0010141");
		uniqueIdList.put("ESACRolePermissionsViewDELETE","QS0010142");

		uniqueIdList.put("ESACRolePermissionsConfirmDELETE","QS0010143");
		
		uniqueIdList.put("ESACRoleViewAllVIEWALL","QS0010151");


		uniqueIdList.put("ESACUserRegistrationEntryAdd", "QS0010211");
		uniqueIdList.put("ESACUserRegistrationAddAdd","QS0010212");
		//uniqueIdList.put("ESACRolePermissionsConfirmMODIFY","QS0010213");

		uniqueIdList.put("ESACUserRoleEnterIdMODIFYUser", "QS0010221");
        uniqueIdList.put("ESACUserRoleEnterIdModifyUser", "QS0010221");
		uniqueIdList.put("ESACUserRegistrationAddModify","QS0010222");
		//uniqueIdList.put("ESACRolePermissionsConfirmMODIFY","QS0010223");

		uniqueIdList.put("ESACUserRoleEnterIdVIEWUser", "QS0010231");
		uniqueIdList.put("ESACUserRegistrationAddView","QS0010232");
		//uniqueIdList.put("ESACRolePermissionsConfirmVIEW","QS0010233");

		uniqueIdList.put("ESACUsersViewAllViewAll","QS0010241");
		
		uniqueIdList.put("ESACUserRoleEnterIdDELETEUser", "QS0010251");
		uniqueIdList.put("ESACUserRoleEnterIdDeleteUser", "QS0010251");
		uniqueIdList.put("ESACUserRegistrationAddDelete", "QS0010252");

		uniqueIdList.put("ESACLoggedUsers", "QS0010261");

		uniqueIdList.put("PasswordReset", "QS0010271");
		uniqueIdList.put("PasswordResetProcess", "QS0010272");
		uniqueIdList.put("ESACPasswordChangeView", "QS0010273");
		uniqueIdList.put("ResetPassword", "QS0010274");
		uniqueIdList.put("PasswordProcess", "QS0010275");
		
		uniqueIdList.put("SecuritySettings", "QS0010281");
		uniqueIdList.put("SecuritySettingsUpdate", "QS0010282");

		uniqueIdList.put("ESLoggingEnterIdView", "QS0010291");
		uniqueIdList.put("ESLoggingReportView", "QS0010292");

		uniqueIdList.put("ESLoggingEnterIdViewAll", "QS00102A1");
		uniqueIdList.put("ESLoggingActivityViewAllReport", "QS00102A2");

	//*************************************ETRANS*****************************************
		///For CountryIds
		uniqueIdList.put("ETCCountryAddAdd","QS1000111");
		uniqueIdList.put("ETCCountryEnterIdModify", "QS1000121");
		uniqueIdList.put("ETCCountryViewModify", "QS1000122");
		uniqueIdList.put("ETCCountryEnterIdView", "QS1000131");
		uniqueIdList.put("ETCCountryViewView", "QS1000132");
		uniqueIdList.put("ETAShowViewAllReportcountrymaster", "QS1000141");
		uniqueIdList.put("ETASuperUserViewAllReportcountrymaster", "QS1000142");
		uniqueIdList.put("ETCCountryEnterIdDelete", "QS1000151");
		uniqueIdList.put("ETCCountryViewDelete", "QS1000152");
    uniqueIdList.put("ETCUploadIndexUploadCountry", "QS1000153");
		///For Company Ids
		uniqueIdList.put("ETCHOCompanyRegistrationAddAdd", "QS1000211");
		uniqueIdList.put("ETCHOCompanyRegistrationEnterIdModify", "QS1000221");
		uniqueIdList.put("ETCHOCompanyRegistrationViewModify", "QS1000222");
		uniqueIdList.put("ETCHOCompanyRegistrationEnterIdView", "QS1000231");
		uniqueIdList.put("ETCHOCompanyRegistrationViewView", "QS1000232");
		uniqueIdList.put("ETAShowViewAllReportcompanyregistration", "QS1000241");
		uniqueIdList.put("ETASuperUserViewAllReportcompanyregistration", "QS1000242");
		uniqueIdList.put("ETCHOCompanyRegistrationEnterIdDelete", "QS1000251");
		uniqueIdList.put("ETCHOCompanyRegistrationViewDelete", "QS1000252");
		///For Location Ids
		uniqueIdList.put("ETLocationAddAdd", "QS1000311");
		uniqueIdList.put("ETLocationEnterIdModify", "QS1000321");
		uniqueIdList.put("ETLocationViewModify", "QS1000322");
		uniqueIdList.put("ETLocationEnterIdView", "QS1000331");
		uniqueIdList.put("ETLocationViewView", "QS1000332");
		uniqueIdList.put("ETAShowViewAllReportlocationmaster", "QS1000341");
		uniqueIdList.put("ETASuperUserViewAllReportlocationmaster", "QS1000342");
		uniqueIdList.put("ETLocationEnterIdDelete", "QS1000351");
		uniqueIdList.put("ETLocationViewDelete", "QS1000352");
    uniqueIdList.put("ETCUploadIndexUploadLocation", "QS1000353");
		///For Commodity Ids
		uniqueIdList.put("ETCCommodityAddAdd", "QS1000411");
		uniqueIdList.put("ETCCommodityEnterIdModify", "QS1000421");
		uniqueIdList.put("ETCCommodityViewModify", "QS1000422");
		uniqueIdList.put("ETCCommodityEnterIdView", "QS1000431");
		uniqueIdList.put("ETCCommodityViewView", "QS1000432");
		uniqueIdList.put("ETAShowViewAllReportcommoditymaster", "QS1000441");
		uniqueIdList.put("ETASuperUserViewAllReportcommoditymaster", "QS1000442");
		uniqueIdList.put("ETCCommodityEnterIdDelete", "QS1000451");
		uniqueIdList.put("ETCCommodityViewDelete", "QS1000452");
    uniqueIdList.put("ETCUploadIndexUploadCommodity", "QS1000453");
		///For Currency Ids
		uniqueIdList.put("ETCCurrencyConversionAddAdd", "QS1000611");
		uniqueIdList.put("ETCCurrencyConversionAddProcessAdd", "QS1000612");
		uniqueIdList.put("ETCCurrencyConversionAddModify", "QS1000621");
		uniqueIdList.put("ETCCurrencyConversionAddProcessModify", "QS1000622");
		uniqueIdList.put("ETCCurrencyConversionViewView", "QS1000631");
		uniqueIdList.put("ETCCurrencyConversionViewProcessView", "QS1000632");
		uniqueIdList.put("ETCCurrencyConversionViewAllSelectForAllCurrencies", "QS1000641");/*@@Modified by Anusha in DHL-4S-CR in Kewill Time & Expense*/
		uniqueIdList.put("ETCCurrencyConversionViewAllProcessALL", "QS1000642");
		///For Tax Ids at Ho_Terminal
		uniqueIdList.put("ETCTaxMasterAddAddHO_TERMINAL", "QS1040211");
		uniqueIdList.put("ETCTaxMasterEnterIdModifyHO_TERMINAL", "QS1040221");
		uniqueIdList.put("ETCTaxMasterAddModifyHO_TERMINAL", "QS1040222");
		uniqueIdList.put("ETCTaxMasterEnterIdViewHO_TERMINAL", "QS1040231");
		uniqueIdList.put("ETCTaxMasterAddViewHO_TERMINAL", "QS1040232");
		uniqueIdList.put("ETAShowViewAllReporttaxmaster", "QS1040241");
		uniqueIdList.put("ETASuperUserViewAllReporttaxmaster", "QS1040242");
		uniqueIdList.put("ETCTaxMasterEnterIdDeleteHO_TERMINAL", "QS1040251");
		uniqueIdList.put("ETCTaxMasterAddDeleteHO_TERMINAL", "QS1040252");
		///For Port Ids
		uniqueIdList.put("ETSPortMasterAddAdd", "QS1020911");
		uniqueIdList.put("ETSPortMasterEnterIdModify", "QS1020921");
		uniqueIdList.put("ETSPortMasterAddModify", "QS1020922");
		uniqueIdList.put("ETSPortMasterEnterIdView", "QS1020931");
		uniqueIdList.put("ETSPortMasterAddView", "QS1020932");
		uniqueIdList.put("ETSPortMasterEnterIdDelete", "QS1020941");
		uniqueIdList.put("ETSPortMasterAddDelete", "QS1020942");
    uniqueIdList.put("ETCUploadIndexUploadPort", "QS1020943");
		///For CodeCustomisation Ids
		uniqueIdList.put("ETCCodeCustomisationAddAdd", "QS1000811");
		uniqueIdList.put("ETCCodeCustomisationEnterIdModify", "QS1000821");
		uniqueIdList.put("ETCCodeCustomisationViewModify", "QS1000822");
		uniqueIdList.put("ETCCodeCustomisationEnterIdView", "QS1000831");
		uniqueIdList.put("ETCCodeCustomisationViewView", "QS1000832");
		///For Terminal Ids
		uniqueIdList.put("ETCOperationTerminalRegistrationEnterIdAdd", "QS1010111");
		uniqueIdList.put("ETCTerminalRegistrationAddAdd", "QS1010112");
		uniqueIdList.put("ETCTerminalRegistrationEnterIdModify", "QS1010121");
		uniqueIdList.put("ETCTerminalRegistrationViewModify", "QS1010122");
		uniqueIdList.put("ETCTerminalRegistrationEnterIdView", "QS1010131");
		uniqueIdList.put("ETCTerminalRegistrationViewView", "QS1010132");
		uniqueIdList.put("ETAViewAllAdminViewTerminal", "QS1010141");
		uniqueIdList.put("ETATerminalMasterReportsViewTerminal", "QS1010142");
		uniqueIdList.put("ETCTerminalRegistrationEnterIdDelete", "QS1010151");
		uniqueIdList.put("ETCTerminalRegistrationViewDelete", "QS1010152");
		
		///For ServiceLevel Ids
		uniqueIdList.put("ETCServiceLevelAddAdd", "QS1000511");
		uniqueIdList.put("ETCServiceLevelEnterIdModify", "QS1000521");
		uniqueIdList.put("ETCServiceLevelViewModify", "QS1000522");
		uniqueIdList.put("ETCServiceLevelEnterIdView", "QS1000531");
		uniqueIdList.put("ETCServiceLevelViewView", "QS1000532");
		uniqueIdList.put("ETAShowViewAllReportservicelevel", "QS1000541");
		uniqueIdList.put("ETASuperUserViewAllReportservicelevel", "QS1000542");
		uniqueIdList.put("ETCServiceLevelEnterIdDelete", "QS1000551");
		uniqueIdList.put("ETCServiceLevelViewDelete", "QS1000552");
		///For Customs Ids
		uniqueIdList.put("ETCCustomsRegistrationAddAdd", "QS1020711");
		uniqueIdList.put("ETCCustomsRegistrationEnterIdModify", "QS1020721");
		uniqueIdList.put("ETCCustomsRegistrationViewModify", "QS1020722");
		uniqueIdList.put("ETCCustomsRegistrationEnterIdView", "QS1020731");
		uniqueIdList.put("ETCCustomsRegistrationViewView", "QS1020732");
		uniqueIdList.put("ETAShowViewAllReportcustomsmaster", "QS1020741");
		uniqueIdList.put("ETAGatewayTerminalViewAllReportcustomsmaster", "QS1020742");
		uniqueIdList.put("ETCCustomsRegistrationEnterIdDelete", "QS1020751");
		uniqueIdList.put("ETCCustomsRegistrationViewDelete", "QS1020752");
		
		
		
		///For DiscrepancyReason Ids
		uniqueIdList.put("ETDiscrepancyReasonShowAdd", "QS1700211");
		uniqueIdList.put("ETDiscrepancyReasonEnterIdModify", "QS1700221");
		uniqueIdList.put("ETDiscrepancyReasonShowModify", "QS1700222");
		uniqueIdList.put("ETDiscrepancyReasonEnterIdView", "QS1700231");
		uniqueIdList.put("ETDiscrepancyReasonShowView", "QS1700232");
		uniqueIdList.put("ETDiscrepancyReasonEnterIdDelete", "QS1700241");
		uniqueIdList.put("ETDiscrepancyReasonShowDelete", "QS1700242");
		///For CorporateCustomer Ids
		uniqueIdList.put("ETCustomerRegistrationAddAdd", "QS1040111");
		uniqueIdList.put("ETCustomerRegistrationEnterIdCModify", "QS1040121");
		uniqueIdList.put("ETCustomerRegistrationAddModify", "QS1040122");
		uniqueIdList.put("ETCustomerRegistrationEnterIdCView", "QS1040131");
		uniqueIdList.put("ETCustomerRegistrationAddView", "QS1040132");
		uniqueIdList.put("ETAViewAllAdminCustomerRegistration", "QS1040141");
		uniqueIdList.put("ETACustomerRegistrationReportsCustomerRegistration", "QS1040142");
		uniqueIdList.put("ETCustomerRegistrationEnterIdCDelete", "QS1040151");
		uniqueIdList.put("ETCustomerRegistrationAddDelete", "QS1040152");
		///For CustomerContract Ids
		uniqueIdList.put("ETCustomercontractAddAddYes", "QS1020211");
		uniqueIdList.put("ETCustomercontractLaneAddYes", "QS1020212");
		uniqueIdList.put("ETCustomercontractChargesAddYes", "QS1020213");
		uniqueIdList.put("ETCustomerContractEnterIdModifyYes", "QS1020221");
		uniqueIdList.put("ETCustomercontractAddModifyYes", "QS1020222");
		uniqueIdList.put("ETCustomercontractLaneModifyYes", "QS1020223");
		uniqueIdList.put("ETCustomercontractChargesModifyYes", "QS1020224");
		uniqueIdList.put("ETCustomerContractEnterIdViewYes", "QS1020231");
		uniqueIdList.put("ETCustomercontractViewViewYes", "QS1020232");
		uniqueIdList.put("ETCustomerContractEnterIdDeleteYes", "QS1020241");
		uniqueIdList.put("ETCustomercontractViewDeleteYes", "QS1020242");
		///For Carrier Ids
		uniqueIdList.put("ETCCarrierRegistrationAddAdd", "QS1020311");
		uniqueIdList.put("ETCCarrierRegistrationEnterIdModify", "QS1020321");
		uniqueIdList.put("ETCCarrierRegistrationViewModify", "QS1020322");
		uniqueIdList.put("ETCCarrierRegistrationEnterIdView", "QS1020331");
		uniqueIdList.put("ETCCarrierRegistrationViewView", "QS1020332");
		uniqueIdList.put("ETAViewAllAdminCarrierRegistration", "QS1020341");
		uniqueIdList.put("ETACarrierRegistrationReportsCarrierRegistration", "QS1020342");
		uniqueIdList.put("ETCCarrierRegistrationEnterIdDelete", "QS1020351");
		uniqueIdList.put("ETCCarrierRegistrationViewDelete", "QS1020352");
		
		///For CarrierSchedule Ids
	/*	uniqueIdList.put("ETCarrierSchedulesAddAdd", "QS1020511");
		uniqueIdList.put("ETCarrierSchedulesEnterIdModify", "QS1020521");
		uniqueIdList.put("ETCarrierSchedulesViewModify", "QS1020522");
		uniqueIdList.put("ETCarrierSchedulesEnterIdView", "QS1020531");
		uniqueIdList.put("ETCarrierSchedulesViewView", "QS1020532");
		uniqueIdList.put("ETCarrierSchedulesEnterIdDelete", "QS1020541");
		uniqueIdList.put("ETCarrierSchedulesViewDelete", "QS1020542");
    */
		///For Report Print Formats
		uniqueIdList.put("ETransPrintFormatSelectionAir", "QS1020611");
		///For Terminal Information Ids
		uniqueIdList.put("ETCTerminalInformationViewModify", "QS1030111");
		uniqueIdList.put("ETCTerminalInformationViewView", "QS1030121");
		///For Terminal Customer Ids
		uniqueIdList.put("ETCustomerRegistrationAddMoreAdressAdd", "QS1030211");
		uniqueIdList.put("ETCustomerRegistrationEnterIdModify", "QS1030221");
		uniqueIdList.put("ETCustomerRegistrationAddMoreAdressModify", "QS1030222");
		uniqueIdList.put("ETCustomerRegistrationEnterIdUpgrade", "QS1030231");
		uniqueIdList.put("ETCustomerRegistrationAddMoreAdressUpgrade", "QS1030232");
		uniqueIdList.put("ETShipperConsigneeMappingshippers", "QS1030241");
		uniqueIdList.put("ETCustomerRegistrationEnterIdView", "QS1030251");
		uniqueIdList.put("ETCustomerRegistrationAddMoreAdressView", "QS1030252");
		uniqueIdList.put("ETCustomerRegistrationEnterIdDelete", "QS1030271");
		uniqueIdList.put("ETCustomerRegistrationAddMoreAdressDelete", "QS1030272");
		///For Terminal CustomerContracts
		uniqueIdList.put("ETCustomercontractAddAddNo", "QS1030311");
		uniqueIdList.put("ETCustomercontractLaneAddNo", "QS1030312");
		uniqueIdList.put("ETCustomercontractChargesAddNo", "QS1030313");
		uniqueIdList.put("ETCustomerContractEnterIdModifyNo", "QS1030321");
		uniqueIdList.put("ETCustomercontractAddModifyNo", "QS1030322");
		uniqueIdList.put("ETCustomercontractLaneModifyNo", "QS1030323");
		uniqueIdList.put("ETCustomercontractChargesModifyNo", "QS1030324");
		uniqueIdList.put("ETCustomerContractEnterIdViewNo", "QS1030331");
		uniqueIdList.put("ETCustomercontractViewViewNo", "QS1030332");
		uniqueIdList.put("ETCustomerContractEnterIdDeleteNo", "QS1030341");
		uniqueIdList.put("ETCustomercontractViewDeleteNo", "QS1030342");
	
		///For TaxIds Terminal Level
		uniqueIdList.put("ETCTaxMasterAddAddOPER_TERMINAL", "QS1030411");
		uniqueIdList.put("ETCTaxMasterEnterIdModifyOPER_TERMINAL", "QS1030421");
		uniqueIdList.put("ETCTaxMasterAddModifyOPER_TERMINAL", "QS1030422");
		uniqueIdList.put("ETCTaxMasterEnterIdViewOPER_TERMINAL", "QS1030431");
		uniqueIdList.put("ETCTaxMasterAddViewOPER_TERMINAL", "QS1030432");
		uniqueIdList.put("ETAShowViewAllReporttaxmasterOPER_TERMINAL", "QS1030441");
		uniqueIdList.put("ETASuperUserViewAllReporttaxmasterOPER_TERMINAL", "QS1030442");
		uniqueIdList.put("ETCTaxMasterEnterIdDeleteOPER_TERMINAL", "QS1030451");
		uniqueIdList.put("ETCTaxMasterAddDeleteOPER_TERMINAL", "QS1030452");
	
	
	
		///For Truck Route Scheduling
		uniqueIdList.put("ETTRoutMasterAddAdd", "SP1740111");
		uniqueIdList.put("ETTRoutMasterEnterIdModify", "SP1740121");
		uniqueIdList.put("ETTRoutMasterAddModify", "SP1740122");
		uniqueIdList.put("ETTRoutMasterEnterIdView", "SP1740131");
		uniqueIdList.put("ETTRoutMasterAddView", "SP1740132");
    
    
    //@@Added by Kameswari for the WPBN issue-61295
    uniqueIdList.put("QMSEmailTextMasterAdd","QS1020511");
    uniqueIdList.put("QMSEmailTextMasterModify","QS1020512");
    uniqueIdList.put("QMSEmailTextMasterView","QS1020513");
    uniqueIdList.put("QMSEmailTextMasterDelete","QS1020514");
    uniqueIdList.put("QMSEmailTextMasterEnterIdModify","QS1020521");
    uniqueIdList.put("QMSEmailTextMasterEnterIdView","QS1020531");
    uniqueIdList.put("QMSEmailTextMasterEnterIdDelete","QS1020541");
	  //@@WPBN issue-61295
  
    //@@Added by Kameswari for the WPBN issue-61289
    uniqueIdList.put("QMSAttachmentEnterIdAdd","QS1020611");
    uniqueIdList.put("QMSAttachmentEnterIdModify","QS1020621");
    uniqueIdList.put("QMSAttachmentEnterIdView","QS1020631");
    uniqueIdList.put("QMSAttachmentEnterIdDelete","QS1020641");
    //uniqueIdList.put("QMSAttachmentEnterIdViewAll","QS1020651");
    //uniqueIdList.put("QMSAttachmentEnterIdInvalidate","QS1020661");
    uniqueIdList.put("QMSAttachmentMasterAdd","QS1020612");
    uniqueIdList.put("QMSAttachmentMasterViewModify","QS1020622");
    uniqueIdList.put("QMSAttachmentMasterViewView","QS1020632");
    uniqueIdList.put("QMSAttachmentMasterViewDelete","QS1020642");
    uniqueIdList.put("QMSAttachmentMasterViewViewDetails","QS1020633");
    uniqueIdList.put("QMSAttachmentViewAllViewAll","QS1020651");
    uniqueIdList.put("QMSAttachmentViewAllInvalidate","QS1020661");
    uniqueIdList.put("QMSAttachmentViewAllDtlViewAll","QS1020652");
    uniqueIdList.put("QMSAttachmentViewAllDtlInvalidate","QS1020662");
    uniqueIdList.put("QMSAttachments","QS1020623");
    //WPBN issue-61289
  }
}
