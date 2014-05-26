<%-- 
% Copyright (c) 1999-2005 by FourSoft, Pvt Ltd. All Rights Reserved.
% This software is the proprietary information of FourSoft, Pvt Ltd.
% Use is subject to license terms.
%
% esupply - v 1.x 
%
--%>
<%--
% File			  	: ETCCommodityUploadProcess.jsp
% Sub-module		: Commodity Upload
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
				 com.foursoft.etrans.setup.commodity.bean.CommodityJspBean,
				 com.foursoft.esupply.common.java.KeyValue" %>
				 
         
  <%!
    private static Logger logger = null;
  %>
<%
	
	ArrayList keyValueList   = null;
	
	String FILENAME="ETCCommodityUploadProcess.jsp";  
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
    String commodityId = "";
    String commodityDescription = "";
    String commodityHandlingInfo = "";
    String commodityType = "";
    String hazardIndicator = "";
	String subClass="";
	String unNumber="";
	String classType="";
	String terminalId="";
	CommodityJspBean commodityMasterDOB = null;
	HashMap	checkMap	=	new HashMap();
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
		for(int noOfCols=0;noOfCols<8; noOfCols++) {
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
				if(noOfCols==4 ){
				    tempString=tempString + " ,";
				}
				if(noOfCols==5) {
					tempString=tempString + " ,";
				}
				if(noOfCols==6) {
					tempString=tempString + " ,";
				}
				if(noOfCols==7) {
					tempString=tempString + " ,";
				}
				if(noOfCols==8) {
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
			commodityId 	= strToken.nextToken();
			if(commodityId!=null && !commodityId.trim().equals(""))
				commodityId = commodityId.toUpperCase();
			else
				remarks = "Mandatory fields are not provided (Commodity Id). ";
			
			if(checkMap.containsKey(commodityId))
				remarks = " Commodity Id is Duplicate. " ;
			else
				checkMap.put(commodityId,"");

			commodityDescription	=strToken.nextToken();
			if(commodityDescription !=null && !commodityDescription.trim().equals(""))
				commodityDescription = commodityDescription.toUpperCase();
			else
				remarks = remarks +"Mandatory fields are not provided (Description). ";
			commodityHandlingInfo = strToken.nextToken();	
			if(commodityHandlingInfo !=null && !commodityHandlingInfo.trim().equals(""))
				commodityHandlingInfo = commodityHandlingInfo.toUpperCase();
			
			//Logger.info("Commodity Upload Process","commodityHandlingInfo::"+commodityHandlingInfo);

			commodityType = strToken.nextToken();	
			if(commodityType !=null && !commodityType.trim().equals(""))
				commodityType = commodityType.toUpperCase();
			else
				remarks = remarks +"Mandatory fields are not provided (Commodity Type). ";
			
			if(!commodityType.equalsIgnoreCase("1") && !commodityType.equalsIgnoreCase("2") && !commodityType.equalsIgnoreCase("3") && !commodityType.equalsIgnoreCase("4") && !commodityType.equalsIgnoreCase("5") && !commodityType.equalsIgnoreCase("6") && !commodityType.equalsIgnoreCase("7") && !commodityType.equalsIgnoreCase("8")&& !commodityType.equalsIgnoreCase("9") && !commodityType.equalsIgnoreCase("10"))
				remarks = remarks +" Must Contain Valid Values As Specified In Sample Format (Commodity Type). ";
			hazardIndicator = strToken.nextToken();
			if(hazardIndicator !=null && !hazardIndicator.trim().equals("")){
				hazardIndicator = hazardIndicator.toUpperCase();
				if(!hazardIndicator.equalsIgnoreCase("Y") && !hazardIndicator.equalsIgnoreCase("N") && !hazardIndicator.equals(""))
				remarks = remarks +" Must Contain Valid Values As Specified In Sample Format (Hazard Indicator). ";

				subClass=strToken.nextToken();
				if(subClass !=null && !subClass.trim().equals(""))
					subClass = subClass.toUpperCase();
			
				classType=strToken.nextToken();
				if(classType !=null && !classType.trim().equals(""))
					classType = classType.toUpperCase();
          
          unNumber=strToken.nextToken();
				if(unNumber !=null && !unNumber.trim().equals(""))
					unNumber = unNumber.toUpperCase();
			
			}
			
			commodityMasterDOB = new CommodityJspBean();
			commodityMasterDOB.setCommodityId(commodityId);
			commodityMasterDOB.setCommodityDescription(commodityDescription);
			commodityMasterDOB.setCommodityHandlingInfo(commodityHandlingInfo);
			try{
				switch (Integer.parseInt(commodityType)){
					case 1: commodityType="Edible,Animal & Vegetable Products";break;
					case 2: commodityType="AVI,Inedible Animal & Vegetable Products";break;
					case 3: commodityType="Textiles,Fibres & Mfrs.";break;
					case 4: commodityType="Metals,Mfrs Excl M/C , Vehicles & Electrical Equipment";break;
					case 5: commodityType="Machinery,Vehicles & Electrical Equipment";break;
					case 6: commodityType="Non-Metallic Minerals & Mfrs.";break;
					case 7: commodityType="Chemicals & Related Products.";break;
					case 8: commodityType="Paper,Reed,Rubber & Wood Mfrs.";break;
					case 9: commodityType="Scientific,Professional & Precision Instruments";break;
					case 10: commodityType="Miscellaneous";break;
					default: break;
				}
			}catch(Exception ex){
				//Logger.error("FILENAME","Wrong Data enetered For Commodity Type");
        logger.error("FILENAME"+"Wrong Data enetered For Commodity Type");
			}
			commodityMasterDOB.setCommodityType(commodityType);
			commodityMasterDOB.setHazardIndicator(hazardIndicator);
			if("Y".equalsIgnoreCase(hazardIndicator))
			{
				if(subClass==null || (subClass!=null && subClass.trim().length()==0))
					remarks	= remarks + " \nSub Class Cannot Be Empty if Commodity is Specified as Hazardous.";
				if(unNumber==null || (unNumber!=null && unNumber.trim().length()==0))
					remarks	= remarks + " \nUN Number Cannot Be Empty if Commodity is Specified as Hazardous ";

			}
			commodityMasterDOB.setSubClass(subClass);
			commodityMasterDOB.setUnNumber(unNumber);
			commodityMasterDOB.setClassType(classType);
			commodityMasterDOB.setTerminalId(loginbean.getTerminalId());
			
			if(remarks.equals("")){
				commodityMasterDOB.setRemarks("");
				successList.add(commodityMasterDOB);
			}else{
				commodityMasterDOB.setRemarks(remarks);
				failureList.add(commodityMasterDOB);
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
			finalMap = remote.uploadCommodityMasterDetails(successList,process.equalsIgnoreCase("ADD")?true:false);
		}else if(successList.size() == 0 && failureList.size() == 0){
			keyValueList       = new ArrayList(3);
			message = new ErrorMessage("No Data Found In The Uploaded File","ETCUploadIndex.jsp");
			keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
			keyValueList.add(new KeyValue("Operation",request.getParameter("Operation"))); 	
			keyValueList.add(new KeyValue("Type","Commodity"));
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
				commodityMasterDOB = (CommodityJspBean)newList.get(j);
				if(j!=newList.size()-1)
					successErrMsg = successErrMsg + commodityMasterDOB.getCommodityId() + ",";
				else
					successErrMsg = successErrMsg + commodityMasterDOB.getCommodityId() + " . \n";
			}
			
			if(exList.size() > 0 || failureList.size() >0)
				failureErrMsg = " The Following Id(s) Couldn't Be Uploaded, Due To Below Reasons : \n";
			for(int k = 0;k<exList.size();k++){
				commodityMasterDOB = (CommodityJspBean)exList.get(k);
				failureErrMsg = failureErrMsg + commodityMasterDOB.getCommodityId() + " " + commodityMasterDOB.getRemarks() +".\n";
			}

			for(int l = 0;l<failureList.size();l++){
				commodityMasterDOB = (CommodityJspBean)failureList.get(l);
				failureErrMsg = failureErrMsg + commodityMasterDOB.getCommodityId() + " " + commodityMasterDOB.getRemarks() +".\n";
			}
				 
			keyValueList       = new ArrayList(3);
			message = new ErrorMessage(successErrMsg + failureErrMsg ,"ETCUploadIndex.jsp");
			keyValueList.add(new KeyValue("ErrorCode","MSG")); 	
			keyValueList.add(new KeyValue("Operation",request.getParameter("Operation"))); 	
			keyValueList.add(new KeyValue("Type","Commodity"));
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
				out.print("Commodity Id:\t");
				out.print("Description:\t");
				out.print("Handling Info:\t");
				out.print("Commodity Type:\t");
				out.print("Harzard Indicator:\t");
				out.println("Remarks:");
			}
			for(int j = 0;j<newList.size();j++){
				commodityMasterDOB = (CommodityJspBean)newList.get(j);
				out.print(j+1+"\t");
				out.print(commodityMasterDOB.getCommodityId()+"\t");
				out.print(commodityMasterDOB.getCommodityDescription()+"\t");
				out.print(commodityMasterDOB.getCommodityHandlingInfo()+"\t");
				out.print(commodityMasterDOB.getCommodityType()+"\t");
				out.print(commodityMasterDOB.getHazardIndicator()+"\t");
				out.println("Successfully Uploaded ");
			}
			
			if(exList.size() > 0 || failureList.size() >0){
				out.println();
				failureErrMsg = " The Following Id(s) Couldn't Be Uploaded, Due To Below Reasons : ";
				out.print("Sl.No:\t");
				out.print("Commodity Id:\t");
				out.print("Description:\t");
				out.print("Handling Info:\t");
				out.print("Commodity Type:\t");
				out.print("Harzard Indicator:\t");
				out.println("Remarks:");
			}
			
			for(int k = 0;k<exList.size();k++){
				commodityMasterDOB = (CommodityJspBean)exList.get(k);
				out.print((k+1)+"\t");
				out.print(commodityMasterDOB.getCommodityId()+"\t");
				out.print(commodityMasterDOB.getCommodityDescription()+"\t");
				out.print(commodityMasterDOB.getCommodityHandlingInfo()+"\t");
				out.print(commodityMasterDOB.getCommodityType()+"\t");
				out.print(commodityMasterDOB.getHazardIndicator()+"\t");
				out.println(commodityMasterDOB.getRemarks());
			}

			for(int l = 0;l<failureList.size();l++){
				commodityMasterDOB = (CommodityJspBean)failureList.get(l);
				out.print(l+1+"\t");
				out.print(commodityMasterDOB.getCommodityId()+"\t");
				out.print(commodityMasterDOB.getCommodityDescription()+"\t");
				out.print(commodityMasterDOB.getCommodityHandlingInfo()+"\t");
				out.print(commodityMasterDOB.getCommodityType()+"\t");
				out.print(commodityMasterDOB.getHazardIndicator()+"\t");
				out.println(commodityMasterDOB.getRemarks());
			}
		}
	}catch(Exception exp){
		//Logger.error(FILENAME,"Error in ETCCommodityUploadProcess.jsp file ",exp.toString());
    logger.error(FILENAME+"Error in ETCCommodityUploadProcess.jsp file "+exp.toString());
		exp.printStackTrace();	 
		keyValueList       = new ArrayList(3);
		message = new ErrorMessage("Process could not be completed.Please try again","ETCUploadIndex.jsp");
		keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
		keyValueList.add(new KeyValue("Operation",operation)); 	
		keyValueList.add(new KeyValue("Type","Commodity"));
		message.setKeyValueList(keyValueList);
             
		request.setAttribute("ErrorMessage",message); 
%>
			<jsp:forward page="../ESupplyErrorPage.jsp"/>
<%
	}
%>	


