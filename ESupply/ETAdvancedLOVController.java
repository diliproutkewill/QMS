/* 
*
* Copyright(c) 1999-2001 by FourSoft,Pvt Ltd.All Rights Reserved.
* This Software is the proprietary information of FourSoft,Pvt Ltd.
* Use is subject to license terms
*
*
* esupply-V 1.x
*
		File					:	ETAdvancedLOVController.java
		Sub-module name			:	Advanced LOV
		Module name				:	ETrans
		Purpose of the class	:	It presents The GUI it will redirect to different JSPs
		Author					:	Ravi Kumar G.
		Date					:	15-Mar-2005
		Modified history		:
*
*/
 
import java.util.ArrayList;
import java.util.Date;
import java.io.IOException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import com.foursoft.esupply.common.bean.ESupplyGlobalParameters;
import com.foursoft.esupply.common.java.ErrorMessage;
import com.foursoft.esupply.common.java.KeyValue;
//import com.foursoft.esupply.common.util.Logger;
import org.apache.log4j.Logger;
import com.foursoft.esupply.common.dao.ETAdvancedLOVMasterDAO;
import com.foursoft.esupply.common.java.ETAdvancedLOVMasterVO;
import com.foursoft.esupply.common.java.ETCarrierContractAdvVO;
import com.foursoft.esupply.common.java.ETHAWBAdvVO; 
import com.foursoft.esupply.common.java.ETHBLAdvVO;
import com.foursoft.esupply.common.java.ETMAWBAdvVO;
import com.foursoft.esupply.common.java.ETConsoleAdvVO;
import com.foursoft.esupply.common.java.ETManifestAdvVO;
import com.foursoft.esupply.common.java.ETConsignNoteAdvVO;
//import com.foursoft.esupply.common.java.ETInvoiceAdvVO;
import com.foursoft.esupply.common.java.ETCustomerAdvVO;
import com.foursoft.esupply.common.java.ETPRQAdvVO;



public class ETAdvancedLOVController extends HttpServlet 
{
	public static String FILE_NAME = "ETAdvancedLOVController.java";
  private static Logger logger = null;

	public void init(ServletConfig config) throws ServletException
	{
    logger  = Logger.getLogger(ETAdvancedLOVController.class);
		super.init(config);
	}
	public void doGet(HttpServletRequest request,HttpServletResponse response)		  throws ServletException,IOException
	{		
		doPost(request,response);
	}
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{   
		//Logger.info(FILE_NAME,"[ETAdvancedLOVController.java] in do post");
    logger.info(FILE_NAME+"[ETAdvancedLOVController.java] in do post");
		java.util.Enumeration e = request.getParameterNames();
		while(e.hasMoreElements())
		{
			String str = (String)e.nextElement();
			//Logger.info(">>>>>>>>>>>>>>>>", "Param name is ["+str+"]","Value is <<"+request.getParameter(str) +">>");
		}
		ArrayList lovData = null;
		int errorCode ;
		errorCode = validateInitialRequest(request, response);
		//Logger.info(FILE_NAME,"[ETAdvancedLOVController.java] after validating request: error code is ["+errorCode+"]");

		if(errorCode<1)
		{
			displayError(request,response,errorCode);
		}
		else
		{
			lovData = getLOVData(request, response);      
		}

		request.setAttribute("LOVData",lovData);
			forwardToJSP(request,response);
	}
  
	public int validateInitialRequest(HttpServletRequest request, HttpServletResponse response)
	{
		if(request.getParameter("entity") == null)
			return -1;

		if(request.getParameter("mode") == null)
			return -2;

		if(request.getParameter("type") == null)
			return -3;

		if(request.getParameter("operation") == null)
			return -4;
		return 1;
	}

	public ArrayList getLOVData(HttpServletRequest request, HttpServletResponse response)
	{      
		ArrayList result = null;
		ETAdvancedLOVMasterVO  etaLovMVO     = null;

		if(request.getParameter("entity").equalsIgnoreCase("Console"))
		{
			etaLovMVO = getConsoleDetails(request, response);
		}
		else if(request.getParameter("entity").equalsIgnoreCase("CarrierContract"))
		{
			etaLovMVO = getCarConDetails(request, response);
		}
		else if(request.getParameter("entity").equalsIgnoreCase("HAWB"))
		{
			etaLovMVO = getHAWBDetails(request, response);
		} 
		else if(request.getParameter("entity").equalsIgnoreCase("HBL"))
		{
			etaLovMVO = getHBLDetails(request, response);
		}
		else if(request.getParameter("entity").equalsIgnoreCase("MAWB"))
		{
			etaLovMVO = getMAWBDetails(request, response);
		}
		else if(request.getParameter("entity").equalsIgnoreCase("Manifest"))
		{
			etaLovMVO = getManifestDetails(request, response);
		}    
		else if(request.getParameter("entity").equalsIgnoreCase("ConsignmentNote"))
		{
			etaLovMVO = getConsignNoteDetails(request, response);
		}		
/*
		else if(request.getParameter("entity").equalsIgnoreCase("Invoice"))
		{
			etaLovMVO = getInvoiceDetails(request, response);
		}   
*/ 
		else if(request.getParameter("entity").equalsIgnoreCase("Customer"))
		{
			etaLovMVO=getCustomerDetails(request, response);
		}
		else if(request.getParameter("entity").equalsIgnoreCase("PRQ"))
		{
			etaLovMVO = getPRQDetails(request, response);
		}
		else
		{
			//Logger.info(FILE_NAME,"----------------> Invalid Entity Passed");
      logger.info(FILE_NAME+"----------------> Invalid Entity Passed");
		}
		return ETAdvancedLOVMasterDAO.getData(etaLovMVO);
	}
	public void displayError(HttpServletRequest request, HttpServletResponse response,int errorCode)
	{
/*
		ErrorMessage errorMessage = null;
		ArrayList keyValueList = new ArrayList();	
		errorMessage = new ErrorMessage("Error Message",""); 
		keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
		keyValueList.add(new KeyValue("Operation","")); 	
		errorMessage.setKeyValueList(keyValueList);
		request.setAttribute("ErrorMessage",errorMessage);
		try
		{
		doFileDispatch(request,response,"ESupplyErrorPage.jsp");
		}
		catch(Exception e)
		{
		}
*/
	}

	public String getNoOfDays()
	{
		return "15";
	}

	public ETCustomerAdvVO getCustomerDetails(HttpServletRequest request, HttpServletResponse response)
	{
		ETCustomerAdvVO customerVO			=	new ETCustomerAdvVO();
		HttpSession session					= 	request.getSession();
		ESupplyGlobalParameters loginBean	=	null;
		loginBean							= 	(ESupplyGlobalParameters)session.getAttribute("loginbean");

		if (request.getParameter("entity")!= null || request.getParameter("entity").equalsIgnoreCase("Customer"))
		{
			//Logger.info(FILE_NAME,"............CustomerID............"+request.getParameter("customerId"));
			customerVO.setCustomerID(request.getParameter("customerId")==null?"":request.getParameter("customerId"));
			customerVO.setCustomername(request.getParameter("name")==null?"":request.getParameter("name"));
			customerVO.setCity(request.getParameter("city")==null?"":request.getParameter("city"));
			customerVO.setTerminal(request.getParameter("terminal")==null?"":request.getParameter("terminal"));
			customerVO.setCountry(request.getParameter("country")==null?"":request.getParameter("country"));
			customerVO.setRegi(request.getParameter("CustomerType")==null?"":request.getParameter("CustomerType")); 
			customerVO.setEntity(request.getParameter("entity"));
			customerVO.setSearchType(request.getParameter("searchType")==null?"":request.getParameter("searchType"));       
      customerVO.setOperation(request.getParameter("operation")==null?"":request.getParameter("operation"));
			customerVO.setFormField(request.getParameter("formfield")==null?"":request.getParameter("formfield"));
			customerVO.setTerminalID(loginBean.getTerminalId());
			customerVO.setRegister(request.getParameter("register"));
			customerVO.setCustomer(request.getParameter("registrationLevel"));
			customerVO.setCorporate(request.getParameter("customerType"));
			customerVO.setCustomerregi(request.getParameter("module"));
			customerVO.setAIRPRQ(request.getParameter("prq")); 
			customerVO.setECustomer(request.getParameter("eaccount"));
			customerVO.setSEAPRQ(request.getParameter("seaprq"));
			customerVO.setINVOICEADD(request.getParameter("invoice"));
			customerVO.setMapping(request.getParameter("Mapping"));
			customerVO.setDAWB(request.getParameter("DAWB"));
			customerVO.setDirect(request.getParameter("DC"));
			customerVO.setTerminaldel(request.getParameter("terminaldel"));
      customerVO.setcontractadd(request.getParameter("contract"));
			customerVO.setAccessLevel(loginBean.getAccessType());//ADDED BY RK
			//Logger.info(FILE_NAME,"............terminalId......@@@@@@@@@@@@@@@@@......"+loginBean.getTerminalId());        
			request.setAttribute("searchDetails",customerVO); 
		}   
		return customerVO;        
	}

	public ETCarrierContractAdvVO getCarConDetails(HttpServletRequest request, HttpServletResponse response)
	{
		ETCarrierContractAdvVO carConVO = new ETCarrierContractAdvVO();

		if (request.getParameter("entity")!= null && request.getParameter("entity").equalsIgnoreCase("CarrierContract"))
		{
			//Logger.info(FILE_NAME,"............prqId............"+request.getParameter("prqId"));
			carConVO.setContractId(request.getParameter("contractId")==null?"":request.getParameter("contractId"));
			//Logger.info(FILE_NAME,"............shipper............"+request.getParameter("shipper"));
			carConVO.setCarrierId(request.getParameter("carrierId")==null?"":request.getParameter("carrierId"));
			carConVO.setOriginTerminalId(request.getParameter("originTerminalId")==null?"":request.getParameter("originTerminalId"));
			carConVO.setDestTerminalId(request.getParameter("destTerminalId")==null?"":request.getParameter("destTerminalId"));
			carConVO.setServiceLevelId(request.getParameter("serviceLevelId")==null?"":request.getParameter("serviceLevelId"));
			carConVO.setActiveStatus(request.getParameter("activeStatus")==null?"":request.getParameter("activeStatus"));
			carConVO.setValidUpto(request.getParameter("validUpto")==null?"":request.getParameter("validUpto"));
			carConVO.setEntity(request.getParameter("entity"));
			carConVO.setSearchType(request.getParameter("searchType")==null?"":request.getParameter("searchType"));
			carConVO.setNoOfDaysControl(request.getParameter("past")==null?"lessOrEqualTo":request.getParameter("past"));
			carConVO.setNoOfDays(request.getParameter("noOfDays")==null?getNoOfDays():request.getParameter("noOfDays"));
			carConVO.setTerminalID(request.getParameter("terminalId")==null?"":request.getParameter("terminalId"));
			//Logger.info(FILE_NAME,"..........getHAWBDetails:terminalId.........."+request.getAttribute("terminalId"));  
			request.setAttribute("searchDetails",carConVO);  
		}
		//Logger.info(FILE_NAME,"..........getHAWBDetails:searchDetails.........."+request.getAttribute("searchDetails"));
		//Logger.info(FILE_NAME,"..........getHAWBDetails:getNoOfDays........"+getNoOfDays());
		return carConVO;
	}
	public ETHAWBAdvVO getHAWBDetails(HttpServletRequest request, HttpServletResponse response)
	{

		ETHAWBAdvVO hawbVO = new ETHAWBAdvVO();
		//Logger.info(FILE_NAME,"............hawbId............"+request.getParameter("hawbId"));
		hawbVO.setHawbId(request.getParameter("hawbId")==null?"":request.getParameter("hawbId"));
		//Logger.info(FILE_NAME,"............shipper............"+request.getParameter("shipper"));
		hawbVO.setShipperName(request.getParameter("shipper")==null?"":request.getParameter("shipper"));
		hawbVO.setConsigneeName(request.getParameter("consignee")==null?"":request.getParameter("consignee"));
		hawbVO.setOriginTerminal(request.getParameter("origin")==null?"":request.getParameter("origin"));
		hawbVO.setDestinationTerminal(request.getParameter("destination")==null?"":request.getParameter("destination"));
		hawbVO.setOriginCountryId(request.getParameter("originCountryId")==null?"":request.getParameter("originCountryId"));
		hawbVO.setDestinationCountryId(request.getParameter("destCountryId")==null?"":request.getParameter("destCountryId"));
		hawbVO.setEntity(request.getParameter("entity"));
		//Logger.info(FILE_NAME,"............mode........@@@@@@@@@@@......"+request.getParameter("mode"));
		//Logger.info(FILE_NAME,"............operation...@@@@@@@@@@@......"+request.getParameter("operation"));
		//Logger.info(FILE_NAME,"............terminalId..@@@@@@@@@@@......"+request.getParameter("terminalId"));
		//Logger.info(FILE_NAME,"............terminalType.@@@@@@@@@@......"+request.getParameter("terminalType"));
		//Logger.info(FILE_NAME,"............operationType.@@@@@@@@@......"+request.getParameter("operationType"));
//		Logger.info(FILE_NAME,".........invokerOperation.@@@@@@@@@......"+request.getParameter("invokerOperation"));

////////////////////////@@ G.Srinivas Modified on 20050402 (LOV Advanced Search ) -->	
	String invokerOperation		=	"";
	HttpSession session = request.getSession();
	if(request.getParameter("invokerOperation")==null || "null".equals(request.getParameter("invokerOperation")) || "".equals(request.getParameter("invokerOperation")))
		{
			//Logger.info(FILE_NAME,"invokerOperation--@@@@@@@@@@@@@---if--get session -->"+(String)session.getAttribute("invokerOperation"));
			invokerOperation = (String)session.getAttribute("invokerOperation");  
		}
		else
		{
			//Logger.info(FILE_NAME,"invokerOperation----@@@@@@@@@@@@@@@@@@---if-else-  session set ->");
			invokerOperation=request.getParameter("invokerOperation");
			session.setAttribute("invokerOperation", invokerOperation); 
		}
		hawbVO.setInvokerOperation(invokerOperation);
//prqVO.setInvokerOperation(request.getParameter("invokerOperation")==null?"":request.getParameter("invokerOperation"));
//Logger.info(FILE_NAME,"invokerOperation----@@@@@@@@@@@@@@@@@@----->"+invokerOperation);
//Logger.info(FILE_NAME,"invokerOperation----from the session--->["+(String)session.getAttribute("invokerOperation")+"]");
////////////////////////@@ G.Srinivas Modified on 20050402 (LOV Advanced Search ) -->

		hawbVO.setMode(request.getParameter("mode")==null?"":request.getParameter("mode"));
		hawbVO.setOperation(request.getParameter("operation")==null?"":request.getParameter("operation"));
		hawbVO.setTerminalID(request.getParameter("terminalId")==null?"":request.getParameter("terminalId"));
		hawbVO.setTerminalType(request.getParameter("terminalType")==null?"":request.getParameter("terminalType"));
		hawbVO.setOperationType(request.getParameter("operationType")==null?"":request.getParameter("operationType"));
//		hawbVO.setInvokerOperation(request.getParameter("invokerOperation")==null?"":request.getParameter("invokerOperation"));
		hawbVO.setSearchType(request.getParameter("searchType")==null?"":request.getParameter("searchType"));
		hawbVO.setNoOfDaysControl(request.getParameter("past")==null?"lessOrEqualTo":request.getParameter("past"));
		hawbVO.setNoOfDays(request.getParameter("noOfDays")==null?"15":request.getParameter("noOfDays"));
		request.setAttribute("searchDetails",hawbVO);  
		return hawbVO;
	}

	public ETHBLAdvVO getHBLDetails(HttpServletRequest request, HttpServletResponse response)
	{
		ETHBLAdvVO hblVO = new ETHBLAdvVO();

		//Logger.info(FILE_NAME,"............hblId............"+request.getParameter("hblId"));
		hblVO.setHblId(request.getParameter("hblId")==null?"":request.getParameter("hblId"));
		//Logger.info(FILE_NAME,"............shipper............"+request.getParameter("shipper"));
		hblVO.setShipperName(request.getParameter("shipper")==null?"":request.getParameter("shipper"));
		hblVO.setConsigneeName(request.getParameter("consignee")==null?"":request.getParameter("consignee"));
		hblVO.setOriginTerminal(request.getParameter("origin")==null?"":request.getParameter("origin"));
		hblVO.setDestinationTerminal(request.getParameter("destination")==null?"":request.getParameter("destination"));
		hblVO.setOriginCountryId(request.getParameter("originCountryId")==null?"":request.getParameter("originCountryId"));
		hblVO.setDestinationCountryId(request.getParameter("destCountryId")==null?"":request.getParameter("destCountryId"));
		hblVO.setEntity(request.getParameter("entity"));
		//Logger.info(FILE_NAME,"............mode........@@@@@@@@@@@......"+request.getParameter("mode"));
		//Logger.info(FILE_NAME,"............operation...@@@@@@@@@@@......"+request.getParameter("operation"));
		//Logger.info(FILE_NAME,"............terminalId..@@@@@@@@@@@......"+request.getParameter("terminalId"));
		//Logger.info(FILE_NAME,"............terminalType.@@@@@@@@@@......"+request.getParameter("terminalType"));
		//Logger.info(FILE_NAME,"............operationType.@@@@@@@@@......"+request.getParameter("operationType"));
	//	Logger.info(FILE_NAME,".........invokerOperation.@@@@@@@@@......"+request.getParameter("invokerOperation"));
		//Logger.info(FILE_NAME,"............consoleType........@@@@@@@@@@@......"+request.getParameter("consoleType"));
			  
		////////////////////////@@ G.Srinivas Modified on 20050402 (LOV Advanced Search ) -->	
	String invokerOperation		=	"";
	HttpSession session = request.getSession();
	if(request.getParameter("invokerOperation")==null || "null".equals(request.getParameter("invokerOperation")) || "".equals(request.getParameter("invokerOperation")))
		{
			//Logger.info(FILE_NAME,"invokerOperation--@@@@@@@@@@@@@---if--get session -->"+(String)session.getAttribute("invokerOperation"));
			invokerOperation = (String)session.getAttribute("invokerOperation");  
		}
		else
		{
			//Logger.info(FILE_NAME,"invokerOperation----@@@@@@@@@@@@@@@@@@---if-else-  session set ->");
			invokerOperation=request.getParameter("invokerOperation");
			session.setAttribute("invokerOperation", invokerOperation); 
		}
		hblVO.setInvokerOperation(invokerOperation);
//prqVO.setInvokerOperation(request.getParameter("invokerOperation")==null?"":request.getParameter("invokerOperation"));
//Logger.info(FILE_NAME,"invokerOperation----@@@@@@@@@@@@@@@@@@----->"+invokerOperation);
//Logger.info(FILE_NAME,"invokerOperation----from the session--->["+(String)session.getAttribute("invokerOperation")+"]");
////////////////////////@@ G.Srinivas Modified on 20050402 (LOV Advanced Search ) -->

			  
		hblVO.setConsoleType(request.getParameter("consoleType")==null?"":request.getParameter("consoleType"));
		hblVO.setMode(request.getParameter("mode")==null?"":request.getParameter("mode"));
		hblVO.setOperation(request.getParameter("operation")==null?"":request.getParameter("operation"));
		hblVO.setTerminalID(request.getParameter("terminalId")==null?"":request.getParameter("terminalId"));
		hblVO.setTerminalType(request.getParameter("terminalType")==null?"":request.getParameter("terminalType"));
		hblVO.setOperationType(request.getParameter("operationType")==null?"":request.getParameter("operationType"));
	//	hblVO.setInvokerOperation(request.getParameter("invokerOperation")==null?"":request.getParameter("invokerOperation"));
		hblVO.setSearchType(request.getParameter("searchType")==null?"":request.getParameter("searchType"));
		hblVO.setNoOfDaysControl(request.getParameter("past")==null?"lessOrEqualTo":request.getParameter("past"));
		hblVO.setNoOfDays(request.getParameter("noOfDays")==null?"15":request.getParameter("noOfDays"));
		request.setAttribute("searchDetails",hblVO);  
		return hblVO;     
	}

	public ETMAWBAdvVO getMAWBDetails(HttpServletRequest request, HttpServletResponse response)
	{
		ETMAWBAdvVO mawbVO = new ETMAWBAdvVO();

		if (request.getParameter("entity")!= null && "MAWB".equalsIgnoreCase(request.getParameter("entity")))
		{
			//Logger.info(FILE_NAME,"............mawbId............"+request.getParameter("mawbId"));
			mawbVO.setMawbId(request.getParameter("mawbId")==null?"":request.getParameter("mawbId"));
			//Logger.info(FILE_NAME,"............originGatewayId............"+request.getParameter("originGatewayId"));
			mawbVO.setOriginGatewayId(request.getParameter("originGatewayId")==null?"":request.getParameter("originGatewayId"));
			mawbVO.setDestinationGatewayId(request.getParameter("destinationGatewayId")==null?"":request.getParameter("destinationGatewayId"));
			mawbVO.setOriginTerminal(request.getParameter("origin")==null?"":request.getParameter("origin"));
			mawbVO.setDestinationTerminal(request.getParameter("destination")==null?"":request.getParameter("destination"));
			mawbVO.setCarrierId(request.getParameter("carrierId")==null?"":request.getParameter("carrierId"));
			mawbVO.setChargeableWeight(request.getParameter("chargeableWeight")==null?"0":request.getParameter("chargeableWeight"));
			mawbVO.setChargeableWeightControl(request.getParameter("chargeableWeightControl")==null?"":request.getParameter("chargeableWeightControl"));
			mawbVO.setBlockedSpace(request.getParameter("blockedSpace")==null?"0":request.getParameter("blockedSpace"));
			mawbVO.setBlockedSpaceControl(request.getParameter("blockedSpaceControl")==null?"":request.getParameter("blockedSpaceControl"));

			mawbVO.setEntity(request.getParameter("entity"));
			mawbVO.setTerminalId(request.getParameter("terminalId")==null?"":request.getParameter("terminalId"));
			
			// @@ Suneetha Commented 0n 20050512 (SPETI-6802)
			//if("ModifyMasterCost".equalsIgnoreCase(request.getParameter("operation")) || "UpdateAccounts".equalsIgnoreCase(request.getParameter("operation")))
			//{	
				mawbVO.setNoOfDaysControl(request.getParameter("past")==null?"lessOrEqualTo":request.getParameter("past"));
				mawbVO.setNoOfDays(request.getParameter("noOfDays")==null?"15":request.getParameter("noOfDays"));
			//}
			//else
			//{   //Modified By G.Srinivas to resolve the QA-Issue No:SPETI-5253 on 20050411.
			//	mawbVO.setNoOfDaysControl(request.getParameter("past")==null?"greaterOrEqualTo":request.getParameter("past"));
			//	mawbVO.setNoOfDays(request.getParameter("noOfDays")==null?"15":request.getParameter("noOfDays"));					
			//}
			// @@ 20050512 (SPETI-6802)

			mawbVO.setSearchType(request.getParameter("searchType")==null?"":request.getParameter("searchType"));
			mawbVO.setOperation(request.getParameter("operation")==null?"":request.getParameter("operation"));	
			request.setAttribute("searchDetails",mawbVO);  
		}
		return mawbVO;
	}

	public ETManifestAdvVO getManifestDetails(HttpServletRequest request, HttpServletResponse response)
	{
		String invokerOperation		=	"";
		HttpSession session = request.getSession();
		if(request.getParameter("invokerOperation")==null || "null".equals(request.getParameter("invokerOperation")) || "".equals(request.getParameter("invokerOperation")))
		{
			//Logger.info(FILE_NAME,"invokerOperation--@@@@@@@@@@@@@---if--get session -->"+(String)session.getAttribute("invokerOperation"));
			invokerOperation = (String)session.getAttribute("invokerOperation");  
		}
		else
		{
			//Logger.info(FILE_NAME,"invokerOperation----@@@@@@@@@@@@@@@@@@---if-else-  session set ->");
			invokerOperation=request.getParameter("invokerOperation");
			session.setAttribute("invokerOperation", invokerOperation); 
		}
		ETManifestAdvVO manifestAdvVO = new ETManifestAdvVO();
		manifestAdvVO.setManifestId(request.getParameter("manifestId")==null?"":request.getParameter("manifestId"));
		manifestAdvVO.setManifestType(request.getParameter("manifestType")==null?"":request.getParameter("manifestType"));
		//Logger.info(FILE_NAME,"----------inside getManifestDetails()------>"+request.getParameter("manifestType"));
		manifestAdvVO.setShipperName(request.getParameter("shipperName")==null?"":request.getParameter("shipperName"));
		manifestAdvVO.setConsigneeName(request.getParameter("consigneeName")==null?"":request.getParameter("consigneeName"));
		manifestAdvVO.setOriginLocation(request.getParameter("originLocation")==null?"":request.getParameter("originLocation"));
		manifestAdvVO.setDestinationLocation(request.getParameter("destLocation")==null?"":request.getParameter("destLocation"));
		manifestAdvVO.setOriginTerminal(request.getParameter("originTerminal")==null?"":request.getParameter("originTerminal"));
		manifestAdvVO.setDestinationTerminal(request.getParameter("destTerminal")==null?"":request.getParameter("destTerminal"));
		manifestAdvVO.setServiceLevel(request.getParameter("serviceLevel")==null?"":request.getParameter("serviceLevel"));
		manifestAdvVO.setTerminalID(request.getParameter("terminalId")==null?"":request.getParameter("terminalId"));
		manifestAdvVO.setEntity(request.getParameter("entity")==null?"":request.getParameter("entity"));
		manifestAdvVO.setType(request.getParameter("type")==null?"":request.getParameter("type"));
		manifestAdvVO.setInvokerOperation(invokerOperation);
		manifestAdvVO.setNoOfDaysControl(request.getParameter("past")==null?"lessOrEqualTo":request.getParameter("past"));
		manifestAdvVO.setNoOfDays(request.getParameter("noOfDays")==null?"15":request.getParameter("noOfDays"));
		manifestAdvVO.setSearchType(request.getParameter("searchType")==null?"":request.getParameter("searchType"));
		request.setAttribute("searchDetails",manifestAdvVO);  
		return manifestAdvVO;        
	}

	public ETConsignNoteAdvVO getConsignNoteDetails(HttpServletRequest request, HttpServletResponse response)
	{
	String invokerOperation		=	"";
	HttpSession session = request.getSession();
	if(request.getParameter("invokerOperation")==null || "null".equals(request.getParameter("invokerOperation")) || "".equals(request.getParameter("invokerOperation")))
		{
			//Logger.info(FILE_NAME,"invokerOperation--@@@@@@@@@@@@@---if--get session -->"+(String)session.getAttribute("invokerOperation"));
			invokerOperation = (String)session.getAttribute("invokerOperation");  
		}
		else
		{
			//Logger.info(FILE_NAME,"invokerOperation----@@@@@@@@@@@@@@@@@@---if-else-  session set ->");
			invokerOperation=request.getParameter("invokerOperation");
			session.setAttribute("invokerOperation", invokerOperation); 
		}
		ETConsignNoteAdvVO consignNoteAdvVO = new ETConsignNoteAdvVO();
		consignNoteAdvVO.setConsignNoteId(request.getParameter("consignNoteId")==null?"":request.getParameter("consignNoteId"));
		consignNoteAdvVO.setShipperId(request.getParameter("shipperId")==null?"":request.getParameter("shipperId"));
		consignNoteAdvVO.setShipperName(request.getParameter("shipperName")==null?"":request.getParameter("shipperName"));
		consignNoteAdvVO.setConsigneeName(request.getParameter("consigneeName")==null?"":request.getParameter("consigneeName"));
		consignNoteAdvVO.setOriginTerminal(request.getParameter("originTerminal")==null?"":request.getParameter("originTerminal"));
		consignNoteAdvVO.setDestinationTerminal(request.getParameter("destinationTerminal")==null?"":request.getParameter("destinationTerminal"));
		consignNoteAdvVO.setOriginCountry(request.getParameter("originCountry")==null?"":request.getParameter("originCountry"));
		consignNoteAdvVO.setDestinationCountry(request.getParameter("destinationCountry")==null?"":request.getParameter("destinationCountry"));
		consignNoteAdvVO.setEntity(request.getParameter("entity"));
		consignNoteAdvVO.setType(request.getParameter("type")==null?"":request.getParameter("type"));
		consignNoteAdvVO.setTruckLoad(request.getParameter("truckLoad")==null?"":request.getParameter("truckLoad"));
		consignNoteAdvVO.setTerminalID(request.getParameter("terminalId")==null?"":request.getParameter("terminalId"));
		consignNoteAdvVO.setInvokerOperation(invokerOperation);
		consignNoteAdvVO.setFromDate(request.getParameter("fromDate")==null?"":request.getParameter("fromDate"));
		consignNoteAdvVO.setToDate(request.getParameter("toDate")==null?"":request.getParameter("toDate"));
		consignNoteAdvVO.setDateFormat(request.getParameter("dateFormat")==null?"":request.getParameter("dateFormat"));
		consignNoteAdvVO.setInvoiceGenerationType(request.getParameter("invoiceGenerationType")==null?"":request.getParameter("invoiceGenerationType"));
		consignNoteAdvVO.setNoOfDaysControl(request.getParameter("past")==null?"lessOrEqualTo":request.getParameter("past"));
		consignNoteAdvVO.setNoOfDays(request.getParameter("noOfDays")==null?"15":request.getParameter("noOfDays"));
		consignNoteAdvVO.setSearchType(request.getParameter("searchType")==null?"":request.getParameter("searchType"));
		request.setAttribute("searchDetails",consignNoteAdvVO);  
		return consignNoteAdvVO;        
	}  

	public ETConsoleAdvVO getConsoleDetails(HttpServletRequest request, HttpServletResponse response)
	{
		ETConsoleAdvVO consoleVO = new ETConsoleAdvVO();
		if (request.getParameter("entity")!= null || request.getParameter("entity").equalsIgnoreCase("Console"))
		{
			ESupplyGlobalParameters loginBean		= 	null;
			HttpSession 			session 		= 	request.getSession();
			loginBean								= 	(ESupplyGlobalParameters)session.getAttribute("loginbean");
// Murali 03042005			
			String dateFormat       = loginBean.getUserPreferences().getDateFormat();
//03042005
			consoleVO.setConsoleId(request.getParameter("consoleId")==null?"":request.getParameter("consoleId"));
			consoleVO.setOriginGateway(request.getParameter("terminalId")==null?"":request.getParameter("terminalId"));
			consoleVO.setDestinationGateway(request.getParameter("destinationGateway")==null?"":request.getParameter("destinationGateway"));
			consoleVO.setPortOfLoading(request.getParameter("portOfLoading")==null?"":request.getParameter("portOfLoading"));
			consoleVO.setPortOfDestination(request.getParameter("portOfDestination")==null?"":request.getParameter("portOfDestination"));
			consoleVO.setCarrierId(request.getParameter("carrierId")==null?"":request.getParameter("carrierId"));
			consoleVO.setServiceLevel(request.getParameter("serviceLevel")==null?"":request.getParameter("serviceLevel"));
			consoleVO.setCutOffDays(request.getParameter("cutOffDays")==null?"":request.getParameter("cutOffDays"));
			consoleVO.setFromWhat(request.getParameter("fromWhat")==null?"":request.getParameter("fromWhat"));
			consoleVO.setOperation(request.getParameter("operation")==null?"":request.getParameter("operation"));
			consoleVO.setFormField(request.getParameter("formField")==null?"":request.getParameter("formField"));
// @@ Murali on 20050331 PR - 1180
			consoleVO.setDateFormat(dateFormat==null?"":dateFormat); 
// @@ Murali on 20050331 PR - 1180

			String consoleType = request.getParameter("consoleType");

			if (consoleType==null)
			{
				// error
			}
			else if (consoleType.equals("LCLCONSOLE"))
			{
				consoleVO.setConsoleType("'LCL_TO_LCL','LCL_TO_FCL'");
			}
			else if (consoleType.equals("FCLCONSOLE"))
			{
				consoleVO.setConsoleType("'FCL_TO_FCL'");
			}
			else if (consoleType.equals("FCL_BACK_TO_BACK"))
			{
				consoleVO.setConsoleType("'FCL_BACK_TO_BACK'");
			}
			else
			{
				consoleVO.setConsoleType("'BREAK_BULK'");
			}
			consoleVO.setEntity(request.getParameter("entity"));
			consoleVO.setSearchType(request.getParameter("searchType")==null?"startsWith":request.getParameter("searchType"));
			consoleVO.setNoOfDaysControl(request.getParameter("past")==null?"lessOrEqualTo":request.getParameter("past"));
			consoleVO.setNoOfDays(request.getParameter("noOfDays")==null?"15":request.getParameter("noOfDays"));
			request.setAttribute("searchDetails",consoleVO);  
		}
		return consoleVO;
	}

/*
	public ETInvoiceAdvVO getInvoiceDetails(HttpServletRequest request, HttpServletResponse response)
	{
		ETInvoiceAdvVO invoiceAdvVO = new ETInvoiceAdvVO();
		invoiceAdvVO.setInvoiceId(request.getParameter("invoiceId")==null?"":request.getParameter("invoiceId"));
		invoiceAdvVO.setHawbId(request.getParameter("hawbId")==null?"":request.getParameter("hawbId"));
		invoiceAdvVO.setHblId(request.getParameter("hblId")==null?"":request.getParameter("hblId"));
		invoiceAdvVO.setConsignmentNoteId(request.getParameter("consignmentNoteId")==null?"":request.getParameter("consignmentNoteId"));
		invoiceAdvVO.setShipperName(request.getParameter("shipperName")==null?"":request.getParameter("shipperName"));
		invoiceAdvVO.setConsigneeName(request.getParameter("consigneeName")==null?"":request.getParameter("consigneeName"));
		invoiceAdvVO.setOriginTerminal(request.getParameter("originTerminal")==null?"":request.getParameter("originTerminal"));
		invoiceAdvVO.setDestinationTerminal(request.getParameter("destinationTerminal")==null?"":request.getParameter("destinationTerminal"));
		invoiceAdvVO.setInvoiceAmount(request.getParameter("invoiceAmount")==null?"":request.getParameter("invoiceAmount"));
		invoiceAdvVO.setMode(request.getParameter("mode"));
		invoiceAdvVO.setEntity(request.getParameter("entity"));
		invoiceAdvVO.setType(request.getParameter("type"));
		invoiceAdvVO.setOperation(request.getParameter("operation"));
		invoiceAdvVO.setSubOperation(request.getParameter("subOperation"));
		invoiceAdvVO.setNoOfDaysControl(request.getParameter("past")==null?"lessOrEqualTo":request.getParameter("past"));
		invoiceAdvVO.setNoOfDays(request.getParameter("noOfDays")==null?"15":request.getParameter("noOfDays"));
		invoiceAdvVO.setSearchType(request.getParameter("searchType")==null?"":request.getParameter("searchType"));
		request.setAttribute("searchDetails",invoiceAdvVO);  
		return invoiceAdvVO;        
	}
*/
	public ETPRQAdvVO getPRQDetails(HttpServletRequest request, HttpServletResponse response)
	{
		ETPRQAdvVO prqVO = new ETPRQAdvVO();
		prqVO.setPrqId(request.getParameter("prqId")==null?"":request.getParameter("prqId"));
		prqVO.setShipperName(request.getParameter("shipper")==null?"":request.getParameter("shipper"));
		prqVO.setConsigneeName(request.getParameter("consignee")==null?"":request.getParameter("consignee"));
		prqVO.setOriginLocation(request.getParameter("origin")==null?"":request.getParameter("origin"));
		prqVO.setDestinationLocation(request.getParameter("destination")==null?"":request.getParameter("destination"));
		prqVO.setOriginCountryId(request.getParameter("originCountryId")==null?"":request.getParameter("originCountryId"));
		prqVO.setDestinationCountryId(request.getParameter("destCountryId")==null?"":request.getParameter("destCountryId"));
		prqVO.setConsoleType(request.getParameter("consoleType")==null?"":request.getParameter("consoleType"));
		prqVO.setEntity(request.getParameter("entity")==null?"":request.getParameter("entity"));
		prqVO.setTerminalID(request.getParameter("terminalId")==null?"":request.getParameter("terminalId"));
		prqVO.setOperation(request.getParameter("operation")==null?"":request.getParameter("operation"));    
		prqVO.setConsigneeId(request.getParameter("consigneeId")==null?"":request.getParameter("consigneeId"));
		prqVO.setShipperId(request.getParameter("shipperId")==null?"":request.getParameter("shipperId"));
		prqVO.setUOW(request.getParameter("UOW")==null?"":request.getParameter("UOW"));
		prqVO.setUOM(request.getParameter("UOM")==null?"":request.getParameter("UOM"));    
		
////////////////////////@@ G.Srinivas Modified on 20050402 (LOV Advanced Search ) -->	
	String invokerOperation		=	"null";
	HttpSession session = request.getSession();
	
if(request.getParameter("invokerOperation")!=null && !request.getParameter("invokerOperation").equals(""))
		{
		//Logger.info(FILE_NAME,"invokerOperation----@@@@@@@@@@@@@@@@@@---if-   session set ->");
		invokerOperation=request.getParameter("invokerOperation");
		session.setAttribute("invokerOperation", invokerOperation); 
		prqVO.setInvokerOperation(request.getParameter("invokerOperation")==null?"":request.getParameter("invokerOperation"));
		
		}
		else
		{
		//Logger.info(FILE_NAME,"invokerOperation--@@@@@@@@@@@@@---if-else-get session -->"+(String)session.getAttribute("invokerOperation"));
		invokerOperation = (String)session.getAttribute("invokerOperation");  
		prqVO.setInvokerOperation(invokerOperation);
	
		}
//prqVO.setInvokerOperation(request.getParameter("invokerOperation")==null?"":request.getParameter("invokerOperation"));
//Logger.info(FILE_NAME,"invokerOperation----@@@@@@@@@@@@@@@@@@----->"+invokerOperation);
////////////////////////@@ G.Srinivas Modified on 20050402 (LOV Advanced Search ) -->

		//Logger.info(FILE_NAME,"operation"+request.getParameter("operation"));
		prqVO.setMode(request.getParameter("mode")==null?"":request.getParameter("mode"));
		prqVO.setBookType(request.getParameter("bookType")==null?"":request.getParameter("bookType"));
		prqVO.setOperationType(request.getParameter("operationType")==null?"":request.getParameter("operationType"));
		prqVO.setSubOperation(request.getParameter("subOperation")==null?"":request.getParameter("subOperation"));
		prqVO.setTerminalType(request.getParameter("terminalType")==null?"":request.getParameter("terminalType"));
		prqVO.setSearchType(request.getParameter("searchType")==null?"":request.getParameter("searchType"));
		prqVO.setNoOfDaysControl(request.getParameter("past")==null?"lessOrEqualTo":request.getParameter("past"));
		prqVO.setNoOfDays(request.getParameter("noOfDays")==null?"15":request.getParameter("noOfDays"));
		//Logger.info(FILE_NAME,"invokerOperation--------->"+request.getParameter("invokerOperation"));
		//Logger.info(FILE_NAME,"operation"+request.getParameter("operation"));
		request.setAttribute("searchDetails",prqVO);  
		return prqVO;        
	}
	public void forwardToJSP(HttpServletRequest request, HttpServletResponse response)
	{
		try
		{
			if(request.getParameter("entity").equalsIgnoreCase("HAWB"))
			{  
				doFileDispatch(request,response,"etrans/ETHAWBAdvancedLOV.jsp");
			}
			else if(request.getParameter("entity").equalsIgnoreCase("CarrierContract"))
			{
				doFileDispatch(request,response,"etrans/ETCarrierContractAdvLOV.jsp");
			}
			else if(request.getParameter("entity").equalsIgnoreCase("HBL"))
			{
				doFileDispatch(request,response,"etrans/ETHBLAdvancedLOV.jsp");
			}
			else if(request.getParameter("entity").equalsIgnoreCase("MAWB"))
			{
				//Logger.info(FILE_NAME," In before dispatcher forwardToJSP " + request.getParameter("entity") );
				doFileDispatch(request,response,"etrans/ETMAWBAdvancedLOV.jsp");
			}
			else if(request.getParameter("entity").equalsIgnoreCase("Manifest"))
			{  
				doFileDispatch(request,response,"etrans/truck/ETTManifestAdvancedLov.jsp");
			}
			else if(request.getParameter("entity").equalsIgnoreCase("ConsignmentNote"))
			{  
				doFileDispatch(request,response,"etrans/truck/ETTConsignNoteAdvancedLov.jsp");
			}
			else if(request.getParameter("entity").equalsIgnoreCase("Invoice"))
			{  
				doFileDispatch(request,response,"etrans/ETInvoiceAdvLOV.jsp");
			}
			else if(request.getParameter("entity").equalsIgnoreCase("console"))
			{  
				doFileDispatch(request,response,"etrans/ETConsoleAdvancedLov.jsp");
			}
			else if(request.getParameter("entity").equalsIgnoreCase("Customer"))
			{  
				doFileDispatch(request,response,"etrans/ETCustomerAdvLOV.jsp");
			}
			else if(request.getParameter("entity").equalsIgnoreCase("PRQ"))
			{  
				doFileDispatch(request,response,"etrans/ETPRQAdvLOV.jsp");
			}
			else
			{  
			// error
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public void doFileDispatch(HttpServletRequest request, HttpServletResponse response, String forwardFile)			throws IOException, ServletException
	{
		//Logger.info(FILE_NAME," In Dispatcher " + forwardFile );
		try
		{   
			RequestDispatcher rd = request.getRequestDispatcher(forwardFile);
			rd.forward(request, response);
			return;
		}
		catch(Exception ex)
		{
			//Logger.error(FILE_NAME, " [doFileDispatch() ", " Exception in forwarding ---> "+ ex.toString());
      logger.error(FILE_NAME+ " [doFileDispatch() "+ " Exception in forwarding ---> "+ ex.toString());
			ex.printStackTrace();
		}
	}
}