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
				 com.foursoft.esupply.common.java.CountryMasterDOB,
				 com.foursoft.esupply.common.java.KeyValue" %>				 
<%!
  private static Logger logger = null;
%>
<%
	
	ArrayList keyValueList   = null;
	
	String FILENAME="ETCCountryUploadProcess.jsp";  
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
    String countryId = "";
    String countryName = "";
    String currencyId = "";
    String region = "";
    String area = "";
	CountryMasterDOB countryMasterDOB = null;
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
		for(int noOfCols=0;noOfCols<5; noOfCols++) {
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
			countryId 	= strToken.nextToken();
			if(countryId!=null && !countryId.trim().equals("")){
				countryId = countryId.toUpperCase();
				if(countryId.length() != 2)
					remarks = remarks + "Max Length Must Be 2 (Country Id). ";
			}else
				remarks = remarks + "Mandatory fields are not provided (Country Id). ";

			countryName	=strToken.nextToken();
			if(countryName !=null && !countryName.trim().equals("")){
				countryName = countryName.toUpperCase();
				if(countryName.length() > 30)
					remarks = remarks + "Max Length Must Be Less Than 30 (Country Name). ";
			}else
				remarks = remarks +"Mandatory fields are not provided (Country Name). ";
			
			currencyId = strToken.nextToken();	
			//System.out.println("currencyId   "+currencyId);
			if(currencyId !=null && !currencyId.trim().equals("")){
				currencyId = currencyId.toUpperCase();
				//System.out.println("currencyId   "+currencyId.length());
				if(currencyId.length() != 3)
					remarks = remarks + "Max Length Must Be 3 (Currency Id). ";
			}else
				remarks = remarks +"Mandatory fields are not provided (Currency Id). ";
			region = strToken.nextToken();	
			if(region !=null && !region.trim().equals(""))
				region = region.toUpperCase();
			else
				remarks = remarks +"Mandatory fields are not provided (Region). ";
			//@@ Modified  by subrahmanyam for the adding of regions as requested by KIM by adding "SAP" & "NAP" IN THE FOLLOWING CONDITION on 18-jan-10.  
			if(!region.equalsIgnoreCase("ASPA") && !region.equalsIgnoreCase("AMNO") && !region.equalsIgnoreCase("AMLA") && !region.equalsIgnoreCase("EURE") && !region.equalsIgnoreCase("EMA")  && !region.equalsIgnoreCase("OTHER") )
				remarks = remarks +" Must Contain Valid Values As Specified In Sample Format (Region). ";
			area = strToken.nextToken();	
			if(area !=null && !area.trim().equals("")){
				area = area.toUpperCase();
				if(!area.equalsIgnoreCase("Area1") && !area.equalsIgnoreCase("Area2") && !area.equalsIgnoreCase("Area3") && !area.equals(""))
				remarks = remarks +" Must Contain Valid Values As Specified In Sample Format (Area). ";
			}
			
			countryMasterDOB = new CountryMasterDOB(countryId,countryName,currencyId,region,area);
			
			if(remarks.equals("")){
				countryMasterDOB.setRemarks("");
				successList.add(countryMasterDOB);
			}else{
				countryMasterDOB.setRemarks(remarks);
				failureList.add(countryMasterDOB);
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
			finalMap = remote.uploadCountryMasterDetails(successList,process.equalsIgnoreCase("ADD")?true:false);
		}else if(successList.size() == 0 && failureList.size() == 0){
			keyValueList       = new ArrayList(3);
			message = new ErrorMessage("No Data Found In The Uploaded File","ETCUploadIndex.jsp");
			keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
			keyValueList.add(new KeyValue("Operation",request.getParameter("Operation"))); 	
			keyValueList.add(new KeyValue("Type","Country"));
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
				countryMasterDOB = (CountryMasterDOB)newList.get(j);
				if(j!=newList.size()-1)
					successErrMsg = successErrMsg + countryMasterDOB.getCountryId() + ",";
				else
					successErrMsg = successErrMsg + countryMasterDOB.getCountryId() + " . \n";
			}
			
			if(exList.size() > 0 || failureList.size() >0)
				failureErrMsg = " The Following Id(s) Couldn't Be Uploaded, Due To Below Reasons : \n";
			for(int k = 0;k<exList.size();k++){
				countryMasterDOB = (CountryMasterDOB)exList.get(k);
				failureErrMsg = failureErrMsg + countryMasterDOB.getCountryId() + " " + countryMasterDOB.getRemarks() +".\n";
			}

			for(int l = 0;l<failureList.size();l++){
				countryMasterDOB = (CountryMasterDOB)failureList.get(l);
				failureErrMsg = failureErrMsg + countryMasterDOB.getCountryId() + " " + countryMasterDOB.getRemarks() +".\n";
			}
				 
			keyValueList       = new ArrayList(3);
			message = new ErrorMessage(successErrMsg + failureErrMsg ,"ETCUploadIndex.jsp");
			keyValueList.add(new KeyValue("ErrorCode","MSG")); 	
			keyValueList.add(new KeyValue("Operation",request.getParameter("Operation"))); 	
			keyValueList.add(new KeyValue("Type","Country"));
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
				out.print("Country Id:\t");
				out.print("Country Name:\t");
				out.print("Currency Id:\t");
				out.print("Region:\t");
				out.print("Area:\t");
				out.println("Remarks:");
			}
			for(int j = 0;j<newList.size();j++){
				countryMasterDOB = (CountryMasterDOB)newList.get(j);
				out.print(j+1+"\t");
				out.print(countryMasterDOB.getCountryId()+"\t");
				out.print(countryMasterDOB.getCountryName()+"\t");
				out.print(countryMasterDOB.getCurrencyId()+"\t");
				out.print(countryMasterDOB.getRegion()+"\t");
				out.print(countryMasterDOB.getArea()+"\t");
				out.println("Successfully Uploaded ");
			}
			
			if(exList.size() > 0 || failureList.size() >0){
				out.println();
				failureErrMsg = " The Following Id(s) Couldn't Be Uploaded, Due To Below Reasons : ";
				out.print("Sl.No:\t");
				out.print("Country Id:\t");
				out.print("Country Name:\t");
				out.print("Currency Id:\t");
				out.print("Region:\t");
				out.print("Area:\t");
				out.println("Remarks:");
			}
			
			for(int k = 0;k<exList.size();k++){
				countryMasterDOB = (CountryMasterDOB)exList.get(k);
				out.print((k+1)+"\t");
				out.print(countryMasterDOB.getCountryId()+"\t");
				out.print(countryMasterDOB.getCountryName()+"\t");
				out.print(countryMasterDOB.getCurrencyId()+"\t");
				out.print(countryMasterDOB.getRegion()+"\t");
				out.print(countryMasterDOB.getArea()+"\t");
				out.println(countryMasterDOB.getRemarks());
			}

			for(int l = 0;l<failureList.size();l++){
				countryMasterDOB = (CountryMasterDOB)failureList.get(l);
				out.print(l+1+"\t");
				out.print(countryMasterDOB.getCountryId()+"\t");
				out.print(countryMasterDOB.getCountryName()+"\t");
				out.print(countryMasterDOB.getCurrencyId()+"\t");
				out.print(countryMasterDOB.getRegion()+"\t");
				out.print(countryMasterDOB.getArea()+"\t");
				out.println(countryMasterDOB.getRemarks());
			}
		}
	}catch(Exception exp){
		//Logger.error(FILENAME,"Error in ETCCountryUploadProcess.jsp file ",exp.toString());
    logger.error(FILENAME+"Error in ETCCountryUploadProcess.jsp file "+exp.toString());
			 
		keyValueList       = new ArrayList(3);
		message = new ErrorMessage("Process could not be completed.Please try again","ETCUploadIndex.jsp");
		keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
		keyValueList.add(new KeyValue("Operation",operation)); 	
		keyValueList.add(new KeyValue("Type","Country"));
		message.setKeyValueList(keyValueList);
             
		request.setAttribute("ErrorMessage",message); 
%>
			<jsp:forward page="../ESupplyErrorPage.jsp"/>
<%
	}
%>	


