
<%--

Copyright (c) 1999-2001 by FourSoft, Pvt Ltd. All Rights Reserved.
This software is the proprietary information of FourSoft, Pvt Ltd.
Use is subject to license terms.
esupply - v 1.x.
Program	Name	: BuyRatesExpiryReport.jsp
Product Name	: QMS
Module Name		: BuyRatesExpiryReport
Date started	: 16 September 2005 
Date Completed	: 
Date modified	:  
Author    		: K.NareshKumarReddy

--%>
<%@page import ="javax.naming.InitialContext,
				 java.util.ArrayList,
				 com.qms.reports.java.ReportsEnterIdDOB,
				 com.qms.reports.java.ReportDetailsDOB,
				 org.apache.log4j.Logger,
				 com.foursoft.esupply.common.java.ErrorMessage,
				 com.foursoft.esupply.common.java.KeyValue,
				 com.foursoft.esupply.common.util.ESupplyDateUtility,
				 com.qms.reports.ejb.sls.ReportsSession,
				 com.qms.reports.ejb.sls.ReportsSessionBeanHome "%>

<jsp:useBean id = "loginbean" class = "com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope = "session"/>

<%!
  private static Logger logger = null;
	private static final String FILE_NAME="BuyRatesExpiryReport.jsp";
%>

<%
    logger  = Logger.getLogger(FILE_NAME);	
		 ErrorMessage errorMessageObject = null;
		 ArrayList	  keyValueList	 	 =   new ArrayList();
		 ESupplyDateUtility  dateutil    = new ESupplyDateUtility();
		 
		 String datFormat=dateutil.getCurrentDateString(loginbean.getUserPreferences().getDateFormat());

		 String dateFormat1 = loginbean.getUserPreferences().getDateFormat();

		 System.out.println("dateFormat1dateFormat1dateFormat1dateFormat1dateFormat1dateFormat1 :: "+dateFormat1);
		 
		 String		  operation		= request.getParameter("Operation");
		 String       expDate       = request.getParameter("expDate");
		 String       label         = "";
		 String       sortLabel		= "";
         
		 String       sortBy        = request.getParameter("SortBy");
		 String       sortOrder     = request.getParameter("SortOrder");
		 String       pageNo        = request.getParameter("PageNo");
		 String       imagePath     = "";
		 if("ASC".equalsIgnoreCase(sortOrder))
		   imagePath = request.getContextPath()+"/images/asc.gif";
		 else
		   imagePath = request.getContextPath()+"/images/desc.gif";

	//	 String		  suboperation	= request.getparameter("subOperation");
		 System.out.println("expDate::"+expDate);

		 ArrayList	  pageIterList	= null;
 		 ArrayList    dataList		= null;
		 ArrayList    mainDataList	= null;
		 ReportsEnterIdDOB reportenterDob = null;
		 int noofRecords	=	0;
		 int noofPages		=	0;
		 int noOfSegments	=	0;
		 try
		 {
		 noOfSegments           =   Integer.parseInt(loginbean.getUserPreferences().getSegmentSize());
		 dateutil.setPattern(dateFormat1);

		 pageIterList			= new ArrayList();
		 dataList				= new ArrayList(); 
		 mainDataList			= new ArrayList(); 
		 reportenterDob			= new ReportsEnterIdDOB();
		 mainDataList			= (ArrayList)request.getAttribute("mainDataList");
         reportenterDob			= (ReportsEnterIdDOB)session.getAttribute("reportenterDob");
		 
		 if(mainDataList!=null && mainDataList.size()>0)
			{
				pageIterList       = 	(ArrayList)mainDataList.get(0);
				dataList		   =	(ArrayList)mainDataList.get(1);

				Integer  no_ofRecords   =   (Integer)pageIterList.get(0);
				Integer  no_ofPages		=	(Integer)pageIterList.get(1);

				noofRecords				=	no_ofRecords.intValue();
				noofPages				=	no_ofPages.intValue();

			//session.setAttribute("reportenterDob",reportenterDob);
			//Logger.error(FILE_NAME," noofRecordsnoofRecordsnoofRecords : "+noofRecords);
			//Logger.error(FILE_NAME," noofPagesnoofPagesnoofPages : "+noofPages);
			}
		 
		 if("Y".equalsIgnoreCase(expDate))
		 {
           label="Expiry With In";
		   sortLabel= "ExpiryWithIn";
		 }
		 else
		 {
           label="Active Period";
		   sortLabel= "ActivePeriod";
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

<script src="<%=request.getContextPath()%>/reports/TableSorting.js"></script>

<script>
function functionCall(pageNo,operation)
{
document.forms[0].action='QMSReportController?Operation=buyratesexpiry&pageNo='+pageNo+'&Operation='+operation+'&subOperation=buyratespagging';
document.forms[0].submit();
}
function continueto()
{
document.forms[0].action='QMSReportController?Operation=buyratesExpiryReport';
document.forms[0].submit();

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
	document.forms[0].action = "QMSReportController?Operation="+Operation+"&subOperation=BuyRatesPageBrowse&SortBy="+sortBy+"&SortOrder="+sortOrder+"&PageNo="+pageNo;
	//alert(document.forms[0].action);
    document.forms[0].submit();
}
</script>

  <body>
	<form method='POST' action='' onSubmit=''>
	  <table width="100%" border="0" cellspacing="0" cellpadding="0">
	    <tr valign="top" bgcolor="#FFFFFF">
		  <td>
			<table width="100%" cellpadding="4" cellspacing="1">
			  <tr valign="top" class='formlabel'>
			    <td colspan="3"><table width="790" border="0" ><tr class='formlabel'>
			      <td>Buy Rates Expiry  - View Module </td>
			      <td align=right>
						</td></tr></table></td><td align=right>QS1012511</td>
			  
			  </tr>
		    </table>
			  
        <table width="100%"  cellspacing="1" cellpadding="4">
          <tr valign="top" class='formdata'>
			    <td colspan=4>&nbsp;
				</td><td></td>
				
		  </tr>
			  <tr valign="top" class='formdata'>
			    
            <td>Terminal Id:<br>
              <%=loginbean.getTerminalId()%>
				</td>
				
            <td>User:<br>
             <%=loginbean.getUserId()%>
				</td>
				
            <td> Date:<br>
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
<%
			if(mainDataList!=null && (dataList!=null && dataList.size()>0))
			{
%>			
			<table width="100%"  cellspacing="1" cellpadding="4">
			  <tr valign="top" class='formheader'>			   
			    <td NOWRAP><A href='###' onClick='fetchSortedPageData("FromCountry","1","lable")' onmouseover='status = "Sort by From Country as";return true;' onmouseout="status = '';return true;" title="Sort by From Country as">
				From&nbsp;Country<%  if(sortBy.equalsIgnoreCase("FromCountry")){%>					
				      <img SRC=<%=imagePath%> border="0">
				 <%}%> 
				</A></td>
			    <td NOWRAP><A href='###' onClick='fetchSortedPageData("FromLocation","1","lable")' onmouseover='status = "Sort by From Location as";return true;' onmouseout="status = '';return true;" title="Sort by From Location as">
				From&nbsp;Location<%  if(sortBy.equalsIgnoreCase("FromLocation")){%>					
				      <img SRC=<%=imagePath%> border="0">
				 <%}%></A></td>
			    <td NOWRAP><A href='###' onClick='fetchSortedPageData("ToCountry","1","lable")' onmouseover='status = "Sort by To Country as";return true;' onmouseout="status = '';return true;" title="Sort by To Country as">
				To&nbsp;Country<%  if(sortBy.equalsIgnoreCase("ToCountry")){%>					
				      <img SRC=<%=imagePath%> border="0">
				 <%}%></A></td>
			    <td NOWRAP><A href='###' onClick='fetchSortedPageData("ToLocation","1","lable")' onmouseover='status = "Sort by To Location as";return true;' onmouseout="status = '';return true;" title="Sort by To Location as">
				To&nbsp;Location<%  if(sortBy.equalsIgnoreCase("ToLocation")){%>					
				      <img SRC=<%=imagePath%> border="0">
				 <%}%></A></td>
			    <td NOWRAP><A href='###' onClick='fetchSortedPageData("CarrierId","1","lable")' onmouseover='status = "Sort by Carrier Id";return true;' onmouseout="status = '';return true;" title="Sort by Carrier Id as">
				Carrier&nbsp;Id<%  if(sortBy.equalsIgnoreCase("CarrierId")){%>					
				      <img SRC=<%=imagePath%> border="0">
				 <%}%></A></td>
				<td NOWRAP><A href='###' onClick='fetchSortedPageData("ServiceLevel","1","lable")' onmouseover='status = "Sort by Service Level as";return true;' onmouseout="status = '';return true;" title="Sort by Service Level as">
				Service&nbsp;Level<%  if(sortBy.equalsIgnoreCase("ServiceLevel")){%>					
				      <img SRC=<%=imagePath%> border="0">
				 <%}%></A></td>
			    <td NOWRAP><A href='###' onClick='fetchSortedPageData("CreatedDate","1","lable")' onmouseover='status = "Sort by Created Date";return true;' onmouseout="status = '';return true;" title="Sort by Created Date">
				Created&nbsp;Date<%  if(sortBy.equalsIgnoreCase("CreatedDate")){%>					
				      <img SRC=<%=imagePath%> border="0">
				 <%}%></A></td>
			    <td NOWRAP><A href='###' onClick='fetchSortedPageData("EffectiveFrom","1","lable")' onmouseover='status = "Sort by Effective From";return true;' onmouseout="status = '';return true;" title="Sort by Effective From">
				Effective&nbsp;From<%  if(sortBy.equalsIgnoreCase("EffectiveFrom")){%>					
				      <img SRC=<%=imagePath%> border="0">
				 <%}%></A></td>
			    <td NOWRAP><A href='###' onClick='fetchSortedPageData("ValidUpto","1","lable")' onmouseover='status = "Sort by Valid Upto";return true;' onmouseout="status = '';return true;" title="Sort by Valid Upto">
				Valid&nbsp;Upto<%  if(sortBy.equalsIgnoreCase("ValidUpto")){%>					
				      <img SRC=<%=imagePath%> border="0">
				 <%}%></A></td>
				<td NOWRAP><%=label%></td>                
			  </tr>

<%				
			for(int i=0;i<dataList.size();i++)
				{
					ReportDetailsDOB detailsDob=(ReportDetailsDOB)dataList.get(i);
%>

			<tr valign="top" class='formdata'>
			
            <td><%=detailsDob.getFromCountry()!=null?detailsDob.getFromCountry():"-"%></td>
		    <td><%=detailsDob.getFromLocation()!=null?detailsDob.getFromLocation():"-"%></td>
            <td ><%=detailsDob.getToCountry()!=null?detailsDob.getToCountry():"-"%></td>
  		    <td><%=detailsDob.getToLocation()!=null?detailsDob.getToLocation():"-"%></td>
 		    <td><%=detailsDob.getCarrierId()!=null?detailsDob.getCarrierId():"-"%></td>
			<td><%=detailsDob.getServiceLevel()!=null?detailsDob.getServiceLevel():"-"%></td>
			<td><%=detailsDob.getCreateDate()!=null?dateutil.getDisplayString(detailsDob.getCreateDate()):""%></td> 			
		    <td><%=detailsDob.getEffectiveFrom()!=null?dateutil.getDisplayString(detailsDob.getEffectiveFrom()):""%></td>
		    <td><%=detailsDob.getValidUpto()!=null?dateutil.getDisplayString(detailsDob.getValidUpto()):""%></td>
			<td><%=detailsDob.getExpiryinDays()%></td>
			 
		</tr>
<%
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
			  <tr valign="top" class='denotes'>
			    <td colspan="5"><font color="#ff0000">*</font> Denotes Mandatory </td>
				<INPUT TYPE="hidden" name='Operation' value="<%=request.getParameter("Operation")%>">
				<INPUT TYPE="hidden" name='subOperation' value="<%=request.getParameter("subOperation")%>">
				<INPUT TYPE="hidden" name='sortedOrder' value="<%=sortOrder%>">
				<INPUT TYPE="hidden" name='sortedBy' value="<%=sortBy%>">
				<INPUT TYPE="hidden" name='pageNo' value="<%=pageNo%>">
			    <td colspan="4" align="right"><input type='button' class='input'  name='b1' onClick='continueto()' value='Continue'></td>
			    </tr>
			</table>

		  
 <%		}%>
				
          </td>
	    </tr>
	  </table>
	  
	</form>
  </body>
</html>
<%
		}
		else
		{
			errorMessageObject = new ErrorMessage(  "No records found","QMSReportController?Operation=buyratesExpiryReport"); 
			keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
			keyValueList.add(new KeyValue("Operation","Add")); 	
			errorMessageObject.setKeyValueList(keyValueList);
			request.setAttribute("ErrorMessage",errorMessageObject);
			%>
			<jsp:forward page="../ESupplyErrorPage.jsp" />
			<%
		 }
		 }
		catch(Exception e)
		{
			e.printStackTrace();
			//Logger.error(FILE_NAME,"Error in BuyRatesExpiryReport.jsp "+e);
      logger.error(FILE_NAME+"Error in BuyRatesExpiryReport.jsp "+e);
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
