/*
	Program Name	:QMSQuoteAjaxController.java
	Module Name		:
	Task			:
	Sub Task		:
	Author Name		:Govind
	Date Started	:1-NOV-2010
	Date Completed:
	Date Modified	:
	Description		:Used As a AjaxController for Quote Add Operations
*/



import java.io.IOException;
import java.io.PrintWriter;

import com.foursoft.esupply.common.bean.ESupplyGlobalParameters;
import com.qms.operations.quote.dao.QMSQuoteAjaxDAO;
import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.log4j.Logger;

import weblogic.xml.security.wsse.v200207.SecureOutputPipelineFactory;

public class QMSQuoteAjaxController extends HttpServlet {
	
			private static final String 	   FILE_NAME 		= "QMSQuoteAjaxController.java";
			private static Logger logger = null;
			private static final String CONTENT_TYPE1 = "text/xml";
			
		    HttpSession				session					=	 null;
		    String					nextNavigation			=	 null;
  
  
  
			public void init(ServletConfig config) throws ServletException
			{
				logger  = Logger.getLogger(BuyRatesController.class);
				super.init(config);
			} 
			
			/**
			* Process the HTTP doGet request.
			*/
			public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
			{
				response.setContentType(CONTENT_TYPE1);
			    PrintWriter out=response.getWriter();
			    ESupplyGlobalParameters loginbean  = (ESupplyGlobalParameters)request.getSession().getAttribute("loginbean");
				try{
				String type             = null;//@@ Indicates it is a ajax call
				String searchOption     = null;//@@Indicates which operation to perform
				String result			= null;//@@ Carry's result
				String customerId		= null;//@@Custmer id
				String attentionSlno=null;
				String salesPersonId	= null;//@@Salesperson
				String address			= null;//Address Value
                                String attention		= null; 
                String slno             = null;
				String addressType		= null; 
				String result1			= null;
				String type1             = null;
				String result2			= null;
				          type 			= request.getParameter("type");
				        
				          type 			= request.getParameter("type");
				          //System.out.println("type-----"+type);
				          searchOption  = request.getParameter("SearchOption");
				          //System.out.println("searchOption-----"+searchOption);
				          customerId	= request.getParameter("customerId");	
				          //System.out.println("customerId-----"+customerId);
				          address		= request.getParameter("address");
				          //System.out.println("address-----"+address);
				          slno          = request.getParameter("slno");
				        String  customer         = request.getParameter("customer");
				
			 if ("Ajax".equalsIgnoreCase(type)) {
				 
				 if("CustAdd".equalsIgnoreCase(searchOption)){
				 QMSQuoteAjaxDAO   ajaxDao = new QMSQuoteAjaxDAO();
				 //System.out.println("customerId--->"+customerId);
				 result = ajaxDao.getCustomerAddress(customerId,loginbean);
				// System.out.println(result);
			    out.write(result);
				 }
                        //else if ("Ajax".equalsIgnoreCase(type)) {
					 	 //Added by Rakesh for Issue:
				 if("SalesPerson1".equalsIgnoreCase(searchOption)){
					 QMSQuoteAjaxDAO   ajaxDao = new QMSQuoteAjaxDAO();
					// System.out.println("customerId--->"+customerId);
					 //result = ajaxDao.getCustomerAddress(customerId,loginbean);
					 result1=ajaxDao.getSalesPerson(customerId,loginbean,addressType);
					 //System.out.println(result);
					// System.out.println("result1-->"+result1);
				   // out.write(result);
				    out.write(result1);
					 }
					 //Ended by Rakesh for Issue:
					 if("CustAtten1".equalsIgnoreCase(searchOption)){
					 QMSQuoteAjaxDAO   ajaxDao = new QMSQuoteAjaxDAO();
					// System.out.println("customerId--->"+customerId);
					 //result = ajaxDao.getCustomerAddress(customerId,loginbean);
					 result2=ajaxDao.getCustomerAttention(customerId,loginbean,addressType);
					 //System.out.println(result);
					// System.out.println("result2-->"+result2);
				   // out.write(result);
				    out.write(result2);
					 }
				// }
			else if("CustAtt".equalsIgnoreCase(searchOption)){
					 QMSQuoteAjaxDAO   ajaxDao = new QMSQuoteAjaxDAO();
					// System.out.println("customerId--->"+customerId);
					 result = ajaxDao.getCustomerAttention(customerId,loginbean,address);
					// System.out.println(result);
				    out.write(result);					 
				 }
					 //@Added by silpa for invalidate the contactpersons table
			/*else if("Update".equalsIgnoreCase(searchOption)){
				
				System.out.println("slno--->"+slno);
					 QMSQuoteAjaxDAO   ajaxDao = new QMSQuoteAjaxDAO();
					 ajaxDao.getCustomerUpdate(slno ,loginbean,customer);
					 //System.out.println(result);
				    //out.write(result);					 
				 }*/
					 //@ended
			else if("SalesPersonValid".equalsIgnoreCase(searchOption)){
					 salesPersonId =request.getParameter("salesPersonId");
					 String shipmentMode  = request.getParameter("shipmentMode");
					 String serviceLevel  = request.getParameter("servicelevel");
					// System.out.println("salesPersonId-----"+salesPersonId);
					 QMSQuoteAjaxDAO   ajaxDao = new QMSQuoteAjaxDAO();
					 result = ajaxDao.checkSalesPersonValid(salesPersonId,loginbean,shipmentMode,serviceLevel);
					 out.write(result);
				}
			else if("OriginDestinationValid".equalsIgnoreCase(searchOption))	{
				String Location = request.getParameter("Location");
				String shipmentMode  = request.getParameter("shipmentMode");
				String LocationId = null;
				String rest 	  = null;
				//System.out.println("Location--------"+Location);
				QMSQuoteAjaxDAO   ajaxDao = new QMSQuoteAjaxDAO();
				if("origin".equalsIgnoreCase(Location)){
					LocationId = request.getParameter("originLoc");
					rest = ajaxDao.originDestinationValid(LocationId, shipmentMode);
					result = (rest!= null && !"".equals(rest))?"Origin "+rest:"";  
					
					}
				else{
					LocationId = request.getParameter("destLoc");
					rest = ajaxDao.originDestinationValid(LocationId, shipmentMode);
					result = (rest!= null && !"".equals(rest))?"Destination "+rest:"";
				}
				out.write(result);
			}
			else if("OriginDestinationPortValid".equalsIgnoreCase(searchOption))	{
				String Location = request.getParameter("Location");
				String shipmentMode  = request.getParameter("shipmentMode");
				String portId = null;
				String rest 	  = null;
				//System.out.println("Location--------"+Location);
				QMSQuoteAjaxDAO   ajaxDao = new QMSQuoteAjaxDAO();
				if("origin".equalsIgnoreCase(Location)){
					portId = request.getParameter("originPort");
					rest = ajaxDao.originDestinationPortValid(portId, shipmentMode);
					result = (rest!= null && !"".equals(rest))?"Origin "+rest:"";  
					
					}
				else{
					portId = request.getParameter("destPort");
					rest = ajaxDao.originDestinationPortValid(portId, shipmentMode);
					result = (rest!= null && !"".equals(rest))?"Destination "+rest:"";
				}
				out.write(result);
			}else if("IndustryCheck".equalsIgnoreCase(searchOption)){
				String industryId = request.getParameter("industryId");
				QMSQuoteAjaxDAO   ajaxDao = new QMSQuoteAjaxDAO();
				result = ajaxDao.checkIndustryValid(industryId,loginbean);
				out.write(result);
			}else if("ComodityCheck".equalsIgnoreCase(searchOption)){
				String comodityId = request.getParameter("comodityId");
				String shipmentMode	= request.getParameter("shipmentMode");
				QMSQuoteAjaxDAO   ajaxDao = new QMSQuoteAjaxDAO();
				result = ajaxDao.checkComodityValid(comodityId,shipmentMode,loginbean);
				out.write(result);
			}else if("servicelevelcheck".equalsIgnoreCase(searchOption)){
				String servicelevel = request.getParameter("servicelevel");
				String shipmentMode = request.getParameter("shipmentMode");
				QMSQuoteAjaxDAO   ajaxDao = new QMSQuoteAjaxDAO();
				result = ajaxDao.checkServiceLevelValid(servicelevel,shipmentMode,loginbean);
				out.write(result);
			}
				 else if("RateCheck".equalsIgnoreCase(searchOption)){
					 String org   = request.getParameter("origin");
					 String dest  = request.getParameter("destination");
					 String weight_break  = request.getParameter("weightBreak");
					 String shipmentmode  = request.getParameter("shipmentmode");
					 QMSQuoteAjaxDAO   ajaxDao = new QMSQuoteAjaxDAO();
					  result = ajaxDao.checkRateExistsForTheLane(org,dest,shipmentmode,weight_break,loginbean);
					 if(result!= null)
						 out.write(result);
				 }
				 //Added By Kishore Podili For the Single and MultiQuote Validation
				 else if("validQuote".equalsIgnoreCase(searchOption)){
						//String singleOrMulti = request.getParameter("mode");
						String quoteId = request.getParameter("id")+"~"+request.getParameter("multiQuote");
						QMSQuoteAjaxDAO   ajaxDao = new QMSQuoteAjaxDAO();
						result = ajaxDao.checkQuoteId(quoteId,loginbean);
						out.write(result);
					}
				 // End Of Kishore Podili For the Single and MultiQuote Validation
			    
				//Added By Kishore Podili For Zone Code  Validation
				 else if("IsValidZoneCode".equalsIgnoreCase(searchOption)){
						//String singleOrMulti = request.getParameter("mode");
						String zoneCode = request.getParameter("zoneCode");
						String shipmentMode = request.getParameter("shipmentMode");
						String  location = request.getParameter("location");
						String  weightBreak = request.getParameter("weightBreak");
						String  consoleType = null;
						if("SEA".equalsIgnoreCase(shipmentMode)){
							if("FLAT".equalsIgnoreCase(weightBreak) ||"SLAB".equalsIgnoreCase(weightBreak))
								consoleType = "LCL";
							else
								consoleType = "FCL";
						}
						
						QMSQuoteAjaxDAO   ajaxDao = new QMSQuoteAjaxDAO();
						result = ajaxDao.validateZoneCode(zoneCode,shipmentMode,location,consoleType);
						out.write(result);
					}
				 // End Of Kishore Podili For Zone Code Validation
				 else if("Cust_ID".equalsIgnoreCase(searchOption)){
					 
					 String cust_id = request.getParameter("Cust_id");
					 QMSQuoteAjaxDAO   ajaxDao = new QMSQuoteAjaxDAO();
					 result  = ajaxDao.getCustomerIDs(cust_id,loginbean);
					 response.setContentType("text/xml");
			         response.setHeader("Cache-Control", "no-cache");
					 if(result != null && !"".equals(result))
			         out.write("<ids>"+result+"</ids>");				 
				 }
			    
			 						
			}else {
				doPost(request,response);
				}
				}catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}finally{
					out.close();
				}
				
				
			}
			
			/**
			* Process the HTTP doPost request.
			*/
			 public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
			{
				
				
				
				
				
				
				
				
			}
  
  
}
