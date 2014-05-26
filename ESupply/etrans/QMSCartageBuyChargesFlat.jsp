<%--
% 
% Copyright (c) 1999-2005 by FourSoft, Pvt Ltd. All Rights Reserved.
% This software is the proprietary information of FourSoft, Pvt Ltd.
% Use is subject to license terms.
%
% QMS - v 1.0 
%
--%>
<%@ page import = "javax.naming.InitialContext,
			java.util.ArrayList,
			java.util.HashMap,
			java.util.Iterator,
			org.apache.log4j.Logger,
			com.foursoft.esupply.common.java.ErrorMessage,
			com.qms.operations.charges.java.QMSCartageMasterDOB,
			com.foursoft.esupply.common.java.KeyValue,
			com.foursoft.esupply.common.util.ESupplyDateUtility"%>
<jsp:useBean id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/>

<%!
      private static Logger logger = null;
      private static final  String  FILE_NAME = "QMSCartageBuyChargesFlat.jsp" ;
%>

<%
  logger  = Logger.getLogger(FILE_NAME);	
	ESupplyDateUtility  eSupplyDateUtility		=	new ESupplyDateUtility();
    String              dateFormat				=	loginbean.getUserPreferences().getDateFormat();
	String				terminalId				=	loginbean.getTerminalId();
	String				operation				=	null;
	String 				shipmentMode			=	null;
	String				fromWhere				=	null;
	ErrorMessage		errorMessageObject		=   null;
	ArrayList			keyValueList			=   new ArrayList();
	QMSCartageMasterDOB cartageMaster			=	null;

	String				weightClass				=	null;
	String				unitOfMeasure			=	null;
	String				vendorId				=   null;
	String				chargeType				=	null;
	String				weightBreak				=	null;
	String				rateType				=	null;
	String				currencyId				=	null;
	String				chargeBasis				=	null;
	String[]			zoneCodes				=   null;
	String				maxChargeFlag			=	null;
	//@@UI Calculation variables
	int					colSpan					=	0;
	int					flatColSpan				=	2;
	String				label					=	"";
	String				maxFlagCheck			=	"";
	String				maxRateCol1				=	"";
	String				maxRateCol2				=	"";
	int					widthPer				=	0;
	String				width					=	"";
	String				headerString			=	"";
	//@@
	String				primaryUnit				=	null;
	ArrayList           densityGroupList        =  null; //@@Added for the WPBN issue-54554
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
		operation				=	request.getParameter("Operation");
		//commented by subrahmanyam for the WPBN issue-143507 on 12/11/08
		//shipmentMode			=	request.getParameter("shipmentMode");
		// added by subrahmanyam for the WPBN issue-143507 on 12/11/08
		shipmentMode			=	request.getParameter("shipMode");

		cartageMaster			=   (QMSCartageMasterDOB)session.getAttribute("cartageMaster");
		densityGroupList        =   (ArrayList)request.getAttribute("densityList");//@@Added for the WPBN issue-54554
		if(cartageMaster!=null)
		{
			unitOfMeasure		=	cartageMaster.getUom();
			vendorId			=	cartageMaster.getVendorId();
			chargeType			=	cartageMaster.getChargeType();
			weightBreak			=	cartageMaster.getWeightBreak();
			rateType			=	cartageMaster.getRateType();
			currencyId			=	cartageMaster.getCurrencyId();
			chargeBasis			=	cartageMaster.getChargeBasis();
			zoneCodes			=	cartageMaster.getZoneCodes();
			maxChargeFlag		=	cartageMaster.getMaxChargeFlag();
			primaryUnit			=	cartageMaster.getPrimaryUnit();
			//Logger.info(FILE_NAME,"chargeType::"+chargeType);
		}
		
		//@@ --- UI Calculations ---

		if("Delivery".equals(chargeType))
			label   =		"DELIVERY CHARGES";
		else
			label   =		"PICKUP CHARGES";

		if("Y".equals(maxChargeFlag))
		{
			colSpan		 =	1;
			flatColSpan	 =	0;

			if("Both".equals(chargeType))
				widthPer =	10;
			else
				widthPer =	20;

			maxFlagCheck  =  "<td width='"+widthPer+"%'><b>Max*</b></td>";
			maxRateCol1   =	"<td><input type='text' class='text' name='maxCharge' size='8' maxlength='8'></td>";
			maxRateCol2   =	"<td><input type='text' class='text' name='dMaxCharge' size='8' maxlength='8'></td>";
		}
		else
		{
			if("Both".equals(chargeType))
				widthPer =	12;
			else
				widthPer =	25;
		}
		
		if("Both".equals(chargeType))
			headerString = "<td colspan='"+(colSpan+5)+"' align='center'>DELIVERY CHARGES</td>";
		//@@End

		//Logger.info(FILE_NAME, " operation ", operation);
%>

<html>

<head>
<%@ include file="../ESEventHandler.jsp" %>
<script>
function Mandatory()
{
	var min	 = document.getElementsByName("minCharge");
	var flat = document.getElementsByName("flatCharge");
	var max	 = document.getElementsByName("maxCharge");

	var dMin = document.getElementsByName("dMinCharge");
	var dFlat= document.getElementsByName("dFlatCharge");
	var dMax = document.getElementsByName("dMaxCharge");
	
	var primaryUnit = '<%=primaryUnit%>'.toUpperCase();

	
	for(var j=0;j<min.length;j++)
	{
		if(min[j].value.length==0)
		{
			alert('Please Enter the Minimum Charge at Row '+(j+1));
			min[j].focus();
			return false;
		}
		if(isNaN(min[j].value))
		{
				alert('Please Enter Numeric Values Only');
				min[j].focus();
				return false;
		}
		if(flat[j].value.length==0)
		{
			alert('Please Enter the Flat Charge at Row '+(j+1));
			flat[j].focus();
			return false;
		}
		if(isNaN(flat[j].value))
		{
				alert('Please Enter Numeric Values Only');
				flat[j].focus();
				return false;
		}
	}
	
	for(var j=0;j<max.length;j++)
	{
		if(max[j].value.length==0)
		{
			alert('Please Enter the Maximum Charge at Row '+(j+1));
			max[j].focus();
			return false;
		}if(isNaN(max[j].value))
			{
				alert('Please Enter Numeric Values Only');
				max[j].focus();
				return false;
			}
	}
	if(primaryUnit == 'KG' || primaryUnit=='LB' || primaryUnit=='CBM' || primaryUnit=='CFT')
	{
		for(var j=0;j<min.length;j++)
		{
			if(document.getElementsByName("densityRatio"+j)[0].value.length==0)
			{
				alert("Please Enter the Density Ratio at Row "+(j+1));
				document.getElementsByName("densityRatio"+j)[0].focus();
				return false;

			}
				//@@Added by kameswari for the WPBN issue- 54554
			else
			{
			 
				var c=0;
				   <%for(int m=0;m<densityGroupList.size();m++)
					{%>
					   if(document.getElementsByName("densityRatio"+j)[0].value=='<%=densityGroupList.get(m)%>')
						{
                                 c++;
						 
						}
					   <%}
					  %>
					  if(c==0)
					   {
								 alert("Please enter valid Density Group Code");
								 document.getElementsByName("densityRatio"+j)[0].focus();
								 return false;

					   }					   

			}
			//@@WPBN issue-54554
		}
	}
	else
	{
		for(var j=0;j<min.length;j++)
		{
			if(document.getElementsByName("densityRatio"+j)[0].value.length > 0)
			{
				document.getElementsByName("densityRatio"+j)[0].value='';
			}
		}
	}
	for(var j=0;j<dMin.length;j++)
	{
		if(dMin[j].value.length==0)
		{
			alert('Please Enter the Minimum Delivery Charge at Row '+(j+1));
			dMin[j].focus();
			return false;
		}
		if(isNaN(dMin[j].value))
		{
			alert('Please Enter Numeric Values Only');
			dMin[j].focus();
			return false;
		}
		if(dFlat[j].value.length==0)
		{
			alert('Please Enter the Flat Delivery Charge at Row '+(j+1));
			dFlat[j].focus();
			return false;
		}
		if(isNaN(dFlat[j].value))
		{
			alert('Please Enter Numeric Values Only');
			dFlat[j].focus();
			return false;
		}
	}
	
	for(var j=0;j<dMax.length;j++)
	{
		if(dMax[j].value.length==0)
		{
			alert('Please Enter the Maximum Delivery Charge at Row '+(j+1));
			dMax[j].focus();
			return false;
		}
		if(isNaN(dFlat[j].value))
		{
				alert('Please Enter Numeric Values Only');
				dFlat[j].focus();
				return false;
		}
	}
	if(primaryUnit == 'KG' || primaryUnit=='LB' || primaryUnit=='CBM' || primaryUnit=='CFT')
	{
		
		for(var j=0;j<dMin.length;j++)
		{
				if(document.getElementsByName("dDensityRatio"+j)[0].value.length==0)
			{
				alert("Please Enter the Density Ratio for Delivery Charges at Row "+(j+1));
				document.getElementsByName("dDensityRatio"+j)[0].focus();
				return false;

			}
			//@@Added by kameswari for the WPBN issue- 54554 
			else
			{
				var c=0;
				 <%for(int k=0;k<densityGroupList.size();k++)
					{
					  %>
				   if(document.getElementsByName("dDensityRatio"+j)[0].value=='<%=densityGroupList.get(k)%>')
					{
					  c++;
					 
					}
				   <%}
				  %>
				  if(c==0)
				   {
							 alert("Please enter valid Density Group Code");
							 document.getElementsByName("dDensityRatio"+j)[0].focus();
							 return false;

				   }					   

			}
			//@@WPBN issue-54554
		}
	}
  //@@Added by Kameswari for internal issue on 15/10/08
else
	{
		for(var j=0;j<dMin.length;j++)
		{
			if(document.getElementsByName("dDensityRatio"+j)[0].value.length > 0)
			{
				document.getElementsByName("dDensityRatio"+j)[0].value='';
			}
		}
	}
//@@ internal issue on 15/10/08
}
function showLOV(input)
{
	var name			=	input;	
	var searchString	=	document.getElementsByName(name)[0].value;
	//Commeneted and Modified by subrahmanyam for the WPBN issue-143507 on 12/11/08
	/*var Url				= "etrans/QMSDensityRatioLOV.jsp?Operation=<%=operation%>&name="+input+"&searchString="+searchString+"&fromWhere=BC&uom=<%=chargeBasis%>";*/
	var Url				= "etrans/QMSDensityRatioLOV.jsp?Operation=<%=operation%>&name="+input+"&searchString="+searchString+"&fromWhere=BC&uom=<%=chargeBasis%>&shipmentMode="+<%=shipmentMode%>;
	//@@ WPBN issue-143507 on 12/11/08
	var Bars = 'directories=no, location=no,menubar=no,status=no,titlebar=no';
	var Options='scrollbar=yes,width=400,height=360,resizable=no';
	var Features=Bars+' '+Options;
	var Win=open(Url,'Doc',Features);
}
function getDotNumberCode1(input)    // Numbers + Dot
	{
		if(event.keyCode!=13)
		{	
			if(event.keyCode == 46 )
			{
				if(input.value.indexOf(".") == -1)
					return true;
				else
				return false;
			}

		 if((event.keyCode < 46 || event.keyCode==47 || event.keyCode > 57) )
		   return false;	
		  else
		  {
			var index = input.value.indexOf(".");
			if( index != -1 )
			{
				if(input.value.length == index+3)
				return false;
			}
		  }
		}
		return true;	
	}



function checkNumbers1(input)
	{
		
		if(trim(input.value).length>0)
		{
			if(isNaN(trim(input.value)))
			{
				//alert("Please do not enter characters for "+label);
				input.value='';
				input.focus();
				return false;
			}
		}
		return true
	}
	function trim(input)
	 { 
		while (input.substring(0,1) == ' ') 
			input = input.substring(1, input.length);

		while (input.substring(input.length-1,input.length) == ' ')
			input = input.substring(0, input.length-1);

	   return input;
	 }

</script>
<meta http-equiv="Content-Type" content="text/html; charset=windows-1252">
<title>Cartage Buy Charges Master</title>
	<link rel="stylesheet" href="ESFoursoft_css.jsp">
</head>

<body bgcolor="#FFFFFF">

<form method="post" action='QMSCartageController' onsubmit='return Mandatory()'>
  <table width="100%" cellpadding="0" cellspacing="0" bgcolor="#FFFFFF">
		  <tr valign="top" class="formlabel">
			<td	colspan="2"><table cellpadding="4" cellspacing="1" width="100%"><tr class='formlabel'><td>Cartage Buy Charges - Add</td><td align=right>QS1050712</td></tr></table></td>
		  </tr></table>
		  
		  
		  <table border="0" width="100%" cellspacing="1" cellpadding="4" bgcolor="#FFFFFF">
		  <tr class='formdata'> 
              <td>Weight Class:<br> 
				  <select name="weightClass" class='select'>
				   <option  value="G">General</option>
				  </select>
              </td>
              <td>Charge Basis:<br><b><%=chargeBasis!=null?chargeBasis:""%></b></td>
              <td >Charge Type:<br><b><%=chargeType!=null?chargeType:""%></b></td>
			  
<%
			if(!"Both".equals(chargeType))
			{
%>			 <td></td>
			 </tr>
			 <tr class='formdata'>
<%
			}
%>
				<td>Weight Break:<b><br><%=weightBreak!=null?weightBreak:""%></b></td>
				<td>Rate Type:<br><b><%=rateType!=null?rateType:""%></b></td>
				<td colspan='<%=colSpan%>'>Currency:<br><b><%=currencyId!=null?currencyId:""%><br></b></td>
				<td></td>
             </tr>
			 </table>
		  
		  
		  <table border="0" width="100%" cellspacing="1" cellpadding="4" bgcolor="#FFFFFF">
          <tr class='formdata'>
		  <!-- @@Commented by subrahmanyam for Enhancement 170759 on 25/May/09 -->
	<!--  <td colspan="<%=8+colSpan%>"><b>Please Enter Charges For Zones: </b></td> -->
	  <!-- @@Added by subrahmanyam for Enhancement 170759 on 25/May/09 -->
            <td colspan="<%=10+colSpan%>"><b>Please Enter Charges For Zones: </b></td>
<!-- @@ For 170759 -->
          </tr>
          <tr class='formheader' align="center"> 
            <td rowspan="2" width='24%'>Zone Code:</td>
			<!-- @@Commented by subrahmanyam for Enhancement 170759 on 25/may/09 -->
<!--             <td colspan="4"><%=label%></td> -->
	<!-- @@Added by subrahmanyam for Enhancement 170759 on 25/may/09 -->
		<td colspan="5"><%=label%></td> 
		<!-- @@For 170759 -->
			
			<%=headerString%>
			
          </tr>
          <tr class='formheader' align="center"> 
		   <td width='<%=widthPer%>%'><b>Base</b></td><!-- @@Added by subrahmanyam for 170759 on 25/may/09 -->
            <td width='<%=widthPer%>%'><b>Min</b></td>
			<td colspan='<%=flatColSpan%>' width='<%=widthPer%>%'><b>Flat</b></td>
			<%=maxFlagCheck%>
			<td width='<%=widthPer%>%'><b>Density Ratio</b></td>
			
<%
			if("Both".equals(chargeType))
			{
%>				<td width='<%=widthPer%>%'><b>Base</b></td><!-- @@Added by subrahmanyam for 170759 on 25/may/09 -->
				<td width='<%=widthPer%>%'><b>Min</b></td>
				<td  width='<%=widthPer%>%'><b>Flat</b></td>
				<%=maxFlagCheck%>
				<td width='<%=widthPer%>%'><b>Density Ratio</b></td>
				
<%
			}
%>
          </tr>
<%
		  for(int i=0;i<zoneCodes.length;i++)
		  {
%>
          <tr class='formdata' align="center"> 
			  <td>Zone <%=zoneCodes[i]!=null?zoneCodes[i]:""%></td>
			  <!-- @@Added by subrahmanyam for 170759 on 25/may/09 -->
			  <td>
				<input type='text' class='text' name="baseCharge" size="8" maxlength='8'  onkeypress='return  getDotNumberCode1(this)' onBlur='checkNumbers1(this);'>
			  </td>
			  <!-- @@Ended by subrahmanyam for 170759 on 25/may/09 -->
			  <td>
				<input type='text' class='text' name="minCharge" size="8" maxlength='8'  onkeypress='return  getDotNumberCode1(this)' onBlur='checkNumbers1(this);'>
			  </td>
			  <td colspan='<%=flatColSpan%>'>
				<input type='text' class='text' name="flatCharge" size="8" maxlength='8'  onkeypress='return  getDotNumberCode1(this)' onBlur='checkNumbers1(this);'>
			  </td>			  
			  <%=maxRateCol1%>
			  <td >
				<input type='text' class='text' name="densityRatio<%=i%>" size="8">&nbsp;<input type='button' name='densityLOV' class='input' onClick='showLOV("densityRatio<%=i%>")' value='...'>
			  </td>
<%
			if("Both".equals(chargeType))
			{
%>			
			  <!-- @@Added by subrahmanyam for 170759 on 25/may/09 -->

				<td>
				<input type='text' class='text' name="dBaseCharge" size="8" maxlength='8'  onkeypress='return  getDotNumberCode1(this)' onBlur='checkNumbers1(this);'>
				 </td>
			<!-- @@Ended by subrahmanyam for 170759 on 25/may/09 -->
				<td>
				<input type='text' class='text' name="dMinCharge" size="8" maxlength='8'  onkeypress='return  getDotNumberCode1(this)' onBlur='checkNumbers1(this);'>
			 </td>
			 <td > 
              <input type='text' class='text' name="dFlatCharge" size="8"   maxlength='8'  onkeypress='return  getDotNumberCode1(this)' onBlur='checkNumbers1(this);'>
			 </td>
			  <%=maxRateCol2%>
			  <td >
				<input type='text' class='text' name="dDensityRatio<%=i%>" size="8">&nbsp;<input type='button' name='ddensityLOV' class='input' onClick='showLOV("dDensityRatio<%=i%>")' value='...'>
			  </td>
<%
			}
%>
		   </tr>
<%
		}
%>
        </table>
     
      
      <table border="0" width="100%" cellspacing="1" cellpadding="4" bgcolor="#FFFFFF">
          <tr class=text> 
            <td>
              * Maximum Charge per truckload
            </td>
            <td  align="right">
			<input type="reset" value="Reset" class='input'>
			<input type="Submit" value="Submit" class='input'>
			<input type="hidden" name ="Operation"  value="<%=operation%>" >
			<input type="hidden" name="subOperation" value="flatAdd">

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
		//Logger.error(FILE_NAME,"error in JSP:"+e);
    logger.error(FILE_NAME+"error in JSP:"+e);
		e.printStackTrace();
		errorMessageObject = new ErrorMessage("Error while retrieving the details","QMSCartageController"); 
		keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
		keyValueList.add(new KeyValue("Operation",operation)); 	
		errorMessageObject.setKeyValueList(keyValueList);
		request.setAttribute("ErrorMessage",errorMessageObject);

%>		<jsp:forward page="../ESupplyErrorPage.jsp"/>

<%
	}
%>

