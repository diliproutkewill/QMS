
<%--
<%--

Copyright (c) 1999-2001 by FourSoft, Pvt Ltd. All Rights Reserved.
This software is the proprietary information of FourSoft, Pvt Ltd.
Use is subject to license terms.
esupply - v 1.x.
Program	Name	: QMSIndustryRegistration.jsp
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
<%@page import ="com.qms.setup.ejb.sls.SetUpSessionHome,
				  com.qms.setup.ejb.sls.SetUpSession,
				  org.apache.log4j.Logger,
				  com.foursoft.esupply.common.java.ErrorMessage,
				  com.foursoft.esupply.common.java.KeyValue,
				  java.util.ArrayList,
				  com.qms.setup.java.MarginLimitMasterDOB,
				  com.foursoft.esupply.common.java.FoursoftConfig"
				  %>
<%!
  private static Logger logger = null;  
	private static final String FILE_NAME="QMSMarginLimitMaster.jsp";
%>

<%
  logger  = Logger.getLogger(FILE_NAME);	
	String		operation		=	request.getParameter("Operation");
	String		shipmentMode	=	request.getParameter("shipmentmode");
	String		consoletype		=	request.getParameter("consoletype");
	String[]	levelId			=	request.getParameterValues("levelId");
	String[]	serviceLevelId	=	request.getParameterValues("serviceLevelId");
	String      chargeType      =   request.getParameter("ChargeType");
	//Logger.info("Line No is 43::",chargeType);
	ArrayList	marginList		=	null;
	MarginLimitMasterDOB	marginLimitMasterDOB	=	null;
	SetUpSessionHome	home	=	null;
	SetUpSession		remote	=	null;	
	int  shipmentType			=	0;
	boolean		success			=	true;
	String readOnly				=	"";
	String		msg				=	"";
	ErrorMessage errorMessageObject =   null;
	ArrayList	 keyValueList	    =	new ArrayList();
	String		errorMessage		=	null;
	String		url					=	null;
	String		shipMode			=	"Air/Sea/Truck";
	String		marginId			=	"";
	try{
		
		
		if(operation!=null && (operation.equals("View") || operation.equals("Delete")))
		{
			readOnly	=	"readOnly";
		}
       
		if(shipmentMode!=null)
		{
			
			if(shipmentMode.equals("1"))
			{
				shipMode		=	"Air";
				shipmentType	=	FoursoftConfig.AIR;
			}else if(shipmentMode.equals("2"))
			{
				shipMode			=	"Sea";
				if(consoletype!=null && consoletype.equals("LCL"))
				{
					shipmentType	=	FoursoftConfig.SEA_LCL;
				}else if(consoletype!=null && consoletype.equals("FCL"))
				{
					shipmentType	=	FoursoftConfig.SEA_FCL;
				}
			}else if(shipmentMode.equals("4"))
			{
				shipMode			=	"Truck";
				if(consoletype!=null && consoletype.equals("LTL"))
				{
					shipmentType	=	FoursoftConfig.TRUCK_LTL;
				}else if(consoletype!=null && consoletype.equals("FTL"))
				{
					shipmentType	=	FoursoftConfig.TRUCK_FTL;
				}
			}
		
		
		}
		 
		if(operation!=null && !operation.equals("Add"))
		{
			home	=	(SetUpSessionHome)loginbean.getEjbHome("SetUpSessionBean");
			remote	=	(SetUpSession)home.create();
			if(operation.equals("ViewAll") || operation.equals("Invalidate"))
			{
				//System.out.println("chargeType : "+chargeType);
				//System.out.println("shipmentModeshipmentMode : "+shipmentMode);
				if("Freight".equals(chargeType))
					marginList	=	remote.getMarginLimitList(operation,shipmentMode,consoletype,levelId,serviceLevelId,loginbean);
				else
					marginList	=	remote.getMarginLimitList1(operation,chargeType,levelId,loginbean);

				session.setAttribute("marginList",marginList);
			}else
			{
				
				if(levelId!=null && serviceLevelId!=null)
				{
					//@@ Commented & Added by subrahmanyam for the pbn id: 203354 on 22-APR-010 
					//marginLimitMasterDOB =	remote.getMarginLimitDetails(Integer.toString(shipmentType),levelId[0],serviceLevelId[0],loginbean,chargeType.toUpperCase());
					marginLimitMasterDOB =	remote.getMarginLimitDetails(Integer.toString(shipmentType),levelId[0],serviceLevelId[0],loginbean,chargeType.toUpperCase(),operation);

				    //Logger.info(FILE_NAME,"marginLimitMasterDOB:::"+marginLimitMasterDOB.getLevelId());
				}
			}
			if(marginList!=null && marginList.size()>0)
			{
				marginLimitMasterDOB =(MarginLimitMasterDOB)marginList.get(0);
				//Logger.info(FILE_NAME,"marginLimitMasterDOB:::"+marginLimitMasterDOB.getLevelId());
			}
		}
%>
<html>
<head>
<title>Margin Limit Master</title>       <%@ include file="../ESEventHandler.jsp" %>
<link rel="stylesheet" href="../ESFoursoft_css.jsp">
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<script src ="../html/dynamicContent.js"></script>
<script src ='../html/eSupply.js'></script>
<script >
<%
	String onLoad	="";
	if(operation!=null && operation.equals("Add"))
	{
		onLoad	="chargeTypeChange()";
	%>
		function initialize()
		{
			importXML('../xml/marginlimits.xml');
		}
		function validateBeforeDeletion()
		{
			return true;
		}
		function validateBeforeCreation()
		{
			return true;
		}
		function chargeTypeChange()
		{
			
			
			initialize();
			//setValues();
		    
		}
		function dummy()
		{
		   
		   checkChargeType();
		   var tempVal  = document.forms[0].ChargeType.options[document.forms[0].ChargeType.selectedIndex].value;		   
		   if(tempVal=='Freight')
			{
			    
				var tableObj = document.getElementById("marginlimit");
			    if(false)
			    {}
			    else
			    {
				if(tableObj.getAttribute("idcounter")==1)
							createDynamicContentRow(tableObj.getAttribute("id"));
			    }
		   }
		   else
		   {
			 
			 var tableObj = document.getElementById("chargeDetails");
			    
				if(false)
			    {}
			    else
			    {
				if(tableObj.getAttribute("idcounter")==1)
							createDynamicContentRow(tableObj.getAttribute("id"));
			    }		   
		   }
		}
		function initializeDynamicContentRows()
		{					
		   setTimeout("dummy()",100);
		}
		function checkChargeType()
		{		  		
		  if(document.forms[0].ChargeType.options[document.forms[0].ChargeType.selectedIndex].value=='Freight')
		  {
		  document.getElementById("sample").innerHTML='<table border="0" width="800"   id="marginlimit"  idcounter="1" defaultElement="levelId" xmlTagName="marginlimit" functionName="defaultFunction" deleteFunctionName="defaultDeleteFunction" onBeforeDelete="validateBeforeDeletion" onBeforeCreate="validateBeforeCreation" maxRows=20><tr class="formheader"><td ></td><td align="center">Level:<FONT SIZE="1" COLOR="red">*</FONT></td><td align="center">Service Level:<FONT SIZE="1" COLOR="red">*</FONT></td><td align="center">Max.Discount(%):<FONT SIZE="1" COLOR="red">*</FONT></td><td align="center">Min.Margin(%):<FONT SIZE="1" COLOR="red">*</FONT></td><td ></td> </tr> </table>';
          }
		  else
		  {
		  
		  document.getElementById("sample").innerHTML='<table border="0" width="800"   id="chargeDetails"  idcounter="1" defaultElement="levelId" xmlTagName="chargeDetails" functionName="defaultFunction" deleteFunctionName="defaultDeleteFunction" onBeforeDelete="validateBeforeDeletion" onBeforeCreate="validateBeforeCreation" maxRows=20><tr class="formheader"><td ></td><td align="center">Level:<FONT SIZE="1" COLOR="red">*</FONT></td><td align="center">Max.Discount(%):<FONT SIZE="1" COLOR="red">*</FONT></td><td align="center">Min.Margin(%):<FONT SIZE="1" COLOR="red">*</FONT></td><td ></td> </tr></table>';
		  }
		}
		function defaultFunction(currentRow)
		{
		}
		/*function defaultDeleteFunction()
		{
		
			var tableObj = document.getElementById("marginLimits");
			var idcount =tableObj.getAttribute("idcounter");
//			idcount=idcount-1;
			tableObj.setAttribute("idcounter",idcount);

		}*/
	//*************end of dynamic row methods********//
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
					document.layers.console.document.write(data);
					document.layers.console.document.close();
				}
				else
				{
					if (document.all )
					{
						console.innerHTML = data;
					}
				}
		}
		var Win  = null;
		function openLevelIdLOV(input)
		{
			var id			=	input.id;
			var Bname		=	input.name;
			var index		=	id.substring(Bname.length);
			var searchStr	=	document.getElementById("levelId"+index).value;
			var name		=	"levelId"+index;
			var Url			= "QMSLOVLevelIds.jsp?Operation=<%=operation%>&searchString="+searchStr+"&name="+name+"&selection=single&&fromwhere=";
			showLOV(Url);
		}
		function openSLevelIdLOV(input)
		{
			var shipMode	=	document.forms[0].shipmentmode.value;
			var id			=	input.id;
			var Bname		=	input.name;
			var index		=	id.substring(Bname.length);
			var searchStr	=	document.getElementById("serviceLevelId"+index).value;
			var name		=	"serviceLevelId"+index;
			var Url			= "ETCLOVServiceLevelIds.jsp?searchString="+searchStr+"&shipmentMode="+shipMode+"&name="+name+"&selection=single&fromwhere=";
			showLOV(Url);
		}
<%
	}
%>
	function mandatory()
	{

<%
	if(operation!=null && operation.equals("Add"))
	{
%>
		
        var tempVal  = document.forms[0].ChargeType.options[document.forms[0].ChargeType.selectedIndex].value;
		var keyArray			=	new Array();
		if(tempVal=='Freight')
        {
           var tableObj		=	document.getElementById("marginLimit");
		   var idCount		=	document.getElementsByName('levelId').length;
        
		if(parseInt(idCount)>1)
		{
			for(var i=0;i<idCount;i++)
			{
				if(document.getElementsByName('levelId')[i].value.length==0)
				{
					alert("Please Select Level Id At lane No :"+(i+1));
					document.getElementsByName('levelId')[i].focus();
					return false;
				}
				if(document.getElementsByName('serviceLevelId')[i].value.length==0)
				{
					alert("Please Select Service Level Id At lane No :"+(i+1));
					document.getElementsByName('serviceLevelId')[i].focus();
					return false;
				}	
				 keyArray[i] = key;
			}

		}
		else
		{
				if(document.forms[0].levelId.value=='')
				{
					alert("Please Enter Level Id");
					document.forms[0].levelId.focus();
					return false;
				}
				if(document.forms[0].serviceLevelId.value=='')
				{
					alert("Please Enter Service Level Id");
					document.forms[0].serviceLevelId.focus();
					return false;
				}
		  }
		}
		else
        {
		   var tableObj		=	document.getElementById("chargeDetails");
		   var idCount		=	document.getElementsByName('levelId').length;
        
//alert(idCount)
		if(parseInt(idCount)>1)
		{
			for(var i=0;i<idCount;i++)
			{
				if(document.getElementsByName('levelId')[i].value.length==0)
				{
					alert("Please Select Level Id At lane No :"+(i+1));
					document.getElementsByName('levelId')[i].focus();
					return false;
				}
				var key = document.getElementsByName('levelId')[i].value;
				keyArray[i] = key;
					
			}

		}else
		{
				if(document.forms[0].levelId.value.length==0)
				{
					alert("Please Enter Level Id");
					document.forms[0].levelId.focus();
					return false;
				}
				
		  }
		
		}

		for(var i=0;i<document.getElementsByName("levelId").length;i++)
		{
			for(var j=0;j<keyArray.length;j++)
			{
				if(i==j)
				{
					break;
				}
				else
				{
					if(keyArray[j]==document.getElementsByName('levelId')[i].value)
					{
						alert('Please Enter a Unique Level at Row '+(i+1));
						document.getElementsByName('levelId')[i].focus();
						return false;
					}
				}
			}
		}

<%
	}

%>
       if(document.forms[0].submit!=null)
		{
			document.forms[0].submit.disabled	=true;
		}
		
		<% 
		  if(!operation.equalsIgnoreCase("Delete"))
		  {
		%> 
		if(document.forms[0].reset!=null)
		{
			document.forms[0].reset.disabled	=true;
		}
		<%
	      }
		%>
		
		return true;
	}
	function checkLevelNos()
	{
	  var flag      = false;
	  var invalidate=document.getElementsByName("invalidate");
	  
	  for(m=0;m<invalidate.length;m++)
	  {
	    if(invalidate[m].checked)
		flag=true;
	  }
	  return flag;
	}
	function submitButton()//Added by rk
	{
		
		var operation =  '<%=operation%>';
		
		if(operation=="View")
		{
		 window.document.forms[0].action='QMSMarginLimitMasterEnterId.jsp?Operation=View';
		 window.document.forms[0].submit();
	    }
		else
        {
		 window.document.forms[0].action='QMSMarginLimitMasterViewAllEnterId.jsp?Operation=ViewAll';
		 window.document.forms[0].submit();
		}
	}

		function toUpper(input)
		{		
			input.value	=	input.value.toUpperCase();
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
	      document.getElementById("console").innerHTML='';
		}
	}
	function checkVal()
	{
	  var flag = false;
	  var len=document.getElementsByName("levelId");
	  for(m=0;m<len.length;m++)
	  {
	    if(document.getElementById("invalidate"+m).checked)
	    flag=true;
	  }
	  return flag; 
	}
	function validateBeforeCreation()
	{
	
	 maxDis=document.getElementsByName("maxDiscount");
     minMar=document.getElementsByName("minMargin");
	    	
		 for(m=0;m<maxDis.length;m++)
		 {
	  
			  if(parseInt(maxDis[m].value)>100 || parseInt(minMar[m].value)>100)
			  {
			    alert("Please enter Correct Percentage");
			    return false;
			  }
	     }
	   
	  return true;
	}
</script>
</head>
<body onLoad ="<%=onLoad%>">
<form name ="marginlimitmaster" method ="post" action ="QMSMarginLimitMasterProcess.jsp" onSubmit ="return mandatory();">
<table width="800" border="0" cellspacing="0" cellpadding="0" >
    <tr valign="top" bgcolor="#FFFFFF">
      <td>
        <table border="0" width="800" cellpadding="4" cellspacing="1">
          <tr valign="top" class="formlabel">
            <td >
				<table border="0" width="790" >
					<tr valign="top" class="formlabel">
						<td> Margin Limit Master - <%=operation%> </td>
						<td align=right>QS1010411
						</td></tr></table>
			</td>
		  </tr>
		</table>
		<table border='0' width="800" >
		  <tr class='formdata'>
<%		if(operation!=null && operation.equals("Add"))
		{
%>		 
		   <td width='40%'>ChargeType:
		   <select name='ChargeType' class='select' onChange='chargeTypeChange();changeShipmentMode()'>
		   <option value='Charges'>Charges</option>
		   <option value='Freight'>Freight</option>
		   <option value='Cartages'>Cartages</option>
		   </select></td>
		   <td width='40%'><span id='mode'></span></td>
		   <td width='20%'>
		   <span id='console'></span></td>
           
		   
		   
<%
		}else if(operation!=null && (operation.equals("Modify") || operation.equals("View") || operation.equals("Delete")))
		{
%>
		   <td width='50%'>ShipmentMode:
		   <b><%=(shipMode!=null)?shipMode:""%></b>
		   <input type='hidden' name='shipmentmode' value='<%=(shipmentMode!=null)?shipmentMode:""%>'></td>
		   <td width='50%'>ConsoleType:
		   <b><%=(consoletype!=null && (shipmentMode!=null && !shipmentMode.equals("1")))?consoletype:""%></b>
		   <input type='hidden' name='consoletype' value='<%=(consoletype!=null)?consoletype:""%>'>
		   <input type='hidden' name='ChargeType' value='<%=(chargeType!=null)?chargeType:""%>'></td>
           
<%
		}
%>
		 </tr>
		</table>
<%
		if(operation!=null && operation.equals("Add"))
		{
%>
		
		  <div id="sample"></div>
		
<%
		}else if(operation!=null && (operation.equals("Modify") || operation.equals("View") || operation.equals("Delete")))
		{
           if(marginLimitMasterDOB.getLevelId()!=null)
		   {
%>
		<table border='0' width="800">
		 <tr class='formheader'>
			<td ></td>
			<td align='center'>Level:<FONT SIZE="1" COLOR="red">*</FONT></td>
			<% if(chargeType.equalsIgnoreCase("FREIGHT"))
			 {
			%>
			<td align='center'>Service Level:<FONT SIZE="1" COLOR="red">*</FONT></td>
			<% } %>
			<td align='center'>Max.Discount(%):<FONT SIZE="1" COLOR="red">*</FONT></td>
			<td align='center'>Min.Margin(%):<FONT SIZE="1" COLOR="red">*</FONT></td>
			<td ></td>
	     </tr>
		 <tr class ='formdata'>
			<td></td>
			<td align='center'><%=(marginLimitMasterDOB.getLevelId()!=null)?marginLimitMasterDOB.getLevelId():""%>
			<input type='hidden' name='levelId' value='<%=(marginLimitMasterDOB.getLevelId()!=null)?marginLimitMasterDOB.getLevelId():""%>'></td>
			<% if(chargeType.equalsIgnoreCase("FREIGHT"))
			   {
			%>
			<td align='center'><%=(marginLimitMasterDOB.getServiceLevel()!=null)?marginLimitMasterDOB.getServiceLevel():""%>
			<% } %>
			<input type='hidden' name='serviceLevelId' value='<%=(marginLimitMasterDOB.getServiceLevel()!=null)?marginLimitMasterDOB.getServiceLevel():""%>'></td></td>
			<td><input type='text' name='maxDiscount' class='text' <%=readOnly%> value='<%=(marginLimitMasterDOB.getMaxDiscount()!=null)?marginLimitMasterDOB.getMaxDiscount():""%>'  onKeypress='numberFilter2(this,"minMargin")' onBlur='checkNumbersAndRoundToTwoDecimal(this,"minMargin");validateBeforeCreation()'></td>
			<td><input type='text' name='minMargin' class ='text' <%=readOnly%> value='<%=(marginLimitMasterDOB.getMinMargin()!=null)?marginLimitMasterDOB.getMinMargin():""%>'  onKeypress='numberFilter2(this,"minMargin")' onBlur='checkNumbersAndRoundToTwoDecimal(this,"minMargin");validateBeforeCreation()'></td>
		 </tr>
		</table>
<%
		   }
           else
		   {
		            errorMessage			=  "Record does not exist";
					url						=	"QMSMarginLimitMasterEnterId.jsp?Operation="+operation;
					errorMessageObject      =  new ErrorMessage(errorMessage,url); 
					//@@ Commented & Added by subrahmanyam for the pbn id: 203354 on 22-APR-010 
					//keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
					keyValueList.add(new KeyValue("ErrorCode","INF")); 	
					keyValueList.add(new KeyValue("Operation","Modify")); 	
					errorMessageObject.setKeyValueList(keyValueList);
					request.setAttribute("ErrorMessage",errorMessageObject);
%>
                    <jsp:forward page="../QMSESupplyErrorPage.jsp" />
<%
		   
		   }

		  }else if(operation!=null && (operation.equals("ViewAll") || operation.equals("Invalidate")))
		{
%>
		<table border='0' width="800">
		 <tr class='formheader'>
			<td >ShipmentMode</td>
			<td>ConsoleType</td>
			<td align='center'>Level:</td>
			<td align='center'>Service Level:</td>
			<td align='center'>Max.Discount(%):</td>
			<td align='center'>Min.Margin(%):</td>
			<td align='center'>Charge Type</td>
			<td ><%=(operation!=null && operation.equals("Invalidate"))?"Invalidate":""%></td>
	     </tr>
<%
			if(marginList!=null && marginList.size()>0)
			{
				for(int i=0;i<marginList.size();i++)
				{
						marginLimitMasterDOB =	(MarginLimitMasterDOB)marginList.get(i);
						marginId			 =	marginLimitMasterDOB.getMarginId();
						if(Integer.parseInt(marginId)==FoursoftConfig.AIR)
							{ shipMode		 =	"Air";
							  consoletype	 =	"--";}
						else if(Integer.parseInt(marginId)==FoursoftConfig.SEA_LCL)
							{ shipMode		 =	"Sea";
							  consoletype	 =	"LCL";}
						else if(Integer.parseInt(marginId)==FoursoftConfig.SEA_FCL)
							{ shipMode		 =	"Sea";
							  consoletype	 =	"FCL";}
						else if(Integer.parseInt(marginId)==FoursoftConfig.TRUCK_LTL)
							{ shipMode		 =	"Truck";
							  consoletype	 =	"LTL";}
						else if(Integer.parseInt(marginId)==FoursoftConfig.TRUCK_FTL)
							{ shipMode		 =	"Truck";
							  consoletype	 =	"FTL";}
%>
		 <tr class ='formdata'>
			<td align='center'><b><%=shipMode%></b><input type='hidden' name='marginId' value='<%=marginId%>'></td>

			<td align='center'><b><%=consoletype==null?"":consoletype%></b></td>

			<td align='center'><%=(marginLimitMasterDOB.getLevelId()!=null)?marginLimitMasterDOB.getLevelId():""%>
			<input type='hidden' name='levelId' value='<%=(marginLimitMasterDOB.getLevelId()!=null)?marginLimitMasterDOB.getLevelId():""%>'></td>

			<td align='center'><%=(marginLimitMasterDOB.getServiceLevel()!=null)?marginLimitMasterDOB.getServiceLevel():""%>
			<input type='hidden' name='serviceLevelId' value='<%=(marginLimitMasterDOB.getServiceLevel()!=null)?marginLimitMasterDOB.getServiceLevel():""%>'></td>

			<td><%=(marginLimitMasterDOB.getMaxDiscount()!=null)?marginLimitMasterDOB.getMaxDiscount():""%><input type='hidden' name='maxDiscount'  value='<%=(marginLimitMasterDOB.getMaxDiscount()!=null)?marginLimitMasterDOB.getMaxDiscount():""%>'></td>

			<td><%=(marginLimitMasterDOB.getMinMargin()!=null)?marginLimitMasterDOB.getMinMargin():""%><input type='hidden' name='minMargin'  value='<%=(marginLimitMasterDOB.getMinMargin()!=null)?marginLimitMasterDOB.getMinMargin():""%>'></td>

            <td align='center'><%=marginLimitMasterDOB.getChargeType()==null?"":marginLimitMasterDOB.getChargeType()%><input type='hidden' name='chargeType' value='<%=marginLimitMasterDOB.getChargeType()==null?"":marginLimitMasterDOB.getChargeType()%>'></td>
<%
						if(operation!=null && operation.equals("Invalidate"))
						{
%>
							<td align='center'><input type='checkbox' name='invalidate<%=i%>' <%=(marginLimitMasterDOB.getInvalidate()!=null && marginLimitMasterDOB.getInvalidate().equals("T"))?"Checked":""%>></td>	
                                   
<%						
						}else{
%>
							<td></td>
<%
						}
%>

		 </tr>
<%				}
			}
%>
		 </table>
<%
		}
%>
		 <table border="0" width="800" cellpadding="4" cellspacing="1">
          <tr class='denotes'>
            <td valign="top" >
              <font color="#FF0000">*</font>Denotes Mandatory
            </td>
            <td valign="top" align="right">
<%
		if(operation!=null && (operation.equals("Add") || operation.equals("Modify") || operation.equals("Invalidate")))
		{
%>
                <input type="submit" value="Submit" name="submit" class="input">
				<input type="reset" value="Reset" name="reset" class="input">
<%		}else if(operation!=null && operation.equals("Delete"))
		{
%>
				<input type="submit" value="Submit" name="submit" class="input">
<%		}else{
%>
				<input type="button" value="Continue" name="continue" class="input" onclick='submitButton()'><!-- added by rk for issue 8385 -->
<%}%>
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
<%
	}catch(javax.ejb.ObjectNotFoundException e)
	{
		success=false;
		msg	=	"Record doesnt Exist with Given Data :";
		//Logger.error(FILE_NAME,"Exception while retrieving the data"+e);
    logger.error(FILE_NAME+"Exception while retrieving the data"+e);
	}catch(Exception e)
	{
		success	=	false;
		msg	=	"Exception while reading the data";
		//Logger.error(FILE_NAME,"Exception while retrieving the data"+e);
    logger.error(FILE_NAME+"Exception while retrieving the data"+e);
	} 
	if(!success)
	{
				if(operation!=null && operation.equals("Modify"))
				{
					errorMessage			=  msg;
					url						=	"QMSMarginLimitMasterEnterId.jsp?Operation=Modify";
					errorMessageObject      =  new ErrorMessage(errorMessage,url); 
					keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
					keyValueList.add(new KeyValue("Operation","Modify")); 	
					errorMessageObject.setKeyValueList(keyValueList);
					request.setAttribute("ErrorMessage",errorMessageObject);
				}else if(operation!=null && (operation.equals("Delete")))
				{
					errorMessage			=	msg;
					url						=	"QMSMarginLimitMasterEnterId.jsp?Operation=Delete";
					errorMessageObject      =  new ErrorMessage(errorMessage,url); 
					keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
					keyValueList.add(new KeyValue("Operation","Delete")); 	
					errorMessageObject.setKeyValueList(keyValueList);
					request.setAttribute("ErrorMessage",errorMessageObject);
				}
				else if(operation!=null && (operation.equals("View")))
				{
					errorMessage			=	msg;
					url						=	"QMSMarginLimitMasterEnterId.jsp?Operation=View";
					errorMessageObject      =  new ErrorMessage(errorMessage,url); 
					keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
					keyValueList.add(new KeyValue("Operation","View")); 	
					errorMessageObject.setKeyValueList(keyValueList);
					request.setAttribute("ErrorMessage",errorMessageObject);
				}
				else if(operation!=null && (operation.equals("Invalidate")))
				{
					errorMessage			=	msg;
					url						=	"QMSMarginLimitMasterViewAllEnterId.jsp?Operaion=Invalidate";
					errorMessageObject      =  new ErrorMessage(errorMessage,url); 
					keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
					keyValueList.add(new KeyValue("Operation","Invalidate")); 	
					errorMessageObject.setKeyValueList(keyValueList);
					request.setAttribute("ErrorMessage",errorMessageObject);
				}

	%>
					<jsp:forward page="../QMSESupplyErrorPage.jsp" />
	<%
	}
%>