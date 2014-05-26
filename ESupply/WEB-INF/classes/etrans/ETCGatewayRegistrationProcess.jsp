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
	
	Program Name		:	ETCGatewayRegistrationProcess.jsp
	Module Name			:   ETrans
	Task				:	Gateway Master
	Sub Task			:	Modify/View/Delte
	Author Name			:   Anand . A
	Date Started		:	September 11,2001
	Date Ended			:	September 11,2001
	Date Modified		:  
	
    	
	Description			:	This File main Purpose is to show the Details of Gateway Master
	                        provided the Gateway Master Id entered in GatewayMasterEnterId 
  */
%>
<%@ page import="javax.naming.InitialContext,
				 org.apache.log4j.Logger,
                 com.foursoft.esupply.common.util.ESupplyDateUtility,
				 com.qms.setup.ejb.sls.SetUpSession,
                    com.qms.setup.ejb.sls.SetUpSessionHome"%>
				  
<jsp:useBean id="gatewayJSP" class="com.foursoft.etrans.setup.gateway.bean.GatewayJSPBean" scope="request"/>
<jsp:setProperty name="gatewayJSP" property="*" />
<jsp:useBean id="address" class="com.foursoft.etrans.common.bean.Address" scope="request"/>
<jsp:setProperty name="address" property="*" />
<jsp:useBean id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/> 

<%!
  private static Logger logger = null;
	private static final String FILE_NAME	=	"ETCGatewayRegistrationProcess.jsp ";
%>


<%
   logger  = Logger.getLogger(FILE_NAME);	
   try
   {   // try1
    if(loginbean.getTerminalId() == null )
     {
%>
       <jsp:forward page="../ESupplyLogin.jsp"/>
<%
     }
   } // catch1
   catch(Exception e)
   {
    //Logger.error(FILE_NAME,"Error in ETGatewayRegistrationProcess.jsp file : ", e.toString());
    logger.error(FILE_NAME+"Error in ETGatewayRegistrationProcess.jsp file : "+ e.toString());
   } 
   
   String air="0",sea="0",truck="0",type="";  // for storing GatewayType i.e., Air,Sea,Surface
   int addressId      = 0;                    // for storing Address Id         
   String terminals[] = null;                 // for storing all the Terminals
   boolean flag       = true;                   
   String operation   = null;                // for storing Parameter Passed i.e., Add/Modify/View/Delete
   InitialContext initialContext = null;            // InitialContext of JNDI
   operation = session.getAttribute("Operation").toString();	
 	 try 
     {
		initialContext = new InitialContext(); 
		SetUpSessionHome gmsh=(SetUpSessionHome)initialContext.lookup("SetUpSessionBean");
		SetUpSession gms=(SetUpSession)gmsh.create();    
		String notes = 	gatewayJSP.getNotes();
		if( notes == null || notes.trim().length() == 0)
		gatewayJSP.setNotes(" ");	 

		String terminal = request.getParameter("terminalhide"); 
		java.util.StringTokenizer token  =new java.util.StringTokenizer(terminal,",");
		terminals = new String[token.countTokens()];
		int i=0;
		while(token.hasMoreTokens())
		{
			terminals[i] = token.nextToken();
			i++;
		} 
			if(operation.equals("Delete"))
		{	
			flag = gms.removeGateway(request.getParameter("gatewayId"),loginbean);
			if(flag == true)
			{ 
				String errorMessage = "Record successfully deleted with GatewayId : "+request.getParameter("gatewayId");
				session.setAttribute("Operation", "Delete");
				session.setAttribute("ErrorCode","RSD");
				session.setAttribute("ErrorMessage",errorMessage);
				session.setAttribute("NextNavigation","ETCGatewayRegistrationEnterId.jsp");  
					
%>
        		<jsp:forward page="../ESupplyErrorPage.jsp"/>
<%
       		}
       		else 
	   		{
  				String errorMessage = "You are not allowed to delete this GatewayId :"+request.getParameter("gatewayId")+", this Id under usage";
				session.setAttribute("Operation", "Delete");
				session.setAttribute("ErrorCode","ERR");
				session.setAttribute("ErrorMessage",errorMessage);
				session.setAttribute("NextNavigation","ETCGatewayRegistrationEnterId.jsp"); 
           
%>
          		<jsp:forward page="../ESupplyErrorPage.jsp"/>
<%
       		}
     
    	} 
   		else if(operation.equals("Modify"))
		 {
	        gatewayJSP.setTimeZone(request.getParameter("timeZone"));
			/*Added for getting serverTime difference*/
			ESupplyDateUtility  eSupplyDateUtility =new ESupplyDateUtility();
            String serverTimeDiff = eSupplyDateUtility.getTimeDifference(gatewayJSP.getTimeZone());	  
            gatewayJSP.setServerTimeDiff(serverTimeDiff);
	        flag = gms.updateGatewayDB(gatewayJSP.getGatewayId(),gatewayJSP.getGatewayType(),gatewayJSP.getGatewayName(),gatewayJSP.getCompanyName(),gatewayJSP.getContactName(),gatewayJSP.getNotes(),gatewayJSP,address,terminals,loginbean);
			if(flag)
			{
				String errorMessage = "Record successfully modified with GatewayId : "+gatewayJSP.getGatewayId();
				session.setAttribute("Operation", "Modify");
				session.setAttribute("ErrorCode","RSM");
				session.setAttribute("ErrorMessage",errorMessage);
				session.setAttribute("NextNavigation","ETCGatewayRegistrationEnterId.jsp");     
%>
             <jsp:forward page="../ESupplyErrorPage.jsp"/>
<%
	      	}
	     	else
		  	{
		       String errorMessage = "Error while modifying the GatewayId : "+gatewayJSP.getGatewayId();
			   session.setAttribute("Operation", "Modify");
			   session.setAttribute("ErrorCode","ERR");
			   session.setAttribute("ErrorMessage",errorMessage);
			   session.setAttribute("NextNavigation","ETCGatewayRegistrationEnterId.jsp");  
%>
            <jsp:forward page="../ESupplyErrorPage.jsp"/>
<%
	      	}
	    }  
	} 
	catch(Exception e)
	{
		//Logger.error(FILE_NAME,"Error in last GatewayRegistrationProcess.jsp file : ",e.toString());
    logger.error(FILE_NAME+"Error in last GatewayRegistrationProcess.jsp file : "+e.toString());
		String errorMessage ="Error while modifying the Gateway : "+gatewayJSP.getGatewayId();
		session.setAttribute("ErrorCode","ERR");
		session.setAttribute("ErrorMessage",errorMessage);
		session.setAttribute("Operation","Modify");
		session.setAttribute("NextNavigation","ETCGatewayRegistrationEnterId.jsp");
%>
		<jsp:forward page="../ESupplyErrorPage.jsp"/>
<%
	}
	 
%>  	  
       
   