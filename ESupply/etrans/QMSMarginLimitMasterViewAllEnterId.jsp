
<%--
<%--

Copyright (c) 1999-2001 by FourSoft, Pvt Ltd. All Rights Reserved.
This software is the proprietary information of FourSoft, Pvt Ltd.
Use is subject to license terms.
esupply - v 1.x.
Program	Name	: QMSMarginLimitMasterViewAllEnterId.jsp
Product Name	: QMS
Module Name		: Industry Registration
Task		    : Adding/View/Modify/Delete/Invalidate Industry
Date started	: 16-06-2005 	
Date Completed	: 22-06-2005 with all the validations
Date modified	:  
Author    		: I.V.Sekhar Merrinti
Description		: The application "Adding/View/Modify/Delete/Invalidate" Industry information
Actor           :
Related Document: CR_DHLQMS_1002
--%>
<jsp:useBean id = "loginbean" class = "com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope = "session"/>
<%!
	private static final String FILE_NAME="QMSMarginLimitMasterViewAllEnterId.jsp";
%>
<%
	String operation	=	request.getParameter("Operation");
	String manditory	=	"";
	String multiple		=	"";
	if(operation!=null && (operation.equals("ViewAll") || operation.equals("Invalidate")))
	{
		manditory		=	"*";
		multiple		=	"multiple";
	}
%>
<html>
<head>
<title>CarrierRegistration Add</title>       <%@ include file="../ESEventHandler.jsp" %>
<link rel="stylesheet" href="../ESFoursoft_css.jsp">
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<script src ="../html/eSupply.js"></script>
<script >

	var Win  = null;
	function mandatory()
	{
		/*if(marginlimitmaster.levelId.value=='')
		{
			alert("Pls,Select the levelId Id");
			marginlimitmaster.b1.focus();
			return false;
		}
		if(marginlimitmaster.serviceLevelId.value=='')
		{
			alert("Pls,Select the Service levelId Id");
			marginlimitmaster.b2.focus();
			return false;
		}*/
		if(marginlimitmaster.submit!=null)
		{
			marginlimitmaster.submit.disabled	=true;
		}
		if(marginlimitmaster.reset!=null)
		{
			marginlimitmaster.reset.disabled	=true;
		}
		return true;
	}
	function openLevelIdLOV()
	{
		var searchStr	=	"";
		var shipMode	=	document.forms[0].shipmentmode.value;
		var consoleType	=	"";
		var slevelId		=	document.forms[0].serviceLevelId;
		var slevelIdStr	=	"";
		if(document.forms[0].ChargeType.value=="Freight")
		{
			if(document.forms[0].consoletype!=null)
			{
				consoleType	=	document.forms[0].consoletype.value;
			}
			for(var i=0;i<slevelId.options.length;i++)
			{
				if(slevelId.options[i].selected)
				{
					slevelIdStr	=	slevelIdStr+slevelId.options[i].value+"$";
				}
			}
		}
		var Url			= "QMSLOVLevelIds.jsp?Operation=<%=operation%>&searchString="+searchStr+"&shipmentMode="+shipMode+"&consoleType="+consoleType+"&selection=multiple&slevelId="+slevelIdStr+"&fromwhere=marginlimitsView";
		showLOV(Url);
	}
	function openSLevelIdLOV()
	{
		var searchStr	=	"";
		var shipMode	=	document.forms[0].shipmentmode.value;
		var consoleType	=	"";
		var levelId		=	document.forms[0].levelId;
		var levelIdStr	=	"";
		if(document.forms[0].consoletype!=null)
		{
			consoleType	=	document.forms[0].consoletype.value;
		}
		for(var i=0;i<levelId.options.length;i++)
		{
			if(levelId.options[i].selected)
			{
				levelIdStr	=	levelIdStr+levelId.options[i].value+"$";
			}
		}
		var Url			= "ETCLOVServiceLevelIds.jsp?Operation=<%=operation%>&searchString="+searchStr+"&shipmentMode="+shipMode+"&consoleType="+consoleType+"&selection=multiple&levelId="+levelIdStr+"&fromwhere=marginlimitsView";
		showLOV(Url);
	}
	function setServiceIdValues(values)
	{
		var sLevelId	=	document.forms[0].serviceLevelId;
		sLevelId.options.length	=	0;
		for(var i=0;i<values.length;i++)
			sLevelId.options[sLevelId.options.length]=new Option(values[i],values[i]);

	}
	function setLevelIdValues(values)
	{
		var levelId	=	document.forms[0].levelId;
		levelId.options.length	=	0;
		for(var i=0;i<values.length;i++)
			levelId.options[i]=new Option(values[i],values[i]);
	}
	function selectAll()
	{
		var levelId	=	document.forms[0].levelId;
		var sLevelId	=	document.forms[0].serviceLevelId;
	 	if(levelId.selectedIndex<0)
		{
			for(var i=0;i<levelId.options.length;i++)
				levelId.options[i].selected	=	true;
		}
		if(sLevelId.selectedIndex<0)
		{
				for(var i=0;i<sLevelId.options.length;i++)
					sLevelId.options[i].selected	=	true;
		}
		
	}
	function consoleType()
	{
		
		var shipmentmode	=	document.forms[0].shipmentmode.options[document.forms[0].shipmentmode.selectedIndex].value;
		var	data	="";
			data	="ConsoleType:"
					+"<select name ='consoletype' class ='select'>";
			if(shipmentmode=='2')
			{
				data	=data+"<option value=''>Both</option>"
						+"<option value='LCL'>LCL</option>"
						+"<option value='FCL'>FCL</option>";
			}else if(shipmentmode=='4')
			{
				data	=data+"<option value=''>Both</option>"
						+"<option value='LTL'>LTL</option>"
						+"<option value='FTL'>FTL</option>";
			}

			data	=data+"</select>";
			if(shipmentmode=='1' || shipmentmode=='')
				data	="";
			if (document.layers )
			{
				document.layers.consoletype.document.write(data);
				document.layers.consoletype.document.close();
			}
			else
			{
				if (document.all )
				{
					consoletype.innerHTML = data;
				}
			}
			resetValues();
	}
	function resetValues()
	{
		var levelId		=	document.forms[0].levelId;
		var sLevelId	=	document.forms[0].serviceLevelId;
		levelId.options.length	=	0;
		sLevelId.options.length	=	0;
	}
function changeShipmentMode()
{
  if(document.forms[0].ChargeType.options[document.forms[0].ChargeType.selectedIndex].value=='Freight')
	{
	  document.getElementById("mode").innerHTML='ShipmentMode:<select name="shipmentmode" class="select" onChange="consoleType()"><option value="">All</option><option value="1">Air</option><option value="2">Sea</option> <option value="4">Truck</option> </select>';

	  document.getElementById("service").innerHTML='<b>Service Level Id:</b><br><select name="serviceLevelId" multiple class="select" size="5" onKeyPress=""></select><input type="button" name="b2" class ="input" value ="..." Onclick ="openSLevelIdLOV()">';
	}
   else
	{  
	  document.getElementById("mode").innerHTML='<input type="hidden" name="shipmentmode" value="0">';
	 // document.getElementById("console").innerHTML='';
	  document.getElementById("service").innerHTML='';
	}
}

</script>
</head>
<body onLoad="changeShipmentMode()">
<form name="marginlimitmaster" method="post" action="QMSMarginLimitMaster.jsp" onSubmit ='selectAll();return mandatory();'>
<table width="800" border="0" cellspacing="0" cellpadding="0">
    <tr valign="top" bgcolor="#FFFFFF">
      <td>
        <table border="0" width="800" cellpadding="4" cellspacing="1">
          <tr valign="top" class="formlabel">
            <td >
				<table border="0" width="790" >
					<tr valign="top" class="formlabel">
						<td> Margin Limit Master - <%=operation%> </td>
						<td align=right>QS1010421
						</td></tr></table>
			</td>
		  </tr>
		</table>
		<table border='0' width="800" >
		 <tr class='formdata'>
		   <td width='40%'>Charge Type:
			<select name='ChargeType' class='select' onChange='changeShipmentMode()'>
		   <option value='Charges'>Charges</option>
		   <option value='Freight'>Freight</option>
		   <option value='Cartages'>Cartages</option>
		   </select></td>
		   <td width='40%'><span id='mode'></span></td>
		  
		   <td width='50%'>
		   <span id='consoletype'></span></td>
		 </tr>
		</table>
		<table border='0' width="800" cellpadding="0" cellspacing="1">
			<tr class='formdata'>
				<td align ='left' width="25"><td>
				<td align ='left' width="100"><b>Level Id:</b><br>
				<select name='levelId' multiple class='select' size='5' onKeyPress	="">
				</select>
				<input type	='button' name='b1' class ='input' value ="..." Onclick ="openLevelIdLOV()"></td>
			<td width='40%'><span id='service'></span></td>
				<td align ='left' width="525"><td>
			</tr>
		</table>
		<table border="0" width="800" cellpadding="4" cellspacing="1">
          <tr class='denotes'>
            <td valign="top" >
              <font color="#FF0000">*</font>Denotes Mandatory
            </td>
            <td valign="top" align="right">
                <input type="submit" value="Submit" name="submit" class="input">
				<input type="reset" value="Reset" name="reset" class="input" onClick="resetValues()">
				<input type='hidden' name='Operation' value='<%=operation%>'> 
		     </td>
          </tr>
        </table>
	  </td>	
    </tr>
</table>
</form>
</body>
</html>