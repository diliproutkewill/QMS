
<%--
<%--

Copyright (c) 1999-2001 by FourSoft, Pvt Ltd. All Rights Reserved.
This software is the proprietary information of FourSoft, Pvt Ltd.
Use is subject to license terms.
esupply - v 1.x.
Program	Name	: QMSIndustryRegistrationEnterId.jsp
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
	private static final String FILE_NAME="QMSMarginLimitMasterEnterId.jsp";
%>
<%
	String operation	=	request.getParameter("Operation");	
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
		if(marginlimitmaster.levelId.value=='')
		{
			alert("Please Select the levelId");
			marginlimitmaster.b1.focus();
			return false;
		}
		
		if(document.marginlimitmaster.ChargeType.options[document.marginlimitmaster.ChargeType.selectedIndex].value=='Freight')
		{
		if(marginlimitmaster.serviceLevelId.value=='')
		{
			alert("Please Select the Service levelId");
			marginlimitmaster.b2.focus();
			return false;
		}
		}
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
		
		var searchStr	=	document.forms[0].levelId.value;
		var chgType     =   document.forms[0].ChargeType.options[document.forms[0].ChargeType.selectedIndex].value;
		var consoleType =   "";
		
		if(chgType=='Freight')
		{
		       var shipmentMode=document.forms[0].shipmentmode.options[document.forms[0].shipmentmode.selectedIndex].value;
               if(shipmentMode!='1')
		       consoleType=document.forms[0].consoletype.options[document.forms[0].consoletype.selectedIndex].value;
		}
		else
        var shipmentMode='0';

		var Url			= "QMSLOVLevelIds.jsp?Operation=<%=operation%>&searchString="+searchStr+"&name=levelId&selection=single&fromwhere=marginlimitsView&flag=marginLimts&chargeType="+chgType+"&shipmentMode="+shipmentMode+"&consoleType="+consoleType;
		showLOV(Url);
	}
	function openSLevelIdLOV()
	{
		var searchStr	=	document.forms[0].serviceLevelId.value;
		var shipMode	=	document.forms[0].shipmentmode.value;
		var levelId	=	document.forms[0].levelId.value;
		var consoleType	=	"";
		if(document.forms[0].consoletype!=null)
		{
			consoleType	=	document.forms[0].consoletype.value;
		}
		var Url			= "ETCLOVServiceLevelIds.jsp?Operation=<%=operation%>&searchString="+searchStr+"&selection=single&levelId="+levelId+"&fromwhere=marginlimitsView&shipmentMode="+shipMode;
		showLOV(Url);
	}
	function consoleType()
	{

		var shipmentmode	=	document.forms[0].shipmentmode.options[document.forms[0].shipmentmode.selectedIndex].value;
		
		var	data	="";
			data	="ConsoleType:"
					+"<select name ='consoletype' class ='select'>";
			if(shipmentmode=='2')
			{
				data	=data+"<option value='LCL'>LCL</option>"
						+"<option value='FCL'>FCL</option>";
			}else if(shipmentmode=='4')
			{
				data	=data+"<option value='LTL'>LTL</option>"
						+"<option value='FTL'>FTL</option>";
			}

			data	=data+"</select>";
			if(shipmentmode=='1')
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
	}
	function chargeTypeChange()
	{
	  var chargeSelection=document.forms[0].ChargeType.options[document.forms[0].ChargeType.selectedIndex].value;
	  if(chargeSelection=='Freight')
	  {
	    
		document.getElementById("chaSel").innerHTML='Service Level Id:<font color="#FF0000">*</font><input type	="text" name ="serviceLevelId" size="15" maxLength="5" class ="text" value ="" onkeyPress="" onBlur="toUpperCase()"><input type="button" name="b2" class ="input" value ="..." Onclick ="openSLevelIdLOV()">';
	  }
	  else
	  {
	    document.getElementById("chaSel").innerHTML='<input type="hidden" name ="serviceLevelId" size="15" maxLength="25">';
	  }
	  
	}
	
	function changeShipmentMode()
    {
      
	  if(document.forms[0].ChargeType.options[document.forms[0].ChargeType.selectedIndex].value=='Freight')
		{
		  document.getElementById("mode").innerHTML='ShipmentMode:<select name="shipmentmode" class="select" onChange="consoleType()"><option value="1">Air</option><option value="2">Sea</option> <option value="4">Truck</option> </select>';
		  
		}
       else
        {  
	      document.getElementById("mode").innerHTML='<input type="hidden" name="shipmentmode" value="0">';
	      document.getElementById("consoletype").innerHTML='';
		}
	}
</script>
</head>
<body onLoad='chargeTypeChange()'>
<form name="marginlimitmaster" method="post" action="QMSMarginLimitMaster.jsp" onSubmit ='return mandatory()'>
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
		   <td width='40%'>ChargeType:
		   <select name='ChargeType' class='select' onChange='changeShipmentMode();chargeTypeChange()'>
		   <option value='Charges'>Charges</option>
		   <option value='Freight'>Freight</option>
		   <option value='Cartages'>Cartages</option>
		   </select></td>
		   <td width='40%'><span id='mode'></span></td>
		   <td width='20%'>
		   <span id='consoletype'></span></td>
		  </tr>
		</table>
		<table border='0' width="800" cellpadding="0" cellspacing="0">
		      <tr class='formdata'>
				<td align ='center' width="250">Level Id:<font color="#FF0000">*</font>
				<input type	='text' name ="levelId" size='15' maxLength='25' class ='text' value ='' onkeyPress=''  onBlur="specialCharFilter(this,'Service levelId');toUpperCase()">
				<input type	='button' name='b1' class ='input' value ="..." Onclick ="openLevelIdLOV()"></td>
			    <td align ="center">
			    <span id="chaSel"></span></td>	
			   </tr>
		</table>
		<table border="0" width="800" cellpadding="4" cellspacing="1">
          <tr class='denotes'>
            <td valign="top" >
              <font color="#FF0000">*</font>Denotes Mandatory
            </td>
            <td valign="top" align="right">
                <input type="submit" value="Submit" name="submit" class="input">
				<input type="reset" value="Reset" name="reset" class="input">
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