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
/* Program Name		: ETLocationProcess.jsp
   Module name		: HO Setup
   Task		        : Location
   Sub task			: View,Modify,Delete processes
   Author Name		: A.Hemanth Kumar
   Date Started     : September 08, 2001
   Date completed   : 
   
   Description      :
     This file is invoked on submission of LocationView.jsp.This file is used to set the  Location details into the FS_FR_LOCATIONMASTER after  
     Modifying the Location details from the screen LocationView.jsp . This file will interacts with LocationMasterSessionBean and then calls the  
     method updateLocationMasterDetails or deleteLocationMaster. 
     details are then set to the respective variables through Object LocationMasterJSPBean.
*/
%>
<%@ page import = "javax.naming.InitialContext,
				org.apache.log4j.Logger,
				com.qms.setup.ejb.sls.SetUpSession,
                com.qms.setup.ejb.sls.SetUpSessionHome,
				com.foursoft.esupply.common.java.ErrorMessage,
				com.foursoft.esupply.common.java.KeyValue"%>

<jsp:useBean id ="LocationMasterObj" class= "com.foursoft.etrans.setup.location.bean.LocationMasterJspBean" scope ="request"/>
<jsp:setProperty name="LocationMasterObj" property="*"/>
<jsp:useBean id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/>
<%!
  private static Logger logger = null;
	private static final String FILE_NAME  = "ETLocationProcess.jsp";

%>
<%
   logger  = Logger.getLogger(FILE_NAME);	
   try
   {
    // checking for terminalID
    if(loginbean.getTerminalId() == null )
    {
%>
   <jsp:forward page="../ESupplyLogin.jsp" />
<%
   }
   else
   {
%>    	
<%
	String strParam   = null;    // String to store the type of operation
	String locationId = null;    // String to store locationId

    ErrorMessage errorMessageObject = null;	 
    java.util.ArrayList keyValueList = new java.util.ArrayList();   

  try
   {
       InitialContext initial = new InitialContext();     // Context variable
       SetUpSessionHome clh = ( SetUpSessionHome )initial.lookup( "SetUpSessionBean" );    // Home interface variable
       SetUpSession cl = (SetUpSession)clh.create();    // Session bean instance
       strParam = request.getParameter("Operation");	
	   //Logger.info(FILE_NAME,"Operation:	"+strParam);
       locationId = LocationMasterObj.getLocationId();
	   String shipmentMode	 =	 request.getParameter("shipmentMode");		//Variable to store shipmentMode
	   //Logger.info(FILE_NAME,"ShipmentMode:	"+shipmentMode);
	   LocationMasterObj.setShipmentMode(shipmentMode);
	   //Logger.info(FILE_NAME,"shipmentMode 01010:	"+LocationMasterObj.getShipmentMode());
       // checking the type of operation
	   
		
	   if(strParam.equalsIgnoreCase("Add"))
	   {
		  shipmentMode	 =	 request.getParameter("shipmentMode");		//Variable to store shipmentMode
		  LocationMasterObj.setShipmentMode(shipmentMode);
		 
		  if(!cl.isLocationMasterLocationIdExists(locationId))
		  {	
		    boolean flag = cl.addLocationMasterDetails(LocationMasterObj, loginbean);		// flag to verify the success of record addition
		    
			if( flag)
		     {  
    	       
			  
			   
			   errorMessageObject = new ErrorMessage("Record successfully added with Location-Id : "+locationId ,"ETLocationAdd.jsp");
			   keyValueList.add(new KeyValue("ErrorCode","RSI")); 	
			   errorMessageObject.setKeyValueList(keyValueList);
               request.setAttribute("ErrorMessage",errorMessageObject); 
			   /**
			   String errorMessage = "Record successfully added with Location-Id : "+locationId;		// String to store error message
			   session.setAttribute("ErrorCode","RSI");
			   session.setAttribute("ErrorMessage",errorMessage);
			   session.setAttribute("NextNavigation","ETLocationAdd.jsp");  */
			   
%>
				<jsp:forward page="../ESupplyErrorPage.jsp" />
<%
   		     }
		  else
		   {
        	    errorMessageObject = new ErrorMessage("Error while adding record with Location-Id : "+locationId,"ETLocationAdd.jsp");
			    keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
			    keyValueList.add(new KeyValue("Operation","Add")); 	
				errorMessageObject.setKeyValueList(keyValueList);
                request.setAttribute("ErrorMessage",errorMessageObject); 
				
				/**
				String errorMessage = "Error while adding record with Location-Id : "+locationId;		// String to store error message
			    session.setAttribute("Operation", "Add");
				session.setAttribute("ErrorCode","ERR");
				session.setAttribute("ErrorMessage",errorMessage);
				session.setAttribute("NextNavigation","ETLocationAdd.jsp");  */
%>
				<jsp:forward page="../ESupplyErrorPage.jsp" />
<%
			 }
 		  }
		  else
		   {
        	    errorMessageObject = new ErrorMessage("Record already exists with Location-Id : "+locationId ,"ETLocationAdd.jsp");
			    keyValueList.add(new KeyValue("ErrorCode","RAE")); 	
			    keyValueList.add(new KeyValue("Operation","Add")); 	
				errorMessageObject.setKeyValueList(keyValueList);
                request.setAttribute("ErrorMessage",errorMessageObject);
				/**							
				
				String errorMessage = "Record already exists with Location-Id : "+locationId;		// String to store error message
			    session.setAttribute("Operation", "Add");
				session.setAttribute("ErrorCode","RAE");
				session.setAttribute("ErrorMessage",errorMessage);
				session.setAttribute("NextNavigation","ETLocationAdd.jsp");  */
%>
				<jsp:forward page="../ESupplyErrorPage.jsp" />
<%
			 }
	   }
	   if(strParam.equalsIgnoreCase("Modify"))
	   {
		  
		   boolean flag = cl.updateLocationMasterDetails(LocationMasterObj,loginbean);    // flag denoting the success of modification operation
  		   // Checking for successful modification 
  		   if( flag)
	       {
		     
			 errorMessageObject = new ErrorMessage("Record successfully modified with Location-Id : "+locationId ,"ETLocationEnterId.jsp?Operation="+strParam);
			 keyValueList.add(new KeyValue("ErrorCode","RSM")); 	
			 keyValueList.add(new KeyValue("Operation","Modify")); 	
			 errorMessageObject.setKeyValueList(keyValueList);
             request.setAttribute("ErrorMessage",errorMessageObject);
			 
			 /**
			 String errorMessage = "Record successfully modified with Location-Id : "+locationId;    // String to store error message
			 session.setAttribute("Operation", "Modify");
			 session.setAttribute("ErrorCode","RSM");
			 session.setAttribute("ErrorMessage",errorMessage);
			 session.setAttribute("NextNavigation","ETLocationEnterId.jsp?Operation="+strParam);  */
%>
			<jsp:forward page="../ESupplyErrorPage.jsp" />
<%
	       }
	    else
		   {
		     
			 errorMessageObject = new ErrorMessage("Error while modifying the record with Location-Id : "+locationId ,"ETLocationEnterId.jsp?Operation="+strParam);
			 keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
			 keyValueList.add(new KeyValue("Operation","Modify")); 	
			 errorMessageObject.setKeyValueList(keyValueList);
             request.setAttribute("ErrorMessage",errorMessageObject);
			 
			 /**
			 
			 String errorMessage = "Error while modifying the record with Location-Id : "+locationId;    // String to store error message
			 session.setAttribute("Operation", "Modify");
			 session.setAttribute("ErrorCode","ERR");
			 session.setAttribute("ErrorMessage",errorMessage);
			 session.setAttribute("NextNavigation","ETLocationEnterId.jsp?Operation="+strParam);  */
%>
			<jsp:forward page="../ESupplyErrorPage.jsp" />
<%
            }
	   }				
      if(strParam.equalsIgnoreCase("Delete"))
	   {
			
			locationId = LocationMasterObj.getLocationId();
		   	boolean flag = cl.deleteLocationMasterDetails(LocationMasterObj.getLocationId(),loginbean );
		  	if( flag)
	         {
			 
			 errorMessageObject = new ErrorMessage("Record successfully deleted with Location-Id : "+locationId ,"ETLocationEnterId.jsp?Operation="+strParam);
			 keyValueList.add(new KeyValue("ErrorCode","RSD")); 	
			 keyValueList.add(new KeyValue("Operation","Delete")); 	
			 errorMessageObject.setKeyValueList(keyValueList);
             request.setAttribute("ErrorMessage",errorMessageObject);
			 
			 
			 /**
			 String errorMessage = "Record successfully deleted with Location-Id : "+locationId ;
			 session.setAttribute("Operation", "Delete");
			 session.setAttribute("ErrorCode","RSD");
			 session.setAttribute("ErrorMessage",errorMessage);
			 session.setAttribute("NextNavigation","ETLocationEnterId.jsp?Operation="+strParam);  */
%>
			 <jsp:forward page="../ESupplyErrorPage.jsp" />
<%
	         }
		    else
		    {
			 
			 errorMessageObject = new ErrorMessage("You can not Delete this Location : "+locationId +",as this is under use","ETLocationEnterId.jsp?Operation="+strParam);
			 keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
			 keyValueList.add(new KeyValue("Operation","Delete")); 	
			 errorMessageObject.setKeyValueList(keyValueList);
             request.setAttribute("ErrorMessage",errorMessageObject);
			 
			 /**
			 String errorMessage = "You can not Delete this Location : "+locationId +",because this Location under use";
			 session.setAttribute("Operation", "Delete");
			 session.setAttribute("ErrorCode","ERR");
			 session.setAttribute("ErrorMessage",errorMessage);
			 session.setAttribute("NextNavigation","ETLocationEnterId.jsp?Operation="+strParam);  */
%>
			<jsp:forward page="../ESupplyErrorPage.jsp" />
<%
		    }
		}
    if(strParam.equalsIgnoreCase("Invalidate"))
		{
			boolean flag =false;
			String checkBoxValue[]=request.getParameterValues("checkBoxValue");
			java.util.ArrayList dobList=(java.util.ArrayList)session.getAttribute("dobList");
			for(int i=0;i<dobList.size();++i)
			{
				com.foursoft.etrans.setup.location.bean.LocationMasterJspBean locationDOB=(com.foursoft.etrans.setup.location.bean.LocationMasterJspBean)dobList.get(i);
				if(checkBoxValue[i].equalsIgnoreCase("true"))
					checkBoxValue[i]="T";
				else if(checkBoxValue[i].equalsIgnoreCase("false"))
					checkBoxValue[i]="F";
				locationDOB.setInvalidate(checkBoxValue[i]);

			}
	   		flag= cl.invalidateLocation(dobList);

		  	if( flag)
	         {
			 
			 errorMessageObject = new ErrorMessage("Records successfully Updated","../Invalidate.jsp?View=locationmaster");
			 keyValueList.add(new KeyValue("ErrorCode","RSV")); 	
			 keyValueList.add(new KeyValue("Operation","Invalidate")); 	
			 errorMessageObject.setKeyValueList(keyValueList);
             request.setAttribute("ErrorMessage",errorMessageObject);
		%>
			 <jsp:forward page="../ESupplyErrorPage.jsp" />
<%
	         }
		    else
		    {
			 
			 errorMessageObject = new ErrorMessage("Error in Validating the LocationIds","../Invalidate.jsp?View=locationmaster");
			 keyValueList.add(new KeyValue("ErrorCode","RSU")); 	
			 keyValueList.add(new KeyValue("Operation","Invalidate")); 	
			 errorMessageObject.setKeyValueList(keyValueList);
       request.setAttribute("ErrorMessage",errorMessageObject);
		 
%>
			<jsp:forward page="../ESupplyErrorPage.jsp" />
<%
		    }
		}
 	  
 }
 catch(Exception exp)
 {
	 //Logger.error(FILE_NAME,"Error in LocationProcess.jsp file : "+exp.toString());
   logger.error(FILE_NAME+"Error in LocationProcess.jsp file : "+exp.toString());
	 if(strParam.equals("Add"))
	    errorMessageObject = new ErrorMessage("Error while "+strParam+"ing the record","ETLocationAdd.jsp");
	 else
        errorMessageObject = new ErrorMessage("Error while "+strParam+"ing the record","ETLocationEnterId.jsp?Operation="+strParam);        
	 keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
	 keyValueList.add(new KeyValue("Operation","Delete")); 	
	 errorMessageObject.setKeyValueList(keyValueList);
     request.setAttribute("ErrorMessage",errorMessageObject);
	 
	 
	 /**
	 session.setAttribute("ErrorCode","ERR");
	 session.setAttribute("ErrorMessage","Error while "+strParam+"ing the record");
	 if(strParam.equals("Add"))
		 session.setAttribute("NextNavigation","ETLocationAdd.jsp");
	 else
		 session.setAttribute("NextNavigation","ETLocationEnterId.jsp?Operation="+strParam);  */
%>
	 <jsp:forward page="../ESupplyErrorPage.jsp" />
<%
     }
   }
 }
 catch(Exception e)
  {
	//Logger.error(FILE_NAME,"Error in ETLocationProcess.jsp file : "+e.toString());
  logger.error(FILE_NAME+"Error in ETLocationProcess.jsp file : "+e.toString());
  } 
%>
