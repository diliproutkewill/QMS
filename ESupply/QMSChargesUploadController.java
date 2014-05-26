import com.foursoft.esupply.common.bean.ESupplyGlobalParameters;
import com.foursoft.esupply.common.exception.FoursoftException;
import com.foursoft.esupply.common.java.ErrorMessage;
import com.foursoft.esupply.common.java.KeyValue;
import com.foursoft.esupply.common.java.LookUpBean;
//import com.foursoft.esupply.common.util.Logger;
import org.apache.log4j.Logger;
import com.oreilly.servlet.multipart.FilePart;
import com.oreilly.servlet.multipart.MultipartParser;
import com.oreilly.servlet.multipart.ParamPart;
import com.oreilly.servlet.multipart.Part;
import com.qms.operations.charges.ejb.sls.ChargeMasterSession;
import com.qms.operations.charges.ejb.sls.ChargeMasterSessionHome;
import com.qms.operations.charges.java.BuychargesHDRDOB;
import com.qms.setup.ejb.sls.SetUpSession;
import com.qms.setup.ejb.sls.SetUpSessionHome;
import com.qms.setup.java.ChargeGroupingDOB;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.PrintWriter;
import java.io.IOException;

public class QMSChargesUploadController extends HttpServlet 
{
  private static final String CONTENT_TYPE = "text/html; charset=windows-1252";
  private static final String FILE_NAME    =  "QMSChargesUploadController.java";
 private static Logger logger = null;
  public void init(ServletConfig config) throws ServletException
  {
    super.init(config);
    logger  = Logger.getLogger(FILE_NAME);	
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
    String        operation             = request.getParameter("Operation");
    String        subOperation          = request.getParameter("subOperation");
    String        process               = request.getParameter("process");//@@Add/Modify
    String        errorMode             = request.getParameter("errorMode");
    String        nextNavigation        = null;
    ErrorMessage	errorMessageObject		= null;
    ArrayList			keyValueList			    = new ArrayList();
    String        errorMessage          = "";
    String        errorCode             = "";
    ArrayList     insertedList          = null;
    ArrayList     failedList            = null;
    String        successErrMsg         = "";
    String        failureErrMsg         = "";
    ArrayList     list                  = null;
    ArrayList     failureList           = null;
    HashMap       map                   = null;
    try
    {
      logger.info(FILE_NAME+"operation:"+request.getParameter("Operation"));
      logger.info(FILE_NAME+"subOperation:"+request.getParameter("subOperation"));
      logger.info(FILE_NAME+"process:"+process);
      logger.info(FILE_NAME+"errorMode::"+errorMode);
      
      if("Upload".equalsIgnoreCase(operation) && process==null)
      {
        nextNavigation  = "etrans/QMSChargesUploadIndex.jsp";
      }
      else if ("Upload".equalsIgnoreCase(operation) && "chargeDesc".equalsIgnoreCase(subOperation))
      {
        BuychargesHDRDOB  headerDOB             = null;
        list              = doChargeDescUploadProcess(request,response);
        if(list !=null)
        {
          failureList       = (ArrayList)list.get(0);
          map               = (HashMap)list.get(1);
        }
        if(map != null)
        {
          insertedList  = (ArrayList)map.get("NONEXISTS");
          failedList    = (ArrayList)map.get("EXISTS");
        }
        if("N".equalsIgnoreCase(errorMode))
        {
          if(insertedList!=null && insertedList.size()>0)
          {
            successErrMsg = "The Following Charge Description Id(s) Were Uploaded Successfully :\n";
            int insListSize	= insertedList.size();
            for(int i=0;i<insListSize;i++)
            {
              headerDOB = (BuychargesHDRDOB)insertedList.get(i);
              if(headerDOB!=null)
                  successErrMsg = successErrMsg + headerDOB.getChargeId() + " - "+ headerDOB.getChargeDescId()+"\n"; 
            }
          }
          if((failureList!=null && failureList.size()>0) || (failedList!=null && failedList.size()>0))
          {
              failureErrMsg = "The Following Charge Description Id(s) Were Not Uploaded Due to the Following Reasons :\n";       
              if(failureList!=null && failureList.size()>0)
              {
            	  int failListSize	=	 failureList.size();
                for(int i=0;i<failListSize;i++)
                {
                  headerDOB = (BuychargesHDRDOB)failureList.get(i);
                  if(headerDOB!=null)
                      failureErrMsg = failureErrMsg + headerDOB.getChargeDescId() + "--" + headerDOB.getChargeDesc()+"\n";
                }
              }
              if(failedList!=null && failedList.size()>0)
              {
            	  int failedListSize	=	failedList.size();
                for(int i=0;i<failedListSize;i++)
                {
                  headerDOB = (BuychargesHDRDOB)failedList.get(i);
                  if(headerDOB!=null)
                      failureErrMsg = failureErrMsg + headerDOB.getChargeDescId() + "--" + headerDOB.getChargeDesc()+"\n";
                }
              }
          }
            errorMessage  = successErrMsg + failureErrMsg;
            errorCode     = "MSG";
            errorMessageObject = new ErrorMessage(errorMessage,"QMSChargesUploadController?Operation="+operation+"&subOperation="+subOperation);
            keyValueList.add(new KeyValue("ErrorCode",errorCode)); 	
            keyValueList.add(new KeyValue("Operation",operation)); 	
            errorMessageObject.setKeyValueList(keyValueList);
            request.setAttribute("ErrorMessage",errorMessageObject); 
            nextNavigation = "ESupplyErrorPage.jsp";
        }
        else
        {
          StringBuffer  sb  =  doChargeDescExcelGen(list,process);
          if(sb.length()>0)
          {
            PrintWriter out = response.getWriter();
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Content-Disposition","attachment;filename=ChargeDescUpload.xls");
            out.print(sb.toString());
          }
        }
      }
      else if ("Upload".equalsIgnoreCase(operation) && "chargeGroup".equalsIgnoreCase(subOperation))
      {
        ChargeGroupingDOB chargeGroupDOB  =   null;
        list              = doChargeGroupUploadProcess(request,response);
        if(list !=null)
        {
          failureList       = (ArrayList)list.get(0);
          map               = (HashMap)list.get(1);
        }
        if(map != null)
        {
          insertedList  = (ArrayList)map.get("NONEXISTS");
          failedList    = (ArrayList)map.get("EXISTS");
        }
       if("N".equalsIgnoreCase(errorMode))
       {
          HashMap   checkMap    =     new HashMap();
          if(insertedList!=null && insertedList.size()>0)
          {
            successErrMsg = "The Following Charge Group Id(s) Were Uploaded Successfully :\n";
            int insListSize	=	insertedList.size();
            for(int i=0;i<insListSize;i++)
            {
              chargeGroupDOB = (ChargeGroupingDOB)insertedList.get(i);
              if(chargeGroupDOB!=null)
              {
                  if(!checkMap.containsKey(chargeGroupDOB.getChargeGroup()))
                  {
                    successErrMsg = successErrMsg + chargeGroupDOB.getChargeGroup() +"\n";
                    checkMap.put(chargeGroupDOB.getChargeGroup(),"");
                  }
              }
            }
         }
          if((failureList!=null && failureList.size()>0) || (failedList!=null && failedList.size()>0))
          {
              failureErrMsg = "The Following Charge Group Id(s) Were Not Uploaded Due to the Following Reasons :\n";
              if(failureList!=null && failureList.size()>0)
              {
            	  int failListSize	=	failureList.size();
                for(int i=0;i<failListSize;i++)
                {
                  chargeGroupDOB = (ChargeGroupingDOB)failureList.get(i);
                  if(chargeGroupDOB!=null && chargeGroupDOB.getRemarks()!=null)
                      failureErrMsg = failureErrMsg + chargeGroupDOB.getChargeGroup() + "(" + chargeGroupDOB.getChargeIds()+","+chargeGroupDOB.getChargeDescId()+") --"+chargeGroupDOB.getRemarks()+"\n";
                }
              }
              if(failedList!=null && failedList.size()>0)
              {
            	  int failedListSize	=	failedList.size();
                for(int i=0;i<failedListSize;i++)
                {
                  chargeGroupDOB = (ChargeGroupingDOB)failedList.get(i);
                  if(chargeGroupDOB!=null && chargeGroupDOB.getRemarks()!=null)
                      failureErrMsg = failureErrMsg + chargeGroupDOB.getChargeGroup() + "(" + chargeGroupDOB.getChargeIds()+","+chargeGroupDOB.getChargeDescId()+","+chargeGroupDOB.getOriginCountry()+","+chargeGroupDOB.getDestinationCountry()+") --"+chargeGroupDOB.getRemarks()+"\n";//Modified by Anil.k for CR 231214 on 25Jan2011
                }
              }    
          }
            errorMessage  = successErrMsg + failureErrMsg;
            errorCode     = "MSG";
            errorMessageObject = new ErrorMessage(errorMessage,"QMSChargesUploadController?Operation="+operation+"&subOperation="+subOperation);
            keyValueList.add(new KeyValue("ErrorCode",errorCode)); 	
            keyValueList.add(new KeyValue("Operation",operation)); 	
            errorMessageObject.setKeyValueList(keyValueList);
            request.setAttribute("ErrorMessage",errorMessageObject); 
            nextNavigation = "ESupplyErrorPage.jsp";
        }
        else
        {
          StringBuffer  sb  =  doChargeGroupExcelGen(list,process);
          if(sb.length()>0)
          {
            PrintWriter out = response.getWriter();
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Content-Disposition","attachment;filename=ChargeDescUpload.xls");
            out.print(sb.toString());
          }
        }
      }
    }
    catch(FoursoftException fs)
    {
      logger.error(FILE_NAME+"Error in Controller:"+fs);
      fs.printStackTrace();
      errorMessage = fs.getMessage();
      errorCode    = "ERR";
      errorMessageObject = new ErrorMessage(errorMessage,"QMSChargesUploadController?Operation="+operation+"&subOperation="+subOperation);
      keyValueList.add(new KeyValue("ErrorCode",errorCode)); 	
      keyValueList.add(new KeyValue("Operation",operation)); 	
      errorMessageObject.setKeyValueList(keyValueList);
      request.setAttribute("ErrorMessage",errorMessageObject); 
      nextNavigation = "ESupplyErrorPage.jsp";
    }
    catch(Exception e)
    {
      e.printStackTrace();
      logger.error(FILE_NAME+"Exception in doPost: "+e);
      errorMessage = "An Unexpected Error has Occurred in the Upload Process.Please Try Again.";
      errorCode    = "ERR";
      errorMessageObject = new ErrorMessage(errorMessage,"QMSChargesUploadController?Operation="+operation+"&subOperation="+subOperation);
      keyValueList.add(new KeyValue("ErrorCode",errorCode)); 	
      keyValueList.add(new KeyValue("Operation",operation)); 	
      errorMessageObject.setKeyValueList(keyValueList);
      request.setAttribute("ErrorMessage",errorMessageObject); 
      nextNavigation = "ESupplyErrorPage.jsp";
    }
    finally
    {
      try
      {
        if(nextNavigation!=null)
          doFileDispatch(request,response,nextNavigation);
      }
      catch(Exception e)
      {
        e.printStackTrace();
        logger.error(FILE_NAME+"Error while forwarding page "+e.toString());
      }
    }
  }
  private ArrayList doChargeDescUploadProcess(HttpServletRequest request,HttpServletResponse response) throws FoursoftException
  {
    ArrayList                 list        =   null;
    ArrayList                 successList =   null;
    ArrayList                 failureList =   null;
    HashMap                   map         =   null;
    ArrayList                 returnList  =   new ArrayList();
    ChargeMasterSessionHome   home        =   null;
    ChargeMasterSession       remote      =   null;
    String                    process     =   request.getParameter("process");
    HttpSession               session     =   request.getSession();
    ESupplyGlobalParameters   loginbean   =   (ESupplyGlobalParameters)session.getAttribute("loginbean");

    try
    {
       list         =   doGetChargeDescDetails(request,response);
       successList  =   (ArrayList)list.get(0);
       failureList  =   (ArrayList)list.get(1);
       
       if(successList.size()==0 && failureList.size()==0)
          throw new FoursoftException("No Data Found In the Uploaded File.\nPlease Ensure that the Format of the File being Uploaded Matches the Format of the Sample File.");
       else
       {
          home    =   (ChargeMasterSessionHome)LookUpBean.getEJBHome("ChargeMasterSession");
          remote  =   (ChargeMasterSession)home.create();
          map     =   remote.uploadChargeDescriptionDetails(successList,process,loginbean);
          returnList.add(0,failureList);
          returnList.add(1,map);
       }
    }
    catch(FoursoftException fs)
    {
      fs.printStackTrace();
      throw new FoursoftException(fs.getMessage(),fs);
    }
    catch(Exception e)
    {
      e.printStackTrace();
      logger.error(FILE_NAME+"Exception in doChargeDescUploadProcess "+e);
      throw new FoursoftException("An Error has Occurred While Uploading the Data. Please Try Again.",e);
    }
    return returnList;
  }
  private ArrayList doChargeGroupUploadProcess(HttpServletRequest request, HttpServletResponse response) throws FoursoftException
  {
    ArrayList                 list        =   null;
    ArrayList                 successList =   null;
    ArrayList                 failureList =   null;
    HashMap                   map         =   null;
    ArrayList                 returnList  =   new ArrayList();
    SetUpSessionHome          home        =   null;
    SetUpSession              remote      =   null;
    String                    process     =   request.getParameter("process");
    HttpSession               session     =   request.getSession();
    ESupplyGlobalParameters   loginbean   =   (ESupplyGlobalParameters)session.getAttribute("loginbean");
    
    try
    {
        list         =   doGetChargeGroupDetails(request,response);
        successList  =   (ArrayList)list.get(0);
        failureList  =   (ArrayList)list.get(1);
       
       if(successList.size()==0 && failureList.size()==0)
          throw new FoursoftException("No Data Found In the Uploaded File.\nPlease Ensure that the Format of the File being Uploaded Matches the Format of the Sample File.");
       else
       {
         home     =   (SetUpSessionHome)LookUpBean.getEJBHome("SetUpSessionBean");
         remote   =   (SetUpSession)home.create();
         map      =   remote.uploadChargeGroupDetails(successList,process,loginbean.getTerminalId());
         returnList.add(0,failureList);
         returnList.add(1,map);
       }
    }
    catch (FoursoftException fs)
    {
      throw new FoursoftException(fs.getMessage(),fs);
    }
    catch (Exception e)
    {
      e.printStackTrace();
      logger.error(FILE_NAME+"Exception in doChargeGroupUploadProcess "+e);
      throw new FoursoftException("An Error has Occurred While Uploading the Data. Please Try Again.",e);
    }
    return returnList;
  }
  /**
   * This Method is used to Fetch & Validate the Data provided in the Upload File.
   * The BuychargesHDRDOB objects are consolidated in a single ArrayList object which is used in the Callee Method to insert this data.
   * @throws com.foursoft.esupply.common.exception.FoursoftException
   * @return ArrayList
   * @param response
   * @param request
   */
  private ArrayList doGetChargeDescDetails(HttpServletRequest request, HttpServletResponse response) throws FoursoftException
  {
    ArrayList               list          =   new ArrayList();
    ArrayList               successList   =   new ArrayList();
    ArrayList               failureList   =   new ArrayList();
    BuychargesHDRDOB			  headerDOB	 	  =   null;
    HttpSession             session       =   request.getSession();
    ESupplyGlobalParameters loginbean     =   (ESupplyGlobalParameters)session.getAttribute("loginbean");
    MultipartParser		      mp            =   null;
    Part					          part          =   null;
    String                  process       =   request.getParameter("process");
    String				          name          =   null;
    String				          value         =   null;
    String				          operation     =   null;
    FilePart				        filePart      =   null;
    ParamPart				        paramPart     =   null;
    FileInputStream		      fileRead      =   null;
    String				          fileName      =   null;
    long					          size          =   0;
    BufferedReader		      br            =   null;
    String				          data          =   null;      
    String				          errorMode     =   null;
    String                  terminalId    =   null;
    String                  shipMode      =   null;
    int                     shipmentMode  =   0;
    String                  chargeId      =   null;
    String                  chargeDescId  =   null;
    String                  internalName  =   null;
    String                  externalName  =   null;
    int					            delimIndex    =   0;
    int					            beginIndex    =   0;
    String				          tempString    =   null;
    String				          subString     =   null;
    StringBuffer            remarks       =   null;
    StringTokenizer         strToken      =   null;
    StringTokenizer         sModeToken    =   null;
    HashMap                 checkMap      =   new HashMap();
    HashMap                 chargeDescMap =   new HashMap();
    
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
          
                if("errorMode".equalsIgnoreCase(name))
                     errorMode = value;
                if("Operation".equalsIgnoreCase(name))
                  operation = value;
            }
           else if (part.isFile())
           {
              filePart         =         (FilePart) part;
              fileName         =         filePart.getFileName();
              
              if (fileName != null)
              { 
                fileName = fileName.trim();
                size = filePart.writeTo(new File("./"));
              }
              else
              {                // the field did not contain a file
                throw new FoursoftException("Please specify a file.");
              }
          }//else if
        }//while
        
        fileRead            =        new FileInputStream(fileName);	 
        br                  =        new BufferedReader(new InputStreamReader(fileRead));
				br.readLine(); // Header--Will be Ignored
        data                =        br.readLine();
        int i =2;
        while(data !=null && data.trim().length()!=0)
        {
          data           +=        ",";
          tempString     =         "";
          beginIndex     =         0;
          
          for(int noOfCols=0;noOfCols<5; noOfCols++) 
          {
              delimIndex  =  data.indexOf(",");             
              
              if(delimIndex==0)
              {
                if(noOfCols==0 )
                  tempString=tempString+" ,";
                if(noOfCols==1)
                  tempString=tempString+" ,";
                if(noOfCols==2)
                  tempString=tempString+" ,";
                if(noOfCols==3 )
                  tempString=tempString + " ,";
                if(noOfCols==4 )
                  tempString=tempString + " ,";
              }
              else 
              {
                subString=data.substring(beginIndex,delimIndex);
                tempString=tempString + subString+",";
              } 
              data             =     data.substring(delimIndex+1);
              beginIndex       =     0;
          }
          data             =     tempString.substring(0,tempString.length());
          //Logger.info(FILE_NAME,"datadata::"+data);
          strToken         =     new StringTokenizer(data,",");
    
          while(strToken.hasMoreElements())
          {
              //headerDOB	 	  =   new BuychargesHDRDOB();
              remarks       =   new StringBuffer();
              
              if("Add".equalsIgnoreCase(process))
              {
                shipMode      =   strToken.nextToken();
                if(shipMode!=null && shipMode.trim().length()!=0)
                {
                  //Logger.info(FILE_NAME,"shipMode.indexOf::"+shipMode.indexOf("/"));
                  if(shipMode.indexOf("/")==-1)
                  {
                    if("Air".equalsIgnoreCase(shipMode))
                      shipmentMode  = 1;
                    else if ("Sea".equalsIgnoreCase(shipMode))
                      shipmentMode  = 2;
                    else if ("Truck".equalsIgnoreCase(shipMode))
                      shipmentMode  = 4;
                    else 
                      remarks.append("Shipment Mode Should Be Air or Sea or Truck.");
                  }
                  else
                  {
                    HashMap   shipMap     = new HashMap();
                    boolean   isUnique    = true;
                    boolean   isValid     = true;
                    String    shipModeStr = null;
                    int       air         = 0;
                    int       sea         = 0;
                    int       truck       = 0;
                    sModeToken  = new StringTokenizer(shipMode,"/");
                    int j =0;
                    while(sModeToken.hasMoreTokens())
                    {
                      shipModeStr   = sModeToken.nextToken();
                      if("Air".equalsIgnoreCase(shipModeStr) || "Sea".equalsIgnoreCase(shipModeStr) || "Truck".equalsIgnoreCase(shipModeStr))
                      {
                        if(!shipMap.containsKey(shipModeStr.toUpperCase()))
                          shipMap.put(shipModeStr.toUpperCase(),""+j);
                        else
                        {
                          isUnique  = false;
                          break;
                        }
                        j++;
                      }
                      else
                      {
                        isValid = false;
                        break;
                      }
                    }
                    
                    if(!isValid)
                        remarks.append("Shipment Mode Should Be Air and/or Sea and/or Truck.");
                    else
                    {
                      if(isUnique)
                      {
                          if(shipMap.containsKey("AIR"))
                            air = 1;
                          if(shipMap.containsKey("SEA"))
                            sea = 2;
                          if(shipMap.containsKey("TRUCK"))
                            truck = 4;
                          
                          shipmentMode  = air+sea+truck;
                      }
                      else
                      {
                        remarks.append("Please Enter Only Unique Values in Shipment Mode.");
                      }
                    }
                  }
                }
                else
                  remarks.append("Shipment Mode Cannot Be Empty.");
                  
                terminalId  = loginbean.getTerminalId();
              }
              else
              {
                  terminalId  = strToken.nextToken();
                  if(terminalId!=null && terminalId.trim().length()!=0)
                      terminalId  = terminalId.trim().toUpperCase();
                  else
                      terminalId  = loginbean.getTerminalId();
              }
             //headerDOB.setShipmentMode(shipmentMode);
             //Logger.info(FILE_NAME,"shipmentMode ::"+shipmentMode);
             //Logger.info(FILE_NAME,"remarks "+remarks);
             chargeId       =    strToken.nextToken();
             
             if(chargeId!=null && chargeId.trim().length()!=0)
                  chargeId  = chargeId.trim().toUpperCase();
             else
                  remarks.append("Charge Id Cannot Be Empty.");
             chargeDescId   =    strToken.nextToken();
             
             if(chargeDescId!=null && chargeDescId.trim().length()!=0)
             {
               if(!hasSpecialCharacters(chargeDescId,"chargeDescId"))
               {
                   chargeDescId = chargeDescId.trim().toUpperCase();
                   if("Add".equalsIgnoreCase(process) && chargeDescId.length()>50)
                      remarks.append("The Maximum Length Allowed For Charge Description Id is 50.");
                   if(chargeDescMap.containsKey(chargeDescId))
                      remarks.append("Duplicate Charge Description Id.");
                   else
                     chargeDescMap.put(chargeDescId,"");
               }
               else
                remarks.append("Special Characters Are Not Allowed in Charge Description Id.");
             }
             else
                remarks.append("Charge Description Id Cannot Be Empty.");
             
             internalName   =    strToken.nextToken();
             if(internalName!=null && internalName.trim().length()!=0)
             {
               if(hasSpecialCharacters(internalName,"chargeName"))
                 remarks.append("Special Characters ' and \" are not Allowed in Internal Name.");
               else
               {
                 if (internalName.length()>50)
                    remarks.append("The Maximum Length Allowed For Internal Name is 50.");
               }
             }
             else
                remarks.append("Internal Name Cannot Be Empty at Row No "+i+"\n");
             externalName   =    strToken.nextToken();
             if(externalName!=null && externalName.trim().length()!=0)
             {
               if(hasSpecialCharacters(externalName,"chargeName"))
                 remarks.append("Special Characters ' and \" are not Allowed in External Name.");
               else
               {
                 if (externalName.length()>50)
                    remarks.append("The Maximum Length Allowed For External Name is 50.");
               }
             }
             else
                remarks.append("External Name Cannot Be Empty.");
            
            headerDOB	 	  =   new BuychargesHDRDOB();
            headerDOB.setShipmentMode(shipmentMode);//@@For Add
            headerDOB.setTerminalId(terminalId);//@@For Modify
            headerDOB.setChargeId(chargeId);
            headerDOB.setChargeDescId(chargeDescId);
            headerDOB.setRemarks(internalName.trim());//@@Internal Name.
            headerDOB.setExternalChargeName(externalName.trim());
            headerDOB.setShipModeString(shipMode);
            if(remarks.length()>0)
            {
              headerDOB.setChargeDesc(remarks.toString());//@@Remarks to be displayed to the user.
              failureList.add(headerDOB);
            }
            else
            {
              headerDOB.setChargeDesc("");
              successList.add(headerDOB);
            }
          }
          data=br.readLine();
          if (data==null)
            break;
          //Logger.info(FILE_NAME,"datadata::"+data);
          i++;
        }
        list.add(0,successList);
        list.add(1,failureList);
    }
    catch (FoursoftException fs)
    {
      throw new FoursoftException(fs.getMessage(),fs);    
    }
    catch(Exception e)
    {
      e.printStackTrace();
      logger.error(FILE_NAME+"Exception in doGetChargeDescDetails:"+e);
      throw new FoursoftException("An Error Has Occurred While Fetching the Data from the Uploaded File. \nPlease Ensure that the Format of the File being Uploaded Matches the Format of the Sample File.",e);
    }
    return list;
  }
  private ArrayList doGetChargeGroupDetails(HttpServletRequest request, HttpServletResponse response) throws FoursoftException
  {
    ArrayList               list          =   new ArrayList();
    ArrayList               successList   =   new ArrayList();
    ArrayList               failureList   =   new ArrayList();
    HttpSession             session       =   request.getSession();
    ESupplyGlobalParameters loginbean     =   (ESupplyGlobalParameters)session.getAttribute("loginbean");
    MultipartParser		      mp            =   null;
    Part					          part          =   null;
    String                  process       =   request.getParameter("process");
    String				          name          =   null;
    String				          value         =   null;
    String				          operation     =   null;
    FilePart				        filePart      =   null;
    ParamPart				        paramPart     =   null;
    FileInputStream		      fileRead      =   null;
    String				          fileName      =   null;
    long					          size          =   0;
    BufferedReader		      br            =   null;
    String				          data          =   null;      
    String				          errorMode     =   null;
    String                  terminalId    =   null;
    String                  shipMode      =   null;
    int                     shipmentMode  =   0;
    String                  chargeGroupId =   null;
    String                  chargeId      =   null;
    String                  chargeDescId  =   null;
    String					originCountry =	  null;//Added by Anil.k for Enhancement 231214 on 25Jan2011
    String					destCountry	  =	  null;//Added by Anil.k for Enhancement 231214 on 25Jan2011
    int					            delimIndex    =   0;
    int					            beginIndex    =   0;
    String				          tempString    =   null;
    String				          subString     =   null;
    StringBuffer            remarks       =   null;
    StringTokenizer         strToken      =   null;
    StringTokenizer         sModeToken    =   null;
    HashMap                 chargeMap     =   new HashMap();
    HashMap                 chargeGrpMap  =   new HashMap();
    HashMap                 failedMultiMap=   new HashMap();
    HashMap                 succesMultiMap=   new HashMap();
    
    ChargeGroupingDOB       chargeGroupDOB=   null;
    
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
        
              if("errorMode".equalsIgnoreCase(name))
                   errorMode = value;
              if("Operation".equalsIgnoreCase(name))
                operation = value;
          }
         else if (part.isFile())
         {
            filePart         =         (FilePart) part;
            fileName         =         filePart.getFileName();
            
            if (fileName != null)
            { 
              fileName = fileName.trim();
              size = filePart.writeTo(new File("./"));
            }
            else
            {                // the field did not contain a file
              throw new FoursoftException("Please specify a file.");
            }
        }//else if
      }//while
        
        fileRead            =        new FileInputStream(fileName);	 
        br                  =        new BufferedReader(new InputStreamReader(fileRead));
				br.readLine(); // Header--Will be Ignored
        data                =        br.readLine();
        while(data !=null && data.trim().length()!=0)
        {
          data           +=        ",";
          tempString     =         "";
          beginIndex     =         0;
          for(int noOfCols=0;noOfCols<6; noOfCols++)//Modified by Anil.k for Enhancement 231214 on 25Jan2011
          {
              delimIndex  =  data.indexOf(",");
              if(delimIndex==0)
              {
                if(noOfCols==0 )
                  tempString=tempString+" ,";
                if(noOfCols==1)
                  tempString=tempString+" ,";
                if(noOfCols==2)
                  tempString=tempString+" ,";
                if(noOfCols==3 )
                  tempString=tempString + " ,";
              }
              else 
              {
                subString=data.substring(beginIndex,delimIndex);
                tempString=tempString + subString+",";
              } 
              data             =     data.substring(delimIndex+1);
              beginIndex       =     0;
          }
          data             =     tempString.substring(0,tempString.length());
          //Logger.info(FILE_NAME,"datadata::"+data);
          strToken         =     new StringTokenizer(data,",");
          while(strToken.hasMoreElements())
          {
            remarks       =   new StringBuffer();
            chargeGroupId =   strToken.nextToken();
            if(chargeGroupId!=null && chargeGroupId.trim().length()!=0)
            {
              chargeGroupId = chargeGroupId.trim().toUpperCase();
              if(!hasSpecialCharacters(chargeGroupId,""))
              {
                if("Add".equalsIgnoreCase(process) && chargeGroupId.length()>40)
                  remarks.append("The Max Length Allowed For Charge Group Id is 40 Characters.");
              }
              else
              {
                remarks.append("Special Characters Not Allowed in Charge Group Id.");
              }
            }
            else
            {
              remarks.append("Charge Group Id Cannot Be Empty.");
            }
            if("Add".equalsIgnoreCase(process))
            {
              shipMode      =   strToken.nextToken();
              if(shipMode!=null && shipMode.trim().length()!=0)
              {
                //Logger.info(FILE_NAME,"shipMode.indexOf::"+shipMode.indexOf("/"));
                if(shipMode.indexOf("/")==-1)
                {
                  if("Air".equalsIgnoreCase(shipMode))
                    shipmentMode  = 1;
                  else if ("Sea".equalsIgnoreCase(shipMode))
                    shipmentMode  = 2;
                  else if ("Truck".equalsIgnoreCase(shipMode))
                    shipmentMode  = 4;
                  else 
                    remarks.append("Shipment Mode Should Be Air or Sea or Truck.");
                  
                  if(shipmentMode!=0)
                  {
                    if(chargeGrpMap.containsKey(chargeGroupId))
                    {
                      if(!((String)chargeGrpMap.get(chargeGroupId)).equalsIgnoreCase(""+shipmentMode))
                          remarks.append("For a Charge Group Id, there can be only one Shipment Mode.");
                    }
                    else
                      chargeGrpMap.put(chargeGroupId,""+shipmentMode);
                  }
                }
                else
                {
                  HashMap   shipMap     = new HashMap();
                  boolean   isUnique    = true;
                  boolean   isValid     = true;
                  String    shipModeStr = null;
                  int       air         = 0;
                  int       sea         = 0;
                  int       truck       = 0;
                  sModeToken  = new StringTokenizer(shipMode,"/");
                  int j =0;
                  while(sModeToken.hasMoreTokens())
                  {
                    shipModeStr   = sModeToken.nextToken();
                    if("Air".equalsIgnoreCase(shipModeStr) || "Sea".equalsIgnoreCase(shipModeStr) || "Truck".equalsIgnoreCase(shipModeStr))
                    {
                      if(!shipMap.containsKey(shipModeStr.toUpperCase()))
                        shipMap.put(shipModeStr.toUpperCase(),""+j);
                      else
                      {
                        isUnique  = false;
                        break;
                      }
                      j++;
                    }
                    else
                    {
                      isValid = false;
                      break;
                    }
                  }
                  
                  if(!isValid)
                      remarks.append("Shipment Mode Should Be Air and/or Sea and/or Truck.");
                  else
                  {
                    if(isUnique)
                    {                         
                        if(shipMap.containsKey("AIR"))
                          air = 1;
                        if(shipMap.containsKey("SEA"))
                          sea = 2;
                        if(shipMap.containsKey("TRUCK"))
                          truck = 4;
                        
                        shipmentMode  = air+sea+truck;
                        
                        if(chargeGrpMap.containsKey(chargeGroupId))
                        {
                          if(!((String)chargeGrpMap.get(chargeGroupId)).equalsIgnoreCase(""+shipmentMode))
                              remarks.append("For a Charge Group Id, there can be only one Shipment Mode.");
                        }
                        else
                          chargeGrpMap.put(chargeGroupId,""+shipmentMode);
                    }
                    else
                    {
                      remarks.append("Please Enter Only Unique Values in Shipment Mode.");
                    }
                  }
                }
              }
              else
                remarks.append("Shipment Mode Cannot Be Empty.");
                
              terminalId  = loginbean.getTerminalId();
            }
            else
            {
                terminalId  = strToken.nextToken();
                if(terminalId!=null && terminalId.trim().length()!=0)
                    terminalId  = terminalId.trim().toUpperCase();
                else
                    terminalId  = loginbean.getTerminalId();
            }
            
            chargeId       =    strToken.nextToken();
             
             if(chargeId!=null && chargeId.trim().length()!=0)
                  chargeId  = chargeId.trim().toUpperCase();
             else
                  remarks.append("Charge Id Cannot Be Empty.");
             
             chargeDescId   =    strToken.nextToken();
             
             if(chargeDescId!=null && chargeDescId.trim().length()!=0)
             {
               chargeDescId = chargeDescId.trim().toUpperCase();
               if(chargeMap.containsKey(chargeGroupId+chargeId+chargeDescId))
                  remarks.append("This Charge Id & Charge Description Id Combination is a Duplicate Record for this Charge Group Id.");
               else
                 chargeMap.put(chargeGroupId+chargeId+chargeDescId,"");
             }
             else
             {
               remarks.append("Charge Description Id Cannot Be Empty.");
             }
             originCountry = strToken.nextToken();//Added by Anil.k for Enhancement 231214 on 25Jan2011
             destCountry   = strToken.nextToken();//Added by Anil.k for Enhancement 231214 on 25Jan2011
             chargeGroupDOB   =   new ChargeGroupingDOB();
             chargeGroupDOB.setChargeGroup(chargeGroupId);
             chargeGroupDOB.setShipmentMode(shipmentMode);
             chargeGroupDOB.setChargeIds(chargeId);
             chargeGroupDOB.setChargeDescId(chargeDescId);
             chargeGroupDOB.setTerminalId(terminalId);
             chargeGroupDOB.setShipModeString(shipMode);
             chargeGroupDOB.setOriginCountry(originCountry);//Added by Anil.k for Enhancement 231214 on 25Jan2011
             chargeGroupDOB.setDestinationCountry(destCountry);//Added by Anil.k for Enhancement 231214 on 25Jan2011
             
            if(remarks.length()>0)
            {
              chargeGroupDOB.setRemarks(remarks.toString());//@@Remarks to be displayed to the user.
              ArrayList  values = (ArrayList)failedMultiMap.get(chargeGroupDOB.getChargeGroup());
              if(values==null)
                  failedMultiMap.put(chargeGroupDOB.getChargeGroup(),values=new ArrayList());
              values.add(chargeGroupDOB);
            }
            else
            {
              chargeGroupDOB.setRemarks("");
              ArrayList  values = (ArrayList)succesMultiMap.get(chargeGroupDOB.getChargeGroup());
              if(values==null)
                  succesMultiMap.put(chargeGroupDOB.getChargeGroup(),values=new ArrayList());
              values.add(chargeGroupDOB);
             // successList.add(chargeGroupDOB);
            }
          }//@@Inner While
          data=br.readLine();
          if (data==null)
            break;
          //Logger.info(FILE_NAME,"datadata::"+data);
        }//@@Outer While
        
        ArrayList successValues =   null;
        ArrayList failedValues  =   null;
        Set       keys          =   succesMultiMap.keySet();
        Iterator  it            =   keys.iterator();
        String    chargeGroup   =   null;
        int       sucessSize;
        int       failedSize;
        while(it.hasNext())
        {
          chargeGroup   = (String)it.next();
          sucessSize    = 0;
          failedSize    = 0;
          if(failedMultiMap.containsKey(chargeGroup))
          {
              successValues  =   (ArrayList)succesMultiMap.get(chargeGroup);
              sucessSize     =   successValues.size();
              for(int i=0;i<sucessSize;i++)
                  failureList.add((ChargeGroupingDOB)successValues.get(i));
              
              failedValues   =   (ArrayList)failedMultiMap.get(chargeGroup);
              failedSize     =   failedValues.size();
              for(int i=0;i<failedSize;i++)
                  failureList.add((ChargeGroupingDOB)failedValues.get(i));
          }
          else
          {
              successValues  =   (ArrayList)succesMultiMap.get(chargeGroup);
              sucessSize     =   successValues.size();
              for(int i=0;i<sucessSize;i++)
                  successList.add((ChargeGroupingDOB)successValues.get(i));
          }              
        }
        
        keys          =   failedMultiMap.keySet();
        it            =   keys.iterator();
        
        while(it.hasNext())
        {
          chargeGroup   = (String)it.next();
          if(!succesMultiMap.containsKey(chargeGroup))
          {
             failedValues   =   (ArrayList)failedMultiMap.get(chargeGroup);
             failedSize     =   failedValues.size();
              for(int i=0;i<failedSize;i++)
                  failureList.add((ChargeGroupingDOB)failedValues.get(i));
          }
        }
        
        list.add(0,successList);
        list.add(1,failureList);
    }
    catch (FoursoftException fs)
    {
      throw new FoursoftException(fs.getMessage(),fs);
    }
    catch(Exception e)
    {
      e.printStackTrace();
      logger.error(FILE_NAME+"Exception in doGetChargeGroupDetails:"+e);
      throw new FoursoftException("An Error Has Occurred While Fetching the Data from the Uploaded File. \nPlease Ensure that the Format of the File being Uploaded Matches the Format of the Sample File.",e);
    }
    return list;
  }
  private boolean hasSpecialCharacters(String input,String fieldName) throws FoursoftException
  {
    String    filteredValues = "";
    boolean   returnFlag     = false;
    char      checkChar;
    try
    {
      if(fieldName.equalsIgnoreCase("chargeName"))
          filteredValues  = "''\"";
      else
          filteredValues  = "''~!@#$%^&*()+=|\\:;<>,./?\"";
          
      for (int i=0;i<input.trim().length();i++)
      {
          checkChar = input.charAt(i);
          if(filteredValues.indexOf(""+checkChar)!=-1)
          {
              returnFlag  = true;
              break;
          }
      }
    }
    catch(Exception e)
    {
      e.printStackTrace();
      logger.error(FILE_NAME+"Exception in hasSpecialCharacters:"+e);
      throw new FoursoftException("An Exception Has Occured While Validating Data. Please try Again.",e);
    }
    return returnFlag;
  }
  private StringBuffer doChargeDescExcelGen(ArrayList list,String process) throws FoursoftException
  {
    StringBuffer      sb             = new StringBuffer();
    ArrayList         failureList    = null;
    HashMap           map            = null;
    ArrayList         insertedList   = null;
    ArrayList         failedList     = null;
    BuychargesHDRDOB  headerDOB      = null;
    String            successErrMsg  = "";
    String            failureErrMsg  = "";
    try
    {
        failureList     =   (ArrayList)list.get(0);
        map             =   (HashMap)list.get(1);
        if(map != null)
        {
          insertedList  = (ArrayList)map.get("NONEXISTS");
          failedList    = (ArrayList)map.get("EXISTS");
        }
        sb.append("<html>");
        sb.append("<body>");
        sb.append("<table width='100%' border='1' cellspacing='0' cellpadding='0'>");
        if(insertedList!=null && insertedList.size()>0)
        {
            sb.append("<tr>");
            sb.append("<td colspan='5'><b>The Following Records Were Successfully Uploaded:</b></td>");
            sb.append("</tr><tr>");
            sb.append("<td><b>").append("Add".equalsIgnoreCase(process)?"Shipment Mode":"Terminal Id").append("</b></td>");
            sb.append("<td><b>Charge Id</b></td>");
            sb.append("<td><b>Charge Description ID</b></td>");
            sb.append("<td><b>Internal Charge Name</b></td>");
            sb.append("<td><b>External Charge Name</b></td>");
            sb.append("</tr>");
            int insListSize	=	insertedList.size();
          for(int i=0;i<insListSize;i++)
          {
            headerDOB = (BuychargesHDRDOB)insertedList.get(i);
            if(headerDOB!=null)
            {
                sb.append("<tr>");
                sb.append("<td>").append("Add".equalsIgnoreCase(process)?headerDOB.getShipModeString():headerDOB.getTerminalId()).append("</td>");
                sb.append("<td>").append(headerDOB.getChargeId()).append("</td>");
                sb.append("<td>").append(headerDOB.getChargeDescId()).append("</td>");
                sb.append("<td>").append(headerDOB.getRemarks()).append("</td>");
                sb.append("<td>").append(headerDOB.getExternalChargeName()).append("</td>");
                sb.append("</tr>");
            }
          }
        }
        if((failureList!=null && failureList.size()>0) || (failedList!=null && failedList.size()>0))
        {
            sb.append("<tr>");
            sb.append("<td colspan='6'><b>The Following Charge Description Id(s) Were Not Uploaded: (Please Refer to the Remarks Column for Details).<b></td>");
            sb.append("</tr><tr>");
            sb.append("<td><b>").append("Add".equalsIgnoreCase(process)?"Shipment Mode":"Terminal Id").append("</b></td>");
            sb.append("<td><b>Charge Id</b></td>");
            sb.append("<td><b>Charge Description ID</b></td>");
            sb.append("<td><b>Internal Charge Name</b></td>");
            sb.append("<td><b>External Charge Name</b></td>");
            sb.append("<td><b>Remarks</b></td>");
            sb.append("</tr>");   
        }
        if(failureList!=null && failureList.size()>0)
        {
        	int failListSize	=	failureList.size();
            for(int i=0;i<failListSize;i++)
            {
              headerDOB = (BuychargesHDRDOB)failureList.get(i);
              if(headerDOB!=null)
              {
                  sb.append("<tr>");
                  sb.append("<td>").append("Add".equalsIgnoreCase(process)?headerDOB.getShipModeString():headerDOB.getTerminalId()).append("</td>");
                  sb.append("<td>").append(headerDOB.getChargeId()).append("</td>");
                  sb.append("<td>").append(headerDOB.getChargeDescId()).append("</td>");
                  sb.append("<td>").append(headerDOB.getRemarks()).append("</td>");
                  sb.append("<td>").append(headerDOB.getExternalChargeName()).append("</td>");
                  sb.append("<td nowrap>").append(headerDOB.getChargeDesc()).append("</td>");
                  sb.append("</tr>");
              }
            }
        }
        if(failedList!=null && failedList.size()>0)
        {
        	int faildListSize	=	failedList.size();
            for(int i=0;i<faildListSize;i++)
            {
              headerDOB = (BuychargesHDRDOB)failedList.get(i);
              if(headerDOB!=null)
              {
                  sb.append("<tr>");
                  sb.append("<td>").append("Add".equalsIgnoreCase(process)?headerDOB.getShipModeString():headerDOB.getTerminalId()).append("</td>");
                  sb.append("<td>").append(headerDOB.getChargeId()).append("</td>");
                  sb.append("<td>").append(headerDOB.getChargeDescId()).append("</td>");
                  sb.append("<td>").append(headerDOB.getRemarks()).append("</td>");
                  sb.append("<td>").append(headerDOB.getExternalChargeName()).append("</td>");
                  sb.append("<td nowrap>").append(headerDOB.getChargeDesc()).append("</td>");
                  sb.append("</tr>");
              }
            }
        }
    }
    catch(Exception e)
    {
      e.printStackTrace();
      logger.error(FILE_NAME+"Exception in doChargeDescExcelGen "+e);
      throw new FoursoftException("An Error Has Occurred While Generating Error File. Please Try Again.",e);
    }
    return sb;
  }
  private StringBuffer doChargeGroupExcelGen (ArrayList list,String process) throws FoursoftException
  {
    StringBuffer      sb             = new StringBuffer();
    ArrayList         failureList    = null;
    HashMap           map            = null;
    ArrayList         insertedList   = null;
    ArrayList         failedList     = null;
    ChargeGroupingDOB chargeGroupDOB = null;
    String            successErrMsg  = "";
    String            failureErrMsg  = "";
    try
    {
        failureList     =   (ArrayList)list.get(0);
        map             =   (HashMap)list.get(1);
        if(map != null)
        {
          insertedList  = (ArrayList)map.get("NONEXISTS");
          failedList    = (ArrayList)map.get("EXISTS");
        }
        sb.append("<html>");
        sb.append("<body>");
        sb.append("<table width='100%' border='1' cellspacing='0' cellpadding='0'>");
        if(insertedList!=null && insertedList.size()>0)
        {
            sb.append("<tr>");
            sb.append("<td colspan='4'><b>The Following Records Were Successfully Uploaded:</b></td>");
            sb.append("</tr><tr>");
            sb.append("<td><b>Charge Group Id</b></td>");
            sb.append("<td><b>").append("Add".equalsIgnoreCase(process)?"Shipment Mode":"Terminal Id").append("</b></td>");
            sb.append("<td><b>Charge Id</b></td>");
            sb.append("<td><b>Charge Description ID</b></td>");
            sb.append("</tr>");
            int insListSize	=	insertedList.size();
            for(int i=0;i<insListSize;i++)
            {
              chargeGroupDOB = (ChargeGroupingDOB)insertedList.get(i);
              if(chargeGroupDOB!=null)
              {
                  sb.append("<tr>");
                  sb.append("<td>").append(chargeGroupDOB.getChargeGroup()).append("</td>");
                  sb.append("<td>").append("Add".equalsIgnoreCase(process)?chargeGroupDOB.getShipModeString():chargeGroupDOB.getTerminalId()).append("</td>");
                  sb.append("<td>").append(chargeGroupDOB.getChargeIds()).append("</td>");
                  sb.append("<td>").append(chargeGroupDOB.getChargeDescId()).append("</td>");
                  sb.append("</tr>");
              }
            }
        }
        if((failureList!=null && failureList.size()>0) || (failedList!=null && failedList.size()>0))
        {
            sb.append("<tr>");
            sb.append("<td colspan='5'><b>The Following Charge Description Id(s) Were Not Uploaded: (Please Refer to the Remarks Column for Details).<b></td>");
            sb.append("</tr><tr>");
            sb.append("<td><b>Charge Group Id</b></td>");
            sb.append("<td><b>").append("Add".equalsIgnoreCase(process)?"Shipment Mode":"Terminal Id").append("</b></td>");
            sb.append("<td><b>Charge Id</b></td>");
            sb.append("<td><b>Charge Description ID</b></td>");
            sb.append("<td><b>Remarks</b></td>");
            sb.append("</tr>");
        }
        if(failureList != null && failureList.size()>0)
        {
        	int failureListSize	=	failureList.size();
            for(int i=0;i<failureListSize;i++)
            {
              chargeGroupDOB = (ChargeGroupingDOB)failureList.get(i);
              if(chargeGroupDOB!=null)
              {
                  sb.append("<tr>");
                  sb.append("<td>").append(chargeGroupDOB.getChargeGroup()).append("</td>");
                  sb.append("<td>").append("Add".equalsIgnoreCase(process)?chargeGroupDOB.getShipModeString():chargeGroupDOB.getTerminalId()).append("</td>");
                  sb.append("<td>").append(chargeGroupDOB.getChargeIds()).append("</td>");
                  sb.append("<td>").append(chargeGroupDOB.getChargeDescId()).append("</td>");
                  sb.append("<td>").append(chargeGroupDOB.getRemarks()!=null?chargeGroupDOB.getRemarks():"").append("</td>");
                  sb.append("</tr>");
              }
            }
        }
        if(failedList != null && failedList.size() >0)
        {
        	int faileListSize	=	failedList.size();
            for(int i=0;i<faileListSize;i++)
            {
              chargeGroupDOB = (ChargeGroupingDOB)failedList.get(i);
              if(chargeGroupDOB!=null)
              {
                  sb.append("<tr>");
                  sb.append("<td>").append(chargeGroupDOB.getChargeGroup()).append("</td>");
                  sb.append("<td>").append("Add".equalsIgnoreCase(process)?chargeGroupDOB.getShipModeString():chargeGroupDOB.getTerminalId()).append("</td>");
                  sb.append("<td>").append(chargeGroupDOB.getChargeIds()).append("</td>");
                  sb.append("<td>").append(chargeGroupDOB.getChargeDescId()).append("</td>");
                  sb.append("<td nowrap>").append(chargeGroupDOB.getRemarks()!=null?chargeGroupDOB.getRemarks():"").append("</td>");
                  sb.append("</tr>");
              }
            }
        }
    }
    catch (Exception e)
    {
      e.printStackTrace();
      logger.error(FILE_NAME+"Exception in doChargeGroupExcelGen "+e);
      throw new FoursoftException("An Error Has Occurred While Generating Error File in Excel Format. Please Try Again.",e);
    }
    return sb;
  }
  private void doFileDispatch(HttpServletRequest request, HttpServletResponse response, String forwardFile) throws IOException, ServletException
  {
		try
		{
			RequestDispatcher rd = request.getRequestDispatcher(forwardFile);
			rd.forward(request, response);
		    
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			logger.error(FILE_NAME+ " [doDispatcher() "+ " Exception in forwarding to "+forwardFile+ ex);
		}
   }
}