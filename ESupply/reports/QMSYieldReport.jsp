
<%--

Copyright (c) 1999-2001 by FourSoft, Pvt Ltd. All Rights Reserved.
This software is the proprietary information of FourSoft, Pvt Ltd.
Use is subject to license terms.
esupply - v 1.x.
Program	Name	: QMSActivityReport.jsp
Product Name	: QMS
Module Name		: PendigQuotesReports
Date started	: 16 September 2005 
Date Completed	: 
Date modified	:  
Author    		: K.NareshKumarReddy

--%>
<%@page import ="javax.naming.InitialContext,
				 java.util.ArrayList,
				 java.util.HashMap,
				 com.foursoft.esupply.common.java.KeyValue,
				 com.qms.reports.java.ReportsEnterIdDOB,
				 com.qms.reports.java.ReportDetailsDOB,
				 com.qms.reports.ejb.sls.ReportsSession,
                 com.qms.reports.ejb.sls.ReportsSessionBeanHome,
		         com.foursoft.esupply.common.java.ErrorMessage,
				 org.apache.log4j.Logger,
				 com.foursoft.esupply.common.util.ESupplyDateUtility "%>

<jsp:useBean id = "loginbean" class = "com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope = "session"/>

<%!
  private static Logger logger = null;
	private static final String FILE_NAME="QMSYieldReport.jsp";
%>

<%       
     logger  = Logger.getLogger(FILE_NAME);	
		 ErrorMessage errorMessageObject = null;
		 ArrayList	  keyValueList	 	 = new ArrayList();
         ESupplyDateUtility  dateutil=new  ESupplyDateUtility();  
		 String dateFormat	=	loginbean.getUserPreferences().getDateFormat();
		 dateutil.setPattern(dateFormat);
		 String currDate	=	dateutil.getCurrentDateString(dateFormat);

		 ArrayList	  pageIterList	     = null;
 		 ArrayList    dataList		     = null;
		 ArrayList    mainDataList	     = null;
		 ReportDetailsDOB detailsDob     = null;
		 ReportsEnterIdDOB reportenterDob= null;
		 HashMap		mapValue1		 = null;
		 HashMap        mapValue2        = null;  
		 String        disabled          =   "";
		 String	        keyVlue	            =   null;

		 int noofRecords                 =0;
		 int noofPages	                 =0;
         String       operation          =null; 

         int noOfSegments	=	0;

		 try
		 {
		 noOfSegments            =   Integer.parseInt(loginbean.getUserPreferences().getSegmentSize());
		 pageIterList			= new ArrayList();
		 dataList				= new ArrayList(); 
		 mainDataList			= new ArrayList(2); 
		 reportenterDob			= new ReportsEnterIdDOB();
		 mainDataList			= (ArrayList)request.getAttribute("mainDataList");
		// mainDataList			= (ArrayList)session.getAttribute("mainDataList");
         reportenterDob			= (ReportsEnterIdDOB)session.getAttribute("YieldEnterData");
         operation              = request.getParameter("operation");

		 String       sortBy    = request.getParameter("SortBy");
		 String       sortOrder = request.getParameter("SortOrder");
		 String       pageNo    = request.getParameter("PageNo");
		 String       imagePath     = "";
		 if("ASC".equalsIgnoreCase(sortOrder))
		   imagePath = request.getContextPath()+"/images/asc.gif";
		 else
		   imagePath = request.getContextPath()+"/images/desc.gif";
		 
		 mapValue1					=	(HashMap)session.getAttribute("HashList1");
         mapValue2					=	(HashMap)session.getAttribute("HashList2");
         if(mapValue1==null)
		   mapValue1	=	new HashMap();
		 if(mapValue2==null)
		   mapValue2	=	new HashMap();
		

		 if(mainDataList!=null && mainDataList.size()>0)
		 {
				pageIterList          = (ArrayList)mainDataList.get(0);
				dataList		      =	(ArrayList)mainDataList.get(1);
        		Integer  no_ofRecords   =   (Integer)pageIterList.get(0);
				Integer  no_ofPages		=	(Integer)pageIterList.get(1);

				noofRecords				=	no_ofRecords.intValue();
				noofPages				=	no_ofPages.intValue();
		 }
		 if(dataList!=null && dataList.size()>0)
	     {
%>






<html>
  <head>
	<title>Activity Report</title>
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
 
document.forms[0].action='QMSReportController?Operation=pendingquotes&pageNo='+pageNo+'&Operation='+operation+'&subOperation=pendingquotespagging';
document.forms[0].submit();
}
function mainPage()
{
  document.forms[0].action='QMSReportController?Operation=yieldAdd';
  document.forms[0].submit();
}


function forwardQuote(quoteId,index)
{
   
    //document.forms[0].action='QMSQuoteController?Operation=View&subOperation=Report&QuoteId='+quoteId;
    //document.forms[0].submit();

	var Url		=	'QMSQuoteController?Operation=View&subOperation=Report&QuoteId='+quoteId+'&serchButton=noButton&fromWhere=Pending';	
	var Bars = 'toolbar=0,status=1,location=no,menubar=no,directories=no,scrollbars=yes,resizable=yes';
	var Options='';
	var Features=Bars+' '+Options;
	var Win=open(Url,'Doc',Features);
	Win.resizeTo(screen.availWidth,screen.availHeight) 
	Win.focus(); 
	Win.moveTo(0,0);
 
}

function showDetails(input)
 {

		 
		var Url		=	'QMSReportController?Operation=yieldAdd&subOperation=details&QuoteId='+input.id+'&index='+input.ind;	
		var Bars = 'directories=no, location=no,menubar=no,status=yes,titlebar=no,scrollbars=yes';
		var Options='width=800,height=360,resizable=yes';
		var Features=Bars+' '+Options;
		var Win=open(Url,'Doc',Features);
 }
function fetchSortedPageData(lable,pageNo,clickFrom)
{
   var sortBy     = lable;
   var sortOrder  = "";   
   //var Operation  = document.forms[0].Operation.value;       
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
	document.forms[0].action = "QMSReportController?Operation=YieldPageBrowse&SortBy="+sortBy+"&SortOrder="+sortOrder+"&PageNo="+pageNo;
	//alert(document.forms[0].action);
    document.forms[0].submit();
}

</script>
  <body>
	<form method='POST' action='' onSubmit=''>
	   <table width="100%"  cellspacing="0" cellpadding="0">
	    <tr valign="top" bgcolor="#FFFFFF">
		  <td>
			 <table width="100%"  cellspacing="1" cellpadding="4">
			  <tr valign="top" class='formlabel'>
			    <td colspan="3"> <table width="100%"  cellspacing="1" cellpadding="4"><tr class='formlabel'>
			      <td>YieldReport</td>
				  <td align=right>QS1010241</td>
			      </tr></table></td>
			  </tr>
		    </table>
			  
        <table width="100%"  cellspacing="1" cellpadding="4">
          <tr valign="top" class='formdata'>
			    <td >&nbsp;
				</td>
				<td ></td>
				<td colspan="10"></td>
		  </tr>
			  <tr valign="top" class='formdata'>
			    
            <td width="174" >Terminal Id:<br>
              <%=loginbean.getTerminalId()%>
				</td>
				
            <td width="229">User:<br>
             <%=loginbean.getUserId()%>
				</td>
				
            <td width="209"> Date:<br>
             <%=currDate%>
			  </td>
			  <td colspan="8"> <br>
			  </td>
		     <td></td>
			 </tr>
			</table>
				<table width="100%"  cellspacing="1" cellpadding="4">
			  <tr valign="top" class='formheader'>
			    <td><A href='###' onClick='fetchSortedPageData("SalesPerson","<%=pageNo%>","lable")' onmouseover='status = "Sort by Sales Person as";return true;' onmouseout="status = '';return true;" title="Sort by Sales Person as">
				Sales Person<%  if(sortBy.equalsIgnoreCase("SalesPerson")){%>					
				      <img SRC=<%=imagePath%> border="0">
				 <%}%></A></td>
				<td><A href='###' onClick='fetchSortedPageData("QuoteId","<%=pageNo%>","lable")' onmouseover='status = "Sort by Quote Id as";return true;' onmouseout="status = '';return true;" title="Sort by Quote Id as">
				Quote Id<%  if(sortBy.equalsIgnoreCase("QuoteId")){%>					
				      <img SRC=<%=imagePath%> border="0">
				 <%}%></A></td>
			    <td><A href='###' onClick='fetchSortedPageData("CustomerId","<%=pageNo%>","lable")' onmouseover='status = "Sort by Customer Id as";return true;' onmouseout="status = '';return true;" title="Sort by Customer Id as">
				Customer Id<%  if(sortBy.equalsIgnoreCase("CustomerId")){%>					
				      <img SRC=<%=imagePath%> border="0">
				 <%}%></A></td>
				 <!--@@Modified by Kameswari for the WPBN isssue-38175-->
				  <!--@@Added by kameswari for the WPBN issue-30313-->
				 <td NOWRAP><A href='###' onClick='fetchSortedPageData("CustomerName","<%=pageNo%>","lable")' onmouseover='status = "Sort by Customer Name as";return true;' onmouseout="status = '';return true;" title="Sort by Customer Name as">Customer Name<%  if(sortBy.equalsIgnoreCase("CustomerName")){%>					
				      <img SRC=<%=imagePath%> border="0">
				 <%}%></A></td>
				 <!--@@WPBN 30313-->
			    <td><A href='###' onClick='fetchSortedPageData("QuoteStatus","<%=pageNo%>","lable")' onmouseover='status = "Sort by Quote Status as";return true;' onmouseout="status = '';return true;" title="Sort by Quote Status as">
				Quote Status<%  if(sortBy.equalsIgnoreCase("QuoteStatus")){%>					
				      <img SRC=<%=imagePath%> border="0">
				 <%}%></A></td>
				<td><A href='###' onClick='fetchSortedPageData("Mode","<%=pageNo%>","lable")' onmouseover='status = "Sort by  Mode as";return true;' onmouseout="status = '';return true;" title="Sort by  Mode as">
				Shipment Mode<%  if(sortBy.equalsIgnoreCase("Mode")){%>					
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
				<td ><A href='###' onClick='fetchSortedPageData("AverageYield","<%=pageNo%>","lable")' onmouseover='status = "Sort by To Average Yield";return true;' onmouseout="status = '';return true;" title="Sort by Average Yield as">
				Average Yield<%  if(sortBy.equalsIgnoreCase("AverageYield")){%>					
				      <img SRC=<%=imagePath%> border="0">
				 <%}%></A></td>
			    <td NOWRAP>YieldDetails</td>
				<td></td>
			  </tr>

<%				for(int i=0;i<dataList.size();i++)
				{
					detailsDob=(ReportDetailsDOB)dataList.get(i);
                    
%>

			  <tr valign="top" class='formdata'>
				
		
			
            <td NOWRAP>
			<%=detailsDob.getSalesPerson()==null?"-":detailsDob.getSalesPerson()%>
			  </td>
           			
            <td  NOWRAP> 
			<!-- Added by Rakesh on 25-02-2011 for Issue:236363  -->
			<!--  <a href="javascript:forwardQuote('<%=detailsDob.getQuoteId()%>','<%=i%>');" ><%=detailsDob.getQuoteId()==null?"-":detailsDob.getQuoteId()%></a> -->
			<a href="QMSQuoteController?Operation=View&subOperation=Report&QuoteId=+<%=detailsDob.getQuoteId()%>+&serchButton=noButton&fromWhere=Pending" > <%=detailsDob.getQuoteId()==null?"-":detailsDob.getQuoteId()%></a>
			<!-- Ended by Rakesh on 25-02-2011 for Issue:236363  -->
				</td>
            <td NOWRAP><%=detailsDob.getCustomerId()==null?"-":detailsDob.getCustomerId()%></td>
			<td NOWRAP><%=detailsDob.getCustomerName()==null?"-":detailsDob.getCustomerName()%></td><!-- added by kameswari for the WPBN issue-30313-->
<%
			String status="";
			if(detailsDob.getQuoteStatus()!=null)
			{
				if("QUE".equalsIgnoreCase(detailsDob.getQuoteStatus()))
				{
					status	=	"Queued";
				}
				else if("GEN".equalsIgnoreCase(detailsDob.getQuoteStatus()))
				{
					status	=	"Generated";
				}
				else if("ACC".equalsIgnoreCase(detailsDob.getQuoteStatus()))
				{
					status	=	"Positive";
				}
				else if("NAC".equalsIgnoreCase(detailsDob.getQuoteStatus()))
				{
					status	=	"Negative";
				}
				else if("PEN".equalsIgnoreCase(detailsDob.getQuoteStatus()))
				{
					status	=	"Pending";
				}
			}
%>

			  <td NOWRAP><%=status%></td>
			  <td nowrap ><%=detailsDob.getShipmentMode()==null?"-":("100".equals(detailsDob.getShipmentMode()) ?"Multi-Modal" :detailsDob.getShipmentMode() )%></td>
			  <td NOWRAP><%=detailsDob.getServiceLevel()==null?"-":detailsDob.getServiceLevel()%></td>
			  <td NOWRAP><%=detailsDob.getFromCountry()==null?"-":detailsDob.getFromCountry()%></td>
			  <td NOWRAP><%=detailsDob.getFromLocation()==null?"-":detailsDob.getFromLocation()%></td>
			  <td NOWRAP><%=detailsDob.getToCountry()==null?"-":detailsDob.getToCountry()%></td>
			  <td NOWRAP><%=detailsDob.getToLocation()==null?"-":detailsDob.getToLocation()%></td>
			 <td NOWRAP><%=detailsDob.getAverageYield()==null?"-":detailsDob.getAverageYield()%></td>
			  <td NOWRAP><input type='button' class='input'  name='update' value='Details' ind='<%=i%>' id='<%=detailsDob.getQuoteId()%>' onClick="showDetails(this)"></td>
			  <td valign="middle" colspan="8" >
             
           </tr>
<%
				}
%>
			   
			</table>
            <table width="100%"  cellspacing="1" cellpadding="4">
			<tr>
			<TD>
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
			<tr valign="top" class='denotes'>
			 <td colspan="7"><font color="#ff0000">*</font> Denotes Mandatory </td>
			 <INPUT TYPE="hidden" name='sortedOrder' value="<%=sortOrder%>">
			 <INPUT TYPE="hidden" name='sortedBy' value="<%=sortBy%>">
			 <INPUT TYPE="hidden" name='pageNo' value="<%=pageNo%>">
			 <td colspan="6" align="right"><input type='button' class='input'  name='b1' value='Continue' onclick='mainPage()'></td>
			</tr>
			</table>
         </td>
	    </tr>
	  </table>
	  
	</form>
  </body>
</html>
<%      }
		else
		{
			errorMessageObject = new ErrorMessage(  "No records found","ESMenuRightPanel.jsp?link=es-et-Administration-Reports"); 
            keyValueList       = new ArrayList(3);
			keyValueList.add(new KeyValue("ErrorCode","RNF")); 	
			keyValueList.add(new KeyValue("Operation","Add")); 	
			errorMessageObject.setKeyValueList(keyValueList);
			request.setAttribute("ErrorMessage",errorMessageObject);
			%>
			<jsp:forward page="../QMSESupplyErrorPage.jsp"/>
			<%
		 }
		}
		catch(Exception e)
		{
			e.printStackTrace();
			//Logger.error(FILE_NAME,"Error in QMSYieldReport.jsp "+e);
      logger.error(FILE_NAME+"Error in QMSYieldReport.jsp "+e);
			keyValueList       = new ArrayList(3);
			errorMessageObject = new ErrorMessage(  "Error while retreiving the details","QMSReportController?Operation=buyratesExpiryReport"); 
			keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
			keyValueList.add(new KeyValue("Operation","Add")); 	
			errorMessageObject.setKeyValueList(keyValueList);
			request.setAttribute("ErrorMessage",errorMessageObject);
%>
			<jsp:forward page="ESupplyErrorPage.jsp" />
<%
		}
%>