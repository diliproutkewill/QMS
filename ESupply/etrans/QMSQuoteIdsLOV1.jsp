
<!--
% 
% Copyright (c) 1999-2001 by FourSoft, Pvt Ltd. All Rights Reserved.
% This software is the proprietary information of FourSoft, Pvt Ltd.
% Use is subject to license terms.
%
% esupply - v 1.x 
%

	Program Name	:QMSQuoteIdsLOV1.jsp
	Module Name		:ETrans
	Task			:Location
	Sub Task		:QMSQuoteIdsLOV
	Author Name		:
	Date Started	:November 17,2005
	Date Completed	:November 17,2005
	Date Modified	:
	
-->

<%@ page import="java.util.ArrayList,					
				java.util.Locale,
				java.util.Hashtable,
				com.foursoft.esupply.common.util.Logger,
				com.qms.operations.quote.ejb.sls.QMSQuoteSession,
				com.qms.operations.quote.ejb.sls.QMSQuoteSessionHome,
				com.qms.operations.multiquote.ejb.sls.QMSMultiQuoteSession,	 
				com.qms.operations.multiquote.ejb.sls.QMSMultiQuoteSessionHome,	  
				com.foursoft.esupply.common.java.LOVListHandler,
				com.qms.operations.quote.dob.QuoteMasterDOB,
				com.foursoft.esupply.common.java.FoursoftConfig" %>
        
<%@ taglib  uri="/WEB-INF/lib/PageIterator.jar"  prefix="pageIterator"  %>
<%!
	private static final String FILE_NAME	=	"QMSQuoteIdsLOV1.jsp ";
%>
<jsp:useBean id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/>

<%
	ArrayList     requiredAttributes = null;
	LOVListHandler   listHandler     = null;
	ArrayList        locationIds	 = null;	
	int len					         = 0;		
	String           whereClause	 = null;
	String           wheretoset		 = "";
	String           ArrayOrNot		 = null;
	String           fromWhat		 = request.getParameter("fromWhat");
	String           from			 = request.getParameter("from");
	String           searchString    = request.getParameter("searchString");
	ArrayList        quoteList       = new ArrayList();
	String           customerId      = request.getParameter("customerId");
	String           whereToSet      = request.getParameter("whereToSet");
	String           operation       = request.getParameter("Operation");
	String           shipMentMode    = request.getParameter("shipmentMode");
	QuoteMasterDOB	 masterDOB		 = null;
	String			 buyRatesPermission	 = "Y";
	QMSQuoteSessionHome       home	 = null;
	QMSQuoteSession           remote = null;
	QMSMultiQuoteSessionHome       home2	  = null; //Added by Rakesh on 12-01-2010
	QMSMultiQuoteSession           remote2 = null;	  //Added by Rakesh on 12-01-2010
	String            origin         = request.getParameter("originLoc");
	String            destination    = request.getParameter("destLoc");
	String			  quoteType		 = request.getParameter("quoteType");
    String            module	     = null;
    String		      searchString2	 = "";
	String            rows           = null;
	String            listTypes1[]	 = request.getParameterValues("listTypes1");
	Hashtable		  accessList	 = null;

	accessList  =  (Hashtable)session.getAttribute("accessList");

	if(accessList.get("10605")==null)
		buyRatesPermission	= "N";

		if(request.getParameter("pageNo")!=null)
		{
			try
			{
				listHandler           = (LOVListHandler)session.getAttribute("locationIds");
				requiredAttributes    = listHandler.requiredAttributes; 
			}
			catch (Exception e)
			{
				listHandler = null;
			}
		}

  
		if(listHandler == null)
		{
        
         requiredAttributes  = new ArrayList();
		 rows	= request.getParameter("row");

         if(fromWhat == null)
			fromWhat="0";
		 else
			fromWhat = fromWhat;
		 searchString	= request.getParameter("searchString");
		 searchString2	= request.getParameter("searchString2");
		
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
			wheretoset = "QuoteId";	
		
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
			masterDOB				=	  new QuoteMasterDOB();
			
			masterDOB.setCustomerId(customerId);
			masterDOB.setUserId(loginbean.getEmpId());
			masterDOB.setBuyRatesPermission(buyRatesPermission);
			masterDOB.setOperation(operation);
			masterDOB.setOriginLocation(origin);
			masterDOB.setDestLocation(destination);
			if(quoteType!=null && quoteType.trim().length()!=0)
				masterDOB.setShipmentMode(Integer.parseInt(quoteType));
			//Modified by Rakesh on 12-01-2010
			if(request.getParameter("typeQuote")!=null){
			home2                    =     (QMSMultiQuoteSessionHome)loginbean.getEjbHome("QMSMultiQuoteSessionBean");
			remote2                  =     (QMSMultiQuoteSession)home2.create();
			searchString            =      searchString!=null?searchString:"";
			locationIds				=     remote2.getQuoteGroupIds(masterDOB);
			}else{
			home                    =     (QMSQuoteSessionHome)loginbean.getEjbHome("QMSQuoteSessionBean");
			remote                  =     (QMSQuoteSession)home.create();
			searchString            =      searchString!=null?searchString:"";
			
			locationIds				=     remote.getQuoteGroupIds(masterDOB);
			}
			
			
		
			if(locationIds != null)
			{
				len	=	locationIds.size();
				System.out.println("the value of lenth is "+len);
			}
			
	
	if(locationIds!=null)
	{
		listHandler   = new LOVListHandler(locationIds,FoursoftConfig.NOOFRECORDSPERPAGE,requiredAttributes);
		session.setAttribute("locationIds", listHandler);
		listHandler = (LOVListHandler)session.getAttribute("locationIds");
	}
	}
	
	catch(Exception e)
	{
		Logger.error(FILE_NAME,"QMSQuoteIdsLOV1.jsp : " , e.toString());
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
	String 		fileName	= "QMSQuoteIdsLOV1.jsp";  

	 try
    {
        System.out.println("Hai Size is " + currentPageList.size());
        if(requiredAttributes!=null)
        {
            whereClause        = (String)requiredAttributes.get(0);
            wheretoset        = (String)requiredAttributes.get(1);
            ArrayOrNot         = (String)requiredAttributes.get(2);
			rows			   = (String)requiredAttributes.get(3);
            System.out.println("Hai334555 " + wheretoset);
        }
    }
    catch(Exception ex)
    {
      Logger.error(FILE_NAME,"QMSQuoteIdsLOV1.jsp : " +ex);
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

	document.forms[0].action="ETCLOVLocationIds1.jsp?pageNo="+var1;
	document.forms[0].submit();
}

 function selectallValues()
 {
	<% if(locationIds!=null && locationIds.size()>0) { %>
	 obj = document.forms[0].listTypes1;
	for (var i=0; i<obj.options.length; i++) 
		{
			obj.options[i].selected = true;
		}
    <% } %>

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
function  populateLocation1Names()
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
function  populatelocationNames()
{

<%

	for( int i=0;i<currentPageList.size();i++ )
	{// begin of for loop
		String str	=	currentPageList.get(i).toString();
%>
		document.forms[0].locationNames.options[ <%= i %> ] = new Option('<%= str %>','<%=str%>','<%= str %>');
<%
	}//end of for loop
	if(currentPageList.size() > 0)
	{
%>
			document.forms[0].locationNames.options[0].selected = true;	
			document.forms[0].locationNames.focus();

<%
	}else{
%>
		document.forms[0].B2.focus();
<%
		}
%>
}
function setlocationNames()
{
	selectallValues();
	if(document.forms[0].listTypes1.selectedIndex == -1)
	{
		alert("Please select atleast one QuoteId")
	}
	else
	{

		
<%
	if(module == null)
	{
%>
		var len		=<%=fromWhat%>;
		window.opener.showLocationValues(document.forms[0].listTypes1,'<%=wheretoset%>');
		var cond 	='<%= ArrayOrNot%>' ;
		/*var index	=document.forms[0].locationNames.selectedIndex;
		firstTemp	=document.forms[0].locationNames.options[index].value;
		secondTemp	=document.forms[0].locationNames.options[index].value;
		firstIndex	=	firstTemp.indexOf(0);
		lastIndex	=	firstTemp.indexOf('[');	
		firstTemp	=	firstTemp.substring(firstIndex,lastIndex);
		temp		=   firstTemp.toString();
		lastIndex1	=	secondTemp.lastIndexOf('[')+1;
		lastIndex2	=	secondTemp.lastIndexOf(']');	
		temp1		=	secondTemp.substring(lastIndex1,lastIndex2);

		var whereClause = "<%=whereClause%>";
	
	
		if(whereClause=="" && cond=='NoArray')
		{
			  if('<%=from%>'!=null && '<%=from%>'=='GateWay')
			{		   
			   window.opener.document.forms[0].<%=wheretoset%>.value=temp;
			   window.opener.document.forms[0].gatewayId.value=temp+'G';
			   window.opener.document.forms[0].gatewayName.value=temp1;
		       window.opener.document.forms[0].<%=wheretoset%>.focus();
			}
			else
			{
				window.opener.document.forms[0].<%=wheretoset%>.value=temp;
				window.opener.document.forms[0].<%=wheretoset%>.focus();
			}

		}
		else if((parseInt(len) > 0) && (cond == 'YesArray'))
		{
			if(document.forms[0].locationNames.selectedIndex==-1)
			{
				alert("Please select a Id");
			}
			else
			{
				window.opener.document.forms[0].<%=wheretoset%>[len].value=temp//.substring(index+2,index2);	
				window.opener.document.forms[0].<%=wheretoset%>[len].focus();
				resetValues();
			}
		}
	//	else
	//		window.opener.document.forms[0].withLocationId.value=temp;*/
			resetValues();
	}
<%
  }
  else
  {
 %>
 		var len		=	<%= fromWhat %>;
		var cond 	=	'<%= ArrayOrNot%>' ;
		var index	=	document.forms[0].locationNames.selectedIndex;
		firstTemp	=	document.forms[0].locationNames.options[index].value;
		firstIndex	=	firstTemp.indexOf(0);
		lastIndex	=	firstTemp.indexOf('[');	
		firstTemp	=	firstTemp.substring(firstIndex,lastIndex);
		window.opener.document.forms[0].locationId.value=firstTemp;
		window.opener.document.forms[0].locationId.focus();
		window.opener.document.forms[0].LOVlocationId.focus();
		window.opener.document.forms[0].locationId.focus();
   }	
<%
  }
%>  			
}


function onEnterKey()
{

	if(event.keyCode == 13)
	{
			if(document.forms[0].locationNames.selectedIndex == -1)
			{
				alert("Please select atleast one QuoteId")
			}
			else
			{

<%
				if(module == null)
				{
%>
							var len		=<%= fromWhat %>;
							var cond 	='<%= ArrayOrNot%>' ;
							var index	=document.forms[0].locationNames.selectedIndex;
							firstTemp	=document.forms[0].locationNames.options[index].value;
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
									alert("Please select a Id");
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
						var index	=document.forms[0].locationNames.selectedIndex;
						firstTemp	=document.forms[0].locationNames.options[index].value;
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
  document.forms[0].action="ETCLOCLocationIds.jsp?pageNo="+<%=pageNo%>;
	document.forms[0].submit();
}

function toKillSession()
{
   if(closeWindow == 'true' && isAttributeRemoved=='false')
   {
      window.location.href="../ESupplyRemoveAttribute.jsp?valueList=locationIds";
   }
}

function resetValues()
{
    isAttributeRemoved  = 'true';
    document.forms[0].action="../ESupplyRemoveAttribute.jsp?valueList=locationIds";
    document.forms[0].submit();   
}

</script>
</head>
<body class='formdata' onUnLoad='toKillSession()' onbeforeunload='selectallValues();' onLoad="populateLocation1Names();populatelocationNames()" onKeyPress='onEscKey()'>
<form method="post" action="">
<input type=hidden name='pageNo' value='<%=pageNo%>'>

<center>
<b><font face="Verdana">QuoteIds</font></b>
</center>
<br>
<%
	if(currentPageList.size() > 0)
	{// begin of if
%>

<center>
<select size="10" name="locationNames" MULTIPLE onKeyPress='onEnterKey()' class="select">
</select>
<input type="button" value='>>' name="right" class='input' onClick='moveDestSelectedRecords(document.forms[0].locationNames,document.forms[0].listTypes1)'>
<input type="button" value='<<' name="left" class='input' onClick='moveDestSelectedRecords(document.forms[0].listTypes1,document.forms[0].locationNames)'>
<select size="10" name="listTypes1" MULTIPLE onDblClick='setlocationNames()' onKeyPress='onEnterKey()' class="select">
</select>
</center>
<br>
<center>
<input type="button" value=" Ok " name="OK" onClick="setlocationNames()" class="input">
<input type="button" value="Cancel" name="B2" onClick="resetValues()" class="input">
<TABLE cellSpacing=0 width=95%>
		<tr  class="formdata"> 
		<td width=100% align='center'>Pages : &nbsp;
        
		<pageIterator:PageIterator currentPageNo="<%= listHandler.currentPageIndex %>" noOfPages="<%= listHandler.getNoOfPages() %>" noOfSegments="<%=FoursoftConfig.NOOFSEGMENTS%>" fileName="<%= fileName %>"/>
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
<textarea cols=30 class='select' rows=6 readOnly >No QuoteIds available.
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
