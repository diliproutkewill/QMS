
<!--
% 
% Copyright (c) 1999-2001 by FourSoft, Pvt Ltd. All Rights Reserved.
% This software is the proprietary information of FourSoft, Pvt Ltd.
% Use is subject to license terms.
%
% esupply - v 1.x 
%

	Program Name	:ETCLOVMultipleSlaesPerson.jsp
	Module Name		:ETrans
	Task			:ETCLOVMultipleSlaesPerson
	Sub Task		:ETCLOVMultipleSlaesPerson
	Author Name		:
	Date Started	:
	Date Completed	:
	Date Modified	:
	Description		:This file is invoked when clicked on the sales person LOV in the LocationEnterId Screen 

-->

<%@ page import="javax.naming.InitialContext,org.apache.log4j.Logger,
				java.util.ArrayList,
				com.qms.setup.ejb.sls.QMSSetUpSession,
				com.qms.setup.ejb.sls.QMSSetUpSessionHome,
				com.foursoft.etrans.setup.country.bean.CountryMaster,
				com.foursoft.etrans.common.util.java.OperationsImpl,
				com.foursoft.esupply.common.java.LOVListHandler,
				com.foursoft.esupply.common.java.FoursoftConfig" %>
        
<%@ taglib  uri="/WEB-INF/lib/PageIterator.jar"  prefix="pageIterator"  %>
<%!
  private static Logger logger = null;
	private static final String FILE_NAME	=	"ETCLOVMultipleSalesPerson.jsp";
%>
<jsp:useBean id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/><!-- added by rk -->

<%
	logger  = Logger.getLogger(FILE_NAME);	
	ArrayList requiredAttributes = null;
	LOVListHandler listHandler   = null;
	ArrayList customerIds	=null;	// ArrayList to store locationIds
	int len					=0;		//len to store length of locationIds
	String whereClause		=null;
	String wheretoset		="";
	String ArrayOrNot		= null;
	String fromWhat			= request.getParameter("fromWhat");
	String from				= request.getParameter("from");
	String operation        = request.getParameter("Operation");//added by rk
	String shipMentMode     = request.getParameter("shipmentMode");//added by rk

	String salesPersonCode  = request.getParameter("salesPersonCode");

	String terminalId		= request.getParameter("terminalId");
	
	QMSSetUpSession          remote				=     null;
	QMSSetUpSessionHome      home					=     null;
	
	//String from				=null;
	String module			= null;
     String		searchString	="";	
	 String		searchString2	="";
	 String rows=null;
	String  listTypes1[]	= request.getParameterValues("listTypes1");
	//System.out.println("listTypes1[]:"+listTypes1);

	if(request.getParameter("pageNo")!=null)
	{
		try
		{
			listHandler           = (LOVListHandler)session.getAttribute("customerIds");
			requiredAttributes    = listHandler.requiredAttributes; 
		}
		catch (Exception e)
		{
			listHandler = null;
		}
	}

  
    if(listHandler == null)
    {
        System.out.println("Handler is not null");
        requiredAttributes  = new ArrayList();
		rows	= request.getParameter("row");

        if(fromWhat == null)
			fromWhat="0";
		else
			fromWhat = fromWhat;
		searchString	= request.getParameter("searchString");
		searchString2	= request.getParameter("searchString2");
		//System.out.println("searchString2:"+searchString2);
		if(searchString == null)
			searchString="";
		else
			searchString = searchString.trim();

		if(searchString2==null)
			searchString2="";
		else
			searchString2=searchString2.trim();

		if(request.getParameter("row")!=null)
		rows=request.getParameter("row");
	else
		rows = "";

		module = request.getParameter("Module");
		
		if(request.getParameter("wheretoset")!=null)
			wheretoset=request.getParameter("wheretoset");	
		else
			wheretoset = "locationId";	
		
		if(request.getParameter("ArrayOrNot")!=null)
			ArrayOrNot=request.getParameter("ArrayOrNot");
		else
			ArrayOrNot="NoArray";
		
		if(request.getParameter("whereClause")!=null)
			whereClause=request.getParameter("whereClause");
		else
			whereClause="";

		String new_whereClause="";

		requiredAttributes.add(whereClause);
		requiredAttributes.add(wheretoset);
		requiredAttributes.add(ArrayOrNot);	
		requiredAttributes.add(rows);

		
		try
			{
			
			InitialContext initial	=	new InitialContext(); 
			home                    =     (QMSSetUpSessionHome)initial.lookup("QMSSetUpSessionBean");
			remote                  =     (QMSSetUpSession)home.create();
		   // customerIds				=   remote.getEmpIds(salesPersonCode,terminalId,loginbean.getEmpId());
		    customerIds				=   
			//@@ modified by subrahmanyam for the pbn id:220125 on 07-oct-10	
			//remote.getEmpIds(salesPersonCode,terminalId,loginbean.getAccessType(),loginbean.getEmpId());//@@Modified for the issue
			remote.getEmpIds(salesPersonCode,terminalId,loginbean.getAccessType(),loginbean.getEmpId(),request.getParameter("fromWhere"));
			
			
			if(customerIds != null)
			{
				len	=	customerIds.size();
				
			}
			
	
	if(customerIds!=null)
	{
	
		listHandler   = new LOVListHandler(customerIds,Integer.parseInt(loginbean.getUserPreferences().getLovSize()),requiredAttributes);
		session.setAttribute("customerIds", listHandler);
		listHandler = (LOVListHandler)session.getAttribute("customerIds");
	}
	}
	
	catch(Exception e)
	{
		//Logger.error(FILE_NAME,"ETCLOVMultipleSalesPerson.jsp : " , e.toString());
    logger.error(FILE_NAME+"ETCLOVMultipleSalesPerson.jsp : " + e.toString());
	}
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
	String 		fileName	= "ETCLOVMultipleSalesPerson.jsp";  

	 try
    {
       // System.out.println("Hai Size is " + currentPageList.size());
        if(requiredAttributes!=null)
        {
            whereClause        = (String)requiredAttributes.get(0);
            wheretoset        = (String)requiredAttributes.get(1);
            ArrayOrNot         = (String)requiredAttributes.get(2);
			rows			   = (String)requiredAttributes.get(3);
           // System.out.println("Hai334555 " + wheretoset);
        }
    }
    catch(Exception ex)
    {
      //Logger.error(FILE_NAMsE,"ETCLOVMultipleSalesPerson.jsp : " +ex);
      logger.error(FILE_NAME+"ETCLOVMultipleSalesPerson.jsp : " +ex);
    }

%>
<html>
<head>
<title>Select </title>
<link rel="stylesheet" href="../ESFoursoft_css.jsp">
<%@ include file="/ESEventHandler.jsp" %>
<script>
var isAttributeRemoved  = 'false';

function submitForm(var1)
{
	selectallValues()
	setVar();

	document.forms[0].action="ETCLOVMultipleSalesPerson.jsp?pageNo="+var1;
	document.forms[0].submit();
}

 function selectallValues()
 {
	
	 obj = document.forms[0].listTypes1;
	 if(obj!=null)
	 {
		
		for (var i=0; i<obj.options.length; i++) 
		{
			obj.options[i].selected = true;
		}
	 }

 }
function hasOptions(obj) {
	if (obj!=null && obj.options!=null) { return true; }
	return false;
	}

function moveDestSelectedRecords(objSourceElement, objTargetElement)    {    
	var aryTempSourceOptions = new Array();        var x = 0;                //looping through source element to find selected options        
			for (var i = 0; i < objSourceElement.length; i++) {   
					if (objSourceElement.options[i].selected) {               
							//need to move this option to target element                
							var intTargetLen = objTargetElement.length++; 
							objTargetElement.options[intTargetLen].text = objSourceElement.options[i].text;                
							objTargetElement.options[intTargetLen].value = objSourceElement.options[i].value;           
					}
					else
					{               
						//storing options that stay to recreate select element          
						var objTempValues = new Object(); 
						objTempValues.text = objSourceElement.options[i].text;
						objTempValues.value = objSourceElement.options[i].value;  
						aryTempSourceOptions[x] = objTempValues;  
						x++;    
					}
			}
			//resetting length of source 

			objSourceElement.length = aryTempSourceOptions.length; 
			//looping through temp array to recreate source select element 
			for (var i = 0; i < aryTempSourceOptions.length; i++) {      
				objSourceElement.options[i].text = aryTempSourceOptions[i].text;  
				objSourceElement.options[i].value = aryTempSourceOptions[i].value; 
				objSourceElement.options[i].selected = false;  
			}
}
function  populateCustomer1Ids()
{
<%
	if(listTypes1!=null)
	{
	for(int i=0;i<listTypes1.length;i++ )
	{// for loop begin
		

%>
		document.forms[0].listTypes1.options[ <%= i %> ] = new Option('<%= listTypes1[i] %>','<%= listTypes1[i] %>');
<%
	}// for loop end
	}
	
%>
}
function populateCustomerIds()
{

 <%
boolean flag = true;
    int     count = 0;
	for( int i=0;i<currentPageList.size();i++ )
	{// begin of for loop
		String str	=	currentPageList.get(i).toString();
		if(listTypes1!=null)
		for(int j=0;j<listTypes1.length;j++ )
	    {
          if(str.equalsIgnoreCase(listTypes1[j])){
			  flag=false;
			  break;
		  }
		}
		if(flag){
%>
		document.forms[0].customerIds.options[ <%= count %> ] = new Option('<%= str %>','<%=str%>','<%= str %>');
<%     count++;}flag=true;
	}//end of for loop
	if(count>0 && currentPageList.size() > 0)
	{
%>
			document.forms[0].customerIds.options[0].selected = true;	
			document.forms[0].customerIds.focus();

<%
	}else{
%>
		document.forms[0].B2.focus();
<%
		}
%>
}
function setCustomerIds()
{
	selectallValues();
	if(document.forms[0].listTypes1.selectedIndex == -1)
	{
		alert("Please select atleast one  Sales Person Id")
	}
	else
	{

		
<%
	if(module == null)
	{
%>
		var len		=<%=fromWhat%>;
		window.opener.showSalesPersonIds(document.forms[0].listTypes1,'<%=wheretoset%>');
		var cond 	='<%= ArrayOrNot%>' ;
		resetValues();
	}
<%
  }
  else
  {
 %>
 		var len		=	<%= fromWhat %>;
		var cond 	=	'<%= ArrayOrNot%>' ;
		var index	=	document.forms[0].customerIds.selectedIndex;
		firstTemp	=	document.forms[0].customerIds.options[index].value;
		firstIndex	=	firstTemp.indexOf(0);
		lastIndex	=	firstTemp.indexOf('[');	
		firstTemp	=	firstTemp.substring(firstIndex,lastIndex);
		window.opener.document.forms[0].locationId.value=firstTemp;
		window.opener.document.forms[0].locationId.focus();
		window.opener.document.forms[0].LOVlocationId.focus();
		window.opener.document.forms[0].locationId.focus();
   	
<%
  }
%>  			
}


function onEnterKey()
{

	if(event.keyCode == 13)
	{
			if(document.forms[0].customerIds.selectedIndex == -1)
			{
				alert("Please select atleast one Sales Person Id")
			}
			else
			{

<%
				if(module == null)
				{
%>
							var len		=<%= fromWhat %>;
							var cond 	='<%= ArrayOrNot%>' ;
							var index	=document.forms[0].customerIds.selectedIndex;
							firstTemp	=document.forms[0].customerIds.options[index].value;
							firstIndex	=	firstTemp.indexOf(0);
							lastIndex	=	firstTemp.indexOf('[');	
							firstTemp	=	firstTemp.substring(firstIndex,lastIndex);
							temp		=	firstTemp.toString();
							var whereClause = "<%=whereClause%>";
	
	
							if(whereClause=="" && cond=='NoArray')
							{
									window.opener.document.forms[0].<%=wheretoset%>.value=temp;
								   window.opener.document.forms[0].<%=wheretoset%>.focus();
							}
							else if((parseInt(len) > 0) && (cond == 'YesArray'))
							{
								if(document.forms[0].locationNames.selectedIndex==-1)
								{
									alert("Please select a Sales Person Id");
								}
								else
								{
									
									window.opener.document.forms[0].<%=wheretoset%>[len].value=temp//.substring(index+1,index1);	
									window.opener.document.forms[0].<%=wheretoset%>[len].focus();
									resetValues();
								}
							}
	
							resetValues();
					}//end of else 
<%
				}
				else
				{
%>
						var len		=<%= fromWhat %>;
						var cond 	='<%= ArrayOrNot%>' ;
						var index	=document.forms[0].customerIds.selectedIndex;
						firstTemp	=document.forms[0].customerIds.options[index].value;
						firstIndex	=	firstTemp.indexOf(0);
						lastIndex	=	firstTemp.indexOf('[');	
						firstTemp	=	firstTemp.substring(firstIndex,lastIndex);
						temp		=	firstTemp.toString(); 	
						window.opener.document.forms[0].locationId.value=temp;
						window.opener.document.forms[0].locationId.focus();
						window.opener.document.forms[0].LOVlocationId.focus();
						window.opener.document.forms[0].locationId.focus();
			}	
<%
				}//end of else
%>  			
		}
		if(event.keyCode == 27){
			resetValues();
		}
}

function onEscKey(){
	if(event.keyCode == 27){
		resetValues();
	}
}

var closeWindow = 'true';

function setVar()
{
  closeWindow = 'false';
  document.forms[0].action="ETCLOVMultipleSalesPerson.jsp?pageNo="+<%=pageNo%>;
	document.forms[0].submit();
}
function toKillSession()
{
   if(closeWindow == 'true' && isAttributeRemoved=='false')
   {
      window.location.href="../ESupplyRemoveAttribute.jsp?valueList=customerIds";
   }
}

function resetValues()
{
    isAttributeRemoved  = 'true';
    document.forms[0].action="../ESupplyRemoveAttribute.jsp?valueList=customerIds";
    document.forms[0].submit();   
}

</script>
</head>
<body class='formdata' onUnLoad='toKillSession()' onbeforeunload='selectallValues();' onLoad="populateCustomer1Ids();populateCustomerIds()" onKeyPress='onEscKey()'>
<form method="post" action="">
<input type=hidden name='pageNo' value='<%=pageNo%>'>

<center>
<b><font face="Verdana">Sales Person Ids</font></b>
</center>
<br>
<%
	if(currentPageList.size() > 0)
	{// begin of if
%>

<center>
<TABLE cellSpacing=0 width="100%" align='center'>
  <tr  class="formdata"> 
	<td width="49%" align='center'>
		<select size="10" name="customerIds" MULTIPLE onKeyPress='onEnterKey()' class="select"  style="width:250px;margin:0px 0 5px 0;">
		</select></td>
   <td width="2%" align='center' valign="middle">
		<input type="button" value='>>' name="right" class='input' onClick='moveDestSelectedRecords(document.forms[0].customerIds,document.forms[0].listTypes1)'>
		<input type="button" value='<<' name="left" class='input' onClick='moveDestSelectedRecords(document.forms[0].listTypes1,document.forms[0].customerIds)'></td>
   <td width="49%" align='center' valign="middle">
		<select size="10" name="listTypes1" MULTIPLE onDblClick='setCustomerIds()' onKeyPress='onEnterKey()' class="select"  style="width:250px;margin:0px 0 5px 0;">
		</select>
</td>
 </tr>
</table>
</center>
<br>
<center>
<input type="button" value=" Ok " name="OK" onClick="setCustomerIds()" class="input">
<input type="button" value="Cancel" name="B2" onClick="resetValues()" class="input">
<TABLE cellSpacing=0 width=95%>
		<tr  class="formdata"> 
		<td width=100% align='center'>Pages : &nbsp;
        
		<pageIterator:PageIterator currentPageNo="<%= listHandler.currentPageIndex %>" noOfPages="<%= listHandler.getNoOfPages() %>" noOfSegments="<%=Integer.parseInt(loginbean.getUserPreferences().getSegmentSize())%>" fileName="<%= fileName %>"/>
		</td>
		</tr>	
		</table>
<%
	}// end if
	else
	{// begin of else
%>
<br><br>
<center>
<textarea cols=30 class='select' rows=6 readOnly >No Sales Person available.
</textarea>
<br><br>
<input type="button" value="Close" name="B2" onClick="window.close()" class="input">
<%
	}// end of else
%>
</center>
</form>
</body>
</html>
