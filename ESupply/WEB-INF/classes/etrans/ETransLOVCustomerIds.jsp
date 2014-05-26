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
/* Program Name		: ETransLOVCustomerIds.jsp
   Module name		: HO Setup
   Task		        : Adding Customer
   Sub task		: to obtain Customer IDs
   Author Name		: A.Hemanth Kumar
   Date Started		: September 08, 2001
   Date completed	: September 11, 2001
   Date Modified	: December 10,2001 by Rizwan.
			Corrected the problem which was not showing CorporateCustomerId when modified.
   Description      :
    This file is invoked when clicked on the CustomerIds LOV. In this, all the CustomerIds
    are displayed in the List Box. Once Selected any one of the CustomerIds, that CustomerId
    is displayed in the respective Text Field. 
    This file interacts with CustomerRegSessionBean to call the method getCustomerIds( ESupplyGlobalParameters loginbean ) which inturn 
    retrieves the CustomerIds.
*/
%>
<%@ page import = "	javax.naming.InitialContext,java.util.ArrayList,
				 org.apache.log4j.Logger,
					com.qms.setup.ejb.sls.SetUpSession,
                    com.qms.setup.ejb.sls.SetUpSessionHome,
					 com.foursoft.esupply.common.java.LOVListHandler,
					com.foursoft.esupply.common.java.FoursoftConfig"%>
<%@ taglib  uri="../WEB-INF/lib/PageIterator.jar"  prefix="pageIterator"  %>
<jsp:useBean id = "loginbean" class = "com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope = "session"/>
<%!
  private static Logger logger = null;
	private static final String FILE_NAME	=	"ETransLOVCustomerIds.jsp ";
%>

<%	
  logger  = Logger.getLogger(FILE_NAME);	
	ArrayList requiredAttributes = null;
	LOVListHandler listHandler   = null;
	//String customerType			=	null;   // String to store customer Type
	String whereClause			=	null;     
	String temp					=	null;

	String	searchString	=	request.getParameter("searchString");
	String	terminalId		=	request.getParameter("terminalId");
	String	registered		=	request.getParameter("registered");
	String	customerType	=	request.getParameter("customerType");
	String	operation		=	request.getParameter("operation");
	String	regLevel		=	request.getParameter("registrationLevel");


	//searchString				=	request.getParameter("searchString");
	
/*	if(searchString == null)
		searchString="";
	else
		searchString = searchString.trim(); */

	if(request.getParameter("CustomerType")!=null)
	{
	  customerType=request.getParameter("CustomerType"); 
	}
	
	terminalId   = request.getParameter("terminalId");

	if(terminalId == null)
	 terminalId   = loginbean.getTerminalId(); 


	if(request.getParameter("whereClause")!=null)
	 {
	   whereClause=request.getParameter("whereClause");
	   //Logger.info(FILE_NAME,"WhereClause..." +whereClause);
	}
	if(customerType!=null && request.getParameter("whereClause")!=null)
	 {
	     temp=whereClause;
//	     whereClause=" CUSTOMERID LIKE '"+searchString+"%' AND REGISTERED='"+customerType+"' AND  "+ temp;
	     //Logger.info(FILE_NAME,"Where Clause is 000000000:" + whereClause);
	 }
	if(customerType!=null && request.getParameter("whereClause")==null)
	 {
	     whereClause=" CUSTOMERID LIKE '"+searchString+"%' AND REGISTERED ='"+customerType+"' AND TERMINALID='"+terminalId+"'";
	     //Logger.info(FILE_NAME,"Where Clause is :" + whereClause);
	 }
	if(customerType==null && request.getParameter("whereClause")==null)
	{
		 whereClause=" CUSTOMERID LIKE '"+searchString+"%' AND CUSTOMERTYPE = 'Customer' AND TERMINALID='"+terminalId+"'";
		//Logger.info(FILE_NAME,"Where Clause is :" + whereClause);
	}	 	  
	if(customerType==null && request.getParameter("whereClause")!=null)
	{
		 whereClause=" A.CUSTOMERID LIKE '"+searchString+"%' "+temp;
		//Logger.info(FILE_NAME,"Where Clause is :" + whereClause);
	}	 	 
	

	java.util.ArrayList		vecCustomerId	=	null;    // Vector to store Customer Ids
	int				len						=	0;    // integer to store sizze of Vector
	InitialContext				initial 			=	null;    // Context variable  
	SetUpSessionHome 	home   		= 	null;    // variable to store Home Object
	SetUpSession     	remote 		= 	null;    // variable to store Remote Object
	
	String str_label="Customer Ids";
	String whereToSet=request.getParameter("whereToSet");
	//System.out.println("whereToSet is +++++++++++++++"+whereToSet);
	if(whereToSet!=null && whereToSet.equals("customerId"))
	{
		str_label="Consignee Id";
	}
	else if(whereToSet!=null && whereToSet.equals("custWHId"))
	{
		str_label="Shipper Id";
	}
	// @@ Added by Murali On 20050429 Regarding SPETI - 6455
	if(request.getParameter("dCusType")!=null && request.getParameter("dCusType").equals("DCustomer"))
	{
		str_label="Customer Id";
	}
	// @@ Added by Murali On 20050429 Regarding SPETI - 6455
	if(request.getParameter("pageNo")!=null)
	{
		try
		{
	  
			listHandler           = (LOVListHandler)session.getAttribute("vecCustomerId");
			requiredAttributes    = listHandler.requiredAttributes; 
		}
		catch (Exception e)
		{
			listHandler = null;
		}
	}

	try
		{
		 if(listHandler == null)
		{
		System.out.println("Handler is not null");
		requiredAttributes  = new ArrayList();
				initial 	= new InitialContext();
				home   		= (SetUpSessionHome)initial.lookup("SetUpSessionBean");
				remote 		= (SetUpSession)home.create();
			//	vecCustomerId	= remote.getCustomerIds(loginbean,whereClause);
			//Logger.info(FILE_NAME,"regLevel :---------------------------------------------->" + regLevel);
			if(regLevel!=null && !(regLevel.equals("")))
			{
					vecCustomerId	= remote.getCustomerIds(searchString,terminalId,customerType,registered,regLevel,operation);
			}
			else
			{	
				if (registered!=null)
					registered=registered;
				else
					registered="R";
				if(customerType != null && customerType.equals("Corporate"))
					terminalId ="";
				
				vecCustomerId	= remote.getCustomerIds(searchString,terminalId,customerType,registered,operation);//ADDED BY RK //remote.getCustomerIds(searchString,terminalId,"Customer",registered);
			}
			

				if(vecCustomerId != null)
					len=vecCustomerId.size();
		if(vecCustomerId!=null)
		{
			listHandler                     = new LOVListHandler(vecCustomerId,Integer.parseInt(loginbean.getUserPreferences().getLovSize()),requiredAttributes);
			 session.setAttribute("whereToSet",whereToSet);
			 session.setAttribute("whereClause",whereClause);
			 session.setAttribute("searchString",searchString);
			 session.setAttribute("vecCustomerId", listHandler);
			listHandler = (LOVListHandler)session.getAttribute("vecCustomerId");
		}
		}
		else
	{
		whereToSet = (String) session.getAttribute("whereToSet");
		whereClause = (String) session.getAttribute("whereClause");
		searchString= (String) session.getAttribute("searchString");
	}
		}
		catch(Exception ex)
		{	
			//Logger.error(FILE_NAME,"Error in ETransLOVCustomerIds.jsp : " + ex);
      logger.error(FILE_NAME+"Error in ETransLOVCustomerIds.jsp : " + ex);
		}
		finally
		{
			initial	=	null;
			home	=	null;
			remote	=	null;
		}	

		String pageNo			= request.getParameter("pageNo");
	if(pageNo == null)
	{
		pageNo = "1";
		listHandler.currentPageIndex = 1;
	}
	else
	{
		listHandler.currentPageIndex = Integer.parseInt(pageNo);
	}

	

	ArrayList	currentPageList	= listHandler.getCurrentPageData();
	String 		fileName	= "ETransLOVCustomerIds.jsp"; 

%>
<html>
<head>
<title>Select </title>
<script>
var isAttributeRemoved  = 'false';

    // This function is used to show all the Customer Ids on clicking of the LOV.	
	function showCustomerIds()
	{
		
<%		for(int i = 0;i<currentPageList.size(); i++)
		{
			
			String customerId	=	currentPageList.get(i).toString();
%>
			val	=	'<%=customerId%>';			
			window.document.form1.ids.options['<%=i%>']	=	new Option(val,val);
<%
		}
		if(currentPageList.size() > 0)
		{
%>
				window.document.form1.ids.options[0].selected = true;	
				window.document.form1.ids.focus();

<%
		}else{
%>
			window.document.form1.B2.focus();
<%
		}	
%>
	}	

	// This function is used to set the Customer Id to the Text Filed after selecting it from the LOV.
	function setCustomerId()
	{
		if(document.form1.ids.selectedIndex == -1)
		{
			alert("Plese select atleast one Customer Id");
		}
		else
		{
			temp1	=	document.form1.ids.options[document.form1.ids.selectedIndex].value
			index1	=	temp1.indexOf('[');
			temp2	=	temp1.substring(0,(index1-1)).toString();
<%

				 // Logger.info(FILE_NAME,"WW  "+whereToSet);
				 if(whereToSet==null)
				 {
				 %>
					 window.opener.document.forms[0].corpCustomerId.value	=	temp2;
				<%
				 }
				 else
				 { %>
					window.opener.document.forms[0].<%=whereToSet%>.value	=	temp2;
				<% } 
			%>   
		}
			resetValues();
	}


function onEnterKey()
{

	if(event.keyCode == 13)
	{
		setCustomerId()
	}
	if(event.keyCode == 27){
		  resetValues();

	}
}
function onEscKey(){
	if(event.keyCode == 27 ){
		  resetValues();

	}
}


var closeWindow = 'true';

function setVar()
{
  closeWindow = 'false';
}

function toKillSession()
{
   if(closeWindow == 'true' && isAttributeRemoved=='false')
   {
      window.location.href="../ESupplyRemoveAttribute.jsp?valueList=vecCustomerId";
   }
}

function resetValues()
{
    isAttributeRemoved  = 'true';
    document.forms[0].action="../ESupplyRemoveAttribute.jsp?valueList=vecCustomerId";
    document.forms[0].submit();   
}
</script>
<link rel="stylesheet" href="../ESFoursoft_css.jsp">

</head>

<body onLoad = "showCustomerIds()" onUnLoad='toKillSession()' onKeyPress='onEscKey()' class='formdata'>
<form name= form1 method="post" action="">
<br>	<b><center><%=str_label %></center></b>
<br>
<%
	if(currentPageList.size() > 0)
	{
%>
		<center>
				<select size=8 name="ids" class="select" onDblClick='setCustomerId()'  onKeyPress='onEnterKey()'>
				</select>
		</center>
		<br>
		<center>
				<input type=button name="addButton" value=" Ok " onClick="setCustomerId()" class="input">
				<input type="button" value="Cancel" name="B2" onClick="resetValues()" class="input">
		</center>
    <TABLE cellSpacing=0 width=95%>
		<tr  class="formdata"> 
		<td width=100% align='center'>Pages : &nbsp;
        
		<pageIterator:PageIterator currentPageNo="<%= listHandler.currentPageIndex %>" noOfPages="<%= listHandler.getNoOfPages() %>" noOfSegments="<%=Integer.parseInt(loginbean.getUserPreferences().getSegmentSize())%>" fileName="<%= fileName %>"/>
		</td>
		</tr>	
		</table>
<%
	}
	else
	{
%>
		<center>
		<textarea rows=6 name="ta" class='select' readOnly cols="30" >No CustomerIds available for this Terminal :<%= terminalId%></textarea>
		</center><br>
		<center><input type="button" value="Close" name="B2" onClick="window.close()" class="input"></center>
<%
	}
%>
</form>

</body>
</html>
