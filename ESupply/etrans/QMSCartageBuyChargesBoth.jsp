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
      private static final  String  FILE_NAME = "QMSCartageBuyChargesBoth.jsp" ;
%>

<%System.out.println("_____________________________________");
  logger  = Logger.getLogger(FILE_NAME);	
	ESupplyDateUtility  eSupplyDateUtility		=	new ESupplyDateUtility();
    String              dateFormat				=	loginbean.getUserPreferences().getDateFormat();
	String				terminalId				=	loginbean.getTerminalId();
	String				operation				=	null;
	String 				shipmentMode			=	null;
	String				fromWhere				=	null;
	ErrorMessage		errorMessageObject		=   null;
	ArrayList			keyValueList			=   new ArrayList();
	ArrayList           densityGroupList        =  null; //@@Added for the WPBN issue-54554
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
	String[]			slabRates				=   null;
	String				maxChargeFlag			=	null;
	String				primaryUnit				=	null;

	String				label					=	"";

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
		//commented by subrahmanyam for the WPBN issue-143507
		//shipmentMode			=	request.getParameter("shipmentMode");
		// added by subrahmanyam for the WPBN issue-143507
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
			slabRates			=	cartageMaster.getSlabRates();
			primaryUnit			=	cartageMaster.getPrimaryUnit();
			//Logger.info(FILE_NAME,"chargeType::"+chargeType);
		}
		
		if("Delivery".equals(chargeType))
			label   =		"DELIVERY CHARGES";
		else
			label   =		"PICKUP CHARGES";


		//Logger.info(FILE_NAME, " operation ", operation);
	}
	catch(Exception e)
	{
		//Logger.error(FILE_NAME,"error in JSP:"+e);
    logger.error(FILE_NAME+"error in JSP:"+e);
		e.printStackTrace();
	}
%>
<html>

<head>
<script>
function showLOV(input)
{
	var name			=	input;	
	var searchString	=	document.getElementsByName(name)[0].value;
	//Commented and Modified by subrahmanyam for the WPBN issue-143507
	/*var Url				= "etrans/QMSDensityRatioLOV.jsp?Operation=<%=operation%>&name="+input+"&searchString="+searchString+"&fromWhere=BC&uom=<%=chargeBasis%>";*/
	  var Url				= "etrans/QMSDensityRatioLOV.jsp?Operation=<%=operation%>&name="+input+"&searchString="+searchString+"&fromWhere=BC&uom=<%=chargeBasis%>&shipmentMode="+<%=shipmentMode%>;
	  //@@WPBN issue-143507
	var Bars = 'directories=no, location=no,menubar=no,status=no,titlebar=no';
	var Options='scrollbar=yes,width=400,height=360,resizable=no';
	var Features=Bars+' '+Options;
	var Win=open(Url,'Doc',Features);
}
function clearValues(obj1,obj2)
{
	var ind = obj1.id.substring(1,obj1.id.length);

	document.getElementById(ind).value = obj1.id.substring(0,1).toUpperCase();

	if(obj2.value.length>0)
		obj2.value = '';
}
function clearDValues(obj1,obj2)//@@For Delivery Charges, when Charge Type is Both
{
	var ind = obj1.id.substring(2,obj1.id.length);


	document.getElementById("d"+ind).value = obj1.id.substring(1,2).toUpperCase();


	if(obj2.value.length>0)
		obj2.value = '';
}

function Mandatory()
{
	var slab;
	var flat;
	var min;
	var max;

	var dSlab;
	var dFlat;
	var dMin;
	var dMax;
	
	var primaryUnit	=	'<%=primaryUnit%>'.toUpperCase();

	for(var j=0;j<<%=zoneCodes.length%>;j++)
	{
		min = document.getElementsByName("minValue");
		max = document.getElementsByName("maxValue");
		if(min[j].value.length==0)
		{
			alert("Please Enter the Minimum Charge at Lane "+(j+1));
			min[j].focus();
			return false;
		}
		if(isNaN(min[j].value))
		{
				alert('Please Enter Numeric Values Only');
				min[j].focus();
				return false;
		}
		for(var i=0;i<<%=slabRates.length%>;i++)
		{
			slab = document.getElementById("s"+i+""+j);
			flat = document.getElementById("f"+i+""+j);

			if(isNaN(slab.value) || isNaN(flat.value))
			{
				alert('Please Enter Numeric Values Only');
				return false;
			}

			if(slab.value.length==0 && flat.value.length==0)
			{
				alert('Please Enter Either Flat or Slab Value at Lane '+(j+1));
				slab.focus();
				return false;
			}
		}
		if(max[j]!=null)
		{
			if(max[j].value.length==0)
			{
				alert("Please Enter the Maximum Charge at Lane "+(j+1));
				max[j].focus();
				return false;
			}
			if(isNaN(max[j].value))
			{
				alert('Please Enter Numeric Values Only');
				max[j].focus();
				return false;
			}
		}
		if(document.getElementsByName("densityRatio"+j)[0]!=null && document.getElementsByName("densityRatio"+j)[0].value.length==0)
		{
			if(primaryUnit == 'KG' || primaryUnit=='LB' || primaryUnit=='CBM' || primaryUnit=='CFT')
			{
				alert("Please Enter the Density Ratio at Lane "+(j+1));
				document.getElementsByName("densityRatio"+j)[0].focus();
				return false;
			}
			else
			{
				document.getElementsByName("densityRatio"+j)[0].value='';
			}
		}
    //@@Added for the WPBN issue-54554
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
		
	for(var j=0;j<<%=zoneCodes.length%>;j++)
	{
		
		dMin = document.getElementsByName("dMinValue");
		dMax = document.getElementsByName("dMaxValue");

		if(dMin[j]!=null)
		{
			if(dMin[j].value.length==0)
			{
				alert("Please Enter the Minimum Charge at Lane "+(j+1));
				dMin[j].focus();
				return false;
			}
			if(isNaN(dMin[j].value))
			{
				alert('Please Enter Numeric Values Only');
				dMin[j].focus();
				return false;
			}
			for(var i=0;i<<%=slabRates.length%>;i++)
			{
				dSlab = document.getElementById("dS"+i+""+j);
				dFlat = document.getElementById("dF"+i+""+j);

				if(isNaN(dSlab.value) || isNaN(dFlat.value))
				{
					alert('Please Enter Numeric Values Only');
					return false;
				}

				if(dSlab.value.length==0 && dFlat.value.length==0)
				{
					alert('Please Enter Either Flat or Slab Value at Lane '+(j+1));
					dSlab.focus();
					return false;
				}
			}
		}
		if(dMax[j]!=null)
		{
			if(dMax[j].value.length==0)
			{
				alert("Please Enter the Maximum Charge at Lane "+(j+1));
				dMax[j].focus();
				return false;
			}
			if(isNaN(dMax[j].value))
			{
				alert('Please Enter Numeric Values Only');
				dMax[j].focus();
				return false;
			}
		}
		if(document.getElementsByName("dDensityRatio"+j)[0]!=null && document.getElementsByName("dDensityRatio"+j)[0].value.length==0)
		{
			if(primaryUnit == 'KG' || primaryUnit=='LB' || primaryUnit=='CBM' || primaryUnit=='CFT')
			{
				alert("Please Enter the Density Ratio for Delivery Charges at Row "+(j+1));
				document.getElementsByName("dDensityRatio"+j)[0].focus();
				return false;
			}
			else
				document.getElementsByName("dDensityRatio"+j)[0].value='';
		}
    //@@Added for the WPBN issue-54554
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
</script>
<meta http-equiv="Content-Type" content="text/html; charset=windows-1252">
<title>Cartage Buy Charges-Add</title>
<link rel="stylesheet" href="ESFoursoft_css.jsp">
</head>

<body bgcolor="#FFFFFF">

<form method="post" onsubmit='return Mandatory()'>
<table width="100%" cellpadding="0" cellspacing="0" bgcolor="#FFFFFF">
 <tr>
	<td>
	<table width="100%" cellpadding="0" cellspacing="0" bgcolor="#FFFFFF">
		  <tr valign="top" class="formlabel">
			<td	colspan="2"><table cellpadding="4" cellspacing="1" width="100%"><tr class='formlabel'><td>Cartage Buy Charges - Add</td><td align=right>QS1050712</td></tr></table></td>
		  </tr></table>


		  <table border="0" width="100%"  cellspacing="1" cellpadding="4" bgcolor="#FFFFFF">
		  <tr class='formdata'> 
              <td >WeightClass:<br> <select size="1" name="weightClass" class='select'>
               <option  value="G">General</option>
               
              </select>
              </td>
              <td colspan='2'>Charge Basis:<b><br>
              <%=chargeBasis!=null?chargeBasis:""%>
              </b>
              </td>
              <td colspan="2">Charge Type:<br>
                <b><%=chargeType!=null?chargeType:""%></b>
              </td>
			 <td colspan="2">Weight Break:<b><br>
              <%=weightBreak!=null?weightBreak:""%></b>
              </td>
			 <td colspan="2">Rate Type:<b><br>
              <%=rateType!=null?rateType:""%>
              </b>
              </td>
			<td colspan='6'>Currency: <b><br>
                <%=currencyId!=null?currencyId:""%>
                </b>
              </td>
              </tr>
			</table>
			<table border="0" width="100%"  cellspacing="1" cellpadding="4" bgcolor="#FFFFFF">
          <tr class='formdata'> 
            <td colspan="17"><b>Please Enter Cartage Charges For Zones: </b></td>
          </tr>
          <tr class='formheader' align="center"> 
            <td  colspan="2"><%=label%></td>
			<!-- @@Added by subrahmanyam for Enhancement 170759 on 25/May/09 -->
            <td><b>Base</b></td>
			<!-- @@ Ended For 170759 -->
            <td><b>Min</b>*</td>
<%
			for(int i=0;i<slabRates.length;i++)
			{
%>				<td><%=slabRates[i]!=null?slabRates[i]:""%></td>
<%			}
			if("Y".equals(maxChargeFlag))
			{
%>	           <td>Max**</td>
<%			}
%>			<td>Density Ratio</td>
          </tr>
<%
		for(int j=0;j<zoneCodes.length;j++)
		{
%>
          <tr class='formdata' align="center"> 
			  <td valign="bottom" colspan="2">Zone <%=zoneCodes[j]!=null?zoneCodes[j]:""%></td>
<!-- @@Added by subrahmanyam for Enhancement 170759 on 25/May/09 -->
				<td>
				<input type='text' class='text' name="baseValue" size="5" maxlength='8'>
		      </td>
<!-- @@ Ended For 170759 -->
			  <td>
				<input type='text' class='text' name="minValue" size="5" maxlength='8'>
			  </td>
<%
			for(int i=0;i<slabRates.length;i++)
			{
%>				<td>
					<input type='text' class='text' name="slabValues" size="2" onkeypress='clearValues(this,document.getElementById("f<%=i+""+j%>"))' id='s<%=i+""+j%>' AUTOCOMPLETE="off" onpaste='clearValues(this,document.getElementById("f<%=i+""+j%>"))' id='s<%=i+""+j%>' maxlength='8'><input type='text' class='text' name="flatValues" size="5" id='f<%=i+""+j%>' onkeypress='clearValues(this,document.getElementById("s<%=i+""+j%>"))' AUTOCOMPLETE="off" onpaste='clearValues(this,document.getElementById("s<%=i+""+j%>"))' maxlength='8'>
					<input type='hidden' name='indicator' id='<%=i+""+j%>'>
				</td>
			
<%			}
			if("Y".equals(maxChargeFlag))
			{
%>				<td>
					<input type='text' class='text' name="maxValue" size="5" maxlength='8'>
				</td>
<%
			}
%>           <td >
				<input type='text' class='text' name="densityRatio<%=j%>" size="8">&nbsp;<input type='button' name='densityLOV' class='input' onClick='showLOV("densityRatio<%=j%>")' value='...'>
			  </td></tr>
<%
		}
		if("Both".equals(chargeType))
		{
%>
           <tr class=formheader align="center">
            <td colspan="2">DELIVERY CHARGES</td>
<!-- @@Added by subrahmanyam for Enhancement 170759 on 25/May/09 -->
            <td><b>Base</b></td>
			<!-- @@ Ended For 170759 -->
            <td><b>Min</b>*</td>
<%
			for(int i=0;i<slabRates.length;i++)
			{
%>				<td><%=slabRates[i]!=null?slabRates[i]:""%></td>
<%			}
			if("Y".equals(maxChargeFlag))
			{
%>	           <td>Max**</td>
<%			}
%>			<td>Density Ratio</td>
          </tr>
<%
		for(int j=0;j<zoneCodes.length;j++)
		{
%>
          <tr class='formdata'  align="center"> 
			  <td colspan="2">Zone <%=zoneCodes[j]!=null?zoneCodes[j]:""%></td>
  <!-- @@Added by subrahmanyam for Enhancement 170759 on 25/May/09 -->
				<td>
				<input type='text' class='text' name="dBaseValue" size="5" maxlength='8'>
		      </td>
<!-- @@ Ended For 170759 -->
			<td>
				<input type='text' class='text' name="dMinValue" size="5" maxlength='8'>
            </td>
<%
			for(int i=0;i<slabRates.length;i++)
			{
%>				<td>
					<input type='text' class='text' name="dSlabValues" size="2" onkeypress='clearDValues(this,document.getElementById("dF<%=i+""+j%>"))' id='dS<%=i+""+j%>' AUTOCOMPLETE="off" onpaste='clearDValues(this,document.getElementById("dF<%=i+""+j%>"))' maxlength='8'><input type='text' class='text' name="dFlatValues" size="5" id='dF<%=i+""+j%>' onkeypress='clearDValues(this,document.getElementById("dS<%=i+""+j%>"))' AUTOCOMPLETE="off" onpaste='clearDValues(this,document.getElementById("dS<%=i+""+j%>"))' maxlength='8'>
					<input type='hidden' name='dIndicator' id='d<%=i+""+j%>'>
				</td>
			
<%			}
			if("Y".equals(maxChargeFlag))
			{
%>	    
				<td>
					<input type='text' class='text' name="dMaxValue" size="5" maxlength='8'>
				</td>
<%
			}
%>			 <td >
				<input type='text' class='text' name="dDensityRatio<%=j%>" size="8">&nbsp;<input type='button' name='densityLOV' class='input' onClick='showLOV("dDensityRatio<%=j%>")' value='...'>
			  </td></tr>          
<%
		}
	}
%>
          
        </table>
     
      
      <table border="0" width="100%" cellspacing="1" cellpadding="4" bgcolor="#FFFFFF">
          <tr class='denotes'> 
            <td>
			 *	Denotes Mandatory<br>
             ** Maximum charge per truckload
            </td>
            <td  align="right">
			<input type="reset" value="Reset" class='input'>
			<input type="Submit" value="Submit" class='input'>
			<input type="hidden" name ="Operation"  value="<%=operation%>" >
			<input type="hidden" name="subOperation" value="slabAdd">
            </td>
          </tr>
        </table>
	</tr>
 </td>
</table>
</form>

   
</body>

</html>