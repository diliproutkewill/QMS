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
		             java.sql.Timestamp,
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
     public static final String FILE_NAME  =  "QMSQuoteApprovedReport.jsp"; %>
<% 
     logger  = Logger.getLogger(FILE_NAME);	
		 ErrorMessage    message           =   null;
		 ArrayList       approvedList      =   new ArrayList();
		 ArrayList	  keyValueList	 	 =   new ArrayList();
		 ESupplyDateUtility  dateutil    = new ESupplyDateUtility();
		 ReportDetailsDOB  detailDOB       =   null;
		 HashMap	  mapValue			 =	null;
		 String       keyVlue            = null; 
		 String		  operation		= request.getParameter("Operation");
	//	 String		  suboperation	= request.getparameter("subOperation");
		 String       sortBy        = request.getParameter("SortBy");
		 String       sortOrder     = request.getParameter("SortOrder");
		 String       pageNo        = request.getParameter("PageNo");
     String       fromSummary        =request.getParameter("fromSummary");//subrahmanyam

		 String       imagePath     = "";
		 if("ASC".equalsIgnoreCase(sortOrder))
		   imagePath = request.getContextPath()+"/images/asc.gif";
		 else
		   imagePath = request.getContextPath()+"/images/desc.gif";
       
		 System.out.println("operayion"+operation);
		 String format=loginbean.getUserPreferences().getDateFormat();
		 String dd ="";
		 String date="";
		 String datedate="";
		 String datetime="";
		 ArrayList	  pageIterList	= null;
 		 ArrayList    dataList		= null;
		 ArrayList    mainDataList	= null;
		 ReportsEnterIdDOB reportenterDob = null;
		 int noofRecords =0;
		 int noofPages	 =0;
         String datFormat=loginbean.getUserPreferences().getDateFormat();
		 dateutil.setPatternWithTime(datFormat);
		 String currentDate	=	dateutil.getCurrentDateString(loginbean.getUserPreferences().getDateFormat());
		 mapValue					=	(HashMap)session.getAttribute("HashList");
		 if(mapValue==null)
		   mapValue	=	new HashMap();

		 int noOfSegments	   =	0;
		 int relativeOffset	   =	loginbean.getRelativeOffset();
		 Timestamp localTime   =	null;
		 String		empty	   =""; //Added by Rakesh on 25-02-2011 for Issue:236363 
		 try
		{
			dateutil.setPatternWithTime(format);
		}
		catch(Exception exp)
		{
			//Logger.error(FILE_NAME," Error in JSP UserPreferences DateFormat---> "+exp.toString());
      logger.error(FILE_NAME+" Error in JSP UserPreferences DateFormat---> "+exp.toString());
		}

		 try
		 {
		 pageIterList			= new ArrayList();
		 dataList				= new ArrayList(); 
		 mainDataList			= new ArrayList(); 
		 reportenterDob			= new ReportsEnterIdDOB();
		 mainDataList			= (ArrayList)request.getAttribute("mainDataList");
         reportenterDob			= (ReportsEnterIdDOB)session.getAttribute("reportenterDob");

		 noOfSegments            =   Integer.parseInt(loginbean.getUserPreferences().getSegmentSize());

		 if(mainDataList!=null && mainDataList.size()>0)
			{
				pageIterList       = 	(ArrayList)mainDataList.get(0);
				dataList		   =	(ArrayList)mainDataList.get(1);

				Integer  no_ofRecords   =   (Integer)pageIterList.get(0);
				Integer  no_ofPages		=	(Integer)pageIterList.get(1);

				noofRecords				=	no_ofRecords.intValue();
				noofPages				=	no_ofPages.intValue();

			 session.setAttribute("reportenterDob",reportenterDob);	
			 approvedList     =    (ArrayList)request.getAttribute("Approved");
			 session.setAttribute("dataList",dataList);
			 session.setAttribute("Approved",approvedList);
			}
			if(dataList!=null && dataList.size()>0)
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
<script src="<%=request.getContextPath()%>/reports/TableSorting.js"></script>
<script>
/*function validateForm(obj)
	{
	   if(obj.name=='Excel')
          document.forms[0].Submit.value='Excel';
	   else
		  document.forms[0].Submit.value='Continue';
	   
	}*/
function excelReport()
{
	document.forms[0].action='QMSReportController?Operation=Approved&nextOperation=Submit&format=Excel';
	document.forms[0].submit();
    //document.forms[0].action='';
}
function updateStatus()
{
	document.forms[0].action='QMSReportController?Operation=Approved&subOperation=ApprovedUpdate';
}
function functionCall(pageNo,operation)
{
	document.forms[0].action='QMSReportController?Operation='+operation+'&pageNo='+pageNo+'&nextOperation=Approvedpagging';
	document.forms[0].submit();
}
function forwardQuote(quoteId,index)
{
//	alert(index);
	var len = document.forms[0].isMultiQuote.length;//Added by Anil.k
	if(len>1)
		 var isMultiQuote = document.forms[0].isMultiQuote[index].value;
	else
		 var isMultiQuote = document.forms[0].isMultiQuote.value;//Added by Anil.k
   if(isMultiQuote == "Y")
    document.forms[0].action='QMSMultiQuoteController?Operation=View&subOperation=Report&QuoteId='+quoteId+'&from=summary&fromWhere=Approved'+'&originLoc='+""+'&destLoc='+"";
   else//End
    document.forms[0].action='QMSQuoteController?Operation=View&subOperation=Report&QuoteId='+quoteId+'&from=summary&fromWhere=Approved';
    document.forms[0].submit();
 
}
function openQuote(quoteId)
{
	Bars = 'directories=no,location=no,menubar=no,status=no,titlebar=no,scrollbars=yes';
	Options='width=800,height=600,resizable=yes';
	Url='QMSQuoteController?Operation=View&subOperation=Report&QuoteId='+quoteId+'&fromWhere=Pending';
	Features=Bars+','+Options;
	Win=open(Url,'Doc',Features);
	Win.resizeTo(screen.availWidth,screen.availHeight) 
	Win.focus(); 
	Win.moveTo(0,0);
}
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
	document.forms[0].action = "QMSReportController?Operation="+Operation+"&nextOperation=pageBrowse&SortBy="+sortBy+"&SortOrder="+sortOrder+"&PageNo="+pageNo;
	//alert(document.forms[0].action);
    document.forms[0].submit();
}
</script>

  <body onLoad=''><!-- initTable("report0") -->
	<form method='POST'>
	  <table width="100%" border="0" cellspacing="0" cellpadding="0">
	    <tr valign="top" bgcolor="#FFFFFF">
		  <td>
			<table width="100%" cellpadding="4" cellspacing="1">
			  <tr valign="top" class='formlabel'>
			    <td colspan="3"><table width="100%" border="0" ><tr class='formlabel'>
			      <td>Quotes Approved</td>
			      <td align=right>QS1010641</td></tr></table></td>
			  </tr>
		    </table>
			  
        <table width="100%"  cellspacing="1" cellpadding="4">
          
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
		  </tr>
			</table>
				<table ID="report0" width="100%"  cellspacing="1" cellpadding="4">
				
			  <tr valign="top" class='formheader'>
			    <TH rowspan="2" NOWRAP><A href='###' onClick='fetchSortedPageData("Important","<%=pageNo%>","lable")' onmouseover='status="Sort by Important as";return true;' onmouseout="status = '';return true;" title="Sort by Important as">
				Imp<%  if(sortBy.equalsIgnoreCase("Important")){%>					
				      <img SRC=<%=imagePath%> border="0">
				 <%}%></A></TH>
			    <TH rowspan="2"><A href='###' onClick='fetchSortedPageData("CustomerId","<%=pageNo%>","lable")' onmouseover='status = "Sort by Customer Id as";return true;' onmouseout="status = '';return true;" title="Sort by Customer Id as">
				Customer Id<%  if(sortBy.equalsIgnoreCase("CustomerId")){%>					
				      <img SRC=<%=imagePath%> border="0">
				 <%}%></A></TH>
				 <!--@@Modified by Kameswari for the WPBN issue-38175-->
				  <!--@@Added by kameswari for the WPBN issue-30313-->
				  <TH rowspan="2" NOWRAP><A href='###' onClick='fetchSortedPageData("CustomerName","<%=pageNo%>","lable")' onmouseover='status = "Sort by Customer Name as";return true;' onmouseout="status = '';return true;" title="Sort by Customer Name as">Customer Name<%  if(sortBy.equalsIgnoreCase("CustomerName")){%>					
				      <img SRC=<%=imagePath%> border="0">
				 <%}%></A></TH>
				  <!--@@WPBN 30313-->
			    <TH rowspan="2"><A href='###' onClick='fetchSortedPageData("QuoteId","<%=pageNo%>","lable")' onmouseover='status = "Sort by Quote Id as";return true;' onmouseout="status = '';return true;" title="Sort by Quote Id as">
				Quote Id<%  if(sortBy.equalsIgnoreCase("QuoteId")){%>					
				      <img SRC=<%=imagePath%> border="0">
				 <%}%></A></TH>
				 <TH rowspan="2">Send Options</TH>
			    <TH rowspan="2">Send Quote </TH>
			    <TH rowspan="2"><A href='###' onClick='fetchSortedPageData("ShimentMode","<%=pageNo%>","lable")' onmouseover='status = "Sort by Shiment Mode as";return true;' onmouseout="status = '';return true;" title="Sort by Shiment Mode as">
				Shiment Mode<%  if(sortBy.equalsIgnoreCase("ShimentMode")){%>					
				      <img SRC=<%=imagePath%> border="0">
				 <%}%></A></TH>
			    <TH rowspan="2"><A href='###' onClick='fetchSortedPageData("ServiceLevel","<%=pageNo%>","lable")' onmouseover='status = "Sort by Service Level as";return true;' onmouseout="status = '';return true;" title="Sort by Service Level as">
				Service Level<%  if(sortBy.equalsIgnoreCase("ServiceLevel")){%>					
				      <img SRC=<%=imagePath%> border="0">
				 <%}%></A></TH>
			    <TH rowspan="2"><A href='###' onClick='fetchSortedPageData("FromCountry","<%=pageNo%>","lable")' onmouseover='status = "Sort by From Country as";return true;' onmouseout="status = '';return true;" title="Sort by From Country as">
				From Country<%  if(sortBy.equalsIgnoreCase("FromCountry")){%>					
				      <img SRC=<%=imagePath%> border="0">
				 <%}%></A></TH>
			    <TH rowspan="2"><A href='###' onClick='fetchSortedPageData("FromLocation","<%=pageNo%>","lable")' onmouseover='status = "Sort by From Location as";return true;' onmouseout="status = '';return true;" title="Sort by From Location as">
				From Location<%  if(sortBy.equalsIgnoreCase("FromLocation")){%>					
				      <img SRC=<%=imagePath%> border="0">
				 <%}%></A></TH>
			    <TH rowspan="2"><A href='###' onClick='fetchSortedPageData("ToCountry","<%=pageNo%>","lable")' onmouseover='status = "Sort by To Country as";return true;' onmouseout="status = '';return true;" title="Sort by To Country as">
				To Country<%  if(sortBy.equalsIgnoreCase("ToCountry")){%>					
				      <img SRC=<%=imagePath%> border="0">
				 <%}%></A></TH>
			    <TH rowspan="2"><A href='###' onClick='fetchSortedPageData("ToLocation","<%=pageNo%>","lable")' onmouseover='status = "Sort by To Location as";return true;' onmouseout="status = '';return true;" title="Sort by To Location as">
				To Location<%  if(sortBy.equalsIgnoreCase("ToLocation")){%>					
				      <img SRC=<%=imagePath%> border="0">
				 <%}%></A></TH>
				 <!--@@WPBN issue-38175-->
			    <TH rowspan="2" ><A href='###' onClick='fetchSortedPageData("DueDate","<%=pageNo%>","lable")' onmouseover='status = "Sort by Due Date as";return true;' onmouseout="status = '';return true;" title="Sort by Due Date as">
				Due Date<%  if(sortBy.equalsIgnoreCase("DueDate")){%>					
				      <img SRC=<%=imagePath%> border="0">
				 <%}%></A></TH>
			    <TH rowspan="2" ><A href='###' onClick='fetchSortedPageData("DueTime","<%=pageNo%>","lable")' onmouseover='status = "Sort by Due Time as";return true;' onmouseout="status = '';return true;" title="Sort by Due Time as">
				Due Time<%  if(sortBy.equalsIgnoreCase("DueTime")){%>					
				      <img SRC=<%=imagePath%> border="0">
				 <%}%></A></TH>
			    <TH rowspan="2" ><A href='###' onClick='fetchSortedPageData("RejectedBy","<%=pageNo%>","lable")' onmouseover='status = "Sort by Approved By as";return true;' onmouseout="status = '';return true;" title="Sort by Approved By as">
				Approved By<%  if(sortBy.equalsIgnoreCase("RejectedBy")){%>					
				      <img SRC=<%=imagePath%> border="0">
				 <%}%></A></TH>
			    <TH rowspan="2" ><A href='###' onClick='fetchSortedPageData("RejectedDate","<%=pageNo%>","lable")' onmouseover='status = "Sort by Approved Date as";return true;' onmouseout="status = '';return true;" title="Sort by Approved Date as">
				Approved Date<%  if(sortBy.equalsIgnoreCase("RejectedDate")){%>					
				      <img SRC=<%=imagePath%> border="0">
				 <%}%></A></TH>
			    <TH rowspan="2" ><A href='###' onClick='fetchSortedPageData("RejectedTime","<%=pageNo%>","lable")' onmouseover='status = "Sort by Approved Time as";return true;' onmouseout="status = '';return true;" title="Sort by Approved Time as">
				Approved Time<%  if(sortBy.equalsIgnoreCase("RejectedTime")){%>					
				      <img SRC=<%=imagePath%> border="0">
				 <%}%></A></TH>
			    <TH colspan="2" NOWRAP>Notes</TH>
				<!-- <td colspan="2">EXTERNAL Notes</td> -->
			    </tr>
			  <tr valign="top" class='formheader'>
			    <TH><A href='###' onClick='fetchSortedPageData("InternalNotes","<%=pageNo%>","lable")' onmouseover='status = "Sort by Internal Notes as";return true;' onmouseout="status = '';return true;" title="Sort by Internal Notes as">
				Internal Notes<%  if(sortBy.equalsIgnoreCase("InternalNotes")){
					if(sortOrder.equalsIgnoreCase("ASC")){%>
					  <img SRC=<%=request.getContextPath()+"/images/asc.gif"%> border="0">
                   <%}else{%>
				      <img SRC=<%=request.getContextPath()+"/images/desc.gif"%> border="0">
				   <%}
				}%></TH>
			    <TH><A href='###' onClick='fetchSortedPageData("ExternalNotes","<%=pageNo%>","lable")' onmouseover='status = "Sort by External Notes as";return true;' onmouseout="status = '';return true;" title="Sort by External Notes as">
				External Notes<%  if(sortBy.equalsIgnoreCase("ExternalNotes")){
					if(sortOrder.equalsIgnoreCase("ASC")){%>
					  <img SRC=<%=request.getContextPath()+"/images/asc.gif"%> border="0">
                   <%}else{%>
				      <img SRC=<%=request.getContextPath()+"/images/desc.gif"%> border="0">
				   <%}
				}%></th>
			  </tr>
			  <%
				String tempKeyVlue ="";//Added by Anil.k for multiquote Approved quotes
				int indexValue	   =0;//Added by Anil.k for multiquote Approved quotes
				   for(int i=0;i<dataList.size();i++)
				   {
				      detailDOB = (ReportDetailsDOB)dataList.get(i);
				      keyVlue	=	detailDOB.getQuoteId();
				%>
			  <tr valign="top" class='formdata'>
			  <!-- <td ><%=detailDOB.getImportant()%></td> -->
<%			//Added by Anil.k for multiquote pending quotes
			if(!tempKeyVlue.equalsIgnoreCase(keyVlue))
			{//End
%>
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
              <td  NOWRAP><%=detailDOB.getCustomerId()%></td>
			  <td><%=detailDOB.getCustomerName()%></td><!--@@added by kameswari for the WPBN issue-30313-->
              <td  NOWRAP>
			  			  
			  <!--Commented and Added by Rakesh on 25-02-2011 for Issue:236363  -->
			  <!-- <a href="javascript:forwardQuote('<%=detailDOB.getQuoteId()%>','<%=indexValue%>');" ><%=detailDOB.getQuoteId()==null?"-":detailDOB.getQuoteId()%></a> -->
			  <% if("Y".equalsIgnoreCase(detailDOB.getIsMultiQuote())) {%> 	   <!-- Modified by Rakesh for Issue:240224 -->
			  <a href="QMSMultiQuoteController?Operation=View&subOperation=Report&QuoteId=+<%=(detailDOB.getQuoteId()).trim()%> +&from=summary&fromWhere=Approved'+'&originLoc=+<%=empty.trim()%>+&destLoc=+<%=empty.trim()%>" ><%=detailDOB.getQuoteId()==null?"-":detailDOB.getQuoteId()%></a>
			  <% }else{ %>
			  <a href="QMSQuoteController?Operation=View&subOperation=Report&QuoteId=+<%=(detailDOB.getQuoteId()).trim()%>+&from=summary&fromWhere=Approved" ><%=detailDOB.getQuoteId()==null?"-":detailDOB.getQuoteId()%></a>
			  <%}%>
			  <!--Ended by Rakesh on 25-02-2011 for Issue:236363  -->
			  </td>
			  <td><%=detailDOB.getSendOptions()%></td>

<%			   if(mapValue.containsKey(keyVlue))
				{
%>
					<td  NOWRAP><input type="checkbox" name="mfValues" value="<%=keyVlue%>" id='<%=indexValue%>' checked></td>
<%
				}
				else
				{
%>
					<td  NOWRAP><input type="checkbox" name="mfValues" value="<%=keyVlue%>" id='<%=indexValue%>'></td>
<%
				}
%>
            <input type="hidden" name="checkValue" value="<%=keyVlue%>">

              <td   NOWRAP><%=detailDOB.getShipmentMode().equals("1")?"Air":(detailDOB.getShipmentMode().equals("2")?"Sea":"Truck")%></td>
			  <td  NOWRAP ><%=detailDOB.getServiceLevel()%></td>
			  <input type="hidden" name="isMultiQuote" id='<%=indexValue%>' value="<%=detailDOB.getIsMultiQuote()%>">
<%	 indexValue++; }	else {	%><!-- //Added by Anil.k on 10-Jan-2011 -->
				<input type="hidden" name="checkValue" value="<%=keyVlue%>">
				<td  NOWRAP></td>
				<td  NOWRAP></td>				
				<td  NOWRAP></td>
				<td  NOWRAP></td>
				<td  NOWRAP></td>
				<td  NOWRAP></td>
				<td  NOWRAP></td>
				<td  NOWRAP></td>
<% } %><!-- Ended by Anil.k -->
			  <td  NOWRAP ><%=detailDOB.getFromCountry()%></td>
			  <td  NOWRAP ><%=detailDOB.getFromLocation()%></td>
			  <td  NOWRAP ><%=detailDOB.getToCountry()%></td>
			  <td  NOWRAP ><%=detailDOB.getToLocation()%></td>
<%
			 String str[]   = null;
			if(detailDOB.getDueDateTmstmp()!=null)			
			{
			  localTime = new Timestamp(detailDOB.getDueDateTmstmp().getTime() + relativeOffset);
			  str = dateutil.getDisplayStringArray(localTime);
			  datedate=str[0];
			  datetime=str[1];
			}
%>
			  <td nowrap ><%=datedate%></td>
			  <td nowrap ><%=datetime%></td>
<%
			if(detailDOB.getApprovedRejTstmp()!=null)			
			{
			  localTime = new Timestamp(detailDOB.getApprovedRejTstmp().getTime() + relativeOffset);
			  str = dateutil.getDisplayStringArray(localTime);
			  datedate=str[0];
			  datetime=str[1];
			  
			}
%>

			  <td  NOWRAP ><%=detailDOB.getApprovedRrejectedBy()%></td>
			  <td  NOWRAP ><%=datedate%></td>
			  <td  NOWRAP ><%=datetime%></td>			  
			  <td  NOWRAP ><input type="text" class="text" name="<%=keyVlue%>A" value="<%=detailDOB.getInternalRemarks()!=null?detailDOB.getInternalRemarks():""%>" readonly></td>
			  <td  NOWRAP ><input type="text" class="text" name="<%=keyVlue%>B" value="<%=detailDOB.getExternalRemarks()!=null?detailDOB.getExternalRemarks():""%>" readonly></td>
			  
			  </tr>
<%	tempKeyVlue = keyVlue;//Added by Anil.k
				  }%>
			 </table>
			 <table width="100%" cellpadding="4" cellspacing="0">
			 <tr class="text" vAlign="top">
                <td valign="middle" colspan="10" >
<%
					int  currentPageNo	=	0;
					currentPageNo = Integer.parseInt(pageNo);

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
            </table>
			<table width="100%"  cellspacing="1" cellpadding="4">
			  <tr valign="top" class='denotes'>
			    <td colspan="12"></td>
			    <td colspan="5" align="right">
			    <input type='button' class='input'  name='Excel' onClick = 'excelReport()' value='Excel'> 
		        <input type='Submit' class='input'  name='Update' onClick = 'updateStatus()' value='Update'>
			<!-- 	<input type='hidden' class='input'  name='nextOperation' value='Submit'> -->
				<INPUT TYPE="hidden" name='sortedOrder' value="<%=sortOrder%>">
				<INPUT TYPE="hidden" name='sortedBy' value="<%=sortBy%>">
				<INPUT TYPE="hidden" name='pageNo' value="<%=pageNo%>">
				<!--input type='hidden' class='input'  name='Submit'-->
				<input type='hidden' class='input'  name='Operation' value='<%=operation%>'></td>
			 	 <INPUT TYPE="hidden" name='fromSummary' value="<%=fromSummary%>"><!-- subrahmanyam -->
		
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
			message = new ErrorMessage(  "No records found","ESMenuRightPanel.jsp?link=es-et-Administration-Reports"); 
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
		//Logger.error(FILE_NAME,"Error in QMSQuoteApprovedReport.jsp file ",exp.toString());
    logger.error(FILE_NAME+"Error in QMSQuoteApprovedReport.jsp file "+exp.toString());
			 
		keyValueList       = new ArrayList(3);
		message = new ErrorMessage("Process could not be completed.Please try again","QMSQuoteApprovedReport.jsp");
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