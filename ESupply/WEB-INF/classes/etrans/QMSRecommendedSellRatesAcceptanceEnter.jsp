<%--

	Copyright (c) 1999-2001 by FourSoft, Pvt Ltd. All Rights Reserved.
	This software is the proprietary information of FourSoft, Pvt Ltd.
	Use is subject to license terms.
	esupply - v 1.x.
	Program	Name	: QMSRecommendedSellRatesAcceptanceEnter.jsp
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
						org.apache.log4j.Logger,
						com.foursoft.esupply.common.java.ErrorMessage,
						com.foursoft.esupply.common.java.KeyValue,
						com.qms.operations.sellrates.java.QMSSellRatesDOB,
						com.foursoft.esupply.common.util.ESupplyDateUtility "%>
<%!
  private static Logger logger = null;  
	private static final String FILE_NAME="QMSRecommendedSellRatesAcceptanceEnter.jsp";
%>
<jsp:useBean id = "loginbean" class = "com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope = "session"/>
<%
  logger  = Logger.getLogger(FILE_NAME);	
	String operation	=	request.getParameter("Operation");
	String				origin					=	null;
	//System.out.println("operationoperationoperationoperationoperation ::: "+operation);
%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=windows-1252">
<meta name="GENERATOR" content="Microsoft FrontPage 4.0">
<meta name="ProgId" content="FrontPage.Editor.Document">
<title>Recommended Sell Rates Acceptance Enter</title>
<%@ include file="../ESEventHandler.jsp" %>
<link rel="stylesheet" href="ESFoursoft_css.jsp">
</head>
<script>
function showLocationLOV(toSet)
{
	//alert("function");
	var Bars = 'directories=no, location=no,menubar=no,status=no,titlebar=no';
	var Options='scrollbar=yes,width=700,height=300,resizable=no';
	
	var Features=Bars+' '+Options;
	var searchString = (toSet=='origin')?(document.forms[0].origin.value):"";
	//alert(searchString);
	
	

	var Url='etrans/ETCLOVLocationIds1.jsp?wheretoset='+toSet+'&searchString='+searchString;
	var Win=open(Url,'Doc',Features);
}
function trimAll(obj) 
{
	var sString = obj.value;
	while (sString.substring(0,1) == ' ')
	{
		sString = sString.substring(1, sString.length);
	}
	while (sString.substring(sString.length-1, sString.length) == ' ')
	{
		sString = sString.substring(0,sString.length-1);
	}
	obj.value = sString;
}


function showLocationValues(obj,where)
{
	var data="";
	for( i=0;i<obj.length;i++)
	{
		firstTemp	=obj[i].value;
		firstIndex	=	firstTemp.indexOf(0);
		if(where=="serviceLevelId")
			lastIndex	=	firstTemp.indexOf(' [');	
		else
			lastIndex	=	firstTemp.indexOf('[');
		firstTemp	=	firstTemp.substring(0,lastIndex);
		temp		=   firstTemp.toString();
		secondTemp	=obj[i].value;
		lastIndex1	=	secondTemp.lastIndexOf('[')+1;
		lastIndex2	=	secondTemp.lastIndexOf(']');	
		temp1		=	secondTemp.substring(lastIndex1,lastIndex2);
		if(where=="originCountry"||where=="destinationCountry")
		{
			if(data!="")
			data=data+","+temp1;
		else
			data=temp1;
		}
		else
		{
			if(data!="")
				data=data+","+temp;
			else	
				data=temp;
		}
	}
	document.getElementById(where).value=data;
}








function showHide()
{
	//document.forms[0].shipmentMode.focus();
	var data="";
	var index	=	document.forms[0].shipmentMode.value;
	if(index=="2")
	{
		 data='Console&nbsp;Type:<font  color=#ff0000>*</font><br><select size="1" name="consoleType" class="select" ><option  value="LCL">LCL</option><option  value="FCL" >FCL</option></select>';
	}
	else if(index=="4")
	{
		data='Console&nbsp;Type:<font  color=#ff0000>*</font><br><select size="1" name="consoleTypes" class="select" ><option  value="LTL">LTL</option><option  value="FTL">FTL</option></select>';
	}
	if( document.layers)
	{
		document.layers.cust1.document.write(data);
		document.layers.cust1.document.close();
	}
	else
	{
		if(document.all)
		{
		   cust1.innerHTML = data;
		 }
	 }
}
</script>
<body onLoad="showHide();">
<form action="QMSSellRateController" method="post" name="webform" >
 <table width="100%" cellpadding="4" cellspacing="0">
    <tbody>
      <tr  vAlign="top">
        <td width="900">
          <table width="100%" cellpadding="4" cellspacing="0">
              <tr class="formlabel" vAlign="top">
                <td >
                  UPDATED SELL RATES
                </td>
				<td align="right">QS1060211</td>
              </tr>
			 </table>
			  <table width="100%" cellpadding="4" cellspacing="0">
              <tr class="text" vAlign="top">
                <td colspan="2"  height="15" width="691"></td>
              </tr>
              <tr class="formdata" vAlign="top">
                <td valign="middle" >Shipment Mode: <select size="1" name="shipmentMode" onChange="showHide();">
                    <option selected value="1">Air</option>
                    <option value="2">Sea</option>
                    <option value="4">Truck</option>
                  </select>
				</td>

<td>
					Origin:<input type="text" name="origin" value='<%=origin!=null?origin:""%>' class='text' size="8" onblur='trimAll(this);this.value=this.value.toUpperCase()' maxlength='100'>&nbsp;<input type="button" value="..." name="originLOV"  class='input' onclick='showLocationLOV("origin")'>
				</td>



				<td>
				  <div id='cust1' style='position:relative;'></div>
				</td>
              </tr>
			</table>
			 <table width="100%" cellpadding="4" cellspacing="0">
              <tr class="text">
                <td align="right">
				<input type="hidden" name="Operation" value="<%=operation%>">
				<input type="hidden" name="subOperation" value="<%=operation%>">
				<input type="hidden" name="PageNo" value="1">
				<INPUT TYPE="hidden" name='SortBy' value="Origin">
				<INPUT TYPE="hidden" name='SortOrder' value="ASC">
                <input type="submit" value="Continue>" class="input">
                </td>
              </tr>
          </table>
        </td>
      </tr>
	</table>
</form>
</body>
</html>

