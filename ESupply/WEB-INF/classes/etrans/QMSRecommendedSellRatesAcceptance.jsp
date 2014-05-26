<%--

	Copyright (c) 1999-2001 by FourSoft, Pvt Ltd. All Rights Reserved.
	This software is the proprietary information of FourSoft, Pvt Ltd.
	Use is subject to license terms.
	esupply - v 1.x.
	Program	Name	: QMSRecommendedSellRatesAcceptance.jsp
	Product Name	: QMS
	Module Name		: Recommended Sell Rate
	Task		    : Adding/View/Modify/Invalidate Recommended Sell Rate
	Date started	: 26-07-2005 	
	Date Completed	: 
	Date modified	:  
	Author    		: Madhusudhan Reddy .Y
	Description		: The application "Report" Recommended Sell Rate
	Actor           :
	Related Document: CR_DHLQMS_1004

--%>
<%@page import =	"	com.foursoft.esupply.common.java.FoursoftConfig,
						java.util.ArrayList,
						java.util.HashMap,
						org.apache.log4j.Logger,
						com.foursoft.esupply.common.java.ErrorMessage,
						com.foursoft.esupply.common.java.KeyValue,
						com.qms.operations.sellrates.java.QMSSellRatesDOB,
						com.foursoft.esupply.common.util.ESupplyDateUtility "%>
<%!
  private static Logger logger = null;
	private static final String FILE_NAME="QMSRecommendedSellRatesAcceptance.jsp";
%>
<jsp:useBean id = "loginbean" class = "com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope = "session"/>
<%
  logger  = Logger.getLogger(FILE_NAME);	
	ArrayList			listValues						=	null;
	String				operation						=	request.getParameter("Operation");
	
	String				dateFormat						=	loginbean.getUserPreferences().getDateFormat();
	ESupplyDateUtility	eSupplyDateUtility				=	new ESupplyDateUtility();
	ArrayList			keyValueList					=   new ArrayList();
	ErrorMessage		errorMessageObject				=   null;
	ArrayList			accList							=	null;
	ArrayList			dobList							=	null;
	QMSSellRatesDOB		headerSellRatesDOB				=	null;
	QMSSellRatesDOB		dtlSellRatesDOB					=	null;
	int					noofRecords						=	0;
	int					noofPages						=	0;
	String				displayLable					=	"";
	HashMap				mapValue						=	null;
    String              sortBy                          =   request.getParameter("SortBy");
	String              sortOrder                       =   request.getParameter("SortOrder");
	String              pageNo                          =   request.getParameter("PageNo");
	String       imagePath     = "";
	if("ASC".equalsIgnoreCase(sortOrder))
	  imagePath = request.getContextPath()+"/images/asc.gif";
	else
	  imagePath = request.getContextPath()+"/images/desc.gif";

	int                 noOfSegments    =	0;
	try
	{
		noOfSegments				=   Integer.parseInt(loginbean.getUserPreferences().getSegmentSize());
		listValues					=	(ArrayList)session.getAttribute("HeaderValues");
		mapValue					=	(HashMap)session.getAttribute("HashList");
		if(mapValue==null)
			mapValue	=	new HashMap();
		if(listValues!=null && listValues.size()>0)
		{
			headerSellRatesDOB		=	(QMSSellRatesDOB)listValues.get(0);
			accList					=	(ArrayList)listValues.get(1);
			dobList					=	(ArrayList)accList.get(0);
			Integer  no_ofRecords   =   (Integer)accList.get(1);
			Integer  no_ofPages		=	(Integer)accList.get(2);

			noofRecords				=	no_ofRecords.intValue();
			noofPages				=	no_ofPages.intValue();			
		}
		if("2".equals(headerSellRatesDOB.getShipmentMode()))
			displayLable	=	"consoleType";
		else if("4".equals(headerSellRatesDOB.getShipmentMode()))
			displayLable	=	"consoleTypes";
		int noofPage	=	Integer.parseInt(headerSellRatesDOB.getPageNo());		
%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=windows-1252">
<meta name="GENERATOR" content="Microsoft FrontPage 4.0">
<meta name="ProgId" content="FrontPage.Editor.Document">
<title>Sell Rates Acceptance</title>
<%@ include file="../ESEventHandler.jsp" %>
<link rel="stylesheet" href="ESFoursoft_css.jsp">
<STYLE type="text/css">
A:link { COLOR: blue; FONT-WEIGHT: none; TEXT-DECORATION: none }
A:hover { COLOR: blue; FONT-WEIGHT: none; TEXT-DECORATION: underline }
A:visited { color: blue; font-weight: none; text-decoration: none }
</STYLE>
</head>
<script src="<%=request.getContextPath()%>/reports/TableSorting.js"></script>
<script>

function validate()
{
	var operation	='<%=operation%>';	
	var minRates=document.getElementsByName("mfValues");
	for(m=0;m<minRates.length;m++)
	{
		if(minRates[m].checked==false)
			flag=false;
		else
		{
			flag=true;
			document.forms[0].action='QMSSellRateController?Operation='+operation+'&subOperation=Insert';
			document.forms[0].submit();
			return true;
		}
	}
	if(!flag)
	{
		alert("Please select atleast on check box.");
		return false;
	}
}

/*
* This function used for select all checkboxs
*/
function selectAll(obj)
{
	if(obj.name=="select1" && document.forms[0].select1.checked)
	{
		for(var i=0;i<document.forms[0].elements.length;i++)
		{
			if(document.forms[0].elements[i].type=="checkbox")
			{
				document.forms[0].elements[i].checked=true;
			} 
		} 
	}
	else 
	{
		for(var i=0;i<document.forms[0].elements.length;i++)
		{
			if(document.forms[0].elements[i].type=="checkbox")
			{
				document.forms[0].elements[i].checked=false;
				
			}
		}
	}
}
function showModify(obj)
{	
	var cheObh		=	document.getElementsByName("mfValues");
	var butObj		=	document.getElementsByName("but");

	butObj[obj.id].disabled=true;
	cheObh[obj.id].checked=true;
	//cheObh[obj.id].disabled=true;
	var operation		=	'<%=operation%>';
	var Bars = 'directories=no, location=no,menubar=no,status=no,titlebar=no';
	var Options='scrollbar=yes,width=1600,height=400,resizable=yes';
	var Features=Bars+' '+Options;
	var Url='QMSSellRateController?Operation='+operation+'&subOperation=Modify&index='+obj.id;
	var Win=open(Url,'Doc',Features);
}
function functionCall(shipmentMode,lable,consoleType,pageNo,operation)
{
	document.forms[0].action='QMSSellRateController?shipmentMode='+shipmentMode+'&'+lable+'='+consoleType+'&pageNo='+pageNo+'&Operation='+operation+'&subOperation='+operation;
	document.forms[0].submit();
}
function fetchSortedPageData(lable,pageNo,clickFrom)
{
   var sortBy     = lable;
   var sortOrder  = "";   
   var Operation  = document.forms[0].Operation.value;
   var shipmentMode = '<%=headerSellRatesDOB.getShipmentMode()%>';
   var lable1        = '<%=displayLable%>';
   var consoleType  = '<%=headerSellRatesDOB.getConsoleType()%>';
   
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
	document.forms[0].action = "QMSSellRateController?Operation="+Operation+"&subOperation="+Operation+"&shipmentMode="+shipmentMode+"&"+lable1+"="+consoleType+"&SortBy="+sortBy+"&SortOrder="+sortOrder+"&PageNo="+pageNo;
	//alert(document.forms[0].action);
    document.forms[0].submit();
}
</script>
<body>
<form action="" method="post" name="webform">
  <table width="100%" cellpadding="4" cellspacing="0" bgcolor="#FFFFFF">
      <tr  vAlign="top" >
        <td>
          <table width="100%" cellpadding="4" cellspacing="0">
              <tr class="formlabel" vAlign="top">
                <td >UPDATED SELL RATES</td>
				<td align="right">QS1060211</td>
              </tr>
			</table>
			<table width="100%" cellpadding="4" cellspacing="0">
              <tr class="formdata" vAlign="top">
                <td height="14" colspan="4" >Terminal ID:<br><%=loginbean.getTerminalId()%></td>
                <td height="14" colspan="3" >USER ID:<br><%=loginbean.getUserId()%></td>
                <td height="14" colspan="3" >DATE:<br><%=eSupplyDateUtility.getCurrentDateString(dateFormat)%></td>
              </tr>
			 </table>
<%
	if(listValues!=null && (dobList!=null && dobList.size()>0))
	{
%>
			<!-- ID="report0" -->
			 <table  width="100%" cellpadding="4" cellspacing="1">
              <tr valign="top" class='formheader'>
                <td NOWRAP align="center">Accept<br><input type="checkbox" name="select1" onClick="selectAll(this)"></td>
                <td NOWRAP align="center">Modify</td>
                <td NOWRAP align="center"><A href='###' onClick='fetchSortedPageData("Origin","1","lable")' onmouseover='status = "Sort by Origin as";return true;' onmouseout="status = '';return true;" title="Sort by Origin as">
				Origin<%  if(sortBy.equalsIgnoreCase("Origin")){%>					
				      <img SRC=<%=imagePath%> border="0">
				 <%}%> 
				</A></td>
                <td NOWRAP align="center"><A href='###' onClick='fetchSortedPageData("OriginCountry","1","lable")' onmouseover='status = "Sort by Origin Country as";return true;' onmouseout="status = '';return true;" title="Sort by Origin Country as">
				Origin&nbsp;Country<%  if(sortBy.equalsIgnoreCase("OriginCountry")){%>					
				      <img SRC=<%=imagePath%> border="0">
				 <%}%> 
				</A></td>
                <td NOWRAP align="center"><A href='###' onClick='fetchSortedPageData("Destination","1","lable")' onmouseover='status = "Sort by Destination as";return true;' onmouseout="status = '';return true;" title="Sort by Destination as">
				Destination<%  if(sortBy.equalsIgnoreCase("Destination")){%>					
				      <img SRC=<%=imagePath%> border="0">
				 <%}%> 
				</A></td>
                <td NOWRAP align="center"><A href='###' onClick='fetchSortedPageData("DestinationCountry","1","lable")' onmouseover='status = "Sort by Destination Country as";return true;' onmouseout="status = '';return true;" title="Sort by Destination Country as">
				Destination&nbsp;Country<%  if(sortBy.equalsIgnoreCase("DestinationCountry")){%>					
				      <img SRC=<%=imagePath%> border="0">
				 <%}%> 
				</A></td>
                <td NOWRAP align="center"><A href='###' onClick='fetchSortedPageData("Carrier","1","lable")' onmouseover='status = "Sort by Carrier as";return true;' onmouseout="status = '';return true;" title="Sort by Carrier as">
				Carrier<%  if(sortBy.equalsIgnoreCase("Carrier")){%>					
				      <img SRC=<%=imagePath%> border="0">
				 <%}%> 
				</A></td>
                <td NOWRAP align="center"><A href='###' onClick='fetchSortedPageData("ServiceLevel","1","lable")' onmouseover='status = "Sort by Service Level as";return true;' onmouseout="status = '';return true;" title="Sort by Service Level as">
				Service&nbsp;Level<%  if(sortBy.equalsIgnoreCase("ServiceLevel")){%>					
				      <img SRC=<%=imagePath%> border="0">
				 <%}%> 
				</A></td>
                <td NOWRAP align="center"><A href='###' onClick='fetchSortedPageData("Frequency","1","lable")' onmouseover='status = "Sort by Frequency as";return true;' onmouseout="status = '';return true;" title="Sort by Frequency as">
				Frequency<%  if(sortBy.equalsIgnoreCase("Frequency")){%>					
				      <img SRC=<%=imagePath%> border="0">
				 <%}%> 
				</A></td>
				<!-- Modified by Mohan for Issue No.219976 on 1-11-2010 -->
                <td NOWRAP align="center">Internal Notes</td>
				 <td NOWRAP align="center">External Notes</td>
              </tr>
<%
		int accListSize	=	dobList.size();
		String	keyVlue	=	"";
		for(int i=0;i<accListSize;i++)
		{
			dtlSellRatesDOB		=	(QMSSellRatesDOB)dobList.get(i);
			keyVlue				=	dtlSellRatesDOB.getBuyRateId()+"&"+dtlSellRatesDOB.getLanNumber();
			//System.out.println("keyVluekeyVluekeyVluekeyVluekeyVluekeyVluekeyVluekeyVluekeyVluekeyVluekeyVlue :: "+keyVlue);
%>
              <tr valign="top" class='formdata'>
<%
				if(mapValue.containsKey(keyVlue))
				{
%>
					<td NOWRAP align="center"><input type="checkbox" name="mfValues" value="<%=keyVlue%>" id='<%=i%>' checked></td>
<%
				}
				else
				{
%>
					<td NOWRAP align="center"><input type="checkbox" name="mfValues" value="<%=keyVlue%>" id='<%=i%>'></td>
<%
				}
%>
				<input type="hidden" name="checkValue" value="<%=keyVlue%>">
                <td NOWRAP align="center"><input type="button" value="Modify" name="but" class="input" onClick="showModify(this)" id="<%=i%>"></td>
                <td NOWRAP align="center"><%=dtlSellRatesDOB.getOrigin()%></td>
                <td NOWRAP align="center"><%=dtlSellRatesDOB.getOriginCountry()!=null?dtlSellRatesDOB.getOriginCountry():""%></td>
                <td NOWRAP align="center"><%=dtlSellRatesDOB.getDestination()%></td>
                <td NOWRAP align="center"><%=dtlSellRatesDOB.getDestinationCountry()!=null?dtlSellRatesDOB.getDestinationCountry():""%></td>
                <td NOWRAP align="center"><%=dtlSellRatesDOB.getCarrier_id()%></td>
                <td NOWRAP align="center"><%=dtlSellRatesDOB.getServiceLevel()%></td>
                <td NOWRAP align="center"><%=dtlSellRatesDOB.getFrequency()%></td>
              <!-- @@ Commented by subrahmanyam for the wpbn id: 179985 on 19-aug-09 -->
				<!-- <td NOWRAP align="center"><input type="text" name="notes" size="20" class="text"></td> -->
				<!-- @@ Added by subrahmanyam for the wpbn id: 179985 on 19-aug-09 -->
				<!-- Modified by Mohan for Issue No.219976 on 1-11-2010 -->
                <td NOWRAP align="center"><input type="text" name="notes" size="20" class="text"  maxLength="1000" value="<%=dtlSellRatesDOB.getNotes()!=null?dtlSellRatesDOB.getNotes():""%>"></td>
				 <td NOWRAP align="center"><input type="text" name="extNotes" size="20" class="text"  maxLength="1000" value="<%=dtlSellRatesDOB.getExtNotes()!=null?dtlSellRatesDOB.getExtNotes():""%>"></td>
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
					if(request.getParameter("PageNo")!=null)
						currentPageNo =Integer.parseInt(request.getParameter("PageNo"));

					if(currentPageNo != 1)
					{
%>
						<a href="#" onClick='fetchSortedPageData("<%=sortBy%>","<%=currentPageNo-1%>","pageNo")'><img src="images/Toolbar_backward.gif"  alt="Previous Page"></a>
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
							<a href="###" onClick='fetchSortedPageData("<%=sortBy%>","<%=(i + 1)%>","pageNo")' ><%=(i + 1)%></a>&nbsp;

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
              <tr class="text" vAlign="top">
                <td height="1" valign="middle" colspan="9" >
                </td>
                <td height="1" valign="middle" align="right" >
					<INPUT TYPE="hidden" name='Operation' value="<%=request.getParameter("Operation")%>">
					<INPUT TYPE="hidden" name='subOperation' value="<%=request.getParameter("Operation")%>">
					<INPUT TYPE="hidden" name='sortedOrder' value="<%=sortOrder%>">
					<INPUT TYPE="hidden" name='sortedBy' value="<%=sortBy%>">
					<INPUT TYPE="hidden" name='pageNo' value="<%=pageNo%>">
					<input type="button" value="Submit" class="input" onClick="return validate()">
                </td>
              </tr>
          </table>

<%
	}
	else if(listValues!=null && (dobList!=null && dobList.size()==0))
	{
System.out.println("madhu");
%>
		<table width="100%"   border="0" cellpadding="4" cellspacing="1">
			<tr class='formdata'> 
				<td colspan="6" align="center">
					<b>No Rates Are Defined for the Specified Details.<b>
				</td>
			</tr>
		</table>
		<table width="100%" cellpadding="4" cellspacing="0">
              <tr class="text" vAlign="top">
                <td height="1" valign="middle" colspan="9" >
                </td>
                <td height="1" valign="middle" align="right" >
                  <input type="button" value="BACK" class="input" onClick="window.history.back(-1)">
                </td>
              </tr>
          </table>
<%
	}
%>
        </td>
      </tr>
  </table>
</form>
</body>
</html>
<%
		}
		catch(Exception e)
		{
			e.printStackTrace();
			//Logger.error(FILE_NAME,"Error in QMSRecommendedSellRatesAcceptance.jsp "+e);
      logger.error(FILE_NAME+"Error in QMSRecommendedSellRatesAcceptance.jsp "+e);
			errorMessageObject = new ErrorMessage(  "Error while retreiving the details  ","QMSSellRateController?Operation="+operation+"&subOperation=Enter"); 
			keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
			keyValueList.add(new KeyValue("Operation","Add")); 	
			errorMessageObject.setKeyValueList(keyValueList);
			request.setAttribute("ErrorMessage",errorMessageObject);
%>
			<jsp:forward page="ESupplyErrorPage.jsp" />
<%
		}
%>
