<%--

Copyright (c) 1999-2001 by FourSoft, Pvt Ltd. All Rights Reserved.
This software is the proprietary information of FourSoft, Pvt Ltd.
Use is subject to license terms.
esupply - v 1.x.
Program	Name	: UpdatedQuotesReport.jsp
Product Name	: QuoteShop
Module Name		: Quote Reports
Date started	: 21-Nov-2005
Date Completed	: 21-Nov-2005
Date modified	:  
Author    		: Yuvraj

--%>
<%@page import ="java.util.ArrayList,
				 java.util.HashMap,
				 com.qms.reports.java.UpdatedQuotesReportDOB,
				 com.qms.reports.java.UpdatedQuotesDOB,
				 com.qms.reports.java.UpdatedQuotesFinalDOB,
				 org.apache.log4j.Logger,
				 com.foursoft.esupply.common.util.ESupplyDateUtility"%>

<jsp:useBean id = "loginbean" class = "com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope = "session"/>

<%!
  private static Logger logger = null;
	private static final String FILE_NAME="UpdatedQuotesReport.jsp";
%>

<%
    logger  = Logger.getLogger(FILE_NAME);	
		ESupplyDateUtility  eSupplyDateUtility		=	new ESupplyDateUtility();
		ArrayList			dataList				=	null;
		ArrayList			excelData				=	null;//Added by Anil.k for Excel Page
		ArrayList			finalList				=	null;
		UpdatedQuotesReportDOB reportDOB			=	null;
		UpdatedQuotesFinalDOB  finalDOB				=	null;
		UpdatedQuotesDOB	   changeDescDOB		=	null;
		String              dateFormat				=	loginbean.getUserPreferences().getDateFormat();
		String				currentDate				=	null;
		String				changeDesc				=	null;
		String              sortBy                  =   request.getParameter("SortBy");
		String              sortOrder               =   request.getParameter("SortOrder");
		String              pageNo                  =   request.getParameter("PageNo");
		String              format                  =   request.getParameter("format");
		String				imagePath				=	"";
		HashMap				mapValue				=	null;
		String				keyVlue					=	null; 
		String              fromSummary             = request.getParameter("fromSummary");//added by subrahmanyam 146463
		String				userTerminalType		=	loginbean.getUserTerminalType();//Added by Anil.k for the CR 231104
		
		if("ASC".equalsIgnoreCase(sortOrder))
		  imagePath = request.getContextPath()+"/images/asc.gif";
		else
		  imagePath = request.getContextPath()+"/images/desc.gif";
      
		int                 noofRecords           =   0;
		int                 noofPages             =   0;
		
		mapValue									=	(HashMap)session.getAttribute("HashList");

		int			noOfSegments					=   Integer.parseInt(loginbean.getUserPreferences().getSegmentSize());
	    
		if(mapValue==null)
		   mapValue	=	new HashMap();

		
		try
		{
			eSupplyDateUtility.setPatternWithTime(dateFormat);
		}
		catch(Exception exp)
		{
			//Logger.error(FILE_NAME," Error in JSP UserPreferences DateFormat---> "+exp.toString());
      logger.error(FILE_NAME+" Error in JSP UserPreferences DateFormat---> "+exp.toString());
		}
		
		try
		{
			finalDOB				=	(UpdatedQuotesFinalDOB)session.getAttribute("reportFinalDOB");	
			
			if(finalDOB!=null)
			{
				//System.out.println("finalDOB.getUpdatedQuotesList() sizes:"+(finalDOB.getUpdatedQuotesList()).size());
				if("html".equalsIgnoreCase(request.getParameter("format")))
				{
				finalList				=	finalDOB.getUpdatedQuotesList();

				
				if(finalList.get(0)!=null)
					noofRecords = ((Integer)finalList.get(0)).intValue();
				if(finalList.get(1)!=null)
					noofPages   = ((Integer)finalList.get(1)).intValue();
				if(finalList.get(2)!=null)
					dataList      =   (ArrayList) finalList.get(2);
			
//System.out.println("no_Of_pagesno_Of_pages :"+no_Of_pages);
				changeDescDOB			=	(UpdatedQuotesDOB)finalDOB.getChangeDescList().get(finalDOB.getChangeDescSelectedIndex());
				changeDesc				=	changeDescDOB.getChangeDesc();
			}
				else
					excelData				=	finalDOB.getUpdatedQuotesList();
			}

			if(dataList==null)
				dataList			=	new ArrayList();
			
			currentDate				=	eSupplyDateUtility.getCurrentDateString(dateFormat);

		
		if("html".equalsIgnoreCase(request.getParameter("format")))
		{
%>
<html>
  <head>
	<title>Updated Quotes Report</title>
	<link rel="stylesheet" href="ESFoursoft_css.jsp">
  <STYLE type="text/css">
  A:link { COLOR: blue; FONT-WEIGHT: none; TEXT-DECORATION: none }
  A:hover { COLOR: blue; FONT-WEIGHT: none; TEXT-DECORATION: underline }
  A:visited { color: blue; font-weight: none; text-decoration: none }
  </STYLE>
 
	<script>


function emailselectAll()
{
	var checkBoxes = document.getElementsByName("mfValues");
		//@@Added by Kameswari forthe WPBN issue-142381 on 23/10/2008
		
	if(document.forms[0].selectEmail.checked)
	{
		
	    for(var i=0;i<checkBoxes.length;i++)
		{		
			document.getElementById("emailCheck"+i).checked=true;
			
		}
	}
	else
	{
		for(var i=0;i<checkBoxes.length;i++)
		{
			document.getElementById("emailCheck"+i).checked=false;

			//setValue(i);
		}
	}
}
//@@23/10/2008
function selectAll()
{

  var checkBoxes = document.getElementsByName("mfValues");
	if(document.forms[0].select.checked==true)
	{
		for(var i=0;i<checkBoxes.length;i++)
		{		
			checkBoxes[i].checked=true;
			//setValue(i);
		}
	}
	else
	{
		for(var i=0;i<checkBoxes.length;i++)
		{
			checkBoxes[i].checked=false;
			//setValue(i);
		}
	}
}
function setValue(index)
{
	if(document.getElementsByName("checkbox")[index].checked)
		document.getElementsByName("checkBoxValue")[index].value=index;
	else
		document.getElementsByName("checkBoxValue")[index].value='';
}
function disableUnChecked()
{
	var checkBoxes = document.getElementsByName("checkbox");
	for(var i=0;i<checkBoxes.length;i++)
	{
		if(checkBoxes[i].checked==false)
			document.getElementsByName("checkBoxValue")[i].disabled=true;
		else
			document.getElementsByName("checkBoxValue")[i].disabled=false;
	}
}
function validate()
{
	var checkBoxes	= document.getElementsByName("mfValues");
	var checkedFlag	= false;
	//@@Added by Kameswari forthe WPBN issue-142381 on 23/10/2008
	var count = 0;
	for(var i=0;i<<%=dataList.size()%>;i++)
	{
		var a = "emailCheck"+i;
		var mf = "mfValues"+i;

		if(document.getElementById(a).checked==true&&document.getElementById(mf).checked==false)
		{
            count++;
		    break;
		}

	}
	if(count>0)
	{
		alert("Please select respective quote to confirm");
		document.forms[0].select.focus();
		return false;	
    }
	//@@23/10/2008
	for(var i=0;i<checkBoxes.length;i++)
	{
		if(checkBoxes[i].checked)
			checkedFlag=true;
	}
	if(!checkedFlag && document.forms[0].submitName.value=='Confirm>>')
	{
		alert('Please Select At Least One Quote to Confirm.');
		document.forms[0].select.focus();
		return false;
	}
	else
	{
		document.forms[0].subOperation.disabled=false;
		document.forms[0].Operation.disabled=false;
		document.forms[0].action = 'QMSReportController';
	}
}
function setName(name)
{
	document.forms[0].submitName.value=name;
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

	document.forms[0].action = "QMSReportController?Operation="+Operation+"&subOperation=pageBrowse&ChargeDesc=<%=changeDesc%>&SortBy="+sortBy+"&SortOrder="+sortOrder+"&PageNo="+pageNo+"&format="+format;
	//alert(document.forms[0].action);
    document.forms[0].submit();
}
//@@Commented by Kameswari for the WPBN issue-61235
/*function openQuote(uniqueId,sellBuyFlag,changeDesc,quoteId)
{
	flag = confirm("Select OK to Edit Quote or CANCEL to View/Print Quote");
  document.forms[0].action = 'QMSQuoteController?Operation=Update&quoteId='+uniqueId+'&sellBuyFlag='+sellBuyFlag+'&changeDesc='+changeDesc+'&masterId='+quoteId+'&sortedBy=<%=sortBy%>&sortedOrder=<%=sortOrder%>&pageNo=<%=pageNo%>&fromWhat=<%=request.getParameter("fromWhat")!=null?request.getParameter("fromWhat"):""%>&flag='+flag;
	document.forms[0].subOperation.disabled=true;
	document.forms[0].Operation.disabled=true;

	document.forms[0].submit();


}*/
//@@WPBN issue-61235
//@@Modified by Kameswari for the WPBN issue-61235
	function openQuote(uniqueId,sellBuyFlag,changeDesc,quoteId,index)
{
	//	alert(index);
		if(index==0)
			var isMultiQuoteVal = document.forms[0].isMultiQuote.value
		else
			var isMultiQuoteVal = document.forms[0].isMultiQuote[index].value
	var Url	= './ConfirmBox.jsp?Operation=Update&quoteId='+uniqueId+'&sellBuyFlag='+sellBuyFlag+'&changeDesc='+changeDesc+'&masterId='+quoteId+'&sortedBy=<%=sortBy%>&sortedOrder=<%=sortOrder%>&pageNo=<%=pageNo%>&fromWhat=<%=request.getParameter("fromWhat")!=null?request.getParameter("fromWhat"):""%>&isMultiQuote='+isMultiQuoteVal;//Added by Anil.k
	var Bars = 'directories=no, location=no,menubar=no,status=no,titlebar=no,scrollbars=1';
	var Options='width=700,height=100, resizable=yes';
	var Features=Bars+' '+Options; 
	var Win=open(Url,'Doc',Features);

}
//@@WPBN issue-61235

//Added by Anil.k for CR 231104 on 27Jan2011
function dontModifyselectAll()
{
	var checkBoxes = document.getElementsByName("dontModifyValue");
	if(document.forms[0].dontModify.checked)
	{	
	    for(var i=0;i<checkBoxes.length;i++)
		{		
			document.getElementById("dontModifyValue"+i).checked=true;
		}
	}
	else
	{
		for(var i=0;i<checkBoxes.length;i++)
		{
			document.getElementById("dontModifyValue"+i).checked=false;
		}
	}
}//Ended by Anil.k for CR 231104 on 27Jan2011

</script>
  </head>

  <body>
 <form method='POST' action='QMSReportController' onsubmit='return validate();'>
	<table width="100%" cellpadding="0" cellspacing="0" bgcolor='#FFFFFF'>
	<tr>
	<!-- added by subrahmanyam for 146463 -->
	<input type='hidden' name='fromSummary' value='<%=fromSummary%>'/>
		<td>
	 <table width="100%" cellpadding="0" cellspacing="0" bgcolor='#FFFFFF'>
		  <tr valign="top" class="formlabel">
			<td	colspan="2"><table cellpadding="4" cellspacing="1" width="100%"><tr class='formlabel'><td>Updated Quotes</td><td align='right'><!-- Screen Id goes here --></td></tr></table></td>
		  </tr></table>
			  
        <table width="100%" cellpadding="4" cellspacing="1" bgcolor='#FFFFFF'>
			 <tr valign="top" class='formdata'>
				<td>Terminal Id:&nbsp;<%=loginbean.getTerminalId()%></td>
				<td >User:<%=loginbean.getUserId()%></td>
	            <td> Date:&nbsp;<%=currentDate%></td>
			 </tr> 
		</table> 

	  <table width="100%" cellpadding="4" cellspacing="1" bgcolor='#FFFFFF'>
	  <tr valign="top" class='formheader'>
	<!-- 	<td>Confirm & Send <br><INPUT TYPE="checkbox" NAME="select" onclick='selectAll()'></td> -->
		<td>Confirm <br><INPUT TYPE="checkbox" NAME="select" onclick='selectAll()'></td> 
		<!-- Added by Anil.k for CR 231104 on 27Jan2011 -->
		<%if(!"O".equalsIgnoreCase(userTerminalType)){ %>
		<td>Dont Modify<br><INPUT TYPE="checkbox" NAME="dontModify" onclick='dontModifyselectAll()'></td>
		<%}%>
		<td>Send Options <br>(Previous)</td>
		<td>Send Email <br><INPUT TYPE="checkbox" NAME="selectEmail" onclick='emailselectAll()'></td> 
		<td NOWRAP>
			<A href='###' onClick='fetchSortedPageData("Important","<%=pageNo%>","lable")' onmouseover='status="Sort by Important as";return true;' onmouseout="status = '';return true;" title="Sort by Important as">
				Important<%  if(sortBy.equalsIgnoreCase("Important")){%>					
				      <img SRC=<%=imagePath%> border="0">
				 <%}%></A></td>
		<td>
			<A href='###' onClick='fetchSortedPageData("CustomerId","<%=pageNo%>","lable")' onmouseover='status = "Sort by Customer Id as";return true;' onmouseout="status = '';return true;" title="Sort by Customer Id as">
				Customer Id<%  if(sortBy.equalsIgnoreCase("CustomerId")){%>					
				      <img SRC=<%=imagePath%> border="0">
				 <%}%></A></td>
				 <!--@@Modified by Kameswari for the WPBN issue-38175-->
		 <!--@@Added by kameswari for the WPBN issue-30313-->
		 <td NOWRAP><A href='###' onClick='fetchSortedPageData("CustomerName","<%=pageNo%>","lable")' onmouseover='status = "Sort by Customer Name as";return true;' onmouseout="status = '';return true;" title="Sort by Customer Name as">Customer Name<%  if(sortBy.equalsIgnoreCase("CustomerName")){%>					
		  <img SRC=<%=imagePath%> border="0">
		 <%}%></A></td>
		<!--@@WPBN 30313-->
		<td>
			<A href='###' onClick='fetchSortedPageData("QuoteId","<%=pageNo%>","lable")' onmouseover='status = "Sort by Quote Id as";return true;' onmouseout="status = '';return true;" title="Sort by Quote Id as">
				QuoteId<br>(Previous)<%  if(sortBy.equalsIgnoreCase("QuoteId")){%>
				      <img SRC=<%=imagePath%> border="0">
				 <%}%></A></td>
		<td>
			<A href='###' onClick='fetchSortedPageData("Mode","<%=pageNo%>","lable")' onmouseover='status = "Sort by Customer Id as";return true;' onmouseout="status = '';return true;" title="Sort by Customer Id as">
				Mode<%  if(sortBy.equalsIgnoreCase("Mode")){%>					
				      <img SRC=<%=imagePath%> border="0">
				 <%}%></A></td>
		<td>
			<A href='###' onClick='fetchSortedPageData("ServiceLevel","<%=pageNo%>","lable")' onmouseover='status = "Sort by Service Level as";return true;' onmouseout="status = '';return true;" title="Sort by Service Level as">
				Service Level<%  if(sortBy.equalsIgnoreCase("ServiceLevel")){%>					
				      <img SRC=<%=imagePath%> border="0"> 
				 <%}%></A></td>
		<td>
			<A href='###' onClick='fetchSortedPageData("FromCountry","<%=pageNo%>","lable")' onmouseover='status = "Sort by From Country as";return true;' onmouseout="status = '';return true;" title="Sort by From Country as">
				From Country<%  if(sortBy.equalsIgnoreCase("FromCountry")){%>					
				      <img SRC=<%=imagePath%> border="0">
				 <%}%></A></td>
		<td>
			<A href='###' onClick='fetchSortedPageData("FromLocation","<%=pageNo%>","lable")' onmouseover='status = "Sort by From Location as";return true;' onmouseout="status = '';return true;" title="Sort by From Location as">
				From Location<%  if(sortBy.equalsIgnoreCase("FromLocation")){%>					
				      <img SRC=<%=imagePath%> border="0">
				 <%}%></A></td>
		<td><A href='###' onClick='fetchSortedPageData("ToCountry","<%=pageNo%>","lable")' onmouseover='status = "Sort by To Country as";return true;' onmouseout="status = '';return true;" title="Sort by To Country as">To<br> Country
		<%  if(sortBy.equalsIgnoreCase("ToCountry")){%>					
				      <img SRC=<%=imagePath%> border="0">
				 <%}%></A></td>
		<td>
			<A href='###' onClick='fetchSortedPageData("ToLocation","<%=pageNo%>","lable")' onmouseover='status = "Sort by To Location as";return true;' onmouseout="status = '';return true;" title="Sort by To Location as">
				To <br>Location<%  if(sortBy.equalsIgnoreCase("ToLocation")){%>					
				      <img SRC=<%=imagePath%> border="0">
				 <%}%></A></td>
				 <!--@@WPBN issue-38175-->
	  </tr>

<%		 for(int i=0;i<dataList.size();i++)
		{
			   reportDOB		=		(UpdatedQuotesReportDOB)dataList.get(i);
			   keyVlue			=		""+reportDOB.getQuoteId();
			   
			   //System.out.println("reportDOB.getFaxEmailPrintFlag---"+reportDOB.getFaxEmailPrintFlag());
%>

			   <tr valign="top" class='formdata'>
<%			   if(mapValue.containsKey(keyVlue))
				{
				    
%>
					<td><input type="checkbox" name="mfValues" value="<%=keyVlue%>" id='mfValues<%=i%>' checked></td>
					
<%
				}
				else
				{
					 	
%>
					<td><input type="checkbox" name="mfValues" value="<%=keyVlue%>" id='mfValues<%=i%>'></td>
<%
				}
%>
					<input type="hidden" name="checkValue" value="<%=keyVlue%>">

					<!-- Added by Anil.k for CR 231104 on 27Jan2011 -->
					<%if(!"O".equalsIgnoreCase(userTerminalType)){ %>
					<td><input type="checkbox" name="dontModifyValue" value="<%=keyVlue%>" id="dontModifyValue<%=i%>"></td>
					<input type="hidden" name="dontModifyCheck" value="<%=keyVlue%>">
					<%}%>

				<td><%=reportDOB.getFaxEmailPrintFlag()%></td>
				 <!--@@Added by Kameswari for client request on 14/10/2008-->
				<%			   if(mapValue.containsKey(keyVlue))
				{
					%>
				 <td><input type="checkbox" name="emailCheck<%=keyVlue%>" id="emailCheck<%=i%>"   checked ></td>
				
               <%
						
				}
				else
				{
				
%>
				 <td><input type="checkbox" name="emailCheck<%=keyVlue%>" id="emailCheck<%=i%>"   ></td>
				
				 <%
				}
%>
		 <!--@@Added by Kameswari for client request on 14/10/2008-->
				<td nowrap>
<%
					if("I".equalsIgnoreCase(reportDOB.getImpFlag()))
					{
%>
						<IMG SRC=<%=request.getContextPath()+"/images/imp_flag.gif"%>>
<%
					}
%>
				</td>				
				<td nowrap><%=reportDOB.getCustomerId()%></td>
				<td><%=reportDOB.getCustomerName()!=null?reportDOB.getCustomerName():""%></td><!--added by kameswari for the WPBN issue-30313-->
				<td nowrap><a href = "javascript:openQuote('<%=reportDOB.getUniqueId()%>','<%=reportDOB.getSellBuyFlag()%>','<%=changeDesc%>','<%=reportDOB.getQuoteId()%>','<%=i%>')" onmouseover="status = 'Modify Quote';return true;" onclick="status = '';return true;" onmouseout="status = '';return true;" title="Click to Modify Quote"><%=reportDOB.getQuoteId()!=null?reportDOB.getQuoteId()+"":""%></a></td>
	<!--in the above line reportDOB.getQuoteId()!=0 replced with reportDOB.getQuoteId()!=null by subrahmanyam for the enhancement #146971 on 03/12/2008  -->
				<input type="hidden" name="isMultiQuote" id="isMultiQuote<%=i%>" value='<%=reportDOB.getIsMultiQuote()%>'><!--Added by Anil.k for CR 231104 on 31Jan2011 -->
				<td nowrap><%=reportDOB.getShipmentMode()!=null?reportDOB.getShipmentMode():""%></td>
				<td nowrap><%=reportDOB.getServiceLevel()!=null?reportDOB.getServiceLevel():""%></td>
			    <td NOWRAP><%=reportDOB.getOriginCountry()!=null?reportDOB.getOriginCountry():""%></td>
				<td NOWRAP><%=reportDOB.getOriginLocation()!=null?reportDOB.getOriginLocation():""%></td>
				<td><%=reportDOB.getDestCountry()!=null?reportDOB.getDestCountry():""%></td>
				<td><%=reportDOB.getDestLocation()!=null?reportDOB.getDestLocation():""%></td>
			  </tr>
<%
		}%>
		    <tr valign="top" class='formdata'>			  
			 <td colspan="14">
<%
					int  currentPageNo	=	0;
					
					currentPageNo =Integer.parseInt(request.getParameter("PageNo"));

					if(currentPageNo != 1)
					{
%>
						  <a href="###" onClick='fetchSortedPageData("<%=request.getParameter("SortBy")%>","<%=currentPageNo-1%>","pageNo")'><img src="images/Toolbar_backward.gif"  border="0"></a>
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
							<A href="###" onClick='fetchSortedPageData("<%=request.getParameter("SortBy")%>","<%=i+1%>","pageNo")'><%=i+1%></A>

<%	                       }
				 if(currentPageNo != noofPages)
				  {
%>
						<a href="###" onClick='fetchSortedPageData("<%=request.getParameter("SortBy")%>","<%=currentPageNo+1%>","pageNo")'><img src="images/Toolbar_forward.gif" border="0"></a>
<%					
				  }
%>
			 </td>
             </tr>
			  <tr valign="top" class='denotes'>
			    <td colspan="10">NOTE: Previous Quote ID hyperlink displays the updated quote details as per new Buy Rates.<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;New Quote ID is generated after the Updated Quote is Confirmed, which is sent to the Customer.</td>
			    <td colspan="2" align="right">
					<input type='submit' name='submit1' class='input'  value='<<Back' onclick='setName(this.value)'>&nbsp;
					<input type='submit' name='submit1' class='input'  value='Confirm>>' onclick='setName(this.value)'>
					<INPUT TYPE="hidden" name='Operation' value='<%=request.getParameter("Operation")%>'>
					<INPUT TYPE="hidden" name='subOperation' value='Confirm'>
					<INPUT TYPE="hidden" name='submitName'>
					<INPUT TYPE="hidden" name='fromWhat'  value='<%=request.getParameter("fromWhat")!=null?request.getParameter("fromWhat"):""%>'>
                    <INPUT TYPE="hidden" name='sortedOrder' value="<%=sortOrder%>">
					<INPUT TYPE="hidden" name='sortedBy' value="<%=sortBy%>">
					<INPUT TYPE="hidden" name='pageNo' value="<%=pageNo%>">
					<INPUT TYPE="hidden" name='format' value="<%=format%>">
				</td>
			    </tr>
			</table>
		</tr>
	</td>
</table>
	</form>
  </body>
</html>
<%			}
			else
			{
				response.setContentType("application/vnd.ms-excel");
				response.setHeader("Content-Disposition","attachment;filename=UpdatedQuotesReport.xls");
%>				
<html>
  <head>
	<title>Updated Quotes Report</title>
  </head>

  <body>
	<table width="100%" cellpadding="0" cellspacing="0" border='1'>
	<tr>
		<td>
	 <table width="100%" cellpadding="0" cellspacing="0" border='1'>
		  <tr valign="top" class="formlabel">
			<td colspan='11' align='center'><B>Updated Quotes</B></td>
			<br><br>
		  </tr>
	</table>
			  
        <table width="100%" cellpadding="4" cellspacing="1" border='1'>
			 <tr valign="top" class='formdata'>
				<td colspan='3'><B>Terminal Id:</B>&nbsp;<%=loginbean.getTerminalId()%></td>
				<td colspan='3'><B>User:</B><%=loginbean.getUserId()%></td>
	            <td colspan='5'> <B>Date:</B>&nbsp;<%=currentDate%></td>
			 </tr>
			 <tr>
				<td colspan='13'></td>
			 </tr>
		</table>

	  <table width="100%" cellpadding="4" cellspacing="1"  border='1'>
		  <tr valign="top" class='formheader'>
			 <td><b>Created By</b></td>
			 <td><b>Sales Person</b></td>
			 <td><b>Created Date</b></td>
			<!-- <td>Send Options <br>(Previous)</td>
			<td><b>Important</b></td> -->			
			<td><B>Customer Id</B></td>
			<td><B>Customer Name<B></td><!-- @@added by kameswari for the WPBN issue-30313-->
			<td><B>Quote Id (Previous)</B></td>
			<td><b>Charge Description</b></td>
			<td><B>Mode </B></td>
			<td><B>Service Level</B> </td>
			<td><B>From Country</B> </td>
			<td><B>From Location</B> </td>
			<td><B>To Country</B> </td>
			<td><B>To Location</B></td>
		  </tr>
			<br><br>
<%	
			//excelData				=	finalDOB.getUpdatedQuotesList();
			for(int j=0;j<excelData.size();j++)
			{
			finalList				=	(ArrayList)excelData.get(j);

				
				if(finalList.get(0)!=null)
					noofRecords = ((Integer)finalList.get(0)).intValue();
				if(finalList.get(1)!=null)
					noofPages   = ((Integer)finalList.get(1)).intValue();
				if(finalList.get(2)!=null)
					dataList      =   (ArrayList) finalList.get(2);
			
//System.out.println("no_Of_pagesno_Of_pages :"+no_Of_pages);
				changeDescDOB			=	(UpdatedQuotesDOB)finalDOB.getChangeDescList().get(Integer.parseInt(finalDOB.getChangeDescSelectedIndexArr()[j]));
				changeDesc				=	changeDescDOB.getChangeDesc();
				System.out.println(changeDesc+"!!!!!!!!!");
			
			for(int i=0;i<dataList.size();i++)
		{
					reportDOB		=		(UpdatedQuotesReportDOB)dataList.get(i);
%>

			   <tr valign="top" class='formdata'>
			    <td align='left'><%=reportDOB.getCreatedBy()%></td>
				<td align='left'><%=reportDOB.getSalesPerson()%></td>
				<td align='left'><%=reportDOB.getCreatedDateStr()%></td>
				<!-- <td align='left'><%=reportDOB.getFaxEmailPrintFlag()%></td>
				<td align='left'><%="I".equalsIgnoreCase(reportDOB.getImpFlag())?"IMP":""%></td>	 -->
				<td align='left'><%=reportDOB.getCustomerId()%></td>
				<td align='left'><%=reportDOB.getCustomerName()%></td><!--@@added by kameswari for the WPBN issue-30313-->
				<td align='left'><%=reportDOB.getQuoteId()!=null?reportDOB.getQuoteId()+"":""%></td>
				<td align='left'><%=changeDesc%></td>
				<!--in the above line reportDOB.getQuoteId()!=0 replced with reportDOB.getQuoteId()!=null by subrahmanyam sfor the enhancement #146971 on 03/12/2008  -->
				<td align='left'><%=reportDOB.getShipmentMode()!=null?reportDOB.getShipmentMode():""%></td>
				<td align='left'><%=reportDOB.getServiceLevel()!=null?reportDOB.getServiceLevel():""%></td>
			    <td align='left'><%=reportDOB.getOriginCountry()!=null?reportDOB.getOriginCountry():""%></td>
				<td align='left'><%=reportDOB.getOriginLocation()!=null?reportDOB.getOriginLocation():""%></td>
				<td align='left'><%=reportDOB.getDestCountry()!=null?reportDOB.getDestCountry():""%></td>
				<td align='left'><%=reportDOB.getDestLocation()!=null?reportDOB.getDestLocation():""%></td>
			  </tr>
<%
		}
		}
%>
			</table>
		</tr>
	</td>
		</table>
		</form>
  </body>
</html>		
<%			}
		}
		catch(Exception	e)
		{
			e.printStackTrace();
			//Logger.error(fileName,"Exception while calling remote method", e.toString());
		}
%>