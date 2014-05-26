<%--
% 
% Copyright (c) 1999-2001 by FourSoft, Pvt Ltd. All Rights Reserved.
% This software is the proprietary information of FourSoft, Pvt Ltd.
% Use is subject to license terms.
%
% esupply - v 1.x 
%
/**
	Program Name	: CarrierRegistrationReports.jsp
	Module Name		: ETrans
	Task			: CarrierRegistration
	Sub Task		: Reports. 
	Author Name		: Rizwan.
	Date Started	: December 4,2001
	Date Completed	: December 5,2001
	Date Modified	: 
*/
--%>
<%@ page import ="java.sql.Timestamp,
				java.util.Date,
        java.util.ArrayList,
				java.util.StringTokenizer,
				java.text.SimpleDateFormat,
				org.apache.log4j.Logger,				
				com.qms.setup.ejb.sls.SetUpSession,
        com.foursoft.esupply.common.java.ErrorMessage,
				com.foursoft.esupply.common.java.KeyValue,
        com.qms.setup.ejb.sls.SetUpSessionHome,
				com.foursoft.esupply.common.util.ESupplyDateUtility"%>
<jsp:useBean id="loginbean"  class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session" />

<%!
  private static Logger logger = null;
%>

<%         

		   final	String	FILE_NAME	= "ETACarrierRegistrationReports.jsp "; 		
       logger  = Logger.getLogger(FILE_NAME);
		   int headingSize              = 0,len=0;
		   int noOfcols=0;
       ESupplyDateUtility	eSupplyDateUtility  = new ESupplyDateUtility();
		   String		        currentDate         = eSupplyDateUtility.getCurrentDateString(loginbean.getUserPreferences().getDateFormat());
		   String	heading            = null;
       ArrayList			keyValueList			=   new ArrayList();
       ErrorMessage		errorMessageObject		=   null;
		   String	terminalId         = null;
		   String	userId             = null;
		   String	terminals[][]	   = null;
		   String	operation		   = request.getParameter("Operation");
		   SimpleDateFormat df	       = new SimpleDateFormat("dd-MM-yy");
		   Date		    d	           = new Date();
		   String	    date           = df.format(d); 
		   String	    str	           = "";
 
		   terminalId				   = loginbean.getTerminalId();
		   userId					   = loginbean.getUserId();
		   
	       str+="CARRIERID,";
		   noOfcols++;
			
			if(request.getParameter("SHIPMENTNAME")!=null && request.getParameter("SHIPMENTNAME").equals("0"))
			{
				str+="SHIPMENTMODE,";
				noOfcols++;
			}
			if(request.getParameter("CARRIERNAME")!=null && request.getParameter("CARRIERNAME").equals("0"))
			{
				str+="CARRIERNAME,";
				noOfcols++;
       		}
			if (request.getParameter("CITY")!=null && request.getParameter("CITY").equals("0"))
			{
				str+="CITY,";
				noOfcols++;
			}
			if(operation.equals("CarrierRegistration") && (!str.equals("")))
				{
  				try
				  {
					 // InitialContext	initial = new InitialContext();
					//  SetUpSessionHome reportsHome=(SetUpSessionHome)initial.lookup("SetUpSessionBean");
					    SetUpSessionHome reportsHome=(SetUpSessionHome) loginbean.getEjbHome("SetUpSessionBean");
					  SetUpSession reportRemote=
					  (SetUpSession)reportsHome.create();
					  terminals=reportRemote.getViewCarrierRegistration( str, noOfcols);
					  len = terminals.length;
					  if(len>0)
					  {	 				
%>	
<html>
<head>
<title>Carrier Registration</title>       
<link rel="stylesheet" href="../../ESFoursoft_css.jsp">
<script>
 
</script>
<title>ESupplyErrorPage</title>       <%@ include file="../../ESEventHandler.jsp" %>
</head>
<body >
<form method="post" action="">
     <table width="100%" border="0" cellspacing="0" cellpadding="0">
     <tr valign="top" bgcolor="#FFFFFF"> 
     <td>
		<table width="100%" border="0" cellspacing="1" cellpadding="4">
		<tr valign="top" class='formlabel'> 
		<td width="100%" align="center" >
		<%//Modified by Sreelakshmi KVA - 20050411 SPETI-5561 //%>
		<table width="100%" border="0" ><tr class='formlabel'><td>Carrier - View All 
        </td><td align=right><%=loginbean.generateUniqueId("ETACarrierRegistrationReports.jsp","CarrierRegistration")%></td></tr></table></td>
		</tr>
		</table>
		<table width="100%" border="0" cellspacing="1" cellpadding="4">
		<tr class='formdata' valign="top"  bgcolor="#FFFFFF"> 
		<td ><b>Terminal : </b><%=terminalId%></td>
		<td align="center" ><b>User :</b><%=userId%></td>
		<td align="right" ><b>Date :</b> 
		<%=currentDate%>
		</td>
		</tr>
		</table>
<%
  			  StringTokenizer st1= new StringTokenizer(str,",");
			  int tableWidth = 0;  
			  while(st1.hasMoreTokens())
		          {
			        tableWidth = tableWidth +190;	 
				st1.nextToken();	
		          }     
%>
     
              <table width=100%   border="0" cellspacing="1" cellpadding="4" >
             <tr valign="top" class='formheader' > 
<%
 		   StringTokenizer st= new StringTokenizer(str,",");
		   while(st.hasMoreTokens())
		   {
			
			 heading = st.nextToken();
			 if(heading.equals("CARRIERID"))
	            heading="Carrier Id";    
	 		 if(heading.equals("CARRIERNAME"))
				heading="Carrier Name ";
		  	 if(heading.equals("SHIPMENTMODE"))
			   	heading="Shipment Mode ";
			 if(heading.equals("CITY"))
				heading="City";	
%>
			  
        <td width ="20%" colspan="1"><%=heading%>
        </td>
<%   
		         }
%>
        </tr>
<%
		  for(int i=0;i<len;i++)
		    {
%> 
                  <tr valign="top" class='formdata'>                   
<%					
			    for( int j=0;j<noOfcols;j++)
			 	 {
					if(terminals[i][j].equals("3"))
						terminals[i][j]="Air,Sea";
					else if(terminals[i][j].equals("1"))
						terminals[i][j]="Air";
					else if(terminals[i][j].equals("2"))
						terminals[i][j]="Sea";
					else if(terminals[i][j].equals("7"))
						terminals[i][j]="Air,Sea,Truck";
					else if(terminals[i][j].equals("4"))
						terminals[i][j]="Truck";
					else if(terminals[i][j].equals("6"))
						terminals[i][j]="Sea,Truck";
					else if(terminals[i][j].equals("5"))
						terminals[i][j]="Air,Truck";


					  if(terminals[i][j]==null)
					   {		
%>
         <td width="20%" align="left"> <font face="Verdana" size="2"  COLOR=black>Not Available</font></td>
<%   
           		                   }  
					else
					   {   
%>
         <td width="20%" align="left"> <font face="Verdana" size="2"  COLOR=black><%=terminals[i][j]%></font></td>
<%				
				    	 }		   
 			 		  }
%>					   
                     </tr>
<%
			        }
%>		</table> 
            <!-- Modified By G.Srinivas to resolve the QA-Issue -->
			<table><tr align="right" border="0">
						<td align="">&nbsp;</td>
						<td width="100%"   align="right" bgcolor="white">
						<input type="button" value="Continue" name="close" onClick="javascript:history.go(-1);" class='input'>
						</td>
					</tr>
			</table>
        </table>
      </form>
	  </body>
	</html>
  <%
  }
    
 else
 {

		errorMessageObject = new ErrorMessage(  "No relevent data was found.  ","../ETAViewAllAdmin.jsp?View=CarrierRegistration"); 
		keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
		keyValueList.add(new KeyValue("View","CarrierRegistration")); 	
		errorMessageObject.setKeyValueList(keyValueList);
		request.setAttribute("ErrorMessage",errorMessageObject);
%>   
		<jsp:forward page="../../ESupplyErrorPage.jsp" />
<%  
   }

  } // try
   catch(Exception e)
	{
     	//Logger.error(FILE_NAME,"problem in execution : ",e.toString());
      logger.error(FILE_NAME+"problem in execution : "+e.toString());
	}
 } // if main
%>