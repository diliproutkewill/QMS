<%-- 
% Copyright (c) 1999-2005 by FourSoft, Pvt Ltd. All Rights Reserved.
% This software is the proprietary information of FourSoft, Pvt Ltd.
% Use is subject to license terms.
%
% esupply - v 1.x 
%
--%>
<%--
% File			  	: QMSZoneCodeMasterUploadProcess.jsp
% Sub-module		: ZoneCodeMaster Upload
% Module		  	: QMS
%
%
% author			: RamaKrishna
% date				: 14-07-2005
% Purpose			: Process's The Upload File
% Modified Date		Modified By			Reason
% 
% 
--%>


<jsp:useBean id="loginbean" scope="session" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" />	
<%@ page import="java.sql.Timestamp,
				 com.oreilly.servlet.multipart.*,
				 java.io.BufferedReader,
				 java.io.InputStreamReader,
				 java.io.FileInputStream,
				 java.util.ArrayList,
                 java.util.HashMap,
				 java.util.Hashtable,
				 java.util.Enumeration,
                 java.io.File,
                 java.util.Iterator,
				 java.util.ListIterator,
				 java.util.StringTokenizer,
				 javax.naming.InitialContext,
				 com.qms.setup.ejb.sls.QMSSetUpSession,
                 com.qms.setup.ejb.sls.QMSSetUpSessionHome,
				 org.apache.log4j.Logger,
				 com.foursoft.esupply.common.java.ErrorMessage,
				 com.foursoft.esupply.common.exception.FoursoftException,
				 com.qms.setup.java.ZoneCodeMasterDOB,
				 com.qms.setup.java.ZoneCodeChildDOB,
				 com.foursoft.esupply.common.java.KeyValue" %>
<%!
    private static Logger logger = null;
%>			 
<%
	
	ArrayList keyValueList   = null;
	
	String FILENAME="QMSZoneCodeMasterUploadProcess.jsp";  
  
  logger  = Logger.getLogger(FILENAME);
	int count;
	String data = "";
	java.util.ArrayList exList = new java.util.ArrayList();
	java.util.ArrayList newList = new java.util.ArrayList();
	java.util.ArrayList successList = new java.util.ArrayList();
	java.util.ArrayList failureList = new java.util.ArrayList(); 
	FileInputStream fileRead=null;	
	ErrorMessage message				= null;
	KeyValue			keyValue		= null;
	ArrayList			list			= null;
	String				fileName		= "";
	String				process			= "";
	String				operation		= "";
	String				type			= "";
	String				errorMode		= "";
	String				shipmentMode	= "";
	String				consoleType		= "";
    String				locationId		= "";
    String				terminalId  	= "";
    String				city			= "";
    String				state			= "";
    String				zipCode			= "";
//	String              port            = "";
	String				alphaNumeric 	= "";
    String				fromZipCode		= "";
    String				toZipCode		= "";
    String				zone			= "";
    String				estimatedTime	= "";
	String              estimatedDistance = "";
	ZoneCodeMasterDOB zoneCodeDOB       = null;
	ZoneCodeChildDOB  zoneCodeChildDOB  = null;
	ArrayList         zoneCodeList      = new ArrayList();
	String            tempString        = null;
	StringTokenizer   strToken          = null;
	String			  remarks           = "";
	String            subString         = null;
	Hashtable         zoneDOBHashtable  = null;
	int               rowId             = -1;
	int               beginIndex        = 0;
	int               rowNo             =  0;

	try{
		
		MultipartParser mp = new MultipartParser(request, 10*1024*1024); // 10MB
		Part part; 		
		while ((part = mp.readNextPart()) != null){
			String name = part.getName();
            if (name != null){
                name = name.trim();
			}
			
			if (part.isParam()){                     
				ParamPart paramPart = (ParamPart) part;
				String value = paramPart.getStringValue();
				
				if (name.equals("Process")){
					process = value;               
                }
				if(name.equals("type")){
					type = value;                    
				}	
				if(name.equals("errorMode")){
					errorMode = value;
				}
				if(name.equals("Operation")){
					operation = value;
				}
             }else if (part.isFile()){
				FilePart filePart = (FilePart) part;
				fileName = filePart.getFileName();
				if (fileName != null){ 
				  fileName = fileName.trim();
				  long size = filePart.writeTo(new File("./"));
				}else{ 
					// the field did not contain a file
					
				}
            }//else if
	   }//while
	 
	 fileRead = new FileInputStream(fileName);
	 
	 BufferedReader br=new BufferedReader(new InputStreamReader(fileRead));
	 zoneDOBHashtable    =  new Hashtable();
	 data=br.readLine();	 
	 data=br.readLine();	 
	 //Logger.info(FILENAME,"data=  "+data);
	 int i=1;
	 while(!data.equals("")){  
			 zoneCodeDOB		 =   new ZoneCodeMasterDOB();
			 zoneCodeChildDOB    =   new ZoneCodeChildDOB();
			 zoneCodeList        =   new ArrayList();
			data+=",";
			tempString="";
			 beginIndex=0;
		for(int noOfCols=0;noOfCols<8; noOfCols++) 
		{
			int delimIndex=data.indexOf(",");
			//Logger.info(FILENAME,noOfCols+"  delimIndex=  "+delimIndex);
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
				if(noOfCols==8 ){
				    tempString=tempString + " ,";
				}
				
				
            }else{  
				
				subString=data.substring(beginIndex,delimIndex);
				tempString=tempString + subString+",";
				
			} 
			//Logger.info(FILENAME,"  tempString=  "+tempString);
		    data=data.substring(delimIndex+1);
			//Logger.info(FILENAME,"  data=  "+data);
			beginIndex=0;
		}//for
		//Logger.info(FILENAME,"  tempString=123  "+tempString);
		data=tempString.substring(0,tempString.length());
		//Logger.info(FILENAME,"  data=123  "+data);
		strToken = new StringTokenizer(data,",");	
		    
		while(strToken.hasMoreElements()){
			
			try
			{
				rowId 	= new Integer(strToken.nextToken()).intValue();
			}
			catch (NumberFormatException nf)
			{
				rowId = -1;
				throw new FoursoftException("Row Id has not been provided for the Header Data at Row "+i+". Please ensure that the Upload File format matches the sample file format.");
			}
			
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
			/*port = strToken.nextToken();	
			if(port !=null && !port.trim().equals("")){
				port = port.toUpperCase();
				if(port.length() > 20 )
					remarks = remarks + "Max Length Must Be less than 20 (Port). ";
			}
			else
				remarks = remarks +"Mandatory fields are not provided (Port). ";	*/		
			//Logger.info(FILENAME,"  zipCode  "+zipCode);
			zipCode = strToken.nextToken();	
			//Logger.info(FILENAME,"  zipCode  "+zipCode);
			if(zipCode !=null && !zipCode.trim().equals("")){
				zipCode = zipCode.toUpperCase();
				if(!zipCode.equalsIgnoreCase("N") && !zipCode.equalsIgnoreCase("AN"))
				    remarks = remarks +" Must Contain Valid Values As Specified In Sample Format (Zip Code). ";
				else if(zipCode.equalsIgnoreCase("N"))
				{
                  zipCode   =  "NUMERIC";
				}
				else
					zipCode =  "ALPHANUMERIC";
			}
			else
			{
				remarks = remarks +" Mandatory Fields are not provided (Zip Code Type). ";
			}
			//Logger.info(FILENAME,"  rowId  "+rowId);
			zoneCodeDOB = new ZoneCodeMasterDOB(locationId,terminalId,city,state,zipCode);
			zoneCodeDOB.setRowId(rowId);
			zoneCodeDOB.setShipmentMode(shipmentMode);
			zoneCodeDOB.setConsoleType(consoleType);
			zoneCodeDOB.setRemarks(remarks);
			zoneCodeDOB.setZoneCodeList(new ArrayList());
		}//End For While
				
			data=br.readLine();			
			if (data==null){
				break;
			}
			remarks = "";
			i++;
			//Logger.info(FILENAME,"  zoneCodeDOB  "+zoneCodeDOB);
			zoneDOBHashtable.put(new Integer(rowId),zoneCodeDOB);
			//Logger.info(FILENAME,"  rowId  "+rowId);
			rowId = -1;
	}
	
//Logger.info(FILENAME,"End of Master dtl");
		 data=br.readLine();
		 data=br.readLine();

		 i = 1;
		 
		 while(!data.equals("")){  
			data+=",";
			tempString="";
			beginIndex=0;
			rowId = -1;
			int c =  0;
			if(!process.equalsIgnoreCase("ADD"))
				c=8;
			else
				c=7;

			for(int noOfCols=0;noOfCols<c; noOfCols++) {
				int delimIndex=data.indexOf(",");
				//Logger.info(FILENAME,noOfCols+"  delimIndex=  "+delimIndex);
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
				if(!process.equalsIgnoreCase("ADD")){
					if(noOfCols==7 ){
				    tempString=tempString + " ,";
				   }
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
			     try
				{
					rowId 	= new Integer(strToken.nextToken()).intValue();
				}
				catch (NumberFormatException nf)
				{
					rowId = -1;
					throw new FoursoftException("Row Id has not been provided for the Details Data at row "+i+". Please ensure that the Upload File format matches the sample file format.");
				}
				// Logger.info(FILENAME," rowId "+rowId);
				if(rowId==-1)
					remarks = remarks + "Mandatory fields are not provided (rowId ). ";

			    alphaNumeric = strToken.nextToken();	
				//Logger.info(FILENAME,"alphaNumeric  "+alphaNumeric);
				if("ALPHANUMERIC".equalsIgnoreCase(zipCode) && (alphaNumeric ==null ||(alphaNumeric!=null && (alphaNumeric.equals("-") ||  alphaNumeric.trim().length()==0))))
				{
					remarks = remarks +"Mandatory fields are not provided (ALPHANUMERIC). ";
				}
				else
				{
					if(alphaNumeric !=null && (alphaNumeric.equals("-") || "".equalsIgnoreCase(alphaNumeric.trim())))
						alphaNumeric   =   "";
					else
					{ 
						  if(alphaNumeric !=null && !alphaNumeric.trim().equals(""))
						  {
							alphaNumeric = alphaNumeric.toUpperCase();
							if(alphaNumeric.length() > 15 )
								remarks = remarks + "Max Length Cannot be more than 15 (ALPHANUMERIC). ";
						  }
						  else
						 {
							remarks = remarks +"Mandatory fields are not provided (ALPHANUMERIC). ";
						  }
					 }
				}
				
				fromZipCode 	= strToken.nextToken();
				//Logger.info(FILENAME,"fromZipCode       "+fromZipCode);
				if(fromZipCode!=null && !fromZipCode.trim().equals("")){
					fromZipCode = fromZipCode.toUpperCase();	
					if(fromZipCode.length() > 20)
						remarks = remarks + "Max Length Must Be  20 (From Zip Code). ";
				}else
					remarks = remarks + "Mandatory fields are not provided (From Zip Code). ";

				toZipCode	=strToken.nextToken();
				//Logger.info(FILENAME,"toZipCode  "+toZipCode);
				if(toZipCode !=null && !toZipCode.trim().equals("")){
					toZipCode = toZipCode.toUpperCase();
					if(toZipCode.length() > 20)
						remarks = remarks + "Max Length Must Be  20 (To Zip Code). ";
				}else
					remarks = remarks +"Mandatory fields are not provided (To Zip Code). ";

				zone = strToken.nextToken();	
				//Logger.info(FILENAME,"zone  "+zone);
				if(zone !=null && !zone.trim().equals("")){
					zone = zone.toUpperCase();
					if(zone.length() > 20 )
						remarks = remarks + "Max Length Must Be less than 20 (Zone). ";
				}else
					remarks = remarks +"Mandatory fields are not provided (Zone). ";

				estimatedTime = strToken.nextToken();	
				//Logger.info(FILENAME,"estimatedTime  "+estimatedTime);
				if(estimatedTime !=null && !estimatedTime.trim().equals("")){
					estimatedTime = estimatedTime.toUpperCase();
					if(estimatedTime.length() > 5 )
						remarks = remarks + "Max Length Must Be less than 5 (Estimated Time). ";
				}
				else
					remarks = remarks +"Mandatory fields are not provided (Estimated Time). ";

				estimatedDistance = strToken.nextToken();	
				//Logger.info(FILENAME,"estimatedDistance  "+estimatedDistance);
				if(estimatedDistance !=null && !estimatedDistance.trim().equals("")){
					estimatedDistance = estimatedDistance.toUpperCase();
					if(estimatedDistance.length() > 5 )
						remarks = remarks + "Max Length Must Be less than 5 (Estimated Distance)"+estimatedDistance+". ";
				}
				else
					remarks = remarks +"Mandatory fields are not provided (Estimated Distance)"+estimatedDistance+". ";	
				if(!process.equalsIgnoreCase("ADD")){
				rowNo  = new Integer(strToken.nextToken()).intValue();
				if(rowNo >= 0){
					rowNo = rowNo;					
				}
				else
					remarks = remarks +"Mandatory fields are not provided (rowNo)"+rowNo+". ";	
				}

				
		}//End For While

		zoneCodeChildDOB =   new ZoneCodeChildDOB(alphaNumeric,fromZipCode,toZipCode,zone,estimatedTime,estimatedDistance);
		//Logger.info(FILENAME,"new Integer(rowId)  "+new Integer(rowId));
		if(!process.equalsIgnoreCase("ADD")){
			zoneCodeChildDOB.setRowNo(new Integer(rowNo).toString());
		}
		zoneCodeDOB      =  (ZoneCodeMasterDOB)zoneDOBHashtable.get(new Integer(rowId));
		zoneCodeList     =   (ArrayList)zoneCodeDOB.getZoneCodeList();		
		zoneCodeChildDOB.setRemarks(remarks);
		zoneCodeList.add(zoneCodeChildDOB);		
		zoneCodeDOB.setZoneCodeList(zoneCodeList);		
		
		
				data=br.readLine();	
				if (data==null)
					break;
				remarks = "";
				i++;
			
	}

	Enumeration enum1		 = zoneDOBHashtable.keys();
	ArrayList   childDOBList = null;
    int         listSize     = 0;
    boolean		flag         = true;
	while(enum1.hasMoreElements())
	{
		zoneCodeDOB  = (ZoneCodeMasterDOB)zoneDOBHashtable.get(enum1.nextElement());
		childDOBList = zoneCodeDOB.getZoneCodeList();
        if(childDOBList!=null)
            listSize  = childDOBList.size();

		if(zoneCodeDOB.getRemarks().equals(""))
		{			
			for(int k=0;k<listSize;k++)
		    {
				zoneCodeChildDOB  = (ZoneCodeChildDOB)childDOBList.get(k);
				if(zoneCodeChildDOB!=null)
				{
					if(zoneCodeChildDOB.getFromZipCode()!=null && zoneCodeChildDOB.getToZipCode().trim().length() > 0 && zoneCodeChildDOB.getToZipCode()!=null && zoneCodeChildDOB.getToZipCode().trim().length()>0)
					{
						if(Double.parseDouble(zoneCodeChildDOB.getFromZipCode()) > Double.parseDouble(zoneCodeChildDOB.getToZipCode()))
						{
							zoneCodeChildDOB.setRemarks("The To Zip Code Cannot be less than From Zip Code");
						}
					}
					if(!"".equalsIgnoreCase(zoneCodeChildDOB.getRemarks()))
					{
						failureList.add(zoneCodeDOB);
						flag = false;
						break;
					}
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


		HashMap finalMap = new HashMap(2,2);
		if(successList.size() > 0){
			InitialContext initial = new InitialContext();
			QMSSetUpSessionHome 	home	=	(QMSSetUpSessionHome)initial.lookup("QMSSetUpSessionBean");
			QMSSetUpSession 		remote	=	(QMSSetUpSession)home.create();
			finalMap = remote.uploadZoneCodeMasterDetails(successList,process.equalsIgnoreCase("ADD")?true:false);
		}else if(successList.size() == 0 && failureList.size() == 0){
			keyValueList       = new ArrayList(3);
			message = new ErrorMessage("No Data Found In The Uploaded File","QMSZoneCodeMasterUploadIndex.jsp?Operation=UpLoad");
			keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
			keyValueList.add(new KeyValue("Operation",request.getParameter("Operation"))); 	
			keyValueList.add(new KeyValue("Type","Country"));
			message.setKeyValueList(keyValueList);
				 
			request.setAttribute("ErrorMessage",message); 
%>
			<jsp:forward page="QMSESupplyErrorPage.jsp"/>
<%
		}
		if(successList.size() > 0)
		{
			if(process.equalsIgnoreCase("ADD"))
			{
				exList = (ArrayList)finalMap.get("NONEXISTS");
				newList = (ArrayList)finalMap.get("EXISTS");
			}
			else
			{
				exList = (ArrayList)finalMap.get("NONEXISTS");
				newList = (ArrayList)finalMap.get("EXISTS");
			}
		}
		
		String successErrMsg ="";
		String failureErrMsg = "";
		if(errorMode.equalsIgnoreCase("N")){
			if(newList.size() > 0)
				successErrMsg = " The Following Record(s) have been Uploaded Successfully : \n";
			for(int j = 0;j<newList.size();j++)
			{
				zoneCodeDOB = (ZoneCodeMasterDOB)newList.get(j);
				/*if(j!=newList.size()-1)
					successErrMsg = successErrMsg + zoneCodeDOB.getZoneCode() + ",";
				else
					successErrMsg = successErrMsg + zoneCodeDOB.getZoneCode() + " . \n";*/
				ArrayList childList	= zoneCodeDOB.getZoneCodeList();
				if(childList!=null)
				{
					for(int z=0;z<childList.size();z++)
					{
						zoneCodeChildDOB     =  (ZoneCodeChildDOB)childList.get(z);
						successErrMsg = successErrMsg +  " For Location Id :"+zoneCodeDOB.getOriginLocation()+" Zone: " + zoneCodeChildDOB.getZone() +".\n";
					}
				}
			}
			if(exList.size() > 0 || failureList.size() >0)
				failureErrMsg = " The Following Record(s) Couldn't Be Uploaded, Due To Below Reasons : \n";
			for(int k = 0;k<exList.size();k++)
			{
				zoneCodeDOB = (ZoneCodeMasterDOB)exList.get(k);
				ArrayList childList	= zoneCodeDOB.getZoneCodeList();
				if(childList!=null)
				{
					for(int z=0;z<childList.size();z++)
					{
						zoneCodeChildDOB     =  (ZoneCodeChildDOB)childList.get(z);
						failureErrMsg = failureErrMsg +  " " + zoneCodeChildDOB.getRemarks() +".\n";
					}
				}
			}

			for(int l = 0;l<failureList.size();l++)
			{
				zoneCodeDOB = (ZoneCodeMasterDOB)failureList.get(l);
				failureErrMsg = failureErrMsg +  " " + zoneCodeDOB.getRemarks() +".\n";
				/*ArrayList childList	= zoneCodeDOB.getZoneCodeList();
				if(childList!=null)
				{
					for(int z=0;z<childList.size();z++)
					{
						zoneCodeChildDOB     =  (ZoneCodeChildDOB)childList.get(z);
						failureErrMsg = failureErrMsg +  " " + zoneCodeChildDOB.getRemarks() +".\n";
					}
				}*/
			}
				 
			keyValueList       = new ArrayList(3);
		
    	message = new ErrorMessage(successErrMsg + failureErrMsg ,"QMSZoneCodeMasterUploadIndex.jsp?Operation=UpLoad");
		
    	keyValueList.add(new KeyValue("ErrorCode","MSG")); 	
			keyValueList.add(new KeyValue("Operation",request.getParameter("Operation"))); 	
			keyValueList.add(new KeyValue("Type","Country"));
			message.setKeyValueList(keyValueList);
				 
			request.setAttribute("ErrorMessage",message); 
%>
				<jsp:forward page="QMSESupplyErrorPage.jsp"/>
<%
		}
		else
		{
			out.clearBuffer();
			response.setContentType("application/vnd.ms-excel");	
			String contentDisposition = " :attachment;";	
			response.setHeader("Content-Disposition","attachment;filename=Exceptions.xls");
			if(newList.size() > 0)
			{
				successErrMsg = " The Following Id(s) Are Uploaded Successfully : ";
				out.println(successErrMsg);
				out.println();
				out.print("Row Id\t");
				out.print("Shipment Mode\t");
				out.print("Console Type\t");
				out.print("Origin Location\t");
				out.print("TerminalId\t");
				out.print("City\t");
				out.print("State\t");
				out.print("ZipCode Type\t");
				//out.print("Port:\t");
				out.println("Remarks");
			}
			for(int j = 0;j<newList.size();j++)
			{
				zoneCodeDOB = (ZoneCodeMasterDOB)newList.get(j);
				out.print(zoneCodeDOB.getRowId()+"\t");
				out.print(("1".equalsIgnoreCase(zoneCodeDOB.getShipmentMode())?"Air":"Sea")+"\t");
				out.print((zoneCodeDOB.getConsoleType()!=null?zoneCodeDOB.getConsoleType():"")+"\t");
				out.print(zoneCodeDOB.getOriginLocation()+"\t");
				out.print(zoneCodeDOB.getTerminalId()+"\t");
				out.print(zoneCodeDOB.getCity()+"\t");
				out.print(zoneCodeDOB.getState()+"\t");
				out.print(zoneCodeDOB.getZipCode()+"\t");
				//out.print(zoneCodeDOB.getPort()+"\t");
				out.println(zoneCodeDOB.getRemarks()!=null?zoneCodeDOB.getRemarks():"");				
			}
			if(newList.size() > 0)
			{
				out.println();
				out.print("Row Id\t");
				out.print("Alpha-Numeric Code \t");
				out.print("From Zipcode \t");
				out.print("To Zipcode \t");
				out.print("Zone \t" );
				out.print("Estimated Time \t");
				out.print("Estimated Distance\t");
				out.println("Remarks");
			}
			for(int j = 0;j<newList.size();j++)
			{
				zoneCodeDOB = (ZoneCodeMasterDOB)newList.get(j);
			    zoneCodeList = (ArrayList)zoneCodeDOB.getZoneCodeList();
				for(int k=0;k<zoneCodeList.size();k++)
				{
					zoneCodeChildDOB = (ZoneCodeChildDOB)zoneCodeList.get(k);
					out.print(zoneCodeDOB.getRowId()+"\t");
					out.print(zoneCodeChildDOB.getAlphaNumaric()+"\t");
					out.print(zoneCodeChildDOB.getFromZipCode()+"\t");
					out.print(zoneCodeChildDOB.getToZipCode()+"\t");
					out.print(zoneCodeChildDOB.getZone()+"\t");
					out.print(zoneCodeChildDOB.getEstimationTime()+"\t");
					out.print(zoneCodeChildDOB.getEstimatedDistance()+"\t");
					out.println(zoneCodeChildDOB.getRemarks()!=null?zoneCodeChildDOB.getRemarks():"");
				}
			}

			if(exList.size() > 0 || failureList.size() >0)
			{
				out.println();
				failureErrMsg = " The Following Id(s) Couldn't Be Uploaded, Due To Below Reasons : ";
				out.println(failureErrMsg);
				out.print("Row Id\t");
				out.print("Shipment Mode\t");
				out.print("Console Type\t");
				out.print("Location\t");
				out.print("TerminalId\t");
				out.print("City\t");
				out.print("State\t");
				out.print("ZipCode Type\t");
				//out.print("Port:\t");
				out.println("Remarks");
			}
			
			for(int k = 0;k<exList.size();k++)
			{
				zoneCodeDOB = (ZoneCodeMasterDOB)exList.get(k);
				out.print(zoneCodeDOB.getRowId()+"\t");
				out.print(("1".equalsIgnoreCase(zoneCodeDOB.getShipmentMode())?"Air":"Sea")+"\t");
				out.print((zoneCodeDOB.getConsoleType()!=null?zoneCodeDOB.getConsoleType():"")+"\t");
				out.print(zoneCodeDOB.getOriginLocation()+"\t");
				out.print(zoneCodeDOB.getTerminalId()+"\t");
				out.print(zoneCodeDOB.getCity()+"\t");
				out.print(zoneCodeDOB.getState()+"\t");
				out.print(zoneCodeDOB.getZipCode()+"\t");
				//out.print(zoneCodeDOB.getPort()+"\t");
				out.println(zoneCodeDOB.getRemarks()!=null?zoneCodeDOB.getRemarks():"");
			}
			if(exList.size() > 0)
			{
				//out.println(successErrMsg);
				out.println();
				out.print("Row Id\t");
				out.print("Alpha-Numeric  Code \t");
				out.print("From Zipcode\t");
				out.print("To Zipcode\t");
				out.print("Zone\t" );
				out.print("Estimated Time \t");
				out.print("Estimated Distance\t");
				out.println("Remarks:");
			}
			for(int j = 0;j<exList.size();j++)
			{
				zoneCodeDOB = (ZoneCodeMasterDOB)exList.get(j);
			    zoneCodeList = (ArrayList)zoneCodeDOB.getZoneCodeList();
				for(int k=0;k<zoneCodeDOB.getZoneCodeList().size();k++)
				{
					zoneCodeChildDOB = null;
					zoneCodeChildDOB = (ZoneCodeChildDOB)zoneCodeDOB.getZoneCodeList().get(k);
					out.print(zoneCodeDOB.getRowId()+"\t");
					out.print(zoneCodeChildDOB.getAlphaNumaric()+"\t");
					out.print(zoneCodeChildDOB.getFromZipCode()+"\t");
					out.print(zoneCodeChildDOB.getToZipCode()+"\t");
					out.print(zoneCodeChildDOB.getZone()+"\t");
					out.print(zoneCodeChildDOB.getEstimationTime()+"\t");
					out.print(zoneCodeChildDOB.getEstimatedDistance()+"\t");
					out.println(zoneCodeChildDOB.getRemarks()!=null?zoneCodeChildDOB.getRemarks():"");
				}
			}
			if(failureList.size() > 0){
				out.println();
				out.print("Row Id\t");
				out.print("Shipment Mode\t");
				out.print("Console Type\t");
				out.print("Location\t");
				out.print("TerminalId\t");
				out.print("City\t");
				out.print("State\t");
				out.print("ZipCode Type\t");
				//out.print("Port:\t");
				out.println("Remarks");
			}
			for(int l = 0;l<failureList.size();l++)
			{
				zoneCodeDOB = (ZoneCodeMasterDOB)failureList.get(l);
				out.print(zoneCodeDOB.getRowId()+"\t");
				out.print(("1".equalsIgnoreCase(zoneCodeDOB.getShipmentMode())?"Air":"Sea")+"\t");
				out.print((zoneCodeDOB.getConsoleType()!=null?zoneCodeDOB.getConsoleType():"")+"\t");
				out.print(zoneCodeDOB.getOriginLocation()+"\t");
				out.print(zoneCodeDOB.getTerminalId()+"\t");
				out.print(zoneCodeDOB.getCity()+"\t");
				out.print(zoneCodeDOB.getState()+"\t");
				out.print(zoneCodeDOB.getZipCode()+"\t");
//				out.print(zoneCodeDOB.getPort()+"\t");
				out.println(zoneCodeDOB.getRemarks()!=null?zoneCodeDOB.getRemarks():"");
			}
			if(failureList.size() > 0){						
				out.println();
				out.print("Row Id\t");
				out.print("Alpha-Numeric  Code \t");
				out.print("From Zipcode\t");
				out.print("To Zipcode\t");
				out.print("Zone\t" );
				out.print("Estimated Time \t");
				out.print("Estimated Distance\t");
				out.println("Remarks");
			}
			for(int j = 0;j<failureList.size();j++)
			{
				zoneCodeDOB = (ZoneCodeMasterDOB)failureList.get(j);
			    zoneCodeList = (ArrayList)zoneCodeDOB.getZoneCodeList();
				for(int k=0;k<zoneCodeList.size();k++)
				{
					zoneCodeChildDOB = (ZoneCodeChildDOB)zoneCodeList.get(k);
					out.print(zoneCodeDOB.getRowId()+"\t");
					out.print(zoneCodeChildDOB.getAlphaNumaric()+"\t");
					out.print(zoneCodeChildDOB.getFromZipCode()+"\t");
					out.print(zoneCodeChildDOB.getToZipCode()+"\t");
					out.print(zoneCodeChildDOB.getZone()+"\t");
					out.print(zoneCodeChildDOB.getEstimationTime()+"\t");
					out.print(zoneCodeChildDOB.getEstimatedDistance()+"\t");
					out.println(zoneCodeChildDOB.getRemarks()!=null?zoneCodeChildDOB.getRemarks():"");
				}
			}
		}
	}
	catch(FoursoftException fs){
		//exp.printStackTrace();
		//Logger.error(FILENAME,"Error in ETCCountryUploadProcess.jsp file ",exp.toString());
		//logger.error(FILENAME+"Error in ETCCountryUploadProcess.jsp file "+exp.toString());
			 
		keyValueList       = new ArrayList(3);
		message = new ErrorMessage(fs.getMessage(),"QMSZoneCodeMasterUploadIndex.jsp?Operation=UpLoad");
		keyValueList.add(new KeyValue("ErrorCode","INF")); 	
		keyValueList.add(new KeyValue("Operation",operation)); 	
		//keyValueList.add(new KeyValue("Type","Country"));
		message.setKeyValueList(keyValueList);
             
		request.setAttribute("ErrorMessage",message); 
%>
			<jsp:forward page="QMSESupplyErrorPage.jsp"/>
<%
	}
	catch(Exception exp){
		exp.printStackTrace();
		//Logger.error(FILENAME,"Error in ETCCountryUploadProcess.jsp file ",exp.toString());
		logger.error(FILENAME+"Error in ETCCountryUploadProcess.jsp file "+exp.toString());
			 
		keyValueList       = new ArrayList(3);
		message = new ErrorMessage("Process could not be completed.Please try again","QMSZoneCodeMasterUploadIndex.jsp?Operation=UpLoad");
		keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
		keyValueList.add(new KeyValue("Operation",operation)); 	
		keyValueList.add(new KeyValue("Type","Country"));
		message.setKeyValueList(keyValueList);
             
		request.setAttribute("ErrorMessage",message); 
%>
			<jsp:forward page="QMSESupplyErrorPage.jsp"/>
<%
	}
%>	


