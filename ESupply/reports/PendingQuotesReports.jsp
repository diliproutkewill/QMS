<%--

Copyright (c) 1999-2001 by FourSoft, Pvt Ltd. All Rights Reserved.
This software is the proprietary information of FourSoft, Pvt Ltd.
Use is subject to license terms.
esupply - v 1.x.
Program	Name	:PendingQuotesReports.jsp
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
	private static final String FILE_NAME="PendingQuotesReports.jsp";
%>

<%       
		 logger  = Logger.getLogger(FILE_NAME);	
		 ErrorMessage errorMessageObject = null;
		 ArrayList	  keyValueList	 	 = new ArrayList();

		 ESupplyDateUtility  dateutil=new  ESupplyDateUtility();

		 String datFormat=loginbean.getUserPreferences().getDateFormat();

		 dateutil.setPattern(datFormat);
		   //	Added By Kishore For The Quote StatusReason
		 HashMap<Integer, String> 		statusReasonMap =  null;
		 ArrayList	  pageIterList	     = null;
 		 ArrayList    dataList		     = null;
		 ArrayList    mainDataList	     = null;
		 ReportDetailsDOB detailsDob     = null;
		 ReportsEnterIdDOB reportenterDob= null;
		 HashMap		mapValue1		 = null;
		 HashMap        mapValue2        = null;  
		 String        disabled          =   "";
		 String	        keyVlue	            =   null;

		 int noofRecords                 = 0;
		 int noofPages	                 = 0;
         String       operation          = null; 
		 String		  primaryOption		 = null;
		 String       sortBy             = null;
		 String       sortOrder          = null;
		 String       pageNo             = null;
		 String       repFormat          = null;
		 String       imagePath          = "";
         String		  empty				 = "";//Added by Rakesh on 25-02-2011 for Issue:236363 
		
		 int		  noOfSegments       =	0;
		 try
		 {
		 pageIterList			= new ArrayList();
		 dataList				= new ArrayList(); 
		 mainDataList			= new ArrayList(3);  //	Modified By Kishore For The Quote StatusReason
		 reportenterDob			= new ReportsEnterIdDOB();
		 mainDataList			= (ArrayList)request.getAttribute("mainDataList");
         reportenterDob			= (ReportsEnterIdDOB)session.getAttribute("reportenterDob");

		 noOfSegments            =   Integer.parseInt(loginbean.getUserPreferences().getSegmentSize());

         operation              = request.getParameter("Operation");
		 String       fromSummary        =request.getParameter("fromSummary");//subrahmanyam
		 sortBy                 = request.getParameter("SortBy");
		 sortOrder              = request.getParameter("SortOrder");
		 pageNo                 = request.getParameter("PageNo");
		 repFormat              =   request.getParameter("format");
     
		 if("ASC".equalsIgnoreCase(sortOrder))
		   imagePath = request.getContextPath()+"/images/asc.gif";
		 else
		   imagePath = request.getContextPath()+"/images/desc.gif";
       
		 mapValue1				= (HashMap)session.getAttribute("HashList1");
         mapValue2				= (HashMap)session.getAttribute("HashList2");
         if(mapValue1==null)
		   mapValue1	=	new HashMap();
		 if(mapValue2==null)
		   mapValue2	=	new HashMap();
		
		 primaryOption	=	request.getParameter("wiseoptions");
		 
		 if("W".equalsIgnoreCase(primaryOption))
			primaryOption	=	"Week Wise";
		 else if("C".equalsIgnoreCase(primaryOption))
			 primaryOption	=	"Customer Wise";
		 else if("L".equalsIgnoreCase(primaryOption))
			 primaryOption	=	"Lane Wise";
		 else if("SP".equalsIgnoreCase(primaryOption))
			 primaryOption	=	"Sales Person Wise";
		 else if("servicelevelwise".equalsIgnoreCase(primaryOption))
			 primaryOption	=	"Service Level Wise";

		 if(mainDataList!=null && mainDataList.size()>0)
		 {
				pageIterList          = (ArrayList)mainDataList.get(0);
				dataList		      =	(ArrayList)mainDataList.get(1);
        		Integer  no_ofRecords = (Integer)pageIterList.get(0);
				Integer  no_ofPages	  =	(Integer)pageIterList.get(1);
				noofRecords			  =	 no_ofRecords.intValue();
				noofPages			  =	 no_ofPages.intValue();	

				statusReasonMap = 	(HashMap<Integer,String>)mainDataList.get(2);		 //	Added By Kishore For The Quote StatusReason

		 session.setAttribute("reportenterDob",reportenterDob);
		 session.setAttribute("dataList",dataList);         
		 }
		 if(dataList!=null && dataList.size()>0)
	     {
%>
<html>
  <head>
	<title>Customer Contracts - Add</title>
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
function mainPage1()
{
  document.forms[0].action='QMSReportController?Operation=pendingquotes&subOperation=pendingUpdate&nextOperation=Update';
  document.forms[0].submit();
  return false;
}
//@@ Added by kiran.v on 17/11/2011 for Wpbn Issue -280271
  function mainPage()
{	  
	  var flag=false;
	  var  mfValues1=document.getElementsByName("mfValues1");
	    for(var i=0;i<mfValues1.length;i++)
        {
		    if( document.getElementById("mfValues1"+i).checked || document.getElementById("mfValues2"+i).checked  )	 {
				if(document.getElementById("statusReason"+(i+1)).value==null || document.getElementById("statusReason"+(i+1)).value=='') {
				alert("Please Select the Status Reason for selected Quote");
				  document.getElementById("statusReason"+(i+1)).focus();
				  return false;
				}
				flag=true;	  
			}
		}
	     if(!flag){
		 alert("Please Select Atleast One Quote Id ");
		 return false;
		 }else{
			 mainPage1();
		 return true;
		 }
}
//@@Ended by kiran.v
function checkMarked(obj,id)
{
var id1=id.substring(9); // @@ Modified  by kiran.v on 17/11/2011 for Wpbn Issue -280271
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
  if(objvalue=='1' &&document.forms[0].mfValues1[id1].checked==true )
	  document.forms[0].mfValues2[id1].checked=false;
  else if(objvalue=='2' &&document.forms[0].mfValues2[id1].checked==true )
	  document.forms[0].mfValues1[id1].checked=false;
	}
}

function forwardQuote(quoteId,index)
{
   var len='<%=dataList.size()%>';
   var isMultiQuote = document.forms[0].isMultiQuote[index].value;//Added by Anil.k
   if(len=='1')
   {
     var positive=document.forms[0].mfValues1.checked;
     var negative=document.forms[0].mfValues2.checked;
	 if((!positive) && (!negative))
	 {    
		  if(isMultiQuote == "Y")//Added by Anil.k
			 document.forms[0].action='QMSMultiQuoteController?Operation=Modify&subOperation=EnterId&QuoteId='+quoteId+'&originLoc='+""+'&destLoc='+"";
		  else//End
	      document.forms[0].action='QMSQuoteController?Operation=Modify&subOperation=EnterId&QuoteId='+quoteId;
          document.forms[0].submit();
         
	 }
     else  
	 {
		
	     Bars = 'directories=no,location=no,menubar=no,status=no,titlebar=no,scrollbars=yes';
	     Options='width=800,height=600,resizable=yes';
		 if(isMultiQuote == "Y")//Added by Anil.k
			 Url='QMSMultiQuoteController?Operation=View&subOperation=Report&QuoteId='+quoteId+'&originLoc='+""+'&destLoc='+"";
		 else//End
		 Url='QMSQuoteController?Operation=View&subOperation=Report&QuoteId='+quoteId;
		 Features=Bars+','+Options;
	     Win=open(Url,'Doc',Features); 
	 }
   }
   else
   {
     var positive=document.forms[0].mfValues1[index].checked;
     var negative=document.forms[0].mfValues2[index].checked;
	 if((!positive) && (!negative))
	 {
	     if(isMultiQuote == "Y")//Added by Anil.k
			 document.forms[0].action='QMSMultiQuoteController?Operation=Modify&subOperation=EnterId&QuoteId='+quoteId+'&originLoc='+""+'&destLoc='+"";
		 else//End
		  document.forms[0].action='QMSQuoteController?Operation=Modify&subOperation=EnterId&QuoteId='+quoteId;
          document.forms[0].submit();
         
	 }
     else  
	 {   
      
	     Bars = 'directories=no,location=no,menubar=no,status=no,titlebar=no,scrollbars=yes';
	     Options='width=800,height=600,resizable=yes';
		 if(isMultiQuote == "Y")//Added by Anil.k
			 Url='QMSMultiQuoteController?Operation=View&subOperation=Report&QuoteId='+quoteId+'&originLoc='+""+'&destLoc='+"";
		 else//End
		 Url='QMSQuoteController?Operation=View&subOperation=Report&QuoteId='+quoteId;
		 Features=Bars+','+Options;
	     Win=open(Url,'Doc',Features); 
	 }
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
</script>
  <body>
	<form method='POST' action='' onsubmit="return mainPage()">
	  <table width="100%" border="0" cellspacing="0" cellpadding="0">
	    <tr valign="top" bgcolor="#FFFFFF">
		  <td>
			<table width="100%" cellpadding="4" cellspacing="1">
			  <tr valign="top" class='formlabel'>
			    <td colspan="3"><table width="790" border="0" ><tr class='formlabel'>
			      <td>Pending Quotes  - View Module </td>
				  <td align=right>QS1010241</td>
			      </tr></table></td>
			  </tr>
		    </table>
			  
        <table width="100%"  cellspacing="1" cellpadding="4">
          <tr valign="top" class='formdata'>
			    <td colspan=4>&nbsp;
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
             <%=dateutil.getCurrentDateString(datFormat)%>
			</td>
			<td width="209"> Primary Option:<br>
			  <%=primaryOption!=null?primaryOption:""%>
            </td>
		  </tr>
			</table>
				<table width="100%"  cellspacing="1" cellpadding="4">
			  <tr valign="top" class='formheader'>
			     <td NOWRAP>Positive</td>
			    <td NOWRAP>Negative</td>
			    <td NOWRAP><A href='###' onClick='fetchSortedPageData("Important","<%=pageNo%>","lable")' onmouseover='status="Sort by Important as";return true;' onmouseout="status = '';return true;" title="Sort by Important as">
				Important<%  if(sortBy.equalsIgnoreCase("Important")){%>					
				      <img SRC=<%=imagePath%> border="0">
				 <%}%></A></td>
				<td><A href='###' onClick='fetchSortedPageData("QuoteId","<%=pageNo%>","lable")' onmouseover='status = "Sort by Quote Id as";return true;' onmouseout="status = '';return true;" title="Sort by Quote Id as">
				Quote Id<%  if(sortBy.equalsIgnoreCase("QuoteId")){%>					
				      <img SRC=<%=imagePath%> border="0">
				 <%}%></A></td>
				 <!--@@Added by kameswari for the WPBN issue-61310-->
			    <td><A href='###' onClick='fetchSortedPageData("CreatedDate","<%=pageNo%>","lable")' onmouseover='status = "Sort by Customer Id as";return true;' onmouseout="status = '';return true;" title="Sort by Created Date as">
				Created Date<%  if(sortBy.equalsIgnoreCase("CreatedDate")){%>					
				      <img SRC=<%=imagePath%> border="0">
				 <%}%></A></A></td>
				  <!--@@61310-->
				 <td><A href='###' onClick='fetchSortedPageData("CustomerId","<%=pageNo%>","lable")' onmouseover='status = "Sort by Customer Id as";return true;' onmouseout="status = '';return true;" title="Sort by Customer Id as">
				Customer Id<%  if(sortBy.equalsIgnoreCase("CustomerId")){%>					
				      <img SRC=<%=imagePath%> border="0">
				 <%}%></A></A></td>
			
				  <!--@@Added by kameswari for the WPBN issue-30313-->
                  
				  <!--@@Modified by Kameswari for the WPBN issue-38175-->
				 <td NOWRAP><A href='###' onClick='fetchSortedPageData("CustomerName","<%=pageNo%>","lable")' onmouseover='status = "Sort by Customer Name as";return true;' onmouseout="status = '';return true;" title="Sort by Customer Name as">Customer Name<%  if(sortBy.equalsIgnoreCase("CustomerName")){%>					
				      <img SRC=<%=imagePath%> border="0">
				 <%}%></A></td>
				 <!--@@WPBN 30313-->
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
				 <%}%></A> </td>
			    <td><A href='###' onClick='fetchSortedPageData("ToCountry","<%=pageNo%>","lable")' onmouseover='status = "Sort by To Country as";return true;' onmouseout="status = '';return true;" title="Sort by To Country as">
				To Country<%  if(sortBy.equalsIgnoreCase("ToCountry")){%>					
				      <img SRC=<%=imagePath%> border="0">
				 <%}%></A></td>
			    <td><A href='###' onClick='fetchSortedPageData("ToLocation","<%=pageNo%>","lable")' onmouseover='status = "Sort by To Location as";return true;' onmouseout="status = '';return true;" title="Sort by To Location as">
				To Location<%  if(sortBy.equalsIgnoreCase("ToLocation")){%>					
				      <img SRC=<%=imagePath%> border="0">
				 <%}%></A></td>
				
				<!--  Added by Kishore For statusReason 
				 -- if("H".equals(loginbean.getUserTerminalType())) -->
							<td> Status Reason</td>
			  </tr>
               	<!--@@WPBN issue-38175-->   
<%				String tempKeyVlue ="";//Added by Anil.k for multiquote pending quotes
				int indexValue	   =0;
				for(int i=0;i<dataList.size();i++)
				{
					
					detailsDob=(ReportDetailsDOB)dataList.get(i);
                    keyVlue	=	detailsDob.getQuoteId();
			
%>

			  <tr valign="top" class='formdata'>
				
			<%	
				//System.out.println("keyVluekeyVlue : "+keyVlue);
			//Added by Anil.k for multiquote pending quotes
			if(!tempKeyVlue.equalsIgnoreCase(keyVlue))
			{//End
			if(mapValue1.containsKey(keyVlue))
				{
%>
					<td  NOWRAP><input type="checkbox" name="mfValues1" value="<%=keyVlue%>" id='mfValues1<%=indexValue%>' onClick='checkMarked(document.forms[0].mfValues1,id)'checked <%=disabled%>></td>
<%
				}
				else
				{
%>
					<td  NOWRAP><input type="checkbox" name="mfValues1" value="<%=keyVlue%>" id='mfValues1<%=indexValue%>' onClick='checkMarked(document.forms[0].mfValues1,id)' <%=disabled%>></td>
<%
				}
%>
            <input type="hidden" name="checkValue1" value="<%=keyVlue%>">
<%			   if(mapValue2.containsKey(keyVlue))
				{
%>
					<td  NOWRAP><input type="checkbox" name="mfValues2" value="<%=keyVlue%>" id='mfValues2<%=indexValue%>' onClick='checkMarked(document.forms[0].mfValues2,id)' checked <%=disabled%>></td>
<%
				}
				else
				{
%>
					<td  NOWRAP><input type="checkbox" name="mfValues2" value="<%=keyVlue%>" id='mfValues2<%=indexValue%>' onClick='checkMarked(document.forms[0].mfValues2,id)' <%=disabled%>></td>
<%
				}
%>
            <input type="hidden" name="checkValue2" value="<%=keyVlue%>">
			
            <td NOWRAP>
<%
				if(detailsDob.getImportant()!=null)
				{
					if("I".equalsIgnoreCase(detailsDob.getImportant()))
					{
%>
						<IMG SRC=<%=request.getContextPath()+"/images/imp_flag.gif"%>>
<%
					}
				}
%>
			  </td>
        <td NOWRAP> 
		    <!--Commented and Added by Rakesh on 25-02-2011 for Issue:236363  -->
			<!--  <a href="javascript:forwardQuote('<%=detailsDob.getQuoteId()%>','<%=indexValue%>');" ><%=detailsDob.getQuoteId()==null?"-":detailsDob.getQuoteId()%></a> -->
			 <% if("Y".equalsIgnoreCase(detailsDob.getIsMultiQuote())) {%>
			 <a href="QMSMultiQuoteController?Operation=Modify&subOperation=EnterId&QuoteId=+<%=(detailsDob.getQuoteId()).trim()%>+&originLoc=+<%=empty.trim()%>+&destLoc=+<%=empty.trim()%>" ><%=detailsDob.getQuoteId()==null?"-":detailsDob.getQuoteId()%></a>
			 <%}else{%>
			 <a href="QMSQuoteController?Operation=Modify&subOperation=EnterId&QuoteId=+<%=(detailsDob.getQuoteId()).trim()%>" ><%=detailsDob.getQuoteId()==null?"-":detailsDob.getQuoteId()%></a>
			 <%}%>
			 <!--Ended by Rakesh on 25-02-2011 for Issue:236363  -->
				</td>
				<td NOWRAP><%=detailsDob.getCreateDate()==null?"-":dateutil.getDisplayString(detailsDob.getCreateDate())%></td><!--@@Added by kameswari for the WPBN issue-61310-->
            <td NOWRAP><%=detailsDob.getCustomerId()==null?"-":detailsDob.getCustomerId()%></td>
			 <td><%=detailsDob.getCustomerName()==null?"-":detailsDob.getCustomerName()%></td><!--@@added by kameswari for the WPBN issue-30313-->
			  <input type="hidden" name="isMultiQuote" id='<%=indexValue%>' value="<%=detailsDob.getIsMultiQuote()%>">
		
<%		indexValue++;	
			  
			  
			  }//Added by Anil.k for multiquote pending quotes
			else
			{
%>				<input type="hidden" name="checkValue1" value="<%=keyVlue%>">
			   <input type="hidden" name="checkValue2" value="<%=keyVlue%>">
				<td  NOWRAP></td>
				<td  NOWRAP></td>
				<td  NOWRAP></td>
				<td  NOWRAP></td>
				<td  NOWRAP></td>
				<td  NOWRAP></td>
				<td  NOWRAP></td>
<%			}//Ended for multiquote pending quotes
%>
			  <td NOWRAP><%=detailsDob.getServiceLevel()==null?"-":detailsDob.getServiceLevel()%></td>
			  <td NOWRAP><%=detailsDob.getFromCountry()==null?"-":detailsDob.getFromCountry()%></td>
			  <td NOWRAP><%=detailsDob.getFromLocation()==null?"-":detailsDob.getFromLocation()%></td>
			  <td NOWRAP><%=detailsDob.getToCountry()==null?"-":detailsDob.getToCountry()%></td>
			  <td NOWRAP><%=detailsDob.getToLocation()==null?"-":detailsDob.getToLocation()%></td>
			  

			  <!--  Added by Kishore For statusReason -->
			  
		<!-- 	 if("H".equals(loginbean.getUserTerminalType())){ -->
				
				<%if(!tempKeyVlue.equalsIgnoreCase(keyVlue)){ %>
		
			  <td NOWRAP>
				<!--<select class='select' valign='bottom' name='statusReason'  style="width:118px;margin:0px 0 5px 0;">-->
				<select class='select' valign='bottom' name='statusReason' id='statusReason<%=indexValue%>' style="width:220px;margin:0px 0 5px 0;"><!-- Modified by silap.p on 14-06-11-->
					<option value=''></option>
				<%
				     Set statusReasonIds  = statusReasonMap.keySet();
					Iterator It = statusReasonIds.iterator();
				
					while (It.hasNext()) {
						Integer id = (Integer) (It.next());
						String statusReason	= statusReasonMap.get(id);
						//System.out.println("StatusReason:"+id+"------------------"+statusReason);
					%>
						<option value= '<%=id%>' ><%=statusReason%> </option>
			  <%}%>	 	
			</select>

			</td>
  			<%}else{%>
			 <INPUT TYPE="hidden" name='statusReason' id='statusReason<%=indexValue%>'  value=''>
				<td/>
			<%}%>
			<!--  End Of  Kishore For statusReason -->
				
           </tr>
<%
			tempKeyVlue = keyVlue;//Added by Anil.k	
				}
%>
			   
			</table>
            <table width="100%"  cellspacing="1" cellpadding="4">
			<tr class="text" vAlign="top">
                <td valign="middle" colspan="8" >
<%
					int  currentPageNo	=	0;
					//if(reportenterDob.getPageNo()!=null)
						//currentPageNo =Integer.parseInt(reportenterDob.getPageNo());
						//currentPageNo = Integer.parseInt(sellRatesDob.getPageNo());
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
			 <td colspan="5"><font color="#ff0000">*</font> Denotes Mandatory </td>
			 <td colspan="4" align="right"><input type='submit' class='input'  name='b1' value='Update'></td>
			 <INPUT TYPE="hidden" name='Operation' value="<%=request.getParameter("Operation")%>">
			 <INPUT TYPE="hidden" name='wiseoptions' value="<%=primaryOption!=null?primaryOption:""%>">
			 <INPUT TYPE="hidden" name='sortedOrder' value="<%=sortOrder%>">
			 <INPUT TYPE="hidden" name='sortedBy' value="<%=sortBy%>">
			 <INPUT TYPE="hidden" name='pageNo' value="<%=pageNo%>">
			 <INPUT TYPE="hidden" name='format' value="<%=request.getParameter("subOperation")%>">
			 <INPUT TYPE="hidden" name='fromSummary' value="<%=fromSummary%>"><!-- subrahmanyam -->
			 fromSummary
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
			//Logger.error(FILE_NAMsE,"Error in PendingQuotesReports.jsp "+e);
      logger.error(FILE_NAME+"Error in PendingQuotesReports.jsp "+e);
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