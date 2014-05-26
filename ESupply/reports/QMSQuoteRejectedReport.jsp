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
  public static final String FILE_NAME  =  "QMSQuoteRejectedReport.jsp"; %>
<%
     logger  = Logger.getLogger(FILE_NAME);	
		 ErrorMessage         message      =   null;
		 ESupplyDateUtility   dateutil     =   new ESupplyDateUtility();
		 ArrayList            rejectList   =   new ArrayList();
		 ArrayList            approvedList =   new ArrayList();
		 ArrayList	          keyValueList =   new ArrayList();
		 ReportDetailsDOB     detailDOB    =   null;
		 String		  operation		= request.getParameter("Operation");
		 String		  suboperation	= request.getParameter("subOperation");
		 String		  nextOperation	= request.getParameter("nextOperation");
		 String       sortBy        = request.getParameter("SortBy");
		 String       sortOrder     = request.getParameter("SortOrder");
		 String       pageNo        = request.getParameter("PageNo");
		 String       imagePath     = "";
		 if("ASC".equalsIgnoreCase(sortOrder))
		   imagePath = request.getContextPath()+"/images/asc.gif";
		 else
		   imagePath = request.getContextPath()+"/images/desc.gif";
       
		 String dd ="";
		 String date="";
		 String datedate="";
		 String datetime="";
         String format=loginbean.getUserPreferences().getDateFormat();
		 int relativeOffset	   =	loginbean.getRelativeOffset();
		 Timestamp localTime   =	null;

		 ArrayList	  pageIterList	= null;
 		 ArrayList    dataList		= null;
		 ArrayList    mainDataList	= null;
		 ReportsEnterIdDOB reportenterDob = null;
		 int noofRecords	=	0;
		 int noofPages		=	0;
		 int noOfSegments	=	0;
		 String empty		=""; //Added by Rakesh on 25-02-2011 for Issue:236363 
		 try
		{
			dateutil.setPatternWithTime(format);
		}
		catch(Exception exp)
		{
			//Logger.error(FILE_NAME," Error in JSP UserPreferences DateFormat---> "+exp.toString());
      logger.error(FILE_NAME+" Error in JSP UserPreferences DateFormat---> "+exp.toString());
		}

		String currentDate=dateutil.getCurrentDateString(loginbean.getUserPreferences().getDateFormat());
		 try
		 {
		 noOfSegments           =   Integer.parseInt(loginbean.getUserPreferences().getSegmentSize());
		 pageIterList			= new ArrayList();
		 dataList				= new ArrayList(); 
		 mainDataList			= new ArrayList(); 
		 reportenterDob			= new ReportsEnterIdDOB();
		 mainDataList			= (ArrayList)request.getAttribute("mainDataList");
         reportenterDob			= (ReportsEnterIdDOB)session.getAttribute("reportenterDob");
		 System.out.println("operayion"+mainDataList);
		 if(mainDataList!=null && mainDataList.size()>0)
			{
				pageIterList       = 	(ArrayList)mainDataList.get(0);
				dataList		   =	(ArrayList)mainDataList.get(1);

				Integer  no_ofRecords   =   (Integer)pageIterList.get(0);
				Integer  no_ofPages		=	(Integer)pageIterList.get(1);

				noofRecords				=	no_ofRecords.intValue();
				noofPages				=	no_ofPages.intValue();

		session.setAttribute("reportenterDob",reportenterDob);	
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
<script>
function functionCall(pageNo,operation)
{
document.forms[0].action='QMSReportController?Operation='+operation+'&pageNo='+pageNo+'&nextOperation=Rejectedpagging';
document.forms[0].submit();
}
function forwardQuote(quoteId,index)
{
	var len = document.forms[0].isMultiQuote.length;//Added by Anil.k
    if(len>1)
		 var isMultiQuote = document.forms[0].isMultiQuote[index].value;
	else
		 var isMultiQuote = document.forms[0].isMultiQuote.value;	
	if(isMultiQuote=="Y")
	{
		document.forms[0].action='QMSMultiQuoteController?Operation=Modify&subOperation=EnterId&QuoteId='+quoteId+'&originLoc='+""+'&destLoc='+"";
		document.forms[0].submit();
	}
	else
	{//Ended by Anil.k
    document.forms[0].action='QMSQuoteController?Operation=Modify&subOperation=EnterId&QuoteId='+quoteId;
    document.forms[0].submit();
	}
 
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


  <body>
	<form method='POST' action="QMSReportController?Operation=Rejected&nextOperation=Submit" >
	  <table width="100%" border="0" cellspacing="0" cellpadding="0">
	    <tr valign="top" bgcolor="#FFFFFF">
		  <td>
			<table width="100%" cellpadding="4" cellspacing="1">
			  <tr valign="top" class='formlabel'>
			    <td colspan="3"><table width="840" border="0" ><tr class='formlabel'>
			      <td> Quotes Rejected </td>
			      <td align=right>QS1010741</td></tr></table></td>
			  </tr>
		    </table>
			  
        <table width="100%"  cellspacing="1" cellpadding="4">
          <tr valign="top" class='formdata'>
			    <td colspan=3>&nbsp;
				</td>
				
		  </tr>
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
				<table  width="100%"  cellspacing="1" cellpadding="4">
				
			  <tr valign="top" class='formheader'>
			    <td><A href='###' onClick='fetchSortedPageData("Important","<%=pageNo%>","lable")' onmouseover='status="Sort by Important as";return true;' onmouseout="status = '';return true;" title="Sort by Important as">
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
				 <!-- @@WPBN issue-38175-->
			    <td width="40%" NOWRAP><A href='###' onClick='fetchSortedPageData("DueDate","<%=pageNo%>","lable")' onmouseover='status = "Sort by Due Date as";return true;' onmouseout="status = '';return true;" title="Sort by Due Date as">
				Due Date<%  if(sortBy.equalsIgnoreCase("DueDate")){%>					
				      <img SRC=<%=imagePath%> border="0">
				 <%}%></A></td>
			    <td><A href='###' onClick='fetchSortedPageData("DueTime","<%=pageNo%>","lable")' onmouseover='status = "Sort by Due Time as";return true;' onmouseout="status = '';return true;" title="Sort by Due Time as">
				Due Time<%  if(sortBy.equalsIgnoreCase("DueTime")){%>					
				      <img SRC=<%=imagePath%> border="0">
				 <%}%></A></td>
			    <td><A href='###' onClick='fetchSortedPageData("RejectedBy","<%=pageNo%>","lable")' onmouseover='status = "Sort by Rejected By as";return true;' onmouseout="status = '';return true;" title="Sort by Rejected By as">
				Rejected By<%  if(sortBy.equalsIgnoreCase("RejectedBy")){%>					
				      <img SRC=<%=imagePath%> border="0">
				 <%}%></A></td>
			    <td><A href='###' onClick='fetchSortedPageData("RejectedDate","<%=pageNo%>","lable")' onmouseover='status = "Sort by Rejected Date as";return true;' onmouseout="status = '';return true;" title="Sort by Rejected Date as">
				Rejected Date<%  if(sortBy.equalsIgnoreCase("RejectedDate")){%>					
				      <img SRC=<%=imagePath%> border="0">
				 <%}%></A></td>
			    <td><A href='###' onClick='fetchSortedPageData("RejectedTime","<%=pageNo%>","lable")' onmouseover='status = "Sort by Rejected Time as";return true;' onmouseout="status = '';return true;" title="Sort by Rejected Time as">
				Rejected Time<%  if(sortBy.equalsIgnoreCase("RejectedTime")){%>					
				      <img SRC=<%=imagePath%> border="0">
				 <%}%></A></td>
			    <!-- <td>Reasons for Rejection </td> -->
			 
			    <td><A href='###' onClick='fetchSortedPageData("InternalNotes","<%=pageNo%>","lable")' onmouseover='status = "Sort by Internal Notes as";return true;' onmouseout="status = '';return true;" title="Sort by Internal Notes as">
				Internal Notes<%  if(sortBy.equalsIgnoreCase("InternalNotes")){%>					
				      <img SRC=<%=imagePath%> border="0">
				 <%}%></A></td>
			    <td><A href='###' onClick='fetchSortedPageData("ExternalNotes","<%=pageNo%>","lable")' onmouseover='status = "Sort by External Notes as";return true;' onmouseout="status = '';return true;" title="Sort by External Notes as">
				External Notes<%  if(sortBy.equalsIgnoreCase("ExternalNotes")){%>					
				      <img SRC=<%=imagePath%> border="0">
				 <%}%></A></td>
			  </tr>
				<%	String tempKeyVlue ="";//Added by Anil.k for multiquote Rejected quotes
					String keyVlue	   ="";//Added by Anil.k for multiquote Rejected quotes
					int indexValue	   =0;//Added by Anil.k for multiquote Rejected quotes
					for(int i=0;i<dataList.size();i++)
				  {
				      detailDOB = (ReportDetailsDOB)dataList.get(i);
					  keyVlue	=	detailDOB.getQuoteId();//Added by Anil.k for multiquote Rejected quotes
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
						<INPUT TYPE="image" SRC=<%=request.getContextPath()+"/images/imp_flag.gif"%>>
<%
					}
				}
%>
              <td  NOWRAP><%=detailDOB.getCustomerId()%></td>
			  <td><%=detailDOB.getCustomerName()%></td><!--@@added by kameswari for the WPBN issue-30313-->
              <td  NOWRAP>
			  <!--Commented and Added by Rakesh on 25-02-2011 for Issue:236363  -->
			  <!-- <a href="javascript:forwardQuote('<%=detailDOB.getQuoteId()%>','<%=indexValue%>');" ><%=detailDOB.getQuoteId()==null?"-":detailDOB.getQuoteId()%></a> -->
			  <% if("Y".equalsIgnoreCase(detailDOB.getIsMultiQuote())) {%>
			  <a href="QMSMultiQuoteController?Operation=Modify&subOperation=EnterId&QuoteId=+<%=(detailDOB.getQuoteId()).trim()%>+&originLoc=+<%=empty.trim()%>+&destLoc=+<%=empty.trim()%>" ><%=detailDOB.getQuoteId()==null?"-":detailDOB.getQuoteId()%></a>
			  <%}else{%>
			  <a href="QMSQuoteController?Operation=Modify&subOperation=EnterId&QuoteId=+<%=(detailDOB.getQuoteId()).trim()%>" ><%=detailDOB.getQuoteId()==null?"-":detailDOB.getQuoteId()%></a>			  
			  <%}%>
			  <!--Ended by Rakesh on 25-02-2011 for Issue:236363  -->
			  </td>
              <td  NOWRAP ><%=detailDOB.getShipmentMode().equals("1")?"Air":(detailDOB.getShipmentMode().equals("2")?"Sea":"Truck")%></td>
			  <td  NOWRAP ><%=detailDOB.getServiceLevel()%></td>
			  <input type="hidden" name="isMultiQuote" id='<%=indexValue%>' value="<%=detailDOB.getIsMultiQuote()%>"><!-- Added by Anil.k for multiquote Rejected quotes -->
<%  indexValue++; }	else { %><!-- //Added by Anil.k on 10-Jan-2011 -->
				<td  NOWRAP></td>
				<td  NOWRAP></td>
				<td  NOWRAP></td>
				<td  NOWRAP></td>
				<td  NOWRAP></td>
				<td  NOWRAP></td>
<%	}	%><!-- Ended by Anil.k -->
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
			  <td  NOWRAP ><%=datedate%></td>
			  <td  NOWRAP ><%=datetime%></td>
			  <td  NOWRAP ><%=detailDOB.getApprovedRrejectedBy()%></td>
<%
			if(detailDOB.getApprovedRejTstmp()!=null)			
			{
			  localTime = new Timestamp(detailDOB.getApprovedRejTstmp().getTime() + relativeOffset);
			  str = dateutil.getDisplayStringArray(localTime);
			  datedate=str[0];
			  datetime=str[1];
			}
%>

			  <td  NOWRAP ><%=datedate%></td>
			  <td  NOWRAP ><%=datetime%></td>
			  <!-- <td  ><input type='text' value='<%=detailDOB.getInternalRemarks()%>'></td> -->
			  <td  NOWRAP ><input type="text" class="text" value="<%=detailDOB.getInternalRemarks()!=null?detailDOB.getInternalRemarks():""%>" readonly></td>
			  <td  NOWRAP ><input type="text" class="text" value="<%=detailDOB.getExternalRemarks()!=null?detailDOB.getExternalRemarks():""%>" readonly></td>
			  </tr>
<%	tempKeyVlue = keyVlue;//Added by Anil.k	
			  }%>
			  			 </table>
			 <table width="100%" cellpadding="4" cellspacing="0">
			 <tr class="text" vAlign="top">
			 <td valign="middle" colspan="10" >
<%
					int  currentPageNo	=	0;
					currentPageNo =	reportenterDob.getPageNo();

					if(currentPageNo != 1)
					{
%>
						 <a href="###" onClick='fetchSortedPageData("<%=sortBy%>","<%=currentPageNo-1%>","pageNo")'><img src="images/Toolbar_backward.gif"  border="0"></a>
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
			    <td colspan="12">&nbsp;</td>
				<INPUT TYPE="hidden" name='sortedOrder' value="<%=sortOrder%>">
				<INPUT TYPE="hidden" name='sortedBy' value="<%=sortBy%>">
				<INPUT TYPE="hidden" name='pageNo' value="<%=pageNo%>">
			    <td colspan="4" align="right"><input type='submit' class='input'  name='Submit' value='Excel'>
		        <input type='button' class='input'  name='b1' value='Cancel' onClick='history.go(-1);'></td>
				<input type='hidden' class='input'  name='nextOperation' value='Submit'></td>
				<input type='hidden' class='input'  name='Operation' value='<%=operation%>'></td>
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
		//Logger.error(FILE_NAME,"Error in QMSQuoteRejectedReport.jsp file ",exp.toString());
    logger.error(FILE_NAME+"Error in QMSQuoteRejectedReport.jsp file "+exp.toString());
			 
		keyValueList       = new ArrayList(3);
		message = new ErrorMessage("Process could not be completed.Please try again","QMSQuoteRejectedReport.jsp");
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