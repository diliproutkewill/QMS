<%-- 
% Copyright (c) 1999-2005 by FourSoft, Pvt Ltd. All Rights Reserved.
% This software is the proprietary information of FourSoft, Pvt Ltd.
% Use is subject to license terms.
%
% esupply - v 1.x 
%
--%>
<%--
% File			  	: ETCPortUploadProcess.jsp
% Sub-module		: Port Upload
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
				 com.foursoft.etrans.sea.setup.port.bean.PortMasterJSPBean,
				 com.foursoft.esupply.common.java.KeyValue" %>
<%!
  private static Logger logger = null;
%>		
		 
<%
  
	ArrayList keyValueList   = null;
	
	String FILENAME="ETCPortUploadProcess.jsp";  
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
    String portId = "";
    String portName = "";
    String countryId = "";
    String description = "";
	PortMasterJSPBean portMasterDOB = null;
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
	 //Logger.info(FILENAME,"data ::"+data);
	 
	 int i=1;
	 while(!data.trim().equals("")){  
		data+=",";
		String tempString="";
		int beginIndex=0;
		for(int noOfCols=0;noOfCols<4; noOfCols++) 
		{
			//Logger.info(FILENAME,"datadata::"+data);
			int delimIndex=data.indexOf(",");
			if(delimIndex<=0){
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
			portId 	= strToken.nextToken();
			if(portId!=null && !portId.trim().equals("")){
				portId = portId.toUpperCase();
				if(portId.length() > 15)
					remarks = remarks + "Max Length Must Be Less Than 15 (Port Id). ";
			}else
				remarks = "Mandatory fields are not provided (Port Id). ";

			portName	=strToken.nextToken();
			if(portName !=null && !portName.trim().equals("")){
				portName = portName.toUpperCase();
				if(portName.length() > 30)
					remarks = remarks + "Max Length Must Be Less Than 30 (Port Name). ";
			}else
				remarks = remarks +"Mandatory fields are not provided (Port Name). ";
			countryId = strToken.nextToken();	
			if(countryId !=null && !countryId.trim().equals("")){
				countryId = countryId.toUpperCase();
				if(countryId.length() != 2)
					remarks = remarks + "Max Length Must Be 2 (Country Id). ";
			}else
				remarks = remarks +"Mandatory fields are not provided (Country Id). ";
			description = strToken.nextToken();	
			if(description !=null && !description.trim().equals(""))
				description = description.toUpperCase();
						
			portMasterDOB = new PortMasterJSPBean();
			portMasterDOB.setPortId(portId);
			portMasterDOB.setPortName(portName);
			portMasterDOB.setCountryId(countryId);
			portMasterDOB.setDescription(description);

			if(remarks.equals("")){
				portMasterDOB.setRemarks("");
				successList.add(portMasterDOB);
			}else{
				portMasterDOB.setRemarks(remarks);
				failureList.add(portMasterDOB);
			}
		}//End For While
			data=br.readLine();
			
			if (data==null){
				break;
			}
			remarks = "";
			i++;
	}
	
		HashMap finalMap = new HashMap(2,2);
		if(successList.size() > 0){
			InitialContext initial = new InitialContext();
			SetUpSessionHome 	home	=	(SetUpSessionHome)initial.lookup("SetUpSessionBean");
			SetUpSession 		remote	=	(SetUpSession)home.create();
			finalMap = remote.uploadPortMasterDetails(successList,process.equalsIgnoreCase("ADD")?true:false);
		}else if(successList.size() == 0 && failureList.size() == 0){
			keyValueList       = new ArrayList(3);
			message = new ErrorMessage("No Data Found In The Uploaded File","ETCUploadIndex.jsp");
			keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
			keyValueList.add(new KeyValue("Operation",request.getParameter("Operation"))); 	
			keyValueList.add(new KeyValue("Type","Port"));
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
			if(newList.size() > 0)
				successErrMsg = " The Following Id(s) Are Uploaded Successfully : \n";
			for(int j = 0;j<newList.size();j++){
				portMasterDOB = (PortMasterJSPBean)newList.get(j);
				if(j!=newList.size()-1)
					successErrMsg = successErrMsg + portMasterDOB.getPortId() + ",";
				else
					successErrMsg = successErrMsg + portMasterDOB.getPortId() + " . \n";
			}
			
			if(exList.size() > 0 || failureList.size() >0)
				failureErrMsg = " The Following Id(s) Couldn't Be Uploaded, Due To Below Reasons : \n";
			for(int k = 0;k<exList.size();k++){
				portMasterDOB = (PortMasterJSPBean)exList.get(k);
				failureErrMsg = failureErrMsg + portMasterDOB.getPortId() + " " + portMasterDOB.getRemarks() +".\n";
			}

			for(int l = 0;l<failureList.size();l++){
				portMasterDOB = (PortMasterJSPBean)failureList.get(l);
				failureErrMsg = failureErrMsg + portMasterDOB.getPortId() + " " + portMasterDOB.getRemarks() +".\n";
			}
				 
			keyValueList       = new ArrayList(3);
			message = new ErrorMessage(successErrMsg + failureErrMsg ,"ETCUploadIndex.jsp");
			keyValueList.add(new KeyValue("ErrorCode","MSG")); 	
			keyValueList.add(new KeyValue("Operation",operation)); 	
			keyValueList.add(new KeyValue("Type","Port"));
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
				out.print("Port Id:\t");
				out.print("Port Name:\t");
				out.print("Country Id:\t");
				out.print("Description:\t");
				out.println("Remarks:");
			}
			for(int j = 0;j<newList.size();j++){
				portMasterDOB = (PortMasterJSPBean)newList.get(j);
				out.print(j+1+"\t");
				out.print(portMasterDOB.getPortId()+"\t");
				out.print(portMasterDOB.getPortName()+"\t");
				out.print(portMasterDOB.getCountryId()+"\t");
				out.print(portMasterDOB.getDescription()+"\t");
				out.println("Successfully Uploaded ");
			}
			
			if(exList.size() > 0 || failureList.size() >0){
				out.println();
				failureErrMsg = " The Following Id(s) Couldn't Be Uploaded, Due To Below Reasons : ";
				out.print("Sl.No:\t");
				out.print("Port Id:\t");
				out.print("Port Name:\t");
				out.print("Country Id:\t");
				out.print("Description:\t");
				out.println("Remarks:");
			}
			
			for(int k = 0;k<exList.size();k++){
				portMasterDOB = (PortMasterJSPBean)exList.get(k);
				out.print((k+1)+"\t");
				out.print(portMasterDOB.getPortId()+"\t");
				out.print(portMasterDOB.getPortName()+"\t");
				out.print(portMasterDOB.getCountryId()+"\t");
				out.print(portMasterDOB.getDescription()+"\t");
				out.println(portMasterDOB.getRemarks());
			}

			for(int l = 0;l<failureList.size();l++){
				portMasterDOB = (PortMasterJSPBean)failureList.get(l);
				out.print(l+1+"\t");
				out.print(portMasterDOB.getPortId()+"\t");
				out.print(portMasterDOB.getPortName()+"\t");
				out.print(portMasterDOB.getCountryId()+"\t");
				out.print(portMasterDOB.getDescription()+"\t");
				out.println(portMasterDOB.getRemarks());
			}
		}
	}catch(Exception exp){

		//Logger.error(FILENAME,"Error in ETCCountryUploadProcess.jsp file ",exp.toString());
    logger.error(FILENAME+"Error in ETCCountryUploadProcess.jsp file "+exp.toString());
		exp.printStackTrace();	 

		keyValueList       = new ArrayList(3);
		message = new ErrorMessage("Process could not be completed.Please try again","ETCUploadIndex.jsp");
		keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
		keyValueList.add(new KeyValue("Operation",operation)); 	
		keyValueList.add(new KeyValue("Type","Port"));
		message.setKeyValueList(keyValueList);
             
		request.setAttribute("ErrorMessage",message); 
%>
			<jsp:forward page="../ESupplyErrorPage.jsp"/>
<%
	}
%>	


