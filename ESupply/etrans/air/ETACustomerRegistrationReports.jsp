<%--
% 
% Copyright (c) 1999-2001 by FourSoft, Pvt Ltd. All Rights Reserved.
% This software is the proprietary information of FourSoft, Pvt Ltd.
% Use is subject to license terms.
%
% esupply - v 1.x 
%
--%>
<%@ page import ="	java.sql.Timestamp,
				java.text.SimpleDateFormat,
				java.util.Date,
				java.util.ArrayList,
				java.util.StringTokenizer,
				org.apache.log4j.Logger,				
				com.qms.setup.ejb.sls.SetUpSession,
				com.qms.setup.ejb.sls.SetUpSessionHome,
				com.foursoft.esupply.common.java.ErrorMessage,
				com.foursoft.esupply.common.java.KeyValue,
				com.foursoft.esupply.common.util.ESupplyDateUtility"%>
<jsp:useBean id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/>

<%!
  private static Logger logger = null;
%>
<%         
		final String	FILE_NAME	=	"ETACustomerRegistrationReports.jsp ";
    logger  = Logger.getLogger(FILE_NAME);		
		int	headingSize       = 0,len=0;
		int	row		  =0,col=0;   
		int	noOfcols	  =0;
		ArrayList			keyValueList			=   new ArrayList();
		ErrorMessage		errorMessageObject		=   null;
		String	heading           = null;
		String  terminalId        = null;
		String  userId            = null;
		

		//shyam starts here
		int noofRecords	=	0;
		int noofPages	=	0;
		ArrayList			mainList	= null;
		String				sortBy                  =   request.getParameter("SortBy");
		String              sortOrder               =   request.getParameter("SortOrder");
		int		            pageNo                  =   Integer.parseInt(request.getParameter("PageNo"));
		String				noOfrecs1				= 	loginbean.getUserPreferences().getLovSize();
		int					noOfrecs				=	Integer.parseInt(noOfrecs1);
		//int			noOfrecs	=	5;
		

		String       imagePath     = "";
		 if("ASC".equalsIgnoreCase(sortOrder))
		   imagePath = request.getContextPath()+"/images/asc.gif";
		 else
		   imagePath = request.getContextPath()+"/images/desc.gif";

		//shyam ends here

        
        ESupplyDateUtility	eSupplyDateUtility  = new ESupplyDateUtility();
		String		  currentDate = eSupplyDateUtility.getCurrentDateString(loginbean.getUserPreferences().getDateFormat());
		  
		String	str		 		  	= "";
		String  operation	 		= request.getParameter("Operation");
		String  terminals[][]    	= null;

		terminalId        = loginbean.getTerminalId();
		userId            = loginbean.getUserId();
		String	customer  = request.getParameter("customer");
		if(customer==null)
			customer="";
		SimpleDateFormat df= new SimpleDateFormat("dd-MM-yy");
		        Date	d		  = new Date();
		String	date		  = df.format(d);

	     if(request.getParameter("queryStr")!=null)//Shyam condition
		 {
			 str=request.getParameter("queryStr");
			 noOfcols=Integer.parseInt(request.getParameter("noOfcols"));
		 }//shyam
		 else
		 {
			
			if (request.getParameter("CUSTOMERID") ==null )//&& request.getParameter("CUSTOMERID").equals("0"))
				{
				str+="CUSTOMERID,";
				noOfcols++;
				}				
			if	(request.getParameter("COMPANYNAME")!=null && request.getParameter("COMPANYNAME").equals("0"))
				{
				str+="COMPANYNAME,";
				noOfcols++;
				}	
				if	(request.getParameter("CONTACTNAME")!=null && request.getParameter("CONTACTNAME").equals("0"))
				{
				str+="CONTACTNAME,";
				noOfcols++;
				}	
				
			if	(request.getParameter("TERMINALID")!=null && request.getParameter("TERMINALID").equals("0"))
				{
				str+="TERMINALID,";
				noOfcols++;
				}
			if	(request.getParameter("CITY")!=null && request.getParameter("CITY").equals("0"))
				{
				str+="CITY,";
				noOfcols++;
				}
		 }
		


		 if(operation.equals("CustomerReports") && (!str.equals("")))
		 {				  		  
  	      try
	       {
		  // InitialContext initial = new InitialContext();
		  // SetUpSessionHome reportsHome=(SetUpSessionHome)initial.lookup("SetUpSessionBean");
		   
		   if(request.getParameter("registrationLevel").equals("T"))
		     customer="CustomerTerminal";
           else
             customer="corporate";
		   
		  // System.out.println("strstrstrstrstrstrstrstrstrstrstrstrstrstrstrstrstrstrstrstrstr :: "+str);
		  // System.out.println("noOfcolsnoOfcolsnoOfcolsnoOfcolsnoOfcolsnoOfcolsnoOfcolsnoOfcols :: "+noOfcols);
		   SetUpSessionHome reportsHome=(SetUpSessionHome) loginbean.getEjbHome("SetUpSessionBean");
		   SetUpSession reportRemote=(SetUpSession)reportsHome.create();
		   //terminals=reportRemote.getViewCustomerRegistrationReports( str, noOfcols,terminalId,customer );
		   mainList=reportRemote.getViewCustomerRegistrationReports( str, noOfcols,terminalId,customer,noOfrecs,pageNo,sortBy,sortOrder);
			

			//Shyam starts here
			if(mainList!=null && mainList.size()>0)
			{
				Integer  no_ofRecords   =   (Integer)mainList.get(0);
				Integer  no_ofPages		=	(Integer)mainList.get(1);
				terminals				=	(String [][])mainList.get(2);
				noofRecords				=	no_ofRecords.intValue();
				noofPages				=	no_ofPages.intValue();
			}
		    //Shyam ends here
			len = terminals.length;
			if(len>0)
			{
%>		
<html>
<head>
<title>CustomerRegistrationReports</title>       
<title>ESupplyErrorPage</title>       <%@ include file="../../ESEventHandler.jsp" %>
<link rel="stylesheet" href="../../ESFoursoft_css.jsp">

<!-- shyam starts here -->
<script language="JavaScript">
	function fetchSortedPageData(lable,pageNo,clickFrom)
	{
	
	   var sortBy     = lable;
	   var sortOrder  = "";   
	   var Operation  = document.forms[0].Operation.value;
	   if(lable==document.forms[0].sortedBy.value)
		{	 
		  if(clickFrom=="lable")
			{
			  if(document.forms[0].sortedOrder.value=="ASC")
				sortOrder = "DESC";
			  else
				sortOrder = "ASC";
			}
		  else
			sortOrder = document.forms[0].sortedOrder.value;
		}
		else
		{
			sortOrder = "ASC";
		}
		document.forms[0].action = "ETACustomerRegistrationReports.jsp?Operation="+Operation+"&SortBy="+sortBy+"&SortOrder="+sortOrder+"&PageNo="+pageNo;
		//alert(document.forms[0].action);
		document.forms[0].submit();
	}

	function gotoMainPage()
	{
		document.forms[0].action="../ETAViewAllAdmin.jsp?View=CustomerRegistration&registrationLevel=T";
		document.forms[0].submit();
	}

</script>
<!-- shyam  ends here -->

</head>
<body >
<form method="post" action="">
         <table width="100%" border="0" cellspacing="0" cellpadding="0">
	 <tr valign="top" bgcolor="#FFFFFF"> 
	<td>
		<table width="100%" border="0" cellspacing="1" cellpadding="4" >
		<tr valign="top" class='formlabel'> 
		<td width="100%" align="center" >
		<table width="100%" border="0" ><tr class='formlabel'><td>Customer   Registration 
		</td><td align=right><%=loginbean.generateUniqueId("ETACustomerRegistrationReports.jsp","CustomerRegistration")%></td></tr></table></td>
		</tr>
		</table>
     
		<table width="100%" border="0" cellspacing="1" cellpadding="4">
		<tr class='formdata' valign="top"> 
		<td ><b>Terminal : </b><%=terminalId%></td>
		<td align="center" ><b>User :</b><%=userId%></td>
		<td align="right" ><b>Date :</b> 
		<%=currentDate%>
		</td>
		</tr>
		<!-- <tr valign="top" align="left" class='formdata'> 
		<td width="100%" colspan=3>
		<b>R:</b> Registered&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		<b>U: </b>Unregistered
		</td>
		</tr> -->
		</table>
 <%
  			  StringTokenizer st1= new StringTokenizer(str,",");
			  int tableWidth = 0;  
		      while(st1.hasMoreTokens())
		        {
			         tableWidth = tableWidth +150;	 
					 st1.nextToken();	
		        }    
%>
      <table width=100%  border="0" cellspacing="1" cellpadding="4" >
          <tr valign="top" class='formheader'> 
<%
 		   StringTokenizer st= new StringTokenizer(str,",");
		   String headLabel = "";
		   while(st.hasMoreTokens())
		   {
			 //st.nextToken();
			// count++;

			 heading = st.nextToken();
			if(heading.equals("CUSTOMERID"))
			  {
					headLabel=heading;
					heading="Customer Id";
			  }
		    if(heading.equals("COMPANYNAME"))
				{
					headLabel=heading;
					heading="Customer Name";
				}
			if(heading.equals("CONTACTNAME"))
				{
					headLabel=heading;
					heading="Contact Name";	
				}
			if(heading.equals("TERMINALID"))
				{
					headLabel=heading;
					heading="Terminal Id";	
				}
			if(heading.equals("CITY"))
				{
					headLabel=heading;
					heading="City";	
				}
				
%>
        <td  colspan="1">
		<A href='###' onClick='fetchSortedPageData("<%=headLabel%>","<%=pageNo%>","lable")' onmouseover='status="Sort by <%=heading%> as";return true;' onmouseout="status = '';return true;" title="Sort by <%=heading%> as">
		<%=heading%>
		<%  if(sortBy.equalsIgnoreCase(headLabel)){%>					
				      <img SRC=<%=imagePath%> border="0">
		 <%}%>
		</A>

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
					  
					  if(terminals[i][j]==null)
					   {
%>
                          <td  >Not Available</td>
<%   
           		        }  
						else
						{   
%>
                          <td  ><%=terminals[i][j]%></td>
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
<table width="100%" >
<tr class="text" vAlign="top">
<td align="left"  width="100%" >
      <%         int  currentPageNo =pageNo;
				 if(currentPageNo != 1)
			      {%>
			     <a href="#" onClick='fetchSortedPageData("<%=sortBy%>","<%=currentPageNo-1%>","pageNo")'><img src="../../images/Toolbar_backward.gif"  border="0"></a>
     <%			  } 
					int multiplier = 1;
					int startNo = 0;
					int endNo = 0;
					int noOfSegments	=	Integer.parseInt(loginbean.getUserPreferences().getSegmentSize());

					if(currentPageNo > noOfSegments)
					{
						multiplier = currentPageNo / noOfSegments;
						if(currentPageNo % noOfSegments != 0)
							startNo = noOfSegments * multiplier;
						else
							startNo = noOfSegments * (multiplier - 1);
					}
					if(noofPages > startNo)
					{
						if(noofPages > startNo + noOfSegments)
							endNo = startNo + noOfSegments;
						else
							endNo = startNo + (noofPages - startNo);
					} else
					{
						endNo = noofPages;
					}
					for(int i = startNo; i < endNo; i++)
						if(currentPageNo == i + 1){%>
							<%=(i + 1)%>&nbsp;
						<%}else{%>
							<a href="#" onClick="fetchSortedPageData('<%=sortBy%>','<%=(i + 1)%>','pageNo')" ><%=(i + 1)%></a>&nbsp;
<%	                   }
				  
				  if(currentPageNo != noofPages)
			      { %>
                   <a href="#" onClick='fetchSortedPageData("<%=sortBy%>","<%=currentPageNo+1%>","pageNo")'><img src="../../images/Toolbar_forward.gif" border="0"></a>
   <%		      }
    %>       
			</td>
			  </tr>
			  <tr class="text" vAlign="top">

						
						<td  width="100%" align="right" bgcolor="white">
						<input type="button" value="Continue" name="close" onClick="gotoMainPage();" class='input'>
						</td>
						<!-- shyam starts here -->
						<INPUT TYPE="hidden" name='Operation' value="<%=request.getParameter("Operation")%>">
						<INPUT TYPE="hidden" name='sortedOrder' value="<%=sortOrder%>">
						<INPUT TYPE="hidden" name='sortedBy' value="<%=sortBy%>">
						<INPUT TYPE="hidden" name='pageNo' value="<%=pageNo%>">

						<INPUT TYPE="hidden" name='customer' value="<%=customer%>">
						<INPUT TYPE="hidden" name='queryStr' value="<%=str%>">
						<INPUT TYPE="hidden" name='noOfcols' value="<%=noOfcols%>">
						<INPUT TYPE="hidden" name='registrationLevel' value="<%=request.getParameter("registrationLevel")%>">
						<!-- shyam ends here -->
		</tr>
</table>
 </form>
 </body>
</html>
<%
}
 else
   {
		errorMessageObject = new ErrorMessage(  "No relevant data was found.  ","../ETAViewAllAdmin.jsp?View=CustomerRegistration&registrationLevel=C"); 
		keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
		keyValueList.add(new KeyValue("View","CustomerRegistration")); 	
		errorMessageObject.setKeyValueList(keyValueList);
		request.setAttribute("ErrorMessage",errorMessageObject);
%>
		<jsp:forward page="../../ESupplyErrorPage.jsp" />
<%  
   }

  } // try
   catch(Exception e)
	{
	   e.printStackTrace();
     	//Logger.info(FILE_NAME,"problem in execution : ",e.toString());
      logger.info(FILE_NAME+"problem in execution : "+e.toString());

	}
 } // if main
 	
%>