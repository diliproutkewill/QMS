
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
				  java.util.Set,
				  java.util.Iterator,
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
	private static final String FILE_NAME="QMSActivityReport.jsp";
%>

<%       
		 logger  = Logger.getLogger(FILE_NAME);	
		 ErrorMessage errorMessageObject = null;
		 ArrayList	  keyValueList	 	 = new ArrayList();
         ESupplyDateUtility  dateutil=new  ESupplyDateUtility();  
		 String dateFormat	=	loginbean.getUserPreferences().getDateFormat();
		 dateutil.setPattern(dateFormat);
		 String currDate=dateutil.getCurrentDateString(dateFormat);

		 ArrayList	  pageIterList	     = null;
 		 ArrayList    dataList		     = null;
		 ArrayList    mainDataList	     = null;
		 ReportDetailsDOB detailsDob     = null;
		 ReportsEnterIdDOB reportenterDob= null;
		 HashMap		mapValue1		 = null;
		 HashMap        mapValue2        = null;  
		 String        disabled          = "";
		 String	        keyVlue	         = null;

		 int noofRecords                 = 0;
		 int noofPages	                 = 0;
         String       operation          = null; 
         String       sortBy			 = null;
		 String       sortOrder			 = null;
		 String       pageNo			 = null;
     String       imagePath          = "";
		 String		  empty				 = ""; //Added by Rakesh on 25-02-2011 for Issue:236363 
	    //	Added By Kishore For The Quote StatusReason
		 HashMap<Integer, String> 		statusReasonMap =  null;
		 try
		 {
		 pageIterList			= new ArrayList();
		 dataList				= new ArrayList(); 
		 mainDataList			= new ArrayList(3);    //	Modified By Kishore For The Quote StatusReason
		 reportenterDob			= new ReportsEnterIdDOB();
		 mainDataList			= (ArrayList)request.getAttribute("mainDataList");
         reportenterDob			= (ReportsEnterIdDOB)session.getAttribute("ActivityEnterData");
         operation              = request.getParameter("Operation");
		 sortBy					= request.getParameter("SortBy");
		 sortOrder				= request.getParameter("SortOrder");
		 pageNo					= request.getParameter("PageNo");
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
		 int noOfSegments	=	0;

		 noOfSegments            =   Integer.parseInt(loginbean.getUserPreferences().getSegmentSize());
		
     
		 if(mainDataList!=null && mainDataList.size()>0)
		 {
				pageIterList            =   (ArrayList)mainDataList.get(0);
				dataList		        =	(ArrayList)mainDataList.get(1);
        		Integer  no_ofRecords   =   (Integer)pageIterList.get(0);
				Integer  no_ofPages		=	(Integer)pageIterList.get(1);
				statusReasonMap = 	(HashMap<Integer,String>)mainDataList.get(2);  //	Added By Kishore For The Quote StatusReason

				noofRecords				=	no_ofRecords.intValue();
				noofPages				=	no_ofPages.intValue();			  
		 
				        
		 }
if(dataList!=null && dataList.size()>0)
{%>

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
  document.forms[0].action='QMSReportController?Operation=activityAdd';
  document.forms[0].submit();
}


function forwardQuote(quoteId,index)
{
   
    //document.forms[0].action='QMSQuoteController?Operation=Modify&subOperation=EnterId&QuoteId='+quoteId;
    //document.forms[0].submit();
	//Added by Anil.k to display Multiquote View page
	 var len = document.forms[0].isMultiQuote.length;
	 if(len>1)
		 var isMultiQuote = document.forms[0].isMultiQuote[index].value;
	 else
		 var isMultiQuote = document.forms[0].isMultiQuote.value;
	 //alert(isMultiQuote);
	 if(isMultiQuote == "Y")
	 {
		 document.forms[0].action='QMSMultiQuoteController?Operation=View&subOperation=Report&QuoteId='+quoteId+'&fromWhere=Pending'+'&originLoc='+""+'&destLoc='+"";
		 document.forms[0].submit();
	 }
	 else
	 {//Ended by Anil.k
	 Features = 'directories=no,location=no,menubar=no,status=yes,titlebar=no,scrollbars=yes,resizable=yes';
	 Url='QMSQuoteController?Operation=View&subOperation=Report&QuoteId='+quoteId+'&fromWhere=Pending';
	 Win=open(Url,'Doc',Features); 
	 Win.resizeTo(screen.availWidth,screen.availHeight) 
	 Win.focus(); 
	 Win.moveTo(0,0);
	 }
 
}

function checkValues()
{
   var checkBox=document.getElementsByName("actChk");
   var checkBoxValue=document.getElementsByName("checkValue");
   var actInactValue=document.getElementsByName("actInact");
   //alert(checkBox.length);
   for(m=0;m<checkBox.length;m++)
   {
     if(checkBox[m].checked)
		checkBoxValue[m].value=actInactValue[m].value;
	//alert(document.forms[0].checkValue[m].value);
   }
}

function fetchSortedPageData(lable,pageNo,clickFrom)
{
   checkValues();

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
	document.forms[0].chkUpdate.value	=	"no";//shyam
	//alert(document.forms[0].chkUpdate.value);
	document.forms[0].action = "QMSReportController?Operation=ActivityPageBrowse&SortBy="+sortBy+"&SortOrder="+sortOrder+"&PageNo="+pageNo;
	//alert(document.forms[0].action);
    document.forms[0].submit();
}

//shyam
function checkAndSubmit()
{
	checkValues();
	document.forms[0].chkUpdate.value	=	"yes";//shyam
	
	document.forms[0].action = "QMSReportController?Operation=ActivityPageBrowse&SortBy="+document.forms[0].sortedBy.value+"&SortOrder="+document.forms[0].sortedOrder.value+"&PageNo="+document.forms[0].pageNo.value;
    document.forms[0].submit();
}//shyam

</script>
  <body>
	<form method='POST' action='' >
	  <table width="100%" border="0" cellspacing="0" cellpadding="0">
	    <tr valign="top" bgcolor="#FFFFFF">
		  <td>
			<table width="100%" cellpadding="4" cellspacing="1">
			  <tr valign="top" class='formlabel'>
			    <td colspan="3"><table width="100%" border="0" ><tr class='formlabel'>
			      <td>Activity Report </td>
				  <td align=right>QS1010241</td>
			      </tr></table></td>
			  </tr>
		    </table>
			  
        <table width="100%"  cellspacing="1" cellpadding="4">
          <tr valign="top" class='formdata'>
			    <td colspan="4">&nbsp;
				</td>
				<td colspan="4"></td>
		  </tr>
			  <tr valign="top" class='formdata'>
			    
            <td>Terminal Id:<br>
              <%=loginbean.getTerminalId()%>
				</td>
				
            <td>User:<br>
             <%=loginbean.getUserId()%>
				</td>
				
            <td> Date:<br>
             <%=currDate%>
			  </td>
			  <td colspan="4"> <br>
			  </td>
		     <td></td>
			 </tr>
			</table>
				<table width="100%"  cellspacing="1" cellpadding="4">
			  <tr valign="top" class='formheader'>
			    <td NOWRAP>Modify</td><!-- shyam -->
				<!--@@Modified by Kameswari for the WPBN issue-38175-->
			    <td>Active/<br>Inactive</td><!-- shyam -->
<!-- @@ Added by subrahmanyam for wpbn issue: 173831 -->
				<td><A href='###' onClick='fetchSortedPageData("CreatedBy","<%=pageNo%>","lable")' onmouseover='status = "Sort by Created By ";return true;' onmouseout="status = '';return true;" title="Sort by Created By ">
				Created By<%  if(sortBy.equalsIgnoreCase("CreatedBy")){%>					
				      <img SRC=<%=imagePath%> border="0">
				 <%}%></A></td>
<!-- @@ Ended by subrahmanyam for the 173831 -->
				<td><A href='###' onClick='fetchSortedPageData("SalesPerson","<%=pageNo%>","lable")' onmouseover='status = "Sort by Sales Person ";return true;' onmouseout="status = '';return true;" title="Sort by Sales Person ">
				Sales Person<%  if(sortBy.equalsIgnoreCase("SalesPerson")){%>					
				      <img SRC=<%=imagePath%> border="0">
				 <%}%></A></td>
				<td><A href='###' onClick='fetchSortedPageData("QuoteDate","<%=pageNo%>","lable")' onmouseover='status = "Sort by Quote Date ";return true;' onmouseout="status = '';return true;" title="Sort by Quote Date ">
				Created Date<%  if(sortBy.equalsIgnoreCase("QuoteDate")){%>		<!-- Added by Rakesh on 25-02-2011 for Issue:236363 --> 			
				      <img SRC=<%=imagePath%> border="0">
				 <%}%></A></td>
				<td><A href='###' onClick='fetchSortedPageData("QuoteId","<%=pageNo%>","lable")' onmouseover='status = "Sort by Quote Id ";return true;' onmouseout="status = '';return true;" title="Sort by Quote Id ">
				Quote Id<%  if(sortBy.equalsIgnoreCase("QuoteId")){%>					
				      <img SRC=<%=imagePath%> border="0">
				 <%}%></A></td>
			    <td><A href='###' onClick='fetchSortedPageData("CustomerId","<%=pageNo%>","lable")' onmouseover='status = "Sort by Customer Id ";return true;' onmouseout="status = '';return true;" title="Sort by Customer Id ">
				Customer Id<%  if(sortBy.equalsIgnoreCase("CustomerId")){%>					
				      <img SRC=<%=imagePath%> border="0">
				 <%}%></A></td>
				 <!--@@Added by kameswari for the WPBN issue-30313-->
				 <td NOWRAP><A href='###' onClick='fetchSortedPageData("CustomerName","<%=pageNo%>","lable")' onmouseover='status = "Sort by Customer Name as";return true;' onmouseout="status = '';return true;" title="Sort by Customer Name as">Customer Name<%  if(sortBy.equalsIgnoreCase("CustomerName")){%>					
				      <img SRC=<%=imagePath%> border="0">
				 <%}%></A></td>
				 <!--@@WPBN 30313-->
			    <td><A href='###' onClick='fetchSortedPageData("QuoteStatus","<%=pageNo%>","lable")' onmouseover='status = "Sort by Quote Status ";return true;' onmouseout="status = '';return true;" title="Sort by Quote Status ">
				Quote Status<%  if(sortBy.equalsIgnoreCase("QuoteStatus")){%>					
				      <img SRC=<%=imagePath%> border="0">
				 <%}%></A></td>
				<td><A href='###' onClick='fetchSortedPageData("Mode","<%=pageNo%>","lable")' onmouseover='status = "Sort by  Mode ";return true;' onmouseout="status = '';return true;" title="Sort by  Mode ">
				Shipment Mode<%  if(sortBy.equalsIgnoreCase("Mode")){%>					
				      <img SRC=<%=imagePath%> border="0">
				 <%}%></A></td>
				<td><A href='###' onClick='fetchSortedPageData("ServiceLevel","<%=pageNo%>","lable")' onmouseover='status = "Sort by Service Level ";return true;' onmouseout="status = '';return true;" title="Sort by Service Level ">
				Service Level<%  if(sortBy.equalsIgnoreCase("ServiceLevel")){%>					
				      <img SRC=<%=imagePath%> border="0">
				 <%}%></A></td>
			    <td><A href='###' onClick='fetchSortedPageData("FromCountry","<%=pageNo%>","lable")' onmouseover='status = "Sort by From Country ";return true;' onmouseout="status = '';return true;" title="Sort by From Country ">
				From Country<%  if(sortBy.equalsIgnoreCase("FromCountry")){%>					
				      <img SRC=<%=imagePath%> border="0">
				 <%}%></A></td>
			    <td><A href='###' onClick='fetchSortedPageData("FromLocation","<%=pageNo%>","lable")' onmouseover='status = "Sort by From Location ";return true;' onmouseout="status = '';return true;" title="Sort by From Location ">
				From Location<%  if(sortBy.equalsIgnoreCase("FromLocation")){%>					
				      <img SRC=<%=imagePath%> border="0">
				 <%}%></A></td>
			    <td><A href='###' onClick='fetchSortedPageData("ToCountry","<%=pageNo%>","lable")' onmouseover='status = "Sort by To Country ";return true;' onmouseout="status = '';return true;" title="Sort by To Country ">
				To Country<%  if(sortBy.equalsIgnoreCase("ToCountry")){%>					
				      <img SRC=<%=imagePath%> border="0">
				 <%}%></A></td>
			    <td><A href='###' onClick='fetchSortedPageData("ToLocation","<%=pageNo%>","lable")' onmouseover='status = "Sort by To Location ";return true;' onmouseout="status = '';return true;" title="Sort by To Location ">
				To Location<%  if(sortBy.equalsIgnoreCase("ToLocation")){%>					
				      <img SRC=<%=imagePath%> border="0">
				 <%}%></A></td>				
				  <!--  Added by Kishore For statusReason 
				  if("H".equals(loginbean.getUserTerminalType())) -->
				 <td> Status Reason </td> 
			  </tr>
                <!--@@WPBN issue-38175-->
<%				String checked	=	"";//shyam
				String actFlag	=	"";//shyam
				//String selected	=	"";//shyam
				HashMap checkList = (HashMap)session.getAttribute("checkList");//shyam
				int index	=	0;//Added by Anil.k for multiquote values
				String quoteId	=	"";//Added by Anil.k for multiquote values
				for(int i=0;i<dataList.size();i++)
				{
					detailsDob=(ReportDetailsDOB)dataList.get(i);
                    
%>

		  <tr valign="top" class='formdata'>
<%		  if(detailsDob.getQuoteId()!=null && !quoteId.equals(detailsDob.getQuoteId()))//Added by Anil.k for multiquote values
		  {
		  if("NAC".equalsIgnoreCase(detailsDob.getQuoteStatus()) || "ACC".equalsIgnoreCase(detailsDob.getQuoteStatus()))
			{
				if(checkList!=null)
				{
					if(checkList.containsKey(detailsDob.getQuoteId()))
						checked	=	"checked";
					else
						checked	=	"";
					
					actFlag	=	(String)checkList.get(detailsDob.getQuoteId());
					if(actFlag==null)
						actFlag	=	"";
				}
				//System.out.println("actFlag---"+actFlag);

%>
					 <td NOWRAP> 
						<input type="checkbox" name="actChk" value="" <%=checked%>>
						<input type="hidden" name="checkValue" value="<%=detailsDob.getQuoteId()%>">
						<input type="hidden" name="quoteIdChked" value="<%=detailsDob.getQuoteId()%>">
						
					 </td><!-- shyam -->
<%				
				if(detailsDob.getActiveFlag().equalsIgnoreCase("A") || actFlag.equalsIgnoreCase("A"))
				  {
						if("ACC".equalsIgnoreCase(detailsDob.getQuoteStatus()))
						{
							if(actFlag.equalsIgnoreCase("I") )
							 {
%>					  
							  <td NOWRAP> 
								  <select name="actInact" class='select'>
									  <option value="A"></option>
									  <option value="I" selected>Inactive</option>
								  </select>
							  </td>
<%							}
							else 
							{
%>					  
							  <td NOWRAP> 
								  <select name="actInact" class='select'>
									  <option value="A" selected></option>
									  <option value="I" >Inactive</option>
								  </select>
							  </td>
<%							}
						}
						else
					    {
							if(actFlag.equalsIgnoreCase("I") )
							 {
%>					  
							  <td NOWRAP> 
								  <select name="actInact" class='select'>
									  <option value="A" >Active</option>
									  <option value="I" selected>Inactive</option>
								  </select>
							  </td><!-- shyam -->
<%							}
							else 
							{
%>					  
							  <td NOWRAP> 
								  <select name="actInact" class='select'>
									  <option value="A" selected>Active</option>
									  <option value="I" >Inactive</option>
								  </select>
							  </td><!-- shyam -->
<%							}
						}
				  }
				  else
				  {
					 if("ACC".equalsIgnoreCase(detailsDob.getQuoteStatus()))
					 {
%>						<td NOWRAP> 
						  <select name="actInact" class='select'>
							  <option value="A" ></option>
							  <option value="I" selected>Inactive</option>
						  </select>
						</td><!-- shyam -->
<%					}
					else
					{
%>						<td NOWRAP> 
						  <select name="actInact" class='select'>
							  <option value="A" >Active</option>
							  <option value="I" selected>Inactive</option>
						  </select>
						</td>
<%					}
				  }
			  }
			  else
			  {
%>	
				<td NOWRAP>&nbsp;</td>
				<td NOWRAP>&nbsp;</td>
<%
			  }
%>
	<!--@@Added by subrahmanyam for the wpbn id: 173831  -->
			<td NOWRAP>
			<%=detailsDob.getCreatedBy()==null?"-":detailsDob.getCreatedBy()%>
			  </td>
<!-- @@Ended by subrahmanyam for the wpbn id: 173831 -->
			<td NOWRAP>
			<%=detailsDob.getSalesPerson()==null?"-":detailsDob.getSalesPerson()%>
			  </td>
<%
			String str[]   = null;
			if(detailsDob.getQuoteDateTstmp()!=null)			
			{
			 
			  str = dateutil.getDisplayStringArray(detailsDob.getQuoteDateTstmp());
			  
			}
%>
            <td nowrap>
			<%=detailsDob.getQuoteDateTstmp()==null?"-":str[0]%>
			  </td> 
				
            <td NOWRAP> 
			  <!-- Commented and Added by Rakesh on 25-02-2011 for Issue:236363  -->
			  <!-- <a href="javascript:forwardQuote('<%=detailsDob.getQuoteId()%>','<%=index%>');" ><%=detailsDob.getQuoteId()==null?"-":detailsDob.getQuoteId()%></a> -->
			  <% if("Y".equalsIgnoreCase(detailsDob.getIsMultiQuote())) {%> 
			 <a href="QMSMultiQuoteController?Operation=View&subOperation=Report&QuoteId=+<%=detailsDob.getQuoteId()%>+&fromWhere=Pending&originLoc=+<%=empty%>+&destLoc=+<%=empty%>"  ><%=detailsDob.getQuoteId()==null?"-":detailsDob.getQuoteId()%></a>
			  <%}else{
				%>
			  <a href="QMSQuoteController?Operation=View&subOperation=Report&QuoteId=+<%=detailsDob.getQuoteId()%>+&fromWhere=Pending"  ><%=detailsDob.getQuoteId()==null?"-":detailsDob.getQuoteId()%></a>
			  <%}%>
			  <!-- Ended by Rakesh on 25-02-2011 for Issue:236363  -->
				</td>
			  <td NOWRAP><%=detailsDob.getCustomerId()==null?"-":detailsDob.getCustomerId()%></td>
			  <td><%=detailsDob.getCustomerName()==null?"-":detailsDob.getCustomerName()%></td><!--added by kameswari for the WPBN issue-30313-->
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
			  <td NOWRAP><%=detailsDob.getShipmentMode()==null?"-":detailsDob.getShipmentMode()%></td>
			  <input type="hidden" name="isMultiQuote" id='<%=index%>' value="<%=detailsDob.getIsMultiQuote()%>"><!-- Added by Anil.k for multiquote values quoteId = detailsDob.getQuoteId();
			index++; -->
			  <% 	 } else {%>
			    <td NOWRAP>&nbsp;</td>
			    <td NOWRAP>&nbsp;</td>
			    <td NOWRAP>&nbsp;</td>
				<td NOWRAP>&nbsp;</td>
				<td NOWRAP>&nbsp;</td>
				<td NOWRAP>&nbsp;</td>
				<td NOWRAP>&nbsp;</td>
				<td NOWRAP>&nbsp;</td>
				<td NOWRAP>&nbsp;</td>
				<td NOWRAP>&nbsp;</td>
				<% } %><!-- Ended by Anil.k for multiquote values -->
			  <td NOWRAP><%=detailsDob.getServiceLevel()==null?"-":detailsDob.getServiceLevel()%></td>
			  <td NOWRAP><%=detailsDob.getFromCountry()==null?"-":detailsDob.getFromCountry()%></td>
			  <td NOWRAP><%=detailsDob.getFromLocation()==null?"-":detailsDob.getFromLocation()%></td>
			  <td NOWRAP><%=detailsDob.getToCountry()==null?"-":detailsDob.getToCountry()%></td>
			  <td NOWRAP><%=detailsDob.getToLocation()==null?"-":detailsDob.getToLocation()%></td>			  

				 <!--  Added by Kishore For statusReason
		 	
			 	 if("H".equals(loginbean.getUserTerminalType())){ -->
				<%	// System.out.println(quoteId+"------ 1 ---------"+detailsDob.getQuoteId());
					if(detailsDob.getQuoteId()!=null && !quoteId.equals(detailsDob.getQuoteId())) { 
				//	System.out.println("------ 2 ---------");
			%>
							<td NOWRAP>
							<select class='select' valign='bottom' name='statusReason'  style="width:118px;margin:0px 0 5px 0;">
								<option value=''></option>
							<%
								int quoteStatusReason =    detailsDob.getStatusReason()!=null? Integer.parseInt(detailsDob.getStatusReason()):0;
								
								 Set statusReasonIds  = statusReasonMap.keySet();
								Iterator It = statusReasonIds.iterator();
							
								while (It.hasNext()) {
									Integer id = (Integer) (It.next());
									String statusReason	= statusReasonMap.get(id);
									//System.out.println("StatusReason:"+id+"------------------"+statusReason+"    Quote:"+detailsDob.getQuoteId()+"     StautsReason:"+detailsDob.getStatusReason());
								%>
									<option value= '<%=id%>'   <%= id == quoteStatusReason ?"selected":""%>><%=statusReason%> </option>
						  <%}%>
						</select>
						  </td>
						<% quoteId = detailsDob.getQuoteId();
								index++;	
					} else{%>
						<td/>
						<%}
				%>
			<!-- }--  End Of  Kishore For statusReason -->


           </tr>
<%
				}
%>
			   
			</table>
            <table width="100%"  cellspacing="1" cellpadding="4">
			<tr valign="top" class='formdata'>			  
			 <td colspan="10">
			 <%         int  currentPageNo =Integer.parseInt(request.getParameter("PageNo"));
			//System.out.println("currentPageNocurrentPageNo : "+request.getParameter("PageNo"));
				 if(currentPageNo != 1)
			      {%>
			     <A href='###' onClick='fetchSortedPageData("<%=request.getParameter("SortBy")%>","<%=currentPageNo-1%>","pageNo")'><img src="images/Toolbar_backward.gif"  border="0"></a>
<%			  } 
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
			 <td colspan="5"><font color="#ff0000">*</font> Denotes Mandatory </td>
			 <INPUT TYPE="hidden" name='Operation' value="<%=operation%>">
			 <INPUT TYPE="hidden" name='sortedOrder' value="<%=sortOrder%>">
			 <INPUT TYPE="hidden" name='sortedBy' value="<%=sortBy%>">
			 <INPUT TYPE="hidden" name='pageNo' value="<%=pageNo%>">
			 <input type="hidden" name="chkUpdate" value="">
<!-- 			 <td colspan="4" align="right"><input type='submit' class='input'  name='b1' value='Continue'></td>
 -->
			<td colspan="4" align="right"><input type='button' class='input'  name='Submit' value='Update' onClick="checkAndSubmit();"></td>	
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
			keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
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
			//Logger.error(FILE_NAME,"Error in QMSActivityReport.jsp "+e);
      logger.error(FILE_NAME+"Error in QMSActivityReport.jsp "+e);
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