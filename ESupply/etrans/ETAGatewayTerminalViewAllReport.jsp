<%--
% 
% Copyright (c) 1999-2001 by FourSoft, Pvt Ltd. All Rights Reserved.
% This software is the proprietary information of FourSoft, Pvt Ltd.
% Use is subject to license terms.
%
% esupply - v 1.x 
%
--%>
<% // @@ Murali Modified on 20050427 Regarding SPETI - 5536 %>

 <%@ page import = "javax.naming.InitialContext,
					java.util.Vector,
					java.util.StringTokenizer,
					java.sql.Timestamp,
					org.apache.log4j.Logger,
					com.qms.setup.ejb.sls.SetUpSession,
                    com.qms.setup.ejb.sls.SetUpSessionHome,
					com.foursoft.etrans.common.util.java.FormatDate,
					com.foursoft.esupply.common.util.ESupplyDateUtility						  	
			" %>
<% // @@ Murali Modified on 20050427 Regarding SPETI - 5536 %>
<jsp:useBean id="loginbean"  class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/>

<%!
  private static Logger logger = null;
	private static final String FILE_NAME = "ETAGatewayTerminalViewAllReport.jsp ";
%>

<%
  logger  = Logger.getLogger(FILE_NAME);
	String	     terminalid   = null; 
	String       userId       = null; 
	String       insetAttribute   = null;
	Vector       details      =	new java.util.Vector();
	
	try
    {
		terminalid 			=  	loginbean.getTerminalId();
		userId		 		= 	loginbean.getUserId();
		// @@ Murali Modified on 20050427 Regarding SPETI - 5536
		/*
		SimpleDateFormat df		=	new SimpleDateFormat("dd-MM-yy");
		Date d				=	new Date();
		String date			=	df.format(d);
		*/
		ESupplyDateUtility eSupplyDateUtility = new ESupplyDateUtility();	
	    String date = eSupplyDateUtility.getCurrentDateString(loginbean.getUserPreferences().getDateFormat());
		// @@ Murali Modified on 20050427 Regarding SPETI - 5536
		insetAttribute 			= 	(String)session.getAttribute("InsetAttribute");
		InitialContext initial=	new InitialContext();
		SetUpSessionHome etransReportsHome 	= (SetUpSessionHome )initial.lookup( "SetUpSessionBean" );
		SetUpSession  etransReportsRemote 	= (SetUpSession) etransReportsHome.create();
%>		
	
<html>
<head>
<%@ include file="/ESEventHandler.jsp" %>
<title><%=insetAttribute%> View all Report</title>
<link rel="stylesheet" href="../ESFoursoft_css.jsp">
</head>
<body>
<form method="post" action="">
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
			<tr  valign="top"> 
			<td width="100%" bgcolor="ffffff">

			
	       
<%	
 if (insetAttribute.equalsIgnoreCase("gatewaymaster") )
  {	  
	String[][]   records      = null;
	String       heading      = null;
	int cols = 0;
	int leng = 0;
    String values="",values1="",gatewayId="",gatewayName="",city="",gatewayType="",gatewayType1="",companyName="",contactName="",indicator="";	  //variables values1 and gatewayType1 was added by Ravi Kumar to Resolve the Issue SPETI-2723 on 24-02-2005.
    int count=0;
	try
		   {	 	     
				gatewayId ="A.GATEWAYID,";
				count++;
			   if(request.getParameter("P_GATEWAYNAME")!=null )
		       { 
				gatewayName="A.GATEWAYNAME,";
				count++;				
		       }
		     if(request.getParameter("P_CITY")!=null )
		       {
				city="B.CITY,";
				count++;				
		       }
		  
		    if(request.getParameter("P_GATEWAYTYPE")!=null )
		      { 
				gatewayType="A.GATEWAYTYPE,";
				gatewayType1="decode(A.GATEWAYTYPE,1,'Air',2,'Sea',3,'Air,Sea',4,'Truck',5,'Air,Truck',6,'Sea,Truck',7,'Air,Sea,Truck','Not Available') GATEWAYTYPE,"; //Added by Ravi Kumar to Resolve the Issue SPETI-2723 on 24-02-2005
				count++;				
		      }	  	   	
		    if(request.getParameter("P_COMPANY")!=null )
		      { 
				companyName="A.COMPANYNAME,";
				count++;				
		      }	 
	        if(request.getParameter("P_CONTACT")!=null )
		      {  
				contactName="A.CONTACTNAME,";
				count++;				
			  }	
		    if(request.getParameter("P_INDICAT")!=null )
		      {
				indicator="A.INDICATOR,";
				count++;				
			  }	 		  	   		  
 	        values=values+gatewayId+gatewayName+city+gatewayType+companyName+contactName+indicator;
			//Modified By G.Srinivas to resolve the QA-Issue No:SPETI-5490 on 20050407.
 	        values1=values1+gatewayId+gatewayName+city+gatewayType1+companyName+contactName+indicator;			 
		  }	 
		  catch(Exception e)
		    {
			    String errorMessage = " Problem in retrieving the Gateway details" ;
				session.setAttribute("Operation", "");
				session.setAttribute("ErrorCode","RNF");
				session.setAttribute("ErrorMessage",errorMessage);
        		session.setAttribute("NextNavigation","window.close()");
%>
				<jsp:forward page="../../ESupplyErrorPage.jsp" />
     
<% 
		    }
  if(!values.equals(""))
  {	
  	// records = etransReportsRemote.getGatewayDetails(values,count);
 	 records = etransReportsRemote.getGatewayDetails(values1,count);  //Added By Ravi Kumar to resolve the issue SPETI-2723 on 24-02-2005
	 leng=records.length;
	  if(leng != 0)
     {	
%>	
	<table width="100%" border="0" class='formlabel'>
			<tr valign="top" > 
			<% // @@ Modified by Sailaja on 2005 05 03 for SPETI - 5538 %>
			<td> 
			Gateway - View All
			</td>
			<% // @@ 2005 05 03 for SPETI - 5538 %>
			<td align=right>
			<%=loginbean.generateUniqueId("ETAGatewayTerminalViewAllReport.jsp",insetAttribute.toLowerCase())%>
			</td>

			</tr>
      </table>
  	 <table width="100%" border="0" cellspacing="1" cellpadding="4">
				<tr valign="top" class='formdata'> 
				<td width="33%"><b> Terminal : </b><%=terminalid%></td>
				<td width="33%" align="center"><b>User : </b><%=userId%></td>
				<td width="33%" align="right"><b>Date : </b><%=date%></td>
				</tr>
				<tr valign="top" class='formdata'> 
				<td>&nbsp;</td>
				<td colspan="2"> 

				</td>
				</tr>
          </table>
<table width="100%"  border="0" cellspacing="1" cellpadding="4" >
   <tr valign="top" class='formheader'> 
<%
    StringTokenizer st= new StringTokenizer(values,",");
	while(st.hasMoreTokens())
	{ 
	    cols++;
	    heading = st.nextToken().substring(2);
        if(heading.equalsIgnoreCase("GatewayId"))
			heading = "Gateway Id";			 
		if(heading.equalsIgnoreCase("GatewayName"))
			heading = "Gateway Name";
		if(heading.equalsIgnoreCase("City"))
			heading = "City";
		if(heading.equalsIgnoreCase("GatewayType"))
			heading = "Gateway Type";
		if(heading.equalsIgnoreCase("CompanyName"))
			heading = "Company Name";
		if(heading.equalsIgnoreCase("ContactName"))
			heading = "Contact Name";
		if(heading.equalsIgnoreCase("Indicator"))
			heading = "Indicator";	
%>
   <td width ="15%"><%=heading%></td>
<%   
	}
%>
   </tr>
<%
    for(int i=0;i<leng;i++)
	{
%> 
       <tr valign="top" class='formdata'>                   
<%					
		for(int j=0;j<count;j++)
		{
		  if(records[i][j]==null)
		  {
%>
       <td width="15%" >Not Available</td>
<%   
          } 
		  else 
		  {   
%>
          <td width="15%" ><%=records[i][j]%></td>
<%				
		  }		   
		}
%>					   
       </tr>
<%
	}
%>
       </table>
	    <!-- Modified By G.Srinivas to resolve the QA-Issue -->
	   <table><tr align="right" border="0">
								<td align="">&nbsp;</b>
								<td width="100%"   align="right" bgcolor="white">
								<input type="button" value="Continue" name="close" onClick="javascript:history.go(-1);" class='input'>
								</td>
							</tr>
	   </table>
      </td>
    </tr>
 </table>
</form>

<%
  } else
     {
				String errorMessage = "No Data found for Gateway(s) ." ;
				session.setAttribute("Operation", "");
				session.setAttribute("ErrorCode","RNF");
				session.setAttribute("ErrorMessage",errorMessage);
        		session.setAttribute("NextNavigation","window.close()");
%>
				<jsp:forward page="../../ESupplyErrorPage.jsp" />
     
<% 
   }  
  	
  }	  
 }	if (insetAttribute.equalsIgnoreCase("customsmaster") )
  {	 
	String[][]   records      = null;
	String       heading      = null;
	int cols = 0;
	int leng = 0;
	 String values="",customesId="",contactName="",designation="",city="",terminalId="";	 
    int count=0;
	try
		   {	
				customesId = "A.CUSTOMSID,";
				count++;
			
			 if(request.getParameter("P_CONTACTNAME")!=null )
			 { 
				contactName = "A.CONTACTNAME,";
				count++;
			 }	
			 if(request.getParameter("P_DESIGNATION")!=null )
			 {
		        designation = "A.DESIGNATION,";
				count++;
			 }
			 if(request.getParameter("P_CITY")!=null)
			 {
				city = "B.CITY,";
				count++;
			 }	
			 if(request.getParameter("P_TERMINALID")!=null)
			 {
				terminalId = "A.TERMINALID,";
				count++;
			 }	
     		 values=values+customesId+contactName+designation+city+terminalId;	
		   }
		    catch(Exception e)
		    {
				String errorMessage = " Problem in retrieving the Customs details." ;
		 		session.setAttribute("Operation", "");
				session.setAttribute("ErrorCode","RNF");
				session.setAttribute("ErrorMessage",errorMessage);
        		session.setAttribute("NextNavigation","window.close()");
%>
     <jsp:forward page="../../ESupplyErrorPage.jsp" />
     
<% 
		    }
  if(!values.equals(""))
  {	
	 records = etransReportsRemote.getCustomsMaster(values,terminalid,count);
	 leng=records.length;
	 if(leng != 0)
     {	
%>	  
		<table width="100%" border="0" cellspacing="0" cellpadding="0">
			<tr valign="top" bgcolor="ffffff"> 
			<td> 
			<table width="100%" border="0" cellspacing="1" cellpadding="4">
			<tr valign="top" class='formlabel'> 
			<% // @@ Murali Modified On 20050422 Regarding SPETI-5570 ;  %>
			<!-- <td align="center">  Customs  Master -->
			<td >  Customs - View All
			<% // @@ Murali Modified On 20050422 Regarding SPETI- 5570;  %>
			</td>
			<td align=right><!-- Modified By G.Srinivas to resolve the QA-Issue -->
			<%=loginbean.generateUniqueId("ETAGatewayTerminalViewAllReport.jsp",insetAttribute.toLowerCase())%>
			</td>
			</tr>
       </table>
	   <table width="100%" border="0" cellspacing="1" cellpadding="4">
				<tr valign="top" class='formdata'> 
				<td width="33%"><b> Terminal : </b><%=terminalid%></td>
				<td width="33%" align="center"><b>User : </b><%=userId%></td>
				<td width="33%" align="right"><b>Date : </b><%=date%></td>
				</tr>
				<tr valign="top" class='formdata'> 
				<td>&nbsp;</td>
				<td colspan="2"> 

				</td>
				</tr>
          </table>
   
	
 <table width="100%"  border="0" cellspacing="1" cellpadding="4" >
   <tr valign="top" class='formheader'> 
<%
    StringTokenizer st= new StringTokenizer(values,",");
	while(st.hasMoreTokens())
	{ 
	    cols++;
	    heading = st.nextToken().substring(2);
                if(heading.equalsIgnoreCase("CustomsId"))
					   heading = "Customs Id";			 
			    if(heading.equalsIgnoreCase("ContactName"))
				       heading = "Contact Name";
			    if(heading.equalsIgnoreCase("Designation"))
				       heading = "Designation";
				 if(heading.equalsIgnoreCase("City"))
				       heading = "City";
			     if(heading.equalsIgnoreCase("TERMINALID"))
				       heading = "TerminalId";
					   	
%>
   <td width ="20%"><%=heading%></td>
<%   
	}
%>
   </tr>
<%
    for(int i=0;i<leng;i++)
	{
%> 
       <tr valign="top" class='formdata'>                   
<%					
		for(int j=0;j<count;j++)
		{
		  if(records[i][j]==null)
		  {
%>
       <td width="20%">Not Available</td>
<%   
          } 
		  else 
		  {   
%>
          <td width="20%"><%=records[i][j]%></td>
<%				
		  }		   
		}
%>					   
       </tr>
<%
	}
%>
       </table></center>
      </td>
     </tr><!-- Modified By G.Srinivas to resolve the QA-Issue -->
	<tr valign="top" border="0"> 
                <td width="100%"   align="right" bgcolor="white">
                <input type="button" value="Continue" name="close" onClick="javascript:history.go(-1);" class='input'>
				</td></tr>
 </table>
</form>

<%
  } else
     {
		

				String errorMessage = "No Data found for Customs(s) ." ;
				session.setAttribute("Operation", "");
				session.setAttribute("ErrorCode","RNF");
				session.setAttribute("ErrorMessage",errorMessage);
        		session.setAttribute("NextNavigation","ETAShowViewAllReport.jsp?viewParameter=customsMaster");
%>
				<jsp:forward page="../ESupplyErrorPage.jsp" />
<% 
   }  
  }	
  }	 if (insetAttribute.equalsIgnoreCase("carriercontractsWRTterminal") )
  {	  
	String[][]   records      = null;
	String       heading      = null;
	int cols = 0;
	int leng = 0;
	 String values="",carrierId="",carrierName="",carrierContractId="";	 
    int count=0;
	try
		   {
			 carrierId = "A.CARRIERID,";
				count++;
			 	
			 if(request.getParameter("P_CARRIERNAME")!=null )
			 {
				carrierName = "B.CARRIERNAME,";
				count++;
			 }	
			 if(request.getParameter("P_CARRIERCONID")!=null )
			 {
		        carrierContractId = "A.CACONTRACTID,";
				count++;
			 }	
     		 values=values+carrierId+carrierName+carrierContractId;	
		   }
		    catch(Exception e)
		    {
				String errorMessage = " Problem in retrieving the Carrier Contract details." ;
				session.setAttribute("Operation", "");
				session.setAttribute("ErrorCode","RNF");
				session.setAttribute("ErrorMessage",errorMessage);
        		session.setAttribute("NextNavigation","window.close()");
%>
     <jsp:forward page="../../ESupplyErrorPage.jsp" />
     
<% 
		    }
  if(!values.equals(""))
  {	 
	 records = etransReportsRemote.getGWCarrierContracts(values,terminalid,count);
	 leng=records.length;
	 if(leng != 0)
     {	
%>	 
		<table width="100%" border="0" cellspacing="0" cellpadding="0">
			<tr valign="top" bgcolor='ffffff'> 
			<td> 
			<table width="100%" border="0" cellspacing="1" cellpadding="4">
			<tr valign="top" class='formlabel'> 
			<td align="center"> Carrier Contracts
			</td>														
			</tr>
		</table>
    <table width="100%" border="0" cellspacing="1" cellpadding="4">
				<tr valign="top" class='formdata'> 
				<td width="33%"><b> Terminal : </b><%=terminalid%></td>
				<td width="33%" align="center"><b>User : </b><%=userId%></td>
				<td width="33%" align="right"><b>Date : </b><%=date%></td>
				</tr>
				<tr valign="top" class='formdata'> 
				<td>&nbsp;</td>
				<td colspan="2"> 

				</td>
				</tr>
          </table>
	
 <table width="100%"  border="0" cellspacing="1" cellpadding="4">
   <tr valign="top" class='formheader'> 
<%
    StringTokenizer st= new StringTokenizer(values,",");
	while(st.hasMoreTokens())
	{
	    cols++;
	    heading = st.nextToken().substring(2);
                if(heading.equalsIgnoreCase("CarrierId"))
					   heading = "Carrier Id";			 
			    if(heading.equalsIgnoreCase("CarrierName"))
				       heading = "Carrier Name";
			    if(heading.equalsIgnoreCase("CACONTRACTID"))
				       heading = "Carrier ContractId";
			    
%>
   <td width ="30%"><%=heading%></td>
<%   
	}
%>
   </tr>
<%
    for(int i=0;i<leng;i++)
	{
%> 
       <tr valign="top" class='formdata'>                   
<%					
		for(int j=0;j<count;j++)
		{
		  if(records[i][j]==null)
		  {
%>
       <td width="30%" align="left">Not Available</td>
<%   
          } 
		  else 
		  {   
%>
          <td width="30%" ><%=records[i][j]%></td>
<%				
		  }		   
		}
%>					   
       </tr>
<%
	}
%>
       </table>
      </td>
    </tr>
 </table>
</form>

<%
  } 
else
     {
				String errorMessage = "No Data found for Carrier Contract(s) ." ;
				session.setAttribute("Operation", "");
				session.setAttribute("ErrorCode","RNF");
				session.setAttribute("ErrorMessage",errorMessage);
        		session.setAttribute("NextNavigation","window.close()");
%>
				<jsp:forward page="../../ESupplyErrorPage.jsp" />
     
<% 
   }  
  }	
  }	 if (insetAttribute.equalsIgnoreCase("terminalmaster") )
  {	 
	String[][]   records      = null;
	String       heading      = null;
	int cols = 0;
	int leng = 0;
	 String values         ="",TerminalId="",contactName="",designation="",city="";
    int count=0;
	try
		  {
			
				TerminalId="A.TERMINALID,";
				count++;
			
		   if(request.getParameter("P_CONTACT")!=null )
		   {
			    contactName="A.CONTACTNAME,";
				count++;
		   }
		   if(request.getParameter("P_DESIGNATION")!=null )
		   {
			   designation ="A.DESIGNATION,";
			   count++;
		   }
		   if(request.getParameter("P_ADDRESS")!=null )
		   {
			   city="B.CITY,";
			   count++;
		   }
		   values=values+TerminalId+contactName+designation+city;
		  }
		  catch(Exception e)
		  {
			//Logger.error(FILE_NAME,"Exception in first try block at TerminalmasterProcess.jsp",e.toString());
      logger.error(FILE_NAME+"Exception in first try block at TerminalmasterProcess.jsp"+e.toString());
			String errorMessage = " Problem in retrieving the Terminal details" ;
		    session.setAttribute("Operation", "");
			session.setAttribute("ErrorCode","RNF");
			session.setAttribute("ErrorMessage",errorMessage);
        	session.setAttribute("NextNavigation","OperationsIndex.jsp");
%>
     <jsp:forward page="../../ESupplyErrorPage.jsp" />
     
<% 
		  }
  if(!values.equals(""))
  {	 
	 records = etransReportsRemote.getTerminalMaster(values,count,terminalid);
	 leng=records.length;
	 if(leng != 0)
     {	
%>	
<table width="100%" cellpadding="0" cellspacing="0">
<tr bgcolor="ffffff">
<td>
  <table width="100%" border="0" cellspacing="1" cellpadding="4">
         <tr valign="top" class='formlabel'> 
          <td align="center"> Terminal Master
            </td>
        </tr>
      </table>
   <table width="100%" border="0" cellspacing="1" cellpadding="4">
				<tr valign="top" class='formdata'> 
				<td width="33%"><b> Terminal : </b><%=terminalid%></td>
				<td width="33%" align="center"><b>User : </b><%=userId%></td>
				<td width="33%" align="right"><b>Date : </b><%=date%></td>
				</tr>
				<tr valign="top" class='formdata'> 
				<td>&nbsp;</td>
				<td colspan="2"> 

				</td>
				</tr>
          </table>
 <table width="100%"  border="0" cellspacing="1" cellpadding="4">
   <tr valign="top" class='formheader'> 
<%
    StringTokenizer st= new StringTokenizer(values,",");
	while(st.hasMoreTokens())
	{ 
	    cols++;
	    heading = st.nextToken().substring(2);
				if(heading.equalsIgnoreCase("TerminalId"))
					   heading = "Terminal Id";			
			    if(heading.equalsIgnoreCase("ContactName"))
				       heading = "Contact Name";
			    if(heading.equalsIgnoreCase("Designation"))
				       heading = "Designation";
			    if(heading.equalsIgnoreCase("City"))
					   heading = "City";
			    
%>
   <td width ="20%"><%=heading%></td>
<%   
	}
%>
   </tr>
<%
    for(int i=0;i<leng;i++)
	{
%> 
       <tr valign="top" class='formdata'>                   
<%					
		for(int j=0;j<count;j++)
		{
		  if(records[i][j]==null)
		  {
%>
       <td width="20%">Not Available</td>
<%   
          } 
		  else 
		  {   
%>
          <td width="20%"><%=records[i][j]%></td>
<%				
		  }		   
		}
%>					   
       </tr>
<%
	}
%>
       </table>
      </td>
    </tr>
 </table>
</form>

<%
  } else
     {
		       String errorMessage = "No Data found for Terminal Master(s) ." ;
				session.setAttribute("Operation", "");
				session.setAttribute("ErrorCode","RNF");
				session.setAttribute("ErrorMessage",errorMessage);
        		session.setAttribute("NextNavigation","window.close()");
%>
				<jsp:forward page="../../ESupplyErrorPage.jsp" />
     
<% 
     }  
  }	
  }	   if (insetAttribute.equalsIgnoreCase("customercontractsWRTterminal") )
  {	   
	String[][]   records      = null;
	String       heading      = null;
	int cols = 0;
	int leng = 0;
	String values="",customerId="",contractId="",contactName="";
    int count=0;
	try
		     {
			    customerId = "A.ID,";
				count++;
			 	
			 if(request.getParameter("P_CONTRACTID")!=null )
			 {
				contractId = "A.CONTRACTID,";
				count++;
			 }	
			 if(request.getParameter("P_CONTACTNAME")!=null)
			 {
		        contactName = "B.CONTACTNAME,";
				count++;
			 }	
     		    values=values+customerId+contractId+contactName;	
		     }
		    catch(Exception e)
		    {
				String errorMessage = " Problem in retrieving the Customer Contract details." ;
		   		session.setAttribute("Operation", "");
				session.setAttribute("ErrorCode","RNF");
				session.setAttribute("ErrorMessage",errorMessage);
        		session.setAttribute("NextNavigation","window.close()");
%>
     <jsp:forward page="../../ESupplyErrorPage.jsp" />
     
<%
		    }
  if(!values.equals(""))
  {	 
	 records = etransReportsRemote.getCustomerContracts(values,terminalid,count);
	 leng=records.length;
	 if(leng != 0)
     {	
%>	
<table width="100%" cellpadding="4" cellspacing="1">
<tr bgcolor="ffffff">
<td>
  <table width="100%" border="0" cellspacing="1" cellpadding="4">
         <tr valign="top" class='formlabel'> 
          <td align="center"> 
              Customer Contracts
            </td>
        </tr>
      </table>
     <table width="100%" border="0" cellspacing="1" cellpadding="4">
				<tr valign="top" class='formdata'> 
				<td width="33%"><b> Terminal : </b><%=terminalid%></td>
				<td width="33%" align="center"><b>User : </b><%=userId%></td>
				<td width="33%" align="right"><b>Date : </b><%=date%></td>
				</tr>
				<tr valign="top" class='formdata'> 
				<td>&nbsp;</td>
				<td colspan="2"> 

				</td>
				</tr>
          </table>
 <table width="100%"  border="0" cellspacing="1" cellpadding="4">
   <tr valign="top" class='formdata'> 
<%
    StringTokenizer st= new StringTokenizer(values,",");
	while(st.hasMoreTokens())
	{ 
	    cols++;
	    heading = st.nextToken().substring(2);
                if(heading.equalsIgnoreCase("CustomerId"))
					   heading = "Customer Id";			 
			    if(heading.equalsIgnoreCase("ContractId"))
				       heading = "Contract Id";
			    if(heading.equalsIgnoreCase("ContactName"))
				       heading = "Contact Name";
			    
%>
   <td width ="30%"><%=heading%></td>
<%   
	}
%>
   </tr>
<%
    for(int i=0;i<leng;i++)
	{
%> 
       <tr valign="top" class='formdata'>                   
<%					
		for(int j=0;j<count;j++)
		{
		  if(records[i][j]==null)
		  {
%>
       <td width="30%">Not Available</td>
<%   
          } 
		  else 
		  {   
%>
          <td width="30%"><%=records[i][j]%></td>
<%				
		  }		   
		}
%>					   
       </tr>
<%
	}
%>
       </table>
      </td>
    </tr>
 </table>
</form>

<%
  } else
     {
				String errorMessage = "No Data found for Customer Contracts WRT Terminal(s) ." ;
				session.setAttribute("Operation", "");
				session.setAttribute("ErrorCode","RNF");
				session.setAttribute("ErrorMessage",errorMessage);
        		session.setAttribute("NextNavigation","window.close()");
%>
				<jsp:forward page="../../ESupplyErrorPage.jsp" />
     
<% 
     }  
  }	
  }	   	  if (insetAttribute.equalsIgnoreCase("CustomerRegistrationWRTterminal") )
  {	  
	String[][]   records      = null;
	String       heading      = null;
	int cols = 0;
	int leng = 0;
	 String values="",customerId="",companyName="",registered="",city="";
    int count=0;
	 try
		   {			 
				customerId = "A.CUSTOMERID,";
				count++;
			
			 if(request.getParameter("P_CUSTOMERNAME")!=null)
			 {
				companyName = "A.COMPANYNAME,";
				count++;
			 }	
			 if(request.getParameter("P_REGISTRATION")!=null )
			 {
		        registered = "A.REGISTERED,";
				count++;
			 }
			 if(request.getParameter("P_CITY")!=null )
			 {
				city = "B.CITY,";
				count++;
			 }	
     		 values=values+customerId+companyName+registered+city;	
		   }
		    catch(Exception e)
		    {
				
				String errorMessage = " Problem in retrieving the Customer Registration details." ;
		    	session.setAttribute("Operation", "");
				session.setAttribute("ErrorCode","RNF");
				session.setAttribute("ErrorMessage",errorMessage);
        		session.setAttribute("NextNavigation","window.close()");
%>
     <jsp:forward page="../../ESupplyErrorPage.jsp" />
     
<%
		    }
  if(!values.equals(""))
  {	 
	 records = etransReportsRemote.getCustomerRegistration(values,terminalid,count);;
	 leng=records.length;
	 if(leng != 0)
     {	
%>	 
<table width="100%" cellpadding="0" cellspacing="0">
<tr bgcolor="ffffff">
<td>
<table width="100%" border="0" cellspacing="1" cellpadding="4" >
         <tr valign="top" class='formlabel'> 
          <td align="center">Customer Registration
            </td>
			<tr class='formdata'> 
              <td >U::Unregistered &nbsp;&nbsp;&nbsp;R::Registered
            </tr>
      </table>   
    
	 <table width="100%" border="0" cellspacing="1" cellpadding="4">
				<tr valign="top" class='formdata'> 
				<td width="33%"><b> Terminal : </b><%=terminalid%></td>
				<td width="33%" align="center"><b>User : </b><%=userId%></td>
				<td width="33%" align="right"><b>Date : </b><%=date%></td>
				</tr>
				<tr valign="top" class='formdata'> 
				<td>&nbsp;</td>
				<td colspan="2"> 

				</td>
				</tr>
          </table>
 <table width="100%"  border="0" cellspacing="1" cellpadding="4">
   <tr valign="top" class='formdata'> 
<%
    StringTokenizer st= new StringTokenizer(values,",");
	while(st.hasMoreTokens())
	{ 
	    cols++;
	    heading = st.nextToken().substring(2);
                if(heading.equalsIgnoreCase("CUSTOMERID"))
					   heading = "Customer Id";			 
			    if(heading.equalsIgnoreCase("COMPANYNAME"))
				       heading = "Customer Name";
			    if(heading.equalsIgnoreCase("REGISTERED"))
				       heading = "Registration Details";
			    if(heading.equalsIgnoreCase("City"))
				       heading = "City";
			    
%>
   <td width ="20%"><%=heading%></td>
<%   
	}
%>
   </tr>
<%
    for(int i=0;i<leng;i++)
	{
%> 
       <tr valign="top" class='formdata'>                   
<%					
		for(int j=0;j<count;j++)
		{
		  if(records[i][j]==null)
		  {
%>
       <td width="20%">Not Available</td>
<%   
          } 
		  else 
		  {   
%>
          <td width="20%"><%=records[i][j]%></td>
<%				
		  }		   
		}
%>					   
       </tr>
<%
	}
%>
       </table>
      </td>
    </tr>
 </table>
</form>
</html>
<%
  } else
     {
				String errorMessage = "No Data found for Customer Registration WRT Terminal(s) ." ;
				session.setAttribute("Operation", "");
				session.setAttribute("ErrorCode","RNF");
				session.setAttribute("ErrorMessage",errorMessage);
        		session.setAttribute("NextNavigation","window.close()");
%>
				<jsp:forward page="../../ESupplyErrorPage.jsp" />
     
<% 
   }  
  }	
  }	   	  	  
} catch(Exception exp)
  {
        String errorMessage = " Problem in retrieving  details.";
		session.setAttribute("Operation", "");
		session.setAttribute("ErrorCode","RNF");
		session.setAttribute("ErrorMessage",errorMessage);
        session.setAttribute("NextNavigation","window.close()");
%>
     <jsp:forward page="../../ESupplyErrorPage.jsp" />
     
<% 
      
  }
%>