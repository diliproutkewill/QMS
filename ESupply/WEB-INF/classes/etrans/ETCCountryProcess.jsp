<%--
% 
% Copyright (c) 1999-2001 by FourSoft, Pvt Ltd. All Rights Reserved.
% This software is the proprietary information of FourSoft, Pvt Ltd.
% Use is subject to license terms.
%
% esupply - v 1.x 
%
--%>
<%
/*	Programme Name : CountryProcessJSP.
*	Module Name    : ETrans.
*	Task Name      : Country Master 
*	Sub Task Name  : Modifying or deleting the Country,s Name alongwith it's Id,currency and it's region of existance like Europe,Asia etc..
*	Author		   : 
*	Date Started   :
*	Date Ended     : Sept 06, 2001.
*	Modified Date  : Sept 06, 2001(Ratan K.M.).
*	Description    :
*	Methods		   :
*/
%>
<%@ page import	=	"javax.naming.InitialContext,
					org.apache.log4j.Logger,
					com.qms.setup.ejb.sls.SetUpSession,
                    com.qms.setup.ejb.sls.SetUpSessionHome,
					com.foursoft.esupply.common.java.ErrorMessage,
				    com.foursoft.esupply.common.java.KeyValue,
				    java.util.ArrayList" %>
					
<jsp:useBean id ="CountryMasterObj" class= "com.foursoft.etrans.setup.country.bean.CountryMaster" scope ="request"/>
<jsp:setProperty name="CountryMasterObj" property="*"/>
<jsp:useBean id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/>
<%!
  private static Logger logger = null;
	private static final String FILE_NAME	=	"ETCCountryProcess.jsp ";
%>
<%
  logger  = Logger.getLogger(FILE_NAME);
  String strParam	= null;
  
  ErrorMessage errorMessageObject = null;
  ArrayList    keyValueList	      = null;	  
 
 try
 {
       
	   
	   InitialContext initial = new InitialContext();
	   SetUpSessionHome 	ETransHOSuperUserHome	=	(SetUpSessionHome)initial.lookup("SetUpSessionBean");
	   SetUpSession 		ETransHOSuperUserRemote	=	(SetUpSession)ETransHOSuperUserHome.create();
       strParam 												= request.getParameter("Operation");
	    String area = CountryMasterObj.getArea();
	   if(area == null)
			CountryMasterObj.setArea(request.getParameter("area"));
	   if(strParam.equals("Add"))
	   {
		 String countryId[] = CountryMasterObj.getCountryId();
		 if(ETransHOSuperUserRemote.isCountryMasterCountryAlreadyExists(countryId[0]))
		 {
			 
			 keyValueList = new ArrayList();
			 errorMessageObject = new ErrorMessage("Record already exists with Country Id :" + countryId[0],"ETCCountryAdd.jsp");
		     keyValueList.add(new KeyValue("ErrorCode","RAE")); 	
		     keyValueList.add(new KeyValue("Operation","Add")); 	
			 errorMessageObject.setKeyValueList(keyValueList);
             request.setAttribute("ErrorMessage",errorMessageObject); 
			 		 
			 /**
			 String errorMessage = "Record already exists with Country Id : "+countryId[0];
			 session.setAttribute("Operation", "Add");
			 session.setAttribute("ErrorCode","RAE");
			 session.setAttribute("ErrorMessage",errorMessage);
			 session.setAttribute("NextNavigation","ETCCountryAdd.jsp");  */
%>
			<jsp:forward page="../ESupplyErrorPage.jsp"/>
<%
		 }
		 else
		 {
	    	 ETransHOSuperUserRemote.addCountryMasterDetails(CountryMasterObj,loginbean);
			 keyValueList = new ArrayList();
			 errorMessageObject = new ErrorMessage("Record successfully added with Country Id : "+countryId[0],"ETCCountryAdd.jsp");
		     keyValueList.add(new KeyValue("ErrorCode","RSI")); 	
		     keyValueList.add(new KeyValue("Operation","Add")); 	
			 errorMessageObject.setKeyValueList(keyValueList);
             request.setAttribute("ErrorMessage",errorMessageObject); 
			 			 
			 /**
			 ETransHOSuperUserRemote.addCountryMasterDetails(CountryMasterObj,loginbean);    
			 String errorMessage = "Record successfully added with Country Id : "+countryId[0];
			 session.setAttribute("ErrorCode","RSI");
			 session.setAttribute("ErrorMessage",errorMessage);
			 session.setAttribute("NextNavigation","ETCCountryAdd.jsp");  */

%>
			<jsp:forward page="../ESupplyErrorPage.jsp"/>
<%
		 }
	   }
	   
	   if(strParam.equals("Modify"))
	   {
	       boolean 		flag 		= ETransHOSuperUserRemote.updateCountryMasterDetails(CountryMasterObj,loginbean);
		   if( flag)
	       {
			 String cou[] 		    = CountryMasterObj.getCountryId();
			 
			 keyValueList       = new ArrayList();
			 errorMessageObject = new ErrorMessage("Record successfully modified with Country Id : " + cou[0],"ETCCountryEnterId.jsp");
		     
			 keyValueList.add(new KeyValue("ErrorCode","RSM")); 	
		     keyValueList.add(new KeyValue("Operation","Modify")); 	
			 
			 errorMessageObject.setKeyValueList(keyValueList);
             
			 request.setAttribute("ErrorMessage",errorMessageObject); 
			 
			 
			 /**
			 String errorMessage 	= "Record successfully modified with Country Id : " + cou[0];
			 session.setAttribute("Operation","Modify");
			 session.setAttribute("ErrorCode","RSM");
			 session.setAttribute("ErrorMessage",errorMessage);
			 session.setAttribute("NextNavigation","ETCCountryEnterId.jsp");  */
%>
			<jsp:forward page="../ESupplyErrorPage.jsp"/>
<%
	       }
		   else
		   {
			 String cou[]		 = CountryMasterObj.getCountryId();
			 
			 
			 keyValueList       = new ArrayList();
			 errorMessageObject = new ErrorMessage("Error while modifying record Country Id : " + cou[0],"ETCCountryEnterId.jsp");
		     
			 keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
		     keyValueList.add(new KeyValue("Operation","Modify")); 	
			 
			 errorMessageObject.setKeyValueList(keyValueList);
             
			 request.setAttribute("ErrorMessage",errorMessageObject); 
			 
			 
			 /**
			 
			 String errorMessage = "Error while modifying record Country Id : " + cou[0];
			 session.setAttribute("Operation","Modify");
			 session.setAttribute("ErrorCode","ERR");
			 session.setAttribute("ErrorMessage",errorMessage);
			 session.setAttribute("NextNavigation","ETCCountryEnterId.jsp");  */
		   }
%>
			<jsp:forward page="../ESupplyErrorPage.jsp"/>
<%
	    }
	   else if(strParam.equals("Delete"))
	   {
			String[] countryId 	 = CountryMasterObj.getCountryId();
	    	boolean flag 		 = ETransHOSuperUserRemote.deleteCountryMasterDetails(countryId[0],loginbean );
			if(flag)
			{
			 
			 keyValueList       = new ArrayList();
			 errorMessageObject = new ErrorMessage("Record successfully deleted with Country Id : "+countryId[0],"ETCCountryEnterId.jsp");
		     
			 keyValueList.add(new KeyValue("ErrorCode","RSD")); 	
		     keyValueList.add(new KeyValue("Operation","Delete")); 	
			 
			 errorMessageObject.setKeyValueList(keyValueList);
             
			 request.setAttribute("ErrorMessage",errorMessageObject); 
			 
			 
			 /**
			 String errorMessage = "Record successfully deleted with Country Id : "+countryId[0];
			 session.setAttribute("Operation","Delete");
			 session.setAttribute("ErrorCode","RSD");
			 session.setAttribute("ErrorMessage",errorMessage);
			 session.setAttribute("NextNavigation","ETCCountryEnterId.jsp");  */
%>
			<jsp:forward page="../ESupplyErrorPage.jsp"/>
<%
			}
			else
			{
			 keyValueList       = new ArrayList();
			 errorMessageObject = new ErrorMessage("You are not allowed to delete this Country Id : "+countryId[0]+ ",as this is under usage","ETCCountryEnterId.jsp");
		     
			 keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
		     keyValueList.add(new KeyValue("Operation","Delete")); 	
			 
			 errorMessageObject.setKeyValueList(keyValueList);
             
			 request.setAttribute("ErrorMessage",errorMessageObject); 
			 
			 
			 
			 /**
			 String errorMessage = "You are not allowed to delete this Country Id : "+countryId[0]+ ",this Id under usage";
			 session.setAttribute("Operation","Delete");
			 session.setAttribute("ErrorCode","ERR");
			 session.setAttribute("ErrorMessage",errorMessage);
			 session.setAttribute("NextNavigation","ETCCountryEnterId.jsp");  */
%>
			<jsp:forward page="../ESupplyErrorPage.jsp"/>
<%
			}
        }
            if(strParam.equals("Invalidate"))

	 {
		   java.util.ArrayList dobList=(ArrayList)session.getAttribute("dobList");
		   String invalidater[]=request.getParameterValues("checkBoxValue");
		   String currencyInvalidate[]=request.getParameterValues("currecnycheckBoxValue");
            boolean flag=false;
		   for(int i=0;i<dobList.size();++i)
		 {
			   if(invalidater[i].equalsIgnoreCase("false"))
					invalidater[i]="F";
			   else if(invalidater[i].equalsIgnoreCase("true"))
					invalidater[i]="T";
			   if(currencyInvalidate[i].equalsIgnoreCase("false"))
					currencyInvalidate[i]="N";
			   else if(currencyInvalidate[i].equalsIgnoreCase("true"))
					currencyInvalidate[i]="Y";

			   	com.foursoft.esupply.common.java.CountryMasterDOB countryDOB=(com.foursoft.esupply.common.java.CountryMasterDOB)dobList.get(i);
				countryDOB.setInvalidate(invalidater[i]);
				countryDOB.setCurrencyInvalidate(currencyInvalidate[i]);
		 }
 			flag=ETransHOSuperUserRemote.invalidateCountry(dobList);

		 if(flag)
			{
			 
			 keyValueList       = new ArrayList();
			 errorMessageObject = new ErrorMessage("successfully Updated","../Invalidate.jsp?View=countrymaster");
		     
			 keyValueList.add(new KeyValue("ErrorCode","RSV")); 	
		     keyValueList.add(new KeyValue("Operation","Invalidate")); 	
			 
			 errorMessageObject.setKeyValueList(keyValueList);
             
			 request.setAttribute("ErrorMessage",errorMessageObject); 
			 
			
%>
			<jsp:forward page="../ESupplyErrorPage.jsp"/>
<%
			}
			else
			{
			 keyValueList       = new ArrayList();
			 errorMessageObject = new ErrorMessage("Error in validating the Country Ids","../Invalidate.jsp?View=countrymaster");
		     
			 keyValueList.add(new KeyValue("ErrorCode","RSU")); 	
		     keyValueList.add(new KeyValue("Operation","Invalidate")); 	
			 
			 errorMessageObject.setKeyValueList(keyValueList);
             
			 request.setAttribute("ErrorMessage",errorMessageObject); 
			 			 
%>
			<jsp:forward page="../ESupplyErrorPage.jsp"/>
<%
			}
	 }
   
 }
 catch(Exception exp)
 {
			 //Logger.error(FILE_NAME,"Error in CountryProcess.jsp file ",exp.toString());
       logger.error(FILE_NAME+"Error in CountryProcess.jsp file "+exp.toString());
			 
			 keyValueList       = new ArrayList();
			 if(strParam.equals("Add"))
				 errorMessageObject = new ErrorMessage("Unable to "+strParam+"the record","ETCCountryAdd.jsp");
		     else
				 errorMessageObject = new ErrorMessage("Unable to "+strParam+"the record","ETCCountryEnterId.jsp?Operation="+strParam);
			 
			 keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
		     keyValueList.add(new KeyValue("Operation",request.getParameter("Operation"))); 	
			 
			 errorMessageObject.setKeyValueList(keyValueList);
             
			 request.setAttribute("ErrorMessage",errorMessageObject); 
			 
			 			 
			 
			 /**
			 String errorMessage = "Unable to "+strParam+" the record";
           	 session.setAttribute("Operation",request.getParameter("Operation"));
			 session.setAttribute("ErrorCode","ERR");
			 session.setAttribute("ErrorMessage",errorMessage);
			 if(strParam.equals("Add"))
				session.setAttribute("NextNavigation","ETCCountryAdd.jsp");
			 else	
			 	session.setAttribute("NextNavigation","ETCCountryEnterId.jsp?Operation="+strParam);  */
%>
			<jsp:forward page="../ESupplyErrorPage.jsp"/>
<%
 }
%>


     
