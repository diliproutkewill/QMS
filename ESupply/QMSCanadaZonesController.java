import com.foursoft.esupply.common.exception.FoursoftException;
import com.foursoft.esupply.common.java.ErrorMessage;
import com.foursoft.esupply.common.java.KeyValue;
import com.foursoft.esupply.common.java.LookUpBean;
import com.oreilly.servlet.multipart.*;
import com.qms.setup.ejb.sls.QMSSetUpSession;
import com.qms.setup.ejb.sls.QMSSetUpSessionHome;
import com.qms.setup.java.ZoneCodeChildDOB;
import com.qms.setup.java.ZoneCodeMasterDOB;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.HashMap;
import java.util.StringTokenizer;
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.PrintWriter;
import java.io.IOException;
import org.apache.log4j.Logger;

public class QMSCanadaZonesController extends HttpServlet 
{
  private static final String CONTENT_TYPE = "text/html; charset=windows-1252";
  private static final String FILE_NAME    =  "QMSCanadaZonesController.java";
  private static Logger logger = null;

  public void init(ServletConfig config) throws ServletException
  {
    super.init(config);
    logger  = Logger.getLogger(FILE_NAME);
  }

  /**
   * Process the HTTP doGet request.
   * @throws java.io.IOException
   * @throws javax.servlet.ServletException
   * @param response
   * @param request
   */
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
  {
    doPost(request,response);
  }

  /**
   * Process the HTTP doPost request.
   * @throws java.io.IOException
   * @throws javax.servlet.ServletException
   * @param response
   * @param request
   */
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
  {
    String        operation             = request.getParameter("Operation");
    String        subOperation          = request.getParameter("subOperation");
    String        errorMode             = request.getParameter("errorMode");
    String        nextNavigation        = null;
    ErrorMessage	errorMessageObject		= null;
    ArrayList			keyValueList			    = new ArrayList();
    ArrayList     list                  = null;
    String        errorMessage          = "";
    String        errorCode             = "";
    ArrayList     failureList           = null;
    ArrayList     insertedList          = null;
    ArrayList     failedList            = null;
    String        successErrMsg         = "";
    String        failureErrMsg         = "";
    HashMap       map                   = null;
    ZoneCodeMasterDOB masterDOB         = null;
    ArrayList     childList             = null;
    ZoneCodeChildDOB  childDOB          = null;
    
    try
    {
 
      
      if("Upload".equalsIgnoreCase(operation) && subOperation==null)
      {
        nextNavigation  = "QMSCanadaZonesUploadIndex.jsp";
      }
      else if("Upload".equalsIgnoreCase(operation) && "process".equalsIgnoreCase(subOperation))
      {
          list       =   doCanadaZoneCodeUploadProcess(request,response);
          if(list !=null)
          {
            failureList       = (ArrayList)list.get(0);
            map               = (HashMap)list.get(1);
          }
          if(map != null)
          {
            insertedList  = (ArrayList)map.get("EXISTS");
            failedList    = (ArrayList)map.get("NONEXISTS");
          }
          if("N".equalsIgnoreCase(errorMode))
          {
              if(insertedList!=null && insertedList.size()>0)
              {
                successErrMsg = "The Following Records Were Successfully Uploaded:\n";
                int insListSize	=	insertedList.size();
                for(int i=0;i<insListSize;i++)
                {
                    masterDOB = (ZoneCodeMasterDOB)insertedList.get(i);
                    if(masterDOB!=null)
                    {
                      childList = masterDOB.getZoneCodeList();
                      if(childList!=null)
                      {
                    	  int chldListSize	=	 childList.size();
                        for(int j=0;j<chldListSize;j++)
                        {
                            childDOB = (ZoneCodeChildDOB)childList.get(j);
                            successErrMsg = successErrMsg + "Zone Code "+childDOB.getZone()+" for Location Id "+masterDOB.getOriginLocation()+
                                                            ", Shipment Mode "+("1".equalsIgnoreCase(masterDOB.getShipmentMode())?"Air":"Sea")+ 
                                                            ((masterDOB.getConsoleType()!=null && masterDOB.getConsoleType().trim().length()>0)?(" & Console Type "+masterDOB.getConsoleType()):"")+"\n";
                                                            
                         }
                      }                      
                    }
                }
              }
              if((failureList!=null && failureList.size()>0) || (failedList!=null && failedList.size()>0))
              {
                failureErrMsg = "The Following Records Were Not Uploaded:\n";
                if(failureList != null && failureList.size()>0)
                {
                	int failListSize	=	failureList.size();
                  for(int i=0;i<failListSize;i++)
                  {
                    masterDOB = (ZoneCodeMasterDOB)failureList.get(i);
                    if(masterDOB!=null)
                    {
                       childList = masterDOB.getZoneCodeList();
                       if(childList!=null)
                       {
                    	   int childListSize	= childList.size();
                          for(int j=0;j<childListSize;j++)
                          {
                              childDOB = (ZoneCodeChildDOB)childList.get(j);
                              failureErrMsg = failureErrMsg + "Zone Code "+childDOB.getZone()+" for Location Id "+masterDOB.getOriginLocation()+
                                                            ", Shipment Mode "+("1".equalsIgnoreCase(masterDOB.getShipmentMode())?"Air":"Sea")+ 
                                                            ((masterDOB.getConsoleType()!=null && masterDOB.getConsoleType().trim().length()>0)?(" & Console Type "+masterDOB.getConsoleType()):"")+
                                                            ((masterDOB.getRemarks()!=null && masterDOB.getRemarks().length()>0)?("("+masterDOB.getRemarks()+")"):("("+childDOB.getRemarks()+")"))+"\n"; 
                          }
                       }
                    }
                  }
                }
                if(failedList != null && failedList.size()>0)
                {
                	int failedListSize		=	failedList.size();
                  for(int i=0;i<failedListSize;i++)
                  {
                    masterDOB = (ZoneCodeMasterDOB)failedList.get(i);
                    if(masterDOB!=null)
                    {
                       childList = masterDOB.getZoneCodeList();
                       if(childList!=null)
                       {
                    	   int childListSize	= childList.size();
                          for(int j=0;j<childListSize;j++)
                          {
                              childDOB = (ZoneCodeChildDOB)childList.get(j);
                              failureErrMsg = failureErrMsg + "Zone Code "+childDOB.getZone()+" for Location Id "+masterDOB.getOriginLocation()+
                                                            ", Shipment Mode "+("1".equalsIgnoreCase(masterDOB.getShipmentMode())?"Air":"Sea")+ 
                                                            ((masterDOB.getConsoleType()!=null && masterDOB.getConsoleType().trim().length()>0)?(" & Console Type "+masterDOB.getConsoleType()):"")+
                                                            ((masterDOB.getRemarks()!=null && masterDOB.getRemarks().length()>0)?("("+masterDOB.getRemarks()+")"):("("+childDOB.getRemarks()+")"))+"\n"; 
                          }
                       }
                    }
                  }
                }
              }
              errorMessage  = successErrMsg + failureErrMsg;
              errorCode     = "MSG";
              errorMessageObject = new ErrorMessage(errorMessage,"QMSCanadaZonesController?Operation="+operation);
              keyValueList.add(new KeyValue("ErrorCode",errorCode)); 	
              keyValueList.add(new KeyValue("Operation",operation)); 	
              errorMessageObject.setKeyValueList(keyValueList);
              request.setAttribute("ErrorMessage",errorMessageObject); 
              nextNavigation = "ESupplyErrorPage.jsp";
          }
          else
          {
            StringBuffer  sb  =  doExcelGen(list);
            if(sb.length()>0)
            {
              PrintWriter out = response.getWriter();
              response.setContentType("application/vnd.ms-excel");
              response.setHeader("Content-Disposition","attachment;filename=CanadaZonesUpload.xls");
              out.print(sb.toString());
            }
          }
      }
      else if("Download".equalsIgnoreCase(operation) && subOperation == null)
      {
        nextNavigation    =   "QMSCanadaZonesDownloadIndex.jsp";
      }
      else if ("Download".equalsIgnoreCase(operation) && "process".equalsIgnoreCase(subOperation))
      {
          list       =   getCanadaZones(request,response);          
          StringBuffer  sb  =  doDownloadExcelGeneration(list);
          if(sb.length()>0)
          {
            PrintWriter out = response.getWriter();
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Content-Disposition","attachment;filename=CanadaZonesDownload.xls");
            out.print(sb.toString());
          }          
      }
    }
    catch(FoursoftException fs)
    {
      logger.error(FILE_NAME+"Error in Controller:"+fs);
      fs.printStackTrace();
      errorMessage = fs.getMessage();
      errorCode    = "ERR";
     // errorMessageObject = new ErrorMessage(errorMessage,"QMSCanadaZonesController?Operation="+operation+"&subOperation="+subOperation);
      errorMessageObject = new ErrorMessage(errorMessage,"QMSCanadaZonesController?Operation="+operation);
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
    //  errorMessageObject = new ErrorMessage(errorMessage,"QMSCanadaZonesController?Operation="+operation+"&subOperation="+subOperation);
      errorMessageObject = new ErrorMessage(errorMessage,"QMSCanadaZonesController?Operation="+operation);
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
  /**
   * Calls doGetZoneCodeDetails to get the details from the uploaded file & Inserts or Updates the records
   * based on the data uploaded.
   * @throws com.foursoft.esupply.common.exception.FoursoftException
   * @return ArrayList
   * @param response
   * @param request
   */
  private ArrayList doCanadaZoneCodeUploadProcess(HttpServletRequest request,HttpServletResponse response) throws FoursoftException
  {
    ArrayList                 list        =   null;
    ArrayList                 successList =   null;
    ArrayList                 failureList =   null;
    HashMap                   map         =   null;
    ArrayList                 returnList  =   new ArrayList();
    QMSSetUpSessionHome 	    home	      =	  null;
		QMSSetUpSession 		      remote	    =	  null;
    
    try
    {
       list         =   doGetZoneCodeDetails(request,response);
       successList  =   (ArrayList)list.get(0);
       failureList  =   (ArrayList)list.get(1);
       
       
       if(successList.size()==0 && failureList.size()==0)
          throw new FoursoftException("No Data Found In the Uploaded File.\nPlease Ensure that the Format of the File being Uploaded Matches the Format of the Sample File.");
       else
       {
          home    =   (QMSSetUpSessionHome)LookUpBean.getEJBHome("QMSSetUpSessionBean");
          remote  =   (QMSSetUpSession)home.create();
                map     =   remote.uploadCanadaZoneCodes(successList);
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
      logger.error(FILE_NAME+"Exception in doCanadaZoneCodeUploadProcess "+e);
      throw new FoursoftException("An Error has Occurred While Uploading the Data. Please Try Again.",e);
    }
    return returnList;
  }
  
  /**
   * Fetches the data from the uploaded File & Performs Basic validations on the data. 
   * @throws com.foursoft.esupply.common.exception.FoursoftException
   * @return ArrayList object containing Successful & failed data ArrayList objects
   * @param response
   * @param request
   */
  private ArrayList doGetZoneCodeDetails(HttpServletRequest request,HttpServletResponse response) throws FoursoftException
  {
    ArrayList               list          =   new ArrayList();
    ArrayList               successList   =   new ArrayList();
    ArrayList               failureList   =   new ArrayList();
    MultipartParser		      mp            =   null;
    Part					          part          =   null;
    String				          name          =   null;
    String				          value         =   null;
    String				          operation     =   null;
    FilePart				        filePart      =   null;
    ParamPart				        paramPart     =   null;
    String				          fileName      =   null;
    long					          size          =   0;
    BufferedReader		      br            =   null;
    String				          data          =   null;      
    String				          errorMode     =   null;
    String				          shipmentMode	=   "";
    String				          consoleType		=   "";
    String				          locationId		=   "";
    String				          terminalId  	=   "";
    String				          city			    =   "";
    String				          state			    =   "";
    String				          zipCode			  =   "";
    String				          alphaNumeric 	=   "";
    String				          fromZipCode		=   "";
    String				          toZipCode		  =   "";
    String				          zone			    =   "";
    String				          estimatedTime	=   "";
    String                  estimatedDistance = "";
    FileInputStream         fileRead      =   null;
    HashMap                 hMap          =   null;
    String                  tempString    =   null;
    StringTokenizer         strToken      =   null;
    String			            remarks       =   "";
    String                  subString     =   null;
    ZoneCodeMasterDOB       zoneCodeDOB   =   null;
    ZoneCodeChildDOB        childDOB      =   null;
    ArrayList               zoneCodeList  =   new ArrayList();
    
    int                     rowId         =   -1;
    int                     beginIndex    =    0;
    int                     rowNo         =    0;
    String[]                 str           = null;
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
         fileRead = new FileInputStream(fileName);
         br=new BufferedReader(new InputStreamReader(fileRead));
         hMap     =  new HashMap();
         data=br.readLine();	 
         data=br.readLine();	 
       
         int i=1;
         while(data!=null&&!data.equals("")&&data.trim().length()>0){  
 
             zoneCodeDOB		 =   new ZoneCodeMasterDOB();
             childDOB        =   new ZoneCodeChildDOB();
             zoneCodeList    =   new ArrayList();
            data+=",";
            tempString="";
             beginIndex=0;
          for(int noOfCols=0;noOfCols<7; noOfCols++) 
          {
            int delimIndex=data.indexOf(",");            
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
                if(noOfCols==7 ){
                    tempString=tempString + " ,";
                }
              }
              else
              {  
                subString=data.substring(beginIndex,delimIndex);
                tempString=tempString + subString+",";
              } 
            
             data=data.substring(delimIndex+1);
            
            beginIndex=0;
          }//for
          
          data=tempString.substring(0,tempString.length());
          
          strToken = new StringTokenizer(data,",");	

          while(strToken.hasMoreElements()){
            
            rowId 	= new Integer(strToken.nextToken()).intValue();
            
            if(rowId==-1)
              remarks = remarks + "Mandatory fields are not provided (rowId ). ";
            
            shipmentMode = strToken.nextToken();
            if(shipmentMode!=null && shipmentMode.trim().length()!=0)
            {
              shipmentMode = shipmentMode.toUpperCase();
              if("AIR".equalsIgnoreCase(shipmentMode))
                          shipmentMode  = "1";
                      else if ("SEA".equalsIgnoreCase(shipmentMode))
                          shipmentMode  = "2";
              else
                remarks = remarks + "Shipment Mode Should Be Air or Sea. ";
            }
            else
              remarks = remarks + "Mandatory fields are not provided (Shipment Mode).";
      
            consoleType = strToken.nextToken();
            
            if(!"1".equalsIgnoreCase(shipmentMode) && (consoleType==null || (consoleType!=null && consoleType.trim().length()==0)))
              remarks = remarks + "Mandatory fields are not provided (Console Type).";
            else if(!"1".equalsIgnoreCase(shipmentMode))
            {
              consoleType	= consoleType.toUpperCase();
              if(!("LCL".equalsIgnoreCase(consoleType) || "FCL".equalsIgnoreCase(consoleType)))
                remarks = remarks + "Console Type Should Be LCL or FCL.";
            }
            
            locationId 	= strToken.nextToken();
            if(locationId!=null && !locationId.trim().equals("")){
              locationId = locationId.toUpperCase();	
              if(locationId.length() != 3)
                remarks = remarks + "Max Length Must Be  3 (Location Id). ";
            }else
              remarks = remarks + "Mandatory fields are not provided (Location Id). ";
            
            terminalId	=strToken.nextToken();
            if(terminalId !=null && !terminalId.trim().equals("")){
              terminalId = terminalId.toUpperCase();
              if(terminalId.length() > 16)
                remarks = remarks + "Max Length Must Be  16 (TerminalId). ";
            }else
              remarks = remarks +"Mandatory fields are not provided (TerminalId). ";
            city = strToken.nextToken();	
            if(city !=null && !city.trim().equals("")){
              city = city.toUpperCase();
              if(city.length() > 20 )
                remarks = remarks + "Max Length Must Be less than 20 (City). ";
            }else
              remarks = remarks +"Mandatory fields are not provided (City). ";
            state = strToken.nextToken();	
            if(state !=null && !state.trim().equals("")){
              state = state.toUpperCase();
              if(state.length() > 20 )
                remarks = remarks + "Max Length Must Be less than 20 (state). ";
            }
            else
              state = "";
     
            zoneCodeDOB = new ZoneCodeMasterDOB(locationId,terminalId,city,state,zipCode);
            zoneCodeDOB.setRowId(rowId);
            zoneCodeDOB.setShipmentMode(shipmentMode);
            zoneCodeDOB.setConsoleType(consoleType);
            zoneCodeDOB.setRemarks(remarks);
            zoneCodeDOB.setZoneCodeList(new ArrayList());
          }//End For While
              remarks = "";
            i++;
            hMap.put(new Integer(rowId),zoneCodeDOB);
            rowId = -1;

             data = br.readLine();
       
         
           if(data==null||data.trim().length()==0)
           {
               data=null;	 
               break;
            }
          
            
        }
        
          // br.readLine();
           data=br.readLine();
           data=br.readLine();
           while(data!=null&&!data.equals("")&&data.trim().length()>0)
           {  
            data+=",";
            tempString="";
            beginIndex=0;
            rowId = -1;
            
            for(int noOfCols=0;noOfCols<4; noOfCols++) {
              int delimIndex=data.indexOf(",");
              
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
              }else{
                subString=data.substring(beginIndex,delimIndex);
                tempString=tempString + subString+",";              
            } 			
              data=data.substring(delimIndex+1);			
              beginIndex=0;
          }//for
          
          data=tempString.substring(0,tempString.length());          
          strToken = new StringTokenizer(data,",");
          while(strToken.hasMoreElements())
          {		 
          
              rowId 	= new Integer(strToken.nextToken()).intValue();              
               if(rowId==-1)
                  remarks = remarks + "Mandatory fields are not provided (rowId ). ";
      
              fromZipCode 	= strToken.nextToken();
              
              if(fromZipCode!=null && !fromZipCode.trim().equals("")){
                fromZipCode = fromZipCode.toUpperCase();	
                if(fromZipCode.length() > 20)
                  remarks = remarks + "Max Length Must Be  20 (From Zip Code). ";
              }else
                remarks = remarks + "Mandatory fields are not provided (From Zip Code). ";
      
              toZipCode	=strToken.nextToken();
              
              if(toZipCode !=null && !toZipCode.trim().equals("")){
                toZipCode = toZipCode.toUpperCase();
                if(toZipCode.length() > 20)
                  remarks = remarks + "Max Length Must Be  20 (To Zip Code). ";
              }else
                remarks = remarks +"Mandatory fields are not provided (To Zip Code). ";
      
              zone = strToken.nextToken();	
              
              if(zone !=null && !zone.trim().equals("")){
                zone = zone.toUpperCase();
                if(zone.length() > 20 )
                  remarks = remarks + "Max Length Must Be less than 20 (Zone). ";
              }else
                remarks = remarks +"Mandatory fields are not provided (Zone). ";
          }//End For While
      
          childDOB    = new ZoneCodeChildDOB();
          childDOB.setFromZipCode(fromZipCode);
          childDOB.setToZipCode(toZipCode);
          childDOB.setZone(zone);
         zoneCodeDOB      =  (ZoneCodeMasterDOB)hMap.get(new Integer(rowId));
          
         // logger.info("zoneCodeDOB"+zoneCodeDOB);
          zoneCodeList     =  (ArrayList)zoneCodeDOB.getZoneCodeList();
          childDOB.setRemarks(remarks);
          zoneCodeList.add(childDOB);
          zoneCodeDOB.setZoneCodeList(zoneCodeList);
          
         data =  br.readLine();	
          if (data==null||data.trim().length()==0)
                break;
              remarks = "";
            
        }
        
      Set       keySet      = hMap.keySet();
      Iterator  it          = keySet.iterator();
      ArrayList childDOBList= null;
      int       listSize    = 0;
      boolean   flag        = true;
      while(it.hasNext())
      {
        flag        = true;
        zoneCodeDOB  = (ZoneCodeMasterDOB)hMap.get(it.next());
        childDOBList = zoneCodeDOB.getZoneCodeList();
        if(childDOBList!=null)
            listSize  = childDOBList.size();
        if(zoneCodeDOB.getRemarks().equals(""))
        {
          for(int k=0;k<listSize;k++)
          {
            childDOB  = (ZoneCodeChildDOB)childDOBList.get(k);
            if(childDOB!=null && !"".equalsIgnoreCase(childDOB.getRemarks()))
            {
                failureList.add(zoneCodeDOB);
                flag = false;
                break;
            }
          }
          if(flag)
            successList.add(zoneCodeDOB);
        }
        else
        {			
          failureList.add(zoneCodeDOB);
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
      logger.error(FILE_NAME+"Exception in doGetZoneCodeDetails:"+e);
      throw new FoursoftException("An Error Has Occurred While Fetching the Data from the Uploaded File. \nPlease Ensure that the Format of the File being Uploaded Matches the Format of the Sample File.",e);
    }
    return list;
  }
  /**
   * Used For Generating Error File in Excel Format.
   * @throws com.foursoft.esupply.common.exception.FoursoftException
   * @return StringBuffer
   * @param list
   */
  private StringBuffer doExcelGen(ArrayList list) throws FoursoftException
  {
    StringBuffer      sb             = new StringBuffer();
    ArrayList         failureList    = null;
    HashMap           map            = null;
    ArrayList         insertedList   = null;
    ArrayList         failedList     = null;
    ZoneCodeMasterDOB masterDOB      = null;
    ArrayList         childDOBList   = null;
    ZoneCodeChildDOB  childDOB       = null;
    
    String            successErrMsg  = "";
    String            failureErrMsg  = "";
    try
    {
        failureList     =   (ArrayList)list.get(0);
        map             =   (HashMap)list.get(1);
        if(map != null)
        {
          insertedList  = (ArrayList)map.get("EXISTS");
          failedList    = (ArrayList)map.get("NONEXISTS");
        }
        sb.append("<html>");
        sb.append("<body>");
        sb.append("<table width='100%' border='1' cellspacing='0' cellpadding='0'>");
        if(insertedList!=null && insertedList.size()>0)
        {
            sb.append("<tr>");
            sb.append("<td colspan='7'><b>The Following Records Were Successfully Uploaded:</b></td>");
            sb.append("</tr><tr>");
            sb.append("<td><b>Row Id</b></td>");
            sb.append("<td><b>Shipment Mode</b></td>");
            sb.append("<td><b>Console Type</b></td>");
            sb.append("<td><b>Location Id</b></td>");
            sb.append("<td><b>Terminal Id</b></td>");
            sb.append("<td><b>City</b></td>");
            sb.append("<td><b>State</b></td>");
            sb.append("</tr>");
            int insListSize		=	insertedList.size();
            for(int i=0;i<insListSize;i++)
            {
              masterDOB = (ZoneCodeMasterDOB)insertedList.get(i);
              if(masterDOB!=null)
              {
                  sb.append("<tr>");
                  sb.append("<td>").append(masterDOB.getRowId()).append("</td>");
                  sb.append("<td>").append("1".equalsIgnoreCase(masterDOB.getShipmentMode())?"Air":"Sea").append("</td>");
                  sb.append("<td>").append(masterDOB.getConsoleType()!=null?masterDOB.getConsoleType():"").append("</td>");
                  sb.append("<td>").append(masterDOB.getOriginLocation()).append("</td>");
                  sb.append("<td>").append(masterDOB.getTerminalId()).append("</td>");
                  sb.append("<td>").append(masterDOB.getCity()).append("</td>");
                  sb.append("<td>").append(masterDOB.getState()).append("</td>");
                  sb.append("</tr>");
              }
            }
            sb.append("</table><table width='100%' border='1' cellspacing='0' cellpadding='0'>");
            sb.append("<tr></tr><tr>");
            sb.append("<td><b>Row Id</b></td>");
            sb.append("<td><b>From ZipCode</b></td>");
            sb.append("<td><b>To ZipCode</b></td>");
            sb.append("<td><b>Zone</b></td>");
            sb.append("</tr>");
            
            for(int i=0;i<insListSize;i++)
            {
              masterDOB = (ZoneCodeMasterDOB)insertedList.get(i);
              if(masterDOB!=null)
              {
                childDOBList  = masterDOB.getZoneCodeList();
                int childDOBSize	= childDOBList.size();
                for(int j=0;j<childDOBSize;j++)
                {
                  childDOB = (ZoneCodeChildDOB)childDOBList.get(j);
                  if(childDOB!=null)
                  {
                    sb.append("<tr>");
                    sb.append("<td>").append(masterDOB.getRowId()).append("</td>");
                    sb.append("<td>").append(childDOB.getFromZipCode()).append("</td>");
                    sb.append("<td>").append(childDOB.getToZipCode()).append("</td>");
                    sb.append("<td>").append(childDOB.getZone()).append("</td>");
                    sb.append("</tr>");
                  }
                }
              }
            }
            sb.append("</table>");
        }
        if((failureList!=null && failureList.size()>0) || (failedList!=null && failedList.size()>0))
        {
            sb.append("<table width='100%' border='1' cellspacing='0' cellpadding='0'>");
            sb.append("<tr>");
            sb.append("<td colspan='8'><b>The Following Zone Codes Were Not Uploaded: (Please Refer to the Remarks Column for Details).<b></td>");
            sb.append("</tr><tr>");
            sb.append("<td><b>Row Id</b></td>");
            sb.append("<td><b>Shipment Mode</b></td>");
            sb.append("<td><b>Console Type</b></td>");
            sb.append("<td><b>Location Id</b></td>");
            sb.append("<td><b>Terminal Id</b></td>");
            sb.append("<td><b>City</b></td>");
            sb.append("<td><b>State</b></td>");
            sb.append("<td><b>Remarks</b></td>");
            sb.append("</tr>");
        }
        if(failureList != null && failureList.size()>0)
        {
        	int failListSize	=	failureList.size();
            for(int i=0;i<failListSize;i++)
            {
              masterDOB = (ZoneCodeMasterDOB)failureList.get(i);
              if(masterDOB!=null)
              {
                  sb.append("<tr>");
                  sb.append("<td>").append(masterDOB.getRowId()).append("</td>");
                  sb.append("<td>").append("1".equalsIgnoreCase(masterDOB.getShipmentMode())?"Air":"Sea").append("</td>");
                  sb.append("<td>").append(masterDOB.getConsoleType()!=null?masterDOB.getConsoleType():"").append("</td>");
                  sb.append("<td>").append(masterDOB.getOriginLocation()).append("</td>");
                  sb.append("<td>").append(masterDOB.getTerminalId()).append("</td>");
                  sb.append("<td>").append(masterDOB.getCity()).append("</td>");
                  sb.append("<td>").append(masterDOB.getState()).append("</td>");
                  sb.append("<td>").append(masterDOB.getRemarks()!=null?masterDOB.getRemarks():"Please Refer Details Data.").append("</td>");
                  sb.append("</tr>");
              }
            }
        }
        if(failedList != null && failedList.size() >0)
        {
        	int failedListSize		=	 failedList.size();
            for(int i=0;i<failedListSize;i++)
            {
              masterDOB = (ZoneCodeMasterDOB)failedList.get(i);
              if(masterDOB!=null)
              {
                  sb.append("<tr>");
                  sb.append("<td>").append(masterDOB.getRowId()).append("</td>");
                  sb.append("<td>").append("1".equalsIgnoreCase(masterDOB.getShipmentMode())?"Air":"Sea").append("</td>");
                  sb.append("<td>").append(masterDOB.getConsoleType()!=null?masterDOB.getConsoleType():"").append("</td>");
                  sb.append("<td>").append(masterDOB.getOriginLocation()).append("</td>");
                  sb.append("<td>").append(masterDOB.getTerminalId()).append("</td>");
                  sb.append("<td>").append(masterDOB.getCity()).append("</td>");
                  sb.append("<td>").append(masterDOB.getState()).append("</td>");
                  sb.append("<td>").append(masterDOB.getRemarks()!=null?masterDOB.getRemarks():"Please Refer Details Data.").append("</td>");
                  sb.append("</tr>");
              }
            }
        }
        if((failureList!=null && failureList.size()>0) || (failedList!=null && failedList.size()>0))
        {
            sb.append("</table><table width='100%' border='1' cellspacing='0' cellpadding='0'>");
            sb.append("<tr></tr><tr>");
            sb.append("<td><b>Row Id</b></td>");
            sb.append("<td><b>From ZipCode</b></td>");
            sb.append("<td><b>To ZipCode</b></td>");
            sb.append("<td><b>Zone</b></td>");
            sb.append("<td><b>Remarks</b></td>");
            sb.append("</tr>");
        }
        if(failureList != null && failureList.size()>0)
        {
        	int failListSize	=	failureList.size();
            for(int i=0;i<failListSize;i++)
            {
              masterDOB = (ZoneCodeMasterDOB)failureList.get(i);
              if(masterDOB!=null)
              {
                childDOBList  = masterDOB.getZoneCodeList();
                int childDOBSize	=	childDOBList.size(); 
                for(int j=0;j<childDOBSize;j++)
                {
                  childDOB = (ZoneCodeChildDOB)childDOBList.get(j);
                  if(childDOB!=null)
                  {
                    sb.append("<tr>");
                    sb.append("<td>").append(masterDOB.getRowId()).append("</td>");
                    sb.append("<td>").append(childDOB.getFromZipCode()).append("</td>");
                    sb.append("<td>").append(childDOB.getToZipCode()).append("</td>");
                    sb.append("<td>").append(childDOB.getZone()).append("</td>");
                    sb.append("<td>").append(childDOB.getRemarks()!=null?childDOB.getRemarks():"Please Refer Header Remarks.").append("</td>");
                    sb.append("</tr>");
                  }
                }
              }
            }
        }
        if(failedList != null && failedList.size()>0)
        {
        	int failedListSize	= failedList.size();
            for(int i=0;i<failedListSize;i++)
            {
              masterDOB = (ZoneCodeMasterDOB)failedList.get(i);
              if(masterDOB!=null)
              {
                childDOBList  = masterDOB.getZoneCodeList();
                int chldDOBListSize		= childDOBList.size();	
                for(int j=0;j<chldDOBListSize;j++)
                {
                  childDOB = (ZoneCodeChildDOB)childDOBList.get(j);
                  if(childDOB!=null)
                  {
                    sb.append("<tr>");
                    sb.append("<td>").append(masterDOB.getRowId()).append("</td>");
                    sb.append("<td>").append(childDOB.getFromZipCode()).append("</td>");
                    sb.append("<td>").append(childDOB.getToZipCode()).append("</td>");
                    sb.append("<td>").append(childDOB.getZone()).append("</td>");
                    sb.append("<td>").append(childDOB.getRemarks()!=null?childDOB.getRemarks():"Please Refer Header Remarks.").append("</td>");
                    sb.append("</tr>");
                  }
                }
              }
            }
            sb.append("</table>");
        }
    }
    catch(Exception e)
    {
      e.printStackTrace();
      logger.error(FILE_NAME+"Exception in doChargeGroupExcelGen "+e);
      throw new FoursoftException("An Error Has Occurred While Generating Error File in Excel Format. Please Try Again.",e);
    }
    return sb;
  }
 
  /**
   * Used for Fetching Canada Zone Codes from EJB
   * @throws com.foursoft.esupply.common.exception.FoursoftException
   * @return ArrayList
   * @param response
   * @param request
   */
  private ArrayList getCanadaZones(HttpServletRequest request,HttpServletResponse response) throws FoursoftException
  {
    ZoneCodeMasterDOB         masterDOB    =   null;
    ArrayList                 list         =   null;
    String                    locationIds  =   null;
    QMSSetUpSessionHome 	    home	       =	 null;
		QMSSetUpSession 		      remote	     =	 null;
    
    try
    {
      masterDOB     =     new ZoneCodeMasterDOB();
      locationIds   =     request.getParameter("locationId")!=null?request.getParameter("locationId").replaceAll(",","~"):"";
      masterDOB.setShipmentMode(request.getParameter("shipmentMode")!=null?request.getParameter("shipmentMode"):"");
      masterDOB.setConsoleType(request.getParameter("consoleType")!=null?request.getParameter("consoleType"):"");
      masterDOB.setOriginLocation(locationIds);
      
      home    =   (QMSSetUpSessionHome)LookUpBean.getEJBHome("QMSSetUpSessionBean");
      remote  =   (QMSSetUpSession)home.create();
      
      list    =    remote.downloadCanadaZones(masterDOB);
      
    }
    catch(Exception e)
    {
      logger.error("Exception in getMasterDetails "+e);
      e.printStackTrace();
      throw new FoursoftException("An Error has Occurred While Retrieving Data. Please Try Again.");
    }
    return list;
  }
  private StringBuffer doDownloadExcelGeneration(ArrayList list) throws FoursoftException
  {
    StringBuffer        sb          =   new StringBuffer();
    ZoneCodeMasterDOB   masterDOB   =   null;
    ArrayList           zoneCodeList=   null;
    ZoneCodeChildDOB    childDOB    =   null;
    int                 listSize    =   0;
    
    try
    {
      sb.append("<html>");
      sb.append("<body>");
      sb.append("<table width='100%' border='1' cellspacing='0' cellpadding='0'>");
      if(list!=null && list.size() >0)
      {
        listSize = list.size();
        sb.append("<tr>");
        sb.append("<td><b>Row Id</b></td>");
        sb.append("<td><b>Shipment Mode</b></td>");
        sb.append("<td><b>Console Type</b></td>");
        sb.append("<td><b>Location Id</b></td>");
        sb.append("<td><b>Terminal Id</b></td>");
        sb.append("<td><b>City</b></td>");
        sb.append("<td><b>State</b></td>");
        sb.append("</tr>");
      }
      else
      {
        sb.append("<tr><td>No Zones are Defined for the Selected Locations</td></tr>");
      }
          
      for(int i=0;i<listSize;i++)
      {
        masterDOB   =   (ZoneCodeMasterDOB)list.get(i);      
        if(masterDOB!=null)
        {
            sb.append("<tr>");
            sb.append("<td>").append(i+1).append("</td>");
            sb.append("<td>").append(masterDOB.getShipmentMode()).append("</td>");
            sb.append("<td>").append(masterDOB.getConsoleType()!=null?masterDOB.getConsoleType():"").append("</td>");
            sb.append("<td>").append(masterDOB.getOriginLocation()).append("</td>");
            sb.append("<td>").append(masterDOB.getTerminalId()).append("</td>");
            sb.append("<td>").append(masterDOB.getCity()!=null?masterDOB.getCity():"").append("</td>");
            sb.append("<td>").append(masterDOB.getState()!=null?masterDOB.getState():"").append("</td>");
            sb.append("</tr>");
        }
      }
      
      if(listSize >0)
      {
        sb.append("<tr></tr>");
        sb.append("<tr>");
        sb.append("<td><b>Row Id</b></td>");
        sb.append("<td><b>From Zip Code</b></td>");
        sb.append("<td><b>To Zip Code</b></td>");
        sb.append("<td colspan='4'><b>Zone</b></td>");
        //sb.append("<td><b>Row No</b></td>");
        sb.append("</tr>");
      }
      
      for(int i=0;i<listSize;i++)
      {
        masterDOB   =   (ZoneCodeMasterDOB)list.get(i);            
        if(masterDOB!=null)
        {
            zoneCodeList  = masterDOB.getZoneCodeList();
            int zCListSize	=	zoneCodeList.size();
            for(int j=0;j<zCListSize;j++)
            {
              childDOB = (ZoneCodeChildDOB)zoneCodeList.get(j);
              if(childDOB!=null)
              {
                sb.append("<tr>");
                sb.append("<td>").append(i+1).append("</td>");
                sb.append("<td>").append(childDOB.getFromZipCode()).append("</td>");
                sb.append("<td>").append(childDOB.getToZipCode()).append("</td>");
                sb.append("<td colspan='4'>").append(childDOB.getZone()).append("</td>");
                //sb.append("<td>").append(childDOB.getRowNo()).append("</td>");
                sb.append("</tr>");
              }
            }
        }
      }
      sb.append("</table>");
      
    }
    catch(Exception e)
    {
      e.printStackTrace();
      logger.error("Exception While Generating Download Excel File."+e);
      throw new FoursoftException("An Error Has Occurred While Generating the Download Excel File. Please Try Again!",e);
    }
    return sb;
  }
  /**
   * Process the file Name and dispatch to that file.
   * @throws javax.servlet.ServletException
   * @throws java.io.IOException
   * @param forwardFile
   * @param response
   * @param request
   */
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