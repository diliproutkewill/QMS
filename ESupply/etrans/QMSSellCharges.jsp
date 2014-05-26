
<%--
<%--

Copyright (c) 1999-2001 by FourSoft, Pvt Ltd. All Rights Reserved.
This software is the proprietary information of FourSoft, Pvt Ltd.
Use is subject to license terms.
esupply - v 1.x.
Program	Name	: QMSSellChargesAdd.jsp
Product Name	: QMS
Module Name		: SellCharges Master
Task		    : Adding/View/Modify
Date started	: -08-2005 	
Date Completed	: -08-2005 with all the validations
Date modified	:  
Author    		: I.V.Sekhar Merrinti
Description		: The application "Adding/View/Modify" Sellcharges information
Actor           :
Related Document: CR_DHLQMS_1005
--%>
<%@ page import="java.util.ArrayList,
				org.apache.log4j.Logger,
				com.qms.operations.charges.java.BuychargesHDRDOB,
				com.qms.operations.charges.java.BuychargesDtlDOB,
				com.foursoft.esupply.common.java.ErrorMessage,
				com.foursoft.esupply.common.java.KeyValue"
%>
<jsp:useBean id = "loginbean" class = "com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope = "session"/>
<%!
  private static Logger logger = null;
	private static final String FILE_NAME="QMSSellCharges.jsp";
%>
<%
  logger  = Logger.getLogger(FILE_NAME);	
	BuychargesDtlDOB buychargesDtlDOB	=	null;
	ArrayList dtlList					=	null;
	String operation					=	request.getParameter("Operation");

	String subOperation					=	request.getParameter("subOperation");
	BuychargesHDRDOB buychargesHDRDOB	=	(BuychargesHDRDOB)request.getAttribute("BuyChargesHDRDtls");	
	String chargeId						=	request.getParameter("chargeId");
	String chargeDescriptionId			=	request.getParameter("chargeDescriptionId");
	String chargeBasisId				=	request.getParameter("chargeBasisId");
	String chargeBasisDescription		=	request.getParameter("chargeBasisDescription");
	String currencyId					=	request.getParameter("currencyId");
	String rateBreak					=	request.getParameter("rateBreak");
	String rateType						=	request.getParameter("rateType");
	String weightClass					=	request.getParameter("weightClass");
	String densityGrpCode				=	request.getParameter("densityGrpCode");
	String primaryUnit					=	request.getParameter("primaryUnit");
	String secondaryUnit				=	request.getParameter("secondaryUnit");
  String chargeGroupId        =	request.getParameter("chargeGroupId"); //added by VLAKSHMI for issue 174838 on 29/06/2009
	String base				=	"";
	String min				=	"";
	String max				=	"";
	String flat				=	"";
	String[] chargeSlab		=	new String[14];
	String[] chargeRate		=	new String[14];
	String[] chargeFlatRate	=	new String[14];
	String	 buychargeID	=	null;
    
	String   headerMsg      =   null;

	String readOnly = "";
	String disabled = "";
//logger.info("chargeGroupId33"+chargeGroupId);
try{
// @@ Commented by subrahmanyam for wpbn issue: 146440 on 16/02/09  
  /*if(currencyId == null || currencyId.equals(""))
    { currencyId = loginbean.getCurrencyId();}*/
    
	headerMsg = (String)request.getAttribute("errorMsg");
	 
	if("search".equals(subOperation))
	{
		if(buychargesHDRDOB!=null)
		{
			buychargeID		=	buychargesHDRDOB.getBuySellChargeId()+"";
			chargeBasisId	=	buychargesHDRDOB.getChargeBasisId();
			chargeBasisDescription = buychargesHDRDOB.getChargeBasisDesc();
			densityGrpCode  =   buychargesHDRDOB.getDensityGrpCode();
			primaryUnit		=	buychargesHDRDOB.getPrimaryUnit();
			currencyId		=   buychargesHDRDOB.getCurrencyId();//@@ Added by subrahmanyam for wpbn issue: 146440 on 16/02/09  

			dtlList	=	(ArrayList)buychargesHDRDOB.getBuyChargeDtlList();

			if(dtlList!=null && dtlList.size()>0)
			{
				if("Percent".equals(buychargesHDRDOB.getRateBreak()) || "Absolute".equals(buychargesHDRDOB.getRateBreak()))
				{
					buychargesDtlDOB	=	(BuychargesDtlDOB)dtlList.get(0);
					flat				=	new Double(buychargesDtlDOB.getChargeRate()).toString();
				}else if("Flat".equals(buychargesHDRDOB.getRateBreak()) || "Flat%".equals(buychargesHDRDOB.getRateBreak()) || "Slab".equals(buychargesHDRDOB.getRateBreak()) || "Slab%".equals(buychargesHDRDOB.getRateBreak()))
				{
					int cnt		=	0;
					for(int i=0;i<dtlList.size();i++)
					{
								buychargesDtlDOB	=	(BuychargesDtlDOB)dtlList.get(i);
								//Logger.info(FILE_NAME,"buychargesDtlDOB.getChargeRate():"+buychargesDtlDOB.getChargeRate());
								if(buychargesDtlDOB.getChargeSlab().equals("BASE"))
									{	base	=	new Double(buychargesDtlDOB.getChargeRate()).toString();}
								else if(buychargesDtlDOB.getChargeSlab().equals("MIN"))
									{	min		=	new Double(buychargesDtlDOB.getChargeRate()).toString();}
								else if(buychargesDtlDOB.getChargeSlab().equals("MAX"))
									{	max		=	new Double(buychargesDtlDOB.getChargeRate()).toString();}
								else if(buychargesDtlDOB.getChargeSlab().equals("Flat"))
									{	flat	=	new Double(buychargesDtlDOB.getChargeRate()).toString();}
								else
								{
									chargeSlab[cnt]		=	buychargesDtlDOB.getChargeSlab();
									//if("F".equals(buychargesDtlDOB.getChargeRate_indicator()))//Highlighted text is for slab
									if("S".equals(buychargesDtlDOB.getChargeRate_indicator()))
										{	chargeFlatRate[cnt++]	=	new Double(buychargesDtlDOB.getChargeRate()).toString();}
									else
										{	chargeRate[cnt++]	=	new Double(buychargesDtlDOB.getChargeRate()).toString();}
								}
					}
				}
			}
		}
	}
	
	/*Logger.info(FILE_NAME,"operation :::"+operation);
	Logger.info(FILE_NAME,"subOperation :::"+subOperation);
	Logger.info(FILE_NAME,"buychargesHDRDOB :::"+buychargesHDRDOB);
	Logger.info(FILE_NAME,"chargeId"+chargeId);
	Logger.info(FILE_NAME,"chargeBasisId"+chargeBasisId);
	Logger.info(FILE_NAME,"rateBreak"+buychargesHDRDOB.getRateBreak());
	Logger.info(FILE_NAME,"rateType"+buychargesHDRDOB.getRateType());
	Logger.info(FILE_NAME,"base"+base);
	Logger.info(FILE_NAME,"min"+min);
	Logger.info(FILE_NAME,"max"+max);
	Logger.info(FILE_NAME,"flat"+flat);
	//Logger.info(FILE_NAME,"buychargesHDRDOB.getRateBreak()"+buychargesHDRDOB.getRateBreak());
	//ogger.info(FILE_NAME,"buychargesHDRDOB.getRateType()"+buychargesHDRDOB.getRateType());*/
	
%>
<html>
<head>
<title>SellCharges Add</title>       <%@ include file="../ESEventHandler.jsp" %>
<link rel="stylesheet" href="ESFoursoft_css.jsp">
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<META HTTP-EQUIV="Expires" CONTENT="0">
<script src ="html/eSupply.js"></script>
<script>
	var Win  = null;//Added by VLAKSHMI for CR #170761 on 20090626
  // modified by VLAKSHMI for issue 174838 on 29/06/2009 
	function setSelectedDetails(chargeGroupId,chargeId,chargeDescriptionId,chargeBasisId,chargeBasisDescription,currencyId,rateBreak,rateType,weightClass)
	{   
		document.sellChargeform.chargeGroupId.value			=	chargeGroupId;
		document.sellChargeform.chargeId.value				=	chargeId;
		document.sellChargeform.chargeDescriptionId.value	=	chargeDescriptionId;
		document.sellChargeform.chargeBasisId.value			=	chargeBasisId;
		document.sellChargeform.chargeBasisDescription.value=	chargeBasisDescription;
		document.sellChargeform.currencyId.value			=	currencyId;
		document.sellChargeform.rateBreak.value				=	rateBreak;
		assaignRateType();
		document.sellChargeform.rateType.value				=	rateType;
		document.sellChargeform.weightClass.value			=	weightClass;

	}
	//added by subrahmanyam for 180161
function getDotNumberCode(input)    // Numbers + Dot
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
			if(input.value.length == index+6)
			return false;
		}
	  }
	}
	return true;
}

//@@ Ended by subrahmanyam for 180161
	function onLoadCheckDetails()
	{
		var spanContent	=	"";
		var flag;
<%
		if("search".equals(subOperation))
		{
%>
 // modified by VLAKSHMI for issue 174838 on 29/06/2009
			setSelectedDetails('<%=chargeGroupId%>','<%=chargeId%>','<%=chargeDescriptionId%>','<%=chargeBasisId%>','<%=chargeBasisDescription%>','<%=currencyId%>','<%=rateBreak%>','<%=rateType%>','<%=weightClass%>');
<%
			if(buychargesHDRDOB!=null)
			{
				if(rateBreak!=null && rateType!=null && 
					rateBreak.equals(buychargesHDRDOB.getRateBreak()) && rateType.equals(buychargesHDRDOB.getRateType()) && 
					weightClass.equals(buychargesHDRDOB.getWeightClass()))
				{
					readOnly = "readOnly";
					disabled = "disabled";
%>
					spanContent = getSpanContent();
					<%--alert(spanContent) --%>
					setSpanContent(spanContent,"spanSlabs");
					assaignValues();
					document.sellChargeform.dummyBuychargesflag.value='F';
<%				}else
				{
%>
					/*flag = confirm("Incorrect combination selected\nSelect OK to automatically change combination as per existing Buy charges Or\nSelect CANCEL to define new Buy charges and margins for the selected combination");*/
//@@ Commented by subrahmanyam for 146440 on 16/02/09
					/*flag = confirm("Incorrect combination selected\nSelect OK to automatically change combination as per existing Buy charges Or\nSelect CANCEL to choose another combination");
					if(flag)
					{	*/
<%
						readOnly = "readOnly";
						disabled = "disabled";
%>
						document.sellChargeform.dummyBuychargesflag.value='F';	
             // modified by VLAKSHMI for issue 174838 on 29/06/2009
            setSelectedDetails('<%=chargeGroupId%>','<%=chargeId%>','<%=chargeDescriptionId%>','<%=chargeBasisId%>','<%=chargeBasisDescription%>','<%=currencyId%>','<%=buychargesHDRDOB.getRateBreak()%>','<%=buychargesHDRDOB.getRateType()%>','<%=buychargesHDRDOB.getWeightClass()%>');
						spanContent = getSpanContent();
						setSpanContent(spanContent,"spanSlabs");
						assaignValues();
	//@@ Commented by subrahmanyam for 146440 on 16/02/09
					/*}else
					{*/
						/*document.sellChargeform.dummyBuychargesflag.value='T';
						spanContent = getSpanContent();
						setSpanContent(spanContent,"spanSlabs");						*/
						/*setSpanContent(spanContent,"spanSlabs");
					}*/
<%				}
			}else if(headerMsg==null || headerMsg.trim().length()<=0)
			{
%>
				flag = confirm("Buycharges Not Found with the selected data(ChargeId,Chargebasis)\nSelect OK to define the Buycharge here itself Or\nSelect CANCEL to Choose another selection of Buycharges");
				if(flag)
				{
						document.forms[0].dummyBuychargesflag.value='T';
						spanContent = getSpanContent();
						setSpanContent(spanContent,"spanSlabs");					
				}else
				{
         // modified by VLAKSHMI for issue 174838 on 29/06/2009
						setSelectedDetails('','','','','','','','','');
						setSpanContent(spanContent,"spanSlabs");					
				}
<%
			}
		}
%>
	}
function assaignValues()
{
<%--alert("hi") --%>
	var rateBreak	='';
	var rateType	='';
<%	if("search".equals(subOperation) &&  buychargesHDRDOB!=null)
	{
%>
		rateBreak	='<%=(buychargesHDRDOB.getRateBreak())%>';
		rateType	='<%=buychargesHDRDOB.getRateType()%>';
<%	
	}
%>
		if('Percent'==rateBreak || 'Absolute'==rateBreak)
			{	
				document.getElementById("chargeRate1").value	=	'<%=flat%>';
			}
		else if('Flat'==rateBreak || 'Flat%'==rateBreak)
			{	
				document.getElementById("chargeRate1").value	=	'<%=(base!=null)?base:""%>';
				document.getElementById("chargeRate2").value	=	'<%=min%>';
				document.getElementById("chargeRate3").value	=	'<%=(max!=null)?max:""%>';
				document.getElementById("chargeRate4").value	=	'<%=flat%>';
			}
		else if('Slab'==rateBreak || 'Slab%'==rateBreak)
			{	
					document.forms[0].chargeRate[0].value	=	'<%=(base!=null)?base:""%>';
					document.forms[0].chargeRate[1].value	=	'<%=min%>';
					document.forms[0].chargeRate[2].value	=	'<%=(max!=null)?max:""%>';

<%					for(int i=3;i<14;i++)
					{
%>
						document.forms[0].chargeSlab[<%=i%>].value	=  '<%=(chargeSlab[i-3]!=null)?chargeSlab[i-3]:""%>';
						document.forms[0].chargeRate[<%=i%>].value	=  '<%=(chargeRate[i-3]!=null)?chargeRate[i-3]:""%>';
<%					}
%>
					if('Both'==rateType)
					{
<%						for(int i=0;i<11;i++)
						{
%>
							document.forms[0].chargeFlatRate[<%=i%>].value	=  '<%=(chargeFlatRate[i]!=null)?chargeFlatRate[i]:""%>';
<%						}
%>

					}
			}
}

function setMarginSpan()
{
	var data	=	"";
	var overallmargin	=	document.forms[0].overallmargin;
	var margintype		=	document.forms[0].margintype;
	var marginbasis		=	document.forms[0].marginbasis;
	var ratebreak		=	document.forms[0].rateBreak;
	var ratetype		=	document.forms[0].rateType;
	
	if((overallmargin.value!='') && (margintype.value!=''))
	{
		if(ratebreak.value=='Flat' || ratebreak.value=='Flat%')
		{
				if((marginbasis.value=='N'))
				{
					<%--ADDED BY SUBRAHMANYAM FOR THE ISSUE 143489--%>
					
					if(margintype.value == 'P' && overallmargin.value =='Y')
					{
						data	=	data	+"<table width='100%' border='0' cellspacing='1' cellpadding='' bgColor='#FFFFFF'>"+
									"<tr class='formdata'><td width='25%' height='25' align='center' valign='center'>"+
									"<input type='text' class='text' name='marginRate'  size=4 maxLength='10' "+
									"onKeyPress='return getDotNumberCode(this)' onBlur='checkNumbers1(this);'></td>"+
									"<td width='25%' height='25' align='center' valign='center'>"+
									"<input type='hidden' class='text' name='marginRate'  size=4 maxLength='10' onKeyPress='return getDotNumberCode(this)' value='0.0' onBlur='checkNumbers1(this);'></td>"+
									"<td width='25%' height='25' align='center' valign='center'>"+
									"<input type='hidden' class='text' name='marginRate'  size=4 maxLength='10' onKeyPress='return getDotNumberCode(this)' value='0.0'  onBlur='checkNumbers1(this);'></td>"+
									"<td width='25%' height='25' align='center' valign='center'>"+
									"<input type='hidden' class='text' name='marginRate'  size=4 maxLength='10' onKeyPress='return getDotNumberCode(this)' value='0.0'  onBlur='checkNumbers1(this);'></td></tr></table>";
					}
					else{ <%--ADDED BY SUBRAHMANYAM FOR THE ISSUE 143489 --%>

					data	=	data	+"<table width='100%' border='0' cellspacing='1' cellpadding='' bgColor='#FFFFFF'>"+
									"<tr class='formdata'><td width='25%' height='25' align='center' valign='center'>"+
									"<input type='text' class='text' name='marginRate'  size=4 maxLength='10' "+
									"onKeyPress='return getDotNumberCode(this)' onBlur='checkNumbers1(this);'></td>"+
									"<td width='25%' height='25' align='center' valign='center'>"+
									"<input type='text' class='text' name='marginRate'  size=4 maxLength='10' onKeyPress='return getDotNumberCode(this)' onBlur='checkNumbers1(this);'></td>"+
									"<td width='25%' height='25' align='center' valign='center'>"+
									"<input type='text' class='text' name='marginRate'  size=4 maxLength='10' onKeyPress='return getDotNumberCode(this)' onBlur='checkNumbers1(this);'></td>"+
									"<td width='25%' height='25' align='center' valign='center'>"+
									"<input type='text' class='text' name='marginRate'  size=4 maxLength='10' onKeyPress='return getDotNumberCode(this)' onBlur='checkNumbers1(this);'></td></tr></table>";
					}
					 
					if(document.getElementById("spanMargin")!=null)
					{
						setSpanContent(data,"spanMargin");
						<%--		document.getElementById("spanMargin").innerHTML=data;
						document.getElementById("spanMargin").style.display='block'; --%>
					}
					if(document.getElementById("spanValueBreaks")!=null)
					{
						setSpanContent("","spanValueBreaks");
				<%--		document.getElementById("spanValueBreaks").innerHTML="";
						document.getElementById("spanSlabs").style.display='block';--%>
					}
				}else if((marginbasis.value=='V'))
				{
					data=data+"<table width='100%' border='0' cellspacing='1' cellpadding='' bgColor='#FFFFFF'>"+
							  "<tr class='formdata'><td height='25' valign='center' colspan='11'><b>Value Breaks</b></td></tr>"+
							  "<tr class='formdata'><td height='25' valign='center' align='center'>"+
							  "<input type='text' class='text' name='marginSlab' size=4 maxLength='10' "+
							  " onKeyPress ='getDotNumberCode(this)' onBlur='checkNumbers1(this)'></td>";
							 for(var i=1;i<10;i++)
							 {
								 data=data+"<td height='25' valign='center' valign='center' align='center'><input type='text' class='text' name='marginSlab' size=4 maxLength='10' "+
							  "onKeyPress='return getDotNumberCode(this)' onBlur='checkNumbers1(this);'></td>";
							 }
							  data=data+"</tr><tr class='formdata'>";
							 for(var i=0;i<10;i++)
							 {
								 data=data+"<td height='25' valign='center' align='center'><input type='text' class='text' name='marginRate' size=4 maxLength='10' "+
							  "onKeyPress='return getDotNumberCode(this)' onBlur='checkNumbers1(this);'></td>";
							 }
							  data=data+"</tr></table>";

					if(document.getElementById("spanValueBreaks")!=null)
					{
						setSpanContent(data,"spanValueBreaks");
				<%--		document.getElementById("spanValueBreaks").innerHTML=data;
						document.getElementById("spanSlabs").style.display='block';--%>
					}
					if(document.getElementById("spanMargin")!=null)
					{
						setSpanContent("","spanMargin");
				<%--		document.getElementById("spanMargin").innerHTML="";
						document.getElementById("spanSlabs").style.display='block';--%>
					}
				}
			
		}else if(ratebreak.value=='Absolute' || ratebreak.value=='Percent')
		{
				data	=	data	+"<input type='text' class='text' name='marginRate'  size=4 maxLength='10' "+
									"onKeyPress='return getDotNumberCode(this)' onBlur='checkNumbers1(this);'>";
				if(document.getElementById("spanMargin")!=null)
				{
						setSpanContent(data,"spanMargin");
				<%--		document.getElementById("spanMargin").innerHTML="";
						document.getElementById("spanSlabs").style.display='block';  --%>
				}
		}else if(ratebreak.value=='Slab' || ratebreak.value=='Slab%')
		{
			if((overallmargin.value=='Y') && ratetype.value!='Both')
			{
				if((margintype.value=='P'))
				{
					data	=	data	+"<table width='100%' border='0' cellspacing='1' bgColor='#FFFFFF'>"+
										"<tr class='formdata'><td height='25' valign='center'>&nbsp;&nbsp;"+
										"<input type='text' class='text' name='marginRate'  size=6 maxLength='10' "+
										"onKeyPress='return getDotNumberCode(this)' onBlur='checkNumbers1(this)'></td></tr></table>";
				}else
				{
					data	=	data	+"<table width='100%' border='0' cellspacing='1' bgColor='#FFFFFF'>"+
										"<tr class='formdata'><td width='7%' height='25' align='center' valign='center'>"+
										"<input type='text' class='text' name='marginRate'  size=4 maxLength='10' "+
										"onKeyPress='return getDotNumberCode(this)' onBlur='checkNumbers1(this);'></td>"+
										"<td width='7%' height='25' align='center' valign='center'>"+
										"<input type='text' class='text' name='marginRate'  size=4 maxLength='10' onKeyPress='return getDotNumberCode(this)' onBlur='checkNumbers1(this);'></td>"+
										"<td width='7%' height='25' align='center' valign='center'>"+
										"<input type='text' class='text' name='marginRate'  size=4 maxLength='10' onKeyPress='return getDotNumberCode(this)' onBlur='checkNumbers1(this);'></td>"+
										"<td height='25' valign='center' colspan='11'>Slab Margin "+
										"<input type='text' class='text' name='marginRate'  size=4 maxLength='10' onKeyPress='return getDotNumberCode(this)' onBlur='checkNumbers1(this);'></td></tr></table>";
				}
			}else if((overallmargin.value=='N') || (ratetype.value=='Both'))
			{
					var width = 7;
					var addValue = 0;
					if( ratetype.value=='Both')
					{	width = 5;addValue=1;}
					data	=	data	+"<table width='100%' border='0' cellspacing='1' cellpadding='' bgColor='#FFFFFF'>"+
										"<tr class='formdata'><td width='"+width+"%' height='25' align='center' valign='center'>"+
										"<input type='text' class='text' name='marginRate'  size=4 maxLength='10' "+
										"onKeyPress='return getDotNumberCode(this)' onBlur='checkNumbers1(this);'></td>"+
										"<td width='"+width+"%' height='25' align='center' valign='center'>"+
										"<input type='text' class='text' name='marginRate'  size=4 maxLength='10' onKeyPress='return getDotNumberCode(this)' onBlur='checkNumbers1(this);'></td>"+
										"<td width='"+width+"%' height='25' align='center' valign='center'>"+
										"<input type='text' class='text' name='marginRate'  size=4 maxLength='10' onKeyPress='return getDotNumberCode(this)' onBlur='checkNumbers1(this);'></td>";
					for(i=0;i<11;i++)
					{
						data = data+"<td width='"+(width+addValue)+"%' height='25' align='center' valign='center'>"+
									"<input type='text' class='text' name='marginRate'  size=4 maxLength='10' onKeyPress='return getDotNumberCode(this)' onBlur='checkNumbers1(this);'></td>";
					}
				    data = data+"</tr></table>"
			}
				if(document.getElementById("spanMargin")!=null)
				{
						setSpanContent(data,"spanMargin");
				<%--		document.getElementById("spanMargin").innerHTML="";
						document.getElementById("spanSlabs").style.display='block'; --%>
				}
		}
	}

}
function showCalculationType(type)
{
	var data	=	""
	data	=	"<table width='940' border='0' cellspacing='1' cellpadding='' bgColor='#FFFFFF'>"+
				"<tr class='formdata'><td>Overall Margin:<select name='overallmargin' class='select' onChange='setMarginSpan()'>"+
				"<option value=''></option><option value='N'>No</option>";
	if(type=='Both')
	{	data = data+"</select></td>";}
	else
	{   data = data+"<option value='Y'>Yes</option></select></td>";}

	data = data +"<td>Margin Type<select name='margintype' class='select' onChange='setMarginSpan()'><option value=''></option>"+
				"<option value='A'>Absolute</option>"+
				"<option value='P'>Percent</option></select></td>";
	if(type=='Flat')
	{
		data	= data+"<td><input type='hidden' name='marginbasis' value='N'></td>";
		<%--Margin Basis<select name='marginbasis' class='select' onChange='setMarginSpan()'><option value=''></option>"+
					   "<option value='N'>Normal</option>";
					   "<option value='V'>Value</option></select> --%>
					   
		
	}
	data	=	data+"</tr></table>";
	return data;
}

function showFlat()
	{
		var chargeIdValue = ""
		var data = "";
		var chargeObj = document.getElementById("chargeId1");
		
		if(chargeObj!=null && chargeObj.value!=0 && chargeObj.value!="")
		{
			chargeIdValue = chargeObj.value;
	   <%-- alert(chargeIdValue); --%>
			data = showCalculationType('Flat');
			data = data +"<table width='940' border='0' cellspacing='1' cellpadding='' bgColor='#FFFFFF'>"+
						"<tr class='formdata' colspan=''><td rowspan=2 width='10%' height='50' valign='center'><b>ChargeId: <br>"+chargeIdValue+"</b></td>"+
						"<td width='7%' height='25' align='center' valign='center'><b>BASE</b><input type=hidden name='chargeSlab' value='BASE'></td>"+
						"<td width='7%' height='25' align='center' valign='center'><b>MIN</b><input type=hidden name='chargeSlab' value='MIN'></td>"+
						"<td width='7%' height='25' align='center' valign='center'><b>MAX</b><input type=hidden name='chargeSlab' value='MAX'></td>"+
						"<td width='7%' height='25' align='center' valign='center'><b>FLAT</b><input type=hidden name='chargeSlab' value='Flat'></td>"+
						"<td rowspan=3 width='62%' height='75'><span id='spanValueBreaks'></span></td></tr>"+
						"<tr class='formdata' colspan='' valign='center' >"+
						"<td width='7%' height='25' align='center' valign='center'><input type='text' class='text' name='chargeRate' id='chargeRate1' value='' size=4  maxLength='10' onKeyPress='return getDotNumberCode(this)' onBlur='checkNumbers1(this);' <%=readOnly%>></td>"+
						"<td width='7%' height='25' align='center' valign='center'><input type='text' class='text' name='chargeRate' id='chargeRate2' value='' size=4  maxLength='10' onKeyPress='return getDotNumberCode(this)' onBlur='checkNumbers1(this);' <%=readOnly%>></td>"+
						"<td width='7%' height='25' align='center' valign='center'><input type='text' class='text' name='chargeRate' id='chargeRate3' value='' size=4  maxLength='10' onKeyPress='return getDotNumberCode(this)' onBlur='checkNumbers1(this);' <%=readOnly%>></td>"+
						"<td width='7%' height='25' align='center' valign='center'><input type='text' class='text' name='chargeRate' id='chargeRate4' value='' size=4  maxLength='10' onKeyPress='return getDotNumberCode(this)' onBlur='checkNumbers1(this);' <%=readOnly%>></td></tr>"+
						"<tr class='formdata'><td width='10%' height='25'><b>Margin</b></td><td colspan='4' height='25'>"+
						"<span id='spanMargin'></span></td></tr></table>";
		 }
		return data;
	}
	function showAbsValue()
	{
		var chargeIdValue = ""
		var data = "";
		var chargeObj = document.getElementById("chargeId1");
		if(chargeObj!=null && chargeObj.value!=0 && chargeObj.value!="")
		{
			chargeIdValue = chargeObj.value;
			data = showCalculationType('AbsRPersent');
			data = data +"<table width='940' border='0' cellspacing='1' cellpadding='4' bgColor='#FFFFFF'>"+
						"<tr class='formdata'  colspan='' valign='Top'><td rowspan=2 width='10%' ><b>ChargeId: <br>"+chargeIdValue+"</b></td>"+
						"<td width='10%' ><b>FLAT</b><input type=hidden name='chargeSlab' value='AbsRPersent'></td>"+
						"<td></td></tr>"+
						"<tr class='formdata' colspan='' valign='Top'>"+
						"<td width='10%'><input type='text' class='text' id='chargeRate1' name='chargeRate' value='' size=4 maxLength='10' onKeyPress='return getDotNumberCode(this)' onBlur='checkNumbers1(this);' <%=readOnly%>></td>"+ 
						"<td></td></tr><tr class='formdata'><td width='10%'><b>Margin</b></td><td colspan='1'>"+
						"<span id='spanMargin'></span></td><td></td></tr></table>";
	  }
		return data;
	}
	function showSlab()
	{
		var chargeIdValue = ""
	var deptIdValue = ""
	var data				 = "";
	var chargeObj		 = document.getElementById("chargeId1");
	var i=0
	if(chargeObj!=null && chargeObj.value!=0 && chargeObj.value!="")
	{
		chargeIdValue = chargeObj.value;
		data = showCalculationType('Slab');
		data = data +"<table width='940' border='0' cellspacing='1' cellpadding='' bgColor='#FFFFFF'>"+
					"<tr class='formdata' colspan='' valign='Top'><td rowspan=2 width='10%' height='50' valign='center'><b>ChargeId: <br>"+chargeIdValue+"</b></td>"+
					"<td width='6%' height='25' align='center' valign='center'><b>BASE</b>"+
					"<input type=hidden name='chargeSlab' value='BASE'></td>"+
					"<td width='6%' height='25' align='center' valign='center'><b>MIN</b>"+
					"<input type=hidden name='chargeSlab' value='MIN'></td>"+
					"<td width='6%' height='25' align='center' valign='center'><b>MAX</b>"+
					"<input type=hidden name='chargeSlab' value='MAX'></td>"+
					"<td width='6%' height='25' align='center' valign='center'>"+
					"<input type='text' class='text' name='chargeSlab' value='' size=4  maxLength='6' onBlur='checkNumbers1(this)'   ></td>";
					for(i=0;i<10;i++)
					{
						data=data+"<td width='6%' height='25' align='center' valign='center'><input type='text' class='text' name='chargeSlab' value='' size=4 maxLength='10' onKeyPress='return getDotNumberCode(this)' <%=readOnly%>  onBlur='checkNumbers1(this);'></td>";
					}
					data	=	data+"</tr>"+
									"<tr class='formdata' colspan='' valign='Top'>";
					for(i=0;i<14;i++)
					{
						data	=	data+"<td width='6%' height='25' align='center' valign='center'><input type='text' class='text' name='chargeRate' value='' size=4 maxLength='10'  onKeyPress='return getDotNumberCode(this)' <%=readOnly%>   onBlur='checkNumbers1(this);'></td>";
					}
					data	=	data+"</tr><tr class='formdata' height='25'><td width='10%' height='25'><b>Margin</b></td><td colspan='14'><span id='spanMargin'></span></td></tr></table>";
		}
    return data;
	}
	function showBoth()
	{
			var chargeIdValue = ""
		var deptIdValue = ""
		var data				 = "";
		var chargeObj		 = document.getElementById("chargeId1");
		
		if(chargeObj!=null && chargeObj.value!=0 && chargeObj.value!="")
		{
			chargeIdValue = chargeObj.value;
			data = showCalculationType('Both');
			data = data +"<table width='940' border='0' cellspacing='1' bgColor='#FFFFFF'>"+
						"<tr class='formdata' colspan='' valign='Top'><td rowspan=2 width='8%' height='50' valign='center'><b>ChargeId: <br>"+chargeIdValue+"</b></td></td>"+
						"<td width='5%' height='25' align='center' valign='center'><b>BASE</b>"+
						"<input type=hidden name='chargeSlab' value='BASE'></td>"+
						"<td width='5%' height='25' align='center' valign='center'><b>MIN</b>"+
						"<input type=hidden name='chargeSlab' value='MIN'></td>"+
						"<td width='5%' height='25' align='center' valign='center'><b>MAX</b>"+
						"<input type=hidden name='chargeSlab' value='MAX'></td>"+
						"<td width='6%' height='25' align='center' valign='center'>"+
						"<input type='text' class='text' name='chargeSlab' value='' size=4  onBlur='checkNumbers1(this)'></td>";
						for(i=0;i<10;i++)
						{
							data=data+"<td width='6%' height='25' align='center' valign='center'>"+
									"<input type='text' class='text' name='chargeSlab' value='' size=4 onKeyPress='return getDotNumberCode(this)' onBlur='checkNumbers1(this);' <%=readOnly%> ></td>";
						}
						data	=	data+"</tr>"+
									"<tr class='formdata' valign='Top'>"+
									"<td width='5%' height='25' align='center' valign='center'><input type='text' class='text' name='chargeRate' value='' size=4 maxLength='10' onKeyPress='return getDotNumberCode(this)' onBlur='checkNumbers1(this);' <%=readOnly%>></td>"+
									"<td width='5%' height='25' align='center' valign='center'><input type='text' class='text' name='chargeRate' value='' size=4 maxLength='10' onKeyPress='return 	getDotNumberCode(this)'  <%=readOnly%> onBlur='checkNumbers1(this);'></td>"+
									"<td width='5%' height='25' align='center' valign='center'><input type='text' class='text' name='chargeRate' value='' size=4 maxLength='10' onKeyPress='return getDotNumberCode(this)' onBlur='checkNumbers1(this);' <%=readOnly%>></td>";
						for(i=1;i<12;i++)
						{
							data=data+"<td width='6%' height='25' align='center' valign='center'><input type='text' class='textHighlight' name='chargeFlatRate' id='chargeFlatRate1@"+i+"' value='' size=1 maxLength='10' onKeyPress='clearDataOnFocus(this);return getDotNumberCode(this);'  <%=readOnly%> onpaste='return false;' autocomplete='off'><input type='text' class='text' name='chargeRate' id='chargeRate1@"+i+"' value='' size=3 maxLength='10' onKeyPress='clearDataOnFocus(this);return getDotNumberCode(this);'  onpaste='return false;' <%=readOnly%> autocomplete='off' ></td>";
						}
						data	=	data+"</tr><tr class='formdata' ><td width='8%' height='25'><b>Margin</b></td><td colspan='14'><span id='spanMargin'></span></td></tr></table>";
			}
		return data;
	}
	function showCharges(chargeId,index)
	{
			var operation	 = document.forms[0].operation.value;
			var termid		 = '<%=loginbean.getTerminalId()%>';
			var GroupId	=	 document.forms[0].chargeGroupId.value;//Added by VLAKSHMI for CR #170761 on 20090626
			//alert(GroupId)
			<%--Commented and Modified By Sunil for LOV issue.
			Url='etrans/QMSLOVChargeIds.jsp?searchString=&shipmentMode=&index='+index+'&Operation='+operation+'&teminalId='+termid+'&name='+chargeId+'&chargeGroupId='+GroupId+'&fromWhere=sellcharges&selection=single';
			--%>
			var searchStr    =document.forms[0].chargeId.value;	
			Url='etrans/QMSLOVChargeIds.jsp?searchString='+searchStr+'&shipmentMode=&index='+index+'&Operation='+operation+'&teminalId='+termid+'&name='+chargeId+'&chargeGroupId='+GroupId+'&fromWhere=sellcharges&selection=single';	
			
			var Bars = 'directories=no, location=no,menubar=no,status=no,titlebar=no';
			var Options='scrollbar=yes,width=400,height=360,resizable=no';
			var Features=Bars+' '+Options;
			var Win=open(Url,'Doc',Features);
	}
	function showChargeBasis(chargeBasisId,index)
	{
			var operation	 = document.forms[0].operation.value;
			var termid		 = '<%=loginbean.getTerminalId()%>';
			var	searchStr	 = document.getElementById(chargeBasisId).value;
			/*var	searchStr	 = document.getElementById(chargeBasisId).value; Url='etrans/QMSLOVChargeBasisIds.jsp?searchString=&shipmentMode=&index='+index+'&Operation='+operation+'&teminalId='+termid+'&name='+chargeBasisId+'&fromWhere=sellcharges&selection=single';
			var Bars = 'directories=no, location=no,menubar=no,status=no,titlebar=no';
			var Options='scrollbar=yes,width=400,height=360,resizable=no';
			var Features=Bars+' '+Options;
			var Win=open(Url,'Doc',Features);*/
			var tabArray = '';
			var formArray = '';
			var lovWhere	=	"";
			formArray = 'chargeBasisId'+index+',chargeBasisDescription'+index+',primaryUnit'+index+',secondaryUnit'+index;		
			tabArray = 'CHARGEBASIS,BASIS_DESCRIPTION,PRIMARY_BASIS,SECONDARY_BASIS';
			Url	
			<%--="qms/ListOfValues.jsp?lovid=CHARGEBASIS_MASTER&tabArray="+tabArray+"&formArray="+formArray+"&lovWhere="+lovWhere+"&operation=SELLCHARGEBASIS&search=  Commented By Sunil for SearchString--%>
			="qms/ListOfValues.jsp?lovid=CHARGEBASIS_MASTER&tabArray="+tabArray+"&formArray="+formArray+"&lovWhere="+lovWhere+"&operation=SELLCHARGEBASIS&search=where CHARGEBASIS LIKE'"+searchStr+"~'";
			<%--End here --%>
			Options	='width=750,height=750,resizable=yes';
<%--@@ Commented by subrahmanyam for the WPBN ISSUE: 146436 on 12/12/2008 --%>
			<%--Bars	='directories=0,location=0,menubar=no,status=yes,titlebar=0,scrollbars=0'; --%>
<%--@@ Added by subrahmanyam for the WPBN ISSUE: 146436 ON 12/12/2008--%>
				Bars	='directories=0,location=0,menubar=no,status=yes,titlebar=0,scrollbars=1';
			Features=Bars+','+Options;

			Win=open(Url,'Doc',Features);
	}
	function showCurrencies(currencyId,index)
	{
		var operation	 = document.forms[0].operation.value;
		var termid		 = '<%=loginbean.getTerminalId()%>';
    var searchStr	 =	document.getElementById(currencyId).value;
		Url='etrans/ETCCurrencyConversionAddLOV.jsp?searchString='+searchStr+'&index='+index+'&Operation='+operation+'&teminalId='+termid+'&name='+currencyId+'&fromWhere=buycharges&selection=single';
		var Bars = 'directories=no, location=no,menubar=no,status=no,titlebar=no';
		var Options='scrollbar=yes,width=400,height=360,resizable=no';
		var Features=Bars+' '+Options;
		var Win=open(Url,'Doc',Features);
	}
	function openChargeDescIdsLOV(input)
	{
		var id			=	input.id;
		var Bname		=	input.name;
		var index		=	id.substring(Bname.length);
		var name		=	"chargeDescriptionId";
		var searchStr	=	document.getElementById(name).value;
		var chargeId	=	document.getElementById("chargeId"+index).value;
		var chargeGroupId	=	document.getElementById("chargeGroupId"+index).value;//@@Added by subrahmanyam for pbn id: 195270 on 20-Jan-10
		var fromWhere	=	"sellcharge";

			if(chargeId=='')
			{ alert("Pls,Select chargeId");return false;}
//@@Modified by subrahmanyam for pbn id: 195270 on 20-Jan-10
			var Url			= "etrans/QMSLOVDescriptionIds.jsp?Operation=<%=operation%>&searchString="+searchStr+"&name="+name+"&selection=sigle&fromWhere="+fromWhere+"&chargeId="+chargeId+"&index="+index+"&chargeGroupId="+chargeGroupId;
			var Bars = 'directories=no, location=no,menubar=no,status=no,titlebar=no';
			var Options='scrollbar=yes,width=400,height=360,resizable=no';
			var Features=Bars+' '+Options;
			var Win=open(Url,'Doc',Features);
	}
	function getBuyChargesDetails()
	{
		if(document.sellChargeform.chargeId.value=='')
		{	
			if(document.sellChargeform.chargeDescriptionId.value=='')
			{	
				alert("Please,Select ChargeId and chargeDescriptionId");
			}
			else
			{	
				alert("Please,Select ChargeId");
			}
		}
		else if(document.sellChargeform.chargeDescriptionId.value=='')
		{	
			alert("Please,Select chargeDescriptionId");
		}
		else if(checkRateBreakForPS())
		{
			<%--alert(checkRateBreakForPS());--%>
			document.sellChargeform.subOperation.value	=	"search";
			document.sellChargeform.submit();
		}
	}
	function assaignRateType()
	{
		var rateBreak	=	document.sellChargeform.rateBreak;
		var rateType	=	document.sellChargeform.rateType;
		rateType.options.length	=	0;
		if(rateBreak.value	==	'Flat' || rateBreak.value	==	'Flat%' || 
			rateBreak.value	==	'Absolute' || rateBreak.value	==	'Percent')
		{
				rateType.options.add(new Option('Flat','Flat'));
		}else if(rateBreak.value	==	'Slab')
		{       
			   
				rateType.options.add(new Option('Slab','Slab'));
				rateType.options.add(new Option('Flat','Flat'));
				rateType.options.add(new Option('Both','Both'));
		}else if(rateBreak.value	==	'Slab%')
		{
				rateType.options.add(new Option('Slab','Slab'));
		}
	}
	function getSpanContent()
	{
		var chargeObj		 = document.getElementById("chargeId1");
		var rateBreak		 = document.sellChargeform.rateBreak;
		var rateType		 = document.sellChargeform.rateType;
		var spanContent		 = "";
		if(chargeObj!=null && chargeObj.value!=0 && chargeObj.value!="")
		{
			if(rateBreak.value=='Flat' || rateBreak.value=='Flat%')
			{
				spanContent	=	showFlat();
			}else if(rateBreak.value=='Slab' || rateBreak.value=='Slab%')
			{
				if(rateType.value=='Both')
				{
					spanContent	=	showBoth();
				}else
				{
					spanContent	=	showSlab();
				}
			}else if(rateBreak.value=='Absolute' || rateBreak.value=='Percent')
			{
				spanContent	=	showAbsValue();
			}

		}
		return spanContent;
	}
	function setSpanContent(spanContent,spanLocation)
	{
		if(document.getElementById(spanLocation)!=null)
		{
			document.getElementById(spanLocation).innerHTML=spanContent;	
			document.getElementById(spanLocation).style.display='block';
		}
	}
	function clearDataOnFocus(input)
	{
		var index	=	input.id.substring(input.name.length);
		if(input.name=='chargeFlatRate')
		{	document.getElementById("chargeRate"+index).value='';}
		else if(input.name=='chargeRate')
		{	document.getElementById("chargeFlatRate"+index).value='';}
	}

	function validataSellChargeHdr()
	{
			var index = 1;
			
				if(document.getElementById("chargeId"+(index)).value.length ==0)
				{
					alert("Enter ChargeId");
					document.getElementById("chargeId"+(index)).focus();
					return false;
				}
				else if(document.getElementById("chargeDescriptionId").value.length ==0)
				{
					alert("Enter chargeDescriptionId");
					document.getElementById("chargeDescriptionId").focus();
					return false;
				 }
				else if(document.getElementById("chargeBasisId"+(index)).value.length ==0)
				{
					alert("Enter chargeBasisId");
					document.getElementById("chargeBasisId"+(index)).focus();
					return false;
				 }
				else if(document.getElementById("currencyId"+(index)).value.length ==0)
				{
					alert("Enter Charge Currency");
					document.getElementById("currencyId"+(index)).focus();
					return false;
				}
				else if(document.getElementById("rateBreak"+(index)).value.length ==0)
				{
					alert("Select rateBreak");
					document.getElementById("rateBreak"+(index)).focus();
					return false;
				}
				else if(document.getElementById("rateType"+(index)).value.length ==0)
				{
					alert("Select chargeRateType");
					document.getElementById("rateType"+(index)).focus();
					return false;
				}else if(document.getElementById("chargeBasisId"+(index)).value.length >0)
				{
						if(document.getElementById("primaryUnit"+(index)).value=='KG' || document.getElementById("primaryUnit"+(index)).value=='CBM' || document.getElementById("primaryUnit"+(index)).value=='LB' ||
						document.getElementById("primaryUnit"+(index)).value=='CFT')
						{
							 if(document.getElementById("densityGrpCode"+(index)).value.length ==0)
								{
									alert("Select DensityGroupCode As primaryUnit: "+document.getElementById("primaryUnit"+(index)).value);
									document.getElementById("densityGrpCode"+(index)).focus();
									return false;
								}
						}else
						{
							 if(document.getElementById("densityGrpCode"+(index)).value.length >0)
								{
									alert("deselect DensityGroupCode As primaryUnit: "+document.getElementById("primaryUnit"+(index)).value);
									document.getElementById("densityGrpCode"+(index)).focus();
									return false;
								}
						}

				}
				if(!checkRateBreakForPS(index))
					return false;
		return true;
	}
	function validateMarginType()
	{
		var overAllMargin	= document.getElementById("overallmargin");
		var marginType		= document.getElementById("margintype");
		var marginBasis		= document.getElementById("marginbasis");
		var rateBreak		= document.getElementById("rateBreak1").value;
		var rateType		= document.getElementById("rateType1").value;
		var message			= '';
		if(overAllMargin==null || marginType==null)
		{
				alert("You cannot proceed without entering margin values\nPlease,Click on search to get BuyCharges");
				document.sellChargeform.buychargesPOP.focus();
				return false;
		}else if((rateBreak=='Flat' || rateBreak=='Flat%') && marginBasis==null)
		{
				alert("You cannot proceed without entering margin values\nPlease,Click on search to get BuyCharges");
				document.sellChargeform.buychargesPOP.focus();
				return false;
		}
		if(overAllMargin.value.length==0)
		{
			alert("Please,select overAllMargin");
			overAllMargin.focus();
			return false;
		}else if(marginType.value.length==0)
		{
			alert("Please,select marginType");
			marginType.focus();
			return false;
		}else if((rateBreak=='Flat' || rateBreak=='Flat%') && marginBasis.value.length==0)
		{
			alert("Please,select marginBasis");
			marginBasis.focus();
			return false;
		}
		return true;
	}

	function validateSlabs()
	{
	  	chargeIdVal = document.getElementById("chargeId"+1);
        rateBreak = document.getElementById("rateBreak"+1);
	    rateType = document.getElementById("rateType"+1);
		chargeIdValue=chargeIdVal.value;	
			 if(rateBreak.value == 'Percent' || rateBreak.value == 'Absolute')
			 {	
				if(document.getElementById("chargeRate"+1).value=='' || parseFloat(document.getElementById("chargeRate"+1))==0.0)
				{	
						alert('Please Enter '+rateBreak.value+' value for ChargeId '+chargeIdValue);
						document.getElementById("chargeRate"+1).focus();
						return false;
				}
			}
			else if(rateBreak.value == 'Flat' || rateBreak.value== 'Flat%')
			{
					<%--if(document.getElementById("chargeRate1@1").value=='' || parseFloat(document.getElementById("chargeRate1@1").value)==0.0)
					{	
						    alert('Please Enter BASE Rate value for ChargeId '+chargeIdValue);
							document.getElementById("chargeRate1@1").focus();
							return false;
					}--%>
					if(document.getElementById("chargeRate2").value=='' || parseFloat(document.getElementById("chargeRate2").value)==0.0)
					{	
						    alert('Please Enter MIN Rate value for ChargeId '+chargeIdValue);
							document.getElementById("chargeRate2").focus();
							return false;
					}
					<%--if(document.getElementById("chargeRate1@3").value=='' || parseFloat(document.getElementById("chargeRate1@3").value)==0.0)
					{	
						    alert('Please Enter MAX Rate value for ChargeId '+chargeIdValue);
							document.getElementById("chargeRate1@3").focus();
							return false;
					}--%>
					if(document.getElementById("chargeRate4").value=='' || parseFloat(document.getElementById("chargeRate4").value)==0.0)
					{	
						    alert('Please Enter FLAT Rate value for ChargeId '+chargeIdValue);
							document.getElementById("chargeRate4").focus();
							return false;
					}
			}
			else if(rateBreak.value == 'Slab' || rateBreak.value == 'Slab%')
			{
				for(var i=0; i<document.forms[0].chargeSlab.length; i++)
				{
					if(i == 0)
					{
						<%--if(document.forms[0].chargeRate[i].value == 0)
						{
							alert('Please Enter BASE Rate Value ChargeId '+chargeIdValue);
							document.forms[0].chargeRate[i].focus();
							return false;
						}--%>
					}
					else if(i == 1)
					{
						if(document.forms[0].chargeRate[i].value == 0)
						{
							alert('Please Enter MIN Rate Value ChargeId '+chargeIdValue);
							document.forms[0].chargeRate[i].focus();
							return false;
						}
					}
					else if(i == 2)
					{
						<%--if(document.forms[0].chargeRate[i].value == 0)
						{
							alert('Please Enter MAX Rate Value ChargeId '+chargeIdValue);
							document.forms[0].chargeRate[i].focus();
							return false;
						}--%>
					}
					else if(i == 3)
					{
						if(document.forms[0].chargeSlab[i].value == 0)
						{
							alert('Please Enter Negative Value For Slab at Column 4 ChargeId '+chargeIdValue);
							document.forms[0].chargeSlab[i].focus();
							return false;
						}
						else
						{
							if(isNaN(document.forms[0].chargeSlab[i].value) || parseInt(document.forms[0].chargeSlab[i].value) >= 0)
							{
								alert('Please Enter Negative Value For Slab at Column 4 ChargeId '+chargeIdValue);
								document.forms[0].chargeSlab[i].focus();
								return false;
							}
							if(document.forms[0].chargeRate[i].value == 0 )
							{
								if(rateType.value=='Flat' || rateType.value=='Slab')
								{
									alert('Please Enter Rate Value at column 4 ChargeId '+chargeIdValue);
									document.forms[0].chargeRate[i].focus();
									return false;
								}else
								{
									if(document.forms[0].chargeFlatRate[i-3].value==0)
									{
									alert('Please Enter FlatRate/SlabRate Value at column 4 ChargeId '+chargeIdValue);
									document.forms[0].chargeFlatRate[i-3].focus();
									return false;
									}
								}
							}
						}
					}
					else if(i == 4)
					{
						if(document.forms[0].chargeSlab[i].value == 0)
						{
							alert('Please Enter Positive Value For Slab at Column 5 ChargeId '+chargeIdValue);
							document.forms[0].chargeSlab[i].focus();
							return false;
						}
						else
						{
							if(isNaN(document.forms[0].chargeSlab[i].value) || parseInt(document.forms[0].chargeSlab[i].value) <= 0)
							{
								alert('Please Enter Positive Value For Slab at Column 5 ChargeId '+chargeIdValue);
								document.forms[0].chargeSlab[i].focus();
								return false;
							}
							if(Math.abs(document.forms[0].chargeSlab[3].value)  != Math.abs(document.forms[0].chargeSlab[i].value))
							{
								 alert("Please Enter Equal Possitive Amount for Slab at Column No. "+(i+1));
								 document.forms[0].chargeSlab[i].focus();
								 return false;				 
							}
							if(document.forms[0].chargeRate[i].value == 0)
							{
								if(rateType.value=='Flat' || rateType.value=='Slab')
								{
									alert('Please Enter Rate Value at column 5 ChargeId '+chargeIdValue);
									document.forms[0].chargeRate[i].focus();
									return false;
								}else
								{
									if(document.forms[0].chargeFlatRate[i-3].value==0)
									{
									alert('Please Enter FlatRate/SlabRate Value at column 5 ChargeId '+chargeIdValue);
									document.forms[0].chargeFlatRate[i-3].focus();
									return false;
									}
								}
							}
						}
					}
					else
					{
						if(document.forms[0].chargeSlab[i].value != 0 || document.forms[0].chargeSlab[i].value!='')
						{
							if(isNaN(document.forms[0].chargeSlab[i].value) || parseInt(document.forms[0].chargeSlab[i].value) <= 0)
							{
								alert('Please Enter Positive Value For Slab at Column '+(i+1));
								document.forms[0].chargeSlab[i].focus();
								return false;
							}
							if(Math.abs(document.forms[0].chargeSlab[i].value)  <= Math.abs(document.forms[0].chargeSlab[i-1].value))
							{
								 alert("Please Enter Slab at Column No. "+(i+1)+" is should be greate than Slab at Column No. "+i);
								 document.forms[0].chargeSlab[i].focus();
								 return false;				 
							}
							if(document.forms[0].chargeRate[i].value == 0)
							{
								if(rateType.value=='Flat' || rateType.value=='Slab')
								{
									alert('Please Enter Rate Value at column '+(i+1)+' ChargeId '+chargeIdValue);
									document.forms[0].chargeRate[i].focus();
									return false;
								}else
								{
									if(document.forms[0].chargeFlatRate[i-3].value==0)
									{
									alert('Please Enter FlatRate/SlabRate Value at column '+(i+1)+' ChargeId '+chargeIdValue);
									document.forms[0].chargeFlatRate[i-3].focus();
									return false;
									}
								}
							}
						}else
						{
									if(rateType.value=='Both')
									{
										if(document.forms[0].chargeRate[i].value!=0 || document.forms[0].chargeRate[i].value!='' || (document.forms[0].chargeFlatRate[i-3]!=null && document.forms[0].chargeFlatRate[i-3].value!=0))
										{
											alert('Please Enter Positive Value For Slab at Column '+(i+1));
											document.forms[0].chargeSlab[i].focus();
											return false;
										}
									}else if(document.forms[0].chargeRate[i].value!=0 || document.forms[0].chargeRate[i].value!='')
									{
										alert('Please Enter Positive Value For Slab at Column '+(i+1));
										document.forms[0].chargeSlab[i].focus();
										return false;
									}

						}
					}
				}
			}
		return true;
	}

	function validateMargins()
	{
		var overAllMargin	= document.getElementById("overallmargin").value;
		var marginType		= document.getElementById("margintype").value;
		var marginBasis		= '';
		var rateBreak		= document.getElementById("rateBreak1").value;
		var rateType		= document.getElementById("rateType1").value;
		
	

	<%--ENDED BY SUBRAHMANYAM FOR THE ISSUE 143489 --%>
		if(rateBreak=='Absolute' || rateBreak=='Percent')
		{
			if(document.sellChargeform.marginRate.value.length==0)
			{
					alert("Please Enter the Base Margin");
					document.sellChargeform.marginRate.focus();
					return false;			
			}
		}else if(rateBreak == 'Flat' || rateBreak=='Flat%')
		{
			marginBasis		= document.getElementById("marginbasis").value;
<%--ADDED BY SUBRAHMANYAM FOR THE ISSUE 143489--%>
			if(marginType == 'P' && overAllMargin == 'Y')
			{
					document.sellChargeform.marginRate[1].value=document.sellChargeform.marginRate[0].value;
					document.sellChargeform.marginRate[2].value=document.sellChargeform.marginRate[0].value;
					document.sellChargeform.marginRate[3].value=document.sellChargeform.marginRate[0].value;
					if(document.getElementById("chargeRate3").value==0)
							document.sellChargeform.marginRate[2].value=0.0
			}
<%--ENDED BY SUBRAHMANYAM FOR THE ISSUE 143489 --%>
			if((marginBasis=='N'))
			{
				if((document.sellChargeform.marginRate[0].value.length==0) && document.getElementById("chargeRate1").value.length>0)
				{
					alert("Please Enter the Base Margin");
					document.sellChargeform.marginRate[0].focus();
					return false;
				}
				if(document.sellChargeform.marginRate[1].value.length==0 )
				{
					alert("Please Enter the Min Margin.");
					document.sellChargeform.marginRate[1].focus();
					return false;
				}
				if(document.sellChargeform.marginRate[2].value.length==0 && document.getElementById("chargeRate3").value.length>0)
				{
					alert("Please Enter the Max Margin.");
					document.sellChargeform.marginRate[2].focus();
					return false;
				}
				if(document.sellChargeform.marginRate[3].value.length==0)
				{
					alert("Please Enter the Flat Margin.");
					document.sellChargeform.marginRate[3].focus();
					return false;
				}

			}else if((marginBasis=='V'))
			{
				for(i=0;i<document.sellChargeform.marginSlab.length;i++)
				{
					if(i==0)
					{
						if(document.forms[0].marginSlab[i].value == 0 || document.forms[0].marginSlab[i].value=='')
						{
							alert('Please Enter Negative Value For marginSlab at Column 1');
							document.forms[0].marginSlab[i].focus();
							return false;
						}else
						{
							if(isNaN(document.forms[0].marginSlab[i].value) || parseInt(document.forms[0].marginSlab[i].value) >= 0)
							{
								alert('Please Enter Negative Value For marginSlab at Column 1');
								document.forms[0].marginSlab[i].focus();
								return false;
							}
							if(document.forms[0].marginRate[i].value.length == 0)
							{
									alert('Please Enter the Margin at column 1');
									document.forms[0].marginRate[i].focus();
									return false;
							}
						}
					}else if(i==1)
					{
						if(document.forms[0].marginSlab[i].value == 0 || document.forms[0].marginSlab[i].value=='')
						{
							alert('Please Enter Positive Value For marginSlab at Column 2');
							document.forms[0].marginSlab[i].focus();
							return false;
						}else
						{
							if(isNaN(document.forms[0].marginSlab[i].value) || parseInt(document.forms[0].marginSlab[i].value) <= 0)
							{
								alert('Please Enter Positive Value For Slab at Column 2');
								document.forms[0].marginSlab[i].focus();
								return false;
							}
							if(Math.abs(document.forms[0].marginSlab[0].value)  != Math.abs(document.forms[0].marginSlab[i].value))
							{
								 alert("Please Enter Equal Possitive Amount for Slab at Column No. "+(i+1));
								 document.forms[0].marginSlab[i].focus();
								 return false;				 
							}
							if(document.forms[0].marginRate[i].value.length == 0)
							{
									alert('Please Enter the Margin at column 2');
									document.forms[0].marginRate[i].focus();
									return false;
							}
						}
					}else
					{
						if(document.forms[0].marginSlab[i].value != 0)
						{
							if(isNaN(document.forms[0].marginSlab[i].value) || parseInt(document.forms[0].marginSlab[i].value) <= 0)
							{
								alert('Please Enter Positive Value For marginSlab at Column '+(i+1));
								document.forms[0].marginSlab[i].focus();
								return false;
							}
							if(Math.abs(document.forms[0].marginSlab[i].value)  <= Math.abs(document.forms[0].marginSlab[i-1].value))
							{
								 alert("Please Enter marginSlab at Column No. "+(i+1)+" is should be greate than marginSlab at Column No. "+i);
								 document.forms[0].marginSlab[i].focus();
								 return false;				 
							}
							if(document.forms[0].marginRate[i].value.length == 0)
							{
									alert('Please Enter the Margin at column '+(i+1));
									document.forms[0].marginRate[i].focus();
									return false;
							}
						}else
						{
								if(document.forms[0].marginRate[i].value.length!=0)
								{
										alert('Please Enter Positive Value For marginSlab at Column '+(i+1));
										document.forms[0].marginSlab[i].focus();
										return false;									
								}
						}
					}
				}
			}
		}else if(rateBreak=='Slab' || rateBreak=='Slab%')
		{
			if((overAllMargin=='Y'))
			{
				if((marginType=='P'))
				{
					if(document.forms[0].marginRate.value.length==0)
					{
						alert("Please Enter the Margin.");
						document.forms[0].marginRate.focus();
						return false;
					}
				}else
				{
					if(document.sellChargeform.marginRate[0].value.length==0 && document.sellChargeform.chargeRate[0].value.length>0)
					{
						alert("Please Enter the Base Margin.");
						document.sellChargeform.marginRate[0].focus();
						return false;
					}
					if(document.sellChargeform.marginRate[1].value.length==0)
					{
						alert("Please Enter the Min Margin.");
						document.sellChargeform.marginRate[1].focus();
						return false;
					}
					if(document.sellChargeform.marginRate[2].value.length==0 && document.sellChargeform.chargeRate[2].value.length>0)
					{
						alert("Please Enter the Max Margin.");
						document.sellChargeform.marginRate[2].focus();
						return false;
					}
					if(document.sellChargeform.marginRate[3].value.length==0)
					{
						alert("Please Enter the Slab Margin.");
						document.sellChargeform.marginRate[3].focus();
						return false;
					}					
				}
			}else if((overAllMargin=='N'))
			{
				for(i=0;i<14;i++)
				{
					if(i<=2)
					{
						if(document.sellChargeform.chargeRate[i].value.length>0)
						{
							if(document.sellChargeform.marginRate[i].value.length==0)
							{
								alert("Please Enter the Margin Rate.");
								document.sellChargeform.marginRate[i].focus();
								return false;
							}
						}
					}else
					{
						if(document.sellChargeform.marginRate[i].value.length==0)
						{
							if(rateType!='Both')
							{
								if(document.sellChargeform.chargeRate[i].value.length>0)
								{
									alert("Please Enter the Margin Rate.");
									document.sellChargeform.marginRate[i].focus();
									return false;
								}
							}else
							{
								if(document.sellChargeform.chargeRate[i].value.length>0 ||   document.sellChargeform.chargeFlatRate[i-3].value.length>0)
								{
									alert("Please Enter the Margin Rate.");
									document.sellChargeform.marginRate[i].focus();
									return false;
								}
							}
						}
						else if(document.sellChargeform.marginRate[i].value.length>0)
						{
							if(rateType!='Both')
							{
								if(document.sellChargeform.chargeRate[i].value.length==0)
								{
									alert("Entered marginRate is Invalid,As no chargeRate\nPlese remove..");
									document.sellChargeform.marginRate[i].focus();
									return false;
								}
							}else
							{
								if(document.sellChargeform.chargeRate[i].value.length==0 && document.sellChargeform.chargeFlatRate[i-3].value.length==0)
								{
									alert("Entered marginRate is Invalid,As no chargeRate\nPlese remove..");
									document.sellChargeform.marginRate[i].focus();
									return false;
								}
							}
						}
					}
				}
			}
		}
		return true;
	}
	function submitToServer()
	{
		var flag = true;
		if(!validataSellChargeHdr())
			flag = false;
		if(flag && !validateMarginType())
			flag = false;
		if(flag && !validateSlabs())
			flag = false;
		if(flag && !validateMargins())
			flag = false;
		if(flag)
		{
			if(document.sellChargeform.submitB!=null)
			{	document.sellChargeform.submitB.disabled = true;}
			if(document.sellChargeform.resetB!=null)
			{	document.sellChargeform.resetB.disabled = true;}

			document.sellChargeform.subOperation.value	=	"<%=operation%>";
			document.sellChargeform.submit();
		}
	}

	function checkRateBreakForPS(tmpCurrentId)
	{
		//alert(tmpCurrentId);
		tmpCurrentId	=	1;
		var chargeidvalue	=	document.getElementById("chargeId"+tmpCurrentId);
		var rateBreak		=	document.getElementById("rateBreak"+tmpCurrentId);
		var rateType		=	document.getElementById("rateType"+tmpCurrentId);
		var primaryUnit		=	document.getElementById("primaryUnit"+tmpCurrentId);
		var secondaryUnit		=	document.getElementById("secondaryUnit"+tmpCurrentId);
		
		<%--alert(primaryUnit.value);
		alert(secondaryUnit.value);
		alert(rateBreak.value); --%>
		if(primaryUnit.value.toUpperCase()=='SHIPMENT' && secondaryUnit.value.length==0)
		{
			if(rateBreak.value!='Absolute')
			{
				alert("For the selected Charge Basis, only Absolute Rate Break can be selected.\n Please change the Rate Break in lane :"+tmpCurrentId);
				rateBreak.value='';
				return false;
			}
		}
		else if(primaryUnit.value.toUpperCase()=='SHIPMENT' && secondaryUnit.value.length>0)
		{
			if(rateBreak.value!='Absolute' && rateBreak.value!='Percent')
			{
				alert("For the selected Charge Basis, either Absolute or Percent Rate Break can be selected.\n Please change the Rate Break in lane :"+tmpCurrentId);
				rateBreak.value='';
				return false;
			}
		}

		return true;
	}

	function openBuycharges()
	{
		
		var chargeId	=	document.sellChargeform.chargeId;
		var chargeDescId=	document.sellChargeform.chargeDescriptionId;
		if(chargeId=='')
		{	
			if(chargeDescId.value=='')
			{	alert("Please,Select ChargeId and chargeDescriptionId");chargeId.focus();return false;}
			else
			{	alert("Please,Select ChargeId");return false;}
		}else if(chargeDescId.value=='')
		{	alert("Please,Select chargeDescriptionId");chargeDescId.focus();return false;}
		else
		{	
			Url="QMSSellChargesController?Operation=Add&subOperation=BuyView&chargeId="+chargeId.value+"&chargeDescriptionId="+chargeDescId.value+"&terminalId=<%=loginbean.getTerminalId()%>";
			var Bars = 'directories=no, location=no,menubar=no,status=no,titlebar=no,scrollbars=yes';
			var Options='width=900,height=360,resizable=no';
			var Features=Bars+' '+Options;
			var Win=open(Url,'Doc',Features);
		}
		return false;
	}

	function enableBotton()
	{

		document.forms[0].chargeBasisIdPoPLov.disabled = false;
		document.forms[0].densityGrpCode.readOnly = false;
		document.forms[0].densityPop.disabled = false;
	}

function opendensityLOV(input)
{
	var id			=	input.id;
	var Bname		=	input.name;
	var index		=	id.substring(Bname.length);
	var name		=	"densityGrpCode"+index;
	var searchStr	=	document.getElementById(name).value;
	
	<%--var chargeId	=	document.getElementById("densityGrpCode"+index).value; --%>
	var fromWhere	=	"buycharge";
  <%--  alert(document.getElementById("primaryUnit"+index).value) --%>
	var uom = document.getElementById("chargeBasisId"+index).value;
		
	if(uom.length>0)
	{
		var Url			= "etrans/QMSDensityRatioLOV.jsp?Operation=<%=operation%>&searchString="+searchStr+"&shipmentMode=null&name="+name+"&fromWhere=BC&uom="+uom;
		var Bars = 'directories=no, location=no,menubar=no,status=no,titlebar=no';
		var Options='scrollbar=yes,width=400,height=360,resizable=no';
		var Features=Bars+' '+Options;
		var Win=open(Url,'Doc',Features);
	}else
	{
		alert("Select ChargeBasisId before Selecting densityRation At laneNo :"+index);
		document.getElementById("chargeBasisId"+index).focus();
		return false;
	}
}

function checkRateBreak()
	{
		if(!(document.getElementById("rateBreak1").value=='Slab' || document.getElementById("rateBreak1").value=='Slab%'))
		{
			document.forms[0].weightClass.value='G';
		}
	}
   //Added by VLAKSHMI for CR #170761 on 20090626
function openChargeGroupIdLOV()
	{
		var searchStr	=	 document.forms[0].chargeGroupId.value;
		var chargeId	=	document.getElementById("chargeId1").value;
		var chargeIdDesc = document.getElementById("chargeDescriptionId").value;
		
      // alert("iiiiiii"+searchStr)  
		var Url			= "etrans/QMSLOVChargeGroupIds.jsp?Operation=<%=operation%>&searchString="+searchStr+"&name=chargeGroupId&fromWhere=chargeGroupEnterId&terminalId="+document.forms[0].terminalId.value+"&chargeId="+chargeId+"&chargeIdDesc="+chargeIdDesc;
		showLOV(Url);
	}
</script>
</head>
<body onLoad = 'onLoadCheckDetails()'>
<form method="post" name='sellChargeform' action='QMSSellChargesController'>
<table width="100%" border="0" cellspacing="1" cellpadding="4">
	<tr bgcolor="#FFFFFF">
		<td >
			<table width="100%" border="0" cellspacing="0" cellpadding="4">
				<tr class='formlabel' > 
				<td><b>Sell Charges - <%=operation%></b></td><td align="right">QS1050611</td></tr>
			</table>
			<table width="100%" border="0" cellspacing="1" cellpadding="4">
			<%
				if(headerMsg!=null && headerMsg.length()>0)
				{
				%>
					<tr class="formdata"><td colspan="13" ><font color='#ff0000'>The Follwoing data is Invalid:<br><%=(headerMsg!=null)?headerMsg:""%></font></td></tr>
				<%}else{%>
					<tr class="formdata"><td colspan="13" >&nbsp;</td></tr>
				<%}%>

			 </table>
			<table id="chargesTable" width="100%" border="0" cellspacing="1" cellpadding="4" bgcolor="#FFFFFF">
			  <tr class='formheader'>
			  <td></td>
        <!--Added by VLAKSHMI for CR #170761 on 20090626 -->
			  <td><b>Charge Group Id:</b></td>
				<td><b>Charge:<font color="#FF0000">*</font></b></td>
				<td><b>Description Id:<font color="#FF0000">*</font></b></td>
				<!-- @@Commented by subrahmanyam for 146440 -->
				<!-- <td><b>Charge Basis:<font color="#FF0000">*</font></b></td>
				<td><b>Description:</b></td>
				<td><b>Currency:<font color="#FF0000">*</font></b></td>
				<td><b>Rate Break:<font color="#FF0000">*</font></b></td>
				<td><b>Rate Type:<font color="#FF0000">*</font></b></td>
				<td><b>Weight Class:<font color="#FF0000">*</font></b></td>
				<td><b>Density Ratio:<font color="#FF0000">*</font></b></td> -->
				<!-- @@Added by subrahmanyam for 146440 -->
				<td><b>Charge Basis:</b></td>
				<td><b>Description:</b></td>
				<td><b>Currency:</b></td>
				<td><b>Rate Break:</b></td>
				<td><b>Rate Type:</b></td>
				<td><b>Weight Class:</b></td>
				<td><b>Density Ratio:</b></td>
				<!-- @@ Ended by subrahmanyam for 146440 -->
			  </tr>
			  <tr class='formdata'>
				<td><input type='hidden' name='buychargeid' value='<%=(buychargeID!=null)?buychargeID:""%>'></td>
				<td>
				<input type	='text' name ="chargeGroupId" size='15' maxLength='20' class ='text' value ='' onkeyPress='specialCharFilter1(this.value,"chargeGroupId")'  onBlur="toUpperCase()">&nbsp;
				<input type	='button' name='b1' class ='input' value ="..." Onclick ="openChargeGroupIdLOV()">
				</td>
				<td><input type='text' class='text' size='7' maxLength='10' name='chargeId' id="chargeId1" value='' onChange='setSpanContent("","spanSlabs");setSpanContent("","spanValueBreaks");setSpanContent("","spanMargin");enableBotton()' onBlur='toUpper(this)'>&nbsp;<input type='button' class ='input' name='chargeIdPoPLov' value='...' onClick='showCharges("chargeId","1")'>					
				</td>
				<td><input type='text' class='text' size='20' maxLength='50' name='chargeDescriptionId' id="chargeDescriptionId" value='' onChange='setSpanContent("","spanSlabs");setSpanContent("","spanValueBreaks");setSpanContent("","spanMargin");enableBotton()'  onBlur='toUpper(this)'>&nbsp;<input type='button' class ='input' name='chargeDescIdPoPLov' value='...' onClick='openChargeDescIdsLOV(this)'></td>
				<td><input type='text' class='text' size='7' maxLength='10' name='chargeBasisId' id="chargeBasisId1" value=''  onChange='setSpanContent("","spanSlabs");setSpanContent("","spanValueBreaks");setSpanContent("","spanMargin");enableBotton()' onBlur='toUpper(this)' >&nbsp;<input type='button' class ='input' name='chargeBasisIdPoPLov' value='...' onClick='showChargeBasis("chargeBasisId","1")' <%=disabled%>>					
				</td>
				<td><input type='text' class='text' size='15' maxLength='' name='chargeBasisDescription' id="chargeBasisDescription1" value='' readOnly></td>
				<td><input type='text' class='text' size='7' maxLength='5' name='currencyId' id="currencyId1" value='' onBlur='this.value=this.value.toUpperCase()'  >&nbsp;<input type='button' class ='input' name='currencyIdPoPLov' value='...' onClick='showCurrencies("currencyId","1")'>					
				</td>
				<td><select name ='rateBreak' id='rateBreak1' class='select' onChange='assaignRateType();setSpanContent("","spanSlabs");setSpanContent("","spanValueBreaks");setSpanContent("","spanMargin");enableBotton();'>
					<option value=''></option>
					<option value='Flat'>Flat</option>
					<option value='Flat%'>Flat%</option>
					<option value='Slab'>Slab</option>
					<option value='Slab%'>Slab%</option>
					<option value='Percent'>Percent</option>
					<option value='Absolute'>Absolute</option></select>
				</td>
				<td><select name ='rateType' id='rateType1' class='select' onChange='setSpanContent("","spanSlabs");setSpanContent("","spanValueBreaks");setSpanContent("","spanMargin");enableBotton()'>
					<option value=''></option></select>
				</td>
				<td><select name ='weightClass' class='select' onChange='return checkRateBreak()'>
					<option value='G'>General</option>
					<option value='W'>Weightscale</option></select>
				</td>
				 <td align="center"> 
							  <input type='text' class='text'name="densityGrpCode" id='densityGrpCode1' onblur='checkNumbers1(this)' value='<%=(densityGrpCode!=null)? densityGrpCode :"" %>' size="5"  maxlength=25 <%=readOnly%>>&nbsp;<input type='button' class='input' value="..." name='densityPop' onClick='opendensityLOV(this)' <%=disabled%>>
							  <input type='hidden' name='primaryUnit' id='primaryUnit1' value='<%=(primaryUnit!=null)? primaryUnit :"" %>' >
							  <input type='hidden' name='secondaryUnit' id='secondaryUnit1' value='<%=(secondaryUnit!=null)? secondaryUnit :"" %>' >
				 </td>
			  </tr>
			  <tr class='formdata'>
		<%-- <td colspan='8' align='left'>Click the BuyCharges button to get the BuyCharges defined<br>Select ChargeId,ChargeBasisId to View the BuyCharges defined for those combination</td> --%>

				<td colspan='9' align='right' ><a href='' id='buychargeslink' onclick='return openBuycharges()'  target="_blank"><b>View Buy Charge Details</b></a></td>
				<td><input type='button' class='input' name='buychargesPOP' value='Search' onClick='getBuyChargesDetails()'></td>
			  </tr>
            </table>
			<table width="100%" border="0" cellspacing="0" cellpadding="" >
			  <tr class='formlabel'>
				<td colspan='10'>&nbsp Margin Calculations</td>
			  </tr>
			  <tr class='formdata'>
				<td colspan='9' ><span id='spanSlabs' ></span><td>
			  </tr>
			</table>
			
			<table width="100%" border="0" cellspacing="0" cellpadding="4">
				<tr  class='denotes' bgcolor="#FFFFFF"> 
				<td><font color="#FF0000">*</font>Denotes Mandatory</td>
				<td align="right"><input type="hidden" name="Operation" id="operation" value="<%=operation%>">
					<input type="hidden" name="dummyBuychargesflag"  value="">
					<input type="hidden" name="subOperation" id="subOperation" value="">
					<input type="hidden" name="terminalId" id="terminalId" value="<%=loginbean.getTerminalId()%>">
					<input id='submit1' type='button' name='submitB' value='Submit' class='input' onClick='submitToServer()'>
					<input type='reset' value='Reset' class='input' name ='resetB'>
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
	}catch(Exception e)
	{
		
    logger.error(FILE_NAME+"Exception while displying the data"+e);
	}
%>
