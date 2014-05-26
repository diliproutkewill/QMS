import com.foursoft.esupply.common.bean.ESupplyGlobalParameters;
import com.foursoft.esupply.common.java.LookUpBean;
import com.foursoft.esupply.common.util.ESupplyDateUtility;
import com.foursoft.esupply.common.util.StringUtility;
import com.qms.reports.java.QMSChargesReportDOB;
import com.qms.reports.java.QMSRatesReportDOB;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.PrintWriter;
import java.io.IOException;
import org.apache.log4j.Logger;
import com.qms.reports.ejb.sls.ReportsSession;
import com.qms.reports.ejb.sls.ReportsSessionBeanHome;
public class QMSRatesReportController extends HttpServlet 
{
  private static final String CONTENT_TYPE = "text/html; charset=windows-1252";
	private static final String 	   FILE_NAME 		= "QMSRatesReportController.java";
	private static Logger logger = null;
 
  HttpSession				session					=	 null;
  String					nextNavigation			=	 null;
  public void init(ServletConfig config) throws ServletException
  {
    logger  = Logger.getLogger(QMSRatesReportController.class);
    super.init(config);
  }

  public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
  {
     	doPost(request,response);
 }

  public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
  {
     String                   operation        = null;
     String                   subOperation     = null;
     String                   nextNavigation   = null; 
     String                   customerId       = null; 
     String                   originCountry    = null; 
     String                   originCity       = null; 
     String                   destCountry      = null; 
     String                   destCity         = null; 
     String                   shipmentMode     = null; 
     StringBuffer			      	errorMessage     =	new StringBuffer();
     ReportsSession           remote           =  null;
     ReportsSessionBeanHome   home             =  null;
     QMSRatesReportDOB        ratesReportDOB   =  null;
     ArrayList                ratesList        =  null;
     ArrayList                wtBreakList      =  null; 
     ArrayList                chargeRateList   =  null;
     ArrayList                totalList        = null;
     String                   errorMsg         = null;
     PrintWriter              out              = null;
     int                      colspan          = 0;
     HttpSession              session          = request.getSession();
     ESupplyGlobalParameters 	loginbean				 = (ESupplyGlobalParameters)session.getAttribute("loginbean");
     ESupplyDateUtility        dateUtility     = null;
     String                    strn            = null;
     String[]                  notes          = null;
     ArrayList                  shmode         = null;
     int                        number         = 0;
     ESupplyDateUtility         dateutil       = null;
      //added by phani for cr 167656
     ArrayList                dataList         = null;
     ArrayList                 mainDataList    = null;
     String                   frmDate          =	null;
     String                   toDate           = null;
     StringBuffer             errors           = null;
     QMSRatesReportDOB        ratesrptDOB      =  null;
     String                   consoleType      = null;
     String                   spotRateFlag     = null;
     String                   quote_id         = null;//@@ Added by govind for the issue  263250
     String                   temp             = "";
     //Added By Kishore Podili For QuoteGroupExcel Report with Charges
     String					  weightBreak		= "";
     HashMap<String, String>  paramsMap			= null;
     HashMap                  quoteMap          = null;
     HashSet                  QuoteIdset           = null;
     ArrayList<ArrayList>     returnedList      = null;
     ArrayList<QMSChargesReportDOB>  orgDestChargesList = null;
     QMSChargesReportDOB	orgDestChargesDOB = null;
   //End Of  Kishore Podili For QuoteGroupExcel Report with Charges
     
     int count1 = 0;
     int count2 = 0;
     int count3 = 0;
     int count4 = 0;
     int count5 = 0;
     int count7  = 0;
     java.text.DecimalFormat df = null;
     try
    {
       operation       = request.getParameter("Operation");
       subOperation    = request.getParameter("subOperation");
       String              format          =  loginbean.getUserPreferences().getDateFormat();
        df			  =	new java.text.DecimalFormat("##,###,##0.00");
            home        = (ReportsSessionBeanHome)LookUpBean.getEJBHome("ReportsSessionBean");
           remote       = (ReportsSession)home.create(); 
       if(operation!=null&&"GroupingExcel".equalsIgnoreCase(operation))
      {         
          if(subOperation!=null&&"Report".equalsIgnoreCase(subOperation))
            {
              customerId       =  request.getParameter("customerId");  
              originCountry    =  request.getParameter("originCountry");
              originCity       =  request.getParameter("originCity");  
              destCountry      =  request.getParameter("destCountry");  
              destCity         =  request.getParameter("destCity");  
              shipmentMode     =  request.getParameter("shipmentMode");  
               consoleType     =  request.getParameter("consoleType");  
               spotRateFlag    =  request.getParameter("spotRateFlag");  
             
               //Added By Kishore Podili For QuoteGroupExcel Report with Charges on 03=Feb-11
               weightBreak	   =  request.getParameter("weightBreak"); 
               
               paramsMap	= new HashMap<String, String>();
               
               paramsMap.put("customerId",    customerId);
               paramsMap.put("originCountry", originCountry);
               paramsMap.put("originCity",   originCity);
               paramsMap.put("destCountry",  destCountry);
               paramsMap.put("destCity",     destCity);
               paramsMap.put("shipmentMode", shipmentMode);
               paramsMap.put("consoleType",  consoleType);
               paramsMap.put("spotRateFlag", spotRateFlag);
               paramsMap.put("weightBreak", weightBreak);
               
               //End Of Kishore Podili For QuoteGroupExcel Report with Charges on 03=Feb-11 
               errorMsg        =  remote.validateRateDetails( customerId,originCountry,originCity,destCountry,destCity,loginbean);
               if(errorMsg!=null&&errorMsg.trim().length()>0)
              {
                 	request.setAttribute("errorMsg",errorMsg);
                  nextNavigation = "reports/QMSGroupExcelReport.jsp?customerId="+customerId+"&originCountry="+originCountry+"&originCity="+originCity+"&destCountry="+destCountry+"&destCity="+destCity+"&shipmentMode="+shipmentMode+"&spotRateFlag="+spotRateFlag;
                
                  	doFileDispatch(request,response,nextNavigation);
              }
              else
              {
                  dateUtility  = new ESupplyDateUtility();
                //Modified By Kishore Podili For QuoteGroupExcel Report with Charges on 03=Feb-11
                  quoteMap    =   remote.getGroupingExcelDetails(paramsMap,loginbean); 
                       returnedList = (ArrayList)quoteMap.get("returnList");
                       quoteMap.remove("returnList");    
               if(returnedList!=null && returnedList.size()>0){
            	   
                  totalList		   =   returnedList.get(0);
                  orgDestChargesList =  returnedList.get(1);
                  
              
                  if(totalList!=null&&totalList.size()>3)
                  {
                       response.setContentType("application/vnd.ms-excel"); 
                       response.setHeader("Content-Disposition","attachment;filename=QuoteGroup Excel Report.xls");                  
                       out = response.getWriter();
                       errorMessage.append("<html>");
                       errorMessage.append("<body>");
                      /* errorMessage.append("<table width='100%' border=0 cellspacing='0' cellpadding='0'> ");*/
                    int totalListSize	=	totalList.size();
                       for(int k=3;k<totalListSize;k++)
                     { 
                
                        ratesList =(ArrayList)(totalList.get(k));
                      
                        ratesReportDOB = (QMSRatesReportDOB)ratesList.get(0);
                         if("2".equalsIgnoreCase(ratesReportDOB.getShipmentMode())&&"LIST".equalsIgnoreCase(ratesReportDOB.getWtBreak()))
                        {
                                if("on".equalsIgnoreCase(spotRateFlag))
                              {
                                  colspan =22; //Modified by Mohan for Issue No.219976 on 28-10-2010
                              }
                              else
                              {
                                  colspan =21; //Modified by Mohan for Issue No.219976 on 28-10-2010
                              }
                        }
                        else
                        {
                                if("on".equalsIgnoreCase(spotRateFlag))
                              {
                               //Modified by Mohan for Issue No.219976 on 28-10-2010
                                  colspan = 16+ratesReportDOB.getWtBreakList().size();
                              }
                              else
                              {
                               //Modified by Mohan for Issue No.219976 on 28-10-2010
                                 colspan = 15+ratesReportDOB.getWtBreakList().size();
                              }
                        }
                     
                       
                      if(k==3)
                      {
                         errorMessage.append("<table width='100%' border=0 cellspacing='0' cellpadding='0' bgcolor='#d3d3d3' ><tr align='left'><td><b>DHL GLOBAL FORWARDING</font></b></td>");
                         for(int i=0;i<colspan-1;i++)
                         {
                          errorMessage.append("<td></td>"); 
                         }
                         errorMessage.append("</tr><tr align='left'><td><b>"+(String)totalList.get(2)+"</b></td>");
                          for(int i=0;i<colspan-1;i++)
                         {
                          errorMessage.append("<td></td>"); 
                         }
                          errorMessage.append("</tr></table>");
                      }
                     if("1".equalsIgnoreCase(ratesReportDOB.getShipmentMode()))
                    {
                      if("FLAT".equalsIgnoreCase(ratesReportDOB.getWtBreak()))
                       {
                         errorMessage.append("<table width='100%' border=0 cellspacing='0' cellpadding='0' bgcolor='#d3d3d3'><tr>");
                        for(int i=0;i<colspan;i++)
                         {
                          errorMessage.append("<td></td>"); 
                         }
                           errorMessage.append("</tr><tr>");
                         errorMessage.append("<td align='left' border=0 cellpadding=0 cellspacing=0 bgcolor='#d3d3d3'><b>Air - Flat Charges</b></td></tr><tr>");
                      
                          for(int i=0;i<colspan-1;i++)
                         {
                          errorMessage.append("<td></td>"); 
                         } 
                         errorMessage.append("</tr></table>");
                      
                       }
                       else  if("SLAB".equalsIgnoreCase(ratesReportDOB.getWtBreak()))
                       {
                          errorMessage.append("<table width='100%' border=0 cellspacing='0' cellpadding='0' bgcolor='#d3d3d3'><tr>");
                        for(int i=0;i<colspan;i++)
                         {
                          errorMessage.append("<td></td>"); 
                         }
                           errorMessage.append("</tr><tr>");
                         errorMessage.append("<td align='left' border=0 cellpadding=0 cellspacing=0 bgcolor='#d3d3d3'><b>Air - Slab Charges</b></td></tr><tr>");
                      
                          for(int i=0;i<9;i++)
                         {
                          errorMessage.append("<td></td>"); 
                         } 
                         errorMessage.append("</tr></table>");
                      
                       }
                       else
                       {
                          errorMessage.append("<table width='100%' border=0 cellspacing='0' cellpadding='0' bgcolor='#d3d3d3'><tr>");
                        for(int i=0;i<colspan;i++)
                         {
                          errorMessage.append("<td></td>"); 
                         }
                           errorMessage.append("</tr><tr>");
                         errorMessage.append("<td align='left' border=0 cellpadding=0 cellspacing=0 bgcolor='#d3d3d3'><b>Air - List Charges</b></td></tr><tr>");
                      
                          for(int i=0;i<colspan-1;i++)
                         {
                          errorMessage.append("<td></td>"); 
                         } 
                         errorMessage.append("</tr></table>");
                      
                       }
                    }
                    else if ("2".equalsIgnoreCase(ratesReportDOB.getShipmentMode()))
                    {
                        if("FLAT".equalsIgnoreCase(ratesReportDOB.getWtBreak()))
                       {
                        errorMessage.append("<table width='100%' border=0 cellspacing='0' cellpadding='0' bgcolor='#d3d3d3'><tr>");
                        for(int i=0;i<colspan;i++)
                         {
                          errorMessage.append("<td></td>"); 
                         }
                           errorMessage.append("</tr><tr>");
                         errorMessage.append("<td align='left' border=0 cellpadding=0 cellspacing=0 bgcolor='#d3d3d3'<b>Sea - Flat Charges</b></td></tr><tr>");
                      
                          for(int i=0;i<colspan-1;i++)
                         {
                          errorMessage.append("<td></td>"); 
                         } 
                         errorMessage.append("</tr></table>");
                      
                       }
                       else  if("SLAB".equalsIgnoreCase(ratesReportDOB.getWtBreak()))
                       {
                          errorMessage.append("<table width='100%' border=0 cellspacing='0' cellpadding='0' bgcolor='#d3d3d3'><tr>");
                        for(int i=0;i<colspan;i++)
                         {
                          errorMessage.append("<td></td>"); 
                         }
                           errorMessage.append("</tr><tr>");
                         errorMessage.append("<td align='left' border=0 cellpadding=0 cellspacing=0 bgcolor='#d3d3d3'><b>Sea - Slab Charges</b></td></tr><tr>");
                      
                          for(int i=0;i<colspan-1;i++)
                         {
                          errorMessage.append("<td></td>"); 
                         } 
                         errorMessage.append("</tr></table>");
                      
                       }
                       else
                       {
                           errorMessage.append("<table width='100%' border=0 cellspacing='0' cellpadding='0' bgcolor='#d3d3d3'><tr>");
                        for(int i=0;i<colspan;i++)
                         {
                          errorMessage.append("<td></td>"); 
                         }
                           errorMessage.append("</tr><tr>");
                         errorMessage.append("<td align='left' border=0 cellpadding=0 cellspacing=0 bgcolor='#d3d3d3'><b>Sea - List Charges</b></td></tr><tr>");
                      
                          for(int i=0;i<colspan-1;i++)
                         {
                          errorMessage.append("<td></td>"); 
                         } 
                         errorMessage.append("</tr></table>");
                      
                    }
                    }
                    else
                    {
                        if("FLAT".equalsIgnoreCase(ratesReportDOB.getWtBreak()))
                       {
                         errorMessage.append("<table width='100%' border=0 cellspacing='0' cellpadding='0' bgcolor='#d3d3d3'><tr>");
                        for(int i=0;i<colspan;i++)
                         {
                          errorMessage.append("<td></td>"); 
                         }
                           errorMessage.append("</tr><tr>");
                         errorMessage.append("<td align='left' border=0 cellpadding=0 cellspacing=0 bgcolor='#d3d3d3'><b>Truck - Flat Charges</b></td></tr><tr>");
                      
                          for(int i=0;i<colspan-1;i++)
                         {
                          errorMessage.append("<td></td>"); 
                         } 
                         errorMessage.append("</tr></table>");
                      
                       }
                       else  if("SLAB".equalsIgnoreCase(ratesReportDOB.getWtBreak()))
                       {
                        errorMessage.append("<table width='100%' border=0 cellspacing='0' cellpadding='0' bgcolor='#d3d3d3'><tr>");
                        for(int i=0;i<colspan;i++)
                         {
                          errorMessage.append("<td></td>"); 
                         }
                           errorMessage.append("</tr><tr>");
                         errorMessage.append("<td align='left' border=0 cellpadding=0 cellspacing=0 bgcolor='#d3d3d3'><b>Truck - Slab Charges</b></td></tr><tr>");
                      
                          for(int i=0;i<colspan-1;i++)
                         {
                          errorMessage.append("<td></td>"); 
                         } 
                         errorMessage.append("</tr></table>");
                       }
                       else
                       {
                          errorMessage.append("<table width='100%' border=0 cellspacing='0' cellpadding='0' bgcolor='#d3d3d3'><tr>");
                         for(int i=0;i<colspan;i++)
                         {
                          errorMessage.append("<td></td>"); 
                         }
                         errorMessage.append("</tr><tr>");
                         errorMessage.append("<td align='left' border=0 cellpadding=0 cellspacing=0 bgcolor='#d3d3d3'><b>Truck - List Charges</b></td></tr><tr>");
                          for(int i=0;i<colspan-1;i++)
                         {
                          errorMessage.append("<td></td>"); 
                         } 
                         errorMessage.append("</tr></table>");
                       }
                    
                    }
                     
                     
                     //Added By Kishore Podili For QuoteGroupExcel Report with Charges on 08-Feb-11
                     if(orgDestChargesList.size()!=0){
                    	 
                    	 errorMessage.append("<table width='100%' border=1 cellspacing='0' cellpadding='0'><tr></tr>");
	                     
                        errorMessage.append("<tr> </tr><tr bgcolor='#FFD700'>");
	                     errorMessage.append("<td align='left' border=0 cellpadding=0 cellspacing=0 ><b> Origin Charges</b></td></tr>");
	                      /*for(int j=0;j<11;j++)
	                     {
	                      errorMessage.append("<tr><td></td> </tr>"); 
	                     } */
	                      
	                     errorMessage.append("<tr bgcolor='#FFD700'>");
	                     errorMessage.append("<td><b>QS Quote Ref #</b></td>");
	                     errorMessage.append("<td><b>Origin</b></td>");
	                     errorMessage.append("<td><b>Charge Name</b></td>");
	                     errorMessage.append("<td><b>External Name</b></td>");
	                     //errorMessage.append("<td><b>Defined By</b></td>");
	                     errorMessage.append("<td><b>Breakpoint</b></td>");
	                     errorMessage.append("<td><b>Currency</b></td>");
	                   /*  errorMessage.append("<td><b>Buy Rate</b></td>");
	                     errorMessage.append("<td><b>RSR</b></td>");*/
	                     errorMessage.append("<td><b>Sell Rate</b></td>");
	                     errorMessage.append("<td><b>Basis</b></td>");
	                     errorMessage.append("<td><b>Ratio</b></td>");
	                     errorMessage.append("</tr>");
                    	 
	                     int orgDestChargesListSize	=	orgDestChargesList.size();
	                     String	origin				=   "";
	                     String tempOrigin			=	"";
	                     String	tempQuoteId			= 	"";
	                     String	quoteId				= 	"";
	                     String	chargeDesc			= 	"";
	                     String	externalName		= 	"";
	                     String	tempChargeDesc		= 	"";
	                     String definedBy			=   "";
	                     String tempDefinedBy		=   "";
	                     String currency			=   "";
	                     String tempCurrency		=   "";
	                     String	ratio				= 	"";
	                     String	tempExternalName	= 	"";
	                     
	                     for(int i=0;i<orgDestChargesListSize;i++)
	                     {
	                    	 orgDestChargesDOB  = 	orgDestChargesList.get(i);
	                    	 QuoteIdset 		= (HashSet)quoteMap.get(ratesReportDOB.getWtBreak()+ratesReportDOB.getShipmentMode());
	                    	 quoteId 		    = 	orgDestChargesDOB.getQuoteId();
	                    	 
	                    	 if(("Origin".equalsIgnoreCase(orgDestChargesDOB.getChargeAt() ) 
	                    			 || "Pickup".equalsIgnoreCase(orgDestChargesDOB.getChargeAt()))
                               && QuoteIdset.contains(quoteId)){
	                    		 quoteId ="";
	                    		 
	                    		origin			   = 	orgDestChargesDOB.getOrigin();
	                    		 quoteId 		   = 	orgDestChargesDOB.getQuoteId();
		                    	 chargeDesc 	   = 	orgDestChargesDOB.getChargeName();
		                    	 externalName	   = 	orgDestChargesDOB.getExternalChargeName();
		                    	 externalName	   =    null!=externalName?externalName:"";
		                    	 definedBy		   = 	orgDestChargesDOB.getDefinedBy();
		                    	 currency		   =	orgDestChargesDOB.getCurrency();
		                    	 ratio 			   = 	orgDestChargesDOB.getRatio();
		                    	 ratio			   =	("".equals(ratio) || null==ratio)?"":"1 CBM = "+ratio+" KGS"; //Modified by Kishore For QuoteGroupExcel Report Changes ;
		                    	 if(("".equals(tempQuoteId) || !tempQuoteId.equals(quoteId))){
		                    		 tempQuoteId 	= quoteId;
		                    		 tempOrigin		= "";
		                    	 }else
		                    		 quoteId = "";
		                    		
		                    	 if("".equals(tempOrigin) || !tempOrigin.equals(origin)){
		                    		 tempOrigin		= origin;
		                    		 tempChargeDesc = "";
		                    	 }else
		                    		 origin		= "";
		                    	 
		                    	 if("".equals(tempChargeDesc) || !tempOrigin.equals(chargeDesc)){
		                    		 tempChargeDesc = chargeDesc;
		                    		 tempExternalName = "";
		                    	 }else
		                    		 origin		= "";
		                    	 
		                    	 	                    		 
		                    	
		                    	 if("".equals(tempExternalName) || !tempExternalName.equals(externalName)){
		                    		 tempExternalName = externalName;
		                    		 tempDefinedBy="";
		                    	}else
		                    		externalName = "";
		                    	 
		                    	 
		                    	 
		                    	if("".equals(tempDefinedBy) || !tempDefinedBy.equals(definedBy)){
		                    		 tempDefinedBy = definedBy;
		                    		 tempCurrency  = "";
		                    	 }else
		                    		 definedBy = "";
		                    	 
		                    	 if("".equals(tempCurrency) || !tempCurrency.equals(currency))
		                    		 tempCurrency = currency;
		                    	 else
		                    		 currency = "";
		                    	 
		                	  	 errorMessage.append("<tr>");
		                         errorMessage.append("<td>"+quoteId+"</td>");
		                         errorMessage.append("<td>"+origin+"</td>");		                         
		                    	 errorMessage.append("<td>"+chargeDesc+"</td>");
		                    	 errorMessage.append("<td>"+externalName+"</td>");
		                    	// errorMessage.append("<td>"+definedBy +"</td>");
		                    	 errorMessage.append("<td>"+orgDestChargesDOB.getBreakPoint()+"</td>");
		                    	 errorMessage.append("<td>"+currency+"</td>");
		                    /*   errorMessage.append("<td>"+Double.parseDouble(orgDestChargesDOB.getBuyRate())+"</td>");
		                    	 errorMessage.append("<td>"+Double.parseDouble(orgDestChargesDOB.getRsr())+"</td>");
		                    */   errorMessage.append("<td>"+Double.parseDouble(orgDestChargesDOB.getSellRate())+"</td>");
		                    	 errorMessage.append("<td>"+orgDestChargesDOB.getBasis()+"</td>");
		                    	 errorMessage.append("<td>"+ ratio+"</td>");
		                    	 errorMessage.append("</tr>");
	                      }
	                     
	                     }
	                     
	                     errorMessage.append("</table>");
                     }

                     
                     // End of  Kishore Podili For QuoteGroupExcel Report with Charges on 08-Feb-11

                     
                      errorMessage.append("<table width='100%' border=1 cellspacing='0' cellpadding='0'>");
                      errorMessage.append("<tr> </tr> <tr bgcolor='#FFD700'><td align='left' border=0 cellpadding=0 cellspacing=0 ><b> Freight Charges</b></td></tr>");
                      
                      /*for(int i=0;i<colspan;i++)
                      {
                       errorMessage.append(" <tr><td></td> </tr>"); 
                      }*/
                      
                      errorMessage.append("<tr bgcolor='#FFD700'>");
                       errorMessage.append("<td><b>QS Quote Ref #</b></td>");
                         errorMessage.append("<td><b>Quote Validity</b></td>");//added for 181328
                         // commented by subrahmanya for change after delivery 181328
                       // errorMessage.append("<td><b>Validity</b></td>");
                        errorMessage.append("<td><b>Origin Country</b></td>");
                       if("1".equalsIgnoreCase(ratesReportDOB.getShipmentMode())||"4".equalsIgnoreCase(ratesReportDOB.getShipmentMode()))
                      {
                        errorMessage.append("<td><b>Origin</b></td>");
                         errorMessage.append("<td><b>Destination Country</b></td>");
                        errorMessage.append("<td><b>Destination</b></td>");
                      }
                      else
                      { 
                        errorMessage.append("<td><b>Origin Port</b></td>");
                        errorMessage.append("<td><b>Destination Country</b></td>");
                        errorMessage.append("<td><b>Destination Port</b></td>");
                      }
                        errorMessage.append("<td><b>Carrier</b></td>");
                         errorMessage.append("<td><b>Service Level</b></td>");
                        errorMessage.append("<td><b>Frequency</b></td>");
                          if("2".equalsIgnoreCase(ratesReportDOB.getShipmentMode()))
                          {
                              errorMessage.append("<td><b>Transit Days(Approx.)</b></td>");
                          }
                          else
                          {
                             errorMessage.append("<td><b>Transit Time - hour(Approx.)</b></td>");
                       
                          }
                        errorMessage.append("<td><b>Currency</b></td>");
                        if("2".equalsIgnoreCase(ratesReportDOB.getShipmentMode())&&"LIST".equalsIgnoreCase(ratesReportDOB.getWtBreak()))
                        {
                            errorMessage.append("<td><b>Container&nbsp;Type</b></td>");
                            errorMessage.append("<td><b>Freight per Container</b></td>");
                            errorMessage.append("<td><b>BAF per Container</b></td>");
                            errorMessage.append("<td><b>CAF per Container</b></td>");
                            errorMessage.append("<td><b>CSF per Container</b></td>");
                            errorMessage.append("<td><b>PSS per Container</b></td>");
                            
                        }
                        else
                        {
                              for(int j=0;j<ratesReportDOB.getWtBreakList().size();j++)
                              {
                                  if("MIN".equalsIgnoreCase((String)ratesReportDOB.getWtBreakList().get(j)))
                                  {
                                      errorMessage.append("<td><b>"+(String)ratesReportDOB.getWtBreakList().get(j)+"</font></b></td>");
                                  }
                                  else
                                  {
                                     if("2".equals(ratesReportDOB.getShipmentMode()))
                                    {
                                      errorMessage.append("<td><b>"+(String)ratesReportDOB.getWtBreakList().get(j)+" per cbm</b></td>");
                                    }
                                    else
                                    {
                                      errorMessage.append("<td><b>"+(String)ratesReportDOB.getWtBreakList().get(j)+" per kg</b></td>");
                                    }
                                    
                             
                                  }
                              }
                        }
                       // added by subrahmanya for change after delivery 181328
                        errorMessage.append("<td><b>FreightRate Validity</b></td>");//ended
                         errorMessage.append("<td><b>Density Ratio</b></td>");
                          //Modified by Mohan for Issue No.219976 on 28-10-2010
                         errorMessage.append("<td><b>Internal Notes</b></td>");
                         errorMessage.append("<td><b>External Notes</b></td>");
                           if("on".equalsIgnoreCase(spotRateFlag))
                        {
                           errorMessage.append("<td><b>Spot Rates</b></td>");
                        }
                        errorMessage.append("</tr>");
                        int rateListSIze	=	ratesList.size();
                        //@Added by silpa.p on 4-may-11 for modify the repeated lanes (Duplicated Data)
                        String	tempQuoteIdforFreight			= 	"";
	                     String	quoteIdforFreight				= 	"";
	                     String tempQuoteValidUpto				=	"";
	                     String  quoteValidUpto     			=   "";
	                     String tempOriginCountry   			=   "";
	                     String originCountryforFreight			=	"";
	                     String tempOrigin						=	"";
	                     String origin 							=	"";
	                     String tempDestCountry					=	"";
	                     String destCountryforFreight			=	"";
	                     String tempDestinationforFreight		=	"";
	                     String destinationforFreight			=	"";
	                     String tempCarrierId					=	"";
	                     String carrierid						=	"";
	                     String tempServiceLevelId				=	"";
	                     String serviceLevelId 					=	"";
                      for(int i=0;i<rateListSIze;i++)
                      {
                           ratesReportDOB = (QMSRatesReportDOB)ratesList.get(i);
                           quoteIdforFreight		   = 	ratesReportDOB.getQuoteId();
                          dateUtility.setPattern("DD-MON-YY");
                          if(ratesReportDOB.getQuoteValidUpto()!=null&&ratesReportDOB.getQuoteValidUpto().toString().trim().length()>0){
                        	 
                        	  quoteValidUpto=dateUtility.getDisplayString(ratesReportDOB.getQuoteValidUpto());
                          }else{
                        	  quoteValidUpto="";
                          }
                          originCountryforFreight=ratesReportDOB.getOriginCountry();
                          origin = ratesReportDOB.getOrigin();
                          destCountryforFreight=ratesReportDOB.getDestCountry();
                          destinationforFreight=ratesReportDOB.getDestination();
                       if(ratesReportDOB.getCarrierId()!=null&&ratesReportDOB.getCarrierId().trim().length()>0)
                          {
                              carrierid=ratesReportDOB.getCarrierId();
                         }
                       else{
                    	   carrierid="";
                       }
                      if(ratesReportDOB.getServiceLevelId()!=null&&ratesReportDOB.getServiceLevelId().trim().length()>0)
                       {
                    	  serviceLevelId=ratesReportDOB.getServiceLevelId();
                       }
                      else
                      {
                    	  serviceLevelId="";
                      }
                  
                       if(("".equals(tempQuoteIdforFreight) || !tempQuoteIdforFreight.equals(quoteIdforFreight	))){
	                    		 tempQuoteIdforFreight 	= quoteIdforFreight	;
	                    		 tempQuoteValidUpto	= "";
	                    	 }else
	                    		 quoteIdforFreight	 = "";
                           
                           if(("".equals(tempQuoteValidUpto) || ! tempQuoteValidUpto.equals(quoteValidUpto))){
                        	   tempQuoteValidUpto	= quoteValidUpto;
	                    		 tempOriginCountry	= "";
	                    	 }else
	                    		 quoteValidUpto = "";
                           
                           if(("".equals(tempOriginCountry) || ! tempOriginCountry.equals(originCountryforFreight))){
                        	   tempOriginCountry	= originCountryforFreight;
	                    		 tempOrigin	= "";
	                    	 }else
	                    		 originCountryforFreight = "";
                           
                           if(("".equals(tempOrigin) || ! tempOrigin.equals(origin))){
                        	   tempOrigin	= origin;
	                    		 tempDestCountry	= "";
	                    	 }else
	                    		 origin = "";
                           
                           if(("".equals(tempDestCountry) || ! tempDestCountry.equals(destCountryforFreight))){
                        	   tempDestCountry	=  destCountryforFreight;
                        	   tempDestinationforFreight	= "";
	                    	 }else
	                    		 destCountryforFreight = "";
                           if(("".equals(tempDestinationforFreight) || ! tempDestinationforFreight.equals(destinationforFreight))){
                        	   tempDestinationforFreight	=  destinationforFreight;
	                    		 tempCarrierId	= "";
	                    	 }else
	                    		 destinationforFreight = "";
                           if(("".equals(tempCarrierId) || ! tempCarrierId.equals(carrierid))){
                        	   tempCarrierId	=  carrierid;
	                    		  tempServiceLevelId	= "";
	                    	 }else
	                    		 carrierid = "";
                           if(("".equals( tempServiceLevelId) || !  tempServiceLevelId.equals(serviceLevelId))){
                        	   tempServiceLevelId	=  serviceLevelId;
	                    	 }else
	                    		 serviceLevelId = "";
	                    
                           errorMessage.append("<tr border='1'>");
                           //errorMessage.append("<td>"+ratesReportDOB.getQuoteId()+"</td>");
                           errorMessage.append("<td>"+quoteIdforFreight	+"</td>");
                           // dateUtility.setPatternWithTime("DD/MM/YY");
                           //dateUtility.setPattern("DD-MON-YY");
                           //strn  = null;
                            //added by subrahmanyam for 181328
                            //String strn1  = null;
                           /*if(ratesReportDOB.getQuoteValidUpto()!=null&&ratesReportDOB.getQuoteValidUpto().toString().trim().length()>0)
                           {

                              strn1  = dateUtility.getDisplayString(ratesReportDOB.getQuoteValidUpto());

                           }
                            if(strn1!=null)
                            {
                                errorMessage.append("<td align='LEFT'>"+strn1+"</td>");
                            }
                            else
                            {
                                errorMessage.append("<td>&nbsp;</td>");
                            }*/
                        
                            	errorMessage.append("<td align='LEFT'>"+quoteValidUpto+"</td>"); 
                            //commented by subrahmanyam for changes after delivering the 181328
                           //ended by subrahmanyam for 181328
                           /*
                             if(ratesReportDOB.getValidUpto()!=null&&ratesReportDOB.getValidUpto().toString().trim().length()>0)
                           {
                              strn  = dateUtility.getDisplayString(ratesReportDOB.getValidUpto());
                           }
                            if(strn!=null)
                            {
                                errorMessage.append("<td>"+strn+"</td>");
                            }
                            else
                            {
                                errorMessage.append("<td>&nbsp;</td>");
                            }
                            */
                           //ended by subrahmanyam for changes after delivering the 181328

                      /*SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
              
                               if(ratesReportDOB.getValidUpto()!=null)
                               {
                               errorMessage.append("<td>" +  formatter.format(ratesReportDOB.getValidUpto())+"</td>");
                               }
                               else
                               {
                                errorMessage.append("<td>&nbsp;</td>");
                               }*/
                               if(i==12)
                               {
                               logger.info("errorMessage  :"+errorMessage.toString());
                               }
                           /*errorMessage.append("<td>"+ratesReportDOB.getOriginCountry()+"</td>");
                           errorMessage.append("<td>"+ratesReportDOB.getOrigin()+"</td>");
                             errorMessage.append("<td>"+ratesReportDOB.getDestCountry()+"</td>");
                             errorMessage.append("<td>"+ratesReportDOB.getDestination()+"</td>");*/
                             errorMessage.append("<td>"+originCountryforFreight+"</td>");
                             errorMessage.append("<td>"+origin+"</td>");
                             errorMessage.append("<td>"+ destCountryforFreight+"</td>");
                             errorMessage.append("<td>"+destinationforFreight+"</td>");
                             /*if(ratesReportDOB.getCarrierId()!=null&&ratesReportDOB.getCarrierId().trim().length()>0)
                             {
                                  errorMessage.append("<td>"+ratesReportDOB.getCarrierId()+"</td>");
                             }
                             else
                             {
                                  errorMessage.append("<td>&nbsp;</td>");
                             }*/
                             errorMessage.append("<td>"+carrierid+"</td>");
                             //@@Added by Kameswari for the WPBN issue-178965 on 12/08/09
                              /* if(ratesReportDOB.getServiceLevelId()!=null&&ratesReportDOB.getServiceLevelId().trim().length()>0)
                             {
                                  errorMessage.append("<td>"+ratesReportDOB.getServiceLevelId()+"</td>");
                             }
                             else
                             {
                                  errorMessage.append("<td>&nbsp;</td>");
                             }
                             */
                             //@WPBN issue-178965 on 12/08/09
                             errorMessage.append("<td>"+ serviceLevelId+"</td>");
                             //ended by silpa.p on 4-may-11 
                             //@WPBN issue-178965 on 12/08/09
                             if(ratesReportDOB.getFrequency()!=null&&ratesReportDOB.getFrequency().trim().length()>0)
                             {
                            	 String frequncey = ratesReportDOB.getFrequency();
                            	
                            	frequncey = frequncey.replaceAll("1", "Monday");
                         		frequncey = frequncey.replaceAll("2", "Tuesday");
                         		frequncey = frequncey.replaceAll("3", "Wednesday");
                         		frequncey = frequncey.replaceAll("4", "Thursday");
                         		frequncey = frequncey.replaceAll("5", "Friday");
                         		frequncey = frequncey.replaceAll("6", "Saturday");
                         		frequncey = frequncey.replaceAll("7", "Sunday");
                            	 
                            	 
                            	 errorMessage.append("<td>"+frequncey+"</td>");
                             }
                             else
                             {
                                  errorMessage.append("<td>&nbsp;</td>");
                             }
                               if(ratesReportDOB.getTransitTime()!=null&&ratesReportDOB.getTransitTime().trim().length()>0)
                             {
                                  errorMessage.append("<td>"+ratesReportDOB.getTransitTime()+"</td>");
                             }
                             else
                             {
                                  errorMessage.append("<td>&nbsp;</td>");
                             }
                            errorMessage.append("<td>"+ratesReportDOB.getCurrency()+"</td>");
                            if(ratesReportDOB.getDensityCode()!=null)
                            {
                             if("KG".equals(ratesReportDOB.getDensityType()))
                           {
                                temp= "1 CBM ="+ ratesReportDOB.getDensityCode()+" KGS";
                           }
                           else
                                  temp= "1 CFT ="+ ratesReportDOB.getDensityCode()+" LBS";
                            }
                           
                           
                          if("2".equalsIgnoreCase(ratesReportDOB.getShipmentMode())&&"LIST".equalsIgnoreCase(ratesReportDOB.getWtBreak()))
                        {
                            errorMessage.append("<td>"+ratesReportDOB.getWtBreakSlab()+"</td>");
                             for(int j=0;j<ratesReportDOB.getRateList().length;j++)
                           {
                            //  df.format(Double.parseDouble(ratesReportDOB.getRateList()[j]))
                            //@@ modified by subrahmanyam for pbn id: 186811 on 22/oct/09
                             if(!("-".equalsIgnoreCase(ratesReportDOB.getRateList()[j]) || ("".equalsIgnoreCase(ratesReportDOB.getRateList()[j]))))
                           {
                          errorMessage.append("<td>"+df.format(Double.parseDouble(ratesReportDOB.getRateList()[j]))+"</td>");
                           }
                         /*  if(!("-".equalsIgnoreCase(ratesReportDOB.getRateList()[j])))
                           {
                            errorMessage.append("<td>"+df.format(Double.parseDouble((String)ratesReportDOB.getRateLinkedList().get(j)))+"</td>");
                           }*/
                           else
                           {
                              errorMessage.append("<td>-</td>");
                         
                           }
                               //errorMessage.append("<td>"+ratesReportDOB.getRateList()[j]+"</td>");
                           }
                        }
                        else
                        {
                            for(int j=0;j<ratesReportDOB.getRateList().length;j++)
                           {
                              // errorMessage.append("<td>"+ratesReportDOB.getRateList()[j]+"</td>");
                              //@@ modified by subrahmanyam for pbn id: 186811 on 22/oct/09
                           if(!("-".equalsIgnoreCase(ratesReportDOB.getRateList()[j])|| ("".equalsIgnoreCase(ratesReportDOB.getRateList()[j]))))
                           {
                          errorMessage.append("<td>"+df.format(Double.parseDouble(ratesReportDOB.getRateList()[j]))+"</td>");
                           }
                           /* if(!("-".equalsIgnoreCase(ratesReportDOB.getRateList()[j])))
                           {
                            errorMessage.append("<td>"+df.format(Double.parseDouble((String)ratesReportDOB.getRateLinkedList().get(j)))+"</td>");
                           }*/
                          else
                           {
                              errorMessage.append("<td>-</td>");
                         
                           }
                           }
                        }
                       //added by subrahmanyam for changes after delivering 181328
                        if(ratesReportDOB.getValidUpto()!=null&&ratesReportDOB.getValidUpto().toString().trim().length()>0)
                           {
                              strn  = dateUtility.getDisplayString(ratesReportDOB.getValidUpto());
                           }
                            if(strn!=null)
                            {
                                errorMessage.append("<td>"+strn+"</td>");
                            }
                            else
                            {
                                errorMessage.append("<td>&nbsp;</td>");
                            }
                        //ended by subrahmanyam for changes after delivering 181328

                        errorMessage.append("<td>"+temp+"</td>");
                         //Modified by Mohan for Issue No.219976 on 28-10-2010
                        errorMessage.append("<td>"+ratesReportDOB.getInternalNotes()+"</td>");
                        errorMessage.append("<td>"+ratesReportDOB.getExternalNotes()+"</td>");
                        
                        if("on".equalsIgnoreCase(spotRateFlag))
                        {
                        
                           if("SBR".equalsIgnoreCase(ratesReportDOB.getSellBuyFlag()))
                           {
                              errorMessage.append("<td>Yes</td>");
                           }
                           else
                           { 
                             errorMessage.append("<td>No</td>");
                           }
                        }  
                            errorMessage.append("</tr>");
                         
                         }
                         /*errorMessage.append("<tr><td colspan="+colspan+"></td></tr>");
                         errorMessage.append("<tr><td colspan="+colspan+"></td></tr>");*/
                           // errorMessage.append("</table>");
                      
                      errorMessage.append("</table>");
                      
                      
                      //Added By Kishore Podili For QuoteGroupExcel Report with Charges on 08-Feb-11
                      if(orgDestChargesList.size()!=0){
                    	  
                    	  
                    	  errorMessage.append("<table width='100%' border=1 cellspacing='0' cellpadding='0'><tr></tr>");
	                      
	                      errorMessage.append("<tr bgcolor='#FFD700'>");
	                      errorMessage.append("<td align='left' border=0 cellpadding=0 cellspacing=0 ><b> Destination Charges</b></td></tr>");
	                       /*for(int j=0;j<11;j++)
	                      {
	                       errorMessage.append("<tr><td></td> </tr>"); 
	                      } */
	                       
	                      errorMessage.append("<tr bgcolor='#FFD700'>");
	                      errorMessage.append("<td><b>QS Quote Ref #</b></td>");
	                      errorMessage.append("<td><b>Destination</b></td>");
	                      errorMessage.append("<td><b>Charge Name</b></td>");
	                      errorMessage.append("<td><b>External Name</b></td>");
	                     // errorMessage.append("<td><b>Defined By</b></td>");
	                      errorMessage.append("<td><b>Breakpoint</b></td>");
	                      errorMessage.append("<td><b>Currency</b></td>");
	                  /*    errorMessage.append("<td><b>Buy Rate</b></td>");
	                      errorMessage.append("<td><b>RSR</b></td>");
	             */         errorMessage.append("<td><b>Sell Rate</b></td>");
	                      errorMessage.append("<td><b>Basis</b></td>");
	                      errorMessage.append("<td><b>Ratio</b></td>");
	                      errorMessage.append("</tr>");
	                      
	                      String	tempQuoteId			= 	"";
	                      String	quoteId				= 	"";
	                      String	tempDestination		= 	"";
	                      String	destination			= 	"";
	                      String	chargeDesc			= 	"";
	                      String	externalName		= 	"";
	                      String	tempChargeDesc		= 	"";
	                      String 	definedBy			=   "";
	                      String 	tempDefinedBy		=   "";
	                      String 	currency			=   "";
	                      String 	tempCurrency		=   "";
	                      String	ratio				= 	"";
	                      String	tempExternalName	= 	"";
                    	  	
                    	  int orgDestChargesListSize	=	orgDestChargesList.size();
			                     
			                      
			                      for(int i=0;i<orgDestChargesListSize;i++)
			                      {
			                     	 orgDestChargesDOB = 	orgDestChargesList.get(i);
			                     	 QuoteIdset 		= (HashSet)quoteMap.get(ratesReportDOB.getWtBreak()+ratesReportDOB.getShipmentMode());
			                    	 quoteId 		    = 	orgDestChargesDOB.getQuoteId();
			                    	 
			                     	 
			                     	 if(("Destination".equalsIgnoreCase(orgDestChargesDOB.getChargeAt() ) 
			                     			 || "Delivery".equalsIgnoreCase(orgDestChargesDOB.getChargeAt()))
			                     	   && QuoteIdset.contains(quoteId)){
			                     		quoteId = "";
			                     		 
			                     		 quoteId 		   = 	orgDestChargesDOB.getQuoteId();
			 	                    	 chargeDesc 	   = 	orgDestChargesDOB.getChargeName();
			 	                    	 externalName	   =	orgDestChargesDOB.getExternalChargeName() ;
			 	                    	 externalName	   =    null!=externalName?externalName:"";
			 	                    	 definedBy		   = 	orgDestChargesDOB.getDefinedBy();
			 	                    	 currency		   =	orgDestChargesDOB.getCurrency();  
			 	                    	 destination	   =	orgDestChargesDOB.getDestination();
			 	                    	 ratio			   =    orgDestChargesDOB.getRatio();
			 	                    	 ratio			   =	("".equals(ratio) || null==ratio)?"":"1 CBM ="+ratio+" KGS"; //Modified by Kishore For QuoteGroupExcel Report Changes 
			 	                    	
			 	                    	 if(("".equals(tempQuoteId) || !tempQuoteId.equals(quoteId)) ){
			 	                    		 tempQuoteId 	= quoteId;
			 	                    		 tempDestination	= "";
			 	                    	}
			 	                    	else
			 	                    		 quoteId = "";
			 	                    		 
			 	                    	 
			 	                    	 if("".equals(tempDestination) || !tempDestination.equals(destination)){
			 	                    		tempDestination = destination;
			 	                    		tempChargeDesc = "";
			 	                    	 }
			 	                    	 
			 	                    	 else
			 	                    		destination =	"";
			 	                    		 
			 	                    		 
			 	                    		 
			 	                    	 if("".equals(tempChargeDesc) || !tempChargeDesc.equals(chargeDesc)){
			 	                    		 tempChargeDesc = chargeDesc;
			 	                    		tempExternalName="";
			 	                    		 
			 	                    	 }
			 	                    	 else
			 	                    		 chargeDesc = "";
			 	                    	 
			 	                    	if("".equals(tempExternalName) || !tempExternalName.equals(externalName)){
			 	                    		tempExternalName = externalName;
			 	                    		 tempDefinedBy="";
			 	                    		 
			 	                    	 }
			 	                    	 else
			 	                    		externalName = "";
			 	                    	 
			 	                    	 
			 	                    	 if("".equals(tempDefinedBy) || !tempDefinedBy.equals(definedBy)){
			 	                    		 tempDefinedBy = definedBy;
			 	                    		 tempCurrency  = "";
			 	                    	 }
			 	                    	 else
			 	                    		 definedBy = "";
			 	                    	 
			 	                    	 if("".equals(tempCurrency) || !tempCurrency.equals(currency))
			 	                    		 tempCurrency = currency;
			 	                    	 else
			 	                    		 currency = "";
			 	                    	 
			 	                	  	 errorMessage.append("<tr>");
			 	                         errorMessage.append("<td>"+quoteId+"</td>");
			 	                         errorMessage.append("<td>"+destination+"</td>");
			 	                         errorMessage.append("<td>"+chargeDesc+"</td>");
			 	                         errorMessage.append("<td>"+externalName+"</td>");
			 	                    	// errorMessage.append("<td>"+definedBy +"</td>");
			 	                    	 errorMessage.append("<td>"+orgDestChargesDOB.getBreakPoint()+"</td>");
			 	                    	 errorMessage.append("<td>"+currency+"</td>");
			 	                    	/*  errorMessage.append("<td>"+Double.parseDouble(orgDestChargesDOB.getBuyRate())+"</td>");
			 	                    	errorMessage.append("<td>"+Double.parseDouble(orgDestChargesDOB.getRsr())+"</td>");*/ 
			 	                    	 //@Added by kiran.v on 29-jan-2013
			 	                    	 errorMessage.append("<td>"+Double.parseDouble(orgDestChargesDOB.getSellRate()!=null ? orgDestChargesDOB.getSellRate() : "0.00")+"</td>");
			 	                    	errorMessage.append("<td>"+orgDestChargesDOB.getBasis()+"</td>");
			 	                    	 errorMessage.append("<td>"+ratio+"</td>");
			 	                    	 errorMessage.append("</tr>");
			                       }
			                      
			                      }
			                      
			                      errorMessage.append("</table>");
                      }
                     }

                      
                      // End of  Kishore Podili For QuoteGroupExcel Report with Charges on 08-Feb-11
                      
                      
                      
                       notes = (String[])totalList.get(0);
                       shmode= (ArrayList)totalList.get(1);
                    
                      if(notes!=null&&notes.length>0&&totalList.size()>1)
                      {
                          errorMessage.append("<table border=0 cellspacing=0 cellpadding=0 ><tr></tr>");
                            errorMessage.append("<tr><td>"+"<b>General Terms and Conditions :</b></td></tr>");
                            for(int i=0;i<colspan-1;i++)
                             {
                                errorMessage.append("<td></td>"); 
                             } 
                               
                             errorMessage.append("</tr>"); 
                              errorMessage.append("</table>");
                    int noteLen	=	notes.length;
                      for (int n=0;n<noteLen;n++)
                         {
                           number =n+1;
                         
                                     
                        String[] notesArray =  notes[n].split("\\n");
                        int notesArrLen	=	notesArray.length;
                               if("1".equalsIgnoreCase((String)shmode.get(n)))
                          {
                                 if(count1==0)
                                 {
                                       errorMessage.append("<table border=0 cellspacing=0 cellpadding=0 >");
                                       errorMessage.append("<tr><td nowrap>"+"<b>Air Freight</b></td>");
                                       for(int i=0;i<colspan-1;i++)
                                     {
                                        errorMessage.append("<td></td>"); 
                                     } 
                                      count1++;
                                       errorMessage.append("</tr>"); 
                                         errorMessage.append("</table>");
                                 }
                                 
                                for(int k=0;k<notesArrLen;k++)
                              {
                                    errorMessage.append("<table border=0 cellspacing=0 cellpadding=0 >");
                                   errorMessage.append("<tr><td nowrap>"+notesArray[k]+"</td>");
                                  for(int i=0;i<colspan-1;i++)
                                   {
                                      errorMessage.append("<td></td>"); 
                                   } 
                                     
                                   errorMessage.append("</tr>"); 
                                     errorMessage.append("</table>");
                              }
                             
                         }
                         else if("2".equalsIgnoreCase((String)shmode.get(n)))
                          {
                             if(count2==0)
                             {
                                   errorMessage.append("<table border=0 cellspacing=0 cellpadding=0 >");
                                    errorMessage.append("<tr><td nowrap>"+"<b>Sea Freight</b></td>");
                                  count2++;
                                    for(int i=0;i<colspan-1;i++)
                                   {
                                      errorMessage.append("<td></td>"); 
                                   } 
                                     
                                   errorMessage.append("</tr>"); 
                                     errorMessage.append("</table>");
                             }
                             for(int k=0;k<notesArrLen;k++)
                            {
                                  errorMessage.append("<table border=0 cellspacing=0 cellpadding=0 >");
                                  errorMessage.append("<tr><td nowrap>"+notesArray[k]+"</td>");
                                 for(int i=0;i<colspan-1;i++)
                                 {
                                    errorMessage.append("<td></td>"); 
                                 } 
                                   
                                 errorMessage.append("</tr>"); 
                                   errorMessage.append("</table>");
                            }
                          }
                           else if("3".equalsIgnoreCase((String)shmode.get(n)))
                          {
                             if(count3==0)
                             {
                                    errorMessage.append("<table border=0 cellspacing=0 cellpadding=0 >");
                                    errorMessage.append("<tr><td nowrap>"+"<b>Truck Freight</b></td>");
                                   for(int i=0;i<colspan-1;i++)
                                 {
                                    errorMessage.append("<td></td>"); 
                                 } 
                                   
                                 errorMessage.append("</tr>"); 
                                   errorMessage.append("</table>");
                                  count3++;
                             }
                             
                            for(int k=0;k<notesArrLen;k++)
                            {
                                 errorMessage.append("<table border=0 cellspacing=0 cellpadding=0 >");
                                errorMessage.append("<tr><td nowrap>"+notesArray[k]+"</td>");
                               for(int i=0;i<colspan-1;i++)
                               {
                                  errorMessage.append("<td></td>"); 
                               } 
                                   
                                 errorMessage.append("</tr>"); 
                                   errorMessage.append("</table>");
                            }
                          }
                           else if("4".equalsIgnoreCase((String)shmode.get(n)))
                          {
                             if(count4==0)
                             {
                                   errorMessage.append("<table border=0 cellspacing=0 cellpadding=0 >");
                                   errorMessage.append("<tr><td nowrap>"+"<b>Air and Truck Freight</b></td>");
                                   for(int i=0;i<colspan-1;i++)
                                 {
                                    errorMessage.append("<td></td>"); 
                                 } 
                                   
                                 errorMessage.append("</tr>"); 
                                   errorMessage.append("</table>");
                                  count4++;
                             }
                            
                           
                             for(int k=0;k<notesArrLen;k++)
                            {
                                errorMessage.append("<table border=0 cellspacing=0 cellpadding=0 >");
                                errorMessage.append("<tr><td nowrap>"+notesArray[k]+"</td>");
                                for(int i=0;i<colspan-1;i++)
                               {
                                  errorMessage.append("<td></td>"); 
                               } 
                                 
                               errorMessage.append("</tr>"); 
                                 errorMessage.append("</table>");
                            }
                          }
                           else if("5".equalsIgnoreCase((String)shmode.get(n)))
                          {
                             if(count5==0)
                             {
                                 errorMessage.append("<table border=0 cellspacing=0 cellpadding=0 >");
                                  errorMessage.append("<tr><td nowrap>"+"<b>Sea and Truck Freight</b></td>");
                                   for(int i=0;i<colspan-1;i++)
                                 {
                                    errorMessage.append("<td></td>"); 
                                 } 
                                   
                                 errorMessage.append("</tr>"); 
                                   errorMessage.append("</table>");
                                  count5++;
                             }
                            
                         for(int k=0;k<notesArrLen;k++)
                          {
                                errorMessage.append("<table border=0 cellspacing=0 cellpadding=0 >");
                               errorMessage.append("<tr><td nowrap>"+notesArray[k]+"</td>");
                              for(int i=0;i<colspan-1;i++)
                             {
                                errorMessage.append("<td></td>"); 
                             } 
                                   
                                 errorMessage.append("</tr>"); 
                                   errorMessage.append("</table>");
                             }
                        }
                           else if("7".equalsIgnoreCase((String)shmode.get(n)))
                          {
                             if(count7==0)
                             {
                                  errorMessage.append("<table border=0 cellspacing=0 cellpadding=0 >");
                                 errorMessage.append("<tr><td nowrap>"+"<b>Air,Sea and Truck Freight</b></td>");
                                   for(int i=0;i<colspan-1;i++)
                                 {
                                    errorMessage.append("<td></td>"); 
                                 } 
                                   
                                 errorMessage.append("</tr>"); 
                                   errorMessage.append("</table>");
                                  count7++;
                             }
                          for(int k=0;k<notesArrLen;k++)
                        {
                            errorMessage.append("<table border=0 cellspacing=0 cellpadding=0 >");
                           errorMessage.append("<tr><td nowrap>"+notesArray[k]+"</td>");
                            for(int i=0;i<colspan-1;i++)
                             {
                                errorMessage.append("<td></td>"); 
                             } 
                               
                             errorMessage.append("</tr>"); 
                               errorMessage.append("</table>");
                          }
                          }
                          
                         }
                   
                      }
                      
                       errorMessage.append("</body>");
                       errorMessage.append("</html>");
                       out.print(errorMessage.toString());
                  }
                  else
                  {
                       
                       response.setContentType("application/vnd.ms-excel"); 
                       response.setHeader("Content-Disposition","attachment;filename=Exceptions.xls");                  
                        out = response.getWriter();
                
                       errorMessage.append("<html>");
                       errorMessage.append("<body>");
                       errorMessage.append("<table width='100%' border='1' cellspacing='0' cellpadding='0'>");
                       errorMessage.append("<tr><td><b>NO DATA FOUND</b></td></tr>");
                       errorMessage.append("</table>");
                       errorMessage.append("</body>");
                       errorMessage.append("</html>");
          
                       out.print(errorMessage);
                  }
              }else
              {
                  
                  response.setContentType("application/vnd.ms-excel"); 
                  response.setHeader("Content-Disposition","attachment;filename=Exceptions.xls");                  
                   out = response.getWriter();
           
                  errorMessage.append("<html>");
                  errorMessage.append("<body>");
                  errorMessage.append("<table width='100%' border='1' cellspacing='0' cellpadding='0'>");
                  errorMessage.append("<tr><td><b>NO DATA FOUND</b></td></tr>");
                  errorMessage.append("</table>");
                  errorMessage.append("</body>");
                  errorMessage.append("</html>");
     
                  out.print(errorMessage);
             }
              }
      
      }
        else
        {
            
             	nextNavigation = "reports/QMSGroupExcelReport.jsp";
            	doFileDispatch(request,response,nextNavigation);
        }
      }
      
     //added by phani sekhar for 167656
      if("FreightRates".equalsIgnoreCase(operation))
      {
      dateutil        =  new ESupplyDateUtility();
         if(subOperation==null)
        {
             	nextNavigation = "reports/QMSFreightRatesExcelEnterid.jsp";
              doFileDispatch(request,response,nextNavigation);
        }
        else if("getfreightDetails".equals(subOperation))
        {
            ratesrptDOB = new QMSRatesReportDOB();
            ratesrptDOB.setTerminalId(loginbean.getTerminalId());
             ratesrptDOB.setAcessLevel(loginbean.getUserTerminalType());
            
            ratesrptDOB.setOrigin(StringUtility.noNull(request.getParameter("fromLocation")));
            ratesrptDOB.setDestination(StringUtility.noNull(request.getParameter("toLocation")));
            ratesrptDOB.setServiceLevelId(StringUtility.noNull(request.getParameter("serviceLevelId")));
            ratesrptDOB.setShipmentMode(StringUtility.noNull(request.getParameter("shipmentMode")));
            frmDate= request.getParameter("fromDate");
            ratesrptDOB.setFrmDate(dateutil.getTimestampWithTime(format, frmDate, "00:00"));
            toDate = request.getParameter("toDate");
           ratesrptDOB.setToDate(dateutil.getTimestampWithTime(format, toDate, "23:59"));
            ratesrptDOB.setCustomerId(StringUtility.noNull(request.getParameter("customerId")));
            ratesrptDOB.setWeightBreak(request.getParameter("weightBreak").toUpperCase());
            ratesrptDOB.setRateType(request.getParameter("rateType").toUpperCase());  
            ratesrptDOB.setOriginCountry(StringUtility.noNull(request.getParameter("fromCountry")));
            ratesrptDOB.setDestCountry(StringUtility.noNull(request.getParameter("toCountry")));
            ratesrptDOB.setSalesPerson(request.getParameter("salesPerson"));//Added by Anil.k for Issue 236362 on 24Feb2011

            errors = remote.validateFreightReportData(ratesrptDOB);
            if(errors.length()==0)
            {
             mainDataList=remote.getFreightRatesExcelDetails(ratesrptDOB);
            
              if("Excel".equalsIgnoreCase(request.getParameter("format")))
            {
              nextNavigation        = null;
              String str[]          = null;
              StringBuffer sb       = null;
               shipmentMode   = "";
               
              ArrayList headerList,chargesList = null;
             QMSRatesReportDOB detailsDOB = null;
              if(mainDataList!=null && mainDataList.size()>0)
              {       		
                headerList		   =	(ArrayList)mainDataList.get(0);
                dataList	           =	(ArrayList)mainDataList.get(1);		
               
                out  =  response.getWriter();
              
                response.setContentType("application/vnd.ms-excel");	
                
            //    String contentDisposition = " :attachment;";	
                response.setHeader("Content-Disposition","attachment;filename=FreightRatesReport.xls");
                sb   =   new StringBuffer();
                sb.append("<html>");
                sb.append("<body>");
                sb.append("<table width='100%' border='1' cellspacing='0' cellpadding='0'>");              
                sb.append("<tr align='center'>");
               // 
               sb.append("<td><b>SNO</b></td>");
               sb.append("<td><b>QUOTE CREATION DATE</b></td>");
               sb.append("<td><b>QUOTE MODIFIED DATE</b></td>");//Added by Anil.k for Issue 236362 on 24Feb2011
               sb.append("<td><b>CUSTOMER REQUEST DATE</b></td>");//Added by Anil.k for Issue 236362 on 24Feb2011
                sb.append("<td><b>QUOTE VALIDITY</b></td>"); 
                sb.append("<td><b>QUOTE ID</b></td>");  
                sb.append("<td><b>CREATED BY</b></td>");//Added by Anil.k for Issue 236362 on 24Feb2011
                sb.append("<td><b>SALES PERSON</b></td>");//Added by Anil.k for Issue 236362 on 24Feb2011
                 sb.append("<td><b>CUSTOMER ID</b></td>");
                sb.append("<td><b>CUSTOMER NAME</b></td>");
                sb.append("<td><b>ORIGIN COUNTRY</b></td>");
                sb.append("<td><b>ORIGIN</b></td>");
                sb.append("<td><b>DESTINATION COUNTRY</b></td>");
                sb.append("<td><b>DESTINATION</b></td>");                
                sb.append("<td><b>SHIPMENT MODE</b></td>");                
                sb.append("<td><b>CARRIER ID</b></td>");
                sb.append("<td><b>SERVICE LEVEL</b></td>"); 
                sb.append("<td><b>CURRENCY </b></td>");
                int headListSize	=	headerList.size();
                for(int i=0;i<headListSize;i++)
                {
                      //  sb.append("<td><b>(String)headerList.get(i)</b></td>");
                        sb.append("<td><b>").append((String)headerList.get(i)).append("</b></td>");
                }
                sb.append("<td><b>FREIGHT RATE VALIDITY</b></td>");
                sb.append("<td><b>DENSITY RATI0</b></td>");
                 //Modified by Mohan for Issue No.219976 on 28-10-2010
                sb.append("<td><b>INTERNAL NOTES</b></td>");
                sb.append("<td><b>EXTERNAL NOTES</b></td>");
                sb.append("</tr>");
                int dataListSize	=	dataList.size();
                int jr=0;
                for(int i=0;i<dataListSize;i++)
                {
                	
                    sb.append("<tr align='center'>");
                    detailsDOB  = (QMSRatesReportDOB)dataList.get(i);
                    
                    if("1".equalsIgnoreCase(detailsDOB.getShipmentMode()))
                        shipmentMode  = "Air";
                    else if("2".equalsIgnoreCase(detailsDOB.getShipmentMode()))
                        shipmentMode  = "Sea";
                    else if("4".equalsIgnoreCase(detailsDOB.getShipmentMode()))
                        shipmentMode  = "Truck";
                    else if("100".equalsIgnoreCase(detailsDOB.getShipmentMode()))
                        shipmentMode  = "Multi-Modal";
                        
                    

                   // sb.append("<td>").append(detailsDOB.getQuoteId()).append("</td>");
                   if(("Y".equalsIgnoreCase(detailsDOB.getMultiQuote()) && !detailsDOB.getQuoteId().equalsIgnoreCase(quote_id))|| "N".equalsIgnoreCase(detailsDOB.getMultiQuote())){
                    sb.append("<td>").append(jr+1).append("</td>");                   
                   dateutil.setPatternWithTime("DD/MM/YY");
                   sb.append("<td>").append(dateutil.getDisplayString(detailsDOB.getCreationDate())).append("</td>");
                 //Added by Anil.k for Issue 236362 on 24Feb2011
                   sb.append("<td>").append(dateutil.getDisplayString(detailsDOB.getModifiedDate())).append("</td>");
                   sb.append("<td>").append(dateutil.getDisplayString(detailsDOB.getCustomerReqDate())).append("</td>");
                 //Ended by Anil.k for Issue 236362 on 24Feb2011
                   sb.append("<td>").append(dateutil.getDisplayString(detailsDOB.getQuoteValidDate())).append("</td>");  
                    sb.append("<td>").append(detailsDOB.getQuoteId()).append("</td>");
                  //Added by Anil.k for Issue 236362 on 24Feb2011
                    sb.append("<td>").append(detailsDOB.getCreatedBy()).append("</td>"); 
                    sb.append("<td>").append(detailsDOB.getSalesPerson()).append("</td>"); 
                  //Ended by Anil.k for Issue 236362 on 24Feb2011
                    sb.append("<td>").append(detailsDOB.getCustomerId()).append("</td>");                    
                    sb.append("<td>").append(detailsDOB.getCustomerName()).append("</td>");
                    jr=jr+1;
                   }else{
                	    sb.append("<td></td>");                   
                        sb.append("<td></td>");
                        sb.append("<td></td>");
                        sb.append("<td></td>");
                        sb.append("<td></td>");  
                        sb.append("<td></td>");
                        sb.append("<td></td>"); 
                        sb.append("<td></td>"); 
                        sb.append("<td></td>");                    
                        sb.append("<td></td>");  
                   }
                     sb.append("<td>").append(detailsDOB.getOriginCountry()).append("</td>");
                     sb.append("<td>").append(detailsDOB.getOrigin()).append("</td>");
                     sb.append("<td>").append(detailsDOB.getDestCountry()).append("</td>");
                     sb.append("<td>").append(detailsDOB.getDestination()).append("</td>");
                    sb.append("<td>").append(shipmentMode).append("</td>");                       
                     sb.append("<td>").append(StringUtility.noNull(detailsDOB.getCarrierId())).append("</td>");  
                    sb.append("<td>").append(StringUtility.noNull(detailsDOB.getServiceLevelId())).append("</td>");                                          
                    sb.append("<td>").append(detailsDOB.getCurrency()).append("</td>");
                  chargesList = detailsDOB.getChargeRateList();
                  int chargListSize	=	chargesList.size();
                  for(int j=0;j<chargListSize;j++)
                  {
                      if(!"NA".equals((String)chargesList.get(j)) && !"".equals((String)chargesList.get(j))) // Added by Gowtham
                     // sb.append("<td>").append(((Double)chargesList.get(j)).doubleValue()).append("</td>");
                     sb.append("<td>").append(df.format(Double.parseDouble((String)chargesList.get(j)))).append("</td>");
                     else
                     sb.append("<td>").append((String)chargesList.get(j)).append("</td>");
                      
                  }
                    sb.append("<td>").append(dateutil.getDisplayString(detailsDOB.getFreightValidDate())).append("</td>");                                        
                if(detailsDOB.getDensityCode()!=null)
                {
                  if("KG".equals(detailsDOB.getDensityType()))
                 {
                 temp= "1 CBM ="+ detailsDOB.getDensityCode()+" KGS";
                 }
                 else
                 temp= "1 CFT ="+ detailsDOB.getDensityCode()+" LBS";
                }
                 sb.append("<td>"+temp+"</td>");
                  //Modified by Mohan for Issue No.219976 on 28-10-2010
                 sb.append("<td>").append(detailsDOB.getInternalNotes()).append("</td>");
                 sb.append("<td>").append(detailsDOB.getExternalNotes()).append("</td>");
                    sb.append("</tr>");
                    quote_id = detailsDOB.getQuoteId();
                }
                sb.append("</body>");
                sb.append("</html>");
                out.print(sb);
              }

	      }
             
             //ends phani
        }
        else
      {
        request.setAttribute("errors",errors);
        request.setAttribute("freightReportDOB",ratesrptDOB);     
        doFileDispatch(request, response, "reports/QMSFreightRatesExcelEnterid.jsp");
      }
        }
      }//ends 167656
      	
      
    }
    catch(Exception e)
    {
        e.printStackTrace();
    }
  }
  	public void doFileDispatch(HttpServletRequest request, HttpServletResponse response, String forwardFile)throws IOException, ServletException
	{
		try
		{
			RequestDispatcher rd = request.getRequestDispatcher(forwardFile);
			rd.forward(request, response);
		}
		catch(Exception ex)
		{
			//Logger.error(FILE_NAME, " [doFileDispatch() ", " Exception in forwarding ---> "+ ex.toString());
        logger.error(FILE_NAME+ " [doFileDispatch() "+ " Exception in forwarding ---> "+ ex.toString());
			ex.printStackTrace();
		}
	}

    
}