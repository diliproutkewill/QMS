import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import com.foursoft.esupply.common.exception.InvalidDateDelimiterException;
import com.foursoft.esupply.common.exception.InvalidDateFormatException;
import com.foursoft.esupply.common.java.ErrorMessage;
import com.foursoft.esupply.common.java.KeyValue;
import com.foursoft.esupply.common.java.LookUpBean;
import com.foursoft.esupply.common.util.ESupplyDateUtility;
import com.foursoft.etrans.common.bean.Address;
import com.foursoft.etrans.common.util.java.OperationsImpl;
import com.foursoft.etrans.setup.customer.java.CustContactDtl;
import com.foursoft.etrans.setup.customer.java.CustomerModel;
import com.oreilly.servlet.MultipartRequest;
import com.oreilly.servlet.multipart.FilePart;
import com.oreilly.servlet.multipart.MultipartParser;
import com.oreilly.servlet.multipart.ParamPart;
import com.oreilly.servlet.multipart.Part;
import com.qms.operations.rates.dob.FlatRatesDOB;
import com.qms.operations.rates.dob.RateDOB;
import com.qms.operations.rates.ejb.sls.BuyRatesSession;
import com.qms.operations.rates.ejb.sls.BuyRatesSessionHome;
import com.qms.operations.sellrates.java.QMSSellRatesDOB;
import com.qms.setup.ejb.sls.SetUpSession;
import com.qms.setup.ejb.sls.SetUpSessionHome;


public class QMSBuyRatesUploadController extends HttpServlet 
{
  private static final String CONTENT_TYPE = "text/html; charset=windows-1252";
  private static final String FILE_NAME = "QMSBuyRatesUploadController";
  
  private static  String DEFAULT_ENCODING;
  private String UPLOAD_PATH="";
  private static final int DEFAULT_MAX_POST_SIZE=10*1024*1024; //10 MB
  private static Logger logger = null;
  private static  String Message = "";
  private static String error = "";
  private int i;
  HttpServletRequest request = null;
  HttpServletResponse response = null;
  public void init(ServletConfig config) throws ServletException
  {
    logger  = Logger.getLogger(QMSBuyRatesUploadController.class);
    super.init(config);
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
	  BuyRatesSessionHome 	 home           =   null;
    BuyRatesSession 		   remote         =   null;
     ArrayList             errorList      =   null;
     StringBuffer			  	 errorMessage   =	  new StringBuffer();
     RateDOB               rateDOB            = null;
     FlatRatesDOB          flatRatesDOB       = null;
     String                str[]              = null;
     String                strn[]             = null;
     String                 s                 = "";
     String                errorMode          = null;
     StringBuffer          errorMsg           =   new StringBuffer("");
     ErrorMessage			     errorMessageObject = null;
     String                nextNavigation     = "QMSBuyRatesUploadController?Operation=UpLoad&subOperation=";
     ArrayList						 keyValueList	      = null;
  	loginbean						                  = (ESupplyGlobalParameters)session.getAttribute("loginbean");
   String endsWith = request.getParameter("endsWith");
     try
    {

     if("UpLoad".equals(operation))
      {
      if("UpLoadCustomerDetails".equals(subOperation)){
           doCustomerUpLoadProcess(request,response,loginbean);
        } else if("UpLoad".equals(subOperation)){
      //   UPLOAD_PATH="./";
			// MultipartRequest multitest =new MultipartRequest(request,UPLOAD_PATH,DEFAULT_MAX_POST_SIZE,DEFAULT_ENCODING);  
		//	 endsWith = multitest.getParameter("endsWith");
		 System.out.println("endswith:"+endsWith);
    //   multitest=null;
		  if("EXCEL".equals(endsWith)){
       //@@Added by ramakrishna for the upload issue
          long l=System.currentTimeMillis(); 
		     PrintWriter out = response.getWriter();
        response.setContentType("text/plain");
        
        request.setCharacterEncoding("UTF-8");
        String DEFAULT_ENCODING=request.getCharacterEncoding();    
        final String contentType = request.getHeader("Content-Type");
     
      if (contentType != null && contentType.startsWith("multipart/form-data"))
      {
         try {	 
               ESupplyDateUtility eSupplyDateUtil      =  new ESupplyDateUtility();
               String dateFormat                   	=	loginbean.getUserPreferences().getDateFormat();
               eSupplyDateUtil.setPattern(dateFormat);
	  	       //UPLOAD_PATH="/opt/bea/user_projects/domains/DHLTEST/";
           // UPLOAD_PATH="C:/upload";
          //    UPLOAD_PATH="/bea/user_projects/domains/dhldomain/myserver/";
           UPLOAD_PATH="./";
              MultipartRequest multi =new MultipartRequest(request,UPLOAD_PATH,DEFAULT_MAX_POST_SIZE,DEFAULT_ENCODING);       
            //MultipartRequest multi =new MultipartRequest(request,UPLOAD_PATH);       
              
               errorMode = multi.getParameter("errorMode");
               System.out.println("endswth in controller"+multi.getParameter("endsWth"));
               File originalFile=multi.getFile("fileName");
               String name=multi.getFilesystemName("fileName");
               String orignalName=multi.getOriginalFileName("fileName");
               String fileName=name.substring(0,name.lastIndexOf("."));
               String extension=name.substring(name.lastIndexOf(".")+1,name.length());
               Date d = new Date();
               DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault());
               NumberFormat numberFormat=df.getNumberFormat();
               SimpleDateFormat formatter = new SimpleDateFormat("yyyy-mm-dd"+" "+"hh.mm.ss");
               String pathval=fileName+formatter.format(d)+"."+extension;
         
               File newFile=new File(UPLOAD_PATH+pathval); //File Rename 
               boolean rename=originalFile.renameTo(newFile);
				
		
       if(newFile!=null  && rename)
         {
						long l1=System.currentTimeMillis();						
			 //@@Added by kameswari for the upload issue
               home	=	(BuyRatesSessionHome)LookUpBean.getEJBHome("BuyRatesSessionBean");
              remote	=	(BuyRatesSession)home.create();	
                 s=remote.processExcel(UPLOAD_PATH+pathval,loginbean);
                 if(!("6".equalsIgnoreCase(s)))
            {
               errorList  = remote.getErrorMsg(loginbean);
            }
              
				   if("Y".equalsIgnoreCase(errorMode))
         {
            		String fileval="File "+ newFile.getName()+" "+"Upload Successfuly";
						Message=fileval+"\n"+"To Upload  : -"+ "\n"+" M.SEC : "+(l1-l) +"  SEC : " + (l1-l)/(1000) + " MIN :  "+(l1-l)/(1000*60)+"\n" + "To Insert data" + s;						
						request.setAttribute("Message",Message);
            //  response.setContentType("application/vnd.ms-excel"); 
            response.setContentType("application/vnd.ms-excel;");

               response.setHeader("Content-Disposition","attachment;filename=Exceptions.xls");                  
                out = response.getWriter();   

            if("6".equalsIgnoreCase(s))
            {
                errorMessage.append("Records are successfully inserted");
                out.print("Records are successfully inserted");
            }
            else
            {
              		 
                        if(errorList!=null && errorList.size()>0)
                     {
                         rateDOB  = (RateDOB)errorList.get(0);
                         errorMessage.append("The following Records(RowId) are not inserted\n");
                          errorMessage.append("<html>");
                          errorMessage.append("<body>");
                          errorMessage.append("<table width='100%' border='1' cellspacing='0' cellpadding='0'>");
                          errorMessage.append("<tr align='center'>");
                          errorMessage.append("<td><b>SHIPMENT MODE</b></td>");
                          errorMessage.append("<td><b>CURRENCY</b></td>");
                          errorMessage.append("<td><b>WEIGHT BREAK</b></td>");
                          errorMessage.append("<td><b>WEIGHT CLASS</b></td>");
                          errorMessage.append("<td><b>RATE TYPE</b></td>");
                          errorMessage.append("<td><b>UOM</b></td>");
                          errorMessage.append("<td><b>CONSOLETYPE</b></td>");
                          errorMessage.append("<td><b>REMARKS</b></td>");
                          errorMessage.append("</tr>");
                          errorMessage.append("<tr align='center'>");
                          errorMessage.append("<td>").append(rateDOB.getShipmentMode()).append("</td>");
                          errorMessage.append("<td>").append(rateDOB.getCurrency()).append("</td>");
                          errorMessage.append("<td>").append(rateDOB.getWeightBreak()).append("</td>");
                          errorMessage.append("<td>").append(rateDOB.getWeightClass()).append("</td>");
                          errorMessage.append("<td>").append(rateDOB.getRateType()).append("</td>");
                          errorMessage.append("<td>").append(rateDOB.getUom()).append("</td>");
                          errorMessage.append("<td>").append((rateDOB.getConsoleType()!=null)?rateDOB.getConsoleType():"").append("</td>");
                          errorMessage.append("<td>").append((rateDOB.getRemarks()!=null?rateDOB.getRemarks():"")).append("</td>");
                          errorMessage.append("</tr>");
                  
                          errorMessage.append("<table width='100%' border='1' cellspacing='0' cellpadding='0'>");
                          errorMessage.append("<tr align='center'>");
                          errorMessage.append("<td><b>RowId</b></td>");
                          errorMessage.append("<td><b>ORIGIN</b></td>");
                          errorMessage.append("<td><b>DESTINATION</b></td>");
                          errorMessage.append("<td><b>CARRIER ID</b></td>");
                          errorMessage.append("<td><b>SERVICELEVEL</b></td>");
                          errorMessage.append("<td><b>FREQUENCY</b></td>");
                          if("2".equalsIgnoreCase(rateDOB.getShipmentMode()))
                          {
                              errorMessage.append("<td><b>APPROXIMATE TRANSIT DAYS</b></td>");
                          }
                          else
                          {
                              errorMessage.append("<td><b>APPROXIMATE TRANSIT TIME</b></td>");
                          }
                          errorMessage.append("<td><b>EFFECTIVE FROM</b></td>");
                          errorMessage.append("<td><b>VALID UPTO</b></td>");
                          errorMessage.append("<td><b>REMARKS</b></td>");
                          errorMessage.append("</tr>");
                          int eListSize		=	errorList.size();
                          for(int i=1;i<eListSize;i++)
                          {
                            flatRatesDOB = (FlatRatesDOB)errorList.get(i);
                             str  = eSupplyDateUtil.getDisplayStringArray(flatRatesDOB.getEffDate());
                          
                             if(flatRatesDOB.getValidUpto()!=null)
                             {
                                strn  =  eSupplyDateUtil.getDisplayStringArray(flatRatesDOB.getValidUpto());
                               
                             }
                              errorMessage.append("<tr align='center'>");
                              errorMessage.append("<td>").append(flatRatesDOB.getSlNo()).append("</td>");
                              errorMessage.append("<td>").append(flatRatesDOB.getOrigin()).append("</td>");
                              errorMessage.append("<td>").append(flatRatesDOB.getDestination()).append("</td>");
                              errorMessage.append("<td>").append(flatRatesDOB.getCarrierId()).append("</td>");
                              errorMessage.append("<td>").append(flatRatesDOB.getServiceLevel()).append("</td>");
                              errorMessage.append("<td>").append(flatRatesDOB.getFrequency()).append("</td>");
                              errorMessage.append("<td>").append(flatRatesDOB.getTransittime()).append("</td>");
                              errorMessage.append("<td>").append(str[0]).append("</td>");
                              errorMessage.append("<td>").append((flatRatesDOB.getValidUpto()!=null)?strn[0]:"").append("</td>");
                              errorMessage.append("<td>").append((flatRatesDOB.getRemarks())!=null?flatRatesDOB.getRemarks():"").append("</td>");
                              errorMessage.append("</tr>");
                    
                          }//for list_invalidDtls
                         
                            errorMessage.append("</body>");
                            errorMessage.append("</html>");
                        }//if list_invalidDtls           
               
             out.print(errorMessage.toString());
            }
                
             
         }
         else
         {
                if("6".equalsIgnoreCase(s))
                {
                     errorMsg.append("Records are successfully inserted:\n");
                     int eListSize	=	errorList.size();
                     for(int i=1;i<eListSize;i++)
                     {
                       flatRatesDOB = (FlatRatesDOB)errorList.get(i);
                       errorMsg.append(flatRatesDOB.getSlNo()+4);
                       errorMsg.append("\n");
                     }
                   }
                   else
                   {
                         errorMsg.append("The following Records(RowId) are not inserted\n");
                         int eListSize	=	errorList.size();
                         for(int i=1;i<eListSize;i++)
                         {
                           flatRatesDOB = (FlatRatesDOB)errorList.get(i);
                           errorMsg.append(flatRatesDOB.getSlNo()+4);
                           errorMsg.append(".");
                           errorMsg.append(flatRatesDOB.getRemarks());
                           errorMsg.append("\n");
                    
                         } 
                   
                   }
                
                   keyValueList = new ArrayList();
                    errorMessageObject = new ErrorMessage(errorMsg.toString(),nextNavigation); 
                    keyValueList.add(new KeyValue("Operation",operation)); 
                    keyValueList.add(new KeyValue("ErrorCode","QMS"));
                    errorMessageObject.setKeyValueList(keyValueList);
                    request.setAttribute("ErrorMessage",errorMessageObject);    
                    doDispatcher(request,response,"QMSESupplyErrorPage.jsp");//use send redirect 
  
         
         }
						newFile.delete();
			//			doDispatch(request,response);
                   }else{
					   newFile.delete();
					   throw new Exception();
				   }
					
			}catch (IllegalArgumentException Ex) {
      
			  Message="Invlid Directory";
				
			}catch(IOException iox)
			{
			 
       Message=iox.getMessage();
  		 //    s =  remote.processExcel(UPLOAD_PATH+pathval,loginbean);
			   
			}/*catch(javax.ejb.CreateException ce){
						System.out.println("Create Exception from Controller..."+ce.toString());
						Message="Could not create bean Instance";
					    request.setAttribute("Message",Message);
					    doDispatch(request,response);
			}*/catch(Exception se)
			{
			  error="Unable To Upload The File";
			}
     }
   }else if("CSV".equals(endsWith))
			{
				 doUpLoadProcess(request,response,loginbean);
			}
 }
   //@@Added by Kameswari for the WPBN issue-171210
      else if("UploadDeleteDetails".equals(subOperation))
      {
        if(!("OPER_TERMINAL".equalsIgnoreCase(loginbean.getAccessType())))
        {
        ArrayList  quotesList  = new ArrayList();
       errorMsg           =   new StringBuffer("");
          long l=System.currentTimeMillis(); 
		     PrintWriter out = response.getWriter();
        response.setContentType("text/plain");
        
        request.setCharacterEncoding("UTF-8");
        String DEFAULT_ENCODING=request.getCharacterEncoding();    
        final String contentType = request.getHeader("Content-Type");
        
      if (contentType != null && contentType.startsWith("multipart/form-data"))
      {
         try {	 
              ESupplyDateUtility eSupplyDateUtil      =  new ESupplyDateUtility();
               String dateFormat                   	=	loginbean.getUserPreferences().getDateFormat();
               eSupplyDateUtil.setPattern(dateFormat);
	  	  	   
	    	 //  UPLOAD_PATH=rb.getStrindg("uploadpath");//"C:/OC4J_10.1.3/j2ee/home/upload/";
           //  UPLOAD_PATH="C:/uploaddelete/";
        // UPLOAD_PATH= "upload/";//for local
         //  UPLOAD_PATH="bea/user_projects/domains/dhldomain/myserver/upload/";
         UPLOAD_PATH="./";// for weblogic
          //UPLOAD_PATH="/opt/bea/user_projects/domains/DHLTEST/";
              MultipartRequest multi =new MultipartRequest(request,UPLOAD_PATH,DEFAULT_MAX_POST_SIZE,DEFAULT_ENCODING);       
//MultipartRequest multi =new MultipartRequest(request,UPLOAD_PATH);       
                 errorMode = multi.getParameter("errorMode");
               File originalFile=multi.getFile("fileName");
               String name=multi.getFilesystemName("fileName");
               String orignalName=multi.getOriginalFileName("fileName");
               String fileName=name.substring(0,name.lastIndexOf("."));
               String extension=name.substring(name.lastIndexOf(".")+1,name.length());
               Date d = new Date();
               DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault());
               NumberFormat numberFormat=df.getNumberFormat();
               SimpleDateFormat formatter = new SimpleDateFormat("yyyy-mm-dd"+" "+"hh.mm.ss");
              // SimpleDateFormat formatter = new SimpleDateFormat("yyyy-mm-dd"+"hh.mm.ss");
               String pathval=fileName+formatter.format(d)+"."+extension;
         
               File newFile=new File(UPLOAD_PATH+pathval); //File Rename 
               boolean rename=originalFile.renameTo(newFile);
				
		
       if(newFile!=null  && rename)
         {
						long l1=System.currentTimeMillis();						
				//	FileUpload obj = new FileUpload();	
					//	obj.processExcel(UPLOAD_PATH+pathval, "DETAILS");
					//	s=obj.processExcel(UPLOAD_PATH+pathval);
       // s=obj.processExcel(UPLOAD_PATH+pathval,loginbean);
               home	=	(BuyRatesSessionHome)LookUpBean.getEJBHome("BuyRatesSessionBean");
              remote	=	(BuyRatesSession)home.create();	
              
                 s=remote.processExcelDelete(UPLOAD_PATH+pathval);
             if(!("6".equalsIgnoreCase(s)))
            {
               quotesList  = remote.getDeleteErrorMsg();
            }
            
				   if("Y".equalsIgnoreCase(errorMode))
         {
            		String fileval="File "+ newFile.getName()+" "+"Upload Successfuly";
						Message=fileval+"\n"+"To Upload  : -"+ "\n"+" M.SEC : "+(l1-l) +"  SEC : " + (l1-l)/(1000) + " MIN :  "+(l1-l)/(1000*60)+"\n" + "To Insert data" + s;						
						request.setAttribute("Message",Message);
              response.setContentType("application/vnd.ms-excel"); 
               response.setHeader("Content-Disposition","attachment;filename=Deletion.xls");                  
                out = response.getWriter();   
            if("6".equalsIgnoreCase(s))
            {
                errorMessage.append("Records are successfully deleted");
                out.print("Records are successfully deleted");
            }
            else
            {
                   
                    if(quotesList!=null && quotesList.size()>0)
                     {
                         errorMessage.append("The following Records(RowId) are not inserted\n");
                          errorMessage.append("<html>");
                          errorMessage.append("<body>");
                          errorMessage.append("<table width='100%' border='1' cellspacing='0' cellpadding='0'>");
                          errorMessage.append("<tr align='center'>");
                          errorMessage.append("<td><b>RowId</b></td>");
                          errorMessage.append("<td><b>ORIGIN</b></td>");
                          errorMessage.append("<td><b>DESTINATION</b></td>");
                          errorMessage.append("<td><b>CARRIER ID</b></td>");
                          errorMessage.append("<td><b>SERVICELEVEL</b></td>");
                          errorMessage.append("<td><b>FREQUENCY</b></td>");
                          //Commented by subrahmanyam for the Defect Management while moving to the production from U.A.T.
                                      /*   
                           
                          if("2".equalsIgnoreCase(flatRatesDOB.getShipmentMode()))
                          {
                              errorMessage.append("<td><b>APPROXIMATE TRANSIT DAYS</b></td>");
                            }
                          else
                          {
                              errorMessage.append("<td><b>APPROXIMATE TRANSIT TIME</b></td>");
                            }
                       */
                          errorMessage.append("<td><b>APPROXIMATE TRANSIT TIME</b></td>");//Added by subrahmanyam for the Defect Management while moving to the production from U.A.T.
                          errorMessage.append("<td><b>QUOTES APPENDED</b></td>");
                          errorMessage.append("</tr>");
                          int qListSize	=	quotesList.size();
                          for(int i=0;i<qListSize;i++)
                          {
                               
                              flatRatesDOB = (FlatRatesDOB)quotesList.get(i);
                              errorMessage.append("<tr align='center'>");
                              errorMessage.append("<td>").append(flatRatesDOB.getSlNo()).append("</td>");
                              errorMessage.append("<td>").append(flatRatesDOB.getOrigin()).append("</td>");
                              errorMessage.append("<td>").append(flatRatesDOB.getDestination()).append("</td>");
                              errorMessage.append("<td>").append(flatRatesDOB.getCarrierId()).append("</td>");
                              errorMessage.append("<td>").append(flatRatesDOB.getServiceLevel()).append("</td>");
                              errorMessage.append("<td>").append(flatRatesDOB.getFrequency()).append("</td>");
                              errorMessage.append("<td>").append(flatRatesDOB.getTransittime()).append("</td>");
                               errorMessage.append("<td>").append((flatRatesDOB.getRemarks())!=null?flatRatesDOB.getRemarks():"").append("</td>");
                              errorMessage.append("</tr>");
                                                       
                          }//for list_invalidDtls
                            errorMessage.append("</body>");
                            errorMessage.append("</html>");
                        }//if list_invalidDtls           
            out.print(errorMessage.toString());
            }
                
             
         }
         else
         {
         
                if("6".equalsIgnoreCase(s))
                {
                     errorMsg.append("Records are successfully deleted:\n");
                   
                   }
                   else
                   {
                         errorMsg.append("Quotes Appended\n");
                         int qListSize	=	quotesList.size();
                         for(int i=0;i<qListSize;i++)
                         {
                           flatRatesDOB = (FlatRatesDOB)quotesList.get(i);
                           errorMsg.append(flatRatesDOB.getSlNo());
                           errorMsg.append(".");
                           errorMsg.append(flatRatesDOB.getRemarks());
                           errorMsg.append("\n");
                         } 
                   
                   }
                   nextNavigation = "QMSBuyRatesUploadController?Operation=UpLoad&amp;subOperation=uploadDelete";
                    errorMessageObject = new ErrorMessage(errorMsg.toString(),nextNavigation); 
                      keyValueList = new ArrayList();
                    keyValueList.add(new KeyValue("Operation",operation)); 
                    keyValueList.add(new KeyValue("ErrorCode","QMS"));
                    errorMessageObject.setKeyValueList(keyValueList);
                    request.setAttribute("ErrorMessage",errorMessageObject);    
                    doDispatcher(request,response,"QMSESupplyErrorPage.jsp");//use send redirect 
  
         
         }
					//	newFile.delete();
			//			doDispatch(request,response);
                   }else{
					   newFile.delete();
					   throw new Exception();
				   }
					
			}catch (IllegalArgumentException Ex) {
      
			  Message="Invlid Directory";
				
			}catch(IOException iox)
			{
			 
       Message=iox.getMessage();
  		 //    s =  remote.processExcel(UPLOAD_PATH+pathval,loginbean);
			   
			}/*catch(javax.ejb.CreateException ce){
						System.out.println("Create Exception from Controller..."+ce.toString());
						Message="Could not create bean Instance";
					    request.setAttribute("Message",Message);
					    doDispatch(request,response);
			}*/catch(Exception se)
			{
			  error="Unable To Upload The File";
			}
     }
     }
     else
     {
                  nextNavigation = "QMSBuyRatesUploadController?Operation=UpLoad&amp;subOperation=uploadDelete";
                  errorMsg.append("You don't have sufficient previleges to delete these records.");
                   errorMessageObject = new ErrorMessage(errorMsg.toString(),nextNavigation); 
                   keyValueList = new ArrayList();
                    keyValueList.add(new KeyValue("Operation",operation)); 
                    keyValueList.add(new KeyValue("ErrorCode","QMS"));
                    errorMessageObject.setKeyValueList(keyValueList);
                    request.setAttribute("ErrorMessage",errorMessageObject);    
                    doDispatcher(request,response,"QMSESupplyErrorPage.jsp");//use send redirect 
     }
   }
   //@@WPBN issue-171210
      else if("upLoadCustomer".equalsIgnoreCase(subOperation))
      {
      doDispatcher(request, response, "etrans/BuyRatesUpLoad.jsp?Operation="+operation+"&subOperation="+subOperation);
    }
         else if("uploadDelete".equalsIgnoreCase(subOperation)){
      doDispatcher(request, response, "etrans/BuyRatesUpLoad.jsp?Operation="+operation+"&subOperation="+subOperation);
         }

    else{
    
      doDispatcher(request, response, "etrans/BuyRatesUpLoad.jsp?Operation="+operation+"&subOperation="+operation);
     
          }
      }   
        else if("DownLoad".equals(operation))
      {
        if("DownLoad".equals(subOperation))
        {
          doDownLoadProcess(request,response,loginbean);
        }else
        {
          doDispatcher(request, response, "etrans/BuyRatesDownLoad.jsp?Operation="+operation+"&subOperation="+operation);
        }        
      }
	  else
      {
          throw new Exception();
      }
    }catch(Exception e)
    {
            e.printStackTrace();
            //Logger.info(FILE_NAME,"Exception in doPost-->"+e);
            logger.info(FILE_NAME+"Exception in doPost-->"+e);
            doForwardToErrorPage(loginbean,request,response);     
    }
    //End of Id Condition
  
			
} 
  private void doUpLoadProcess(HttpServletRequest request, HttpServletResponse response,ESupplyGlobalParameters loginbean)
  {
          ArrayList       list_insertedDtls       =   null;
          ArrayList       list_invalidDtls       =   null;
          RateDOB         rateDOB         =   null;
          FlatRatesDOB    flatRatesDOB    =   null;  
          BuyRatesSessionHome 	home      =   null;
          BuyRatesSession 		remote      =   null;
          String              remarks     =   null;
          StringBuffer         errorMsg    =   null;
          int               listSize      =   0;
          ArrayList						keyValueList	= null;
          StringBuffer				errorMessage	=	null;
          ErrorMessage				errorMessageObject= null;
          String              nextNavigation = "QMSBuyRatesUploadController?Operation=UpLoad&subOperation=";
          HttpSession 			    session 	      =	null;
          String              operation   = null;
          String              errorMode   = null;
          PrintWriter         out         = null;
          String              str[]       = null;
          String              strn[]      = null;
          long   stTime = 0;
          try
          {
            operation       =  request.getParameter("Operation");
            session 	      =	request.getSession();
            keyValueList	= new ArrayList();
            errorMessage	=	new StringBuffer();
            errorMsg    =  new StringBuffer("");
            stTime = new Date().getTime();
            
            rateDOB = doGetDetails(request,response,loginbean);  
                        
 
            errorMode = (String)request.getAttribute("errorMode");
            
            request.setAttribute("buyRateDOB",rateDOB);
             //System.out.println(new Date());
             //@@Added by Kameswari for the WPBN issue-15581
            ESupplyDateUtility eSupplyDateUtil =  new ESupplyDateUtility();
            String dateFormat	=	loginbean.getUserPreferences().getDateFormat();
		        eSupplyDateUtil.setPattern(dateFormat);
            //@@WPBN issue-15581
            if(rateDOB!=null)
            {
                home	=	(BuyRatesSessionHome)LookUpBean.getEJBHome("BuyRatesSessionBean");
                remote	=	(BuyRatesSession)home.create();	
                list_insertedDtls = rateDOB.getRateDtls();
                remarks = rateDOB.getRemarks();
                 if(list_insertedDtls!=null && list_insertedDtls.size()>0)
                {
                 
                  rateDOB = remote.upLoadBuyRates(rateDOB,loginbean);
                }else
                {
                  throw new FoursoftException("Invalid data");
                }
               
                
                 if(remarks==null || "".equals(remarks))
                {
                   list_insertedDtls = rateDOB.getRateDtls();
                   list_invalidDtls  = rateDOB.getInvalidList();
                  if(errorMode.equalsIgnoreCase("N"))
                  {                  
                   if((list_invalidDtls == null || list_invalidDtls.size()<=0 ) && list_insertedDtls!=null && list_insertedDtls.size()>0)
                   {
                     errorMsg.append("Records are successfully inserted:\n");
                     listSize = list_insertedDtls.size();
                     for(int i=0;i<listSize;i++)
                     {
                       flatRatesDOB = (FlatRatesDOB)list_insertedDtls.get(i);
                       errorMsg.append(flatRatesDOB.getSlNo()+4);
                       errorMsg.append("\n");
                     }
                     //keyValueList.add(new KeyValue("ErrorCode","RSI"));
                     
                   }
                   if(list_invalidDtls!=null && list_invalidDtls.size()>0)
                   {
                         errorMsg.append("The following Records(RowId) are not inserted\n");
                         listSize = list_invalidDtls.size();                   
                         for(int i=0;i<listSize;i++)
                         {
                           flatRatesDOB = (FlatRatesDOB)list_invalidDtls.get(i);
                           errorMsg.append(flatRatesDOB.getSlNo()+4);
                           errorMsg.append(".");
                           errorMsg.append(flatRatesDOB.getRemarks());
                           errorMsg.append("\n");
                         } 
                   
                   }
                    errorMessageObject = new ErrorMessage(errorMsg.toString(),nextNavigation); 
                    keyValueList.add(new KeyValue("Operation",operation)); 
                    keyValueList.add(new KeyValue("ErrorCode","QMS"));
                    errorMessageObject.setKeyValueList(keyValueList);
                    request.setAttribute("ErrorMessage",errorMessageObject);    
                    doDispatcher(request,response,"QMSESupplyErrorPage.jsp");//use send redirect 
                    //session.setAttribute("ErrorMessageObj",errorMessageObject);
                    //response.sendRedirect("QMSESupplyErrorPage.jsp");                     
                   //sendredirect
                   
                  }else if(errorMode.equalsIgnoreCase("Y"))
                  {
                      response.setContentType("application/vnd.ms-excel"); 
                      response.setHeader("Content-Disposition","attachment;filename=Exceptions.xls");                  
                      out = response.getWriter();
                    
                     if(list_insertedDtls!=null && list_insertedDtls.size()>0){
                        
                        errorMessage.append("Records are successfully inserted:\n");
                     }
                     if(list_invalidDtls!=null && list_invalidDtls.size()>0)
                     {
                       errorMessage.append("The following Records(RowId) are not inserted\n");
                       listSize = list_invalidDtls.size();
                       
                      
                            
                            
                            
                            if(remarks!=null || !"".equals(remarks))
                            {
                              //@@Commented by kameswari for the WPBN issue-15581
                               /* errorMessage.append("SHIPMENT MODE:\t");
                                errorMessage.append("CURRENCY:\t");
                                errorMessage.append("WEIGHT BREAK:\t");
                                errorMessage.append("RATE TYPE:\t");
                                errorMessage.append("UOM:\t");
                                errorMessage.append("CONSOLETYPE:\t");
                                errorMessage.append("REMARKS:\t" );
                                errorMessage.append(rateDOB.getShipmentMode()+"\t");
                                errorMessage.append(rateDOB.getCurrency()+"\t");
                                errorMessage.append(rateDOB.getWeightBreak()+"\t");
                                errorMessage.append(rateDOB.getWeightClass()+"\t");
                                errorMessage.append(rateDOB.getRateType()+"\t");
                                errorMessage.append(rateDOB.getUom()+"\t");
                                errorMessage.append(rateDOB.getConsoleType()+"\t");
                                errorMessage.append(rateDOB.getRemarks()+"\t");
                                errorMessage.append("\n");*/
                                //@@WPBN issue-15581
                                //@@Added by kameswari for the WPBN issue-15581
                                errorMessage.append("<html>");
                                errorMessage.append("<body>");
                                errorMessage.append("<table width='100%' border='1' cellspacing='0' cellpadding='0'>");
                                errorMessage.append("<tr align='center'>");
                                errorMessage.append("<td><b>SHIPMENT MODE</b></td>");
                                errorMessage.append("<td><b>CURRENCY</b></td>");
                                errorMessage.append("<td><b>WEIGHT BREAK</b></td>");
                                errorMessage.append("<td><b>WEIGHT CLASS</b></td>");
                                errorMessage.append("<td><b>RATE TYPE</b></td>");
                                errorMessage.append("<td><b>UOM</b></td>");
                                errorMessage.append("<td><b>CONSOLETYPE</b></td>");
                                errorMessage.append("<td><b>REMARKS</b></td>");
                                errorMessage.append("</tr>");
                                errorMessage.append("<tr align='center'>");
                                errorMessage.append("<td>").append(rateDOB.getShipmentMode()).append("</td>");
                                errorMessage.append("<td>").append(rateDOB.getCurrency()).append("</td>");
                                errorMessage.append("<td>").append(rateDOB.getWeightBreak()).append("</td>");
                                errorMessage.append("<td>").append(rateDOB.getWeightClass()).append("</td>");
                                errorMessage.append("<td>").append(rateDOB.getRateType()).append("</td>");
                                errorMessage.append("<td>").append(rateDOB.getUom()).append("</td>");
                                errorMessage.append("<td>").append(rateDOB.getConsoleType()).append("</td>");
                                errorMessage.append("<td>").append(rateDOB.getRemarks()).append("</td>");
                                errorMessage.append("</tr>");
                                //@@WPBN issue-15581
                               
                            }
                            if(list_invalidDtls!=null && list_invalidDtls.size()>0)
                            {
                            
                              listSize = list_invalidDtls.size();
                               //@@Commented by kameswari for the WPBN issue-15581
                              /*errorMessage.append("RowId");
                              errorMessage.append("ORIGIN:\t");
                              errorMessage.append("DESTINATION:\t" );
                              errorMessage.append("CARRIER ID:\t" );
                              errorMessage.append("SERVICELEVEL:\t" );
                              errorMessage.append("FREQUENCY:\t" );
                              errorMessage.append("TRANSIT TIME:\t" ); 
                              errorMessage.append("EFFECTIVE FROM:\t" );  
                              errorMessage.append("VALID UPTO:\t" );
                              errorMessage.append("REMARKS:\t" );  
                              errorMessage.append("\n");
                              //@@WPBN issue-15581
                              
                             out.print("ORIGIN:\t" );
                             out.print("DESTINATION:\t" );
                             out.print("CARRIER ID:\t" );
                             out.print("SERVICELEVEL:\t" );
                             out.print("FREQUENCY:\t" );
                             out.print("TRANSIT TIME:\t" ); 
                             out.print("EFFECTIVE FROM:\t" );  
                             out.print("VALID UPTO:\t" );
                             out.println("REMARKS:\t" );  
                             */
                             
                             
                             //@@Added by kameswari for the WPBN issue-15581
                                errorMessage.append("<table width='100%' border='1' cellspacing='0' cellpadding='0'>");
                                errorMessage.append("<tr align='center'>");
                                errorMessage.append("<td><b>RowId</b></td>");
                                errorMessage.append("<td><b>ORIGIN</b></td>");
                                errorMessage.append("<td><b>DESTINATION</b></td>");
                                errorMessage.append("<td><b>CARRIER ID</b></td>");
                                errorMessage.append("<td><b>SERVICELEVEL</b></td>");
                                errorMessage.append("<td><b>FREQUENCY</b></td>");
                                if("2".equalsIgnoreCase(rateDOB.getShipmentMode()))
                                {
                                  errorMessage.append("<td><b>APPROXIMATE TRANSIT DAYS</b></td>");
                                }
                                else
                                {
                                  errorMessage.append("<td><b>APPROXIMATE TRANSIT TIME</b></td>");
                                }
                                errorMessage.append("<td><b>EFFECTIVE FROM</b></td>");
                                errorMessage.append("<td><b>VALID UPTO</b></td>");
                                errorMessage.append("<td><b>REMARKS</b></td>");
                                errorMessage.append("</tr>");
                                //@@WPBN issue-15581
                              for(int i=0;i<listSize;i++)
                              {
                                flatRatesDOB = (FlatRatesDOB)list_invalidDtls.get(i);
                                remarks      = flatRatesDOB.getRemarks();
                                if(remarks!=null && !"".equals(remarks))
                                {
                                     //@@Added by kameswari for the WPBN issue-15581  
                                      str  = eSupplyDateUtil.getDisplayStringArray(flatRatesDOB.getEffDate());
                                     
                                   if(flatRatesDOB.getValidUpto()!=null)
                                   {
                                      strn  =  eSupplyDateUtil.getDisplayStringArray(flatRatesDOB.getValidUpto());
                                     
                                   }
                                   //@@WPBN issue-15581 
                                   
                                  //@@Commented by kameswari for the WPBN issue-15581
                                   /*errorMessage.append((flatRatesDOB.getSlNo()+4)+"\t");
                                   errorMessage.append(flatRatesDOB.getOrigin()+"\t");
                                   errorMessage.append(flatRatesDOB.getDestination()+"\t");
                                   errorMessage.append(flatRatesDOB.getCarrierId()+"\t");
                                   errorMessage.append(flatRatesDOB.getServiceLevel()+"\t"); 
                                   errorMessage.append(flatRatesDOB.getFrequency()+"\t");
                                   errorMessage.append(flatRatesDOB.getTransittime()+"\t");
                                   errorMessage.append(flatRatesDOB.getEffDate()+"\t");
                                   errorMessage.append((flatRatesDOB.getValidUpto()!=null)?flatRatesDOB.getValidUpto().toString():""+"\t");
                                   errorMessage.append(flatRatesDOB.getRemarks());    
                                   errorMessage.append("\n");
                                
                                   out.print(flatRatesDOB.getOrigin()+"\t");
                                   out.print(flatRatesDOB.getDestination()+"\t");
                                   out.print(flatRatesDOB.getCarrierId()+"\t");
                                   out.print(flatRatesDOB.getServiceLevel()+"\t"); 
                                   out.print(flatRatesDOB.getFrequency()+"\t");
                                   out.print(flatRatesDOB.getTransittime()+"\t");
                                   out.print(flatRatesDOB.getEffDate()+"\t");
                                   out.print(flatRatesDOB.getValidUpto()+"\t");
                                   out.println(flatRatesDOB.getRemarks());  
                                   */
                                   //@@WPBN issue-15581
                                   
                                  //@@Added by kameswari for the WPBN issue-15581
                                  errorMessage.append("<tr align='center'>");
                                  errorMessage.append("<td>").append(flatRatesDOB.getSlNo()+4).append("</td>");
                                  errorMessage.append("<td>").append(flatRatesDOB.getOrigin()).append("</td>");
                                  errorMessage.append("<td>").append(flatRatesDOB.getDestination()).append("</td>");
                                  errorMessage.append("<td>").append(flatRatesDOB.getCarrierId()).append("</td>");
                                  errorMessage.append("<td>").append(flatRatesDOB.getServiceLevel()).append("</td>");
                                  errorMessage.append("<td>").append(flatRatesDOB.getFrequency()).append("</td>");
                                  errorMessage.append("<td>").append(flatRatesDOB.getTransittime()).append("</td>");
                                  errorMessage.append("<td>").append(str[0]).append("</td>");
                                  errorMessage.append("<td>").append((flatRatesDOB.getValidUpto()!=null)?strn[0]:"").append("</td>");
                                  errorMessage.append("<td>").append(flatRatesDOB.getRemarks()).append("</td>");
                                  errorMessage.append("</tr>");
                                  //@@WPBN issue-15581
                                }
                                  //@@Added by kameswari for the WPBN issue-15581
                               //@@Commented by subrahmanyam for the WPBN issue-146449
                                //errorMessage.append("</body>");
                               // errorMessage.append("</html>");
                                //@@WPBN issue-15581
                              }//for list_invalidDtls
                                  //@@Added by subrahmanyam for the WPBN issue-146449
                                errorMessage.append("</body>");
                                errorMessage.append("</html>");
                            }//if list_invalidDtls           
                        
                      }
                      out.print(errorMessage.toString());                       
                   }
                }else
                {
                  errorMsg.append("The records are not Inserted:\n");
                  //errorMsg.append(remarks);//@@Commentd by subrahmanyam for CustomerUploadMandatory Fields
                  errorMsg.append(remarks!=null?remarks:"");//@@Added by subrahmanyam for CustomerUploadMandatory Fields
                  //Logger.info(FILE_NAME,""+(stTime-(new Date().getTime())));
                  logger.info(FILE_NAME+""+(stTime-(new Date().getTime())));
                  errorMessageObject = new ErrorMessage(errorMsg.toString(),nextNavigation); 
                  keyValueList.add(new KeyValue("Operation",operation)); 	
                  keyValueList.add(new KeyValue("ErrorCode","QMS"));
                  errorMessageObject.setKeyValueList(keyValueList);
                  request.setAttribute("ErrorMessage",errorMessageObject);    
                  doDispatcher(request,response,"QMSESupplyErrorPage.jsp");//use send redirect 
                 // session.setAttribute("ErrorMessageObj",errorMessageObject);
                  //response.sendRedirect("QMSESupplyErrorPage.jsp");                   
                }
               
                
                
            }else
            {
              throw new FoursoftException("Invalid data");
            }
            
                        
          }catch(FoursoftException e)
          {
            e.printStackTrace();
            
            //Logger.info(FILE_NAME,"Invalid data--->");
            logger.info(FILE_NAME+"Invalid data--->");
            
            //Logger.info(FILE_NAME,""+(stTime-(new Date().getTime())));
            logger.info(FILE_NAME+""+(stTime-(new Date().getTime())));
            
            doForwardToErrorPage(loginbean,request,response);
          }
          catch(Exception e)
          {
            e.printStackTrace();
            //Logger.info(FILE_NAME,""+(stTime-(new Date().getTime())));
             logger.info(FILE_NAME+""+(stTime-(new Date().getTime())));
            //Logger.info(FILE_NAME,"Exception while calling bean the data"+e);
            logger.info(FILE_NAME+"Exception while calling bean the data"+e);
            doForwardToErrorPage(loginbean,request,response);              
          }
          
  }
  
//Modified by ashlesh for DHL CR 154393 on 23/01/2009
   private void doCustomerUpLoadProcess(HttpServletRequest request, HttpServletResponse response,ESupplyGlobalParameters loginbean)
  {
          SetUpSessionHome 	home      =   null;
          SetUpSession 		remote      =   null;
          String              remarks     =   null;
          StringBuffer         errorMsg    =   null;
 
          ArrayList						keyValueList	= null;
          ErrorMessage				errorMessageObject= null;
          String              nextNavigation = "QMSBuyRatesUploadController?Operation=UpLoad&subOperation=upLoadCustomer";
          HttpSession 			    session 	      =	null;
          String              operation   = null;
          String              errorMode   = null;
          PrintWriter         out         = null;
          ArrayList           list        = null;
          StringBuffer				errorMessage	=	null;
          try
          {  
            operation       =  request.getParameter("Operation");
            session 	      =	request.getSession();
            keyValueList	= new ArrayList();
          //  errorMessage	=	new StringBuffer();
            errorMsg    =  new StringBuffer("");
            
            list = doGetCustomerDetails(request,response,loginbean);  
                        
              errorMode = request.getParameter("errorMode");
            ESupplyDateUtility eSupplyDateUtil =  new ESupplyDateUtility();
            String dateFormat	=	loginbean.getUserPreferences().getDateFormat();
		        eSupplyDateUtil.setPattern(dateFormat);
            
            if(list!=null)
            {
                home	=	(SetUpSessionHome)LookUpBean.getEJBHome("SetUpSessionBean");
                remote	=	(SetUpSession)home.create();	
               /* CustomerModel customerModel=new CustomerModel();
                Address adr  =new Address(); 
                customerModel=(CustomerModel)list.get(0);
                adr          =(Address)list.get(1);
                ArrayList li=  (ArrayList)list.get(2);*/
                try{
                  StringBuffer customerIds=remote.uploadCustomerDetails(list,loginbean);
                  
                  if(customerIds!=null)
                    customerIds=customerIds.deleteCharAt(customerIds.lastIndexOf(","));
                  errorMsg.append("Customer Ids"+" "+customerIds.toString()+" registered successfully");
                  errorMessageObject = new ErrorMessage(errorMsg.toString(),nextNavigation); 
                  keyValueList.add(new KeyValue("Operation",operation)); 
                  keyValueList.add(new KeyValue("ErrorCode","QMS"));
                  errorMessageObject.setKeyValueList(keyValueList);
                  request.setAttribute("ErrorMessage",errorMessageObject);    
                  doDispatcher(request,response,"QMSESupplyErrorPage.jsp");//use send redirect 
              
                }catch(Exception e)	{
                  e.printStackTrace();
                  errorMsg.append("The records are not Inserted:\n");
                  //errorMsg.append(remarks);//@@Commentd by subrahmanyam for CustomerUploadMandatory Fields
                  errorMsg.append(remarks!=null?remarks:"");//@@Added by subrahmanyam for CustomerUploadMandatory Fields
                  errorMessageObject = new ErrorMessage(errorMsg.toString(),nextNavigation); 
                  keyValueList.add(new KeyValue("Operation",operation)); 	
                  keyValueList.add(new KeyValue("ErrorCode","QMS"));
                  errorMessageObject.setKeyValueList(keyValueList);
                  request.setAttribute("ErrorMessage",errorMessageObject);    
                  doDispatcher(request,response,"QMSESupplyErrorPage.jsp");
               }
            } 
          } 
          catch(Exception e)
          {
            e.printStackTrace();
            logger.info(FILE_NAME+"Exception while calling bean the data"+e);
            doForwardToErrorPage(loginbean,request,response);              
          }
          
  }
  
  
private ArrayList doGetCustomerDetails(HttpServletRequest request, HttpServletResponse response,ESupplyGlobalParameters loginbean)throws FoursoftException
  {
      CustomerModel customerModel   = new  CustomerModel();
	    MultipartParser		mp                =   null;
	    Part					part              =   null;
	    String				name              =   null;
      String				value             =   null;
      String				operation         =   null;
	    FilePart				filePart          =   null;
	    ParamPart				paramPart         =   null;
	    FileInputStream		fileRead          =   null;
      String				fileName          =   null;
      long					size              = 0;
	    BufferedReader		br                =   null;
      String				data              =   null;      
      String				errorMode         =   null;
      int					delimIndex        = 0;
      int					beginIndex        = 0;
      String				tempString        =   null;
      String				subString         =   null;
      String[]				strToken          =  null;
      StringBuffer			remarks           =   null;
      int					count             =   0;
      int					lineNo            =   0;
      int        rowId                 =  0;
      String				terminalId            =   null;
      String				abbrName              =   null;
      String			  companyName           =   null;
      String				addressType           =   null;
      String				contactPerson         =   null;
      String				designation         =   null;
      String				department            =   null;
      String				addressLine1  =   null; 
      String				addressLine2            =   null;
      String				addressLine3            =   null;
      String				city            =   null;
      String				state            =   null;
      String				countryId            =   null;
      String				postalCode            =   null;
      String				phoneNo            =   null;
      String				faxNo            =   null;
      String			 emailId            =   null;
      int					parseInteger	  =   0;
      String remarksList = null;
      ArrayList   list    = new ArrayList();;
      Address addressObj=new Address();
      ArrayList costAddressList	=	new ArrayList();
      ArrayList custContactDtl=new ArrayList();
      ArrayList remarkList= new ArrayList();
      OperationsImpl operationsImpl	= new OperationsImpl();
      ArrayList   customerDtl       =new ArrayList();
      ArrayList  customerDtl1     =new ArrayList();
      int counter=1;
      boolean flag = false;
      int temprowid =0;
      String  tempaddresstype="";//Added by subrahmanyam for CustomerUpload on 04/May/09
      
     try
      {
          mp                =   new MultipartParser(request, 10*1024*1024); // 10MB
          
          while ((part = mp.readNextPart()) != null)
          {
          
            name = part.getName();
         
            if (name != null)
                    name = name.trim();
            if (part.isParam())
            {   				  
                paramPart         =        (ParamPart) part;
                value             =        paramPart.getStringValue();
                
            
                if(name.equals("errorMode")){
                  errorMode = value;
                }
                if(name.equals("Operation")){
                  operation = value;
                }
    
            }
           else if (part.isFile())
           {
              filePart         =         (FilePart) part;
              fileName         =         filePart.getFileName();
             
              if (fileName != null){                             
               
				        fileName = fileName.trim();
                size = filePart.writeTo(new File("./"));

              }else{ 

                // the field did not contain a file
                throw new Exception("Please specify a file");

              }
          }
        }
        
        
        fileRead            =        new FileInputStream(fileName);	 
        br                  =        new BufferedReader(new InputStreamReader(fileRead));
        br.readLine();// empty Line
      
        data                =        br.readLine();
        
        strToken            =        data.split(",");
  			int totalColumnsCount         =        strToken.length; 
        int i=0;
        String countryIds[] =  null  ;
        ArrayList countryIdList  = new ArrayList();
        countryIds 		= operationsImpl.getCountryIds();
        int countryIdsLen	=	countryIds.length;
			  for(int h=0;h<countryIdsLen;h++){
           	int idx = countryIds[h].indexOf('(');     //braces change from angular to square
			      int idy	= countryIds[h].indexOf(')');
			      String cId = countryIds[h].substring(idx+2,idy-1);
			      countryIdList.add(cId);
			  }
        int rows=0;//@@Added by subrahmanyam for customerUpload 04/may/09
        
        
      	while(!data.equals("") && data!=null)
        {   
            data           +=        ",";
            
            tempString     =         "";
            beginIndex     =         0;
            strToken         =     data.split(",");
            remarks         =     new StringBuffer("");
            int strTokenLen		=	strToken.length;
            for(int noOfCols=0;noOfCols<strTokenLen; noOfCols++) 
            {
                delimIndex  =  data.indexOf(",");
                if(delimIndex==0){					
                    tempString=tempString + " ,";
                }
                else {				
                    subString=data.substring(beginIndex,delimIndex);
                    tempString=tempString + subString+",";				
                } 
                data             =     data.substring(delimIndex+1);
                beginIndex       =     0;
            } 
            data             =     tempString.substring(0,tempString.length());
            strToken         =     data.split(",");
            
            int temp = 0;
              if(!strToken[0].trim().equalsIgnoreCase("")){
                 rowId     =  Integer.parseInt(strToken[0]);

              }
                 terminalId	    = strToken[1].toUpperCase();
                 abbrName 	    = strToken[2].toUpperCase();
                 companyName 	  = strToken[3].toUpperCase();
                 addressType 	  = strToken[4].toUpperCase();
                 contactPerson 	= strToken[5].toUpperCase();
                 designation 	  = strToken[6].toUpperCase();
                 department 	  = strToken[7].toUpperCase();
                 addressLine1	  = strToken[8].toUpperCase();
                 addressLine2 	= strToken[9].toUpperCase();
                 addressLine3 	= strToken[10].toUpperCase();
                 city 	        = strToken[11].toUpperCase();
                 state 	        = strToken[12].toUpperCase();
                 countryId	    = strToken[13].toUpperCase();
                 postalCode 	  = strToken[14].toUpperCase();
                phoneNo 	      = strToken[15].toUpperCase();
                faxNo 	        = strToken[16].toUpperCase();
                emailId 	      = strToken[17];
                               
                 if(tempaddresstype.equalsIgnoreCase(addressType))
                {                   
                     list = (ArrayList)customerDtl.get(customerDtl.size()-1);
                     remarkList =  (ArrayList)list.get(3);
                     remarks   =  (StringBuffer)remarkList.get(0);
                     remarks.append("Pickup/Delivery/Billing is mandatory.\n");
                     remarkList.remove(0);
                     remarkList.add(0,remarks);
                     list.remove(3);
                     list.add(remarkList);
                     customerDtl.remove(customerDtl.size()-1);
                     customerDtl.add(list);
                     flag=true;
                     remarks = new StringBuffer("");
                  
                  }
                 else
                 {
                   tempaddresstype =addressType;
                 }
              
            if(strToken.length>0)
            {   
                if(rowId==counter){
                
                    list            = new ArrayList();  
                    customerModel   = new CustomerModel();
                    addressObj      = new Address();
                    custContactDtl  = new ArrayList();
                    remarkList      = new ArrayList();
                    list.add(customerModel);
                    list.add(addressObj);
                    list.add(custContactDtl);
                    list.add(remarkList);
                    customerDtl.add(list);
                    i=0;
                    counter++;
                }
               
                
               
                 if(i==0 ){
                
                 if(terminalId.length()<2){
                         remarks.append("Mandatory fields are not provided (Terminal Id). \n");
                  }else if(!operationsImpl.getTerminalIds().contains(terminalId)){
                        remarks.append("Invalid (Terminal Id). \n");
                  }
                  if(abbrName.length()<2){
                         remarks.append("Mandatory fields are not provided (Abbreviation Name). \n");
                  }// modified by phani sekhar for wpbn 178967 on 20090811 
                  else if(abbrName.length()>4)
                  {                   
                  remarks.append("Abbreviation Name is more than Four characters (Abbreviation Name). \n");
                  }
                  if(companyName.length()<2){
                         remarks.append("Mandatory fields are not provided (Company Name). \n");
                  }
                  if(addressType.length()<2){
                         remarks.append("Mandatory fields are not provided (Address Type). \n");
                  }else if(!addressType.equalsIgnoreCase("Default")) {
                        remarks.append("Address Type should be Default \n");
                  }
                 if(contactPerson.length()<2){
                    remarks.append("Mandatory fields are not provided (Contact Person). \n");
                  }
                  if(addressLine1.length()<2){
                    remarks.append("Mandatory fields are not provided (Address Line 1). \n");
                  }
//@@ Commented by subrahmanyam for CustomerUpload Mandatory fields.                  
                 /* if(addressLine2.length()<2){
                    remarks.append("Mandatory fields are not provided (Address Line 2). \n");
                  }*/
                  if(city.length()<2){
                    remarks.append("Mandatory fields are not provided (City). \n");
                  }
//@@ Commented by subrahmanyam for CustomerUpload Mandatory fields.                                    
                 /* if(state.length()<2){
                    remarks.append("Mandatory fields are not provided (State). \n");
                  }*/
                  if(countryId.length()<2){
                    remarks.append("Mandatory fields are not provided (Country Id). \n");
                  } else if(!countryIdList.contains(countryId)){
                     remarks.append("Invalid (Country Id). \n");
                  }
                  if(phoneNo.length()>2){
                  String regExp = "(?:(?:[\\+][0-9]{2,5})(\\s|\\-))?[0-9]*"; 
                    if(!phoneNo.matches(regExp)){
                        remarks.append("Please enter numeric values Only. (Phone No) \n");
                   } 
                 }
                 if(faxNo.length()>2){
                        String regExp = "(?:(?:[\\+][0-9]{2,5})(\\s|\\-))?[0-9]*"; 
                        if(!faxNo.matches(regExp)){
                            remarks.append("Please enter numeric values Only. (Fax No) \n");
                        } 
                      }
 
                     if(emailId.length()<2){
                        remarks.append("Mandatory fields are not provided (Email Id). \n");
                      }else {
                         Pattern p = Pattern.compile(".+@.+\\.[a-z]+");
                         Matcher m = p.matcher(emailId);
                        //check whether match is found 
                         boolean matchFound = m.matches();
                         if (!matchFound)
                          remarks.append("Please enter valid (Email Id). \n");
                       } 
                       
//@@ Added by subrahmanyam for CustomerUpload Mandatory fields.     
          /*  if( (faxNo!=null && faxNo.length()>2 )|| (emailId!=null && emailId.length()<2))
            {
                  if(faxNo.length()>2){
                        String regExp = "(?:(?:[\\+][0-9]{2,5})(\\s|\\-))?[0-9]*"; 
                        if(!faxNo.matches(regExp)){
                            remarks.append("Please enter numeric values Only. (Fax No) \n");
                        } 
                      }
                      
                     if(emailId.length()<2){
                        remarks.append("Mandatory fields are not provided (Email Id). \n");
                      }else {
                         Pattern p = Pattern.compile(".+@.+\\.[a-z]+");
                         Matcher m = p.matcher(emailId);
                        //check whether match is found 
                         boolean matchFound = m.matches();
                         if (!matchFound)
                          remarks.append("Please enter valid (Email Id). \n");
                       }
            }*/
//@@ ended by subrahmanyam   for CustomerUpload Mandatory fields.
                  }
                
               if(i>0){
                
                
                 if(addressType.length()<2){
                         remarks.append("Mandatory fields are not provided (Address Type). \n");
                  }
                 if(contactPerson.length()<2){
                         remarks.append("Mandatory fields are not provided (Contact Person). \n");
                  }
                
                  
//@@ Commented by subrahmanyam for CustomerUpload Mandatory Fields   

/*                  
 *      if(designation.length()<2){
                         remarks.append("Mandatory fields are not provided (Designation). \n");
                  }
                  if(department.length()<2){
                         remarks.append("Mandatory fields are not provided (Department). \n");
                  }
                  if(postalCode.length()<2){
                         remarks.append("Mandatory fields are not provided (Postal Code). \n");
                  } 
                  if(phoneNo.length()<2){
                       remarks.append("Mandatory fields are not provided (Phone No). \n");
                  } else {
                    String regExp = "(?:(?:[\\+][0-9]{2,5})(\\s|\\-))?[0-9]*";
                    if(!phoneNo.matches(regExp)){
                        remarks.append("Please enter numeric values Only. (Phone No) \n");
                  } 
                }
                
                if(faxNo.length()<2){
                         remarks.append("Mandatory fields are not provided (Fax No). \n");
                  }else{
                    String regExp = "(?:(?:[\\+][0-9]{2,5})(\\s|\\-))?[0-9]*";
                    if(!faxNo.matches(regExp)){
                        remarks.append("Please enter numeric values Only. (Fax No) \n");
                    } 
                  }
                  if(emailId.length()<2){
                         remarks.append("Mandatory fields are not provided (Email Id). \n");
                  }else {
                     Pattern p = Pattern.compile(".+@.+\\.[a-z]+");
                     Matcher m = p.matcher(emailId);
                    //check whether match is found 
                     boolean matchFound = m.matches();
                     if (!matchFound)
                      remarks.append("Please enter valid (Email Id). \n");
                   } 
*/                
               
                }
//@@Added by subrahmanyam for CustomerUpload on 04-MAY-09                
             /* else
                remarks.append("\n Pickup/Delivery/Billing Is Mandatory \n");*/
//@@Ended by subrahmanyam  for CustomerUpload on 04-MAY-09                                
       
        if(remarks!=null && remarks.length()>0){
          flag=true;
        }
        remarkList.add(i,remarks);
                  
        if(i>0){
          CustContactDtl custDtl=new CustContactDtl();
          if(addressType.equalsIgnoreCase("PickUp")){
               addressType="P";
            }else if(addressType.equalsIgnoreCase("Delivery")){
               addressType="D";
            }else if(addressType.equalsIgnoreCase("Billing")){
               addressType="B";
            }
				  custDtl.setAddrType(addressType);
				  custDtl.setContactPerson(contactPerson);
				  custDtl.setDesignation(designation);
				  custDtl.setDept(department);
				  custDtl.setFax(faxNo);
				  custDtl.setEmail(emailId);
				  custDtl.setZipCode(postalCode);
          custDtl.setContact(phoneNo);
          custContactDtl.add(custDtl); 
          customerModel.setContactDtl(custContactDtl);
        }
         if(i==0){
            customerModel.setAbbrName(abbrName);
            if(addressType.equalsIgnoreCase("Default")){
               addressType="D";
            }
            customerModel.setAddressType(addressType);
            customerModel.setContactName(contactPerson);
            customerModel.setDesignation(designation);
            customerModel.setCompanyName(companyName);
            customerModel.setOpEmailId(emailId);
            customerModel.setTerminalId(terminalId);
            customerModel.setcustType("U");
            if(abbrName!=null && abbrName.length()>0){
              customerModel.setTypeOfCustomer("Customer");
            }else{
              customerModel.setTypeOfCustomer("Corporate");
            }
              addressObj.setAddressLine1(addressLine1);
              addressObj.setAddressLine2(addressLine2);
              addressObj.setAddressLine3(addressLine3);
              addressObj.setCity(city);
              addressObj.setState(state);
              addressObj.setFax(faxNo);
              addressObj.setEmailId(emailId);
              addressObj.setPhoneNo(phoneNo);
              addressObj.setCountryId(countryId);
              addressObj.setDesignation(designation);
              addressObj.setZipCode(postalCode);
              // addressObj.setAddressLine3(request.getParameter(addressLine3)); // Commented by subrahmanyam for the pbn id: 187382 on 23/OCT-09
         }
        
    }
      data=br.readLine();	
      
				if (data==null)
					break;
        i++;
        rows++;
					
	  }
  
    if(rows==0||"DEFAULT".equalsIgnoreCase(tempaddresstype))
    {
       remarks  = (StringBuffer)remarkList.get(0);
      remarks.append("\n Pickup/Delivery/Billing is Mandatory. \n");
      remarkList.add(0,remarks);
      flag =true;
    }
       request.setAttribute("errorMode",errorMode);
       request.removeAttribute("RateDOB");
       request.setAttribute("customerDtl",customerDtl);  
      if(customerDtl!=null && customerDtl.size()>0){
        if(flag==true){
          throw new FoursoftException("Invalid data");
        }
     }
      
  }
  catch(Exception e) {
      e.printStackTrace();
      logger.info(FILE_NAME+"Exception while calling bean the data"+e);
      throw new FoursoftException(); 
}
return customerDtl;     
     
}
  
  
  
   private RateDOB doGetDetails(HttpServletRequest request, HttpServletResponse response,ESupplyGlobalParameters loginbean)throws FoursoftException
  {
	    MultipartParser		mp                =   null;
	    Part					part              =   null;
	    String				name              =   null;
      String				value             =   null;
      String				operation         =   null;
	    FilePart				filePart          =   null;
	    ParamPart				paramPart         =   null;
	    FileInputStream		fileRead          =   null;
      String				fileName          =   null;
      long					size              = 0;
	    BufferedReader		br                =   null;
      String				data              =   null;      
      String				errorMode         =   null;
      int					delimIndex        = 0;
      int					beginIndex        = 0;
      String				tempString        =   null;
      String				subString         =   null;
      
      String[]				strToken          =   null;
      String				shipmentMode      =   null;
      String				currencyId        =   null;
      String				weightClass       =   null;
      String				weightBreak       =   null;
      String				rateType          =   null;
      String				uom               =   null;
      String				consoleType       =   null;
      StringBuffer			remarks           =   null;
      RateDOB				buyRateDOB        =   null;
      int					count             =   0;
      int					lineNo            =   0;
      ArrayList				wtBreakList       =   null;
	//Added by Mohan for issue no.219973 on 01122010
      ArrayList				wtBreakDescList   	= null;
      ArrayList				wtBreakCurrencyList	= null;
      ArrayList				wtBreakCurrencyList1	= null; // Added by Gowtham for BuyRateUpload Issue.
      ArrayList				wtBreakTypesList  	= null;
      String				surChargeCurr	  	= null;
      ArrayList				rateTypeList      	= null;
      //ArrayList				wtBreakFlat   	=  null;
      HashMap				tempWtBreakMap  	= null;
      TreeSet				tempWtBreakSet		= null;
      int					tempWtBreakFlatSize = 0;
      int []				currLoc				= null;
      //End by Mohan for issue no.219973 on 01122010
      ArrayList				chargeRateList    =   null;
      String				origin            =   null;
      String				notes            =   null;//@@Added by kameswari for Surcharge Enhancements
      String				externalNotes    =   null; //Modified by Mohan for Issue No.219976 on 28-10-2010
      String				destination       =   null;
      String				carrierId         =   null;
      String				serviceLevel      =   null;
      String				frequency         =   null;
      String				transitTime       =   null;
      String				effFromDate       =   null;
      String				validUpto         =   null;    
	    String        densityRatio      =   null;//added by rk
	  String		densityRatio1	  = ""; // Added by Gowtham for Buyrate Upload Issue.	
	  String				frCurrency		 = null; // Added by Gowtham for BuyRate Upload Issue.
	  String				frCurrency1	     = "";
      //String[]				strTok          =   null;//Commented by Mohan for issue no.219973 on 01122010
      //String				chargeRate        =   null;//Commented by Mohan for issue no.219973 on 01122010
      ArrayList				slabFlatList    =   null;
      ArrayList				flatRatesList   =   null;
      FlatRatesDOB			flatRatesDOB  =   null;
      ESupplyDateUtility	fomater			=   null;
      java.sql.Timestamp				validupTo         =   null;
      java.sql.Timestamp				effectiveFrom     =   null;
	    int					parseInteger	  =   0;
      java.util.TreeSet     set_sortedHdr = null;
      boolean     remarksExist        = false;
      Timestamp   currentDateTimestamp = null;
      HashMap     map           = null;
      String st_dup             = null;
      String[] frequencyArry    = null;
      int frequencyArryLength   = 0;
      int arrayIndex=0;      
      String strUnique  = null;
      String remarksList = null;
      double parseDouble = 0.0;
       ArrayList breakList = new ArrayList();
        int columnsCount    =  0;
        int totalLength     =  0;
        int initialLength   =  0;
        int tempSize       = 0; 
     try
      {
          map               =   new HashMap();
          set_sortedHdr     =   new java.util.TreeSet();
          remarks           =   new StringBuffer("");
          wtBreakList       =   new ArrayList();
		  //Added by Mohan for issue no.219973 on 01122010
          wtBreakDescList   =   new ArrayList();
          wtBreakCurrencyList=  new ArrayList();
          wtBreakCurrencyList1 =  new ArrayList(); // Added by Gowtham for BuyRateUpload Issue.	
          wtBreakTypesList  =   new ArrayList();
          rateTypeList      =   new ArrayList(); 
          //wtBreakFlat       =   new ArrayList();
         //End by Mohan for issue no.219973 on 01122010
          fomater			      =   new ESupplyDateUtility();
          mp                =   new MultipartParser(request, 10*1024*1024); // 10MB

         
          currentDateTimestamp = new Timestamp((new Date()).getTime());
          currentDateTimestamp = fomater.getTimestampWithTimeAndSeconds(loginbean.getUserPreferences().getDateFormat(),loginbean.getCurrentDateString(),"00:00:00");
          
         // System.out.println("currentDateTimestamp::"+currentDateTimestamp);
          /*currentDateTimestamp.setHours(0);
          currentDateTimestamp.setMinutes(0);
          currentDateTimestamp.setSeconds(0);
          currentDateTimestamp.setNanos(0);*/
          
          while ((part = mp.readNextPart()) != null)
          {
          
            name = part.getName();
         
            if (name != null)
                    name = name.trim();
            if (part.isParam())
            {   				  
                paramPart         =        (ParamPart) part;
                value             =        paramPart.getStringValue();
                
            
                if(name.equals("errorMode")){
                  errorMode = value;
                }
                if(name.equals("Operation")){
                  operation = value;
                }
    
            }
           else if (part.isFile())
           {
              filePart         =         (FilePart) part;
              fileName         =         filePart.getFileName();
             
              if (fileName != null){                             
               
				        fileName = fileName.trim();
                size = filePart.writeTo(new File("./"));

              }else{ 

                // the field did not contain a file
                throw new Exception("Please specify a file");

              }
          }//else if
        }//while     
        
        
           fileRead            =        new FileInputStream(fileName);	 
        br                  =        new BufferedReader(new InputStreamReader(fileRead));

									 br.readLine();// empty Line
      
        data                =        br.readLine();
         if( data!=null &&  !data.equals("") )
        {
          data           +=        ",";
          tempString     =         "";
          beginIndex     =         0;
       
          //for(int noOfCols=0;noOfCols<8; noOfCols++)
          for(int noOfCols=0;noOfCols<6;noOfCols++) // Added by Gowtham.
            {
                delimIndex  =  data.indexOf(",");
           
               if(delimIndex==0){
                       if(noOfCols==0 ){
                            tempString=tempString+" ,";
                          }
                          if(noOfCols==1){
                              tempString=tempString+" ,";
                          }
                          if(noOfCols==2){
                            tempString=tempString+" ,";
                          }
                          if(noOfCols==3 ){
                            tempString=tempString + " ,";
                          }
                          if(noOfCols==4 ){
                            tempString=tempString + " ,";
                          }
                          if(noOfCols==5 ){
                            tempString=tempString + " ,";
                          }
                          if(noOfCols==6 ){
                            tempString=tempString + " ,";
                          }	
                          /*if(noOfCols==7 ){
                            tempString=tempString + " ,";
                          }
						             if(noOfCols==8 ){
                            tempString=tempString + " ,";
                          }*/
                          
                }
                 else {				

                subString=data.substring(beginIndex,delimIndex);
               
                tempString=tempString + subString+",";	
            
                }
             
                data             =     data.substring(delimIndex+1);
                beginIndex       =     0;
          }
            
            data             =     tempString.substring(0,tempString.length());
            strToken           =     data.split(",");
         
             if(strToken!=null && strToken.length>0)
            {
                 
                shipmentMode 	= strToken[0];
                if(shipmentMode!=null && !"".equals(shipmentMode.trim()))
                {
                  shipmentMode = shipmentMode.toUpperCase();									

                    if(shipmentMode.equalsIgnoreCase("AIR"))
                       shipmentMode = "1";
                    else if(shipmentMode.equalsIgnoreCase("SEA"))
                      shipmentMode = "2";
                    else if(shipmentMode.equalsIgnoreCase("TRUCK"))
                      shipmentMode = "4";
                    else
                      remarks.append("Shipment Mode  Must Be  Air,Sea,or Truck . \n");

    
                }else
                    remarks.append("Mandatory fields are not provided (Shipment  Mode). \n");
           
               /* currencyId 	= strToken[1];
                if(currencyId!=null && !currencyId.trim().equals("")){
                  currencyId = currencyId.toUpperCase();	
                  if(currencyId.length() > 3)
                    remarks.append("Max Length Must Be 3 (Currency Id). \n");
                }else
                  remarks.append("Mandatory fields are not provided (Location Id). \n"); */ // CurrencyId Commented by Gowtham

                //weightBreak 	= strToken[2];
                weightBreak 	= strToken[1];
                if(weightBreak!=null && !"".equals(weightBreak.trim())){

                            weightBreak = weightBreak.toUpperCase().trim();
                if(!("FLAT".equalsIgnoreCase(weightBreak)||"SLAB".equalsIgnoreCase(weightBreak)||"LIST".equalsIgnoreCase(weightBreak)))
                    { 
                      remarks.append("Weight Break should be Either FLAT,SLAB or List\n");
                    }
               /* if(weightBreak.length() > 8)
                    { 
                      remarks.append("Max Length Must Be  8 (Weight Break ). \n");
                    }*/
                }else
                    { 
                      remarks.append("Mandatory fields are not provided (Weight Break ). \n");
                    }
                           //weightClass 	= strToken[3];
                weightClass 	= strToken[2];

                if(weightClass!=null && !"".equals(weightClass.trim())){

                  weightClass = weightClass.toUpperCase().trim();	
                  /*if(weightClass.length() > 10)
                  { 
                      remarks.append("Max Length Must Be  10 (Weight Class ). \n");
                  }else
                  {*/
                    if("General".equalsIgnoreCase(weightClass))
                      { weightClass  = "G";}
                    else if("Weight Scale".equalsIgnoreCase(weightClass))
                      { weightClass  = "W";}
                    else
                      {
						remarks.append("Weight Class Should Be General or Weight Scale. \n");
					 // }
				  }
                }else
                    { 
						remarks.append("Mandatory fields are not provided (Weight Class ). \n");
					}
                   //rateType 	= strToken[4];
                rateType 	= strToken[3];
                
                if(rateType!=null && !"".equals(rateType.trim()))
                {
                  rateType = rateType.toUpperCase().trim();	
                 /* if(rateType.length() > 8)
                   {
                    remarks.append("Max Length Must Be  8 (Rate Type ). \n");
                   }
                   else
                   {*/
                      if("FLAT".equalsIgnoreCase(weightBreak)){
                        if(!"FLAT".equalsIgnoreCase(rateType))
                           {  remarks.append("RateType Should be FLAT. \n");}
                      }
                      else if("SLAB".equalsIgnoreCase(weightBreak)){								       
                        if(!("FLAT".equalsIgnoreCase(rateType)||"SLAB".equalsIgnoreCase(rateType)||"BOTH".equalsIgnoreCase(rateType)))
                           {  remarks.append("RateType Should be Either FLAT,SLAB or BOTH. \n");}
                      }
                      else if("LIST".equalsIgnoreCase(weightBreak) && "1".equalsIgnoreCase(shipmentMode)){
                        if(!("PIVOT".equalsIgnoreCase(rateType)))
                           {  remarks.append("RateType Should be PIVOT. \n");}
                           
                      }
                      else if("LIST".equalsIgnoreCase(weightBreak) && ("2".equalsIgnoreCase(shipmentMode) || "4".equalsIgnoreCase(shipmentMode) ))
                      {
                        if(!("Flat".equalsIgnoreCase(rateType)))
                          { remarks.append("RateType Should be Flat. \n");}
                      }
                   //}
                }else
                  { remarks.append("Mandatory fields are not provided (Rate Type ). \n");}
    
                 //uom 	= strToken[5];
                uom 	= strToken[4];
                if(uom!=null && !uom.trim().equals("")){
                  uom = uom.toUpperCase();	
                 /* if(uom.length() > 8)
                   { remarks.append("Max Length Must Be  8 (UOM ). \n");}*/
                  if("2".equalsIgnoreCase(shipmentMode)) 
                  {
                    if(!"CBM".equalsIgnoreCase(uom) && !"CFT".equalsIgnoreCase(uom))
                    {
                      remarks.append("UOM Should be either CBM or CFT. \n");
                    }
                  }
                  else if("1".equalsIgnoreCase(shipmentMode))
                  {
                    if(!"KG".equalsIgnoreCase(uom) && !"LB".equalsIgnoreCase(uom))
                    {
                      remarks.append("UOM Should be either KG,LB. \n");
                    }                    
                  }
                  else if("4".equalsIgnoreCase(shipmentMode))
                  {
                    if(!"KG".equalsIgnoreCase(uom) && !"LB".equalsIgnoreCase(uom) && !"CBM".equalsIgnoreCase(uom) && !"CFT".equalsIgnoreCase(uom))
                    {
                      remarks.append("UOM Should be either KG,LB,CBM,CFT. \n");
                    }
                  }
                }else
                  { remarks.append("Mandatory fields are not provided (UOM ). \n");}
                

    
               // consoleType 	= strToken[6];
                consoleType 	= strToken[5];
              
                if(consoleType!=null && !consoleType.trim().equals("")){						    
                  consoleType = consoleType.toUpperCase();	
                  if("1".equalsIgnoreCase(shipmentMode) ){
                  /*  if(consoleType.length()>0)
                      { */
                      remarks.append("The ConsoleType Should be Empty. \n");
                      //}
                  }
                  else if("2".equalsIgnoreCase(shipmentMode) ){
                    if(!("LCL".equalsIgnoreCase(consoleType)||"FCL".equalsIgnoreCase(consoleType)))
                        { remarks.append("The ConsoleType Should be Either LCL or FCL. \n");}

                    else if("List".equalsIgnoreCase(weightBreak) && !"FCL".equalsIgnoreCase(consoleType))								  
                        { remarks.append("The ConsoleType Should be FCL. \n");}

                    else if(!"LCL".equalsIgnoreCase(consoleType) &&  !"List".equalsIgnoreCase(weightBreak))
                        { remarks.append("The ConsoleType Should be LCL. \n");}
                  }
                    
                  if( "4".equalsIgnoreCase(shipmentMode)){
                    if(!("LTL".equalsIgnoreCase(consoleType)||"FTL".equalsIgnoreCase(consoleType)))
                        { remarks.append("The ConsoleType Should be Either LTL or FTL. \n");}
                    else if("List".equalsIgnoreCase(weightBreak) && !"FTL".equalsIgnoreCase(consoleType))								  
                        { remarks.append("The ConsoleType Should be FTL. \n");}
                    else if(!"LTL".equalsIgnoreCase(consoleType) &&  !"List".equalsIgnoreCase(weightBreak))
                        { remarks.append("The ConsoleType Should be LTL. \n");}
                  }
                  /*if(consoleType.length() > 8)
                      { remarks.append("Max Length Must Be  8 (ConsoleType ). \n");}*/
                }else{
                  if("2".equalsIgnoreCase(shipmentMode) || "4".equalsIgnoreCase(shipmentMode))
                    { remarks.append("Mandatory fields are not provided (ConsoleType ). \n");}
                }
                //added by rk
                //Commented by Gowtham DensityRatio.
                /*  densityRatio 	= strToken[7];
                
                if(densityRatio!=null && !densityRatio.trim().equals("")){						    
                  densityRatio = densityRatio.toUpperCase();	
                    if(densityRatio.length() > 25)
                    { 
                      remarks.append("Max Length Must Be (densityRatio ). < 25\n");
                    }
                    else
                    {
                     if(densityRatio.indexOf(":")<=0)
                      {
                        remarks.append("Density ratio is Invalid \n");  
                      }else
                      {
                        try
                        {
                          /*Integer.parseInt(densityRatio.substring(0,densityRatio.indexOf(":")));
                          Integer.parseInt(densityRatio.substring(densityRatio.indexOf(":")+1,densityRatio.length()));
                          Double.parseDouble(densityRatio);
                          
                        }catch(NumberFormatException e)
                        {
                          remarks.append("Density ratio is Invalid \n");
                        }
                      //}
                    }
                  }
                  else
                  {
                    remarks.append("Density ratio is shouldnot be empty \n");
                    densityRatio = "";
                  }   */  // Commented by Gowtham.
                
                
                buyRateDOB      =   new RateDOB();
                buyRateDOB.setShipmentMode(shipmentMode);
               // buyRateDOB.setCurrency(currencyId); // Commented by Gowtham for Buyrate Upload Issue.					  
                buyRateDOB.setWeightBreak(weightBreak);
                buyRateDOB.setRateType(rateType);
                buyRateDOB.setWeightClass(weightClass);
                buyRateDOB.setTerminalId(loginbean.getTerminalId());					  
                buyRateDOB.setUom(uom);
               if(consoleType.trim().length()>0)
                buyRateDOB.setConsoleType(consoleType);
                else
                buyRateDOB.setConsoleType(null);
                buyRateDOB.setUser(loginbean.getUserId());
              //  buyRateDOB.setRateDtls(new ArrayList());
                buyRateDOB.setCreatedTime(new java.sql.Timestamp((new java.util.Date()).getTime()));
                buyRateDOB.setAccessLevel(loginbean.getAccessType());
                  
                //buyRateDOB.setRemarks(remarks.toString());
            }
     
        
         br.readLine();                 
        //if (data==null)
            //	break;				
            //remarks = "";
        }
         for(int j=0;j<8;j++)
            {
              breakList.add("Surcharge");
            }
        data          =        br.readLine();
              strToken      =        data.split(",");
  			int totalColumnsCount         =        strToken.length;
    
        if(remarks!=null && remarks.length()>0)
        {
          remarksExist  = true;
        }
        flatRatesList = new ArrayList();
      
      	while(!data.equals("") && data!=null)
        {   
            data           +=        ",";
            tempString     =         "";
            beginIndex     =         0;
         strToken         =     data.split(",");
        int strTokenLen		=	strToken.length	;
            for(int noOfCols=0;noOfCols<strTokenLen; noOfCols++) 
            {
                delimIndex  =  data.indexOf(",");
                if(delimIndex==0){					
                    tempString=tempString + " ,";
                }
                else {				
                    subString=data.substring(beginIndex,delimIndex);
                  
                 
                    tempString=tempString + subString+",";				
                } 
                data             =     data.substring(delimIndex+1);
                beginIndex       =     0;
            } 
            data             =     tempString.substring(0,tempString.length());
            strToken         =     data.split(",");
            int temp = 0;
            String tempRemarks ="";
         //Modified by Mohan for Issue No.219976 on 28-10-2010
	      if(count==0 && !"External Notes".equalsIgnoreCase(strToken[strToken.length-1]))
	      {
	    	  remarks.append("External Notes Header Not Defined.\n");
	    	  buyRateDOB.setRemarks(remarks.toString());
	    	  remarksExist = true;
	    	  break;
	      }else
	      {
          if(count==0&&"External Notes".equalsIgnoreCase(strToken[strToken.length-1]))
          {
              columnsCount   = totalColumnsCount-2;
              temp  =  strToken.length;
          }
          else if(columnsCount==totalColumnsCount-2)
          {
             columnsCount   = totalColumnsCount-2;
          }
          else
          {
              columnsCount   = totalColumnsCount;
          }
               if(count==0)
        {
         totalLength   = columnsCount;
        }     
   
            if(strToken.length>1)
            {   
              
          
              if(count==0)
              {
                  //Added by Mohan for issue no.219973 on 01122010
            	  int currIndex = 0;//Added by Mohan
                  int currLength = 0 ;
                  //for(int m=8;m<columnsCount;m++)//For Identifying the no of surcharges in the file
                	  for(int m=10;m<columnsCount;m++) // Added by Gowtham.
                  {
                  	if("CURRENCY".equals(strToken[m]))
                  	{ 
                  		currLength=currLength+1;
                  	}
                  }
                  currLoc =  new int[currLength+1];
                 
                  //for(int m=8;m<columnsCount;m++)//For Surchage parts
                  for(int m=10;m<columnsCount;m++) // Added by Gowtham.
                  {
                  	if("CURRENCY".equals(strToken[m]))
                  	{ 
                  	 	currLoc[currIndex]=m;
                  	 	currIndex = currIndex+1;
                  	}
                  }
                 currLoc[currIndex] = columnsCount;
                 // tempSize = currLoc[0]-8;//For No of Freight Wt Break Ups
                 tempSize = currLoc[0]-10; // Added by Gowtham.
             	 for(int c=0;c<tempSize;c++)
             	 {
                	 wtBreakDescList.add("A FREIGHT RATE");
                	// wtBreakCurrencyList.add(currencyId); Commented by Gowtham for Buyrate Upload Issue.
                	 if(c!=tempSize-1)
                		 wtBreakTypesList.add(weightBreak+"~"+c);
                	 else
                		 wtBreakTypesList.add(weightBreak+"~-1");
                	 rateTypeList.add(rateType);
             	 }             	 
             	 tempWtBreakMap = getFlatSurCharges(shipmentMode);
        		/* Iterator itr = tempWtBreakMap.values().iterator();
        		while(itr.hasNext())
        		 {
        			System.out.println("From DAO Surcharge Id--->"+(String)itr.next());	
        		 }*/
                 if("FLAT".equalsIgnoreCase(weightBreak) || "SLAB".equalsIgnoreCase(weightBreak))
                { 
                 	tempRemarks =  validateFrightRates(strToken,weightBreak,shipmentMode,wtBreakList, 10, currLoc[0]);
                	remarks.append(tempRemarks);
                }else
                {
                	tempWtBreakSet = getListSurCharges(shipmentMode);
                	tempRemarks =  validateListFrgtcharges(tempWtBreakSet,strToken,shipmentMode,wtBreakList, 10, currLoc[0]);
        			remarks.append(tempRemarks);
                }
                int listSurChargeSize = 0; 
                for (int j=0;j<currLoc.length-1;j++)
                {
                	listSurChargeSize= ((currLoc[j+1]) - (currLoc[j])-3) ;//In case of list,surcharge wtbk = frt wtbrk
                	if(listSurChargeSize>tempSize && "LIST".equalsIgnoreCase(weightBreak))
                		remarks.append("List Surcharge Weight Breaks Should be equal to Freight Weight Breaks.");
                	else
                	   //	tempRemarks = validateSurchargeHdrs(wtBreakList,wtBreakDescList,rateTypeList,tempWtBreakMap,shipmentMode,strToken,currLoc[j],currLoc[j+1],wtBreakTypesList,tempWtBreakSet);
                	   	tempRemarks = validateSurchargeHdrs(wtBreakList,wtBreakDescList,rateTypeList,tempWtBreakMap,shipmentMode,strToken,currLoc[j],currLoc[j+1],wtBreakTypesList,tempWtBreakSet);
                	
                	remarks.append(tempRemarks);
                }
			 //End by Mohan for issue no.219973 on 01122010
			 //Commented by Mohan for issue no.219973 on 01122010
                /*for(int k=8;k<columnsCount;k++)//@@Modified by kameswari for Surcharge Enhancements
                {
                            breakList.add(strToken[k]);
                    if(weightBreak!=null && !"".equals(weightBreak))
                    {
                    	 if(k==8)
                         {
                             if("FLAT".equalsIgnoreCase(weightBreak) || "SLAB".equalsIgnoreCase(weightBreak)|| "LIST".equalsIgnoreCase(weightBreak))
                             {                          
                                 if(!"BASIC".equalsIgnoreCase(strToken[k]))
                                 {
                                   remarks.append("Invalid weight break header(BASIC)\n");
                                 }
                             }
                         }
                    	 else if(k==9)
                        {
                            if("FLAT".equalsIgnoreCase(weightBreak) || "SLAB".equalsIgnoreCase(weightBreak))
                            {                          
                                if(!"MIN".equalsIgnoreCase(strToken[k]))
                                {
                                  remarks.append("Invalid weight break header(MIN)\n");
                                }
                            }else if("LIST".equalsIgnoreCase(weightBreak) && "1".equalsIgnoreCase(shipmentMode))
                            {
                                if(!"OVER-PIVOT RATE".equalsIgnoreCase(strToken[k]))
                                {
                                  remarks.append("Invalid weight break header(OVER-PIVOT RATE)\n");
                                }else
                                {
                                  strToken[k] = "overPivot";
                                }
                            }
                            
                        }else if(k==10)
                        {
                          if("FLAT".equalsIgnoreCase(weightBreak))
                          {
                            if(!"FLAT".equalsIgnoreCase(strToken[k]))
                            {
                              remarks.append("Invalid weight break header(FLAT)\n");
                              
                            }
                          }else if("SLAB".equalsIgnoreCase(weightBreak))
                            {
                              try
                              {
                                parseDouble = Double.parseDouble(strToken[k]);
                                if(parseDouble>0)
                                {
                                  remarks.append("Invalid Weight break values(MIN,-X,X)\n");
                                }
                              }catch(NumberFormatException e)
                              {
                                e.printStackTrace();
                                //Logger.error(FILE_NAME,"at 853");
                                remarks.append("Weight break value Not a number\n");
                                break;
                              } 
                            }
                        }else if(k==11)
                        {
                          if("SLAB".equalsIgnoreCase(weightBreak))
                          {
                               try
                              {
                                if(Double.parseDouble(strToken[k]) != Math.abs(parseDouble) )
                                {
                                  remarks.append("Invalid Weight breaks values(MIN,-X,X)\n");
                                }
                              }catch(NumberFormatException e)
                              {
                                e.printStackTrace();
                                //Logger.error(FILE_NAME,"at 871");
                                remarks.append("Weight break value Not a number\n");
                                break;
                              }                            
                          }
                          
                          if("CURRENCY".equals(strToken[k]))//Added by Mohan
                          {
                        	  currLoc[currIndex]= k;//Added by Mohan
                        	  currIndex=currIndex+1;
                          }
                          
                        }else
                        {
                        
                          
                           /* if(strToken[k].startsWith("FS")||strToken[k].startsWith("SS")||strToken[k].startsWith("CAF")
                            ||strToken[k].startsWith("BAF")||strToken[k].startsWith("PSS")||strToken[k].startsWith("CSF") ||strToken[k].startsWith("SURCHARGE"))
                                 {
                                    if(!(strToken[k].equalsIgnoreCase("FSKG")||strToken[k].equalsIgnoreCase("FSBASIC")
                                    ||strToken[k].equalsIgnoreCase("FSMIN")||strToken[k].equalsIgnoreCase("SSKG")||strToken[k].equalsIgnoreCase("SSBASIC")
                                    ||strToken[k].equalsIgnoreCase("SSMIN")||strToken[k].equalsIgnoreCase("CAF%")||strToken[k].equalsIgnoreCase("CAFMIN")
                                    ||strToken[k].equalsIgnoreCase("BAFMIN")||strToken[k].equalsIgnoreCase("BAFM3")||strToken[k].equalsIgnoreCase("PSSMIN")||strToken[k].equalsIgnoreCase("PSSM3")
                                    ||strToken[k].equalsIgnoreCase("CSF") || "SURCHARGE".equalsIgnoreCase(strToken[k])))
                                    {
                                         remarks.append("Invalid Weight break :"+strToken[k]);
                                    }
                                 }*/
                                /* else if(strToken[k].startsWith("SS"))
                                 {
                                   if(!(strToken[k].equalsIgnoreCase("SSKG")||strToken[k].equalsIgnoreCase("SSBASIC")
                                    ||strToken[k].equalsIgnoreCase("SSMIN")))
                                    {
                                         remarks.append("Invalid Weight break :"+strToken[k]);
                                    }
                                 }
                                 else if(strToken[k].startsWith("CAF"))
                                 {
                                   if(!(strToken[k].equalsIgnoreCase("CAF%")||strToken[k].equalsIgnoreCase("CAFMIN")
                                    ))
                                    {
                                         remarks.append("Invalid Weight break :"+strToken[k]);
                                    }
                                 }
                                 else if(strToken[k].startsWith("BAF"))
                                 {
                                   if(!(strToken[k].equalsIgnoreCase("BAFMIN")||strToken[k].equalsIgnoreCase("BAFM3")
                                    ))
                                    {
                                         remarks.append("Invalid Weight break :"+strToken[k]);
                                    }
                                 }
                                 else if(strToken[k].startsWith("PSS"))
                                 {
                                   if(!(strToken[k].equalsIgnoreCase("PSSMIN")||strToken[k].equalsIgnoreCase("PSSM3")
                                    ))
                                    {
                                         remarks.append("Invalid Weight break :"+strToken[k]);
                                    }
                                 }
                                 else if(strToken[k].startsWith("CSF"))
                                 {
                                   if(!(strToken[k].equalsIgnoreCase("CSF")))
                                    {
                                         remarks.append("Invalid Weight break :"+strToken[k]);
                                    }
                                 }* /
                            
                                // else
                                // {
                        	  		if("CURRENCY".equals(strToken[k]))//Added by Mohan
                        	  		{
                        	  			currLoc[currIndex]= k;//Added by Mohan
                        	  			currIndex = currIndex+1;
                        	  		}
                        			
	                        	   if("Flat".equalsIgnoreCase(weightBreak))
	                               {
	                        		   if(!"CURRENCY".equals(strToken[k]))
	                        			   wtBreakFlat.add(strToken[k].toUpperCase());
	                        		   
	                               }else if("Slab".equalsIgnoreCase(weightBreak))
                                      {
                                        try
                                        {
                                                                               
                                        if(Double.parseDouble(strToken[k]) < Double.parseDouble(strToken[k-1]) )
                                       {
                                        remarks.append("Invalid Weight breaks values(MIN,-X,X,Y,Z(Y<Z))\n");
                                        }
                                     }
                                    catch(NumberFormatException e)
                                    {
                                      e.printStackTrace();
                                      //Logger.error(FILE_NAME,"at 890");
                                      remarks.append("Weight break value Not a number\n");
                                      break;
                                    }
                                 }
                             // }
                            /*  else  if("Flat".equalsIgnoreCase(weightBreak))
                              {
                               if(strToken[k].startsWith("FS"))
                                 {
                                    if(!(strToken[k].equalsIgnoreCase("FSKG")||strToken[k].equalsIgnoreCase("FSBASIC")
                                    ||strToken[k].equalsIgnoreCase("FSMIN")))
                                    {
                                         remarks.append("Invalid Weight break :"+strToken[k]);
                                    }
                                 }
                                 else if(strToken[k].startsWith("SS"))
                                 {
                                   if(!(strToken[k].equalsIgnoreCase("SSKG")||strToken[k].equalsIgnoreCase("SSBASIC")
                                    ||strToken[k].equalsIgnoreCase("SSMIN")))
                                    {
                                         remarks.append("Invalid Weight break :"+strToken[k]);
                                    }
                                 }
                                 else if(strToken[k].startsWith("CAF"))
                                 {
                                   if(!(strToken[k].equalsIgnoreCase("CAF%")||strToken[k].equalsIgnoreCase("CAFMIN")
                                    ))
                                    {
                                         remarks.append("Invalid Weight break :"+strToken[k]);
                                    }
                                 }
                                 else if(strToken[k].startsWith("BAF"))
                                 {
                                   if(!(strToken[k].equalsIgnoreCase("BAFMIN")||strToken[k].equalsIgnoreCase("BAFM3")
                                    ))
                                    {
                                         remarks.append("Invalid Weight break :"+strToken[k]);
                                    }
                                 }
                                 else if(strToken[k].startsWith("PSS"))
                                 {
                                   if(!(strToken[k].equalsIgnoreCase("PSSMIN")||strToken[k].equalsIgnoreCase("PSSM3")
                                    ))
                                    {
                                         remarks.append("Invalid Weight break :"+strToken[k]);
                                    }
                                 }
                                 else if(strToken[k].startsWith("CSF"))
                                 {
                                   if(!(strToken[k].equalsIgnoreCase("CSF")))
                                    {
                                         remarks.append("Invalid Weight break :"+strToken[k]);
                                    }
                                 }
                              }* /
                             }
                        }
                   wtBreakList.add(strToken[k].toUpperCase());
                    
                }*/
                buyRateDOB.setWtBreakList(wtBreakList);
                 //Added by Mohan for issue no.219973 on 01122010
                buyRateDOB.setWtBreakDescList(wtBreakDescList);//Added by Mohan
                buyRateDOB.setWtBreakTypesList(wtBreakTypesList);//Added by Mohan
                buyRateDOB.setRateTypeList(rateTypeList);//Added by Mohan
               // buyRateDOB.setSurChargeCurrList(wtBreakCurrencyList);//Added by Mohan
               //Commented by Mohan for issue no.219973 on 01122010 
              /*  if("Flat".equalsIgnoreCase(weightBreak) && wtBreakFlat!=null && wtBreakFlat.size()>0)
                {
                	tempWtBreakSet = getFlatSurCharges(shipmentMode);
                   	//if(tempWtBreakSet!=null)
                    	//tempWtBreakFlatSize = tempWtBreakSet.size();
                   	
                   	Iterator itr = tempWtBreakSet.iterator();
                   	while(itr.hasNext())
                   	{
                   		System.out.println("From DAO Surcharge Id--->"+(String)itr.next());	
                   	}
                   	tempWtBreakFlatSize = wtBreakFlat.size();
                    for(int i =0; i<tempWtBreakFlatSize;i++)
                    {
                    	System.out.println("Surcharge Id--->"+((String)wtBreakFlat.get(i)).substring(0,5));
                    	if(!tempWtBreakSet.contains(((String)wtBreakFlat.get(i)).substring(0,5)))
                    		remarks.append("Surcharge Id "+(String)wtBreakFlat.get(i)+" is Invalid.\n");;
                    }
                }*/
              /*  
                if("List".equalsIgnoreCase(weightBreak) && wtBreakList!=null && wtBreakList.size()>0)
                  {
                      
                      
                      remarksList =  validateWBreaks(wtBreakList,shipmentMode);
                  }*/
                
                if(remarksList!=null && remarksList.trim().length()>0)
                  remarks.append(remarksList+"\n");
                
                buyRateDOB.setRemarks(remarks.toString());
              }			
              else
              {
                 remarks.delete(0,remarks.length());
                 origin	= strToken[0];
                // notes = "";
               
              /* if(strToken.length>columnsCount)
                 {
                   remarks.append("Comma should not be used in between notes");
                 }
                 else
                 {*/
                //Modified by Mohan for Issue No.219976 on 28-10-2010
                  if(columnsCount==strToken.length-2)
                  {
                	  externalNotes  = strToken[strToken.length-1];
                	  if(externalNotes.length()>1000)
                	  {
                		  remarks.append("Max Length Must Be 1000 (External Notes).\n");
                	  }
                	  else if (externalNotes.trim().length()==0)
                	  {
                		  remarks.append("External Notes should not be blank.Please enter NA if not applicable.\n");
                	  }
                	  if("NA".equalsIgnoreCase(externalNotes))
                	  {
                		  externalNotes = "";
                	  }
                  }     
                  else if(temp>strToken.length)
                  {
                  
                  }

                  if(columnsCount==strToken.length-2)
                  {
                	  notes  = strToken[strToken.length-2];
                	  if(notes.length()>1000)
                	  {
                		  remarks.append("Max Length Must Be 1000 (Internal Notes). \n");
                	  }
                	  else if (notes.trim().length()==0)
                	  {
                		  remarks.append("Internal Notes should not be blank.Please enter NA if not applicable.\n");
                	  }
                	  if("NA".equalsIgnoreCase(notes))
                	  {

                		  notes = "";
                	  }
                  }     
                 
                  if(origin!=null && !origin.trim().equals(""))
                  {
                    origin = origin.toUpperCase();	
                    if(shipmentMode.equalsIgnoreCase("AIR") || shipmentMode.equalsIgnoreCase("TRUCK"))
                    {
                      if(origin.length() > 3)
                        remarks.append("Max Length Must Be 3 (Origin). \n");
                    }
                    else
                    {
                      if(origin.length() > 5)
                        remarks.append("Max Length Must Be 5 (Origin). \n");
                    }
                  }
                  else
                  { 
                    remarks.append("Mandatory fields are not provided (Origin). \n");
                  }

              
                  destination 	= strToken[1];
                  if(destination!=null && !destination.trim().equals(""))
                  {
                    destination = destination.toUpperCase();	
                    if(shipmentMode.equalsIgnoreCase("AIR") || shipmentMode.equalsIgnoreCase("TRUCK"))
                    {
                      if(destination.length() > 3)
                        remarks.append("Max Length Must Be 3 (Destination). \n");
                    }
                    else
                    {
                      if(destination.length() > 5)
                        remarks.append("Max Length Must Be 5 (Destination). \n");
                    }
                  }
                  else
                  { 
                    remarks.append("Mandatory fields are not provided (Destination ). \n");
                  }
                  
                    carrierId 	= strToken[2];
                    
                    if(carrierId!=null && !carrierId.trim().equals("")){
                      carrierId = carrierId.toUpperCase();	
                      if(carrierId.length() > 5)
                        { remarks.append("Max Length Must Be 5 (Carrier Id ). \n");}
                    }else
                      { remarks.append("Mandatory fields are not provided (Carrier Id ). \n");}

                  serviceLevel 	= strToken[3];
                  if(serviceLevel!=null && !serviceLevel.trim().equals("")){
                    serviceLevel = serviceLevel.toUpperCase();	
                    if(serviceLevel.length() > 8)
                      { remarks.append("Max Length Must Be 8 (Service Level ). \n");}
                  }else
                      {  remarks.append("Mandatory fields are not provided (Service Level ). \n");}
    
                  frequency 	= strToken[4];
                   
                  if(frequency!=null && !frequency.trim().equals(""))
                  {
                    if(frequency.length() > 15)
                    {
                      remarks.append("Max Length Must Be < = 15 (Frequency ). \n");
                    }else
                    {
                        if("1".equalsIgnoreCase(shipmentMode) || "4".equalsIgnoreCase(shipmentMode))
                        {
                        if(frequency!=null && !frequency.trim().equals(""))
                           frequency     = frequency.replace('&',',');
                           
                           try
                           {
                           
                             frequency = getSortedNums(frequency);
                             
                           }catch(NumberFormatException e)
                           {
                                parseInteger = 0;
                                remarks.append("Frequency Should be either 1&2&3&4&5&6 or 7. \n");
                                                            
                           }catch(Exception e)
                           {
                              parseInteger = 0;
                              remarks.append("Frequency Should be either 1&2&3&4&5&6 or 7. \n");
                           }
                           
                           /*strTok  =  frequency.split(",");
                           if(strTok.length>0)
                           {
                             for(int i=0;i<strTok.length;i++)
                              {
                                 try{
                                      parseInteger = Integer.parseInt(strTok[i]);
                  
                                   }catch(NumberFormatException e)
                                    {
                                    parseInteger = 0;
                                    remarks.append("Frequency Should be either 1,2,3,4,5,6 or 7. \n");
                                    break;
                                    }

                                    if(parseInteger<=0 || parseInteger>7 )
                                    {
                                      remarks.append("Frequency Should be either 1,2,3,4,5,6 or 7. \n");
                                      break;
                                    }
                                    
                               }
                             }*/
                          }                                   
                      else if("2".equalsIgnoreCase(shipmentMode)) 
                      {
                         frequency  = frequency.toUpperCase();
                         if(!"Weekly".equalsIgnoreCase(frequency) && !"Fortnightly".equalsIgnoreCase(frequency) && !"Monthly".equalsIgnoreCase(frequency) && !"Every 10 Days".equalsIgnoreCase(frequency))
                         {  
                            remarks.append("Frequency Should be either Weekly, Fortnightly or Monthly. \n");
                         }
                      }
                    }				  
                  }else
                      { remarks.append("Mandatory fields are not provided (Frequency ). \n");}
                   transitTime 	= strToken[5];
                  if(transitTime!=null && !transitTime.trim().equals("")){
                    transitTime = transitTime.toUpperCase();	
                    if(transitTime.length() > 6)
                      { remarks.append("Max Length Must Be 6 (TransitTime ). \n");}
                    else
                      {
                        if("1".equalsIgnoreCase(shipmentMode) || "4".equalsIgnoreCase(shipmentMode))
                        {
                          if(transitTime.length()==4 && transitTime.indexOf(":")<=0)
							{
								try{
										parseInteger = Integer.parseInt(transitTime.substring(0,1));
										if(parseInteger<0 || parseInteger>99)
										{
											throw new NumberFormatException();
										}
										parseInteger = Integer.parseInt(transitTime.substring(2,transitTime.length()));
										if(parseInteger<0 || parseInteger>60)
										{
											throw new NumberFormatException();
										}
								}catch(NumberFormatException e)
								{
									parseInteger = 0;
									remarks.append("The Transit Time format should be HH:MM. \n");

								}
								if(parseInteger>0)
								{	
									transitTime  =  transitTime.substring(0,1)+":"+transitTime.substring(2,transitTime.length());
								}
							}else if(transitTime.length()==5 && transitTime.indexOf(":")>0)
							{
								try{
										parseInteger = Integer.parseInt(transitTime.substring(0,1));
										if(parseInteger<0 || parseInteger>99)
										{  
											throw new NumberFormatException();
										}
										parseInteger = Integer.parseInt(transitTime.substring(3,transitTime.length()));
										if(parseInteger<0 || parseInteger>60)
										{
											throw new NumberFormatException();
										}
								}catch(NumberFormatException e)
								{
									parseInteger = 0;
									remarks.append("The Transit Time format should be HH:MM. \n");

								}									
							}
                             
  /*                        else if(transitTime.length()==4)
                             transitTime  =  transitTime.substring(0,1)+":"+transitTime.substring(2,transitTime.length());
                          else if(transitTime.indexOf(":")<0 && transitTime.length()==5)
                                remarks = remarks + "The Transit Time format should be HH:MM. ";

                            if(transitTime.indexOf(":")<=0)
                            {
                              remarks.append("The Transit Time format should be HH:MM. \n");
                            }*/
                        }else if("2".equalsIgnoreCase(shipmentMode))
                        {
							try{
									parseInteger = Integer.parseInt(transitTime);
							}catch(NumberFormatException e)
							{
								parseInteger	=	0;
								remarks.append("The Transit Time for sea must be in days. \n");
							}
                        }
                      }
                  }else
                    { remarks.append("Mandatory fields are not provided (TransitTime ). \n");}
                   effFromDate 	= strToken[6];
                 
                  if(effFromDate!=null && !effFromDate.trim().equals("")){
                    effFromDate = effFromDate.toUpperCase();
                    if(effFromDate.length() > 10)
                      remarks.append("Max Length Must Be 8 (EffFromDate ). \n");
                  }else
                     remarks.append("Mandatory fields are not provided (EffFromDate ). \n");
    
                  validUpto 	= strToken[7];
                  
                  if(validUpto!=null && !validUpto.trim().equals("")){
                    validUpto = validUpto.toUpperCase();	
                    if(validUpto.length() > 10)
                      remarks.append("Max Length Must Be 8 (ValidUpto ). \n");
                  }
                  
                  frCurrency    = strToken[8]; // Added by Gowtham for BuyRate Upload Issue.
                  densityRatio  = strToken[9];
                  frCurrency1   = frCurrency1+strToken[8]+","; 
                  /*else
                    remarks.append("Mandatory fields are not provided (ValidUpto ). \n");
                  */
                  chargeRateList    =  new ArrayList();
                  slabFlatList      =  new ArrayList(); 
 				  //Added by Mohan for issue no.219973 on 01122010
                  if("FLAT".equalsIgnoreCase(weightBreak) || "SLAB".equalsIgnoreCase(weightBreak))
                	  tempRemarks  = validateFreightCharges(wtBreakList,chargeRateList,slabFlatList,strToken,weightBreak,rateType,10,currLoc[0]); 
                  else//Freight Rates validations
                	  tempRemarks = validateListFreightCharges(wtBreakList,chargeRateList,slabFlatList,strToken,weightBreak,rateType,10,currLoc[0]);  
                  
                  remarks.append(tempRemarks);
                  
                  // Added by Gowtham for Upload Issue.
                  for(int frCurIndex = 0 ;frCurIndex < tempSize; frCurIndex++)
                  {
                	  
                	  wtBreakCurrencyList1.add(strToken[8]);
                  }
                  
                  densityRatio1 = densityRatio1 +strToken[9]+  "," ; 
                  
                  // Added by Gowtham for Upload Issue.
                  for (int j=0;j<currLoc.length-1;j++)
                  {
                	//For Each surcharge block
                  	tempRemarks = validateSurcharges(chargeRateList,wtBreakCurrencyList1,slabFlatList,strToken,currLoc[j],currLoc[j+1]);
                  	remarks.append(tempRemarks);
                  }
                 //Surcharge Currency validation
                  for(int currIndex=0; currIndex<currLoc.length-1;currIndex++)
                  {   
               	   	surChargeCurr = strToken[currLoc[currIndex]];
               	   	if(surChargeCurr==null || "".equals(surChargeCurr.trim()))
               	   		remarks.append("Surcharge Currency should not be blank at "+(currLoc[currIndex]+1)+" Column.\n");
               		   
                  }
                  //Validating Surcharge Weight Break
                  for(int currIndex=0; currIndex<currLoc.length-1;currIndex++)
                  {   
                	 if((currLoc[currIndex]+1)<columnsCount)
                	 {
                		 surChargeCurr = strToken[currLoc[currIndex]+1];
                		 if(surChargeCurr==null || "".equals(surChargeCurr.trim()))
               	   			remarks.append("Surcharge Weight Break should not be blank at "+(currLoc[currIndex]+2)+" Column.\n");
                	 }
                  }
                  //Validating Surcharge Rate Type
                  for(int currIndex=0; currIndex<currLoc.length-1;currIndex++)
                  {   
                	 if((currLoc[currIndex]+2)<columnsCount)
                 	 {
               	   		surChargeCurr = strToken[currLoc[currIndex]+2];
               	   		if(surChargeCurr==null || "".equals(surChargeCurr.trim()))
               	   			remarks.append("Surcharge Rate Type should not be blank at "+(currLoc[currIndex]+3)+" Column.\n");
                 	 }
                  }
                  
                
                  
                  //End by Mohan for issue no.219973 on 01122010
                  //Commented by Mohan for issue no.219973 on 01122010
                  //for(int k=8;k<strToken.length;k++)//@@Modified by kameswari for Surcharge Enhancements
                 /* for(int k=8;k<columnsCount;k++)
                    {
                      chargeRate 	= strToken[k].trim();
                      if(chargeRate!=null && !chargeRate.trim().equals("") && !chargeRate.trim().equalsIgnoreCase("NA"))
                      {
                        chargeRate = chargeRate.toUpperCase();	
                          if(chargeRate.length() > 15)
                              remarks.append("Max Length Must Be  15 (Charge Rate ). \n");
                          if("Both".equalsIgnoreCase(rateType)){
                          
							try{
                  //if(k==8)//@@ Commented by subrahmanyam for the pbn id:186861 ON 22/OCT/09
                  //@@ added by subrahmanyam for the pbn id:186861 ON 22/OCT/09
                  if(k==8 || ((((String)breakList.get(k)).startsWith("FS")||((String)breakList.get(k)).startsWith("SS")
                                 ||((String)breakList.get(k)).startsWith("BAF")||((String)breakList.get(k)).startsWith("CAF")
                                 ||((String)breakList.get(k)).startsWith("CSF")||((String)breakList.get(k)).startsWith("SURCHARGE")
                                 ||((String)breakList.get(k)).startsWith("PSS"))))

									   parseDouble = Double.parseDouble(chargeRate.substring(0,chargeRate.length()));                    
                  else
									   parseDouble = Double.parseDouble(chargeRate.substring(0,chargeRate.length()-1));
		
							}catch(NumberFormatException e)
							  {
									parseDouble	= -1;
									remarks.append("Charge Rate is not a Number. \n");

							  }          
                //if(k==8)//@@ Commented by subrahmanyam for the pbn id:186861 ON 22/OCT/09
                  //@@ added by subrahmanyam for the pbn id:186861 ON 22/OCT/09
                             if(k==8 || ((((String)breakList.get(k)).startsWith("FS")||((String)breakList.get(k)).startsWith("SS")
                                 ||((String)breakList.get(k)).startsWith("BAF")||((String)breakList.get(k)).startsWith("CAF")
                                 ||((String)breakList.get(k)).startsWith("CSF")||((String)breakList.get(k)).startsWith("SURCHARGE")
                                 ||((String)breakList.get(k)).startsWith("PSS"))))

                              chargeRateList.add(chargeRate.substring(0,chargeRate.length()));
                            else
                              chargeRateList.add(chargeRate.substring(0,chargeRate.length()-1));
                              
						  
            	if(parseDouble > 0.0)
                            {
                             
                            
                            //if(k==8)//@@ Commented by subrahmanyam for the pbn id:186861 ON 22/OCT/09
                  //@@ added by subrahmanyam for the pbn id:186861 ON 22/OCT/09
                              if(k==8 || ((((String)breakList.get(k)).startsWith("FS")||((String)breakList.get(k)).startsWith("SS")
                                 ||((String)breakList.get(k)).startsWith("BAF")||((String)breakList.get(k)).startsWith("CAF")
                                 ||((String)breakList.get(k)).startsWith("CSF")||((String)breakList.get(k)).startsWith("SURCHARGE")
                                 ||((String)breakList.get(k)).startsWith("PSS"))))
                                       slabFlatList.add("");
                             
                            else
                            {
                                 if(!(((String)breakList.get(k)).startsWith("FS")||((String)breakList.get(k)).startsWith("SS")
                                 ||((String)breakList.get(k)).startsWith("BAF")||((String)breakList.get(k)).startsWith("CAF")
                                 ||((String)breakList.get(k)).startsWith("CSF")||((String)breakList.get(k)).startsWith("SURCHARGE")||((String)breakList.get(k)).startsWith("PSS")))
                                  { 
                                  if("F".equalsIgnoreCase(chargeRate.substring(chargeRate.length()-1,chargeRate.length())))
                                           slabFlatList.add("FLAT");
                                  else
                                       if("S".equalsIgnoreCase(chargeRate.substring(chargeRate.length()-1,chargeRate.length())))
                                           slabFlatList.add("SLAB");										
                                  else
                                      remarks.append("The PostFix Should be Either F or S. \n");
                              }
                              else
                              {
                                slabFlatList.add("");
                              }
                            } 
                          }  
                              
                            /*else
                            {
                              remarks.append("Charge Rate is should be >0. \n");
                            }* /
                          }else{

							  try{
								  if(!("FLAT".equalsIgnoreCase(chargeRate) || "SLAB".equalsIgnoreCase(chargeRate) || "BOTH".equalsIgnoreCase(chargeRate) || "LIST".equalsIgnoreCase(chargeRate)))
								  {
									  parseDouble = Double.parseDouble(chargeRate.substring(0,chargeRate.length()));
								  }
							  }catch(NumberFormatException e)
							  {
								   isCurrentyFlag = true;//Added Mohan 
								   parseDouble	= -1;
								   System.out.println(" Current Loc-->"+k);
								   for(int i=0;i<currLoc.length;i++)
								   {	
									   System.out.println("currLoc --->"+currLoc[i]);
									   if(!(currLoc[i]==k))//Added Mohan
										   remarks.append("Charge Rate is not a Number. \n");
								   }

							  }
                         
							  if(!isCurrentyFlag)
							  {
								  chargeRateList.add(chargeRate);
								  isCurrentyFlag = false;
							  }
                          }                          
                        
                        
                      }else
                      {
                      //ADDED BY SUBRAHMANYAM FOR PBN ID: 187381 on 23/oct/09
                         if("List".equalsIgnoreCase(weightBreak) && "Flat".equalsIgnoreCase(rateType))
                          {
                            if(((String)breakList.get(k)).indexOf("BAF")==-1 && ((String)breakList.get(k)).indexOf("CAF")==-1 && 
                            ((String)breakList.get(k)).indexOf("CSF")==-1 && ((String)breakList.get(k)).indexOf("PSS")==-1)
                            {
                              if("NA".equalsIgnoreCase(chargeRate) || "".equalsIgnoreCase(chargeRate))
                              {
                                remarks.append("Value of "+breakList.get(k)+" Should Not be Empty.\n");
                                break;
                              }
                            }
                            else
                            chargeRateList.add(null); //added for 188794 by subrahmanyam on 5th Nov09

                          }
                          else
                          {//ENDED FOR 187381 on 23/oct/09

                          if(k==8)
                             {
                                remarks.append("The Charge Rate Should Not be Empty For MIN");
                                break;
                             }
                          else if(k==9 && "FLAT".equalsIgnoreCase(rateType))
                            {
                              remarks.append("The Charge Rate Should Not be Empty For FLAT");
                              break;
                            }
                          else if(k==9 && "SLAB".equalsIgnoreCase(rateType))
                          {
                             remarks.append("The Charge Rate Should Not be Empty for the slab rate "+wtBreakList.get(1));
                             break;
                          }
                          else if(k==10 && "SLAB".equalsIgnoreCase(rateType))
                          {
                             remarks.append("The Charge Rate Should Not be Empty for the slab rate "+wtBreakList.get(2));								  
                             break;
                          }
                          else
                            chargeRateList.add(null);
                      }
					  }// FOR 187381
           }*///end for loop					
                  
              }//end else count!=0
            count++;
          }//end if (strToken.length>0 )

		  if(count>1 && strTokenLen>1){
		  flatRatesDOB  =  new FlatRatesDOB();
			
				parseInteger = 1;
				try{
          /*String[] date = null;
					
          date = effFromDate.split("/");
          
          if(date.length>0)
          {
            if(Integer.parseInt(date[1])>12)
            {
                remarks.append("Date should be in DD-MM-YYYY format");
            }
          }*/
        
                 
          if(effFromDate!=null&&effFromDate.trim().length()>0)
          {
            effectiveFrom	=	fomater.getTimestampWithTime(loginbean.getUserPreferences().getDateFormat(),effFromDate,"00:00");
            if(effectiveFrom.compareTo(currentDateTimestamp) < 0)
            {
              remarks.append("effective date should be greater than or equal to current date.\n");
            }
          }
					/*effectiveFrom.setHours(0);
					effectiveFrom.setMinutes(0);
					effectiveFrom.setSeconds(0);   */       
				}catch(InvalidDateFormatException e)
				{
					remarks.append("Invalid date format(effectiveFrom).\n");
					parseInteger = -1;

				}catch(InvalidDateDelimiterException e)
				{
					remarks.append("Invalid date format(effectiveFrom).\n");
					parseInteger = -1;
				}
        if(validUpto!=null && !validUpto.trim().equals(""))
        {
            try{
              
              
              validupTo		=   fomater.getTimestampWithTimeAndSeconds(loginbean.getUserPreferences().getDateFormat(),validUpto,"23:59:59");
    
              if(validupTo.compareTo(currentDateTimestamp) <= 0)
              {
                remarks.append("validupTo date should be greater than current date.\n");
              }else
              {
                if(validupTo.compareTo(effectiveFrom) < 0)
                {
                  remarks.append("validupTo date should be greater than effective from date.\n");
                }
              }
              
              /*validupTo.setHours(23);
              validupTo.setMinutes(59);
              validupTo.setSeconds(59);       */   
    
            }catch(InvalidDateFormatException e)
            {
              remarks.append("Invalid date format(validupTo).\n");
              parseInteger = -1;
    
            }catch(InvalidDateDelimiterException e)
            {
              remarks.append("Invalid date format(validupTo).\n");
              parseInteger = -1;
            }
        }else
        {
          validUpto = "";
        }
				//flatRatesDOB.setEffDate(fomater.getTimestamp(loginbean.getUserPreferences().getDateFormat(),effFromDate));
			  //flatRatesDOB.setValidUpto(fomater.getTimestamp(loginbean.getUserPreferences().getDateFormat(),validUpto));
				//buyRateDOB           =   (RateDOB)buyRateDOBHashtable.get(new Integer(rowId));						
				//flatRatesList        =   (ArrayList)buyRateDOB.getRateDtls();				

           //String strUnique = origin+"_"+destination+"_"+carrierId+"_"+serviceLevel+"_"+frequency;
           
           if(origin.equalsIgnoreCase(destination))
           {
             remarks.append("Origin,Destination Should Not Be same"+origin+" "+destination+".\n");
           }
           
           
           
           if(!"2".equalsIgnoreCase(shipmentMode))
           {
             // strUnique = origin+"_"+destination+"_"+carrierId+"_"+serviceLevel;
        	   strUnique = origin+"_"+destination+"_"+carrierId+"_"+serviceLevel+"_"+frCurrency+"_"+densityRatio; // Added by Gowtham for BuyRate Upload Issue
                if(!set_sortedHdr.add(strUnique))
              {
                st_dup   = (String)map.get(strUnique);
                frequencyArry  = frequency.split(",");
                frequencyArryLength = frequencyArry.length;
                arrayIndex=0;
                for(arrayIndex=0;arrayIndex<frequencyArryLength;arrayIndex++)
                {
                    if(st_dup.indexOf(frequencyArry[arrayIndex])>=0)
                    {
                      remarks.append("Duplicate(frequency)/ (FreightCurrency) records are found. \n");
                      break;
                    }
                }
                if(arrayIndex == frequencyArryLength)
                {
                  st_dup  = st_dup+","+frequency;
                  map.put(strUnique,st_dup);
                }
                
              }else
              {
                map.put(strUnique,frequency);
              }
           }else
           {
        	   //strUnique = origin+"_"+destination+"_"+carrierId+"_"+serviceLevel+"_"+frequency;
             strUnique = origin+"_"+destination+"_"+carrierId+"_"+serviceLevel+"_"+frequency+"_"+frCurrency+"_"+densityRatio; // Added by Gowtham for BuyRate Upload Issue
             if(!set_sortedHdr.add(strUnique))
             {
               remarks.append("Dupilcate(frequency)/ (FreightCurrency) records are found. \n");
             }
             
           }

          flatRatesDOB.setOrigin(origin);
          flatRatesDOB.setDestination(destination);
          flatRatesDOB.setServiceLevel(serviceLevel);
          flatRatesDOB.setFrequency(frequency);
          flatRatesDOB.setTransittime(transitTime);				
          flatRatesDOB.setslabFlatList(slabFlatList);
          //flatRatesDOB.setChargeRate(Double.parseDouble(chargeRate));
          //flatRatesDOB.setWtBreakSlab(weightBreakSlab);
          // flatRatesDOB.setNotes("");//@Modified by kameswari for Surcharge Enhancements
       
          flatRatesDOB.setNotes(notes);
          flatRatesDOB.setExtNotes(externalNotes);//Addded by Mohan for Issue No on 04112010
          flatRatesDOB.setCarrierId(carrierId);				
          buyRateDOB.setCarrierId(carrierId);		
          //buyRateDOB.setSurChargeCurrList(wtBreakCurrencyList); //Added by Mohan for issue no.219973 on 01122010 
          buyRateDOB.setSurChargeCurrList(wtBreakCurrencyList1);  // Added by Gowtham for BuyrateUpload Issue. on 07032011
          buyRateDOB.setCurrency(frCurrency1); // Added by Gowtham
          flatRatesDOB.setEffDate(effectiveFrom);
         // flatRatesDOB.setDensityRatio(densityRatio);//added by rk
          flatRatesDOB.setDensityRatio(densityRatio1); // Added by Gowtham.
          if(validUpto!=null && !"".equals(validUpto))
            flatRatesDOB.setValidUpto(validupTo);          
            
          flatRatesDOB.setChargeRateIndicator("");
          flatRatesDOB.setOverPivot("");						
          flatRatesDOB.setChargeRateList(chargeRateList);	
          flatRatesDOB.setRemarks(remarks.toString());
          flatRatesDOB.setLaneNo(count-1);
    	  	flatRatesList.add(flatRatesDOB);     	
			  }// end for (count>1)
        
        if(remarks!=null && remarks.length()>0)
        {
          remarksExist  = true;
        }
        
                data=br.readLine();				
				if (data==null)
					break;				
	      }			
	  }//end for while loop (child data)
      buyRateDOB.setRateDtls(flatRatesList);
      
      request.setAttribute("errorMode",errorMode);
      
       if(remarksExist)
      {
        throw new FoursoftException("Invalid data");
      }
      
      
      
      
      }catch(FoursoftException e)
      {
        e.printStackTrace();
        request.setAttribute("buyRateDOB",buyRateDOB);
        throw new FoursoftException(e.toString());
        //doForwardToErrorPage(loginbean,request,response);
      }
      catch(Exception e)
          {
            e.printStackTrace();
            //Logger.info(FILE_NAME,"Exception while calling bean the data"+e);
             logger.info(FILE_NAME+"Exception while calling bean the data"+e);
            throw new FoursoftException();
            //doForwardToErrorPage(loginbean,request,response);              
          }finally{
           //Added by Mohan for issue no.219973 on 01122010
        	  strToken =null;
        	  currLoc = null;
              chargeRateList     = null;
              slabFlatList       = null;
              tempWtBreakMap     = null;
              tempWtBreakSet     = null;
              wtBreakList        = null;
              wtBreakDescList    = null;
              wtBreakCurrencyList= null;
              wtBreakTypesList   = null;
              rateTypeList       = null;
              fomater	         = null;
              map           	 = null;
              set_sortedHdr      = null;
              breakList 		 = null; 	
              currentDateTimestamp = null;
              if(br!=null)
              {
            	  br             = null;
              }
          }
          
     return buyRateDOB;     
     
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
             int stValLen		=	stValue.length;
              for(int m=0;m<stValLen;m++)
              {

                 
                   parseInteger = Integer.parseInt(stValue[m]);
                   
                   if(parseInteger<1 || parseInteger>7)
                      throw new NumberFormatException();
                      
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
                  int sortValLen	=	sortValues.length;
                   for(int i=0;i<sortValLen;i++)
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
  
  private void doDownLoadProcess(HttpServletRequest request, HttpServletResponse response,ESupplyGlobalParameters loginbean)
  {
        QMSSellRatesDOB sellRatesDOB = null;
        String    operation          = null;
        ArrayList headerValues       =  null;
        ArrayList fslListValues      =  null;
        ArrayList finalList          =  null;
        HttpSession				    session			                  =	  request.getSession();
        QMSSellRatesDOB       sellDob = null;
        PrintWriter          out     =null;
        StringBuffer         errorMessage_data  = null;
        QMSSellRatesDOB     sellRatesDob1 = null;
        String[] chargesValue       = null;
        String[] chargeRateIndicator = null;
        String[] chargeDescription   = null;//Added BYGovind for the CR-219973
        String   srChargeId          = null;//Added BYGovind for the CR-219973
        HashMap  srChargeMap		 = null;//Added BYGovind for the CR-219973
        int chargesValueSize    = 0;
        String    weightBrk = null;
        String    rateTpe   = null;
        String    rate      = null;
        String[] effFrom      = null;
        String[] validUpto    = null;
        ESupplyDateUtility  eSupplyDateUtility=	new ESupplyDateUtility();
        int temp1			=0;
        //ESupplyGlobalParameters loginbean  = (ESupplyGlobalParameters)request.getSession().getAttribute("loginbean");
        
    try
    {
            errorMessage_data   = new StringBuffer();
            operation = request.getParameter("Operation");
            sellRatesDOB  = new QMSSellRatesDOB();
            doGetBuyRates(request,response,loginbean,operation,sellRatesDOB);
            
            headerValues  = (ArrayList)session.getAttribute("HeaderValues");
            session.removeAttribute("HeaderValues");
            response.setContentType("application/vnd.ms-excel"); 
            response.setHeader("Content-Disposition","attachment;filename=BuyRatesDownLoad.xls");
            
            eSupplyDateUtility.setPatternWithTime(loginbean.getUserPreferences().getDateFormat());
            //eSupplyDateUtility.setPatternWithTime(loginbean.getUserPreferences().getDateFormat());
            
            
            out = response.getWriter();            
            
            if(headerValues!=null && headerValues.size()>1)
            {
             
               sellDob					=	(QMSSellRatesDOB)headerValues.get(0);
               finalList				=	(ArrayList)headerValues.get(1);
               fslListValues    = (ArrayList)finalList.get(0);
              
             
              weightBrk				=	sellDob.getWeightBreak();
              rateTpe					=	sellDob.getRateType();
              
              //Added By Kishore to form the common header and data
              
              
              Set<String> 			commonHeaderSet 				=    new LinkedHashSet<String>();
              QMSSellRatesDOB[] objs = new  QMSSellRatesDOB[fslListValues.size()];
              int allTotalHeadcount = 0;
              String tempdesc = "";
              
              for (int i = 0; i < fslListValues.size(); i++){
					objs[i] = (QMSSellRatesDOB) fslListValues.get(i);
					allTotalHeadcount = allTotalHeadcount + objs[i].getRateDescription().length;
				}
              
              Map map = new LinkedHashMap();
              for(int i = 0; i < objs.length; i++){
					QMSSellRatesDOB obj = objs[i];
					String rateDesArry[] = obj.getRateDescription();
					String wtBreaksArry[] = obj.getWeightBreaks();
					for (int j = 0; j < allTotalHeadcount; j++) {
						if (j < rateDesArry.length){
							String rateDes  = rateDesArry[j];
							if (map.containsKey(rateDes)){
								Set s = (Set) map.get(rateDes);
								s.add(wtBreaksArry[j].toUpperCase()); //Added by Kishore
								map.put(rateDes, s);
						}else{
								Set s = new HashSet();
								s.add(wtBreaksArry[j].toUpperCase());
								map.put(rateDes, s);
							}
						}else{
							j = allTotalHeadcount;
						}
					}
				}
           
                
              if(fslListValues!=null && fslListValues.size()>0)
              {

                  
                       errorMessage_data.append("ORIGIN:\t" );
                       errorMessage_data.append("DESTINATION:\t" );
                       //errorMessage_data.append("CURRENCY:\t" ); // Commented by Gowtham for Download Issue.
                       errorMessage_data.append("CARRIER ID:\t" );
                       errorMessage_data.append("SERVICELEVEL:\t" );
                       errorMessage_data.append("FREQUENCY:\t" );
                       if("2".equalsIgnoreCase(sellDob.getShipmentMode()))
                         errorMessage_data.append("APPROXIMATE TRANSIT DAYS:\t" ); 
                      else
                         errorMessage_data.append("APPROXIMATE TRANSIT TIME:\t" );
                      
                       errorMessage_data.append("EFFECTIVE FROM:\t" );
                       errorMessage_data.append("VALID UPTO:\t" );
                       errorMessage_data.append("DENSITY RATIO\t");
                       
                       String finalStr1 ="";
                       
                       Iterator it = map.keySet().iterator();
                      
                       while(it.hasNext()){
                    	   String finalStr = "";
                    	   String rateDes = (String) it.next();
                    	   Set s = (Set) map.get(rateDes);
                    	   Object[] objs1 = null;
                    	   
                    	   if(rateDes.equals("A FREIGHT RATE")){
                    		   commonHeaderSet.add(rateDes);
                    		  if( s.contains("BASIC")){
                    			  s.remove("BASIC");
                    			  commonHeaderSet.add("BASIC~A FREIGHT RATE");
                    		  }
                    		  
                    		  if(s.contains("MIN")){
                    			   s.remove("MIN");
                    			   commonHeaderSet.add("MIN~A FREIGHT RATE");
                    		  }
                    		  
                    		  if(s.contains("FLAT")){
                    			   s.remove("FLAT");
                    			   commonHeaderSet.add("FLAT~A FREIGHT RATE");
                    		  }
                    		  
                    		  if(s.size()!= 0 ){
	                    		  objs1 = s.toArray();
	                    		  
	                    		  if(!"LIST".equalsIgnoreCase(weightBrk)){
	                    		  Double[] brksArray = new Double [objs1.length];	
		                  		  
	                    		  for(int i = 0; i < objs1.length; i++) {
		                  				brksArray[i] = Double.parseDouble(objs1[i].toString());
		                  			}
	                  			
	                  			 Arrays.sort(brksArray);
	                  			
	                  			 for (int i = 0; i < brksArray.length; i++) {
	                  				if (brksArray[i].floatValue() == brksArray[i].intValue()){
	                  					commonHeaderSet.add(brksArray[i].intValue()+"~"+rateDes);
	                  				}else{
	                  					 commonHeaderSet.add(brksArray[i]+"~"+rateDes);
	                  				}
	                  			}
	                    		  }else{
	                    			  
	                    			  	String[] brksArray = new String [objs1.length];	
			                  		  
		                    		  for(int i = 0; i < objs1.length; i++) 
			                  				brksArray[i] = objs1[i].toString();
			                  			
		                  			  
		                    		  for (int i = 0; i < brksArray.length; i++) 
		                  					 commonHeaderSet.add(brksArray[i]+"~"+rateDes);
                    		  
                    	 }
                    		  }
	                    }
                    	 
                    	 else if (rateDes.endsWith("-SS") || rateDes.endsWith("-SB") || rateDes.endsWith("-SF")){
                    		   
                		   s.remove("MIN");
                		   commonHeaderSet.add(rateDes);
                		   commonHeaderSet.add("CURRENCY~"+rateDes); // Added By Kishore For Surcharge Currency
                		   commonHeaderSet.add("MIN~"+rateDes);
                		   objs1 = s.toArray();
                		   
                		   Double[] brksArray = new Double [objs1.length];	
                			for(int i = 0; i < objs1.length; i++) {
                				brksArray[i] = Double.parseDouble(objs1[i].toString());
                			}
                			
                			Arrays.sort(brksArray);
                			
                			for (int i = 0; i < brksArray.length; i++) {
                				if (brksArray[i].floatValue() == brksArray[i].intValue()){
                					commonHeaderSet.add(brksArray[i].intValue()+"~"+rateDes);
                				}else{
                					 commonHeaderSet.add(brksArray[i]+"~"+rateDes);
                				}
                			}
                    			
                	   }else{
                		   commonHeaderSet.add(rateDes);
                		   commonHeaderSet.add("CURRENCY~"+rateDes); // Added By Kishore For Surcharge Currency
                		   objs1 = s.toArray();
                		   for(int i = 0; i<objs1.length;i++){
                			  if (objs1[i].toString().endsWith("BASIC")){
                				  commonHeaderSet.add(objs1[i]+"~"+rateDes);
                				  finalStr = finalStr + "\t";
                				  objs1[i]="";
                				  i = objs1.length;
                			  }
                		   }
                		   for(int i = 0; i<objs1.length;i++){
                 			  if (objs1[i].toString().endsWith("MIN")){
                 				 commonHeaderSet.add(objs1[i]+"~"+rateDes);
                 				  finalStr = finalStr + "\t";
                 				  objs1[i]="";
                 				  i = objs1.length;
                 			  }
                 		   }
                		   for(int i = 0; i<objs1.length;i++){
                 			  if (objs1[i].toString().endsWith("FLAT")){
                 				 commonHeaderSet.add(objs1[i]+"~"+rateDes);
                 				  finalStr = finalStr + "\t";
                 				  objs1[i]="";
                 				  i = objs1.length;
                 			  }
                 		   }
                		   for(int i = 0; i<objs1.length;i++){
                  			  if (objs1[i].toString().endsWith("PERCENT")){
                  				 commonHeaderSet.add(objs1[i]+"~"+rateDes);
                  				  finalStr = finalStr + "\t";
                  				  objs1[i]="";
                  				  i = objs1.length;
                  			  }
                  		   }
                		   for(int i = 0; i<objs1.length;i++){
                			   if (!objs1[i].toString().equals("")){
                				   commonHeaderSet.add(objs1[i]+"~"+rateDes);
                   				  finalStr = finalStr + "\t";
                   			}
                			   finalStr = finalStr + "\t";
                		   }
                	   }
                    	  
                    }
                       sellRatesDob1					=	(QMSSellRatesDOB)fslListValues.get(0);
                       String[] weighttBkValues		=	sellRatesDob1.getWeightBreaks();
                       int	wtBreakLength				=	weighttBkValues.length;
                       srChargeMap       		= 	sellRatesDob1.getSurchargesIds();   //Added by Govind for cr 219973
                       chargeDescription = sellRatesDob1.getRateDescription();
                    		   
                       
                       //Adding the Freight Rate and Surhcarge Weight Breaks : Kishore
                       int tempCount = 0;
                       String headerBreak="";
                       for( String s : commonHeaderSet){
                    	    if(s.indexOf('~') == -1)
                    	    	tempCount = s.length();
                    	    else
                    	    	tempCount = s.indexOf('~');
                    	   
                    	   headerBreak = s.substring(0,tempCount).toUpperCase();
                    	   if(!"LIST".equalsIgnoreCase(weightBrk)){
                    		   		errorMessage_data.append(headerBreak.length()>5  && (headerBreak.endsWith("BASIC") || headerBreak.endsWith("MIN") || headerBreak.endsWith("FLAT") || headerBreak.endsWith("PERCENT"))?headerBreak.substring(5)+"\t":headerBreak+"\t");
                    	   }
                    	   else{
                    		   errorMessage_data.append((headerBreak.length()>4&& headerBreak.length()<10 && !headerBreak.startsWith("CURRENCY")) ?headerBreak.substring(0,headerBreak.length()-5)+"\t":headerBreak+"\t");
                    	   }
                    	  if(s.equalsIgnoreCase("A FREIGHT RATE"))
                    		errorMessage_data.append("CURRENCY:\t");  
                       }
                        //Modified by Mohan for Issue No.219976 on 4-11-2010
                       errorMessage_data.append("INTERNAL NOTES:\t");
                       errorMessage_data.append("EXTERNAL NOTES:\t");
                       errorMessage_data.append("TERMINALID:\t");
                       
                       //errorMessage_data.append("RATES DESCRIPTION:\t");
                       errorMessage_data.append("\n");
                        int listSize = fslListValues.size();
                        for(int i=0;i<listSize;i++)
                        {
                            effFrom       = null;
                            validUpto     = null;
                            sellRatesDob1 = (QMSSellRatesDOB)fslListValues.get(i);
                            temp1=0; // Added by Gowtham
                        
                            errorMessage_data.append(sellRatesDob1.getOrigin()+"\t");
                            errorMessage_data.append(sellRatesDob1.getDestination()+"\t");
                            //errorMessage_data.append(sellRatesDob1.getCurrencyId()+"\t"); // Commented by Gowtham for Buyrate Download Issue.
                            errorMessage_data.append(sellRatesDob1.getCarrier_id()+"\t");
                            errorMessage_data.append(sellRatesDob1.getServiceLevel()+"\t");
                            errorMessage_data.append(sellRatesDob1.getFrequency()+"\t");
                            errorMessage_data.append(sellRatesDob1.getTransitTime()+"\t");
                                                      
                                if(sellRatesDob1.getEffectiveFrom()!=null)
                                effFrom   = eSupplyDateUtility.getDisplayStringArray(sellRatesDob1.getEffectiveFrom());
                            
                            errorMessage_data.append(effFrom!=null?effFrom[0]:"");
                            errorMessage_data.append("\t");
                            
                            if(sellRatesDob1.getValidUpto()!=null)
                                validUpto = eSupplyDateUtility.getDisplayStringArray(sellRatesDob1.getValidUpto());
                          errorMessage_data.append(validUpto!=null?validUpto[0]:"");
                            errorMessage_data.append("\t");
                            errorMessage_data.append(sellRatesDob1.getDensityRatio()+"\t");
                            chargesValue			=	sellRatesDob1.getChargeRates();
                            chargeRateIndicator	=	sellRatesDob1.getChargeInr();
                            chargesValueSize  = chargesValue.length;
                            weighttBkValues		=	sellRatesDob1.getWeightBreaks(); // Added By kishore
                            String[] rateDescArry			= 	sellRatesDob1.getRateDescription();
                          
                            //Added by Kishroe For SurCharge Currency
                           
                            HashMap<String, String>	 surChargeMap = new HashMap<String, String>();
                            
                            int currCount=0; 
                            String[] surChargeCurrency		=   sellRatesDob1.getSurChargeCurency();
                            String   tempRateDesc = "";
                          
                            for (int j = 0; j < rateDescArry.length; j++) {
                            	weighttBkValues[j] = weighttBkValues[j].toUpperCase()+"~"+rateDescArry[j]; //Added by Kishore 
                            //Modified By Kishore for SCH-Currency
                            	if(!"A FREIGHT RATE".equalsIgnoreCase(rateDescArry[j])
                            			&& ("".equalsIgnoreCase(tempRateDesc)
                            				|| !tempRateDesc.equalsIgnoreCase(rateDescArry[j]))
                            	 ){
                            		surChargeMap.put("CURRENCY~"+rateDescArry[j],surChargeCurrency[j]);
                            		tempRateDesc=rateDescArry[j];
                            		currCount++;
                            	}
							}
                           
                            String[] commonHeaderArray1 = new String[commonHeaderSet.size()];
                            commonHeaderArray1 = (String[])commonHeaderSet.toArray(commonHeaderArray1);
                            
                            List  dataList = (List) Arrays.asList(weighttBkValues);
                            int temp = 0;
                            
                            //Added By Kishore For SCH- Currency 
                            String ratedesc = "" ; 
                            
                            for (int k=0; k<commonHeaderArray1.length; k++)
                			{
                		
                				temp = dataList.indexOf(commonHeaderArray1[k]); //kishore
                				ratedesc = commonHeaderArray1[k].indexOf('~')==-1?commonHeaderArray1[k]:"~~~"; //sch-currency
                				if (temp!= -1){
                					if("SLAB".equalsIgnoreCase(chargeRateIndicator[temp]))
                						rate = chargesValue[temp]+"S";
                					else if("FLAT".equalsIgnoreCase(chargeRateIndicator[temp]))
                						rate = chargesValue[temp]+"F";
                					else
                						rate = chargesValue[temp]+"";
                					
                					errorMessage_data.append(rate +"\t");
                                }else
                                {
                					          					
                					//Added By Kishore For SCH- Currency ..
                                	if("A FREIGHT RATE".equalsIgnoreCase(ratedesc)) {
                					errorMessage_data.append("-"+"\t");
                					if(temp1==0){
                					errorMessage_data.append(sellRatesDob1.getCurrencyId()+"\t"); // Added by Gowtham
                					temp1++;
                					}
                                }
                                	else{ 
                                		
                                		errorMessage_data.append(surChargeMap.containsKey(commonHeaderArray1[k])
                                								 ? (surChargeMap.get(commonHeaderArray1[k])!=null && !"-".equals(surChargeMap.get(commonHeaderArray1[k]))? surChargeMap.get(commonHeaderArray1[k]):sellRatesDob1.getCurrencyId() )+"\t"
                                								 : "-\t"
                                								);
                                	}
                                }
                				//End of Kishore For SCH- Currency ..
                            }
                            
                            errorMessage_data.append((sellRatesDob1.getNotes()!=null)?sellRatesDob1.getNotes():"");
                            errorMessage_data.append("\t");
                             //Modified by Mohan for Issue No.219976 on 2-11-2010
                            errorMessage_data.append((sellRatesDob1.getExtNotes()!=null)?sellRatesDob1.getExtNotes():"");
                            errorMessage_data.append("\t");
                            errorMessage_data.append(sellRatesDob1.getTerminalId()+"\t");
                           
                            System.out.println(sellRatesDob1.getRateDescription()+"\t");
                            errorMessage_data.append("\n");
                        }
                        
              }else
              { errorMessage_data.append("No data Found");}
          }else
          {
            errorMessage_data.append("No data Found");
          }
                        out.print(errorMessage_data.toString());
    }catch(Exception e)
    {
      e.printStackTrace();
      //Logger.info(FILE_NAME,""+e);
      logger.info(FILE_NAME+""+e);
    }
  }

  private void doGetBuyRates(HttpServletRequest request, HttpServletResponse response,ESupplyGlobalParameters loginBean,String operation,QMSSellRatesDOB sellRatesDOB)throws IOException,ServletException
{
  ErrorMessage			        errorMessageObject                =   null;
  ArrayList			            keyValueList	                  =   new ArrayList();
  BuyRatesSessionHome			sellRatesHome                     =   null;
  BuyRatesSession				sellRatesRemote                   =   null;
  ArrayList			            headerValues	                  =   null;
  ArrayList			            headerVal	                      =   null;
  StringBuffer					errorMassege                      =   null;
  HttpSession				    session			                  =	  request.getSession();
  String                nextNavigation                = null;
	try
	{  

	insertValues(request,response,loginBean,operation,sellRatesDOB,nextNavigation);
	sellRatesHome		  =	  (BuyRatesSessionHome)LookUpBean.getEJBHome("BuyRatesSessionBean");
	sellRatesRemote	  =	  (BuyRatesSession)sellRatesHome.create();
    
    errorMassege      =   sellRatesRemote.validateSellRatesHdrData(sellRatesDOB);
    if(errorMassege.length() > 0)
    {
        headerVal = new ArrayList();
        //System.out.println("************After Look up**************** ");
        request.setAttribute("Errors",errorMassege.toString());
        headerVal.add(sellRatesDOB);
        session.setAttribute("HeaderValue",headerVal);
        nextNavigation			=	 "etrans/BuyRatesDownLoad.jsp";
        
        doDispatcher(request,response,nextNavigation);
        
    }
    else
    {
        headerValues      =   sellRatesRemote.getSellRatesValues(sellRatesDOB,loginBean,operation);   
        //System.out.println("************After Look up**************** "+headerValues.size());
        session.setAttribute("HeaderValues",headerValues);
        
        
        
    }
    nextNavigation			=	 "etrans/BuyRatesDownLoad.jsp";
	}
	catch(Exception e)
	{
		e.printStackTrace();
		errorMessageObject = new ErrorMessage("Error while doGetBuyRates()",nextNavigation); 
		keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
		keyValueList.add(new KeyValue("Operation",operation)); 	
		errorMessageObject.setKeyValueList(keyValueList);
		request.setAttribute("ErrorMessage",errorMessageObject);
		doDispatcher(request,response,"ESupplyErrorPage.jsp");
	}
}  


private void insertValues(HttpServletRequest request, HttpServletResponse response,ESupplyGlobalParameters loginBean,String operation,QMSSellRatesDOB sellRatesDOB,String nextNavigation)throws IOException,ServletException
{
    ErrorMessage			        errorMessageObject                =   null;
    ArrayList			            keyValueList	                    =   new ArrayList();
    String[]			            carriers			                    =	  null;
    String[]                  marginValues                      =   null;
     String                   shipmentMode                      =   null;
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
          sellRatesDOB.setCurrencyId(loginBean.getCurrencyId());
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

    }
    catch(Exception e)
    {
        e.printStackTrace();
        errorMessageObject = new ErrorMessage("Error while insertValues()",nextNavigation); 
        keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
        keyValueList.add(new KeyValue("Operation",operation)); 	
        errorMessageObject.setKeyValueList(keyValueList);
        request.setAttribute("ErrorMessage",errorMessageObject);
        doDispatcher(request,response,"ESupplyErrorPage.jsp");
    }
}
  
  
  private String validateWBreaks(ArrayList wtbreakList,String shipmentMode)throws Exception
  {
  
          BuyRatesSessionHome home      =   null;
          BuyRatesSession 		remote      =   null;
          String              remarksList = null;

    try
    {
                 home	=	(BuyRatesSessionHome)LookUpBean.getEJBHome("BuyRatesSessionBean");
                remote	=	(BuyRatesSession)home.create();	
                remarksList = remote.validateWBreaks(wtbreakList,shipmentMode);
                
    }catch(Exception e)
    {
      e.printStackTrace();
      throw new Exception();
    }finally
    {
      remote = null;
      home = null;
      
    }
    return remarksList;
  }
   //Added by Mohan for issue no.219973 on 01122010
  private HashMap getFlatSurCharges(String shipmentMode)throws Exception
  {

	BuyRatesSessionHome home      =   null;
	BuyRatesSession 		remote      =   null;
	HashMap              surChargelist = null;
	try
	{ 
		home	=	(BuyRatesSessionHome)LookUpBean.getEJBHome("BuyRatesSessionBean");
		remote	=	(BuyRatesSession)home.create();	
		surChargelist= remote.getFlatSurchargeList(shipmentMode);
	}catch(Exception e)
	{
	  e.printStackTrace();
	  throw new Exception();
	}finally
	{
	  remote = null;
	  home = null;
	}
	return surChargelist;
  }
   //Added by Mohan for issue no.219973 on 01122010
  private TreeSet getListSurCharges(String shipmentMode)throws Exception
  {

	BuyRatesSessionHome home      =   null;
	BuyRatesSession 		remote      =   null;
	TreeSet              surChargelist = null;
	try
	{ 
		home	=	(BuyRatesSessionHome)LookUpBean.getEJBHome("BuyRatesSessionBean");
		remote	=	(BuyRatesSession)home.create();	
		surChargelist = remote.getListSurchargeList(shipmentMode);
	}catch(Exception e)
	{
	  e.printStackTrace();
	  throw new Exception();
	}finally
	{
	  remote = null;
	  home = null;
	}
	return surChargelist;
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
    nextNavigation  = "QMSBuyRatesUploadController?Operation=UpLoad&amp;subOperation=";
    RateDOB        rateDOB    =   null;
    ArrayList      list_Dtls  = null;
    FlatRatesDOB   flatRatesDOB = null;
    String         remarks    = null;
    int            listSize = 0;
    String         errorMode = null;
    PrintWriter   out       = null;
    String              str[]       = null;
    String              strn[]      = null;
    
    ArrayList      customerDtl  = null;
    boolean        flag  =false;
    try{
             
            rateDOB = (RateDOB)request.getAttribute("buyRateDOB");
            customerDtl =  (ArrayList)request.getAttribute("customerDtl");
            errorMode = (String)request.getAttribute("errorMode");
           //@@Added by kameswari for teh WPBN issue-15581
             ESupplyDateUtility eSupplyDateUtil=new ESupplyDateUtility();
             String dateFormat	=	loginbean.getUserPreferences().getDateFormat();
		         eSupplyDateUtil.setPattern(dateFormat);
             //@@WPBN issue-15581
            if(rateDOB!=null && subOperation.equalsIgnoreCase("UpLoad"))
            {
             
                errorMessage.append("The following errors occured while process the data:(RowId)\n");
                remarks = rateDOB.getRemarks();
                if(errorMode.equalsIgnoreCase("Y"))
                {
                    
                    response.setContentType("application/vnd.ms-excel"); 
                    response.setHeader("Content-Disposition","attachment;filename=Exceptions.xls");
                    
                    out = response.getWriter();
                    
                    //if(remarks!=null && !"".equals(remarks.trim()))
                    //{
                         //@@Commented by kameswari for the WPBN issue-15581
                        /*errorMessage.append("SHIPMENT MODE:\t");
                        errorMessage.append("CURRENCY:\t");
                        errorMessage.append("WEIGHT BREAK:\t");
                        errorMessage.append("WEIGHT_CLASS:\t");
                        errorMessage.append("RATE TYPE:\t");
                        errorMessage.append("UOM:\t");
                        errorMessage.append("CONSOLETYPE:\t");
                        errorMessage.append("REMARKS:\t" );
                        errorMessage.append("\n");
                        errorMessage.append(rateDOB.getShipmentMode()+"\t");
                        errorMessage.append(rateDOB.getCurrency()+"\t");
                        errorMessage.append(rateDOB.getWeightBreak()+"\t");
                        errorMessage.append(rateDOB.getWeightClass()+"\t");
                        errorMessage.append(rateDOB.getRateType()+"\t");
                        errorMessage.append(rateDOB.getUom()+"\t");
                        errorMessage.append(rateDOB.getConsoleType()+"\t");
                        errorMessage.append(rateDOB.getRemarks()+"\t");
                        errorMessage.append("\n");
                        errorMessage.append("\n");*/
                        //@@WPBN issue-15581
                        
                        //@@Added by kameswari for the WPBN issue-15581
                        errorMessage.append("<html>");
                        errorMessage.append("<body>");
                        errorMessage.append("<table width='100%' border='1' cellspacing='0' cellpadding='0'>");
                        errorMessage.append("<tr align='center'>");
                        errorMessage.append("<td><b>SHIPMENT MODE</b></td>");
                        errorMessage.append("<td><b>CURRENCY</b></td>");
                        errorMessage.append("<td><b>WEIGHT BREAK</b></td>");
                        errorMessage.append("<td><b>RATE TYPE</b></td>");
                        errorMessage.append("<td><b>UOM</b></td>");
                        errorMessage.append("<td><b>CONSOLETYPE</b></td>");
                        errorMessage.append("<td><b>REMARKS</b></td>");
                        errorMessage.append("</tr>");
                        errorMessage.append("<tr align='center'>");
                        errorMessage.append("<td>").append(rateDOB.getShipmentMode()).append("</td>");
                        errorMessage.append("<td>").append(rateDOB.getCurrency()).append("</td>");
                        errorMessage.append("<td>").append(rateDOB.getWeightBreak()).append("</td>");
                        errorMessage.append("<td>").append(rateDOB.getWeightClass()).append("</td>");
                        errorMessage.append("<td>").append(rateDOB.getRateType()).append("</td>");
                        errorMessage.append("<td>").append(rateDOB.getUom()).append("</td>");
                        errorMessage.append("<td>").append(rateDOB.getConsoleType()).append("</td>");
                        errorMessage.append("<td>").append(rateDOB.getRemarks()).append("</td>");
                        errorMessage.append("</tr>");
                        //@@WPBN issue-15581
                               
                        list_Dtls = (ArrayList)rateDOB.getRateDtls();
                        
                        if(list_Dtls!=null && list_Dtls.size()>0)
                        {
                        
                          listSize = list_Dtls.size();
                          //@@Commented by kameswari for the WPBN issue-15581
                          /*errorMessage.append("RowId:\t");
                          errorMessage.append("ORIGIN:\t");
                          errorMessage.append("DESTINATION:\t" );
                          errorMessage.append("CARRIER ID:\t" );
                          errorMessage.append("SERVICELEVEL:\t" );
                          errorMessage.append("FREQUENCY:\t" );
                          errorMessage.append("TRANSIT TIME:\t" ); 
                          errorMessage.append("EFFECTIVE FROM:\t" );  
                          errorMessage.append("VALID UPTO:\t" );
                          errorMessage.append("REMARKS:\t" );  
                          errorMessage.append("\n");*/
                          //@@WPBN issue-15581
                          
                          //@@Added by kameswari for the WPBN issue-15581
                          errorMessage.append("<table width='100%' border='1' cellspacing='0' cellpadding='0'>");
                          errorMessage.append("<tr align='center'>");
                          errorMessage.append("<td><b>RowId</b></td>");
                          errorMessage.append("<td><b>ORIGIN</b></td>");
                          errorMessage.append("<td><b>DESTINATION</b></td>");
                          errorMessage.append("<td><b>CARRIER ID</b></td>");
                          errorMessage.append("<td><b>SERVICELEVEL</b></td>");
                          errorMessage.append("<td><b>FREQUENCY</b></td>");
                           if("2".equalsIgnoreCase(rateDOB.getShipmentMode()))
                          {
                               errorMessage.append("<td><b>APPROXIMATE TRANSIT DAYS</b></td>");
                            }
                             else
                           {
                          errorMessage.append("<td><b>APPROXIMATE TRANSIT TIME</b></td>");
                            }
                          errorMessage.append("<td><b>EFFECTIVE FROM</b></td>");
                          errorMessage.append("<td><b>VALID UPTO</b></td>");
                          errorMessage.append("<td><b>REMARKS</b></td>");
                          errorMessage.append("</tr>");
                          //@@WPBN issue-15581
                          for(int i=0;i<listSize;i++)
                          {
                            flatRatesDOB = (FlatRatesDOB)list_Dtls.get(i);
                            remarks      = flatRatesDOB.getRemarks();
                            if(remarks!=null && !"".equals(remarks.trim()))
                            {
                               //@@Added by kameswari for the WPBN issue-15581  
                                  str  = eSupplyDateUtil.getDisplayStringArray(flatRatesDOB.getEffDate());
                                  if(flatRatesDOB.getValidUpto()!=null)
                                   {
                                      strn  =  eSupplyDateUtil.getDisplayStringArray(flatRatesDOB.getValidUpto());
                                   }
                                   //@@WPBN issue-15581 
                               //@@Commented by kameswari for the WPBN issue-15581
                              /* errorMessage.append(i+5);
                               errorMessage.append("\t");
                               errorMessage.append(flatRatesDOB.getOrigin()+"\t");
                               errorMessage.append(flatRatesDOB.getDestination()+"\t");
                               errorMessage.append(flatRatesDOB.getCarrierId()+"\t");
                               errorMessage.append(flatRatesDOB.getServiceLevel()+"\t"); 
                               errorMessage.append(flatRatesDOB.getFrequency()+"\t");
                               errorMessage.append(flatRatesDOB.getTransittime()+"\t");
                               errorMessage.append(flatRatesDOB.getEffDate()+"\t");
                               errorMessage.append((flatRatesDOB.getValidUpto()!=null)?flatRatesDOB.getValidUpto().toString():""+"\t");
                               errorMessage.append(flatRatesDOB.getRemarks());    
                               errorMessage.append("\n");
                            
                               out.print(flatRatesDOB.getOrigin()+"\t");
                               out.print(flatRatesDOB.getDestination()+"\t");
                               out.print(flatRatesDOB.getCarrierId()+"\t");
                               out.print(flatRatesDOB.getServiceLevel()+"\t"); 
                               out.print(flatRatesDOB.getFrequency()+"\t");
                               out.print(flatRatesDOB.getTransittime()+"\t");
                               out.print(flatRatesDOB.getEffDate()+"\t");
                               out.print(flatRatesDOB.getValidUpto()+"\t");
                               out.println(flatRatesDOB.getRemarks());  */  
                               //@@WPBN issue-15581
                               
                               //@@Added by kameswari for the WPBN issue-15581
                               errorMessage.append("<tr align='center'>");
                               errorMessage.append("<td>").append(i+5).append("</td>");
                               errorMessage.append("<td>").append(flatRatesDOB.getOrigin()).append("</td>");
                               errorMessage.append("<td>").append(flatRatesDOB.getDestination()).append("</td>");
                               errorMessage.append("<td>").append(flatRatesDOB.getCarrierId()).append("</td>");
                               errorMessage.append("<td>").append(flatRatesDOB.getServiceLevel()).append("</td>");
                               errorMessage.append("<td>").append(flatRatesDOB.getFrequency()).append("</td>");
                              errorMessage.append("<td>").append(flatRatesDOB.getTransittime()).append("</td>");
                              errorMessage.append("<td>").append(str[0]).append("</td>");
                              errorMessage.append("<td>").append((flatRatesDOB.getValidUpto()!=null)?strn[0]:"").append("</td>");
                              errorMessage.append("<td>").append(flatRatesDOB.getRemarks()).append("</td>");
                              errorMessage.append("</tr>");
                              //@@WPBN issue-15581
                            }
                          }
                               //@@Added by kameswari for the WPBN issue-15581 
                               errorMessage.append("</body>");
                               errorMessage.append("</html>");
                               //@@WPBN issue-15581
                        }                       
                       // System.out.println("errorMessageerrorMessage::"+errorMessage);
                        out.print(errorMessage.toString());
                    
                    //}
                    
                    
                  
                }else
                {
                    if(remarks!=null && !"".equals(remarks.trim()))
                    {
                      System.out.println(remarks);
                      System.out.println("External Notes Header Not Defined.");
                      
                      if(!"External Notes Header Not Defined.\n".equalsIgnoreCase(remarks))
                      {	 errorMessage.append(2);
                         errorMessage.append(".");
                      }
                      errorMessage.append(remarks);
                    }
                    
                    list_Dtls = (ArrayList)rateDOB.getRateDtls();
                    if(list_Dtls!=null && list_Dtls.size()>0)
                    {
                      listSize = list_Dtls.size();
                      for(int i=0;i<listSize;i++)
                      {
                        flatRatesDOB = (FlatRatesDOB)list_Dtls.get(i);
                        remarks      = flatRatesDOB.getRemarks();
                        if(remarks!=null && !"".equals(remarks))
                        {
                          errorMessage.append("\n");
                          errorMessage.append(i+5);
                          errorMessage.append(".");
                          errorMessage.append(remarks);
                          
                        }
                      }
                    }
                  errorMessageObject = new ErrorMessage(errorMessage.toString(),nextNavigation); 
                  keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
                  keyValueList.add(new KeyValue("Operation",operation)); 	
                  errorMessageObject.setKeyValueList(keyValueList);
                  request.setAttribute("ErrorMessage",errorMessageObject);    
                  doDispatcher(request,response,"QMSESupplyErrorPage.jsp");//use send redirect 
                  //session.setAttribute("ErrorMessageObj",errorMessageObject);
                  //response.sendRedirect("QMSESupplyErrorPage.jsp");                    
                    
                }               
              
            }else if(customerDtl!=null && subOperation.equalsIgnoreCase("UpLoadCustomerDetails"))
            {
                nextNavigation  = "QMSBuyRatesUploadController?Operation=UpLoad&amp;subOperation=upLoadCustomer";
                errorMessage.append("The following errors occured while process the data:(RowId)\n");
               
                if(errorMode.equalsIgnoreCase("Y"))
            {
                    String addresstype=null;
                    response.setContentType("application/vnd.ms-excel"); 
                    response.setHeader("Content-Disposition","attachment;filename=Exceptions.xls");
                    out = response.getWriter();
                        errorMessage.append("<html>");
                        errorMessage.append("<body>");
                        errorMessage.append("<table width='100%' border='1' cellspacing='0' cellpadding='0'>");
                        errorMessage.append("<tr align='center'>");
                        errorMessage.append("<td><b>TERMINAL ID</b></td>");
                        errorMessage.append("<td><b>ABBREVIATED NAME</b></td>");
                        errorMessage.append("<td><b>COMPANY NAME</b></td>");
                       errorMessage.append("<td><b>ADDRESS TYPE</b></td>");
                        errorMessage.append("<td><b>CONTACT PERSON</b></td>");
                        errorMessage.append("<td><b>DESIGNATION</b></td>");
                        errorMessage.append("<td><b>ADDRESS LINE 1</b></td>");
                        errorMessage.append("<td><b>ADDRESS LINE 2</b></td>");
                        errorMessage.append("<td><b>CITY</b></td>");
                        errorMessage.append("<td><b>STATE</b></td>");
                        errorMessage.append("<td><b>COUNTRY ID</b></td>");
                        errorMessage.append("<td><b>POSTAL CODE</b></td>");
                        errorMessage.append("<td><b>PHONE NO</b></td>");
                        errorMessage.append("<td><b>FAX NO</b></td>");
                        errorMessage.append("<td><b>email Id</b></td>");
                        errorMessage.append("<td><b>REMARKS</b></td>");
                        errorMessage.append("</tr>");
                        int custDtlsSize	=	customerDtl.size();
                        for(int i=0;i<custDtlsSize;i++){
                          
                          ArrayList temp=(ArrayList)customerDtl.get(i);
                          ArrayList remarksList = (ArrayList)temp.get(3);
                          CustomerModel customerModel=(CustomerModel) temp.get(0);
                          Address addressObj=(Address)temp.get(1);  
                          CustContactDtl custDtl=new CustContactDtl();
                          ArrayList custDtlList=(ArrayList)temp.get(2);
                        errorMessage.append("<tr align='center'>");
                        errorMessage.append("<td>").append(customerModel.getTerminalId()).append("</td>");
                        errorMessage.append("<td>").append(customerModel.getAbbrName()).append("</td>");
                        errorMessage.append("<td>").append(customerModel.getCompanyName()).append("</td>");
                        errorMessage.append("<td>").append("Default").append("</td>");
                        errorMessage.append("<td>").append(customerModel.getContactName()).append("</td>");
                        errorMessage.append("<td>").append(customerModel.getDesignation()).append("</td>");
                        errorMessage.append("<td>").append(addressObj.getAddressLine1()).append("</td>");
                        errorMessage.append("<td>").append(addressObj.getAddressLine2()).append("</td>");
                        errorMessage.append("<td>").append(addressObj.getCity()).append("</td>");
                        errorMessage.append("<td>").append(addressObj.getState()).append("</td>");
                        errorMessage.append("<td>").append(addressObj.getCountryId()).append("</td>");
                        errorMessage.append("<td>").append(addressObj.getZipCode()).append("</td>");
                        errorMessage.append("<td>").append(addressObj.getPhoneNo()).append("</td>");
                        errorMessage.append("<td>").append(addressObj.getFax()).append("</td>");
                        errorMessage.append("<td>").append(addressObj.getEmailId()).append("</td>");
                        errorMessage.append("<td>").append(remarksList.get(0).toString()).append("</td>");
                        errorMessage.append("</tr>");
                        int custDtlListSize		=	custDtlList.size();
                        for(int p=0;p<custDtlListSize;p++){
                        custDtl=(CustContactDtl)custDtlList.get(p);
                        
                        errorMessage.append("<tr align='center'>");
                        errorMessage.append("<td>").append("").append("</td>");
                        errorMessage.append("<td>").append("").append("</td>");
                        errorMessage.append("<td>").append("").append("</td>");
                        if("P".equalsIgnoreCase(custDtl.getAddrType()))
                        {
                            addresstype   = "Pickup";
                        }
                        else  if("B".equalsIgnoreCase(custDtl.getAddrType()))
                        {
                             addresstype   = "Billing";
                        }
                        else
                        {
                            addresstype   = "Delivery";
                        }
                        errorMessage.append("<td>").append(addresstype).append("</td>");
                        errorMessage.append("<td>").append(custDtl.getContactPerson()).append("</td>");
                        errorMessage.append("<td>").append(custDtl.getDesignation()).append("</td>");
                        errorMessage.append("<td>").append("").append("</td>");
                        errorMessage.append("<td>").append("").append("</td>");
                        errorMessage.append("<td>").append("").append("</td>");
                        errorMessage.append("<td>").append("").append("</td>");
                        errorMessage.append("<td>").append("").append("</td>");
                        errorMessage.append("<td>").append(custDtl.getZipCode()).append("</td>");
                        errorMessage.append("<td>").append(custDtl.getContact()).append("</td>");
                        errorMessage.append("<td>").append(custDtl.getFax()).append("</td>");
                        errorMessage.append("<td>").append(custDtl.getEmail()).append("</td>");
                        errorMessage.append("<td>").append(remarksList.get(p+1).toString()).append("</td>");
                        errorMessage.append("</tr>");
                        } 
                        //@@WPBN issue-15581
                        
                   }
                   errorMessage.append("</body>");
                   errorMessage.append("</html>");         
                  out.print(errorMessage.toString());
                }
                else
                {
                    if(remarks!=null && !"".equals(remarks.trim()))
            {
                      errorMessage.append(2);
                      errorMessage.append(".");                
                      errorMessage.append(remarks);
                    }
                  nextNavigation  = "QMSBuyRatesUploadController?Operation=UpLoad&amp;subOperation=upLoadCustomer";
                  errorMessageObject = new ErrorMessage(errorMessage.toString(),nextNavigation); 
                  keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
                  keyValueList.add(new KeyValue("Operation",operation)); 	
                  errorMessageObject.setKeyValueList(keyValueList);
                  request.setAttribute("ErrorMessage",errorMessageObject);    
                  doDispatcher(request,response,"QMSESupplyErrorPage.jsp");//use send redirect 
                }               
        }
            
          else{
              if(operation.equals("UpLoad"))
                { errorMessage.append("Exception While UpLoading the records :");}
              else if(operation.equals("DownLoad"))
                { errorMessage.append("Exception While DownLoading the records :");}

                  errorMessageObject = new ErrorMessage(errorMessage.toString(),nextNavigation); 
                  keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
                  keyValueList.add(new KeyValue("Operation",operation)); 	
                  errorMessageObject.setKeyValueList(keyValueList);
                  request.setAttribute("ErrorMessage",errorMessageObject);    
                  doDispatcher(request,response,"QMSESupplyErrorPage.jsp");//use send redirect 
                  //session.setAttribute("ErrorMessageObj",errorMessageObject);
                  //response.sendRedirect("QMSESupplyErrorPage.jsp");
            }
    }catch(Exception e)
    {
     e.printStackTrace();
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
      logger.error(FILE_NAME+ " [doDispatcher() "+ " Exception in forwarding to "+forwardFile+ ex);
		}
	}  
	 //Added by Mohan for issue no.219973 on 01122010
	/**
	 * @param wtBkList
	 * @param wtBreakDescList
	 * @param rateTypeList
	 * @param wtBreakMap
	 * @param shipmentMode
	 * @param chrgHdrs
	 * @param startIndex
	 * @param endIndex
	 * @param wtBrkTypesList
	 * @param tempWtBreakSet
	 * @return String,error Messages
	 * @throws Exception
	 */
	public String validateSurchargeHdrs(ArrayList wtBkList,ArrayList wtBreakDescList,ArrayList rateTypeList,HashMap wtBreakMap,String shipmentMode,String chrgHdrs[],int startIndex,int endIndex,ArrayList wtBrkTypesList,TreeSet tempWtBreakSet)throws Exception
	{
		 StringBuffer remarks = new StringBuffer();
		 String temp		  = "";
		 String frgtWtBrk ="";

		 String surWtBreak = chrgHdrs[startIndex+2];
		 frgtWtBrk = wtBrkTypesList.get(0)+"";
		 frgtWtBrk = frgtWtBrk.substring(0, frgtWtBrk.indexOf("~"));
		 
		 if("FLAT".equals(frgtWtBrk) || "SLAB".equals(frgtWtBrk))
		 {
			 if("FLAT".equalsIgnoreCase(surWtBreak))
			 {	
				 temp = validateFlatSurcharges(wtBkList,wtBreakDescList,wtBrkTypesList,rateTypeList,wtBreakMap, chrgHdrs, startIndex, endIndex);
				 remarks.append(temp);
				 
			 }else if("SLAB".equalsIgnoreCase(surWtBreak) || "BOTH".equalsIgnoreCase(surWtBreak) )
			 {
				 temp = validateSlabSurcharges(wtBkList,wtBreakDescList,wtBrkTypesList,rateTypeList,wtBreakMap, chrgHdrs, startIndex, endIndex);
				 remarks.append(temp);
				 
			 }else
				 remarks.append("Combination not allowed for FLAT/SLAB.\n");
			 
		 }
		 else if("LIST".equals(frgtWtBrk))
		 {
			 if((("FLAT".equalsIgnoreCase(surWtBreak)) && ("2".equals(shipmentMode) || "4".equals(shipmentMode))) || ("Pivot".equalsIgnoreCase(surWtBreak) && "1".equals(shipmentMode)))
			 {
				temp =  validateListSurcharges(wtBkList,wtBreakDescList,wtBrkTypesList,rateTypeList,wtBreakMap,tempWtBreakSet, chrgHdrs, startIndex, endIndex,shipmentMode); 
			 	remarks.append(temp);
			 }else
				 remarks.append("Combination not allowed for LIST.\n"); 
		 }
		 return remarks.toString();
	}
	public String validateFlatSurcharges(ArrayList surList,ArrayList surDesc,ArrayList wtBrkTypesList,ArrayList rateTypeList,HashMap tempWtBreakMap,String chrgHdrs[],int startIndex,int endIndex)throws Exception
	{
		StringBuffer remarks = new StringBuffer();
		int wtTypeIndex=0;
		String chargeCode =null;
		//String chargeBreak =null;
		for(int i=startIndex+3;i<endIndex;i++)
		{
			System.out.println("Surchar Values--->"+chrgHdrs[i]);
			 chargeCode = chrgHdrs[i].substring(0,5).toUpperCase();
			// chargeBreak = chrgHdrs[i].substring(5,chrgHdrs[i].length());
			 
			if(!tempWtBreakMap.containsKey(chargeCode))
         		remarks.append("Surcharge Id "+chrgHdrs[i]+" is Invalid.\n");
         	else{
         		surList.add(chargeCode+"~"+chrgHdrs[i]);
         		surDesc.add(tempWtBreakMap.get((chrgHdrs[i]).substring(0,5)));
         		if(i!= endIndex-1)
         			wtBrkTypesList.add(chrgHdrs[startIndex+1]+"~"+wtTypeIndex);
         		else
         			wtBrkTypesList.add(chrgHdrs[startIndex+1]+"~-1");
         		wtTypeIndex=wtTypeIndex+1;
         		rateTypeList.add(chrgHdrs[startIndex+2]);
         	}
		}
		return remarks.toString();
	}
	
	/**
	 * @param wtBrkList
	 * @param surDesc
	 * @param wtBrkTypesList
	 * @param rateTypeList
	 * @param tempWtBreakMap
	 * @param tempWtBreakSet
	 * @param chrgHdrs
	 * @param startIndex
	 * @param endIndex
	 * @param shipmentMode
	 * @return String,error Messages
	 * @throws Exception
	 */
	public String validateListSurcharges(ArrayList wtBrkList,ArrayList surDesc,ArrayList wtBrkTypesList,ArrayList rateTypeList,HashMap tempWtBreakMap,TreeSet tempWtBreakSet,String chrgHdrs[],int startIndex,int endIndex,String shipmentMode)throws Exception
	{
		StringBuffer remarks = new StringBuffer();
		int wtTypeIndex=0;
		String chargeCode =null;
		String chargeBreak =null;
		int surChargCodelength =0; 
		//for(int i=startIndex+3,j=8;i<endIndex;i++,j++)
			for(int i=startIndex+3,j=10;i<endIndex;i++,j++) // Added by Gowtham for Buyrate Upload Issue.
		{
			//System.out.println("Surchar Values--->"+chrgHdrs[i]);
			surChargCodelength = chrgHdrs[i].length();
			 //chargeCode = chrgHdrs[i].substring(3,chrgHdrs[i].length()).toUpperCase();
			 //chargeBreak = chrgHdrs[i].substring(0,3);
			 chargeCode = chrgHdrs[i].substring(surChargCodelength-5,surChargCodelength).toUpperCase();
			 chargeBreak = chrgHdrs[i].substring(0,surChargCodelength-5);
			 
			 if(i<endIndex-1 )
			 {
				 if(!tempWtBreakMap.containsKey(chargeCode))
					remarks.append("Surcharge Id "+chrgHdrs[i]+" is Invalid.\n");
				 
				 else if(!tempWtBreakSet.contains(chargeBreak))
				 {
					 remarks.append(" ContainersTypes : "+chrgHdrs[i]+" is Invalid.\n");
		         		
				 }else if(!chargeBreak.equals(chrgHdrs[j]))
				 {
					 remarks.append("Freight Weight Breaks and Surcharge Weight Breaks ["+chargeBreak+"] Should be same.\n");
				 }
						 
			 }else if(!"OVER-PIVOT RATE".equalsIgnoreCase(chrgHdrs[endIndex-1]) && "1".equals(shipmentMode))
	         {
				 remarks.append("Invalid weight break header(OVER-PIVOT RATE).\n");
	         }
			
			 if("OVER-PIVOT RATE".equalsIgnoreCase(chrgHdrs[i]) && "1".equals(shipmentMode))
			 {
				 surDesc.add(tempWtBreakMap.get((chrgHdrs[i-1]).substring(3,chrgHdrs[i-1].length())));
				 wtBrkList.add(chrgHdrs[i-1].substring(3,chrgHdrs[i-1].length()).toUpperCase()+"~overPivot");
			 }else{
				 surDesc.add(tempWtBreakMap.get(chargeCode));
				 //wtBrkList.add(chargeCode+"~"+chargeBreak);
				 wtBrkList.add(chargeBreak+chargeCode);
			 }
	     		
     		if(i!= endIndex-1)
     			wtBrkTypesList.add(chrgHdrs[startIndex+1]+"~"+wtTypeIndex);
     		else
     			wtBrkTypesList.add(chrgHdrs[startIndex+1]+"~-1");
     		wtTypeIndex=wtTypeIndex+1;
     		rateTypeList.add(chrgHdrs[startIndex+2]);
		}
		return remarks.toString();
	}
	
	public String validateListFrgtcharges(TreeSet tempWtBreakSet,String[] chrgHdrs,String shipmentMode,ArrayList breakList,int startIndex,int endIndex)throws Exception
	{
		StringBuffer remarks = new StringBuffer();
		for(int k=startIndex;k<endIndex;k++)
        {
			//System.out.println("Surchar Values--->"+chrgHdrs[k]);
			 if(!tempWtBreakSet.contains((chrgHdrs[k])) && (k<endIndex-1))
			 {
	         		remarks.append(" ContainersTypes : "+chrgHdrs[k]+" is Invalid.\n");
	         		
			 }else if(!"OVER-PIVOT RATE".equalsIgnoreCase(chrgHdrs[endIndex-1]) && "1".equals(shipmentMode))
	         {
				 remarks.append("Invalid weight break header(OVER-PIVOT RATE)\n");
				 
	         }
			 if("OVER-PIVOT RATE".equalsIgnoreCase(chrgHdrs[k]) && "1".equals(shipmentMode))
			 	 breakList.add("overPivot");
			 else
			 	 breakList.add(chrgHdrs[k]);
		}
		return remarks.toString();
	}
	
	/**
	 * @param surList
	 * @param surDesc
	 * @param wtBrkTypesList
	 * @param rateTypeList
	 * @param tempWtBreakMap
	 * @param chrgHdrs
	 * @param startIndex
	 * @param endIndex
	 * @return String,error Messages
	 * @throws Exception
	 */
	public String validateSlabSurcharges(ArrayList surList,ArrayList surDesc,ArrayList wtBrkTypesList,ArrayList rateTypeList,HashMap tempWtBreakMap,String chrgHdrs[],int startIndex,int endIndex)throws Exception
	{
		StringBuffer remarks = new StringBuffer();
		double parseDouble = 0;
		String chargeCode = null;
		String chargeBreak = null;
		double maxValue = 0d;
		int wtTypeIndex=0;
		for(int i=startIndex+3;i<endIndex;i++)
		{
			 chargeCode = chrgHdrs[i].substring(0,5).toUpperCase();
			 chargeBreak = chrgHdrs[i].substring(5,chrgHdrs[i].length());
			 
			if(!tempWtBreakMap.containsKey(chargeCode))
				remarks.append("Surcharge Id "+chrgHdrs[i]+" is Invalid.\n");
			 
			if(!"MIN".equalsIgnoreCase(chargeBreak) && startIndex+3==i)
			{
				remarks.append("Invalid Weight break values.[Min]\n");
				
			}else{
				if(startIndex+4==i)
				{	try
		            {
		              parseDouble = Double.parseDouble(chargeBreak);
		              maxValue = Math.abs(parseDouble);
		              if(parseDouble>0)
		              {
		                remarks.append("Invalid Weight break values(MIN,-X,X).\n");
		              }
					
		            }catch(NumberFormatException e)
		            {
		              e.printStackTrace();
		              remarks.append("Weight break value Not a number.\n");
		              break;
		            } 
				}else if(startIndex+5==i)
				{
					try
	                {
					  if(Double.parseDouble(chargeBreak) != Math.abs(parseDouble) )
		              {
		                 remarks.append("Invalid Weight breaks values(MIN,-X,X).\n");
		              }
	                 
	                }catch(NumberFormatException e)
	                {
	                  e.printStackTrace();
	                  remarks.append("Weight break value Not a number.\n");
	                  break;
	                }     
				}else if(i>startIndex+5)
				{
					try
		            {
		              parseDouble = Double.parseDouble(chargeBreak);
		              if(parseDouble<maxValue)
		              {
		                remarks.append("Weight Break Values should be more than the previous value.\n");
		              }else
			              maxValue = parseDouble;
					
		            }catch(NumberFormatException e)
		            {
		              e.printStackTrace();
		              remarks.append("Weight break value Not a number."+chargeBreak+"\n");
		              break;
		            }   
				}
			}
			surList.add(chargeCode+"~"+chargeBreak);
     		surDesc.add(tempWtBreakMap.get(chargeCode));
     		if(i!= endIndex-1)
     			wtBrkTypesList.add(chrgHdrs[startIndex+1]+"~"+wtTypeIndex);//Need to check this...
     		else
     			wtBrkTypesList.add(chrgHdrs[startIndex+1]+"~-1");
     		wtTypeIndex = wtTypeIndex+1;
     		rateTypeList.add(chrgHdrs[startIndex+2]);
		}
		return remarks.toString();
	}
	
	/**
	 * @param strToken
	 * @param weightBreak
	 * @param shipmentMode
	 * @param breakList
	 * @param startIndex
	 * @param endIndex
	 * @return String, error Messages
	 * @throws Exception
	 */
	public String validateFrightRates(String[] strToken,String weightBreak,String shipmentMode,ArrayList breakList,int startIndex,int endIndex)throws Exception
	{
		StringBuffer remarks =  new StringBuffer();
		double parseDouble	 = 0d;
	
		for(int k=startIndex;k<endIndex;k++)
        {
            breakList.add(strToken[k]);
            if(weightBreak!=null && !"".equals(weightBreak))
            {
            	// if(k==8)
            	if(k==10) // Added by Gowtham for Air Case Buyrate Upload.
                 {
                     if("FLAT".equalsIgnoreCase(weightBreak) || "SLAB".equalsIgnoreCase(weightBreak)|| "LIST".equalsIgnoreCase(weightBreak))
                     {                          
                         if(!"BASIC".equalsIgnoreCase(strToken[k]))
                         {
                           remarks.append("Invalid weight break header(BASIC)\n");
                         }
                     }
                 }
            	 //else if(k==9)
            	else if(k==11) // Added by Gowtham for Air Case Buyrate Upload.
                {
                    if("FLAT".equalsIgnoreCase(weightBreak) || "SLAB".equalsIgnoreCase(weightBreak))
                    {                          
                        if(!"MIN".equalsIgnoreCase(strToken[k]))
                        {
                          remarks.append("Invalid weight break header(MIN)\n");
                        }
                    }else if("LIST".equalsIgnoreCase(weightBreak) && "1".equalsIgnoreCase(shipmentMode))
                    {
                        if(!"OVER-PIVOT RATE".equalsIgnoreCase(strToken[k]))
                        {
                          remarks.append("Invalid weight break header(OVER-PIVOT RATE)\n");
                        }else
                        {
                          strToken[k] = "overPivot";
                        }
                    }
                    
                }
            	//else if(k==10)
            	else if(k==12)
                {
                  if("FLAT".equalsIgnoreCase(weightBreak))
                  {
                    if(!"FLAT".equalsIgnoreCase(strToken[k]))
                    {
                      remarks.append("Invalid weight break header(FLAT)\n");
                      
                    }
                  }else if("SLAB".equalsIgnoreCase(weightBreak))
                    {
                      try
                      {
                        parseDouble = Double.parseDouble(strToken[k]);
                        if(parseDouble>0)
                        {
                          remarks.append("Invalid Weight break values(MIN,-X,X)\n");
                        }
                      }catch(NumberFormatException e)
                      {
                        e.printStackTrace();
                        remarks.append("Weight break value Not a number\n");
                        break;
                      } 
                    }
                }
            	//else if(k==11)
            		else if(k==13)
                {
                  if("SLAB".equalsIgnoreCase(weightBreak))
                  {
                       try
                      {
                        if(Double.parseDouble(strToken[k]) != Math.abs(parseDouble) )
                        {
                          remarks.append("Invalid Weight breaks values(MIN,-X,X)\n");
                        }
                      }catch(NumberFormatException e)
                      {
                        e.printStackTrace();
                        remarks.append("Weight break value Not a number\n");
                        break;
                      }                            
                  }
                }
            }
        }
		return remarks.toString();
	}
	/**
	 * @param wtBreakList
	 * @param chargeRateList
	 * @param slabFlatList
	 * @param chargeRates
	 * @param surChargeType
	 * @param rateType
	 * @param startIndex
	 * @param endIndex
	 * @return String,error Messages
	 * @throws Exception
	 */
	public String validateFreightCharges(ArrayList wtBreakList,ArrayList chargeRateList,ArrayList slabFlatList,String[] chargeRates,String surChargeType,String rateType,int startIndex,int endIndex)throws Exception
	{
		StringBuffer remarks=new StringBuffer();
		double tempCharges = 0d;
		String strTempCharges = null;
		String bothIndicator = "";

		for(int k=startIndex;k<endIndex;k++)
	    {
			strTempCharges=chargeRates[k];
			if(strTempCharges!=null && !"".equals(strTempCharges))
			{
				  if("BOTH".equals(rateType))
				  {
					  if(k>startIndex+1)
					  {
						  if(strTempCharges!=null && !"NA".equalsIgnoreCase(strTempCharges) && strTempCharges.length()>1)
						  {
							  bothIndicator = strTempCharges.substring(strTempCharges.length()-1);
							  strTempCharges = strTempCharges.substring(0, strTempCharges.length()-1);
							  if(!((bothIndicator==null) || ("S".equalsIgnoreCase(bothIndicator))||("F".equalsIgnoreCase(bothIndicator))))
							  {
								  remarks.append("Charge indicator should be S(Slab) or F(Flat) only. \n");
							  }else if("F".equalsIgnoreCase(bothIndicator))
							  {
								  bothIndicator ="FLAT";
							  }else if("S".equalsIgnoreCase(bothIndicator))
							  {
								  bothIndicator ="SLAB";
							  }
						  }
					  }
				  }
				  
				  if("BASIC".equalsIgnoreCase((String)wtBreakList.get(0)) && "NA".equalsIgnoreCase(strTempCharges) && k==startIndex)
					  strTempCharges = "0";
				//	  strTempCharges = "NA";
				  else{
					  if(strTempCharges.length()>15)
							remarks.append("Max Length Must Be 15 ("+strTempCharges+"). \n");
					  else{
						  try{
							  tempCharges = Double.parseDouble(strTempCharges);
						  }catch(NumberFormatException e)
						  {
							  tempCharges = -1;
							  remarks.append("Charge Rate is not a Number."+chargeRates[k]+" \n");
						  }
					  }
				  }
			  	
			  	chargeRateList.add(strTempCharges);
			}else
			{
			  if(k==8)
			  {
				 remarks.append("The Charge Rate Should Not be Empty For BASIC.\n");
				 break;
				 
			  }else if(k==9)
			  {
				  remarks.append("The Charge Rate Should Not be Empty For MIN.\n");
			      break;
			      
			  }else if(k==10 && "FLAT".equalsIgnoreCase(surChargeType))
			  {
				   remarks.append("The Charge Rate Should Not be Empty For FLAT.\n");
			       break;
			       
			  }else if(k==10 && "SLAB".equalsIgnoreCase(surChargeType))
			  {
			      remarks.append("The Charge Rate Should Not be Empty for the slab rate "+wtBreakList.get(1)+"\n");
			      break;
			      
			  }else if(k==11 && "SLAB".equalsIgnoreCase(surChargeType))
			  {
			      remarks.append("The Charge Rate Should Not be Empty for the slab rate "+wtBreakList.get(2)+"\n");								  
			      break;
			      
			  }
			  chargeRateList.add(null);
		   }
			slabFlatList.add(bothIndicator);
	    }
		
		return remarks.toString();
	}
	/**
	 * @param wtBreakList
	 * @param chargeRateList
	 * @param slabFlatList
	 * @param chargeRates
	 * @param surChargeType
	 * @param rateType
	 * @param startIndex
	 * @param endIndex
	 * @return String,error Messages
	 * @throws Exception
	 */
	public String validateListFreightCharges(ArrayList wtBreakList,ArrayList chargeRateList,ArrayList slabFlatList,String[] chargeRates,String surChargeType,String rateType,int startIndex,int endIndex)throws Exception
	
	{
		StringBuffer remarks=new StringBuffer();
		double tempCharges = 0d;
		String strTempCharges = null;
		String bothIndicator = "";
		for(int k=startIndex;k<endIndex;k++)
	    {
			strTempCharges=chargeRates[k];
			if(strTempCharges!=null && !"".equals(strTempCharges))
			{
			  try{
				  
				  tempCharges = Double.parseDouble(strTempCharges);
				  
			  }catch(NumberFormatException e)
			  {
				  tempCharges = -1;
				  remarks.append("Charge Rate is Not a Number."+strTempCharges+" \n");
			  }
			}else{
					remarks.append("The Charge Rate Should Not be Empty. \n");								  
				}
			chargeRateList.add(strTempCharges);
			slabFlatList.add(bothIndicator);
		   }
		return remarks.toString();
	}
	/**
	 * @param chargeRateList
	 * @param wtBreakCurrList
	 * @param slabFlatList
	 * @param chrgHdrs
	 * @param startIndex
	 * @param endIndex
	 * @return String,error Messages
	 * @throws Exception
	 */
	public String validateSurcharges(ArrayList chargeRateList,ArrayList wtBreakCurrList1,ArrayList slabFlatList,String chrgHdrs[],int startIndex,int endIndex)throws Exception
	{
		StringBuffer remarks = new StringBuffer();
		double parseDouble = 0d;
		String surChargeType = chrgHdrs[startIndex+2];
		String chargeValue = null;
		String bothIndicator = "";

		for(int i=startIndex+3;i<endIndex;i++)
		{
			if(surChargeType!=null && (("FLAT".equals(surChargeType)) || ("LIST".equals(surChargeType))))
			{
				if(!"NA".equalsIgnoreCase(chrgHdrs[i]))
				{
					if(chrgHdrs[i].length()>15)
						remarks.append("Max Length Must Be 15 (Charge Rate ). \n");
					else{
						try{
							parseDouble = Double.parseDouble(chrgHdrs[i]);
						}catch(NumberFormatException e)
						{
							parseDouble	= -1;
							remarks.append("Charge Rate is not a Number."+chrgHdrs[i]+" \n");
						}
					}
				}
			}
			else if(surChargeType!=null && ("SLAB".equals(surChargeType) || "BOTH".equals(surChargeType)))
			{
				chargeValue = chrgHdrs[i] ;
				
				if("BOTH".equals(surChargeType))
				{
					if(chargeValue!=null && !"NA".equalsIgnoreCase(chargeValue) &&  chargeValue.length()>1 && i>startIndex+3)
					{
						bothIndicator = chargeValue.substring(chargeValue.length()-1);
						chargeValue = chargeValue.substring(0, chargeValue.length()-1);
						
						if(!((bothIndicator==null) || ("S".equalsIgnoreCase(bothIndicator))||("F".equalsIgnoreCase(bothIndicator))))
						{
							remarks.append("Charge indicator should be S(Slab) or F(Flat) only. \n");
						}else if("F".equalsIgnoreCase(bothIndicator)){
							  bothIndicator ="FLAT";
						}else if("S".equalsIgnoreCase(bothIndicator)){
							  bothIndicator ="SLAB";
						}
					}
				}
				
				if(!"NA".equalsIgnoreCase(chargeValue))
				{	
					if(chrgHdrs[i].length()>15)
						remarks.append("Max Length Must Be 15 (Charge Rate ).\n");
					else{
						try{
							parseDouble = Double.parseDouble(chargeValue);
						}catch(NumberFormatException e)
						{
							parseDouble	= -1;
							remarks.append("Charge Rate is not a Number."+chrgHdrs[i]+" \n");
						}
					}
					if("NA".equalsIgnoreCase(chrgHdrs[i+1]) && ((i+1)<endIndex))
					{
						remarks.append("Min,-X is mandatory for surcharge slab."+chrgHdrs[i]+" \n");
						
					}else if("NA".equalsIgnoreCase(chrgHdrs[i+2]) && ((i+2)<endIndex))
					{
						remarks.append("Min,-X,+X is mandatory for surcharge slab."+chrgHdrs[i]+" \n");
					}
				}else
				{
					if(!"NA".equalsIgnoreCase(chrgHdrs[i+1]) && ((i+2)<endIndex))
					{
						remarks.append("Min is mandatory for surcharge slab."+chrgHdrs[i]+" \n");
						
					}else if(!"NA".equalsIgnoreCase(chrgHdrs[i+2]) && ((i+2)<endIndex))
					{
						remarks.append("Min,-X,+X is mandatory for surcharge slab."+chrgHdrs[i]+" \n");
					}
				}
			}
			
			if("BOTH".equals(surChargeType))
			{
				chargeRateList.add(chargeValue);
			}else
			{
				chargeRateList.add(chrgHdrs[i]);
			}
			//wtBreakCurrList.add(chrgHdrs[startIndex]);
			wtBreakCurrList1.add(chrgHdrs[startIndex]); // Added by Gowtham for BuyRate Upload Issue.
			slabFlatList.add(bothIndicator);
		}
		return remarks.toString();
	}
	//End by Mohan
	
	 private boolean isInteger( String input )  
	 {  
	    try  
	    {  
	       Integer.parseInt( input );  
	       return true;  
	    }  
	    catch( NumberFormatException nfe )  
	    {  
	       return false;  
	    }  
	 }
	 private boolean isDouble( String input )  
	 {  
	    try  
	    {  
	       Double.parseDouble( input );  
	       return true;  
	    }  
	    catch( NumberFormatException nfe )  
	    {  
	       return false;  
	    }  
	 }

}