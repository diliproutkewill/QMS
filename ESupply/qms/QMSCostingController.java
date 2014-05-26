import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.ejb.EJBException;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.foursoft.esupply.common.bean.ESupplyGlobalParameters;
import com.foursoft.esupply.common.exception.FoursoftException;
import com.foursoft.esupply.common.java.ErrorMessage;
import com.foursoft.esupply.common.java.KeyValue;
import com.foursoft.esupply.common.java.LookUpBean;
import com.foursoft.esupply.common.util.ConnectionUtil;
import com.foursoft.esupply.common.util.ESupplyDateUtility;
import com.foursoft.etrans.common.util.java.OperationsImpl;
import com.lowagie.text.Anchor;
import com.lowagie.text.Cell;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.Table;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;
import com.lowagie.text.pdf.PdfWriter;
import com.qms.operations.costing.dob.CostingChargeDetailsDOB;
import com.qms.operations.costing.dob.CostingHDRDOB;
import com.qms.operations.costing.dob.CostingLegDetailsDOB;
import com.qms.operations.costing.dob.CostingMasterDOB;
import com.qms.operations.costing.dob.CostingRateInfoDOB;
import com.qms.operations.quote.ejb.sls.QMSQuoteSession;
import com.qms.operations.quote.ejb.sls.QMSQuoteSessionHome;


public class QMSCostingController extends HttpServlet
{
    private static final String CONTENT_TYPE = "text/html; charset=windows-1252";
    private static final String FILE_NAME = "QMSCostingController";
    private static Logger logger = null;

  public void init(ServletConfig config) throws ServletException
  {
    super.init(config);
    logger  = Logger.getLogger(FILE_NAME);


  }

  public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
  {
    doPost(request,response);

  }

  public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
  {

    ESupplyGlobalParameters	loginbean			= null;
    HttpSession 			    session 	      =	request.getSession();
    String operation                      = request.getParameter("Operation");
    String subOperation                   = request.getParameter("subOperation");
    String origin                         = null;
    String destination                    = null;
    String advantage                      = null;
    String customerId                     = null;
    String quoteId                        = null;
    String validTill                      = null;
    ESupplyDateUtility formater           = null;
    CostingHDRDOB costingHDRDOB           = null;
    CostingMasterDOB costingMasterDOB     = null;
    StringBuffer errorMsg                 = null;
    ArrayList	quoteLanes				  = null;//Added by Mohan for issue no.219979 on 10122010

    double  noOfPieces                    = 0.0;
    String  uom                           = null;
    double  actualWeight                  = 0;
    double  volume                        = 0;
    double  invValue                      = 0;
    String  invCurrency                   = null;
    String  baseCurrency                  = null;
    String  volumeUom                     = null;

    String breakValue                     = null;
    String primaryUnitValue               = null;
    String secUnitValue                   = null;
    String densityRatio                   = null;
    Timestamp currentDate                 = null;
    OperationsImpl operationsImpl        = null;
    String					dateFormat				   =	null;

    String                    email       = null;
    String                    fax         = null;
    String                    print       = null;
    String 					  fromPage    = null;//Added by Mohan for issue no.219979 on 10122010
    String 					  isFetchNext = null;//Added by Mohan for issue no.219979 on 10122010
    String					  laneNo	  = null;//Added by Mohan for issue no.219979 on 10122010
    //Added by Rakesh on 24-01-2011 for CR:231219
    String 					  transittimeCheck = null;
    String 					  frequencyCheck = null;
    String 					  commodityTypeCheck = null;
    String 					  carrierCheck		 = null;
    String 					  serviceLevelCheck	 = null;
    String 					  noOfPiecesCheck	 = null;
    String 					  actualWeightCheck = null;
    String 					  uomCheck = null;
    String 					  volumeCheck = null;
    String                    customer_Id_Costing =null;
    String 					  costingNotes  =null;
    int listSize=0;
    


    ArrayList legDetails    = null;
    ArrayList chargeList    = null;
    ArrayList destChargeList    = null;
    ArrayList rateList      = null;

    CostingLegDetailsDOB costingLegDetailsDOB = null;
    CostingChargeDetailsDOB costingChargeDetailsDOB = null;
    CostingRateInfoDOB costingRateInfoDOB = null;
    ArrayList          exchangeRatesList  = new ArrayList();
    ArrayList          costingChargeList  = new ArrayList();//@@Added by kameswari for Surcharge Enhancements
   ArrayList   detailsList  = null;
   LinkedHashSet    exset = null;
        double convFactor = 0;
         int countDetail;
         int  shipmentMode =0;
         String customerName = null;
String salesPersonEmailId = request.getParameter("opEmailId");//@@ Added by subrahmanyam for the Enhancement 146444 on 10/02/09
String[] salesPersonEmailList = null; //@@ Added by subrahmanyam for the WPBN issue- 168100 on 20/04/09
HashMap attentionLOV = null;//@@ added by phani sekhar for wpbn 167678
    try
    {
      operationsImpl = new OperationsImpl();
      formater = new ESupplyDateUtility();
      errorMsg = new StringBuffer();
      loginbean = (ESupplyGlobalParameters)session.getAttribute("loginbean");
      //System.out.println("sdfgsd sekharalsdkfjla"+operation);
      //System.out.println("sdfgsd"+subOperation);
      //Added by Mohan for issue no.219979 on 10122010
      fromPage = request.getParameter("fromPage");
      isFetchNext = request.getParameter("isFetchNext");
      laneNo = request.getParameter("laneNo");
      
      if(operation!=null && "Add".equals(operation))
      {
        if(subOperation!=null &&  "enter".equals(subOperation))
        {
          //origin      = request.getParameter("origin");
         // destination = request.getParameter("destination");
          advantage   = request.getParameter("advantage");
         // customerId  = request.getParameter("customerid");
          quoteId     = request.getParameter("quoteid");
          validTill   = request.getParameter("validtill");
      if(request.getParameter("origin")!=null && request.getParameter("origin").trim().length()>0)
        origin = request.getParameter("origin");
      if(request.getParameter("destination")!=null && request.getParameter("destination").trim().length()>0)
        destination = request.getParameter("destination");
      if(request.getParameter("customerid")!=null && request.getParameter("customerid").trim().length()>0)
        customerId = request.getParameter("customerid");
      //added by silpa.p on 3-06-11
      if(request.getParameter("customer_Id")!=null && request.getParameter("customer_Id").trim().length()>0)
    	  customer_Id_Costing = request.getParameter("customer_Id");//ended
          if(request.getParameter("shipmentMode")!=null && request.getParameter("shipmentMode").trim().length()>0)
      {
      if("AIR".equalsIgnoreCase(request.getParameter("shipmentMode")))
        shipmentMode =  1;
     else if("SEA".equalsIgnoreCase(request.getParameter("shipmentMode")))
         shipmentMode =  2;
     else if("TRUCK".equalsIgnoreCase(request.getParameter("shipmentMode")))
          shipmentMode =  4;
      }
          costingHDRDOB = new CostingHDRDOB();
         if(request.getParameter("CustomerName")!=null && request.getParameter("CustomerName").trim().length()>0)
             customerName = request.getParameter("CustomerName");
       //Added by Rakesh on 24-01-2011 for CR:231219
         if(request.getParameter("listSize")!=null){
          listSize = Integer.parseInt(request.getParameter("lane"));
          costingHDRDOB.setServiceLevel(request.getParameter("serviceLevel"+listSize));
		  costingHDRDOB.setCarrier(request.getParameter("carrier"+listSize));
         }
         

          costingHDRDOB.setOrigin(origin);
          costingHDRDOB.setDestination(destination);
          costingHDRDOB.setAdvantage(advantage);
          costingHDRDOB.setCustomerid(customerId);
          costingHDRDOB.setQuoteid(quoteId);
          costingHDRDOB.setAccessLevel(loginbean.getAccessType());
          costingHDRDOB.setTerminalId(loginbean.getTerminalId());
          costingHDRDOB.setUserId(loginbean.getUserId());
           costingHDRDOB.setCompanyName(customerName);
           costingHDRDOB.setShipmentMode(shipmentMode);
           costingHDRDOB.setLaneNo( (laneNo!=null && !"".equals(laneNo))? laneNo :"1" );//Added by Mohan for issue no.219979 on 10122010

            try{

             if(validTill!=null && !"".equals(validTill))
             {
                dateFormat         =	loginbean.getUserPreferences().getDateFormat();

                currentDate = new Timestamp((new Date()).getTime());

                if(formater.getTimestamp(dateFormat,validTill).compareTo(currentDate) < 0 )
                  errorMsg.append("Valid Till Date should be greater than Current Date");
                costingHDRDOB.setValidtill(formater.getTimestamp(dateFormat,validTill));

             }


            }catch(Exception ie)
            {
                errorMsg.append("Invalid date format  ");

                logger.info(FILE_NAME+"in date exception"+ie);
            }

          errorMsg.append(validateHDR(costingHDRDOB,loginbean));

          if(errorMsg!=null && !"".equals(errorMsg.toString()))
          {
              request.setAttribute("errorMsg",errorMsg.toString());
              errorMsg=null;
              doDispatcher(request, response, "etrans/QMSCostingEnterId.jsp?Operation="+operation+"&subOperation=");

          }else
          {   //Added by Mohan for issue no.219979 on 10122010
        	  if(fromPage!=null && "MultiCosting".equals(fromPage) && (isFetchNext==null || "".equals(isFetchNext)))
          {
        		  	 quoteLanes = getQuoteLanesInfo(request,response,costingHDRDOB,loginbean);
        		  	 costingHDRDOB.setQuoteLanes(quoteLanes);
		             session.setAttribute("costingHDRDOB",costingHDRDOB);
		             session.setAttribute("costingMasterDOB",costingMasterDOB);
		             doDispatcher(request, response, "etrans/QMSMultiLaneCosting.jsp?Operation="+operation+"&subOperation="+subOperation+"&fromPage="+fromPage);

        	  } //added by silpa.p on 3-06-11
        	  if(fromPage!=null && "MultiQuote".equals(fromPage) && (isFetchNext==null || "".equals(isFetchNext)))
              {
     		  	 quoteLanes = getQuoteLanesInfo(request,response,costingHDRDOB,loginbean);
     		  	 costingHDRDOB.setQuoteLanes(quoteLanes);
		             session.setAttribute("costingHDRDOB",costingHDRDOB);
		             session.setAttribute("costingMasterDOB",costingMasterDOB);
		             doDispatcher(request, response, "etrans/QMSCostingEnterId.jsp?Operation="+operation+"&subOperation="+subOperation+"&fromPage="+fromPage);
              }//ended
        	  else{
             costingMasterDOB = doGetQuoteInfo(request,response,costingHDRDOB,loginbean);
             costingHDRDOB.setOrigin(costingMasterDOB.getOrigin());
             costingHDRDOB.setDestination(costingMasterDOB.getDestination());
             costingHDRDOB.setCustomerid(costingMasterDOB.getCustomerid());
             costingHDRDOB.setIncoterms(costingMasterDOB.getIncoterms());
             costingHDRDOB.setCommodityType(costingMasterDOB.getCommodityType());
             costingHDRDOB.setVersionNo(costingMasterDOB.getVersionNo());

             session.setAttribute("costingHDRDOB",costingHDRDOB);
             session.setAttribute("costingMasterDOB",costingMasterDOB);
             doDispatcher(request, response, "etrans/QMSCostingDetails.jsp?Operation="+operation+"&subOperation="+subOperation);
          }
          }



        }
        else if(subOperation!=null &&  ("details".equals(subOperation)||"calculate".equalsIgnoreCase(subOperation)))
        {

        countDetail  = Integer.parseInt(request.getParameter("countDetail"));



          costingHDRDOB = (CostingHDRDOB)session.getAttribute("costingHDRDOB");

          noOfPieces = (request.getParameter("noofpieces")!=null && !"".equals(request.getParameter("noofpieces")))?Double.parseDouble(request.getParameter("noofpieces")):0.0;
          uom        = request.getParameter("uom");
          actualWeight= (request.getParameter("actualweight")!=null && !"".equals(request.getParameter("actualweight")))?Double.parseDouble(request.getParameter("actualweight")):0.0;
          volume      = (request.getParameter("volume")!=null && !"".equals(request.getParameter("volume")))?Double.parseDouble(request.getParameter("volume")):0;
          volumeUom   = request.getParameter("volumeUom");
          invValue   = (request.getParameter("invvalue")!=null && !"".equals(request.getParameter("invvalue")))?Double.parseDouble(request.getParameter("invvalue")):0.0;
          invCurrency= request.getParameter("invcurrency");
          baseCurrency=request.getParameter("basecurrency");
          costingHDRDOB.setSalesPersonEmail(salesPersonEmailId);//added by subrahmanyam for 146444 on 10/02/09
          costingHDRDOB.setNoOfPieces(noOfPieces);
          costingHDRDOB.setUom(uom);
          costingHDRDOB.setActualWeight(actualWeight);
          costingHDRDOB.setVolume(volume);
          costingHDRDOB.setInvValue(invValue);
          costingHDRDOB.setInvCurrency(invCurrency);
          costingHDRDOB.setBaseCurrency(baseCurrency);
          costingHDRDOB.setVolumeUom(volumeUom);
          costingHDRDOB.setIncoterms(request.getParameter("incoterms")!=null?request.getParameter("incoterms"):"");
          costingHDRDOB.setNotes(request.getParameter("notes")!=null?request.getParameter("notes"):"");
          session.setAttribute("costingHDRDOB",costingHDRDOB);

          String validCurrency = null;

          if(baseCurrency!=null && baseCurrency.trim().length()>0)
          {
            validCurrency = validateCurrency(baseCurrency,loginbean);
          }else
          {
            validCurrency = "BaseCurrency should not be Empty";
          }


          if(validCurrency!=null && validCurrency.trim().length()>0)
          {
              request.setAttribute("errorMsg",validCurrency);
              doDispatcher(request, response, "etrans/QMSCostingDetails.jsp?Operation="+operation+"&subOperation="+subOperation);

          }
          else
          {

                costingMasterDOB = (CostingMasterDOB)session.getAttribute("costingMasterDOB");
           //    logger.info("contactPersons"+request.getParameterValues("contactPersons"));
              /*  if(request.getParameterValues("contactPersons")!=null && request.getParameterValues("contactPersons").length!=0)
                {
                  costingMasterDOB.setContactPersonNames(request.getParameterValues("contactPersons"));
                  costingMasterDOB.setContactPersonIds(request.getParameter("contactIds").split(","));
                }*/

                  if(request.getParameterValues("contactPersons")!=null && request.getParameterValues("contactPersons").length!=0)
                {
                  costingMasterDOB.setContactPersonNames(request.getParameterValues("contactPersons"));
                  costingMasterDOB.setContactPersonIds(request.getParameter("contactIds").split(","));
                }
                //adeed by phani sekhar for wpbn 167678 on 20090415
                if(request.getParameter("userModifiedMailIds")!=null && request.getParameter("userModifiedMailIds").length()>0)
                {
                costingMasterDOB.setContactEmailIds(request.getParameter("userModifiedMailIds").split("&"));
                }

                 //@@added by phani sekhar for 167768
                    if(request.getParameter("attentionCustomerId")!=null && request.getParameter("attentionCustomerId").length()>0)
                  {
                  attentionLOV = new HashMap();

                  if(request.getParameter("attentionCustomerId")!=null && request.getParameter("attentionCustomerId").length()>0)
                  attentionLOV.put("customerId",request.getParameter("attentionCustomerId").split(","));

                  if(request.getParameter("attentionSlno")!=null && request.getParameter("attentionSlno").length()>0)
                  attentionLOV.put("slNo",request.getParameter("attentionSlno").split(","));

                  if(request.getParameter("attentionEmailId")!=null && request.getParameter("attentionEmailId").length()>0)
                  attentionLOV.put("emailId",request.getParameter("attentionEmailId").split(","));

                  if(request.getParameter("attentionFaxNo")!=null && request.getParameter("attentionFaxNo").length()>0)
                  attentionLOV.put("faxNo",request.getParameter("attentionFaxNo").split(","));

                  if(request.getParameter("attentionContactNo")!=null && request.getParameter("attentionContactNo").length()>0)
                  attentionLOV.put("contactNo",request.getParameter("attentionContactNo").split(","));

                  }
                  if("details".equals(subOperation) && attentionLOV!=null)
                  {
                     QMSQuoteSessionHome home              = null;
                     QMSQuoteSession     remote            = null;
                    home        = (QMSQuoteSessionHome)LookUpBean.getEJBHome("QMSQuoteSessionBean");
                    remote      = (QMSQuoteSession)home.create();
                    if(attentionLOV!=null)
                    {
                      remote.updateAttentionToContacts(attentionLOV);
                    }
                  }
                //@@ ends 167768

                email       = request.getParameter("email");
                fax         = request.getParameter("fax");
                print       = request.getParameter("print");

                if(email==null)
                {
                  if("Y".equalsIgnoreCase(costingMasterDOB.getEmailFlag()))
                      email = "on";
                }
                if(fax==null)
                {
                  if("Y".equalsIgnoreCase(costingMasterDOB.getFaxFlag()))
                      fax = "on";
                }
                if(print==null)
                {
                  if("Y".equalsIgnoreCase(costingMasterDOB.getPrintFlag()))
                      print = "on";
                }
                if("on".equalsIgnoreCase(email))
                    costingMasterDOB.setEmailFlag("Y");
                else
                    costingMasterDOB.setEmailFlag("N");
                if("on".equalsIgnoreCase(fax))
                    costingMasterDOB.setFaxFlag("Y");
                else
                    costingMasterDOB.setFaxFlag("N");

                if("on".equalsIgnoreCase(print))
                    costingMasterDOB.setPrintFlag("Y");
                else
                    costingMasterDOB.setPrintFlag("N");


                chargeList = costingMasterDOB.getOriginList();
                if(chargeList!=null && chargeList.size()>0)
                {
                    int chargeListSize = chargeList.size();
                    for(int i=0;i<chargeListSize;i++)
                    {
                     if(request.getParameter("origincheck"+i)!=null)
                      {
                        costingChargeDetailsDOB = (CostingChargeDetailsDOB)chargeList.get(i);

                     if(costingChargeDetailsDOB.getWeightBreak().equalsIgnoreCase("List"))
                        {
                            costingChargeDetailsDOB.setPrimaryUnitArray(request.getParameterValues("orginP"+i));
                            costingChargeDetailsDOB.setSecUnitArray(request.getParameterValues("orginS"+i));
                        }
                        else
                        {
                          costingChargeDetailsDOB.setBrkPoint((request.getParameter("originBreakPoint"+i)==null || "".equals(request.getParameter("originBreakPoint"+i))  ) ?"1":request.getParameter("originBreakPoint"+i) );
                          costingChargeDetailsDOB.setPrimaryUnitValue(request.getParameter("orginP"+i));
                          costingChargeDetailsDOB.setSecUnitValue(request.getParameter("orginS"+i));
                        }

                        convFactor = operationsImpl.getConvertionFactor(costingChargeDetailsDOB.getCurrency(),costingHDRDOB.getBaseCurrency());
                      
                        if(!(exchangeRatesList.contains(costingChargeDetailsDOB.getCurrency()+"~"+convFactor)) && !costingChargeDetailsDOB.getCurrency().equalsIgnoreCase(costingHDRDOB.getBaseCurrency()))
                              exchangeRatesList.add(costingChargeDetailsDOB.getCurrency()+"~"+convFactor);

                        costingChargeDetailsDOB.setConvFactor(convFactor);

                        //costingChargeDetailsDOB = doCalculate(costingHDRDOB,costingChargeDetailsDOB);
                        //@@Modified by Kameswari for Surcharge Enhancements
                        
                        System.out.println("origin charges : "+costingChargeDetailsDOB.getWeightBreak());
                        costingChargeList          = doCalculate(costingHDRDOB,costingChargeDetailsDOB);
                        costingChargeDetailsDOB    = (CostingChargeDetailsDOB)costingChargeList.get(0);
                       costingChargeDetailsDOB.setChecked("checked");

                        chargeList.remove(i);
                        chargeList.add(i,costingChargeDetailsDOB);


                      }else
                      {

                        costingChargeDetailsDOB = (CostingChargeDetailsDOB)chargeList.get(i);
                        if("checked".equals(costingChargeDetailsDOB.getChecked()))
                        {
                            costingChargeDetailsDOB.setChecked("");
                            chargeList.remove(i);
                            chargeList.add(i,costingChargeDetailsDOB);
                        }
                      }

                    }

                    costingMasterDOB.setOriginList(chargeList);
                }

               /* destChargeList = costingMasterDOB.getDestinationList();

                if(destChargeList!=null && destChargeList.size()>0)
                {
                    //System.out.println("destChargeList.size():"+destChargeList.size());
                    for(int i=0;i<destChargeList.size();i++)
                    {
                      if(request.getParameter("destcheck"+i)!=null)
                      {
                        costingChargeDetailsDOB = (CostingChargeDetailsDOB)destChargeList.get(i);

                        if(costingChargeDetailsDOB.getWeightBreak().equalsIgnoreCase("List"))
                        {
                            costingChargeDetailsDOB.setPrimaryUnitArray(request.getParameterValues("destP"+i));
                            costingChargeDetailsDOB.setSecUnitArray(request.getParameterValues("destS"+i));
                        }
                        else
                        {
                          costingChargeDetailsDOB.setBrkPoint((request.getParameter("destBreakPoint"+i)==null || "".equals(request.getParameter("destBreakPoint"+i))) ?"1":request.getParameter("destBreakPoint"+i) );
                          costingChargeDetailsDOB.setPrimaryUnitValue(request.getParameter("destP"+i));
                          costingChargeDetailsDOB.setSecUnitValue(request.getParameter("destS"+i));
                        }
                       /* costingChargeDetailsDOB.setBrkPoint((request.getParameter("destBreakPoint"+i)==null || "".equals(request.getParameter("destBreakPoint"+i)) ) ?"1" :request.getParameter("destBreakPoint"+i) );
                        costingChargeDetailsDOB.setPrimaryUnitValue(request.getParameter("destP"+i));
                        costingChargeDetailsDOB.setSecUnitValue(request.getParameter("destS"+i));*/

                       /* convFactor = operationsImpl.getConvertionFactor(costingChargeDetailsDOB.getCurrency(),costingHDRDOB.getBaseCurrency());

                        if(!exchangeRatesList.contains(costingChargeDetailsDOB.getCurrency()+"~"+convFactor))
                              exchangeRatesList.add(costingChargeDetailsDOB.getCurrency()+"~"+convFactor);

                        costingChargeDetailsDOB.setConvFactor(convFactor);

                        costingChargeDetailsDOB = (costingHDRDOB,costingChargeDetailsDOB);
                        costingChargeDetailsDOB.setChecked("checked");
                        destChargeList.remove(i);
                        destChargeList.add(i,costingChargeDetailsDOB);

                      }else
                      {

                        costingChargeDetailsDOB = (CostingChargeDetailsDOB)destChargeList.get(i);
                        if("checked".equals(costingChargeDetailsDOB.getChecked()))
                        {
                            costingChargeDetailsDOB.setChecked("");
                            destChargeList.remove(i);
                            destChargeList.add(i,costingChargeDetailsDOB);
                        }
                      }
                    }
                    costingMasterDOB.setDestinationList(destChargeList);
                }  */

                legDetails = costingMasterDOB.getCostingLegDetailsList();

                int count=0;
                String exchg_str= "";

                if(legDetails!=null && legDetails.size()>0)
                {
                      int legsize = legDetails.size();
                      for(int i=0;i<legsize;i++)
                      {
                         //System.out.println("in leg for i="+i +"legsize"+legsize);
                          costingLegDetailsDOB = (CostingLegDetailsDOB)legDetails.get(i);

                          chargeList  = costingLegDetailsDOB.getCostingChargeDetailList();
                          if(chargeList!=null && chargeList.size()>0)
                          {
                              //System.out.println("chargeList.size():"+chargeList.size());
                        	  int chargeListSize 	= chargeList.size();
                             // for(int j=0;j<chargeList.size();j++)
                        	 
                        	  for(int j=0;j<chargeListSize;j++)
                              {
                                /*if(request.getParameter("frtcheck"+count)!=null)
                                {*///logger.info("subOperation"+subOperation);
                                   if("calculate".equals(subOperation)||countDetail>1)
                                   {
                                      detailsList = (ArrayList)chargeList.get(j);
                                      costingChargeDetailsDOB =(CostingChargeDetailsDOB)detailsList.get(0);
                                       }

                                  else
                                  {
                                    costingChargeDetailsDOB = (CostingChargeDetailsDOB)chargeList.get(j);
                                   }
                             //     costingChargeDetailsDOB = (CostingChargeDetailsDOB)chargeList.get(j);

                                
                                   
                                   if(costingChargeDetailsDOB.getWeightBreak().equalsIgnoreCase("List"))
                                  {


                                      costingChargeDetailsDOB.setPrimaryUnitArray(request.getParameterValues("frtP"+count));
                                      costingChargeDetailsDOB.setSecUnitArray(request.getParameterValues("frtS"+count));

                                       if( "calculate".equalsIgnoreCase(subOperation))
                                      {
                                          costingChargeDetailsDOB.setPrimaryUnitValue(request.getParameter("frtP"+count));
                                         // if(request.getParameter("cafP"+count)!=null)
                                          if(request.getParameter("cafP"+count)!=null&&("CAF%".equalsIgnoreCase(costingChargeDetailsDOB.getRateDescription())||"Currency Adjustment Factor %".equalsIgnoreCase(costingChargeDetailsDOB.getRateDescription())))
                                               costingChargeDetailsDOB.setPrimaryUnitValue(request.getParameter("cafP"+count));
                                          if(request.getParameter("surchargeP"+count)!=null)
                                              costingChargeDetailsDOB.setPrimaryUnitValue(request.getParameter("surchargeP"+count));
                                      }
                                      else   if( "details".equalsIgnoreCase(subOperation))
                                      {
                                      costingChargeDetailsDOB.setPrimaryUnitValue(null);

                                      }
                                      costingChargeDetailsDOB.setSecUnitValue(request.getParameter("frtS"+count));

                                  }
                                  else
                                  {
                                      costingChargeDetailsDOB.setBrkPoint((request.getParameter("frtBreakPoint"+count)==null || "".equals(request.getParameter("frtBreakPoint"+count)))?"1":request.getParameter("frtBreakPoint"+count));

                                  if( "calculate".equalsIgnoreCase(subOperation))
                                  {
                                      costingChargeDetailsDOB.setPrimaryUnitValue(request.getParameter("frtP"+count));
                                     
                                      if(request.getParameter("cafP"+count)!= null)
                                          costingChargeDetailsDOB.setPrimaryUnitValue(request.getParameter("cafP"+count));
                                      if(request.getParameter("surchargeP"+count)!=null)
                                          costingChargeDetailsDOB.setPrimaryUnitValue(request.getParameter("surchargeP"+count));
                                  }
                                  else   if( "details".equalsIgnoreCase(subOperation))
                                  {
                                  costingChargeDetailsDOB.setPrimaryUnitValue(null);

                                  }
                                      costingChargeDetailsDOB.setSecUnitValue(request.getParameter("frtS"+count));

                                  }

                               //   convFactor = operationsImpl.getConvertionFactor(costingChargeDetailsDOB.getCurrency(),costingHDRDOB.getBaseCurrency());
                                  // Commented by govind bcoz of fetching the conversion factor in docalaculate method

                                 /* if(!(exchangeRatesList.contains(costingChargeDetailsDOB.getCurrency()+"~"+convFactor)) && !costingChargeDetailsDOB.getCurrency().equalsIgnoreCase(costingHDRDOB.getBaseCurrency()))
                                        exchangeRatesList.add(costingChargeDetailsDOB.getCurrency()+"~"+convFactor);
                                        Commented by govind for not getting the prope exchange rates in costing pdf*/
                                  costingChargeDetailsDOB.setConvFactor(convFactor);

                                  //if(!costingChargeDetailsDOB.getWeightBreak().equalsIgnoreCase("List"))
                                  //costingChargeDetailsDOB = doCalculate(costingHDRDOB,costingChargeDetailsDOB);
                                   //@@Modified by Kameswari for Surcharge Enhancements
                              
                                  	System.out.println("getWeightBreak controller : "+costingChargeDetailsDOB.getWeightBreak());
                                 
                                  costingChargeList          = doCalculate(costingHDRDOB,costingChargeDetailsDOB);


                               costingChargeDetailsDOB.setChecked("checked");

                                  chargeList.remove(j);
                                  
                                  if(costingChargeList!= null)
                                  {
                                	  for(int ex=0;ex<costingChargeList.size();ex++)
                                	  {
                                		  exchg_str=  ((CostingChargeDetailsDOB)costingChargeList.get(ex)).getCurrency()+"~"+((CostingChargeDetailsDOB)costingChargeList.get(ex)).getConvFactor();
                                		  if(exchangeRatesList!= null && !exchangeRatesList.contains(exchg_str) ){//@@ Modified by govind for getting multiple exchange rates in costing pdf
                                			  exchangeRatesList.add(exchg_str);
                                		  }
                                	  }
                                  }
                                	  
                                  
                               //   chargeList.add(j,costingChargeDetailsDOB);
                               chargeList.add(j,costingChargeList);
                                /*}else
                                {

                                  costingChargeDetailsDOB = (CostingChargeDetailsDOB)chargeList.get(j);
                                  if("checked".equals(costingChargeDetailsDOB.getChecked()))
                                  {
                                      costingChargeDetailsDOB.setChecked("");
                                      chargeList.remove(j);
                                      chargeList.add(j,costingChargeDetailsDOB);
                                  }
                                }*/

                                count++;
                              }
                          }
                          costingLegDetailsDOB.setCostingChargeDetailList(chargeList);
                          //System.out.println("i value ="+i);
                          legDetails.remove(i);
                          legDetails.add(i,costingLegDetailsDOB);
                          //System.out.println("end");
                      }
                      costingMasterDOB.setCostingLegDetailsList(legDetails);
                }

                destChargeList = costingMasterDOB.getDestinationList();

                if(destChargeList!=null && destChargeList.size()>0)
                {
                    //System.out.println("destChargeList.size():"+destChargeList.size());
                	int destChargListSize	= destChargeList.size();
                	//for(int i=0;i<destChargeList.size();i++)
                    for(int i=0;i<destChargListSize;i++)
                    {

                      if(request.getParameter("destcheck"+i)!=null)
                      {

            costingChargeDetailsDOB = (CostingChargeDetailsDOB)destChargeList.get(i);
                        if(costingChargeDetailsDOB.getWeightBreak().equalsIgnoreCase("List"))
                        {
                            costingChargeDetailsDOB.setPrimaryUnitArray(request.getParameterValues("destP"+i));
                            costingChargeDetailsDOB.setSecUnitArray(request.getParameterValues("destS"+i));
                        }
                        else
                        {
                          costingChargeDetailsDOB.setBrkPoint((request.getParameter("destBreakPoint"+i)==null || "".equals(request.getParameter("destBreakPoint"+i))) ?"1":request.getParameter("destBreakPoint"+i) );
                          costingChargeDetailsDOB.setPrimaryUnitValue(request.getParameter("destP"+i));
                          costingChargeDetailsDOB.setSecUnitValue(request.getParameter("destS"+i));
                        }
                       /* costingChargeDetailsDOB.setBrkPoint((request.getParameter("destBreakPoint"+i)==null || "".equals(request.getParameter("destBreakPoint"+i)) ) ?"1" :request.getParameter("destBreakPoint"+i) );
                        costingChargeDetailsDOB.setPrimaryUnitValue(request.getParameter("destP"+i));
                        costingChargeDetailsDOB.setSecUnitValue(request.getParameter("destS"+i));*/

                       convFactor = operationsImpl.getConvertionFactor(costingChargeDetailsDOB.getCurrency(),costingHDRDOB.getBaseCurrency());
                     
                        if(!(exchangeRatesList.contains(costingChargeDetailsDOB.getCurrency()+"~"+convFactor)) && !costingChargeDetailsDOB.getCurrency().equalsIgnoreCase(costingHDRDOB.getBaseCurrency()))
                                 exchangeRatesList.add(costingChargeDetailsDOB.getCurrency()+"~"+convFactor);

                        costingChargeDetailsDOB.setConvFactor(convFactor);

                    //costingChargeDetailsDOB = doCalculate(costingHDRDOB,costingChargeDetailsDOB);
                        //@@Modified by Kameswari for Surcharge Enhancements
                         costingChargeList          = doCalculate(costingHDRDOB,costingChargeDetailsDOB);
                         costingChargeDetailsDOB    = (CostingChargeDetailsDOB)costingChargeList.get(0);
                         costingChargeDetailsDOB.setChecked("checked");
                         destChargeList.remove(i);
                         //destChargeList.add(i,costingChargeDetailsDOB);
                         destChargeList.add(i,costingChargeDetailsDOB);

                      }else
                      {

                        costingChargeDetailsDOB = (CostingChargeDetailsDOB)destChargeList.get(i);
                        if("checked".equals(costingChargeDetailsDOB.getChecked()))
                        {
                            costingChargeDetailsDOB.setChecked("");
                            destChargeList.remove(i);
                            destChargeList.add(i,costingChargeDetailsDOB);
                        }
                      }
                    }
                    costingMasterDOB.setDestinationList(destChargeList);
                }
                costingMasterDOB.setExchangeRatesList(exchangeRatesList);
                session.setAttribute("costingMasterDOB",costingMasterDOB);

                //Added by Rakesh on 24-01-2011 for CR:231219
                transittimeCheck       = request.getParameter("transittimeCheck");
                frequencyCheck         = request.getParameter("frequencyCheck");
                commodityTypeCheck     = request.getParameter("commodityTypeCheck");	
                carrierCheck		   = request.getParameter("carrierCheck");	
                serviceLevelCheck	   = request.getParameter("serviceLevelCheck"); 
                noOfPiecesCheck	       = request.getParameter("noOfPiecesCheck");
                actualWeightCheck      = request.getParameter("actualWeightCheck");
                uomCheck               = request.getParameter("uomCheck");
                volumeCheck            = request.getParameter("volumeCheck");
                
                
                if("on".equalsIgnoreCase(transittimeCheck))
                    costingMasterDOB.setTransittimeCheck("Y");
                else
                    costingMasterDOB.setTransittimeCheck("N");
                if("on".equalsIgnoreCase(frequencyCheck))
                    costingMasterDOB.setFrequencyCheck("Y");
                else
                    costingMasterDOB.setFrequencyCheck("N");
                if("on".equalsIgnoreCase(commodityTypeCheck))
                    costingMasterDOB.setCommodityTypeCheck("Y");
                else
                    costingMasterDOB.setCommodityTypeCheck("N");
                if("on".equalsIgnoreCase(carrierCheck))
                    costingMasterDOB.setCarrierCheck("Y");
                else
                    costingMasterDOB.setCarrierCheck("N");
                if("on".equalsIgnoreCase(serviceLevelCheck))
                    costingMasterDOB.setServiceLevelCheck("Y");
                else
                    costingMasterDOB.setServiceLevelCheck("N");
                if("on".equalsIgnoreCase(noOfPiecesCheck))
                    costingMasterDOB.setNoOfPiecesCheck("Y");
                else
                    costingMasterDOB.setNoOfPiecesCheck("N");
                if("on".equalsIgnoreCase(actualWeightCheck))
                    costingMasterDOB.setActualWeightCheck("Y");
                else
                    costingMasterDOB.setActualWeightCheck("N");
                if("on".equalsIgnoreCase(uomCheck))
                    costingMasterDOB.setUomCheck("Y");
                else
                    costingMasterDOB.setUomCheck("N");
                if("on".equalsIgnoreCase(volumeCheck))
                    costingMasterDOB.setVolumeCheck("Y");
                else
                    costingMasterDOB.setVolumeCheck("N");
                
                costingNotes		=	request.getParameter("costingNotes");
                if(costingNotes!=null)
                	costingMasterDOB.setCostingNotes(costingNotes);
                else
                	costingMasterDOB.setCostingNotes("");
                //doCalculate(costingHDRDOB);
                doDispatcher(request, response, "etrans/QMSCostingDetails.jsp?Operation="+operation+"&subOperation="+subOperation);
          }

        }
        else if(subOperation!=null &&  ("sendmailsave".equals(subOperation) || "save".equals(subOperation) ) )
        {

             costingHDRDOB = (CostingHDRDOB)session.getAttribute("costingHDRDOB");

             costingHDRDOB.setIncoterms(request.getParameter("incoterms")!=null?request.getParameter("incoterms"):"");
             costingHDRDOB.setTerminalId(loginbean.getTerminalId());
             costingHDRDOB.setUserId(loginbean.getUserId());
             costingMasterDOB = (CostingMasterDOB)session.getAttribute("costingMasterDOB");

             if(request.getParameterValues("contactPersons")!=null && request.getParameterValues("contactPersons").length!=0)
              {
                costingMasterDOB.setContactPersonNames(request.getParameterValues("contactPersons"));
                costingMasterDOB.setContactPersonIds(request.getParameter("contactIds").split(","));
              }

              email       = request.getParameter("email");
              fax         = request.getParameter("fax");
              print       = request.getParameter("print");

              if(email==null)
              {
                if("Y".equalsIgnoreCase(costingMasterDOB.getEmailFlag()))
                    email = "on";
              }
              if(fax==null)
              {
                if("Y".equalsIgnoreCase(costingMasterDOB.getFaxFlag()))
                    fax = "on";
              }
              if(print==null)
              {
                if("Y".equalsIgnoreCase(costingMasterDOB.getPrintFlag()))
                    print = "on";
              }
              if("on".equalsIgnoreCase(email))
                  costingMasterDOB.setEmailFlag("Y");
              else
                  costingMasterDOB.setEmailFlag("N");
              if("on".equalsIgnoreCase(fax))
                  costingMasterDOB.setFaxFlag("Y");
              else
                  costingMasterDOB.setFaxFlag("N");

              if("on".equalsIgnoreCase(print))
                  costingMasterDOB.setPrintFlag("Y");
              else
                  costingMasterDOB.setPrintFlag("N");

            costingMasterDOB.setOriginTotal((request.getParameter("originTotal")!=null)?request.getParameter("originTotal"):"");
            costingMasterDOB.setFrtTotal((request.getParameter("frtTotal")!=null)?request.getParameter("frtTotal"):"");
            costingMasterDOB.setDestTotal((request.getParameter("destTotal")!=null)?request.getParameter("destTotal"):"");
            costingMasterDOB.setTotal((request.getParameter("total")!=null)?request.getParameter("total"):"");
            costingMasterDOB.setExtNotes((request.getParameter("extNotes")!=null)?request.getParameter("extNotes"):"");//  Modified by Rakesh on 21-03-2011
            //Added by Rakesh on 24-01-2011 for CR:231219
            transittimeCheck       = request.getParameter("transittimeCheck");
            frequencyCheck         = request.getParameter("frequencyCheck");
            commodityTypeCheck     = request.getParameter("commodityTypeCheck");	
            carrierCheck		   = request.getParameter("carrierCheck");	
            serviceLevelCheck	   = request.getParameter("serviceLevelCheck"); 
            noOfPiecesCheck	       = request.getParameter("noOfPiecesCheck");
            actualWeightCheck      = request.getParameter("actualWeightCheck");
            uomCheck               = request.getParameter("uomCheck");
            volumeCheck            = request.getParameter("volumeCheck");
            
            if("on".equalsIgnoreCase(transittimeCheck))
                costingMasterDOB.setTransittimeCheck("Y");
            else
                costingMasterDOB.setTransittimeCheck("N");
            if("on".equalsIgnoreCase(frequencyCheck))
                costingMasterDOB.setFrequencyCheck("Y");
            else
                costingMasterDOB.setFrequencyCheck("N");
            if("on".equalsIgnoreCase(commodityTypeCheck))
                costingMasterDOB.setCommodityTypeCheck("Y");
            else
                costingMasterDOB.setCommodityTypeCheck("N");
            if("on".equalsIgnoreCase(carrierCheck))
                costingMasterDOB.setCarrierCheck("Y");
            else
                costingMasterDOB.setCarrierCheck("N");
            if("on".equalsIgnoreCase(serviceLevelCheck))
                costingMasterDOB.setServiceLevelCheck("Y");
            else
                costingMasterDOB.setServiceLevelCheck("N");
            if("on".equalsIgnoreCase(noOfPiecesCheck))
                costingMasterDOB.setNoOfPiecesCheck("Y");
            else
                costingMasterDOB.setNoOfPiecesCheck("N");
            if("on".equalsIgnoreCase(actualWeightCheck))
                costingMasterDOB.setActualWeightCheck("Y");
            else
                costingMasterDOB.setActualWeightCheck("N");
            if("on".equalsIgnoreCase(uomCheck))
                costingMasterDOB.setUomCheck("Y");
            else
                costingMasterDOB.setUomCheck("N");
            if("on".equalsIgnoreCase(volumeCheck))
                costingMasterDOB.setVolumeCheck("Y");
            else
                costingMasterDOB.setVolumeCheck("N");
            
            costingNotes		=	request.getParameter("costingNotes");
            if(costingNotes!=null)
            	costingMasterDOB.setCostingNotes(costingNotes);
            else
            	costingMasterDOB.setCostingNotes("");

            //Added by Rakesh on 24-01-2011 for CR:231219
            costingMasterDOB.setTerminalId(costingHDRDOB.getTerminalId());
            costingMasterDOB.setQuoteId(costingHDRDOB.getQuoteid());
            costingMasterDOB.setOperation(operation);
            doSaveProcess(costingHDRDOB,costingMasterDOB,request,response);




        }
        else
        {
          doDispatcher(request, response, "etrans/QMSCostingEnterId.jsp?Operation="+operation+"&subOperation=");
        }


      }


    }catch(Exception e)
    {
        errorMsg=null;
        e.printStackTrace();

        //Logger.info(FILE_NAME,"Exception in doPost method while forwarding"+e);
        logger.info(FILE_NAME+"Exception in doPost method while forwarding"+e);
        doForwardToErrorPage(loginbean,request,response);
    }


  }

  private void  doSaveProcess(CostingHDRDOB costingHDRDOB ,CostingMasterDOB costingMasterDOB,HttpServletRequest request, HttpServletResponse response)throws Exception
  {

    String              nextNavigation    = "";
		ArrayList						keyValueList			= new ArrayList();
		StringBuffer				errorMessage		  =	new StringBuffer();
		ErrorMessage				errorMessageObject= null;
    String              errorMsg          = "";

    QMSQuoteSessionHome home              = null;
    QMSQuoteSession     remote            = null;
    boolean             success           = false;
    String subOperation = "";
    ArrayList       returnList      	  =   null;
    ArrayList       sentList        	  =   null;
    ArrayList       unsentList      	  =   null;
    ArrayList       sentFaxList           =   null;
    ArrayList       unsentFaxList         =   null;
    String          forwardPage           =   "QMSErrorPage.jsp";
    try
    {


             subOperation = request.getParameter("subOperation");

              HttpSession 			    session 	      =	request.getSession();

              home        = (QMSQuoteSessionHome)LookUpBean.getEJBHome("QMSQuoteSessionBean");
              remote      = (QMSQuoteSession)home.create();

              success = remote.insertCostingHDR(costingHDRDOB);

              /*contactPersons  =   costingMasterDOB.getContactEmailIds();

               if(contactPersons!=null)
              {
                for(int i=0;i<contactPersons.length;i++)
                {
                  if(i==(contactPersons.length-1))
                  {
                    toEmailIds.append(costingMasterDOB.getContactEmailIds()[i]);
                  }
                  else
                  {
                    toEmailIds.append(costingMasterDOB.getContactEmailIds()[i]).append(",");
                  }
                }
              }
              else
              {
                toEmailIds.append(costingMasterDOB.getEmailId());
              }*/

              errorMsg = "Costing Successfully Generated.\n";
              //try
              //{


                if("sendmailsave".equals(subOperation))
                {
                	
                	costingMasterDOB = getQuoteContentDtl(costingMasterDOB);
                	
                  //generate PDF and send mail;
                  returnList  = doPDFGeneration(costingHDRDOB,costingMasterDOB,request);

                  if(returnList!=null && returnList.size()>0)
                  {
                    sentList  = (ArrayList)returnList.get(0);
                    if(sentList!=null && sentList.size()>0)
                      errorMsg  = errorMsg+"Costing Has been Successfully sent through Email to "+sentList+ " ";
                    unsentList = (ArrayList)returnList.get(1);

                    if(unsentList!=null && unsentList.size()>0)
                      errorMsg  = errorMsg+" Email(s) Were Not Sent To "+unsentList + " ";

                    sentFaxList = (ArrayList)returnList.get(2);
                    if(sentFaxList!=null && sentFaxList.size()>0)
                      errorMsg  = errorMsg + "Costing has been successfully sent through Fax to "+sentFaxList + " ";

                    unsentFaxList = (ArrayList)returnList.get(3);
                    if(unsentFaxList != null && unsentFaxList.size()>0)
                      errorMsg =   errorMsg + "Fax Was Not Sent to  "+ unsentFaxList;

                     errorMsg    =   errorMsg.trim();

                     //@@ To Remove the Comma at the End of the message (if any).
                     if (errorMsg.indexOf(",")!=-1)
                     {
                       if(errorMsg.indexOf(",") == errorMsg.length()-1)
                          errorMsg = errorMsg.substring(0,errorMsg.length()-1);
                     }

                      if("Y".equalsIgnoreCase(costingMasterDOB.getPrintFlag()))
                        forwardPage = forwardPage+"?print=on";
                  }
                }
              /*}
              catch(FoursoftException e)
              {
                errorMsg = "Record successfully inserted,but mail was not send to the mail id "+toEmailIds;
              }*/

              session.removeAttribute("costingHDRDOB");
              session.removeAttribute("costingMasterDOB");

              if(success)
              {
		            //Added by Mohan for issue no.219979 on 10122010
                    errorMessageObject = new ErrorMessage(errorMsg,"QMSCostingController?Operation=Add&subOperation=&fromPage="+request.getParameter("fromPage")+"&quoteid="+request.getParameter("quoteid"));
                    keyValueList.add(new KeyValue("ErrorCode","RSI"));
                    keyValueList.add(new KeyValue("Operation","Add"));
                    errorMessageObject.setKeyValueList(keyValueList);
                    request.setAttribute("ErrorMessage",errorMessageObject);
                    doDispatcher(request,response,forwardPage);//use send redirect
                    //session.setAttribute("ErrorMessageObj",errorMessageObject);
                    //response.sendRedirect("QMSESupplyErrorPage.jsp");
              }else
              {
		            //Added by Mohan for issue no.219979 on 10122010
                    errorMessageObject = new ErrorMessage("Error while inserting","QMSCostingController?Operation=Add&subOperation=&fromPage="+request.getParameter("fromPage"));
                    keyValueList.add(new KeyValue("ErrorCode","ERR"));
                    keyValueList.add(new KeyValue("Operation","Add"));
                    errorMessageObject.setKeyValueList(keyValueList);
                    request.setAttribute("ErrorMessage",errorMessageObject);
                    doDispatcher(request,response,"QMSESupplyErrorPage.jsp");//use send redirect
                    //session.setAttribute("ErrorMessageObj",errorMessageObject);
                    //response.sendRedirect("QMSESupplyErrorPage.jsp");
              }


    }catch(Exception e)
    {
      e.printStackTrace();
      throw new Exception();
    }
  }
    private ArrayList doPDFGeneration(CostingHDRDOB costingHDRDOB,CostingMasterDOB costingMasterDOB,HttpServletRequest request)throws FoursoftException
  {

    ArrayList sentList      =   new ArrayList();
    ArrayList unsentList    =   new ArrayList();
    ArrayList sentFaxList   =   new ArrayList();
    ArrayList unsentFaxList =   new ArrayList();
    ArrayList returnList    =   new ArrayList();
    boolean   isOriginChecked = false;
    boolean   isDestChecked =   false;
    DecimalFormat   deciFormat  = null;
    DecimalFormat   dFormat     = null;
    DecimalFormat   formatTotal = null;
    DecimalFormat   currencyFormat = null;
    ArrayList list_exNotes =  null;
    Color     DHLColor      =   new Color(255,204,0);//@@Color Gold of DHL
    HttpSession session = null;
    QMSQuoteSessionHome home      =     null;
    QMSQuoteSession     remote    =     null;

	 String[]        salesPersonEmailList = null; //@@ Added by subrahmanyam for changes to 146444 on 20-04-09
	 String             transitTime     = null;
	//Added by Rakesh on 24-01-2011 for CR:231219
	 String[]           contents        = null;
	 String[]           levels          = null;
	 String[]           aligns          = null;
	 String[]           headFoot        = null;
    try
    {
     home    =     (QMSQuoteSessionHome)LookUpBean.getEJBHome("QMSQuoteSessionBean");
        remote  =     (QMSQuoteSession)home.create();
                int frtListSize     = costingMasterDOB.getCostingLegDetailsList().size();
                int originListSize	=	costingMasterDOB.getOriginList().size();
                int destSize        = costingMasterDOB.getDestinationList().size();
                deciFormat          = new DecimalFormat("##0.00");
                dFormat             = new DecimalFormat("##");
                formatTotal         = new DecimalFormat("##,##,##0.00");
                currencyFormat      = new DecimalFormat("0.000000");
                //logger.debug(dFormat.format(1245.0));
                list_exNotes        =  (ArrayList)costingMasterDOB.getExternalNotes();

            ESupplyDateUtility  eSupplyDateUtility=	new ESupplyDateUtility();
            ESupplyGlobalParameters loginbean  = (ESupplyGlobalParameters)request.getSession().getAttribute("loginbean");
            eSupplyDateUtility.setPatternWithTime("DD-MONTH-YYYY");
            //eSupplyDateUtility.setPatternWithTime(loginbean.getUserPreferences().getDateFormat());
            String[] strDate  = eSupplyDateUtility.getDisplayStringArray(costingMasterDOB.getDateOfQuotation());
            String[] effDate  = eSupplyDateUtility.getDisplayStringArray(costingMasterDOB.getEffectiveFrom());
            String[] validTo  = eSupplyDateUtility.getDisplayStringArray(costingMasterDOB.getValidityOfQuote());// Added by Gowtham for Costing report issue.	 
//@@ Added by subrahmanyam for the enhancement on 16/12/2008 for 146443
            String curDate    = eSupplyDateUtility.getCurrentDateString("DD-MONTH-YYYY");



            CostingLegDetailsDOB      legDetails      = null;
            CostingChargeDetailsDOB   detailsDOB      = null;
            ArrayList                 rateDetails     = null;
            CostingRateInfoDOB        rateDetailsDOB  = null;
            			        session 	      =	request.getSession();
            //ESupplyGlobalParameters loginbean = (ESupplyGlobalParameters)session.getAttribute("loginbean");
            StringBuffer              subject         = new StringBuffer("");
            StringBuffer              attentionTo     = new StringBuffer("");

            //costingMasterDOB    =   getContactPersonDetails(costingMasterDOB);
            //Logger.info(FILE_NAME,"headerDOB.getAttentionTo()::"+headerDOB.getAttentionTo());
            if(costingMasterDOB.getContactPersonNames()!=null)
            {
              for(int i=0;i<costingMasterDOB.getContactPersonNames().length;i++)
              {
                //Logger.info(FILE_NAME,"headerDOB.getAttentionTo()::"+masterDOB.getCustomerContacts()[i]);
                attentionTo.append(costingMasterDOB.getContactPersonNames()[i]!=null?costingMasterDOB.getContactPersonNames()[i]:"");
                if(i!=(costingMasterDOB.getContactPersonNames().length-1))
                  attentionTo.append(",");
              }
            }

            Document document = new Document(PageSize.A4,54f,54f,68.4f,68.4f);//@@ 36 points represent 0.5 inch
            String file_tsmp = ""+new java.sql.Timestamp((new java.util.Date()).getTime());
            file_tsmp        = file_tsmp.replaceAll("\\:","");
            file_tsmp        = file_tsmp.replaceAll("\\.","");
            file_tsmp        = file_tsmp.replaceAll("\\-","");
            file_tsmp        = file_tsmp.replaceAll(" ","");
            String PDF_FILE_NAME = "Costing"+file_tsmp+".pdf";
            document.addTitle("Costing Report");
            document.addSubject("Costing PDF");
            document.addKeywords("Test, Key Words");
            document.addAuthor("Rama Krishna");
            document.addCreator("Auto Generated through 4S DHL");
            document.addCreationDate();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
	          PdfWriter writer	=	PdfWriter.getInstance(document, baos);
            document.open();
            //document.setMargins(8,8,8,8);
            int[] widths = {8,8,8,8,8,8,12,28};
            Table mainT = new Table(8);
            mainT.setWidths(widths);
            mainT.setWidth(100);
            //mainT.setBackgroundColor(Color.ORANGE);
            // vanishing table border
             mainT.setBorderColor(Color.white);
             mainT.setPadding(1);
             mainT.setSpacing(0);
             String shipmentMode = "";

            Phrase  headingPhrase    =  new Phrase("",FontFactory.getFont("ARIAL", 10, Font.BOLD,Color.BLACK));
            Cell cellHeading = new Cell(headingPhrase);
            cellHeading.setBorderColor(new Color(255,255, 255));
            cellHeading.setHorizontalAlignment(cellHeading.ALIGN_CENTER);
            cellHeading.setColspan(7);
            mainT.addCell(cellHeading);
            Cell imageCell = new Cell();
            //java.awt.Image img0 = ImageIO.read(getServletContext().getResourceAsStream("images/DHLlogo.gif"));

            java.net.URL url =  getServletConfig().getServletContext().getResource("/images/DHLlogo.gif");
            Image img0 = Image.getInstance(url);
            //img0.setAlignment(Image.RIGHT);
            //img0.setBorder(0);
            //img0.scaleAbsoluteHeight(45);
            //img0.scaleAbsoluteWidth(100);
            imageCell.add(img0);
            imageCell.setHorizontalAlignment(imageCell.ALIGN_CENTER);
            //imageCell.add(getServletContext().getResourceAsStream("images/DHLlogo.gif"));
            imageCell.setHorizontalAlignment(imageCell.ALIGN_RIGHT);
            imageCell.setBorderWidth(0);
            imageCell.setNoWrap(true);
            mainT.addCell(imageCell);
            mainT.setAlignment(mainT.ALIGN_CENTER);
            document.add(mainT);

            //System.out.println("After Heading --------------------------->");
            Table part = new Table(1,8);
            part.setBorderWidth(0);
            part.setWidth(100);
            part.setBorderColor(new Color(255,255, 255));
            //part.setBackgroundColor(Color.ORANGE);
            part.setPadding(1);
            part.setSpacing(0);
            part.setAutoFillEmptyCells(true);
            part.setTableFitsPage(true);
            part.setAlignment(part.ALIGN_CENTER);
           // part.setWidth(100.0f);
            Cell cell;
            Chunk chk = new Chunk(costingMasterDOB.getShipmentMode().toUpperCase()+" FREIGHT COSTING",FontFactory.getFont("ARIAL", 10, Font.BOLD,Color.BLACK));
            cell = new Cell(chk);
            cell.setWidth("100");
            cell.setBorderWidth(0);
            cell.setNoWrap(true);
            cell.setLeading(8.0f);
            cell.setHorizontalAlignment(cell.ALIGN_CENTER);
            part.addCell(cell);
            //document.add(part);
            //@@Modified by Kameswari for the WPBN Change Request-71229
            if("MY".equalsIgnoreCase(costingMasterDOB.getCustomerCountryId()))
            {
            chk = new Chunk((costingMasterDOB.getOriginCountryName()!=null?costingMasterDOB.getOriginCountryName().toUpperCase():"")+" TO "+(costingMasterDOB.getDestCountryName()!=null?costingMasterDOB.getDestCountryName().toUpperCase():""),FontFactory.getFont("ARIAL", 10, Font.BOLD,Color.BLUE));
            cell = new Cell(chk);
            cell.setWidth("100");
            cell.setBorderWidth(0);
            cell.setNoWrap(true);
            cell.setLeading(15.0f);//@@Do Not Decrease
            cell.setHorizontalAlignment(cell.ALIGN_CENTER);
            part.addCell(cell);

            cell = new Cell("");
            cell.setBorderWidth(0);
            cell.setLeading(5.0f);
            part.addCell(cell);


            chk = new Chunk(costingMasterDOB.getCustomerName(),FontFactory.getFont("ARIAL", 10, Font.BOLD,Color.BLUE));
            cell = new Cell(chk);
            cell.setHeader(true);
            cell.setWidth("100");
            cell.setBorderWidth(0);
            cell.setNoWrap(true);
            cell.setLeading(8.0f);
            cell.setHorizontalAlignment(cell.ALIGN_CENTER);
            part.addCell(cell);

            chk = new Chunk("ATTENTION TO: "+attentionTo.toString(),FontFactory.getFont("ARIAL", 10, Font.BOLD,Color.BLUE));
            cell = new Cell(chk);
            cell.setWidth("100");
            cell.setBorderWidth(0);
            cell.setNoWrap(true);
            cell.setLeading(13.0f);
            cell.setHorizontalAlignment(cell.ALIGN_CENTER);
            part.addCell(cell);

            cell = new Cell("");
            cell.setBorderWidth(0);
            cell.setLeading(5.0f);
            part.addCell(cell);
            }
            else
            {
            chk = new Chunk((costingMasterDOB.getOriginCountryName()!=null?costingMasterDOB.getOriginCountryName().toUpperCase():"")+" TO "+(costingMasterDOB.getDestCountryName()!=null?costingMasterDOB.getDestCountryName().toUpperCase():""),FontFactory.getFont("ARIAL", 10, Font.BOLD,Color.RED));
            cell = new Cell(chk);
            cell.setWidth("100");
            cell.setBorderWidth(0);
            cell.setNoWrap(true);
            cell.setLeading(15.0f);//@@Do Not Decrease
            cell.setHorizontalAlignment(cell.ALIGN_CENTER);
            part.addCell(cell);

            cell = new Cell("");
            cell.setBorderWidth(0);
            cell.setLeading(5.0f);
            part.addCell(cell);


            chk = new Chunk(costingMasterDOB.getCustomerName(),FontFactory.getFont("ARIAL", 10, Font.BOLD,Color.RED));
            cell = new Cell(chk);
            cell.setHeader(true);
            cell.setWidth("100");
            cell.setBorderWidth(0);
            cell.setNoWrap(true);
            cell.setLeading(8.0f);
            cell.setHorizontalAlignment(cell.ALIGN_CENTER);
            part.addCell(cell);

            chk = new Chunk("ATTENTION TO: "+attentionTo.toString(),FontFactory.getFont("ARIAL", 10, Font.BOLD,Color.RED));
            cell = new Cell(chk);
            cell.setWidth("100");
            cell.setBorderWidth(0);
            cell.setNoWrap(true);
            cell.setLeading(13.0f);
            cell.setHorizontalAlignment(cell.ALIGN_CENTER);
            part.addCell(cell);

            cell = new Cell("");
            cell.setBorderWidth(0);
            cell.setLeading(5.0f);
            part.addCell(cell);
            }
            //@@WPBN  Change Request-71229
            chk = new Chunk("Costing Based On Quote: "+costingHDRDOB.getQuoteid(),FontFactory.getFont("ARIAL", 10, Font.BOLD,Color.BLACK));
            cell = new Cell(chk);
            cell.setWidth("100");
            cell.setBorderWidth(0);
            cell.setNoWrap(true);
            cell.setLeading(8.0f);
            cell.setHorizontalAlignment(cell.ALIGN_CENTER);
            part.addCell(cell);

            //modified by silpa.p on 9-06-11
          /*  chk = new Chunk("Date of Quotation: "+strDate[0],FontFactory.getFont("ARIAL", 10, Font.BOLD,Color.BLACK));
            cell = new Cell(chk);
            cell.setWidth("100");
            cell.setBorderWidth(0);
            cell.setNoWrap(true);
            cell.setLeading(12.0f);
            cell.setHorizontalAlignment(cell.ALIGN_CENTER);
            part.addCell(cell);
            
            chk = new Chunk("Effective From: "+effDate[0],FontFactory.getFont("ARIAL", 10, Font.BOLD,Color.BLACK));
            cell = new Cell(chk);
            cell.setWidth("100");
            cell.setBorderWidth(0);
            cell.setNoWrap(true);
            cell.setLeading(12.0f);
            cell.setHorizontalAlignment(cell.ALIGN_CENTER);
            part.addCell(cell);*/ //ended
            
            chk = new Chunk("Validity Of Quote: "+ (validTo != null ? (validTo[0]!= null? validTo[0] : ""):"") ,FontFactory.getFont("ARIAL", 10, Font.BOLD,Color.BLACK));
            cell = new Cell(chk);
            cell.setWidth("100");
            cell.setBorderWidth(0);
            cell.setNoWrap(true);
            cell.setLeading(12.0f);
            cell.setHorizontalAlignment(cell.ALIGN_CENTER);
            part.addCell(cell);
            
//@@ Added By Subrahmanyam for the Enhancement on 16/12/2008 for 146443
            chk = new Chunk("Date of Costing: "+curDate,FontFactory.getFont("ARIAL", 10, Font.BOLD,Color.BLACK));
            cell = new Cell(chk);
            cell.setWidth("100");
            cell.setBorderWidth(0);
            cell.setNoWrap(true);
            cell.setLeading(12.0f);
            cell.setHorizontalAlignment(cell.ALIGN_CENTER);
            part.addCell(cell);
    //@@ Ended By Subrahmanyam for The Enhancement on 16/12/2008 for 146443
            document.add(part);

            part  =  new Table(1);
            part.setOffset(8);
            part.setWidth(100);
            part.setPadding(1);
            part.setSpacing(0);
            part.setBackgroundColor(Color.WHITE);
            part.setBorderColor(Color.black);
            part.setBorderWidth(0);

            chk = new Chunk("Total Cost, Based On Below Details =  "+((costingHDRDOB.getBaseCurrency()!=null)?costingHDRDOB.getBaseCurrency():"")+" "+((costingMasterDOB.getTotal()!=null && costingMasterDOB.getTotal().trim().length()>0)?formatTotal.format(Double.parseDouble(costingMasterDOB.getTotal())):""),FontFactory.getFont("ARIAL", 9, Font.BOLD,Color.BLACK));
            cell = new Cell(chk);
            cell.setBackgroundColor(DHLColor);
            cell.setHorizontalAlignment(cell.ALIGN_CENTER);
            cell.setVerticalAlignment(cell.ALIGN_BOTTOM);
            cell.setBorderWidth(1);
            cell.setNoWrap(true);
            part.addCell(cell);

            /*chk = new Chunk(((costingHDRDOB.getBaseCurrency()!=null)?costingHDRDOB.getBaseCurrency():"")+" "+costingMasterDOB.getTotal(),FontFactory.getFont("ARIAL", 12, Font.BOLD,Color.BLACK));
            cell = new Cell(chk);
            cell.setBackgroundColor(DHLColor);
            cell.setNoWrap(true);
            cell.setHorizontalAlignment(cell.ALIGN_RIGHT);
            cell.setBorderWidth(1);
            part.addCell(cell);*/

            document.add(part);

            ///////////////////////////////////////////Second Table////////////////////////////
            int[] hdrWidths = {15,17,5,12,15,8,15,13};
            part  =  new Table(8);
            part.setOffset(8);//@@To control the space between the table and the previous element.
            part.setWidth(100);
            part.setWidths(hdrWidths);
            part.setPadding(1);
            part.setSpacing(0);
            part.setBackgroundColor(Color.WHITE);
            part.setBorderColor(Color.black);
            part.setBorderWidth(0);

            /*chk = new Chunk("Volume (length x width x height) ",FontFactory.getFont("ARIAL", 8, Font.BOLDITALIC,Color.BLACK));
            cell = new Cell(chk);
            cell.setNoWrap(true);
            cell.setColspan(3);
            cell.setBorderWidth(0);

            part.addCell(cell);


            chk = new Chunk(deciFormat.format(costingHDRDOB.getVolume()),FontFactory.getFont("ARIAL", 8, Font.BOLD,Color.BLACK));
            cell = new Cell(chk);
            cell.setNoWrap(true);
            cell.setBorderWidth(1);
            //cell.setBorder(1);
            cell.setBackgroundColor(Color.ORANGE);
            cell.setHorizontalAlignment(cell.ALIGN_RIGHT);
            cell.setVerticalAlignment(cell.ALIGN_BOTTOM);
            part.addCell(cell);

            chk = new Chunk("",FontFactory.getFont("ARIAL", 8, Font.BOLDITALIC,Color.BLACK));
            cell = new Cell(chk);
            cell.setBorderWidth(0);
            part.addCell(cell);


            chk = new Chunk("Unit of Volume",FontFactory.getFont("ARIAL", 8, Font.BOLDITALIC,Color.BLACK));
            cell = new Cell(chk);
            cell.setNoWrap(true);
            cell.setColspan(2);
            cell.setBorderWidth(0);
            part.addCell(cell);

            chk = new Chunk(costingHDRDOB.getVolumeUom(),FontFactory.getFont("ARIAL", 8, Font.BOLD,Color.BLACK));
            cell = new Cell(chk);
            cell.setNoWrap(true);
            //cell.setBorder(0);
            cell.setBorderWidth(1);
            cell.setHorizontalAlignment(cell.ALIGN_RIGHT);
            cell.setVerticalAlignment(cell.ALIGN_BOTTOM);
            cell.setBackgroundColor(Color.ORANGE);
            part.addCell(cell);

            chk = new Chunk("",FontFactory.getFont("ARIAL", 8, Font.BOLDITALIC,Color.BLACK));
            cell = new Cell(chk);
            cell.setBorderWidth(0);
            part.addCell(cell);*/

            //Added by Rakesh on 24-01-2011 for CR:231219
            contents = costingMasterDOB.getContentOnQuote();
            levels   = costingMasterDOB.getLevels();
            aligns   = costingMasterDOB.getAlign();
            headFoot = costingMasterDOB.getHeaderFooter();
            Table content  = null;
            if(contents!=null && contents.length>0)
            {
              content  =  new Table(1);
              content.setOffset(5);
              content.setWidth(100);
              content.setPadding(1);
              content.setSpacing(0);
              content.setBackgroundColor(Color.WHITE);
              content.setBorderColor(Color.black);
              content.setBorderWidth(1f);
              Cell  cellContent =  null;
              chk         =  null;
              int headFootLen	= headFoot.length;
              for(int i=0;i<headFootLen;i++)
              {
                if(headFoot[i]!=null && "H".equalsIgnoreCase(headFoot[i]))
                {
                  chk = new Chunk(contents[i],FontFactory.getFont("ARIAL", 7, Font.ITALIC,Color.BLACK));
                  cellContent = new Cell(chk);
                  cellContent.setBorder(0);
                  cellContent.setLeading(8.0f);
                  cellContent.setBackgroundColor(Color.LIGHT_GRAY);
                  if("L".equalsIgnoreCase(aligns[i]))
                   cellContent.setHorizontalAlignment(cellContent.ALIGN_LEFT);
                  else if("C".equalsIgnoreCase(aligns[i]))
                   cellContent.setHorizontalAlignment(cellContent.ALIGN_CENTER);
                  else if("R".equalsIgnoreCase(aligns[i]))
                   cellContent.setHorizontalAlignment(cellContent.ALIGN_RIGHT);                   
                  content.addCell(cellContent);                  
                }
              }
              document.add(content);
            }

            if(costingMasterDOB.getActualWeightCheck()!=null && "Y".equalsIgnoreCase(costingMasterDOB.getActualWeightCheck())){
            chk = new Chunk("Actual Weight",FontFactory.getFont("ARIAL", 7, Font.BOLD,Color.BLACK));
            cell = new Cell(chk);
            cell.setLeading(8.0f);
            cell.setNoWrap(true);
            cell.setVerticalAlignment(cell.ALIGN_CENTER);
            cell.setBorderWidth(0);
            part.addCell(cell);


            chk = new Chunk(deciFormat.format(costingHDRDOB.getActualWeight()),FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
            cell = new Cell(chk);
            cell.setNoWrap(true);
            cell.setLeading(8.0f);
            cell.setBorderWidth(1);
            cell.setBorderColor(Color.GRAY);
            cell.setHorizontalAlignment(cell.ALIGN_LEFT);
            cell.setVerticalAlignment(cell.ALIGN_CENTER);
            part.addCell(cell);

            chk = new Chunk(costingHDRDOB.getUom()!=null?costingHDRDOB.getUom():"",FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
            cell = new Cell(chk);
            cell.setLeading(8.0f);
            cell.setVerticalAlignment(cell.ALIGN_CENTER);
            cell.setBorderWidth(0);
            part.addCell(cell);
            }else{
            	chk = new Chunk("",FontFactory.getFont("ARIAL", 7, Font.BOLD,Color.BLACK));
                cell = new Cell(chk);
                cell.setLeading(8.0f);
                cell.setNoWrap(true);
                cell.setVerticalAlignment(cell.ALIGN_CENTER);
                cell.setBorderWidth(0);
                part.addCell(cell);


                chk = new Chunk("",FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
                cell = new Cell(chk);
                cell.setNoWrap(true);
                cell.setLeading(8.0f);
                cell.setBorderWidth(0);
                cell.setBorderColor(Color.GRAY);
                cell.setHorizontalAlignment(cell.ALIGN_LEFT);
                cell.setVerticalAlignment(cell.ALIGN_CENTER);
                part.addCell(cell);

                chk = new Chunk("",FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
                cell = new Cell(chk);
                cell.setLeading(8.0f);
                cell.setVerticalAlignment(cell.ALIGN_CENTER);
                cell.setBorderWidth(0);
                part.addCell(cell);	
            }
            /*chk = new Chunk("Unit of Weight",FontFactory.getFont("ARIAL", 8, Font.BOLDITALIC,Color.BLACK));
            cell = new Cell(chk);
            cell.setNoWrap(true);
            cell.setBorderWidth(0);
            cell.setColspan(2);
            part.addCell(cell);*/
            if(costingMasterDOB.getVolumeCheck()!=null && "Y".equalsIgnoreCase(costingMasterDOB.getVolumeCheck())){
            chk = new Chunk("Volume ",FontFactory.getFont("ARIAL", 7, Font.BOLD,Color.BLACK));
            cell = new Cell(chk);
            cell.setLeading(8.0f);
            cell.setNoWrap(true);
            cell.setVerticalAlignment(cell.ALIGN_CENTER);
            //cell.setColspan(3);
            cell.setBorderWidth(0);

            part.addCell(cell);

            chk = new Chunk(deciFormat.format(costingHDRDOB.getVolume()),FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
            cell = new Cell(chk);
            cell.setLeading(8.0f);
            cell.setNoWrap(true);
            cell.setBorderWidth(1);
            cell.setBorderColor(Color.GRAY);
            cell.setHorizontalAlignment(cell.ALIGN_LEFT);
            cell.setVerticalAlignment(cell.ALIGN_CENTER);
            part.addCell(cell);

            chk = new Chunk(costingHDRDOB.getVolumeUom()!=null?costingHDRDOB.getVolumeUom():"",FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
            cell = new Cell(chk);
            cell.setLeading(8.0f);
            cell.setBorderWidth(0);
            cell.setVerticalAlignment(cell.ALIGN_CENTER);
            part.addCell(cell);
            }else{
            	chk = new Chunk("",FontFactory.getFont("ARIAL", 7, Font.BOLD,Color.BLACK));
                cell = new Cell(chk);
                cell.setLeading(8.0f);
                cell.setNoWrap(true);
                cell.setVerticalAlignment(cell.ALIGN_CENTER);
                //cell.setColspan(3);
                cell.setBorderWidth(0);

                part.addCell(cell);

                chk = new Chunk("",FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
                cell = new Cell(chk);
                cell.setLeading(8.0f);
                cell.setNoWrap(true);
                cell.setBorderWidth(0);
                cell.setBorderColor(Color.GRAY);
                cell.setHorizontalAlignment(cell.ALIGN_LEFT);
                cell.setVerticalAlignment(cell.ALIGN_CENTER);
                part.addCell(cell);

                chk = new Chunk("",FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
                cell = new Cell(chk);
                cell.setLeading(8.0f);
                cell.setBorderWidth(0);
                cell.setVerticalAlignment(cell.ALIGN_CENTER);
                part.addCell(cell);	
            }

            /*chk = new Chunk(costingHDRDOB.getUom(),FontFactory.getFont("ARIAL", 8, Font.BOLD,Color.BLACK));
            cell = new Cell(chk);
            cell.setNoWrap(true);
            //cell.setBorder(0);
            cell.setBorderWidth(1);
            cell.setBackgroundColor(Color.ORANGE);
            cell.setHorizontalAlignment(cell.ALIGN_RIGHT);
            cell.setVerticalAlignment(cell.ALIGN_BOTTOM);
            part.addCell(cell);

            chk = new Chunk("",FontFactory.getFont("ARIAL", 8, Font.BOLDITALIC,Color.BLACK));
            cell = new Cell(chk);
            cell.setBorderWidth(0);
            part.addCell(cell);*/
            if(costingMasterDOB.getNoOfPiecesCheck()!=null && "Y".equalsIgnoreCase(costingMasterDOB.getNoOfPiecesCheck())){
            chk = new Chunk("No. Of Pieces",FontFactory.getFont("ARIAL", 7, Font.BOLD,Color.BLACK));
            cell = new Cell(chk);
            cell.setLeading(8.0f);
            cell.setNoWrap(true);
            cell.setVerticalAlignment(cell.ALIGN_CENTER);
            cell.setBorderWidth(0);
            //cell.setColspan(3);
            part.addCell(cell);


            chk = new Chunk(dFormat.format(costingHDRDOB.getNoOfPieces()),FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
            cell = new Cell(chk);
            cell.setLeading(8.0f);
            cell.setNoWrap(true);
            //cell.setBorder(0);
            cell.setBorderWidth(1);
            cell.setBorderColor(Color.GRAY);
            cell.setHorizontalAlignment(cell.ALIGN_LEFT);
            cell.setVerticalAlignment(cell.ALIGN_CENTER);
            part.addCell(cell);

            //@@One Empty Row
            chk = new Chunk("");//@@,FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK)
            cell = new Cell(chk);
            cell.setBorderWidth(0);
            cell.setLeading(5);
            cell.setColspan(8);
            part.addCell(cell);
            //@@
            }else{
            	chk = new Chunk("",FontFactory.getFont("ARIAL", 7, Font.BOLD,Color.BLACK));
                cell = new Cell(chk);
                cell.setLeading(8.0f);
                cell.setNoWrap(true);
                cell.setVerticalAlignment(cell.ALIGN_CENTER);
                cell.setBorderWidth(0);
                //cell.setColspan(3);
                part.addCell(cell);


                chk = new Chunk("",FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
                cell = new Cell(chk);
                cell.setLeading(8.0f);
                cell.setNoWrap(true);
                //cell.setBorder(0);
                cell.setBorderWidth(0);
                cell.setBorderColor(Color.GRAY);
                cell.setHorizontalAlignment(cell.ALIGN_LEFT);
                cell.setVerticalAlignment(cell.ALIGN_CENTER);
                part.addCell(cell);

                //@@One Empty Row
                chk = new Chunk("");//@@,FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK)
                cell = new Cell(chk);
                cell.setBorderWidth(0);
                cell.setLeading(5);
                cell.setColspan(8);
                part.addCell(cell);	
            }

            /*chk = new Chunk("",FontFactory.getFont("ARIAL", 8, Font.BOLDITALIC,Color.BLACK));
            cell = new Cell(chk);
            cell.setBorderWidth(0);
            part.addCell(cell);*/

           /* chk = new Chunk("Base Currency",FontFactory.getFont("ARIAL", 8, Font.BOLDITALIC,Color.BLACK));
            cell = new Cell(chk);cell.setBorderWidth(0);
            cell.setNoWrap(true);
            cell.setColspan(2);
            part.addCell(cell);


            chk = new Chunk(costingHDRDOB.getBaseCurrency(),FontFactory.getFont("ARIAL", 8, Font.BOLD,Color.BLACK));
            cell = new Cell(chk);
            cell.setNoWrap(true);
            //cell.setBorder(0);
            cell.setBorderWidth(1);
            cell.setBackgroundColor(Color.ORANGE);
            cell.setHorizontalAlignment(cell.ALIGN_RIGHT);
            cell.setVerticalAlignment(cell.ALIGN_BOTTOM);
            part.addCell(cell);

            chk = new Chunk("",FontFactory.getFont("ARIAL", 8, Font.BOLDITALIC,Color.BLACK));
            cell = new Cell(chk);
            cell.setBorderWidth(0);
            part.addCell(cell);*/
            if(frtListSize>0)
            {
                chk = new Chunk("Leg",FontFactory.getFont("ARIAL", 7, Font.BOLD,Color.BLACK));
                cell = new Cell(chk);
                cell.setNoWrap(true);
                cell.setLeading(8.0f);
                cell.setBorderWidth(0);
                cell.setVerticalAlignment(cell.ALIGN_CENTER);
                //cell.setColspan(3);
                part.addCell(cell);

                legDetails = (CostingLegDetailsDOB)costingMasterDOB.getCostingLegDetailsList().get(0);

                chk = new Chunk(legDetails.getOrigin()+"-"+legDetails.getDestination(),FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
                cell = new Cell(chk);
                cell.setLeading(8.0f);
                cell.setNoWrap(true);
                cell.setBorderWidth(1);
                cell.setBorderColor(Color.GRAY);
                cell.setHorizontalAlignment(cell.ALIGN_LEFT);
                cell.setVerticalAlignment(cell.ALIGN_CENTER);
                part.addCell(cell);

                chk = new Chunk("",FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
                cell = new Cell(chk);
                cell.setLeading(8.0f);
                cell.setBorderWidth(0);
                part.addCell(cell);

                
                if(costingMasterDOB.getFrequencyCheck()!=null && "Y".equalsIgnoreCase(costingMasterDOB.getFrequencyCheck())){
                chk = new Chunk("Frequency",FontFactory.getFont("ARIAL", 7, Font.BOLD,Color.BLACK));
                cell = new Cell(chk);
                cell.setLeading(8.0f);
                cell.setNoWrap(true);
                cell.setVerticalAlignment(cell.ALIGN_CENTER);
                cell.setBorderWidth(0);
                //cell.setColspan(3);
                part.addCell(cell);

                chk = new Chunk(legDetails.getFrequency(),FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
                cell = new Cell(chk);
                cell.setLeading(8.0f);
                cell.setNoWrap(true);
                cell.setBorderWidth(1);
                cell.setBorderColor(Color.GRAY);
                cell.setHorizontalAlignment(cell.ALIGN_LEFT);
                cell.setVerticalAlignment(cell.ALIGN_CENTER);
                part.addCell(cell);

                chk = new Chunk("",FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
                cell = new Cell(chk);
                cell.setLeading(8.0f);
                cell.setBorderWidth(0);
                part.addCell(cell);
                }else{
                	chk = new Chunk("",FontFactory.getFont("ARIAL", 7, Font.BOLD,Color.BLACK));
                    cell = new Cell(chk);
                    cell.setLeading(8.0f);
                    cell.setNoWrap(true);
                    cell.setVerticalAlignment(cell.ALIGN_CENTER);
                    cell.setBorderWidth(0);
                    //cell.setColspan(3);
                    part.addCell(cell);

                    chk = new Chunk("",FontFactory.getFont("ARIAL", 7, Font.BOLD,Color.BLACK));
                    cell = new Cell(chk);
                    cell.setLeading(8.0f);
                    cell.setNoWrap(true);
                    cell.setBorderWidth(0);
                    cell.setBorderColor(Color.GRAY);
                    cell.setHorizontalAlignment(cell.ALIGN_LEFT);
                    cell.setVerticalAlignment(cell.ALIGN_CENTER);
                    part.addCell(cell);

                    chk = new Chunk("",FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
                    cell = new Cell(chk);
                    cell.setLeading(8.0f);
                    cell.setBorderWidth(0);
                    part.addCell(cell);	
                }
                
                
                if(costingMasterDOB.getTransittimeCheck()!=null && "Y".equalsIgnoreCase(costingMasterDOB.getTransittimeCheck())){
                                  
                    chk = new Chunk("Transit Time",FontFactory.getFont("ARIAL", 7, Font.BOLD,Color.BLACK));
                    cell = new Cell(chk);
                    cell.setLeading(8.0f);
                    cell.setNoWrap(true);
                    cell.setBorderWidth(0);
                    cell.setVerticalAlignment(cell.ALIGN_CENTER);
                    //cell.setColspan(3);
                    part.addCell(cell);

                    chk = new Chunk(legDetails.getTransitTime(),FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
                    cell = new Cell(chk);
                    cell.setLeading(8.0f);
                    cell.setNoWrap(true);
                    cell.setBorderWidth(1);
                    cell.setBorderColor(Color.GRAY);
                    cell.setHorizontalAlignment(cell.ALIGN_LEFT);
                    cell.setVerticalAlignment(cell.ALIGN_CENTER);
                    part.addCell(cell);

                    //@@One Empty Row
                    chk = new Chunk("");//@@,FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK)
                    cell = new Cell(chk);
                    cell.setBorderWidth(0);
                    cell.setLeading(5);
                    cell.setColspan(8);
                    part.addCell(cell);
                    //@@**/
                    
                	}else{
                		chk = new Chunk("",FontFactory.getFont("ARIAL", 7, Font.BOLD,Color.BLACK));
                        cell = new Cell(chk);
                        cell.setLeading(8.0f);
                        cell.setNoWrap(true);
                        cell.setBorderWidth(0);
                        cell.setVerticalAlignment(cell.ALIGN_CENTER);
                        //cell.setColspan(3);
                        part.addCell(cell);

                        chk = new Chunk("",FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
                        cell = new Cell(chk);
                        cell.setLeading(8.0f);
                        cell.setNoWrap(true);
                        cell.setBorderWidth(0);
                        cell.setBorderColor(Color.GRAY);
                        cell.setHorizontalAlignment(cell.ALIGN_LEFT);
                        cell.setVerticalAlignment(cell.ALIGN_CENTER);
                        part.addCell(cell);

                        //@@One Empty Row
                        chk = new Chunk("");//@@,FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK)
                        cell = new Cell(chk);
                        cell.setBorderWidth(0);
                        cell.setLeading(5);
                        cell.setColspan(8);
                        part.addCell(cell);
                        //@@**/	
                    }
                
                //Commented by Rakesh on 24-01-2011 for CR:231219
                /**
                chk = new Chunk("Incoterms",FontFactory.getFont("ARIAL", 7, Font.BOLD,Color.BLACK));
                cell = new Cell(chk);
                cell.setLeading(8.0f);
                cell.setNoWrap(true);
                cell.setBorderWidth(0);
                cell.setVerticalAlignment(cell.ALIGN_CENTER);
                //cell.setColspan(3);
                part.addCell(cell);

                chk = new Chunk(costingHDRDOB.getIncoterms(),FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
                cell = new Cell(chk);
                cell.setLeading(8.0f);
                cell.setNoWrap(true);
                cell.setBorderWidth(1);
                cell.setBorderColor(Color.GRAY);
                cell.setHorizontalAlignment(cell.ALIGN_LEFT);
                cell.setVerticalAlignment(cell.ALIGN_CENTER);
                part.addCell(cell);

                //@@One Empty Row
                chk = new Chunk("");//@@,FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK)
                cell = new Cell(chk);
                cell.setBorderWidth(0);
                cell.setLeading(5);
                cell.setColspan(8);
                part.addCell(cell);
                //@@**/ //Comment ended by Rakesh on 24-01-2011 for CR:231219

                for (int i=1;i<frtListSize;i++)
                {
                    chk = new Chunk("",FontFactory.getFont("ARIAL", 7, Font.BOLD,Color.BLACK));
                    cell = new Cell(chk);
                    cell.setLeading(8.0f);
                    cell.setNoWrap(true);
                    cell.setBorderWidth(0);
                    part.addCell(cell);

                    legDetails = (CostingLegDetailsDOB)costingMasterDOB.getCostingLegDetailsList().get(i);

                    chk = new Chunk(legDetails.getOrigin()+"-"+legDetails.getDestination(),FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
                    cell = new Cell(chk);
                    cell.setLeading(8.0f);
                    cell.setNoWrap(true);
                    cell.setBorderWidth(1);
                    cell.setBorderColor(Color.GRAY);
                    cell.setHorizontalAlignment(cell.ALIGN_LEFT);
                    cell.setVerticalAlignment(cell.ALIGN_CENTER);
                    part.addCell(cell);

                    chk = new Chunk("",FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
                    cell = new Cell(chk);
                    cell.setLeading(8.0f);
                    cell.setBorderWidth(0);
                    cell.setColspan(2);
                    part.addCell(cell);


                    chk = new Chunk(legDetails.getFrequency(),FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
                    cell = new Cell(chk);
                    cell.setLeading(8.0f);
                    cell.setNoWrap(true);
                    cell.setBorderWidth(1);
                    cell.setBorderColor(Color.GRAY);
                    cell.setHorizontalAlignment(cell.ALIGN_LEFT);
                    cell.setVerticalAlignment(cell.ALIGN_CENTER);
                    part.addCell(cell);

                    chk = new Chunk("",FontFactory.getFont("ARIAL", 7, Font.BOLD,Color.BLACK));
                    cell = new Cell(chk);
                    cell.setLeading(8.0f);
                    cell.setNoWrap(true);
                    cell.setBorderWidth(0);
                    cell.setColspan(3);
                    part.addCell(cell);

                   //@@One Empty Row
                  chk = new Chunk("");//@@,FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK)
                  cell = new Cell(chk);
                  cell.setBorderWidth(0);
                  cell.setLeading(5);
                  cell.setColspan(8);
                  part.addCell(cell);
                  //@@
                }
            }

            chk = new Chunk("Base Currency",FontFactory.getFont("ARIAL", 7, Font.BOLD,Color.BLACK));
            cell = new Cell(chk);
            cell.setLeading(8.0f);
            cell.setNoWrap(true);
            cell.setBorderWidth(0);
            cell.setVerticalAlignment(cell.ALIGN_CENTER);
            part.addCell(cell);

            chk = new Chunk(costingHDRDOB.getBaseCurrency(),FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
            cell = new Cell(chk);
            cell.setLeading(8.0f);
            cell.setNoWrap(true);
            cell.setBorderWidth(1);
            cell.setBorderColor(Color.GRAY);
            cell.setHorizontalAlignment(cell.ALIGN_LEFT);
            cell.setVerticalAlignment(cell.ALIGN_CENTER);
            part.addCell(cell);

            chk = new Chunk("",FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
            cell = new Cell(chk);
            cell.setLeading(8.0f);
            cell.setBorderWidth(0);
            part.addCell(cell);

            if(costingMasterDOB.getTransittimeCheck()!=null && "Y".equalsIgnoreCase(costingMasterDOB.getTransittimeCheck())){
            chk = new Chunk("Commodity",FontFactory.getFont("ARIAL", 7, Font.BOLD,Color.BLACK));
            cell = new Cell(chk);
            cell.setNoWrap(true);
            cell.setLeading(8.0f);
            cell.setBorderWidth(0);
            cell.setVerticalAlignment(cell.ALIGN_CENTER);
            //cell.setColspan(3);
            part.addCell(cell);

            //Modified by Rakesh on 24-01-2011 for CR:231219
            chk = new Chunk((costingHDRDOB.getCommodityType()!=null)?costingHDRDOB.getCommodityType():"",FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
            cell = new Cell(chk);
            cell.setLeading(8.0f);
            cell.setNoWrap(true);
            cell.setBorderWidth(1);
            //cell.setColspan(4);
            cell.setBorderColor(Color.GRAY);
            cell.setHorizontalAlignment(cell.ALIGN_LEFT);
            cell.setVerticalAlignment(cell.ALIGN_CENTER);
            part.addCell(cell);

            //Added by Rakesh on 24-01-2011 for CR:231219
            chk = new Chunk("",FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
            cell = new Cell(chk);
            cell.setLeading(8.0f);
            cell.setBorderWidth(0);
            part.addCell(cell);
            }else{
            	chk = new Chunk("",FontFactory.getFont("ARIAL", 7, Font.BOLD,Color.BLACK));
                cell = new Cell(chk);
                cell.setNoWrap(true);
                cell.setLeading(8.0f);
                cell.setBorderWidth(0);
                cell.setVerticalAlignment(cell.ALIGN_CENTER);
                //cell.setColspan(3);
                part.addCell(cell);
                
                //Modified by Rakesh on 24-01-2011 for CR:231219
                chk = new Chunk("",FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
                cell = new Cell(chk);
                cell.setLeading(8.0f);
                cell.setNoWrap(true);
                cell.setBorderWidth(0);
                //cell.setColspan(4);
                cell.setBorderColor(Color.GRAY);
                cell.setHorizontalAlignment(cell.ALIGN_LEFT);
                cell.setVerticalAlignment(cell.ALIGN_CENTER);
                part.addCell(cell);
               
                //Added by Rakesh on 24-01-2011 for CR:231219
                chk = new Chunk("",FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
                cell = new Cell(chk);
                cell.setLeading(8.0f);
                cell.setBorderWidth(0);
                part.addCell(cell);	
            }
            //Added by Rakesh on 24-01-2011 for CR:231219
            chk = new Chunk("Incoterms",FontFactory.getFont("ARIAL", 7, Font.BOLD,Color.BLACK));
            cell = new Cell(chk);
            cell.setLeading(8.0f);
            cell.setNoWrap(true);
            cell.setBorderWidth(0);
            cell.setVerticalAlignment(cell.ALIGN_CENTER);
            //cell.setColspan(3);
            part.addCell(cell);

            chk = new Chunk(costingHDRDOB.getIncoterms(),FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
            cell = new Cell(chk);
            cell.setLeading(8.0f);
            cell.setNoWrap(true);
            cell.setBorderWidth(1);
            cell.setBorderColor(Color.GRAY);
            cell.setHorizontalAlignment(cell.ALIGN_LEFT);
            cell.setVerticalAlignment(cell.ALIGN_CENTER);
            part.addCell(cell);

            //@@One Empty Row
            chk = new Chunk("");//@@,FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK)
            cell = new Cell(chk);
            cell.setBorderWidth(0);
            cell.setLeading(5);
            cell.setColspan(8);
            part.addCell(cell);
            //@@
            //Ended by Rakesh on 24-01-2011 for CR:231219
          //Added by Rakesh on 21-02-2011
            if(costingMasterDOB.getCarrierCheck()!=null && "Y".equalsIgnoreCase(costingMasterDOB.getCarrierCheck())){
            chk = new Chunk("Carrier",FontFactory.getFont("ARIAL", 7, Font.BOLD,Color.BLACK));
            cell = new Cell(chk);
            cell.setLeading(8.0f);
            cell.setNoWrap(true);
            cell.setBorderWidth(0);
            cell.setVerticalAlignment(cell.ALIGN_CENTER);
            part.addCell(cell);

            chk = new Chunk(costingHDRDOB.getCarrier()!=null ? costingHDRDOB.getCarrier():"" ,FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
            cell = new Cell(chk);
            cell.setLeading(8.0f);
            cell.setNoWrap(true);
            cell.setBorderWidth(1);
            cell.setBorderColor(Color.GRAY);
            cell.setHorizontalAlignment(cell.ALIGN_LEFT);
            cell.setVerticalAlignment(cell.ALIGN_CENTER);
            part.addCell(cell);

            chk = new Chunk("",FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
            cell = new Cell(chk);
            cell.setLeading(8.0f);
            cell.setBorderWidth(0);
            part.addCell(cell);
            }else{
            	chk = new Chunk("",FontFactory.getFont("ARIAL", 7, Font.BOLD,Color.BLACK));
                cell = new Cell(chk);
                cell.setLeading(8.0f);
                cell.setNoWrap(true);
                cell.setBorderWidth(0);
                cell.setVerticalAlignment(cell.ALIGN_CENTER);
                part.addCell(cell);

                chk = new Chunk("",FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
                cell = new Cell(chk);
                cell.setLeading(8.0f);
                cell.setNoWrap(true);
                cell.setBorderWidth(0);
                cell.setBorderColor(Color.GRAY);
                cell.setHorizontalAlignment(cell.ALIGN_LEFT);
                cell.setVerticalAlignment(cell.ALIGN_CENTER);
                part.addCell(cell);

                chk = new Chunk("",FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
                cell = new Cell(chk);
                cell.setLeading(8.0f);
                cell.setBorderWidth(0);
                part.addCell(cell);//Ended by Rakesh
                }
            if(costingMasterDOB.getServiceLevelCheck()!=null && "Y".equalsIgnoreCase(costingMasterDOB.getServiceLevelCheck())){
                chk = new Chunk("Service Level",FontFactory.getFont("ARIAL", 7, Font.BOLD,Color.BLACK));
                cell = new Cell(chk);
                cell.setLeading(8.0f);
                cell.setNoWrap(true);
                cell.setBorderWidth(0);
                cell.setVerticalAlignment(cell.ALIGN_CENTER);
                part.addCell(cell);

                chk = new Chunk(costingHDRDOB.getServiceLevel(),FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
                cell = new Cell(chk);
                cell.setLeading(8.0f);
                cell.setNoWrap(true);
                cell.setBorderWidth(1);
                cell.setBorderColor(Color.GRAY);
                cell.setHorizontalAlignment(cell.ALIGN_LEFT);
                cell.setVerticalAlignment(cell.ALIGN_CENTER);
                part.addCell(cell);

                chk = new Chunk("",FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
                cell = new Cell(chk);
                cell.setLeading(8.0f);
                cell.setBorderWidth(0);
                part.addCell(cell);//Ended by Rakesh
                }else{
                	chk = new Chunk("",FontFactory.getFont("ARIAL", 7, Font.BOLD,Color.BLACK));
                    cell = new Cell(chk);
                    cell.setLeading(8.0f);
                    cell.setNoWrap(true);
                    cell.setBorderWidth(0);
                    cell.setVerticalAlignment(cell.ALIGN_CENTER);
                    part.addCell(cell);

                    chk = new Chunk("",FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
                    cell = new Cell(chk);
                    cell.setLeading(8.0f);
                    cell.setNoWrap(true);
                    cell.setBorderWidth(0);
                    cell.setBorderColor(Color.GRAY);
                    cell.setHorizontalAlignment(cell.ALIGN_LEFT);
                    cell.setVerticalAlignment(cell.ALIGN_CENTER);
                    part.addCell(cell);

                    chk = new Chunk("",FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
                    cell = new Cell(chk);
                    cell.setLeading(8.0f);
                    cell.setBorderWidth(0);
                    part.addCell(cell);//Ended by Rakesh
                }
            document.add(part);
            //commented by Rakesh on 25-01-2011 for CR:231219 
            /**
            part  =  new Table(1);
            part.setOffset(8);
            part.setWidth(100);
            part.setPadding(1);
            part.setSpacing(0);
            part.setBackgroundColor(Color.WHITE);
            part.setBorderColor(Color.black);
            part.setBorderWidth(0);

            chk = new Chunk("Notes",FontFactory.getFont("ARIAL", 7, Font.BOLD,Color.BLACK));
            cell = new Cell(chk);
            //cell.setBackgroundColor(DHLColor);
            cell.setHorizontalAlignment(cell.ALIGN_LEFT);
            cell.setVerticalAlignment(cell.ALIGN_BOTTOM);
            cell.setBorderWidth(0);
            cell.setNoWrap(true);
            part.addCell(cell);

            chk = new Chunk(costingHDRDOB.getNotes()!=null?costingHDRDOB.getNotes():"",FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
            cell = new Cell(chk);
            //cell.setBackgroundColor(DHLColor);
            cell.setBorderWidth(1);
            cell.setBorderColor(Color.GRAY);
            //cell.setNoWrap(true);
            part.addCell(cell);

            document.add(part); **/

			  /* if(frtListSize>0)
				 {
            part  =  new Table(11);
            part.setOffset(8);
            part.setWidth(100);
            part.setPadding(1);
            //part.setCellsFitPage(true);
            part.setSpacing(1);
            part.setBackgroundColor(Color.WHITE);
            part.setBorderColor(Color.black);
            part.setBorderWidth(0);

            chk = new Chunk("Incoterms",FontFactory.getFont("ARIAL", 8, Font.ITALIC,Color.BLACK));
            cell = new Cell(chk);
            cell.setNoWrap(true);
            cell.setBorderWidth(0);
            cell.setColspan(2);
            part.addCell(cell);

            chk = new Chunk(costingHDRDOB.getIncoterms(),FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
            cell = new Cell(chk);
            cell.setNoWrap(true);
            cell.setBorderWidth(1);
            //cell.setBackgroundColor(Color.lightGray);
            part.addCell(cell);

            /*chk = new Chunk("",FontFactory.getFont("ARIAL", 8, Font.BOLDITALIC,Color.BLACK));
            cell = new Cell(chk);
            cell.setBorderWidth(0);
            part.addCell(cell);*/

           /* chk = new Chunk("Commodity",FontFactory.getFont("ARIAL", 8, Font.ITALIC,Color.BLACK));
            cell = new Cell(chk);
            cell.setBorderWidth(0);
            cell.setColspan(2);
            cell.setHorizontalAlignment(cell.ALIGN_CENTER);
            cell.setVerticalAlignment(cell.ALIGN_BOTTOM);
            cell.setNoWrap(true);
            part.addCell(cell);

            chk = new Chunk((costingHDRDOB.getCommodityType()!=null)?costingHDRDOB.getCommodityType():"",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
            cell = new Cell(chk);
            cell.setNoWrap(true);
            cell.setBorderWidth(1);
            //cell.setBackgroundColor(Color.lightGray);
            cell.setColspan(6);
            part.addCell(cell);

            document.add(part);

            part  =  new Table(3);
            part.setOffset(8);
            part.setWidth(100);
            part.setPadding(1);
            part.setSpacing(0);
            part.setBackgroundColor(Color.WHITE);
            part.setBorderColor(Color.black);
            part.setBorderWidth(1);

            chk = new Chunk("Leg",FontFactory.getFont("ARIAL", 8, Font.ITALIC,Color.BLACK));
            cell = new Cell(chk);

            cell.setHorizontalAlignment(cell.ALIGN_CENTER);
            cell.setVerticalAlignment(cell.ALIGN_BOTTOM);
            cell.setNoWrap(true);
            cell.setHeader(true);
            part.addCell(cell);

            chk = new Chunk("Transit Time ",FontFactory.getFont("ARIAL", 8, Font.ITALIC,Color.BLACK));
            cell = new Cell(chk);

            cell.setHorizontalAlignment(cell.ALIGN_CENTER);
            cell.setVerticalAlignment(cell.ALIGN_BOTTOM);
            cell.setNoWrap(true);
            cell.setHeader(true);
            part.addCell(cell);

            chk = new Chunk("Frequency",FontFactory.getFont("ARIAL", 8, Font.ITALIC,Color.BLACK));
            cell = new Cell(chk);
            cell.setNoWrap(true);

            cell.setHorizontalAlignment(cell.ALIGN_CENTER);
            cell.setVerticalAlignment(cell.ALIGN_BOTTOM);
            cell.setHeader(true);
            part.addCell(cell);

            for (int i=0;i<frtListSize;i++)
            {
              legDetails = (CostingLegDetailsDOB)costingMasterDOB.getCostingLegDetailsList().get(i);
              chk = new Chunk(legDetails.getOrigin()+"-"+legDetails.getDestination(),FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
              cell = new Cell(chk);
              cell.setHorizontalAlignment(cell.ALIGN_CENTER);
              cell.setVerticalAlignment(cell.ALIGN_BOTTOM);
              cell.setNoWrap(true);
              //cell.setBackgroundColor(Color.lightGray);
              part.addCell(cell);

              chk = new Chunk(legDetails.getTransitTime(),FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
              cell = new Cell(chk);
              cell.setHorizontalAlignment(cell.ALIGN_CENTER);
              cell.setVerticalAlignment(cell.ALIGN_BOTTOM);
              cell.setNoWrap(true); //cell.setBackgroundColor(Color.lightGray);
              part.addCell(cell);

              chk = new Chunk(legDetails.getFrequency(),FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
              cell = new Cell(chk);//cell.setBackgroundColor(Color.lightGray);
              cell.setHorizontalAlignment(cell.ALIGN_CENTER);
              cell.setVerticalAlignment(cell.ALIGN_BOTTOM);
              cell.setNoWrap(true);
              part.addCell(cell);
            }
            document.add(part);
        }*/

            /*part  =  new Table(2);
            part.setOffset(8);
            part.setWidth(100);
            part.setPadding(1);
            part.setSpacing(0);
            part.setBackgroundColor(Color.WHITE);
            part.setBorderColor(Color.black);
            part.setBorderWidth(0);

              chk = new Chunk("Total Cost, based on below details =  ",FontFactory.getFont("ARIAL", 12, Font.BOLD,Color.BLACK));
              cell = new Cell(chk);
              cell.setBackgroundColor(Color.WHITE);
              cell.setHorizontalAlignment(cell.ALIGN_CENTER);
              cell.setVerticalAlignment(cell.ALIGN_BOTTOM);
              cell.setBorderWidth(0);
              cell.setNoWrap(true);
              part.addCell(cell);

              chk = new Chunk(((costingHDRDOB.getBaseCurrency()!=null)?costingHDRDOB.getBaseCurrency():"")+" "+costingMasterDOB.getTotal(),FontFactory.getFont("ARIAL", 12, Font.BOLD,Color.BLACK));
              cell = new Cell(chk);
              cell.setNoWrap(true);
              cell.setBorderWidth(0);
              part.addCell(cell);

            document.add(part);*/

           // int[] widhts = {20,9,9,19,7,8,11,2,13};
            //int[] widhts = {30,9,9,19,7,8,11,2,13};//@@Modified by Kameswari for Surcharge Enhancements
            int[] widhts = {35,7,9,22,7,8,9,2,10};//@@ Modified by subrahmanyam for the Wpbn issue:146447
            //part  =  new Table(8);
            part  =  new Table(9);
            part.setOffset(8);
            part.setWidth(100);
            part.setWidths(widhts);
            part.setPadding(1);
            part.setSpacing(0);
            part.setBackgroundColor(Color.WHITE);
            part.setBorderColor(Color.BLACK);
            part.setCellsFitPage(true);
            //part.setTableFitsPage(true);
            part.setBorderWidth(1);

            chk = new Chunk("Charge",FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
            cell = new Cell(chk);
            cell.setHorizontalAlignment(cell.ALIGN_LEFT);
            cell.setVerticalAlignment(cell.ALIGN_BOTTOM);
            cell.setNoWrap(false);
            //cell.setWidth("20");
            cell.setLeading(8.0f);
            cell.setBorderWidth(1);
            cell.setBorderColor(Color.BLACK);
            cell.setBackgroundColor(DHLColor);
            part.addCell(cell);

            /*chk = new Chunk("BREAK POINT",FontFactory.getFont("ARIAL", 7, Font.BOLD,Color.WHITE));
            cell = new Cell(chk);
            cell.setHorizontalAlignment(cell.ALIGN_CENTER);
            cell.setVerticalAlignment(cell.ALIGN_BOTTOM);
            cell.setBackgroundColor(Color.red);
            //cell.setNoWrap(true);
            //cell.setWidth("12");
            cell.setBorderWidth(0);
            part.addCell(cell);*/

            chk = new Chunk("Currency",FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
            cell = new Cell(chk);
            cell.setBorderWidth(1);
            cell.setBorderColor(Color.BLACK);
            cell.setBackgroundColor(DHLColor);
            cell.setHorizontalAlignment(cell.ALIGN_LEFT);
            cell.setVerticalAlignment(cell.ALIGN_BOTTOM);
            cell.setNoWrap(true);
            cell.setLeading(8.0f);
            //cell.setWidth("8");
            part.addCell(cell);

            chk = new Chunk("Rate",FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
            cell = new Cell(chk);
            cell.setBorderWidth(1);
            cell.setBorderColor(Color.BLACK);
            cell.setBackgroundColor(DHLColor);
            cell.setHorizontalAlignment(cell.ALIGN_LEFT);
            cell.setVerticalAlignment(cell.ALIGN_BOTTOM);
            cell.setNoWrap(false);
            cell.setLeading(8.0f);
            //cell.setWidth("8");
            part.addCell(cell);

            chk = new Chunk("Basis",FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
            cell = new Cell(chk);
            cell.setHorizontalAlignment(cell.ALIGN_LEFT);
            cell.setVerticalAlignment(cell.ALIGN_BOTTOM);
            cell.setBackgroundColor(DHLColor);
            //cell.setWidth("12");
            cell.setBorderWidth(1);
            cell.setLeading(8.0f);
            cell.setBorderColor(Color.BLACK);
            part.addCell(cell);

            chk = new Chunk("Ratio",FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
            cell = new Cell(chk);
            cell.setBackgroundColor(DHLColor);
            cell.setLeading(8.0f);
            //cell.setWidth("8");
            cell.setHorizontalAlignment(cell.ALIGN_LEFT);
            cell.setVerticalAlignment(cell.ALIGN_BOTTOM);
            cell.setNoWrap(false);
            cell.setBorderWidth(1);
            cell.setBorderColor(Color.BLACK);
            part.addCell(cell);

            chk = new Chunk("Primary Unit",FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
            cell = new Cell(chk);
            cell.setBackgroundColor(DHLColor);
            cell.setLeading(8.0f);
            cell.setBorderWidth(1);
            cell.setBorderColor(Color.BLACK);
            cell.setHorizontalAlignment(cell.ALIGN_LEFT);
            cell.setVerticalAlignment(cell.ALIGN_BOTTOM);
            //cell.setNoWrap(false);
            //cell.setWidth("8");
            part.addCell(cell);


            chk = new Chunk("Secondary Unit",FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
            cell = new Cell(chk);
            cell.setBackgroundColor(DHLColor);
            cell.setLeading(8.0f);
            cell.setHorizontalAlignment(cell.ALIGN_LEFT);
            cell.setVerticalAlignment(cell.ALIGN_BOTTOM);
            //cell.setNoWrap(false);
            //cell.setWidth("8");
            cell.setBorderWidth(1);
            cell.setBorderColor(Color.BLACK);
            part.addCell(cell);

            chk = new Chunk("",FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
            cell = new Cell(chk);
            cell.setBorderWidth(1);
            cell.setLeading(8.0f);
            cell.setBorderColor(Color.BLACK);
            cell.setBackgroundColor(DHLColor);
            cell.setHorizontalAlignment(cell.ALIGN_LEFT);
            cell.setVerticalAlignment(cell.ALIGN_BOTTOM);
            cell.setNoWrap(true);
            //cell.setWidth("2");
            part.addCell(cell);

            chk = new Chunk("Total(Base Currency)",FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
            cell = new Cell(chk);
            cell.setBackgroundColor(DHLColor);
            cell.setLeading(8.0f);
            cell.setBorderWidth(1);
            cell.setBorderColor(Color.BLACK);
            cell.setHorizontalAlignment(cell.ALIGN_LEFT);
            cell.setVerticalAlignment(cell.ALIGN_BOTTOM);
            //cell.setNoWrap(false);
            //cell.setWidth("8");
            part.addCell(cell);
            part.endHeaders();
            String[] primaryUnitArray     = null;
            String[] secUnitArray         = null;
            String   percentFlag          = "";
            ArrayList detailsList = null;
            if(originListSize>0)
            {
                  primaryUnitArray  = ((CostingChargeDetailsDOB)costingMasterDOB.getOriginList().get(0)).getPrimaryUnitArray();
                  for(int i=0;i<originListSize;i++)
                  {

                    detailsDOB = (CostingChargeDetailsDOB)costingMasterDOB.getOriginList().get(i);
                    rateDetails=(ArrayList)detailsDOB.getCostingRateInfoDOB();
                   //@@Modified by kameswari for Surcharge Enhancements
                    /* detailsList = (ArrayList)costingMasterDOB.getOriginList().get(i);
                     detailsDOB = (CostingChargeDetailsDOB)detailsList.get(0);*/

                    rateDetails=(ArrayList)detailsDOB.getCostingRateInfoDOB();
                     if("checked".equals(detailsDOB.getChecked()))
                     {
                            //chk = new Chunk(detailsDOB.getChargeDescId()==null?"":detailsDOB.getChargeDescId().trim(),FontFactory.getFont("ARIAL", 9, Font.NORMAL,Color.BLACK));
                            isOriginChecked = true;
                            if(!"List".equalsIgnoreCase(detailsDOB.getWeightBreak()))
                            {
                              chk = new Chunk(detailsDOB.getExternalName()!=null?detailsDOB.getExternalName():"",FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
                              cell = new Cell(chk);
                              cell.setBackgroundColor(Color.WHITE);
                              cell.setNoWrap(false);
                              cell.setBorderWidth(1);
                              cell.setBorderColor(Color.GRAY);
                              cell.setLeading(9.0f);
                              cell.setHorizontalAlignment(cell.ALIGN_LEFT);
                              cell.setVerticalAlignment(cell.ALIGN_BASELINE);
                              //cell.setBackgroundColor(Color.lightGray);
                              //cell.setWidth("20");
                              part.addCell(cell);

                              /*chk = new Chunk((detailsDOB.getBrkPoint()!=null && !"Absolute".equalsIgnoreCase(detailsDOB.getBrkPoint()) && !"Percent".equalsIgnoreCase(detailsDOB.getBrkPoint()))?detailsDOB.getBrkPoint():"",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
                              cell = new Cell(chk);
                              cell.setNoWrap(true);
                              cell.setLeading(9.0f);
                              cell.setHorizontalAlignment(cell.ALIGN_CENTER);
                              cell.setVerticalAlignment(cell.ALIGN_BASELINE);
                              cell.setBorderWidth(1);
                              //cell.setWidth("12");
                              part.addCell(cell);*/

                              chk = new Chunk((detailsDOB.getCurrency()!=null)?detailsDOB.getCurrency():"",FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
                              cell = new Cell(chk);
                              cell.setLeading(9.0f);
                              cell.setHorizontalAlignment(cell.ALIGN_LEFT);
                              cell.setVerticalAlignment(cell.ALIGN_BASELINE);
                              cell.setBackgroundColor(Color.WHITE);
                              cell.setNoWrap(true);
                              cell.setBorderWidth(1);
                              cell.setBorderColor(Color.GRAY);
                              //cell.setWidth("8");
                              part.addCell(cell);

                              if("Percent".equals(detailsDOB.getWeightBreak()) || "Absolute".equals(detailsDOB.getWeightBreak()))
                              {
                                  rateDetailsDOB =  (CostingRateInfoDOB)rateDetails.get(0);
                                  chk = new Chunk(deciFormat.format(rateDetailsDOB.getRate())+("Percent".equalsIgnoreCase(detailsDOB.getWeightBreak())?"%":""),FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
                                  cell = new Cell(chk);
                                  cell.setLeading(9.0f);
                                  //cell.setBackgroundColor(Color.lightGray);
                                  cell.setHorizontalAlignment(cell.ALIGN_LEFT);
                                  cell.setVerticalAlignment(cell.ALIGN_BASELINE);
                                  //cell.setWidth("8");
                                  cell.setBorderWidth(1);
                                  cell.setBorderColor(Color.GRAY);
                                  cell.setNoWrap(true);
                                  part.addCell(cell);
                              }
                              else
                              {
                                  if(detailsDOB.getWeightBreak()!=null && detailsDOB.getWeightBreak().endsWith("%") && !"Per Shipment".equalsIgnoreCase(detailsDOB.getChargeBasis()))
                                    percentFlag = "%";
                                  else
                                    percentFlag = "";

                                  chk = new Chunk(deciFormat.format(Double.parseDouble(detailsDOB.getRate()!=null?detailsDOB.getRate():"0"))+percentFlag,FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
                                  cell = new Cell(chk);
                                  cell.setLeading(9.0f);
                                  //cell.setBackgroundColor(Color.lightGray);
                                  cell.setBorderWidth(1);
                                  cell.setBorderColor(Color.GRAY);
                                  cell.setHorizontalAlignment(cell.ALIGN_LEFT);
                                  cell.setVerticalAlignment(cell.ALIGN_BASELINE);
                                  cell.setNoWrap(true);
                                  //cell.setWidth("8");

                                  part.addCell(cell);
                              }
                              chk = new Chunk(detailsDOB.getChargeBasis()!=null?detailsDOB.getChargeBasis():"",FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
                              cell = new Cell(chk);
                              //cell.setLeading(9.0f);
                              //cell.setBackgroundColor(Color.lightGray);
                              cell.setHorizontalAlignment(cell.ALIGN_LEFT);
                              cell.setVerticalAlignment(cell.ALIGN_BASELINE);
                              cell.setBorderWidth(1);
                              cell.setBorderColor(Color.GRAY);
                              cell.setLeading(9.0f);


                              //cell.setWidth("12");

                              part.addCell(cell);


                              chk = new Chunk((detailsDOB.getDensityRatio()!=null)?detailsDOB.getDensityRatio():"",FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
                              cell = new Cell(chk);
                              cell.setLeading(9.0f);
                              cell.setBackgroundColor(Color.WHITE);
                              cell.setHorizontalAlignment(cell.ALIGN_LEFT);
                              cell.setVerticalAlignment(cell.ALIGN_BASELINE);
                              //cell.setWidth("8");
                              cell.setBorderWidth(1);
                              cell.setBorderColor(Color.GRAY);
                              cell.setNoWrap(true);
                              part.addCell(cell);

                              chk = new Chunk((detailsDOB.getPrimaryUnitValue()!=null && detailsDOB.getPrimaryUnitValue().trim().length()!=0)?deciFormat.format(Double.parseDouble(detailsDOB.getPrimaryUnitValue())):"",FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
                              cell = new Cell(chk);
                              cell.setBackgroundColor(Color.WHITE);
                              cell.setHorizontalAlignment(cell.ALIGN_LEFT);
                              cell.setVerticalAlignment(cell.ALIGN_BASELINE);
                              cell.setBorderWidth(1);
                              cell.setBorderColor(Color.GRAY);
                              //cell.setWidth("8");
                              cell.setLeading(9.0f);
                              cell.setNoWrap(false);
                              part.addCell(cell);

                              ///min
                              if(detailsDOB.getSecondaryBasis()!=null && detailsDOB.getSecondaryBasis().trim().length()>0)
                              {
                                  chk = new Chunk((detailsDOB.getSecUnitValue()!=null && detailsDOB.getSecUnitValue().trim().length()!=0)?deciFormat.format(Double.parseDouble(detailsDOB.getSecUnitValue())):"",FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
                                  cell = new Cell(chk);
                                  cell.setBackgroundColor(Color.WHITE);
                                  cell.setHorizontalAlignment(cell.ALIGN_LEFT);
                                  cell.setVerticalAlignment(cell.ALIGN_BASELINE);
                                  cell.setNoWrap(false);
                                  cell.setLeading(9.0f);
                                  cell.setBorderWidth(1);//cell.setWidth("8");
                                  cell.setBorderColor(Color.GRAY);
                                  part.addCell(cell);
                              }
                              else
                              {
                                  chk = new Chunk("",FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
                                  cell = new Cell(chk);
                                  cell.setBackgroundColor(Color.WHITE);
                                  cell.setHorizontalAlignment(cell.ALIGN_LEFT);
                                  cell.setVerticalAlignment(cell.ALIGN_BASELINE);
                                  cell.setNoWrap(false);
                                  cell.setLeading(9.0f);
                                  cell.setBorderWidth(1);//cell.setWidth("8");
                                  cell.setBorderColor(Color.GRAY);
                                  part.addCell(cell);
                              }

                              chk = new Chunk("=",FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
                              cell = new Cell(chk);
                              cell.setBackgroundColor(Color.WHITE);
                              cell.setBorderWidth(1);
                              cell.setBorderColor(Color.GRAY);
                              cell.setHorizontalAlignment(cell.ALIGN_LEFT);
                              cell.setVerticalAlignment(cell.ALIGN_BASELINE);
                              cell.setNoWrap(true); //cell.setWidth("2");
                              cell.setLeading(9.0f);
                              part.addCell(cell);

                              //chk = new Chunk((detailsDOB.getRateValue()!=null && detailsDOB.getRateValue().trim().length()>0)?(formatTotal.format(Double.parseDouble(detailsDOB.getRateValue()))):"",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
                              chk = new Chunk((detailsDOB.getRateValue()!=null && detailsDOB.getRateValue().trim().length()>0)?(formatTotal.format(Double.parseDouble(detailsDOB.getRateValue())*detailsDOB.getConvFactor())):"",FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
                              cell = new Cell(chk);
                              //cell.setBackgroundColor(Color.lightGray);
                              //cell.setWidth("8");
                              cell.setBorderWidth(1);
                              cell.setBorderColor(Color.GRAY);
                              cell.setHorizontalAlignment(cell.ALIGN_LEFT);
                              cell.setVerticalAlignment(cell.ALIGN_BASELINE);
                              cell.setLeading(9.0f);
                              cell.setNoWrap(false);
                              part.addCell(cell);

                          }
                          else
                          {
                              primaryUnitArray  = detailsDOB.getPrimaryUnitArray();
                              secUnitArray      = detailsDOB.getSecUnitArray();
                              /*chk = new Chunk(detailsDOB.getExternalName()!=null?detailsDOB.getExternalName():"",FontFactory.getFont("ARIAL", 9, Font.NORMAL,Color.BLACK));
                              cell = new Cell(chk);
                              cell.setBackgroundColor(Color.lightGray);
                              cell.setHorizontalAlignment(cell.ALIGN_CENTER);
                              cell.setVerticalAlignment(cell.ALIGN_BASELINE);
                              cell.setNoWrap(true);
                              cell.setBorderWidth(1);
                              cell.setLeading(9.0f);
                              //cell.setWidth("20");
                              part.addCell(cell);

                              chk = new Chunk("",FontFactory.getFont("ARIAL", 9, Font.NORMAL,Color.BLACK));
                              cell = new Cell(chk);
                              cell.setBackgroundColor(Color.WHITE);//cell.setWidth("9");
                              cell.setHorizontalAlignment(cell.ALIGN_CENTER);
                              cell.setVerticalAlignment(cell.ALIGN_BASELINE);
                              cell.setNoWrap(false);
                              cell.setBorderWidth(1);
                              cell.setLeading(9.0f);
                              part.addCell(cell);

                              chk = new Chunk(detailsDOB.getCurrency(),FontFactory.getFont("ARIAL", 9, Font.NORMAL,Color.BLACK));
                              cell = new Cell(chk);
                              cell.setBackgroundColor(Color.WHITE);
                              //cell.setWidth("8");
                              cell.setHorizontalAlignment(cell.ALIGN_CENTER);
                              cell.setVerticalAlignment(cell.ALIGN_BASELINE);
                              cell.setNoWrap(true); cell.setBorderWidth(1);
                              cell.setLeading(9.0f);
                              part.addCell(cell);

                              chk = new Chunk("",FontFactory.getFont("ARIAL", 9, Font.NORMAL,Color.BLACK));
                              cell = new Cell(chk);
                              cell.setHorizontalAlignment(cell.ALIGN_RIGHT);
                              cell.setVerticalAlignment(cell.ALIGN_BASELINE);
                              cell.setBackgroundColor(Color.lightGray);//cell.setWidth("8");
                              cell.setNoWrap(true);
                              cell.setLeading(9.0f);
                              cell.setBorderWidth(1);
                              part.addCell(cell);

                              //chk = new Chunk(detailsDOB.getChargeBasisDesc(),FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
                              chk = new Chunk("PER CONTAINER",FontFactory.getFont("ARIAL", 9, Font.NORMAL,Color.BLACK));
                              cell = new Cell(chk);
                              cell.setHorizontalAlignment(cell.ALIGN_CENTER);
                              cell.setVerticalAlignment(cell.ALIGN_BASELINE);
                              cell.setBackgroundColor(Color.lightGray);
                              cell.setBorderWidth(1);
                              cell.setLeading(9.0f);
                              //cell.setWidth("15");
                              part.addCell(cell);

                              chk = new Chunk("",FontFactory.getFont("ARIAL", 9, Font.NORMAL,Color.BLACK));
                              cell = new Cell(chk);
                              cell.setHorizontalAlignment(cell.ALIGN_CENTER);
                              cell.setVerticalAlignment(cell.ALIGN_BASELINE);
                              cell.setBackgroundColor(Color.WHITE);
                              cell.setLeading(9.0f);
                              //cell.setWidth("8");
                              cell.setNoWrap(true); cell.setBorderWidth(1);
                              part.addCell(cell);

                              chk = new Chunk("",FontFactory.getFont("ARIAL", 9, Font.NORMAL,Color.BLACK));
                              cell = new Cell(chk);
                              cell.setBackgroundColor(Color.WHITE);
                              //cell.setWidth("8");
                              cell.setHorizontalAlignment(cell.ALIGN_RIGHT);
                              cell.setVerticalAlignment(cell.ALIGN_BASELINE);
                              cell.setLeading(9.0f);
                              cell.setNoWrap(false); cell.setBorderWidth(1);
                              part.addCell(cell);

                              chk = new Chunk("",FontFactory.getFont("ARIAL", 9, Font.NORMAL,Color.BLACK));
                              cell = new Cell(chk);
                              cell.setBackgroundColor(Color.WHITE);
                              //cell.setWidth("8");
                              cell.setHorizontalAlignment(cell.ALIGN_RIGHT);
                              cell.setVerticalAlignment(cell.ALIGN_BASELINE);
                              cell.setLeading(9.0f);
                              cell.setNoWrap(false); cell.setBorderWidth(1);
                              part.addCell(cell);

                              ///min
                              chk = new Chunk("",FontFactory.getFont("ARIAL", 9, Font.NORMAL,Color.BLACK));
                              cell = new Cell(chk);
                              cell.setBackgroundColor(Color.WHITE);//cell.setWidth("2");
                              cell.setHorizontalAlignment(cell.ALIGN_CENTER);
                              cell.setVerticalAlignment(cell.ALIGN_BASELINE);
                              cell.setLeading(9.0f);
                              cell.setNoWrap(true); cell.setBorderWidth(1);
                              part.addCell(cell);

                              chk = new Chunk("",FontFactory.getFont("ARIAL", 9, Font.NORMAL,Color.BLACK));
                              cell = new Cell(chk);
                              cell.setBackgroundColor(Color.WHITE);
                              cell.setHorizontalAlignment(cell.ALIGN_RIGHT);
                              cell.setVerticalAlignment(cell.ALIGN_BASELINE);
                              cell.setBorderWidth(1);
                              cell.setLeading(9.0f);
                              cell.setNoWrap(true); //cell.setWidth("8");
                              part.addCell(cell);  */

                              chk = new Chunk(detailsDOB.getExternalName()!=null?detailsDOB.getExternalName():"",FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
                              cell = new Cell(chk);
                              //cell.setBackgroundColor(Color.lightGray);
                              cell.setHorizontalAlignment(cell.ALIGN_LEFT);
                              cell.setVerticalAlignment(cell.ALIGN_BASELINE);
                              cell.setNoWrap(true);
                              cell.setBorderWidth(1);
                              cell.setBorderColor(Color.GRAY);
                              cell.setLeading(9.0f);
                              cell.setColspan(9);
                              //cell.setWidth("20");
                              part.addCell(cell);
                              int rateDtlSize =	rateDetails.size();
                              for(int j=0;j<rateDtlSize;j++)
                              {
                                  rateDetailsDOB=(CostingRateInfoDOB)rateDetails.get(j);

                                  /*if(j==0)
                                  {
                                    chk = new Chunk(detailsDOB.getExternalName()!=null?detailsDOB.getExternalName():"",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
                                    cell = new Cell(chk);
                                    //cell.setBackgroundColor(Color.lightGray);
                                    cell.setHorizontalAlignment(cell.ALIGN_LEFT);
                                    cell.setVerticalAlignment(cell.ALIGN_BASELINE);
                                    cell.setNoWrap(true);
                                    cell.setBorderWidth(1);
                                    cell.setBorderColor(Color.GRAY);
                                    cell.setLeading(9.0f);
                                    //cell.setWidth("20");
                                    part.addCell(cell);
                                  }
                                  else
                                  {*/
                                  if((primaryUnitArray[j]!=null&&primaryUnitArray[j].trim().length()!=0))//@@Added by Kameswari for the issue
                                  {

                                    chk = new Chunk(rateDetailsDOB.getWeightBreakSlab()!=null?rateDetailsDOB.getWeightBreakSlab().toUpperCase():"",FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
                                    cell = new Cell(chk);
                                    //cell.setBackgroundColor(Color.lightGray);
                                    cell.setHorizontalAlignment(cell.ALIGN_LEFT);
                                    cell.setVerticalAlignment(cell.ALIGN_BASELINE);
                                    cell.setNoWrap(true);
                                    cell.setBorderWidth(1);
                                    cell.setBorderColor(Color.GRAY);
                                    cell.setLeading(9.0f);
                                    //cell.setWidth("20");
                                    part.addCell(cell);
                                  //}

                                  /*chk = new Chunk(rateDetailsDOB.getWeightBreakSlab()!=null?rateDetailsDOB.getWeightBreakSlab().toUpperCase():"",FontFactory.getFont("ARIAL", 9, Font.NORMAL,Color.BLACK));
                                  cell = new Cell(chk);
                                  cell.setHorizontalAlignment(cell.ALIGN_CENTER);
                                  cell.setVerticalAlignment(cell.ALIGN_BASELINE);
                                  cell.setBackgroundColor(Color.WHITE);
                                  cell.setNoWrap(true); //cell.setWidth("9");
                                  cell.setLeading(9.0f);
                                  cell.setBorderWidth(1);
                                  part.addCell(cell);*/



                                  chk = new Chunk(detailsDOB.getCurrency(),FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
                                  cell = new Cell(chk);
                                  cell.setBackgroundColor(Color.WHITE);//cell.setWidth("8");
                                  cell.setHorizontalAlignment(cell.ALIGN_LEFT);
                                  cell.setVerticalAlignment(cell.ALIGN_BASELINE);
                                  cell.setNoWrap(true); cell.setBorderWidth(1);
                                  cell.setBorderColor(Color.GRAY);
                                  cell.setLeading(9.0f);
                                  part.addCell(cell);

                                  chk = new Chunk(deciFormat.format(rateDetailsDOB.getRate()),FontFactory.getFont("ARIAL",7, Font.NORMAL,Color.BLACK));
                                  cell = new Cell(chk);
                                  cell.setHorizontalAlignment(cell.ALIGN_LEFT);
                                  cell.setVerticalAlignment(cell.ALIGN_BASELINE);
                                  //cell.setBackgroundColor(Color.lightGray);
                                  cell.setBorderWidth(1);
                                  cell.setBorderColor(Color.GRAY);
                                  cell.setLeading(9.0f);
                                  cell.setNoWrap(false); //cell.setWidth("9");
                                  part.addCell(cell);
                                  //chk = new Chunk(detailsDOB.getChargeBasisDesc()!=null?detailsDOB.getChargeBasisDesc():"",FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
                                 // Logger.info(FILE_NAME,"detailsDOB.getShipmentMode()::"+detailsDOB.getShipmentMode());
                                  //chk = new Chunk("PER CONTAINER",FontFactory.getFont("ARIAL", 9, Font.NORMAL,Color.BLACK));
                                  chk = new Chunk("Per Container",FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
                                  cell = new Cell(chk);
                                  cell.setHorizontalAlignment(cell.ALIGN_LEFT);
                                  cell.setVerticalAlignment(cell.ALIGN_BASELINE);
                                  //cell.setBackgroundColor(Color.lightGray);
                                  cell.setLeading(9.0f);
                                  //cell.setWidth("15");
                                  cell.setBorderWidth(1);
                                  cell.setBorderColor(Color.GRAY);
                                  part.addCell(cell);

                                  chk = new Chunk((detailsDOB.getDensityRatio()!=null && !"".equals(detailsDOB.getDensityRatio())  ) ?detailsDOB.getDensityRatio() :"" ,FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
                                  cell = new Cell(chk);
                                  cell.setHorizontalAlignment(cell.ALIGN_LEFT);
                                  cell.setVerticalAlignment(cell.ALIGN_BASELINE);
                                  cell.setBackgroundColor(Color.WHITE);//cell.setWidth("8");
                                  cell.setNoWrap(true); cell.setBorderWidth(1);
                                  cell.setBorderColor(Color.GRAY);
                                  cell.setLeading(9.0f);
                                  part.addCell(cell);

                                //  chk = new Chunk((primaryUnitArray[j]!=null && primaryUnitArray[j].trim().length()!=0)?deciFormat.format(Double.parseDouble(primaryUnitArray[j])):"",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));//@@Commented by Kameswari for the issue
                                  chk = new Chunk(deciFormat.format(Double.parseDouble(primaryUnitArray[j])),FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));//@@Modified by Kameswari for the issue
                                  cell = new Cell(chk);
                                  cell.setHorizontalAlignment(cell.ALIGN_LEFT);
                                  cell.setVerticalAlignment(cell.ALIGN_BASELINE);
                                  cell.setBackgroundColor(Color.WHITE);//cell.setWidth("8");
                                  cell.setNoWrap(false); cell.setBorderWidth(1);
                                  cell.setBorderColor(Color.GRAY);
                                  cell.setLeading(9.0f);
                                  part.addCell(cell);

                                  chk = new Chunk((secUnitArray[j]!=null && secUnitArray[j].trim().length()!=0)?deciFormat.format(Double.parseDouble(secUnitArray[j])):"",FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
                                  cell = new Cell(chk);
                                  cell.setBorderWidth(1);
                                  cell.setBorderColor(Color.GRAY);
                                  cell.setBackgroundColor(Color.WHITE);//cell.setWidth("8");
                                  cell.setHorizontalAlignment(cell.ALIGN_LEFT);
                                  cell.setVerticalAlignment(cell.ALIGN_BASELINE);
                                  cell.setNoWrap(false);
                                  cell.setLeading(9.0f);
                                  part.addCell(cell);
                               //        chk = new Chunk((detailsDOB.getWeightBreak().equalsIgnoreCase("List"))?((rateDetailsDOB.getRateValue()==null || "".equals(rateDetailsDOB.getRateValue()))?"":"="):"=",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
                                   chk = new Chunk((detailsDOB.getWeightBreak().equalsIgnoreCase("List"))?((rateDetailsDOB.getRateValue()==null || "".equals(rateDetailsDOB.getRateValue()))?"":"="):"=",FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
                                  cell = new Cell(chk);cell.setBorderWidth(1);
                                  cell.setBorderColor(Color.GRAY);
                                  cell.setBackgroundColor(Color.WHITE);
                                  cell.setHorizontalAlignment(cell.ALIGN_LEFT);
                                  cell.setVerticalAlignment(cell.ALIGN_BASELINE);
                                  cell.setNoWrap(true); //cell.setWidth("2");
                                  cell.setLeading(9.0f);
                                  part.addCell(cell);

                                //  chk = new Chunk((detailsDOB.getWeightBreak().equalsIgnoreCase("List"))?((rateDetailsDOB.getRateValue()==null || "".equals(rateDetailsDOB.getRateValue()))?"":(formatTotal.format(Double.parseDouble(rateDetailsDOB.getRateValue())))):"",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
                                    chk = new Chunk((detailsDOB.getWeightBreak().equalsIgnoreCase("List"))?((rateDetailsDOB.getRateValue()==null || "".equals(rateDetailsDOB.getRateValue()))?"":(formatTotal.format(Double.parseDouble(rateDetailsDOB.getRateValue())*detailsDOB.getConvFactor()))):"",FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
                                  cell = new Cell(chk);cell.setBorderWidth(1);
                                  cell.setBorderColor(Color.GRAY);
                                  cell.setBackgroundColor(Color.WHITE);
                                  cell.setHorizontalAlignment(cell.ALIGN_LEFT);
                                  cell.setVerticalAlignment(cell.ALIGN_BASELINE);
                                  cell.setNoWrap(true); //cell.setWidth("8");
                                  cell.setLeading(9.0f);
                                  part.addCell(cell);
                                  }
                              }//end of for
                          }
                      }//end for if
                }//end for for

                if(isOriginChecked)
                {
                  chk = new Chunk("Subtotal A (Base Currency)",FontFactory.getFont("ARIAL", 7, Font.BOLD,Color.BLACK));
                  cell = new Cell(chk);
                  cell.setBackgroundColor(Color.WHITE);
                  cell.setNoWrap(false);
                  //cell.setWidth("20");
                  cell.setBorderWidth(1);
                  cell.setBorderColor(Color.GRAY);
                  cell.setBorder(Rectangle.TOP | Rectangle.BOTTOM);
                  cell.setColspan(7);
                  cell.setLeading(9.0f);
                  cell.setHorizontalAlignment(cell.ALIGN_RIGHT);
                  cell.setVerticalAlignment(cell.ALIGN_BOTTOM);
                  part.addCell(cell);

                  chk = new Chunk("=",FontFactory.getFont("ARIAL", 7, Font.BOLD,Color.BLACK));
                  cell = new Cell(chk);cell.setBorderWidth(1);
                  cell.setBorderColor(Color.GRAY);
                  cell.setLeading(9.0f);
                  cell.setBorder(Rectangle.TOP | Rectangle.BOTTOM);
                  cell.setHorizontalAlignment(cell.ALIGN_LEFT);
                  cell.setVerticalAlignment(cell.ALIGN_BOTTOM);
                  part.addCell(cell);

                  chk = new Chunk((costingMasterDOB.getOriginTotal()!=null && costingMasterDOB.getOriginTotal().trim().length()>0)?formatTotal.format(Double.parseDouble(costingMasterDOB.getOriginTotal())):"",FontFactory.getFont("ARIAL", 7, Font.BOLD,Color.BLACK));
                  cell = new Cell(chk);
                  cell.setBorderWidth(1);
                  cell.setBorderColor(Color.GRAY);
                  cell.setBorder(Rectangle.TOP | Rectangle.BOTTOM);
                  cell.setLeading(9.0f);
                  cell.setHorizontalAlignment(cell.ALIGN_LEFT);
                  cell.setVerticalAlignment(cell.ALIGN_BOTTOM);
                  part.addCell(cell);
                }

            }//end for if

            frtListSize                    = costingMasterDOB.getCostingLegDetailsList().size();
            ArrayList frieghtChargeDetails = null;
           String wtbreakSlab = null;
            if(frtListSize>0)
            {

                for(int m=0;m<frtListSize;m++)
                {
                  legDetails = (CostingLegDetailsDOB)costingMasterDOB.getCostingLegDetailsList().get(m);
						      frieghtChargeDetails      =  (ArrayList)legDetails.getCostingChargeDetailList();
						      int frtChargDtlSize	=	frieghtChargeDetails.size();
				  //for(int n=0;n<frieghtChargeDetails.size();n++)
			      for(int n=0;n<frieghtChargeDetails.size();n++)
                  {
                      //detailsDOB = (CostingChargeDetailsDOB)frieghtChargeDetails.get(n);
                        //@@Modified by kameswari for Surcharge Enhancements
                      detailsList = (ArrayList)frieghtChargeDetails.get(n);
                      int dtlsListSize	=	detailsList.size();
                      for(int k=0;k<dtlsListSize;k++)
                      {
                        detailsDOB = (CostingChargeDetailsDOB)detailsList.get(k);
                         rateDetails=(ArrayList)detailsDOB.getCostingRateInfoDOB();
                            if("checked".equals(detailsDOB.getChecked()))
                            {
                               // if(n==0 && !detailsDOB.getWeightBreak().equalsIgnoreCase("List"))
                                if(!((detailsDOB.getWeightBreak().equalsIgnoreCase("List")))||
                                ((detailsDOB.getWeightBreak().equalsIgnoreCase("List"))&&!
                                ("2".equalsIgnoreCase(detailsDOB.getShipmentMode()))&&!
                                ("FREIGHT RATE".equalsIgnoreCase(detailsDOB.getRateDescription()) || "A FREIGHT RATE".equalsIgnoreCase(detailsDOB.getRateDescription()))))
                                 {
//@@ Commented by subrahmanyam for 146455
                                 /*
                                  chk = new Chunk((detailsDOB.getRateDescription()!=null)?detailsDOB.getRateDescription().toUpperCase():"",FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
                                 //  chk = new Chunk((detailsDOB.getRateDescription()!=null)?"Freight Rate":"",FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
                                    cell = new Cell(chk);
                                    //cell.setBackgroundColor(Color.lightGray);
                                    cell.setHorizontalAlignment(cell.ALIGN_LEFT);
                                    cell.setVerticalAlignment(cell.ALIGN_BASELINE);
                                    cell.setNoWrap(false);
                                    cell.setLeading(9.0f);
                                    cell.setBorderWidth(1);
                                    cell.setBorderColor(Color.GRAY);

                                    //cell.setWidth("20");
                                    */
    //@@Added by subrahmanyam for 146455
                                	//Modified by silpa.p on 7-06-11
                        String rateDes="";
                        if("FUEL SURCHARGE".equalsIgnoreCase(detailsDOB.getRateDescription().indexOf("-")!=-1?detailsDOB.getRateDescription().substring(0,detailsDOB.getRateDescription().length()-3):detailsDOB.getRateDescription()))//modified by silpa.p on 8-06-11
                        {
                          rateDes="Fuel Surcharge";
                        }
                        else if("FREIGHT RATE".equalsIgnoreCase(detailsDOB.getRateDescription()) || "A FREIGHT RATE".equalsIgnoreCase(detailsDOB.getRateDescription()))
                          {
                              rateDes="Freight Rate";
                          }

                        else if("SECURITY SURCHARGE".equalsIgnoreCase(detailsDOB.getRateDescription().indexOf("-")!=-1?detailsDOB.getRateDescription().substring(0,detailsDOB.getRateDescription().length()-3):detailsDOB.getRateDescription()))//modified by silpa.p on 8-06-11
                        {
                          rateDes="Security Surcharge";
                        }
                        else if("C.A.F%".equalsIgnoreCase(detailsDOB.getRateDescription())|| "CAF%".equalsIgnoreCase(detailsDOB.getRateDescription()))
                        {
                          rateDes="C.a.f%";
                        }
                        else if("B.A.F".equalsIgnoreCase(detailsDOB.getRateDescription())||"BAF".equalsIgnoreCase(detailsDOB.getRateDescription()))
                        {
                          rateDes="B.a.f";
                        }
                        else if("P.S.S".equalsIgnoreCase(detailsDOB.getRateDescription())|| "PSS".equalsIgnoreCase(detailsDOB.getRateDescription()))
                        {
                          rateDes="P.s.s";
                        }
                        else if("C.S.F".equalsIgnoreCase(detailsDOB.getRateDescription()) || "CSF".equalsIgnoreCase(detailsDOB.getRateDescription()))
                        {
                          rateDes="C.s.f";
                        }
                        else
                         rateDes=("!A FREIGHT RATE".equalsIgnoreCase(detailsDOB.getRateDescription())||!"FREIGHT RATE".equalsIgnoreCase(detailsDOB.getRateDescription()) && detailsDOB.getRateDescription().length()>3)?
                        		 detailsDOB.getRateDescription().substring(0,detailsDOB.getRateDescription().length()-3):detailsDOB.getRateDescription();


                          chk = new Chunk((rateDes!=null?rateDes:""),FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
                                cell = new Cell(chk);
                                    //cell.setBackgroundColor(Color.lightGray);
                                    cell.setHorizontalAlignment(cell.ALIGN_LEFT);
                                    cell.setVerticalAlignment(cell.ALIGN_BASELINE);
                                    cell.setNoWrap(false);
                                    cell.setLeading(9.0f);
                                    cell.setBorderWidth(1);
                                    cell.setBorderColor(Color.GRAY);
//ended
   //@@ Ended by subrahmanym for 146455
                                    part.addCell(cell);

                                    /*chk = new Chunk((detailsDOB.getBrkPoint()!=null)?detailsDOB.getBrkPoint().toUpperCase():"",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
                                    cell = new Cell(chk);
                                    cell.setBackgroundColor(Color.WHITE);
                                    //cell.setWidth("9");
                                    cell.setHorizontalAlignment(cell.ALIGN_CENTER);
                                    cell.setVerticalAlignment(cell.ALIGN_BASELINE);
                                    cell.setLeading(9.0f);
                                    cell.setNoWrap(true);
                                    cell.setBorderWidth(1);
                                    part.addCell(cell);*/

                                    chk = new Chunk(detailsDOB.getCurrency(),FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
                                    cell = new Cell(chk);
                                    cell.setBackgroundColor(Color.WHITE);
                                    //cell.setWidth("8");
                                    cell.setHorizontalAlignment(cell.ALIGN_LEFT);
                                    cell.setVerticalAlignment(cell.ALIGN_BASELINE);
                                    cell.setLeading(9.0f);
                                    cell.setNoWrap(true);
                                    cell.setBorderWidth(1);
                                    cell.setBorderColor(Color.GRAY);
                                    part.addCell(cell);

                                    chk = new Chunk(deciFormat.format(Double.parseDouble(detailsDOB.getRate()!=null?detailsDOB.getRate():"0")),FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
                                    cell = new Cell(chk);
                                    cell.setHorizontalAlignment(cell.ALIGN_LEFT);
                                    cell.setVerticalAlignment(cell.ALIGN_BASELINE);
                                    cell.setBorderWidth(1);
                                    cell.setBorderColor(Color.GRAY);
                                    //cell.setBackgroundColor(Color.lightGray);
                                    cell.setLeading(9.0f);
                                    //cell.setWidth("8");
                                    cell.setNoWrap(true);
                                    part.addCell(cell);

                                    chk = new Chunk(detailsDOB.getChargeBasis()!=null?detailsDOB.getChargeBasis():"",FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
                                    cell = new Cell(chk);
                                    cell.setHorizontalAlignment(cell.ALIGN_LEFT);
                                    cell.setVerticalAlignment(cell.ALIGN_BASELINE);
                                    //cell.setBackgroundColor(Color.lightGray);
                                    cell.setBorderWidth(1);
                                    cell.setBorderColor(Color.GRAY);
                                    cell.setLeading(9.0f);
                                    //cell.setWidth("15");
                                    part.addCell(cell);

                                    chk = new Chunk(detailsDOB.getDensityRatio()!=null?detailsDOB.getDensityRatio():"",FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
                                    cell = new Cell(chk);
                                    cell.setHorizontalAlignment(cell.ALIGN_LEFT);
                                    cell.setVerticalAlignment(cell.ALIGN_BASELINE);
                                    cell.setBackgroundColor(Color.WHITE);
                                    cell.setLeading(9.0f);
                                    //cell.setWidth("8");
                                    cell.setNoWrap(true); cell.setBorderWidth(1);
                                    cell.setBorderColor(Color.GRAY);
                                    part.addCell(cell);

                                    chk = new Chunk((detailsDOB.getPrimaryUnitValue()!=null && detailsDOB.getPrimaryUnitValue().trim().length()!=0)?deciFormat.format(Double.parseDouble(detailsDOB.getPrimaryUnitValue())):"",FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
                                    cell = new Cell(chk);
                                    cell.setBackgroundColor(Color.WHITE);
                                    //cell.setWidth("8");
                                    cell.setHorizontalAlignment(cell.ALIGN_LEFT);
                                    cell.setVerticalAlignment(cell.ALIGN_BASELINE);
                                    cell.setNoWrap(false);
                                    cell.setBorderWidth(1);
                                    cell.setBorderColor(Color.GRAY);
                                    cell.setLeading(9.0f);
                                    part.addCell(cell);

                                    chk = new Chunk((detailsDOB.getSecUnitValue()!=null && detailsDOB.getSecUnitValue().trim().length()!=0)?deciFormat.format(Double.parseDouble(detailsDOB.getSecUnitValue())):"",FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
                                    cell = new Cell(chk);
                                    cell.setBackgroundColor(Color.WHITE);
                                    //cell.setWidth("8");
                                    cell.setHorizontalAlignment(cell.ALIGN_LEFT);
                                    cell.setVerticalAlignment(cell.ALIGN_BASELINE);
                                    cell.setLeading(9.0f);
                                    cell.setNoWrap(false);
                                    cell.setBorderWidth(1);
                                    cell.setBorderColor(Color.GRAY);
                                    part.addCell(cell);

                                    ///min
                                    chk = new Chunk("=",FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
                                    cell = new Cell(chk);
                                    cell.setBackgroundColor(Color.WHITE);
                                    //cell.setWidth("2");
                                    cell.setHorizontalAlignment(cell.ALIGN_LEFT);
                                    cell.setVerticalAlignment(cell.ALIGN_BASELINE);
                                    cell.setNoWrap(true); cell.setBorderWidth(1);
                                    cell.setBorderColor(Color.GRAY);
                                    cell.setLeading(9.0f);
                                    part.addCell(cell);

                                   // chk = new Chunk((detailsDOB.getRateValue()!=null && detailsDOB.getRateValue().trim().length()>0)?formatTotal.format(Double.parseDouble(detailsDOB.getRateValue())):"",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
                                   chk = new Chunk((detailsDOB.getRateValue()!=null && detailsDOB.getRateValue().trim().length()>0)?formatTotal.format(Double.parseDouble(detailsDOB.getRateValue())*detailsDOB.getConvFactor()):"",FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
                                     cell = new Cell(chk);
                                    //cell.setBackgroundColor(Color.lightGray);
                                    cell.setHorizontalAlignment(cell.ALIGN_LEFT);
                                    cell.setBorderColor(Color.GRAY);
                                    cell.setVerticalAlignment(cell.ALIGN_BASELINE);
                                    cell.setLeading(9.0f);
                                    cell.setNoWrap(true); //cell.setWidth("8");
                                    part.addCell(cell);
                                 }
                                 else
                                 {
                                    primaryUnitArray  = detailsDOB.getPrimaryUnitArray();
                                    /*chk = new Chunk("Freight Rate",FontFactory.getFont("ARIAL", 9, Font.NORMAL,Color.BLACK));
                                    cell = new Cell(chk);
                                    cell.setBackgroundColor(Color.lightGray);
                                    cell.setHorizontalAlignment(cell.ALIGN_CENTER);
                                    cell.setVerticalAlignment(cell.ALIGN_BASELINE);
                                    cell.setNoWrap(true);
                                    cell.setBorderWidth(1);
                                    cell.setLeading(9.0f);
                                    //cell.setWidth("20");
                                    part.addCell(cell);

                                    chk = new Chunk("",FontFactory.getFont("ARIAL", 9, Font.NORMAL,Color.BLACK));
                                    cell = new Cell(chk);
                                    cell.setBackgroundColor(Color.WHITE);//cell.setWidth("9");
                                    cell.setHorizontalAlignment(cell.ALIGN_CENTER);
                                    cell.setVerticalAlignment(cell.ALIGN_BASELINE);
                                    cell.setNoWrap(false);
                                    cell.setBorderWidth(1);
                                    cell.setLeading(9.0f);
                                    part.addCell(cell);

                                    chk = new Chunk(detailsDOB.getCurrency(),FontFactory.getFont("ARIAL", 9, Font.NORMAL,Color.BLACK));
                                    cell = new Cell(chk);
                                    cell.setBackgroundColor(Color.WHITE);
                                    //cell.setWidth("8");
                                    cell.setHorizontalAlignment(cell.ALIGN_CENTER);
                                    cell.setVerticalAlignment(cell.ALIGN_BASELINE);
                                    cell.setNoWrap(true); cell.setBorderWidth(1);
                                    cell.setLeading(9.0f);
                                    part.addCell(cell);

                                    chk = new Chunk("",FontFactory.getFont("ARIAL", 9, Font.NORMAL,Color.BLACK));
                                    cell = new Cell(chk);
                                    cell.setHorizontalAlignment(cell.ALIGN_RIGHT);
                                    cell.setVerticalAlignment(cell.ALIGN_BASELINE);
                                    cell.setBackgroundColor(Color.lightGray);//cell.setWidth("8");
                                    cell.setNoWrap(true);
                                    cell.setLeading(9.0f);
                                    cell.setBorderWidth(1);
                                    part.addCell(cell);

                                    //chk = new Chunk(detailsDOB.getChargeBasisDesc(),FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
                                    chk = new Chunk("1".equalsIgnoreCase(detailsDOB.getShipmentMode())?"PER ULD":"PER CONTAINER",FontFactory.getFont("ARIAL", 9, Font.NORMAL,Color.BLACK));
                                    cell = new Cell(chk);
                                    cell.setHorizontalAlignment(cell.ALIGN_CENTER);
                                    cell.setVerticalAlignment(cell.ALIGN_BASELINE);
                                    cell.setBackgroundColor(Color.lightGray);
                                    cell.setBorderWidth(1);
                                    cell.setLeading(9.0f);
                                    //cell.setWidth("15");
                                    part.addCell(cell);

                                    chk = new Chunk("",FontFactory.getFont("ARIAL", 9, Font.NORMAL,Color.BLACK));
                                    cell = new Cell(chk);
                                    cell.setHorizontalAlignment(cell.ALIGN_CENTER);
                                    cell.setVerticalAlignment(cell.ALIGN_BASELINE);
                                    cell.setBackgroundColor(Color.WHITE);
                                    cell.setLeading(9.0f);
                                    //cell.setWidth("8");
                                    cell.setNoWrap(true); cell.setBorderWidth(1);
                                    part.addCell(cell);

                                    chk = new Chunk("",FontFactory.getFont("ARIAL", 9, Font.NORMAL,Color.BLACK));
                                    cell = new Cell(chk);
                                    cell.setBackgroundColor(Color.WHITE);
                                    //cell.setWidth("8");
                                    cell.setHorizontalAlignment(cell.ALIGN_RIGHT);
                                    cell.setVerticalAlignment(cell.ALIGN_BASELINE);
                                    cell.setLeading(9.0f);
                                    cell.setNoWrap(false); cell.setBorderWidth(1);
                                    part.addCell(cell);

                                    chk = new Chunk("",FontFactory.getFont("ARIAL", 9, Font.NORMAL,Color.BLACK));
                                    cell = new Cell(chk);
                                    cell.setBackgroundColor(Color.WHITE);
                                    //cell.setWidth("8");
                                    cell.setHorizontalAlignment(cell.ALIGN_RIGHT);
                                    cell.setVerticalAlignment(cell.ALIGN_BASELINE);
                                    cell.setLeading(9.0f);
                                    cell.setNoWrap(false); cell.setBorderWidth(1);
                                    part.addCell(cell);

                                    ///min
                                    chk = new Chunk("",FontFactory.getFont("ARIAL", 9, Font.NORMAL,Color.BLACK));
                                    cell = new Cell(chk);
                                    cell.setBackgroundColor(Color.WHITE);//cell.setWidth("2");
                                    cell.setHorizontalAlignment(cell.ALIGN_CENTER);
                                    cell.setVerticalAlignment(cell.ALIGN_BASELINE);
                                    cell.setLeading(9.0f);
                                    cell.setNoWrap(true); cell.setBorderWidth(1);
                                    part.addCell(cell);

                                    chk = new Chunk("",FontFactory.getFont("ARIAL", 9, Font.NORMAL,Color.BLACK));
                                    cell = new Cell(chk);
                                    cell.setBackgroundColor(Color.WHITE);
                                    cell.setHorizontalAlignment(cell.ALIGN_RIGHT);
                                    cell.setVerticalAlignment(cell.ALIGN_BASELINE);
                                    cell.setBorderWidth(1);
                                    cell.setLeading(9.0f);
                                    cell.setNoWrap(true); //cell.setWidth("8");
                                    part.addCell(cell);*/

                                   /* chk = new Chunk(detailsDOB.getRateDescription(),FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
                                    cell = new Cell(chk);
                                    //cell.setBackgroundColor(Color.lightGray);
                                    cell.setHorizontalAlignment(cell.ALIGN_LEFT);
                                    cell.setVerticalAlignment(cell.ALIGN_BASELINE);
                                    cell.setNoWrap(true);
                                    cell.setBorderWidth(1);
                                    cell.setBorderColor(Color.GRAY);
                                    cell.setLeading(9.0f);
                                    cell.setColspan(9);
                                    //cell.setWidth("20");
                                    part.addCell(cell);*/
                                      String s = null;
                                      int rateDtlSize	=	rateDetails.size();
                                  for(int j=0;j<rateDtlSize;j++)
                                    {

                                        rateDetailsDOB=(CostingRateInfoDOB)rateDetails.get(j);

                                        if(detailsDOB.getRateDescription().equalsIgnoreCase(rateDetailsDOB.getRateDescription()))
                                        {

											//Added by Mohan for issue no.219979 on 10122010
                                            if(primaryUnitArray!=null && (primaryUnitArray[j]!=null&&primaryUnitArray[j].trim().length()!=0))//@@Added by Kameswari for the issue
                                          {
                                        /*if(j==0)
                                        {
                                          chk = new Chunk("Freight Rate",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
                                          cell = new Cell(chk);
                                          //cell.setBackgroundColor(Color.lightGray);
                                          cell.setHorizontalAlignment(cell.ALIGN_LEFT);
                                          cell.setVerticalAlignment(cell.ALIGN_BASELINE);
                                          cell.setNoWrap(true);
                                          cell.setBorderWidth(1);
                                          cell.setBorderColor(Color.GRAY);
                                          cell.setLeading(9.0f);
                                          //cell.setWidth("20");
                                          part.addCell(cell);
                                        }
                                        else
                                        {*/
                                        if("List".equalsIgnoreCase(detailsDOB.getWeightBreak()) && "2".equals(detailsDOB.getShipmentMode())&&
                                        		(!"FREIGHT RATE".equalsIgnoreCase(detailsDOB.getRateDescription()) && !"A FREIGHT RATE".equalsIgnoreCase(detailsDOB.getRateDescription()) && !"CAF%".equalsIgnoreCase(detailsDOB.getRateDescription()))){//added by silpa.p on 29-06-11
                                        	chk = new Chunk(rateDetailsDOB.getWeightBreakSlab()!=null && (detailsDOB.getRateDescription()!= null && detailsDOB.getRateDescription().length()>3)?rateDetailsDOB.getWeightBreakSlab().substring(0,rateDetailsDOB.getWeightBreakSlab().length()-2).toUpperCase():rateDetailsDOB.getWeightBreakSlab().toUpperCase(),FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
                                        }else if ((!"FREIGHT RATE".equalsIgnoreCase(detailsDOB.getRateDescription()) || !"A FREIGHT RATE".equalsIgnoreCase(detailsDOB.getRateDescription())&&
                                        		 (rateDetailsDOB.getWeightBreakSlab()!=null && rateDetailsDOB.getWeightBreakSlab().length()>5 ))&& !"CAF%".equalsIgnoreCase(detailsDOB.getRateDescription())){//added by silpa.p on 29-06-11
                                          chk = new Chunk(rateDetailsDOB.getWeightBreakSlab().substring(5).toUpperCase(),FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
                                        }else{
                                        	chk = new Chunk(rateDetailsDOB.getWeightBreakSlab()!=null?rateDetailsDOB.getWeightBreakSlab().toUpperCase():"",FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
                                        }
                                          cell = new Cell(chk);
                                          //cell.setBackgroundColor(Color.lightGray);
                                          cell.setHorizontalAlignment(cell.ALIGN_LEFT);
                                          cell.setVerticalAlignment(cell.ALIGN_BASELINE);
                                          cell.setNoWrap(true);
                                          cell.setBorderWidth(1);
                                          cell.setBorderColor(Color.GRAY);
                                          cell.setLeading(9.0f);
                                          //cell.setWidth("20");
                                          part.addCell(cell);
                                       // }

                                        /*chk = new Chunk(rateDetailsDOB.getWeightBreakSlab()!=null?rateDetailsDOB.getWeightBreakSlab().toUpperCase():"",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
                                        cell = new Cell(chk);
                                        cell.setHorizontalAlignment(cell.ALIGN_CENTER);
                                        cell.setVerticalAlignment(cell.ALIGN_BASELINE);
                                        cell.setBackgroundColor(Color.WHITE);
                                        cell.setNoWrap(true); //cell.setWidth("9");
                                        cell.setLeading(9.0f);
                                        cell.setBorderWidth(1);
                                        part.addCell(cell);*/

                                        chk = new Chunk(detailsDOB.getCurrency(),FontFactory.getFont("ARIAL",7, Font.NORMAL,Color.BLACK));
                                        cell = new Cell(chk);
                                        cell.setBackgroundColor(Color.WHITE);//cell.setWidth("8");
                                        cell.setHorizontalAlignment(cell.ALIGN_LEFT);
                                        cell.setVerticalAlignment(cell.ALIGN_BASELINE);
                                        cell.setNoWrap(true); cell.setBorderWidth(1);
                                        cell.setBorderColor(Color.GRAY);
                                        cell.setLeading(9.0f);
                                        part.addCell(cell);

                                        chk = new Chunk(deciFormat.format(rateDetailsDOB.getRate()),FontFactory.getFont("ARIAL",7, Font.NORMAL,Color.BLACK));
                                        cell = new Cell(chk);
                                        cell.setHorizontalAlignment(cell.ALIGN_LEFT);
                                        cell.setVerticalAlignment(cell.ALIGN_BASELINE);
                                        //cell.setBackgroundColor(Color.lightGray);
                                        cell.setBorderWidth(1);
                                        cell.setBorderColor(Color.GRAY);
                                        cell.setLeading(9.0f);
                                        cell.setNoWrap(false); //cell.setWidth("9");
                                        part.addCell(cell);
                                        //String  wtbreakSlab  =  null;
                                        //chk = new Chunk(detailsDOB.getChargeBasisDesc()!=null?detailsDOB.getChargeBasisDesc():"",FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
                                       // Logger.info(FILE_NAME,"detailsDOB.getShipmentMode()::"+detailsDOB.getShipmentMode());
                                         /*   if("Basic Charge".equalsIgnoreCase(rateDetailsDOB.getWeightBreakSlab())||"Minimum".equalsIgnoreCase(rateDetailsDOB.getWeightBreakSlab())||"SSMIN".equalsIgnoreCase(rateDetailsDOB.getWeightBreakSlab())||"FSMIN".equalsIgnoreCase(rateDetailsDOB.getWeightBreakSlab()))
                                          {
                                               wtbreakSlab  =  "Per Shipment";
                                          }
                                          else if ("Flat Rate".equalsIgnoreCase(rateDetailsDOB.getWeightBreakSlab())||"SSKG".equalsIgnoreCase(rateDetailsDOB.getWeightBreakSlab()))
                                          {
                                               wtbreakSlab  =  "Per Kg";
                                          }
                                          else if("SURCHARGE".equalsIgnoreCase(rateDetailsDOB.getWeightBreakSlab()))
                                          {
                                              wtbreakSlab  =  "Percent";
                                          }
                                          else
                                          {
                                            if("1".equalsIgnoreCase(detailsDOB.getShipmentMode()))
                                            {
                                              wtbreakSlab  =  "Per ULD";
                                            }
                                            else
                                            {
                                              wtbreakSlab  =  "Per Container";
                                            }

                                          }*/

                                        //chk = new Chunk("1".equalsIgnoreCase(detailsDOB.getShipmentMode())?"Per ULD":"Per Container",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
                                        chk = new Chunk(detailsDOB.getRateDescription().endsWith("CAF%")||detailsDOB.getRateDescription().startsWith("Currency Adjustment Factor")?"Percent of Freight":"Per Container",FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
                                        cell = new Cell(chk);cell.setHorizontalAlignment(cell.ALIGN_LEFT);
                                        cell.setVerticalAlignment(cell.ALIGN_BASELINE);
                                        //cell.setBackgroundColor(Color.lightGray);
                                        cell.setLeading(9.0f);
                                        //cell.setWidth("15");
                                        cell.setBorderWidth(1);
                                        cell.setBorderColor(Color.GRAY);
                                        part.addCell(cell);

                                        chk = new Chunk((detailsDOB.getDensityRatio()!=null && !"".equals(detailsDOB.getDensityRatio())  ) ?detailsDOB.getDensityRatio() :"" ,FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
                                        cell = new Cell(chk);
                                        //cell.setHorizontalAlignment(cell.ALIGN_RIGHT);// Commented by subrahmanyam for 178970 on 11-aug-09
                                        cell.setHorizontalAlignment(cell.ALIGN_LEFT);// Added by subrahmanyam for 178970 on 11-aug-09
                                        cell.setVerticalAlignment(cell.ALIGN_BASELINE);
                                        cell.setBackgroundColor(Color.WHITE);//cell.setWidth("8");
                                        cell.setNoWrap(true); cell.setBorderWidth(1);
                                        cell.setBorderColor(Color.GRAY);
                                        cell.setLeading(9.0f);
                                        part.addCell(cell);

                                       // chk = new Chunk((primaryUnitArray[j]!=null && primaryUnitArray[j].trim().length()!=0)?deciFormat.format(Double.parseDouble(primaryUnitArray[j])):"",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));//@@Commented by Kameswari for the issue
                                       chk = new Chunk(deciFormat.format(Double.parseDouble(primaryUnitArray[j])),FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));//@@Modified by Kameswari for the issue
                                       //chk = new Chunk(deciFormat.format(Double.parseDouble(s)),FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));//@@Modified by Kameswari for the issue
                                          cell = new Cell(chk);
                                         // cell.setHorizontalAlignment(cell.ALIGN_RIGHT);// Commented by subrahmanyam for 178970 on 11-aug-09
                                         cell.setHorizontalAlignment(cell.ALIGN_LEFT);// Added by subrahmanyam for 178970 on 11-aug-09
                                        cell.setVerticalAlignment(cell.ALIGN_BASELINE);
                                        cell.setBackgroundColor(Color.WHITE);//cell.setWidth("8");
                                        cell.setLeading(9.0f);
                                        cell.setNoWrap(false);
                                        cell.setBorderWidth(1);
                                        cell.setBorderColor(Color.GRAY);
                                        part.addCell(cell);

                                        chk = new Chunk("",FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
                                        cell = new Cell(chk);cell.setBorderWidth(1);
                                        cell.setBorderColor(Color.GRAY);
                                        cell.setBackgroundColor(Color.WHITE);//cell.setWidth("8");
                                        cell.setVerticalAlignment(cell.ALIGN_BASELINE);
                                        cell.setNoWrap(false);
                                        cell.setLeading(9.0f);
                                        part.addCell(cell);

                                       // chk = new Chunk((detailsDOB.getWeightBreak().equalsIgnoreCase("List"))?((rateDetailsDOB.getRateValue()==null || "".equals(rateDetailsDOB.getRateValue()))?"":"="):"=",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
                                        chk = new Chunk((detailsDOB.getWeightBreak().equalsIgnoreCase("List"))?((rateDetailsDOB.getRateValue()==null || "".equals(rateDetailsDOB.getRateValue()))?"":"="):"=",FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
                                         cell = new Cell(chk);cell.setBorderWidth(1);
                                        cell.setBorderColor(Color.GRAY);
                                        cell.setBackgroundColor(Color.WHITE);
                                        cell.setHorizontalAlignment(cell.ALIGN_LEFT);
                                        cell.setVerticalAlignment(cell.ALIGN_BASELINE);
                                        cell.setNoWrap(true); //cell.setWidth("2");
                                        cell.setLeading(9.0f);
                                        part.addCell(cell);

                                        //chk = new Chunk((detailsDOB.getWeightBreak().equalsIgnoreCase("List"))?((rateDetailsDOB.getRateValue()==null || "".equals(rateDetailsDOB.getRateValue()))?"":(formatTotal.format(Double.parseDouble(rateDetailsDOB.getRateValue())))):"",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
                                        chk = new Chunk((detailsDOB.getWeightBreak().equalsIgnoreCase("List"))?((rateDetailsDOB.getRateValue()==null || "".equals(rateDetailsDOB.getRateValue()))?"":(formatTotal.format(Double.parseDouble(rateDetailsDOB.getRateValue())*detailsDOB.getConvFactor()))):"",FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
                                        cell = new Cell(chk);cell.setBorderWidth(1);
                                        cell.setBorderColor(Color.GRAY);
                                        cell.setBackgroundColor(Color.WHITE);
                                        //cell.setHorizontalAlignment(cell.ALIGN_RIGHT);// Commented by subrahmanyam for 178970 on 11-aug-09
                                        cell.setHorizontalAlignment(cell.ALIGN_LEFT);// Added by subrahmanyam for 178970 on 11-aug-09
                                        cell.setVerticalAlignment(cell.ALIGN_BASELINE);
                                        cell.setNoWrap(true); //cell.setWidth("8");
                                        cell.setLeading(9.0f);
                                        part.addCell(cell);
                                         }
                                          }
                                    }
                                    //end of for

                                 }

                            }//end of if

                    }//end of for

                  }

                }//end of for

                  chk = new Chunk("Subtotal B (Base Currency) ",FontFactory.getFont("ARIAL", 7, Font.BOLD,Color.BLACK));
                  cell = new Cell(chk);
                  cell.setBackgroundColor(Color.WHITE);
                  cell.setColspan(7);
                  cell.setLeading(9.0f);
                  cell.setNoWrap(false);
                  cell.setBorderWidth(1);
                  cell.setBorderColor(Color.GRAY);
                  cell.setBorder(Rectangle.TOP | Rectangle.BOTTOM);
                  cell.setHorizontalAlignment(cell.ALIGN_RIGHT);
                  cell.setVerticalAlignment(cell.ALIGN_BOTTOM);
                  part.addCell(cell);

                  chk = new Chunk("=",FontFactory.getFont("ARIAL", 7, Font.BOLD,Color.BLACK));
                  cell = new Cell(chk);
                  cell.setBorderWidth(1);
                  cell.setBorderColor(Color.GRAY);
                  cell.setBorder(Rectangle.TOP | Rectangle.BOTTOM);
                  cell.setLeading(9.0f);
                  cell.setHorizontalAlignment(cell.ALIGN_LEFT);
                  cell.setVerticalAlignment(cell.ALIGN_BOTTOM);
                  part.addCell(cell);

                  chk = new Chunk((costingMasterDOB.getFrtTotal()!=null && costingMasterDOB.getFrtTotal().trim().length()>0)?formatTotal.format(Double.parseDouble(costingMasterDOB.getFrtTotal())):"",FontFactory.getFont("ARIAL", 7, Font.BOLD,Color.BLACK));
                  cell = new Cell(chk);
                  cell.setLeading(9.0f);
                  cell.setBorderWidth(1);
                  cell.setHorizontalAlignment(cell.ALIGN_LEFT);
                  cell.setBorderColor(Color.GRAY);
                  cell.setBorder(Rectangle.TOP | Rectangle.BOTTOM);
                  cell.setVerticalAlignment(cell.ALIGN_BOTTOM);
                  part.addCell(cell);
            }//end of if
          secUnitArray  = null;
          if(destSize>0)
          {
					   for(int m=0;m<destSize;m++)
					   {
                 detailsDOB = (CostingChargeDetailsDOB)costingMasterDOB.getDestinationList().get(m);
                   //@@Modified by kameswari for Surcharge Enhancements
                  //detailsList = (ArrayList)costingMasterDOB.getDestinationList().get(m);
                  //detailsDOB = (CostingChargeDetailsDOB)detailsList.get(0);
                 rateDetails=(ArrayList)detailsDOB.getCostingRateInfoDOB();

                 if("checked".equals(detailsDOB.getChecked()))
                 {
                      //chk = new Chunk(detailsDOB.getChargeDescId()==null?"":detailsDOB.getChargeDescId().trim(),FontFactory.getFont("ARIAL", 9, Font.NORMAL,Color.BLACK));
                    isDestChecked = true;
                    if(!"List".equalsIgnoreCase(detailsDOB.getWeightBreak()))
                    {
                      chk = new Chunk(detailsDOB.getExternalName()!=null?detailsDOB.getExternalName():"",FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
                      cell = new Cell(chk);
                      //cell.setBackgroundColor(Color.lightGray);
                      cell.setHorizontalAlignment(cell.ALIGN_LEFT);
                      cell.setVerticalAlignment(cell.ALIGN_BASELINE);
                      cell.setNoWrap(false);
                      cell.setBorderWidth(1);
                      cell.setBorderColor(Color.GRAY);
                      cell.setLeading(9.0f);
                      //cell.setWidth("20");
                      part.addCell(cell);

                      /*chk = new Chunk((detailsDOB.getBrkPoint()!=null && !"Absolute".equalsIgnoreCase(detailsDOB.getBrkPoint()) && !"Percent".equalsIgnoreCase(detailsDOB.getBrkPoint()))?detailsDOB.getBrkPoint():"",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
                      cell = new Cell(chk);
                      cell.setBackgroundColor(Color.WHITE);
                      cell.setLeading(9.0f);
                      //cell.setWidth("9");
                      cell.setHorizontalAlignment(cell.ALIGN_LEFT);
                      cell.setVerticalAlignment(cell.ALIGN_BASELINE);
                      cell.setNoWrap(true);
                      cell.setBorderWidth(1);
                      part.addCell(cell);*/

                      chk = new Chunk(detailsDOB.getCurrency(),FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
                      cell = new Cell(chk);
                      cell.setBackgroundColor(Color.WHITE);//cell.setWidth("8");
                      cell.setHorizontalAlignment(cell.ALIGN_LEFT);
                      cell.setVerticalAlignment(cell.ALIGN_BASELINE);
                      cell.setLeading(9.0f);
                      cell.setNoWrap(true);
                      cell.setBorderWidth(1);
                      cell.setBorderColor(Color.GRAY);
                      part.addCell(cell);
                      if("Percent".equals(detailsDOB.getWeightBreak()) || "Absolute".equals(detailsDOB.getWeightBreak()))
                      {
                          rateDetailsDOB =  (CostingRateInfoDOB)rateDetails.get(0);
                          chk = new Chunk(deciFormat.format(rateDetailsDOB.getRate())+("Percent".equalsIgnoreCase(detailsDOB.getWeightBreak())?"%":""),FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
                          cell = new Cell(chk);
                          cell.setLeading(9.0f);
                          //cell.setBackgroundColor(Color.lightGray);
                          cell.setHorizontalAlignment(cell.ALIGN_LEFT);
                          cell.setVerticalAlignment(cell.ALIGN_BASELINE);
                          //cell.setWidth("8");
                          cell.setBorderWidth(1);
                          cell.setBorderColor(Color.GRAY);
                          cell.setNoWrap(true);
                          part.addCell(cell);
                      }
                      else
                      {
                        if(detailsDOB.getWeightBreak()!=null && detailsDOB.getWeightBreak().endsWith("%") && !"Per Shipment".equalsIgnoreCase(detailsDOB.getChargeBasis()))
                          percentFlag = "%";
                        else
                          percentFlag = "";

                        chk = new Chunk(deciFormat.format(Double.parseDouble(detailsDOB.getRate()!=null?detailsDOB.getRate():"0"))+percentFlag,FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
                        cell = new Cell(chk);
                        //cell.setBackgroundColor(Color.lightGray);
                        cell.setHorizontalAlignment(cell.ALIGN_LEFT);
                        cell.setVerticalAlignment(cell.ALIGN_BASELINE);
                        cell.setBorderWidth(1);
                        cell.setBorderColor(Color.GRAY);
                        cell.setLeading(9.0f);
                        cell.setNoWrap(true); //cell.setWidth("8");
                        part.addCell(cell);
                      }

                      chk = new Chunk(detailsDOB.getChargeBasis()!=null?detailsDOB.getChargeBasis():"",FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
                      cell = new Cell(chk);
                      cell.setHorizontalAlignment(cell.ALIGN_LEFT);
                      cell.setVerticalAlignment(cell.ALIGN_BASELINE);
                      cell.setBorderWidth(1);
                      cell.setBorderColor(Color.GRAY);
                      cell.setLeading(9.0f);
                      //cell.setBackgroundColor(Color.lightGray);//cell.setWidth("15");

                      part.addCell(cell);

                      chk = new Chunk((detailsDOB.getDensityRatio()!=null && !"".equals(detailsDOB.getDensityRatio())  ) ?detailsDOB.getDensityRatio() :"",FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
                      cell = new Cell(chk);
                      cell.setHorizontalAlignment(cell.ALIGN_LEFT);
                      cell.setVerticalAlignment(cell.ALIGN_BASELINE);
                      cell.setBackgroundColor(Color.WHITE);//cell.setWidth("8");
                      cell.setNoWrap(true);
                      cell.setLeading(9.0f);
                      cell.setBorderWidth(1);
                      cell.setBorderColor(Color.GRAY);
                      part.addCell(cell);

                      chk = new Chunk((detailsDOB.getPrimaryUnitValue()!=null && detailsDOB.getPrimaryUnitValue().trim().length()!=0)?deciFormat.format(Double.parseDouble(detailsDOB.getPrimaryUnitValue())):"",FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
                      cell = new Cell(chk);
                      cell.setHorizontalAlignment(cell.ALIGN_LEFT);
                      cell.setVerticalAlignment(cell.ALIGN_BASELINE);
                      cell.setBackgroundColor(Color.WHITE);//cell.setWidth("8");
                      cell.setNoWrap(false); cell.setBorderWidth(1);
                      cell.setBorderColor(Color.GRAY);
                      cell.setLeading(9.0f);
                      part.addCell(cell);

                      if(detailsDOB.getSecondaryBasis()!=null && detailsDOB.getSecondaryBasis().trim().length()>0)
                      {
                          chk = new Chunk((detailsDOB.getSecUnitValue()!=null && detailsDOB.getSecUnitValue().trim().length()!=0)?deciFormat.format(Double.parseDouble(detailsDOB.getSecUnitValue())):"",FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
                          cell = new Cell(chk);
                          cell.setBackgroundColor(Color.WHITE);
                          cell.setHorizontalAlignment(cell.ALIGN_LEFT);//cell.setWidth("8");
                          cell.setVerticalAlignment(cell.ALIGN_BASELINE);
                          cell.setNoWrap(false); cell.setBorderWidth(1);
                          cell.setBorderColor(Color.GRAY);
                          cell.setLeading(9.0f);
                          part.addCell(cell);
                      }
                      else
                      {
                          chk = new Chunk("",FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
                          cell = new Cell(chk);
                          cell.setBackgroundColor(Color.WHITE);
                          cell.setHorizontalAlignment(cell.ALIGN_LEFT);//cell.setWidth("8");
                          cell.setVerticalAlignment(cell.ALIGN_BASELINE);
                          cell.setNoWrap(false); cell.setBorderWidth(1);
                          cell.setBorderColor(Color.GRAY);
                          cell.setLeading(9.0f);
                          part.addCell(cell);
                      }

                      chk = new Chunk("=",FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
                      cell = new Cell(chk);
                      cell.setBackgroundColor(Color.WHITE);
                      cell.setHorizontalAlignment(cell.ALIGN_LEFT);//cell.setWidth("2");
                      cell.setVerticalAlignment(cell.ALIGN_BASELINE);
                      cell.setNoWrap(true); cell.setBorderWidth(1);
                      cell.setBorderColor(Color.GRAY);
                      cell.setLeading(9.0f);
                      part.addCell(cell);

                     // chk = new Chunk((detailsDOB.getRateValue()!=null && detailsDOB.getRateValue().trim().length()>0)?formatTotal.format(Double.parseDouble(detailsDOB.getRateValue())):"",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
                       chk = new Chunk((detailsDOB.getRateValue()!=null && detailsDOB.getRateValue().trim().length()>0)?formatTotal.format(Double.parseDouble(detailsDOB.getRateValue())*detailsDOB.getConvFactor()):"",FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
                        cell = new Cell(chk);
                      //cell.setBackgroundColor(Color.lightGray);//cell.setWidth("8");
                      cell.setHorizontalAlignment(cell.ALIGN_LEFT);
                      cell.setVerticalAlignment(cell.ALIGN_BASELINE);
                      cell.setLeading(9.0f);
                      cell.setBorderWidth(1);
                      cell.setBorderColor(Color.GRAY);
                      cell.setNoWrap(true);
                      part.addCell(cell);

                    }
                    else
                    {
                        primaryUnitArray  = detailsDOB.getPrimaryUnitArray();
                        secUnitArray      = detailsDOB.getSecUnitArray();

                        /*chk = new Chunk(detailsDOB.getExternalName()!=null?detailsDOB.getExternalName():"",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
                        cell = new Cell(chk);
                        cell.setBackgroundColor(Color.lightGray);
                        cell.setHorizontalAlignment(cell.ALIGN_CENTER);
                        cell.setVerticalAlignment(cell.ALIGN_BASELINE);
                        cell.setNoWrap(true);
                        cell.setBorderWidth(1);
                        cell.setLeading(9.0f);
                        //cell.setWidth("20");
                        part.addCell(cell);

                        chk = new Chunk("",FontFactory.getFont("ARIAL", 9, Font.NORMAL,Color.BLACK));
                        cell = new Cell(chk);
                        cell.setBackgroundColor(Color.WHITE);//cell.setWidth("9");
                        cell.setHorizontalAlignment(cell.ALIGN_CENTER);
                        cell.setVerticalAlignment(cell.ALIGN_BASELINE);
                        cell.setNoWrap(false);
                        cell.setBorderWidth(1);
                        cell.setLeading(9.0f);
                        part.addCell(cell);

                        chk = new Chunk(detailsDOB.getCurrency(),FontFactory.getFont("ARIAL", 9, Font.NORMAL,Color.BLACK));
                        cell = new Cell(chk);
                        cell.setBackgroundColor(Color.WHITE);
                        //cell.setWidth("8");
                        cell.setHorizontalAlignment(cell.ALIGN_CENTER);
                        cell.setVerticalAlignment(cell.ALIGN_BASELINE);
                        cell.setNoWrap(true); cell.setBorderWidth(1);
                        cell.setLeading(9.0f);
                        part.addCell(cell);

                        chk = new Chunk("",FontFactory.getFont("ARIAL", 9, Font.NORMAL,Color.BLACK));
                        cell = new Cell(chk);
                        cell.setHorizontalAlignment(cell.ALIGN_RIGHT);
                        cell.setVerticalAlignment(cell.ALIGN_BASELINE);
                        cell.setBackgroundColor(Color.lightGray);//cell.setWidth("8");
                        cell.setNoWrap(true);
                        cell.setLeading(9.0f);
                        cell.setBorderWidth(1);
                        part.addCell(cell);

                        //chk = new Chunk(detailsDOB.getChargeBasisDesc(),FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
                        chk = new Chunk("PER CONTAINER",FontFactory.getFont("ARIAL", 9, Font.NORMAL,Color.BLACK));
                        cell = new Cell(chk);
                        cell.setHorizontalAlignment(cell.ALIGN_CENTER);
                        cell.setVerticalAlignment(cell.ALIGN_BASELINE);
                        cell.setBackgroundColor(Color.lightGray);
                        cell.setBorderWidth(1);
                        cell.setLeading(9.0f);
                        //cell.setWidth("15");
                        part.addCell(cell);

                        chk = new Chunk("",FontFactory.getFont("ARIAL", 9, Font.NORMAL,Color.BLACK));
                        cell = new Cell(chk);
                        cell.setHorizontalAlignment(cell.ALIGN_CENTER);
                        cell.setVerticalAlignment(cell.ALIGN_BASELINE);
                        cell.setBackgroundColor(Color.WHITE);
                        cell.setLeading(9.0f);
                        //cell.setWidth("8");
                        cell.setNoWrap(true); cell.setBorderWidth(1);
                        part.addCell(cell);

                        chk = new Chunk("",FontFactory.getFont("ARIAL", 9, Font.NORMAL,Color.BLACK));
                        cell = new Cell(chk);
                        cell.setBackgroundColor(Color.WHITE);
                        //cell.setWidth("8");
                        cell.setHorizontalAlignment(cell.ALIGN_RIGHT);
                        cell.setVerticalAlignment(cell.ALIGN_BASELINE);
                        cell.setLeading(9.0f);
                        cell.setNoWrap(false); cell.setBorderWidth(1);
                        part.addCell(cell);

                        chk = new Chunk("",FontFactory.getFont("ARIAL", 9, Font.NORMAL,Color.BLACK));
                        cell = new Cell(chk);
                        cell.setBackgroundColor(Color.WHITE);
                        //cell.setWidth("8");
                        cell.setHorizontalAlignment(cell.ALIGN_RIGHT);
                        cell.setVerticalAlignment(cell.ALIGN_BASELINE);
                        cell.setLeading(9.0f);
                        cell.setNoWrap(false); cell.setBorderWidth(1);
                        part.addCell(cell);

                        ///min
                        chk = new Chunk("",FontFactory.getFont("ARIAL", 9, Font.NORMAL,Color.BLACK));
                        cell = new Cell(chk);
                        cell.setBackgroundColor(Color.WHITE);//cell.setWidth("2");
                        cell.setHorizontalAlignment(cell.ALIGN_CENTER);
                        cell.setVerticalAlignment(cell.ALIGN_BASELINE);
                        cell.setLeading(9.0f);
                        cell.setNoWrap(true); cell.setBorderWidth(1);
                        part.addCell(cell);

                        chk = new Chunk("",FontFactory.getFont("ARIAL", 9, Font.NORMAL,Color.BLACK));
                        cell = new Cell(chk);
                        cell.setBackgroundColor(Color.WHITE);
                        cell.setHorizontalAlignment(cell.ALIGN_RIGHT);
                        cell.setVerticalAlignment(cell.ALIGN_BASELINE);
                        cell.setBorderWidth(1);
                        cell.setLeading(9.0f);
                        cell.setNoWrap(true); //cell.setWidth("8");
                        part.addCell(cell); */

                        chk = new Chunk(detailsDOB.getExternalName()!=null?detailsDOB.getExternalName():"",FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
                        cell = new Cell(chk);
                        //cell.setBackgroundColor(Color.lightGray);
                        cell.setHorizontalAlignment(cell.ALIGN_LEFT);
                        cell.setVerticalAlignment(cell.ALIGN_BASELINE);
                        cell.setNoWrap(true);
                        cell.setBorderWidth(1);
                        cell.setBorderColor(Color.GRAY);
                        cell.setLeading(9.0f);
                        cell.setColspan(9);
                        //cell.setWidth("20");
                        part.addCell(cell);
                        int rateDtlSize = rateDetails.size();
                        for(int j=0;j<rateDtlSize;j++)
                        {
                            rateDetailsDOB=(CostingRateInfoDOB)rateDetails.get(j);
                           if(primaryUnitArray[j]!=null&&primaryUnitArray[j].trim().length()!=0)//@@Added by Kameswari for the issue
                           {

                            /*if(j==0)
                            {
                              chk = new Chunk(detailsDOB.getExternalName()!=null?detailsDOB.getExternalName():"",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
                              cell = new Cell(chk);
                              //cell.setBackgroundColor(Color.lightGray);
                              cell.setHorizontalAlignment(cell.ALIGN_LEFT);
                              cell.setVerticalAlignment(cell.ALIGN_BASELINE);
                              cell.setNoWrap(true);
                              cell.setBorderWidth(1);
                              cell.setBorderColor(Color.GRAY);
                              cell.setLeading(9.0f);
                              //cell.setWidth("20");
                              part.addCell(cell);
                            }
                            else
                            {*/
                              chk = new Chunk(rateDetailsDOB.getWeightBreakSlab()!=null?rateDetailsDOB.getWeightBreakSlab().toUpperCase():"",FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
                              cell = new Cell(chk);
                              //cell.setBackgroundColor(Color.lightGray);
                              cell.setHorizontalAlignment(cell.ALIGN_LEFT);
                              cell.setVerticalAlignment(cell.ALIGN_BASELINE);
                              cell.setNoWrap(true);
                              cell.setBorderWidth(1);
                              cell.setBorderColor(Color.GRAY);
                              cell.setLeading(9.0f);
                              //cell.setWidth("20");
                              part.addCell(cell);
                            //}

                            /*chk = new Chunk(rateDetailsDOB.getWeightBreakSlab()!=null?rateDetailsDOB.getWeightBreakSlab().toUpperCase():"",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
                            cell = new Cell(chk);
                            cell.setHorizontalAlignment(cell.ALIGN_CENTER);
                            cell.setVerticalAlignment(cell.ALIGN_BASELINE);
                            cell.setBackgroundColor(Color.WHITE);
                            cell.setNoWrap(true); //cell.setWidth("9");
                            cell.setLeading(9.0f);
                            cell.setBorderWidth(1);
                            part.addCell(cell);*/



                            chk = new Chunk(detailsDOB.getCurrency(),FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
                            cell = new Cell(chk);
                            cell.setBackgroundColor(Color.WHITE);//cell.setWidth("8");
                            cell.setHorizontalAlignment(cell.ALIGN_LEFT);
                            cell.setVerticalAlignment(cell.ALIGN_BASELINE);
                            cell.setNoWrap(true); cell.setBorderWidth(1);
                            cell.setBorderColor(Color.GRAY);
                            cell.setLeading(9.0f);
                            part.addCell(cell);

                            chk = new Chunk(deciFormat.format(rateDetailsDOB.getRate()),FontFactory.getFont("ARIAL",7, Font.NORMAL,Color.BLACK));
                            cell = new Cell(chk);
                            cell.setHorizontalAlignment(cell.ALIGN_LEFT);
                            cell.setVerticalAlignment(cell.ALIGN_BASELINE);
                            //cell.setBackgroundColor(Color.lightGray);
                            cell.setBorderWidth(1);
                            cell.setBorderColor(Color.GRAY);
                            cell.setLeading(9.0f);
                            cell.setNoWrap(false); //cell.setWidth("9");
                            part.addCell(cell);
                            //chk = new Chunk(detailsDOB.getChargeBasisDesc()!=null?detailsDOB.getChargeBasisDesc():"",FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
                           // Logger.info(FILE_NAME,"detailsDOB.getShipmentMode()::"+detailsDOB.getShipmentMode());
                            chk = new Chunk("Per Container",FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
                            cell = new Cell(chk);
                            cell.setHorizontalAlignment(cell.ALIGN_LEFT);
                            cell.setVerticalAlignment(cell.ALIGN_BASELINE);
                            //cell.setBackgroundColor(Color.lightGray);
                            cell.setLeading(9.0f);
                            //cell.setWidth("15");
                            cell.setBorderWidth(1);
                            cell.setBorderColor(Color.GRAY);
                            part.addCell(cell);

                            chk = new Chunk((detailsDOB.getDensityRatio()!=null && !"".equals(detailsDOB.getDensityRatio())  ) ?detailsDOB.getDensityRatio() :"" ,FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
                            cell = new Cell(chk);
                            cell.setHorizontalAlignment(cell.ALIGN_LEFT);
                            cell.setVerticalAlignment(cell.ALIGN_BASELINE);
                            cell.setBackgroundColor(Color.WHITE);//cell.setWidth("8");
                            cell.setNoWrap(true); cell.setBorderWidth(1);
                            cell.setBorderColor(Color.GRAY);
                            cell.setLeading(9.0f);
                            part.addCell(cell);
                          //   chk = new Chunk((primaryUnitArray[j]!=null && primaryUnitArray[j].trim().length()!=0)?deciFormat.format(Double.parseDouble(primaryUnitArray[j])):"",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));//@@Commented by Kameswari for the issue
                           chk = new Chunk(deciFormat.format(Double.parseDouble(primaryUnitArray[j])),FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));//@@Modified by Kameswari for the issue
                              cell = new Cell(chk);
                            cell.setHorizontalAlignment(cell.ALIGN_LEFT);
                            cell.setVerticalAlignment(cell.ALIGN_BASELINE);
                            cell.setBackgroundColor(Color.WHITE);//cell.setWidth("8");
                            cell.setNoWrap(false);
                            cell.setLeading(9.0f);
                            cell.setBorderWidth(1);
                            cell.setBorderColor(Color.GRAY);
                            part.addCell(cell);

                            chk = new Chunk((secUnitArray[j]!=null && secUnitArray[j].trim().length()!=0)?deciFormat.format(Double.parseDouble(secUnitArray[j])):"",FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
                            cell = new Cell(chk);
                            cell.setBackgroundColor(Color.WHITE);//cell.setWidth("8");
                            cell.setHorizontalAlignment(cell.ALIGN_LEFT);
                            cell.setVerticalAlignment(cell.ALIGN_BASELINE);
                            cell.setNoWrap(false);
                            cell.setLeading(9.0f);
                            part.addCell(cell);
                            chk = new Chunk((detailsDOB.getWeightBreak().equalsIgnoreCase("List"))?((rateDetailsDOB.getRateValue()==null || "".equals(rateDetailsDOB.getRateValue()))?"":"="):"=",FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
                            cell = new Cell(chk);cell.setBorderWidth(1);
                            cell.setBorderColor(Color.GRAY);
                            cell.setBackgroundColor(Color.WHITE);
                            cell.setHorizontalAlignment(cell.ALIGN_LEFT);
                            cell.setVerticalAlignment(cell.ALIGN_BASELINE);
                            cell.setNoWrap(true); //cell.setWidth("2");
                            cell.setLeading(9.0f);
                            part.addCell(cell);

                           // chk = new Chunk((detailsDOB.getWeightBreak().equalsIgnoreCase("List"))?((rateDetailsDOB.getRateValue()==null || "".equals(rateDetailsDOB.getRateValue()))?"":(formatTotal.format(Double.parseDouble(rateDetailsDOB.getRateValue())))):"",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
                            chk = new Chunk((detailsDOB.getWeightBreak().equalsIgnoreCase("List"))?((rateDetailsDOB.getRateValue()==null || "".equals(rateDetailsDOB.getRateValue()))?"":(formatTotal.format(Double.parseDouble(rateDetailsDOB.getRateValue())*detailsDOB.getConvFactor()))):"",FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
                            cell = new Cell(chk);cell.setBorderWidth(1);
                            cell.setBorderColor(Color.GRAY);
                            cell.setBackgroundColor(Color.WHITE);
                            cell.setHorizontalAlignment(cell.ALIGN_LEFT);
                            cell.setVerticalAlignment(cell.ALIGN_BASELINE);
                            cell.setNoWrap(true); //cell.setWidth("8");
                            cell.setLeading(9.0f);
                            part.addCell(cell);
                           }
                        }//end of for
                    }

                 }//end for if

             }//end for for
                if(isDestChecked)
                  {
                    chk = new Chunk("Subtotal C (Base Currency) ",FontFactory.getFont("ARIAL", 7, Font.BOLD,Color.BLACK));
                    cell = new Cell(chk);
                    cell.setBackgroundColor(Color.WHITE);
                    cell.setColspan(7);
                    cell.setLeading(9.0f);
                    cell.setNoWrap(false);
                    cell.setBorderWidth(1);
                    cell.setBorderColor(Color.GRAY);
                    cell.setBorder(Rectangle.TOP | Rectangle.BOTTOM);
                    cell.setHorizontalAlignment(cell.ALIGN_RIGHT);
                    cell.setVerticalAlignment(cell.ALIGN_BOTTOM);
                    cell.setLeading(9.0f);
                    part.addCell(cell);

                    chk = new Chunk("=",FontFactory.getFont("ARIAL", 7, Font.BOLD,Color.BLACK));
                    cell = new Cell(chk);
                    cell.setBorderWidth(1);
                    cell.setBorderColor(Color.GRAY);
                    cell.setBorder(Rectangle.TOP | Rectangle.BOTTOM);
                    cell.setHorizontalAlignment(cell.ALIGN_LEFT);
                    cell.setVerticalAlignment(cell.ALIGN_BOTTOM);
                    cell.setLeading(9.0f);
                    part.addCell(cell);

                    chk = new Chunk((costingMasterDOB.getDestTotal()!=null && costingMasterDOB.getDestTotal().trim().length()>0)?formatTotal.format(Double.parseDouble(costingMasterDOB.getDestTotal())):"",FontFactory.getFont("ARIAL", 7, Font.BOLD,Color.BLACK));
                    cell = new Cell(chk);
                    cell.setBorderWidth(1);
                    cell.setHorizontalAlignment(cell.ALIGN_LEFT);
                    cell.setBorderColor(Color.GRAY);
                    cell.setBorder(Rectangle.TOP | Rectangle.BOTTOM);
                    cell.setVerticalAlignment(cell.ALIGN_BOTTOM);
                    cell.setLeading(9.0f);
                    part.addCell(cell);
                  }
        }//end for if

      chk = new Chunk("Grand Total ("+(costingHDRDOB.getBaseCurrency()!=null?costingHDRDOB.getBaseCurrency():"")+") ",FontFactory.getFont("ARIAL", 7, Font.BOLD,Color.BLACK));
        cell = new Cell(chk);
        cell.setBackgroundColor(DHLColor);
        cell.setColspan(7);
        //cell.setLeading(12.0f);
        cell.setNoWrap(false);
        cell.setBorderWidth(1);
        cell.setBorder(Rectangle.TOP | Rectangle.BOTTOM);
        cell.setBorderColor(Color.GRAY);
        cell.setHorizontalAlignment(cell.ALIGN_RIGHT);
        cell.setVerticalAlignment(cell.ALIGN_BOTTOM);
        part.addCell(cell);

        chk = new Chunk("=",FontFactory.getFont("ARIAL", 7, Font.BOLD,Color.BLACK));
        cell = new Cell(chk);
        cell.setBackgroundColor(DHLColor);
        cell.setBorderWidth(1);
        cell.setBorderColor(Color.GRAY);
        cell.setBorder(Rectangle.TOP | Rectangle.BOTTOM);
        cell.setHorizontalAlignment(cell.ALIGN_LEFT);
        cell.setVerticalAlignment(cell.ALIGN_BOTTOM);
        //cell.setLeading(9.0f);
        part.addCell(cell);

        chk = new Chunk((costingMasterDOB.getTotal()!=null && costingMasterDOB.getTotal().trim().length()>0)?formatTotal.format(Double.parseDouble(costingMasterDOB.getTotal())):"",FontFactory.getFont("ARIAL", 7, Font.BOLD,Color.BLACK));
        cell = new Cell(chk);
        cell.setBackgroundColor(DHLColor);
        cell.setBorderWidth(1);
        cell.setBorderColor(Color.GRAY);
        cell.setBorder(Rectangle.TOP | Rectangle.BOTTOM);
        if(costingMasterDOB.getTotal().length()>7)
        {
          cell.setHorizontalAlignment(cell.ALIGN_LEFT);

        }
        else
        {
           cell.setHorizontalAlignment(cell.ALIGN_CENTER);

        }
        cell.setVerticalAlignment(cell.ALIGN_BOTTOM);
        //cell.setLeading(9.0f);
        part.addCell(cell);

       document.add(part);
       document.newPage();
        ArrayList   exchangeRatesList   =   costingMasterDOB.getExchangeRatesList();
        int         exchangeListSize    =   0;
        String      currencyConversion  =   null;
        String[]    currencyArray       =   null;
        String      currency            =   "";
        String      rate                =   "";
        String      baseCurrency        =   costingHDRDOB.getBaseCurrency();

        if(exchangeRatesList!=null && exchangeRatesList.size()>0)
        {
            part = new Table(1);
            part.setOffset(15);
            part.setWidth(100);
            part.setPadding(1);
            part.setSpacing(2);
            part.setBackgroundColor(Color.WHITE);
            part.setBorderColor(Color.black);
            
            part.setTableFitsPage(true);
            part.setBorderWidth(1);
            
            chk = new Chunk("Please note this is an estimated costing.",FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
            cell = new Cell(chk);
            cell.setBackgroundColor(Color.WHITE);
            cell.setNoWrap(false);
            cell.setBorderWidth(0);
            cell.setHorizontalAlignment(cell.ALIGN_LEFT);
            cell.setVerticalAlignment(cell.ALIGN_BOTTOM);
            part.addCell(cell);
           
            
            chk = new Chunk("The exchange rates used in this costing are shown below.",FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
            cell = new Cell(chk);
            cell.setBackgroundColor(Color.WHITE);
            cell.setNoWrap(false);
            cell.setBorderWidth(0);
            cell.setHorizontalAlignment(cell.ALIGN_LEFT);
            cell.setVerticalAlignment(cell.ALIGN_BOTTOM);
            part.addCell(cell);

            Paragraph para    =   new Paragraph("These are based on the daily rates in ",FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
            Anchor anchor1    =   new Anchor("www.oanda.com",FontFactory.getFont("ARIAL", 7, Font.UNDERLINE,Color.BLUE));
            anchor1.setReference("http://www.oanda.com");
            para.add(anchor1);
            para.add(" and are subject to fluctuation.");

            //chk = new Chunk("These are based on the daily rates in ",FontFactory.getFont("ARIAL", 8, Font.NORMAL,Color.BLACK));
            cell = new Cell(para);
            cell.setBackgroundColor(Color.WHITE);
            cell.setNoWrap(false);
            cell.setLeading(9.0f);
            cell.setBorderWidth(0);
            cell.setHorizontalAlignment(cell.ALIGN_LEFT);
            cell.setVerticalAlignment(cell.ALIGN_BOTTOM);
            part.addCell(cell);
            /*
            document.add(part);

            
            part = new Table(9);
            part.setWidth(100);
            part.setPadding(1);
            part.setSpacing(0);
            part.setBackgroundColor(Color.WHITE);
            part.setBorderColor(Color.black);
            part.setTableFitsPage(true);
            part.setBorderWidth(0);*/
            //Modified by Rakesh on 27-01-2011 for CR:231219 
            exchangeListSize  = exchangeRatesList.size();
            for(int i=0;i<exchangeListSize;i++)
            {
              currencyConversion  = (String)exchangeRatesList.get(i);

              if(currencyConversion!=null)
                  currencyArray   =   currencyConversion.split("~");

              if(currencyArray!=null)
              {
                currency    =   currencyArray[0];
                rate        =   currencyArray[1];
              }

              chk   =   new Chunk("",FontFactory.getFont("ARIAL",7,Font.NORMAL,Color.BLACK));
              cell = new Cell(chk);
              cell.setBackgroundColor(Color.WHITE);
              cell.setBorderWidth(0);
              cell.setLeading(9.0f);
              part.addCell(cell);

              chk   =   new Chunk((currency!=null?currency:"")+(" - "+baseCurrency)+(rate!=null?(currencyFormat.format(Double.parseDouble("  "+rate))):""),FontFactory.getFont("ARIAL",7,Font.NORMAL,Color.BLACK));
              cell = new Cell(chk);
              cell.setBackgroundColor(Color.WHITE);
              cell.setNoWrap(false);
              cell.setBorderWidth(0);
              cell.setLeading(9.0f);
              cell.setHorizontalAlignment(cell.ALIGN_LEFT);
              //cell.setVerticalAlignment(cell.ALIGN_BOTTOM);
              part.addCell(cell);
              /**
              chk   =   new Chunk(rate!=null?(currencyFormat.format(Double.parseDouble(rate))):"",FontFactory.getFont("ARIAL",7,Font.NORMAL,Color.BLACK));
              cell = new Cell(chk);
              cell.setBackgroundColor(Color.WHITE);
              cell.setNoWrap(false);
              cell.setBorderWidth(0);
              cell.setLeading(9.0f);
              //cell.setColspan(7);
              cell.setHorizontalAlignment(cell.ALIGN_LEFT);
              part.addCell(cell);*/
            }
            document.add(part);
        }


       if(list_exNotes!=null && list_exNotes.size()>0)
				{		int c=1;
					int exNotesSize = list_exNotes.size(); // Added by Gowtham for Costing Report
					for(int x=0;x<exNotesSize;x++)
					{
						
						c = (list_exNotes.get(x)!=null &&!"".equals(list_exNotes.get(x)))? x+1:0;//Modified by silpa.p on 22-06-11 for external notes missing
					}
			if(c>0)
				{
          part  =  new Table(1);
          part.setOffset(0);
          part.setWidth(100);
          part.setPadding(1);
          part.setSpacing(0);
          part.setBackgroundColor(Color.WHITE);
          part.setBorderColor(Color.black);
          part.setTableFitsPage(true);
          part.setBorderWidth(1);//Modified by Mohan for issue no on 02112010 
          chk = new Chunk("External Notes",FontFactory.getFont("ARIAL", 7, Font.BOLD,Color.BLACK));
          cell = new Cell(chk);
          cell.setBackgroundColor(DHLColor);
          cell.setNoWrap(false);
          cell.setBorderWidth(1);
          cell.setBorder(Rectangle.TOP | Rectangle.BOTTOM);
          cell.setBorderColor(Color.GRAY);
          cell.setHorizontalAlignment(cell.ALIGN_LEFT);
          cell.setVerticalAlignment(cell.ALIGN_CENTER);
          part.addCell(cell);          
          //Comment and Modified by Rakesh on 21-03-2011
          for(int i=0;i<c;i++)
		  {
          	//  chk = new Chunk(costingMasterDOB.getExtNotes(),FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
              chk = new Chunk(list_exNotes.get(i).toString(),FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
              cell = new Cell(chk);
              cell.setBackgroundColor(Color.WHITE);       
              cell.setLeading(8.0f);  
              cell.setBorderWidth(1);
              cell.setHorizontalAlignment(cell.ALIGN_LEFT);
              cell.setBorder(Rectangle.TOP | Rectangle.BOTTOM);
              cell.setVerticalAlignment(cell.ALIGN_CENTER);
              cell.setBorderColor(Color.GRAY);
              //End Modified by Mohan for issue no on 02112010
              part.addCell(cell);
          }
          document.add(part);
        }
        }

       //Added by Rakesh on 24-01-2011 for CR:231219
       if(costingMasterDOB.getCostingNotes()!=null && !"".equals(costingMasterDOB.getCostingNotes()))
		{
			//int exNotesSize = list_exNotes.size();
			part  =  new Table(1);
			part.setOffset(0);
			part.setWidth(100);
			part.setPadding(1);
			part.setSpacing(0);
			part.setBackgroundColor(Color.WHITE);
			part.setBorderColor(Color.black);
			part.setTableFitsPage(true);
			part.setBorderWidth(1);
			chk = new Chunk("Additional Notes",FontFactory.getFont("ARIAL", 7, Font.BOLD,Color.BLACK));
			cell = new Cell(chk);
			cell.setBackgroundColor(DHLColor);
			cell.setNoWrap(false);
			cell.setBorderWidth(1);
			cell.setBorder(Rectangle.TOP | Rectangle.BOTTOM);
			cell.setBorderColor(Color.GRAY);
			cell.setHorizontalAlignment(cell.ALIGN_LEFT);
			cell.setVerticalAlignment(cell.ALIGN_CENTER);
			part.addCell(cell);          
			chk = new Chunk(costingMasterDOB.getCostingNotes(),FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
			cell = new Cell(chk);
			cell.setBackgroundColor(Color.WHITE);       
			cell.setLeading(8.0f);  
			cell.setBorderWidth(1);
			cell.setHorizontalAlignment(cell.ALIGN_LEFT);
			cell.setBorder(Rectangle.TOP | Rectangle.BOTTOM);
			cell.setVerticalAlignment(cell.ALIGN_CENTER);
			cell.setBorderColor(Color.GRAY);
			part.addCell(cell);
		
			document.add(part);
		} //Ended by Rakesh on 24-01-2011 for CR:231219
       
       //Added by Rakesh on 24-01-2011 for CR:231219
          
       if(costingHDRDOB.getNotes()!=null && !"".equals(costingHDRDOB.getNotes())) // Added by Gowtham.
		{
			//int exNotesSize = list_exNotes.size();
			part  =  new Table(1);
			part.setOffset(0);
			part.setWidth(100);
			part.setPadding(1);
			part.setSpacing(0);
			part.setBackgroundColor(Color.WHITE);
			part.setBorderColor(Color.black);
			part.setTableFitsPage(true);
			part.setBorderWidth(1);
			chk = new Chunk("Customer Notes",FontFactory.getFont("ARIAL", 7, Font.BOLD,Color.BLACK));
			cell = new Cell(chk);
			cell.setBackgroundColor(DHLColor);
			cell.setNoWrap(false);
			cell.setBorderWidth(1);
			cell.setBorder(Rectangle.TOP | Rectangle.BOTTOM);
			cell.setBorderColor(Color.GRAY);
			cell.setHorizontalAlignment(cell.ALIGN_LEFT);
			cell.setVerticalAlignment(cell.ALIGN_CENTER);
			part.addCell(cell);          
			chk = new Chunk(costingHDRDOB.getNotes(),FontFactory.getFont("ARIAL", 7, Font.NORMAL,Color.BLACK));
			cell = new Cell(chk);
			cell.setBackgroundColor(Color.WHITE);       
			cell.setLeading(8.0f);  
			cell.setBorderWidth(1);
			cell.setHorizontalAlignment(cell.ALIGN_LEFT);
			cell.setBorder(Rectangle.TOP | Rectangle.BOTTOM);
			cell.setVerticalAlignment(cell.ALIGN_CENTER);
			cell.setBorderColor(Color.GRAY);
			part.addCell(cell);
		
			document.add(part);
		} //Ended by Rakesh on 24-01-2011 for CR:231219
       
       if(contents!=null && contents.length>0)
       {
         content  =  new Table(1);
         content.setOffset(5);
         content.setWidth(100);
         content.setPadding(1);
         content.setSpacing(0);
         content.setBackgroundColor(Color.WHITE);
         content.setBorderColor(Color.black);
         content.setBorderWidth(1f);
         content.setTableFitsPage(true);
         Cell  cellContent =  null;
         int hFLen	=	headFoot.length;
         for(int i=0;i<hFLen;i++)
         {
           if(headFoot[i]!=null && "F".equalsIgnoreCase(headFoot[i]))
           {
             chk = new Chunk(contents[i],FontFactory.getFont("ARIAL", 7, Font.ITALIC,Color.BLACK));
             cellContent = new Cell(chk);
             cellContent.setBorder(0);
             cellContent.setLeading(8.0f);
             cellContent.setBackgroundColor(Color.LIGHT_GRAY);
             if("L".equalsIgnoreCase(aligns[i]))
              cellContent.setHorizontalAlignment(cellContent.ALIGN_LEFT);
             else if("C".equalsIgnoreCase(aligns[i]))
              cellContent.setHorizontalAlignment(cellContent.ALIGN_CENTER);
             else if("R".equalsIgnoreCase(aligns[i]))
              cellContent.setHorizontalAlignment(cellContent.ALIGN_RIGHT);                   
             content.addCell(cellContent);                      
           }
         }
         document.add(content);
              }
       
       document.close();
       //System.out.println("After     document Close----------------------------------------->");

        File f = new File("Costing.pdf");
        String space=" ";
        FileOutputStream  fileOutputStream= new FileOutputStream(f);
        baos.writeTo(fileOutputStream);
          //@@Added by Kameswari for the WPBN issue-80440
             PdfReader reader = new PdfReader("Costing.pdf");
            int n = reader.getNumberOfPages();
            File fs = new File(PDF_FILE_NAME);

            // we create a stamper that will copy the document to a new file
            PdfStamper stamp = new PdfStamper(reader, new FileOutputStream(fs));

            // adding some metadata
            // adding content to each page

            int k = 0;
            PdfContentByte under;
            PdfContentByte over=null;
              BaseFont bf = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.EMBEDDED);
            while (k < n) {
            	k++;
            	 	over = stamp.getOverContent(k);
            	over.beginText();
            	over.setFontAndSize(bf, 8);
            	over.setTextMatrix(12, 12);
             	over.showText("page " + k+" of "+n);
              if(k>1)
              {
               over.setFontAndSize(bf, 7);


               over.showText("                                                                                          COSTING BASED ON QUOTE: "+costingHDRDOB.getQuoteid());

//@@Added by subrahmanyam for the WPBN:146452 on 12/12/2008
               over.endText();

               over.beginText();
               over.showText("                                                                                                                     CUSTOMER NAME: "+costingMasterDOB.getCustomerName());


//@@ Ended by subrahmanyam for the WPBM:146452 on 12/12/2008
               }
             	over.endText();
            }
            stamp.close();
               //@@WPBN issue-80440
        if("Y".equalsIgnoreCase(costingMasterDOB.getPrintFlag()))
        {
            session 	      =	request.getSession();
             request.getSession().setAttribute("QuoteOuptutStream",fs);
        }

        if("Y".equalsIgnoreCase(costingMasterDOB.getEmailFlag()) || "Y".equalsIgnoreCase(costingMasterDOB.getFaxFlag()))
            costingMasterDOB    =   getContactPersonDetails(costingMasterDOB);
        File farr[] = new File[1];
        farr[0] = f;
        String  contactName =   "";
        String  body        =   null;
        String  innerBody        =   "";//added by VLAKSHMI for WPBN issue 167673 (CR) on 22/05/2009
         String  outerBody        =   "";
        String                  quoteType         = null;
        subject.append("DHL Global Forwarding Costing,").append(costingMasterDOB.getShipmentMode()).append("Freight ");
        subject.append(costingMasterDOB.getOriginCountryName()).append(" to ").append(costingMasterDOB.getDestCountryName());
        subject.append(", Quote Reference ").append(costingHDRDOB.getQuoteid());

        if("Y".equalsIgnoreCase(costingMasterDOB.getEmailFlag()))
        {
              quoteType  = "C"; //added by VLAKSHMI for WPBN issue 167673 (CR) on 27/04/2009

               body =remote.getEmailText(loginbean.getTerminalId(),quoteType);//added by VLAKSHMI for WPBN issue 167673 (CR) on 27/04/2009
             if(costingMasterDOB.getContactPersonNames()!=null)
          {
            for(int i=0;i<costingMasterDOB.getContactPersonNames().length;i++)
            {
              contactName = costingMasterDOB.getContactPersonNames()[i];
             innerBody ="Dear "+contactName+",\n\n";

             if(body!=null && body.trim().length()>0)
             {
             outerBody=innerBody+body+("\n\n\nRegards,\n"+(costingMasterDOB.getCreatorDetails()!=null?costingMasterDOB.getCreatorDetails():"")+"\n"+(costingMasterDOB.getTerminalAddress()!=null?costingMasterDOB.getTerminalAddress():"")+"\n\n"+"Mobile "+(costingMasterDOB.getMobileNo()!=null?costingMasterDOB.getMobileNo():"")+"\n"+"Fax "+(costingMasterDOB.getFaxNo()!=null?costingMasterDOB.getFaxNo():"")+"\n"+"Phone  "+
             (costingMasterDOB.getPhoneNo()!=null?costingMasterDOB.getPhoneNo():"")+"\n\n"+"EmailId "+(costingMasterDOB.getFromMailId()!=null?costingMasterDOB.getFromMailId():""));
             }
           else
           {
      outerBody = "Dear "+contactName+",\n\nThank you for the opportunity to provide this Costing. All information is contained within the attachment."+
                    " Should you have any queries, please do not hesitate to contact us.\n\n\nRegards,\n"+(costingMasterDOB.getCreatorDetails()!=null?costingMasterDOB.getCreatorDetails():"")+"\n"+(costingMasterDOB.getTerminalAddress()!=null?costingMasterDOB.getTerminalAddress():"")+"\n\n"+"Mobile "+(costingMasterDOB.getMobileNo()!=null?costingMasterDOB.getMobileNo():"")+"\n"+"Fax "+(costingMasterDOB.getFaxNo()!=null?costingMasterDOB.getFaxNo():"")+"\n"+"Phone  "+(costingMasterDOB.getPhoneNo()!=null?costingMasterDOB.getPhoneNo():"")+"\n\n"+"EmailId "+(costingMasterDOB.getFromMailId()!=null?costingMasterDOB.getFromMailId():"");
               }
              //end for WPBN issue 167673 (CR) on 27/04/2009
              try
              {
                sendMail(costingMasterDOB.getFromMailId(),costingMasterDOB.getContactEmailIds()[i],subject.toString(),outerBody,PDF_FILE_NAME,farr);
                sentList.add(costingMasterDOB.getContactEmailIds()[i]);
              }
              catch(Exception e)
              {
                e.printStackTrace();
                unsentList.add(costingMasterDOB.getContactEmailIds()[i]);
              }

            }
            //@@ Added by subrahmanyam for the Enhancement 146444 on 10/02/09  & 20-apr-09
              if(costingHDRDOB.getSalesPersonEmail()!=null&&costingHDRDOB.getSalesPersonEmail().length()>0)
			  {
             innerBody ="Dear SalesPerson,\n\n";

             if(body!=null && body.trim().length()>0)
             {
             outerBody=innerBody+body+("\n\n\nRegards,\n"+(costingMasterDOB.getCreatorDetails()!=null?costingMasterDOB.getCreatorDetails():"")+"\n"+(costingMasterDOB.getTerminalAddress()!=null?costingMasterDOB.getTerminalAddress():"")+"\n\n"+"Mobile "+(costingMasterDOB.getMobileNo()!=null?costingMasterDOB.getMobileNo():"")+"\n"+"Fax "+(costingMasterDOB.getFaxNo()!=null?costingMasterDOB.getFaxNo():"")+"\n"+"Phone  "+(costingMasterDOB.getPhoneNo()!=null?costingMasterDOB.getPhoneNo():"")+"\n\n"+"EmailId "+(costingMasterDOB.getFromMailId()!=null?costingMasterDOB.getFromMailId():""));
             }
                else
                 {
                   outerBody = "Dear SalesPerson,\n\nThank you for the opportunity to provide this Costing. All information is contained within the attachment."+
                     " Should you have any queries, please do not hesitate to contact us.\n\n\nRegards,\n"+(costingMasterDOB.getCreatorDetails()!=null?costingMasterDOB.getCreatorDetails():"")+"\n"+(costingMasterDOB.getTerminalAddress()!=null ? costingMasterDOB.getTerminalAddress():"")+"\n\n"+"Mobile "+(costingMasterDOB.getMobileNo()!=null ? costingMasterDOB.getMobileNo():"")+"\n"+"Fax "+(costingMasterDOB.getFaxNo()!=null?costingMasterDOB.getFaxNo():"")+"\n"+"Phone  "+(costingMasterDOB.getPhoneNo()!=null?costingMasterDOB.getPhoneNo():"")+"\n\n"+"EmailId "+(costingMasterDOB.getFromMailId()!=null?costingMasterDOB.getFromMailId():"");
                 }
                salesPersonEmailList = costingHDRDOB.getSalesPersonEmail().split(";");
                int emailListLen	= salesPersonEmailList.length;
                  for(int i=0;i<emailListLen;i++)
                  {
                    sentList.add(salesPersonEmailList[i]);
                   sendMail(costingMasterDOB.getFromMailId(),salesPersonEmailList[i],subject.toString(),outerBody,PDF_FILE_NAME,farr);
                  }

			  }
			  //if(costingHDRDOB.getSalesPersonEmail().length()!=0)

//@@ Ended by subrahmanyam for the Enhancement 146444 on 10/02/09  & 20-apr-09
          }
          else
          {

				   sentList.add(costingHDRDOB.getSalesPersonEmail());
                 innerBody ="Dear  Customer,\n\n";
             if(body!=null && body.trim().length()>0)
             {
             outerBody=innerBody+body+("\n\n\nRegards,\n"+(costingMasterDOB.getCreatorDetails()!=null?costingMasterDOB.getCreatorDetails():"")+"\n"+(costingMasterDOB.getTerminalAddress()!=null?costingMasterDOB.getTerminalAddress():"")+"\n\n"+"Mobile "+(costingMasterDOB.getMobileNo()!=null?costingMasterDOB.getMobileNo():"")+"\n"+"Fax "+(costingMasterDOB.getFaxNo()!=null?costingMasterDOB.getFaxNo():"")+"\n"+"Phone  "+(costingMasterDOB.getPhoneNo()!=null?costingMasterDOB.getPhoneNo():"")+"\n\n"+"EmailId "+(costingMasterDOB.getFromMailId()!=null?costingMasterDOB.getFromMailId():""));
             }
              else
               {
                  outerBody = "Dear Customer,\n\nThank you for the opportunity to provide this Costing. All information is contained within the attachment."+
                     " Should you have any queries, please do not hesitate to contact us.\n\n\nRegards,\n"+(costingMasterDOB.getCreatorDetails()!=null?costingMasterDOB.getCreatorDetails():"")+"\n"+(costingMasterDOB.getTerminalAddress()!=null ? costingMasterDOB.getTerminalAddress():"")+"\n\n"+"Mobile "+(costingMasterDOB.getMobileNo()!=null ? costingMasterDOB.getMobileNo():"")+"\n"+"Fax "+(costingMasterDOB.getFaxNo()!=null?costingMasterDOB.getFaxNo():"")+"\n"+"Phone  "+(costingMasterDOB.getPhoneNo()!=null?costingMasterDOB.getPhoneNo():"")+"\n\n"+"EmailId "+(costingMasterDOB.getFromMailId()!=null?costingMasterDOB.getFromMailId():"");
               }

            try
            {
              sendMail(costingMasterDOB.getFromMailId(),costingMasterDOB.getEmailId(),subject.toString(),outerBody,PDF_FILE_NAME,farr);
              sentList.add(costingMasterDOB.getEmailId());
 //@@ Added by subrahmanyam for the Enhancement 146444 on  10/02/09 & 20-apr-09
              if(costingHDRDOB.getSalesPersonEmail()!=null&&costingHDRDOB.getSalesPersonEmail().length()>0)
              {
               innerBody ="Dear SalesPerson,\n\n";
             if(body!=null && body.trim().length()>0)
             {
             outerBody=innerBody+body+("\n\n\nRegards,\n"+(costingMasterDOB.getCreatorDetails()!=null?costingMasterDOB.getCreatorDetails():"")+"\n"+(costingMasterDOB.getTerminalAddress()!=null?costingMasterDOB.getTerminalAddress():"")+"\n\n"+"Mobile "+(costingMasterDOB.getMobileNo()!=null?costingMasterDOB.getMobileNo():"")+"\n"+"Fax "+(costingMasterDOB.getFaxNo()!=null?costingMasterDOB.getFaxNo():"")+"\n"+"Phone  "+(costingMasterDOB.getPhoneNo()!=null?costingMasterDOB.getPhoneNo():"")+"\n\n"+"EmailId "+(costingMasterDOB.getFromMailId()!=null?costingMasterDOB.getFromMailId():""));
             }
				  else
           {
				   outerBody = "Dear SalesPerson,\n\nThank you for the opportunity to provide this Costing. All information is contained within the attachment."+
                     " Should you have any queries, please do not hesitate to contact us.\n\n\nRegards,\n"+(costingMasterDOB.getCreatorDetails()!=null?costingMasterDOB.getCreatorDetails():"")+"\n"+(costingMasterDOB.getTerminalAddress()!=null ? costingMasterDOB.getTerminalAddress():"")+"\n\n"+"Mobile "+(costingMasterDOB.getMobileNo()!=null ? costingMasterDOB.getMobileNo():"")+"\n"+"Fax "+(costingMasterDOB.getFaxNo()!=null?costingMasterDOB.getFaxNo():"")+"\n"+"Phone  "+(costingMasterDOB.getPhoneNo()!=null?costingMasterDOB.getPhoneNo():"")+"\n\n"+"EmailId "+(costingMasterDOB.getFromMailId()!=null?costingMasterDOB.getFromMailId():"");
           }
                   salesPersonEmailList = costingHDRDOB.getSalesPersonEmail().split(";");
                   int emailListLen	=	 salesPersonEmailList.length;
                  for(int i=0;i<emailListLen;i++)
                  {
                    sentList.add(salesPersonEmailList[i]);
                   sendMail(costingMasterDOB.getFromMailId(),salesPersonEmailList[i],subject.toString(),outerBody,PDF_FILE_NAME,farr);
                  }
              }
//@@ Ended by subrahmanyam for the Enhancement 146444 on 10/02/09 & 20-apr-09

            }
            catch(Exception e)
            {
              e.printStackTrace();
              unsentList.add(costingMasterDOB.getEmailId());
            }
          }
       }

       if("Y".equalsIgnoreCase(costingMasterDOB.getFaxFlag()))
       {
          StringBuffer  sb  = null;
          if(costingMasterDOB.getContactPersonNames()!=null)
          {
            for(int i=0;i<costingMasterDOB.getContactPersonNames().length;i++)
            {
              sb  =   new StringBuffer();
              contactName = costingMasterDOB.getContactPersonNames()[i];
                 body = "Dear "+contactName+",\n\nThank you for the opportunity to provide this Costing. All information is contained within the attachment."+
                     " Should you have any queries, please do not hesitate to contact us.\n\n\nRegards,\n"+costingMasterDOB.getCreatorDetails()+"\n"+costingMasterDOB.getTerminalAddress()+"\n\n"+"Mobile "+costingMasterDOB.getMobileNo()+"\n"+"Fax "+costingMasterDOB.getFaxNo()+"\n"+"Phone  "+costingMasterDOB.getPhoneNo()+"\n\n"+"EmailId "+costingMasterDOB.getFromMailId();
             if("SG".equalsIgnoreCase(costingMasterDOB.getCustomerCountryId()))
                    sb.append("fax#").append(costingMasterDOB.getContactsFax()[i]).append("@tcdhl.com");
              else
                    sb.append("ifax#").append(costingMasterDOB.getContactsFax()[i]).append("@tcdhl.com");
              try
              {
                if(costingMasterDOB.getContactsFax()[i]!=null && costingMasterDOB.getContactsFax()[i].trim().length()!=0)
                {
                    sendMail(costingMasterDOB.getFromMailId(),sb.toString(),subject.toString(),body,PDF_FILE_NAME,farr);
                    sentFaxList.add(costingMasterDOB.getContactsFax()[i]);
                }
                else
                  unsentFaxList.add("No Fax Number Provided for Contact Person "+contactName);
              }
              catch(Exception e)
              {
                e.printStackTrace();
                unsentFaxList.add(costingMasterDOB.getContactsFax()[i]);
              }

            }
          }
          else
          {
                body = "Dear Customer ,\n\nThank you for the opportunity to provide this Costing. All information is contained within the attachment."+
                     " Should you have any queries, please do not hesitate to contact us.\n\n\nRegards,\n"+costingMasterDOB.getCreatorDetails()+"\n"+costingMasterDOB.getTerminalAddress()+"\n\n"+"Mobile "+costingMasterDOB.getMobileNo()+"\n"+"Fax "+costingMasterDOB.getFaxNo()+"\n"+"Phone  "+costingMasterDOB.getPhoneNo()+"\n\n"+"EmailId "+costingMasterDOB.getFromMailId();

            sb  =   new StringBuffer();
            if("SG".equalsIgnoreCase(costingMasterDOB.getCustomerCountryId()))
                   sb.append("fax#").append(costingMasterDOB.getCustomerFax()).append("@tcdhl.com");
            else
                   sb.append("ifax#").append(costingMasterDOB.getCustomerFax()).append("@tcdhl.com");
            try
            {
              if(costingMasterDOB.getCustomerFax()!=null && costingMasterDOB.getCustomerFax().trim().length()!=0)
              {
                sendMail(costingMasterDOB.getFromMailId(),sb.toString(),subject.toString(),body,PDF_FILE_NAME,farr);
                sentFaxList.add(costingMasterDOB.getCustomerFax());
              }
              else
                unsentFaxList.add("No Fax Number Provided!");
            }
            catch(Exception e)
            {
              e.printStackTrace();
              unsentFaxList.add(costingMasterDOB.getCustomerFax());
            }
          }
       }
        returnList.add(sentList);
        returnList.add(unsentList);
        returnList.add(sentFaxList);
        returnList.add(unsentFaxList);
    }
    catch(Exception e)
    {
      e.printStackTrace();
      throw new FoursoftException();
    }
    return returnList;
  }
  /**
   * This Method is used to fetch the Contact Person Details like Email Ids & Fax Numbers.
   * @throws java.io.IOException
   * @throws javax.servlet.ServletException
   * @return CostingMasterDOB
   * @param costingMasterDOB
   */
  private CostingMasterDOB getContactPersonDetails(CostingMasterDOB costingMasterDOB)throws ServletException,IOException
  {
    QMSQuoteSessionHome home      =     null;
    QMSQuoteSession     remote    =     null;

    try
    {
        home    =     (QMSQuoteSessionHome)LookUpBean.getEJBHome("QMSQuoteSessionBean");
        remote  =     (QMSQuoteSession)home.create();

        costingMasterDOB    =   remote.getContactPersonDetails(costingMasterDOB);
    }
    catch (Exception e)
    {
      e.printStackTrace();
      logger.error("Error in getContactPersonDetails."+e);
      //throw new FoursoftException();
    }
    return costingMasterDOB;
  }
  private ArrayList doCalculate(CostingHDRDOB costingHDRDOB,CostingChargeDetailsDOB costingChargeDetailsDOB)throws Exception
  {

    String primaryUnit = null;
    String secUnit     = null;
    String tertiaryUnit= null;
    OperationsImpl operationsImpl        = null;
    String rateDescription  = null;//@@Added by kameswari for Surchareg Enhancements
    ArrayList rateDescList = new ArrayList();
    ArrayList rateCurrencyList = new ArrayList();//Added by Mohan for issue no.219979 on 10122010
    ArrayList  costingChargeList = new ArrayList();    
    String volumeUom  = null;
    String densityRatio = null;
    ArrayList rateInfo  = null;
    CostingRateInfoDOB  costingRateInfoDOB = null;
    double primaryUnitValue = 0.0;
    double actualWt = 0.0;
    double actualWt_block = 0.0;
    double volume   = 0.0;
    double actualWtSur = 0.0;
    double volumeSur   = 0.0;
    double chargableWt = 0.0;
    double tempWt = 0.0;
    double tempValue = 0.0;
    double base = 0;
    double flat = 0;
    double max  = Double.POSITIVE_INFINITY;
    double min  = 0;
    double basesurcharge = 0;
    double flatsurcharge = 0;
    double minsurcharge  = 0;
    double primaryValue =0;
    double secValue     =0;
    int rateInfoSize =0;
    int temp_value=0;
    int temp_value1=0;
    double flatAdv = 0;
    double prevValue = 0;
    double prevSlabValue = 0;
    double tempAdvValue = 0;
    double flatSlabValues = 0;
    double       convfactorSur = 0;
    double block = 0.0;
    double  temp ;
    double convfactor = 0;
    boolean brkPoint = false;
    String rateIndicator  = null;
    String rateType       = null;
    String prevIndicator = null;
    String weightClass  = null;   
    DecimalFormat   deciFormat     = null;       
    ArrayList rateInfoSurcharge = null;
    CostingChargeDetailsDOB costingDetailsDOB = null;
       int c =0;
    String   temp_chgDesc   = null;
    String   chg_primaryunit  = null;
        
    try
    {
      deciFormat = new DecimalFormat("##0.00");

      //System.out.println("in docalculate");
      //System.out.println("break"+costingChargeDetailsDOB.getWeightBreak());
      //System.out.println(""+costingChargeDetailsDOB.getWeightBreak().equalsIgnoreCase("SLAB"));
      primaryUnit = costingChargeDetailsDOB.getPrimaryBasis();
      actualWt  = costingHDRDOB.getActualWeight();
      volume    = costingHDRDOB.getVolume();
      volumeUom = costingHDRDOB.getVolumeUom();
      densityRatio = costingChargeDetailsDOB.getDensityRatio();
      convfactor = (costingChargeDetailsDOB.getConvFactor()>0)?costingChargeDetailsDOB.getConvFactor():1;
      operationsImpl = new OperationsImpl();
      block      = costingChargeDetailsDOB.getBlock();

      //@@Added by Kameswari for the WPBN issue-85518
                if(block>0.0)
                  temp = block;
               else
                 temp=1;
               //@@@WPBN issue-85518
      /**
       * Converting the Volume and Weight to Standard units
       * Weight -- KG
       * Volume -- CBM
       */

        if(volumeUom.equalsIgnoreCase("CFT"))
        {
           volume =  volume/35.31467;
        }
        if(costingHDRDOB.getUom().equalsIgnoreCase("LB"))
        {
          actualWt = actualWt/2.20462;
        }


      /**Getting Chargeable weigth if tertiary basis is Chargeable
       * For Freight charges always the calculation is on chargeable
       * For Cartage charges
       *     if chargeBasis is Weight then Actual
       *     else if chargeBasis is Volume then Chargeable
      */

      if(costingChargeDetailsDOB.getTertiaryBasis()!=null &&
      (costingChargeDetailsDOB.getTertiaryBasis().equalsIgnoreCase("Chargeable") ))
      {

         if(densityRatio!=null && densityRatio.trim().length()>0)
         {
            //tempWt = volume*Double.parseDouble(densityRatio);

           if("LB".equalsIgnoreCase(primaryUnit) || "CFT".equalsIgnoreCase(primaryUnit))
           {
             //Conveting Volume in CBM to KGs using DensityRation(LB/CFT)
             tempWt = volume*(Double.parseDouble(densityRatio)*35.31467/2.20462);
           }
           else
           {
             //Conveting Volume in CBM to KGs using DensityRation(KG/CBM)
             tempWt = volume*Double.parseDouble(densityRatio);
           }
        }
        //Take More Weight as actual weight
        if(tempWt > actualWt)
                  actualWt = tempWt;
      }
      /**
       * Now your ActualWeight is in KGs
       */
        rateInfoSurcharge = costingChargeDetailsDOB.getCostingRateInfoDOB();
      //System.out.println("actualWt:::"+actualWt);

      if(costingChargeDetailsDOB.getWeightBreak().equalsIgnoreCase("FLAT")
      || costingChargeDetailsDOB.getWeightBreak().equalsIgnoreCase("FLAT%"))
      {
           //logger.info("in if.....");
            base    = 0;
            min     = 0;
            flat    = 0;
             max  = Double.POSITIVE_INFINITY;
             costingDetailsDOB = new CostingChargeDetailsDOB();
             primaryUnit = costingChargeDetailsDOB.getPrimaryBasis();

            rateInfo = costingChargeDetailsDOB.getCostingRateInfoDOB();

            int rtInfoSize	= rateInfo.size();
            for(int i=0;i<rtInfoSize;i++)
            {
              costingRateInfoDOB = (CostingRateInfoDOB)rateInfo.get(i);

              //@@Added by kameswari for Surcharge Enhancements
            if("FREIGHT RATE".equalsIgnoreCase(costingRateInfoDOB.getRateDescription())||"".equalsIgnoreCase(costingRateInfoDOB.getRateDescription())||costingRateInfoDOB.getRateDescription()==null)
            {
          // logger.info("in if");
              if(costingRateInfoDOB.getWeightBreakSlab().equalsIgnoreCase("BASE") || "BASIC".equalsIgnoreCase(costingRateInfoDOB.getWeightBreakSlab()))//Basic condition Added by govind for the issue 267801
              {
                base  = costingRateInfoDOB.getRate();
              }else if(costingRateInfoDOB.getWeightBreakSlab().equalsIgnoreCase("MIN"))
              {
                min  = costingRateInfoDOB.getRate();
              }else if(costingRateInfoDOB.getWeightBreakSlab().equalsIgnoreCase("Flat"))
              {
                flat  = costingRateInfoDOB.getRate();
              }else if(costingRateInfoDOB.getWeightBreakSlab().equalsIgnoreCase("MAX"))
              {
                max = costingRateInfoDOB.getRate();
              }
            }
         }

            if(primaryUnit.equalsIgnoreCase("KG"))
            {
              actualWt = actualWt;
             }
            else if(primaryUnit.equalsIgnoreCase("LB"))
            {
                //Converting Actualweight in KGs to LBs.
                actualWt = actualWt*2.20462;
            }else if(primaryUnit.equalsIgnoreCase("Piece"))
            {
              actualWt = costingHDRDOB.getNoOfPieces();
                }else if(primaryUnit.equalsIgnoreCase("CBM") || primaryUnit.equalsIgnoreCase("CFT"))
            {

                  if( "Chargeable".equalsIgnoreCase(costingChargeDetailsDOB.getTertiaryBasis()))
                  {
                      /**
                       * if your primaryUnit is CBM/CFT and chargeable,have to convert our actualwt to volume using densityRatio
                       */
                      if(densityRatio!=null && densityRatio.trim().length()>0)
                        volume = actualWt/Double.parseDouble(densityRatio);

                      /**
                       * If primaryUnit is CFT then
                       *     densityRatio is defined in LB/CFT so in above equation
                       *     volume = KG/(LB/CFT)
                       *            =>KG*CFT/LB
                       *     So convert LBs in denominator to KGs
                       *            =>KG*CFT/(KG/2.20462)
                       *            =>CFT*2.20462
                       * else if primaryUnit is CBM then
                       *    densityRatio is defined in KG/CBM so in above equation
                       *    volume = KG/(KG/CBM)
                       *           =>CBM
                      */
                      if(primaryUnit.equalsIgnoreCase("CFT"))
                        volume   = volume*2.20462;
                      //@@Added by kiran.v for Wpbn Issue
                    /*  if(block>0.0){
                    	  int block1=(int)block;
                      if(volume%block1!=0.0)
                     actualWt_block = ((int)volume/block1)+1;
                      else
                      actualWt_block = (int)volume/block1; 
                      }*/
                  }else
                  {
                      /**
                       * If it is not chargeable then simply convert volume to CFT if primaryUnit is CFT
                       */
                      if(primaryUnit.equalsIgnoreCase("CFT"))
                      {
                        volume = volume*35.31467;

                      }
                  }
                  actualWt = volume;

            }else
            {

              /**
               * If primaryUnit is Otherthan (KG,LB,CBM,CFT,Shipment,Peice)
               * Take the Units from BreakPoint field where user enters the noOfunits
               * If notthing is entered take 1
               *
               * Note: brkPoint is used as flag for this condition
               */

              brkPoint = true;
			  temp = 1;//@@Added by Kameswari for the WPBN issue - 266115  on 19/08/2011
              if(costingChargeDetailsDOB.getBrkPoint()!=null && !"".equals(costingChargeDetailsDOB.getBrkPoint()))
                    actualWt = Double.parseDouble(costingChargeDetailsDOB.getBrkPoint());
              else

               actualWt = 1;

            }

            /*System.out.println("volume::::"+volume);
            System.out.println("actualWt---->"+actualWt);*/

            /**
             * If primaryUnit is Otherthan (KG,LB,CBM,CFT,Shipment,Peice)
             * devide the ActualWt with block as user will treat one block as one unit
             * Eg: actualWt=100kgs,block =8(means 10kgs as on unit)
             *     So,100/8=10Kgs is the Actualwt.
             */

            if(!brkPoint)
            {
              if(block > 0.0)
                actualWt = actualWt/block;
            }

                //actualWt    =   round(actualWt);
                secValue = 1;
                if(costingChargeDetailsDOB.getSecUnitValue()!=null && !"".equals(costingChargeDetailsDOB.getSecUnitValue()))
                {
                  secValue = Double.parseDouble(costingChargeDetailsDOB.getSecUnitValue());
                  if(secValue==0)
                  {
                    secValue=1.0;
                  }
                    costingDetailsDOB.setSecUnitValue(""+secValue);

                }else
                {
                  secValue=1.0;
                  costingDetailsDOB.setSecUnitValue("1");
                }

            /**
             * Getting the actualCost for that actualwt
             */


            if(costingChargeDetailsDOB.getWeightBreak().equalsIgnoreCase("FLAT%"))
              tempValue = base+((flat)/100)*actualWt*secValue;
           else
              tempValue = base+(flat*actualWt*secValue);
            //@@Added by kiran.v for Wpbn Issue
       /*    if( block>0.0 && (primaryUnit.equalsIgnoreCase("CBM") || primaryUnit.equalsIgnoreCase("CFT"))&& "Chargeable".equalsIgnoreCase(costingChargeDetailsDOB.getTertiaryBasis()))
            {
        	   tempValue = base+(flat*actualWt_block*secValue);
            } */
                 
            if(tempValue < min || primaryUnit.equalsIgnoreCase("Shipment"))
            {
              /**
               * if actalCost is lessthan minimum or if primayUnit is shipment we will apply minimun value
               *
               * brkPoint = true then BreakPoint is a text and is editable for value and allows only munbers
               */

              if(!brkPoint)

                 costingDetailsDOB.setBrkPoint("Min");

              costingDetailsDOB.setRate(min+"");
              costingDetailsDOB.setChargeBasis("Per Shipment");
              costingDetailsDOB.setPrimaryUnitValue("1");
              //costingDetailsDOB.setRateValue(deciFormat.format(min*convfactor)+"");//@@Commented and Modified by Kameswari for the WPBN issue-133207
              costingDetailsDOB.setRateValue(deciFormat.format(min)+"");

            }else if(tempValue > max)
            {
              /**
               * if actalCost is greaterthan maximum  we will apply maximun value
               *
               * brkPoint = true then BreakPoint is a text and is editable for value and allows only munbers
               */

                if(!brkPoint)
                  costingDetailsDOB.setBrkPoint("Max");

                costingDetailsDOB.setRate(max+"");
                costingDetailsDOB.setChargeBasis("Per Shipment");
                costingDetailsDOB.setPrimaryUnitValue("1");
               // costingDetailsDOB.setRateValue(deciFormat.format(max*convfactor)); //@@Commented and Modified by Kameswari for the WPBN issue-133207
               costingDetailsDOB.setRateValue(deciFormat.format(max));
            }
            else
            {
              /**
               * if actalCost is greaterthan maximum  we will apply maximun value
               *
               * brkPoint = true then BreakPoint is a text and is editable for value and allows only munbers
               */

               if(!brkPoint)
                costingDetailsDOB.setBrkPoint("Flat");

              costingDetailsDOB.setRate(flat+"");
              costingDetailsDOB.setChargeBasis(costingChargeDetailsDOB.getChargeBasisDesc());
              //costingChargeDetailsDOB.setPrimaryUnitValue(deciFormat.format(actualWt));
              //@@Modified by Kameswari for the WPBN issue-85518
              costingDetailsDOB.setPrimaryUnitValue(deciFormat.format(actualWt*temp));
               //@@WPBN issue-85518
             // costingDetailsDOB.setRateValue( deciFormat.format(tempValue*convfactor));//@@Commented and Modified by Kameswari for the WPBN issue-133207
             costingDetailsDOB.setRateValue( deciFormat.format(tempValue));
            }




      }else if(costingChargeDetailsDOB.getWeightBreak().equalsIgnoreCase("SLAB")
               || costingChargeDetailsDOB.getWeightBreak().equalsIgnoreCase("SLAB%"))
      {

           costingDetailsDOB = new CostingChargeDetailsDOB();
        primaryUnit = costingChargeDetailsDOB.getPrimaryBasis();

            if(primaryUnit.equalsIgnoreCase("KG"))
            {
              actualWt = actualWt;
            }
            else if(primaryUnit.equalsIgnoreCase("LB"))
            {
                actualWt = actualWt*2.20462;
            }else if(primaryUnit.equalsIgnoreCase("Piece"))
            {
              actualWt = costingHDRDOB.getNoOfPieces();
            }else if(primaryUnit.equalsIgnoreCase("CBM") || primaryUnit.equalsIgnoreCase("CFT"))
            {
                  if(costingChargeDetailsDOB.getTertiaryBasis()!=null &&
                  (costingChargeDetailsDOB.getTertiaryBasis().equalsIgnoreCase("Chargeable") ))
                  {

                        if(densityRatio!=null && densityRatio.trim().length()>0)
                          volume = actualWt/Double.parseDouble(densityRatio);

                        if(primaryUnit.equalsIgnoreCase("CFT"))
                          volume = volume*2.20462;

                  }else
                  {
                     /*System.out.println("volume::::"+volume);
                      System.out.println("actualWt---->"+actualWt);
                       */
                      if(primaryUnit.equalsIgnoreCase("CFT"))
                      {
                        volume = volume*35.31467;
                      }
                  }
                  actualWt = volume;

            }else
            {
              brkPoint = true;
			  temp = 1;//@@Added by Kameswari for the WPBN issue - 266115 on 19/08/2011
              if(costingChargeDetailsDOB.getBrkPoint()!=null && !"".equals(costingChargeDetailsDOB.getBrkPoint()))
               actualWt = Double.parseDouble(costingChargeDetailsDOB.getBrkPoint());
              else
               actualWt = 1;
            }

             if(!brkPoint)
            {
              if(block > 0.0)
                actualWt = actualWt/block;
            }
            //actualWt    =   round(actualWt);
             //System.out.println("actualWt2345235::::"+actualWt);
              rateInfo = costingChargeDetailsDOB.getCostingRateInfoDOB();
            rateType = (costingChargeDetailsDOB.getRateType()!=null && !"".equals(costingChargeDetailsDOB.getRateType()))?costingChargeDetailsDOB.getRateType():"SLAB";

            weightClass = (costingChargeDetailsDOB.getWeightClass()!=null && !"".equals(costingChargeDetailsDOB.getWeightClass()))?costingChargeDetailsDOB.getWeightClass():"G";
            rateInfoSize = rateInfo.size();

            double tempActualWt = actualWt;
                   boolean flag = false;
            //for(int i=0;i<rateInfo.size();i++)

            for(int i=0;i<rateInfoSize;i++)
            {

                 costingRateInfoDOB = (CostingRateInfoDOB)rateInfo.get(i);
                 if("FREIGHT RATE".equalsIgnoreCase(costingRateInfoDOB.getRateDescription())
                 ||"".equalsIgnoreCase(costingRateInfoDOB.getRateDescription())||costingRateInfoDOB.getRateDescription()==null)
                {
                 if(costingRateInfoDOB.getWeightBreakSlab().equalsIgnoreCase("BASE") || "BASIC".equalsIgnoreCase(costingRateInfoDOB.getWeightBreakSlab()))//Basic condition Added by govind for the issue 267801
                {
                  base  = costingRateInfoDOB.getRate();
                }else if(costingRateInfoDOB.getWeightBreakSlab().equalsIgnoreCase("MIN"))
                {
                  min  = costingRateInfoDOB.getRate();
                }else if(costingRateInfoDOB.getWeightBreakSlab().equalsIgnoreCase("MAX"))
                {
                  max = costingRateInfoDOB.getRate();
                }else
                {
                   if("W".equals(weightClass))                  {

                     /**
                      * WeightScale Calculation
                      * Eg:
                      * Slab  :  -20   20    40     60     80
                      * LB-UB : 0-20 20-40 40-60  60-80  80-99999
                      * Rate  :   50.5 40.5  30.5   20.5   8.5
                      *
                      *   if actalWt is 45 then
                      *     For first 20 - rate 50.5 will apply
                      *     next      20 - rate 40.5 will apply
                      *     and next  5  - rate 30.5 will apply
                      *
                      *   So, finally the total Equation can be any one of these
                      *   1.RateType=slab
                      *      20*50.5 + 20*40.5 + 5*30.5
                      *   2.RateTyoe=Flat
                      *      50.5 + 40.5 + 30.5
                      *   3.RateType = Both
                      *      50.5 + 20*40.5 + 30.5  where the rates are like ----- 50.5F   40.5S  30.5F
                      *
                      */

                     //@@Modified by Kameswari for the WPBN issue-85518
                         // if(costingRateInfoDOB.getLowerBound() < actualWt)
                      if(costingRateInfoDOB.getLowerBound() < (actualWt*temp))
                      {

                         if((actualWt*temp) <= costingRateInfoDOB.getUpperBound())
                        {
                            rateIndicator = costingRateInfoDOB.getRateIndicator();
                          if(rateType.equalsIgnoreCase("SLAB") || ("Both".equalsIgnoreCase(rateType) &&  rateIndicator!=null && rateIndicator.startsWith("S")))
                          {
                              flat = flat+(costingRateInfoDOB.getRate()*tempActualWt);
                          }
                          else
                          {
                            flat = flat+costingRateInfoDOB.getRate();
                            }
                            if("Customer".equals(costingHDRDOB.getAdvantage()))
                            {
                              /**
                               * for Customer Advantage getting the next weightBreak value if exist
                               */
                              if(i+1 < rateInfoSize)
                                {
                                  costingRateInfoDOB = (CostingRateInfoDOB)rateInfo.get(i+1);
                                  if(!("FUEL SURCHARGE".equalsIgnoreCase(costingRateInfoDOB.getRateDescription())
                            ||"SECURITY SURCHARGE".equalsIgnoreCase(costingRateInfoDOB.getRateDescription())
                            ||"SURCHARGE".equalsIgnoreCase(costingRateInfoDOB.getRateDescription())
                            ||"C.A.F%".equalsIgnoreCase(costingRateInfoDOB.getRateDescription())
                            ||"B.A.F".equalsIgnoreCase(costingRateInfoDOB.getRateDescription())
                            ||"C.S.F".equalsIgnoreCase(costingRateInfoDOB.getRateDescription())))
                            {
                                  if(!"MAX".equalsIgnoreCase(costingRateInfoDOB.getWeightBreakSlab()))
                                  {
                                    prevValue = costingRateInfoDOB.getRate();
                                     prevSlabValue = Double.parseDouble(costingRateInfoDOB.getWeightBreakSlab());
                                     prevIndicator = costingRateInfoDOB.getRateIndicator();
                                  }
                            }
                                }
                            }



                        }else
                        {
                         rateIndicator = costingRateInfoDOB.getRateIndicator();

                          if(rateType.equalsIgnoreCase("SLAB") || ( "Both".equalsIgnoreCase(rateType) && rateIndicator!=null && rateIndicator.startsWith("S")))
                          {
                             flat = flat+(costingRateInfoDOB.getRate()*(costingRateInfoDOB.getUpperBound()-costingRateInfoDOB.getLowerBound()));
                            }else
                          {
                            flat = flat+costingRateInfoDOB.getRate();
                           }
                          tempActualWt = ((actualWt*temp)-costingRateInfoDOB.getUpperBound());
                              }
                      }


                  }else
                  {
                     //@@Modified by Kameswari for the WPBN issue-85518
                      //if(costingRateInfoDOB.getLowerBound() < (tempActualWt) && (tempActualWt) <= costingRateInfoDOB.getUpperBound())

                       if(costingRateInfoDOB.getLowerBound() < (tempActualWt*temp) && (tempActualWt*temp) <= costingRateInfoDOB.getUpperBound())
                      {

                         flat = costingRateInfoDOB.getRate();
                         rateIndicator = costingRateInfoDOB.getRateIndicator();

                          if("Customer".equals(costingHDRDOB.getAdvantage()))
                        {
                          if(i+1 < rateInfoSize)
                            {
                             costingRateInfoDOB = (CostingRateInfoDOB)rateInfo.get(i+1);
                            if(!("FUEL SURCHARGE".equalsIgnoreCase(costingRateInfoDOB.getRateDescription())
                            ||"SECURITY SURCHARGE".equalsIgnoreCase(costingRateInfoDOB.getRateDescription())
                            ||"SURCHARGE".equalsIgnoreCase(costingRateInfoDOB.getRateDescription())
                            ||"C.A.F%".equalsIgnoreCase(costingRateInfoDOB.getRateDescription())
                            ||"B.A.F".equalsIgnoreCase(costingRateInfoDOB.getRateDescription())
                            ||"C.S.F".equalsIgnoreCase(costingRateInfoDOB.getRateDescription())
                            ||(costingRateInfoDOB.getRateDescription().toUpperCase()).contains("FUEL SURCHARGE")
                            ||(costingRateInfoDOB.getRateDescription().toUpperCase()).contains("SECURITY SURCHARGE")))//@@ Added by govind for the issue 262475
                            {
                               if(!"MAX".equalsIgnoreCase(costingRateInfoDOB.getWeightBreakSlab()))
                               {
                                   prevValue = costingRateInfoDOB.getRate();
                                   prevSlabValue = Double.parseDouble(costingRateInfoDOB.getWeightBreakSlab());
                                   prevIndicator = costingRateInfoDOB.getRateIndicator();
                               }
                              
                            }
                            }
                        }

                            //break;
                      }
                  }
                }

              /*  prevValue     = costingRateInfoDOB.getRate();
                prevSlabValue = Double.parseDouble(costingRateInfoDOB.getWeightBreakSlab());*/

            }
            }
            //System.out.println("flat::::::::::::::::::::"+flat);
            /**
             * If calculation is weightScale then the final amount should devided with actualWt to get UnitValue
             */

            if("W".equals(weightClass) && actualWt > 0)
            {
                flat = flat/(actualWt*temp);
                //flat = Double.parseDouble(deciFormat.format(flat));
            }

            secValue = 1;
            if(costingChargeDetailsDOB.getSecUnitValue()!=null && !"".equals(costingChargeDetailsDOB.getSecUnitValue()))
            {
              secValue = Double.parseDouble(costingChargeDetailsDOB.getSecUnitValue());
              if(secValue==0)
              {
                secValue=1.0;
                costingDetailsDOB.setSecUnitValue(""+secValue);
              }
            }else
            {
              costingDetailsDOB.setSecUnitValue("1");
            }

           if("SLAB".equalsIgnoreCase(rateType) || ("Both".equalsIgnoreCase(rateType) &&  rateIndicator!=null && rateIndicator.startsWith("S") ))
           {

               /* if(costingChargeDetailsDOB.getWeightBreak().equalsIgnoreCase("SLAB%"))
                  tempValue = base+((flat)/100)*actualWt;
                else
                  tempValue = base+(flat)*actualWt;*/
                  if(flat>0)
                  {
                 if(costingChargeDetailsDOB.getWeightBreak().equalsIgnoreCase("SLAB%"))
                        tempValue = base+((flat)/100)*actualWt*secValue;
                 else
                        tempValue = base+(flat)*actualWt*secValue;
                  }
                  else
                 {
                  tempValue = base+actualWt*secValue;
                  }



           }else
           {
             tempValue = base+(flat*secValue);
           }
           /**
            * for Customer advantage
            * if the rate for the next weightBreak values is less than the exact rate then this rate should apply
            *
            * costingChargeDetailsDOB.setCustomerAdvFlag(true); is set true if next weigth break value is applied
            */

            if("Customer".equals(costingHDRDOB.getAdvantage()) && prevValue > 0)
            {
              if("SLAB".equalsIgnoreCase(rateType) || (prevIndicator!=null && prevIndicator.startsWith("S") ))
               {

                       if(costingChargeDetailsDOB.getWeightBreak().equalsIgnoreCase("SLAB%"))
                        //@@Modified by Kameswari for the WPBN issue-83827
                       // tempAdvValue = base+((prevValue)/100)*(prevSlabValue+1)*secValue;
                       tempAdvValue = base+((prevValue)/100)*(prevSlabValue)*secValue;
                      else
                       //@@Modified by Kameswari for the WPBN issue-83827
                        //tempAdvValue = base+(prevValue)*(prevSlabValue+1)*secValue;
                        tempAdvValue = base+(prevValue)*(prevSlabValue)*secValue;
                   //@@WPBN issue-83827
               }else
               {
                 tempAdvValue = base+(prevValue*secValue);
               }

               if(tempAdvValue < tempValue)
               {
                  costingDetailsDOB.setCustomerAdvFlag(true);
                 //@@Modified by Kameswari for the WPBN issue-83827
                 // actualWt = prevSlabValue+1;
                 actualWt = prevSlabValue;
                 //@@WPBN issue-83827
                  flat     = prevValue;
                  tempValue = tempAdvValue;
                }else
                  costingDetailsDOB.setCustomerAdvFlag(false);
            }
          System.out.println("flat-----in >>>>"+flat);
           System.out.println("weightclass>>>>>>"+weightClass);
           System.out.println("actualWt----->"+actualWt);
           

           if(tempValue < min || primaryUnit.equalsIgnoreCase("Shipment"))
            {
              if(!brkPoint)
                costingDetailsDOB.setBrkPoint("Min");

              costingDetailsDOB.setRate(min+"");
              costingDetailsDOB.setChargeBasis("Per Shipment");
              costingDetailsDOB.setPrimaryUnitValue("1");
              //costingDetailsDOB.setRateValue(deciFormat.format(min*convfactor));//@@Commented and Modified by Kameswari for the WPBN issue-133207
              costingDetailsDOB.setRateValue(deciFormat.format(min));

            }else if(tempValue > max)
            {

                if(!brkPoint)
                   costingDetailsDOB.setBrkPoint("Max");
                 costingDetailsDOB.setRate(max+"");
                costingDetailsDOB.setChargeBasis("Per Shipment");
                costingDetailsDOB.setPrimaryUnitValue("1");
               // costingDetailsDOB.setRateValue(deciFormat.format(max*convfactor));
               costingDetailsDOB.setRateValue(deciFormat.format(max));
            }
            else
            {
               if(!brkPoint)
                costingDetailsDOB.setBrkPoint("Slab");

           if("SLAB".equalsIgnoreCase(rateType) || ("Both".equalsIgnoreCase(rateType) &&  rateIndicator!=null && rateIndicator.startsWith("S") ))
               {
                 costingDetailsDOB.setRate(deciFormat.format(flat)+"");
               }else
               {
                 costingDetailsDOB.setRate(deciFormat.format(flat)+"");
               }
               costingDetailsDOB.setChargeBasis(costingChargeDetailsDOB.getChargeBasisDesc());
                if("Flat".equalsIgnoreCase(rateType) || ( "Both".equalsIgnoreCase(rateType) && rateIndicator!=null &&  rateIndicator.startsWith("F")) )
               {
                  costingDetailsDOB.setPrimaryUnitValue("1");
               }else
               {
                   //@@Modified by Kameswari for the WPBN issue-85518
                  //costingChargeDetailsDOB.setPrimaryUnitValue(deciFormat.format(actualWt));
                  costingDetailsDOB.setPrimaryUnitValue(deciFormat.format(actualWt*temp));
               }
              /*tempValue = 1;
              if(costingChargeDetailsDOB.getSecUnitValue()!=null && !"".equals(costingChargeDetailsDOB.getSecUnitValue()))
              {
                tempValue = Double.parseDouble(costingChargeDetailsDOB.getSecUnitValue());
              }
              */
               if("SLAB".equalsIgnoreCase(rateType) || ( "Both".equalsIgnoreCase(rateType) &&  rateIndicator!=null &&  rateIndicator.startsWith("S")) )
               {

               /* if(costingChargeDetailsDOB.getWeightBreak().equalsIgnoreCase("SLAB%"))
                  costingChargeDetailsDOB.setRateValue( deciFormat.format(base+((flat)*actualWt*tempValue*convfactor/100)));
                else
                  costingChargeDetailsDOB.setRateValue(deciFormat.format(base+((flat)*actualWt*tempValue*convfactor)));*/

                 // costingDetailsDOB.setRateValue( deciFormat.format(tempValue*convfactor));
                 costingDetailsDOB.setRateValue( deciFormat.format(tempValue));//@@Commented and Modified by Kameswari for the WPBN issue-133207

               }else
               {
                /* if(costingChargeDetailsDOB.getWeightBreak().equalsIgnoreCase("SLAB%"))
                  costingChargeDetailsDOB.setRateValue( deciFormat.format(base + ((flat)*tempValue*convfactor/100)));
                 else
                  costingChargeDetailsDOB.setRateValue( deciFormat.format(base +  ((flat)*tempValue*convfactor)));*/

                  // costingDetailsDOB.setRateValue( deciFormat.format(tempValue*convfactor));//@@Commented and Modified by Kameswari for the WPBN issue-133207
                  costingDetailsDOB.setRateValue( deciFormat.format(tempValue));
               }

                //System.out.println(costingChargeDetailsDOB.getRateValue());
            }

              if(("BOTH".equalsIgnoreCase(rateType) && "F".equalsIgnoreCase(rateIndicator)))
                  costingDetailsDOB.setChargeBasis("Per Shipment");
       }
      else if(costingChargeDetailsDOB.getWeightBreak().equalsIgnoreCase("List"))
      {
         costingDetailsDOB = new CostingChargeDetailsDOB();

         /**
             * If weightBreak is List then for each container user will specify the number of units in primary unit column
             * even for ovePivot also user will specify the pivot value
             *
             */

            /*primaryUnit = costingChargeDetailsDOB.getPrimaryBasis();

            if(primaryUnit.equalsIgnoreCase("LB"))
            {
                actualWt = actualWt*2.20462;
            }else if(primaryUnit.equalsIgnoreCase("CBM") || primaryUnit.equalsIgnoreCase("CFT"))
            {
                  if(primaryUnit.equalsIgnoreCase("CFT"))
                  {
                    volume = volume*35.31467;
                  }
                  actualWt = volume;

            }else
            {
              if(costingChargeDetailsDOB.getBrkPoint()!=null && !"".equals(costingChargeDetailsDOB.getBrkPoint()))
               actualWt = Double.parseDouble(costingChargeDetailsDOB.getBrkPoint());
              else
               actualWt = 1;
            }*/


             //System.out.println("in list");
             rateInfo = costingChargeDetailsDOB.getCostingRateInfoDOB();
             boolean   isPrimaryUnitSelected =  false;
             boolean   isSecUnitSelected     =  false;
             String[]  primaryUnitArray = costingChargeDetailsDOB.getPrimaryUnitArray();
             String[]  secUnitArray     = costingChargeDetailsDOB.getSecUnitArray();
             //System.out.println("primaryUnitArray::"+rateInfo.size()+"primaryUnitArray"+primaryUnitArray.length);



             int rateInfosize = rateInfo.size();
             int count = 0;
            // int temp_count=0;
            // int temp_count1=0;
             for(int k=0;k<rateInfosize;k++)
             {
                  costingRateInfoDOB = (CostingRateInfoDOB)rateInfo.get(k);
             
 if("FREIGHT RATE".equalsIgnoreCase(costingRateInfoDOB.getRateDescription())||"".equalsIgnoreCase(costingRateInfoDOB.getRateDescription())||costingRateInfoDOB.getRateDescription()==null)
            {

                  count++;
                }
             }
              if(primaryUnitArray==null)
              {
                // primaryUnitArray = new String[rateInfosize];
                 primaryUnitArray = new String[rateInfosize];//@@Modified by kameswari for Surcharge Enhancements
                 primaryUnitArray[0] = "1";
                 costingDetailsDOB.setPrimaryUnitArray(primaryUnitArray);
             }
              else
              {
            	  int prmUnitArrLen	= primaryUnitArray.length;	 
                   for(int z=0;z<prmUnitArrLen;z++)
                {
                  if(primaryUnitArray[z]!=null && primaryUnitArray[z].trim().length()!=0)
                    {
                        isPrimaryUnitSelected = true;
                        break;
                    }
                }
             if(!isPrimaryUnitSelected && primaryUnitArray.length > 0)
                    primaryUnitArray[0] = "1";

                costingDetailsDOB.setPrimaryUnitArray(primaryUnitArray);
              }
              //@@Modified by kiran.v on 22/03/2012 for Wpbn Issue - 296832
              for(int k=0;k<rateInfosize;k++){
            	  costingRateInfoDOB = (CostingRateInfoDOB)rateInfo.get(k);
            	if( "BAF".equalsIgnoreCase(costingRateInfoDOB.getRateDescription())|| "CSF".equalsIgnoreCase(costingRateInfoDOB.getRateDescription()) || "PSS".equalsIgnoreCase(costingRateInfoDOB.getRateDescription())){
            		if(!isPrimaryUnitSelected && primaryUnitArray.length > 0)
            		primaryUnitArray[k] = "1";	
          	}
              }
           costingDetailsDOB.setPrimaryUnitArray(primaryUnitArray);
          	//@@Ended by kiran.v
              if(secUnitArray == null)
              {
                secUnitArray    = new String[rateInfosize];//@@Modified by kameswari for Surcharge Enhancements
               //secUnitArray    = new String[count];
                if(costingChargeDetailsDOB.getSecondaryBasis()!=null)
                    secUnitArray[0] = "1";
                costingDetailsDOB.setSecUnitArray(secUnitArray);
              }
              else
              {
                /*for(int z=0;z<secUnitArray.length;z++)
                {
                    if(secUnitArray[z]!=null && secUnitArray[z].trim().length()!=0)
                    {
                        isSecUnitSelected = true;
                        break;
                    }
                }
                if(!isSecUnitSelected && secUnitArray.length > 0 && costingChargeDetailsDOB.getSecondaryBasis()!=null)
                    secUnitArray[0] = "1";*/
                if(secUnitArray.length>0)
                  secUnitArray[0] = "";

                if(primaryUnitArray!=null)
                {
                 int prmUnitArrLen	= primaryUnitArray.length;
                 for(int z=0;z<prmUnitArrLen;z++)
                  {

                    if(primaryUnitArray[z]!=null && primaryUnitArray[z].trim().length()!=0)
                    {
                      if(secUnitArray[z]==null || (secUnitArray[z]!=null && secUnitArray[z].trim().length()==0))
                          secUnitArray[z] = "1";
                    }
                  }

                }

                costingDetailsDOB.setSecUnitArray(secUnitArray);
              }


            for(int i=0;i<rateInfosize;i++)
            {
              costingRateInfoDOB = (CostingRateInfoDOB)rateInfo.get(i);
              //System.out.println(":::::::::: i="+i);
              //System.out.println(i+":::::::::"+primaryUnitArray);
              if("FREIGHT RATE".equalsIgnoreCase(costingRateInfoDOB.getRateDescription())||"".equalsIgnoreCase(costingRateInfoDOB.getRateDescription())||costingRateInfoDOB.getRateDescription()==null)
            {

              primaryValue = (primaryUnitArray[c]!=null && !"".equals(primaryUnitArray[c])?Double.parseDouble(primaryUnitArray[c]):0);
              secValue     = (secUnitArray[c]!=null && !"".equals(secUnitArray[c])?Double.parseDouble(secUnitArray[c]):1);
                    c++;
              if(primaryValue>0 )
              {
                tempValue = primaryValue*secValue*costingRateInfoDOB.getRate();
                //tempValue = primaryValue*costingRateInfoDOB.getRate()*convfactor;//@@Commented and Modified by Kameswari for the WPBN issue-133207
                costingRateInfoDOB.setRateValue(deciFormat.format(tempValue));
              }
              else
                costingRateInfoDOB.setRateValue("");

              rateInfo.remove(i);
              rateInfo.add(i,costingRateInfoDOB);

            }

            }
            costingDetailsDOB.setChargeBasis("Per Container");
         costingDetailsDOB.setCostingRateInfoDOB(rateInfo);

      }else if(costingChargeDetailsDOB.getWeightBreak().equals("Absolute") ||
               costingChargeDetailsDOB.getWeightBreak().equals("Percent")  )
      {

                 costingDetailsDOB = new CostingChargeDetailsDOB();
               flat = 0;

            /**
             * If weightBreak is Absolute then this rate will apply directly
             * else if weight break is percent then
             *   x % of actualWt value where x is the percentage
             */
            /**
             * Modified by Yuvraj as per WPBN Issue No. DHLQMS - 26682
             */
            primaryUnit = costingChargeDetailsDOB.getPrimaryBasis();

            if(primaryUnit.equalsIgnoreCase("KG"))
            {
              actualWt = actualWt;
            }
            else if(primaryUnit.equalsIgnoreCase("LB"))
            {
                actualWt = actualWt*2.20462;
            }
            else if(primaryUnit.equalsIgnoreCase("Piece"))
            {
              actualWt = costingHDRDOB.getNoOfPieces();
            }
            else if(primaryUnit.equalsIgnoreCase("CBM") || primaryUnit.equalsIgnoreCase("CFT"))
            {
                  if(costingChargeDetailsDOB.getTertiaryBasis()!=null &&
                  (costingChargeDetailsDOB.getTertiaryBasis().equalsIgnoreCase("Chargeable") ))
                  {
                     /*if(densityRatio!=null && !"".equals(densityRatio) && densityRatio.indexOf(":")>0)
                        volume = actualWt*((Double.parseDouble(densityRatio.substring(0,densityRatio.indexOf(":"))))/(Double.parseDouble(densityRatio.substring(densityRatio.indexOf(":")+1,densityRatio.length()))));
                        *****/
                        if(densityRatio!=null && densityRatio.trim().length()>0)
                          volume = actualWt/Double.parseDouble(densityRatio);

                        if(primaryUnit.equalsIgnoreCase("CFT"))
                        {
                          volume = volume*2.20462;
                        }
                  }
                  else
                  {
                    if(primaryUnit.equalsIgnoreCase("CFT"))
                    {
                      volume = volume*35.31467;
                    }
                  }
                  actualWt = volume;

            }
            else
            {
              brkPoint = true;
			  temp = 1;//@@Added by Kameswari for the WPBN issue - 266115 on 19/08/2011
              if(costingChargeDetailsDOB.getBrkPoint()!=null && !"".equals(costingChargeDetailsDOB.getBrkPoint()))
               {
                  if(costingChargeDetailsDOB.getPrimaryUnitValue()!=null && costingChargeDetailsDOB.getPrimaryUnitValue().trim().length() != 0)
                   actualWt =  Double.parseDouble(costingChargeDetailsDOB.getPrimaryUnitValue());
                  else
                    actualWt =1 ;
               }
              else
               actualWt = 1;
            }

            if(actualWt==0)
              actualWt = 1;
            if(!brkPoint)
            {
              if(block > 0.0)
                actualWt = actualWt/block;
            }
           // actualWt    =   round(actualWt);
            rateInfo = costingChargeDetailsDOB.getCostingRateInfoDOB();
            costingRateInfoDOB = (CostingRateInfoDOB)rateInfo.get(0);

              flat =     costingRateInfoDOB.getRate();
              costingDetailsDOB.setRate(flat+"");
              costingDetailsDOB.setChargeBasis(costingChargeDetailsDOB.getChargeBasisDesc());
             //@@Modified by Kameswari for the WPBN issue-85518
              //costingChargeDetailsDOB.setPrimaryUnitValue(deciFormat.format(actualWt));
              costingDetailsDOB.setPrimaryUnitValue(deciFormat.format(actualWt*temp));
              tempValue = 1;
              if(costingChargeDetailsDOB.getSecUnitValue()!=null && costingChargeDetailsDOB.getSecUnitValue().trim().length()!=0)
              {
                tempValue = Double.parseDouble(costingChargeDetailsDOB.getSecUnitValue());
                if(tempValue==0)
                  {
                    tempValue=1.0;
                    costingDetailsDOB.setSecUnitValue(""+tempValue);
                  }
              }


                if(costingChargeDetailsDOB.getWeightBreak().equalsIgnoreCase("Percent"))
                  //costingDetailsDOB.setRateValue( deciFormat.format( (flat*actualWt*tempValue*convfactor/100)));//@@Commented and Modified by Kameswari for the WPBN issue-133207
                costingDetailsDOB.setRateValue( deciFormat.format( (flat*actualWt*tempValue/100)));
                else
                {
                  //costingDetailsDOB.setRateValue(deciFormat.format(flat*actualWt*tempValue*convfactor));//@@Commented and Modified by Kameswari for the WPBN issue-133207
                  costingDetailsDOB.setRateValue(deciFormat.format(flat*actualWt*tempValue));
                  //costingChargeDetailsDOB.setRateValue(deciFormat.format(flat*convfactor));
                }


      }
      System.out.println("in absolute : "+costingDetailsDOB.getRateDescription());

      if(brkPoint)//@@Added by Kameswari for the WPBN issue-145724
   {

      costingDetailsDOB.setBrkPoint(costingChargeDetailsDOB.getBrkPoint());//@@Added by Kameswari for the WPBN issue-135557
      }
            costingDetailsDOB.setRateDescription("FREIGHT RATE");
         //    costingDetailsDOB.setConvFactor(costingChargeDetailsDOB.getConvFactor());
            convfactor = operationsImpl.getConvertionFactor(costingChargeDetailsDOB.getCurrency(),costingHDRDOB.getBaseCurrency());
            costingDetailsDOB.setConvFactor(convfactor);
             costingDetailsDOB.setChargeBasisDesc(costingChargeDetailsDOB.getChargeBasisDesc());
             costingDetailsDOB.setDensityRatio(costingChargeDetailsDOB.getDensityRatio());;
             costingDetailsDOB.setChecked("checked");
             costingDetailsDOB.setShipmentMode(costingChargeDetailsDOB.getShipmentMode());
             costingDetailsDOB.setRateType(costingChargeDetailsDOB.getRateType());
             costingDetailsDOB.setPrimaryBasis(costingChargeDetailsDOB.getPrimaryBasis());
             costingDetailsDOB.setSecUnitValue(costingChargeDetailsDOB.getSecUnitValue());
             costingDetailsDOB.setCurrency(costingChargeDetailsDOB.getCurrency());
              costingDetailsDOB.setWeightBreak(costingChargeDetailsDOB.getWeightBreak());
              costingDetailsDOB.setTertiaryBasis(costingChargeDetailsDOB.getTertiaryBasis());
             costingDetailsDOB.setCostingRateInfoDOB(costingChargeDetailsDOB.getCostingRateInfoDOB());
             costingDetailsDOB.setBlock(costingChargeDetailsDOB.getBlock());
           //  costingDetailsDOB.setBrkPoint(costingChargeDetailsDOB.getBrkPoint());
             costingDetailsDOB.setCustomerAdvFlag(costingChargeDetailsDOB.isCustomerAdvFlag());
             costingDetailsDOB.setBuyChargeId(costingChargeDetailsDOB.getBuyChargeId());
            costingDetailsDOB.setWeightClass(costingChargeDetailsDOB.getWeightClass());
             costingDetailsDOB.setTertiaryBasis(costingChargeDetailsDOB.getTertiaryBasis());
             costingDetailsDOB.setSellChargeId(costingChargeDetailsDOB.getSellChargeId());
           costingDetailsDOB.setSecondaryBasis(costingChargeDetailsDOB.getSecondaryBasis());
           costingDetailsDOB.setMarginDiscountType(costingChargeDetailsDOB.getMarginDiscountType());

             costingDetailsDOB.setInternalName(costingChargeDetailsDOB.getInternalName());
             costingDetailsDOB.setExternalName(costingChargeDetailsDOB.getExternalName());
             costingChargeList.add(costingDetailsDOB);

    //    rateInfo = costingChargeDetailsDOB.getCostingRateInfoDOB();
             int rtInfoSize	= rateInfo.size();
        for(int i=0;i<rtInfoSize;i++)
       {

         if(i==0)
          {
             // costingRateInfoDOB = (CostingRateInfoDOB)rateInfo.get(i);
             costingRateInfoDOB = (CostingRateInfoDOB)rateInfoSurcharge.get(i);
             if(costingRateInfoDOB.getRateDescription()!=null)
             {
                    rateDescList.add(costingRateInfoDOB.getRateDescription());
             		rateCurrencyList.add(costingRateInfoDOB.getMutilQuoteCurrency());//Added by Mohan for issue no.219979 on 10122010            
             }
          }
          else
          {
             costingRateInfoDOB = (CostingRateInfoDOB)rateInfoSurcharge.get(i-1);
             rateDescription = costingRateInfoDOB.getRateDescription();

              costingRateInfoDOB = (CostingRateInfoDOB)rateInfoSurcharge.get(i);
            if(rateDescription!=null&&!(rateDescription.equalsIgnoreCase(costingRateInfoDOB.getRateDescription())))
            {  rateDescList.add(costingRateInfoDOB.getRateDescription());
               rateCurrencyList.add(costingRateInfoDOB.getMutilQuoteCurrency());//Modififed by Mohan        
            }

          }

       }
      if(rateDescList!=null)
       {
    	  int rTDescList	= rateDescList.size();
       for(int i=0;i<rTDescList;i++)
       {
           actualWtSur   =  costingHDRDOB.getActualWeight();;
           volumeSur     =  costingHDRDOB.getVolume();
           convfactorSur =  convfactor;
           //@@Added by kiran.v 
           if(costingHDRDOB.getUom().equalsIgnoreCase("LB"))
           {
        	   actualWtSur = actualWtSur/2.20462;
           }
           if(volumeUom.equalsIgnoreCase("CFT"))
           {
        	   volumeSur =  volumeSur/35.31467;
           }
           //@@Ended by kiran.v       
              min  = 0.0;
           flat = 0.0;
           base = 0.0;
           tempWt = 0.0;
          String  tempAbs = "" ;//Added by subrahmanyam for 181430
           primaryUnit = costingChargeDetailsDOB.getPrimaryBasis();
            costingDetailsDOB = new CostingChargeDetailsDOB();
              if(costingChargeDetailsDOB.getTertiaryBasis()!=null &&
      (costingChargeDetailsDOB.getTertiaryBasis().equalsIgnoreCase("Chargeable") ))
      {

         if(densityRatio!=null && densityRatio.trim().length()>0)
         {
            //tempWt = volume*Double.parseDouble(densityRatio);

           if("LB".equalsIgnoreCase(primaryUnit) || "CFT".equalsIgnoreCase(primaryUnit))
           {
             //Conveting Volume in CBM to KGs using DensityRation(LB/CFT)
             tempWt = volume*(Double.parseDouble(densityRatio)*35.31467/2.20462);
           }
           else
           {
             //Conveting Volume in CBM to KGs using DensityRation(KG/CBM)
             tempWt = volume*Double.parseDouble(densityRatio);
           }
        }
        //Take More Weight as actual weight
        if(tempWt > actualWtSur)
                  actualWtSur = tempWt;
      }
              int rtInfoSurCharg		=rateInfoSurcharge.size();	
          /*if( "FUEL SURCHARGE".equalsIgnoreCase((String)rateDescList.get(i))||
            "SECURITY SURCHARGE".equalsIgnoreCase((String)rateDescList.get(i)))
*/ 		  		String surchargeDesc	= "";
				String surChargeFlag	=	"";
              if( "1".equalsIgnoreCase(costingChargeDetailsDOB.getShipmentMode()) && !"FREIGHT RATE".equalsIgnoreCase((String)rateDescList.get(i)) && !"A FREIGHT RATE".equalsIgnoreCase((String)rateDescList.get(i)))
              {
            	  surchargeDesc	=(String)	rateDescList.get(i);
              costingDetailsDOB.setChargeBasis(costingChargeDetailsDOB.getChargeBasis());
              
              for(int k=0;k<rtInfoSurCharg;k++)
              {
            	 
                  costingRateInfoDOB = (CostingRateInfoDOB)rateInfoSurcharge.get(k);
                  if(surchargeDesc.equalsIgnoreCase(costingRateInfoDOB.getRateDescription())){
                  if(costingRateInfoDOB.getWeightBreakSlab().endsWith("FFBASIC") || costingRateInfoDOB.getWeightBreakSlab().endsWith("BASIC"))
                       base          = costingRateInfoDOB.getRate();
                  else if(costingRateInfoDOB.getWeightBreakSlab().endsWith("FFMIN") || costingRateInfoDOB.getWeightBreakSlab().endsWith("MIN") ||
                		  costingRateInfoDOB.getWeightBreakSlab().equalsIgnoreCase("MIN"))
                      min         = costingRateInfoDOB.getRate();
                 else if(costingRateInfoDOB.getWeightBreakSlab().endsWith("FFFLAT")|| costingRateInfoDOB.getWeightBreakSlab().endsWith("FLAT") || costingRateInfoDOB.getWeightBreakSlab().endsWith("M3"))
                      flat           = costingRateInfoDOB.getRate();
                 else if(costingRateInfoDOB.getWeightBreakSlab().endsWith("KG"))
                     flat           = costingRateInfoDOB.getRate();
                 
                 else
                 {
                     if(costingRateInfoDOB.getLowerBound() < (actualWtSur*temp) && (actualWtSur*temp) <= costingRateInfoDOB.getUpperBound())
                     {
                    	 surChargeFlag	=	"Slab";
                        flat = costingRateInfoDOB.getRate();
                        rateIndicator = costingRateInfoDOB.getRateIndicator();
                     }

                 }
              }
               }
             /* if("FUEL SURCHARGE".equalsIgnoreCase((String)rateDescList.get(i)))
             {
              for(int k=0;k<rtInfoSurCharg;k++)
              {
                  costingRateInfoDOB = (CostingRateInfoDOB)rateInfoSurcharge.get(k);
                if("FUEL SURCHARGE".equalsIgnoreCase(costingRateInfoDOB.getRateDescription()))
                {
                  if(costingRateInfoDOB.getWeightBreakSlab().equalsIgnoreCase("FSBASIC"))
                       base          = costingRateInfoDOB.getRate();
                  else if(costingRateInfoDOB.getWeightBreakSlab().equalsIgnoreCase("FSMIN"))
                      min         = costingRateInfoDOB.getRate();
                 else if(costingRateInfoDOB.getWeightBreakSlab().equalsIgnoreCase("FSKG"))
                      flat           = costingRateInfoDOB.getRate();
                 }
               }
              }
              else
              {
               for(int k=0;k<rtInfoSurCharg;k++)
               {
                costingRateInfoDOB = (CostingRateInfoDOB)rateInfoSurcharge.get(k);
                  if("SECURITY SURCHARGE".equalsIgnoreCase(costingRateInfoDOB.getRateDescription()))
                 {
                  if(costingRateInfoDOB.getWeightBreakSlab().equalsIgnoreCase("SSBASIC"))
                  {
                       base           = costingRateInfoDOB.getRate();
                  }
                  else if(costingRateInfoDOB.getWeightBreakSlab().equalsIgnoreCase("SSMIN"))
                       min          = costingRateInfoDOB.getRate();
                  else if(costingRateInfoDOB.getWeightBreakSlab().equalsIgnoreCase("SSKG"))
                       flat         = costingRateInfoDOB.getRate();
                 }
                }
              }*/
              
              
                /* if( "Chargeable".equalsIgnoreCase(costingChargeDetailsDOB.getTertiaryBasis()))
                    {
                         if(densityRatio!=null && densityRatio.trim().length()>0)
                          volumeSur = actualWtSur/Double.parseDouble(densityRatio);
                         if(primaryUnit.equalsIgnoreCase("CFT"))
                          volumeSur   = volumeSur*2.20462;
                    }else
                    {
                         if(primaryUnit.equalsIgnoreCase("CFT"))
                        {
                          volumeSur = volumeSur*35.31467;
                        }
                    }
                    actualWtSur = volumeSur;*/

                  if(!brkPoint)
                {
                  if(block > 0.0)
                    actualWtSur = actualWtSur/block;
                }
                // actualWtSur = round(actualWtSur);
                secValue = 1;
                if(costingChargeDetailsDOB.getSecUnitValue()!=null && !"".equals(costingChargeDetailsDOB.getSecUnitValue()))
                {
                  secValue = Double.parseDouble(costingChargeDetailsDOB.getSecUnitValue());
                  if(secValue==0)
                  {
                    secValue=1.0;
                    costingDetailsDOB.setSecUnitValue(""+secValue);
                  }
                }else
                {
                  costingDetailsDOB.setSecUnitValue("1");
                }
                 tempValue = base+(flat*actualWtSur*secValue);
                 if(min==0.0&&flat==0.0)
                 {
                  costingDetailsDOB.setBrkPoint("Basic");
                   costingDetailsDOB.setRate(base+"");
                   costingDetailsDOB.setChargeBasis("Per Shipment");
                   costingDetailsDOB.setPrimaryUnitValue("1");
                   //costingDetailsDOB.setRateValue( deciFormat.format(base*convfactorSur));//@@Commented and Modified by Kameswari for the WPBN issue-133207
                   costingDetailsDOB.setRateValue( deciFormat.format(base));
                   costingDetailsDOB.setRateDescription(costingChargeDetailsDOB.getRateDescription());
                 }
                 else
                 {
                     if(tempValue < min )
                      {
                    /* if("FUEL SURCHARGE".equalsIgnoreCase((String)rateDescList.get(i)))
                         costingDetailsDOB.setBrkPoint("FSMIN");
                     else*/
                          costingDetailsDOB.setBrkPoint("Min");
                          costingDetailsDOB.setRate(min+"");
                          costingDetailsDOB.setChargeBasis("Per Shipment");
                          costingDetailsDOB.setPrimaryUnitValue("1");
                         // costingDetailsDOB.setRateValue(deciFormat.format(min*convfactorSur)+"");
                          costingDetailsDOB.setRateValue(deciFormat.format(min)+"");
                          costingDetailsDOB.setRateDescription(costingChargeDetailsDOB.getRateDescription());
                    }
                   else
                   {
                     if(!brkPoint)
                     {
                   /*  if("FUEL SURCHARGE".equalsIgnoreCase((String)rateDescList.get(i)))
                         costingDetailsDOB.setBrkPoint("FSKG");
                     else*/
                    	 if("Slab".equalsIgnoreCase(surChargeFlag))
                    		 costingDetailsDOB.setBrkPoint("Slab");
                    	 else
                          costingDetailsDOB.setBrkPoint("Flat");
                     }
                     costingDetailsDOB.setRate(flat+"");
                     costingDetailsDOB.setChargeBasis("Per Kilogram");
                     costingDetailsDOB.setPrimaryUnitValue(deciFormat.format(actualWtSur*temp));
                     //costingDetailsDOB.setRateValue( deciFormat.format(tempValue*convfactorSur));//@@Commented and Modified by Kameswari for the WPBN issue-133207
                     costingDetailsDOB.setRateValue( deciFormat.format(tempValue));
                     costingDetailsDOB.setRateDescription(costingChargeDetailsDOB.getRateDescription());
                   }
                 }

            }
              else if( "2".equalsIgnoreCase(costingChargeDetailsDOB.getShipmentMode()) && !"FREIGHT RATE".equalsIgnoreCase((String)rateDescList.get(i)) && !"A FREIGHT RATE".equalsIgnoreCase((String)rateDescList.get(i)))
            {
            	  surchargeDesc	=(String)	rateDescList.get(i);
            	  temp_value1=0;
            	  		 for(int k=0;k<rtInfoSurCharg;k++)
                         {
            	  			 
                             costingRateInfoDOB = (CostingRateInfoDOB)rateInfoSurcharge.get(k);
                             if(!"A FREIGHT RATE".equalsIgnoreCase(costingRateInfoDOB.getRateDescription()) && !"FREIGHT RATE".equalsIgnoreCase(costingRateInfoDOB.getRateDescription())){
                             if(((!costingRateInfoDOB.getWeightBreakSlab().endsWith("LF") && costingRateInfoDOB.getWeightBreakSlab().length()>=5 
                            		 && ("FF".equalsIgnoreCase(costingRateInfoDOB.getWeightBreakSlab().substring(3,5)) 
                            		 || "FS".equalsIgnoreCase(costingRateInfoDOB.getWeightBreakSlab().substring(3,5))
                            		 || "FP".equalsIgnoreCase(costingRateInfoDOB.getWeightBreakSlab().substring(3,5))
                            		 || "AF".equalsIgnoreCase(costingRateInfoDOB.getWeightBreakSlab().substring(3,5))
                            		 || "PF".equalsIgnoreCase(costingRateInfoDOB.getWeightBreakSlab().substring(3,5))
                            		 || costingRateInfoDOB.getWeightBreakSlab().startsWith("BAFMIN")
                            		 || costingRateInfoDOB.getWeightBreakSlab().startsWith("BAFM3")
                            		 || costingRateInfoDOB.getWeightBreakSlab().startsWith("PSSMIN")
                            		 || costingRateInfoDOB.getWeightBreakSlab().startsWith("PSSM3")))
                            		 || costingRateInfoDOB.getWeightBreakSlab().endsWith("CAF%")
                            		 || costingRateInfoDOB.getWeightBreakSlab().startsWith("CAFMIN")
                            		 || costingRateInfoDOB.getWeightBreakSlab().endsWith("PERCENT"))
                   	  			  && surchargeDesc.equalsIgnoreCase(costingRateInfoDOB.getRateDescription())){
                            	 temp_value1 = temp_value1==0?(temp_value1+1):temp_value1+1; 
                            	 if(temp_value1==1)
                            	 temp_value =k;
                            	 temp_chgDesc = surchargeDesc;
                            	 chg_primaryunit = costingRateInfoDOB.getPrimaryUnitValue()!= null?costingRateInfoDOB.getPrimaryUnitValue():"1";
                             if(costingRateInfoDOB.getWeightBreakSlab().startsWith("BAFMIN")
                            		 || costingRateInfoDOB.getWeightBreakSlab().endsWith("FFMIN")
                            		 || costingRateInfoDOB.getWeightBreakSlab().endsWith("PFMIN")//Added by govind for costing issue to take min when breaks are min&Percent 
                            		 || costingRateInfoDOB.getWeightBreakSlab().endsWith("FFBASIC")
                            		 || costingRateInfoDOB.getWeightBreakSlab().startsWith("PSSMIN")
                            		 || costingRateInfoDOB.getWeightBreakSlab().startsWith("CAFMIN")
                            		 )
                             {
                                  min            = costingRateInfoDOB.getRate();
                                  tempAbs = "MIN";// added by subrahmanyam for 181430
                             }
                             else if(costingRateInfoDOB.getWeightBreakSlab().equalsIgnoreCase("MIN"))
                             {
                                 min            = costingRateInfoDOB.getRate();
                                tempAbs = "MinSlab";

                             }
                             else if(costingRateInfoDOB.getWeightBreakSlab().startsWith("BAFM3") 
                            		 || costingRateInfoDOB.getWeightBreakSlab().startsWith("PSSM3")
                            		 || costingRateInfoDOB.getWeightBreakSlab().endsWith("FFLAT")
                            		 || costingRateInfoDOB.getWeightBreakSlab().endsWith("PFLAT")
                            		 )  // || costingRateInfoDOB.getWeightBreakSlab().endsWith("FFLAT"))
                             {
                                  flat            = costingRateInfoDOB.getRate();
                             }
                             else if(costingRateInfoDOB.getWeightBreakSlab().toUpperCase().endsWith("ABSOLUTE")
                            		 )//Added By Govind for Costing To display Breakpoint Absolute when Absolute is break
                             {
                            	 flat            = costingRateInfoDOB.getRate();
                           	 tempAbs = "ABSOLUTE";
                             }
                             else if(costingRateInfoDOB.getWeightBreakSlab().endsWith("CAF%")
                            		 || costingRateInfoDOB.getWeightBreakSlab().toUpperCase().endsWith("CAFPFPERCENT")
                            		 || costingRateInfoDOB.getWeightBreakSlab().toUpperCase().endsWith("PERCENT"))//Added By Govind for Costing To display Breakpoint Absolute when Absolute is break
                             {
                            	 flat            = costingRateInfoDOB.getRate();
                            	 tempAbs = "PERCENT";
                             }
                             
                         else
                         {
                             if(costingRateInfoDOB.getLowerBound() < (actualWtSur*temp) && (actualWtSur*temp) <= costingRateInfoDOB.getUpperBound())
                             {
                            	 surChargeFlag	=	"Slab";
                                flat = costingRateInfoDOB.getRate();
                                rateIndicator = costingRateInfoDOB.getRateIndicator();
                             }

                         }
            	  	 } //END IF 2
                             else if(!(costingRateInfoDOB.getWeightBreakSlab().endsWith("LF"))){

                                 if(costingRateInfoDOB.getWeightBreakSlab().equalsIgnoreCase("MIN"))
                                 {
                                     min            = costingRateInfoDOB.getRate();
                                     tempAbs = "MinSlab";

                                 }
                                 else if("CSF".equalsIgnoreCase(costingRateInfoDOB.getWeightBreakSlab()) && 
                                		 surchargeDesc.equalsIgnoreCase(costingRateInfoDOB.getRateDescription()))
                        		 {
                        	        flat = costingRateInfoDOB.getRate();
                        	        tempAbs = "Absolute";
                        	        
                        		 }
                             else
                             {
                                 if(costingRateInfoDOB.getLowerBound() < (actualWtSur*temp) && (actualWtSur*temp) <= costingRateInfoDOB.getUpperBound())
                                 {
                                	 surChargeFlag	=	"Slab";
                                    flat = costingRateInfoDOB.getRate();
                                    rateIndicator = costingRateInfoDOB.getRateIndicator();
                                 }

                             }
                              

                             }
//                                }
                        } //END IF 1 
             
                         } // END FOR 
                               if( "Chargeable".equalsIgnoreCase(costingChargeDetailsDOB.getTertiaryBasis()))
                               {
                                    if(densityRatio!=null && densityRatio.trim().length()>0)
                                     volumeSur = actualWtSur/Double.parseDouble(densityRatio);
                                    if(primaryUnit.equalsIgnoreCase("CFT"))
                                     volumeSur   = volumeSur*2.20462;
                               }else
                               {
                                    if(primaryUnit.equalsIgnoreCase("CFT"))
                                   {
                                     volumeSur = volumeSur*35.31467;
                                   }
                               }
                               actualWtSur = volumeSur;
                                if(!brkPoint)
                               {
                                 if(block > 0.0)
                                   actualWtSur = actualWtSur/block;
                               }
                              secValue = 1;
                           if(costingChargeDetailsDOB.getSecUnitValue()!=null && !"".equals(costingChargeDetailsDOB.getSecUnitValue()))
                           {
                             secValue = Double.parseDouble(costingChargeDetailsDOB.getSecUnitValue());
                             if(secValue==0)
                             {
                               secValue=1.0;
                              }
                               costingDetailsDOB.setSecUnitValue(""+secValue);

                           }else
                           {
                             secValue=1.0;
                             costingDetailsDOB.setSecUnitValue("1");
                           }
                            if(costingChargeDetailsDOB.getPrimaryUnitValue()!= null && !"".equals(costingChargeDetailsDOB.getPrimaryUnitValue()) )
                            	if("".equals(costingChargeDetailsDOB.getPrimaryUnitValue()))//added by silpa.p on 29-06-11
                            	{
                            		costingChargeDetailsDOB.setPrimaryUnitValue("1");
                           	tempValue = flat*Double.parseDouble(costingChargeDetailsDOB.getPrimaryUnitValue())*secValue/100;
                            	}//ended
                            	else{
                             	if("percent".equalsIgnoreCase(tempAbs))
                             tempValue = flat*Double.parseDouble(costingChargeDetailsDOB.getPrimaryUnitValue())*secValue/100;
                             	else if("Absolute".equalsIgnoreCase(tempAbs))//@@ by govind for the absolute issue
                                //    tempValue = flat*Double.parseDouble(costingChargeDetailsDOB.getPrimaryUnitValue())*secValue;
                             	    tempValue = flat*Double.parseDouble("1")*secValue;
                                	else{
                                		if(surchargeDesc.equalsIgnoreCase(temp_chgDesc))
                             			tempValue = flat*actualWtSur*secValue;
                                	}
                            	}
                            else
                             tempValue = flat*actualWtSur*secValue/100;//govind
                          
                            if(tempValue < min )
                           {
                              /* if("B.A.F".equalsIgnoreCase((String)rateDescList.get(i)))
                                  costingDetailsDOB.setBrkPoint("BAFMIN");
                               else*/
                            	if(costingRateInfoDOB.getWeightBreakSlab().endsWith("FFBASIC"))
                            		costingDetailsDOB.setBrkPoint("Basic");
                            	else 
                            	costingDetailsDOB.setBrkPoint("Min");
                            	
                             costingDetailsDOB.setRate(min+"");
                             costingDetailsDOB.setChargeBasis("Per Shipment");
                          
                            if(costingChargeDetailsDOB.getPrimaryUnitValue()!=null )
                             {
                            if("List".equalsIgnoreCase(costingChargeDetailsDOB.getWeightBreak())) 
                            	costingDetailsDOB.setPrimaryUnitValue(costingChargeDetailsDOB.getPrimaryUnitValue());
                            else if(!"List".equalsIgnoreCase(costingChargeDetailsDOB.getWeightBreak()) && ("C.A.F%".equalsIgnoreCase(surchargeDesc)|| "Currency Adjustment Factor%-PF".equalsIgnoreCase(surchargeDesc)||"Currency Adjustment Factor-PF".equalsIgnoreCase(surchargeDesc)))
                            	costingDetailsDOB.setPrimaryUnitValue(costingChargeDetailsDOB.getPrimaryUnitValue());
                            else
                            	 //costingDetailsDOB.setPrimaryUnitValue(deciFormat.format(actualWtSur*temp));
                            	costingDetailsDOB.setPrimaryUnitValue(deciFormat.format(1));
                            }
                            else
                            {
                            costingDetailsDOB.setPrimaryUnitValue("1");
                            }
                          //   costingDetailsDOB.setRateValue(deciFormat.format(min*convfactorSur)+"");//@@Commented and Modified by Kameswari for the WPBN issue-133207
                          costingDetailsDOB.setRateValue(deciFormat.format(min)+"");
//                             costingDetailsDOB.setRateDescription("B.A.F");
                          	costingDetailsDOB.setRateDescription(costingRateInfoDOB.getRateDescription());
                              }
                            else
                            {
                              if(!brkPoint)
                              {
                              /* if("B.A.F".equalsIgnoreCase((String)rateDescList.get(i)))
                               costingDetailsDOB.setBrkPoint("BAFM3");
                               else */
                                 //costingDetailsDOB.setBrkPoint("Or"); // commented by subrahmanyam for 181430
                                 //added by subrahmanyam for 181430
                                 if(tempAbs.equalsIgnoreCase("MIN"))
                                  //   costingDetailsDOB.setBrkPoint("Or");
                                	 costingDetailsDOB.setBrkPoint("Flat");
                                 else if("Slab".equalsIgnoreCase(surChargeFlag))
                                	 costingDetailsDOB.setBrkPoint("Slab");
                                 else if("MinSlab".equalsIgnoreCase(surChargeFlag))
                                	 costingDetailsDOB.setBrkPoint("Min");
                                 else if("Absolute".equalsIgnoreCase(tempAbs))//Added By Govind for Costing To display Breakpoint Absolute when Absolute is break
                                	 costingDetailsDOB.setBrkPoint("Absolute");
                                 else if("Percent".equalsIgnoreCase(tempAbs))//Added By Govind for Costing To display Breakpoint Absolute when Absolute is break
                                	 costingDetailsDOB.setBrkPoint("Percent");
                                   else
                                    // costingDetailsDOB.setBrkPoint("Absolute");
                                	   costingDetailsDOB.setBrkPoint("Flat");
                                	   //ended for 181430
                              }
                             costingDetailsDOB.setRate(flat+"");
                             if("Slab".equalsIgnoreCase(surChargeFlag))
                            	 costingDetailsDOB.setChargeBasis("Per Kg");
                             else if("MinSlab".equalsIgnoreCase(surChargeFlag))
                            	 costingDetailsDOB.setChargeBasis("Per Shipment");
                             else if("percent".equalsIgnoreCase(tempAbs))
                            	 costingDetailsDOB.setChargeBasis("Percent of Freight");
                             else if("Absolute".equalsIgnoreCase(tempAbs))
                            	 costingDetailsDOB.setChargeBasis("Per Shipment");
                             else
                          //   costingDetailsDOB.setChargeBasis("Per Cubic Meter");
                            	 costingDetailsDOB.setChargeBasis("Per Weight Measurement"); 
                             
                            
                             if(costingChargeDetailsDOB.getPrimaryUnitValue()!=null )
                             {
                            if("List".equalsIgnoreCase(costingChargeDetailsDOB.getWeightBreak())) 
                            	costingDetailsDOB.setPrimaryUnitValue(costingChargeDetailsDOB.getPrimaryUnitValue());
                            else if(!"List".equalsIgnoreCase(costingChargeDetailsDOB.getWeightBreak()) && ("C.A.F%".equalsIgnoreCase(surchargeDesc) || "Currency Adjustment Factor-PF".equalsIgnoreCase(surchargeDesc)  || "Currency Adjustment Factor%-PF".equalsIgnoreCase(surchargeDesc)|| costingDetailsDOB.getBrkPoint().endsWith("Percent")))
                            	costingDetailsDOB.setPrimaryUnitValue(costingChargeDetailsDOB.getPrimaryUnitValue());
                            else if("Absolute".equalsIgnoreCase(tempAbs))
                            //	costingDetailsDOB.setPrimaryUnitValue(costingChargeDetailsDOB.getPrimaryUnitValue());
                            	costingDetailsDOB.setPrimaryUnitValue("1");
                            else
                            	 costingDetailsDOB.setPrimaryUnitValue(deciFormat.format(actualWtSur*temp));
                            }
                            else
                            {
                            	if("C.S.F".equalsIgnoreCase((String)rateDescList.get(i)))
                            		costingDetailsDOB.setPrimaryUnitValue("1");
                            	 else if("Absolute".equalsIgnoreCase(tempAbs)){
                                 	costingDetailsDOB.setPrimaryUnitValue("1");
                                 	tempValue =  flat*Double.parseDouble(costingDetailsDOB.getPrimaryUnitValue())*secValue;
                            	 }
                            	else
                            costingDetailsDOB.setPrimaryUnitValue(deciFormat.format(actualWtSur*temp));
                            } 
                             
                  // costingDetailsDOB.setRateValue( deciFormat.format(tempValue*convfactorSur));
                             costingDetailsDOB.setRateValue( deciFormat.format(tempValue));
//                              costingDetailsDOB.setRateDescription("B.A.F");
                             costingDetailsDOB.setRateDescription(costingRateInfoDOB.getRateDescription());
                           }
//        }
                      if(costingRateInfoDOB.getWeightBreakSlab().endsWith("LF") || costingRateInfoDOB.getWeightBreakSlab().endsWith("LP")
                    		||( "LIST".equalsIgnoreCase(costingChargeDetailsDOB.getWeightBreak()) && (costingRateInfoDOB.getWeightBreakSlab().toUpperCase()).endsWith("BAF"))){
                          costingDetailsDOB.setChargeBasis(costingChargeDetailsDOB.getChargeBasis());
                          boolean   isPrimaryUnitSelected =  false;
                          boolean   isSecUnitSelected     =  false;
                          String[]  primaryUnitArray = costingChargeDetailsDOB.getPrimaryUnitArray();
                          String[]  secUnitArray     = costingChargeDetailsDOB.getSecUnitArray();
                         int rateInfosize = rateInfoSurcharge.size();
                          int count = 1;
                           for(int j=0;j<rateInfosize;j++)
                          {
                                costingRateInfoDOB = (CostingRateInfoDOB)rateInfoSurcharge.get(j);
                              if("FREIGHT RATE".equalsIgnoreCase(costingRateInfoDOB.getRateDescription()))
                             {
                               count++;
                              }
                          }
                       if(primaryUnitArray==null)
                       {
                          primaryUnitArray = new String[rateInfosize];//@@Modified by kameswari for Surcharge Enhancements
                          primaryUnitArray[c] = "1";
                     
                          costingDetailsDOB.setPrimaryUnitArray(primaryUnitArray);
                        }
                       else
                       {
                       	int prUnitArrLen	= primaryUnitArray.length;
                         for(int z=c;z<prUnitArrLen;z++)
                         {
                             if(primaryUnitArray[z]!=null && primaryUnitArray[z].trim().length()!=0)
                             {
                                 isPrimaryUnitSelected = true;
                                 break;
                             }
                         }
                       if(!isPrimaryUnitSelected && primaryUnitArray.length > 0)
                           primaryUnitArray[c] = "1";

                       costingDetailsDOB.setPrimaryUnitArray(primaryUnitArray);
                       }
                     if(secUnitArray == null)
                     {
                      secUnitArray    = new String[rateInfosize];
                       if(costingChargeDetailsDOB.getSecondaryBasis()!=null)
                           secUnitArray[0] = "1";
                       costingDetailsDOB.setSecUnitArray(secUnitArray);

                     }
                     else
                     {
                        if(secUnitArray.length>0)
                         secUnitArray[0] = "";

                       if(primaryUnitArray!=null)
                       {
                       	int prUnitArrLen	= primaryUnitArray.length;
                         for(int z=0;z<prUnitArrLen;z++)
                         {
                           if(primaryUnitArray[z]!=null && primaryUnitArray[z].trim().length()!=0)
                           {
                             if(secUnitArray[z]==null || (secUnitArray[z]!=null && secUnitArray[z].trim().length()==0))
                                 secUnitArray[z] = "1";
                           }
                         }
                       }

                       costingDetailsDOB.setSecUnitArray(secUnitArray);
                     }
                    for(int p=c;p<rateInfosize;p++)
                   {
                     costingRateInfoDOB = (CostingRateInfoDOB)rateInfoSurcharge.get(p);
                     String s  =(String)rateDescList.get(i);
                       if(s.equalsIgnoreCase(costingRateInfoDOB.getRateDescription()))
                     {
                           primaryValue = (primaryUnitArray[p]!=null && !"".equals(primaryUnitArray[p])?Double.parseDouble(primaryUnitArray[p]):0);
                          secValue     = (secUnitArray[p]!=null && !"".equals(secUnitArray[p])?Double.parseDouble(secUnitArray[p]):1);
                                c++;
                       if(primaryValue>0 )
                       {
                         if("CAF%".equalsIgnoreCase(s) || s.endsWith("-LP"))
                         {
                           //tempValue = (primaryValue*secValue*costingRateInfoDOB.getRate()*convfactorSur)/100;//@@Commented and Modified by Kameswari for the WPBN issue-133207
                           tempValue = (primaryValue*secValue*costingRateInfoDOB.getRate())/100;
                           costingRateInfoDOB.setRateValue(deciFormat.format(tempValue));
                         //  costingDetailsDOB.setChargeBasis(costingChargeDetailsDOB.getChargeBasis());
                          }
                         else
                         {
                             //tempValue = primaryValue*secValue*costingRateInfoDOB.getRate()*convfactorSur;//@@Commented and Modified by Kameswari for the WPBN issue-133207
                             tempValue = primaryValue*secValue*costingRateInfoDOB.getRate();
                           costingRateInfoDOB.setRateValue(deciFormat.format(tempValue));
                          // costingDetailsDOB.setChargeBasis(costingChargeDetailsDOB.getChargeBasis());
                          }
                       }
                       else
                          costingRateInfoDOB.setRateValue("");
                          rateInfoSurcharge.remove(p);
                          rateInfoSurcharge.add(p,costingRateInfoDOB);

                      }
                   }
                  costingDetailsDOB.setCostingRateInfoDOB(rateInfo);
                      }
        }
			else if("2".equalsIgnoreCase(costingChargeDetailsDOB.getShipmentMode()) && !"A FREIGHT RATE".equalsIgnoreCase((String)rateDescList.get(i)) && !"FREIGHT RATE".equalsIgnoreCase((String)rateDescList.get(i)))  
             {
				costingDetailsDOB.setChargeBasis(costingChargeDetailsDOB.getChargeBasis());
             /*  if("B.A.F".equalsIgnoreCase((String)rateDescList.get(i)))
             {
               for(int k=0;k<rtInfoSurCharg;k++)
              {
                  costingRateInfoDOB = (CostingRateInfoDOB)rateInfoSurcharge.get(k);

               if("B.A.F".equalsIgnoreCase(costingRateInfoDOB.getRateDescription()))
               {

                  if(costingRateInfoDOB.getWeightBreakSlab().startsWith("BAFMIN"))
                  {
                       min            = costingRateInfoDOB.getRate();
                       tempAbs = "MIN";// added by subrahmanyam for 181430
                  }
                  if(costingRateInfoDOB.getWeightBreakSlab().startsWith("BAFM3"))
                       flat            = costingRateInfoDOB.getRate();
                  }
                 }
              }

               else  if("P.S.S".equalsIgnoreCase((String)rateDescList.get(i)))
              {
                 for(int k=0;k<rtInfoSurCharg;k++)
                {
                    costingRateInfoDOB = (CostingRateInfoDOB)rateInfoSurcharge.get(k);

                 if("P.S.S".equalsIgnoreCase(costingRateInfoDOB.getRateDescription()))
                 {

                   if(costingRateInfoDOB.getWeightBreakSlab().startsWith("PSSMIN"))
                   {
                       min            = costingRateInfoDOB.getRate();
                       tempAbs  = "MIN";// added by subrahmanyam for 181430
                   }
                   if(costingRateInfoDOB.getWeightBreakSlab().startsWith("PSSM3"))
                      flat            = costingRateInfoDOB.getRate();
                   }
                   }

              }*/
              for(int k=0;k<rtInfoSurCharg;k++)
              {
                  costingRateInfoDOB = (CostingRateInfoDOB)rateInfoSurcharge.get(k);

/*               if("B.A.F".equalsIgnoreCase(costingRateInfoDOB.getRateDescription()))
               {
*/
                  if(costingRateInfoDOB.getWeightBreakSlab().startsWith("BAFMIN"))
                  {
                       min            = costingRateInfoDOB.getRate();
                       tempAbs = "MIN";// added by subrahmanyam for 181430
                  }
                  if(costingRateInfoDOB.getWeightBreakSlab().startsWith("BAFM3"))
                       flat            = costingRateInfoDOB.getRate();
//                  }
                }
              
              
                    if( "Chargeable".equalsIgnoreCase(costingChargeDetailsDOB.getTertiaryBasis()))
                    {
                         if(densityRatio!=null && densityRatio.trim().length()>0)
                          volumeSur = actualWtSur/Double.parseDouble(densityRatio);
                         if(primaryUnit.equalsIgnoreCase("CFT"))
                          volumeSur   = volumeSur*2.20462;
                    }else
                    {
                         if(primaryUnit.equalsIgnoreCase("CFT"))
                        {
                          volumeSur = volumeSur*35.31467;
                        }
                    }
                    actualWtSur = volumeSur;
                     if(!brkPoint)
                    {
                      if(block > 0.0)
                        actualWtSur = actualWtSur/block;
                    }
                   secValue = 1;
                if(costingChargeDetailsDOB.getSecUnitValue()!=null && !"".equals(costingChargeDetailsDOB.getSecUnitValue()))
                {
                  secValue = Double.parseDouble(costingChargeDetailsDOB.getSecUnitValue());
                  if(secValue==0)
                  {
                    secValue=1.0;
                   }
                    costingDetailsDOB.setSecUnitValue(""+secValue);

                }else
                {
                  secValue=1.0;
                  costingDetailsDOB.setSecUnitValue("1");
                }
                 tempValue = flat*actualWtSur*secValue;
                 if(tempValue < min )
                {
                   /* if("B.A.F".equalsIgnoreCase((String)rateDescList.get(i)))
                       costingDetailsDOB.setBrkPoint("BAFMIN");
                    else*/
                       costingDetailsDOB.setBrkPoint("Min");
                  costingDetailsDOB.setRate(min+"");
                  costingDetailsDOB.setChargeBasis("Per Shipment");
                  costingDetailsDOB.setPrimaryUnitValue("1");
               //   costingDetailsDOB.setRateValue(deciFormat.format(min*convfactorSur)+"");//@@Commented and Modified by Kameswari for the WPBN issue-133207
               costingDetailsDOB.setRateValue(deciFormat.format(min)+"");
                  costingDetailsDOB.setRateDescription("B.A.F");
                   }
                 else
                 {
                   if(!brkPoint)
                   {
                   /* if("B.A.F".equalsIgnoreCase((String)rateDescList.get(i)))
                    costingDetailsDOB.setBrkPoint("BAFM3");
                    else */
                      //costingDetailsDOB.setBrkPoint("Or"); // commented by subrahmanyam for 181430
                      //added by subrahmanyam for 181430
                      if(tempAbs.equalsIgnoreCase("MIN"))
                          costingDetailsDOB.setBrkPoint("Or");
                        else
                          costingDetailsDOB.setBrkPoint("Absolute");
                          //ended for 181430
                   }
                  costingDetailsDOB.setRate(flat+"");
                  costingDetailsDOB.setChargeBasis("Per Cubic Meter");
                  costingDetailsDOB.setPrimaryUnitValue(deciFormat.format(actualWtSur*temp));
                  // costingDetailsDOB.setRateValue( deciFormat.format(tempValue*convfactorSur));
                  costingDetailsDOB.setRateValue( deciFormat.format(tempValue));
                   costingDetailsDOB.setRateDescription("B.A.F");
                }
              }
             else  if("C.S.F".equalsIgnoreCase((String)rateDescList.get(i))||"SURCHARGE".equalsIgnoreCase((String)rateDescList.get(i))
             ||"C.A.F%".equalsIgnoreCase((String)rateDescList.get(i)))
             {
            costingDetailsDOB.setChargeBasis(costingChargeDetailsDOB.getChargeBasis());
            String weightbreakslab=null;
             if("C.S.F".equalsIgnoreCase((String)rateDescList.get(i)))
             {
               for(int k=0;k<rtInfoSurCharg;k++)
              {
                  costingRateInfoDOB = (CostingRateInfoDOB)rateInfoSurcharge.get(k);

               if("C.S.F".equalsIgnoreCase(costingRateInfoDOB.getRateDescription()))
               {

                 if(costingRateInfoDOB.getWeightBreakSlab().startsWith("CSF"))
                 {
                   weightbreakslab = costingRateInfoDOB.getWeightBreakSlab();
                   flat            = costingRateInfoDOB.getRate();
                 }
                }
             }
             }
              else if("C.A.F%".equalsIgnoreCase((String)rateDescList.get(i)))
             {
               for(int k=0;k<rtInfoSurCharg;k++)
              {
                  costingRateInfoDOB = (CostingRateInfoDOB)rateInfoSurcharge.get(k);

               if("C.A.F%".equalsIgnoreCase(costingRateInfoDOB.getRateDescription()))
               {

                 if(costingRateInfoDOB.getWeightBreakSlab().startsWith("CAFMIN"))
                 {
                   weightbreakslab = costingRateInfoDOB.getWeightBreakSlab();
                   {
                        min            = costingRateInfoDOB.getRate();
                        tempAbs   = "MIN";
                   }
                 }
                  if(costingRateInfoDOB.getWeightBreakSlab().startsWith("CAF%"))
                 {
                   weightbreakslab = costingRateInfoDOB.getWeightBreakSlab();
                   flat            = costingRateInfoDOB.getRate();
                 }
                }
               }
             }
              else  if("SURCHARGE".equalsIgnoreCase((String)rateDescList.get(i)))
             {
               for(int k=0;k<rtInfoSurCharg;k++)
              {
                  costingRateInfoDOB = (CostingRateInfoDOB)rateInfoSurcharge.get(k);

               if("SURCHARGE".equalsIgnoreCase(costingRateInfoDOB.getRateDescription()))
               {

                 if(costingRateInfoDOB.getWeightBreakSlab().startsWith("SURCHARGE"))
                 {
                   weightbreakslab = costingRateInfoDOB.getWeightBreakSlab();
                   flat            = costingRateInfoDOB.getRate();
                 }
                }
              }
             }
                if(primaryUnit.equalsIgnoreCase("KG"))
                {
                    actualWtSur   = actualWtSur;
                }
                else if(primaryUnit.equalsIgnoreCase("LB"))
                {
                     actualWtSur  = actualWtSur*2.20462;
                }else if(primaryUnit.equalsIgnoreCase("Piece"))
                {
                  actualWtSur = costingHDRDOB.getNoOfPieces();
                }else if(primaryUnit.equalsIgnoreCase("CBM") || primaryUnit.equalsIgnoreCase("CFT"))
                {
                    if( "Chargeable".equalsIgnoreCase(costingChargeDetailsDOB.getTertiaryBasis()))
                    {
                         if(densityRatio!=null && densityRatio.trim().length()>0)
                          volumeSur = actualWtSur/Double.parseDouble(densityRatio);
                         if(primaryUnit.equalsIgnoreCase("CFT"))
                          volumeSur   = volumeSur*2.20462;
                    }else
                    {
                         if(primaryUnit.equalsIgnoreCase("CFT"))
                        {
                          volumeSur = volumeSur*35.31467;
                        }
                    }
                    actualWtSur = volumeSur;

             }
               else
                 {
                    brkPoint = true;
					temp = 1;//@@Added by Kameswari for the WPBN issue - 266115 on 19/08/2011
                    if(costingChargeDetailsDOB.getBrkPoint()!=null && !"".equals(costingChargeDetailsDOB.getBrkPoint()))
                     actualWtSur = Double.parseDouble(costingChargeDetailsDOB.getBrkPoint());
                    else
                     actualWtSur = 1;
                }

                  if(!brkPoint)
                  {
                    if(block > 0.0)
                      actualWtSur = actualWtSur/block;
                  }
                //actualWtSur = round(actualWtSur);
                secValue = 1;
                if(costingChargeDetailsDOB.getSecUnitValue()!=null && !"".equals(costingChargeDetailsDOB.getSecUnitValue()))
                {
                  secValue = Double.parseDouble(costingChargeDetailsDOB.getSecUnitValue());
                  if(secValue==0)
                  {
                    secValue=1.0;
                    costingDetailsDOB.setSecUnitValue(""+secValue);
                  }
                }else
                {
                  secValue=1.0;
                  costingDetailsDOB.setSecUnitValue("1");
                }
                if("C.S.F".equalsIgnoreCase((String)rateDescList.get(i)))
                   tempValue = flat*actualWtSur*secValue;
                else
                {
                if(costingChargeDetailsDOB.getPrimaryUnitValue()!=null)
                {
                    tempValue = (Double.parseDouble(costingChargeDetailsDOB.getPrimaryUnitValue())*flat*secValue)/100;
                }
                  else
                 {
                   tempValue = (flat*actualWtSur*secValue)/100;
                  }
                }


                  if("C.A.F%".equalsIgnoreCase((String)rateDescList.get(i)))
               {
                  if(tempValue < min )
                  {
                    costingDetailsDOB.setBrkPoint("Min");
                    costingDetailsDOB.setRate(min+"");
                    costingDetailsDOB.setChargeBasis("Per Shipment");
                     if(costingChargeDetailsDOB.getPrimaryUnitValue()!=null)
                     {
                    costingDetailsDOB.setPrimaryUnitValue(costingChargeDetailsDOB.getPrimaryUnitValue());
                    // costingDetailsDOB.setRateValue( deciFormat.format(Double.parseDouble(costingDetailsDOB.getPrimaryUnitValue())*convfactorSur));
                  }
                  else
                  {


                  costingDetailsDOB.setPrimaryUnitValue("1");
                  }
                 //   costingDetailsDOB.setPrimaryUnitValue("1");
                   // costingDetailsDOB.setRateValue(deciFormat.format(min*convfactorSur)+"");//@@Commented and Modified by Kameswari for the WPBN issue-133207
                     costingDetailsDOB.setRateValue(deciFormat.format(min)+"");

                    costingDetailsDOB.setRateDescription("C.A.F%");
                  }
                else
                {
                  if(!brkPoint)
                            // costingDetailsDOB.setBrkPoint("Or");// Commented by subrahmanyam for 181430
                            // Added by subrahmanyam for 181430
                            if(tempAbs.equalsIgnoreCase("MIN"))
                                costingDetailsDOB.setBrkPoint("Or");
                            else
                              costingDetailsDOB.setBrkPoint("Absolute");
                              //ended for 181430
                  costingDetailsDOB.setRate(flat+"");
                  costingDetailsDOB.setChargeBasis("Percent of Freight");
                  if(costingChargeDetailsDOB.getPrimaryUnitValue()!=null)
                   {
                   costingDetailsDOB.setPrimaryUnitValue(costingChargeDetailsDOB.getPrimaryUnitValue());
//costingDetailsDOB.setRateValue( deciFormat.format(Double.parseDouble(costingDetailsDOB.getPrimaryUnitValue())*convfactorSur));
                  }
                  else
                  {
                	  if( "C.S.F".equalsIgnoreCase((String)rateDescList.get(i)))
                		  costingDetailsDOB.setPrimaryUnitValue("1");
                	  else
                          costingDetailsDOB.setPrimaryUnitValue(deciFormat.format(actualWtSur*temp));
                  }
                       //costingDetailsDOB.setRateValue( deciFormat.format(tempValue*convfactorSur));//@@Commented and Modified by Kameswari for the WPBN issue-133207
                       costingDetailsDOB.setRateValue( deciFormat.format(tempValue));
                         costingDetailsDOB.setRateDescription((String)rateDescList.get(i));
                }
               }
                else
                {
                   if(!brkPoint)
                  {
                   if("C.S.F".equalsIgnoreCase((String)rateDescList.get(i)))
                   {
                          costingDetailsDOB.setBrkPoint("Absolute");
                           costingDetailsDOB.setPrimaryUnitValue("1");
                           costingDetailsDOB.setRate(flat+"");
                           // costingDetailsDOB.setRateValue( deciFormat.format(flat*convfactorSur));
                           costingDetailsDOB.setRateValue( deciFormat.format(flat));
                          costingDetailsDOB.setChargeBasis("Per Shipment");
                           costingDetailsDOB.setRateDescription((String)rateDescList.get(i));
                   }
                     else
                     {
                        costingDetailsDOB.setBrkPoint("Percent");
                        costingDetailsDOB.setChargeBasis("Percent of Freight");
                        costingDetailsDOB.setRate(flat+"");

                   //.setPrimaryUnitValue(costingChargeDetailsDOB.getPrimaryUnitValue());

                //  costingDetailsDOB.setPrimaryUnitValue(deciFormat.format(actualWtSur*temp));
                  if(costingChargeDetailsDOB.getPrimaryUnitValue()!=null)
                   {
                   costingDetailsDOB.setPrimaryUnitValue(costingChargeDetailsDOB.getPrimaryUnitValue()); //@@Commented and Modified by Kameswari for the WPBN issue-133207
//costingDetailsDOB.setRateValue( deciFormat.format(Double.parseDouble(costingDetailsDOB.getPrimaryUnitValue())*convfactorSur));
                  }
                  else
                  {
                  costingDetailsDOB.setPrimaryUnitValue(deciFormat.format(actualWtSur*temp));
                  }
               //   costingDetailsDOB.setRateValue( deciFormat.format(tempValue*convfactorSur));//@@Commented and Modified by Kameswari for the WPBN issue-133207
                costingDetailsDOB.setRateValue( deciFormat.format(tempValue));
                   costingDetailsDOB.setRateDescription((String)rateDescList.get(i));
                     }
                  }

                }
            }
              if("CAF%".equalsIgnoreCase((String)rateDescList.get(i)) || "BAF".equalsIgnoreCase((String)rateDescList.get(i)) || "CSF".equalsIgnoreCase((String)rateDescList.get(i)) 
          		  || "PSS".equalsIgnoreCase((String)rateDescList.get(i)) || "p.s.s".equalsIgnoreCase((String)rateDescList.get(i)) || "C.A.F%".equalsIgnoreCase((String)rateDescList.get(i)) || ((String)rateDescList.get(i)).startsWith("Currency Adjustment Factor") && !((String)rateDescList.get(i)).endsWith("-LF") && !((String)rateDescList.get(i)).endsWith("-LP") 
          		  || "Bunker Adjustment Factor-FF".equalsIgnoreCase((String)rateDescList.get(i)))

            {
                  //  costingDetailsDOB.setChargeBasis(costingChargeDetailsDOB.getChargeBasis());
                   boolean   isPrimaryUnitSelected =  false;
                   boolean   isSecUnitSelected     =  false;
                   String[]  primaryUnitArray = costingChargeDetailsDOB.getPrimaryUnitArray();
                   String[]  secUnitArray     = costingChargeDetailsDOB.getSecUnitArray();
                  int rateInfosize = rateInfoSurcharge.size();
                   int count = 0;
                  // int temp_count=0;
                  // int temp_count1=0;
                    for(int j=0;j<rateInfosize;j++)
                   {
                         costingRateInfoDOB = (CostingRateInfoDOB)rateInfoSurcharge.get(j);
                       if("FREIGHT RATE".equalsIgnoreCase(costingRateInfoDOB.getRateDescription()))
                      {
                        count++;
                       }
                   }
                if(primaryUnitArray==null )
                {
                   primaryUnitArray = new String[rateInfosize];//@@Modified by kameswari for Surcharge Enhancements
               //    primaryUnitArray[c] = "1";
                   primaryUnitArray[temp_value] = "1";
                   costingDetailsDOB.setPrimaryUnitArray(primaryUnitArray);
                 }
                else
                {
                	int prUnitArrLen	= primaryUnitArray.length;
                  for(int z=temp_value;z<prUnitArrLen;z++)
                  {
                      if(primaryUnitArray[z]!=null && primaryUnitArray[z].trim().length()!=0)
                      {
                          isPrimaryUnitSelected = true;
                          break;
                      }
                  }
                if(!isPrimaryUnitSelected && primaryUnitArray.length > 0 )
              //      primaryUnitArray[c] = "1";
                	primaryUnitArray[temp_value] = "1";

                costingDetailsDOB.setPrimaryUnitArray(primaryUnitArray);
                }

                //@@Modified by kiran.v on 22/03/2012 for Wpbn Issue - 296832 
                             for(int k=0;k<rateInfosize;k++){
                           	  costingRateInfoDOB = (CostingRateInfoDOB)rateInfo.get(k);
                           	if( "BAF".equalsIgnoreCase(costingRateInfoDOB.getRateDescription()) || "CSF".equalsIgnoreCase(costingRateInfoDOB.getRateDescription()) || "PSS".equalsIgnoreCase(costingRateInfoDOB.getRateDescription())){
                           	 if(!isPrimaryUnitSelected && primaryUnitArray.length > 0 )
                           		primaryUnitArray[k] = "1";
                         	}
                             }
                          costingDetailsDOB.setPrimaryUnitArray(primaryUnitArray);
                         	//@@Ended by kiran.v
                
              if(secUnitArray == null)
              {
               secUnitArray    = new String[rateInfosize];
                if(costingChargeDetailsDOB.getSecondaryBasis()!=null)
                    secUnitArray[0] = "1";
                costingDetailsDOB.setSecUnitArray(secUnitArray);

              }
              else
              {
                 if(secUnitArray.length>0)
                  secUnitArray[0] = "";

                if(primaryUnitArray!=null)
                {
                	int prUnitArrLen	= primaryUnitArray.length;
                  for(int z=0;z<prUnitArrLen;z++)
                  {
                    if(primaryUnitArray[z]!=null && primaryUnitArray[z].trim().length()!=0)
                    {
                      if(secUnitArray[z]==null || (secUnitArray[z]!=null && secUnitArray[z].trim().length()==0))
                          secUnitArray[z] = "1";
                    }
                  }
                }

                costingDetailsDOB.setSecUnitArray(secUnitArray);
              }
             for(int p=c;p<rateInfosize;p++)
            {
              costingRateInfoDOB = (CostingRateInfoDOB)rateInfoSurcharge.get(p);
              String s  =(String)rateDescList.get(i);
                if(s.equalsIgnoreCase(costingRateInfoDOB.getRateDescription()))
              { 
                    primaryValue = (primaryUnitArray[p]!=null && !"".equals(primaryUnitArray[p])?Double.parseDouble(primaryUnitArray[p]):0);               	
                    secValue     = (secUnitArray[p]!=null && !"".equals(secUnitArray[p])?Double.parseDouble(secUnitArray[p]):1);              	
                   c++;
                if(primaryValue>0 )
                {
                  if("CAF%".equalsIgnoreCase(s))
                  {
                    //tempValue = (primaryValue*secValue*costingRateInfoDOB.getRate()*convfactorSur)/100;//@@Commented and Modified by Kameswari for the WPBN issue-133207
                    tempValue = (primaryValue*secValue*costingRateInfoDOB.getRate())/100;
                    costingRateInfoDOB.setRateValue(deciFormat.format(tempValue));
                  //  costingDetailsDOB.setChargeBasis(costingChargeDetailsDOB.getChargeBasis());
                   }
                  else
                  {
                      //tempValue = primaryValue*secValue*costingRateInfoDOB.getRate()*convfactorSur;//@@Commented and Modified by Kameswari for the WPBN issue-133207
                      tempValue = primaryValue*secValue*costingRateInfoDOB.getRate();
                    costingRateInfoDOB.setRateValue(deciFormat.format(tempValue));
                   // costingDetailsDOB.setChargeBasis(costingChargeDetailsDOB.getChargeBasis());
                   }
                }
                else
                   costingRateInfoDOB.setRateValue("");
                   rateInfoSurcharge.remove(p);
                   rateInfoSurcharge.add(p,costingRateInfoDOB);

               }
            }
           costingDetailsDOB.setCostingRateInfoDOB(rateInfo);
          }


          if(brkPoint)//@@Added by Kameswari for the WPBN issue-145724
         {
            costingDetailsDOB.setBrkPoint(costingChargeDetailsDOB.getBrkPoint());//@@Added by Kameswari for the WPBN issue-135557

         }
             costingDetailsDOB.setRateDescription((String)rateDescList.get(i));
             convfactor = operationsImpl.getConvertionFactor((String)rateCurrencyList.get(i),costingHDRDOB.getBaseCurrency());
			 costingDetailsDOB.setConvFactor(convfactor);
			 costingDetailsDOB.setChargeBasisDesc(costingChargeDetailsDOB.getChargeBasisDesc());
             costingDetailsDOB.setDensityRatio(costingChargeDetailsDOB.getDensityRatio());;
             costingDetailsDOB.setChecked("checked");
             costingDetailsDOB.setShipmentMode(costingChargeDetailsDOB.getShipmentMode());
             costingDetailsDOB.setRateType(costingChargeDetailsDOB.getRateType());
             costingDetailsDOB.setPrimaryBasis(costingChargeDetailsDOB.getPrimaryBasis());
             costingDetailsDOB.setSecUnitValue(costingChargeDetailsDOB.getSecUnitValue());
             costingDetailsDOB.setCurrency((String)rateCurrencyList.get(i));//Added by Mohan for issue no.219979 on 10122010
              costingDetailsDOB.setWeightBreak(costingChargeDetailsDOB.getWeightBreak());
            costingDetailsDOB.setCostingRateInfoDOB(rateInfoSurcharge);
            costingDetailsDOB.setTertiaryBasis(costingChargeDetailsDOB.getTertiaryBasis());
           if(costingDetailsDOB.getPrimaryUnitValue()!=null||costingDetailsDOB.getPrimaryUnitArray()!=null)
            {
             costingChargeList.add(costingDetailsDOB);

            }
         }
       }

    }catch(Exception e)
    {
      e.printStackTrace();
      throw new Exception();
    }

          return costingChargeList;
  }


  private String validateHDR(CostingHDRDOB costingHDRDOB,ESupplyGlobalParameters loginbean)throws Exception
  {
      StringBuffer sb_errormsg  = null;
      QMSQuoteSession			remote					=	null;
			QMSQuoteSessionHome		home					=	null;
      String errorMsg = null;
      try
      {
        sb_errormsg =  new StringBuffer();
        /*if(costingHDRDOB.getCustomerid()==null || "".equals(costingHDRDOB.getCustomerid()))
        {
            sb_errormsg.append("CustomerId  ");
        }*/
        if(costingHDRDOB.getQuoteid()==null || "".equals(costingHDRDOB.getQuoteid()))
        {
          sb_errormsg.append("QuoteId  ");
        }
        if(sb_errormsg!=null && !"".equals(sb_errormsg.toString()))
        {
          sb_errormsg.append(" Should not be empty :");
          return sb_errormsg.toString();
        }


        if(sb_errormsg==null || "".equals(sb_errormsg.toString()))
        {


              home        = (QMSQuoteSessionHome)LookUpBean.getEJBHome("QMSQuoteSessionBean");
              remote      = (QMSQuoteSession)home.create();

              errorMsg = remote.validateCostingHDR(costingHDRDOB,loginbean);
              sb_errormsg.append(errorMsg);
              errorMsg=null;
        }

        if(sb_errormsg!=null && !"".equals(sb_errormsg.toString()))
        {
          //Logger.info(FILE_NAME,"sb_errormsg:"+sb_errormsg);
          sb_errormsg.append("Invalid");
        }

      }catch(Exception e)
      {
        errorMsg=null;
        //Logger.info(FILE_NAME,"in validateHDR()"+e);
        logger.info(FILE_NAME+"in validateHDR()"+e);
        throw new Exception(e);

      }

      return sb_errormsg.toString();
  }

  private String validateCurrency(String baseCurrency , ESupplyGlobalParameters loginbean)throws Exception
  {
      String errormsg  = null;
      QMSQuoteSession			remote					=	null;
			QMSQuoteSessionHome		home					=	null;
    try
    {
      home        = (QMSQuoteSessionHome)LookUpBean.getEJBHome("QMSQuoteSessionBean");
      remote      = (QMSQuoteSession)home.create();
      errormsg    =  remote.validateCurrency(baseCurrency);
    }catch(Exception e)
    {
      e.printStackTrace();
      //Logger.info(FILE_NAME,"in validateCurrency()"+e);
      logger.info(FILE_NAME+"in validateCurrency()"+e);
      throw new Exception();
    }
    return errormsg;
  }


  private CostingMasterDOB doGetQuoteInfo(HttpServletRequest request,HttpServletResponse responce,CostingHDRDOB costingHDRDOB,ESupplyGlobalParameters loginbean)
  {

        QMSQuoteSessionHome       home              = null;
        QMSQuoteSession           remote            = null;
        CostingMasterDOB          costingMasterDOB  = null;
        try
        {
              home        = (QMSQuoteSessionHome)LookUpBean.getEJBHome("QMSQuoteSessionBean");
              remote      = (QMSQuoteSession)home.create();

              costingMasterDOB = remote.getQuoteRateInfo(costingHDRDOB,loginbean);


        }catch(Exception e)
        {
            e.printStackTrace();

        }

        return costingMasterDOB;
  }

  private void doForwardToErrorPage(ESupplyGlobalParameters loginbean,HttpServletRequest request,
								HttpServletResponse response)
  {
    String operation    = request.getParameter("Operation");
    String subOperation = request.getParameter("subOperation");
    String nextNavigation = "";
		ArrayList						keyValueList			= new ArrayList();
		StringBuffer					errorMessage		=	new StringBuffer();
		ErrorMessage				errorMessageObject= null;
    String              errorMsg          = "";
    HttpSession         session           = request.getSession();

    nextNavigation  = "QMSCostingController?Operation="+operation+"&subOperation=";
    try{
             errorMsg = (String)request.getAttribute("errorMsg");
             //Logger.info(FILE_NAME,"insdfadnfa--------"+errorMsg);
            if("".equals(errorMsg) || errorMsg==null)
            {
              if(operation.equals("Add"))
                { errorMessage.append("Exception While inserting the records :");}
              else if(operation.equals("Modify"))
                { errorMessage.append("Exception While Modifying the records :");}
              else if(operation.equals("View"))
                { errorMessage.append("Exception While Viewing the records :");}
            }else
            {
              errorMessage.append(errorMsg);
            }
            errorMessageObject = new ErrorMessage(errorMessage.toString(),nextNavigation);
						keyValueList.add(new KeyValue("ErrorCode","ERR"));
						keyValueList.add(new KeyValue("Operation",operation));
						errorMessageObject.setKeyValueList(keyValueList);
						request.setAttribute("ErrorMessage",errorMessageObject);
						doDispatcher(request,response,"QMSESupplyErrorPage.jsp");//use send redirect
            //session.setAttribute("ErrorMessageObj",errorMessageObject);
            //response.sendRedirect("QMSESupplyErrorPage.jsp");
    }catch(Exception e)
    {
      //Logger.info(FILE_NAME,"Exception while forwarding the controller"+e);
      logger.info(FILE_NAME+"Exception while forwarding the controller"+e);
    }

  }

  /**
	 * This is method is used for dispatching to a particular file
	 *
	 * @param request 	an HttpServletRequest object that contains the request the client has made of the servlet
	 * @param response 	an HttpServletResponse object that contains the responsethe servlet sends to the client
	 * @param forwardFile 	a String that contains where to forward
	 *
	 * @exception IOException if an input or output error is detected when the servlet handles the POST request
	 * @exception ServletException if the request for the POST could not be handled.
	 */
	public void doDispatcher(HttpServletRequest request,
								HttpServletResponse response,
								String forwardFile) throws ServletException, IOException
	{
		try
		{
			RequestDispatcher rd = request.getRequestDispatcher(forwardFile);
			rd.forward(request, response);
		}
		catch(Exception ex)
		{
			//Logger.error(FILE_NAME, " [doDispatcher() ", " Exception in forwarding to "+forwardFile, ex);
      logger.error(FILE_NAME+ " [doDispatcher() "+ " Exception in forwarding to "+forwardFile, ex);
		}
	}
    private void sendMail(String frmAddress, String toAddress,String subject, String message, String attachments,File[] fArr) throws Exception
		{
//			System.out.println("sendMail in CustomizedReportBean called.. attchment :: "+attachment);
		   	try
			{   //attachment = "c:/"+attachment;
          //Logger.info(FILE_NAME,"message  "+message);
          //Logger.info(FILE_NAME,"frmAddress  "+frmAddress);
          //Logger.info(FILE_NAME,"toAddress  "+toAddress);
          //Logger.info(FILE_NAME,"attachment  "+attachments);

          Context initial = new InitialContext();
          Session session = (Session) initial.lookup("java:comp/env/mail/MS");
          InternetAddress fromAddress = new InternetAddress(frmAddress);

          Message msg = new MimeMessage(session);
          msg.setFrom(fromAddress);
          msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toAddress) );
          msg.setSubject(subject);
          msg.setSentDate(new java.util.Date());

          // create and fill the first message part
          MimeBodyPart mbp1 = new MimeBodyPart();
          mbp1.setText(message);
          Multipart mp = new MimeMultipart();
          mp.addBodyPart(mbp1);

          //Logger.info(FILE_NAME,"mbp1  "+mbp1);
          //Logger.info(FILE_NAME,"mp  "+mp);

          String attachs[] = attachments.split(",");

          if(attachs!=null && attachs.length>0)
          {
           ///---------
           Multipart multipart = new MimeMultipart();
//<in  a loop for multiple files>
      /* BodyPart messageBodyPart = new MimeBodyPart();
       String fileName = <the file to be attached (full path)>
       DataSource source =  new FileDataSource(fileName);
       messageBodyPart.setDataHandler(new DataHandler(source));

messageBodyPart.setFileName(fileName.substring(fileName.lastIndexOf(separator)+1));
       multipart.addBodyPart(messageBodyPart);
<file attachement loop ends>*/

          // ----------
           int attachsLen	= attachs.length;
           for(int i=0;i<attachsLen;i++)
           {
             BodyPart mbp2 = new MimeBodyPart();
             DataSource fds = new FileDataSource(fArr[i]);

             mbp2.setDataHandler(new DataHandler(fds));
             mbp2.setFileName(fds.getName());
             mp.addBodyPart(mbp2);
           }
          }
	      msg.setContent(mp);
	   		Transport.send(msg);
	   }
	   catch(MessagingException me)
	   {
//		System.out.println("Message Exception in send Mails ... "+me.toString());
		throw new Exception(me.toString());
	   }
	   catch(Exception e)
		 {
//		   System.out.println("Exception in sendMails CustomizedReportBean..."+e.toString());
	       throw new Exception(e.getMessage());
	   }
	}
  private double round(double number)
  {
    double  returnValue = 0;
    boolean flag        = false;
    int     d           = 2;
    double  f           = Math.pow(8, d);
    String  value       = "";

    try
    {
      if(number < 0)
      {
        flag      = true;
        number    = Math.abs(number);
      }
      number        = number + Math.pow(8, - (d + 1));
      number        = Math.round(number * f) / f;
      number        = number + Math.pow(8, - (d + 1));
      value         = number+"";
      if(flag)
        returnValue   = -(Double.parseDouble(value.substring(0, value.indexOf('.') + d + 1)));
      else
        returnValue   = Double.parseDouble(value.substring(0, value.indexOf('.') + d + 1));
    }
    catch(NumberFormatException nf)
    {
      returnValue = number;
    }
    return returnValue;
  }
  //Added by Mohan for issue no.219979 on 10122010
  private ArrayList getQuoteLanesInfo(HttpServletRequest request,HttpServletResponse responce,CostingHDRDOB costingHDRDOB,ESupplyGlobalParameters loginbean)
  {
        com.qms.operations.quote.dao.QMSQuoteDAO doa	= null;
        ArrayList lanesList	= null;
        try
        {
        	doa =  new com.qms.operations.quote.dao.QMSQuoteDAO();
        	lanesList= doa.getQuoteLaneDetails(costingHDRDOB);
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        return lanesList;
  }
  
  //Added by Rakesh on 25-01-2011 for CR:231219
  public CostingMasterDOB getQuoteContentDtl(CostingMasterDOB masterDOB) throws EJBException
  {
	    Connection          conn        =   null;
	    PreparedStatement   pstmt       =   null;
	    ResultSet           rs          =   null;
	   // PreparedStatement   pstmt1       =   null;
	    //ResultSet           rs1          =   null;
	    ArrayList           descList          =   null;
	    ArrayList           hdrFtrList        =   null;
	    ArrayList           alignList         =   null;
	    ArrayList           defaultDescList   =   null;
	    ArrayList           defaultHdrFtrList =   null;
	    StringBuffer        terminalQry       =   null;
	    String              quoteQry          =   "";
	    String       salesPersonEmailQuery    =   "";//@@Added by kameswari for enhancements
	    StringBuffer        contentQry        =   null;
	    String 				quoteTypeQuery	  =	  null;
	    String              shipModeStr       =   "";
	    String              operation         =   "";
	     
	    String[]            contentArray        = null;
	    String[]            headerFooterArray   = null;
	    String[]            alignArray          = null;
	    
	    String[]            defaultContentArray  = null;
	    String[]            defaultHdrFooterArray= null;
	    String 	isMultiQuote = null;
	    String	shipmentMode = null;
	    String                terminalIdQuery     ="SELECT TERMINAL_ID FROM QMS_QUOTE_MASTER WHERE QUOTE_ID=?";
	    try
	    {
	      conn              =     this.getConnection();
	      terminalQry       =     new StringBuffer("");
	      contentQry        =     new StringBuffer("");
	      descList          =     new ArrayList();
	      hdrFtrList        =     new ArrayList();
	      alignList         =     new ArrayList();
	      defaultDescList   =     new ArrayList();
	      defaultHdrFtrList =     new ArrayList(); 
	      
	      operation         =     masterDOB.getOperation();
	     /**
	      if("View".equalsIgnoreCase(operation))
	      {
	            pstmt1      =     conn.prepareStatement(terminalIdQuery);
	            pstmt1.setString(1,masterDOB.getQuoteId());
	            rs1         =     pstmt1.executeQuery();
	            while(rs1.next())
	            {
	                  masterDOB.setTerminalId(rs1.getString("TERMINAL_ID"));   
	            }
	      }  */
	      
	      quoteQry          =     "SELECT CM.DESCRIPTION,QCD.HEADER,QCD.ALIGN FROM QMS_CONTENTDTLS CM,QMS_QUOTE_HF_DTL QCD "+
	                          //"WHERE CM.CONTENTID=QCD.CONTENT AND QCD.QUOTE_ID = ? AND CM.FLAG ='F' ORDER BY HEADER DESC,CLEVEL";
	      						"WHERE CM.CONTENTID=QCD.CONTENT AND QCD.QUOTE_ID = '0' AND CM.FLAG ='F' ORDER BY HEADER DESC,CLEVEL";	
	                            


	      salesPersonEmailQuery ="SELECT EMAILID FROM FS_USERMASTER WHERE EMPID=?";
	      quoteTypeQuery		="SELECT DISTINCT QUOTE.IS_MULTI_QUOTE,QUOTE.SHIPMENT_MODE FROM QMS_QUOTE_MASTER QUOTE WHERE QUOTE.QUOTE_ID=?";	
	    
	     // if("H".equalsIgnoreCase(masterDOB.getAccessLevel()))
	      //@@ Commented & Added by subrahmanyam for the pbn id: 210886 on 12-Jul-10
	      //if("DHLCORP".equalsIgnoreCase(masterDOB.getTerminalId()))
	      if("DHLCORP".equalsIgnoreCase(masterDOB.getTerminalId()) || "DHLASPA".equalsIgnoreCase(masterDOB.getTerminalId()))
	      {
	      //  terminalQry.append( " (SELECT TERMINALID TERM_ID FROM FS_FR_TERMINALMASTER)");
	      terminalQry.append( " (SELECT TERMINALID TERM_ID FROM FS_FR_TERMINALMASTER WHERE TERMINALID='"+masterDOB.getTerminalId()+"')");
	      }
	      else
	      {
	        terminalQry.append( "(SELECT PARENT_TERMINAL_ID TERM_ID FROM FS_FR_TERMINAL_REGN CONNECT BY CHILD_TERMINAL_ID = PRIOR PARENT_TERMINAL_ID START WITH CHILD_TERMINAL_ID = '")
	                   .append(masterDOB.getTerminalId()).append("'")
	                   .append( " UNION ")
	                   .append( " SELECT TERMINALID TERM_ID FROM FS_FR_TERMINALMASTER WHERE OPER_ADMIN_FLAG = 'H' ")
	                   .append( " UNION ")
	                   .append( " SELECT '").append(masterDOB.getTerminalId()).append("' TERM_ID FROM DUAL ")
	                   .append( " UNION ")
	                   .append( " SELECT CHILD_TERMINAL_ID TERM_ID FROM FS_FR_TERMINAL_REGN CONNECT BY PRIOR CHILD_TERMINAL_ID = PARENT_TERMINAL_ID START WITH PARENT_TERMINAL_ID = '")
	                   .append(masterDOB.getTerminalId()).append("')");
	      }
	      pstmt         =     conn.prepareStatement(quoteTypeQuery);
	      pstmt.setString(1,masterDOB.getQuoteId());
	      rs      =   pstmt.executeQuery();
	      while(rs.next()){
	    	  isMultiQuote = rs.getString("IS_MULTI_QUOTE");
	    	  shipmentMode = rs.getString("SHIPMENT_MODE");
	    	
	      }
	      if(rs!=null)
		       rs.close();
	      if(pstmt!=null)
		       pstmt.close();
	      //if("Y".equalsIgnoreCase(isMultiQuote))
	     // {
	        if("1".equalsIgnoreCase(shipmentMode))
	          shipModeStr = " AND SHIPMENTMODE IN (1,3,5,7)";
	        else if("2".equalsIgnoreCase(shipmentMode))
	          shipModeStr = " AND SHIPMENTMODE IN (2,3,6,7)";
	        else if("4".equalsIgnoreCase(shipmentMode))
	          shipModeStr = " AND SHIPMENTMODE IN (4,5,6,7)";
	    //  }
  
	      contentQry.append("SELECT DESCRIPTION,HEADERFOOTER,'L' ALIGN,(DECODE(ACCESSLEVEL,'H','A','A','H','O'))ACCESSLEVEL,FLAG FROM QMS_CONTENTDTLS WHERE ACTIVEINACTIVE='N' AND INVALIDATE='F' ")
	                .append(shipModeStr).append("AND FLAG ='T' AND TEMINALID IN ").append(terminalQry)
	               // .append(" ORDER  BY ACCESSLEVEL,CONTENTID"); commented by VLAKSHMI for issue 172986 on 10/6/2009
	               .append(" ORDER  BY CONTENTID");
  
	      pstmt   =   conn.prepareStatement(contentQry.toString());
  
	      rs      =   pstmt.executeQuery();
  
	      if("QuoteGrouping".equalsIgnoreCase(operation))
	      {
	        while(rs.next())
	        {
	          defaultDescList.add(rs.getString("DESCRIPTION"));
	          defaultHdrFtrList.add(rs.getString("HEADERFOOTER"));
	        }
	      }
	      else
	      {
	        while(rs.next())
	        {
	          descList.add(rs.getString("DESCRIPTION"));
	          hdrFtrList.add(rs.getString("HEADERFOOTER"));
	          alignList.add(rs.getString("ALIGN"));//@@Default Left Aligned
	        }
	      }
  
	      if(rs!=null)
	        rs.close();
	      if(pstmt!=null)
	        pstmt.close();
  
	      pstmt         =     conn.prepareStatement(quoteQry);
	      
	      //pstmt.setLong(1,masterDOB.getUniqueId());
	      
	      rs            =     pstmt.executeQuery();
	      
	      while(rs.next())
	      {
	        descList.add(rs.getString("DESCRIPTION"));
	        hdrFtrList.add(rs.getString("HEADER"));
	        alignList.add(rs.getString("ALIGN"));
	      }
	      
	      if(descList.size()>0)
	      {
	        contentArray            = new String[descList.size()];
	        headerFooterArray       = new String[hdrFtrList.size()];
	        alignArray              = new String[alignList.size()];
	      }
	     int desListSize	=	descList.size();
	      for(int i=0;i<desListSize;i++)
	      {
	        contentArray[i]       = (String)descList.get(i);
	        headerFooterArray[i]  = (String)hdrFtrList.get(i);
	        alignArray[i]         = (String)alignList.get(i);
	      }
  
	      int defaultContentSize  = defaultDescList.size();
	      
	      if(defaultContentSize >0)
	      {
	        defaultContentArray   =   new String[defaultContentSize];
	        defaultHdrFooterArray =   new String[defaultContentSize];
	      }
	      
	      for(int i=0;i<defaultContentSize;i++)
	      {
	        defaultContentArray[i]   =   (String)defaultDescList.get(i);
	        defaultHdrFooterArray[i] =   (String)defaultHdrFtrList.get(i);
	      }
	       if(rs!=null)
	        rs.close();
	      if(pstmt!=null)
	        pstmt.close();
	      /**
	      if("Y".equalsIgnoreCase(masterDOB.getSalesPersonFlag()))
	       {
	          pstmt = conn.prepareStatement(salesPersonEmailQuery);
	          pstmt.setString(1,masterDOB.getSalesPersonCode());
	          rs  = pstmt.executeQuery();
	          if(rs.next())
	          {
	              masterDOB.setSalesPersonEmail(rs.getString("EMAILID"));
	          }
	       }   */
	      masterDOB.setHeaderFooter(headerFooterArray); 
	      masterDOB.setContentOnQuote(contentArray);
	      masterDOB.setAlign(alignArray);
	      
	      masterDOB.setDefaultContent(defaultContentArray);
	      masterDOB.setDefaultHeaderFooter(defaultHdrFooterArray);
  
	    }
	    catch(EJBException ejb)
	    {
	      //Logger.error(FILE_NAME,"EJBException while fetching Quote Content Header Footer Details "+ejb);
	      logger.error(FILE_NAME+"EJBException while fetching Quote Content Header Footer Details "+ejb);
	      ejb.printStackTrace();
	      throw new EJBException(ejb);
	    }
	    catch(Exception e)
	    {
	      //Logger.error(FILE_NAME,"Exception while fetching Quote Content Header Footer Details "+e);
	      logger.error(FILE_NAME+"Exception while fetching Quote Content Header Footer Details "+e);
	      e.printStackTrace();
	      throw new EJBException(e);
	    }
	    finally
	    {
	      ConnectionUtil.closeConnection(conn,pstmt,rs);
	      //ConnectionUtil.closeConnection(conn,pstmt1,rs1);
	    }
	    return masterDOB;
  
  
  }
  private Connection  getConnection() throws EJBException
  {
    Connection con = null;
    try
    {
      if(dataSource== null)
        getDataSource();
      con = dataSource.getConnection();
    }
    catch( Exception e )
    {
      e.printStackTrace();
      logger.error(FILE_NAME+"Exception in getConnectin() method of QMSQuoteSessionBean.java: "+e.toString());
    }
    return con;
  }
  private InitialContext  initialContext	= null; 
	private javax.sql.DataSource	dataSource		  = null;
  private void getDataSource() throws EJBException
  {
    try
    {
      initialContext = new InitialContext();
      dataSource = (javax.sql.DataSource)initialContext.lookup("java:comp/env/jdbc/DB");
    }
    catch( Exception e )
    {
      e.printStackTrace();
      logger.error(FILE_NAME+"Exception in getDataSource() method of QMSQuoteSessionBean.java: "+e.toString());
      //logger.error(FILE_NAME+"Exception in getDataSource() method of QMSQuoteSessionBean.java: "+e.toString());
    }
  }
  
  
  
}