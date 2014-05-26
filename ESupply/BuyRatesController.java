import com.foursoft.esupply.common.java.LookUpBean;
import com.qms.operations.sellrates.ejb.sls.QMSSellRatesSession;
import com.qms.operations.sellrates.ejb.sls.QMSSellRatesSessionHome;
import com.qms.operations.sellrates.java.QMSSellRatesDOB;
import com.qms.operations.rates.dob.FlatRatesDOB;
import com.qms.operations.rates.dob.RateDOB;
import com.foursoft.esupply.common.bean.DateFormatter;
import com.foursoft.esupply.common.bean.ESupplyGlobalParameters;
//import com.foursoft.esupply.common.util.Logger;
import org.apache.log4j.Logger;
import com.qms.operations.rates.ejb.sls.BuyRatesSessionBean;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeSet;
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.ArrayList;
import com.qms.operations.rates.ejb.sls.BuyRatesSession;
import com.qms.operations.rates.ejb.sls.BuyRatesSessionHome;

import com.foursoft.esupply.common.java.ErrorMessage;
import com.foursoft.esupply.common.java.KeyValue;
import com.foursoft.esupply.common.util.ESupplyDateUtility;

public class BuyRatesController extends HttpServlet 
{
	
	private static final String 	   FILE_NAME 		= "BuyRatesController.java";
	private static Logger logger = null;
 
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
   	doPost(request,response);
  }

  /**
   * Process the HTTP doPost request.
   */
	  public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	  {
     	String					operation				=	 null;
			String					shipmentMode			=	 null;
			String					weightBreak				=	 null;
			String					rateType				=	 null;
			String					subOperation			=	 null;
			String					effectiveFrom			=	 null;
			String					effectiveTo				=	 null;
			String					currency				=	 null;
			String					carrierId				=	 null;
			String					serviceLevel			=	 null;  
			String					uom						=	 null;
			String					consoleType				=	 null;
			RateDOB					rateDOB					=	 null;
			String[]				wtbrakValues			=	 null;
			String[] 				surchargeWeightBreaks	=	 null;//Added by Govind for the CR-219973
		        String[] 				surchargeIds			=	 null;//Added by Govind for the CR-219973
		        String[] 				surchargeDesc			=	 null;//Added by Govind for the CR-219973
			String[]				listValues				=	 null;
			String					rateDtls				=	 null;	
			String					fromWhere				=	null;
			String					currentDate				=	null;
			String					dateFormat				=	null;
			String					originLocation			=	null;
			String					originCountry			=	null;
			String					destinationLocation		=	null;
			String					destinationCountry		=	null;
			String[]				carriers				=	null;
			String					currencyId				=	null;
			String					weightClass				=	null;
			int						preRows					=	 0;	
			ArrayList				lanes					=	 null;	
			FlatRatesDOB			flatRatesDOB			=	 null;
			FlatRatesDOB			flatRatesDOB1			=	 null;
			String					result					=	 null; 
			String					accessLevel				=	 null; 
	        QMSSellRatesDOB			sellRatesDOB			=	 null;
			ESupplyGlobalParameters loginbean				=	 null;
			ESupplyDateUtility		fomater					=	 null;
			ErrorMessage			errorMessageObject		=	null;
			ArrayList				keyValueList			=	null;	
			String					validateratedob			=	null;
			String					detailRateDob			=	null;
		    BuyRatesSession			remote					=	null;
			BuyRatesSessionHome		home					=	null;
			QMSSellRatesSessionHome home1					= null;
			QMSSellRatesSession     remote1					= null;
			StringBuffer			errors					= null;
			int srclineno 									=	0;//Govind
			String srcwatBraks[]							=	null;//Govind
			int srcwatBrakslen								=	0;//Govind
			int surChargeIdlen								=	0;//Govind
			String chargeIndForSlabBoth						=	null;//Govind
			String slabBothValueInd							=	null;//Govind
      String frequency    = null;
      ArrayList     list_returnList = null;
		  StringBuffer        sb_successErrorMsg     = null;
      StringBuffer        sb_failErrorMsg  = null;
      String                    rateslist  = null;
      String                    breakslist  = null;
       ArrayList                 ratelaneslist  = new ArrayList();//@@Added for the WPBN issues-146448,146968 on 18/12/08
     // String[]                  ratesList  = null;
     // String[]                 breaksList  = null;
	try
	{
            session				=	request.getSession();
		    keyValueList		=	new ArrayList();

	        loginbean			= (ESupplyGlobalParameters)session.getAttribute("loginbean");
       
			nextNavigation		=	"etrans/BuyRatesAdd.jsp";
      
			operation			=	request.getParameter("Operation");
			shipmentMode		=	request.getParameter("shipmentMode");
			subOperation		=	request.getParameter("subOperation");
			weightBreak			=	request.getParameter("weightBreak");
  		consoleType			=	request.getParameter("consoleType");
			subOperation		=	request.getParameter("subOperation");
			effectiveFrom		=	request.getParameter("effectiveFrom");
			effectiveTo			=	request.getParameter("effectiveTo");
			currency			=	request.getParameter("baseCurrency");
			carrierId			=	request.getParameter("carrierId");
			uom					=	request.getParameter("uom");
			rateType			=	request.getParameter("rateType");
			wtbrakValues		=	request.getParameterValues("wtbrak");
			surchargeWeightBreaks = request.getParameterValues("surchargeWeightBreaks");
	        surchargeIds = request.getParameterValues("surchargeIds");
	        surchargeDesc = request.getParameterValues("surchargeDesc");
	        if(surchargeIds!=null){
	        for(int i=0;i<surchargeIds.length;i++){
	        	if("".equalsIgnoreCase(surchargeIds[1])){
	        		surchargeIds = null;
	        		if("".equalsIgnoreCase(surchargeDesc[1])){
		        		surchargeDesc = null;	
		        	}
	        		break;
	        	}
	        	
	        }
	        }
		    listValues			=	request.getParameterValues("listValue");
			rateDtls			=	request.getParameter("rateDtls");
			fromWhere          =	request.getParameter("fromWhere");
			currentDate        =	request.getParameter("currentDate");

			/*rateslist         = 	request.getParameter("rateslist");
      breakslist         = 	request.getParameter("breakslist");
			accessLevel        =	loginbean.getUserLevel();

      if(rateslist!=null)
          ratesList = rateslist.split(",");
      if(breakslist!=null) 
          breaksList =breakslist.split(",");*/
       if("OPER_TERMINAL".equalsIgnoreCase(accessLevel))
      {
        accessLevel="O";
      }else if("HO_TERMINAL".equalsIgnoreCase(accessLevel))
      {
          accessLevel ="H";
      }else if("ADMN_TERMINAL".equalsIgnoreCase(accessLevel))
      {
          accessLevel ="A";
      }
      

			rateDOB				=	new RateDOB();
			lanes				=	new ArrayList();
			fomater			    =   new ESupplyDateUtility();

		
			if(request.getParameter("preRows")!=null && !"".equals(request.getParameter("preRows").trim()) )
				preRows				=	Integer.parseInt(request.getParameter("preRows"));

		if("back".equals(operation))
		{
			nextNavigation = "etrans/BuyRatesAdd.jsp";
		}
		if ((subOperation== null) && ("Add".equalsIgnoreCase(operation)))
		{
			System.out.println("operation-----------"+operation);
			nextNavigation = "etrans/BuyRatesAdd.jsp";
		}else if( "Add".equalsIgnoreCase(subOperation) && "Add".equalsIgnoreCase(operation))
		{
        dateFormat         =	loginbean.getUserPreferences().getDateFormat();
    		rateDOB.setRateType(rateType);
				rateDOB.setAccessLevel(accessLevel);
				rateDOB.setWeightBreak(weightBreak);
				rateDOB.setCurrency(currency);
				rateDOB.setCarrierId(carrierId);
				rateDOB.setUom(uom);
        //getTimestampWithTimeAndSeconds
        
				rateDOB.setEffectiveFrom(fomater.getTimestampWithTimeAndSeconds(loginbean.getUserPreferences().getDateFormat(),effectiveFrom,"00:00:01"));

        if(effectiveTo != null && !"".equals(effectiveTo))
          rateDOB.setValidUpto(fomater.getTimestampWithTimeAndSeconds(loginbean.getUserPreferences().getDateFormat(),effectiveTo,"23:59:59"));
          
				rateDOB.setCreatedTime(new java.sql.Timestamp((new java.util.Date()).getTime()));
				rateDOB.setConsoleType(consoleType);

				if("Air".equalsIgnoreCase(shipmentMode))
					rateDOB.setShipmentMode("1");
				else if("Sea".equalsIgnoreCase(shipmentMode))
					rateDOB.setShipmentMode("2");
				else if("Truck".equalsIgnoreCase(shipmentMode))
					rateDOB.setShipmentMode("4");

      
				rateDOB.setTerminalId(loginbean.getTerminalId());
				rateDOB.setServiceLevel(serviceLevel);

				session.setAttribute("rateDOB",rateDOB);
				session.setAttribute("wtbrak",wtbrakValues);  
				//Added by Rakesh
				if(surchargeIds!=null && surchargeDesc!=null){
				session.setAttribute("surchargeIds",surchargeIds);
				session.setAttribute("surchargeDesc",surchargeDesc);
				}//Ended by Rakesh
				session.setAttribute("surchargeWeightBreaks",surchargeWeightBreaks);
   			session.setAttribute("listValues",listValues);  
				//validateratedob		=	validateRateDOB(effectiveFrom,effectiveTo,currency,carrierId);
        validateratedob		=	validateRateDOB(effectiveFrom,effectiveTo,currency,carrierId,rateDOB.getShipmentMode(),loginbean.getTerminalId());
     		if(validateratedob !=null && !"".equals(validateratedob.trim()))
			{
      	request.setAttribute("detailRateDob", validateratedob);
				nextNavigation = "etrans/BuyRatesAdd.jsp";

			}else{

				if("Flat".equalsIgnoreCase(weightBreak))
						nextNavigation = "etrans/BuyRatesAddFlatFlat.jsp";
				else if("Slab".equalsIgnoreCase(weightBreak))
				{
					if("Both".equalsIgnoreCase(rateType))
					  nextNavigation = "etrans/BuyRatesAddSlabBoth.jsp";
					else
					  nextNavigation = "etrans/BuyRatesAddSlabSlab.jsp";
				
				}else if("List".equalsIgnoreCase(weightBreak))
				{
			        nextNavigation = "etrans/BuyRatesAddList.jsp";
				}

			}
		}
		if( ("View".equalsIgnoreCase(operation)) && "Enter".equals(subOperation))
		{
  
			    nextNavigation = "etrans/QMSBuyRateView.jsp?Operation=View&subOperation=View";
     
		}else if(("View".equalsIgnoreCase(subOperation) || ("pageIterator".equalsIgnoreCase(subOperation)) ) && ("View".equalsIgnoreCase(operation)))
		{
            sellRatesDOB  = new QMSSellRatesDOB();
            doGetBuyRates(request,response,loginbean,operation,sellRatesDOB,nextNavigation);
            nextNavigation			=	 "etrans/QMSBuyRateView.jsp";
      
		}
	    if( ("InValide".equalsIgnoreCase(operation)) && "Enter".equals(subOperation))
		{
			    nextNavigation = "etrans/QMSBuyRateView.jsp?Operation=InValide&subOperation=InValide";
		
		}else if(("InValide".equalsIgnoreCase(subOperation)) && ("InValide".equalsIgnoreCase(operation)))
		{
				sellRatesDOB  = new QMSSellRatesDOB();
				doGetBuyRates(request,response,loginbean,operation,sellRatesDOB,nextNavigation);
				nextNavigation			=	 "etrans/QMSBuyRateView.jsp?Operation=InValide&subOperation=values";
		}else if(("pageIterator".equalsIgnoreCase(subOperation)) && ("InValide".equalsIgnoreCase(operation)))
    {
        String errorsMsg = getCheckedLanesInvalidate(request,response,loginbean);

        HashMap hm= (HashMap)session.getAttribute("hm_buyRates");
          
        if(!"".equals(errorsMsg.trim()))
          {
            request.setAttribute("Errors",errorsMsg);
            nextNavigation			=	 "etrans/QMSBuyRateView.jsp?Operation=InValide&subOperation=values";
          }else
          {
              sellRatesDOB  = new QMSSellRatesDOB();
              doGetBuyRates(request,response,loginbean,operation,sellRatesDOB,nextNavigation);           
              nextNavigation			=	 "etrans/QMSBuyRateView.jsp?Operation=InValide&subOperation=values";
          }
		}else if(("values".equalsIgnoreCase(subOperation)) && ("InValide".equalsIgnoreCase(operation)))
		{
          String errorsMsg = getCheckedLanesInvalidate(request,response,loginbean);
          
          if(!"".equals(errorsMsg.trim()))
          {
            request.setAttribute("Errors",errorsMsg);
            nextNavigation			=	 "etrans/QMSBuyRateView.jsp?Operation=InValide&subOperation=values";
          }else
          {
              invalidateBuyRateDtls(request,response);
              
              errorMessageObject = new ErrorMessage("Records Invalidated successfully ","BuyRatesController?Operation=InValide&subOperation=Enter"); 
              
              keyValueList.add(new KeyValue("ErrorCode","RSI")); 	
              keyValueList.add(new KeyValue("Operation",operation)); 	
              
              errorMessageObject.setKeyValueList(keyValueList);
              
              request.setAttribute("ErrorMessage",errorMessageObject);
              session.setAttribute("ErrorMessage","Records Invalidated successfully");
              session.setAttribute("ErrorCode","RSI");
              session.setAttribute("NextNavigation","BuyRatesController?Operation=InValide&subOperation=Enter");
              nextNavigation	=	"ESupplyErrorPage.jsp";
          }
		}
		if( ("Modify".equalsIgnoreCase(operation)) && "Enter".equals(subOperation))
		{
		    nextNavigation = "etrans/QMSBuyRateModify.jsp?Operation=Modify&subOperation=Modify";
		
		}else if(("Modify".equalsIgnoreCase(subOperation)) && ("Modify".equalsIgnoreCase(operation)))
		{
				sellRatesDOB  = new QMSSellRatesDOB();
				doGetBuyRates(request,response,loginbean,operation,sellRatesDOB,nextNavigation);
				nextNavigation			=	 "etrans/QMSBuyRateModify.jsp?Operation=Modify&subOperation=values";
		}else if(("pageIterator".equalsIgnoreCase(subOperation)) && ("Modify".equalsIgnoreCase(operation)))
    {
        String errorsMsg = getCheckedLanes(request,response,loginbean);
        
        HashMap hm= (HashMap)session.getAttribute("hm_buyRates");
       
      
        if(!"".equals(errorsMsg.trim()))
          {
            request.setAttribute("Errors",errorsMsg);
            nextNavigation			=	 "etrans/QMSBuyRateModify.jsp?Operation=Modify&subOperation=values";
          }else
          {
              sellRatesDOB  = new QMSSellRatesDOB();
              doGetBuyRates(request,response,loginbean,operation,sellRatesDOB,nextNavigation);           
              nextNavigation			=	 "etrans/QMSBuyRateModify.jsp?Operation=Modify&subOperation=values";
          }
    }
    else if(("values".equalsIgnoreCase(subOperation)) && ("Modify".equalsIgnoreCase(operation)))
		{
      	String errorsMsg = getCheckedLanes(request,response,loginbean);
        if(!"".equals(errorsMsg.trim()))
        {
          request.setAttribute("Errors",errorsMsg);
          nextNavigation			=	 "etrans/QMSBuyRateModify.jsp?Operation=Modify&subOperation=values";
        }else
        {
          errorsMsg = modifyBuyRateDtl(request,response,loginbean);
          
          if(!"".equals(errorsMsg.trim()))
          {
            request.setAttribute("Errors",errorsMsg);
            nextNavigation			=	 "etrans/QMSBuyRateModify.jsp?Operation=Modify&subOperation=values";
          }else
          {
              session.removeAttribute("HeaderValue");
              session.removeAttribute("HeaderValues");
              
              errorMessageObject = new ErrorMessage("Records Successfully Modified","BuyRatesController?Operation=Modify&subOperation=Enter"); 
              keyValueList.add(new KeyValue("ErrorCode","RSM")); // modified by VALSKHMI on 12/11/2008 form RSI to RSM
              keyValueList.add(new KeyValue("Operation",operation)); 	
      
              errorMessageObject.setKeyValueList(keyValueList);
        
              request.setAttribute("ErrorMessage",errorMessageObject);
              session.setAttribute("ErrorMessage","Records Successfully Modified");
              session.setAttribute("ErrorCode","RSM");
              session.setAttribute("NextNavigation","BuyRatesController?Operation=Modify&subOperation=Enter");
              nextNavigation	=	"ESupplyErrorPage.jsp";
          }
        }
		}
		if("FlatFlat".equalsIgnoreCase(rateDtls))
		{
			surchargeIds	= (String[])session.getAttribute("surchargeIds");//Govind
			surchargeDesc	= (String[])session.getAttribute("surchargeDesc");//Govind
			surchargeWeightBreaks	= (String[])session.getAttribute("surchargeWeightBreaks");//Govind
			int count;
			weightClass	=	request.getParameter("weightClass");
			rateDOB = (RateDOB)session.getAttribute("rateDOB");
			detailRateDob = detailRateDOB(request,response,rateDtls,rateDOB);//COMMENTED TO TEST EXECUTION.. NEED REMOVE THIS COMMENT...
			
				if((detailRateDob !=null) && !"".equals(detailRateDob))
			{
				request.setAttribute("detailRateDob", detailRateDob);
				nextNavigation = "etrans/BuyRatesAddFlatFlat.jsp?rows=0";

			}else
			{
		  	for(int i=0;i<=preRows;i++)
			{
		  		srclineno	=	0;
       lanes  = new ArrayList();//@@Added for the WPBN issues-146448,146968
       if(request.getParameter("frequency"+i)		!= null && !"".equals(request.getParameter("frequency"+i)) )
       {
           if(!"2".equals(rateDOB.getShipmentMode()))
           {
             frequency = getSortedNums(request.getParameter("frequency"+i).trim());
           }else
           {
             frequency = request.getParameter("frequency"+i).trim();
           }             
       }  
              for(int fr=0;fr<=2;fr++){
            	  flatRatesDOB = new FlatRatesDOB();
                  flatRatesDOB.setOrigin		(request.getParameter("origin"+i)		    != null ? request.getParameter("origin"+i)		 : "");
                  flatRatesDOB.setDestination	(request.getParameter("destination"+i)		!= null ? request.getParameter("destination"+i)	 : "");
                  if(request.getParameter("effDate"+i)			!= null)
                  flatRatesDOB.setEffDate		(fomater.getTimestamp("DD-MM-YY",request.getParameter("effDate"+i)));
                  flatRatesDOB.setFrequency	(frequency);  
                  flatRatesDOB.setTransittime	(request.getParameter("transitTime"+i)		!= null ? request.getParameter("transitTime"+i)	 : "");
          
                  //flatRatesDOB.setChargeRate(Double.parseDouble(request.getParameter("min"+i)));
                  flatRatesDOB.setNotes(request.getParameter("notes"+i));
                //Modified by Mohan for Issue No.219976 on 28-10-2010
                  flatRatesDOB.setExtNotes(request.getParameter("extNotes"+i));
                  flatRatesDOB.setDensityRatio((request.getParameter("densityRatio"+i)!=null)?request.getParameter("densityRatio"+i):"");//added by rk
                  flatRatesDOB.setLowerBound(0);
                  flatRatesDOB.setUpperBound(0);
                  flatRatesDOB.setLaneNo(i);
                  flatRatesDOB.setLineNo(srclineno);
                  if(srclineno == 0){
                  	     flatRatesDOB.setWtBreakSlab("BASIC");
                         flatRatesDOB.setServiceLevel(request.getParameter("serviceLevel"+i)		!= null ? request.getParameter("serviceLevel"+i) : "");
                         flatRatesDOB.setChargeRate(Double.parseDouble((request.getParameter("basic"+i)!= null && !"".equals(request.getParameter("basic"+i)))?request.getParameter("basic"+i) :"0.00"));
                         flatRatesDOB.setTypeofcharge("A FREIGHT RATE");   
                     }
                  if(srclineno == 1){
               	   flatRatesDOB.setWtBreakSlab("MIN");
                      flatRatesDOB.setServiceLevel(request.getParameter("serviceLevel"+i)		!= null ? request.getParameter("serviceLevel"+i) : "");
                      flatRatesDOB.setChargeRate(Double.parseDouble(request.getParameter("min"+i)));
                      flatRatesDOB.setTypeofcharge("A FREIGHT RATE");
                  }
                  if(srclineno == 2){
                      flatRatesDOB.setWtBreakSlab("FLAT");
                      flatRatesDOB.setServiceLevel(request.getParameter("serviceLevel"+i)		!= null ? request.getParameter("serviceLevel"+i) : "");
                      flatRatesDOB.setChargeRate(Double.parseDouble(request.getParameter("flat"+i)));
                      flatRatesDOB.setTypeofcharge("A FREIGHT RATE");
                      }
                  flatRatesDOB.setWeightClass(weightClass);
                  if( flatRatesDOB.getWtBreakSlab()!=null) 
          		      lanes.add(flatRatesDOB);
                  srclineno++;
             }
              
     //Freigth rate ends  
            //Added by Rakesh
            if(surchargeIds!=null){  
       		surChargeIdlen = surchargeIds.length;
         for(int k=1;k<surChargeIdlen;k++)
         {   
        	 srcwatBraks = surchargeWeightBreaks[k].split(",");
        	  srcwatBrakslen = srcwatBraks.length;
        	  if(srcwatBraks[0].equalsIgnoreCase("slab")&& srcwatBrakslen ==1){
        		  if("on".equalsIgnoreCase(request.getParameter(surchargeIds[k]+i)))
         		 {
        		  for(int slbbraks=0;slbbraks<=11;slbbraks++){
        			  System.out.println(request.getParameter("srslabbreaks"+i+(k-1)+slbbraks));
        			  if((request.getParameter("srslabbreaks"+i+(k-1)+slbbraks)!=null) && (!"".equals(request.getParameter("srslabbreaks"+i+(k-1)+slbbraks)))){
        			  flatRatesDOB = new FlatRatesDOB();
                      flatRatesDOB.setOrigin		(request.getParameter("origin"+i)		    != null ? request.getParameter("origin"+i)		 : "");
                      flatRatesDOB.setDestination	(request.getParameter("destination"+i)		!= null ? request.getParameter("destination"+i)	 : "");
                      if(request.getParameter("effDate"+i)			!= null)
                      flatRatesDOB.setEffDate		(fomater.getTimestamp("DD-MM-YY",request.getParameter("effDate"+i)));
                      flatRatesDOB.setFrequency	(frequency);  
                      flatRatesDOB.setTransittime	(request.getParameter("transitTime"+i)		!= null ? request.getParameter("transitTime"+i)	 : "");
              
                      //flatRatesDOB.setChargeRate(Double.parseDouble(request.getParameter("min"+i)));
                      flatRatesDOB.setNotes(request.getParameter("notes"+i));
                      //Modified by Mohan for Issue No.219976 on 28-10-2010
                      flatRatesDOB.setExtNotes(request.getParameter("extNotes"+i));
                      flatRatesDOB.setDensityRatio((request.getParameter("densityRatio"+i)!=null)?request.getParameter("densityRatio"+i):"");//added by rk
                      flatRatesDOB.setSurchargeCurrency((request.getParameter("surchargeCurrency"+(k-1)+i)!=null)?request.getParameter("surchargeCurrency"+(k-1)+i):"");//Added By Govind for the CR-219973
                      if(slbbraks == 0){
                      flatRatesDOB.setLowerBound(0);
                      flatRatesDOB.setUpperBound(0);
                      }else if(slbbraks == 1){
                    	  flatRatesDOB.setLowerBound(0);
                    	  flatRatesDOB.setUpperBound(Double.parseDouble(
                    			  request.getParameter("srslabbreaks"+i+(k-1)+slbbraks).substring(1)));
                      }else{
                   	   if(slbbraks == 11){
                		   flatRatesDOB.setLowerBound(Double.parseDouble(request.getParameter("srslabbreaks"+i+(k-1)+slbbraks)));              
                      	   flatRatesDOB.setUpperBound(Double.parseDouble("100000"));
                     	                      		   
                	   }else{
                    	  flatRatesDOB.setLowerBound(Double.parseDouble(request.getParameter("srslabbreaks"+i+(k-1)+slbbraks)));              
                    	  flatRatesDOB.setUpperBound(Double.parseDouble(request.getParameter("srslabbreaks"+i+(k-1)+(slbbraks+1)).length()>1?
                    			  request.getParameter("srslabbreaks"+i+(k-1)+(slbbraks+1)):"100000"));		  
                      }
                   }
                      flatRatesDOB.setLaneNo(i);
                      flatRatesDOB.setLineNo(srclineno);
                      flatRatesDOB.setServiceLevel("SCH");
                      flatRatesDOB.setWtBreakSlab(request.getParameter("srslabbreaks"+i+(k-1)+slbbraks));
                      flatRatesDOB.setChargeRate(Double.parseDouble(request.getParameter("srslabvalues"+i+(k-1)+slbbraks)));
                      flatRatesDOB.setTypeofcharge(surchargeDesc[k]+'-'+(surchargeIds[k]).substring(3)); //Added By Kishore apeending Surcharge Rate Type And Weight Break 
                      flatRatesDOB.setSurchargeId(surchargeIds[k]);
                      flatRatesDOB.setWeightClass(weightClass);
                      if( flatRatesDOB.getWtBreakSlab()!=null) 
             		      lanes.add(flatRatesDOB);
                      srclineno++;
        		  }//inner if ends    
        		  }//slbbraks for ends
                 }//on if ends	  
         
        	  }//slab if ends
        	  else if(srcwatBraks[0].equalsIgnoreCase("both")&& srcwatBrakslen ==1){
        		  
        		  if("on".equalsIgnoreCase(request.getParameter(surchargeIds[k]+i)))
          		 {
        			  
         		  for(int slbbraks=0;slbbraks<=11;slbbraks++){
         			  System.out.println(request.getParameter("srslabbreaks"+i+(k-1)+slbbraks));
         			  if((request.getParameter("srslabbreaks"+i+(k-1)+slbbraks)!=null) && (!"".equals(request.getParameter("srslabbreaks"+i+(k-1)+slbbraks)))){
         			  flatRatesDOB = new FlatRatesDOB();
                       flatRatesDOB.setOrigin		(request.getParameter("origin"+i)		    != null ? request.getParameter("origin"+i)		 : "");
                       flatRatesDOB.setDestination	(request.getParameter("destination"+i)		!= null ? request.getParameter("destination"+i)	 : "");
                       if(request.getParameter("effDate"+i)			!= null)
                       flatRatesDOB.setEffDate		(fomater.getTimestamp("DD-MM-YY",request.getParameter("effDate"+i)));
                       flatRatesDOB.setFrequency	(frequency);  
                       flatRatesDOB.setTransittime	(request.getParameter("transitTime"+i)		!= null ? request.getParameter("transitTime"+i)	 : "");
                       flatRatesDOB.setSurchargeCurrency((request.getParameter("surchargeCurrency"+(k-1)+i)!=null)?request.getParameter("surchargeCurrency"+(k-1)+i):"");//Added By Govind for the CR-219973
                       //flatRatesDOB.setChargeRate(Double.parseDouble(request.getParameter("min"+i)));
                       flatRatesDOB.setNotes(request.getParameter("notes"+i));
                       flatRatesDOB.setDensityRatio((request.getParameter("densityRatio"+i)!=null)?request.getParameter("densityRatio"+i):"");//added by rk
                       if(slbbraks == 0){
                       flatRatesDOB.setLowerBound(0);
                       flatRatesDOB.setUpperBound(0);
                       }else if(slbbraks == 1){
                     	  flatRatesDOB.setLowerBound(0);
                     	  flatRatesDOB.setUpperBound(Double.parseDouble(
                     			  request.getParameter("srslabbreaks"+i+(k-1)+slbbraks).substring(1)));
                       }else{
                    	   if(slbbraks == 11){
                    		   flatRatesDOB.setLowerBound(Double.parseDouble(request.getParameter("srslabbreaks"+i+(k-1)+slbbraks)));              
                          	   flatRatesDOB.setUpperBound(Double.parseDouble("100000"));
                         	                      		   
                    	   }else{
                     	  flatRatesDOB.setLowerBound(Double.parseDouble(request.getParameter("srslabbreaks"+i+(k-1)+slbbraks)));              
                     	  flatRatesDOB.setUpperBound(Double.parseDouble(request.getParameter("srslabbreaks"+i+(k-1)+(slbbraks+1)).length()>1?
                     			  request.getParameter("srslabbreaks"+i+(k-1)+(slbbraks+1)):"100000"));		  
                       }
                       }
                       flatRatesDOB.setLaneNo(i);
                       flatRatesDOB.setLineNo(srclineno);
                       flatRatesDOB.setServiceLevel("SCH");
                       flatRatesDOB.setWtBreakSlab(request.getParameter("srslabbreaks"+i+(k-1)+slbbraks));
                       slabBothValueInd     = (request.getParameter("srslabvalue"+i+(k-1)+slbbraks)!=null && !"".equals(request.getParameter("srslabvalue"+i+(k-1)+slbbraks)))?"srslabvalue"+i+(k-1)+slbbraks:"srflatvalue"+i+(k-1)+slbbraks;
                       chargeIndForSlabBoth = (slbbraks!=0)?((request.getParameter("srslabvalue"+i+(k-1)+slbbraks)!=null && !"".equals(request.getParameter("srslabvalue"+i+(k-1)+slbbraks)))?"SLAB":"FLAT"):"";
                       flatRatesDOB.setChargeRate(Double.parseDouble(request.getParameter(slabBothValueInd)));
                       flatRatesDOB.setChargeRateIndicator(chargeIndForSlabBoth);
                       flatRatesDOB.setTypeofcharge(surchargeDesc[k]+'-'+(surchargeIds[k]).substring(3)); //kishore
                       flatRatesDOB.setSurchargeId(surchargeIds[k]);
                       flatRatesDOB.setWeightClass(weightClass);
                       if( flatRatesDOB.getWtBreakSlab()!=null) 
              		      lanes.add(flatRatesDOB);
                       srclineno++;
         		  }//inner if ends    
         		  }//slbbraks for ends
                  }//on if ends	  
        		  
        		  
        		  
        		  
        		  
        	  }
        	  else{//slab else begins
        	 for(int j=0;j<srcwatBrakslen;j++)
        	 {
        		 if("on".equalsIgnoreCase(request.getParameter(surchargeIds[k]+i)))
        		 {
        			 flatRatesDOB = new FlatRatesDOB();
                     flatRatesDOB.setOrigin		(request.getParameter("origin"+i)		    != null ? request.getParameter("origin"+i)		 : "");
                     flatRatesDOB.setDestination	(request.getParameter("destination"+i)		!= null ? request.getParameter("destination"+i)	 : "");
                     if(request.getParameter("effDate"+i)			!= null)
                     flatRatesDOB.setEffDate		(fomater.getTimestamp("DD-MM-YY",request.getParameter("effDate"+i)));
                     flatRatesDOB.setFrequency	(frequency);  
                     flatRatesDOB.setTransittime	(request.getParameter("transitTime"+i)		!= null ? request.getParameter("transitTime"+i)	 : "");
                     flatRatesDOB.setSurchargeCurrency((request.getParameter("surchargeCurrency"+(k-1)+i)!=null)?request.getParameter("surchargeCurrency"+(k-1)+i):"");//Added By Govind for the CR-219973
                     //flatRatesDOB.setChargeRate(Double.parseDouble(request.getParameter("min"+i)));
                     flatRatesDOB.setNotes(request.getParameter("notes"+i));
                     flatRatesDOB.setDensityRatio((request.getParameter("densityRatio"+i)!=null)?request.getParameter("densityRatio"+i):"");//added by rk
                     flatRatesDOB.setLowerBound(0);
                     flatRatesDOB.setUpperBound(0);
                     flatRatesDOB.setLaneNo(i);
                     flatRatesDOB.setLineNo(srclineno);
                     flatRatesDOB.setServiceLevel("SCH");
                     flatRatesDOB.setWtBreakSlab(surchargeIds[k]+srcwatBraks[j]);
                     flatRatesDOB.setChargeRate(Double.parseDouble(request.getParameter(surchargeIds[k]+srcwatBraks[j]+i)));
                     flatRatesDOB.setTypeofcharge(surchargeDesc[k]+'-'+(surchargeIds[k]).substring(3)); //kishore
                     flatRatesDOB.setSurchargeId(surchargeIds[k]);
                     flatRatesDOB.setWeightClass(weightClass);
                     if( flatRatesDOB.getWtBreakSlab()!=null) 
             		      lanes.add(flatRatesDOB);
                     srclineno++;
                     
                   
        		 }//end if
        	 }//end j loop
         } //slab else end
         }//end k
		}
         ratelaneslist.add(lanes);
		
			}
		//	 System.out.println(ratelaneslist.size());
			home				= (BuyRatesSessionHome)LookUpBean.getEJBHome("BuyRatesSessionBean");
			remote				= (BuyRatesSession)home.create();
			rateDOB				=  (RateDOB)session.getAttribute("rateDOB");

			rateDOB.setWeightClass(weightClass);
			 
			//result				=   remote.crateBuyFlatRate(rateDOB,lanes);
      // list_returnList	=   remote.crateBuyFlatRate(rateDOB,lanes);
      list_returnList	=   remote.crateBuyFlatRate(rateDOB,ratelaneslist);//@@Modified for the WPBN issues-146448,146968 on 18/12/08
          
      sb_successErrorMsg     = (StringBuffer)list_returnList.get(0);
      sb_failErrorMsg        = (StringBuffer)list_returnList.get(1);
      
			session.removeAttribute("lanes");
			session.removeAttribute("rateDOB");
            session.removeAttribute("surchargeIds");
            session.removeAttribute("surchargeDesc");
            session.removeAttribute("surchargeWeightBreaks");
			session.removeAttribute("rateDOB");
			session.removeAttribute("wtbrak");  
			session.removeAttribute("listValues");  
      errors = new StringBuffer();
      if(sb_successErrorMsg!=null && sb_successErrorMsg.length()>0)
      {
        errors.append("Records Successfully Inserted for the following lanes:\n"+sb_successErrorMsg.toString());
        
      }
      
      if(sb_failErrorMsg!=null && sb_failErrorMsg.length()>0)
      {
        errors.append("The following lanes are not Inserted As rates already defined at higher levels :\n"+sb_failErrorMsg.toString());
      }
      
			errorMessageObject = new ErrorMessage(errors.toString(),"BuyRatesController?Operation=Add"); 

			keyValueList.add(new KeyValue("ErrorCode","RSI")); 	
			keyValueList.add(new KeyValue("Operation",operation)); 	

			errorMessageObject.setKeyValueList(keyValueList);

			request.setAttribute("ErrorMessage",errorMessageObject);
			session.setAttribute("ErrorMessage","Buy Rates added Sucessfully");
			session.setAttribute("ErrorCode","RSI");
			session.setAttribute("NextNavigation","BuyRatesController?Operation=Add");
			nextNavigation	=	"ESupplyErrorPage.jsp";
		}
	}		
	else if("SlabSlab".equalsIgnoreCase(rateDtls))
    {
    
      rateDOB = (RateDOB)session.getAttribute("rateDOB");
      surchargeIds	= (String[])session.getAttribute("surchargeIds");//Govind
	 surchargeDesc	= (String[])session.getAttribute("surchargeDesc");//Govind
	surchargeWeightBreaks	= (String[])session.getAttribute("surchargeWeightBreaks");//Govind
	     int count=0;
  		detailRateDob = detailRateDOB(request,response,rateDtls,rateDOB); 
  		//detailRateDOB(request,response,rateDtls,rateDOB);
			if((detailRateDob !=null) && !"".equals(detailRateDob))
			{
				request.setAttribute("detailRateDob", detailRateDob);
				nextNavigation = "etrans/BuyRatesAddSlabSlab.jsp?rows=0";

			}else
			{
				
			  wtbrakValues	=	(String[])session.getAttribute("wtbrak");
			  weightClass	= request.getParameter("weightClass");
		      
          int lineno;
				for(int i=0;i<=preRows;i++)
				{
			   lanes     = new ArrayList();//@@Added for the WPBN issues-146448,146968
         count =0;
         lineno=0;
        if("on".equalsIgnoreCase(request.getParameter(""+i)))
				 {//on if for first freight lane begins
		          frequency = "";
				    if(request.getParameter("frequency"+i)		!= null && !"".equals(request.getParameter("frequency"+i)))
		        {
              if(!"2".equals(rateDOB.getShipmentMode()))
              {
                  frequency = getSortedNums(request.getParameter("frequency"+i).trim());
              }
              else
				      {
                	frequency = request.getParameter("frequency"+i).trim();
		           }               
				   }
          
        
            int wtbrakValuesLen		=	wtbrakValues.length;
    			  for (int j=0 ; j<wtbrakValuesLen;j++)
					  {
						   flatRatesDOB = new FlatRatesDOB();
             
							if(!"".equals(wtbrakValues[j]))
							{
        				flatRatesDOB.setOrigin		(request.getParameter("origin"+i)		    != null ? request.getParameter("origin"+i)		 : "");
								flatRatesDOB.setDestination	(request.getParameter("destination"+i)		!= null ? request.getParameter("destination"+i)	 : "");
								flatRatesDOB.setServiceLevel(request.getParameter("serviceLevel"+i)		!= null ? request.getParameter("serviceLevel"+i) : "");

								if(request.getParameter("effDate"+i)			!= null)
								flatRatesDOB.setEffDate		(fomater.getTimestamp("DD-MM-YY",request.getParameter("effDate"+i)));
    						flatRatesDOB.setFrequency	(frequency);  
								flatRatesDOB.setTransittime	(request.getParameter("transitTime"+i)		!= null ? request.getParameter("transitTime"+i)	 : "");
								flatRatesDOB.setChargeRate(Double.parseDouble(request.getParameterValues("wtbrk"+i)[j]));
								flatRatesDOB.setNotes(request.getParameter("notes"+i));
								//Modified by Mohan for Issue No.219976 on 28-10-2010
					            flatRatesDOB.setExtNotes(request.getParameter("extNotes"+i));
								flatRatesDOB.setDensityRatio((request.getParameter("densityRatio"+i)!=null)?request.getParameter("densityRatio"+i):"");//added by rk
								
                flatRatesDOB.setLaneNo(i);
						  	flatRatesDOB.setLineNo(lineno);
		 				   flatRatesDOB.setTypeofcharge("A FREIGHT RATE");
                lineno++;
                if(j==0)
				{
					flatRatesDOB.setLowerBound(0);
					flatRatesDOB.setUpperBound(0);
  flatRatesDOB.setWtBreakSlab("BASIC");
					flatRatesDOB.setChargeRate(Double.parseDouble((request.getParameter("basic"+i)!= null && !"".equals(request.getParameter("basic"+i)))?request.getParameter("basic"+i) :"0.00"));
				}
         		
                else if(j==1)
								{
									flatRatesDOB.setLowerBound(0);
									flatRatesDOB.setUpperBound(0);
                  flatRatesDOB.setWtBreakSlab("MIN");
									flatRatesDOB.setChargeRate(Double.parseDouble(request.getParameter("min"+i)));
								}
								else if(j==2)
								{
									flatRatesDOB.setLowerBound(0);
               		flatRatesDOB.setUpperBound(Double.parseDouble(wtbrakValues[j-1].substring(0,wtbrakValues[j-1].length())));
									 flatRatesDOB.setWtBreakSlab(wtbrakValues[j-2]);
						       flatRatesDOB.setChargeRate(Double.parseDouble(request.getParameterValues("wtbrk"+i)[j-2]));
								}else 
								{
									flatRatesDOB.setLowerBound(Double.parseDouble(wtbrakValues[j-2]));
									flatRatesDOB.setUpperBound(Double.parseDouble(wtbrakValues[j-1]));
                 	flatRatesDOB.setWtBreakSlab(wtbrakValues[j-2]);
									flatRatesDOB.setChargeRate(Double.parseDouble(request.getParameterValues("wtbrk"+i)[j-2]));
								}
                lanes.add(flatRatesDOB);
               
              }
               
              if(j==request.getParameterValues("wtbrk"+i).length)
              	{
                flatRatesDOB.setOrigin		(request.getParameter("origin"+i)		    != null ? request.getParameter("origin"+i)		 : "");
								flatRatesDOB.setDestination	(request.getParameter("destination"+i)		!= null ? request.getParameter("destination"+i)	 : "");
								flatRatesDOB.setServiceLevel(request.getParameter("serviceLevel"+i)		!= null ? request.getParameter("serviceLevel"+i) : "");

								if(request.getParameter("effDate"+i)			!= null)
								flatRatesDOB.setEffDate		(fomater.getTimestamp("DD-MM-YY",request.getParameter("effDate"+i)));

         				flatRatesDOB.setFrequency	(frequency);  
								flatRatesDOB.setTransittime	(request.getParameter("transitTime"+i)    != null ? request.getParameter("transitTime"+i)	 : "");
								 //Modified by Mohan for Issue No.219976 on 28-10-2010
				                  flatRatesDOB.setExtNotes(request.getParameter("extNotes"+i));
								flatRatesDOB.setNotes(request.getParameter("notes"+i));
								flatRatesDOB.setDensityRatio((request.getParameter("densityRatio"+i)!=null)?request.getParameter("densityRatio"+i):"");//added by rk
                 flatRatesDOB.setLowerBound(Double.parseDouble(wtbrakValues[j-2]));
					     	flatRatesDOB.setUpperBound(1000000);
             		flatRatesDOB.setChargeRate(Double.parseDouble(request.getParameterValues("wtbrk"+i)[j-2]));
		            flatRatesDOB.setLaneNo(i);
						   	flatRatesDOB.setLineNo(lineno);
               flatRatesDOB.setWtBreakSlab(wtbrakValues[j-2]);
                flatRatesDOB.setTypeofcharge("A FREIGHT RATE");
                lanes.add(flatRatesDOB);
                lineno++;
                }
            }
				 }//if ends  freight rates end
             
        
        if(surchargeIds!=null){//Added by Rakesh for Issue:
        surChargeIdlen = surchargeIds.length;
        for(int k=1;k<surChargeIdlen;k++)
        {   
       	 srcwatBraks = surchargeWeightBreaks[k].split(",");
       	  srcwatBrakslen = srcwatBraks.length;
       	  if(srcwatBraks[0].equalsIgnoreCase("slab")&& srcwatBrakslen ==1){
       		  if("on".equalsIgnoreCase(request.getParameter(surchargeIds[k]+i)))
        		 {
       		  for(int slbbraks=0;slbbraks<=11;slbbraks++){
       			  System.out.println(request.getParameter("srslabbreaks"+i+(k-1)+slbbraks));
       			  if((request.getParameter("srslabbreaks"+i+(k-1)+slbbraks)!=null) && (!"".equals(request.getParameter("srslabbreaks"+i+(k-1)+slbbraks)))){
       			  flatRatesDOB = new FlatRatesDOB();
                     flatRatesDOB.setOrigin		(request.getParameter("origin"+i)		    != null ? request.getParameter("origin"+i)		 : "");
                     flatRatesDOB.setDestination	(request.getParameter("destination"+i)		!= null ? request.getParameter("destination"+i)	 : "");
                     if(request.getParameter("effDate"+i)			!= null)
                     flatRatesDOB.setEffDate		(fomater.getTimestamp("DD-MM-YY",request.getParameter("effDate"+i)));
                     flatRatesDOB.setFrequency	(frequency);  
                     flatRatesDOB.setTransittime	(request.getParameter("transitTime"+i)		!= null ? request.getParameter("transitTime"+i)	 : "");
                     flatRatesDOB.setSurchargeCurrency((request.getParameter("surchargeCurrency"+(k-1)+i)!=null)?request.getParameter("surchargeCurrency"+(k-1)+i):"");//Added By Govind for the CR-219973
                     //flatRatesDOB.setChargeRate(Double.parseDouble(request.getParameter("min"+i)));
                     //Modified by Mohan for Issue No.219976 on 28-10-2010
                     flatRatesDOB.setExtNotes(request.getParameter("extNotes"+i));
                     flatRatesDOB.setNotes(request.getParameter("notes"+i));
                     flatRatesDOB.setDensityRatio((request.getParameter("densityRatio"+i)!=null)?request.getParameter("densityRatio"+i):"");//added by rk
                     if(slbbraks == 0){
                     flatRatesDOB.setLowerBound(0);
                     flatRatesDOB.setUpperBound(0);
                     }else if(slbbraks == 1){
                   	  flatRatesDOB.setLowerBound(0);
                   	  flatRatesDOB.setUpperBound(Double.parseDouble(
                   			  request.getParameter("srslabbreaks"+i+(k-1)+slbbraks).substring(1)));
                     }else{
                  	   if(slbbraks == 11){
                		   flatRatesDOB.setLowerBound(Double.parseDouble(request.getParameter("srslabbreaks"+i+(k-1)+slbbraks)));              
                      	   flatRatesDOB.setUpperBound(Double.parseDouble("100000"));
                     	                      		   
                	   }else{
                   	  flatRatesDOB.setLowerBound(Double.parseDouble(request.getParameter("srslabbreaks"+i+(k-1)+slbbraks)));              
                   	  flatRatesDOB.setUpperBound(Double.parseDouble(request.getParameter("srslabbreaks"+i+(k-1)+(slbbraks+1)).length()>1?
                   			  request.getParameter("srslabbreaks"+i+(k-1)+(slbbraks+1)):"100000"));		  
                     }
                   }
                     flatRatesDOB.setLaneNo(i);
                     flatRatesDOB.setLineNo(lineno);
                     flatRatesDOB.setServiceLevel("SCH");
                     flatRatesDOB.setWtBreakSlab(request.getParameter("srslabbreaks"+i+(k-1)+slbbraks));
                     flatRatesDOB.setChargeRate(Double.parseDouble(request.getParameter("srslabvalues"+i+(k-1)+slbbraks)));
                     flatRatesDOB.setTypeofcharge(surchargeDesc[k]+'-'+(surchargeIds[k]).substring(3)); //kishore
                     flatRatesDOB.setSurchargeId(surchargeIds[k]);
                     flatRatesDOB.setWeightClass(weightClass);
                     if( flatRatesDOB.getWtBreakSlab()!=null) 
            		      lanes.add(flatRatesDOB);
                     lineno++;
       		  }//inner if ends    
       		  }//slbbraks for ends
                }//on if ends	  
        
       	  }//slab if ends
       	  else if(srcwatBraks[0].equalsIgnoreCase("both")&& srcwatBrakslen ==1){
       		  
       		  if("on".equalsIgnoreCase(request.getParameter(surchargeIds[k]+i)))
         		 {
       			  
        		  for(int slbbraks=0;slbbraks<=11;slbbraks++){
        			  System.out.println(request.getParameter("srslabbreaks"+i+(k-1)+slbbraks));
        			  if((request.getParameter("srslabbreaks"+i+(k-1)+slbbraks)!=null) && (!"".equals(request.getParameter("srslabbreaks"+i+(k-1)+slbbraks)))){
        			  flatRatesDOB = new FlatRatesDOB();
                      flatRatesDOB.setOrigin		(request.getParameter("origin"+i)		    != null ? request.getParameter("origin"+i)		 : "");
                      flatRatesDOB.setDestination	(request.getParameter("destination"+i)		!= null ? request.getParameter("destination"+i)	 : "");
                      if(request.getParameter("effDate"+i)			!= null)
                      flatRatesDOB.setEffDate		(fomater.getTimestamp("DD-MM-YY",request.getParameter("effDate"+i)));
                      flatRatesDOB.setFrequency	(frequency);  
                      flatRatesDOB.setTransittime	(request.getParameter("transitTime"+i)		!= null ? request.getParameter("transitTime"+i)	 : "");
                      flatRatesDOB.setSurchargeCurrency((request.getParameter("surchargeCurrency"+(k-1)+i)!=null)?request.getParameter("surchargeCurrency"+(k-1)+i):"");//Added By Govind for the CR-219973
                      //flatRatesDOB.setChargeRate(Double.parseDouble(request.getParameter("min"+i)));
                      //Modified by Mohan for Issue No.219976 on 28-10-2010
                      flatRatesDOB.setExtNotes(request.getParameter("extNotes"+i));
                      flatRatesDOB.setNotes(request.getParameter("notes"+i));
                      flatRatesDOB.setDensityRatio((request.getParameter("densityRatio"+i)!=null)?request.getParameter("densityRatio"+i):"");//added by rk
                      if(slbbraks == 0){
                      flatRatesDOB.setLowerBound(0);
                      flatRatesDOB.setUpperBound(0);
                      }else if(slbbraks == 1){
                    	  flatRatesDOB.setLowerBound(0);
                    	  flatRatesDOB.setUpperBound(Double.parseDouble(
                    			  request.getParameter("srslabbreaks"+i+(k-1)+slbbraks).substring(1)));
                      }else{
                   	   if(slbbraks == 11){
                		   flatRatesDOB.setLowerBound(Double.parseDouble(request.getParameter("srslabbreaks"+i+(k-1)+slbbraks)));              
                      	   flatRatesDOB.setUpperBound(Double.parseDouble("100000"));
                     	                      		   
                	   }else{
                    	  flatRatesDOB.setLowerBound(Double.parseDouble(request.getParameter("srslabbreaks"+i+(k-1)+slbbraks)));              
                    	  flatRatesDOB.setUpperBound(Double.parseDouble(request.getParameter("srslabbreaks"+i+(k-1)+(slbbraks+1)).length()>1?
                    			  request.getParameter("srslabbreaks"+i+(k-1)+(slbbraks+1)):"100000"));		  
                      }
                    }
                      flatRatesDOB.setLaneNo(i);
                      flatRatesDOB.setLineNo(lineno);
                      flatRatesDOB.setServiceLevel("SCH");
                      flatRatesDOB.setWtBreakSlab(request.getParameter("srslabbreaks"+i+(k-1)+slbbraks));
                      slabBothValueInd   = (request.getParameter("srslabvalue"+i+(k-1)+slbbraks)!=null && !"".equals(request.getParameter("srslabvalue"+i+(k-1)+slbbraks)))?"srslabvalue"+i+(k-1)+slbbraks:"srflatvalue"+i+(k-1)+slbbraks;
                      chargeIndForSlabBoth = (slbbraks!=0)?((request.getParameter("srslabvalue"+i+(k-1)+slbbraks)!=null && !"".equals(request.getParameter("srslabvalue"+i+(k-1)+slbbraks)))?"SLAB":"FLAT"):"";
                      flatRatesDOB.setChargeRate(Double.parseDouble(request.getParameter(slabBothValueInd)));
                      flatRatesDOB.setChargeRateIndicator(chargeIndForSlabBoth);
                      flatRatesDOB.setTypeofcharge(surchargeDesc[k]+'-'+(surchargeIds[k]).substring(3)); //kishore
                      flatRatesDOB.setSurchargeId(surchargeIds[k]);
                      flatRatesDOB.setWeightClass(weightClass);
                      if( flatRatesDOB.getWtBreakSlab()!=null) 
             		      lanes.add(flatRatesDOB);
                      lineno++;
        		  }//inner if ends    
        		  }//slbbraks for ends
                 }//on if ends	  
       		 		  
     	  }
       	  else{//slab else begins
       	 for(int j=0;j<srcwatBrakslen;j++)
       	 {
       		 if("on".equalsIgnoreCase(request.getParameter(surchargeIds[k]+i)))
       		 {
       			 flatRatesDOB = new FlatRatesDOB();
                    flatRatesDOB.setOrigin		(request.getParameter("origin"+i)		    != null ? request.getParameter("origin"+i)		 : "");
                    flatRatesDOB.setDestination	(request.getParameter("destination"+i)		!= null ? request.getParameter("destination"+i)	 : "");
                    if(request.getParameter("effDate"+i)			!= null)
                    flatRatesDOB.setEffDate		(fomater.getTimestamp("DD-MM-YY",request.getParameter("effDate"+i)));
                    flatRatesDOB.setFrequency	(frequency);  
                    flatRatesDOB.setTransittime	(request.getParameter("transitTime"+i)		!= null ? request.getParameter("transitTime"+i)	 : "");
                    flatRatesDOB.setSurchargeCurrency((request.getParameter("surchargeCurrency"+(k-1)+i)!=null)?request.getParameter("surchargeCurrency"+(k-1)+i):"");//Added By Govind for the CR-219973
                    //Modified by Mohan for Issue No.219976 on 28-10-2010
                    flatRatesDOB.setExtNotes(request.getParameter("extNotes"+i));
                    //flatRatesDOB.setChargeRate(Double.parseDouble(request.getParameter("min"+i)));
                    flatRatesDOB.setNotes(request.getParameter("notes"+i));
                    flatRatesDOB.setDensityRatio((request.getParameter("densityRatio"+i)!=null)?request.getParameter("densityRatio"+i):"");//added by rk
                    flatRatesDOB.setLowerBound(0);
                    flatRatesDOB.setUpperBound(0);
                    flatRatesDOB.setLaneNo(i);
                    flatRatesDOB.setLineNo(lineno);
                    flatRatesDOB.setServiceLevel("SCH");
                    flatRatesDOB.setWtBreakSlab(surchargeIds[k]+srcwatBraks[j]);
                    flatRatesDOB.setChargeRate(Double.parseDouble(request.getParameter(surchargeIds[k]+srcwatBraks[j]+i)));
                    flatRatesDOB.setTypeofcharge(surchargeDesc[k]+'-'+(surchargeIds[k]).substring(3)); //kishore
                    flatRatesDOB.setSurchargeId(surchargeIds[k]);
                    flatRatesDOB.setWeightClass(weightClass);
                    if( flatRatesDOB.getWtBreakSlab()!=null) 
            		      lanes.add(flatRatesDOB);
                    lineno++;
                    
                  
       		 }//end if
       	 }//end j loop
        } //slab else end
        }//end k
		}
        
        ratelaneslist.add(lanes);//@@Added for the WPBN issues-146448,146968
         }
        
    
            home  = (BuyRatesSessionHome)LookUpBean.getEJBHome("BuyRatesSessionBean");
               remote	= (BuyRatesSession)home.create();
            rateDOB	= (RateDOB)session.getAttribute("rateDOB");
                  rateDOB	= (RateDOB)session.getAttribute("rateDOB");
            rateDOB.setWeightClass(weightClass);
            //result	=  remote.crateBuyFlatRate(rateDOB,lanes);
        // list_returnList	=   remote.crateBuyFlatRate(rateDOB,lanes);
       list_returnList	=   remote.crateBuyFlatRate(rateDOB,ratelaneslist);//@@Modified for the WPBN issues-146448,146968 on 18/12/08
            
        sb_successErrorMsg     = (StringBuffer)list_returnList.get(0);
        sb_failErrorMsg        = (StringBuffer)list_returnList.get(1);
        
        session.removeAttribute("lanes");
        session.removeAttribute("rateDOB");
        session.removeAttribute("surchargeIds");
        session.removeAttribute("surchargeDesc");
        session.removeAttribute("surchargeWeightBreaks");
        session.removeAttribute("rateDOB");
        session.removeAttribute("wtbrak");  
        session.removeAttribute("listValues");  
        
        errors = new StringBuffer();
      
      if(sb_successErrorMsg!=null && sb_successErrorMsg.length()>0)
      {
        errors.append("Records Successfully Inserted for the following lanes:\n"+sb_successErrorMsg.toString());
        
      }
      
      if(sb_failErrorMsg!=null && sb_failErrorMsg.length()>0)
      {
        errors.append("The following lanes are not Inserted As rates already defined at higher levels :\n"+sb_failErrorMsg.toString());
      }

                  
            errorMessageObject	= new ErrorMessage(errors.toString(),"BuyRatesController?Operation=Add"); 
		
			keyValueList.add(new KeyValue("ErrorCode","RSI")); 	
			keyValueList.add(new KeyValue("Operation",operation)); 	

			errorMessageObject.setKeyValueList(keyValueList);
			request.setAttribute("ErrorMessage",errorMessageObject);
			request.setAttribute("ErrorMessage",errorMessageObject);
			session.setAttribute("ErrorMessage","Buy Rates added Sucessfully");
			session.setAttribute("ErrorCode","RSI");
			session.setAttribute("NextNavigation","BuyRatesController?Operation=Add");

			nextNavigation	=	"ESupplyErrorPage.jsp";
		}  
    }
	else if("SlabBoth".equalsIgnoreCase(rateDtls))
    {
		int lineno =0;
     rateDOB = (RateDOB)session.getAttribute("rateDOB");
     surchargeIds	= (String[])session.getAttribute("surchargeIds");//Govind
	 surchargeDesc	= (String[])session.getAttribute("surchargeDesc");//Govind
	surchargeWeightBreaks	= (String[])session.getAttribute("surchargeWeightBreaks");//Govind
    
			detailRateDob =detailRateDOB(request,response,rateDtls,rateDOB);  
			if((detailRateDob !=null) && !"".equals(detailRateDob))
			{
				request.setAttribute("detailRateDob", detailRateDob);
				nextNavigation = "etrans/BuyRatesAddSlabBoth.jsp?rows=0";

			}else
			{
    				
      wtbrakValues	=	(String[])session.getAttribute("wtbrak");
      weightClass  = request.getParameter("weightClass");
      
      	for(int i=0;i<=preRows;i++)
        {
          lanes   =  new ArrayList();//@@Added for the WPBN issues-146448,146968 on 18/12/08
			if("on".equalsIgnoreCase(request.getParameter(""+i)))
			{
          frequency = "";
            if(request.getParameter("frequency"+i)		!= null && !"".equals(request.getParameter("frequency"+i)))
            {
              if(!"2".equals(rateDOB.getShipmentMode()))
              {
                frequency = getSortedNums(request.getParameter("frequency"+i).trim());
              }else
              {
                frequency = request.getParameter("frequency"+i).trim();
              }               
            }
            	int wtbrakValuesLen		=	wtbrakValues.length;
            	
            	for(int fr=0;fr<2;fr++){
            		flatRatesDOB = new FlatRatesDOB();
  				    
  						flatRatesDOB.setOrigin		(request.getParameter("origin"+i)		    != null ? request.getParameter("origin"+i)		 : "");
  						flatRatesDOB.setDestination	(request.getParameter("destination"+i)		!= null ? request.getParameter("destination"+i)	 : "");
  						flatRatesDOB.setServiceLevel(request.getParameter("serviceLevel"+i)		!= null ? request.getParameter("serviceLevel"+i) : "");
  				if(request.getParameter("effDate"+i)			!= null)
  						flatRatesDOB.setEffDate		(fomater.getTimestamp("DD-MM-YY",request.getParameter("effDate"+i)));
  		  	    flatRatesDOB.setFrequency	(frequency);  
        			flatRatesDOB.setTransittime	(request.getParameter("transitTime"+i)		!= null ? request.getParameter("transitTime"+i)	 : "");
  				  	flatRatesDOB.setNotes(request.getParameter("notes"+i));
  				//Modified by Mohan for Issue No.219976 on 28-10-2010
  		            flatRatesDOB.setExtNotes(request.getParameter("extNotes"+i));
              flatRatesDOB.setDensityRatio((request.getParameter("densityRatio"+i)!=null)?request.getParameter("densityRatio"+i):"");//added by rk
                     if(fr==0){
                    	flatRatesDOB.setWtBreakSlab("BASIC");
  						flatRatesDOB.setLowerBound(0);
  						flatRatesDOB.setUpperBound(0);
  						flatRatesDOB.setChargeRate(Double.parseDouble(request.getParameter("basic"+i)!= null && !"".equals(request.getParameter("basic"+i))?request.getParameter("basic"+i) :"0.00"));
  						
                      }
                      else if(fr==1)
  					{
                    	flatRatesDOB.setWtBreakSlab("MIN");
                    	flatRatesDOB.setLowerBound(0);
  						flatRatesDOB.setUpperBound(0);
  						flatRatesDOB.setChargeRate(Double.parseDouble(request.getParameter("min"+i)));
  					}
                      
                    flatRatesDOB.setLaneNo(i);
  					flatRatesDOB.setLineNo(fr);
  					flatRatesDOB.setTypeofcharge("A FREIGHT RATE");
  					lanes.add(flatRatesDOB);
  					lineno++;
            		
            	}//for basic and min freight rates to post in db
            	
            	
				for (int j=0 ; j<wtbrakValuesLen;j++)
			  {
					
				   flatRatesDOB = new FlatRatesDOB();
				  if(!"".equals(wtbrakValues[j]))
					{

					  
						flatRatesDOB.setOrigin		(request.getParameter("origin"+i)		    != null ? request.getParameter("origin"+i)		 : "");
						flatRatesDOB.setDestination	(request.getParameter("destination"+i)		!= null ? request.getParameter("destination"+i)	 : "");
						flatRatesDOB.setServiceLevel(request.getParameter("serviceLevel"+i)		!= null ? request.getParameter("serviceLevel"+i) : "");
				if(request.getParameter("effDate"+i)			!= null)
				flatRatesDOB.setEffDate		(fomater.getTimestamp("DD-MM-YY",request.getParameter("effDate"+i)));
		  	    flatRatesDOB.setFrequency	(frequency);  
      			flatRatesDOB.setTransittime	(request.getParameter("transitTime"+i)		!= null ? request.getParameter("transitTime"+i)	 : "");
      			 //Modified by Mohan for Issue No.219976 on 28-10-2010
                flatRatesDOB.setExtNotes(request.getParameter("extNotes"+i));
				flatRatesDOB.setNotes(request.getParameter("notes"+i));
                flatRatesDOB.setDensityRatio((request.getParameter("densityRatio"+i)!=null)?request.getParameter("densityRatio"+i):"");//added by rk
                flatRatesDOB.setWtBreakSlab(wtbrakValues[j]);
                     if(j==0)
					{
						flatRatesDOB.setLowerBound(0);
						flatRatesDOB.setUpperBound(Double.parseDouble(wtbrakValues[j].substring(0,wtbrakValues[j].length())));
						
						if(request.getParameterValues("wtbrks"+i)[j] != null && !"".equals(request.getParameterValues("wtbrks"+i)[j]))
						{
						flatRatesDOB.setChargeRateIndicator("SLAB");
						flatRatesDOB.setChargeRate(Double.parseDouble(request.getParameterValues("wtbrks"+i)[j]));
						}else{
					    flatRatesDOB.setChargeRateIndicator("FLAT");
						flatRatesDOB.setChargeRate(Double.parseDouble(request.getParameterValues("wtbrkf"+i)[j]));
		      	}
					}else 
					{
						flatRatesDOB.setLowerBound(Double.parseDouble(wtbrakValues[j]));
						flatRatesDOB.setUpperBound(Double.parseDouble(j==(request.getParameterValues("wtbrks"+i).length-1)?"100000":wtbrakValues[j+1]));

						if(request.getParameterValues("wtbrks"+i)[j] != null && !"".equals(request.getParameterValues("wtbrks"+i)[j]))
						{
						flatRatesDOB.setChargeRateIndicator("SLAB");
						flatRatesDOB.setChargeRate(Double.parseDouble(request.getParameterValues("wtbrks"+i)[j]));
						}else{

						flatRatesDOB.setChargeRateIndicator("FLAT");
						flatRatesDOB.setChargeRate(Double.parseDouble(request.getParameterValues("wtbrkf"+i)[j]));
 			   	}
					}
			        flatRatesDOB.setLaneNo(i);
					flatRatesDOB.setLineNo(lineno);
                    flatRatesDOB.setTypeofcharge("A FREIGHT RATE");
					lanes.add(flatRatesDOB);
					lineno++;
					}
			
				
			  }
				if(surchargeIds!=null){//Added by Rakesh for Issue:
     	        surChargeIdlen = surchargeIds.length;
		        for(int k=1;k<surChargeIdlen;k++)
		        {   
		       	 srcwatBraks = surchargeWeightBreaks[k].split(",");
		       	  srcwatBrakslen = srcwatBraks.length;
		       	  if(srcwatBraks[0].equalsIgnoreCase("slab")&& srcwatBrakslen ==1){
		       		  if("on".equalsIgnoreCase(request.getParameter(surchargeIds[k]+i)))
		        		 {
		       		  for(int slbbraks=0;slbbraks<=11;slbbraks++){
		       			  System.out.println(request.getParameter("srslabbreaks"+i+(k-1)+slbbraks));
		       			  if((request.getParameter("srslabbreaks"+i+(k-1)+slbbraks)!=null) && (!"".equals(request.getParameter("srslabbreaks"+i+(k-1)+slbbraks)))){
		       			  flatRatesDOB = new FlatRatesDOB();
		                     flatRatesDOB.setOrigin		(request.getParameter("origin"+i)		    != null ? request.getParameter("origin"+i)		 : "");
		                     flatRatesDOB.setDestination	(request.getParameter("destination"+i)		!= null ? request.getParameter("destination"+i)	 : "");
		                     if(request.getParameter("effDate"+i)			!= null)
		                     flatRatesDOB.setEffDate		(fomater.getTimestamp("DD-MM-YY",request.getParameter("effDate"+i)));
		                     flatRatesDOB.setFrequency	(frequency);  
		                     flatRatesDOB.setTransittime	(request.getParameter("transitTime"+i)		!= null ? request.getParameter("transitTime"+i)	 : "");
		                     flatRatesDOB.setSurchargeCurrency((request.getParameter("surchargeCurrency"+(k-1)+i)!=null)?request.getParameter("surchargeCurrency"+(k-1)+i):"");//Added By Govind for the CR-219973
		                     //Modified by Mohan for Issue No.219976 on 28-10-2010
		                     flatRatesDOB.setExtNotes(request.getParameter("extNotes"+i));
		                     //flatRatesDOB.setChargeRate(Double.parseDouble(request.getParameter("min"+i)));
		                     flatRatesDOB.setNotes(request.getParameter("notes"+i));
		                     flatRatesDOB.setDensityRatio((request.getParameter("densityRatio"+i)!=null)?request.getParameter("densityRatio"+i):"");//added by rk
		                     if(slbbraks == 0){
		                     flatRatesDOB.setLowerBound(0);
		                     flatRatesDOB.setUpperBound(0);
		                     }else if(slbbraks == 1){
		                   	  flatRatesDOB.setLowerBound(0);
		                   	  flatRatesDOB.setUpperBound(Double.parseDouble(
		                   			  request.getParameter("srslabbreaks"+i+(k-1)+slbbraks).substring(1)));
		                     }else{
		                    	   if(slbbraks == 11){
		                    		   flatRatesDOB.setLowerBound(Double.parseDouble(request.getParameter("srslabbreaks"+i+(k-1)+slbbraks)));              
		                          	   flatRatesDOB.setUpperBound(Double.parseDouble("1000000"));
		                         	                      		   
		                    	   }else{
		                   	  flatRatesDOB.setLowerBound(Double.parseDouble(request.getParameter("srslabbreaks"+i+(k-1)+slbbraks)));              
		                   	  flatRatesDOB.setUpperBound(Double.parseDouble(request.getParameter("srslabbreaks"+i+(k-1)+(slbbraks+1)).length()>1?
		                   			  request.getParameter("srslabbreaks"+i+(k-1)+(slbbraks+1)):"100000"));		  
		                     }
		                       }
		                     flatRatesDOB.setLaneNo(i);
		                     flatRatesDOB.setLineNo(lineno);
		                     flatRatesDOB.setServiceLevel("SCH");
		                     flatRatesDOB.setWtBreakSlab(request.getParameter("srslabbreaks"+i+(k-1)+slbbraks));
		                     flatRatesDOB.setChargeRate(Double.parseDouble(request.getParameter("srslabvalues"+i+(k-1)+slbbraks)));
		                     flatRatesDOB.setTypeofcharge(surchargeDesc[k]+'-'+(surchargeIds[k]).substring(3)); //kishore
		                     flatRatesDOB.setSurchargeId(surchargeIds[k]);
		                     flatRatesDOB.setWeightClass(weightClass);
		                     if( flatRatesDOB.getWtBreakSlab()!=null) 
		            		      lanes.add(flatRatesDOB);
		                     lineno++;
		       		  }//inner if ends    
		       		  }//slbbraks for ends
		                }//on if ends	  
		        
		       	  }//slab if ends
		       	  else if(srcwatBraks[0].equalsIgnoreCase("both")&& srcwatBrakslen ==1){
		       		  
		       		  if("on".equalsIgnoreCase(request.getParameter(surchargeIds[k]+i)))
		         		 {
		       			  
		        		  for(int slbbraks=0;slbbraks<=11;slbbraks++){
		        			  System.out.println(request.getParameter("srslabbreaks"+i+(k-1)+slbbraks));
		        			  if((request.getParameter("srslabbreaks"+i+(k-1)+slbbraks)!=null) && (!"".equals(request.getParameter("srslabbreaks"+i+(k-1)+slbbraks)))){
		        			  flatRatesDOB = new FlatRatesDOB();
		                      flatRatesDOB.setOrigin		(request.getParameter("origin"+i)		    != null ? request.getParameter("origin"+i)		 : "");
		                      flatRatesDOB.setDestination	(request.getParameter("destination"+i)		!= null ? request.getParameter("destination"+i)	 : "");
		                      if(request.getParameter("effDate"+i)			!= null)
		                      flatRatesDOB.setEffDate		(fomater.getTimestamp("DD-MM-YY",request.getParameter("effDate"+i)));
		                      flatRatesDOB.setFrequency	(frequency);  
		                      flatRatesDOB.setTransittime	(request.getParameter("transitTime"+i)		!= null ? request.getParameter("transitTime"+i)	 : "");
		                      flatRatesDOB.setSurchargeCurrency((request.getParameter("surchargeCurrency"+(k-1)+i)!=null)?request.getParameter("surchargeCurrency"+(k-1)+i):"");//Added By Govind for the CR-219973
		                      //flatRatesDOB.setChargeRate(Double.parseDouble(request.getParameter("min"+i)));
		                      //Modified by Mohan for Issue No.219976 on 28-10-2010
		                      flatRatesDOB.setExtNotes(request.getParameter("extNotes"+i));
		                      flatRatesDOB.setNotes(request.getParameter("notes"+i));
		                      flatRatesDOB.setDensityRatio((request.getParameter("densityRatio"+i)!=null)?request.getParameter("densityRatio"+i):"");//added by rk
		                      if(slbbraks == 0){
		                      flatRatesDOB.setLowerBound(0);
		                      flatRatesDOB.setUpperBound(0);
		                      }else if(slbbraks == 1){
		                    	  flatRatesDOB.setLowerBound(0);
		                    	  flatRatesDOB.setUpperBound(Double.parseDouble(
		                    			  request.getParameter("srslabbreaks"+i+(k-1)+slbbraks).substring(1)));
		                      }else{
		                    	   if(slbbraks == 11){
		                    		   flatRatesDOB.setLowerBound(Double.parseDouble(request.getParameter("srslabbreaks"+i+(k-1)+slbbraks)));              
		                          	   flatRatesDOB.setUpperBound(Double.parseDouble("1000000"));
		                         	                      		   
		                    	   }else{
		                    	  flatRatesDOB.setLowerBound(Double.parseDouble(request.getParameter("srslabbreaks"+i+(k-1)+slbbraks)));              
		                    	  flatRatesDOB.setUpperBound(Double.parseDouble(request.getParameter("srslabbreaks"+i+(k-1)+(slbbraks+1)).length()>1?
		                    			  request.getParameter("srslabbreaks"+i+(k-1)+(slbbraks+1)):"1000000"));		  
		                      }
		                       }
		                      flatRatesDOB.setLaneNo(i);
		                      flatRatesDOB.setLineNo(lineno);
		                      flatRatesDOB.setServiceLevel("SCH");
		                      flatRatesDOB.setWtBreakSlab(request.getParameter("srslabbreaks"+i+(k-1)+slbbraks));
		                      slabBothValueInd   = (request.getParameter("srslabvalue"+i+(k-1)+slbbraks)!=null && !"".equals(request.getParameter("srslabvalue"+i+(k-1)+slbbraks)))?"srslabvalue"+i+(k-1)+slbbraks:"srflatvalue"+i+(k-1)+slbbraks;
		                      chargeIndForSlabBoth = (slbbraks!=0)?((request.getParameter("srslabvalue"+i+(k-1)+slbbraks)!=null && !"".equals(request.getParameter("srslabvalue"+i+(k-1)+slbbraks)))?"SLAB":"FLAT"):"";
		                      flatRatesDOB.setChargeRate(Double.parseDouble(request.getParameter(slabBothValueInd)));
		                      flatRatesDOB.setChargeRateIndicator(chargeIndForSlabBoth);
		                      flatRatesDOB.setTypeofcharge(surchargeDesc[k]+'-'+(surchargeIds[k]).substring(3)); //kishore
		                      flatRatesDOB.setSurchargeId(surchargeIds[k]);
		                      flatRatesDOB.setWeightClass(weightClass);
		                      if( flatRatesDOB.getWtBreakSlab()!=null) 
		             		      lanes.add(flatRatesDOB);
		                      lineno++;
		        		  }//inner if ends    
		        		  }//slbbraks for ends
		                 }//on if ends	  
		       		 		  
		     	  }
		       	  else{//slab else begins
		       	 for(int j=0;j<srcwatBrakslen;j++)
		       	 {
		       		 if("on".equalsIgnoreCase(request.getParameter(surchargeIds[k]+i)))
		       		 {
		       			 flatRatesDOB = new FlatRatesDOB();
		                    flatRatesDOB.setOrigin		(request.getParameter("origin"+i)		    != null ? request.getParameter("origin"+i)		 : "");
		                    flatRatesDOB.setDestination	(request.getParameter("destination"+i)		!= null ? request.getParameter("destination"+i)	 : "");
		                    if(request.getParameter("effDate"+i)			!= null)
		                    flatRatesDOB.setEffDate		(fomater.getTimestamp("DD-MM-YY",request.getParameter("effDate"+i)));
		                    flatRatesDOB.setFrequency	(frequency);  
		                    flatRatesDOB.setTransittime	(request.getParameter("transitTime"+i)		!= null ? request.getParameter("transitTime"+i)	 : "");
		                    flatRatesDOB.setSurchargeCurrency((request.getParameter("surchargeCurrency"+(k-1)+i)!=null)?request.getParameter("surchargeCurrency"+(k-1)+i):"");//Added By Govind for the CR-219973
		                    //Modified by Mohan for Issue No.219976 on 28-10-2010
		                    flatRatesDOB.setExtNotes(request.getParameter("extNotes"+i));
		                    //flatRatesDOB.setChargeRate(Double.parseDouble(request.getParameter("min"+i)));
		                    flatRatesDOB.setNotes(request.getParameter("notes"+i));
		                    flatRatesDOB.setDensityRatio((request.getParameter("densityRatio"+i)!=null)?request.getParameter("densityRatio"+i):"");//added by rk
		                    flatRatesDOB.setLowerBound(0);
		                    flatRatesDOB.setUpperBound(0);
		                    flatRatesDOB.setLaneNo(i);
		                    flatRatesDOB.setLineNo(lineno);
		                    flatRatesDOB.setServiceLevel("SCH");
		                    flatRatesDOB.setWtBreakSlab(surchargeIds[k]+srcwatBraks[j]);
		                    flatRatesDOB.setChargeRate(Double.parseDouble(request.getParameter(surchargeIds[k]+srcwatBraks[j]+i)));
		                    flatRatesDOB.setTypeofcharge(surchargeDesc[k]+'-'+(surchargeIds[k]).substring(3)); //kishore
		                    flatRatesDOB.setSurchargeId(surchargeIds[k]);
		                    flatRatesDOB.setWeightClass(weightClass);
		                    if( flatRatesDOB.getWtBreakSlab()!=null) 
		            		      lanes.add(flatRatesDOB);
		                    lineno++;
		                    
		                  
		       		 }//end if
		       	 }//end j loop
		        } //slab else end
		        }//end k
			}
		        
			}//end main if
        }//end for
		        
		               	  
		          ratelaneslist.add(lanes);//@@Added for the WPBN issues-146448,146968
		         						
				
          home		= (BuyRatesSessionHome)LookUpBean.getEJBHome("BuyRatesSessionBean");
          remote	= (BuyRatesSession)home.create();
          rateDOB	=	(RateDOB)session.getAttribute("rateDOB");
          
      rateDOB.setWeightClass(weightClass);
		  //result	=   remote.crateBuyFlatRate(rateDOB,lanes);
     // list_returnList	=   remote.crateBuyFlatRate(rateDOB,lanes);
      list_returnList	=   remote.crateBuyFlatRate(rateDOB,ratelaneslist);//@@Modified for the WPBN issues-146448,146968 on 18/12/08
      sb_successErrorMsg     = (StringBuffer)list_returnList.get(0);
      sb_failErrorMsg        = (StringBuffer)list_returnList.get(1);
      
			session.removeAttribute("lanes");
			session.removeAttribute("rateDOB");
			session.removeAttribute("surchargeIds");
            session.removeAttribute("surchargeDesc");
            session.removeAttribute("surchargeWeightBreaks");
			session.removeAttribute("rateDOB");
			session.removeAttribute("wtbrak");  
			session.removeAttribute("listValues");  
      errors = new StringBuffer();
      if(sb_successErrorMsg!=null && sb_successErrorMsg.length()>0)
      {
        errors.append("Records Successfully Inserted for the following lanes:\n"+sb_successErrorMsg.toString());
        
      }
      
      if(sb_failErrorMsg!=null && sb_failErrorMsg.length()>0)
      {
        errors.append("The following lanes are not Inserted As rates already defined at higher levels :\n"+sb_failErrorMsg.toString());
      }
      errorMessageObject = new ErrorMessage(errors.toString(),"BuyRatesController?Operation=Add"); 
      
			keyValueList.add(new KeyValue("ErrorCode","RSI")); 	
			keyValueList.add(new KeyValue("Operation",operation)); 	

			errorMessageObject.setKeyValueList(keyValueList);
			request.setAttribute("ErrorMessage",errorMessageObject);
			request.setAttribute("ErrorMessage",errorMessageObject);
			session.setAttribute("ErrorMessage","Buy Rates added Sucessfully");
			session.setAttribute("ErrorCode","RSI");
			session.setAttribute("NextNavigation","BuyRatesController?Operation=Add");
			nextNavigation	=	"ESupplyErrorPage.jsp";
	 
    }
    }else if("ListList".equalsIgnoreCase(rateDtls))
	{
    	int lineno =0;
  rateDOB = (RateDOB)session.getAttribute("rateDOB");
  surchargeIds	= (String[])session.getAttribute("surchargeIds");//Govind
  surchargeDesc	= (String[])session.getAttribute("surchargeDesc");//Govind
  surchargeWeightBreaks	= (String[])session.getAttribute("surchargeWeightBreaks");//Govind

		detailRateDob =detailRateDOB(request,response,rateDtls,rateDOB);
    
		if((detailRateDob !=null) && !"".equals(detailRateDob))
			{
				request.setAttribute("detailRateDob", detailRateDob);
				nextNavigation = "etrans/BuyRatesAddList.jsp?rows=0";

			}else
			{
		  wtbrakValues	=	(String[])session.getAttribute("listValues");
        weightClass  = request.getParameter("weightClass");
        //rateDOB.setWeightClass(weightClass);
        
	    	for(int i=0;i<=preRows;i++)
 	 	    {
            lanes   =   new ArrayList();//@@Added for the WPBN issues-146448,146968 on 18/12/08
    		if("on".equalsIgnoreCase(request.getParameter(""+i)))
			{
            frequency = "";
            if(request.getParameter("frequency"+i)		!= null && !"".equals(request.getParameter("frequency"+i)))
            {
              if(!"2".equals(rateDOB.getShipmentMode()))
              {
                frequency = getSortedNums(request.getParameter("frequency"+i).trim());
              }else
              {
                frequency = request.getParameter("frequency"+i).trim();
              }               
            }  
            int wtbrakValuesLen		=	wtbrakValues.length;
         for (int j=0 ; j<wtbrakValuesLen;j++)
			  {
        	     
        	 flatRatesDOB = new FlatRatesDOB();
			 
          if(!"".equals(wtbrakValues[j]))
          {
            flatRatesDOB.setOrigin		(request.getParameter("origin"+i)		    != null ? request.getParameter("origin"+i)		 : "");
            flatRatesDOB.setDestination	(request.getParameter("destination"+i)		!= null ? request.getParameter("destination"+i)	 : "");
            flatRatesDOB.setServiceLevel(request.getParameter("serviceLevel"+i)		!= null ? request.getParameter("serviceLevel"+i) : "");
            flatRatesDOB.setDensityRatio((request.getParameter("densityRatio"+i)!=null)?request.getParameter("densityRatio"+i):"");//added by rk
            flatRatesDOB.setNotes(request.getParameter("notes"+i));
          //Modified by Mohan for Issue No.219976 on 28-10-2010
            flatRatesDOB.setExtNotes(request.getParameter("extNotes"+i));
            if(request.getParameter("effDate"+i)			!= null)
            flatRatesDOB.setEffDate		(fomater.getTimestamp("DD-MM-YY",request.getParameter("effDate"+i)));
            flatRatesDOB.setFrequency	(frequency);  
            flatRatesDOB.setTransittime	(request.getParameter("transitTime"+i)		!= null ? request.getParameter("transitTime"+i)	 : "");
            flatRatesDOB.setChargeRate(Double.parseDouble(request.getParameterValues("list"+i)[j]));
            flatRatesDOB.setWtBreakSlab(wtbrakValues[j]);
            flatRatesDOB.setChargeRate(Double.parseDouble(request.getParameterValues("list"+i)[j]));
            flatRatesDOB.setLowerBound(0);
            flatRatesDOB.setUpperBound(0);
            flatRatesDOB.setTypeofcharge("A FREIGHT RATE");
            flatRatesDOB.setLaneNo(i);
           /* flatRatesDOB.setLineNo(j+1);
            
            if(wtbrakValues.length==(j+1))
              flatRatesDOB.setLineNo(0);*/
              flatRatesDOB.setLineNo(j); //@@Modified by Kameswari for thw WPBN issue-148239
          lanes.add(flatRatesDOB);	
          lineno++;
           }
        }
      }// freight rate ends
    	    		
        int n =wtbrakValues.length; 
        if(surchargeIds!=null){//Added by Rakesh for Issue:  
        surChargeIdlen = surchargeIds.length;
        for(int k=1;k<surChargeIdlen;k++)
        { 
        	 if("on".equalsIgnoreCase(request.getParameter(surchargeIds[k]+i)))
    		 {
                for(int srlstval=0;srlstval<n;srlstval++)//srlstval----surchargelistvalue
                {
                	if(!"".equals(wtbrakValues[srlstval]))
                    {    
                		 flatRatesDOB = new FlatRatesDOB();
                    	 flatRatesDOB.setOrigin		(request.getParameter("origin"+i)		    != null ? request.getParameter("origin"+i)		 : "");
                         flatRatesDOB.setDestination	(request.getParameter("destination"+i)		!= null ? request.getParameter("destination"+i)	 : "");
                         flatRatesDOB.setServiceLevel("SCH");
                         flatRatesDOB.setDensityRatio((request.getParameter("densityRatio"+i)!=null)?request.getParameter("densityRatio"+i):"");//added by rk
                         //Modified by Mohan for Issue No.219976 on 28-10-2010
                         flatRatesDOB.setExtNotes(request.getParameter("extNotes"+i));
                         flatRatesDOB.setNotes(request.getParameter("notes"+i));
                         flatRatesDOB.setSurchargeCurrency((request.getParameter("surchargeCurrency"+(k-1)+i)!=null)?request.getParameter("surchargeCurrency"+(k-1)+i):"");//Added By Govind for the CR-219973
                         if(request.getParameter("effDate"+i)			!= null)
                         flatRatesDOB.setEffDate		(fomater.getTimestamp("DD-MM-YY",request.getParameter("effDate"+i)));
                         flatRatesDOB.setFrequency	(frequency);  
                         flatRatesDOB.setTransittime	(request.getParameter("transitTime"+i)		!= null ? request.getParameter("transitTime"+i)	 : "");
                         flatRatesDOB.setChargeRate(Double.parseDouble(request.getParameterValues("list"+i)[srlstval]));
                         flatRatesDOB.setWtBreakSlab(wtbrakValues[srlstval]+surchargeIds[k]);
                         flatRatesDOB.setChargeRate(Double.parseDouble(request.getParameter("srlistvalue"+i+(k-1)+srlstval)));
                         flatRatesDOB.setLowerBound(0);
                         flatRatesDOB.setUpperBound(0);
                         flatRatesDOB.setTypeofcharge(surchargeDesc[k]+'-'+(surchargeIds[k]).substring(3)); //kishore
                         flatRatesDOB.setSurchargeId(surchargeIds[k]);
                         flatRatesDOB.setLaneNo(i);
                        /* flatRatesDOB.setLineNo(j+1);
                         
                         if(wtbrakValues.length==(j+1))
                           flatRatesDOB.setLineNo(0);*/
                         flatRatesDOB.setLineNo(lineno); //@@Modified by Kameswari for thw WPBN issue-148239
                       lanes.add(flatRatesDOB);	
                       lineno++;
                    }
                }
    		 }//on if ends
        }//k loop ends
 	 	    }
			}
         /*if("2".equals(rateDOB.getShipmentMode()))
         {
           if("on".equalsIgnoreCase(request.getParameter("baf"+i)))
			    {
           
            for (int j=0 ; j<n;j++)
			  {
				   flatRatesDOB = new FlatRatesDOB();
			
				if(!"".equals(wtbrakValues[j]))
				{
					flatRatesDOB.setOrigin		(request.getParameter("origin"+i)		    != null ? request.getParameter("origin"+i)		 : "");
					flatRatesDOB.setDestination	(request.getParameter("destination"+i)		!= null ? request.getParameter("destination"+i)	 : "");
					flatRatesDOB.setServiceLevel("SCH");
          flatRatesDOB.setDensityRatio((request.getParameter("densityRatio"+i)!=null)?request.getParameter("densityRatio"+i):"");//added by rk
    			if(request.getParameter("effDate"+i)			!= null)
						flatRatesDOB.setEffDate		(fomater.getTimestamp("DD-MM-YY",request.getParameter("effDate"+i)));
					flatRatesDOB.setFrequency	(frequency);  
          flatRatesDOB.setNotes(request.getParameter("notes"+i));
					flatRatesDOB.setTransittime	(request.getParameter("transitTime"+i)		!= null ? request.getParameter("transitTime"+i)	 : "");
				//	flatRatesDOB.setChargeRate(Double.parseDouble(request.getParameterValues("list"+i)[j]));
		     if(request.getParameterValues("baflist"+i)[j]!=null&&request.getParameterValues("baflist"+i)[j].trim().length()>0)
          {
					  flatRatesDOB.setChargeRate(Double.parseDouble(request.getParameterValues("baflist"+i)[j]));
            flatRatesDOB.setWtBreakSlab(wtbrakValues[j]+"BAF");
          }
				     flatRatesDOB.setLowerBound(0);
					flatRatesDOB.setUpperBound(0);
          flatRatesDOB.setTypeofcharge("BAF");
					flatRatesDOB.setLaneNo(i);
					flatRatesDOB.setLineNo(n);
					lanes.add(flatRatesDOB);	
          n++;
           }
        }
           }
           int wtbrakValuesLen		=	wtbrakValues.length;
             if("on".equalsIgnoreCase(request.getParameter("caf"+i)))
			{
          for (int j=0 ; j<wtbrakValuesLen;j++)
			  {
				   flatRatesDOB = new FlatRatesDOB();
			
				if(!"".equals(wtbrakValues[j]))
				{
					flatRatesDOB.setOrigin		(request.getParameter("origin"+i)		    != null ? request.getParameter("origin"+i)		 : "");
					flatRatesDOB.setDestination	(request.getParameter("destination"+i)		!= null ? request.getParameter("destination"+i)	 : "");
        	flatRatesDOB.setServiceLevel("SCH");   
           flatRatesDOB.setDensityRatio((request.getParameter("densityRatio"+i)!=null)?request.getParameter("densityRatio"+i):"");//added by rk
           flatRatesDOB.setNotes(request.getParameter("notes"+i));
 				  if(request.getParameter("effDate"+i)			!= null)
						flatRatesDOB.setEffDate		(fomater.getTimestamp("DD-MM-YY",request.getParameter("effDate"+i)));
  				flatRatesDOB.setFrequency	(frequency);  
 					flatRatesDOB.setTransittime	(request.getParameter("transitTime"+i)		!= null ? request.getParameter("transitTime"+i)	 : "");
					//flatRatesDOB.setChargeRate(Double.parseDouble(request.getParameterValues("list"+i)[j]));
				
          if(request.getParameterValues("caflist"+i)[j]!=null&&request.getParameterValues("caflist"+i)[j].trim().length()>0)
					 {
            flatRatesDOB.setChargeRate(Double.parseDouble(request.getParameterValues("caflist"+i)[j]));
           	flatRatesDOB.setWtBreakSlab(wtbrakValues[j]+"CAF");
           }
        	flatRatesDOB.setLowerBound(0);
					flatRatesDOB.setUpperBound(0);
					flatRatesDOB.setLaneNo(i);
					flatRatesDOB.setLineNo(n);
           flatRatesDOB.setTypeofcharge("CAF%");
				 lanes.add(flatRatesDOB);	
          n++;
           }
					}
           }
             
		    if("on".equalsIgnoreCase(request.getParameter("css"+i)))
		   	{
         for (int j=0 ; j<wtbrakValuesLen;j++)
			  {
				   flatRatesDOB = new FlatRatesDOB();
			
			  	 if(!"".equals(wtbrakValues[j]))
				  {
				     flatRatesDOB.setOrigin		(request.getParameter("origin"+i)		    != null ? request.getParameter("origin"+i)		 : "");
					   flatRatesDOB.setDestination	(request.getParameter("destination"+i)		!= null ? request.getParameter("destination"+i)	 : "");
	           flatRatesDOB.setServiceLevel("SCH");
             flatRatesDOB.setDensityRatio((request.getParameter("densityRatio"+i)!=null)?request.getParameter("densityRatio"+i):"");//added by rk
             flatRatesDOB.setNotes(request.getParameter("notes"+i));
 			  	if(request.getParameter("effDate"+i)			!= null)
						flatRatesDOB.setEffDate		(fomater.getTimestamp("DD-MM-YY",request.getParameter("effDate"+i)));
				   	flatRatesDOB.setFrequency	(frequency);  
 				    flatRatesDOB.setTransittime	(request.getParameter("transitTime"+i)		!= null ? request.getParameter("transitTime"+i)	 : "");
				  //	flatRatesDOB.setChargeRate(Double.parseDouble(request.getParameterValues("list"+i)[j]));
					  
					if(request.getParameterValues("csslist"+i)[j]!=null&&request.getParameterValues("csslist"+i)[j].trim().length()>0)
          {
          	flatRatesDOB.setChargeRate(Double.parseDouble(request.getParameterValues("csslist"+i)[j]));
            flatRatesDOB.setWtBreakSlab(wtbrakValues[j]+"CSF");
          }
            flatRatesDOB.setLowerBound(0);
            flatRatesDOB.setUpperBound(0);
            flatRatesDOB.setLaneNo(i);
            flatRatesDOB.setLineNo(n);
             flatRatesDOB.setTypeofcharge("CSF");
            lanes.add(flatRatesDOB);	
            n++;
           }
					}
           }
           if("on".equalsIgnoreCase(request.getParameter("pss"+i)))
			    {
           for (int j=0 ; j<wtbrakValuesLen;j++)
          {
             flatRatesDOB = new FlatRatesDOB();
        
				   if(!"".equals(wtbrakValues[j]))
				   {
              flatRatesDOB.setOrigin		(request.getParameter("origin"+i)		    != null ? request.getParameter("origin"+i)		 : "");
              flatRatesDOB.setDestination	(request.getParameter("destination"+i)		!= null ? request.getParameter("destination"+i)	 : "");
              flatRatesDOB.setServiceLevel("SCH");
               flatRatesDOB.setDensityRatio((request.getParameter("densityRatio"+i)!=null)?request.getParameter("densityRatio"+i):"");//added by rk
               flatRatesDOB.setNotes(request.getParameter("notes"+i));
               if(request.getParameter("effDate"+i)			!= null)
              flatRatesDOB.setEffDate		(fomater.getTimestamp("DD-MM-YY",request.getParameter("effDate"+i)));
              flatRatesDOB.setFrequency	(frequency);  
              flatRatesDOB.setTransittime	(request.getParameter("transitTime"+i)		!= null ? request.getParameter("transitTime"+i)	 : "");
             
              if(request.getParameterValues("psslist"+i)[j]!=null&&request.getParameterValues("psslist"+i)[j].trim().length()>0)
              {
                flatRatesDOB.setWtBreakSlab(wtbrakValues[j]+"PSS");
                 flatRatesDOB.setChargeRate(Double.parseDouble(request.getParameterValues("psslist"+i)[j]));
              }
              flatRatesDOB.setLowerBound(0);
              flatRatesDOB.setUpperBound(0);
              flatRatesDOB.setLaneNo(i);
              flatRatesDOB.setLineNo(n);
              lanes.add(flatRatesDOB);
              
              flatRatesDOB.setTypeofcharge("PSS");
              n++;
           }
           }
			   	}
        }
        else
        {
            int value=0;
              if("1".equals(rateDOB.getShipmentMode()))
                {
               if("on".equalsIgnoreCase(request.getParameter("fs"+i)))
                     value=3;
               if("on".equalsIgnoreCase(request.getParameter("ss"+i)))       
                   value=value+3;
                }
                else
                {
                  if("on".equalsIgnoreCase(request.getParameter("surcharge"+i)))    
                  value =1;
                }
             for(int p=0;p<value;p++)
             {
            flatRatesDOB = new FlatRatesDOB();
        	flatRatesDOB.setOrigin		(request.getParameter("origin"+i)		    != null ? request.getParameter("origin"+i)		 : "");
					flatRatesDOB.setDestination	(request.getParameter("destination"+i)		!= null ? request.getParameter("destination"+i)	 : "");
	         flatRatesDOB.setServiceLevel("SCH");
          flatRatesDOB.setDensityRatio((request.getParameter("densityRatio"+i)!=null)?request.getParameter("densityRatio"+i):"");//added by rk
          flatRatesDOB.setNotes(request.getParameter("notes"+i));
 			  	if(request.getParameter("effDate"+i)			!= null)
			 			flatRatesDOB.setEffDate		(fomater.getTimestamp("DD-MM-YY",request.getParameter("effDate"+i)));
				  flatRatesDOB.setFrequency	(frequency);  
  				flatRatesDOB.setTransittime	(request.getParameter("transitTime"+i)		!= null ? request.getParameter("transitTime"+i)	 : "");
					//flatRatesDOB.setChargeRate(Double.parseDouble(request.getParameterValues("list"+i)[j]));
					//	flatRatesDOB.setWtBreakSlab(wtbrakValues[j]+"PSS");
					//-	flatRatesDOB.setChargeRate(Double.parseDouble(request.getParameterValues("psslist"+i)[j]));
					flatRatesDOB.setLowerBound(0);
					flatRatesDOB.setUpperBound(0);
  				flatRatesDOB.setLaneNo(i);
					flatRatesDOB.setLineNo(n);
          if(p==0)
          {
              if("1".equals(rateDOB.getShipmentMode()))
                {
                 if("on".equalsIgnoreCase(request.getParameter("fs"+i)))
                 {
                  if(!("".equalsIgnoreCase(request.getParameter("fsbasic"+i))))
                    {
                      flatRatesDOB.setWtBreakSlab("FSBASIC");
                      flatRatesDOB.setChargeRate(Double.parseDouble(request.getParameter("fsbasic"+i)));
                      flatRatesDOB.setTypeofcharge("FUEL SURCHARGE");  
                    }
                     else
                    {
                        flatRatesDOB.setWtBreakSlab(null);
                    }
                 }
                 else
                 {
                     if(!("".equalsIgnoreCase(request.getParameter("ssbasic"+i))))
                     {
                      flatRatesDOB.setWtBreakSlab("SSBASIC");
                      flatRatesDOB.setChargeRate(Double.parseDouble(request.getParameter("ssbasic"+i)));
                      flatRatesDOB.setTypeofcharge("SECURITY SURCHARGE");  
                     }
                      else
                    {
                        flatRatesDOB.setWtBreakSlab(null);
                    }
                 }
              }
             else
             {
                if(!("".equalsIgnoreCase(request.getParameter("surchargePercent"+i))))
                 {
                   flatRatesDOB.setWtBreakSlab("SURCHARGE");
                   flatRatesDOB.setChargeRate(Double.parseDouble(request.getParameter("surchargePercent"+i)));
                   flatRatesDOB.setTypeofcharge("SURCHARGE");  
                }
                 else
                    {
                        flatRatesDOB.setWtBreakSlab(null);
                    }
             }
          }
          else if(p==1)
          {
             if("on".equalsIgnoreCase(request.getParameter("fs"+i)))
             {
                 if(!("".equalsIgnoreCase(request.getParameter("fsmin"+i))))
                  {
                   flatRatesDOB.setWtBreakSlab("FSMIN");
                   flatRatesDOB.setChargeRate(Double.parseDouble(request.getParameter("fsmin"+i)));
                   flatRatesDOB.setTypeofcharge("FUEL SURCHARGE");
                  }
                   else
                    {
                        flatRatesDOB.setWtBreakSlab(null);
                    }
              }
               else
              {
                 if(!("".equalsIgnoreCase(request.getParameter("ssmin"+i))))
                  {
                    flatRatesDOB.setWtBreakSlab("SSMIN");
                    flatRatesDOB.setChargeRate(Double.parseDouble(request.getParameter("ssmin"+i)));
                    flatRatesDOB.setTypeofcharge("SECURITY SURCHARGE"); 
                  }
                   else
                    {
                        flatRatesDOB.setWtBreakSlab(null);
                    }
              }
          }
            else if(p==2)
          {
             if("on".equalsIgnoreCase(request.getParameter("fs"+i)))
             {
                if(!("".equalsIgnoreCase(request.getParameter("fskg"+i))))
                   { 
                      flatRatesDOB.setWtBreakSlab("FSKG");
                      flatRatesDOB.setChargeRate(Double.parseDouble(request.getParameter("fskg"+i)));
                      flatRatesDOB.setTypeofcharge("FUEL SURCHARGE");  
                  }
                   else
                    {
                        flatRatesDOB.setWtBreakSlab(null);
                    }
             }
             else
             {
               if(!("".equalsIgnoreCase(request.getParameter("sskg"+i))))
                 { 
                    flatRatesDOB.setWtBreakSlab("SSKG");
                    flatRatesDOB.setChargeRate(Double.parseDouble(request.getParameter("sskg"+i)));
                    flatRatesDOB.setTypeofcharge("SECURITY SURCHARGE");
                 }
                  else
                    {
                        flatRatesDOB.setWtBreakSlab(null);
                    }
             }
          }
           else if(p==3)
          {
              if(!("".equalsIgnoreCase(request.getParameter("ssbasic"+i))))
              {
                 flatRatesDOB.setWtBreakSlab("SSBASIC");
                 flatRatesDOB.setChargeRate(Double.parseDouble(request.getParameter("ssbasic"+i)));
                 flatRatesDOB.setTypeofcharge("SECURITY SURCHARGE");
              }
               else
                    {
                        flatRatesDOB.setWtBreakSlab(null);
                    }
          }
          else if(p==4)
          {
              if(!("".equalsIgnoreCase(request.getParameter("ssmin"+i))))
             {
                flatRatesDOB.setWtBreakSlab("SSMIN");
                flatRatesDOB.setChargeRate(Double.parseDouble(request.getParameter("ssmin"+i)));
                flatRatesDOB.setTypeofcharge("SECURITY SURCHARGE");
             }
              else
              {
                  flatRatesDOB.setWtBreakSlab(null);
              }
          }
           else if(p==5)
          {
              if(!("".equalsIgnoreCase(request.getParameter("sskg"+i))))
             {
              flatRatesDOB.setWtBreakSlab("SSKG");
             flatRatesDOB.setChargeRate(Double.parseDouble(request.getParameter("sskg"+i)));
              flatRatesDOB.setTypeofcharge("SECURITY SURCHARGE");
             }
             else
              {
                  flatRatesDOB.setWtBreakSlab(null);
              }
          }
              }
             
        }*/
        ratelaneslist.add(lanes);//@@Added for the WPBN issues-146448,146968 on 18/12/08
			      
   		   home  = (BuyRatesSessionHome)LookUpBean.getEJBHome("BuyRatesSessionBean");
	         remote	 = (BuyRatesSession)home.create();
            rateDOB  =  (RateDOB)session.getAttribute("rateDOB");
            
      rateDOB.setWeightClass(weightClass);
      
			//result	 =  remote.crateBuyFlatRate(rateDOB,lanes);
   //   list_returnList	=   remote.crateBuyFlatRate(rateDOB,lanes);//@@Modified for the WPBN issues-146448,146968 on 18/12/08
    list_returnList	=   remote.crateBuyFlatRate(rateDOB,ratelaneslist);
      
      sb_successErrorMsg     = (StringBuffer)list_returnList.get(0);
      sb_failErrorMsg        = (StringBuffer)list_returnList.get(1);
      
			session.removeAttribute("lanes");
			session.removeAttribute("rateDOB");
			session.removeAttribute("surchargeIds");
            session.removeAttribute("surchargeDesc");
            session.removeAttribute("surchargeWeightBreaks");
		
			session.removeAttribute("wtbrak");  
			session.removeAttribute("listValues");  
      errors = new StringBuffer();
      if(sb_successErrorMsg!=null && sb_successErrorMsg.length()>0)
      {
        errors.append("Records Successfully Inserted for the following lanes:\n"+sb_successErrorMsg.toString());
        
      }
      
      if(sb_failErrorMsg!=null && sb_failErrorMsg.length()>0)
      {
        errors.append("The following lanes are not Inserted As rates already defined at higher levels :\n"+sb_failErrorMsg.toString());
      }
      

                  
      errorMessageObject	= new ErrorMessage(errors.toString(),"BuyRatesController?Operation=Add"); 
		
			keyValueList.add(new KeyValue("ErrorCode","RSI")); 	
			keyValueList.add(new KeyValue("Operation",operation)); 	

			errorMessageObject.setKeyValueList(keyValueList);
			request.setAttribute("ErrorMessage",errorMessageObject);
			request.setAttribute("ErrorMessage",errorMessageObject);
			session.setAttribute("ErrorMessage","Buy Rates added Sucessfully");
			session.setAttribute("ErrorCode","RSI");

			session.setAttribute("NextNavigation","BuyRatesController?Operation=Add");
			nextNavigation	=	"ESupplyErrorPage.jsp";
        
              
              
		}	 
    }

  		doFileDispatch(request,response,nextNavigation);

		  }catch(Exception e)
		  {
        e.printStackTrace();
        session.removeAttribute("lanes");
        session.removeAttribute("rateDOB");
        errorMessageObject = new ErrorMessage("Usage of browser 'Back' button is not allowed Click 'Continue button to redo the operation ","BuyRatesController?Operation="+operation); 
        keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
        keyValueList.add(new KeyValue("Operation",operation)); 	
  
        errorMessageObject.setKeyValueList(keyValueList);
        request.setAttribute("ErrorMessage",errorMessageObject);
        doFileDispatch(request,response,"ESupplyErrorPage.jsp"); 
		  }
	}


	public void doFileDispatch(HttpServletRequest request, HttpServletResponse response, String forwardFile)throws IOException, ServletException
	{
		try
		{
		if(forwardFile.equalsIgnoreCase("etrans/BuyRatesAdd.jsp")){
			session.removeAttribute("lanes");
		}
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


	private String validateRateDOB(String effectiveFrom,String effectiveTo,String currency,String carrierId,String shipmentMode,String terminalId)throws Exception 
	{
		StringBuffer		sb_errorMsg =  null;
		BuyRatesSessionHome home		= null;
		BuyRatesSession     remote		=  null;
		String				errorMsg	=	null;		
		int surchargeIdlen=0;
		String surchargeIds[] 			= (String[])session.getAttribute("surchargeIds");
		String surchargeDesc[]		   	= (String[])session.getAttribute("surchargeDesc");//Govind
		if(surchargeIds!=null){
		surchargeIdlen =surchargeIds.length;
		}
		try{
				
				sb_errorMsg	=	new StringBuffer ();	

				/*if(effectiveFrom==null || effectiveTo== null)
				{
					sb_errorMsg.append(" Effective from and ValidUp to Dates are Mandatory <BR>") ;

				}*/
        if(effectiveFrom==null || "".equals(effectiveFrom))
				{
					sb_errorMsg.append(" Effective from is Mandatory ") ;

				}        
				if(currency==null || "".equals(currency))
				{
					sb_errorMsg.append(" Currency Should not be null ") ;
				}
				if(carrierId==null || "".equals(carrierId))
				{
					sb_errorMsg.append(" Carrier Should not be null  ") ;
				}
                if(surchargeIds != null)
                {
                	for(int i=1;i<surchargeIdlen;i++)
                	{
                		for(int j=i+1;j<surchargeIdlen;j++)
                		{
                			if(surchargeIds[i].equalsIgnoreCase(surchargeIds[j])){
                				sb_errorMsg.append(" Surcharge "+surchargeDesc[i]+" is Duplicated At Line"+j) ;
                			}
                		}
                	}
                }
				
				if(!"".equals(sb_errorMsg.toString().trim()))
				{
					return sb_errorMsg.toString()	;
				}
        
					  home		= (BuyRatesSessionHome)LookUpBean.getEJBHome("BuyRatesSessionBean");
		              remote	= (BuyRatesSession)home.create();
					  errorMsg	=  remote.validateRateDOB(carrierId,currency,shipmentMode,terminalId);
			 

			return errorMsg;
			
	

		}catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}

	}
private String detailRateDOB(HttpServletRequest request,HttpServletResponse response,String rateDtls,RateDOB rateDOB )throws Exception
	{
		StringBuffer		sb_errorMsg		=   null;
		StringBuffer		sb_errorMsg1	=   null;
		BuyRatesSessionHome home			=	null;
		BuyRatesSession     remote			=	null;

		String				errorMsg		=	null;		
		String				origin			=	null;		
		String				destination		=	null;		
		String				serviceLevel	=	null;		
		TreeSet				set				=	null;
		boolean				isTrue			=	false;
		int					preRows			=	0;
		boolean				flag			=	false;	
		FlatRatesDOB		flatRatesDOB	=	null;
		ArrayList			lanes			=	null;	
		String				min				=	null;
		String				flat			=	null;
    String				fsbasic			=	null;//@@Added by Kameswari for the CR
    String				fsmin			=	null;//@@Added by Kameswari for the CR
    String				fskg			=	null;//@@Added by Kameswari for the CR
		String				ssbasic			=	null;//@@Added by Kameswari for the CR
    String				ssmin			=	null;//@@Added by Kameswari for the CR
    String				sskg			=	null;//@@Added by Kameswari for the CR
    String				bafmin			=	null;
     String				bafm3			=	null;
    String				cafmin			        =	null;
     boolean				slabflag =false;
     boolean				bothflag=false;
     boolean				listflag=false;
     boolean  				flatflag=false;
     boolean  				srcurrencyflag=false;;
    String				csf			      =	null;
     String				surchargePercent		   	=	null;
    String				frequency		   =	null;
		String				transitTime	  	=	null;
		String				rates[]			    =	null;
		String[]            listValues			=	null;
   	String[]				srwatbraks			    =	null;
   	String[]				surchargeWeightBreaks			    =	null;
   	String[]				surchargeDesc			    =	null;
   	String[]				surchargeIds			    =	null;
		String				slabRates[]	  =	null;
		String				wtbrakValues[]	=	null;
		HashMap       map             = null;
		String dummybothname =null;
    String        dup_frequency   = null;
    String[]      frequecyArray   = null;
    ArrayList     wtBreakList     = new ArrayList();
    HashMap          mapList      =  new HashMap();
    //added by VLAKSHMI for issue 175657 on 070709
    int             cafcount     = 0;
     int             bafcount     = 0;
      int             csscount     = 0;
       int             psscount     = 0;
       
       boolean             cafflag     = false;
     boolean             bafflag     = false;
      boolean             cssflag     = false;
       boolean             pssflag     = false;
	   //end  for issue 175657
		try{
				
				sb_errorMsg	 =	new StringBuffer ();	
				sb_errorMsg1 =	new StringBuffer ();	
				set			 =	new TreeSet();
				lanes		 =	new ArrayList();	
        map      =  new HashMap();
				 home	= (BuyRatesSessionHome)LookUpBean.getEJBHome("BuyRatesSessionBean");
				 remote	= (BuyRatesSession)home.create();

         
				if(request.getParameter("preRows")!=null && !"".equals(request.getParameter("preRows").trim()) )
					preRows				=	Integer.parseInt(request.getParameter("preRows"));

				wtbrakValues =	(String[])session.getAttribute("wtbrak");
				surchargeIds	= (String[])session.getAttribute("surchargeIds");//Govind
				  surchargeDesc	= (String[])session.getAttribute("surchargeDesc");//Govind
				  surchargeWeightBreaks	= (String[])session.getAttribute("surchargeWeightBreaks");//Govind

				for(int i=0;i<=preRows;i++)
				{
							flag = false;
					if("on".equalsIgnoreCase(request.getParameter(""+i)))
					{
						
						flatRatesDOB = new FlatRatesDOB();
						origin		 =	request.getParameter("origin"+i);
						destination	 =  request.getParameter("destination"+i);
						serviceLevel =  request.getParameter("serviceLevel"+i);
						min			 =	request.getParameter("min"+i);  
						frequency	 =	request.getParameter("frequency"+i);
						transitTime  =	request.getParameter("transitTime"+i);
						  if(surchargeIds!=null){
						  for(int k=1;k<surchargeIds.length;k++){
							  if("on".equalsIgnoreCase(request.getParameter(surchargeIds[k]+i))){
								 if(surchargeIds[k].length()>1){
									 System.out.println("(k-1)+i-------------"+(k-1)+i);
				                     System.out.println("srcrrr-----"+request.getParameter("surchargeCurrency"+(k-1)+i));
								  srwatbraks = surchargeWeightBreaks[k].split(",");
								  if((request.getParameter("surchargeCurrency"+(k-1)+i)!=null) && !"".equals(request.getParameter("surchargeCurrency"+(k-1)+i))){
						mapList.put("surchargeCurrency"+(k-1)+i,(request.getParameter("surchargeCurrency"+(k-1)+i)!=null)?request.getParameter("surchargeCurrency"+(k-1)+i):"");	
								  }
								  else{
									  mapList.put("surchargeCurrency"+(k-1)+i,(request.getParameter("surchargeCurrency"+(k-1)+i)!=null)?request.getParameter("surchargeCurrency"+(k-1)+i):"");
									  srcurrencyflag =true;
								  }
						if((srwatbraks[0].equalsIgnoreCase("slab") && srwatbraks.length ==1) ||(srwatbraks[0].equalsIgnoreCase("both") && srwatbraks.length ==1))
									 {
							 for(int t=0;t<=11;t++){
								      if(srwatbraks[0].equalsIgnoreCase("slab")){//slab if begins
				        if((request.getParameter("srslabbreaks"+i+(k-1)+t)!=null) && !"".equals(request.getParameter("srslabbreaks"+i+(k-1)+t))){//slab break if check
							   if((request.getParameter("srslabbreaks"+i+(k-1)+t)!=null) && !"".equals(request.getParameter("srslabbreaks"+i+(k-1)+t))
								 &&(request.getParameter("srslabvalues"+i+(k-1)+t)!=null) && !"".equals(request.getParameter("srslabvalues"+i+(k-1)+t))){//error if
				                       mapList.put("srslabbreaks"+i+(k-1)+t,(request.getParameter("srslabbreaks"+i+(k-1)+t)!=null?
								      request.getParameter("srslabbreaks"+i+(k-1)+t):"")); 
							           mapList.put("srslabvalues"+i+(k-1)+t,(request.getParameter("srslabvalues"+i+(k-1)+t)!=null?
								      request.getParameter("srslabvalues"+i+(k-1)+t) :"")); 
							   }else{
							mapList.put("srslabbreaks"+i+(k-1)+t,(request.getParameter("srslabbreaks"+i+(k-1)+t)!=null?
										      request.getParameter("srslabbreaks"+i+(k-1)+t):"")); 
							mapList.put("srslabvalues"+i+(k-1)+t,(request.getParameter("srslabvalues"+i+(k-1)+t)!=null?
										      request.getParameter("srslabvalues"+i+(k-1)+t) :"")); 
								slabflag =true;
							   }//error if end
						}//slab break if check end
									  }//slab if ends
									  if(srwatbraks[0].equalsIgnoreCase("both")){//both if begins

				     if((request.getParameter("srslabbreaks"+i+(k-1)+t)!=null) && !"".equals(request.getParameter("srslabbreaks"+i+(k-1)+t))){//slabboth break if check

				                           dummybothname = ((request.getParameter("srslabvalue"+i+(k-1)+t)!=null) && !"".equals(request.getParameter("srslabvalue"+i+(k-1)+t)))?
											   "srslabvalue"+i+(k-1)+t:"srflatvalue"+i+(k-1)+t;
				   						   System.out.println("dummybothname==========="+request.getParameter("srslabvalue"+i+(k-1)+t).length());
										   System.out.println("dummybothname==========="+dummybothname);
							if((request.getParameter("srslabbreaks"+i+(k-1)+t)!=null) && !"".equals(request.getParameter("srslabbreaks"+i+(k-1)+t))
								 &&(request.getParameter(dummybothname)!=null) && !"".equals(request.getParameter(dummybothname))){//error if)		
									mapList.put("srslabbreaks"+i+(k-1)+t,(request.getParameter("srslabbreaks"+i+(k-1)+t)!=null)?request.getParameter("srslabbreaks"+i+(k-1)+t):"");
									mapList.put(dummybothname,(request.getParameter(dummybothname)!= null)?
					                request.getParameter(dummybothname):"");
							}else{
								mapList.put("srslabbreaks"+i+(k-1)+t,(request.getParameter("srslabbreaks"+i+(k-1)+t)!=null)?request.getParameter("srslabbreaks"+i+(k-1)+t):"");
								mapList.put(dummybothname,(request.getParameter(dummybothname)!= null)?
				                request.getParameter(dummybothname):"");
							   bothflag =true;
							}//error if end

					 }//slabboth break if check ends	
									  }//both if ends
							 }//end t for loop
									 }//slab and both if check end
									 else if((srwatbraks[0].equalsIgnoreCase("list") && srwatbraks.length ==1))
									 {
									 
									      listValues = request.getParameterValues("list"+i);
				        for(int lv=0;lv<listValues.length;lv++){
							if((request.getParameter("srlistvalue"+i+(k-1)+lv)!=null) && !"".equals((request.getParameter("srlistvalue"+i+(k-1)+lv))) ){
				mapList.put("srlistvalue"+i+(k-1)+lv,(request.getParameter("srlistvalue"+i+(k-1)+lv)!=null)?request.getParameter("srlistvalue"+i+(k-1)+lv):"");
							}else{
								mapList.put("srlistvalue"+i+(k-1)+lv,(request.getParameter("srlistvalue"+i+(k-1)+lv)!=null)?request.getParameter("srlistvalue"+i+(k-1)+lv):"");
						 listflag =true;
							}
						}
					}else{
							for(int l=0;l<srwatbraks.length;l++){
				if((request.getParameter(surchargeIds[k]+srwatbraks[l]+i)!=null) && !"".equals(request.getParameter(surchargeIds[k]+srwatbraks[l]+i))){
				mapList.put(surchargeIds[k]+srwatbraks[l]+i,(request.getParameter(surchargeIds[k]+srwatbraks[l]+i)!=null)?request.getParameter(surchargeIds[k]+srwatbraks[l]+i):"");
				}else{
				       
				       flatflag =true;
				}//error check end
							}// l loop end
									 }//end slab if
								 }// inner if end
								 
			sb_errorMsg.append(srcurrencyflag?" Surcharge Currency for the charge "+surchargeDesc[k]+"must not be empty<br>":"");					 
			sb_errorMsg.append(slabflag?"The corresponding slabbreak/slabvalue for the charge "+surchargeDesc[k]+"must not be empty <br>":"");
			sb_errorMsg.append(bothflag?"The corresponding slabbreak/slabvalue for the charge "+surchargeDesc[k]+"must not be empty <br>":"");
			sb_errorMsg.append(listflag?"The corresponding ListValue for the charge "+surchargeDesc[k]+"must not be empty <br>":"");
			sb_errorMsg.append(flatflag?"The corresponding Value for the charge "+surchargeDesc[k]+"must not be empty <br>":"");
			
							  }//check box if end					 			 
}//k loop end
}			
		
						
						
						
						
           //@@Added by kameswari for the CR
        /*  fsbasic		  =	request.getParameter("fsbasic"+i);
           fsmin		    =	request.getParameter("fsmin"+i);
           fskg		      =	request.getParameter("fskg"+i);
					 ssbasic		  =	request.getParameter("ssbasic"+i);
           ssmin		    =	request.getParameter("ssmin"+i);
           sskg		      =	request.getParameter("sskg"+i);
           cafmin		    =	request.getParameter("cafmin"+i);
           caf		      =	request.getParameter("caf%"+i);
         
           bafmin		    =	request.getParameter("bafmin"+i);
					 bafm3		    =	request.getParameter("bafm3"+i);
           pssmin		    =	request.getParameter("pssmin"+i);
					 pssm3		    =	request.getParameter("pssm3"+i);
           csf		      =	request.getParameter("csfabsolute"+i);
           surchargePercent    =	request.getParameter("surchargePercent"+i);
           cafrates   = request.getParameterValues("caflist"+i);
           bafrates   = request.getParameterValues("baflist"+i);
           cssrates   = request.getParameterValues("csslist"+i);
           pssrates   = request.getParameterValues("psslist"+i);
				     // appened i by VLAKSHMI for issue 145908 0n 23/11/2008
					 	mapList.put("fsbasic"+i,(fsbasic!=null)?fsbasic:"");
            mapList.put("fsmin"+i,(fsmin!=null)?fsmin:"");
            mapList.put("fskg"+i,(fskg!=null)?fskg:"");
            mapList.put("ssbasic"+i,(ssbasic!=null)?ssbasic:"");
            mapList.put("ssmin"+i,(ssmin!=null)?ssmin:"");
            mapList.put("sskg"+i,(sskg!=null)?sskg:"");
            mapList.put("bafmin"+i,(bafmin!=null)?bafmin:"");
            mapList.put("bafm3"+i,(bafm3!=null)?bafm3:"");
            mapList.put("cafmin"+i,(cafmin!=null)?cafmin:"");
            mapList.put("caf%"+i,(caf!=null)?caf:"");
            mapList.put("pssmin"+i,(pssmin!=null)?pssmin:"");
            mapList.put("pssm3"+i,(pssm3!=null)?pssm3:"");
            mapList.put("csf"+i,(csf!=null)?csf:"");
           
            mapList.put("surchargePercent",(surchargePercent!=null)?surchargePercent:"");
           //added by VLAKSHMI for issue 175657 on 070709
         if(cafrates!=null)
        {
        	 int cafratesLen	=	cafrates.length;
          for(int k=0;k<cafratesLen;k++)
          {
             if(cafrates[k]!=null&&cafrates[k].trim().length()>0)
             {
                cafcount++;
              
             }
          }
          if(cafcount!=cafratesLen)
          {
          cafflag=true;
          }
        }
         if(bafrates!=null)
        {
        	 int bafratesLen	= bafrates.length;
          for(int l=0;l<bafratesLen;l++)
          {
             if(bafrates[l]!=null && bafrates[l].trim().length()>0)
             {
                bafcount++;
              
             }
          }
          if(bafcount!=bafratesLen)
          {
          bafflag=true;
          }
        }
         if(cssrates!=null)
        {
        	 int cssratesLen	=	cssrates.length;	
          for(int z=0;z<cssratesLen;z++)
          {
             if(cssrates[z]!=null && cssrates[z].trim().length()>0)
             {
                csscount++;
              
             }
          }
          if(csscount!=cssratesLen)
          {
          cssflag=true;
          }
        }
         if(pssrates!=null)
        {
        	 int pssratesLen	=	pssrates.length;
          for(int c=0;c<pssratesLen;c++)
          {
             if(pssrates[c]!=null&&pssrates[c].trim().length()>0)
             {
               psscount++;
              
             }
          }
          if(psscount!=pssratesLen)
          {
          pssflag=true;
          }
        }
         // end  for issue 175657 on 070709
           if(cafrates!=null)
            {
              for(int m=0;m<cafrates.length;m++)
              {
                 mapList.put("cafrates"+i+m,(cafrates[m]!=null)?cafrates[m]:"");
              } 
            }
             if(bafrates!=null)
            {
              for(int m=0;m<cafrates.length;m++)
              {
                 mapList.put("bafrates"+i+m,(bafrates[m]!=null)?bafrates[m]:"");
              }
            }
            if(cssrates!=null)
            {
              for(int m=0;m<cafrates.length;m++)
              {
                mapList.put("cssrates"+i+m,(cssrates[m]!=null)?cssrates[m]:"");
              }
            }
             if(pssrates!=null)
            {
              for(int m=0;m<cafrates.length;m++)
              {
                 mapList.put("pssrates"+i+m,(pssrates[m]!=null)?pssrates[m]:"");
              }
            } */
            //surcharges for the rate
						flatRatesDOB.setBreaksList(mapList);
           
          	if(origin== null || "".equalsIgnoreCase(origin))
						{
							sb_errorMsg.append("Origin ");
							flag = true;
						}
						if(destination== null || "".equalsIgnoreCase(destination))
						{
							sb_errorMsg.append(" Destination ");
							flag = true;
						}
						if(serviceLevel== null || "".equalsIgnoreCase(serviceLevel))
						{
							sb_errorMsg.append(" Service Level ");
							flag = true;
						}

						if(transitTime==null || "".equalsIgnoreCase(transitTime))
						{
							sb_errorMsg.append(" Transit Time ");//frequency
							flag = true;
						}if(frequency==null || "".equalsIgnoreCase(frequency.trim()))
						{
							sb_errorMsg.append(" Frequency  ");//frequency
							flag = true;
						}
						if(min==null || "".equalsIgnoreCase(min))
						{
              if(!"List".equalsIgnoreCase(rateDOB.getWeightBreak()))
              {
							sb_errorMsg.append(" Minimum Rate ");
							flag = true;
              }
						}/*
            if("on".equalsIgnoreCase(request.getParameter("fs"+i)))
						  {
              
               	if((fskg==null || "".equalsIgnoreCase(fskg))&&(fsmin==null || "".equalsIgnoreCase(fsmin))&&(fsbasic==null || "".equalsIgnoreCase(fsbasic)))
							{
                	sb_errorMsg.append(" All Fuel Surcharge Rates");
                  flag = true;
              }
            }
               if("on".equalsIgnoreCase(request.getParameter("ss"+i)))
						  {
                
                  if((sskg==null || "".equalsIgnoreCase(sskg))&&(ssmin==null || "".equalsIgnoreCase(ssmin))&&(ssbasic==null || "".equalsIgnoreCase(ssbasic)))
                {
                    sb_errorMsg.append("All Security Surcharge Rates");
                    flag = true;
                }
              }
               if("on".equalsIgnoreCase(request.getParameter("baf"+i))&&!("ListList".equalsIgnoreCase(rateDtls)))
						  {
                if((bafm3==null || "".equalsIgnoreCase(bafm3))&&(bafmin==null || "".equalsIgnoreCase(bafmin)))
                {
                    sb_errorMsg.append("All B.A.F Rates");
                    flag = true;
                }
             }
              if("on".equalsIgnoreCase(request.getParameter("caf"+i))&&!("ListList".equalsIgnoreCase(rateDtls)))
						  {
             
               	if((caf==null || "".equalsIgnoreCase(caf))&&(cafmin==null || "".equalsIgnoreCase(cafmin)))
							{
                	sb_errorMsg.append(" All C.A.F Rates");
                  flag = true;
              }
             }
               if("on".equalsIgnoreCase(request.getParameter("pss"+i))&&!("ListList".equalsIgnoreCase(rateDtls)))
						  {
               if((pssm3==null || "".equalsIgnoreCase(pssm3))&&(pssmin==null || "".equalsIgnoreCase(pssmin)))
                {
                    sb_errorMsg.append("All P.S.S Rates");
                    flag = true;
                }
             }
              if("on".equalsIgnoreCase(request.getParameter("csfabsolute"+i))&&!("ListList".equalsIgnoreCase(rateDtls)))
						  {
               	if(csf==null || "".equalsIgnoreCase(csf))
							{
                	sb_errorMsg.append("C.S.F Absolute");
                  flag = true;
              }
             }
             if("on".equalsIgnoreCase(request.getParameter("surcharge"+i)))
						  {
                    if(surchargePercent==null || "".equalsIgnoreCase(surchargePercent))
                {
                    sb_errorMsg.append("Surcharge");
                    flag = true;
                }
             }
              if("on".equalsIgnoreCase(request.getParameter("baf"+i))&&"ListList".equalsIgnoreCase(rateDtls)&&bafflag)
              {
                sb_errorMsg.append("B.A.F rates");
                flag = true;
              
              }
               if("on".equalsIgnoreCase(request.getParameter("caf"+i))&&"ListList".equalsIgnoreCase(rateDtls)&&cafflag)
              {
                sb_errorMsg.append("C.A.F% rates");
                flag = true;
              
              }
               if("on".equalsIgnoreCase(request.getParameter("pss"+i))&&"ListList".equalsIgnoreCase(rateDtls)&&pssflag)
              {
                sb_errorMsg.append("P.S.S rates");
                flag = true;
              
              }
               if("on".equalsIgnoreCase(request.getParameter("css"+i))&&"ListList".equalsIgnoreCase(rateDtls)&&cssflag)
              {
                sb_errorMsg.append("C.S.S rates");
                flag = true;
              
              }*/
						if("FlatFlat".equalsIgnoreCase(rateDtls))
						{
							flat  =	request.getParameter("flat"+i);

							if(flat==null || "".equalsIgnoreCase(flat))
							{
								sb_errorMsg.append(" Flat Rate ");//frequency
								flag = true;
							}
              flatRatesDOB.setFlat(flat);
              //@@Added by kameswari for enhancements
              
     			}
						else if("SlabSlab".equalsIgnoreCase(rateDtls))
						{
							rates = request.getParameterValues("wtbrk"+i);
							  for (int j=0 ; j<wtbrakValues.length;j++)
							  {
								  if(!"".equals(wtbrakValues[j]))
			           {
									if(rates[j]==null || "".equalsIgnoreCase(rates[j]))
									 {	
									   	sb_errorMsg.append("Slab Rate ");
										flag = true;
										break;
									 }		
                 }
                }
      				flatRatesDOB.setRates(rates);
						}
						else if("SlabBoth".equalsIgnoreCase(rateDtls))
						{
							rates	  = request.getParameterValues("wtbrkf"+i);
							slabRates = request.getParameterValues("wtbrks"+i);
							  for (int j=0 ; j<wtbrakValues.length;j++)
							  {
								  if(!"".equals(wtbrakValues[j]))
			            {
									if((rates[j]==null || "".equalsIgnoreCase(rates[j])) && (slabRates[j]==null || "".equalsIgnoreCase(slabRates[j])))
									 {
										sb_errorMsg.append("Slab and Flat");
										flag = true;
										
									 }
								 }
                
							  }
          
							flatRatesDOB.setSlabValues(slabRates);
							flatRatesDOB.setFlatValues(rates);
							
    
						}
            else if("ListList".equalsIgnoreCase(rateDtls))
            {
              rates = request.getParameterValues("list"+i);
           
              for (int j=0 ; j<rates.length;j++)
              {
                  if(rates[j]==null || "".equalsIgnoreCase(rates[j]))
                 {	
                    sb_errorMsg.append("List Rate ");
                  flag = true;
                  break;
                 }		
                }
             }
                             
            
            
						if((flag) &&(srcurrencyflag||slabflag||bothflag||listflag||flatflag))
						{
							sb_errorMsg.append(" Should not be Empty at Lane "+(i+1)+"<br>" );
						}
						
            
						if(!"".equalsIgnoreCase(origin.trim()) && !"".equalsIgnoreCase(destination.trim()) &&  origin.equalsIgnoreCase(destination))
						{
							sb_errorMsg1.append("Origin,Destination Should Not Be same"+origin+" "+destination+" "+"at line "+(i+1)+"<br>");
						}
            
            if(!"".equalsIgnoreCase(frequency.trim()) && !"2".equals(rateDOB.getShipmentMode()))
            {
                
                //frequency     = getSortedNums(frequency);
               
                
                isTrue = set.add(origin+"_"+destination+"_"+serviceLevel);// add  frequency for checking
                if(!isTrue)
                {
                  dup_frequency = (String)map.get(origin+"_"+destination+"_"+serviceLevel);
                  
                  
                  
                  frequecyArray  = frequency.split(",");
                  int k=0;
                  for(k=0;k<frequecyArray.length;k++)
                  {
                    if( dup_frequency!=null && !"".equals(dup_frequency) &&  dup_frequency.indexOf(frequecyArray[k])>=0)
                    {
                      sb_errorMsg1.append("Origin,Destination,ServiceLevel,frequency should not duplicate "+origin+" "+destination+" "+serviceLevel+" at line "+(i+1)+"<BR> ");
                      break;
                    }
                  }
                  if(k==frequecyArray.length)
                  {
                    dup_frequency = dup_frequency+","+frequency;
                    map.put(origin+"_"+destination+"_"+serviceLevel,dup_frequency);
                  }
                  
                }else
                  { map.put(origin+"_"+destination+"_"+serviceLevel,frequency);}
            }else
            {
             if(!"".equals(origin.trim()) &&  !"".equals(destination.trim()) && !"".equals(serviceLevel.trim()) &&  !"".equals(frequency.trim()))
             {
                 isTrue = set.add(origin+"_"+destination+"_"+serviceLevel+"_"+frequency);// add  frequency for checking
                  if(!isTrue)
                  {
                    sb_errorMsg1.append("Origin,Destination,ServiceLevel,frequency should not duplicate "+origin+" "+destination+" "+serviceLevel+" at line "+(i+1)+"<BR> ");
                  }
             }
            }
              
						flatRatesDOB.setOrigin(origin);
						flatRatesDOB.setDestination(destination);
						flatRatesDOB.setServiceLevel(serviceLevel);
            
						flatRatesDOB.setFrequency	(frequency);
						flatRatesDOB.setTransittime	(transitTime);
            
						flatRatesDOB.setMin(min); 
            /*flatRatesDOB.setFsbasic(fsbasic);
             flatRatesDOB.setFsmin(fsmin);
              flatRatesDOB.setFskg(fskg);*/
              
            if(request.getParameter("densityRatio"+i)!=null && request.getParameter("densityRatio"+i).trim().length()>0)
  						flatRatesDOB.setDensityRatio(request.getParameter("densityRatio"+i));//added by rk
            else
              sb_errorMsg1.append("Density ratio should not be empty at laneNo:"+(i+1));
						lanes.add(flatRatesDOB);

					}
				}

				session.setAttribute("lanes",lanes);
				
				if((sb_errorMsg.toString()+sb_errorMsg1.toString() !=null) && !"".equals((sb_errorMsg.toString()+sb_errorMsg1.toString()).trim()))
				{
					return sb_errorMsg.toString()+sb_errorMsg1.toString();
				}

        if(lanes!=null && lanes.size()>0)
          errorMsg = remote.validateDetailRateDOB(lanes,rateDOB);
        
          

			return errorMsg;
		}catch(Exception e)
		{
			e.printStackTrace();
      throw new Exception();
			
		}
	}

    private String getSortedNums(String inputs)throws NumberFormatException,Exception
    {
        String[]        stValue			  =	null;
        int				      count			    =	0;     
        TreeSet			    uniqueValues	=	null;
        int[]			      sortValues		=	null;
        Iterator		    fetchValues		=	null;
        int             parseInteger  = 0;
        StringBuffer    sortValuesStr = null;
		    try{
             sortValuesStr = new StringBuffer("");
            // moreValues		=	new int[1];
             stValue		=	inputs.split(","); 
             uniqueValues	=	new TreeSet(); 

              for(int m=0;m<stValue.length;m++)
              {

                 
                   parseInteger = Integer.parseInt(stValue[m]);
                   

                 uniqueValues.add(stValue[m]);  
              }
              sortValues=new int[uniqueValues.size()];

                   fetchValues=uniqueValues.iterator(); 
               
                   while(fetchValues.hasNext())
                   {
                          try
                          {
                            sortValues[count]=(Integer.parseInt((String)fetchValues.next()));
                            count++;
                          }catch(NumberFormatException e)
                           {
                             throw new NumberFormatException();
                           }
                   }
                   
                   Arrays.sort(sortValues);
                  
                   for(int i=0;i<sortValues.length;i++)
                   {
                     sortValuesStr.append(sortValues[i]);
                     sortValuesStr.append(",");
                   }
                   sortValuesStr.delete(sortValuesStr.length()-1,sortValuesStr.length());
                   
        }catch(NumberFormatException e)
        {
            throw new NumberFormatException();  
        }catch(Exception e)
        {
          throw new Exception();
        }
			 return sortValuesStr.toString();  
	
	}
  
  private void doGetBuyRates(HttpServletRequest request, HttpServletResponse response,ESupplyGlobalParameters loginBean,String operation,QMSSellRatesDOB sellRatesDOB,String nextNavigation)throws IOException,ServletException
{
  ErrorMessage			        errorMessageObject                =   null;
  ArrayList			            keyValueList	                  =   new ArrayList();
  BuyRatesSessionHome			sellRatesHome                     =   null;
  BuyRatesSession				sellRatesRemote                   =   null;
  ArrayList			            headerValues	                  =   null;
  ArrayList			            headerVal	                      =   null;
  StringBuffer					errorMassege                      =   null;
  HttpSession				    session			                  =	  request.getSession();
	try
	{  

	insertValues(request,response,loginBean,operation,sellRatesDOB,nextNavigation);
	sellRatesHome		  =	  (BuyRatesSessionHome)LookUpBean.getEJBHome("BuyRatesSessionBean");
	sellRatesRemote	  =	  (BuyRatesSession)sellRatesHome.create();
    
    errorMassege      =   sellRatesRemote.validateSellRatesHdrData(sellRatesDOB);
    if(errorMassege.length() > 0)
    {
        headerVal = new ArrayList();
        request.setAttribute("Errors",errorMassege.toString());
        headerVal.add(sellRatesDOB);
        session.setAttribute("HeaderValue",headerVal);
    }
    else
    {
        headerValues      =   sellRatesRemote.getSellRatesValues(sellRatesDOB,loginBean,operation);   
       session.setAttribute("HeaderValues",headerValues);
    }
    
    nextNavigation			=	 "etrans/QMSBuyRateView.jsp";
	}
	catch(Exception e)
	{
		e.printStackTrace();
		errorMessageObject = new ErrorMessage("Error while doGetBuyRates()",nextNavigation); 
		keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
		keyValueList.add(new KeyValue("Operation",operation)); 	
		errorMessageObject.setKeyValueList(keyValueList);
		request.setAttribute("ErrorMessage",errorMessageObject);
		doFileDispatch(request,response,"ESupplyErrorPage.jsp");
	}
}

private void insertValues(HttpServletRequest request, HttpServletResponse response,ESupplyGlobalParameters loginBean,String operation,QMSSellRatesDOB sellRatesDOB,String nextNavigation)throws IOException,ServletException
{
    ErrorMessage			        errorMessageObject                =   null;
    ArrayList			            keyValueList	                    =   new ArrayList();
    String[]			            carriers			                    =	  null;
    String[]                  marginValues                      =   null;
     String                   shipmentMode                      =   null;
	String                   pageNo                            = null;
    try
    {  
        if(request.getParameter("shipmentMode")!=null)
          sellRatesDOB.setShipmentMode(request.getParameter("shipmentMode"));
        else
          sellRatesDOB.setShipmentMode("");
          
        if(request.getParameter("consoleType")!=null && "2".equals(request.getParameter("shipmentMode")))
          sellRatesDOB.setConsoleType(request.getParameter("consoleType"));
        else  if(request.getParameter("consoleTypes")!=null && "4".equals(request.getParameter("shipmentMode")))
          sellRatesDOB.setConsoleType(request.getParameter("consoleTypes"));
        else
          sellRatesDOB.setConsoleType("");  
        if(request.getParameter("weightBreak")!=null)
          sellRatesDOB.setWeightBreak(request.getParameter("weightBreak"));
        else
          sellRatesDOB.setWeightBreak("");
        if(request.getParameter("rateType")!=null)
          sellRatesDOB.setRateType(request.getParameter("rateType"));
        else
          sellRatesDOB.setRateType("");
        if(request.getParameter("baseCurrency")!=null)
          sellRatesDOB.setCurrencyId(request.getParameter("baseCurrency"));
        else
          sellRatesDOB.setCurrencyId("");
        if(request.getParameter("weightClass")!=null)
          sellRatesDOB.setWeightClass(request.getParameter("weightClass"));
        else
          sellRatesDOB.setWeightClass("");
        if(request.getParameter("origin")!=null)
          sellRatesDOB.setOrigin(request.getParameter("origin"));
        else
          sellRatesDOB.setOrigin("");
        if(request.getParameter("originCountry")!=null)
          sellRatesDOB.setOriginCountry(request.getParameter("originCountry"));
        else
          sellRatesDOB.setOriginCountry("");
        if(request.getParameter("destination")!=null)
          sellRatesDOB.setDestination(request.getParameter("destination"));
        else
          sellRatesDOB.setDestination("");
        if(request.getParameter("destinationCountry")!=null)
          sellRatesDOB.setDestinationCountry(request.getParameter("destinationCountry"));
        else
          sellRatesDOB.setDestinationCountry("");
        if(request.getParameter("serviceLevelId")!=null)
          sellRatesDOB.setServiceLevel(request.getParameter("serviceLevelId"));
        else
          sellRatesDOB.setServiceLevel("");
          
        if(request.getParameter("overMargin")!=null)
          sellRatesDOB.setOverAllMargin(request.getParameter("overMargin"));
        else
          sellRatesDOB.setOverAllMargin("");
          
        if(request.getParameter("marginType")!=null)
          sellRatesDOB.setMarginType(request.getParameter("marginType"));
        else
          sellRatesDOB.setMarginType("");
        if(request.getParameter("marginBasis")!=null)
          sellRatesDOB.setMarginBasis(request.getParameter("marginBasis"));
        else
          sellRatesDOB.setMarginBasis("");
          
        if(request.getParameter("carriers")!=null)
          sellRatesDOB.setCarrier_id(request.getParameter("carriers"));
        else
          sellRatesDOB.setCarrier_id("");  

        if(request.getParameter("pageNo")!=null && !"".equals(request.getParameter("pageNo")))
          sellRatesDOB.setPageNo(request.getParameter("pageNo"));
        else
          sellRatesDOB.setPageNo("1");
		//added by phani sekhar for wpbn 171213 on 20090605
         if(request.getParameter("originRegion")!=null && !"".equals(request.getParameter("originRegion")))
          sellRatesDOB.setOriginRegions(request.getParameter("originRegion"));
        else
          sellRatesDOB.setOriginRegions("");
          
           if(request.getParameter("destinationRegion")!=null && !"".equals(request.getParameter("destinationRegion")))
          sellRatesDOB.setDestRegions(request.getParameter("destinationRegion"));
        else
          sellRatesDOB.setDestRegions("");
          //ends 171213

    }
    catch(Exception e)
    {
        e.printStackTrace();
        errorMessageObject = new ErrorMessage("Error while insertValues()",nextNavigation); 
        keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
        keyValueList.add(new KeyValue("Operation",operation)); 	
        errorMessageObject.setKeyValueList(keyValueList);
        request.setAttribute("ErrorMessage",errorMessageObject);
        doFileDispatch(request,response,"ESupplyErrorPage.jsp");
    }
}
  private String getCheckedLanesInvalidate(HttpServletRequest request, HttpServletResponse response,ESupplyGlobalParameters loginbean)
  {
      HashMap        hm_buyRates    = null;
      FlatRatesDOB			flatRatesDOB	=	null;
      int						count			=	0;
      String        errorMsg = "";
      try
      {
 					
				count	=	Integer.parseInt(request.getParameter("count"));
    			//list	=	new ArrayList();	
       
        HttpSession session = request.getSession();
        
        hm_buyRates = (HashMap)session.getAttribute("hm_buyRates");
        
        if(hm_buyRates==null)
        {
          hm_buyRates = new HashMap();
        }
        
			for(int i=0;i<count;i++)
			{
        		flatRatesDOB	=	new FlatRatesDOB();
				if("on".equalsIgnoreCase(request.getParameter(""+i)))
				{
					flatRatesDOB.setBuyrateId(request.getParameter("buyRateId"+i));
					flatRatesDOB.setLaneNo(Integer.parseInt(request.getParameter("lanNumber"+i)));
					flatRatesDOB.setInvalidate("T");
          
				}else
		        {
        
             flatRatesDOB.setBuyrateId(request.getParameter("buyRateId"+i));
             flatRatesDOB.setLaneNo(Integer.parseInt(request.getParameter("lanNumber"+i)));
           
		        }
				//list.add(flatRatesDOB);
          
          hm_buyRates.put(request.getParameter("buyRateId"+i)+"_"+request.getParameter("lanNumber"+i),flatRatesDOB);
        
		     }
       
          session.setAttribute("hm_buyRates",hm_buyRates);
       
			/*if(list.size()>0)
			{
				home	= (BuyRatesSessionHome)LookUpBean.getEJBHome("BuyRatesSessionBean");
				remote	= (BuyRatesSession)home.create();
				
				remote.invalidateBuyRateDtls(list);
			}   */    
      }catch(Exception e)
      {
          e.printStackTrace();
      }
      return errorMsg;
  }

  	private void invalidateBuyRateDtls(HttpServletRequest request, HttpServletResponse response)
	{
      FlatRatesDOB			flatRatesDOB	=	null;
      ArrayList				list			=	null;
      Iterator			            itr						                    =	  null;
      HashMap				            hm_buyRates				                  =	  null;
      String				            keyHash					                  =	  null;
      BuyRatesSessionHome		home			=	null;
		  BuyRatesSession			remote			=	null;
      String errorMsg = "";
      try
      {
        HttpSession				        session			                      =	  request.getSession();
        list	=	new ArrayList();
        
        hm_buyRates				        =	  (HashMap)session.getAttribute("hm_buyRates");
        session.removeAttribute("hm_buyRates");
        itr				              =	  (hm_buyRates.keySet()).iterator();
        
        while(itr.hasNext())
        {
          keyHash	=	(String)itr.next();
          
          flatRatesDOB	=	new FlatRatesDOB();
          flatRatesDOB = (FlatRatesDOB)hm_buyRates.get(keyHash);
          
          list.add(flatRatesDOB);
          
        }
        if(list.size()>0)
        {
          home	= (BuyRatesSessionHome)LookUpBean.getEJBHome("BuyRatesSessionBean");
          remote	= (BuyRatesSession)home.create();
          
          remote.invalidateBuyRateDtls(list);
        }        
        
      }catch(Exception e)
      {
          e.printStackTrace();
      } 
  }
	/*private void invalidateBuyRateDtls(HttpServletRequest request, HttpServletResponse response)
	{
		int						count			=	0;
		String					buyrateId		=	null;
		String					lanNumber		=	null;
		FlatRatesDOB			flatRatesDOB	=	null;
		ArrayList				list			=	null;
		BuyRatesSessionHome		home			=	null;
		BuyRatesSession			remote			=	null;

		try{
					
				count	=	Integer.parseInt(request.getParameter("count"));
    			list	=	new ArrayList();	

			for(int i=0;i<count;i++)
			{
        		flatRatesDOB	=	new FlatRatesDOB();
				if("on".equalsIgnoreCase(request.getParameter(""+i)))
				{
					flatRatesDOB.setBuyrateId(request.getParameter("buyRateId"+i));
					flatRatesDOB.setLaneNo(Integer.parseInt(request.getParameter("lanNumber"+i)));
					flatRatesDOB.setInvalidate("T");
				}else
		        {
        
					 flatRatesDOB.setBuyrateId(request.getParameter("buyRateId"+i));
					 flatRatesDOB.setLaneNo(Integer.parseInt(request.getParameter("lanNumber"+i)));
		        }
				list.add(flatRatesDOB);
		     }
    
			if(list.size()>0)
			{
				home	= (BuyRatesSessionHome)LookUpBean.getEJBHome("BuyRatesSessionBean");
				remote	= (BuyRatesSession)home.create();
				
				remote.invalidateBuyRateDtls(list);
			}
			
		}catch(Exception e)
		{
	      e.printStackTrace();
		}
	}*/

  public String getCheckedLanes(HttpServletRequest request, HttpServletResponse response,ESupplyGlobalParameters loginBean)
  {
		int						count			=	0;
		String					rateType		=	null;
		String					lanNumber		=	null;
		FlatRatesDOB			flatRatesDOB	=	null;
		ArrayList				list			=	null;
		BuyRatesSessionHome		home			=	null;
		BuyRatesSession			remote			=	null;
		String					weightBreak     =	null;
		StringBuffer			errors          =	null;
		String					flat[]			=	null;
		String					slab[]			=	null;
		String					rate[]			=	null;
		String				 chargeIndicator[]	=	null;
    String         operation			=	null;
    QMSSellRatesDOB sellRatesDOB  = null;
    HashMap        hm_buyRates    = null;
    String        buyrateId       = null;
    String        laneNo          = null;
		try{
        operation = request.getParameter("Operation");
				count	=	Integer.parseInt(request.getParameter("count"));
    			list	=	new ArrayList();	
				errors	=	new StringBuffer();
        
        HttpSession session = request.getSession();
        
        hm_buyRates = (HashMap)session.getAttribute("hm_buyRates");
        
        if(hm_buyRates==null)
        {
          hm_buyRates = new HashMap();
        }
        
        /*sellRatesDOB = new QMSSellRatesDOB();
        insertValues(request,response,loginBean,operation,sellRatesDOB,nextNavigation);

        errorMassege      =   sellRatesRemote.validateSellRatesHdrData(sellRatesDOB);*/
   			for(int i=0;i<count;i++)
				{
   				if("on".equalsIgnoreCase(request.getParameter(""+i)))
					{

						weightBreak		=	request.getParameter("weightBreak");
						rateType		=	request.getParameter("rateType");

						flatRatesDOB	=	new FlatRatesDOB();
				
						if("Slab".equalsIgnoreCase(weightBreak) && "Both".equalsIgnoreCase(rateType))
						{

							flat	=	request.getParameterValues("flat"+i)	;
							slab	=	request.getParameterValues("slab"+i)	;

						//	chargeIndicator  = new String[slab.length+1];
						//	rate			 = new String[slab.length+1];
         
             if(flat.length>slab.length)
             {
                chargeIndicator  = new String[flat.length+2];
                rate			       = new String[flat.length+2];
             }
             else
             {
                chargeIndicator  = new String[slab.length+2];
                 rate			       = new String[slab.length+2];
           }
						   rate[0]	=	request.getParameter("basic"+i)	;
						   rate[1]	=	request.getParameter("min"+i)	;
						   chargeIndicator[0]				=	null;
						   chargeIndicator[1]				=	null;	
							for(int j=0;j<flat.length;j++)
							{
						
								if(flat[j] != null && !"".equals(flat[j])) 
								{
								  rate[j+2]				=  flat[j]		;
								  chargeIndicator[j+2]	=	"FLAT";	

								}
								else if(slab[j] != null && !"".equals(slab[j]))
								{ 
								  rate[j+2]				=  slab[j];
								  chargeIndicator[j+2]	=	"SLAB";	
								}else
                {
                  errors.append(i+",");
                }
							flatRatesDOB.setRates(rate);
					flatRatesDOB.setWtBreaks(request.getParameterValues("weightBreak"+i));
          	flatRatesDOB.setSlabValues(chargeIndicator);
							}	

						}else{
							if( ( request.getParameter("flat"+i)== null || "".equals(request.getParameter("flat"+i).trim())))
							{
								errors.append(i+",");
							}
              
              
             // flatRatesDOB.setSurchargeRates(request.getParameterValues("surcharges"+i));
							flatRatesDOB.setRates(request.getParameterValues("flat"+i));
             // flatRatesDOB.setWtBreakSlab(request.getParameterValues("weighttBkValues"+i));
            
						 flatRatesDOB.setWtBreaks(request.getParameterValues("weightBreak"+i));
						 flatRatesDOB.setSlabValues(request.getParameterValues("indicator"+i));//Govind
            }
						
						
						

            buyrateId = request.getParameter("buyRateId"+i);
						laneNo    = request.getParameter("lanNumber"+i);
            
						flatRatesDOB.setBuyrateId(buyrateId);
						flatRatesDOB.setLaneNo(Integer.parseInt(laneNo));
						
						            
            flatRatesDOB.setRemarks(request.getParameter("notes"+i));
            flatRatesDOB.setExtNotes(request.getParameter("extNotes" + i));
	    flatRatesDOB.setSchCurr(request.getParameterValues("Currency" + i)); // Added By Gowtham for Surcharge Currency 

			
	    list.add(flatRatesDOB);
            
            hm_buyRates.put(buyrateId+"_"+laneNo,flatRatesDOB);
  						
					}else
          {
             buyrateId = request.getParameter("buyRateId"+i);
						 laneNo    = request.getParameter("lanNumber"+i);
            
             hm_buyRates.remove(buyrateId+"_"+laneNo);
          }
				}
        
        session.setAttribute("hm_buyRates",hm_buyRates); 
        
				if(errors.length()>0)
				{
					errors.append(" lines Rate is Empty. Rate Should not be empty");
				}
        
       /* else{
						
					if(list.size()>0)
					{
						home	= (BuyRatesSessionHome)LookUpBean.getEJBHome("BuyRatesSessionBean");
						remote	= (BuyRatesSession)home.create();
						remote.modifyFlatRates(list,loginBean);
					}
				}*/
			}catch(Exception e)
			{
		      e.printStackTrace();
			}
		
		return errors.toString() ;    
  }

	public String modifyBuyRateDtl(HttpServletRequest request, HttpServletResponse response,ESupplyGlobalParameters loginBean)
	 {
		String					weightBreak		=	null;
		String					rateType		=	null;
    
		try{

				weightBreak		=	request.getParameter("weightBreak");
				rateType		=	request.getParameter("rateType");
				//if("Flat".equalsIgnoreCase(weightBreak) && "Flat".equalsIgnoreCase(rateType))
				{
					//return	modifyFlatRates(request,response,loginBean);
          return modifyFlatRatesValues(request,response,loginBean);
				}
			
		}catch(Exception e)
		{
	      e.printStackTrace();
		}
		return null;
	}

  public String modifyFlatRatesValues(HttpServletRequest request, HttpServletResponse response,ESupplyGlobalParameters loginBean)
  {
      FlatRatesDOB			flatRatesDOB	=	null;
      ArrayList				list			=	null;
      Iterator			            itr						                    =	  null;
      HashMap				            hm_buyRates				                  =	  null;
      String				            keyHash					                  =	  null;
      BuyRatesSessionHome		home			=	null;
		  BuyRatesSession			remote			=	null;
      String errorMsg = "";
      try
      {
        HttpSession				        session			                      =	  request.getSession();
        list	=	new ArrayList();
        
        hm_buyRates				        =	  (HashMap)session.getAttribute("hm_buyRates");
        session.removeAttribute("hm_buyRates");
        itr				              =	  (hm_buyRates.keySet()).iterator();
        
        while(itr.hasNext())
        {
          keyHash	=	(String)itr.next();
          
          flatRatesDOB	=	new FlatRatesDOB();
          flatRatesDOB = (FlatRatesDOB)hm_buyRates.get(keyHash);
          list.add(flatRatesDOB);
          
        }
        	if(list.size()>0)
					{
						home	= (BuyRatesSessionHome)LookUpBean.getEJBHome("BuyRatesSessionBean");
						remote	= (BuyRatesSession)home.create();

						remote.modifyFlatRates(list,loginBean);
					}else
          {
            errorMsg = "NoLanes are selected for modify";
          }
        
      }catch(Exception e)
			{
		      e.printStackTrace();
			}
      return errorMsg;
  }

	public String modifyFlatRates(HttpServletRequest request, HttpServletResponse response,ESupplyGlobalParameters loginBean)
	{
		int						count			=	0;
		String					rateType		=	null;
		String					lanNumber		=	null;
		FlatRatesDOB			flatRatesDOB	=	null;
		ArrayList				list			=	null;
		BuyRatesSessionHome		home			=	null;
		BuyRatesSession			remote			=	null;
		String					weightBreak     =	null;
		StringBuffer			errors          =	null;
		String					flat[]			=	null;
		String					slab[]			=	null;
		String					rate[]			=	null;
		String				 chargeIndicator[]	=	null;
		
    String         operation			=	null;
    QMSSellRatesDOB sellRatesDOB  = null;
		try{
        operation = request.getParameter("Operation");
				count	=	Integer.parseInt(request.getParameter("count"));
    			list	=	new ArrayList();	
				errors	=	new StringBuffer();
        
        /*sellRatesDOB = new QMSSellRatesDOB();
        insertValues(request,response,loginBean,operation,sellRatesDOB,nextNavigation);

        errorMassege      =   sellRatesRemote.validateSellRatesHdrData(sellRatesDOB);*/
        

				for(int i=0;i<count;i++)
				{
     			if("on".equalsIgnoreCase(request.getParameter(""+i)))
					{

						weightBreak		=	request.getParameter("weightBreak");
						rateType		=	request.getParameter("rateType");

						flatRatesDOB	=	new FlatRatesDOB();
				
						if("Slab".equalsIgnoreCase(weightBreak) && "Both".equalsIgnoreCase(rateType))
						{

							flat	=	request.getParameterValues("flat"+i)	;
							slab	=	request.getParameterValues("slab"+i)	;

							chargeIndicator  = new String[slab.length+1];
							rate			 = new String[slab.length+1];
							
						   rate[0]	=	request.getParameter("min"+i)	;

						   chargeIndicator[0]				=	null;	
							for(int j=0;j<flat.length;j++)
							{
						
								if(flat[j] != null && !"".equals(flat[j])) 
								{
								  rate[j+1]				=  flat[j]		;
								  chargeIndicator[j+1]	=	"FLAT";	

								}
								else if(slab[j] != null && !"".equals(slab[j]))
								{
								  rate[j+1]				=  slab[j];
              	  chargeIndicator[j+1]	=	"SLAB";	
								}else
                {
                  errors.append(i+",");
                }
							flatRatesDOB.setRates(rate);
							flatRatesDOB.setSlabValues(chargeIndicator);
							}	

						}else{
							if( ( request.getParameter("flat"+i)== null || "".equals(request.getParameter("flat"+i).trim())))
							{
								errors.append(i+",");
							}


							flatRatesDOB.setRates(request.getParameterValues("flat"+i));
						}
						
						
						


						
						flatRatesDOB.setBuyrateId(request.getParameter("buyRateId"+i));
						flatRatesDOB.setLaneNo(Integer.parseInt(request.getParameter("lanNumber"+i)));
						
								list.add(flatRatesDOB);
					}
				}
				if(errors.length()>0)
				{
					errors.append(" lines Rate is Empty. Rate Should not be empty");
				}else{
						
					if(list.size()>0)
					{
						home	= (BuyRatesSessionHome)LookUpBean.getEJBHome("BuyRatesSessionBean");
						remote	= (BuyRatesSession)home.create();
      			remote.modifyFlatRates(list,loginBean);
					}
				}
			}catch(Exception e)
			{
		      e.printStackTrace();
			}
		
		return errors.toString() ;

	}
}