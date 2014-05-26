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
	
	Program Name		:	ETCGatewayRegistrationAddProcess.jsp
	Module Name			:   ETrans
	Task				:	Gateway Master
	Sub Task			:	Add
	Author Name			:   Anand.A
	Date Started		:	September 11,2001
	Date Ended			:	September 11,2001
	Date Modified		:  
	
	Description			:	This File main Purpose is to capture the Gateway Master Details in
	                        GatewayMasterJSPBean Java Object  entered by the User in the previous 
							Screen i.e., ETCGatewayRegistrationAdd.jsp and makes an attempt to lookup 
							the SetUpSessionBean and invokes a method  insGatewayDB() on 
							Session Bean.      
  */
%>
<%@page import	=	"javax.naming.InitialContext,
				 	org.apache.log4j.Logger,
                    com.foursoft.esupply.common.util.ESupplyDateUtility,
					com.qms.setup.ejb.sls.SetUpSession,
                    com.qms.setup.ejb.sls.SetUpSessionHome" %>
				 
<jsp:useBean id="gatewayJSP" class="com.foursoft.etrans.setup.gateway.bean.GatewayJSPBean" scope="request"/>
<jsp:setProperty name="gatewayJSP" property="*" />
<jsp:useBean id="address" class="com.foursoft.etrans.common.bean.Address" scope="request"/>
<jsp:setProperty name="address" property="*" />
<jsp:useBean id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/> 

<%!
  private static Logger logger = null;
	private static final String FILE_NAME	=	"ETCGatewayRegistrationAddProcess.jsp ";
%>


<%
   logger  = Logger.getLogger(FILE_NAME);	
   try
   {
    if(loginbean.getTerminalId() == null )
    {
%>
     <jsp:forward page="../ESupplyLogin.jsp"/>
<%
    }
   }catch(Exception e)
    {
	  //Logger.error(FILE_NAME,"Error in ETCGatewayRegistrationAddProcess.jsp file : "+e.toString());
    logger.error(FILE_NAME+"Error in ETCGatewayRegistrationAddProcess.jsp file : "+e.toString());
    } 
    String air="0",sea="0",truck="0",type=""; // temporary variables to store intermediate values
    int addressId      = 0;    		// for storing Address Id
    String terminals[] = null; 		// for storing all the Terminal Ids
    boolean flag       = true;      
	
    try
    {
      String notes = 	gatewayJSP.getNotes();   // for storing Notes 
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
 }catch(Exception e)
 {
	//Logger.error(FILE_NAME,"Error in ETCGatewayRegistrationAddProcess.jsp file : ", e.toString());
  logger.error(FILE_NAME+"Error in ETCGatewayRegistrationAddProcess.jsp file : "+ e.toString());
 }	
    InitialContext initialContext = null;

    try{
	
       initialContext = new InitialContext();
       SetUpSessionHome gmsh = (SetUpSessionHome)initialContext.lookup("SetUpSessionBean");
       SetUpSession gms = (SetUpSession)gmsh.create();
		   
       flag = gms.checkGatewayId(gatewayJSP.getGatewayId());
     if(flag)
       {
         String errorMessage = "Record already exists with Gateway Id : "+gatewayJSP.getGatewayId();
		 session.setAttribute("Operation", "ADD");
		 session.setAttribute("ErrorCode","RAE");
		 session.setAttribute("ErrorMessage",errorMessage);
		 session.setAttribute("NextNavigation","ETCGatewayRegistrationAdd.jsp");     
%>
       <jsp:forward page="../ESupplyErrorPage.jsp"/>
<%
       }
       else
	   { 
          addressId=gms.selectId();
		  gatewayJSP.setTimeZone(request.getParameter("timeZone"));
	
			/* Added for server Time difference*/
		  ESupplyDateUtility eSupplyDateUtility   = new ESupplyDateUtility();
          String serverTimeDiff=eSupplyDateUtility.getTimeDifference(gatewayJSP.getTimeZone()); 
          gatewayJSP.setServerTimeDiff(serverTimeDiff);
		  gms.insGatewayDB(gatewayJSP.getGatewayId(),gatewayJSP.getGatewayType(),gatewayJSP.getGatewayName(),gatewayJSP.getCompanyName(),gatewayJSP.getContactName(),addressId,gatewayJSP.getNotes(),gatewayJSP.getIndicator(),gatewayJSP,address,terminals,loginbean,addressId);
          String errorMessage = "Record successfully added with Gateway Id : "+gatewayJSP.getGatewayId();
		  session.setAttribute("Operation", "ADD");
		  session.setAttribute("ErrorCode","RSI");
		  session.setAttribute("ErrorMessage",errorMessage);
		  session.setAttribute("NextNavigation","ETCGatewayRegistrationAdd.jsp");     

%>
              <jsp:forward page="../ESupplyErrorPage.jsp"/>
<%
       }
      }catch(Exception e)  
       {
	     //Logger.error(FILE_NAME,"Error in GatewayRegistrationProcess.jsp file : ", e.toString());
       logger.error(FILE_NAME+"Error in GatewayRegistrationProcess.jsp file : "+ e.toString());
 	     String errorMessage = "Error while adding the record ";
		 session.setAttribute("Operation", "ADD");
		 session.setAttribute("ErrorCode","ERR");
		 session.setAttribute("ErrorMessage",errorMessage);
		 session.setAttribute("NextNavigation","ETCGatewayRegistrationAdd.jsp");   
%>		
             <jsp:forward page="../ESupplyErrorPage.jsp"/>
<%	    
       }
 	
%> 
   
   