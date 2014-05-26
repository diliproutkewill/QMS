<%--

	Copyright (c) 1999-2001 by FourSoft, Pvt Ltd. All Rights Reserved.
	This software is the proprietary information of FourSoft, Pvt Ltd.
	Use is subject to license terms.
	esupply - v 1.x.
	Program	Name	: QMSCartageAcceptanceEnter.jsp
	Product Name	: QMS
	Module Name		: Cartage Acceptence
	Task		    : 
	Date started	: 26-07-2005 	
	Date Completed	: 
	Date modified	:  
	Author    		: RamaKrishna .Y
	Description		: The application "Report" Cartage Sell Charge
	Actor           :
	Related Document: CR_DHLQMS_1003

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
	private static final String FILE_NAME="QMSCartageAcceptanceEnter.jsp";
%>
<jsp:useBean id = "loginbean" class = "com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope = "session"/>
<%
	logger  = Logger.getLogger(FILE_NAME);	
	String operation	=	request.getParameter("Operation");
	//Logger.info(FILE_NAME,"loginbean.getAccessType()::: "+loginbean.getAccessType());
%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=windows-1252">
<title>New Page 1</title>
<link rel="stylesheet" href="ESFoursoft_css.jsp">
</head>
<script>
function showLOV(toSet)
{
	var Bars = 'directories=no, location=no,menubar=no,status=no,titlebar=no';
	var Options='scrollbar=yes,width=700,height=360,resizable=no';
	var Features=Bars+' '+Options;
	
	var searchString = document.forms[0].locationId.value;
	var searchString2= "";

	var Url='etrans/ETCLOVLocationIds1.jsp?wheretoset='+toSet+'&searchString='+searchString+'&searchString2='+searchString2+'&Operation=<%=operation%>';
	var Win=open(Url,'Doc',Features);
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
		firstTemp	=	firstTemp.substring(firstIndex,lastIndex);
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
function changeConsoleVisibility()
{
	var console	 = document.getElementById('console');
	var weightBreak = document.getElementById('weightBreak');
	
	if(document.forms[0].shipmentMode.value=='2')
		console.style.display="block";
	else
	{
		console.style.display="none";
		document.forms[0].consoleType.options[0].selected = true;
	}
}
function toUpperCase(obj)
{
  obj.value = obj.value.toUpperCase();
}
function chrnum(input)
{
	s = input.value;
	var filteredValues;

	filteredValues = "''~!@#$%^&*()+=|\:;<>./?-";

	var i;
	var returnString = "";
	var flag = 0;
	for (i = 0; i < s.length; i++)
	{
		var c = s.charAt(i);
		if ( filteredValues.indexOf(c) == -1 )
				returnString += c.toUpperCase();
		else
			flag = 1;
	}
	if( flag==1 )
	{
		alert("Special Characters not allowed");
		var field = document.forms[0];
		for(i = 0; i < field.length; i++)
		{
			if( field.elements[i] == input )
			{
				document.forms[0].elements[i].focus();
				break;
			}
		}
	}

	input.value = returnString;
}
</script>
<body >
<form action="QMSCartageController" method="post" name="webform" >
 <table width="100%" cellpadding="4" cellspacing="0">
    <tbody>
      <tr  vAlign="top">
        <td width="900">
          <table width="100%" cellpadding="4" cellspacing="0">
              <tr class="formlabel" vAlign="top">
                <td >
                  UPDATED BUY CHARGES
                </td>
				<td align="right">QS1050810</td>
              </tr>
			 </table>
			  <table width="100%" cellpadding="4" cellspacing="0">
              <tr class="formdata" vAlign="top">
			 <td>Shipment Mode:<br>
				<select name="shipmentMode" class='select' onchange='changeConsoleVisibility()'>
					<option value="1">Air</option>
					<option value="2">Sea</option>
			  </td>
			   <td>
				<DIV id="console" style="DISPLAY:none">
					Console Type:</br>
					<select name="consoleType" class='select'>
						<option value="LCL">LCL</option>
						<option value="FCL">FCL</option>
					</select>
				</DIV>
			  </td>
                <td valign="middle" >Charge Type:<br> <select size="1" name="chargeType" class='select'>
                    <option selected value="Pickup">Pickup</option>
                    <option value="Delivery">Delivery</option>
                    <option value="Both">Both</option>
                  </select>
				</td>
				<td>
				  Location Id: <br><input type="text" name="locationId" maxLength='30' size='15' onBlur='chrnum(this)' class='text'>&nbsp;<input type="button" class='input' name="locationIdLOV" value='...' onClick='showLOV("locationId")'>
				</td>
              </tr>
			</table>
			 <table width="100%" cellpadding="4" cellspacing="0">
              <tr class="text">
                <td align="right">
				<input type="hidden" name="Operation" value="cartageAccept">
				<input type="hidden" name="subOperation" value="Details">
				<input type="hidden" name="pageNo" value="1">
                <input type="submit" value="Continue>>" class="input">
                </td>
              </tr>
          </table>
        </td>
      </tr>
	</table>
</form>
</body>
</html>

