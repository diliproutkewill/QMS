<% // @@ Murali Modified Regarding Date Format %>

<%@ page import ="        javax.naming.InitialContext,java.sql.Timestamp,
				 		  com.foursoft.etrans.setup.company.bean.HORegistrationJspBean,
						  com.foursoft.etrans.setup.jointventure.bean.JointVentureJspBean,
						  java.util.ArrayList,
						  org.apache.log4j.Logger,
				 		  java.util.Vector,java.util.StringTokenizer,
						  java.util.Enumeration, com.foursoft.esupply.common.java.ErrorMessage,
						  com.foursoft.esupply.common.java.KeyValue,
				 		  com.qms.setup.ejb.sls.SetUpSession,
                            com.qms.setup.ejb.sls.SetUpSessionHome,
						  com.foursoft.etrans.common.util.java.FormatDate,
						  com.foursoft.esupply.common.util.ESupplyDateUtility " %>
<% // @@ Murali Modified Regarding Date Format %>

<jsp:useBean id="loginbean"  class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session" />
<%!
  private static Logger logger = null;
%>

<% 
  
	String		 FILE_NAME	  = "ETASuperUserViewAllReport.jsp";
  logger  = Logger.getLogger(FILE_NAME);
	//Logger.info(FILE_NAME,"ENTERED HERE");
  logger.info(FILE_NAME+"ENTERED HERE");
	ErrorMessage errorMessageObject  = null;
	ArrayList    keyValueList	     = new ArrayList();
	String       terminalid   = null; 
	String       userId       = null; 
	String       insetAttribute   = null;
	String		 insetAttribute1  =	null;
	String[]     countryid    = null;
    String[]     countryname  = null;
	String[]     currencyid   = null;
	String[]     region       = null;
	Vector       details      =	new Vector();
	terminalid 	=  loginbean.getTerminalId();
	userId 		=  loginbean.getUserId();
	String		 shipmentMode	=	"Air";
    String displayValue		=	null;      
	int 	in					=	0;

	try
    {
      // @@ Murali Modified Regarding Date Format
	  ESupplyDateUtility eSupplyDateUtility = new ESupplyDateUtility();	

	  String date = eSupplyDateUtility.getCurrentDateString(loginbean.getUserPreferences().getDateFormat());
	  // @@ Murali Modified Regarding Date Format
	  insetAttribute 				= 	(String)session.getAttribute("InsetAttribute");	
	  insetAttribute1=insetAttribute;
      insetAttribute1		=	insetAttribute1.substring(0,1).toUpperCase()+insetAttribute.substring(1).toLowerCase();

	  if (insetAttribute.endsWith("WRTTERMINAL" ) )
      { 
		 displayValue=insetAttribute.substring(0,insetAttribute.lastIndexOf("WRTTERMINAL" ));    
      }
      if(insetAttribute.endsWith("MASTER")) 
	  {
	 		/* in	= insetAttribute.indexOf("MASTER");	
	  	 displayValue=insetAttribute.substring(0,(in));	
		 displayValue = displayValue +"  "+"MASTER";			     	 */
		  in	= insetAttribute1.indexOf("master");	
          displayValue=insetAttribute1.substring(0,(in));	
		  displayValue = displayValue +"  "+"Master";	
		     	 
      }
	  else if(insetAttribute.endsWith("REGISTRATION")) 
	  {
	 	 /*in	= insetAttribute.indexOf("REGISTRATION");	
	  	 displayValue=insetAttribute.substring(0,(in));	
		 displayValue = displayValue +"  "+"REGISTRATION";*/
		 in	= insetAttribute1.indexOf("registration");	
 	  	 displayValue=insetAttribute1.substring(0,(in));	
		 displayValue = displayValue +"  "+"Registration";
	  }
	  else if(insetAttribute.equals("SERVICELEVEL"))
	  {
		  displayValue="Service Level";
	  }
	  else
	  {
		  
	      // @@ Murali Modified On 20050427 Regarding SPETI - 5713 
		  // displayValue=insetAttribute.toString();
		  if("AGENTJV".equalsIgnoreCase(insetAttribute.toString()))
		  {
		       displayValue = "Agent/Joint Venture";
			   
	 	  }
	      else 
		   {
		  displayValue=insetAttribute.toString();
      }
		   // @@ Murali Modified On 20050427 Regarding SPETI - 5713 
	  }
	  
	  InitialContext initial	=	new InitialContext();
      SetUpSessionHome 	etransReportsHome 		= (SetUpSessionHome )initial.lookup( "SetUpSessionBean" );
      SetUpSession  	etransReportsRemote 	= (SetUpSession) etransReportsHome.create();
%>		
		
<html>
<head>
<title><%=insetAttribute%> View all Report</title>
<%@ include file="/ESEventHandler.jsp" %>
<link rel="stylesheet" href="../ESFoursoft_css.jsp">
</head>
<body  leftmargin="5" topmargin="0" marginwidth="0" marginheight="0" >
<form method="post"  >
  <table align="center" width="100%" border="0" cellspacing="0" cellpadding="4">
    <tr  valign="top" bgcolor="white"> 
      <td width="100%">
	  
	   <table width="100%" border="0"  class='formlabel' >
        <tr valign="top" > 
        <td ><%=displayValue%> - View All</td><td align=right><%=loginbean.generateUniqueId("ETASuperUserViewAllReport.jsp",(insetAttribute).toLowerCase())%></td>
        </tr>
      </table>
  	 <table align="center" width="100%" border="0" cellspacing="0" cellpadding="4">
          <tr valign="top" class='formdata'> 
            <td width="33%" align="left"> Terminal : <%=terminalid%></td>
            <td width="33%"> 
              <div align="center">User : <%=userId%></div></td>
            <td width="33%"> 
              <div align="right">Date : <%=date%></div></td>
          </tr>
		  <tr valign="top" > 
            <td></td>
            <td colspan="2"> 
              <div align="right"></div>
            </td>
          </tr>
          </table>
<%	
  if   (insetAttribute.equalsIgnoreCase("countrymaster") )
  {	  
       //com.foursoft.etrans.common.java.CountryMaster  CountryObj = (com.foursoft.etrans.common.java.CountryMaster)etransReportsRemote.getCountryDetails();
	 	details = (Vector)etransReportsRemote.getCountryDetails();
		if(details.size() > 0)
    	{   
%>				  
       	 
 		<table width="100%"  border="0" cellspacing="2" cellpadding="4" >
   		<tr valign="top" class='formheader'> 
            <td width="25%" align="left">Country Id</td>
<%	      
         if(request.getParameter("P_COUNTRYNAME")!=null)
		 {
%>
            <td width="25%" align="left">Country Name</td>
<%	     
          }
		 if(request.getParameter("P_CURRENCYID")!=null)
		 {
%> 
           <td width="25%" align="left">Currency Id</td>
<%	      
         } 
		  if(request.getParameter("P_REGION")!=null) 
		 {
%>
            <td width="25%" align="left">Region</td>
<%		 
         }
%>
      </tr>
	  
<%   
	Enumeration enum1= details.elements();
	while(enum1.hasMoreElements())
	{
		String strCountry=(String)enum1.nextElement();
		StringTokenizer	tokenCountry = new StringTokenizer(strCountry,"?");
	 	String arrCountry[] = new String[4];
	 	int i=0;
		while(tokenCountry.hasMoreTokens())
		{
			arrCountry[i++] =(String)tokenCountry.nextToken();
		}  
%>     
	  <tr valign="top" class='formdata'> 
         <td width="25%" align="left"><%=arrCountry[0]%></td>
<%	  
		if(request.getParameter("P_COUNTRYNAME")!=null)
	 	{
%>
            <td width="25%" align="left"><%=arrCountry[1]%></td>
<%	
      	} 
		if(request.getParameter("P_CURRENCYID")!=null)
	    {
%>
            <td width="25%" align="left"><%=arrCountry[2]%></td>
<%		
        }
		if(request.getParameter("P_REGION")!=null)
		{
%> 
           <td width="25%" align="left"><%=arrCountry[3]%></td>
<%	
     	}
%>  
		 </tr> 
<%	  
    }//End of while. 
%> 
       </table>
      </td>
     </tr><!-- Modified By G.Srinivas to resolve the QA-Issue -->
	<tr valign="top" border="0"> 
                <td width="100%"   align="right" bgcolor="white">
                <input type="button" value="Continue" name="close" onClick="javascript:history.go(-1);" class='input'>
				</td>
    </tr>
    </table>
   </form>
<%
	}else
    {
 		String errorMessage = "No data was found." ;
		session.setAttribute("Operation", "");
		session.setAttribute("ErrorCode","RNF");
		session.setAttribute("ErrorMessage",errorMessage);
        session.setAttribute("NextNavigation","javascript:window.close()");
%>
		<jsp:forward page="../ESupplyErrorPage.jsp" />
				
<%

	}
}//End of outer if loop.
	else  if (insetAttribute.equalsIgnoreCase("companyregistration"))
  	{
		HORegistrationJspBean HORegObj = new HORegistrationJspBean();
		details = (Vector)etransReportsRemote.getCompanyInfo();
	
	if(details.size() > 0)
    {
%>     
     <table width="100%"  border="0" cellspacing="2" cellpadding="4" >
   <tr valign="top" class='formheader'> 
<%        if(request.getParameter("P_COMPANYID")!=null) 
		  {
%>
		    <td width="15%" align="left">Company Id</td>

<%	      }			
	      
          if(request.getParameter("P_COMPANYNAME")!=null) 
		  {
%>
            <td width="18%" align="left">Company Name</td>
<%	     
          } 
          if(request.getParameter("P_HCURRENCY")!=null)
		  {
%> 
           <td width="18%" align="left">HO Currency</td>
<%	      
          }
		  if(request.getParameter("P_DAYLIGHTSAVING")!=null)
		  {
%>
            <td width="18%" align="left">Daylight Saving</td>
<%		 
          }
		//  if(request.getParameter("P_TIMEZONE")!=null)
		 // {
%> 
       <!--    <td width="25%" align="left">Time Zone</td>-->
<%	      
		//}
		  if(request.getParameter("P_CITY")!=null)
		  {
%>
            <td width="20%" align="left">City</td>
<%		 
          }
%>
      </tr>
	  
<%
  for(int i=0;i<details.size();i++)
  {
	HORegObj=(HORegistrationJspBean)details.elementAt(i);
%>     
	  <tr valign="top" class='formdata'> 
<%    if(request.getParameter("P_COMPANYID")!=null) 
	   {
%>
		    <td width="15%" align="left"><%=HORegObj.getCompanyId()%></td>

<%	   }		
	  
      if(request.getParameter("P_COMPANYNAME")!=null)
	  {
%>
            <td width="15%" align="left"><%=HORegObj.getCompanyName()%></td>
<%	
      }
	  if(request.getParameter("P_HCURRENCY")!=null)
	  {
%>
            <td width="15%" align="left"><%=HORegObj.getHCurrency()%></td>
<%		
      }
	  if(request.getParameter("P_DAYLIGHTSAVING")!=null)
	  {
%> 
           <td width="15%" align="left"><%=HORegObj.getDayLightSavings()%></td>
<%	
      }
	//  if(request.getParameter("P_TIMEZONE")!=null && (request.getParameter("P_TIMEZONE")).equals("null"))
	//  {
%>
     <!--       <td width="15%" align="left"><%=HORegObj.getTimeZone()%></td>-->

<%
	  if(request.getParameter("P_CITY")!=null)
	  {
%> 
           <td width="15%" align="left"><%=HORegObj.getCity()%></td>
<%	
      }
%>   </tr> 
<%	  
    } //End of for loop.
%> 
       </table>
      </td>
      </tr><!-- Modified By G.Srinivas to resolve the QA-Issue -->
	<tr valign="top" border="0"> 
                <td width="100%"   align="right" bgcolor="white">
                <input type="button" value="Continue" name="close" onClick="javascript:history.go(-1);" class='input'>
				</td>
    </tr>
    </table>
   </form>
<%
    }else
    {
		String errorMessage = "No data found" ;
		session.setAttribute("Operation", "");
		session.setAttribute("ErrorCode","RNF");
		session.setAttribute("ErrorMessage",errorMessage);
        session.setAttribute("NextNavigation","javascript:window.close()");
%>
		<jsp:forward page="../ESupplyErrorPage.jsp" />
				
<%   }
  }
   else if (insetAttribute.equalsIgnoreCase("agentjv"))
   {
	JointVentureJspBean AJVObj = new JointVentureJspBean();
	details = (Vector)etransReportsRemote.getAgentJVDetails();
	if(details.size() > 0)
    {   
%>      
      <table width="100%"  border="0" cellspacing="1" cellpadding="4" >
   <tr valign="top" class='formheader'> 

            <td width="11%" align="left">Company Id</td>
<%	      
          if(request.getParameter("P_COMPANYNAME")!=null) 
		  {
%>
            <td width="11%" align="left">Company Name</td>
<%	     
          } 
		  if(request.getParameter("P_EQUITY")!=null)
		  {
%> 
           <td width="11%" align="left">Percent Of Equity</td>
<%	      
          }
		  if(request.getParameter("P_A_JV")!=null)
		  {
%>
            <td width="11%" align="left">Agent/JV</td>
<%		 
          }
		  if(request.getParameter("P_SALESNO")!=null)
		  {
%> 
           <td width="9%" align="left">SalesTax Reg.No</td>
<%	      
          }
          if(request.getParameter("P_CONTACTPERSON")!=null)
		  {
%> 
           <td width="9%" align="left">Contact Person</td>
<%	      
          }
		  
		  if(request.getParameter("P_DESIGNATION")!=null)
		  {
%> 
           <td width="9%" align="left">Designation</td>
<%	      
          }
		  if(request.getParameter("P_CITY")!=null)
		  {
%>
            <td width="9%" align="left">City</td>
<%		 
          }
		  if(request.getParameter("P_LOCALCURRENCY")!=null)
		  {
%>
            <td width="9%" align="left">Local Currency</td>
<%		 
          }
		  
%>		  
      </tr>
	  
<% 
  for(int i=0;i<details.size();i++)
  {
	AJVObj=(JointVentureJspBean)details.elementAt(i);
%>     
	  <tr valign="top" class='formdata'> 
         <td width="11%" align="left"><%=AJVObj.getCompanyId()%></td>
<%	  
      if(request.getParameter("P_COMPANYNAME")!=null)
	  {
%>
            <td width="11%" align="left""><%=AJVObj.getCompanyName()%></td>
<%	
      }
	  if(request.getParameter("P_EQUITY")!=null)
	  {
%>
            <td width="11%" align="left"><%=AJVObj.getPercentOfEquity()%></td>
<%		
      }
	  if(request.getParameter("P_A_JV")!=null)
	  {
%> 
           <td width="11%" align="left"><%=AJVObj.getAgentJV()%></td>
<%	
      }
	  if(request.getParameter("P_SALESNO")!=null)
	  {
%>
            <td width="11%" align="left"><%=AJVObj.getSalesTaxRegNo()%></td>
<%		
      }
	  if(request.getParameter("P_CONTACTPERSON")!=null)
	  {
%>
            <td width="11%" align="left"><%=AJVObj.getContactPerson() %></td>
<%		
      }
	  if(request.getParameter("P_DESIGNATION")!=null)
	  {
%>
            <td width="11%" align="left"><%=AJVObj.getDesignation()%></td>
<%		
      }
	  if(request.getParameter("P_CITY")!=null)
	  {
%> 
           <td width="11%" align="left"><%=AJVObj.getCity()%></td>
<%	
      }
	  if(request.getParameter("P_LOCALCURRENCY")!=null)
	  {
%>
            <td width="11%" align="left"><%=AJVObj.getLocalCurrency()%></td>
<%		
      }
 
%>   </tr> 
<%	  
    }//End of for loop.
%> 
       </table>
      </td>
     </tr><!-- Modified By G.Srinivas to resolve the QA-Issue -->
	<tr valign="top" border="0"> 
                <td width="100%"   align="right" bgcolor="white">
                <input type="button" value="Continue" name="close" onClick="javascript:history.go(-1);" class='input'>
				</td>
    </tr>
    </table>
   </form>
<%
    }else
    {
		String errorMessage = "No data found" ;
		session.setAttribute("Operation", "");
		session.setAttribute("ErrorCode","RNF");
		session.setAttribute("ErrorMessage",errorMessage);
        session.setAttribute("NextNavigation","ETAShowViewAllReport.jsp?viewParameter=agentjv");
%>
		<jsp:forward page="../ESupplyErrorPage.jsp" />
				
<%  }
  }
  	else if (insetAttribute.equalsIgnoreCase("chargesmaster"))
    {
	//com.foursoft.etrans.common.java.ChargeMasterJSPBean ChargeObj = new com.foursoft.etrans.common.java.ChargeMasterJSPBean();
	details = etransReportsRemote.getChargeDetails();
	if(details.size() > 0)
    {   
%>   
     <table width="100%"  border="0" cellspacing="2" cellpadding="4"  >
   <tr valign="top" class='formheader'> 
     
            <td width="20%" align="left">Charge Id</td>
<%	      
        if(request.getParameter("P_CHARGEDESC")!=null) 
		 {
%>
            <td width="30%" align="left">Description</td>
<%	     
          } 
		  if(request.getParameter("P_COSTINCURREDAT")!=null)
		  {
%> 
           <td width="30%" align="left">Cost Incurred At</td>
		   
<%	      
          }
%>
      </tr>
<%
	 Enumeration enum1= details.elements();
	 while(enum1.hasMoreElements())
	 {
		String strcharge=(String)enum1.nextElement();
		StringTokenizer	tokencharge = new StringTokenizer(strcharge,"?");
		String arrcharge[] = new String[3];
		 int i=0;
		 while(tokencharge.hasMoreTokens())
		 {
			arrcharge[i++] =(String)tokencharge.nextToken();
		
	 	}  
%>     
	  <tr valign="top" class='formdata'> 
	   
	    <td width="20%" align="leftr"><%=arrcharge[0]%></td>
      
<%	  
      if(request.getParameter("P_CHARGEDESC")!=null)
	  {
%>
            
    <td width="30%" align="left"><%=arrcharge[1]%></td>
<%	
      }
	  if(request.getParameter("P_COSTINCURREDAT")!=null)
	  {
%>
            <td width="30%" align="left"><%=arrcharge[2]%></td>
<%		
      }
%>   </tr> 
<%	  
    }//End of while 
%> 
       </table>
      </td>
    </tr>
    </table>
   </form>
<%
    }else
    {
		String errorMessage = "No data was found" ;
		session.setAttribute("Operation", "");
		session.setAttribute("ErrorCode","RNF");
		session.setAttribute("ErrorMessage",errorMessage);
        session.setAttribute("NextNavigation","javascript:window.close()");
%>
		<jsp:forward page="../ESupplyErrorPage.jsp" />
				
<%  }
   
}
else if (insetAttribute.equalsIgnoreCase("serviceLevel"))
   {
	//com.foursoft.etrans.common.java.ServiceLevelJspBean ServiceObj = new com.foursoft.etrans.common.java.ServiceLevelJspBean();
    details = etransReportsRemote.getServiceLevelDetails("ALL","");
	if(details.size() > 0)
    {   
	
%> 
     <table width="100%"  border="0" cellspacing="2" cellpadding="4" >
   <tr valign="top" class='formheader'> 
                 
            <td width="20%" align="left">ServiceLevel Id</td>
<%	      
        if(request.getParameter("P_SERVICEDESC")!=null) 
		 {
%>
            <td width="30%" align="left">ServiceLevel Description</td>
<%	     
          } 
		  if(request.getParameter("P_REMARKS")!=null)
		  {
%> 
           <td width="30%" align="left">Remarks</td>
<%	      
          }
%>
      </tr>
	  
<% 
  	Enumeration enum1= details.elements();
	 while(enum1.hasMoreElements())
	 {
		String strservicelevel=(String)enum1.nextElement();
		StringTokenizer	tokenservicelevel = new StringTokenizer(strservicelevel,"?");
	 String arrservicelevel[] = new String[3];
	 int i=0;
	 while(tokenservicelevel.hasMoreTokens())
	 {
		arrservicelevel[i++] =(String)tokenservicelevel.nextToken();
		
	 }  
%>     
	  <tr valign="top" class='formdata'> 
         <td width="20%" align="left"><%=arrservicelevel[0]%></td>
		 
<%	  
      if(request.getParameter("P_SERVICEDESC")!=null)
	  {
%>
            <td width="40%" align="left"><%=arrservicelevel[1]%></td>
<%	
      }
	  if(request.getParameter("P_REMARKS")!=null)
	  {
%>
            <td width="30%" align="left"><%=arrservicelevel[2]%></td>
<%		
      }
%>   </tr> 
<%	  
    } 
%> 
       </table>
      </td>
     </tr><!-- Modified By G.Srinivas to resolve the QA-Issue -->
	<tr valign="top" border="0"> 
                <td width="100%"   align="right" bgcolor="white">
                <input type="button" value="Continue" name="close" onClick="javascript:history.go(-1);" class='input'>
				</td>
    </tr>
    </table>
   </form>
</body>
</html>
<%
    }else
    {
 		String errorMessage = "No data found" ;
		session.setAttribute("Operation", "");
		session.setAttribute("ErrorCode","RNF");
		session.setAttribute("ErrorMessage",errorMessage);
        session.setAttribute("NextNavigation","ETAShowViewAllReport.jsp?viewParameter=serviceLevel");
%>
		<jsp:forward page="../ESupplyErrorPage.jsp" />
				
<%  }
}  else if (insetAttribute.equalsIgnoreCase("commoditymaster"))
   {
		//shyam starts here
		int noofRecords	=	0;
		int noofPages	=	0;
		ArrayList			mainList	= null;
		String				sortBy                  =   request.getParameter("SortBy");
		String              sortOrder               =   request.getParameter("SortOrder");
		int		            pageNo                  =   Integer.parseInt(request.getParameter("PageNo"));
		String				noOfrecs1				= 	loginbean.getUserPreferences().getLovSize();
		int			noOfrecs	=	Integer.parseInt(noOfrecs1);
		//int			noOfrecs	=	10;

		String       imagePath     = "";
		 if("ASC".equalsIgnoreCase(sortOrder))
		   imagePath = request.getContextPath()+"/images/asc.gif";
		 else
		   imagePath = request.getContextPath()+"/images/desc.gif";

		//shyam ends here
//Shyam starts here
		
	//Shyam ends here
	//com.foursoft.etrans.common.java.CommodityJspBean CommodityObj = new com.foursoft.etrans.common.java.CommodityJspBean();
	//details = (Vector)etransReportsRemote.getViewCommodityReports();
	mainList = (ArrayList)etransReportsRemote.getViewCommodityReports(noOfrecs,pageNo,sortBy,sortOrder);
		Integer  no_ofRecords   =   (Integer)mainList.get(0);
		Integer  no_ofPages		=	(Integer)mainList.get(1);
		details				=	(Vector)mainList.get(2);
		noofRecords				=	no_ofRecords.intValue();
		noofPages				=	no_ofPages.intValue();
	
%>
<!-- shyam starts here -->
<script language="JavaScript">
	function fetchSortedPageData(lable,pageNo,clickFrom)
	{
	   var sortBy     = lable;
	   var sortOrder  = "";   
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
		document.forms[0].action = "ETASuperUserViewAllReport.jsp?SortBy="+sortBy+"&SortOrder="+sortOrder+"&PageNo="+pageNo;
		//alert(document.forms[0].action);
		document.forms[0].submit();
	}

</script>
<!-- shyam  ends here -->
<%

	if(details.size() > 0)
    {   
	  
%> 
    <table width="100%"  border="0" cellspacing="2" cellpadding="4"  >
   <tr valign="top" class='formheader' > 
        <td width="15%" align="left">
			<A href='###' onClick='fetchSortedPageData("COMODITYID","<%=pageNo%>","lable")' onmouseover='status="Sort by Commodity Id as";return true;' onmouseout="status = '';return true;" title="Sort by Commodity Id as">
			Commodity Id
		<%  if(sortBy.equalsIgnoreCase("P_COMMODITYID")){%>					
				      <img SRC=<%=imagePath%> border="0">
		 <%}%>
		</A>	
			
		</td>
<%
	   if(request.getParameter("P_COMMODITYDESC")!=null)
	  {
		 if(request.getParameter("P_COMMODITYDESC").equals("NO"))
		{
%>
          <td width="35%" align="left">
			<A href='###' onClick='fetchSortedPageData("COMODITYDESCRIPTION","<%=pageNo%>","lable")' onmouseover='status="Sort by Commodity Description as";return true;' onmouseout="status = '';return true;" title="Sort by Commodity Description as">
		  Commodity Description
		  <%  if(sortBy.equalsIgnoreCase("P_COMMODITYDESC")){%>					
				      <img SRC=<%=imagePath%> border="0">
		 <%}%>
		</A>
		  </td>
<%
		}
	  }
     if(request.getParameter("P_HANDLINGINFO")!=null)
	 {	
		if(request.getParameter("P_HANDLINGINFO").equals("NO"))
		{
%>
          <td width="35%" align="left">
			<A href='###' onClick='fetchSortedPageData("HANDLINGINFO","<%=pageNo%>","lable")' onmouseover='status="Sort by Handling Information as";return true;' onmouseout="status = '';return true;" title="Sort by Handling Information as">
		  Handling Information
		  <%  if(sortBy.equalsIgnoreCase("P_HANDLINGINFO")){%>					
				      <img SRC=<%=imagePath%> border="0">
		 <%}%>
		</A>
		  </td>
<%
		}
     }
	 if(request.getParameter("P_COMMODITYTYPE")!=null)
	 {
		if(request.getParameter("P_COMMODITYTYPE").equals("NO"))
		{
%>
         <td width="15%" align="left">
			<A href='###' onClick='fetchSortedPageData("COMODITYTYPE","<%=pageNo%>","lable")' onmouseover='status="Sort by Commodity Type as";return true;' onmouseout="status = '';return true;" title="Sort by Commodity Type as">
		 Commodity Type
		  <%  if(sortBy.equalsIgnoreCase("P_COMMODITYTYPE")){%>					
				      <img SRC=<%=imagePath%> border="0">
		 <%}%>
		</A>
		 </td>
<%
		}
	 }
%>
          </tr>
<%
	
	  //CommodityObj=(com.foursoft.etrans.common.java.CommodityJspBean)details.elementAt(i);	
	  Enumeration enum1= details.elements();
	 while(enum1.hasMoreElements())
	 {
		String strComodity=(String)enum1.nextElement();
		StringTokenizer	tokenCommodity = new StringTokenizer(strComodity,"?");
		String arrCommodity[] = new String[4];
		int i=0;
		while(tokenCommodity.hasMoreTokens())
		{
			arrCommodity[i++] =(String)tokenCommodity.nextToken();
	 	}  
%>				
          <tr class='formdata' valign="top"> 
            <td width="15%" align="left"><%=arrCommodity[0]%></td>
<%
	  if(request.getParameter("P_COMMODITYDESC")!=null )
	  {
		if(request.getParameter("P_COMMODITYDESC").equals("NO"))
		{
%>
            <td width="35%" align="left"><%=arrCommodity[1]%></td>
<%
		}
      }
	  if(request.getParameter("P_HANDLINGINFO")!=null)
	  {
		 if(request.getParameter("P_HANDLINGINFO").equals("NO"))
		{
%>
            <td width="35%" align="left"><%=arrCommodity[2]%></td>
<%
		}
      }
	  if(request.getParameter("P_COMMODITYTYPE")!=null)
	  {
		  if(request.getParameter("P_COMMODITYTYPE").equals("NO"))
		  {
%>
            <td width="15%" align="left"><%=arrCommodity[3]%></td>
<%
		  }
	  }
%>   </tr> 
<%	  
	 
	}
%>
<!--        </table>
        </div>
       </td>
      </tr> --><!-- Modified By G.Srinivas to resolve the QA-Issue -->
	<tr valign="top" border="0"> 
                
				<td   width='100%' colspan="4" align="right" bgcolor="white">
                <input type="button" value="Continue" name="close" onClick="javascript:history.go(-1);" class='input'>
						
						<INPUT TYPE="hidden" name='sortedOrder' value="<%=sortOrder%>">
						<INPUT TYPE="hidden" name='sortedBy' value="<%=sortBy%>">
						<INPUT TYPE="hidden" name='pageNo' value="<%=pageNo%>">
						<INPUT TYPE="hidden" name='P_COMMODITYID' value="<%=request.getParameter("P_COMMODITYID")%>">
						<INPUT TYPE="hidden" name='P_COMMODITYDESC' value="<%=request.getParameter("P_COMMODITYDESC")%>">
						<INPUT TYPE="hidden" name='P_HANDLINGINFO' value="<%=request.getParameter("P_HANDLINGINFO")%>">
						<INPUT TYPE="hidden" name='P_COMMODITYTYPE' value="<%=request.getParameter("P_COMMODITYTYPE")%>">

				</td>
    </tr>

	<tr valign="top" border="0">
		<td  width='100%' colspan="4" align="left" bgcolor="white">
								<!-- shyam starts here -->
					 <%         int  currentPageNo =pageNo;
					 if(currentPageNo != 1)
					  {%>
					 <a href="#" onClick='fetchSortedPageData("<%=sortBy%>","<%=currentPageNo-1%>","pageNo")'><img src="../images/Toolbar_backward.gif"  border="0"></a>
<%					  } 

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
					   <a href="#" onClick='fetchSortedPageData("<%=sortBy%>","<%=currentPageNo+1%>","pageNo")'><img src="../images/Toolbar_forward.gif" border="0"></a>
<%					  }

%>        


							<!-- shyam ends here -->
					</td>

		</tr>

	       </table>
        </div>
       </td>
      </tr><!-- Modified By G.Srinivas to resolve the QA-Issue -->

  </table>
    
 </form>
</body>
</html>
<%
   }else
   {
  		String errorMessage = "No data found" ;
		session.setAttribute("Operation", "");
		session.setAttribute("ErrorCode","RNF");
		session.setAttribute("ErrorMessage",errorMessage);
        session.setAttribute("NextNavigation","javascript:window.close()");
%>
		<jsp:forward page="../ESupplyErrorPage.jsp" />
				
<%  }
   
}else if (insetAttribute.equalsIgnoreCase("taxmaster"))
    {
	//com.foursoft.etrans.common.java.TaxMaster TaxObj = new com.foursoft.etrans.common.java.TaxMaster();
	details = (Vector)etransReportsRemote.getTaxDetails(loginbean.getTerminalId());
	if(details.size() > 0)
    {   
%>     
  <table width="100%"  border="0" cellspacing="2" cellpadding="4"  >
   <tr valign="top" class='formheader'> 
            <td width="20%" align="left">Tax Id</td>
<%	      
          if(request.getParameter("P_TAXDESC")!=null) 
		  { 	
%>
            <td width="35%" align="left">Tax Description</td>
<%	     
          } 
		  if(request.getParameter("P_REMARKS")!=null)
		  {
%> 
           <td width="35%" align="left">Remarks</td>
<%	      
          }
%>
      </tr>
	  
<% 
   	Enumeration enum1= details.elements();
	 while(enum1.hasMoreElements())
	 {
		String strTax=(String)enum1.nextElement();
		StringTokenizer	tokenTax = new StringTokenizer(strTax,"?");
		 String arrTax[] = new String[3];
		 int i=0;
		 while(tokenTax.hasMoreTokens())
		 {
			arrTax[i++] =(String)tokenTax.nextToken();
		 }  
%>     
	  <tr valign="top" class='formdata'> 
         <td width="20%" align="left"><%=arrTax[0]%></td>
<%	  
     	 if(request.getParameter("P_TAXDESC")!=null)
	 	 {
%>
            <td width="35%" align="left"><%=arrTax[1]%></td>
<%	
      	}
	 	 if(request.getParameter("P_REMARKS")!=null)
	 	 {
%>
            <td width="35%" align="left"><%=arrTax[2]%></td>
<%		
      	 }
%>   </tr> 
<%	  
	} 
%> 
       </table>
      </td>
     </tr><!-- Modified By G.Srinivas to resolve the QA-Issue -->
	<tr valign="top" border="0"> 
                <td width="100%"   align="right" bgcolor="white">
                <input type="button" value="Continue" name="close" onClick="javascript:history.go(-1);" class='input'>
				</td>
    </tr>
    </table>
   </form>
</body>
</html>
<%
 } else
  {
   errorMessageObject = new ErrorMessage("No Tax Records Exist ","ETAShowViewAllReport.jsp?viewParameter=taxmaster"); 
	keyValueList.add(new KeyValue("ErrorCode","RNF")); 	
	keyValueList.add(new KeyValue("Operation","")); 	
	errorMessageObject.setKeyValueList(keyValueList);
	request.setAttribute("ErrorMessage",errorMessageObject);
%>
     <jsp:forward page="../ESupplyErrorPage.jsp" />
<% 
   }  
}else if (insetAttribute.equalsIgnoreCase("locationmaster"))
 {	  	
	String[][]   records      = null;
	String       heading      = null;
	int cols = 0;
	int leng = 0;
	String values  ="",locationId="",locationName="",countryId="";	 
    int count=0;
	try
	{
	   if(request.getParameter("P_LOCATIONID")!=null)
	   {
	    locationId="LOCATIONID,";
	    count++;
	   }
	   if(request.getParameter("P_LOCATIONNAME")!=null)
	   {	
	      locationName="LOCATIONNAME,";
		  count++;
	   }
	   if(request.getParameter("P_COUNTRYID")!=null)
	   {
	    	countryId ="COUNTRYID,";
	    	count++;
	   }
	   values=values+locationId+locationName+countryId;
	 }
		catch(Exception e)
		{
			String errorMessage = " Problem in retrieving Location  details" ;
		    session.setAttribute("Operation", "");
			session.setAttribute("ErrorCode","RNF");
			session.setAttribute("ErrorMessage",errorMessage);
        	session.setAttribute("NextNavigation","OperationsIndex.jsp");
%>
    		 <jsp:forward page="../ESupplyErrorPage.jsp" />
<% 
	}
  if(!values.equals("locationmaster"))
  {	
	 records = etransReportsRemote.getLocationMaster(values,count);
	 leng=records.length;
	 if(leng != 0)
     {	
%>	 
 <table width="100%"  border="0" cellspacing="2" cellpadding="4" >
   <tr valign="top" class='formheader'> 
<%
    StringTokenizer st= new StringTokenizer(values,",");
	while(st.hasMoreTokens())
	{
	    cols++;
	    heading = st.nextToken();
	    if(heading.equalsIgnoreCase("LocationId"))
			heading = "Location Id";			
		if(heading.equalsIgnoreCase("LocationName"))
			heading = "Location Name";
		if(heading.equalsIgnoreCase("CountryId"))
			heading = "Country Id";
%>
   <td width ="30%"><div align="left"><%=heading%></div></td>
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
          <td width="30%" align="left"><%=records[i][j]%></td>
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
     </tr><!-- Modified By G.Srinivas to resolve the QA-Issue -->
	<tr valign="top" border="0" class='formdata'> 
               <td width="100%"   align="right" bgcolor="white">
                <input type="button" value="Continue" name="close" onClick="javascript:history.go(-1);" class='input'>
				</td>
    </tr>
 </table>
</form>
</body>
</html>
<%
  }
   else
   {
		String errorMessage = "No data found" ;
		session.setAttribute("Operation", "");
		session.setAttribute("ErrorCode","RNF");
		session.setAttribute("ErrorMessage",errorMessage);
        session.setAttribute("NextNavigation","javascript:window.close()");
%>
		<jsp:forward page="../ESupplyErrorPage.jsp" />
<% 
 }
}	
 }	
} catch(Exception exp)
  {
        //Logger.error(FILE_NAME,"Exception was "+exp,exp);
        logger.error(FILE_NAME+"Exception was "+exp);
		String errorMessage = " Problem in retrieving details" ;
		session.setAttribute("Operation", "");
		session.setAttribute("ErrorCode","RNF");
		session.setAttribute("ErrorMessage",errorMessage);
        session.setAttribute("NextNavigation","OperationsIndex.jsp");
%>
    	 <jsp:forward page="../ESupplyErrorPage.jsp" />
<% 
      
  }
%>
