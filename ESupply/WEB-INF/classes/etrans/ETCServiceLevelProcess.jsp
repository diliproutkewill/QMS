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
/**
	Program Name	  : ETCServiceLevelProcess.jsp
	Module Name		  : ETrans
	Task			  : ServiceLevel	
	Sub Task		  : Process	
	Author Name	      : Sivarama Krishna .V
	Date Started	  : September 12,2001
	Date Completed	  : September 12,2001
	Date Modified	  : Feb 18 ,2003
    Modification      : Added New Functionlity for Task Plan
	Modified by		  : K.N.V.Prasada Reddy 	

	Description		  : 
		This file gets the values form ETCServiceLevelView.jsp which are used in Modifying a particular record.
		This method interacts with ServiceLevelSessionBean to call the method 
		updateServiceLevelDetails(ServiceLevelObj,loginbean) to update the the values in the tables FS_FR_SERVICELEVELMASTER.
	Method Summary	:
*/
%>

<%@ page import = "javax.naming.InitialContext,
				   javax.naming.NamingException,
                   java.util.StringTokenizer,
				   org.apache.log4j.Logger,	
				   com.foursoft.etrans.setup.servicelevel.bean.ServiceLevelJspBean,
				   com.qms.setup.ejb.sls.SetUpSession,
                    com.qms.setup.ejb.sls.SetUpSessionHome,
				   com.foursoft.esupply.common.java.ErrorMessage,
				   com.foursoft.esupply.common.java.KeyValue,
				   java.util.ArrayList" %>

<jsp:useBean id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/>

<jsp:useBean id ="ServiceLevelObj" class= "com.foursoft.etrans.setup.servicelevel.bean.ServiceLevelJspBean" scope ="request"/>
<jsp:setProperty name="ServiceLevelObj" property="*"/>

<%!
  private static Logger logger = null;
%>

<%   String FILE_NAME			     =  "ETCServiceLevelProcess.jsp";
    logger  = Logger.getLogger(FILE_NAME);	
	String strParam		             = request.getParameter("Operation");      //Variable the stores the Operation type
    String serviceLevelId		     = request.getParameter("serviceLevelId"); //String vatiable that stores the ServiceLevelId				
    String sMode				     = "";
    String[] EventIds		     	 = request.getParameterValues("EventDesc");
    String[] strAllocatedTime	     = request.getParameterValues("AllocatedTime");  
    String[] strAlertTime	         = request.getParameterValues("AlertTime");   
    String OriginLocationIds	     = request.getParameter("originLocationIds");
    String DestiLocationIds		     = request.getParameter("destiLocationIds");  
    int[] AllocatedTime		  	     = null;
    int[] AlertTime                  = null;
    String[] OriginLocations         = null;
    String[] DestiLocations          = null;
    String[] strPreEventIds          = null;
    String[] strPreAllocatedTime     = null;
    String[] strPreAlertTime         = null;
	ErrorMessage errorMessageObject  = null;
    int shipmentMode                 = 0;
    int flagCount                    = 0;
    String[]    evntDelete           = null;
	ArrayList	 keyValueList	     = new ArrayList();
 
 try
	{
		
	if(loginbean.getTerminalId() == null)
	{
%>
	<jsp:forward page="../ESupplyLogin.jsp" />
<%
	}
	else
	{
 	//try 1
 	try
	{
    	InitialContext initial 			= new InitialContext();  //Object of InitialContext for JNDI look up Process 
      SetUpSessionHome slh = (SetUpSessionHome)initial.lookup("SetUpSessionBean");  //Object of ETransHOSuperSessionBean Home Interface
      SetUpSession sl = (SetUpSession) slh.create();  //Object of ServiceLevelSessionBean Remote Interface
      String[] EventIdList        = null;
	    int[]    AllocatedTimeList  = null;
	    int[]   AlertTimeList       = null;
    	
      if(EventIds!=null)
      if (EventIds.length>0)
         {
              AllocatedTime=new int[strAllocatedTime.length];
              AlertTime=new int[strAllocatedTime.length];
              for(int i=0;i<strAllocatedTime.length;i++)
                {
                AllocatedTime[i]=Integer.parseInt(strAllocatedTime[i]);
                AlertTime[i]=Integer.parseInt(strAlertTime[i]);
                } 
          }
          
       if(OriginLocationIds!=null)  
          {
           StringTokenizer token=new StringTokenizer(OriginLocationIds,"-");
           OriginLocations=new String[token.countTokens()];
           int count=0;
           while(token.hasMoreElements())
             {
                 OriginLocations[count]=(String)token.nextToken();
                 count++;
              }
             token=new StringTokenizer(DestiLocationIds,"-");
             DestiLocations=new String[token.countTokens()];
             count=0;
             while(token.hasMoreElements())
             {
                 DestiLocations[count]=(String)token.nextToken();
                 count++;
             }
            
          }
		  /* Getting Predeclared Events  */
     if((strParam.equals("Add"))||(strParam.equals("Modify")))
       {  
		       strPreEventIds=request.getParameterValues("EventDesc1");
           strPreAllocatedTime=request.getParameterValues("AllocatedTime1");
           strPreAlertTime=request.getParameterValues("AlertTime1");
           evntDelete =request.getParameterValues("evntDeleteValue");
           if (strPreEventIds!=null)
              {
               int preEventCount       = 0;
               for(int i=0;i<evntDelete.length;i++)
                     if (!evntDelete[i].equals("Y"))
                              flagCount++;
                      if(flagCount!=0)
                             {
     						   if(EventIds!=null)
  	    							 preEventCount=EventIds.length;
 		          				 EventIdList=new String[flagCount+preEventCount];
				    			 AllocatedTimeList=new int[flagCount+preEventCount];
					    		 AlertTimeList    =new int[flagCount+preEventCount];
						     	preEventCount=0;
							    for(int i=0;i<strPreEventIds.length;i++)
							       {
							       if(!evntDelete[i].equals("Y"))
								       {
										   EventIdList[preEventCount]=strPreEventIds[i];
										   AllocatedTimeList[preEventCount]=Integer.parseInt(strPreAllocatedTime[i]);
										   AlertTimeList[preEventCount]=Integer.parseInt(strPreAlertTime[i]);
										   preEventCount++;
	       							   }
      							   } //End for
							  if(EventIds!=null)
								  for(int i=0;i<EventIds.length;i++)
									 {
									   EventIdList[i+flagCount]=EventIds[i];
									   AllocatedTimeList[i+flagCount]=AllocatedTime[i];
									   AlertTimeList[i+flagCount]=AlertTime[i];
          				  }
		  		   }
           }
             if(OriginLocationIds!=null)
			       {
               if(EventIdList!=null)
                   {
                     ServiceLevelObj.setLocEventIds(EventIdList);
                     ServiceLevelObj.setLocAllocatedTime(AllocatedTimeList);
                     ServiceLevelObj.setLocAlertTime(AlertTimeList);
                   }
               else{
                     ServiceLevelObj.setLocEventIds(EventIds);
                     ServiceLevelObj.setLocAllocatedTime(AllocatedTime);
                    ServiceLevelObj.setLocAlertTime(AlertTime); 
                   }
                ServiceLevelObj.setOriginLocations(OriginLocations);  
                ServiceLevelObj.setDestiLocations(DestiLocations);
                   }
			   else{
			        if(EventIdList!=null)
				       {       
						 ServiceLevelObj.setEventIds(EventIdList);
						 ServiceLevelObj.setAllocatedTime(AllocatedTimeList);
						 ServiceLevelObj.setAlertTime(AlertTimeList);
	   				   }
                else{
                      ServiceLevelObj.setEventIds(EventIds);
		     	      ServiceLevelObj.setAllocatedTime(AllocatedTime);
					  ServiceLevelObj.setAlertTime(AlertTime); 
					 }
			       }
        }     
            ServiceLevelJspBean service=(ServiceLevelJspBean)session.getAttribute("Service");
            if((OriginLocationIds!=null)&&(service==null))   
              { 
                   errorMessageObject = new ErrorMessage("Session Expired. Try Again","ETCServiceLevelEnterId.jsp"); 
                   keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
                   keyValueList.add(new KeyValue("Operation",strParam)); 	
                   errorMessageObject.setKeyValueList(keyValueList);
                   request.setAttribute("ErrorMessage",errorMessageObject);
			 
                   
            %>
                  <jsp:forward page="../ESupplyErrorPage.jsp" />
<%           }
               if(service!=null)
                 {  
    
                 if((OriginLocationIds!=null))
                   { 
                     ServiceLevelObj.setRemarks(service.getRemarks());
                     ServiceLevelObj.setServiceLevelId(service.getServiceLevelId());
                     ServiceLevelObj.setServiceLevelDescription(service.getServiceLevelDescription());
                     ServiceLevelObj.setShipmentMode(service.getShipmentMode());
                     ServiceLevelObj.setGuaranteeDelivery(service.getGuaranteeDelivery());
                     ServiceLevelObj.setEventIds(service.getEventIds());
                     ServiceLevelObj.setAllocatedTime(service.getAllocatedTime());
                     ServiceLevelObj.setAlertTime(service.getAlertTime());
                   }  
               else
				   {
				     ServiceLevelObj.setLocEventIds(service.getLocEventIds());
					 ServiceLevelObj.setLocAllocatedTime(service.getLocAllocatedTime());
					 ServiceLevelObj.setLocAlertTime(service.getLocAlertTime());
					 ServiceLevelObj.setOriginLocations(service.getOriginLocations());
					 ServiceLevelObj.setDestiLocations(service.getDestiLocations());
				   }
                 shipmentMode=service.getShipmentMode();
                 serviceLevelId=service.getServiceLevelId();
                 session.removeAttribute("Service");
                 } 
              else
     			      {
                   shipmentMode =  ServiceLevelObj.getShipmentMode();
		      	      }
                  
	        if(strParam.equals("Add"))
	             {
                  int idMode	 =	sl.isServiceLevelIdExists(serviceLevelId); 
                  if(idMode == 1)
                   sMode = "Air";
                  if(idMode == 2)
                   sMode = "Sea";	 
                  if(idMode == 3)
                   sMode = "Air & Sea";	 
                  if(idMode == 4)
                   sMode = "Truck";	 
                  if(idMode == 5)
                   sMode = "Air & Truck";	 
                  if(idMode == 6)
                   sMode = "Sea & Truck";	 
                  if(idMode == 7)
                   sMode = "All";	 
                 if( idMode != 0 )
                 {
			 
                   errorMessageObject = new ErrorMessage("Service Level already exists with Service Level Id : "+ serviceLevelId+" and Shipment Mode "+sMode+" ","ETCServiceLevelAdd.jsp"); 
                   keyValueList.add(new KeyValue("ErrorCode","RAE")); 	
                   keyValueList.add(new KeyValue("Operation","Add")); 	
                   errorMessageObject.setKeyValueList(keyValueList);
                   request.setAttribute("ErrorMessage",errorMessageObject);
			 
                 
            %>
                  <jsp:forward page="../ESupplyErrorPage.jsp" />
            <%
                 }
                 else
                 {
  
                  boolean flag =sl.addServiceLevelDetails(ServiceLevelObj,loginbean);
                    if( flag)
                      {
			 	
                    errorMessageObject = new ErrorMessage("Service Level successfully added with Service Level Id : "+serviceLevelId,"ETCServiceLevelAdd.jsp"); 
                      keyValueList.add(new KeyValue("ErrorCode","RSI")); 	
                      keyValueList.add(new KeyValue("Operation","Add")); 	
                      errorMessageObject.setKeyValueList(keyValueList);
                      request.setAttribute("ErrorMessage",errorMessageObject);
				
            %>
                    <jsp:forward page="../ESupplyErrorPage.jsp" />
            <%
                     }
                   else
                     {
			 	
                      errorMessageObject = new ErrorMessage("Error while inserting the record with Service Level Id : "+serviceLevelId,"ETCServiceLevelAdd.jsp"); 
                      keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
                      keyValueList.add(new KeyValue("Operation","Add")); 	
                      errorMessageObject.setKeyValueList(keyValueList);
                      request.setAttribute("ErrorMessage",errorMessageObject);
				
            %>
                    <jsp:forward page="../ESupplyErrorPage.jsp" />
            <%
                  }
                }
                }
	  if(strParam.equals("Modify"))
	  {
           ServiceLevelObj.setFlag(true);
       		// Logger.info(FILE_NAME,"shipmentMode:	"+shipmentMode);	  
	         boolean flag = sl.updateServiceLevelDetails(ServiceLevelObj,loginbean,ServiceLevelObj.getShipmentMode());
		   if( flag)
	       {
				  errorMessageObject = new ErrorMessage("Service Level successfully modified with Service Level Id : "+ServiceLevelObj.getServiceLevelId(),"ETCServiceLevelEnterId.jsp"); 
			    keyValueList.add(new KeyValue("ErrorCode","RSM")); 	
			    keyValueList.add(new KeyValue("Operation",strParam)); 	
			    errorMessageObject.setKeyValueList(keyValueList);
			    request.setAttribute("ErrorMessage",errorMessageObject);
				 
			
%>
			<jsp:forward page="../ESupplyErrorPage.jsp" />
<%    		
	       }
		   else
		   {
			 errorMessageObject = new ErrorMessage("Operation Failed .Try Again. ","ETCServiceLevelEnterId.jsp"); 
			 keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
			 keyValueList.add(new KeyValue("Operation",strParam)); 	
			 errorMessageObject.setKeyValueList(keyValueList);
			 request.setAttribute("ErrorMessage",errorMessageObject);
	
%>
			<jsp:forward page="../ESupplyErrorPage.jsp" />
<%
		   }
	    }
    //for Delete  
      	else if(strParam.equals("Delete"))
	       	{  
          	boolean flag =sl.deleteServiceLevelDetails(serviceLevelId,loginbean,shipmentMode);
              if (flag)
              {
                errorMessageObject = new ErrorMessage("Service Level successfully deleted with Service Level Id : "+ServiceLevelObj.getServiceLevelId(),"ETCServiceLevelEnterId.jsp"); 
                keyValueList.add(new KeyValue("ErrorCode","RSD")); 	
                keyValueList.add(new KeyValue("Operation",strParam)); 	
                errorMessageObject.setKeyValueList(keyValueList);
                request.setAttribute("ErrorMessage",errorMessageObject);
       
        %>
              <jsp:forward page="../ESupplyErrorPage.jsp" />
        <%
                  }
                else
                {
                 errorMessageObject = new ErrorMessage("You are not allowed to delete this Service Level Id :"+ServiceLevelObj.getServiceLevelId(),"ETCServiceLevelEnterId.jsp"); 
               keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
               keyValueList.add(new KeyValue("Operation",strParam)); 	
               errorMessageObject.setKeyValueList(keyValueList);
               request.setAttribute("ErrorMessage",errorMessageObject);
			 
     
        %>
              <jsp:forward page="../ESupplyErrorPage.jsp" />
        <%
                  }
          }//end for delete option
          else if(strParam.equals("Invalidate"))
	       	{  java.util.ArrayList dobList=(java.util.ArrayList)session.getAttribute("dobList");
		   String invalidater[]=request.getParameterValues("checkBoxValue");
		   boolean flag=false;
		   for(int i=0;i<dobList.size();++i)
		 {
			   if(invalidater[i].equalsIgnoreCase("false"))
					invalidater[i]="F";
			   else if(invalidater[i].equalsIgnoreCase("true"))
					invalidater[i]="T";
			com.foursoft.etrans.setup.servicelevel.bean.ServiceLevelJspBean serviceDOB=(com.foursoft.etrans.setup.servicelevel.bean.ServiceLevelJspBean )dobList.get(i);
			serviceDOB.setInvalidate(invalidater[i]);
		 }
		 			flag=sl.invalidateServiceLevelMaster(dobList);

          	
              if (flag)
              {
                errorMessageObject = new ErrorMessage("Service Level successfully Validated : ","../Invalidate.jsp?View=serviceLevel"); 
                keyValueList.add(new KeyValue("ErrorCode","RSV")); 	
                keyValueList.add(new KeyValue("Operation",strParam)); 	
                errorMessageObject.setKeyValueList(keyValueList);
                request.setAttribute("ErrorMessage",errorMessageObject);
       
        %>
              <jsp:forward page="../ESupplyErrorPage.jsp" />
        <%
                  }
                else
                {
                 errorMessageObject = new ErrorMessage("ServiceLevel Unsuccessfully validated :","../Invalidate.jsp?View=serviceLevel"); 
               keyValueList.add(new KeyValue("ErrorCode","RSI")); 	
               keyValueList.add(new KeyValue("Operation",strParam)); 	
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
 	  exp.printStackTrace();
    String Message=null;
    StringTokenizer stToken=new StringTokenizer(exp.getMessage(),"**");
    while(stToken.countTokens()>0)
           Message=stToken.nextToken();
 		 //Logger.error(FILE_NAME,"Exception in ETCServiceLevelProcess.jsp "+exp.toString());
     logger.error(FILE_NAME+"Exception in ETCServiceLevelProcess.jsp "+exp.toString());
 		 
		 if(strParam.equals("Add"))
		    errorMessageObject = new ErrorMessage(Message,"ETCServiceLevelAdd.jsp"); 
		 else
            errorMessageObject = new ErrorMessage("Unable to "+strParam+" the Service Level","ETCServiceLevelEnterId.jsp"); 
	  keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
		 keyValueList.add(new KeyValue("Operation",strParam)); 	
		 errorMessageObject.setKeyValueList(keyValueList);
		 request.setAttribute("ErrorMessage",errorMessageObject);
	%>
		<jsp:forward page="../ESupplyErrorPage.jsp" />
<%
 	}
  } // end of  first If
}
catch(Exception e)
 {
	e.printStackTrace();
	//Logger.error(FILE_NAME,"Exception while accessing Loginbean in ServiceLevelProcess JSP");
  logger.error(FILE_NAME+"Exception while accessing Loginbean in ServiceLevelProcess JSP");
 }
%> 

