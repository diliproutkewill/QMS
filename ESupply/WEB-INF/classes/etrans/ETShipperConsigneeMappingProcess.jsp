<%@ page import="javax.naming.InitialContext,
 				 javax.naming.NamingException,
				 com.qms.setup.ejb.sls.SetUpSession,
                    com.qms.setup.ejb.sls.SetUpSessionHome,
				 java.util.ArrayList,
				 java.util.StringTokenizer,
				 org.apache.log4j.Logger,
				 com.foursoft.esupply.common.java.ErrorMessage,
				 com.foursoft.esupply.common.java.KeyValue"%>					

<jsp:useBean id = "loginbean" class = "com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope = "session"/>

<%!
  private static Logger logger = null;
%>

<%
	  String FILE_NAME ="ETShipperConsigneeMappingProcess.jsp";
    logger  = Logger.getLogger(FILE_NAME);	
      
	  SetUpSession		customerRemote		= null;
	  SetUpSessionHome	customerHome		= null;
      
      ArrayList	 listOfValues	= new ArrayList();
      
      String customerId = null;			
      String registrationLevel = request.getParameter("registrationLevel");
      
      String terminalId = request.getParameter("terminalId");
	  if(registrationLevel.equals("T"))
	    customerId = request.getParameter("customerId");
	  else
	    customerId = request.getParameter("corpCustomerId");
	  
	  String[] shipperIds   = request.getParameterValues("shipper");
	  String[] consigneeIds = request.getParameterValues("consignee");
	  
	  String[] outBoundTerminalIds = request.getParameterValues("outBoundTerminalId");
	  String[] inBoundTerminalIds  = request.getParameterValues("inBoundTerminalId");
	  
	  listOfValues.add(outBoundTerminalIds);
	  listOfValues.add(consigneeIds);
	  listOfValues.add(inBoundTerminalIds);
	  listOfValues.add(shipperIds);
	  
	  
	  boolean flag = false;
	  
	  ErrorMessage  errorMessageObject = null;
	  ArrayList	    keyValueList	   = null;	
	  
	  try
	  {
	     
		 customerHome   =(SetUpSessionHome)loginbean.getEjbHome("SetUpSessionBean");
		 customerRemote =(SetUpSession)customerHome.create();
	     
	     String errorMessage1 = (String)customerRemote.validateCustomer(terminalId,customerId,listOfValues);
		// Logger.info(FILE_NAME,"Error Message is " + errorMessage1);
		 if(errorMessage1!=null)
		 {
		   request.setAttribute("OurBoundTerminals",outBoundTerminalIds);
		   request.setAttribute("InBoundTerminals",inBoundTerminalIds);
		   request.setAttribute("Consignees",consigneeIds);
		   request.setAttribute("Shipper",shipperIds);
		   request.setAttribute("ErrorCodes",errorMessage1);
		   request.setAttribute("CustomerId",customerId);
		   String actionPage = " ETShipperConsigneeMapping.jsp?registrationLevel="+registrationLevel;				 

%>		 
			<jsp:forward page="<%=actionPage%>" />		 
<%		
		}
		 
		     
	     
	     
	     ArrayList arrayList = new ArrayList();
	     arrayList.add(customerId);
		 arrayList.add(terminalId);
		 arrayList.add(shipperIds);
		 arrayList.add(consigneeIds);
	     arrayList.add(outBoundTerminalIds);
		 arrayList.add(inBoundTerminalIds);
	     
	     
	     flag	=   customerRemote.mapShipOrConsDtls(arrayList);
	     
	     
     
	     
	     if(flag)
	     {
	        
	        errorMessageObject = new ErrorMessage(" Shipper and Consignees are Successfully Mapped for  the Customer " + customerId,"ETShipperConsigneeMapping.jsp?registrationLevel="+registrationLevel); 
			
			keyValueList = new ArrayList();
			
			keyValueList.add(new KeyValue("ErrorCode","RSI")); 	
			errorMessageObject.setKeyValueList(keyValueList);
			request.setAttribute("ErrorMessage",errorMessageObject);

			
%>	     
            <jsp:forward page="../ESupplyErrorPage.jsp" />
<%
	     }   
	     else
	     { 
		   
		   errorMessageObject = new ErrorMessage(" Unable to Map Shipper and Consignee for the Customer " + customerId,"ETShipperConsigneeMapping.jsp?registrationLevel="+registrationLevel); 
			
		   keyValueList = new ArrayList();
			
		   keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
		   errorMessageObject.setKeyValueList(keyValueList);
		   request.setAttribute("ErrorMessage",errorMessageObject);

		   
%>	     
            <jsp:forward page="../ESupplyErrorPage.jsp" />
<%	     
	     }
	  }catch(Exception ex)
	  {
	       
	       errorMessageObject = new ErrorMessage(" Unable to Map Shipper and Consignee for the Customer " + customerId,"ETShipperConsigneeMapping.jsp?registrationLevel="+registrationLevel); 
			
		   keyValueList = new ArrayList();
			
		   keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
		   errorMessageObject.setKeyValueList(keyValueList);
		   request.setAttribute("ErrorMessage",errorMessageObject);

		  
%>	     
            <jsp:forward page="../ESupplyErrorPage.jsp" />
<%	        
	  } 
%>	  