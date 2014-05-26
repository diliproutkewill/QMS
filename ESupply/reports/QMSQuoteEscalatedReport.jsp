
<!-- 
File				: BuyRatesUpLoad.jsp
Sub-module		    : Quote Reports
Module			    : QMS
Author              : RamaKrishna Y.
date				: 16-09-2005

Modified Date	Modified By			Reason
-->

<%@ page language = "java"
		 import   = "java.util.ArrayList,
					 java.util.HashMap,
					com.foursoft.esupply.common.java.ErrorMessage,
					com.foursoft.esupply.common.java.KeyValue,
					com.foursoft.esupply.common.util.ESupplyDateUtility,
					com.qms.reports.java.ReportsEnterIdDOB,
					com.qms.reports.java.ReportDetailsDOB,
					org.apache.log4j.Logger"
%>

<jsp:useBean id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/>

<%!
  private static Logger logger = null;
  public static final String FILE_NAME  =  "QMSQuoteEscalatedReport.jsp"; %>

<%
    logger  = Logger.getLogger(FILE_NAME);	
		ArrayList       keyValueList      =   null;
		ErrorMessage    message           =   null;
		HashMap		mapValue1		    =	null;
		HashMap		mapValue2		    =	null;
		String	keyVlue	                 = null;
		ArrayList         escaltedList    =   new ArrayList();			
		ESupplyDateUtility fomater        =   new ESupplyDateUtility();
		ArrayList       approvedList      =   new ArrayList();
		String       disabled             =   ""; 
		String		  operation		= request.getParameter("Operation");
		String       checkValue     = request.getParameter("radiobutton");	
		String		  readOnly	=	"";
		//	 String		  suboperation	= request.getparameter("subOperation");
		String		fromSummary    = request.getParameter("fromSummary");//added by subrahmanayam for 146463

		String       sortBy        = request.getParameter("SortBy");
		String       sortOrder     = request.getParameter("SortOrder");
		String       pageNo        = request.getParameter("PageNo");
		String       imagePath     = "";
		if("ASC".equalsIgnoreCase(sortOrder))
		imagePath = request.getContextPath()+"/images/asc.gif";
		else
		imagePath = request.getContextPath()+"/images/desc.gif";

		if("others".equalsIgnoreCase(checkValue))
		{
		disabled="disabled";   
		readOnly	=	"readOnly";
		}

		ArrayList	  pageIterList	= null;
		ArrayList    dataList		= null;
		ArrayList    mainDataList	= null;
		ReportsEnterIdDOB reportenterDob = null;
		int noofRecords		=	0;
		int noofPages		=	0;
		int noOfSegments	=	0;	
		String empty		="";   //Added by Rakesh on 25-02-2011 for Issue:236363 
		try
		{
			noOfSegments            =   Integer.parseInt(loginbean.getUserPreferences().getSegmentSize());
			ESupplyDateUtility	eSupplyDateUtility  = new ESupplyDateUtility();
			String		dateFormat                  = "";
			dateFormat				                = loginbean.getUserPreferences().getDateFormat();
			String		currentDate				    = eSupplyDateUtility.getCurrentDateString(dateFormat);

			pageIterList			= new ArrayList();
			dataList				= new ArrayList(); 
			mainDataList			= new ArrayList(2); 
			reportenterDob			= new ReportsEnterIdDOB();
			mainDataList			= (ArrayList)request.getAttribute("mainDataList");
			reportenterDob			= (ReportsEnterIdDOB)session.getAttribute("reportenterDob");
			mapValue1					=	(HashMap)session.getAttribute("HashList1");
			mapValue2					=	(HashMap)session.getAttribute("HashList2");
			if(mapValue1==null)
			mapValue1	=	new HashMap();
			if(mapValue2==null)
			mapValue2	=	new HashMap();
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

				escaltedList     =    (ArrayList)mainDataList.get(1);
			}
			if(escaltedList!=null && escaltedList.size()>0)
			{
        
%>

<html>
<head>
<link rel="stylesheet" href="ESFoursoft_css.jsp">
<STYLE type="text/css">
A:link { COLOR: blue; FONT-WEIGHT: none; TEXT-DECORATION: none }
A:hover { COLOR: blue; FONT-WEIGHT: none; TEXT-DECORATION: underline }
A:visited { color: blue; font-weight: none; text-decoration: none }
</STYLE>
</head>
<script>
function functionCall(pageNo,operation)
{
document.forms[0].action='QMSReportController?Operation=Escalated&pageNo='+pageNo+'&nextOperation=Escalatedpagging&subOperation=html/excel';
document.forms[0].submit();
}

function updateValues(pageNo,operation)
{
    
	if(validChBoxes())
	{
	  document.forms[0].action='QMSReportController?Operation=Escalated&pageNo='+pageNo+'&subOperation=EscalatedUpdate';
      document.forms[0].submit();
    }
}
function continueto()
{
document.forms[0].action='QMSReportController?Operation=Escalated';
document.forms[0].submit();
}

function checkMarked(obj,id)
{
  var len='<%=dataList.size()%>';
  if(len=='1')
	{
		var objvalue=obj.name.substring((obj.name.length-1),obj.name.length);

		if(objvalue=='1' &&document.forms[0].mfValues2.checked==true )
		  document.forms[0].mfValues2.checked=false;
		else if(objvalue=='2' &&document.forms[0].mfValues1.checked==true )
		  document.forms[0].mfValues1.checked=false;
	}
  else
	{
		var objvalue=obj[id].name.substring((obj[id].name.length-1),obj[id].name.length);
		if(objvalue=='1' &&document.forms[0].mfValues2[id].checked==true )
			document.forms[0].mfValues2[id].checked=false;
		else if(objvalue=='2' &&document.forms[0].mfValues1[id].checked==true )
			document.forms[0].mfValues1[id].checked=false;
	}
}
function validChBoxes()
{
  var mfVal1=document.getElementsByName("mfValues1"); 
  var mfVal2=document.getElementsByName("mfValues2"); 
  var flag=false;
  
  if(mfVal1.length>1)
  {
    for(m=0;m<mfVal1.length;m++)
	  {
	    if(mfVal1[m].checked)
	    flag=true;
	  
	    if(mfVal2[m].checked)	
	    flag=true;
	  }
  }
  else
  {
   if(document.forms[0].mfValues1.checked)
    flag=true;
  
   if(document.forms[0].mfValues2.checked)
   flag=true;
  }
  return flag; 
}
function fetchSortedPageData(lable,pageNo,clickFrom)
{
   var sortBy     = lable;
   var sortOrder  = "";   
   var Operation  = document.forms[0].Operation.value;
   var radiobutton= document.forms[0].radiobutton.value;
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
	document.forms[0].action = "QMSReportController?Operation="+Operation+"&subOperation=html/excel&nextOperation=EscalatedPageBrowse&SortBy="+sortBy+"&SortOrder="+sortOrder+"&PageNo="+pageNo+"&radiobutton="+radiobutton;
	//alert(document.forms[0].action);
    document.forms[0].submit();
}
function openQuote(quoteId,index)
{
	var radiobutton= document.forms[0].radiobutton.value
	//Added by Anil.k
	var len = document.forms[0].isMultiQuote.length;
	if(len>1)
		var isMultiQuote = document.forms[0].isMultiQuote[index].value;
	else
		var isMultiQuote = document.forms[0].isMultiQuote.value;	
	if(isMultiQuote == "Y")
	{
		Bars = 'directories=no,location=no,menubar=no,status=no,titlebar=no,scrollbars=yes';
		Options='width=800,height=600,resizable=yes';
		Features=Bars+','+Options;
		Url='QMSMultiQuoteController?Operation=View&subOperation=Report&QuoteId='+quoteId+'&fromWhere=Pending&radiobutton='+radiobutton+'&originLoc='+""+'&destLoc='+""+'&isEscalated=Y';
		// Url='QMSMultiQuoteController?Operation=Modify&subOperation=EnterId&QuoteId='+quoteId+'&originLoc='+""+'&destLoc='+"";
		 Win = open(Url,'Doc',Features);
		//document.forms[0].submit();
	}
	else//Ended by Anil.k
	{
	Bars = 'directories=no,location=no,menubar=no,status=no,titlebar=no,scrollbars=yes';
	Options='width=800,height=600,resizable=yes';
	Url='QMSQuoteController?Operation=View&subOperation=Report&QuoteId='+quoteId+'&fromWhere=Pending&radiobutton='+radiobutton+'&quoteName='+quoteId+'&quoteStatus=ESCALATED&isEscalated=Y'; // Added by Gowtham for Escalated Modify issue
	Features=Bars+','+Options;
	Win=open(Url,'Doc',Features);
}
}
</script>

<script src="<%=request.getContextPath()%>/reports/TableSorting.js"></script>

  <body ><!-- onLoad='initTable("report0")' -->
	<form method='POST' >
	 
	  <table width="100%" border="0" cellspacing="0" cellpadding="0">
	    <tr valign="top" bgcolor="#FFFFFF">
		  <td>
			<table width="100%" cellpadding="4" cellspacing="1">
			  <tr valign="top" class='formlabel'>
			    <td colspan="3"><table width="99%" border="0" ><tr class='formlabel'>
			      <td> Quotes Awaiting Approval </td>
			      <td align=right>QS1012561</td></tr></table></td>
			  </tr>
		    </table>
			  
        
	  <table width="100%" border="0" cellspacing="0" cellpadding="0">
	    <tr valign="top" bgcolor="#FFFFFF">
		  <td><table width="100%"  cellspacing="1" cellpadding="4">
		  <tr valign="top" class='formdata'>
		  <td  width='20%'>Terminal Id:<br>
              <%=loginbean.getTerminalId()%> 
				</td>
				
            <td width='20%'>User:<br>
              <%=loginbean.getUserId()%> 
				</td>
				
            <td > Date:<br>
              <%=currentDate%> 
			  </td>
		  
		  <td>
			</td> 

		  </tr>
		  </table>
		  <table ID="report0" width="100%"  cellspacing="1" cellpadding="4">
        <tr valign="top" class='formheader'>
<%
		if(!"others".equalsIgnoreCase(checkValue))
		{
%>
          <td>Approve Quote</td>
          <td>Reject Quote </td>
<%
		}
%>
          <td NOWRAP><A href='###' onClick='fetchSortedPageData("Important","<%=pageNo%>","lable")' onmouseover='status="Sort by Important as";return true;' onmouseout="status = '';return true;" title="Sort by Important as">
				Important<%  if(sortBy.equalsIgnoreCase("Important")){%>					
				      <img SRC=<%=imagePath%> border="0">
				 <%}%></A></td>
			    <td><A href='###' onClick='fetchSortedPageData("CustomerId","<%=pageNo%>","lable")' onmouseover='status = "Sort by Customer Id as";return true;' onmouseout="status = '';return true;" title="Sort by Customer Id as">
				Customer Id<%  if(sortBy.equalsIgnoreCase("CustomerId")){%>					
				      <img SRC=<%=imagePath%> border="0">
				 <%}%></A></td>
				 <!--@@Modified by kameswari fot the WPBN issue-38175-->
				 <!--@@Added by kameswari for the WPBN issue-30313-->
				 <td NOWRAP><A href='###' onClick='fetchSortedPageData("CustomerName","<%=pageNo%>","lable")' onmouseover='status = "Sort by Customer Name as";return true;' onmouseout="status = '';return true;" title="Sort by Customer Name as">Customer Name<%  if(sortBy.equalsIgnoreCase("CustomerName")){%>					
				      <img SRC=<%=imagePath%> border="0">
				 <%}%></A></td>
				<!--@@WPBN 30313-->
			    <td><A href='###' onClick='fetchSortedPageData("QuoteId","<%=pageNo%>","lable")' onmouseover='status = "Sort by Quote Id as";return true;' onmouseout="status = '';return true;" title="Sort by Quote Id as">
				Quote Id<%  if(sortBy.equalsIgnoreCase("QuoteId")){%>					
				      <img SRC=<%=imagePath%> border="0">
				 <%}%></A></td>
			    <td><A href='###' onClick='fetchSortedPageData("ShimentMode","<%=pageNo%>","lable")' onmouseover='status = "Sort by Shiment Mode as";return true;' onmouseout="status = '';return true;" title="Sort by Shiment Mode as">
				Shiment Mode<%  if(sortBy.equalsIgnoreCase("ShimentMode")){%>					
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
          <td><A href='###' onClick='fetchSortedPageData("InternalNotes","<%=pageNo%>","lable")' onmouseover='status = "Sort by Internal Notes as";return true;' onmouseout="status = '';return true;" title="Sort by Internal Notes as">
				Internal Notes<%  if(sortBy.equalsIgnoreCase("InternalNotes")){%>					
				      <img SRC=<%=imagePath%> border="0">
				 <%}%></A></td>
			    <td><A href='###' onClick='fetchSortedPageData("ExternalNotes","<%=pageNo%>","lable")' onmouseover='status = "Sort by External Notes as";return true;' onmouseout="status = '';return true;" title="Sort by External Notes as">
				External Notes<%  if(sortBy.equalsIgnoreCase("ExternalNotes")){%>					
				      <img SRC=<%=imagePath%> border="0">
				 <%}%></A></td>
        </tr>
        <%
		  String tempKeyVlue ="";//Added by Anil.k for multiquote Escalated quotes
		  int indexValue	   =0;//Added by Anil.k for multiquote Escalated quotes
		 for(int i=0;i<escaltedList.size();i++)
		  {
			  
			  ReportDetailsDOB detailDOB  = (ReportDetailsDOB)escaltedList.get(i);
              
			  keyVlue				      =	detailDOB.getQuoteId();
%>
			  <tr valign="top" class='formdata'>
<%			//Added by Anil.k for multiquote pending quotes
			if(!tempKeyVlue.equalsIgnoreCase(keyVlue))
			{//End
			  if(!"others".equalsIgnoreCase(checkValue))
			  {
				if(mapValue1.containsKey(keyVlue))
				{
%>
					<td  NOWRAP><input type="checkbox" name="mfValues1" value="<%=keyVlue%>" id='<%=indexValue%>' onClick='checkMarked(document.forms[0].mfValues1,id)'checked <%=disabled%>></td>
<%
				}
				else
				{
%>
					<td  NOWRAP><input type="checkbox" name="mfValues1" value="<%=keyVlue%>" id='<%=indexValue%>' onClick='checkMarked(document.forms[0].mfValues1,id)' <%=disabled%>></td>
<%
				}
%>
				<input type="hidden" name="checkValue1" value="<%=keyVlue%>">
<%			    if(mapValue2.containsKey(keyVlue))
				{
%>
					<td  NOWRAP><input type="checkbox" name="mfValues2" value="<%=keyVlue%>" id='<%=indexValue%>' onClick='checkMarked(document.forms[0].mfValues2,id)' checked <%=disabled%>></td>
<%
				}
				else
				{
%>
					<td  NOWRAP><input type="checkbox" name="mfValues2" value="<%=keyVlue%>" id='<%=indexValue%>' onClick='checkMarked(document.forms[0].mfValues2,id)' <%=disabled%>></td>
<%
				}
			  }
%>
				<input type="hidden" name="checkValue2" value="<%=keyVlue%>">

              <td  NOWRAP>
<%
				if(detailDOB.getImportant()!=null)
				{
					if("I".equalsIgnoreCase(detailDOB.getImportant()))
					{
%>
						<img SRC=<%=request.getContextPath()+"/images/imp_flag.gif"%>>
<%
					}
				}
%>
			  </td>
              <td NOWRAP><%=detailDOB.getCustomerId()%></td>
			  <td ><%=detailDOB.getCustomerName()%></td><!--@@added by kameswari for the WPBN issue-30313-->
              <td NOWRAP>
			  <!--Commented and Added by Rakesh on 25-02-2011 for Issue:236363  -->
			  <!-- <a href="javascript:openQuote('<%=detailDOB.getQuoteId()%>','<%=indexValue%>')" onmouseover='status = "View Quote";return true;' onmouseout="status = '';return true;"><%=detailDOB.getQuoteId()%></a> -->
			  <% if("Y".equalsIgnoreCase(detailDOB.getIsMultiQuote())) {%>
			  <a href="QMSMultiQuoteController?Operation=View&subOperation=Report&QuoteId=+<%=(detailDOB.getQuoteId()).trim()%>+&fromWhere=Pending&radiobutton=+<%=checkValue%>+&originLoc=+<%=empty%>+&destLoc=+<%=empty%>+&isEscalated=Y" onmouseover='status = "View Quote";return true;' onmouseout="status = '';return true;"><%=detailDOB.getQuoteId()%></a>
			  <%}else{%>
			  <a href="QMSQuoteController?Operation=View&subOperation=Report&QuoteId=+<%=(detailDOB.getQuoteId()).trim()%>+&fromWhere=Pending&radiobutton=+<%=checkValue%>+&isEscalated=Y" onmouseover='status = "View Quote";return true;' onmouseout="status = '';return true;"><%=detailDOB.getQuoteId()%></a>
			  <%}%>
			  <!--Ended by Rakesh on 25-02-2011 for Issue:236363  -->
			  </td>
              <td NOWRAP><%="1".equals(detailDOB.getShipmentMode())?"Air":("2".equals(detailDOB.getShipmentMode())?"Sea":"Truck")%></td>
			  <td  NOWRAP><%=detailDOB.getServiceLevel()%></td>
			  <input type="hidden" name="isMultiQuote" id='<%=indexValue%>' value="<%=detailDOB.getIsMultiQuote()%>">
<% indexValue++; } else { //Added by Anil.k on 10-Jan-2011
				  if(!"others".equalsIgnoreCase(checkValue))
			  {%>
			  <td  NOWRAP></td><input type="hidden" name="checkValue1" value="<%=keyVlue%>">
				<td  NOWRAP></td><input type="hidden" name="checkValue2" value="<%=keyVlue%>">
				<% } %>
				<td  NOWRAP></td>
				<td  NOWRAP></td>
				<td  NOWRAP></td>
				<td  NOWRAP></td>
				<td  NOWRAP></td>
				<td  NOWRAP></td>				
	<% } %><!-- Ended by Anil.k -->
			  <td  NOWRAP><%=detailDOB.getFromCountry()%></td>
			  <td  NOWRAP><%=detailDOB.getFromLocation()%></td>
			  <td  NOWRAP><%=detailDOB.getToCountry()%></td>
			  <td  NOWRAP><%=detailDOB.getToLocation()%></td>
<%	
			if(mapValue1.containsKey(keyVlue))
				{
				detailDOB=(ReportDetailsDOB)mapValue1.get(keyVlue);
%>
			  <td   NOWRAP><input type='text' name="<%=keyVlue%>A" value='<%=detailDOB.getInternalRemarks()%>' <%=readOnly%> maxlength=200></td>
			   <td   NOWRAP><input type='text' name="<%=keyVlue%>B" value='<%=detailDOB.getExternalRemarks()%>' <%=readOnly%> maxlength=200></td>
<%              } else if(mapValue2.containsKey(keyVlue))
				{
				detailDOB=(ReportDetailsDOB)mapValue2.get(keyVlue);
%>
			  <td   NOWRAP><input type='text'  class="text" name="<%=keyVlue%>A" value='<%=detailDOB.getInternalRemarks()%>' <%=readOnly%> maxlength=200></td>
			   <td   NOWRAP><input type='text' class="text" name="<%=keyVlue%>B" value='<%=detailDOB.getExternalRemarks()%>' <%=readOnly%> maxlength=200></td>
<%              }				
			    else
				{%>
			  <td   NOWRAP><input type='text' class="text" name="<%=keyVlue%>A" value='<%=detailDOB.getInternalRemarks()%>' <%=readOnly%> maxlength=200></td>
			   <td  NOWRAP ><input type='text' class="text" name="<%=keyVlue%>B" value='<%=detailDOB.getExternalRemarks()%>' <%=readOnly%> maxlength=200></td>
			   <%}%>
			  </tr>
		<% tempKeyVlue = keyVlue;//Added by Anil.k	
		} %>
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
		   <INPUT TYPE="hidden" name='sortedOrder' value="<%=sortOrder%>">
		   <INPUT TYPE="hidden" name='sortedBy' value="<%=sortBy%>">
		   <INPUT TYPE="hidden" name='pageNo' value="<%=pageNo%>">
		   <INPUT TYPE="hidden" name='radiobutton' value="<%=checkValue!=null?checkValue:""%>">
		   <!-- added by subrahmanyam for 146463 -->
   		   <INPUT TYPE="hidden" name='fromSummary' value="<%=fromSummary%>">
	  </table>
	  
			
			<table width="100%">
			  <tr class='denotes'>
				
				<td width='50%'>&nbsp</td>
				<td width='50%' align='right'>
<%
		if(!"others".equalsIgnoreCase(checkValue))
		{
%>
					<input type='button' class='input'  name='update' value='Update' onClick='updateValues()'>
<%		}
%>
					<input type='Submit' class='input'  name='Submit' onClick='continueto()' value='Continue' >
				</td>
						
			<!-- 	<td><input type='hidden' class='input'  name='subOperation' value='Submit'></td> -->
				<input type='hidden' class='input'  name='Operation' value='<%=operation%>'>
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
			message = new ErrorMessage(  "No records found","QMSReportController?Operation=Escalated"); 
			keyValueList       = new ArrayList(3);
			keyValueList.add(new KeyValue("ErrorCode","RNF")); 	
			keyValueList.add(new KeyValue("Operation","Add")); 	
			message.setKeyValueList(keyValueList);
			request.setAttribute("ErrorMessage",message);
			%>
			<jsp:forward page="../QMSESupplyErrorPage.jsp"/>
			<%
		 }
		 }catch(Exception exp){
		exp.printStackTrace();
		//Logger.error(FILE_NAME,"Error in QMSQuoteEscalatedReport.jsp file ",exp.toString());
    logger.error(FILE_NAME+"Error in QMSQuoteEscalatedReport.jsp file "+exp.toString());
			 
		keyValueList       = new ArrayList(3);
		message = new ErrorMessage("Process could not be completed.Please try again","QMSQuoteEscalatedReport.jsp");
		keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
		keyValueList.add(new KeyValue("Operation",operation)); 	
		keyValueList.add(new KeyValue("Type","Country"));
		message.setKeyValueList(keyValueList);
             
		request.setAttribute("ErrorMessage",message); 
%>
			<jsp:forward page="../QMSESupplyErrorPage.jsp"/>
<%
	}
%>