<%--

Copyright (c) 1999-2001 by FourSoft, Pvt Ltd. All Rights Reserved.
This software is the proprietary information of FourSoft, Pvt Ltd.
Use is subject to license terms.
esupply - v 1.x.
Program	Name	: ListMasterAdd.jsp
Product Name	: QMS
Module Name		: ListMasterRegistration
Task		    : Adding/View/Modify/Delete/Invalidate ListMaster
Date started	: 7 July 2005 
Date Completed	: 
Date modified	:  
Author    		: K.NareshKumarReddy
Description		: The application "Adding/View/Modify/Delete/Invalidate" ListMaster
Actor           :
Related Document: CR_DHLQMS_1002
--%>
<%@page import = "com.qms.setup.ejb.sls.SetUpSessionHome,
				  com.qms.setup.ejb.sls.SetUpSession,
		          javax.naming.InitialContext,
				  javax.naming.Context,
				  org.apache.log4j.Logger,
				  com.foursoft.esupply.common.java.ErrorMessage,
				  com.foursoft.esupply.common.java.KeyValue,
				  java.util.ArrayList,
				  javax.ejb.ObjectNotFoundException"
%>
<jsp:useBean id = "loginbean" class = "com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope = "session"/>
<%!
  private static Logger logger = null;
	private static final String FILE_NAME="ListMasterAdd.jsp";
%>
<% 
  logger  = Logger.getLogger(FILE_NAME);
	String operation=request.getParameter("Operation");
	String shipmentMode=request.getParameter("shipmentMode");
	if(shipmentMode==null)
		shipmentMode="Air";
	//System.out.println("shipmentMode:"+shipmentMode);
	

%>




<html>

<head>
<title>ListMaster</title>
<link rel="stylesheet" href="ESFoursoft_css.jsp">
<script src ="html/dynamicContent.js"></script>
<script src ="html/eSupply.js"></script>
<script>
	function initialize()
	{

		importXML('xml/listmasterreg.xml');
	}
	function validateBeforeDeletion()
	{
		return true;
	}
	function validateBeforeCreation()
	{
		return true;
	}
	function initializeDynamicContentRows()
	{
		setValues();
	}
	function setValues()
	{
		var tableObj = document.getElementById("listMasterReg");
		if(false)
		{}
		else
		{
			if(tableObj.getAttribute("idcounter")==1)
						createDynamicContentRow(tableObj.getAttribute("id"));
		}

	}
	function defaultFunction(currentRow)
	{
	}
	function defaultDeleteFunction()
	{
	}
function changeLabel()
{
  var selectedIndex=document.forms[0].shipmentMode.selectedIndex;
	if(document.forms[0].shipmentMode.options[selectedIndex].value=="Sea")
	{
		document.getElementById("RefDocLabel").innerHTML="Container&nbsp;Type:&nbsp;*";
		document.getElementById("RefDocLabel1").innerHTML="Container&nbsp;Description:";
		document.getElementById("RefDocLabel2").innerHTML="Volume:*";
		document.getElementById("RefDocLabel3").innerHTML="Laden&nbsp;Weight:";
		document.getElementById("RefDocLabel4").innerHTML="Tare Weight:";
	}
	else if(document.forms[0].shipmentMode.options[selectedIndex].value=="Air")
	{
		document.getElementById("RefDocLabel").innerHTML="ULD Type:*";
		document.getElementById("RefDocLabel1").innerHTML="ULD&nbsp;Description:";
		document.getElementById("RefDocLabel2").innerHTML="Volume:";
		document.getElementById("RefDocLabel3").innerHTML="PivotWeight:*";
		document.getElementById("RefDocLabel4").innerHTML="Over&nbsp;Pivot&nbsp;Weight:*";

	}
	else if(document.forms[0].shipmentMode.options[selectedIndex].value=="Truck")
	{
		document.getElementById("RefDocLabel").innerHTML="Container&nbsp;Type:&nbsp;*";
		document.getElementById("RefDocLabel1").innerHTML="Container&nbsp;Description:";
		document.getElementById("RefDocLabel2").innerHTML="Volume:*";
		document.getElementById("RefDocLabel3").innerHTML="Laden&nbsp;Weight:";
		document.getElementById("RefDocLabel4").innerHTML="Tare Weight:";
	}
		
}
 function toUpper(input)
 {
  input.value=input.value.toUpperCase();
 }
function mandatory()
{
	  var selectedIndex =   document.forms[0].shipmentMode.selectedIndex;
	  var type			=   document.forms[0].Type;
	  var upweight		=	document.forms[0].upweight;
	  var otweight		=	document.forms[0].otweight;
	  var volume		=	document.forms[0].volume;
	  var length		=	type.length;
	  var typeArray		=	new Array();

	  if(document.forms[0].shipmentMode.options[selectedIndex].value=="Air")
	  {
			for(var i=0;i<document.getElementsByName("Type").length;i++)
			{
				typeArray[i]	=	document.getElementsByName("Type")[i].value;
				if(document.getElementsByName("Type")[i].value.length==0)
				{
					alert("Please enter the value for Type at Row "+(i+1));
					document.getElementsByName("Type")[i].focus();
					return false;
				}
				if(document.getElementsByName("upweight")[i].value.length==0)
				{
					alert("Please enter the value for Piovot Weight at Row "+(i+1));
					document.getElementsByName("upweight")[i].focus();
					return false;
				}
				if(document.getElementsByName("otweight")[i].value.length==0)
				{
					alert("Please enter the value for  Over Pivot Weight at Row "+(i+1));
					document.getElementsByName("otweight")[i].focus();
					return false;
				}
			}

	}
	else if(document.forms[0].shipmentMode.options[selectedIndex].value=="Sea" || document.forms[0].shipmentMode.options[selectedIndex].value=="Truck")
	{
			for(var i=0;i<document.getElementsByName("Type").length;i++)
			{
				typeArray[i]	=	document.getElementsByName("Type")[i].value;
				if(document.getElementsByName("Type")[i].value.length==0)
				{
					alert("Please enter the value for Container Type at Row "+(i+1));
					document.getElementsByName("Type")[i].focus();
					return false;
				}
				if(document.getElementsByName("volume")[i].value.length==0)
				{
					alert("Please enter the value for Volume at Row "+(i+1));
					document.getElementsByName("volume")[i].focus();
					return false;
				}
			}
	}
	var label;
	if(document.forms[0].shipmentMode.options[selectedIndex].value=="Sea" || document.forms[0].shipmentMode.options[selectedIndex].value=="Truck")
		label	=	" Container ";
	else
		label	=	" ULD ";

	for(var i=0;i<document.getElementsByName("Type").length;i++)
	{
		for(var j=0;j<typeArray.length;j++)
		{
			if(i==j)
			{
				break;
			}
			else
			{
				if(typeArray[j]==document.getElementsByName('Type')[i].value)
				{
					alert('Please Enter a Unique'+label+'Type at Row '+(i+1));
					document.getElementsByName('Type')[i].focus();
					return false;
				}
			}
		}
	}
		
}

</script>
</head>
<form method="post" name="listmaster" onSubmit="return mandatory();" action="ListMasterProcess.jsp">

<body onLoad="initialize()">

       <table width="100%" cellpadding="4" cellspacing="1" height="77" bgcolor='#FFFFFF'>

		<tr Class=formlabel> 
			<td colspan=2 align=left width="768" height="1"> 
				List Master - <%=operation%>
			</td>
			<td align="right">QS1020211</td>
		</tr>
		<tr Class=formdata> 
		<td width="400" height="1">
              Shipment Mode:<font color="#FF0000">*</font><br>
              <select size="1" name="shipmentMode" class="select" onChange="changeLabel()">
                <option value="Air" selected>Air</option>
                <option value="Sea">Sea</option>
				<option value="Truck">Truck</option>
            </select>
            </td>
		<td width="124" height="1">
              UOV:&nbsp;<br>
              <select size="1" name="uov" class="select">
              <option>CFT</option>
              <option selected>CBM</option>
            </select>
            </td>
		<td width="372" height="1">
            UOW:&nbsp;<br>
              <select size="1" name="uow" class="select">
                <option>KG</option>
                <option>LB</option>
              </select>
            </td>
</tr>





</table>

		<table border='0' width="100%" bgcolor='#FFFFFF'  id="listMasterReg"  idcounter="1" 
         defaultElement="Type" xmlTagName="listMasterReg" functionName="defaultFunction" deleteFunctionName="defaultDeleteFunction" onBeforeDelete="" onBeforeCreate="" maxRows=25>
		<tr Class=formheader> 
		<td>&nbsp;&nbsp;&nbsp;</td>
		<td height="1" align="center"><font color="#FFFFFF"><span id=RefDocLabel>ULD Type:*</span></font></td>
		<td  height="1" align="center"><span id=RefDocLabel1>ULD&nbsp;Description:</span></td>
		<td  height="1" align="center"><span id=RefDocLabel2>Volume:</span></td>
		<td  height="1" align="center"><span id=RefDocLabel3>Pivot Weight:*</span></td>
		<td  height="1" align="center"><span id=RefDocLabel4>Over&nbsp;Pivot&nbsp;Weight:*</span></td>
		<td ></td>
	</tr>
		 </table>

	<table border="0" width="100%" bgcolor='#FFFFFF'>
		<tr bgcolor="#FFFFFF">
			<td width="352" height="1" colspan="4" class='denotes'>
            &nbsp;<font color="#FF0000">*</font> Denotes Mandatory
            </td>
			<td width="327" height="1" align="center" colspan="2">
            <p align="right"> 
              <input type="submit" name="Submit" value="Submit" class='input'>
            </td>
			<input type="hidden" name="Operation" value="<%=operation%>"
		</tr>
	</table>


</body>

</html>
