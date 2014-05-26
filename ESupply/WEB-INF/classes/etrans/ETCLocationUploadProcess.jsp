<%-- 
% Copyright (c) 1999-2005 by FourSoft, Pvt Ltd. All Rights Reserved.
% This software is the proprietary information of FourSoft, Pvt Ltd.
% Use is subject to license terms.
%
% esupply - v 1.x 
%
--%>
<%--
% File			  	: ETcCountryUploadProcess.jsp
% Sub-module		: Country Upload
% Module		  	: QMS
%
%
% author			: Ravi Kumar
% date				: 06-07-2005
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
                 java.io.File,
                 java.util.Iterator,
				 java.util.ListIterator,
				 java.util.StringTokenizer,
				 javax.naming.InitialContext,
				 com.qms.setup.ejb.sls.SetUpSession,
                 com.qms.setup.ejb.sls.SetUpSessionHome,
				 org.apache.log4j.Logger,
				 com.foursoft.esupply.common.java.ErrorMessage,
				 com.foursoft.etrans.setup.location.bean.LocationMasterJspBean,
				 com.foursoft.esupply.common.java.KeyValue" %>
				 
<%!
  private static Logger logger = null;
%>
         
<%
	
	ArrayList keyValueList   = null;
	
	String FILENAME="ETCLocationUploadProcess.jsp";  
  logger  = Logger.getLogger(FILENAME);	
	int count;
	String data = "";
	java.util.ArrayList exList = new java.util.ArrayList(5);
	java.util.ArrayList newList = new java.util.ArrayList(5);
	java.util.ArrayList successList = new java.util.ArrayList(5);
	java.util.ArrayList failureList = new java.util.ArrayList(5); 
	FileInputStream fileRead=null;
	
	//Hashtable	hashTable			    = new Hashtable();
	ErrorMessage message				= null;
	KeyValue			keyValue			= null;
	ArrayList			list			  	= null;
	String fileName = "";
	String process	= "";
	String operation = "";
	String type		= "";
	String errorMode = "";
    String locationId = "";
    String locationName = "";
    String city = "";
	String zipCode = "";
    String countryId = "";
    String shipmentMode = "";
	LocationMasterJspBean locationMasterDOB = null;
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
	 
	 data=br.readLine();
	 
	 data=br.readLine();
	 
	 int i=1;
	 while(!data.equals("")){  
		data+=",";
		String tempString="";
		int beginIndex=0;
		for(int noOfCols=0;noOfCols<6; noOfCols++) {
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
            }else{  
				
				String subString=data.substring(beginIndex,delimIndex);
				tempString=tempString + subString+",";
				
			} 
		    data=data.substring(delimIndex+1);
			beginIndex=0;
		}//for
		data=tempString.substring(0,tempString.length());
		StringTokenizer strToken = new StringTokenizer(data,",");
		String			remarks = "";
		    
		while(strToken.hasMoreElements()){
			locationId 	= strToken.nextToken();
			if(locationId!=null && !locationId.trim().equals("")){
				locationId = locationId.toUpperCase();
				if(locationId.length() > 3)
					remarks = remarks + "Max Length Must Be 3 (Location Id). ";
			}else
				remarks = "Mandatory fields are not provided (Location Id). ";

			locationName	=strToken.nextToken();
			if(locationName !=null && !locationName.trim().equals(""))
				locationName = locationName.toUpperCase();
			else
				remarks = remarks +"Mandatory fields are not provided (Location Name). ";
			countryId 	= strToken.nextToken();
			if(countryId!=null && !countryId.trim().equals("")){
				countryId = countryId.toUpperCase();
				if(countryId.length() != 2)
					remarks = remarks + "Max Length Must Be 2 (Country Id). ";
			}else
				remarks = remarks + "Mandatory fields are not provided (Country Id). ";	
			city = strToken.nextToken();	
			if(city !=null && !city.trim().equals(""))
				city = city.toUpperCase();
			else
				remarks = remarks +"Mandatory fields are not provided (City). ";
			zipCode = strToken.nextToken();	
			if(zipCode !=null && !zipCode.trim().equals("")){
				zipCode = zipCode.toUpperCase();
				try{
				if(new Double(zipCode).isNaN())
					remarks = remarks + "";
				}catch(Exception ex){
					remarks = remarks + "Zip Code Must Contains Only Digits. ";
				}
			}
			
			
			shipmentMode = strToken.nextToken();	
			if(shipmentMode ==null && shipmentMode.trim().equals(""))
				remarks = remarks + "Mandatory fields are not provided (Shipment Mode). ";	 
			if(!shipmentMode.equalsIgnoreCase("1") && !shipmentMode.equalsIgnoreCase("2") && !shipmentMode.equalsIgnoreCase("3") && !shipmentMode.equalsIgnoreCase("4") && !shipmentMode.equalsIgnoreCase("5")&& !shipmentMode.equalsIgnoreCase("6") && !shipmentMode.equalsIgnoreCase("7") && !shipmentMode.equals(""))
				remarks = remarks +" Must Contain Valid Values As Specified In Sample Format (Shipment Mode). ";
			
			
			locationMasterDOB = new LocationMasterJspBean();
			locationMasterDOB.setLocationId(locationId);
			locationMasterDOB.setLocationName(locationName);
			locationMasterDOB.setCity(city);
			locationMasterDOB.setZipCode(zipCode);
			locationMasterDOB.setCountryId(countryId);
			locationMasterDOB.setShipmentMode(shipmentMode);
			if(remarks.equals("")){
				locationMasterDOB.setRemarks("");
				successList.add(locationMasterDOB);
			}else{
				locationMasterDOB.setRemarks(remarks);
				failureList.add(locationMasterDOB);
			}
		}//End For While
			data=br.readLine();
			
			if (data==null){
				break;
			}
			remarks = "";
			i++;
	}
		//System.out.println("10 "+failureList.size());
		HashMap finalMap = new HashMap(2,2);
		if(successList.size() > 0){
			InitialContext initial = new InitialContext();
			SetUpSessionHome 	home	=	(SetUpSessionHome)initial.lookup("SetUpSessionBean");
			SetUpSession 		remote	=	(SetUpSession)home.create();
			finalMap = remote.uploadLocationMasterDetails(successList,process.equalsIgnoreCase("ADD")?true:false);
		}else if(successList.size() == 0 && failureList.size() == 0){
			keyValueList       = new ArrayList(3);
			message = new ErrorMessage("No Data Found In The Uploaded File","ETCUploadIndex.jsp");
			keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
			keyValueList.add(new KeyValue("Operation",request.getParameter("Operation"))); 	
			keyValueList.add(new KeyValue("Type","Location"));
			message.setKeyValueList(keyValueList);
				 
			request.setAttribute("ErrorMessage",message); 
%>
			<jsp:forward page="../ESupplyErrorPage.jsp"/>
<%
		}
		if(successList.size() > 0){
			if(process.equalsIgnoreCase("ADD")){
				exList = (ArrayList)finalMap.get("EXISTS");
				newList = (ArrayList)finalMap.get("NONEXISTS");
			}else{
				exList = (ArrayList)finalMap.get("NONEXISTS");
				newList = (ArrayList)finalMap.get("EXISTS");
			}
		}
		
		String successErrMsg ="";
		String failureErrMsg = "";
		if(errorMode.equalsIgnoreCase("N")){
			//System.out.println("10"+newList.size());
			if(newList.size() > 0)
				successErrMsg = " The Following Id(s) Are Uploaded Successfully : \n";
			for(int j = 0;j<newList.size();j++){
				locationMasterDOB = (LocationMasterJspBean)newList.get(j);
				if(j!=newList.size()-1)
					successErrMsg = successErrMsg + locationMasterDOB.getLocationId() + ",";
				else
					successErrMsg = successErrMsg + locationMasterDOB.getLocationId() + " . \n";
			}
			
			if(exList.size() > 0 || failureList.size() >0)
				failureErrMsg = " The Following Id(s) Couldn't Be Uploaded, Due To Below Reasons : \n";
			for(int k = 0;k<exList.size();k++){
				locationMasterDOB = (LocationMasterJspBean)exList.get(k);
				failureErrMsg = failureErrMsg + locationMasterDOB.getLocationId() + " " + locationMasterDOB.getRemarks() +".\n";
			}

			for(int l = 0;l<failureList.size();l++){
				locationMasterDOB = (LocationMasterJspBean)failureList.get(l);
				failureErrMsg = failureErrMsg + locationMasterDOB.getLocationId() + " " + locationMasterDOB.getRemarks() +".\n";
			}
				 
			keyValueList       = new ArrayList(3);
			message = new ErrorMessage(successErrMsg + failureErrMsg ,"ETCUploadIndex.jsp");
			keyValueList.add(new KeyValue("ErrorCode","MSG")); 	
			keyValueList.add(new KeyValue("Operation",request.getParameter("Operation"))); 	
			keyValueList.add(new KeyValue("Type","Location"));
			message.setKeyValueList(keyValueList);
				 
			request.setAttribute("ErrorMessage",message); 
%>
				<jsp:forward page="../ESupplyErrorPage.jsp"/>
<%
		}else{
			out.clearBuffer();
			response.setContentType("application/vnd.ms-excel");	
			String contentDisposition = " :attachment;";	
			response.setHeader("Content-Disposition","attachment;filename=Exceptions.xls");
			if(newList.size() > 0){
				successErrMsg = " The Following Id(s) Are Uploaded Successfully : ";
				out.println(successErrMsg);
				out.println();
				out.print("Sl.No:\t");
				out.print("Location Id:\t");
				out.print("Location Name:\t");
				out.print("City:\t");
				out.print("Zip Code:\t");
				out.print("Country Id:\t");
				out.print("Shipment Mode:\t");
				out.println("Remarks:");
			}
			for(int j = 0;j<newList.size();j++){
				locationMasterDOB = (LocationMasterJspBean)newList.get(j);
				out.print(j+1+"\t");
				out.print(locationMasterDOB.getLocationId()+"\t");
				out.print(locationMasterDOB.getLocationName()+"\t");
				out.print(locationMasterDOB.getCity()+"\t");
				out.print(locationMasterDOB.getZipCode()+"\t");
				out.print(locationMasterDOB.getCountryId()+"\t");
				out.print(locationMasterDOB.getShipmentMode()+"\t");
				out.println("Successfully Uploaded ");
			}
			
			if(exList.size() > 0 || failureList.size() >0){
				out.println();
				failureErrMsg = " The Following Id(s) Couldn't Be Uploaded, Due To Below Reasons : ";
				out.println(failureErrMsg);
				out.print("Sl.No:\t");
				out.print("Location Id:\t");
				out.print("Location Name:\t");
				out.print("City:\t");
				out.print("Zip Code:\t");
				out.print("Country Id:\t");
				out.print("Shipment Mode:\t");
				out.println("Remarks:");
			}
			
			for(int k = 0;k<exList.size();k++){
				locationMasterDOB = (LocationMasterJspBean)exList.get(k);
				out.print((k+1)+"\t");
				out.print(locationMasterDOB.getLocationId()+"\t");
				out.print(locationMasterDOB.getLocationName()+"\t");
				out.print(locationMasterDOB.getCity()+"\t");
				out.print(locationMasterDOB.getZipCode()+"\t");
				out.print(locationMasterDOB.getCountryId()+"\t");
				out.print(locationMasterDOB.getShipmentMode()+"\t");
				out.println(locationMasterDOB.getRemarks());
			}

			for(int l = 0;l<failureList.size();l++){
				locationMasterDOB = (LocationMasterJspBean)failureList.get(l);
				out.print(l+1+"\t");
				out.print(locationMasterDOB.getLocationId()+"\t");
				out.print(locationMasterDOB.getLocationName()+"\t");
				out.print(locationMasterDOB.getCity()+"\t");
				out.print(locationMasterDOB.getZipCode()+"\t");
				out.print(locationMasterDOB.getCountryId()+"\t");
				out.print(locationMasterDOB.getShipmentMode()+"\t");
				out.println(locationMasterDOB.getRemarks());
			}
		}
	}catch(Exception exp){
		//Logger.error(FILENAME,"Error in ETCCountryUploadProcess.jsp file ",exp.toString());
    logger.error(FILENAME+"Error in ETCCountryUploadProcess.jsp file "+exp.toString());
			 
		keyValueList       = new ArrayList(3);
		message = new ErrorMessage("Process could not be completed.Please try again","ETCUploadIndex.jsp");
		keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
		keyValueList.add(new KeyValue("Operation",operation)); 	
		keyValueList.add(new KeyValue("Type","Location"));
		message.setKeyValueList(keyValueList);
             
		request.setAttribute("ErrorMessage",message); 
%>
			<jsp:forward page="../ESupplyErrorPage.jsp"/>
<%
	}
%>	


