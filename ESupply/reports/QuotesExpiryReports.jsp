
<%--

Copyright (c) 1999-2001 by FourSoft, Pvt Ltd. All Rights Reserved.
This software is the proprietary information of FourSoft, Pvt Ltd.
Use is subject to license terms.
esupply - v 1.x.
Program	Name	: QuotesExpiryReports.jsp
Product Name	: QMS
Module Name		: QuotesExpiryReports
Date started	: 16 September 2005 
Date Completed	: 
Date modified	:  
Author    		: K.NareshKumarReddy

--%>
<%@page import ="javax.naming.InitialContext,
				 java.util.ArrayList,
				 java.util.HashMap,
				 com.qms.reports.java.ReportsEnterIdDOB,
				 com.qms.reports.java.ReportDetailsDOB,
				 org.apache.log4j.Logger,
				 com.foursoft.esupply.common.java.ErrorMessage,
				 com.foursoft.esupply.common.java.KeyValue,
				 com.qms.reports.ejb.sls.ReportsSession,
         com.qms.reports.ejb.sls.ReportsSessionBeanHome,
		 com.foursoft.esupply.common.util.ESupplyDateUtility "%>

<jsp:useBean id = "loginbean" class = "com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope = "session"/>

<%!
  private static Logger logger = null;
	private static final String FILE_NAME="QuotesExpiryReports.jsp";
%>

<%
    logger  = Logger.getLogger(FILE_NAME);	
    ESupplyDateUtility  dateutil    = new ESupplyDateUtility();
		String		format					=	loginbean.getUserPreferences().getDateFormat();
		int			noOfSegments			=   Integer.parseInt(loginbean.getUserPreferences().getSegmentSize());
		String		datFormat=dateutil.getCurrentDateString(loginbean.getUserPreferences().getDateFormat());
		try
		{
			dateutil.setPatternWithTime(format);
		}
		catch(Exception exp)
		{
			//Logger.error(FILE_NAME," Error in JSP UserPreferences DateFormat---> "+exp.toString());
      logger.error(FILE_NAME+" Error in JSP UserPreferences DateFormat---> "+exp.toString());
		}

		 ErrorMessage errorMessageObject = null;
		 ArrayList	  keyValueList	 	 =   new ArrayList();
	     HashMap	  mapValue			 =	null;
		 String		  operation		     = request.getParameter("Operation");
	
		 ArrayList	  pageIterList	     = null;
 		 ArrayList    dataList		     = null;
		 ArrayList    mainDataList	     = null;
		 ReportsEnterIdDOB reportenterDob= null;
		 String	keyVlue	                 = null;
		 String Validdate				 = null;
		  String dd ="";
		 String date="";
		 String datedate="";
		 String datetime="";
		 int noofRecords =0;
		 int noofPages	 =0;
		 String             sortBy                  =   request.getParameter("SortBy");
		 String              sortOrder               =   request.getParameter("SortOrder");
		 String              pageNo                  =   request.getParameter("PageNo");
	     String              repFormat               =   request.getParameter("format");
		 String				 imagePath     = "";
		 String				 fromSummary             =  request.getParameter("fromSummary");//added by subrahmanyam  146463	
		 String				 empty			="";//Added by Rakesh on 25-02-2011 for Issue:236363 
		 if("ASC".equalsIgnoreCase(sortOrder))
		   imagePath = request.getContextPath()+"/images/asc.gif";
		 else
		   imagePath = request.getContextPath()+"/images/desc.gif";
       
     mapValue					=	(HashMap)session.getAttribute("HashList");
		 if(mapValue==null)
		   mapValue	=	new HashMap();
		 try
		 {
		 pageIterList			= new ArrayList();
		 dataList				= new ArrayList(); 
		 mainDataList			= new ArrayList(); 
		 reportenterDob			= new ReportsEnterIdDOB();
		 mainDataList			= (ArrayList)request.getAttribute("mainDataList");
		 System.out.println("mainDataList"+mainDataList);
         reportenterDob			= (ReportsEnterIdDOB)session.getAttribute("reportenterDob");
		 
		 if(mainDataList!=null && mainDataList.size()>0)
			{
				pageIterList       = 	(ArrayList)mainDataList.get(0);
				dataList		   =	(ArrayList)mainDataList.get(1);

				Integer  no_ofRecords   =   (Integer)pageIterList.get(0);
				Integer  no_ofPages		=	(Integer)pageIterList.get(1);

				noofRecords				=	no_ofRecords.intValue();
				noofPages				=	no_ofPages.intValue();

			session.setAttribute("reportenterDob",reportenterDob);	
            session.setAttribute("dataList",dataList);
			}
		  if(dataList!=null && dataList.size()>0)
			 {
			
%>



<html>
  <head>
	<title>Quote Expiry - Report</title>
	<link rel="stylesheet" href="ESFoursoft_css.jsp">
  <STYLE type="text/css">
  A:link { COLOR: blue; FONT-WEIGHT: none; TEXT-DECORATION: none }
  A:hover { COLOR: blue; FONT-WEIGHT: none; TEXT-DECORATION: underline }
  A:visited { color: blue; font-weight: none; text-decoration: none }
  </STYLE>
	<jsp:include page="../etrans/ETDateValidation.jsp" >
	<jsp:param name="format" value='<%=loginbean.getUserPreferences().getDateFormat()%>' />
	</jsp:include>

  </head>
<script src="<%=request.getContextPath()%>/reports/TableSorting.js"></script>
<script>
function enableDisablevalues(obj,elementname,index)
				 {

                    if(elementname == 'mfValues'){
	                              if(obj.checked)
                     document.getElementsByName("inActiveValues")[index].checked=false;
					}
					else if(elementname == 'inActiveValues'){

					  if( document.getElementsByName("update_flag")[index+1].value =="0")
						{
						  if(obj.checked)
                     document.getElementsByName("mfValues")[index].checked=false;
					}else{
					
						  if(obj.checked)
								   if(confirm("Do You want to inactive the quote in Update Report"))
                     document.getElementsByName("mfValues")[index].checked=false;
							   else
								   obj.checked = false;
					}

					}
				 }




function functionCall(pageNo,operation)
{
document.forms[0].action='QMSReportController?Operation=expiryquotes&pageNo='+pageNo+'&subOperation=quoteratespagging';
document.forms[0].submit();
}
function continueto()
{
document.forms[0].action='QMSReportController?Operation=quotesExpiryReport';
document.forms[0].submit();
}
function forwardQuote(quoteId,index)
{
	//Added by Anil.k
	var len = document.forms[0].isMultiQuote.length;
	if(len>1)
		var isMultiQuote = document.forms[0].isMultiQuote[index].value;
	else
		var isMultiQuote = document.forms[0].isMultiQuote.value;	
	if(isMultiQuote == "Y")
	{
		document.forms[0].action='QMSMultiQuoteController?Operation=View&subOperation=Report&QuoteId='+quoteId+'&fromWhere=Pending'+'&originLoc='+""+'&destLoc='+"";
		 document.forms[0].submit();
	}
	else//Ended by Anil.k
	{   
   Features = 'directories=no,location=no,menubar=no,status=yes,titlebar=no,scrollbars=yes,resizable=yes';
	 Url='QMSQuoteController?Operation=View&subOperation=Report&QuoteId='+quoteId+'&fromWhere=Pending';
	 Win=open(Url,'Doc',Features); 
	 Win.resizeTo(screen.availWidth,screen.availHeight) 
	 Win.focus(); 
	 Win.moveTo(0,0);
	}
   
}
function mandatory()
{
	var minRates	=document.getElementsByName("mfValues");
	var checkVal	=document.getElementsByName("checkValue");
	var minRates1	=document.getElementsByName("inActiveValues");//Added by Anil.k for 28Feb2011
	var checkVal1	=document.getElementsByName("inActiveCheckValue");//Added by Anil.k for 28Feb2011

	for(m=0;m<minRates.length;m++)
	{
		if(minRates[m].checked==false)
			flag=false;
		else
		{
			//for(i=0;i<checkVal.length;i++)
			for(i=0;i<minRates.length;i++)
			{
				if(minRates[i].checked==true)
				{
					//var values	=	checkVal[i].value;
					var values	=	minRates[i].value;
					if(!chkFutureDate(document.getElementsByName(values)[0],"gt"))
					{
						alert("Valid Upto Date should be greater than or equal to current date")
						document.getElementsByName(values)[0].focus();
						return false;
					}
				}
			}
			flag=true;
			return true;
		}
	}
	for(m=0;m<minRates1.length;m++)
	{
		if(minRates1[m].checked==false)
			flag1=false;
		else
		{
			//for(i=0;i<checkVal.length;i++)
			for(i=0;i<minRates1.length;i++)
			{
				if(minRates1[i].checked==true)
				{
					//var values	=	checkVal[i].value;
					var values	=	minRates1[i].value;					
				}
			}
			flag1=true;
			return true;
		}
	}
	if(!flag && !flag1)
	{
		alert("Please select atleast on check box.");
		return false;
	}
}
function fetchSortedPageData(lable,pageNo,clickFrom)
{
   var sortBy     = lable;
   var sortOrder  = "";   
   var Operation  = document.forms[0].Operation.value;
   var format     = document.forms[0].format.value;    
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
	document.forms[0].action = "QMSReportController?Operation="+Operation+"&subOperation=pageBrowse&SortBy="+sortBy+"&SortOrder="+sortOrder+"&PageNo="+pageNo+"&format="+format;
	//alert(document.forms[0].action);
    document.forms[0].submit();
}
//Added by Anil.k on 28Feb2011
function checkAll()
{
	var len = document.forms[0].mfValues.length;
	if(document.forms[0].confirm.checked)
	{
		for(var i=0;i<len;i++)
		{
			document.forms[0].mfValues[i].checked=true;
			//@@Added by kiran.v on 22/11/2011 
			document.forms[0].inActiveValues[i].checked=false;
		}
		//@@Added by kiran.v on 22/11/2011 
		document.forms[0].inActive.checked=false;
	}
	else
	{
		for(var i=0;i<len;i++)
		{
			document.forms[0].mfValues[i].checked=false;
		}
	}
}
function inActiveAll()
{
	var len = document.forms[0].inActiveValues.length;
	if(document.forms[0].inActive.checked)
	{
		for(var i=0;i<len;i++)
		{
			document.forms[0].inActiveValues[i].checked=true;
			//@@Added by kiran.v on 22/11/2011 
			document.forms[0].mfValues[i].checked=false;
		}
		//@@Added by kiran.v on 22/11/2011 
		document.forms[0].confirm.checked=false;
	}
	else
	{
		for(var i=0;i<len;i++)
		{
			document.forms[0].inActiveValues[i].checked=false;
		}
	}
}
//Ended by Anil.k on 28Feb2011
</script>

  <body><!-- initTable("report0") -->
	<form method='POST' action='QMSReportController?Operation=expiryquotes&subOperation=expiryQuotesUpdate' onSubmit='return mandatory()'>
	  <table width="100%" border="0" cellspacing="0" cellpadding="0">
	    <tr valign="top" bgcolor="#FFFFFF">
		  <td>
			<table width="100%" cellpadding="4" cellspacing="1">
			  <tr valign="top" class='formlabel'>
			    <td colspan="3"><table width="100%" border="0" ><tr class='formlabel'>
			      <td>Quote Expiry  Report </td>
			      <td align=right>QS1012541</td></tr></table></td>
			  </tr>
		    </table>
			  
        <table width="100%"  cellspacing="1" cellpadding="4">
          <tr valign="top" class='formdata'>
			    <td colspan="6">&nbsp;
				</td>
				
		  </tr>
			  <tr valign="top" class='formdata'>
			    
            <td width="174" >Terminal Id:<br>
              <%=loginbean.getTerminalId()%>
				</td>
				
            <td width="229">User:<br>
             <%=loginbean.getUserId()%>
				</td>
				
            <td width="209"> Date:<br>
             <%=datFormat%>
			  </td>
			<% for(int i=0;i<dataList.size();i++)
				{
					ReportDetailsDOB detailsDob=(ReportDetailsDOB)dataList.get(i);
			%>
			<td>Shipment Mode: <%=detailsDob.getShipmentMode().equals("1")?"Air":(detailsDob.getShipmentMode().equals("2")?"Sea":"Truck")%>
			<% break; } %>
			</td> 

			<td>With Expiry Date:<%=reportenterDob.getExpiryActiveIndicator().equals("Y")?"Yes":"No"%></td>
			
		  </tr>
			</table>
				<table width="100%"  cellspacing="1" cellpadding="4">
			  <tr valign="top" class='formheader'>
			    <td NOWRAP>Select
				<!-- Added by Anil.k on 28Feb2011 --><br>
				<input type='checkbox' name='confirm' value='' onClick='checkAll()'></td>
				<td NOWRAP>In Active<br>
				<input type='checkbox' name='inActive' value='' onClick='inActiveAll()'></td>
				<!-- Ended by Anil.k on 28Feb2011 -->
			    <td NOWRAP><A href='###' onClick='fetchSortedPageData("Important","<%=pageNo%>","lable")' onmouseover='status="Sort by Important as";return true;' onmouseout="status = '';return true;" title="Sort by Important as">
				Important<%  if(sortBy.equalsIgnoreCase("Important")){%>					
				      <img SRC=<%=imagePath%> border="0">
				 <%}%></A></td>
			    <td><A href='###' onClick='fetchSortedPageData("CustomerId","<%=pageNo%>","lable")' onmouseover='status = "Sort by Customer Id as";return true;' onmouseout="status = '';return true;" title="Sort by Customer Id as">
				Customer Id<%  if(sortBy.equalsIgnoreCase("CustomerId")){%>					
				      <img SRC=<%=imagePath%> border="0">
				 <%}%></A></td>
				 <!--@@Modified by Kameswari for the WPBN issue-38175-->
				 <!--@@Added by kameswari for the WPBN issue-30313-->
				 <td NOWRAP><A href='###' onClick='fetchSortedPageData("CustomerName","<%=pageNo%>","lable")' onmouseover='status = "Sort by Customer Name as";return true;' onmouseout="status = '';return true;" title="Sort by Customer Name as">Customer Name<%  if(sortBy.equalsIgnoreCase("CustomerName")){%>					
				      <img SRC=<%=imagePath%> border="0">
				 <%}%></A></td>
				 <!--@@WPBN 30313-->
			    <td><A href='###' onClick='fetchSortedPageData("QuoteId","<%=pageNo%>","lable")' onmouseover='status = "Sort by Quote Id as";return true;' onmouseout="status = '';return true;" title="Sort by Quote Id as">
				Quote Id<%  if(sortBy.equalsIgnoreCase("QuoteId")){%>					
				      <img SRC=<%=imagePath%> border="0">
				 <%}%></A></td>
			    <td><A href='###' onClick='fetchSortedPageData("ServiceLevel","<%=pageNo%>","lable")' onmouseover='status = "Sort by Service Level as";return true;' onmouseout="status = '';return true;" title="Sort by Service Level as">
				Service Level<%  if(sortBy.equalsIgnoreCase("ServiceLevel")){%>					
				      <img SRC=<%=imagePath%> border="0">
				 <%}%></A></td>
				<td><A href='###' onClick='fetchSortedPageData("FromCountry","<%=pageNo%>","lable")' onmouseover='status = "Sort by From Country as";return true;' onmouseout="status = '';return true;" title="Sort by From Country as">
				From Country<%  if(sortBy.equalsIgnoreCase("FromCountry")){%>					
				      <img SRC=<%=imagePath%> border="0">
				 <%}%></A></td>
			    <td><A href='###' onClick='fetchSortedPageData("FromLocation","<%=pageNo%>","lable")' onmouseover='status = "Sort by From Location as";return true;' onmouseout="status = '';return true;" title="Sort by From Location as">
				From Location<%  if(sortBy.equalsIgnoreCase("FromLocation")){%>					
				      <img SRC=<%=imagePath%> border="0">
				 <%}%></A></td>
			    <td><A href='###' onClick='fetchSortedPageData("ToCountry","<%=pageNo%>","lable")' onmouseover='status = "Sort by To Country as";return true;' onmouseout="status = '';return true;" title="Sort by To Country as">
				To Country<%  if(sortBy.equalsIgnoreCase("ToCountry")){%>					
				      <img SRC=<%=imagePath%> border="0">
				 <%}%></A></td>
			    <td><A href='###' onClick='fetchSortedPageData("ToLocation","<%=pageNo%>","lable")' onmouseover='status = "Sort by To Location as";return true;' onmouseout="status = '';return true;" title="Sort by To Location as">
				To Location<%  if(sortBy.equalsIgnoreCase("ToLocation")){%>					
				      <img SRC=<%=imagePath%> border="0">
				 <%}%></A></td>
				 <!--@@WPBN issue-38175-->
				<td><A href='###' onClick='fetchSortedPageData("ValidUptoDate","<%=pageNo%>","lable")' onmouseover='status = "Sort by Valid Upto Date as";return true;' onmouseout="status = '';return true;" title="Sort by To Location as">
				Valid Upto Date<%  if(sortBy.equalsIgnoreCase("ValidUptoDate")){%>					
				      <img SRC=<%=imagePath%> border="0">
				 <%}%></A></td>
			  </tr>

<%				String tempKeyVlue ="";//Added by Anil.k for multiquote Expiry quotes
				int indexValue	   =0;//Added by Anil.k for multiquote Expiry quotes
				for(int i=0;i<dataList.size();i++)
				{
					ReportDetailsDOB detailsDob=(ReportDetailsDOB)dataList.get(i);
                    keyVlue				=	detailsDob.getQuoteId();
%>

			  <tr valign="top" class='formdata'>
<%			   //Added by Anil.k for multiquote pending quotes
				if(!tempKeyVlue.equalsIgnoreCase(keyVlue))
				{//End
				if(mapValue.containsKey(keyVlue))
				{
%>
					<td  NOWRAP><input type="checkbox" name="mfValues" value="<%=keyVlue%>" id='<%=indexValue%>' checked
					onClick="enableDisablevalues(this,'mfValues',<%=indexValue%>)"></td>
<%
				}
				else
				{
%>
					<td  NOWRAP><input type="checkbox" name="mfValues" value="<%=keyVlue%>" id='<%=indexValue%>' onClick="enableDisablevalues(this,'mfValues',<%=indexValue%>)"></td>
<%
				}
%>
            <input type="hidden" name="checkValue" value="<%=keyVlue%>">
			<!-- Added by Anil.k on 28Feb2011 -->
			<td NOWRAP><input type="checkbox" name="inActiveValues" value="<%=keyVlue%>" id='<%=indexValue%>' onClick="enableDisablevalues(this,'inActiveValues',<%=indexValue%>)"></td>
			<input type="hidden" name="inActiveCheckValue" value="<%=keyVlue%>">
			<!-- Ended by Anil.k on 28Feb2011 -->
			<!-- <%=detailsDob.getImportant()%> -->
			<td  NOWRAP>
			
			  
<%
				if(detailsDob.getImportant()!=null)
				{
					if("I".equalsIgnoreCase(detailsDob.getImportant()))
					{
%>
						<img SRC=<%=request.getContextPath()+"/images/imp_flag.gif"%>>
<%
					}
				}
%>
			</td>	
            <td  NOWRAP> 
			 <%=detailsDob.getCustomerId()%></td>
			 <td> <%=detailsDob.getCustomerName()%></td><!--added by kameswari for the WPBN issue-30313-->
			 <td>
			 <!--Commented and Added by Rakesh on 25-02-2011 for Issue:236363  -->
			<!--  <a href="javascript:forwardQuote('<%=detailsDob.getQuoteId()%>','<%=indexValue%>');" ><%=detailsDob.getQuoteId()==null?"-":detailsDob.getQuoteId()%></a>  -->
			<% if("Y".equalsIgnoreCase(detailsDob.getIsMultiQuote())) {%>
			<a href="QMSMultiQuoteController?Operation=View&subOperation=Report&QuoteId=+<%=(detailsDob.getQuoteId()).trim()%>+&fromWhere=Pending&originLoc=+<%=empty%>+&destLoc=+<%=empty%>" ><%=detailsDob.getQuoteId()==null?"-":detailsDob.getQuoteId()%></a>
			<%}else{%> <!-- Modified by Rakesh for Issue:240224 -->
			<a href="QMSQuoteController?Operation=View&subOperation=Report&QuoteId=+<%=(detailsDob.getQuoteId()).trim()%>+&fromWhere=Pending" > <%=detailsDob.getQuoteId()==null?"-":detailsDob.getQuoteId()%></a>			
			<%}%>
			<!--Ended by Rakesh on 25-02-2011 for Issue:236363  --> 
			 </td>
			  <input type="hidden" name="isMultiQuote" id='<%=indexValue%>' value="<%=detailsDob.getIsMultiQuote()%>">
<%	indexValue++;}	else {	%><!-- //Added by Anil.k on 10-Jan-2011 -->
				<input type="hidden" name="checkValue" value="<%=keyVlue%>">
				<!-- Added by Anil.k on 28Feb2011 -->
				<input type="hidden" name="inActiveCheckValue" value="<%=keyVlue%>">
				<td  NOWRAP></td>
				<!-- Ended by Anil.k on 28Feb2011 -->
				<td  NOWRAP></td>
				<td  NOWRAP></td>
				<td  NOWRAP></td>
				<td  NOWRAP></td>
				<td  NOWRAP></td>
<%	}	%><!-- Ended by Anil.k -->
			  <td NOWRAP><%=detailsDob.getServiceLevel()%></td>	
			  <td NOWRAP><%=detailsDob.getFromCountry()%></td>
			  <td NOWRAP><%=detailsDob.getFromLocation()%></td>
			  <td NOWRAP><%=detailsDob.getToCountry()%></td>
			  <td NOWRAP><%=detailsDob.getToLocation()%></td>
			  <input type="hidden" name ="update_flag"  value='<%=detailsDob.getUpdate_flag()%>'/>
			  <%    if(detailsDob.getValidUpto()!=null)
					{
					  dd=detailsDob.getApprovedRrejectedDtNtime().toString();
					  if(dd.length()>0)
					  {
					  dd=dd.substring(2,dd.length());
					  date=dateutil.getTimestamp(format,dd).toString();
					  datedate=date.substring(2,10);
					  datetime=date.substring(11,dd.length()-3);
					  }
					}
			  if(!tempKeyVlue.equalsIgnoreCase(keyVlue))
			  {%>
			  <td align="right" NOWRAP>
			  <input type="text" class="text" name='<%=keyVlue%>' size='10' maxlength=10  onBlur='dtCheck(this)' value='<%=dd%>' id='<%=i%>'>&nbsp;
			  <input type='button' class='input'  name='b1' value='...' onClick="newWindow('<%=keyVlue%>','0','0','')"></td>
			  </tr>
			  <% } else { %>
			  <td  NOWRAP></td>
			  <td  NOWRAP></td>
			  <% } %>
<%	tempKeyVlue = keyVlue;//Added by Anil.k	
				}
%>

			 </table>
			 <table width="100%" cellpadding="4" cellspacing="0">
			 <tr class="text" vAlign="top">
                <td valign="middle" colspan="10" >
     <%
					int  currentPageNo	=	0;
					
					currentPageNo =reportenterDob.getPageNo();

					if(currentPageNo != 1)
					{
%>
						  <a href="#" onClick='fetchSortedPageData("<%=sortBy%>","<%=currentPageNo-1%>","pageNo")'><img src="images/Toolbar_backward.gif"  border="0"></a>
 <%					}        
					
					int multiplier = 1;
					int startNo = 0;
					int endNo = 0;

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
							<font size="3"><B><%=(i + 1)%></B></font>&nbsp;
						<%}else{%>
							<A href="###" onClick='fetchSortedPageData("<%=sortBy%>","<%=i+1%>","pageNo")'><%=i+1%></A>
<%	                       }
				 if(currentPageNo != noofPages)
				  {
%>
						<a href="#" onClick='fetchSortedPageData("<%=sortBy%>","<%=currentPageNo+1%>","pageNo")'><img src="images/Toolbar_forward.gif" border="0"></a>
<%					
				  }
%>
			</td>
             </tr>
			  <tr valign="top" class='denotes'>
			    <td colspan="9"><font color="#ff0000">*</font> Denotes Mandatory </td>
				<td align="right"><input type='submit' class='input'  name='b1' value='Submit'>&nbsp;
				<input type='button' class='input'  name='b1' onClick='continueto()' value='Continue'></td>
				<INPUT TYPE="hidden" name='Operation' value="<%=request.getParameter("Operation")%>">
				<INPUT TYPE="hidden" name='sortedOrder' value="<%=sortOrder%>">
				<INPUT TYPE="hidden" name='sortedBy' value="<%=sortBy%>">
				<INPUT TYPE="hidden" name='pageNo' value="<%=pageNo%>">
				<INPUT TYPE="hidden" name='format' value="<%=request.getParameter("subOperation")%>">
				<INPUT TYPE="hidden" name='fromSummary' value="<%=fromSummary%>"><!-- added by subrahmanyam for 146463 -->

			    </tr>
			</table>
			
          </td>
	    </tr>
	  </table>
	  
	</form>
  </body>
</html>
<%}
		else
		{
			
			
			errorMessageObject = new ErrorMessage(  "No records found","QMSReportController?Operation=quotesExpiryReport"); 
			keyValueList.add(new KeyValue("ErrorCode","RNF")); 	
			keyValueList.add(new KeyValue("Operation","Add")); 	
			errorMessageObject.setKeyValueList(keyValueList);
			request.setAttribute("ErrorMessage",errorMessageObject);
			%>
			<jsp:forward page="../QMSESupplyErrorPage.jsp" />
			<%
		 }
		}
		catch(Exception e)
		{
			e.printStackTrace();
			//Logger.error(FILE_NAME,"Error in QuotesExpiryReports.jsp "+e);
      logger.error(FILE_NAME+"Error in QuotesExpiryReports.jsp "+e);
			errorMessageObject = new ErrorMessage(  "Error while retreiving the details","QMSReportController?Operation=quotesExpiryReport"); 
			keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
			keyValueList.add(new KeyValue("Operation","Add")); 	
			errorMessageObject.setKeyValueList(keyValueList);
			request.setAttribute("ErrorMessage",errorMessageObject);
%>
			<jsp:forward page="../QMSESupplyErrorPage.jsp"/>
<%
		}
%>
